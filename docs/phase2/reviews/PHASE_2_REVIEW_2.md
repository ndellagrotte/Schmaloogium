# Schmaloogium — Phase 2: Conformance harness — Review Round 2

## 0. Method and reading order

I independently re-derived both gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, the document gate,
   and the mandatory template.
2. `docs/research/v1/RESEARCH.md`, including the mission and decisions and the conformance
   requirements in §§8–9.
3. The manifest-selected binding dependency,
   `docs/phase1/v14/PHASE_1_DOC.md`, with particular attention to its §5 contract, module seam,
   round-eleven addendum, and current §G1.3 status.
4. The whole target, `docs/phase2/v1/PHASE_2_DOC.md`.
5. Only after settling the candidates, the discovered prior review
   `docs/phase2/reviews/PHASE_2_REVIEW_1.md`.

There were no reading-list deviations. No network access was used. No subagents or other agent
fan-out were started in this adjudication role; the canonical engine supplied the finder,
refuter, and Gate results. The Gate reported no drops, and there were no candidates eliminated
before adjudication. Forbidden sources were not read.

The prior review's PASS does not clear either present candidate. It was read last and repeatedly
treated Phase 1's then-pending V11-1 state as current. The manifest now selects Phase 1 v14, whose
own addendum and closing status provide materially newer dependency evidence. The prior review
also asserted that no assumed interface existed, but did not settle the concrete compilation
direction of the `FrontEndProjection` declaration against C-4.

## 1. Findings

### candidate-001 — Phase 1 dependency path and live gating status are stale

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:1718–1728`
- **Claim:** The live §11 open-items ledger accurately identifies the current binding Phase 1
  dependency and work still owed on it.
- **Evidence:** Phase 2 says the dependency is under `docs/phase1/v10/` and that Phase 1 remains
  open until V11-1's fix-up and ensuing re-verification run
  (`docs/phase2/v1/PHASE_2_DOC.md:1718–1728`). The manifest-selected dependency instead records
  that V11-1 was applied (`docs/phase1/v14/PHASE_1_DOC.md:942–945`) and states that the document
  is verified and valid for dependency consumption at v14
  (`docs/phase1/v14/PHASE_1_DOC.md:5109–5113`). Section 11.3 presents these statements as current
  open-item status, not merely as an authoring-time history.
- **Disposition:** Admitted. Update §11.3 items 2–3 to resolve the dependency through
  `docs/phase1/v14/PHASE_1_DOC.md`, state that V11-1 was applied and its verification chain
  completed, and remove the claim that this work remains open. Historical §0.3 context may remain
  if it is explicitly identified as historical.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-002 — `FrontEndProjection` reverses the binding module dependency

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:1040–1054`, repeated in
  `docs/phase2/v1/PHASE_2_DOC.md:1219–1221`
- **Claim:** Phase 3 can implement the Phase 2-declared `FrontEndProjection` in `:engine` while
  respecting the binding module seam.
- **Evidence:** Phase 2 correctly states C-4 as `:conformance` depending on `:engine`, never on
  `:mod` (`docs/phase2/v1/PHASE_2_DOC.md:152–156`). It then declares
  `FrontEndProjection` in `com.schmaloogium.conformance.golden`, gives it a conformance-owned
  `GoldenDocument` return type, and requires Phase 3 to implement it in `:engine`
  (`docs/phase2/v1/PHASE_2_DOC.md:1040–1054`). That implementation would make `:engine`
  reference `:conformance`, creating the reverse edge and a project dependency cycle. Phase 1's
  binding §5 exposes both the three-module layout and seam constraints to all phases
  (`docs/phase1/v14/PHASE_1_DOC.md:3962–3966`). No adapter or engine-owned projection contract
  elsewhere in Phase 2 removes the reverse reference. The faulty obligation is repeated inside
  Phase 2's binding §5 interface region (`docs/phase2/v1/PHASE_2_DOC.md:1219–1221`).
- **Disposition:** Admitted. Redesign this boundary so Phase 3 exposes an `:engine`-owned API and
  data model while a `:conformance`-side adapter maps it to `GoldenDocument` and, if retained,
  implements `FrontEndProjection`. Update §4.11.4, §5, R5, staging, and checklist references so
  no `:engine` type implements or references a `:conformance` type.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The conformance lens found no unmapped in-scope requirement: all seven pack rows, all four tiers,
the §8.3 harness requirements, §9 exit criteria, behavioral scene families, fixture constraints,
and OQ-10 have substantive mappings. Outside the admitted dependency-status defect, repeated run
IDs, decision IDs, milestone counts, OQ-10 references, and checklist numbering were consistent.
All other examined Phase 1 consumption rows were represented in Phase 2 §5.2 and aligned with the
selected binding contract. The remaining harness artifacts and downstream requests were connected
to substantive definitions.

Both candidates survived independent re-derivation; none was refuted or cleared. The Gate dropped
none. No additional finding is created from the candidate-free clean areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded fix-up work rather than structural omissions requiring a rebuild.
Candidate-001 corrects stale dependency bookkeeping outside §5. Candidate-002 changes the binding
cross-phase interface region, so the fix-up must update every corresponding promise consistently.

There is no usable prior-round convergence trend: round 1 reported PASS, but the current round
admits two newly re-derived corrections against the manifest-selected dependency, including one
interface correction. The next required action is a fix-up resolving both findings and recording
their resolutions. Because §5 must change, a fresh verify round is required before Phase 2 can
close or its interface can be consumed as verified.

## Resolutions

### candidate-001 — resolved

Re-derived against the manifest-selected dependency rather than treating the finding as evidence.
`docs/phase1/v14/PHASE_1_DOC.md` records V11-1 as applied and closes Phase 1 as verified for
dependency consumption. Phase 2 §11.3 items 2–3 now name the v14 document, state that the
verification chain completed, and remove the open-gate claim. Section 0.3 item 6 remains only as
explicitly labelled authoring-time history and points to the corrected live status.

### candidate-002 — resolved

The original signature put a `conformance.golden.GoldenDocument` return type on an interface that
`:engine` was required to implement, contradicting the binding one-way `:conformance` → `:engine`
edge. Section 4.11.4 now requires Phase 3 to own both an engine API and its immutable snapshot data
model; every type in that signature belongs to `:engine`. A Phase 2-owned
`GoldenProjectionAdapter` in `:conformance` consumes the snapshot and maps it to
`GoldenDocument`, so no engine type references or implements a conformance type.

The same direction is now reflected in the package table, artifact flow, §5 exposure and R5
contract, R8/R10 terminology, staging, week-one subset, Phase 3 handoff, and implementation
checklist. The adapter is staged only once Phase 3's engine API exists; the week-one golden
skeleton continues to use hand-built `GoldenDocument`s and therefore remains independently
compilable. Because binding §5 changed, a fresh verify round is required before Phase 2 closes.

### Notes deferred

None; the adjudication admitted no notes.
