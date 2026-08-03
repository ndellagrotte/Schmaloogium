# Schmaloogium — Phase 8: Shadow pass — Architecture

## 0. Header

**Phase:** 8 — Shadow pass
**Milestone:** v0.2
**Date:** 2026-08-02
**Governing design:** `docs/design/v2.0-RC3/DESIGN.md`, Part I §G0–§G12 and the Phase 8
assignment at lines 1957–2034. This phase deliberately adopts RC3 for its initial build; it does not
change the governance of any earlier phase.
**Declared dependencies:** Phases 4, 5, 6, and 7.

All dependency gates were checked before their documents were consumed:

- Phase 4 closes with literal `PASS`, zero findings, in
  `docs/phase4/reviews/PHASE_4_REVIEW_18.md:55`–`:70`.
- Phase 5 closes with literal `PASS`, zero findings, in
  `docs/phase5/reviews/PHASE_5_REVIEW_30.md:52`–`:63`.
- Phase 6 closes with literal `PASS`, zero findings, in
  `docs/phase6/reviews/PHASE_6_REVIEW_7.md:50`–`:57`.
- Phase 7 closes with literal `PASS`, zero findings, in
  `docs/phase7/reviews/PHASE_7_REVIEW_19.md:25`–`:42`. The bytes read were the current on-disk
  `docs/phase7/v1/PHASE_7_DOC.md`, including its §0.22 fix-up.

### 0.1 Inputs actually read

Read in the mandated order:

1. `docs/design/v2.0-RC3/DESIGN.md`:
   - all of Part I, §G0–§G12, lines 92–1109;
   - the Phase 8 assignment, lines 1957–2034;
   - only the phase titles/dependency table for other Part II phases, lines 580–608.
2. `docs/research/v1/RESEARCH.md`:
   - §0, lines 11–54, and §1, lines 55–107;
   - §4.5, lines 567–580;
   - Appendix A.3, lines 1154–1191;
   - Appendices B.2–B.3, lines 1216–1254;
   - Appendix D.3, lines 1354–1367;
   - Appendix E rows 1–2, lines 1394–1399.
3. Phase-assigned supporting evidence:
   - `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §10, lines 589–618;
   - `reference-src/pintonium-9c2fcc1/modern/src/main/shaders_java/net/irisshaders/iris/shadows/ShadowMatrices.java`,
     whole file;
   - the camera construction and pass-order regions of
     `reference-src/pintonium-9c2fcc1/modern/src/main/shaders_java/net/irisshaders/iris/shadows/ModernShadowRenderer.java:107`
     and `:225`–`:454`;
   - the shadow program, uniform, texture, buffer, and directive tables in
     `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:61`–`:69`, `:130`–`:170`,
     `:188`–`:235`, `:270`–`:345`, and `:350`–`:412`;
   - only the shadow-pass section of
     `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:350`–`:409`.
4. Verified dependency contracts and only the detailed regions needed to interpret them:
   - `docs/phase4/v1/PHASE_4_DOC.md` §5 plus §3.2–§3.4 and §4.10;
   - `docs/phase5/v1/PHASE_5_DOC.md` §5 plus §3.2–§3.3 and §4.6, §4.10, §4.12;
   - `docs/phase6/v1/PHASE_6_DOC.md` §5 plus §2.2, §4.2, §4.4.2–§4.4.3, §4.7,
     §4.9–§4.12, and §11.3;
   - `docs/phase7/v1/PHASE_7_DOC.md` §5 plus §4.2–§4.4, §4.10, and §11.

### 0.2 Deviations, extra reads, and tool disposition

The following narrow extra reads were necessary and are recorded rather than hidden:

- `docs/research/v1/RESEARCH.md` Appendix D.2 lines 1338–1352 was read because the assignment
  explicitly owns `shadowAngle`, which is not in the listed D.3 table.
- `docs/research/v1/RESEARCH.md` Appendix F.1 lines 1439–1445 was read because the assignment
  explicitly assigns the `shadowTranslucent` engine flag.
- The required Cleanroom MCP query
  `search_cleanroom_api("render pass entity", kind="event")` returned no event. A broader
  framework query found the actual Forge-lineage interop: public
  `ForgeHooksClient.setRenderPass(int)` and `MinecraftForgeClient.getRenderPass()`. Their current
  Cleanroom source was then checked at
  `reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/client/ForgeHooksClient.java:234`–`:238`,
  `reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/client/MinecraftForgeClient.java:48`–`:53`,
  and the pass-aware entity traversal patch at
  `reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch:5`–`:77`.
  There is no event substitute; Phase 8 uses the verified setter/getter pair through glue.
- Cleanroom MCP symbol details were queried for `RenderGlobal`, `ViewFrustum`, `RenderChunk`,
  `CompiledChunk`, `ChunkRenderContainer`, `RenderManager`, `Render`, and
  `GameSettings.thirdPersonView` to specify the additional hook/accessor ledger in §4.13. Vanilla
  method bodies were not searched in the patch-only Cleanroom tree.
- Pintonium's `CommonShadowRenderer` angle region and `CelestialUniforms` were read narrowly to
  disambiguate the modern reference's day/night and celestial-vector conventions. This was a
  genuine math gap after PD §10; the relevant source is
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/CommonShadowRenderer.java:107`–`:129`
  and
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CelestialUniforms.java:20`–`:124`.

No other phase specification, OD section, forbidden transcript, root `*.txt`, web source, or
decompiled implementation source was read. No build, test, verify loop, or adversarial agent was
run; this is the build session.

### 0.3 Legal and provenance posture

- RESEARCH is contract authority. Pintonium is LGPL evidence, never contract. The camera mechanism
  is adopted only through the recorded contract check in D-P8-1.
- The Pintonium transformation boundaries and unverified expression code were not read. No AGPL
  material was used.
- The OptiFine-derived digest contributes behavioral observations only. This document restates
  behavior and does not reproduce decompiled class structure, method structure, or identifiers.
- All new implementation remains GPL-3.0-or-later. Any later incorporation of LGPL code must retain
  notices and mark modifications.

### 0.4 Verification round 1 corrections

The round-1 fix-up defines the previously named public value/callable shapes, makes the requested
Phase 7 execution credential and Phase 5 shadow-operation extensions implementable, and completes
the conformance-map force-shadow and provenance coverage. The §5 interface changes require a fresh
verification round before Phase 8 can close.

### 0.5 Verification round 2 corrections

The round-2 fix-up aligns execution-view and borrowed-binding ownership with R8-1/R8-2 and defines
the complete effective `ShadowPolicy` as the configuration-derived portion of plan identity. The
§5 clarification requires a fresh verification round before Phase 8 can close.

### 0.6 Verification round 3 corrections

The round-3 fix-up adds an explicit generation-scoped publication owner for slot teardown and
restricts hardware-PCF policy to shadow depth buffers. Both §5 changes require a fresh verification
round before Phase 8 can close.

---

## 1. Scope & boundaries

### 1.1 Owned by Phase 8

Phase 8 owns the complete v0.2 shadow-pass transaction:

- deciding whether a published pipeline has an executable shadow pass;
- immutable shadow policy projection and lifecycle;
- sun/moon selection, shadow camera projection/model-view math, and orthographic texel snapping;
- shadow MVP plane extraction, light-direction side-plane synthesis, AABB culling, and the
  `shadowDistanceRenderMul` traversal optimization;
- the second, scoped vanilla terrain setup/traversal and restoration of Phase 7's main traversal;
- forced-third-person state, shadow viewport/camera installation, and guaranteed restoration;
- Phase 4's shadow-barrier context and the exact begin/end of `shadowPass=true`;
- terrain, entity, cloud, depth-split, optional translucent, and entity-pass-1 ordering;
- Phase 6 shadow-matrix and celestial-value production;
- post-pass shadow mipmap scheduling and hardware-PCF feature disposition;
- suppression of vanilla entity blob shadows while real shader shadows are active;
- Phase-8-specific hook/accessor health and diagnostics.

### 1.2 Explicit adjacent ownership

- **Owned by Phase 3:** parsing directives/properties, defaults, aliases, validation, configuration
  fingerprints, resource-requirement algebra, and the master engine-flag map. Phase 8 consumes an
  immutable resolved projection and never reopens shader sources or properties.
- **Owned by Phase 4:** stage/slot identity, `shadow` fallback semantics, compiled-program lifetime,
  requested/effective resolution, the use-program barrier, and the force-shadow override. Phase 8
  defines the interval in which `shadowPass=true`; it never binds a program directly.
- **Owned by Phase 5:** sfb allocation, attachments, formats, extents, shadowcolor sides/flips,
  compare/filter parameters, neutral fallback textures, typed copy implementation, and all texture
  handles. Phase 8 chooses operation order only.
- **Owned by Phase 6:** uniform inventory, locations, upload caches, sampler integer maps, inverse
  calculation, per-uniform failure isolation, and barrier participants. Phase 8 supplies primary
  values through its typed event interface.
- **Owned by Phase 7:** the world-frame transaction, H-FRAME-05 invocation moment, main-camera
  snapshot, main-estate clear/bind after return, publication/reload transaction, and final recovery
  decision. Phase 8 is a synchronous slot implementation and returns before the main clear.
- **Owned by Phase 9:** entity/block-entity/held-item aliases and dynamic IDs. Phase 8 preserves the
  entity scopes through Forge pass interop but does not invent IDs.
- **Owned by Phase 10:** extended vertex attributes and both draw paths. Phase 8 consumes ordinary
  vanilla chunk draws and does not alter their vertex format.
- **Owned by Phase 13:** companion atlases, custom textures, and noise objects. Their future shadow
  bindings enter through Phase 5's fixed-unit snapshot.
- **Owned by Phase 14:** new performance tiers and GL modernization. Phase 8 specifies a bounded
  allocation-free baseline first.
- **Owned by G8/S1:** `shadowcomp`, shadow-target ping-pong execution, and shadowcolor2–7 wiring.
  Phase 8 leaves the StageRegistry identities dormant.

### 1.3 Hard boundaries

Phase 8 does not allocate or delete a framebuffer or texture, compile a shader, parse pack input,
create a fourth barrier participant, implement `shadowcomp`, replace `RenderGlobal`, or add a chunk
renderer. Mixins remain dumb adapters. All policy and math are pure `:engine`; Minecraft, Forge,
Mixin, and LWJGL types remain in `:mod` glue.

The verified dependency surfaces contain several missing operations needed by this assignment.
They are specified as R8 requests in §5.5 and are never treated as granted. In particular, the
current Phase 7 context contains an equality-only `FrameToken` but not the `frameId` that Phase 5's
`ShadowEstateView.beginPass(long,...)` requires
(`docs/phase7/v1/PHASE_7_DOC.md:1181`–`:1183` versus
`docs/phase5/v1/PHASE_5_DOC.md:1372`–`:1381`). This architecture is complete; implementation is
gated on the listed fix-up/reverification path.

---

## 2. Architecture overview

### 2.1 Placement

The intended package shape is:

```text
:engine
  com.schmaloogium.engine.shadow
    ShadowPlanFactory          resolved policy -> immutable plan
    ShadowPassFactory          plan + dependency capabilities -> Phase 7 slot
    ShadowPass                 render-thread transaction/state machine
    ShadowCameraMath           projection/model-view/celestial values
    ShadowFrustumBuilder       six clip planes + synthesized side planes
    ShadowTraversalPlanner     full-view or sun-aligned-prism iterator
    ShadowHookHealth           immutable Phase-8 hook report projection

:mod
  com.schmaloogium.mod.glue.shadow
    MinecraftShadowWorldPort   vanilla traversal/draw/state adapter
    ForgeEntityPassPort        pass 0/1 setter/getter adapter
    ShadowMatrixPort           FF matrix/viewport lease through GL facade
  com.schmaloogium.mod.mixin.shadow
    RenderGlobalShadowMixin    scoped traversal redirects/accessors
    RenderBlobShadowMixin      blob-only suppression redirect
```

The new package slots require the Phase 1 grant R8-3; they are not silently assumed. If the grant
chooses a different public package spelling, these responsibilities move intact without changing
the interfaces below.

### 2.2 Public shape

Illustrative signatures; the closed results and semantics are binding:

```java
public interface ShadowPlanFactory {
    ShadowPlanResult plan(ShadowPlanInput input);
}

public record ShadowPlanInput(
    RegistryFingerprint registry,
    ShadowPolicy policy,
    ShadowHookHealth hookHealth) {}

public sealed interface ShadowPlanResult {
    record Ready(ShadowPlan plan) implements ShadowPlanResult {}
    record NotRequested() implements ShadowPlanResult {}
    record Disabled(ShadowDisableReason reason, String diagnosticId)
        implements ShadowPlanResult {}
}

public record ShadowPolicy(
    OptionalFloat shadowMapFov,
    float shadowDistance,
    float shadowDistanceRenderMul,
    float shadowIntervalSize,
    float sunPathRotationDegrees,
    boolean shadowTranslucent,
    boolean cloudsInShadow,
    ShadowMipmapPolicy mipmaps,
    ShadowPcfPolicy pcf) {}

public interface ShadowPassFactory {
    ShadowPassBuildResult create(
        ShadowPlan plan,
        UniformRuntime uniforms,
        ShadowWorldPort world,
        DiagnosticReporter diagnostics);
}

public sealed interface ShadowPassBuildResult {
    record Ready(ShadowPassPublication publication) implements ShadowPassBuildResult {}
    record Disabled(ShadowDisableReason reason, String diagnosticId)
        implements ShadowPassBuildResult {}
    record Invalid(String diagnosticId) implements ShadowPassBuildResult {}
}

public interface ShadowPassPublication {
    ShadowInvocationSlot slot();
    ShadowPublicationCloseResult close();
}

public sealed interface ShadowPublicationCloseResult {
    record Closed() implements ShadowPublicationCloseResult {}
    record AlreadyClosed() implements ShadowPublicationCloseResult {}
    record Rejected(ShadowPublicationCloseRejection reason) implements ShadowPublicationCloseResult {}
}

public enum ShadowPublicationCloseRejection { WRONG_THREAD, CLOSE_WHILE_INVOKING }

public interface ShadowWorldPort {
    ShadowWorldSample sample(ShadowFrameView frame);
    ShadowStateOpenResult openState(ShadowCamera camera, ShadowExecutionView execution);
    ShadowPortResult setupTerrain(ShadowTraversalPlan traversal, ShadowFrustum frustum);
    ShadowPortResult drawTerrain(ShadowTerrainLayer layer);
    ShadowPortResult drawEntities(ForgeRenderPass pass, ShadowFrustum frustum);
    ShadowPortResult drawClouds(CloudMode mode, ShadowFrustum frustum);
}

public sealed interface ShadowStateOpenResult {
    record Opened(ShadowStateLease lease) implements ShadowStateOpenResult {}
    record Rejected(ShadowPortFailure reason) implements ShadowStateOpenResult {}
    record Failed(ShadowPortFailure reason, String diagnosticId)
        implements ShadowStateOpenResult {}
}

public interface ShadowStateLease {
    ShadowPortResult restore();
}

public sealed interface ShadowPortResult {
    record Succeeded() implements ShadowPortResult {}
    record Rejected(ShadowPortFailure reason) implements ShadowPortResult {}
    record Failed(ShadowPortFailure reason, String diagnosticId) implements ShadowPortResult {}
}

public enum ShadowPortFailure {
    WRONG_THREAD, STALE_EXECUTION, STATE_CAPTURE, TERRAIN_SETUP, DRAW, RESTORE
}

public enum ShadowTerrainLayer { SOLID, CUTOUT_MIPPED, CUTOUT, TRANSLUCENT }
public enum ForgeRenderPass { OPAQUE_ZERO, TRANSLUCENT_ONE }
public enum CloudMode { OFF, FAST, FANCY }

public record ShadowFrameView(
    long worldEpoch,
    long frameId,
    float partialTicks,
    int mainTerrainFrameToken,
    Double3 cameraPosition,
    float skyAngle,
    float sunAngle) {}

public record ShadowCamera(
    Matrix4Value projection,
    Matrix4Value modelView,
    Float3 lightDirectionWorld,
    float shadowAngle,
    CelestialSample celestial) {}

public sealed interface ShadowDisableReason {
    record HookUnavailable(String hookId) implements ShadowDisableReason {}
    record InvalidPolicy(String field) implements ShadowDisableReason {}
    record EstateUnavailable(BufferFailure failure) implements ShadowDisableReason {}
    record RuntimeFailure(String operation) implements ShadowDisableReason {}
}

public record ShadowPlan(
    RegistryFingerprint registry,
    ShadowPolicy policy,
    ShadowCelestialPolicy celestialPolicy,
    ShadowHookHealth hookHealth,
    ShadowPlanFingerprint fingerprint) {}

public record ShadowPlanFingerprint(String canonicalSha256) {}
public record ShadowMipmapPolicy(Set<LogicalBuffer> buffers) {}
public record ShadowPcfPolicy(Set<LogicalBuffer> compareDepthBuffers) {}

public interface ShadowCelestialPolicy {
    CelestialSample sample(float sunAngle);
}

public interface ShadowCameraMath {
    ShadowCamera compute(ShadowFrameView frame, ShadowPolicy policy, Extent2i shadowExtent);
    ShadowFrustum frustum(ShadowCamera camera);
}

public interface ShadowFrustum {
    boolean intersects(AabbValue bounds);
    List<PlaneValue> planes();
}

public record AabbValue(double minX, double minY, double minZ,
                        double maxX, double maxY, double maxZ) {}
public record PlaneValue(double x, double y, double z, double distance) {}

public record ShadowHookRow(String hookId, int expected, int actual, HookDisposition disposition) {}
public record ShadowHookHealth(List<ShadowHookRow> rows, boolean shadowEnabled,
                               ShadowHookFingerprint fingerprint) {}
public record ShadowHookFingerprint(String canonicalSha256) {}
public enum HookDisposition { HEALTHY, FEATURE_DISABLED }
```

All collections above are immutable, reject nulls/duplicates, and iterate in canonical logical-buffer
or hook-ID order. `ShadowPolicy` is the complete effective configuration-derived projection for plan
identity. `ShadowPlanFingerprint` hashes the registry fingerprint plus every policy value and the
hook fingerprint using their canonical encodings; structural equality is by record value, while
publication reuse and stale checks use that fingerprint. `ShadowHookHealth` is owned by the
application health audit and borrowed immutably by plans. `ShadowCameraMath` implements exactly
§4.5–§4.6; `planes()` returns an immutable normalized ordered fixture view and `intersects` is the
total finite-AABB predicate. `ShadowCelestialPolicy.sample` implements §4.5.1/§4.5.4 and returns the
same sample consumed by Phase 6 and `ShadowCamera`.

`ShadowFrameView` and `ShadowExecutionView` are requested Phase 7 grants, not current interfaces.
The world port contains no Minecraft object in `:engine`; its glue implementation is construction-
bound to the current client renderer and validates the borrowed Phase 7 execution view before each
operation.

### 2.3 Relationship and ownership map

```text
Phase 7-owned projection of the immutable Phase 3 configuration
        |
        v
 ShadowPlan (pure, fingerprinted) -----> Phase 6 provider gets celestial policy
        |                                      |
        +---------------------+----------------+
                              v
Phase 7 H-FRAME-05 -> ShadowInvocationSlot.invoke(borrowed context)
                              |
          +-------------------+--------------------+
          |                   |                    |
          v                   v                    v
  Phase 4 barrier      Phase 5 ShadowEstate   Phase 6 event sink
  shadowPass=true      begin/bind/clear/copy  celestial/matrices
          \                   |                    /
           +------------------+-------------------+
                              v
                    ShadowWorldPort (mod glue)
             setupTerrain -> terrain/entities/clouds
                              |
                              v
                  mipmaps -> complete -> restore
                              |
                              v
                  Phase 7 main clear and gbuffers
```

The slot owns no published dependency and retains no invocation context. It may retain the
generation-scoped `ShadowPlan`, its construction-time `UniformRuntime`, world port, and diagnostics
until the enclosing Phase 7 pipeline publication closes.

### 2.4 Core invariants

1. One frame may open at most one shadow snapshot. Every acquired snapshot is completed or aborted.
2. No shadow GL/draw operation occurs before context, registry, estate, plan fingerprint, and hook
   health validation.
3. The main traversal list, camera mode, Forge render pass, blob-shadow state, matrices, framebuffer,
   viewport, culling, active texture, and cached vanilla state are restored in `finally` order.
4. The shadow program is selected only through a Phase-4-issued context whose
   `shadowPass == true`; no direct shader bind exists.
5. Phase 6 receives the primary shadow matrices before the first shadow activation. It computes
   inverses; Phase 8 never uploads a uniform.
6. `shadowtex1` is copied exactly once after all pre-translucent shadow content and before every
   optional shadow-translucent draw.
7. The exact terrain order is `SOLID -> CUTOUT_MIPPED -> CUTOUT`; reference order differences do
   not override RESEARCH.
8. An absent/unavailable shadow estate never blocks the main pipeline; it uses Phase 5's neutral
   bindings and returns `Completed`.
9. `shadowcomp` is never executed at v0.2.

---

## 3. Contract conformance map

### 3.1 Shadow-pass behavior

| Contract item | Design element | Provenance |
|---|---|---|
| pass inside frame begin, before world/main clear | Phase 7 H-FRAME-05 slot and §4.2 transaction | `[V:observed]` `docs/research/v1/RESEARCH.md:567`–`:580`; `docs/phase7/v1/PHASE_7_DOC.md:648`–`:655` |
| force third person, restore afterward | `ShadowStateLease` in §4.4 | `[V:observed]` `docs/research/v1/RESEARCH.md:569`–`:572` |
| ortho ±`shadowDistance`, near 0.05, far 256 | exact matrix in §4.5.2 | `[V:observed]` `docs/research/v1/RESEARCH.md:569`–`:572` |
| perspective when `shadowMapFov` is set | exact square-aspect perspective in §4.5.3 | same |
| sun by day, moon by night, `sunPathRotation` | §4.5.1/§4.5.4 | same; App A.3 at `docs/research/v1/RESEARCH.md:1178` |
| portable camera/snap structure | §4.5 re-derives the projection/model-view/snap values and rejects the reference's perspective bottom-right value and perspective snapping | PD §10 (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:594`–`:618`); `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/modern/src/main/shaders_java/net/irisshaders/iris/shadows/ShadowMatrices.java:13–83]`; contract check against `docs/research/v1/RESEARCH.md:569`–`:572`; D-P8-1/D-P8-2 |
| texel snap by `shadowIntervalSize` | §4.5.5, Java-remainder formula, ortho only | `[V:observed]` `docs/research/v1/RESEARCH.md:572`; behavioral cross-check `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:366`–`:371`; D-P8-1/D-P8-2 |
| shadow MVP planes plus light side planes | §4.6 exact plane algebra | `[V:observed]` `docs/research/v1/RESEARCH.md:573`–`:575` |
| sun-aligned optimized traversal | §4.7 dominant-axis/prism plan + scoped vanilla setup; no modern collector is adopted | `[V:observed]` `docs/research/v1/RESEARCH.md:574`–`:575`; PD §10 (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:605`–`:609`) confirms the 1.12 renderer is absent; D-P8-4 |
| terrain solid -> cutout-mipped -> cutout | §4.8.1 | `[V:observed]` `docs/research/v1/RESEARCH.md:575`–`:577` |
| Forge entity passes | §4.8.2 pass 0/pass 1 protocol | `[V:observed]` `docs/research/v1/RESEARCH.md:576`; Cleanroom verification in §0.2 |
| water-shadow depth split | §4.8.3 exactly one `SHADOW_PRE_TRANSLUCENT` copy | `[V:doc]` `docs/research/v1/RESEARCH.md:1223`–`:1225` |
| optional translucent terrain | resolved `shadowTranslucent` in §4.8.4 | `[V:doc]` `docs/research/v1/RESEARCH.md:577`–`:578`; F.1 `:1441`–`:1444` |
| per-config shadow mipmaps | §4.9 typed post-pass request | `[V:doc]` `docs/research/v1/RESEARCH.md:1172`–`:1175`; R8-2 |
| hardware PCF | Phase 5 construction applies compare policy; Phase 8 gates/diagnoses it | `[V:doc]` `docs/research/v1/RESEARCH.md:1173`; verified dependency at `docs/phase5/v1/PHASE_5_DOC.md:1334`–`:1343` |
| blob-shadow suppression | H8-BLOB-01 redirects only the blob call, retaining fire | `[V:observed]` `docs/research/v1/RESEARCH.md:578`–`:580`; §4.13 |
| clouds only per shadow config | §4.8.1 pre-split optional cloud draw | `[V:observed]` `docs/research/v1/RESEARCH.md:579`–`:580` |
| force root `shadow` program for the shadow draw interval | §4.10: begin immediately before the first shadow draw; end before fixed-function/state restoration | `[V:doc]` assignment `docs/design/v2.0-RC3/DESIGN.md:2000`–`:2001`; Phase 4 barrier `docs/phase4/v1/PHASE_4_DOC.md:1373`–`:1375` |

### 3.2 Appendix A.3 shadow-directive coverage

| A.3 row | Phase 8 disposition | Provenance |
|---|---|---|
| `shadow`/`shadowtex0`/`shadowtex1`/`watershadow` declarations | plan is executable only when Phase 5 reports a requested shadow estate; no rescan | `[V:doc]` `docs/research/v1/RESEARCH.md:1161`; `docs/phase5/v1/PHASE_5_DOC.md:1334`–`:1360` |
| `shadowcolor`/`shadowcolor0/1` declarations | Phase 5 snapshot owns zero-to-two v0.2 color attachments; Phase 8 draws and completes their flips | `[V:doc]` `docs/research/v1/RESEARCH.md:1162`; `docs/phase5/v1/PHASE_5_DOC.md:1349`–`:1352` |
| `shadowMapResolution` / `SHADOWRES` | Phase 5 extent is authoritative; Phase 8 uses it as viewport and rejects non-positive mismatch | `[V:doc]` `docs/research/v1/RESEARCH.md:1167` |
| `shadowMapFov` / `SHADOWFOV` | optional perspective branch in §4.5.3 | `[V:doc]` `docs/research/v1/RESEARCH.md:1168` |
| `shadowDistance` / `SHADOWHPL` | orthographic half-plane and traversal basis | `[V:doc]` `docs/research/v1/RESEARCH.md:1169` |
| `shadowDistanceRenderMul` | positive values enable §4.7 optimization; non-positive values select full-view traversal | `[V:doc]` `docs/research/v1/RESEARCH.md:1170` |
| `shadowIntervalSize` | §4.5.5, default 2.0 | `[V:doc]` `docs/research/v1/RESEARCH.md:1171` |
| `generateShadowMipmap` / `generateShadowColorMipmap` | unioned with per-texture requests in immutable `ShadowMipmapPolicy` | `[V:doc]` `docs/research/v1/RESEARCH.md:1172` |
| `shadowHardwareFiltering`, `0`, `1` | preserved per depth texture; Phase 5 applies compare mode at creation | `[V:doc]` `docs/research/v1/RESEARCH.md:1173`; `docs/phase5/v1/PHASE_5_DOC.md:1339`–`:1342` |
| per-texture shadow mipmap aliases | canonical per-logical-buffer set; generated only after draws/copy | `[V:doc]` `docs/research/v1/RESEARCH.md:1174` |
| per-texture nearest aliases | immutable Phase 5 texture policy; Phase 8 never mutates ordinary min/mag choice | `[V:doc]` `docs/research/v1/RESEARCH.md:1175` |
| `sunPathRotation` | model-view and celestial-vector rotation in §4.5 | `[V:doc]` `docs/research/v1/RESEARCH.md:1178` |
| shadow program `DRAWBUFFERS` | consumed through Phase 4 `ProgramStateBundle` and Phase 5 `ShadowPassSnapshot`; order and duplicates retained | `[V:doc]` generic row `docs/research/v1/RESEARCH.md:1187`; dependency `docs/phase4/v1/PHASE_4_DOC.md:1370` |

The Phase 3 front end remains responsible for recognizing both const and comment forms, including
capitalization aliases. Phase 8 consumes only the resolved result.

### 3.3 Appendix B.2/B.3 shadow rows

| Contract row | Design element | Provenance |
|---|---|---|
| shadowtex0 = everything | real Phase 5 depth attachment; clear then all shadow draws | `[V:doc]` `docs/research/v1/RESEARCH.md:1223`; `docs/phase5/v1/PHASE_5_DOC.md:1334`–`:1343` |
| shadowtex1 excludes shadow translucents | exact split point in §4.8.3 | `[V:doc]` `docs/research/v1/RESEARCH.md:1224` |
| shadowcolor0/1 | Phase 5 typed color attachments and generic completion flip | `[V:doc]` `docs/research/v1/RESEARCH.md:1225`; `docs/phase5/v1/PHASE_5_DOC.md:1384`–`:1393` |
| unit 4 = shadowtex0/watershadow/conditional shadow | Phase 6 fixed sampler plan + requested Phase 5 shadow binding snapshot | `[V:doc]` `docs/research/v1/RESEARCH.md:1235`; `docs/phase6/v1/PHASE_6_DOC.md:855`–`:913`; R8-2 |
| unit 5 = shadowtex1/conditional shadow | same | `[V:doc]` `docs/research/v1/RESEARCH.md:1236` |
| unit 13 = shadowcolor0/shadowcolor | same | `[V:doc]` `docs/research/v1/RESEARCH.md:1244` |
| unit 14 = shadowcolor1 | same | `[V:doc]` `docs/research/v1/RESEARCH.md:1245` |

No unit is dynamically allocated. `shadow` selects unit 5 only when the effective program layout
declares compatible `watershadow`; Phase 6 already owns that exact rule.

### 3.4 Appendix D shadow rows

| Uniform(s) | Producer and timing | Provenance |
|---|---|---|
| `shadowAngle` | `ShadowCelestialPolicy` supplies Phase 6's frame provider; same value drives the camera | `[V:doc]` `docs/research/v1/RESEARCH.md:1348`; dependency semantics `docs/phase6/v1/PHASE_6_DOC.md:607`–`:610` |
| `sunPosition`, `moonPosition`, `shadowLightPosition`, `upPosition` | one `CelestialSample` immediately after camera math and before shadow activation | `[V:doc]` `docs/research/v1/RESEARCH.md:1360`; Phase 6 event at `docs/phase6/v1/PHASE_6_DOC.md:478`–`:482` |
| `shadowProjection`, `shadowModelView` | one `ShadowMatrixSample` after FF camera installation and before activation | `[V:doc]` `docs/research/v1/RESEARCH.md:1364`; Phase 6 event at `docs/phase6/v1/PHASE_6_DOC.md:280`–`:284` |
| `shadowProjectionInverse`, `shadowModelViewInverse` | Phase 6 deterministic inversion of the same two primary matrices | `[V:doc]` `docs/phase6/v1/PHASE_6_DOC.md:785`–`:810` |

All matrices use Phase 6's column-major `Matrix4Value` upload order. A singular inverse disables
only that inverse; it does not suppress the original matrix or the pass.

---

## 4. Detailed design

### 4.1 Plan construction and lifecycle

`ShadowPlanFactory` is pure. It receives an already-resolved policy, registry fingerprint, and
hook-health snapshot. It never accepts source strings, property maps, Minecraft objects, GL
handles, or mutable collections.

Validation is closed and deterministic:

- present `shadowMapFov` must be finite and strictly between 0 and 180 degrees;
- `shadowDistance` must be finite and positive;
- `shadowDistanceRenderMul`, `shadowIntervalSize`, and `sunPathRotationDegrees` must be finite;
- every mipmap buffer index must be in the v0.2 shadow inventory `{depth 0, depth 1, color 0,
  color 1}`; every PCF member must be `{depth 0, depth 1}` and color membership is invalid policy;
- H8-TRAVERSE-01, H8-RESTORE-01, and H8-BLOB-01 must have their expected application count before
  a real plan is enabled.

Invalid values are not clamped into a different pack contract. They produce a stable diagnostic and
a disabled shadow feature; the main pipeline remains valid. Ordinary absence returns
`NotRequested`, not an error.

The lifecycle is:

```text
PLANNED -> READY -> INVOKING -> READY
                   |    |
                   |    +-> DISABLED_RUNTIME (after safely neutralized feature failure)
                   +------> CLOSED (pipeline replacement/shutdown)
```

Only the render thread enters `INVOKING`. A second or re-entrant invocation returns
`Rejected(WRONG_FRAME)` before mutation. `CLOSED` never becomes ready again. Plan identity includes
the registry fingerprint, hook-health fingerprint, and every field of the complete effective
`ShadowPolicy`; reload creates a new plan rather than mutating one in place.

The generation-scoped `ShadowPassPublication` owns the slot. `close()` first rejects a non-render
thread with `WRONG_THREAD`, then accepts `READY` or `DISABLED_RUNTIME`. First success invalidates
the slot epoch, releases every retained service reference, and returns `Closed`; later calls return
`AlreadyClosed`. A call during `INVOKING` returns `Rejected(CLOSE_WHILE_INVOKING)` without mutation;
Phase 7 must first finish or abort that frame, so no close races an invocation.

### 4.2 Invocation transaction

With R8-1/R8-2 granted, `invoke` performs this exact sequence:

1. Validate borrowed Phase 7 execution, frame identity, plan fingerprint, current registry
   generation, buffer estate generation, render thread, and single-entry state. Stale or wrong-frame
   input returns `Rejected` before Phase 5 or GL work.
2. Query `PublishedBufferEstate.shadow()`:
   - `ShadowEstateNotRequested` returns `Completed` without opening state;
   - `ShadowEstateUnavailable` records a feature diagnostic, confirms neutral shadow bindings, and
     returns `Completed`;
   - `ShadowEstateAvailable` continues.
3. Look up the contained `SHADOW` `StageStep`, root `shadow` `ProgramSlotId`, its `PassDescriptor`,
   and the entire `ResolvedProgramDescriptor`. Never reconstruct a fallback or overlay child state.
4. Call `beginPass(frameId, pass, resolved)`. Continue only on `Acquired`. A stale generation is
   mutation-free and returns Phase 7 `Rejected(STALE_PUBLICATION)`; an ordering error aborts the
   shader frame rather than guessing.
5. Sample one `ShadowWorldSample`; validate its echoed identities and camera presence, then derive
   the camera, celestial sample, culling frustum, and traversal plan. An absent/stale camera rejects
   before GL mutation.
6. Open the `ShadowStateLease` with that camera and the already borrowed active
   `ShadowExecutionView`. The lease validates and uses that view while snapshotting every state in
   §4.4 and installing the shadow camera. Phase 7 remains the sole bridge opener and closer around
   `invoke`.
7. Bind and clear the Phase 5 snapshot. Any backend failure leaves the snapshot open; call
   `abortPass` immediately, then follow §6.
8. Call `setupTerrain` with the derived traversal/frustum. Then send `updateCelestial` and
   `updateShadowMatrices` to the construction-bound Phase 6 runtime. Acquire Phase 5's fixed shadow
   texture-binding snapshot and bind it before any barrier participant can upload sampler integers.
9. Acquire `barrierContexts.activation(shadowStep, true)` and call Phase 4 with root `shadow`.
   Branch on every barrier result:
   - `Activated` or `FixedFunction`: draw;
   - `Skipped`: omit the shadow pass and abort its Phase 5 snapshot without flip;
   - `StalePublication`: abort and return Phase 7 `Rejected(STALE_PUBLICATION)`;
   - `ShadersOff` or `FailedSafe`: abort and return `Failed`.
10. Execute §4.8's content order through `ShadowWorldPort`.
11. Issue the requested post-pass mipmap operation. Per-buffer feature failures are isolated; a
    pass-wide backend failure follows §6.
12. Call `completePass`. Only `Completed(frameId)` commits shadowcolor flips. A rejection is not
    coerced into success.
13. Close Phase-8-owned traversal, Forge-pass, and shadow-state leases in reverse order. The
    borrowed Phase 5 binding snapshot simply ceases to be usable when its shadow snapshot completes,
    aborts, or invalidates. Verify the borrowed Phase 7 execution is still current and return
    `Completed`.

Every exit after step 4 runs one cleanup path. A throwable from vanilla is preserved only after
Phase 8 has restored state and reported a closed result to Phase 7; engine exceptions are contained
and converted to stable failures.

### 4.3 Shadow world sample

The glue port copies one immutable sample at invocation:

```java
public record ShadowWorldSample(
    ShadowFrameView frame,
    int viewDistanceChunks,
    int worldMinSection,
    int worldMaxSection,
    CloudMode cloudMode,
    boolean cameraPresent) {}
```

The render thread cannot advance world time between Phase 6 frame begin and this sample. The sample
must echo `worldEpoch`/`frameId`, and its `sunAngle` must bit-equal the Phase 6 provider value used
for that frame. A mismatch is a stale-frame rejection, not a second time basis. Camera position is
the unshifted render-view origin used by chunk rendering. World-section bounds are inclusive and
validated before traversal.

The pure `ShadowCelestialPolicy` is constructed before Phase 6's platform provider, so
`FrameUniformSample.shadowAngle` and Phase 8 camera math call the same function. This requires the
Phase 7 composition grant in R8-1; Phase 8 does not add a second Phase 6 participant or a late
`shadowAngle` upload.

### 4.4 Reversible state lease

`ShadowStateLease` snapshots and restores, in a strict stack:

1. `GameSettings.thirdPersonView` (`field_74320_O`) and the render-view entity/camera basis;
2. current Forge render pass from `MinecraftForgeClient.getRenderPass()`;
3. `RenderManager.isRenderShadow()` state used during the shadow entity calls;
4. projection and model-view matrix modes/stacks;
5. viewport, framebuffer, draw buffers, color/depth masks, active texture, atlas/lightmap binding;
6. cull, blend, alpha, depth-test/depth-func, shade model, and cached `GlStateManager` state touched
   by the pass;
7. Phase 7's main `RenderGlobal.renderInfos` traversal view and the shadow-execution guard; and
8. `RenderGlobal`'s entity-startup and rendered/hidden/total debug counters, so two shadow entity
   traversals cannot consume startup suppression or pollute main-pass diagnostics.

Opening forces third-person value `1`, pushes both FF stacks, loads the supplied shadow
projection/model-view, sets the sfb viewport, and suppresses blob shadows. It does not permanently
alter the user's option. Closing restores the exact saved Forge pass (including `-1`), camera
option, traversal list, matrices, viewport, framebuffer, and cached state even after vanilla
throws.

The lease-scoped `RenderManager` toggle prevents blob draws during offscreen entity traversal and
is restored in `finally`. It is distinct from H8-BLOB-01's publication-scoped availability gate,
which remains active for the later main entity render and is removed only when the shadow feature
becomes unavailable or the pipeline publication closes.

All LWJGL/Forge/vanilla calls live in `mod.glue.shadow`. The pure lease protocol returns closed
`Opened`, `Rejected`, or `Failed` results; the mixin contains no policy.

### 4.5 Camera and celestial math

#### 4.5.1 Angles and day/night selection

Let `s` be the frame's normalized `sunAngle` in `[0,1)`. Day includes the boundary:

```text
day = s <= 0.5
shadowAngle = day ? s : s - 0.5
a = shadowAngle < 0.25 ? shadowAngle + 0.75 : shadowAngle - 0.25
theta = -2*pi*a
```

`shadowAngle` is the Phase 6 uniform value and remains in `[0,1)`. The active light is the sun by
day and moon by night. Boundary tests cover `0`, `0.25`, `0.5`, the next representable value above
`0.5`, and values approaching `1`.

#### 4.5.2 Orthographic projection

With half-plane `H = shadowDistance`, `N = 0.05`, and `F = 256`, conventional mathematical rows
acting on a column vector give:

```text
P_ortho =
| 1/H   0      0               0 |
| 0     1/H    0               0 |
| 0     0     -2/(F-N)        -(F+N)/(F-N) |
| 0     0      0               1 |
```

The sixteen entries are serialized column-major into `Matrix4Value` and uploaded with
`transpose=false`. This is the
re-derived form of the Pintonium structure at
`reference-src/pintonium-9c2fcc1/modern/src/main/shaders_java/net/irisshaders/iris/shadows/ShadowMatrices.java:13`–`:25`,
checked against the authoritative ±half-plane/0.05/256 behavior.

#### 4.5.3 Perspective projection

When `shadowMapFov` is present, shadow maps are square and aspect is exactly `1`. Let
`q = 1 / tan(radians(fov)/2)`:

```text
P_perspective =
| q   0      0                  0 |
| 0   q      0                  0 |
| 0   0     (F+N)/(N-F)        2*F*N/(N-F) |
| 0   0     -1                  0 |
```

The bottom-right entry is `0`, as required by the compatibility-profile perspective transform.
The modern reference file's constructor text and its embedded expected matrix disagree at that
entry; D-P8-1 therefore re-derives the value instead of copying it.

#### 4.5.4 Model-view and celestial vectors

For column vectors, the baseline shadow model-view is:

```text
M0 = T(0,0,-100) * Rx(90 degrees) * Rz(theta) * Rx(sunPathRotationDegrees)
```

This is loaded onto the FF model-view stack. The active light direction used for culling is derived
from the same rotation, never from a separately rounded trigonometric path:

```text
lightDirectionWorld = normalize(inverse(rotation(M0)) * (0,0,1,0))
```

The sign is defined as **from the camera/world toward the active light**. Tests assert that
extruding a caster opposite this direction reaches the receiving region.

Celestial uniform vectors use the current gbuffer model-view from Phase 7's `CameraSnapshot`:

```text
sunWorld  = Ry(-90 degrees) * Rz(sunPathRotation) * Rx(skyAngle*360 degrees) * (0, 100, 0, 0)
moonWorld = -sunWorld
sunPosition  = gbufferModelView * sunWorld
moonPosition = gbufferModelView * moonWorld
shadowLightPosition = day ? sunPosition : moonPosition
upPosition = gbufferModelView * Ry(-90 degrees) * (0,100,0,0)
```

Only xyz are submitted. The same immutable sample is sent before the first shadow activation; the
later main-sky observation may repeat an equal value but may not introduce a different formula.

#### 4.5.5 Texel snapping

Snapping applies only to the orthographic branch. Let `I = shadowIntervalSize` and `(cx,cy,cz)` be
the unshifted camera position. If `I == 0.0f`, snapping is disabled. Otherwise use Java floating
remainder, deliberately not floor-mod:

```text
ox = float(cx) % I - I/2
oy = float(cy) % I - I/2
oz = float(cz) % I - I/2
M = M0 * T(ox,oy,oz)
```

Negative camera coordinates therefore preserve Java's signed remainder. This exactly captures the
observed stabilization quirk; “cleaning it up” to `[0,I)` would move shadows at the origin seam.
Perspective mode uses `M0` without the snap translation.

### 4.6 Shadow frustum and synthesized planes

The frustum operates on camera-relative world coordinates. `C = P * M` is the shadow clip matrix.
With matrix rows `r0..r3`, extract the six inward half-spaces:

```text
left   = r3 + r0       right = r3 - r0
bottom = r3 + r1       top   = r3 - r1
near   = r3 + r2       far   = r3 - r2
```

Normalize each `(nx,ny,nz,d)` by `length(n)`. Non-finite or zero-length planes disable culling for
that frame and select the conservative all-loaded-chunks fallback; they never cull everything.

To preserve casters outside the ordinary shadow frustum, synthesize extrusion planes on the twelve
frustum edges. For each adjacent plane pair `(p+, p-)` whose normal/light dots have opposite signs,
orient the pair so `u = dot(n+,L) >= 0` and `v = dot(n-,L) < 0`, then form:

```text
q = (-v) * p+ + u * p-
```

`dot(q.normal,L) == 0`, so the plane runs parallel to the light direction, and both coefficients
are non-negative, so the original frustum interior stays inside. Normalize `q`, orient it using the
frustum center, and deduplicate parallel equal planes within a fixed epsilon. Retain the base planes
facing the light plus every synthesized silhouette plane; capacity is fixed at ten, matching the
observed maximum. Overflow or ambiguity disables culling and selects `FullLoadedView` for that
frame. This conservative fallback records a diagnostic and cannot omit a caster.

AABB visibility is total and allocation-free. For each plane, choose the AABB vertex maximizing
`n dot x`; if even that vertex is outside, reject. Camera world coordinates are subtracted before
testing. NaN never means outside.

### 4.7 Sun-aligned traversal

`ShadowTraversalPlanner` returns one of two immutable strategies:

```java
public sealed interface ShadowTraversalPlan {
    record FullLoadedView(ShadowFrustum frustum) implements ShadowTraversalPlan {}
    record SunAlignedPrism(
        ShadowFrustum frustum,
        int shadowRadiusChunks,
        int viewRadiusChunks,
        Float3 towardLight) implements ShadowTraversalPlan {}
}
```

Let `V` be vanilla view distance in chunks. If `shadowDistanceRenderMul <= 0`, use
`FullLoadedView`. Otherwise:

```text
D_blocks = shadowDistance * shadowDistanceRenderMul
D = ceil(max(0,D_blocks) / 16)
```

If `D >= V`, use `FullLoadedView`; the optimization has no tighter bound. If `D < V`, enumerate a
sun-aligned prism from `camera - L*D` through `camera + L*V`, with perpendicular half-width `D`
and every loaded vertical chunk section. The iterator chooses the largest absolute light component
as its longitudinal axis, walks monotonically along it, and emits perpendicular slabs in stable
near-to-far order. A near-vertical light uses a deterministic X-major horizontal square rather than
dividing by a near-zero horizontal projection.

For `SunAlignedPrism`, the glue adapter maps each emitted chunk coordinate through
`ViewFrustum.getRenderChunk`, verifies
the returned toroidal chunk's actual position equals the requested coordinate, deduplicates by
render-chunk identity, then applies the extended AABB frustum. Surviving identities fill one
reusable pass-local allowed set. No coordinate outside the loaded view-frustum estate is requested.
The iterator is lazy and reuses fixed primitive storage. `FullLoadedView` has no allowed-set
restriction; the shadow frustum alone conservatively filters vanilla traversal.

The main `RenderGlobal.setupTerrain` has already run at H-FRAME-05. Phase 8 snapshots its
`renderInfos`, invokes one second setup with the shadow frustum and
the captured `mainTerrainFrameToken`. During only that authenticated call, H8-TRAVERSE-01 redirects
`RenderChunk.setFrameIndex(int)` to Phase 8's pass-local identity visited set, so the argument is
never persisted into a vanilla `RenderChunk` and cannot collide with either current or stale main
visit state. The same hook filters neighbor expansion through this plan and disables ordinary
chunk-occlusion visibility for shadow traversal. In `SunAlignedPrism`, its neighbor redirect admits
only chunks in the allowed identity set; after setup, glue filters the shadow `renderInfos` through
that same set as a postcondition while retaining vanilla's order. `FullLoadedView` skips both
membership filters. Phase 8 renders from the resulting shadow list, then restores the exact main
list before returning. Chunk rebuild scheduling performed by vanilla setup is retained; only the
visible-list view is restored.

This is deliberately a vanilla `RenderGlobal` traversal, not Pintonium's replacement collector.
The absence of a working 1.12.2 Pintonium traversal remains a risk in §11.3.

### 4.8 Content and render order

#### 4.8.1 Opaque terrain and clouds

After barrier activation, call the actual world-loop overload for layers in this immutable order:

1. `SOLID`;
2. `CUTOUT_MIPPED`;
3. `CUTOUT`.

Each call runs under the Phase 7 shadow-execution guard, so existing H-TERRAIN hooks do not open a
main Phase 5 snapshot. Phase 4's already-active shadow barrier remains authoritative.

If `cloudsInShadow` is true and the resolved cloud mode is not off, draw clouds after opaque
terrain and before the split. This places the configured cloud caster in the pre-translucent depth
set. Phase 7's main H-CLOUD scope is bypassed by the same guard. A cloud-only failure disables
clouds-in-shadow for the publication and continues the shadow pass.

#### 4.8.2 Entities and Forge render-pass interop

The required MCP event query found no event. Cleanroom retains Forge's pass protocol:
`RenderGlobal.renderEntities` reads `MinecraftForgeClient.getRenderPass()` and filters entities and
tile entities by `shouldRenderInPass(pass)`
(`reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch:5`–`:77`).

The adapter therefore:

1. saves the prior pass and the entity-startup/debug counters;
2. if the saved startup counter is positive, skips both shadow entity traversals without decrementing
   it; the later main traversal remains the sole owner of vanilla startup suppression;
3. otherwise calls `ForgeHooksClient.setRenderPass(0)` and invokes
   `RenderGlobal.renderEntities(viewEntity, shadowFrustum, partialTicks)`;
4. restores the prior pass in a local `finally` if any later operation fails;
5. after the split/translucent terrain, sets pass 1 and invokes the same method once; and
6. restores the original pass and all four counters exactly.

Pass 0 is pre-split; pass 1 is post-split. The Phase 7 entity/block-entity program hooks are guarded
off, but Forge's per-entity/per-tile-entity pass predicates and batching remain active. The
`RenderManager` blob call is suppressed while fire rendering remains intact.

#### 4.8.3 Depth split

After opaque terrain, configured clouds, and entity pass 0, call exactly:

```text
shadow.copyDepth(snapshot, SHADOW_PRE_TRANSLUCENT)
```

`Applied` is the only result that permits post-split content when shadowtex1 exists. A protocol
rejection aborts the frame; a backend failure aborts the shadow snapshot and follows §6. A one-depth
estate treats the typed operation as a successful no-op owned by Phase 5; Phase 8 never infers
buffer count from sampler names.

#### 4.8.4 Translucent terrain and entity pass 1

If `shadowTranslucent` is true, draw `TRANSLUCENT` exactly once after the split. Then execute Forge
entity pass 1 regardless of the terrain flag when §4.8.2's startup rule admitted entity traversal,
because modded entities independently declare their pass. Both occur under the same shadow program
override. No deferred/composite program runs inside the shadow pass.

### 4.9 Mipmaps and hardware PCF

`ShadowMipmapPolicy` is a set of typed `LogicalBuffer`s. Global depth/color enablement expands to
the allocated members of that domain; per-texture flags union into the set. Phase 8 submits the set
after all draws and before `completePass`, so generation observes the final written side. Phase 5
owns the handle, current shadowcolor side, min-filter selection, mipmap call, and restoration on
failure. R8-2 requests the missing typed operation.

One buffer's mipmap failure is rung 2a: mark mipmaps disabled for that buffer, restore its
non-mipmap min filter, and keep the pass/program active. No stale mipmap chain is advertised as
fresh.

Hardware PCF is not toggled per frame. Phase 5 already applies per-depth compare mode and the
legacy depth swizzle during candidate creation
(`docs/phase5/v1/PHASE_5_DOC.md:1334`–`:1347`). Phase 8 checks the resulting estate disposition:
creation failure yields `ShadowEstateUnavailable` plus neutral compare-compatible bindings and
does not abort the main pipeline. Phase 8 performs no duplicate texture-parameter call.
`ShadowPcfPolicy.compareDepthBuffers` admits only shadow depth 0 and shadow depth 1; any shadowcolor
member produces the same deterministic invalid-policy result as any other out-of-domain member.

### 4.10 Uniform wiring and barrier protocol

Phase 6 exposes exactly two relevant event calls:

```java
uniforms.events().updateCelestial(celestialSample);
uniforms.events().updateShadowMatrices(
    new ShadowMatrixSample(worldEpoch, frameId, projection, modelView));
```

Both run after the FF camera is installed and before the first barrier activation. Phase 6 copies
values, derives inverses once, and always uploads matrices on every successful shader activation.
No event carries a `ProgramHandle` or location.

The barrier request is root `shadow` with the Phase-4-issued `activation(shadowStep,true)` context.
Phase 4 validates `shadowPass == (stage == SHADOW && band == SHADOW)` and replaces any requested
slot with root `shadow` before backup resolution
(`docs/phase4/v1/PHASE_4_DOC.md:1373`–`:1375`). The state interval begins immediately before the
first shadow draw and ends before Phase 8 restores fixed function/state. The slot never fabricates
a context or calls `use` directly.

Texture objects must be bound before Phase 6's sampler participant uploads integers. The current
Phase 5 API accepts only a main `PassBufferSnapshot`, not a `ShadowPassSnapshot`; R8-2 supplies the
missing fixed shadow binding snapshot. Until granted, implementation must not rely on prior-frame
bindings.

### 4.11 Blob-shadow policy

Shader shadow availability, not “currently inside the offscreen pass,” controls suppression. If a
ready Phase 8 plan and available shadow estate are active, H8-BLOB-01 redirects only the call from
`Render.doRenderShadowAndFire` to `Render.renderShadow` into a no-op. The enclosing method still
renders entity fire. When the shadow feature is absent, unavailable, disabled, or shutting down,
the original blob call executes unchanged.

Hook application count is a feature gate. If H8-BLOB-01 is missing or over-matched, Phase 8 does
not enable real shadow maps; it leaves vanilla blob shadows intact and the main pipeline otherwise
active. This avoids double shadows without turning an optional Mixin failure into a client crash.

### 4.12 Cleanup and reload

The Phase 7 pipeline publication owns the returned `ShadowPassPublication` and accesses its slot.
Reload/shutdown ordering is:

1. stop admitting new world frames;
2. finish or abort the current Phase 7 frame;
3. close the Phase 8 publication, which invalidates its slot epoch and releases retained service
   references but owns no GL object;
4. close Phase 6 and Phase 5/4 publications in their existing coordinated order;
5. restore blob-shadow behavior and remove Phase-8 hook-state publication.

Closing during `INVOKING` is rejected without mutation; Phase 7 first aborts the frame. A stale
slot cannot neutralize or mutate a newer Phase 5 estate. Dimension change builds a new plan from
the new resolved configuration; camera/traversal state never crosses world epochs.

### 4.13 Additional hook and accessor ledger

The table extends Appendix E's exact format. `ACCESSOR` exposes a field/method to glue but contains
no policy. Every injection uses SRG name plus descriptor and `require=0`, `expect=1`; Phase 8's
health audit decides whether the feature may enable.

| ID | Class (readable) | Obf | SRG target / descriptor / style | Purpose and health |
|---|---|---|---|---|
| H8-SLOT-01 | `EntityRenderer` | `buq` | consume Phase 7 H-FRAME-05 after `RenderGlobal.func_174970_a(Entity,D,ICamera,I,Z)V`; no new injection | exact invocation before main clear; `CORE` through Phase 7 |
| H8-TRAVERSE-01 | `RenderGlobal` | `buy` | inside `func_174970_a(Entity,D,ICamera,I,Z)V`, redirect `RenderChunk.func_178577_a(I)Z`, `func_181562_a(BlockPos,RenderChunk,EnumFacing)RenderChunk`, and the `CompiledChunk.func_178495_a(EnumFacing,EnumFacing)Z` visibility query only while the authenticated shadow execution view is active | use pass-local visited identity without mutating vanilla frame indices, filter neighbor expansion through §4.7, and disable ordinary occlusion only for shadow setup; `FEATURE`, missing disables Phase 8 |
| H8-RESTORE-01 | `RenderGlobal` | `buy` | getter `@Accessor`s for `field_72755_R` (`renderInfos`) and `field_175008_n` (`viewFrustum`); direct public invocation of `func_174970_a(Entity,D,ICamera,I,Z)V` | snapshot/restore main traversal and map optimized coordinates; `FEATURE`, missing disables Phase 8 |
| H8-TERRAIN-01 | `RenderGlobal` | `buy` | invoke `func_174977_a(BlockRenderLayer,D,I,Entity)I` for the four ordered layers; Phase 7 hook guard is R8-1 | ordinary vanilla layer draw, no class replacement; `FEATURE` |
| H8-ENTITY-01 | `RenderGlobal` | `buy` | invoke `func_180446_a(Entity,ICamera,F)V` under Forge pass 0/1; getter/setter `@Accessor`s for `field_72740_G`, `field_72748_H`, `field_72749_I`, and `field_72750_J` | pass-aware entities and tile entities without consuming main startup/debug counters; `FEATURE` |
| H8-CLOUD-01 | `RenderGlobal` | `buy` | invoke `func_180447_b(F,I,D,D,D)V` only when configured | configured cloud caster; `FEATURE`, failure disables clouds only |
| H8-BLOB-01 | `Render` | `bzg` | in `func_76979_b(Entity,D,D,D,F,F)V`, redirect only `func_76975_c(Entity,D,D,D,F,F)V` | suppress blob, retain fire; `FEATURE`, missing disables Phase 8 |
| H8-FORGE-01 | `ForgeHooksClient` / `MinecraftForgeClient` | n/a | public `setRenderPass(I)V` / `getRenderPass()I`; no Mixin | verified Cleanroom pass interop; restore exact prior value |

H8-TRAVERSE-01 never changes behavior for the main setup call or another mod's call: it requires the
borrowed Phase 7 shadow execution identity and current slot epoch. H8-RESTORE-01 returns copied or
borrowed views only for the duration of `invoke`; no vanilla collection is retained across frames.

---

## 5. Cross-phase interfaces

### 5.1 Interfaces exposed by Phase 8

| Exposed contract | Exact content | Consumer(s) |
|---|---|---|
| `ShadowPlanFactory`, `ShadowPlanInput`, `ShadowPlanResult`, `ShadowPlan`, `ShadowPolicy` | pure resolved-policy validation; complete effective configuration-derived identity projection with exact camera/traversal/flag/mipmap/PCF fields; registry/hook fingerprint; no parser, MC, GL, or handle | Phase 7 pipeline construction; Phase 2 headless tests |
| `ShadowCelestialPolicy` | pure total `sunAngle -> day/shadowAngle/celestial rotation` function shared by Phase 6 provider and Phase 8 camera | Phase 6 `mod.glue` provider via Phase 7 composition |
| `ShadowPassFactory`, `ShadowPassBuildResult`, `ShadowPassPublication` | construction of exactly one generation-scoped owner exposing Phase 7's `ShadowInvocationSlot` plus idempotent render-thread close; close from `READY`/`DISABLED_RUNTIME` invalidates the slot epoch and releases retained references, while `INVOKING` rejects without mutation | Phase 7 |
| `ShadowWorldPort` and closed world/state/terrain/draw results | loader-neutral primitive/value interface; Minecraft implementation owns setup/draw/state restoration and Forge pass adapter; every borrowed execution validated | `mod.glue.shadow`; recorded tests |
| `ShadowCameraMath`, `ShadowCamera`, `ShadowFrustum`, `ShadowTraversalPlan` | deterministic column-major camera/celestial math, finite plane set, total AABB predicate, full/prism traversal strategies | Phase 8 runtime; Phase 2 fixtures |
| `ShadowHookHealth`, Phase-8 hook rows | immutable expected/actual counts and enabled/disabled outcome for §4.13 | diagnostics; Phase 2 manifest integration |
| implementation of Phase 7 `ShadowInvocationSlot` | synchronous closed result; retains no invocation context; returns before main clear; absent/unavailable feature returns `Completed` | Phase 7 frame driver |

Phase 8 exposes no GL handle, framebuffer name, program handle, parsed source, mutable vanilla
collection, or physical shadowcolor side.

### 5.2 Phase 4 contracts consumed

| Phase 4 §5 contract | Use |
|---|---|
| `StageRegistry`, `StageId`, `StageBand`, `StageStep` | locate the contained SHADOW occurrence and mint only an accepted shadow context |
| `ProgramSlotId`, `ProgramStateBundle` | root `shadow` identity and complete effective state; no overlay |
| `ProgramRegistryView.resolve`, `ResolvedProgramDescriptor` | Phase 5 begin input; fixed-function root remains a legal depth-only pass |
| `PublishedProgramStateBarrier`, `FrameBarrierContexts`, `UseProgramRequest`, closed `BarrierResult` | sole activation route and force-shadow rule |
| `RegistryFingerprint` / generation | plan/publication pairing and stale rejection |

The binding rows are `docs/phase4/v1/PHASE_4_DOC.md:1368`–`:1378`. Phase 8 never calls the compiler,
publisher, program lookup service, or fallback resolver.

### 5.3 Phase 5 contracts consumed

| Phase 5 §5 contract | Use |
|---|---|
| `BufferEstateView`/inventory/sizing | shadow extent and allocated logical inventory only |
| `PublishedBufferEstate.shadow()` closed result | ordinary absent, neutral unavailable, or available branch |
| `ShadowEstateView.beginPass/bind/clear/copyDepth/completePass/abortPass` | exact transaction and split point |
| `ShadowPassSnapshot` | borrowed frame/pass identity, typed attachments/readable sides, flip set; never retained |
| `ShadowProtocolRejection` and closed results | pre-GL stale/order handling and mandatory abort after backend failure |
| fixed App B.3 table and neutral shadow objects | exact shadow sampler backing after R8-2 grant |

The current shadow surface is exact at `docs/phase5/v1/PHASE_5_DOC.md:1362`–`:1437`; its ordinary
absence/rung-2a neutral behavior is `:1354`–`:1360`.

### 5.4 Phase 6 and Phase 7 contracts consumed

**Phase 6:**

| Phase 6 §5 contract | Use |
|---|---|
| `UniformRuntime.events()` | typed celestial and primary shadow-matrix signals |
| `CelestialSample`, `ShadowMatrixSample`, `Matrix4Value` | copied values with exact world/frame identity |
| fixed sampler map and barrier participants | units 4/5/13/14 and upload on activation |
| deterministic inverses/per-uniform isolation | no duplicate inversion or pass-wide failure for one inverse |

The runtime/event surface is `docs/phase6/v1/PHASE_6_DOC.md:234`–`:291`; Phase 8 handoff is also
explicit at `docs/phase6/v1/PHASE_6_DOC.md:1508`–`:1517`.

**Phase 7:**

| Phase 7 §5 contract | Use |
|---|---|
| `ShadowInvocationSlot`, current context/result algebra | sole frame integration point and outer cleanup policy |
| borrowed `CameraSnapshot`, `PublishedRegistry`, `PublishedBufferEstate`, `FrameBarrierContexts` | current main matrices and dependency credentials |
| result semantics | `Completed` advances to main clear; mutation-free `Rejected` aborts one frame; `Failed` schedules off |
| H-FRAME-05 ordering | pass returns before main bind/clear |

The current context is `docs/phase7/v1/PHASE_7_DOC.md:1178`–`:1190`; borrowing and cleanup semantics
are `:1436`–`:1447`.

### 5.5 Requested dependency changes — flagged, never assumed

| ID | Owner | Required binding change | Why required |
|---|---|---|---|
| R8-1 | Phase 7 | Extend `ShadowInvocationContext` with immutable `ShadowFrameView` (`worldEpoch`, driver `frameId`, `partialTicks`, exact token passed to the main `setupTerrain`, unshifted camera position, sampled sky/sun angles) and a borrowed authenticated `ShadowExecutionView`; have the frame driver open/close its `ShadowExecutionBridge` around `invoke`, and make existing terrain/entity/cloud/frustum hooks bypass main-snapshot policy while that view is active | current equality-only `FrameToken` exposes none of the `frameId`/sample values Phase 5 and Phase 6 require; Phase 7 mentions `ShadowExecutionBridge` only in detail, not binding §5 |
| R8-2 | Phase 5 | Add generation/frame/snapshot-checked shadow fixed-texture bindings, typed post-shadow mipmap generation, and a runtime `degradeToNeutral` transition that aborts an open snapshot, invalidates it, and makes subsequent `shadow()`/unit 4/5/13/14 views coherently unavailable/neutral | current shadow API has bind/clear/copy/complete/abort only; generic texture bindings require a main `PassBufferSnapshot`; no mipmap or safe runtime feature-disable operation exists |
| R8-3 | Phase 1 | Grant `com.schmaloogium.engine.shadow`, `mod.glue.shadow`, and `mod.mixin.shadow` (or exact owner-selected equivalents) in the closed package table | module placement is binding; Phase 8 does not squat in another phase's package |
| R8-4 | Phase 7 pipeline composition | From the existing immutable Phase 3 configuration/dimension view, project exactly one typed `ShadowPolicy` without reparsing; invoke `ShadowPlanFactory` before construction of the Phase 6 platform provider, pass `ShadowCelestialPolicy` into that provider, and construct/own the `ShadowPassPublication` after Phase 6 runtime creation; use its slot for invocation and include the publication in reverse-order rollback/close | current transaction constructs Phase 6 first and has no Phase 8 projection/factory/ownership step; `shadowAngle` must share the camera's policy rather than be sampled by unrelated logic |
| R8-5 | Phase 7/Phase 2 hook reporting | Merge or nest Phase 8's §4.13 immutable hook-health rows in the application report without changing Phase 7 row identities | capture/diagnostics must prove blob/traversal hook health rather than claim shadow capability from configuration alone |

R8-1's requested representation-neutral authentication contract is exact: Phase 7 is the sole
issuer and owner of `ShadowExecutionBridge`; `open(activeExecutionIdentity, slotEpoch)` returns
`Opened(borrowed ShadowExecutionView)` or `Rejected(WRONG_THREAD | ALREADY_ACTIVE)` and issues a
view only for the dynamic extent of that slot invocation. The bridge
exposes `validate(view, activeExecutionIdentity, slotEpoch)` with the closed result `Valid` or
`Rejected(WRONG_ISSUER | INACTIVE | WRONG_EXECUTION | STALE_SLOT_EPOCH | WRONG_THREAD)`, checked in
that order before any hook/glue operation. `Valid` proves both supplied identities equal the
currently open invocation. `close(view)` returns `Closed` or
`Rejected(WRONG_ISSUER | INACTIVE | WRONG_EXECUTION)` and invalidates a valid view before returning
from `invoke`. Opening while active returns `ALREADY_ACTIVE`; no nested/re-entered invocation gets a
second credential. Existing §4.3 rules remain authoritative for thread, retention, re-entry, and
close invalidation.

R8-2 requests these Phase-5-owned public shapes (names illustrative, semantics binding):

```java
ShadowBindingResult shadowBindings(long generation, long frameId, ShadowPassSnapshot snapshot);
ShadowMipmapResult generateShadowMipmaps(long generation, long frameId,
    ShadowPassSnapshot snapshot, ShadowMipmapPolicy policy);
ShadowNeutralizationResult degradeToNeutral(long generation, ShadowNeutralReason reason);
```

All three validate `generation`, then open `frameId` where applicable, then snapshot identity/epoch;
the first rejection wins and is pre-GL/mutation-free. `shadowBindings` returns
`Bound(ShadowBindingSnapshot)` or `Rejected(ShadowProtocolRejection)`. The immutable borrowed
snapshot is Phase-5-owned, valid until the shadow snapshot completes/aborts/invalidates, and contains
exactly units 4, 5, 13, and 14 in ascending order with `Bindable(TextureHandle)` or
`Neutral(TextureHandle)`; Phase 8 neither closes it nor retains it.

`generateShadowMipmaps` returns `Generated(List<ShadowMipmapOutcome>)` or
`Rejected(ShadowProtocolRejection)`. Outcomes are in canonical requested-buffer order and are
exactly `Generated(LogicalBuffer)`, `NotAllocated(LogicalBuffer)`, or
`Degraded(LogicalBuffer,BufferFailure,diagnosticId)`; a degraded buffer has its base non-mipmap min
filter restored before return, while other buffers continue. Aggregate backend failure is therefore
represented only by per-buffer `Degraded` outcomes.

`degradeToNeutral` returns `Neutralized(generation, diagnosticId, boolean openSnapshotAborted)`,
`AlreadyNeutral(generation, diagnosticId)`, or `Rejected(STALE_GENERATION)`. Success atomically
aborts and invalidates any open shadow snapshot without flips, restores safe framebuffer/texture
state, and makes all later `shadow()` calls return `ShadowEstateUnavailable` and all fixed-unit
views return Phase-5-owned neutral objects for that generation. It is idempotent, and no old binding
or pass snapshot remains valid after success.

R8-1, R8-2, and R8-4 alter binding §5 surfaces and therefore require their owners' governed fix-up
and fresh verification before Phase 8 implementation or a dependent integration may consume them.
This document does not edit those dependencies and does not fabricate substitutes.

---

## 6. Failure modes & degradation

| Failure | Ladder rung | Required behavior |
|---|---:|---|
| no shadow buffers requested | normal absence | return `Completed`; vanilla blob shadows remain; no diagnostic |
| sfb creation unavailable | 2a | use Phase 5 neutral shadow bindings, disable Phase 8, keep main program/pipeline active |
| invalid resolved shadow policy | 2a | disable shadow feature with one source-attributed diagnostic; do not clamp |
| missing/over-matched H8 traversal/restore/blob hook | 2a | keep vanilla behavior, disable real shadow feature, report exact hook ID |
| frustum numeric degeneracy | 2a | disable culling/optimization for that frame and over-render loaded chunks; never under-render or crash |
| cloud draw failure | 2a | disable clouds-in-shadow only; terrain/entity shadows continue |
| one mipmap/filter operation fails | 2a | disable mipmaps for that logical buffer, restore base filter, continue |
| hardware-PCF texture setup fails | 2a | Phase 5 yields unavailable/neutral shadow estate; main continues |
| singular shadow matrix inverse | uniform rung 2 | Phase 6 disables only the corresponding inverse uniform; original matrix/pass continue |
| Phase 4 participant degrades | uniform/custom rung 1/2 | retain active shadow program and report returned degradation list |
| root shadow resolves fixed function | normal fallback | render depth/fixed function; this is not compile failure |
| root activation returns `ShadersOff`/`FailedSafe` | 3/5 | abort snapshot, restore all state, return Phase 7 `Failed`; vanilla path remains reachable |
| stale context/registry/estate before GL | protocol | mutation-free `Rejected`; Phase 7 aborts this shader frame but keeps healthy publication |
| Phase 5 bind/clear/copy backend failure | 2a after R8-2; otherwise 5 | abort snapshot; after coherent neutralization disable shadow only. Before R8-2 exists, fail the shader pipeline rather than sample partial targets |
| vanilla draw throws | 5 | abort, restore state/traversal/Forge pass, report `Failed`, then preserve outer throwable policy |
| state restoration cannot be proven | 5 | forbid further shader draws, return `Failed`, Phase 7 schedules shaders off |

Diagnostics use `schmaloogium.shadow` for policy/traversal/pass failures and
`schmaloogium.gl` for backend failures. Repeated per-frame failures are rate-limited by stable
`(pipelineVersion, operation, reason)` keys. No Phase-8-owned failure escapes as a client crash;
an unrelated vanilla throwable is restored around first and then follows Phase 7's outer policy.

---

## 7. Threading & performance notes

### 7.1 Thread ownership

- `ShadowPlanFactory`, camera math, plane construction, and traversal-plan derivation are pure and
  may run off-thread against immutable values.
- `ShadowPassFactory.create` runs on Phase 7's composition/render thread because it receives the
  operational Phase 6 runtime and glue port.
- `invoke`, every Phase 4/5 operation, FF state lease, vanilla setup/draw, Forge pass change, and
  mipmap generation are render-thread-only.
- No Phase 8 type crosses to chunk-build workers. Render chunks are read only through vanilla's
  published compiled-chunk access during the render pass.
- Hook-health publication is immutable after Mixin application and may be read from diagnostics
  threads.

### 7.2 Allocation and hot paths

Per publication, preallocate:

- six base planes, capacity for ten active planes, twelve adjacency pairs, and scratch vectors;
- traversal slab state and a primitive identity-dedup set sized from view distance;
- one reusable list for the copied main `renderInfos` and one for shadow traversal;
- immutable ordered terrain-layer and mipmap-request arrays.

The pass performs no stream pipeline, boxing iterator, per-chunk matrix allocation, or per-AABB
corner array. Plane/AABB testing uses the positive vertex. Matrix trigonometry is computed once per
frame. The optimized traversal cost is proportional to its sun-aligned prism plus loaded vertical
sections; full mode is proportional to loaded render chunks. Both are followed by exact frustum
filtering.

### 7.3 Performance posture and correctness guards

`shadowDistanceRenderMul` is an explicit pack optimization, not permission to omit an in-bound
caster. A brute-force full-view oracle test must prove the prism emits every full-view candidate
that both intersects the extended frustum and lies within the effective distance. Failure disables
the optimization, not shadows.

The second vanilla setup is acknowledged cost. It reuses loaded vanilla chunks and avoids a new
chunk renderer. Phase 14 may profile and optimize the iterator or state capture only after T1/T2
correctness; it may not change camera, plane, split, or order semantics.

---

## 8. Testability plan

### 8.1 Pure headless tests

`engine.shadow` JUnit tests require no Minecraft or GL classpath:

1. **Projection goldens:** ortho H=32/H=110; perspective FOV 90; near/far signs; column-major
   serialization; invalid FOV/distance rejection.
2. **Angle boundaries:** day/night boundary, moon selection, periodicity, sun-path rotation, and
   `shadowAngle` equality between Phase 6 policy and camera output.
3. **Snapping:** positive/negative coordinates, exact interval boundaries, zero interval, default
   2.0, camera motion below/above one cell, and perspective-no-snap.
4. **Celestial vectors:** sun/moon opposition, active-light choice, `upPosition` independence from
   sky rotation, finite output.
5. **Plane extraction:** known identity/ortho frusta; all twelve adjacency pairs; synthesized plane
   parallelism to light; frustum-center orientation; maximum-capacity and degenerate fallback.
6. **AABB predicate:** every corner boundary, camera-relative translation, NaN conservative
   behavior, and comparison to an eight-corner scalar oracle.
7. **Traversal:** all light octants, vertical light, negative chunk coordinates, toroidal alias
   rejection, deterministic order/deduplication, D<V and D>=V branches, and brute full-view
   no-omission property tests.
8. **State machine:** re-entry, stale frame, every closed Phase 4/5/7 result, exactly one complete or
   abort, and no retained borrowed context.

Pintonium's dawn/negative-coordinate sample is a cross-check, not a golden authority. Expected
values are generated from the formulas in §4.5 and compared independently.

### 8.2 Recorded facade and glue tests

With `RecordingGLDevice`/scripted ports and fake verified dependencies:

- assert bind -> clear -> uniform signals -> texture binds -> barrier -> ordered draws -> split ->
  optional draws -> mipmaps -> complete;
- inject failure after each operation and assert reverse restoration, exact abort count, and no
  later draw;
- assert fixed-function root still draws depth;
- assert sampler objects are bound before Phase 6 integer upload;
- assert shadow matrices/celestial values precede first activation;
- assert pass 0 before split and pass 1 after optional translucent draw;
- assert prior Forge pass, third-person option, matrices, viewport, framebuffer, renderInfos, and
  blob behavior restore exactly;
- assert H8-BLOB-01 suppresses only the blob invocation and preserves the fire path;
- assert unavailable estate performs no shadow GL and main frame proceeds.

The Cleanroom integration fixture records `shouldRenderInPass(0/1)` calls for modded entity and tile
entity fixtures and verifies the current RenderGlobal patch behavior.

### 8.3 Conformance scenes and tier gates

Phase 2 runs, with no committed rendered images:

| Scene family | Defect class exercised | Gate |
|---|---|---|
| fixed noon/dusk/night caster and receiver | sun/moon choice, matrix orientation | T1 all classic-shadow packs; T2 classic parity |
| slow camera translation across positive/negative world coordinates | texel crawl and signed-remainder seams | T1 stability; T2 diff sequence |
| moving camera path plus animated sun | temporal matrix/culling stability | T1 motion run |
| water/stained-glass caster | shadowtex0/1 split and `shadowTranslucent` | T1/T2 |
| entity + tile entity with Forge pass 0/1 | interop ordering and blob suppression | T1 integration |
| far uphill/downhill caster with D<V | synthesized planes and sun-prism completeness | T1; compare optimization on/off |
| perspective-shadow pack/config | FOV branch and no ortho snap | T1/T2 where matrix pack supplies it |
| PCF on/off and per-texture nearest/mipmap matrix | compare/filter policy and post timing | headless + T1 |
| clouds enabled/disabled/fast/fancy | config and pre-split order | reference-free T1 self-baseline |

The implementation gate is RESEARCH v0.2: classic packs with shadows at T1 and the first T2 runs.
Pintonium is not an oracle for these scenes; PD §10 confirms its 1.12.2 renderer is absent.

Fixture manifests follow Phase 2/G6: hashes and provenance only, no pack source and no rendered
image committed; `-PupdateGoldens` remains explicit and fails the regeneration run.

---

## 9. Milestone staging

| Component | Milestone | Staging |
|---|---|---|
| pure shadow plan/camera/celestial math | v0.2 | implement first; headless goldens |
| extended frustum and full-view traversal | v0.2 | correctness baseline before optimization |
| `shadowDistanceRenderMul` sun-prism iterator | v0.2 | enable only after full-view oracle proof |
| Phase 7 slot/context/composition grants | v0.2 | dependency fix-up and fresh verification first |
| Phase 5 shadow binding/mipmap/neutralization grants | v0.2 | dependency fix-up and fresh verification first |
| FF state/world port and hook ledger | v0.2 | Cleanroom integration after pure tests |
| terrain/entity/cloud/split render order | v0.2 | ordered recorded test then T1 |
| PCF/filter/mipmap activation | v0.2 | Phase 5-backed, per-feature degradation |
| blob-shadow suppression | v0.2 | health-gated with fire-preservation test |
| Phase 6 matrix/celestial wiring | v0.2 | before first shadow program activation |
| Phase 9 IDs during shadow entities | v0.3 | consume later scopes; no Phase 8 redesign |
| Phase 10 extended shadow terrain vertices | v0.3 | ordinary vanilla draw-path integration |
| Phase 13 custom/companion shadow inputs | v0.5 | enter through fixed binding snapshot |
| shadowcomp and shadowcolor2–7 execution | post-v0.5 G8/S1 | dormant identities only now |
| traversal/GL performance modernization | v0.5 / Phase 14 | profile without contract changes |

Every in-scope Phase 8 component is architected now. Later tags change wiring/capacity, not the
camera, culling, split, or pass transaction.

---

## 10. OQ & spike specifications

No RESEARCH open question is assigned to Phase 8. The absence of a working 1.12.2 reference is a
known implementation risk, not permission to invent an OQ or defer the subsystem.

The traversal and hook-risk experiments are ordinary implementation tests specified in §8, with
closed fallbacks already designed:

- if the sun-prism oracle finds an omission, use `FullLoadedView`;
- if a traversal/blob hook does not apply exactly once, disable shader shadows and retain vanilla;
- if runtime neutralization is not granted, fail the shader pipeline rather than continue with a
  partial shadow target.

These outcomes do not modify RESEARCH §11.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale |
|---|---|---|
| D-P8-1 | Adopt Pintonium's camera-math structure but re-derive every value against RESEARCH/compatibility GL | contract-visible adoption rule; avoids inheriting the perspective-entry inconsistency and modern terrain order |
| D-P8-2 | Apply signed-remainder snapping only in ortho mode | matches the observed OptiFine behavior; floor-mod or perspective snapping changes pack-visible matrices |
| D-P8-3 | Synthesize silhouette planes with `q=(-v)p+ + u p-` | exact light-parallel half-space formula with interior-preserving non-negative coefficients |
| D-P8-4 | Re-run scoped vanilla setup while redirecting its visit marker to a pass-local identity set, render its shadow list, then restore the main list | preserves RenderGlobal/loaded-chunk ownership without corrupting current or stale main-frame visit state |
| D-P8-5 | One Phase 5 shadow snapshot and one Phase 4 root-shadow activation own the whole pass | makes split/flip/cleanup atomic and leaves force-selection with Phase 4 |
| D-P8-6 | Use Forge setter/getter pass 0/1 interop, not an event | the required event query returned none; Cleanroom exposes and RenderGlobal consumes the Forge protocol |
| D-P8-7 | Redirect only vanilla blob rendering, not the combined shadow/fire method | suppresses double shadows without making burning entities disappear |
| D-P8-8 | Treat absent/creation-unavailable shadows as a successful no-op with neutral bindings | Phase 5's verified rung-2a contract keeps the main program alive |
| D-P8-9 | Keep all Minecraft traversal/state behind `ShadowWorldPort` | preserves D-6 and makes pure camera/culling/traversal headless-testable |
| D-P8-10 | Do not execute shadowcomp at v0.2 | explicit scope-out/G8 ownership |
| D-P8-11 | Do not add an unconditional `glFlush` to the pass contract | ordered commands on one GL context already order draws, copy, mipmaps, and completion; the digest's flush is not a RESEARCH contract and would create an avoidable driver-submission policy |

### 11.2 Binding decision disposition

| Decision | Phase 8 disposition |
|---|---|
| D-1 | Cleanroom 1.12.2 only; hooks and Forge pass protocol target its verified API |
| D-2 | shadows only; no renderer replacement or unrelated visual feature |
| D-3 | test gates are the fixed pack matrix, not open-ended Iris parity |
| D-4 | consume Phase 4's superset stage shape while leaving shadowcomp dormant |
| D-5 | targeted Mixins/accessors only; no class replacement |
| D-6 | pure engine math/policy and narrow Minecraft glue are structural |
| D-7 | project and new source remain GPL-3.0-or-later |
| D-8 | Pintonium LGPL evidence is contract-checked; OF evidence is behavior-only; AGPL boundaries excluded |
| D-9 | compatibility-profile FF matrix cooperation is mandatory |
| D-10 | headless and Phase 2 shadow scenes precede implementation exit |

### 11.3 Input contradictions, gaps, and rulings

1. **Cleanroom entity-pass query:** the assigned event query returns none; the actual public Forge
   setter/getter and patched `RenderGlobal` consumer exist. D-P8-6 uses that protocol and records
   the extra read.
2. **Reference terrain order:** the Pintonium modern file draws solid -> cutout -> cutout-mipped at
   `ModernShadowRenderer.java:316`–`:326`; RESEARCH requires solid -> cutout-mipped -> cutout at
   `docs/research/v1/RESEARCH.md:575`–`:577`. RESEARCH wins.
3. **Perspective matrix:** Pintonium's constructor text and its embedded expected matrix disagree
   on the bottom-right term. The compatibility perspective formula in §4.5.3 is re-derived;
   nothing is copied silently.
4. **Perspective snapping:** the modern reference applies its model-view snap independent of FOV;
   the assigned OptiFine behavior digest states snapping is ortho-only. D-P8-2 follows the target
   contract era.
5. **No 1.12.2 traversal reference:** PD §10 says the 1.12 shadow renderer is absent. The §4.7
   algorithm therefore rests on RESEARCH plus vanilla RenderGlobal, with the full-view oracle as
   mandatory risk containment.
6. **Phase 7 context gap:** current context omits the driver `frameId` while Phase 5 requires it,
   and `ShadowExecutionBridge` is not exposed in §5. R8-1 is blocking; no field is inferred from
   `FrameToken`.
7. **Phase 5 post/binding gap:** current shadow surface lacks fixed-unit bindings, mipmap generation,
   and runtime neutralization. R8-2 is blocking; Phase 8 does not call GL on leaked handles.
8. **PCF ownership wording:** the Phase 8 assignment names the feature, while verified Phase 5
   already owns texture-parameter setup. Phase 8 owns policy disposition/timing diagnostics and
   consumes Phase 5's operation; it does not duplicate the GL call.
9. **Digest `glFlush`:** the behavioral digest reports one before post-processing, but RESEARCH
   constrains pass order rather than a flush. D-P8-11 relies on same-context command ordering and
   leaves any evidence-driven synchronization change to a governed correction, not an incidental
   glue call.

### 11.4 Open hand-offs

- Phase 9 must make entity/block-entity ID scopes shadow-execution-aware without changing the
  Phase 8 pass ordering.
- Phase 10 must verify both VBO and client-array extended attributes during Phase 8's ordinary
  RenderGlobal layer calls.
- Phase 13 must supply shadow-stage companion/custom objects through Phase 5's fixed binding
  snapshot; it may not allocate a dynamic unit.
- Phase 14 may profile the second setup/traversal and mipmap calls; full-view and synchronous
  fallbacks remain mandatory.
- G8/S1 consumes the completed shadow targets after Phase 8 and adds real `shadowcomp` flips; it
  may not move the v0.2 water-shadow split.

### 11.5 Requested upstream changes

R8-1 through R8-5 in §5.5 are the complete request set. R8-1/R8-2/R8-4 are implementation blockers
and require owner fix-up plus fresh verification because binding §5 changes. R8-3 blocks package
placement. R8-5 blocks a complete hook-health manifest but not pure math tests.

No change is requested to RESEARCH's shadow contract. A future DESIGN candidate should retain the
Phase 5/Phase 8 PCF ownership split explicitly, but current behavior is unambiguous through the
verified dependency.

---

## 12. Implementation checklist

1. **[v0.2]** Land R8-1/R8-2/R8-3/R8-4 owner fix-ups and required fresh literal-PASS reviews;
   compile nothing against ungranted surfaces.
2. **[v0.2]** Add the granted engine/glue/mixin packages with seam tests rejecting Minecraft,
   Forge, Mixin, and LWJGL from `:engine`.
3. **[v0.2]** Implement `ShadowPolicy`, plan validation, fingerprinting, and closed results;
   headless invalid/absence tests.
4. **[v0.2]** Implement `ShadowCelestialPolicy` and share it with the Phase 6 frame provider;
   angle-boundary tests.
5. **[v0.2]** Implement ortho/perspective matrices in column-major `Matrix4Value`; projection
   goldens and compatibility-profile replay.
6. **[v0.2]** Implement model-view day/night rotation and signed-remainder ortho snapping;
   positive/negative camera motion tests.
7. **[v0.2]** Implement celestial eye-space vectors from the copied main model-view; compare the
   shadow light to camera direction.
8. **[v0.2]** Implement six-plane extraction, adjacency table, synthesized-plane algebra,
   orientation, deduplication, and conservative fallback.
9. **[v0.2]** Implement allocation-free AABB testing and brute-corner oracle tests.
10. **[v0.2]** Implement `FullLoadedView` traversal and exact ViewFrustum toroidal-position check.
11. **[v0.2]** Implement the sun-aligned prism iterator and full-view no-omission property suite;
    keep it disabled until green.
12. **[v0.2]** Implement the Phase 8 hook/accessor Mixins with `require=0`, `expect=1`, SRG+
    descriptor targets, and immutable health report.
13. **[v0.2]** Implement authenticated Phase 7 shadow-execution guard handling in existing main
    terrain/entity/cloud/frustum hook adapters.
14. **[v0.2]** Implement `ShadowStateLease` snapshots and reverse restoration through the GL facade;
    failure injection after every state mutation.
15. **[v0.2]** Implement scoped second `setupTerrain`, pass-local visit identity, shadow renderInfos
    use, and exact main-list/vanilla-frame-index preservation.
16. **[v0.2]** Implement root SHADOW step/slot/pass resolution without fallback re-resolution.
17. **[v0.2]** Implement Phase 5 begin/bind/clear/abort/complete branching and one-snapshot invariant.
18. **[v0.2]** Implement fixed shadow texture-binding acquisition before Phase 6 sampler upload;
    assert all sixteen rows and units 4/5/13/14.
19. **[v0.2]** Emit celestial and primary shadow matrix events before first barrier activation;
    assert Phase 6 inverse isolation.
20. **[v0.2]** Activate Phase 4 with its current frame issuer and `shadowPass=true`; cover every
    closed barrier result including fixed function.
21. **[v0.2]** Render terrain exactly SOLID -> CUTOUT_MIPPED -> CUTOUT; recorded call-order test.
22. **[v0.2]** Implement configured cloud draw with cloud-only degradation.
23. **[v0.2]** Implement Forge pass 0 entity traversal and exact prior-pass restoration.
24. **[v0.2]** Issue exactly one `SHADOW_PRE_TRANSLUCENT` copy and handle all Phase 5 outcomes.
25. **[v0.2]** Render optional `shadowTranslucent`, then Forge entity pass 1; false/true matrix tests.
26. **[v0.2]** Implement typed post-pass mipmap request and per-buffer degradation/filter restore.
27. **[v0.2]** Implement H8-BLOB-01 blob-only suppression and fire-preservation tests; gate feature
    enablement on hook health.
28. **[v0.2]** Implement coherent runtime neutralization after R8-2 and prove main pipeline remains
    active after each isolated shadow feature failure.
29. **[v0.2]** Integrate Phase 8 construction/rollback/close into Phase 7 publication with no draw
    between paired publications.
30. **[v0.2]** Add Phase 8 hook-health data to Phase 2 manifests after R8-5; no inferred capability.
31. **[v0.2]** Run static day/night, moving-camera, water split, entity pass, cloud, perspective,
    PCF/mipmap, and far-caster T1 scenes.
32. **[v0.2]** Run first classic-pack T2 shadow comparisons; retain local/CI images only and commit
    manifests/hashes.
33. **[v0.2]** Satisfy the implementation gate: classic-shadow packs at T1 and first T2 results,
    with no unresolved state-restoration or traversal-omission defect.
34. **[post-v0.5]** Hand completed targets to G8/S1 for `shadowcomp`; do not implement it in this
    checklist.
