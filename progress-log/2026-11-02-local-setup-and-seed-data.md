# 2026-11-02: Tracker by week + local setup + seed data

**Repos / commits:** both repos, commit "Add local setup guide, seed data and progress log" (see `git log`)

## Why
1. The tracker's content was good but the flow was confusing: topics were numbered 1–15 while the weeks visited them in a different order (4, 5 → 1, 6 → 2, 3 …), and lab tasks sat in a separate block at the end.
2. There was no clear way to run several services locally, load sample data, or see the data.

## What changed
- **Study map** (outside git), restructured top to bottom: Today → Plan at a glance → How to study → Week 0…8+ (each with *Open these* / questions / lab checklist / *Done when*) → DSA → Reference. Questions moved verbatim (337 checkboxes before and after). Area numbers dropped; resilience questions moved from Kafka (Week 5) to Week 7.
- **`backend-mastery/projects/README.md`** rewritten as the single local-setup guide: ports table, install, start infra, run services, `./seed.sh`, API tables, see data (psql / Adminer, Kafka CLI / Kafka UI), stop/reset, troubleshooting, design notes.
- **`projects/docker-compose.yml`**: profiles. Default = postgres + kafka; `apps` = the three services; `tools` = Adminer (:8090) + Kafka UI (:8085). Removed ledger's meaningless 8081 port mapping (ledger has no web server).
- Deleted `wallet-service/docker-compose.yml` (a second Postgres on the same port 5432).
- wallet / ledger / screening / postgres-notes READMEs: their own run sections now point to the one guide.
- **`projects/seed.sh`** (new): 2 wallets with deposits/withdrawals (incl. `FOR UPDATE` path), 2 expected failures, 3 resumes (approved / human review / rejected) + a recruiter decision.
- **questionnaire-patterns:** app port 8080 → **8083** (clashed with wallet); `docker-compose.yml` gained a `tools` profile with mongo-express (:8091); **`seed.sh`** (new) creates one questionnaire per family (incl. Yes/No branching) and triggers 4 expected rule failures; README gained setup, API reference, mongosh queries, and a "no Maven? run in the Maven Docker image" command.
- **progress-log/** (this folder) created.

## Verified (actually run, Docker 29.9 on macOS)
- Infra healthy in ~10 s; Adminer and Kafka UI return 200; services built and ran via `--profile apps`.
- `seed.sh` (backend): balances 379.50 / 750.00; 409 + 400 failures; screenings scored 95 / 50 / 30; decision → APPROVED.
- Postgres: 2 wallets (`version` = 2), 4 transactions. Kafka: 4 messages keyed by walletId; `ledger-service` group LAG 0.
- questionnaire app (via Maven container) + `seed.sh`: 3 published questionnaires, all 4 rule failures fire with the right message; mongosh shows `version`, 5 `_class` subclasses, `COLLSCAN`.

## Bugs found while verifying (fixed)
- README Kafka shortcut `K="docker exec …"; $K/…` fails in **zsh** (no word splitting) → replaced with a `kafka() {…}` function.
- Questionnaire seed's "publish without KNOCKOUT" hit the empty-questionnaire rule first → added a valid question before it.

## Decisions & learnings
- `docker compose up -d`: `-d` = detached (runs in the background). An image (`mongo:7`) is a template; a container (`questionnaire-mongo`) is a running instance.
- Events show `occurredAt` as epoch seconds (Jackson default for `Instant`). Fine for now.
- Linux: works with small differences (install via apt/dnf instead of brew; `host.docker.internal` needs `--add-host=host.docker.internal:host-gateway` on plain Docker Engine; install `jq`).

## Next
Install Maven → Week 1.
