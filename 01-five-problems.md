# 01 - The Initial 5 Problems

These are the same five backend-style interview problems.

Important: each problem is intentionally structured as:

- Problem
- Think first
- Hints
- Hidden solution

---

## Problem 1: Race Condition

### Problem

Many threads call `count++` on a shared counter.

Question themes:

- Why is final count wrong?
- Does `volatile` fix it?
- Compare `synchronized`, `AtomicInteger`, and `LongAdder`.

### Think First

- What operations happen inside `count++`?
- Where can two threads interleave?
- What does `volatile` guarantee and what does it not guarantee?

### Hints

- `count++` is read, add, write.
- Visibility is not the same as atomicity.
- High contention changes the best counter choice.

<details>
<summary>Solution</summary>

`count++` is not atomic, so increments are lost under concurrency.

`volatile` does not fix this because it gives visibility only.

Fix options:

- `synchronized`: correct and simple, but lock contention under load.
- `AtomicInteger`: lock-free CAS, good default for exact counters.
- `LongAdder`: best throughput under high contention for metrics-like counters.

</details>

### Internals You Should Know

- Java Memory Model defines when one thread is guaranteed to see another thread's writes.
- `volatile` creates visibility guarantees (happens-before between write and later read), but does not make read-modify-write atomic.
- `AtomicInteger` uses CAS (compare-and-swap): if value changed before update, operation retries.
- `LongAdder` reduces contention by spreading updates across multiple cells, then summing.

---

## Problem 2: ThreadLocal Memory Leak

### Problem

A request user is stored in static `ThreadLocal`. Application uses thread pool.

Question themes:

- What production issue happens?
- Why GC may not clean immediately?
- Where should cleanup happen?
- How Spring Security avoids this issue?

### Think First

- Is `ThreadLocal` data tied to request or thread?
- Are thread-pool threads reused?
- What happens when cleanup is forgotten?

### Hints

- Reused worker thread can carry old context.
- `remove()` is mandatory in `finally`.
- Security context is cleared after request lifecycle.

<details>
<summary>Solution</summary>

In a thread pool, threads are reused, so `ThreadLocal` values can leak between requests and hold memory longer than expected.

Always clear context in `finally`:

```java
try {
    USER.set(user);
    // business logic
} finally {
    USER.remove();
}
```

Spring Security uses `SecurityContextHolder` with lifecycle-aware cleanup in the filter chain.

</details>

### Internals You Should Know

- Each `Thread` owns a `ThreadLocalMap`.
- Map key (`ThreadLocal`) is weakly referenced, but value is strongly referenced.
- If key is collected, stale values can stay until map cleanup is triggered by access.
- Spring Security stores auth in `SecurityContextHolder` and clears it in filter-chain cleanup.

---

## Problem 3: LazyInitializationException

### Problem

A `@Transactional` service returns entity. Controller later reads lazy association.

Question themes:

- Why exception happens?
- Why Open Session in View is discouraged?
- Compare `JOIN FETCH`, `EntityGraph`, DTO projection.
- What scales best for APIs?

### Think First

- When does transaction end?
- When is lazy field actually loaded?
- Should API return entities directly?

### Hints

- Controller often runs outside persistence context.
- OSIV can hide expensive query behavior.
- DTO projection gives strongest API control.

<details>
<summary>Solution</summary>

`LazyInitializationException` occurs because lazy association is accessed after transaction/session closes.

Options:

- `JOIN FETCH`: use-case specific eager load.
- `EntityGraph`: reusable fetch plan.
- DTO projection: best for APIs (predictable payload, no accidental lazy queries).

OSIV is usually discouraged for API services due to hidden queries and N+1 risk.

</details>

### Internals You Should Know

- Lazy relations are proxies; real data loads only when proxy is accessed.
- Proxy needs an open Hibernate session (persistence context) to fetch missing data.
- `@Transactional` works through a Spring proxy that starts/commits/rolls back around method execution.
- Open Session in View keeps persistence context open for the whole request, which can hide N+1 problems.

---

## Problem 4: Lost Update in Spring Boot

### Problem

Two requests withdraw from same account at same time:

```java
balance = balance - amount
```

Question themes:

- Why incorrect balance occurs?
- Timeline of failure
- Optimistic vs pessimistic locking
- How to test reliably

### Think First

- What if both requests read same old balance?
- Which locking style fits money operations?
- Why are mocks not enough?

### Hints

- Read-modify-write race on same row.
- `@Version` detects conflicts.
- Pessimistic lock serializes access.

<details>
<summary>Solution</summary>

This is a classic lost update:

- Tx A reads 1000
- Tx B reads 1000
- both write 900

Optimistic locking:

- add `@Version`
- second conflicting update fails and must retry

Pessimistic locking:

- row lock (`PESSIMISTIC_WRITE`)
- second transaction waits

For money-like operations, stronger correctness (often pessimistic or atomic SQL update) is preferred.

</details>

### Internals You Should Know

- Postgres uses MVCC (multi-version concurrency control): concurrent readers see snapshots.
- Default isolation is READ COMMITTED (new snapshot per statement).
- Optimistic locking is usually implemented as `UPDATE ... WHERE id=? AND version=?`.
- Pessimistic locking uses row locks (`FOR UPDATE`) and forces serialization.

---

## Problem 5: CompletableFuture Deadlock / Starvation

### Problem

Async chain blocks using `.join()` while using a tiny fixed pool that also runs nested tasks.

Question themes:

- Why requests hang?
- Explain thread starvation
- Redesign approach
- When to consider virtual threads

### Think First

- What happens if all pool threads block?
- Who executes queued nested tasks?
- Is this true deadlock or starvation?

### Hints

- Blocking inside async pipeline reduces worker availability.
- `thenCompose` avoids nested blocking.
- Separate pools for different workloads.

<details>
<summary>Solution</summary>

Pool starvation happens when worker threads are blocked waiting for work queued on the same exhausted pool.

Fixes:

- Avoid `thenApply(...join())`
- Use `thenCompose(...)` and async composition
- Use dedicated executors for blocking IO vs orchestration
- Add timeouts and fallbacks

Virtual threads help in blocking IO-heavy services, but they do not remove DB pool limits, locking issues, or bad retries.

</details>

### Internals You Should Know

- Without explicit executor, many `CompletableFuture` operations use `ForkJoinPool.commonPool()`.
- Blocking a worker with `.join()` can starve the pool when queued work also needs that pool.
- Starvation is resource exhaustion; deadlock is circular waiting.
- Virtual threads make blocking cheaper, but do not remove limits like DB connection pools.

---

## After You Finish All 5

Go to:

- [`02-big-picture-map.md`](02-big-picture-map.md)
- [`03-postgresql-mac-and-scenarios.md`](03-postgresql-mac-and-scenarios.md)
- [`04-spring-security-and-aop.md`](04-spring-security-and-aop.md)
