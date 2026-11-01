# Local setup: all services

One guide to run everything on your Mac, load sample data, call the APIs and **see** what landed in Postgres and Kafka.
(`questionnaire-patterns` is a separate repo with its own README, using the same steps with MongoDB.)

## What runs where

| Thing | How it runs | Address |
| --- | --- | --- |
| PostgreSQL 17 | Docker | `localhost:5432` (db `wallet_service`, user/pass `wallet`/`wallet`) |
| Kafka 3.9 (KRaft, no Zookeeper) | Docker | `localhost:9094` from your Mac · `kafka:9092` inside Docker |
| wallet-service | `mvn spring-boot:run` | http://localhost:8080 |
| ledger-service | `mvn spring-boot:run` | no HTTP API, consumes Kafka (watch its console) |
| screening-workflow-service | `mvn spring-boot:run` | http://localhost:8082 |
| Adminer (Postgres UI, optional) | Docker, `tools` profile | http://localhost:8090 |
| Kafka UI (optional) | Docker, `tools` profile | http://localhost:8085 |
| questionnaire-patterns (other repo) | `mvn spring-boot:run` | http://localhost:8083 |

```
            POST /deposits                 AFTER_COMMIT                     @KafkaListener
  you ───▶ wallet-service ──▶ Postgres ──▶ topic wallet.transactions ──▶ ledger-service (logs, dedupes by eventId)

  you ───▶ screening-workflow ──▶ screening.requests ──▶ AI worker ──▶ screening.results ──▶ status
                 ▲ POST /decision ──▶ screening.decisions ──▶ decision listener ──┘
```

## 1. One-time install

```bash
brew install --cask docker     # Docker Desktop, then open it once so the engine starts (OrbStack also works)
brew install maven openjdk@21  # Java 21 is what these services target
export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # add to ~/.zshrc
docker --version && mvn -v && java -version        # all three should print versions
```

`jq` (pretty JSON in the terminal) ships with macOS. Check with `jq --version`.

## 2. Start infrastructure (Postgres + Kafka)

```bash
cd backend-mastery/projects
docker compose up -d                    # starts postgres + kafka in the background
docker compose ps                       # wait until both show "healthy" (~20 s for Kafka)
docker compose --profile tools up -d    # optional: Adminer + Kafka UI
```

`-d` = detached (runs in the background). Data lives in a Docker volume, so it survives restarts.

## 3. Run the services (one terminal each)

```bash
cd wallet-service && mvn spring-boot:run                # terminal 1  → :8080
cd ledger-service && mvn spring-boot:run                # terminal 2  (consumer, no port)
cd screening-workflow-service && mvn spring-boot:run    # terminal 3  → :8082 (optional)
```

Ready when you see `Started ...Application`. Quick check: `curl -s localhost:8080/actuator/health` → `{"status":"UP"}`.

## 4. Load sample data

```bash
./seed.sh
```

It creates two wallets (Asha, Ravi) with deposits and withdrawals, including the `FOR UPDATE` path, and triggers two expected failures (409 insufficient balance, 400 validation). Then, if screening is running, it submits three resumes that score APPROVED, HUMAN_REVIEW_REQUIRED and REJECTED, and approves the borderline one. Every wallet write also publishes a Kafka event, so ledger's console fills up too.

## 5. APIs

**wallet-service (:8080)**

| Method | Path | Body | Notes |
| --- | --- | --- | --- |
| POST | `/api/wallets` | `{"ownerName":"Asha"}` | 201 + `Location` header |
| GET | `/api/wallets/{id}` | | |
| POST | `/api/wallets/{id}/deposits` | `{"amount":100.00}` | publishes a Kafka event after commit |
| POST | `/api/wallets/{id}/withdrawals` | `{"amount":30.00}` | optimistic lock (`@Version`) |
| POST | `/api/wallets/{id}/withdrawals/locked` | `{"amount":30.00}` | pessimistic lock (`SELECT … FOR UPDATE`) |
| GET | `/api/wallets/{id}/transactions` | | history, oldest first |

**screening-workflow-service (:8082)**

| Method | Path | Body | Notes |
| --- | --- | --- | --- |
| POST | `/api/screenings` | `{"candidateName","email","resumeText"}` (resume ≥ 30 chars) | 202 Accepted: processing is async. Optional header `X-Correlation-Id` |
| GET | `/api/screenings/{id}` | | `RECEIVED → PROCESSING → APPROVED / REJECTED / HUMAN_REVIEW_REQUIRED` |
| POST | `/api/screenings/{id}/decision` | `{"decision":"APPROVE"\|"REJECT","reason":"..."}` | only when status is `HUMAN_REVIEW_REQUIRED` (else 409) |

Scoring is keyword-based: spring +20, kafka +20, microservice +15, testing/junit +10, >500 chars +10, starting at 30. ≥70 → approved, 45–69 → human review.

Example:

```bash
curl -s -X POST localhost:8080/api/wallets -H 'Content-Type: application/json' -d '{"ownerName":"me"}' | jq
curl -s -X POST localhost:8080/api/wallets/1/deposits -H 'Content-Type: application/json' -d '{"amount":50}' | jq
curl -s localhost:8080/api/wallets/1/transactions | jq
```

## 6. See the data

### Postgres

**Terminal:**

```bash
docker exec -it backend-mastery-postgres psql -U wallet -d wallet_service
```

```sql
\dt                                              -- list tables: wallets, wallet_transactions
SELECT * FROM wallets;                           -- note the version column (optimistic locking)
SELECT id, wallet_id, type, amount, balance_after, created_at FROM wallet_transactions ORDER BY id;
\q
```

**UI:** http://localhost:8090 (Adminer). System `PostgreSQL`, server `postgres`, user `wallet`, password `wallet`, database `wallet_service`.

### Kafka

**Terminal:**

```bash
kafka() { docker exec -it backend-mastery-kafka /opt/kafka/bin/"$@"; }   # helper; works in zsh and bash
kafka kafka-topics.sh --bootstrap-server localhost:9092 --list
kafka kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic wallet.transactions \
   --from-beginning --property print.key=true          # key = walletId; Ctrl+C to stop
kafka kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group ledger-service   # offsets + LAG
```

**UI:** http://localhost:8085 (Kafka UI). Look at Topics → `wallet.transactions` → Messages, and Consumers → `ledger-service` → lag.

**Try this:** stop ledger (Ctrl+C), make 3 deposits, check the consumer group LAG = 3, start ledger again and watch it catch up. That's offsets and consumer lag, seen live.

## 7. Stop / reset

```bash
docker compose stop                          # pause, keep data
docker compose --profile tools down          # remove containers, keep data
docker compose --profile tools down -v       # remove containers AND data (fresh start)
```

## Running the apps inside Docker too (optional)

```bash
docker compose --profile apps up -d --build   # builds and runs wallet, ledger, screening in containers
docker logs -f backend-mastery-ledger-service
```

Inside Docker the apps reach Kafka at `kafka:9092` and Postgres at `postgres:5432` (set via environment variables in `docker-compose.yml`). Don't run the same app on the host at the same time, because the ports would clash.

## Troubleshooting

| Symptom | Fix |
| --- | --- |
| `docker: command not found` / `Cannot connect to the Docker daemon` | Install Docker Desktop and open it |
| `Port 8080 was already in use` | Another app is on it: `lsof -i :8080`, then stop it |
| wallet starts but logs Kafka connection errors | Kafka isn't healthy yet (`docker compose ps`), or run without Kafka: `APP_KAFKA_ENABLED=false mvn spring-boot:run` |
| `Connection refused` to 5432 | `docker compose up -d` wasn't run, or another Postgres is using 5432 |
| Build fails on a very new JDK | Use Java 21: `export JAVA_HOME=$(/usr/libexec/java_home -v 21)` |

## Tests

```bash
mvn test                                    # in any service folder; uses H2 + EmbeddedKafka, no Docker needed
mvn test -Dtest='!*KafkaIntegrationTest'    # fast loop, skip Kafka integration tests
```

## Design notes worth explaining in interviews

- **Publish after commit:** `WalletService` raises an event and `WalletEventPublisher` sends it with `@TransactionalEventListener(AFTER_COMMIT)`, so Kafka never sees a rolled-back deposit. The remaining gap is a commit followed by a Kafka outage, where the event is lost. The fix is a transactional outbox (Week 5).
- **Message key = walletId:** all events for one wallet go to the same partition, so they stay in order.
- **No shared event JAR:** `WalletTransactionEvent` is duplicated in wallet and ledger on purpose. The contract is the JSON, not a Java class (type headers are disabled).
- **Idempotency:** ledger and screening dedupe by `eventId` in memory, so the record is lost on restart. Durable version: a processed-events table (Week 5).
- **`app.kafka.enabled=false`** turns off publisher and listener beans (`@ConditionalOnProperty`). Tests use it.
