# Phase 5 adversarial review — round 7

## 0. Method and reading order

I independently re-derived every candidate against the complete target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, and the binding §5 regions of the Phase 1, 3, and 4
dependencies. I checked the cited governing and supporting evidence where needed. Only after
settling those judgments did I read `docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_6.md`, including their resolutions.

I used no network access, no subagents or other agent fan-out, and no forbidden source. In
accordance with the already-dispatched atomic role and the verify-loop skill, I did not invoke the
verification loop, run `scripts/verify`, or start another Codex session. There were no deviations
from the supplied reading contract and no Gate drops. Candidate-003 duplicates candidate-002 and
is dispositioned once, without double-counting, in §2.

## 1. Findings

### candidate-001 — Public planning has no successful result or creation handoff

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:286`–`:289`,
`docs/phase5/v1/PHASE_5_DOC.md:507`–`:528`, and
`docs/phase5/v1/PHASE_5_DOC.md:1203`–`:1206`

**Claim.** The binding planning API defines invalid planning but not what a valid pure planning
call returns or how its deterministic artifacts reach candidate creation. Phase 7 therefore
cannot implement the successful branch of `BufferArchitecture.plan` or determine its relationship
to `create`.

**Evidence.** The public contract declares
`BufferPlanResult plan(BufferPlanRequest request)` beside
`BufferBuildResult create(BufferBuildRequest request)` but does not define
`BufferPlanResult` (`docs/phase5/v1/PHASE_5_DOC.md:286`–`:289`). Planning produces deterministic
FBO keys, clear groups, texture-unit rows, and teardown order
(`docs/phase5/v1/PHASE_5_DOC.md:507`–`:525`), yet the only specified result variant is
`BufferPlanResult.Invalid(BufferFailure)` (`docs/phase5/v1/PHASE_5_DOC.md:527`–`:528`). Section
5 exposes this result to Phase 7 while describing deterministic planning and candidate creation
without naming a successful plan artifact or a re-planning rule
(`docs/phase5/v1/PHASE_5_DOC.md:1203`–`:1206`). The signatures are expressly the cross-phase
contract (`docs/phase5/v1/PHASE_5_DOC.md:235`–`:238`).

**Required correction.** Define `BufferPlanResult` as a closed success/failure result and specify
the immutable successful plan payload. State whether `create` consumes that artifact or
independently reruns the same deterministic planning operation from `BufferBuildRequest`.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-002 — Resize failure promises an identity registration cannot supply

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:296`–`:329` and
`docs/phase5/v1/PHASE_5_DOC.md:1061`–`:1081`

**Claim.** `ConsumerFailed` promises Phase 7 the failed consumer's identity, but neither
registration nor `BufferResizeConsumer` supplies an identity and no deterministic derivation rule
is defined.

**Evidence.** `addResizeConsumer` accepts only the consumer, acknowledged sizing, and acknowledged
generation, while `BufferResizeConsumer` exposes only `resize`
(`docs/phase5/v1/PHASE_5_DOC.md:296`–`:321`). Nevertheless, the closed publication result carries
`String consumerId` (`docs/phase5/v1/PHASE_5_DOC.md:323`–`:329`), and the lifecycle promises that
the publisher returns the failed consumer identity to Phase 7 as a recovery signal
(`docs/phase5/v1/PHASE_5_DOC.md:1075`–`:1081`). Registration order and `deliveredCount` locate a
dispatch position but do not establish the promised stable identity. No target-wide definition
supplies an accessor, argument, generated-ID rule, stability rule, or correspondence.

**Required correction.** Add an explicit stable diagnostic identity to registration or to
`BufferResizeConsumer`, define how it is retained and returned by `ConsumerFailed`, and reflect
that rule in §5. Alternatively, remove `consumerId` and the identity promise if dispatch position
is the intended recovery contract.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-004 — Main color allocation policy is absent from the conformance map

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:417`–`:420` and
`docs/phase5/v1/PHASE_5_DOC.md:572`–`:575`

**Claim.** Section 3 does not completely map the governing requirement that main/alt color pairs
are allocated up front with `CLAMP_TO_EDGE`, `NEAREST` filtering for integer formats, and
`LINEAR` filtering otherwise.

**Evidence.** The governing Phase 5 assignment requires up-front main/alt pair allocation and the
exact filtering and wrapping policy (`docs/design/v2.0-RC3/DESIGN.md:1595`–`:1597`). The
conformance map's pair row names only `ColorPair(A,B)` in §4.3, while nearby format rows do not map
the complete main-color policy (`docs/phase5/v1/PHASE_5_DOC.md:417`–`:420`). The detailed design
does specify the required wrap and filters (`docs/phase5/v1/PHASE_5_DOC.md:572`–`:575`), so this
is a traceability omission rather than an architectural absence. The mandatory template requires
every in-scope item to map to its satisfying design element with zero unmapped rows
(`docs/design/v2.0-RC3/DESIGN.md:804`–`:808`).

**Required correction.** Expand the existing pair row or add a nearby §3.1 row mapping up-front
allocation to §4.3 and the complete filter/wrap policy to §4.2, with governing provenance.

**Severity:** correction

**touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The publication lifecycle now gives
the installed-but-not-open state explicit success and failure exits; provenance rejection
preserves ownership and generation; planning and build requests enumerate their raw inputs;
resize acknowledgement and retry behavior are otherwise coherent. Consumed Phase 3 and Phase 4
contracts match their binding surfaces, and the three ungranted Phase 1 facade requirements are
honestly recorded as prerequisites. App B.1/B.2/B.4 coverage, fixed App B.3 bindings including
depthtex1 at unit 11, flip/clear behavior, fog alpha, depth-copy strategy, shadow behavior, sizing,
resize, and growth posture are otherwise mapped and supported.

Candidate-003 is dropped as an exact substantive duplicate of admitted candidate-002. It identifies
the same missing identity source in the same resize registration and `ConsumerFailed` contract and
requires the same interface correction; counting it separately would count one defect twice.

Prior reviews do not settle the admitted findings. Round 6 introduced the public planning result
inventory but its resolution did not define the successful result now tested by candidate-001.
Earlier resize fixes established delivery, acknowledgement, failure ordering, and the registration
entry point, but none supplies the identity tested by candidate-002. Earlier conformance fixes
added other omitted Phase 5 requirements, not the complete main-color allocation parameter policy
tested by candidate-004.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

Three localized correction-level findings are admitted; none requires structural rebuilding, so
`FAIL` is not warranted. Candidates 001 and 002 correct binding public contracts and activate the
declared interface change trigger. Candidate 004 is conformance-map-only.

Prior rounds reported correction counts of 4, 5, 2, 2, 1 (in the coverage-blocked Round 5), and
3. This round again has 3 corrections, including defects in newly revised interface surface, so
convergence has not been reached. The next required action is a scoped fix-up resolving
candidate-001, candidate-002, and candidate-004 and appending resolutions to this review. Because
the fix-up must change or clarify §5 and its exposed types, a fresh verification round is required
before Phase 5 can close.

## Resolutions

### candidate-001 — resolved

Defined `BufferPlanResult` as the closed `Valid(BufferPlan)` / `Invalid(BufferFailure)` result.
The successful opaque immutable plan contains the resolved sizing, inventory, resource descriptors,
pass/FBO keys, clear groups, fixed unit rows, and teardown order, with value equality over those
artifacts. `create` explicitly reruns the same deterministic planning operation from the planning
fields already present in `BufferBuildRequest`; it performs no GL allocation unless that repeated
plan is valid. Sections 4.1 and 5.1 state the same handoff rule.

### candidate-002 — resolved

Added a caller-supplied `String consumerId` to `addResizeConsumer`. Registration requires it to be
nonblank and unique among live registrations, and the publisher retains it unchanged for that
registration's lifetime. The retained value is the identity returned by `ConsumerFailed`; §4.11.2
and §5.1 now bind this diagnostic identity source and lifetime.

### candidate-004 — resolved

Expanded the §3.1 main/alt-pair row to map up-front pair allocation to §4.3 and both sides'
`CLAMP_TO_EDGE`, integer-NEAREST, otherwise-LINEAR parameters to §4.2, citing the governing RC3
assignment in addition to the existing contract and mechanism provenance.

### Notes deferred

None.

The §5 cross-phase interface region changed. A fresh verification round is required before Phase 5
can close.
