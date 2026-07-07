# Lab 01: Race Condition

## Goal

Experience how a shared Java counter loses updates when multiple threads call `count++` concurrently.

## Run It Quickly

From this folder:

```bash
java RaceConditionLab.java
```

## Build The Broken Version

Create a small Java program with:

- A `Counter` class with `private int count`
- An `increment()` method using `count++`
- 100 worker threads
- Each worker increments 10,000 times
- A final print of the expected and actual count

Expected count:

```text
1,000,000
```

## What To Observe

Run the program multiple times.

Look for:

- Final count lower than expected
- Different incorrect result on different runs
- More visible failures when thread count or increment count increases

## Explain The Root Cause

Write a timeline like this:

```text
Thread A reads count = 10
Thread B reads count = 10
Thread A writes 11
Thread B writes 11
One increment is lost
```

## Fixes To Try

Try these fixes one by one:

1. `synchronized increment()`
2. `AtomicInteger.incrementAndGet()`
3. `LongAdder.increment()`

## Compare

Answer:

- Which fixes correctness?
- Which is simplest?
- Which scales best under high contention?
- Why does `volatile` not solve the issue?

## Done When

You can explain:

- Difference between visibility and atomicity
- Why `count++` is read-modify-write
- When to choose `synchronized`, `AtomicInteger`, or `LongAdder`
