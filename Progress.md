# Microservices Learning Progress

Project: Hotel Rating microservices (Spring Boot + Spring Cloud).
Last updated: 2026-11-19.

Legend: ✅ done, 🔜 in progress or next, 📋 remaining.

---

## ✅ Done

1. **Microservices basics**
   - Database per service
   - Layered service structure (controller, service, repository, entity, DTO, exception handling)
2. **Inter-service communication**
   - OpenFeign (declarative HTTP client)
   - Service composition (rating details API)
3. **Service discovery**
   - Eureka server and service registration
   - Client-side discovery
   - Calling services by name
4. **Centralized configuration**
   - Config Server and config-repo (git)
   - Profiles (dev/prod)
   - `{cipher}` encryption
5. **API Gateway**
   - Single entry point
   - Routes and predicates
   - `lb://` load-balanced routing
6. **Resilience (partial)**
   - Circuit breaker (CLOSED, OPEN, HALF_OPEN)
   - Timeouts
   - Fallbacks and `FallbackFactory` on rating-service Feign calls
7. **Security (partial)**
   - Custom auth-server: JWT, BCrypt, `AuthenticationManager`, stateless config, register/login/validate APIs
   - Gateway `AuthenticationFilter`: remote token validation, path-based role rules, `X-User-*` identity headers, fail-closed (401, 403, 503), header-spoofing protection
8. **Reactive vs servlet**
   - WebFlux/Netty vs MVC/Tomcat
   - `Mono` / `Flux`
   - `GlobalFilter` vs `GatewayFilter` vs `OncePerRequestFilter`

9. **Redis basics**
   - Key/value, `INCR`, `EXPIRE`, `TTL`, `KEYS`
   - Atomic counters, Lua scripts
   - Portable Windows Redis (Docker blocked by admin rights)
10. **Rate limiting (built, testing pending)**
    - Token bucket (`burstCapacity`, `replenishRate`)
    - `RequestRateLimiter` at the gateway
    - Per-IP, per-route key resolver
    - Filter ordering (limiter before auth-server call)

---

## 🔜 In progress / next

- Test the rate limiter: 429s, `X-RateLimit-*` headers, keys in `redis-cli`
- Commit `config-repo` and the gateway changes

---

## 📋 Remaining

### Resilience (finish)
- Retry with backoff
- Gateway circuit breaker with a clean 503 fallback
- Bulkhead
- Resilience4j RateLimiter for outgoing calls (incoming limiting is done)
- Load balancing across several instances

### Security (finish)
- Gateway secret header (`X-Gateway-Secret`), planned
- Per-service JWT validation (defence in depth)
- Feign identity or token propagation
- Ownership checks (data-level authorization)
- Secret management (JWT secret out of code)
- Refresh tokens
- RS256 with JWKS
- OAuth2/OIDC and Keycloak (awareness)

### Data and consistency
- Caching (Redis as a cache)
- Distributed transactions problem
- Saga pattern
- Outbox pattern
- Eventual consistency, idempotency

### Messaging
- Kafka or RabbitMQ
- Event-driven architecture (producers, consumers, topics, queues)
- Dead-letter queues, retries

### Configuration (finish)
- Runtime refresh: `/actuator/refresh`, Spring Cloud Bus

### Observability
- Health checks and Actuator
- Metrics (Micrometer, Prometheus, Grafana)
- Distributed tracing (Micrometer Tracing, Zipkin)
- Centralized logging (ELK or Loki), correlation IDs

### Packaging and deployment
- Docker, Docker Compose
- Network isolation (only the gateway exposed)
- Kubernetes (Deployments, Services, Ingress, ConfigMaps, Secrets)
- CI/CD

### Quality
- Testcontainers
- Contract testing (Spring Cloud Contract or Pact)
- API versioning
- OpenAPI/Swagger docs

### Housekeeping
- Push `config-repo` to its own private GitHub repo
- Stop tracking `.idea/`

---

## Related files
- `MICROSERVICES_CONCEPTS.md`: concept explanations
- `LEARNING_NOTES.md`: chronological per-commit log
