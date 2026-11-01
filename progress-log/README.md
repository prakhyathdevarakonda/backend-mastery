# Progress log

A record of every push to `backend-mastery` and `questionnaire-patterns`: what changed, why, and what was verified.

## Starting a new chat (with Claude or anyone else)

Paste **`CONTEXT.md`** plus the **latest entry**. That is enough context to continue. No need to re-explain the repos.

## Rule: one entry per push

After each push, add `YYYY-MM-DD-short-title.md` here and update `CONTEXT.md` if the current state changed. A push only to `questionnaire-patterns` is logged here too (commit the log in this repo).

## Entry template

```markdown
# YYYY-MM-DD: Short title

**Repos / commits:** backend-mastery `abc1234` · questionnaire-patterns `def5678`

## Why
What problem or question triggered this.

## What changed
- Bullet per meaningful change (file → what and why)

## Verified
How it was checked (commands run, results). Say "not run" if it wasn't.

## Decisions & learnings
Choices made and the reasoning, so they aren't re-litigated later.

## Next
What to do next / open items.
```

## Entries

| Date | Entry |
| --- | --- |
| 2026-11-01 | [Single plan + doc cleanup](2026-11-01-single-plan-and-cleanup.md) |
| 2026-11-02 | [Tracker by week + local setup + seed data](2026-11-02-local-setup-and-seed-data.md) |
| 2026-11-02 | [Shareable setup (Mac, Linux, Windows)](2026-11-02-shareable-setup.md) |
