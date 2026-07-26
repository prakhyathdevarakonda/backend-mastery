# Projects Overview

Runnable backend services for hands-on learning: PostgreSQL, concurrency, and Kafka event-driven flows.

## Services

| Service | Port (host) | Responsibility |
|---------|-------------|----------------|
| **wallet-service** | 8080 | Wallet API, PostgreSQL persistence, Kafka producer |
| **ledger-service** | 8081 | Kafka consumer, in-memory idempotency (no DB) |

Paths:

- `projects/wallet-service`
- `projects/ledger-service`

## Kafka Flow (Checkpoint 7)

```
POST /api/wallets/{id}/deposits
        │
        ▼
  wallet-service
  ├─ DB commit (wallet + transaction row)
  └─ AFTER_COMMIT → publish to topic `wallet.transactions`
        │
        ▼
      Kafka
        │
        ▼
  ledger-service
  └─ @KafkaListener → log event (skip duplicate eventId)
```

**Topic:** `wallet.transactions`  
**Message key:** `walletId` (String)  
**Payload:** JSON `WalletTransactionEvent` (duplicated class per service — see below)

### Key classes to read

| Service | Class | Role |
|---------|-------|------|
| wallet | `WalletService` | Calls `publishAfterCommit()` on deposit/withdraw |
| wallet | `WalletEventPublisher` | `@TransactionalEventListener(AFTER_COMMIT)` → `KafkaTemplate.send` |
| wallet | `WalletTransactionEvent` | Event payload record |
| ledger | `WalletTransactionEventListener` | `@KafkaListener`, idempotency via `eventId` set |
| ledger | `LedgerKafkaConfig` | `@EnableKafka` (conditional on `app.kafka.enabled`) |

### Serialization note

Producer and consumer disable Kafka type headers (`spring.json.add.type.headers=false`).  
Both services define their own `WalletTransactionEvent` class (same JSON shape, different Java packages). This avoids a shared Maven module while keeping the setup interview-friendly.

Toggle Kafka without code changes:

```yaml
app:
  kafka:
    enabled: false   # disables publisher / listener beans
```

Tests set `app.kafka.enabled=false` by default so unit tests do not need a running broker.

## Ports and Kafka Listeners

| Where you run | Bootstrap servers |
|---------------|-------------------|
| Services on **host** (Maven) | `localhost:9094` |
| Services **inside Docker Compose** | `kafka:9092` |

Root `docker-compose.yml` exposes Kafka on host port **9094** (`EXTERNAL` listener).  
Inside the Docker network, brokers advertise `kafka:9092` (`PLAINTEXT` listener).

## Run Modes

### Mode A — Postgres only (wallet API, no Kafka)

From `wallet-service`:

```bash
docker compose up -d          # postgres only
mvn test
mvn spring-boot:run
```

Use this for locking, JPA, and API checkpoints. Kafka beans are off in tests; in dev you can set `app.kafka.enabled=false` if no broker is running.

### Mode B — Host services + Docker infra (recommended for Kafka learning)

From `projects`:

```bash
docker compose up -d postgres kafka
```

Terminal 1 — wallet:

```bash
cd wallet-service
mvn spring-boot:run
```

Terminal 2 — ledger:

```bash
cd ledger-service
mvn spring-boot:run
```

Both default to `localhost:9094` for Kafka.

### Mode C — Full Docker stack

From `projects`:

```bash
docker compose up --build
```

Starts PostgreSQL, Kafka (KRaft), wallet-service, and ledger-service.  
Wallet API: http://localhost:8080 — Ledger logs: `docker logs -f backend-mastery-ledger-service`

## End-to-end smoke test

```bash
# 1. Create wallet
curl -X POST http://localhost:8080/api/wallets \
  -H 'Content-Type: application/json' \
  -d '{"ownerName":"learner"}'

# 2. Deposit
curl -X POST http://localhost:8080/api/wallets/1/deposits \
  -H 'Content-Type: application/json' \
  -d '{"amount":100.00}'
```

**Expect:**

- wallet logs: `Published wallet transaction eventId=...`
- ledger logs: `Ledger consumed eventId=... walletId=1 type=DEPOSIT ...`

## Tests

```bash
# All tests (includes EmbeddedKafka integration tests — ~20s total)
cd wallet-service && mvn test
cd ledger-service && mvn test

# Fast loop — skip Kafka integration tests
mvn test -Dtest='!*KafkaIntegrationTest'

# Kafka integration only
mvn test -Dtest=*KafkaIntegrationTest
```

Integration tests use `@EmbeddedKafka` (in-process broker; no Docker required for CI).

## Why No Shared Module

`WalletTransactionEvent` is duplicated in wallet and ledger on purpose:

- simpler Maven layout (two independent apps)
- mirrors real microservices with separate deployables
- forces you to think about JSON contracts, not shared JARs

## Learning checklist

1. Run Mode B or C and trigger deposit/withdraw.
2. Trace code: `WalletService` → `WalletEventPublisher` → topic → `WalletTransactionEventListener`.
3. Explain why publish happens **after** DB commit, not before.
4. Stop ledger, deposit, restart ledger — events consumed from earliest offset (`auto-offset-reset: earliest` on fresh group).
5. Discuss: what if DB commits but Kafka publish fails? (outbox pattern — see `wallet-service/PRACTICE-CHECKPOINTS.md`).

## Further reading (repo root)

- `../05-learning-roadmap.md` — Weeks 7–8 Kafka
- `wallet-service/PRACTICE-CHECKPOINTS.md` — Checkpoint 7 tasks
- `../02-big-picture-map.md` — system diagram
