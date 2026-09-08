# Schmaloogium — Phase 8: Shadow pass — Architecture

## 0. Header

**Phase:** 8 — Shadow pass
**Milestone:** v0.2
**Date:** 2026-08-02 · **Last revised:** 2026-09-08 (§0.17).
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
   - §4.5, lines 568–581;
   - Appendix A.3, lines 1155–1192;
   - Appendices B.2–B.3, lines 1217–1255;
   - Appendix D.3, lines 1355–1368;
   - Appendix E rows 1–2, lines 1401–1402.
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

- `docs/research/v1/RESEARCH.md` Appendix D.2 lines 1339–1353 was read because the assignment
  explicitly owns `shadowAngle`, which is not in the listed D.3 table.
- `docs/research/v1/RESEARCH.md` Appendix F.1 lines 1441–1445 was read because the assignment
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
(`docs/phase7/v1/PHASE_7_DOC.md:2348`), continued by “registry to break the cycle” at `:2349` —
historical pins retained; the quoted text’s current location is `docs/phase7/v1/PHASE_7_DOC.md:4068-4069`
(2026-09-08 fix-up note).
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

### 0.10 IR-18 prepared submissions and schema18 (2026-09-07)

RC3 remains governing. Research §3.2/4.5 and RC3 P4/P7/P8 assignments were read before the
maintainer's explicit prepared-submission/v0.5 decision. P4 §11.5 records independently checked
author/OSS evidence and its limits. §§4.8.5/5/11 adopt the receiving contract without replaying
world/Forge traversal; P3 schema18 is consumed through P7, with no older-schema upgrade.
§5 changed and remains unverified. Earlier addenda/PASS are historical; no implementation,
validation, tests, formatter, reference-code reuse or certification is claimed.

---

### 0.11 R5 celestial dataflow correction (2026-09-08)

Authorized architecture-only R5-1 fix-up, D-P8-22: separate pure angular policy from current
frame-bound celestial sampling. Read the complete preserved R5, governing RC3 Phase 8/§G1.3,
and P6 event/provider and P7 frame/camera receiver contracts. §§2/4/5 now pass the actual
post-camera snapshot and unchanged frame sample into camera math; P7's provider uses the same
pure angular result before capture. §5 changed and remains unverified; coordinated P8 and P7
receiver reverification is required. P7 R37 PASS is historical for the changed receiving surface;
R38 is required. No self-PASS, implementation or execution evidence is claimed. Schema21,
reporting, traversal, binding, publication and restoration contracts remain unchanged.

### 0.12 R6 terminal release correction (2026-09-08)

D-P8-23 specifies the missing current-frame barrier release after per-draw restoration and
before platform restoration, with exhaustive release-result containment and independent cleanup.
P7 D-P7-40 receives the terminal contract. R6's review body/verdict remains historical;
§5 is changed/unverified and fresh owner/receiver review remains required. Backend lock
ownership stays P1/P4; Main integrates their final receiving terms and P2 `/4` evidence.
No implementation or validation commands are authorized by this architecture correction.

### 0.13 R7 demand and disabled-estate correction (2026-09-08)

D-P8-26/27 correct R7-1/2 with the three-field registry-independent planning input and
accepted-P5 feature-disable transition before admission. D-P8-28 makes the existing P7
replay-delivery prerequisite explicit before shadow drawing; it is P7 R39's receiver note,
not a third P8 finding or new API. Earlier amendment descriptions remain historical.
The original R7 review/counts/verdict are preserved with separate Resolutions. §§2/4/5/6/8/9/11/12
carry the current contract; P7 owns its construction/admission and replay receiver implementation
description. §5 remains changed/unverified pending fresh coordinated review. No validation,
implementation or runtime proof is claimed; IR-01 and final G5.3 remain open.

### 0.14 R8 forced traversal and schema22 correction (2026-09-08)

D-P8-29 resolves review 8 R8-1 in §§4.2/4.4/4.7/4.13/5/6/8/9/12. Read the preserved
review, RC3 §G1.3/Phase 8 assignment and RESEARCH §§0–1/4.5/E.1; independently read
the review's vanilla `RenderGlobal.setupTerrain` control flow and resolved the dirty setter,
cache/debug fields and traversal anchors through Cleanroom MCP (1.12.2 stable_39).
This is a re-derived scoped operation, not copied transformation code or a replacement renderer.
Primary control-flow evidence: [vanilla RenderGlobal.java, setupTerrain lines 840–1020 and
dirty setter lines 2639–2642](https://github.com/KealJones/mc-1.12.2-source_files/blob/master/src/minecraft/net/minecraft/client/renderer/RenderGlobal.java).
This source establishes the cache/debug gates and scheduling tail, not a runtime result or
byte-identical claim for a different loader revision; mapped-anchor health remains mandatory.
D-P8-30 receives schema22 through the exact-current admission below; older numeric receipts
are historical. §5 changed and remains unverified; P7 must receive the revised hook projection
and forward it unchanged to P2. No implementation, validation or runtime result is claimed.

### 0.15 R9 scalar health, estate receiver and schema23 correction (2026-09-08)

D-P8-31/32 resolve C1/C2 in the preserved R9; D-P8-33 receives the coordinated schema23
cutover. Inputs actually read: the complete owner and R9 (C1/C2 in full), governing RC3
Part I/Phase 8 assignment, RESEARCH §§0–1/4.5 and applicable App A.3/B.2/B.3/D.2/D.3/E/F
rows, and P5's actual publication/view declarations and ownership semantics in §2.
§4.13 now publishes the complete flattened scalar catalogue, not group-summary wire rows.
Construction/invocation authenticate the accepted wrapper's present estate once before dispatch.
Earlier numeric schema and health-cardinality receipts are historical, not current admission.
RC3, confidence/source restrictions and original review bodies remain unchanged. This is a
documentation-only correction; §5 changed/unverified, with P7/P2 receiving edits and fresh
whole-document owner/receiver verification required. No implementation or validation was run.

### 0.16 R10 shadow-only outline exclusion (2026-09-08)

D-P8-35 resolves C1 in the preserved R10 through live §§4.8.2/4.13, incorporated §5,
future §8 fixtures and §12. Inputs read: this owner's header and load-bearing traversal,
authentication, health and interface contracts; RC3 G0 and Phase 8 assignment; RESEARCH
§§0–1/4.5; P7's health and invocation receiver; and R10's complete correction and provenance
note. R10's primary vanilla/Forge control-flow evidence motivates the exact invocation guard,
not a claim of new mapped-source or runtime verification. Historical pin limitations and
confidence remain unchanged. At this amendment the catalogue was 60 rows, 59 required non-CLOUD
under `ShadowHookHealth/flattened-v2`; D-P8-37 supersedes that historical cardinality/domain.
§5 changed/unverified; Main owns P7/P2 receiver adoption and fresh owner/receiver review.
No implementation, authority expansion or validation is performed.

### 0.17 Attempt7 sort-state and independent celestial correction (2026-09-08)

D-P8-37 resolves preserved R11 C1; D-P8-38 grants the pure producer required by P7 R43 C1.
Read the current owned state/math/interfaces, R11, RC3 G0/Phase 8 assignment, RESEARCH
§§0–1/4.5/D.2–D.3 and P6/P7 receiving event/camera contracts. The six new sort-cache
accessor observations make the current catalogue 66 rows/65 non-CLOUD under
`ShadowHookHealth/flattened-v3`; earlier cardinalities/domains are historical.
The pure celestial producer has no shadow-availability prerequisite. §5 is changed/unverified.
D-P8-39 also receives P5/P13/P7's actual-base context and synchronous retained-shadow binding
refresh; existing binder signatures, root activation, result ownership and cleanup remain.
Fresh owner/receiver reviews remain gates. R11's body/verdict and source-pin limitations remain
unchanged; no implementation, validation, runtime proof or broadened source authority is claimed.

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
  snapshot, the D-P7-76 main-estate bind/clear before vanilla sky and rebind after the slot
  returns, publication/reload transaction, and final recovery decision. Phase 8 is a synchronous
  slot implementation and returns before the main-estate rebind.
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
    ShadowHookHealth hookHealth,
    boolean requested) {}

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

public record ShadowCelestialAngles(boolean day, float shadowAngle, double thetaRadians) {}

public final class CelestialMath {
    private CelestialMath();
    public static ShadowCelestialAngles angles(float sunAngle);
    public static CelestialSample sample(ShadowFrameView frame, CameraSnapshot mainCamera,
                                         float sunPathRotationDegrees);
}

public interface ShadowCelestialPolicy {
    ShadowCelestialAngles sample(float sunAngle);
}

public interface ShadowCameraMath {
    ShadowCamera compute(ShadowFrameView frame, CameraSnapshot mainCamera,
                         ShadowPlan plan, Extent2i shadowExtent);
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
or hook-ID order (the exact ASCII-ascending flattened catalogue in §4.13 for hook rows).
`ShadowPolicy` is the complete effective configuration-derived policy projection.
Pure input identity is the structural triple `(policy, hookHealth, requested)`, including demand
even when the policy and health are otherwise identical. `ShadowPlanFingerprint` hashes every
policy value, the hook fingerprint and canonical boolean `requested=true` for a Ready plan,
never a registry fingerprint or generation. Absent/disabled results have no fabricated plan hash;
any planning-result cache key includes the full input triple. The retained celestial policy is a
stateless delegate to `CelestialMath.angles`, not an availability gate. The final publication separately fingerprints the ordered pair
`(plan.fingerprint(), registry)` supplied to `create`, using canonical SHA-256 encoding. Publication pairing/reuse must
match both inputs, not the plan fingerprint alone; equal hashes never revive a closed slot epoch.
`ShadowHookHealth` is owned by the application health audit and borrowed immutably by plans.
`ShadowCameraMath` implements exactly
§4.5–§4.6; `planes()` returns an immutable normalized ordered fixture view and `intersects` is the
total finite-AABB predicate. Public static `CelestialMath.angles` owns §4.5.1 and public static
`CelestialMath.sample` owns §4.5.4's unchanged Phase 6 event construction. Neither requires an
instance, Ready plan, requested/enabled shadows, estate, extent, hook health, GL or retained world.
`ShadowCelestialPolicy.sample` delegates to `CelestialMath.angles` without another formula.
`compute` delegates celestial production to `CelestialMath.sample(frame,mainCamera,
plan.policy().sunPathRotationDegrees())` and stores it in `ShadowCamera`. P7 main sky calls the
same public route without constructing shadow state. These are the sole callable shapes.

`ShadowFrameView` and `ShadowExecutionView` are Phase-7-owned contracts, incorporated in §5.4;
the declaration above reproduces the value shape, not a second issuer. The world port contains no
Minecraft object in `:engine`; its glue implementation is construction-bound to the current client
renderer and validates the borrowed Phase 7 execution view before each operation. The invocation
also borrows the exact `selection`, `activationContext`, `texturePublication`, and `textureLeases`
specified in §5.4. None is construction-retained or closed by Phase 8.

### 2.3 Relationship and ownership map

```text
Phase 7 projection of accepted Phase 3 policy + minima-derived requested + hook health
        |
        v
 ShadowPlan (pure, registry-independent); Phase 6 provider gets independent CelestialMath.angles
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
                  Phase 7 main rebind and gbuffers
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
8. An authenticated present estate's not-requested/unavailable **shadow result** never blocks
   the main pipeline; it uses Phase 5's neutral disposition and returns `Completed`. An absent/off
   publication estate or mismatched wrapper/view is invalid input, not normal shadow absence.
9. `shadowcomp` is never executed at v0.2.
10. Phase 7 selects root shadow once; Phase 8 uses that identical selection/context for acquisition,
    physical binding and activation. Only `Bound`, successful activation and §4.10's replay-delivery
    prerequisite authorize draw.
11. The shared binding has sixteen ascending rows, not four shadow-only rows. Phase 5 freezes
    ordinary and shadow readable sides and performs every required object bind before activation
    and sampler upload. No Phase 8 row-bind loop or fallback re-resolution exists.
12. Completion/abort/neutralization invalidates binding use, not the Bound owner's closure duty.
13. Planning needs only policy, hook health and accepted-minima-derived request presence. Final
    construction fingerprints the plan with the new registry; no prior registry enters planning.
14. Positive-demand disabled/uninstalled composition establishes §4.1's accepted P5 neutral estate
    before main admission; `NotInstalled` alone never makes real undrawn bindings safe.

---

## 3. Contract conformance map

### 3.1 Shadow-pass behavior

| Contract item | Design element | Provenance |
|---|---|---|
| pass inside frame begin, completed before main gbuffers draws (P7 main bind/clear precede vanilla sky, D-P7-76) | Phase 7 H-FRAME-05 slot and §4.2 transaction | `[V:observed]` `docs/research/v1/RESEARCH.md:568`–`:581`; `docs/phase7/v1/PHASE_7_DOC.md:648`–`:655`, ordering `:1129`–`:1136` and `:1153`–`:1159` |
| force third person, restore afterward | `ShadowStateLease` in §4.4 | `[V:observed]` `docs/research/v1/RESEARCH.md:570` |
| ortho ±`shadowDistance`, near 0.05, far 256 | exact matrix in §4.5.2 | `[V:observed]` `docs/research/v1/RESEARCH.md:570`–`:571` |
| perspective when `shadowMapFov` is set | exact square-aspect perspective in §4.5.3 | `[V:observed]` `docs/research/v1/RESEARCH.md:571`–`:572` |
| sun by day, moon by night, `sunPathRotation` | §4.5.1/§4.5.4 | `[V:observed]` `docs/research/v1/RESEARCH.md:572`; App A.3 at `docs/research/v1/RESEARCH.md:1179` |
| portable camera/snap structure | §4.5 re-derives the projection/model-view/snap values and rejects the reference's perspective bottom-right value and perspective snapping | PD §10 (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:594`–`:618`); `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/modern/src/main/shaders_java/net/irisshaders/iris/shadows/ShadowMatrices.java:13–83]`; contract check against `docs/research/v1/RESEARCH.md:570`–`:573`; D-P8-1/D-P8-2 |
| texel snap by `shadowIntervalSize` | §4.5.5, Java-remainder formula, ortho only | `[V:observed]` `docs/research/v1/RESEARCH.md:573`; behavioral cross-check `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:366`–`:371`; D-P8-1/D-P8-2 |
| shadow MVP planes plus light side planes | §4.6 exact plane algebra | `[V:observed]` `docs/research/v1/RESEARCH.md:574`–`:575` |
| sun-aligned optimized traversal | §4.7 dominant-axis/prism plan + scoped vanilla setup; no modern collector is adopted | `[V:observed]` `docs/research/v1/RESEARCH.md:575`–`:576`; PD §10 (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:605`–`:609`) confirms the 1.12 renderer is absent; D-P8-4 |
| terrain solid -> cutout-mipped -> cutout | §4.8.1 | `[V:observed]` `docs/research/v1/RESEARCH.md:576`–`:577` |
| Forge entity passes | §4.8.2 pass 0/pass 1 protocol | `[V:observed]` `docs/research/v1/RESEARCH.md:577`; Cleanroom verification in §0.2 |
| water-shadow depth split | §4.8.3 exactly one `SHADOW_PRE_TRANSLUCENT` copy | `[V:doc]` `docs/research/v1/RESEARCH.md:1224`–`:1226` |
| optional translucent terrain | resolved `shadowTranslucent` in §4.8.4 | `[V:doc]` `docs/research/v1/RESEARCH.md:578`–`:579`; F.1 `:1441`–`:1445` |
| per-config shadow mipmaps | §4.9 typed post-pass request | `[V:doc]` `docs/research/v1/RESEARCH.md:1173`–`:1175`; R8-2 |
| hardware PCF | Phase 5 construction applies compare policy; Phase 8 gates/diagnoses it | `[V:doc]` `docs/research/v1/RESEARCH.md:1174`; verified dependency at `docs/phase5/v1/PHASE_5_DOC.md:2091`–`:2096` |
| blob-shadow suppression | H8-BLOB-01 redirects only the blob call, retaining fire | `[V:observed]` `docs/research/v1/RESEARCH.md:580`; §4.13 |
| clouds only per shadow config | §4.8.1 pre-split optional cloud draw | `[V:observed]` `docs/research/v1/RESEARCH.md:580`–`:581` |
| force root `shadow` program for the shadow draw interval | §4.10: begin immediately before the first shadow draw; end before fixed-function/state restoration | `[V:doc]` assignment `docs/design/v2.0-RC3/DESIGN.md:2000`–`:2001`; Phase 4 force-shadow barrier `docs/phase4/v1/PHASE_4_DOC.md:1761`–`:1763` and `:1861`–`:1872` |

### 3.2 Appendix A.3 shadow-directive coverage

| A.3 row | Phase 8 disposition | Provenance |
|---|---|---|
| `shadow`/`shadowtex0`/`shadowtex1`/`watershadow` declarations | P7 derives requested from accepted P3 shadow minima before planning; execution additionally requires P5 available estate; no rescan | `[V:doc]` `docs/research/v1/RESEARCH.md:1162`; `docs/phase5/v1/PHASE_5_DOC.md:2086`–`:2087` and §5.1 `:2710` |
| `shadowcolor`/`shadowcolor0/1` declarations | Phase 5 snapshot owns zero-to-two v0.2 color attachments; Phase 8 draws and completes their flips | `[V:doc]` `docs/research/v1/RESEARCH.md:1163`; `docs/phase5/v1/PHASE_5_DOC.md:1349`–`:1352` |
| `shadowMapResolution` / `SHADOWRES` | Phase 5 extent is authoritative; Phase 8 uses it as viewport and rejects non-positive mismatch | `[V:doc]` `docs/research/v1/RESEARCH.md:1168` |
| `shadowMapFov` / `SHADOWFOV` | optional perspective branch in §4.5.3 | `[V:doc]` `docs/research/v1/RESEARCH.md:1169` |
| `shadowDistance` / `SHADOWHPL` | orthographic half-plane and traversal basis | `[V:doc]` `docs/research/v1/RESEARCH.md:1170` |
| `shadowDistanceRenderMul` | positive values enable §4.7 optimization; non-positive values select full-view traversal | `[V:doc]` `docs/research/v1/RESEARCH.md:1171` |
| `shadowIntervalSize` | §4.5.5, default 2.0 | `[V:doc]` `docs/research/v1/RESEARCH.md:1172` |
| `generateShadowMipmap` / `generateShadowColorMipmap` | unioned with per-texture requests in immutable `ShadowMipmapPolicy` | `[V:doc]` `docs/research/v1/RESEARCH.md:1173` |
| `shadowHardwareFiltering`, `0`, `1` | preserved per depth texture; Phase 5 applies compare mode at creation | `[V:doc]` `docs/research/v1/RESEARCH.md:1174`; `docs/phase5/v1/PHASE_5_DOC.md:2091`–`:2096` |
| per-texture shadow mipmap aliases | canonical per-logical-buffer set; generated only after draws/copy | `[V:doc]` `docs/research/v1/RESEARCH.md:1175` |
| per-texture nearest aliases | immutable Phase 5 texture policy; Phase 8 never mutates ordinary min/mag choice | `[V:doc]` `docs/research/v1/RESEARCH.md:1176` |
| `sunPathRotation` | model-view and celestial-vector rotation in §4.5 | `[V:doc]` `docs/research/v1/RESEARCH.md:1179` |
| shadow program `DRAWBUFFERS` | consumed through Phase 4 `ProgramStateBundle` and Phase 5 `ShadowPassSnapshot`; order and duplicates retained | `[V:doc]` generic row `docs/research/v1/RESEARCH.md:1188`; dependency `docs/phase4/v1/PHASE_4_DOC.md:1373` |

The Phase 3 front end remains responsible for recognizing both const and comment forms, including
capitalization aliases. Phase 8 consumes only the resolved result.

### 3.3 Appendix B.2/B.3 shadow rows

| Contract row | Design element | Provenance |
|---|---|---|
| shadowtex0 = everything | real Phase 5 depth attachment; clear then all shadow draws | `[V:doc]` `docs/research/v1/RESEARCH.md:1224`; `docs/phase5/v1/PHASE_5_DOC.md:1334`–`:1343` |
| shadowtex1 excludes shadow translucents | exact split point in §4.8.3 | `[V:doc]` `docs/research/v1/RESEARCH.md:1225` |
| shadowcolor0/1 | Phase 5 typed color attachments and generic completion flip | `[V:doc]` `docs/research/v1/RESEARCH.md:1226`; `docs/phase5/v1/PHASE_5_DOC.md:990`, `:2088`–`:2094` |
| unit 4 = shadowtex0/watershadow/conditional shadow | Phase 5 sole fixed policy and shared sixteen-row physical binding; Phase 6 uploads fixed integers only after Bound/activation, with R7-10 still gated | `[V:doc]` `docs/research/v1/RESEARCH.md:1236`; Phase 5 shared table `docs/phase5/v1/PHASE_5_DOC.md:2723`; §5.3 |
| unit 5 = shadowtex1/conditional shadow | same | `[V:doc]` `docs/research/v1/RESEARCH.md:1237` |
| unit 13 = shadowcolor0/shadowcolor | same | `[V:doc]` `docs/research/v1/RESEARCH.md:1245` |
| unit 14 = shadowcolor1 | same | `[V:doc]` `docs/research/v1/RESEARCH.md:1246` |

No unit is dynamically allocated. The sole Phase 5 policy selects unit 5 for `shadow` only when the
effective provider layout directly declares sampler-compatible `watershadow`; buffer count is
irrelevant. The shared result covers all sixteen App B.3 rows, not just the four listed above.

### 3.4 Appendix D shadow rows

| Uniform(s) | Producer and timing | Provenance |
|---|---|---|
| `shadowAngle` | public CelestialMath.angles supplies P7's frame provider independently of shadow availability; delegating policy drives camera identically | `[V:doc]` RESEARCH App D.2; §§4.3/4.5.1 |
| `sunPosition`, `moonPosition`, `shadowLightPosition`, `upPosition` | public CelestialMath.sample for P7 main celestial moment, reused for same association; real-shadow event still before activation | `[V:doc]` RESEARCH App D.3; §§4.3/4.5.4 |
| `shadowProjection`, `shadowModelView` | one `ShadowMatrixSample` after FF camera installation and before activation | `[V:doc]` `docs/research/v1/RESEARCH.md:1365`; Phase 6 event at `docs/phase6/v1/PHASE_6_DOC.md:280`–`:284` |
| `shadowProjectionInverse`, `shadowModelViewInverse` | Phase 6 deterministic inversion of the same two primary matrices | `[V:doc]` `docs/phase6/v1/PHASE_6_DOC.md:785`–`:810` |

All matrices use Phase 6's column-major `Matrix4Value` upload order. A singular inverse disables
only that inverse; it does not suppress the original matrix or the pass.

---

## 4. Detailed design

### 4.1 Plan construction and lifecycle

`ShadowPlanFactory` is pure. Its exact input is
`ShadowPlanInput(ShadowPolicy policy, ShadowHookHealth hookHealth, boolean requested)`. It never accepts a registry
fingerprint/generation, source strings, property maps, Minecraft objects, GL handles, or mutable
collections. The resulting `ShadowPlan` likewise contains no hidden registry identity.
P7 supplies only the resolved projection after exact `schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION`
checks on containing configuration and nested IDs (23 adopted by D-P8-33), and any received
inspection snapshot; reject every other version or mismatch before construction, never upgrade/reconstruct. Shadow
extent consumes P5's actual allocation from canonical `shadowResMul`, not an alias
`shadowQuality` key or a second multiplier in camera math. The user/pack old-light tri-state,
independent companion pair and reserved zero-only antialiasing setting follow current P3/P7
load policy; none is a new P8 parser, AA path or texture binder.

P7 derives `requested = minima.shadowDepthBuffers() > 0 || minima.shadowColorBuffers() > 0`
from the same accepted P3 `ResourceRequirements.minima()` supplied to P5 planning for this
configuration/dimension tuple. Color-only demand counts. Defaults in `ShadowPolicy`, source
rescans, registry presence and late `BufferEstateView.shadow()` queries are not demand
proxies. Freeze the boolean with policy/health before provider construction; it cannot be changed
after compilation to repair a mismatched tuple.

Validation/absence precedence is closed and deterministic. First validate structural input
(non-null input/policy/health and nested values, immutable null-free duplicate-free canonical
collections, well-formed hook rows/fingerprint); malformed structure produces
`Disabled(InvalidPolicy(field), diagnosticId)`, never `NotRequested`. P7's schema/accepted-minima
checks precede this call. Next, structurally valid `requested=false` returns `NotRequested`
without feature-value or hook-availability gating. Only `requested=true` applies the following
policy checks in listed order, then the required hook checks in hook-ID order:

- present `shadowMapFov` must be finite and strictly between 0 and 180 degrees;
- `shadowDistance` must be finite and positive;
- `shadowDistanceRenderMul`, `shadowIntervalSize`, and `sunPathRotationDegrees` must be finite;
- every mipmap buffer index must be in the v0.2 shadow inventory `{depth 0, depth 1, color 0,
  color 1}`; every PCF member must be `{depth 0, depth 1}` and color membership is invalid policy;
- every required non-cloud scalar row in §4.13 must be HEALTHY at its individual expected
  count before a real plan is enabled; a cloud-only mismatch disables clouds, not real shadows.

Invalid requested policy values are not clamped into a different pack contract. They produce a
stable diagnostic and disabled shadow feature; main continuation requires the construction-time
neutral disposition below. Ordinary structural-valid absence is `NotRequested`, not an error.

Construction follows Phase 7's §5.3 ordering, with R7-13's request adopted here:

1. Freeze the intended resolved policy, hook health and minima-derived requested boolean; build the pure plan and its celestial
   policy before constructing Phase 6's provider/runtime. `NotRequested`/`Disabled` supplies
   explicit shadow absence rather than a fabricated ready plan.
2. Phase 7 supplies the plan-independent `CelestialMath.angles` producer to the new provider, constructs the new runtime,
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
5. After P5 publication is accepted and its actual estate generation is known, but before first
   main-frame admission, P7 applies the following disposition to that same accepted tuple.
   This applies to plan/build `Disabled` and any positive-demand `NotInstalled` (including
   verification/installation gating), not just hook failure; no candidate neutralization API exists.
   First retain the exact accepted `PublishedBufferEstate` wrapper and obtain its `estate()`
   once. Empty/off publication follows existing failed-composition/off containment before any
   main bind/clear/draw; it is neither NotRequested nor coherent shadow-only Unavailable.
   Authenticate the present `BufferEstateView` against P7's accepted configuration/dimension/
   resource/registry tuple: its `generation()` equals that wrapper's accepted generation,
   its `registryFingerprint()` matches the accepted registry, and its resource evidence is
   the accepted wrapper/candidate evidence. Public value equality alone cannot establish
   accepted ownership. Retain this same view and generation; only then call its `shadow()`.
   - For `requested=false`, require `ShadowEstateNotRequested`; do not neutralize or diagnose
     ordinary absence. Available/unavailable contradicts the accepted demand tuple and contains
     admission through existing failed-composition/off recovery.
   - For `requested=true` with a ready installed slot, retain the ordinary P5 estate dispatch.
     For disabled/uninstalled disposition, inspect that authenticated `BufferEstateView.shadow()`:
     `ShadowEstateAvailable.view()` must belong to the same accepted estate generation and
     must call its existing `degradeToNeutral(acceptedEstateGeneration, EXPLICIT_FEATURE_DISABLE)`;
     `ShadowEstateUnavailable` already proves coherent neutral backing and needs no repeat call;
     `ShadowEstateNotRequested` is a tuple mismatch, not successful absence.
   - `Neutralized` and `AlreadyNeutral` must name that actual current estate generation and
     establish its unavailable/neutral disposition before admission. Preserve the returned
     diagnostic; invalidate prior binding use, never manufacture a new binding or flip. Construction
     admits no open frame/snapshot: unexpected `openSnapshotAborted=true` is a lifecycle violation
     requiring off containment, not permission to resume an interrupted frame.
   - Any rejection (including stale generation), exception, mismatched returned generation,
     missing safe-neutral proof or tuple mismatch forbids main bind/clear/draw and enters existing
     failed-composition/frame/off containment. Never retry against a replacement estate or
     substitute registry generation. Normal reverse retirement still owns all real/neutral objects.

The returned publication retains that exact registry fingerprint and the canonical SHA-256
fingerprint of `(plan.fingerprint(), registry)`. Build validation and Phase 7 precommit pairing
include both; a mismatch prevents installation and follows the existing failed-build
rollback/off path. A plan may have identical content across compilations, but a changed registry
requires a newly paired publication; never patch the pure plan with a late registry field.
At invocation, compare the borrowed published registry fingerprint with the retained construction
fingerprint and validate the plan fingerprint as well as the separate live execution/slot/registry/
estate generations. Hash equality cannot authenticate live publication ownership.
`RegistryFingerprint` excludes generation (`docs/phase4/v1/PHASE_4_DOC.md:1921`,
“It does not hash” ... “generation”); accepted-generation adoption remains Phase 7's subsequent
publication handshake, not a predicted generation or part of pure planning.

This removes the plan -> provider -> compile -> plan cycle: policy/health/demand planning precedes the
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
`Rejected(WRONG_FRAME)` before mutation. `CLOSED` never becomes ready again. Pure input identity
includes request presence, hook-health fingerprint and every complete effective policy field;
Ready plan identity includes canonical `requested=true`, not a registry. Reload derives an immutable plan from new inputs and creates a new
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
   Phase 7 must already associate `context.frame()`, `context.shadowFrame()` and
   `context.camera()` with this same live invocation and accepted post-camera capture. P8
   validates the supplied frame/execution association before computing; matrix/value equality
   alone is not authentication. `CameraSnapshot` has only modelView/projection, no identity
   fields; do not invent such fields or recover a camera from position, sun angle or current GL.
2. Retain `context.buffers()` from the accepted active tuple; obtain its `estate()` once and
   authenticate the present `BufferEstateView` and wrapper/view generation, registry and accepted
   resource/configuration/dimension pairing exactly as §4.1. Empty/off, stale or mismatched
   publication/view returns mutation-free `Rejected(STALE_PUBLICATION)` before any P5 mutation,
   state opening or GL; no successful absence is synthesized and no current estate is refetched.
   Query that same `BufferEstateView.shadow()` once:
   - `ShadowEstateNotRequested` returns `Completed` without opening state;
   - `ShadowEstateUnavailable` records its feature diagnostic and returns `Completed`, relying on
     Phase 5's coherent neutral estate disposition, not prior-frame physical bindings;
   - `ShadowEstateAvailable` continues only with its `view()` belonging to the same accepted
     wrapper/view generation; mismatch rejects before mutation. Preserve that view as `shadow`.
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
5. Sample one `ShadowWorldSample`; validate its echoed identities and camera presence, then call
   `cameraMath.compute(context.shadowFrame(), context.camera(), plan, shadowExtent)`,
   with `shadowExtent` taken from the actual available Phase 5 allocation, not recomputed sizing.
   Derive culling/traversal from that returned camera and the validated world sample. An
   absent/stale camera rejects before GL; invalid/nonfinite math input is contained through the
   existing engine-failure path, never a synthesized prior/identity camera. Any acquired snapshot
   is still aborted by cleanup. No additional compute overload or hidden camera lookup is allowed.
   Set the terminal-release obligation immediately before entering step 6, before any platform
   mutation or possible activation; every subsequent exit, including throws while opening state,
   runs step 13. Steps 1–5 may reject without release only while mutation-free and with no shadow
   activation issued by this invocation; any acquired snapshot is independently aborted.
   Step 2's absent/unavailable Completed proves no shadow activation or state scope was opened.
6. Open the `ShadowStateLease` with that camera and the already borrowed active
   `ShadowExecutionView`. It snapshots/restores §4.4's state and installs the shadow camera.
   Phase 7 remains the sole execution-bridge opener/closer around `invoke`.
7. Bind and clear the Phase 5 shadow target. A backend failure leaves the snapshot open; abort
   immediately without flips, then follow §6's neutralization/restoration containment.
8. Call §4.7's authenticated, forced exactly-once shadow `setupTerrain` (never a cache-only call),
   then send `updateCelestial(camera.celestial())` and `updateShadowMatrices`
   to the construction-bound Phase 6 runtime, using this camera's primary matrices and the same
   frame worldEpoch/frameId. The immutable celestial event is the one computed at step 5, not
   rebuilt after installing shadow FF state. These are value events, not sampler uploads.
   Take the immutable `expectedOverlay = context.texturePublication().id()` and call
   `context.textureLeases().lease(expectedOverlay,selection,context.execution().currentBaseBinding())`. `Acquired(overlay)` gives Phase 8
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
   - `Activated`: inspect returned participant degradation for `phase6.replay.delivery.failed`
     and consume P7's synchronous collector-failure prerequisite before any draw. Either failure
     latches `Failed`, aborts the open snapshot without flips and skips all draws/copies/mipmaps;
     still execute terminal release and every independent finally cleanup. Ordinary isolated
     uniform degradation without delivery/collector failure remains allowed;
   - `FixedFunction`: draw only while that same failure prerequisite is clear; no sampler uploads;
   - `Skipped`: omit the pass and abort without flips or mipmaps, then restore and return `Completed`;
   - `StalePublication`: abort and return `Failed` after restoration; no post-GL mutation-free
     rejection or stale retry;
   - `ShadersOff` or `FailedSafe`: abort and return `Failed`.
10. Execute §4.8's unchanged terrain/cloud/entity/depth-split/translucent order through the port.
    At v0.5 the approved §4.8.5 countInstances adapter repeats prepared submissions inside
    these calls, never the calls/traversal themselves; the invocation still owns one snapshot.
11. Call the typed post-pass mipmap operation. `Generated` carries canonical per-buffer outcomes;
    `Degraded` within it continues only after successful base-filter restoration. Result-level
    `Neutralized(...,true)` has already aborted without flips and invalidated the snapshot:
    stop, mark the feature disabled, skip step 12 and every additional abort/neutralization call,
    but still execute `finally`. A protocol rejection aborts the still-open snapshot and follows §6.
12. Call `completePass`. Only `Completed(frameId)` commits the recorded shadowcolor flips.
    A rejection is not success; cleanup aborts only if the snapshot is still open.
13. In the single `finally`, independently attempt each applicable cleanup even if another fails:
    - Finish/abort snapshot bookkeeping exactly once; already-neutralized/invalidated snapshots
      never receive a duplicate terminal operation. Restore required per-draw P9 IDs, P7 color,
      and P4/P10 instance/input scopes before barrier release, while their activity authority is
      still current. A failed restoration latches `Failed`, but does not skip release or later cleanup.
    - When the release obligation was set before step 6, call exactly
      `context.registry().barrier().releaseToFixedFunction(context.barrierContexts().release())`.
      This is the supplied current frame's canonical release-kind context (`shadowPass=false`),
      never the borrowed activation context, a new frame issuer, or a reselected provider.
      `FixedFunction` alone proves token invalidation, provider-lock restoration and fixed-function
      binding for continuation. `ShadersOff` requires off containment, not Completed;
      `FailedSafe` means restoration is unproven and prohibits further rendering until recovery;
      `StalePublication` performs no release and requires Failed/off containment without retrying
      a replacement registry inside this invocation. `Activated` or `Skipped` is not release proof:
      treat either as a protocol failure and return Failed. A missing/unusable barrier/context or
      thrown release call is likewise Failed, never a success synthesized from a stale token.
    - After the release attempt, close **only the binding snapshot** after Bound, including after
      completion/abort/neutralization/off/exception; before transfer close **only any acquired
      overlay lease**, including a thrown lease-consuming call. No acquired lease means no close.
      A failed close does not skip traversal, Forge-pass or shadow-state cleanup.
    - Restore Phase-8-owned traversal, Forge-pass and platform shadow-state scopes in reverse
      order, after release, attempting each independently. The state lease does not release the
      program or own the barrier. Even failed/unproven release cannot bypass best-effort platform
      restoration; no draw is admitted by that attempt.
    Never close borrowed publication/source/selection/context/execution. Phase 7's independent
    outer `finally` closes its execution view; only it owns that bridge.

Every post-acquisition exit runs applicable cleanup. Completion, invalidation and program release
never discharge binding/lease closure. `Completed` requires either the step-2 no-activation proof
or `FixedFunction` release proof, all required cleanup successful, and current borrowed execution.
This applies to normal shader/fixed-function draws, Skipped activation, lease/binding suppression,
feature neutralization and mipmap exits alike. Any failure latch overrides an earlier Completed.
`ShadersOff`/`FailedSafe`/stale activation and thrown draws always remain Failed even if later release
succeeds. Phase 7 must skip main bind/clear/draw, abort and schedule off on Failed; vanilla resumes
only after program/state recovery is proven, otherwise rendering stays contained. Preserve unrelated
vanilla throwables only after all independent cleanup and failure reporting; contain engine exceptions.

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

Phase 7's `mod.glue` uniform provider retains the copied `FrameUniformSample` and its same-sample
`skyAngle` under the accepted world/frame identity. The invocation view reuses these exact sun/sky
values and unshifted camera position; no second world/time read is allowed. The world sample must
echo this view and its current `worldEpoch`/`frameId`; `sunAngle` bit-equals the provider value.
A missing/mismatched sample is a stale-frame rejection, not a second time basis. World-section
bounds are inclusive and validated before traversal.

P7's provider calls `CelestialMath.angles(sunAngle).shadowAngle()` before main-camera capture,
including NotRequested, Disabled, NotInstalled and unavailable-estate compositions. It cannot
construct eye vectors then. P7 retains the accepted configuration's finite sunPathRotation,
same frame/sun/sky sample and actual post-`setupCameraTransform` camera association independently
of shadow installation. Real shadow dispatch calls §2.2 compute; main H-SKY-02 dispatch calls
`CelestialMath.sample(frame,mainCamera,sunPathRotationDegrees)` at the celestial-rotation moment.
The original sky-angle call still executes/returns unchanged; it does not replace the retained
time basis. A same-frame sample already produced for shadow is reused when the accepted camera
association and rotation also match; otherwise the main route computes once for that association.
No cross-frame retention, fake Ready plan, target allocation or shadow-hook success is required.
P6's engine runtime receives only its existing frame/event records: no engine-to-shadow
implementation dependency, second participant, resampling or late `shadowAngle` upload.

### 4.4 Reversible state lease

`ShadowStateLease` snapshots and restores, in a strict stack:

1. `GameSettings.thirdPersonView` (`field_74320_O`) and the render-view entity/camera basis;
2. current Forge render pass from `MinecraftForgeClient.getRenderPass()`;
3. `RenderManager.isRenderShadow()` state used during the shadow entity calls;
4. projection and model-view matrix modes/stacks;
5. viewport, framebuffer, draw buffers, color/depth masks, active texture, atlas/lightmap binding;
6. cull, blend, alpha, depth-test/depth-func, shade model, and cached `GlStateManager` state touched
   by the pass;
7. Phase 7's main `RenderGlobal.renderInfos` object, §4.7 traversal/cache/debug state and
   lossless pending-update capture, §4.8.4's exact three translucent-sort cache values, plus the shadow-execution guard; and
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

Let `s` be the frame's normalized `sunAngle` in `[0,1)`. `sample` rejects NaN, infinities and
values outside that interval with `IllegalArgumentException` before returning any result; it
does not clamp, wrap or substitute a previous value. It is total over this validated domain.
Day includes the boundary:

```text
day = s <= 0.5
shadowAngle = day ? s : s - 0.5
a = shadowAngle < 0.25 ? shadowAngle + 0.75 : shadowAngle - 0.25
theta = -2*pi*a
```

Return `ShadowCelestialAngles(day, shadowAngle, theta)` with finite `thetaRadians = theta` in
radians, not degrees. `shadowAngle` is the Phase 6 uniform value and remains in `[0,1)`; exactly
`s == 0.5` is day with `shadowAngle == 0.5`, not night/zero. The active light is the sun by day
and moon by night. Boundary tests cover `0`, `0.25`, `0.5`, the next representable value above
`0.5`, and values approaching `1`. `CelestialMath.sample` rejects null inputs, nonfinite/out-of-range
skyAngle (required `[0,1)`), invalid sunAngle, nonfinite sunPathRotation, camera/matrix/position
values or nonfinite Float3 outputs before returning an event, with `IllegalArgumentException`.
`compute` additionally validates plan/extent. P7 authenticates the current frame/main-camera
association before either route; pure math cannot establish identity from matrix equality.
worldEpoch/frameId are never calculated from angles. Provider rejection retains P6's existing
per-cell diagnostic/neutral-or-last-valid behavior, but does not authorize a mismatched shadow
invocation to proceed.

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

`compute` takes `day` and `thetaRadians` through the delegating plan policy; shadow model-view
rotation comes from `plan.policy().sunPathRotationDegrees()`. `Rz(theta)` above uses radians.
The public `CelestialMath.sample` takes day from `CelestialMath.angles(frame.sunAngle())`,
and owns the following single vector formula; P7 never duplicates it. Celestial vectors use
`gbufferModelView = mainCamera.modelView()` from the supplied P7 post-camera snapshot, not the
shadow model-view, an identity placeholder or a current GL query. `skyAngle` is `frame.skyAngle()`;
`sunPathRotation` is the finite explicit `sunPathRotationDegrees` argument. Degree rotations convert once.

```text
sunWorld  = Ry(-90 degrees) * Rz(sunPathRotation) * Rx(skyAngle*360 degrees) * (0, 100, 0, 0)
moonWorld = -sunWorld
sunPosition  = gbufferModelView * sunWorld
moonPosition = gbufferModelView * moonWorld
shadowLightPosition = day ? sunPosition : moonPosition
upPosition = gbufferModelView * Ry(-90 degrees) * (0,100,0,0)
```

All source vectors have `w=0`: translation in the main matrix contributes nothing, while its
rotation does affect their xyz components. Only xyz are submitted. Construct exactly
`CelestialSample(frame.worldEpoch(), frame.frameId(), sunPosition, moonPosition,
shadowLightPosition, upPosition)` using immutable finite `Float3` values; the Phase 6 record
shape is unchanged. Real-shadow compute stores this event in `ShadowCamera.celestial()` and sends
it before first shadow activation (§4.2); independent main H-SKY-02 calls the same pure producer.
Equal time/position with distinct valid main rotations produce different eye vectors while
shadowAngle stays equal. P7 reuses the shadow sample at main sky only for the same accepted
frame, camera association and rotation; it cannot resample time or substitute the shadow matrix.

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

The main `RenderGlobal.setupTerrain` has already run at H-FRAME-05. A different `ICamera`
does not invalidate vanilla's traversal cache: a stationary, update-free second call otherwise
skips it. The binding procedure for **both** strategies is:

1. Validate the borrowed P7 execution identity, current slot epoch, render thread and main
   setup association. Use the same render-view entity, partial ticks and captured
   `mainTerrainFrameToken`; never move the entity, change vanilla frame indices or reload the
   view-frustum estate. A changed world/view entity/render distance since main setup fails
   admission rather than restoring an obsolete estate. Reject nested/repeated shadow setup.
2. Before any mutation, capture the exact main `renderInfos` reference (do not clear/copy its
   elements into the shadow list), the dirty bit, all five `lastViewEntity*` values, the debug
   fixed helper and pending debug-capture flag, and a reusable identity snapshot of pending
   `chunksToUpdate`. Arm exception cleanup before forcing; vanilla's entered rebuild installs
   its new shadow list, so glue does not allocate a redundant replacement list.
   Keep the completed main setup's entity/position inputs unchanged, so its frustum-position
   caches, toroidal chunk positions and render-container origin do not acquire shadow values.
3. Open a narrower, non-reentrant **setup-only** guard inside the existing authenticated shadow
   invocation. Temporarily set `debugFixedClippingHelper=null` and `debugFixTerrainFrustum=false`
   so vanilla cannot replace the shadow camera, suppress traversal or capture a shadow debug
   frustum. Call the actual public `RenderGlobal.setDisplayListEntitiesDirty()`,
   SRG `func_174979_m()V`, immediately before exactly one
   `func_174970_a(Entity,D,ICamera,I,Z)V` with the shadow frustum. This sets
   `displayListEntitiesDirty` (`field_147595_R`) true; vanilla's dirty calculation preserves it,
   and the now-unfrozen rebuild branch consumes it normally. No synthetic frame token,
   retry, extra main setup or second shadow traversal is permitted.
4. Only during that exact call, H8-TRAVERSE-01 redirects all three
   `RenderChunk.func_178577_a(I)Z` call sites to the pass-local identity visited set, never
   storing any vanilla frame index. The single `Minecraft.field_175612_E:Z` read returns false
   (not a write to the user's `renderChunksMany` state); the single
   `func_174978_c(BlockPos)Set` seed query returns all six directions. Thus camera-cell
   visibility cannot collapse the queue to one chunk, and ordinary path-direction/compiled
   occlusion cannot prune shadow traversal. The existing compiled visibility redirect remains
   shadow-only, though short-circuited with this flag. Neighbor expansion still uses vanilla
   `func_181562_a(BlockPos,RenderChunk,EnumFacing)RenderChunk` and the shadow frustum.
   `SunAlignedPrism` applies its allowed-set filter there and as an ordered list postcondition;
   `FullLoadedView` has neither membership filter.
5. On setup return or throw, disarm the setup-only guard and restore all five main view-cache
   values and both debug fields in independent `finally` cleanup. Retain vanilla's new pending
   update set and completed scheduling; union the captured pending identities into the current
   set even on throw (vanilla may throw before its own final `addAll`). Never roll back chunk
   rebuild results or restore a stale pending-set reference. Restore dirty as
   `savedDirty || postSetupDirty || !currentPendingUpdates.isEmpty() || setupThrew`.
   This conservatively retains genuine invalidation; a clean successful shadow-only forcing
   does not dirty the next main frame. No cleanup clears a later dirty bit.
6. Successful setup leaves only the shadow list installed for terrain/entity drawing. At outer
   state-lease cleanup restore the **exact main list reference** in `finally`, on every terminal
   path, before P7 can admit main drawing. Setup failure restores it immediately as well;
   cleanup is idempotent. Never obtain the main list by traversing again. A failed restore or
   failed setup returns `Failed` under §6, with no shadow-list/main draw continuation.
   Terrain draws also obey §4.8.4's independent translucent-sort cache restoration. That scope
   restores only three doubles, not the scheduling queues or completed work preserved above.

The public dirty setter is the forcing operation, not a guessed field method or evidence of
runtime hook success. H8-REBUILD-01 authenticates entry into the actual rebuild branch; its
entry count must be exactly one before setup can return success. Zero/multiple entries fail
without retry. Transformation application health and this per-invocation assertion are distinct.

This is deliberately a vanilla `RenderGlobal` traversal, not Pintonium's replacement collector.
The absence of a working 1.12.2 Pintonium traversal remains a risk in §11.3.

### 4.8 Content and render order

#### 4.8.1 Opaque terrain and clouds

After barrier activation and §4.10's replay-delivery prerequisite, call the actual world-loop overload for layers in this immutable order:

1. `SOLID`;
2. `CUTOUT_MIPPED`;
3. `CUTOUT`.

Each call runs under the Phase 7 shadow-execution guard, so existing H-TERRAIN hooks do not open a
main Phase 5 snapshot. Phase 4's already-active shadow barrier remains authoritative.

If `cloudsInShadow` is true, H8-CLOUD-01-RESOLVE is HEALTHY and the resolved cloud mode
is not off, draw clouds after opaque
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

Both method calls install a private, non-reentrant entity-call guard only for their synchronous
dynamic extent, bound to this port's exact `RenderGlobal` receiver and the borrowed P7 execution/
slot epoch. Clear it in local `finally`, including throws. H-SHADOW-OUTLINE-01 redirects only
the `RenderGlobal.isRenderEntityOutlines` / `func_174985_d()Z` invocation inside
`RenderGlobal.func_180446_a(Entity,ICamera,F)V` that gates the entire outline subpass.
Return false only while that exact entity-call guard is active and P7 bridge validation returns
`Valid` for the current execution identity/slot epoch. Otherwise call the original predicate
once and preserve its result; feature availability or a global shadow boolean is not authority.
Stale execution never admits a shadow method call under the port's existing preflight.

The false result excludes the whole branch, including retained `entityOutlinesRendered=true`
with no newly glowing entities. Do not merely filter the glowing list, clear the retained field,
cancel renderEntities, replace the predicate globally, or repair the target after the branch.
No outline framebuffer clear/bind, outline postprocess/program mutation, or platform framebuffer
rebind may occur between ordinary shadow entity rendering and tile-entity traversal. The accepted
sfb and retained root-shadow activity remain continuous through both, without extra activation.
Regular entities, Forge pass predicates/cadence, fire rendering and later main-frame outlines
(including retained state) remain unchanged. The required health row gates real shadow before
invocation; a missing/overmatched guard disables shadows, not main outlines.

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

Around the actual authenticated shadow `RenderGlobal.func_174977_a(BlockRenderLayer,D,I,Entity)I`
TRANSLUCENT invocation, before it can mutate state, capture the exact receiver's doubles
`prevRenderSortX`/`field_147596_f:D`, `prevRenderSortY`/`field_147597_g:D`, and
`prevRenderSortZ`/`field_147602_h:D`. Arm cleanup before entering vanilla; call it exactly once,
then independently restore X, Y and Z to their bit-exact saved values in `finally`, on return
and throw. A failed getter prevents the call; a failed setter still attempts the other restores,
then returns `Failed(RESTORE,…)` and forbids main continuation under §6. No retry/replay.
This nested scope completes before entity pass 1 and outer lease restoration; outer cleanup
still independently restores main renderInfos and platform state on failure.

Vanilla may update these caches and schedule transparency sorting for up to fifteen eligible
chunks of the installed shadow list. Preserve every legitimately scheduled task and completed
sort; do not roll back queues, cancel work or restore a stale collection. Only the three
position caches are rolled back, so the subsequent original main translucent call still sees
the moved-camera threshold and schedules a main-only eligible chunk absent from the shadow prism.
When shadowTranslucent is false no translucent invocation or sort-cache scope is entered.

#### 4.8.5 Prepared shadow submissions (v0.5)

The maintainer's 2026-09-07 **Repeat prepared submissions** decision is adopted here. P4 §11.5
records the author documentation, bounded licensed-source checks and explicit choice; none of
those sources is misrepresented as proving the target's ordering. For count N from the effective
root-shadow selection, each prepared native geometry submission is issued adjacently N times
with `instanceId=0..N-1`. A then B yields A0…A(N−1), B0…B(N−1), not repeated world traversal.

P7 owns policy and private synchronous mod-side adaptation through P10's existing draw hooks.
P8 continues to supply the same live Valid shadow execution, exact root selection/context,
frame and publication; the adapter revalidates those credentials and current program activity
before copies. No main gbuffers scope/snapshot/selection is opened inside shadow. Retained
metadata, equal fingerprints or an expired execution never authorize a draw.

VBO/client-array pointer setup and build/upload/reset occur once; only final native submission
repeats before teardown. Geometry display lists compile once without count expansion or instance
uploads and repeat at playback, under a private dynamic-extent guard that excludes nested lower
wrappers. Replay-stable geometry/program/state must be established by P10's vanilla hook coverage;
this is not permission to adopt a replacement renderer or fabricate a public renderer API.
Only instanceId varies between copies; P6's existing event supplies it. Nested finally restores
the predecessor, outermost zero, before shadow release, alongside existing ID/color restoration.

All terrain/cloud/layer calls, Forge pass predicates and entity/TE callbacks, counters, scheduling,
depth split, clear, flips and mipmaps retain §4.8's original cadence. In particular pass0 remains
before the single split and pass1 after optional translucents. On stale pre-mutation admission,
draw nothing. On failure after any copy, stop remaining copies, restore in finally, abort the
open snapshot without flips and use §§4.2/6's existing neutralization/Failed containment; never
restart copies or return partial success or a mutation-free Rejected after mutation. Existing
P6 per-uniform isolation remains unchanged. This adds v0.5 execution beneath the v0.2 traversal,
not a changed v0.2 exit milestone or a second shadow invocation.
Every immediate instance/ID/color/atlas event and restoration observes §4.10's delivery-failure
prerequisite before another native copy or shadow draw; per-uniform isolation cannot mask it.

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
current Phase 5 §5 semantics (`docs/phase5/v1/PHASE_5_DOC.md:2249-2253`, `:2724`), not a new Phase 8 GL path.

Hardware PCF is not toggled per frame. Phase 5 already applies per-depth compare mode and the
legacy depth swizzle during candidate creation
(`docs/phase5/v1/PHASE_5_DOC.md:2111-2115`). Phase 8 checks the resulting estate disposition:
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
The celestial event is exactly the returned `ShadowCamera.celestial()`; its identity comes from
the accepted invocation frame and its eye vectors from the supplied main-camera snapshot.
Installing/restoring FF shadow state cannot alter the copied main snapshot or justify recomputation.

Phase 7 issues `activation(shadowStep,true)` and selects root `shadow` once before invocation.
Phase 8 consumes its exact borrowed selection/context, authenticates them, and passes that same
selection to `beginPass` and `UseProgramRequest`. Phase 4 validates the shadow stage/band relation
and applies force-shadow before the sole fallback resolution
(Phase 4 §4.10). The `shadowPass=true` activation interval ends through the explicit §4.2 step-13
release after per-draw restoration and before fixed-function/platform state restoration.
Only its `FixedFunction` result authorizes successful continuation; all other release outcomes
use §4.2 Failed/off containment. The slot never fabricates a context, reselects or calls `use`.

P7 §5.1's already-binding synchronous replay receiver is a prerequisite, not a new API or finding:
after `Activated`, inspect participant degradations for stable `phase6.replay.delivery.failed`
(scope `"uniform replay evidence delivery"`) and honor P7's latched collector failure before
the first or any subsequent shadow draw. Either forbids draw despite an otherwise Activated
barrier. Immediate event `IllegalStateException` with that ID, including instance/ID/color/atlas
updates and restoration, has the same Failed/off consequence before another draw. Do not wait
for frame end or add a P8 collector/getter. Ordinary isolated uniform failures still degrade
locally when delivery succeeded. Abort the still-open snapshot without flips, stop later draws,
copies and mipmaps, and return Failed after terminal release and independent closure/restoration;
successful release cannot clear the failure latch. Non-upload final restoration remains required.

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

**D-P8-39 — actual-base refresh during retained root shadow.** Before any shadow draw, P8
registers once through `context.execution().installBaseBindingReceiver(receiver)`, accepting
only `SignalResult.Accepted`; rejected/failed installation suppresses drawing and contains under
§4.2 cleanup. The P7-owned `ShadowBaseBindingReceiver` method is exactly
`SignalResult refreshBaseBinding(AtlasBindingEvidence evidence)`. Registration is invocation-local,
removed by P7 execution close, and cannot retain the invocation after return.
P7 authenticates actual external base-bind/restoration evidence and dispatches to this receiver
synchronously before the next native draw, also for transitions inside vanilla terrain/entities.

The receiver closes the previous binding snapshot (ownership/lease release only, no native bind
or base restoration), obtains `execution.currentBaseBinding()` and requires the triggering actual
object/serial still current. It then acquires
`textureLeases.lease(expectedOverlay,retainedSelection,currentEvidence)` and reruns the same
five-argument `shadowBindings` on the **same open shadow snapshot**, generation and frame.
Only Bound transfers the new lease and installs the new binding, returning Accepted without
select/use/activation, snapshot completion, flip, traversal replay or depth recopy. The retained
root-shadow activity and fixed sampler integers remain unchanged. P5 preflights the full rows,
resolves the accepted winning base association, and alone binds compatible objects.
Rejected/Degraded closes the still-caller-owned lease, suppresses the undrawn remainder and
aborts without flips/mipmaps; return Rejected and latch no-next-draw. BackendFailed, close/lease/
refresh throws or observation failure return Failed with ordinary shadow neutralization/off
containment. Neither outcome permits a later native draw; independent abort, lease closure,
terminal release and platform restoration still execute. Already-closed old bindings are not
closed twice. No stale-evidence retry or prior atlas fallback is allowed.

P5 batches its own successful physical bind observations and commits the resulting serial/lease
and atlasSize atomically; its expected echo does not recursively refresh. External transitions
are never hidden by that batching. Actual nested/platform restoration belongs its existing owner,
emits new evidence/serial and follows the same admission rules; binding.close never rewinds it.

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

The table extends Appendix E's exact format as a **group ledger**, not a wire-row table.
`ACCESSOR` exposes a field/method to glue but contains no policy. Every injection uses SRG
name plus descriptor and `require=0`; each independently selected anchor has `expect=1`.
The three visit sites are separately audited, never one expect=3 success counter.
The nine H8 group labels below are not `ShadowHookRow.hookId` values. The added
H-SHADOW-OUTLINE-01 entry is itself one scalar ID. Only §4.13.1's complete 66 flattened IDs
cross the boundary; group counts never replace wire rows.

| ID | Class (readable) | Obf | SRG target / descriptor / style | Purpose and health |
|---|---|---|---|---|
| H8-SLOT-01 | `EntityRenderer` | `buq` | consume Phase 7 H-FRAME-05 after `RenderGlobal.func_174970_a(Entity,D,ICamera,I,Z)V`; no new injection | exact invocation before the main-estate rebind; `CORE` through Phase 7 |
| H8-TRAVERSE-01 | `RenderGlobal` | `buy` | within `func_174970_a(Entity,D,ICamera,I,Z)V`: three separately selected `RenderChunk.func_178577_a(I)Z` sites (seed, fallback seed, neighbor); one neighbor `func_181562_a(BlockPos,RenderChunk,EnumFacing)RenderChunk`; one `CompiledChunk.func_178495_a(EnumFacing,EnumFacing)Z`; one GETFIELD `Minecraft.field_175612_E:Z`; one `func_174978_c(BlockPos)Set` | §4.7 setup-only authentication; seven individual scalar anchors, expected `(1,1,1,1,1,1,1)`; former `(3,1,1,1,1)` group summary is not wire evidence; mismatch disables Phase 8 |
| H8-RESTORE-01 | `RenderGlobal` | `buy` | getter/setter accessors for `field_72755_R` (renderInfos), `field_147595_R` (dirty), five view doubles `field_174997_H`, `field_174998_I`, `field_174999_J`, `field_175000_K`, `field_174994_L`, debug `field_175001_U`/`field_175002_T`; getters for `field_175008_n`/`field_175009_l`; three sort-double getter/setter pairs `field_147596_f:D`, `field_147597_g:D`, `field_147602_h:D` | existing 31 observations plus six independently target-bound sort GET/SET observations, no added RESOLVE rows; exact §§4.7/4.8.4 restoration, lossless scheduling; FEATURE mismatch disables Phase 8 |
| H8-REBUILD-01 | `RenderGlobal` | `buy` | public `func_174979_m()V` immediately before glue's single `func_174970_a(Entity,D,ICamera,I,Z)V`; one injection at rebuild-branch entry, before its `field_147595_R:Z=false` store, excluding other dirty stores | actual mapped forcing setter plus authenticated entry witness; expected setter resolution 1 and branch anchor 1, `FEATURE`, missing/ambiguous disables Phase 8; entry witness never forces a retry |
| H8-TERRAIN-01 | `RenderGlobal` | `buy` | invoke `func_174977_a(BlockRenderLayer,D,I,Entity)I` for the four ordered layers; consume Phase 7's R8-1 shadow-execution guard | ordinary vanilla layer draw, no class replacement; `FEATURE` |
| H8-ENTITY-01 | `RenderGlobal` | `buy` | invoke `func_180446_a(Entity,ICamera,F)V` under Forge pass 0/1; getter/setter `@Accessor`s for `field_72740_G`, `field_72748_H`, `field_72749_I`, and `field_72750_J` | pass-aware entities and tile entities without consuming main startup/debug counters; `FEATURE` |
| H8-CLOUD-01 | `RenderGlobal` | `buy` | invoke `func_180447_b(F,I,D,D,D)V` only when configured | configured cloud caster; `FEATURE`, failure disables clouds only |
| H8-BLOB-01 | `Render` | `bzg` | in `func_76979_b(Entity,D,D,D,F,F)V`, redirect only `func_76975_c(Entity,D,D,D,F,F)V` | suppress blob, retain fire; `FEATURE`, missing disables Phase 8 |
| H8-FORGE-01 | `ForgeHooksClient` / `MinecraftForgeClient` | n/a | public `setRenderPass(I)V` / `getRenderPass()I`; no Mixin | verified Cleanroom pass interop; restore exact prior value |
| H-SHADOW-OUTLINE-01 | `RenderGlobal` | `buy` | invocation redirect in `func_180446_a(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V`, selecting `Lnet/minecraft/client/renderer/RenderGlobal;func_174985_d()Z` (`isRenderEntityOutlines`), the unique outer outline-subpass predicate invocation; `require=0`, `expect=1` | §4.8.2 exact receiver/entity-call guard plus Valid P7 execution/slot epoch; false only there, original predicate otherwise; independently counted applied transformation, `FEATURE`, missing/overmatch disables real shadows |

H8-TRAVERSE-01/H8-REBUILD-01 require both the borrowed Phase 7 execution identity/current
slot epoch and §4.7's exact setup-only guard, not merely any active shadow draw. Other calls
retain vanilla behavior. H8-RESTORE-01 borrows state only within `invoke`; no vanilla
collection is retained across frames. §4.13.1 defines the complete ordered scalar fingerprint
inputs, member/accessor coverage and selected branch identity; old three-redirect/getter-only
evidence cannot certify this surface.

#### 4.13.1 Canonical flattened scalar health catalogue — D-P8-31/D-P8-35

`ShadowHookRow(String hookId,int expected,int actual,HookDisposition disposition)` remains
unchanged. The following table is the entire dense catalogue, in ascending ASCII hook-ID order.
Every row has `expected=1`. `actual` is a nonnegative audit observation count for **only**
the named target: successfully applied exact-anchor transformations for redirect/entry rows;
resolved exact mapped members for RESOLVE rows; successfully generated, target-bound accessors
for GET/SET rows. SLOT copies the actual P7 H-FRAME-05 anchor application count, not a
completed-frame or slot-installed boolean. Resolution is not a runtime invocation count.
GET and SET observations are independent of the corresponding field RESOLVE observation.
The three sort-cache fields add GET/SET only: each actual count independently records successful
generation bound to the exact mapped double target; failed resolution gives that accessor zero.
There is no extra RESOLVE row for those fields and no six-accessor aggregate observation.

| Stable wire hookId | Independently counted target |
|---|---|
| H-SHADOW-OUTLINE-01 | exact `RenderGlobal.func_174985_d()Z` invocation redirect in `func_180446_a(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V`, selected as the unique outer outline-subpass predicate in §4.13 |
| H8-BLOB-01-REDIRECT | blob-only invocation redirect in the ledger's `func_76979_b` |
| H8-CLOUD-01-RESOLVE | `RenderGlobal.func_180447_b(F,I,D,D,D)V` resolution |
| H8-ENTITY-01-FIELD-72740-G-GET | `field_72740_G:I` getter |
| H8-ENTITY-01-FIELD-72740-G-RESOLVE | `field_72740_G:I` resolution |
| H8-ENTITY-01-FIELD-72740-G-SET | `field_72740_G:I` setter |
| H8-ENTITY-01-FIELD-72748-H-GET | `field_72748_H:I` getter |
| H8-ENTITY-01-FIELD-72748-H-RESOLVE | `field_72748_H:I` resolution |
| H8-ENTITY-01-FIELD-72748-H-SET | `field_72748_H:I` setter |
| H8-ENTITY-01-FIELD-72749-I-GET | `field_72749_I:I` getter |
| H8-ENTITY-01-FIELD-72749-I-RESOLVE | `field_72749_I:I` resolution |
| H8-ENTITY-01-FIELD-72749-I-SET | `field_72749_I:I` setter |
| H8-ENTITY-01-FIELD-72750-J-GET | `field_72750_J:I` getter |
| H8-ENTITY-01-FIELD-72750-J-RESOLVE | `field_72750_J:I` resolution |
| H8-ENTITY-01-FIELD-72750-J-SET | `field_72750_J:I` setter |
| H8-ENTITY-01-METHOD-RESOLVE | `RenderGlobal.func_180446_a(Entity,ICamera,F)V` resolution |
| H8-FORGE-01-GET-RESOLVE | `MinecraftForgeClient.getRenderPass()I` resolution |
| H8-FORGE-01-SET-RESOLVE | `ForgeHooksClient.setRenderPass(I)V` resolution |
| H8-REBUILD-01-ENTRY | selected rebuild-branch entry injection before its dirty=false store |
| H8-REBUILD-01-SETTER-RESOLVE | public `RenderGlobal.func_174979_m()V` forcing setter resolution |
| H8-RESTORE-01-FIELD-147595-R-GET | `field_147595_R:Z` getter |
| H8-RESTORE-01-FIELD-147595-R-RESOLVE | `field_147595_R:Z` resolution |
| H8-RESTORE-01-FIELD-147595-R-SET | `field_147595_R:Z` setter |
| H8-RESTORE-01-FIELD-147596-F-GET | `RenderGlobal.field_147596_f:D` (`prevRenderSortX`) getter |
| H8-RESTORE-01-FIELD-147596-F-SET | `RenderGlobal.field_147596_f:D` (`prevRenderSortX`) setter |
| H8-RESTORE-01-FIELD-147597-G-GET | `RenderGlobal.field_147597_g:D` (`prevRenderSortY`) getter |
| H8-RESTORE-01-FIELD-147597-G-SET | `RenderGlobal.field_147597_g:D` (`prevRenderSortY`) setter |
| H8-RESTORE-01-FIELD-147602-H-GET | `RenderGlobal.field_147602_h:D` (`prevRenderSortZ`) getter |
| H8-RESTORE-01-FIELD-147602-H-SET | `RenderGlobal.field_147602_h:D` (`prevRenderSortZ`) setter |
| H8-RESTORE-01-FIELD-174994-L-GET | `field_174994_L:D` getter |
| H8-RESTORE-01-FIELD-174994-L-RESOLVE | `field_174994_L:D` resolution |
| H8-RESTORE-01-FIELD-174994-L-SET | `field_174994_L:D` setter |
| H8-RESTORE-01-FIELD-174997-H-GET | `field_174997_H:D` getter |
| H8-RESTORE-01-FIELD-174997-H-RESOLVE | `field_174997_H:D` resolution |
| H8-RESTORE-01-FIELD-174997-H-SET | `field_174997_H:D` setter |
| H8-RESTORE-01-FIELD-174998-I-GET | `field_174998_I:D` getter |
| H8-RESTORE-01-FIELD-174998-I-RESOLVE | `field_174998_I:D` resolution |
| H8-RESTORE-01-FIELD-174998-I-SET | `field_174998_I:D` setter |
| H8-RESTORE-01-FIELD-174999-J-GET | `field_174999_J:D` getter |
| H8-RESTORE-01-FIELD-174999-J-RESOLVE | `field_174999_J:D` resolution |
| H8-RESTORE-01-FIELD-174999-J-SET | `field_174999_J:D` setter |
| H8-RESTORE-01-FIELD-175000-K-GET | `field_175000_K:D` getter |
| H8-RESTORE-01-FIELD-175000-K-RESOLVE | `field_175000_K:D` resolution |
| H8-RESTORE-01-FIELD-175000-K-SET | `field_175000_K:D` setter |
| H8-RESTORE-01-FIELD-175001-U-GET | `field_175001_U:ClippingHelper` getter |
| H8-RESTORE-01-FIELD-175001-U-RESOLVE | `field_175001_U:ClippingHelper` resolution |
| H8-RESTORE-01-FIELD-175001-U-SET | `field_175001_U:ClippingHelper` setter |
| H8-RESTORE-01-FIELD-175002-T-GET | `field_175002_T:Z` getter |
| H8-RESTORE-01-FIELD-175002-T-RESOLVE | `field_175002_T:Z` resolution |
| H8-RESTORE-01-FIELD-175002-T-SET | `field_175002_T:Z` setter |
| H8-RESTORE-01-FIELD-175008-N-GET | `field_175008_n:ViewFrustum` getter |
| H8-RESTORE-01-FIELD-175008-N-RESOLVE | `field_175008_n:ViewFrustum` resolution |
| H8-RESTORE-01-FIELD-175009-L-GET | `field_175009_l:Set` getter |
| H8-RESTORE-01-FIELD-175009-L-RESOLVE | `field_175009_l:Set` resolution |
| H8-RESTORE-01-FIELD-72755-R-GET | `field_72755_R:List` getter |
| H8-RESTORE-01-FIELD-72755-R-RESOLVE | `field_72755_R:List` resolution |
| H8-RESTORE-01-FIELD-72755-R-SET | `field_72755_R:List` setter |
| H8-SLOT-01-FRAME-05 | copied P7 H-FRAME-05 application observation |
| H8-TERRAIN-01-RESOLVE | `RenderGlobal.func_174977_a(BlockRenderLayer,D,I,Entity)I` resolution |
| H8-TRAVERSE-01-COMPILED-VISIBILITY | exact `CompiledChunk.func_178495_a` invocation redirect |
| H8-TRAVERSE-01-NEIGHBOR | exact `RenderGlobal.func_181562_a` invocation redirect |
| H8-TRAVERSE-01-RENDER-CHUNKS-MANY | exact `Minecraft.field_175612_E:Z` GETFIELD redirect |
| H8-TRAVERSE-01-SEED-DIRECTIONS | exact `RenderGlobal.func_174978_c` invocation redirect |
| H8-TRAVERSE-01-VISIT-FALLBACK-SEED | fallback-seed `RenderChunk.func_178577_a` redirect |
| H8-TRAVERSE-01-VISIT-NEIGHBOR | neighbor `RenderChunk.func_178577_a` redirect |
| H8-TRAVERSE-01-VISIT-SEED | camera-cell seed `RenderChunk.func_178577_a` redirect |

Group cardinalities are OUTLINE=1, BLOB=1, CLOUD=1, ENTITY=13, FORGE=2, REBUILD=2, RESTORE=37,
SLOT=1, TERRAIN=1, TRAVERSE=7, total 66. They describe the complete table, not replacement rows.
All rows remain present when a feature is disabled or clouds are unrequested. An unobserved/
failed target has actual=0; overmatch preserves the actual count, never clamps to one.
For each row, HEALTHY iff actual==expected; otherwise FEATURE_DISABLED. No aggregate
disposition is copied over healthy rows. `shadowEnabled` is true iff all 65 non-CLOUD rows
are HEALTHY; cloud availability additionally requires the CLOUD row HEALTHY and requested
cloud policy. A CLOUD mismatch alone suppresses clouds. A SLOT failure also retains P7's
independent CORE containment; this projection cannot downgrade it to shadow-only recovery.

Missing/duplicate/unknown/group IDs, negative counts, wrong expected counts, out-of-order rows,
inconsistent dispositions/aggregate or fingerprint are malformed input, not a sparse success
report. The audit publishes all observations before freezing; receivers reject malformed evidence,
never sort, fill missing rows, sum counts, replace expected values or infer success from rendering.

Fingerprint preimage is UTF-8 text, LF-terminated, beginning `ShadowHookHealth/flattened-v3\n`,
then one `hookId|expected|actual|disposition\n` line per catalogue row (base-10 counts with no
sign/leading zeros, exact enum names), then `shadowEnabled=true\n` or `shadowEnabled=false\n`.
SHA-256 yields lowercase 64-hex `canonicalSha256`. The domain commits to this exact catalogue,
mapped descriptors, accessor roles and structural branch/site selectors; any change to those
meanings requires a new domain and coordinated receiver cutover, never same-ID reinterpretation.
Every actual observation therefore crosses the boundary and affects identity independently.
P7/P2 preserve the owner's fingerprint and may validate this preimage; no hidden vector exists.

The §4.7 runtime rebuild-entry witness is a separate invocation-local count: exactly one is
required before setup success. It never overwrites H8-REBUILD-01-ENTRY's frozen transformation
count. Reached visit sites or short-circuited compiled visibility likewise do not modify health.

---

## 5. Cross-phase interfaces

### 5.1 Interfaces exposed by Phase 8

| Exposed contract | Exact content | Consumer(s) |
|---|---|---|
| `ShadowPlanFactory`, `ShadowPlanInput`, `ShadowPlanResult`, `ShadowPlan`, `ShadowPolicy` | exact §2.2 shapes and §4.1 precedence: input `(ShadowPolicy policy,ShadowHookHealth hookHealth,boolean requested)`; P7 derives requested from the same accepted P3 shadow minima as P5 before provider construction; structural triple/cache identity and Ready hash including true; no registry, parser, MC, GL or handle | Phase 7 pipeline construction; Phase 2 headless tests |
| `ShadowCelestialPolicy`, `ShadowCelestialAngles` | exact `sample(float sunAngle) -> ShadowCelestialAngles(boolean day,float shadowAngle,double thetaRadians)`; §4.5.1 finite normalized-input rejection, day boundary and radians; pure angular output only, no world/frame/camera state | P7-owned `mod.glue` Phase 6 provider and P8 camera math |
| `CelestialMath` | public static `angles(float sunAngle) -> ShadowCelestialAngles`; public static `sample(ShadowFrameView frame,CameraSnapshot mainCamera,float sunPathRotationDegrees) -> CelestialSample`; §§4.3/4.5 single formulas and rejection rules, no construction or shadow availability prerequisite; P7 authenticates accepted frame/camera/rotation and owns same-association reuse | P7 pre-camera provider/main H-SKY-02; P8 compute; P2 fixtures; unchanged P6 event sink |
| `ShadowPassFactory`, `ShadowPassBuildResult`, `ShadowPassPublication` | exact `create(ShadowPlan plan,RegistryFingerprint registry,UniformRuntime uniforms,ShadowWorldPort world,DiagnosticReporter diagnostics) -> ShadowPassBuildResult`; final new registry immediately after plan; §4.1 validation and combined publication fingerprint; one generation-scoped owner exposing Phase 7's slot and idempotent render-thread close; READY/DISABLED_RUNTIME close invalidates epoch/releases services, INVOKING rejects | Phase 7 |
| `ShadowWorldPort` and closed world/state/terrain/draw results | loader-neutral shapes unchanged; §4.7 is incorporated: authenticated public dirty-setter forcing, one witnessed shadow rebuild, exact main-list/cache/debug restoration, lossless scheduling/dirtiness, exception containment and no replay; every borrowed execution validated | `mod.glue.shadow`; P7 execution bridge; recorded tests |
| `ShadowCameraMath`, `ShadowCamera`, `ShadowFrustum`, `ShadowTraversalPlan` | exact `compute(ShadowFrameView frame,CameraSnapshot mainCamera,ShadowPlan plan,Extent2i shadowExtent) -> ShadowCamera`; §4.5 uses plan policy/celestialPolicy, actual main modelView and frame identity/skyAngle for unchanged P6 event; deterministic column-major math, finite planes, total AABB predicate and full/prism traversal | Phase 8 runtime; Phase 2 fixtures |
| `ShadowHookHealth`, Phase-8 hook rows | unchanged scalar §2.2 record; exact dense 66-row ASCII-ordered §4.13.1 catalogue including H-SHADOW-OUTLINE-01 first and six independent sort-cache GET/SET rows, expected=1/actual/disposition, all 65 non-CLOUD required for shadowEnabled and `ShadowHookHealth/flattened-v3` fingerprint | P7 copies/freeze-validates unchanged into HookApplicationReport; P2 validates/preserves the same dense projection |
| implementation of Phase 7 `ShadowInvocationSlot` | §4.2's full R7-12 transaction: supplied same selection/context and expected-publication lease, five-argument physical binding, four result branches, Bound-only transfer and exactly-one finally closure; no retained invocation values; returns before the main-estate rebind (P7 D-P7-76; no second clear); real slot remains gated under §5.5 | Phase 7 frame driver |
| Shadow-aware ID/color/atlas integration | §4.8.2 alternate authenticated P7 IdScopeAdmission for P9 entity/TE tokens, independent v0.1 P7 color stack, and §4.10 actual-bind P7→P13→P6 atlas update; no main gbuffers snapshot/program and no second texture binder | P7/P9/P13 glue |
| Prepared shadow submissions | §4.8.5 incorporates P4 §11.5's approved N adjacent native submissions/IDs0..N-1, P7 policy/P10 existing private adapters, authenticated shadow-only admission, nested restoration and failure containment; one unchanged P8 traversal/snapshot/depth split | P7/P10 adapters; P6 existing instance event |
| Shadow-only outline exclusion | §4.8.2 and §4.13 exact invocation/descriptor/selector and private entity-call guard with Valid P7 execution/slot epoch; false only for the exact current shadow call, original predicate otherwise; suppress entire outline branch including retained entityOutlinesRendered before any outline FBO/program mutation ahead of TE traversal; continuous sfb/root-shadow activity, ordinary entities/Forge/fire/main outlines unchanged | P7 guarded entity adapter; P7/P2 complete health projection |

Phase 8 exposes no GL handle, framebuffer name, program handle, parsed source, mutable vanilla
collection, or physical shadowcolor side.

The complete §2.2 plan/factory/publication shapes and §4.1 construction, identity, validation,
failure and lifecycle rules are incorporated into this binding §5 interface. R7-13 at
`docs/phase7/v1/PHASE_7_DOC.md:4063-4069` is granted by this owner: pure planning before the
provider; final-registry `create` after compile/compose. `ShadowPlanFingerprint` excludes registry;
the retained publication fingerprint hashes the ordered canonical pair `(plan.fingerprint(),
registry)`. Phase 7 checks the supplied registry against its final candidate and intended tuple
before installation; Phase 8 checks it against the invocation's registry before any Phase 5/GL
work. No old fingerprint, implicit registry lookup, late plan mutation or plan-only stale check
is permitted. Live generation/slot authentication remains mandatory even when content hashes
match. This grant closes the design cycle, not §5.5's implementation/verification gates.

**R5-1 owner grant — D-P8-22 (2026-09-08; changed §5, unverified).** P8 grants exactly the
angular record, delegating policy and compute signature above, extended by D-P8-38's public
plan-independent `CelestialMath` entry points. P7 wires `angles` into its provider before camera
capture, retains the same sun/sky sample and passes the actual post-camera invocation context.
P7 validates frame/view/camera association using its live invocation state, not new camera
identity fields. P8 constructs the immutable current-frame event once and sends it before
activation under §§4.2/4.5.4/4.10. No P6 event enlargement or P6 engine dependency on shadow
implementation is granted. Coordinated receiver adoption and fresh whole-document P8/P7
verification are required; prior grants/reviews do not accept this replacement §5 automatically.

**R8-1 owner grant — D-P8-29 (2026-09-08; changed §5, unverified).** The world port must
execute §§4.4/4.7 rather than treating a returned second setup as proof of traversal. P7 retains
its sole execution bridge and main frame token, receives the narrower authenticated setup guard,
and cannot clear/bind/draw main until the original list and cache state are restored. It must
receive all 66 §4.13.1 scalar rows, including outline, separate visit sites, six sort GET/SET rows,
and forcing-setter/rebuild-entry observations. P7 freezes/copies exact owner IDs, expected/actual
counts, dispositions, aggregate and fingerprint unchanged into its existing scalar nested report;
P2 requires that same dense catalogue and validates order/count/disposition/aggregate/fingerprint.
Neither receiver imposes an eight/nine-row cap, transmits group IDs, invents child/vector fields,
sums compensating failures, fills missing rows, or infers health from completed rendering.
The per-invocation rebuild witness remains separate from frozen application health. Changed
P7/P2 receiving contracts and fresh owner/receiver review are required under D-P8-31/35.

**R11 C1 / P7 R43 C1 owner grants — D-P8-37/38 (changed §5, unverified).**
The world-port interface incorporates §4.8.4's exact receiver-bound three-double finally
restoration, preserving scheduled work and forbidding replay/main continuation after failed
restoration. P7/P2 receive all 66 rows/65 non-CLOUD/flattened-v3 without inferred counts.
P7 owns both real-shadow preactivation delivery and main H-SKY-02 delivery through the public
CelestialMath route, including absent/disabled/unavailable shadow compositions. P6's record,
event validation, upload cadence and retirement stay unchanged; no P6 shadow implementation
dependency, new participant or synthetic shadow matrix event accompanies main-only celestial data.

### 5.2 Phase 4 contracts consumed

| Phase 4 §5 contract | Use |
|---|---|
| `StageRegistry`, `StageId`, `StageBand`, `StageStep`, `PassDescriptor` | locate/validate the contained SHADOW pass against the supplied selection; Phase 7 alone issues the activation context |
| `ProgramSlotId`, `ProgramStateBundle` | root `shadow` identity and complete effective state; no child overlay |
| `ProgramBindingSelection`, `ProgramBindingSelections.validateSelection`, closed validation results | supplied opaque private credential and its effective descriptor/layout; identical selection in begin/bind/activate; no Phase 8 fallback resolution |
| `PublishedProgramStateBarrier`, `FrameBarrierContexts`, `BarrierContext`, `UseProgramRequest(selection,context)`, closed `BarrierResult` | activate only after Bound with supplied context; terminal `releaseToFixedFunction(context.barrierContexts().release())` after per-draw restoration and before platform restoration; §4.2's exhaustive results/independent cleanup are binding |
| `RegistryFingerprint` / generation | final construction/publication pairing and invocation stale rejection; deterministic content fingerprint is separate from live generation and absent from pure planning |

These are the incorporated contracts at `docs/phase4/v1/PHASE_4_DOC.md:1782-1796`, with exact
selection accessors/validation/request shapes at `:700-750` (`validateSelection` at `:728-729`) and
authentication/activation/lifetime at `:1740-1800`. Phase 8 never calls the compiler, publisher, program lookup service, selector or
fallback resolver. The newly changed owner surface requires fresh verification before implementation.

**D-P8-25 — P4 R32 receiving contract (2026-09-08).** Activation closes the prior P1
`AlphaBlendOverride`, binds the selected program, acquires the effective provider's new
alpha/blend lease, then runs sampler/built-in/custom participants. P1 enforces held aspects
against ordinary setters through its guarded backend and P7 hooks; P8 never owns a second
lock or attempts to enforce it with immediate setters. Effective blend reaches P6 through
the P1/P7 event path before built-ins. Nested transitions close/reacquire rather than snapshot
the previous override. The existing §4.2 terminal release closes that lease before platform
restoration; acquisition/notification/close failure cannot prove safe continuation.
**D-P8-36:** carry P4 D-P4-43's current `RegistryFingerprint/profile-selection-v3` identity
opaquely, committing accepted selection/evaluated state upstream. This supersedes D-P8-25's
positional-route-v2 domain only; no local parser, rehash or older-domain compatibility.

### 5.3 Phase 5 contracts consumed

**D-P8-34 synchronous value receipt:** P1 D-P1-63/P5 D-P5-42 close the existing
target-bearing allocation/upload values. P5 still owns the shadow copy operation and
policy; its 2D source region is `TextureRegion(srcX,srcY,0,width,height,1)` and destination
origin0/level0. First-copy exact-format definition and steady-copy storage preservation remain
distinct. P8 adds no allocator, direct GL, reinterpretation of depth layout or copy retry.

| Phase 5 §5 contract | Use |
|---|---|
| `PublishedBufferEstate.estate()`, `generation()`, `resources()` and `BufferEstateView`/inventory/sizing | obtain accepted wrapper's present view once; authenticate same tuple, wrapper/view generation, registry and resource evidence before dispatch; authoritative extent/inventory; empty/off/mismatch contains before admission under §§4.1/4.2 |
| `BufferEstateView.shadow()` | only after that authentication: closed not-requested, unavailable/neutral, or available result; preserve `ShadowEstateAvailable.view()` and accepted generation; no wrapper forwarding member or P5 API extension |
| `ShadowEstateView.beginPass(long frameId,PassDescriptor pass,ProgramBindingSelection selection)` | acquire one authenticated snapshot with the exact supplied selection; no descriptor-only substitute |
| `ShadowPassSnapshot` | exact fields: estateGeneration, depthAttachmentEpoch, frameId, pass, selection, framebuffer, colorAttachments, readableTextures, flipAfterPass; freeze ordinary **and** shadow readable sides at acquisition, never consult later live main sides |
| `bind/clear/copyDepth/completePass/abortPass`, `ShadowProtocolRejection` and closed results | existing target transaction, single split, complete-only flips and abort/full-clear semantics |
| `TextureBindingResult shadowBindings(long generation,long frameId,ShadowPassSnapshot snapshot,TextureOverlayLease overlay,TextureOverlayPublicationId expectedOverlay)` | full shared physical bind operation below; supersedes R8-2's four-row borrowed view |
| `TextureOverlayLease`, `TextureOverlayPublicationId`, `TextureBindingSnapshot`, rows/outcomes/purpose and diagnostic types | Phase-5-owned shared schemas; Bound alone transfers closure to the returned binding snapshot |
| `FixedSamplerPolicies` and complete App B.3 policy | sole compatible-object/name/unit authority; Phase 8 neither copies the map nor binds handles |
| `ShadowMipmapPolicy`, `generateShadowMipmaps`, `ShadowMipmapResult`/outcomes | canonical logical-buffer post-pass generation and base-filter restoration, including already-contained result-level `Neutralized` |
| `degradeToNeutral`, `ShadowNeutralReason`, `ShadowNeutralizationResult` | generation-checked coherent unavailable/neutral transition and idempotence; §4.1 accepted-estate EXPLICIT_FEATURE_DISABLE before admission and §4.9/§6 runtime containment |
| D-P5-34 neutral sampling backings | §4.10 full compatible `TextureParameters` policy cache, at most two depth and two color objects, initialized before readiness and retained through creation failure/neutralization; no P8 bind-time parameter mutation or P14 prerequisite |
| Actual-base association/refresh — D-P8-39 | P5 BaseAtlasContext Atlas(AtlasId)/NonAtlas/Unavailable and overlay baseAtlasContext(), baseTexture(), atlasContext(TextureHandleRef); compatible custom-first selection, then only exact accepted-base matching companion, otherwise explicit DefaultFill(kind); retained-root refresh in §4.10 uses unchanged shadowBindings and result/closure ownership |

The exact Phase-5-owned signatures, field types/order, variants and semantics incorporated here are
`docs/phase5/v1/PHASE_5_DOC.md:943-1029`, `:1816-1968`, `:2174-2297` and §5.1 `:2356-2375`.
The binding row says “Shared sixteen-row closeable binding protocol, never separate four rows”
at `:2357`. All §4.2 ownership, suppression, timing and cleanup rules are incorporated into this
§5 interface; they are not optional implementation commentary.

D-P8-25 receives P5's policy-keyed fully-far/opaque-white neutral backings unchanged.
Comparison and ordinary depth samples never share incompatible object state; different
remaining sampling parameters also prevent sharing. Failed neutral initialization cannot
preserve main rendering with an incompatible texture: use P5's failed-candidate/off result.
P8 allocates or deletes none of these objects; candidate/accepted-estate ownership and
binding-drain-before-retirement remain P5's. Shadow mipmap signatures/results are unchanged;
the new `generateMainMipmaps(PassBufferSnapshot)` belongs solely to P7's fullscreen path.

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
registry-generation check in `:2571-2593` remain binding; this is not the narrower
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
| synchronous replay delivery and P7 collector failure | §4.10 prerequisite after Activated and every immediate event, before another draw; phase6.replay.delivery.failed/collector latch forces Failed/off, unlike ordinary isolated uniform degradation; no new API |

The current runtime/event and sampler contracts are incorporated by
`docs/phase6/v1/PHASE_6_DOC.md` §5.1 at `:1613`, `:1617-1618`; R7-10/R7-11 are adopted but
not verified per `:296-300`. Phase 7, not Phase 8, owns runtime construction, accepted-generation
adoption and retirement under that owner's §5.1/§5.2. Its required consumer synchronization and
owner-verification gates at `:1688-1707` remain; no Phase 8 runtime-close authority is added.
`CelestialSample` remains exactly `(long worldEpoch,long frameId,Float3 sunPosition,
Float3 moonPosition,Float3 shadowLightPosition,Float3 upPosition)`. P8 supplies identity from
the current `ShadowFrameView` and eye vectors from the actual `CameraSnapshot.modelView()`;
P6 preserves its finite-value/current-world/nonregressing-frame event validation and retirement
rules. The pure angular result is not a P6 event or runtime dependency.

**Phase 7:**

| Phase 7 §5 contract | Use |
|---|---|
| `ShadowInvocationSlot`, `ShadowInvocationContext`, `ShadowInvocationResult` | sole synchronous frame seam; exact invocation fields below; borrowed only during invoke |
| `ShadowFrameView`, `CameraSnapshot`, `PublishedRegistry`, `PublishedBufferEstate`, `FrameBarrierContexts` | exact driver frame/world/sample and main matrices; no value inferred from equality-only FrameToken |
| `ShadowExecutionBridge`/identity/view and closed results | Phase 7 sole issuer/opener/closer; authenticated dynamic-extent main-hook bypass, preserved below |
| `ProgramBindingSelection selection`, `BarrierContext activationContext` | Phase 7 selects root shadow once before invoke; Phase 8 authenticates and reuses identical values, never remints/reselects |
| `TexturePublication texturePublication`, `TextureLeaseSource textureLeases` | non-owning members of the same active tuple; acquire full expected-publication lease with that same selection |
| result semantics and H-FRAME-05 | NotInstalled/no-activation advances only with §4.1 demand/neutral proof; Completed additionally requires §4.2 release/cleanup and replay-delivery proof, then P7 execution close; mutation-free Rejected aborts one frame; Failed forbids the main-estate rebind/draw and schedules off; vanilla only after safe recovery |
| §5.3 construction protocol and §5.4 R7-13 | §4.1 policy/health/accepted-minima requested plan -> provider/runtime -> compile/compose -> final-registry create; accepted PublishedBufferEstate.estate() once -> authenticated same BufferEstateView/generation/tuple -> BufferEstateView.shadow() -> available.view() neutralization for disabled/uninstalled before admission; off/absent/mismatch contains without refetch; no registry/late-estate planning lookup |
| `IdScopeAdmission` and current-bind adapter | exact P7 §5.1 opaque admission/authentication and AtlasBindingEvidence/AtlasBindingSink; P8 admits owned traversal IDs only while current execution is Valid, restores before release, never mints credentials itself |
| `ShadowExecutionView.currentBaseBinding()` and `installBaseBindingReceiver(ShadowBaseBindingReceiver)` | P7-issued current evidence; install returns SignalResult and accepts once/live invocation, rejecting wrong/stale/duplicate; receiver refreshBaseBinding(evidence)→SignalResult is synchronously dispatched before draws under §4.10, removed on execution close; no native binder or second execution bridge |

The exact field order incorporated from `docs/phase7/v1/PHASE_7_DOC.md:2697-2708` is:

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

The appended types/order are R7-12's exact request at `:4051-4061`; Phase 8 adopts that request here.
The record is Phase-7-owned, not a second Phase 8 constructor authority. Frame sample, bridge and
result semantics are incorporated unchanged from `:2717-2760`, together with §4.2's
ownership and post-mutation result mapping. Borrowed publication, source, selection/context and
execution are never closed or retained by the slot.
`CameraSnapshot` remains exactly `(Matrix4Value modelView,Matrix4Value projection)`. P7 owns
the live association of that copied post-camera value with the context's frame and shadowFrame.
Missing, stale or mismatched association rejects before camera math/GL; equal positions or
matrices do not establish identity. P8 borrows this value only for the invocation, as with the
other context fields, and uses it explicitly in the sole compute call.

`TexturePublication` retains `id`, registry fingerprint/generation, resource epoch, plan and
candidate table without lookup/close authority. The Phase-7-supplied Phase-13-owned source exposes
`TextureLeaseSource.lease(TextureOverlayPublicationId expected,ProgramBindingSelection selection,
AtlasBindingEvidence baseBinding)` returning exactly `TextureLeaseResult.Acquired(TextureOverlayLease lease)` or
`Rejected(TextureLeaseRejection reason)`. Reasons are `PUBLICATION_UNAVAILABLE`,
`PUBLICATION_ID_MISMATCH`, `REGISTRY_FINGERPRINT_MISMATCH`, `STALE_SELECTION`,
`INVALID_BASE_BINDING`, `STALE_BASE_BINDING`, in that order. Acquisition authenticates active
owner/expected ID/selection, then base-evidence issuer/thread/composition and latest serial/resource
epoch before incrementing its lease count. Later serial invalidation is P5 CLOSED_OVERLAY_LEASE;
rejection transfers nothing. There is no two-call `lease()`/`publicationId()` path. These producer
semantics are incorporated through `docs/phase7/v1/PHASE_7_DOC.md:3875-3897`; Phase 8 acquires no
texture owner or new direct composition authority. A present empty publication is not publication
absence and still uses this protocol. Phase 13's required fresh verification remains a gate.

### 5.5 Dependency adoption status — ungranted changes are never assumed

| ID | Owner | Required binding change | Why required |
|---|---|---|---|
| R8-1 | Phase 7 — architecturally granted, owner reverification required | Consume exact ShadowFrameView, authenticated ShadowExecutionView and driver-owned bridge from §5.4; preserve existing main-hook bypass and traversal token | §0.25 granted this; R7-12 appends credentials without changing bridge ownership |
| R8-2 | Phase 5 — architecturally granted, current shared contract adopted here subject to owner reverification | Consume §5.3's five-argument physical binding, sixteen-row closeable result and Bound-only transfer, plus typed mipmaps and coherent runtime neutralization | current §5 supersedes the historical four-row borrowed proposal; no additional shadow-only binder requested |
| R8-3 | Phase 1 — owner-granted/receiver-adopted, unverified | exact engine.shadow/mod.glue.shadow/mod.mixin.shadow slots | no new placement request; current owner/receiver verification required |
| R8-4 | Phase 7 — owner-designed/receiver-adopted, unverified | pure typed policy before provider, final-registry create and coherent close/off | planning cycle closed, fresh reviews remain |
| R8-5 / D-P8-37 | Phase 8 owner catalogue specified; P7/P2 coordinated receiving edits and reverification required | complete §4.13.1 dense 66-row/65-non-CLOUD scalar catalogue under `/4`, exact order/counts/dispositions/aggregate and flattened-v3 fingerprint | historical cardinalities/domains cannot grant current admission; no grouping, inferred success or wire fallback |
| R7-12 | Phase 8 — adopted by §0.7, unverified | §4.2 and incorporated §§5.1–5.4 consume the exact supplied selection/context/publication/source, selector-based beginPass, full physical binding and four-result closure protocol | closes the consumer design mismatch only; real slot remains gated until fresh whole-document owner PASS |
| R7-13 | Phase 8 — adopted by §0.8, refined by D-P8-26, unverified | ShadowPlanInput(policy,hookHealth,requested) and ShadowPlan contain no registry; create(plan,registry,uniforms,world,diagnostics) receives final registry; §4.1 validation and publication fingerprint include it | closes cycle without old fingerprint; positive-demand NotInstalled requires accepted-estate neutralization before admission |
| R7-10 / R7-11 | Phase 6 — owner-designed/P7-receiver-adopted, unverified | sole FixedSamplerResolver and permanent non-GL candidate/replacement/shutdown retirement | fresh current owner/receiver reviews required; no second map or runtime close |
| R5-1 / D-P8-38 | Phase 8 owner-granted; P7/P6 receiver reverification required | public static CelestialMath angles/sample, delegating policy/compute, independent main-sky and real-shadow preactivation cadence with same accepted frame/main camera | no shadow-availability gate or duplicate formula; preserved reviews are not implementation acceptance |

Current P4/5/6/7/13 shared-unit contracts require fresh whole-document owner verification.
P7 has adopted P6 R7-10/11 and P8 R7-12/13; P1 R8-3 and P2 R8-5 reporting are granted and
adopted, not open grants. P3 exact-current schema/required companion/lossless projections are
adopted through P7. The 2026-09-07 `docs/decisions/U1_TEXTURE_SAMPLING.md` correction is
adopted: numeric discriminators and P13-owned sidecars remain, with no unspecified key-suffix
execution or typed suffix grant required. jcpp permission and native legacy source preservation
remain separate owner gates. No package/reporting grant creates shader capability or waives
P8's own fresh verification.

**Current schema23 receiver receipt — D-P8-33 (2026-09-08; unverified).** P7 admits containing
configuration, nested `IdMappingInput` and any received inspection snapshot only at matching
`PackFrontEnd.CURRENT_SCHEMA_VERSION=23`, with current configuration identity, before policy
derivation or retained reuse. P3's range-capable selector value shape uses `MaterializedSource-v23`;
all earlier numeric schema/materialization receipts, including D-P8-30/schema22, are historical.
Reject older/future/mismatched input without synthesis, relabeling or an older cache fallback.
The nine metadata-only trees and projectionVersion=1 remain. P9 alone resolves selectors against
registries; P8 neither parses ranges nor resolves IDs. Assets/same-load identity, native source,
options, required companions and parameter domains are unchanged. Carry current opaque
identities without a new field in registry-independent `ShadowPlan`.

**Schema21 receiver receipt — D-P8-20, 2026-09-08 (unverified).** Adopt P3
§0.63/D-P3-70/§5 through P7 D-P7-36. Before shadow-policy derivation or retained-input
reuse, P7 requires containing configuration and nested `IdMappingInput` each to equal
`PackFrontEnd.CURRENT_SCHEMA_VERSION` (21 at this adoption) and each other. Any received
inspection snapshot must match that current schema/configuration identity before reporting.
Older, future or mismatched data is rejected, never relabeled, repaired by deleting profile
fields, or completed with old defaults/empty components. D-P8-18 and schema19 receipts below
are historical only. Current configuration and `MaterializedSource-v21` identities replace
old derived cache identities without adding source/materialization input to the pure P8 plan.
P4 D-P4-31's `RegistryFingerprint/own-build-v1` remains opaque final-publication/invocation
identity under existing comparisons, not a parsed domain or new registry-evidence gate.
D-P3-69 assets/nine source-free trees and same-load ownership remain unchanged; no binary access.
Shadow sizing/policy/traversal/depth/lease/provider/slot lifetimes are unchanged; fresh review remains.

**Current reporting receipt — D-P8-24, 2026-09-08 (unverified).** Adopt coordinated P2 §5.1.1
and P7 D-P7-41: `capture-plan/4` and `run-manifest/4`; D-P8-21's /3 adoption is historical.
scene/2 and golden version1 remain unchanged. Nested health remains scalar but its active dense
catalogue is now §4.13.1's 66 rows under D-P8-37, not historical eight-/59-/60-row receipts.
Main/root-shadow roles do not change. Existing `ShadowFrameView` carries the accepted main
frame's partial/worldEpoch/frameId; root-shadow stays inside that one accepted main frame,
never advances ticks/seconds for subpasses and never invents timing evidence. P8 adds no timing
API, reporting field, timing provider or clock authority. No /1-/2-/3 wire fallback or fresh PASS.
Under D-P8-25, P7/P2 also receive P5's `/4` PLANNED/REALIZED resource evidence and P2's
option-state/profile/comparison closure. P8 contributes only its existing immutable hook
and shadow values. It neither reconstructs realized formats or fog-clear RGBA nor adds
a profile codec, comparison result, timing field or resource projection of its own.

**Schema20 receiver receipt — D-P8-18, 2026-09-08 (unverified).** Adopt P3
§0.62/D-P3-69/§5 through P7; earlier current-schema assertions and the schema19 receipt
below record historical versions only. P7 requires configuration and nested IdMappingInput
schema20 before deriving shadow policy. Required non-null `assets` after `sources` stays
the exact same-load P3 capability paired with the exact containing `PackIdentity`; reject
foreign-load pairing even if structurally equal. The metadata-derived configuration identity
is carried unchanged through composition; the pure P8 plan still contains only its published
policy/hook inputs, never a new asset or registry field. No binary acquisition/parser,
dummy field, empty-manifest upgrade, reconstructed capability or resource-epoch relabeling.
Source-free inspection preserves P3's actual ninth canonical assets metadata section with
digest strings via TextHash, original eight meanings and projectionVersion=1, not bytes/
cursors/providers. P13 owns optional owned-sidecar-only recovery; other P3 safety/bounds/
index/container/source/configuration failures stay fatal. Shadow policy/traversal/depth/
texture semantics are unchanged; fresh reviews and IR-01 remain, not historical PASS.

**Cached native-call receipt — D-P8-19, 2026-09-08 (unverified).** Adopt P7
D-P7-32/R10-6 and P10 §§4.6/4.11's actual ModelRenderer every-call guard under the existing
authenticated root-shadow execution and successful effective P4 activation. Before every
cached native playback, P10 validates current geometryInput, full product/provenance and
owner incarnation; it selects or safely recaptures a compatible same-epoch product using
the original compile scale, otherwise contains before any stale/incompatible prefix.
Off/NONE/QUADS capture is not later TRIANGLES permission. The eight expanded CORE owner10
capture/playback/lifetime rows are required for both v0.1 geometry-only and v0.3 full health,
not replaced by chunk-only coverage. P1 LIST_REPLAY_GUARD and pointer-free replay restoration
remain owner operations; P8 adds no API or list parser. At v0.5 repeat only adjacent native
geometry-list calls, never ModelRenderer methods/children/matrix operations, capture, P8
traversal, Forge callbacks, depth split, clears, flips or mipmaps. Existing enclosing P7/P9
ID/color rows and restoration occur once, with no per-model/per-copy owner9 push, re-resolution
or upload; neutral pre-v0.3 state remains admitted. Failure uses existing P8 containment.
This receipt grants no runtime coverage, implementation clearance or fresh PASS.

**Schema19 receiver receipt — 2026-09-07:** adopt P3 D-P3-68/§0.61 and §11 migration
through P7. Before deriving shadow policy, the containing configuration and nested
IdMappingInput must equal CURRENT_SCHEMA_VERSION=19; reject 18/all other versions and
nested mismatch, never synthesize metadata. Existing shadow/depth/sizing/traversal values
are unchanged; native source and conditional draw compatibility belong to P1/P4/P7/P10,
not a P8 GLSL rescan. Prior schema18 addenda/decisions remain historical. Owner/receiver
reviews, implementation and conformance gates remain, no PASS claimed.

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
| no shadow buffers requested | normal absence | structurally valid planning returns NotRequested; authenticated present accepted P5 view must return ShadowEstateNotRequested; invoke returns Completed without state; vanilla blobs remain, no diagnostic or neutralization |
| sfb creation unavailable | 2a | authenticated present P5 view's coherent unavailable/neutral shadow proof permits main continuation; no repeated neutralization |
| empty/off publication estate or mismatched accepted wrapper/view | protocol | construction contains before main admission; invocation rejects before mutation; never interpret as shadow-only absence or refetch a current replacement |
| invalid resolved shadow policy | 2a only with safe fallback | disable with source-attributed diagnostic, no clamp; positive demand requires §4.1 accepted-estate neutralization before main admission |
| invalid final construction input or final-registry/publication mismatch | protocol | create returns Invalid without publication; P7 refuses mismatched installation and follows rollback/off; invocation mismatch rejects before P5/GL; never borrow old registry |
| missing/over-matched required non-cloud scalar H8 row or positive-demand NotInstalled gate | 2a only with safe fallback | preserve actual evidence and vanilla blobs; after accepted wrapper/view authentication, Available must become neutral with current estate generation and EXPLICIT_FEATURE_DISABLE before admission; Unavailable already safe, NotRequested mismatch contains off; SLOT also retains P7 CORE containment |
| frustum numeric degeneracy | 2a | disable culling/optimization for that frame and over-render loaded chunks; never under-render or crash |
| cloud resolution-row mismatch or cloud draw failure | 2a | disable clouds-in-shadow only; terrain/entity shadows continue; preserve CLOUD actual/disposition independently |
| one mipmap operation fails, base filter restored | 2a | consume per-buffer Degraded; disable mipmaps for that logical buffer and continue later buffers |
| mipmap base-filter restoration fails | 2a if restoration is safe, otherwise 5 | consume result-level Neutralized(...,true), stop without another complete/abort/neutralize, close binding and restore Phase 8 state; Completed only when safe, otherwise Failed |
| hardware-PCF texture setup fails | 2a only with safe fallback, otherwise 5 | Phase 5 yields unavailable/neutral only with preinitialized compatible backings; main continues under that proof, otherwise failed candidate/off containment |
| singular shadow matrix inverse | uniform rung 2 | Phase 6 disables only the corresponding inverse uniform; original matrix/pass continue |
| Phase 4 participant degrades | uniform/custom rung 1/2 | retain active shadow program only for ordinary isolated degradation with successful replay delivery and clear collector latch |
| root shadow resolves fixed function | normal fallback | render depth/fixed function; this is not compile failure |
| root activation returns `ShadersOff`/`FailedSafe` | 3/5 | abort snapshot, restore all state, return Phase 7 `Failed`; vanilla path remains reachable |
| stale context/registry/estate before GL | protocol | mutation-free `Rejected`; Phase 7 aborts this shader frame but keeps healthy publication |
| texture lease Rejected or shared binding Degraded/Rejected | selected-pass suppression | zero texture binds and no transfer; abort undrawn shadow snapshot without flips/mipmaps; close only any acquired lease; no activation/upload/draw/reselection; Completed after safe restoration, not a post-GL Phase 7 Rejected |
| shared binding BackendFailed | 2a if contained, otherwise 5 | partial binds possible, no transfer; close caller-owned lease, abort open snapshot, apply coherent neutralization and restore; no activation/upload/draw |
| Phase 5 target bind/clear/copy backend failure | 2a if contained, otherwise 5 | abort open snapshot without flips, then neutralize coherently and disable shadow only; failed containment forbids further shader draws and returns Failed |
| post-binding activation StalePublication | 5 | abort/restore and return Failed; never retry or describe the already-mutated invocation as mutation-free |
| vanilla draw throws | 5 | abort, restore state/traversal/Forge pass, report `Failed`, then preserve outer throwable policy |
| forced setup throws, rebuild-entry witness is not exactly one, or scheduling merge fails | 5 | no retry/traversal replay; restore original list and cache/debug fields, retain pending work/dirty invalidation, abort without flips and independently release/close/restore; return Failed and forbid main admission |
| state restoration cannot be proven | 5 | forbid further shader draws, return `Failed`, Phase 7 schedules shaders off |
| terminal release not FixedFunction, unavailable context/barrier, or release throw | 5 | latch Failed; no main continuation; independently attempt binding/lease closure, all state restoration and P7 execution close/abort/off; no rendering until safe program/state recovery is proven |
| replay evidence delivery diagnostic/exception or P7 collector failure | 5 | even after Activated, no next shadow draw/copy/mipmap or main admission; abort without flips, independently release/close/restore and return Failed/off; never downgrade to allowed uniform isolation |
| construction feature-disable neutralization rejected/unproven | 5 | forbid main admission and use failed-composition/off containment; no stale-generation retry, P8 texture, per-frame clear or binding workaround |

For backend-failure containment, `abortPass` consumes the still-open shadow token once, then
`degradeToNeutral(generation,BIND_BACKEND_FAILURE | CLEAR_BACKEND_FAILURE |
DEPTH_COPY_BACKEND_FAILURE | PASS_BACKEND_FAILURE)` uses the matching operation reason.
`Neutralized` or `AlreadyNeutral` permits `DISABLED_RUNTIME` and `Completed` only after §4.2's
`FixedFunction` terminal release proof, safe state restoration and current execution authority.
Rejected neutralization, failed abort/release/cleanup or unproven restoration returns `Failed`;
never touch a replacement estate using stale credentials.
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
2. **Angle boundaries:** exact `0`, `0.25`, `0.5` and adjacent representable values, near-1 moon
   selection, theta radians and sun-path rotation; reject NaN/infinite/negative/1 inputs rather
   than wrap. Compare provider shadowAngle with camera output from the same plan/sun sample.
3. **Snapping:** positive/negative coordinates, exact interval boundaries, zero interval, default
   2.0, camera motion below/above one cell, and perspective-no-snap.
4. **Celestial vectors:** independent scalar oracle for the four w=0 equations, sun/moon opposition,
   active-light choice, up independence from sky rotation and finite output. Hold time/position
   fixed and change a valid main rotation: correct changed eye vectors but equal shadowAngle.
   Change only main translation: unchanged eye vectors. Change world/frame identity with equal
   angles: the event echoes the new identity, never cached pure-policy identity.
   Exercise public sample with no shadow plan, targets or hook health for NotRequested, Disabled,
   NotInstalled and unavailable-estate compositions: the same accepted frame/camera/rotation
   yields identical celestial values. Reject invalid finite-domain inputs before event delivery.
5. **Plane extraction:** known identity/ortho frusta; all twelve adjacency pairs; synthesized plane
   parallelism to light; frustum-center orientation; maximum-capacity and degenerate fallback.
6. **AABB predicate:** every corner boundary, camera-relative translation, NaN conservative
   behavior, and comparison to an eight-corner scalar oracle.
7. **Traversal:** all light octants, vertical light, negative chunk coordinates, toroidal alias
   rejection, deterministic order/deduplication, D<V and D>=V branches, and brute full-view
   no-omission property tests.
8. **State machine:** re-entry, stale frame, every closed Phase 4/5/7 result, exactly one complete or
   abort, and no retained borrowed context.
9. **Plan/publication identity and absence precedence:** identical policy/health with false demand
   returns NotRequested, with true demand and healthy valid policy returns Ready; color-only minima
   also requests. Malformed structure disables before absence; valid false demand bypasses feature
   value/hook gates. Cache identity includes demand; Ready hashes encode true. Planning needs no
   registry and equal triples yield equal identity across compilations; changing final registry only
   changes publication identity, wrong-registry invocation rejects before P5/GL and equal content
   never revives a closed epoch.
10. **Lossless health boundary:** all 66 exact rows healthy (65 non-CLOUD); each individual zero/overmatch
    preserved, including compensated visit counts 0/2/1 (same total as 1/1/1) that must disable.
    Separate missing setter resolution, rebuild-entry anchor and each GET/SET/RESOLVE observation;
    cloud-only failure preserves real-shadow enablement but suppresses clouds. Reject missing,
    duplicate, unknown/group IDs, reordering, negative/wrong expected counts, forged disposition,
    aggregate or fingerprint. P7→P2 round-trip preserves every actual count and owner fingerprint.
    Runtime zero/multiple rebuild witnesses fail invocation without changing frozen health.
    Each of the six sort GET/SET failures independently disables; compensated 0/2 observations
    cannot sum to success. Old flattened-v1/v2 domains reject rather than migrate.
11. **Current-schema boundary:** schema23 containing/nested/inspection identity agreement admits
    policy derivation; older/future/mixed or stale materialization identities reject without
    reconstruction. Nine metadata-only trees/projectionVersion=1 and sole P9 resolution remain.

Pintonium's dawn/negative-coordinate sample is a cross-check, not a golden authority. Expected
values are generated from the formulas in §4.5 and compared independently.

### 8.2 Recorded facade and glue tests

With `RecordingGLDevice`/scripted ports and fake verified dependencies:

- assert Phase 7 select-once -> beginPass with identical selection -> target bind/clear -> uniform
  value signals -> atomic expected-publication lease -> sixteen-row preflight -> ascending physical
  binds -> same-selection/context activation -> ordered draws/split -> mipmaps -> complete ->
  per-draw ID/color/instance restoration -> current-frame release/FixedFunction ->
  binding close -> reverse platform restoration -> Phase 7 execution close/main-estate rebind;
- inject failure after each operation and assert reverse restoration, exact abort count, and no
  later draw;
- exercise each release result, release throw and invalid context: only FixedFunction permits
  continuation; no activation is required for step-2 absent/unavailable success. Inject failure
  separately in per-draw restoration, release, binding/lease close and each platform cleanup;
  prove later independent cleanup still runs and Failed never permits the main-estate rebind or unsafe vanilla.
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
- exercise creation failure and runtime neutralization with comparison shadowtex0 and ordinary
  shadowtex1, and with differing other sampling policies, while P14 is inactive: main sampling
  remains compatible only with preinitialized policy-matched backings; neutral initialization
  failure contains rendering rather than substituting a shared incompatible texture.
- assert the real slot remains NotInstalled while any remaining grant, consumer synchronization
  or required owner-verification gate remains open, despite R7-12/R7-13 adoption;
- exercise positive-demand Disabled/NotInstalled after accepted publication and before admission:
  Available invokes EXPLICIT_FEATURE_DISABLE with actual estate generation; Neutralized and
  AlreadyNeutral establish safe continuation, Unavailable skips duplicate work, NotRequested or
  rejected/stale/wrong-generation/unproven transition contains off; zero-demand NotRequested does
  no neutralization. Verify no real-undrawn shadow binding reaches main drawing;
- exercise wrapper-to-view boundaries in composition and invocation: accepted present estate
  unwraps once and dispatches only that view's shadow result; Available neutralizes its own
  returned shadow view/generation. Empty/off, wrapper/view generation mismatch, foreign accepted
  tuple or resource/registry mismatch forbids main admission; invocation rejects before mutation.
  Equal-content retired views cannot repair failure; no replacement-current lookup is attempted.
- inject replay delivery failure in activation and each immediate shadow event/copy boundary:
  Activated cannot authorize the next draw, no flips/mipmaps follow, Failed survives successful
  release, and all independent cleanup runs; ordinary isolated degradation still permits drawing.
- exercise first-load and reload ordering: pure plan/celestial policy before provider/runtime,
  compile/compose before final-registry create; wrong candidate pairing prevents installation,
  compile failure creates no real slot, and no path borrows an old registry;
- exercise the real provider -> post-camera capture -> invocation -> event -> activation path:
  provider obtains only shared shadowAngle before capture; no eye event exists yet. Retained
  sun/sky values plus actual main snapshot reach compute; the same returned celestial sample
  and primary shadow matrices precede first activation. Missing/mismatched live association
  rejects without GL; later sky notification preserves the equal value/formula. Repeat across
  rotation-only changes, world reset and publication replacement without stale-sample reuse.
- assert pass 0 before split and pass 1 after optional translucent draw;
- assert prior Forge pass, third-person option, matrices, viewport, framebuffer, renderInfos, and
  blob behavior restore exactly;
- future mapped-glue fixture: first complete main setup in a stationary world with no pending
  chunk updates and a clean dirty bit, then invoke shadow setup. An off-main-frustum caster
  within the shadow frustum must be visited and drawn; record exactly one shadow rebuild
  entry and reached seed/neighbor visit redirects, unchanged vanilla frame markers, and exact
  main list identity/order on return. Repeat full-view/prism, frozen debug frustum and an
  opaque camera cell; the compiled-visibility call may be short-circuited, not falsely counted.
- inject throws before/after forcing, during traversal, during near rebuild and at cleanup:
  no retry/duplicate traversal; main list and five view caches/debug fields restore, original
  and newly scheduled pending work survives, dirty invalidation survives. A clean successful
  call leaves the next stationary main setup able to reuse its main list; a failed restore
  forbids main drawing. Each required non-cloud row mismatch disables before invocation and
  P7→P2 retains all 66 exact scalar observations/flattened-v3 fingerprint. These are unrun future fixtures.
- future moved-camera translucent fixture: retain a main list with an eligible transparent chunk
  among the first fifteen candidates, excluded by the shadow prism. Move more than one block;
  run shadow TRANSLUCENT once and then the original main call after restoration. The main-only
  chunk must still be scheduled for sorting; all legitimate shadow tasks survive. Inject a throw
  after native sort-cache writes and verify exact three-double/main-list restoration and no queue
  rollback; production failure contains rather than drawing main, then a safely admitted later
  main call must still observe the movement threshold. Failed restore forbids continuation.
  Cover disabled translucents (no scope), each getter/setter failure, independent cleanup and
  no setup/traversal replay. These are architecture fixtures, not executed runtime evidence.
- future retained-shadow binding fixture: actual atlas A→non-atlas→atlas B transitions before
  native draws refresh the same open snapshot via P5, selecting exact companions/default fill
  after custom precedence. Observe no extra activation, split or traversal; each non-Bound
  refresh/close failure forbids the next draw and preserves independent terminal cleanup.
- assert H8-BLOB-01 suppresses only the blob invocation and preserves the fire path;
- future mapped-glue outline fixtures: initialized outline support with a visible glowing entity,
  then no glowing entity but retained `entityOutlinesRendered=true`. In each authenticated shadow
  call, observe ordinary entity and TE draws on the accepted sfb under continuous root-shadow
  activity, zero outline framebuffer clears/binds/postprocess/platform-FBO rebinds and no extra
  activation. Preserve Forge pass0/1 predicates, fire, counters and depth-split cadence. After
  normal return and injected throw, prove the entity-call guard is cleared and ordinary main
  outlines still use the original predicate/result and retained state. Wrong receiver, inactive/
  foreign/stale execution or calls outside the guarded extent cannot suppress the predicate;
  stale port admission draws nothing. Missing/overmatched H-SHADOW-OUTLINE-01 disables shadow
  before invocation, preserves main outlines, and crosses P7→P2 as its exact scalar observation.
- assert an authenticated present estate's unavailable shadow result performs no shadow GL and
  main frame proceeds; absent/off publication is not that result.
- at v0.5, verify N=3 gives each prepared submission IDs0,1,2 while Forge predicates, entity
  counters, traversal/setup, clear/split/flip/mipmap counts equal N=1; no main scopes in shadow;
- exercise nested instance restoration, stale shadow admission, second-copy failure (no third
  copy/no partial completion), and display-list compilation once/playback N times without
  captured instance IDs or nested N² multiplication.

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
| pure shadow plan/camera/celestial math | v0.2 | angular result first; frame-bound compute with actual main camera; future boundary/rotation/identity fixtures |
| extended frustum and full-view traversal | v0.2 | exactly-once forced rebuild, stationary/debug/exception fixtures and main-cache restoration before optimization |
| `shadowDistanceRenderMul` sun-prism iterator | v0.2 | enable only after full-view oracle proof |
| Phase 7 slot/context/composition | v0.2 | accepted-minima demand before provider, final-registry create, accepted-estate feature-disable neutralization before admission and replay prerequisite before drawing; remaining gates and fresh owner/P8 whole-document verification before real slot |
| Phase 5 shared binding/mipmap/neutralization | v0.2 | five-argument physical binding and sixteen-row closure protocol consumed; fresh owner verification first, no four-row substitute |
| FF state/world port and hook ledger | v0.2 | complete 66-row scalar health/receiver fixtures, independent runtime witness, sort-cache/outline continuity, then Cleanroom integration after pure tests |
| terrain/entity/cloud/split render order | v0.2 | ordered recorded test then T1 |
| PCF/filter/mipmap activation | v0.2 | Phase 5-backed, per-feature degradation |
| blob-shadow suppression | v0.2 | health-gated with fire-preservation test |
| Phase 6 matrix/celestial wiring | v0.2 | P7 provider shares pure shadowAngle before camera capture; same-frame post-camera event before first shadow activation; R5-1 receiver gate |
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
| D-P8-16 | Adopt the maintainer-approved v0.5 prepared-submission boundary, leaving shadow traversal/cadence intact | P4 §11.5 records exact evidence and 2026-09-07 authority; §4.8.5 defines shadow authentication/restoration/failure and excludes world/Forge replay. No new renderer API. |
| D-P8-17 | Adopt P3 schema18 through P7, superseding D-P8-15's earlier schema; reject schema17 | No parser/source reopening or inferred migration; P5 alone supplies actual main/shadow extents and no engine superSamplingLevel sizing field exists. |
| D-P8-18 | 2026-09-08: adopt P3 schema20/matching nested IDs and same-load assets identity through P7 in §§4.1/5.5 | Supersedes older current versions only; no plan field, acquisition authority or shadow semantic change |
| D-P8-19 | 2026-09-08: receive P7 D-P7-32/R10-6 and P10 actual cached-model guard for root-shadow native calls in §5.5 | Preserve one traversal and enclosing owner9 timing; owner10 health/lifetime guards do not add a P8 API or certify implementation |
| D-P8-20 | 2026-09-08: adopt P3 D-P3-70 schema21 through P7 D-P7-36 in §§4.1/5.5, exact-current containing/nested rejection and current opaque configuration/materialization/P4 registry identity | Preserve registry-independent plans, assets and all shadow lifetimes; previous schema receipts are historical; fresh review required |
| D-P8-21 | 2026-09-08: receive P2 R39/R55 and P7 D-P7-34/35 current /3 reporting in §5.5 | Preserve nested shadow hooks and one accepted main/root-shadow frame; no new timing API or execution evidence |
| D-P8-22 | 2026-09-08: resolve R5-1 with pure ShadowCelestialAngles and sole compute(frame,mainCamera,plan,shadowExtent); current P6 event uses frame identity/sky and actual main modelView | No stateful angular policy or pre-camera vectors; P7 provider shares angular result and validates live context association; §5 changed/unverified, fresh P8/P7 receiver review required |
| D-P8-23 | 2026-09-08 R6-1: current-frame release after draw-state restoration and before platform restoration; FixedFunction is sole release-success proof, failures/off contain rendering with independent cleanup | Makes the shadow activation interval executable on every terminal path; P7 D-P7-40 receives without duplicate release or bridge ownership |
| D-P8-24 | 2026-09-08 coordinated P2/P7 reporting cutover to capture-plan/4 and run-manifest/4 only in §5.5 | Preserve nested hooks/one accepted main frame; Main integrates final exact P2/P5 evidence fields, older /3 receipts remain historical |
| D-P8-25 | 2026-09-08 receive P4/P1 duration lock and positional registry identity, P5 compatible neutral cache and P2/P7 `/4` evidence ownership | Concrete owner mechanisms only; existing shadow mipmap/result API and terminal release remain, no new P8 binder or evidence codec |
| D-P8-26 | 2026-09-08 R7-1: three-field ShadowPlanInput(policy,hookHealth,requested), same accepted P3 minima as P5, structural-validation/absence precedence and demand-aware identity | Distinguishes identical default policies with different demand without registry/provider compilation cycle; §5 changes require fresh P8/P7 review |
| D-P8-27 | 2026-09-08 R7-2: requested disabled/uninstalled composition neutralizes accepted P5 Available estate with current estate generation and EXPLICIT_FEATURE_DISABLE before main admission | Unavailable proves neutral, zero-demand NotRequested is absence; all mismatches/rejections contain off; no new P5 candidate API |
| D-P8-28 | 2026-09-08 make P7 §5.1 replay receiver prerequisite explicit after Activated and immediate shadow events | P7 R39 receiver note, not a new finding/API: stable delivery failure/collector latch forbids next draw; ordinary isolation, terminal release and independent cleanup preserved |
| D-P8-29 | 2026-09-08 R8-1: authenticated mapped dirty setter forces one witnessed vanilla traversal with reversible main list/cache/debug state and lossless update scheduling | Repairs clean-cache reachability without frame-token changes, replacement renderer or duplicate traversal; expanded §5 hook projection requires P7/P2 receipt and fresh review |
| D-P8-30 | 2026-09-08 receive schema22/current-constant admission through P7 | Historical numeric receipts do not authorize old input; P3 BLOCK dual-era provenance adds no P8 ID heuristic, plan field or asset/native/option change |
| D-P8-31 | 2026-09-08 R9 C1: retain scalar ShadowHookRow and publish §4.13.1's complete canonical 59-row catalogue, exact audit meanings/order/aggregate and flattened-v1 fingerprint | Nine H8 labels are groups, not wire rows; independent counts cannot compensate; P7/P2 receive unchanged, runtime witness stays separate |
| D-P8-32 | 2026-09-08 R9 C2: obtain/authenticate accepted PublishedBufferEstate.estate() once, then BufferEstateView.shadow() and available.view() with matching accepted generation | Off/empty/mismatch contains before admission; invocation rejects before mutation; preserve wrapper/view/tuple, no refetch or P5 forwarding API |
| D-P8-33 | 2026-09-08 receive exact-current schema23 containing/nested/inspection and MaterializedSource-v23 through P7 | Range-capable P3 selectors remain metadata; P9 alone resolves; nine metadata-only trees/projectionVersion=1 unchanged; prior numerical receipts historical |
| D-P8-34 | Receive P1/P5 exact target-bearing values and 2D depth-region representation | P5 copy ownership/cadence and first-versus-steady semantics remain; no new P8 operation |
| D-P8-35 | 2026-09-08 R10 C1: exact authenticated shadow entity-call invocation guard suppresses the whole outline subpass, including retained outline state; publish H-SHADOW-OUTLINE-01 and complete 60-row/59-non-CLOUD flattened-v2 health | Prevents outline FBO/program escape before TE traversal while preserving regular entities/Forge/fire/main outlines; §5 and P7/P2 receivers require fresh review, no P5 API |
| D-P8-36 | Receive P4 D-P4-43 opaque profile-selection-v3 identity, superseding positional-route-v2 receipt | Shadow composition cannot reuse prior-domain candidates; no profile policy, local codec or new P8 API |
| D-P8-37 | 2026-09-08 R11 C1: exact three-double sort-cache finally restoration around actual shadow translucent call; 66-row/65-non-CLOUD flattened-v3 with six independent GET/SET additions | Preserve later main moved-camera scheduling without canceling legitimate shadow work, traversal replay or health aggregation; §5/P7/P2 require fresh review |
| D-P8-38 | 2026-09-08 P7 R43 C1 grant: public static CelestialMath angles/sample available without shadow plan/estate/health; existing policy and compute delegate one formula | Main celestial values must exist with no shadows; P7 authenticates current frame/main camera/rotation, shares same-association sample and preserves real-shadow before-activation delivery; unchanged P6 event |
| D-P8-39 | Receive P5/P13/P7 actual-base context through the third lease argument and P7 execution receiver; refresh only binding/lease on the same open shadow snapshot | Matching companions/default fills follow actual accepted base; retained root activity, four-result ownership, no-recursion observer batching and independent cleanup remain |

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

**R6-1 correction receipt (D-P8-23):** §§4.2/4.10/5.2/5.4/6/8.2/12 now require explicit
terminal release and result handling; P7 §§4.3/5.1 receives continuation/containment.
D-P8-25 completes the P1/P4 lock, P5 compatible neutral/mipmap and P2/P7 `/4` evidence
receipts in §§5.2/5.3/5.5; these sections incorporate the invocation/cleanup semantics.
No second owner mechanism is introduced. Fresh owner/receiver reviews, IR-01 and final §G5.3
remain open; this receipt is architecture correction, not runtime proof or review PASS.
D-P8-24/25 supersede the older `/3` receipt for current consumption; no fallback encoding
or independent resource reconstruction is admitted.

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

- R11 C1 / D-P8-37: P7/P2 receive §4.8.4's exact sort-cache restoration and complete
  66-row/65-non-CLOUD catalogue. Six sort GET/SET IDs follow 147595-R-SET in ASCII order;
  H-SHADOW-OUTLINE-01 remains first. Cut over to flattened-v3, rejecting v1/v2 without migration.
- P7 R43 C1 / D-P8-38: P7 main/provider and P6/P2 receivers receive the public pure producer,
  unchanged event and authenticated association/reuse/cadence without shadow availability gates.
- R9 C1 / D-P8-31: Main must update P7 scalar copy/freeze/validation and P2 dense-row/count/
  fingerprint receivers to incorporate the complete §4.13.1 catalogue. Exact field shapes remain;
  no eight/nine-row cap, aggregate substitution or second parser. Fresh P8/P7/P2 review remains.
- R9 C2 / D-P8-32: Main must update P7 D-P7-46 and incorporated composition/admission/invocation
  receivers to unwrap the accepted wrapper once, authenticate its present view/tuple/generation
  and dispatch BufferEstateView.shadow(); off/absent/mismatch contains, never refetches. P5
  needs no extension. D-P8-33's schema23/current identity must be received alongside this change.
- R8-1/N8-1: P7 must receive §5.1's forced traversal/restoration and full revised
  §4.13.1 flattened scalar health projection, then forward it unchanged
  to P2; fresh owner/receiver review remains required, not runtime certification.
- R7-1/R7-2: P7 must receive D-P8-26/27's exact triple, accepted-minima provenance/precedence
  and accepted-estate feature-disable disposition in actual construction, §5 and admission branches.
  Fresh coordinated P8/P7 review is required; preserved R7 counts/verdict are not new acceptance.
- P7 R39 receiver note: D-P8-28 makes the existing replay-delivery mandate explicit at P8
  activation/immediate-event draw boundaries; no additional finding or interface is claimed.

- R5-1: P7 receives D-P8-22's exact angular/compute contract in provider composition and live
  frame/camera dispatch. Preserve R37 PASS as history; changed receiving §5 requires R38.
  P8 remains unverified pending fresh whole-document review; no self-certification.
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
- P7/P10 consume §4.8.5's approved v0.5 prepared-submission contract through existing private
  draw adapters. P8 retains one traversal and cannot mint main admissions inside shadow.

### 11.5 Requested upstream changes

§5.5 is the active adoption/gate ledger. R8-1–5 and R7-10–13 are owner-designed and receiver-
adopted as applicable, not fresh verified implementation permission. Current schema23 and
P7's exact reload/current-bind/ID adapters require coordinated fresh reviews. Real shadow
remains NotInstalled until those gates and applicable separate upstream authority requests
close. No duplicate package grant or `/1` reporting fallback is required.

IR-18's non-fullscreen boundary is explicitly approved by the maintainer on 2026-09-07:
repeat prepared submissions, not traversal (§4.8.5). P4 §11.5 holds the independently consulted
author/OSS evidence and limits. Governing RC3 and historical RESEARCH remain unchanged.
A future DESIGN candidate should retain Phase 5/8 PCF ownership and this approved v0.5 boundary.
This amendment preserves traversal/camera/bridge/depth/mipmap/neutralization contracts and
does not grant a renderer-extension API or waive fresh owner/receiver verification.

---

## 12. Implementation checklist

1. **[v0.2]** Obtain fresh whole-document owner and P8 reviews for §5.5's adopted contracts,
   and close applicable separate upstream authority gates. Keep real shadow NotInstalled
   until verified; no repeated package/reporting grant request.
2. **[v0.2]** Add the granted engine/glue/mixin packages with seam tests rejecting Minecraft,
   Forge, Mixin, and LWJGL from `:engine`.
3. **[v0.2]** Implement registry-independent three-field ShadowPlanInput with accepted-minima
   request provenance, structural-first/absence-next validation and demand-aware cache/plan identity;
   headless identical-policy/different-demand and final-publication identity fixtures.
4. **[v0.2]** Implement public static CelestialMath angles/sample and delegating ShadowCelestialPolicy;
   P7 pre-camera/main-sky routes require no shadow plan/targets/health; finite-domain/boundary fixtures.
5. **[v0.2]** Implement ortho/perspective matrices in column-major `Matrix4Value`; projection
   goldens and compatibility-profile replay.
6. **[v0.2]** Implement model-view day/night rotation and signed-remainder ortho snapping;
   positive/negative camera motion tests.
7. **[v0.2]** Implement compute(frame,mainCamera,plan,shadowExtent) delegating to CelestialMath;
   authenticate P7 live input and preserve preactivation shadow/main-sky cadence and reuse.
   Prove rotation-sensitive w=0 vectors, no-shadow production, shared shadowAngle and no stale identity.
8. **[v0.2]** Implement six-plane extraction, adjacency table, synthesized-plane algebra,
   orientation, deduplication, and conservative fallback.
9. **[v0.2]** Implement allocation-free AABB testing and brute-corner oracle tests.
10. **[v0.2]** Implement `FullLoadedView` traversal and exact ViewFrustum toroidal-position check.
11. **[v0.2]** Implement the sun-aligned prism iterator and full-view no-omission property suite;
    keep it disabled until green.
12. **[v0.2]** Implement the Phase 8 hook/accessor Mixins with `require=0`, `expect=1`, SRG+
    descriptor targets, and all 66 independently observed §4.13.1 scalar rows/flattened-v3 fingerprint.
13. **[v0.2]** Implement authenticated Phase 7 shadow-execution guard handling in existing main
    terrain/entity/cloud/frustum hook adapters.
14. **[v0.2]** Implement `ShadowStateLease` snapshots and reverse restoration through the GL facade;
    failure injection after every state mutation.
15. **[v0.2]** Implement §4.7's authenticated dirty-setter-forced single setup and branch witness,
    complete flattened scalar health separate from runtime witness, pass-local visits and
    seed/occlusion overrides; prove stationary
    off-camera caster reachability, exact main list/cache/debug restoration, lossless pending
    work/dirtiness and exception containment without changed vanilla indices or replay.
    Implement §4.8.4's three independent sort-cache finally restores and moved-camera main-only
    translucent chunk/throw fixture without canceling legitimate scheduling or replaying traversal.
16. **[v0.2]** Consume and authenticate Phase 7's once-selected SHADOW selection/context and
    contained pass; prove no resolve/select/context-remint call inside Phase 8.
17. **[v0.2]** Implement selector-based beginPass, frozen ordinary+shadow readable sides,
    bind/clear/abort/complete branching and one-snapshot invariant.
18. **[v0.2]** Acquire the supplied expected-publication lease and call five-argument shadowBindings;
    exercise sixteen rows, physical binding before activation/upload, all four results and
    exactly-one finally closure after Bound versus every non-transfer/throw; no manual row loop.
    Register the P7 base-binding receiver and exercise same-snapshot refresh after actual
    external binds/restoration, full result ownership and no-recursive-echo/retained-root behavior.
19. **[v0.2]** Emit the same returned celestial event and primary shadow matrices before first
    barrier activation; prove provider/camera/event dataflow and Phase 6 inverse isolation.
20. **[v0.2]** Activate Phase 4 after Bound with the identical supplied selection/context and
    shadowPass=true; cover every barrier result and fixed-function NONE/no-upload behavior.
    Implement §4.2 terminal release with the supplied current FrameBarrierContexts.release();
    prove per-draw restoration precedes it and platform restoration follows it. Cover all release
    results/throws and independently failing cleanup; only FixedFunction plus completed cleanup
    and clear replay-delivery/collector failure state permits main continuation. No-activation
    returns require their mutation-free proof and §4.1 demand/neutral disposition.
21. **[v0.2]** Render terrain exactly SOLID -> CUTOUT_MIPPED -> CUTOUT; recorded call-order test.
22. **[v0.2]** Implement configured cloud draw with cloud-only degradation.
23. **[v0.2]** Implement Forge pass 0 entity traversal and exact prior-pass restoration.
    Implement H-SHADOW-OUTLINE-01's exact invocation guard with Valid execution/slot and
    receiver/dynamic-extent authentication; suppress the full outline branch before TE traversal,
    preserve main predicate behavior and retained outline state, and clear guard in finally.
    Exercise visible-glowing and retained-outline cases proving continuous sfb/root-shadow,
    no outline FBO/program mutation, unchanged ordinary entities/Forge/fire and main outlines.
24. **[v0.2]** Issue exactly one `SHADOW_PRE_TRANSLUCENT` copy and handle all Phase 5 outcomes.
25. **[v0.2]** Render optional `shadowTranslucent`, then Forge entity pass 1; false/true matrix tests.
26. **[v0.2]** Implement typed post-pass mipmaps and per-buffer degradation/filter restore; on
    result-level Neutralized prove no duplicate terminal call and preserved binding closure.
27. **[v0.2]** Implement H8-BLOB-01 blob-only suppression and fire-preservation tests; gate feature
    enablement on hook health.
28. **[v0.2]** Consume coherent runtime neutralization; partial binds never activate/upload/draw.
    After Activated and every immediate event enforce replay-delivery/collector prerequisite before
    another draw, preserving ordinary isolated degradation, terminal release and independent cleanup.
29. **[v0.2]** Integrate demand-aware plan-before-provider and final-registry create into P7
    composition/rollback/close after §5.5 gates. Before main admission, requested disabled/uninstalled
    accepted P5 wrappers unwrap estate() once, authenticate the present view/tuple/generation,
    then satisfy §4.1 BufferEstateView.shadow() and available.view() neutralization branches;
    absent/off/stale/rejected/unproven transitions contain off before draw. Prove no refetch,
    old-registry lookup, real-undrawn
    main binding, or wrong candidate pairing; drain binding/lease closure between publications.
30. **[v0.2]** Forward the complete §4.13.1 66-row scalar health catalogue through P7 to P2;
    validate dense order/counts/disposition/aggregate/fingerprint without eight/nine-row caps,
    count collapsing or inferred capability; keep invocation witnesses outside frozen health.
31. **[v0.2]** Run static day/night, moving-camera, water split, entity pass, cloud, perspective,
    PCF/mipmap, and far-caster T1 scenes.
32. **[v0.2]** Run first classic-pack T2 shadow comparisons; retain local/CI images only and commit
    manifests/hashes.
33. **[v0.2]** Satisfy the implementation gate: classic-shadow packs at T1 and first T2 results,
    with no unresolved state-restoration or traversal-omission defect.
34. **[post-v0.5]** Hand completed targets to G8/S1 for `shadowcomp`; do not implement it in this
    checklist.

**Current §G1.3 status:** active §5 includes D-P8-37's sort restoration/66-row flattened-v3,
D-P8-38's shadow-independent celestial producer and D-P8-39's actual-base binding refresh.
Phase 8 remains **unverified**; fresh whole-document owner and affected P7/P2 receiver review
is required. Prior decisions and frozen review verdicts remain historical, not acceptance.
Real shadow stays NotInstalled/unavailable until remaining gates close. This fix-up changes
only owner architecture prose and appends R10 Resolutions; no implementation, builds, tests,
lint, formatters, validation commands, native/runtime evidence, IR-01 or final §G5.3 clearance.
