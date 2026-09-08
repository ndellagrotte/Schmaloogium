# Phase 5 — Attempt-6 whole-owner architecture review, R43

## Frozen identity and disposition

- **Owner:** `docs/phase5/v1/PHASE_5_DOC.md`, all sections **0–12**, current 3,474-line document.
- **Frozen SHA256:** `b03b661108e74059e63d69d798a063b51a4ea6e189c62ffd5b370d17b03092d6`.
- **Identity source:** the Phase 5/R43 entry in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_6.json`. This review records the supplied frozen identity; it does not claim an independently executed checksum command.
- **Result:** **PASS-WITH-CORRECTIONS** — one substantive required correction, zero advisory notes; **§5 is affected**. This is not implementation clearance or final integration certification.

## Scope, authority, and method

This was an independent whole-document review, not a confirmation of D-P5-42 through D-P5-45 or an adoption of previous review verdicts. I read the entire current owner, including its incorporated declarations, detailed algorithms, binding §5, failure handling, acceptance plans, decisions, historical qualifications, and implementation handoff.

The governing design is the revision selected by the owner's header: `docs/design/v2.0-RC3/DESIGN.md`, not the globally newest design (`docs/phase5/v1/PHASE_5_DOC.md:17–20,346–351`). I read RC3 Part I (`:92–1109`) and its complete Phase 5 assignment (`:1572–1685`), the governing portions of `docs/research/v1/RESEARCH.md` including §§0–1, 3.6.3, 4.1/4.3 and Appendix B, and the relevant Appendix F sampler/flip rules. `docs/MOVES.md` was consulted for versioned citation resolution. The explicit pack-option-only supersampling disposition was treated as the current authorized correction, not as an outstanding requirement to implement engine SSAA.

Dependency §5 contracts were read in full in `docs/phase1/v14/PHASE_1_DOC.md`, `docs/phase3/v1/PHASE_3_DOC.md`, and `docs/phase4/v1/PHASE_4_DOC.md`, with their incorporated load-bearing declarations. Reciprocal checks covered the actual receiving contracts in Phases 6, 7, 8, 13, and 14. These checks establish Phase 5's interface assessment only; they do not certify sibling owners.

No repository file was changed. No formatter, linter, build, test, native execution, or pack-tier validation was run.

## Independent checks and evidence

1. **Resource planning, counts, allocation vocabulary, and fallback.** Checked the distinction between declarative PLANNED evidence and REALIZED allocations, counts and limits, the 37 pack-facing formats versus private plain-RGBA compatibility allocation, and whole-estate fallback. The format/allocation contract at `docs/phase5/v1/PHASE_5_DOC.md:1327–1469` agrees with the documented format inventory in `docs/research/v1/RESEARCH.md:1257–1267` and the permitted shipped shader documentation. The BGRA versus RGBA_INTEGER null-allocation behavior is separately corroborated by the permitted behavioral digest at `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:209–213`; it is not promoted into a new raw-upload format.

2. **D-P5-42 synchronous values and their consumers.** Traced the target-bearing TextureSpec/TextureData/TextureRegion/TextureExtent/PixelLayout contract from Phase 5's `:1407–1469` and binding row `:2608` into Phase 1's shared values/backend obligations at `docs/phase1/v14/PHASE_1_DOC.md:4200–4485`, and Phase 13's exact source conversion at `docs/phase13/v1/PHASE_13_DOC.md:970–1090`. Checked scalar versus packed byte sizing, target-specific dimensions and mip constraints, explicit main/depth defaults, and the separation of private fallback from legal pack raw declarations. No additional correction was found in this amendment.

3. **Complete owned-object baseline and sampler state.** Checked Phase 5's current object-state obligation (`docs/phase5/v1/PHASE_5_DOC.md:2609`) against the Phase 1/P14 receivers, including `docs/phase14/v1/PHASE_14_DOC.md:586–656,711–747`. Successful parameter changes, temporary mipmap filtering, and restoration must update the complete owned baseline; sampler-cache state is not a substitute. The native rationale was independently corroborated by the Khronos [glBindSampler reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBindSampler.xhtml): binding sampler zero exposes the texture object's sampling parameters. Optional asynchronous APIs remain ungranted.

4. **Flip, pass, routing, and prelude ownership.** Read and traced the full side-state and frame-end decision at `docs/phase5/v1/PHASE_5_DOC.md:1530–1642`, route realization at `:1643–1691`, and the exact contained-prelude receipt at `:2681–2687`. Compared them with Phase 4's stage/pass contracts and Phase 7's actual traversal/acquisition/cleanup paths. The current contract preserves positional draw-route holes, consumes the actual virtual descriptor before indexed raster passes, does not invent a provider or execute the prelude twice, and distinguishes pre-mutation rejection from already-consumed-frame failure. Current schema23 receipt at `:2668–2677` explicitly supersedes earlier numeric receipts; historical schema language was not treated as a current defect.

5. **Depth, shadow, publication, and resize.** Checked `docs/phase5/v1/PHASE_5_DOC.md:1784–2338`, including borrowed-depth authenticity and lifetime, same-extent reattachment versus rebuild, first-copy versus storage-preserving copy, shadow availability/neutralization, acknowledged resize baselines, and publication/retirement ownership. The receiving Phase 7 same-extent handling at `docs/phase7/v1/PHASE_7_DOC.md:1104–1106` requires reacquisition rather than use of invalidated snapshots. Phase 8's `docs/phase8/v1/PHASE_8_DOC.md:1720–1830` receives the sixteen-row binding and terminal shadow outcomes. Cleanroom 1.12.2 class metadata independently confirmed the Framebuffer creation descriptor and relevant mapped fields; this is mapping evidence, not runtime proof.

6. **All-buffer fixed binding and reciprocal dispatch.** Checked the full exact-name, stage/context, compatibility, alias, candidate-order, lease, and sixteen-row physical-binding protocol at `docs/phase5/v1/PHASE_5_DOC.md:2339–2540`, not merely the logical resolver. Traced its outputs into Phase 6's sampler resolution and full eight-argument factory (`docs/phase6/v1/PHASE_6_DOC.md:1220–1310,1802–1840,1890–1960`), Phase 7's binding/result handling, Phase 8's shadow receiver, and Phase 13's candidate/lease publication (`docs/phase13/v1/PHASE_13_DOC.md:1503–1620`). No additional owner defect was found in result dispatch, selector authentication, lease transfer, or unused-unit normalization.

7. **Clear execution, failure containment, and acceptance design.** Reviewed ordinary/full-clear selection, disabled-clear overrides, physical sides, ordered batching, once-only plan execution, restoration, and full-clear retention. This uncovered Correction 1 below: the current architecture admits integer attachments but cannot express the clear operation required to initialize them with defined values through its granted facade. The defect is visible at the architecture boundary without running a future implementation.

## Required correction

### 1. Define integer-aware color clears and request the corresponding Phase 1 operation

**Severity:** P2 — incorrect initialization for a supported attachment format; substantive contract defect, not a documentation nit. **Confidence:** high.

**Current owner evidence:**

- `docs/phase5/v1/PHASE_5_DOC.md:1344–1345` admits signed and unsigned integer color attachments.
- `docs/phase5/v1/PHASE_5_DOC.md:1693–1723` defines floating-component clear inputs/defaults and says an explicit Phase 3 clear color replaces all four components, without an integer-format exception or conversion rule.
- `docs/phase5/v1/PHASE_5_DOC.md:1735–1738` groups clear operations by `(extent, color, side)`; numeric class is not represented in the batching policy.
- `docs/phase5/v1/PHASE_5_DOC.md:1740–1752` promises successful execution of these batches and permits that success to consume the estate's mandatory full-clear requirement.
- `docs/phase5/v1/PHASE_5_DOC.md:2594,2650` incorporates exact-color execution into binding §5 and consumes the Phase 1 facade for clearing.

**Consuming-side evidence:** `docs/phase1/v14/PHASE_1_DOC.md:3330–3333` exposes only `clearColor(float,float,float,float)` and `clear(EnumSet<ClearTarget>)`. Its native-operation mapping at `:5020` associates this surface with `glClearColor`/clear. A search of the complete current Phase 1, Phase 5, and Phase 14 owners found no integer/typed color-clear operation or grant. Phase 5 therefore cannot satisfy the promised operation by silently selecting an already-granted typed clear; issuing a direct native clear would violate its facade boundary.

**Normative evidence and observable breakage:** Khronos [EXT_texture_integer, §4.2.3](https://registry.khronos.org/OpenGL/extensions/EXT/EXT_texture_integer.txt) explicitly states that clearing integer color buffers with a floating-point-specified clear color has undefined results, and distinguishes signed and unsigned clear setters. The [glClearBuffer reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glClearBuffer.xhtml) likewise requires the appropriate floating, signed-integer, or unsigned-integer color-clear entry point; using the wrong category is undefined and need not produce an error. For example, a supported `R32UI` colortex attachment with explicit clear color `(1,1,1,1)` has no defined unsigned clear path in the present facade. A same-extent RGBA8 attachment with that color can also enter the same stated batching class. The clear can appear successful while the integer attachment is not initialized to the promised value, and the mandatory full-clear flag can then be consumed. The problem applies even to an integer-only batch; mixed batches expose the missing category distinction additionally.

**Minimal owner fix:** Define the clear payload/dispatch semantics for floating/normalized, signed-integer, and unsigned-integer realized attachments, including a deterministic conversion/range policy from the finite Phase 3 clear components. Make batching numeric-class-compatible, or explicitly dispatch typed clears per attachment. Add the exact required Phase 1 typed-clear capability to Phase 5 §5.5 and coordinate its real facade/backend/recorder receipt rather than assuming it exists. Update the incorporated clear contract in §5 and the architecture acceptance cases for ordinary and mandatory full clears, including mixed numeric classes and failure retention. Preserve current side selection, clear-disabled override, generation/frame checks, restoration, and all-batches-success rule. Do not silently downgrade integer formats or add direct GL access as a workaround.

**Attribution and impact:** This finding belongs to Phase 5's format/clear policy and its missing upstream request. The coordinated Phase 1 receiver change is necessary, but this report issues no separate Phase 1 verdict. **§5 impact: yes.** A fresh owner/receiver review is required after the correction.

## Source confidence and limitations

The historical local `reference-src/pintonium-9c2fcc1` directory is absent; that fact was not treated as proof that all pinned evidence is unavailable. Exact pinned upstream files were independently retrieved under [Xplodin/Pintonium at 9c2fcc1](https://github.com/Xplodin/Pintonium/tree/9c2fcc1), including [BufferFlipper](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/BufferFlipper.java), [ClearPassCreator](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/ClearPassCreator.java), [RenderTargets](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/RenderTargets.java), [DepthCopyStrategy](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/texture/DepthCopyStrategy.java), [ShadowRenderTargets](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/ShadowRenderTargets.java), and the [Framebuffer mixin](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinFramebuffer_Shaders.java). They corroborate specific mechanisms, not compatibility certification. In particular, float-style reference clear batching does not prove defined integer clearing. The pinned root license is GPL-3.0; historical blanket LGPL descriptions were not adopted as present authority, and the owner's later provenance qualification was retained.

OptiFine evidence was limited to the permitted shipped documentation and behavioral digest. No decompilation mining, forbidden Oculus pipeline/transform code, libs/glsl-relocated, glsl-transformer implementation, chatlogs, or root transcript files were read. Native behavior, real-pack rendering, and runtime failure recovery remain future implementation evidence, not conditions invented for this architecture gate.

## Final verdict

**PASS-WITH-CORRECTIONS**

The current Phase 5 architecture is sufficiently structured for a discrete owner correction rather than structural rejection, but its integer-clear obligation cannot presently be implemented through the granted facade with defined results. Resolve Correction 1 and its §5 receiver contract, then obtain fresh verification. No literal PASS, implementation clearance, sibling certification, or final BUILD integration approval is issued by this review.

## Resolutions

2026-09-08 architecture-only fix-up; original report body, frozen identity, confidence
and PASS-WITH-CORRECTIONS verdict are preserved.

- **Correction1 — corrected by D-P5-46 and actual P1 grant D-P1-67.** P5 §4.6 now
  defines finite realized-format conversion/ranges, signed versus unsigned saturation,
  typed value grouping and per-attachment positional route dispatch. P1 §4.7.4b grants
  FramebufferService.clearColorAttachment and exact Floating/Signed/Unsigned records,
  strict class/route/capability admission, GL3 matching glClearBuffer forms and GL2
  EXT_texture_integer typed setters with isolated route, exact full-state/cache
  restoration and distinct work/restore recorder errors. Unsupported integer capability
  is rejected before allocation, never silently downgraded or bypassed through raw GL.
  The clear initializes defined-but-uninitialized storage; partial work is not rollback.
  P5 §4.6 and shadow §4.10 retain side/generation/epoch/frame/once checks, disabled-clear
  full override, failure retention and consumption only after every attachment and all
  restoration succeed. Live §5.1/§5.2/§5.5, §8.1 and §12 incorporate these obligations.
  Khronos EXT_texture_integer §4.2.3 and glClearBuffer supply the native numeric-class/
  tier evidence; deterministic finite saturation is identified as local policy, not
  newly observed G6 behavior.
- **Coordinated upstream limit receipt — D-P5-47:** §4.2/§5.2 consumes P1 D-P1-66
  actual target-specific positive-or-unsupported-zero maxima and mandatory serialized
  keys for identical pure/backend/recorder admission. Target equality/maximum+1 and
  unsupported-zero boundary cases accompany the mixed-format/nondefault-state cases.

No implementation, native execution, test, build, checksum, formatter, linter or other
validation command was run. Planned acceptance evidence is not claimed as passing.
Fresh affected owner/receiver review and Main's consolidated integration remain required.
