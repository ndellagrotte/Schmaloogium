# omp verification operations

The verification loop runs on supported omp surfaces (pinned `@oh-my-pi/pi-coding-agent`
**17.2.11**):

- repository guidance: root `AGENTS.md`;
- reusable workflow discovery: this repository-scoped skill under `.agents/skills/`;
- role execution: one isolated `createAgentSession` SDK session per role (`src/sdk.ts` in the
  installed package), never a forked or shared conversation;
- structured results: per-session `outputSchema` with `outputSchemaMode: "strict"` and the
  `yield` tool — one terminal call, or incremental `type: ["section"]` submissions for oversized
  results, which the runner assembles at finalization — re-validated by the engine against the
  same schema file. A read-only role that fails with `AGENT_ERROR` is retried once with a fresh
  session (recorded in the journal's `role_retries`); writer roles never auto-retry;
- least-privilege execution: `toolNames` + `restrictToolNames` tool allowlists — read-only roles
  get `read`/`grep`/`glob`, writer roles add `write`/`edit`, and no role session ever has a shell,
  `eval`, `task`, MCP, or LSP route;
- deterministic path enforcement: an inline `tool_call` hook blocks forbidden/deny-listed reads
  and out-of-allowlist writes *before* the tool executes, with the legacy post-stage worktree-hash
  and immutable-evidence checks kept as the backstop.

The engine uses direct, isolated SDK sessions because each role needs a different schema and tool
surface plus deterministic barriers and aggregation; adding a custom-agent definition layer would
duplicate the canonical prompts. Role sessions carry a full system-prompt override, `skills: []`,
`contextFiles: []`, and `SessionManager.inMemory()`: they see exactly what the engine hands them
and leave no resumable state. (Decision: role sessions do *not* receive the repo's `AGENTS.md`;
the legacy prompts were self-contained and the engine transmits every rule a role needs.)

## Runtime requirements

- **Bun** for live runs — the SDK's published entry point is TypeScript (`src/sdk.ts`), which
  Node cannot import. The `scripts/verify` shim prefers `bun` and falls back to `node`; dry runs,
  resolution, and the test suite work under either.
- The SDK is resolved, in order: `OMP_SDK_PATH` (package directory or `sdk.ts` path), a bare
  `@oh-my-pi/pi-coding-agent` import resolvable from the engine, then the package behind the
  `omp` binary on `PATH`. A version differing from the pinned 17.2.11 is reported on stderr and
  in no case silently re-pinned.
- omp provider credentials must already be configured (`omp` itself must be able to run a
  session). Role sessions are headless (`hasUI: false`); a tool that would need interaction is
  absent rather than stalled.

## Entry points

From a fresh omp session:

```text
$verify-loop Dry-run the phase-3 target and report the estimate.
```

From a shell:

```bash
scripts/verify --target phase-3 --dry-run
scripts/verify --target phase-3 --design-version v3 --dry-run
scripts/verify --target phase-3 --preset lean --review-only --max-rounds 1
scripts/verify --target phase-3 --fixup-review latest --dry-run
scripts/verify --target phase-3 --fixup-review latest
scripts/verify --target phase-3 --preset thorough --max-rounds 6
```

`scripts/verify` is only a launcher. The single implementation is
`.agents/skills/verify-loop/scripts/verify.mjs` plus `engine.mjs`; the SDK adapter is
`omp-agent-runner.mjs` beside them and is the only file that imports omp.

## Arguments

| Argument | Meaning |
|---|---|
| `--target <id\|path>` | Required target ID or repo-relative manifest path |
| `--design-version VERSION` | Verification-only design directory override; otherwise use the target document's §0 declaration |
| `--preset lean\|thorough` | `lean` (the CLI default): 3 finders, 2 refuters, no steelman, concurrency 6; `thorough`: 5, 3, steelman, concurrency 8. These two are the whole set — there is no preset named `default`. A preset's finder count must fit the target's lens orders, so `thorough` needs 5 lens ids in both `first_review` and `mature`; a short order fails at startup |
| `--start-round N` | Optional assertion; must equal the unique next discovered round |
| `--max-rounds N` | Cap, default 6; `0` resolves with zero agents |
| `--review-only` | One adjudicated review, no fix-up |
| `--fixup-review latest\|PATH` | Continue only the latest unresolved correction-bearing review |
| `--dry-run` / `--estimate` | Full parse/path/selector/state/write-plan validation, zero agents |
| `--model MODEL` | Optional model pattern for all role sessions |
| `--agent-timeout-ms N` | Per-session timeout, default 30 minutes |
| `--json` | Compact result JSON |
| `--progress human\|json\|none` | Live stderr progress; `human` (default) renders lines, `json` emits one event per line |
| `--heartbeat-ms N` | Heartbeat interval while a stage is in flight, default 30000; `0` disables |
| `--quiet` | Alias for `--progress none` |

Headless role sessions need no approval flow: the tool allowlist makes every available action
pre-authorized by construction, and the hook denies everything else deterministically.

## Watching a run

A paid round is hours of long isolated model sessions behind stage barriers, so the loop reports
continuously rather than only at exit. Every stage announces entry and completion, every role
session announces its start and its finish with a duration, and a heartbeat names what is still in
flight:

```text
r34 Attack: entering - 7 session(s), concurrency 4
r34 Attack: start contract [0/7 done, 1 in flight]
r34 Attack: still running 18m07s - 3 in flight (edge-cases, consistency, evidence), 4/7 done
r34 Attack: done contract in 4m21s - 3 finding(s) [5/7 done, 2 in flight]
r34 Attack: complete in 21m33s - 9 candidate(s) after dedupe from 7 lens(es); next Refute
```

Two surfaces carry it:

- **stderr**, immediately, with wall clock and elapsed prefixes (`[14:22:05 +21m33s]`). The JSON
  result stays alone on stdout, so `scripts/verify … > result.json` still works.
- **a progress log** beside the run journal — same basename, `.log` extension, one ISO-timestamped
  record per line — so a run is tailable from another terminal and readable after the fact. Its
  path is the first thing a run prints. Appends take the workspace mutation lease, exactly like
  journal commits, so they never register inside another target's writer window.

**Do not launch a paid run through a captured shell call.** A harness `bash` tool buffers output
until the process exits, which turns hours of reporting into one spinner. Run it as a supervised
process and follow it, or tail the log:

```text
hub start  name=verify-phase-7 application=scripts/verify args=[--target, phase-7, --preset, lean, --review-only, --max-rounds, 1]
hub logs   name=verify-phase-7 follow=true
```

```bash
tail -f .verification-runs/phase-7/phase-7-*.log
```

`--progress json` emits the same events as structured JSON (`kind`, `round`, `stage`, `role`, `ms`,
`at`) for anything that wants to consume the stream; `--quiet` silences stderr while still writing
the log.

## Cost and concurrency

The dry-run reports an upper estimate from the configured assumed candidates per finder.
For `F` finders, `R` refuters, and assumed total candidates `C`:

```text
Attack F + Refute C×R + conditional Refute-correction C×R + optional Steelman C
         + conditional Gate 1 + Adjudicate 1 + conditional Adjudicate-correction 1
         + conditional Fix-up 1
```

Actual calls fall when finders return fewer/deduplicated candidates, refuters eliminate candidates,
refuters return valid citations without their one allowed correction attempt, the adjudicator's
first payload validates, steelman is disabled, Gate has no survivors, PASS/FAIL stops, or
review-only omits fix-up. Each role is a fresh omp
session and therefore consumes its own tokens. Parallelism is capped per preset and never crosses a
stage barrier. Use `--model` with a cheaper pattern for finder/refuter-scale experiments; the
estimate math itself is model-agnostic, so price a run from your provider's per-model rates.

Different targets may also run in parallel, as permitted by the governing execution waves. Attack,
Refute, Steelman, and Gate remain concurrent across those runs. Repository mutations are narrower:
atomic leases below `.verification-runs/.locks/` serialize journal commits and each complete writer
snapshot → agent → enforcement window. This prevents one loop's journal or review from being
misattributed to another writer without removing ignored files from write-scope enforcement. A
second paid run or dry run for the same target fails with `RUN_CONFLICT`; it must be retried after
the owning run exits and current review state is resolved again.

## Generic target contract

A manifest owns every target-specific value:

- target artifacts;
- for design-governed targets, the §0 bounds/declaration matcher and a design-path template;
- authoritative sources and supporting evidence;
- dependency artifacts and binding-contract selectors;
- prior-review directory, filename regex, numeric round group, gap policy, and read order;
- forbidden path and provenance patterns;
- immutable patterns and each writer's allowlist;
- review output naming/placement and recovery-journal placement;
- a referenced attack-lens set and first/mature lens order;
- citation format/resolver and unique-relocation policy;
- interface/change-trigger regions;
- verdict/convergence policy reference;
- optional target context and fix-up conventions.

For a design-governed target, `design_revision.target_index` identifies the reviewed artifact,
`section_zero` bounds its §0, and `declaration_pattern` must match once inside that range and capture
the canonical design path in a named `path` group. Exactly one authoritative source uses
`docs/design/{design_version}/DESIGN.md`; the engine materializes that template before validating
its ordinary content selectors.

Paths must be repository-relative. Existing paths and symlink targets must remain inside the Git
root. Missing, ambiguous, colliding, gapped, or conflicting configuration fails before any agent
runs. A design-governed profile extracts exactly one canonical `docs/design/<version>/DESIGN.md`
path from its target's §0 unless `--design-version` supplies a safe directory label. The selected
revision is re-resolved at every round boundary for the §0 default and remains fixed for an explicit
override. A generic profile with no design source is unchanged by default and rejects the override.
Selector starts are unique. Ends default to `unique-after-start`; a manifest must explicitly
choose `first-after-start` when a repeated terminator is intentional. The engine reports current
coordinates rather than trusting permanent line numbers. It revalidates every target, authority,
supporting-evidence, dependency binding-contract, and interface selector from the current
filesystem at each round boundary before dispatching Attack, then replaces the selector context
for every role in that round. The same refresh rediscovers prior reviews for the read-only deny
list and adjudicator-last context. A missing, ambiguous, escaped, or otherwise invalid refreshed
source fails before Attack and is recorded in the run journal. Output naming must round-trip
through prior-review discovery, prior reviews must be immutable, journal/output/write areas cannot
overlap, and empty immutable globs fail unless they intentionally match the next review output.
Verdict, stop, and refuter policies declare fixed preserved semantics; contradictory policy data
is rejected.

Current production profiles are `phase-1` through `phase-8` and `phase-11`. The
`non-phase-fixture` profile is intentionally tiny and proves the core has no phase number,
document basename, version-directory, review-directory, or section-number dependency.

## Enforcement stack

Two layers, in order:

1. **Pre-execution (new relative to the Codex loop).** Every role session registers an inline
   `tool_call` hook built from the engine's per-role enforcement payload. Writer sessions
   (Adjudicate, Fix-up) may `write`/`edit` only their exact manifest allowlist — the adjudicator
   exactly `{review_output}` — and read-only sessions are denied forbidden-source patterns and
   deny-listed prior reviews before the read happens. The one configured prior-Resolutions attack
   lens and the fix-up's own current review are the only exemptions, matching the prompt rules.
   Because no role session has a shell, this hook covers every write route a session has.
2. **Post-stage (legacy, unchanged).** Writer stages run inside the workspace-mutation lease with
   full-worktree hashes before and after: any change outside the allowlist, or any
   immutable-evidence change (content or metadata), fails the stage with `WRITE_VIOLATION`
   regardless of how it happened.

The hook is the deterministic gate the prompt rules describe; the hash check stays as the
independent backstop. Both are required — neither alone covers a hook bypass bug or an
out-of-process modification, respectively.

## Canonical sources

| Concern | Canonical source |
|---|---|
| orchestration, barriers, aggregation, safety checks | `.agents/skills/verify-loop/scripts/engine.mjs` |
| omp session construction, yield capture, path hook | `.agents/skills/verify-loop/scripts/omp-agent-runner.mjs` |
| CLI parsing and entry | `.agents/skills/verify-loop/scripts/verify.mjs` |
| progress events, rendering, heartbeat | `.agents/skills/verify-loop/scripts/progress.mjs` |
| role instructions | `.agents/skills/verify-loop/prompts/*.md` |
| structured results and contracts | `.agents/skills/verify-loop/schemas/*.schema.json` |
| target-specific values | `verification/targets/*.json` |
| shared behavior and estimates | `verification/policy.json` |
| attack-lens data | `verification/lenses/*.json` |

This document references those sources; it is not a manually synchronized prompt copy.

## Validation

```bash
node tests/verify-engine-v2.test.mjs
scripts/verify --target phase-1 --dry-run
scripts/verify --target phase-2 --dry-run
scripts/verify --target phase-3 --dry-run
scripts/verify --target phase-4 --dry-run
scripts/verify --target phase-7 --design-version v3 --dry-run
scripts/verify --target non-phase-fixture --dry-run
git diff --check
```

The tests cover schema/config parsing, target selection, missing/ambiguous/conflicting inputs, root
containment, prior-review/output state, selectors, stage order and stops, refuter aggregation,
citation rejection/relocation, recursive Gate evidence, write allowlists, the pre-execution hook's
read-denial and write-allowlist decisions, ignored-file/mode checks, literal PASS, adjudication
disposition scope, the bounded adjudication-payload correction, fake-agent full orchestration,
first-to-mature transitions, review-only continuation, round-boundary selector/prior-review
refresh, parallel-target coordination, duplicate-target rejection, stale/live lease handling,
failure journaling, and the non-phase fixture. The suite injects a fake agent runner behind the
engine's `agentRunner` seam; it makes no live model calls and runs under plain `node`.

## Partial failure

Every real run checkpoints stage and round state below `.verification-runs/`; CLI errors identify
the exact journal. On failure:

1. inspect the journal named in the error context when available;
2. inspect `git status --short` and the diff;
3. treat a newly written review or partial fix-up as real state;
4. reconcile or complete that state before retrying;
5. never overwrite an existing review and never edit a prior review's evidence.

A review-only correction is continued with `--fixup-review latest`, never by starting the next
Attack round. The continuation validates the latest verdict/counts, rejects an existing Resolutions
section, excludes only that review from the immutable baseline, and permits append-only resolution.
A final-round fix-up is intentionally reported as unreviewed if the cap is reached. Raising the cap
is not recovery by itself; first inspect whether correction counts converge and whether the fix-up
is generating excessive new prose.

Leases owned by a dead PID are reclaimed automatically. A live workspace-mutation owner is never
preempted; waiters report the holder periodically and fail with `LOCK_TIMEOUT` if their bounded wait
expires. A conflict or timeout includes the lock, target, PID, stage, and journal metadata available
for diagnosis.

### Rejected writer payloads

A writer role never auto-retries. A schema-valid adjudication payload the engine rejects as
self-contradictory is nevertheless correctable, so it gets exactly one bounded correction
dispatch — same role, same write allowlist, prompted with the rejection text and its own rejected
payload, and told to reconcile what it already judged rather than re-derive findings. A second
contradictory payload ends the round. Both payloads are journaled as the round's `adjudication`
before validation runs, with the rejection under `adjudication_correction`, so a round that does
die is diagnosed from the journal and never reconstructed from the review prose.

`candidate_dispositions` must carry exactly one entry per candidate that survived to Adjudicate.
A redundant `DROPPED`/`none` entry for a candidate Refute, Steelman, or the Gate already
eliminated is ignored — the adjudicator is shown those candidates and asked to write them up under
`## 2. Checked and clean`, and such an entry feeds no count and no interface flag. It is recorded
as the round's `adjudication_echoed_eliminations`. Everything else is fatal and names the
candidate: an omitted survivor, a duplicate, an id from neither set, or an eliminated candidate
revived as `ADMITTED` — the last being the abuse the coverage check exists to stop, since that
evidence never survived the stage that rejected it.

`touches_interface` is a *change* predicate at every stage, defined once in the finder prompt:
whether the correction that finding orders would change a manifest-declared
interface/change-trigger region. A finding that orders no edit there is `false` even when the line
it cites sits inside the region, and `interface_changed` is the OR of `touches_interface` over the
ADMITTED dispositions. That flag is reporting only — trend and review text. What actually decides
whether a region changed is `authoritativeInterfaceRegions`, which hashes each region selector
before and after the fix-up.

Discarding a rejected round removes the review from the **git index**, not just the worktree: the
engine's existing-file view is `git ls-files -co`, so a bare `rm` leaves the path colliding with
the adjudicator's write allowlist and preflight fails with `Write permission conflicts with
existing immutable evidence`. The discarded content stays recoverable from history whenever the
rejected review was already committed.

### Provider failures

A role session ends in `AGENT_ERROR` when the model never lands a valid terminal `yield`. The
runner reports its yield telemetry in the error (`accepted yields`, `sections`, `rejected yields`
with the last rejection), mirrors it per nudge on stderr, and the engine retries the role once with
a fresh session, recorded in the journal's `role_retries`. When the telemetry shows **zero accepted
and zero rejected yields**, the model never attempted a submission — that is a provider/session
failure, not a prompt or schema one. Corroborate in `~/.omp/logs/omp.<date>.<pid>.log` (the run's
PID is in its journal filename): `agent turn ended with provider error` entries carry the upstream
message — HTTP 429 rate caps, stream stalls, or upstream connection resets. Provider-error turns
can retain a partial text prefix, which is how a dropped stream used to surface as the misleading
`returned invalid JSON` fallback.

On provider-error clusters:

- Do not run several paid targets concurrently on a rate-capped account: every concurrent role
  session and every nudge spends the same account budget, and the 429 backoff turns a twenty-minute
  round into an hour of grinding.
- Retry the run once the upstream recovers, or route the loop to a healthier provider for the
  session with `--model <pattern>` (per-role `modelPattern`, all stages).
- A rejected yield (`rejected yields: N`) is different: the last rejection text names the schema
  violation, the nudge relays it verbatim to the model, and a persistent rejection means the role's
  payload is genuinely malformed — investigate that role's prompt/schema, not the provider.

## Migration notes (Codex loop → omp loop)

Behavioral contract — independence, fail-closed citation Gate, one review one verdict, adversarial
aggregation, manifest-driven targets, evidence and write discipline, operational flags — is
unchanged and binding. Harness machinery was re-pointed:

| Codex loop | omp loop |
|---|---|
| `codex exec` subprocess per role | `createAgentSession` per role, in-process |
| `--sandbox read-only` / `workspace-write` | `toolNames` + `restrictToolNames` allowlists (no shell in any role) |
| `--output-schema` | `outputSchema` + strict mode + terminal `yield`, engine re-validation unchanged |
| prompt-only forbidden-source/read discipline | pre-execution `tool_call` hook denial, prompts unchanged in substance |
| worktree-hash write enforcement | kept, now the backstop behind the hook |
| `--model` | `--model` pattern per session |
| Node engine | Node engine core; Bun only for the SDK adapter on live runs |

The retired loop's stage decomposition (Attack → Refute → Steelman → Gate → Adjudicate → Fix-up)
is preserved as engine machinery; whether it binds as design text is governed by
`docs/design/v3/DESIGN.md` and `docs/tooling/CODEX_MIGRATION_OVERLAY.md`, not by this runbook.
