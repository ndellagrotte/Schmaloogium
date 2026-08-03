# Phase 8 — Shadow pass — Verification Review 4

## 0. Method and reading order

I independently re-derived the sole Gate-surviving candidate against the complete Phase 8 target,
`docs/design/v2.0-RC3/DESIGN.md` Part I and the Phase 8 assignment/doc gate, the binding §5
contracts of Phases 4–7, and the cited permitted evidence. Only after settling that judgment did I
read `docs/phase8/reviews/PHASE_8_REVIEW_1.md`,
`docs/phase8/reviews/PHASE_8_REVIEW_2.md`, and
`docs/phase8/reviews/PHASE_8_REVIEW_3.md`, including their resolutions, last.

There were no deviations from the resolved source contract. I did not use the network, invoke the
verification harness, start another Codex process, or fan out to agents. I did not read forbidden
sources. The Gate reported no drops, and no candidate was eliminated before adjudication.

## 1. Findings

No candidate is admitted as a finding.

## 2. Checked and clean

The finder-reported new surface, interfaces, and conformance areas are clean. In particular, the
round-3 publication-close algebra consistently supplies render-thread-only idempotent teardown,
mutation-free rejection while invoking, slot-epoch invalidation, and retained-reference release.
The hardware-PCF domain is consistently depth-only. R8-4 assigns construction, ownership,
rollback, and reverse close without assuming an ungranted dependency surface. The conformance map
continues to cover the governing shadow behaviors, directives, buffers, samplers, uniforms, camera
math, traversal, render order, depth split, mipmaps, blob suppression, clouds, and hook ledger.

**candidate-001 is dropped on re-derivation.** `ShadowWorldPort.sample` returns the successful
immutable value directly (`docs/phase8/v1/PHASE_8_DOC.md:280`–`:281`), but that does not leave its
failure behavior open. After `beginPass`, the invocation validates the sample's echoed identities
and camera presence and maps absent or stale input to rejection
(`docs/phase8/v1/PHASE_8_DOC.md:574`–`:580`). Every exit after acquisition shares one cleanup path;
vanilla throwables are preserved only after restoration and a closed Phase 7 result, while engine
exceptions are converted to stable failures (`docs/phase8/v1/PHASE_8_DOC.md:605`–`:607`). Phase
7's binding contract expressly treats a thrown exception as a contained backend failure and gives
Phase 8 ownership of its internal taxonomy (`docs/phase7/v1/PHASE_7_DOC.md:1436`–`:1444`). The
failure-injection plan additionally requires reverse restoration, exact abort count, and no later
draw after every operation (`docs/phase8/v1/PHASE_8_DOC.md:1240`–`:1243`).

The §5.1 phrase “`ShadowWorldPort` and closed world/state/terrain/draw results” summarizes the
port's value and result surface; it does not state that every port method must have a distinct
method-local sealed algebra. The governing design and dependency contracts impose no such rule.
Adding `ShadowWorldSampleResult` would be a permissible redesign, but it is not a required
correction because validation, abort/cleanup, and the closed invocation-level rejection/failure
mapping are already specified. Final disposition: dropped, severity none, interface impact no.

## 3. Verdict

# PASS
Counts: blocking=0; corrections=0; notes=0
Interface changed: no

The sole candidate is dropped on independent re-derivation. There are no blocking findings,
corrections, or notes, and this adjudication orders no interface change. Prior corrections fell
from five in round 1 to three in round 2 to two in round 3, and each round's resolutions are
present; round 4 now reaches literal PASS. There is no convergence warning and FAIL is not
warranted.

Next action: none within the verification loop. Phase 8 may close under the governing process.
