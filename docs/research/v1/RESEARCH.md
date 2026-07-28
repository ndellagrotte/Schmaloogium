# Schmaloogium — Research Document

> **Status:** First complete draft, 2026-07-24.
> **Role:** Primary source of truth feeding the future Schmaloogium **design doc** (which is
> explicitly out of scope here). This document consolidates every research finding, decision,
> constraint, opportunity, and open question gathered before design begins.
> **Audience:** the design-doc author (project owner + AI assistants working the repo).

---

## 0. Front matter

### 0.1 Reading guide

| Sections | What they answer |
|---|---|
| §1–§2 | *Why* — mission, scope, why this niche exists and why prior attempts died |
| §3–§4 | *What* — the shader-pack contract packs rely on, and how the reference implementation (OptiFine 1.12.2) satisfies it |
| §5–§6 | *Where* — the Cleanroom platform: verified facts, and what a modern stack newly allows/forbids |
| §7–§9 | *How (research-level)* — directional decisions, conformance strategy, milestones |
| §10–§12 | Legal posture, open-question register, annotated source index |
| Appendices A–H | Self-contained lookup tables the design doc will work from directly |

### 0.2 Confidence tags

Every non-obvious claim in this document carries a tag:

| Tag | Meaning |
|---|---|
| `[V:doc]` | Verified against the shipped OptiFine pack-author docs (`doc/shaders.txt`, `doc/shaders.properties` — legally the cleanest source; see §10) |
| `[V:observed]` | Verified behavior of the reference implementation (decompilation-derived digests; restated here as *behavior*, never as code structure to be copied — see §10.2) |
| `[V:template]` | Verified against this repo's build files (ground truth as committed) |
| `[V:mcp]` | Verified via the `cleanroom` MCP server (docs/mappings/API/example DBs), 2026-07-24 |
| `[V:web]` | Verified against a live web source (URL in §12.5 or inline), accessed 2026-07-24 |
| `[D-n]` | A **decision** — a choice already made (logged in §1.3), not a verifiable fact |
| `[U]` | Unverified claim originating from AI reasoning in the idea doc; every `[U]` must have an open-question row (§11) or be upgraded before the design doc relies on it |
| `[A]` | Working assumption, explicitly overturnable |
| `[Q:OQ-n]` | Open question — see the register in §11 |

Date-stamp discipline: Cleanroom is an alpha-stage platform with a **daily release cadence**
(§5.2); web/MCP facts rot fast here. All were gathered **2026-07-24** unless noted.

### 0.3 Conventions

- "OF" = OptiFine. "The reference implementation" = OptiFine HD_U **G6_pre1** for MC 1.12.2,
  studied via the decompiled `schlorbium-project` sibling repo (see §12).
- "The contract" = the pack-author-facing shader-pack format (§3) that existing shader packs
  are written against. Schmaloogium treats it as a fixed external interoperability surface.
- The idea doc (`SCHMALOOGIUM_IDEA_DOC.md`, see §12) is cited by its assistant-turn section
  headings (e.g. *IDEA_DOC §"Why it's technically feasible"*), not line numbers — it is a
  chat log and may be regenerated.

---

## 1. Mission, scope, and non-goals

### 1.1 Mission statement

> **Schmaloogium provides OptiFine/Iris-format shader-pack support for Cleanroom clients on
> Minecraft 1.12.2. Just shaders. Nothing else.**

Strict Unix philosophy: one job, done well, composable with the rest of the Cleanroom
ecosystem instead of competing with it. `[D-2]`

### 1.2 Non-goals (enumerated, with reasons)

This list exists so that scope creep must argue against a written record
(*IDEA_DOC §"2. Things you missed" — "a written non-goals list"*):

| Non-goal | Reason |
|---|---|
| Performance tweaks (chunk pipeline rewrites, fast math, smooth FPS, lagometer…) | Other Cleanroom-ecosystem mods own performance (Nothirium, Celeritas — §2.3); OF's perf core (`Config`, chunk-cache machinery, array caches — DESIGN.md §3.1) is out of scope entirely |
| MCPatcher feature set (CTM, CIT, CEM, custom colors/sky/GUIs, random entities, emissive, dynamic lights…) | An entire second product (DESIGN.md §3.2 lists ~17 subsystems); not shaders |
| Capes / cosmetics / player items | Off-mission (DESIGN.md §3.4) |
| Telemetry, version-check HTTP, crash upload | Off-mission; also a distribution liability (DESIGN.md §3.5) |
| Installer / patch-jar distribution | Schmaloogium is an ordinary mod jar; OF's installer/xdelta apparatus (DESIGN.md §4) exists only because OF patches vanilla — irrelevant to a Cleanroom mod |
| Vanilla-launcher or stock-Forge 1.12.2 support | Cleanroom-exclusive `[D-1]`; a thin abstraction seam keeps a later port *possible* but it is not a goal (§7.2) |
| Server-side anything | Client-only, like every shader engine |
| UBO-based uniform redesign | The contract forbids it: packs declare default-block GLSL-120-style uniforms, so per-program uniform upload is structurally required (§6.1) `[V:doc]` |
| FXAA/AA/AF features | OF's interlock matrix exists *because* it bundles these (§4.7); Schmaloogium simply doesn't have them |

**A scope dividend worth recording:** a large fraction of OF's shader-adjacent complexity is
the *feature interlock matrix* — packs refused while FXAA/anisotropic-filtering/fast-render
are enabled, per-feature conflict warnings, GUI disable logic `[V:observed]`. With no other
features, that entire category of code ceases to exist in Schmaloogium
(*IDEA_DOC §"3. Things I'd affirm, not reconsider"*).

### 1.3 Decision log

Decisions already made in the idea-doc phase. The design doc must honor these or overturn
them with explicit cause:

| ID | Decision | Rationale (source) |
|---|---|---|
| D-1 | **Cleanroom-exclusive** (ideological, and the market aligns: the OF-free 1.12.2 ecosystem is exactly the audience with no shader option — confirmed empty niche, §2.3) | IDEA_DOC §"3. Things I'd affirm" |
| D-2 | **Unix-philosophy scope**: shaders only; written non-goals list (§1.2) | IDEA_DOC mission-statement turn |
| D-3 | **Target = a fixed pack-compatibility matrix**, not "Iris-spec parity" (unmeasurable, moving). Same work, but a finish line that exists | IDEA_DOC §"1. Reconsider targeting Iris-spec" |
| D-4 | **Stage registry architected for the full modern stage set from day one** (setup, begin, shadow, shadowcomp, prepare, gbuffers, deferred, composite, final — §3.6) even though v0.1 wires only a subset — retrofitting stage architecture is the structural pain that kills projects | IDEA_DOC §"1. Reconsider…", "the one architectural consequence" |
| D-5 | **Mixin-based hooks, no class replacement.** ~25–30 injection points across ~a dozen classes (Appendix E), each small and targeted | IDEA_DOC §"Why it's technically feasible" |
| D-6 | **Engine-core / loader-glue seam**; core is headless-testable and GL-abstracted enough to unit test | IDEA_DOC §"3. Things I'd affirm" |
| D-7 | **GPL-3.0-or-later license** for Schmaloogium (overturns the idea-doc MIT choice — user decision 2026-07-24; the template's MIT-style LICENSE `[V:template]` must be replaced) | IDEA_DOC legal notes, superseded by user decision 2026-07-24; §10 |
| D-8 | **Published docs + OSS source OK; license-compliant reuse permitted.** Published specs usable freely; open-source engines (Iris, Angelica) may be read for understanding, and their LGPL-3.0 code may be incorporated with license compliance (LGPL-3.0 combines into a GPL-3.0-or-later work; preserve notices, mark modifications — §10.3). Two prohibitions stand: Iris's bundled glsl-transformer is **AGPL-3.0 — never copy**, and the OF decompile is behavioral-observation-only | User decision 2026-07-24, revised same day with the `[D-7]` license change; §10 |
| D-9 | **Compatibility-profile GL baseline.** No core-profile rewrite: pack parity chains the engine to compat profile (§3.5, §6.1) | IDEA_DOC §"First, a verification note and an important correction" |
| D-10 | **Conformance harness from week one**: screenshot-diff + tiered conformance against the pack matrix (§8) | IDEA_DOC §"2. Things you missed" |

---

## 2. Prior art and niche analysis

### 2.1 What OptiFine is on 1.12.2, and its architectural cost

OptiFine remains the only working shader engine for MC 1.12.2. Its cost structure
`[V:observed]` (DESIGN.md §4–5, §9):

- **Distribution:** a self-installing patch jar carrying GDIFF/xdelta binary diffs (1,063
  patch entries) applied against the user's own vanilla jar, MD5-verified — the copyright
  workaround for shipping modified vanilla classes.
- **Integration:** a LaunchWrapper tweaker + class transformer that **wholesale-replaces 112
  vanilla classes** at class-load (the entire render stack among them: `EntityRenderer`,
  `RenderGlobal`, `RenderChunk`, `BufferBuilder`, `Tessellator`, `GlStateManager`,
  `TextureMap`, …; full list in `files.txt`, summarized in DESIGN.md Appendix B), with an
  `AccessFixer` reconciling access flags to satisfy the verifier.
- **Consequences:** brittle across versions, a chronic source of mod incompatibilities, not
  redistributable in modpacks (all-rights-reserved), closed source, and incompatible with the
  modern 1.12.2 optimization ecosystem's coremods.
- Crucially, OF replaces classes **because its tooling predates Mixin**, not because the
  shader pipeline requires it. Every hook OF compiles into a replaced class is expressible as
  a targeted Mixin injection (*IDEA_DOC §"Why it's technically feasible"*; CleanMix
  divergence check pending `[Q:OQ-4]`).

The sibling repos in this workspace are live evidence of the pain: `schlorbium-fixes` (an ASM
coremod whose whole purpose is patching OF's own regressions) and `schlorbiumCITpatch` (a
Mixin mod patching OF's CIT internals, pinned to one exact `unpatched_srg` OF build). And the
Cleanroom ecosystem now maintains **OptiRefine** — a Mixin-based project whose entire purpose
is re-patching OptiFine into Cleanroom compatibility, with no usable releases yet and 181/249
features reimplemented `[V:web github.com/Ecdcaeb/OptiRefine]`. An ecosystem that must patch
a patcher — twice — is an ecosystem asking for a native citizen.

### 2.2 The Iris-backport graveyard `[V:web]` (statuses 2026-07-24)

The idea doc's failure analysis is now corroborated by the actual project landscape:

| Project | What | Status |
|---|---|---|
| **Spectra** (`kristitrnka/Spectra-Broken`) | Experimental Iris/Oculus backport to Forge 1.12.2 (LGPL-3.0, fork chain Asek3/Oculus ← IrisShaders/Iris) | Alpha; broken shader-select GUI, pack init failures; not OF-pack-compatible in practice; repo is a re-upload literally named "-Broken"; created + last push 2026-07-01 |
| `pixelreyn/Oculus-1.12.2` | Personal backport experiment | Mar–May 2026 activity, 1 star, no license — stalled |
| `luckyboy66666666/Oculus-1.12.2`, `rafi67000/Iris-1.12.2` | Study/stub repos | Inactive |
| Upstream asks | Asek3/Oculus#616, Asek3/sodium-1.12#42 | Open feature requests, unfulfilled |

**No shipped, usable Iris backport for 1.12.2 exists.** The failure pattern matches the
analysis (*IDEA_DOC §"Why the Iris backports died"*):

- **Architectural mismatch, not impossibility.** Iris is designed around modern Minecraft's
  core-profile renderer and Sodium's terrain pipeline. Dragging those abstractions back to
  1.12.2's compat-profile / `BufferBuilder` world yields a foreign architecture *and* an
  unfamiliar host simultaneously.
- **Motivation, not tech.** Ports without early useful milestones stall. 1.12.2 being frozen
  is actually an *advantage* — the moving-target problem doesn't apply.
- The viable framing is therefore **a native 1.12.2 engine written against the pack-format
  contract**, not a port.

### 2.3 The competitive landscape and the niche `[V:web]`

| Mod | Relevance | Status 2026-07-24 |
|---|---|---|
| **Vintagium** (`Asek3/sodium-1.12`) | Sodium port to Forge 1.12.2; the chunk-renderer coexistence question (§7.4) | Stale — last push 2024-10; ecosystem fragmented into ≥5 forks |
| **Celeritas** (embeddedt, `git.taumc.org/embeddedt/celeritas`) | Embeddium + **Oculus 1.7** fork; supports "Forge 1.12.2 (lwjgl3ify **or Cleanroom Loader**)" — the only project with an explicit Cleanroom render story | Active; **source-only, no official binaries**; 1.12.2 *shader* support advertised but WIP/not usable per third-party guides — treat as in-progress, unverified |
| **Nothirium** (`Meldexun/Nothirium`) | Independent modern-GL chunk-render rewrite for 1.12.2; recommended on Cleanroom's own wiki | 0.4.9-beta (2026-01); no license file |
| **OptiRefine** (`Ecdcaeb/OptiRefine`) | Mixin-patched OF for Cleanroom (MIT) | "Do not download or install in any actual scenario" — no usable release |
| **Kirino-Engine** (CleanroomMC) | Cleanroom's own next-gen render engine — see §5.2; will *replace* the pipeline Schmaloogium hooks | Highly WIP |

Cleanroom's own modpack-preparation wiki page recommends OptiFine (with caveats), Celeritas,
or Nothirium for render optimization and **names no working shader mod**; a "AdvancedShader"
entry is flagged "Binary patching, incompatible — Remove" `[V:web cleanroommc.com wiki]`.

**The niche is confirmed open.** The only credible path to shaders on Cleanroom today is
Celeritas's unfinished Oculus port. Schmaloogium would be first-to-market with:

- a GPL-3.0-or-later-licensed engine that packs and modpacks can redistribute (OF cannot be) `[D-7]`;
- a compatibility-first Mixin mod for an ecosystem whose coremod-heavy stacks conflict with OF;
- native Cleanroom citizenship, where OF requires an external re-patching project just to load
  `[Q:OQ-17 for OF's exact failure/caveat profile under Cleanroom]`.

The pitch is not "OF but cleaner" — it is **"the missing shader layer for the OF-free
1.12.2/Cleanroom ecosystem"** (*IDEA_DOC §"Is it worthwhile?"*).

### 2.4 Honest costs (recorded so the design doc budgets for them)

From *IDEA_DOC §"The honest costs"* `[U]` (estimates, not measurements):

- Reference engine magnitude: ~16k lines of decompiled engine code (87 shader classes + 23
  expression-engine classes) → a clean reimplementation estimated at **12–20k lines + 3–5k
  Mixin glue**; months of part-time work to "runs BSL-class packs".
- The long tail is behavioral: flip-semantics edge cases, `<buf>Clear` quirks, wetness decay,
  `eyeBrightnessSmooth`, hand-depth matrix games, packs depending on OF bugs. Conformance
  testing (§8) is the only defense.
- Initial performance will be worse than OF-with-shaders (no chunk-pipeline optimizations, by
  design §1.2). Acceptable; optimize inside our own pipeline only (§6.2).

---

## 3. The pack-format contract

**This section is the external interoperability surface.** Existing shader packs are written
against it; Schmaloogium must match it exactly where it claims support. §3.1–§3.5 and §3.7
describe the 1.12.2-era (G6) contract `[V:doc]`; §3.6 describes the modern superset
`[V:web]`. Compact tables are duplicated into Appendices A–D and F so the design doc never
needs the sibling repo open.

### 3.1 Pack layout, programs, and stages

- A pack is a folder or zip under `shaderpacks/`; all shader sources live in its `shaders/`
  folder. Extensions: `.vsh` (vertex), `.gsh` (geometry), `.fsh` (fragment) — and in the
  modern spec `.csh` (compute, §3.6.2). Geometry shaders require GL 3.2 layout qualifiers
  **or** `GL_ARB_geometry_shader4` + `maxVerticesOut`.
- Per-dimension overrides: `shaders/world<id>/` (e.g. `world-1` nether, `world1` end). When a
  world folder exists, shaders load *only* from there; an empty world folder disables shaders
  for that dimension. Only `.vsh`/`.fsh` are read from dimension folders.
- **Program set (1.12.2-era):** the full program-name → renders → fallback table is
  **Appendix A**. Structure: 3 shadow programs, ~21 gbuffers programs, 16 deferred + virtual
  `deferred_pre`, 16 composite + virtual `composite_pre`, 1 final. Fallback ("backup") chains
  let a pack ship only e.g. `gbuffers_terrain` and have the whole terrain family inherit it.
- **1.12.2-era stage order:** `shadow → gbuffers (opaque) → deferred → gbuffers (translucent)
  → composite1..15 → final`. Deferred passes run **between solid terrain and translucent
  terrain**; composites run after the world; `final` writes to the screen.
- **Modern stage superset** (§3.6.1): `setup → begin → shadow → shadowcomp → prepare →
  gbuffers (opaque) → deferred → gbuffers (translucent) → composite → final`.
  Per `[D-4]`, Schmaloogium's stage registry is designed for the superset from day one.

### 3.2 Source-level directives

Parsed from shader sources (`const` declarations, `/* KEY:value */` comments, legacy
`// KEY:value` comments) — the complete directive → effect table is **Appendix A.3**.
Highlights:

- `/* DRAWBUFFERS:0257 */` — per-program draw-buffer routing (digits = buffer indices 0–7,
  `N` = none). Absent → program writes all used buffers. Modern replacement:
  `/* RENDERTARGETS: 3,4,7 */` (§3.6.4).
- Buffer configuration: `const int colortexNFormat = <fmt>` (37-format table, Appendix B.4),
  `const bool colortexNClear = false`, `const vec4 colortexNClearColor = …`,
  `const bool colortexNMipmapEnabled = true`, legacy `/* GAUX4FORMAT:RGBA32F */`.
- Shadow configuration: `shadowMapResolution`, `shadowMapFov`, `shadowDistance`,
  `shadowDistanceRenderMul`, `shadowIntervalSize`, per-texture mipmap/nearest/hardware-PCF
  bools (full list Appendix A.3).
- Engine constants: `wetnessHalflife`, `drynessHalflife`, `eyeBrightnessHalflife`,
  `centerDepthHalflife`, `sunPathRotation`, `ambientOcclusionLevel`, `superSamplingLevel`,
  `noiseTextureResolution`.
- **Implicit resource declaration:** merely *declaring* a uniform enables its backing
  resource — `uniform sampler2D shadowtex1;` allocates a second shadow depth buffer;
  `uniform sampler2D colortex7;` raises the color-buffer count; declaring `gdepth` upgrades
  buffer 1's format to RGBA32F; declaring `centerDepthSmooth` enables the center-depth
  readback. The engine must scan sources to size its framebuffers. `[V:doc]`
- Vertex-stage: declaring attributes `mc_Entity` / `mc_midTexCoord` / `at_tangent` opts the
  program into the extended vertex data; `const int countInstances = N` re-renders geometry N
  times with `instanceId` incrementing.
- `#include "relative"` or `#include "/absolute-from-shaders-root"`, nesting ≤ 10 deep.

### 3.3 `shaders.properties` surface

The pack's `shaders/shaders.properties` (itself macro-preprocessed!) carries pack-level
configuration. Complete key catalog with semantics: **Appendix F**. Categories:

1. **Tri-state engine flags** (unset/true/false; `clouds` is fast/fancy/off): `clouds`,
   `oldHandLight`, `dynamicHandLight`, `oldLighting`, `shadowTranslucent`,
   `underwaterOverlay`, `sun`, `moon`, `vignette`, `backFace.{solid,cutout,cutoutMipped,
   translucent}`, `rain.depth`, `beacon.beam.depth`, `separateAo`, `frustum.culling`.
2. **Version gate**: `version.<mcver>=<edition>` minimum-engine check.
3. **Custom textures**: `texture.<stage>.<samplerName>[.0-9]=<path|asset|raw spec>` +
   `texture.noise=<path>` (§4.6).
4. **Options UI**: `sliders=`, `screen[.NAME]=` (with `[subscreen]`, `<profile>`, `<empty>`,
   `*`), `screen[.NAME].columns=`.
5. **Profiles**: `profile.NAME=` token lists (`OPTION`, `!OPTION`, `OPTION:value`,
   `profile.OTHER` copy, `!program.<name>` disable).
6. **Custom uniforms/variables**: `uniform.<type>.<name>=<expr>`,
   `variable.<type>.<name>=<expr>` — a full expression language (Appendix F.6).
7. **Per-program render state**: `alphaTest.<prog>=off|FUNC ref`,
   `blend.<prog>=off|src dst srcA dstA`, `scale.<prog>=s [ox oy]`,
   `flip.<prog>.<buf>=bool`, `program.<prog>.enabled=<boolean expr over options>`.

Option discovery itself happens **in shader sources**, not properties: `#define`/commented
`#define` switches (recognized only when an `#ifdef`/`#ifndef` uses them), `#define NAME
value // comment [allowed values]` variables, plus a whitelist of `const` options
(Appendix F.3). Tooltips/labels come from `shaders/lang/*.lang`.

### 3.4 Uniform, sampler, and attribute contract

1. **Vertex attributes** (1.12.2-era): `mc_Entity` (xyz = blockID, renderType, metadata),
   `mc_midTexCoord` (sprite-center UV), `at_tangent` (xyz tangent, w handedness). Bound at
   locations 10/11/12 respectively `[V:observed]`. Modern additions: §3.6.6.
2. **Built-in uniforms**: ~50 scalar/vector/matrix uniforms (held items, fog/sky, time,
   celestial positions, camera, all 10 matrices incl. inverses and previous-frame, player
   state, `entityColor`/`entityId`/`blockEntityId`, `blendFunc`, `instanceId`) — complete
   inventory with types and semantics in **Appendix D**.
3. **Sampler-unit contract**: sampler uniforms are bound to a **fixed texture-unit map**
   (units 0–15) that differs between gbuffers/shadow stages and deferred/composite stages —
   the exact map is **Appendix B.3**. Packs rely on these numbers.
4. **Custom uniforms**: pack-defined expressions over the built-in uniform values, biome
   parameters (`biome`, `temperature`, `rainfall`, `BIOME_*` constants), and view-entity
   booleans (`is_burning`, `is_hurt`, `is_in_water`, …), with ~40 functions incl. `if()`,
   `smooth(id, val, fadeIn, fadeOut)`, vector constructors. Updated on program change.
   Dynamic per-draw uniforms (`entityColor`, `entityId`, `blockEntityId`, `fogMode`,
   `fogColor`) are excluded as expression inputs `[V:doc]`.

### 3.5 GLSL environment contract

- Packs of the 1.12.2 era are written as **GLSL 120-era compatibility-profile** code:
  `gl_Vertex`, `gl_Color`, `gl_MultiTexCoord0/1`, `gl_Normal`, `gl_ModelViewMatrix`,
  `ftransform()`, etc. The engine feeds them through the fixed-function matrix stack and
  client vertex arrays. **This chains the engine to a compatibility-profile GL context**
  `[D-9]` — cross-confirmed by Cleanroom's own RenderBook material stating 1.12.2 runs on
  compat profile `[V:mcp]`.
- **Standard macro header** injected after `#version` in every shader: `MC_VERSION`
  (1.9.4 → 10904 format), `MC_GL_VERSION`, `MC_GLSL_VERSION`, OS (`MC_OS_*`), vendor
  (`MC_GL_VENDOR_*`), renderer (`MC_GL_RENDERER_*`), on-demand `MC_<GL_extension>` macros,
  and option macros (`MC_NORMAL_MAP`, `MC_SPECULAR_MAP`, `MC_RENDER_QUALITY`,
  `MC_SHADOW_QUALITY`, `MC_HAND_DEPTH`, `MC_OLD_HAND_LIGHT`, `MC_OLD_LIGHTING`,
  `MC_FXAA_LEVEL`). Packs branch on these — the identity set Schmaloogium defines is a
  deliberate compatibility decision (§7.5, `[Q:OQ-7]`).
- Preprocessor support: `#define/#undef/#ifdef/#ifndef/#if/#elif/#else/#endif` with
  `defined X` / `defined(X)`; macro substitution; conditional compilation also applies to
  `shaders.properties` and the ID-mapping properties files.
- **The era bridge (unique Schmaloogium advantage):** a compat-profile context compiles both
  GLSL 120-era packs *and* `#version 150+` sources (drivers expose all GLSL versions in
  compat). Neither OF-1.12.2's frozen engine nor core-profile Iris serves both pack
  generations at once; Schmaloogium potentially can `[U→OQ-18]`
  (*IDEA_DOC §"2. Things you missed"*).

### 3.6 The modern superset `[V:web — upstream OptiFine doc + Iris docs, 2026-07-24]`

*What current packs use beyond the G6 contract. Primary sources: upstream
`sp614x/optifine` `OptiFineDoc/doc/shaders.txt` (master) and the Iris documentation at
shaders.properties (per-page URLs in §12.5). None of this exists in any 1.12.2 OptiFine
build; OptiFine gained shadowcomp/prepare/compute in the ~1.16.5–1.17.1 era (approximate —
no upstream changelog pins it precisely).*

#### 3.6.1 New pass arrays and frame order

Modern frame order: `setup → begin → shadow → shadowcomp → prepare → gbuffers (opaque) →
deferred → gbuffers (translucent) → composite → final`.

| Pass | Naming | When | Nature | Origin |
|---|---|---|---|---|
| `setup` | `setup`, `setup1`…`setup99` | At pack load + on screen-size change (not per frame) | **Compute-only** | Iris-only |
| `begin` | `begin`, `begin1`…`begin99` | Per frame, before shadow pass | Composite-style fullscreen; writes colortex; "set up data needed for the shadow pass" | Iris-only |
| `shadowcomp` | `shadowcomp`, `shadowcomp1`…`shadowcomp99` | Directly after the shadow pass | Composite-style; can **write shadowcolor buffers**; reads shadowtex/shadowcolor | modern OF + Iris |
| `prepare` | `prepare`, `prepare1`…`prepare99` | After shadow, before gbuffers | Composite-style; writes colortex; "set up data needed for gbuffers" | modern OF + Iris |

Numbering runs to 99 in the modern spec (vs 15 in G6's deferred/composite arrays — the
registry must not hardcode 16 `[D-4]`). Virtual `deferred_pre`/`composite_pre` flip-control
programs persist unchanged.

New/changed gbuffers programs (Iris): `gbuffers_particles` / `gbuffers_particles_translucent`,
`gbuffers_entities_translucent`, `gbuffers_block_translucent`, `gbuffers_lightning`, and
re-activated `gbuffers_terrain_solid` / `gbuffers_terrain_cutout` (unused in G6). Iris's rule:
"opaque geometry renders before deferred, translucent after deferred."

#### 3.6.2 Compute shaders

- `.csh` files attach to **every program except gbuffers**: `<program>.csh` plus suffixes
  `<program>_a.csh` … `<program>_z.csh` (up to 27 dispatches per pass), executing **before**
  the associated program. Require `#version 430` + local-size declaration.
- Work-group sizing: `const ivec3 workGroups = ivec3(x,y,z);` (fixed) or
  `const vec2 workGroupsRender = vec2(sx,sy);` (screen-relative; default 1.0,1.0 = per pixel).
- Image write access (modern OF): `colorimg0-5` (= colortex0-5) and `shadowcolorimg0-1`.
- Iris extensions: `indirect.<pass> = <bufferObjectIndex> <byteOffset>` (GPU-driven dispatch
  from an SSBO-resident `uvec3`), `allowConcurrentCompute=true` (relaxed memory barriers).

#### 3.6.3 Buffer-system evolution

- **`/* RENDERTARGETS: 3,4,7 */`** supersedes `DRAWBUFFERS`: comma-separated, addresses
  buffers 0–15; fragment outputs become `out vec4 outColor0..N` mapped in listed order. Max 8
  simultaneous attachments still applies.
- **colortex count**: 16 (`colortex0-15`) in the modern baseline; **Iris 1.10.5+ allows 32**
  (probe via `MAX_COLOR_BUFFERS` macro). Legacy aliases unchanged (colortex0-3 =
  gcolor/gdepth/gnormal/composite; 4-7 = gaux1-4); gdepth-declared-as-RGBA still upgrades to
  RGBA32F.
- **`size.buffer <name> <w> <h>`** (Iris): fixed/relative per-buffer resolution; a resized
  buffer becomes unwritable from gbuffers programs.
- **Custom images** (Iris): `image.<name> = <sampler> <format> <internalFormat> <pixelType>
  <clearEachFrame> <isRelative> <x> <y> [<z>]` — up to 16 true 2D/3D images with
  imageLoad/imageStore; GL 4.2+; `CUSTOM_IMAGES` feature flag; unavailable on macOS.
- **SSBOs** (Iris): `bufferObject.<index> = <byteSize>` (0–8; screen-sized and
  file-initialized variants exist); `layout(std430, binding=N) buffer …`; GL 4.3+; `SSBO`
  feature flag; contents persist across frames.
- **shadowcolor2-7** exist behind the `HIGHER_SHADOWCOLOR` feature flag (vs 2 in G6).

#### 3.6.4 Pipeline-ordering directives (Iris)

`particles.ordering=mixed|after|before`; `separateEntityDraws=true` (defer translucent
entities/block-entities until after the deferred pass, into the `*_translucent` programs);
`shadow.enabled`; `skipAllRendering`; `voxelizeLightBlocks`; `weather`; `endFlashShadows`;
`supportsColorCorrection`; enhanced `customTexture.<name>` binding.

#### 3.6.5 Newer vertex attributes (feeds Appendix C.3)

| Attribute | Semantics | Origin |
|---|---|---|
| `at_midBlock` (`vec3` OF / `vec4` Iris) | xyz = offset from vertex to block center in 1/64-block units; blocks only. Iris `.w` = block light-emission 0–15 (`BLOCK_EMISSION_ATTRIBUTE` flag) | modern OF + Iris |
| `at_velocity` (`vec3`) | View-space vertex offset to previous frame (motion vectors); entities/block-entities only | modern OF (Iris support historically partial) |
| `mc_chunkFade` (`float`) | Chunk fade-in progress 0–1 (`FADE_VARIABLE` flag); constant −1.0 outside terrain | Iris-only (MC 1.21-era) |
| `vaPosition`, `vaColor`, `vaUV0`, `ivec2 vaUV1`, `ivec2 vaUV2`, `vaNormal` + `modelViewMatrix`/`projectionMatrix`/`normalMatrix`/`modelOffset` uniforms | Core-profile (MC 1.17+) replacements for the `gl_*` fixed-function inputs; `vaPosition` for terrain is **chunk-origin-relative** | modern OF |

#### 3.6.6 Newer uniforms (delta over Appendix D)

- `renderStage` (int) + `MC_RENDER_STAGE_*` constants — fine-grained current-geometry stage
  (SKY, SUN, MOON, STARS, VOID, TERRAIN_SOLID/CUTOUT[_MIPPED], ENTITIES, BLOCK_ENTITIES,
  DESTROY, OUTLINE, HAND_SOLID/TRANSLUCENT, TERRAIN_TRANSLUCENT, TRIPWIRE, PARTICLES, CLOUDS,
  RAIN_SNOW, WORLD_BORDER, DEBUG…). Presupposes stage instrumentation of the render loop.
- `alphaTestRef` (packs implement discard manually: `if (color.a < alphaTestRef) discard;`).
- Biome category: `biome`, `temperature`, `rainfall`, `hasSkylight`, `heightLimit`,
  `bedrockLevel` — as *uniforms* (G6 exposed biome data only to custom-uniform expressions).
- Iris-only families: precision-split camera (`cameraPositionFract`/`cameraPositionInt`),
  `eyePosition`, `relativeEyePosition`, `playerBodyVector`, `playerLookVector`; player status
  (`currentPlayerHealth`/`Hunger`/`Air`, `playerMood`, `constantMood`); wall-clock
  (`currentDate`, `currentTime`); id extensions (`vehicleId`, `currentRenderedItemId`,
  `currentSelectedBlockId`/`Pos`); `thunderStrength`; `textureFilteringMode`.
- MC 1.19-era: `darknessFactor`, `darknessLightFactor`.

#### 3.6.7 Engine identity and feature flags (feeds §7.5)

- **`IS_IRIS`** is the canonical engine discriminator — defined only by Iris. **OptiFine
  defines no positive self-identifier**; packs detect OF as "not IS_IRIS".
- `IRIS_VERSION` = major×10000 + minor×100 + patch. Other Iris macros: `IRIS_TAG_SUPPORT`,
  `IRIS_HAS_CONNECTED_TEXTURES`, `IRIS_HAS_TRANSLUCENCY_SORTING`, `MC_MIPMAP_LEVEL`,
  `MAX_COLOR_BUFFERS`.
- **Feature flags**: `iris.features.required=<FLAGS>` (hard load error if unsupported) /
  `iris.features.optional=<FLAGS>` (defines `IRIS_FEATURE_<FLAG>` when supported). Known
  flags: `COMPUTE_SHADERS`, `CUSTOM_IMAGES`, `SSBO`, `ENTITY_TRANSLUCENT`,
  `PER_BUFFER_BLENDING`, `REVERSED_CULLING`, `SEPARATE_HARDWARE_SAMPLERS`,
  `HIGHER_SHADOWCOLOR`, `BLOCK_EMISSION_ATTRIBUTE`, `FADE_VARIABLE`, `TEXTURE_FILTERING`,
  `TESSELATION_SHADERS`/`TESSELLATION_SHADERS`, `CAN_DISABLE_WEATHER`.
- Also modern OF: `MC_ANISOTROPIC_FILTERING`, `MC_TEXTURE_FORMAT_LAB_PBR(_1_3)` (advertises
  the labPBR texture convention to packs).
- **The partial-impersonation trap:** defining `IS_IRIS` claims Iris semantics *wholesale* —
  packs will then assume every gated feature works. The feature-flag system is the sanctioned
  way to advertise capabilities granularly. This materially shapes the §7.5 decision.

#### 3.6.8 Modern features a 1.12.2 engine can't (or can barely) provide — risk list

| Feature | Assessment for 1.12.2 |
|---|---|
| Core-profile `va*` attribute semantics (chunk-origin-relative positions, `modelOffset`) | Must be *synthesized* if targeted; packs lacking `MC_VERSION < 11700` fallback paths won't compile against legacy inputs |
| `renderStage` granularity (TRIPWIRE, WORLD_BORDER, OUTLINE…) | Requires instrumenting the monolithic 1.12.2 render loop with stage transitions; partial emission risks silent pack misbehavior |
| `separateEntityDraws` / `particles.ordering` / translucent-split gbuffers programs | Deep reordering of entity/particle draws around the deferred pass; "hybrid deferred" packs (modern Photon-class) hard-depend on it |
| `at_velocity` motion vectors | Needs previous-frame transform capture per entity/BE — expensive and invasive |
| Distant Horizons integration (`dh_*` programs, `dhMaterialId`, DH depth textures) | DH doesn't exist for 1.12.2; ensure graceful absence (unused programs, inert/macro-gated `dh*` uniforms) |
| Tag-based ID files (`IRIS_TAG_SUPPORT`) | 1.13+ datapack tags don't exist on 1.12.2; would need an ore-dictionary-style shim + Iris's entries-beat-tags priority rule |
| `mc_chunkFade` | Only the documented constant −1.0 fallback is possible |
| Compute / custom images / SSBO / tessellation / indirect dispatch | Pure OpenGL — *feasible on 1.12.2 in principle* (GL 4.2/4.3 floor), but excludes macOS/old GPUs; packs with `iris.features.required=SSBO …` cannot load on a G6-level engine at all |
| 32 colortex / shadowcolor2-7 | MRT + FBO plumbing beyond G6's 8+2 — registry/FBO code must not hardcode G6 limits |
| `supportsColorCorrection`, `textureFilteringMode` | Hook modern MC color-space handling; flag as unimplementable-as-specified |

### 3.7 ID mapping files

`shaders/block.properties`, `item.properties`, `entity.properties` map pack-facing stable IDs
to blocks/items/entities (`block.31=red_flower yellow_flower reeds`, long form
`minecraft:red_flower`, property-matched forms, legacy id:meta). Consumed by `mc_Entity`,
`heldItemId/2`, `entityId`, `blockEntityId`. **Mods may extend the mapping** by shipping
`assets/<modid>/shaders/<same>.properties` — a Forge-mod-facing extension point Schmaloogium
must preserve. `block.properties` may also define custom render layers
(`layer.solid/cutout/cutout_mipped/translucent=<blocks>`; solid-opaque cubes excluded).
All these files are macro-preprocessed (standard macros A–G, no option macros). `[V:doc]`

---

## 4. Reference implementation anatomy (behavioral)

*What the OF engine actually does, recorded as observed behavior — the design doc's map of
"the machine that already satisfies the contract". Everything here is `[V:observed]` (see
§10.2 for the legal framing). Deep-dive narrative: SHADER_ENGINE_IMPL.md in the sibling repo
(§12); this section is the condensed, design-relevant restatement.*

### 4.1 Lifecycle

1. **Startup** (display init): probe GL capabilities (GL version, `GL_MAX_DRAW_BUFFERS`,
   `GL_MAX_COLOR_ATTACHMENTS`, `GL_MAX_TEXTURE_IMAGE_UNITS`; mipmap gen requires GL 3.0),
   load global config (`optionsshaders.txt`).
2. **Pack discovery**: `shaderpacks/` dir; "OFF" and "(internal)" sentinels; folder packs and
   zip packs (nested-root tolerant, path-sanitized).
3. **Pack load** (selection/resource reload): close old pack → scan dimension folders
   (world −128..128) → parse options from sources → parse `shaders.properties`
   (macro-preprocessed) into flags/profiles/screens/custom textures/custom uniforms/
   per-program state → refresh block/item/entity aliases → if vertex-format-relevant state
   changed, rebuild vertex formats and reload world renderers.
4. **Init (lazy, first frame)**: reset counters (4 color buffers, 1 depth buffer, no shadow
   buffers, RGBA formats), evaluate `program.<name>.enabled` + active profile, compile/link
   every program (§4.2), resolve backup chains, create FBOs, create noise texture.
5. **Uninit**: delete all GL objects; triggered by pack change, option change, dimension
   switch (when per-dimension packs exist), and resolution-multiplier changes.

### 4.2 Program registry mechanics

- **60 classic program slots** (Appendix A): each with stage, backup parent, and per-program state
  (draw-buffer routing, composite-mipmap bitmask, instance count, alpha/blend overrides,
  render scale, flip config).
- **Compile flow per file**: read from pack → resolve `#include`s (≤10 deep, with `#line`
  bookkeeping) → inject standard-macro header after `#version` → rewrite changed option
  `#define`s → evaluate preprocessor conditionals → scan surviving lines for declarations
  (attributes, uniforms → resource sizing, `const` directives, `DRAWBUFFERS`).
- Attributes bound pre-link at fixed locations (10/11/12). Programs failing validation are
  deleted with a user-visible error; empty programs resolve through the **backup chain**
  (copy full config from nearest non-empty ancestor; shadow excluded from chains).
- A "use program" call is the universal state barrier: it re-points every sampler uniform to
  the fixed unit map, refreshes ~90 built-in uniforms (per-program location caching +
  redundant-upload skipping), evaluates custom uniforms, and locks per-program alpha/blend
  state. During the shadow pass it force-selects the shadow program regardless of what the
  hook requested — hook sites stay dumb.

### 4.3 Framebuffer architecture

- **Main FBO** ("dfb"): up to 8 logical color buffers, each backed by **two textures (A/B)
  for ping-pong**; gbuffers read/write "main", deferred/composite read "main"/write "alt"
  then **flip** the buffers they wrote (per-program flip overrides possible, incl. the
  virtual `*_pre` programs). Three depth textures: `depthtex0` (real depth attachment),
  `depthtex1` (copied before translucent terrain), `depthtex2` (copied before weather) — the
  copies give composites "no water" / "no weather/hand" depth views.
- Buffer sizing = display size × render-quality multiplier. Formats per pack directives
  (Appendix B.4); integer formats use the integer pixel-transfer path.
  Incomplete-format fallback: recreate everything as plain RGBA.
- Buffer 0 clears to fog color; buffer 1 clears to white (and upgrades to RGBA32F when
  declared as `gdepth`); buffers 2–7 clear to transparent black; per-buffer clear /
  clear-color overrides apply. Clearing honors flip state (clears both sides when flipped).
- **Shadow FBO** ("sfb"): created only when shadow depth buffers are used; ≤2 depth textures
  (+ optional hardware-PCF compare mode), ≤2 color textures, at pack-set shadow resolution ×
  shadow-quality multiplier; per-texture nearest/mipmap filters.
- **Final** renders to the vanilla framebuffer (anaglyph-aware color masking).

### 4.4 Per-frame flow

Condensed one-frame data flow (hook-need catalog: §7.1 + Appendix E):

```
frame start
 ├─ sample world state → uniforms (time, rain→wetness decay, eye brightness smooth,
 │   isEyeInWater, night vision/blindness, sky color …)
 ├─ snapshot previous-frame camera + matrices (TAA-style previous* uniforms)
 ├─ bind gbuffers texture set (fixed unit map, Appendix B.3)
 ├─ SHADOW PASS (every frame when the pack uses shadows; §4.5)
 ├─ bind main FBO, per-buffer clear, capture camera matrices (+inverses)
 ├─ GBUFFERS: sky → terrain solid/cutout-mipped/cutout → damaged-block → entities
 │   (per-entity id) → glowing outline → block entities (per-TE id) → beacon beam
 │   → armor glint → spider eyes → particles → clouds → weather (copy depth→depthtex2 first)
 ├─ copy depth→depthtex1, then DEFERRED passes (fullscreen ping-pong)
 ├─ translucent terrain (water program) → hand solid → hand translucent (depth-scaled)
 └─ COMPOSITE passes (fullscreen ping-pong, per-pass mipmap gen + render-scale viewports)
     → FINAL to screen
```

Behavioral details the design doc must not lose:

- **Program-per-phase dispatch table** (Appendix A.1) with push/pop program semantics around
  leash/glint rendering.
- **Entity-data stack**: during chunk building, block state pushes
  `(renderType<<16 | aliasedBlockId, metadata)`; every emitted vertex is stamped with the
  current value into the `mc_Entity` attribute slots (§4.6). Per-entity/per-TE hooks set
  `entityId`/`blockEntityId` uniforms through the alias tables.
- **Depth copies at precise moments** (before translucent; before weather) and a synchronous
  center-depth readback when packs use `centerDepthSmooth` (a per-frame pipeline stall —
  modernization target, §6.2).
- **Hand rendering** is split solid/translucent with a depth-scale matrix trick, and the
  first-person item overlay routes through draw-buffers=none.
- Composite passes draw one fullscreen quad (triangle-strip fallback where quads are
  unavailable) under an identity ortho, fog/depth/blend disabled, optional per-buffer mipmap
  regeneration, optional sub-viewport (`scale.<prog>`), `countInstances` instancing loop.
- Frame end guarantees composites run even on early exits.

### 4.5 Shadow pass

Runs *inside* frame begin, before the world render. Forces third-person camera; ortho
projection (±`shadowDistance` half-plane, near 0.05 / far 256) or perspective when
`shadowMapFov` set; modelview = celestial rotation (sun by day, moon by night, plus
`sunPathRotation`); **texel snapping** by `shadowIntervalSize` so shadow texels don't crawl.
Culling = shadow-frustum planes derived from the shadow MVP **plus synthesized side planes**
along the light direction; chunk traversal iterates a sun-aligned box between camera and
light when the shadow render distance is tighter than view distance. Renders terrain
solid→cutout-mipped→cutout, entities (Forge render-pass interop — free on a Forge-lineage
loader), copies depth→shadowtex1 (water-shadow split), optionally translucent terrain
(`shadowTranslucent`), then per-config mipmap generation on shadow textures. Vanilla entity
blob-shadows are suppressed while a shadow pass exists; clouds render in the shadow pass only
per config.

### 4.6 Vertex pipeline and texture system

- **Extended vertex format**: vanilla 28-byte block vertex → **56 bytes / 14 ints**; added
  fields `mc_midTexCoord` (2×float @ byte 32), `at_tangent` (4×short @ 40), `mc_Entity`
  (shorts @ 48). Exact layout: **Appendix C**. Formats are swapped in/out on pack toggle —
  which also invalidates Forge's baked-quad/lighting caches `[Q:OQ-14]`.
- A **vertex-builder side channel** attached to every buffer builder stamps entity data into
  each vertex and, per quad, computes: face normal (diagonal cross product), UV-space tangent
  frame with handedness, and mid-texture UV. VBO and non-VBO attribute-pointer paths both
  exist (stride 56, attribs 10–12 enabled around shader-active draws).
- **Texture system**: every texture can gain normal/specular companions (`_n`/`_s`); the
  blocks atlas allocates full companion atlases with matching mip chains (missing sprites →
  flat-normal `0xFF7F7FFF` / zero-specular defaults). Units 2/3 carry them during world
  rendering. A `noiseTextureResolution²` RGB noise texture (xorshift-generated, overridable
  by `texture.noise`) sits on unit 15. Pack custom textures bind per stage
  (`texture.<stage>.<name>`, asset-location / raw-binary / pack-PNG forms, `.mcmeta`
  blur/clamp).

### 4.7 Options, aliases, GUI, failure handling

- **Options**: discovered in sources (switch/variable/const forms), applied by *rewriting
  source lines at compile time*; only changed options persist to per-pack
  `shaderpacks/<pack>.txt`; global engine settings in `optionsshaders.txt`.
- **Aliases**: block/item/entity properties files (+ per-mod merge from mod jars) feed
  `mc_Entity`, held-item, and entity/block-entity id uniforms (§3.7).
- **GUI**: pack list + 8 engine options (AA, normal/specular map toggles, render/shadow
  resolution multipliers, hand depth, old hand light, old lighting); pack options screen
  generated from `screen.*` config with sliders/subscreens/profiles; tooltips from lang
  files; F3+R / `/reloadShaders` reload. (Schmaloogium equivalent: §7.6.)
- **Failure handling**: capability gates produce chat errors; invalid programs delete
  themselves and fall back through backup chains; per-uniform GL errors disable just that
  uniform; custom-uniform runtime errors disable just that uniform; "function not supported"
  during init turns the pack off gracefully. Graceful degradation everywhere is itself
  contract-adjacent behavior packs implicitly rely on.
- **Interlocks with OF's own features** (FXAA/AF/fast-render refusal): cease to exist in
  Schmaloogium (§1.2).

### 4.8 Keep / Adapt / Skip

The payoff table — every reference subsystem mapped to Schmaloogium's stance:

| Subsystem (behavioral) | Stance | Notes |
|---|---|---|
| Stage semantics, program set, backup chains | **Keep** | Contract-visible (§3.1, App A) |
| Ping-pong buffer + flip semantics, buffer clear rules | **Keep** | Packs depend on exact flip behavior |
| Fixed texture-unit map | **Keep** | Contract-visible (App B.3) |
| Built-in uniform inventory + update cadences | **Keep** | Contract (App D); implementation may batch/cache differently |
| Source scanning → implicit resource sizing | **Keep** | Contract (§3.2) |
| Preprocessor + standard macro header | **Keep** (identity set: §7.5) | Contract |
| Options/profiles/screens/lang, per-pack persistence | **Keep** | Contract + UX parity |
| Custom-uniform expression language | **Keep** | Contract (App F.6); modern evaluator (§6.3) |
| ID aliasing + per-mod merge | **Keep** | Contract + mod-ecosystem extension point |
| Shadow-pass semantics (celestial camera, texel snap, depth split, translucency toggle) | **Keep** | Contract-visible via shadow uniforms/buffers |
| 56-byte vertex format @ attribs 10–12 | **Keep, but extensible** | Modern attributes must be addable (§7.4, App C) |
| Per-quad tangent/midUV computation | **Keep** | Feeds contract attributes |
| `_n`/`_s` companion atlases | **Keep** (v0.5) | Contract (`MC_NORMAL_MAP` era) |
| Stage set frozen at 1.12.2-era five, arrays of 16 | **Adapt** | Registry designed for the modern superset, arrays to 99 `[D-4]` |
| Monolithic static engine hub | **Adapt** | Engine-core/loader-glue seam, testable modules `[D-6]` |
| ARB-era GL entry points (`*ARB`) | **Adapt** | Core equivalents within compat profile (§6.2) |
| Per-frame synchronous center-depth read | **Adapt** | PBO + fence async (§6.2) |
| Per-frame filter re-parameterization of flip textures | **Adapt** | GL 3.3 sampler objects (§6.2) |
| Synchronous on-thread compile of ~40 programs | **Adapt** | Shared-context async compile (§6.2) |
| Allocation-discipline machinery (array caches, mutable poses) | **Skip** | Modern GC removes the constraint (§6.3) |
| Whole-class replacement + AccessFixer + patch jar + installer | **Skip** | Mixin injections instead `[D-5]` |
| Failure-tolerant reflection layer ("Reflector") | **Skip** | Cleanroom-native mod: direct references + access transformers `[V:mcp]` |
| Perf core, MCPatcher features, capes, telemetry, FXAA | **Skip** | Non-goals (§1.2) |
| Hand-rolled GUI on 2012-era screens | **Skip** | ModularUI candidate (§7.6) |

---

## 5. Cleanroom platform facts

### 5.1 Verified

**Template repo ground truth `[V:template]`** (this repo is a pristine CleanroomModTemplate
as of 2026-07-24):

| Fact | Value |
|---|---|
| Build system | Unimined fork `xyz.wagyourtail.unimined` **1.4.26-kappa** (NOT ForgeGradle/RetroFuturaGradle); Gradle 9.6.1 |
| Java | **Java 25 toolchain** (source/target 25) |
| Mappings | **MCP `stable`, `39-1.12`**; ATs written in MCP names, remapped to SRG at build |
| Cleanroom loader pin | **0.5.17-alpha** (template pin; current is 0.6.6-alpha — §5.2, `[Q:OQ-2]`) |
| Mixin | `com.cleanroommc:sponge-mixin:0.20.13+mixin.0.8.7` (compileOnly — the loader provides it at runtime) |
| LWJGL compat | `com.cleanroommc:lwjglx:1.0.0` (LWJGL2-API compat layer, template-enabled; **runtime-only — never compile against `org.lwjglx`** `[V:mcp]` wiki porting page) |
| Templating | Blossom 2.2.0 (`Reference.java`, `mcmod.info`, `pack.mcmeta` tokens from `gradle.properties`) |
| Entry point | Standard `@Mod` class + `@SidedProxy`, `ModType: CRL` manifest |
| Extras | ATs enabled (`modid_at.cfg`), jar-in-jar `contain` configuration, shadow (disabled), JUnit 6 testing enabled, 3 GitHub CI workflows (build / tag-release / CurseForge+Modrinth publish), MIT-style LICENSE (to be replaced with GPL-3.0-or-later per `[D-7]`) |

**Mixin stack `[V:mcp + V:web]`:** Cleanroom ships **CleanMix** built in — a fork of Fabric
Mixin (itself the cutting-edge fork of SpongePowered Mixin) targeting 1.12.2, living at the
standard `org.spongepowered.asm` package, with **MixinExtras bundled** and a re-implementation
of the `zone.rong.mixinbooter` API for ecosystem compat. Configs are declared via the
**`MixinConfigs` jar-manifest attribute**; the legacy MixinBooter loader interfaces are
deprecated. Mixins are written against **SRG names** and applied through Cleanroom's remapper
chain in dev and production. Dev ergonomics: `crl.dev.mixin` extra-config property,
`-Dmixin.debug.export=true` in template runs; crash reports annotate which mixins touched
each class. Coremods (`IFMLLoadingPlugin`) still exist but are discouraged.

**Platform components `[V:web CleanroomMC org, 2026-07-24]`:**

- **Loader releases:** current **0.6.6-alpha (2026-07-24)** — *daily* release cadence this
  week (0.6.3→0.6.6 in three days). README states **"1.12.2 on Java 25+"** and **"Latest
  LWJGL3"**. 0.6.6 notes mention Foundation classloader changes + MixinBooter bump — churn is
  real; pin and re-verify `[Q:OQ-2]`.
- **Foundation** = Cleanroom's LaunchWrapper replacement ("with better debug logging");
  canonical repo kappa-maintainer/Foundation (MIT), mirrored under CleanroomMC.
- **Fugue** = compat mod fixing abandoned 1.12.2 mods under Cleanroom (GPL-3.0, active);
  **Scalar** = Scala provider. Both standard install companions; both refuse to load on
  plain Forge.
- **lwjglx strategy is in flux:** the README no longer mentions LWJGL2 compat; org repos show
  **LWJGLXX** ("using lwjglx without redirecting everything", early) and **LWJGLY** ("LWJGL
  2⇒3 Shim & Router", currently an *empty* placeholder repo). Assume LWJGL3-native code
  paths for our own code; the shim exists for legacy mods and is being reworked `[Q:OQ-21]`.
- **Mc122RenderBook** (`tttsaurus/Mc122RenderBook`, MIT — linked from the official wiki's
  Render Documentation page `[V:mcp]`): rendering-education repo covering GL state, fixed
  function vs programmable pipeline (VAOs, shader programs, buffer objects), execution
  model/synchronization/hazards — and it **ships a "JUnit OpenGL Extension"** for setting up
  a GL test environment, plus Nsight Graphics and Gradle-profiling integration. Directly
  relevant to §8.3 `[Q:OQ-10]` for CI viability.
- **ModularUI**: Cleanroom-canonical GUI framework (LGPL-3.0) with featured examples in the
  MCP examples DB `[V:mcp]` — candidate for the shader-options GUI (§7.6).
- Cleanroom platform license: **LGPL-2.1**.

### 5.2 The "modular render pipeline" — CONFIRMED, and it changes the calculus `[V:web]`

The unverified idea-doc rumor is real, in two artifacts:

1. **Discussion #405 "A Modular Render System for Cleanroom"** (proposed 2025-07-24 by
   tttsaurus, CleanroomMC maintainer; active engagement from Rongmario through Oct 2025):
   a structured rendering abstraction layer — GL-object abstractions, materials/meshes/render
   passes, ECS, centralized GL state management, multi-pass pipeline, and even ambitions to
   deprecate raw GL access with annotation-processor enforcement.
2. **Kirino-Engine** (CleanroomMC org repo, 393 commits, updated 2026-07-24): "the next-gen
   rendering engine for Minecraft; decoupled, extensible" — deferred render-command system,
   abstracted GL calls, ECS, meshlet-based terrain rewrite, immutable
   RenderPass/Subpass-composed pipeline. **Highly WIP; will ship with Cleanroom via rolling
   updates rather than standalone.** License: custom ("Custom Mod Permissions License").
   Its README states it will **not be compatible with existing render mods** — it replaces
   the whole pipeline.

**Strategic read for Schmaloogium `[A]`:** near-term (v0.1–v0.5 horizon), the vanilla-1.12.2
render loop remains the reality and the Mixin hook plan (§7.1) stands. Long-term,
Kirino-Engine is simultaneously the biggest threat (it would invalidate every render-loop
hook *and* the vanilla vertex pipeline we extend) and the biggest opportunity (a sanctioned
pass/pipeline API is exactly the natural home for a shader engine). Consequences:

- Track Kirino development actively; engage upstream early (§7.7).
- The engine-core/loader-glue seam `[D-6]` graduates from "nice hygiene" to **strategically
  load-bearing**: the core must survive a backend swap from "mixins into vanilla loop" to
  "Kirino passes". Record as a hard design-doc requirement.
- New risk row `[Q:OQ-20]`: Kirino compatibility/timeline; whether Cleanroom will run it by
  default; whether a compat-profile FF pipeline remains available beneath it.

### 5.3 Remaining unverified / open platform items

| Item | Status |
|---|---|
| GL context creation mechanics (compat-profile request, GLFW hints, HiDPI framebuffer-size handling, what lwjglx intercepts at runtime) | Unverified; determines how we request debug/shared contexts `[Q:OQ-3]` |
| CleanMix divergences from upstream Mixin relevant to hot render-path injections | CleanMix repo confirmed (1,247 commits) but no delta doc published; test-driven verification needed `[Q:OQ-4]` |
| Chunk-renderer coexistence policy (Celeritas on Cleanroom; stale Vintagium forks) | Landscape mapped (§2.3); the *policy* — detect and bail vs. integrate — is a design-doc decision `[Q:OQ-5]` |
| Exact OF-under-Cleanroom caveat profile (wiki says "with caveats"; OptiRefine exists because plain OF misbehaves) | `[Q:OQ-17]` |

---

## 6. Modern-platform opportunities and constraints

*Highest-density `[U]` zone in this document: most items originate from AI reasoning in the
idea doc (§"Benefits you didn't mention"). Each carries a risk note; none should be promoted
into the design doc without its OQ row being resolved or the claim spot-checked.*

### 6.1 Constraints (hard, keep in view)

| Constraint | Consequence |
|---|---|
| **Compat profile is mandatory** `[D-9]` `[V:doc + V:mcp]` | LWJGL3's value = modern entry points/extensions/tooling *within* compat; never plan a core-profile rewrite. GL_QUADS stays available (keep the triangle-strip composite fallback anyway) |
| **UBOs are unusable for the pack contract** `[V:doc]` | Packs declare default-block GLSL-120-style uniforms; per-program upload with location caching remains the model |
| Vanilla renders through fixed-function state + client arrays + display lists | Hooks must cooperate with FF state; matrix uniforms are captured from the FF stack at defined moments (§4.4) |
| `org.lwjglx` is runtime-only | Compile against LWJGL3 proper `[V:mcp]`; note the shim itself is being reworked `[Q:OQ-21]` |
| Alpha-platform drift (daily releases) | Pin loader/toolchain versions; re-verify §5 facts at design time `[Q:OQ-2]` |
| Kirino-Engine horizon | Backend-swappable core required (§5.2) `[Q:OQ-20]` |

### 6.2 GL opportunities (all within compat profile)

| Opportunity | Replaces (reference behavior) | Risk note |
|---|---|---|
| Core GL program/shader objects (`glCreateProgram` …) | ARB shader-object entry points throughout `[V:observed]` | Trivial; behavior-identical `[U]` |
| Guaranteed `glGenerateMipmap` (GL 3.0 baseline) | Capability-gated mipmap generation | None `[U]` |
| Core geometry shaders (GL 3.2) with internal translation | `ARB_geometry_shader4` program-parameter dance | Packs still declare the ARB extension + `maxVerticesOut`; the preprocessor must keep accepting both forms `[V:doc]` |
| **GL 3.3 sampler objects** per stage | Re-setting filter parameters on flip textures every frame | Low risk; removes per-frame state churn `[U]` |
| **PBO + fence-sync async readback** for `centerDepthSmooth` | Synchronous `glReadPixels` every frame (full pipeline stall) `[V:observed]` | One-frame latency on an already-smoothed value; verify imperceptibility `[U]` |
| **GLFW shared-context async shader compile** (+ async `_n`/`_s` atlas upload) | Multi-second render-thread freeze on pack switch | Driver quality for shared compat contexts varies; needs a synchronous fallback `[U→OQ-15]` |
| KHR_debug labels/groups + debug context | ARB-era debug handling | Dev-only; pairs with RenderBook's Nsight integration `[V:web]` |
| Explicit GLFW context hints (compat profile, debug) | Implicit LWJGL2 Display context | Depends on how Cleanroom owns context creation `[Q:OQ-3]` |
| HiDPI correctness via framebuffer-size (≠ window size) | 1.12.2 Display had no content-scale concept | Verify what Cleanroom's window layer already handles `[Q:OQ-3]` |
| GL 4.2/4.3 floor for §3.6 features (compute, images, SSBO) when the pack requires them | *(no G6 equivalent)* | Post-v0.5; capability-gate per pack via feature flags (§3.6.7); excludes macOS |

### 6.3 JVM opportunities

| Opportunity | Notes |
|---|---|
| **Delete the allocation-discipline design constraint** | OF's array caches / mutable positions / intrusive lists exist because of 2012-era GCs `[V:observed]`; generational ZGC on Java 25 makes straightforward code acceptable. Write clean code first; optimize with evidence `[U]` |
| FFM API for native buffer work | Replaces reflection-into-direct-buffer hacks; useful for pixel-transfer paths `[U]` |
| Vector API for CPU-side math | Per-quad tangent math, frustum plane tests `[U]` — measure first (incubator churn risk) |
| Modern language features | Records/sealed types for the option & uniform models; text blocks for embedded GLSL; `MethodHandle`/bytecode-compiled expressions for per-frame custom-uniform evaluation `[U]` |
| JUnit 6 + headless GL testing | JUnit wiring already in the template `[V:template]`; RenderBook's JUnit OpenGL Extension exists `[V:web]` — CI (headless/EGL) viability still to prove `[Q:OQ-10]` |

---

## 7. Architectural direction (research-level)

*Constraints and directional decisions the design doc must honor or overturn with cause — not
a design. Class diagrams, module layouts, and API shapes belong to the design doc.*

### 7.1 Hook strategy

`[D-5]` The engine integrates through **targeted Mixin injections** — the same ~25–30
behavioral hook points OF compiles into its replaced classes, expressed as CleanMix mixins
declared via `MixinConfigs`, written against SRG names `[V:mcp]`.

The **behavioral hook needs** (what must happen, independent of injection mechanics):

1. Frame begin/end (world-state sampling; previous-frame snapshots; composite guarantee).
2. Camera-matrix capture immediately after camera setup.
3. Shadow-pass invocation before the world render (own camera/frustum/traversal).
4. Per-phase program switching around: sky (incl. the celestial-rotation moment for
   sun/moon-position uniforms), terrain layers (solid/cutout-mipped/cutout/translucent),
   damaged-block overlay, entities (+ per-entity id), glowing outline, block entities
   (+ per-TE id), beacon beam, armor glint, spider eyes, particles (lit/unlit), clouds,
   weather, hand solid/translucent (+ depth scale), first-person overlay.
5. Depth-texture copies (before translucent; before weather) + center-depth readback.
6. Deferred-stage trigger between solid and translucent terrain; composite/final at frame end.
7. Vertex-write interception: entity-data stamping + per-quad attribute computation in the
   buffer-builder path; attribute-pointer setup in both VBO and client-array draw paths.
8. Chunk-build entity push/pop around per-block model rendering.
9. Atlas-stitch companion hooks (`_n`/`_s` loading, atlas-size uniform).
10. Display-resize + framebuffer-size interception.
11. GUI: pack selection/options screens; F3+R / `/reloadShaders` reload paths.

The **candidate vanilla classes** with resolved MCP/SRG/obfuscated names are catalogued in
**Appendix E** — the main new research artifact this document adds over its sources. Every
class there also appears in OF's 112-replaced-classes list (`files.txt`), confirming each is
genuinely load-bearing for a shader engine.

### 7.2 Engine-core / loader-glue seam

`[D-6]` Keep the engine core (pack parsing, preprocessor, program registry, uniform system,
expression engine, FBO-management policy) free of Minecraft/loader types behind a thin glue
layer. Three reasons, in rising order of importance: (a) headless testability (§8.3);
(b) it removes the only irreversible coupling decision; (c) **Kirino-Engine** (§5.2) means
the render backend under Schmaloogium may be replaced wholesale within the mod's lifetime —
the core must survive a backend swap. Cost is near zero if done from day one.

### 7.3 Stage registry for the modern superset

`[D-4]` The stage/pass registry models the full modern sequence (§3.6.1) even while only a
subset is wired: stage identity, pass arrays with sparse population **up to index 99**,
per-pass buffer read/write sets, flip bookkeeping, and (later) compute-dispatch slots
(`.csh` + `_a…_z` suffixes) and buffer counts beyond 8 (§3.6.3). The 1.12.2-era five stages
become instances of the general mechanism, not the mechanism itself.

### 7.4 Vertex-format extensibility

The 56-byte layout (Appendix C) is the *floor*, not the format. Requirements recorded:
attribute slots addressable by name; layout growable (`at_midBlock` as the canonical first
addition — semantics now documented, §3.6.5) without touching every consumer; per-program
attribute enablement driven by source scanning (§3.2); and a defined coexistence story for
replaced chunk pipelines (Celeritas today, Kirino tomorrow — likely "detect and disable with
a clear message" first `[A]`, `[Q:OQ-5]`). Interaction with Forge baked-quad/lighting caches
on format switch is a known friction point to spike early `[Q:OQ-14]`.

### 7.5 Renderer-identity macros (open decision — sharpened, not resolved)

Now informed by §3.6.7: `IS_IRIS` is the ecosystem's engine discriminator, OF has *no*
positive identifier, and Iris's **feature-flag system is the sanctioned granular capability
channel**. Options `[Q:OQ-7]`:

1. **Identify as OF-era** (no `IS_IRIS`, OF macro set only): classic packs take OF paths —
   correct for v0.1–v0.5; modern packs assume OF semantics, which is *more* honest for us
   than Iris impersonation.
2. **Define `IS_IRIS` (+ `IRIS_VERSION`)**: packs assume the full gated Iris feature set —
   the documented partial-impersonation trap. Only viable per-pack, never globally, until
   feature coverage is deep.
3. **OF-era identity + honest Iris feature flags + own `SCHMALOOGIUM` macro** (+ per-pack
   compatibility overrides in engine config): advertise exactly what we support via
   `IRIS_FEATURE_*`-style defines; packs that check flags work; packs that only check
   `IS_IRIS` get OF-era behavior.

Leaning 3 `[A]`; final call needs pack-matrix experiments (§8) once v0.1 exists.

### 7.6 Options GUI

ModularUI (Cleanroom-canonical, LGPL-3.0, featured examples `[V:mcp]`) is the candidate for
pack-selection/options screens. Fitness unproven for: dynamically generated screens from
`screen.*` config, sliders bound to option values, profile cycling, tooltip-from-lang
plumbing `[Q:OQ-9]`. LGPL-3.0 dependency note in §10.3.

### 7.7 Upstream engagement (upgraded from "nice to have" to necessary)

Cleanroom's maintainers are actively designing the render future of the platform (§5.2:
Discussion #405 is maintainer-authored; Kirino-Engine is org-hosted; the RenderBook author
*is* the #405 proposer). Engagement plan: participate in #405 with the shader engine's hook
requirements as a concrete consumer use-case; track Kirino milestones; propose sanctioned
hook points where our mixins are riskiest. Schmaloogium as the reference rendering consumer
of Cleanroom's future API is the best available insurance for `[Q:OQ-20]`.

---

## 8. Compatibility target and conformance strategy

### 8.1 The pack-compat matrix is the definition of done

`[D-3]` Support is defined as "these packs render correctly", not "the spec is implemented".
Candidate matrix with verified versions/licenses: **Appendix G**. Composition: 3 classic
OptiFine-era packs (SEUS Renewed, Chocapic13 V9, projectLUMA) + 4 actively-maintained
dual-spec packs (BSL, Complementary Reimagined/Unbound, Sildur's Vibrant).

**Unusual luck worth recording `[V:web]`:** the actively-maintained "big three" (BSL,
Complementary, Sildur's) still ship **dual-spec** (OptiFine + Iris) with declared support
down to MC 1.7.10 — meaning current 2026 releases are still nominally consumable by an
OF-1.12.2-era engine. The modern/classic split is therefore softer than feared; verify
per-pack at runtime (Modrinth version metadata is coarse). Angelica (the 1.7.10 precedent)
standardized on Complementary + Euphoria Patches as its primary target — a sensible primary
target for Schmaloogium too.

### 8.2 Conformance tiers (resolving the reference-render paradox)

Screenshot-diffing against OptiFine reference renders **only works for classic packs** — OF
G6 *is* the oracle for them. Modern-pack behavior on OF-1.12.2 is not a defined baseline, and
Iris-on-modern-MC renders a different world (not pixel-comparable). Tiers:

| Tier | Applies to | Gate |
|---|---|---|
| T0 — loads | all matrix packs | pack parses, programs compile, no GL errors, stable frame loop |
| T1 — renders plausibly | all matrix packs | hand-approved baseline screenshots (fixed seed/scene/time/camera set), re-diffed against *themselves* thereafter (regression oracle) |
| T2 — pixel-parity vs OF | classic packs only | screenshot diff vs OF G6 on the fixed scene set within tolerance |
| T3 — feature-complete | per pack | every feature the pack exercises (per its own option screens) behaves; no fallback program silently masking a failure |

### 8.3 Harness

`[D-10]` From week one: fixed test scenes (seed + coordinates + time + weather + camera
paths), automated screenshot capture, image diff with tolerance, plus **headless smoke tests
for the portable core** (run a matrix pack's sources through the preprocessor against a
recorded GL-capability profile; validate resource-sizing decisions without a live context —
this is where the `[D-6]` seam pays rent). RenderBook's **JUnit OpenGL Extension** is
confirmed to exist `[V:web]`; CI (headless EGL/virtual display) viability still to prove
`[Q:OQ-10]`. JUnit wiring is already in the template `[V:template]`.

**CI fixtures — resolved policy `[V:web]`:** none of the seven matrix packs may be committed
to the repo or re-hosted (App G licensing column; even Complementary's comparatively
permissive license bans redistribution outside Modrinth/CurseForge channels). CI must
**download at test time** (BSL, Complementary, Sildur's have stable Modrinth version IDs via
the Modrinth API) with a local cache, and never re-host. SEUS/Chocapic/projectLUMA:
canonical-download-only.

---

## 9. Milestones

`[D]` From the idea doc, with exit criteria expressed against §8. Each milestone ships
something a user can run.

| Milestone | Scope | Exit criteria |
|---|---|---|
| **v0.1** | gbuffers + composite + final; program registry w/ backup chains (modern-superset-shaped `[D-4]`); preprocessor + macros + includes; source-scan resource sizing; main FBO ping-pong + flips + clears; built-in uniforms (no shadow set); fixed unit map; options *parsing* (no GUI); internal default pack | ≥1 classic pack at T1 on the fixed scenes; T0 across the classic matrix |
| **v0.2** | Shadow pass (camera, texel snap, frustum + traversal, depth split, translucency toggle, shadow uniforms/samplers, hardware PCF) | Classic packs with shadows at T1; first T2 runs |
| **v0.3** | Extended vertex format + per-quad attributes + entity-data stack; block/item/entity aliases (+ per-mod merge); per-entity/TE id uniforms | Classic packs at T2 within tolerance on terrain scenes |
| **v0.4** | Custom uniforms/variables expression engine; profiles/screens/sliders model; options GUI (§7.6); per-program alphaTest/blend/scale/flip overrides | Classic matrix at T2/T3; options round-trip persistence |
| **v0.5** | `_n`/`_s` companion atlases + `MC_NORMAL_MAP`/`MC_SPECULAR_MAP`; noise texture; custom textures (all 3 source forms); depth copies incl. async center-depth; render scale; instancing | Full classic matrix at T3 |
| **post-v0.5** (explicitly out of the above) | §3.6 modern stages (shadowcomp/prepare/setup/begin, compute), `RENDERTARGETS` + >8 colortex, modern attributes (`at_midBlock` first), custom images/SSBOs (GL-floor-gated), feature-flag advertisement + identity decision finalization (§7.5), era-bridge positioning for GLSL 150+ packs; dual-spec matrix packs (current BSL/Complementary/Sildur's releases) to T1+ | Modern-matrix progression |

Scope-discipline note: v0.1 is deliberately shippable-useful ("runs simple packs, no
shadows") — the anti-abandonment weapon (*IDEA_DOC §"How to avoid joining the graveyard"*).

---

## 10. Legal and licensing

### 10.1 Clean-room methodology `[D-8]`

- **The contract sources are:** the shipped pack-author docs (`doc/shaders.txt`,
  `doc/shaders.properties`), upstream published OptiFine documentation
  (`sp614x/optifine` OptiFineDoc), Iris/shaderLABS published documentation (incl. the
  ShaderDoc spec repo), and **observed pack behavior**.
- **Open-source engines (Iris, Angelica) may be read, and their LGPL-3.0 code may be
  incorporated** with license compliance: LGPL-3.0 combines into a GPL-3.0-or-later work
  `[D-7]` provided copyright/license notices are preserved and modifications are marked.
  One trap remains flagged: Iris bundles **glsl-transformer, which is AGPL-3.0** — never
  copy it; its network-service terms would attach to the derived portion `[V:web]`.
- **The OptiFine decompile (schlorbium-project) is behavioral-observation-only.** No
  identifier, structure, or code derived from it ships. This document deliberately restates
  decompilation-derived findings as *behavior* (§4), never implementation structure.

### 10.2 Provenance honesty

SHADER_ENGINE_IMPL.md and DESIGN.md derive from a decompilation of a closed-source product
(itself a legally gray rebrand). They inform *understanding*; the bright line above is what
makes RESEARCH.md safe to commit publicly while its deep-dive sources stay in the private
sibling repo. Recorded plainly: behavioral knowledge in §4 traces to decompilation-assisted
study; the reimplementation contract is §3 + §8, not §4.

### 10.3 Licenses

| Artifact | License | Interaction |
|---|---|---|
| Schmaloogium | **GPL-3.0-or-later** (intended `[D-7]`; template's MIT-style LICENSE `[V:template]` to be replaced) | — |
| Cleanroom platform | LGPL-2.1 `[V:web]` | Platform, not a linked library in the derivative-work sense; standard mod practice `[Q:OQ-12]` |
| ModularUI (if adopted) | LGPL-3.0 `[V:mcp]` | Dynamic linking as a mod dependency is ecosystem-standard; jar-in-jar (`contain`) bundling eased under GPL-3.0-or-later (LGPL-3.0 combines cleanly) `[Q:OQ-12]` |
| Iris | **LGPL-3.0** `[V:web github license API]`; bundled glsl-transformer **AGPL-3.0** | LGPL-3.0 portions reusable with compliance per `[D-8]`; glsl-transformer-derived code remains **never-copy** |
| Angelica | LGPL-3.0 primarily, MIT portions `[V:web]` | Reusable with compliance per `[D-8]` (both LGPL-3.0 and MIT portions are GPL-compatible) |
| Kirino-Engine | Custom permissions license `[V:web]` | Observe API surface only; licensing needs review before any integration `[Q:OQ-20]` |
| Fugue | GPL-3.0 `[V:web]` | Ecosystem companion, no code relationship |
| Mc122RenderBook | MIT `[V:web]` | Freely usable (test-harness patterns) |
| Shader packs | Per pack — **all seven matrix candidates prohibit repo/CI bundling** (App G) | Download-at-test-time policy (§8.3) — resolves OQ-11 |

---

## 11. Open questions and risk register

*The design-doc author's to-do feed. Rule enforced in this doc: every `[U]` either has a row
here or was upgraded to `[V:*]`. Resolved rows stay (with date) — don't delete.*

| ID | Question | Why it matters | Blocks | Verification path | Status |
|---|---|---|---|---|---|
| OQ-1 | Does a sanctioned Cleanroom render API exist? | Could replace the riskiest mixins | §7.1 | CleanroomMC org | **RESOLVED 2026-07-24**: yes-in-progress — Discussion #405 + Kirino-Engine (§5.2); spawned OQ-20 |
| OQ-2 | Current Cleanroom loader vs template's 0.5.17-alpha pin | Alpha drift; daily cadence | build setup | GitHub releases | **RESOLVED 2026-07-24**: 0.6.6-alpha current; **standing item** — re-verify at design time and pin deliberately |
| OQ-3 | GL context creation mechanics under Cleanroom (compat request, GLFW hints, lwjglx runtime role, HiDPI) | Debug/shared contexts; resize hooks | §6.2 items | Cleanroom sources; RenderBook; ask upstream | open |
| OQ-4 | CleanMix divergences relevant to hot render-path injections | Hook viability/perf | §7.1, App E | CleanMix repo; spike-test injections | open |
| OQ-5 | Chunk-renderer coexistence policy (Celeritas now, Kirino later) | Vertex pipeline (§7.4) conflicts with replaced chunk pipelines | §7.4 | test against Celeritas source builds; decide detect-and-bail vs integrate | open (landscape mapped §2.3) |
| OQ-6 | Exact shadowcomp/prepare/setup/begin + compute semantics | §3.6 completeness | §7.3, post-v0.5 | upstream OF doc + Iris docs | **RESOLVED 2026-07-24**: documented in §3.6 (sources §12.5); residual: exact OF build introducing them is approximate only |
| OQ-7 | Renderer-identity macro + feature-flag posture | Packs branch on it | §7.5, v0.1 | pack experiments once v0.1 exists | open — options sharpened (§7.5), leaning option 3 |
| OQ-8 | Conformance oracle for modern packs | honest baselines | §8 | tiered approach | mitigated (tiers, §8.2) |
| OQ-9 | ModularUI fitness for generated option screens/sliders/profiles | v0.4 GUI | v0.4 | prototype against `screen.*` config | open |
| OQ-10 | Headless GL testing in CI (RenderBook JUnit GL Extension exists — but does it run headless/EGL on CI runners?) | §8.3 harness automation | §8.3 | try Mc122RenderBook extension on a CI runner | open — narrowed (harness confirmed to exist) |
| OQ-11 | Pack licenses re: CI fixtures | conformance automation legality | §8.3 | per-pack license read | **RESOLVED 2026-07-24**: no bundling permitted for any candidate; download-at-test-time via Modrinth API + cache (§8.3, App G) |
| OQ-12 | GPL-3.0-or-later mod on LGPL-2.1 platform + LGPL-3.0 GUI dep; jar-in-jar implications | licensing hygiene | §10.3 | short considered note; ecosystem precedent survey | open — concern reduced by the `[D-7]` GPL-3.0-or-later change (LGPL-3.0 combines cleanly) |
| OQ-13 | Modern vertex-attribute layouts and adoption | §7.4 format growth | post-v0.5 | Iris/OF docs | **RESOLVED 2026-07-24**: documented §3.6.5 / App C.3; residual: per-pack usage frequency unmeasured |
| OQ-14 | Forge baked-quad/LightUtil cache interplay on vertex-format switch under Cleanroom | v0.3 stability | v0.3 | early spike in dev env | open |
| OQ-15 | Shared-context async compile reliability across drivers (compat contexts) | §6.2 headline feature | quality-of-life | prototype + synchronous fallback design | open |
| OQ-16 | Named prior Iris-1.12.2 backports and their failure modes | §2.2 accuracy | doc hygiene | web search | **RESOLVED 2026-07-24**: §2.2 table (Spectra et al.); none usable |
| OQ-17 | Exact OF-under-Cleanroom caveat/failure profile | §2.3 claim accuracy | messaging | test; read OptiRefine issue tracker; wiki caveats | open |
| OQ-18 | Era-bridge reality: do GLSL 150+ modern packs compile/run meaningfully on a compat context | §3.5 positioning claim | post-v0.5 | experiment once v0.1 exists | open |
| OQ-19 | Iris exact license | §10.3 | doc hygiene | Iris repo | **RESOLVED 2026-07-24**: LGPL-3.0 (+ AGPL-3.0 glsl-transformer caveat) |
| OQ-20 | **Kirino-Engine trajectory**: timeline, default-on?, does a compat-profile vanilla pipeline survive beneath it, license terms | Every render-loop hook + the vertex pipeline could be invalidated; also the best future backend | long-term architecture (§5.2, §7.2) | track repo + #405; engage upstream (§7.7) | open — **highest-weight strategic risk** |
| OQ-21 | lwjglx replacement flux (LWJGLXX/LWJGLY) | template has `enable_lwjglx=true`; legacy-GL shim behavior may change under us | build config; §6.1 | track CleanroomMC repos | open |
| OQ-22 | Catch-all for low-risk `[U]` items: §2.4 effort estimates and the §6.2/§6.3 modernization claims without their own row (core-GL swap, sampler objects, PBO readback latency, FFM/Vector API payoffs, expression-engine compilation) | Individually small; collectively they shape effort planning | implementation-time choices | spot-check each at the milestone that touches it; promote to its own OQ row if it turns out contentious | open |

---

## 12. Annotated index of reference material

*Trust levels: **contract** (authoritative for §3), **behavioral** (authoritative for §4),
**decisions** (authoritative for §1), **platform** (authoritative for §5), **context**.*

### 12.1 Sibling repo `schlorbium-project/` (decompiled OF G6_pre1, study-only — §10.2)

| Path | What | Trust | Read when |
|---|---|---|---|
| `SCHMALOOGIUM_IDEA_DOC.md` | The vision chat log: feasibility analysis, Cleanroom benefits, mission-statement review. Cite by assistant-turn headings | decisions (+ `[U]` claims) | revisiting any D-n rationale |
| `SHADER_ENGINE_IMPL.md` | 14-section engine deep dive (lifecycle, registry, FBOs, frame flow, shadow pass, vertex pipeline, textures, options, uniforms, aliases, GUI, failure handling, data-flow diagram) | behavioral | any §4 topic needs mechanism-level detail |
| `DESIGN.md` | Whole-mod anatomy: identity/provenance, loading & patch architecture, feature survey (the skip-list), engineering observations, 112-class appendix | behavioral/context | understanding what we're *not* building |
| `doc/shaders.txt` (668 ln) | **The G6-era pack-author spec** — programs, attributes, uniforms, sampler tables, directives, formats, ID mapping, macros | **contract** | verifying any §3 / App A–D fact |
| `doc/shaders.properties` (489 ln) | Annotated key-by-key properties reference incl. expression language | **contract** | verifying §3.3 / App F |
| `doc/*` (25 other files) | MCPatcher-format specs (CTM, CIT, CEM, colors, sky…) | context | confirming non-goals only |
| `files.txt` (112 ln) | The 112 replaced vanilla classes | behavioral | hook-class cross-check (App E) |
| `net/schlorbium/shaders/` (87 files), `expr/` (23) | The decompiled engine itself | behavioral (observation only, D-8) | last resort when the digests are silent; never as code source |

### 12.2 This repo (`Schmaloogium/`)

| Path | What |
|---|---|
| `build.gradle`, `gradle.properties`, `settings.gradle`, `gradle/scripts/*` | Platform ground truth (§5.1); Unimined kappa + Blossom + deps |
| `src/main/java*/com/example/modid/**` | Template skeleton to be renamed/filled |
| `.github/workflows/*` | CI: build / tag-release / CurseForge+Modrinth publish |
| `RESEARCH.md` | this document |

### 12.3 Sibling prior-art repos

`schlorbium-fixes/` (ASM coremod patching OF; RetroFuturaGradle/Java 8) and
`schlorbiumCITpatch/` (Mixin patch of OF CIT internals; legacy ForgeGradle) — §2.1 evidence;
also small worked examples of 1.12.2 coremod/Mixin packaging outside Cleanroom.

### 12.4 MCP server `cleanroom` — query recipes

| Need | Tool/query |
|---|---|
| Any vanilla symbol → readable/SRG/obf | `resolve_symbol("net.minecraft…" / "func_…" / notch token)` — the App E workhorse |
| Cleanroom/Forge API classes, events, annotations | `search_cleanroom_api(query, kind=event/annotation)` |
| Mixin/AT setup canon | `get_porting_guide("mixin-setup")`; scaffolding via `get_project_template(...)` |
| Docs (cleanroommc wiki + Forge 1.12.x docs) | `search_docs(query, category=…, loader=cleanroom)` |
| Real-mod patterns (ModularUI, MinecraftByExample) | `search_mod_examples(query, category=gui/coremods-mixins)` |

### 12.5 Web sources (all accessed 2026-07-24)

| Source | URL | Feeds |
|---|---|---|
| Upstream OptiFine shader spec (current) | `https://raw.githubusercontent.com/sp614x/optifine/master/OptiFineDoc/doc/shaders.txt` | §3.6 |
| Iris docs (per-topic pages under /current/reference/) | `https://shaders.properties` | §3.6 |
| Iris spec repo (incl. `unsupported-features.md` — worth mining) | `https://github.com/IrisShaders/ShaderDoc` | §3.6, §7.5 |
| Iris repo + license | `https://github.com/IrisShaders/Iris` | §10.3 |
| Cleanroom loader + releases | `https://github.com/CleanroomMC/Cleanroom` (+ `/releases`, `/discussions/405`) | §5 |
| Kirino-Engine | `https://github.com/CleanroomMC/Kirino-Engine` | §5.2 |
| CleanMix / Foundation / Fugue / LWJGLXX / LWJGLY | `https://github.com/CleanroomMC/<name>` | §5 |
| RenderBook | `https://github.com/tttsaurus/Mc122RenderBook` (linked from `https://cleanroommc.com/wiki/forge-mod-development/render/render-documentation`) | §5.1, §8.3 |
| Cleanroom wiki (install, modpack prep, porting) | `https://cleanroommc.com/wiki/…` | §2.3, §5 |
| Angelica | `https://github.com/GTNewHorizons/Angelica` | §2.3 |
| Vintagium | `https://github.com/Asek3/sodium-1.12` | §2.3 |
| Celeritas | `https://git.taumc.org/embeddedt/celeritas` (GH mirror: GTNewHorizons/Celeritas) | §2.3 |
| Nothirium | `https://github.com/Meldexun/Nothirium` + CurseForge page | §2.3 |
| OptiRefine | `https://github.com/Ecdcaeb/OptiRefine` | §2.1, §2.3 |
| Spectra (Iris backport attempt) | `https://github.com/kristitrnka/Spectra-Broken` | §2.2 |
| Pack pages | Modrinth (BSL, Complementary ×2, Sildur's), CurseForge (Chocapic13, projectLUMA), sonicether.com (SEUS) | App G |

Fetch failures recorded (do not silently retry-and-trust): bitslablab.com (HTTP 526 — BSL
first-party terms unverified; Modrinth ARR used), Nothirium raw README (404; CurseForge page
used), Celeritas on Modrinth (absent; source-only distribution).

---

## Appendix A — Program registry `[V:doc]`

*Provenance: `doc/shaders.txt` "Shader Programs" table, cross-checked against the reference
registry `[V:observed]`. 2026-07-24. Modern additions: §3.6.1.*

### A.1 Programs, what they render, fallback chain

| Program | Renders | Fallback ("when not defined use") |
|---|---|---|
| *(none)* | gui, menus | — |
| `shadow` | everything in the shadow pass | *(none — fixed pipeline)* |
| `shadow_solid` | *(unused)* | shadow |
| `shadow_cutout` | *(unused)* | shadow |
| `gbuffers_basic` | leash, block selection box | *(none)* |
| `gbuffers_textured` | particles | gbuffers_basic |
| `gbuffers_textured_lit` | lit particles, world border | gbuffers_textured |
| `gbuffers_skybasic` | sky, horizon, stars, void | gbuffers_basic |
| `gbuffers_skytextured` | sun, moon | gbuffers_textured |
| `gbuffers_clouds` | clouds | gbuffers_textured |
| `gbuffers_terrain` | solid, cutout, cutout_mip terrain | gbuffers_textured_lit |
| `gbuffers_terrain_solid` | *(unused in G6; Iris re-activates)* | gbuffers_terrain |
| `gbuffers_terrain_cutout_mip` | *(unused)* | gbuffers_terrain |
| `gbuffers_terrain_cutout` | *(unused in G6; Iris re-activates)* | gbuffers_terrain |
| `gbuffers_damagedblock` | block-damage overlay | gbuffers_terrain |
| `gbuffers_block` | block entities | gbuffers_terrain |
| `gbuffers_beaconbeam` | beacon beam | gbuffers_textured |
| `gbuffers_item` | *(unused in 1.12.2)* | gbuffers_textured_lit |
| `gbuffers_entities` | entities | gbuffers_textured_lit |
| `gbuffers_entities_glowing` | glowing entities (spectral effect) | gbuffers_entities |
| `gbuffers_armor_glint` | enchantment glint | gbuffers_textured |
| `gbuffers_spidereyes` | spider/enderman/dragon eyes | gbuffers_textured |
| `gbuffers_hand` | hand + opaque handheld | gbuffers_textured_lit |
| `gbuffers_weather` | rain, snow | gbuffers_textured_lit |
| `deferred_pre` | *(virtual — flip control only)* | — |
| `deferred` … `deferred15` | fullscreen passes between solid terrain and translucents | *(none — pass skipped)* |
| `gbuffers_water` | translucent terrain | gbuffers_terrain |
| `gbuffers_hand_water` | translucent handheld | gbuffers_hand |
| `composite_pre` | *(virtual — flip control only)* | — |
| `composite` … `composite15` | fullscreen end-of-frame passes | *(none — pass skipped)* |
| `final` | to screen | *(none — passthrough copy)* |

Count: 60 named shader/virtual slots, excluding the external `<none>` sentinel; includes the 2
virtual programs and the 16-element deferred/composite arrays `[V:doc]`. Modern spec extends arrays
to 99 and adds the §3.6.1 pass families — registry must not hardcode either 16 or this table's exact membership `[D-4]`.

### A.2 Backup-chain semantics

A program with no source files inherits the **entire configuration** (compiled program,
draw buffers, alpha/blend, scale, flips…) of the nearest non-empty ancestor in the fallback
column; `shadow` never inherits. A pack shipping only `gbuffers_terrain` thereby covers the
whole terrain family. Programs disabled via profile / `program.<name>.enabled=false` are
treated as absent (fallback applies). `[V:observed]`

### A.3 Source-directive table

| Directive (canonical form) | Effect |
|---|---|
| `attribute … mc_Entity / mc_midTexCoord / at_tangent` (vsh) | enable extended vertex attribute for this program |
| `const int countInstances = N;` (vsh) | instanced re-render with `instanceId` uniform |
| `#extension GL_ARB_geometry_shader4 : enable` + `const int maxVerticesOut = N;` (gsh) | legacy geometry-shader configuration |
| `uniform … shadow/shadowtex0/shadowtex1/watershadow` | allocate 1–2 shadow depth buffers |
| `uniform … shadowcolor/shadowcolor0/shadowcolor1` | allocate 1–2 shadow color buffers |
| `uniform … depthtex0/1/2`, `gdepthtex` | allocate 1–3 main depth textures |
| `uniform … colortex0-7` / legacy names | raise used color-buffer count |
| `uniform … gdepth` | upgrade buffer 1 format RGBA → RGBA32F |
| `uniform … centerDepthSmooth` | enable center-depth readback |
| `const int shadowMapResolution = N;` / `/* SHADOWRES:N */` | shadow map size |
| `const float shadowMapFov = F;` / `/* SHADOWFOV:F */` | perspective shadow projection |
| `const float shadowDistance = F;` / `/* SHADOWHPL:F */` | ortho half-plane |
| `const float shadowDistanceRenderMul = F;` | >0 enables shadow render-distance optimization |
| `const float shadowIntervalSize = F;` | texel-snap interval (default 2.0) |
| `const bool generateShadowMipmap / generateShadowColorMipmap = true;` | post-shadow-pass mipmaps |
| `const bool shadowHardwareFiltering[0/1] = true;` | hardware-PCF compare mode |
| `const bool shadowtex0/1Mipmap, shadowcolor0/1Mipmap = true;` (+capitalization aliases) | per-texture mipmaps |
| `const bool shadowtex0/1Nearest, shadowcolor0/1Nearest = true;` (+aliases incl. `shadow0MinMagNearest`) | per-texture NEAREST filtering |
| `const float wetnessHalflife / drynessHalflife = F;` (ticks) / `/* WETNESSHL:F */`, `/* DRYNESSHL:F */` | wetness uniform decay |
| `const float eyeBrightnessHalflife / centerDepthHalflife = F;` | smoothing constants |
| `const float sunPathRotation = F;` | tilts the celestial path |
| `const float ambientOcclusionLevel = F;` | 0 = AO off, 1 = vanilla AO |
| `const int superSamplingLevel = N;` | SSAA multiplier |
| `const int noiseTextureResolution = N;` | enable + size noisetex (default 256) |
| `const int colortexNFormat / <legacyName>Format = FMT;` | buffer internal format (App B.4) |
| `const bool colortexNClear = false;` | skip clear (composite/deferred scope) |
| `const vec4 colortexNClearColor = vec4(…);` | clear color (composite/deferred scope) |
| `const bool colortexNMipmapEnabled = true;` | per-pass mipmap gen (composite/deferred/final) |
| `/* GAUX4FORMAT:RGBA32F\|RGB32F\|RGB16 */` | legacy buffer-7 format |
| `/* DRAWBUFFERS:XXXX */` | per-program draw-buffer routing (digits 0–7, `N` none) |
| *(modern)* `/* RENDERTARGETS: a,b,c */` | comma-separated routing, buffers 0–15 (§3.6.3) |

Both `const`-declaration and comment-directive forms must be parsed; legacy `// KEY:value`
comments too `[V:observed]`.

---

## Appendix B — Framebuffers, buffers, and the texture-unit map `[V:doc]`

*Provenance: `doc/shaders.txt` sampler/texture tables + observed FBO behavior. 2026-07-24.*

### B.1 Color buffers (G6 baseline)

| Index | Name | Legacy name | Clear behavior | Notes |
|---|---|---|---|---|
| 0 | colortex0 | gcolor | cleared to **fog color** | |
| 1 | colortex1 | gdepth | cleared to **solid white** | declared as `gdepth` → format upgraded to RGBA32F |
| 2 | colortex2 | gnormal | transparent black | |
| 3 | colortex3 | composite | transparent black | |
| 4–7 | colortex4-7 | gaux1-4 | transparent black | double as custom-texture units in gbuffers stage |

≥4 guaranteed, 8 where hardware allows (modern: 16, Iris 1.10.5+: 32 — §3.6.3). Every buffer
= **two textures ("main"/"alt" = A/B)**; gbuffers write "main"; deferred/composite read
"main", write "alt", then flip written buffers (per-program `flip.*` overrides; virtual
`deferred_pre`/`composite_pre` pre-flip). Blending is disabled while composites write color
attachments; reading a buffer the same composite writes produces artifacts
(contract-documented caveat).

### B.2 Depth and shadow buffers

| Name | Contents |
|---|---|
| depthtex0 | everything |
| depthtex1 | copy taken **before translucents** (no water/stained glass) |
| depthtex2 | copy taken **before weather** (also excludes handheld) |
| shadowtex0 | shadow-pass depth, everything |
| shadowtex1 | copy **before shadow translucents** (water-shadow split) |
| shadowcolor0/1 | shadow-pass color attachments (modern: 2–7 behind `HIGHER_SHADOWCOLOR`) |

### B.3 The fixed texture-unit map (packs rely on these numbers)

| Unit | GBUFFERS / SHADOW programs | DEFERRED / COMPOSITE / FINAL programs |
|---|---|---|
| 0 | `texture` (atlas/albedo; shadow-stage alias `tex`) | `colortex0` / `gcolor` |
| 1 | `lightmap` | `colortex1` / `gdepth` |
| 2 | `normals` (atlas `_n`) | `colortex2` / `gnormal` |
| 3 | `specular` (atlas `_s`) | `colortex3` / `composite` |
| 4 | `shadowtex0`; `watershadow`; `shadow` when no water-shadow | same |
| 5 | `shadowtex1`; `shadow` when water-shadow enabled | same |
| 6 | `depthtex0` | `depthtex0` / `gdepthtex` |
| 7 | `gaux1` / custom | `colortex4` / `gaux1` |
| 8 | `gaux2` / custom | `colortex5` / `gaux2` |
| 9 | `gaux3` / custom | `colortex6` / `gaux3` |
| 10 | `gaux4` / custom | `colortex7` / `gaux4` |
| 11 | `depthtex1` | `depthtex1` |
| 12 | — | `depthtex2` |
| 13 | `shadowcolor0` / `shadowcolor` | same |
| 14 | `shadowcolor1` | same |
| 15 | `noisetex` | same |

(Buffer index → unit mapping is `{0,1,2,3,7,8,9,10}` `[V:observed]`.)

> **Known inconsistency in the shipped doc:** `doc/shaders.txt`'s "GBuffers Textures" ID
> table lists `depthtex1` at unit **12**, but its own uniform table and the observed engine
> behavior both bind `depthtex1` to unit **11** in gbuffers programs (12 = `depthtex2`,
> composite stage only). Treat **11 as authoritative**; this is exactly the kind of
> doc-vs-behavior quirk conformance testing exists to catch.

### B.4 Texture formats (37), pixel formats, pixel types

Internal formats accepted by `<buf>Format` and raw custom textures:

- 8-bit norm: `R8 RG8 RGB8 RGBA8`; signed: `R8_SNORM RG8_SNORM RGB8_SNORM RGBA8_SNORM`
- 16-bit norm: `R16 RG16 RGB16 RGBA16`; signed: `R16_SNORM RG16_SNORM RGB16_SNORM RGBA16_SNORM`
- 16-bit float: `R16F RG16F RGB16F RGBA16F`; 32-bit float: `R32F RG32F RGB32F RGBA32F`
- 32-bit int: `R32I RG32I RGB32I RGBA32I`; uint: `R32UI RG32UI RGB32UI RGBA32UI`
- Mixed: `R3_G3_B2 RGB5_A1 RGB10_A2 R11F_G11F_B10F RGB9_E5`

Pixel formats: `RED RG RGB BGR RGBA BGRA` + `*_INTEGER` variants (integer internal formats
require the integer transfer path `[V:observed]`). Pixel types: `BYTE SHORT INT HALF_FLOAT
FLOAT UNSIGNED_BYTE` + packed variants (`UNSIGNED_SHORT_5_6_5`, `UNSIGNED_INT_8_8_8_8_REV`,
… full list in doc/shaders.txt "Pixel Types").

### B.5 Draw-buffer index prefixes

`colortex<0-7>` → 0–7; legacy `gcolor gdepth gnormal composite gaux1-4` → 0,1,2,3,4,5,6,7.

---

## Appendix C — Vertex format `[V:observed]`

*Provenance: reference-implementation behavior. Modern additions `[V:web]` §3.6.5.
2026-07-24.*

### C.1 The 1.12.2-era extended layout (the floor — §7.4)

Vanilla 28-byte block vertex extended to **56 bytes / 14 ints**:

| Ints | Byte offset | Content | GLSL input | Attrib location |
|---|---|---|---|---|
| 0–2 | 0 | position 3×float | `gl_Vertex` | FF |
| 3 | 12 | color 4×ubyte | `gl_Color` | FF |
| 4–5 | 16 | UV 2×float | `gl_MultiTexCoord0` | FF |
| 6 | 24 | lightmap 2×short | `gl_MultiTexCoord1` | FF |
| 7 | 28 | normal 3×byte + pad | `gl_Normal` | FF |
| 8–9 | 32 | mid-texture UV 2×float | `mc_midTexCoord` | **11** |
| 10–11 | 40 | tangent 4×short (xyz + handedness w) | `at_tangent` | **12** |
| 12–13 | 48 | entity data 3×short (2 used) | `mc_Entity` | **10** |

### C.2 Population rules

- `mc_Entity` value = current top of the chunk-build entity stack
  (`renderType<<16 | aliased-block-id`, metadata) stamped into every vertex at write time.
- Per quad (every 4 vertices): face normal = normalize((v2−v0)×(v3−v1)); tangent/bitangent
  from UV deltas with handedness `w = sign(dot(bitangent, normal×tangent))`;
  `mc_midTexCoord` = average of the quad's UVs.
- Attribute pointers: stride 56, offsets 32/40/48, arrays 10–12 enabled only around
  shader-active draws; both VBO and client-array paths required.

### C.3 Growth candidates (design for growth; implement post-v0.5) `[V:web]`

| Attribute | Semantics (see §3.6.5) |
|---|---|
| `at_midBlock` | vec3 offset to block center in 1/64-block units (OF); Iris vec4 with `.w` = light emission (`BLOCK_EMISSION_ATTRIBUTE`) |
| `at_velocity` | view-space offset to previous frame, entities/BEs only — expensive; §3.6.8 risk |
| `mc_chunkFade` | Iris/1.21-era; only the constant −1.0 fallback is honest on 1.12.2 |
| core-profile `va*` family | only relevant if we ever target packs with no legacy path (§3.6.8) |

---

## Appendix D — Built-in uniform inventory `[V:doc]`

*Provenance: `doc/shaders.txt` "Uniforms" section; cadence notes `[V:observed]`. Sampler
uniforms: App B.3. Modern delta: §3.6.6. 2026-07-24.*

### D.1 Held item / player

| Uniform | Type | Value |
|---|---|---|
| `heldItemId` / `heldItemId2` | int | alias-mapped item id, main/off hand |
| `heldBlockLightValue` / `heldBlockLightValue2` | int | held-item light value (old-hand-light mode: brighter hand wins) |
| `wetness` | float | rainStrength smoothed by wetness/drynessHalflife |
| `eyeAltitude` | float | view-entity Y |
| `eyeBrightness` | ivec2 | x block / y sky light, 0–240 |
| `eyeBrightnessSmooth` | ivec2 | smoothed by eyeBrightnessHalflife |
| `isEyeInWater` | int | 1 water, 2 lava |
| `nightVision` / `blindness` | float | effect strengths 0–1 |
| `screenBrightness` | float | video-settings brightness 0–1 |
| `hideGUI` | int | F1 state |

### D.2 World / time / weather

| Uniform | Type | Value |
|---|---|---|
| `worldTime` | int | ticks % 24000 |
| `worldDay` | int | ticks / 24000 |
| `moonPhase` | int | 0–7 |
| `frameCounter` | int | frame index, wraps at 720720 |
| `frameTime` | float | last frame seconds |
| `frameTimeCounter` | float | runtime seconds, wraps at 3600 |
| `sunAngle` / `shadowAngle` | float | 0–1 celestial angles |
| `rainStrength` | float | 0–1 |
| `fogMode` | int | GL_LINEAR / GL_EXP / GL_EXP2 |
| `fogDensity` | float | 0–1 |
| `fogColor` / `skyColor` | vec3 | rgb |

### D.3 Camera / matrices / screen

| Uniform | Type | Value |
|---|---|---|
| `aspectRatio`, `viewWidth`, `viewHeight` | float | render dimensions |
| `near`, `far` | float | 0.05 / renderDistance×16 |
| `sunPosition`, `moonPosition`, `shadowLightPosition`, `upPosition` | vec3 | eye-space celestial vectors (updated at the celestial-rotation moment) |
| `cameraPosition`, `previousCameraPosition` | vec3 | world-space |
| `gbufferModelView`, `gbufferModelViewInverse`, `gbufferPreviousModelView` | mat4 | |
| `gbufferProjection`, `gbufferProjectionInverse`, `gbufferPreviousProjection` | mat4 | |
| `shadowProjection`, `shadowProjectionInverse`, `shadowModelView`, `shadowModelViewInverse` | mat4 | |
| `centerDepthSmooth` | float | center-pixel depth, smoothed by centerDepthHalflife |
| `atlasSize` | ivec2 | set while the atlas texture is bound |
| `terrainTextureSize`, `terrainIconSize` | ivec2/int | documented "not used" |

### D.4 Per-draw dynamics (excluded from custom-uniform expressions)

| Uniform | Type | Value |
|---|---|---|
| `entityColor` | vec4 | hurt/flash tint |
| `entityId` | int | alias-mapped, set per entity |
| `blockEntityId` | int | alias-mapped, set per block entity |
| `blendFunc` | ivec4 | current blend srcRGB, dstRGB, srcA, dstA |
| `instanceId` | int | 0 original, 1..N instanced copies |

Cadence model `[V:observed]`: everything refreshes on program switch (per-program location
cache + redundant-upload skip; matrices always upload); celestial vectors at the sky-rotation
moment; shadow matrices during shadow-pass camera setup; per-draw dynamics at their hooks;
custom uniforms on every program switch after built-ins.

---

## Appendix E — Hooks catalog `[V:mcp mappings DB, 2026-07-24]`

*Behavioral hook needs (§7.1) → candidate 1.12.2 classes → resolved names (MCP stable_39 /
SRG / notch). Injection-style column is a candidate, not a design commitment. Every class
below also appears in OF's `files.txt` replacement list — independent confirmation that each
is load-bearing. Obfuscated method names are heavily overloaded single letters; Mixin targets
must use SRG name + descriptor.*

### E.1 Class and method resolution table

| # | Class (readable) | Obf | Key methods (readable → SRG, descriptor) | Serves hook needs (§7.1) |
|---|---|---|---|---|
| 1 | `net.minecraft.client.renderer.EntityRenderer` | `buq` | `renderWorldPass(IFJ)V` → `func_175068_a`; `updateCameraAndRender(FJ)V` → `func_181560_a`; `setupCameraTransform(FI)V` → `func_78479_a`; `renderHand(FI)V` → `func_78476_b` | 1, 2, 3 (invocation site), 4 (hand), 5 |
| 2 | `net.minecraft.client.renderer.RenderGlobal` | `buy` | `renderBlockLayer(BlockRenderLayer)V` → `func_174982_a`; `renderEntities(Entity,ICamera,F)V` → `func_180446_a`; `renderSky(FI)V` → `func_174976_a`; `renderClouds(FIDDD)V` → `func_180447_b`; `setupTerrain(Entity,D,ICamera,I,Z)V` → `func_174970_a` | 4 (sky/terrain/entities/clouds), 6, shadow-pass traversal (3) |
| 3 | `net.minecraft.client.renderer.chunk.RenderChunk` | `bxr` | `rebuildChunk(FFF,ChunkCompileTaskGenerator)V` → `func_178581_b` | 8 |
| 4 | `net.minecraft.client.renderer.chunk.ChunkRenderDispatcher` | `bxm` | — (class-level; async build coordination) | 8 |
| 5 | `net.minecraft.client.renderer.BufferBuilder` | `buk` | `begin(I,VertexFormat)V` → `func_181668_a`; `endVertex()V` → `func_181675_d`; `addVertexData([I)V` → `func_178981_a` | 7 (vertex stamping + per-quad attributes) |
| 6 | `net.minecraft.client.renderer.Tessellator` | `bve` | `draw()V` → `func_78381_a` | 7 (client-array draw path) |
| 7 | `net.minecraft.client.renderer.WorldVertexBufferUploader` | `bul` | `draw(BufferBuilder)V` → `func_181679_a` | 7 (attribute pointers, non-VBO) |
| 8 | `net.minecraft.client.renderer.vertex.VertexBuffer` | `cdz` | `bufferData(ByteBuffer)V` → `func_181722_a`; `drawArrays(I)V` → `func_177358_a` | 7 (VBO upload + draw) |
| 9 | `net.minecraft.client.renderer.BlockModelRenderer` | `bvo` | `renderModel(IBlockAccess,IBakedModel,IBlockState,BlockPos,BufferBuilder,Z)Z` → `func_178267_a` | 8 (entity push/pop per block) |
| 10 | `net.minecraft.client.renderer.texture.TextureMap` | `cdp` | `loadTexture(IResourceManager)V` → `func_110551_a`; `loadTextureAtlas(IResourceManager)V` → `func_110571_b` | 9 (`_n`/`_s` companion atlases, atlasSize) |
| 11 | `net.minecraft.client.renderer.texture.TextureAtlasSprite` | `cdq` | — (class-level; per-sprite companion loading) | 9 |
| 12 | `net.minecraft.client.renderer.ItemRenderer` | `buu` | `renderItemInFirstPerson(F)V` → `func_78440_a` | 4 (hand/first-person overlay) |
| 13 | `net.minecraft.client.renderer.entity.RenderManager` | `bzf` | `renderEntityStatic(Entity,F,Z)V` → `func_188388_a`; `renderEntity(Entity,DDDFFZ)V` → `func_188391_a` (note: no `doRenderEntity` exists in 1.12.2) | 4 (per-entity id uniform) |
| 14 | `net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher` | `bwx` | `render(TileEntity,DDD,F)V` → `func_147549_a` | 4 (per-TE id uniform) |
| 15 | `net.minecraft.client.particle.ParticleManager` | `btg` | `renderParticles(Entity,F)V` → `func_78874_a` | 4 (particles lit/unlit) |
| 16 | `net.minecraft.client.renderer.GlStateManager` | `bus` | — (class-level; blend/alpha state observation for `blendFunc` uniform + per-program state locking) | 4-adjacent state cooperation |
| 17 | `net.minecraft.client.shader.Framebuffer` | `bvd` | `bindFramebuffer(Z)V` → `func_147610_a` | 6 (final-to-screen handoff), 10 |
| 18 | `net.minecraft.client.multiplayer.WorldClient` | `bsb` | — (class-level; world-state sampling for uniforms) | 1 |

### E.2 Coverage notes

- **Hook needs 1–6** map onto classes 1–2 + 17 (frame lifecycle, camera, phases, depth
  copies, composite trigger) — the two big render classes carry most injection points, as
  expected from the reference's hook distribution `[V:observed]`.
- **Hook need 7** spans classes 5–8 (vertex write, both draw paths); **need 8** classes 3–4 +
  9; **need 9** classes 10–11; **need 10** class 17 + display-resize site (context layer,
  `[Q:OQ-3]`); **need 11** is pure mod-side GUI (no vanilla injection).
- Sky celestial-rotation hooks (uniform update moment for sun/moon vectors) live inside
  `renderSky` — sub-method injection points to be identified at design time within
  `func_174976_a`.
- Remaining resolution work for the design doc: exact `@Inject`/`@Redirect`/`@ModifyArg`
  choices per site, `@At` targets inside long methods, and CleanMix-specific validation
  `[Q:OQ-4]`.

---

## Appendix F — `shaders.properties` key catalog `[V:doc]`

*Provenance: `doc/shaders.properties` annotated sample. Semantics condensed; the file itself
is macro-preprocessed before parsing. Modern/Iris-only keys: §3.6.4. 2026-07-24.*

### F.1 Engine flags (tri-state: unset/true/false unless noted)

`clouds=fast|fancy|off` · `oldHandLight` · `dynamicHandLight` · `oldLighting` ·
`shadowTranslucent` · `underwaterOverlay` · `sun` · `moon` · `vignette` ·
`backFace.solid` · `backFace.cutout` · `backFace.cutoutMipped` · `backFace.translucent` ·
`rain.depth` · `beacon.beam.depth` · `separateAo` (AO moves to color.a) · `frustum.culling`
— corresponding in-game video settings win where both exist.

### F.2 Compatibility

`version.<mcver>=<edition>` — minimum engine edition per MC version (pack-list warning).

### F.3 Options (discovered in sources; properties only organizes them)

- Switch: `#define NAME // tooltip` (default ON) or `// #define NAME // tooltip` (default
  OFF); recognized only when the same file `#ifdef`/`#ifndef`s it. Tooltips split on ". ";
  lines ending "!" render red.
- Variable: `#define NAME <value> // tooltip [v1 v2 v3]`; default auto-added to the list.
- Const options (whitelist): `shadowMapResolution shadowMapFov shadowDistance
  shadowDistanceRenderMul shadowIntervalSize generateShadowMipmap generateShadowColorMipmap
  shadowHardwareFiltering[/0/1] shadowtex0/1Mipmap(+aliases) shadowcolor0/1Mipmap(+aliases)
  shadowtex0/1Nearest(+aliases) shadowcolor0/1Nearest(+aliases) wetnessHalflife
  drynessHalflife eyeBrightnessHalflife centerDepthHalflife sunPathRotation
  ambientOcclusionLevel superSamplingLevel noiseTextureResolution` — visible only when
  carrying a value list or referenced by a slider/profile/screen.
- Ambiguous options (conflicting defaults across files) are disabled.
- Lang decoration in `shaders/lang/*.lang`: `option.<NAME>[.comment]`, `value.<NAME>.<val>`,
  `prefix./suffix.<NAME>`, `profile.<NAME>[.comment]`, `screen.<NAME>[.comment]`.
- `sliders=<option list>` renders listed variable options as sliders.

### F.4 Profiles and screens

- `profile.NAME=<tokens>`: `OPTION` (bool on), `!OPTION` (off), `OPTION:value` /
  `OPTION=value`, `profile.OTHER` (copy, cycle-guarded), `!program.<name>` (disable program;
  may carry a dimension prefix, e.g. `world-1/gbuffers_water`). Current profile inferred from
  option values; otherwise "Custom".
- `screen=<entries>` main, `screen.NAME=<entries>` subscreens; entries: option names,
  `[SUBSCREEN]`, `<profile>`, `<empty>`, `*` (all unplaced options).
  `screen[.NAME].columns=N` (default 2; auto-widens beyond 18 options).

### F.5 Custom textures and noise

- `texture.<gbuffers|deferred|composite>.<samplerName>[.0-9]=` one of:
  pack-relative PNG path; `minecraft:`-prefixed asset (incl. `dynamic/lightmap_1`, atlas
  paths; `_n`/`_s` suffix selects companion variants); raw form
  `<path> <TEXTURE_1D|2D|3D|RECTANGLE> <internalFormat> <dims…> <pixelFormat> <pixelType>`.
  Multiple texture types may share a unit (distinguished by sampler type; one type per unit
  per program). `.mcmeta` sidecars set blur/clamp. Stage names map: gbuffers → gbuffers +
  shadow programs; deferred → deferred; composite → composite + final.
- `texture.noise=<pack path>` overrides the generated noise texture.

### F.6 Custom uniforms / variables (expression language)

- `uniform.<float|int|bool|vec2|vec3|vec4>.<name>=<expr>` uploads on program change;
  `variable.<type>.<name>=<expr>` defines reusable intermediates (not uploaded).
- Inputs: numeric literals, `pi true false`, `BIOME_*` constants, `biome temperature
  rainfall` (camera biome), every fixed scalar uniform (vector members `.x/.y/.z`, colors
  `.r/.g/.b`, matrix `name.<row>.<col>`), view-entity booleans `is_alive is_burning is_child
  is_glowing is_hurt is_in_lava is_in_water is_invisible is_on_ground is_ridden is_riding
  is_sneaking is_sprinting is_wet`. Excluded (change mid-program): `entityColor entityId
  blockEntityId fogMode fogColor`.
- Operators: `+ - * / %`, `! && ||`, `> >= < <= == !=`.
- Functions: `sin cos asin acos tan atan atan2 torad todeg min max clamp abs floor ceil exp
  frac log pow random round signum sqrt fmod`, conditional `if(cond,val,…,val_else)`,
  `smooth([id,]val[,fadeIn[,fadeOut]])` (time-corrected exponential, default 1 s), boolean
  `between(x,min,max) equals(x,y,eps) in(x,v1,v2,…)`, constructors `vec2 vec3 vec4`.
- Precipitation rule (documented alongside): rain renders when
  `biome_precipitation != PPT_NONE`; rain if `temperature >= 0.15`, else snow.

### F.7 Per-program render state

`alphaTest.<prog>=off|<NEVER|LESS|EQUAL|LEQUAL|GREATER|NOTEQUAL|GEQUAL|ALWAYS> <ref>` ·
`blend.<prog>=off|<src> <dst> [<srcA> <dstA>]` (15 GL factors) ·
`scale.<prog>=<scale> [<offsetX> <offsetY>]` (composite/deferred sub-viewport, 0.0–1.0) ·
`flip.<prog>.<buf>=true|false` (ping-pong override; virtual `*_pre` programs accepted; last
writer should leave flip enabled so later passes can read) ·
`program.<prog>.enabled=<boolean expr over switch options>` (disabled → fallback chain).

### F.8 Dimension & includes (contract recap)

`shaders/world<id>/` overrides (empty folder = shaders off in that dimension; options also
scanned there, may use distinct names); `#include` relative or `/`-absolute from `shaders/`,
depth ≤ 10, include-guard idiom documented; `-Dshaders.debug.save=true` dumps final processed
sources to `shaderpacks/debug/` (a debug affordance worth replicating `[A]`).

---

## Appendix G — Pack matrix candidates `[V:web, 2026-07-24]`

| Pack | Version (date) | Spec era | 1.12.2 support | License / CI stance | Source |
|---|---|---|---|---|---|
| **BSL Shaders** | v10.1.3 (2026-04-20) | Dual (OptiFine + Iris) | Listed 1.7.10–1.21.2 | **ARR** → no bundling; Modrinth-API download at test time | modrinth.com/shader/bsl-shaders |
| **Complementary Reimagined** | r5.8.1 (2026-05-21) | Dual | Listed 1.7.10+ | Complementary License 1.6: no redistribution outside Modrinth/CF, no modified bundling → download-on-demand only | modrinth.com/shader/complementary-reimagined |
| **Complementary Unbound** | r5.8.1 (2026-05-21) | Dual | Listed 1.7.10+ | Same as Reimagined | modrinth.com/shader/complementary-unbound |
| **Sildur's Vibrant** | v2.01 Extreme (2026-06-22) | Dual, OptiFine-native lineage | "Every version since 1.7.10" (author) — strongest legacy candidate | ARR → no bundling | sildurs-shaders.github.io |
| **Chocapic13** | V9 (2020-09-18, final) | Classic OptiFine-era | Yes (CurseForge lists 1.12.x) | ARR + famous edit-license (edits allowed w/ naming+credit rules); no unmodified redistribution | curseforge.com/minecraft/customization/chocapic13-shaders |
| **SEUS Renewed** | 1.0.1 (2020-02-10) | Classic OptiFine/legacy-GLSL era | Yes | "© Sonic Ether. All rights reserved." → canonical-download only, never bundle | sonicether.com/seus |
| **projectLUMA** | v1.32 (2019-12-14) | Classic OptiFine-era (KUDA successor) | Yes (1.12 range) | ARR, attribution required → no bundling | curseforge.com/minecraft/customization/projectluma |

Matrix roles: SEUS Renewed + Chocapic V9 + projectLUMA = the **classic tier** (T2
pixel-parity oracle vs OF G6). BSL + Complementary ×2 + Sildur's = the **dual-spec tier**
(T0/T1 targets through v0.5; feature-flag/modern-stage targets post-v0.5). Primary
compatibility target per Angelica precedent: **Complementary** (+ Euphoria Patches later).
Caveats: Modrinth version-range metadata is coarse — verify each release actually loads on
OF-1.12.2-era engines before enshrining it in CI; BSL first-party terms page was unreachable
(HTTP 526) — ARR per Modrinth used.

---

## Appendix H — Glossary

| Term | Meaning |
|---|---|
| **Cleanroom** | Modernized Forge-lineage loader for MC 1.12.2 (Java 25+, LWJGL3, built-in Mixin) |
| **CleanroomLoader / Foundation** | Cleanroom's FML revamp / its LaunchWrapper replacement |
| **CleanMix** | Cleanroom's built-in Mixin (Fabric-Mixin fork at `org.spongepowered.asm`, MixinExtras bundled) |
| **Kirino-Engine** | CleanroomMC's WIP next-gen render engine (§5.2) — future backend risk/opportunity |
| **lwjglx / LWJGLXX / LWJGLY** | LWJGL2-API compat layer over LWJGL3 (runtime-only) and its in-flux successors |
| **Fugue / Scalar** | Cleanroom companion mods: compat patches / Scala provider |
| **ModularUI** | Cleanroom-canonical GUI framework (LGPL-3.0) |
| **RenderBook (Mc122RenderBook)** | 1.12.2 rendering-education repo (MIT) with a JUnit OpenGL test extension |
| **Unimined (kappa fork)** | The Gradle toolchain the template builds with (not ForgeGradle) |
| **OF** | OptiFine; reference implementation = HD_U G6_pre1 for 1.12.2 |
| **schlorbium** | The decompiled, rebranded OF used as behavioral reference (study-only) |
| **Iris / Oculus** | The modern-MC open-source shader engine (Fabric / its Forge port) defining today's pack ecosystem |
| **Angelica / Celeritas / Vintagium / Nothirium** | Prior-art render mods (§2.3) |
| **pack contract** | The pack-author-facing format in §3 |
| **gbuffers** | The geometry stage: world rendered into multiple color attachments |
| **deferred / composite passes** | Fullscreen shader passes (mid-frame / end-of-frame) |
| **setup / begin / shadowcomp / prepare** | Modern-spec pass arrays (§3.6.1) |
| **dfb / sfb** | Reference shorthand for the main / shadow framebuffers (§4.3) |
| **ping-pong (main/alt, A/B)** | Two textures per color buffer; passes read one, write the other, then flip |
| **flip** | The main/alt swap after a pass writes a buffer (`flip.*` overrides) |
| **backup chain** | Program fallback inheritance (App A.2) |
| **mc_Entity / mc_midTexCoord / at_tangent** | The extended vertex attributes (App C) |
| **`_n` / `_s` textures** | Normal / specular companion textures (atlas-wide) |
| **labPBR** | Modern pack-side specular/normal encoding convention (engine-neutral; advertised via `MC_TEXTURE_FORMAT_LAB_PBR`) |
| **compat profile** | OpenGL compatibility profile — required by the contract (§3.5) |
| **feature flags** | Iris's `iris.features.required/optional` capability-negotiation channel (§3.6.7) |
| **IS_IRIS** | The ecosystem's engine-discriminator macro (§3.6.7, §7.5) |
| **SRG / MCP / notch names** | 1.12.2 mapping layers (stable intermediate / readable / obfuscated) |
| **T0–T3** | Conformance tiers (§8.2) |
| **D-n / OQ-n** | Decision-log / open-question identifiers (§1.3 / §11) |
