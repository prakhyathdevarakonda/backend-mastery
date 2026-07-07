# Wallet Service

Simple Spring Boot project to practice backend concepts in real code.

Current features:

- create wallet
- get wallet
- deposit
- withdraw
- withdraw with row lock (`/withdrawals/locked`)
- list transactions
- publish Kafka events after successful DB commit

## Tech Stack

- Java 21
- Spring Boot 3.5
- Maven
- Spring Data JPA
- PostgreSQL (Docker Compose)
- H2 for tests

## Prerequisites

- Java 21
- Maven 3.8+
- Docker (for PostgreSQL)

## Java 21 Setup (Mac)

Check Java:

```bash
java -version
```

If needed (Homebrew):

```bash
brew install openjdk@21
```

Set Java 21 for current shell:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```

## Run Project

From `projects/wallet-service`:

```bash
docker compose up -d
mvn test
mvn spring-boot:run
```

Full multi-service mode (Postgres + Kafka + wallet + ledger):

```bash
cd ..
docker compose up --build
```

## API Examples

Create wallet:

```bash
curl -X POST http://localhost:8080/api/wallets \
  -H 'Content-Type: application/json' \
  -d '{"ownerName":"learner"}'
```

Deposit:

```bash
curl -X POST http://localhost:8080/api/wallets/1/deposits \
  -H 'Content-Type: application/json' \
  -d '{"amount":100.00}'
```

Withdraw:

```bash
curl -X POST http://localhost:8080/api/wallets/1/withdrawals \
  -H 'Content-Type: application/json' \
  -d '{"amount":30.00}'
```

Withdraw with explicit row lock path:

```bash
curl -X POST http://localhost:8080/api/wallets/1/withdrawals/locked \
  -H 'Content-Type: application/json' \
  -d '{"amount":30.00}'
```

List transactions:

```bash
curl http://localhost:8080/api/wallets/1/transactions
```

## Next Learning Steps

After basics, extend this project with:

- optimistic and pessimistic locking
- isolation-level experiments
- indexing and query tuning
- idempotency for payment-like APIs
- Spring Security protection
- AOP logging/timing
- Kafka events
- separate `ledger-service` consumer
- observability and performance tuning

Use root docs for full path:

- `../../01-five-problems.md`
- `../../03-postgresql-mac-and-scenarios.md`
- `../../04-spring-security-and-aop.md`
- `../README.md`
