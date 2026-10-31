# Project 01: URL Shortener Fundamentals

## Primary Concepts

- API design basics
- key-value style data access
- unique ID generation
- read-heavy scaling

## Functional Requirements

- Create short URL from long URL
- Redirect short code to long URL
- Optional expiration time for short URL
- Track basic click count

## Non-Functional Requirements

- Low redirect latency
- High read throughput
- No duplicate short codes

## Suggested API

- `POST /shorten`
- `GET /{shortCode}`
- `GET /stats/{shortCode}`

## Suggested Data Model

- `short_code` (unique)
- `original_url`
- `created_at`
- `expires_at` (nullable)
- `click_count`

## Design Prompts

- How do you generate collision-resistant short codes?
- When do you choose random code vs sequence + encoding?
- When traffic grows, where do cache and CDN help?

## Scaling Discussion

- Start with single DB instance and unique index
- Add caching for hot short codes
- Discuss sharding strategy by short code hash

## Interview Questions

- How do you prevent abuse/spam URLs?
- What consistency is needed for click counts?
- How would you migrate to multi-region redirects?
