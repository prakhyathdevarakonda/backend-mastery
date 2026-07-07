# 04 - Spring Security and AOP Essentials

This document gives the exact supporting knowledge interviewers often ask after concurrency/transaction questions.

## Spring Security: What You Must Know

### Request Flow (high level)

1. request enters security filter chain
2. authentication is resolved
3. `SecurityContextHolder` is set for current thread
4. controller/service runs
5. security context is cleared after request

Why this matters:

- explains how per-request user context avoids leaking across pooled threads

### Core Concepts

- `SecurityFilterChain`
- `Authentication` and `Principal`
- `SecurityContextHolder`
- stateless auth (JWT) vs session auth
- endpoint authorization (`hasRole`, `hasAuthority`)

### Security Questions Linked to Your 5 Problems

- ThreadLocal leak question -> explain context cleanup in security chain
- Async question -> explain context propagation if async executors are used
- API design question -> explain authn and authz boundaries

## Spring AOP: What You Must Know

### What AOP Is

AOP handles cross-cutting concerns without duplicating code in every method.

Typical concerns:

- logging
- metrics/timing
- auditing
- transaction boundaries (via proxies)
- retry wrappers

### Core Terms

- Advice (`@Before`, `@After`, `@Around`)
- Pointcut (where advice applies)
- Join point (the method call)
- Proxy-based weaving in Spring

### AOP and Interview Problems

- `@Transactional` itself is AOP/proxy based
- self-invocation trap: transactional advice may not apply for internal method calls
- use AOP for logging/timing, but not to hide business logic

## Practical Mini Checklist

For any backend feature:

1. What data needs authentication?
2. What authorization rule protects each endpoint?
3. What should be logged for audit?
4. Which cross-cutting concerns belong in AOP?
5. Are transaction boundaries applied through proxies correctly?

## Common Interview Follow-ups

- "How does Spring Security avoid user context leak?"
- "How does `@Transactional` work internally?"
- "When would you use `@Around` advice?"
- "What are risks of too much AOP?"

Short answer:

- Security: filter chain + context lifecycle
- Transaction: proxy wraps method and opens/commits/rolls back
- AOP usage: keep it for cross-cutting, keep business rules explicit in services
