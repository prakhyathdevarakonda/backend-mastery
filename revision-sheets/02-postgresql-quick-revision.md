# PostgreSQL - Quick Revision

## Must-Know Areas

- tables, primary keys, foreign keys
- indexes and query plans
- transactions and isolation levels
- row locks and deadlocks
- slow query debugging

## Indexing

What to say:

- Index helps database find rows without scanning full table.
- Good index depends on query pattern.
- Indexes speed reads but slow writes and use storage.

Example:

```sql
CREATE INDEX idx_wallet_tx_wallet_created_at
ON wallet_transactions(wallet_id, created_at DESC);
```

Follow-ups:

- What is composite index?
- Does column order matter?
- Why can database ignore an index?
- What is covering index?
- How do indexes affect writes?
- How do you prove index helped?

Proof:

```sql
EXPLAIN ANALYZE
SELECT *
FROM wallet_transactions
WHERE wallet_id = 1
ORDER BY created_at DESC
LIMIT 20;
```

## Isolation Levels

Simple explanation:

- READ COMMITTED: each query sees committed data at query start.
- REPEATABLE READ: same transaction sees stable snapshot.
- SERIALIZABLE: strongest isolation, may abort conflicting transactions.

Internal concepts:

- Postgres uses MVCC row versions (`xmin`/`xmax`) to build snapshots.
- READ COMMITTED creates snapshot per statement.
- REPEATABLE READ creates snapshot per transaction.

Follow-ups:

- What is dirty read?
- What is non-repeatable read?
- What is phantom read?
- Does isolation alone solve every lost update?
- Why can SERIALIZABLE reduce throughput?

## Locking

Optimistic locking:

- no lock while reading
- version checked during update
- conflict causes failure/retry

Pessimistic locking:

- lock row before update
- other transaction waits
- useful for money/inventory/booking

Internal concepts:

- `SELECT ... FOR UPDATE` acquires row-level locks.
- Long transactions hold locks longer and increase contention risk.

Follow-ups:

- What is `SELECT FOR UPDATE`?
- What is lock wait timeout?
- What is deadlock?
- How do you reduce lock duration?
- Why keep transactions short?

## Production Diagnostics

Useful query:

```sql
SELECT pid, wait_event_type, wait_event, query
FROM pg_stat_activity
WHERE state <> 'idle';
```

Follow-ups:

- How do you find slow queries?
- What metrics do you monitor?
- How do connection pools affect performance?
- What happens if DB connections are exhausted?
- Why should API pagination be mandatory?

## Interview Answer Formula

For any DB performance issue:

1. identify query pattern
2. inspect query plan
3. add or adjust index
4. measure with `EXPLAIN ANALYZE`
5. mention write/storage tradeoff
