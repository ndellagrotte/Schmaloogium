# Schmaloogium — Phase 5: Framebuffer & buffer architecture — Architecture

## 0. Header

**Phase:** 5 — Framebuffer & buffer architecture

**Milestone:** v0.1; shadow-FBO structure at v0.1, shadow-pass wiring at v0.2

**Depends on:** Phase 1, Phase 3, Phase 4

**Assigned OQs:** none

**Authored:** 2026-07-28

**Deliverable:** this document, following
`docs/design/v2.0-RC3/DESIGN.md:790`–`:826` and its mandatory thirteen-section template.

**Governing design revision:** `docs/design/v2.0-RC3/DESIGN.md`. The Phase 5 assignment starts at
`docs/design/v2.0-RC3/DESIGN.md:1572`; its objective says the subsystem is
*"Contract-visible almost end to end"* at `docs/design/v2.0-RC3/DESIGN.md:1583`.

### 0.1 Inputs actually read

| Input | Material read and use |
|---|---|
| `AGENTS.md` | Complete repository and document-system instructions. |
| `docs/MOVES.md` | Path/version manifest and the four-`DESIGN.md` collision warning. |
| `docs/design/v2.0-RC3/DESIGN.md` | All of Part I, §G0–§G12 (`docs/design/v2.0-RC3/DESIGN.md:92`–`:1119`), and the complete Phase 5 assignment (`docs/design/v2.0-RC3/DESIGN.md:1572`–`:1685`). Other phase specifications were not read. |
| `docs/research/v1/RESEARCH.md` | Mandatory §0–§1; assigned §3.6.3, §4.1, §4.3, and Appendix B whole. Two narrow extra reads are disclosed in §0.3: App A.3 at `docs/research/v1/RESEARCH.md:1180` and App F.7 at `docs/research/v1/RESEARCH.md:1508`–`:1515`. |
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
| Latest dependency reviews | `docs/phase1/reviews/PHASE_1_REVIEW_18.md`, `docs/phase3/reviews/PHASE_3_REVIEW_14.md`, and `docs/phase4/reviews/PHASE_4_REVIEW_11.md`. Each ends in literal `PASS`, zero corrections, and no outstanding §5 change. Phase 2's latest review was also checked for wave state but Phase 2 is not a Phase 5 dependency. |

The dependency gate is satisfied. Phase 1's latest review says *"Phase 1 remains verified and is a
valid dependency input"* (`docs/phase1/reviews/PHASE_1_REVIEW_18.md:60`–`:61`); Phase 3's latest
review records *"literal PASS"* and no owed interface change
(`docs/phase3/reviews/PHASE_3_REVIEW_14.md:42`–`:48`); Phase 4's latest review says its fresh review
of the corrected §5 surface established convergence
(`docs/phase4/reviews/PHASE_4_REVIEW_11.md:50`–`:59`).

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
   `docs/research/v1/RESEARCH.md:1513`–`:1514`:
   *"`flip.<prog>.<buf>` ...; last writer should leave flip enabled so later passes can read."*
   That narrow read is necessary to make D-P5-4 an actual contract check rather than a citation by
   hearsay.
2. **RESEARCH App A.3 was read for `superSamplingLevel`.**
   `docs/research/v1/RESEARCH.md:1180` defines the directive only as an *"SSAA multiplier"*. The
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
- the binding table that answers **which texture object backs each fixed App B.3 unit per stage**;
  and
- headless state-machine and recorded-GL test contracts for all of the above.

Policy lives in `com.schmaloogium.engine.buffers`; GL object operations use only Phase 1's
`engine.gl` facade. The 1.12.2 depth replacement and providers live in `mod.mixin`/`mod.glue`.
Mixins observe and delegate; no flip, clear, sizing, or fallback policy lives in a mixin.

### 1.2 Explicit adjacent ownership

- **Owned by Phase 1:** `GLDevice`, services, opaque handles, `GLCapabilityProfile`, recording/replay,
  diagnostics, log channels, and bring-up stage 2. Phase 5 requests three narrow facade-contract
  corrections in §5.5 and assumes none.
- **Owned by Phase 3:** parsing and validation of format/clear/mipmap/shadow/supersampling
  directives, `ProgramStateModel`, `ResourceRequirements`, immutable configuration, and its
  fingerprint. Phase 5 never reopens or rescans the pack.
- **Owned by Phase 4:** stage/pass registry, program fallback, effective `ProgramStateBundle`,
  draw-routing values, symbolic `AllUsedBuffers`, and explicit flip configuration. Phase 5 resolves
  physical sides but never stores them back into Phase 4 state.
- **Owned by Phase 6:** sampler-uniform location caching and uploading fixed unit numbers. Phase 5
  supplies a pass-coherent unit→texture snapshot; it does not upload a sampler uniform.
- **Owned by Phase 7:** when frame clears and depth copies occur, pass execution, viewport/state
  setup, final rendering to Minecraft's framebuffer with its anaglyph-aware color mask,
  frame-driver try/finally, runtime display and quality inputs, preparation of Minecraft's shader
  framebuffer at Phase 5's required main extent, and orchestration of registry/estate publication.
- **Owned by Phase 8:** shadow camera, traversal, shadow pass execution, and the moment of the
  shadowtex1 split. Phase 5 supplies sfb and its operations.
- **Owned by Phase 13:** generated noise, `_n`/`_s` companion atlases, and custom texture objects.
  Phase 5 reserves and resolves their fixed-map slots through an overlay contract; it creates none.
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
with `docs/research/v1/RESEARCH.md:526` and
`docs/phase1/v14/PHASE_1_DOC.md:3986`, which warns that `bindDefault` means GL framebuffer name zero,
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
    Optional<Extent2i> shadowExtent,
    int superSamplingLevel) {}

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

public sealed interface BufferPlanResult {
    record Valid(BufferPlan plan) implements BufferPlanResult {}
    record Invalid(BufferFailure failure) implements BufferPlanResult {}
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

public final class BufferEstateCandidate implements AutoCloseable {
    public BufferEstateInspection inspection();
    public void close(); // idempotent while caller-owned; deletes only owned handles
}

public interface BufferEstateInspection {
    RegistryFingerprint registryFingerprint();
    BufferSizing sizing();
    BufferInventory inventory();
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
    Optional<BufferEstateView> estate) {}

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
The three `BufferRuntimeInputs` fields participate by value in planning identity and plan reuse;
there is no separate runtime revision or rebuild trigger.
`BufferPlan` is an opaque immutable value containing the resolved sizing and inventory, ordered
resource descriptors, pass/FBO keys, clear groups, fixed texture-unit rows, and teardown order.
Its value equality covers all those artifacts. `create` does not consume a prior plan: it reruns
the same deterministic planning operation from the planning fields of `BufferBuildRequest`, then
performs GL creation only for a valid result.
`BufferSizing` is structurally value-equal across all three components; `shadowExtent` is empty
exactly when no sfb is planned. `BufferInventory.entries` is immutable, ordered by
`BufferDomain` declaration order and then `BufferIndex`, and contains exactly one row for every
logical texture in the estate; `count(domain)` is the number of rows in that domain. Thus identities,
counts, and final fallback-resolved color/depth formats are consumer-visible without exposing a GL
handle.
`BufferEstateCandidate` is an opaque, compiler-created ownership product. Its pre-publication
`inspection()` is metadata-only: it exposes no generation, snapshots, or mutating estate operation.
Acceptance transfers ownership; caller `close()` becomes harmless. The publisher then assigns the
next generation atomically and creates the sole accepted `BufferEstateView`; that generation is
immutable for the view's lifetime. Rejection leaves the candidate caller-owned. `publish` requires
the exact `RegistryFingerprint` used to build the candidate. That prevents a buffer plan produced
for one effective registry from being paired with another. A mismatch returns
`ProvenanceRejected` without ownership transfer, publication, generation change, or resize
notification; the candidate remains caller-owned. Snapshots and stale checks use only the
publisher-assigned generation of an accepted view.

The non-owning view:

```java
public interface BufferEstateView {
    long generation();
    RegistryFingerprint registryFingerprint();
    BufferSizing sizing();
    BufferInventory inventory();

    MainDepthRefreshResult refreshMainDepth();
    FrameBeginResult beginFrame(long frameId);
    PassSnapshotResult snapshot(PassDescriptor pass, ResolvedProgramDescriptor program);
    PassCompletionResult completePass(PassBufferSnapshot snapshot);
    ClearExecutionPlan clearPlan(ClearRequest request);
    ClearExecutionResult executeClear(ClearExecutionPlan plan);
    DepthCopyResult copyDepth(DepthCopyPoint point, long frameId);
    FrameEndResult commitFrame(long frameId);
    FrameEndResult abortFrame(long frameId, String diagnosticId);

    TextureBindingSnapshot textureBindings(
        PassBufferSnapshot snapshot,
        TextureOverlayLease overlay,
        TextureOverlayPublicationId expectedOverlay);
    ShadowEstateResult shadow();
}

public enum ClearExecutionResult { SUCCESS, STALE_OR_PROTOCOL_REJECTED, BACKEND_FAILED }

public sealed interface MainDepthRefreshResult {
    record Unchanged(long version) implements MainDepthRefreshResult {}
    record Reattached(long version, long depthAttachmentEpoch) implements MainDepthRefreshResult {}
    record ResizeRequired(BufferFailure failure) implements MainDepthRefreshResult {}
    record Failed(BufferFailure failure) implements MainDepthRefreshResult {}
}

public enum DepthCopyPoint { PRE_TRANSLUCENT, PRE_WEATHER }

public enum FrameProtocolRejection {
    STALE_GENERATION,
    STALE_DEPTH_ATTACHMENT_EPOCH,
    FRAME_ALREADY_OPEN,
    NO_OPEN_FRAME,
    WRONG_FRAME_ID,
    NON_NORMALIZED_FLIP_STATE,
    OPEN_PASS_SNAPSHOT,
    INVALID_PASS_SNAPSHOT
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
}

public sealed interface PassCompletionResult {
    record Completed(long frameId) implements PassCompletionResult {}
    record Rejected(FrameProtocolRejection reason) implements PassCompletionResult {}
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
    List<ColorAttachment> colorAttachments,
    Map<LogicalBuffer, TextureHandle> readableTextures,
    Set<LogicalBuffer> flipAfterPass,
    PassDrawTarget drawTarget) {}
```

Every mutating method rejects a stale generation, depth-attachment epoch, or frame token before a GL
call. `snapshot` returns `Acquired` only with a matching current view, open frame, and no open pass;
otherwise it returns the applicable `STALE_GENERATION`, `STALE_DEPTH_ATTACHMENT_EPOCH`,
`NO_OPEN_FRAME`, or `OPEN_PASS_SNAPSHOT` rejection without GL or mutation. `completePass` returns
`Completed` only for the one currently open snapshot after applying its recorded transition.
Acquisition cannot return `WRONG_FRAME_ID`: it accepts no caller frame ID and stamps the installed
open-frame token into the acquired snapshot.
Generation, epoch, and frame mismatches return their corresponding rejection; a foreign,
duplicate, or already-consumed snapshot returns `INVALID_PASS_SNAPSHOT`. Every rejection performs
no GL and leaves the open token and flip state unchanged. `snapshot` returns
`PassDrawTarget.Screen.INSTANCE` exactly for `StageId.FINAL`; all other wired
raster passes return `EngineFramebuffer`. SCREEN carries no framebuffer handle. It is still an open,
generation/epoch/frame-checked snapshot: Phase 7 validates it, performs the platform framebuffer
bind and anaglyph-aware color mask, draws, and requires `Completed` exactly as for an engine target.
Completion consumes the snapshot and applies its recorded flip set; SCREEN invents no extra flip.

### 2.3 Relationship map

```text
PackConfiguration.ResourceRequirements ─┐
ProgramRegistryView / PassDescriptor ───┼─> BufferPlanner (pure)
GLCapabilityProfile + runtime sizing ───┘         │
                                                   v
MainDepthSource ─> BufferEstateFactory ─> BufferEstateCandidate
                                              │ accepted by Phase 7
                                              v
                                      PublishedBufferEstate
                                      │       │        │
                         Pass snapshots       │        └─> Phase 8 sfb view
                                      │       └─> Phase 6 sampler-unit participant
                                      └─> Phase 7 frame/pass driver
```

Phase 7 builds a Phase 4 registry candidate first, uses its read-only view to build a Phase 5
candidate, composes the Phase 4 barrier, publishes Phase 4, and then publishes the already-ready
Phase 5 candidate before permitting another draw. If Phase 4 publication fails, Phase 7 closes the
buffer candidate. Phase 5 publication after a ready candidate is a no-GL ownership swap and cannot
fail for a driver reason; provenance rejection is checked before Phase 4 publication.

## 3. Contract conformance map

### 3.1 Main color buffers and flip behavior

| Contract item | Design element | Provenance |
|---|---|---|
| colortex0 / gcolor, fog clear | §4.6 default table; fog RGB with alpha forced to 1.0 | `[V:doc]` `docs/research/v1/RESEARCH.md:1201`–`:1204`; deployed alpha quirk `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/ClearPassCreator.java]` at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/ClearPassCreator.java:35`–`:42`; D-P5-2 |
| colortex1 / gdepth, white clear, conditional RGBA32F | §4.2 format resolution and §4.6 clear table | `[V:doc]` `docs/research/v1/RESEARCH.md:1204`; Phase 3 already publishes the conditional upgrade at `docs/phase3/v1/PHASE_3_DOC.md:468` |
| colortex2–7 transparent black | §4.6 default table | `[V:doc]` `docs/research/v1/RESEARCH.md:1205`–`:1207` |
| at least 4, up to 8 in G6 | §4.1 capability/count gate and contiguous scan-driven inventory | `[V:doc]` `docs/research/v1/RESEARCH.md:1209` |
| main/alt pair for every color, allocated up front with contract parameters | `ColorPair(A,B)` allocation in §4.3; §4.2 applies `CLAMP_TO_EDGE`, NEAREST for integer formats, and LINEAR otherwise to both sides | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1595`–`:1597`; `[V:doc]` `docs/research/v1/RESEARCH.md:1210`; `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/RenderTarget.java]` at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/RenderTarget.java:36`–`:46`; D-P5-1 |
| gbuffers reads/writes main | `PassMode.GBUFFERS_MAIN` in §4.5 | `[V:doc]` `docs/research/v1/RESEARCH.md:1210`–`:1212` |
| deferred/composite read main, write alt, then flip written buffers | pass snapshot + `completePass` transition in §4.4–§4.5 | `[V:doc]` `docs/research/v1/RESEARCH.md:1210`–`:1212`; D-P5-1 |
| explicit `flip.*`; virtual `*_pre` | transition rows in §4.4 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:33`–`:39`; Phase 3 publication at `docs/phase3/v1/PHASE_3_DOC.md:443`–`:445` |
| last writer leaves flip enabled for later reads | relative flip remains set through final; frame end rebases the physical committed side and clears only the relative bit | `[V:doc]` `docs/research/v1/RESEARCH.md:1513`–`:1514`; D-P5-4 |
| composite blend/read-write rules | Phase 7/4 disable blending while a composite writes color attachments; independently, the buffer snapshot reports a read/write intersection diagnostic | `[V:doc]` `docs/research/v1/RESEARCH.md:1212`–`:1214`; explicit Phase 7 boundary |
| per-buffer clear enable/color overrides and flip-aware clears | §4.6 resolves Phase 3 overrides, preserves mandatory full clears, and selects one/both physical sides from flip state | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1606`–`:1613`; Phase 3 clear policy |
| clear batching by implementation limit | §4.6 groups equal extent/color/side and chunks by `maxDrawBuffers` | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1611`–`:1613` |

### 3.2 Depth and shadow buffers

| Contract item | Design element | Provenance |
|---|---|---|
| depthtex0 = everything, real depth attachment | `MainDepthSource` bridge, safe-point extent preparation, and borrowed attachment in §4.8 | `[V:doc]` `docs/research/v1/RESEARCH.md:1218`–`:1221`; D-P5-5, D-P5-15 |
| depthtex1 = pre-translucent copy | owned copy texture + `PRE_TRANSLUCENT` operation; Phase 7 calls it | `[V:doc]` `docs/research/v1/RESEARCH.md:1221`; §4.9 |
| depthtex2 = pre-weather/no-hand copy | owned copy texture + `PRE_WEATHER` operation; Phase 7 calls it | `[V:doc]` `docs/research/v1/RESEARCH.md:1222`; §4.9 |
| shadowtex0 everything | sfb real depth attachment | `[V:doc]` `docs/research/v1/RESEARCH.md:1223`; §4.10 |
| shadowtex1 pre-shadow-translucent copy | owned shadow split target; Phase 8 calls the copy | `[V:doc]` `docs/research/v1/RESEARCH.md:1224`; §4.10 |
| shadowcolor0/1, future 2–7 | paired `ShadowColorPair` indexed without a hard cap; v0.1 allocation gate ≤2 | `[V:doc]` `docs/research/v1/RESEARCH.md:1225`; growth source `docs/research/v1/RESEARCH.md:385` |
| optional hardware PCF and filter/mipmap | `ShadowTexturePolicy` in §4.10 | `[V:doc]` `docs/research/v1/RESEARCH.md:523`–`:525` |
| legacy shadow-depth `R,R,R,1` sampling swizzle | `ShadowTexturePolicy` in §4.10 applies it to depth textures | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1630`–`:1637`; Pintonium mechanism evidence `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:273`–`:276` |
| real shadow flip semantics | same generic `FlipState` over shadowcolor; no stub | Pintonium B4 is negative evidence at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:785`–`:791`; D-P5-8 |
| main and shadow sizing | checked display × render-quality and shadow-resolution × shadow-quality formulas in §4.11.1 | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1638`–`:1644`; RESEARCH §4.3 |
| `superSamplingLevel` | retained in `BufferSizing` for Phase 7 SSAA execution, without changing allocation extent | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1638`–`:1645`; D-P5-10 |
| resize/recreate triggers and owned invalidation | §4.11.2 trigger matrix and complete owned-object/full-clear/notice checklist | RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1638`–`:1646`; PD §5.3; D-P5-14 |
| Final renders to Minecraft framebuffer | §4.5 returns `SCREEN`; §1.3 requires Phase 7's platform bind and anaglyph-aware color mask before drawing | `[V:doc]` `docs/research/v1/RESEARCH.md:526`; RC3 assignment `docs/design/v2.0-RC3/DESIGN.md:1647`–`:1649` |

### 3.3 Fixed texture-unit map

Section 4.12 reproduces every App B.3 row. The controlling table is
`docs/research/v1/RESEARCH.md:1227`–`:1248`. The shipped-doc contradiction is not hidden:
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:283` says `depthtex1` is unit 12 in one ID
table, while its uniform table says 11 at
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:203` and its composite table says 11 at
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:321`. RESEARCH resolves it at
`docs/research/v1/RESEARCH.md:1250`–`:1254`: **unit 11 is authoritative**. D-P5-9 rejects
Pintonium's dynamic allocation.

### 3.4 Texture formats, pixel formats, and pixel types

| Contract family | Exact handling | Provenance |
|---|---|---|
| 8-bit norm (4) | `R8 RG8 RGB8 RGBA8`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1260` |
| 8-bit signed norm (4) | `R8_SNORM RG8_SNORM RGB8_SNORM RGBA8_SNORM`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1260` |
| 16-bit norm (4) | `R16 RG16 RGB16 RGBA16`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1261` |
| 16-bit signed norm (4) | `R16_SNORM RG16_SNORM RGB16_SNORM RGBA16_SNORM`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1261` |
| 16-bit float (4) | `R16F RG16F RGB16F RGBA16F`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1262` |
| 32-bit float (4) | `R32F RG32F RGB32F RGBA32F`; non-integer null-allocation path | `[V:doc]` `docs/research/v1/RESEARCH.md:1262` |
| 32-bit signed int (4) | `R32I RG32I RGB32I RGBA32I`; integer transfer path, NEAREST | `[V:doc]` `docs/research/v1/RESEARCH.md:1263` |
| 32-bit unsigned int (4) | `R32UI RG32UI RGB32UI RGBA32UI`; integer transfer path, NEAREST | `[V:doc]` `docs/research/v1/RESEARCH.md:1263` |
| mixed (5) | `R3_G3_B2 RGB5_A1 RGB10_A2 R11F_G11F_B10F RGB9_E5` | `[V:doc]` `docs/research/v1/RESEARCH.md:1264` |
| null-allocation transfer layout | non-integer → `BGRA`; integer → `RGBA_INTEGER`; both use `UNSIGNED_INT_8_8_8_8_REV` with null data | `[V:observed]` `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:209`–`:213`; D-P5-13 |
| pixel-format vocabulary | six normalized channel orders and their six `_INTEGER` variants; integer internal formats require integer layout | `[V:doc]` `docs/research/v1/RESEARCH.md:1266`–`:1267` |
| pixel-type vocabulary | complete shipped list retained in `PixelType`; canonical allocation subset does not narrow raw-texture types | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:512`–`:533` |
| incomplete framebuffer | delete the entire candidate and retry all color pairs with `RGBA_COMPAT`; warn; second failure → shaders off | `[V:observed]` `docs/research/v1/RESEARCH.md:517`–`:520`; D-P5-3 |

The eight four-member families plus five mixed formats total exactly 37. Pintonium's additional
8/16-bit integer internal formats are not admitted; they are absent from Appendix B.4.

### 3.5 Draw-buffer prefixes and growth

Phase 3 already canonicalizes every B.5 prefix into an index
(`docs/phase3/v1/PHASE_3_DOC.md:487`–`:493`), and Phase 4 preserves ordered routing
(`docs/phase4/v1/PHASE_4_DOC.md:869`–`:878`). Phase 5 consumes only `BufferRef`; it never parses
`gcolor` or `DRAWBUFFERS`. The growth model accepts non-negative indices and sparse route sets, but
v0.1's G6 realization rejects a required colortex index above 7 with a named unsupported-post-v0.5
failure rather than silently dropping it. `RENDERTARGETS`, 16/32 colortex, custom images, SSBOs, and
shadowcolor2–7 retain their future identities from
`docs/research/v1/RESEARCH.md:368`–`:385`.

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
| stubbed shadow `flip()` | reject | PD §17 B4 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:785`–`:790`; contract-visible state must be real; D-P5-8 |
| dynamic sampler units | reject | PD §6.5 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:342`–`:356` and PD §18 texture-unit row at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808`; fixed App B.3; D-P5-9 |

## 4. Detailed design

### 4.1 Pure plan derivation and capability gate

`BufferPlanner` consumes only immutable values. Its result contains no `GLHandle` and is stable for
equal inputs.

Planning steps:

1. verify Phase 3 schema/fingerprint and Phase 4 registry fingerprint;
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

A successful call returns `BufferPlanResult.Valid(BufferPlan)` with the immutable artifacts from
step 10. A validation failure returns `BufferPlanResult.Invalid(BufferFailure)` without a GL call.
Capability failure is a pack-level `ERROR/CHAT` on `schmaloogium.buffers` and leaves shaders off.
Candidate creation independently repeats these steps from `BufferBuildRequest`; equal planning
fields must yield a value-equal plan before any GL allocation.

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

`ColorInternalFormat` has 38 values: the 37 pack-facing names plus private
`RGBA_COMPAT`, used only by fallback. It never accepts Pintonium-only additions.

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

All color textures use `CLAMP_TO_EDGE` for S/T. Min and mag filters are both NEAREST for integer
formats and LINEAR otherwise. Mipmap allocation/generation is not implicit: Phase 7 requests
per-pass mipmap generation through the Phase 1 capability-gated verb. A texture begins with only
level zero.

Phase 1's `TextureSpec` slot is a closed tagged value populated here:

- `ColorTextureSpec(ColorInternalFormat, PixelFormat, PixelType, extent, mipLevels)`; or
- `DepthTextureSpec(DepthAttachmentFormat, DepthTransferLayout, extent, mipLevels)`.

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

1. resolve the effective Phase 4 provider and state;
2. freeze `read/main` for every readable buffer;
3. freeze `write/alt` for every deferred/composite write;
4. freeze `read/main` as the gbuffers write target;
5. build the FBO and unit-binding snapshot from those same handles; and
6. calculate, but do not yet apply, the post-pass flip set.

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

`Acquired` is the only permission to bind or draw. Phase 7 corrects no/open-frame ordering or
reacquires the current publication on stale acquisition rejection. `Completed` is the success boundary; Phase 7 may
advance only on it, and on completion rejection aborts the frame before applying the §6 recovery
for that rejection. Constructing or binding a snapshot never changes flip state. This makes a
skipped draw unable to expose an unwritten alt side.

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

`commitFrame` requires the matching open frame and no open pass snapshot. `Committed` performs the
rebase above, clears the frame token, and leaves normalized flip state. A protocol rejection changes
nothing. `abortFrame` has the same token checks, but intentionally consumes any open pass snapshot,
rebases the current readable side, clears every relative flip and the frame token, marks
`fullClearRequired`, and returns `Aborted` with the caller's sanitized diagnostic ID. It cannot undo
texels already written, so the next frame's mandatory full clear is the safety boundary. If backend
work needed by either end operation fails, `BackendFailed` clears the token, normalizes by the same
rebase, requires full clear, makes the estate stale, and enters §6 shaders-off recovery. Phase 7
must branch on every closed result: proceed only on `Begun`/`Committed`, retain the diagnostic and
honor the mandatory clear on `Aborted`, correct protocol ordering on `Rejected`, and publish
shaders-off before another shader draw on `BackendFailed`.

The same generic machine is used for shadowcolor pairs. There is no `ShadowRenderTargets.flip()`
stub or hardcoded “read main” shortcut.

### 4.5 Phase 4 route realization

Phase 4 exposes exact/symbolic writes and explicitly warns:
*"Phase 5 must not infer a resolved ping-pong side from `explicitFlips`; it owns the side state"*
(`docs/phase4/v1/PHASE_4_DOC.md:1187`). Phase 5 obeys that division.

`PassBufferPlanner`:

- expands `AllUsedBuffers(COLORTEX)` to the estate's ascending allocated set;
- preserves an `Explicit` route's list order;
- uses the effective provider's entire `ProgramStateBundle`, never requested-slot overlays;
- merges Phase 4's logical pass resource access with the current Phase 5 snapshot;
- reports a read/write intersection as a pack diagnostic but preserves the contract behavior;
- returns `PassDrawTarget.Screen.INSTANCE` and no engine FBO for `StageId.FINAL`; that terminal
  requires Phase 7 to bind
  Minecraft's framebuffer and apply Minecraft's anaglyph-aware color mask before the draw; and
- retains dormant `SHADOWCOMP`, `PREPARE`, `BEGIN`, and `SETUP` identities without wiring them.

Pass FBO attachment updates occur before binding. `FramebufferService.check` runs at candidate build
and after a main-depth version reattachment, not on every bind.

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

Side rules:

| Condition | Physical sides cleared | Flip change |
|---|---|---|
| full clear | A and B | none |
| normal clear, `flipped=false` | current read/main side | none |
| normal clear, `flipped=true` | both sides | none |
| clear disabled, normal clear | none | none |

The third row is the governing *"clears both sides when flipped"* behavior
(`docs/research/v1/RESEARCH.md:520`–`:522`). A clear never toggles.

Clear operations group only buffers with equal `(extent, color, side)` and split each group into
chunks no larger than `maxDrawBuffers`. This adopts the deployed batching shape verified at
`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/targets/ClearPassCreator.java:59`
–`:67` and keeps differently sized future buffers separate.

Phase 7 owns the depth clear moment and Minecraft state bracketing. At that moment it calls
`executeClear(clearPlan(request))` on the render thread. The immutable plan carries estate
generation, attachment epoch, frame token, and ordered color-clear batches. `executeClear` rejects
a foreign, stale, previously executed, or wrong-frame plan before GL; otherwise Phase 5 executes
the batches exactly once and restores the prior framebuffer binding and viewport through Phase 1's
snapshot/restore discipline on success or failure. Its result names success, stale/protocol
rejection, or backend failure; it never clears depth or escapes an exception.

`clearPlan` derives effective full-clear intent as
`request.fullClear || estate.fullClearRequired`; the caller cannot suppress estate policy. The bit
is cleared only after every batch in that plan succeeds for its validated generation. Rejection,
partial execution, or backend failure retains or re-establishes it, so the next valid plan remains
a full clear.

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
   and, when enabled, stencil; delete it with the owning Minecraft framebuffer; expose an opaque
   Phase-5 marker handle and monotonically changing version.
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

public interface BorrowedDepthAttachmentHandle extends TextureHandle {}
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

The existing Phase 1 contract does **not** permit step 2: its §5 says a foreign texture is illegal
for `FramebufferService.attachDepth` (`docs/phase1/v14/PHASE_1_DOC.md:3970` and
`docs/phase1/v14/PHASE_1_DOC.md:3980`). Phase 5 therefore requests the narrow correction in §5.5.
Until that request is fixed up and re-verified, the design is complete but implementation of the
depth bridge is blocked; no wider foreign-handle permission is assumed.

Combined depth-stencil also requires an explicit facade operation that attaches the same texture to
both depth and stencil. The second §5.5 request is additive and equally narrow. The mandatory
first-copy operation is not expressible separately from Phase 1's steady-copy verb; §5.5 requests
that third distinction rather than assuming backend state heuristics.

### 4.9 depthtex1/depthtex2 copies

Both copy targets:

- are Phase-5-owned textures;
- match main depth's extent and depth/depth-stencil format;
- use NEAREST + CLAMP_TO_EDGE;
- are reallocated and marked uninitialized on size/format/version change; and
- are never substituted for depthtex0's attachment.

The first copy after allocation/reallocation uses the requested
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

Backend operations restore read/draw framebuffer and texture bindings before return, as Phase 1's
facade requires. A pure `DepthCopyStrategySelector` is tested with independent
`copyImageCallable`, `blitAvailable`, and `combinedStencil` booleans; those booleans are
`mod.glue` backend facts and do not leak GL constants into `:engine`.

`copyDepth(point, frameId)` permits each point at most once in order:

```text
FRAME_BEGUN -> PRE_TRANSLUCENT_COPIED -> PRE_WEATHER_COPIED -> FRAME_COMMITTED
```

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
`FailedSafe` for the current shader frame. A first-copy failure or recurring copy failure binds
depthtex0 as the temporary backing for the affected unit, reports a feature-local degradation, and
marks that copied-depth view unavailable to conformance diagnostics. It never exposes stale
previous-frame contents as current.

### 4.10 Shadow estate

The sfb is absent unless Phase 3 reports at least one shadow depth buffer. When present:

- allocate shadowtex0, plus shadowtex1 only when required;
- allocate zero to two shadowcolor pairs at v0.1/v0.2;
- size all at §4.11's shadow extent;
- attach shadowtex0 as real depth;
- apply per-depth hardware compare mode when requested;
- use the legacy `R,R,R,1` sampling swizzle for depth textures;
- apply per-texture NEAREST/LINEAR and mipmap flags from Phase 3; and
- expose Phase 8 operations to bind, clear, copy shadowtex1, and snapshot shadowcolor sides.

The old-pack swizzle and hardware compare are structurally corroborated at
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:273`–`:276`. The immediately following stub is
not inherited.

Shadow depth is not ping-ponged at v0.2: shadowtex0 is real depth and shadowtex1 is the translucent
split copy. Shadowcolor uses the generic pair/flip state now, even though shadowcomp execution is
post-v0.5. This makes the future `SHADOWCOMP` stage a new schedule population, not a new buffer
architecture.

When no sfb is planned, Phase 8 receives `ShadowEstateNotRequested`; this is ordinary absence and
does not create a `BufferFailure`. If sfb creation fails while main dfb succeeds, the shadow
feature is disabled, neutral shadow bindings are supplied, and the program/main pipeline may
continue (rung 2a). The candidate owns one
1×1 fully-far depth fallback and one 1×1 opaque-white color fallback, configured to the pack's
compare/filter expectations; units 4/5 and 13/14 reuse those objects when their real target is
absent. Phase 8 receives `ShadowEstateUnavailable`, not a partial sfb.

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
        long frameId, PassDescriptor pass, ResolvedProgramDescriptor program);
    ShadowOperationResult bind(ShadowPassSnapshot snapshot);
    ShadowOperationResult clear(ShadowPassSnapshot snapshot, ClearRequest request);
    ShadowOperationResult copyDepth(
        ShadowPassSnapshot snapshot, ShadowDepthCopyPoint point);
    ShadowCompletionResult completePass(ShadowPassSnapshot snapshot);
    ShadowAbortResult abortPass(ShadowPassSnapshot snapshot, String diagnosticId);
}

public record ShadowPassSnapshot(
    long estateGeneration,
    long depthAttachmentEpoch,
    long frameId,
    PassDescriptor pass,
    ResolvedProgramDescriptor program,
    FramebufferHandle framebuffer,
    List<ColorAttachment> colorAttachments,
    Map<LogicalBuffer, TextureHandle> readableShadowcolorTextures,
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
```

`beginPass(frameId,pass,program) -> ShadowBeginResult` acquires the sole open
`ShadowPassSnapshot` or returns `Rejected`; a call while one is open returns
`Rejected(PASS_ALREADY_OPEN)` before GL and without mutation. `bind(snapshot)`,
`clear(snapshot,request)`, and
`copyDepth(snapshot,SHADOW_PRE_TRANSLUCENT)` each return `ShadowOperationResult`; and
`completePass(snapshot)` / `abortPass(snapshot,diagnosticId)` return their corresponding closed
result.

All calls are render-thread-only. Validation returns the exact `ShadowProtocolRejection` before GL
with no token, full-clear, or flip mutation. `Applied` means the requested bind, clear, or copy
finished; `BackendFailed(BufferFailure)` is possible only for those three GL operations, leaves the
token open and flip state unchanged, and requires Phase 8 to call `abortPass`. `Completed` consumes
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

`superSamplingLevel` is retained as a positive `SupersamplingPlan.level` in `BufferSizing`, but it
does **not** multiply texture extent. The assigned behavioral digest separates buffer extent
(display × render quality at
`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:209`–`:213`) from the SSAA multiplier
(`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:173`). Phase 7 owns the SSAA
draw/sample sequence. This ruling is D-P5-10 and the upstream clarification request remains in
§11.5.

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

### 4.12 Fixed texture-unit object table

The table reproduces App B.3 exactly. `Foreign(key)` uses Phase 1's bind-only provider;
`Owned(x)` is a Phase-5-owned current-side texture; `OwnedOrNeutral(x,k)` selects the real
Phase-5-owned shadow object or the §4.10 neutral object of kind `k`; `Overlay(x,fallback)` permits a
Phase 13 object at that stage and otherwise uses the stated fallback;
`CopyOrMain(copy,depthtex0)` applies §4.9's initialization/failure state; `Missing` reports a
`MissingTextureBinding`; and `Absent` means Phase 6 still assigns no different number and the
binding plan reports no object.

`TextureOverlayKey` is closed as `NORMALS`, `SPECULAR`, `GAUX1`…`GAUX4`, or `NOISE`;
`TextureOverlayAbsence` is closed as `NOT_CONFIGURED`, `NOT_APPLICABLE_TO_STAGE`, or
`PUBLICATION_UNAVAILABLE`. `TextureOverlayEntry` is exactly `Present(TextureHandle)` or
`Absent(TextureOverlayAbsence)`. An immutable `TextureOverlaySnapshot` exposes
`TextureOverlayPublicationId id()`, `RegistryFingerprint registryFingerprint()`, and total
`entry(StageId,TextureOverlayKey)` lookup; the publication ID is `(long generation,
TextureOverlayFingerprint contentFingerprint)`, where the fingerprint is an opaque Phase-13 value.

Phase 7 acquires a Phase-13-owned `TextureOverlayLease extends TextureOverlaySnapshot,
AutoCloseable`, an immutable snapshot view, and passes it with the `TextureOverlayPublicationId`
read from the same atomic Phase 13 publication to `textureBindings`. Phase 5 requires the lease ID
to equal that expected ID and its
`RegistryFingerprint` to equal the estate's `registryFingerprint`; mismatch yields
`MissingTextureBinding` and no draw. A successful `TextureBindingSnapshot` retains the lease.
`TextureBindingSnapshot` is `AutoCloseable` with idempotent `close`. Phase 7 closes it in the
enclosing draw's `finally`, after the draw completes; closure releases the lease and is Phase 13's
deterministic permission to destroy referenced handles. Phase 13 retains registry, allocation,
upload, ownership, and destruction.

| Unit | GBUFFERS / SHADOW backing object | DEFERRED / COMPOSITE / FINAL backing object |
|---:|---|---|
| 0 | `Foreign("texture")` (shadow alias `tex`) | `Owned(colortex0.read)` |
| 1 | `Foreign("lightmap")` | `Owned(colortex1.read)` |
| 2 | `Overlay(Phase13 normals companion atlas, Missing)` | `Owned(colortex2.read)` |
| 3 | `Overlay(Phase13 specular companion atlas, Missing)` | `Owned(colortex3.read)` |
| 4 | `OwnedOrNeutral(shadowtex0, depth)`; aliases `watershadow`, or `shadow` without water split | same |
| 5 | `OwnedOrNeutral(shadowtex1, depth)`; alias `shadow` with water split | same |
| 6 | borrowed `depthtex0` | borrowed `depthtex0`; alias `gdepthtex` |
| 7 | `Overlay(stage custom gaux1, colortex4.read)` | `Owned(colortex4.read)` |
| 8 | `Overlay(stage custom gaux2, colortex5.read)` | `Owned(colortex5.read)` |
| 9 | `Overlay(stage custom gaux3, colortex6.read)` | `Owned(colortex6.read)` |
| 10 | `Overlay(stage custom gaux4, colortex7.read)` | `Owned(colortex7.read)` |
| 11 | `CopyOrMain(depthtex1, depthtex0)` | `CopyOrMain(depthtex1, depthtex0)` |
| 12 | `Absent` | `CopyOrMain(depthtex2, depthtex0)` |
| 13 | `OwnedOrNeutral(shadowcolor0.read, color)`; alias `shadowcolor` | same |
| 14 | `OwnedOrNeutral(shadowcolor1.read, color)` | same |
| 15 | `Overlay(NOISE, Missing)` | `Overlay(NOISE, Missing)` |

Missing unallocated optional buffers yield an explicit `MissingTextureBinding`; they never index
past an array or cause dynamic unit reassignment. Phase 7/Phase 6 may then degrade the affected
program or feature according to §6.

For unit 15 at every listed stage, `NOISE -> Present(handle)` binds exactly that handle.
`Absent(NOT_CONFIGURED)`, `Absent(NOT_APPLICABLE_TO_STAGE)`, and
`Absent(PUBLICATION_UNAVAILABLE)` each produce `MissingTextureBinding`; no fallback object is
substituted.

`TextureBindingSnapshot` contains the estate generation, depth-attachment epoch, frame/pass
identity, and immutable unit→handle rows. It closes over the exact `PassBufferSnapshot`, so Phase 6
cannot observe a flip transition between attaching an output and binding its sampler inputs. Phase
6 uploads sampler integers; Phase 5 or Phase 7 executes `TextureService.bindToUnit` from this
snapshot before the draw. The Phase 7 composition must order the object binds before Phase 6's
sampler-uniform upload inside Phase 4's sampler participant.

The map is never searched for a free unit. Pintonium's dynamic unit allocation is the pre-decided
rejection in §G11.4 and is not an alternative implementation.

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
| `BufferArchitecture.plan/create`, `BufferPlanRequest`, `BufferBuildRequest`, `BufferRuntimeInputs`, `BufferPlan`, `BufferPlanResult`, `BufferBuildResult`, `BufferFailure` | Phase-7-owned immutable configuration/registry/fingerprint/capability inputs plus runtime display extent, render quality, and shadow quality; all three runtime fields participate by value in planning identity and reuse, with no separate runtime revision or rebuild trigger; closed valid/invalid pure planning with an immutable resolved-artifact plan; `create` independently reruns identical planning from its request before render-thread creation; ready/awaiting-depth/closed-failure build results, no partial publication | Phase 7 bootstrap/reload; Phase 2 tests |
| `BufferEstateCandidate`, `BufferEstateInspection`, `BufferEstatePublisher`, `PublishedBufferEstate`, `BufferPublicationResult` | candidate inspection exposes only registry fingerprint, sizing, and inventory, never a generation or estate operation; Phase 7 validates that fingerprint against the Phase 4 candidate before publishing Phase 4, composes and publishes Phase 4 first, closes its still-owned Phase 5 candidate if Phase 4 publication fails, then publishes the ready Phase 5 candidate and permits no shader draw until both publications complete; acceptance atomically transfers ownership, assigns the next generation, and creates the sole generation-bearing view; accepted generation is immutable and is the only generation snapshots/stale checks use; `ProvenanceRejected` leaves the candidate caller-owned and changes no publication state; `ConsumerFailed` exposes failed/off generations, stable consumer identity, and the count of preceding successful acknowledgements, excluding the failing callback | Phase 7; Phase 12 indirectly through Phase 7 |
| `BufferEstateView`, `BufferSizing`, `BufferInventory`, `BufferInventoryEntry`, `ResolvedBufferFormat` | accepted immutable non-owning estate metadata and publisher-assigned generation; structural sizing equality over exact main extent, optional shadow extent, and supersampling level; immutable domain/index-ordered logical inventory with per-domain counts and final resolved color/depth format; no GL handles | Phases 6, 7, 8, 13, 14 |
| `refreshMainDepth`, `MainDepthRefreshResult` | public render-thread comparison against the published estate; closed unchanged/reattached/resize-required/failed outcomes; successful same-extent reattachment invalidates open snapshots but permits same-frame continuation only after Phase 7 abandons them and reacquires pass/binding snapshots; resize-required carries exactly `BufferFailureCode.MAIN_DEPTH_RESIZE_REQUIRED`, performs no GL or mutation, and requires abort/normalize plus prepare/build/publication; every failed outcome advances the attachment epoch, retains the cached prior identity, makes the estate stale/unusable, and requires abort/normalize plus shaders-off publication until safe-point replacement succeeds; no shader draw is permitted during either recovery | Phase 7 |
| `FrameProtocolRejection`, `FrameBeginResult`, `FrameEndResult`, `PassSnapshotResult`, `PassCompletionResult`, `PassDrawTarget`, `PassBufferSnapshot`, `completePass`, `commitFrame`, `abortFrame` | closed generation/attachment-epoch/frame-checked lifecycle: begin succeeds with the installed token or rejects/fails without opening a frame; snapshot acquisition returns `Acquired` or an applicable stale/epoch/no-frame/open-pass rejection; completion returns `Completed` or the applicable stale/epoch/wrong-frame/invalid-snapshot rejection, with every rejection observable and mutation-free; commit requires the matching token and no open snapshot, rebases and normalizes on success; abort consumes an open snapshot, rebases, normalizes, and requires full clear; backend end failure performs the same safe normalization, makes the estate stale, and requires shaders-off recovery. Phase 7 binds/draws only on `Acquired`, advances only on `Completed`, corrects ordering or reacquires the current publication after acquisition rejection, and aborts the frame on completion rejection; it proceeds only on begun/committed, honors aborted diagnostics/full clear, and publishes off on backend failure. One immutable side snapshot drives bindings and carries a closed draw target: wired raster passes use `EngineFramebuffer(handle)`, while `StageId.FINAL` uses `Screen.INSTANCE` with no engine handle; Phase 7 performs the platform bind and anaglyph-aware color mask, then requires `Completed` for SCREEN under the same validation and flip rules. Dormant `SHADOWCOMP`, `PREPARE`, `BEGIN`, and `SETUP` identities remain unwired | Phase 7; G8/S1 |
| `ClearRequest`, `ClearExecutionPlan`, `executeClear`, `ClearExecutionResult` | effective full clear is caller intent OR estate-owned requirement; generation/epoch/frame-checked, exactly-once color-clear execution consumes that requirement only after every batch succeeds; exact colors, side rules, batching, and guaranteed framebuffer/viewport restoration | Phase 7 |
| `DepthCopyPoint`, `copyDepth`, `DepthCopyResult` | PRE_TRANSLUCENT/PRE_WEATHER mechanics and ordering; caller owns moments | Phase 7 |
| `BufferDomain`, `BufferIndex`, `LogicalBuffer`, `ColorAttachment` | immutable `BufferDomain { COLORTEX, SHADOWCOLOR, SHADOWTEX, DEPTH }`; `BufferIndex(int value)` rejects negative values; `LogicalBuffer(BufferDomain domain, BufferIndex index)`; `ColorAttachment(int outputOrdinal, int framebufferAttachment, LogicalBuffer logicalBuffer, TextureHandle physicalTexture)` | Phase 8; Phase 7 |
| `ShadowEstateResult`, `ShadowEstateAvailable`, `ShadowEstateNotRequested`, `ShadowEstateUnavailable`, `ShadowEstateView`, `ShadowPassSnapshot`, `ShadowProtocolRejection`, `ShadowDepthCopyPoint`, `ShadowBeginResult`, `ShadowOperationResult`, `ShadowCompletionResult`, `ShadowAbortResult` | `shadow()` returns exactly `ShadowEstateAvailable(view)` when planned and created, `ShadowEstateNotRequested(estateGeneration)` when no sfb was planned, or `ShadowEstateUnavailable(BufferFailure,estateGeneration)` after creation failure; ordinary absence never creates a `BufferFailure`. The view exposes its generation plus typed begin/bind/clear/`SHADOW_PRE_TRANSLUCENT` split-copy/complete/abort methods. The immutable snapshot carries generation, depth-attachment epoch, frame/pass/program identity, sfb handle, color attachments, readable shadowcolor handles, and `flipAfterPass`. Acquisition returns `Acquired(snapshot)` or `Rejected(reason)`, including exact `PASS_ALREADY_OPEN` for a second begin; operations return `Applied`, `Rejected(reason)`, or `BackendFailed(BufferFailure)`; completion returns `Completed(frameId)` or `Rejected(reason)`; abort returns `Aborted(frameId,diagnosticId,fullClearRequired)` or `Rejected(reason)`. Every named top-level shadow type is public. Every validation rejection is pre-GL and mutation-free; backend failure leaves the token open and flip unchanged for mandatory abort; completion alone applies flip, while abort closes without flip and requires full clear. Phase 8 continues only on success, corrects ordering or reacquires after rejection, and aborts on backend failure | Phase 8; G8/S1 |
| `TextureOverlayLease`, `TextureOverlaySnapshot`, `TextureOverlayPublicationId`, `TextureOverlayFingerprint`, `TextureOverlayKey`, `TextureOverlayEntry`, `TextureOverlayAbsence`, `TextureBindingSnapshot`, fixed App B.3 table, `MissingTextureBinding` | closed total Phase 13 stage/key lookup; exact publication-ID and estate-registry-fingerprint checks; successful binding snapshot retains the lease until Phase 7 closes it after the enclosing draw; pass-coherent fixed unit→texture objects; unit 15 resolves `NOISE` `Present(handle)` to that handle and each of the three closed absence values to `MissingTextureBinding`; no uniform upload or dynamic allocation | Phase 7 composition; Phase 6 sampler participant; Phase 13 overlay |
| `BufferResizeNotice`, `BufferResizeReason`, `BufferResizeConsumer`, `ResizeConsumerResult`, `BufferResizeRegistrationResult`, `BufferResizeRegistration`, `BufferResizeRegistrationRejection` | notice exposes old/new structural sizing, new generation, and one closed rebuild reason with declaration-order priority for simultaneous changes; registration returns `Registered` or a stable rejected reason for blank ID, duplicate live ID, future generation, unknown generation, or sizing mismatch; ready generation→sizing facts remain known for the publisher lifetime, off/never-installed generations are unknown, and supplied sizing must exactly equal the known fact; rejection installs nothing and changes no acknowledgement/publication state; accepted identity is retained unchanged and returned by `ConsumerFailed`; render-thread removal, per-consumer acknowledged sizing/generation, and ordered synchronous delivery after install and before drawing; retry `oldSizing` is each consumer's truthful acknowledged baseline; first failure stops dispatch, disposes the unopened estate, and returns the consequent off publication | Phases 13 and 14; G8/S2 |
| `MainDepthSource`, `MainDepthPreparation`, `MainDepthSnapshot`, `BorrowedDepthAttachmentHandle` | engine-side SPI implemented by `mod.glue`; safe-point platform-FBO preparation plus opaque depth identity/version/format/extent, no GL name | Phase 7 installs/prepares; Phase 5 consumes |
| `ColorInternalFormat`, `PixelFormat`, `PixelType`, `DepthAttachmentFormat`, `DepthTransferLayout` | exact App B.4 vocabulary plus private color fallback/depth allocation values; integer classification and allocation layout | Phase 5 facade calls; Phase 13 raw-upload adapter |

### 5.2 Consumed Phase 1 contracts

| Phase 1 §5 contract | Use |
|---|---|
| module layout, C-1…C-4, package and `.internal` rules | all pure policy and glue placement |
| engine bring-up stage 2 | install the main-depth/foreign texture providers only after GL capabilities exist |
| `GLDevice` and seven services | create/allocate/parameterize/bind/delete textures and FBOs, clear, snapshot/restore, labels |
| `FramebufferService.copyDepthToTexture` | depthtex1/depthtex2 and Phase 8 shadow split |
| `GLCapabilityProfile` and serialization | limits, extents, format/copy strategy fixtures |
| opaque handle lifetime | candidate ledger, generation/stale-view rules, re-acquisition |
| `ForeignTextureProvider` | bind-only unit-map sources; never attachment or deletion |
| `RecordingGLDevice`, `ScriptedResponses`, `GLCallLog`, `ReplayAssertions` | fallback, state-machine call sequence, leaks, use-after-delete |
| diagnostic/log conventions | `schmaloogium.buffers`, `.gl`; chat/GUI/log routing |

No Phase 1 operation is widened silently. The three required corrections are §5.5.

### 5.3 Consumed Phase 3 contracts

| Phase 3 §5 contract | Use |
|---|---|
| `PackConfiguration`, schema/fingerprint | sole pack/config truth and rebuild identity |
| `ProgramStateModel` | explicit flip values retained through Phase 4 |
| `ResourceRequirements` | color/depth/shadow counts, formats, clear policy, routing minima, shadow policy, supersampling |
| `MacroConfiguration` | no parsing; only configuration fingerprint participates |
| immutable publication/version discipline | no pack reopen/rescan or retained parser builders |

Runtime display/render/shadow quality values are Phase-5 `BufferRuntimeInputs` supplied later by
Phase 7. Those three values are the complete runtime portion of planning identity; Phase 5 does
not assume a separate revision or that they are fields Phase 3 currently exposes.

### 5.4 Consumed Phase 4 contracts

| Phase 4 §5 contract | Use |
|---|---|
| `StageRegistry`, `StageStep`, `PassDescriptor` | deterministic pass-plan traversal |
| `PassResourceAccess` | readable, exact/symbolic writes, explicit logical flips, mipmap declarations |
| `ProgramStateBundle` | routing, effective provider flip/scale/state data |
| `ProgramRegistryView.resolve`, `ResolvedProgramDescriptor` | whole effective provider; no fallback re-resolution |
| `CompiledRegistryCandidate.view()` | non-owning pre-publication `ProgramRegistryView` used by Phases 5 and 7 to derive and validate the buffer candidate before Phase 4 publication; candidate ownership and provenance remain unchanged |
| `RegistryFingerprint` | estate derivation identity/publication pairing |
| generation/stale-view discipline | Phase 7 orchestration and invalidation |

Phase 5 never reaches a `ProgramHandle`, mutates registry state, or writes a physical side into a
Phase 4 type.

### 5.5 Requested changes to dependency contracts

Three Phase 1 changes are required before implementation:

1. **Borrowed depth attachment permission.** Add the Phase-5-owned
   `BorrowedDepthAttachmentHandle extends TextureHandle` marker to the Phase 1 foreign-handle
   contract. A backend-authenticated handle of that marker may be passed to
   `FramebufferService.attachDepth` and the new combined operation below. It remains illegal for
   `TextureService.allocate`, `setParameters`, `upload`, `generateMipmap`, `delete`,
   `FramebufferService.attachColor`, and `copyDepthToTexture` destination use. Ordinary
   `ForeignTextureProvider` handles remain bind-only. The backend must reject forged or wrong-origin
   marker handles. This narrow exception is safe specifically because Phase 5 consumes the exposed
   version and reattaches on every identity change.
2. **Combined depth-stencil attachment.** Add
   `FramebufferService.attachDepthStencil(FramebufferHandle, TextureHandle)`, with the contract that
   the same combined texture is attached to both depth and stencil and prior bindings are restored.
   `attachDepth` remains depth-only. The current surface exposes only that verb at
   `docs/phase1/v14/PHASE_1_DOC.md:2778`–`:2782`. Recording/replay must distinguish the operation.
3. **First depth-copy initialization.** Add
   `FramebufferService.initializeDepthTextureFromFramebuffer(FramebufferHandle, TextureHandle,
   TextureRegion)`. It redefines the destination level and extent with the source framebuffer's
   exact depth/depth-stencil format and contents using `glCopyTexImage2D` semantics, restores prior
   bindings, and is legal only for an owned destination. Phase 5 calls it on the first copy after
   creation/reallocation; subsequent copies use the existing `copyDepthToTexture` verb and its
   capability-tiered backend, whose current contract is
   `docs/phase1/v14/PHASE_1_DOC.md:2810`–`:2815`. Recording and replay must distinguish
   initialization from a steady copy.

All three are additive but alter Phase 1 §5 and therefore require a Phase 1 fix-up plus a fresh
verify round before Phase 5 implementation or any dependent implementation consumes the amended
surface. This document assumes none is already granted.

No Phase 3 contract change is requested.

Phase 4 §5 must add the already-defined public `CompiledRegistryCandidate.view()` as a non-owning
pre-publication `ProgramRegistryView` consumed by Phases 5 and 7. This is a binding-surface
clarification only: it preserves the candidate's existing opacity, caller ownership before
acceptance, compiler-origin credential, publication provenance checks, and absence of a public
registry accessor. Phase 5 assumes no new inspection API and does not treat this clarification as
already granted.

## 6. Failure modes & degradation

| Failure | Required behavior | G2.4 rung |
|---|---|---:|
| malformed/null request, stale schema/fingerprint, invalid route | return closed failure before GL; keep/publish shaders off | 4/5 |
| requested color formats produce allocation/FBO failure | delete whole attempt, retry all colors as `RGBA_COMPAT`, user-visible warning | 2a |
| RGBA fallback also fails | delete partial estate; publish shaders off; vanilla path remains | 4→5 |
| main depth unavailable or borrowed-attachment contract not granted | mark any published estate stale, advance its attachment epoch, abort/normalize any active frame, and publish shaders off; do not create a false depthtex0; require successful safe-point replacement before another shader draw | 4 |
| prepared main-depth extent differs from planned main extent | attach nothing; remain on vanilla path and repeat safe-point prepare/build, with bounded retry diagnostics | 4/5 containment |
| same-extent main-depth version changes mid-frame | successful reattachment invalidates the open snapshot; abandon it and reacquire pass/binding snapshots before continuing; failure follows the shaders-off recovery above | 5 containment |
| main-depth reattachment/check fails | retain the advanced epoch and prior cached identity, mark the partially mutated estate stale/unusable, abort/normalize any active frame, publish shaders off, and require safe-point replacement before another shader draw | 5 containment |
| one depthtex1/2 copy fails | bind current depthtex0 as non-stale fallback for that unit, disable copied-depth feature view, warn | 2a |
| combined stencil has no legal copy tier | copied-depth feature unavailable; pack requiring the split may gate off, otherwise bind depthtex0 fallback | 2a/4 |
| sfb creation fails while dfb is healthy | disable shadow estate only; Phase 8 skips; bind neutral shadow objects | 2a |
| clear call fails | abort shader frame, mark full clear; recurring or unrecoverable failure publishes off | 2a→4 |
| missing Phase 13 overlay/noise/companion texture | explicit missing binding; affected sampler/program degrades, never steals a unit | 2a/3 |
| missing vanilla `texture`/`lightmap` foreign handle | affected draw is skipped/fixed-fallback through Phase 4/7; no null/raw handle | 3 |
| pass acquisition rejection | no GL work; correct ordering for no/open-frame rejection, or reacquire the current publication for stale generation/epoch | local protocol failure |
| pass completion rejection | no GL work or flip mutation; abort the frame, then correct ordering or reacquire the current publication according to the rejection | local protocol failure |
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
- No chunk-build worker state enters this phase.

### 7.2 Allocation posture

The steady frame allocates no textures, FBOs, collections, or unit maps. Pass snapshots are
precomputed immutable plan objects with frame/generation/attachment-epoch tokens supplied from a
bounded pool or primitive-indexed arrays. The API signatures are records for clarity;
implementations may reuse internal storage without exposing mutation.

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

1. Exhaustively test the four `committedMain × flipped` side rows.
2. Property-test up to eight buffers across arbitrary write sets, explicit true/false overrides,
   virtual pre flips, skipped passes, success completion, commit, and abort.
3. Prove FBO output attachments and sampler reads come from the same snapshot.
4. Prove `commitFrame` preserves the latest physical texture and starts the next frame unflipped
   without a copy operation.
5. Run the same properties over shadowcolor identities and assert the Pintonium B4 stub behavior is
   impossible.
6. Test ordinary/full clear side matrices, fog alpha 1.0, explicit color replacement, clear-disable,
   equal-size/color batching, and `maxDrawBuffers` chunking. Also prove an estate-required full
   clear overrides ordinary caller intent, is consumed only after all batches succeed, and survives
   rejection, partial execution, and backend failure.
7. Table-test every applicable main-pass acquisition/completion rejection, including foreign,
   duplicate, and consumed snapshots; assert the exact reason, zero GL calls, and unchanged open
   frame/pass and flip state, and prove acquisition never returns `WRONG_FRAME_ID`.
8. Table-test every shadow acquisition, bind, clear, copy, completion, and abort result, including
   `PASS_ALREADY_OPEN` on a second begin: exact validation rejection is pre-GL and mutation-free;
   scripted bind/clear/copy backend failure keeps the token open for abort; only completion flips;
   abort closes and requires full clear.

### 8.2 Contract-table tests

- `formatTable_hasExactly37PackFacingNames`;
- `formatTable_integerUsesIntegerPixelFormatAndNearest`;
- `formatTable_allPixelFormatsAndTypesRecognized`;
- `unitMap_exactAppB3Rows`;
- `unitMap_depthtex1Is11AndDepthtex2CompositeIs12`;
- `unitMap_noDynamicAllocation`;
- `drawPrefixes_phase3CanonicalIndicesOnly`;
- `inventory_minFourScanDrivenNotSixteen`;
- `growth_nonNegativeIdentityNoHardcodedModelCap`; and
- `supersampling_doesNotChangeBufferExtent`.

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
8. distinguish `attachDepth` from requested `attachDepthStencil`;
9. exercise first-copy and steady copy protocol events;
10. prove candidate inspection exposes metadata but no generation or estate operation; publish it,
    then test immutable accepted generation plus resize/rebuild generation inequality;
11. register current and retained historical ready generations with matching sizing; reject blank
    and duplicate-live IDs, future generations, off/never-installed unknown generations, and known
    generations with mismatched sizing; assert each exact reason and zero registration,
    acknowledgement, publication, or generation mutation;
12. fail the first, each middle, and last resize-consumer position by returned `FAILED` and throw;
    assert `deliveredCount` is respectively zero or the exact number of preceding `SUCCESS`
    acknowledgements, and prove consecutive ready/off generations, unopened-estate disposal, no
    later callback, and retry convergence from each consumer's distinct acknowledged sizing baseline;
13. prove borrowed/foreign handles are never deleted or used in forbidden verbs;
14. exercise every overlay key and absence reason; reject expected-ID and registry-fingerprint
    mismatches without drawing, and prove referenced handles remain live until the successful
    binding snapshot is closed after the enclosing draw; and
15. create/destroy the full classic estate for the implementation gate with
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

## 9. Milestone staging

| Component | Milestone | Staging |
|---|---|---|
| pure buffer plan, identities, format/unit tables | v0.1 | implement |
| dfb colortex0–7 main/alt pairs | v0.1 | implement |
| flip/virtual-pre/frame-end rebase state machine | v0.1 | implement |
| clear policy/batching/fog alpha rule | v0.1 | implement |
| depthtex0 replacement/provider/version/extent-preparation tracking | v0.1 | implement after §5.5 Phase 1 fix-up |
| depthtex1/2 allocation and copy mechanics | v0.1 | implement; Phase 7 supplies moments |
| requested-format → all-RGBA fallback | v0.1 | implement |
| fixed App B.3 object map | v0.1 | implement core/P5 objects; Phase 13 slots remain explicit |
| resize/publication/generation/full-clear lifecycle | v0.1 | implement |
| sfb structure, policies, and real shadowcolor flips | v0.1 | architect and allocate when required |
| shadow pass bind/copy use | v0.2 | Phase 8 |
| Phase 13 overlays/noise/companions | v0.5 | consume stable slots/notices |
| sampler objects, DSA, async copies | v0.5 | Phase 14 implementation behind unchanged policy |
| shadowcomp/prepare/begin/setup wiring, colortex16/32, shadowcolor2–7 | post-v0.5 | G8/S1/S2; no identity redesign |

## 10. OQ & spike specifications

Phase 5 has no assigned open question. No spike is invented.

Two prerequisites are verification work, not OQs:

- Phase 1 must fix up and re-verify §5.5's borrowed-depth attachment contracts.
- The Phase 5 verification session must attack this document after the build session stops.

The unresolved `superSamplingLevel` ownership wording is an upstream clarification request in §11.5,
not a Phase-5 implementation spike: the fallback behavior is already designed in §4.11.

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision and rationale / contract check |
|---|---|
| D-P5-1 | Adopt a main/alt pair plus per-pass immutable flip snapshot. App B.1 and the shipped attachment description require exactly that; Pintonium supplies working structural evidence. |
| D-P5-2 | Adopt clear grouping, both-side behavior, and fog alpha 1.0. App B.1/§4.3 control the base colors and flipped clearing; the alpha quirk is deployed evidence consistent with them. |
| D-P5-3 | Retry an incomplete requested-format estate only as a wholly recreated `RGBA_COMPAT` estate. This restates §4.3's estate-wide plain-RGBA fallback and avoids an uncontracted mixed retry. |
| D-P5-4 | Reject alt→main copy-back. Preserve App F.7's last-writer state, then rebase the physical committed-main identity and clear the relative bit at frame end. No bandwidth copy occurs. |
| D-P5-5 | Adopt Pintonium's depth-renderbuffer replacement/version shape behind a Phase-5 SPI, with a requested narrow Phase 1 borrowed-attachment permission. App B.2 requires sampleable depthtex0; no raw GL name crosses D-6. |
| D-P5-6 | Adopt function-pointer-checked copy image → combined blit → depth copy-sub-image tiers as backend mechanics. App B.2 constrains contents/moments, not the mechanism. |
| D-P5-7 | Allocate a contiguous minimum-four G6 inventory through the highest required index, never unconditional 16. This satisfies App B.1 and RC3's scan-driven ruling while retaining growth-shaped IDs. |
| D-P5-8 | Use the generic real flip machine for shadowcolor and reject Pintonium B4's stub. Structure is reusable; the stub is contract-visible missing behavior. |
| D-P5-9 | Reproduce the fixed App B.3 map, including depthtex1 at unit 11, and reject dynamic allocation. |
| D-P5-10 | Buffer extent is display × render quality only; `superSamplingLevel` is a separate Phase 7 execution plan. The assigned behavioral digest separates those values, while RESEARCH does not specify a different allocation formula. |
| D-P5-11 | Use candidate ownership plus equality generation tied to `RegistryFingerprint`, so no partial or cross-registry estate can publish. |
| D-P5-12 | Pack FBO color attachments into route order while retaining logical buffer identity. This preserves G6 behavior and avoids hardcoding logical colortex index as the future physical attachment limit. |
| D-P5-13 | Use the observed G6 null-allocation transfer layout for all 37 formats: non-integer `BGRA`, integer `RGBA_INTEGER`, and `UNSIGNED_INT_8_8_8_8_REV` with null data. The full pack-facing pixel vocabulary remains available for Phase 13 raw uploads. |
| D-P5-14 | Adopt the deployed resize/version invalidation checklist for Phase-5-owned color/depth/FBO state, including full clear, and publish resize notices to later object owners. This satisfies RESEARCH §4.1/§4.3 without letting Phase 5 seize Phase 13/14 resources. |
| D-P5-15 | Require Phase 7 to prepare Minecraft's shader framebuffer so the borrowed depth texture exactly matches the render-quality-scaled dfb extent before candidate creation. A mismatch returns an awaiting/rebuild result and attaches nothing. This reconciles §4.3 sizing with App B.2's real depth attachment where Pintonium's fixed-1.0 render quality supplies no answer. |

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
| D-8 | published docs + LGPL evidence only; OF behavior restated, no copy |
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
4. **Pintonium shadow structure versus stubbed flip.** **Ruling: reuse structure, reject stub** and
   test real state.
5. **Phase 1 foreign-handle rule versus mandatory depth reattachment.** Phase 1 correctly forbids
   ordinary foreign attachment, but RC3 mandates reattaching owned FBOs to a platform-owned
   replacement depth texture. **Ruling: request the marker-scoped exception; do not assume it.**
6. **Phase 1 has no combined depth-stencil attachment verb.** `attachDepth` does not contractually
   attach stencil. **Ruling: request an additive explicit operation.**
7. **`superSamplingLevel` allocation ambiguity.** App A.3 says only “SSAA multiplier”; §4.3 says
   display × render quality; observed behavior separates them. **Ruling: D-P5-10** and request
   upstream wording.
8. **Pintonium format superset.** Its enum includes 8/16-bit integer targets absent from App B.4.
   **Ruling: exactly 37**, no imported additions.
9. **Pintonium copy strategy does not handle combined stencil without GL3.** **Ruling:** capability
   failure for the copied-depth feature; never perform an invalid GL2 copy.
10. **Phase 3 does not expose typed runtime quality multipliers in `PackConfiguration`.** **Ruling:**
    `BufferRuntimeInputs` is a Phase-5 interface supplied by Phase 7; no Phase 3 field is invented.
11. **Borrowed Minecraft depth extent versus render-quality-scaled dfb extent.** Pintonium's
    replacement is proven only with its global render quality fixed at 1.0, while RESEARCH §4.3
    requires display × render quality. **Ruling: D-P5-15 safe-point preparation**; a mismatch is
    never attached or stretched.

### 11.4 Hand-offs

- **Phase 6:** consume `TextureBindingSnapshot` through the Phase 7 composition; upload only fixed
  unit integers and never select a physical side independently.
- **Phase 7:** own frame/copy/clear moments, final-to-Minecraft bind, SSAA execution, try/finally
  commit/abort, runtime quality input, safe-point `MainDepthSource.prepare(plan.mainExtent())`, and
  registry→estate publication ordering. Never build or draw with a mismatched depth extent.
- **Phase 8:** use `ShadowEstateView`; own camera/traversal/pass and shadowtex1 copy moment.
- **Phase 13:** supply typed overlay objects for units 2/3, 7–10, and 15; consume resize notices.
- **Phase 14:** modernize facade/backend mechanics without changing this policy.
- **G8/S1/S2:** lift v0.1 population gates and wire dormant stages; retain identities and flip
  machine.
- **Final integration review:** audit the P5/P6 texture-unit split, Phase 7 publication ordering,
  Phase 8 shadow ownership, and Phase 13 overlay completeness.

### 11.5 Requested upstream changes

1. Apply and re-verify the three Phase 1 contract corrections in §5.5.
2. Clarify in a future RESEARCH/DESIGN revision whether `superSamplingLevel` affects framebuffer
   allocation or only Phase 7's SSAA execution, and specify multiplier-to-integer rounding. This
   document follows the observed separation and `round` pending that clarification.
3. Add a data-only `verification/targets/phase-5.json` profile anchored to RC3 before the separate
   verify session. Creating/running the verification harness is outside this build-session
   document deliverable.

## 12. Implementation checklist

| # | Work item | Tag | Test hook |
|---:|---|---|---|
| 1 | Define logical buffer IDs, extents, closed format/depth/unit enums | v0.1 | contract-table tests |
| 2 | Implement pure `BufferPlanner` and closed validation failures | v0.1 | invalid/capability property tests |
| 3 | Implement exact 37-format table + private `RGBA_COMPAT` | v0.1 | `formatTable_hasExactly37PackFacingNames` |
| 4 | Implement checked main/shadow extent formulas and limits | v0.1 | overflow/round/maxTextureSize tests |
| 5 | Implement scan-driven contiguous colortex inventory | v0.1 | min4/highest-index/not16 tests |
| 6 | Implement Phase 4 route expansion and packed attachment map | v0.1 | explicit/symbolic/sparse route tests |
| 7 | Implement main/alt `FlipState` and immutable snapshots | v0.1 | exhaustive four-state tests |
| 8 | Implement raster/virtual-pre transition rules | v0.1 | override permutation property tests |
| 9 | Implement frame-end metadata rebase and abort/full-clear | v0.1 | no-copy next-frame continuity test |
| 10 | Implement clear colors, alpha rule, side rules, batching | v0.1 | clear matrix + batch chunk tests |
| 11 | Implement dfb texture allocation and pass/clear FBO cache | v0.1 | recorded attachment/filter/wrap run |
| 12 | Implement candidate ownership ledger and idempotent close | v0.1 | leak/use-after-delete assertions |
| 13 | Implement requested-format attempt and all-RGBA retry | v0.1 | scripted incomplete-FBO tests |
| 14 | Apply Phase 1 borrowed-depth contract fix-up and re-verify | v0.1 prerequisite | Phase 1 review PASS |
| 15 | Apply Phase 1 combined depth-stencil verb fix-up/replay | v0.1 prerequisite | distinct recorded operation |
| 16 | Apply Phase 1 first-depth-copy initialization fix-up/replay | v0.1 prerequisite | initialization distinct from steady copy |
| 17 | Implement dumb framebuffer depth-replacement mixin | v0.1 | live create/delete/stencil smoke |
| 18 | Implement `MainDepthSource` safe-point prepare plus version/extent snapshots | v0.1 | ready/pending/mismatch/version tests |
| 19 | Implement depth-version reattachment and FBO recheck | v0.1 | recorded reattach/epoch test |
| 20 | Allocate/reallocate depthtex1/depthtex2 | v0.1 | format/extent/lifetime tests |
| 21 | Implement first-copy and three-tier steady copy backend | v0.1 | selector matrix + live pointer test |
| 22 | Implement copy-point ordering and non-stale fallback | v0.1 | duplicate/out-of-order/failure tests |
| 23 | Implement exact App B.3 object table | v0.1 | 16-row × stage equality test |
| 24 | Implement pass-coherent `TextureBindingSnapshot` | v0.1 | flip-between-bind prevention test |
| 25 | Implement sfb structure, neutral fallbacks, and shadow policy | v0.1 | recorded creation/filter/PCF/swizzle |
| 26 | Implement real shadowcolor flip state | v0.1 | B4-negative state-machine test |
| 27 | Implement estate publisher/generation/fingerprint checks | v0.1 | stale/cross-registry rejection |
| 28 | Implement resize classification, rebuild, notices, full clear | v0.1 | trigger matrix test |
| 29 | Wire Phase 7 frame/clear/copy/publication orchestration | v0.1 | recorded complete classic frame |
| 30 | Wire Phase 8 sfb use and shadowtex1 copy | v0.2 | shadow scene/T1 test |
| 31 | Wire Phase 13 overlay slots and resize listener | v0.5 | companion/custom/noise binding tests |
| 32 | Implement Phase 14 backend modernization behind same APIs | v0.5 | cross-backend call-log equivalence |
| 33 | Lift modern buffer/stage population gates | post-v0.5 | 16/32/sparse/shadowcomp tests |
| 34 | Run the implementation gate: full classic estate create/use/destroy | v0.1 exit | `noLeakedObjects`, `noUseAfterDelete` |

The future coding effort must complete items in dependency order. Items 14–16 are hard prerequisites
for 17–21; no implementation may bypass them with a raw GL name or direct engine-side LWJGL call.
