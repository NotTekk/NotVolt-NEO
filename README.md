# NotVolt NEO

Quickstart

1. Copy environment file

```bash
cp .env.example .env
```

2. Start services

```bash
docker compose up -d
```

3. Build all modules (Java 24 required)

```bash
./gradlew build
```

4. Run web app

```bash
./gradlew :notvolt-web:bootRun
```

5. Run bot (requires `NOTVOLT_TOKEN` in env)

```bash
./gradlew :notvolt-bot:run
```