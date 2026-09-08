# Schmaloogium — Phase 8: Shadow pass — Architecture

## 0. Header

**Phase:** 8 — Shadow pass
**Milestone:** v0.2
**Date:** 2026-08-02 · **Last revised:** 2026-09-07 (§0.9).
**Governing design:** `docs/design/v2.0-RC3/DESIGN.md`, Part I §G0–§G12 and the Phase 8
assignment at lines 1957–2034. This phase deliberately adopts RC3 for its initial build; it does not
change the governance of any earlier phase.
**Declared dependencies:** Phases 4, 5, 6, and 7.

Historical initial-build gate record (not verification of the current §0.9 amendment):

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

### 0.7 Maintenance adoption — R7-12 shared physical binding (2026-09-07)

This maintainer-authorized architecture-only amendment adopts Phase 7 R7-12, not R7-13.
The active invocation and incorporated §5 interfaces now consume the supplied selection/context,
texture publication and lease source, and Phase 5's five-argument sixteen-row physical binding
operation. It replaces the four-row borrowed R8-2 proposal; §§0.1–0.6 remain historical records.
The initial dependency-gate statements above describe the initial build, not current readiness.

Inputs for this amendment: `docs/MOVES.md`; this whole document; RC3 Part I and Phase 8 assignment;
`docs/research/v1/RESEARCH.md` §§0–1, §4.5 and App B.2/B.3; Phase 5 §0.39, §§2.4/4.10/4.12/5.1/5.3.1;
Phase 7 §0.40, §§5.1–5.4; and Phase 4's §2.2/§4.10 selector/activation contracts incorporated by §5.1.
Only the latest Phase 5/7/8 reviews' closure evidence was read, not used as current-contract grants.
The narrow Phase 4 read establishes private selection authentication; no vanilla or reference-tree
implementation was re-derived. Each consumed phase declares RC3; no v3 override is adopted.

The authorizing request permits this provisional architecture alignment with changed upstream
contracts, not verified implementation consumption. Phase 5 §0.39 says the prior evidence “does
not certify this rebuild” (`docs/phase5/v1/PHASE_5_DOC.md:340-342`); Phase 7 §0.40 says “Unverified;
a fresh whole-document review returning literal PASS is required” in its closing status
(`docs/phase7/v1/PHASE_7_DOC.md:355`). Phase 4's newly coordinated selector surface likewise awaits
fresh verification, as recorded in `docs/phase5/v1/PHASE_5_DOC.md:350-354`.
Phase 8 Review 4's PASS (`docs/phase8/reviews/PHASE_8_REVIEW_4.md:51-61`) covers only pre-amendment
bytes. **§5 changed: Phase 8 is unverified and requires fresh whole-document verification returning
literal PASS before verified downstream consumption.** R7-13 and the other gates in §5.5 remain;
the real slot stays `NotInstalled`/unavailable until adoption and owner verification gates close.
No code, reviews, builds, tests, verification, or directory roll are part of this amendment.

### 0.8 Maintenance adoption — R7-13 registry-independent planning (2026-09-07)

This maintainer-authorized architecture-only amendment adopts Phase 7 §5.4 R7-13:
“Final publication identity and build validation include that registry; planning never borrows an old”
(`docs/phase7/v1/PHASE_7_DOC.md:2348`), continued by “registry to break the cycle” at `:2349`.
`ShadowPlanInput` and `ShadowPlan` now contain no registry identity. Policy and hook health
determine the pure plan fingerprint; `ShadowPassFactory.create` receives the final new
`RegistryFingerprint registry` immediately after `ShadowPlan plan` and binds publication identity
and validation to it. §§2/4 and their incorporated §5 contracts define the complete cutover.
§§0.1–0.7 remain unchanged historical records, including §0.7's then-ungranted R7-13 posture.

Scoped inputs actually read: `docs/MOVES.md`; this whole document; RC3 Part I and Phase 8
assignment; `docs/research/v1/RESEARCH.md` §§0–1 and §4.5; Phase 7 §0, §4.1's construction
sequence and §§5.3–5.4; Phase 4 §0, §4.11's fingerprint/generation distinction and §5.1's
registry interfaces; Phase 6 §0, §2.2 and §§5.1–5.2's current sampler/retirement grants.
Those narrow dependency reads establish the acyclic construction and current gates, not
verified implementation consumption. Each consumed phase retains its declared RC3 governance.
No reference implementation, review or forbidden transcript was read for this amendment.

Phase 6 now records R7-10/R7-11 as “adopted in” its current bytes, “not verified grants”
(`docs/phase6/v1/PHASE_6_DOC.md:296-297`); active readiness wording below reflects that owner
status without changing its APIs or Phase 7's still-unsynchronized request rows. R7-12/R7-13
are both adopted here, not verified. Remaining ungranted dependencies and owner synchronization/
verification gates are explicit in §5.5. **§5 changed: fresh whole-document Phase 8 verification
returning literal PASS is required before verified downstream consumption.** Real shadow stays
`NotInstalled`/unavailable until those gates close. No code, reviews, builds, tests, verification
or directory roll are part of this amendment.

### 0.9 Integration-review receiver adoption (2026-09-07)

Architecture-only IR-03/04/10/11/21/22/24 adoption in active §§4/5/11. RC3 remains governing.
Read inputs: integration findings; RESEARCH §§0–1/4.3–4.5/App A.3/F.1 and RC3 Phase8 assignment;
P1 exact package grant/non-verbs/bootstrap handoff; P2 v2 §§4.5/5; P3 §5 schema/typed
configuration; P6 current resolver/retirement; P7 complete active construction, invocation,
reload and ID admission interfaces; coordinated P9/P13 owner handoffs.
Earlier grants and PASS records remain historical. **§5 changed; unverified**; fresh whole-
document owner/receiver verification still gates real shadow. No implementation, reference-source
inspection, tests, validation commands or new PASS. R8-3 and P2 R8-5 are granted/adopted,
not missing interfaces; R7-10–13 producer and receiver architecture is synchronized.

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
- **Owned by Phase 13:** companion atlases, custom textures, noise objects, and the texture
  publication/lease owner. Phase 7 lends its non-owning publication and lease source; Phase 5 alone
  selects compatible fixed-unit objects and physically binds them. Phase 8 owns only the acquired
  lease until `Bound`, then only the returned binding snapshot's closure.
- **Owned by Phase 14:** new performance tiers and GL modernization. Phase 8 specifies a bounded
  allocation-free baseline first.
- **Owned by G8/S1:** `shadowcomp`, shadow-target ping-pong execution, and shadowcolor2–7 wiring.
  Phase 8 leaves the StageRegistry identities dormant.

### 1.3 Hard boundaries

Phase 8 does not allocate or delete a framebuffer or texture, compile a shader, parse pack input,
create a fourth barrier participant, implement `shadowcomp`, replace `RenderGlobal`, or add a chunk
renderer. Mixins remain dumb adapters. All policy and math are pure `:engine`; Minecraft, Forge,
Mixin, and LWJGL types remain in `:mod` glue.

The current dependency surfaces grant R8-1/R8-2/R8-4 and Phase 7's part of R8-5 in architecture,
subject to their fresh-verification gates. Phase 8 consumes R7-12's replacement binding contract
in §0.7, not the obsolete four-row proposal. R7-13 is now adopted by §0.8: planning is
registry-independent and final construction explicitly receives the new registry fingerprint.
R8-3 package placement and both R8-5 halves are granted/adopted architecture. Fresh owner/
receiver verification and separate upstream gates in §5.5 remain blockers. Neither an old
registry nor prior-frame bindings substitute for a gated real shadow slot.

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

Phase 1 §11.4 grants the exact engine.shadow/mod.glue.shadow/mod.mixin.shadow slots above.
R8-3 is receiver-adopted here, pending current owner/receiver verification, not another package
request; structural seam and Mixin-config rules remain unchanged.

### 2.2 Public shape

Illustrative signatures; the closed results and semantics are binding:

```java
public interface ShadowPlanFactory {
    ShadowPlanResult plan(ShadowPlanInput input);
}

public record ShadowPlanInput(
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
        RegistryFingerprint registry,
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
    ShadowPolicy policy,
    ShadowCelestialPolicy celestialPolicy,
    ShadowHookHealth hookHealth,
    ShadowPlanFingerprint fingerprint) {}

public record ShadowPlanFingerprint(String canonicalSha256) {}
// Phase-5-owned shape, consumed unchanged.
public record ShadowMipmapPolicy(List<LogicalBuffer> buffers) {}
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
identity. `ShadowPlanFingerprint` hashes every policy value and the hook fingerprint using their
canonical encodings, never a registry fingerprint or generation. Structural policy/hook equality
and this fingerprint identify pure planning content; the celestial policy is derived only from
that content. The final publication separately fingerprints the ordered pair `(plan.fingerprint(),
registry)` supplied to `create`, using canonical SHA-256 encoding. Publication pairing/reuse must
match both inputs, not the plan fingerprint alone; equal hashes never revive a closed slot epoch.
`ShadowHookHealth` is owned by the application health audit and borrowed immutably by plans.
`ShadowCameraMath` implements exactly
§4.5–§4.6; `planes()` returns an immutable normalized ordered fixture view and `intersects` is the
total finite-AABB predicate. `ShadowCelestialPolicy.sample` implements §4.5.1/§4.5.4 and returns the
same sample consumed by Phase 6 and `ShadowCamera`.

`ShadowFrameView` and `ShadowExecutionView` are Phase-7-owned contracts, incorporated in §5.4;
the declaration above reproduces the value shape, not a second issuer. The world port contains no
Minecraft object in `:engine`; its glue implementation is construction-bound to the current client
renderer and validates the borrowed Phase 7 execution view before each operation. The invocation
also borrows the exact `selection`, `activationContext`, `texturePublication`, and `textureLeases`
specified in §5.4. None is construction-retained or closed by Phase 8.

### 2.3 Relationship and ownership map

```text
Phase 7-owned projection of the immutable Phase 3 configuration
        |
        v
 ShadowPlan (pure, registry-independent) -> Phase 6 provider gets celestial policy
                                                   |
                                        runtime -> Phase 4 compile/compose
                                                   |
                  plan + final new registry fingerprint + same runtime
                                                   |
                                         ShadowPassFactory.create
                                                   |
                                   registry-bound ShadowPassPublication
                                                   |
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
pure `ShadowPlan`, final construction registry fingerprint and combined publication fingerprint,
construction-time `UniformRuntime`, world port, and diagnostics until the enclosing Phase 7
pipeline publication closes. Registry generation and slot epoch remain separate live credentials.
An invocation owns a separately acquired `TextureOverlayLease` only until `Bound` transfers it
into a `TextureBindingSnapshot`. The one cleanup path closes exactly that current owner, including
on exceptions and after pass invalidation; it never closes any borrowed publication or source.

### 2.4 Core invariants

1. One frame may open at most one shadow snapshot. Every acquired snapshot is completed or aborted.
2. No shadow GL/draw operation occurs before context, registry, estate, registry-bound publication
   identity, plan fingerprint, and hook health validation.
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
10. Phase 7 selects root shadow once; Phase 8 uses that identical selection/context for acquisition,
    physical binding and activation. Only `Bound` followed by successful activation authorizes draw.
11. The shared binding has sixteen ascending rows, not four shadow-only rows. Phase 5 freezes
    ordinary and shadow readable sides and performs every required object bind before activation
    and sampler upload. No Phase 8 row-bind loop or fallback re-resolution exists.
12. Completion/abort/neutralization invalidates binding use, not the Bound owner's closure duty.
13. Planning needs only policy and hook health. Final construction fingerprints the plan with the
    new registry; a prior registry is never an input to planning or a substitute at construction.

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
| unit 4 = shadowtex0/watershadow/conditional shadow | Phase 5 sole fixed policy and shared sixteen-row physical binding; Phase 6 uploads fixed integers only after Bound/activation, with R7-10 still gated | `[V:doc]` `docs/research/v1/RESEARCH.md:1236`; Phase 5 shared table `docs/phase5/v1/PHASE_5_DOC.md:2125-2147`; §5.3 |
| unit 5 = shadowtex1/conditional shadow | same | `[V:doc]` `docs/research/v1/RESEARCH.md:1237` |
| unit 13 = shadowcolor0/shadowcolor | same | `[V:doc]` `docs/research/v1/RESEARCH.md:1245` |
| unit 14 = shadowcolor1 | same | `[V:doc]` `docs/research/v1/RESEARCH.md:1246` |

No unit is dynamically allocated. The sole Phase 5 policy selects unit 5 for `shadow` only when the
effective provider layout directly declares sampler-compatible `watershadow`; buffer count is
irrelevant. The shared result covers all sixteen App B.3 rows, not just the four listed above.

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

`ShadowPlanFactory` is pure. Its exact input is
`ShadowPlanInput(ShadowPolicy policy, ShadowHookHealth hookHealth)`. It never accepts a registry
fingerprint/generation, source strings, property maps, Minecraft objects, GL handles, or mutable
collections. The resulting `ShadowPlan` likewise contains no hidden registry identity.
P7 supplies only the resolved projection from current P3 schema17, validating the containing
and nested ID schema before construction; no schema16 upgrade/default reconstruction. Shadow
extent consumes P5's actual allocation from canonical `shadowResMul`, not an alias
`shadowQuality` key or a second multiplier in camera math. The user/pack old-light tri-state,
independent companion pair and reserved zero-only antialiasing setting follow current P3/P7
load policy; none is a new P8 parser, AA path or texture binder.

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

Construction follows Phase 7's §5.3 ordering, with R7-13's request adopted here:

1. Freeze the intended resolved policy and hook health; build the pure plan and its celestial
   policy before constructing Phase 6's provider/runtime. `NotRequested`/`Disabled` supplies
   explicit shadow absence rather than a fabricated ready plan.
2. Phase 7 supplies that same celestial policy to the new provider, constructs the new runtime,
   then compiles Phase 4 using that runtime's macro contribution and the sole Phase 5 policy.
   Derive the Phase 5 candidate from the final detached registry view and compose Phase 4 using
   exactly that runtime's three participants. Planning never waits for these products.
3. Only after the final new registry fingerprint exists, call
   `create(plan, registry, uniforms, world, diagnostics)` with that fingerprint and the same new
   runtime. The factory validates required non-null inputs, policy/hook validity and the canonical
   plan fingerprint before returning `Ready`; malformed or inconsistent build input returns
   `Invalid(diagnosticId)` without a publication or retained services. Feature-disable results
   remain `Disabled(reason,diagnosticId)`. No registry is fetched implicitly or substituted.
4. Phase 7 validates the supplied fingerprint against the final candidate view and the intended
   configuration/dimension/resource/hook tuple before installing the returned publication; the
   opaque fingerprint alone is not evidence that a caller selected the right candidate. Its
   composition transaction also pairs the exact runtime/provider and buffer candidate. Phase 8
   receives no invented runtime fingerprint getter, registry lookup or publisher authority.

The returned publication retains that exact registry fingerprint and the canonical SHA-256
fingerprint of `(plan.fingerprint(), registry)`. Build validation and Phase 7 precommit pairing
include both; a mismatch prevents installation and follows the existing failed-build
rollback/off path. A plan may have identical content across compilations, but a changed registry
requires a newly paired publication; never patch the pure plan with a late registry field.
At invocation, compare the borrowed published registry fingerprint with the retained construction
fingerprint and validate the plan fingerprint as well as the separate live execution/slot/registry/
estate generations. Hash equality cannot authenticate live publication ownership.
`RegistryFingerprint` excludes generation (`docs/phase4/v1/PHASE_4_DOC.md:1615-1617`,
“It does not hash” ... “generation”); accepted-generation adoption remains Phase 7's subsequent
publication handshake, not a predicted generation or part of pure planning.

This removes the plan -> provider -> compile -> plan cycle: policy/hook planning precedes the
provider, while registry-dependent validation/fingerprinting belongs to final construction and
publication. No old registry is borrowed, even on first load, reload, or failed compilation.
Construction and execution remain unavailable until §5.5's remaining gates close.

The lifecycle is:

```text
PLANNED -> READY -> INVOKING -> READY
                   |    |
                   |    +-> DISABLED_RUNTIME (after safely neutralized feature failure)
                   +------> CLOSED (pipeline replacement/shutdown)
```

Only the render thread enters `INVOKING`. A second or re-entrant invocation returns
`Rejected(WRONG_FRAME)` before mutation. `CLOSED` never becomes ready again. Pure plan identity
includes the hook-health fingerprint and every field of the complete effective `ShadowPolicy`,
not a registry. Reload derives an immutable plan from the new resolved inputs and creates a new
registry-bound publication; content equality never revives an old publication.

The generation-scoped `ShadowPassPublication` owns the slot. `close()` first rejects a non-render
thread with `WRONG_THREAD`, then accepts `READY` or `DISABLED_RUNTIME`. First success invalidates
the slot epoch, releases every retained service reference, and returns `Closed`; later calls return
`AlreadyClosed`. A call during `INVOKING` returns `Rejected(CLOSE_WHILE_INVOKING)` without mutation;
Phase 7 must first finish or abort that frame, so no close races an invocation.

### 4.2 Invocation transaction

This is the adopted R7-12 transaction, executable only after §5.5's remaining grants and fresh
owner-verification gates close. Phase 7 selects root shadow once before `invoke` and owns the
execution bridge around it; Phase 8 does not repeat selection or mint another activation context.

1. Validate borrowed Phase 7 execution, frame identity, plan fingerprint and registry-bound
   publication pairing against the retained final construction registry fingerprint, current
   registry and estate generations, render thread, and single-entry state. Stale/wrong-frame
   input returns `Rejected` before Phase 5 or GL work. Borrow all invocation fields from one
   active pipeline tuple; equal plan content alone cannot admit a different registry.
2. Query `PublishedBufferEstate.shadow()`:
   - `ShadowEstateNotRequested` returns `Completed` without opening state;
   - `ShadowEstateUnavailable` records its feature diagnostic and returns `Completed`, relying on
     Phase 5's coherent neutral estate disposition, not prior-frame physical bindings;
   - `ShadowEstateAvailable` continues.
3. Consume `context.selection()` and the identical `context.activationContext()`. Authenticate
   through `ProgramBindingSelections.validateSelection(selection,activationContext)` and pair the
   contained SHADOW `PassDescriptor` with that selection's effective descriptor, stage/band,
   registry and root-shadow request. Invalid/stale credentials reject before acquisition/GL.
   Never call `select`, `resolve`, or `barrierContexts.activation` here; do not reconstruct fallback,
   overlay child state, or replace private authentication with public-record equality.
4. Set `generation = shadow.estateGeneration()` from the available view, distinct from the registry
   generation, and call `beginPass(frameId,pass,selection)`. Continue only on `Acquired`; the
   snapshot retains this exact selection and freezes **both ordinary and shadow** readable sides.
   A stale generation returns Phase 7 `Rejected(STALE_PUBLICATION)` before GL; an ordering error
   aborts the shader frame rather than guessing. Acquisition alone authorizes no draw.
5. Sample one `ShadowWorldSample`; validate its echoed identities and camera presence, then derive
   the camera, celestial sample, culling frustum and traversal plan. An absent/stale camera rejects
   before GL; the acquired snapshot is still aborted by the cleanup path.
6. Open the `ShadowStateLease` with that camera and the already borrowed active
   `ShadowExecutionView`. It snapshots/restores §4.4's state and installs the shadow camera.
   Phase 7 remains the sole execution-bridge opener/closer around `invoke`.
7. Bind and clear the Phase 5 shadow target. A backend failure leaves the snapshot open; abort
   immediately without flips, then follow §6's neutralization/restoration containment.
8. Call `setupTerrain`, then send `updateCelestial` and `updateShadowMatrices` to the
   construction-bound Phase 6 runtime. These are value events, not sampler uploads. Take the
   immutable `expectedOverlay = context.texturePublication().id()` and call
   `context.textureLeases().lease(expectedOverlay,selection)`. `Acquired(overlay)` gives Phase 8
   closure responsibility; `Rejected` gives no lease and suppresses this undrawn shadow pass.
   Never query a new current publication or use a no-argument lease followed by an ID lookup.
   With the acquired lease call exactly:

   ```java
   shadow.shadowBindings(generation, frameId, snapshot, overlay, expectedOverlay);
   ```

   This is Phase 5's **physical binding operation**, not a metadata request. Its full preflight and
   four-result contract are incorporated in §5.3:
   - `Bound(binding)`: every required bind succeeded. Ownership transfers exactly once from the
     acquired overlay lease to this closeable sixteen-row binding snapshot. Continue immediately
     to step 9; Phase 8 does not bind the rows itself.
   - `Degraded(degradation)`: no bind, retention or transfer; preserve ordered diagnostics and
     `SUPPRESS_DRAW`. Abort the undrawn snapshot without flips; no activation/upload/draw or
     post-draw mipmaps. Close only the acquired overlay lease in `finally`.
   - `Rejected(reason)`: same no-bind/no-transfer suppression and abort, preserving the exact
     protocol reason. Do not retry with another lease, provider, context or previous binding.
   - `BackendFailed(failure)`: may follow partial GL binds; no transfer or retained lease.
     Forbid activation/upload/draw, abort the open snapshot, close only the acquired lease in
     `finally`, and apply §6's existing shadow neutralization/restoration containment. Never
     describe this as mutation-free.

   Lease rejection, binding rejection and binding degradation suppress only this selected pass;
   after successful abort/restoration they return `Completed` to let Phase 7 advance. They do not
   advertise a newly rendered map or permanently disable the feature. Their local preflight was
   mutation-free, but the invocation already bound/cleared at step 7: they must not be returned as
   a mutation-free Phase 7 `Rejected`. Failed abort/restoration instead returns `Failed`.
9. Only after `Bound`, call
   `context.registry().barrier().activate(new UseProgramRequest(selection,activationContext))`.
   No reentrant publication transition is allowed between preflight/binding and this activation.
   Phase 4 uses the retained private binding, never resolves again. Branch on every barrier result:
   - `Activated` or `FixedFunction`: draw; fixed function uploads no sampler integers;
   - `Skipped`: omit the pass and abort without flips or mipmaps, then restore and return `Completed`;
   - `StalePublication`: abort and return `Failed` after restoration; no post-GL mutation-free
     rejection or stale retry;
   - `ShadersOff` or `FailedSafe`: abort and return `Failed`.
10. Execute §4.8's unchanged terrain/cloud/entity/depth-split/translucent order through the port.
11. Call the typed post-pass mipmap operation. `Generated` carries canonical per-buffer outcomes;
    `Degraded` within it continues only after successful base-filter restoration. Result-level
    `Neutralized(...,true)` has already aborted without flips and invalidated the snapshot:
    stop, mark the feature disabled, skip step 12 and every additional abort/neutralization call,
    but still execute `finally`. A protocol rejection aborts the still-open snapshot and follows §6.
12. Call `completePass`. Only `Completed(frameId)` commits the recorded shadowcolor flips.
    A rejection is not success; cleanup aborts only if the snapshot is still open.
13. In the single `finally`, complete/abort bookkeeping prevents duplicate terminal operations.
    After `Bound`, close **only the binding snapshot**, even after completion, abort,
    neutralization, replacement/off or an exception. Before transfer, close **only any acquired
    overlay lease**, including a thrown lease-consuming call. No acquired lease means nothing to
    close. Then restore Phase-8-owned traversal, Forge-pass and shadow-state scopes in reverse
    order. Never close the borrowed publication, lease source, selection/context or execution
    view. Phase 7's outer `finally` closes its execution view before main clear.

Every exit after acquisition uses this cleanup path. A binding snapshot is an evidence/lifetime
holder, not permission to replay expired handles; invalidation never discharges closure duty.
Return `Completed` only after safe restoration and a still-current borrowed execution credential;
otherwise return `Failed`. Preserve vanilla throwables only after restoration and closed-result
reporting to Phase 7; contain engine exceptions as stable failures.

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
Phase 7 composition grant in R8-4 and the R7-13 ordering now adopted in §4.1, still subject to
§5.5's remaining gates. Phase 8 does not add a second Phase 6 participant or a late `shadowAngle`
upload.

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

Pass 0 is pre-split; pass 1 is post-split. Main entity/block-entity program hooks are bypassed,
but IDs are not: P7 issues opaque `IdScopeAdmission` from the currently Valid
`ShadowExecutionView`, binding frame/pipeline, current ID generation, execution/slot epoch
and stack depth. P9 `enterEntity(admission,entityOrdinal)` /
`enterBlockEntity(admission,stateOrdinal)` return Entered(IdScopeToken) or
Rejected(STALE_ADMISSION|STALE_GENERATION|WRONG_THREAD|STACK_LIMIT); `leave(token)` validates
LIFO and restores the preceding ID. Main accepted gbuffers scopes are the other admission
kind, not a precondition of this shadow path. No main snapshot, selection or activation occurs.
Every nested finally restores P7 color then P9 ID before shadow program release; stale,
unbalanced or thrown traversal drains IDs/color to zero and preserves §6 containment. Before
P9 installation IDs remain neutral; the P7 v0.1 hurt/flash producer is independently active.
Forge per-entity/TE predicates, batching, prior-pass/counter restoration and depth split remain
unchanged. `RenderManager` blob suppression still retains fire rendering.

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

`ShadowMipmapPolicy` is Phase 5's immutable, duplicate-free canonical list of typed shadow
`LogicalBuffer`s. Global depth/color flags expand to the allocated domain members; per-texture
flags union before canonicalization. Submit after all draws and before `completePass`, so Phase 5
uses the final written shadowcolor side. It owns handles, filters, generation and restoration;
logical-buffer mipmaps never overwrite a custom/foreign replacement's effective parameters.

`Generated` contains `Generated`, `NotAllocated`, or `Degraded` per requested buffer. One-buffer
failure is rung 2a only when Phase 5 restores its non-mipmap min filter; later buffers continue and
no stale chain is advertised as fresh. If that restoration fails, result-level
`Neutralized(buffer,failure,diagnosticId,true)` already aborts without flips, invalidates snapshots
and makes the estate unavailable/neutral. Phase 8 stops without another complete/abort/neutralize
call, restores its state and still closes a transferred binding in `finally`. These are the
current Phase 5 §5 semantics (`docs/phase5/v1/PHASE_5_DOC.md:2358-2359`), not a new Phase 8 GL path.

Hardware PCF is not toggled per frame. Phase 5 already applies per-depth compare mode and the
legacy depth swizzle during candidate creation
(`docs/phase5/v1/PHASE_5_DOC.md:1784-1792`). Phase 8 checks the resulting estate disposition:
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

Phase 7 issues `activation(shadowStep,true)` and selects root `shadow` once before invocation.
Phase 8 consumes its exact borrowed selection/context, authenticates them, and passes that same
selection to `beginPass` and `UseProgramRequest`. Phase 4 validates the shadow stage/band relation
and applies force-shadow before the sole fallback resolution
(`docs/phase4/v1/PHASE_4_DOC.md:1473-1505`). The `shadowPass=true` activation interval begins
immediately before the first shadow draw and ends before fixed-function/state restoration.
The slot never fabricates a context, reselects a provider, or calls `use` directly.

All sixteen rows preflight before Phase 5 binds any object. Its five-argument `shadowBindings`
performs the required ascending-unit binds; only `Bound` permits immediate same-selection
activation and then Phase 6 sampler integer upload. `Rejected`/`Degraded` bind nothing;
`BackendFailed` may have partially bound objects and requires containment. No Phase 8 manual
row loop, second unit map, four-row view, or prior-frame fallback is permitted. Phase 6's R7-10
sole-resolver contract is adopted by its owner but still awaits fresh verification; existing
callback count/cache/error isolation is unchanged. Non-final `FixedFunctionEmpty` produces purpose `NONE`, sixteen `Unused` rows,
no shader candidates/object binds, and no sampler upload; it still follows the same ownership
protocol and renders depth through vanilla state after `FixedFunction`.

Actual base-texture binding/restoration during shadow drawing uses P7 §5.1's authenticated
`AtlasBindingEvidence`→`AtlasBindingSink.currentBinding` adapter: opaque evidence from the
mod binding observer contains optional P13 AtlasId, PipelineVersion, resourceReloadEpoch and
bindSerial; only current issuer/thread/composition/epoch/latest serial may query `atlasSize`.
Known becomes P6 `updateAtlasSize(Int2(width,height))`; Unknown/non-atlas/reset is `(0,0)`.
It updates the current active shadow program immediately or caches before activation. P5
remains the only physical shader texture binder; shadow family identity never implies an atlas.
P8 restores ID/color before release, then real texture restoration emits new bind evidence.

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
Full replacement/shutdown ordering is:

1. stop admitting new world frames;
2. finish or abort the current Phase 7 frame; unwind §4.2's exactly-one binding/lease closure even
   when its usable lifetime has already expired;
3. close the Phase 8 publication, invalidating its slot epoch and releasing retained services;
   it owns no GL object and no outstanding invocation resource may be hidden inside it;
4. follow Phase 7's coordinated owner teardown: retire texture acquisition authority before its
   borrowed services disappear, let outstanding leases defer owned deletion rather than stale-use
   rejection, and retire Phase 6 only after its final callback under its adopted-but-unverified
   R7-11 contract; never invent `UniformRuntime.close()` or revive retired textures as fallback;
5. restore blob-shadow behavior and remove Phase-8 hook-state publication.

Resource-only P12 NONE+resourceReacquire follows P7 §5.1's separate retained-configuration
branch: quiesce/drain before vanilla replacement, refresh texture/ID resources and current atlas
evidence, and recreate P8 publication only if borrowed services change. It does not retire a
retained P6 runtime or reload P3. Any failed refresh converges to coherent off, never old atlas
reuse. Nonterminal resets remain distinct from UNPUBLISHED_ABORT/REPLACEMENT/SHUTDOWN retirement.

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
| H8-TERRAIN-01 | `RenderGlobal` | `buy` | invoke `func_174977_a(BlockRenderLayer,D,I,Entity)I` for the four ordered layers; consume Phase 7's R8-1 shadow-execution guard | ordinary vanilla layer draw, no class replacement; `FEATURE` |
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
| `ShadowPlanFactory`, `ShadowPlanInput`, `ShadowPlanResult`, `ShadowPlan`, `ShadowPolicy` | exact §2.2 shapes and §4.1 rules: input `(ShadowPolicy policy,ShadowHookHealth hookHealth)`; pure resolved-policy/celestial metadata; complete camera/traversal/flag/mipmap/PCF and hook fingerprint identity; neither input, plan nor plan fingerprint contains registry identity; no parser, MC, GL, or handle | Phase 7 pipeline construction; Phase 2 headless tests |
| `ShadowCelestialPolicy` | pure total `sunAngle -> day/shadowAngle/celestial rotation` function shared by Phase 6 provider and Phase 8 camera | Phase 6 `mod.glue` provider via Phase 7 composition |
| `ShadowPassFactory`, `ShadowPassBuildResult`, `ShadowPassPublication` | exact `create(ShadowPlan plan,RegistryFingerprint registry,UniformRuntime uniforms,ShadowWorldPort world,DiagnosticReporter diagnostics) -> ShadowPassBuildResult`; final new registry immediately after plan; §4.1 validation and combined publication fingerprint; one generation-scoped owner exposing Phase 7's slot and idempotent render-thread close; READY/DISABLED_RUNTIME close invalidates epoch/releases services, INVOKING rejects | Phase 7 |
| `ShadowWorldPort` and closed world/state/terrain/draw results | loader-neutral primitive/value interface; Minecraft implementation owns setup/draw/state restoration and Forge pass adapter; every borrowed execution validated | `mod.glue.shadow`; recorded tests |
| `ShadowCameraMath`, `ShadowCamera`, `ShadowFrustum`, `ShadowTraversalPlan` | deterministic column-major camera/celestial math, finite plane set, total AABB predicate, full/prism traversal strategies | Phase 8 runtime; Phase 2 fixtures |
| `ShadowHookHealth`, Phase-8 hook rows | immutable expected/actual counts and enabled/disabled outcome for §4.13 | diagnostics; Phase 2 manifest integration |
| implementation of Phase 7 `ShadowInvocationSlot` | §4.2's full R7-12 transaction: supplied same selection/context and expected-publication lease, five-argument physical binding, four result branches, Bound-only transfer and exactly-one finally closure; no retained invocation values; returns before main clear; real slot remains gated under §5.5 | Phase 7 frame driver |
| Shadow-aware ID/color/atlas integration | §4.8.2 alternate authenticated P7 IdScopeAdmission for P9 entity/TE tokens, independent v0.1 P7 color stack, and §4.10 actual-bind P7→P13→P6 atlas update; no main gbuffers snapshot/program and no second texture binder | P7/P9/P13 glue |

Phase 8 exposes no GL handle, framebuffer name, program handle, parsed source, mutable vanilla
collection, or physical shadowcolor side.

The complete §2.2 plan/factory/publication shapes and §4.1 construction, identity, validation,
failure and lifecycle rules are incorporated into this binding §5 interface. R7-13 at
`docs/phase7/v1/PHASE_7_DOC.md:2344-2349` is granted by this owner: pure planning before the
provider; final-registry `create` after compile/compose. `ShadowPlanFingerprint` excludes registry;
the retained publication fingerprint hashes the ordered canonical pair `(plan.fingerprint(),
registry)`. Phase 7 checks the supplied registry against its final candidate and intended tuple
before installation; Phase 8 checks it against the invocation's registry before any Phase 5/GL
work. No old fingerprint, implicit registry lookup, late plan mutation or plan-only stale check
is permitted. Live generation/slot authentication remains mandatory even when content hashes
match. This grant closes the design cycle, not §5.5's implementation/verification gates.

### 5.2 Phase 4 contracts consumed

| Phase 4 §5 contract | Use |
|---|---|
| `StageRegistry`, `StageId`, `StageBand`, `StageStep`, `PassDescriptor` | locate/validate the contained SHADOW pass against the supplied selection; Phase 7 alone issues the activation context |
| `ProgramSlotId`, `ProgramStateBundle` | root `shadow` identity and complete effective state; no child overlay |
| `ProgramBindingSelection`, `ProgramBindingSelections.validateSelection`, closed validation results | supplied opaque private credential and its effective descriptor/layout; identical selection in begin/bind/activate; no Phase 8 fallback resolution |
| `PublishedProgramStateBarrier`, `FrameBarrierContexts`, `BarrierContext`, `UseProgramRequest(selection,context)`, closed `BarrierResult` | only activation route, consuming the already-issued exact context after Bound; Phase 4 retains force-shadow and fallback authority |
| `RegistryFingerprint` / generation | final construction/publication pairing and invocation stale rejection; deterministic content fingerprint is separate from live generation and absent from pure planning |

These are the incorporated contracts at `docs/phase4/v1/PHASE_4_DOC.md:1782-1796`, with exact
selection accessors/validation/request shapes at `:614-644` and authentication/activation/lifetime
at `:1473-1524`. Phase 8 never calls the compiler, publisher, program lookup service, selector or
fallback resolver. The newly changed owner surface requires fresh verification before implementation.

### 5.3 Phase 5 contracts consumed

| Phase 5 §5 contract | Use |
|---|---|
| `BufferEstateView`/inventory/sizing | authoritative shadow extent and allocated logical inventory |
| `PublishedBufferEstate.shadow()` | closed not-requested, unavailable/neutral, or available branch |
| `ShadowEstateView.beginPass(long frameId,PassDescriptor pass,ProgramBindingSelection selection)` | acquire one authenticated snapshot with the exact supplied selection; no descriptor-only substitute |
| `ShadowPassSnapshot` | exact fields: estateGeneration, depthAttachmentEpoch, frameId, pass, selection, framebuffer, colorAttachments, readableTextures, flipAfterPass; freeze ordinary **and** shadow readable sides at acquisition, never consult later live main sides |
| `bind/clear/copyDepth/completePass/abortPass`, `ShadowProtocolRejection` and closed results | existing target transaction, single split, complete-only flips and abort/full-clear semantics |
| `TextureBindingResult shadowBindings(long generation,long frameId,ShadowPassSnapshot snapshot,TextureOverlayLease overlay,TextureOverlayPublicationId expectedOverlay)` | full shared physical bind operation below; supersedes R8-2's four-row borrowed view |
| `TextureOverlayLease`, `TextureOverlayPublicationId`, `TextureBindingSnapshot`, rows/outcomes/purpose and diagnostic types | Phase-5-owned shared schemas; Bound alone transfers closure to the returned binding snapshot |
| `FixedSamplerPolicies` and complete App B.3 policy | sole compatible-object/name/unit authority; Phase 8 neither copies the map nor binds handles |
| `ShadowMipmapPolicy`, `generateShadowMipmaps`, `ShadowMipmapResult`/outcomes | canonical logical-buffer post-pass generation and base-filter restoration, including already-contained result-level `Neutralized` |
| `degradeToNeutral`, `ShadowNeutralReason`, `ShadowNeutralizationResult` | generation-checked coherent unavailable/neutral transition and idempotence; §4.9/§6 containment |

The exact Phase-5-owned signatures, field types/order, variants and semantics incorporated here are
`docs/phase5/v1/PHASE_5_DOC.md:943-1029`, `:1816-1968`, `:2174-2297` and §5.1 `:2356-2375`.
The binding row says “Shared sixteen-row closeable binding protocol, never separate four rows”
at `:2357`. All §4.2 ownership, suppression, timing and cleanup rules are incorporated into this
§5 interface; they are not optional implementation commentary.

**Physical operation and results.** All sixteen immutable rows are ordered by unit 0–15 and contain
`TextureBindingOutcome.BoundObject(TextureHandleRef,DeclaredGlslType.Sampler,
List<ResolvedSamplerBinding>,BindingOrigin)` or `Unused`; missing/stale backing is never a successful
drawable row. `BindingPurpose` is `SHADER`, `FIXED_FUNCTION_PASSTHROUGH`, or `NONE`; shadow fixed
function uses `NONE` with no shader candidates/uploads. Only used compatible rows are physically
bound, in ascending order, by Phase 5 before same-selection activation. There is no four-row
`ShadowBindingSnapshot`, `Bindable`/`Neutral` result algebra, or Phase 8 bind loop.

`TextureBindingResult` has exactly:

| Result | Mutation, continuation and ownership |
|---|---|
| `Bound(TextureBindingSnapshot snapshot)` | all required binds completed; compatible-base fallback diagnostics may remain. Transfers the overlay lease exactly once; Phase 8 finally closes only this binding snapshot after any later outcome |
| `Degraded(TextureBindingDegradation degradation)` | exact selection, ordered diagnostics and `TextureBindingAction.SUPPRESS_DRAW`; zero binds/retention/transfer. Abort undrawn shadow snapshot, no flips/mipmaps/activation/upload; finally close only acquired overlay lease |
| `Rejected(TextureBindingRejection reason)` | zero binds/retention/transfer; same suppression/abort/lease closure, preserve exact reason and do not retry |
| `BackendFailed(BufferFailure failure)` | may follow partial binds; no retained/transferred lease. No activation/upload/draw; caller finally closes acquired lease and uses existing abort/neutralization/restoration containment |

**Preflight.** First failure wins: structural input then thread; estate generation; open frame then
explicit frameId; issued/current snapshot then depth-attachment epoch; private selector authentication
and pass/provider/effective-stage/band/layout pairing; live current overlay lease; expected overlay
ID; registry fingerprint; configuration/estate and fixed-policy pairing; full-shape candidate
resolution. All sixteen rows resolve with zero GL before any bind. Shadow retains
generation-before-frame-before-snapshot; overlay ID rejection precedes registry mismatch.
The closed `TextureBindingRejection` domain is `INVALID_INPUT`, `WRONG_THREAD`,
`STALE_ESTATE_GENERATION`, `STALE_DEPTH_ATTACHMENT_EPOCH`, `NO_OPEN_FRAME`, `WRONG_FRAME_ID`,
`INVALID_PASS_SNAPSHOT`, `INVALID_PROGRAM_SELECTION`, `PROGRAM_SELECTION_MISMATCH`,
`STALE_REGISTRY_GENERATION`, `SAMPLER_LAYOUT_MISMATCH`, `CLOSED_OVERLAY_LEASE`,
`OVERLAY_PUBLICATION_ID_MISMATCH`, `REGISTRY_FINGERPRINT_MISMATCH`,
`CONFIGURATION_FINGERPRINT_MISMATCH`. Phase 5's exact selector-rejection mappings and overlay
registry-generation check in `:2243-2252` remain binding; this is not the narrower
`ShadowProtocolRejection` algebra used by non-texture operations.

Phase 5 resolves full sampler shape/target/format/comparison capability against the effective
provider layout. Greatest compatible custom canonical ordinal wins; aliases retain their exact
source names. Incompatible types or distinct winning object/source/parameter identities sharing
a unit conflict rather than choosing a traversal-order winner. Compatible base fallback can return
Bound with diagnostics; missing required backing returns Degraded. Undeclared rows are Unused.
Neither Phase 8 nor Phase 6 may allocate a free unit, coerce a shape, or retry another provider.

**Identity and lifetime.** Expected ID is the immutable supplied publication's `id()`, whose
generation is **estate generation**, not registry generation. Registry generation/fingerprint,
configuration/policy and resource epoch remain distinct authenticated identities. Lease
`isCurrent()` means open and owned by the currently READY publication; equal content never revives
a retired owner. Binding validity is the intersection of its own open state and current
pass/frame/estate/depth/registry/selection/publication lifetimes. Completion, abort, neutralization,
replacement/off invalidate use but never discharge the Bound owner's `close()` duty.
`TextureBindingSnapshot.close()` idempotently releases its transferred lease; before Bound or on a
throw without transfer, Phase 8 closes only the acquired lease. It never deletes handles (even an
`Owned` handle reference remains producer-owned), retains invocation resources, or closes the
borrowed publication/source. §§4.2/4.12 incorporate the same exactly-one finally obligation.

### 5.4 Phase 6 and Phase 7 contracts consumed

**Phase 6:**

| Phase 6 §5 contract | Use |
|---|---|
| `UniformRuntime.events()` | typed celestial and primary shadow-matrix signals |
| `CelestialSample`, `ShadowMatrixSample`, `Matrix4Value` | copied values with exact world/frame identity |
| sampler participant and barrier participants | fixed integer upload after Phase 5 object binding and same-selection activation; R7-10 adopted by Phase 6 but unverified, with no fourth participant |
| deterministic inverses/per-uniform isolation | no duplicate inversion or pass-wide failure for one inverse |

The current runtime/event and sampler contracts are incorporated by
`docs/phase6/v1/PHASE_6_DOC.md` §5.1 at `:1613`, `:1617-1618`; R7-10/R7-11 are adopted but
not verified per `:296-300`. Phase 7, not Phase 8, owns runtime construction, accepted-generation
adoption and retirement under that owner's §5.1/§5.2. Its required consumer synchronization and
owner-verification gates at `:1688-1707` remain; no Phase 8 runtime-close authority is added.

**Phase 7:**

| Phase 7 §5 contract | Use |
|---|---|
| `ShadowInvocationSlot`, `ShadowInvocationContext`, `ShadowInvocationResult` | sole synchronous frame seam; exact invocation fields below; borrowed only during invoke |
| `ShadowFrameView`, `CameraSnapshot`, `PublishedRegistry`, `PublishedBufferEstate`, `FrameBarrierContexts` | exact driver frame/world/sample and main matrices; no value inferred from equality-only FrameToken |
| `ShadowExecutionBridge`/identity/view and closed results | Phase 7 sole issuer/opener/closer; authenticated dynamic-extent main-hook bypass, preserved below |
| `ProgramBindingSelection selection`, `BarrierContext activationContext` | Phase 7 selects root shadow once before invoke; Phase 8 authenticates and reuses identical values, never remints/reselects |
| `TexturePublication texturePublication`, `TextureLeaseSource textureLeases` | non-owning members of the same active tuple; acquire full expected-publication lease with that same selection |
| result semantics and H-FRAME-05 | NotInstalled/Completed advance to main clear; pre-mutation Rejected aborts one frame; Failed schedules off; Phase 8 returns and Phase 7 closes execution before main bind/clear |
| §5.3 construction protocol and §5.4 R7-13 | adopted §4.1 sequence: policy/hook plan -> new provider/runtime -> compile/compose -> create with final new registry; publication validates/fingerprints both plan and registry; no planning-time registry dependency |
| `IdScopeAdmission` and current-bind adapter | exact P7 §5.1 opaque admission/authentication and AtlasBindingEvidence/AtlasBindingSink; P8 admits owned traversal IDs only while current execution is Valid, restores before release, never mints credentials itself |

The exact field order incorporated from `docs/phase7/v1/PHASE_7_DOC.md:1576-1587` is:

```java
public record ShadowInvocationContext(
    FrameToken frame,
    ShadowFrameView shadowFrame,
    CameraSnapshot camera,
    PublishedRegistry registry,
    PublishedBufferEstate buffers,
    FrameBarrierContexts barrierContexts,
    ShadowExecutionView execution,
    ProgramBindingSelection selection,
    BarrierContext activationContext,
    TexturePublication texturePublication,
    TextureLeaseSource textureLeases) {}
```

The appended types/order are R7-12's exact request at `:2332-2342`; Phase 8 adopts that request here.
The record is Phase-7-owned, not a second Phase 8 constructor authority. Frame sample, bridge and
result semantics are incorporated unchanged from `:1588-1633` and `:1952-1998`, together with §4.2's
ownership and post-mutation result mapping. Borrowed publication, source, selection/context and
execution are never closed or retained by the slot.

`TexturePublication` retains `id`, registry fingerprint/generation, resource epoch, plan and
candidate table without lookup/close authority. The Phase-7-supplied Phase-13-owned source exposes
`TextureLeaseSource.lease(TextureOverlayPublicationId expected,ProgramBindingSelection selection)`
returning exactly `TextureLeaseResult.Acquired(TextureOverlayLease lease)` or
`Rejected(TextureLeaseRejection reason)`. Reasons are `PUBLICATION_UNAVAILABLE`,
`PUBLICATION_ID_MISMATCH`, `REGISTRY_FINGERPRINT_MISMATCH`, `STALE_SELECTION`. Acquisition
authenticates the active owner, expected ID and selection before incrementing its lease count;
rejection transfers nothing. There is no two-call `lease()`/`publicationId()` path. These producer
semantics are incorporated through `docs/phase7/v1/PHASE_7_DOC.md:2199-2215`; Phase 8 acquires no
texture owner or new direct composition authority. A present empty publication is not publication
absence and still uses this protocol. Phase 13's required fresh verification remains a gate.

### 5.5 Dependency adoption status — ungranted changes are never assumed

| ID | Owner | Required binding change | Why required |
|---|---|---|---|
| R8-1 | Phase 7 — architecturally granted, owner reverification required | Consume exact ShadowFrameView, authenticated ShadowExecutionView and driver-owned bridge from §5.4; preserve existing main-hook bypass and traversal token | §0.25 granted this; R7-12 appends credentials without changing bridge ownership |
| R8-2 | Phase 5 — architecturally granted, current shared contract adopted here subject to owner reverification | Consume §5.3's five-argument physical binding, sixteen-row closeable result and Bound-only transfer, plus typed mipmaps and coherent runtime neutralization | current §5 supersedes the historical four-row borrowed proposal; no additional shadow-only binder requested |
| R8-3 | Phase 1 — owner-granted/receiver-adopted, unverified | exact engine.shadow/mod.glue.shadow/mod.mixin.shadow slots | no new placement request; current owner/receiver verification required |
| R8-4 | Phase 7 — owner-designed/receiver-adopted, unverified | pure typed policy before provider, final-registry create and coherent close/off | planning cycle closed, fresh reviews remain |
| R8-5 | Phase 7 and Phase 2 — owner-designed/receiver-adopted, unverified | P2 v2 §4.5.4/R18 accepts all eight nested P8 hook rows; P7 `/2` serialization copies exact frozen projection | no inferred hook success or /1 compatibility |
| R7-12 | Phase 8 — adopted by §0.7, unverified | §4.2 and incorporated §§5.1–5.4 consume the exact supplied selection/context/publication/source, selector-based beginPass, full physical binding and four-result closure protocol | closes the consumer design mismatch only; real slot remains gated until fresh whole-document owner PASS |
| R7-13 | Phase 8 — adopted by §0.8, unverified | ShadowPlanInput(policy,hookHealth) and ShadowPlan contain no registry; create(plan,registry,uniforms,world,diagnostics) receives final new registry immediately after plan; §4.1 validation and publication fingerprint include it | closes the cycle without an old fingerprint; real construction stays NotInstalled until remaining grants and fresh owner/Phase 8 PASS |
| R7-10 / R7-11 | Phase 6 — owner-designed/P7-receiver-adopted, unverified | sole FixedSamplerResolver and permanent non-GL candidate/replacement/shutdown retirement | fresh current owner/receiver reviews required; no second map or runtime close |

Current P4/5/6/7/13 shared-unit contracts require fresh whole-document owner verification.
P7 has adopted P6 R7-10/11 and P8 R7-12/13; P1 R8-3 and P2 R8-5 reporting are granted and
adopted, not open grants. P3 current schema17/required companion/lossless projections are
adopted through P7; typed suffix semantics, jcpp permission and native legacy source
preservation remain separate authority/owner gates. No package/reporting grant creates
shader capability or waives P8's own fresh verification.

R8-1's consumed representation-neutral authentication contract is exact: Phase 7 is the sole
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

R8-2 now consumes these Phase-5-owned public shapes (no local alternate binding API):

```java
TextureBindingResult shadowBindings(long generation, long frameId, ShadowPassSnapshot snapshot,
    TextureOverlayLease overlay, TextureOverlayPublicationId expectedOverlay);
ShadowMipmapResult generateShadowMipmaps(long generation, long frameId,
    ShadowPassSnapshot snapshot, ShadowMipmapPolicy policy);
ShadowNeutralizationResult degradeToNeutral(long generation, ShadowNeutralReason reason);
```

Binding validation, all four results, sixteen rows and Bound-only ownership transfer are exactly
§5.3, including generation-before-frame-before-snapshot and publication-ID-before-registry
precedence. It is a physical bind, not acquisition of a non-closeable shadow-only view.
The pass/lease/binding lifetime and exactly-one finally cleanup in §4.2 are binding parts of R8-2.

`generateShadowMipmaps` returns `Generated(List<ShadowMipmapOutcome>)`,
`Neutralized(LogicalBuffer buffer,BufferFailure failure,String diagnosticId,boolean openSnapshotAborted)`,
or `Rejected(ShadowProtocolRejection)`. Canonical per-buffer outcomes remain `Generated`,
`NotAllocated`, or `Degraded(buffer,failure,diagnosticId)`. Per-buffer Degraded proves successful
base-filter restoration and lets later buffers continue. Result-level Neutralized proves the
failed-restoration containment already aborted this snapshot (`openSnapshotAborted=true`) without
flips and invalidated all bindings: stop without further completion/abort/neutralization, but still
close the Bound-owned binding in finally. Phase 5 alone mutates logical targets, never custom or
foreign replacement parameters.

`degradeToNeutral` returns `Neutralized(generation, diagnosticId, boolean openSnapshotAborted)`,
`AlreadyNeutral(generation, diagnosticId)`, or `Rejected(STALE_GENERATION)`. Success atomically
aborts and invalidates any open shadow snapshot without flips, restores safe framebuffer/texture
state, and makes all later `shadow()` calls return `ShadowEstateUnavailable`; fixed shadow units
4/5/13/14 resolve to Phase-5-owned neutral objects for that generation. Other fixed-unit policy
is unchanged. It is idempotent, and no old binding/pass snapshot remains usable after success;
the Bound owner must still close its binding snapshot.

R8-1/R8-2/R8-4's architectural grants are not readiness claims. R7-12 and R7-13 are adopted in
the active and incorporated §5 contracts here, requiring fresh whole-document verification.
Until all remaining required grants, consumer synchronization and owner reviews land, real shadow
is `NotInstalled`/unavailable with existing typed neutral-shadow behavior, never success through
an old registry, four rows or prior-frame bindings. No dependency is edited or substitute
interface fabricated.

---

## 6. Failure modes & degradation

| Failure | Ladder rung | Required behavior |
|---|---:|---|
| no shadow buffers requested | normal absence | return `Completed`; vanilla blob shadows remain; no diagnostic |
| sfb creation unavailable | 2a | use Phase 5 neutral shadow bindings, disable Phase 8, keep main program/pipeline active |
| invalid resolved shadow policy | 2a | disable shadow feature with one source-attributed diagnostic; do not clamp |
| invalid final construction input or final-registry/publication mismatch | protocol | create returns Invalid without a publication for invalid input; Phase 7 refuses mismatched installation and follows candidate rollback/off; invocation mismatch rejects before Phase 5/GL; never borrow an old registry |
| missing/over-matched H8 traversal/restore/blob hook | 2a | keep vanilla behavior, disable real shadow feature, report exact hook ID |
| frustum numeric degeneracy | 2a | disable culling/optimization for that frame and over-render loaded chunks; never under-render or crash |
| cloud draw failure | 2a | disable clouds-in-shadow only; terrain/entity shadows continue |
| one mipmap operation fails, base filter restored | 2a | consume per-buffer Degraded; disable mipmaps for that logical buffer and continue later buffers |
| mipmap base-filter restoration fails | 2a if restoration is safe, otherwise 5 | consume result-level Neutralized(...,true), stop without another complete/abort/neutralize, close binding and restore Phase 8 state; Completed only when safe, otherwise Failed |
| hardware-PCF texture setup fails | 2a | Phase 5 yields unavailable/neutral shadow estate; main continues |
| singular shadow matrix inverse | uniform rung 2 | Phase 6 disables only the corresponding inverse uniform; original matrix/pass continue |
| Phase 4 participant degrades | uniform/custom rung 1/2 | retain active shadow program and report returned degradation list |
| root shadow resolves fixed function | normal fallback | render depth/fixed function; this is not compile failure |
| root activation returns `ShadersOff`/`FailedSafe` | 3/5 | abort snapshot, restore all state, return Phase 7 `Failed`; vanilla path remains reachable |
| stale context/registry/estate before GL | protocol | mutation-free `Rejected`; Phase 7 aborts this shader frame but keeps healthy publication |
| texture lease Rejected or shared binding Degraded/Rejected | selected-pass suppression | zero texture binds and no transfer; abort undrawn shadow snapshot without flips/mipmaps; close only any acquired lease; no activation/upload/draw/reselection; Completed after safe restoration, not a post-GL Phase 7 Rejected |
| shared binding BackendFailed | 2a if contained, otherwise 5 | partial binds possible, no transfer; close caller-owned lease, abort open snapshot, apply coherent neutralization and restore; no activation/upload/draw |
| Phase 5 target bind/clear/copy backend failure | 2a if contained, otherwise 5 | abort open snapshot without flips, then neutralize coherently and disable shadow only; failed containment forbids further shader draws and returns Failed |
| post-binding activation StalePublication | 5 | abort/restore and return Failed; never retry or describe the already-mutated invocation as mutation-free |
| vanilla draw throws | 5 | abort, restore state/traversal/Forge pass, report `Failed`, then preserve outer throwable policy |
| state restoration cannot be proven | 5 | forbid further shader draws, return `Failed`, Phase 7 schedules shaders off |

For backend-failure containment, `abortPass` consumes the still-open shadow token once, then
`degradeToNeutral(generation,BIND_BACKEND_FAILURE | CLEAR_BACKEND_FAILURE |
DEPTH_COPY_BACKEND_FAILURE | PASS_BACKEND_FAILURE)` uses the matching operation reason.
`Neutralized` or `AlreadyNeutral` permits `DISABLED_RUNTIME` and `Completed` only after safe state
restoration and with current execution authority. Rejected neutralization, failed abort, or
unprovable restoration returns `Failed`; never touch a replacement estate using stale credentials.
Result-level mipmap `Neutralized` has already done the terminal work and skips these calls.
On every path, binding invalidation and pass termination leave the independent finally closure
obligation intact. The real slot is not installed at all while required architectural grants or
owner verification remain absent; an ungranted operation is not a runtime fallback.

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
- `invoke`, every Phase 4/5 operation, texture lease acquisition and binding/lease closure, FF state
  lease, vanilla setup/draw, Forge pass change, and mipmap generation are render-thread-only.
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
The invocation does not hash source bytes, rebuild the unit policy, or copy/re-resolve a provider
layout per draw. Phase 5 owns precomputed immutable binding evidence; no optimization may cache a
lease or physical binding past its authenticated lifetime.

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
9. **Plan/publication identity:** planning succeeds without a registry; equal policy/hook inputs
   yield equal plan fingerprints across compilations. Changing only the final registry changes
   publication identity, not plan identity; wrong-registry invocation rejects before Phase 5/GL,
   and equal content never revives a closed epoch.

Pintonium's dawn/negative-coordinate sample is a cross-check, not a golden authority. Expected
values are generated from the formulas in §4.5 and compared independently.

### 8.2 Recorded facade and glue tests

With `RecordingGLDevice`/scripted ports and fake verified dependencies:

- assert Phase 7 select-once -> beginPass with identical selection -> target bind/clear -> uniform
  value signals -> atomic expected-publication lease -> sixteen-row preflight -> ascending physical
  binds -> same-selection/context activation -> ordered draws/split -> mipmaps -> complete ->
  binding close -> reverse restoration -> Phase 7 execution close/main clear;
- inject failure after each operation and assert reverse restoration, exact abort count, and no
  later draw;
- assert fixed-function root still draws depth;
- assert all sixteen rows, including non-shadow inputs, and no Phase 8 row-bind loop; unused rows
  perform no bind and a fixed-function shadow pass has purpose NONE with no sampler upload;
- exercise lease rejection and all four TextureBindingResult branches: Degraded/Rejected bind
  nothing and abort without flips or mipmaps; late BackendFailed may partially bind but never
  activates/uploads/draws; compatible fallback diagnostics can accompany Bound;
- prove Bound closes only binding once; every non-transferring result/throw closes only acquired
  lease once; completion/abort/neutralization/off/replacement never silently release closure duty;
- reject stale/content-equal retired leases, wrong expected publication and selector/context
  mismatch in owner-defined order; freeze ordinary and shadow readable sides at acquisition;
- assert objects bind before sampler integers, with no reselection, stale retry, publication
  transition or manual GL bind between physical binding and activation;
- assert mipmap filter-restoration failure consumes result-level Neutralized and performs no second
  complete/abort/neutralize while still closing the binding and restoring camera/traversal state;
- assert the real slot remains NotInstalled while any remaining grant, consumer synchronization
  or required owner-verification gate remains open, despite R7-12/R7-13 adoption;
- exercise first-load and reload ordering: pure plan/celestial policy before provider/runtime,
  compile/compose before final-registry create; wrong candidate pairing prevents installation,
  compile failure creates no real slot, and no path borrows an old registry;
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
| Phase 7 slot/context/composition | v0.2 | R8-1/R8-4 architectural grants consumed; R7-12/R7-13 adopted here; remaining grants, consumer synchronization and fresh owner/Phase 8 whole-document verification before real slot |
| Phase 5 shared binding/mipmap/neutralization | v0.2 | five-argument physical binding and sixteen-row closure protocol consumed; fresh owner verification first, no four-row substitute |
| FF state/world port and hook ledger | v0.2 | Cleanroom integration after pure tests |
| terrain/entity/cloud/split render order | v0.2 | ordered recorded test then T1 |
| PCF/filter/mipmap activation | v0.2 | Phase 5-backed, per-feature degradation |
| blob-shadow suppression | v0.2 | health-gated with fire-preservation test |
| Phase 6 matrix/celestial wiring | v0.2 | before first shadow program activation |
| Phase 9 IDs during shadow entities | v0.3 | consume later scopes; no Phase 8 redesign |
| Phase 10 extended shadow terrain vertices | v0.3 | ordinary vanilla draw-path integration |
| Phase 13 custom/companion shadow inputs | v0.5 | supplied publication/lease source and same full shared binding; pre-v0.5 present-empty publication is not absent authority |
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
- if runtime neutralization cannot safely contain a backend failure, fail the shader pipeline
  rather than continue with a partial shadow target;
- while remaining required grants, consumer synchronization or owner reviews remain open, keep
  real shadow NotInstalled with typed unavailable/neutral behavior; R7-12/R7-13 adoption is not readiness.

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
| D-P8-12 | Adopt R7-12's same-selection, full shared physical binding and Bound-only closure contract | Phase 7 §5.4 `docs/phase7/v1/PHASE_7_DOC.md:2332-2342` and Phase 5 §5.1 `docs/phase5/v1/PHASE_5_DOC.md:2357-2363` supply the operation; preserves RESEARCH App B.3 `docs/research/v1/RESEARCH.md:1228-1255` without a second map or four-row success |
| D-P8-13 | Adopt R7-13: pure policy/hook plan first; final-registry validation and fingerprinting only at publication construction | Phase 7 §5.4 `docs/phase7/v1/PHASE_7_DOC.md:2344-2349` requires registry-independent metadata and final-registry create; breaks the provider/compile cycle without borrowing an old registry or weakening live generation/epoch authentication |
| D-P8-14 | accept authenticated shadow execution as alternate P9 ID admission and independent P7 color producer | delivers IDs without illegal main gbuffers scopes; preserves Forge pass order and nested restoration |
| D-P8-15 | adopt current producer grants/schema and authenticated current-bind atlas route | distinguish architecture adoption from verification; no second binder, stale atlas inference or historical schema fallback |

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
6. **Phase 7 context gap — architecturally closed:** R8-1 supplies driver frame/sample and
   authenticated bridge; R7-12's appended selection/context/publication/source are adopted in
   §§4.2/5 here. Owner reverification still gates use; no frame field is inferred from FrameToken.
7. **Phase 5 post/binding gap — architecturally closed, consumer migrated:** current §5 supplies
   shared physical binding, typed mipmaps and runtime neutralization. R8-2 consumes that contract,
   not its historical four-row view; Phase 8 owns only lease/binding closure, never object binds.
   Phase 5's current §5 still needs fresh whole-document verification.
8. **PCF ownership wording:** the Phase 8 assignment names the feature, while verified Phase 5
   already owns texture-parameter setup. Phase 8 owns policy disposition/timing diagnostics and
   consumes Phase 5's operation; it does not duplicate the GL call.
9. **Digest `glFlush`:** the behavioral digest reports one before post-processing, but RESEARCH
   constrains pass order rather than a flush. D-P8-11 relies on same-context command ordering and
   leaves any evidence-driven synchronization change to a governed correction, not an incidental
   glue call.
10. **Planning cycle architecturally closed:** R7-13 is adopted in §§2/4/5 with the exact
    input/factory order requested at `docs/phase7/v1/PHASE_7_DOC.md:2344-2349`. The pure plan
    precedes the provider/runtime and compilation; final publication validation/fingerprinting
    includes the final new registry. An old registry fingerprint is never a workaround.
11. **Shared pipeline gates:** current P6 R7-10/11 and P8 R7-12/13 are owner-designed and
    P7 receiver-adopted, unverified. P1 R8-3 and P2 R8-5 are likewise adopted. Fresh current
    owner/receiver reviews and separate §5.5 upstream authority gates remain; historical
    PASS never certifies this amendment.

### 11.4 Open hand-offs

- P7/P9 alternate shadow IdScopeAdmission is adopted in §4.8.2/§5; verify nested entity/TE
  restoration and failure zeroing before shadow release, without changing Forge pass order.
- Phase 10 must verify both VBO and client-array extended attributes during Phase 8's ordinary
  RenderGlobal layer calls.
- Phase 13 supplies shadow-stage companion/custom/noise candidates through the full expected-
  publication lease lent by Phase 7; Phase 5 selects/binds compatible fixed-unit objects.
  Publication absence is not a present empty candidate table; no dynamic unit is allocated.
- Phase 14 may profile the second setup/traversal and mipmap calls; full-view and synchronous
  fallbacks remain mandatory.
- G8/S1 consumes the completed shadow targets after Phase 8 and adds real `shadowcomp` flips; it
  may not move the v0.2 water-shadow split.

### 11.5 Requested upstream changes

§5.5 is the active adoption/gate ledger. R8-1–5 and R7-10–13 are owner-designed and receiver-
adopted as applicable, not fresh verified implementation permission. Current schema17 and
P7's exact reload/current-bind/ID adapters require coordinated fresh reviews. Real shadow
remains NotInstalled until those gates and applicable separate upstream authority requests
close. No duplicate package grant or `/1` reporting fallback is required.

No change is requested to RESEARCH's shadow contract. A future DESIGN candidate should retain the
Phase 5/Phase 8 PCF ownership split explicitly. This amendment preserves that behavior and the
traversal/camera/bridge/copied-depth/mipmap/neutralization contracts; it grants no broader redesign.

---

## 12. Implementation checklist

1. **[v0.2]** Obtain fresh whole-document owner and P8 reviews for §5.5's adopted contracts,
   and close applicable separate upstream authority gates. Keep real shadow NotInstalled
   until verified; no repeated package/reporting grant request.
2. **[v0.2]** Add the granted engine/glue/mixin packages with seam tests rejecting Minecraft,
   Forge, Mixin, and LWJGL from `:engine`.
3. **[v0.2]** Implement registry-independent `ShadowPolicy`/plan validation, fingerprinting and
   closed results; headless invalid/absence and plan/publication identity-boundary tests.
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
16. **[v0.2]** Consume and authenticate Phase 7's once-selected SHADOW selection/context and
    contained pass; prove no resolve/select/context-remint call inside Phase 8.
17. **[v0.2]** Implement selector-based beginPass, frozen ordinary+shadow readable sides,
    bind/clear/abort/complete branching and one-snapshot invariant.
18. **[v0.2]** Acquire the supplied expected-publication lease and call five-argument shadowBindings;
    exercise sixteen rows, physical binding before activation/upload, all four results and
    exactly-one finally closure after Bound versus every non-transfer/throw; no manual row loop.
19. **[v0.2]** Emit celestial and primary shadow matrix events before first barrier activation;
    assert Phase 6 inverse isolation.
20. **[v0.2]** Activate Phase 4 after Bound with the identical supplied selection/context and
    shadowPass=true; cover every barrier result and fixed-function NONE/no-upload behavior.
21. **[v0.2]** Render terrain exactly SOLID -> CUTOUT_MIPPED -> CUTOUT; recorded call-order test.
22. **[v0.2]** Implement configured cloud draw with cloud-only degradation.
23. **[v0.2]** Implement Forge pass 0 entity traversal and exact prior-pass restoration.
24. **[v0.2]** Issue exactly one `SHADOW_PRE_TRANSLUCENT` copy and handle all Phase 5 outcomes.
25. **[v0.2]** Render optional `shadowTranslucent`, then Forge entity pass 1; false/true matrix tests.
26. **[v0.2]** Implement typed post-pass mipmaps and per-buffer degradation/filter restore; on
    result-level Neutralized prove no duplicate terminal call and preserved binding closure.
27. **[v0.2]** Implement H8-BLOB-01 blob-only suppression and fire-preservation tests; gate feature
    enablement on hook health.
28. **[v0.2]** Consume coherent runtime neutralization and prove main pipeline remains active only
    after safe feature-local containment; partial bind failures never activate/upload/draw.
29. **[v0.2]** Integrate plan-before-provider and final-registry create/validation/fingerprinting
    into Phase 7 construction/rollback/close after §5.5's gates; prove no old-registry dependency,
    reject wrong candidate pairing, and drain binding/lease closure between paired publications.
30. **[v0.2]** Add Phase 8 hook-health data to Phase 2 manifests after R8-5; no inferred capability.
31. **[v0.2]** Run static day/night, moving-camera, water split, entity pass, cloud, perspective,
    PCF/mipmap, and far-caster T1 scenes.
32. **[v0.2]** Run first classic-pack T2 shadow comparisons; retain local/CI images only and commit
    manifests/hashes.
33. **[v0.2]** Satisfy the implementation gate: classic-shadow packs at T1 and first T2 results,
    with no unresolved state-restoration or traversal-omission defect.
34. **[post-v0.5]** Hand completed targets to G8/S1 for `shadowcomp`; do not implement it in this
    checklist.

**Current §G1.3 status:** active §5 adopts R7-12/13 and the integration corrections in §0.9.
Phase 8 remains **unverified**; fresh whole-document owner/receiver review returning literal
PASS is required. Package/reporting and planning/binding architecture is adopted, not
implementation evidence. Separate upstream authority gates remain; real shadow stays
NotInstalled/unavailable until verified. No code/reviews/builds/tests/validation were changed
or run; v1 is retained.
