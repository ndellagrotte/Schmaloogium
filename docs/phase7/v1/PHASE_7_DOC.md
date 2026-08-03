# Schmaloogium — Phase 7: Render-loop integration & frame orchestration — Architecture

## 0. Header

**Phase:** 7, both mandated parts: (a) engine-side frame driver and (b) Mixin hook catalog.  
**Document version:** v1, initial build.  
**Date:** 2026-08-03 · **Last revised:** 2026-08-03 (§0.25).
**Governing design:** `docs/design/v2.0-RC3/DESIGN.md`; its Phase 7 assignment begins at
`docs/design/v2.0-RC3/DESIGN.md:1805` and names dependencies 2–6 at
`docs/design/v2.0-RC3/DESIGN.md:1807`. The heading and ranges were derived from this
file's own headings, not shifted from another `DESIGN.md` revision.  
**Build-session result:** both parts fit in this session; the Phase 7b fallback was not invoked.

### 0.1 Inputs actually read

The assigned reading order was followed. Repository governance was read first:

- `AGENTS.md` in full and `docs/MOVES.md` in full.
- `docs/design/v2.0-RC3/DESIGN.md` Part I, §§G0–G12
  (`docs/design/v2.0-RC3/DESIGN.md:92`–`:1109`), and only the Phase 7 Part-II assignment
  (`docs/design/v2.0-RC3/DESIGN.md:1805`–`:1953`). The mandatory thirteen-section skeleton is at
  `docs/design/v2.0-RC3/DESIGN.md:790`–`:827`.
- `docs/research/v1/RESEARCH.md` §0, §1, §4.4, §5.3, §7.1, Appendix A.1, and Appendix E in
  full. The exact OQ-3/OQ-4 rows in §11 were also read because §G4.4 requires their questions
  verbatim in §10 below.
- `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §4, §6.1, and §16.
- The two assigned LGPL field-evidence files, and no other Pintonium implementation file:
  `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java`
  and
  `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinRenderGlobal_Shaders.java`.
- Cleanroom deltas in
  `reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch`
  and
  `reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch`.
- Only the lifecycle, per-frame flow, and one-frame summary sections of
  `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md` (§2, §5, §14), plus
  `reference-src/schlorbium-HD_U_G6_pre1/files.txt`. The digest was used only for behavioral
  observation. No decompiled implementation source, identifier, or code structure is reproduced.

The `cleanroom` MCP server was used on 2026-08-02 as assigned:

- `resolve_symbol` and `get_method_signature` checked every Appendix-E method consumed or
  explicitly deferred below, plus the supporting sky/weather/hand/particle/fog/blend/beam/glint/
  leash/resize/bootstrap methods. MCP stable_39 confirmed, among others,
  `EntityRenderer.renderWorldPass` → `func_175068_a (IFJ)V`,
  `RenderGlobal.renderSky` → `func_174976_a (FI)V`, and the world-render overload of
  `RenderGlobal.renderBlockLayer` → `func_174977_a
  (Lnet/minecraft/util/BlockRenderLayer;DILnet/minecraft/entity/Entity;)I`.
- `search_cleanroom_api(kind=event)` and `get_api_class` checked
  `RenderWorldLastEvent`, `RenderHandEvent`, `RenderSpecificHandEvent`,
  `DrawBlockHighlightEvent`, `RenderBlockOverlayEvent`, the three fog events,
  `RenderTickEvent`, `WorldEvent.Unload`, and `TextureStitchEvent`. Section 4.11 records each
  event decision; an event is not claimed to cover a moment its published API does not expose.

### 0.2 Dependency PHASE docs consumed

Only verified dependency artifacts were consumed. Their latest reviews were inspected solely to
establish the §G5.3 gate, before their current binding contracts were used:

| Dependency | Current artifact consumed | Binding material actually read | Gate evidence |
|---|---|---|---|
| Phase 2 | `docs/phase2/v1/PHASE_2_DOC.md` | §3.5, §4.5, §4.9, §5 | literal `PASS` at `docs/phase2/reviews/PHASE_2_REVIEW_15.md:67` |
| Phase 3 | `docs/phase3/v1/PHASE_3_DOC.md` | §§2.2–2.4, §§3.1–3.2, §§4.1–4.2, §§4.7–4.9, §5 | literal `PASS` at `docs/phase3/reviews/PHASE_3_REVIEW_22.md:51` |
| Phase 4 | `docs/phase4/v1/PHASE_4_DOC.md` | §§2.2–2.3, §§3.2–3.3, §§4.1–4.2, §4.10, §5 | literal `PASS` at `docs/phase4/reviews/PHASE_4_REVIEW_18.md:57` |
| Phase 5 | `docs/phase5/v1/PHASE_5_DOC.md` | §§2.1–2.3, §§4.4–4.5, §4.9, §5 | literal `PASS` at `docs/phase5/reviews/PHASE_5_REVIEW_30.md:54` |
| Phase 6 | `docs/phase6/v1/PHASE_6_DOC.md` | §§2.1–2.3, §4.2, §§4.6–4.7, §4.12, §5 | literal `PASS` at `docs/phase6/reviews/PHASE_6_REVIEW_7.md:52` |

The dependency rule is applied literally: this document consumes only interfaces present in those
current §5 regions. Section 5.4 flags missing or contradictory interfaces and does not pretend the
requested forms already exist.

### 0.3 Reading-list deviations and reasons

Three narrow additions were necessary and are recorded rather than hidden:

1. `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §3.1 was read because the Phase 7 scope
   itself mandates its per-dimension pipeline-cache/version-counter shape at
   `docs/design/v2.0-RC3/DESIGN.md:1853`–`:1855`, but the Required-inputs list accidentally names
   only PD §4, §6.1, and §16.
2. `docs/research/v1/RESEARCH.md` Appendix F.1 was read because the assignment makes Phase 7 the
   behavior owner for eleven engine flags while the listed inputs otherwise provide only their
   field names. The source says the corresponding video setting wins where one exists
   (`docs/research/v1/RESEARCH.md:1439`–`:1445`).
3. The exact OQ-3/OQ-4 rows at `docs/research/v1/RESEARCH.md:1008`–`:1009` were read to satisfy
   §G4.4's verbatim-question rule.

There was no network use. No `docs/**/chatlogs/**`, root-level `*.txt`, Oculus material, unlisted
Pintonium source, or decompiled OptiFine implementation source was read.

### 0.4 Legal and provenance posture

Schmaloogium remains GPL-3.0-or-later. Pintonium observations below carry an exact
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/...:line]` coordinate; its mechanism is
evidence, never contract. The two assigned
Pintonium files are LGPL evidence and no code is copied. The Schlorbium digest is used only to
restate behavior under the governing clean-room/licensing rules. RESEARCH wins every conflict. Nothing from
`glsl-transformation-lib`, vendored stareval, or any excluded transformation boundary is used.

### 0.5 Round-1 fix-up

Round 1 defines the hook result algebras and the synchronous Phase 8 invocation seam in §5.1, and
corrects `ProgramStateBundle` dependency ownership in §5.2. The §5 interface region changed and
therefore requires a fresh verification round before Phase 7 can close.

### 0.6 Round-2 fix-up

Round 2 closes the diagnostic-ID domain, makes the remaining consumer-facing §5.1 schemas
callable and closed, and adds the two missing lifecycle rows to §3.1. The §5 interface region
changed again, so a fresh verification round is required before Phase 7 can close.

### 0.7 Round-3 fix-up

Round 3 corrects four non-interface identifier and decision-disposition inconsistencies. The §5
interface region is unchanged.

### 0.8 Round-4 fix-up

Round 4 restores Phase 5 ownership of the final draw target and adds row-local provenance to the
two remaining conformance tables. The §5 interface region changed and requires fresh verification.

### 0.9 Round-5 fix-up

Round 5 defines capture-listener installation and corrects readiness generation identity. The §5
interface region changed and requires fresh verification.

### 0.10 Round-6 fix-up

Round 6 makes hook-report classes faithful to the catalog, including mixed-class rows and deferred
owner phases. The §5 interface region changed and requires fresh verification.

### 0.11 Round-7 fix-up

Round 7 qualifies mixed-row deferral and makes the built-in content contract a Phase 3 internal
source. The §5 interface region changed and requires fresh verification.

### 0.12 Round-8 fix-up

Round 8 defines the internal-pack content-digest contract and corpus invariants. The §5 interface
region changed and requires fresh verification.

### 0.13 Round-9 fix-up

Round 9 closes canonical path ordering and requests the missing Phase 3 path projection. The §5
interface region changed and requires fresh verification.

### 0.14 Round-10 fix-up

Round 10 synchronizes R7-9 into the downstream dependency and implementation action lists. The §5
interface region is unchanged.

### 0.15 Round-11 fix-up

Round 11 corrects R7-9 staging, feature-specific checklist gates, and §3.5 provenance tags. The §5
interface region is unchanged.

### 0.16 Round-12 fix-up

Round 12 separates ordinary internal loading from gated manifest production, closes reload intents,
and distinguishes documented engine-flag facts from Phase 7 runtime decisions. The §5 interface
region changed and requires fresh verification.

### 0.17 Round-13 fix-up

Round 13 closes reload-reason validation and makes coordinator publication an internal composition
capability. The §5 interface region changed and requires fresh verification.

### 0.18 Round-14 fix-up

Round 14 closes the resize/attachment observation-to-safe-boundary scheduling seam. The §5
interface region changed and requires fresh verification.

### 0.19 Round-15 fix-up

Round 15 authenticates resize state to a world incarnation and publishes its narrow observation
facade to glue. The §5 interface region changed and requires fresh verification.

### 0.20 Round-16 fix-up

Round 16 defines the canonical active-world identity publication used to authenticate frame and
resize signals. The §5 interface region changed and requires fresh verification.

### 0.21 Round-17 fix-up

Round 17 corrects the §5 fence, closes reload-token polling, and traces first-person overlay.
The §5 interface region changed and requires fresh verification.

### 0.22 Round-18 fix-up

Round 18 synchronizes reload polling into the §5 exposed-contract inventory. The §5 interface
region changed and requires fresh verification.

### 0.23 Maintenance addendum (Phase 9 coordinated integration — 2026-08-03)

Phase 9's R9-2 request is granted across §§1–9, 11, and 12. The safe-boundary transaction now owns
and publishes the Phase 9 ID-runtime candidate as a fourth coherent component, resets per-draw
dynamics on every terminal path, samples held values immediately after Phase 6 accepts a frame,
orders entity and block-entity ID scopes around existing program scopes, and replaces the former
RETURN-only color observer with exact `GL_TEXTURE_ENV_COLOR` operand capture. Registry remap and
ID-source reasons enter the reload queue, Phase-9 hook rows enter `HookApplicationReport`, and a
generation change gates layer/chunk invalidation before another shader frame.

The maintenance read was limited to Phase 9's R9-2 request and the detailed regions it names in
`docs/phase9/v1/PHASE_9_DOC.md` §§4.9 and 4.11–4.14, §§5.1–5.4, and §8. Phase 9 is a downstream
request source, not a declared Phase 7 dependency; Phase 7 consumes its runtime contracts only
after Phase 9 itself is verified. Phase 3's R9-1 surface was re-read only after its fresh literal
PASS in `docs/phase3/reviews/PHASE_3_REVIEW_22.md`.

**Current §G1.3 status:** round nineteen's literal PASS applies only to the pre-§0.23 bytes. This
addendum changes §5, so Phase 7 is **not verified** and is not a valid dependency input until a
fresh verification round returns literal PASS. The version directory remains `v1` while the loop
is open.

### 0.24 Round-20 fix-up

Round 20 corrects the two active Phase 9 ID-row counts to use the declared Mixin injection-anchor
unit. The §5 interface region changed and requires fresh verification.

### 0.25 Downstream-request addendum (Phase 8 coordinated integration — 2026-08-03)

Round twenty-one subsequently returned literal PASS with zero findings
(`docs/phase7/reviews/PHASE_7_REVIEW_21.md`). This maintenance amendment accepts Phase 8 R8-1,
R8-4, and Phase 7's half of R8-5 from `docs/phase8/v1/PHASE_8_DOC.md` §5.5.

The frame driver now lends an immutable `ShadowFrameView` with the exact driver frame ID, main
`setupTerrain` token, copied camera position, and same-sample sky/sun angles. A Phase-7-owned
`ShadowExecutionBridge` opens one authenticated dynamic-extent credential around slot invocation,
closes it before main clear, and makes existing terrain/entity/cloud/frustum hooks bypass only
their main-snapshot policy while that credential is active. Pipeline construction projects one
typed Phase 8 policy without reparsing, plans before constructing the Phase 6 provider, shares the
plan's celestial policy with that provider, and owns the resulting Phase 8 publication in its
rollback/teardown ledger. The application report nests Phase 8's immutable health rows without
renaming or re-keying any Phase 7 row.

**Current §G1.3 status:** round twenty-one's PASS applies to the pre-§0.25 bytes. This amendment
changes binding §5, so Phase 7 is **not verified** and is not a valid dependency input until a
fresh round twenty-two returns literal PASS (or any corrections are fixed and the changed
interface is re-verified). The version directory and manifest remain at `v1` while the loop is
open.

---

## 1. Scope & boundaries

### 1.1 Owned by Phase 7

Phase 7 owns:

1. the pure-`:engine` frame state machine that coordinates Phases 3–6 into one world frame;
2. transactional Phase 3/4/5/6/9 pipeline preparation/publication, shaders-off recovery, coherent
   ID-dependent geometry invalidation, the per-dimension slot cache, and its equality-only
   `PipelineVersion` invalidation signal;
3. exact classic-program dispatch, nested push/pop semantics, the deferred trigger, composite/final
   executor, fullscreen draw policy, and the strict composite guarantee;
4. the bounded internal passthrough pack supplied through Phase 3's `InternalPackSource`;
5. Phase 7's engine-flag behavior (`clouds`, four `backFace.*` flags, `sun`, `moon`, `vignette`,
   `underwaterOverlay`, `rain.depth`, `beacon.beam.depth`, and `frustum.culling`);
6. the dumb-hook bridge and the complete 1.12.2 hook catalog, including explicit deferrals for every
   Appendix-E row outside this phase;
7. display/Framebuffer observation, world/dimension transition detection, and reload safe points;
8. the Phase 2 capture point, readiness signal, capture-agent host, and clean-shutdown bridge, subject
   to the ungranted manifest projections in §5.4; and
9. invocation/registration seams consumed by Phases 8–14 so the later subsystem can join without
   changing vanilla hook coordinates.

### 1.2 Explicit adjacent ownership

- **Owned by Phase 1:** module/build enforcement, the GL facade and LWJGL implementation, bootstrap
  stages, diagnostic primitives, Mixin configuration, and the coexistence registry. Phase 7 requests
  package slots but does not rewrite Phase 1.
- **Owned by Phase 2:** scene validation, capture-plan and run-manifest wire schemas, fixture
  acquisition, image diffing, tiers, and runner-side failure manifests. Phase 7 hosts the client
  agent and frame moment only.
- **Owned by Phase 3:** pack discovery/loading, `PackConfiguration`, dimension-folder semantics,
  options/properties, `ResourceRequirements`, and internal-source validation. Phase 7 supplies bytes
  and consumes immutable results; it never reopens or reparses a pack.
- **Owned by Phase 4:** stage/program definitions, backup-chain resolution, compilation, private
  program handles, the state barrier, and registry publication. Phase 7 schedules and calls them.
- **Owned by Phase 5:** physical FBO/texture ownership, sides, flips, clears, depth-copy execution,
  resize publication, and pass snapshots. Phase 7 owns only the required moments and result handling.
- **Owned by Phase 6:** uniform/sampler catalogs, provider sampling, smoothing, temporal snapshots,
  typed event cells, upload caches, and barrier participants. Phase 7 produces hook events.
- **Owned by Phase 8:** shadow camera, traversal, content, split depth, PCF, and shadow completion.
  Phase 7 leaves and invokes one slot before main gbuffers drawing.
- **Owned by Phase 9:** registry/mod/tag snapshots, alias/layer tables, hand policy, ID-runtime
  candidate/publication, entity/block-entity/held/color values, and per-draw tokens. Phase 7 owns
  coordinated publication, accepted-frame/terminal calls, hook coordinates, and ordering around
  its existing render scopes; it never resolves an alias or samples a registry itself.
- **Owned by Phase 10:** vertex writes, chunk-build push/pop, attribute pointers, and the final
  chunk-renderer coexistence policy. Appendix-E classes 3–9 are catalogued, not specified here.
- **Owned by Phase 11:** custom-expression evaluation. It enters only through Phase 6's custom
  participant; Phase 7 never installs a fourth barrier participant.
- **Owned by Phase 12:** options GUI, persistence, user-facing selection, reload commands, and
  option mutation. Phase 7 exposes the safe reload controller and lifecycle status.
- **Owned by Phase 13:** atlas companions, custom/noise textures, texture-overlay publication, and
  atlas-size values. Appendix-E classes 10–11 are deferred to it.
- **Owned by Phase 14:** asynchronous/modernized GL work and performance changes. Phase 7 exposes
  timing and resize-consumer seams but defines no optimization policy.

### 1.3 Hard boundaries

The frame driver contains no Minecraft, Forge, Cleanroom, Mixin, LWJGL, raw GL integer, native
buffer, or `ProgramHandle`. Mixins contain no dispatch, fallback, flip, reload, or failure policy;
they translate parameters and delegate. Replacing every hook in §4.10 with a Kirino adapter must
leave the driver and all Phase 3–6 interactions byte-for-byte conceptually unchanged. This is the
Phase 7 expression of D-6 and the explicit Part-(a)/Part-(b) seam required at
`docs/design/v2.0-RC3/DESIGN.md:1935`–`:1939`.

---

## 2. Architecture overview

### 2.1 Placement

```text
:engine
  com.schmaloogium.engine.frame
    lifecycle/     FrameDriver, pipeline transactions, PipelineVersion
    dispatch/      RenderSection, phase table, nested scope stack
    fullscreen/    deferred/composite/final plans and viewport math
    flags/         resolved Phase-7 engine-flag policy
    internalpack/  immutable passthrough-pack manifest
    spi/           loader-neutral FrameRenderPort and later-phase bridges

:mod
  com.schmaloogium.mod.glue.frame
    FrameHookBridge, MinecraftFrameRenderPort, fixed-function matrix/world samplers
  com.schmaloogium.mod.mixin.frame
    the dumb Mixins in §4.10
  com.schmaloogium.mod.compat
    hook-health facts and the Phase-1/Phase-10 compatibility verdict adapter
  com.schmaloogium.mod.conformance
    Phase-2-schema capture-plan reader and CaptureAgent host (requested package)
```

`engine.frame` and `mod.conformance` are additive Phase 1 package-table requests, not assumed grants
(§5.4 R7-8). All GL work still goes through Phase 1/5 published operations or a loader-neutral
`FrameRenderPort` whose implementation lives in `mod.glue`.

### 2.2 Binding public shape

`FrameDriver` implements the `FrameHookSink` published in §5.1; that is the sole hook-facing engine
surface. Its exact operations are `open`, `beforeFirstClear`, `afterFirstClear`,
`captureMainCamera`, `afterTerrainSetup`, balanced `enter`/`exit`, `finish`, and `abort`. All return
closed result algebras. `FrameToken` and `ScopeToken` are opaque credentials rather than caller-made
frame IDs. The optional v0.3 `IdDynamicsFrameSlot` is a construction dependency of the driver, not
a new vanilla hook surface; it is paired atomically with the Phase 9 publisher and receives only
accepted-frame and terminal-reset calls.

```java
public interface FrameHookSink {
    FrameOpenResult open(FrameBeginSignal signal);
    FrameStepResult beforeFirstClear(FrameToken token);
    FrameStepResult afterFirstClear(FrameToken token, MainDepthPreparation depth);
    FrameStepResult captureMainCamera(FrameToken token, CameraSnapshot camera);
    FrameStepResult afterTerrainSetup(FrameToken token);
    ScopeOpenResult enter(FrameToken token, RenderSection section);
    ScopeCloseResult exit(FrameToken token, ScopeToken scope);
    FrameFinishResult finish(FrameToken token, FrameExitKind exitKind);
    FrameAbortResult abort(FrameToken token, FrameAbortReason reason);
}

public enum RenderSection {
    SKY_BASIC, SKY_TEXTURED, TERRAIN_SOLID, TERRAIN_CUTOUT_MIPPED, TERRAIN_CUTOUT,
    DAMAGED_BLOCK, ENTITIES, ENTITIES_GLOWING, BLOCK_ENTITIES, BEACON_BEAM,
    ARMOR_GLINT, SPIDER_EYES, LEASH, PARTICLES_LIT, PARTICLES_UNLIT, WORLD_BORDER,
    CLOUDS, WEATHER, TERRAIN_TRANSLUCENT, HAND_SOLID, HAND_TRANSLUCENT,
    FIRST_PERSON_OVERLAY
}
```

`RenderSection` is engine vocabulary for vanilla moments, not a replacement for pack-facing program
names or Phase 4's `StageId`/`StageBand`. The immutable `PhaseDispatchTable` maps each value to an
exact `ProgramSlotId` and contained Phase-4 `StageStep`; pack-facing names remain verbatim.

`FrameRenderPort` is the single loader-neutral platform surface. It snapshots/normalizes/restores
vanilla-visible state, binds a typed Phase 5 draw target, and executes immutable fullscreen draws.
World, extent, and camera data enter as copied `FrameBeginSignal`/`CameraSnapshot` values; capture
and shutdown remain `mod.conformance` services called at the published finalization notification.
No raw GL name or Minecraft object crosses into `:engine`.

### 2.3 Major relationships

```text
mod.mixin handlers ── dumb call ──> FrameHookBridge (:mod glue)
                                      │ immutable values/callbacks
                                      v
                         FrameDriver / FrameHookSink (:engine)
                         │         │          │             │
                Phase 4 barrier   Phase 5    Phase 6       Phase 9 runtime
                + stage view      estate     runtime/sink  aliases/dynamics
                         │         │          │             │
                         └──── coordinated frame/publication token ─────┘
                                      │
                         FrameRenderPort (:mod glue)
                                      │
                         Minecraft / GlStateManager / facade
```

The driver never asks a hook which program to use. A hook supplies only a typed moment; the driver
looks up the table, obtains Phase-4-issued frame contexts, resolves the published program, acquires
the Phase 5 snapshot, and branches on every closed result.

### 2.4 Invariants

1. At most one shader frame and one Phase-5 pass snapshot are open on the render thread.
2. `UniformRuntime.beginFrame` happens before any active-frame resize, replacement, or clear.
3. Current gbuffer matrices are captured exactly once, after `setupCameraTransform`, never merely
   because the first clear returned.
4. `FrameBarrierContexts.beginFrame()` is called once per shader frame; only its issued activation
   and release contexts are used.
5. A nested phase suspends and completes its parent's gbuffers snapshot, runs, then reacquires and
   reactivates the parent. No nested Phase-5 snapshot is fabricated.
6. A Phase-5 snapshot is the only permission to bind or draw, and `Completed` is the only permission
   to advance flip state.
7. Deferred/composite families traverse Phase 4's sparse population; no index hole terminates a
   family and no hardcoded count is authoritative.
8. `finish` is idempotent and exactly-once per opened frame. Normal TAIL and the outer `finally`
   race only through the same token.
9. A healthy early exit still executes composite/final. A protocol/backend failure aborts instead;
   it never draws from an untrusted snapshot merely to satisfy the guarantee.
10. No draw occurs between Phase 4 publication and the matching Phase 5 and, once activated,
    Phase 9 publications;
    a frame opens only after all active identities are coherent.
11. Every off/replacement/teardown invalidates Phase 4 activity tokens and increments the Phase-7
    equality-only `PipelineVersion` once.
12. Shaders-off means no Phase 4/5 shader draw, no engine FBO interception, and the unmodified
    Minecraft framebuffer path remains reachable.
13. A Phase 9 generation change is followed by one successful alias/layer-dependent geometry
    invalidation before another shader frame; stale chunk products are never drawn under a new ID
    runtime.
14. Phase 9 per-draw entity, block-entity, and color stacks are neutralized before Phase 4 fixed-
    function release on every normal, early, thrown, abort, off, replacement, and teardown path.

---

## 3. Contract conformance map

### 3.1 RESEARCH §4.4 frame-flow rows

| Contract item | Design element | Provenance |
|---|---|---|
| frame world-state sampling | §4.3 `open` calls Phase 6 first | `[V:observed]` `docs/research/v1/RESEARCH.md:533`–`:536` — “sample world state” and previous snapshots |
| fixed gbuffers texture set | Phase 5 snapshot/fixed table binding in §4.6 | `[V:doc]` `docs/research/v1/RESEARCH.md:537` — “fixed unit map” |
| shadow before main gbuffers | §4.3 shadow slot, then main clear/bind | `[V:observed]` `docs/research/v1/RESEARCH.md:538`–`:540` |
| complete gbuffers phase order | §3.2 + §4.4 dispatch table and hook rows | `[V:observed]` `docs/research/v1/RESEARCH.md:540`–`:542` |
| depthtex1 then deferred before translucents | §4.5 `beforeTranslucent` | `[V:observed]` `docs/research/v1/RESEARCH.md:543`–`:544` |
| water, hand solid, hand translucent/depth scale | §4.4/§4.10 H-HAND | `[V:observed]` `docs/research/v1/RESEARCH.md:544`, `docs/research/v1/RESEARCH.md:560`–`:561` |
| composite ping-pong and final | §4.6 | `[V:observed]` `docs/research/v1/RESEARCH.md:545`–`:546` |
| push/pop around leash/glint | `NestedRenderScopeStack` in §4.4 | `[V:observed]` `docs/research/v1/RESEARCH.md:551`–`:552` |
| precise depth copies and center depth | §4.3/§4.5; Phase 6 samples prior center depth at frame begin | `[V:observed]` `docs/research/v1/RESEARCH.md:557`–`:559` |
| identity fullscreen state, mipmaps, scale, instances | `FullscreenPassExecutor` §4.6 | `[V:observed]` `docs/research/v1/RESEARCH.md:562`–`:564` |
| composite guarantee | outer render wrapper + idempotent `finish` | `[V:observed]` `docs/research/v1/RESEARCH.md:565` — “guarantees composites run even on early exits” |
| `(internal)` is a real pack and `Off` is no shaders | §4.7 separates `PackSelection.Internal` from `PackSelection.Off` | `[V:doc]` `docs/research/v1/RESEARCH.md:478`–`:481` |
| dimension cache and all uninit/reinit triggers | §4.8 per-`DimensionKey` cache plus pack-, option-, resource-, dimension-, resolution-, registry-remap-, and ID-source/catalog/policy-triggered safe-point rebuild | `[V:doc]` `docs/research/v1/RESEARCH.md:483`–`:489`; assigned Pintonium mechanism, not contract, at `docs/design/v2.0-RC3/DESIGN.md:1853`–`:1855`; Phase 9 R9-2 adds the identity triggers |

### 3.2 Appendix A.1: every program row

| Appendix-A.1 slot/family | Phase-7 execution route | Provenance |
|---|---|---|
| `<none>` | GUI/menu stays fixed-function; `FIRST_PERSON_OVERLAY` uses draw-buffers-none, pending R7-3 | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `shadow`, `shadow_solid`, `shadow_cutout` | Phase 8 slot; Phase 4's `shadowPass=true` force-selection remains authoritative | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_basic` | leash and selection box | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_textured` | unlit particles | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_textured_lit` | lit particles and world border | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_skybasic` | sky, horizon, stars, void base scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_skytextured` | nested sun/moon scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_clouds` | clouds | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_terrain`, `gbuffers_terrain_solid`, `gbuffers_terrain_cutout_mip`, `gbuffers_terrain_cutout` | the three terrain-layer moments request the exact specific slot; Phase 4 resolves its G6 fallback to `gbuffers_terrain` | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_damagedblock` | damaged-block texture scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_block` | block-entity scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_beaconbeam` | beacon and crystal-beam scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_item` | retained/dormant; no 1.12.2 dispatch claim | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_entities` | entity scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_entities_glowing` | glowing-outline subsection | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_armor_glint` | balanced nested armor-glint scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_spidereyes` | balanced eye-layer scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_hand` | solid arm/item scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_weather` | weather, with PRE_WEATHER copy at v0.5 | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `deferred_pre`, `deferred…deferred15` | virtual pre transition request, then sparse pass traversal at translucent HEAD | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_water` | translucent terrain after deferred | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_hand_water` | translucent hand/item scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `composite_pre`, `composite…composite15` | virtual pre transition request, then sparse frame-end traversal | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `final` | final to `PassDrawTarget.Screen.INSTANCE`; absent/fixed terminal performs the required passthrough copy | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |

The names and fallbacks are not re-resolved here. Phase 4 publishes the entire mapping and warns
Phase 7 not to overlay requested-slot state on the effective provider
(`docs/phase4/v1/PHASE_4_DOC.md:1383`–`:1385`).

### 3.3 RESEARCH §7.1 hook-needs 1–11

| Need | Disposition | Provenance |
|---:|---|---|
| 1 frame begin/end | H-FRAME-01…07 plus outer-finally H-FRAME-00; v0.3 H9-HELD-01 follows accepted begin and `IdDynamicsFrameSlot.resetFrame` precedes terminal release | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 2 camera matrices | H-FRAME-04 after `setupCameraTransform`, once | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 3 shadow invocation | H-FRAME-05; content deferred to Phase 8/v0.2 | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 4 all per-phase switches | H-SKY, H-TERRAIN, H-DAMAGE, H-LINE, H-ENTITY, H-LEASH, H-GLINT, H-EYES, H-BEAM, H-PARTICLE, H-CLOUD, H-WEATHER, H-BORDER, H-HAND, and R7-3-gated H-OVERLAY-01 | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 5 depth copies + center depth | H-WEATHER and H-TERRAIN-02; center depth is Phase 6 frame-begin sampling | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 6 deferred + composite/final | H-TERRAIN-02 and `finish`/H-FRAME-06/00 | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 7 vertex interception | explicit Phase 10 deferrals E5–E8 in §4.10.8 | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 8 chunk-build push/pop | explicit Phase 10 deferrals E3/E4/E9 | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 9 atlas companions | explicit Phase 13 deferrals E10/E11; `TextureStitchEvent` is preferred where sufficient | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 10 display/framebuffer resize | H-RESIZE rows; OQ-3 decides context-layer reach | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 11 GUI/reload | GUI/screens/F3+R/command owned by Phase 12; Phase 7 exposes `ShaderReloadController` and no vanilla GUI mixin | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |

This is zero-unmapped coverage of `docs/research/v1/RESEARCH.md:802`–`:819`.

### 3.4 Seven-row Pintonium timeline disposition

| Reference row | Disposition and contract check |
|---:|---|
| 1 `renderWorldPass` HEAD | **Adopt moment** for dimension observation, phase reset, and Phase 6 begin. Phase 3 remains the only dimension/source truth. `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:71`–`:93]` (`D-P7-3`) |
| 2 before first clear | **Adopt moment** for cache-coherent vanilla state normalization, not policy. `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:95`–`:100]` |
| 3 after first clear | **Split/deviate.** The reference combines capture/begin at this point (`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:102`–`:113`). Buffer preparation may remain after the clear, but matrix capture moves after the subsequent `setupCameraTransform` call because the Cleanroom patch visibly orders clear first (`reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:175`–`:179`). RESEARCH requires capture after camera setup. `D-P7-4`. |
| 4 after `setupTerrain` | **Adopt moment** for the Phase 8 slot, then rebind/clear main. `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:116`–`:121]` |
| 5 translucent layer HEAD | **Adopt moment**, but target the actual four-argument world-render overload. `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinRenderGlobal_Shaders.java:23`–`:33]`; RESEARCH confirmation at `docs/research/v1/RESEARCH.md:543`–`:544`. |
| 6 around first-person item | **Adopt balanced hand scope only. Reject the reference's depthtex2/center-depth timing:** RESEARCH puts depthtex2 before weather and Phase 6 owns center-depth at frame begin. `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:124`–`:142]`; `D-P7-5`. |
| 7 `renderWorldPass` TAIL | **Adopt normal-return fast path and strengthen** with H-FRAME-00 `finally`. The implementation uses a TAIL only (`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:144`–`:149`), and PD records no early-exit handling (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:195`–`:197`). `D-P7-2`. |

Sky, weather, and clouds have **no working reference**. PD states that absence directly at
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:199`–`:203`; their catalog rows are marked
high-risk and are front-loaded in v0.1 assembly.

### 3.5 Phase-7 engine flags

| Phase 3 field | Exact owner behavior | Provenance |
|---|---|---|
| `clouds` | resolve `DEFAULT/FAST/FANCY/OFF` once per publication; a corresponding explicit video setting wins; OFF cancels, FAST/FANCY is returned to vanilla's cloud-mode query | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:477` and precedence at `docs/research/v1/RESEARCH.md:1439`–`:1445`; runtime mapping `[D-P7-11]` |
| `backFaceSolid/CutoutMipped/Cutout/Translucent` | TRUE temporarily disables culling for exactly that terrain-layer scope; FALSE/DEFAULT preserves vanilla state; scope exit restores | `[V:doc]` fields/owner at `docs/phase3/v1/PHASE_3_DOC.md:486`–`:489`; runtime mapping `[D-P7-11]` |
| `underwaterOverlay` | FALSE cancels only the WATER `RenderBlockOverlayEvent`; TRUE/DEFAULT preserves vanilla | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:482`; runtime mapping `[D-P7-11]` |
| `sun`, `moon` | FALSE suppresses only the corresponding textured sky draw; TRUE/DEFAULT preserves vanilla | `[V:doc]` fields/owner at `docs/phase3/v1/PHASE_3_DOC.md:483`–`:484`; runtime mapping `[D-P7-11]` |
| `vignette` | FALSE cancels only `RenderGameOverlayEvent.Pre(VIGNETTE)`; TRUE/DEFAULT preserves vanilla | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:485`; runtime mapping `[D-P7-11]` |
| `rainDepth` | FALSE disables depth test only for the weather scope and restores it; TRUE/DEFAULT preserves vanilla | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:490`; runtime mapping `[D-P7-11]` |
| `beaconBeamDepth` | FALSE disables depth test only for beacon/crystal beam scopes and restores it; TRUE/DEFAULT preserves vanilla | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:491`; runtime mapping `[D-P7-11]` |
| `frustumCulling` | FALSE makes only the catalogued world-render frustum queries return visible; TRUE/DEFAULT delegates unchanged | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:493`; runtime mapping `[D-P7-11]` |

All are tri-state-preserving; unset never silently becomes TRUE. The precipitation helper retains the
Phase 3 handoff exactly: none for `PPT_NONE`, rain at temperature `>= 0.15`, snow below
(`docs/phase3/v1/PHASE_3_DOC.md:536`).

### 3.6 Input contradictions and binding rulings

| Conflict/gap | Ruling in this document |
|---|---|
| Appendix E lists the one-argument `func_174982_a` overload, while the actual world loop calls the four-argument overload (`reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:200`–`:206`). MCP confirms `func_174977_a (...;DILnet/minecraft/entity/Entity;)I`. | H-TERRAIN targets `func_174977_a`; the one-argument method remains a validated but non-world-loop overload. Requested upstream correction U7-1. |
| RC3/Pintonium row 3 calls the ordinal-zero-clear site a matrix-capture moment, but the Cleanroom patch orders that clear before camera setup (`reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:175`–`:179`). | RESEARCH and Phase 6 win: split the hook and capture after `setupCameraTransform`. |
| Phase 5 orders `PRE_TRANSLUCENT` before `PRE_WEATHER` (`docs/phase5/v1/PHASE_5_DOC.md:1314`–`:1318`), while RESEARCH's world order renders weather first and requires copies before weather and before translucent (`docs/research/v1/RESEARCH.md:542`–`:559`). | Driver calls the contract moments in vanilla order; R7-1 requests a Phase 5 fix-up. No reversed call is assumed. |
| Phase 4 makes `*_pre` virtual with no program (`docs/phase4/v1/PHASE_4_DOC.md:663`–`:666`), but Phase 5's only snapshot operation requires a `ResolvedProgramDescriptor` (`docs/phase5/v1/PHASE_5_DOC.md:579`–`:587`). | R7-2 requests a typed virtual-transition operation. Phase 7 never fabricates a program descriptor. |
| Phase 2 requests program/resource/GL-error manifest projections not present in current dependency §5 contracts. | Capture remains schema-aware but cannot publish a conforming COMPLETE manifest until R7-4…R7-6 are granted. Runner failure manifests remain Phase 2-owned. |

### 3.7 OptiFine replacement-list cross-check

An exhaustive read of `reference-src/schlorbium-HD_U_G6_pre1/files.txt` produced this result rather
than the blanket corroboration asserted by Appendix E:

| Set | Result |
|---|---|
| App E rows 1–16 and 18 | all 17 classes are present; representative coordinates are `reference-src/schlorbium-HD_U_G6_pre1/files.txt:20`, `reference-src/schlorbium-HD_U_G6_pre1/files.txt:25`, `reference-src/schlorbium-HD_U_G6_pre1/files.txt:27`, and `reference-src/schlorbium-HD_U_G6_pre1/files.txt:32` for `WorldClient`, `BufferBuilder`, `EntityRenderer`, and `RenderGlobal` |
| App E row 17, `net.minecraft.client.shader.Framebuffer` | **absent** from the complete 112-line list; this contradicts `docs/research/v1/RESEARCH.md:1390`'s “every class” claim |
| added supporting hook targets | `GuiMainMenu`, `OpenGlHelper`, `GameSettings`, `RenderLiving`, `RenderLivingBase`, armor/eye layers, and `TileEntityBeaconRenderer` are present; representative coordinates are `reference-src/schlorbium-HD_U_G6_pre1/files.txt:11`, `reference-src/schlorbium-HD_U_G6_pre1/files.txt:31`, `reference-src/schlorbium-HD_U_G6_pre1/files.txt:57`–`:69`, `reference-src/schlorbium-HD_U_G6_pre1/files.txt:82`, and `reference-src/schlorbium-HD_U_G6_pre1/files.txt:93` |
| added `Minecraft` resize and `RenderDragon` beam targets | **absent**; H-RESIZE and H-BEAM-02 therefore have MCP/Cleanroom/Pintonium evidence but no `files.txt` corroboration |

Absence from an OptiFine replacement digest does not disprove a vanilla call site. The exact hooks
remain subject to OQ-4 and their health groups. U7-3 requests that RESEARCH narrow its independent-
corroboration statement.

---

## 4. Detailed design

### 4.1 Pipeline lifecycle and publication

`DimensionPipelineManager` is the one render-thread composition root. It owns the selected
`PackConfiguration`, `DimensionKey`, caller-owned candidates during preparation, the successful
`UniformRuntime`, the active non-owning Phase 4/5 publications and optional v0.3 Phase 9
publication, the optional v0.2 Phase 8 plan/publication, its Phase-7-owned
`ShadowExecutionBridge`, hook-health/compatibility facts,
the ID-dependent geometry invalidator, and one `PipelineVersion`. It never owns a program, texture,
registry object, alias table, or raw handle directly.

The closed manager states are:

```text
OFF
  └─ request(selection) -> DISCOVERING/LOADING (Phase 3; may prepare off-thread)
LOADING
  ├─ PackLoadResult.Off/Failed/incompatible -> OFF_PUBLICATION_PENDING
  └─ Loaded -> PREPARING
PREPARING
  ├─ any closed failure -> OFF_PUBLICATION_PENDING
  └─ shadow plan + registry + uniform + buffer + barrier
       (+ installed v0.2 shadow publication / v0.3 ID candidate) ready
       -> READY_TO_PUBLISH
READY_TO_PUBLISH
  ├─ Phase 4 rejected/recovered-off -> close caller-owned remainder -> OFF
  └─ Phase 4 accepted -> publish matching Phase 5, then Phase 9, with no intervening draw
       ├─ all accepted + geometry invalidated -> ACTIVE
       └─ rejection/provenance/invalidation failure -> compensate all accepted parts to off -> OFF
ACTIVE
  ├─ safe reload/dimension/resize/remap/ID-source request -> QUIESCING
  ├─ runtime fatal/backend failure -> OFF_PUBLICATION_PENDING
  └─ world render -> frame state machine §4.2
QUIESCING -> finish/abort -> close Phase 8 publication -> reset Phase 9 dynamics
          -> release fixed function -> deactivate Phase 9 then publish Phase 5/4 off
          -> close Phase 6 runtime -> PREPARING/OFF
```

Preparation follows dependency ownership exactly:

1. obtain one validated Phase 3 `PackConfiguration`; reject any schema version other than
   `PackFrontEnd.CURRENT_SCHEMA_VERSION`, or a nested `IdMappingInput.schemaVersion` mismatch,
   before deriving state;
2. select its base, explicit override, or explicit disabled `DimensionConfiguration`; disabled does
   not inherit base;
3. reject `CompatibilityStatus.REQUIRES_NEWER_EDITION` to shaders-off with the Phase 3 diagnostic;
4. project exactly one typed Phase 8 `ShadowPolicy` from the already validated immutable Phase 3
   configuration/dimension view, without reopening pack resources, reparsing properties, or
   consulting hot-hook state; call `ShadowPlanFactory.plan` now, before constructing the Phase 6
   platform provider;
5. construct the loader-neutral Phase 6 platform provider with the ready plan's
   `ShadowCelestialPolicy` (or explicit absence for `NotRequested`/`Disabled`) and create Phase 6's
   `UniformRuntime`. The provider adapter records the same accepted frame sample used by Phase 6 so
   §4.3 can project camera/sky/sun values without resampling;
6. through `mod.glue.id`, freeze the live registry/tag/mod-source/hand-policy inputs required by
   Phase 9, call its `IdRuntimeBuilder`, and retain the returned caller-owned candidate; validate
   that its view identifies this exact configuration and registry fingerprint;
7. call Phase 4 `ProgramRegistryCompiler.compile`, using Phase 6's **empty**
   `centerDepthMacroContributor` contribution and the exact dimension;
8. inspect `CompiledRegistryCandidate.view()` without taking or inventing ownership;
9. call Phase 5 `BufferArchitecture.create` from that view, its exact fingerprint, capabilities,
   runtime sizing, `MainDepthSource`, device, and diagnostics; `AwaitingMainDepth` defers publication
   and permits only vanilla drawing;
10. call `ProductionBarrierComposer.compose` exactly once with the compiler candidate and Phase 6's
   sampler/built-in/custom participants in that fixed order;
11. when step 4 produced `Ready(plan)`, construct exactly one Phase 8
    `ShadowPassPublication` after the Phase 6 runtime exists, passing that plan, that runtime, the
    bridge-authenticated `ShadowWorldPort`, and diagnostics. `NotRequested`/`Disabled` installs no
    publication and retains vanilla blob behavior; a closed construction failure disables only
    Phase 8 when safe and otherwise fails the transaction;
12. compare `BufferEstateCandidate.inspection().registryFingerprint()` to the registry candidate's
    fingerprint, verify the shadow plan/publication and nested hook report share the current
    registry/hook fingerprints, and recheck every Phase 9 candidate input fingerprint before any
    publication;
13. at a no-draw, no-open-frame safe point, close the old Phase 8 publication, reset all old Phase
    9 per-draw scopes, acquire the
    current Phase-4 publication's issued release context, and
    publish `RegistryPublication.Ready`; close all caller-owned candidates on rejection;
14. only after Phase 4 accepts, publish the already-created matching buffer candidate;
15. only after Phase 5 accepts, publish the Phase 9 candidate with the matching world,
    configuration, and registry context;
16. after the Phase 9 generation changes, synchronously submit its old/new generation and
    alias/layer fingerprints to the installed ID-dependent geometry invalidator; and
17. atomically install the new Phase 8 publication's slot (or the explicit `NotInstalled` slot),
    retained runtime, and accepted dependency publications as one `ActivePipeline`; and
18. permit shader drawing only after the complete composition and successful invalidation are
    visible together.

The Phase 5 ordering is binding at `docs/phase5/v1/PHASE_5_DOC.md:1781`–`:1785`. A buffer consumer
failure after Phase 4 acceptance publishes both systems off before returning to vanilla. If Phase 9
rejects after Phase 4/5 accept, Phase 7 immediately recovers Phase 4/5 off and deactivates the old ID
publication after reset; it never opens a frame that combines new programs/buffers with old ordinals.
If post-publication geometry invalidation fails, Phase 7 deactivates the new ID runtime, recovers
Phase 4/5 off, and requires a full invalidation before any later activation. A Phase 4
`RecoveredOff` is already off and receives neither buffer nor ID publication. An accepted candidate
is not closed by Phase 7; caller close is harmless after transfer, but its publisher is the owner.

`ActivePipeline` is the immutable composition of one `PublishedRegistry`, one
`PublishedBufferEstate`, its retained `UniformRuntime`, one `PipelineVersion`, an optional Phase 8
`ShadowPassPublication` paired with its current slot and plan fingerprint, and either both
`Optional<PublishedIdRuntime>` plus `Optional<IdDynamicsFrameSlot>` present or both absent before
v0.3. Present components must share the transaction's configuration, registry, world, and hook-
report capability identities. The manager atomically replaces this composition only after §5.3's
last gate; hooks borrow one snapshot for the whole frame and never assemble components separately.

#### Per-dimension cache and version counter

The required Pintonium shape is adopted as a mechanism, not as dimension semantics
(`D-P7-3`). `DimensionPipelineCache` is a deterministic map from Phase 3 `DimensionKey` to:

```text
Disabled | PlanOnly(configuration + registry/mod/tag/policy + dimension + pure plan fingerprints)
         | ReadyUnpublished(caller-owned Phase 4/5/9 candidates)
         | Active(non-owning Phase 4/5/9 publication identities)
```

Only one slot can be `Active`. Phase 4/5/9 do not return accepted candidates to the caller when a
publication is replaced, so switching away demotes that slot to `PlanOnly`; Phase 7 does not invent
a reusable live-object lease. Other slots may retain caller-owned, not-yet-published candidates if
they were prepared on the render thread. Pack reload, option change, capability change, or config
fingerprint inequality, registry remap, or ID-source/catalog/policy fingerprint change closes every
unpublished candidate and clears the map. This retains the
per-dimension lookup and invalidation shape observed at
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:110`–`:114` without contradicting Phase 3's
no-base-merge world-folder contract.

`PipelineVersion(long value)` increments exactly once after each fully accepted ready publication,
accepted off publication, or forced recovered-off outcome. Consumers compare equality only; it is
not a clock and ordering/subtraction is forbidden. Phase 4 and Phase 5 generations remain their own
authorities, as does Phase 9's runtime generation; Phase 7 never claims the values are equal.

### 4.2 Per-frame state machine and composite guarantee

The per-frame machine is separate from pipeline lifecycle:

```text
IDLE
  -> SAMPLED            Phase 6 beginFrame accepted; Phase 9 held sample accepted;
                        Phase 4 contexts issued
  -> BUFFER_OPEN        Phase 5 refresh/replacement check then beginFrame(Begun)
  -> MATRICES_CAPTURED  current gbuffer matrices captured after camera setup
  -> SHADOW_DONE        Phase 8 slot returns/absent; main estate clear/bind succeeds
  -> GBUFFERS           hook-driven scoped raster draws
  -> DEFERRED_DONE      PRE_TRANSLUCENT copy result handled; deferred family complete
  -> GBUFFERS_TRANS     water/hand/overlay scopes
  -> FINALIZING         composite family then final, exactly once
  -> COMMITTED          Phase 5 commit + Phase 4 fixed-function release
  -> IDLE

Any state from BUFFER_OPEN onward
  -> ABORTING           consume any open snapshot, restore scopes/state
  -> ABORTED            Phase 5 abort result handled; full-clear fact retained
  -> RECOVERING/OFF     on backend failure; otherwise IDLE for the next full-clear frame
```

`open` rejects a second frame, a wrong thread, a stale active Phase 4/5/9 publication, a non-world
pass, or a missing world/camera without GL work. A duplicate Phase 6 begin is a safe no-op and may continue;
`REJECTED_STALE_FRAME` or `REJECTED_GENERATION` forbids shader drawing and reacquires the current
publication as required by `docs/phase6/v1/PHASE_6_DOC.md:754`–`:759`.

H-FRAME-06 calls `finish(NORMAL)` on ordinary TAIL. H-FRAME-00 wraps the containing
`renderWorld` invocation and calls `finish(EARLY_RETURN|THROWN)` from `finally`. Both use the
same opaque token and `compare-and-finish`; the loser is a no-op. If the frame is healthy,
`finish` closes/suspends any current gbuffers scope, runs all remaining composite passes and final,
fires the before-present observer, commits Phase 5, calls Phase 9 `PerDrawDynamics.resetFrame` to
neutralize entity/block-entity/color stacks, releases Phase 4 to fixed function, and restores the
vanilla state lease. Abort, thrown, resize, off, replacement, and teardown paths call the same reset
before fixed-function release and before any later shader draw. This is deliberately stronger than
the Pintonium normal-TAIL behavior.

An engine protocol/backend error is not a healthy “early exit.” It calls `abortFrame`, never commits
an unwritten alt side, releases fixed function, and publishes off if Phase 5 returns
`BackendFailed` or Phase 4 returns `ShadersOff`/`FailedSafe`. An exception thrown by vanilla is
preserved after best-effort healthy finalization; Phase 7 catches and contains only its own failures.

### 4.3 Exact frame-begin ordering

The seven reference moments are split into the following normative sequence:

1. **`renderWorldPass` HEAD:** identify world/dimension/frame. For an active unchanged pipeline,
   issue Phase 4 `FrameBarrierContexts` and call Phase 6 `beginFrame`. This rotates previous values,
   samples world/tick/frame state, and reads the **completed prior framebuffer** before Phase 5 is
   touched. New-world epoch reset uses prior dimensions `0,0` and cannot read cross-world depth.
   Only after Phase 6 returns its accepted/duplicate frame result, call the current Phase 9 held-
   hands operation with that exact `FrameBeginSignal`/runtime generation; glue samples both hands
   once, Phase 9 resolves them, and its Phase 6 sink update completes before shadow or any gbuffers
   activation. A missing/rejected held provider writes the typed zero tuple for this frame and
   disables only held delivery; a stale Phase 9 generation rejects the shader frame.
2. **Before ordinal-zero `GlStateManager.clear`:** normalize only vanilla cached state required for
   a known starting point. No engine buffer is cleared here.
3. **After that clear:** sample `BufferRuntimeInputs`; refresh the borrowed main-depth identity; if
   resizing/replacement is required, abandon this shader frame and perform the safe-point rebuild
   before a later frame. Otherwise call Phase 5 `beginFrame`. Buffer construction during initial/off
   preparation has no completed prior framebuffer to sample; the first active frame passes `0,0`.
4. **After the invocation of `setupCameraTransform(FI)V`:** `FrameHookBridge` copies current
   model-view and projection values into `CameraSnapshot` and calls Phase 6
   `captureGbufferMatrices(frameId, modelView, projection)` exactly once. The Phase-6 provider
   adapter has already retained the copied `FrameUniformSample` and its same-sample `skyAngle`
   under this world/frame identity; it never calls Minecraft again to prepare the shadow view.
5. **Around the exact `RenderGlobal.setupTerrain(...)` invocation:** H-FRAME-05 captures the integer
   token actually passed to vanilla, invokes the original exactly once, then constructs
   `ShadowFrameView(worldEpoch,frameId,partialTicks,mainTerrainFrameToken,cameraPosition,skyAngle,
   sunAngle)`. It rejects a missing/mismatched provider sample before opening shadow execution.
   For an installed Phase 8 slot, the driver creates one opaque execution identity, reads the
   slot's current epoch, calls `ShadowExecutionBridge.open`, and passes the returned borrowed view
   in `ShadowInvocationContext`. One `finally` closes that exact view before the method may advance;
   failed close is a terminal backend failure. At v0.1 the explicit `NotInstalled` slot opens no
   execution; at v0.2 Phase 8 owns its complete invocation result handling.
6. **After the shadow slot:** bind the main estate, execute Phase 5's one clear plan using the current
   fog RGB, and enter `GBUFFERS`. This order follows RESEARCH's shadow-before-main-clear flow,
   even though the Pintonium implementation clears earlier.
7. Start sky/terrain drawing only after every prior step has a successful closed result.

Display resize, render-quality change, pack reload, and dimension change never rebuild mid-draw.
They mark `QUIESCING`; the current healthy frame finishes when possible, or renders vanilla only,
then replacement happens at the no-draw boundary. The next active frame samples before any further
clear. A main-depth same-extent `Reattached` invalidates snapshots; the driver abandons/reacquires as
Phase 5 requires. `ResizeRequired`/`Failed` enters the off/rebuild path with no shader draw.

### 4.4 Scoped gbuffers routing and push/pop

`PhaseDispatchTable` is immutable per Phase-4 registry fingerprint. Each entry contains the exact
`RenderSection`, requested `ProgramSlotId`, contained gbuffers `StageStep`, and whether the phase is
allowed before/after deferred. It is validated at pipeline preparation against
`StageRegistry.named`; a missing legal slot is left to Phase 4's fixed/skip terminal rather than
removed from the table.

Entering a scope performs:

1. if a parent Phase-5 snapshot is open, `completePass(parentSnapshot)` and retain only the logical
   parent phase on `NestedRenderScopeStack`;
2. get the requested slot's `PassDescriptor` from the current contained StageStep and the entire
   `ResolvedProgramDescriptor` from the published view;
3. call Phase 5 `snapshot(pass, resolved)`; draw only on `Acquired`;
4. bind exactly its `PassDrawTarget`, color attachments, and readable textures;
5. ask the current Phase-4 `FrameBarrierContexts` for `activation(step, shadowPass)` and call the
   barrier with the requested slot;
6. branch on every result: draw on `Activated`/`FixedFunction`, omit only the named operation on
   `Skipped`, publish off on `ShadersOff`, abort/off on `FailedSafe`, reacquire on
   `StalePublication`; and
7. apply phase-local reversible state such as cull/depth/matrix policy only after activation.

Closing reverses local state, requires `completePass(Completed)`, then reacquires and reactivates the
suspended parent. Thus leash, glint, eyes, beams, and sky-textured work are real push/pop operations,
not a global “current program” assignment. A leaked nested scope is diagnosed and drained by
`finish`; if it has an open Phase-5 snapshot, the frame is aborted rather than guessing.

During a Phase 8 traversal, the authenticated `ShadowExecutionView` is the sole guard for bypassing
Phase 7's main-snapshot policies. H-TERRAIN, H-ENTITY, H-CLOUD, and H-FRUSTUM validate that the
current bridge is active for the exact execution identity and slot epoch before returning their
vanilla operation unchanged; they neither open a gbuffers scope nor apply the main frame's
cloud/frustum flags in that dynamic extent. A missing, foreign, stale, wrong-thread, or closed view
does not bypass anything. The Phase 8 call then requests `shadowPass=true` from its own Phase-4
barrier context, which forces root `shadow` before backup resolution. Phase 7 never implements its
own shadow override, and another mod's direct vanilla call can never activate this guard.

At v0.3 the active pipeline also lends Phase 9's `RenderLayerLookup` to the loader-side block-state
classification adapter. Glue translates the exact state to the current dense ordinal and applies
only the returned `SOLID`/`CUTOUT`/`CUTOUT_MIPPED`/`TRANSLUCENT` decision; absence preserves
vanilla. H-TERRAIN still receives the resulting vanilla `BlockRenderLayer` and never resolves a
name, property, tag, or alias on the draw path. The lookup generation is stamped into the compiled
product, and §5.3's invalidation gate prevents an old classification from reaching a new frame.

### 4.5 Depth-copy and deferred trigger

H-WEATHER calls `beforeWeather` immediately before weather draws. At v0.5 this requests
`copyDepth(PRE_WEATHER, frameId)` and handles all four Phase 5 outcomes: `Copied` continues,
`DuplicateIgnored` continues with the diagnostic, `BackendDegraded` continues with Phase 5's
depthtex0 fallback, and `Rejected` aborts. Because the current Phase 5 order is contradictory,
R7-1 must land before this call is enabled.

At the **four-argument** `RenderGlobal.renderBlockLayer` HEAD for `TRANSLUCENT`, H-TERRAIN-02:

1. closes any opaque gbuffers scope;
2. at v0.5 requests `copyDepth(PRE_TRANSLUCENT, frameId)` and handles its closed result;
3. executes Phase 5's virtual `deferred_pre` transition through requested R7-2;
4. traverses every populated Phase-4 `DEFERRED/BETWEEN_GBUFFERS` raster descriptor in ascending
   sparse order via §4.6;
5. restores the blocks atlas and vanilla blend posture through `FrameRenderPort`; and
6. enters `gbuffers_water` before the original translucent-layer body.

Architecture and deferred pass execution are v0.1; actual copied-depth calls are v0.5 as assigned.
Before v0.5, Phase 5's published fixed binding fallback for unavailable copied-depth targets remains
authoritative; Phase 7 does not bind stale storage.

The reference's hand-time depthtex2 copy is explicitly not used. It would contradict the
before-weather contract. Center-depth is also not read here: Phase 6 already reads the previous
completed image in step 1 of §4.3.

### 4.6 Deferred/composite/final executor

`FullscreenPassExecutor` handles a Phase-4 raster descriptor as one transaction:

1. resolve the effective provider once; never re-run the backup chain;
2. acquire the Phase-5 `PassBufferSnapshot` before any attachment or texture bind;
3. pass the snapshot's Phase-5-owned draw target unchanged to `FrameRenderPort.bind` with the
   current vanilla anaglyph mask; `PassDrawTarget.Screen.INSTANCE` carries no engine handle;
4. establish identity model-view and orthographic 0…1 projection; disable fog/depth/blend, set
   depth writes off, and retain a cache-coherent restoration lease;
5. bind each snapshot-readable texture through the fixed Phase 5 table. Phase 13's optional overlay
   is leased and merged only through Phase 5's published `textureBindings` contract; missing values
   degrade locally, absent values are skipped, and every lease/snapshot is closed;
6. for each `PassResourceAccess.mipmappedBeforeRead` logical texture, generate mipmaps for the exact
   snapshot side before activation/draw; a failure disables that mipmap feature only (rung 2a);
7. activate the exact requested slot through Phase 4, thereby re-pointing samplers and refreshing
   built-in/custom uniforms in the fixed Phase 6 participant order;
8. apply `ViewportScale`: for non-negative normalized values, origin and extent use mathematical
   floor (equivalent to truncation); clamp an otherwise zero positive-scale extent to one pixel and
   validate it remains inside the main target. The default is the whole target;
9. choose `FullscreenPrimitive.QUADS` when the captured capability profile supports it, otherwise
   the retained four-vertex triangle-strip fallback;
10. draw exactly `instanceCount` times. Before draw `i`, call Phase 6 `updateInstanceId(i)`; always
    restore `instanceId=0` after the loop. v0.1 count is one; the generic loop enables at v0.5;
11. restore viewport/matrix/fog/depth/blend/alpha state in `finally`; and
12. call Phase 5 `completePass`; advance only on `Completed`, otherwise abort the frame.

Virtual `deferred_pre`/`composite_pre` descriptors execute no barrier or draw. They request only the
Phase-5-owned transition in R7-2. An absent sparse raster descriptor is never visited. A Phase-4
`Skipped` result does not complete a never-drawn flip transition; if a snapshot was already acquired,
the driver aborts it rather than commit.

At frame end:

1. close the last gbuffers scope;
2. execute virtual `composite_pre`;
3. traverse populated `COMPOSITE/FRAME_END` descriptors in ascending sparse order;
4. execute `FINAL/SCREEN` exactly once. If the final shader is absent and Phase 4 resolves fixed
   function, draw the same colortex0 fullscreen passthrough through the fixed-function terminal;
5. after Phase 5 reports final `Completed`, fire `FrameCompletionObserver.beforePresent` and the
   optional CaptureAgent frame grab;
6. commit Phase 5, release Phase 4 to fixed function with the issued release context, and restore
   every remaining platform lease.

The observer is after final pixels exist and before Minecraft presents. `RenderWorldLastEvent` is
not used: the Cleanroom patch dispatches it before hand rendering
(`reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:258`–`:266`),
so it cannot satisfy Phase 2's moment.

### 4.7 Internal default pack and shaders-off

`BuiltInPassthroughPack` implements only Phase 3 `InternalPackSource`, with a stable identity and one
finite, normalized, directory-aware snapshot. It stops immediately at the Phase-3-provided
`PackInputLimits`; bytes are immutable and copied on every `ImmutableBytes.copy()` call.

The clean-room-authored GPL resource set contains:

| Source pair | Semantics |
|---|---|
| `shaders/gbuffers_basic.vsh/.fsh` | compatibility transform + vertex color to colortex0 |
| `shaders/gbuffers_textured.vsh/.fsh` | compatibility transform + base texture × vertex color |
| `shaders/gbuffers_textured_lit.vsh/.fsh` | base texture × lightmap × vertex color |
| `shaders/final.vsh/.fsh` | identity fullscreen input; copy colortex0 to the screen |
| `shaders/shaders.properties` | empty canonical properties surface; no hidden engine override |

All other classic slots intentionally resolve through Phase 4's documented fallback graph. The pack
declares no shadow, copied-depth, custom texture, extended attribute, deferred, composite, geometry,
or modern feature. It therefore tests the backup chain and the final handoff while remaining a true
passthrough. Source text is authored from the published GLSL/pack contract, not the OptiFine
decompile. Every successful snapshot exposes this same immutable corpus; caller limits only accept
or reject it and never truncate it or change its manifest, digest, or identity. Snapshot order, byte
content, manifest identity/paths, and digest are golden-tested.

`PackSelection.Internal` selects this content. `PackSelection.Off` does **not** select it: Phase 3's
successful `Off` result publishes shaders-off and leaves vanilla un-intercepted. A failed external or
internal load likewise goes off; there is no recursive “fallback to internal” loop and no hidden
change of user selection.

### 4.8 Dimension, pack, and resize lifecycle

`FrameHookBridge` is the sole writer of the internal `ActiveWorldIdentityPublication`. Before the
first H-FRAME-01 for a world object it atomically installs `(incremented worldEpoch,
DimensionKey)`; a dimension change atomically replaces that pair before that dimension's first
H-FRAME-01, and `WorldEvent.Unload` clears it before pipeline teardown. Thus no active identity
exists between unload and the next install, and a new world object increments `worldEpoch` even if
the numeric dimension repeats. At H-FRAME-01 the bridge reads one publication snapshot and copies
that snapshot's epoch and dimension, plus `logicalTick`, into `FrameBeginSignal`; it never derives
those identity fields from resize observations. The numeric dimension ID becomes Phase 3
`DimensionKey`; no namespaced/Iris folder rule is introduced.

Dimension change behavior is:

1. mark the active frame vanilla-only and quiesce the old pipeline;
2. reset Phase 9 per-draw dynamics, deactivate its publication, then release/close Phase 5/4
   through their publishers at the next no-draw safe point;
3. look up the new `DimensionKey` in the per-dimension cache;
4. explicit `DimensionMode.DISABLED` publishes off; an explicit override compiles only its `.vsh`/
   `.fsh` set; a missing entry selects base;
5. prepare/publish transactionally per §4.1; and
6. reset Phase 6 with a new world epoch so previous matrices/camera initialize current=current.

Pack selection, option changes, resource reload, resolution multiplier changes, registry remaps,
changed mod ID files, tag/alias-catalog changes, and hand-light policy changes use the same
`ShaderReloadController.request(ReloadRequest)` queue. `FMLModIdMappingEvent` submits a validated
active rebuild with `REGISTRY_REMAP`; resource callbacks use `RESOURCE_RELOAD`, and the Phase 9
source/catalog adapters add their narrower reasons when their fingerprints change. Selection-change
intents supersede older selection intents; active-rebuild intents coalesce only when their expected
active identity matches, while all trigger reasons are retained for diagnostics. A request accepted
during a frame is not executed until `finish`/abort establishes fixed-function, normalized state.
There is no GL destruction from a GUI/event thread.

Every accepted reason re-snapshots the Phase 9 registry/mod/tag/policy inputs before candidate
construction; equality may prove the resulting ID candidate unchanged, but the controller never
assumes that from the reason name. `RESOURCE_RELOAD` rebuilds Phase 3 and Phase 9 together.
`REGISTRY_REMAP` always advances the registry snapshot identity even when pack bytes are unchanged.
After Phase 9 accepts a changed generation, the transaction invokes the Phase 10/vanilla chunk and
layer invalidator before marking Active. Repeated reasons coalesce, but no new frame opens while
that invalidation is pending or failed.

Display/main-framebuffer change is observed by H-RESIZE. Phase 7 does not trust logical window size:
it samples the actual main framebuffer extent/version through `MainDepthSource` and Phase 5. On an
active frame, Phase 6 samples the prior depth first; Phase 7 then abandons that shader frame,
rebuilds/publishes at the safe point, and resumes shaders on a later frame. This avoids resizing the
source before center-depth sampling.

### 4.9 Engine-flag execution

`FrameFlagResolver` freezes one `ResolvedFrameFlags` per configuration fingerprint and relevant
video-setting revision. It never consults properties on a hot hook. Video-setting precedence is
applied only where an actual corresponding setting exists; otherwise Phase 3's tri-state controls.

Every mutation uses a scope lease:

- back-face flags suspend culling for the named terrain call and restore the exact prior state;
- rain/beacon depth flags suspend depth testing only for their draw scope, never depth writes outside
  it;
- sun/moon suppression skips the corresponding tessellation call, not merely its texture bind;
- underwater/vignette use the cancellable Forge events in §4.11 and do not cancel unrelated overlay
  elements;
- cloud policy intercepts the exact `GameSettings.shouldRenderClouds()` query within
  `RenderGlobal.renderClouds`, preserving modded provider early-return behavior from the Cleanroom
  patch;
- frustum-culling FALSE redirects only the catalogued `ICamera.isBoundingBoxInFrustum` calls inside
  world setup/entity/block-entity paths. It does not globally replace `ICamera` or affect Phase 8's
  shadow frustum.

An invalid/missing hook disables that flag's effect (rung 2a), except a mismatched state-restore
anchor is critical and turns shaders off. Default/unset always delegates to vanilla.

---


### 4.10 Complete hook catalog

#### 4.10.1 Catalog notation and hook-health classes

Every Mixin target below uses the SRG name **and** descriptor. `HEAD/RETURN` pairs use an
engine-issued token retained in the bridge's render-thread scope stack; RETURN is the normal close
path, while H-FRAME-00's outer `finally` detects, aborts, and drains a scope leaked by a throw.
No mixin instance field owns policy or a scope. `AROUND` means a redirect of one named invocation to
a dumb bridge that calls the original operation inside `try/finally`. `AFTER INVOKE` means `@At(value="INVOKE",
target=..., shift=AFTER)`. Ordinals are accepted only where the Cleanroom patch or the OQ-4
application test proves that exact ordinal.

The catalog has four failure classes:

| Class | Application policy | Runtime policy |
|---|---|---|
| `CORE` | all core anchors form one Mixin health group; any missing or over-matched anchor disables shader rendering for the session | one diagnostic, rung 3 (`Off`), vanilla rendering continues |
| `FEATURE` | each balanced feature family is its own group | disable that program family/flag only (rung 2a), restore vanilla state |
| `OBSERVER` | optional state observer, no cancellation | retain the last valid/default Phase 6 value and diagnose once |
| `DEFERRED(Pn)` | a wholly deferred row has no Phase 7 mixin; on a mixed row only the owner-phase capability or augmentation is absent, while its active Phase 7 hook remains | no false capability claim; the owner phase activates and health-checks its portion |

All Phase 7 mixins use `require = 0`, normally `expect = 1`; the Mixin configuration plugin audits
actual application counts against this catalog after preparation. This makes optional application
non-crashing without making a missing core hook invisible. The registry's coexistence bail-out is
evaluated before the group is enabled (§4.12). The policy satisfies the degradation rule without
depending on Mixin's default fatal-injection behavior.

#### 4.10.2 Bootstrap and core frame transaction

| ID / class | SRG target and injection | Dumb bridge call | Health / evidence |
|---|---|---|---|
| H-BOOT-01 `GameSettings` | `func_74300_a()V` (`loadOptions`) RETURN | `BootstrapHooks.onEarlyConfigurationLoaded()` | `CORE`; Phase 1 owns the three-stage sequence |
| H-BOOT-02 `OpenGlHelper` | `func_77474_a()V` (`initializeTextures`) RETURN | `BootstrapHooks.onGlReady()` | `CORE`; no GL work is attempted before it |
| H-BOOT-03 `GuiMainMenu` | `func_73866_w_()V` (`initGui`) RETURN, once | `BootstrapHooks.onClientLoadingComplete()` | `FEATURE`; shader startup may remain deferred |
| H-FRAME-00 `EntityRenderer` | `func_181560_a(FJ)V`, AROUND its `func_78471_a(FJ)V` (`renderWorld`) invocation | `WorldRenderBoundary.invoke(original, partialTicks, finishTimeNano)` | `CORE`; the outer `finally` is the early-exit guarantee |
| H-FRAME-01 `EntityRenderer` | `func_175068_a(IFJ)V` HEAD | `FrameHooks.open(pass, partialTicks, finishTimeNano)` | `CORE`; Pintonium row 1 and App E row 1 (`docs/research/v1/RESEARCH.md:1398`) |
| H9-HELD-01 accepted H-FRAME-01 boundary | no additional Mixin; immediately after Phase 6 accepts the copied frame identity | call current Phase 9 held sampler/resolver once and complete `updateHeldItems` before any program activation | `DEFERRED(P9,v0.3)` until installed, then `FEATURE`; zero tuple on provider failure, stale generation aborts the shader frame |
| H-FRAME-02 `EntityRenderer` | `func_175068_a(IFJ)V`, BEFORE `GlStateManager.clear(I)V`, ordinal 0 | `FrameHooks.normalizeVanillaState(frameToken)` | `CORE`; validated Pintonium row 2 |
| H-FRAME-03 `EntityRenderer` | same invocation, shift AFTER | `FrameHooks.afterFirstClear(frameToken)` | `CORE`; creates/refreshes Phase 5 frame resources but does not claim matrices |
| H-FRAME-04 `EntityRenderer` | `func_175068_a(IFJ)V`, AFTER INVOKE `func_78479_a(FI)V` (`setupCameraTransform`) | `FrameHooks.captureMainCamera(frameToken)` | `CORE`; exact post-camera point, once per frame |
| H-FRAME-05 `EntityRenderer` | `func_175068_a(IFJ)V`, AROUND the exact `RenderGlobal.func_174970_a(Entity,D,ICamera,I,Z)V` invocation | call original once with its exact arguments; on normal return retain its integer terrain token and call `FrameHooks.invokeShadowSlotThenRestoreMain(frameToken,terrainToken)` inside an execution scope whose close is in `finally` | `CORE`; Phase 8 slot, Pintonium row 4 |
| H-FRAME-06 `EntityRenderer` | `func_175068_a(IFJ)V` TAIL | `FrameHooks.finishNormal(frameToken)` | `CORE`; idempotent normal-return fast path |
| H-FRAME-07 `EntityRenderer` | `func_181560_a(FJ)V` wrapper's `finally` after H-FRAME-00 | `FrameHooks.finishGuaranteed(frameToken, exitKind)` | `CORE`; strictly stronger than Pintonium's TAIL-only behavior (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:195`–`:197`) |

H-FRAME-03 deliberately does not implement the governing text's matrix-capture claim at the clear
anchor. Cleanroom visibly places the clear before camera setup
(`reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:175`–`:179`),
so H-FRAME-04 is the first valid capture point. Sampling/previous-frame rotation already happened
at H-FRAME-01, before H-FRAME-03 can resize or clear, preserving the Phase 6 ordering contract.

#### 4.10.3 Sky, terrain, damage, and line scopes

| ID / class | SRG target and injection | Engine action | Health / evidence |
|---|---|---|---|
| H-SKY-01 `RenderGlobal` | `func_174976_a(FI)V` HEAD/RETURN | enter/exit `gbuffers_skybasic` | `FEATURE`; **no working reference**; App E row 2 (`docs/research/v1/RESEARCH.md:1399`) |
| H-SKY-02 `RenderGlobal` | same method, REDIRECT `World.func_72826_c(F)F` | call original, publish the returned celestial angle, return it unchanged | `OBSERVER`; concrete celestial-rotation sub-site; **no working reference** |
| H-SKY-03 `RenderGlobal` | same method, slice-bounded redirects of the sun and moon `Tessellator.func_78381_a()V` draws | enter `gbuffers_skytextured`, honor `sun`/`moon`, call or suppress draw, restore sky-basic | `FEATURE`; ordinals must be fixed by OQ-4; **no working reference** |
| H-TERRAIN-01 `RenderGlobal` | `func_174977_a(BlockRenderLayer,D,I,Entity)I` HEAD/RETURN, only for SOLID/CUTOUT_MIPPED/CUTOUT | while an authenticated Phase 8 execution is active, bypass main-snapshot policy and preserve the vanilla call; otherwise select `gbuffers_terrain_solid`, `gbuffers_terrain_cutout_mip`, or `gbuffers_terrain_cutout` and apply scoped `backFace.*` | `CORE`; actual world-loop overload at `reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:200`–`:206` |
| H-TERRAIN-02 `RenderGlobal` | the same exact method HEAD/RETURN, only for `TRANSLUCENT` | while authenticated shadow execution is active, bypass main copy/deferred/water policy; otherwise at HEAD perform PRE_TRANSLUCENT copy/virtual/deferred sequence then enter `gbuffers_water`, and at RETURN close/restore | `CORE`; Pintonium row 5; copy remains disabled until Phase 5 order is corrected |
| H-DAMAGE-01 `RenderGlobal` | `func_174981_a(Tessellator,BufferBuilder,Entity,F)V` HEAD/RETURN | enter/exit `gbuffers_damagedblock` | `FEATURE`; MCP-validated supporting hook |
| H-LINE-01 `RenderGlobal` | `func_72731_b(EntityPlayer,RayTraceResult,I,F)V` HEAD/RETURN | enter/exit `gbuffers_basic` | `FEATURE`; no event is used because `DrawBlockHighlightEvent` has no post scope |

The one-argument `RenderGlobal.func_174982_a(BlockRenderLayer)V` in Appendix E is retained in the
symbol ledger but receives no Phase 7 injection: it is not the world-loop call site. This is the
explicit U7-1 correction request, not an unreported substitution.

#### 4.10.4 Entities, block entities, and balanced nested effects

| ID / class | SRG target and injection | Engine action | Health / evidence |
|---|---|---|---|
| H-ENTITY-01 `RenderGlobal` | `func_180446_a(Entity,ICamera,F)V` HEAD/RETURN; REDIRECT its `RenderManager.func_178632_c(Z)V` calls | while authenticated shadow execution is active, bypass the main entity/glowing program policy and preserve vanilla calls; otherwise enter/exit `gbuffers_entities`, call the original outline toggle, and push/pop `gbuffers_entities_glowing` | `FEATURE`; App E row 2 |
| H-ENTITY-02 / H9-ENTITY-ID-01 `RenderManager` | `func_188388_a(Entity,F,Z)V` and `func_188391_a(Entity,D,D,D,F,F,Z)V` HEAD/RETURN | within the already-selected entity program: HEAD maps the exact entity ordinal, pushes prior `entityId`, and publishes mapped-or-zero; RETURN validates/restores the Phase 9 LIFO token. Nested calls of one overload from the other remain nested ID scopes | `DEFERRED(P9,v0.3)` until installed, then `FEATURE`; App E row 13 (`docs/research/v1/RESEARCH.md:1410`); mismatch disables ID delivery and writes zero, not the surrounding entity program |
| H-ENTITY-03 / H9-BLOCK-ENTITY-ID-01 `TileEntityRendererDispatcher` | `func_147549_a(TileEntity,D,D,D,F)V` HEAD/RETURN | HEAD first opens `gbuffers_block`; only an accepted program scope may obtain the TE state ordinal, push prior `blockEntityId`, and publish mapped-or-zero. RETURN restores the ID token **before** closing the Phase 7 program scope | `FEATURE` for program scope; ID augmentation is `DEFERRED(P9,v0.3)` until installed then `FEATURE`; App E row 14 (`docs/research/v1/RESEARCH.md:1411`); detached/unknown TE publishes zero |
| H-LEASH-01 `RenderLiving` | `func_110827_b(EntityLiving,D,D,D,F,F)V` HEAD/RETURN | suspend parent snapshot, push `gbuffers_basic`, then reacquire parent | `FEATURE`; required push/pop semantics |
| H-GLINT-01 `LayerArmorBase` | `func_188364_a(RenderLivingBase,EntityLivingBase,ModelBase,F,F,F,F,F,F,F)V` HEAD/RETURN | suspend parent, push `gbuffers_armor_glint`, then reacquire parent | `FEATURE`; required push/pop semantics |
| H-EYES-01 `LayerSpiderEyes`, `LayerEndermanEyes`, `LayerEnderDragonEyes` | each `func_177141_a(<concrete entity>;FFFFFFF)V` HEAD/RETURN: `EntitySpider`, `EntityEnderman`, or `EntityDragon` descriptor respectively | suspend parent, push `gbuffers_spidereyes`, then reacquire parent | `FEATURE`; bridge overloads taking `EntityLivingBase` are excluded by descriptor |
| H-BEAM-01 `TileEntityBeaconRenderer` | both `func_188204_a(DDDDDDII[F)V` and `func_188205_a(DDDDDDII[FDD)V` HEAD/RETURN | enter/exit `gbuffers_beaconbeam`; scoped `beaconBeamDepth` | `FEATURE` |
| H-BEAM-02 `RenderDragon` | `func_188325_a(DDDFDDDIDDD)V` HEAD/RETURN | enter/exit `gbuffers_beaconbeam`; scoped `beaconBeamDepth` | `FEATURE`; Pintonium supporting hook |
| H9-COLOR-01 `RenderLivingBase` | within `func_177092_a(EntityLivingBase,F,Z)Z`, redirect the exact `GlStateManager.func_187448_b(IILjava/nio/FloatBuffer;)V` invocation | when `(target,pname)` is exactly `(GL_TEXTURE_ENV,GL_TEXTURE_ENV_COLOR)`, duplicate the buffer, copy four floats from its current position into the current Phase 9 color scope, then always call the original unchanged | `DEFERRED(P9,v0.3)` until installed, then `OBSERVER`; exactly one injection anchor, runtime observation once only on the true/effect path |
| H9-COLOR-02 `RenderLivingBase` | `func_177091_f()V` RETURN | pop/restore the current Phase 9 color scope after vanilla unsets the effect; neutral when no outer scope remains | `DEFERRED(P9,v0.3)` until installed, then `OBSERVER`; frame reset is the throw fallback |

`PassScopeStack` never asks Phase 5 for two simultaneous open snapshots. Push completes the parent
snapshot without applying its flip set, acquires/binds the child, and records the parent's effective
provider. Pop completes the child normally and reacquires that provider through the Phase 4 barrier.
Any rejection aborts the shader frame. An AROUND hook restores in its local `finally`; a
HEAD/RETURN hook leaked by a throw is drained and restored by the H-FRAME-00 boundary before the
original throwable escapes.

The Phase 9 augmentation order is one reverse-close stack, not three independent callbacks. On
normal entity/TE RETURN it restores color, then entity/block-entity ID, then the Phase 7 program
scope implicated by that call. On any throw or terminal frame path, H-FRAME-00 asks Phase 9 to
`resetFrame` before Phase 7 drains program scopes or releases fixed function. The mixins retain only
call-local opaque tokens/ordinals; alias lookup, nesting, diagnostics, and neutral writes remain in
Phase 9.

H9-COLOR-01 never queries TexEnv after the call and never reconstructs vanilla hurt/creeper
formulas. Glue validates four readable floats at the duplicate's current position, finite-checks
them, and neither mutates nor retains the original buffer. A wrong target/pname, short buffer,
missing/extra observation, or stale scope forwards the original call but disables color delivery
for that frame and publishes neutral. This is the exact operand/current-scope refinement required
by R9-2; the former RETURN-only H-COLOR description is superseded.

#### 4.10.5 Particles, clouds, weather, border, hand, and overlays

| ID / class | SRG target and injection | Engine action | Health / evidence |
|---|---|---|---|
| H-PARTICLE-01 `ParticleManager` | `func_78874_a(Entity,F)V` HEAD/RETURN | enter/exit `gbuffers_textured` | `FEATURE`; App E row 15 (`docs/research/v1/RESEARCH.md:1412`) |
| H-PARTICLE-02 `ParticleManager` | `func_78872_b(Entity,F)V` HEAD/RETURN | enter/exit `gbuffers_textured_lit` | `FEATURE`; MCP-validated companion method |
| H-CLOUD-01 `RenderGlobal` | `func_180447_b(F,I,D,D,D)V` HEAD/RETURN | while authenticated shadow execution is active, bypass the main cloud flag/program policy; otherwise honor resolved `clouds` and enter/exit `gbuffers_clouds` | `FEATURE`; **no working reference** |
| H-CLOUD-02 `GameSettings` | `func_181147_e()I` RETURN modifier, active only during H-CLOUD-01 | return the Phase 3-resolved FAST/FANCY mode; OFF is handled before draw | `FEATURE`; vanilla mode query otherwise unchanged |
| H-WEATHER-01 `EntityRenderer` | `func_78474_d(F)V` HEAD/RETURN | PRE_WEATHER copy, enter/exit `gbuffers_weather`, apply scoped `rainDepth` | `CORE` copy / `FEATURE` routing; **no working reference** |
| H-BORDER-01 `RenderGlobal` | `func_180449_a(Entity,F)V` HEAD/RETURN | enter/exit `gbuffers_textured_lit` | `FEATURE`; App A.1 world-border mapping |
| H-HAND-01 `EntityRenderer` | `func_78476_b(F,I)V`, AROUND `ItemRenderer.func_78440_a(F)V` | invoke the original through `HandSubpassBridge` once with SOLID mask and once with TRANSLUCENT mask; apply depth-scale only to the latter | `CORE`; Pintonium row 6 moment, with reference depth timing rejected |
| H-HAND-02 `ItemRenderer` | inside `func_78440_a(F)V`, REDIRECT calls to `func_187462_a(EntityLivingBase,ItemStack,TransformType,Z)V`, `func_187456_a(F,F,EnumHandSide)V`, `func_187463_a(F,F,F)V`, `func_187461_a(ItemStack)V`, and `func_187465_a(F,EnumHandSide,F,ItemStack)V` | `HandContentClassifier` supplies a copied SOLID/TRANSLUCENT category; call each original only in its matching mask, with bare arms/maps classified SOLID | `FEATURE`; every target/descriptor MCP-validated; no policy in mixin |
| H-OVERLAY-01 `ItemRenderer` | `func_78447_b(F)V` HEAD/RETURN | acquire/release draw-buffers-none route around first-person overlays | `FEATURE`, **gated by R7-3**; until granted, vanilla overlay path remains |
| H-OVERLAY-02 Forge event bus | cancelable `RenderBlockOverlayEvent` only for `WATER` when `underwaterOverlay == FALSE` | no program switch | `FEATURE`; event preferred over a mixin |
| H-OVERLAY-03 Forge event bus | cancelable `RenderGameOverlayEvent.Pre` only for `VIGNETTE` when `vignette == FALSE` | no program switch | `FEATURE`; event preferred over a mixin |

The hand bridge never owns the center-depth read or the weather copy. It consumes only the already
sampled Phase 6 value and the two balanced hand routes, preserving the authoritative ordering at
`docs/research/v1/RESEARCH.md:557`–`:561`.

#### 4.10.6 State observation and restoration

| ID / class | SRG target and injection | Engine action | Health |
|---|---|---|---|
| H-BLEND-01 `GlStateManager` | `func_179147_l()V`, `func_179084_k()V`, `func_187401_a(SourceFactor,DestFactor)V`, `func_179112_b(I,I)V`, `func_187428_a(SourceFactor,DestFactor,SourceFactor,DestFactor)V`, `func_179120_a(I,I,I,I)V` RETURN | publish exact blend enabled/function state to Phase 6 | `OBSERVER`; App E row 16 (`docs/research/v1/RESEARCH.md:1413`) |
| H-FOG-01 `GlStateManager` | `func_179127_m()V`, `func_179106_n()V`, `func_187430_a(FogMode)V`, `func_179093_d(I)V`, `func_179095_a(F)V`, `func_179102_b(F)V`, `func_179153_c(F)V` RETURN | publish exact fog state | `OBSERVER` |
| H-FOG-02 Forge event bus | `EntityViewRenderEvent.FogColors`, `FogDensity`, and `RenderFogEvent` | publish fog color/mode/density at event fidelity | `OBSERVER`; event preferred |
| H-STATE-01 `GlStateManager` | `func_179135_a(Z,Z,Z,Z)V`, `func_179089_o()V`, `func_179129_p()V` RETURN | update the bridge's restoration ledger only | `OBSERVER`; driver policy remains engine-side |
| H-FRUSTUM-01 `RenderGlobal` | within `func_174970_a(Entity,D,ICamera,I,Z)V` and `func_180446_a(Entity,ICamera,F)V`, REDIRECT only `ICamera.func_78546_a(AxisAlignedBB)Z` calls | authenticated shadow execution bypasses only the main `frustumCulling` override and delegates to Phase 8's supplied camera; otherwise delegate normally unless `frustumCulling == FALSE`, then return visible | `FEATURE`; no global `ICamera` replacement |

The observer callbacks receive primitives/enums translated by `mod.glue`; no Minecraft, Forge,
LWJGL, or Mixin type crosses into `:engine`. Driver-applied state changes carry an internal guard so
observers report the effective state once rather than recursively re-entering policy.

#### 4.10.7 Resize, framebuffer, world lifetime, and capture

| ID / class | SRG target and injection | Engine action | Health / evidence |
|---|---|---|---|
| H-RESIZE-01 `Minecraft` | `func_147119_ah()V` (`updateFramebufferSize`) RETURN | offer actual framebuffer width/height to `ResolutionCoordinator` | `FEATURE`; fallback OQ-3 path |
| H-RESIZE-02 `Minecraft` | `func_71370_a(I,I)V` (`resize`) RETURN | mark window/display extent changed; actual FBO size still comes from H-RESIZE-01 | `FEATURE`; keeps HiDPI dimensions distinct |
| H-FBO-01 `Framebuffer` | `func_147613_a(I,I)V` (`createBindFramebuffer`) RETURN | increment vanilla-FBO attachment epoch and schedule next safe-boundary rebuild | `CORE`; App E row 17 (`docs/research/v1/RESEARCH.md:1414`) |
| H-FBO-02 `Framebuffer` | `func_147610_a(Z)V` (`bindFramebuffer`) RETURN and `func_147615_c(I,I)V` (`framebufferRender`) HEAD/RETURN | observe final target/presentation boundaries; never retain the vanilla handle beyond its epoch | `OBSERVER`; capture occurs before presentation |
| H-WORLD-01 Forge event bus | `WorldEvent.Unload` for the client world | abort open frame, demote the dimension pipeline to plan-only, clear temporal world state | `CORE`; event preferred over `WorldClient` mixin (App E row 18) |
| H-CAPTURE-01 frame bridge | after `final` completes and before H-FBO-02 presentation | invoke the optional installed `FrameCaptureListener` once with the borrowed view | `FEATURE`; Phase 2 R11; absent listener disables capture |
| H-CAPTURE-02 `Minecraft` | clean client shutdown entry `func_71400_g()V`, invoked through main-thread scheduling | terminate after the final artifact commit | `FEATURE`; Phase 2 R14; no timeout kill |

Resize never mutates an open Phase 5 generation. The coordinator records the newest actual FBO
extent, aborts the current shader frame if an allocation epoch changes mid-frame, and performs one
candidate rebuild/publication at the next H-FRAME-01 boundary. The vanilla render continues during
that degraded frame.

#### 4.10.8 Appendix E owner/deferral ledger (all 18 rows)

This ledger is the completeness proof for Appendix E's class catalog
(`docs/research/v1/RESEARCH.md:1396`–`:1415`). A deferred row is intentionally **not** a Phase 7
injection specification; it records the exact owner and milestone so later work cannot mistake
absence for omission.

| App E row | Class | Phase 7 disposition |
|---:|---|---|
| 1 | `EntityRenderer` | H-FRAME, H-WEATHER, H-HAND implemented v0.1; H9-HELD-01 accepted-frame augmentation activates with Phase 9 at v0.3 |
| 2 | `RenderGlobal` | H-SKY, H-TERRAIN, H-DAMAGE, H-LINE, H-ENTITY, H-CLOUD, H-BORDER; implemented v0.1 |
| 3 | `RenderChunk.func_178581_b(FFF,ChunkCompileTaskGenerator)V` | `DEFERRED(P10,v0.3)`: chunk-build entity-data push/pop |
| 4 | `ChunkRenderDispatcher` | `DEFERRED(P10,v0.3)`: async build-context propagation; exact method chosen by Phase 10 |
| 5 | `BufferBuilder.func_181668_a(I,VertexFormat)V`, `func_181675_d()V`, `func_178981_a([I)V` | `DEFERRED(P10,v0.3)`: stamp attributes at vertex-write boundaries |
| 6 | `Tessellator.func_78381_a()V` | `DEFERRED(P10,v0.3)`: non-VBO draw path (sky's H-SKY redirects are scoped program/cancellation wrappers, not vertex interception) |
| 7 | `WorldVertexBufferUploader.func_181679_a(BufferBuilder)V` | `DEFERRED(P10,v0.3)`: client-array attribute pointers |
| 8 | `VertexBuffer.func_181722_a(ByteBuffer)V`, `func_177358_a(I)V` | `DEFERRED(P10,v0.3)`: VBO upload/draw attributes |
| 9 | `BlockModelRenderer.func_178267_a(IBlockAccess,IBakedModel,IBlockState,BlockPos,BufferBuilder,Z)Z` | `DEFERRED(P10,v0.3)`: per-block push/pop |
| 10 | `TextureMap.func_110551_a(IResourceManager)V`, `func_110571_b(IResourceManager)V` | `DEFERRED(P13,v0.5)`: companion atlas lifecycle and `atlasSize`; prefer `TextureStitchEvent.Pre/Post` where sufficient |
| 11 | `TextureAtlasSprite` class-level lifecycle | `DEFERRED(P13,v0.5)`: per-sprite companions; exact method chosen by Phase 13 |
| 12 | `ItemRenderer.func_78440_a(F)V` | H-HAND/H-OVERLAY; implemented v0.1 |
| 13 | `RenderManager.func_188388_a(Entity,F,Z)V`, `func_188391_a(Entity,D,D,D,F,F,Z)V` | H9-ENTITY-ID-01 is fully ordered/specified here and activates at v0.3; Phase 7's surrounding entity program scope remains H-ENTITY-01 |
| 14 | `TileEntityRendererDispatcher.func_147549_a(TileEntity,D,D,D,F)V` | H-ENTITY-03 program scope at v0.1; H9-BLOCK-ENTITY-ID-01's inside-open/before-close augmentation activates at v0.3 |
| 15 | `ParticleManager.func_78874_a(Entity,F)V` | H-PARTICLE; implemented v0.1 |
| 16 | `GlStateManager` class-level | H-BLEND/H-FOG/H-STATE implemented v0.1; the exact H9-COLOR-01 TexEnv operand redirect and H9-COLOR-02 reset activate at v0.3 |
| 17 | `Framebuffer.func_147610_a(Z)V` | H-FBO/H-RESIZE; implemented v0.1 |
| 18 | `WorldClient` class-level | Forge `WorldEvent.Unload` plus Phase 6 provider; no Phase 7 world mixin |

Hook need 11 remains `DEFERRED(P12,v0.4)`: pack selection/options/reload GUI, F3+R, and command
integration are pure mod-side work. Phase 7 exposes `ShaderReloadController`; it adds no GUI mixin.

### 4.11 Forge/Cleanroom event choices

| Need | Event decision | Reason |
|---|---|---|
| final trigger | reject `RenderWorldLastEvent` | Cleanroom dispatches it before first-person hand rendering, so it cannot guarantee composite/final after the full Phase 7 frame |
| hand scope | reject `RenderHandEvent` | it is a cancelable pre-event with no balanced post event; H-HAND-01 provides exact `try/finally` scope |
| selection line | reject `DrawBlockHighlightEvent` | pre/cancel only; no balanced post point for program restoration |
| water overlay | use `RenderBlockOverlayEvent(WATER)` | exact cancelable behavior; no program-scope requirement |
| vignette | use `RenderGameOverlayEvent.Pre(VIGNETTE)` | exact cancelable behavior |
| fog state | use `EntityViewRenderEvent` family plus minimal GL observers | events supply view-level values; GL observers preserve exact effective state |
| world teardown | use `WorldEvent.Unload` | exact lifecycle signal, avoids a `WorldClient` mixin |
| registry remap | use `FMLModIdMappingEvent` | enqueue `RebuildActive` with `REGISTRY_REMAP`; the callback never resolves IDs or publishes directly |
| atlas | defer with preference for `TextureStitchEvent.Pre/Post` | Phase 13 decides whether sprite-companion fidelity needs more |
| resize | no suitable event found | H-RESIZE/H-FBO implement the OQ-3 fallback |

The event disposition is based on the pinned Cleanroom/Forge event API queried for this design;
events are not assumed to provide a post point their contract does not expose.

### 4.12 Coexistence, application audit, and hot-path posture

Before Mixin application, Phase 1's compatibility registry supplies a closed outcome:
`Compatible`, `ReplaceableBackendDetected`, or `UnsafeRendererDetected(reason)`. Phase 7 consumes
that outcome and does not invent the Phase 10 policy. `UnsafeRendererDetected` prevents the CORE
group from applying and selects `Off`; `ReplaceableBackendDetected` may select a registered backend
adapter only when its capability contract covers §5.1. Otherwise it also selects `Off`. There is no
partial double-injection into a replaced chunk/world pipeline.

`HookApplicationReport` contains catalog ID, target description, expected count, actual count,
failure classes, deferred owner phase where applicable, and fallback. In addition to the Phase 7
rows above it always contains H9-HELD-01, H9-ENTITY-ID-01, H9-BLOCK-ENTITY-ID-01, H9-COLOR-01, and
H9-COLOR-02. Before Phase 9 activation those rows are `DEFERRED`, owner 9, expected/actual zero, and
`VANILLA`; after a verified Phase 9 participant is installed they are respectively 1/1 participant
registration, then 4/4, 2/2, 1/1, and 1/1 Mixin anchors, with `FEATURE` for held/IDs and
`OBSERVER` for color. Partial ID-row activation disables the whole ID/held feature group and
neutralizes its sinks; color observation degrades independently. No Phase 9 row is hidden merely
because its milestone is dormant.

Phase 8 health is nested rather than flattened into the primary row list. A verified Phase 8
contributor supplies exactly one owner-phase-8 `HookApplicationSubreport` copied from its immutable
`ShadowHookHealth`: the canonical fingerprint, aggregate `shadowEnabled` value, and all eight §4.13
rows in hook-ID order with their exact expected/actual counts and `HEALTHY|FEATURE_DISABLED`
disposition. The primary Phase 7 row list—including H-FRAME-05 and every H9 row—retains its existing
catalog IDs, target strings, order, counts, and failure classes. Before Phase 8 is installed there
is no owner-phase-8 subreport; that absence is explicit and cannot be interpreted as healthy shadow
capability. A Phase 8 plan/publication may enable only when its hook fingerprint equals the nested
subreport fingerprint and every Phase-8-required row is healthy. Other owner phases may later use
the same nested shape, with unique positive owner phase and unique fingerprint, without changing
Phase 7 row identities.

The report is frozen before first frame, printed once on the `schmaloogium.hooks` channel, and
included in capture diagnostics. The bridge measures aggregate nanoseconds/call count only when a
debug profiler is enabled; production hot paths allocate no report records and perform one token
check plus one interface call.

OQ-4 does not reopen the engine contract. It may replace a catalog row's injection form while
preserving its ID, moment, bridge call, balance, and failure class. Such a change is a Phase 7 doc
fix-up because exact hooks are binding here.

### 4.13 Capture agent and manifest boundary

`CaptureAgent` lives in `:mod`, because pixels, main-thread shutdown, and GL diagnostics are client
concerns. It consumes Phase 2's `schmaloogium.capture-plan/1` and emits
`schmaloogium.run-manifest/1`; it never moves MC or GL types into `:conformance`. At H-CAPTURE-01:

1. validate that the requested scene/shot matches the active plan and that readiness is true;
2. copy the finalized vanilla framebuffer with dimensions and anaglyph eye recorded explicitly;
3. atomically write the image, then serialize the manifest from immutable projections, copying
   the complete frozen primary hook rows and nested owner subreports without inferring capability;
4. acknowledge the shot only after both commits; and
5. schedule the next shot or H-CAPTURE-02 shutdown on the client thread.

Readiness is `pipeline publication active && N configured frames finalized && no open failure`, not
merely “main menu loaded.” A warming pipeline is reported as `WAITING`; timeout/wedge handling and
failure-manifest production remain Phase 2 runner policy.

The current dependencies do not expose all manifest projections. Phase 7 therefore defines no
parallel introspection and may emit only a schema-valid **failure** manifest until R7-4…R7-7 are
granted. In particular, GL attribution is copied only from Phase 2 R4A's replay-aware boolean; it is
never guessed from an operation label. Acquisition method, archive SHA-512, and licence are runner
fixture facts, so the runner must place them in an authenticated plan extension or merge them after
capture (R7-7).

---

## 5. Cross-phase interfaces

### 5.1 Interfaces and data contracts exposed by Phase 7

The public frame seam is deliberately split in two. `engine.frame` owns policy and transaction
state. `mod.glue.frame` adapts Minecraft/Forge events and the catalogued mixins to primitive or
engine-owned values. `mod.mixin.frame` contains no state beyond a call-local token. A future Kirino
integration replaces the hook producer and `FrameRenderPort`, not `FrameDriver`, the program
schedule, or any Phase 3–6 contract.

```java
public interface FrameHookSink {
    FrameOpenResult open(FrameBeginSignal signal);
    FrameStepResult beforeFirstClear(FrameToken token);
    FrameStepResult afterFirstClear(FrameToken token, MainDepthPreparation depth);
    FrameStepResult captureMainCamera(FrameToken token, CameraSnapshot camera);
    FrameStepResult afterTerrainSetup(FrameToken token);
    ScopeOpenResult enter(FrameToken token, RenderSection section);
    ScopeCloseResult exit(FrameToken token, ScopeToken scope);
    FrameFinishResult finish(FrameToken token, FrameExitKind exitKind);
    FrameAbortResult abort(FrameToken token, FrameAbortReason reason);
}

public record FrameBeginSignal(
    long worldEpoch, long logicalTick, double smoothingTimeTicks, float frameTimeSeconds,
    DimensionKey dimension, int vanillaPass, float partialTicks,
    Extent2i targetView, Extent2i priorCompletedFramebuffer, AnaglyphEye eye) {}

public record CameraSnapshot(
    Matrix4Value modelView, Matrix4Value projection) {}

public enum FrameExitKind { NORMAL, EARLY_RETURN, THROWN }
public enum FrameAbortReason {
    PROTOCOL_REJECTION, BACKEND_FAILURE, RESIZE_EPOCH, WORLD_CHANGE, HOOK_UNHEALTHY
}
public enum FrameState {
    SAMPLED, BUFFER_OPEN, MATRICES_CAPTURED, SHADOW_DONE, GBUFFERS,
    DEFERRED_DONE, GBUFFERS_TRANS, FINALIZING, COMMITTED
}
public record FailureId(String diagnosticId) {
    public FailureId {
        if (diagnosticId == null || diagnosticId.isBlank()) throw new IllegalArgumentException();
    }
}

public sealed interface FrameOpenResult {
    record Opened(FrameToken token) implements FrameOpenResult {}
    record VanillaOnly(FrameOpenRejection reason) implements FrameOpenResult {}
    record Failed(FailureId failure) implements FrameOpenResult {}
}
public enum FrameOpenRejection {
    WRONG_THREAD, FRAME_ALREADY_OPEN, STALE_PUBLICATION, NON_WORLD_PASS,
    MISSING_WORLD_OR_CAMERA, INVALID_INPUT, SHADERS_OFF
}

public sealed interface FrameStepResult {
    record Advanced(FrameState state) implements FrameStepResult {}
    record Rejected(HookRejection reason) implements FrameStepResult {}
    record Aborted(FrameAbortReason reason) implements FrameStepResult {}
    record Failed(FailureId failure) implements FrameStepResult {}
}
public sealed interface ScopeOpenResult {
    record Opened(ScopeToken scope, DrawDisposition draw) implements ScopeOpenResult {}
    record Rejected(HookRejection reason) implements ScopeOpenResult {}
    record Aborted(FrameAbortReason reason) implements ScopeOpenResult {}
    record Failed(FailureId failure) implements ScopeOpenResult {}
}
public enum DrawDisposition { DRAW_SHADER, DRAW_FIXED_FUNCTION, OMIT_OPERATION }
public enum HookRejection { WRONG_TOKEN, WRONG_ORDER, STALE_PUBLICATION, INVALID_SECTION }
public sealed interface ScopeCloseResult {
    record Closed(DrawDisposition resumedParent) implements ScopeCloseResult {}
    record Rejected(HookRejection reason) implements ScopeCloseResult {}
    record Aborted(FrameAbortReason reason) implements ScopeCloseResult {}
    record Failed(FailureId failure) implements ScopeCloseResult {}
}
public sealed interface FrameFinishResult {
    record Finalized(FinalizedFrame frame) implements FrameFinishResult {}
    record AlreadyTerminal() implements FrameFinishResult {}
    record Aborted(FrameAbortReason reason) implements FrameFinishResult {}
    record Failed(FailureId failure) implements FrameFinishResult {}
}
public sealed interface FrameAbortResult {
    record Aborted(FrameAbortReason reason) implements FrameAbortResult {}
    record AlreadyTerminal() implements FrameAbortResult {}
    record Failed(FailureId failure) implements FrameAbortResult {}
}

public interface IdDynamicsFrameSlot {
    IdAcceptedFrameResult afterUniformFrameAccepted(
        FrameToken frame, FrameBeginSignal signal, PublishedIdRuntime runtime);
    IdResetFrameResult resetFrame(
        FrameToken frame, PublishedIdRuntime runtime, IdFrameResetReason reason);
}
public sealed interface IdAcceptedFrameResult {
    record Completed() implements IdAcceptedFrameResult {}
    record HeldFeatureDisabled(FailureId failure) implements IdAcceptedFrameResult {}
    record Rejected(IdFrameRejection reason) implements IdAcceptedFrameResult {}
    record Failed(FailureId failure) implements IdAcceptedFrameResult {}
}
public sealed interface IdResetFrameResult {
    record Reset() implements IdResetFrameResult {}
    record AlreadyNeutral() implements IdResetFrameResult {}
    record Rejected(IdFrameRejection reason) implements IdResetFrameResult {}
    record Failed(FailureId failure) implements IdResetFrameResult {}
}
public enum IdFrameRejection { WRONG_FRAME, STALE_RUNTIME, WRONG_THREAD }
public enum IdFrameResetReason {
    NORMAL, EARLY_RETURN, THROWN, ABORT, RESIZE, OFF, REPLACEMENT, TEARDOWN
}

public interface ShadowInvocationSlot {
    ShadowSlotEpoch slotEpoch();
    ShadowInvocationResult invoke(ShadowInvocationContext context);
}
public record ShadowInvocationContext(
    FrameToken frame,
    ShadowFrameView shadowFrame,
    CameraSnapshot camera,
    PublishedRegistry registry,
    PublishedBufferEstate buffers,
    FrameBarrierContexts barrierContexts,
    ShadowExecutionView execution) {}
public record ShadowFrameView(
    long worldEpoch,
    long frameId,
    float partialTicks,
    int mainTerrainFrameToken,
    Double3 cameraPosition,
    float skyAngle,
    float sunAngle) {}
public interface ShadowSlotEpoch {}
public interface ShadowExecutionIdentity {}
public interface ShadowExecutionView {}
public interface ShadowExecutionBridge {
    ShadowExecutionOpenResult open(
        ShadowExecutionIdentity activeExecutionIdentity, ShadowSlotEpoch slotEpoch);
    ShadowExecutionValidationResult validate(
        ShadowExecutionView view,
        ShadowExecutionIdentity activeExecutionIdentity,
        ShadowSlotEpoch slotEpoch);
    ShadowExecutionCloseResult close(ShadowExecutionView view);
}
public sealed interface ShadowExecutionOpenResult {
    record Opened(ShadowExecutionView view) implements ShadowExecutionOpenResult {}
    record Rejected(ShadowExecutionOpenRejection reason) implements ShadowExecutionOpenResult {}
}
public enum ShadowExecutionOpenRejection { WRONG_THREAD, ALREADY_ACTIVE }
public sealed interface ShadowExecutionValidationResult {
    record Valid() implements ShadowExecutionValidationResult {}
    record Rejected(ShadowExecutionValidationRejection reason)
        implements ShadowExecutionValidationResult {}
}
public enum ShadowExecutionValidationRejection {
    WRONG_ISSUER, INACTIVE, WRONG_EXECUTION, STALE_SLOT_EPOCH, WRONG_THREAD
}
public sealed interface ShadowExecutionCloseResult {
    record Closed() implements ShadowExecutionCloseResult {}
    record Rejected(ShadowExecutionCloseRejection reason)
        implements ShadowExecutionCloseResult {}
}
public enum ShadowExecutionCloseRejection { WRONG_ISSUER, INACTIVE, WRONG_EXECUTION }
public sealed interface ShadowInvocationResult {
    record NotInstalled() implements ShadowInvocationResult {}
    record Completed() implements ShadowInvocationResult {}
    record Rejected(ShadowRejection reason) implements ShadowInvocationResult {}
    record Failed(FailureId failure) implements ShadowInvocationResult {}
}
public enum ShadowRejection { WRONG_FRAME, STALE_PUBLICATION, UNSUPPORTED }

public interface FrameRenderPort {
    StateSnapshot snapshotState();
    PortResult normalizeForEngine();
    PortResult bind(PassDrawTarget target, AnaglyphEye eye);
    PortResult drawFullscreen(FullscreenDraw draw);
    PortResult restore(StateSnapshot snapshot);
}

public sealed interface PortResult {
    record Completed() implements PortResult {}
    record Rejected(PortRejection reason) implements PortResult {}
    record Failed(FailureId failure) implements PortResult {}
}
public enum PortRejection { WRONG_THREAD, STALE_TARGET, INVALID_DRAW, UNSUPPORTED }
public interface StateSnapshot {}
public enum FullscreenPrimitive { QUADS, TRIANGLE_STRIP }
public record MipmapSet(Set<Integer> colortexIndices) {}
public record ViewportScale(float x, float y, float width, float height) {}
public record FullscreenDraw(
    PassDescriptor pass, MipmapSet mipmaps, ViewportScale viewport,
    int instanceIndex, int instanceCount, FullscreenPrimitive primitive) {}

// Internal composition contracts; visible only to frame/reload glue and bootstrap.
interface PipelineCoordinator {
    PipelineBuildResult buildAndPublish(PipelineBuildRequest request);
    PipelineStatus current();
}
record PipelineBuildRequest(
    PackSelection selection, PackConfiguration configuration, DimensionKey dimension,
    Extent2i targetExtent, ReloadReasons reasons) {}
sealed interface PipelineBuildResult {
    record Active(PipelineIdentity identity, PipelineVersion version) implements PipelineBuildResult {}
    record Off(PipelineVersion version) implements PipelineBuildResult {}
    record Rejected(PipelineBuildRejection reason) implements PipelineBuildResult {}
    record Failed(FailureId failure) implements PipelineBuildResult {}
}
public enum PipelineBuildRejection { WRONG_THREAD, INVALID_REQUEST, STALE_INTENT, BUILD_IN_PROGRESS }
public record PipelineIdentity(
    PackIdentity pack, DimensionKey dimension, ConfigurationFingerprint configuration) {}
public sealed interface PipelineStatus {
    record Active(PipelineIdentity identity, PipelineVersion version) implements PipelineStatus {}
    record Off(PipelineVersion version) implements PipelineStatus {}
    record Building(ReloadToken token, PipelineVersion priorVersion) implements PipelineStatus {}
}
public interface DimensionPipelineCache {
    Optional<DimensionPipelineRecord> find(DimensionKey key);
    List<DimensionPipelineRecord> snapshot();
}
public record DimensionPipelineRecord(
    DimensionKey key, PipelineCacheState state, ConfigurationFingerprint fingerprint,
    PipelineVersion version, Optional<PipelineIdentity> activeIdentity) {}
public enum PipelineCacheState { PLAN_ONLY, READY_UNPUBLISHED, ACTIVE }
public record PipelineVersion(long value) {}
public record ReloadToken(long value) {}
public enum ReloadReason {
    PACK_SELECTION, OPTION_CHANGE, RESOURCE_RELOAD, DIMENSION_CHANGE, RESOLUTION_MULTIPLIER,
    REGISTRY_REMAP, MOD_ID_SOURCE_CHANGE, TAG_OR_ALIAS_CATALOG_CHANGE, HAND_LIGHT_POLICY_CHANGE
}
public record ReloadReasons(Set<ReloadReason> values) {}

public interface IdDependentGeometryInvalidator {
    IdGeometryInvalidationResult invalidate(IdPublicationChange change);
}
public record IdPublicationChange(
    OptionalLong previousGeneration, long currentGeneration,
    Optional<IdRuntimeFingerprint> previousFingerprint,
    IdRuntimeFingerprint currentFingerprint) {}
public sealed interface IdGeometryInvalidationResult {
    record Completed() implements IdGeometryInvalidationResult {}
    record Rejected(IdGeometryInvalidationRejection reason)
        implements IdGeometryInvalidationResult {}
    record Failed(FailureId failure) implements IdGeometryInvalidationResult {}
}
public enum IdGeometryInvalidationRejection {
    STALE_GENERATION, INVALID_CHANGE, SCHEDULER_UNAVAILABLE
}

public interface ResizeObservationPort {
    ResizeObservationResult observe(ResizeObservation observation);
}
public sealed interface ResizeObservation {
    record DisplayExtentChanged(long worldEpoch, DimensionKey dimension, Extent2i displayExtent)
        implements ResizeObservation {}
    record FramebufferExtentObserved(long worldEpoch, DimensionKey dimension, Extent2i actualExtent)
        implements ResizeObservation {}
    record AttachmentEpochAdvanced(
        long worldEpoch, DimensionKey dimension, Extent2i actualExtent, long attachmentEpoch)
        implements ResizeObservation {}
}
public sealed interface ResizeObservationResult {
    record Recorded(boolean abortOpenShaderFrame) implements ResizeObservationResult {}
    record Duplicate() implements ResizeObservationResult {}
    record Rejected(ResizeLifecycleRejection reason) implements ResizeObservationResult {}
    record Failed(FailureId failure) implements ResizeObservationResult {}
}
sealed interface ResizeBoundaryResult {
    record NoChange() implements ResizeBoundaryResult {}
    record Rebuilt(PipelineIdentity identity, PipelineVersion version)
        implements ResizeBoundaryResult {}
    record Off(PipelineVersion version) implements ResizeBoundaryResult {}
    record Rejected(ResizeLifecycleRejection reason) implements ResizeBoundaryResult {}
    record Failed(FailureId failure) implements ResizeBoundaryResult {}
}
public enum ResizeLifecycleRejection {
    WRONG_THREAD, INVALID_EXTENT, STALE_WORLD_EPOCH, STALE_DIMENSION, REGRESSING_ATTACHMENT_EPOCH,
    NO_ACTIVE_WORLD, NOT_FRAME_BOUNDARY, BUILD_IN_PROGRESS
}

record ActiveWorldIdentity(long worldEpoch, DimensionKey dimension) {}
interface ActiveWorldIdentityPublication {
    Optional<ActiveWorldIdentity> current();
}

interface ResizeLifecycleSink extends ResizeObservationPort {
    ResizeBoundaryResult applyPendingAtFrameBoundary(long worldEpoch, DimensionKey dimension);
}
```

`ActiveWorldIdentityPublication` is one atomically replaced, internal composition object:
`FrameHookBridge` is its sole writer, while `ResizeLifecycleSink` receives it as a private
constructor dependency and reads it independently of every submitted payload. `mod.glue.frame`
submits H-RESIZE-02, H-RESIZE-01, and H-FBO-01 observations in hook order on the render thread
through `ResizeObservationPort` and handles every closed result; `FrameDriver` calls
`applyPendingAtFrameBoundary` once with the accepted `FrameBeginSignal.worldEpoch` at H-FRAME-01, after Phase 6
sampling and before Phase 5 resize/clear. The sink retains the newest display/actual extents and
strictly greatest attachment epoch per `(worldEpoch, dimension)`, rejects an observation or
boundary application against no current publication as `NO_ACTIVE_WORLD`, an unequal epoch as
`STALE_WORLD_EPOCH`, or an unequal dimension as `STALE_DIMENSION`. Those outcomes are
mutation-free. An identical authenticated observation returns `Duplicate` without mutation; the
sink coalesces all accepted changes before that boundary into one internal
`PipelineCoordinator` rebuild/publication. An epoch advance during an open shader frame returns
`Recorded(true)`; glue aborts that frame and continues vanilla. Every rejection or failure is
mutation-free, performs no publication, and follows §6 degradation; no caller receives direct
coordinator access.

```java
public interface FullscreenPassExecutor {
    FullscreenExecutionResult execute(FrameToken frame, FullscreenDraw draw);
}
public sealed interface FullscreenExecutionResult {
    record Completed() implements FullscreenExecutionResult {}
    record Rejected(PortRejection reason) implements FullscreenExecutionResult {}
    record Failed(FailureId failure) implements FullscreenExecutionResult {}
}
public interface InternalPackManifestProducer {
    InternalPackManifest manifest();
}
public enum ContentDigestAlgorithm { SHA_256_V1 }
public record ContentDigest(ContentDigestAlgorithm algorithm, String lowercaseHex) {}
public record InternalPackManifest(PackIdentity identity, ContentDigest digest, List<String> paths) {}

public interface ShaderReloadController {
    ReloadResult request(ReloadRequest request);
    ReloadStatus status(ReloadToken token);
}
public sealed interface ReloadIntent {
    record Select(PackSelection selection) implements ReloadIntent {}
    record RebuildActive(PipelineIdentity expectedActive) implements ReloadIntent {}
}
public record ReloadRequest(
    ReloadIntent intent, ReloadReasons reasons, long intentSequence) {}
public sealed interface ReloadResult {
    record Accepted(ReloadToken token) implements ReloadResult {}
    record Coalesced(ReloadToken token) implements ReloadResult {}
    record Rejected(ReloadRejection reason) implements ReloadResult {}
}
public enum ReloadRejection { WRONG_THREAD, INVALID_REQUEST, SHUTTING_DOWN }
public sealed interface ReloadStatus {
    record Queued() implements ReloadStatus {}
    record Building() implements ReloadStatus {}
    record Active(PipelineIdentity identity, PipelineVersion version) implements ReloadStatus {}
    record Off(PipelineVersion version) implements ReloadStatus {}
    record Failed(FailureId failure) implements ReloadStatus {}
    record Unknown() implements ReloadStatus {}
}

public record FrameReadiness(
    PipelineIdentity identity, long registryGeneration, long bufferEstateGeneration,
    OptionalLong idRuntimeGeneration,
    int consecutiveFinalizedFrames, Optional<FailureId> failure) {}
public record FinalizedFrame(
    long frameId, PipelineIdentity identity, PipelineVersion version,
    Extent2i extent, AnaglyphEye eye, FrameReadiness readiness) {}
public interface FrameCaptureListener {
    void onFinalized(FrameCaptureView frame);
}
public interface FrameCaptureView {
    FinalizedFrame metadata();
    PixelCopyResult copyPixels(PixelCopyTarget target);
}
public enum PixelFormat { RGBA8 }
public record PixelCopyTarget(PixelFormat format, boolean bottomUp) {}
public sealed interface PixelCopyResult {
    record Copied(ImmutableBytes bytes) implements PixelCopyResult {}
    record Rejected(CaptureRejection reason) implements PixelCopyResult {}
    record Failed(FailureId failure) implements PixelCopyResult {}
}
public enum CaptureRejection { EXPIRED, WRONG_THREAD, INVALID_TARGET }

public interface HookReportSource { HookApplicationReport snapshot(); }
public record HookApplicationReport(
    List<HookApplicationRow> rows,
    List<HookApplicationSubreport> subreports) {}
public record HookApplicationRow(
    String catalogId, String target, int expectedCount, int actualCount,
    Set<HookHealthClass> classes, OptionalInt deferredOwnerPhase, HookFallback fallback) {}
public record HookApplicationSubreport(
    int ownerPhase,
    String canonicalFingerprint,
    boolean featureEnabled,
    List<HookApplicationSubrow> rows) {}
public record HookApplicationSubrow(
    String catalogId,
    int expectedCount,
    int actualCount,
    HookSubreportDisposition disposition) {}
public enum HookSubreportDisposition { HEALTHY, FEATURE_DISABLED }
public enum HookHealthClass { CORE, FEATURE, OBSERVER, DEFERRED }
public enum HookFallback { NONE, EVENT, VANILLA, SHADERS_OFF }
public interface UniformSignalBridge {
    SignalResult frame(FrameBeginSignal signal);
    SignalResult camera(FrameToken frame, CameraSnapshot camera);
    SignalResult event(FrameToken frame, UniformSignal signal);
}
public sealed interface UniformSignal {
    record Celestial(float sunAngle, float shadowAngle, float rainStrength) implements UniformSignal {}
    record Fog(float start, float end, ColorValue color) implements UniformSignal {}
    record Blend(BlendStateValue state) implements UniformSignal {}
}
public record ColorValue(float red, float green, float blue, float alpha) {}
public sealed interface SignalResult {
    record Accepted() implements SignalResult {}
    record Rejected(HookRejection reason) implements SignalResult {}
    record Failed(FailureId failure) implements SignalResult {}
}
```

`Coalesced.token()` is an already-issued controller token and is polled exactly like an
`Accepted.token()`. `status` returns `Unknown` for any token the controller did not issue; that
query is mutation-free. Issued tokens remain queryable through their current closed status; this
contract does not introduce token expiry or a separate supersession history.

`IdPublicationChange.previousGeneration/previousFingerprint` are both empty on first activation and
both present thereafter; partial presence is invalid. `currentGeneration` and its fingerprint must
equal the just-accepted `PublishedIdRuntime`. Every generation change schedules every Phase 10
stamped chunk product from the old generation and invalidates vanilla/compat render-layer
classification, even when the underlying mapping bytes compare equal; consumers authenticate to
the generation, not to a guessed sub-fingerprint. `Completed` is the sole result that permits
Active; rejection/failure follows §5.3 compensation and never leaks a Minecraft object across this
seam.

`BuiltInPassthroughPack` is always constructible and loadable through Phase 3 without a manifest.
`InternalPackManifestProducer` is a separate post-R7-9 capability and must not be implemented,
constructed, or queried until R7-9 is granted and Phase 3 reverified. `ContentDigest` is immutable
and record equality is its value equality. `SHA_256_V1` requires
exactly 64 lowercase ASCII hexadecimal characters and hashes this canonical byte stream: ASCII
`schmaloogium-internal-pack\0sha256-v1\0` (each `\0` is one zero byte), then every snapshot entry
in ascending unsigned lexicographic order of the exact UTF-8 path bytes defined below; if one byte
sequence is a prefix of another, the shorter sequence sorts first. Snapshot production,
`manifest().paths()`, and digest traversal use this same total comparator.
Each entry is one byte (`0` directory, `1` file), its path's UTF-8 byte length as an unsigned
64-bit big-endian integer, those UTF-8 bytes, and, for a file only, its content length in the same
integer encoding followed by its exact bytes. Subject to R7-9, each manifest String is exactly the
requested Phase 3 canonical slash-separated String projection of `NormalizedPackPath`, and the path
bytes are exactly that String encoded as UTF-8; implicit `toString()` use is forbidden. Until R7-9
is granted and reverified, internal-pack manifest/digest production is gated, not inferred.
`manifest().identity()` equals the associated source's `identity()`, and the manifest and digest describe exactly the corpus
returned by every successful `snapshot(limits)`. Insufficient
caller limits reject the snapshot through Phase 3's checked protocol; they never select or hash a
subset.

`Opened` is the only `open` result that creates a live token; both other variants leave the driver
`IDLE`, require vanilla rendering, and forbid all later calls with a fabricated token. `Advanced`
preserves the token and permits the next ordered hook. Every step/scope `Rejected` is mutation-free
and requires the adapter to omit shader work and call `abort` for any still-live frame; `Aborted`
has already drained scopes, aborted Phase 5, released Phase 4/fixed state, and invalidated every
frame/scope token. `Failed` has those same terminal effects and additionally schedules shaders-off
recovery. `Opened.draw` is exhaustive: draw through the acquired shader scope, draw the vanilla
operation under fixed function, or cancel only that named operation. `Closed` consumes exactly its
scope token and reports the restored parent's disposition. `Finalized` commits exactly once and
invalidates all tokens; `AlreadyTerminal` is the mandatory no-op response to the losing TAIL/finally
call. `FrameAbortResult.Aborted` reports completed cleanup, while its `Failed` reports best-effort
cleanup plus shaders-off recovery. No terminal result permits another shader call in that frame.

`IdDynamicsFrameSlot` is construction-installed together with the verified Phase 9 publisher or is
absent together with it before v0.3; partial presence is an invalid composition. Immediately after
Phase 6 accepts a frame, `FrameDriver` calls `afterUniformFrameAccepted` once with the borrowed
current ID runtime. `Completed` permits the next step; `HeldFeatureDisabled` means the slot already
wrote the typed zero held tuple and permits the frame without held delivery; `Rejected` aborts this
shader frame; `Failed` also schedules shaders off. Before every terminal fixed-function release,
the driver calls `resetFrame` with the exact reason. Only `Reset`/`AlreadyNeutral` permit later
shader activation; a reset rejection/failure forces off and no later shader draw occurs until a new
runtime passes the boundary. The slot retains neither frame nor publication and delegates all
sampling/value/stack policy to Phase 9's `PerDrawDynamics` and glue providers.

`ShadowInvocationContext` is a synchronous, invocation-borrowed view. `ShadowFrameView` copies the
current world epoch, driver-assigned frame ID, partial ticks, exact integer passed to the completed
main `setupTerrain` call, unshifted camera position, and same-sample finite sky/sun angles. The
world/frame identity must equal the current token and Phase 6 sample; angles are normalized to
`[0,1)`. Phase 8 may use these values and current publication credentials only during `invoke`,
must not retain or close them, and must return before Phase 7 binds or clears the main estate.

`ShadowExecutionBridge` is the sole issuer and owner of the execution credential. Immediately
before `invoke`, the frame driver creates one opaque execution identity, reads the installed slot's
opaque epoch, and calls `open(identity,epoch)`. Open returns `Opened(borrowed view)` or checks
`WRONG_THREAD` before `ALREADY_ACTIVE`; a nested or re-entered invocation never receives a second
credential. `validate(view,identity,epoch)` checks, in order,
`WRONG_ISSUER`, `INACTIVE`, `WRONG_EXECUTION`, `STALE_SLOT_EPOCH`, then `WRONG_THREAD`; `Valid`
proves both supplied identities equal the one currently open. `close(view)` returns `Closed` or
checks `WRONG_ISSUER`, `INACTIVE`, then `WRONG_EXECUTION`; successful close invalidates the view
before returning. One driver-owned `finally` closes it on every post-open exit and before the main
clear. The view is non-closeable to Phase 8, cannot be retained, and authenticates only the dynamic
extent of that one slot invocation.

`NotInstalled` and `Completed` advance to the main clear. `Rejected` is mutation-free and makes
Phase 7 abort this shader frame, restore fixed state, and continue vanilla without forcing the
healthy publication off. `Failed`, a thrown exception, an execution-open/close failure, or a return
after Phase 8 has invalidated a credential is contained as a backend failure: Phase 7 best-effort
aborts, restores state, schedules shaders off, and continues vanilla when safe. Phase 8 owns its
internal failure taxonomy and all shadow camera/traversal/framebuffer/program/uniform policy.

The driver assigns `frameId` and supplies the active registry generation; neither is hook-made.
Inputs reject non-finite time/matrix values and non-positive target extents before mutation. Both
interfaces are `:engine` types. Their values contain booleans, finite scalars, immutable
matrices, extents, closed enums, and verified Phase 1/3/4/5/6 types plus the v0.3 Phase 9 opaque
fingerprint/generation types—never MC, Forge, Mixin, LWJGL, or raw GL integer names. The exact
exposed contracts are:

Every operation above is render-thread-only except `ShaderReloadController.request`, which may be
called on the client main thread and only queues immutable copied values. Null values, blank report
identifiers/targets, negative counts/versions/sequences, empty reason sets, non-positive extents or
instance counts, out-of-range instance indices, and inconsistent optional identities are rejected
before mutation. Returned lists and bytes are immutable snapshots. Tokens, snapshots, capture views,
and borrowed dependency credentials are non-closeable and must not be retained beyond their stated
call/frame lifetime; `FrameCaptureView` expires when its listener returns. Candidates and runtimes
retain the ownership/close rules of §§5.2–5.3. `Failed` is terminal for that operation and carries no
partially accepted ownership. `Rejected` is mutation-free unless its variant explicitly reports a
previously accepted queued token. Dependency-produced non-empty diagnostic IDs are copied verbatim
into `FailureId`; Phase 7 never sanitizes, rewrites, or assigns them a longer lifetime.

`HookApplicationRow.classes` is non-empty and contains every class on its catalog row. It may contain
multiple values; no row is split or collapsed. `deferredOwnerPhase` is present exactly when
`classes` contains `DEFERRED` and preserves that row's `Pn`; otherwise it is empty.
For Mixin rows counts are successfully applied injection anchors. H9-HELD-01's target explicitly
names the accepted-frame participant slot, whose 1/1 count is construction-time registration and
never pretends to be a Mixin. The five Phase 9 rows obey §4.12's dormant/active count table; their
active counts are respectively 1/1 registration, then 4/4, 2/2, 1/1, and 1/1 Mixin anchors.

`HookApplicationReport.rows` remains the canonical Phase 7 list. `subreports` is immutable and
sorted by unique positive `ownerPhase`; each fingerprint is exactly 64 lowercase hexadecimal
digits, and each subreport's rows are immutable, non-empty, and sorted by unique catalog ID. The Phase 8 adapter
copies `ShadowHookHealth` into owner phase 8 without changing an ID, count, aggregate enabled bit,
or disposition. Missing owner phase 8 is absence, never a successful audit. The report is frozen
before first frame and is copied verbatim into Phase 2's manifest projection.

At bootstrap, `FrameDriver` receives exactly one `Optional<FrameCaptureListener>` construction
dependency; absence means capture is disabled for that driver, and the listener cannot be replaced
or removed at runtime. For each frame whose final pass completes successfully, the driver invokes
the installed listener exactly once on the render thread at H-CAPTURE-01, before presentation.
The view expires when that call returns. A listener exception is contained as a capture-feature
failure, is reported to the Phase 2 host, and does not prevent Phase 5 commit or schedule shaders
off; Phase 2 retains the artifact-failure and shot-acknowledgement policy in §6.

`FrameReadiness.registryGeneration` equals the `PublishedRegistry.generation` accepted for the
frame, and `bufferEstateGeneration` equals the accepted `BufferEstateView.generation()` used for
that frame. `idRuntimeGeneration` is empty before the v0.3 participant activates and otherwise
equals the `PublishedIdRuntime.generation()` whose aliases and per-draw dynamics were used. The
present generations are independent equality tokens and are not compared to each other.
`FinalizedFrame.version` separately identifies the coordinated Phase 7 publication.

For `ReloadIntent.Select`, the controller invokes its construction-installed Phase 3 front end and
load environment, obtains the current discovery generation where required, and owns the resulting
`PackConfiguration`; callers supply no configuration. A `Select` request must include
`PACK_SELECTION`; omission is rejected as `INVALID_REQUEST`. `RebuildActive` reuses only the currently
active, manager-owned configuration and is rejected as `INVALID_REQUEST` unless `expectedActive`
equals the active identity; it cannot carry `PACK_SELECTION`. `REGISTRY_REMAP`,
`MOD_ID_SOURCE_CHANGE`, `TAG_OR_ALIAS_CATALOG_CHANGE`, and `HAND_LIGHT_POLICY_CHANGE` are legal only
on `RebuildActive`. `RESOURCE_RELOAD` is legal on either intent and forces both Phase 3 and Phase 9
input refresh. Every accepted set is non-empty and preserved through coalescing. Thus selection,
configuration, and ID-snapshot provenance cannot be paired by a caller.

| Exposed contract | Exact content | Consumer(s) |
|---|---|---|
| `FrameDriver` / `FrameHookSink` | render-thread transaction owner; closed open/step/scope/finish/abort results; idempotent normal/finally finish | vanilla Mixin/event adapter; future Kirino adapter |
| `IdDynamicsFrameSlot` and closed accepted/reset results | optional v0.3 slot paired with the Phase 9 publisher; held delivery exactly after Phase 6 frame acceptance and neutral reset before every terminal fixed-function release, without moving value policy into Phase 7 | Phase 9 `PerDrawDynamics`/glue adapter |
| `FrameToken` / `ScopeToken` | opaque frame/epoch/generation and balanced-scope credentials; equality only; invalid after finish/abort/publication | `mod.glue.frame` call stack only |
| `FrameBeginSignal` / `CameraSnapshot` / `ShadowFrameView` / `FrameExitKind` | immutable frame/pass/time/dimension signal, copied main-camera matrices, exact driver/main-terrain token plus same-sample unshifted camera and sky/sun values for Phase 8, and `NORMAL`, `EARLY_RETURN`, `THROWN` exit classification | hook adapters; Phase 6 producer bridge; Phase 8 |
| `RenderSection` | closed vanilla-phase vocabulary mapped to §3.2's exact pack-facing slots, including terrain variants and nested effects; no backup logic | hook adapters; Phases 8/9 may submit owned sections |
| `DimensionPipelineCache` / `DimensionPipelineRecord` / `PipelineVersion` | map by Phase 3 `DimensionKey`; immutable PlanOnly/ReadyUnpublished/Active metadata; equality-only version counter | Phase 12 diagnostics; driver |
| `FullscreenPassExecutor` / `FullscreenDraw` | typed execution of deferred/composite/final descriptors, mipmap set, viewport scale, instance index/count, QUADS-or-triangle-strip primitive choice | driver; recorded-GL tests |
| `BuiltInPassthroughPack` / `InternalPackSource` | always-available Phase 3 source protocol: stable identity and limit-bounded snapshot throwing `InternalPackReadException` for Phase 3 to map to `INTERNAL_SOURCE_INVALID` | bootstrap; Phase 3 loader |
| `InternalPackManifestProducer` | separate R7-9-gated deterministic GPL manifest/digest capability; unavailable before grant and reverification | Phase 2 headless golden input |
| `ShaderReloadController` / `ReloadRequest` / `ReloadIntent` / `ReloadResult` / `ReloadToken` / `ReloadStatus` | main-thread asynchronous closed selection-change or validated active-rebuild intent, including resource/remap/mod-source/tag-alias/hand-policy reasons, whose render-thread commit uses `PipelineCoordinator`; accepted/coalesced tokens remain queryable; never accepts caller-paired selection/configuration/ID snapshots or publishes a partial pipeline | Phase 12 GUI/F3+R/command; Forge/resource/Phase 9 glue producers |
| `IdDependentGeometryInvalidator` / `IdPublicationChange` / closed result | synchronous safe-boundary gate after an accepted Phase 9 generation change; schedules alias/layer-dependent vanilla and Phase 10 chunk products before another shader frame, with no registry object crossing the seam | Phase 10 and `mod.glue` chunk scheduler |
| `FrameReadiness` | active pipeline identity, independent accepted Phase 4 registry, Phase 5 estate, and Phase 9 ID-runtime generations, consecutive finalized-frame count, and optional failure ID | Phase 2 capture agent |
| `FinalizedFrame` / `FrameCaptureListener` | construction-installed optional listener; exactly-once render-thread notification after final/pass completion and before presentation; borrowed view, dimensions, eye, frame ID, identities, no retained framebuffer handle | Phase 2 `:mod` capture agent |
| `HookApplicationReport` / `HookApplicationSubreport` | immutable primary catalog ID/target/expected/actual/class-set/deferred-owner/fallback rows, including all five Phase 9 rows, plus owner-phase/fingerprint/enabled nested rows copied from downstream audits. Primary Phase 7 row identities never change; a missing subreport is explicit absence. Frozen before first frame and serialized without capability inference | diagnostics, Phase 2 manifest diagnostics, Phase 8 hook audit, Phase 10 coexistence policy |
| `UniformSignalBridge` | maps frame/camera/celestial/fog/blend signals to Phase 6's `UniformEventSink` without resampling; Phase 9 delivers held/entity/TE/color through authenticated `PerDrawDynamics` | Phase 6 integration; Phase 9 remains value owner |
| `ShadowInvocationSlot` / `ShadowInvocationContext` / `ShadowInvocationResult` | synchronous borrowed `ShadowFrameView`, main-camera, publication/barrier, and authenticated execution credentials; opaque slot epoch; closed `NotInstalled`, `Completed`, `Rejected`, `Failed` outcomes and the cleanup rules above | Phase 8 |
| `ShadowExecutionBridge`, `ShadowExecutionIdentity`, `ShadowExecutionView`, and closed open/validate/close results | Phase 7 is sole issuer/owner; one non-nestable dynamic-extent view per installed-slot invocation; exact validation order and `finally` invalidation before main clear; terrain/entity/cloud/frustum main-policy bypass requires `Valid` for the current execution and slot epoch | Phase 8 `mod.glue.shadow` and guarded existing hook adapters |

No exposed contract contains a `ProgramHandle`, framebuffer GL name, physical ping-pong side, parsed
source, or Minecraft object. `RenderSection` selects a requested Phase 4 slot; Phase 4 alone resolves
its effective provider.

### 5.2 Dependency contracts consumed

#### Phase 2

| Consumed contract | Use |
|---|---|
| T0–T3 definitions and named-run catalog | §8/§9 implementation and milestone gates |
| scene/capture-plan wire contract | capture-agent input and deterministic shot boundary |
| run-manifest wire schema, including complete `hooks.*` primary/nested grammar | serialization target; copy the frozen `HookApplicationReport` directly under Phase 2 R18, subject to the remaining §5.4 requests |
| determinism ledger | every new clock/frame/world input is recorded rather than hidden |

Phase 7 does not redefine a pass condition, baseline, scene, timeout, or fixture-acquisition rule.
It accepts R18 by exposing and serializing only the frozen report described in §5.1; successful
frames, images, programs, and diagnostics never synthesize hook health.
The binding Phase 2 surfaces are at `docs/phase2/v1/PHASE_2_DOC.md:1471`–`:1487`.

#### Phase 3

| Consumed contract | Use |
|---|---|
| `PackFrontEnd`, discovery/load requests/results | bootstrap, selection, reload |
| `PackConfiguration`, `PackIdentity`, `DimensionConfiguration`, fingerprint/version discipline | sole configuration and per-dimension cache truth |
| schema-v2 `IdMappingInput` and nested schema/version discipline | handed unchanged into the Phase 9 candidate request; Phase 7 validates equality but never parses or resolves a rule |
| `ProgramStateModel`, `ProgramState`, `EvaluatedProgramStates` | typed engine flags and evaluated property state; no properties reparse |
| `ResourceRequirements` | Phase 4/5/6 build inputs and Phase 7 world constants/routing |
| `InternalPackSource` / `InternalPackSnapshot` | Phase 7 supplies the built-in bytes through the Phase 3-owned protocol |
| `centerDepthMacroContributor` slot | pass Phase 6's empty contribution into materialization |

The complete Phase 3 exposure is binding at
`docs/phase3/v1/PHASE_3_DOC.md:1251`–`:1337` and is freshly verified by
`docs/phase3/reviews/PHASE_3_REVIEW_22.md`.

#### Phase 4

| Consumed contract | Use |
|---|---|
| `ProgramRegistryCompiler`, `RegistryBuildRequest`, opaque `CompiledRegistryCandidate` and detached `view()` | candidate compilation and prepublication Phase 5 planning |
| `StageRegistry`, `StageStep`, `PassDescriptor`, `ProgramSlotDescriptor`, `ResolvedProgramDescriptor` | data-driven schedule traversal and requested/effective program separation |
| `ProgramStateBundle` | effective per-slot state, scale, flips, and instance projection; Phase 7 never overlays requested state or re-resolves it |
| `ProductionBarrierComposer` and exactly the three Phase 6 participants | authenticated barrier candidate composition |
| `ProgramRegistryPublisher.publish/current` | publish/off transaction, generation checks |
| `PublishedProgramStateBarrier` and Phase-4-issued `FrameBarrierContexts` | every activation/release; no direct program bind |
| per-slot scale/mipmap/instance metadata | fullscreen executor inputs |

Phase 7 obeys Phase 4's explicit prohibition on re-resolving fallback or overlaying requested state
(`docs/phase4/v1/PHASE_4_DOC.md:1383`–`:1385`).

#### Phase 5

| Consumed contract | Use |
|---|---|
| `BufferArchitecture.plan/create`, runtime inputs, opaque `BufferEstateCandidate`/inspection | derive paired estate from detached Phase 4 candidate view |
| `BufferEstatePublisher` / `PublishedBufferEstate` | second half of the coordinated publication |
| `beginFrame`, `snapshot`, `bind`, `executeClear`, `completePass`, `commitFrame`, `abortFrame` | exact frame/pass transaction; closed outcomes always handled |
| `refreshMainDepth` / `MainDepthSource` | framebuffer epoch/resize coordination after Phase 6 begin |
| `copyDepth` / `DepthCopyResult` | v0.5 PRE_WEATHER and PRE_TRANSLUCENT points, after R7-1 |
| `PassDrawTarget`, including payload-free `PassDrawTarget.Screen.INSTANCE` | unchanged snapshot draw target and anaglyph-aware final handoff |
| texture overlay leases/bindings | Phase 13 composition without dynamic units |

These are the current binding rows at `docs/phase5/v1/PHASE_5_DOC.md:1777`–`:1796`.

#### Phase 6

| Consumed contract | Use |
|---|---|
| `UniformRuntimeFactory` / `UniformRuntime` | one runtime per pipeline publication |
| `beginFrame(FrameBeginInput)` | first mutation at H-FRAME-01, before resize/clear |
| `UniformEventSink` | camera, celestial, fog, blend, color, instance and future owned signals |
| sampler/built-in/custom participants | exact Phase 4 composition positions |
| `UniformPlatformProvider` / `CenterDepthSource` | `mod.glue` implementations installed after GL-ready |

The ordering and public surface are binding at
`docs/phase6/v1/PHASE_6_DOC.md:1181`–`:1196`.

### 5.3 Candidate composition/publication protocol

One `PipelineBuildTransaction` owns all unaccepted candidates. It executes on the render thread;
only the pure Phase 9 build in step 4 may complete off-thread after glue freezes all inputs, and its
result rejoins the same transaction before publication:

1. load one immutable Phase 3 configuration/dimension view and validate its schema-v2 ID input;
2. project exactly one typed `ShadowPolicy` from that existing view, without reparsing or reopening
   pack resources, and call `ShadowPlanFactory.plan` before constructing the Phase 6 platform
   provider;
3. give the ready plan's `ShadowCelestialPolicy` (or explicit absence) to that provider and create
   the Phase 6 runtime, preserving the provider's accepted sample for `ShadowFrameView`;
4. snapshot the Phase 9 registry/tag/mod-source/alias-catalog/hand-policy inputs through D-6 glue,
   build the opaque Phase 9 candidate, and validate its configuration/registry identities;
5. compile the Phase 4 candidate with Phase 6's macro contribution;
6. take the detached Phase 4 candidate view, plan and create the Phase 5 candidate, and validate
   matching registry fingerprints;
7. compose the Phase 4 barrier candidate with exactly the three participants from that Phase 6
   runtime;
8. only after the Phase 6 runtime exists, construct the optional Phase 8
   `ShadowPassPublication` from the ready plan, runtime, authenticated world port, and frozen hook
   subreport; retain its invocation slot and slot epoch without invoking it during preparation;
9. validate every configuration, registry, world, plan, hook-report, and candidate fingerprint;
10. at a no-frame/no-draw/no-active-shadow-execution boundary, close the old Phase 8 publication,
    reset the old Phase 9 per-draw stacks, then publish Phase 4 first with a Phase-4-issued release
    context;
11. publish Phase 5 second, synchronously delivering its resize notice;
12. publish Phase 9 third with the exact configuration/registry/world context;
13. if the Phase 9 generation changed, complete `IdDependentGeometryInvalidator.invalidate` for
    alias/layer-dependent vanilla and Phase 10 chunk products; and
14. atomically mark the pipeline Active with the new Phase 8 slot (or `NotInstalled`) only after all
    publications and invalidation accept. No shader draw or shadow invocation is legal during
    steps 10–14.

Any failure before step 10 closes every still-owned object in exact reverse-construction order:
Phase 8 publication first when present, then barrier/buffer/registry/ID candidates as applicable,
then the unattached Phase 6 runtime. Phase 4 rejection leaves its candidate caller-owned. If Phase
4 accepts but Phase 5 rejects, the transaction immediately publishes Phase 4 `RecoveredOff`, closes
the rejected Phase 5 and still-owned Phase 9/8 objects, closes the unattached runtime, and renders
vanilla. If Phase 9 rejects after Phase 4/5 accept, Phase 7 closes the not-yet-installed Phase 8
publication, recovers both earlier publications off, and deactivates the old ID runtime after reset;
it never exposes new programs/buffers with old ordinals. If invalidation rejects/fails after Phase 9
accepts, Phase 7 closes the new Phase 8 publication, deactivates that new ID runtime, recovers Phase
4/5 off, and records a required full geometry invalidation before any retry. Shaders-off and active
teardown first finish or abort the open frame, then close Phase 8 (after aborting its dynamic extent
if necessary), reset and deactivate Phase 9, release Phase 5/4 in reverse composition order, and
finally close Phase 6. Acceptance transfers each candidate's ownership exactly once.

Phase 9 is not retroactively added to Phase 7's declared build dependencies. These v0.3 steps are a
binding downstream integration slot: they remain dormant and report `DEFERRED(P9)` until a Phase 9
artifact with a verified §5 supplies `IdRuntimeBuilder`, `IdRuntimeCandidate`, `IdRuntimePublisher`,
`PublishedIdRuntime`, `RenderLayerLookup`, and `PerDrawDynamics`, and its glue installs
`IdDynamicsFrameSlot`. Once installed, the transaction
uses only those contracts and never reads Phase 9 internals.

Phase 8 is likewise a downstream v0.2 integration slot, not a retroactive declared dependency.
The plan/publication steps remain `NotInstalled` until a Phase 8 artifact with a verified §5 is
available; once installed, Phase 7 consumes only its `ShadowPlanFactory`, immutable plan and
celestial policy, `ShadowPassPublication`, invocation slot, health projection, and closed outcomes.

### 5.4 Requested changes to dependency contracts — flagged, never assumed

| ID | Owner | Requested binding change | Why it blocks/limits Phase 7 |
|---|---|---|---|
| R7-1 | Phase 5 | change depth-copy protocol order to `FRAME_BEGUN -> PRE_WEATHER -> PRE_TRANSLUCENT -> FRAME_COMMITTED`, matching authoritative world order | current contract rejects the correct second call as out-of-order; copied depth remains disabled until fixed and reverified |
| R7-2 | Phase 5 (coordinated with Phase 4) | typed `applyVirtualTransition(frameId, PassDescriptor)` for `deferred_pre`/`composite_pre`, applying flips without requiring a `ResolvedProgramDescriptor` or shader draw | current snapshot API cannot represent a virtual no-program stage; Phase 7 will not fabricate one |
| R7-3 | Phase 5 | a balanced generation/frame-checked draw-buffers-none lease for first-person overlays | current public snapshot requires a resolved program; overlay routing cannot be implemented faithfully |
| R7-4 | Phase 4 | grant Phase 2 R10: per-slot `SOURCED/CHAIN/ABSENT/FAILED` plus `sourcePresent`, as immutable runtime and golden projections | required `programs.*` manifest/T3 evidence is unavailable |
| R7-5 | Phase 5 | grant Phase 2 R10A: immutable canonical `resources.*` projection with exact absence/capability-shortfall grammar | capture must not infer live allocation facts |
| R7-6 | Phase 1 | grant Phase 2 R4A: total replay-aware `GLError + attributed` result | capture must not guess attribution; COMPLETE manifest is gated |
| R7-7 | Phase 2 | define how immutable runner facts—acquisition mode, archive SHA-512, licence—reach or merge with the client capture without trusting pack self-report | current capture plan supplies pack selection/options but not all required manifest pack scalars |
| R7-8 | Phase 1 | add package slots for `com.schmaloogium.engine.frame`, `mod.glue.frame`, `mod.mixin.frame`, and `mod.conformance` to the binding package table | Phase 1 makes package placement closed; this document does not assume undeclared slots |
| R7-9 | Phase 3 | expose a canonical slash-separated String projection of `NormalizedPackPath`, stable for every valid path and suitable for exact UTF-8 encoding | canonical manifest strings, ordering, and digest bytes must not be inferred from `toString()` |

R7-1 through R7-3 block their named feature claims, not v0.1's fixed/pass-through end-to-end frame.
R7-4 through R7-7 block a COMPLETE Phase 2 manifest, not ordinary rendering. R7-8 blocks code
placement; R7-9 blocks internal-pack manifest/digest production until the dependency fix-up. Every
request needs its owner's §G1.3 fix-up and any review owed by a changed §5; this build session edits
none of those documents.

Phase 8's downstream requests R8-1, R8-4, and the Phase-7 half of R8-5 are accepted by §§4.1,
4.3–4.4, 4.12–4.13, and 5.1–5.3. They are not new dependency requests: they fill this owner's
existing v0.2 downstream slot. Because these edits change §5, the grants remain unavailable to
dependents until this document receives the fresh review recorded in §0.25.

### 5.5 Downstream hand-offs

| Phase | Contract handed onward |
|---:|---|
| 8 | consume the typed policy exactly once from Phase 3, plan before the Phase 6 provider, receive the same plan's celestial policy and same-sample `ShadowFrameView`, publish only after the runtime exists, invoke through the authenticated `ShadowExecutionBridge`, return before main clear, and contribute the immutable owner-phase-8 hook subreport without renaming Phase 7 IDs |
| 9 | supply the §5.3 candidate/publisher/dynamics contracts; Phase 7 publishes them third, calls held/reset at the accepted/terminal frame boundaries, and orders H9 entity/TE/color augmentation exactly as §4.10 specifies |
| 10 | activate only Appendix E rows 3–9, implement `IdDependentGeometryInvalidator` scheduling for alias/layer generations, and supply the Phase 1 coexistence policy outcome |
| 11 | install into Phase 6's custom participant; no new Phase 7 barrier participant |
| 12 | use `ShaderReloadController`, never publish Phase 3/4/5 objects independently |
| 13 | implement overlay/atlas leases behind Phase 5 and the deferred event/mixin rows |
| 14 | observe resize publications and profile the frame driver; no hook-policy fork |

---

## 6. Failure modes & degradation

| Failure | Detection boundary | Required disposition |
|---|---|---|
| internal/default pack bytes invalid | Phase 3 load | deterministic diagnostic; publish `Off`; vanilla continues |
| pack/preprocess/compile failure | Phase 3/4 closed result | close candidates; current active publication remains unless replacement was explicitly requested as Off |
| uniform runtime creation/composition failure | Phase 6/4 closed result | close transaction; publish/recover `Off` if no prior healthy pipeline |
| shadow plan or publication construction failure | Phase 8 closed result before publication | close the partial shadow object and continue with explicit `NotInstalled` only when the result is feature-local and hook/fingerprint state remains coherent; otherwise fail the transaction |
| ID snapshot/build failure | Phase 9 closed result before publication | close transaction in reverse order; retain prior coherent active pipeline or `Off`; never publish a partial replacement |
| buffer planning/build failure | Phase 5 closed result | close registry candidate; retain prior active or `Off`; no partial estate |
| Phase 4 publish rejection | publisher result | close both caller-owned candidates; no generation change |
| Phase 5 reject after Phase 4 accepts | publisher result | immediate Phase 4 `RecoveredOff`; close owned resources; no draw in between |
| Phase 9 reject after Phase 4/5 accept | ID publisher result | recover Phase 4/5 off, reset/deactivate old ID publication, close caller-owned ID candidate; no frame opens with mixed ordinals |
| ID geometry invalidation reject/failure | synchronous post-ID-publication gate | deactivate new ID runtime, recover Phase 4/5 off, require full chunk/layer invalidation before retry |
| shadow execution open/validate/close rejection or invocation failure | H-FRAME-05 / bridge closed result | never enter main-policy bypass on a rejected credential; best-effort abort shadow, close/invalidate the view, restore main state, and schedule Phase 8 off or the whole pipeline off according to the closed failure; no main clear occurs while the credential remains active |
| missing CORE hook | startup application audit | disable shader group for session (rung 3); vanilla render |
| missing FEATURE hook/event | application/report audit | disable only that phase/flag (rung 2a) and restore vanilla state |
| stale frame/barrier/generation or protocol rejection | closed Phase 4/5 result | abort shader frame, normalize, reacquire only at next frame; never retry inside a draw |
| depth backend degradation | `DepthCopyResult.BackendDegraded` | bind Phase 5's depthtex0 fallback, diagnose, continue feature-locally |
| backend pass/final failure | Phase 5 completion/port result | abort, normalize, recover `Off`; present vanilla if still valid |
| resize/attachment epoch changes mid-frame | refresh/observer result | abort shader frame, schedule one safe-boundary rebuild, continue vanilla |
| nested scope throws | local AROUND `finally` or H-FRAME-00 leaked-scope drain | abort child/frame, restore platform state, preserve the original throwable |
| Phase 9 ID/color scope mismatch or throw | Phase 9 token result / H-FRAME-00 drain | reset entity/block-entity/color to neutral before Phase 7 program drain; disable only producer unless generation is stale |
| ordinary early return | H-FRAME-00 `finally` with coherent token | run remaining composites and final exactly once, then commit |
| early return after protocol/backend corruption | token marked unhealthy | do not issue unsafe fullscreen draws; abort/normalize and fall back to vanilla |
| capture image/manifest write failure | capture agent commit | Phase 2-owned failure manifest; do not acknowledge shot; clean shutdown remains available |
| event/state sample malformed | Phase 6 typed sink | retain typed fallback/last-valid according to Phase 6; observer feature diagnoses once |

Finalization is non-throwing at the engine boundary: closed failures are returned. The outer wrapper
does not swallow a vanilla/mod throwable. It attempts coherent finalization in `finally`, attaches
any diagnostic without replacing the original failure, and rethrows the original after state
restoration. “Composite guarantee” therefore does not mean “render after the transaction is known
unsafe.”

---

## 7. Threading & performance notes

- Pipeline compile, Phase 4/5/9 candidate publication, Phase 8 plan/publication construction and
  close, all hook signals, shadow bridge open/validate/close, barrier activation,
  fullscreen execution, resize delivery, capture, and teardown occur on the render thread. A reload
  request may be queued elsewhere but crosses as an immutable request and commits only there.
- Phase 9's registry/mod/tag/policy snapshot is taken on its documented loader boundary; its pure
  candidate build may run off-thread only after that snapshot freezes, while publication,
  per-draw calls, and geometry invalidation remain render-thread safe-boundary operations.
- `DimensionPipelineCache` is render-thread-confined. `PipelineVersion` is a 64-bit equality token;
  no greater-than comparison or temporal inference survives wrap.
- Hot hook paths allocate no collections or strings. Catalog IDs and `RenderSection`s are constants;
  scope/token storage uses a bounded preallocated stack sized for the documented nesting families.
- Effective provider lookup remains Phase 4's ordinal/memoized operation. Phase 7 never builds a
  string map or scans all 60+ slots during a draw.
- Phase 5 snapshots are acquired once per scope and released promptly. No GL handle or framebuffer
  snapshot survives a hook return, frame, resize epoch, or publication.
- `ShadowFrameView` and `ShadowExecutionView` are synchronous borrowed values. They are never
  retained, closed by Phase 8, or used outside the one authenticated slot invocation.
- Fullscreen mipmaps are generated only for the descriptor's declared readable set immediately
  before that pass. Scale viewports and instance loops do not allocate per instance.
- The synchronous center-depth read is performed only by Phase 6 at frame begin when declared.
  Phase 7 adds no readback. Image capture is plan-gated and outside ordinary play.
- State snapshot/restore is one outer fullscreen operation plus the minimal balanced scoped deltas;
  observer callbacks are guarded during driver-owned restoration.
- Debug hook timing uses preallocated counters and is off by default. OQ-4 must demonstrate no
  per-call allocation and less than 1% added CPU frame time in its fixed-scene comparison.

---

## 8. Testability plan

### 8.1 Pure `:engine` tests

| Test family | Assertions |
|---|---|
| frame state-machine tables | every legal transition, duplicate finish idempotence, all stale/foreign/wrong-order rejections mutation-free |
| composite guarantee | normal, early-return, and thrown exits finalize exactly once when coherent; corrupted token aborts instead |
| phase-dispatch completeness | every §3.2 program maps to one requested slot/fixed/virtual/deferral; no hidden string fallback |
| nested scope property tests | arbitrary bounded push/pop/throw sequences leave no open Phase 5 snapshot and restore parent/fixed state |
| candidate ownership model | every failure step closes exactly the caller-owned Phase 4/5/9 candidates and optional Phase 8 publication in reverse order; no draw or shadow invocation between publication steps or geometry invalidation |
| shadow composition and bridge | policy projection happens once with no parse/read, planning precedes provider construction, the exact celestial policy reaches that provider, publication follows runtime creation, and open/validate/close covers exact rejection priority, non-nesting, wrong issuer/execution/thread, stale slot epoch, and `finally` invalidation |
| shadow frame identity | driver frame ID, world epoch, partial ticks, exact main setup-terrain token, unshifted camera, sky angle, and sun angle all come from one accepted sample; mismatch prevents bridge open |
| ID-runtime coordination | schema/config/registry fingerprint mismatch, Phase 9 rejection after either earlier acceptance, reverse off order, generation invalidation, and unchanged-generation no-op |
| per-draw dynamics | accepted Phase 6 frame → held sample before activation; nested entity overloads; TE ID strictly inside block program; color operand copy; normal/throw reset ordering |
| dimension cache | base/override/disabled switches, version equality, failed build retention, demotion to plan-only |
| fullscreen plan | mipmap-before-pass, identity projection, exact scale viewport, instances `0..N-1`, restore `0`, final/passthrough |
| internal pack source/golden | pre-R7-9 Phase 3 load and limit rejection require no manifest; post-R7-9 exact manifest identity/path list and SHA-256-v1 digest cover every accepting limit; rejecting limits cannot yield a subset |
| engine flags | tri-state/default behavior and `finally` restoration for every §3.5 row |
| hook-coverage model | exactly one disposition for §7.1 needs 1–11, Appendix A.1 rows, App E rows 1–18, and Pintonium rows 1–7; owner-phase-8 subreport rows copy without primary-ID drift and absence is never healthy |

### 8.2 Recorded-GL and integration tests

`RecordingGLDevice` tests assert Phase 4 release→bind→three Phase 6 participants→state lock order;
Phase 5 acquire/bind/clear/complete; deferred/composite/final mipmap and viewport order; final uses
`Screen` rather than an engine handle; every state snapshot is restored; and candidate failure leaks
no owned handle. Scripted failures cover every §6 row. Two recorded capability profiles include the
minimum GL 2.1 profile and one without quad support to force triangle strip.

Mixin integration tests launch the Cleanroom dev client with the catalog report enabled and assert
application counts, call order, early-exit finalization, actual-vs-window framebuffer extent, and
event cancellation. Sky/cloud/weather—the three reference-free families—are tested before any pack
matrix run. At v0.2 they additionally assert all eight Phase 8 health rows, the plan/report
fingerprint match, same-sample shadow frame values, main-policy bypass only during a valid dynamic
extent, and close-before-main-clear on normal and thrown paths. At v0.3 they additionally assert the
five H9 report rows and exact expected counts,
`FMLModIdMappingEvent` → queued `REGISTRY_REMAP`, both RenderManager entry methods, TE inside-open/
before-close order, buffer-position-preserving color capture, and reset-before-fixed-function on an
injected throw. OQ-4 is a separate spike gate, not replaced by unit tests.

### 8.3 Phase 2 implementation and milestone gates

- During development: `RUN-SCENE-SELFCHECK` on all fixed scenes, with repeated frames identical.
- Phase 7 implementation gate: `RUN-T1-APPROVE` then `RUN-T1-REGRESS[one classic pack, all scenes]`,
  plus `RUN-T0[classic × all scenes]` exactly as Phase 2 maps v0.1
  (`docs/phase2/v1/PHASE_2_DOC.md:417`–`:422`).
- `RUN-CAPS-GATE` and `RUN-GOLDEN-CORE` remain green for the internal pack and minimum profile.
- R7-4…R7-7 must be granted before any run manifest may claim the COMPLETE/T3 predicates.

No OptiFine image/source is committed. Oracle images remain local fixture artifacts under Phase 2's
licensing and cache rules.

---

## 9. Milestone staging

### 9.1 v0.1 first end-to-end assembly, in dependency order

| Order | Assembly increment | Observable gate |
|---:|---|---|
| 1 | dependency grant R7-8; grant and Phase 3 owner reverification of R7-9 before row 3 manifest/digest production; R7-1…R7-3 remain gates only for their named features | package/seam compile tests; dependency reviews literal PASS where §5 changed |
| 2 | install Phase 1 bootstrap/GL-ready providers and the Phase 2 capture-plan skeleton required by D-10 | startup reaches `Off` and exits cleanly without renderer |
| 3 | supply/load Phase 7's internal pack through Phase 3; after R7-9 grant and Phase 3 owner reverification, produce its canonical manifest/digest and run the headless golden; build Phase 6 runtime | headless internal golden and uniform-runtime tests |
| 4 | project the dormant typed shadow policy and plan before the Phase 6 provider, then compile Phase 4, derive Phase 5, compose participants, and perform the v0.1 dual publication; Phase 8 publication activates after the runtime at v0.2 and the third Phase 9 publication at v0.3 | recorded ownership/publication tests |
| 5 | wire H-FRAME core transaction and fixed/final passthrough | empty world renders and early-return test finalizes once |
| 6 | prove reference-free H-SKY, H-CLOUD, H-WEATHER first | dev hook report plus one fixed scene per family |
| 7 | add terrain/damage/entity/effect/particle/border balanced routes | phase-dispatch coverage and recorded activations |
| 8 | execute deferred/composite families and anaglyph-aware final; actual copied-depth remains v0.5-gated | fullscreen order/viewport/state tests |
| 9 | add split hand/overlay routing where R7-3 permits and wire engine flags | water/hand/overlay fixed scenes |
| 10 | add dimension/reload/resize rebuild transaction, hook audit, capture readiness/shutdown | resize/dimension fault matrix and `RUN-SCENE-SELFCHECK` |
| 11 | run the binding v0.1 Phase 2 gates | ≥1 classic pack T1; classic matrix T0 |

This ordering makes the three unreferenced hot areas fail early, before a visually plausible terrain
path could hide them. It also yields a valid fixed/passthrough frame before optional families.

### 9.2 Later milestones

| Milestone | Phase 7 increment |
|---|---|
| v0.2 | Phase 7 projects one typed shadow policy without reparsing, plans before the Phase 6 provider, passes its celestial policy into that provider, constructs/owns the Phase 8 publication after the runtime, installs its slot, invokes it with the same-sample frame view and authenticated bridge, and nests its health report; rollback/teardown closes it first |
| v0.3 | Phase 9 candidate/publication, held/reset, entity/TE/color hooks, remap/source reasons, report rows, and Phase 10 alias/layer chunk invalidation activate coherently; Appendix E rows 3–9 and coexistence policy land |
| v0.4 | Phase 11 custom bridge and Phase 12 reload/GUI consume existing contracts |
| v0.5 | enable actual PRE_WEATHER/PRE_TRANSLUCENT copies after R7-1, render-scale viewports, instance loops, Phase 13 atlas/overlay bindings |
| post-v0.5 | schedule new Phase 4 families data-first; no new catch-all render hook |

---

## 10. OQ & spike specifications

### 10.1 OQ-3 — Cleanroom GL context and framebuffer sizing

**Question, verbatim from RESEARCH.md §11:** “GL context creation mechanics under Cleanroom
(compat request, GLFW hints, lwjglx runtime role, HiDPI)”
(`docs/research/v1/RESEARCH.md:1008`).

**Current evidence.** Pintonium does not alter context creation; PD records no implementation there
(`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:756`–`:779`). This supports the conservative
fallback but does not answer Cleanroom/lwjglx behavior.

**Procedure.** In a pinned Cleanroom dev environment:

1. trace the loader/window path from legacy display creation through lwjglx to GLFW, recording the
   requested version/profile/debug/forward-compatible hints and the actual context flags/version;
2. run a minimal GLSL 120 shader and fixed-function draw to prove compatibility behavior, without
   changing production hints;
3. instrument logical window size, framebuffer pixel size, vanilla `Framebuffer` size, and
   H-RESIZE values on initial creation, ordinary resize, GUI-scale change, fullscreen toggle, and
   movement between 1× and HiDPI monitors;
4. repeat with lwjglx's relevant runtime options toggled one at a time and state exactly which layer
   observes each option; and
5. record source coordinates, logs, hardware/OS/scale, and the recommended hook set in
   `docs/decisions/OQ-3_GL_CONTEXT.md`.

**Success.** The spike identifies the authoritative framebuffer-pixel extent and proves the existing
context is compatibility-profile GL ≥2.1 on the supported matrix. A context-hint change is adopted
only if it is sanctioned by Cleanroom, preserves legacy fixed-function behavior, and passes client
startup/resize on every spike platform.

**Failure/fallback.** Make **no context-flag change**. Probe the context Phase 1 receives, reject an
inadequate profile through normal capability failure, and use only H-RESIZE-01/02 plus H-FBO-01/02.
Framebuffer pixel size, never logical window size, drives Phase 5. This fallback is the default plan
until the spike positively proves more.

### 10.2 OQ-4 — CleanMix hot-path injection viability and overhead

**Question, verbatim from RESEARCH.md §11:** “CleanMix divergences relevant to hot render-path
injections” (`docs/research/v1/RESEARCH.md:1009`). Pintonium proves HEAD/INVOKE/TAIL moments on a
MixinBooter-family 1.12.2 loader; this spike isolates CleanMix specifically.

**Procedure.** With production mappings/refmap and the pinned loader, enable a counter-only spike
mixin for each category:

1. `@Inject` HEAD/RETURN in `EntityRenderer.func_175068_a(IFJ)V`;
2. `@Redirect` of a slice-bounded invocation in `RenderGlobal.func_174976_a(FI)V`;
3. ordinal INVOKE before/after around the first clear and terrain setup;
4. constant/argument interception in a representative `BufferBuilder.func_181668_a(I,VertexFormat)V`
   path (a spike only; Phase 10 owns production); and
5. the same dev runs with CleanMix enabled and disabled as the controlled comparison.

The Mixin plugin records expected/actual counts; tests exercise ten thousand calls per hot category,
verify balanced counters and bytecode/application logs, use JFR/allocation profiling, and compare
fixed-scene CPU frame time after warm-up. Test one early-return and one injected exception. Store
coordinates, alternative form per row, timing distribution, and loader/mapping hashes in
`docs/decisions/OQ-4_CLEANMIX_HOOKS.md`.

**Success.** Every representative applies exactly once, preserves control flow and balanced cleanup,
allocates nothing per hot call, and adds less than 1% CPU frame time on the fixed scene. Catalog IDs,
moments, and bridge calls remain unchanged.

**Failure/fallback.** Replace only the failing form: paired HEAD/RETURN with an outer invocation
redirect; redirect with slice-bounded BEFORE/AFTER injection plus a bridge token; constant hook with
`@ModifyArg` at its consuming invocation or an accessor at a stable call boundary. If no form
preserves a CORE moment, mark the group unavailable and select `Off`; never lower `expect`, broaden a
slice, or continue partially. Any selected alternative is recorded by a Phase 7 fix-up.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale |
|---|---|---|
| D-P7-1 | engine-side `FrameDriver` behind `FrameHookSink`/`FrameRenderPort` | keeps hook policy dumb and makes a Kirino producer replaceable |
| D-P7-2 | outer render-world `finally` plus idempotent TAIL fast path | provides the required early-exit composite guarantee beyond the reference |
| D-P7-3 | per-dimension plan cache with equality-only version counter; only one active GL publication | adopts PD's useful shape without inventing cross-dimension GL ownership |
| D-P7-4 | split post-clear resource begin from post-camera matrix capture | Cleanroom orders the clear before camera setup; RESEARCH/Phase 6 require post-setup matrices |
| D-P7-5 | adopt the reference hand moment but not its depthtex2/center-depth work | authoritative depth moments and Phase 6 ownership differ |
| D-P7-6 | compose paired candidates, publish Phase 4 then Phase 5, forbid intervening draw | follows both dependency ownership/provenance contracts |
| D-P7-7 | use Forge events only when they provide exact fidelity and balanced lifecycle where needed | reduces OQ-4 surface without pretending a pre-event is a scope |
| D-P7-8 | `(internal)` and `Off` are distinct successful selections | required internal fallback is renderable; off bypasses engine GL work |
| D-P7-9 | `require=0` plus independent post-application health groups | missing hooks degrade instead of crashing, but cannot disappear silently |
| D-P7-10 | final fixed/absent slot performs typed passthrough; quad capability selects strip fallback | final is total on the GL 2.1 baseline |
| D-P7-11 | map Phase 3's typed flags to the narrow cancellable draws, scoped GL leases, and catalogued visibility queries in §3.5 | Phase 3 assigns behavior ownership but not hook-level semantics; narrow scopes preserve vanilla defaults and make restoration testable |
| D-P7-12 | publish Phase 9 after Phase 4/5, invalidate ID-dependent geometry before Active, and reset its per-draw stacks before fixed-function release | grants R9-2 without moving alias/value policy into hooks or permitting mixed publication generations |
| D-P7-13 | compose Phase 8 from one typed Phase 3 projection: plan before the Phase 6 provider, publish after the runtime, invoke through a Phase-7-issued dynamic-extent credential, and close first on rollback | one policy/sample/fingerprint lineage prevents shadow/main drift while keeping Phase 8 downstream and hooks dumb |

### 11.2 Binding decision disposition

| Decision | Phase 7 disposition |
|---|---|
| D-1 | Cleanroom 1.12.2 is the exclusive loader/runtime target; hook selection uses its verified bytecode and events |
| D-2 | frame orchestration remains shader-pack rendering only and adds none of the written non-goals |
| D-3 | the §3 conformance map and data-driven dispatch target the fixed pack matrix, not open-ended Iris parity |
| D-4 | traverse Phase 4 registry/schedule; never hardcode a terminal slot count |
| D-5 | all required vanilla hooks are Mixins or precise Forge events; no coremod/ASM fork |
| D-6 | pure engine frame policy and narrow glue are structural and tested |
| D-7 | Schmaloogium remains GPL-3.0-or-later (§0.4) |
| D-8 | published and LGPL evidence is cited without copying; OptiFine is behavioral-observation-only and AGPL input is excluded (§0.4) |
| D-9 | compatibility-profile GL is mandatory; capability-driven `QUADS` retains the triangle-strip fallback (§4.6) |
| D-10 | Phase 2 skeleton is an implementation precondition and its v0.1 runs are the gate |

### 11.3 Open items and dependency blockers

- OQ-3 and OQ-4 remain open until their §10 artifacts and acceptance criteria are complete.
- R7-1 blocks actual copied-depth enablement; R7-2 blocks virtual pre flips; R7-3 blocks faithful
  draw-buffers-none overlays.
- R7-4…R7-7 block COMPLETE run-manifest claims and T3 evidence, not ordinary v0.1 rendering.
- R7-8 blocks implementation package placement under Phase 1's closed table.
- R7-9 blocks internal-pack manifest/digest production pending its Phase 3 grant and reverification.
- Phase 8's v0.2 integration is fully specified by the accepted R8-1/R8-4/R8-5 requests but remains
  dormant until this changed §5 and Phase 8's own §5 are verified. Phase 10/13-owned deferrals remain
  dormant at their stated milestones. Phase 9's v0.3 integration is fully ordered but remains
  dormant until its own §5 is verified and the
  Phase 10/vanilla ID-dependent geometry invalidator is installed.

### 11.4 Input contradictions and rulings

Section 3.6 is binding and is not repeated here. No conflict was silently resolved: RESEARCH wins
the depth ordering and matrix semantics; actual Cleanroom byte order selects a later valid injection
site; Phase 4/5 interface gaps become requests; Pintonium remains evidence only.

### 11.5 Requested upstream changes

- **U7-1 — RESEARCH Appendix E:** replace or qualify the `RenderGlobal.renderBlockLayer` row with
  the actual world-loop overload `func_174977_a(BlockRenderLayer,D,I,Entity)I`; retain
  `func_174982_a(BlockRenderLayer)V` only for its actual role. The Cleanroom caller is visible at
  `reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:200`–`:206`.
- **U7-2 — next DESIGN candidate:** split timeline row 3 into post-clear buffer preparation and
  post-`setupCameraTransform` matrix capture. Current RC3 remains immutable; D-P7-4 is the local
  ruling required by higher authority.
- **U7-3 — RESEARCH Appendix E:** narrow the claim at
  `docs/research/v1/RESEARCH.md:1390`–`:1391`; 17 of 18 Appendix-E classes occur in the assigned
  replacement list, but `net.minecraft.client.shader.Framebuffer` does not. Section 3.7 records the
  exhaustive result and separately flags supporting hook targets without that corroboration.
- **Dependency requests R7-1…R7-9:** apply only through the owners' governed fix-up/reverification
  process described in §5.4.
- **Accepted Phase 8 requests R8-1/R8-4/R8-5:** §§4.1, 4.3–4.4, 4.12–4.13, and 5.1–5.3 now grant
  the authenticated execution bridge, exact shadow frame, one-policy composition order, owned
  publication slot, reverse close, and nested immutable hook report. The grant becomes consumable
  only after the fresh Phase 7 review required by §0.25.

This session does not edit `docs/research/v1/RESEARCH.md`, any `docs/design/*/DESIGN.md`,
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`, or another phase document.

---

## 12. Implementation checklist

| # | Work item | Tag | Test hook |
|---:|---|---:|---|
| 1 | land/reverify R7-8 before package placement and Phase 3's R7-9 before item 5 manifest/digest production; gate copied-depth, virtual-pre-flip, and overlay work only on R7-1, R7-2, and R7-3 respectively | v0.1 prereq | binding §5 diff + literal-PASS review |
| 2 | create declared engine/glue/mixin/conformance packages and seam rules | v0.1 | forbidden-dependency/package architecture tests |
| 3 | implement immutable frame/scope/result algebra and state machine | v0.1 | transition/property tests |
| 4 | implement `DimensionPipelineCache` and equality-only versions | v0.1 | dimension/base/override/failure matrix |
| 5 | implement project-owned `BuiltInPassthroughPack` as Phase 3 `InternalPackSource` with stable identity and whole-corpus bounded snapshot/checked provider failure; only after R7-9 add the separate `InternalPackManifestProducer` | v0.1 | pre-grant Phase 3 load/limit/provider-failure; post-grant manifest/digest golden |
| 6 | implement pipeline build ownership ledger and reverse close | v0.1 | fault at every build step |
| 7 | compose Phase 6 participants and paired Phase 4/5 publication at v0.1; at v0.2 add the one-policy Phase 8 plan/provider/publication order and reverse close; at v0.3 add Phase 9 publication and geometry invalidation exactly as §5.3 | v0.1/v0.2/v0.3 | recorded publication order, reverse compensation, no-draw/no-shadow assertion |
| 8 | implement `FrameRenderPort` through Phase 1 facade only | v0.1 | seam + recorded-GL tests |
| 9 | implement bootstrap H-BOOT-01…03 | v0.1 | ordered startup test |
| 10 | implement H-FRAME-00…07 and outer-finally/idempotence | v0.1 | normal/early/throw integration |
| 11 | implement post-camera matrix capture and once-per-frame assertion | v0.1 | camera snapshot ordering test |
| 12 | implement shadow invocation slot with v0.1 `NotInstalled` plus the Phase-7-owned exact-order, non-nestable execution bridge | v0.1 | auth rejection table and main clear resumes correctly |
| 13 | prove H-SKY reference-free family, including celestial redirect | v0.1 | dev application report + sky scene |
| 14 | prove H-CLOUD reference-free family and cloud flag precedence | v0.1 | fast/fancy/off scene matrix |
| 15 | prove H-WEATHER reference-free family; keep actual copy v0.5-gated | v0.1 | rain/snow/depth restoration scenes |
| 16 | implement terrain/deferred trigger and virtual transition after R7-2 | v0.1 | solid→deferred→water activation log |
| 17 | implement damaged block/line/entity/block-entity subsection routing; at v0.3 add H9 entity and inside-TE ID augmentation | v0.1/v0.3 | scoped activation, nested overload, and reverse-close tests |
| 18 | implement leash/glint/eyes/beam nested scope stack | v0.1 | injected exception restores parent |
| 19 | implement lit/unlit particle and world-border scopes | v0.1 | recorded slot selection |
| 20 | implement split hand/depth-scale and overlay lease after R7-3 | v0.1 | solid/translucent/overlay scene |
| 21 | implement flag/event wiring and state observers with recursion guard; at v0.3 replace legacy H-COLOR with exact H9 TexEnv operand/current-scope capture | v0.1/v0.3 | all §3.5 tests plus buffer-position/nested-color/throw-reset tests |
| 22 | implement fullscreen executor: mipmaps, identity ortho, scale plumbing, strip fallback, final passthrough | v0.1 | two recorded capability profiles |
| 23 | implement resize/FBO epochs and safe-boundary rebuild | v0.1 | resize/HiDPI/fullscreen fault matrix |
| 24 | implement world unload/pack/dimension/resource/remap/ID-source reload teardown and reverse Phase 9→5→4 off order | v0.1/v0.3 | no stale token/handle/generation tests |
| 25 | implement Mixin health plugin, group report, compatibility-registry bail, nested owner report shape with no primary-ID drift, all five dormant/active Phase 9 rows, and the v0.2 Phase 8 report copy | v0.1/v0.2/v0.3 | missing/overmatched/unsafe-renderer, H8 fingerprint/count, and H9 count tests |
| 26 | run and record OQ-3 spike; retain default fallback until success | v0.1 | `docs/decisions/OQ-3_GL_CONTEXT.md` |
| 27 | run and record OQ-4 spike before production hot hooks close | v0.1 | `docs/decisions/OQ-4_CLEANMIX_HOOKS.md` |
| 28 | implement capture readiness/frame boundary/shutdown without inferred manifest fields | v0.1 | capture-plan success/failure integration |
| 29 | run reference-free family integration and `RUN-SCENE-SELFCHECK` | v0.1 | all repeated frames identical |
| 30 | run Phase 7 impl gate: classic T0 matrix and one classic pack T1 | v0.1 | Phase 2 named artifacts/manifests |
| 31 | project one immutable-config `ShadowPolicy`; plan before the Phase 6 provider; pass its celestial policy to that provider; after runtime construction own/install the Phase 8 publication; invoke its slot with exact `ShadowFrameView` and authenticated execution view; serialize its nested health rows; close first on rollback/teardown | v0.2 | shadow T1 scene plus composition/auth/fingerprint/throw matrix |
| 32 | activate the verified Phase 9 candidate/publisher, accepted-frame held call, terminal reset, H9 entity/TE/color hooks, reason producers, and Phase 10 ID-dependent geometry invalidator as one coherent v0.3 capability | v0.3 | ID/vertex/layer/remap/report/coexistence suites |
| 33 | connect Phase 11/12 consumers | v0.4 | custom/reload/options tests |
| 34 | enable depth copies, scale, instances, and Phase 13 overlay/atlas paths | v0.5 | copied-depth/multi-instance/T3 scenes |

---

*End of PHASE_7_DOC.md. Twenty-one review rounds ended in PASS before §0.25. This maintenance
addendum changes binding §5 and leaves v1 unverified pending fresh review round 22; no version roll
occurs until that loop exits.*
