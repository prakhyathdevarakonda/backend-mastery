# Screening Workflow Service

Sample runnable project to practice resume-aligned backend concepts:

- REST API design
- event-driven architecture with Kafka
- async processing and eventual consistency
- human-in-the-loop decision flow
- idempotent consumers
- correlation-id propagation

## Architecture

```
POST /api/screenings
       |
       v
  screening.requests (Kafka topic)
       |
       v
  AI worker listener -> score resume -> screening.results
       |
       v
  result listener updates in-memory state

If score is borderline:
POST /api/screenings/{id}/decision
       |
       v
  screening.decisions -> decision listener -> screening.results
```

## Topics

- `screening.requests`
- `screening.results`
- `screening.decisions`

## Run with host Maven + Docker Kafka

From `projects`:

```bash
docker compose up -d kafka
cd screening-workflow-service
mvn spring-boot:run
```

Service runs on `http://localhost:8082`.

## Run full stack in Docker

From `projects`:

```bash
docker compose up --build screening-workflow-service
```

## API examples

Create screening request:

```bash
curl -X POST http://localhost:8082/api/screenings \
  -H 'Content-Type: application/json' \
  -H 'X-Correlation-Id: demo-123' \
  -d '{
    "candidateName":"Alex",
    "email":"alex@example.com",
    "resumeText":"Spring Boot Kafka microservice testing and distributed tracing experience ..."
  }'
```

Check status:

```bash
curl http://localhost:8082/api/screenings/<screeningId>
```

If status is `HUMAN_REVIEW_REQUIRED`, submit decision:

```bash
curl -X POST http://localhost:8082/api/screenings/<screeningId>/decision \
  -H 'Content-Type: application/json' \
  -d '{"decision":"APPROVE","reason":"Strong practical backend examples"}'
```

## Interview topics this demonstrates

- Sync API to async Kafka boundary
- At-least-once processing and idempotency
- Event contracts between producer and consumers
- Human override flow in automated systems
- Tradeoff: in-memory state vs durable DB
