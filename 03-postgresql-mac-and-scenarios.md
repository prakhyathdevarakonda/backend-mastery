# 03 - PostgreSQL on Mac + Scenarios

This guide is for practical backend learning with PostgreSQL on Mac.

## Setup Options on Mac

Use any one option.

### Option A: Docker (recommended)

```bash
docker compose up -d
docker compose ps
```

From this repo:

- `projects/wallet-service/docker-compose.yml`

### Option B: Homebrew

```bash
brew update
brew install postgresql@17
brew services start postgresql@17
```

Connect:

```bash
psql postgres
```

### Option C: Postgres.app

- Download Postgres.app
- Start app
- Use bundled `psql`

## Basic SQL You Should Know

```sql
-- list databases
\l

-- connect database
\c wallet_service

-- list tables
\dt

-- describe table
\d wallets
```

## Topics You Must Learn

### 1) Indexing

Questions:

- Which queries are slow?
- Which columns are used in `WHERE`, `JOIN`, `ORDER BY`?
- Is the index helping or ignored?

Commands:

```sql
EXPLAIN ANALYZE
SELECT * FROM wallet_transactions
WHERE wallet_id = 1
ORDER BY created_at DESC
LIMIT 20;
```

Practical index example:

```sql
CREATE INDEX idx_wallet_tx_wallet_created_at
ON wallet_transactions(wallet_id, created_at DESC);
```

### 2) Isolation Levels

Know behavior of:

- READ COMMITTED
- REPEATABLE READ
- SERIALIZABLE

Interview angle:

- Lost update
- non-repeatable read
- phantom read (conceptually)

### 3) Locking and Contention

Use cases:

- money transfer
- inventory update
- seat booking

Patterns:

- optimistic locking (`version`)
- pessimistic locking (`FOR UPDATE`)
- short transactions to reduce lock wait

### 4) Performance Diagnostics

Use:

```sql
SELECT pid, wait_event_type, wait_event, query
FROM pg_stat_activity
WHERE state <> 'idle';
```

And:

```sql
EXPLAIN ANALYZE ...
```

## Scenarios to Practice

### Scenario A: Transaction History Pagination

Problem:

- transaction history endpoint slows down as data grows

Try:

- measure with `EXPLAIN ANALYZE`
- add composite index
- compare before/after

### Scenario B: Double Withdraw Race

Problem:

- two requests withdraw at same time

Try:

- reproduce lost update
- apply optimistic locking
- apply pessimistic locking
- compare behavior and throughput

### Scenario C: Slow Dashboard Query

Problem:

- reporting query uses full table scan

Try:

- capture plan
- rewrite query shape
- add proper index
- validate speedup

## What to Say in Interview

Short answer structure:

1. Query pattern
2. Current plan and bottleneck
3. Index or query change
4. Tradeoff (write overhead, storage, complexity)
5. Verification with `EXPLAIN ANALYZE`
