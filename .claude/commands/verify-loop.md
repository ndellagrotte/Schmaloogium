---
description: Loop adversarial §G1.2 verify → §G1.3 fix-up on a phase doc until a review returns PASS
argument-hint: "[phase=N] [lean|thorough] [maxRounds=N] [startRound=N] [reviewOnly]"
allowed-tools: Workflow, Read, Bash, Glob, Grep
---

# /verify-loop — drive a `PHASE_<N>_DOC.md` to a literal PASS

Arguments given: `$ARGUMENTS`

## What this is

**A bare `DESIGN.md` is ambiguous in this repository, and the harness no longer has one.** Three
files carry that basename — the reorg put the version in the directory — and they are different
documents. Which one a round reads is a **declared per-phase fact**, `design` in the script's
`PHASE_FACTS`, resolved through the `DESIGN_PINS` table that holds that revision's complete set of
section→line pins:

| Revision | Lines | Who reads it |
|---|---|---|
| `docs/design/v1.1/DESIGN.md` | 1,586 | **Phase 2** (`PHASE_2_DOC.md` §0.1 cites the Phase 2 spec at v1.1 ll. 662–720), and every Phase 1 review through round 11 |
| `docs/design/v2.0-RC2/DESIGN.md` | 2,478 | **Phase 1** from §0.11 onward — `PHASE_1_DOC.md` l. 12 declares it, adopted at the round-eleven fix-up as §G0.4 step 3 |
| `docs/design/v2.0-RC1/DESIGN.md` | 2,304 | nothing; kept for history |

The split is real and it is not a mistake: §G0.4 step 3 migrates phase docs **one at a time**, and
`PHASE_2_DOC.md` has not been migrated. RC2 therefore keeps its `-RC` suffix (`docs/MOVES.md`'s
version-label rule) and phase 2 keeps reading v1.1 until it is migrated in turn.

**Pointing a phase at the wrong revision would not error — it would silently feed every agent the
wrong text at coordinates that look entirely plausible.** So adding or moving a revision means
deriving that revision's whole pin set from **its own headings** (`grep -n '^#'`, then print each
range and confirm its first and last line), never shifting another revision's numbers by an offset.
That is §G0.4 step 1, and it is why the offset figures this section used to quote are gone: they were
wrong when they were written.

`DESIGN.md` §G1.2 assigns each phase doc an adversarial verify session, and §G1.3 a fix-up when the
verdict is PASS-WITH-CORRECTIONS. This command automates that cadence — brief → verify → brief →
fix-up — and runs it until a review returns a literal **PASS**, defined as **zero blocking findings
and zero corrections**. Notes may be raised; they are recorded in the review and deferred, not
applied. It also stops on FAIL, on the round cap, or on a token budget — and in every one of those
cases it says plainly that PASS was not reached rather than dressing the outcome up.

The harness was built for Phase 1, which reached it after ten hand-run rounds. It is now
**parameterized by phase**: `phase=N` selects the document, and everything else — paths, the spec
and doc-gate line ranges, the OQ set, the dependency docs — derives from the `PHASE_FACTS` table in
the script. Adding a phase means adding a row, not editing prompts.

**Current default is `phase=2`** (Conformance harness), which has been built under §G1.1 and has had
**zero verify sessions**. It starts at round 1.

## How to run it

The workflow script lives at `.claude/workflows/verify-loop.js` — one copy, git-tracked
alongside the documents it drives. Invoke it directly; do **not** retype it inline:

```
Workflow({
  scriptPath: ".claude/workflows/verify-loop.js",
  args: { phase: 2, preset: "lean", startRound: 1, maxRounds: 6 }
})
```

Parse `$ARGUMENTS` into `args`:

| Argument | Default | Meaning |
|---|---|---|
| `phase=N` | `2` | Which phase doc to review. A phase with no `PHASE_FACTS` row throws at startup rather than silently reusing another phase's facts. |
| `lean` \| `thorough` | `lean` | 3 finders / 2 refuters / no steelman, versus 5 / 3 / steelman |
| `maxRounds=N` | `6` | Hard cap. `maxRounds=0` is a parse-only smoke test that spawns nothing. |
| `startRound=N` | `1` | Round number, and therefore the review filename `docs/phase<N>/reviews/PHASE_<N>_REVIEW_<R>.md`. **`startRound=1` also means "this document has never been reviewed"** and changes the prompts — see below. |
| `reviewOnly` | off | Run one verify round and stop before the fix-up. Use this first. |
| `addendumLines=N` | `40` | Cap on the §0.N addendum the fix-up may write |

Report the returned `trend` array to the user round by round. It is the honest record of whether the
loop converged.

## Cost, stated up front because it is large

Per round, with `C` candidate findings surviving dedup (historically 5–8):

| Preset | Agents per round | Six rounds |
|---|---|---|
| `lean` | `3 + 2C + 1 + 2` ≈ **16** | ≈ 96 |
| `thorough` | `5 + 3C + steelmen + 1 + 2` ≈ **36** | ≈ 216 |

This session's workflow-size guideline is *medium — under 15 agents*. **A loop of this shape
necessarily exceeds it.** Default to `lean`. Tell the user the estimate before a long run, and never
raise `maxRounds` past 6 without them asking. Expect a first-ever review to run at the high end of
`C`: the whole document is unreviewed surface.

## The six stages

```
Attack     read-only finders, disjoint §G1.2 lenses           → candidate findings
Refute     N skeptics per candidate, prompted to KILL it      → survivors (majority rule)
Steelman   opposed lens on surviving corrections / §5 claims  → final severity
Gate       one agent re-resolves EVERY citation at the line   → anchor-verified findings
Adjudicate one agent writes PHASE_<N>_REVIEW_<R>.md, one verdict → PASS? break
Fix up     one agent applies corrections + Resolutions + §0.N → new surface for round R+1
```

Stage boundaries are **barriers, and each is justified**: dedup needs every finder's output at once,
the adjudicator needs all findings to reach one verdict, the fix-up needs the whole review. Do not
convert them to a pipeline.

## The rules the script encodes, and what each cost to learn

- **Read-only by construction.** Finders, refuters, steelmen and the gate run as `agentType:
  'Explore'`, which has no `Edit`/`Write`. Honest caveat: `Explore` still has `Bash`, so this is
  *structural, not airtight* — the prompt forbids shell writes and mutating git explicitly. Only the
  adjudicator and the fix-up agent are `general-purpose`, and each may touch a named set of files.
- **Every path is repo-relative, and the prompts say so.** The documents moved out of the repo root
  into `docs/` in commit `9df5f05`; the harness was written before that and referenced bare
  filenames, none of which resolved any more. They were reorganized again into versioned
  directories (`docs/design/v1.1/`, `docs/research/v1/`, `docs/phase<N>/<version>/`,
  `docs/phase<N>/reviews/`) — basenames were deliberately preserved that time, so bare-filename
  prose citations survived and only paths changed. The phase doc's version directory is declared
  per-phase as `docVersion` in the script's `PHASE_FACTS`; it is not derivable from the phase
  number, and rolling it is a manual two-step done **after** a loop exits. The evidence schema now demands a repo-relative path
  explicitly, because the Gate resolves citations literally — bare filenames would have made it drop
  **every** finding.
- **Forbidden sources, by pattern in every prompt.** Prior sessions' terminal transcripts:
  `a directory named `chatlogs/` anywhere below `Schmaloogium/docs/`` (any extension — one is `.md`) and **any `*.txt` at the
  Schmaloogium root**. §G1.2 bars a reviewer from the author's conversation context because it
  transmits the author's blind spots. **A sub-agent read one contrary to instruction in Phase 1
  round nine**; its conclusion was discarded and re-derived from permitted sources
  (`PHASE_1_REVIEW_9.md` §0.2). The old rule was `Schmaloogium/*.txt`, which after the reorg matched
  **1 of the 17** transcripts. Note both halves are needed and neither may name individual files:
  `/export` writes new transcripts to the repo root under dated filenames, so any rule that lists
  them by name is stale the next time someone exports.
- **Reading-order discipline.** Finders read **no** review file *for the phase under review*. Only
  the adjudicator reads them, **last**, after the candidates are in hand — because reading the
  resolutions early converts an independent reader into an auditor of someone else's reasoning. A
  **dependency** phase's reviews are not subject to this: they are evidence about the contract being
  checked, not a frame to inherit.
- **Round one is not round eleven.** The prompts written for Phase 1 asserted "ten verify rounds
  have run" and "the failure mode is the reviewer keeping the loop alive". On a document that has
  never been reviewed those are false and actively harmful — they bias finders toward silence and
  the adjudicator toward a false PASS. At `startRound=1` the harness inverts them: the whole document
  is unreviewed surface, a substantial findings list is expected, and the anti-inflation discipline
  applies to *severity*, not volume. The lens order changes too — `new-surface` is degenerate with no
  prior fix-up, so the structured checks lead and a whole-document lens sweeps up the remainder.
- **Interface honesty is only real when the phase has a dependency.** For Phase 1 (`depends on: —`)
  that check was vacuous. Phase 2 consumes Phase 1, and its §5.2 is a consumption table whose every
  row cites a `PHASE_1_DOC.md` section, so the lens now requires taking each consumed item back to
  the dependency's **§5** — the binding contract under §G1.1. This is the highest-yield check
  available on a doc with dependencies and it cannot be done by reading that doc alone.
- **The Gate is the reason delegating is safe here at all.** Six of Phase 1's ten rounds *declined*
  sub-agents, with a real objection: *"the brief requires each load-bearing quote to be re-derived by
  the reviewer before admission as evidence, which makes a delegated finding a hop rather than a
  saving."* The answer is not to argue but to mechanise the gate round nine ran by hand — *"no
  candidate reached §1 without my own confirmation at the line."* Any finding whose citation does not
  resolve verbatim is **dropped and logged**.
- **Anti-inflation, everywhere.** `clean_areas` is a required field; every prompt carries *an empty
  findings array is a perfectly good outcome*; and Phase 1's round 4 measured the noise floor at **33
  raw agent findings → 21 kept**, which is why the refutation stage exists.
- **Convergence is instrumented, not assumed.** The standing lesson is that *unreviewed material
  yields findings in proportion to its size, not to the document's maturity* — so the fix-up is
  structurally the finding generator. The §0.N addendum is capped at 40 lines with the argument moved
  to `## Resolutions` (what §G1.3 actually requires), and the script logs a **CONVERGENCE WARNING**
  whenever corrections fail to fall across three consecutive rounds.

The four JSON schemas and the `COMMON` / `POSTURE` prompt blocks are lifted from the round-4
adversarial workflow, which ran 15 agents with 0 errors. Do not rewrite them without a reason.

## Before the first unattended run — verify the harness, in this order

Run from `/home/nick/IdeaProjects/schmaloogium-project/Schmaloogium`.

1. **Parse check.** `maxRounds=0`. Returns immediately, spawns nothing. Catches computed values in
   `meta`, TypeScript syntax, or a forbidden `Date.now()`.
2. **Paths resolve.** The check that would have caught the original breakage:
   ```bash
   ls docs/phase2/v1/PHASE_2_DOC.md docs/phase1/v14/PHASE_1_DOC.md \
      docs/research/v1/RESEARCH.md \
      docs/design/v1.1/DESIGN.md docs/design/v2.0-RC2/DESIGN.md
   ```
   All five must exist — **both** design revisions, because which one resolves depends on the
   phase's `design` fact and a missing one fails only for the phase that names it. Then confirm the
   target review file does **not**:
   ```bash
   ls docs/phase2/reviews/PHASE_2_REVIEW_1.md   # must be "No such file"
   ```
   The adjudicator is told to create exactly one file; a wrong `startRound` would overwrite evidence.
3. **One review round, no fix-up.** `phase=2 reviewOnly maxRounds=1`. Then:
   ```bash
   git status --short
   ```
   **Only `docs/phase2/reviews/PHASE_2_REVIEW_1.md` may be new; nothing may be modified.** A finder
   or refuter that wrote anything is a failed read-only contract — stop and fix the harness. This
   check is **precise from round one**: `docs/phase2/reviews/.gitkeep` is tracked, so git lists a
   new review as its own `?? docs/phase2/reviews/PHASE_2_REVIEW_1.md` line instead of collapsing the
   directory to a single `??` entry. The phase doc is tracked too, so a modification to it always
   shows. Should that `.gitkeep` ever be removed, the collapse — and the degraded check — returns.
4. **Prove the Gate drops things.** Add one fabricated finding citing a line whose quote does not
   match, and confirm the run logs `Gate DROPPED`. A gate that passes everything is not a gate.
5. **Prove the §5 report is real.** The fix-up returns a before/after sha256 of §5 taken by content
   anchor. Verify it independently — **anchor by content, never by line**, since the fix-up's
   insertions shift everything below them. Snapshot *before* the run:
   ```bash
   awk '/^## 5\. Cross-phase interfaces/,/^## 6\. Failure modes/' \
       docs/phase2/v1/PHASE_2_DOC.md | sha256sum
   ```
   and again after; an unchanged hash must correspond to `section5_unchanged: true`. Do **not** use
   `git show HEAD:…` for this baseline — in the window between a reorg's `git mv` and its commit,
   `HEAD` still holds the doc at its **old** path, so `git show HEAD:docs/phase2/v1/PHASE_2_DOC.md`
   fails, and a failed `git show` yields a non-empty diff that reads as a false "§5 changed" every
   round. Anchor on the working tree.
6. **Evidence stays byte-identical, every round:**
   ```bash
   md5sum docs/phase1/reviews/*.md docs/phase1/briefs/*.md \
          docs/design/v1.1/DESIGN.md docs/design/v2.0-RC1/DESIGN.md \
          docs/design/v2.0-RC2/DESIGN.md docs/research/v1/RESEARCH.md \
          docs/phase1/v14/PHASE_1_DOC.md
   ```
   **All three design revisions**, not only the one this run reads: the other two are the anchors of
   other phase docs and of earlier reviews, so they are evidence too, and the prompts' do-not-modify
   lists name all three for the same reason.
7. **Watch it live** with `/workflows`. Each run persists its script and a `journal.jsonl` recording
   every agent's actual return value — read that before diagnosing any round that comes back empty.

Only after 1–7 pass should the loop run unattended.

## Prerequisites for an unattended (non-`reviewOnly`) run

- **Tracking is no longer a prerequisite gap.** The Phase 2 doc and `docs/research/v1/RESEARCH.md`
  are both tracked as of the versioned-directory reorg, so the fix-up's `git status --short` check
  reports modifications to them precisely. `docs/phase2/reviews/` is tracked as well — it carries a
  `.gitkeep` — so a new review is listed as its own `??` line and the check is **not** degraded, on
  round one or any round after. The fix-up still reports a degraded check if git ever collapses a
  `reviews/` directory to one `??` entry, but that now happens only for a newly added phase whose
  `reviews/` holds no tracked file yet — so a `degraded` result on phase 1 or 2 means a tracked file
  has gone missing and is worth investigating. Note that transcripts are now ignored via
  `docs/**/chatlogs/`, which is what keeps `git status --short` readable.
- Confirm Phase 1's status is understood, because it changed at the round-eleven fix-up (2026-07-26)
  and the reason it is unverified is no longer the reason it used to be. `PHASE_1_REVIEW_11.md` is
  PASS-WITH-CORRECTIONS and **its `## Resolutions` now exists**, as does `PHASE_1_DOC.md` **§0.11**;
  all six findings were applied. Phase 1 is nonetheless still **not** "verified" under §G1.3, now
  because that fix-up **altered §5** — three hunks: V11-1's deletion from §5.2's pixel-transfer
  consumer column, plus two new §5.1 rows the RC2 migration added (`[D-P1-36]`, `[D-P1-37]`) — so
  §G1.3's re-verify trigger has fired and **a twelfth verify session is owed** before any dependent
  consumes the doc. Run it as `phase=1 startRound=12`; a lower `startRound` overwrites existing
  review evidence and there is no overwrite guard. §G5.3's gating invariant therefore remains
  violated by Phase 2 having been built against Phase 1, as `PHASE_2_DOC.md` §0.3 item 6 discloses.
  The loop proceeds anyway, by decision; the reviewers inherit it as a known open item.
- Note also that `PHASE_1_DOC.md` §4.12, §4.13 and §4.5.2a — three subsections and three decisions
  (`[D-P1-36]`, `[D-P1-37]`, `[D-P1-38]`) added by that fix-up — are **entirely unreviewed material**.
  The standing lesson prices this honestly: unreviewed material yields findings in proportion to its
  size, not to the document's maturity. Round twelve should not be briefed as a mature-document round.

## When it stops

- **PASS** — report the round, the trend, and that the phase now has a verdict that closes §G1.2.
- **FAIL** — stop. §G1.3 routes FAIL to a rerun of the *build* session with the review added to its
  Required inputs. That is a far larger operation and a human call; never automate it.
- **Cap or budget** — print the trend and say plainly that PASS was not reached. If corrections are
  not falling round over round, the convergence levers are not working: that is the finding, and the
  answer is to inspect the loop, not to raise the cap.

The per-round brief templates this harness replaces are kept, readable, in
`docs/tooling/VERIFY_LOOP_BRIEFS.md` — read them if you need to change what an agent is
told, and change both, saying which.

**Both copies changed on 2026-07-26** (§G0.4 steps 1, 2 and 4, completing the adoption the
round-eleven fix-up started at step 3). What changed in the *prompts*: the ground-truth block now
names its design revision and its whole pin set comes from `DESIGN_PINS` rather than being hardcoded
v1.1; the doc-gate lens's §G9 range is interpolated instead of hardcoded; both do-not-modify lists
name all three `DESIGN.md` revisions instead of only the one being read; and every agent is told to
stop and report a coordinate that disagrees with the file rather than guess (§G0.4). What changed in
*this file*: the version-pin section above, the pre-flight `ls` and `md5sum`, and the Phase-1-status
prerequisite. `docs/tooling/VERIFY_LOOP_BRIEFS.md` carries the mirror of the prompt changes.
