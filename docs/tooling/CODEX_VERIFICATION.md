# Codex verification

Invoke the repository skill from a fresh Codex session:

```text
$verify-loop Dry-run the phase-3 target and report the cost estimate.
```

Or run its programmatic entry point:

```bash
scripts/verify --target phase-3 --dry-run
scripts/verify --target phase-3 --preset lean --review-only --max-rounds 1
scripts/verify --target phase-3 --fixup-review latest --dry-run
scripts/verify --target phase-3 --fixup-review latest
```

The first command is zero-agent validation. It discovers the Git root and prior reviews, resolves
content selectors, rejects missing/ambiguous/conflicting/outside-repository inputs and existing
outputs, prints role write allowlists, and estimates calls/tokens. Run it before every paid round.

The second command performs one paid review round and stops before fix-up. If it returns
`PASS-WITH-CORRECTIONS`, the third command validates a one-agent continuation estimate and the
fourth runs only the pending review's narrowly scoped fix-up. Do that before starting the next
review. A full lean round assumes
3 independent finders, 2 refuters per candidate, a conditional Gate, one adjudicator, and a
conditional fix-up. Thorough uses 5 finders, 3 refuters, and steelmen. Every agent is a fresh Codex
session; concurrency is bounded inside each stage and never crosses a stage barrier. Do not start a
costly multi-round run until its dry-run estimate has been surfaced and authorized.

The generic manifest is documented in
`.agents/skills/verify-loop/references/OPERATIONS.md`. Current targets are `phase-1`, `phase-2`,
`phase-3`, and the test-only `non-phase-fixture`. New targets require data/configuration, not engine
or prompt changes.

Real runs checkpoint below `.verification-runs/` (gitignored), and failures report the exact
journal path/stage. If a role fails, inspect the journal,
`git status --short`, and the diff before retrying: a partial writer may have changed its allowed
files before returning an error. Existing review evidence is immutable; the one pending review may
receive only an append-only `## Resolutions` section through `--fixup-review`.
