# Learning Notes: Hotel Rating Microservices

A practice project for learning microservices with Spring Boot and Spring Cloud.
Notes are in the order the work was committed.

---

## 2026-11-13: Foundations

### `23ee461`: First commit: three services + OpenFeign
- Built three Spring Boot services, each with its own Postgres database:

  | Service | Port | Role |
  |---|---|---|
  | user-service | 8081 | Manages users |
  | hotel-service | 8082 | Manages hotels |
  | rating-service | 8083 | Manages ratings |
- Each service follows the same layering: controller, service, repository, entity, DTO, exception handling. APIs live under `/api/v1/<name>`.
- Services talk to each other using **OpenFeign**, a declarative HTTP client.
- **Learned:** database per service, and calling another service through an interface instead of hand-written HTTP code.

### `e16304d`: Service Registry (Eureka)
- Added `service-registry` (Eureka server, port 8761).
- All three services register themselves under their application name.
- **Learned:** why hard-coded URLs don't scale, and how a registry keeps track of live instances.

### `d357dac`: Feign calls by service name
- Feign clients now use the **service name** (resolved through Eureka) instead of a fixed URL and port.
- **Learned:** client-side discovery. A service's port can change without breaking callers.

---

## 2026-11-16: Rating details, Config Server, API Gateway

### `6abca01`: Rating details API
- Added an API in rating-service that returns a rating together with its **user** and **hotel** details.
- It calls user-service and hotel-service through Feign and combines the results.
- **Learned:** the service-composition pattern, and the first real example of inter-service data retrieval.

### Config Server (`1838202`, `36dbcac`; config files in the separate `config-repo`)
**The problem:** every service had its own `application.properties`, with duplicated Eureka settings and DB credentials.

**What was built**
- `config-server` (Spring Cloud Config, port 8888) serving config from a git repo, `config-repo`.
- `config-repo` holds `application.properties` (shared by all services) plus one file per service.
- The three services keep only `spring.application.name` and `spring.config.import=configserver:http://localhost:8888` locally. They fetch the rest at startup through `spring-cloud-starter-config`.
- **Profiles:** `user-service-dev.properties` and `user-service-prod.properties` override the defaults, activated with `spring.profiles.active`.
- **Encryption:** the DB password is stored as `{cipher}...` and decrypted by the server. The key comes from the `ENCRYPT_KEY` environment variable.
- `config-repo` is a separate git repo, ignored in this one. Its history: `0315938` first commit, `ac101af` renamed files, `6738584` user-service fetching config, `d586946` profiles, `e6c1b9f` / `73e5f27` encrypted password, `9570499` gateway properties.

**Learned**
- Centralized config, profile overrides, and server-side encryption.
- The server only serves **committed** files, and more specific files override general ones.

**Problems hit and what each taught**
| Problem | Cause | Lesson |
|---|---|---|
| Server returned empty `propertySources` | Notepad saved files as `*.properties.txt` | Show file extensions in Windows |
| `NoSuchLabelException: master` | Repo branch was `main` | Set `default-label` to the real branch |
| `Failed to configure a DataSource` (all 3 services) | `spring-cloud-starter-config` missing from the pom, and `optional:` hid the failure | Dropping `optional:` makes this fail loudly |
| Password came back still encrypted | Missing `{cipher}` prefix | Only `{cipher}`-prefixed values are decrypted |
| `curl -X` failed in PowerShell | `curl` is an alias for `Invoke-WebRequest` | Use `curl.exe` |

**Skipped on purpose:** runtime refresh with `/actuator/refresh`. We will come back to it with Spring Cloud Bus.

### API Gateway (`b23ee77`)
- Added `api-gateway` (port 8080) as the single entry point.
- Routes `/api/v1/user|hotel|rating/**` to the right service using `lb://SERVICE-NAME`. The routes live in `config-repo/api-gateway.properties`.
- **Learned:** one entry point, routing by service name through Eureka, and a single place for future auth, rate limiting and logging.
- **Tested:** `GET localhost:8080/api/v1/user` returned user data through the gateway.
- **Observed gap:** with hotel-service stopped, the gateway returned a generic 500 error page. There was no useful message and no fallback. Eureka can also keep a dead instance in its cache for up to about 90 seconds.

### Resilience4j on rating-service (commit pending)
**The problem:** with hotel-service stopped, the gateway returned a generic 500 and callers would keep hitting a dead service. A slow dependency could also tie up threads and spread the failure.

**What was built**
- Circuit breaker, timeouts and fallbacks on the two Feign clients in rating-service (`HOTEL-SERVICE`, `USER-SERVICE`), configured in `config-repo/rating-service.properties`.
- A `FallbackFactory` per client. It rethrows 4xx errors and returns a failure `ApiResponse` (`data = null`) for outages.
- `getAllDetailsOfARating` now degrades gracefully with `"Unavailable"` placeholders.
- `createRating` fails with a `503` when the user or hotel cannot be verified, so no unverified rating is saved.
- Added `ServiceUnavailableException` and handlers for it and for a remote 404.

**Learned**
- The circuit breaker states: CLOSED, OPEN, HALF_OPEN, and the settings that drive them.
- Fallbacks must be honest: degrade for reads, fail for writes.
- 4xx responses are valid answers and must not open the circuit or be hidden by the fallback.
- Timeouts need setting explicitly: Feign defaults are very long and the Resilience4j time limiter defaults to 1 s.

**Tested:** healthy call, service down, circuit opening and fail-fast, recovery through HALF_OPEN, 404 not opening the circuit, and a 503 on `createRating`. All behaved as expected.

---

## 2026-11-17 to 2026-11-19: Security (auth-server and gateway filter)

### `8804e15`, `a309d82`, `263b69f`: auth-server
- New `auth-server` with its own credentials DB (`AuthUser`, `Role`), `CustomUserDetailsService`, BCrypt, an `AuthenticationManager`, a custom `JwtUtil` (JJWT, HS256) and a `JwtFilter`.
- APIs: `POST /api/v1/auth` (register), `POST /api/v1/auth/login` (returns JWT), `GET /api/v1/auth/validate` (returns userId, username, role).
- User credentials are synced from user-service through Feign.
- **Learned:** stateless security config, how `AuthenticationManager` verifies a password, JWT claims and expiry.

### Gateway `AuthenticationFilter` (uncommitted at time of writing)
- Global filter (order -1) that asks auth-server to validate the Bearer token, then applies path-based role rules and forwards `X-User-Id/Name/Role` headers.
- Public: `POST /api/v1/auth/login` and `POST /api/v1/user`. Fails closed: 401 (bad token), 403 (wrong role), 503 (auth-server unavailable).
- Strips client-supplied identity headers to stop spoofing.
- Added `WebClientConfig` (load-balanced `WebClient.Builder`) and `ValidateTokenResponse` DTO.
- **Learned:** reactive `GlobalFilter`, remote token validation vs local JWT verification, fail-closed design, header spoofing.

---

## 2026-11-19: Rate limiting with Redis (uncommitted at time of writing)
- Docker Desktop could not be installed (no admin rights for WSL), so Redis runs as the portable Windows build (5.0.14) on port 6379.
- Practised `SET`, `GET`, `INCR`, `EXPIRE`, `TTL`, `KEYS` in `redis-cli`.
- Gateway: added `spring-boot-starter-data-redis-reactive`, `RateLimiterConfig` (`ipKeyResolver` = client IP; Gateway already adds the route id to the Redis key) and a `RequestRateLimiter` filter on each route in `config-repo/api-gateway.properties`.
- Limits: login burst 10, 5/s; user, hotel, rating burst 40, 20/s.
- `AuthenticationFilter` order changed from `-1` to `100`, so the limiter rejects floods before the auth-server call.
- **Learned:** token bucket, atomic counters, Lua scripts in Redis, per-route buckets, filter ordering between route and global filters.

---

## 2026-11-23: Asynchronous messaging with RabbitMQ (uncommitted at time of writing)
- Docker is unavailable, so RabbitMQ 4.4.1 and Erlang/OTP 28 run as portable builds in `~/tools` (no admin). Helper scripts: `start-rabbitmq.bat`, `stop-rabbitmq.bat`. Ports 5672 (AMQP) and 15672 (management UI).
- Learned the concepts first (producer, consumer, exchange, queue, binding, routing key, ack, vhost) and toured every tab of the management UI.
- Both `rating-service` and `hotel-service`: added `spring-boot-starter-amqp`.
- `rating-service`: `RabbitMQConfig` (topic exchange `rating.exchange`, JSON converter), `RatingCreatedEvent`, `RatingEventPublisher`; `createRating()` publishes after saving.
- `hotel-service`: durable queue `hotel.rating.queue` bound with key `rating.created`, `@RabbitListener` `RatingEventListener`, new `Hotel` fields `averageRating` and `ratingCount`, and an atomic JPQL `applyNewRating` update.
- **Tested:** happy path (hotel average updates) and consumer-offline (message waited in the queue, consumed on restart).
- **Learned:** the queue holds messages, not the exchange; a message published with no bound queue is dropped; consumers own their queues; at-least-once delivery means idempotent consumers; update aggregates atomically in SQL.
- **Known gaps (next):** a failed publish is only logged (event lost), a throwing listener loops forever, rating updates are not published.

---

## Current architecture
```
Client -> API Gateway :8080 (AuthenticationFilter) --+--> user-service   :8081
              |                                      +--> hotel-service  :8082
              +--> auth-server (validate)            +--> rating-service :8083 --(Feign)--> user / hotel

All services register with   -> Eureka :8761
All services fetch config    -> Config Server :8888 -> config-repo (git)

rating-service --(RatingCreatedEvent)--> RabbitMQ :5672 [rating.exchange -> hotel.rating.queue] --> hotel-service
```

## Housekeeping
- `config-repo` is a separate repo. Still to do: push it to its own (private) GitHub repo.
- Still to do: stop tracking `.idea/` in this repo.

## Roadmap ahead
1. **Resilience (in progress):** circuit breaker, timeouts and fallbacks on rating-service are done. Still to do: retry with backoff, a circuit breaker at the gateway with a clean 503, and load balancing across several instances.
2. **Security (gateway part done):** custom auth-server and gateway JWT validation with role rules are built. Still to do: defence in depth inside services, Feign identity propagation, ownership checks, secret management, refresh tokens.
3. **Messaging (RabbitMQ basics done):** the rating-created event flow works. Still to do: reliability (retries, dead-letter queues, idempotency, publisher confirms), exchange types hands-on, Kafka, plus Saga and outbox patterns.
4. **Observability:** distributed tracing, centralized logging, metrics.
5. **Packaging and deployment:** Docker, Docker Compose, Kubernetes, CI/CD.
6. **Quality:** Testcontainers, contract tests, OpenAPI docs.
