# Pintonium — Shader-Engine Analysis for the Schmaloogium Project

> **Status:** v1.0, 2026-07-25. Authored from a full parse of the Pintonium working tree
> (Celeritas + Oculus/Iris-1.7 fork with 1.12.2 shader support), cross-checked by direct
> file reads of the load-bearing claims.
> **Role:** Mining report. Every section maps what Pintonium *actually does* onto the
> Schmaloogium phase plan in `DESIGN.md` — what to reuse, what to learn from, and what
> to actively avoid. This document describes Pintonium; it does not change Schmaloogium's
> contract (RESEARCH.md remains the source of truth).
> **Reading guide:** §1–§4 are orientation. §5–§16 are the subsystem analyses, each ending
> with a **"Relevance to Schmaloogium"** block naming the phases it feeds. §17–§20 are the
> pitfalls catalogues. §21 is the quick-reference phase map.

---

## 1. What Pintonium is

Pintonium is a fork of **Celeritas** (itself Embeddium/Sodium 0.5.11-lineage) merged with
**Oculus 1.7** (the Forge port of Iris 1.7), retargeted at legacy Minecraft — 1.12.2
(`forge122`) and 1.7.10 (`forge1710`) — plus modern versions (`modern/`, `babric/`). It is
a *performance mod with shaders*, not a standalone shader engine: the shader support is
built on top of the Sodium-style chunk renderer, which **replaces** vanilla terrain
rendering outright.

Maintainer self-assessment (README) is accurate: "HEAVILY WIP and largely assisted by AI."
Verified consequences found in the tree: real packs render (Complementary Unbound, Solas,
Lumina), but there are confirmed logic bugs, dead subsystems, duplicated edit artifacts,
and one entire shadow pipeline that is allocated but never executes on 1.12.2.

**Why it matters to Schmaloogium anyway:** it is the only working, *legally clean*,
public codebase that (a) runs demanding Iris-format packs on a 1.12.2 LWJGL3 compat
context, (b) hooks the exact 1.12.2 classes Schmaloogium's App E catalogs
(`EntityRenderer`, `RenderGlobal`, `Framebuffer`), and (c) solves the 1.12.2-specific
problems Schmaloogium's design currently only specifies on paper (FF-matrix capture,
depth-texture swap, block-id mapping, option persistence). Where the OptiFine decompile
is behavioral-observation-only (G7 rule 2), **Pintonium is readable *and* LGPL-3.0
reusable** under D-8. It should become a primary reference for Phases 3–13.

### 1.1 Provenance map (trust follows lineage)

| Layer | Origin | License | Trust |
|---|---|---|---|
| `common/` | Celeritas/Embeddium (Sodium 0.5.11) chunk engine | LGPL-3.0 | Battle-tested upstream |
| `common-shaders/.../iris/` | Iris 1.7 via Oculus, ported with AI assistance | LGPL-3.0 | Trustworthy where it matches upstream Iris; suspect where 1.12.2-specific |
| `common-shaders/.../kroppeb/stareval/` | kroppeb's stareval expression engine (vendored by Iris) | historically MIT per Iris credits; upstream repo no longer resolves — **verify before direct reuse** | Complete and coherent |
| `common-shaders/.../com/mitchej123/glsm/` | Angelica/GTNH-derived GL-state service interfaces | LGPL-3.0 (Angelica) | Sound seam; 1.12.2 impls are thin passthroughs |
| `forge122/src/shaders/` | 1.12.2 glue, mostly AI-written | LGPL-3.0 | The least-reviewed code in the tree |
| `org.taumc:glsl-transformation-lib` (dependency) | taumc fork of douira's **glsl-transformer** | **glsl-transformer is AGPL-3.0 (D-8); the fork's license must be treated as AGPL unless verified otherwise — never copy, and do not adopt it as a dependency** | See §14 |
| `org.anarres:jcpp` (dependency) | shevek/jcpp C preprocessor | **Apache-2.0 (verified via Maven POM)** | Clean candidate for Schmaloogium Phase 3 |

D-8 compatibility verdict: the whole tree except the transformation-lib dependency is
LGPL-3.0-or-cleaner and combines into Schmaloogium's GPL-3.0-or-later with compliance
(notices preserved, modifications marked).

### 1.2 Repository traps (read before searching the tree)

1. **`celeritas-shader-refactor/` is a stale full copy of the repo** — untracked, not in
   `settings.gradle.kts`, already diverged (e.g. `blockEntityId` default `0` vs `-1`).
   It pollutes every grep. Exclude it from all searches; never treat its files as current.
2. **Root-level `mixins.vintagefix.json` / `mixins.vintagefix.late.json`** are stray
   files not referenced by any source set.
3. **`DESIGN.md` at the repo root is the Schmaloogium design document** (copied in by the
   project owner). It is *not* Pintonium documentation. Nothing in the Pintonium tree
   documents the Pintonium shader engine except its README and code.
4. Git history is 9 bulk commits (`"shader progress [WIP]"`, `"Update"`) — no useful
   archaeology; the code as-is is the only record.

---

## 2. Architecture in one page

```
:common           Celeritas chunk engine (Sodium lineage), MC-coupled, version-abstracted
:common-shaders   The Iris fork: pack front-end + GL layer + pipeline core.
                  MC-version-agnostic; talks to the game through THREE seams:
                    - com.mitchej123.glsm.{GLStateManagerService, RenderSystemService}
                      (ServiceLoader; per-version impls in META-INF/services)
                    - org.embeddedt.embeddium.compat.mc.MinecraftVersionShimService
                      (camera/world/dimension/framebuffer/biome/weather accessors)
                    - org.taumc.celeritas.api.v0.CeleritasShaderVersionService
                      (per-version pipeline factory, block-entry parser)
:forge122         1.12.2: the seams' impls, all mixins, the Vintage* pipeline classes,
                  hand-written GUI. src/main + src/shaders merged into one source set.
:forge1710/:modern/:babric   Other version backends (modern/ = upstream Iris 1.7 parity;
                  the only module with a working shadow renderer)
```

**Comparison with the D-6 seam.** Pintonium's seam is *service-based* (ServiceLoader
interfaces in shared code, impls per version) rather than Schmaloogium's *module-based*
seam (`:engine` pure-JVM, no MC/GL on classpath). Notably, `common-shaders` is **not**
headless-testable the way `:engine` is designed to be: it compiles against LWJGL3 and
Embeddium GL classes directly and calls GL statically throughout. Schmaloogium's
`:engine`/`engine.gl` facade is strictly stronger for the D-10 conformance strategy.
What Pintonium *does* validate: a single version-agnostic shader core can drive multiple
radically different MC backends behind ~3 service interfaces — the interface inventory
(`MinecraftVersionShimService`'s method list) is a good checklist for what Schmaloogium's
`mod.glue` provider interfaces will need to cover.

**Relevant to:** Phase 1 (seam design, OQ-20 hardness argument), G3.1.

---

## 3. Pipeline core (Phase 4 relevance)

### 3.1 Class model

- `pipeline/WorldRenderingPipeline` (interface) ← `VanillaRenderingPipeline` (no-op
  baseline; binds MC FBO, `glUseProgram(0)`) and `CommonIrisRenderingPipeline`
  (1482 lines; the shader core) ← per-version `Vintage/Archaic/ModernIrisRenderingPipeline`.
- `pipeline/PipelineManager` — **per-dimension pipeline cache**
  (`Map<NamespacedId, WorldRenderingPipeline>`), created in `renderWorldPass` HEAD;
  `destroyPipeline()` on pack reload, plus a `versionCounterForSodiumShaderReload` that
  downstream program caches poll to invalidate. *A version-counter invalidation scheme
  is exactly the reload-safety mechanism Schmaloogium Phase 4/12 needs.*
- `pipeline/WorldRenderingPhase` — 26-value enum (`SKY, SUN, MOON, TERRAIN_SOLID, …,
  HAND_TRANSLUCENT`) with `phase` + `overridePhase` + deferred-pop semantics. Pintonium's
  phase set is the Iris modern superset, not the G6 five-stage list — structurally what
  D-4 asks Schmaloogium's registry to be shaped like.
- `shaderpack/loading/ProgramId` + `ProgramFallbackResolver` — the backup-chain model:
  `TerrainSolid/TerrainCutout → Terrain → TexturedLit → Textured → Basic`,
  `Water → Terrain`, `HandWater → Hand`, `ShadowSolid/ShadowCutout → Shadow`, memoized
  recursive resolution. **Shadow never inherits.** This is App A.2 semantics, ported and
  working — the closest clean-room-adjacent reference for Phase 4's backup chains.

### 3.2 Compilation

All pack sources pass an AST transform (§14) then `gl/program/ProgramBuilder` →
`Program`/`ComputeProgram`. **`Program.use()` is the state barrier**, literally:

```java
public void use() {
    IrisRenderSystem.memoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT | …);
    GL_STATE_MANAGER.glUseProgram(handle());
    uniforms.update(); samplers.update(); images.update();
}
```

This is direct, working validation of the §4.2 "use program is the universal state
barrier; hook sites stay dumb" rule Schmaloogium adopted. Uniform/sampler/image state is
re-pushed on every program switch, with per-`Uniform` value caching to skip redundant
uploads (matrices are dynamic-frequency and always re-upload — matching App D cadence).

**Attribute pre-bind divergence — do not copy:** `gl/shader/ProgramCreator.java:21-25`
pre-binds `mc_Entity`/`iris_Entity`@**11**, `mc_midTexCoord`@**12**, `at_tangent`@**13**,
`at_midBlock`@**14**. That is Iris's modern numbering. Schmaloogium's contract is the OF
G6 numbering — `mc_Entity`=**10**, `mc_midTexCoord`=**11**, `at_tangent`=**12** (Phase 4
spec, App contract). Pintonium gets away with 11+ because it owns its chunk renderer's
VAO layout; a contract-faithful engine may not.

### 3.3 Per-pass state

`CompositeRenderer.Pass` carries: program, framebuffer, `drawBuffers`, per-pass
`BlendModeOverride`, per-buffer `BufferBlendOverride` (GL 4.0 `glEnablei` /
`ARBDrawBuffersBlend`), `viewportScale` (`scale.<pass>`), mipmapped-buffer bitmask
(`colortexNMipmapEnabled` const directives), compute companions, and a flip snapshot
(`stageReadsFromAlt`). This is the per-program state bundle Schmaloogium's Phase 4
registry lists — a working inventory to check the App A.1 slot model against.

**Relevant to:** Phase 4 (registry, barrier, backup chains), Phase 6 (cadence), G4.2.

---

## 4. Frame orchestration — the verified 1.12.2 hook timeline (Phase 7 gold)

This is the single most valuable thing Pintonium offers Schmaloogium: **a proven set of
injection points into the 1.12.2 render loop that survives demanding packs.** From
`forge122/.../mixin/shaders/MixinEntityRenderer_Shaders.java` (verified by direct read)
and `mixin/shaders/MixinRenderGlobal_Shaders.java`:

| # | Injection point (all `EntityRenderer` unless noted) | What runs there |
|---|---|---|
| 1 | `renderWorldPass(IFJ)V` **HEAD** | tick-delta/time uniforms; `PipelineManager.preparePipeline(dimension)`; `setPhase(NONE)` |
| 2 | `renderWorldPass`, INVOKE `GlStateManager.clear(I)` ordinal 0 (before) | reset vanilla GL state to known values |
| 3 | same site, **AFTER** the first clear | **capture gbuffer matrices** from `ActiveRenderInfo.PROJECTION/MODELVIEW` FloatBuffers → `CapturedRenderingState`; `SystemTimeUniforms` begin-frame; `pipeline.beginLevelRendering()` (clears, resize check, shadow clear passes, `FrameUpdateNotifier.onNewFrame()`, custom-uniform update, `begin_*` passes); rebind default FB |
| 4 | `renderWorldPass`, INVOKE `RenderGlobal.setupTerrain(...)` AFTER | `pipeline.renderShadows(...)` (shadow map pass; on 1.12.2 doubles as the `prepare_*` trigger since the shadow renderer is null), rebind default FB |
| 5 | `RenderGlobal.renderBlockLayer` HEAD when layer == TRANSLUCENT | `pipeline.beginTranslucents()`: **depth copy → depthtex1**, then all `deferred_*` passes, re-enable blend |
| 6 | `EntityRenderer.renderHand(FI)V` around `ItemRenderer.renderItemInFirstPerson` | `begin/endVintageHandRendering()` — hand bridge incl. `copyPreHandDepth()` (**depthtex2**) and center-depth sampling |
| 7 | `renderWorldPass` **TAIL** | `finalizeLevelRendering()`: `compositeRenderer.renderAll()` then `finalPassRenderer.renderFinalPass()` to the MC framebuffer |

Supporting hooks: `RenderGlobal.renderEntities` region (entity/block-entity bridges, in
the engine's `RenderGlobalMixin`), `ParticleManager.render{Lit}Particles` HEAD/RETURN,
`RenderDragon.renderCrystalBeams` HEAD/RETURN (beacon beam), `RenderGlobal.drawSelectionBox`
HEAD/RETURN (line program), `EntityRenderer.updateFogColor`/`setupFog` (fog uniforms via
`StateUpdateNotifiers`), startup via `GameSettings.loadOptions` (early init),
`OpenGlHelper.initializeTextures` RETURN (GL caps ready → renderer init + pack load),
`GuiMainMenu.initGui` RETURN (loading complete).

Three observations that de-risk Phase 7's design directly:

1. **The `GlStateManager.clear` ordinal-0 INVOKE site is a stable anchor** for "world
   render begins here" in 1.12.2 — matrix capture and `beginLevelRendering` both hang off
   it. Schmaloogium's App E needs an equivalent moment; it exists and is battle-tested here.
2. **The deferred trigger is exactly where RESEARCH.md §4.4 puts it** (translucent-layer
   HEAD → depth copy → deferred passes). Cross-validated.
3. **Composite/final at `renderWorldPass` TAIL** satisfies the composite-guarantee for
   normal frames; note Pintonium does *not* handle early frame exits specially — Schmaloogium's
   composite-guarantee requirement (Phase 7a) is stricter than what Pintonium implements.

**What is NOT hooked (feature gaps to beat):** sky (`renderHorizon()` is an empty TODO —
sky renders pure fixed-function), weather (no hooks at all; `rain.depth` unsupported in
practice), clouds. Pintonium's working packs are precisely the ones that tolerate vanilla
sky/weather. Schmaloogium's App E rows for sky/weather have no reference implementation
here — those hooks must come from the App E catalog and Cleanroom patches alone.

**Relevant to:** Phase 7 (both parts), Phase 2 (scene families: note which families
Pintonium can't even render), OQ-4 (proof that head/INVOKE/TAIL injections on
`EntityRenderer`+`RenderGlobal` hot paths work under a MixinBooter-style 1.12.2 loader).

---

## 5. Buffer estate (Phase 5 relevance)

`targets/RenderTarget(s)`, `targets/BufferFlipper`, `targets/ClearPassCreator`,
`gl/framebuffer/GlFramebuffer`, `shadows/ShadowRenderTargets`.

### 5.1 Ping-pong and flips — contract semantics, working

- Every colortex is allocated as a **main/alt pair up front** (`RenderTarget`:
  `textures[2]`, LINEAR filter, NEAREST for integer formats, CLAMP_TO_EDGE).
- `BufferFlipper` javadoc is the flip contract in one paragraph (verified):
  *not flipped → write alt, read main; flipped → write main, read alt.* `flip()` toggles;
  `snapshot()` per pass; explicit `flip.<pass>.<buffer>` directives applied at pass
  construction.
- FBO creation per pass attaches main-or-alt per drawbuffer index by inverting the
  pass's writes-to-alt set; samplers close over the same snapshot. Main-vs-alt selection
  is threaded through the pipeline as `flippedBeforeShadow / flippedAfterPrepare /
  flippedAfterTranslucent` snapshots, and the gbuffers FB exists in two variants
  ("before translucent" / "after translucent") selected by frame position.
- **Frame-end flip reconciliation:** Iris's `FinalPassRenderer` runs `SwapPass` copies
  propagating alt contents back to main each frame, so the next frame's gbuffers (which
  read main) stay coherent. **This is a design decision Schmaloogium Phase 5 must make
  explicitly** against App F.7's last-writer-leaves-flip-enabled convention: copy-back
  (Iris, costs bandwidth) vs. flip-state carryover (OF-faithful). The contract
  (gbuffers read/write main) implies state must be unflipped at frame start; RESEARCH.md's
  ruling governs, but Pintonium proves the copy-back mechanism works if the contract
  permits it.
- **Clears honor flip state** (`ClearPassCreator` builds main-variant and alt-variant
  clear FBOs and clears both) — direct confirmation of the App B.1 "clears both sides
  when flipped" ruling in Schmaloogium's Phase 5 spec.
- Clear colors: buffer 0 → fog color with **alpha forced to 1.0** (comment: otherwise
  "Sildur's Vibrant Shaders will give you pink reflections") — a deployed quirk worth
  recording verbatim in Phase 5's clear rules; buffer 1 → white; 2+ → transparent black.
  Batched in groups of `GL_MAX_DRAW_BUFFERS`.

### 5.2 Depth textures — the 1.12.2 problem, solved

Vanilla 1.12.2 `Framebuffer` uses a depth **renderbuffer**, which cannot be sampled.
`mixin/shaders/MixinFramebuffer_Shaders.java` **replaces the depth renderbuffer with a
depth texture** at `createFramebuffer` (`GL_DEPTH_COMPONENT`, or `GL_DEPTH24_STENCIL8` +
stencil attachment when stencil is enabled), tracks it via `IRenderTargetExt`
(`iris$getDepthTextureId`, `iris$getDepthBufferVersion`), and the pipeline re-attaches
all owned FBOs' depth attachments whenever the version counter bumps. **This mixin is
the reference implementation for Schmaloogium's depthtex0** — Phase 5 cannot ship
without an equivalent, and this one is proven against real packs.

`depthtex1`/`depthtex2` copy mechanics: first frame `glCopyTexImage2D`, thereafter
`DepthCopyStrategy.fastest(...)`: **GL 4.3 `glCopyImageSubData` (function-pointer checked
because caps lie) → GL 3.0 blit (combined depth-stencil) → GL 2.0 `glCopyTexSubImage2D`**.
A clean capability-tiered design to copy for Phase 5's copy mechanics.

### 5.3 Sizing and shadow targets

- Resize: per-frame `resizeIfNeeded(version, depthTexId, w, h, depthFormat, directives)`;
  on change: recreate all colortex, realloc depthtex1/2, force full clear,
  `recalculateSizes()` on all four composite renderers + swap passes, resize relative
  SSBOs/custom images. A complete resize lifecycle checklist for Phase 5.
- Per-buffer `size.buffer.<buf>` / scale overrides (relative if the string contains ".",
  absolute otherwise). **No global render-quality multiplier** — `MC_RENDER_QUALITY`
  hardcoded `1.0`. Schmaloogium's `superSamplingLevel`/quality multipliers have no
  reference here.
- **All 16 colortex always allocated** (no used-buffer analysis; TODO acknowledges it).
  Schmaloogium's Phase-3-scan-driven sizing is better — keep it.
- Shadow targets: `shadowtex0/1` depth + `shadowcolor0..N` pairs, hardware-PCF compare
  mode (`GL_COMPARE_REF_TO_TEXTURE`), swizzle `R,R,R,1` for old packs. **But
  `ShadowRenderTargets.flip()` is a stub (`// TODO: Actually flip`)** and shadow samplers
  always read main — shadowcomp ping-pong is non-functional. Treat the shadow buffer code
  as structure-only reference.

**Relevant to:** Phase 5 (nearly end-to-end reference), Phase 7 (copy *moments*),
Phase 14 (the tiered copy strategy is already the "Adapt" version).

---

## 6. Uniforms, samplers, and state (Phase 6 relevance)

### 6.1 Cadence model — matches Schmaloogium's design almost exactly

`gl/program/ProgramUniforms` buckets uniforms into `ONCE` / `PER_TICK` (tick compare) /
`PER_FRAME` (frame counter) / **dynamic** (re-uploaded on every `update()`, i.e. every
program switch). Each `Uniform` caches its last value and skips redundant GL calls;
matrices live in the always-upload bucket. `uniforms/CommonUniforms.java` splits
non-dynamic (camera, viewport, world/system time, biome, celestial, matrices, id-map)
from dynamic (`entityId`, `atlasSize`, `blendFunc`, `renderStage`, fog). This is the
App D cadence note implemented; use it as the checklist when writing Phase 6's cadence
table.

`FrameUpdateNotifier` fires once per frame **before resize/clear** ("so that the center
depth sample is retrieved properly") — an ordering constraint Schmaloogium's Phase 7a
frame-begin sequence must preserve (sample world state before touching buffers).

### 6.2 FF-matrix capture — the 1.12.2 proof

`CapturedRenderingState.gbufferModelView/Projection` are copied from vanilla's
`ActiveRenderInfo.PROJECTION`/`MODELVIEW` FloatBuffers (via an accessor mixin) at the
first-clear moment (§4 row 3) — i.e., the matrices vanilla itself captured with
`glGetFloat` during `orientCamera`. Inverses and `gbufferPrevious*` snapshots derived in
`MatrixUniforms`. **This is exactly the §4.4 FF-matrix-capture design, working in
production on 1.12.2.** Shadow matrices use the FF stack too (`glMatrixMode`/
`glPushMatrix`/`glLoadMatrix` swaps during the shadow pass) — the compat-profile
cooperation D-9 mandates, demonstrated.

### 6.3 centerDepthSmooth — a better design than the planned one

Schmaloogium Phase 6 currently specifies a **synchronous per-frame `glReadPixels`**
(App-D-faithful), with a PBO async replacement scheduled in Phase 14. Pintonium
eliminates the readback entirely: `pathways/CenterDepthSampler` runs a **1×1 R32F
ping-pong GPU pass** (`centerDepth.fsh` blends the current center depth against the
previous frame's value with a `centerDepthHalflife` decay uniform), and the shader
transformer rewrites pack references to `centerDepthSmooth` into
`texture(iris_centerDepthSmooth, vec2(0.5)).r`.

**The transferrable insight:** Schmaloogium doesn't need the AST transformer to do this.
A string-level injection in Phase 3's standard macro header —
`#define centerDepthSmooth texture2D(iris_centerDepthSmooth, vec2(0.5)).r` — achieves
the same redirect for GLSL-120-era packs, since `centerDepthSmooth` is only ever *read*.
Candidate consequence: Phase 6 ships GPU-side smoothing from day one, Phase 14's PBO
spike (and its sync fallback) may become unnecessary. Flag as a Phase 6 design option
with provenance `[V:observed — Pintonium pathways/CenterDepthSampler]`; contract check
against App D semantics required (OF's value is CPU-readback-smoothed; the GPU EMA is
behaviorally equivalent but not bit-identical — verify against the §4 contract before
adopting).

### 6.4 Smoothing math

`transforms/SmoothedFloat` (asymmetric exponential decay, `k = ln2/halfLife`, half-lives
in deciseconds, separate wet/dry rates), `SmoothedVec2f` for `eyeBrightnessSmooth`
(`eyeBrightnessHalflife` default 10). Closed-form references for Phase 6's smoothing
formulas. **Caution:** `PackDirectives.java:254-255` misassigns the `drynessHalflife`
const directive into the `wetnessHalfLife` field (confirmed bug §17) — read the math,
not the wiring.

### 6.5 Sampler re-pointing and the unit-map divergence

Per program switch, `ProgramSamplers.update()` (re)binds textures and pushes sampler
uniforms (queued `GlUniform1iCall`s). Units are **allocated dynamically per program**,
skipping reserved sets — `{0,1,2}` for world programs (0=albedo atlas, 1=overlay,
2=lightmap), `{1,2}` for fullscreen passes with **colortex0 pinned to unit 0** as the
"default sampler"; colortex0–3 are sampler-visible only in fullscreen passes
(`startIndex = isFullscreenPass ? 0 : 4`).

**This is not the OF fixed unit map (App B.3).** Iris re-points sampler uniforms per
program; OF binds textures to contract-fixed units and the pack's sampler defaults do
the rest. Schmaloogium's Phase 5/6 shared App B.3 table (incl. the depthtex1-unit-11
ruling) is contract-visible and must **not** be replaced by Pintonium's dynamic scheme —
but `ProgramSamplers` is still a good mechanical reference for how to *implement*
re-pointing with dedup.

### 6.6 StateUpdateNotifiers

`gl/state/StateUpdateNotifiers` — 8 static notifier hubs (fog start/end, blend func,
texture bind, normal/specular change, phase change, fallback entity) decoupling mixin
hooks from uniform invalidation. Clean pattern; adopt the shape for Phase 6/7.
**Verified gap:** `blendFuncNotifier` is **never assigned on 1.12.2** (no
`MixinGlStateManager` state-listener module) — a pack declaring the `blendFunc` uniform
NPEs at program build. Schmaloogium's `blendFunc` observation via GlStateManager
cooperation (G4.6, App E row 16) must wire its notifier deliberately; Pintonium shows
the failure mode of forgetting it.

**Relevant to:** Phase 6 (cadence, smoothing, capture, samplers), Phase 5 (unit map —
contrast), Phase 7 (frame-begin ordering), Phase 14 (PBO spike possibly obviated).

---

## 7. Pack front-end (Phase 3 relevance)

`shaderpack/` (ShaderPack, include/, preprocessor/, option/, properties/, IdMap),
`gl/shader/StandardMacros`, `parsing/` directive parsers. This package is the closest to
upstream Iris and the most directly reusable subsystem in the tree.

### 7.1 Discovery and sources

- `shaderpacks/` enumeration; a folder pack is valid if a recursive walk finds a
  `shaders` subdir; zip packs via an open `FileSystems.newFileSystem` (nested-root
  tolerant). Corrupt zips → graceful fall back to off. Matches §3.1/§4.1 discovery
  semantics; **zip-FS lifecycle management** (one static FS, closed on destroy) is a
  detail worth copying.
- **`dimension.properties`** (preprocessed with env defines only) → dimension→folder
  map; else auto-detect `world0`/`world-1`/`world1`. **Divergence:** Schmaloogium's
  contract is the OF world −128..128 scan with "only `.vsh`/`.fsh` read from dimension
  folders" (Phase 3 spec). Pintonium implements the Iris model (full program sets per
  dimension, no merge with base). Follow the contract; note Pintonium's per-dimension
  `ProgramSet` cache as the mechanism.
- **No `(internal)` pack** — `isInternal()` hardcoded `false`. Schmaloogium's internal
  default pack (Phase 7a) has no reference here.
- `#include`: resolved **before all other preprocessing** (comment-unaware line scan —
  documented upstream behavior, matches OF), cycle-detected via graph DFS with
  rustc-style diagnostics. No depth cap found — Schmaloogium's ≤10-deep rule (§3.2) is
  contract-side; enforce it in Phase 3 even though Pintonium doesn't.

### 7.2 Preprocessing — jcpp is proven and clean

`shaderpack/preprocessor/JcppProcessor.java`: all GLSL through **jcpp (Apache-2.0 —
verified)**, with two tricks worth stealing:

1. `#version`/`#extension` lines are rewritten to `#warning` markers before jcpp (which
   tolerates them anywhere), recollected by a listener, and **hoisted back to the top**
   of the output (needed for strict drivers/Mesa) — with an injection guard against
   pack-side marker spoofing.
2. Environment defines are injected via `pp.addMacro` (API), not textual `#define`
   lines, **preserving line numbers for error attribution** — exactly what
   Schmaloogium's `#line`-bookkeeping requirement (§3.2) needs.

`PropertiesPreprocessor` runs jcpp over `shaders.properties`, ID-map files, and
`dimension.properties` with pack options as macros (booleans → bare macros, strings →
macro values). **Verified defect:** non-directive lines have all `#` characters stripped
(corrupts values containing `#`), and there's vestigial dead listener machinery. Use
jcpp; write your own properties path.

### 7.3 Options — discovery and application

`option/OptionAnnotatedSource`: boolean options (`#define NAME` / `//#define NAME`,
"confirmed" only when the same component `#ifdef`s it — `#if`/`#elif` deliberately not
analyzed, OF parity); string options (`// comment [values]` with auto-appended default);
const-option whitelist (`shadowMapResolution`, `wetnessHalflife`, …). `ShaderPackOptions`
rewrites sources in place (comment/uncomment, value replacement with
`// OptionAnnotatedSource: Changed option` markers). Profiles: precedence = constraint
count, cycle-checked includes, `!program.x` disabling. All of this is §3.3/App F.3-F.4
machinery, working.

**Verified defect with contract consequences:** `IncludeGraph.computeWeaklyConnectedComponents()`
is **stubbed to return the whole graph**, so `#ifdef`-reference confirmation is
pack-global instead of per-component. Packs where two files `#define` the same name but
only one `#ifdef`s it will behave differently under Pintonium than under OF. Phase 3's
option-discovery conformance rows must implement the component analysis properly — the
stub is marked TODO; don't replicate it.

### 7.4 `ShaderProperties` coverage — and its gaps

Implemented: engine flags (`oldLighting`, `separateAo`, `backFace.*`, `clouds`,
`shadow.*`, `frustum.culling`, `beacon.beam.depth`, `rain.depth`…), `scale.<pass>`,
`size.buffer.<buf>`, `alphaTest.<pass>`, `blend.<pass>[.<buf>]`, `flip.<pass>.<buf>`,
`program.<name>.enabled`, SSBOs, images, custom uniforms/variables, textures (all three
App F.5 source forms incl. `dynamic/lightmap_1`), screens/profiles/sliders keys.

**Gaps vs the App F contract (each is a Phase 3 conformance row Pintonium cannot
validate):** `sliders=` parsed but functionally dead (no slider widget); **no
`version.<mcver>` gate**; `texture.<stage>.<sampler>` filter/wrap suffixes stripped and
ignored; `dynamicHandLight` parsed, never consumed; raw-texture data types partially
TODO.

### 7.5 Directive scanning — the silent-dead-comment problem

`const` directives fully work and dispatch into Pack/RenderTarget/Shadow directives
(half-lives, `sunPathRotation`, `ambientOcclusionLevel`, `colortexNFormat/Clear/
ClearColor` + legacy `gcolor/gdepth/gnormal/composite/gaux1-4` aliases, `gdepth`→RGBA32F
upgrade registration, shadow map params, `workGroups`). `/* DRAWBUFFERS:… */` and
`/* RENDERTARGETS:… */` in fragment sources work (single-digit vs comma-separated
respectively).

**But `DispatchingDirectiveHolder.acceptCommentStringDirective/…Int…/…Float…/
acceptUniformDirective` are all TODO no-ops** — every legacy comment directive
(`/* SHADOWRES */`, `/* SHADOWFOV */`, `/* SHADOWHPL */`, `/* GAUX4FORMAT */`, the
`gdepth` uniform upgrade trigger) is **silently dead**. For Schmaloogium's classic-pack
matrix (SEUS Renewed / Chocapic13 V9 / projectLUMA are exactly the era that uses these
forms), Phase 3's "all three syntactic forms" requirement is validated by this failure:
Pintonium gets away with it only because its working packs are modern const-style.

### 7.6 Standard macros — the OQ-7 reference set

`gl/shader/StandardMacros.createStandardEnvironmentDefines()`: `MC_VERSION` (5-digit OF
format via shim), `MC_MIPMAP_LEVEL`, `MC_GL_VERSION`/`MC_GLSL_VERSION` (regex-parsed
driver strings), `MC_OS_*`, `MC_GL_VENDOR_*`, `MC_GL_RENDERER_*`, every GL extension as
`MC_<ext>` (core-style `glGetStringi` enumeration; TODO notes OF's use-based filtering
differs), `MC_NORMAL_MAP`/`MC_SPECULAR_MAP`, texture-format defines, `MC_RENDER_STAGE_*`.
Plus `IrisDefines` (`BIOME_*` + categories — 1.12 adds modern-name biome aliases with
`-1` for missing) and `IRIS_FEATURE_*`.

This is the §3.5 identity set implemented and pack-tested. Notable for OQ-7: **Pintonium
defines no `IRIS_VERSION` at all** (`getIrisDefines()` empty) — packs cannot
version-detect it, which costs it nothing only because its target packs are Iris-era.
Schmaloogium's planned `SCHMALOOGIUM` macro + honest flags is strictly better; the
`MC_VERSION` 10904-format shim and driver-string regex parsing are the parts to reuse.

**Relevant to:** Phase 3 (near-complete reference; adopt jcpp, fix the gaps), Phase 2
(golden-run targets: this front-end is what "parse all seven matrix packs" competes
against), Phase 12 (the parsed screen/profile model), OQ-7.

---

## 8. Identity layer (Phase 9 relevance)

### 8.1 Block mapping — the dual-spec crown jewel

`shaderpack/IdMap.java` + `forge122/.../materialmap/VintageBlockMaterialMapping.java`
resolve `block.properties` entries against live 1.12.2 registries with **metadata ids,
property predicates, tag expansion, still↔flowing fluid aliases**, and — critically for
Schmaloogium's dual-spec matrix packs — a **modern→1.12 name alias table**
(`grass_block`→`grass`, `short_grass`→`tallgrass`, `tall_grass`→`double_plant`,
`dead_bush`→`deadbush`, `sugar_cane`→`reeds`, `lily_pad`→`waterlily`, `cobweb`→`web`,
`redstone_lamp`→`lit_redstone_lamp`), including a special case for modern packs'
`minecraft:grass`. First writer wins (`putIfAbsent`, order-significant). Defaults fall
back to OF legacy numeric IDs when no `block.properties` exists.

**Entity properties dual-version quirk (adopt this):** if parsing `entity.properties`
under the ≤1.12 rules yields an empty map, Pintonium **re-parses with `MC_VERSION`
forced to 11300** so modern packs' named entity ranges still load. This is exactly the
era-bridging Schmaloogium needs for BSL/Complementary/Sildur's current releases — a
small, proven mechanism for the dual-spec tier.

Item/entity state predicates are skipped with a warning ("currently not supported").
Held-item uniforms (`heldItemId`, `heldBlockLightValue`, `oldHandLight`,
`dynamicHandLight`) are **stubs on 1.12.2** (`VintageIdMapUniforms` empty TODO) — no
reference there; Phase 9 designs these from App D alone.

### 8.2 Per-draw delivery — the constant-attribute trick

Entity IDs are delivered **without touching the vertex format**:
`GL30.glVertexAttribI3i(11, shaderEntityId, 0, 0)` sets `mc_Entity` as a *generic vertex
attribute constant* for the draw (verified at `RenderGlobalMixin.java:459`), with
`CapturedRenderingState.setCurrentEntity` + the `entityId` uniform as fallback for
non-attribute draws. For Schmaloogium Phase 9/10: on a compat context, any attribute
whose array is disabled reads the constant — so entity/TE draws can carry ids with zero
vertex-format intrusion, reserving the 56-byte extended format for terrain where
per-vertex data is actually needed. (Phase 10's per-draw exclusion analysis should weigh
this against OF's exact delivery mechanism; contract check required, but the mechanism
is proven on real packs.)

**Gaps:** `blockEntityId` stamping has no 1.12.2 caller (uniform is a constant 0);
hurt-flash `entityColor` is undelivered (the `EntityPatcherNew` machinery is modern-only;
1.12's FF TexEnv hurt-flash is invisible to bound GLSL programs — this is a root cause
of the README's "entities brighter" bug, §19).

**Relevant to:** Phase 9 (resolution tables, precedence, dual-spec mechanisms), Phase 10
(delivery trade-offs), Phase 3 (`layer.*` parsing exists; consumption is TODO here —
Schmaloogium assigns resolution to P9 and dispatch to P7).

---

## 9. Vertex pipeline (Phase 10 relevance) — a different strategy, worth studying

Pintonium **never extends the vanilla 28-byte `BufferBuilder` format** (no
BufferBuilder/Tessellator/VertexBuffer mixins exist on 1.12.2). Instead:

1. Terrain is built by the **vanilla** `blockRendererDispatcher.renderBlock` into a
   vanilla-format buffer inside the chunk task (Forge render layers respected), while
   `VintageChunkBuildContext.recordRenderedQuads(layer, startVertex, endVertex, state,
   pos)` records a `QuadMetadata(startQuad, endQuad, blockId, renderType, localXYZ,
   lightValue)` range per block.
2. `convertVanillaDataToCeleritasData` **re-encodes** every quad into the engine's own
   chunk formats, computing extended attributes per quad: `mc_Entity`
   (blockId + renderType packed shorts), `mc_midTexCoord` (quad-average UV × 32768 as
   u16), `at_tangent` (UV-edge method with handedness in w — `ChunkVertexExtendedData.
   computeTangent`), `iris_Normal`, **`at_midBlock` + block light** — all in a
   thread-local side channel (`ChunkVertexExtendedData`) safe across chunk-build workers.
3. Formats: `VanillaLikeChunkVertex` (48 B) and `CompactChunkVertex` (40 B), both
   carrying the full extended set.

Contrast with Schmaloogium Phase 10 (stamp into the vanilla buffer at write time via the
entity-data stack, extend format 28→56 B, hook both draw paths):

| Axis | Pintonium | Schmaloogium (planned) |
|---|---|---|
| Vanilla cooperation | Replaces terrain renderer outright (OQ-5 adversary) | Hooks vanilla paths (coexists) |
| Cost when shaders off | Chunk renderer always pays re-encode | Zero (side channel idles) — G2.4 rule 5 favors ours |
| Extra memory pass | Yes (full re-encode) | No |
| Stack vs ranges | Sequential range recording suffices (no push/pop stack) | Entity-data stack (App E rows 3-4, 9) |
| Entity/TE attributes | Constant-attribute trick; no per-vertex data | Per App C contract |
| `at_midBlock` | **Already implemented** (G8/S4's canonical growth item — free reference) | Designed, unwired |

The per-quad math (normal = normalize((v2−v0)×(v3−v1)), UV-delta tangent with
`w = sign(dot(bitangent, normal×tangent))`, midTexCoord = quad UV average) is implemented
in `ChunkVertexExtendedData`/`QuadUtil` — a numeric reference for App C.2's formulas and
their worked example.

**Coexistence (OQ-5) anchors:** Pintonium is precisely the class of mod Schmaloogium's
bail registry must detect — it `@Overwrite`s `RenderGlobal.renderBlockLayer/setupTerrain`
and zeroes vanilla render distance in `loadRenderers`. Detection anchors: mod ids
`embeddium` / `celeritas_shaders`, root packages `org.embeddedt.embeddium`,
`org.taumc.celeritas`, presence of `CeleritasWorldRenderer`. Vintagium/Nothirium share
the lineage. Add these to Phase 10's OQ-5 detection table.

**Relevant to:** Phase 10 (strategy contrast, tangent math reference, thread-safety
pattern, growth-item proof), Phase 9 (id stamping), OQ-5 (detection anchors), OQ-14
(no evidence either way — Pintonium sidesteps the baked-quad cache question by owning
the mesh path).

---

## 10. Shadow pass (Phase 8 relevance) — reference exists, but not where you need it

- `shadows/CommonShadowRenderer` (abstract): pack shadow directives
  (`shadowDistance`/half-plane, near 0.05/far 256 defaults, `shadowIntervalSize`,
  `shadowMapFov`, culling config, translucent/entity/player toggles), two frustum
  holders, hardware-PCF setup, old-pack swizzle.
- `shadows/ShadowCompositeRenderer`: shadowcomp fullscreen passes (targets never flip —
  §5.3 bug).
- **`ModernShadowRenderer` (modern module, 588 lines) is the only working implementation:**
  forced third-person, `createShadowModelView(sunPathRotation, intervalSize)` (texel
  snapping lives here), ortho via `ShadowMatrices.createOrthoMatrix` or legacy
  perspective when FOV set, frustum centered on player camera, vanilla terrain setup
  re-run against the shadow frustum, layers solid→cutout-mipped→cutout, then entities,
  mipmaps, shadowcomp.
- **1.12.2: `VintageIrisRenderingPipeline.createShadowRenderer()` returns `null`**
  (`// TODO: Port the 1.12 shadow terrain renderer`). Shadow targets are still allocated
  and cleared every frame for shadow-enabled packs (wasted VRAM), the mixin calls
  `renderShadows(null, null)`, and chunk shadow programs are never registered. **The
  packs in Pintonium's working list are no-shadow or shadow-tolerant configs.**

Transfer value for Phase 8: the camera/snapping math (`ShadowMatrices`,
`createShadowModelView`) and pass sequencing are portable references; the traversal is
not (theirs re-runs their own chunk collector; Schmaloogium must traverse vanilla's
`RenderGlobal` per App E rows 1-2). Phase 8 remains one of the least de-risked phases —
Pintonium does not change that.

**Relevant to:** Phase 8 (math reference only), Phase 5 (shadow target structure).

---

## 11. Textures (Phase 13 relevance)

- **Noise:** `targets/backed/NoiseTexture` — GL_RGB, LINEAR/REPEAT, `Random(0)`-seeded
  bytes, 256² default (`noiseTextureResolution`). **Divergence:** Schmaloogium's contract
  specifies an xorshift generator (Phase 13 spec); verify against App — if packs depend
  on OF's exact noise values, `Random(0)` is wrong; if they only need *a* repeatable
  noise field, either works. `texture.noise=<path>` override supported.
- **Custom textures:** all three App F.5 forms (pack PNG with `.mcmeta` blur/clamp;
  `minecraft:` asset locations incl. `dynamic/lightmap_1`; raw 1D/2D/3D with
  auto-generated `customtexN` names patched into programs), per-stage binding
  (gbuffers/deferred/composite), override-of-colortex disambiguation. A working App F.5
  checklist — but note the filter/wrap suffix gap (§7.4).
- **`_n`/`_s`:** `PBRTextureManager` keyed per *bound texture id* (not an atlas stitch!),
  defaults `0x7F7FFFFF` normal / `0x00000000` specular, bound as `normals`/`specular`
  dynamic samplers. **Divergence:** Schmaloogium's contract is companion *atlases* with
  matching mip chains (§4.6, App E rows 10-11). Pintonium's approach works because its
  terrain binds one atlas; it does not solve sprite-animation sync or per-sprite
  companions. Phase 13's atlas design has no reference here — but the
  `MC_NORMAL_MAP`/`MC_SPECULAR_MAP` macro wiring (§7.6) does.
- **`atlasSize`:** registered but the notifier is a no-op with a TODO debating OF
  semantics — another App D row Pintonium can't validate.

**Relevant to:** Phase 13 (custom-texture model + noise; atlas design outstanding),
Phase 3 (macro wiring).

---

## 12. The GLSL transformation layer — the AGPL elephant

Every pack program passes `foss_transform/ShaderTransformer` (ANTLR-based, from
`org.taumc:glsl-transformation-lib` — a fork of douira's glsl-transformer): GLSL version
upgrades to 330-core-ish, `gl_FragColor`→`gl_FragData[0]`→`layout(location=N) out`,
`attribute/varying`→`in/out`, `texture2D*`→`texture*`, reserved-word renames,
fixed-function neutralization in composite passes, Sodium chunk-vertex mapping,
cross-stage in/out matching with missing-varying injection, dead-code elimination.
Results are LRU-cached (256 entries) in memory; the disk cache exists but is disabled
(`if (true || …)`).

Three Schmaloogium-relevant rulings:

1. **License:** glsl-transformer is AGPL-3.0 (D-8 prohibition). The taumc fork's license
   is unverified — treat as AGPL. **Do not copy, do not depend on it.** Pintonium itself
   bundles an LGPL/AGPL-mixing fat jar; that's their compliance problem, not a precedent
   to follow.
2. **Necessity:** this machinery exists because Iris targets *modern* packs (GLSL 330+
   core-style sources, `vaPosition`, no fixed function) and must run them everywhere.
   Schmaloogium's contract era is GLSL-120 fixed-function-coupled packs on a compat
   profile — `gl_Vertex`, `ftransform()`, `gl_FragData` are *native* there. Phase 3's
   preprocessor + Phase 4's dual-form geometry handling + per-program upload cover the
   G6 matrix without any AST work. **Pintonium confirms this implicitly:** its 1.12.2
   entity bridges compile *raw unpatched pack GLSL* and only old-style packs survive
   that path — its own architecture proves GLSL-120 packs run natively on the 1.12.2
   compat context.
3. **Era-bridge caution (OQ-18/G8):** Pintonium's dual-spec success with Complementary
   Unbound/Solas rests substantially on this transformer (plus layout heuristics in
   §13). If Schmaloogium's dual-spec tier stalls on modern syntax, the string-level
   subset (version directive handling, `texture2D` defines, the `centerDepthSmooth`
   define from §6.3) is the compliant escalation path before any AST is considered.

**Relevant to:** D-8 compliance, Phase 3 (preprocessor suffices), Phase 4 (dual-form
geometry vs AST), G8/OQ-18.

---

## 13. The 1.12.2 "legacy compatibility" shaders — a technique and a warning

`VintageIrisRenderingPipeline` (1175 lines — the most AI-generated-looking file in the
tree) synthesizes GLSL-130 fixed-function-consumer programs as Java strings for draws
the chunk path doesn't cover (entities, block entities, particles, hand, lines, beacon
beam), choosing drawbuffer output expressions by **heuristics over the pack's drawbuffer
layout** ("Solas-style" `[0,3]`, "oldComplementaryLayout" by presence of buffers
1/4/6/7), with magic lighting constants tuned by trial and error
(`0.03125/1.06667` lightmap scaling, `0.9333` block-light cap, `0.18 + 0.82*light`).

Assessment: it *works* for its three supported packs, and it is exactly the
"near-full compatibility with demanding packs via generative AI" the project owner
described. For Schmaloogium it is (a) a **technique** — synthesized compat programs as a
degradation-ladder rung when a pack program can't serve a draw — and (b) a **warning** —
pack-layout heuristics silently misfire on unusual packs (this is a root cause of the
Solas entity-brightness bug, §19), and contract-visible behavior must come from the
contract, not heuristics (G4.2). Schmaloogium's backup-chain design (App A.2) is the
principled version of the same need.

**Relevant to:** Phase 4 (backup chains as the principled mechanism), Phase 7 (internal
default pack content), G2.4 (ladder rung 3 design space).

---

## 14. GUI, persistence, expressions (Phases 11/12)

- **GUI:** `forge122/.../gui/VintageShaderPackSelectionScreen` +
  `VintageShaderPackOptionsScreen` are **hand-written vanilla `GuiScreen`s** —
  pack list, scrolling option screens, sub-screen navigation, profile cycling,
  lang-file tooltips with `en_us` fallback, name prettification. No ModularUI, no
  sliders. **This is literally Schmaloogium's OQ-9 fallback (vanilla-GuiScreen minimal
  UI), already built and working on 1.12.2** — it de-risks the fallback to near-zero
  and provides a screen-model binding reference either way.
- **Persistence:** changed options → `shaderpacks/<pack>.txt` (ISO-8859-1 Properties),
  merged through a queue on reload; global config at `<config>/embeddium-shaders.
  properties`. Round-trip works; matches Phase 3/12's format split.
- **Expressions:** `kroppeb.stareval` (vendored) + `IrisFunctions` parse/evaluate
  `uniform.<type>.<name>=<expr>` / `variable.*`, with topological sort, cycle detection,
  dead-uniform elimination (`CustomUniforms.optimise()`), evaluation on program switch
  after built-ins. The App F.6 function set lives in `IrisFunctions` as a concrete
  checklist. stareval's upstream repo 404s today — historically MIT-credited; **verify
  license before reuse**; the interface (parsed AST → evaluator with resolver
  indirection) matches Phase 11's interpreter-first plan (G2.5).

**Relevant to:** Phase 12 (OQ-9 fallback proof), Phase 11 (evaluator architecture
reference), Phase 3 (persistence formats).

---

## 15. GL modernization (Phase 14 / G8 relevance)

Pintonium is *more* modern here than Schmaloogium's baseline plan — usable evidence for
several `[U]` claims in RESEARCH.md §6.2:

- **DSA tiering:** `DSACore` (GL 4.5) → `DSAARB` → bind-to-edit fallback, chosen at init.
- **GL 3.3 sampler objects** (`GlSampler`) for shadow HW filtering and mipmap'd custom
  textures, with per-unit bind caches and GL 4.5 `glBindSamplers` batching — Phase 14's
  sampler-object design, deployed.
- **Compute / SSBO / image load-store** (GL 4.2/4.3/4.4 + ARB/EXT fallbacks, indirect
  dispatch, screen-relative SSBO resize, VRAM sanity via `NVX_gpu_memory_info`) — G8/S2's
  entire shopping list, present and pack-exercised, on the 1.12.2 compat context.
  **This is the strongest available evidence that G8/S2 is feasible on Cleanroom.**
- **KHR_debug:** object labels + per-phase push/pop groups via Embeddium's `GLDebug`,
  gated by `-Dceleritas.enableGLDebug` (note: a push/pop imbalance bug exists in
  `setPhase`, §17 — copy the pattern, not the wiring).
- **No PBO/async readback anywhere** — Schmaloogium Phase 14's async center-depth design
  has no reference here; but see §6.3, which may obviate it.

**Relevant to:** Phase 14 (sampler objects, KHR_debug, tiering patterns), G8/S2
(feasibility evidence), OQ-22 (ledger evidence).

---

## 16. Environment & platform notes (Phase 1 relevance)

- 1.12.2 runs on **LWJGL3 via lwjgl3ify or Cleanroom** (README; run config loads
  `Lwjgl3ifyCoremod` + MixinBooter; a coremod relocates LWJGL2 references in mod
  classes; RFB detected at mixin-plugin load). **Pintonium on Cleanroom 1.12.2 is the
  existence proof for Schmaloogium's entire platform bet (D-1, G2.2).**
- GL floor in practice: GL 3.2+ compat for the shader core, with graceful per-feature
  gating above that; fixed-function interop throughout the 1.12.2 path. `GL_QUADS`
  present (terrain/entity FF paths rely on it). Consistent with D-9.
- Mixin registration via a plugin that class-scans a package — no per-mixin JSON upkeep;
  `compatibilityLevel: JAVA_8` with jvmdowngrader for the runtime. Schmaloogium's
  Java-25-on-Cleanroom posture makes most of this moot; the class-scan plugin is a
  convenience worth considering for Phase 1's mixin wiring.
- Startup sequencing: `GameSettings.loadOptions` (early init) →
  `OpenGlHelper.initializeTextures` RETURN (GL caps → init renderer + load pack) →
  `GuiMainMenu.initGui` RETURN (loading complete). A proven three-stage bootstrap for
  Phase 1/7's engine bring-up on 1.12.2.

**Relevant to:** Phase 1 (bootstrap, mixin wiring, lwjglx-adjacent posture), OQ-3
(Pintonium never touches context creation — supports the spike's fallback as the default
plan), OQ-4 (hot-path injections proven on this loader family).

---

## 17. Verified bug catalogue — pitfalls, not rumors

Confirmed by direct read or strong in-code evidence; paths relative to repo root.
Organized by the lesson each teaches.

| # | Bug | Location | Lesson for Schmaloogium |
|---|---|---|---|
| B1 | `drynessHalflife` const directive writes into `wetnessHalfLife` field | `common-shaders/.../properties/PackDirectives.java:254-255` | Directive→field maps need conformance tests per row (Phase 3 doc gate exists for this) |
| B2 | Comment-style directives (`/* SHADOWRES */`, `/* GAUX4FORMAT */`, `gdepth` upgrade) silently dead | `.../parsing/DispatchingDirectiveHolder.java` | Classic-era packs need all three syntactic forms (Phase 3 spec) — Pintonium's matrix doesn't |
| B3 | `computeWeaklyConnectedComponents` stubbed → option confirmation is pack-global, not per-component | `.../include/IncludeGraph.java` | OF option semantics require the component analysis |
| B4 | Shadow buffer flip stubbed; shadow samplers always read main | `.../shadows/ShadowRenderTargets.java` (`TODO: Actually flip`) | Flip semantics are contract-visible; state-machine-test them (Phase 5 doc gate) |
| B5 | `ColorSpaceFragmentConverter(width, width, …)` — height gets width | `.../pipeline/CommonIrisRenderingPipeline.java:1138` | Review AI-generated call sites argument-by-argument |
| B6 | `blendFuncNotifier` never assigned on 1.12.2 → NPE if pack declares `blendFunc` | forge122 has no GlStateManager state-listener module | Every notifier needs a wired producer; audit in Phase 6/7 |
| B7 | GLDebug group push/pop asymmetry in `setPhase` (pops unconditionally, pushes selectively) | `CommonIrisRenderingPipeline.java:1272-1282` | Dev-only, but erodes trust in debug tooling |
| B8 | ShaderMap exception-unwrap infinite loop (`e.getCause()` vs `trueCause.getCause()`) | `.../programs/ShaderMap.java:51-55` | Upstream had it right; port drifted — diff ports against upstream |
| B9 | depthtex2 debug-named `"dephtex2"`; alt texture never labeled; duplicated `buffersToBeCleared.add` block | `RenderTargets.java:66`, `RenderTarget.java:48-51`, `ShadowRenderTargets.create()` | Cosmetic, but markers of unreviewed bulk commits |
| B10 | `ProgramSamplers.CustomTextureSamplerInterceptor.addDynamicSampler(...)` overload returns `false` unconditionally | `ProgramSamplers.java:320-323` | Landmine overloads: masked today, breaks the next refactor |
| B11 | `GLStateManagerImpl.getColorMask()` hardcodes all-true | forge122 `GLStateManagerImpl` | State save/restore must read real state (G4.6 cooperation rule) |
| B12 | `ProgramSet` composite-compute loop bounded by `deferred.length`; `parseDimensionMap` double-inserts `"*"`; `PropertiesPreprocessor` strips `#` from values | `ProgramSet.java:151`, `ShaderPack.java:372-377`, `PropertiesPreprocessor` | Copy-paste class of bugs; golden-run tests (Phase 2) are the net |
| B13 | All 16 colortex always allocated; no used-buffer analysis | `PackRenderTargetDirectives` | Schmaloogium's scan-driven sizing (Phase 3) is the better design — keep it |

---

## 18. Contract divergences — what Pintonium does that Schmaloogium must NOT copy

| Area | Pintonium (Iris semantics) | Schmaloogium contract (OF G6 / Apps) |
|---|---|---|
| Attribute locations | mc_Entity=11, midTexCoord=12, tangent=13, midBlock=14 | **10 / 11 / 12** (Phase 4 pre-bind) |
| Texture units | Dynamic per-program allocation; colortex0 pinned to unit 0 in fullscreen; 0/1/2 reserved | **Fixed App B.3 map**, incl. depthtex1 at unit **11** |
| Flip at frame end | SwapPass copies alt→main | Last-writer-leaves-flip-enabled (App F.7) — decide copy-back vs carryover explicitly |
| Dimension folders | `dimension.properties` + world0/-1/1, full program sets, no base merge | world −128..128 scan; only `.vsh`/`.fsh` read; empty folder disables |
| `(internal)` sentinel | Absent (hardcoded false) | Internal default pack required (Phase 7a) |
| `version.<mcver>` gate | Absent | Required (Phase 3) |
| Entity id delivery | Constant attribute + uniform fallback | App C/D mechanism (constant-attribute trick viable with contract check) |
| Terrain attributes | Own chunk renderer, re-encoded meshes | Vanilla-path vertex side channel, 56-byte format |
| Noise RNG | `java.util.Random(0)` | Xorshift per contract (verify App) |
| colortex count | 16 always allocated | Scan-sized (Phase 3), growth-shaped (D-4) |
| Pack identity macros | No `IRIS_VERSION`; `IS_IRIS` etc. | OF-era identity + honest flags + `SCHMALOOGIUM` (OQ-7 option 3) |
| Sky/weather/clouds | Vanilla FF, unhooked | Full phase-dispatch coverage (App A.1/E) |

---

## 19. README's known bugs → probable root causes (where the hard problems actually are)

1. **"TAA/Bloom/FAA/Chromatic Aberration blurring"** — these are all temporal or
   neighborhood-sampling effects fed by `previous*` matrices, depth copies, and motion
   vectors. Contributing suspects in-tree: no sky/weather phase coverage (motion/TAA
   inputs inconsistent), the B5 color-converter sizing bug, `gbufferPrevious*` suppliers
   allocating fresh matrices per query (PERF-flagged), and mipmapped-buffer staleness
   TODOs. For Schmaloogium: **temporal effects are the conformance long tail** — Phase 2
   scenes must include camera-path motion to catch this class, and Phase 6's
   previous-frame snapshot semantics must be exact.
2. **"Solas: entities/tile entities brighter"** — root cause is structural: entities
   render through synthesized legacy-compat programs with heuristic lighting constants
   and pack-layout detection (§13), while terrain goes through the real pack program.
   Any pack whose entity lighting depends on data the heuristic guesses (Solas's `[0,3]`
   layout) mismatches. Lesson: heuristic compat layers are a degradation rung, never a
   contract path (G4.2) — and per-draw uniform delivery (`entityColor`, lightmap matrix)
   must be real, which Pintonium's 1.12.2 path doesn't implement at all (§8.2 gaps).

---

## 20. Trust tiers (summary)

- **Trust and reuse freely (LGPL-3.0, matches upstream Iris):** pack front-end structure
  (include graph, options, profiles, properties, IdMap), buffer ping-pong/flip/clear
  machinery, depth-copy strategy tiering, uniform cadence machinery, `Program.use()`
  barrier, backup-chain model, DSA tiering, sampler objects, KHR_debug pattern,
  CenterDepthSampler GPU-smoothing design, stareval expression engine (license
  verification pending), the mixin injection-point map, the FF-matrix capture mechanism,
  the Framebuffer depth-texture swap.
- **Reuse as structure, re-derive the values:** StandardMacros identity set,
  VintageBlockMaterialMapping alias table, per-quad tangent/midTexCoord math,
  ShadowMatrices/snapping math, legacy-compat shader *technique*.
- **Do not copy (contract conflict or license):** attribute location numbering, dynamic
  unit allocation as a contract replacement, dimension-folder semantics, `Random(0)`
  noise, anything from `glsl-transformation-lib` (AGPL), pack-layout lighting heuristics,
  the stubbed/dead features in §17.
- **No help available (design from RESEARCH.md alone):** shadow pass on 1.12.2, sky/
  weather/clouds hooks, held-item uniforms, `blockEntityId`/`entityColor` delivery,
  companion atlas stitching, `version.<mcver>` gate, internal default pack, sliders UI,
  render-quality multipliers.

---

## 21. Quick-reference: phase → Pintonium's answer

| Phase | What Pintonium gives you | Confidence |
|---|---|---|
| P1 Foundation | Seam interface inventory (glsm + shim services); 3-stage bootstrap; class-scan mixin plugin; Cleanroom feasibility proof | High |
| P2 Conformance | Working-pack matrix as fixtures; blurring bug class → motion scenes required; this front-end as golden-run competitor | Medium |
| P3 Front-end | 80% of the subsystem, LGPL, pack-tested — adopt jcpp (Apache-2.0), port structure, fix B1-B3/B12 gaps, add contract-only keys | **Very high** |
| P4 Registry | Backup-chain model + barrier + per-pass state bundle, working | High |
| P5 Buffers | Ping-pong/flip/clear/depth-copy/resize nearly complete; depthtex0 swap mixin is essential reading | **Very high** |
| P6 Uniforms | Cadence model, capture mechanism, smoothing math, notifier pattern; centerDepthSmooth GPU alternative to sync readback | **Very high** |
| P7 Render loop | Proven injection-point timeline for `EntityRenderer`/`RenderGlobal`; composite-at-TAIL; gaps at sky/weather/clouds | High |
| P8 Shadow pass | Camera/snap math only; traversal & 1.12.2 wiring absent | Low |
| P9 Aliasing | Registry resolution + dual-spec alias table + entity version re-parse trick; held-item absent | High |
| P10 Vertex pipeline | Alternative strategy + tangent math + constant-attribute trick + `at_midBlock` precedent + OQ-5 detection anchors | Medium |
| P11 Expressions | stareval evaluator + function set checklist (verify license) | Medium |
| P12 GUI | OQ-9 fallback already built (vanilla GuiScreen option/profile screens) | High |
| P13 Textures | Custom-texture model + noise; companion atlases absent | Medium |
| P14 GL modernization | Sampler objects, DSA tiers, KHR_debug, compute/SSBO/images on 1.12.2 compat — G8/S2 feasibility proof | High |

**Bottom line:** Pintonium is weak exactly where Schmaloogium's design is conservative
(contract fidelity, classic-pack features, shadow pass) and strong exactly where
Schmaloogium's design is unproven (1.12.2 hook points, FF interop, buffer/uniform
machinery, front-end). Treat its `common-shaders` tree as the primary LGPL reference
implementation for Phases 3–7 and its `forge122` tree as a field report from the
1.12.2 platform — with the §17/§18 catalogues as the standing list of what not to
inherit.

*End of analysis.*
