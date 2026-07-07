# Backend Mastery Labs

These labs are for experiencing backend issues directly, not just reading answers.

Each lab should follow the same learning loop:

1. Reproduce the failure.
2. Observe the wrong runtime behavior.
3. Explain the root cause with a timeline.
4. Apply one or more fixes.
5. Compare tradeoffs.
6. Write a reflection in your own words.

## Lab Order

1. `01-race-condition` - Java counter race condition
2. `02-threadlocal-leak` - ThreadLocal leak in pooled threads
3. `03-lazy-initialization` - JPA `LazyInitializationException`
4. `04-lost-update` - Lost update in Spring Boot transactions
5. `05-completablefuture-starvation` - `CompletableFuture` starvation with fixed pools

## Completion Rule

A lab is complete only when you can answer these questions without notes:

- What failed?
- Why did it fail?
- What did the runtime do?
- Which fix did you choose?
- Why is that fix production-grade?
- How would you test it reliably?

## Suggested Lab File Pattern

Each lab can later grow into this structure:

```text
lab-folder/
  README.md
  notes.md
  src/
  tests/
```

For now, each lab starts with a `README.md` guide. Add code only when you are ready to run the experiment.
