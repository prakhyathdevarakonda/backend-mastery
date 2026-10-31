# Ledger Service

Separate Spring Boot app that consumes wallet transaction events from Kafka.

Intentionally minimal:

- **No database** — logs events only
- **In-memory idempotency** — duplicate `eventId` values are ignored
- **No HTTP API** — pure consumer

Use this with `wallet-service` to practice multi-service, event-driven design.

## How it fits

```
wallet-service  ──publish──▶  wallet.transactions  ──consume──▶  ledger-service
     (8080)                           (Kafka)                        (8081 in Docker)
```

## Tech stack

- Java 21, Spring Boot 3.5, Maven
- Spring Kafka (consumer only)
- EmbeddedKafka in integration tests

## Prerequisites

- Java 21, Maven 3.8+
- Kafka reachable at `localhost:9094` (host) or `kafka:9092` (Docker)

### Java 21 (Mac)

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```

## Run locally

### With wallet-service on host

Start Kafka (and Postgres for wallet) from `projects`:

```bash
cd ../..
docker compose up -d kafka postgres
```

Run ledger:

```bash
mvn spring-boot:run
```

Default bootstrap: **`localhost:9094`** (`application.yml`).

Run wallet in another terminal, then deposit — watch this service's console for:

```
Ledger consumed eventId=... walletId=... type=DEPOSIT amount=... balanceAfter=...
```

### Full Docker stack

From `projects`:

```bash
docker compose up --build
docker logs -f backend-mastery-ledger-service
```

Ledger is mapped to **http://localhost:8081** (actuator only; no business API).

## Kafka consumer design

| Setting | Value |
|---------|--------|
| Topic | `wallet.transactions` |
| Group | `ledger-service` (`spring.kafka.consumer.group-id`) |
| Deserializer | JSON → `WalletTransactionEvent`, no type headers |
| Idempotency | `ConcurrentHashMap.newKeySet()` on `eventId` |
| Toggle | `app.kafka.enabled` (default `true`) |

### Key classes

```
ledger/
  WalletTransactionEventListener.java   # @KafkaListener
  WalletTransactionEvent.java           # same JSON shape as wallet-service copy
  LedgerKafkaConfig.java                # @EnableKafka when kafka enabled
  LedgerServiceApplication.java
```

Duplicate event handling:

```java
if (!processedEventIds.add(event.eventId())) {
    log.info("Duplicate event ignored eventId=...");
    return;
}
```

## Event payload (JSON)

```json
{
  "eventId": "uuid",
  "walletId": 1,
  "type": "DEPOSIT",
  "amount": 100.00,
  "balanceAfter": 100.00,
  "occurredAt": "2026-07-26T12:00:00Z"
}
```

`type` is `DEPOSIT` or `WITHDRAW`.

## Tests

```bash
mvn test                              # all
mvn test -Dtest='!*KafkaIntegrationTest'   # unit tests only (~instant)
mvn test -Dtest=WalletTransactionKafkaIntegrationTest
```

| Test | What it proves |
|------|----------------|
| `WalletTransactionEventListenerTest` | Idempotency logic (no Kafka) |
| `WalletTransactionKafkaIntegrationTest` | End-to-end consume from EmbeddedKafka |
| `LedgerServiceApplicationTests` | Smoke: main class loads |

Tests use `src/test/resources/application.yml` with `app.kafka.enabled=false` except profile `kafka-it` for integration tests.

## Learning exercises

1. **Happy path:** deposit in wallet → confirm ledger log line.
2. **Duplicate replay:** explain why the same `eventId` twice only logs once (in-memory set; production would use DB unique constraint).
3. **Consumer down:** stop ledger, make deposits, restart — new consumer group reads from `earliest` uncommitted offsets.
4. **Failure mode:** DB committed in wallet but Kafka down — wallet API succeeds, ledger never sees event. Discuss outbox pattern.
5. **Interview:** draw sync HTTP vs async Kafka boundary between wallet and ledger.

See also `../wallet-service/PRACTICE-CHECKPOINTS.md` — Checkpoint 7.

## Configuration reference

`src/main/resources/application.yml`:

- `KAFKA_BOOTSTRAP_SERVERS` — override bootstrap (default `localhost:9094`)
- `app.kafka.enabled` — set `false` to disable listener

## Related docs

- `../README.md` — multi-service overview, ports, smoke test
- `../wallet-service/README.md` — API and producer side
- Study map, Area 9 (Kafka), week 5
