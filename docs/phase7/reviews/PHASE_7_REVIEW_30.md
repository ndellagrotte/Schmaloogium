# Phase 7 verification review — Round 30

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the complete target at
`docs/phase7/v1/PHASE_7_DOC.md`, the manifest-selected `docs/design/v3/DESIGN.md` Part I, Phase 7
specification, document gate, and mandatory template, RESEARCH ground truth, and the binding §5
contracts of Phases 2–6. I settled each candidate's interpretation, severity, and interface
classification before reading `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_29.md`, in order and last, and then checked the candidates
against that settled material.

The selected v3 design revision is the supplied verification-only override; it does not rewrite
the target's declared adoption state. I did not read
`reference-src/schlorbium-HD_U_G6_pre1/files.txt`, because the resolved contract forbids every
`*.txt` source and it is immaterial to these dependency-interface candidates. There was no network
use, forbidden-transcript use, or agent fan-out. This was the canonical engine's already-dispatched
atomic adjudication role, so the supplied `verify-loop` instructions required completing only this
role without invoking the loop or delegating. There were no other reading-order deviations, no
candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — R7-1 through R7-3 remain active gates after Phase 5 granted them

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:2038`–`:2040`
- **Claim:** Phase 7 incorrectly represents three consumed Phase 5 capabilities as unavailable and
  consequently disables supported depth-copy, virtual-transition, and overlay behavior.
- **Evidence:** Phase 7 requests a corrected depth-copy order, a programless virtual-transition
  operation, and a balanced draw-buffers-none lease as R7-1 through R7-3, describing each as absent
  and feature-blocking (`docs/phase7/v1/PHASE_7_DOC.md:2038`–`:2040`). The binding Phase 5 contract
  now exposes the exact ordered
  `FRAME_BEGUN -> PRE_WEATHER -> PRE_TRANSLUCENT -> FRAME_COMMITTED` protocol
  (`docs/phase5/v1/PHASE_5_DOC.md:2011`), the exact programless
  `applyVirtualTransition` operation (`docs/phase5/v1/PHASE_5_DOC.md:2008`), and the balanced,
  generation/epoch/frame-checked `openDrawBuffersNone` lease
  (`docs/phase5/v1/PHASE_5_DOC.md:2009`). The stale assessment is operational rather than
  historical: §4.5 still says R7-1 must land and invokes the requested R7-2
  (`docs/phase7/v1/PHASE_7_DOC.md:872`–`:883`), while the hook catalog still gates H-OVERLAY-01 on
  R7-3 (`docs/phase7/v1/PHASE_7_DOC.md:1162`).
- **Required correction:** Synchronize the detailed design, hook catalog, §5 consumed-contract and
  request tables, blocker/staging prose, and implementation checklist with the exact current Phase
  5 types and semantics. Mark R7-1 through R7-3 granted (or retain them only as clearly satisfied
  history), remove their obsolete dependency gates, and preserve independent milestone gates such
  as v0.5 for actual copied-depth calls.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the stale dependency requests and consumed
  contract inventory are inside the manifest-declared §5 region.

### candidate-002 — R7-4 through R7-7 remain COMPLETE-manifest gates after their grants

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:2041`–`:2044`
- **Claim:** Phase 7's capture hand-off incorrectly classifies the canonical program projection,
  resource projection, replay-aware GL-error result, and runner provenance bridge as unavailable,
  thereby retaining false COMPLETE/T3 gates.
- **Evidence:** Phase 7 says dependencies do not expose all manifest projections and permits only a
  failure manifest until R7-4 through R7-7 land (`docs/phase7/v1/PHASE_7_DOC.md:1307`–`:1312`), then
  lists all four as pending requests (`docs/phase7/v1/PHASE_7_DOC.md:2041`–`:2044`). In contrast,
  Phase 4 exposes the complete immutable `ProgramRegistryView.resolutions()` projection directly to
  Phase 7's serializer (`docs/phase4/v1/PHASE_4_DOC.md:1566`), and Phase 5 exposes the exact canonical
  `resources.*` projection to Phase 7 (`docs/phase5/v1/PHASE_5_DOC.md:2005`). Phase 2 binds the
  capture-plan provenance fields and verbatim runner-owned bridge to Phase 7
  (`docs/phase2/v1/PHASE_2_DOC.md:1605`–`:1606`). It also assigns Phase 7 serialization from the
  accepted R4A result (`docs/phase2/v1/PHASE_2_DOC.md:1604`) and states explicitly that
  `ReplayAwareGLError` is granted, Phase 6 performs replay, and Phase 7 copies the result
  (`docs/phase2/v1/PHASE_2_DOC.md:1629` and `:1633`–`:1636`).
- **Required correction:** Revise §5 and every COMPLETE/T3 gating statement to consume the exact
  Phase 4 program projection, Phase 5 resource projection, Phase 2 provenance bridge, and granted
  replay-aware error flow. Remove R7-4 through R7-7 as pending requests (or retain them only as
  clearly satisfied history), update capture serialization and the checklist accordingly, and
  retain a blocker only for a separately identified contract that is genuinely ungranted.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the stale requests and dependency-consumption
  hand-off are in the declared §5 interface region.

## 2. Checked and clean

The finder-reported clean areas survived independent re-derivation apart from the two admitted
dependency-status defects. The header correctly points to §0.32; §0.31 remains the Round-28
binding-interface correction; §0.32 consistently describes Round 29 as metadata-only; and the
terminal status correctly identifies Round 28 as the latest §5 change while leaving v1 unverified.

The conformance map provides supported coverage for the frame-flow requirements, Appendix A.1
program families, hook needs 1–11, the seven-row injection timeline including split 3a/3b, all 18
Appendix E owner/deferral rows, the assigned engine flags, OQ-3/OQ-4 spikes, and the v0.1 assembly
narrative. No additional interface defect was established in the Phase 3 or Phase 6 consumption
rows, coordinated publication ordering, or Phase 8/9 downstream hand-offs.

Neither candidate is cleared by settled prior material. Earlier reviews' statements that these
requests remained gated predate the current dependency grants. Round 29's Gate drop of an R7-1
through R7-3 candidate was citation-specific and did not settle the present-byte contradiction;
the current Round 30 citations resolve. No candidate was refuted or cleared on re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded dependency-synchronization corrections and do not require structural
rebuilding. Relative to Round 29, corrections rise from one to two because the fresh interface
review exposes current dependency grants that the target still treats as pending; literal
convergence has not been reached and the result cannot be softened to PASS. The next required
action is a scoped fix-up of candidate-001 and candidate-002, including this review's
`## Resolutions` record and a new target addendum. Because both corrections change the
manifest-declared §5 interface region, a fresh whole-document and interface verification round is
required before Phase 7 can close. No version roll may occur until that loop exits.

## Resolutions

### candidate-001 — applied

Re-derived from the current Phase 5 binding rows, the ordered depth-copy protocol,
`applyVirtualTransition`, and `openDrawBuffersNone` are all granted. The target now consumes their
exact semantics in §3.6, §4.5–§4.6, the hook catalog, §5.2/§5.4, staging, blockers, and the
implementation checklist. R7-1 through R7-3 remain only as clearly marked granted history; all
obsolete dependency gates were removed. The independent v0.5 staging of actual depth-copy calls
is unchanged.

### candidate-002 — applied

Re-derived from the binding contracts, Phase 4 exposes the immutable complete
`ProgramRegistryView.resolutions()` projection, Phase 5 exposes the immutable canonical
`BufferResourceProjection`, and Phase 2 exposes both the authenticated capture-plan provenance
bridge and the accepted replay-aware GL-error flow. The capture design and §5 inventories now copy
those owner projections verbatim, with no parallel introspection. R7-4 through R7-7 remain only as
granted history, and the false failure-only, COMPLETE, and T3 dependency gates were removed while
ordinary Phase 2 predicate validation remains binding.

### Interface/change-trigger record

These corrections intentionally change the manifest-declared §5 cross-phase-interface region by
synchronizing its consumed-contract inventory and request-history table with the current Phase
2/4/5 grants. A fresh verification round is therefore required before Phase 7 can close.

### Notes deferred

None; the adjudicator admitted no notes.
