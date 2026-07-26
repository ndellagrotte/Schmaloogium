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

The cost of that trade is one collision: **three files are now named `DESIGN.md`** (a third joined
2026-07-26). They are different documents. See the warning below.

## Documents

| Old path | New path |
|---|---|
| `docs/project/DESIGN.md` | `docs/design/v1.1/DESIGN.md` |
| `docs/pintonium/DESIGN_PINTONIUM_REV1.md` | `docs/design/v2.0-RC1/DESIGN.md` |
| `docs/project/RESEARCH.md` | `docs/research/v1/RESEARCH.md` |
| `docs/pintonium/PINTONIUM_DESIGN.md` | `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` |
| `docs/phase1/artifacts/PHASE_1_DOC.md` | `docs/phase1/v10/PHASE_1_DOC.md` |
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

The Pintonium directory name contained a space, which broke unquoted shell paths.

## Three basenames changed

Everything else kept its filename. These did not, each for a reason:

1. **`DESIGN_PINTONIUM_REV1.md` → `DESIGN.md`** — the version moved into the directory.
2. **`recommendation_doc.md` → `TEMPLATE_BRANCH_RECOMMENDATION.md`** — the old name said nothing;
   verified zero inbound references.
3. **`PHASE_1_REVIEW.md` → `PHASE_1_REVIEW_1.md`** — the harness computes
   `PHASE_<N>_REVIEW_<r>.md`, so round one was the only review it could not address. 40 citations
   updated.

## ⚠️ Three files named `DESIGN.md`

| | `docs/design/v1.1/DESIGN.md` | `docs/design/v2.0-RC1/DESIGN.md` | `docs/design/v2.0-RC2/DESIGN.md` |
|---|---|---|---|
| Lines | 1,586 | 2,304 | 2,478 |
| Status | **the governing design** | unadopted revision (superseded by RC2) | unadopted revision — the current candidate |
| Phase 1 spec at | l. 585 | l. 829 | l. 957 |
| Phase 2 spec at | l. 662 | l. 933 | l. 1,071 |
| §G1.2 at | l. 118 | l. 174 | l. 257 |

*(2026-07-26: the RC1 column previously read 2,300 / 825 / 929 / 170 — off by four against the file
on disk since it was recorded; corrected while adding the RC2 column.)*

Every phase doc and review to date verifies against **v1.1 by line number**, and the `/verify-loop`
harness is pinned there (`DESIGN` in `.claude/workflows/verify-loop.js`, plus ~16 section→line
mappings and the `spec`/`docGate` ranges in `PHASE_FACTS`).

**Re-pointing the harness at either v2.0 revision would not raise an error — it would silently feed
every agent the wrong text.** At v2.0-RC1 ll. 649–652 the doc-gate lens reads §G9's template, which
looks entirely plausible; at v2.0-RC2 the same range lands in §G6's conformance-tier text, equally
plausible. Moving off v1.1 means re-deriving every line number first — the adoption procedure is
v2.0-RC2 §G0.4.

## Version labels

Directory names come from each document's own header, not from the folder it used to sit in:

| Document | Directory | Evidence |
|---|---|---|
| `DESIGN.md` | `v1.1` | header states v1.1; REV1 names "v1.1" as what it supersedes |
| REV1 | `v2.0-RC1` | header states v2.0; recorded as RC because no downstream doc has adopted it |
| REV2 | `v2.0-RC2` | header states v2.0-RC2 (created 2026-07-26, not a move); RC for the same reason — no downstream doc has adopted it; supersedes RC1, which stays for history |
| `RESEARCH.md` | `v1` | "first complete draft"; no version stated |
| `PINTONIUM_DESIGN.md` | `v1.0` | header states v1.0 |
| `PHASE_1_DOC.md` | `v10` | fix-up addenda §0.4–§0.10 stop at round ten; `PHASE_1_REVIEW_11.md` has no `## Resolutions`, so round 11 is unapplied |
| `PHASE_2_DOC.md` | `v1` | initial build session, zero review rounds |

**Rolling a phase doc's version** (`v10` → `v11` once an eleventh fix-up lands) is two steps, run
together and only **after** a `/verify-loop` run exits:

```bash
git mv docs/phase1/v10 docs/phase1/v11
# then bump docVersion in PHASE_FACTS in .claude/workflows/verify-loop.js
```

`v<K>` = the highest `§0.K` fix-up addendum in the doc. Never roll mid-loop: the doc path is
computed once at startup, so a rename between rounds points every later round at a directory that no
longer exists.

## What was deliberately left stale

- The **15 `.txt` session exports** in `docs/phase1/chatlogs/` are byte-identical to before. They are
  verbatim records.
- One `[RESEARCH.md](RESEARCH.md)` link in `docs/reference/pintonium/chatlogs/` — it appears inside a
  quoted dump of a *different repository's* `DESIGN.md` (`/home/nick/Documents/Pintonium/`), so
  repointing it into this tree would be wrong rather than consistent.

Everything else resolves. The acceptance check (`--exclude=MOVES.md` because this
manifest's old-path column dangles by definition — exclusion added 2026-07-26, after
confirming the unexcluded form's 13 self-hits were exactly this file's old paths and
nothing else):

```bash
grep -rhoE 'docs/[A-Za-z0-9._/-]+\.md' docs --include='*.md' --exclude=MOVES.md | sort -u | while read p; do
  [ -f "$p" ] || echo "DANGLING: $p"; done
```
