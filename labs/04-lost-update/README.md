# Lab 04: Lost Update In Spring Boot

## Goal

Experience how two concurrent withdrawals can produce an incorrect account balance.

## Build The Broken Version

Create a small Spring Boot app later with:

- `Account` entity with `id` and `balance`
- `withdraw(accountId, amount)` service method
- `@Transactional` on the service method
- Logic that reads the account, subtracts amount, and saves through dirty checking

Run two withdrawals at the same time against the same account.

## What To Observe

Start with:

```text
balance = 1000
withdraw 100
withdraw 100
```

Expected:

```text
800
```

Possible broken result:

```text
900
```

## Explain The Root Cause

Both transactions read the same old value before either commit is visible.

Timeline:

```text
Transaction A reads 1000
Transaction B reads 1000
Transaction A writes 900
Transaction B writes 900
One withdrawal is lost
```

## Fixes To Try

Try these approaches:

1. Optimistic locking with `@Version`
2. Pessimistic write lock with `LockModeType.PESSIMISTIC_WRITE`
3. Atomic database update statement

## Compare

Answer:

- When is optimistic locking enough?
- When should pessimistic locking be preferred?
- Why is money-like state special?
- Where should insufficient balance validation happen?
- Why are mocks not enough for this test?

## Testing Strategy

Use a real database integration test. Coordinate both transactions with latches so both read before either commits.

The test should prove either:

- final balance is correct, or
- one transaction fails and retry behavior is explicit

## Done When

You can explain:

- Lost update timeline
- Transaction isolation impact
- Optimistic vs pessimistic locking
- How to test concurrent database behavior reliably
