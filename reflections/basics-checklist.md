# Basics Checklist

Use this after completing the first five labs. Mark an item only if you can explain it without notes.

## Java Basics

- [ ] I can explain stack vs heap at a high level.
- [ ] I can explain object references in Java.
- [ ] I can explain why mutable shared state is risky.
- [ ] I can explain the difference between primitive values and object references.
- [ ] I can explain what an exception stack trace tells me.

## Concurrency Basics

- [ ] I can explain what a thread is.
- [ ] I can explain why thread scheduling makes bugs non-deterministic.
- [ ] I can explain visibility vs atomicity.
- [ ] I can explain why `volatile` does not make `count++` safe.
- [ ] I can explain when `synchronized` is useful.
- [ ] I can explain basic executor/thread pool behavior.

## Spring Boot Basics

- [ ] I can explain what a controller, service, and repository do.
- [ ] I can explain why transaction boundaries matter.
- [ ] I can explain why request threads are reused.
- [ ] I can explain where request-scoped cleanup should happen.
- [ ] I can explain why returning entities directly from APIs can be risky.

## Database Basics

- [ ] I can explain read-modify-write.
- [ ] I can explain how two requests can update the same row incorrectly.
- [ ] I can explain why database tests are needed for transaction bugs.
- [ ] I can explain what a lock does at a high level.

## Basic Readiness Signal

You are basic-backend ready when you can:

- Read a backend bug scenario and identify the likely failing component.
- Explain the issue with simple language.
- Reproduce the issue using a small example.
- Avoid guessing when runtime behavior depends on concurrency or transactions.
