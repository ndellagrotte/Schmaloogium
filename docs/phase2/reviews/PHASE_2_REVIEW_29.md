# Phase 2 verification review — round 29

## 0. Method and reading order

I first re-derived the supplied candidate from the complete target surface in
`docs/phase2/v1/PHASE_2_DOC.md`, the resolved governing selectors in
`docs/design/v1.1/DESIGN.md`, the contract ground truth in
`docs/research/v1/RESEARCH.md`, the CI evidence under `.github/workflows`, and the selected Phase 1
binding contract in `docs/phase1/v14/PHASE_1_DOC.md`. Only after settling that independent
judgment did I inspect discovered prior reviews 1–28, last, and compare the candidate with their
settled findings and resolutions.

There were no reading-order deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. The Gate
dropped no candidates, and there were no pre-adjudication eliminations. Forbidden sources were not
read.

Prior review 28's resolution says it added §0.29 and advanced the closing marker to the round-28
review state. Section 0.29 is present, but the marker stops at that pre-fix-up state and therefore
does not describe the surface entering round 29. Prior settled material also establishes that a
fix-up addendum describes the history left by the preceding fix-up; that convention narrows the
candidate's proposed remedy but does not clear its stale-marker defect.

## 1. Findings

### candidate-001 — Closing lifecycle marker was not advanced for round 29

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:209–211,2262–2264`
- **Claim:** The §0.29 surface and operative closing marker consistently identify the completed
  round-28 review/fix-up transition and round 29 as the current fresh review.
- **Evidence:** Section 0.29 is headed “Round-28 fix-up” and says it corrected the closing history
  after the round-27 fix-up (`docs/phase2/v1/PHASE_2_DOC.md:209–211`). The operative closing marker
  instead says round 27 reviewed §0.27 and required §0.28, while round 28 “is the current fresh
  whole-document review” (`docs/phase2/v1/PHASE_2_DOC.md:2262–2264`). That marker describes the
  pre-§0.29 lifecycle and conflicts with this manifest-resolved round-29 review.
- **Disposition:** Admitted in narrowed form. Leave §0.29 unchanged because it follows the settled
  addendum convention. Advance only the closing marker to state that round 28 reviewed the §0.28
  surface and required the now-applied non-interface §0.29 correction, and that round 29 is the
  current fresh whole-document review. Preserve the unverified and no-version-roll statements.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The header's last-revised marker and §0.29 heading consistently identify the latest applied
addendum. Re-derivation clears the candidate's proposed rewrite of §0.29: neighboring addenda and
prior settled adjudication establish that its reference to the round-27 fix-up is intentional, not
self-stale. No equivalent round-29 lifecycle marker elsewhere cures the stale closing paragraph.

The interface audit found the selected Phase 1 consumptions supported by its binding contract,
including capability-profile serialization and fixtures, recording and replay facilities,
replay-aware GL errors, debug flags, and CI extension points. Ungranted needs remain explicit
requests, and Phase 2's exposed contracts resolve to substantive detailed design. The
manifest-declared §5 cross-phase interface region is unaffected.

The conformance audit found no unmapped or semantically unsupported in-scope requirement across
tier machinery, scenes, capture and diffing, fixtures, headless goldens, milestone gates, the
before-renderer subset, or OQ-10. The mandatory thirteen-section structure and document gate remain
intact. Candidate-001 survives in the narrowed form above; no candidate is dropped, the Gate
dropped none, and no finding is created from candidate-free clean areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted defect is a localized verification-history correction rather than a structural miss
requiring rebuild. It lies outside the declared cross-phase interface/change-trigger region.

Recent correction counts remain at one in rounds 26–29. The repeated one-round-late lifecycle
defect shows no convergence and warrants explicit convergence attention, but it neither removes
this evidence-backed correction nor warrants escalation to FAIL.

Next required action: apply the narrowed candidate-001 non-interface fix-up and record its
resolution in this review. Phase 2 remains unverified. After fix-up, run a fresh whole-document
verification round; do not roll the version while the loop remains open.

## Resolutions

### candidate-001 — resolved

Re-derived the lifecycle sequence from the target's neighboring §0 addenda and closing marker. The
closing marker now states that round 28 reviewed the §0.28 surface and required the applied
non-interface §0.29 correction, and that round 29 is the current fresh whole-document review. The
unverified and no-version-roll statements are preserved. Section 0.29 was intentionally left
unchanged because it records the history left by the round-28 fix-up. A compact §0.30 addendum and
the header's last-revised pointer record this fix-up without importing another phase's changelog
conventions. No text in the manifest-declared §5 interface region changed, so the interface change
trigger did not fire.

### Notes deferred

None; the adjudicator admitted no notes.
