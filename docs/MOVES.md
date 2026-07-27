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

The cost of that trade is one collision: **four files are now named `DESIGN.md`** (the third and
fourth joined 2026-07-26). They are different documents. See the warning below.

## Documents

| Old path | New path |
|---|---|
| `docs/project/DESIGN.md` | `docs/design/v1.1/DESIGN.md` |
| `docs/pintonium/DESIGN_PINTONIUM_REV1.md` | `docs/design/v2.0-RC1/DESIGN.md` |
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

## ⚠️ Four files named `DESIGN.md`

| | `docs/design/v1.1/DESIGN.md` | `docs/design/v2.0-RC1/DESIGN.md` | `docs/design/v2.0-RC2/DESIGN.md` | `docs/design/v2.0-RC3/DESIGN.md` |
|---|---|---|---|---|
| Lines | 1,586 | 2,304 | 2,478 | 2,656 |
| Status | **governs Phase 2**; anchors Phase 1 reviews through round 11 | superseded; read by nothing | **governs Phase 1** from its §0.11 onward | **governs Phase 3 only** from its initial build; partial adoption retains `-RC` |
| Phase 1 spec at | l. 585 | l. 829 | l. 957 | l. 1,120 |
| Phase 2 spec at | l. 662 | l. 933 | l. 1,071 | l. 1,234 |
| §G1.2 at | l. 118 | l. 174 | l. 257 | l. 276 |

*(2026-07-26: the RC1 column previously read 2,300 / 825 / 929 / 170 — off by four against the file
on disk since it was recorded; corrected while adding the RC2 column.)*

**There is no longer one governing revision, and no longer one set of line numbers** (changed
2026-07-26, §G0.4 steps 1–2 and 4; Phase 3 adoption recorded 2026-07-27). Phase docs cite
`DESIGN.md` **by line number**, and the three current phase docs are anchored per phase:
`PHASE_1_DOC.md` l. 12 declares
`docs/design/v2.0-RC2/DESIGN.md` from its §0.11 onward, adopted at the round-eleven fix-up as §G0.4
step 3; `PHASE_2_DOC.md` §0.1 still cites v1.1 (the Phase 2 spec at ll. 662–720) because step 3 has
not been run for it; and Phase 3 deliberately adopts RC3 from its initial build. Every Phase 1
review through round 11 is in v1.1's coordinates.

The `/verify-loop` harness therefore resolves the revision **per phase** rather than globally:
`design` in each `PHASE_FACTS` row names it, and `DESIGN_PINS` in
`.claude/workflows/verify-loop.js` holds one complete pin set per revision selected by
`PHASE_FACTS` — **12 section→line mappings for v1.1, 13 for RC2, and 14 for RC3**, plus the §G9
range the doc-gate lens quotes and each row's `spec`/`docGate`. `PHASE_FACTS[3]` selects RC3 with
Phase 3 spec `1316–1470`, Doc gate `1451–1457`, OQ-7, and dependency `[1]`.
(Before this change there was a single `DESIGN` constant and 17 hardcoded v1.1 numbers spread
across the file; §G0.4 step 1 and the earlier text here both said "~16", which matched no literal
count.)

**Pointing a phase at the wrong revision would not raise an error — it would silently feed every
agent the wrong text.** The old pins make the point: at v2.0-RC1 ll. 649–652 the doc-gate lens read
§G9's template, which looks entirely plausible; at v2.0-RC2 the same range lands in §G6's
conformance-tier text, equally plausible. So adopting a new or moved revision means
**re-deriving its whole pin set from its own headings** — never shifting another revision's numbers
by an offset. The procedure is v2.0-RC2 §G0.4, and its step 1 is exactly this.

## Version labels

Directory names come from each document's own header, not from the folder it used to sit in:

| Document | Directory | Evidence |
|---|---|---|
| `DESIGN.md` | `v1.1` | header states v1.1; REV1 names "v1.1" as what it supersedes |
| REV1 | `v2.0-RC1` | header states v2.0; recorded as RC because no downstream doc has adopted it |
| REV2 | `v2.0-RC2` | header states v2.0-RC2 (created 2026-07-26, not a move); supersedes RC1, which stays for history. **`-RC` retained after partial adoption — see the ruling below** |
| REV3 | `v2.0-RC3` | header states v2.0-RC3 (created 2026-07-26, not a move); **governs Phase 3 only** — RC2 still governs Phase 1 and v1.1 still governs Phase 2; partial adoption retains `-RC` |
| `RESEARCH.md` | `v1` | "first complete draft"; no version stated |
| `PINTONIUM_DESIGN.md` | `v1.0` | header states v1.0 |
| `PHASE_1_DOC.md` | `v14` | fix-up addenda §0.4–**§0.14**; `PHASE_1_REVIEW_14.md` has complete `## Resolutions`, and round fifteen returned a literal PASS with no further fix-up. Rolled from `v13` on 2026-07-26 after that loop exited |
| `PHASE_2_DOC.md` | `v1` | initial build session, zero review rounds |
| `PHASE_3_DOC.md` | `v1` | initial build under RC3; rolls only after a fix-up addendum and a later literal PASS |

**Rolling a phase doc's version** (`v14` → `v15` only once a future §0.15 fix-up lands) is two steps,
run together and only **after** that `/verify-loop` run exits:

```bash
git mv docs/phase1/v14 docs/phase1/v15
# then bump docVersion in PHASE_FACTS in both byte-identical workflow scripts
```

`v<K>` = the highest `§0.K` fix-up addendum in the doc. Never roll mid-loop: the doc path is
computed once at startup, so a rename between rounds points every later round at a directory that no
longer exists.

*The `v10` → `v11` roll was performed 2026-07-26, both steps together, with no loop running. It cost
one dangling reference, recorded below.*

*The direct `v11` → `v13` roll was performed 2026-07-26 as a two-overdue-roll catch-up, both steps
together, with no loop running and no temporary `v12` directory. It catches the directory up to the
already-applied §0.12 and §0.13 addenda and costs one additional dangling reference, recorded below.*

*The `v13` → `v14` roll was performed 2026-07-26, both steps together and with no loop running,
after round fifteen's literal PASS confirmed the round-fourteen fix-up. It catches the directory up
to the already-applied §0.14 addendum and costs one additional dangling reference, recorded below.*

### The `-RC` suffix after a partial adoption — ruling, 2026-07-26

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

The same reasoning applies to RC1 and RC3. RC1 remains unread historical evidence; RC3 is adopted
only by Phase 3 while Phase 1 stays on RC2 and Phase 2 stays on v1.1. RC3 therefore remains
`v2.0-RC3`: partial adoption cannot move the label ahead of the design document's own header.

## What was deliberately left stale

- The **15 `.txt` session exports** in `docs/phase1/chatlogs/` are byte-identical to before. They are
  verbatim records.
- One `[RESEARCH.md](RESEARCH.md)` link in `docs/reference/pintonium/chatlogs/` — it appears inside a
  quoted dump of a *different repository's* `DESIGN.md` (`/home/nick/Documents/Pintonium/`), so
  repointing it into this tree would be wrong rather than consistent.
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

**Since the `v13` → `v14` post-loop roll on 2026-07-26, "clean" means exactly three lines, not zero:**

```
DANGLING: docs/phase1/v10/PHASE_1_DOC.md
DANGLING: docs/phase1/v11/PHASE_1_DOC.md
DANGLING: docs/phase1/v13/PHASE_1_DOC.md
```

The three historical citation sets — 15 citations for `v10`, 15 for `v11` and 7 for `v13` — dedup
to those three entries through `sort -u`, so the recorded expectation stays a three-liner however
many immutable records cite them, and **any fourth line is a real regression.** The sweep command is
deliberately left unchanged: adding `--exclude` for the reviews and briefs would make the output read
empty again, but an exclusion list that grows each time something is legitimately stale is how
genuine dangling references stop being noticed.

Only after a future §0.15 fix-up lands and its loop exits is the next illustrative roll `v14` →
`v15`. If that roll strands a fourth path, extend the expected list rather than the exclusions, and
say which roll each entry came from.
