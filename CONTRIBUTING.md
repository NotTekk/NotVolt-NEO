# Contributing to NotVolt NEO

- Fork and create a feature branch
- Write clear, tested code; follow code style
- Use conventional commits if possible
- Open a PR with a concise description and screenshots/logs where relevant

## Setup
- Java 24
- Postgres/Redis/Lavalink via docker compose
- `./gradlew build`

## Code
- Prefer composition over inheritance
- Keep public APIs typed and documented

## Tests
- Add unit/integration tests for new modules/features

## Security
- Do not include secrets in code or logs
- Report vulnerabilities via SECURITY.md process
