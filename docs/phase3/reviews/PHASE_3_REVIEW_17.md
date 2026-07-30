# Phase 3 verification review — round 17

## 0. Method and reading order

I independently re-derived the sole surviving candidate from the complete Phase 3 target, the
selected Part I, Phase 3 specification, document gate, and mandatory-template material in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, the selected binding Phase 1 §5 contract, and the permitted
supporting evidence. I searched the complete target for equivalent ownership and consumer
projections for the generated or overridden noise texture, `NoiseRequirement`, and
`NoiseTextureSpec`.

Only after settling the candidate disposition did I read prior reviews 1 through 16, in order,
including their resolutions, with Round 16 read last. There were no deviations from the required
reading order, no network use, and no agent fan-out. The dispatched-role exception in the supplied
`verify-loop` skill was followed: I did not invoke the verification harness or start another
session. I read no forbidden source.

The Gate reported no drops, and no candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — `ResourceRequirements` omits Phase 13 from the noise-requirement projection

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:875-877` and
`docs/phase3/v1/PHASE_3_DOC.md:1069-1072`

**Claim:** The new closed `ResourceRequirements` contract does not assign every public component
to its downstream consumer because Phase 13 is omitted from the `NoiseRequirement` projection.

**Evidence:** The aggregate expressly includes a noise requirement
(`docs/phase3/v1/PHASE_3_DOC.md:865-873`) and closes it as the distinct
`NoiseRequirement noise` component (`docs/phase3/v1/PHASE_3_DOC.md:882-890`). Yet the detailed
consumer projection assigns subsets only to Phases 4, 5, 6, 7, 8, and 10
(`docs/phase3/v1/PHASE_3_DOC.md:875-877`). The binding §5 row likewise describes the
shadow/center-depth/noise records but assigns no `ResourceRequirements` subset to Phase 13
(`docs/phase3/v1/PHASE_3_DOC.md:1069-1072`). Equivalent coverage does not repair that omission:
the ownership table assigns loading and uploading custom/noise textures to Phase 13
(`docs/phase3/v1/PHASE_3_DOC.md:185-188`), while §3 separately projects
`texture.noise=<pack path>` as `NoiseTextureSpec.Override` and otherwise retains the generated-noise
requirement (`docs/phase3/v1/PHASE_3_DOC.md:520`). The separate lossless texture-source spec
therefore cannot replace the enable/resolution input carried by `NoiseRequirement`.

**Severity:** correction. Add Phase 13 as the consumer of
`NoiseRequirement(enabled, resolution)` in §4.7's projection sentence and in the binding §5
`ResourceRequirements` consumer column, while retaining `NoiseTextureSpec` as the separate
override-source input.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The governing Phase 3 scope, document gate, mandatory thirteen-section template, complete
  target, selected Phase 1 binding interface, and Phase 3 §5 publication region were checked.
- The Round 16 `ResourceRequirements` algebra is otherwise closed and coherent: it defines the
  top-level components, keys, immutable ordered collections, defaults and absence cases, and the
  remaining named consumer projections.
- The Round 15 declared-uniform fingerprint correction remains acyclic and deterministic, and the
  current verification-status wording correctly records that the §0.19 bytes require Round 17.
- The finder-reported conformance areas remain clean: every Appendix F key and Appendix A.3
  directive is mapped, engine-flag ownership is complete, and equivalent detailed coverage exists
  for the remaining in-scope contract families.
- Phase 3 consumes only contracts published by the selected Phase 1 binding interface and requests
  the missing jcpp dependency allowance instead of assuming it.
- Prior reviews do not settle `candidate-001`. Round 16 created the closed algebra and its
  resolution asserted projections for each named consumer, but the resulting detailed and binding
  projections omit Phase 13 even though the new algebra exposes a distinct noise requirement.
- No candidate was refuted or cleared on re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a narrow consumer-projection correction, not a structural miss requiring a
rebuild. Round 16 had two corrections and Round 17 has one on the new §0.19 surface, so the count is
decreasing but the current bytes have not converged to literal PASS.

The next required action is a scoped fix-up resolving `candidate-001` and appending this review's
`## Resolutions`. Because the repair must change the binding §5 cross-phase-interface region, the
interface change trigger fires and a fresh verification round is required before Phase 3 may
close.

## Resolutions

### candidate-001 — resolved

Re-derived from `docs/research/v1/RESEARCH.md`: Appendix A.3 defines
`noiseTextureResolution` as enabling and sizing `noisetex`, §4.6 assigns generation/upload of that
texture to the texture system, and Appendix F.5 makes `texture.noise` an override of the generated
texture rather than a replacement for its enablement/resolution contract. Phase 13 therefore needs
both inputs.

Added Phase 13's `NoiseRequirement(enabled, resolution)` projection to §4.7 and to the binding §5
`ResourceRequirements` consumer column. `NoiseTextureSpec` remains the separate lossless
override-source contract. Added compact §0.20 bookkeeping and advanced the open-loop status to
require round eighteen. The §5 cross-phase-interface region changed, so the manifest trigger fires
and a fresh verification round is required before Phase 3 can close.

### Notes deferred

None.
