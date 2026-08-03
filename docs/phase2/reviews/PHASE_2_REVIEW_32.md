# Phase 2 verification review — round 32

## 0. Method and reading order

I first re-derived all three supplied candidates from the complete target in
`docs/phase2/v1/PHASE_2_DOC.md`, the resolved governing selectors in
`docs/design/v1.1/DESIGN.md`, the contract ground truth in
`docs/research/v1/RESEARCH.md`, the selected binding contract in
`docs/phase1/v14/PHASE_1_DOC.md`, the manifest in `verification/targets/phase-2.json`, and the
permitted CI evidence under `.github/workflows`. Only after settling those independent judgments
did I read discovered prior reviews 1–31, last, and compare every candidate with settled findings,
resolutions, and clean-area conclusions.

There were no reading-order deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. The
canonical engine supplied the finder, refuter, steelman, and Gate material. The Gate dropped no
candidates, there were no pre-adjudication eliminations, and forbidden sources were not read.

Prior material changes two provisional dispositions. Candidate-002 is a narrower instance of round
30 candidate-002's settled and still-unresolved interface/change-trigger defect, so it is not
counted again. Candidate-003 does not survive the governing text or settled clean-area conclusions:
the Phase 2 doc gate requires the before-renderer subset to be identified, which §9.2 does, while
the mandatory §3 wording identifies its mapped contract rows as RESEARCH.md §3/App rows. Prior
whole-document rounds repeatedly checked both the conformance map and the before-renderer subset
without requiring the same target-spec obligation to be duplicated in §3.

## 1. Findings

### candidate-001 — Round-31 fix-up leaves the lifecycle marker one round behind

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:221–223,2274–2276`
- **Claim:** The §0.32 addendum and operative closing marker consistently describe the surface
  produced by the round-31 fix-up and dispatched for round 32.
- **Evidence:** Section 0.32 is headed “Round-31 fix-up,” but says it corrected history after the
  round-30 fix-up (`docs/phase2/v1/PHASE_2_DOC.md:221–223`). More decisively, the closing marker
  still says round 30 reviewed §0.30, required §0.31, and round 31 “is the current fresh
  whole-document review” (`docs/phase2/v1/PHASE_2_DOC.md:2274–2276`). The document already contains
  the §0.32 round-31 fix-up and this is manifest-resolved round 32, so that marker is one lifecycle
  transition behind.
- **Disposition:** Admitted. State that §0.32 records the correction required by round 31, and
  update the closing marker to say round 31 reviewed the §0.31 surface, required the now-applied
  non-interface §0.32 correction, and round 32 is the current fresh whole-document review. Preserve
  the unverified and no-version-roll clauses.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported new-surface checks otherwise hold: the header points uniquely to §0.32, the
addendum sequence is monotonic, and the document consistently remains unverified without a version
roll. The selected Phase 1 binding contract continues to support the consumed module/seam,
capability-profile serialization, recording/replay, debug-capture, replay-aware-error, and CI
extension facilities. The mapped tier, fixture, scene, harness, milestone, and OQ-10 requirements
remain substantively supported.

Candidate-002 is dropped on prior-settlement derivation, not refuted on substance. Section 4.13's
machine- and CI-consumed reporting rules do sit between adjacent declared regions, but round 30
already admitted the broader defect that normative detailed contracts exposed by §5 are outside
reliable change-trigger coverage. Its resolution was refused only because that fix-up role could
not edit the manifest; the maintainer/orchestrator-authorized manifest correction remains open.
Counting this reporting instance again would duplicate that settled correction.

Candidate-003 is cleared on re-derivation. The governing target specification requires identifying
the “runnable before any renderer exists” subset, and the target does so explicitly and concretely
in §9.2. The cited mandatory-template text describes §3's rows as in-scope RESEARCH.md §3/App rows;
it does not require every separate target-spec doc-gate item to be repeated there. Earlier reviews,
including the initial conformance-map audit and the recent whole-document checks, independently
treated the conformance map and the §9.2 before-renderer list as complete. No changed surface or new
authority displaces that settled interpretation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted lifecycle inconsistency is bounded fix-up work rather than a structural miss. This
round admits no new interface correction: candidate-002 is subsumed by round 30's still-open
interface/change-trigger correction, and candidate-003 is dropped.

The lifecycle defect has recurred across many consecutive rounds and remains plainly
non-convergent. Apply candidate-001's correction and record its resolution in this review.
Separately, round 30's refused manifest-selector correction remains open and requires a
maintainer/orchestrator-authorized manifest edit. After both outstanding corrections are resolved,
run a fresh whole-document verification round before Phase 2 can close. Do not roll the version
while the loop remains open.

## Resolutions

### candidate-001 — resolved

Re-derived the lifecycle from the target itself: §0.31 is the round-30 fix-up, §0.32 is the
round-31 fix-up, and this review is round 32. Updated §0.32 to state that it records the correction
required by round 31, and advanced the closing marker to say that round 31 reviewed the §0.31
surface, required the now-applied non-interface §0.32 correction, and round 32 is the current fresh
whole-document review. Preserved the unverified state and no-version-roll clause. Added compact
§0.33 solely to record this round-32 fix-up and advanced the header pointer accordingly.

No declared interface/change-trigger region changed. The round-30 manifest-selector correction
remains outside this role's two-path write allowlist and is not represented as resolved here.

### Notes deferred

None.
