# Project 02: Rate Limiter Service

## Primary Concepts

- throughput control
- back pressure
- low-latency decision path
- consistency vs availability tradeoff

## Functional Requirements

- Limit requests by user ID or API key
- Support per-minute and per-second limits
- Return allow/deny decision with remaining quota

## Non-Functional Requirements

- Decision in a few milliseconds
- Should work under bursty traffic
- Graceful behavior during partial failures

## Suggested API

- `POST /rate-limit/check`
  - input: identifier, route, timestamp
  - output: allowed, remaining, resetAt

## Algorithm Options

- Fixed window (easy, less accurate at boundary)
- Sliding window log (accurate, more memory)
- Token bucket (good for burst handling)

## Suggested Storage Progression

1. In-memory map (single-node learning)
2. Redis with atomic increments + TTL
3. Partitioned keys for large scale

## Design Prompts

- What happens if rate limiter storage is down?
- Fail-open vs fail-closed: when and why?
- Local limiter + global limiter combination?

## Interview Questions

- How do you avoid hot keys?
- How do you enforce tenant-level fairness?
- How would you expose limiter metrics?
