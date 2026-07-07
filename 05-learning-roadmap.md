# 05 - Learning Roadmap (Basics -> Mid -> Senior)

Use this as a 12-week practical plan.

## Weeks 1-2: Foundations

- finish all 5 problems
- understand Java concurrency basics
- run wallet service locally
- practice clean API request/response design

Output:

- you can explain each of the 5 problems without notes

## Weeks 3-4: Transactions + PostgreSQL

- practice lost update scenarios
- compare optimistic vs pessimistic locking
- learn isolation levels with examples
- run query plans with `EXPLAIN ANALYZE`
- add and evaluate indexes

Output:

- you can justify lock and isolation choices for money-like operations

## Weeks 5-6: Spring Security + AOP

- add simple endpoint protection
- understand security context lifecycle
- add one AOP logging/timing aspect
- explain transactional proxy behavior

Output:

- you can answer "how Spring Security handles this" confidently

## Weeks 7-8: Kafka + Event-Driven

- publish wallet transaction events
- consume and process idempotently
- understand outbox pattern concept
- inspect `wallet-service` producer classes:
  - `WalletTransactionEvent`
  - `WalletEventPublisher`
- inspect `ledger-service` consumer class:
  - `WalletTransactionEventListener`

Output:

- you can explain sync vs async boundaries in system design

## Weeks 9-10: Observability + Debugging

- structured logs with correlation id
- actuator metrics basics
- trace request flow for failures
- write one incident-style debugging note

Output:

- you can explain how to detect and debug issues in production

## Weeks 11-12: Performance + CI/CD + Maintainability

- profile one slow API path
- improve via indexing/query/code changes
- add basic CI checks (`mvn test`)
- refactor one module for cleaner boundaries

Output:

- you can discuss performance and maintainability tradeoffs clearly

## Weekly Routine

Use this every week:

1. Learn one concept
2. Implement in project
3. Break it intentionally
4. Fix it
5. Write a short reflection
6. Explain it in interview style (2-3 minutes)
