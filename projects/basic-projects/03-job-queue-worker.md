# Project 03: Job Queue + Worker

## Primary Concepts

- asynchronous processing
- retries and dead-letter queues
- idempotent job handling
- eventual consistency

## Functional Requirements

- Accept job requests from API
- Process jobs asynchronously in worker
- Retry failed jobs with backoff
- Move poison jobs to dead-letter queue

## Non-Functional Requirements

- Reliable processing with at-least-once delivery
- No duplicate side effects even with retries
- Observable job lifecycle

## Suggested API

- `POST /jobs`
- `GET /jobs/{jobId}`
- `POST /jobs/{jobId}/retry`

## Suggested Job States

- `QUEUED`
- `PROCESSING`
- `COMPLETED`
- `FAILED`
- `DEAD_LETTERED`

## Design Prompts

- What data should be part of idempotency key?
- Where do you store retry count and last error?
- How do you avoid infinite retry loops?

## Scaling Discussion

- Separate API node and worker node scaling
- Partition jobs by tenant/type
- Backpressure when queue grows too fast

## Interview Questions

- Why at-least-once is common in distributed systems?
- How do you design safe retries for payment-like jobs?
- When should you use task queue vs message queue?
