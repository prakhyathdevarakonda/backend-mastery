# Senior Backend Readiness

Senior readiness is not only about knowing fixes. It is about preventing classes of failures, making tradeoffs visible, and helping a team build safer systems.

Use this after completing the first five labs and the mid-level checklist.

## Technical Judgment

- [ ] I can choose a fix based on business risk, not just technical preference.
- [ ] I can explain when correctness matters more than throughput.
- [ ] I can identify hidden coupling between thread pools, database pools, and external services.
- [ ] I can reason about failure modes before production traffic exposes them.
- [ ] I can design APIs that avoid leaking persistence details.

## System Design Thinking

- [ ] I can design a safe withdrawal/payment-like workflow.
- [ ] I can design request context handling without leaking user data.
- [ ] I can design async workflows with timeouts, bulkheads, and backpressure.
- [ ] I can design data access patterns that avoid N+1 queries and lazy loading surprises.
- [ ] I can design idempotent operations for retries.

## Production Debugging

- [ ] I can decide what evidence to collect before proposing a fix.
- [ ] I can use logs, metrics, and traces to narrow down a backend issue.
- [ ] I can recognize thread starvation from symptoms like hanging requests and executor queue growth.
- [ ] I can recognize database contention from lock waits and slow transactions.
- [ ] I can explain a production issue clearly to both engineers and non-engineers.

## Code Review And Prevention

- [ ] I can spot unsafe shared mutable state during code review.
- [ ] I can spot missing `ThreadLocal` cleanup.
- [ ] I can spot risky entity exposure in controller responses.
- [ ] I can spot read-modify-write database logic that needs locking.
- [ ] I can spot blocking calls inside async chains.
- [ ] I can suggest simpler designs that avoid the bug entirely.

## Mentoring Signal

- [ ] I can teach each of the five starting problems to another developer.
- [ ] I can ask guiding questions instead of only giving answers.
- [ ] I can explain tradeoffs without overcomplicating.
- [ ] I can turn a production bug into a reusable team learning note.

## Senior Backend Readiness Signal

You are moving toward senior backend readiness when you can:

- Prevent common backend failure modes through design.
- Select fixes based on correctness, scale, and operational risk.
- Build observability into systems before incidents happen.
- Make concurrency and transaction behavior understandable to others.
- Improve team practices through reviews, docs, and debugging discipline.

## Personal Reflection Prompts

After finishing the five labs, write answers to these:

1. Which issue was hardest for me to reproduce?
2. Which issue was hardest for me to explain clearly?
3. Which fix had the most tradeoffs?
4. Which problem is most likely to happen in my current work?
5. What would I now catch in code review that I might have missed earlier?
6. What should I learn next to move closer to senior backend readiness?
