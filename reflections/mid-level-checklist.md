# Mid-Level Backend Checklist

Use this after you can reproduce and fix all five starting labs.

## Root Cause Analysis

- [ ] I can explain the exact root cause, not just name the bug.
- [ ] I can draw a timeline for race conditions and lost updates.
- [ ] I can explain what the runtime, framework, or database is doing.
- [ ] I can separate symptoms from causes.
- [ ] I can identify when a bug is caused by thread reuse, transaction scope, or executor starvation.

## Fix Selection

- [ ] I can compare at least two fixes for each issue.
- [ ] I can explain why `volatile` is not enough for atomicity.
- [ ] I can choose between `synchronized`, `AtomicInteger`, and `LongAdder`.
- [ ] I can choose between DTO projection, `JOIN FETCH`, and `EntityGraph`.
- [ ] I can choose between optimistic and pessimistic locking.
- [ ] I can replace blocking async code with proper future composition.

## Testing

- [ ] I can write or describe a reliable concurrency test.
- [ ] I can explain why some bugs need integration tests.
- [ ] I can use latches or barriers to force concurrent timing.
- [ ] I can test timeout behavior for hanging async flows.
- [ ] I can prove that a fix works instead of assuming it works.

## Production Thinking

- [ ] I can explain the production impact of each bug.
- [ ] I can identify likely logs, metrics, or traces that would reveal the issue.
- [ ] I can explain when retries are safe and when they are dangerous.
- [ ] I can reason about contention and performance tradeoffs.
- [ ] I can communicate the fix clearly to another developer.

## Mid-Level Readiness Signal

You are mid-level-backend ready when you can:

- Reproduce backend issues in small labs.
- Explain root cause with accurate technical language.
- Choose a correct fix and explain tradeoffs.
- Write a test that would catch the bug again.
- Connect the issue to real production behavior.
