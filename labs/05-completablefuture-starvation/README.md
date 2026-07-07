# Lab 05: CompletableFuture Starvation

## Goal

Experience how blocking inside `CompletableFuture` chains can starve a fixed thread pool and cause requests to hang.

## Run It Quickly

From this folder:

```bash
java CompletableFutureStarvationLab.java
```

## Build The Broken Version

Create a small Java program with:

- A fixed thread pool of size 2
- A `CompletableFuture.supplyAsync(...)` call using that pool
- A continuation that calls `.join()` on another task
- Nested tasks submitted to the same pool

The broken shape looks like this:

```java
CompletableFuture
    .supplyAsync(this::loadA, executor)
    .thenApply(a -> loadBAsync(a, executor).join());
```

## What To Observe

Look for:

- Requests hanging
- Program not completing until timeout
- Queued tasks waiting forever because all workers are blocked

## Explain The Root Cause

The executor has limited workers. If workers block waiting for tasks that are queued to the same executor, no worker is free to run those queued tasks.

Timeline:

```text
Pool size = 2
Thread 1 blocks waiting for nested task A
Thread 2 blocks waiting for nested task B
Nested task A is queued
Nested task B is queued
No thread is free
Progress stops
```

## Fixes To Try

Try these approaches:

1. Replace `thenApply(...join())` with `thenCompose(...)`
2. Use separate executors for orchestration and blocking IO
3. Add `orTimeout(...)`
4. Try a virtual-thread executor for blocking IO experiments

## Compare

Answer:

- Why is `.join()` dangerous inside async chains?
- What is the difference between deadlock and starvation?
- Why does increasing pool size only hide the problem sometimes?
- When do virtual threads help?
- What resource limits still matter with virtual threads?

## Production Guardrails

Use:

- Timeouts
- Separate executors for different workloads
- Bounded queues
- Bulkheads
- Metrics for active threads, queue size, and task latency

## Done When

You can explain:

- Why the request hangs
- How `thenCompose` avoids blocking
- How executor starvation appears in production
- Why virtual threads are useful but not magic
