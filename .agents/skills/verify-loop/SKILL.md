---
name: verify-loop
description: Operate or inspect this repository's top-level generic adversarial verification loop, target manifests, dry runs, review-only runs, fix-up continuation, convergence, or new verification profiles.
---

# Verification loop

Use the canonical orchestrator at `scripts/verify` from the repository root. Do not recreate its
stage logic in chat and do not copy prompts or schemas into another executable.

If the current prompt says the canonical engine already dispatched you as an internal Attack,
Refute, Steelman, Gate, Adjudicate, or Fix-up role, **do not invoke this skill or `scripts/verify`**.
Complete only that atomic role. Do not delegate to subagents or start another omp session.

## Safe invocation

1. Read the root `AGENTS.md`, then inspect `git status --short` and the current diff. Preserve
   pre-existing changes.
2. Resolve the requested target by ID or manifest path. If none is named, list
   `verification/targets/*.json` and ask only when the user's intent cannot be inferred safely.
3. Run a zero-agent preflight first:

   ```bash
   scripts/verify --target <id> --preset lean --dry-run
   ```

   Design-governed targets resolve their revision from the target document's §0 declaration by
   default. Use `--design-version <label>` only for an explicit verification-only override.

4. Report the resolved next round, selected lenses, write allowlists, maximum concurrency, and
   estimated agent/input/output tokens before any paid run. For a design-governed target, also
   report the resolved design version, path, and whether it came from §0 or an override.
5. Default the first paid run on a target to one review-only round unless the user explicitly
   authorizes fix-up or unattended multi-round work:

   ```bash
   scripts/verify --target <id> --preset lean --review-only --max-rounds 1
   ```

6. A non-review-only or multi-round run may be expensive. Do not start it until its dry-run
   estimate has been surfaced and the user's request authorizes that scale.
7. If review-only writes a `PASS-WITH-CORRECTIONS` review, preflight and then authorize its scoped
   continuation before starting another review:

   ```bash
   scripts/verify --target <id> --fixup-review latest --dry-run
   scripts/verify --target <id> --fixup-review latest
   ```

The CLI discovers the Git root dynamically. Never embed an absolute checkout path. Live runs
execute under Bun against the pinned `@oh-my-pi/pi-coding-agent` SDK (17.2.11); the shim prefers
`bun` and falls back to `node` for dry runs. Set `OMP_SDK_PATH` to a package checkout when the
global install is not the intended one.

Independent targets may run concurrently. Their read-only stages overlap; journal commits and
Adjudicator/Fix-up snapshot windows queue briefly behind the repository-local mutation lease. Do
not start two paid runs for the same target: the second invocation fails with `RUN_CONFLICT` and
identifies the owning PID and journal rather than operating on stale round state.

## Running it where the operator can see it

A paid run is hours long. **Never start one with a captured shell call** — the `bash` tool returns
nothing until the process exits, so the whole run collapses into one spinner. Start it as a
supervised process and follow its output:

```text
hub start name=verify-<id> application=scripts/verify \
  args=[--target, <id>, --preset, lean, --review-only, --max-rounds, 1]
hub logs  name=verify-<id> follow=true
```

Report what the stream says as it arrives; do not wait for the final JSON to say anything. The
loop emits stage entry and completion, each role session's start and finish with its duration, and
a heartbeat naming the sessions still in flight:

```text
r34 Attack: entering - 7 session(s), concurrency 4
r34 Attack: still running 18m07s - 3 in flight (edge-cases, consistency, evidence), 4/7 done
r34 Attack: complete in 21m33s - 9 candidate(s) after dedupe from 7 lens(es); next Refute
```

Progress goes to stderr; the JSON result is alone on stdout. The same lines are appended to a
timestamped `.log` beside the run journal — the path is the first line a run prints — so the run is
also tailable from a separate terminal and readable afterwards. `--progress json` switches stderr
to one structured event per line, `--heartbeat-ms N` retunes the heartbeat (`0` disables it), and
`--quiet` silences stderr without disabling the log.

## Contract that must not be bypassed

- Stage order is Attack → Refute → optional Steelman → Gate → Adjudicate → Fix-up.
- Every stage is a full barrier. Parallelism exists only among independent calls inside a stage.
- Every role is one fresh, isolated omp agent session with a full system-prompt override, an
  in-memory session manager, and no skills/context-files/MCP/LSP ambient context.
- Finder, refuter, steelman, and Gate sessions get only read/grep/glob tools; a pre-execution
  hook bars forbidden-source patterns and deny-listed prior reviews before any read happens.
- Adjudicator and fix-up sessions add write/edit tools behind a pre-execution allowlist hook;
  no role session has a shell or any indirect write route. After each writer stage the engine
  compares worktree hashes against the role allowlist and rechecks immutable evidence.
- Target and mutation leases under `.verification-runs/.locks/` coordinate parallel processes
  without excluding journals or other ignored files from the writer snapshot.
- Gate coverage is total and fail-closed. The engine also resolves every admitted citation
  deterministically from repo-relative path + inclusive lines + verbatim quote.
- Structured results arrive through the `yield` tool — one terminal call, or incremental
  `type: ["section"]` submissions the runner assembles at finalization — validated against the
  role's schema in strict mode; the engine re-validates the payload against the same schema file.
- A read-only role whose session fails with `AGENT_ERROR` is retried once with a fresh session,
  and the retry is recorded in the journal's `role_retries`. Writer roles never auto-retry.
- A schema-valid adjudication payload the engine rejects as self-contradictory gets exactly one
  bounded correction dispatch, quoting the rejection and the rejected payload, under the same
  write allowlist. That is a correction, not a session retry: the session succeeded, and a second
  contradictory payload ends the round.
- PASS is literal: verdict `PASS`, zero blocking findings, and zero corrections. Notes do not block.
- FAIL stops for a human-authorized rebuild. Review-only stops before fix-up. A cap or partial
  failure is reported honestly and never softened to PASS.
- Prior reviews of the target are barred to independent readers and read by the adjudicator last.
- Forbidden sources are enforced from manifest patterns plus provenance. A transcript remains
  forbidden even at an unlisted path.
- A first review is discovered from the review directory, not guessed from a requested round.
- Existing or ambiguous review output, missing inputs, path escape, symlink escape, selector drift,
  review gaps, and conflicting write/immutable rules fail before agents run.

## Adding a target

Create data, not engine branches:

1. Add `verification/targets/<id>.json` against
   `.agents/skills/verify-loop/schemas/manifest.schema.json`.
2. Reuse an existing lens set under `verification/lenses/` or add a new data-only lens set against
   `lens-set.schema.json`.
3. Use content selectors with a unique start anchor and the default unique end anchor. When a
   repeated terminator is intentional, declare `end_mode: "first-after-start"`. Line coordinates
   are optional reporting assertions and, when present, are checked at startup.
4. Run `scripts/verify --target <id> --dry-run` and the repository tests.

Do not edit shared prompt templates or engine code merely to add a phase, document type, code
artifact, or other target.

## Recovery

Real runs write an ignored JSON checkpoint below the manifest's `journal_directory`. If an agent,
schema, write-scope, immutable-evidence, or Gate check fails, inspect that journal and
`git status --short` before retrying. Never assume a partial writer failed before changing files.
Use `--fixup-review latest` for a correction-bearing review that has no Resolutions. Resume with the
next discovered round only after that fix-up completes and its changes are explicitly unreviewed.

See `docs/tooling/OMP_VERIFICATION.md` for arguments, target fields, cost formulas, tests, and the
canonical source map.
