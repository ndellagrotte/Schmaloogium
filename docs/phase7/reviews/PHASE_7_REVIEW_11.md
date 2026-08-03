## 0. Method and reading order

I independently re-derived all three Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH material, the binding §5
regions of Phases 2–6, and the cited target and dependency passages. Only after settling those
judgments did I read `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_10.md`, in order and last. Round 10 established the intended
R7-9 synchronization but did not settle either remaining action-list inconsistency or the separate
conformance-map provenance omission.

There were no reading-order deviations, no network use, no forbidden source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
supplied `verify-loop` instructions required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — The dependency-ordered v0.1 assembly omits R7-9 before digest work

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1613`–`:1616`
- **Claim:** Section 9.1's dependency-ordered assembly can reach internal-pack manifest/digest
  production and its golden test without first obtaining and reverifying the Phase 3 R7-9 grant.
- **Evidence:** The binding digest contract says manifest/digest production is gated until R7-9 is
  granted and reverified (`docs/phase7/v1/PHASE_7_DOC.md:1298`–`:1301`), and the blocker summary
  repeats that rule (`docs/phase7/v1/PHASE_7_DOC.md:1747`–`:1749`). The ordered implementation
  checklist also places R7-9 before the work item that implements and golden-tests the canonical
  digest (`docs/phase7/v1/PHASE_7_DOC.md:1783`–`:1787`). In contrast, §9.1 expressly presents its
  sequence as being “in dependency order” but names only R7-8 and the applicable R7-1…R7-3 grants
  before row 3 supplies the internal pack and runs the headless internal golden
  (`docs/phase7/v1/PHASE_7_DOC.md:1607`–`:1616`). Accurate controls elsewhere expose but do not
  repair this contradictory implementation sequence.
- **Required correction:** Amend §9.1 before or within row 3 to require the R7-9 grant and Phase 3
  owner reverification before internal-pack manifest/digest production and its golden test. Preserve
  the document's narrower rule that ordinary internal-pack supply/load is not itself blocked by
  R7-9.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the correction belongs to §9.1 and need not
  alter the binding §5 contract.

### candidate-002 — Checklist item 1 gates internal-pack work on unrelated feature grants

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1783`
- **Claim:** The ordered implementation checklist incorrectly makes R7-1 through R7-3 prerequisites
  to item 5 rather than gates only for their named optional features.
- **Evidence:** The binding blocker summary says R7-1 through R7-3 block only their named feature
  claims, while R7-8 blocks package placement and R7-9 blocks internal-pack manifest/digest
  production (`docs/phase7/v1/PHASE_7_DOC.md:1489`–`:1492`). Section 11.3 repeats the precise split:
  R7-1 governs copied depth, R7-2 virtual pre flips, R7-3 draw-buffers-none overlays, and R7-9 the
  internal manifest/digest (`docs/phase7/v1/PHASE_7_DOC.md:1744`–`:1749`). Checklist item 1 instead
  grammatically requires “R7-1…R7-3, R7-8, and Phase 3's R7-9 before item 5”
  (`docs/phase7/v1/PHASE_7_DOC.md:1783`–`:1787`). Because this is an ordered, actionable checklist,
  the statement can unnecessarily block item 5 on three unrelated feature grants; correct prose
  elsewhere does not neutralize it.
- **Required correction:** Split the prerequisites precisely: R7-8 gates package placement, R7-9
  plus Phase 3 owner reverification gates item 5's manifest/digest production, and R7-1 through R7-3
  gate only their corresponding copied-depth, virtual-pre-flip, and overlay work.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the faulty checklist is outside §5, and the
  correction need not change any dependency request.

### candidate-003 — The engine-flag conformance rows omit mandatory provenance tags

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:424`–`:433`
- **Claim:** The eight §3.5 contract-map rows cite sources but fail the mandatory per-row requirement
  for an explicit provenance tag.
- **Evidence:** The governing template requires every in-scope contract item to map to both its
  satisfying design element and a provenance tag, with zero unmapped rows
  (`docs/design/v2.0-RC3/DESIGN.md:804`–`:806`). The eight §3.5 provenance cells contain Phase 3
  owner-row citations and “App F.1,” but none contains an explicit confidence/provenance tag such as
  `[V:doc]` (`docs/phase7/v1/PHASE_7_DOC.md:424`–`:433`). RESEARCH identifies Appendix F as a
  documented-contract catalog and marks it `[V:doc]` (`docs/research/v1/RESEARCH.md:1434`–`:1443`).
  Explicit tags on other §3 rows cannot satisfy the template's per-row requirement for these
  separately mapped engine flags.
- **Required correction:** Add the applicable explicit provenance tag, apparently `[V:doc]`, to
  each §3.5 provenance cell while retaining its Phase 3 owner-row and Appendix F.1 citations.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the required edits are confined to §3.5.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. R7-9 is consistently defined in §§5.1,
5.4, 11.3, and 11.5 as the Phase 3-owned canonical path-projection dependency, and those sections
correctly require its grant and owner reverification before manifest/digest production. The §5
interface remains internally coherent and does not require alteration for any admitted finding.

The selected Phase 2–6 binding contracts yielded no additional interface mismatch. The conformance
audit found semantically aligned mappings for frame flow, all Appendix A.1 program families, hook
needs 1–11, the seven-row reference timeline, and the complete assigned engine-flag slice; the only
admitted conformance defect is the missing explicit provenance tags on the eight §3.5 rows. No
candidate was refuted or cleared on independent re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: no

All three admitted defects are local synchronization or mandatory-template corrections; none
requires structural rebuilding or a change to the cross-phase interface region. The recent
correction trend is 2, 1, 2, 1, 2, 1, 3 across Rounds 5–11. Round 11 worsens the numerical count,
the sequence is not strictly decreasing, and literal convergence has not been reached. The next
required action is a scoped fix-up of this review, including its `## Resolutions` record and Phase 7
addendum, followed by a fresh whole-document verification round. If that fix-up changes §5 despite
the scoped corrections above, the interface change trigger applies independently.

## Resolutions

### candidate-001 — applied

Section 9.1 now places the R7-9 grant and Phase 3 owner reverification before row 3's canonical
manifest/digest production and headless golden. It continues to permit ordinary internal-pack
supply/load independently of R7-9, preserving the narrower binding rule in §§5.4 and 11.3.

### candidate-002 — applied

Checklist item 1 now gates package placement on R7-8, item 5 manifest/digest production on R7-9
and Phase 3 owner reverification, and only the named copied-depth, virtual-pre-flip, and overlay
work on R7-1, R7-2, and R7-3 respectively. Section 9.1 uses the same feature-specific split.

### candidate-003 — applied

Each of the eight §3.5 provenance cells now carries the explicit `[V:doc]` tag warranted by
RESEARCH Appendix F's documented-contract provenance, while retaining its Phase 3 owner-row and
Appendix F.1 citations.

### Notes deferred

None. The adjudicator admitted no notes.

### Fix-up status

All three admitted corrections were applied. The cross-phase §5 interface region is byte-identical,
so its change trigger did not fire. A fresh whole-document verification round is still required.
