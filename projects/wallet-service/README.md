# Wallet Service

Spring Boot service for practicing backend fundamentals: JPA, concurrency, and Kafka event publishing.

## Features

- Create / get wallet
- Deposit and withdraw
- Pessimistic withdraw path: `POST /api/wallets/{id}/withdrawals/locked`
- List transactions
- **Kafka:** publish `wallet.transactions` events after successful DB commit

## Tech stack

- Java 21, Spring Boot 3.5, Maven
- Spring Data JPA + PostgreSQL
- Spring Kafka (producer)
- H2 + EmbeddedKafka in tests

## Run locally

Setup, sample data and how to view the data are in **[`../README.md`](../README.md)** (one guide for all services).

## Kafka producer design

```
WalletService.deposit/withdraw
  └─ @Transactional: save wallet + transaction
  └─ publishAfterCommit(tx)
       └─ ApplicationEventPublisher → WalletTransactionCommittedEvent
            └─ WalletEventPublisher (@TransactionalEventListener AFTER_COMMIT)
                 └─ kafkaTemplate.send("wallet.transactions", walletId, event)
```

| Setting | Value |
|---------|--------|
| Topic | `wallet.transactions` |
| Key | `walletId` (String) |
| Serializer | JSON, no type headers |
| Toggle | `app.kafka.enabled` (default `true`) |

Main config: `src/main/resources/application.yml`

## API examples

Create wallet:

```bash
curl -X POST http://localhost:8080/api/wallets \
  -H 'Content-Type: application/json' \
  -d '{"ownerName":"learner"}'
```

Deposit (triggers Kafka event):

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

Withdraw with row lock:

```bash
curl -X POST http://localhost:8080/api/wallets/1/withdrawals/locked \
  -H 'Content-Type: application/json' \
  -d '{"amount":30.00}'
```

List transactions:

```bash
curl http://localhost:8080/api/wallets/1/transactions
```

After deposit/withdraw, check **ledger-service** logs for `Ledger consumed eventId=...`.

## Tests

```bash
mvn test                              # all (~20s with EmbeddedKafka)
mvn test -Dtest='!*KafkaIntegrationTest'   # fast unit/controller tests
mvn test -Dtest=WalletKafkaIntegrationTest  # producer integration only
```

| Test | What it proves |
|------|----------------|
| `WalletServiceTests` | Basic deposit flow |
| `WalletConcurrencyTest` | Optimistic vs pessimistic locking |
| `WalletKafkaIntegrationTest` | Deposit publishes JSON event to topic |

Tests disable Kafka by default via `src/test/resources/application.yml` (`app.kafka.enabled=false`), except `WalletKafkaIntegrationTest`.

## Project layout (Kafka-related)

```
wallet/
  WalletService.java              # publishAfterCommit on mutations
  WalletEventPublisher.java       # AFTER_COMMIT Kafka send
  WalletTransactionEvent.java     # event payload
  WalletTransactionCommittedEvent.java
  WalletKafkaConfig.java          # @EnableKafka
```

## Practice checkpoints

See `PRACTICE-CHECKPOINTS.md` in this folder:

- Checkpoints 1–6: API, locking, PostgreSQL, Security, AOP
- **Checkpoint 7:** Kafka / event-driven (this service + `ledger-service`)
- Checkpoints 8–10: Observability, CI/CD, performance

## Next learning steps

Already in this repo:

- [x] Optimistic and pessimistic locking (`WalletConcurrencyTest`)
- [x] Kafka events + separate ledger consumer

Good extensions:

- Transactional outbox (reliable publish after commit)
- Idempotency keys on deposit/withdraw API
- Spring Security + AOP (checkpoints 5–6)
- Indexing / `EXPLAIN ANALYZE` on transaction history (checkpoint 4)

Root docs:

- `../../01-five-problems.md`
- `../../03-postgresql-mac-and-scenarios.md`
- `../../04-spring-security-and-aop.md`
- `../README.md`
