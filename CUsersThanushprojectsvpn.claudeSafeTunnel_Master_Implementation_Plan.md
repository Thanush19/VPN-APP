## Implementation Status

After every phase, update this section:

### Current Phase
`Phase 8 – WireGuard VPN Server POC`

### Status
`IN PROGRESS`

### Completed
- [x] Created repository structure
- [x] Created CLAUDE.md with project context and development rules
- [x] Created README.md with project overview
- [x] Created docs/00-product-overview.md with product vision and core features
- [x] Created Android module structure
- [x] Set up Gradle build system with version catalog
- [x] Configured Hilt dependency injection
- [x] Implemented basic MVI architecture
- [x] Created placeholder screens for Home, Servers, and Settings
- [x] Created basic UI components and theming
- [x] Set up MainActivity with NavHost
- [x] Created Spring Boot application with Java 17
- [x] Configured PostgreSQL and Flyway
- [x] Added global exception handler with ProblemDetail responses
- [x] Created health endpoints
- [x] Added domain exceptions
- [x] Added initial integration test for health endpoints
- [x] Updated application.yml with dev & test profiles
- [x] Created Flyway migration for database schema
- [x] Added JPA entity classes
- [x] Created Spring Data JPA repositories
- [x] Added Spring Security + JWT dependencies
- [x] Created SecurityConfig (stateless, permit /auth/**, JWT filter)
- [x] Added BCryptPasswordEncoder bean
- [x] Implemented JwtUtils (generate/validate tokens)
- [x] Implemented AuthService (register/login)
- [x] Implemented AuthController (register/login/refresh endpoints)
- [x] Retrofit API for auth endpoints
- [x] AuthRepository (token storage in DataStore)
- [x] AuthViewModel + MVI intents
- [x] Login / Register screens (Compose)
- [x] Navigation to Home after successful login
- [x] ServerController (GET /api/v1/vpn/servers)
- [x] Seeded one development VPN server via Flyway
- [x] ServersRepository (already in feature/servers)
- [x] ServersViewModel + MVI intents
- [x] Server list UI (Compose) with latency stub
- [x] Navigation from home to server list

### Tests Passed
- [x] Project syncs successfully (Gradle build configured)
- [x] ./gradlew bootJar builds
- [x] ./gradlew test passes the health endpoint test
- [x] ./gradlew :android:assembleDebug passes
- [x] ./gradlew :android:test passes (unit tests for ViewModel and repository)
- [x] ./gradlew :feature:servers:assembleDebug passes
- [x] ./gradlew :feature:servers:test passes (unit tests for ViewModel and repository)

### Known Issues
- [ ] Need to add indexes and unique constraints (e.g., unique email, unique server host/port, unique peer IP)
- [ ] Need integration tests with Testcontainers to verify schema and repositories
- [ ] VPN functionality not implemented (currently working on this - Phase 8)

### Next Step
`Phase 9 – Android VPN Core`
