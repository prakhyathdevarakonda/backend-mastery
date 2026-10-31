# Project 04: Cache-Aside Product Catalog

## Primary Concepts

- cache-aside pattern
- cache invalidation
- stale reads and TTL tradeoffs
- read-heavy service design

## Functional Requirements

- Read product by ID
- Update product details
- Search simple product list (optional)

## Non-Functional Requirements

- Fast read latency for hot products
- Eventual consistency acceptable for reads
- Controlled DB load

## Suggested API

- `GET /products/{id}`
- `PUT /products/{id}`
- `GET /products?category=...`

## Suggested Flow (Cache-Aside)

1. Try cache
2. On miss, fetch from DB and populate cache
3. On update, write DB first then invalidate cache key

## Design Prompts

- TTL-only invalidation vs explicit invalidation?
- What causes cache stampede and how to prevent it?
- What fields should never be cached?

## Scaling Discussion

- Use Redis for distributed cache
- Add jitter to TTL to avoid synchronized expirations
- Preload hot items if traffic patterns are predictable

## Interview Questions

- Why is cache invalidation hard?
- Write-through vs cache-aside differences?
- What metrics prove cache effectiveness?
