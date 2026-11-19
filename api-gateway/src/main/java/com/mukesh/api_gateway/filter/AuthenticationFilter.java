package com.mukesh.api_gateway.filter;

import com.mukesh.api_gateway.dto.ValidateTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    private static final String VALIDATE_URL = "http://AUTH-SERVER/api/v1/auth/validate";
    private static final Duration VALIDATE_TIMEOUT = Duration.ofSeconds(3);

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_NAME = "X-User-Name";
    private static final String HEADER_USER_ROLE = "X-User-Role";

    // Public endpoints: exact matches only, so anything unexpected requires a token (fail closed).
    private static final Set<String> PUBLIC_POST_PATHS = Set.of("/api/v1/auth/login", "/api/v1/user");

    // Role rules by path prefix. Anything not listed is ADMIN only.
    private static final Map<String, Set<String>> ROLE_RULES = Map.of(
            "/api/v1/hotel", Set.of("USER", "ADMIN"),
            "/api/v1/rating", Set.of("USER", "ADMIN"),
            "/api/v1/user", Set.of("USER", "ADMIN"));
    private static final Set<String> DEFAULT_ROLES = Set.of("ADMIN");

    private final WebClient webClient;

    public AuthenticationFilter(WebClient.Builder loadBalancedBuilder) {
        this.webClient = loadBalancedBuilder.build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Public endpoint: no token needed, but still drop any spoofed identity headers.
        if (isPublic(request)) {
            return chain.filter(exchange.mutate().request(withoutIdentityHeaders(request).build()).build());
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return reject(exchange, HttpStatus.UNAUTHORIZED);
        }

        return validate(authHeader).flatMap(outcome -> {
            if (outcome.rejectedWith() != null) {
                return reject(exchange, outcome.rejectedWith());
            }
            ValidateTokenResponse identity = outcome.identity();
            if (!isAllowed(request.getPath().value(), identity.role())) {
                return reject(exchange, HttpStatus.FORBIDDEN);
            }
            ServerHttpRequest mutated = withoutIdentityHeaders(request)
                    .header(HEADER_USER_ID, String.valueOf(identity.userId()))
                    .header(HEADER_USER_NAME, identity.username())
                    .header(HEADER_USER_ROLE, identity.role())
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        });
    }

    // Asks auth-server to verify the token. Never errors: failures become a rejection status,
    // so errors from the routed service are not mixed up with validation errors.
    private Mono<Outcome> validate(String authHeader) {
        return webClient.get()
                .uri(VALIDATE_URL)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(ValidateTokenResponse.class)
                .timeout(VALIDATE_TIMEOUT)
                .map(identity -> new Outcome(identity, null))
                .defaultIfEmpty(new Outcome(null, HttpStatus.UNAUTHORIZED))
                .onErrorResume(WebClientResponseException.Unauthorized.class,
                        e -> Mono.just(new Outcome(null, HttpStatus.UNAUTHORIZED)))
                .onErrorResume(e -> {
                    log.error("auth-server validation failed: {}", e.getClass().getSimpleName());
                    return Mono.just(new Outcome(null, HttpStatus.SERVICE_UNAVAILABLE));  // fail closed
                });
    }

    private boolean isPublic(ServerHttpRequest request) {
        return HttpMethod.POST.equals(request.getMethod())
                && PUBLIC_POST_PATHS.contains(request.getPath().value());
    }

    private boolean isAllowed(String path, String role) {
        Set<String> allowed = ROLE_RULES.entrySet().stream()
                .filter(rule -> path.equals(rule.getKey()) || path.startsWith(rule.getKey() + "/"))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(DEFAULT_ROLES);
        return role != null && allowed.contains(role);
    }

    // Always strip these first, otherwise a client could send "X-User-Role: ADMIN" itself.
    private ServerHttpRequest.Builder withoutIdentityHeaders(ServerHttpRequest request) {
        return request.mutate().headers(h -> {
            h.remove(HEADER_USER_ID);
            h.remove(HEADER_USER_NAME);
            h.remove(HEADER_USER_ROLE);
        });
    }

    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return 100;   // after the rate limiter (route filters, order 1+), before routing
    }

    private record Outcome(ValidateTokenResponse identity, HttpStatus rejectedWith) {
    }
}
