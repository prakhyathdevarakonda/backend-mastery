# Lab 02: ThreadLocal Leak

## Goal

Experience how `ThreadLocal` values can leak across tasks when threads are reused by a pool.

## Run It Quickly

From this folder:

```bash
java ThreadLocalLeakLab.java
```

## Build The Broken Version

Create a small Java program with:

- A static `ThreadLocal<String> USER`
- A fixed thread pool with one or two threads
- Task 1 sets `USER` to a user value
- Task 2 reads `USER` without setting it
- No `remove()` call in the first version

## What To Observe

Look for:

- Task 2 seeing data from Task 1
- Old user data staying attached to a reused worker thread
- Different behavior when using a new thread each time versus a fixed pool

## Explain The Root Cause

Thread pools reuse threads. `ThreadLocal` data is attached to the thread, not to the request or task.

If cleanup is missing:

```text
Request A runs on thread-1
Request A sets USER = prakhyath
Request A finishes but does not remove USER
Request B runs on thread-1
Request B can see stale USER value
```

## Fix To Try

Wrap task logic in `try/finally`:

```java
try {
    USER.set(user);
    // business logic
} finally {
    USER.remove();
}
```

## Spring Boot Connection

Map this to web applications:

- Request threads come from servlet containers like Tomcat
- Those threads are reused
- Request context must be cleared at the end of the request
- Spring Security clears `SecurityContextHolder` after the filter chain

## Done When

You can explain:

- Why pooled threads make leaks more dangerous
- Why GC may not immediately clean stale `ThreadLocal` values
- Where cleanup belongs in Spring Boot
- Why `finally` is required
