# Phase 2 verification review — round 26

## 0. Method and reading order

I first re-derived both candidates from the manifest-selected whole target,
`docs/phase2/v1/PHASE_2_DOC.md`; the governing Part I, Phase 2 target specification, document gate,
and mandatory template in `docs/design/v1.1/DESIGN.md`; the contract ground truth in
`docs/research/v1/RESEARCH.md`; the supporting CI evidence under `.github/workflows`; and the
binding Phase 1 contract in `docs/phase1/v14/PHASE_1_DOC.md`. Only after settling those independent
judgments did I read the discovered prior reviews,
`docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
`docs/phase2/reviews/PHASE_2_REVIEW_25.md`, in round order, and compare the candidates with settled
findings and resolutions.

There were no reading-order deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. No
forbidden source was consulted. The canonical engine reported no candidates eliminated before
adjudication and no Gate drops.

Prior review 25's resolution says it advanced the closing paragraph through round 25 and added
§0.26. That does not clear the current defect because the on-disk paragraph remains at the
pre-§0.26 lifecycle state. The two supplied candidates have the same location, claim, evidence,
severity, interface classification, and remedy; they represent one defect rather than two.

## 1. Findings

### candidate-001 — Closing verification history was not advanced for round 26

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:197–199,2250–2252`
- **Claim:** The §0.26 fix-up claims to have advanced the closing verification history, but the
  operative closing marker still identifies round 25 as current and §0.25 as the latest applied
  correction.
- **Evidence:** Section 0.26 is headed “Round-25 fix-up” and says it “Corrected the v0.3
  terrain-scene gate and advanced the closing verification history”
  (`docs/phase2/v1/PHASE_2_DOC.md:197–199`). The closing marker instead says round 24 reviewed the
  §0.24 surface and required §0.25, and that round 25 “is the current fresh whole-document review”
  (`docs/phase2/v1/PHASE_2_DOC.md:2250–2252`). Both cannot describe the current §0.26 surface, and
  the latter conflicts with this manifest-resolved round-26 review.
- **Disposition:** Admitted. State that round 25 reviewed the §0.25 surface and required the
  now-applied non-interface §0.26 correction, and that round 26 is the current fresh whole-document
  review. Preserve the unverified and no-version-roll statements.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

Candidate-002 was cleared as an exact duplicate of admitted candidate-001, not because its factual
premise was refuted. Counting it separately would double-count the same stale paragraph and the
same required edit. No other supplied candidate was refuted or cleared, and no finding is created
from candidate-free clean areas.

The entities-blocks classification is consistent across §3.4, §9.1, D-P2-18, and the downstream
hand-off: it remains separately reported Phase 9/10 coverage and is excluded from the authoritative
v0.3 terrain-only gate. Section 3.5 maps v0.3 `RUN-T2` to `terrain-day`; the generic run catalogue
and implementation checklist defer to that milestone-declared scene set and do not conflict.

The selected Phase 1 binding contract supports the declared Phase 2 consumptions, including
capability-profile serialization, recording and replay facilities, replay-aware GL errors, debug
flags, fixture placement, and CI extension points. Phase 2's consumer-facing §5 promises resolve to
substantive detailed sections, while unresolved upstream needs remain requests. No correction to
the manifest-declared §5 interface region is required.

The conformance map has no identified unmapped or semantically unsupported in-scope requirement.
The examined T0–T3 mappings, fixture acquisition and licensing, fixed scenes and camera paths,
headless-core requirements, §9 exit criteria, OQ-10 spike, before-renderer subset, mandatory
thirteen-section structure, and document gate remain supported. No equivalent round-26 closing
marker elsewhere cures the admitted contradiction.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted defect is a localized verification-history correction rather than a structural miss
requiring rebuild. It lies outside the declared cross-phase interface/change-trigger region.

Recent correction counts for rounds 19–26 are 1, 2, 1, 1, 1, 1, 2, and 1. They are not strictly
decreasing, and the repeated one-round-late closing-history defect is a strong convergence warning.
That trend neither removes the evidence-backed correction nor warrants escalation to FAIL.

Next required action: apply the candidate-001 non-interface fix-up and record its resolution in
this review. Phase 2 remains unverified. After fix-up, run a fresh whole-document verification
round; do not roll the version while the loop remains open.

## Resolutions

### candidate-001 — Resolved

Re-derived the lifecycle sequence from the target's addenda and current review: round 25 reviewed
the §0.25 surface, required the now-applied §0.26 correction, and round 26 is the current fresh
whole-document review. Updated the closing marker accordingly while preserving the statements
that Phase 2 v1 remains unverified and that no version roll occurs during the open loop. Added the
compact §0.27 addendum required for this target change. No binding §5 text changed.

### Notes deferred

None. The adjudicator admitted no notes.
