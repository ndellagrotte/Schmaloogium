# Phase 5 adversarial review — round 6

## 0. Method and reading order

I independently re-derived every surviving candidate against the complete target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, the binding §5 regions of the Phase 1, 3, and 4 dependencies,
and the cited RESEARCH and supporting-evidence material. Only after settling those judgments did I
read `docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_5.md`, including the available resolutions.

I used no network access, no subagents or other agent fan-out, and no forbidden source. In
accordance with the already-dispatched atomic role and the verify-loop skill, I did not invoke the
verification loop, run `scripts/verify`, or start another Codex session. There were no deviations
from the supplied reading contract.

Gate dropped candidate-005 because its finder quote at
`docs/design/v2.0-RC3/DESIGN.md:1600`–`:1602` did not resolve. Refute dropped candidate-001 by
strict majority or absence of a live severity. Neither is admitted or counted.

## 1. Findings

### candidate-002 — Resize lifecycle branches from an undefined `PUBLISHED_NOT_OPEN` state

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1045`–`:1047` and
`docs/phase5/v1/PHASE_5_DOC.md:1073`–`:1083`

**Claim.** The resize-publication lifecycle has no coherent named transition into the
installed-but-not-open state from which consumer failure is specified.

**Evidence.** Accepted publication installs the new generation, invokes consumers, and opens
drawing only after all consumers succeed (`docs/phase5/v1/PHASE_5_DOC.md:1045`–`:1047`), so the
design requires a distinct installed-but-gated interval. The lifecycle chain moves directly from
`READY_UNPUBLISHED` to `PUBLISHED`, while the consumer-failure branch starts from
`PUBLISHED_NOT_OPEN`, a state that has no incoming transition and appears nowhere in the main
chain (`docs/phase5/v1/PHASE_5_DOC.md:1073`–`:1083`). The prose makes the intended behavior
recoverable but does not reconcile the state identifiers in the required testable lifecycle.

**Required correction.** Insert `PUBLISHED_NOT_OPEN` after installation and before drawing-open
`PUBLISHED`, with success and consumer-failure transitions, or rename the states consistently
while preserving the distinct gated interval.

**Severity:** correction

**touches interface/change-trigger region: no**

### candidate-003 — Publication API cannot represent required provenance rejection

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:284`–`:320` and
`docs/phase5/v1/PHASE_5_DOC.md:1183`–`:1186`

**Claim.** `BufferEstatePublisher.publish` requires an exact registry fingerprint and defines
caller ownership after rejection, but its closed result type has no outcome for that rejection.

**Evidence.** Both publisher operations return `BufferPublicationResult`
(`docs/phase5/v1/PHASE_5_DOC.md:284`–`:294`), whose sealed variants are only `Published` and the
resize-specific `ConsumerFailed` (`docs/phase5/v1/PHASE_5_DOC.md:310`–`:314`). The immediately
following contract requires the exact fingerprint and states that rejection leaves the candidate
caller-owned (`docs/phase5/v1/PHASE_5_DOC.md:317`–`:320`). A Phase 7 provenance precheck does not
define the public method's mismatch outcome, and the document specifies no exception alternative.
The implementation checklist independently requires stale/cross-registry rejection
(`docs/phase5/v1/PHASE_5_DOC.md:1556`). Section 5 promises provenance-checked ownership transfer
through the same types without exposing a representable rejection
(`docs/phase5/v1/PHASE_5_DOC.md:1183`–`:1186`).

**Required correction.** Add a typed provenance-rejection result and specify that mismatch causes
no ownership transfer, publication, generation change, or resize notification and leaves the
candidate caller-owned. Reflect that outcome in §5.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-004 — Binding interface omits public planning and build input contracts

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:252`–`:277` and
`docs/phase5/v1/PHASE_5_DOC.md:1183`–`:1224`

**Claim.** Section 5 does not expose the request and runtime-input contracts Phase 7 must construct;
moreover, `BufferPlanRequest` is not defined anywhere in the target.

**Evidence.** The public-shape section defines `BufferRuntimeInputs` and `BufferBuildRequest`
(`docs/phase5/v1/PHASE_5_DOC.md:252`–`:266`) and declares
`plan(BufferPlanRequest)` plus `create(BufferBuildRequest)`
(`docs/phase5/v1/PHASE_5_DOC.md:274`–`:277`). Whole-target search finds no definition of
`BufferPlanRequest`. The §5 inventory names the operations and result types but omits
`BufferPlanRequest`, `BufferBuildRequest`, and `BufferRuntimeInputs`
(`docs/phase5/v1/PHASE_5_DOC.md:1183`–`:1186`), even though §5 later confirms that Phase 7 supplies
the runtime inputs (`docs/phase5/v1/PHASE_5_DOC.md:1223`–`:1224`). The governing template requires
§5 to identify exposed named interfaces and data contracts
(`docs/design/v2.0-RC3/DESIGN.md:811`–`:813`).

**Required correction.** Define `BufferPlanRequest` with its exact fields and invariants, and add
`BufferPlanRequest`, `BufferBuildRequest`, and `BufferRuntimeInputs` to §5 with Phase 7 ownership.
Section 5 may explicitly incorporate canonical public-shape definitions rather than duplicate
them, provided the binding reference is complete.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The Round-4 shadow-depth swizzle
mapping, clear execution and full-clear retention, overlay snapshot handoff, repeated Phase 1
dependency requests, fixed unit table, unit-11 ruling, format inventory, flip/clear state
machines, sampleable-depth bridge, real shadow flips, sizing behavior, and Final handoff are
otherwise coherent. The consumed Phase 3 and Phase 4 names exist in their binding regions, and
Phase 5 honestly treats the missing Phase 1 depth permissions and operations as requested changes.

No surviving candidate is cleared. Candidate-002 is a localized state-machine naming defect;
candidate-003 is a real public outcome hole rather than an unreachable-path concern; and
candidate-004 is not cured by detail outside §5 because the governing template makes §5 the
binding interface inventory and the planning request lacks a definition anywhere.

Prior-review material does not settle these findings. Round 5's selector blocker is superseded by
this round's validated whole-document range through line 1567. Its unresolved §5
resize-registration finding is not one of this round's candidates and is not independently
readmitted; candidate-004 addresses the distinct planning/build input contracts in the supplied
candidate set. The Gate-dropped candidate-005 and Refute-dropped candidate-001 remain excluded.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three admitted findings are localized and fixable without rebuilding the Phase 5 architecture,
so `FAIL` is not warranted. Candidate-002 corrects the lifecycle diagram. Candidates-003 and -004
require changes to the public contract and declared §5 interface region.

The prior substantive correction counts were four, five, two, and two, followed by Round 5's
coverage-blocked result with one additional correction. This round resolves the coverage
precondition but admits three corrections, so convergence has not been reached. The next required
action is a scoped fix-up resolving candidate-002, candidate-003, and candidate-004 and appending
resolutions to this review. Because the fix-up must change §5, the interface change-trigger
requires a fresh verification round before Phase 5 can close.

## Resolutions

### candidate-002 — resolved

Re-derived the required interval from the publication prose: installation precedes synchronous
consumer delivery, and drawing opens only after every consumer succeeds. Section 4.11.3 now moves
`READY_UNPUBLISHED` to `PUBLISHED_NOT_OPEN`, then branches to `PUBLISHED` on complete success or to
replacement, close, and shaders-off publication on consumer failure. This gives the failure state
an incoming transition without changing the already-specified two-generation failure behavior.

### candidate-003 — resolved

Added `BufferPublicationResult.ProvenanceRejected`, carrying the candidate and accepted registry
fingerprints. The adjacent publication contract now states that mismatch performs no ownership
transfer, publication, generation change, or resize notification and leaves the candidate
caller-owned. Section 5 names the result and its state-preserving effect.

### candidate-004 — resolved

Defined `BufferPlanRequest` from the immutable values enumerated by §4.1: Phase 3 configuration,
Phase 4 registry and its fingerprint, capability profile, and runtime sizing inputs. Its adjacent
invariants require valid configuration provenance, equality between the supplied and registry
fingerprints, valid §4.11 runtime values, and one immutable capability profile. Section 5 now
lists `BufferPlanRequest`, `BufferBuildRequest`, and `BufferRuntimeInputs`, assigns their supply to
Phase 7, and distinguishes planning inputs from the main-depth/device/diagnostic build inputs.

### Notes deferred

None.

The §5 cross-phase interface region changed. Per the manifest trigger, Phase 5 requires a fresh
verification round before it can close.
