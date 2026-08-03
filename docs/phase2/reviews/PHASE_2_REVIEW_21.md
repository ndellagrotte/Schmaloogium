# Schmaloogium — Phase 2: Conformance harness — Review Round 21

## 0. Method and reading order

I independently re-derived the sole gated candidate before reading any prior review. I read the
manifest-selected target, `docs/phase2/v1/PHASE_2_DOC.md`; the governing Part I, Phase 2 target
specification, document gate, and mandatory template in `docs/design/v1.1/DESIGN.md`; the contract
ground truth in `docs/research/v1/RESEARCH.md`; the selected binding dependency in
`docs/phase1/v14/PHASE_1_DOC.md`; and the supplied supporting CI evidence under
`.github/workflows/`. Only after settling the candidate did I read
`docs/phase2/reviews/PHASE_2_REVIEW_1.md` through `PHASE_2_REVIEW_20.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. Forbidden
sources were not read. The Gate reported no drops, and no candidate was eliminated before
adjudication.

The prior reviews were considered only after independent judgment. Round 20 admitted the stale
closing history and diagnostic-wire omissions, and its resolution claims to have advanced the
closing history through the §0.21 fix-up. It does not settle the present candidate because the
on-disk closing paragraph still contains the pre-fix-up wording.

## 1. Findings

### candidate-001 — Closing verification history still treats round 20 as current and §0.21 as pending

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:177–179,2229–2231`
- **Claim:** The §0.21 fix-up corrected the closing review history to reflect completion of round
  20 and the start of round 21.
- **Evidence:** Section 0.21 says, “Completed the Phase-2-owned diagnostic wire grammar and
  corrected the closing review history” (`docs/phase2/v1/PHASE_2_DOC.md:177–179`). The designated
  closing paragraph instead says, “Round 20 is the current fresh whole-document review” and that
  it “requires the §0.21 fix-up” (`docs/phase2/v1/PHASE_2_DOC.md:2229–2231`). Those statements
  cannot both describe the current surface. The latter also conflicts with this manifest-resolved
  round-21 adjudication.
- **Disposition:** Admitted. Update the closing history to state that round 20 reviewed the §0.20
  surface and required the non-interface §0.21 correction, and that round 21 is the current fresh
  whole-document review of the corrected surface.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported diagnostic-wire surface is internally consistent across the §4.5.4 summary,
record fields, scalar and closed value domains, absence encodings, golden-document vocabulary, and
§5 requests. It remains Phase-2-owned, does not claim consumption of Phase 1's ungranted
`EngineDiagnostic`, `DiagnosticSeverity`, or `UserChannel` types, and leaves R4B explicitly
unaccepted. The interface audit found Phase 2's declared Phase 1 consumptions supported by the
selected binding contract and found the downstream contracts implementable directly or through
their precise references. The conformance audit found the Phase 2 objective and scope, all
Appendix G packs, T0–T3 tiers, harness requirements, initial scene families, fixture policy,
before-renderer subset, OQ-10 spike, and every RESEARCH.md §9 exit criterion mapped to substantive
design elements and named runs. No correction to the designated §5 interface region was
identified.

The sole candidate survives independent re-derivation. Section 0.21's accurate addendum does not
cure the contradictory designated closing status, and Round 20's resolution describes an edit
that is absent from the current target bytes. The Gate dropped none, no candidate was cleared, and
no finding is created from candidate-free clean areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted defect is a localized verification-history correction rather than a structural miss
requiring rebuild. It is outside the declared cross-phase interface/change-trigger region, and the
underlying diagnostic-wire correction is otherwise coherent.

Round 19 admitted one correction and Round 20 admitted two; Round 21 admits one residual correction
from Round 20's claimed closing-history fix. The count has declined, but the loop has not converged
to literal PASS because the on-disk status remains stale. The next required action is a scoped
fix-up resolving candidate-001 and recording its resolution in this review, followed by the normal
fresh whole-document review. Because §5 does not change, the interface change trigger does not
fire.

## Resolutions

### candidate-001 — Resolved

Re-derived the status from the target's addendum sequence and this review's place in the loop. The
closing paragraph in `docs/phase2/v1/PHASE_2_DOC.md` now states that round 20 reviewed the §0.20
surface and required the non-interface §0.21 correction, and that round 21 is the current fresh
whole-document review of the corrected surface requiring this §0.22 fix-up. The compact §0.22
addendum records only that localized history correction. No §5 text or declared interface region
changed.

### Notes deferred

None.
