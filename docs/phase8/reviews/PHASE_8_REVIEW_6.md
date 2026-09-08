# Phase 8 — Independent architecture review 6

## 1. Reviewed artifact and authority

- **Reviewed document:** `docs/phase8/v1/PHASE_8_DOC.md`
- **Reviewed SHA-256:** `cb41ea863d9982202067aa42bbab5902a4351578a4dfcebc245d668ca601bb2b`
- **Numbered report destination:** `docs/phase8/reviews/PHASE_8_REVIEW_6.md` — report text returned for Main to preserve; this reviewer wrote no repository file.
- **Governing design:** `docs/design/v2.0-RC3/DESIGN.md`, as declared by the target, including Part I, §G1.2, §G9, and the complete Phase 8 specification and literal document gate. Design v3 was not treated as global authority.
- **Review mode:** independent whole-document architecture verification, not a patch-only check, implementation review, or runtime acceptance.

The target was read completely, including its thirteen numbered sections, historical amendments, current §5 grants and incorporation clauses, failure paths, decisions, and implementation checklist. Historical review conclusions and adoption receipts were treated as claims to check, not evidence that the present architecture or its dependencies had already passed verification.

## 2. Authority and evidence read set

### 2.1 Governing and research material

Read the governing RC3 Part I and Phase 8 assignment; `docs/MOVES.md`; `docs/research/v1/RESEARCH.md` §§0–1, the complete shadow-pass behavior in §4.5, relevant legal/provenance rules, Appendix A.3, B.2/B.3, shadow and celestial uniform rows in D.2/D.3, Appendix E rows 1–2, and the relevant engine-flag material. Read `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` introduction and §10.

Read the permitted published shadow/program/sampler documentation in `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` and only the assigned shadow-pass portion of `SHADER_ENGINE_IMPL.md`. No OptiFine implementation source, transcript, agent history, chatlog, repository-root text transcript, Oculus pipeline/transform source, transformation library, or glsl-relocated tree was used.

Read the original findings in `docs/PHASE_INTEGRATION_REVIEW.md`, its Resolutions, and subsequent recorded follow-ons. Read the binding rulings in `docs/decisions/U1_TEXTURE_SAMPLING.md`, `TEXTURE_SIDECAR_DEFAULTS.md`, and `GEOMETRY_PRIMITIVE_COMPATIBILITY.md`, together with Phase 4 §11.5's prepared-submission decision and evidence. Read Phase 8 review 5 and its Resolutions as historical material, then independently checked the current replacement celestial contract.

### 2.2 Dependency and receiving contracts

Read the complete current §5 regions of Phase 4, Phase 5, Phase 6, and Phase 7, following their relevant incorporated semantics rather than relying on table names alone:

- **Phase 4:** selection authentication and effective-provider identity; context issuance; activation and fixed-function release; activity-token lifetime; alpha/blend override restoration; participant/result handling; prepared-submission authority.
- **Phase 5:** shadow estate states and transaction operations; actual allocation extent; frozen ordinary and shadow readable sides; full sixteen-unit physical binding, preflight and lease transfer; depth split, completion/abort, mipmaps, neutralization and fixed sampler policy.
- **Phase 6:** construction and provider sampling; current-frame/camera values; celestial and matrix events; immediate-upload activity authorization; replay-result delivery and failure containment; adoption and final-use retirement.
- **Phase 7:** plan-before-provider construction and final-registry publication; frame sampling and post-camera capture; shadow invocation and execution-bridge dispatch; result continuation; main-hook bypass; ID, atlas, vertex and prepared-submission receiving adapters; current replay-result collector and reporting contracts.

Narrow additional owner/receiver reads covered Phase 1's shadow package and facade boundaries; Phase 2's nested owner-8 hook projection and current reporting receipts; Phase 9's alternate shadow ID admission; Phase 10's prepared native submission and authenticated shadow vertex-input delivery; and Phase 13's publication/expected-ID lease and current-atlas contracts. These were interface checks, not independent whole-document certifications of those phases.

### 2.3 Independently recovered reference evidence

The historical local reference directories were absent. Newer local trees were not relabeled as the required historical pins.

- [Pintonium commit identity](https://api.github.com/repos/Xplodin/Pintonium/commits/9c2fcc1) resolves `9c2fcc1` to `9c2fcc1a4814cafc0242370757e9e05ea83c5be3`. The pinned [README license statement](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/README.md) identifies LGPLv3.
- Read the pinned [ShadowMatrices.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/modern/src/main/shaders_java/net/irisshaders/iris/shadows/ShadowMatrices.java), relevant camera construction and draw-order portions of [ModernShadowRenderer.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/modern/src/main/shaders_java/net/irisshaders/iris/shadows/ModernShadowRenderer.java), and relevant angular/vector portions of [CommonShadowRenderer.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/CommonShadowRenderer.java) and [CelestialUniforms.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CelestialUniforms.java).
- [Cleanroom tag identity](https://api.github.com/repos/CleanroomMC/Cleanroom/git/ref/tags/0.6.6-alpha) resolves `0.6.6-alpha` to commit `75e899b8a56ad5f1d97dc2e9e2586bb8c69022d0`. Read its [RenderGlobal patch](https://raw.githubusercontent.com/CleanroomMC/Cleanroom/0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch) and relevant [ForgeHooksClient](https://raw.githubusercontent.com/CleanroomMC/Cleanroom/0.6.6-alpha/src/main/java/net/minecraftforge/client/ForgeHooksClient.java) setter and [MinecraftForgeClient](https://raw.githubusercontent.com/CleanroomMC/Cleanroom/0.6.6-alpha/src/main/java/net/minecraftforge/client/MinecraftForgeClient.java) getter.
- Repeated the assigned MCP framework event query, `render pass entity`, which returned no matching event. A broader method query found the public `ForgeHooksClient.setRenderPass(int)` operation. The pinned patch and getter/setter evidence support the document's explicit setter/getter protocol rather than an invented event.

## 3. Substantive adversarial audit

### 3.1 Literal document gate and conformance

The literal gate is substantially satisfied: camera matrices and angular conventions are written out; the orthographic and perspective branches are distinguished; signed-remainder snapping is specified; the culling-plane construction, AABB predicate, full traversal and sun-prism optimization are described; shadow Appendix A.3/B.2/D.3 items are mapped; and the added hook ledger uses SRG names, descriptors, expected counts and failure dispositions.

The reference cross-check is substantive rather than a citation-only adoption. The document rejects the reference's inconsistent perspective bottom-right entry, keeps the contract's solid → cutout-mipped → cutout order instead of inheriting the modern renderer's order, and confines snapping to the target-era orthographic behavior. The absence of a working 1.12.2 Pintonium traversal implementation is acknowledged rather than used as positive proof.

The full-view correctness fallback and optimization-disable rule are specified. This review does not claim that the proposed second vanilla setup, visited-marker isolation, synthesized planes, or far-caster completeness have been demonstrated at runtime. Their recorded tests and conservative fallbacks remain implementation gates.

### 3.2 Celestial replacement and cross-boundary dispatch

The correction following review 5 now supplies a genuine dataflow contract. `ShadowCelestialPolicy.sample(float)` returns the pure angular record; it does not manufacture a frame-bound event before the camera exists. The sole camera computation takes the actual `ShadowFrameView`, post-camera `CameraSnapshot`, plan and allocated extent. Current-frame identity comes from the frame, while eye-space vectors use the supplied main-camera model-view with direction-vector semantics.

The consuming side was checked: Phase 7 installs the angular policy before provider sampling, retains the same sun/sky sample, captures the main matrices, authenticates their association with the live invocation and passes the exact values to Phase 8. Phase 6 accepts the unchanged celestial event shape and retains its own identity/finite-value checks. No second event API or Phase 6 dependency on shadow implementation is required. The earlier missing celestial mechanism is therefore not repeated as a finding.

### 3.3 Binding, ownership and failure paths

The same-selection path is coherent through Phase 7 selection, Phase 8 validation, Phase 5 snapshot and physical binding, and Phase 4 activation. The target no longer substitutes metadata for actual texture binding, a four-row shadow-only view, a second selector, or a prior-frame physical binding.

All four texture-binding results have distinct treatment. `Bound` alone transfers the acquired overlay lease into the binding snapshot; preflight rejection/degradation does not transfer it; backend failure is correctly allowed to have partially mutated GL. Completion or invalidation does not discharge the separate close obligation. The post-GL suppression paths return `Completed` only after safe cleanup instead of falsely claiming mutation-free rejection.

The single pre-translucent depth copy, optional translucent terrain, independent Forge pass 1, mipmap result-level neutralization, and no-double-abort rule are specified. The active publication owns the slot, invocation inputs remain borrowed, and closing an invoking slot is rejected rather than racing final use.

The remaining defect is at the other side of this transaction: the mandatory cleanup does not actually end the Phase 4 shadow activation before restoring platform state. This is detailed in §4 below.

### 3.4 Adjacent receivers and integration rulings

The current P7/P9 alternate ID admission does not open a main gbuffers scope during shadow rendering. Current-bind atlas evidence has a receiving adapter rather than an inferred atlas from stale unit state. P7/P10's v0.5 count contract repeats prepared native submissions with per-copy instance values, not world traversal, Forge callbacks, upload/capture setup, or whole entity calls. Conditional primitive compatibility remains within the separately authorized native submission boundary and does not turn Phase 8 into a replacement renderer.

The current P6 replay-report collector has a real receiving contract in P7, including shadow-related immediate routes, ordered retention and non-success after evidence-delivery failure. It is not treated as an additional barrier participant or a logging-only endpoint. Schema21 and current `/3` reporting receipts are current authority; older numbered receipts remain historical. Maintainer rulings on option-only `superSamplingLevel`, numeric texture target discriminators, sidecars and prepared submissions were not reopened as unresolved research questions.

### 3.5 Scope, template, OQs and provenance

All thirteen §G9 sections are present and substantive. Phase 8 has no assigned research OQ; §10 correctly distinguishes ordinary implementation-risk experiments from an invented OQ. The traversal-oracle, hook-health, feature-neutralization and pipeline-failure outcomes have explicit fallbacks. Missing runtime measurements are not counted as missing architecture simply because they remain unexecuted.

Shadow target allocation and texture parameters remain Phase 5-owned, barrier mechanics Phase 4-owned, and `shadowcomp` remains outside the v0.2 execution scope. D-1 through D-10 are not contradicted by the inspected design. Reference mechanisms are contract-checked, their deliberate deviations are recorded, and prohibited transformation sources were not needed. The document's existing ownership and verification gates must still be discharged independently; this report does not certify them by association.

## 4. Findings

### R6-1 — Explicitly release the shadow barrier before restoring platform state

**Severity:** correction. **§5 impact:** yes.

**Location:** `docs/phase8/v1/PHASE_8_DOC.md` §4.2, step 13, lines 825–832; related promise in §4.10, lines 1251–1257; incorporated transaction and barrier contracts in §§5.1–5.4.

**Claim under test:** the complete invocation transaction ends the `shadowPass=true` activation interval before fixed-function/platform restoration, and `Completed` means the caller can safely resume the main frame.

**Evidence:** step 9 activates the Phase 4 barrier. Step 13 then closes the binding or acquired lease and restores traversal, Forge-pass and shadow-state scopes. It contains no barrier release operation or release-result branch. The `ShadowStateLease` receives a camera and execution view, not the registry and a release-kind context; its specified restoration list covers platform state, not the private barrier's activity token and override ownership. References elsewhere to restoring IDs or instance values “before shadow release” do not supply the missing terminal operation.

The receiving owner is explicit. Phase 4 §4.10, lines 1707–1760, keeps `BoundProgramActivityToken` current until a later activation, fixed-function release, failure/off or publication transition; `releaseToFixedFunction` restores the provider's alpha/blend lock before binding fixed function. Its release context must be the current frame's issued release-kind context, not the borrowed shadow activation context. Phase 7 §4.3, lines 1031–1046, closes only its execution bridge after `invoke` and proceeds to main bind/clear; it does not perform the omitted release on the successful shadow return path. Closing a Phase 5 binding snapshot or a Phase 7 execution view is not one of Phase 4's release/invalidation operations.

**Impact:** after a successful shader-backed shadow pass, the literal transaction can return to platform restoration and main-frame continuation with the shadow program's activity token and provider lock still active. Restoration-time immediate uniform signals remain authorized against that shadow activation, and the later main activation can restore the old shadow lock's underlying snapshot after Phase 8 has already restored its saved platform state. At minimum, the promised end-of-shadow interval has no executable terminal transition. This affects the ordinary successful shadow path, not just a speculative backend fault.

**Required correction:** specify the Phase-8-owned terminal call through the existing published barrier using the current `FrameBarrierContexts.release()` context. Place it after required per-draw ID/color/instance restoration and before platform fixed-function/state restoration. Define which release result proves safe continuation and route stale/failed/unproven release through the existing `Failed`/off containment without skipping independent snapshot, lease or state cleanup. Preserve select-once and Phase 7's sole execution-bridge ownership. Update the incorporated §5 transaction/barrier contract and the corresponding failure/checklist coverage. No new public API or structural redesign is required.

## 5. Counts, cadence and verification limits

| Severity | Count |
|---|---:|
| Blocking | 0 |
| Correction | 1 |
| Note | 0 |
| **Total findings** | **1** |

**§5 impact: yes.** The repair changes the invocation lifetime/terminal-state contract incorporated into §5, not merely descriptive prose. Apply the governed correction and fresh verification cadence; this review does not authorize implementation while the target or its required owner/receiver gates remain open.

No files were edited. No build, test, linter, formatter, render session, conformance run or implementation-validation command was executed. Evidence consists of document, owner/receiver, mapping and pinned reference inspection plus the recorded artifact identity. Pending runtime proof, dependency verification and final fourteen-phase integration remain separate gates.

## 6. Final verdict

**PASS-WITH-CORRECTIONS** — the whole-document architecture and current celestial replacement are substantially coherent, but the successful shadow transaction needs an explicit, result-checked Phase 4 release before platform restoration. This is one bounded lifetime/terminal-transition correction, not a structural rebuild.

## Resolutions

### R6-1 — Corrected in architecture (2026-09-08; D-P8-23)

P8 §4.2 now arms a release obligation before its first platform mutation and calls the supplied
current-frame `releaseToFixedFunction(context.barrierContexts().release())` after required
per-draw ID/color/instance restoration and before platform restoration. Only `FixedFunction`
proves release for continuation. ShadersOff, FailedSafe, StalePublication, unexpected
Activated/Skipped, unusable context/barrier and throws yield Failed/off containment; none skips
independent snapshot bookkeeping, binding/acquired-lease closure or platform cleanup.
Absent/unavailable early Completed proves no activation/state scope opened; mutation-free
rejections cannot masquerade as post-GL outcomes. Failed activation remains Failed even when
later cleanup succeeds. Unproven program/state recovery prevents all rendering, not just shaders.

P8 §§4.10/5.2/5.4/6/8.2/12 incorporate the terminal contract and planned failure coverage.
P7 §§4.3/5.1/D-P7-40 receive it: no main bind/clear after Failed/unproven terminal and no
duplicate release on successful return; P7 retains sole execution-bridge closure.
Main still integrates final P1/P4 lock and P2/P5 evidence/mipmap receiving terms identified in
P8 §11.3. D-P8-24 separately adopts `/4` reporting labels without a `/3` compatibility route.

The original review body and verdict remain unchanged. §5 is changed/unverified; fresh
whole-document owner/receiver review, IR-01 and final §G5.3 remain open. No runtime,
implementation, builds, tests, lint, formatters or validation commands were performed.