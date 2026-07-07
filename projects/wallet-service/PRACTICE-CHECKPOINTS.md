# Wallet Service Practice Checkpoints

Use this file to turn the project into interview practice.

Do not do everything at once. Finish one checkpoint, explain it, then move next.

## Checkpoint 1: Understand Current API

Tasks:

- run app
- create wallet
- deposit
- withdraw
- list transactions
- run tests

Explain:

- What is controller responsibility?
- What is service responsibility?
- What is repository responsibility?
- Why use DTOs instead of exposing entities?

Follow-ups:

- How would you version this API?
- How would you validate request body?
- What error format should APIs return?

## Checkpoint 2: Lost Update Lab

Tasks:

- write concurrent withdraw test
- reproduce wrong balance if possible
- document timeline

Explain:

- Why two transactions read same value
- Why final balance can be wrong
- Why mocks are not enough

Follow-ups:

- Which isolation level are you using?
- What does PostgreSQL do by default?
- How would you force the race reliably?

## Checkpoint 3: Locking Fix

Tasks:

- run `WalletConcurrencyTest`
- inspect optimistic path in `WalletService.withdraw`
- inspect pessimistic path in `WalletService.withdrawWithRowLock`
- call endpoint `POST /api/wallets/{id}/withdrawals/locked`
- compare behavior

Explain:

- when optimistic is better
- when pessimistic is better
- retry strategy after optimistic lock failure

Follow-ups:

- What happens under high contention?
- Can locking reduce throughput?
- How do you avoid long lock duration?

## Checkpoint 4: PostgreSQL Indexing

Tasks:

- insert many wallet transactions
- run transaction-history query
- inspect `EXPLAIN ANALYZE`
- add composite index
- compare before/after

Explain:

- why index helps
- index tradeoffs
- why column order matters

Follow-ups:

- What is full table scan?
- What is covering index?
- Why can an index be ignored?

## Checkpoint 5: Spring Security

Tasks:

- add basic endpoint protection
- identify current user
- explain request security flow

Explain:

- authentication vs authorization
- `SecurityContextHolder`
- ThreadLocal cleanup

Follow-ups:

- What happens in async methods?
- How is JWT different from session?
- How would you secure admin APIs?

## Checkpoint 6: Spring AOP

Tasks:

- add timing/logging aspect for service methods
- inspect logs
- explain proxy behavior

Explain:

- what AOP is
- why `@Transactional` uses proxies
- self-invocation limitation

Follow-ups:

- When should AOP not be used?
- Difference between filter, interceptor, and aspect?
- What is `@Around` advice?

## Checkpoint 7: Kafka/Event-Driven

Tasks:

- run both services (`wallet-service` and `ledger-service`)
- trigger deposit/withdraw in wallet API
- confirm event consumption logs in ledger-service
- trigger duplicate event id scenario (same event replay) and confirm duplicate is ignored
- discuss failure case

Explain:

- why events help decoupling
- what can go wrong if DB commit succeeds but event publish fails
- outbox pattern concept

Follow-ups:

- How do you make consumer idempotent?
- What are Kafka partitions?
- What happens on retry?

## Checkpoint 8: Observability

Tasks:

- add structured logs
- add correlation/request id
- inspect actuator health/metrics
- write one incident note

Explain:

- logs vs metrics vs traces
- what you monitor for slow withdraw
- how to debug hanging requests

Follow-ups:

- What metric shows thread pool starvation?
- What metric shows DB connection exhaustion?
- What should be in logs and what should not?

## Checkpoint 9: CI/CD

Tasks:

- add command/script to run tests
- later add GitHub Actions

Explain:

- why tests run before merge
- what belongs in CI
- what should block deployment

Follow-ups:

- Unit vs integration tests?
- Where should Docker tests run?
- How do rollbacks work?

## Checkpoint 10: Performance and Maintainability

Tasks:

- find one slow path
- measure
- improve
- measure again
- refactor only if needed

Explain:

- bottleneck
- evidence
- change
- tradeoff

Follow-ups:

- CPU bottleneck vs DB bottleneck?
- How do you load test?
- How do you keep service code maintainable?
