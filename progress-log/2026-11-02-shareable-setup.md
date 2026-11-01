# 2026-11-02: Shareable setup (Mac, Linux, Windows)

**Repos / commits:** backend-mastery `3e255af` · questionnaire-patterns `cbb0cd5`

## Why
Question: "If I share these services, what should someone do to get the same setup on their laptop?" The guide assumed a Mac with Homebrew.

## What changed
- `projects/README.md`: new **Quick start on a new machine** (clone both repos → compose up → `--profile apps --build` → seed → check URLs, plus the ports that must be free). "One-time install" is now a per-OS table (Mac / Linux / Windows via WSL 2); Java + Maven are marked optional.
- `.gitattributes` (both repos): `*.sh text eol=lf`, so `seed.sh` doesn't break with `$'\r': command not found` when cloned on Windows.
- questionnaire README: the no-Maven `docker run` gained `--add-host=host.docker.internal:host-gateway` (needed on plain Linux Docker Engine); install line points to the per-OS table.

## Verified
- The `docker run … --add-host=…` command started the app on macOS (API returned 200).
- `git check-attr eol projects/seed.sh` → `lf`.
- Linux/Windows not run here; based on Docker's documented behaviour.

## Decisions & learnings
- Minimum for a newcomer: **Git + Docker + jq**. The services build inside Docker, so no Java needed to run them.
- The study map is still outside git, so people you share with don't get it.

## Next
Install Maven → Week 1. Decide whether to move the study map into this repo.
