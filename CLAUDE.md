# SafeTunnel Project

This CLAUDE.md file serves as the central reference for all developers working on the SafeTunnel project. It outlines the project scope, high‑level architecture, and the development workflow that must be followed.

## Project Context
- **Product**: SafeTunnel – a privacy‑first VPN application targeting Google Play Store.
- **Technology stack**:
  - *Android*: Kotlin, Jetpack Compose, MVI, Hilt, WireGuard integration.
  - *Backend*: Spring Boot 3.x, PostgreSQL, Flyway, JWT authentication.
  - *Infrastructure*: Linux WireGuard server, Docker, HTTPS reverse proxy.

## Master Implementation Plan
The definitive implementation roadmap is documented in
[`SafeTunnel_Master_Implementation_Plan.md`](.claude/SafeTunnel_Master_Implementation_Plan.md).  All work must be coordinated against that plan.

## Documentation
:::
:::
# Reference Documentation
- `docs/*` – Additional project documentation.
- `.claude/SpringBoot-Skills.md` – Spring Boot best‑practice reference.

## Development Rules
1. **Follow the plan** – Do not implement more than one phase at a time.
2. **Read before writing** – The CLAUDE.md and master plan must be read before changes.
3. **No secrets** – Never commit passwords or private keys.
4. **Data safety** – Avoid logging sensitive values.
5. **Build consistently** – Run `./gradlew check` (or equivalent) after all changes.
6. **Test coverage** – All new functionality must have appropriate unit/tests.

## Repository Layout
```
SafeTunnel/
├── android/          # Android source
├── backend/          # Spring Boot source
├── infrastructure/   # Docker / Kubernetes / scripts
├── docs/             # Markdown docs
├── CLAUDE.md
└── README.md
```

## Further Guidance
For detailed design decisions, see the `docs/` folder and the ADR log section of the master plan.  The master plan also documents the development workflow and phase status tracking.

---

**NOTE**: This file should not be modified by automated scripts; updates are performed manually by the developers following the plan.
