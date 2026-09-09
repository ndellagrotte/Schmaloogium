# Schmaloogium — Phase 5: Framebuffer & buffer architecture — Architecture

## 0. Header

**Phase:** 5 — Framebuffer & buffer architecture

**Milestone:** v0.1; shadow-FBO structure at v0.1, shadow-pass wiring at v0.2

**Depends on:** Phase 1, Phase 3, Phase 4

**Assigned OQs:** none

**Authored:** 2026-07-28 · **Last revised:** 2026-09-08 (§0.48)

**Deliverable:** this document, following
`docs/design/v2.0-RC3/DESIGN.md:790`–`:826` and its mandatory thirteen-section template.

**Governing design revision:** `docs/design/v2.0-RC3/DESIGN.md`. The Phase 5 assignment starts at
`docs/design/v2.0-RC3/DESIGN.md:1572`; its objective says the subsystem is
*"Contract-visible almost end to end"* at `docs/design/v2.0-RC3/DESIGN.md:1583`.

### 0.1 Inputs actually read

| Input | Material read and use |
|---|---|
| `AGENTS.md` | Complete repository and document-system instructions. |
| `docs/MOVES.md` | Path/version manifest and the five `DESIGN.md` collision warning. |
| `docs/design/v2.0-RC3/DESIGN.md` | All of Part I, §G0–§G12 (`docs/design/v2.0-RC3/DESIGN.md:92`–`:1109`), and the complete Phase 5 assignment (`docs/design/v2.0-RC3/DESIGN.md:1572`–`:1685`). Other phase specifications were not read. |
| `docs/research/v1/RESEARCH.md` | Mandatory §0–§1; assigned §3.6.3, §4.1, §4.3, and Appendix B whole. Two narrow extra reads are disclosed in §0.3: App A.3 at `docs/research/v1/RESEARCH.md:1181` and App F.7 at `docs/research/v1/RESEARCH.md:1514`–`:1521`. |
| `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` (`PD`) | Assigned §5 whole, §17 rows B4/B13, and §18 flip row; narrow extra §6.5 and §18 texture-unit reads are disclosed in §0.3. |
| `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinFramebuffer_Shaders.java` | Complete. This is the assigned 1.12.2 depth-renderbuffer replacement evidence. |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/` | Every Java file in the assigned directory, including `backed/`. Only the target, flip, clear, depth-copy, and resize behavior admitted in §3/§4 is used. The noise/custom/single-color classes belong to Phase 13 and are not adopted here. |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/ShadowRenderTargets.java` | Narrow extra ranges disclosed in §0.3; PD §5/B4 points outside the assigned `targets/` directory for the shadow structure and stub. |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java` | Two comment sites only, disclosed in §0.3, to validate PD's required fog-alpha quirk wording. |
| `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` | Color-attachment behavior, fixed texture-unit tables, depth/shadow usage, directive rows, all 37 texture formats, pixel formats, and pixel types. This is a shipped pack-author document and therefore `[V:doc]`. |
| Cleanroom MCP 1.12.2 mappings | `net.minecraft.client.shader.Framebuffer` class details and `createFramebuffer` signature. Confirmed descriptor `(II)V`, SRG `func_147605_b`, `useDepth`/`field_147619_e`, and `depthBuffer`/`field_147624_h`. |
| `docs/phase1/v14/PHASE_1_DOC.md` | Verified dependency. Read its header/current status, module/seam and package placement, `engine.gl` handles and facade, recording/replay, diagnostic/logging conventions, bring-up/depth-adjacent material, and binding §5 in full. |
| `docs/phase3/v1/PHASE_3_DOC.md` | Verified dependency. Read its public `PackConfiguration` shape, directive/resource aggregation, format/clear/flip rows, relevant detailed design, §5 in full, and relevant decisions/hand-offs. |
| `docs/phase4/v1/PHASE_4_DOC.md` | Verified dependency. Read its public stage/pass/resource/program-state shape, routing and side-ownership rules, §5 in full, and relevant decisions/hand-offs. |
| Latest dependency reviews | `docs/phase1/reviews/PHASE_1_REVIEW_20.md`, `docs/phase3/reviews/PHASE_3_REVIEW_14.md`, and `docs/phase4/reviews/PHASE_4_REVIEW_15.md`. Each ends in literal `PASS`, zero corrections, and no outstanding §5 change. Phase 2's latest review was also checked for wave state but Phase 2 is not a Phase 5 dependency. |

At original authorship the dependency gate was satisfied; §0.39 supersedes this historical state.
Phase 1's then-latest review says Phase 1 is verified and a valid
dependency input (`docs/phase1/reviews/PHASE_1_REVIEW_20.md:61`–`:75`); Phase 3's latest
review records *"literal PASS"* and no owed interface change
(`docs/phase3/reviews/PHASE_3_REVIEW_14.md:42`–`:48`); Phase 4's latest review says its fresh review
of the corrected §5 surface established convergence
(`docs/phase4/reviews/PHASE_4_REVIEW_15.md:59`–`:72`).

### 0.2 Provenance and legal posture

This document uses RESEARCH's confidence tags exactly as defined at
`docs/research/v1/RESEARCH.md:24`–`:38`. In particular:

- `[V:doc]` means shipped pack-author documentation, not merely another project document.
- `[V:observed]` restates behavior and does not copy OptiFine structure or identifiers.
- `[V:observed — Pintonium <repo-relative path>]` identifies LGPL reference evidence under
  §G11. No Pintonium source is copied.
- `[D-P5-n]` is a Phase 5 design decision, recorded in §11.1.

The OptiFine-derived `SHADER_ENGINE_IMPL.md` read disclosed below is used only to restate behavior,
as §G7 permits. Pintonium is LGPL-3.0 evidence. No file in a `chatlogs/` directory, no root-level
`*.txt`, no transformation-library boundary, and no AGPL material was read.

### 0.3 Deviations and extra reads

1. **RESEARCH App F.7 was read for the required frame-end decision.** The assignment requires a
   recorded contract check against App F.7, while its Required-input list omits App F.7. The exact
   governing sentence is
   `docs/research/v1/RESEARCH.md:1519`–`:1520`:
   *"`flip.<prog>.<buf>` ...; last writer should leave flip enabled so later passes can read."*
   That narrow read is necessary to make D-P5-4 an actual contract check rather than a citation by
   hearsay.
2. **RESEARCH App A.3 was read for `superSamplingLevel`.**
   `docs/research/v1/RESEARCH.md:1181` defines the directive only as an *"SSAA multiplier"*. The
   assigned §4.3 material defines buffer extent independently as display size × render-quality
   multiplier. §4.11 records the resulting ownership ruling and §11.3 records the remaining
   upstream ambiguity.
3. **Five imported Pintonium texture-support files were read.** `InternalTextureFormat.java`,
   `PixelFormat.java`, `PixelType.java`, `DepthBufferFormat.java`, and `DepthCopyStrategy.java` were
   opened because the assigned `targets/` files delegate their format and copy semantics to those
   types. `DepthCopyStrategy` supplies the §3.6 mechanism evidence; the format types were inspected
   to follow that delegation, confirm the Pintonium-only additions rejected by App B.4, and avoid
   importing their enums as contract.
4. **Two small ranges of
   `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md` were read** because the governing
   sources do not say whether `superSamplingLevel` changes allocation extent or how the plain-RGBA
   fallback transfers pixels. The inspected ranges were
   `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:150`–`:235` and
   `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:674`–`:691`.
   Lines 209–213 say the observed color extent is display × render quality and all color
   allocations use BGRA—or `RGBA_INTEGER` for integer formats—with
   `UNSIGNED_INT_8_8_8_8_REV`;
   `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:173` reports
   `superSamplingLevel` separately as an SSAA multiplier. The second range checked resize/uninit
   context and contributes no claim beyond the governing RESEARCH lifecycle. This is behavioral
   observation only.
5. **Only the heading index of `docs/reference/oculus/v1.0/OCULUS_DESIGN.md` was inspected.** This
   procedural discovery read checked whether RC3 had made OD a Phase 5 Required input. It had not:
   §G12.6 explicitly says its reading map does not amend current Required inputs. No OD claim,
   source path, or mechanism is used in this document.
6. **PD §17 B13, §6.5, and the §18 texture-unit row were read narrowly.** RC3's Phase 5 scope
   directly invokes B13 and calls Pintonium's dynamic allocation a pre-decided rejection even
   though its Required-input bullet names only PD §5, B4, and the §18 flip row. The precise extra
   evidence is `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:799`,
   `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:342`–`:356`, and
   `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808`.
7. **Pintonium `ShadowRenderTargets.java` was read narrowly outside the assigned `targets/`
   directory.** PD §5.3 and B4 point to that class for the shadow allocation shape and broken flip.
   The inspected ranges were
   `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/ShadowRenderTargets.java:35`
   –`:165` and
   `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/ShadowRenderTargets.java:260`
   –`:335`; only the allocation/filter shape is adopted, and the stub is rejected in §3.6/D-P5-8.
8. **Two Pintonium pipeline comment sites were read for the required fog-alpha wording.** The
   inspected ranges were
   `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java:545`
   –`:553` and
   `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java:1101`
   –`:1109`. They confirm PD §5.1's deployed-quirk report; no pipeline structure is adopted.

No build, test, verification loop, review agent, or source-code edit was performed. This is the
Phase 5 build-session architecture deliverable only.

### 0.4 Round-1 fix-up

Round 1 added the missing Phase 13 overlay supply seam, synchronous resize-notice delivery,
implementable shadow-estate operations, and explicit conformance rows for clear batching, sizing,
resize invalidation, supersampling, and Final handoff. The changed §5 contract requires a fresh
verify round before Phase 5 can close.

### 0.5 Round-2 fix-up

Round 2 completed resize-failure publication, registration, color-clear execution, composite-blend,
and anaglyph-aware Final handoff contracts. The changed §5 contract requires a fresh verify round.

### 0.6 Round-3 fix-up

Round 3 bound mandatory full-clear state to successful execution and made resize retries use each
consumer's acknowledged sizing baseline. The changed §5 contract requires a fresh verify round.

### 0.7 Round-4 fix-up

Round 4 aligned the resize-registration signature and mapped the legacy shadow-depth swizzle.

### 0.8 Round-6 fix-up

Round 6 completed the publication state/result model and the public planning/build input inventory.
The changed §5 contract requires a fresh verify round.

### 0.9 Round-7 fix-up

Round 7 closed successful planning, resize-consumer identity, and main-color allocation mapping.
The changed §5 contract requires a fresh verify round.

### 0.10 Round-8 fix-up

Round 8 defined the resize-failure delivered-count invariant and its conformance oracle.

### 0.11 Round-9 fix-up

Round 9 defined the published-estate main-depth refresh operation and resize-required carrier.

### 0.12 Round-10 fix-up

Round 10 completed successful and failed same-extent main-depth refresh transitions.

### 0.13 Round-11 fix-up

Round 11 completed format-sensitive depth-copy-target refresh and observable resize-registration
rejection. The changed §5 contract requires a fresh verify round.

### 0.14 Round-12 fix-up

Round 12 aligned depth-refresh classification and completed resize-registration and texture-overlay
handoff contracts. The changed §5 contract requires a fresh verify round.

### 0.15 Round-13 fix-up

Round 13 corrected the Phase 1 request count, completed unit 15's `NOISE` resolution, and separated
pre-publication inspection from publisher-assigned generation. The changed §5 contract requires a
fresh verify round.

### 0.16 Round-14 fix-up

Round 14 defined the published sizing, inventory, and resize-notice value contracts.

### 0.17 Round-15 fix-up

Round 15 removed the unused runtime revision from the planning input contract.

### 0.18 Round-16 fix-up

Round 16 completed the Phase 7 publication transaction and SCREEN-terminal routing contracts.
The changed §5 contract requires a fresh verify round.

### 0.19 Round-17 fix-up

Round 17 gave pass snapshots a closed engine-FBO/SCREEN target and completed the public frame
lifecycle result contracts. The changed §5 contract requires a fresh verify round.

### 0.20 Round-18 fix-up

Round 18 made pass acquisition and completion outcomes closed and observable.

### 0.21 Round-19 fix-up

Round 19 closed and tested the main-pass rejection matrix and the shadow-pass operation outcomes.
The changed §5 contract requires a fresh verify round.

### 0.22 Round-20 fix-up

Round 20 completed the shadow view, snapshot, result-carrier, and already-open rejection contracts.
The changed §5 contract requires a fresh verify round.

### 0.23 Round-21 fix-up

Round 21 distinguished unrequested shadows from failure, made the shadow family public, and
requested binding exposure of Phase 4's existing candidate view. The changed §5 contract requires
a fresh verify round.

### 0.24 Round-22 fix-up

Round 22 completed the dependent-facing component contracts embedded in shadow snapshots.
The changed §5 contract requires a fresh verify round.

### 0.25 Dependency-adoption addendum (2026-07-29)

Phase 1 accepted the three requested framebuffer/depth capabilities, added the authenticated
borrowed-handle issuance route required by review, and reconverged at literal PASS in
`docs/phase1/reviews/PHASE_1_REVIEW_20.md:61`–`:75`. Phase 4 published its existing candidate
view in binding §5, completed the retained-view lifecycle as an immutable detached metadata
snapshot, and reconverged at literal PASS in
`docs/phase4/reviews/PHASE_4_REVIEW_15.md:59`–`:72`. The already-present
`verification/targets/phase-5.json` also grants the tooling request recorded below.

Sections 4, 5, 6, 9, 10, 11, and 12 now consume those verified contracts and remove the historical
blocked-until-granted wording. The future `superSamplingLevel` authority clarification remains
pending and non-blocking under D-P5-10: main allocation extent is
`round(display × renderQuality)`, supersampling does not multiply it, and Phase 7 owns eventual
SSAA execution.

**Current §G1.3 status:** round twenty-three's literal PASS applies to the pre-§0.25 bytes. This
addendum changes binding §5, so Phase 5 is **not verified** and is not a valid dependency input
until a fresh round twenty-four returns literal PASS (or any corrections are fixed and the
changed interface is re-verified). The version directory remains `v1` while the loop is open.

### 0.26 Round-24 fix-up

Round 24 made texture-binding validation a closed result and settled overlay-lease ownership on
both success and rejection. The changed §5 contract requires a fresh verify round.

### 0.27 Round-25 fix-up

Round 25 removed the inapplicable per-unit missing payload from whole-call texture-binding
rejections. The changed §5 contract requires a fresh verify round.

### 0.28 Round-26 fix-up

Round 26 completed the successful texture-binding snapshot's fixed-unit outcome contract.
The changed §5 contract requires a fresh verify round.

### 0.29 Round-27 fix-up

Round 27 added the omitted `Extent2i` leaf value to binding §5.
The changed §5 contract requires a fresh verify round.

### 0.30 Round-28 fix-up

Round 28 defined the closed depth-copy result contract and completed the binding clear-request
shape. The changed §5 contract requires a fresh verify round.

### 0.31 Round-29 fix-up

Round 29 aligned the depth-copy rejection prose and completed the binding resize-reason order.
The changed §5 contract requires a fresh verify round.

### 0.32 Downstream-request addendum (Phase 8 shadow runtime contract — 2026-08-03)

Round thirty subsequently returned literal PASS with zero findings
(`docs/phase5/reviews/PHASE_5_REVIEW_30.md`). This maintenance amendment accepts Phase 8's complete
R8-2 request from `docs/phase8/v1/PHASE_8_DOC.md` §5.5. The shadow estate now exposes a
generation/frame/snapshot-checked four-unit binding snapshot, typed post-pass mipmap generation
with per-buffer outcomes, and an idempotent generation-checked runtime transition to coherent
neutral shadow state.

The grant preserves Phase 5 ownership: Phase 8 receives opaque borrowed handles and closed
outcomes, never GL names or mutable flip/filter state. Validation order, lifetime, per-buffer
filter restoration, open-pass abort, neutral fixed-unit backing, old-snapshot invalidation, and
later `shadow()` behavior are specified together in §§4.10, 4.12, 5, 6, 8, 9, 11, and 12.

**Current §G1.3 status:** round thirty's PASS applies to the pre-§0.32 bytes. This amendment changes
binding §5, so Phase 5 is **not verified** and is not a valid dependency input until a fresh round
thirty-one returns literal PASS (or any corrections are fixed and the changed interface is
re-verified). The version directory and manifest remain at `v1` while the loop is open.

### 0.33 Round-31 fix-up

Round 31 closed shadow min-filter restoration failure and mapped conditional sfb creation. The
changed §5 contract requires a fresh verify round.

### 0.34 Round-32 fix-up

Round 32 made sfb planning honor independent shadow-depth or shadow-color demand.

### 0.35 Round-33 fix-up

Round 33 added the omitted public `clearPlan(ClearRequest)` operation to binding §5.
The changed §5 contract requires a fresh verify round.

### 0.36 Round-34 fix-up

Round 34 corrected the header's latest-revision pointer.

### 0.37 Round-35 fix-up

Round 35 corrected the input ledger's collision count and Part I coordinate.

**Historical status:** review round 36 subsequently returned literal **PASS** with zero findings
(`docs/phase5/reviews/PHASE_5_REVIEW_36.md`). The amendment below supersedes that verified surface.

### 0.38 Downstream-request addendum (Phase 7 frame transitions and resource evidence — 2026-08-03)

This maintenance amendment accepts Phase 7 R7-1, R7-2, R7-3, and R7-5. The main-depth protocol now
follows world order (`PRE_WEATHER` before `PRE_TRANSLUCENT`); virtual pre-passes have a typed,
programless transition operation; first-person overlays use a balanced generation/frame/epoch-
checked draw-buffers-none lease; and planning/publication expose one immutable canonical Phase 2
`resources.*` projection with exact availability and shortfall semantics. `[D-P5-17]` through
`[D-P5-20]` record the decisions.

The additions preserve ownership: Phase 4 supplies typed pass descriptors, Phase 5 alone mutates
flip/draw-buffer/resource state, and Phase 7 only sequences operations and serializes owner-defined
values. This amendment changes binding §5, so round 36's PASS is historical and Phase 5 v1 is **not
verified** pending a fresh whole-document review; the version directory remains unchanged.

### 0.39 Coordinated shared-unit rebuild (2026-09-06)

The maintainer authorized an architecture-only rebuild of the Phase 4/5/7/13 documents after
Phase 13 Review 3's structural finding. Review 38 returned literal PASS for the preceding
§0.38 bytes (`docs/phase5/reviews/PHASE_5_REVIEW_38.md:61-69`); that evidence does not certify
this rebuild. All earlier addenda remain historical, including the superseded four-unit shadow
binding grant. RC3 remains the governing revision; no verification-only v3 override is adopted.

Additional reads: the whole Phase 5 document and Review 38; `AGENTS.md`; `docs/MOVES.md`;
`docs/phase13/reviews/PHASE_13_REVIEW_3.md`; the exact RC3 G1/G5.3/G9/G11.4/G12 and Phase 5
assignment; `docs/research/v1/RESEARCH.md:1228-1255,1482-1492`;
`docs/phase3/v1/PHASE_3_DOC.md:360-420,532-666,749-750,1389-1410,1422-1510`
and Review 36's applied resolutions; `docs/phase6/v1/PHASE_6_DOC.md:325-357,970-1097,1342-1443`;
and `docs/phase8/v1/PHASE_8_DOC.md:225-257,560-607,932-981,1081-1133`.
The coordinated Phase4 sampler/selector §2.2 and incorporated §5.1 declarations were also read
after its owner completed the rebuild; their new bytes remain unverified.
These scoped reads establish the lossless declaration/key boundary and the still-ungranted
Phase 6/8 consumer migrations. Phase 3 remains provisional after Review 36's §5-changing
resolutions; the newly coordinated Phase 4 surface also awaits fresh verification.

The active §§1–9/11–12 replace the narrow overlay and detached shadow models with one
Phase-5-owned fixed-name policy, lossless candidate protocol and authenticated sixteen-row
physical bind transaction. D-P5-21/22 record the shared-unit and lifetime decisions. No code,
review, test, fixture, build, manifest, MOVES or dependency document is changed by this owner.

### 0.40 Integration remediation (2026-09-07)

IR-03/04/12/29 reconcile current producer grants, source-free inspection acquisition and the SSAA
execution handoff. RC3 remains governing; RESEARCH §1.2's no-AA boundary is not amended. Active §§4/5/11
retain nontrivial supersampling as authority-open, not integer-retention-as-support.
Historical addenda/reviews remain unchanged. §5 changed; this document is unverified and requires
fresh whole-document owner verification before implementation consumption. Documentation only:
no builds, tests, formatting, implementation or new PASS claims.

### 0.41 IR-12/24 sampling-authority investigation (2026-09-07)

Research precedes the RC3 assignment. Fresh evidence in §11.6 preceded the maintainer's explicit
2026-09-07 **Pack-option compatibility only** disposition: no engine SSAA requirement, no AA
runtime/UI; preserve the selected constant and pack source uses. Historical research/design
and §0.40 remain unchanged; §11 records the approved correction and D-P5-27 applies it.
Reads covered this phase's §5 and incorporated sampling/sizing/lifecycle semantics, §11, P7 §§4.3/4.6/5.2/11.3,
RESEARCH §§0–1/4.3–4.4/App A.3, RC3 §§G11/Phase 5 and the review's Resolutions.
The historical `pintonium-9c2fcc1` directory is absent; the distinct available
`reference-src/Pintonium-main` checkout was searched and its option recognizer and root GPL-3.0
license read, not represented as the old LGPL snapshot. Published OptiFine docs and the current
LGPL-3.0 Iris option recognizer were also read; §11.6 records exact sources and evidence limits.
No prohibited source, implementation, validation, build, test or formatter was used. The
subsequent approved scope correction closes the sampling-authority question, not verification.

### 0.45 Attempt-5 synchronous value and receiving amendment — 2026-09-08

D-P5-42 closes the P1/P5/P13 target-bearing synchronous allocation/value grant; D-P5-43
receives P14 D-P14-31's complete latest object baseline on every successful setter.
D-P5-44 receives current schema23 and marks prior numeric receipts historical; D-P5-45
receives P4's exact contained virtual prelude. Selected inputs are governing RC3 buffer
scope/seam/failure rules, RESEARCH App B.4/F.5, affected owner contracts and P13 R5 C2.
No pack parser, direct GL, optional async API, native/options/assets/parameter-domain
change or new framebuffer policy is authorized. §5 changes are unverified; fresh
owner/receiver reviews and runtime proof remain required. No validation commands or tests.

### 0.46 Attempt-6 typed-clear and captured-limit correction — 2026-09-08

D-P5-46 resolves R43 Correction1 through P1 D-P1-67's actual typed native/recorder
grant; D-P5-47 receives D-P1-66's captured target maxima. Governing RC3 clear/format/
seam assignment and Khronos EXT_texture_integer/glClearBuffer establish the native
distinction, not G6 observation of our saturation policy. Historical reports/confidence
are preserved; live algorithm, §5, §8 and checklist change together. No code, execution,
validation command or revised PASS verdict accompanies this architecture correction.

### 0.47 Attempt-7 fallback and companion receiver — 2026-09-08

D-P5-49/50 preserve all three allocation-fallback triggers and separate missing integer-clear
tier rejection; receive P1 private discard restoration and P13/P7 actual-base companion refresh.
Reads: current owned clear/binding algorithms, declared §5, selected RC3 clear/format scope,
P13 producer/parameter authority and P7 ordinary/nested/shadow dispatch. Historical reports,
source-pin/licence confidence and existing gates remain; no validation command, runtime proof,
implementation, fresh PASS or optional native grant is claimed.

### 0.48 Attempt-10 citation-anchor fix-up — 2026-09-08

Review 47's corrections repoint the Phase 3 algebra, B.5-prefix canonicalization, and
complete-key-ordering pins and set this header's latest-revision pointer; the colortex1/gdepth,
explicit-flip, and Phase 6 conditional-shadow pins already carried the corrected coordinates at
current bytes and were verified rather than re-edited. No §5 contract bytes change; the outstanding
whole-document verify gate is unchanged.

## 1. Scope & boundaries

### 1.1 What Phase 5 owns

Phase 5 owns the complete buffer-estate policy:

- the pure `engine.buffers` plan for dfb and sfb resources;
- logical colortex/shadowcolor identities and their main/alt physical pairs;
- flip state, pass snapshots, virtual `*_pre` transitions, frame-end normalization, and real
  shadow-color flip state;
- color clear enablement, colors, side selection, and batching;
- all 37 contract color formats, canonical allocation transfer layouts, filtering, wrapping, and
  the all-plain-RGBA fallback;
- depthtex0's sampleable 1.12.2 bridge design, depthtex1/depthtex2 allocation and copy mechanics,
  and version-driven reattachment;
- sfb depth/color allocation, filtering, hardware-PCF compare mode, old-pack swizzle, and lifecycle;
- display/render/shadow sizing, scan-driven counts, candidate creation, resize/recreate, full-clear
  invalidation, publication generation, and teardown;
- resolution of Phase 4 symbolic draw routes into FBO attachment plans;
- the sole fixed-name policy/resolver and the physical bind operation answering **which compatible
  texture object backs each fixed App B.3 unit for the selected effective program**; and
- headless state-machine and recorded-GL test contracts for all of the above.

Policy lives in `com.schmaloogium.engine.buffers`; GL object operations use only Phase 1's
`engine.gl` facade. The 1.12.2 depth replacement and providers live in `mod.mixin`/`mod.glue`.
Mixins observe and delegate; no flip, clear, sizing, or fallback policy lives in a mixin.

### 1.2 Explicit adjacent ownership

- **Owned by Phase 1:** `GLDevice`, services, opaque handles, `GLCapabilityProfile`, recording/replay,
  diagnostics, log channels, bring-up stage 2, and the verified authenticated borrowed-depth,
  combined depth/stencil, and first-versus-steady depth-copy facade shape consumed in §5.2.
- **Owned by Phase 3:** parsing and validation of format/clear/mipmap/shadow directives,
  pack-option recognition/materialization (including `superSamplingLevel`), `ProgramStateModel`,
  `ResourceRequirements`, immutable configuration and fingerprint. Phase 5 never rescans packs
  and does not consume the option as a resource/sampling directive.
- **Owned by Phase 4:** stage/pass registry, sole fallback selection, effective program/state,
  lossless `ProgramSamplerLayout`, private selection authentication, and detached candidate view.
  Phase 5 resolves physical sides but never writes them into a Phase 4 value.
- **Owned by Phase 6:** sampler location caching and integer uploads through its unchanged three
  participants. Its adopted/unverified R7-10 consumes Phase 5's sole `FixedSamplerResolver`;
  Phase 5 binds objects but never uploads uniforms, and Phase 6 never owns a second mapping.
- **Owned by Phase 7:** when frame clears and depth copies occur, pass execution, viewport/state
  setup, final rendering to Minecraft's framebuffer with its anaglyph-aware color mask,
  frame-driver try/finally, runtime display and quality inputs, preparation of Minecraft's shader
  framebuffer at Phase 5's required main extent, and orchestration of registry/estate publication.
- **Owned by Phase 8:** shadow camera, traversal, shadow pass execution, and the moment of the
  shadowtex1 split. Phase 5 supplies sfb and its operations.
- **Owned by Phase 13:** custom/noise/companion production, source/content/parameterization
  semantics, publication and lease accounting, and destruction of its owned textures. Phase 5 owns
  shared protocol types and final physical selection; foreign live objects remain vanilla-owned.
- **Owned by Phase 14:** sampler objects, DSA modernization, asynchronous transfers, persistent
  staging, and performance tuning. Phase 5 exposes stable policy and generations for those later
  implementations.
- **Owned by G8/S1:** actually wiring colortex16/32-era growth, shadowcolor2–7, shadowcomp arrays,
  and modern per-buffer sizes. Phase 5's identities and state machines are cardinality-independent
  now.

### 1.3 Hard boundary

This phase does not decide pass timing, execute a shader, upload a uniform, author a shadow camera,
load a custom texture, or introduce a direct LWJGL call in `:engine`. `Final` has no Phase-5-owned
FBO. Its buffer plan returns a `SCREEN` terminal that obliges Phase 7 to bind Minecraft's framebuffer
through its platform path and apply Minecraft's anaglyph-aware color mask before drawing, consistent
with `docs/research/v1/RESEARCH.md:527` and
`docs/phase1/v14/PHASE_1_DOC.md:5531`, which warns that `bindDefault` means GL framebuffer name zero,
not Minecraft's world FBO.

## 2. Architecture overview

### 2.1 Invariants

1. A logical color buffer is identified by `(BufferDomain, index)`, never by an array position or GL
   name.
2. Every allocated colortex and shadowcolor has exactly two Phase-5-owned textures, `A` and `B`.
3. A pass receives one immutable side snapshot. Its FBO attachments and texture bindings derive from
   that same snapshot.
4. Phase 4 stores only logical read/write/flip intent. Phase 5 alone stores physical side state.
5. A frame starts with every relative flip bit clear. The newest content is preserved by rebasing
   which physical side is the committed logical main, not by copying alt→main.
6. Every Phase-5-owned handle is deleted exactly once; a platform-owned depth handle is never
   allocated, uploaded, or deleted by Phase 5.
7. A published estate is immutable in identity. Resize/rebuild produces a new generation or a
   shaders-off publication; stale views do no GL work.
8. GL operations are render-thread-only. Pure plans contain no handle and may be built off-thread.
9. The fixed texture-unit map is data, not allocation policy. No free-unit search exists.
10. A failed estate can always degrade to shaders-off and Minecraft's vanilla framebuffer path.

### 2.2 Public shape

Illustrative signatures define the cross-phase contract; implementations are private under
`com.schmaloogium.engine.buffers.internal`.

```java
package com.schmaloogium.engine.buffers;

public record BufferIndex(int value) {
    public BufferIndex {
        if (value < 0) throw new IllegalArgumentException("negative buffer index");
    }
}

public enum BufferDomain {
    COLORTEX, SHADOWCOLOR, SHADOWTEX, DEPTH
}

public record LogicalBuffer(BufferDomain domain, BufferIndex index) {}
public record Extent2i(int width, int height) {}
public enum PhysicalSide { A, B }

public record BufferSizing(
    Extent2i mainExtent,
    Optional<Extent2i> shadowExtent) {}

public sealed interface ResolvedBufferFormat {
    record Color(ColorInternalFormat value) implements ResolvedBufferFormat {}
    record Depth(DepthAttachmentFormat value) implements ResolvedBufferFormat {}
}

public record BufferInventoryEntry(
    LogicalBuffer buffer,
    ResolvedBufferFormat format) {}

public record BufferInventory(List<BufferInventoryEntry> entries) {
    public BufferInventory { entries = List.copyOf(entries); }
    public int count(BufferDomain domain) {
        return (int) entries.stream().filter(e -> e.buffer().domain() == domain).count();
    }
}

public enum BufferResizeReason {
    DISPLAY_EXTENT,
    RENDER_QUALITY,
    MAIN_DEPTH_EXTENT,
    SHADOW_RESOLUTION,
    SHADOW_QUALITY,
    PACK_CONFIGURATION,
    REGISTRY_PLAN,
    COLOR_INVENTORY_OR_FORMAT
}

public record BufferResizeNotice(
    BufferSizing oldSizing,
    BufferSizing newSizing,
    long newGeneration,
    BufferResizeReason reason) {}

public record BufferRuntimeInputs(
    Extent2i displayExtent,
    double renderQuality,
    double shadowQuality) {}

public record BufferPlanRequest(
    PackConfiguration configuration,
    ProgramRegistryView registry,
    RegistryFingerprint registryFingerprint,
    GLCapabilityProfile capabilities,
    BufferRuntimeInputs runtime) {}

public record BufferBuildRequest(
    PackConfiguration configuration,
    ProgramRegistryView registry,
    RegistryFingerprint registryFingerprint,
    GLCapabilityProfile capabilities,
    BufferRuntimeInputs runtime,
    MainDepthSource mainDepth,
    GLDevice device,
    DiagnosticReporter diagnostics) {}

public sealed interface BufferResourceSnapshot {
    record Available(BufferResourceProjection projection) implements BufferResourceSnapshot {}
    record Unavailable(ResourceProjectionUnavailableReason reason)
        implements BufferResourceSnapshot {}
}

public enum ResourceProjectionUnavailableReason {
    INPUT_INVALID, DERIVATION_INCOMPLETE, AWAITING_MAIN_DEPTH, SHADERS_OFF
}

public record BufferResourceProjection(
    ResourceEvidenceStage evidenceStage,
    List<ColorBufferResource> colorBuffers,
    int depthTextures,
    ShadowResourceProjection shadow,
    boolean centerDepthSmoothEnabled,
    int noiseResolution,
    List<VertexAttributeResource> vertexAttributes,
    List<InstanceResource> instances,
    CapabilityGate capabilityGate,
    List<CapabilityShortfall> capabilityShortfalls) {}

public enum ResourceEvidenceStage { PLANNED, REALIZED }
public enum ColorAllocationOrigin { REQUESTED, RGBA_FALLBACK }
public record RealizedColorAllocation(
    String format, ColorAllocationOrigin origin) {}
public sealed interface ResourceClearPolicy {
    record FogRgbAlphaOne() implements ResourceClearPolicy {}
    record Constant(double r, double g, double b, double a) implements ResourceClearPolicy {}
}
public record ColorBufferResource(
    ColorAttachmentFormat requestedFormat,
    Optional<RealizedColorAllocation> allocation,
    boolean clear, ResourceClearPolicy clearPolicy) {}

public record ShadowResourceProjection(
    int depthTextures, int colorTextures, int resolution,
    List<ShadowTextureResource> depth,
    List<ShadowTextureResource> color) {}

public record ShadowTextureResource(
    boolean hardwareFiltering, boolean mipmap, boolean nearest) {}
public record VertexAttributeResource(String program, String name) {}
public record InstanceResource(String program, int count) {}

public enum CapabilityGate { OK, SHORTFALL }
public enum CapabilityLimit {
    MAX_DRAW_BUFFERS("maxDrawBuffers"),
    MAX_COLOR_ATTACHMENTS("maxColorAttachments"),
    MAX_TEXTURE_IMAGE_UNITS("maxTextureImageUnits");
    private final String wireName;
    CapabilityLimit(String wireName) { this.wireName = wireName; }
    public String wireName() { return wireName; }
}
public record CapabilityShortfall(
    CapabilityLimit limit, int required, int available) {}

public sealed interface BufferPlanResult {
    record Valid(BufferPlan plan, BufferResourceSnapshot.Available resources)
        implements BufferPlanResult {}
    record Invalid(BufferFailure failure, BufferResourceSnapshot resources)
        implements BufferPlanResult {}
}

public sealed interface BufferBuildResult {
    record Ready(BufferEstateCandidate candidate) implements BufferBuildResult {}
    record AwaitingMainDepth(long expectedVersion) implements BufferBuildResult {}
    record ShadersOff(BufferFailure failure) implements BufferBuildResult {}
}

public interface BufferArchitecture {
    BufferPlanResult plan(BufferPlanRequest request);      // pure; no GL handles
    BufferBuildResult create(BufferBuildRequest request); // render thread
}

public final class BufferArchitectures {
    public static BufferArchitecture create();
}

public final class BufferEstateCandidate implements AutoCloseable {
    public BufferEstateInspection inspection();
    public void close(); // idempotent while caller-owned; deletes only owned handles
}

public interface BufferEstateInspection {
    RegistryFingerprint registryFingerprint();
    BufferSizing sizing();
    BufferInventory inventory();
    BufferResourceSnapshot.Available resources();
}

public interface BufferEstatePublisher {
    PublishedBufferEstate current();
    BufferPublicationResult publish(
        BufferEstateCandidate candidate,
        RegistryFingerprint acceptedRegistry);
    BufferPublicationResult publishOff(BufferFailure cause);
    BufferResizeRegistrationResult addResizeConsumer(
        String consumerId,
        BufferResizeConsumer consumer,
        BufferSizing acknowledgedSizing,
        long acknowledgedGeneration);
}

public record PublishedBufferEstate(
    long generation,
    Optional<BufferEstateView> estate,
    BufferResourceSnapshot resources) {}

public interface BufferResizeRegistration extends AutoCloseable {
    void close(); // idempotent render-thread removal
}

public sealed interface BufferResizeRegistrationResult {
    record Registered(BufferResizeRegistration registration)
        implements BufferResizeRegistrationResult {}
    record Rejected(BufferResizeRegistrationRejection reason)
        implements BufferResizeRegistrationResult {}
}

public enum BufferResizeRegistrationRejection {
    BLANK_CONSUMER_ID,
    DUPLICATE_LIVE_CONSUMER_ID,
    FUTURE_ACKNOWLEDGED_GENERATION,
    UNKNOWN_ACKNOWLEDGED_GENERATION,
    ACKNOWLEDGED_SIZING_MISMATCH
}

public interface BufferResizeConsumer {
    ResizeConsumerResult resize(BufferResizeNotice notice); // render thread; no throw
}

public enum ResizeConsumerResult { SUCCESS, FAILED }

public sealed interface BufferPublicationResult {
    record Published(PublishedBufferEstate publication) implements BufferPublicationResult {}
    record ProvenanceRejected(RegistryFingerprint candidateRegistry,
                              RegistryFingerprint acceptedRegistry)
        implements BufferPublicationResult {}
    record ConsumerFailed(long failedGeneration, PublishedBufferEstate offPublication,
                          String consumerId, int deliveredCount) implements BufferPublicationResult {}
}
```

`BufferPlanRequest` contains the complete immutable planning input. Its configuration must have a
valid Phase 3 schema/fingerprint, `registryFingerprint` must equal `registry.fingerprint()`, runtime
extents/multipliers must satisfy §4.11, and capabilities must be one immutable profile.
Planning/creation also compare `registry.samplerPolicyFingerprint()` with
`FixedSamplerPolicies.appB3().fingerprint()`; mismatch is INVALID_INPUT before any allocation.
The three `BufferRuntimeInputs` fields participate by value in planning identity and plan reuse;
there is no separate runtime revision or rebuild trigger.
`BufferPlan` is an opaque immutable value containing the resolved sizing and inventory, ordered
resource descriptors, pass/FBO keys, clear groups, fixed texture-unit rows, and teardown order.
Its value equality covers all those artifacts. `create` does not consume a prior plan: it reruns
the same deterministic planning operation from the planning fields of `BufferBuildRequest`, then
performs GL creation only for a valid result.
`BufferSizing` is structurally value-equal across its two components, `mainExtent` and `shadowExtent`; `shadowExtent` is empty
exactly when no sfb is planned. `BufferInventory.entries` is immutable, ordered by
`BufferDomain` declaration order and then `BufferIndex`, and contains exactly one row for every
logical texture in the estate; `count(domain)` is the number of rows in that domain. Thus identities,
counts, and final fallback-resolved color/depth formats are consumer-visible without exposing a GL
handle.
`BufferEstateCandidate` is an opaque, compiler-created ownership product. Its pre-publication
`inspection()` is metadata-only: it exposes sizing, inventory, and a new immutable REALIZED
`BufferResourceSnapshot.Available` built from the deterministic plan plus successful allocation or
whole-estate fallback outcome, but no generation, handles, snapshots, or mutating estate operation.
Equal inputs yield equal PLANNED evidence before allocation; successful candidate and accepted
estate retain exactly equal REALIZED evidence. Cross-stage comparison covers declarative fields
only, never the stage discriminant or allocation payload.
Acceptance transfers ownership; caller `close()` becomes harmless. The publisher then assigns the
next generation atomically and creates the sole accepted `BufferEstateView`; that generation is
immutable for the view's lifetime. Rejection leaves the candidate caller-owned. `publish` requires
the exact `RegistryFingerprint` used to build the candidate. That prevents a buffer plan produced
for one effective registry from being paired with another. A mismatch returns
`ProvenanceRejected` without ownership transfer, publication, generation change, or resize
notification; the candidate remains caller-owned. Snapshots and stale checks use only the
publisher-assigned generation of an accepted view.
Every `PublishedBufferEstate` also carries the owner-defined resource snapshot. Ready publication
and its view retain the candidate's exact `Available` value; off publication carries only
`Unavailable(SHADERS_OFF)`. This is the availability bit Phase 7 serializes—absence of an estate is
not an invitation to synthesize sizing facts or reuse a prior generation's projection.

The non-owning view:

```java
public interface BufferEstateView {
    long generation();
    RegistryFingerprint registryFingerprint();
    BufferSizing sizing();
    BufferInventory inventory();
    BufferResourceSnapshot.Available resources();

    MainDepthRefreshResult refreshMainDepth();
    FrameBeginResult beginFrame(long frameId);
    VirtualTransitionResult applyVirtualTransition(long frameId, PassDescriptor pass);
    DrawBuffersNoneOpenResult openDrawBuffersNone(long frameId);
    PassSnapshotResult snapshot(PassDescriptor pass, ProgramBindingSelection selection);
    MainMipmapResult generateMainMipmaps(PassBufferSnapshot snapshot);
    PassCompletionResult completePass(PassBufferSnapshot snapshot);
    PassDiscardResult discardPass(PassBufferSnapshot snapshot);
    ClearExecutionPlan clearPlan(ClearRequest request);
    ClearExecutionResult executeClear(ClearExecutionPlan plan);
    DepthCopyResult copyDepth(DepthCopyPoint point, long frameId);
    FrameEndResult commitFrame(long frameId);
    FrameEndResult abortFrame(long frameId, String diagnosticId);

    TextureBindingResult textureBindings(
        PassBufferSnapshot snapshot,
        TextureOverlayLease overlay,
        TextureOverlayPublicationId expectedOverlay);
    ShadowEstateResult shadow();
}

public enum ClearExecutionResult { SUCCESS, STALE_OR_PROTOCOL_REJECTED, BACKEND_FAILED }

public sealed interface MainMipmapOutcome {
    record Generated(LogicalBuffer buffer) implements MainMipmapOutcome {}
    record AlreadyFresh(LogicalBuffer buffer) implements MainMipmapOutcome {}
    record Degraded(LogicalBuffer buffer, String diagnosticId) implements MainMipmapOutcome {}
}
public sealed interface MainMipmapResult {
    record Completed(List<MainMipmapOutcome> outcomes) implements MainMipmapResult {}
    record Rejected(FrameProtocolRejection reason) implements MainMipmapResult {}
    record Failed(BufferFailure failure, String diagnosticId, boolean frameAborted)
        implements MainMipmapResult {}
}

public sealed interface MainDepthRefreshResult {
    record Unchanged(long version) implements MainDepthRefreshResult {}
    record Reattached(long version, long depthAttachmentEpoch) implements MainDepthRefreshResult {}
    record ResizeRequired(BufferFailure failure) implements MainDepthRefreshResult {}
    record Failed(BufferFailure failure) implements MainDepthRefreshResult {}
}

public enum DepthCopyPoint { PRE_WEATHER, PRE_TRANSLUCENT }

public sealed interface DepthCopyResult {
    record Copied(DepthCopyPoint point, boolean initialized) implements DepthCopyResult {}
    record DuplicateIgnored(DepthCopyPoint point, String diagnosticId) implements DepthCopyResult {}
    record Rejected(FrameProtocolRejection reason) implements DepthCopyResult {}
    record BackendDegraded(
        DepthCopyPoint point, BufferFailure failure, String diagnosticId)
        implements DepthCopyResult {}
}

public sealed interface VirtualTransitionResult {
    record Applied(long frameId, ProgramSlotId transition, List<LogicalBuffer> flipped)
        implements VirtualTransitionResult {}
    record NoChange(long frameId, ProgramSlotId transition)
        implements VirtualTransitionResult {}
    record Rejected(FrameProtocolRejection reason) implements VirtualTransitionResult {}
}

public sealed interface DrawBuffersNoneOpenResult {
    record Opened(DrawBuffersNoneLease lease) implements DrawBuffersNoneOpenResult {}
    record Rejected(FrameProtocolRejection reason) implements DrawBuffersNoneOpenResult {}
    record BackendFailed(BufferFailure failure) implements DrawBuffersNoneOpenResult {}
}

public interface DrawBuffersNoneLease {
    DrawBuffersNoneCloseResult close(); // render thread; idempotent
}

public sealed interface DrawBuffersNoneCloseResult {
    record Restored(long frameId) implements DrawBuffersNoneCloseResult {}
    record AlreadyClosed(long frameId) implements DrawBuffersNoneCloseResult {}
    record Rejected(FrameProtocolRejection reason) implements DrawBuffersNoneCloseResult {}
    record BackendFailed(BufferFailure failure, boolean fullClearRequired)
        implements DrawBuffersNoneCloseResult {}
}

public enum FrameProtocolRejection {
    STALE_GENERATION,
    STALE_DEPTH_ATTACHMENT_EPOCH,
    FRAME_ALREADY_OPEN,
    NO_OPEN_FRAME,
    WRONG_FRAME_ID,
    DEPTH_COPY_OUT_OF_ORDER,
    INVALID_VIRTUAL_TRANSITION,
    DUPLICATE_VIRTUAL_TRANSITION,
    NON_NORMALIZED_FLIP_STATE,
    OPEN_PASS_SNAPSHOT,
    INVALID_PASS_SNAPSHOT,
    OPEN_DRAW_BUFFERS_NONE_LEASE,
    INVALID_DRAW_BUFFERS_NONE_LEASE
}

public sealed interface FrameBeginResult {
    record Begun(long estateGeneration, long depthAttachmentEpoch, long frameId)
        implements FrameBeginResult {}
    record Rejected(FrameProtocolRejection reason) implements FrameBeginResult {}
    record BackendFailed(BufferFailure failure) implements FrameBeginResult {}
}

public sealed interface FrameEndResult {
    record Committed(long frameId) implements FrameEndResult {}
    record Aborted(long frameId, String diagnosticId, boolean fullClearRequired)
        implements FrameEndResult {}
    record Rejected(FrameProtocolRejection reason) implements FrameEndResult {}
    record BackendFailed(BufferFailure failure, boolean fullClearRequired)
        implements FrameEndResult {}
}

public sealed interface PassSnapshotResult {
    record Acquired(PassBufferSnapshot snapshot) implements PassSnapshotResult {}
    record Rejected(FrameProtocolRejection reason) implements PassSnapshotResult {}
    record Failed(BufferFailure failure, String diagnosticId, boolean frameAborted)
        implements PassSnapshotResult {}
}

public sealed interface PassCompletionResult {
    record Completed(long frameId) implements PassCompletionResult {}
    record Rejected(FrameProtocolRejection reason) implements PassCompletionResult {}
}

public sealed interface PassDiscardResult {
    record Discarded(long frameId) implements PassDiscardResult {}
    record Rejected(FrameProtocolRejection reason) implements PassDiscardResult {}
}

public sealed interface PassDrawTarget {
    record EngineFramebuffer(FramebufferHandle framebuffer) implements PassDrawTarget {}
    enum Screen implements PassDrawTarget { INSTANCE }
}

public record PassBufferSnapshot(
    long estateGeneration,
    long depthAttachmentEpoch,
    long frameId,
    PassDescriptor pass,
    ProgramBindingSelection selection,
    List<ColorAttachment> colorAttachments,
    Map<LogicalBuffer, TextureHandle> readableTextures,
    Set<LogicalBuffer> flipAfterPass,
    PassDrawTarget drawTarget) {}
```

Every mutating method rejects a stale generation, depth-attachment epoch, or frame token before a GL
call. `snapshot` validates a matching current view, open frame, and no open pass before preparation;
failed validation returns the applicable `STALE_GENERATION`, `STALE_DEPTH_ATTACHMENT_EPOCH`,
`NO_OPEN_FRAME`, or `OPEN_PASS_SNAPSHOT` rejection without GL or mutation. `completePass` returns
`Completed` only for the one currently open snapshot after applying its recorded transition.
Acquisition cannot return `WRONG_FRAME_ID`: it accepts no caller frame ID and stamps the installed
open-frame token into the acquired snapshot.
Generation, epoch, and frame mismatches return their corresponding rejection; a foreign,
duplicate, or already-consumed snapshot returns `INVALID_PASS_SNAPSHOT`. Every rejection performs
no GL and leaves the open token and flip state unchanged.
Only after successful write-side preparation does `snapshot` install and expose `Acquired`.
`Failed(BufferFailure failure,String diagnosticId,boolean frameAborted)` is the distinct
mutation-bearing preparation result: `frameAborted` is always true, no snapshot is exposed, and
the frame token has already been consumed. P5 invalidates all frame pass/binding snapshots,
normalizes by the §4.4.3 abort rebase without a new flip, requires full clear, and marks the estate
stale/unusable. This includes write-revision/base-filter reset, native binding-restoration failure
and caught backend exceptions during preparation; none escapes as Acquired, Rejected or a throw.
P5 attempts independent internal cleanup even when restoration fails and retains the diagnostic.
P7 marks the shared frame transaction terminal before unwinding: no mipmap, target/texture bind,
activation, draw, new flip, parent resume or remaining fullscreen work, and no later
completePass/discardPass/commitFrame/abortFrame on that consumed frame, including outer finally.
Snapshot invalidation does not close caller-owned bindings, overlay leases, local-state leases or
P4 activity: each independently owned scope still receives its result-checked finally cleanup.
The failed call creates/transfers no overlay lease; close a binding only after Bound, otherwise
close only a separately acquired overlay lease. Required shaders-off publication precedes any
later shader admission; recovery requires safe-point replacement and successful mandatory full
clear, not retry on the poisoned estate. `snapshot` returns
`PassDrawTarget.Screen.INSTANCE` exactly for `StageId.FINAL`; all other wired
raster passes return `EngineFramebuffer`. SCREEN carries no framebuffer handle. It is still an open,
generation/epoch/frame-checked snapshot: Phase 7 validates it, performs the platform framebuffer
bind and anaglyph-aware color mask, draws, and requires `Completed` exactly as for an engine target.
Completion consumes the snapshot and applies its recorded flip set; SCREEN invents no extra flip.

### 2.3 Relationship map

```text
P5 FixedSamplerPolicies.appB3 ─> P4 compile ─> detached ProgramRegistryView
configuration + view + capabilities/sizing ─> P5 pure plan / estate candidate
P7 coherent acceptance ─> PublishedBufferEstate + P13 TexturePublication
P4 select once ─> ProgramBindingSelection ─> P5 main snapshot preparation
main: Acquired ─> frozen pass; Rejected ─> pre-mutation protocol recovery
main: Failed(...,true) ─> consumed frame + full-clear/off; independent finally cleanup only
P4 selection ─> shadow pass snapshot (separate shadow result contract)
P13 lease(expected,selection,baseBinding) + acquired frozen pass ─> P5 physical texture bind
Bound + same selection/context ─> P4 activate ─> three P6 callbacks ─> draw
P5 FixedSamplerPolicies.resolver ─> requested unchanged P6 sampler participant
finally: Bound owner closes binding; every non-transferring path closes acquired lease
```

Phase 7 obtains `FixedSamplerPolicies.appB3()` before compile, builds Phase 4's candidate and
Phase 5's candidate against the same detached view/policy, composes exactly the candidate
runtime's three participants, and publishes Phase 4 then Phase 5. Admission remains closed until
Phase 13 is built against their actual accepted generations, its resize/hook adapters are installed,
and Phase 9's synchronous geometry gate succeeds. §4.11.3 binds compensation, not old-pipeline
restoration. No draw is authorized by a registry or estate acceptance alone.

### 2.4 Shared fixed-sampler and texture-binding protocol

These are Phase-5-owned publication protocols. Phase 13 implements the immutable values and lease
owner; `TextureSourceIdentity`, `TextureParameterSpec`, `TextureParameterFingerprint`, `AtlasId`
and `CompanionKind` have Phase-13-owned content semantics. `DeclaredGlslType.Sampler` and
`TextureTarget` are the exact Phase 3 types, not a reduced sampler enum.

```java
public enum FixedSamplerName {
    TEXTURE("texture"), TEX("tex"), LIGHTMAP("lightmap"), NORMALS("normals"),
    SPECULAR("specular"), SHADOWTEX0("shadowtex0"), WATERSHADOW("watershadow"),
    SHADOW("shadow"), SHADOWTEX1("shadowtex1"), DEPTHTEX0("depthtex0"),
    GDEPTHTEX("gdepthtex"), GAUX1("gaux1"), GAUX2("gaux2"), GAUX3("gaux3"),
    GAUX4("gaux4"), DEPTHTEX1("depthtex1"), DEPTHTEX2("depthtex2"),
    SHADOWCOLOR0("shadowcolor0"), SHADOWCOLOR("shadowcolor"), SHADOWCOLOR1("shadowcolor1"),
    NOISETEX("noisetex"), COLORTEX0("colortex0"), COLORTEX1("colortex1"),
    COLORTEX2("colortex2"), COLORTEX3("colortex3"), COLORTEX4("colortex4"),
    COLORTEX5("colortex5"), COLORTEX6("colortex6"), COLORTEX7("colortex7"),
    GCOLOR("gcolor"), GDEPTH("gdepth"), GNORMAL("gnormal"), COMPOSITE("composite");
    public String exactName();
}
public sealed interface FixedSamplerLookup {
    record Known(FixedSamplerName name) implements FixedSamplerLookup {}
    record Unknown(String exactName) implements FixedSamplerLookup {}
}
public sealed interface FixedSamplerResolution {
    record Resolved(FixedSamplerName name, int unit) implements FixedSamplerResolution {}
    record UnsupportedDomain(FixedSamplerName name, StageId stage, StageBand band)
        implements FixedSamplerResolution {}
}
public final class FixedSamplerPolicies {
    public static FixedSamplerLayoutPolicy appB3();
    public static FixedSamplerResolver resolver();
    public static FixedSamplerLookup lookup(String exactName);
    public static FixedSamplerResolution resolve(
        FixedSamplerName name, ProgramSamplerLayout layout, StageId stage, StageBand band);
}
public interface FixedSamplerResolver {
    FixedSamplerPlanResult resolve(ProgramSamplerLayout layout, StageId stage, StageBand band);
}
public sealed interface FixedSamplerPlanResult {
    record Ready(List<ResolvedSamplerBinding> bindings, FixedSamplerPolicyFingerprint policy)
        implements FixedSamplerPlanResult {}
    record Invalid(SamplerLayoutValidation reason) implements FixedSamplerPlanResult {}
}
public record ResolvedSamplerBinding(
    String exactName, DeclaredGlslType.Sampler shape, int unit) {}
public sealed interface CandidateOrigin {
    record Custom(TextureBindingKey key, int phase3Ordinal) implements CandidateOrigin {}
    record Companion(AtlasId atlas, CompanionKind kind) implements CandidateOrigin {}
    record DefaultFill(CompanionKind kind) implements CandidateOrigin {}
    record Noise() implements CandidateOrigin {}
}
public sealed interface TextureHandleRef {
    record Owned(TextureHandle handle) implements TextureHandleRef {}
    record Borrowed(TextureHandle handle) implements TextureHandleRef {}
}
public record TextureBindingCandidate(
    CandidateOrigin origin, StageId expandedStage, String exactSamplerName,
    FixedSamplerName name, DeclaredGlslType.Sampler shape, TextureTarget target,
    TextureHandleRef handle, TextureSourceIdentity source, TextureParameterSpec parameters,
    TextureParameterFingerprint parameterizationFingerprint, int candidateOrdinal) {}
public interface TextureCandidateTable {
    TextureCandidateEntry entry(StageId expandedStage, FixedSamplerName name);
}
public sealed interface TextureCandidateEntry {
    record Candidates(List<TextureBindingCandidate> candidates) implements TextureCandidateEntry {}
    record Absent(TextureOverlayAbsence reason) implements TextureCandidateEntry {}
}
public enum TextureOverlayAbsence {
    NOT_CONFIGURED, NOT_APPLICABLE_TO_STAGE, PUBLICATION_UNAVAILABLE
}
public record TextureOverlayFingerprint(String value) {}
public record TextureOverlayPublicationId(long generation, TextureOverlayFingerprint contentFingerprint) {}
public interface TextureOverlaySnapshot {
    TextureOverlayPublicationId id();
    RegistryFingerprint registryFingerprint();
    long registryGeneration();
    long resourceReloadEpoch();
    ConfigurationFingerprint configurationFingerprint();
    FixedSamplerPolicyFingerprint policyFingerprint();
    TextureCandidateTable candidates();
}
public interface TextureOverlayLease extends TextureOverlaySnapshot, AutoCloseable {
    BaseAtlasContext baseAtlasContext();
    Optional<TextureHandleRef> baseTexture();
    BaseAtlasContext atlasContext(TextureHandleRef base);
    boolean isCurrent();
    void close();
}
public sealed interface BaseAtlasContext {
    record Atlas(AtlasId atlas) implements BaseAtlasContext {}
    record NonAtlas() implements BaseAtlasContext {}
    record Unavailable() implements BaseAtlasContext {}
}
public enum BindingPurpose { SHADER, FIXED_FUNCTION_PASSTHROUGH, NONE }
public enum BindingOriginKind { CUSTOM, COMPANION, NOISE, ESTATE, FOREIGN, NEUTRAL }
public record BindingOrigin(
    BindingOriginKind kind, List<TextureBindingDiagnostic> diagnostics) {}
public enum TextureBindingDiagnosticCode {
    NO_CANDIDATE, INCOMPATIBLE_CANDIDATE, CONFLICTING_SAMPLER_TYPES, CONFLICTING_CANDIDATES,
    UNSUPPORTED_SAMPLER_NAME, UNSUPPORTED_STAGE_DOMAIN, UNSUPPORTED_SAMPLER_SHAPE,
    PUBLICATION_UNAVAILABLE, NOT_CONFIGURED, NOT_APPLICABLE_TO_STAGE, MISSING_BACKING
}
public record TextureBindingDiagnostic(
    TextureBindingDiagnosticCode code, String exactName, OptionalInt unit) {}
public record MissingTextureBinding(
    TextureBindingDiagnosticCode reason, String exactName, int unit) {}
public sealed interface TextureBindingOutcome {
    record BoundObject(TextureHandleRef handle, DeclaredGlslType.Sampler shape,
        List<ResolvedSamplerBinding> names, BindingOrigin origin) implements TextureBindingOutcome {}
    record Unused() implements TextureBindingOutcome {}
}
public record TextureBindingRow(int unit, TextureBindingOutcome outcome) {}
public interface TextureBindingSnapshot extends AutoCloseable {
    long estateGeneration();
    long depthAttachmentEpoch();
    long frameId();
    PassDescriptor pass();
    ProgramBindingSelection selection();
    TextureOverlayPublicationId overlayPublication();
    BindingPurpose purpose();
    List<TextureBindingRow> rows();
    TextureBindingOutcome outcome(int unit);
    List<TextureBindingDiagnostic> diagnostics();
    boolean isCurrent();
    void close();
}
public enum TextureBindingAction { SUPPRESS_DRAW }
public record TextureBindingDegradation(
    ProgramBindingSelection selection, List<TextureBindingDiagnostic> diagnostics,
    TextureBindingAction action) {}
public enum TextureBindingRejection {
    INVALID_INPUT, WRONG_THREAD, STALE_ESTATE_GENERATION, STALE_DEPTH_ATTACHMENT_EPOCH,
    NO_OPEN_FRAME, WRONG_FRAME_ID, INVALID_PASS_SNAPSHOT, INVALID_PROGRAM_SELECTION,
    PROGRAM_SELECTION_MISMATCH, STALE_REGISTRY_GENERATION, SAMPLER_LAYOUT_MISMATCH,
    CLOSED_OVERLAY_LEASE, OVERLAY_PUBLICATION_ID_MISMATCH, REGISTRY_FINGERPRINT_MISMATCH,
    CONFIGURATION_FINGERPRINT_MISMATCH
}
public sealed interface TextureBindingResult {
    record Bound(TextureBindingSnapshot snapshot) implements TextureBindingResult {}
    record Degraded(TextureBindingDegradation degradation) implements TextureBindingResult {}
    record Rejected(TextureBindingRejection reason) implements TextureBindingResult {}
    record BackendFailed(BufferFailure failure) implements TextureBindingResult {}
}
```

All lists/maps are immutable and canonical. Candidate ordinals are nonnegative and preserve Phase
3's canonical list position on every expanded copy, not original property occurrence order.
`Owned` means Phase 13 owns it, never the consumer. Estate/neutral and foreign objects appear as
`Borrowed` in binding outcomes, with the producer identified by BindingOrigin. Phase 1 authenticates
every opaque handle and its permission. No plan or digest contains handles.
`CandidateOrigin.DefaultFill` maps to existing `BindingOriginKind.NEUTRAL`, retaining the
kind-specific P13 source/parameter identity; it is not an estate-owned object.
Candidate construction rejects null fields, unequal exactSamplerName/name spelling, mismatched
expanded-stage cell, source/handle permission, target/full shape or effective parameter metadata;
Custom origin key/sampler/discriminator and phase3Ordinal remain exact. Candidates are ordered by
candidateOrdinal; companion/noise entries follow their canonical producer order, never a custom key.
Rows and diagnostics are precomputed immutable values. `outcome(int unit)` is total for0–15 and
rejects out-of-range input; diagnostics order by unit (absent first), exact unsigned-UTF8 name,
then diagnostic-code declaration order. No absent name is represented by null or an enum sentinel.

`TextureOverlayPublicationId.generation` is the accepted **estateGeneration**, not registry
generation. The Phase-13-defined texture digest uses SHA-256 and Phase 3 §4.10 framing with a
distinct texture domain/schema tag; source/content/configuration/effective parameterization,
ordered candidates, expansions, registry/policy identity and resource epoch all participate.
The parameterization digest has its own domain/schema tag. Equal content hashes confer no
authority across generations, incarnations or retired owners. Lease `isCurrent()` means open
AND its owner is the currently READY publication; retiring/closed owners return false even when
a new publication happens to have equal content. Idempotent close releases only that lease.

Phase 4 supplies `ProgramBindingSelections.validateSelection(selection,context)` returning
`ProgramSelectionValidation.Valid` or `Rejected(ProgramSelectionRejection)` with exact reasons
`INVALID_ISSUER`, `STALE_GENERATION`, `STALE_CONTEXT`, `WRONG_STAGE_BAND`,
`PROVIDER_LAYOUT_MISMATCH`. It authenticates private publication/context state with zero GL.
The selector accessors are `registryGeneration()`, `registryFingerprint()`, `requested()`,
`effectiveDescriptor()`, `effectiveStage()`, `actualBand()`, `originatingContext()`. It has no
public constructor and carries the already resolved private binding. Phase 5 never substitutes
public-record equality for this credential operation. Activation takes the same selector and
context; it never resolves fallback again.
The published Phase4 barrier exposes `select(ProgramSlotId requested,BarrierContext context)`
→ `ProgramSelectionResult.Selected(ProgramBindingSelection selection)`,
`Skipped(ProgramSlotId requested)`, `StalePublication(long expectedGeneration,long currentGeneration)`
or `ShadersOff(String diagnosticId)`. It validates issued context and applies force-shadow before
its sole fallback walk. Fixed-function selections carry FixedFunctionEmpty; virtual steps bypass
select. `activate(UseProgramRequest(ProgramBindingSelection selection,BarrierContext context))`
uses the retained private binding and preserves existing release, lock restoration, use, exactly
three callbacks, effective lock and failure-safe cleanup. Selection expires on originating context/
frame invalidation or registry replacement/off/close, **not** its immediately following activation;
generation authority is separate from layout content identity. ProgramUniformCacheKey is unchanged.

## 3. Contract conformance map

### 3.1 Main color buffers and flip behavior

| Contract item | Design element | Provenance |
|---|---|---|
| colortex0 / gcolor, fog clear | §4.6 default table; fog RGB with alpha forced to 1.0 | `[V:doc]` `docs/research/v1/RESEARCH.md:1201`–`:1204`; deployed alpha quirk `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/ClearPassCreator.java]` at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/ClearPassCreator.java:35`–`:42`; D-P5-2 |
| colortex1 / gdepth, white clear, conditional RGBA32F | §4.2 format resolution and §4.6 clear table | `[V:doc]` `docs/research/v1/RESEARCH.md:1205`; Phase 3 already publishes the conditional upgrade at `docs/phase3/v1/PHASE_3_DOC.md:1808` |
| colortex2–7 transparent black | §4.6 default table | `[V:doc]` `docs/research/v1/RESEARCH.md:1206`–`:1208` |
| at least 4, up to 8 in G6 | §4.1 capability/count gate and contiguous scan-driven inventory | `[V:doc]` `docs/research/v1/RESEARCH.md:1210` |
| main/alt pair for every color, allocated up front with contract parameters | `ColorPair(A,B)` allocation in §4.3; §4.2 applies `CLAMP_TO_EDGE`, NEAREST for integer formats, and LINEAR otherwise to both sides | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1595`–`:1597`; `[V:doc]` `docs/research/v1/RESEARCH.md:1211`; `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/RenderTarget.java]` at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/RenderTarget.java:36`–`:46`; D-P5-1 |
| gbuffers reads/writes main | `PassMode.GBUFFERS_MAIN` in §4.5 | `[V:doc]` `docs/research/v1/RESEARCH.md:1210`–`:1212` |
| deferred/composite read main, write alt, then flip written buffers | pass snapshot + `completePass` transition in §4.4–§4.5 | `[V:doc]` `docs/research/v1/RESEARCH.md:1210`–`:1212`; D-P5-1 |
| explicit `flip.*`; virtual `*_pre` | transition rows in §4.4 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:33`–`:39`; Phase 3 publication at `docs/phase3/v1/PHASE_3_DOC.md:1783` |
| last writer leaves flip enabled for later reads | relative flip remains set through final; frame end rebases the physical committed side and clears only the relative bit | `[V:doc]` `docs/research/v1/RESEARCH.md:1519`–`:1520`; D-P5-4 |
| composite blend/read-write rules | Phase 7/4 disable blending while a composite writes color attachments; independently, the buffer snapshot reports a read/write intersection diagnostic | `[V:doc]` `docs/research/v1/RESEARCH.md:1213`–`:1215`; explicit Phase 7 boundary |
| per-buffer clear enable/color overrides and flip-aware clears | §4.6 resolves Phase 3 overrides, preserves mandatory full clears, and selects one/both physical sides from flip state | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1606`–`:1613`; Phase 3 clear policy |
| clear batching by implementation limit | §4.6/D-P5-46 groups equal extent/typed value/side and chunks at min(maxDrawBuffers,maxColorAttachments), then dispatches each occupied route position with matching numeric class | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1611`–`:1613`; native integer safety refinement |

### 3.2 Depth and shadow buffers

| Contract item | Design element | Provenance |
|---|---|---|
| depthtex0 = everything, real depth attachment | `MainDepthSource` bridge, safe-point extent preparation, and borrowed attachment in §4.8 | `[V:doc]` `docs/research/v1/RESEARCH.md:1218`–`:1221`; D-P5-5, D-P5-15 |
| depthtex1 = pre-translucent copy | owned copy texture + `PRE_TRANSLUCENT` operation; Phase 7 calls it | `[V:doc]` `docs/research/v1/RESEARCH.md:1222`; §4.9 |
| depthtex2 = pre-weather/no-hand copy | owned copy texture + `PRE_WEATHER` operation; Phase 7 calls it | `[V:doc]` `docs/research/v1/RESEARCH.md:1223`; §4.9 |
| shadowtex0 everything | sfb real depth attachment | `[V:doc]` `docs/research/v1/RESEARCH.md:1224`; §4.10 |
| shadowtex1 pre-shadow-translucent copy | owned shadow split target; Phase 8 calls the copy | `[V:doc]` `docs/research/v1/RESEARCH.md:1225`; §4.10 |
| sfb created when Phase 3 reports shadow depth or shadow color use | §4.10 plans the sfb when either `ResourceRequirements.minima().shadowDepthBuffers()` or `.shadowColorBuffers()` is positive | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1630`–`:1633`; Phase 3 algebra `docs/phase3/v1/PHASE_3_DOC.md:2761`–`:2774` and binding input `docs/phase3/v1/PHASE_3_DOC.md:3417` |
| shadowcolor0/1, future 2–7 | paired `ShadowColorPair` indexed without a hard cap; v0.1 allocation gate ≤2 | `[V:doc]` `docs/research/v1/RESEARCH.md:1226`; growth source `docs/research/v1/RESEARCH.md:386` |
| optional hardware PCF and filter/mipmap | `ShadowTexturePolicy` in §4.10 | `[V:doc]` `docs/research/v1/RESEARCH.md:524`–`:526` |
| legacy shadow-depth `R,R,R,1` sampling swizzle | `ShadowTexturePolicy` in §4.10 applies it to depth textures | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1630`–`:1637`; Pintonium mechanism evidence `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:273`–`:276` |
| real shadow flip semantics | same generic FlipState over shadowcolor; no stub | Governing real-state requirement retained; historical B4 negative attribution qualified by pinned method bodies, §11/D-P5-35 |
| main and shadow sizing | checked display × render-quality and shadow-resolution × shadow-quality formulas in §4.11.1 | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1638`–`:1644`; RESEARCH §4.3 |
| `superSamplingLevel` | pack-option compatibility only; preserve source use, no engine sizing/sampling field or effect | Maintainer 2026-09-07 disposition §11.6; D-P5-27 corrects the historical App A.3/RC3 engine claim |
| resize/recreate triggers and owned invalidation | §4.11.2 trigger matrix and complete owned-object/full-clear/notice checklist | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1638`–`:1646`; PD §5.3; D-P5-14 |
| Final renders to Minecraft framebuffer | §4.5 returns `SCREEN`; §1.3 requires Phase 7's platform bind and anaglyph-aware color mask before drawing | `[V:doc]` `docs/research/v1/RESEARCH.md:527`; RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1647`–`:1649` |

### 3.3 Fixed texture-unit map

Section 4.12 reproduces every App B.3 row. The controlling table is
`docs/research/v1/RESEARCH.md:1227`–`:1248`. The shipped-doc contradiction is not hidden:
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:283` says `depthtex1` is unit 12 in one ID
table, while its uniform table says 11 at
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:203` and its composite table says 11 at
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:321`. RESEARCH resolves it at
`docs/research/v1/RESEARCH.md:1251`–`:1255`: **unit 11 is authoritative**. D-P5-9 rejects
Pintonium's dynamic allocation.

The coordinated AppF.5 coverage is bound to §4.12 and the concrete input→outcome hooks in §8.5:
program-specific targets, prebind alias conflicts, effective fallback, exact expansion/fullscreen
override, fixed range/watershadow, ordinal/exact-name precedence, full sampled shape, stale-before-
mutation, absence versus incompatibility, lease ownership, content/reload identity and coherent
shadow/publication gates. `[D-P5-21/22]`; the controlling shared-unit and expansion text is
`docs/research/v1/RESEARCH.md:1484-1490`. §§9/12 stage those named obligations, not claimed test runs.

### 3.4 Texture formats, pixel formats, and pixel types

| Contract family | Exact handling | Provenance |
|---|---|---|
| 8-bit norm (4) | `R8 RG8 RGB8 RGBA8`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1261` |
| 8-bit signed norm (4) | `R8_SNORM RG8_SNORM RGB8_SNORM RGBA8_SNORM`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1261` |
| 16-bit norm (4) | `R16 RG16 RGB16 RGBA16`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1262` |
| 16-bit signed norm (4) | `R16_SNORM RG16_SNORM RGB16_SNORM RGBA16_SNORM`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1262` |
| 16-bit float (4) | `R16F RG16F RGB16F RGBA16F`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1263` |
| 32-bit float (4) | `R32F RG32F RGB32F RGBA32F`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1263` |
| 32-bit signed int (4) | `R32I RG32I RGB32I RGBA32I`; integer transfer path, NEAREST | `[V:doc]` `docs/research/v1/RESEARCH.md:1264` |
| 32-bit unsigned int (4) | `R32UI RG32UI RGB32UI RGBA32UI`; integer transfer path, NEAREST | `[V:doc]` `docs/research/v1/RESEARCH.md:1264` |
| mixed (5) | `R3_G3_B2 RGB5_A1 RGB10_A2 R11F_G11F_B10F RGB9_E5` | `[V:doc]` `docs/research/v1/RESEARCH.md:1265` |
| null-allocation transfer layout | non-integer → `BGRA`; integer → `RGBA_INTEGER`; both use `UNSIGNED_INT_8_8_8_8_REV` with null data | `[V:observed]` `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:209`–`:213`; D-P5-13 |
| pixel-format vocabulary | six normalized channel orders and their six `_INTEGER` variants; integer internal formats require integer layout | `[V:doc]` `docs/research/v1/RESEARCH.md:1267`–`:1268` |
| pixel-type vocabulary | complete shipped list retained in `PixelType`; canonical allocation subset does not narrow raw-texture types | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:512`–`:533` |
| incomplete framebuffer | delete the entire candidate and retry all color pairs with `RGBA_COMPAT`; warn; second failure → shaders off | `[V:observed]` `docs/research/v1/RESEARCH.md:517`–`:520`; D-P5-3 |

The eight four-member families plus five mixed formats total exactly 37. Pintonium's additional
8/16-bit integer internal formats are not admitted; they are absent from Appendix B.4.

### 3.5 Draw-buffer prefixes and growth

Phase 3 already canonicalizes every B.5 prefix into an index
(`docs/phase3/v1/PHASE_3_DOC.md:2719`–`:2724`), and Phase 4 preserves ordered routing
(`docs/phase4/v1/PHASE_4_DOC.md:2158`). Phase 5 consumes only `BufferRef`; it never parses
`gcolor` or `DRAWBUFFERS`. The growth model accepts non-negative indices and sparse route sets, but
v0.1's G6 realization rejects a required colortex index above 7 with a named unsupported-post-v0.5
failure rather than silently dropping it. `RENDERTARGETS`, 16/32 colortex, custom images, SSBOs, and
shadowcolor2–7 retain their future identities from
`docs/research/v1/RESEARCH.md:371`–`:386`.

### 3.6 Pintonium mechanism disposition

| Evidence | Disposition | Contract check / decision |
|---|---|---|
| main/alt allocation and flip snapshot | adopt the pair/snapshot shape, not its class structure | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/BufferFlipper.java]` at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/BufferFlipper.java:15`–`:34`; PD §5.1 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:218`–`:228`; App B.1 agrees; D-P5-1 |
| both clear sides and fog alpha 1.0 | adopt, with Phase-5 explicit normal/full-clear rules | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/ClearPassCreator.java]` at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/ClearPassCreator.java:21`–`:78`; PD §5.1 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:237`–`:243`; §4.3/App B.1 agree; D-P5-2 |
| alt→main frame-end `SwapPass` | reject as contract behavior; use metadata rebase | PD §5.1 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:229`–`:236` and PD §18 flip row at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:809`; App F.7 controls; D-P5-4 |
| depth-renderbuffer replacement and version reattachment | adopt the mechanism behind a seam-safe provider | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinFramebuffer_Shaders.java]` at `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinFramebuffer_Shaders.java:59`–`:100`; PD §5.2 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:245`–`:254`; App B.2 requires sampleable depthtex0; D-P5-5 |
| function-pointer → blit → copy-sub-image tiers | adopt as the backend strategy | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/texture/DepthCopyStrategy.java]` at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/texture/DepthCopyStrategy.java:16`–`:30`; PD §5.2 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:256`–`:259`; App B.2 does not constrain mechanism; D-P5-6 |
| all 16 colortex allocated | reject | PD §5.3 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:271`–`:272` and PD §17 B13 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:799`; G6 App B.1 + scan-driven RC3 assignment; D-P5-7 |
| resize/version invalidation checklist | adopt for Phase-5-owned objects; notify rather than resize Phase 13/14 objects | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/RenderTargets.java]` at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/RenderTargets.java:148`–`:205`; PD §5.3 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:263`–`:266`; RESEARCH §4.1/§4.3 agree; D-P5-14 |
| shadow target structure | adopt allocation/filter shape only | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/ShadowRenderTargets.java]` at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/ShadowRenderTargets.java:43`–`:72`; PD §5.3 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:273`–`:277`; App B.2/§4.3; D-P5-8 |
| historical B4 stub attribution | retain real local flip requirement, qualify attribution | Actual pinned 9c2fcc1 flip toggles state and getColorTextureId selects alt/main; stale TODO is not method behavior. See §11 receipt; no whole-pipeline correctness inferred |
| dynamic sampler units | reject | PD §6.5 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:342`–`:356` and PD §18 texture-unit row at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808`; fixed App B.3; D-P5-9 |

## 4. Detailed design

### 4.1 Pure plan derivation and capability gate

`BufferPlanner` consumes only immutable values. Its result contains no `GLHandle` and is stable for
equal inputs.

Planning steps:

1. verify Phase 3 schema/fingerprint, Phase 4 registry fingerprint and its fixed-policy fingerprint;
2. derive the contiguous v0.1 colortex inventory:
   `0 .. max(3, highestRequiredColorIndex)`, rejecting a required index >7 at v0.1;
3. derive depth count 1–3 and shadow counts 0–2 from `ResourceRequirements`;
4. resolve every Phase 4 `AllUsedBuffers(COLORTEX)` to the sorted inventory and retain explicit route
   order exactly;
5. reject duplicate, negative, out-of-inventory, or route-length-over-`maxDrawBuffers` values;
6. require `maxColorAttachments` sufficient for the selected packed attachment count, not for the
   highest logical colortex number;
7. require `maxTextureImageUnits >= 16`; the fixed contract addresses unit 15 even if one pack does
   not happen to declare every sampler;
8. derive extents per §4.11 with checked arithmetic and `maxTextureSize` gates;
9. resolve every format/parameter/clear rule into closed engine enums; and
10. produce deterministic FBO keys, clear groups, texture-unit rows, and teardown order.

A successful call returns `BufferPlanResult.Valid(BufferPlan, Available)` with the immutable
artifacts from step 10 and their canonical resource projection. A validation failure returns
`BufferPlanResult.Invalid(BufferFailure, BufferResourceSnapshot)` without a GL call.
Capability failure is a pack-level `ERROR/CHAT` on `schmaloogium.buffers` and leaves shaders off.
Candidate creation independently repeats these steps from `BufferBuildRequest`; equal planning
fields must yield a value-equal plan before any GL allocation.

#### 4.1.1 Canonical resource-evidence projection

`BufferResourceSnapshot` is the only P5→P2/7 evidence surface. Available means complete
evidence for its explicit ResourceEvidenceStage, not successful publication or capability OK.
PLANNED is the pure plan and carries requested/declarative values only; REALIZED is a successfully
allocated candidate/estate and includes driver-resolved allocation facts. Unavailable serializes
only resources.available=false; reasons remain engine-only. No plan claims realized facts.

Color rows are dense ascending colortex order. `requestedFormat` preserves P3 DefaultRgba
versus Explicit(RGBA8), including P3's conditional gdepth resolution. PLANNED rows have empty
allocation; REALIZED rows require RealizedColorAllocation for every row: actual canonical
internal-format name (`RGBA` for plain RGBA) and REQUESTED or RGBA_FALLBACK origin.
The latter is emitted for every color row after whole-estate fallback, even if its request was
already plain RGBA. Both sides agree. These are allocation choices confirmed by successful
allocation/completeness checks, not claimed queried channel precision.
`clearPolicy` is FogRgbAlphaOne for default colortex0, Constant(1,1,1,1) for default colortex1,
Constant(0,0,0,0) for other defaults, or the explicit four finite P3 components. It remains
present when clear=false. Fog is resolved only from that frame's ClearRequest during execution;
resource evidence never invents a fog sample or implies the same colors across frames.

The /4 receiving grammar must use resources.evidence_stage=PLANNED|REALIZED; each color row
has requested_format=`DEFAULT_RGBA` or the explicit pack-format name, clear boolean,
clear_policy=`FOG_RGB_ALPHA_ONE`|`CONSTANT`. CONSTANT alone has clear_color_r/g/b/a; fog
has none. REALIZED alone has allocated_format and allocation_origin=`REQUESTED`|`RGBA_FALLBACK`;
PLANNED has neither. Fields are required/forbidden by variant, not null/empty sentinels.
P2 owns scalar encoding/index syntax; P7 copies these owner values without reconstruction.

Depth/shadow counts, resolution and dense per-texture policies remain declared pack-facing
requirements, not claims of successful shadow availability; centerDepthSmoothEnabled/noiseResolution,
sorted unique attributes/instances and exact capability shortfalls retain their existing meanings.
Gate OK iff shortfalls empty; shortfalls use unique ascending CapabilityLimit order and its three
wire names, with nonnegative required/available counts. Lists are copied/validated, constant
colors finite, no handles, side IDs, source, generation or diagnostic inference enters evidence.
Equal planning inputs give equal PLANNED evidence. Candidate creation derives that same plan,
then produces REALIZED evidence only on success; candidate inspection and accepted ready estate
carry the identical REALIZED value. Off is Unavailable(SHADERS_OFF). Equality across stages is
limited to declared fields; runtime fallback is deliberately not plan equality. P2 inspection
uses PLANNED; runtime capture uses the paired accepted estate's REALIZED snapshot.

The packed-attachment rule maps fragment-output ordinal to the route's logical buffer while attaching
that buffer to an available FBO color slot. It avoids encoding logical colortex index as a physical
attachment limit and preserves the 16/32 growth shape. The `PassBufferSnapshot` retains both:

```java
public record ColorAttachment(
    int outputOrdinal,
    int framebufferAttachment,
    LogicalBuffer logicalBuffer,
    TextureHandle physicalTexture) {}
```

At G6 the common identity route still yields ordinal = attachment = logical index. Future
`RENDERTARGETS: 3,4,7` can map ordinals 0,1,2 to three physical attachment slots without requiring
eight simultaneous attachments.

### 4.2 Format table and allocation layout

`ColorInternalFormat` has 38 values: the 37 pack-facing names plus private `RGBA_COMPAT`,
the plain unsized RGBA allocation representation for both DefaultRgba and whole-estate fallback.
DefaultRgba first attempts RGBA_COMPAT with origin REQUESTED; Explicit(RGBA8) attempts RGBA8,
never silently substituted. P3's conditional gdepth upgrade arrives as Explicit(RGBA32F) and
is allocated independently. RGBA_COMPAT in TextureSpec maps in the backend to unsized GL_RGBA and
wire `RGBA`; it is not a 38th accepted pack directive. Fallback origin stays separately explicit.

| Family | Null-allocation pixel format | Null-allocation pixel type | Integer? | Filter |
|---|---|---|---:|---|
| `R8/RG8/RGB8/RGBA8` | `BGRA` | `UNSIGNED_INT_8_8_8_8_REV` | no | LINEAR |
| `R8_SNORM/RG8_SNORM/RGB8_SNORM/RGBA8_SNORM` | `BGRA` | `UNSIGNED_INT_8_8_8_8_REV` | no | LINEAR |
| `R16/RG16/RGB16/RGBA16` | `BGRA` | `UNSIGNED_INT_8_8_8_8_REV` | no | LINEAR |
| `R16_SNORM/RG16_SNORM/RGB16_SNORM/RGBA16_SNORM` | `BGRA` | `UNSIGNED_INT_8_8_8_8_REV` | no | LINEAR |
| `R16F/RG16F/RGB16F/RGBA16F` | `BGRA` | `UNSIGNED_INT_8_8_8_8_REV` | no | LINEAR |
| `R32F/RG32F/RGB32F/RGBA32F` | `BGRA` | `UNSIGNED_INT_8_8_8_8_REV` | no | LINEAR |
| `R32I/RG32I/RGB32I/RGBA32I` | `RGBA_INTEGER` | `UNSIGNED_INT_8_8_8_8_REV` | yes | NEAREST |
| `R32UI/RG32UI/RGB32UI/RGBA32UI` | `RGBA_INTEGER` | `UNSIGNED_INT_8_8_8_8_REV` | yes | NEAREST |
| `R3_G3_B2` | `BGRA` | `UNSIGNED_INT_8_8_8_8_REV` | no | LINEAR |
| `RGB5_A1` | `BGRA` | `UNSIGNED_INT_8_8_8_8_REV` | no | LINEAR |
| `RGB10_A2` | `BGRA` | `UNSIGNED_INT_8_8_8_8_REV` | no | LINEAR |
| `R11F_G11F_B10F`, `RGB9_E5` | `BGRA` | `UNSIGNED_INT_8_8_8_8_REV` | no | LINEAR |
| private `RGBA_COMPAT` | `BGRA` | `UNSIGNED_INT_8_8_8_8_REV` | no | LINEAR |

The transfer format/type is used with a null data pointer to allocate storage; it does not describe
the internal channel width. D-P5-13 deliberately reproduces the observed G6 allocation path at
`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:209`–`:213`, including the required
integer pixel-format branch.

#### 4.2.1 Main logical-buffer mipmap transaction — D-P5-31

`BufferEstateView.generateMainMipmaps(PassBufferSnapshot snapshot)` is render-thread-only.
The opaque-owner-authenticated open snapshot already stamps generation, depth epoch, frame,
pass and exact P4 selection. Validate them in that order, require an undrawn fullscreen
DEFERRED/COMPOSITE/FINAL shader pass and the effective provider's exact planned
`mipmappedBeforeRead` membership. Derive the immutable canonical COLORTEX-only request
from that descriptor, intersecting nothing with custom bindings; invalid/missing domain or
inventory entries reject INVALID_PASS_SNAPSHOT before mutation. Caller supplies no handles
or substitute set. The frozen readable physical sides, never current live flip lookup, are used.
Call once after snapshot acquisition and before textureBindings/activation; empty membership
returns Completed(empty). Repetition on the same undrawn snapshot returns cached result
without native work. Completion/discard/abort/depth refresh/replacement invalidates that cache.

Each physical color side tracks level-zero write revision, generated revision and effective
minification filter. Generation success plus successful complete TextureParameters application
sets generatedRevision=writeRevision and enables LINEAR_MIPMAP_LINEAR for noninteger or
NEAREST_MIPMAP_NEAREST for integer formats. An unchanged fresh side returns AlreadyFresh.
Otherwise check P1 supportsMipmapGeneration, generate the complete chain and drain errors.
Unsupported capability or generation/filter-enable failure restores the configured base
LINEAR/NEAREST filter, marks chain not fresh and returns Degraded(buffer,diagnosticId);
continue remaining canonical requests. Completed contains exactly one outcome per requested
buffer, in order. It permits drawing with base-filter degradation, not a claim of fresh mipmaps.
If base-filter restoration or binding restoration fails, stop before later buffers, invalidate
snapshots/bindings, abort the frame without new flips, require full clear and mark estate stale;
Failed(failure,diagnosticId,true) means already aborted and shaders-off required. P7 makes no
later complete/discard/abort call and still closes any independently owned lease in finally.
Rejected is mutation-free and forbids drawing; caller follows ordinary frame protocol recovery.

Before any owned clear or pass write can touch a color side, P5 invalidates that side's chain,
advances its write revision and restores its complete owner-authored base parameter value.
Snapshot preparation does this for frozen write sides before installing/exposing Acquired and
before the mipmap operation. Drain native errors and check binding restoration; any preparation
failure returns PassSnapshotResult.Failed(failure,diagnosticId,true) under §2.2, stopping before
draw/new flip and consuming the frame even if abort cleanup also fails. No protocol rejection or
uncaught backend exception substitutes for this result. Owned-clear failure uses executeClear's
BACKEND_FAILED and §6 recovery instead; it is not a snapshot result.
If read/write overlap exists, clear freshness after the draw and restore base before any later sampling. Completion
and abort both conservatively invalidate potentially written sides, including partial draws.
Discard alone does not advertise freshness for a side already marked potentially written.
Frame begin resets all sides to base filters/not-fresh; successful commit's metadata rebase
does not invent freshness. Creation/reallocation/fallback starts not-fresh; refresh, resize,
abort/full-clear and publication retirement invalidate all affected chains and snapshots.
Custom/foreign replacement parameters are never changed; P5 mipmaps only its logical estate.

All color textures use `CLAMP_TO_EDGE` for S/T. Min and mag filters are both NEAREST for integer
formats and LINEAR otherwise. Mipmap allocation/generation is not implicit: Phase 7 sequences
logical-buffer work owned by Phase 5 through Phase 1's capability-gated verb. A custom/foreign
override is never reparameterized by work on its replaced base buffer. Textures begin at level zero.

**D-P5-42 — mandatory target-bearing synchronous allocation.** P1 §4.7.7a/D-P1-63
grants the shared closed `engine.gl` values; P5 owns their format meanings here:
`TextureSpec.ColorTextureSpec(TextureAllocationTarget target, ColorInternalFormat format,
PixelLayout.Color allocationLayout, TextureExtent extent, int mipLevels)` and
`TextureSpec.DepthTextureSpec(TextureAllocationTarget target, DepthAttachmentFormat format,
PixelLayout.Depth allocationLayout, TextureExtent extent, int mipLevels)`.
`TextureExtent(width,height,depth)`, `TextureRegion(x,y,z,width,height,depth)` and
`TextureData(target,region,mipLevel,PixelLayout,ByteBuffer texels)` are exactly that grant,
not a P13-only allocation sum. `TextureAllocationTarget` closes
TEXTURE_1D/TEXTURE_2D/TEXTURE_3D/RECTANGLE; P3 target/format/transfer enumerants convert
one-for-one into these facade values without parsing or broadening accepted pack names.
P5's `ColorInternalFormat` remains the exact 38-value allocation vocabulary above.
`PixelLayout.Color(PixelFormat,PixelType)` and `PixelLayout.Depth(DepthTransferLayout)`
are disjoint; `DepthTransferLayout` is exactly DEPTH_COMPONENT_FLOAT or
DEPTH_STENCIL_UNSIGNED_INT_24_8. Their native depth pairs are specified below.

All P5 main/alt color, shadow color, owned main-depth copies and owned shadow depth use
target TEXTURE_2D and extent `(plannedWidth,plannedHeight,1)`, initially mipLevels=1.
The initial color format/layout is exactly §4.2's table (including private RGBA_COMPAT
fallback, integer branch and unchanged requested-versus-realized provenance). Owned
main-depth copies use the source's actual depth format and its corresponding depth layout,
never a guessed default; standalone shadow depth uses DEPTH_COMPONENT with
DEPTH_COMPONENT_FLOAT. DepthTextureSpec is 2D-only. Main platform depth remains
platform-owned: the existing main-depth bridge, not an engine setter on its borrowed
handle, establishes DEPTH_COMPONENT or DEPTH24_STENCIL8 according to actual stencil state.
Main-depth sampling baseline is NEAREST min/mag, CLAMP_TO_EDGE S/T (R canonical unused),
NONE/LEQUAL compare, base=max=0 and LEGACY_DEPTH_LUMINANCE; owned copies use the same
baseline. The platform bridge establishes its equivalent before lending; borrowed clients
never allocate/parameterize it. Shadow depth/color retain exact effective P3 nearest/mipmap/
hardware-filter settings and existing P5 shadow policy, not these main-depth defaults.
Remaining fields in every owned call are P1 §4.7.7's full neutral owner conversion.

Mipmap generation stays an explicit owner transaction after initialization, never an
allocation side effect. P1 counts initially defined levels; its level-l dimensions are
`max(1,base>>l)` and generated-chain metadata is updated only on success. P5 initially
admits level0, sets its owner-planned last level before generation, and restores the
already-contracted base policy on write/abort/frame transitions; no mip-freshness changes.
Depth initialization/copy receives `(srcX,srcY,0,width,height,1)` and writes destination
origin0/level0. First-copy exact-format definition, steady-copy storage preservation,
capability tiers, attachment permissions, failure and framebuffer/binding restoration remain.

P13 raw allocation and upload retain every App B.4 enumerant; they do **not** reuse P5's
null-allocation table in place of the declared transfer format/type. P1's exact target,
dimension/mip/region, byte-count, tight-unpack and synchronous borrowed-buffer contract
is incorporated. Scalar layouts use component count times scalar width; packed layouts
use one named word per pixel. Three-component packed types (3_3_2/2_3_3_REV/5_6_5/
5_6_5_REV) require RGB or RGB_INTEGER; four-component packed types require RGBA/BGRA
or their integer variants. Integer storage requires integer transfer format and an integer
scalar/packed type (never HALF_FLOAT/FLOAT); noninteger storage requires noninteger format.
Unsupported capability combinations fail preflight, without coercing a requested format.
Depth layouts are not new raw pack formats. P5 owns this single legality table; P13/P1
consume it rather than implement a second policy/parser.

D-P5-47 uses P1 D-P1-66 directly in this pure admission: 1D width and2D width/height
use maxTextureSize, every3D axis max3DTextureSize, RECT width/height
maxRectangleTextureSize. The nonordinary gate definitions and required exact keys are
P1 §4.7.2's, incorporated without independent derivation. Unsupported-zero target
rejects capability before allocation; positive supported maximum is compared on every
used axis, including equality acceptance. Native probe failure supplies no profile or
guessed minimum. D-P5-46 additionally requires the actual GL3/EXT_integer clear tier
before integer estate admission; existing completeness fallback cannot hide its absence.

Planned boundaries (not executed): same 2D and RECT extent remains distinct; all four raw
target arities, 3D mip/subregion bounds, packed versus scalar lengths, every 37-format
conversion and RGBA_COMPAT exclusion from raw; initial/steady depth copy preserves source
format; borrowed destination rejection and upload/restoration failure never publish usable
storage. P1/P5/P13 changed §5 requires fresh review, not implementation clearance.

`DEPTH_COMPONENT` maps to the internal depth-component layout with `DEPTH_COMPONENT/FLOAT`;
`DEPTH24_STENCIL8` maps to `DEPTH_STENCIL/UNSIGNED_INT_24_8`. Those depth layouts are internal
allocation vocabulary, not pack-facing App B.4 additions; the governing swap requirements name
both cases at `docs/design/v2.0-RC3/DESIGN.md:1621`–`:1629`. Raw custom texture upload retains the
full App B.4 pixel-format/type vocabularies under Phase 13; the canonical allocation choices above
do not narrow that separate contract.

The closed pack-facing transfer vocabularies are exact:

```text
PixelFormat =
  RED RG RGB BGR RGBA BGRA
  RED_INTEGER RG_INTEGER RGB_INTEGER BGR_INTEGER RGBA_INTEGER BGRA_INTEGER

PixelType =
  BYTE SHORT INT HALF_FLOAT FLOAT
  UNSIGNED_BYTE UNSIGNED_BYTE_3_3_2 UNSIGNED_BYTE_2_3_3_REV
  UNSIGNED_SHORT UNSIGNED_SHORT_5_6_5 UNSIGNED_SHORT_5_6_5_REV
  UNSIGNED_SHORT_4_4_4_4 UNSIGNED_SHORT_4_4_4_4_REV
  UNSIGNED_SHORT_5_5_5_1 UNSIGNED_SHORT_1_5_5_5_REV
  UNSIGNED_INT UNSIGNED_INT_8_8_8_8 UNSIGNED_INT_8_8_8_8_REV
  UNSIGNED_INT_10_10_10_2 UNSIGNED_INT_2_10_10_10_REV
```

These reproduce `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:495`–`:533`. Invalid
integer-internal-format/non-integer-`PixelFormat` combinations are rejected during pure planning.

### 4.3 dfb allocation and ownership

For each logical colortex:

```java
record ColorPair(
    LogicalBuffer logical,
    TextureHandle sideA,
    TextureHandle sideB,
    ColorInternalFormat format,
    Extent2i extent,
    ClearPolicy clear,
    PhysicalSide committedMain,
    boolean flipped) {}
```

Creation order is ascending logical index, A before B. Destruction is the reverse. Every texture is
allocated before any pass FBO is published. `DebugService.label` labels both sides
`colortex<N>.A` / `.B`; the no-op v0.1 implementation makes the call harmless.

The baseline always allocates colortex0–3. A higher active reference/routing request grows the
contiguous set through the highest required G6 index. This is scan-driven while preserving legacy
assumptions that lower indices exist. It does **not** allocate all 16 merely because the model can
name them.

Phase 5 owns three FBO classes:

- **pass FBOs**: packed color attachments selected from one immutable pass snapshot, with the current
  main depth attachment where the pass needs depth;
- **clear FBOs**: temporary/cacheable packed attachments grouped by extent, color, and physical side;
- **copy destination FBOs**: backend-private only when the GL3 combined depth-stencil strategy needs
  one.

The cache key is `(estateGeneration, mainDepthVersion, ordered attachment handles, hasDepth)`.
Before each use, attachment handles are compared with the cached key; changed physical orientation or
depth version reattaches in place. No unbounded FBO-per-bitset cache exists.

### 4.4 Flip state machine and frame-end decision

#### 4.4.1 Side interpretation

`committedMain` is durable across frames. `flipped` is relative to it:

| committedMain | flipped | logical read/main | logical write/alt |
|---|---:|---|---|
| A | false | A | B |
| A | true | B | A |
| B | false | B | A |
| B | true | A | B |

This is the exact Pintonium-observed relation:
`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/BufferFlipper.java:25`
states *"not flipped ... write to the alternate ... read from the main"* and
`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/BufferFlipper.java:27`
states the inverse when flipped. The table is adopted only after its App B.1 contract check
(D-P5-1).

#### 4.4.2 Pass snapshot and transition

A snapshot is created before any pass attachment or sampler bind:

1. authenticate the already-selected Phase 4 provider/state; never resolve again;
2. freeze `read/main` for every readable buffer;
3. freeze `write/alt` for every deferred/composite write;
4. freeze `read/main` as the gbuffers write target;
5. derive the FBO/attachment and unit-binding snapshot from those same handles;
6. calculate, but do not yet apply, the post-pass flip set; and
7. prepare frozen write sides under §4.2.1, then install/expose Acquired only on success.
   Any mutation-bearing preparation failure takes §2.2's Failed(...,true) terminal transition;
   no partially prepared snapshot is returned. Validate all protocol inputs before step 2.

Effective transition rules:

| Pass case | Writes | Explicit `flip.<pass>.<buf>` | Transition |
|---|---|---|---|
| gbuffers | main | absent | no automatic flip |
| deferred/composite raster | alt | absent | toggle every written buffer after successful draw |
| any raster | any | `false` | suppress that buffer's automatic toggle |
| any raster | written or not | `true` | toggle that buffer after successful draw |
| virtual `deferred_pre` / `composite_pre` | none | `true` | toggle before the following raster snapshot |
| virtual pre | none | absent/`false` | no transition |
| skipped/failed-safe pass | none committed | any | no transition |

Virtual rows never enter `snapshot(pass, selection)`. Phase 7 instead calls
`applyVirtualTransition(frameId, pass)` with the exact Phase 4 descriptor. The estate accepts only
the planned value-equal `deferred_pre` or `composite_pre` descriptor whose slot is
`VIRTUAL_FLIP_CONTROL`, whose draw/compute sets are empty, and whose only transition input is
`resources.explicitFlips`. It validates current publication generation, depth-attachment epoch,
matching open frame, no open raster snapshot, and no draw-buffers-none lease before mutation.
Unknown/altered/raster descriptors reject as `INVALID_VIRTUAL_TRANSITION`; a second call for the
same virtual descriptor in one frame rejects as `DUPLICATE_VIRTUAL_TRANSITION`.

On the first valid call, each canonical-order explicit `true` buffer toggles immediately and is
returned by `Applied`; absent/`false` entries do nothing, and an empty toggle set returns
`NoChange`. Both success variants consume that virtual transition for the frame, issue no program
selection or draw, and affect the very next raster snapshot. Rejection is mutation-free. No
`ResolvedProgramDescriptor` is accepted, looked up, or fabricated (`[D-P5-18]`).

`Acquired` confirms successful snapshot preparation, not permission to draw. The exact selector, successful
required texture binds and activation must all remain current before a draw. `Completed` is the
successful-draw boundary; completion rejection aborts the frame. `discardPass(snapshot)` reuses
completePass authentication, is legal only before a draw is committed, consumes the open snapshot
without flips or post-draw mipmaps, and returns Discarded(frameId) or Rejected(FrameProtocolRejection).
Binding suppression/Skipped uses discard, never false completion or abort of unrelated programs.
Acquiring, binding or discarding a snapshot never exposes an unwritten alt side.

#### 4.4.3 Frame end: carryover without copy-back

`[D-P5-4]` rejects Iris/Pintonium's alt→main `SwapPass` as contract behavior. App F.7 requires the
last writer's flip to remain visible to later passes. After `Final` has consumed that state,
`commitFrame` **rebases metadata**:

```text
if flipped:
    committedMain = opposite(committedMain)
flipped = false
```

No texel is copied. The physical side holding the newest value becomes next frame's committed
logical main, satisfying the assignment's requirement that the next frame begin unflipped while
preserving OF-faithful last-writer carryover. The copy-back conflict is explicit in
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:229`–`:236`; RESEARCH wins.

`beginFrame` returns `Begun` only after installing the frame token; on stale generation/epoch,
another open frame, or non-normalized state it returns the corresponding `Rejected` without GL or
state mutation. A setup-backend failure returns `BackendFailed`, installs no frame token, and makes
the estate stale under §6 recovery.

`commitFrame` requires the matching open frame, no open pass snapshot, and no open
draw-buffers-none lease. `Committed` performs the
rebase above, clears the frame token, and leaves normalized flip state. A protocol rejection changes
nothing. An open overlay lease rejects commit as `OPEN_DRAW_BUFFERS_NONE_LEASE`. `abortFrame` has
the same token checks, but intentionally consumes any open pass snapshot and restores/consumes an
open draw-buffers-none lease,
rebases the current readable side, clears every relative flip and the frame token, marks
`fullClearRequired`, and returns `Aborted` with the caller's sanitized diagnostic ID. It cannot undo
texels already written, so the next frame's mandatory full clear is the safety boundary. If backend
work needed by either end operation fails, `BackendFailed` clears the token, normalizes by the same
rebase, requires full clear, makes the estate stale, and enters §6 shaders-off recovery. Phase 7
must branch on every closed result: proceed only on `Begun`/`Committed`, retain the diagnostic and
honor the mandatory clear on `Aborted`, correct protocol ordering on `Rejected`, and publish
shaders-off before another shader draw on `BackendFailed`.
The same terminal normalization is internal to `PassSnapshotResult.Failed(...,true)`; its
frame is already consumed even if backend restoration fails. P7 must not call either end
operation again. Its ordinary, nested-resume and fullscreen paths share this terminal disposition
with outer-finally recovery while retaining independent binding/lease/local-state closure.

The same generic machine is used for shadowcolor pairs. A local no-op flip or hardcoded
“read main” shortcut is forbidden; this is not the historical B4 claim about pinned method bodies.

### 4.5 Phase 4 route realization

Phase 4 exposes exact/symbolic writes and explicitly warns:
*"Phase 5 must not infer a resolved ping-pong side from `explicitFlips`; it owns the side state"*
(`docs/phase4/v1/PHASE_4_DOC.md:2165`). Phase 5 obeys that division.

`PassBufferPlanner`:

- expands `AllUsedBuffers(COLORTEX)` to the estate's ascending allocated set;
- preserves every Explicit slot and its output ordinal, including None;
- uses the effective provider's entire `ProgramStateBundle`, never requested-slot overlays;
- merges Phase 4's logical pass resource access with the current Phase 5 snapshot;
- reports a read/write intersection as a pack diagnostic but preserves the contract behavior;
- returns `PassDrawTarget.Screen.INSTANCE` and no engine FBO for `StageId.FINAL`; that terminal
  requires Phase 7 to bind
  Minecraft's framebuffer and apply Minecraft's anaglyph-aware color mask before the draw; and
- retains dormant `SHADOWCOMP`, `PREPARE`, `BEGIN`, and `SETUP` identities without wiring them.

For each Attachment slot in order allocate the next dense physical attachment index, retaining
the original output ordinal in ColorAttachment; append FramebufferDrawSlot.Attachment(index).
For None append FramebufferDrawSlot.None and allocate/attach nothing. Thus 0N2 attaches logical
0/2 at physical 0/1 but sends [Attachment(0),None,Attachment(1)], never [0,1].
Repeated/leading/trailing/all-none entries survive; only non-None entries write/flip. Validate
full slot length against maxDrawBuffers, non-None count against maxColorAttachments, family
inventory and duplicate non-None logical buffers before GL. AllUsedBuffers expansion uses the
same checks. All-none retains its full route; overlay no-write uses empty typed list and exact
saved typed route restoration. FBO/cache identity includes the full tagged positional route.

Pass FBO attachment updates occur before binding. `FramebufferService.check` runs at candidate build
and after a main-depth version reattachment, not on every bind.

#### 4.5.1 Balanced draw-buffers-none overlay lease

`openDrawBuffersNone(frameId)` is the only route for Phase 7's first-person overlay scope. It checks
current publication generation, depth-attachment epoch, exact open frame ID, no open pass snapshot,
and no existing overlay lease, in that order. Rejection is pre-GL and mutation-free. On success the
backend privately snapshots the current draw-buffer selection, applies draw-buffers-none, and
returns the sole `DrawBuffersNoneLease`; no raw buffer enum/list crosses the engine seam.

Phase 7 closes the lease in `finally`. The first valid close repeats the generation/epoch/frame and
current-lease identity checks, restores the exact prior selection, consumes the lease, and returns
`Restored`; later closes return `AlreadyClosed`. A foreign, wrong-frame, stale, or superseded lease
rejects as `INVALID_DRAW_BUFFERS_NONE_LEASE` before GL. Open/restore backend failure consumes any
issued lease, marks the estate stale, requires full clear and shaders-off recovery, and Phase 7 may
not continue shader drawing. `abortFrame` performs the same best-effort restoration and consumes an
open lease so a later `finally` close is `AlreadyClosed`. Publication/teardown occurs only at a safe
boundary with no open frame, pass, or lease (`[D-P5-19]`).

### 4.6 Clear policy and execution

Clear timing belongs to Phase 7. Phase 5 accepts:

```java
public record ClearRequest(
    long frameId,
    float fogRed,
    float fogGreen,
    float fogBlue,
    boolean fullClear) {}
```

Default colors:

| Buffer | Default |
|---|---|
| colortex0 | `(fogRed, fogGreen, fogBlue, 1.0)` |
| colortex1 | `(1.0, 1.0, 1.0, 1.0)` |
| colortex2+ | `(0.0, 0.0, 0.0, 0.0)` |

The colortex0 alpha is contractually forced to `1.0`, not copied from a four-component fog value.
The deployed comment records the reason verbatim: *"Sildur's Vibrant Shaders will give you pink
reflections"* otherwise
(`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java]`;
`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java:549`
–`:551`). D-P5-2 retains the quirk.

An explicit Phase 3 clear color replaces all four components. An explicit clear-disabled value
suppresses an ordinary clear but **not** a full clear after creation, resize, abort, or format
fallback.

**D-P5-46 — finite typed conversion against the REALIZED attachment.** Classify each
allocated format, not the requested pre-fallback format. P5 converts every selected
component (default/fog or explicit override) before any clear execution. Reject NaN or
infinity in any selected component; an invalid plan remains owner-issued but nonexecutable,
and executeClear returns STALE_OR_PROTOCOL_REJECTED without mutation or consuming the
full-clear requirement. No native/JVM implementation-defined cast is the policy:

| Realized numeric class | Deterministic component conversion / P1 payload |
|---|---|
| normalized fixed-point, including private RGBA_COMPAT and mixed normalized formats | clamp finite input to `[0,1]`; Floating; native storage quantization remains format-defined |
| signed floating-point (16F/32F families) | clamp to the destination's largest finite magnitude (65504 for16F, Float.MAX_VALUE for32F), retaining sign; Floating |
| unsigned packed floating-point R11F_G11F_B10F | clamp R/G to `[0,65024]`, B to `[0,64512]`; Floating |
| shared-exponent RGB9_E5 | clamp RGB to `[0,65408]`; Floating; native shared-exponent rounding remains format-defined |
| signed integer 32I families | promote source float exactly to double, truncate toward zero mathematically, saturate to `[-2147483648,2147483647]`, then store int; Signed |
| unsigned integer 32UI families | promote exactly to double, truncate toward zero, saturate to `[0,4294967295]`, then store long; Unsigned |

Missing destination channels are ignored by storage, not fabricated as a different format;
the payload still contains all four converted components, with absent floating channels
using the32F rule. Normalize negative zero to positive zero. Explicit override still
replaces all four components before conversion; e.g. 1.9 becomes integer1, negative
unsigned becomes0, and float 2^32 saturates to unsigned4294967295, not signed wrap.
This deterministic saturation is a new local design policy, not claimed G6 observation.
P1 D-P1-67 grants the exact ColorClearValue.Floating/Signed/Unsigned records and
`FramebufferService.clearColorAttachment(f,drawBufferIndex,value)`. GL3 uses matching
glClearBuffer forms; GL2 integer support uses EXT_texture_integer's typed setter plus
single-target clear, never float glClearColor. Unsupported integer capability rejects
before candidate allocation; it is not an incomplete-FBO fallback trigger. Existing
whole-estate RGBA fallback preserves all §4.7 triggers: candidate-color allocation error,
incomplete pass/clear FBO, and internal-format capability rejection. Missing the required
integer-clear tier remains a separate preallocation hard rejection, never hidden by that
single retry. Recompute conversion from every successfully realized fallback format. [D-P5-49]

Side rules:

| Condition | Physical sides cleared | Flip change |
|---|---|---|
| full clear | A and B | none |
| normal clear, `flipped=false` | current read/main side | none |
| normal clear, `flipped=true` | both sides | none |
| clear disabled, normal clear | none | none |

The third row is the governing *"clears both sides when flipped"* behavior
(`docs/research/v1/RESEARCH.md:521`–`:523`). A clear never toggles.

Clear operations group buffers by equal `(extent, converted typed value, side)`; the
sealed value variant is part of the key, so signed/unsigned/floating never alias.
Within each first-occurrence-ordered group retain ascending logical buffer order and
chunk at `min(maxDrawBuffers,maxColorAttachments)`. Bind/attach the batch once using
the existing positional route, then clear each occupied route position in ascending
order through P1's typed facade. A route hole is skipped by P5, never counted as a
successful clear; no native clear broadcasts across numeric classes. The per-attachment
dispatch refines historical batching without changing equal-extent or physical-side policy.

Phase 7 owns the depth clear moment and Minecraft state bracketing. At that moment it calls
`executeClear(clearPlan(request))` on the render thread. The immutable plan carries estate
generation, attachment epoch, frame token, converted typed values and ordered color-clear
batches. `executeClear` rejects foreign, stale, invalid-value, previously executed or
wrong-frame plans before GL; otherwise it consumes execution authority once and dispatches
each occupied position exactly once. Bracket each typed call with P1 error drains;
any exception/work/restore error stops later clears and returns BACKEND_FAILED.
P1 §4.7.4b clears full attachment extent independent of scissor/masks and restores actual
read/draw bindings, destination routes, viewport, scissor, affected indexed/global color
masks, dither, supported sRGB/clamp state and legacy typed clear state/cache in finally.
P1 D-P1-69 additionally privately saves actual rasterizer-discard, disables it before clearing,
and independently restores it in finally on GL>=3.0 or EXT/NV_transform_feedback contexts.
Unsupported contexts never query/change that enum. Failure to disable, clear or restore is
BACKEND_FAILED under the same all-success/poison rule; P5 introduces no public state verb.
P5 also restores its outer batch FBO/route/viewport scope in finally, attempting independent
cleanup after failures. Restoration failure poisons estate draw admission, marks stale/off
recovery and cannot become SUCCESS. Partial texels are not rolled back. Ordinary backend
failure retains the existing caller abort path; no exception escapes, no depth clear occurs.

`clearPlan` derives effective full-clear intent as
`request.fullClear || estate.fullClearRequired`; the caller cannot suppress estate policy. The bit
is cleared only after every attachment in every batch and all inner/outer restoration
succeed for the validated generation, attachment epoch and frame. Rejection, partial
execution, work error or restoration failure retains or re-establishes it, so the next
valid plan remains a full clear. No flip changes on any clear outcome.

### 4.7 Allocation, framebuffer validation, and RGBA fallback

Creation is an ownership-ledger state machine:

```text
PLANNED
  -> ALLOCATING_TEXTURES
  -> ATTACHING_FRAMEBUFFERS
  -> CHECKING
  -> READY
or any intermediate state -> CLEANING_PARTIAL -> RETRY_RGBA -> READY | SHADERS_OFF
```

Every acquired handle enters the ledger immediately. Failure walks the ledger in reverse. No
partial candidate escapes.

The first attempt uses requested formats. Any of these causes the single fallback attempt:

- a facade allocation error attributed to a candidate color texture;
- a non-complete pass/clear FBO status; or
- an internal-format capability rejection.

The attempt is wholly deleted. The retry recreates **every color side** as private `RGBA_COMPAT`;
depth formats are not silently changed. One user-visible warning identifies the requested formats
and the fallback. If the retry is complete, the pack continues. If it fails, the candidate is
deleted, a pack-level error is reported, and the result is `ShadersOff`.

The fallback is estate-wide because mixing requested and fallback formats after an incomplete-FBO
result is driver-dependent and not the observed contract. A runtime pass does not retry formats.

### 4.8 depthtex0: sampleable main-depth bridge

Vanilla 1.12.2's framebuffer depth renderbuffer is not sampleable. The assigned reference replaces
it with a texture at creation:
`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinFramebuffer_Shaders.java:59`
disables the renderbuffer path;
`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinFramebuffer_Shaders.java:74`
–`:100` allocate and attach either depth-only or `DEPTH24_STENCIL8`.

Schmaloogium's equivalent has three pieces:

1. **`mod.mixin` injection:** around
   `net.minecraft.client.shader.Framebuffer#createFramebuffer(II)V` (1.12.2 SRG
   `func_147605_b`), suppress only the vanilla depth-renderbuffer allocation, delegate texture
   creation/attachment to `mod.glue`, restore vanilla's `useDepth` (`field_147619_e`), and publish
   no policy. These symbols/descriptors were resolved through the repository-required Cleanroom
   mappings service, not inferred from a patched source filename.
2. **`mod.glue` implementation:** create a NEAREST, CLAMP_TO_EDGE depth texture; attach it to depth
   and, when enabled, stencil; delete it with the owning Minecraft framebuffer; present its live
   ordinary platform `TextureHandle` to the same Phase 1 device's
   `FramebufferService.borrowDepthAttachment`, and expose the resulting backend-authenticated,
   non-owned `BorrowedDepthAttachmentHandle` with a monotonically changing Phase-5 version.
3. **`engine.buffers` SPI:**

```java
public interface MainDepthSource {
    MainDepthPreparation prepare(Extent2i requiredExtent); // safe point; may resize platform FBO
    MainDepthSnapshot current(); // render thread; never null
}

public sealed interface MainDepthPreparation {
    record Ready(MainDepthSnapshot.Available snapshot) implements MainDepthPreparation {}
    record Pending(long expectedVersion) implements MainDepthPreparation {}
    record Failed(String diagnosticId) implements MainDepthPreparation {}
}

public sealed interface MainDepthSnapshot {
    record Available(
        long version,
        BorrowedDepthAttachmentHandle texture,
        DepthAttachmentFormat format,
        Extent2i extent) implements MainDepthSnapshot {}
    record Unavailable(long version, String diagnosticId) implements MainDepthSnapshot {}
}

// BorrowedDepthAttachmentHandle is the verified Phase 1 engine.gl type. Phase 5 neither
// redeclares nor implements it; the receiving GLDevice issues it through borrowDepthAttachment.
public enum DepthAttachmentFormat { DEPTH_COMPONENT, DEPTH24_STENCIL8 }
```

The version changes whenever the underlying texture identity, format, extent, or availability
changes. It is equality-only; wrap is harmless at process timescales.

The dfb color attachments and borrowed depth attachment must have identical planned main extent.
After pure planning and before candidate creation, Phase 7 calls `prepare(plan.mainExtent())` at a
no-draw safe point. The `mod.glue` implementation creates/resizes Minecraft's shader framebuffer,
including its color attachment, to that extent and lets the mixin publish the replacement depth
texture/version. `Pending` keeps the vanilla path active and schedules re-evaluation; it is not
permission to attach the old texture. Candidate creation accepts only a `Ready` snapshot whose
extent exactly equals the plan. This closes the render-quality case that Pintonium cannot evidence
because PD says its global render-quality multiplier is fixed at 1.0
(`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:267`–`:270`).

On a new same-extent version, Phase 5 first advances `depthAttachmentEpoch`, invalidating all
extant pass, clear, binding, and shadow snapshots, and then:

1. closes every open pass snapshot;
2. reattaches every owned FBO that uses main depth, choosing depth-only or combined depth-stencil;
3. allocates both depth-copy targets in the new main-depth format, attaches their destination FBOs,
   and marks them uninitialized;
4. rechecks every affected main-depth and copy-destination FBO;
5. deletes the superseded copy-target textures only after all allocations, attachments, and checks
   succeed;
6. forces a full color clear.

`BufferEstateView.refreshMainDepth()` is the public render-thread comparison operation. Phase 7
calls it before `beginFrame` and again before each shader draw. It compares
`MainDepthSource.current()` with the version, format, handle identity, and extent cached by that
published estate. No change returns `Unchanged`; a new same-extent available snapshot performs
steps 1–6 above and returns `Reattached` with the new version and attachment epoch. An unavailable
snapshot or reattachment failure returns `Failed(BufferFailure)` under the §6 containment rules.

Successful `Reattached` is resumable in the same shader frame. Phase 7 abandons the invalidated
open pass snapshot without calling `completePass`, reacquires the current pass and texture-binding
snapshots, and only then may issue that shader draw. Earlier completed-pass flip state remains
intact; the forced full clear and uninitialized depth copies govern the remainder of the frame.

Refresh is fail-closed, not rolled back. If copy-target allocation, attachment, or any completeness
check fails, Phase 5 stops, deletes every newly allocated unattached or partially attached copy
target, closes open snapshots, leaves the advanced epoch in place, and marks the estate stale and
unusable. Superseded copy targets remain owned by the poisoned estate and are deleted when that
estate is closed after shaders-off replacement. Its cached prior depth identity is not updated, no
new snapshot or mutating operation is permitted, and partially refreshed FBOs are never drawn.
Phase 7 must
abort/normalize any active shader frame and publish shaders off; a later safe point must prepare,
build, and publish a complete replacement before shader drawing resumes. An unavailable
`current()` follows the same caller transition without attempting reattachment: it marks the
estate stale, advances the epoch once, and retains the cached prior identity.

An extent mismatch returns `ResizeRequired(BufferFailure)` whose failure code is exactly
`MAIN_DEPTH_RESIZE_REQUIRED`. That variant performs no GL call and changes neither estate state,
frame/flip state, attachments, generation, nor attachment epoch. Phase 7 must abort/normalize any
open shader frame, call `prepare(plan.mainExtent())`, build and publish a matching replacement, and
permit no further shader draw until that publication succeeds. The replaced estate then becomes
stale by normal publication generation. `ResizeRequired` construction rejects any other failure
code; `Failed` rejects `MAIN_DEPTH_RESIZE_REQUIRED`, so the closed result has one unambiguous
carrier for this transition.

This mirrors the assigned version-driven behavior at
`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/RenderTargets.java:148`
and its reattachment block beginning at
`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/RenderTargets.java:166`.

Phase 1 now supplies this exact narrow route. Its binding §5 distinguishes ordinary
`ForeignTextureProvider` values from backend-issued borrowed depth, exposes
`borrowDepthAttachment`, `attachDepthStencil`, and
`initializeDepthTextureFromFramebuffer`, and preserves Phase 5 ownership of format, freshness,
cadence, allocation/copy tier, restoration policy, and Minecraft lifetime
(`docs/phase1/v14/PHASE_1_DOC.md:5507`). The whole-document service contract additionally
requires forged, wrong-origin, wrong-device, stale, and illegal owned-versus-borrowed uses to fail
before GL; only an authenticated borrowed value may be sampled/labeled or used by depth-only and
combined depth/stencil attachment operations
(`docs/phase1/v14/PHASE_1_DOC.md:5512`). Round twenty verified that amended surface
(`docs/phase1/reviews/PHASE_1_REVIEW_20.md:61`–`:75`).

Phase 5 therefore uses `attachDepth` for depth-only snapshots and
`attachDepthStencil` only for `DEPTH24_STENCIL8`, reissuing/reacquiring the borrowed handle when
the platform identity changes. It never widens ordinary foreign handles, deletes or allocates a
borrowed handle, trusts a public marker implementation, or substitutes backend-state heuristics
for the explicit combined and initialization operations.

### 4.9 depthtex1/depthtex2 copies

Both copy targets:

- are Phase-5-owned textures;
- match main depth's extent and depth/depth-stencil format;
- use NEAREST + CLAMP_TO_EDGE;
- are reallocated and marked uninitialized on size/format/version change; and
- are never substituted for depthtex0's attachment.

The first copy after allocation/reallocation uses the verified
`FramebufferService.initializeDepthTextureFromFramebuffer` operation, whose contract is
`glCopyTexImage2D` semantics. Later `FramebufferService.copyDepthToTexture` calls use the fastest
valid backend tier:

| Priority | Condition | Mechanism |
|---:|---|---|
| 1 | `glCopyImageSubData` function pointer is non-null | texture→texture copy |
| 2 | GL 3.0 framebuffer blit available; mandatory for combined depth-stencil | depth(+stencil) blit with NEAREST into a backend-private destination FBO |
| 3 | GL 2.0 depth-only path | source-FBO→bound-depth-texture `glCopyTexSubImage2D` |
| fail | combined depth-stencil with neither tier 1 nor 2, or no valid tier | depth-copy feature unavailable; capability result below |

The tier-1 test is the function pointer, not merely version/extension flags. The evidence explicitly
warns that caps can lie at
`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/texture/DepthCopyStrategy.java:16`
–`:30`.

Phase 5 requires the selected backend operations to restore read/draw framebuffer and texture
bindings before return, matching the verified detailed Phase 1 service behavior. A pure
`DepthCopyStrategySelector` is tested with independent
`copyImageCallable`, `blitAvailable`, and `combinedStencil` booleans; those booleans are
`mod.glue` backend facts and do not leak GL constants into `:engine`.

`copyDepth(point, frameId)` permits each point at most once in order:

```text
FRAME_BEGUN -> PRE_WEATHER_CONSUMED -> PRE_TRANSLUCENT_CONSUMED -> FRAME_COMMITTED
```

This is world-render order: weather is rendered before the translucent terrain layer, so
`depthtex2` is captured first and `depthtex1` second. Asking for `PRE_TRANSLUCENT` before
`PRE_WEATHER`, or repeating either point, follows the closed rejection/duplicate rules below;
Phase 5 never silently reorders calls (`[D-P5-17]`).

Each destination also carries:

```text
UNINITIALIZED --initializeDepthTextureFromFramebuffer succeeds--> VALID
VALID         --copyDepthToTexture succeeds---------------------> VALID
any state     --allocation/version change-----------------------> UNINITIALIZED
any state     --copy fails--------------------------------------> DEGRADED_TO_DEPTHTEX0
```

While `UNINITIALIZED` or `DEGRADED_TO_DEPTHTEX0`, that destination's fixed unit resolves to the
current borrowed depthtex0 instead of undefined or stale storage. `VALID` contents persist across
frames until the contract copy point refreshes them; this preserves the deployed prior-frame
behavior before the new frame reaches that point. A successful initialization after rebuild returns
the destination to `VALID`.

Phase 7 owns the calls. A duplicate is diagnosed and ignored; an out-of-order request is
`Rejected(DEPTH_COPY_OUT_OF_ORDER)`, and Phase 7 aborts the current shader frame. A first-copy
failure or recurring copy failure binds depthtex0 as the temporary backing for the affected unit,
reports a feature-local degradation, and marks that copied-depth view unavailable to conformance
diagnostics. It never exposes stale previous-frame contents as current.

`DepthCopyResult` separates scheduled consumption from destination success. Both Copied and
BackendDegraded consume a valid next point exactly once; Copied also reports initialization.
At frame begin neither point is consumed; backend attempt consumes before return on either
outcome. Check consumed membership before next-point order so retrying failed PRE_WEATHER
after PRE_TRANSLUCENT is also DuplicateIgnored. Rejected calls consume nothing. New frame
resets point consumption, not destination validity; no in-frame retry can recreate success.
`DuplicateIgnored` emits its diagnostic but changes neither copy-point nor destination state.
`Rejected` covers the applicable stale-generation, stale-attachment-epoch, no-open-frame,
wrong-frame, or `DEPTH_COPY_OUT_OF_ORDER` rejection; it is pre-copy and mutation-free, and Phase 7
must abort the current shader frame. `BackendDegraded` moves the affected destination to
`DEGRADED_TO_DEPTHTEX0`, consumes that scheduled point, installs depthtex0 for its fixed unit,
emits the failure and diagnostic, and permits Phase 7 to continue that frame using fallback. These four
variants exhaust success, duplicate/no-op, protocol rejection, and backend failure.

### 4.10 Shadow estate

The sfb is planned exactly when Phase 3 reports at least one shadow depth buffer or at least one
shadow color buffer. When present:

- allocate shadowtex0 as the sfb's required physical depth attachment, plus shadowtex1 only when
  `shadowDepthBuffers() >= 2`; a color-only request does not change Phase 3's pack-facing
  `shadowDepthBuffers()` minimum or make an unreferenced shadow-depth sampler required;
- allocate zero to two shadowcolor pairs at v0.1/v0.2;
- size all at §4.11's shadow extent;
- attach shadowtex0 as real depth;
- apply per-depth hardware compare mode when requested;
- use the legacy `R,R,R,1` sampling swizzle for depth textures;
- apply per-texture NEAREST/LINEAR and mipmap flags from Phase 3; and
- expose Phase 8 operations to bind, clear, copy shadowtex1, and snapshot shadowcolor sides.

The old-pack swizzle and hardware compare are structurally corroborated at
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:273`–`:276`; the historical B4 stub
attribution is qualified below, not treated as a verified negative observation.

Shadow depth is not ping-ponged at v0.2: shadowtex0 is real depth and shadowtex1 is the translucent
split copy. Shadowcolor uses the generic pair/flip state now, even though shadowcomp execution is
post-v0.5. This makes the future `SHADOWCOMP` stage a new schedule population, not a new buffer
architecture.

When both shadow minima are zero, no sfb is planned and Phase 8 receives
`ShadowEstateNotRequested`; this is ordinary absence and does not create a `BufferFailure`. If sfb
creation fails while main dfb succeeds, the shadow
feature is disabled, neutral shadow bindings are supplied, and the program/main pipeline may
continue (rung 2a). The candidate owns a bounded neutral cache keyed by full compatible sampling
policy, not one shared depth object: at most one 1×1 fully-far depth texture per distinct required
shadowtex0/1 policy (at most two), and analogously at most two opaque-white color objects for
shadowcolor0/1. Key includes target/format class and the complete effective TextureParameters:
min/mag, all wraps, compare mode/function, border, LOD range/bias, anisotropy, base/max level
and depth swizzle. Normalize only extent-dependent final mip to zero for 1×1 storage; initialize
the complete admitted chain (level zero), depth=1/color=white, before candidate readiness.
Comparison NONE and REF_TO_TEXTURE can never share; different remaining parameters cannot
share either. No bind-time mutation or P14 sampler object is required. Cache is allocated before
real sfb construction, retained through creation failure/runtime neutralization, never allocated
per frame, and deleted once by candidate cleanup or reverse accepted-estate retirement after
bindings drain. Failed neutral initialization means no compatible safe fallback: fail candidate/
recover shaders off, not main continuation with an incompatible object. Phase 8 receives
ShadowEstateUnavailable rather than a partial sfb. The same unavailable result
is returned after a successful runtime `degradeToNeutral`; its `BufferFailure` identifies the
first neutralizing reason/diagnostic while the fixed unit table remains backed by the neutral
objects for that generation.

`shadow()` and the available view have this complete public shape:

```java
public sealed interface ShadowEstateResult
    permits ShadowEstateAvailable, ShadowEstateNotRequested, ShadowEstateUnavailable {}
public record ShadowEstateAvailable(ShadowEstateView view) implements ShadowEstateResult {}
public record ShadowEstateNotRequested(long estateGeneration) implements ShadowEstateResult {}
public record ShadowEstateUnavailable(BufferFailure reason, long estateGeneration)
    implements ShadowEstateResult {}

public interface ShadowEstateView {
    long estateGeneration();
    ShadowBeginResult beginPass(
        long frameId, PassDescriptor pass, ProgramBindingSelection selection);
    ShadowOperationResult bind(ShadowPassSnapshot snapshot);
    ShadowOperationResult clear(ShadowPassSnapshot snapshot, ClearRequest request);
    ShadowOperationResult copyDepth(
        ShadowPassSnapshot snapshot, ShadowDepthCopyPoint point);
    TextureBindingResult shadowBindings(
        long generation, long frameId, ShadowPassSnapshot snapshot,
        TextureOverlayLease overlay, TextureOverlayPublicationId expectedOverlay);
    ShadowMipmapResult generateShadowMipmaps(
        long generation, long frameId, ShadowPassSnapshot snapshot,
        ShadowMipmapPolicy policy);
    ShadowNeutralizationResult degradeToNeutral(
        long generation, ShadowNeutralReason reason);
    ShadowCompletionResult completePass(ShadowPassSnapshot snapshot);
    ShadowAbortResult abortPass(ShadowPassSnapshot snapshot, String diagnosticId);
}

public record ShadowPassSnapshot(
    long estateGeneration,
    long depthAttachmentEpoch,
    long frameId,
    PassDescriptor pass,
    ProgramBindingSelection selection,
    FramebufferHandle framebuffer,
    List<ColorAttachment> colorAttachments,
    Map<LogicalBuffer, TextureHandle> readableTextures,
    Set<LogicalBuffer> flipAfterPass) {}

public enum ShadowProtocolRejection {
    STALE_GENERATION, STALE_DEPTH_ATTACHMENT_EPOCH, PASS_ALREADY_OPEN, NO_OPEN_PASS,
    WRONG_FRAME_ID, FOREIGN_SNAPSHOT, CLOSED_SNAPSHOT
}
public enum ShadowDepthCopyPoint { SHADOW_PRE_TRANSLUCENT }
public sealed interface ShadowBeginResult {
    record Acquired(ShadowPassSnapshot snapshot) implements ShadowBeginResult {}
    record Rejected(ShadowProtocolRejection reason) implements ShadowBeginResult {}
}
public sealed interface ShadowOperationResult {
    record Applied() implements ShadowOperationResult {}
    record Rejected(ShadowProtocolRejection reason) implements ShadowOperationResult {}
    record BackendFailed(BufferFailure failure) implements ShadowOperationResult {}
}
public sealed interface ShadowCompletionResult {
    record Completed(long frameId) implements ShadowCompletionResult {}
    record Rejected(ShadowProtocolRejection reason) implements ShadowCompletionResult {}
}
public sealed interface ShadowAbortResult {
    record Aborted(long frameId, String diagnosticId, boolean fullClearRequired)
        implements ShadowAbortResult {}
    record Rejected(ShadowProtocolRejection reason) implements ShadowAbortResult {}
}

public record ShadowMipmapPolicy(List<LogicalBuffer> buffers) {}
public sealed interface ShadowMipmapResult {
    record Generated(List<ShadowMipmapOutcome> outcomes) implements ShadowMipmapResult {}
    record Neutralized(
        LogicalBuffer buffer, BufferFailure failure, String diagnosticId,
        boolean openSnapshotAborted) implements ShadowMipmapResult {}
    record Rejected(ShadowProtocolRejection reason) implements ShadowMipmapResult {}
}
public sealed interface ShadowMipmapOutcome {
    record Generated(LogicalBuffer buffer) implements ShadowMipmapOutcome {}
    record NotAllocated(LogicalBuffer buffer) implements ShadowMipmapOutcome {}
    record Degraded(
        LogicalBuffer buffer, BufferFailure failure, String diagnosticId)
        implements ShadowMipmapOutcome {}
}
public enum ShadowNeutralReason {
    BIND_BACKEND_FAILURE,
    CLEAR_BACKEND_FAILURE,
    DEPTH_COPY_BACKEND_FAILURE,
    MIPMAP_FILTER_RESTORE_FAILURE,
    PASS_BACKEND_FAILURE,
    EXPLICIT_FEATURE_DISABLE
}
public sealed interface ShadowNeutralizationResult {
    record Neutralized(
        long generation, String diagnosticId, boolean openSnapshotAborted)
        implements ShadowNeutralizationResult {}
    record AlreadyNeutral(long generation, String diagnosticId)
        implements ShadowNeutralizationResult {}
    record Rejected(ShadowProtocolRejection reason)
        implements ShadowNeutralizationResult {}
}
```

`beginPass(frameId,pass,selection) -> ShadowBeginResult` acquires the sole open
`ShadowPassSnapshot` or returns `Rejected`; a call while one is open returns
`Rejected(PASS_ALREADY_OPEN)` before GL and without mutation. `bind(snapshot)`,
`clear(snapshot,request)`, and
`copyDepth(snapshot,SHADOW_PRE_TRANSLUCENT)` each return `ShadowOperationResult`; and
`completePass(snapshot)` / `abortPass(snapshot,diagnosticId)` return their corresponding closed
result.

`shadowBindings(generation,frameId,snapshot,overlay,expectedOverlay)` returns the shared
`TextureBindingResult` of §§2.4/4.12: a closeable sixteen-row snapshot on Bound, or
Degraded/Rejected/BackendFailed with no lease transfer. Acquisition freezes **all ordinary and
shadow readable sides** in `readableTextures`; binding never consults live main-side state later.
The exact selection is mandatory and authenticated. Expiry on completion/abort/neutralization
does not remove the Bound owner's duty to close the binding snapshot in finally.

`ShadowMipmapPolicy.buffers` is immutable, duplicate-free, contains only typed shadow depth/color
logical buffers, and is stored in canonical `LogicalBuffer` order. Construction rejects any
out-of-domain or non-canonical value before the render transaction. `generateShadowMipmaps` uses
the same generation/frame/snapshot validation order as `shadowBindings`, then processes every
requested buffer in canonical order after drawing and before completion. The result contains one
outcome per request: `Generated`, `NotAllocated`, or `Degraded`. One buffer's generation failure
does not stop later buffers when Phase 5 successfully restores that texture's configured
non-mipmap min filter; it then returns `Degraded`, records the stable diagnostic ID, and never
advertises its stale chain as fresh. If restoration fails, Phase 5 stops before later buffers,
atomically performs the same containment as
`degradeToNeutral(generation,MIPMAP_FILTER_RESTORE_FAILURE)`, and returns result-level
`Neutralized` with the affected buffer, backend failure, stable diagnostic, and
`openSnapshotAborted=true`. The transition aborts the supplied snapshot without flips, invalidates
all pass/binding snapshots, restores safe framebuffer/texture state as far as the backend permits,
and makes later `shadow()` calls unavailable with units 4/5/13/14 neutral. Phase 8 stops the pass
and generation checks and makes no later complete, abort, or neutralization call. Phase 5 alone
chooses the current shadowcolor side and calls the capability-gated facade verb.

`degradeToNeutral(generation,reason)` checks generation before mutation. A stale generation returns
`Rejected(STALE_GENERATION)`. First success atomically aborts and invalidates any open shadow
snapshot without applying flips, restores safe framebuffer/texture state, invalidates every old
binding/pass snapshot, and changes the shared estate state so all later `shadow()` calls return
`ShadowEstateUnavailable` while units 4, 5, 13, and 14 resolve to the Phase-5-owned neutral
objects. It returns one stable diagnostic ID and whether it aborted an open snapshot. Repetition
returns `AlreadyNeutral` with the same generation/diagnostic and performs no GL or state mutation.
Real shadow handles remain Phase-5-owned and are deleted only by the ordinary reverse estate
teardown; no borrowed snapshot survives the transition.

The non-texture shadow operations are render-thread-only and return their exact
`ShadowProtocolRejection` before GL with no token/full-clear/flip mutation. `Applied` means bind,
clear or copy finished; their `ShadowOperationResult.BackendFailed` leaves the token open
and flip state unchanged for abort. The separate texture bind operation uses §4.12's
four-result protocol. For shadowcolor, D-P5-46's exact realized-format conversion,
typed facade dispatch, full-extent restoration and all-success full-clear consumption
apply unchanged; shadow depth clearing remains its separate existing operation.
Invalid selected shadow components produce BackendFailed before mutation with the
existing sanitized BufferFailure diagnostic, not an invented protocol enumerant.
Any typed clear/restoration failure likewise returns BackendFailed, retains shadow
full-clear and the open token for abort; unproven restoration forbids further shadow
drawing until neutralization.
`Completed` consumes
the token and applies exactly its generic shadowcolor transitions. `Aborted` consumes it without
flip, marks shadow full-clear-required, and records the diagnostic. A consumed token thereafter
returns `CLOSED_SNAPSHOT`; a snapshot not issued by this view returns `FOREIGN_SNAPSHOT`. Phase 8
branches only on these results: continue on acquisition/operation success, correct ordering or
reacquire the current estate after rejection, and abort immediately after backend failure. It
chooses pass order and the copy moment but cannot mutate sides directly.

### 4.11 Sizing, resize, and invalidation

#### 4.11.1 Extent formulas

For positive display width/height and finite positive multipliers:

```text
mainWidth  = max(1, round(displayWidth  * renderQuality))
mainHeight = max(1, round(displayHeight * renderQuality))

shadowSide = max(1, round(shadowMapResolution * shadowQuality))
```

All multiplication is checked in `double`; non-finite, overflow, or a result above
`maxTextureSize` is a capability failure before allocation.

`superSamplingLevel` is **pack-option compatibility only** under the maintainer's 2026-09-07
disposition (D-P5-27, §11.6). Phase 3 preserves the declared/selected value and its ordinary
GLSL source uses; Phase 5 does not receive or validate an engine sample count. `BufferSizing`
contains only `mainExtent` and `shadowExtent`, with no sampling accessor, synthetic level 1,
or retained resource-only level. For every declared option value the engine performs the
ordinary allocation/draw/final schedule: no extent multiplication, jitter, extra world/shadow
draw, accumulation or resolve is caused by the name. Pack GLSL may still use its value.
Ordinary option changes can change configuration fingerprints/materialized code and therefore
follow the existing rebuild path; this is not a special sampling-size invalidation rule.

#### 4.11.2 Change classification

| Change | Action |
|---|---|
| display or render-quality change | Phase 7 prepares Minecraft's shader framebuffer at the new main extent; rebuild/reallocate dfb pairs and depthtex1/2; rebuild pass/clear keys; full clear |
| same-extent main-depth identity/format change | §4.8 in-place reattachment; reallocate both copy targets in the current main-depth format and reattach their destination FBOs; attachment-epoch bump; full clear |
| main-depth extent differs from planned main extent | no attachment; full prepare/build/publication sequence; old estate goes stale |
| shadow resolution/quality change | rebuild sfb only; force shadow clear |
| pack/configuration fingerprint change | full candidate rebuild |
| registry fingerprint/route/flip change | full pass-plan candidate rebuild |
| color format/count change | full dfb candidate rebuild with fallback available |
| Phase 13 relative resource | publish `BufferResizeNotice`; Phase 13 resizes its own texture |
| G8/S2 relative SSBO/custom image | publish notice; later owner resizes its own object |

`BufferResizeNotice` is immutable and published only after the new estate is ready. Its closed
`BufferResizeReason` maps respectively to the rebuild-causing rows above: display extent, render
quality, main-depth extent mismatch, shadow resolution, shadow quality, pack/configuration
fingerprint, registry/route/flip plan, or color count/format. If several change in one publication,
the first applicable enum value in declaration order is reported deterministically. Phase 5 does
not resize Phase 13/14 objects.

`BufferEstatePublisher.addResizeConsumer(String consumerId, BufferResizeConsumer consumer,
BufferSizing acknowledgedSizing, long acknowledgedGeneration)` is render-thread-only and returns
the closed `BufferResizeRegistrationResult`: `Registered(registration)` on success or
`Rejected(reason)` with exactly `BLANK_CONSUMER_ID`, `DUPLICATE_LIVE_CONSUMER_ID`,
`FUTURE_ACKNOWLEDGED_GENERATION`, `UNKNOWN_ACKNOWLEDGED_GENERATION`, or
`ACKNOWLEDGED_SIZING_MISMATCH`. Rejection installs no
registration and changes no consumer acknowledgement, publication, generation, or drawing state.
A successful registration's `close()` removes the consumer idempotently at the same safe point.
`consumerId` must be nonblank and unique among live registrations. The publisher retains that
caller-supplied stable diagnostic identity unchanged for the registration's lifetime.
During accepted publication, the publisher installs the new generation, invokes consumers in
registration order once with its notice, and opens drawing only after every consumer returns
success. Callbacks and registration mutation are not reentrant.

The publisher tracks each registration's last successfully acknowledged sizing and generation.
A new registration supplies the sizing/generation its already-created resources acknowledge;
the publisher retains a generation→sizing fact for every successfully installed ready estate until
that publisher closes. A supplied generation greater than `current().generation()` is future; any
other generation absent from that ledger (including off and never-installed generations) is
unknown. A known generation is accepted only when `acknowledgedSizing` equals its recorded sizing;
otherwise rejection is `ACKNOWLEDGED_SIZING_MISMATCH`. Each callback receives
`BufferResizeNotice(oldSizing,newSizing,newGeneration,reason)` whose `oldSizing` is that consumer's
acknowledged sizing; success advances only that consumer's baseline. Thus a retry may send
different truthful `oldSizing` values to consumers and converges them on one `newSizing`.

On the first failed result or thrown callback, dispatch stops. The installed generation never opens
for drawing; consumers before the failure have received its notice and remaining consumers have
not. `ConsumerFailed.deliveredCount` is exactly the number of consumers before the failing consumer
that returned `SUCCESS` and advanced their acknowledged baselines; it excludes the failing callback
whether that callback returned `FAILED` or threw. The publisher diagnoses the consumer, replaces
that estate immediately with a shaders-off publication whose generation is the next integer, marks
the failed estate `REPLACED`, and closes its owned objects after outstanding non-owning calls are
excluded. `ConsumerFailed` returns the failed generation, resulting off publication, consumer
identity, and that count to Phase 7 as the recovery signal. Phase 7 must keep drawing gated and
decide registry replacement or retry; Phase 5 performs no registry rollback. Off publication sends
no resize notice.

The full resize checklist is therefore: colortex A/B, depthtex1/2, pass FBO keys, clear FBO keys,
copy destination state, full-clear bit, sfb when its extent changes, and a downstream notice. It
adopts the complete structural checklist in PD §5.3
(`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:263`–`:266`) while keeping SSBO/custom-image
ownership out (D-P5-14).


The notice carries no registry fingerprint. Phase7 closes the old Phase13 registration at the
quiescent replacement boundary, builds the new texture publication only after actual registry/
estate acceptance, then registers it with accepted sizing/generation. The consumer uses those
transaction-bound configuration/registry identities, never infers them from a resize reason.
An unsolicited callback outside a prepared pairing returns FAILED, not independently published
guessed texture state. Equal source/parameter identity permits same-owner size-independent reuse
only with allocation accounting across every live/retiring publication; equal hashes never confer
cross-owner reuse or permit old retirement to delete an object still referenced by the new one.

#### 4.11.3 Lifecycle

```text
ABSENT
  -> CANDIDATE_BUILDING
  -> READY_UNPUBLISHED
  -> PUBLISHED_NOT_OPEN
      -- all consumers succeed --> PUBLISHED
      -- consumer failure --> REPLACED -> CLOSED; publisher -> SHADERS_OFF
  -> REPLACED
  -> CLOSED

Any pre-publication failure -> CLOSED_PARTIAL -> SHADERS_OFF
```

All transitions are render-thread-only except pure planning. `close` is idempotent. A replaced view
returns `STALE_ESTATE` before GL. Each installed ready or off publication increments generation
once, so consumer failure consumes two consecutive generations. Consumers compare equality only.

Phase7 owns the encompassing coherent pipeline transaction (D-P5-22): stop admission, finish/abort
the frame and drain bindings/shadow execution before freezing configuration/resource epoch.
Create inactive Phase13 owner/adapters and the new Phase6 runtime; shadow planning is registry-
independent only under R7-13, otherwise its slot is NotInstalled. Preserve Phase9's pure candidate
and frozen-input validation. Compile Phase4 with the sole fixed policy and candidate runtime macro
contribution; plan/create Phase5 from that exact detached view. AwaitingMainDepth installs nothing.
Compose the three participants, revalidate all intended identities, close old shadow and texture
registrations, retire the old texture owner after draw/binding drain, then publish Phase4 Ready with an issued release context. Rejected keeps candidate
ownership; RecoveredOff is a result, never a RegistryPublication input.

After Accepted, Phase7 reacquires actual registry generation and adopts it into the new runtime;
REJECTED_RETIRED_GENERATION compensates before events/participants/frames. Publish Phase5 and
branch on Published/ProvenanceRejected/ConsumerFailed, retaining exact deliveredCount. Build
Phase13 against actual accepted registry/estate generations and frozen source catalog/payloads,
validate identity, register resize/hooks, then publish Phase9 and pass its synchronous geometry
invalidation gate. Atomically install the complete ActivePipeline and advance PipelineVersion
once; no intermediate draw/shadow call is legal.

Any rebuild failure chooses shaders-off, not prior-pipeline restoration. After any acceptance,
retire accepted textures through their owner, close only caller-owned candidates, close shadow,
reset/deactivate Phase9, `publishOff(BufferFailure)` then publish Phase4 ShadersOff with an issued
release context and handle its actual result. Preserve full geometry invalidation after ID-gate
failure. Retire candidate/replaced uniforms only under requested R7-11, never invented close().
An old texture publication may survive solely for outstanding leases, never as active fallback.

### 4.12 Fixed names, compatible candidates, and physical binding

#### 4.12.1 Sole fixed policy

`FixedSamplerPolicies.appB3()` implements Phase 4's read-only
`FixedSamplerLayoutPolicy.fingerprint()` and
`validate(StageId effectiveStage,StageBand effectiveBand,List<ProgramSamplerDeclaration> declarations)`.
It and `resolver()` are pure and available before any runtime, registry, estate or GL object.
They share one table/schema and `FixedSamplerPolicyFingerprint`; neither retains a runtime.
**D-P5-38 — candidate sampler integer plan.** appB3 also implements P4's
`initializationAssignments(ProgramSamplerLayout.Shader layout) -> List<SamplerUnitAssignment>`.
For a Valid same-policy layout, reuse `FixedSamplerResolver.resolve` for every validated band
with its effective stage. Require Ready and identical exact-name/unit pairs across bands;
inconsistency throws to P4's pre-GL INVALID_SAMPLER_POLICY boundary. Return an immutable
UTF-8 exact-name-sorted list with every declared sampler once, including optimized-out names,
and units 0–15. Compatible aliases retain separate names on their shared unit.
No runtime, estate, GL operation, second unit map or active-uniform guess is involved.
P4 freezes the list during complete planning and P1 D-P1-59 initializes the unpublished
linked candidate before validate. P6's runtime resolver and participant remain unchanged.
`lookup(exactName)` is exact/case-sensitive and returns Known/Unknown, never lowercases or
synthesizes colortex8–15 assignments. `resolve(name,layout,stage,band)` returns Resolved or
UnsupportedDomain; the layout is mandatory for conditional SHADOW.

| Unit | Gbuffers/shadow names | Deferred/composite/final names |
|---:|---|---|
| 0 | texture; tex only in shadow | colortex0, gcolor |
| 1 | lightmap | colortex1, gdepth |
| 2 | normals | colortex2, gnormal |
| 3 | specular | colortex3, composite |
| 4 | shadowtex0, watershadow, conditional shadow | same |
| 5 | shadowtex1, conditional shadow | same |
| 6 | depthtex0 | depthtex0, gdepthtex |
| 7 | gaux1 | colortex4, gaux1 |
| 8 | gaux2 | colortex5, gaux2 |
| 9 | gaux3 | colortex6, gaux3 |
| 10 | gaux4 | colortex7, gaux4 |
| 11 | depthtex1 | depthtex1 |
| 12 | none | depthtex2 |
| 13 | shadowcolor0, shadowcolor | same |
| 14 | shadowcolor1 | same |
| 15 | noisetex | same |

The normative table is `docs/research/v1/RESEARCH.md:1228-1255`. `shadow` resolves to5 iff the
effective provider's complete layout has a **direct sampler-compatible watershadow declaration**,
otherwise4; shadow-buffer count is irrelevant, preserving
`docs/phase6/v1/PHASE_6_DOC.md:1221-1223`. Both gbuffers bands share this map. Virtual steps have
no bindings; compute/unwired domains are unsupported, never treated as composite.

Policy validation preserves every full Phase 3 type and ordered declaration witness. Same-unit
incompatible types yield `ConflictingTypes(List<FixedUnitSamplerConflict>,List<SamplerLayoutIssue>)`;
unsupported names/domains/shapes yield `Unsupported(List<SamplerLayoutIssue>)`; valid input yields
`Valid`. Coexisting issues remain in ConflictingTypes. Fixed-unit conflicts carry unit0–15 and all
declarations in zero-based sampler-projection declaration order. Equal cross-band conflicts
coalesce their witnesses; issues order by band enum, declarationOrder, then issue-code.
Arrays or structs containing samplers remain full types with UNSUPPORTED_SHAPE, never scalar coercion.
Phase 4 validates every provider-permitted band before GL: typed `SAMPLER_LAYOUT` evidence plus
`SAMPLER_UNIT_TYPE_CONFLICT` or `SAMPLER_LAYOUT_UNSUPPORTED` fails only that provider and follows
ordinary fallback. Driver optimization never prunes declarations. Phase 5 repeats defensive checks
on publication input. Fixed/virtual empty variants have distinct canonical encodings.

The policy fingerprint is SHA-256 over Phase 3 §4.10 canonical framing, the distinct
`FixedSamplerPolicy/v1` domain/schema tag, every exact spelling/unit/stage-band mapping, conditional alias rule and shape/
validation schema. This is a new digest choice, not a claim about an existing implementation.
Planning and creation compare `registry.samplerPolicyFingerprint()` against this fingerprint even
for an empty registry. Provider layout and policy fingerprints participate in registry identity.
Resolver Ready bindings are immutable ascending-unit then fixed-name declaration-order rows,
retaining distinct exact names on one unit; Invalid retains the complete validation evidence.
FixedFunctionEmpty and VirtualNotApplicable resolve to an empty Ready list with the same policy
fingerprint; virtual execution never calls the binder. Unsupported shader domains return Invalid,
never an implicit fullscreen map. Null policy or invalid callback output is a typed Phase4 registry
build failure before GL, never skipped validation.

#### 4.12.2 Lossless candidate production and selection

Phase 13 produces §2.4's immutable `TextureCandidateTable`; Phase 5 alone interprets fixed units.
Custom entries expand GBUFFERS→GBUFFERS+SHADOW, DEFERRED→DEFERRED,
COMPOSITE→COMPOSITE+FINAL, preserving original `TextureBindingKey`, exact name, discriminator and
one canonical Phase 3 ordinal in every copy. Multiple targets remain in a cell. Phase 3 already
applied complete-key last-valid-wins before canonical stage/unsigned-UTF8-name/absent-then0–9/
source-kind ordering (`docs/phase3/v1/PHASE_3_DOC.md:3710`–`:3713`). Companion/noise origins never
fabricate custom keys. The new greatest-compatible-ordinal precedence is D-P5-21, not original
properties occurrence order.

After authentication, use only the selector's effective stage/band/provider layout:

1. For each declared exact name, perform the sole lookup/resolution and consult that exact cell.
   Aliases share a unit but do not rename source keys.
2. Filter by the entire sampled shape and actual target/format/comparison capability. D1/D2/D3/
   RECTANGLE correspond exactly to Phase 3's four texture targets. FLOAT/SIGNED_INT/UNSIGNED_INT
   must agree with the effective uploaded internal format; shadow samplers require real
   depth-comparison capability and matching comparison state. Arrayed, multisample, CUBE, BUFFER
   and aggregate samplers have no App F.5 target coercion. Estate/neutral/foreign backings carry
   the same typed capability metadata; dimension equality alone is insufficient.
3. Among compatible Custom entries only, greatest canonical ordinal wins. Thus raw1D and raw3D
   gaux1 entries stay at unit7 and different programs select their compatible object without
   source patching or renaming.
4. If none matches, distinguish NO_CANDIDATE from INCOMPATIBLE_CANDIDATE. For world/shadow
   normals/specular, first resolve the effective winning unit0 object under the same full-shape/
   custom rules (even if units2/3 appear earlier in declaration order). If unit0 is undeclared,
   use the lease's observed baseTexture/baseAtlasContext; otherwise use
   `overlay.atlasContext(winningUnit0Handle)` from the exact accepted P13 association.
   Select only a compatible Companion whose atlas equals that Atlas context and kind equals
   this name. Never rank companions by candidateOrdinal or infer identity from equal extent.
   NonAtlas/Unavailable, or no matching compatible companion, selects only the kind's compatible
   `DefaultFill` candidate; no other atlas may win. Multiple distinct matching companions/defaults
   are CONFLICTING_CANDIDATES. Disabled kinds retain NOT_CONFIGURED and allocate no default;
   missing/incompatible required default degrades with SUPPRESS_DRAW, never an unrelated texture.
   Compatible custom normals/specular still win before this entire fallback branch; custom unit0
   changes the base association, not that precedence. Noise/estate/foreign/neutral rules remain.
   Fullscreen never uses generated companions/defaults; its custom colortex/gaux overrides remain.
   Publication absence differs from a present empty publication. [D-P5-50]
5. Names sharing a unit must have identical compatible sampler shapes or produce
   CONFLICTING_SAMPLER_TYPES. Same-shape aliases with distinct final object source/parameterization
   identities produce CONFLICTING_CANDIDATES, not a traversal-order winner. Only identical object
   identity coalesces, retaining all exact names for Phase 6 integer uploads.
6. Return exactly sixteen ascending unit0–15 rows: BoundObject for used units, Unused for
   undeclared ones. MissingTextureBinding is resolution-only and aggregates into Degraded with
   SUPPRESS_DRAW; no successful drawable snapshot contains a missing/stale row.

Default compatible backings remain: foreign texture/lightmap at0/1 in gbuffers/shadow;
companions at2/3 there; frozen colortex0–7 at0/1/2/3/7/8/9/10 in fullscreen, and frozen colortex4–7
at7–10 in gbuffers/shadow; shadowtex0/1 real-or-neutral at4/5; borrowed depthtex0 at6;
§4.9 CopyOrMain depthtex1 at11 and fullscreen depthtex2 at12; frozen real-or-neutral shadowcolor0/1
at13/14; Phase13 noisetex at15. These are fallback choices, never required binds for undeclared
units. No free-unit search exists.

FixedFunctionEmpty is declaration-empty. Non-final fixed draws use vanilla state with purpose
NONE and no shader candidates/uploads. FINAL uses purpose FIXED_FUNCTION_PASSTHROUGH, binding
the compatible frozen colortex0 side at0 only; names is empty and no custom inference or shader
declaration is fabricated. Shader snapshots use purpose SHADER. VirtualNotApplicable bypasses
selection/binding entirely.

#### 4.12.3 Mutation-free preflight and mutation-bearing result

`textureBindings(snapshot,overlay,expectedOverlay)` and
`shadowBindings(generation,frameId,snapshot,overlay,expectedOverlay)` are **physical bind**
operations owned by Phase 5, not metadata requests. First-failure-wins preflight is:

| Order | Check | Rejection |
|---:|---|---|
| 1 | structural null/malformed input, then render thread | INVALID_INPUT, WRONG_THREAD |
| 2 | current estate generation (including shadow's explicit generation) | STALE_ESTATE_GENERATION |
| 3 | open frame, then explicit shadow frameId | NO_OPEN_FRAME, WRONG_FRAME_ID |
| 4 | issued/current pass snapshot, then depth attachment epoch | INVALID_PASS_SNAPSHOT, STALE_DEPTH_ATTACHMENT_EPOCH |
| 5 | Phase4 private selector authentication against originating context; then pass/provider/effective stage/band/layout pairing | mappings below |
| 6 | live/current overlay lease | CLOSED_OVERLAY_LEASE |
| 7 | exact expected overlay id | OVERLAY_PUBLICATION_ID_MISMATCH |
| 8 | registry fingerprint | REGISTRY_FINGERPRINT_MISMATCH |
| 9 | configuration/estate pairing and fixed policy fingerprint | CONFIGURATION_FINGERPRINT_MISMATCH, SAMPLER_LAYOUT_MISMATCH |
| 10 | full-shape candidate resolution | Degraded, not protocol Rejected |

At5, `INVALID_ISSUER` maps to INVALID_PROGRAM_SELECTION, `STALE_GENERATION` to
STALE_REGISTRY_GENERATION, `STALE_CONTEXT` and `WRONG_STAGE_BAND` to
PROGRAM_SELECTION_MISMATCH, and `PROVIDER_LAYOUT_MISMATCH` to
SAMPLER_LAYOUT_MISMATCH. A valid credential used with a different pass/effective provider/stage/band
maps to PROGRAM_SELECTION_MISMATCH; altered layout/policy membership maps to SAMPLER_LAYOUT_MISMATCH.
An overlay registry-generation mismatch is STALE_REGISTRY_GENERATION after its ID/fingerprint
checks. Its ID estate generation must match this estate in pairing check9. Resource epoch is
authenticated by the active Phase13 owner in lease currentness/atomic acquisition; a retired
resource publication cannot pass by equal hashes. Shadow keeps generation-before-frame-before-
snapshot, and overlay checks retain publication-ID-before-registry precedence.

The overlay lease's base observation is authenticated/current under P13 D-P13-43.
Check it as part of step6: a changed base serial makes isCurrent false and returns
CLOSED_OVERLAY_LEASE before mutation. BaseAtlasContext is an immutable value, not a caller's
authority assertion. `baseTexture()` supplies the actual observed foreign unit0 fallback;
`atlasContext(base)` is a pure lookup in this lease's accepted same-epoch atlas/object association,
returning NonAtlas for a known non-atlas and Unavailable when association cannot be authenticated.
No size, candidate ordinal, resource-name guess or previous atlasSize can manufacture Atlas.
Unit0 resolution is a preflight dependency only; physical rows still bind in ascending order.

All sixteen rows are resolved with **zero GL** before binding. Rejected and Degraded bind nothing,
transfer nothing, retain no lease and suppress only the selected draw/program. The caller closes
the acquired lease in finally and calls main `discardPass`, or shadow `abortPass`, without flips
or post-draw mipmaps. Never choose a different provider after binding selection.

Only after successful preflight, compute the 16-bit mask of BoundObject rows and call
`TextureService.prepareUnitBindings(mask)` once, then `bindToUnit(int,TextureHandle)` for
BoundObject rows in ascending unit order. P1 D-P1-60/P14 use the mask to clear native samplers
on Unused units; no texture mutation is inferred for those rows. Both operations use P1
authentication and error-drain/exception containment. A late error may follow partial mutation and returns
BackendFailed(BufferFailure): no ownership transfer/retained lease, no activation/upload/draw,
caller lease closure, and existing frame/shadow containment. It is not a mutation-free rejection.
Only completion of every required bind returns Bound and transfers the lease exactly once.
Compatible-base fallback diagnostics may accompany Bound; missing required backing may not.

Phase 7 removes its former manual row-bind loop. Immediately after Bound it activates
`UseProgramRequest(selection,context)` with the same context/selection; Phase4 uses its retained
private binding without resolving again. The unchanged Phase6 afterBind callback receives that
effective descriptor and uploads only fixed integers after object binds. No reentrant publication
transition may occur between preflight, bind and this activation on the render thread.

#### 4.12.4 Lifetime and shadow migration gate

A binding snapshot's usable lifetime is the intersection of its open pass/frame, estate/depth/
registry epochs, current selector/publication and its own open state. Completion, abort, discard,
neutralization, replacement and off invalidate use but never silently discharge closure duty.
After Bound the owner closes **only the binding snapshot** in finally; every other result or a
throw before transfer leaves the caller closing **only the acquired lease**. Binding snapshots are
immutable evidence/lifetime holders, not permission to replay expired handles.
Every actual external base bind/restoration invalidates base-serial currentness before another
draw. P7/P8 close the old binding, acquire a fresh context-bound lease and call this same binder
on the still-open main/shadow pass with its retained selector. No flip, snapshot completion,
provider selection, root-shadow activation or new native binder occurs merely for refresh.
During a P5-owned binding transaction the observer records actual unit0 changes but defers
delivery; after all binds succeed it commits the resulting evidence/lease serial atomically,
publishes atlasSize, and marks that expected echo handled without recursively refreshing.
On partial bind/observation failure no Bound is issued; caller owns the lease and containment
invalidates currentness. Binding close is ownership-only: it performs no native bind/restore
and releases the lease without changing the triggering actual base or serial. It cannot restore
a deleted name. Ordinary/nested/shadow restoration belongs the caller and observes the actual
restored object afresh. Failure/throw never cancels independently owed cleanup.

Phase 7 closes the parent's binding snapshot on nested suspension/completion, retains its original
selection/context on the logical scope stack, then reacquires physical sides, lease and binding on
pop and reactivates that retained selection. It never reselects the provider as a new requested
slot or reuses a consumed physical snapshot. Phase5 mipmaps operate on logical buffer objects,
never overwrite the effective parameters of a custom/foreign replacement.

On reload/off/close, Phase13 retires first and stops accepting leases, drawing unwinds and closes
bindings, then owned deletion waits for all leases to close. Retired handles are not an active
fallback. Borrowed handles are released, never deleted; outstanding leases delay deletion, not
stale-use rejection. Teardown defers deletion rather than blocking the render thread on itself.

The shared sixteen-row shadow operation supersedes the old R8-2 four-row borrowed model.
Phase 8 §§0.7–0.8/5 now adopts R7-12 binding and R7-13 planning, owner-designed/unverified and
receiver-adopted/unverified. Real shadow execution remains NotInstalled/unavailable until fresh
owner reviews and the remaining dependencies close; no four-row or prior-frame success is legal.

### 4.13 Diagnostics and teardown

`BufferFailure` is a closed value:

```java
public enum BufferFailureCode {
    INVALID_INPUT,
    UNSUPPORTED_POST_V05_BUFFER_INDEX,
    CAPABILITY_LIMIT,
    MAIN_DEPTH_UNAVAILABLE,
    BORROWED_DEPTH_CONTRACT_UNAVAILABLE,
    TEXTURE_ALLOCATION,
    FRAMEBUFFER_INCOMPLETE,
    FORMAT_FALLBACK_FAILED,
    DEPTH_COPY_UNAVAILABLE,
    MAIN_DEPTH_RESIZE_REQUIRED,
    STALE_ESTATE,
    PROTOCOL_VIOLATION,
    UNEXPECTED_BACKEND
}
```

It carries code, sanitized message key, diagnostic IDs, logical buffer/pass identities, requested
format when applicable, and no raw GL number. Driver detail goes to the GUI/log, not chat.

Teardown order:

1. reject new snapshots and mark the view stale;
   Phase7 first quiesces frame/shadow execution and closes all owned binding snapshots; Phase13
   retires its publication before borrowed services disappear, deferring deletion for live leases.
2. release cached pass/clear FBOs;
3. release backend-private copy destination FBOs through the facade implementation;
4. delete owned depth copy textures;
5. delete shadow owned textures/FBOs and neutral shadow fallbacks;
6. delete colortex pairs in reverse creation order; and
7. drop, but never delete, borrowed/foreign handles.

The recording backend must prove `noLeakedObjects()` and `noUseAfterDelete()`.

## 5. Cross-phase interfaces

### 5.1 Exposed interfaces and data contracts

| Exposed contract | Exact content | Consumer(s) |
|---|---|---|
| `BufferArchitecture.plan/create`, `BufferPlanRequest`, `BufferBuildRequest`, `BufferRuntimeInputs`, `BufferPlan`, `BufferPlanResult`, `BufferBuildResult`, `BufferFailure` | Phase-7-owned immutable configuration/registry/fingerprint/capability inputs plus runtime display extent, render quality, and shadow quality; all three runtime fields participate by value in planning identity and reuse, with no separate runtime revision or rebuild trigger; closed valid/invalid pure planning carries the complete `BufferResourceSnapshot` whenever derivable; `create` independently reruns identical planning from its request before render-thread creation; ready/awaiting-depth/closed-failure build results, no partial publication | Phase 7 bootstrap/reload; Phase 2 tests |
| `BufferArchitectures.create()` | dependency-free public acquisition in `engine.buffers`; returns a non-null stateless `BufferArchitecture`, no public constructor, GL/context/provider work or retained request/runtime. Pure `plan` is available before estate construction; `create(BufferBuildRequest)` retains its separate render-thread requirements | Phase 7; Phase 2 inspection |
| `BufferEstateCandidate`, `BufferEstateInspection`, `BufferEstatePublisher`, `PublishedBufferEstate`, `BufferPublicationResult` | exact §2.2 signatures and §4.11 transaction. Inspection is handle/generation-free; acceptance transfers ownership and issues the sole estate generation; ProvenanceRejected retains caller ownership; ConsumerFailed is an already installed off outcome with exact preceding-success deliveredCount. Phase4→Phase5 acceptance does not admit drawing until Phase13 pairing/registration and Phase9 geometry invalidation succeed. Failure compensates accepted publishers off, never revives an old registry | Phase 7; Phase 12 indirectly |
| `BufferEstateView`, `BufferSizing`, `Extent2i`, `BufferInventory`, `BufferInventoryEntry`, `ResolvedBufferFormat` | accepted immutable non-owning estate metadata and publisher-assigned generation; `Extent2i(int width, int height)` is the exact immutable extent pair and performs no constructor validation, while positive display dimensions remain a sizing precondition; `BufferSizing(Extent2i mainExtent, Optional<Extent2i> shadowExtent)` has structural equality over exactly those two fields; `shadowExtent` is present exactly when either Phase 3 shadow-depth or shadow-color minimum is positive; immutable domain/index-ordered logical inventory with per-domain counts and final resolved color/depth format; `resources()` is the candidate's identical handle-free available projection | Phases 6, 7, 8, 13, 14 |
| `BufferResourceSnapshot`, `BufferResourceProjection`, `ResourceEvidenceStage`, `ColorBufferResource`, `RealizedColorAllocation`, `ColorAllocationOrigin`, `ResourceClearPolicy` and existing other projection rows | Exact §2.2 declarations and §4.1.1 /4 mapping: equal-input PLANNED values equal with no allocation; successful candidate/accepted estate REALIZED values equal exactly; cross-stage comparison is declarative-fields-only. DefaultRgba stays distinct from RGBA8, fallback preserves requested formats with every realized color row's actual allocation/origin, fog policy fabricates no components | Phases 2, 7 |
| `refreshMainDepth`, `MainDepthRefreshResult` | public render-thread comparison against the published estate; closed unchanged/reattached/resize-required/failed outcomes; successful same-extent reattachment invalidates open snapshots but permits same-frame continuation only after Phase 7 abandons them and reacquires pass/binding snapshots; resize-required carries exactly `BufferFailureCode.MAIN_DEPTH_RESIZE_REQUIRED`, performs no GL or mutation, and requires abort/normalize plus prepare/build/publication; every failed outcome advances the attachment epoch, retains the cached prior identity, makes the estate stale/unusable, and requires abort/normalize plus shaders-off publication until safe-point replacement succeeds; no shader draw is permitted during either recovery | Phase 7 |
| `FrameProtocolRejection`, frame/pass result types, `PassDrawTarget`, `PassBufferSnapshot` | exact §2.2 schemas; `snapshot(PassDescriptor pass,ProgramBindingSelection selection)` stamps that mandatory selector after authentication. Snapshot fields are estateGeneration, depthAttachmentEpoch, frameId, pass, selection, colorAttachments, readableTextures, flipAfterPass, drawTarget. Acquired alone is not drawable; Bound plus same-selection activation is required. Completion alone commits flips; frame commit/abort and SCREEN semantics remain §§4.4–4.5 | Phase 7 |
| `PassSnapshotResult` | exact §2.2 Acquired / pre-mutation Rejected / Failed(BufferFailure failure,String diagnosticId,boolean frameAborted). Failed always has true: no returned snapshot, frame already consumed, all frame pass/binding snapshots invalidated, no draw/new flip, full-clear and stale/off recovery. P7 §4.4 ordinary and nested-pop acquisition, §4.6 fullscreen acquisition/cleanup and outer finish/finally share the terminal marker; no double complete/discard/commit/abort, but independently owned binding/lease/P4/local-state cleanup still runs | Phase 7 |
| `discardPass`, `PassDiscardResult` | `discardPass(PassBufferSnapshot snapshot)` → `Discarded(long frameId)` or `Rejected(FrameProtocolRejection reason)`; same completePass authentication, only before committed draw; consumes pass without flips or post-draw mipmaps; invalidates bindings without taking their closure duty | Phase 7 |
| `generateMainMipmaps(PassBufferSnapshot)` / `MainMipmapResult` / `MainMipmapOutcome` | §4.2.1 exact frozen effective-provider request and side/revision/filter contract; Completed(Generated/AlreadyFresh/Degraded rows), pre-mutation Rejected, already-frame-aborted Failed; call before binding/activation, owner-only mutation and reset | Phase 7 |
| `applyVirtualTransition`, `VirtualTransitionResult` | accepts only the exact planned Phase 4 `deferred_pre`/`composite_pre` `PassDescriptor`, checks current generation/epoch/frame and no open pass/overlay lease, applies explicit true flips before the next raster snapshot, consumes each descriptor once per frame, returns canonical flipped buffers or no-change, and never accepts/resolves a program (`[D-P5-18]`) | Phase 7 |
| `openDrawBuffersNone`, `DrawBuffersNoneOpenResult`, `DrawBuffersNoneLease`, `DrawBuffersNoneCloseResult` | balanced generation/epoch/frame/current-lease-checked scope: privately snapshot selection, apply none, restore exact prior selection in `finally`, idempotent close, abort cleanup, commit prohibition while open, and stale/off recovery on backend failure. No raw draw-buffer values cross the seam (`[D-P5-19]`) | Phase 7 first-person overlays |
| `ClearRequest`, `clearPlan`, `ClearExecutionPlan`, `executeClear`, `ClearExecutionResult` | exact immutable ClearRequest(long frameId,float fogRed,float fogGreen,float fogBlue,boolean fullClear); complete §4.6/D-P5-46 finite realized-format saturation, typed payloads, numeric-safe batches/per-attachment route-index dispatch and full-state/error restoration incorporated. P7 plans then executes; effective full clear is intent OR estate requirement; generation/epoch/frame/once checks precede GL; only every attachment plus every restoration succeeding consumes requirement, all other paths retain it without flip | Phase 7 |
| `DepthCopyPoint`, `copyDepth`, `DepthCopyResult` | Ordered FRAME_BEGUN→PRE_WEATHER_CONSUMED→PRE_TRANSLUCENT_CONSUMED→FRAME_COMMITTED. Copied and BackendDegraded both consume once; only Copied validates destination. Failed-point repeat is DuplicateIgnored; weather failure permits next translucent call; rejection is pre-copy and requires abort | Phase 7 |
| `BufferDomain`, `BufferIndex`, `LogicalBuffer`, `ColorAttachment` | immutable `BufferDomain { COLORTEX, SHADOWCOLOR, SHADOWTEX, DEPTH }`; `BufferIndex(int value)` rejects negative values; `LogicalBuffer(BufferDomain domain, BufferIndex index)`; `ColorAttachment(int outputOrdinal, int framebufferAttachment, LogicalBuffer logicalBuffer, TextureHandle physicalTexture)` | Phase 8; Phase 7 |
| `ShadowEstateResult`, `ShadowEstateAvailable`, `ShadowEstateNotRequested`, `ShadowEstateUnavailable`, `ShadowEstateView`, `ShadowPassSnapshot`, `ShadowProtocolRejection`, `ShadowDepthCopyPoint`, `ShadowBeginResult`, `ShadowOperationResult`, `ShadowCompletionResult`, `ShadowAbortResult` | `shadow()` returns available only while the sfb planned by positive shadow-depth or shadow-color demand is usable, not-requested only when both minima are zero, and unavailable after creation failure or runtime neutralization. A color-only sfb owns shadowtex0 solely as its required physical depth attachment without increasing Phase 3's pack-facing shadow-depth minimum. The view exposes generation plus typed begin/bind/clear/split-copy/complete/abort. Snapshot identity, closed outcomes, pre-GL rejection, no-flip abort, and mandatory backend-failure abort semantics are exactly §4.10; every named top-level shadow type is public | Phase 8; G8/S1 |
| `ShadowEstateView.shadowBindings` | exact `TextureBindingResult shadowBindings(long generation,long frameId,ShadowPassSnapshot snapshot,TextureOverlayLease overlay,TextureOverlayPublicationId expectedOverlay)`. Shared sixteen-row closeable binding protocol, never separate four rows; preflight preserves generation→frame→snapshot order before selector/overlay checks; Phase5 performs object binds before activation; Bound owner closes binding in finally, every other result leaves lease caller-owned | Phase 8 R7-12 adopted/unverified |
| `ShadowMipmapPolicy`, `ShadowMipmapResult`, `ShadowMipmapOutcome` | immutable canonical typed shadow-buffer request with the same generation/frame/snapshot validation. `Generated` carries one canonical-order `Generated`, `NotAllocated`, or `Degraded(buffer,failure,diagnosticId)` outcome per request; `Degraded` guarantees successful base-filter restoration and continuation. Restoration failure instead returns result-level `Neutralized(buffer,failure,diagnosticId,true)`, stops later buffers, atomically aborts the open snapshot without flips, invalidates all shadow snapshots, neutralizes units 4/5/13/14, and requires Phase 8 to stop without another completion/abort/neutralization call | Phase 8 |
| `ShadowNeutralReason`, `ShadowNeutralizationResult`, `degradeToNeutral` | generation-checked render-thread transition returning exactly `Neutralized(generation,diagnosticId,openSnapshotAborted)`, idempotent `AlreadyNeutral`, or `Rejected(STALE_GENERATION)`. First success aborts/invalidates an open snapshot without flips, restores safe bindings, invalidates every old pass/binding snapshot, makes later `shadow()` unavailable, and makes fixed units 4/5/13/14 coherently neutral for that generation | Phase 8; Phase 7 failure containment |
| `FixedSamplerPolicies`, `FixedSamplerName`, lookup/resolution/policy/resolver/results | exact complete §2.4 declarations and §4.12.1 table/schema incorporated: appB3() and resolver() are pure before registry/runtime/estate creation; same fingerprint, exact spellings and conditional watershadow rule; no second map or free-unit allocation | Phase 4 compile; Phase 6 R7-10 adopted/unverified participant; Phase 13 lookup |
| `TextureBindingCandidate`, `CandidateOrigin`, `TextureHandleRef`, `TextureCandidateTable`, `TextureCandidateEntry` | exact §2.4 field order/types/variants incorporated; immutable exact-name stage cells retain full sampled shapes, source/parameter identity, original keys/discriminators/ordinals and all targets. Greatest compatible custom ordinal wins; aliases do not rename keys; incompatible aliases/objects are typed conflicts | Phase 13 producer; Phase 5 selection |
| `TextureOverlaySnapshot`, `TextureOverlayLease`, `TextureOverlayPublicationId`, `TextureOverlayFingerprint`, `TextureOverlayAbsence` | exact §2.4 accessors/records/enums incorporated; id generation is estateGeneration, separate registryGeneration/resourceReloadEpoch/configuration/policy identities; lease isCurrent means open and current READY owner; content equality does not revive retired authority | Phase 13 producer; Phase 7/8 lease caller |
| `textureBindings`, `TextureBindingResult`, rejection/degradation/diagnostic/result-row/purpose types, `TextureBindingSnapshot` | exact §§2.2/2.4 declarations and §§4.12.2–4.12.4 semantics incorporated. `TextureBindingResult textureBindings(PassBufferSnapshot snapshot,TextureOverlayLease overlay,TextureOverlayPublicationId expectedOverlay)` executes ascending compatible object binds; Bound alone transfers lease into closeable sixteen-row snapshot with BoundObject/Unused. Degraded/Rejected do zero binds; BackendFailed may follow partial GL and transfers nothing. First failure wins; publication mismatch precedes registry mismatch. Only Bound owner closes binding; all other callers close lease; missing required backing suppresses/discards without flips; no manual Phase7 bind loop | Phases 7, 8, 13; Phase 6 uploads only |
| `BufferResizeNotice`, `BufferResizeReason`, `BufferResizeConsumer`, `ResizeConsumerResult`, `BufferResizeRegistrationResult`, `BufferResizeRegistration`, `BufferResizeRegistrationRejection` | notice exposes old/new structural sizing, new generation, and one closed rebuild reason; `BufferResizeReason` is ordered `DISPLAY_EXTENT`, `RENDER_QUALITY`, `MAIN_DEPTH_EXTENT`, `SHADOW_RESOLUTION`, `SHADOW_QUALITY`, `PACK_CONFIGURATION`, `REGISTRY_PLAN`, `COLOR_INVENTORY_OR_FORMAT`, and that listed declaration order is the priority for simultaneous changes; registration returns `Registered` or a stable rejected reason for blank ID, duplicate live ID, future generation, unknown generation, or sizing mismatch; ready generation→sizing facts remain known for the publisher lifetime, off/never-installed generations are unknown, and supplied sizing must exactly equal the known fact; rejection installs nothing and changes no acknowledgement/publication state; accepted identity is retained unchanged and returned by `ConsumerFailed`; render-thread removal, per-consumer acknowledged sizing/generation, and ordered synchronous delivery after install and before drawing; retry `oldSizing` is each consumer's truthful acknowledged baseline; first failure stops dispatch, disposes the unopened estate, and returns the consequent off publication | Phases 13 and 14; G8/S2 |
| `MainDepthSource`, `MainDepthPreparation`, `MainDepthSnapshot` | Phase-5-owned engine-side SPI implemented by `mod.glue`; safe-point platform-FBO preparation plus opaque depth identity/version/format/extent, no GL name. `MainDepthSnapshot.Available` embeds Phase 1's verified `BorrowedDepthAttachmentHandle`; Phase 5 does not redeclare or mint that type | Phase 7 installs/prepares; Phase 5 consumes |
| `ColorInternalFormat`, `PixelFormat`, `PixelType`, `DepthAttachmentFormat`, `DepthTransferLayout` | exact App B.4 vocabulary plus private color fallback/depth allocation values; integer classification and allocation layout | Phase 5 facade calls; Phase 13 raw-upload adapter |
| Mandatory synchronous texture values — D-P5-42 | §4.2 and P1 §4.7.7a exact shared TextureSpec/TextureData/TextureRegion/TextureExtent/PixelLayout/TextureAllocationTarget, format/transfer closure, target-specific dimensions/mips/bytes, explicit main/depth defaults and unchanged failure/restoration/borrowed permissions | P13 owned source conversion; P1 backend/recorder; P7/P8 existing depth consumers; P14 value-only |
| Complete owned-object baseline — D-P5-43 | P14 D-P14-31/P1 D-P1-64: every successful setParameters maintains entire latest authenticated object state alongside sampler cache, including temporary mip policy and its restoration; ordinary all-unit fixed-function clearing never exposes stale state; no borrowed mutation, failed setup/restore contains | P1/P13/P14; P7 ordinary/FINAL boundaries |
| Base-atlas selection/refresh — D-P5-50 | §2.4 BaseAtlasContext Atlas/NonAtlas/Unavailable; TextureOverlayLease.baseAtlasContext(), baseTexture(), atlasContext(TextureHandleRef); CandidateOrigin.DefaultFill(CompanionKind); §§4.12.2–4 exact accepted unit0 association, custom-first matching/default selection, serial currentness and transaction-observer refresh. Existing main/shadow binder signatures and closed results unchanged | P13 producer; P7/P8 actual binding callers |

All public signatures in §§2.2/2.4/4.10, including their full field order/types, closed variants,
validation precedence, ownership and lifetime, are incorporated into the matching rows above.
Consumer-visible changes update §5 in the same revision and require fresh whole-document PASS.
In particular shadow `beginPass(long frameId,PassDescriptor pass,ProgramBindingSelection selection)`
and `ShadowPassSnapshot(long estateGeneration,long depthAttachmentEpoch,long frameId,
PassDescriptor pass,ProgramBindingSelection selection,FramebufferHandle framebuffer,
List<ColorAttachment> colorAttachments,Map<LogicalBuffer,TextureHandle> readableTextures,
Set<LogicalBuffer> flipAfterPass)` replace descriptor-only acquisition and freeze both readable domains.

**IR-12 approved cutover (D-P5-27).** `BufferSizing` has exactly two fields as published above;
the former `superSamplingLevel()` accessor and constructor argument are removed, not aliased.
Consumers compare only those extents and obtain pack-option provenance from P3, never resource
metadata. All option values use P7's existing once-per-frame camera/history/uniform/shadow/
depth/flip/Final/before-present capture sequence. No engine SSAA effect, level gate, new sample
identity, resolve API or engine AA control is promised. P3 source materialization preserves
pack-authored uses, so this is not permission to normalize or erase the constant.

**IR-29 inspection acquisition (D-P5-25).** Phase 2 obtains `BufferArchitectures.create()` and
calls only `plan(BufferPlanRequest(configuration, registryView, registryView.fingerprint(),
capabilities, runtime))`, using the exact P3 inspection configuration and the detached view from
that configuration's P4 build. No public view configuration-fingerprint accessor is assumed;
the same-request association is retained by the adapter. Runtime is the explicitly recorded
`BufferRuntimeInputs(displayExtent, renderQuality, shadowQuality)` chosen for that run, with the
two multipliers decoded from canonical P3 settings, not guessed from macro strings or images.
The pure plan never creates an estate, calls GL, needs MainDepthSource or publishes runtime.
Copy the returned immutable `resources` from both `Valid` and `Invalid`; an
`Available` projection with `CapabilityGate.SHORTFALL` is complete legitimate capability evidence,
not synthetic success. `Unavailable` is incomplete and supplies no guessed numeric fields.
Missing P4 view likewise prevents this same-build plan: P4 Ready alone never proves capability OK
and P4 CAPABILITY failure/P3 minima never supply the exact shortfall. The golden writer receives
only source-free detached projections and recorded planning inputs, not requests/configuration/
handles. Factory and consumer adoption require fresh owner reviews; no result is claimed here.

### 5.2 Consumed Phase 1 contracts

| Phase 1 §5 contract | Use |
|---|---|
| module layout, C-1…C-4, package and `.internal` rules | all pure policy and glue placement |
| engine bring-up stage 2 | install the main-depth/foreign texture providers only after GL capabilities exist |
| `GLDevice` and its services | create/allocate/parameterize/bind/delete textures and FBOs, typed color clear via D-P1-67, snapshot/restore, labels |
| D-P1-67 `FramebufferService.clearColorAttachment(FramebufferHandle f,int drawBufferIndex,ColorClearValue value)` | P5 §4.6 converts finite values into Floating(float r,g,b,a), Signed(int r,g,b,a), Unsigned(long r,g,b,a in0..4294967295). Exact realized numeric class and positional non-None route, native GL3/EXT_integer capability dispatch, full-extent state/cache restoration, work and restore error drains are mandatory; no direct GL or float integer clear |
| `FramebufferService.borrowDepthAttachment`, `attachDepth`, `attachDepthStencil` | the same-device backend authenticates the live platform texture and returns a non-owned borrowed handle; only owned or authenticated borrowed depth may be attached, forged/wrong-origin values fail before GL, depth-only detaches stale stencil, and the combined form uses the same packed object at depth and stencil. Phase 5 owns format choice, freshness, reattachment cadence, restoration policy, and Minecraft lifetime |
| `FramebufferService.initializeDepthTextureFromFramebuffer`, `copyDepthToTexture` | explicit first-copy initialization versus storage-preserving steady copy for depthtex1/depthtex2 and the Phase 8 shadow split; destinations are owned, never ordinary foreign or borrowed, and Phase 5 owns the copy tier/cadence and restoration policy |
| `TextureService.prepareUnitBindings(int occupiedUnitMask)` | D-P5-39: after complete successful physical-binding preflight only, before every BoundObject bind loop; no call on Rejected/Degraded. Failure returns BackendFailed without lease transfer; P1/P14 normalize Unused native samplers |
| Runtime sampler demotion | P14 D-P14-26/P1 D-P1-62 require complete authenticated owner-parameter replay before NONE admission, otherwise existing stale/off/rebuild containment. P5 supplies its latest complete policy, never mutates borrowed objects or converts failed replay into successful resize/retirement |
| `GLCapabilityProfile` and serialization — D-P1-66 | receive max3DTextureSize/maxRectangleTextureSize immediately after maxTextureSize and required keys max.3DTextureSize/max.rectangleTextureSize; 1D/2D ordinary maximum, all3D axes own maximum, RECT width/height own maximum. Gate-false0 only, gate-true positive captured maxima; no guessed limits or unsupported probes. Main/depth remain2D. Recorder consumes the same complete profile |
| opaque handle lifetime | candidate ledger, generation/stale-view rules, re-acquisition |
| `ForeignTextureProvider` and authenticated borrowed-depth permission matrix | ordinary foreign values remain bind-and-label-only and are never attached, copied into, allocated, or deleted; authenticated borrowed depth is additionally sampleable and legal only at depth/depth-stencil attachment operations; neither foreign class is Phase-5-owned |
| `RecordingGLDevice`, `ScriptedResponses`, `GLCallLog`, `ReplayAssertions` | fallback and state-machine call sequence, including distinct `framebuffers.attachDepth`, `framebuffers.attachDepthStencil`, `framebuffers.initializeDepthTextureFromFramebuffer`, and steady `framebuffers.copyDepthToTexture` events; forged/wrong-origin rejection records no event; borrowed handles are not leak-counted or deletable |
| D-P1-67 recorder | exact framebuffers.clearColorAttachment(f,index,typedValue) plus framebuffers.clearColorAttachment.restore(f,restored); work/restore glError scripts separate, preflight rejects append neither, attempted failed writes remain recorded and false restoration poisons admission |
| diagnostic/log conventions | `schmaloogium.buffers`, `.gl`; chat/GUI/log routing |

No Phase 1 operation is widened silently. Section 5.5 records the verified upstream grant and its
literal-PASS review.

### 5.3 Consumed Phase 3 contracts

| Phase 3 §5 contract | Use |
|---|---|
| `PackConfiguration`, schema/fingerprint | sole pack/config truth; accept exactly schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION (23 under D-P5-44), including equal nested IdMappingInput/inspection; reject every other version before derivation without upgrades |
| `ProgramStateModel` | explicit flip values retained through Phase 4 |
| `ResourceRequirements` | color/depth/shadow counts, formats, clear policy, routing minima and shadow policy; no engine supersampling requirement |
| `MacroConfiguration` | no parsing; only configuration fingerprint participates |
| immutable publication/version discipline | no pack reopen/rescan or retained parser builders |

**Schema23 receiver receipt — D-P5-44.** Current containing/nested/received inspection
must equal CURRENT_SCHEMA_VERSION=23 and MaterializedSource-v23 before planning,
derivation, acquisition, retention or reuse. All earlier numeric receipts, including
D-P5-40 below, are historical. P3 owns range-capable selector parsing; P5 consumes typed
configuration, never reconstructs selectors or resolves registries. Nine metadata-only
trees/projectionVersion1 and assets/native/options/parameter domains are unchanged.

**Current registry identity receipt — D-P5-48.** P4 D-P4-43 now issues opaque
`RegistryFingerprint/profile-selection-v3`, committing exact explicit profile-selection
intent and evaluated state. Carry and compare this identity at existing request/view/
estate/publication checks; no local codec, inferred selection or compatible-old-domain
alias. The prior positional-route-v2 receipt below is historical. Profile-only identity
changes invalidate prior estate pairing under the existing generation protocol.

**Registry prelude receiver — D-P5-45.** Receive P4 D-P4-40:
`SparseArray(int highestLegalIndex,int highestPopulatedIndex,Optional<ProgramSlotId> virtualPrelude)`
contains at most exact deferred_pre/composite_pre in its respective single step. Plan from
the same candidate StageRegistry: consume the exact contained virtual descriptor unchanged
at applyVirtualTransition, then indexed raster descriptors ascending. No source/index/
fallback/compute/read/write/mipmap payload on pre, only flips; no synthesized pre pass or
separate step. Pre/0/5/99, pre-only, omitted-pre and illegal-member cases are planned, not run.

**Historical schema22 receiver receipt — D-P5-40, superseded by D-P5-44.** P3 D-P3-72 supplied CURRENT_SCHEMA_VERSION=22
and MaterializedSource-v22. Equal containing/nested/inspection versions precede planning,
retention, reuse and evidence. Isolated BLOCK alternate data remains P3/P9-owned; P5 does
not select its era. Same-load assets, nine trees/projectionVersion1 and resource semantics
remain unchanged. All older numeric/hash-domain receipts below are historical; use opaque
current P3/P4 identities, never reconstruct or admit an older domain.

**Historical schema21 receiver receipt — D-P5-29, 2026-09-08 (unverified).** Adopt P3
§0.63/D-P3-70/§5.3. Containing configuration and nested `IdMappingInput` must each equal
`PackFrontEnd.CURRENT_SCHEMA_VERSION` and each other before planning, creation, derivation,
retention, reuse or inspection; any received `PackDecisionSnapshot` must match that same
current schema and configuration identity before enrichment. Reject stale/future/mismatched
data, never relabel it, erase old profile fields or synthesize defaults/empty components.
The dated schema20/19 receipts below are historical, not competing admission gates.
Carry current configuration identity unchanged; P3 `MaterializedSource-v21` invalidates old
materialization-derived identity, but P5 gains no materializer or source parser. P4 D-P4-31's
`RegistryFingerprint/own-build-v1` is opaque estate/pairing identity carried through the existing
comparison rules, not parsed, repaired or accepted under an old cache domain. No new registry
getter or resolution-evidence requirement is imposed. Sizing, depth, flips, shadow semantics,
actual provider/lease/publication lifetimes and D-P3-69 same-load assets/nine source-free trees
remain unchanged. No binary authority, timing API, implementation clearance or fresh PASS.

**Schema20 receiver receipt — D-P5-28, 2026-09-08 (unverified).** P3
§0.62/D-P3-69/§5 supersedes earlier current-schema assertions; the schema19 receipt below
records historical adoption, not current admission. Require containing/nested schema20
before planning, derivation, reuse or inspection. Required non-null `assets` immediately
after `sources` is the exact same-load P3 capability, paired with the exact containing
`PackIdentity`; foreign-load pairing is invalid even under structural equality.
Carry the owner's metadata-derived configuration fingerprint unchanged through existing
planning and same-request inspection identity. No binary acquisition, payload parser, dummy
field, fabricated empty manifest, reconstructed capability or old-schema/resource-epoch
upgrade is granted. P13 owns byte interpretation and optional owned-sidecar-only recovery;
other P3 safety/bounds/index/container/source/configuration failures remain fatal.
Sizing, depth, shadows, flips, fixed-unit binding, parameters and pure resource projection
retain their existing semantics. Source-free inspection retains P3's actual ninth canonical
assets metadata section, not bytes/cursors/providers; `projectionVersion=1` and the original
eight section meanings remain unchanged, with digest strings following TextHash.
Fresh owner/receiver whole-document reviews and IR-01 remain; no prior PASS covers this edit.

Runtime display/render/shadow quality values remain `BufferRuntimeInputs` supplied by Phase 7.
The adapter uses decoded current Phase 3 `renderResMul`/`shadowResMul`; display extent remains
platform-owned. These three values are the complete runtime planning identity. No legacy GUI
key alias or separate revision is inferred. Engine AA runtime/UI are excluded by the recorded
§11.6 disposition; `MC_FXAA_LEVEL` is absent, not emitted as zero. `superSamplingLevel` remains
a distinct pack option, not an AA setting, a resource requirement or `renderResMul`.

**Schema19 receiver receipt — 2026-09-07:** adopt P3 D-P3-68/§0.61 and §11 migration
for the containing configuration and nested IDs. Sizing/depth/shadow/resource semantics,
pure planning and same-request inspection are unchanged; P5 neither parses native source
nor derives draw topology. Previous schema18 receipts remain historical, not reuse authority.
Fresh whole-document producer/receiver reviews and IR-01 remain; no implementation evidence.

**P13 parameter receipt — 2026-09-07:** adopt P13 D-P13-27/§5 incorporated §4.3.5/§4.5.1:
`TextureParameterSpec(MinFilter minFilter,MagFilter magFilter,WrapMode wrap)` and
`TextureParameterFingerprint(String value)` retain exact types; identity now uses
`phase13.parameters/v2`, not relabelled v1. P13 owns PNG NEAREST/REPEAT, raw
LINEAR/CLAMP_TO_EDGE and noise override LINEAR/REPEAT baselines, omission preservation,
explicit Boolean overrides and atomic malformed/unreadable baseline recovery with one warning
and deterministic outcome digest. P5 consumes validated effective parameters/outcomes only;
no independent defaults, source/sidecar parse, legality coercion or foreign mutation.
Invalid target/format state is source-local unavailable evidence; only compatible fallback
may bind, otherwise required backing suppresses the program. Logical-buffer mipmaps never
overwrite custom/foreign replacement parameters. P13 §6 closes the complete failure-code
domain, including PARAMETERIZATION_UNSUPPORTED. **P1 mapping receipt — 2026-09-08:**
adopt §§4.7.7/5/D-P1-52's complete TextureParameters shape and native-state contract.
P5 maps its existing owner filters/wraps exactly, neutral compare NONE/LEQUAL except shadow
hardware filtering REF_TO_TEXTURE/LEQUAL, zero border, LOD -1000/+1000, bias0, anisotropy1,
base0 and admitted planned final mip. Owned depth/depth-stencil use LEGACY_DEPTH_LUMINANCE
(R,R,R,1), color IDENTITY; foreign/borrowed handles are never passed to setParameters.
Before generation admit the intended final level; never sample a mip-filtered incomplete chain.
Keep existing generation/filter-restoration moments and custom/foreign override exclusions.
Full prevalidation, errors/drains, binding restoration and recorder semantics are incorporated.
Mandatory synchronous mapping is adopted/unverified, separate from optional P14 optimization.


#### 5.3.1 Coordinated producer and downstream migration requests

Phase13 produces §2.4's exact candidate/snapshot/lease values. Phase7 uses its active non-owning
`TexturePublication.id` and the **same** Phase4 selector in
`TextureLeaseSource.lease(TextureOverlayPublicationId expected,ProgramBindingSelection selection,AtlasBindingEvidence baseBinding)`
→ `TextureLeaseResult.Acquired(TextureOverlayLease lease)` or
`Rejected(TextureLeaseRejection reason)`. Reasons are PUBLICATION_UNAVAILABLE,
PUBLICATION_ID_MISMATCH, REGISTRY_FINGERPRINT_MISMATCH, STALE_SELECTION, INVALID_BASE_BINDING,
STALE_BASE_BINDING. Acquisition validates active owner/ID/selection then issuer/thread/composition
and latest serial/resource epoch before incrementing count. No old overload or racy id lookup.

**Phase 8 R7-12 — adopted/unverified owner and receiver contract:** the owner appends `ProgramBindingSelection selection`,
`BarrierContext activationContext`, `TexturePublication texturePublication`, `TextureLeaseSource textureLeases`
to `ShadowInvocationContext`. Phase7 selects root shadow once before invoking its slot and retains
bridge ownership. Phase8 §4.2 steps3–13 and R8-2 must consume that exact selector/context,
`beginPass(frameId,pass,selection)`, acquire the expected full overlay lease, and invoke
`shadowBindings(long generation,long frameId,ShadowPassSnapshot snapshot,TextureOverlayLease overlay,
TextureOverlayPublicationId expectedOverlay)` with full sixteen-row preflight and physical binds
before same-selection activation/sampler upload. Handle Bound/Degraded/Rejected/BackendFailed;
after Bound close only binding in finally, otherwise close only acquired lease. Suppression aborts
the undrawn shadow pass without flips; backend failure follows existing neutralization containment.
Traversal/camera/copied-depth/mipmap/bridge rules remain Phase8/7-owned. Adoption is present;
until fresh owner PASS and remaining gates close, real shadow remains NotInstalled/unavailable.

**Phase 8 R7-13 — adopted/unverified owner and receiver contract:** remove registry from `ShadowPlanInput`, leaving
`ShadowPolicy policy,ShadowHookHealth hookHealth,boolean requested` under P8 D-P8-26;
requested is the same accepted P3 shadow-minima OR used here. Add `RegistryFingerprint registry` immediately
after `ShadowPlan plan` in `ShadowPassFactory.create`. The final publication/build checks include
that registry while planning is independent. No old registry fingerprint breaks the cycle.

**Phase 6 R7-11 — adopted/unverified owner and receiver contract:**
`UniformRetirementResult retire(UniformRetirementReason reason)` accepts
UNPUBLISHED_ABORT/REPLACEMENT/SHUTDOWN and returns Retired/AlreadyRetired/
Rejected(WRONG_THREAD|ACTIVE_CALLBACK). It performs no GL/barrier operation and permanently
disables events/participants/adoption. Final callbacks and scope-restoration precede retirement;
borrowed services stay alive until Retired/AlreadyRetired. Rejected retains ownership/admission
closed. Candidate abort needs no publication; replacement follows actual old-barrier invalidation;
shutdown precedes Phase 4 atomic teardown. No reset(CLOSE) alias exists.

**Phase 3/13 R1 — owner-designed/unverified, receiver-adopted/unverified:**
Phase 3's required `CompanionOptionMacros(boolean normalMap,boolean specularMap)` immediately
after engineOptions in `PackLoadRequest` enters every same-build option macro/materialization/
fingerprint. Phase 7 obtains preliminary Phase 13 demand before load/jcpp, including independent
decoded normal/specular user preferences, never linked-program demand. Missing non-Off typed
input is INVALID_REQUEST; Off short-circuits. No unchanged-load fallback, undefined-macro
substitution, overloaded MacroContribution or downstream source patch is allowed.
Phase 3 also publishes lossless `textureDeclarations` alongside executable texture specs.
The 2026-09-07 correction in `docs/decisions/U1_TEXTURE_SAMPLING.md` removes the unspecified
filter/wrap key-suffix requirement; retain numeric discriminators, lossless unknown declarations
and P13-owned sidecars. No downstream key parser or new typed suffix API is required.
Phase 1 package grants exist but are unverified; PBR and shared-unit consumption still require
fresh current-owner reviews. Phase 13 R4 post-analysis allocation is separate from macro policy.

### 5.4 Consumed Phase 4 contracts

| Phase 4 §5 contract | Use |
|---|---|
| `StageRegistry`, `StageStep`, `PassDescriptor` | deterministic pass-plan traversal; exact typed virtual `deferred_pre`/`composite_pre` descriptors are passed unchanged to `applyVirtualTransition` |
| `PassResourceAccess` | readable, exact/symbolic writes, explicit logical flips, mipmap declarations; virtual descriptors supply flips without a program |
| `ProgramStateBundle` | routing, effective provider flip/scale/state data |
| `ProgramBindingSelection`, `ProgramBindingSelections.validateSelection`, `ProgramSelectionValidation`, `ResolvedProgramDescriptor.samplerLayout`, `FixedSamplerLayoutPolicy` | exact §§2.4/4.12 consumed Phase4 credential/layout/callback contracts, published at `docs/phase4/v1/PHASE_4_DOC.md:1794`–`:1800`; selection is issued once with force-shadow before fallback, and activation uses its retained private binding; no descriptor-only authentication or fallback re-resolution |
| `ProgramRegistryView.samplerPolicyFingerprint()` | pure policy pairing even for empty registries; planning/create compare the sole Phase5 policy before GL |
| `DrawRoutingSlot.Attachment/None`, P1 `FramebufferDrawSlot.Attachment/None` | D-P4-33/P1-57 adopted: preserve positional holes; pack actual attachments only, typed full-route identity and capacity validation in §4.5 |
| `CompiledRegistryCandidate.view()` | non-owning, generationless, pre-publication `ProgramRegistryView` used by Phases 5 and 7 to derive and validate the buffer candidate. It is an immutable detached metadata snapshot that remains safe after candidate close, rejection/recovery, or accepted transfer, but is never live publication state; candidate ownership, opacity, and compiler provenance remain unchanged |
| `RegistryFingerprint` | estate derivation identity/publication pairing |
| generation/stale-view discipline | Phase 7 orchestration and invalidation |

Phase 5 never reaches a `ProgramHandle`, mutates registry state, or writes a physical side into a
Phase 4 type.

### 5.5 Requested changes to dependency contracts

**GRANTED, unverified — D-P5-46 / P1 D-P1-67:** mandatory synchronous
`FramebufferService.clearColorAttachment(FramebufferHandle,int,ColorClearValue)` with
exact three record variants, route/class/capability admission, GL3 and GL2 EXT_integer
native paths and complete state/error/recorder semantics in P1 §4.7.4b. This is the
R43 integer-clear request and actual P1 receipt, not an assumed StateService overload
or optional P14 API. P1 D-P1-66 also supplies actual target maxima to §4.2/§5.2.

1. **GRANTED — Phase 1 framebuffer/depth contract.** Phase 1 §0.18/§0.19 and binding §5 now expose
   the backend-authenticated `BorrowedDepthAttachmentHandle`, its
   `borrowDepthAttachment(platformTexture)` issuance route, the ordinary-versus-borrowed
   permission matrix, `attachDepthStencil`, and
   `initializeDepthTextureFromFramebuffer`, with distinct recorder/replay operations
   (`docs/phase1/v14/PHASE_1_DOC.md:5507`–`:5513`). The whole document retains the detailed
   owned/borrowed validation, forged/wrong-origin rejection, combined depth/stencil behavior, and
   first-versus-steady copy semantics while assigning Phase 5 the downstream format, freshness,
   cadence, tier, restoration-policy, and Minecraft-lifetime decisions. The literal PASS in
   `docs/phase1/reviews/PHASE_1_REVIEW_20.md:61`–`:75` closes that dependency change.
2. **GRANTED — Phase 4 candidate-view binding.** Phase 4 §5 explicitly publishes
   `CompiledRegistryCandidate.view()` as the non-owning, immutable detached pre-publication
   `ProgramRegistryView` for Phases 5 and 7, without a generation, close operation, handle,
   private-registry accessor, or provenance credential
   (`docs/phase4/v1/PHASE_4_DOC.md:2143`). Candidate ownership, opacity, and
   compiler/publication provenance remain with the opaque candidate. The literal PASS in
   `docs/phase4/reviews/PHASE_4_REVIEW_15.md:59`–`:72` closes that binding clarification.
3. **COORDINATED LOCALLY — Phase 4 virtual-transition input.** Phase 4 §0.22/§5 binds its existing
   `deferred_pre`/`composite_pre` `PassDescriptor` as the exact programless typed input. Phase 5
   consumes that value through `applyVirtualTransition`; no resolved program is requested. The
   coordinated call remains implementation-gated until Phase 4's amended owner surface receives
   its fresh literal-PASS review.

4. **ADOPTED/UNVERIFIED — Phase 6 R7-10 consumer cutover.** Inject `FixedSamplerResolver` immediately after
   configuration in `UniformRuntimeFactory.create`; call its exact §2.4 resolve operation inside
   the unchanged sampler participant using the effective descriptor's samplerLayout and context.
   Preserve afterBind, exact-name locations, fixed integer ordering, caches and activity tokens.
   Owner and receiver contracts agree; fresh owner reviews still gate implementation.
   Requested full factory signature: `create(long initialRegistryGeneration,
   UniformConfiguration configuration,FixedSamplerResolver samplerResolver,
   UniformPlatformProvider platform,CenterDepthSource centerDepth,GLDevice gl,
   DiagnosticReporter diagnostics,UniformReplayErrorSink replayErrors) -> UniformBuildResult`.
   **Dated receiving receipt — 2026-09-08, R25-1, unverified:** the required final replay
   observer is supplied/owned by P7 and borrowed by P6 through successful retirement; no
   old overload/default/no-op remains. P5 receives only this full factory arity correction,
   not sink ownership, replay attribution, capture storage or escalation policy. Its fixed
   resolver injection, exact layout/context and physical binding responsibilities are unchanged.
5. **ADOPTED/UNVERIFIED — coherent lifecycle and shadow consumption.** Phase 6 grants R7-11
   retirement; Phase 8 grants R7-12 selector/texture-aware invocation and R7-13 registry-independent
   planning. §5.3.1 consumes their exact current shapes; old CLOSE/four-row models are removed.

Phase 3 R1 companion input/direct Phase 4 projections/lossless declarations and Phase 1 package/
legacy-configure grants are present, not freshly verified. Adopt
`docs/decisions/U1_TEXTURE_SAMPLING.md` for U1's narrow scope correction, not a binding-policy
change. jcpp permission, native legacy source preservation and fresh Phase 3/4/5/6/7/8/13
verification remain separate gates.
Phase13 R2's former narrow-name shortfall is architecturally superseded by this full-domain seam,
not an active legal-input fallback. Fresh verification and dependent adoption still gate implementation.

**Optional R-P14→P5-2 received, ungranted:** Phase 14 requests a Phase-5-internal complete-preflight
sampler batch boundary, applying complete sampler state before Bound while retaining zero-bind
Rejected/Degraded, late BackendFailed containment and Bound-only lease transfer. H-P14→P5-1 remains
an optimization handoff, not permission to bypass physical-binding ownership. Exact batch/result/
cleanup operations require a separate P5 owner amendment and review; MULTI_BIND stays disabled,
with the existing per-unit binding and NONE/sampler-0 baseline until adopted.

## 6. Failure modes & degradation

| Failure | Required behavior | G2.4 rung |
|---|---|---:|
| malformed/null request, stale schema/fingerprint, invalid route | return closed failure before GL; keep/publish shaders off | 4/5 |
| requested color formats produce allocation/FBO failure | delete whole attempt, retry all colors as `RGBA_COMPAT`, user-visible warning | 2a |
| RGBA fallback also fails | delete partial estate; publish shaders off; vanilla path remains | 4→5 |
| main depth unavailable or backend authentication/issuance fails | mark any published estate stale, advance its attachment epoch, abort/normalize any active frame, and publish shaders off; do not create a false depthtex0; require successful safe-point replacement before another shader draw | 4 |
| prepared main-depth extent differs from planned main extent | attach nothing; remain on vanilla path and repeat safe-point prepare/build, with bounded retry diagnostics | 4/5 containment |
| same-extent main-depth version changes mid-frame | successful reattachment invalidates the open snapshot; abandon it and reacquire pass/binding snapshots before continuing; failure follows the shaders-off recovery above | 5 containment |
| main-depth reattachment/check fails | retain the advanced epoch and prior cached identity, mark the partially mutated estate stale/unusable, abort/normalize any active frame, publish shaders off, and require safe-point replacement before another shader draw | 5 containment |
| one depthtex1/2 copy fails | bind current depthtex0 as non-stale fallback for that unit, disable copied-depth feature view, warn | 2a |
| combined stencil has no legal copy tier | copied-depth feature unavailable; pack requiring the split may gate off, otherwise bind depthtex0 fallback | 2a/4 |
| sfb creation fails while dfb is healthy | disable shadow estate only; Phase 8 skips; bind neutral shadow objects | 2a |
| one requested shadow mipmap generation fails | return `Degraded` for that logical buffer after restoring its base min filter; continue later buffers and keep the shadow pass active | 2a |
| restoring the base min filter after shadow mipmap failure also fails | stop later buffers; return `Neutralized` after atomically aborting the open snapshot without flips, invalidating shadow snapshots, and installing neutral units; Phase 8 stops the pass and generation checks | 2a |
| shadow bind/clear/copy or pass-wide backend failure is containable | Phase 8 aborts if still open, then calls generation-checked `degradeToNeutral`; atomically invalidate old snapshots, expose neutral units 4/5/13/14, and keep the main estate/pipeline active | 2a |
| stale caller attempts shadow neutralization | `Rejected(STALE_GENERATION)` before GL or mutation; never let an old slot disable a newer estate | protocol |
| clear call fails | abort shader frame, mark full clear; recurring or unrecoverable failure publishes off | 2a→4 |
| missing required candidate/noise/companion/backing | Degraded with exact selection, ordered closed diagnostics and SUPPRESS_DRAW; no binds, uploads or draw; caller closes lease and discards/aborts without flips. A compatible base fallback may instead return Bound with warning | 2a/3 |
| conflicting sampler shapes or distinct alias candidates | Phase4 candidate-local pre-GL failure normally selects fallback; Phase5 defensive metadata preflight returns Degraded with zero binds; never re-resolve after selection | 3 |
| stale selector/estate/pass/lease or identity mismatch | first applicable §4.12.3 Rejected, mutation-free and no lease transfer; suppress selected draw and close caller lease | protocol |
| late texture bind backend failure | BackendFailed may follow partial GL; no ownership transfer/activation/upload/draw, caller closes lease and follows main-frame or shadow containment | 4/5 |
| texture build/identity/registration failure after registry/estate acceptance | compensate Phase5 and Phase4 off; no ActivePipeline/draw; accepted textures retire through owner, caller-owned candidates close; ConsumerFailed deliveredCount unchanged | 4/5 |
| pass acquisition rejection | no GL work; correct ordering for no/open-frame rejection, or reacquire the current publication for stale generation/epoch | local protocol failure |
| snapshot write preparation/base-filter reset or binding restoration fails | return PassSnapshotResult.Failed(failure,diagnosticId,true), including contained backend exceptions; expose no snapshot, consume frame/invalidate snapshots, normalize without new flip, mark stale/full-clear and require shaders-off plus safe replacement. P7 stops ordinary/nested/fullscreen work, skips all later P5 lifecycle calls even in outer finally, and closes independent binding/lease/local/P4 scopes | 4/5 |
| pass completion rejection | no GL work or flip mutation; abort the frame, then correct ordering or reacquire the current publication according to the rejection | local protocol failure |
| virtual-transition rejection | no flip or GL mutation; abort on stale/wrong-frame/protocol ordering, and fix the caller for an invalid/duplicate descriptor | local protocol failure |
| draw-buffers-none open/restore backend failure | consume the lease if issued, best-effort normalize/abort, require full clear, mark the estate stale, and publish shaders off before another shader draw | 4→5 |
| resource projection unavailable | serialize only `resources.available=false`; never reuse the prior estate or infer a partial block. A complete capability shortfall remains an available projection and takes the ordinary shaders-off capability path | evidence protocol |
| resize consumer fails after install | stop dispatch, publish next-generation off, close unopened estate, return `ConsumerFailed`; Phase 7 retains registry recovery policy | 4/5 containment |
| delete/close reports backend error | continue reverse ledger cleanup, aggregate diagnostics, quarantine handles, never throw into client | 5 |
| unexpected backend exception | catch at public boundary, clean partial ownership, publish off/vanilla | 5 |

No buffer failure throws through the frame driver. A failure never deletes a foreign or borrowed
texture.

## 7. Threading & performance notes

### 7.1 Thread ownership

- `BufferPlanner` is pure and may run off-thread over immutable values.
- Candidate GL creation, publication, frame begin/end, pass snapshots, clears, copies, resize,
  reattachment, and close are render-thread-only.
- `MainDepthSource.current()` is render-thread-only.
- Published views are immutable references, but their mutating operations enforce the render thread;
  thread-safe publication does not make GL methods cross-thread.
- Shadow bind/lease/mipmap/neutralization is render-thread-only. Binding use expires with its pass;
  the owner still closes the transferred snapshot in finally after invalidation.
- No chunk-build worker state enters this phase.

### 7.2 Allocation posture

Cold layout validation, source preparation, digesting and candidate indexing reuse immutable ordered
lists and bounded unit indexes. The draw path performs no parsing, byte hashing, free-unit scan,
incidental per-row copying or boxing. Fresh immutable selector/lease/snapshot lifecycle objects are
permitted; no false zero-allocation promise is made. No steady-frame texture/FBO allocation occurs.

Hot-path operations are:

- compare cached attachment handles and reattach only when orientation/depth version changed;
- bind one pass FBO;
- bind fixed unit rows from an immutable snapshot; and
- toggle primitive flip bits after successful passes.

Clear grouping and FBO-key construction happen at candidate build or resize, not per clear call.
No 2^N FBO variant cache exists.

### 7.3 Driver interaction

- All GL state cached by `GlStateManager` is changed through Phase 1's backend discipline.
- `copyDepthToTexture` and blit tiers restore framebuffer/texture bindings.
- FBO completeness is checked at construction/reattachment, not every frame.
- Integer targets use NEAREST to avoid invalid interpolation.
- `DebugService` labels are present from v0.1 and activate only at Phase 14.
- Frame-end metadata rebase eliminates Iris-style copy-back bandwidth.
- Optimization never changes flip, clear, format, fixed-unit, or fallback semantics.

## 8. Testability plan

### 8.1 Pure state-machine tests

Planned D-P5-49 checks: each of the three §4.7 triggers independently permits exactly one
whole-color RGBA retry; absent integer-clear tier permits zero allocations/retries; successful
retry converts clear payloads using realized formats. Rasterizer-discard initially enabled on
each legal tier still clears, then restores; unsupported tier makes no illegal query; disable/
clear/restore failure never consumes full-clear. These are architecture cases, not executed proof.

1. Exhaustively test the four `committedMain × flipped` side rows.
2. Property-test up to eight buffers across arbitrary write sets, explicit true/false overrides,
   virtual pre flips, skipped passes, success completion, commit, and abort.
3. Prove FBO output attachments and sampler reads come from the same snapshot.
4. Prove `commitFrame` preserves the latest physical texture and starts the next frame unflipped
   without a copy operation.
5. Run the same properties over shadowcolor identities and assert a local no-op flip/hardcoded-main
   implementation is impossible; historical B4 attribution is qualified by D-P5-35, not pin behavior.
6. Test §4.6/D-P5-46 ordinary/full side matrices, fog alpha1, explicit replacement and
   clear-disable override across mixed RGBA8/RGBA32F/R32I/R32UI attachments. Defend
   fractional truncation, signed/unsigned saturation boundaries, negative unsigned,
   finite floating-format clamping, NaN/infinity rejection, typed grouping and route holes.
   GL3 and GL2+EXT paths must clear only the intended attachment under nonidentity routing.
   Begin with nondefault read/draw FBOs, routes, viewport/scissor, indexed masks,
   dither/sRGB/clamp and integer-origin legacy clear state; prove exact restoration.
   Mandatory full-clear consumes only after all attachments and restoration succeed;
   injected first/middle/final clear or inner/outer restore failure retains it without flip.
   Unsupported integer tier rejects preallocation without RGBA downgrade.
7. Table-test every applicable main-pass acquisition/completion rejection, including foreign,
   duplicate, and consumed snapshots; assert the exact reason, zero GL calls, and unchanged open
   frame/pass and flip state, and prove acquisition never returns `WRONG_FRAME_ID`.
8. Table-test every shadow acquisition, bind, clear, copy, completion, and abort result, including
   `PASS_ALREADY_OPEN` on a second begin: exact validation rejection is pre-GL and mutation-free;
   scripted bind/clear/copy backend failure keeps the token open for abort; only completion flips;
   abort closes and requires full clear.
9. Table-test `shadowBindings`, `generateShadowMipmaps`, and `degradeToNeutral`: generation wins
   over frame/snapshot rejection, frame wins over snapshot rejection, every rejected call is
   mutation-free, and neutralization invalidates all old pass/binding snapshots without a flip.
10. Table-test `applyVirtualTransition`: exact two descriptors, altered/raster rejection, generation/
    epoch/frame/open-pass/open-overlay priority, true/false/absent flips, canonical returned order,
    once-per-frame consumption, and proof that no program resolution or GL call occurs.
11. Model-check the draw-buffers-none lease across open/close/abort/commit: no nesting, exact prior
    selection restoration, idempotent close, stale/foreign/wrong-frame rejection, commit rejection
    while open, abort consumption, and no leaked NONE selection after every backend-failure branch.

### 8.2 Contract-table tests

- `formatTable_hasExactly37PackFacingNames`;
- `formatTable_integerUsesIntegerPixelFormatAndNearest`;
- `formatTable_allPixelFormatsAndTypesRecognized`;
- `unitMap_exactAppB3Rows`;
- `unitMap_depthtex1Is11AndDepthtex2CompositeIs12`;
- `unitMap_noDynamicAllocation`;
- `drawPrefixes_phase3CanonicalIndicesOnly`;
- `inventory_minFourScanDrivenNotSixteen`;
- `growth_nonNegativeIdentityNoHardcodedModelCap`;
- `shadowEstate_plannedForIndependentDepthOrColorDemand`; and
- `packSamplingOption_doesNotChangeEngineSizing` (different selected values preserved in P3
  materialized source, equal otherwise-identical P5 extents; no sample-count resource);
- `depthCopy_worldOrderWeatherThenTranslucent`;
- `resourceProjection_exactCanonicalFullAndAbsentGrammar`;
- `resourceProjection_capabilityShortfallRemainsAvailable`;
- `resourceProjection_equalDeclarativeFieldsButRealizedFallbackDiffersFromPlan`; and
- `resourceProjection_rejectsOrderCardinalityAndNonFiniteValues`.
- DefaultRgba baseline versus explicit RGBA8 versus conditional RGBA32F; truthful all-row fallback
  provenance; dynamic fog policy has no scalar RGBA until ClearRequest execution.
- Weather BackendDegraded then translucent Copied is legal; repeated failed weather is
  DuplicateIgnored without GL, and no success is fabricated for degraded destination.
- Main mipmaps: frozen side/effective provider, empty set, stale/duplicate snapshot, unsupported
  capability, generation and filter failure at every position, continuation only after base restore,
  already-aborted Failed, and write/clear/frame/reallocation reset preventing stale freshness.
- Snapshot preparation: enable mipmaps on colortex0 in a deferred pass with automatic flip disabled,
  then fail the next translucent gbuffers write-side base-filter reset. Expect Failed(...,true),
  no exposed snapshot/draw/new flip, consumed frame, invalidated snapshots, stale/full-clear/off.
  Repeat the failure edge for ordinary acquisition, nested child/pop reacquisition and fullscreen
  acquisition before mipmaps; outer finish/finally must never complete/discard/commit/abort again
  or resume a parent. Independent binding/lease/P4/local-state cleanup runs exactly once even if
  restoration also fails. Pure rejection leaves token/flips unchanged and success exposes only
  fully prepared snapshots. These are planned architectural acceptance cases, not executed tests.
- Neutral creation failure/runtime neutralization with shadowtex0 comparison and shadowtex1
  ordinary sampling (and distinct full policies) resolves compatible units without P14 or per-frame
  allocation; cleanup deletes every bounded backing once. All cases planned, not executed.

### 8.3 Recorded-GL tests

Using Phase 1 `RecordingGLDevice` and serialized profiles:

1. create/destroy a classic four-buffer dfb and assert no leaks/use-after-delete;
2. create an eight-buffer estate and assert both sides, filters, wrap, and route attachments;
3. script one requested-format FBO failure and prove the entire retry is `RGBA_COMPAT`;
4. script retry failure and prove every partial handle is deleted and off is published;
5. call `refreshMainDepth` after same-extent identity-only and format-changing versions; prove every
   depth-using FBO reattaches, both copy targets are reallocated in the new format and their
   destination FBOs reattach, old targets are deleted only after all checks, both new targets are
   uninitialized, full clear is forced, and `Reattached` reports the advanced attachment epoch;
   inject allocation, destination-attachment, and completeness failures and prove new partial
   targets are deleted while the poisoned estate follows `Failed(BufferFailure)` recovery; perform
   refresh after pass/binding snapshot creation and before draw, abandon the invalidated pass,
   reacquire both snapshots, and prove the same frame may then draw;
6. call `refreshMainDepth` after a main-depth extent change and prove `ResizeRequired` carries
   `MAIN_DEPTH_RESIZE_REQUIRED`, records zero GL calls and zero estate/frame/epoch mutation, and no
   shader draw occurs before Phase 7 abort/normalize plus prepare/build/publication;
7. return unavailable current depth before `beginFrame` and mid-frame, and inject failures at the
   first, a middle, and final reattachment/check step; prove epoch advance, retained prior identity,
   stale estate, no partial-FBO draw, abort/normalize when active, shaders-off publication, and the
   draw bar until replacement;
8. distinguish verified `attachDepth` from `attachDepthStencil`, including authenticated borrowed
   acceptance and forged/wrong-origin/ordinary-foreign rejection before any recorded event;
9. exercise the verified, distinct first-copy and steady-copy protocol events;
10. prove candidate inspection exposes metadata but no generation or estate operation; publish it,
    then test immutable accepted generation plus resize/rebuild generation inequality and exact
    candidate-to-accepted-estate REALIZED resource-projection equality. Compare equal-input
    PLANNED values separately; cross-stage comparison is declarative-fields-only, including
    requested formats retained through truthful all-color RGBA fallback allocation evidence.
10a. open/close the draw-buffers-none lease and assert the recorded selection is NONE only inside
    the scope and the exact prior selection is restored; inject open and restore failures and prove
    stale/off recovery with no continued shader draw;
11. register current and retained historical ready generations with matching sizing; reject blank
    and duplicate-live IDs, future generations, off/never-installed unknown generations, and known
    generations with mismatched sizing; assert each exact reason and zero registration,
    acknowledgement, publication, or generation mutation;
12. fail the first, each middle, and last resize-consumer position by returned `FAILED` and throw;
    assert `deliveredCount` is respectively zero or the exact number of preceding `SUCCESS`
    acknowledgements, and prove consecutive ready/off generations, unopened-estate disposal, no
    later callback, and retry convergence from each consumer's distinct acknowledged sizing baseline;
13. prove borrowed/foreign handles are never deleted or used in forbidden verbs;
14. exercise every fixed name, candidate compatibility and typed binding result under §8.5;
    assert sixteen ascending BoundObject/Unused rows on success, no drawable missing row,
    preflight rejection/degradation with zero binds, backend failure distinct from preflight,
    and Bound-only lease transfer with exactly-once finally closure;
15. build depth-only and color-only shadow estates; prove each has a present `shadowExtent`, the
    color-only estate has a complete sfb with its owned physical shadowtex0 attachment without
    changing the reported Phase 3 depth minimum, and only the both-zero case returns
    `ShadowEstateNotRequested`; every shadow binding snapshot has sixteen ascending rows,
    selector-authenticated real/neutral/custom selection, closeable transferred lifetime, and
    expiry without automatic closure on complete/abort/invalidate; script a
    mipmap failure at every requested position and prove base-filter restoration plus continuation;
    fail restoration and prove result-level neutralization, no later generation, snapshot
    invalidation, stable diagnostic propagation, neutral bindings, and no later lifecycle call;
    neutralize with and without an open snapshot and prove idempotence, stable diagnostic identity,
    coherent later main/shadow bindings, and no premature deletion; and
16. create/destroy the full classic estate for the implementation gate with
    `ReplayAssertions.noLeakedObjects()` and `noUseAfterDelete()`.

The tier selector receives scripted function-pointer/blit/combined-stencil booleans. The actual
LWJGL function-pointer query receives one render-thread integration test on a live context.

### 8.4 Conformance tiers

- **T0:** all matrix packs build a valid buffer plan or a named graceful capability failure.
- **T1:** static and camera-path scenes exercise clear/flip/depth-copy continuity and resize.
- **T2 classic:** OptiFine G6 captures compare gcolor/gdepth clears, translucent/weather depth splits,
  and composite chains, including a pack with explicit flip overrides.
- **T3:** per-pack feature manifest marks format, shadow, depth split, and unit-map rows.

Motion scenes are mandatory because a one-frame stale flip/depth copy often looks correct in a static
capture. No pack source, rendered image, or screenshot enters the repository; committed oracles are
hash/provenance manifests under Phase 2 policy.

### 8.5 Coordinated shared-unit behavioral checks

D-P5-50 planned cases: two equal-shape accepted atlases A/B with distinct pixels select A_n/A_s
then B_n/B_s after an actual base bind, including within one draw scope and retained shadow
activation. NonAtlas/Unavailable uses exact kind defaults; compatible custom normals/specular
wins in every context; incompatible custom falls through; custom unit0 is classified by its
own accepted association. Stale serial rejects with zero binds, partial refresh contains,
nested pop observes restored base rather than parent serial, and every branch closes exactly
one transferred binding or caller lease. A missing required default suppresses the draw.

These are **future architectural checks**, not executed tests. Phase4 owns layout/selection
production, Phase5 owns policy/binding checks, Phase13 owns texture/animation/macro/noise producer
checks, and Phase7 owns each corresponding end-to-end orchestration trace. §§3.3/9/12 incorporate
the named obligations and their milestone gates.

| Test hook | Owner input → observable outcome |
|---|---|
| sharedUnit_programSpecificTargets | P13 retains composite gaux1 raw1D and `.2` raw3D in one cell; P5 effective sampler1D binds first object on7, sampler3D second on7; exact name stays gaux1, no source patch/unit reassignment. P7 exercises both draws. |
| sharedUnit_incompatibleAliasesBeforeBind | P4 provider declares colortex4 sampler1D and gaux1 sampler3D → typed SAMPLER_UNIT_TYPE_CONFLICT before shader/program creation, only that provider falls back. P5 defensive invalid metadata → zero binds/uploads/draw. |
| sharedUnit_effectiveFallbackLayout | P4 missing/failed child with different layout → ancestor's full layout/state in selector. P5 candidates and P7 activation/P6 callback all use that ancestor; no child overlay. |
| custom_stageExpansionExact | P13 GBUFFERS/DEFERRED/COMPOSITE keys → respectively gbuffers+shadow/deferred/composite+final copies preserving key/discriminator/ordinal. P5 tex binds only shadow and gets typed stage diagnostic in gbuffers. |
| custom_fullscreenFixedOverrides | Compatible composite/final colortex1 custom and deferred gaux1 custom → P5 binds custom on1 and7, not estate backing. All table names/aliases remain reachable. |
| sharedUnit_fixedRangeAndShadowAlias | P5 sixteen rows → units0–15, depthtex1 at11 and gbuffers12 Unused. Adding effective watershadow declaration changes conditional shadow4→5 in P5 and requested P6 resolver together. |
| sharedUnit_exactNameAndOrdinal | P13 absent/0/9 discriminator entries in arbitrary insertion order → among compatible entries discriminator9 has the greatest canonical list ordinal and wins; filtering precedes ranking. Distinct alias winners on one unit → CONFLICTING_CANDIDATES. |
| sharedUnit_fullSamplerShape | FLOAT/SIGNED_INT/UNSIGNED_INT or shadow/arrayed/multisample mismatch despite equal dimension → P4/P5 reject/degrade, never target-only acceptance; aggregates remain lossless unsupported evidence. |
| binding_staleBeforeMutation | P5 independently stale registry generation/fingerprint, estate/depth epoch, frame/context/provider/band/layout/policy, publication/resource epoch or closed lease → exact first rejection with zero binds; overlay publication mismatch beats registry mismatch. |
| binding_absenceVersusIncompatibility | P5 empty cell/incompatible custom/missing publication/unknown name/conflicting declarations → distinct closed diagnostics. Compatible base fallback draws with warning; missing required backing suppresses only that program. P7 discard applies no flips. |
| binding_leaseOwnershipAllExits | Bound transfers once; Rejected/Degraded/BackendFailed do not. P7 draw, activation error, nested suspend/pop, exception/reload/neutralization/teardown → exactly owned binding or lease closed. P13 old leases delay deletion not stale rejection; foreign never deleted. |
| publication_contentIdentity | P13 same key with different bytes or effective upload/filter parameters → different fingerprint; equal path/dimensions do not permit reuse. P5 pairing rejects a mismatched publication. |
| publication_foreignReloadIdentity | P13 same Minecraft dynamic/atlas string with replacement object → reload/object epoch changes publication identity without GL-name hashing; in-place animation does not republish each tick. P5 rejects retired lease. |
| animation_postVanillaSnapshot | P13 reordered frame map and unequal durations, vanilla ticks first → exact current/next frame, interpolation weight and mips mirrored; reload invalidates old snapshot, missing hook restores frame0. P7 delivers only current adapter events. |
| macro_beforeJcppSameBuild | P13 preliminary active/capability policy and independent decoded preferences precede configuration/linked demand; P7 supplies required typed pair before jcpp. Same-build branches/fingerprints change; missing non-Off input is INVALID_REQUEST, never undefined macros as fallback. |
| noise_signedRecurrence | P13 arithmetic shift/wrapping/signed remainder → xorshift(-1)=253983 and channel(1,1,1) remainder=-115/upload141; unsigned shift or absolute value fails. No implementation run claimed. |
| unsupported_noEnumSentinel | P13 unknown name → UnknownSampler(exactName)+KEY_DOMAIN; recognized illegal stage → KnownSampler+STAGE_COLUMN; P5 lookup supplies no sentinel or invented unit. |
| pipeline_textureFailureCompensates | P7 injects texture build/identity/registration failure after P4/P5 acceptance → both off, no Active tuple/draw, caller candidates close and accepted resources retire; preserve ConsumerFailed deliveredCount and no borrowed deletion. |
| shadow_sharedBindingAndGate | Adopted R7-12/13 path uses one selector, sixteen applicable rows before uploads and transferred binding closure. Remaining review/dependency gates keep slot unavailable, never old four-row success. |

## 9. Milestone staging

| Component | Milestone | Staging |
|---|---|---|
| pure buffer plan, identities, format/unit tables | v0.1 | implement |
| dfb colortex0–7 main/alt pairs | v0.1 | implement |
| flip/virtual-pre/frame-end rebase state machine | v0.1 | implement |
| snapshot write-preparation transaction | v0.1 | Acquired only after success; mutation-free Rejected versus consumed-frame Failed(...,true); all P7 acquisition/finally paths stop and retain independent cleanup |
| typed no-program virtual-transition operation | v0.1 | accept exact Phase 4 descriptors; Phase 7 sequences them |
| balanced draw-buffers-none overlay lease | v0.1 | feature activates at Phase 7 H-OVERLAY-01 |
| clear policy/batching/fog alpha rule | v0.1 | implement |
| depthtex0 replacement/provider/version/extent-preparation tracking | v0.1 | implement against the verified Phase 1 §5 contract |
| depthtex1/2 allocation and copy mechanics | v0.1 | implement; Phase 7 supplies moments |
| requested-format → all-RGBA fallback | v0.1 | implement |
| fixed App B.3 policy/layout/selector/candidate/bind infrastructure | v0.1 | §§8.5/12 sharedUnit and binding hooks; Phase6 consumer adoption/fresh review gates upload cutover |
| resize/publication/generation/full-clear lifecycle | v0.1 | implement |
| canonical available/unavailable `resources.*` projection | v0.1 | equal-input PLANNED equality; successful candidate/accepted estate REALIZED equality; declarative-only cross-stage comparison; Phase 2/7 serialize the appropriate owner stage directly |
| sfb structure, policies, and real shadowcolor flips | v0.1 | architect and allocate when required |
| full shared shadow bindings, post-pass mipmap outcomes, coherent runtime neutralization | v0.2 | R7-12/13 adopted/unverified; remaining owner/dependency gates apply, contained failure preserves main pipeline |
| shadow pass bind/copy use | v0.2 | Phase 8 |
| Phase13 custom/noise/companions and content/reload identity | v0.5 | full typed candidate production/execution, §8.5 producer hooks; empty publication before milestone uses identical selector/lease protocol but does not satisfy v0.5 functionality |
| sampler objects, DSA, async copies | v0.5 | Phase 14 implementation behind unchanged policy |
| shadowcomp/prepare/begin/setup wiring, colortex16/32, shadowcolor2–7 | post-v0.5 | G8/S1/S2; no identity redesign |

## 10. OQ & spike specifications

Phase 5 has no assigned open question. No spike is invented. The Phase 1 and Phase 4 dependency
requests and the Phase 5 target-profile request are granted; §0.25 records their verified state.
This dependency-adoption surface now requires its own fresh Phase 5 verification round.

The former `superSamplingLevel` authority question is resolved by the maintainer's
2026-09-07 option-only disposition in §11.6; it is not an implementation spike or SSAA gate.

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision and rationale / contract check |
|---|---|
| D-P5-1 | Adopt a main/alt pair plus per-pass immutable flip snapshot. App B.1 and the shipped attachment description require exactly that; Pintonium supplies working structural evidence. |
| D-P5-2 | Adopt clear grouping, both-side behavior, and fog alpha 1.0. App B.1/§4.3 control the base colors and flipped clearing; the alpha quirk is deployed evidence consistent with them. |
| D-P5-3 | Retry an incomplete requested-format estate only as a wholly recreated `RGBA_COMPAT` estate. This restates §4.3's estate-wide plain-RGBA fallback and avoids an uncontracted mixed retry. |
| D-P5-4 | Reject alt→main copy-back. Preserve App F.7's last-writer state, then rebase the physical committed-main identity and clear the relative bit at frame end. No bandwidth copy occurs. |
| D-P5-5 | Adopt Pintonium's depth-renderbuffer replacement/version shape behind a Phase-5 SPI and consume Phase 1's verified narrow borrowed-attachment issuance/permission contract. App B.2 requires sampleable depthtex0; no raw GL name crosses D-6. |
| D-P5-6 | Adopt function-pointer-checked copy image → combined blit → depth copy-sub-image tiers as backend mechanics. App B.2 constrains contents/moments, not the mechanism. |
| D-P5-7 | Allocate a contiguous minimum-four G6 inventory through the highest required index, never unconditional 16. This satisfies App B.1 and RC3's scan-driven ruling while retaining growth-shaped IDs. |
| D-P5-8 | Use the generic real flip machine for shadowcolor. Historical B4 stub attribution is superseded by D-P5-35's pin qualification, not the real-state requirement. |
| D-P5-9 | Reproduce the fixed App B.3 map, including depthtex1 at unit 11, and reject dynamic allocation. |
| D-P5-10 | **Historical, superseded by D-P5-27:** retain display × render-quality extent independently of the parsed supersampling integer; the original execution handoff was narrowed by D-P5-23 because no receiving schedule or complete authority semantics existed. |
| D-P5-11 | Use candidate ownership plus equality generation tied to `RegistryFingerprint`, so no partial or cross-registry estate can publish. |
| D-P5-12 | Pack FBO color attachments into route order while retaining logical buffer identity. This preserves G6 behavior and avoids hardcoding logical colortex index as the future physical attachment limit. |
| D-P5-13 | Use the observed G6 null-allocation transfer layout for all 37 formats: non-integer `BGRA`, integer `RGBA_INTEGER`, and `UNSIGNED_INT_8_8_8_8_REV` with null data. The full pack-facing pixel vocabulary remains available for Phase 13 raw uploads. |
| D-P5-14 | Adopt the deployed resize/version invalidation checklist for Phase-5-owned color/depth/FBO state, including full clear, and publish resize notices to later object owners. This satisfies RESEARCH §4.1/§4.3 without letting Phase 5 seize Phase 13/14 resources. |
| D-P5-15 | Require Phase 7 to prepare Minecraft's shader framebuffer so the borrowed depth texture exactly matches the render-quality-scaled dfb extent before candidate creation. A mismatch returns an awaiting/rebuild result and attaches nothing. This reconciles §4.3 sizing with App B.2's real depth attachment where Pintonium's fixed-1.0 render quality supplies no answer. |
| D-P5-16 | Historical four-row R8-2 binding model superseded by D-P5-22. Its typed mipmap outcomes and atomic idempotent neutralization remain active; Phase5 retains filters/flips/owned shadow objects. |
| D-P5-17 | Order main copied-depth moments as frame begin → pre-weather → pre-translucent → commit. RESEARCH world order draws weather before translucent, and the two named depth views must be captured before their corresponding content. |
| D-P5-18 | Apply virtual pre flips only through `applyVirtualTransition(frameId, PassDescriptor)`, accepting the exact planned Phase 4 virtual descriptor and no resolved program. This preserves both typed ownership and the invariant that virtual slots never masquerade as shaders. |
| D-P5-19 | Route first-person overlays through one balanced draw-buffers-none lease that snapshots/restores selection and is checked by generation, attachment epoch, frame, and lease identity. Raw draw-buffer state and unbalanced HEAD/RETURN mutation are prohibited. |
| D-P5-20 | Historical single-stage projection equality superseded by D-P5-32: pure planning cannot establish allocation fallback or frame fog colors. Available still means complete evidence, not capability/runtime success. |
| D-P5-21 | One full exact-name AppB.3 policy/resolver with lossless target-specific candidate cells. AppF.5 requires per-program type selection (`docs/research/v1/RESEARCH.md:1484-1490`); compatibility filtering then greatest canonical ordinal makes distinct discriminators deterministic without inventing property order. Fixed units and watershadow semantics remain authoritative. |
| D-P5-22 | One authenticated sixteen-row physical binding operation for ordinary/shadow passes, distinct preflight/degradation/backend outcomes, Bound-only lease transfer and local no-flip discard. Expiry never removes closure duty; Phase 8 R7-12 consumption is now adopted/unverified. |
| D-P5-23 | **Historical, superseded by D-P5-27:** IR-12 retained SSAA metadata, retracted false Phase 7 execution agreement and gated level>1 pending no-AA/directive reconciliation plus exact sample/resolve/failure authority; no invented algorithm or runtime fallback. |
| D-P5-24 | IR-03/04: adopt current Phase 3 schema/companion/lossless contracts and Phase 6/8 R7-10..13 grants while preserving fresh-review and genuinely missing authority/source gates. |
| D-P5-25 | IR-29: publish dependency-free BufferArchitectures.create acquisition for the existing pure planner and same-build source-free capability projection | Exact shortfalls are P5-owned; P4 failure and P3 minima cannot substitute |
| D-P5-26 | Investigation distinguished published integer/default and licensed option recognition from unproven engine SSAA and bundled FXAA settings. Its recommendation is evidence in §11.6; the subsequent maintainer disposition is D-P5-27. |
| D-P5-27 | Apply the maintainer's explicit 2026-09-07 Pack-option compatibility only decision: remove engine SSAA requirement and sampling-only P5 API/identity, preserve declared/selected source values and normal pack execution, retain no-AA runtime/UI. Historical authority bytes remain unchanged; this recorded disposition controls the active phase contract. |
| D-P5-28 | 2026-09-08: adopt P3 D-P3-69 schema20 and matching nested IDs/same-load assets identity in §5.3, superseding earlier current-schema assertions only. Preserve sizing/depth/binding and source-free resource projection; no binary acquisition authority or historical review clearance. |
| D-P5-29 | 2026-09-08: adopt P3 D-P3-70 exact-current schema21/nested-ID and current configuration/materialization identity in §5.3; carry P4 D-P4-31 opaque registry identity normally. Older receipts are history; sizing/provider/lifetime semantics unchanged; fresh review required. |
| D-P5-30 | R32-1/R39 C1: adopt positional route realization through P1/P4 owner grants and plain-RGBA baseline with independent request/fallback provenance |
| D-P5-31 | R39 C2: own exact frozen main mipmap operation, revision/filter freshness and already-aborted restoration-failure containment |
| D-P5-32 | R39 C3: separate PLANNED/REALIZED evidence and declarative fog policy; /4 variant fields replace impossible cross-stage equality |
| D-P5-33 | R39 C4: scheduled consumption is independent of copied-depth success; BackendDegraded advances once while destination remains fallback |
| D-P5-34 | R39 C5: bounded neutral objects keyed by full compatible policy, initialized before readiness and retained until normal retirement; no P14 dependency |
| D-P5-35 | R39 N1: pinned 9c2fcc1 flip body toggles and accessor selects alt/main despite stale TODO; qualify historical B4 attribution without rewriting authority |
| D-P5-36 | R40 C40-1: snapshot preparation has a total three-way result; mutation-bearing failure returns Failed(...,true), consumes frame and requires stale/full-clear/off, with independent cleanup but no repeated lifecycle call |
| D-P5-37 | R40 C40-2/N40-2: equal-input PLANNED equality and exact candidate/estate REALIZED equality only; declarative-only cross-stage comparison; BufferSizing has exactly mainExtent and shadowExtent |
| D-P5-38 | P4 R34 C1: sole policy returns complete immutable candidate sampler integer assignments through its existing resolver before registry GL allocation |
| D-P5-39 | P14 R1 C2: successful-preflight occupied-unit mask dispatch precedes physical binds; rejected/degraded paths remain mutation-free and normalization failure prevents Bound |
| D-P5-40 | Receive P3 D-P3-72 schema22/current materialization and opaque P4 identity; older numeric/domain receipts historical |
| D-P5-41 | Receive P14/P1 complete owned-state replay before runtime sampler demotion; existing publication/retirement containment remains |
| D-P5-42 | Receive P1 D-P1-63 target-bearing synchronous shared value grant and publish exact P13 conversion vocabulary/main-depth defaults; retain 37 raw formats, private fallback, depth/copy/restoration semantics |
| D-P5-43 | Receive P14 D-P14-31/P1 D-P1-64 complete latest object baseline on every successful setter alongside sampler cache; all-unit clearing and borrowed restrictions unchanged |
| D-P5-44 | Exact-current schema23/MaterializedSource-v23 containing/nested/inspection receipt; all prior numeric receipts historical; nine metadata-only trees/projectionVersion1 unchanged |
| D-P5-45 | Receive P4 D-P4-40 optional exact contained virtual prelude before ascending indexed passes in one deferred/composite step; no synthesized descriptor or new pass policy |
| D-P5-46 | R43: deterministic finite realized-format conversion, exact P1 D-P1-67 typed clear grant, numeric-safe per-attachment execution and complete-state/error restoration; consume mandatory full-clear only on all-success |
| D-P5-47 | Receive P1 D-P1-66 actual target-specific captured limits and required serialization for pure/backend/recorder equality, no guessed maxima |
| D-P5-48 | Receive P4 D-P4-43 opaque RegistryFingerprint/profile-selection-v3 at existing identity checks; earlier positional-route-v2 receipt historical, no local profile codec/inference |
| D-P5-49 | R44 C1: preserve all §4.7 RGBA triggers, separate required integer-clear-tier rejection; receive P1 D-P1-69 capability-legal private rasterizer-discard bracketing and all-success restoration |
| D-P5-50 | R13 C2 receiver: actual accepted base-atlas context via P13 context-bound lease, custom-first exact matching/default selection and P5-only before-draw refresh; same pass/selector, no extra shadow activation |

### 11.2 D-1…D-10 disposition

| Decision | Disposition |
|---|---|
| D-1 | no non-Cleanroom platform path; platform depth bridge is `mod.glue` |
| D-2 | buffers only; no renderer/performance feature |
| D-3 | conformance tests name the fixed pack matrix |
| D-4 | indices/state support future stage/buffer growth without wiring it |
| D-5 | one targeted framebuffer mixin; policy remains outside it |
| D-6 | pure `engine.buffers`, opaque handles, no LWJGL in engine |
| D-7 | GPL-3.0-or-later project posture retained |
| D-8 | published documentation and explicitly attributed reference evidence; OF behavior restated, no copy. The pinned GPL Version 3 qualification in §11.3 governs Pintonium reuse, not historical blanket LGPL wording |
| D-9 | compatibility-profile semantics and fixed-function coexistence retained |
| D-10 | pure and recorded-GL tests are first-class implementation work |

### 11.3 Input contradictions and rulings

1. **depthtex1 unit 12 versus 11.** One shipped ID table says 12; its uniform table, composite table,
   observed behavior, and RESEARCH ruling say 11. **Ruling: 11**, with 12 reserved for composite
   depthtex2.
2. **Pintonium copy-back versus App F.7.** Pintonium copies alt→main; App F.7 says last writer leaves
   flip enabled for later reads. **Ruling: D-P5-4 metadata rebase**, no copy-back.
3. **Pintonium 16-always versus scan-driven G6.** **Ruling: D-P5-7**, contiguous 4–8 at v0.1;
   identities grow later.
4. **Historical B4 shadow-stub attribution.** Actual pinned bodies implement toggle and
   alt/main access; D-P5-35 qualifies the old negative claim. Real local flip tests remain required.
5. **RESOLVED UPSTREAM — Phase 1 foreign-handle rule versus mandatory depth reattachment.**
   Ordinary foreign attachment remains forbidden; Phase 1 now authenticates and issues the
   narrower borrowed-depth handle and permits it only for sampling/labeling and depth/depth-stencil
   attachment. **Ruling: consume the verified marker-scoped issuance and permission matrix.**
6. **RESOLVED UPSTREAM — combined attachment and first-copy initialization.** Phase 1 now exposes
   distinct `attachDepthStencil` and `initializeDepthTextureFromFramebuffer` operations alongside
   depth-only attachment and storage-preserving steady copy. **Ruling: consume the explicit
   operations and their distinct recorder events; never infer backend state.**
7. **RESOLVED — `superSamplingLevel` authority contradiction.** §11.6's prior evidence
   establishes author declaration/default and licensed option recognition, not an executor.
   The maintainer selected pack-option compatibility only on 2026-09-07. D-P5-27 removes the
   engine requirement and preserves source semantics; RESEARCH/RC3 historical text is not edited.
8. **Pintonium format superset.** Its enum includes 8/16-bit integer targets absent from App B.4.
   **Ruling: exactly 37**, no imported additions.
9. **Pintonium copy strategy does not handle combined stencil without GL3.** **Ruling:** capability
   failure for the copied-depth feature; never perform an invalid GL2 copy.
10. **Runtime quality adaptation.** `BufferRuntimeInputs` remains Phase 5's input, supplied by
    Phase 7 from current decoded Phase 3 renderResMul/shadowResMul and platform display extent.
11. **Borrowed Minecraft depth extent versus render-quality-scaled dfb extent.** Pintonium's
    replacement is proven only with its global render quality fixed at 1.0, while RESEARCH §4.3
    requires display × render quality. **Ruling: D-P5-15 safe-point preparation**; a mismatch is
    never attached or stretched.
12. **CURRENT SHADOW GRANT ADOPTED, UNVERIFIED.** Phase 8 §§0.7–0.8 adopts D-P5-22/R7-12
    binding and R7-13 planning. Mipmap/neutralization remain unchanged; fresh owner verification
    and remaining dependency gates still precede real execution. Physical selection stays Phase 5.

**R32/R39 coordinated owner receipt — 2026-09-08, unverified.** Adopt P1 D-P1-57 typed
framebuffer slots; P4 D-P4-33 positional-route-v2 was the historical identity at that receipt.
Current opaque identity is P4 D-P4-43 `RegistryFingerprint/profile-selection-v3`,
committing explicit profile selection and evaluated state; P5 carries/compares the
owner-issued fingerprint without a codec or reconstructed profile inference. P7 must call generateMainMipmaps
after snapshot and before binding/activation, handle its total result and already-aborted failure,
and consume BackendDegraded copy points. P2/P7 must adopt §4.1.1's /4 PLANNED/REALIZED and
clear-policy grammar; old same-projection equality is withdrawn. P8 receives the bounded full-policy
neutral backing semantics without changing its existing binding/neutralization method signatures.
N1 independently checked at https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/ShadowRenderTargets.java :
flip toggles flipped[target], getColorTextureId chooses alt/main via isFlipped. Historical PD/RC3
and report bodies remain unchanged. This narrow observation neither licenses copying nor proves
shadow pipeline correctness; the report's GPL provenance warning governs future source reuse.
All changed §5 contracts await fresh whole-document reviews; IR-01 and final G5.3 remain open.

**R40 owner resolution receipt — 2026-09-08, unverified.** C40-1 is adopted as D-P5-36 in
§§2/4/5/6/8/9/12. P7 must consume the new Failed variant in §4.4 ordinary acquisition and
nested parent reacquisition, §4.6 fullscreen acquisition before mipmaps and final cleanup, and
the outer finish/abort/finally driver. Shared terminal state prevents parent resume, remaining
composites/final, or second complete/discard/commit/abort; independently owned bindings/leases
and P4/local-state scopes still close. P7 owns that receiving amendment; this receipt is not
verified dependent adoption. C40-2 removes active whole cross-stage equality instructions;
PLANNED-to-PLANNED, REALIZED candidate-to-estate and declarative-only cross-stage rules govern
every publication/checklist. N40-2 corrects sizing wording without a new field or alias.
N40-1 is retained: the pinned root LICENSE identifies GNU GPL Version 3, not blanket LGPL;
the historical §0 licensing label is not future reuse authority. The pinned flip/accessor bodies
qualify historical B4 wording, not whole-shadow-pipeline correctness. No source is copied and
historical authority/review bodies remain intact. Fresh owner/receiver review remains required;
IR-01 and final G5.3 remain open. No validation, test or implementation execution is claimed.

### 11.4 Hand-offs

- **Phase 2:** acquire `BufferArchitectures.create()` and use §5.1's same-inspection-build pure
  plan route with explicit recorded runtime/capability inputs. Copy Available/Unavailable
  resources unchanged, including legitimate SHORTFALL; never infer P5 capability from P4 status.
- **Phase 6:** consume adopted/unverified R7-10 resolver in unchanged afterBind; preserve exact
  effective layout and R7-11 retirement. No physical handle selection, new map or participant.
- **Phase 7:** own frame/copy/clear moments, final-to-Minecraft bind, try/finally
  commit/abort, runtime quality input, safe-point `MainDepthSource.prepare(plan.mainExtent())`, and
  registry→estate publication ordering. Call copied-depth points in weather-then-translucent order;
  pass exact virtual descriptors to `applyVirtualTransition`; close draw-buffers-none leases in
  `finally`; and copy the published `BufferResourceSnapshot` directly into Phase 2 evidence. Never
  build or draw with a mismatched depth extent or infer missing projection fields.
  Handle snapshot Failed(...,true) at ordinary acquisition, nested child/pop acquisition and
  fullscreen acquisition before mipmaps; mark the common frame terminal before unwinding.
  Outer finally must close independently owned scopes but never repeat a consumed P5 lifecycle
  call, resume a parent or run remaining composites/final. Publish off and require safe replacement/full clear.
- **Phase 8:** consume adopted/unverified §5.3.1 R7-12/13: same selector/context, full lease,
  five-argument physical shadowBindings, sixteen rows before activation, all result branches,
  exactly-once closure, registry-independent planning. Real slot stays unavailable until fresh
  reviews and remaining owner gates close; old four-row bindings are not a fallback.
- **Phase 13:** produce lossless full-domain candidates and current leases; own source/content/
  parameterization and deferred retirement. Register only after actual accepted estate pairing.
- **Phase 14:** modernize facade/backend mechanics without changing this policy.
- **G8/S1/S2:** lift v0.1 population gates and wire dormant stages; retain identities and flip
  machine.
- **Final integration review:** audit the P5/P6 texture-unit split, Phase 7 publication ordering,
  Phase 8 shadow ownership, and Phase 13 overlay completeness.

### 11.5 Requested upstream changes

- **Optional modernization:** R-P14→P5-2 in §5.5 is received/ungranted; keep per-unit/NONE
  binding until an explicit owner amendment defines complete-preflight batch semantics. No P7 binder.

1. **GRANTED — Phase 1 framebuffer/depth additions.** Phase 1 §0.18/§0.19 and §5 expose the
   authenticated borrowed-depth issuance/permissions, combined depth/stencil attachment, and
   first-copy initialization requested in §5.5. The literal PASS at
   `docs/phase1/reviews/PHASE_1_REVIEW_20.md:61`–`:75` closes the amended dependency surface.
2. **RESOLVED BY MAINTAINER — IR-12/24 (2026-09-07).** `superSamplingLevel` is pack-option
   compatibility only, with declared/selected value and source use preserved and no engine
   allocation/draw/resolve effect. Engine AA runtime/UI remain excluded. D-P5-27/§11.6 apply
   that explicit disposition; no engine sampling domain, algorithm or separate grant is pending.
3. **GRANTED — verification target.** The data-only
   `verification/targets/phase-5.json` profile exists, resolves RC3 and this artifact, and has
   already driven the completed Phase 5 reviews.
4. **GRANTED — Phase 4 candidate-view binding.** Phase 4 §5 publishes the immutable detached,
   non-owning, generationless pre-publication `CompiledRegistryCandidate.view()` required by
   §5.5 while preserving opaque-candidate ownership and provenance. The literal PASS at
   `docs/phase4/reviews/PHASE_4_REVIEW_15.md:59`–`:72` closes that dependency change.
5. **SUPERSEDED BINDING GRANT — Phase 8 R8-2.** §0.32's old four-row binding model is historical;
   §§2.4/4.10/4.12/5.1 now publish the full shared protocol. Mipmap and neutralization semantics
   survive. Phase 8 now adopts §5.3.1 R7-12/13, but fresh verification and remaining dependency
   gates still keep real shadow NotInstalled/unavailable.
6. **GRANTED DOWNSTREAM — Phase 7 R7-1/R7-2/R7-3.** Sections 0.38, 2, 4–6, 8–9, 11, and 12
   publish the corrected depth order, typed virtual transition, and balanced overlay lease.
7. **GRANTED DOWNSTREAM — Phase 2 R10A / Phase 7 R7-5.** The same sections publish the complete
   immutable canonical `resources.*` value and its exact available/unavailable/shortfall grammar.
   Phase 7 serializes it without inference.

8. **CURRENT GRANTS ADOPTED/UNVERIFIED:** §5.3.1/§5.5 reconcile Phase 6 R7-10 resolver/R7-11
   retirement and Phase 8 R7-12 shared binding/R7-13 planning. Phase 3 companion/lossless/direct
   projections and Phase 1 package/native-configure grants are present. Native legacy source,
   suffix semantics, jcpp permission and fresh owner reviews remain open;
   unrelated Phase 10/14 proposals are not granted. Phase 13 R2 is designed, not verified.

### 11.6 Sampling evidence and approved authority disposition (D-P5-26/27)

**Evidence gathered 2026-09-07, before this amendment:**

| Source and exact location | What it establishes; what it does not |
|---|---|
| `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:413–425`, especially 421 | The shipped author document says `const int superSamplingLevel = 1;` with effect `superSamplingLevel = 1`; the Comment cell is empty. It establishes spelling, integer example/default notation and configuration recognition, not “N samples,” “N² samples,” admitted levels, changed extents, jitter or resolve. |
| `[V:web]` [official OptiFine shader author document](https://github.com/sp614x/optifine/blob/07ec2ca62f81aa52cb9ed1a7fd49a4489c131b1e/OptiFineDoc/doc/shaders.txt#L555), Fragment Shader Configuration | The current published document repeats the same row without an explanatory comment. The revision was obtained from the author's commit API. This is corroboration, not permission to import modern engine semantics into G6. |
| `[V:doc]` shipped `doc/shaders.txt:653–661`; [official Standard Macros H](https://github.com/sp614x/optifine/blob/07ec2ca62f81aa52cb9ed1a7fd49a4489c131b1e/OptiFineDoc/doc/shaders.txt#L779-L787) | `MC_FXAA_LEVEL` is present **when FXAA is enabled**, with values 2/4. Render quality has its own macro/domain. Neither statement links the FXAA setting to `superSamplingLevel`; zero-only settings must not emit a false enabled macro. |
| Licensed local engine evidence: `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/option/OptionAnnotatedSource.java:54–96,202–309`; root `LICENSE:1–3` says GPL v3 | The name is in the const-option allowlist; numeric options pass through the generic value-list recognizer. A case-insensitive checkout search for `superSampling`, `supersampling`, `SSAA` and `MC_FXAA` found only that allowlist occurrence. This supports option compatibility and supplies **no** engine executor. It is a bounded absence observation, not proof of all historical engines' behavior or a license ruling on every bundled dependency. No code is copied. |
| Published [Iris 26.1 option recognizer](https://github.com/IrisShaders/Iris/blob/26.1/common/src/main/java/net/irisshaders/iris/shaderpack/option/OptionAnnotatedSource.java), static allowlist and `parseConst`; [LGPL-3.0 license](https://github.com/IrisShaders/Iris/blob/26.1/LICENSE) | Independently confirms the same option-recognition path. Only this file was used; no whole-Iris absence claim or transformer implementation is used. Option recognition is not renderer support. |
| Governing `docs/research/v1/RESEARCH.md:65–86,518,533–547,1181` and `docs/design/v2.0-RC3/DESIGN.md:1638–1646` | RESEARCH excludes bundled AA/AF and separately claims “SSAA multiplier”; baseline sizing/frame flow gives no supersampling operation. RC3 repeats the directive and explicitly says Pintonium supplies no implementation help. RC3 cannot override RESEARCH or turn its label into an algorithm. |

**Result.** Actual purpose beyond recognized configuration/option compatibility, the supported
engine-level domain above 1 and an engine sampling algorithm remain unestablished by these
sources. A shader may consume its own constant through ordinary materialized GLSL; preserving
that source behavior is different from supplying an engine SSAA pass. Neither the word
“supersampling” nor the generic concept of antialiasing authorizes a rendering design. The
prior observed-behavior digest in §0.3 supplies only the same label and no missing schedule.
Search-result summaries and unsourced examples are not authority.

**Maintainer disposition — 2026-09-07, after the evidence above:** **“Pack-option compatibility
only.”** Remove the engine-SSAA requirement for `superSamplingLevel`; preserve the declared/
selected option value and source use, with no engine allocation, draw or resolve effect.
AA UI/runtime remain excluded. This is the explicit authority correction to the earlier
App A.3/RC3 engine interpretation, recorded here rather than rewriting historical research,
design, addenda or reviews. The alternative engine-SSAA exception was not selected.

**Active cross-owner cutover (D-P5-27):**

| Owner | Required contract |
|---|---|
| P3/P12 | Preserve catalog-issued state-free materialization and selectable pack-option values; remove obsolete engine sampling resource meaning and any engine-only positive-level restriction. P12 exposes the pack option through normal pack-option rules, never as an engine AA control. Engine AA runtime/UI are excluded; no `MC_FXAA_LEVEL` enabled macro. P3's coordinated schema 18 owns this semantic cutover; consumers reject older schemas. |
| P5 | `BufferSizing(Extent2i mainExtent, Optional<Extent2i> shadowExtent)` replaces the three-field shape; structural equality has exactly these two extents. No retained sampling resource, alias/accessor, extra allocation, extent multiplier or forced level 1. Ordinary configuration rebuild and resource declaration changes still work. |
| P7/P6/P8 | Use the existing ordinary synchronous frame for every option value. No engine jitter, additional sample/world/shadow draws, history rotation, uniform sampling or tick advancement is triggered by the option's name. Shadow cadence, PRE_WEATHER/PRE_TRANSLUCENT depth moments, flips, one Final and existing early-exit/failure/lease cleanup remain unchanged. Pack-authored GLSL uses are preserved. |
| P2/P7 capture | Capture remains after ordinary Final/before present with the existing camera/history manifest facts. No SSAA sample count, resolve success or engine-supersampling conformance claim. P3 option/configuration evidence records the selected value where its existing schema requires it; P5 resource evidence does not fabricate one. |
| P6/P7/P8/P13/P14 sizing consumers | Migrate constructors, accessors, equality, resize registration baselines and inspection assumptions to the two-field `BufferSizing`; no inferred upgrade or ignored third argument. Main owns cross-document adoption; P5 supplies this exact changed §5 contract. |

The engine-level authority gate is closed by explicit scope disposition, **not** by treating
metadata as execution. The published evidence does not become proof that every historical
OptiFine engine lacked an effect; it supports the chosen project contract. §5 remains changed
and unverified; no implementation or verification clearance follows.

## 12. Implementation checklist

Attempt-6 additions: implement D-P5-46 finite conversion/typed plan, P1 D-P1-67
native/recorder grant and all-success restoration/error consumption as one cutover.
D-P5-47 target limits must reach pure allocation preflight and recorder fixtures;
§8 cases plus each target's exact-limit/maximum+1/unsupported-zero boundary are required
implementation evidence, not tests or native results executed by this architecture fix-up.

R32/R39 prerequisites: implement positional routing, DefaultRgba first attempt, main mipmap
transaction/freshness resets, two-stage evidence/fog policy, consumed failed depth-copy points
and full-policy neutral cache together with their §8 boundary/failure cases. No old raw-route,
single-stage evidence or one-neutral-depth-object compatibility path is retained.

| # | Work item | Tag | Test hook |
|---:|---|---|---|
| 1 | Define logical buffer IDs, extents, closed format/depth/unit enums | v0.1 | contract-table tests |
| 2 | Implement pure `BufferPlanner`, closed validation failures, and exact canonical available/unavailable resource projection | v0.1 | invalid/capability plus `resourceProjection_*` tests |
| 3 | Implement exact 37-format table + private `RGBA_COMPAT` | v0.1 | `formatTable_hasExactly37PackFacingNames` |
| 4 | Implement checked main/shadow extent formulas and limits | v0.1 | overflow/round/maxTextureSize tests |
| 5 | Implement scan-driven contiguous colortex inventory | v0.1 | min4/highest-index/not16 tests |
| 6 | Consume the verified detached `CompiledRegistryCandidate.view()` and implement Phase 4 route expansion and packed attachment map without treating the snapshot as live publication state | v0.1 | explicit/symbolic/sparse route tests plus retained-snapshot planning |
| 7 | Implement main/alt `FlipState` and immutable snapshots | v0.1 | exhaustive four-state tests |
| 7a | Implement total snapshot write-preparation result and P7 shared terminal disposition | v0.1 | base-filter/binding-restoration failure after mipmap enable across ordinary/nested/fullscreen and outer finally; no success/rejection/throw escape, no double lifecycle call, independent cleanup |
| 8 | Implement raster transition rules plus exact-descriptor `applyVirtualTransition` with no program input | v0.1 | override permutations and `virtualTransition_*` matrix |
| 8a | Implement balanced generation/epoch/frame/identity-checked draw-buffers-none lease | v0.1 | open/close/abort/commit model plus recorded NONE/restore failures |
| 9 | Implement frame-end metadata rebase and abort/full-clear | v0.1 | no-copy next-frame continuity test |
| 10 | Implement D-P5-46 finite realized-format conversion, alpha/side rules, typed numeric-safe batches and all-success full-clear consumption through P1 D-P1-67 | v0.1 | §8.1 mixed numeric/route/state/GL3/EXT/error-restoration matrix |
| 11 | Implement dfb texture allocation and pass/clear FBO cache | v0.1 | recorded attachment/filter/wrap run |
| 12 | Implement candidate ownership ledger and idempotent close | v0.1 | leak/use-after-delete assertions |
| 13 | Implement requested-format attempt and all-RGBA retry | v0.1 | scripted incomplete-FBO tests |
| 14 | Consume Phase 1's verified authenticated borrowed-depth issuance and permission matrix | v0.1 prerequisite | R20 literal PASS; forged/wrong-origin/ordinary-foreign rejection |
| 15 | Consume the verified combined depth-stencil verb and replay event | v0.1 prerequisite | distinct recorded operation |
| 16 | Consume the verified first-depth-copy initialization verb and replay event | v0.1 prerequisite | initialization distinct from steady copy |
| 17 | Implement dumb framebuffer depth-replacement mixin | v0.1 | live create/delete/stencil smoke |
| 18 | Implement `MainDepthSource` safe-point prepare plus version/extent snapshots | v0.1 | ready/pending/mismatch/version tests |
| 19 | Implement depth-version reattachment and FBO recheck | v0.1 | recorded reattach/epoch test |
| 20 | Allocate/reallocate depthtex1/depthtex2 | v0.1 | format/extent/lifetime tests |
| 21 | Implement first-copy and three-tier steady copy backend | v0.1 | selector matrix + live pointer test |
| 22 | Implement weather-before-translucent copy-point ordering and non-stale fallback | v0.1 | duplicate/out-of-order/failure plus world-order tests |
| 23 | Implement exact App B.3 object table | v0.1 | 16-row × stage equality test |
| 24 | Implement closed `TextureBindingResult` and pass-coherent `TextureBindingSnapshot` | v0.1 | mismatch reason/no-bind/lease-ownership and flip-between-bind tests |
| 25 | Implement sfb structure, neutral fallbacks, and shadow policy | v0.1 | recorded creation/filter/PCF/swizzle |
| 26 | Implement real shadowcolor flip state | v0.1 | local toggle/alt-main state-machine test, without treating historical B4 as pinned behavior |
| 27 | Implement estate publisher/generation/fingerprint checks; preserve exact candidate REALIZED evidence into ready, Unavailable(SHADERS_OFF) into off | v0.1 | stale/cross-registry rejection; equal-input PLANNED equality, candidate/estate REALIZED equality and declarative-only cross-stage comparison |
| 28 | Implement resize classification, rebuild, notices, full clear | v0.1 | trigger matrix test |
| 29 | Wire Phase 7 frame/clear/copy/publication orchestration, virtual descriptors, balanced overlay lease, and direct resource-evidence capture | v0.1 | recorded complete classic frame and Phase 2 canonical manifest fixture |
| 29a | Implement checked shadow bindings, typed canonical mipmap outcomes including restoration-failure containment, and atomic idempotent neutralization | v0.2 | validation-priority, lifetime, per-buffer and restoration failure, open-abort, and coherent-neutral tests |
| 30 | Wire Phase 8 sfb use and shadowtex1 copy | v0.2 | shadow scene/T1 test |
| 31 | Produce/consume full Phase13 candidates and transaction-bound resize registration, never narrow slots | v0.5 | §8.5 sharedUnit/custom/publication hooks |
| 32 | Implement Phase 14 backend modernization behind same APIs | v0.5 | cross-backend call-log equivalence |
| 33 | Lift modern buffer/stage population gates | post-v0.5 | 16/32/sparse/shadowcomp tests |
| 34 | Run the implementation gate: full classic estate create/use/destroy | v0.1 exit | `noLeakedObjects`, `noUseAfterDelete` |
| 35 | Implement sole fixed-name policy/resolver, prelink layout validation and same-selection physical binding; integrate granted Phase6 adapter only | v0.1 infrastructure | §8.5 sharedUnit_* and binding_* input→outcome rows |
| 36 | Implement Bound-only closure and local pre-draw discard, including nested suspend/pop and failed activation | v0.1 | `binding_leaseOwnershipAllExits`, `binding_absenceVersusIncompatibility` |
| 37 | Adopt requested full shadow migration without changing mipmap/neutralization containment | v0.2 | `shadow_sharedBindingAndGate` |
| 38 | Pair texture build/registration to accepted registry/estate and compensate all failures off | v0.5 execution | `pipeline_textureFailureCompensates`, `publication_contentIdentity`, `publication_foreignReloadIdentity` |
| 39 | Implement D-P5-49 preserved three-trigger fallback and private discard-state receipt | v0.1 | §8.1 allocation/capability/FBO/missing-tier/discard/error cases |
| 40 | Implement D-P5-50 accepted-atlas selection and serial-bound ordinary/nested/shadow refresh through the sole binder | v0.5 | §8.5 two-atlas/non-atlas/custom/default/refresh/lifetime cases |

The future coding effort must complete items in dependency order. The verified contracts in items
14–16 are hard prerequisites for 17–21; no implementation may bypass them with a raw GL name or
direct engine-side LWJGL call.

---

§5 changed in this coordinated rebuild. Unverified; a fresh whole-document review returning literal PASS is required before verified downstream consumption. v1 retained; no directory roll.
