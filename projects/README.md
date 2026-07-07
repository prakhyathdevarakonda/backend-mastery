# Projects Overview

This folder contains runnable backend services for practical learning.

## Services

### 1) wallet-service

- owns wallet API and wallet database updates
- publishes `wallet.transactions` Kafka events after successful DB commit
- path: `projects/wallet-service`

### 2) ledger-service

- consumes `wallet.transactions` events
- uses in-memory idempotency set to ignore duplicate event ids
- path: `projects/ledger-service`

## Run Modes

### Mode A: Fast local mode

- run Postgres only via `wallet-service/docker-compose.yml`
- run services with Maven on host machine

### Mode B: Full multi-service Docker mode

From `projects`:

```bash
docker compose up --build
```

This starts:

- PostgreSQL
- Kafka (KRaft mode)
- wallet-service
- ledger-service

## Why No Shared Module

Event class is duplicated across services on purpose.

Reason:

- simpler setup
- fewer Maven/module concepts to manage initially
- easier for practice and interviews
