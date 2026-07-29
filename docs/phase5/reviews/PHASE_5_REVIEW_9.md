# Phase 5 adversarial review — round 9

## 0. Method and reading order

I independently re-derived the sole surviving candidate against the complete target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, and the binding §5 regions of the Phase 1, 3, and 4
dependencies. I checked the supplied supporting evidence where needed. Only after settling that
judgment did I read `docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_8.md`, including their resolutions.

I used no network access, no subagents or other agent fan-out, and no forbidden source. In
accordance with the already-dispatched atomic role and the verify-loop skill, I did not invoke the
verification loop, run `scripts/verify`, or start another Codex session. The only reading-contract
deviation was that the supporting `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` was not
opened because the supplied forbidden-source rule bars `*.txt`; it was unnecessary to adjudicate
this API-carrier defect. There were no candidates eliminated before adjudication and no Gate
drops.

## 1. Findings

### candidate-001 — The main-depth resize-required signal has no specified public carrier

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:371`–`:378`,
`docs/phase5/v1/PHASE_5_DOC.md:899`–`:924`, and
`docs/phase5/v1/PHASE_5_DOC.md:1233`–`:1242`

**Claim.** Phase 5 requires an already-published estate to distinguish a same-extent main-depth
version change from an extent mismatch and requires Phase 7 to react to the latter, but it does not
assign the comparison to a public estate operation or define which closed result carries
`MAIN_DEPTH_RESIZE_REQUIRED`. Phase 7 therefore cannot consume the required runtime transition
without inventing Phase-5-owned protocol.

**Evidence.** `MainDepthSource` changes its version whenever texture identity, format, extent, or
availability changes (`docs/phase5/v1/PHASE_5_DOC.md:899`–`:900`). The design prescribes in-place
reattachment for a new same-extent version, but says an extent mismatch instead returns
`MAIN_DEPTH_RESIZE_REQUIRED` and requires Phase 7 to abort or normalize the frame and perform the
prepare/build/publication sequence (`docs/phase5/v1/PHASE_5_DOC.md:912`–`:924`). The public
`BufferEstateView` operations expose several result-bearing observation points without specifying
which one performs this comparison or carries that outcome
(`docs/phase5/v1/PHASE_5_DOC.md:371`–`:378`). Binding §5 similarly lists the frame, clear, depth
copy, and main-depth SPI contracts but does not bind the runtime resize-required carrier or Phase
7 response (`docs/phase5/v1/PHASE_5_DOC.md:1233`–`:1242`). The pre-creation
`prepare(plan.mainExtent())` path does not cure the omission: it prepares candidate construction,
whereas the required transition is triggered by a later change observed against a published
estate. The governing Phase 5 scope owns depth-version reattachment and the resize/recreate
lifecycle (`docs/design/v2.0-RC3/DESIGN.md:1617`–`:1629`,
`docs/design/v2.0-RC3/DESIGN.md:1638`–`:1644`).

**Required correction.** Specify the public render-thread operation that compares
`MainDepthSource.current()` with the published estate and define the exact closed result variant
that carries `BufferFailureCode.MAIN_DEPTH_RESIZE_REQUIRED`. State when it is emitted, its no-GL
and no-mutation guarantees, and Phase 7's mandatory abort/normalize and
prepare/build/publication response. Reflect the carrier in §5.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. Round 8's
`ConsumerFailed.deliveredCount` invariant is consistent across the result, lifecycle, §5 contract,
and conformance oracle. The consumed Phase 3 and Phase 4 contracts match their binding regions,
and Phase 5 honestly records the missing Phase 1 facade capabilities rather than assuming them.
Candidate/publication ownership, resize-consumer acknowledgement and failure accounting, overlay
lifetime and provenance, shadow-estate operations, fixed-unit snapshots, the App B.1/B.2/B.4
mapping, and the App B.3 unit-11 ruling are otherwise coherent.

The sole candidate is not cleared. `MainDepthSource.prepare` and the closed
`BufferBuildResult` cover pre-build readiness, while the target separately requires an existing
estate to react to later version and extent changes. A conformance requirement to prove that no
old estate attachment occurs before prepare/rebuild does not define the missing callable
operation or result carrier. No prior review or resolution settles this distinct runtime
main-depth signaling contract.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

One localized correction-level finding is admitted. It does not require rebuilding the Phase 5
architecture, so `FAIL` is not warranted. The correction defines a Phase-7-consumed public result
and must update the binding §5 interface region.

Round 8 reduced the outstanding review surface to one correction, but this fresh round finds one
distinct interface correction, so literal convergence has not been reached. The next required
action is a scoped fix-up resolving candidate-001 and appending its resolution to this review.
Because the correction changes or clarifies the exposed Phase 5 contract and §5, the interface
change-trigger requires a fresh verification round before Phase 5 can close.

## Resolutions

### candidate-001 — resolved

Re-derived against the Phase 5 resize/recreate and version-driven reattachment requirements. The
target now exposes `BufferEstateView.refreshMainDepth()` as the render-thread comparison point and
a closed `MainDepthRefreshResult`. `ResizeRequired(BufferFailure)` is the sole carrier for
`BufferFailureCode.MAIN_DEPTH_RESIZE_REQUIRED`; it is emitted only when the current available main
depth extent differs from the published estate, performs no GL call or mutation, and rejects any
other failure code. The complementary `Failed` variant rejects that code.

Phase 7 must call the operation before frame start and each shader draw. On `ResizeRequired`, it
must abort/normalize an open shader frame, perform safe-point prepare/build/publication, and forbid
another shader draw until a matching replacement is published. The same contract is reflected in
§5 and strengthened by a zero-GL/zero-mutation conformance oracle. The target received compact
§0.11 reporting only. Because binding §5 changed, a fresh verify round is required before Phase 5
can close.

### Notes deferred

None.
