# 2026-11-01: Single plan + doc cleanup

**Repos / commits:** backend-mastery `6b66678` · questionnaire-patterns `5eb5f45`

## Why
Both repos were created right after interviews by vibe coding. Studying from them felt unclear: four overlapping plans (`05-learning-roadmap.md`, `INTERVIEW-READINESS-SPEC.md`, `RESUME-DEPTH-TRACK.md`, the study map), code further along than understanding, and nothing linking a question to a file.

## What changed
- **Study map** (outside git) became the single plan: added a "Start here every day" section, a repo map, a week-by-week plan, a **Lab** line per topic pointing to exact files, and a lab checklist marked ✅/◐/🔨 using the existing repos instead of the spec's proposed flight-booking project.
- **backend-mastery:** deleted 12 superseded docs (old roadmaps, `STEERING.md`, `00-start-here.md`, `02-big-picture-map.md`, `06-self-introspection.md`, `reflections/`, `specs/`). Untracked 61 committed `target/` build files (already in `.gitignore`). README rewritten as a path → week table. Fixed links to deleted files.
- Kept the best idea from `02-big-picture-map.md` in the study map: the six questions to ask of every feature (API contract, transaction boundary, DB access, concurrency, fallback, observability).
- **questionnaire-patterns:** added a README with run steps and a 9-step reading order.

## Verified
`grep` for links to deleted files: none left. No code changed.

## Decisions & learnings
- Repos are labs; the study map is the only tracker. Tick a question only when you can explain it aloud in 2 minutes.
- Code isn't the problem (~2,500 lines total); doc sprawl was.
- "Owning" vibe-coded code = read → break → rebuild from memory → explain every line.

## Next
Reorder the tracker for a clearer flow; local setup docs.
