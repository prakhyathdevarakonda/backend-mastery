# Five Problems - Quick Revision

Use this before interviews. First answer in your own words, then check the short answer.

## 1. Race Condition

Core idea:

- `count++` is read -> add -> write.
- Multiple threads can overwrite each other.
- `volatile` gives visibility, not atomicity.

Best answers:

- `synchronized` for simple mutual exclusion or compound state.
- `AtomicInteger` for exact atomic counter.
- `LongAdder` for high-contention metrics counters.

Follow-ups interviewer can ask:

- What is Java Memory Model?
- What is happens-before?
- How does CAS work?
- Difference between `AtomicInteger` and `LongAdder`?
- Can `LongAdder.sum()` be slightly stale?
- How would you benchmark contention?

Internal concepts:

- Happens-before edges: monitor unlock/lock, volatile write/read, thread start/join.
- CAS retry loop in `AtomicInteger`.
- Contention reduction via striped cells in `LongAdder`.

## 2. ThreadLocal Leak

Core idea:

- `ThreadLocal` value belongs to thread, not request.
- Thread pool reuses threads.
- Missing `remove()` can leak user/context data.

Best answer:

```java
try {
    USER.set(user);
} finally {
    USER.remove();
}
```

Follow-ups interviewer can ask:

- How does `ThreadLocalMap` store keys and values?
- Why are keys weak references but values still leak?
- How does Spring Security use `SecurityContextHolder`?
- What happens with async methods and security context?
- How would you propagate context safely?
- Where would you clean context: filter, interceptor, or service?

Internal concepts:

- `ThreadLocalMap.Entry` keeps weak key but strong value.
- Stale values are cleared lazily, not instantly.

## 3. LazyInitializationException

Core idea:

- Transaction/session closes before lazy association is accessed.
- Controller touches lazy field too late.

Best answer:

- Avoid returning entities from APIs.
- Use DTO projection for API responses.
- Use `JOIN FETCH` or `EntityGraph` when entity graph is needed.
- Disable or avoid relying on Open Session in View.

Follow-ups interviewer can ask:

- What is persistence context?
- What is dirty checking?
- Lazy vs eager loading?
- What is N+1 query problem?
- Difference between `JOIN FETCH` and normal `JOIN`?
- Why DTO projections scale well?
- How does `@Transactional` work internally?

Internal concepts:

- Lazy fields are proxies tied to session.
- Transaction behavior comes from Spring AOP proxy.

## 4. Lost Update

Core idea:

- Two transactions read same balance.
- Both write based on stale value.
- One update is lost.

Best answer:

- Optimistic locking with `@Version` when conflicts are rare.
- Pessimistic locking when correctness is critical and conflicts are likely.
- Atomic SQL update can be best for simple balance changes.

Follow-ups interviewer can ask:

- What are isolation levels?
- Does READ COMMITTED prevent lost update?
- How does `SELECT FOR UPDATE` work?
- What happens under SERIALIZABLE?
- How do you handle retries after optimistic lock failure?
- How would you test with two concurrent transactions?
- How do indexes affect locking/performance?

Internal concepts:

- Postgres MVCC snapshots drive read behavior.
- `@Version` conflict usually means `UPDATE ... WHERE version=?` affects zero rows.

## 5. CompletableFuture Starvation

Core idea:

- Fixed pool is exhausted.
- Worker threads block using `.join()`.
- Nested tasks queued to same pool cannot run.

Best answer:

- Avoid blocking inside async pipeline.
- Use `thenCompose`.
- Separate executors for CPU and blocking IO.
- Add timeouts and fallbacks.

Follow-ups interviewer can ask:

- Deadlock vs starvation?
- What is common fork-join pool?
- What happens if all request threads block?
- When do virtual threads help?
- What limits remain with virtual threads?
- How do you monitor executor queue size?
- How do you design bulkheads?

Internal concepts:

- Many CF stages default to `ForkJoinPool.commonPool()` when executor is omitted.
- Blocking common pool can impact unrelated async flows in same JVM.

## 2-Minute Answer Formula

Use this structure:

1. "The bug is..."
2. "It happens because..."
3. "At runtime, the timeline is..."
4. "The safe fix is..."
5. "Tradeoff is..."
6. "I would test it by..."
