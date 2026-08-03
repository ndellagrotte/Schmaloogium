# Phase 5 Verification Review — Round 33

## 0. Method and reading order

I independently re-derived the sole Gate-surviving candidate before consulting prior reviews. I
read the Phase 5 public `BufferEstateView` signatures, clear state machine and execution protocol,
and manifest-declared §5 interface region; the RC3 mandatory template and Phase 5 specification;
and the selected binding contracts of Phases 1, 3, and 4. The authoritative and dependency
surfaces confirm that this is an interface-completeness question internal to Phase 5's exposed
clear protocol. No supporting implementation evidence was needed.

Only after settling that interpretation did I read Phase 5 reviews 1 through 32 and their
resolutions. Earlier clear findings established the execution boundary, estate-owned full-clear
state, and constructible request shape, but no settled material declares that naming the plan type
alone substitutes for naming the public operation that produces it.

I used no network access, forbidden source, or prior-session transcript. There was no agent
fan-out or delegation. In accordance with the dispatched atomic-role instruction and the
verify-loop skill, I did not invoke the loop, run `scripts/verify`, or start another Codex session.
There were no deviations from the resolved reading contract, no candidates eliminated before
adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — The change-trigger region omits the public clear-plan operation

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:588`–`:594`,
`docs/phase5/v1/PHASE_5_DOC.md:1116`–`:1125`, and
`docs/phase5/v1/PHASE_5_DOC.md:1811`

**Claim.** Phase 5 exposes `clearPlan(ClearRequest)` as a distinct operation consumed by Phase 7,
but the manifest-declared cross-phase interface region names only `ClearRequest`, the returned
`ClearExecutionPlan` type, `executeClear`, and its result. The binding contract therefore does not
independently identify how Phase 7 obtains the immutable plan that `executeClear` requires, and a
change to the public planning operation can escape the interface change trigger.

**Evidence.** `BufferEstateView` declares separate public operations
`ClearExecutionPlan clearPlan(ClearRequest request)` and
`ClearExecutionResult executeClear(ClearExecutionPlan plan)`
(`docs/phase5/v1/PHASE_5_DOC.md:588`–`:594`). The detailed protocol assigns Phase 7 the exact call
`executeClear(clearPlan(request))` and separately specifies `clearPlan`'s effective-full-clear
derivation (`docs/phase5/v1/PHASE_5_DOC.md:1116`–`:1125`). Yet §5's exposed-contract row names
`ClearRequest`, `ClearExecutionPlan`, `executeClear`, and `ClearExecutionResult`, omitting
`clearPlan` and any equivalent statement that Phase 7 obtains the plan through that operation
(`docs/phase5/v1/PHASE_5_DOC.md:1811`). The governing mandatory template requires §5 to identify
the named interfaces exposed to dependents (`docs/design/v2.0-RC3/DESIGN.md:811`–`:812`).

**Required correction.** Add `clearPlan` to the §5.1 clear-contract row and state that Phase 7
calls `clearPlan(ClearRequest)` to obtain the immutable plan before passing it to `executeClear`.
Keep the row's validation, effective-full-clear ownership, and exactly-once execution semantics
synchronized with the detailed public interface.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The Round-32 color-only shadow
repair is consistent across planning, sizing, lifecycle, conformance, tests, staging, and §5. The
conformance map covers the RC3 gate and the in-scope Appendix B buffer, depth/shadow, fixed-unit,
and format requirements. Apart from the admitted clear-planning omission, Phase 5 §5 consistently
represents the consumed Phase 1, Phase 3, and Phase 4 contracts and the publication, frame,
depth-copy, shadow, overlay, resize, depth-source, and format surfaces.

Candidate-001 is not cleared by equivalent whole-document coverage. The detailed signature and
call sequence prove that `clearPlan` is public and consumer-visible, but they sit outside the
manifest's interface change-trigger region and therefore do not cure §5's omission. Prior reviews
2, 3, and 28 settled the clear executor, full-clear state transition, and `ClearRequest` shape;
none settled or intentionally excluded this operation from the named §5 contract. No candidate
was dropped on independent re-derivation, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a localized cross-phase contract omission, not a structural miss requiring
a rebuild, so `FAIL` is not warranted. The supplied prior-round trend is empty. Direct comparison
with Round 32 leaves the correction count at one; the loop has not converged because a fresh
consumer-visible operation omission remains after the preceding fix-up.

The next required action is a scoped fix-up resolving candidate-001 and appending its resolution
to this review. Because the correction changes the manifest-declared §5 cross-phase interface
region, the change trigger applies and Phase 5 owes a fresh verification round before it can close.

## Resolutions

### candidate-001 — corrected

Re-derived from the public `BufferEstateView` signature and §4.6 clear protocol, §5.1 now names
`clearPlan` alongside its request and result types and states the Phase 7 call sequence: obtain the
immutable plan through `clearPlan(ClearRequest)`, then pass it to `executeClear`. The existing
effective-full-clear ownership, generation/epoch/frame validation, exactly-once consumption, and
restoration guarantees remain unchanged. A compact §0.35 addendum records the correction.

This edit changes the manifest-declared cross-phase interface region, so Phase 5 requires a fresh
verification round before closure. No notes were deferred and no proposed correction was refused.
