# Spring Security and AOP - Quick Revision

## Spring Security

### Request Flow

Simple flow:

1. HTTP request enters filter chain
2. authentication is resolved
3. `SecurityContextHolder` stores authentication for current thread
4. controller/service runs
5. context is cleared after request

Why it matters:

- links directly to ThreadLocal leak question
- explains how Spring avoids stale user context in pooled threads

Follow-ups:

- What is `SecurityFilterChain`?
- What is `Authentication`?
- What is `SecurityContextHolder`?
- Is `SecurityContextHolder` backed by ThreadLocal?
- What happens in async execution?
- How do JWT and session authentication differ?
- Difference between authentication and authorization?

Internal concepts:

- `SecurityContextHolder` default strategy is thread-local.
- Security context is set/cleared in filter chain around request handling.

### Short Answer

Spring Security uses a filter chain. It stores authenticated user details in `SecurityContextHolder` for the current request and clears it after request completion, preventing user context leakage across reused threads.

## Spring AOP

### Core Idea

AOP handles cross-cutting concerns:

- transactions
- logging
- metrics
- auditing
- retries

Spring commonly applies AOP through proxies.

### `@Transactional`

What to say:

- `@Transactional` is applied by Spring proxy.
- Proxy opens transaction before method.
- It commits on success and rolls back on runtime exception by default.

Follow-ups:

- Why does self-invocation break `@Transactional`?
- What is proxy object?
- Difference between JDK dynamic proxy and CGLIB?
- When does transaction start and end?
- What exceptions cause rollback?
- What is propagation?
- What is isolation?

Internal concepts:

- `@Transactional` is applied by a proxy, not by direct method bytecode changes.
- Self-invocation bypasses proxy advice.

### AOP Risks

Good use:

- logging
- timing
- audit
- transaction boundaries

Bad use:

- hiding important business rules
- making flow hard to understand
- applying broad pointcuts accidentally

## Interview Answer Formula

For Spring internals:

1. name the Spring mechanism
2. explain lifecycle/proxy/filter flow
3. connect to bug
4. mention limitation
5. give safe practice
