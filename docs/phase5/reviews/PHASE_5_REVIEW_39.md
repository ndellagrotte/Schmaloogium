# Phase 5 — Independent Whole-Document Review 39

## 1. Reviewed artifact and review boundary

- **Reviewed path:** `docs/phase5/v1/PHASE_5_DOC.md`.
- **Assigned frozen SHA-256:** `a7ebe46eef1a54a0369e87db2ef3e600c501036218ec34069793a12449edd513`. This is the identity supplied with the review assignment; no checksum/validation command was run.
- **Governing design:** the target header declares `docs/design/v2.0-RC3/DESIGN.md`. No global v3 override was applied.
- **Method:** fresh §G1.2 whole-document examination, including the complete target, incorporated dependency contracts, actual consuming dispatch paths, governing research, required reference material, integration findings and subsequent resolutions/rulings. Historical reviews and receipts were treated as claims of prior status, not independent proof of current correctness.
- **Execution boundary:** read-only architecture review. No files were edited; no builds, tests, linters, formatters or runtime validation were executed. No transcripts, chatlogs, root text dumps, prohibited Oculus pipeline/transformation sources, `libs`, `glsl-relocated`, or restricted transformation implementations were used.

## 2. Authority and substantive read set

### 2.1 Governing material

Read `AGENTS.md`, `docs/MOVES.md`, RC3 Part I, and the complete Phase 5 assignment, including its scope, required inputs, architecture requirements and literal documentation gate. Licensing/provenance rules were read before reference mining.

Read `docs/research/v1/RESEARCH.md` §§0–1, §3.6.3, §4.1, §4.3, Appendix B in full, and the incorporated Appendix F.5/F.7 custom-texture/flip semantics. The supersampling row and the surrounding authority conflict were checked against the recorded maintainer disposition, rather than treating the old label as an executable algorithm.

Read the complete `docs/PHASE_INTEGRATION_REVIEW.md`, including original findings, Resolutions and later follow-ons. Also read the complete `docs/decisions/U1_TEXTURE_SAMPLING.md` and `docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md`. The recorded pack-option-only supersampling decision, documented numeric-discriminator/sidecar policy and approved prepared-submission repetition were respected. None was treated as runtime or final-integration evidence.

### 2.2 Dependency and consumer contracts

- **Phase 1:** `docs/phase1/v14/PHASE_1_DOC.md` §5 in full, together with incorporated capability, handle, foreign/borrowed-depth, framebuffer, texture, state, recording/replay and parameter semantics used by Phase 5. The current complete `TextureParameters` contract was checked rather than assuming a three-field sampler setter.
- **Phase 3:** `docs/phase3/v1/PHASE_3_DOC.md` §5 in full and incorporated resource/directive semantics, especially the closed format and draw-routing algebras, per-program mipmap projections, per-shadow-texture PCF/filter settings, clear-color absence and schema21/same-load identity requirements.
- **Phase 4:** `docs/phase4/v1/PHASE_4_DOC.md` §5 in full and the relevant complete public/resource-access/routing/barrier/publication semantics. Selection authentication, effective-provider inheritance, pure validation and detached candidate metadata were traced independently.
- **Phase 6:** current factory/resolver consumption and §4.9 sampler dispatch, including Ready/Invalid handling, cache keys, exact names, upload-only ownership and the updated replay-sink factory input.
- **Phase 7:** actual main-frame, nested-scope, copied-depth and fullscreen dispatch, plus its Phase-5-consumption contract. This includes the concrete calls after `BackendDegraded` and the claimed main-buffer mipmap request.
- **Phase 8:** actual invocation transaction, shadow mipmap result dispatch, PCF semantics and §5 consumption, including the already-aborted `Neutralized` branch and exactly-once lease closure.
- **Phase 13:** current candidate/publication/lease producer declarations and binding hand-off, including retirement, expected-publication acquisition, full sampled shape and parameter/source identity.
- **Phase 2:** the canonical resource wire grammar and R10A runtime-snapshot request were checked as the receiving evidence contract.

### 2.3 Permitted reference evidence

Read the required Pintonium design §5, B4 and §18 flip disposition, plus the relevant fixed-unit comparison. The historical local `pintonium-9c2fcc1` directory is absent, so the required source was recovered at the actual upstream pin, not substituted with a newer checkout: [Xplodin/Pintonium at 9c2fcc1](https://github.com/Xplodin/Pintonium/commit/9c2fcc1).

The complete pinned `targets/` contents were examined: `BufferFlipper`, `ClearPass`, `ClearPassCreator`, `ClearPassInformation`, `DepthTexture`, `RenderTarget`, `RenderTargetStateListener`, `RenderTargets`, and the five `backed/` texture classes. Also examined the complete pinned `MixinFramebuffer_Shaders`, the depth-copy strategy, and the narrowly relevant `ShadowRenderTargets` allocation/flip/accessor ranges. Shipped `doc/shaders.txt` buffer, depth/shadow, format and pixel-type sections and the permitted allocation-behavior digest were read. Cleanroom 1.12.2 mapping output independently confirmed `Framebuffer#createFramebuffer(II)V`, `func_147605_b`, `useDepth/field_147619_e` and `depthBuffer/field_147624_h`.

No source was copied. The upstream root license identifies GPLv3; an older blanket LGPL label was not treated as permission for future copying or redistribution. The target already distinguishes the newer checkout's GPL evidence in its sampling investigation. Future implementation attribution remains independently gated. Note N1 below records a specific mismatch between the historical B4 description and the actual pinned method bodies.

## 3. Adversarial architecture audit

### 3.1 Literal documentation gate and all thirteen sections

All required §§0–12 exist and contain substantive material. The document reproduces the fixed App B.3 map, including `depthtex1=11`, fullscreen-only `depthtex2=12`, the conditional `shadow` alias and both gbuffers bands. The conformance map names the App B.1/B.2/B.4 obligations and all 37 pack-facing internal formats. Flip/clear behavior is expressed as state transitions and tables; the frame-end metadata rebase is a recorded decision; fog alpha is explicitly 1.0; the depthtex0 bridge is mapped to the required reference; shadowcolor uses a real state machine.

That structural gate coverage is not sufficient for approval by itself. The format-default receiving branch, main mipmap operation, resource projection and two degradation mechanisms have the concrete defects below.

| Sections | Audit outcome |
|---|---|
| §0 | Declares the correct governing revision and separates historical reviews from the coordinated unverified amendments. Receipts are not implementation authorization. |
| §§1–2 | Ownership and opaque publication/selection/lease boundaries are substantially explicit. Main mipmap execution is nevertheless promised without a callable Phase-5 contract. |
| §3 | Broad row-level conformance coverage exists. A listed format family does not supply the missing `DefaultRgba` branch; the B4 provenance description needs qualification. |
| §4 | Main flip/rebase, clear consumption, candidate cleanup, depth attachment invalidation and binding preflight are detailed. Findings C1–C5 identify remaining concrete contradictions or missing transitions. |
| §5 | Extensive producer/consumer declarations are incorporated rather than relying on nominal type names. The affected rows must change with the corrections below. |
| §6 | Failure classification generally separates mutation-free suppression from backend containment and preserves vanilla recovery. The copied-depth and neutral-shadow continuations are not fully realizable as written. |
| §7 | Render-thread confinement, bounded FBO planning and no steady-frame texture/FBO allocation are explicit. No false zero-allocation claim is made for lifecycle tokens. |
| §8 | Planned pure/recorded tests cover most lifetime and failure transitions. They do not replace the absent mechanisms, and the plan/candidate/runtime projection equality test currently pins an impossible requirement. |
| §9 | Main estate and shadow structure are staged separately from shadow execution and optional modernization. No later sampler-object optimization is available to rescue the neutral-object defect. |
| §10 | No OQ is assigned to Phase 5. The approved option-only supersampling disposition closes that authority question; inventing an SSAA spike is neither necessary nor authorized. |
| §11 | Binding decisions and remaining implementation/integration gates are recorded. Historical grant wording is not fresh certification. |
| §12 | Checklist and test hooks are substantial, but must be updated to implement the corrected contracts rather than merely their existing names. |

### 3.2 Ownership, lifetime and receiving dispatch

The shared-unit cutover is substantially coherent at the actual receiving boundaries: Phase 4 selects once; Phase 5 authenticates and resolves every required row before physical binds; Phase 4 activates the same selection; Phase 6 uploads only the resolver's integers; Phase 7/8 close only the transferred binding after Bound and otherwise close the acquired lease. Partial backend binds are not mislabeled mutation-free. Phase 8 explicitly stops after result-level `Neutralized(...,true)` without another complete/abort/neutralize operation. Retirement delays deletion, not stale-use rejection.

The main-depth bridge correctly distinguishes ordinary foreign textures from authenticated borrowed attachments, and it does not invent ownership of Minecraft resources. Same-extent identity/format changes invalidate snapshots; failed reattachment poisons the estate; extent mismatch remains a no-mutation rebuild request. Candidate failure ledgers and publication compensation do not revive accepted old resources.

A separate current Phase-4 dependency problem was also observed: its `DrawRouting.Explicit(List<BufferRef>)` and empty-`N` interpretation cannot preserve Phase 3's positional `DrawSlot.None` in a route such as `0N2`. No alternate preservation route was found in Phase 5. This is a producer-owned dependency defect, not a duplicate Phase-5 correction in the counts below; Phase 5's packed-attachment receiver must participate in that owner correction, and this review does not certify that dependency or final integration.

## 4. Corrections

### C1 — Represent the incoming plain-RGBA baseline in the allocation algebra

**Severity:** correction. **Location:** `docs/phase5/v1/PHASE_5_DOC.md:1266–1283`, §4.2; incorporated inventory/format contracts in §5.1 and §5.3. **§5 impact:** yes.

Phase 3 explicitly publishes `ColorAttachmentFormat.DefaultRgba`, meaning unsized/plain RGBA, separately from `Explicit(RGBA8)` (`docs/phase3/v1/PHASE_3_DOC.md:3554–3556,3574–3578`). Phase 5's closed allocation vocabulary contains only the 37 sized pack-facing formats plus `RGBA_COMPAT`, which it explicitly reserves **only for fallback**. There is no receiving branch for `DefaultRgba`, despite ordinary attachments starting with that value and §4.1 requiring every format to resolve into the closed enums before allocation. Thus a normal pack without an explicit format has no legal first-attempt format under the published contract. Mapping it to RGBA8 would erase the producer's deliberate unsized/sized distinction; mapping it to the existing compatibility value contradicts the fallback-only restriction.

**Required correction:** define the exact `DefaultRgba`→plain-RGBA allocation and projection mapping, retaining explicit RGBA8 as distinct. Either permit the existing plain-RGBA representation for baseline and fallback with explicit provenance, or publish a separate internal baseline representation. Keep the pack-facing enumeration at exactly 37 and cover baseline, explicit RGBA8 and conditional gdepth upgrade independently.

### C2 — Publish the main logical-buffer mipmap operation that Phase 7 must call

**Severity:** correction. **Location:** `docs/phase5/v1/PHASE_5_DOC.md:730–750,1290–1293`, §§2.2/4.2; §5.1 exposes only the shadow-specific mipmap operation. **§5 impact:** yes.

The document assigns logical-buffer mipmap execution to Phase 5, and Phase 7's actual fullscreen dispatcher requests that work for frozen readable sides before binding/activation (`docs/phase7/v1/PHASE_7_DOC.md:1154–1156`). Phase 4 likewise publishes `mipmappedBeforeRead` and names the Phase-5 operation in §4.4; Phase 3 supplies the per-program membership in its binding §5. But `BufferEstateView` has no main-buffer mipmap method, request/result algebra or failure lifecycle. `generateShadowMipmaps` accepts only shadow snapshots and shadow logical buffers. Consequently the main dispatcher cannot fulfill the published hand-off without inventing an API or moving texture/filter mutation into another owner.

**Required correction:** publish the main operation and its exact frozen-snapshot/request association, admitted buffers, capability handling, generation/filter state, per-buffer degradation and restoration-failure containment. Bind its timing to the effective provider's request and define the post-write/reset relationship so stale chains cannot remain advertised as fresh. Update Phase 7's concrete dispatch and Phase 5 §5/test hooks. A direct `TextureService.generateMipmap` call on borrowed snapshot handles is not a substitute for the missing owner protocol.

### C3 — Separate derivable plan evidence from runtime-resolved resource facts

**Severity:** correction. **Location:** `docs/phase5/v1/PHASE_5_DOC.md:1225–1247`, §4.1.1; §5.1 resource projection and §8.2 equality assertion. **§5 impact:** yes.

The pure projection requires a **fallback-resolved** format and four concrete clear-color doubles for every color row, and requires the identical immutable value in the pure plan, candidate and published runtime estate. Yet §4.7 allows requested allocation/FBO checks to fail and then successfully recreate every color side as plain RGBA (`:1587–1595`). Those driver outcomes are not inputs to the pure plan. Independently, the default colortex0 clear is the current fog RGB supplied later through `ClearRequest` (`:1514–1529`), while `BufferPlanRequest`/`BufferRuntimeInputs` contain no fog sample (`:532–542`). The same plan can therefore produce different actual format outcomes, and the same estate can clear with different frame colors. The promised exact final projection cannot be derived or kept identical without inventing values or misreporting runtime state. This reaches a real consumer: Phase 2 R10A requests the live snapshot, and Phase 7 is forbidden to reconstruct missing facts.

**Required correction:** explicitly distinguish planned/declarative evidence from realized allocation and frame-dependent evidence, or obtain the missing owner inputs at the appropriate stage and narrow the equality promise. Define the wire meaning of default/dynamic clear values with Phase 2, and ensure successful RGBA fallback is truthfully represented. Update the producer and receiving grammar together; do not make Phase 7 guess or preserve an equality test that contradicts admitted runtime outcomes.

### C4 — Consume an ordered copy point when its backend operation degrades

**Severity:** correction. **Location:** `docs/phase5/v1/PHASE_5_DOC.md:1797–1805`, §4.9; §5.1 `DepthCopyResult` row at `:2384`. **§5 impact:** yes.

The copy-point machine advances through PRE_WEATHER before PRE_TRANSLUCENT, and only `Copied` is assigned an advance transition. `BackendDegraded` changes the destination's validity/backing and promises continuation, but has no copy-point transition. Phase 7 concretely continues after PRE_WEATHER degradation and next calls PRE_TRANSLUCENT (`docs/phase7/v1/PHASE_7_DOC.md:1120–1130`). Under the specified transition table, the successful PRE_WEATHER state has not been reached, so the next scheduled call is out of order and requires abort. The once-per-point rule is also incomplete for a repeated failed point. This is a missing architecture transition, not a claim about unimplemented code.

**Required correction:** distinguish scheduled-point consumption from destination-copy success. A valid attempted point that returns BackendDegraded must have an explicit order/duplicate disposition allowing the documented feature-local continuation, while the destination remains degraded and resolves to depthtex0. Add the weather-failure→translucent-call and repeated-failed-point cases to the state-machine plan and receiving contract.

### C5 — Provide comparison-compatible neutral depth objects for independent shadow policies

**Severity:** correction. **Location:** `docs/phase5/v1/PHASE_5_DOC.md:1833–1839`, §4.10; neutralization and full-shape binding contracts in §§4.12/5.1. **§5 impact:** yes.

On shadow failure the candidate owns **one** fully-far depth fallback reused at units 4 and 5, supposedly configured to the pack's compare/filter expectations. Phase 3 and the shipped directive table explicitly permit independent hardware-filtering choices for shadowtex0 and shadowtex1. A valid main shader can therefore use a comparison sampler on one unit and an ordinary depth sampler on the other. Phase 1's current `TextureParameters` makes comparison mode object state (`NONE` versus `REF_TO_TEXTURE`); a single depth texture cannot hold both states simultaneously. Phase 5's full-shape preflight requires matching comparison state, so its one-object fallback either fails that check and suppresses an otherwise usable main draw, or violates the sampling contract. Phase 8 explicitly expects unavailable shadow state to preserve the main pipeline. Optional Phase-14 sampler objects are neither a granted nor baseline rescue path.

**Required correction:** allocate/cache neutral depth backings by the relevant compatible sampling policy, at least separating comparison and non-comparison requirements, and define their bounded ownership, initialization and retirement. Reuse is safe only when the required states are compatible. Exercise a shader declaring differently configured shadowtex0/1 through both creation failure and runtime neutralization, with Phase 14 inactive.

## 5. Non-blocking provenance note

### N1 — Qualify the B4 stub claim against the actual pinned method bodies

**Severity:** note. **Location:** `docs/phase5/v1/PHASE_5_DOC.md:1178–1179,2930,2977–2978`, §§3.6/11.1/11.3. **§5 impact:** no independently required interface change.

The target and historical PD/RC3 wording describe pinned `ShadowRenderTargets.flip()` as a stub and shadow access as always-main. The actual [9c2fcc1 source](https://github.com/Xplodin/Pintonium/blob/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/ShadowRenderTargets.java) retains a stale “TODO: Actually flip” comment, but the method body toggles `flipped[target]`, and `getColorTextureId` selects alt or main using `isFlipped(i)`. This does not establish complete shadow-pipeline correctness, nor does it undermine the decision to implement and test Phase 5's own real flip machine. It does mean the specific negative observation is not supported by the cited pin. Preserve the governing requirement for real state, but distinguish the historical B4 claim from what was independently observed; do not infer a no-op from the comment.

## 6. Counts, §5 impact and disposition

- **Blocking findings:** 0.
- **Corrections:** 5 — C1 through C5.
- **Notes:** 1 — N1.
- **§5 impact:** **yes**. The baseline-format mapping, main mipmap API, resource-evidence meaning, copied-depth transition and neutral-shadow backing semantics all affect binding interfaces or their incorporated semantics. Their owners and affected receivers require coordinated amendment and fresh review.

The architecture is substantially organized around explicit policy ownership, authenticated selections, atomic publication and bounded lifetime. The findings are discrete, repairable contracts rather than reasons to rebuild the architecture wholesale. Pending implementation/runtime/OQ evidence is not counted as a defect here, and this report grants no implementation, dependency or final-integration acceptance.

**Final verdict: PASS-WITH-CORRECTIONS.**

## Resolutions — 2026-09-08 (architecture amendment, unverified)

- C1 / D-P5-30: DefaultRgba first-attempt plain RGBA is represented independently of explicit
  RGBA8 and conditional RGBA32F, with REQUESTED versus RGBA_FALLBACK provenance.
- C2 / D-P5-31: generateMainMipmaps(PassBufferSnapshot) derives the exact frozen effective
  request, returns total Completed/Rejected/Failed outcomes, tracks physical-side write/chain
  revisions and filters, and contains failed restoration as an already-aborted stale estate.
  Write/clear/frame/reallocation resets prevent stale freshness; P7 integration receives it.
- C3 / D-P5-32: PLANNED evidence omits allocation; REALIZED carries actual allocation/origin.
  Default fog is a policy, not fabricated RGBA. /4 variant fields and declared-only cross-stage
  equality replace the impossible same-value promise; P2/P7 receiving edits are coordinated.
- C4 / D-P5-33: valid Copied and BackendDegraded both consume a scheduled point exactly once;
  degraded destination still falls back to depthtex0. Weather failure allows translucent;
  retry of either consumed point is mutation-free DuplicateIgnored.
- C5 / D-P5-34: neutral depth/color caches are bounded by the two required unit policies and
  keyed by full compatible sampling state; compare and ordinary depth never share incompatible
  object state. Initialize before readiness, retain through neutralization, retire exactly once.
- N1 / D-P5-35: independently read actual pinned 9c2fcc1 ShadowRenderTargets bodies. flip
  toggles and getColorTextureId selects alt/main despite a stale TODO. Historical authority and
  original review remain unchanged; real local flip behavior remains mandatory.
- P4 R32 positional-route dependency is adopted through P4/P1 owner grants in P5 §4.5/§5.
  All changed §5 contracts await fresh whole-document review. No validation/build/test/runtime
  commands or PASS certification accompany these resolutions. IR-01/final G5.3 remain open.