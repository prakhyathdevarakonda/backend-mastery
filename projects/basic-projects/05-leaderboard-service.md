# Project 05: Leaderboard Service

## Primary Concepts

- read-heavy architecture
- ranking queries
- data partitioning and hot key handling
- eventual consistency for score updates

## Functional Requirements

- Submit score for user
- Get top N users
- Get rank of a user
- Optional seasonal leaderboard reset

## Non-Functional Requirements

- Fast reads for top N queries
- High write throughput for score updates
- Predictable ranking behavior under concurrent updates

## Suggested API

- `POST /scores`
- `GET /leaderboard?limit=100`
- `GET /leaderboard/rank/{userId}`

## Storage Options

- SQL with indexed score table (simple start)
- Redis sorted sets for fast rank/top-N
- Hybrid: Redis for real-time, DB for durable history

## Design Prompts

- How often to persist Redis data to DB?
- How to prevent leaderboard tampering?
- How to support multiple games/tenants?

## Scaling Discussion

- Partition by game or region
- Fan-out read replicas for query traffic
- Handle hot leaderboards with local caching

## Interview Questions

- Why choose sorted set over relational query?
- How do you keep leaderboard eventually consistent?
- How do you run season reset without downtime?
