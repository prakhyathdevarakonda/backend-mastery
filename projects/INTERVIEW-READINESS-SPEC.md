# Backend + System Design Interview Readiness Spec

## Goal

Make this repository a focused 8-10 week interview prep workspace that builds strong fundamentals before advanced system design.

## Current Repository Analysis

### What is already strong

- Runnable Spring Boot microservices (`wallet-service`, `ledger-service`)
- PostgreSQL transaction handling and locking practice
- Event-driven flow with Kafka producer and consumer
- Idempotency concept at consumer side
- Good practical checkpoint style learning

### Gaps to close for interviews

- Need smaller standalone projects for first-principles system design practice
- Need focused exercises for caching, load balancing tradeoffs, and consistency models
- Need practice framing answers in interview format (requirements -> design -> tradeoffs -> scaling)

## Interview Scope This Spec Covers

- Backend fundamentals (API design, data modeling, concurrency, caching)
- System design fundamentals (availability, consistency, scaling, async workflows)
- Communication pattern for interviews (clear assumptions and tradeoffs)

## Learning Strategy

1. Keep existing wallet/ledger as the main "integration project".
2. Add small projects where each project teaches one core concept.
3. Each project has:
   - functional requirements
   - non-functional requirements
   - minimal API/data design
   - scaling discussion prompts
   - interview questions to practice

## Project Track Added Under `projects/basic-projects`

1. URL Shortener Fundamentals
2. Rate Limiter Service
3. Job Queue + Worker (async processing)
4. Cache-Aside Product Catalog
5. Leaderboard (read-heavy ranking service)

These map directly to your listed system design topics (scalability, CAP tradeoffs, caching, async messaging, database choices).

## Weekly Plan (10 Weeks)

### Weeks 1-2: Foundation and Communication

- Review API and data design basics
- Complete URL shortener spec and walkthrough
- Practice 5-minute system explanation format

### Weeks 3-4: Throughput, Latency, and Backpressure

- Complete rate limiter and job queue specs
- Compare sync vs async execution paths
- Explain failure modes and retry strategy

### Weeks 5-6: Consistency and Caching

- Complete cache-aside catalog spec
- Practice eventual consistency explanations
- Discuss cache invalidation strategies

### Weeks 7-8: Read-heavy System Design

- Complete leaderboard spec
- Compare SQL vs NoSQL decisions
- Discuss sharding and replication options

### Weeks 9-10: Integration + Mock Interviews

- Connect 1-2 basic projects conceptually with wallet/ledger
- Run mock interview rounds:
  - one low-level API design round
  - one high-level system design round
- Capture weak areas and revise

## Interview Answer Framework (Use in Every Mock)

1. Clarify requirements and scale assumptions.
2. Provide high-level architecture.
3. Explain data model and APIs.
4. Discuss bottlenecks and scaling.
5. Cover consistency, availability, and failure handling.
6. Summarize tradeoffs and alternatives.

## Success Criteria

- You can design each basic project in under 30-40 minutes on a whiteboard.
- You can justify SQL vs NoSQL and cache choices with tradeoffs.
- You can explain consistency level choices (strong/eventual/weak) in practical terms.
- You can connect design choices back to production concerns (latency, throughput, failure recovery).

## Non-Goals

- No heavy abstraction or over-engineering.
- No premature distributed complexity.
- No adding large frameworks unless required by a specific exercise.

## Deliverables in This Repository

- This spec file
- `projects/basic-projects/README.md`
- 5 individual basic-project spec documents under `projects/basic-projects`
