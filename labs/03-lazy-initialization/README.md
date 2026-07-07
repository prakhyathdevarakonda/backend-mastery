# Lab 03: LazyInitializationException

## Goal

Experience why accessing a lazy JPA association outside a transaction causes `LazyInitializationException`.

## Build The Broken Version

Create a small Spring Boot app later with:

- `Employee` entity
- `Department` entity
- `Employee` has lazy `department`
- Service method marked `@Transactional`
- Controller calls `employee.getDepartment().getName()` after service returns

For the first version, return the JPA entity directly from the service.

## What To Observe

Look for:

- `LazyInitializationException`
- The exception appearing outside the service transaction
- Different behavior if Open Session in View is enabled

## Explain The Root Cause

The persistence context is active inside the service transaction. After the method returns, the transaction usually closes. A lazy association that was not loaded cannot be fetched later.

Timeline:

```text
Service transaction starts
Employee is loaded
Department is not loaded because it is lazy
Service returns Employee
Transaction/session closes
Controller accesses department name
Hibernate cannot lazy-load it
LazyInitializationException
```

## Fixes To Try

Try these one by one:

1. Access the department inside the transaction.
2. Use a `JOIN FETCH` query.
3. Use `@EntityGraph`.
4. Return a DTO projection from the repository/service.

## Compare

Answer:

- Which fix is quickest?
- Which fix is best for APIs?
- Why can Open Session in View hide performance problems?
- How can DTO projection prevent accidental database access?

## Done When

You can explain:

- Transaction boundary vs controller boundary
- Persistence context lifecycle
- Lazy loading behavior
- Why APIs should usually return DTOs, not entities
