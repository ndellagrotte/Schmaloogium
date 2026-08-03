# Codex verification operations

The migration is built on supported Codex surfaces:

- repository guidance: root `AGENTS.md`;
- reusable workflow discovery: this repository-scoped skill under `.agents/skills/`;
- executable automation: `codex exec`;
- per-call structured output: `codex exec --output-schema`;
- least-privilege execution: explicit `--sandbox read-only` or `workspace-write`.

OpenAI's current documentation describes repository skills at `$REPO_ROOT/.agents/skills`,
project-scoped custom agents at `.codex/agents`, non-interactive automation through `codex exec`,
and structured returns through `--output-schema`. The engine uses direct, isolated `codex exec`
sessions because each role needs a different schema and sandbox plus deterministic barriers and
aggregation; adding a second custom-agent instruction layer would duplicate the canonical prompts.

Official references:

- https://developers.openai.com/codex/skills
- https://developers.openai.com/codex/noninteractive
- https://developers.openai.com/codex/multi-agent
- https://developers.openai.com/codex/guides/agents-md

## Entry points

From a fresh Codex session:

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
`.agents/skills/verify-loop/scripts/verify.mjs` plus `engine.mjs`.

## Arguments

| Argument | Meaning |
|---|---|
| `--target <id\|path>` | Required target ID or repo-relative manifest path |
| `--design-version VERSION` | Verification-only design directory override; otherwise use the target document's §0 declaration |
| `--preset lean\|thorough` | `lean`: 3 finders, 2 refuters, no steelman; `thorough`: 5, 3, steelman |
| `--start-round N` | Optional assertion; must equal the unique next discovered round |
| `--max-rounds N` | Cap, default 6; `0` resolves with zero agents |
| `--review-only` | One adjudicated review, no fix-up |
| `--fixup-review latest\|PATH` | Continue only the latest unresolved correction-bearing review |
| `--dry-run` / `--estimate` | Full parse/path/selector/state/write-plan validation, zero agents |
| `--model MODEL` | Optional explicit Codex model for all role sessions |
| `--agent-timeout-ms N` | Per-session timeout, default 30 minutes |
| `--json` | Compact result JSON |

Codex authentication must already be available to the CLI. Non-interactive roles use approval
policy `never`; a role that needs a new approval fails instead of stalling or silently widening
permissions.

## Cost and concurrency

The dry-run reports an upper estimate from the configured assumed candidates per finder.
For `F` finders, `R` refuters, and assumed total candidates `C`:

```text
Attack F + Refute C×R + conditional Refute-correction C×R + optional Steelman C
         + conditional Gate 1 + Adjudicate 1 + conditional Fix-up 1
```

Actual calls fall when finders return fewer/deduplicated candidates, refuters eliminate candidates,
refuters return valid citations without their one allowed correction attempt, steelman is disabled,
Gate has no survivors, PASS/FAIL stops, or review-only omits fix-up. Each role is a fresh Codex
session and therefore consumes its own tokens. Parallelism is capped per preset and never crosses a
stage barrier.

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

Current production profiles are `phase-1` through `phase-8`. The
`non-phase-fixture` profile is intentionally tiny and proves the core has no phase number,
document basename, version-directory, review-directory, or section-number dependency.

## Canonical sources

| Concern | Canonical source |
|---|---|
| orchestration, barriers, aggregation, safety checks | `scripts/engine.mjs` |
| CLI parsing and entry | `scripts/verify.mjs` |
| role instructions | `prompts/*.md` |
| structured results and contracts | `schemas/*.schema.json` |
| target-specific values | `verification/targets/*.json` |
| shared behavior and estimates | `verification/policy.json` |
| attack-lens data | `verification/lenses/*.json` |

This document references those sources; it is not a manually synchronized prompt copy.

## Validation

```bash
node tests/verify-engine.test.mjs
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
citation rejection/relocation, recursive Gate evidence, write allowlists, ignored-file/mode checks,
literal PASS, fake-agent full orchestration, first-to-mature transitions, review-only continuation,
round-boundary selector/prior-review refresh, parallel-target coordination, duplicate-target
rejection, stale/live lease handling, failure journaling, and the non-phase fixture.

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
