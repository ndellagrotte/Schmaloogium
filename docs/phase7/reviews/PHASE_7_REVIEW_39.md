# Phase 7 — Independent Whole-Document Review 39

## 1. Review identity and verdict

**Target:** `docs/phase7/v1/PHASE_7_DOC.md`, sections 0–12 in their settled current form.

**Assigned identity:** `bfd1b2f9f6f3ed01e0a82434071f3e7b8102fb8422e4ace93a02356b46168065`. This is the commissioning identity, not a checksum recomputed during review.

**Intended report:** `docs/phase7/reviews/PHASE_7_REVIEW_39.md`; report text returned to Main without writing the file.

**Verdict: PASS.** No blocking defect or required correction was found in the current Phase 7 owner document. There is one receiver-scoped note concerning Phase 8’s handling of replay-delivery failure. That note does not require a Phase 7 §5 amendment and does not certify the affected Phase 8 receiver.

**Counts:** blocking 0; corrections 0; notes 1. **Phase 7 §5 impact: no.**

This is architecture verification only. It is not an implementation result, native hook-application result, conformance-tier result, dependent-wide clearance, or final §G5.3 integration verdict.

## 2. Authority, inputs, and source discipline

The governing authority was selected from the target header: `docs/design/v2.0-RC3/DESIGN.md`, Part I and the Phase 7 assignment at lines 1805–1953. Other phases’ governing versions were not substituted for Phase 7’s authority. `AGENTS.md` and `docs/MOVES.md` were read. The review covered the complete assigned Phase 7 document, the assigned RESEARCH sections, Appendix A.1 and Appendix E, the assigned Pintonium design sections, and the declared dependency §5 contracts with the detailed semantics material to Phase 7’s consumption.

Actual receiving contracts were inspected in P8–P14 where Phase 7 dispatches, publishes, retains, or retires their values. In particular, this included P8’s synchronous shadow activation and terminal return, P9’s held/ID scopes and publication, P10’s worker-drain/declaration lifecycle, P11’s expression composition and GUI projection, P12’s reload/option adapters, P13’s publication/lease/resource transitions, and P14’s accepted synchronous/JFR boundary. The entire `docs/PHASE_INTEGRATION_REVIEW.md`, including its dated follow-ons through the corrected-core/resource/state/evidence repairs, was read. Historical findings and resolutions were treated as claims to check against current contracts, not as new PASS evidence.

The assigned local Pintonium and Cleanroom aliases were absent. Exact upstream recovery, rather than a newer local checkout, supplied the reference evidence:

- [Pintonium commit identity](https://api.github.com/repos/Xplodin/Pintonium/commits/9c2fcc1) resolves to `9c2fcc1a4814cafc0242370757e9e05ea83c5be3`.
- Independently read both assigned pinned files: [MixinEntityRenderer_Shaders.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java) and [MixinRenderGlobal_Shaders.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinRenderGlobal_Shaders.java).
- Independently read the pinned [README](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/README.md) and [COPYING.LESSER](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/COPYING.LESSER). These support the LGPL evidence classification, not copied implementation or claims of universal pack success.
- [Cleanroom tag identity](https://api.github.com/repos/CleanroomMC/Cleanroom/git/ref/tags/0.6.6-alpha) resolves to `75e899b8a56ad5f1d97dc2e9e2586bb8c69022d0`. Both exact-tag [EntityRenderer patch](https://raw.githubusercontent.com/CleanroomMC/Cleanroom/0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch) and [RenderGlobal patch](https://raw.githubusercontent.com/CleanroomMC/Cleanroom/0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch) were read, including the renderer patch’s continuation.
- Schlorbium reading remained confined to the permitted lifecycle/frame-flow/one-frame digest sections, replacement list, and shipped author-document depth-property rows. No decompiled implementation was used.

Fresh MCP queries confirmed the two `renderBlockLayer` overloads and both ordinary/separate blend overload families. Framework queries checked `RenderWorldLastEvent`, `CameraSetup`, and the available render-world event result. These establish published signatures and event fidelity, not transformed-bytecode success.

No forbidden chatlog or root transcript content, Oculus transformation/library material, relocated GLSL code, or copied transformation implementation was read. Searches used explicit document/file allowlists. No builds, tests, linters, formatters, checksums, edits, or runtime validation were performed.

## 3. Literal doc-gate and whole-document audit

| Required check | Review result |
|---|---|
| Both mandated parts | Engine-side driver and concrete hook catalog are substantive and separated; the 7b fallback is not needed. |
| All eleven RESEARCH §7.1 hook needs | Frame lifecycle, camera, shadow invocation, render families, depth moments, deferred/final, vertex and chunk handoffs, atlas handoff, resize, and GUI/reload are traced to hooks/events or explicit owner-and-milestone deferrals. |
| Seven-row Pintonium starting timeline | Each starting hypothesis is adopted or explicitly corrected. The pinned renderer patch confirms clear precedes camera setup; current matrix capture therefore correctly remains at the later camera boundary. |
| Composite guarantee | The physical frame wrapper and terminal paths provide a stronger early-return/throw contract than the pinned Pintonium TAIL-only mechanism. Failures do not authorize further shader draws merely to pretend finalization succeeded. |
| Reference-free families | Sky, clouds, and weather are explicitly marked as lacking a working reference and have early proof obligations rather than inherited Pintonium success claims. |
| v0.1 assembly | Section 9 and the milestone checklist describe the dependency-ordered first render, including hook-health, capture, baseline pack, and later-feature gates. |
| OQ-3 and OQ-4 | Section 10 retains question, procedure, acceptance evidence and fallback. No-context-change remains the default; mapping or source evidence is not promoted into successful CleanMix application. |
| Thirteen-section template | Sections 0–12 are present and substantive, including contracts, failure/degradation, performance posture, tests, assembly, spikes, decisions and implementation work. |
| Scope and decisions | P7 owns orchestration and hooks, not source parsing, fallback resolution, physical texture binding, shadow content, vertex production, expression evaluation or option persistence. D-1 through D-10 remain dispositioned. |
| Provenance and deliberate departures | Research wins reference conflicts. Depth-copy timing, matrix capture, fixed sampler ownership, internal-pack behavior and the stronger finalization guarantee retain explicit rulings. Maintainer-authorized option-only supersampling and adjacent prepared-submission repetition are not silently rewritten research history. |

The conformance map was checked against actual Appendix A.1 program-family meanings and RESEARCH §4.4 ordering, rather than accepted from its row count. The current distinction between weather depth writes and depth testing agrees with `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:53–59`.

## 4. Load-bearing owner and receiving-path audit

### 4.1 Selection, positional routes, and duration state

P4 §4.10’s dispatch authenticates the retained selector, closes the predecessor lease after invalidating its activity, binds the selected program, acquires P1’s duration lease, reports effective blend, and only then dispatches the three participants. P7 §§4.4/4.6/5.2 receives that ordering rather than substituting immediate setters.

P1 §4.7.4’s duration mechanism, P4 D-P4-33/34, and P7 §§4.10.6/5.1 agree on nine cancellable HEAD sites, six blend RETURN sites, both overload families, no notification from suppressed attempts, and once-only coherent bypass publication. P7’s installed receiver at lines 2725–2757 pins device/bridge/provider/runtime identity through final restoration and successful P6 retirement. Missing registration is CORE failure, not optional observer degradation. Positional Attachment/None lists and the registry’s positional-route identity remain owner-issued; P7 neither collapses holes nor invents raw GL sentinels.

### 4.2 Mipmaps, copied depth, and staged evidence

P5 §4.2.1 and P7 §4.6 agree on the concrete `generateMainMipmaps(snapshot)` call before binding/activation, effective-provider membership, frozen readable sides, revision/filter freshness, and all result branches. In particular, `Failed(...,true)` is already frame-aborted: P7 explicitly prevents a second complete/discard/abort, including in outer cleanup. Degraded generation permits only restored base-filter sampling.

P7 §4.5 consumes both successful and degraded depth-copy points once. Failed weather copying therefore does not block the subsequent translucent point or authorize retries. The pre-weather depthtex2 moment remains distinct from the reference’s rejected hand-time placement.

P7 §4.13 and P2 §4.5.4 agree that live capture requires paired accepted REALIZED resource evidence. PLANNED evidence does not invent allocation/fallback results; fog remains a declarative policy rather than a fabricated constant. Compatible neutral backing remains P5-owned and preinitialized.

### 4.3 Capture, replay, and lifecycle

P7’s `/4` reader/producer receives P2’s exact complete option maps, option-state association, retained plan hash, sole canonical `gl.profile_text` scalar, dense pose/timing records, and runner-owned comparison authority. No `/3` reader, profile parser, plan echo or client-issued tier PASS survives.

P6’s required replay endpoint has a P7 construction owner and synchronous lossless receiver. Candidate, active, restoring and retiring observations remain ordered; repeated errors are not deduplicated. Pixel capture is not final acknowledgment: P7 waits for final restoration/release callbacks, receipt and successful retirement before sealing evidence. The remaining P8-specific dispatch issue is recorded below.

### 4.4 Publication and downstream ownership

The ten-step transaction preserves actual accepted P4/P5 generations, same-load P3 configuration/assets, P6 adoption and final-use retirement, P13 publication/lease ownership, then P9 publication and P10 matched worker/lookup/geometry admission. Resource-only NONE is an explicit quiescent same-configuration path, not an implicit P3 reload. Rejection, partial acceptance and compensation do not revive the old renderable tuple.

P12 receives final reload receipts and real registry mutations rather than deriving generation from configuration count. P11 diagnostics have a direct source-free final-attempt route. P14 accepts the synchronous baseline/JFR attribution and leaves async staging and optional instrumentation as separate grants. No missing optional optimization was treated as a required architecture defect.

## 5. Note N39-1 — Phase 8 must consume replay-delivery failure before shadow drawing

**Severity:** note in this Phase 7 owner review; required correction belongs to the Phase 8 receiver review.

**Locations:** P7 §5.1, lines 2029–2042; P6 §4.11, lines 1414–1428; P8 §4.7 step 9, lines 819–829, and §6’s participant-degradation row at line 1757.

**Evidence and impact:** P6 catches replay-sink failure and returns `Degraded` with `phase6.replay.delivery.failed`; P7 expressly requires receiver routes, including P8, to suppress another draw. P8’s synchronous slot nevertheless says `Activated` or `FixedFunction` proceeds to drawing and broadly retains the active program for participant degradation. Its world drawing occurs before the slot returns to P7, so P7’s outer-return containment alone cannot enforce that pre-draw boundary. The specific delivery-failure diagnostic/latch must be consumed inside the P8 activation-to-draw route, separately from ordinary allowed uniform degradation.

**Disposition:** independently checked and coordinated with the concurrent P8 reviewer, who owns the corrective finding. No additional P7 API or §5 amendment is established as necessary. This note prevents interpreting the owner PASS as certification of every actual recipient.

## 6. Evidence and readiness boundaries retained

Historical review receipts, dated author amendments, mapping answers and pinned source reads establish neither application counts nor successful execution. OQ-3/OQ-4, real alpha/blend interception and rollback, native geometry/list/state restoration, controlled capture hooks, installed shadow behavior, real resource realization and conformance runs retain their stated implementation gates.

Current dependency grants are coordinated architecture, not automatically verified consumption. This report does not erase the integration review’s original findings, the prior P7 review’s excluded-source qualification, or any pending leaf review. IR-01 and final §G5.3 integration remain open.

## 7. Final conclusion

The settled Phase 7 document satisfies its owner-side architecture and literal doc gate without a required correction. **PASS — blocking 0, corrections 0, notes 1; Phase 7 §5 impact false.** Main may preserve this report and integrate the independently owned Phase 8 receiving correction, but must not promote this result into native/runtime, dependent-wide, or final-integration clearance.