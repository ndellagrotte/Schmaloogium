# Docs reorganization — move manifest

*Recorded 2026-07-25. Every old→new path from the versioned-directory reorg, so a reference that
predates it can be resolved without archaeology. The previous reorg (commit `9df5f05`) left no such
record and cost thirty-two stale references plus a broken harness; this file exists so that does not
repeat.*

## The scheme

**Version lives in the directory; basenames are preserved.** There were ~1,200 bare-filename
citations across the tree (`RESEARCH.md` ~660, `DESIGN.md` ~390, `PHASE_1_DOC.md` ~180). Keeping
basenames meant only *paths* went stale, not prose — so the repair touched ~87 sites rather than
~1,200.

The cost of that trade is one collision: **six files are now named `DESIGN.md`** (the third and
fourth joined 2026-07-26; the fifth and sixth joined 2026-08-03). They are different documents. See the
warning below.

## Documents

| Old path | New path |
|---|---|
| `docs/project/DESIGN.md` | `docs/design/v1.1/DESIGN.md` |
| `docs/pintonium/DESIGN_PINTONIUM_REV1.md` | `docs/design/v2.0-RC1/DESIGN.md` |
| `docs/design/v2.0-RC5/DESIGN.md` | `docs/design/v3/DESIGN.md` |
| `docs/project/RESEARCH.md` | `docs/research/v1/RESEARCH.md` |
| `docs/pintonium/PINTONIUM_DESIGN.md` | `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` |
| `docs/phase1/artifacts/PHASE_1_DOC.md` | `docs/phase1/v14/PHASE_1_DOC.md` *(was `v10/` until the round-eleven roll, then `v11/` until the direct two-overdue-roll catch-up to `v13/`, then `v14/` after round fifteen's literal PASS confirmed the applied §0.14 fix-up, 2026-07-26)* |
| `docs/phase1/artifacts/PHASE_1_REVIEW.md` | `docs/phase1/reviews/PHASE_1_REVIEW_1.md` |
| `docs/phase1/artifacts/PHASE_1_REVIEW_{2..11}.md` | `docs/phase1/reviews/PHASE_1_REVIEW_{2..11}.md` |
| `docs/phase1/artifacts/PHASE_1_REVIEW_9_BRIEF.md` | `docs/phase1/briefs/PHASE_1_REVIEW_9_BRIEF.md` |
| `docs/phase1/artifacts/PHASE_1_REVIEW_10_BRIEF.md` | `docs/phase1/briefs/PHASE_1_REVIEW_10_BRIEF.md` |
| `docs/phase1/artifacts/PHASE_1_FIXUP_6_BRIEF.md` | `docs/phase1/briefs/PHASE_1_FIXUP_6_BRIEF.md` |
| `docs/phase2/artifacts/PHASE_2_DOC.md` | `docs/phase2/v1/PHASE_2_DOC.md` |
| `docs/misc/recommendation_doc.md` | `docs/decisions/TEMPLATE_BRANCH_RECOMMENDATION.md` |
| `docs/project/VERIFY_LOOP_BRIEFS.md` | `docs/tooling/VERIFY_LOOP_BRIEFS.md` |
| `docs/pintonium/pintonium_design_doc_creation_chatlog.md` | `docs/reference/pintonium/chatlogs/` |
| `phase2chat.txt` *(repo root)* | `docs/phase2/chatlogs/phase2chat.txt` |
| `2026-07-25-175142-…txt` *(repo root)* | `docs/phase2/chatlogs/` |

`docs/phase1/chatlogs/` did not move. `docs/project/`, `docs/misc/`, `docs/pintonium/` and both
`artifacts/` directories no longer exist.

### Reference sources (gitignored)

| Old | New |
|---|---|
| `reference-src/Cleanroom-0.6.6-alpha` | `reference-src/cleanroom-0.6.6-alpha` |
| `reference-src/schlorbium-project_G6_pre1` | `reference-src/schlorbium-HD_U_G6_pre1` |
| `reference-src/Pintonium_Commit 9c2fcc1` | `reference-src/pintonium-9c2fcc1` |
| — *(new checkout, 2026-07-26)* | `reference-src/Oculus-1.12` |

The Pintonium directory name contained a space, which broke unquoted shell paths.

## Three basenames changed

Everything else kept its filename. These did not, each for a reason:

1. **`DESIGN_PINTONIUM_REV1.md` → `DESIGN.md`** — the version moved into the directory.
2. **`recommendation_doc.md` → `TEMPLATE_BRANCH_RECOMMENDATION.md`** — the old name said nothing;
   verified zero inbound references.
3. **`PHASE_1_REVIEW.md` → `PHASE_1_REVIEW_1.md`** — the harness computes
   `PHASE_<N>_REVIEW_<r>.md`, so round one was the only review it could not address. 40 citations
   updated.

## ⚠️ Six files named `DESIGN.md`

| | `docs/design/v1.1/DESIGN.md` | `docs/design/v2.0-RC1/DESIGN.md` | `docs/design/v2.0-RC2/DESIGN.md` | `docs/design/v2.0-RC3/DESIGN.md` | `docs/design/v2.0-RC4/DESIGN.md` | `docs/design/v3/DESIGN.md` |
|---|---|---|---|---|---|---|
| Lines | 1,586 | 2,304 | 2,478 | 2,656 | 2,683 | 2,715 |
| Status | **governs Phase 2**; anchors Phase 1 reviews through round 11 | superseded; read by nothing | **governs Phase 1** from its §0.11 onward | **governs Phases 3–9** from their initial builds; partial adoption retains `-RC` | unadopted historical candidate | **unadopted current design revision**; no phase defaults to it |
| Phase 1 spec at | l. 585 | l. 829 | l. 957 | l. 1,120 | l. 1,130 | l. 1,147 |
| Phase 2 spec at | l. 662 | l. 933 | l. 1,071 | l. 1,234 | l. 1,244 | l. 1,261 |
| §G1.2 at | l. 118 | l. 174 | l. 257 | l. 276 | l. 286 | l. 303 |

*(2026-07-26: the RC1 column previously read 2,300 / 825 / 929 / 170 — off by four against the file
on disk since it was recorded; corrected while adding the RC2 column.)*

**There is no longer one governing revision, and no longer one set of line numbers** (changed
2026-07-26, §G0.4 steps 1–2 and 4; Phase 3 adoption recorded 2026-07-27; RC4 and v3 revisions recorded
2026-08-03). Phase docs cite `DESIGN.md` **by line number**, and the nine current phase docs are
anchored per phase:
`PHASE_1_DOC.md` l. 12 declares
`docs/design/v2.0-RC2/DESIGN.md` from its §0.11 onward, adopted at the round-eleven fix-up as §G0.4
step 3; `PHASE_2_DOC.md` §0.1 still cites v1.1 (the Phase 2 spec at ll. 662–720) because step 3 has
not been run for it; and Phases 3–9 deliberately adopt RC3 from their initial builds. Every Phase 1
review through round 11 is in v1.1's coordinates. RC4 and v3 are not adopted by any phase.

The `$verify-loop` harness therefore resolves the revision **per target** rather than globally.
Each phase profile in `verification/targets/` declares how to identify its governing-design path
inside the target document's §0 plus unique content selectors for Part I, the target specification,
the document gate, and the mandatory template. Unless `--design-version` is supplied, the engine
extracts that §0 path, resolves the selectors against its current content at startup and every round
boundary, and reports the version, path, selection source, and current coordinates. Phase 1's §0
selects RC2; Phase 2's selects v1.1; Phases 3–8 select RC3. Phase 9 declares RC3 in its document
header and has no target profile yet. No profile defaults to RC4 or v3; either may be selected
explicitly for a compatible target with `--design-version`. There is no executable line-pin table
to synchronize.

**A syntactically valid but unintended `--design-version` can still feed every agent the wrong
revision when its selectors also resolve.** The old pins make the point: at v2.0-RC1 ll. 649–652
the doc-gate lens read §G9's template, which looks entirely plausible; at v2.0-RC2 the same range
lands in §G6's conformance-tier text, equally plausible. So adopting a new or moved revision means
**re-deriving its whole pin set from its own headings** — never shifting another revision's numbers
by an offset. The procedure is v2.0-RC2 §G0.4, and its step 1 is exactly this.

## Version labels

Directory names come from each document's own header, not from the folder it used to sit in:

| Document | Directory | Evidence |
|---|---|---|
| `DESIGN.md` | `v1.1` | header states v1.1; REV1 names "v1.1" as what it supersedes |
| REV1 | `v2.0-RC1` | header states v2.0; recorded as RC because no downstream doc has adopted it |
| REV2 | `v2.0-RC2` | header states v2.0-RC2 (created 2026-07-26, not a move); supersedes RC1, which stays for history. **`-RC` retained after partial adoption — see the ruling below** |
| REV3 | `v2.0-RC3` | header states v2.0-RC3 (created 2026-07-26, not a move); **governs Phases 3–9** — RC2 still governs Phase 1 and v1.1 still governs Phase 2; partial adoption retains `-RC` |
| REV4 | `v2.0-RC4` | header states v2.0-RC4 (created 2026-08-03, not a move); unadopted candidate implementing PHASE_9_DOC §11.4's tag-evidence qualification; no phase or target points to it |
| REV5 | `v3` | header states v3; promoted together with its directory on 2026-08-03 from the former candidate label. It is the current unadopted design revision implementing PHASE_7_DOC §11.5's split frame-begin/matrix-capture timeline; no phase defaults to it |
| `RESEARCH.md` | `v1` | "first complete draft"; no version stated |
| `PINTONIUM_DESIGN.md` | `v1.0` | header states v1.0 |
| `PHASE_1_DOC.md` | `v14` | fix-up addenda §0.4–**§0.14**; `PHASE_1_REVIEW_14.md` has complete `## Resolutions`, and round fifteen returned a literal PASS with no further fix-up. Rolled from `v13` on 2026-07-26 after that loop exited |
| `PHASE_2_DOC.md` | `v1` | initial build session; `PHASE_2_REVIEW_1.md` returned literal PASS. Future round state is discovered from the review directory |
| `PHASE_3_DOC.md` | `v1` | initial build under RC3; rolls only after a fix-up addendum and a later literal PASS |

**Rolling a phase doc's version** (`v14` → `v15` only once a future §0.15 fix-up lands) is two steps,
run together and only **after** that `$verify-loop` run exits:

```bash
git mv docs/phase1/v14 docs/phase1/v15
# then update the target, interface, and fix-up paths in verification/targets/phase-1.json
```

`v<K>` = the highest `§0.K` fix-up addendum in the doc. Never roll mid-loop: the doc path is
resolved once at startup, so a rename between rounds invalidates every later stage.

*The `v10` → `v11` roll was performed 2026-07-26, both steps together, with no loop running. It cost
one dangling reference, recorded below.*

*The direct `v11` → `v13` roll was performed 2026-07-26 as a two-overdue-roll catch-up, both steps
together, with no loop running and no temporary `v12` directory. It catches the directory up to the
already-applied §0.12 and §0.13 addenda and costs one additional dangling reference, recorded below.*

*The `v13` → `v14` roll was performed 2026-07-26, both steps together and with no loop running,
after round fifteen's literal PASS confirmed the round-fourteen fix-up. It catches the directory up
to the already-applied §0.14 addendum and costs one additional dangling reference, recorded below.*

### Historical `-RC` suffixes after partial adoption — ruling, 2026-07-26

The rule above is that `-RC` means exactly *"no downstream doc has adopted it"*. That condition is now
**literally false**: `PHASE_1_DOC.md` l. 12 declares RC2 as its anchor. The suffix is nonetheless
**retained**, and `docs/design/v2.0-RC2/` is **not** moved, for two reasons:

1. **Adoption is per-document, and it is incomplete.** §G0.4 step 3 covers *both* phase docs, and
   `PHASE_2_DOC.md` has not migrated — it still cites v1.1 by line number. §G0.4's closing sentence
   is explicit that until all four steps are complete the file is *"the candidate it is"*. So the
   rule is **sharpened rather than broken**: `-RC` drops when **every** downstream phase doc has
   adopted the revision, not when the first one does.
2. **The directory name is not a maintainer's to change here.** By the rule at the head of this
   section, directory names come from the document's own header — and RC2's Status line (ll. 3–4)
   reads *"v2.0-RC2 … This file is `docs/design/v2.0-RC2/DESIGN.md`"*. Dropping the suffix would put
   the directory in contradiction with the document, and the only consistent fix would be editing
   `DESIGN.md`, which is evidence (§G1.1/§G1.2). **The label cannot move ahead of a design-document
   revision**, whatever the adoption state.

The same reasoning applies to RC1, RC3, and RC4. RC1 remains unread historical evidence; RC3 is
adopted by Phases 3–9 while Phase 1 stays on RC2 and Phase 2 stays on v1.1. RC3 therefore remains
`v2.0-RC3`: partial adoption cannot move the label ahead of the design document's own header. RC4
remains `v2.0-RC4` because it is unadopted and its own header declares that candidate label. REV5
is now `v3` by an explicit maintainer-directed promotion that changed its header and directory
together; that identity change did not adopt it for any phase.

## What was deliberately left stale

- The **15 `.txt` session exports** in `docs/phase1/chatlogs/` are byte-identical to before. They are
  verbatim records.
- One `[RESEARCH.md](RESEARCH.md)` link in `docs/reference/pintonium/chatlogs/` — it appears inside a
  quoted dump of a *different repository's* `DESIGN.md` (`/home/nick/Documents/Pintonium/`), so
  repointing it into this tree would be wrong rather than consistent.
- **`docs/decisions/OQ-3_GL_CONTEXT.md` and `docs/decisions/OQ-4_CLEANMIX_HOOKS.md` are deliberate
  forward references.** Phase 7 names them as the future outputs of its two implementation-time
  spikes; neither decision has been run or authored yet. Both references already existed before
  the 2026-08-03 RC4 candidate work and must not be “resolved” by inventing decision outcomes.
- **`docs/phase1/v10/PHASE_1_DOC.md`, stranded by the `v10` → `v11` roll (2026-07-26).** Cited at
  **15 sites across 14 files**: `PHASE_1_REVIEW_1.md`…`_10.md` (l. 3 of each), the three briefs
  (`PHASE_1_FIXUP_6_BRIEF.md` ll. 2 and 194, `PHASE_1_REVIEW_9_BRIEF.md` l. 2,
  `PHASE_1_REVIEW_10_BRIEF.md` l. 2) and `PHASE_2_DOC.md` §0.1 l. 30 — plus the gitignored
  `docs/phase1/chatlogs/` transcript. **None was repointed, deliberately.** Reviews and their
  `## Resolutions` are evidence (§G1.1/§G1.2); the briefs and `PHASE_2_DOC.md` §0.1 are records of
  what a session was *actually given*, and `PHASE_1_DOC.md` §0.10's head note settles the principle
  for this project — a stale coordinate in a historical record is a smaller defect than a rewritten
  record. Note `PHASE_1_DOC.md` is **not** in the list: its l. 1051 quotes the `git mv` command from
  this file, which carries no filename, so the regex does not match it.
- **`docs/phase1/v11/PHASE_1_DOC.md`, stranded by the direct `v11` → `v13` catch-up roll
  (2026-07-26).** Cited at **15 sites across 3 files**: `PHASE_1_REVIEW_12.md` at ll. 4, 38, 526,
  809, 834, 844, 852 and 1163; `PHASE_1_REVIEW_13.md` at ll. 4, 40, 659, 689, 700 and 1002; and
  `PHASE_1_DOC.md` §0.11 at l. 1071. **None was repointed, deliberately.** The reviews and their
  `## Resolutions` are immutable evidence, and §0.11 records the path that the earlier session
  actually used. Moving the document without editing it preserves that historical record and its
  checksum.
- **`docs/phase1/v13/PHASE_1_DOC.md`, stranded by the `v13` → `v14` post-loop roll
  (2026-07-26).** Cited at **7 sites across 2 files**: `PHASE_1_REVIEW_14.md` at ll. 3, 17, 459 and
  606, and `PHASE_1_REVIEW_15.md` at ll. 3, 19 and 281. **None was repointed, deliberately.** Both
  reviews are immutable §G1.1/§G1.2 evidence, and those paths record the artifact each session
  actually reviewed.

The acceptance check (`--exclude=MOVES.md` because this manifest's old-path column dangles by
definition — exclusion added 2026-07-26, after confirming the unexcluded form's 13 self-hits were
exactly this file's old paths and nothing else):

```bash
grep -rhoE 'docs/[A-Za-z0-9._/-]+\.md' docs --include='*.md' --exclude=MOVES.md | sort -u | while read p; do
  [ -f "$p" ] || echo "DANGLING: $p"; done
```

**As re-audited while promoting v3 on 2026-08-03, "clean" means exactly five lines, not zero:**

```
DANGLING: docs/decisions/OQ-3_GL_CONTEXT.md
DANGLING: docs/decisions/OQ-4_CLEANMIX_HOOKS.md
DANGLING: docs/phase1/v10/PHASE_1_DOC.md
DANGLING: docs/phase1/v11/PHASE_1_DOC.md
DANGLING: docs/phase1/v13/PHASE_1_DOC.md
```

The two pending decision artifacts plus the three historical citation sets — 15 citations for
`v10`, 15 for `v11` and 7 for `v13` — dedup to those five entries through `sort -u`, so the recorded
expectation stays a five-liner however many immutable records cite them, and **any sixth line is a
real regression.** The sweep command is deliberately left unchanged: adding exclusions for reviews,
briefs, or planned outputs would make the result look empty, but an exclusion list that grows each
time something is legitimately absent is how genuine dangling references stop being noticed.

Only after a future §0.15 fix-up lands and its loop exits is the next illustrative roll `v14` →
`v15`. If that roll strands a sixth path, extend the expected list rather than the exclusions, and
say which roll each entry came from.
