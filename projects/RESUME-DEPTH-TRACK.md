# Resume Depth Track (Project-Based)

## Objective

Turn resume claims into interview-ready depth using small, focused, runnable projects.  
This track avoids repeating Kafka basics already covered in `wallet-service` and `ledger-service`.

## Existing Depth You Already Have

- Kafka producer/consumer flow
- Event-driven decoupling
- Idempotent consumer behavior
- Transaction boundary + publish-after-commit concept

Keep these as your primary event-architecture reference projects.

## New Depth Projects (Non-Kafka-Focused)

1. `auth-gateway-service` (Security)
2. `observability-lab-service` (Monitoring and debugging)
3. `catalog-cache-service` (Caching and consistency)
4. `query-tuning-lab-service` (Database performance)
5. `resume-screening-pipeline` (AI workflow + human-in-loop)

## 10-Week Execution Plan

### Weeks 1-2: Security Depth (`auth-gateway-service`)

Build:

- JWT-based authentication
- Role-based authorization for 2-3 roles
- protected endpoints by role
- refresh-token rotation basics

Depth checklist:

- explain authentication vs authorization clearly
- explain token expiry and refresh lifecycle
- explain stateless auth tradeoffs
- explain common security pitfalls (token leakage, weak secrets, over-permissive roles)

Interview drill questions:

- Why JWT over server session?
- How do you revoke a compromised token?
- Where should authorization checks live: gateway or service?
- How do you secure internal service-to-service calls?

### Weeks 3-4: Observability Depth (`observability-lab-service`)

Build:

- correlation-id filter
- structured JSON logs
- actuator metrics endpoint usage
- one simulated incident and debug note

Depth checklist:

- distinguish logs vs metrics vs traces
- identify top 5 production signals to monitor
- explain error budget and SLO in simple terms
- demonstrate root-cause workflow for latency spikes

Interview drill questions:

- How do you trace one request across services?
- What metrics indicate DB pool exhaustion?
- How would you debug intermittent 500 errors?
- What log fields are mandatory in production APIs?

### Weeks 5-6: Caching Depth (`catalog-cache-service`)

Build:

- cache-aside read path
- write + explicit key invalidation
- TTL strategy and stale read handling
- basic protection against cache stampede

Depth checklist:

- explain cache-aside vs write-through
- explain when eventual consistency is acceptable
- explain cache invalidation challenges
- measure and discuss cache hit ratio impact

Interview drill questions:

- How do you avoid stale critical data?
- What happens on cache miss storms?
- Which objects should not be cached?
- How do you roll out cache safely in production?

### Weeks 7-8: DB Performance Depth (`query-tuning-lab-service`)

Build:

- one intentionally slow query endpoint
- baseline with `EXPLAIN ANALYZE`
- add targeted indexes
- compare before/after query metrics

Depth checklist:

- explain index selection and column order
- explain why optimizer may skip indexes
- explain offset pagination vs cursor pagination
- explain read/write index tradeoff

Interview drill questions:

- How do you identify true DB bottlenecks?
- Why did query X still do full table scan?
- When do you denormalize?
- How do you tune for high read, moderate write traffic?

### Weeks 9-10: AI Workflow Depth (`resume-screening-pipeline`)

Build:

- candidate submission API
- rule/score stage
- human review override stage
- final decision + audit record

Depth checklist:

- explain confidence thresholding strategy
- explain human-in-loop design and failure handling
- explain retry/idempotency for async workflow steps
- explain auditability and compliance concerns

Interview drill questions:

- When should AI auto-approve vs escalate?
- How do you prevent duplicate processing?
- How do you test nondeterministic AI pipelines?
- How do you design rollback/review for wrong AI decisions?

## Per-Project Definition of Done

Each project is complete only when:

1. It runs locally with clear startup steps.
2. You have 3-5 API examples (happy + failure path).
3. You can explain 2 architecture tradeoffs.
4. You can answer at least 10 questions without notes.
5. You write one short "incident or scaling note" per project.

## Interview Readiness Rubric (Self-Score)

Score each project from 1-5:

- Requirement clarity
- API and data model quality
- Tradeoff reasoning quality
- Failure mode handling
- Production readiness thinking
- Communication clarity

Target: average score >= 4 before interview loops.

## What To Say In Interviews (Template)

Use this structure:

1. "Here are my assumptions and expected scale."
2. "This is the simplest design that works first."
3. "These are the top bottlenecks and mitigations."
4. "These are consistency and availability tradeoffs."
5. "If scale grows 10x, this is what I would change."

## Suggested Build Order

1. Security
2. Observability
3. Caching
4. DB performance
5. AI workflow

This order gives the best interview ROI quickly while staying aligned with your resume.
