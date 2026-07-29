# Phase 5 adversarial review — round 8

## 0. Method and reading order

I independently re-derived both surviving candidates against the complete target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, and the binding §5 regions of the Phase 1, 3, and 4
dependencies. I checked the supplied supporting evidence where needed. Only after settling those
judgments did I read `docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_7.md`, including their resolutions.

I used no network access, no subagents or other agent fan-out, and no forbidden source. In
accordance with the already-dispatched atomic role and the verify-loop skill, I did not invoke the
verification loop, run `scripts/verify`, or start another Codex session. There were no deviations
from the supplied reading contract, no candidates eliminated before adjudication, and no Gate
drops.

## 1. Findings

### candidate-001 — `ConsumerFailed.deliveredCount` has no defined counting semantics

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:338`–`:339`,
`docs/phase5/v1/PHASE_5_DOC.md:1096`–`:1102`, and
`docs/phase5/v1/PHASE_5_DOC.md:1421`–`:1424`

**Claim.** The public resize-failure result exposes `deliveredCount`, but the delivery protocol
does not define whether it counts successful acknowledgements, invoked callbacks, or presented
notices. In particular, it does not say whether the failing callback counts when it returns
`FAILED` or throws. Phase 7 and conformance tests can therefore interpret the same failure
position differently.

**Evidence.** The closed result declares
`ConsumerFailed(..., String consumerId, int deliveredCount)` without a field invariant
(`docs/phase5/v1/PHASE_5_DOC.md:338`–`:339`). The failure protocol distinguishes consumers before
the failure, the failing consumer, and consumers not subsequently invoked, then describes the
field only as a delivered count in the recovery signal
(`docs/phase5/v1/PHASE_5_DOC.md:1096`–`:1102`). The conformance plan requires failure at every
consumer position and proof of partial delivery, but gives no numeric oracle for the result
(`docs/phase5/v1/PHASE_5_DOC.md:1421`–`:1424`). Section 5 expressly exposes
`BufferPublicationResult` and `ConsumerFailed` to Phase 7
(`docs/phase5/v1/PHASE_5_DOC.md:1224`–`:1227`), so this ambiguity is consumer-visible.

**Required correction.** Define `deliveredCount` as one exact quantity. Preferably, make it the
number of consumers before the failing consumer that returned `SUCCESS` and advanced their
acknowledged baselines, excluding the failing callback for both a returned `FAILED` and a thrown
outcome. Add matching first-, middle-, and last-position conformance assertions.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. `BufferPlanResult` is closed as
valid/invalid; the immutable plan contents and equality boundary are coherent; `create`
deterministically replans before allocation; resize-consumer identities are nonblank,
live-unique, retained unchanged, and returned on failure; and the main/alt allocation row maps the
required filter and wrap policy. The consumed Phase 1, 3, and 4 contracts are otherwise honestly
attributed, including the explicit Phase 1 depth-operation requests. The App B.1/B.2/B.4
conformance map, fixed App B.3 bindings, flip/clear state machines, sampleable-depth bridge,
shadow behavior, sizing, and resize lifecycle are otherwise supported.

Candidate-002 is dropped. The target already names the exact operation, value flow, timing,
consumer, and invariant: after planning and before candidate creation, Phase 7 calls
`prepare(plan.mainExtent())`, and candidate creation accepts only a ready snapshot with that exact
extent (`docs/phase5/v1/PHASE_5_DOC.md:898`–`:904`). The handoff repeats the same public call and
forbids building or drawing with a mismatch
(`docs/phase5/v1/PHASE_5_DOC.md:1546`–`:1548`). Calling `BufferPlan` opaque hides its
representation, not the explicitly specified accessor. Repeating a Java declaration or naming the
accessor separately in the §5 table would improve discoverability but is not required to make the
cross-phase operation implementable.

Prior reviews do not settle candidate-001. Earlier rounds defined synchronous ordered delivery,
per-consumer acknowledgement baselines, failure stopping, the recovery result, and stable
consumer identity, but none mapped those semantics to the numeric `deliveredCount` field.
Candidate-002 does not reopen a prior correction: Round 7's planning fix made the plan artifact
opaque and immutable, while the current target independently and explicitly specifies
`plan.mainExtent()` at both orchestration sites.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

One localized correction-level finding is admitted. It does not require structural rebuilding, so
`FAIL` is not warranted. Candidate-001 clarifies a field in the binding public result and therefore
activates the interface change trigger. Candidate-002 is cleared and does not count.

Round 7 reported three corrections; this round reduces the count to one, but literal convergence
has not been reached. The next required action is a scoped fix-up defining and testing
`deliveredCount`, with a resolution appended to this review and the Phase 5 addendum updated.
Because that correction changes or clarifies the exposed `BufferPublicationResult` contract, a
fresh verification round is required before Phase 5 can close.

## Resolutions

### candidate-001 — corrected

Re-derived against the existing ordered-delivery and per-consumer acknowledgement rules. The
document now defines `ConsumerFailed.deliveredCount` as the number of consumers preceding the
failing consumer that returned `SUCCESS` and advanced their acknowledged baselines. The failing
callback is excluded whether it returned `FAILED` or threw. The binding §5 table now exposes that
invariant to Phase 7, and the recorded-GL plan requires first-, middle-, and last-position numeric
oracles for both failure forms. A compact §0.10 addendum records the correction.

This clarification changes the `cross-phase-interfaces` region, so a fresh verify round is required
before Phase 5 can close.

### Notes deferred

None.
