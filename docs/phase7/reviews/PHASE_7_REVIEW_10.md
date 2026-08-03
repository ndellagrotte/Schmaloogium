## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, the binding Phase 3 interface, and the cited target
passages. Only after settling those judgments did I read
`docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_9.md`, in order and last. Round 9 established R7-9 and its
production gate, but did not settle the downstream action-list omissions now under review.

There were no reading-order deviations, no network use, no forbidden source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
`verify-loop` instructions required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — R7-9 is omitted from the downstream dependency and implementation action lists

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1739`–`:1781`
- **Claim:** The Round 9 correction does not consistently propagate R7-9's mandatory dependency
  gate into the document's decision summary, requested-upstream-change inventory, and ordered
  implementation checklist.
- **Evidence:** The binding contract requires the Phase 3 canonical path projection and expressly
  gates internal-pack manifest/digest production until R7-9 is granted and reverified
  (`docs/phase7/v1/PHASE_7_DOC.md:1293`–`:1296`). Section 5.4 likewise defines R7-9 and states that
  it blocks internal-pack manifest/digest production (`docs/phase7/v1/PHASE_7_DOC.md:1482`–`:1487`).
  The later open-items summary lists R7-1 through R7-8 but omits R7-9
  (`docs/phase7/v1/PHASE_7_DOC.md:1739`–`:1744`), and the requested-upstream-change inventory still
  ends its governed range at R7-8 (`docs/phase7/v1/PHASE_7_DOC.md:1765`–`:1766`). Most materially,
  checklist item 5 orders implementation of the canonical manifest digest, while the prerequisite
  item names R7-1 through R7-3 and R7-8 but not R7-9
  (`docs/phase7/v1/PHASE_7_DOC.md:1777`–`:1781`). A consumer following the action list can therefore
  reach gated digest work before the required Phase 3 grant and reverification.
- **Required correction:** Add R7-9 to §11.3's dependency-gate summary, extend §11.5's dependency
  request range through R7-9, and make the Phase 3 R7-9 grant and any owed reverification an explicit
  prerequisite to checklist item 5. Preserve the existing §5 R7-9 contract unchanged.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the defect and required synchronization are in
  §§11–12, outside the manifest-selected §5 interface region; the binding R7-9 contract need not
  change.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The new canonical digest definition itself
is coherent: it fixes unsigned UTF-8 byte ordering and prefix handling, uses one order for snapshot,
manifest, and digest traversal, requires exact UTF-8 encoding, prohibits implicit `toString()`, and
correctly gates production on R7-9. The remaining selected Phase 2–6 dependency consumptions are
honest. The conformance map continues to cover the governing frame flow, every Appendix A.1 program
family, all eleven hook needs, all seven reference-timeline rows, the assigned engine flags, and
OQ-3/OQ-4.

`candidate-002` is dropped on independent re-derivation. The declarations uniquely determine the
member owner: `FrameOpenResult.Opened` carries only a `FrameToken`, whereas
`ScopeOpenResult.Opened` alone declares `DrawDisposition draw`
(`docs/phase7/v1/PHASE_7_DOC.md:1085`–`:1106`). The challenged prose describes drawing through an
acquired shader scope and follows the step/scope outcome discussion
(`docs/phase7/v1/PHASE_7_DOC.md:1302`–`:1310`). Thus `Opened.draw` is a compact but uniquely
resolvable reference to `ScopeOpenResult.Opened.draw`; it does not assign `draw` to the frame-open
variant or force an implementing consumer to invent dispatch semantics. Qualification would be an
editorial clarification, not a required contractual correction. No prior review supplies a settled
disposition that changes this result.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The one admitted defect is a local execution-order and inventory inconsistency; it does not require
structural rebuilding or a change to the binding interface. The recent correction trend is
2, 1, 2, 1, 2, 1 across Rounds 5–10. Round 10 numerically improves on Round 9, but the sequence is
not strictly decreasing and literal convergence has not been reached while one correction remains.
The next required action is a scoped fix-up of this review, including its `## Resolutions` record
and Phase 7 addendum, followed by a fresh whole-document verification round. The correction itself
does not fire the §5 interface change trigger; any fix-up that changes §5 would do so independently.

## Resolutions

### candidate-001 — applied

Re-derived from the existing §5.4 R7-9 row and the canonical digest gate in §5.1. Section 11.3 now
names the Phase 3 grant and reverification as the blocker for internal-pack manifest/digest
production; §11.5 extends the governed dependency-request inventory through R7-9; and checklist
item 1 makes that grant and reverification a prerequisite to item 5. The binding §5 contract was
not changed, so the `cross-phase-interfaces` region remains byte-identical and its fresh-review
change trigger does not fire.

### Notes deferred

None. The adjudication admitted no notes.
