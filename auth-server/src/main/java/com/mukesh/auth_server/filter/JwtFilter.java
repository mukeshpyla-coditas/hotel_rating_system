package com.mukesh.auth_server.filter;

import com.mukesh.auth_server.custom.CustomUserDetailsService;
import com.mukesh.auth_server.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7);
            try {
                // Parsing verifies the signature; it throws if the token is tampered, malformed or expired.
                String username = jwtUtil.extractUsername(token);
                if (jwtUtil.isTokenValid(token)) {
                    UserDetails user = customUserDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                // Exceptions thrown inside a filter never reach @ControllerAdvice (that would be a 500),
                // so leave the request unauthenticated and let Spring Security reject it downstream.
                log.warn("Rejected an invalid access token: {}", e.getClass().getSimpleName());
                SecurityContextHolder.clearContext();
            }
        }

        // Always pass the request on, otherwise it never reaches the controller.
        filterChain.doFilter(request, response);
    }
}
