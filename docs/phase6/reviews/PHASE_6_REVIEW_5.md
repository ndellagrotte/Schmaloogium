# Phase 6 verification review — round 5

## 0. Method and reading order

This adjudication first re-derived the sole surviving candidate against the whole target,
`docs/phase6/v1/PHASE_6_DOC.md`; the governing Part I, Phase 6 assignment, document gate, and
mandatory template in `docs/design/v2.0-RC3/DESIGN.md`; the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`; and the manifest-selected binding interfaces of Phases 1, 3, and
4. The OptiFine-format sampler evidence was used only to confirm the observable alias rule, not
to expand a dependency contract.

Only after settling that interpretation did adjudication read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md`,
`docs/phase6/reviews/PHASE_6_REVIEW_2.md`,
`docs/phase6/reviews/PHASE_6_REVIEW_3.md`, and
`docs/phase6/reviews/PHASE_6_REVIEW_4.md`, in that order. Their resolutions are present in the
target and do not settle the current dependency-projection mismatch. There were no reading-order
deviations, no network use, and no agent fan-out. Gate dropped no candidates. Candidate-001 had
already been eliminated by Refute; candidate-002 was independently re-derived rather than
accepted from its incoming label.

## 1. Findings

### candidate-002 — Phase 6 consumes Phase 3 resource fields not granted to it

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:376-383`,
  `docs/phase6/v1/PHASE_6_DOC.md:869-870`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1203-1208`
- **Claim:** Phase 6 treats water-shadow state and world constants as Phase 3
  `ResourceRequirements` inputs even though Phase 3's binding consumer allocation grants Phase 6
  only center-depth and smoothing projections, and the closed algebra contains no
  `waterShadowEnabled` field.
- **Evidence:** `UniformConfiguration` includes `waterShadowEnabled`
  (`docs/phase6/v1/PHASE_6_DOC.md:376-383`), and the sampler plan uses “Phase 3's parsed
  `waterShadowEnabled`” to select unit 5 or 4 for `shadow`
  (`docs/phase6/v1/PHASE_6_DOC.md:869-870`). Section 5 then claims that Phase 6 consumes
  water-shadow and world-constant inputs through the closed `ResourceRequirements` algebra
  (`docs/phase6/v1/PHASE_6_DOC.md:1203-1208`). Phase 3's detailed ownership statement instead
  assigns Phase 6 only center-depth and half-life declarations and assigns shadow data to Phase 8
  (`docs/phase3/v1/PHASE_3_DOC.md:888-890`). Its closed algebra exposes `ShadowRequirements`,
  `CenterDepthRequirements`, `SmoothingConstants`, and `WorldRenderConstants`, but no
  water-shadow boolean (`docs/phase3/v1/PHASE_3_DOC.md:895-903`); its binding §5 consumer table
  grants Phase 6 “center depth/smoothing,” world constants to Phase 7, and shadow to Phase 8
  (`docs/phase3/v1/PHASE_3_DOC.md:1121`). The observable conditional sampler rule is real
  (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:191-197`), but that evidence does not
  authorize the claimed dependency path.
- **Required correction:** Remove world-constant consumption from Phase 6's dependency table.
  For the conditional `shadow` alias, either derive the condition from an already published,
  semantically equivalent input and align `UniformConfiguration`, §4.9, and §5 with that path, or
  obtain a freshly verified Phase 3 contract that publishes the exact value to Phase 6. Do not
  continue to attribute an absent field to `ResourceRequirements`.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation:

- The adopted Phase 3 declaration catalog and Phase 4 effective-layout, callback lookup,
  cache-key, activity-token, participant-order, generation, and instance-count contracts match
  their binding dependency surfaces.
- Phase 1 GL-facade, readback, error-replay, diagnostics, and recording-facade consumption remains
  consistent with its binding contract.
- The complete Appendix D mapping, cadence buckets, smoothing formulas, frame ordering,
  center-depth decision, fixed sampler-unit maps, notifier audit, custom-uniform ordering, and
  Phase 4 barrier trace remain substantively covered.
- Phase 6's promises to Phases 7, 8, 9, 11, and 13 are backed by schemas and lifecycle semantics
  elsewhere in the target.

Candidate-001 remains eliminated at Refute and is not revived. No Gate-surviving candidate was
cleared or dropped on independent re-derivation. Reading prior reviews last did not change the
disposition: Rounds 1–3 address different resolved interface and traceability defects, and Round
4's clean dependency statement did not examine this specific ResourceRequirements allocation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted dependency mismatch is a bounded contract correction rather than a structural miss,
so `FAIL` is not warranted. Literal `PASS` is unavailable while the correction remains.

The next required action is a fix-up resolving candidate-002 and appending its resolution to this
review. Because the correction must update the Section 5 dependency contract, the
`cross-phase-interfaces` change trigger is activated and a fresh verification round is required
before Phase 6 can close.

Trend: Rounds 1–3 reported 2, 2, and 3 corrections, Round 4 reported zero, and this round reports
one newly exposed dependency-interface correction. The prior apparent convergence was premature;
the loop has not yet returned to literal PASS, but the single localized defect does not justify
escalation to `FAIL`.

## Resolutions

### candidate-002 — resolved

Re-derivation confirmed that Phase 3 grants Phase 6 only center-depth and smoothing projections
from `ResourceRequirements`; neither its closed algebra nor its consumer table publishes a
water-shadow boolean or world constants to Phase 6. The target now removes `waterShadowEnabled`
from `UniformConfiguration` and removes water-shadow/world-constant consumption from §5.2.

The conditional alias instead uses an already-published, semantically direct input: for each
effective program, Phase 6 tests whether Phase 4's immutable `ProgramUniformLayout` contains a
sampler-compatible `watershadow` declaration. That layout is already the final declaration/type
truth used to build the sampler plan. A present declaration selects unit 5 for `shadow`; absence
selects unit 4. This matches the pack-facing rule “shadow (when watershadow used)” while avoiding
the non-equivalent inference that two allocated shadow depth buffers necessarily mean
water-shadow use.

The edits align §4.1, §4.9, and binding §5.2. Because §5 changed, the manifest's
`cross-phase-interfaces` trigger fires and Phase 6 requires a fresh verification round before it
can close.

### Notes deferred

None.
