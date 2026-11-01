# Current context (paste this into a new chat)

_Last updated: 2026-11-02_

## Goal

Java full-stack interview readiness with **strong fundamentals**, starting from zero. I learn by working on real code: read → break → rebuild from memory → explain aloud in 2 minutes.

## The three pieces

| What | Where | Role |
| --- | --- | --- |
| **Study map & tracker** | `~/workspace/Java Full Stack – Study Map & Question Tracker.md` (outside git) | The **only** plan and tracker. Organised **by week**: each week holds its files to open, questions, lab checklist and "done when" |
| **backend-mastery** | `github.com/prakhyathdevarakonda/backend-mastery` | Labs + notes: `labs/01–05`, `01-five-problems.md`, `projects/` (wallet, ledger, screening services), `revision-sheets/`, this log |
| **questionnaire-patterns** | `github.com/prakhyathdevarakonda/questionnaire-patterns` | Spring Boot + MongoDB. Abstract Factory, Factory, Builder, Strategy, Template Method, immutability. Week 2 lab |

## Week plan (summary)

0 Setup · 1 Core Java + Concurrency · 2 Spring core + Patterns · 3 Transactions/JPA + REST · Tier 1 buffer · 4 Databases · 5 Kafka · 6 Security + AOP · 7 JVM + Microservices/resilience · 8+ System design, Cloud, React, Behavioral · DSA every morning.

**Current week:** Week 0 (setup verified working, see the 2026-11-02 entry).

## Local setup (verified 2026-11-02)

- `backend-mastery/projects/README.md`: one guide for wallet (:8080), ledger (no HTTP, Kafka consumer), screening (:8082), Postgres :5432, Kafka :9094, Adminer :8090, Kafka UI :8085. `docker compose up -d` = infra only; `--profile apps` = services in Docker; `--profile tools` = UIs. `./seed.sh` loads sample data.
- `questionnaire-patterns/README.md`: Mongo :27017, app :8083, mongo-express :8091, `./seed.sh`.
- New machine: only Git + Docker + jq needed (quick start + per-OS table in `projects/README.md`; Windows via WSL 2).
- Docker installed. **Maven not installed** (`brew install maven` needed for `mvn spring-boot:run` / `mvn test`); services can run via Docker meanwhile.

## Conventions / decisions

- No new projects: the existing repos are the lab (the spec's "flight-booking" project was dropped).
- Keep docs minimal; don't add competing plans. The old roadmaps were deleted on 2026-11-01 (still in git history).
- Markers in tracker: ★ asked 2+ times · ✅ code exists (own it) · ◐ partial · 🔨 build it yourself.
- Each push gets a `progress-log/` entry; commits go straight to `main` (personal repos).

## Open items

- Install Maven.
- The tracker file isn't in git (no backup/history). Consider moving it into this repo.
- Week 1 next: labs 01, 02, 05 + Core Java / Concurrency ★ questions.
