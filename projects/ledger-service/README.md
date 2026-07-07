# Ledger Service

`ledger-service` is a separate Spring Boot service that consumes wallet transaction events from Kafka.

It is intentionally simple:

- no database
- no cloud dependency
- in-memory idempotency using event id set

## Why Separate Service

This lets you practice real multi-service behavior:

- one service publishes (`wallet-service`)
- another service consumes (`ledger-service`)
- both run independently or together through Docker Compose

## Run Locally

From this folder:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
mvn spring-boot:run
```

By default it expects Kafka at `localhost:9092`.
