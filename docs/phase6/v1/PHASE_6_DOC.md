# Schmaloogium — Phase 6: Uniform & sampler system — Architecture

## 0. Header

| Field | Value |
|---|---|
| Phase | 6 — Uniform & sampler system |
| Document revision | v1, maintained architecture through §0.17 |
| Date | 2026-07-29 |
| Governing design | `docs/design/v2.0-RC3/DESIGN.md` |
| Milestone | v0.1; shadow/celestial values v0.2 |
| Declared dependencies | Phases 1, 3, and 4 |
| Assigned open questions | none |

This document originated as the Phase 6 build-session deliverable and still designs architecture
only. The original fresh build session did not implement source code, change a dependency
document, create a verification profile, or perform an adversarial review; §§0.3–0.17 record later
governed maintenance. The governing assignment says the deliverable is `PHASE_6_DOC.md`
(`docs/design/v2.0-RC3/DESIGN.md:1702`, “**Deliverable.** `PHASE_6_DOC.md` per §G9”), and the
mandatory skeleton is the thirteen sections reproduced at
`docs/design/v2.0-RC3/DESIGN.md:790`.

### 0.1 Inputs actually read

Governing and repository inputs:

- `AGENTS.md`, complete.
- `docs/MOVES.md`, complete, including the `DESIGN.md` collision warning.
- `docs/design/v2.0-RC3/DESIGN.md`, all of Part I (§G0–§G12) and the complete Phase 6
  assignment at lines 1689–1801. In particular, the objective is the exact built-in inventory,
  sampler re-pointing, smoothing, and pure provider seam
  (`docs/design/v2.0-RC3/DESIGN.md:1698`, “The ~50 built-in uniforms with their exact semantics
  and update cadences”).

Primary contract inputs:

- `docs/research/v1/RESEARCH.md` §0 and §1, §3.4, §4.2, §4.4, Appendix A.3,
  Appendix B.3, and Appendix D in full. The authority rule matters here: “The contract” is the
  fixed pack-author-facing surface (`docs/research/v1/RESEARCH.md:47`), and the built-in inventory
  is Appendix D (`docs/research/v1/RESEARCH.md:290`).
- `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, “Uniforms”, “GBuffers Uniforms”,
  “Shadow Uniforms”, and “Composite and Deferred Uniforms”, lines 123–266. This is a shipped
  pack-author document, not decompiled implementation source.

Pintonium evidence inputs, under §G11:

- `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §6 in full and §17 rows B1/B6, as
  assigned. The dynamic-unit divergence in §18 was also read because sampler mechanics cannot be
  adopted safely without its fixed-map warning.
- The following LGPL-3.0 source files were opened to verify the load-bearing PD claims:
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramUniforms.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramSamplers.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/uniform/Uniform.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CommonUniforms.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/FrameUpdateNotifier.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CapturedRenderingState.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/MatrixUniforms.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/SystemTimeUniforms.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedVec2f.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pathways/CenterDepthSampler.java`,
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/state/StateUpdateNotifiers.java`,
  and
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/properties/PackDirectives.java`.
- The 1.12.2 capture claim was checked at
  `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java`
  and
  `reference-src/pintonium-9c2fcc1/forge122/src/main/java/org/taumc/celeritas/mixin/core/terrain/ActiveRenderInfoAccessor.java`.
  The former copies the projection/model-view buffers
  (`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:38`,
  “setGbufferProjection”) at the ordinal-zero clear boundary
  (`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:102`);
  the latter exposes the two vanilla FloatBuffers
  (`reference-src/pintonium-9c2fcc1/forge122/src/main/java/org/taumc/celeritas/mixin/core/terrain/ActiveRenderInfoAccessor.java:11`,
  “PROJECTION”).

Dependency documents, read complete:

- `docs/phase1/v14/PHASE_1_DOC.md`
- `docs/phase3/v1/PHASE_3_DOC.md`
- `docs/phase4/v1/PHASE_4_DOC.md`

At build-session time, the highest-numbered reviews were:
`docs/phase1/reviews/PHASE_1_REVIEW_18.md`,
`docs/phase3/reviews/PHASE_3_REVIEW_14.md`, and
`docs/phase4/reviews/PHASE_4_REVIEW_11.md`. They were the verified dependency gates actually read
by the fresh build session. Section 0.6 separately records the later verified amendments adopted by
this maintenance session.

### 0.2 Deviations and limits

- Appendix A.3 was read in addition to the assigned RESEARCH sections because the GPU
  `centerDepthSmooth` candidate depends on the declaration-trigger contract:
  `docs/research/v1/RESEARCH.md:1166` says “`uniform … centerDepthSmooth`” enables the readback.
- Phase 5 was not read. It is a same-wave sibling, not a declared dependency. This document
  preserves the assigned split: Phase 5 owns the texture object behind each unit; Phase 6 owns
  sampler-uniform integer values.
- No Oculus digest/content was used. RC3 §G12 does not add it to Phase 6's required reading.
- No `docs/**/chatlogs/` path and no repository-root `*.txt` was read.
- No file from `glsl-transformation-lib`, the prohibited AGPL dependency, was opened or used.
- At build-session time there was no Phase 6 brief or `verification/targets/phase6*.json` in the
  checkout. That historical absence was not a reason to invent an assignment; §0.6 records the
  data-only target added later.

### 0.3 Review-1 corrections

Review 1 completed the loader-neutral request/sample/event schemas and made their validation,
identity, absence, and copy rules explicit. It also fixed the held-light contract: old-hand-light
publishes the brighter hand through `heldBlockLightValue` while `heldBlockLightValue2` remains the
off-hand light value. Full rationale is in the review's `## Resolutions`.

### 0.4 Review-2 corrections

Review 2 closed the construction/frame-begin result types and the Phase 11 view, typed-command,
sink, and refresh-result boundary. The binding definitions are in §§2.2, 4.6, 4.13, 5.1, and 6.

### 0.5 Review-3 corrections

Review 3 fixed custom-bridge installation and aborted-prefix disposition, and added the missing
frame-begin-before-resize/clear conformance-map row. Binding details are in §§4.13 and 5.1.

### 0.6 Dependency-adoption maintenance addendum (2026-07-29)

After round four's literal PASS, the two dependency requests in the original §5.3 were implemented
through their governed fix-up/re-verification loops. Phase 3 now publishes final attributed
`DeclaredUniformCatalog`s and a closed structural type algebra; round twenty verifies that
interface (`docs/phase3/reviews/PHASE_3_REVIEW_20.md:56`–`:67`). Phase 4 now merges those catalogs
into an effective handle-free `ProgramUniformLayout` and supplies callback-scoped lookup plus an
operation-free activity token; round eighteen verifies that interface
(`docs/phase4/reviews/PHASE_4_REVIEW_18.md:55`–`:70`). Sections 2, 4, 5, 8, 9, 11, and 12 now
consume those grants rather than describing unimplemented requests.

The data-only `verification/targets/phase-6.json` profile also exists and drove rounds one through
four. The RESEARCH custom-expression discrepancy and future-DESIGN Candidate-B clarification
remain pending authority-maintainer items. Neither blocks this architecture: §4.13 keeps the
conservative union exclusion and D-P6-1 keeps the verified Candidate A.

**Current §G1.3 status:** Review 17 produced one correction, resolved in §0.17: compact maintenance
provenance now includes §0.16 and this fix-up addendum. The current bytes remain **not verified**
until a fresh review returns literal PASS, and the version directory remains `v1` while that loop
is open.

### 0.7 Review-5 correction

The conditional `shadow` sampler now derives from the effective program's published
`ProgramUniformLayout`; Phase 6 consumes no Phase 3 water-shadow field or world constants.

### 0.8 Review-6 correction

Maintenance provenance and §G1.3 status now include the resolved Review-5 correction and the
current verification state.

### 0.9 Review-9 correction

Construction now installs the initial Phase 4 publication generation, and the runtime exports an
atomic replacement-generation adoption handshake before frame begin or activation.

### 0.10 Review-10 corrections

Shaders-off retains borrowed providers until terminal close, GL-context loss is included in the
lifecycle reset inventory, and maintenance provenance now includes §0.9.

### 0.11 Review-11 correction

All compact maintenance-provenance markers now include the Review-10 and Review-11 addenda.

### 0.12 Review-12 corrections

The current verification status is synchronized, and the public reset-reason domain and operation
partition are now closed in §§2.2, 4.14, and 5.1.

### 0.13 Review-13 correction

The threading table now names generation adoption separately from direct reset.

### 0.14 Review-14 corrections

Replacement adoption and direct resets now state their observable lifecycle ordering.

### 0.15 Review-15 correction

Terminal close now occurs after final Phase 6 participant use and before Phase 7 initiates the
existing atomic Phase 4 teardown operation.

### 0.16 Review-16 corrections

Terminal close is teardown-only; ordinary publication replacement remains exclusively a generation
adoption. The `blendFunc` conformance row now cites the matching Appendix E hook row.

### 0.17 Review-17 correction

All compact maintenance-provenance markers now include §0.16 and this fix-up addendum.

---

## 1. Scope & boundaries

### 1.1 Owned here

Phase 6 owns:

1. the complete Appendix D built-in uniform catalog: exact pack-facing names, GLSL types,
   meanings, acquisition cadence, upload policy, provider, and milestone;
2. the pure-`:engine` cadence/value-cache machinery and the three Phase 4 barrier participants;
3. per-effective-program uniform-location caches, value deduplication, and the matrix
   always-upload exception;
4. fixed-map sampler-uniform re-pointing for gbuffers/shadow and
   deferred/composite/final programs;
5. frame-begin sampling, temporal snapshots, CPU smoothing, center-depth readback policy, matrix
   capture/inversion, and event-driven per-draw values;
6. loader-neutral provider and event interfaces implemented or invoked from `mod.glue`;
7. the Phase 11 custom-uniform extension point, placed after built-ins;
8. per-uniform GL upload isolation using Phase 1's attributed replay protocol; and
9. the notifier-to-producer contract consumed by Phase 7 and later hook owners.

### 1.2 Adjacent concerns, explicitly not owned here

- **Owned by Phase 1:** module/seam rules, `engine.gl`, opaque handles, `UniformService`,
  `FramebufferService.readDepthPixel`, recording/replay, diagnostics, and the GL-error drain
  protocol. Phase 6 consumes them.
- **Owned by Phase 3:** parsing uniform declarations and half-life directives, the immutable pack
  configuration, resource requirements, source materialization, and the reserved macro-contributor
  slot. Phase 6 neither reopens nor rescans shader source.
- **Owned by Phase 4:** program compilation, handles, effective-provider fallback, generation,
  program activation, participant ordering, and alpha/blend locks. Phase 6 never calls
  `ShaderService.use` or resolves a backup chain itself.
- **Owned by Phase 5:** texture creation/lifetime, the texture object bound behind each fixed unit,
  framebuffer estate, flip state, and depth copies. Re-pointing a sampler uniform to unit 7 is not
  binding a texture to unit 7.
- **Owned by Phase 7:** frame orchestration and every v0.1 vanilla/Mixin producer hook, including
  frame begin, first-clear matrix capture, celestial rotation, GlStateManager observation,
  `entityColor`, and the composite `instanceId` loop.
- **Owned by Phase 8:** shadow-camera setup and the values of the shadow/celestial set at v0.2.
- **Owned by Phase 9:** alias resolution and the values of held-item, `entityId`, and
  `blockEntityId` inputs at v0.3.
- **Owned by Phase 10:** vertex-pipeline work. It does not upload Appendix D uniforms.
- **Owned by Phase 11:** parsing/evaluating custom expressions and rung-1 expression isolation.
  Phase 6 supplies only the ordered activation bridge and upload/error plumbing.
- **Owned by Phase 13:** the `atlasSize` value source and texture-change producers at v0.5.
- **Owned by Phase 14:** the optional PBO/fence replacement for synchronous center-depth readback.
  This document deliberately leaves that ledger item live.

Custom expressions, vanilla hooks, alias computation, shader compilation, texture binding, and
buffer allocation are therefore scope-out, matching the assignment at
`docs/design/v2.0-RC3/DESIGN.md:1776`.

---

## 2. Architecture overview

### 2.1 Placement

Production types split across the Phase 1 seam:

```text
:engine
  com.schmaloogium.engine.uniforms
    catalog/       Appendix D and fixed sampler maps
    runtime/       value cells, cadence clock, temporal state, program caches
    smooth/        tick-domain asymmetric EMA
    matrix/        immutable 4×4 values, inverse and previous snapshots
    barrier/       sampler, built-in and custom participants
    spi/           loader-neutral providers and event inputs

:mod
  com.schmaloogium.mod.glue.uniforms
    Minecraft/Forge/GlStateManager-backed providers and Phase 7 hook adapters
```

`:engine` sees JDK types plus Phase 1/3/4 published `:engine` interfaces. No Minecraft, Forge,
Cleanroom, Mixin, LWJGL, JOML, or raw GL name crosses the seam. This is a correctness constraint,
not a packaging preference: D-6 requires the core to remain headless-testable
(`docs/research/v1/RESEARCH.md:100`, “Engine-core / loader-glue seam”).

### 2.2 Key types and relationships

The public shape is:

```java
public interface UniformRuntimeFactory {
    UniformBuildResult create(
        long initialRegistryGeneration,
        UniformConfiguration configuration,
        UniformPlatformProvider platform,
        CenterDepthSource centerDepth,
        GLDevice gl,
        DiagnosticReporter diagnostics);
}

public sealed interface UniformBuildResult {
    record Success(UniformRuntime runtime) implements UniformBuildResult {}
    record Failure(String diagnosticId) implements UniformBuildResult {}
}

public interface UniformRuntime {
    RegistryGenerationAdoptionResult adoptRegistryGeneration(
        long registryGeneration, UniformResetReason reason);
    FrameBeginResult beginFrame(FrameBeginInput input);
    UniformEventSink events();
    ProgramBindingParticipant samplerParticipant();
    ProgramBindingParticipant builtInParticipant();
    ProgramBindingParticipant customParticipant();
    MacroContributor centerDepthMacroContributor();
    void installCustomUniformBridge(CustomUniformBridge bridge);
    void reset(UniformResetReason reason);
}

public enum RegistryGenerationAdoptionResult {
    ADOPTED, ALREADY_CURRENT, REJECTED_RETIRED_GENERATION
}

public enum UniformResetReason {
    PACK_REPLACEMENT, SHADERS_OFF, GL_CONTEXT_LOSS, WORLD_EPOCH, CLOSE
}

public enum FrameBeginResult {
    ACCEPTED, DUPLICATE, REJECTED_STALE_FRAME, REJECTED_GENERATION
}

public record FrameBeginInput(
    long registryGeneration,
    long frameId,
    long worldEpoch,
    long logicalTick,
    double smoothingTimeTicks,
    float frameTimeSeconds,
    int targetViewWidth,
    int targetViewHeight,
    int priorFramebufferWidth,
    int priorFramebufferHeight) {}

public interface UniformEventSink {
    void captureGbufferMatrices(long frameId, Matrix4Value modelView, Matrix4Value projection);
    void updateCelestial(CelestialSample sample);
    void updateShadowMatrices(ShadowMatrixSample sample);
    void updateFog(FogSample sample);
    void updateBlend(BlendSample sample);
    void updateEntityColor(Float4 value);
    void updateEntityId(int value);
    void updateBlockEntityId(int value);
    void updateInstanceId(int value);
    void updateAtlasSize(Int2 value);
    void updateHeldItems(HeldItemSample value);
}
```

The signatures are illustrative but the data contracts are binding. `Success` contains the only
operational runtime and transfers its lifecycle to the caller; the factory retains nothing.
`Failure` contains one non-empty stable diagnostic ID, contains no runtime, performs no GL
work, and leaves all supplied services caller-owned. A successful runtime retains borrowed service
references until `close` reset; it owns only its caches, snapshots, and participants. The complete
provider-record validation, absence, and copy rules are in §4.2. `Matrix4Value` stores exactly 16
floats in the facade's upload order and exposes no mutable array.

`UniformRuntime` owns no program handle. Phase 4 owns the active linked program and calls the three
participants in sampler → built-in → custom order
(`docs/phase4/v1/PHASE_4_DOC.md:1162`–`:1165`). Its verified
`BoundProgramUniformAccess` contract supplies callback-scoped location lookup without revealing
that handle (`docs/phase4/v1/PHASE_4_DOC.md:1193`–`:1201`).

### 2.3 Data flow

```text
Phase 7 frame-begin hook
  → beginFrame (world/tick/frame sample + previous snapshots + sync center-depth sample)
  → Phase 5 resize/clear may begin
  → Phase 7 first-clear hook captures current gbuffer matrices
  → Phase 4 barrier binds effective program
      → sampler participant asserts fixed unit integers
      → built-in participant uploads current immutable cells
      → custom participant asks Phase 11 to evaluate from the stable built-in view
      → Phase 4 applies provider alpha/blend lock
  → per-draw/celestial/fog/atlas hooks replace event cells and, while a Phase-4 activity token
    remains current, immediately upload changed values without rebinding
  → shadow/matrix hooks replace cells for the next activation
```

Provider evaluation never occurs during the barrier's GL-error replay. A frame/tick/event updates a
typed value cell once; an activation snapshots the cells into immutable upload commands; replay uses
those exact commands.

### 2.4 Two different meanings of cadence

This design separates:

- **acquisition cadence** — when a provider/event may replace a cell (`ONCE`, `PER_TICK`,
  `PER_FRAME`, or `SIGNAL`); and
- **activation policy** — every successful Phase 4 shader activation visits every declared,
  enabled built-in and sampler, regardless of acquisition cadence.

That separation reconciles “everything refreshes on program switch”
(`docs/research/v1/RESEARCH.md:1379`) with tick/frame sampling and redundant skipping. “Refresh”
means participate in the activation sweep; it does not mean resample Minecraft or advance an EMA.

---

## 3. Contract conformance map

| In-scope contract item | Design element | Provenance / disposition |
|---|---|---|
| Full Appendix D names, types, and values | §4.4 exhaustive inventory | `[V:doc]`; source inventory begins at `docs/research/v1/RESEARCH.md:1318`, “Built-in uniform inventory” |
| Everything refreshes on program switch | §4.3 activation sweep and §4.10 participant trace | `[V:observed]`; `docs/research/v1/RESEARCH.md:1379`, “everything refreshes on program switch” |
| Per-program location cache and redundant skip; matrices always upload | §4.3, §4.10, §4.11 | `[V:observed]`; `docs/research/v1/RESEARCH.md:1380`, “matrices always upload” |
| Final declaration/type truth without source reopening | Phase 3 catalog → Phase 4 merged effective layout → Phase 6 plan validation, §§4.9–4.10/5.3 | verified dependency contracts at `docs/phase3/v1/PHASE_3_DOC.md:1115` and `docs/phase4/v1/PHASE_4_DOC.md:1373` |
| Bound lookup and between-activation proof without a program handle | callback-scoped access, generation/provider/layout cache key, and operation-free activity token in §4.10 | verified Phase 4 contract at `docs/phase4/v1/PHASE_4_DOC.md:1374`–`:1375`; D-6 |
| Celestial, shadow, and per-draw event moments | typed `UniformEventSink`, §4.6/§4.12 | `[V:observed]`; exact cadence list at `docs/research/v1/RESEARCH.md:1380` |
| Custom uniforms after built-ins on every switch | third barrier participant and `CustomUniformBridge`, §4.13 | `[V:doc]`; `docs/research/v1/RESEARCH.md:1382`, “custom uniforms … after built-ins” |
| Fixed unit map, including stage variants | immutable tables in §4.9 | `[V:doc]`; `docs/research/v1/RESEARCH.md:1227`, “packs rely on these numbers” |
| `depthtex1` is unit 11 | unit-11 table row and regression test | `[V:doc]` + ruling; `docs/research/v1/RESEARCH.md:1253`, “Treat **11 as authoritative**” |
| P5/P6 split | Phase 6 writes sampler integers only | governing split; Phase 1 assigns the backing texture per unit to Phase 5 and sampler pointing to Phase 6 (`docs/phase1/v14/PHASE_1_DOC.md:1406`) |
| World state sampled at frame begin | stable `FrameSnapshot`, §4.6 | `[V:observed]`; `docs/research/v1/RESEARCH.md:533`, “frame start” |
| Frame-begin sampling completes before resize/clear | ordering rule in §§4.6 and 5.1 | governing REV1 constraint; `docs/design/v2.0-RC3/DESIGN.md:1721-1725`, “before any buffer resize or clear” |
| Previous camera/matrix snapshots | explicit rotate-before-overwrite rules, §4.6/§4.7 | `[V:observed]`; `docs/research/v1/RESEARCH.md:536`, “snapshot previous-frame camera + matrices” |
| Synchronous center-depth readback when declared | CPU candidate selected in §4.8 | `[V:observed]`; `docs/research/v1/RESEARCH.md:558`, “synchronous center-depth readback” |
| Exact tick-domain smoothing | `TickEma` / `AsymmetricTickEma`, §4.5 | contract interpretation; RC3 requires “per-tick exponential decay” at `docs/design/v2.0-RC3/DESIGN.md:1733` |
| Fixed-function matrix capture and inverses | §4.7 | `[V:observed — Pintonium]`; verified source copies vanilla buffers at `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:38` |
| `blendFunc` observation through GlStateManager cooperation | typed producer plus mandatory audit row, §4.12 | `[V:doc]`; App E class row at `docs/research/v1/RESEARCH.md:1415` |
| Every value behind a pure provider/event seam | `UniformPlatformProvider`, `CenterDepthSource`, immutable event records | D-6; assignment at `docs/design/v2.0-RC3/DESIGN.md:1771` |
| Per-uniform GL upload isolation | immutable upload batch + attributed replay, §4.11 | Phase 1 binding contract at `docs/phase1/v14/PHASE_1_DOC.md:4119` |
| PD cadence buckets and cache mechanics | acquisition buckets plus activation sweep | **Adopted as non-contract-visible mechanics**; PD says ONCE/PER_TICK/PER_FRAME/dynamic at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:288`; source confirms dynamic first at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramUniforms.java:200` |
| PD sampler queue/dedup mechanics | deterministic unit/name plan and cached integer uploads | **Mechanics adopted; allocation policy rejected**. PD warns its units are dynamic at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:344`; fixed App B.3 wins |
| PD GPU `centerDepthSmooth` | not populated; CPU path selected | **Contract-visible rejection, D-P6-1**; §4.8 checks App D, Phase 3's macro placement, and unit map |
| PD smoothing math | closed-form EMA only | **Math adopted after unit conversion; wiring rejected**. Source says deciseconds at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java:45`; RESEARCH/RC3 tick units win |
| PD B1 | separate wetness/dryness fields and directive-to-field tests | **Not inherited**; the source writes dryness into wetness at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/properties/PackDirectives.java:254` and the next line |
| PD B6 | notifier objects are constructed non-null; every consumer has a producer audit row | **Not inherited**; source declares but does not initialize `blendFuncNotifier` at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/state/StateUpdateNotifiers.java:10` |
| PD B10 | no sibling sampler overload family; one plan-record path | **Not inherited**; the landmine overload begins at `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramSamplers.java:320` and returns false |
| PD §18 dynamic texture-unit allocation | immutable App B.3 maps | **Pre-decided rejection**; divergence table states fixed map at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808` |

There is no unmapped in-scope item. The exact per-uniform provider/cadence/milestone mapping follows
in §4.4, and the Phase 4 barrier is traced operation by operation in §4.10.

---

## 4. Detailed design

### 4.1 Configuration and lifecycle roots

`UniformConfiguration` is an immutable derivation of one Phase 3 `PackConfiguration` and contains:

- Phase 3 schema/configuration fingerprint;
- the exact half-lives in **ticks**: wetness, dryness, eye brightness, and center depth;
- center-depth requirement;
- enabled v0.1/v0.2 features; and
- a fixed catalog version.

It never holds a source string, parser, Minecraft object, program handle, framebuffer handle, or
mutable provider. Phase 3 remains the single parse truth
(`docs/phase3/v1/PHASE_3_DOC.md:1112`, “single validated downstream truth”). The effective
`ProgramUniformLayout` is deliberately not part of this pack-global configuration; Phase 4 passes
it with each resolved program binding, and `ProgramCache` retains that immutable per-key layout.

Construction validates every half-life as finite and non-negative. A malformed directive is a
Phase 3 diagnostic/default matter; an invariant breach arriving here rejects the uniform candidate
without GL work. Phase 3 already publishes distinct tick fields—for example
`SmoothingConstants.drynessHalfLifeTicks` (`docs/phase3/v1/PHASE_3_DOC.md:918`)—so Phase 6 performs
no parser-side unit conversion. `create` installs the generation from the current Phase 4
`PublishedRegistry` as the runtime's initial current generation. A new published registry
generation creates a new program-cache namespace. Old locations and disabled scopes are discarded;
temporal world values survive only when `worldEpoch` is unchanged.

After Phase 4 accepts a replacement, Phase 7 calls
`adoptRegistryGeneration(replacement.generation(), reason)` on the render thread before any
`beginFrame`, event, or participant activation against that replacement. Adoption is atomic: a
different, never-retired generation becomes current and the prior generation becomes retired; the
current value is an idempotent `ALREADY_CURRENT`; a retired value is rejected without mutation.
Generation values are compared only for equality, never ordered or subtracted, matching Phase 4's
wrap-safe protocol. Phase 7 must pass the generation from the newly reacquired authoritative
publication, not a candidate view or delayed snapshot. `ADOPTED` applies §4.14's reset scope for
the supplied semantic reason before returning. A rejection forbids shader drawing and requires
reacquiring Phase 4's current publication.

Lifecycle:

```text
NEW
  → CONFIGURED(configuration, providers)
  → FRAME_READY after first beginFrame
  → ACTIVE through any number of barrier activations/events
  → RESET on world epoch, pack replacement, shaders-off, GL-context loss, or close
```

No GL work occurs in `reset`. Program-location objects belong to a registry generation and are
dropped, not deleted.

### 4.2 Provider seam and stable values

The platform SPI is intentionally coarse enough to sample related Minecraft values once:

```java
public interface UniformPlatformProvider {
    OnceUniformSample sampleOnce();
    TickUniformSample sampleTick(long worldEpoch, long logicalTick);
    FrameUniformSample sampleFrame(FrameSampleRequest request);
}

public interface CenterDepthSource {
    CenterDepthResult readCenter(CenterDepthRequest request);
}

public sealed interface CenterDepthResult {
    record Sample(float depth) implements CenterDepthResult {}
    record Unavailable(String diagnosticId) implements CenterDepthResult {}
}
```

The SPI records are exactly:

```java
public record OnceUniformSample(float nearPlane) {}
public record TickUniformSample(
    long worldEpoch, long logicalTick, long worldTicks, int moonPhase, float rainStrength) {}
public record FrameSampleRequest(
    long registryGeneration, long worldEpoch, long frameId, long logicalTick,
    float frameTimeSeconds, int targetViewWidth, int targetViewHeight) {}
public record FrameUniformSample(
    long worldEpoch, long frameId, Double3 cameraPosition, float eyeAltitude,
    Int2 eyeBrightness, int isEyeInWater, float nightVision, float blindness,
    float screenBrightness, boolean hideGui, float farPlane, float sunAngle,
    OptionalFloat shadowAngle, Float3 skyColor, OptionalValue<FogSample> fogFallback) {}
public record CenterDepthRequest(
    long registryGeneration, long worldEpoch, long frameId,
    int framebufferWidth, int framebufferHeight, int pixelX, int pixelY) {}
```

`OnceUniformSample.nearPlane` must be exactly `0.05f`; the record exists to keep the value behind
the provider seam rather than to make it configurable. `TickUniformSample` identities must equal
the call arguments, `moonPhase` is 0…7, and `rainStrength` is finite in `[0,1]`.
`FrameSampleRequest` is constructed by the runtime from the accepted `FrameBeginInput`;
dimensions are non-negative and `frameTimeSeconds` is finite and non-negative.
`FrameUniformSample` must echo the request's world/frame identity. Its vectors and floats are
finite; `eyeBrightness` components are 0…240; `isEyeInWater` is 0, 1, or 2; effect strengths and
screen brightness are in `[0,1]`; `farPlane` is non-negative; and `sunAngle` and a present
`shadowAngle` are in `[0,1)`. `fogFallback` is absent only when no valid frame fallback exists.
`CenterDepthRequest` names the completed prior framebuffer: positive dimensions imply
`pixelX=floor(width/2)` and `pixelY=floor(height/2)` in bottom-left pixel coordinates; zero
dimensions produce `Unavailable` without calling the source. `CenterDepthResult.Sample.depth` is
finite in `[0,1]`; `Unavailable.diagnosticId` is a non-empty stable diagnostic key.

Event records are exactly:

```java
public record CelestialSample(
    long worldEpoch, long frameId, Float3 sunPosition, Float3 moonPosition,
    Float3 shadowLightPosition, Float3 upPosition) {}
public record ShadowMatrixSample(
    long worldEpoch, long frameId, Matrix4Value projection, Matrix4Value modelView) {}
public record FogSample(
    long worldEpoch, long frameId, int fogMode, float density, Float3 color) {}
public record BlendSample(
    long worldEpoch, long frameId, boolean enabled,
    int srcRgb, int dstRgb, int srcAlpha, int dstAlpha) {}
public record HeldItemSample(
    long worldEpoch, long logicalTick, int heldItemId, int heldBlockLightValue,
    int heldItemId2, int heldBlockLightValue2) {}
public record Double3(double x, double y, double z) {}
public record Float3(float x, float y, float z) {}
public record Float4(float x, float y, float z, float w) {}
public record Int2(int x, int y) {}
```

All event identities must match the runtime's current world and must not regress; frame-bearing
events apply only to that frame, while `HeldItemSample` replaces the value for its logical tick.
Every vector and matrix component is finite. Fog density is in `[0,1]`; `fogMode`, blend factors,
and alias-derived item IDs intentionally retain their full `int` domains. Held-light values are
0…15. When old-hand-light is disabled, Phase 9 constructs the pair as
`(mainLight, offHandLight)`; when enabled it constructs
`(max(mainLight, offHandLight), offHandLight)`. Thus old packs reading only
`heldBlockLightValue` see the brighter hand, while `heldBlockLightValue2` remains the actual
off-hand value.

`Double3`, `Float3`, `Float4`, `Int2`, and `Matrix4Value` are immutable by-value engine records.
`OptionalFloat` and `OptionalValue<T>` are closed present/absent value types, never nullable.
Constructors reject invalid identity/range/finite data before mutation. Records contain no
Minecraft, GL, supplier, buffer, or mutable collection reference; array-taking value constructors
copy on entry and expose values only by scalar access or fresh copy. Provider exceptions, invalid
records, and unavailable optionals retain the last valid affected cells (neutral on first sample),
emit the stable diagnostic once, and do not affect sibling cells. Phase 9 and Phase 13 feed their
later-owned values through events; the uniform layer does not pretend those values exist early.

Each sample is copied into a typed `UniformCell`. A cell holds:

```text
definition id · immutable value · acquisition revision · valid/disabled state · diagnostic id
```

Providers are invoked once at their acquisition boundary, never from `afterBind`. A provider
exception is caught at the SPI boundary and affects only the cells supplied by that provider.
Headless tests supply scripted providers.

### 4.3 Cadence engine and upload cache

Acquisition buckets:

| Bucket | Trigger | Examples |
|---|---|---|
| `ONCE` | configuration/world-provider install | constants, inert legacy metrics |
| `PER_TICK` | logical tick differs from the last sampled tick | world time/day, moon phase, rain target, held inputs |
| `PER_FRAME` | accepted new frame ID | camera/player/render/time/fog/sky, smoothing, center depth |
| `SIGNAL` | typed hook/event method | matrices, celestial, shadow, entity/TE IDs, blend, instance, atlas |

Every activation then walks a precomputed `ProgramUploadPlan`. For each entry:

1. absent location: no-op;
2. disabled scope: no-op;
3. matrix: upload unconditionally;
4. scalar/vector unchanged by exact canonical comparison: skip;
5. otherwise snapshot an `UploadCommand` and upload it.

Float comparison uses `Float.floatToIntBits` after normalizing `-0.0f` to `0.0f`; NaNs never enter a
valid cell. Ints compare exactly. Vectors compare every component. Matrices deliberately ignore
equality. The last-uploaded value is per **effective linked program**, uniform name, and registry
generation—not per requested fallback child—because Phase 4 resolves one effective provider and
does not overlay child state (`docs/phase4/v1/PHASE_4_DOC.md:682`, “one immutable compiled
binding”).

`SIGNAL` has two policies. `NEXT_ACTIVATION` only replaces cells (gbuffer/shadow matrices and held
inputs). `IMMEDIATE_IF_ACTIVE` replaces cells and builds a small upload batch for the currently
active effective program (celestial, fog, blend, entity color/IDs, instance, and atlas size). The
batch runs only if Phase 4's retained activity token still reports current; otherwise the value is
carried to the next activation. It never calls `use`, resolves fallback, or looks up a new
location. This is what lets `instanceId` change between repeated draws while the same program stays
bound.

Pintonium's bucket order validates the shape but is not copied wholesale. Its source executes
dynamic values before once/tick/frame
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramUniforms.java:200`),
while this design samples
outside activation so an error replay cannot observe a second world state.

### 4.4 Complete built-in inventory

“Acquisition” below describes value replacement. “Activation” is always **every successful shader
activation**, with exact-value skip except matrices. “Pending” means the Phase 6 interface exists at
v0.1 and uploads a documented neutral value until the owning value producer lands.

#### 4.4.1 Held item and player

| Uniform | Type and exact value | Provider / acquisition | Activation | Milestone |
|---|---|---|---|---|
| `heldItemId` | `int`; Phase 9 alias-mapped main-hand item ID | held-item event; pending 0 | every switch, skip equal | interface v0.1; value v0.3 |
| `heldBlockLightValue` | `int`; main-hand emitted light, or `max(main,off)` when old-hand-light is enabled | held-item event; pending 0 | every switch, skip equal | interface v0.1; value v0.3 |
| `heldItemId2` | `int`; Phase 9 alias-mapped off-hand item ID | held-item event; pending 0 | every switch, skip equal | interface v0.1; value v0.3 |
| `heldBlockLightValue2` | `int`; actual off-hand emitted light in both modes | held-item event; pending 0 | every switch, skip equal | interface v0.1; value v0.3 |
| `wetness` | `float`; rain strength smoothed with asymmetric wetness/dryness half-lives | tick rain target + frame EMA | every switch, skip equal | v0.1 |
| `eyeAltitude` | `float`; view-entity world Y | frame provider | every switch, skip equal | v0.1 |
| `eyeBrightness` | `ivec2`; block/sky brightness, each clamped 0…240 | frame provider | every switch, skip equal | v0.1 |
| `eyeBrightnessSmooth` | `ivec2`; component-wise smoothed eye brightness | frame provider + vector EMA | every switch, skip equal | v0.1 |
| `isEyeInWater` | `int`; 0 neither, 1 water, 2 lava | frame provider | every switch, skip equal | v0.1 |
| `nightVision` | `float`; effect strength clamped 0…1 | frame provider | every switch, skip equal | v0.1 |
| `blindness` | `float`; effect strength clamped 0…1 | frame provider | every switch, skip equal | v0.1 |
| `screenBrightness` | `float`; video setting clamped 0…1 | frame provider | every switch, skip equal | v0.1 |
| `hideGUI` | `int`; 1 when GUI hidden, else 0 | frame provider | every switch, skip equal | v0.1 |

These types and meanings are the contract table beginning at `docs/research/v1/RESEARCH.md:1325`; for
example, `eyeBrightness` is “x block / y sky light, 0–240”
(`docs/research/v1/RESEARCH.md:1331`).

#### 4.4.2 World, time, and weather

| Uniform | Type and exact value | Provider / acquisition | Activation | Milestone |
|---|---|---|---|---|
| `worldTime` | `int`; Java remainder `worldTicks % 24000`, then int narrowing | tick provider | every switch, skip equal | v0.1 |
| `worldDay` | `int`; Java integer division `worldTicks / 24000`, then int narrowing | tick provider | every switch, skip equal | v0.1 |
| `moonPhase` | `int`; 0…7 | tick provider | every switch, skip equal | v0.1 |
| `frameCounter` | `int`; starts 0 on runtime creation, increments once per accepted frame, wraps to 0 after 720719 | cadence engine | every switch, skip equal | v0.1 |
| `frameTime` | `float`; elapsed seconds for the immediately preceding rendered frame, finite and ≥0 | frame clock provider | every switch, skip equal | v0.1 |
| `frameTimeCounter` | `float`; accumulated rendered seconds; after addition, any value ≥3600 resets to 0 | cadence engine | every switch, skip equal | v0.1 |
| `sunAngle` | `float`; normalized celestial angle in `[0,1)` | frame provider | every switch, skip equal | v0.1 |
| `shadowAngle` | `float`; normalized active shadow-light angle in `[0,1)` | frame provider; shadow policy supplied at v0.2 | every switch, skip equal | interface v0.1; value v0.2 |
| `rainStrength` | `float`; current unsmoothed rain strength clamped 0…1 | tick provider | every switch, skip equal | v0.1 |
| `fogMode` | `int`; engine enum encoded to legacy `GL_LINEAR`/`GL_EXP`/`GL_EXP2` numeric values by `mod.glue` | fog signal | immediate if active + every switch | v0.1 |
| `fogDensity` | `float`; current density clamped 0…1 | fog signal/frame fallback | immediate if active + every switch | v0.1 |
| `fogColor` | `vec3`; current linear RGB supplied by vanilla state | fog signal/frame fallback | immediate if active + every switch | v0.1 |
| `skyColor` | `vec3`; sampled current sky RGB | frame provider | every switch, skip equal | v0.1 |

The wrap values are contract, not convenience:
`docs/research/v1/RESEARCH.md:1345` says `frameCounter` “wraps at 720720”, and
`docs/research/v1/RESEARCH.md:1347` says `frameTimeCounter` “wraps at 3600”. The exact boundary
mechanics are corroborated by the working reference: the counter uses `(count + 1) % 720720`
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/SystemTimeUniforms.java:49`)
and elapsed time resets to zero at 3600
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/SystemTimeUniforms.java:89`).

#### 4.4.3 Camera, matrices, and screen

| Uniform | Type and exact value | Provider / acquisition | Activation | Milestone |
|---|---|---|---|---|
| `aspectRatio` | `float`; `targetViewWidth / targetViewHeight`, or 1 when height is zero during recovery | frame-begin input | every switch, skip equal | v0.1 |
| `viewWidth` | `float`; coming frame's render width in pixels | frame-begin input | every switch, skip equal | v0.1 |
| `viewHeight` | `float`; coming frame's render height in pixels | frame-begin input | every switch, skip equal | v0.1 |
| `near` | `float`; constant 0.05 | once provider | every switch, skip equal | v0.1 |
| `far` | `float`; render-distance chunks × 16 | frame provider | every switch, skip equal | v0.1 |
| `sunPosition` | `vec3`; eye-space sun vector captured at celestial rotation | celestial signal; pending neutral | immediate if active + every switch | interface v0.1; value v0.2 |
| `moonPosition` | `vec3`; eye-space moon vector captured at celestial rotation | celestial signal; pending neutral | immediate if active + every switch | interface v0.1; value v0.2 |
| `shadowLightPosition` | `vec3`; active sun-or-moon shadow light in eye space | celestial/shadow signal; pending neutral | immediate if active + every switch | interface v0.1; value v0.2 |
| `upPosition` | `vec3`; eye-space world-up vector | celestial signal; pending neutral | immediate if active + every switch | interface v0.1; value v0.2 |
| `cameraPosition` | `vec3`; current world-space view-entity/camera position | frame provider | every switch, skip equal | v0.1 |
| `previousCameraPosition` | `vec3`; prior accepted frame's `cameraPosition` | temporal store at frame begin | every switch, skip equal | v0.1 |
| `gbufferModelView` | `mat4`; model-view captured after camera setup at first clear | gbuffer matrix signal | every switch, **always upload** | v0.1 |
| `gbufferModelViewInverse` | `mat4`; inverse of current gbuffer model-view | matrix engine | every switch, **always upload** | v0.1 |
| `gbufferPreviousModelView` | `mat4`; last accepted frame's captured model-view | temporal matrix store | every switch, **always upload** | v0.1 |
| `gbufferProjection` | `mat4`; projection captured after camera setup at first clear | gbuffer matrix signal | every switch, **always upload** | v0.1 |
| `gbufferProjectionInverse` | `mat4`; inverse of current gbuffer projection | matrix engine | every switch, **always upload** | v0.1 |
| `gbufferPreviousProjection` | `mat4`; last accepted frame's captured projection | temporal matrix store | every switch, **always upload** | v0.1 |
| `shadowProjection` | `mat4`; Phase 8 shadow-camera projection | shadow signal; pending identity | every switch, **always upload** | interface v0.1; value v0.2 |
| `shadowProjectionInverse` | `mat4`; inverse of shadow projection | matrix engine | every switch, **always upload** | interface v0.1; value v0.2 |
| `shadowModelView` | `mat4`; Phase 8 shadow-camera model-view | shadow signal; pending identity | every switch, **always upload** | interface v0.1; value v0.2 |
| `shadowModelViewInverse` | `mat4`; inverse of shadow model-view | matrix engine | every switch, **always upload** | interface v0.1; value v0.2 |
| `centerDepthSmooth` | `float`; center depth in `[0,1]`, tick-domain EMA | sync center-depth source at frame begin | every switch, skip equal | v0.1 |
| `atlasSize` | `ivec2`; current atlas width/height while atlas is bound | atlas signal; pending `(0,0)` | immediate if active + every switch | interface v0.1; value v0.5 |
| `terrainTextureSize` | `ivec2`; documented unused, deterministic `(0,0)` | once neutral provider | every switch, skip equal | v0.1 |
| `terrainIconSize` | `int`; documented unused, deterministic `0` | once neutral provider | every switch, skip equal | v0.1 |

Near/far are fixed at “0.05 / renderDistance×16”
(`docs/research/v1/RESEARCH.md:1359`). The two terrain metrics remain present because the shipped
document declares them but calls them “not used”
(`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:169`); zeros preserve linked-program default
semantics without inventing a Minecraft query.

#### 4.4.4 Per-draw dynamics

| Uniform | Type and exact value | Provider / acquisition | Activation | Milestone |
|---|---|---|---|---|
| `entityColor` | `vec4`; current hurt/flash tint; neutral `(0,0,0,0)` outside a scoped producer | Phase 7 scoped signal | immediate if active + every switch | v0.1 |
| `entityId` | `int`; Phase 9 alias-mapped entity ID; 0 outside scope | Phase 9 scoped signal | immediate if active + every switch | interface v0.1; value v0.3 |
| `blockEntityId` | `int`; Phase 9 alias-mapped block ID for current block entity; 0 outside scope | Phase 9 scoped signal | immediate if active + every switch | interface v0.1; value v0.3 |
| `blendFunc` | `ivec4`; effective draw-state `(srcRGB,dstRGB,srcAlpha,dstAlpha)`, or zeros while blending is disabled | GlStateManager observation plus effective Phase 4 blend-lock overlay | immediate if active + every switch | v0.1 |
| `instanceId` | `int`; 0 original, 1…N copy | Phase 7 draw-loop signal | immediate before each draw + every switch | interface v0.1; composite value v0.5 |

All five are excluded from Phase 11's expression-input view. That exclusion is contract-visible:
`docs/research/v1/RESEARCH.md:1369` calls them “Per-draw dynamics (excluded from custom-uniform
expressions)”.

### 4.5 Smoothing mathematics

All four half-life directives are interpreted in **ticks**. Pintonium's implementation labels its
input as deciseconds/two ticks
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java:45`)
and multiplies by 0.1 seconds
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java:53`).
RC3 explicitly calls for a per-tick formula, so Pintonium supplies
the closed form, not the unit.

For a target \(x\), prior accumulator \(s\), elapsed smoothing time \(\Delta t\) in ticks, and
half-life \(h\) in ticks:

\[
d =
\begin{cases}
0 & h \le 0 \\
2^{-\Delta t/h} = e^{-\ln(2)\Delta t/h} & h > 0
\end{cases}
\]

\[
s' = x + (s-x)d
\]

Equivalently, \(\alpha=1-d\) and \(s'=s+\alpha(x-s)\). The implementation evaluates in `double`,
casts the final value to float, and advances once per accepted frame. Exact edge rules:

1. the first valid sample initializes \(s=x\) with no fade;
2. \(\Delta t = 0\) retains \(s\);
3. negative/regressing time is an invariant failure; the affected smoother retains its previous
   value and reports once;
4. \(h=0\) snaps to the target;
5. non-finite inputs never enter the accumulator; and
6. world-epoch change reinitializes on the new world's first sample.

`smoothingTimeTicks` is a monotonic game-time coordinate supplied by `mod.glue`, derived from the
world tick plus the current render partial tick. It does not advance while game time is paused.
`frameTime` remains elapsed wall/render seconds and is not reused as the smoothing clock. Separating
them prevents a stalled frame from being counted as several logical ticks.

#### Wetness

The target is `clamp(rainStrength,0,1)`. If target > accumulator, use
`wetnessHalflife`; otherwise use `drynessHalflife`. Equality leaves the accumulator unchanged.
The two fields are independently carried from Phase 3 and independently tested. This directly avoids
PD B1, whose source routes `drynessHalflife` into `wetnessHalfLife`
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/properties/PackDirectives.java:254`
and the next line).

#### Eye brightness

The formula is applied independently to block and sky components with the same
`eyeBrightnessHalflife`. Accumulators remain continuous doubles. The pack-facing `ivec2` is obtained
by truncation toward zero, then clamped to 0…240. Inputs are non-negative, so this is floor. This
matches the verified reference mechanic, which constructs ints with casts
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CommonUniforms.java:113`),
while retaining the Appendix D type/range. The rounding rule
is a Phase 6 decision because the shipped contract says only “smoothed”; it is covered by T2
conformance rather than claimed as independently documented OptiFine behavior.

#### Center depth

The raw value is clamped to `[0,1]`, then uses the same formula with
`centerDepthHalflife`. A provider-unavailable frame retains the prior accumulator and does not
advance it. On the first frame with a sample, the accumulator initializes to that sample. The
center-depth read itself occurs once, before resize/clear, only when Phase 3 reports that some
materialized program declares `centerDepthSmooth`.

### 4.6 Frame-begin ordering and temporal snapshots

`beginFrame` returns `ACCEPTED` only for a new adopted-current-generation frame and then performs this exact
sequence:

1. validate registry generation and world epoch;
2. rotate `cameraPosition` into `previousCameraPosition`;
3. rotate the last successfully captured gbuffer matrices into both `gbufferPrevious*` cells;
4. sample tick state if the logical tick changed;
5. sample frame state exactly once and install the new current camera/player/render values;
6. read the previous frame's main depth attachment at
   `(floor(priorFramebufferWidth/2), floor(priorFramebufferHeight/2))`;
7. advance wetness, eye-brightness, and center-depth smoothers once; and
8. publish the immutable `FrameSnapshot` and fire no user callback.

The same `(worldEpoch, frameId)` returns `DUPLICATE`; a lower frame ID or obsolete world epoch
returns `REJECTED_STALE_FRAME`; a registry-generation mismatch returns `REJECTED_GENERATION`.
Those three outcomes perform no sampling, smoothing, snapshot rotation, event publication, or GL
work. Phase 7 may proceed to resize/clear only on `ACCEPTED` or `DUPLICATE`; on either rejection it
must abandon the shader draw and reacquire the current publication. Thus only after an accepted or
duplicate `beginFrame` return may Phase 7 allow Phase 5 to resize, clear, or replace buffers. This is
the exported ordering contract required by
`docs/design/v2.0-RC3/DESIGN.md:1721`, “before any buffer resize or clear”.

The center-depth dimensions and depth source refer to the framebuffer containing the completed
previous image. Width/height ≤0 produce `Unavailable`, not negative coordinates. The source's
`mod.glue` implementation uses Phase 1's synchronous
`FramebufferService.readDepthPixel`; the exact verb is published at
`docs/phase1/v14/PHASE_1_DOC.md:2920`.

Gbuffer matrices are not sampled in `beginFrame`. Phase 7 calls
`captureGbufferMatrices(frameId,...)` exactly once after vanilla camera setup at the ordinal-zero
clear anchor. The prior matrices have already been snapshotted, so the call can overwrite current
matrices without destroying temporal state. A second capture for the same frame is an invariant
diagnostic and is ignored; a missing capture leaves main-program matrix cells invalid so only those
uniforms are disabled for that frame.

The Pintonium evidence validates the capture source, not the exact hook contract:
`reference-src/pintonium-9c2fcc1/forge122/src/main/java/org/taumc/celeritas/mixin/core/terrain/ActiveRenderInfoAccessor.java:11`
and
`reference-src/pintonium-9c2fcc1/forge122/src/main/java/org/taumc/celeritas/mixin/core/terrain/ActiveRenderInfoAccessor.java:16`
expose vanilla's projection/model-view buffers, and
`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:102`
places capture after the first clear. Phase 7 owns the
final injection catalog and must preserve the “after camera setup, once” semantics.

### 4.7 Matrix representation, inversion, and overwrite rules

`Matrix4Value` is column-major in the same element order passed to
`UniformService.uploadMatrix4(..., transpose=false)`. Capture copies all 16 floats immediately;
the provider's FloatBuffer position/limit and later mutation cannot affect it.

Inversion uses a deterministic pure-Java 4×4 algorithm in `engine.uniforms.matrix`, computed once
when a current matrix signal arrives. It computes in `double`, returns finite floats, and reports a
singular/non-finite result instead of manufacturing an inverse. A bad inverse disables only the
corresponding `*Inverse` uniform; the original matrix remains valid. Previous values are copies,
never suppliers that mutate when read.

Overwrite rules:

- previous camera/matrices rotate only at an accepted frame boundary;
- repeated program switches never rotate temporal state;
- a frame that performs no matrix capture does not replace the last valid current matrices;
- a world-epoch reset initializes previous=current on the first valid new-world sample, preventing
  cross-world motion vectors; and
- shadow matrices have no `previous*` contract and update only on Phase 8's shadow-camera signal.

Pintonium's `MatrixUniforms.Previous.get()` advances previous state when the supplier is read
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/MatrixUniforms.java:75`).
That mechanism is deliberately not adopted: a program switch
must not advance a per-frame snapshot.

### 4.8 `centerDepthSmooth` decision

#### Candidate A — selected

Once per frame, synchronously read the center depth through
`FramebufferService.readDepthPixel`, then advance the CPU tick-domain EMA in §4.5. This is the
observed contract path: the frame-flow source calls it a “synchronous center-depth readback”
(`docs/research/v1/RESEARCH.md:558`), and Appendix D exposes a default-block `float`
(`docs/research/v1/RESEARCH.md:1365`).

#### Candidate B — rejected for v0.1

The candidate is a 1×1 `R32F` ping-pong smoothing pass and a source-level redirect. The mechanism is
real: Pintonium constructs two `R32F` textures
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pathways/CenterDepthSampler.java:32`)
and renders a 1×1 viewport
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pathways/CenterDepthSampler.java:78`).
It is not adoptable through the currently
published contracts:

1. **Declaration contract.** Packs declare `uniform float centerDepthSmooth;`
   (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:176`). Phase 3's object-like macro is
   injected before the first restored pack line (`docs/phase3/v1/PHASE_3_DOC.md:403`) and therefore
   also substitutes the identifier inside its declaration. With no declaration-aware removal or
   rename operation in Phase 3 §5, the proposed replacement expression turns a legal declaration
   into invalid GLSL.
2. **Fixed-unit contract.** A sampled texture requires a sampler assigned to a unit. Appendix B.3
   fixes all units 0–15 by stage (table beginning at `docs/research/v1/RESEARCH.md:1229`) and
   publishes no
   universally reserved center-depth sampler. Phase 6 cannot silently allocate one dynamically;
   that is a pre-decided rejection at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808`.
3. **Observable value.** The GPU result is behaviorally similar but not bit-identical to the
   CPU-readback value, as RC3 itself records
   (`docs/design/v2.0-RC3/DESIGN.md:1747`). No conformance evidence justifies spending two upstream
   interface changes for a v0.1 optimization.
4. **License-safe path.** Pintonium's declaration-aware transformer is not a source for this
   project; the AGPL transformer dependency is prohibited at
   `docs/design/v2.0-RC3/DESIGN.md:888`.

`[D-P6-1]` selects candidate A. `centerDepthMacroContributor()` consequently returns
`MacroContribution.Empty` for every configuration. Phase 14's PBO/fence item is **not obviated** and
remains the sole async-readback modernization ledger entry. Reconsideration requires a verified,
declaration-safe Phase 3 operation, an explicit sampler/unit contract extension, and T2 evidence.

### 4.9 Fixed sampler maps and re-point algorithm

Phase 6 publishes immutable maps, not an allocator.

| Unit | GBUFFERS | SHADOW | DEFERRED / COMPOSITE / FINAL |
|---:|---|---|---|
| 0 | `texture` | `tex`, `texture` | `colortex0`, `gcolor` |
| 1 | `lightmap` | `lightmap` | `colortex1`, `gdepth` |
| 2 | `normals` | `normals` | `colortex2`, `gnormal` |
| 3 | `specular` | `specular` | `colortex3`, `composite` |
| 4 | `shadowtex0`, `watershadow`, conditional `shadow` | `shadowtex0`, `watershadow`, conditional `shadow` | `shadowtex0`, `watershadow`, conditional `shadow` |
| 5 | `shadowtex1`, conditional `shadow` | `shadowtex1`, conditional `shadow` | `shadowtex1`, conditional `shadow` |
| 6 | `depthtex0` | `depthtex0` | `depthtex0`, `gdepthtex` |
| 7 | `gaux1` | `gaux1` | `colortex4`, `gaux1` |
| 8 | `gaux2` | `gaux2` | `colortex5`, `gaux2` |
| 9 | `gaux3` | `gaux3` | `colortex6`, `gaux3` |
| 10 | `gaux4` | `gaux4` | `colortex7`, `gaux4` |
| 11 | `depthtex1` | `depthtex1` | `depthtex1` |
| 12 | no sampler | no sampler | `depthtex2` |
| 13 | `shadowcolor0`, `shadowcolor` | `shadowcolor0`, `shadowcolor` | `shadowcolor0`, `shadowcolor` |
| 14 | `shadowcolor1` | `shadowcolor1` | `shadowcolor1` |
| 15 | `noisetex` | `noisetex` | `noisetex` |

For each effective program, the `shadow` alias points to unit 5 when its published
`ProgramUniformLayout` contains a sampler-compatible `watershadow` declaration, otherwise unit 4.
This uses the same immutable layout already required for plan validation and means “when
watershadow used” (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:277`); it does not infer
the condition from shadow-buffer count. Both `shadowtex0` and `watershadow` remain at 4 and
`shadowtex1` remains at 5. The unit-11 choice intentionally contradicts the later “GBuffers
Textures” typo at
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:283`; the same shipped file's uniform table
puts it at 11 (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:203`), matching RESEARCH's
ruling.

At runtime:

1. select map kind from Phase 4's effective `StageId`/`StageBand`;
2. take only names present in the effective `ProgramUniformLayout`;
3. locate each name once through the verified bound-program capability published in §5;
4. validate that the declaration is a sampler-compatible type;
5. queue `(name, location, fixedUnit)` in ascending unit then catalog-name order;
6. remove duplicate `(location,unit)` pairs within the plan;
7. on every activation, visit the plan and upload only when that effective program's cached unit
   differs; and
8. apply §4.11's error isolation to attempted sampler uploads.

An absent/optimized-out location is cached as absent. Two different names at different locations may
legally receive the same unit; they are not deduplicated by unit. A name resolving to the same
location with different required units is a plan invariant failure and disables that program's
sampler participant.

Pintonium validates the useful mechanical shape: it queues external sampler integer calls after a
successful location lookup
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramSamplers.java:134`).
Its dynamic `nextUnit` allocation is visible at
`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramSamplers.java:196`
and is rejected. Phase 6 never calls
`TextureService.bindToUnit`; Phase 5 backs the units before drawing.

### 4.10 Phase 4 barrier fulfillment and program caches

Phase 4's verified binding callback now receives both the handle-free effective descriptor/layout
and a callback-scoped capability over its privately held program
(`docs/phase4/v1/PHASE_4_DOC.md:1056`–`:1066`,
`docs/phase4/v1/PHASE_4_DOC.md:1186`–`:1201`). The published shape consumed here is:

```java
public interface BoundProgramUniformAccess {
    ProgramUniformCacheKey cacheKey();
    UniformLocation locate(String exactName);
    BoundProgramActivityToken activityToken();
}

public interface BoundProgramActivityToken {
    boolean isCurrent();
}

public record ProgramUniformCacheKey(
    long registryGeneration,
    ProgramSlotId effectiveProvider,
    ProgramUniformLayoutFingerprint linkedLayout) {}

public interface ProgramBindingParticipant {
    BarrierParticipantResult afterBind(
        ResolvedProgramDescriptor binding,
        BarrierContext context,
        BoundProgramUniformAccess uniforms);
}
```

Phase 4 mints the capability only after binding its private program. It wraps
`UniformService.locate(privateHandle,name)` and exposes no handle, `use`, delete, or link.
`locate` may be called only during `afterBind`; returned locations may be cached until
`registryGeneration` changes, while the access object itself may not be retained. The token alone
may be retained. Phase 4 invalidates it before any later activation (including the same effective
program), fixed-function release, failed-safe/off transition, ready/off replacement, or teardown
(`docs/phase4/v1/PHASE_4_DOC.md:1203`–`:1210`). `isCurrent()` is a pure thread-safe epoch
comparison and performs no GL query. An immediate signal uses already-cached locations and uploads
only while this token is current.

With it, the barrier trace is:

| Phase 4 step | Phase 6 obligation |
|---|---|
| shadow override and backup resolution | accept effective descriptor; never re-resolve |
| restore prior lock | no action |
| bind effective shader program | receive bound-only access; never call `use` |
| sampler participant | execute §4.9 fixed-unit plan |
| built-in participant | snapshot current cells, then execute §4.11 batch |
| custom participant | invoke §4.13 from the same stable built-in view |
| apply effective alpha/blend lock | before upload, derive `blendFunc` from the same effective `BlendSpec`: explicit factors override the observed underlying state, explicit OFF yields zeros, absent uses observed state; Phase 4 then applies exactly that lock |
| participant `Degraded` | name exact disabled uniform/participant scope; keep program active |

Phase 4 promises participants run even when the same handle stays active
(`docs/phase4/v1/PHASE_4_DOC.md:1181`–`:1184`), satisfying the every-switch contract.

`ProgramCache` is keyed by `ProgramUniformCacheKey` and contains:

- immutable `ProgramUniformLayout`;
- sampler and built-in plans;
- cached present/absent locations;
- last uploaded canonical value per location/name;
- disabled scopes; and
- the count of consecutive clean replays following unattributable drains.

The runtime also retains at most one `(activityToken, ProgramCache)` pair for immediate signals.
Fallback children share the effective provider's cache key. A registry generation change discards
the entire map and active pair before any new activation.

### 4.11 Upload batching and per-uniform GL-error isolation

Each participant builds an immutable ordered list of only the commands it will actually attempt.
Every command carries uniform name, type, copied value, location, and upload operation. It then
executes Phase 1's binding protocol literally:

```text
drainErrors()
upload every command in the immutable batch
errors = drainErrors()
if errors empty:
    commit last-uploaded values
else:
    for each same cached command:
        drainErrors()
        upload command
        attributed = drainErrors()
        if attributed reproduces: disable only (generation, effective program, uniform name)
        else: commit that command's last-uploaded value
    if no command reproduces: report unattributable; disable none
```

The replay never resamples a provider, advances time, rotates a previous snapshot, recomputes an
inverse, or reevaluates a custom expression. Phase 1 makes this a binding precondition:
`docs/phase1/v14/PHASE_1_DOC.md:4119` says “reuses the values already computed for this sweep”.

Disable scope is per generation + effective linked program + uniform name. A type error or GL error
in `fogColor` for one program does not disable `fogColor` in a different linked program, any sibling
uniform, the program, or the pack. Diagnostics are `WARN`/`LOG_ONLY` on
`schmaloogium.uniforms`. Absent locations issue no upload and cannot fail.

If a batched drain is non-empty but no replay command reproduces it, the error may be foreign GL or
non-reproducible. The participant returns `Degraded` only for its diagnostic/reporting state, not for
a guessed uniform. Recurring clean replays are rate-limited per frame/program and handed to Phase
7/Phase 1's broader 3→4 escalation path; the uniform system does not loop indefinitely or disable an
innocent value.

Commands that were skipped as equal are not replayed: no GL operation was attempted for them in the
failed window. Matrices, having no equality skip, are always in the attempted batch when declared.
An `IMMEDIATE_IF_ACTIVE` signal uses the same algorithm over only its changed dynamic commands after
checking the activity token immediately before the first upload. Phase 4 invalidates the token
before rebinding, so a signal can never upload a cached location into fixed function or a different
program.

### 4.12 Notifier-to-producer audit

There are no nullable global listener fields. `UniformEventSink` is constructed with the runtime,
is always valid until reset, and every method is safe when no program declares its consumer. Hook
owners call typed methods; they never assign callbacks into Phase 6.

| Signal/notifier | Phase 6 consumer | Required producer and moment | Owner / milestone |
|---|---|---|---|
| frame begin | tick/frame cells, previous snapshots, all three smoothers | before any resize or clear in world-frame orchestration | Phase 7 / v0.1 |
| gbuffer matrix capture | current/inverse gbuffer matrices | after camera setup at first ordinal-zero clear, once per frame | Phase 7 / v0.1 |
| celestial rotation | sun/moon/shadow-light/up vectors | inside sky rotation after FF transforms are established; immediate upload if a shader token is current | Phase 7 invokes; Phase 8 values / v0.2 |
| shadow camera | four shadow matrices | after Phase 8 installs its shadow FF camera, before shadow draw activation | Phase 8 / v0.2 |
| fog mode/start/end/density/color | `fogMode`, `fogDensity`, `fogColor` | GlStateManager-facing fog mutation sites plus frame fallback; immediate upload if active | Phase 7 / v0.1 |
| blend enable/factors | `blendFunc` | every GlStateManager blend mutation updates underlying observation and immediately uploads if active; every activation overlays the effective Phase 4 `BlendSpec` | Phase 7 + Phase 4 descriptor / v0.1 |
| texture bind | `atlasSize` invalidation only; sampler unit integers do not change | atlas bind/stitch lifecycle | Phase 13 / v0.5 |
| normal/specular texture change | no built-in value; future custom/texture bridge invalidation | companion-atlas bind/change | Phase 13 / v0.5 |
| render phase change | no Appendix D `renderStage`; retained as custom-extension signal only | every Phase 4/7 stage transition | Phase 7, Phase 11/G8 consumer later |
| fallback/current entity | `entityId` | scoped entity render push/pop with immediate upload and 0 restoration | Phase 9 values via Phase 7 hook / v0.3 |
| current block entity | `blockEntityId` | scoped TE render push/pop with immediate upload and 0 restoration | Phase 9 values via Phase 7 hook / v0.3 |
| entity color | `entityColor` | hurt/flash color set/reset scope with immediate upload | Phase 7 / v0.1 |
| instance | `instanceId` | immediately upload 0 before original and `i` before each repeated draw; restore 0 | Phase 7 / v0.5 composite loop |
| held items | four held-item uniforms | tick/inventory change after Phase 9 alias resolution | Phase 9 / v0.3 |
| atlas size | `atlasSize` | atlas becomes current/bound; immediate upload if active; reset on reload | Phase 13 / v0.5 |

The blend row is mandatory, not advisory. Pintonium's shared code dereferences its notifier while
registering `blendFunc`
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CommonUniforms.java:52`),
but its hub is merely a nullable field
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/state/StateUpdateNotifiers.java:10`),
producing PD B6. Here the runtime owns the sink before any plan
is built, so declaring `blendFunc` cannot NPE even before Phase 7 wiring exists; it uploads the
neutral zeros and emits one missing-producer diagnostic in implementation bring-up.

Phase 4 applies its alpha/blend lock after the three participants
(`docs/phase4/v1/PHASE_4_DOC.md:1174`–`:1178`). To keep `blendFunc` truthful on that same draw, the built-in
participant does not wait for a later observer callback: it projects the effective provider's
published `BlendSpec` onto the last observed underlying state, uploads that projected value, and
Phase 4 then applies the identical spec. This is pure derivation from the descriptor, not an early
state mutation or a barrier bypass.

The table is the Phase 6 side of the future Phase 7 integration review. Phase 7 must cite each
producer's actual hook-catalog row rather than saying “GlStateManager somewhere”.

### 4.13 Custom-uniform extension point

```java
public interface CustomUniformBridge {
    CustomRefreshResult refresh(
        ResolvedProgramDescriptor program,
        BuiltInExpressionView values,
        CustomUniformUploadSink uploads);
}

public interface BuiltInExpressionView {
    ExpressionLookup lookup(String packFacingName);
}
public sealed interface ExpressionLookup {
    record Present(ExpressionValue value) implements ExpressionLookup {}
    record Absent() implements ExpressionLookup {}
}
public sealed interface ExpressionValue {
    record Float1(float x) implements ExpressionValue {}
    record Int1(int x) implements ExpressionValue {}
    record Float2(float x, float y) implements ExpressionValue {}
    record Float3(float x, float y, float z) implements ExpressionValue {}
    record Float4(float x, float y, float z, float w) implements ExpressionValue {}
    record Int2(int x, int y) implements ExpressionValue {}
    record Int3(int x, int y, int z) implements ExpressionValue {}
    record Int4(int x, int y, int z, int w) implements ExpressionValue {}
    record Mat4(Matrix4Value value) implements ExpressionValue {}
}
public interface CustomUniformUploadSink {
    CustomSubmitResult submit(CustomUploadCommand command);
}
public sealed interface CustomUploadCommand {
    String uniformName();
    record Float1(String uniformName, float x) implements CustomUploadCommand {}
    record Int1(String uniformName, int x) implements CustomUploadCommand {}
    record Float2(String uniformName, float x, float y) implements CustomUploadCommand {}
    record Float3(String uniformName, float x, float y, float z) implements CustomUploadCommand {}
    record Float4(String uniformName, float x, float y, float z, float w)
        implements CustomUploadCommand {}
    record Int2(String uniformName, int x, int y) implements CustomUploadCommand {}
    record Int3(String uniformName, int x, int y, int z) implements CustomUploadCommand {}
    record Int4(String uniformName, int x, int y, int z, int w) implements CustomUploadCommand {}
    record Mat4(String uniformName, Matrix4Value value) implements CustomUploadCommand {}
}
public sealed interface CustomSubmitResult {
    record Accepted() implements CustomSubmitResult {}
    record Rejected(String diagnosticId) implements CustomSubmitResult {}
}
public sealed interface CustomRefreshResult {
    record NoCustoms() implements CustomRefreshResult {}
    record Completed(int submitted, int rejected) implements CustomRefreshResult {}
    record Aborted(String diagnosticId) implements CustomRefreshResult {}
}
```

The default bridge returns `NoCustoms`. `installCustomUniformBridge` rejects null with
`IllegalArgumentException`; the composition thread may call it only after construction and before
the first `beginFrame` or participant activation. The first non-default bridge installs, repeating
that exact instance is a no-op, and a different instance or a late call throws
`IllegalStateException` without changing the installed bridge. Pack replacement, world epoch,
GL-context loss, and shaders-off retain it; close releases it and restores the default. Phase 11
installs one at the composition root, never as a fourth Phase 4 participant.
`BuiltInExpressionView` is an immutable snapshot from the same activation as the built-in batch and
exposes only contract-permitted expression inputs. It excludes all Appendix D.4 dynamics plus
`fogMode`/`fogColor`, consistent with `docs/research/v1/RESEARCH.md:301`.

The bridge runs after the built-in participant on every successful shader activation. Expression
evaluation errors are Phase 11 rung 1 and disable only the custom uniform. Successful custom values
are handed back as typed immutable upload commands and use the same Phase 1 GL-error replay; an
upload error is rung 2 and disables only that custom uniform for that effective program.

Custom definitions may depend on tick/frame built-ins but cannot resample providers. The view
contains current values regardless of whether their GL upload was skipped as redundant.

Lookup uses the exact pack-facing built-in name; unknown, excluded, disabled, or not-yet-valid
values return `Absent`, never null or a neutral invention. Values and commands are immutable,
finite, by-value records; each command contains one custom-uniform name and exactly the scalar,
vector, or 4×4 matrix payload named by its variant. Phase 11 submits in its validated definition
order. Phase 6 resolves locations, checks the command variant against the linked declaration,
rejects a second command for the same name in one refresh (first wins), and preserves accepted
order. Rejection returns a stable diagnostic ID and emits no GL call for that command.

`Completed` counts sink submissions and rejections. `Aborted` means Phase 11 could not finish the
refresh. Phase 6 commits the accepted prefix, in order, during that same activation before the
participant returns; rejected commands remain rejected and later definitions are absent. No
accepted command carries into another activation, whether upload succeeds or is isolated by rung 2.
Phase 6 owns location/type validation, duplicate/order enforcement, diagnostics, and the §4.11
per-command GL-error replay; Phase 11 owns expression syntax, dependency/evaluation policy, and
whether an evaluation failure omits a command or aborts the remaining refresh.

### 4.14 Reset, missing producers, and neutral values

`UniformResetReason` is the closed domain `PACK_REPLACEMENT`, `SHADERS_OFF`, `GL_CONTEXT_LOSS`,
`WORLD_EPOCH`, and `CLOSE`.

Registry replacement, shaders-off, and GL-context-loss transitions use atomic generation adoption
before the replacement's first frame or activation. Pack replacement and shaders-off discard all
program caches and disabled scopes; GL-context loss additionally discards every location. All three
retire the prior generation. Pack replacement retains temporal values only when configuration
semantics and world epoch are unchanged; shaders-off retains borrowed provider references until
close; GL-context loss retains pure temporal values but requires the newly adopted publication
before activation.
World-epoch reset does not change the generation and resets smoothers and previous snapshots. Close
retires the runtime permanently and cannot be followed by adoption. `adoptRegistryGeneration`
accepts exactly `PACK_REPLACEMENT`, `SHADERS_OFF`, and `GL_CONTEXT_LOSS`. Direct `reset` accepts
exactly `WORLD_EPOCH` and `CLOSE`; any other reason fails fast without mutation.
`WORLD_EPOCH` reset follows final old-world use and precedes the next world's `beginFrame`, event,
or participant activation. Terminal `CLOSE` follows final use of all three Phase 6 participants;
Phase 7 then permits no later participant call and initiates Phase 4's atomic publication
teardown operation. Ordinary publication replacement never invokes `CLOSE` and remains exclusively
on `adoptRegistryGeneration(..., PACK_REPLACEMENT)`.

- Pack/registry replacement drops program caches and disabled scopes; it retains temporal values
  only when world epoch and pack semantics are unchanged.
- World epoch resets smoothers and previous snapshots.
- GL-context loss drops every location and requires a new registry publication before activation.
- Shaders-off makes every participant a no-op; terminal close releases provider references.

Neutral values are deliberately few and visible: later-milestone IDs and integer metrics use 0;
`entityColor`/blend use zeros; pending shadow matrices use identity; pending celestial vectors use
zero; pending atlas size uses `(0,0)`. A program declaring a value whose producer milestone has not
landed receives a once-per-pack warning. Neutral values are not claimed as feature support and never
turn a future producer into optional work.

---

## 5. Cross-phase interfaces

### 5.1 Interfaces exposed by Phase 6

| Exposed contract | Exact content | Consumer(s) |
|---|---|---|
| `UniformRuntimeFactory` / `UniformBuildResult` | creation installs the current `PublishedRegistry.generation`; closed `Success(runtime)` / `Failure(non-empty diagnostic ID)` result; success transfers the sole runtime lifecycle, failure has no runtime or GL work | Phase 7 composition/reload |
| `UniformRuntime` / `UniformResetReason` / `RegistryGenerationAdoptionResult` | closed reasons are `PACK_REPLACEMENT`, `SHADERS_OFF`, `GL_CONTEXT_LOSS`, `WORLD_EPOCH`, `CLOSE`; adoption accepts exactly the first three and direct reset exactly the last two; after accepted publication and reacquisition, atomically adopt the replacement's authoritative generation plus reason before its first `beginFrame`, event, or participant activation; unseen inequality adopts and retires prior, equality is idempotent, retired input rejects without mutation, with equality-only comparison and §4.14 state scopes; `WORLD_EPOCH` reset follows final old-world use and precedes next-world use; teardown-only terminal `CLOSE` follows final use of all three Phase 6 participants, after which Phase 7 permits no later participant call and initiates Phase 4's atomic teardown operation; ordinary publication replacement never invokes `CLOSE` and remains exclusively on `adoptRegistryGeneration(..., PACK_REPLACEMENT)`; also frame begin, typed events, three fixed barrier participants, empty center-depth macro contribution, reset; custom bridge install is non-null, composition-thread/pre-use, first-install wins, same-instance idempotent, different/late fail-fast, retained through non-close resets and released on close | Phases 7, 8, 9, 11, 13 |
| `FrameBeginInput` / `FrameBeginResult` | input schema plus `ACCEPTED`, `DUPLICATE`, `REJECTED_STALE_FRAME`, `REJECTED_GENERATION`; only accepted mutates, duplicate is a safe no-op, rejection forbids shader draw | Phase 7 |
| **Frame-begin ordering contract** | `beginFrame` completes world/tick sampling, previous snapshots, and center-depth read **before any Phase 5 resize or clear**; then first-clear matrix capture occurs after camera setup | Phase 7; integration review |
| `UniformEventSink` and immutable sample records | exact §4.2 schemas; world/frame/tick identity; finite/range validation; copy/absence/fallback rules; held-light old-mode mapping; next-activation vs immediate-if-active policy | Phases 7, 8, 9, 13 |
| `SamplerRepointParticipant` | exact §4.9 fixed-map plan, stage variant, conditional `shadow`, error isolation | Phase 4 composition via Phase 7 |
| `BuiltInUniformRefreshParticipant` | Appendix D plan, every-activation visit, cached-value skip, matrices always upload, error isolation | Phase 4 composition via Phase 7 |
| `CustomUniformRefreshParticipant` / `CustomUniformBridge` | ordered third participant; closed refresh result; typed immutable commands submitted in definition order; an aborted refresh commits its accepted prefix in the current activation with no carry-over; Phase 6 validates, deduplicates, diagnoses, and isolates uploads | Phase 11; Phase 4 composition via Phase 7 |
| `BuiltInExpressionView` / `CustomUniformUploadSink` | exact-name `Present(typed value)`/`Absent` lookup over permitted built-ins; closed scalar/vector/mat4 commands and accepted/rejected submission result per §4.13 | Phase 11 |
| `UniformPlatformProvider` / `CenterDepthSource` | exact §4.2 request/result schemas and validation; loader-neutral sampling SPI with no Minecraft or GL-name types | `mod.glue`, Phase 7 |
| `centerDepthMacroContributor` | always `MacroContribution.Empty` under D-P6-1 | Phase 3/4 materialization |
| Fixed sampler maps | immutable canonical name→unit maps in §4.9, including unit-11 ruling | Phase 5 integration cross-check, Phase 7 |

### 5.2 Dependency contracts consumed

#### Phase 1

| Phase 1 §5 contract | Use |
|---|---|
| module layout, C-1…C-4, package placement | pure engine/runtime plus mod.glue providers |
| `GLDevice.uniforms()` overloads and `UniformLocation.isAbsent()` | all typed uploads and absent-location cache |
| `FramebufferService.readDepthPixel` | synchronous v0.1 center-depth sample |
| `GLDevice.drainErrors` and `GLError` | §4.11 attributed replay |
| `RecordingGLDevice`, `ScriptedResponses.depthPixel/glError`, profiles | §8 headless tests |
| diagnostics/log channel | isolated warnings and escalation |

No Phase 1 change is requested. It already publishes every required upload overload and readback
verb (`docs/phase1/v14/PHASE_1_DOC.md:4127`).

#### Phase 3

| Phase 3 §5 contract | Use |
|---|---|
| `PackConfiguration`, schema/fingerprint discipline | sole parsed truth and cache derivation |
| closed `ResourceRequirements` algebra | center-depth enablement and smoothing half-lives with published defaults/order |
| `DeclaredUniformCatalog`, `DeclaredUniform`, `DeclaredGlslType`, attributed locations | final post-materialization declaration/type provenance; consumed through Phase 4's merged effective layout without reopening source |
| reserved `phase6.centerDepthSmoothRedirect` contributor | deliberately returns Empty |
| `CustomExpressionDecl` | Phase 11 bridge inputs; Phase 6 does not parse |
| materialization/catalog fingerprints | layout and program-cache provenance through Phase 4 |

#### Phase 4

| Phase 4 §5 contract | Use |
|---|---|
| `ProgramSlotDescriptor` / `ResolvedProgramDescriptor` / `ProgramUniformLayout` | stage/effective identity, whole provider state, immutable exact-name declaration/type layout, and fixed-function empty layout |
| `PublishedRegistry.generation` / `RegistryFingerprint` | cache invalidation and reload identity |
| `ProgramUniformCacheKey` / `ProgramUniformLayoutFingerprint` | exact generation + effective provider + linked-layout cache identity shared by fallback children |
| `BarrierContext` | Phase-4-issued stage-map selection and frame validation |
| `ProgramBindingParticipant.afterBind` / `BoundProgramUniformAccess` | callback-only exact-name lookup over the private bound program; no handle or program operation |
| `BoundProgramActivityToken` | retainable operation-free current-activation proof for already-cached immediate uploads |
| three fixed `ProgramBindingParticipant` positions | sampler, built-in, custom ordering on every shader activation |
| `BarrierParticipantResult.Degraded` | isolated uniform/participant degradation |
| per-slot `instanceCount` | Phase 7 loop supplies Phase 6 `instanceId` |

Phase 6 never bypasses `PublishedRegistry.barrier`, as Phase 4 explicitly forbids
(`docs/phase4/v1/PHASE_4_DOC.md:1383`–`:1385`).

### 5.3 Adopted and verified dependency contract changes

The original build document requested the following two interfaces. Both now exist in binding §5
surfaces and have completed their required fresh verification; Phase 6 consumes them directly.

1. **Phase 3 — declaration metadata granted.** Every successful `MaterializedSource` carries a
   complete immutable `DeclaredUniformCatalog`: exact name, closed structural GLSL type, declaring
   stage, attributed identifier location, and the materialization-linked fingerprint. It excludes
   uniform blocks and never claims driver activity
   (`docs/phase3/v1/PHASE_3_DOC.md:1115`). Round twenty's literal PASS makes this a valid dependency
   input (`docs/phase3/reviews/PHASE_3_REVIEW_20.md:58`–`:67`).

2. **Phase 4 — merged layout, lookup, and activity granted.** Phase 4 merges equal structural types,
   rejects same-name conflicts before GL, exposes the effective handle-free layout, and passes each
   shader participant callback-scoped `BoundProgramUniformAccess` with the exact cache key and
   retainable operation-free token. Lookup delegates privately to Phase 1; returned locations live
   only within the publication generation; token invalidation precedes every later activation,
   release/off/replacement/teardown
   (`docs/phase4/v1/PHASE_4_DOC.md:1373`–`:1375`). Round eighteen's literal PASS verifies the
   changed interface (`docs/phase4/reviews/PHASE_4_REVIEW_18.md:57`–`:70`).

No `ProgramHandle`, source string, parallel declaration parser, or unverified interface is assumed.

### 5.4 Requested governing clarification

RC3 calls the object-like GPU redirect “legal without an AST transformer”
(`docs/design/v2.0-RC3/DESIGN.md:1746`). Against Phase 3's actual macro placement, it also rewrites
the pack's uniform declaration and is not legal without a declaration-safe source operation.
Request the next `DESIGN.md` candidate qualify Candidate B with both prerequisites identified in
§4.8: declaration-safe rewriting and an explicit fixed sampler-unit extension. This document follows
RC3's stated default—Candidate A when the check is inconclusive—without editing the governing file.

---

## 6. Failure modes & degradation

| Failure | Ladder rung | Behavior |
|---|---:|---|
| custom expression evaluation fails | 1 | Phase 11 omits that command or returns `Aborted`; accepted earlier commands remain isolated |
| custom command has unknown name/type or duplicates a name | 1/2 | sink rejects it with a stable diagnostic; first accepted command wins; no GL call for rejected command |
| one built-in/sampler/custom GL upload reproduces an error | 2 | disable only that uniform for the effective program/generation; warning on `.uniforms`; continue program |
| feature provider fails (center-depth source unavailable, matrix inverse singular) | 2a | disable/retain only that feature value; center depth retains prior sample, bad inverse disables only inverse |
| Phase 4 compile/link/validate fails | 3 | Phase 4 backup chain; Phase 6 has no cache for a failed program |
| uniform layout/type conflicts with Appendix D | 2 | disable only the mismatched uniform; attributed diagnostic; do not coerce |
| sampler plan maps one location to conflicting units | 3 | sampler participant degrades that program; do not issue ambiguous integer uploads |
| batched GL drain is non-empty but replay is clean | 3→4 | mark unattributable, disable no uniform, rate-limit; persistent condition handed to broader pack-level escalation |
| required capability/texture-unit count fails at init | 4 | pack off through existing capability gate; no Phase 6 GL work |
| provider throws or returns non-finite/out-of-range data | 2/2a | catch at seam, retain last valid affected cell or neutral on first sample, diagnose once; unrelated cells continue |
| immediate signal arrives with no current Phase 4 activity token | normal | replace the cell but issue no GL call; next activation uploads it |
| frame ID repeats | — | return `DUPLICATE`; idempotent no-op; Phase 7 may continue |
| frame/world identity regresses or generation mismatches | 5 guard | return the exact §4.6 rejection; draw forbidden; Phase 7 reacquires publication |
| matrix capture missing for frame | 2a | disable current gbuffer matrix set for that frame; previous snapshot remains coherent |
| second matrix capture in frame | — | first wins; invariant diagnostic |
| missing later-milestone producer | planned degrade | neutral value plus once-per-pack warning; declaring the uniform never throws |
| center-depth dimensions/FBO unavailable | 2a | retain previous smoothed depth; first unavailable frame leaves cell invalid |
| location optimized out | normal | cache absent; no upload, no warning |
| unexpected runtime exception crosses uniform entry point | 5 | Phase 1/mod.core boundary disables shaders and resumes vanilla path |

The system never catches a failure by calling raw GL, rebinding a program, or restoring vanilla
state itself. Those would violate Phase 4 and §G4.6 ownership.

---

## 7. Threading & performance notes

### 7.1 Thread ownership

| Component | Thread |
|---|---|
| runtime construction from pure configuration | any caller thread before publication; no GL work |
| `beginFrame`, all event methods, all barrier participants | render thread only |
| `UniformPlatformProvider` / `CenterDepthSource` production implementation | render thread only |
| smoothing, inverse, catalog planning in headless tests | test thread; no affinity |
| custom bridge evaluation | Phase 11 may prepare pure expression plans elsewhere, but activation/evaluation and upload occur on render thread at v0.4 |
| generation adoption for pack/registry replacement or GL-context loss | render thread after accepted publication and authoritative-generation reacquisition, before replacement use |
| direct reset for `WORLD_EPOCH` or `CLOSE` | render thread; world reset is between final old-world and first new-world use; teardown-only close follows final Phase 6 participant use and precedes Phase 7 initiating Phase 4 teardown; ordinary publication replacement never invokes `CLOSE` |

No event is queued asynchronously. A hook observes a value and writes it synchronously before the
draw activation that consumes it. This prevents an entity ID or blend state from crossing draw
scope.

### 7.2 Hot paths and allocations

The hot path is every successful Phase 4 activation. Steady state must allocate nothing:

- program plans, locations, and command slots are built once per effective program/generation;
- value cells reuse primitive storage;
- activation copies values into preallocated command slots, with immutable semantics enforced by
  ownership rather than per-switch record allocation;
- provider sampling occurs once per tick/frame, not per program switch;
- dynamic signals reuse the current cache and issue only changed commands after one token check;
- no stream, iterator allocation, boxing, varargs, matrix copy, or source lookup occurs in the
  activation loop; and
- error replay allocates diagnostics only on failure.

Clean-path GL cost is changed-value scalar/vector uploads plus every declared matrix, followed by
Phase 1's one-query clean drain protocol. Sampler integers usually skip after the first activation
of a linked program, while their plans are still visited every switch.

Location cache cardinality is bounded by:

```text
effective linked programs × (declared Appendix-D built-ins + declared fixed sampler aliases)
```

not by requested fallback slots. A generation replacement drops the old map in one operation.

### 7.3 Synchronous center-depth cost

The v0.1 read is deliberately one pipeline stall per rendered frame **only when declared**. It is
performed before resize/clear so it observes the completed prior image and cannot multiply by
program switches. Phase 14 owns measuring/replacing that stall. Phase 6 records frame time but does
not hide the cost with an uncontracted one-frame queue.

---

## 8. Testability plan

All unit tests live in `:engine` and use scripted providers plus Phase 1's recording facade. No
Minecraft, LWJGL, display, pack source, or image is needed.

### 8.1 Headless unit tests

| Test | Assertions |
|---|---|
| `UniformCatalogCompletenessTest` | every Appendix D name appears once with exact type/provider/cadence/milestone; no extra built-in silently added |
| `FixedSamplerMapTest` | all three stage maps match §4.9; conditional `shadow`; unit 11 ruling; no gbuffer/shadow unit 12 |
| `TickEmaClosedFormTest` | first sample, 1/2/3 half-lives, irregular \(\Delta t\), zero half-life, zero time, asymmetric wet/dry |
| `EyeBrightnessQuantizationTest` | independent components, truncation toward zero, 0…240 clamp |
| `FrameOrderingTest` | call log proves depth/world sampling completes before synthetic resize/clear callback |
| `FrameIdempotenceTest` | duplicate frame does not resample, rotate previous state, increment counters, or advance EMA |
| `TemporalSnapshotTest` | previous camera/matrices equal exactly the prior accepted frame; two switches do not advance; world reset previous=current |
| `MatrixCaptureTest` | copies mutable input, correct inverse, singular inverse isolates only inverse, matrix uploads always occur |
| `ProgramLocationCacheTest` | one lookup/name/effective program/generation; optimized-out absent cached; fallback children share provider cache; generation invalidates |
| `RedundantUploadTest` | unchanged scalars/vectors skip, changed upload, `-0.0` canonicalized, matrices never skip |
| `BarrierOrderTest` | sampler → built-ins → customs after recorded program use; fixed-function outcome invokes none |
| `DynamicSignalUploadTest` | entity/instance/fog changes upload without rebind while token current; invalidated token performs no GL call and next activation catches up |
| `UploadErrorIsolationTest` | batched failure triggers cached replay, one reproduced name disabled, providers not re-entered, siblings remain |
| `ForeignGlErrorTest` | non-empty batch followed by clean replay disables nothing and rate-limits |
| `CenterDepthDecisionTest` | macro contributor always Empty; one read at exact center; disabled when undeclared; no read on unavailable dimensions |
| `NotifierCoverageTest` | every signal enum has a producer-audit row and a non-null sink method; `blendFunc` declaration never needs listener assignment |
| `CustomBridgeOrderTest` | default no-op; stable permitted view; dynamics excluded; custom upload follows built-ins |
| `CustomBridgeLifecycleTest` | null, late, and different-instance installs fail without mutation; same-instance repeat is a no-op; non-close reset retains and close releases |
| `CustomAbortPrefixTest` | accepted-prefix-then-abort uploads that prefix in order in the current activation, omits the suffix, and carries nothing forward |
| `ResetLifecycleTest` | initial generation installed; replacement adoption precedes frame/activation and does not invoke `CLOSE`; equality is idempotent; retired input rejects; generation/context/world reset scopes match §4.14; no stale location use |

`RecordingGLDevice` is the assigned mechanism
(`docs/phase1/v14/PHASE_1_DOC.md:4146`, “recorded-GL run”). Scripted depth answers and GL errors
exercise the two otherwise-driver-shaped paths.

### 8.2 Conformance coverage

- **T0:** packs declaring every built-in and sampler link without type/location crashes; BSL-style
  alternate declarations cannot NPE `blendFunc`.
- **T1:** static scenes cover fog, rain/wetness, water/lava eye state, item brightness, sky color,
  and sampler routing.
- **T1 camera-path motion:** required for previous-camera/matrix and center-depth temporal behavior.
  RC3 says static captures miss the hardest temporal failures
  (`docs/design/v2.0-RC3/DESIGN.md:669`).
- **T2 classic packs:** compare wetness/brightness rounding, previous-frame motion, unit-11 depth,
  and CPU center depth against OptiFine G6 within Phase 2's tolerance.
- **v0.2 shadow scenes:** cover celestial moment and all four shadow matrices.
- **v0.3 ID scenes:** held/entity/block-entity scoped values and reset to 0.
- **v0.4 custom scenes:** built-ins visible before custom evaluation; dynamics remain excluded.
- **v0.5 atlas/instance scenes:** atlas-size changes and instance sequence 0…N.

No pack or rendered image enters the repository. Derived artifacts follow the hash/provenance policy
at `docs/design/v2.0-RC3/DESIGN.md:691`.

### 8.3 Implementation gate

The Phase 6 implementation gate is met when:

1. all §8.1 tests are green against at least two recorded capability profiles;
2. the recording log proves documented cadence, location-cache, sampler-map, and skip behavior;
3. the error replay test proves providers are evaluated once;
4. the frame log proves center depth precedes resize/clear; and
5. integration compilation consumes the verified Phase 3 declaration and Phase 4 layout/access
   contracts without source reopening, a parallel parser, or test-only handle leakage.

---

## 9. Milestone staging

| Component | Architected | Implemented/wired | Notes |
|---|---:|---:|---|
| catalog, cadence, providers, frame snapshot, CPU smoothing | v0.1 | v0.1 | Phase 6 core |
| synchronous center depth + empty macro contribution | v0.1 | v0.1 | PBO remains Phase 14 |
| fixed sampler maps and sampler participant | v0.1 | v0.1 | backing textures Phase 5 |
| built-in participant, location/value caches, error replay | v0.1 | v0.1 | consumes verified P3/P4 contracts |
| gbuffer capture/inverse/previous machinery | v0.1 | v0.1 | hook invoked by Phase 7 |
| frame/fog/blend/entityColor producers | v0.1 | v0.1 with Phase 7 | audit required |
| custom bridge interface/default participant | v0.1 | v0.1 no-op | Phase 11 plugs in v0.4 |
| shadow matrices and celestial value producers | v0.1 | v0.2 | Phase 8 |
| held-item/entity/block-entity values | v0.1 | v0.3 | Phase 9 |
| custom-expression evaluation | v0.1 extension | v0.4 | Phase 11 |
| atlas-size producer | v0.1 | v0.5 | Phase 13 |
| composite `instanceId` loop values | v0.1 | v0.5 | Phase 7 execution |
| GPU center-depth alternative | evaluated now | not scheduled | rejected D-P6-1 |

Architecture-now means every later value owner gets a stable typed slot at v0.1. It does not mean a
neutral value is feature completion.

---

## 10. OQ & spike specifications

No RESEARCH open question is assigned to Phase 6
(`docs/design/v2.0-RC3/DESIGN.md:1691`, “**OQs:** —”), so this section is intentionally short.

The GPU-vs-CPU center-depth choice is a required design decision, not an OQ. Its contract check and
fallback are complete in §4.8. Phase 14's OQ-15/OQ-22 work may measure asynchronous alternatives but
does not reopen D-P6-1 without the declaration/unit prerequisites.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale |
|---|---|---|
| D-P6-1 | select synchronous CPU `centerDepthSmooth`; return empty macro contribution | only candidate expressible by current App D, Phase 1, Phase 3, and fixed-unit contracts; §4.8 |
| D-P6-2 | separate acquisition cadence from every-activation refresh | prevents program switches/replay from resampling world state while preserving observable refresh |
| D-P6-3 | interpret half-lives as ticks and use time-corrected closed-form EMA | governing assignment says per-tick; Pintonium deciseconds are evidence for math only |
| D-P6-4 | truncate smoothed eye brightness toward zero after continuous EMA | deterministic `ivec2` rule, verified working reference mechanic, bounded by T2 evidence |
| D-P6-5 | rotate previous camera/matrices only at frame begin, never on provider read | program switches cannot advance a per-frame contract |
| D-P6-6 | map samplers exactly to App B.3; never allocate or bind units | pack-visible fixed numbers and P5/P6 split |
| D-P6-7 | cache by effective linked program + generation, not requested fallback slot | Phase 4 fallback copies the whole provider binding |
| D-P6-8 | use typed always-present event sink rather than assignable nullable notifiers | makes B6 structurally impossible and auditable |
| D-P6-9 | GL-error disable scope is uniform + effective program + generation | rung 2 says one uniform only; avoids cross-program over-degradation |
| D-P6-10 | upload matrices unconditionally; exact-bit skip other types | Appendix D cadence contract |
| D-P6-11 | unused terrain metrics are explicit zero providers | preserves declared types/default behavior without inventing a source |
| D-P6-12 | `blendFunc` is zeros while blend is disabled; an effective Phase 4 lock is overlaid before upload | makes the value match the state Phase 4 applies after participants despite barrier ordering; matches verified disabled-state shape |
| D-P6-13 | per-draw/celestial/fog/atlas signals upload immediately only under a Phase-4-invalidated activity token | satisfies hook-time cadence and `instanceId` between-draw semantics without retaining a program handle or uploading into a different program |
| D-P6-14 | seed each temporal smoother from its first valid target | prevents an invented startup/world-transition fade; subsequent samples use the exact tick-domain EMA |

### 11.2 Contradictions and contract gaps found

1. **Pintonium half-life unit vs governing contract.** Its source says deciseconds
   (`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java:45`);
   RC3 says ticks. RC3/RESEARCH win; §4.5 records the conversion
   disposition.
2. **Pintonium sampler allocation vs App B.3.** Dynamic allocation is a standing rejection; §4.9
   adopts queue/cache mechanics only.
3. **Shipped documentation inconsistency.** `depthtex1` appears as 12 in one table and 11 in its
   uniform table. RESEARCH's explicit unit-11 ruling wins.
4. **GPU candidate vs current Phase 3 macro contract.** RC3's suggested object macro rewrites the
   declaration too. D-P6-1 follows RC3's default and §5.4 requests clarification.
5. **RESOLVED UPSTREAM — Phase 3 active-uniform publication.** Phase 3 §5 now publishes complete
   final `DeclaredUniformCatalog`s and closed structural types; Phase 4 consumes them without
   reopening source. Phase 3 round twenty verifies the grant.
6. **RESOLVED UPSTREAM — Phase 4 participant lookup/activity.** Phase 4 §5 now publishes the merged
   handle-free layout, callback-only bound lookup, exact cache key, and retainable operation-free
   activity token. Phase 4 round eighteen verifies the grant.
7. **Eye-brightness rounding is under-specified by the published pack document.** D-P6-4 fixes a
   deterministic, reference-supported rule and explicitly leaves T2 able to correct it through a
   Phase 6 fix-up if OptiFine evidence disagrees.
8. **RESEARCH custom-input lists disagree internally.** Appendix D labels all five D.4 values
   “excluded from custom-uniform expressions” (`docs/research/v1/RESEARCH.md:1369`), while §3.4/F.6
   enumerate `entityColor`, `entityId`, `blockEntityId`, `fogMode`, and `fogColor`, omitting
   `blendFunc`/`instanceId` (`docs/research/v1/RESEARCH.md:301`). This design follows the required
   Appendix D whole-inventory heading and excludes all D.4 values, plus the separately named fog
   pair. The discrepancy is requested upstream below rather than hidden.

No contradiction with RESEARCH.md's authority was silently resolved.

### 11.3 Items handed onward

**To Phase 7:** implement the frame-begin-before-resize/clear contract; invoke first-clear matrix
capture exactly once; supply frame/fog/blend/entityColor/celestial events; restore scoped dynamics;
compose the three participants in Phase 4's fixed positions; add actual hook coordinates beside every
§4.12 audit row. Do not resample providers from a hook that merely switches programs.

**To Phase 8:** supply all four shadow matrices and celestial/shadow-light values after shadow-camera
setup through the v0.1 event interface. A singular inverse disables only that inverse.

**To Phase 9:** supply main/off-hand alias IDs/light values plus scoped entity/TE IDs, always
restoring 0 in `finally`-shaped hook scopes.

**To Phase 11:** install one `CustomUniformBridge`, use exact-name typed/absent view lookup, submit
closed typed commands in validated definition order, and report the closed refresh result. Do not
install another Phase 4 participant, resolve GL locations/types, or read per-draw dynamics.

**To Phase 13:** feed `atlasSize` only at the atlas bind lifecycle and restore/reset on reload.
Texture/normal/specular changes do not change fixed sampler integers.

**To Phase 14:** D-P6-1 leaves the PBO/fence async-center-depth ledger item live. Measure against the
one-read-per-frame synchronous baseline; synchronous fallback remains mandatory.

**To Phase 6 implementers:** consume the verified §5.3 declaration/layout/access contracts
directly. Do not replace them with source strings, a parallel declaration parser, retained
`BoundProgramUniformAccess`, or `ProgramHandle`.

### 11.4 Requested upstream changes

- **PENDING — RESEARCH.md authority clarification:** reconcile the custom-expression exclusion
  lists described in §11.2 item 8.
  The recommended ruling is the one used here: exclude every value that may change between two
  program activations, including all Appendix D.4 values. Until the authority is clarified,
  §4.13's conservative union policy remains in force.
- **PENDING, NON-BLOCKING — next DESIGN candidate:** qualify the rejected GPU center-depth
  Candidate B with §5.4's declaration-safe rewrite and fixed sampler-unit prerequisites. D-P6-1
  already selects the fully specified Candidate A, so no current implementation contract depends
  on this future wording.
- **GRANTED AND VERIFIED — Phase 3 and Phase 4 dependency contracts:** §5.3 records their exact
  adopted surfaces and literal-PASS reviews.
- **GRANTED — verification manifest:** `verification/targets/phase-6.json` is data-only, pinned to
  RC3 selectors and this `v1` artifact, and has driven the Phase 6 review loop.

This maintenance session does not edit `docs/research/v1/RESEARCH.md`, any `DESIGN.md`, or
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`; their bytes and authority remain unchanged.

---

## 12. Implementation checklist

Ordered so each item has one outcome and one test hook.

| # | Work item | Tag | Test hook |
|---:|---|---:|---|
| 1 | consume the verified Phase 3 `DeclaredUniformCatalog` and Phase 4 `ProgramUniformLayout` / `BoundProgramUniformAccess` contracts from §5.3 | v0.1 | dependency rounds 20/18 literal PASS; compile-time API test |
| 2 | create `engine.uniforms` packages and immutable primitive/vector/matrix records under C-1 | v0.1 | seam tests; mutation/finite validation tests |
| 3 | implement the catalog containing every §4.4 row and three §4.9 maps | v0.1 | `UniformCatalogCompletenessTest`, `FixedSamplerMapTest` |
| 4 | adapt Phase 3 configuration into validated `UniformConfiguration` without source reopening | v0.1 | fingerprint/schema/invariant tests |
| 5 | implement provider SPIs and scripted test providers | v0.1 | provider exception/range isolation tests |
| 6 | implement `UniformCell`, acquisition revisions, frame/tick/signal buckets | v0.1 | cadence table-driven tests |
| 7 | implement tick-domain scalar/vector EMA exactly as §4.5 | v0.1 | closed-form and quantization tests |
| 8 | implement frame IDs, counters, frame-time modulo, world-epoch reset | v0.1 | idempotence/wrap/reset tests |
| 9 | implement previous camera/matrix rotation at frame begin | v0.1 | camera-path temporal tests |
| 10 | implement immutable matrix copy/inversion and singular isolation | v0.1 | known-matrix/inverse/singular tests |
| 11 | implement conditional synchronous center-depth source and exact center coordinate | v0.1 | `ScriptedResponses.depthPixel`, ordering and undeclared tests |
| 12 | implement empty Phase 3 macro contributor per D-P6-1 | v0.1 | materialization test has no `centerDepthSmooth` define |
| 13 | implement initial/replacement generation adoption and `ProgramCache` keyed by effective provider/generation, including absent locations | v0.1 | adoption/lookup/fallback/generation tests |
| 14 | implement sampler plans and deterministic integer upload order | v0.1 | recorded GL calls match §4.9 for every stage |
| 15 | implement built-in upload plans, exact skip, and matrix-always rule | v0.1 | recorded GL redundant/matrix tests |
| 16 | implement immutable activation/dynamic attempted batches and Phase 1 attributed replay | v0.1 | reproduced/unattributable/provider-once/token-invalidated tests |
| 17 | implement typed `UniformEventSink` with scoped reset helpers and no nullable listeners | v0.1 | notifier coverage and B6 regression tests |
| 18 | implement three Phase 4 participants and default custom bridge | v0.1 | barrier order/fixed-terminal tests |
| 19 | implement `BuiltInExpressionView` whitelist and custom upload sink | v0.1 interface | dynamic-exclusion tests |
| 20 | implement `mod.glue` world/frame/center-depth providers with no MC type crossing C-1 | v0.1 | seam test + scripted integration |
| 21 | wire Phase 7 frame/fog/blend/entityColor/matrix producers against §4.12 | v0.1 | hook audit + recorded frame run |
| 22 | run recorded-profile gate and the §8.3 Phase 6 implementation gate | v0.1 | all headless tests green on two profiles |
| 23 | wire Phase 8 shadow/celestial producers | v0.2 | shadow-scene and matrix tests |
| 24 | wire Phase 9 held/entity/block-entity producers | v0.3 | scoped-ID conformance tests |
| 25 | install Phase 11 custom bridge | v0.4 | built-in-before-custom and rung-1/rung-2 tests |
| 26 | wire Phase 13 atlas-size producer | v0.5 | atlas reload/bind tests |
| 27 | wire Phase 7 composite instance loop to values 0…N and restore 0 | v0.5 | recorded multi-draw sequence |
| 28 | run T1 camera-path/weather/water scenes and T2 classic uniform/sampler comparisons | v0.1 onward | Phase 2 manifests/diffs; no committed images |
| 29 | measure synchronous center-depth stall and hand baseline to Phase 14 | v0.5 | profiler ledger with declared/undeclared comparison |

---

*End of PHASE_6_DOC.md. The original §G1.1 build session stopped after this architecture
document; §§0.3–0.17 record the later governed review, fix-up, and dependency-adoption maintenance.
Implementation and any post-loop version roll remain separate work.*
