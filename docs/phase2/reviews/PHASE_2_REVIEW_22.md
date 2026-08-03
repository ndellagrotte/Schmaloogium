# Schmaloogium — Phase 2: Conformance harness — Review Round 22

## 0. Method and reading order

I independently re-derived the sole gated candidate before reading any prior review. I read the
manifest-selected target, `docs/phase2/v1/PHASE_2_DOC.md`; the governing Part I, Phase 2 target
specification, document gate, and mandatory template in `docs/design/v1.1/DESIGN.md`; the contract
ground truth in `docs/research/v1/RESEARCH.md`; the selected binding dependency in
`docs/phase1/v14/PHASE_1_DOC.md`; and the supplied supporting CI evidence under
`.github/workflows/`. Only after settling the candidate did I read
`docs/phase2/reviews/PHASE_2_REVIEW_1.md` through `PHASE_2_REVIEW_21.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. Forbidden
sources were not read. The Gate reported no drops, and no candidate was eliminated before
adjudication.

The prior reviews were considered only after independent judgment. Round 21 admitted this same
class of stale closing-history defect and its resolution says the closing paragraph was advanced
through the §0.22 fix-up. That does not clear the present candidate because the current target
bytes still describe §0.22 as pending and round 21 as current.

## 1. Findings

### candidate-001 — Closing verification history still identifies round 21 as current after its §0.22 fix-up

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:181–183,2233–2235`
- **Claim:** The §0.22 fix-up corrected the closing verification history so that it accurately
  describes the reviewed surface and the next fresh review.
- **Evidence:** Section 0.22 states that the round-21 fix-up has already “Corrected the closing
  verification history after the round-20 fix-up”
  (`docs/phase2/v1/PHASE_2_DOC.md:181–183`). The closing paragraph instead says round 21 “is the
  current fresh whole-document review” and “requires the §0.22 fix-up”
  (`docs/phase2/v1/PHASE_2_DOC.md:2233–2235`). Those lifecycle states are incompatible on the
  current surface and the latter also conflicts with this manifest-resolved round-22 review.
- **Disposition:** Admitted. Update the closing history to state that round 21 reviewed the §0.21
  surface and required the now-applied non-interface §0.22 correction, and that round 22 is the
  current fresh whole-document review. Preserve the continuing unverified and no-version-roll
  status while the loop remains open.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported last-revised marker and §0.22 heading consistently identify the applied
addendum. Neighboring implementation-plan rows and target-wide occurrences of §0.22, round 21/22,
current-review wording, and Phase 1 version references revealed no additional defect. The interface
audit found the declared Phase 1 consumptions supported by the selected binding contract, pending
requests honestly unaccepted, and downstream schemas implementable. No §5 interface edit is
implicated. The conformance audit found the Phase 2 objective and scope, Appendix G packs, T0–T3
tiers, harness requirements, initial scene families, fixture policy, before-renderer subset,
OQ-10 spike, and RESEARCH.md §9 exit criteria mapped to substantive design elements and runs.

The sole candidate survives independent re-derivation. The §0.22 addendum cannot supersede or cure
the contradictory designated closing status, and round 21's resolution describes an edit not
present in the target's current bytes. No candidate was refuted or cleared, the Gate dropped none,
and no finding is created from candidate-free clean areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted defect is a localized verification-history correction rather than a structural miss
requiring rebuild. It lies outside the declared cross-phase interface/change-trigger region; the
reviewed architecture and interface surface are otherwise clean.

The recent correction counts are 1 in round 19, 2 in round 20, 1 in round 21, and 1 in this round.
They are not strictly decreasing, and the repeated residual closing-history correction means the
loop has not converged to literal PASS. The next required action is a scoped fix-up resolving
candidate-001 and recording its resolution in this review, followed by the normal fresh
whole-document review. Because §5 does not change, the interface change trigger does not fire.

## Resolutions

### candidate-001 — resolved

Re-derived the lifecycle state from the applied §0.22 addendum and this round's manifest-resolved
review. Updated the closing paragraph to say that round 21 reviewed the §0.21 surface and required
the now-applied non-interface §0.22 correction, and that round 22 is the current fresh
whole-document review. The paragraph continues to mark Phase 2 v1 unverified and forbids a version
roll while the loop remains open. Added compact §0.23 to record this round's fix-up. Binding §5 was
not changed, so the interface change trigger does not fire.

### Notes deferred

None.
