# Schmaloogium — Phase 13: Texture systems — Architecture

## 0. Header

- **Phase:** 13 — Texture systems
- **Milestone:** v0.5 (`docs/design/v3/DESIGN.md:2438`; RESEARCH v0.5 row at
  `docs/research/v1/RESEARCH.md:951`)
- **Module/package:** `:engine` texture policy plus `:mod` glue and mixins — §2.1 states the exact
  placement and the one pending Phase 1 package grant
- **Declared dependencies:** Phases 3, 5, 7 (`docs/design/v3/DESIGN.md:625`)
- **Assigned open questions:** none — the phase row's `OQs` cell is `—`
  (`docs/design/v3/DESIGN.md:2438`)
- **Governing design:** `docs/design/v3/DESIGN.md`, Part I §G0–§G12 and the Phase 13 specification
  only
- **Design status:** initial build document (§G1.1), not yet verified
- **Date:** 2026-08-08

The commissioning request explicitly selected v3, so every `DESIGN.md` coordinate in this document
was re-derived from `docs/design/v3/DESIGN.md`'s own headings and endpoints rather than shifted from
another revision (§G0.4 step 1, `docs/design/v3/DESIGN.md:205`–`:207`). Phase 11 v1 set the
precedent for a new build adopting v3 at its initial build; Phases 3–9 remain anchored to
`docs/design/v2.0-RC3/DESIGN.md` and this document repoints none of them. `docs/MOVES.md` records
the adoption.

### 0.1 Inputs actually read

| Input | Portion read | Why |
|---|---|---|
| `docs/design/v3/DESIGN.md` | Part I §G0–§G12 (`:132`–`:1134`) and the Phase 13 specification (`:2436`–`:2510`); §G9 template at `:817`–`:854` | mandatory §G1.1 reading |
| `docs/research/v1/RESEARCH.md` | §0 (`:11`–`:52`), §1 (`:55`–`:105`), §4.6 texture part (`:593`–`:599`), §9 milestones (`:940`–`:955`), App B.3 (`:1228`–`:1256`), App B.4 (`:1257`–`:1270`), App D.3 (`:1355`–`:1368`), App E (`:1387`–`:1433`) with rows 10–11 at `:1410`–`:1411`, App F.3 const whitelist (`:1454`–`:1470`), App F.5 (`:1482`–`:1492`) | the contract this phase implements |
| `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` (`PD`) | §11 whole (`:619`–`:645`), §7.6 (`:468`–`:489`), §7.4 (`:437`–`:450`), §17 rows B10/B13 (`:796`, `:799`), §18 (`:803`–`:819`) | the §G11.6 P13 reading map row (`docs/design/v3/DESIGN.md:1003`) |
| `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md` | §8 "Texture system" only (`:456`–`:487`) | the one section the spec grants (`docs/design/v3/DESIGN.md:2491`), under §G7 item 2 |
| `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties` | custom-texture and noise block (`:78`–`:125`) | legally clean shipped pack-author doc, citable freely (§G7 item 3, `docs/design/v3/DESIGN.md:743`–`:745`) |
| `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` | sampler/unit tables (`:190`–`:207`, `:272`–`:326`), `atlasSize` row (`:177`), noise const row (`:422`), option-macro rows (`:655`–`:656`) | same shipped-doc grant |
| `docs/phase3/v1/PHASE_3_DOC.md` | §1.2 ownership row (`:300`), §3.2 texture rows (`:737`–`:743`), §3.3 noise row (`:793`), §4.4 macros (`:945`–`:984`), §5.1 in full (`:1396`–`:1576`) | dependency contract |
| `docs/phase5/v1/PHASE_5_DOC.md` | §1.2 ownership (`:382`), resize consumer types (`:617`–`:621`), `addResizeConsumer` (`:1807`), §4.12 in full (`:1869`–`:1957`), §5.1 (`:2000`–`:2020`), §6 row (`:2116`), §9 row (`:2321`) | dependency contract |
| `docs/phase7/v1/PHASE_7_DOC.md` | §1.2 ownership (`:356`–`:357`), hook need 9 (`:563`), executor step 5 (`:931`–`:933`), §4.10.1 catalog notation (`:1075`–`:1098`), §4.10.8 rows 10–11 (`:1244`–`:1245`), §4.11 atlas event row (`:1269`), reload types (`:1564`–`:1568`), §5.5 hand-off (`:2120`) | dependency contract (provisional — §0.2) |
| `docs/phase1/v14/PHASE_1_DOC.md` | package tables (`:1526`–`:1558`), seam constraints C-1…C-4 (`:2229`–`:2244`) | module placement and the D-6 seam |
| MCP `cleanroom` | `resolve_symbol` for `TextureMap`, `TextureAtlasSprite`, `AbstractTexture#getGlTextureId`, `TextureManager#getTexture`; `get_class_details` for both texture classes; `search_cleanroom_api("texture stitch", kind=event)` and `get_api_class` for `TextureStitchEvent` | the spec's named MCP recipes (`docs/design/v3/DESIGN.md:2493`–`:2494`) |

Dependency PHASE docs consumed: Phase 3 (custom-texture and noise algebra, macro configuration),
Phase 5 (texture-overlay lease, fixed unit table, format vocabulary, resize consumer), Phase 7
(hook catalog format, App E deferral ledger, reload lifecycle).

### 0.2 Deviations from the assigned reading list, and their reasons

1. **Phase 7 is not a verified dependency, and this build proceeded anyway under maintainer
   authorization.** §G5.3 item 1 requires a dependency doc to be verified before a dependent build
   session reads it (`docs/design/v3/DESIGN.md:659`–`:663`), and the only sanctioned exception is
   Phase 12's *soft* dependency on Phase 7 (`:668`–`:671`), which does not extend to Phase 13.
   `docs/phase7/reviews/PHASE_7_REVIEW_32.md:299`–`:301` returned
   `PASS-WITH-CORRECTIONS` with `Interface changed: yes`, and
   `docs/phase7/v1/PHASE_7_DOC.md:2474`–`:2475`
   states "v1 remains unverified pending a fresh whole-document review." The maintainer authorized
   proceeding rather than blocking Phase 13 on Phase 7 round 33. Consequence, recorded here and
   again in §5.4: every contract this document consumes from Phase 7 is **provisional**, and if
   round 33 changes Phase 7 §5 in a way that contradicts §5.4's table, Phase 13 owes a §G1.3 fix-up
   session, not a rebuild. The residual risk is small and bounded: round 32's §5 delta was
   `AnaglyphEye`, the reshaped `UniformSignal` variants, `BlendStateValue`, and the removal of
   `ColorValue` — Phase 6 uniform-signal plumbing that Phase 13 does not consume. `[D-P13-2]`
2. **PD §7.4 was read although the Required-inputs line names only §11 and §7.6.** The Phase 13
   scope body cites §7.4 directly for the filter/wrap do-not-inherit row
   (`docs/design/v3/DESIGN.md:2476`–`:2478`), so the row cannot be honored without reading it. PD
   §17 rows B10/B13 and §18 were read for the same reason: §G11.4 requires the relevant
   do-not-inherit rows to be shown handled in §3 (`docs/design/v3/DESIGN.md:958`–`:960`).
3. **The shipped pack-author docs were read beyond `SHADER_ENGINE_IMPL.md` §8.** PD §7.4's
   "filter/wrap suffixes" wording did not resolve against App F.5's grammar, and §G0.1 forbids
   silently smoothing a contradiction. `doc/shaders.properties` and `doc/shaders.txt` are explicitly
   legally clean and citable freely (§G7 item 3, `docs/design/v3/DESIGN.md:743`–`:745`), and they
   settle the question. §3.6 records the ruling.
4. **`docs/phase1/v14/PHASE_1_DOC.md` was read although Phase 1 is not a declared dependency.** Only
   the package tables and the C-1…C-4 seam constraints, because §G1.1 step 2 requires module
   placement per §G3 and the §G3.1 map does not name a texture package.
5. **`AGENTS.md`, `docs/MOVES.md`, `docs/tooling/CODEX_MIGRATION_OVERLAY.md`, and the three
   dependency review files were read as repository governance.** `docs/MOVES.md` is the path
   authority for a tree with six files named `DESIGN.md`; the review files are the only place a
   phase states whether it is verified. This matches the reading the Phase 7 build session recorded
   (`docs/phase7/v1/PHASE_7_DOC.md:19`).
6. **`OCULUS_DESIGN.md` (`OD`) was deliberately not read.** §G12.6 maps P13 to OD §3, §9, §11–§17
   but states plainly that the map "does not amend any phase's current Required inputs"
   (`docs/design/v3/DESIGN.md:1113`–`:1116`), and the Phase 13 Required-inputs list does not name
   OD. The one OD-derived fact this document must respect reaches it through Part I rather than
   through OD: §G12.4's recorded conflict **C-TX01** (`docs/design/v3/DESIGN.md:1088`), which is
   honored in §3.3 and §11.3. No finding is cited to OD.
7. No web search was performed; no listed input was missing or contradictory in a way web evidence
   could settle. No forbidden source was opened: no directory named `chatlogs/` below `docs/` and no
   repository-root `*.txt` was read.

### 0.3 Legal and provenance posture

- The decompiled OptiFine reference is **behavioral-observation-only** (§G7 item 2,
  `docs/design/v3/DESIGN.md:738`–`:742`). This document restates observed *behavior* — companion
  atlases with matching mip chains, the default fill values, the generator recurrence, the per-stage
  binding moments — and contains no class name, method name, field name, or identifier from that
  decompile. Where a numeric algorithm is specified it is specified as arithmetic, not as ported
  code.
- Pintonium is LGPL-3.0 and readable and reusable with compliance (§G11.2 item 1). Every claim taken
  from it carries `PD §n` plus the `[V:observed — Pintonium <path>]` tag required by §G11.4
  (`docs/design/v3/DESIGN.md:945`). Nothing here traces to `org.taumc:glsl-transformation-lib`,
  which is treated as AGPL-3.0 and never copied or adopted (§G11.2 item 2).
- The shipped `doc/shaders.txt` and `doc/shaders.properties` are the legally clean contract sources
  and are cited directly (§G7 item 3).
- No Oculus material is cited; see §0.2 item 6.

---

## 1. Scope & boundaries

### 1.1 What Phase 13 owns

Phase 13 owns the texture estate that is not a framebuffer attachment:

1. **`_n`/`_s` companion atlases** for the block/item atlases — per-sprite companion discovery, full
   companion atlases whose layout and mip chain match the base atlas exactly, the missing-sprite
   default fills, binding on units 2 and 3 during world rendering, and the animation tick that keeps
   companion frames in step with their base sprite.
2. **The generated noise texture** — the reproducible generator, `noiseTextureResolution²` RGB
   sizing, unit 15, and the `texture.noise=<path>` pack override.
3. **Pack custom textures** in all three App F.5 source forms, their `.mcmeta` filter/wrap sidecars,
   their per-stage expansion, sampler-type disambiguation when several types share a unit, and their
   lifecycle across pack, option, and resource reloads.
4. **The `atlasSize` value source** — the ivec2 that is valid only while the atlas texture is bound.
5. **The texture-overlay publication** that Phase 5's fixed unit table consumes: registry,
   allocation, upload, ownership, fingerprinting, lease issue, and destruction
   (`docs/phase5/v1/PHASE_5_DOC.md:1902`–`:1903`).
6. **App E rows 10 and 11** — the `TextureMap` and `TextureAtlasSprite` hook sites Phase 7 deferred
   to this phase (`docs/phase7/v1/PHASE_7_DOC.md:1244`–`:1245`).

### 1.2 Adjacent ownership — explicit "owned by Phase Y" lines

| Concern this phase touches | Owner outside Phase 13 |
|---|---|
| The fixed App B.3 texture-unit map itself, the overlay/lease protocol shape, and which object each unit resolves to | **Phase 5** (`docs/phase5/v1/PHASE_5_DOC.md:1869`–`:1957`); Phase 13 supplies overlay entries only, and never searches for a free unit |
| Uploading sampler integers for those units, and uploading the `atlasSize` ivec2 | **Phase 6**; Phase 13 is the *value source* for `atlasSize` only (`docs/design/v3/DESIGN.md:2479`–`:2481`) |
| Tangent-frame math and any shading that consumes the sampled normals | **Phase 10** (`docs/design/v3/DESIGN.md:2485`–`:2486`) |
| labPBR channel semantics — what the specular channels *mean* | **pack-side convention; engine-neutral.** G8 advertises it (`docs/design/v3/DESIGN.md:2486`); Phase 13 delivers bytes and interprets none of them |
| Parsing `shaders.properties` texture keys and the `noiseTextureResolution` const option into lossless specs | **Phase 3** (`docs/phase3/v1/PHASE_3_DOC.md:300`); Phase 13 never reopens or reparses pack bytes |
| Emitting `MC_NORMAL_MAP` / `MC_SPECULAR_MAP` into shader sources | **Phase 3**'s `MacroConfiguration` (`docs/phase3/v1/PHASE_3_DOC.md:945`–`:984`); Phase 13 supplies the boolean that drives them (§5.3 request R1) |
| Alias-derived id values, held-item and entity id delivery | **Phase 9** |
| Frame/pass transactions, the draw-time bind order, reload orchestration, and the mixin application audit | **Phase 7**; Phase 13 contributes deferred hook rows and an overlay lease, not frame policy |
| Sampler objects, asynchronous transfers, PBO uploads, and any performance rework of these paths | **Phase 14** (`docs/design/v3/DESIGN.md:1686`–`:1687`) |
| Program compilation, slot resolution, backup chains, and the merged per-program declared-uniform layout | **Phase 4** |
| Scene definitions, capture, diffing, and fixture download | **Phase 2** |
| Options GUI presentation of anything above | **Phase 12** |

### 1.3 Hard boundaries

- Phase 13 allocates no framebuffer, performs no flip or clear, and attaches nothing. Every texture
  it owns is a sampled object.
- Phase 13 never assigns a texture unit. It publishes entries keyed by Phase 5's closed
  `TextureOverlayKey`; the unit number is Phase 5's. Pintonium's dynamic per-program allocation is a
  §G11.4 pre-decided rejection (`docs/design/v3/DESIGN.md:953`–`:954`) and is not an alternative.
- Phase 13 opens no GL outside `mod.glue`, per §G4.6; all GL is the Phase 1 facade.
- Phase 13 writes no policy into a mixin. Mixins observe and delegate (§G3.3).
- Phase 13 emits no macro text. It publishes a value; Phase 3 emits.

---

## 2. Architecture overview

### 2.1 Placement in the §G3 module layout

The D-6 seam decides the split: everything that can be decided without a GL context or a Minecraft
type is pure `:engine` policy, and everything that touches `TextureMap`, `TextureAtlasSprite`, the
resource manager, or GL lives in `:mod`.

| Layer | Package | Contents |
|---|---|---|
| `:engine` | `com.schmaloogium.engine.textures` *(pending grant — see below)* | companion-atlas planning, the noise generator, custom-texture spec resolution, `.mcmeta` interpretation, the overlay registry model, `atlasSize` values, closed results and failures. No Minecraft, Forge, Cleanroom, Mixin, or LWJGL type. |
| `:mod` | `com.schmaloogium.mod.glue.textures` *(pending grant)* | the atlas source adapter over `TextureMap`/`TextureAtlasSprite`, the resource-manager reader, the Forge stitch-event listener, and the facade-backed uploader |
| `:mod` | `com.schmaloogium.mod.mixin.textures` *(pending grant; `com.schmaloogium.mod.mixin` is already allocated to Phase 13)* | the dumb accessor and tick hooks of §4.6 |

`docs/phase1/v14/PHASE_1_DOC.md:1530`–`:1543` lists no `engine.textures` package. Phase 7 and Phase 8
each received an analogous three-package allocation (`engine.frame`/`mod.glue.frame`/
`mod.mixin.frame` at `:1537`, `:1551`, `:1554`; the shadow trio at `:1539`, `:1552`, `:1555`), so
this is the established pattern rather than a new mechanism. §5.3 request **R3** asks Phase 1 for it.
The fallback if R3 is not granted is bounded and already safe: Phase 1 explicitly names
`com.schmaloogium.mod.mixin` as a Phase 13 package (`docs/phase1/v14/PHASE_1_DOC.md:1553`), so the
mixins have a granted home regardless, and only the engine and glue package *names* are pending. No
type, contract, or behavior in this document depends on which name is granted.

C-1 is honored by construction: nothing in `engine.textures` references a Minecraft, Forge,
Cleanroom, Mixin, or LWJGL type, and the atlas is presented to it as a plain immutable descriptor
(§2.3). That is what makes the whole companion planner, the noise generator, and the entire custom
texture resolver headless-testable with JUnit alone (§8.1).

### 2.2 Public shape

```java
// engine — the phase entry point
public interface TextureSystem {
    TexturePlanResult      plan(TexturePlanRequest request);      // pure; no GL, no Minecraft
    TextureBuildResult     build(TextureBuildRequest request);    // render thread; allocates + uploads
    TextureOverlayLease    lease();                               // Phase 5's type; caller closes
    TextureOverlayPublicationId publicationId();                  // read with lease(), atomically
    AtlasSizeResult        atlasSize(AtlasId atlas);              // the App D value source
    void                   close();                               // idempotent; destroys owned objects
}
```

`plan` is pure and total: it consumes an immutable `TexturePlanRequest` and returns a closed
`TexturePlanResult` of `Planned(TexturePlan)` or `Invalid(TextureFailure)`. `build` re-runs identical
planning from its own request before touching GL, exactly as Phase 5's `create` does
(`docs/phase5/v1/PHASE_5_DOC.md:2002`), so a plan can never drift between the two calls.

`lease()` and `publicationId()` are read together from one atomic publication; Phase 7 passes both to
Phase 5's `textureBindings`, and Phase 5 rejects a mismatch before any bind
(`docs/phase5/v1/PHASE_5_DOC.md:1887`–`:1899`).

### 2.3 Key types and relationships

```java
// ---- inputs -------------------------------------------------------------
public record TexturePlanRequest(
    PackConfiguration configuration,          // Phase 3
    AtlasCatalog atlases,                     // §2.3 below; supplied by mod.glue
    CompanionPolicy companionPolicy,          // §4.1.1
    GLCapabilityProfile capabilities,         // Phase 1
    RegistryFingerprint registryFingerprint,  // Phase 4, via Phase 5's estate
    long estateGeneration) {}

// ---- the Minecraft-free atlas description -------------------------------
public record AtlasId(String canonicalName) {}
public record SpriteDescriptor(
    String iconName, int originX, int originY, int width, int height,
    int frameCount, boolean animated) {}
public record AtlasDescriptor(
    AtlasId id, int width, int height, int mipmapLevels,
    List<SpriteDescriptor> sprites) {}          // ordered by iconName, UTF-8 byte order
public record AtlasCatalog(List<AtlasDescriptor> atlases) {}

// ---- companion planning -------------------------------------------------
public enum CompanionKind { NORMALS, SPECULAR }
public record CompanionSpriteSource(
    CompanionKind kind, String iconName, CompanionOrigin origin) {}
public sealed interface CompanionOrigin {
    record Resource(String resourceIdentity) implements CompanionOrigin {}
    record DefaultFill(int packedRgba) implements CompanionOrigin {}
}
public record CompanionAtlasPlan(
    AtlasId base, CompanionKind kind, int width, int height, int mipmapLevels,
    List<CompanionSpriteSource> sprites, int defaultFill) {}

// ---- noise --------------------------------------------------------------
public sealed interface NoisePlan {
    record Generated(int resolution) implements NoisePlan {}
    record FromPack(NormalizedPackPath image,
                    Optional<TextureSidecarRef> sidecar, int declaredResolution)
        implements NoisePlan {}
    record Disabled() implements NoisePlan {}
}

// ---- custom textures ----------------------------------------------------
public record CustomTexturePlanEntry(
    TextureBindingKey key,                    // Phase 3's type, unchanged
    TextureOverlayKey overlayKey,             // Phase 5's closed domain
    Set<StageId> stages,                      // expansion of key.stage()
    TextureTarget target,                     // Phase 3's closed domain
    SamplerType samplerType,                  // §4.3.4
    TextureUploadSpec upload,                 // §4.3.3
    TextureParameterSpec parameters) {}       // §4.3.5, from the .mcmeta sidecar

public record TexturePlan(
    List<CompanionAtlasPlan> companions,      // 0, 1, or 2 entries per atlas
    NoisePlan noise,
    List<CustomTexturePlanEntry> customTextures,
    List<UnsupportedBinding> unsupported,     // §4.3.6; diagnosed, never silently dropped
    CompanionMacroState macroState,           // §4.1.6
    TextureMemoryEstimate memory) {}          // §4.8
```

`TexturePlan` is immutable, fingerprintable, and contains no handle. `TextureBuildResult` is closed
as `Ready(TexturePublication)` or `Failed(TextureFailure)`; there is no partial publication, matching
the Phase 5 discipline this phase composes with.

### 2.4 Invariants

1. **No dynamic unit allocation.** The only keys Phase 13 can publish are Phase 5's closed
   `TextureOverlayKey` values (`docs/phase5/v1/PHASE_5_DOC.md:1879`).
2. **One atomic publication.** `lease()` and `publicationId()` observe the same publication or the
   call fails; a lease never outlives the publication that issued it.
3. **Destruction is permission-gated.** Owned handles are destroyed only after Phase 7 closes the
   binding snapshot that borrowed them (`docs/phase5/v1/PHASE_5_DOC.md:1900`–`:1903`).
4. **Companion layout mirrors the base exactly.** Same extent, same per-sprite origin, same mip
   level count. A companion sprite is never rescaled or repacked independently, because the pack
   samples both with the *same* interpolated UVs from the same vertex.
5. **Shaders-off is untouched.** No Phase 13 code path alters vanilla atlas stitching, the vanilla
   atlas object, its mip chain, or its bindings (§G2.4 rule 5, `docs/design/v3/DESIGN.md:439`–`:440`).
6. **Every parse result comes from Phase 3.** Phase 13 opens image and binary *bytes*; it never
   opens `shaders.properties`.

---

## 3. Contract conformance map

Provenance tags follow RESEARCH §0.2 (`docs/research/v1/RESEARCH.md:24`–`:39`). "Design element" names
the §4 subsection that satisfies the row. Zero rows are unmapped.

### 3.1 Appendix F.5 — custom textures and noise (`docs/research/v1/RESEARCH.md:1482`–`:1492`)

| # | Contract surface | Design element | Provenance | Primary test |
|---|---|---|---|---|
| F5-1 | `texture.<gbuffers\|deferred\|composite>.<sampler>` key grammar | §4.3.1 consumes Phase 3's `TextureBindingKey` unchanged (`docs/phase3/v1/PHASE_3_DOC.md:1443`–`:1447`) | `[V:doc]` `docs/research/v1/RESEARCH.md:1484`; `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:81` | `custom_keyGrammarRoundTrip` |
| F5-2 | `.0`–`.9` suffix is a duplicate-key discriminator, not part of the sampler name | §4.3.1 — the discriminator selects among several bindings for one sampler, never renames it | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:117`; Phase 3 keeps it separate at `docs/phase3/v1/PHASE_3_DOC.md:1445`–`:1446` | `custom_duplicateDiscriminatorNotFoldedIntoSampler` |
| F5-3 | Pack-relative PNG source form | §4.3.2 `PackPath` branch | `[V:doc]` `docs/research/v1/RESEARCH.md:1485`; `…/doc/shaders.properties:91` | `custom_packPngLoads` |
| F5-4 | `minecraft:` asset-location source form | §4.3.2 `MinecraftResource` branch | `[V:doc]` `…/doc/shaders.properties:95` | `custom_minecraftAssetLoads` |
| F5-5 | `minecraft:dynamic/lightmap_1` live-texture form | §4.3.2 dynamic sub-branch — resolves to a live vanilla texture identity, never a copy | `[V:doc]` `docs/research/v1/RESEARCH.md:1485`; `…/doc/shaders.properties:98` | `custom_dynamicLightmapResolves` |
| F5-6 | `minecraft:` atlas-path form | §4.3.2 dynamic sub-branch, atlas identity | `[V:doc]` `…/doc/shaders.properties:99` | `custom_atlasPathResolves` |
| F5-7 | `_n`/`_s` suffix selects the companion variant of an asset/atlas path | §4.3.2 companion-variant resolution routes to §4.1's companion objects | `[V:doc]` `docs/research/v1/RESEARCH.md:1486`; `…/doc/shaders.properties:100`–`:101` | `custom_companionVariantSelection` |
| F5-8 | Raw binary form `<path> <target> <internalFormat> <dims…> <pixelFormat> <pixelType>` | §4.3.3 raw uploader over Phase 5's App B.4 vocabulary (`docs/phase5/v1/PHASE_5_DOC.md:2020`) | `[V:doc]` `docs/research/v1/RESEARCH.md:1486`–`:1487`; `…/doc/shaders.properties:104` | `custom_rawUploadAllTargets` |
| F5-9 | `TEXTURE_1D`/`2D`/`3D`/`RECTANGLE` targets with dimension arity 1/2/3/2 | §4.3.3; arity is already validated by Phase 3 (`docs/phase3/v1/PHASE_3_DOC.md:1455`–`:1456`) and re-checked against the byte length here | `[V:doc]` `…/doc/shaders.properties:106` | `custom_rawArityAndByteLength` |
| F5-10 | Several texture types may share one unit; **one type per unit per program** | §4.3.4 sampler-type disambiguation, using the per-program declared samplers | `[V:doc]` `docs/research/v1/RESEARCH.md:1488`–`:1489`; `…/doc/shaders.properties:114`–`:116` | `custom_oneSamplerTypePerUnitPerProgram` |
| F5-11 | `.mcmeta` sidecar sets blur (filter) | §4.3.5 — `blur=true` → `LINEAR`, false → `NEAREST` | `[V:doc]` `docs/research/v1/RESEARCH.md:1489`; `…/doc/shaders.properties:118`–`:119` | `custom_mcmetaBlurSetsFilter` |
| F5-12 | `.mcmeta` sidecar sets clamp (wrap) | §4.3.5 — `clamp=true` → `CLAMP_TO_EDGE`, false → `REPEAT` | `[V:doc]` same rows | `custom_mcmetaClampSetsWrap` |
| F5-13 | Stage `gbuffers` → gbuffers **and shadow** programs | §4.3.1 stage expansion, taken verbatim from Phase 3 (`docs/phase3/v1/PHASE_3_DOC.md:1446`–`:1447`) | `[V:doc]` `docs/research/v1/RESEARCH.md:1489`–`:1490`; `…/doc/shaders.properties:83` | `custom_stageExpansionGbuffersIncludesShadow` |
| F5-14 | Stage `deferred` → deferred programs | §4.3.1 | `[V:doc]` `…/doc/shaders.properties:84` | `custom_stageExpansionDeferred` |
| F5-15 | Stage `composite` → composite **and final** programs | §4.3.1 | `[V:doc]` `…/doc/shaders.properties:85` | `custom_stageExpansionCompositeIncludesFinal` |
| F5-16 | `texture.noise=<pack path>` overrides the generated noise | §4.2.4, consuming Phase 3's `NoiseTextureSpec.Override` (`docs/phase3/v1/PHASE_3_DOC.md:1461`–`:1463`) | `[V:doc]` `docs/research/v1/RESEARCH.md:1491`; `…/doc/shaders.properties:124` | `noise_packOverrideReplacesGenerated` |
| F5-17 | A custom texture may target a sampler name that is otherwise a colortex/gaux buffer | §4.3.6 — designed here in full; the Phase 5 key domain does not yet reach every such unit, so the shortfall is a flagged decision `[D-P13-11]` and §5.3 request **R2**, never a silent drop | `[V:doc]` `…/doc/shaders.properties:86`, `:91`; `[V:observed — Pintonium]` PD §11 override-of-colortex row (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:629`) | `custom_colortexOverrideDiagnosedOrBound` |

### 3.2 Appendix B.3 — the fixed texture-unit map (`docs/research/v1/RESEARCH.md:1228`–`:1256`)

| # | Contract surface | Design element | Provenance | Primary test |
|---|---|---|---|---|
| B3-1 | Unit 2 is `normals` (atlas `_n`) in gbuffers and shadow programs | §4.1.5 publishes `TextureOverlayKey.NORMALS`; Phase 5 resolves the unit (`docs/phase5/v1/PHASE_5_DOC.md:1909`) | `[V:doc]` `docs/research/v1/RESEARCH.md:1234`; `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:192` | `overlay_normalsPublishedForGbuffersAndShadow` |
| B3-2 | Unit 3 is `specular` (atlas `_s`) in gbuffers and shadow programs | §4.1.5, `TextureOverlayKey.SPECULAR` (`docs/phase5/v1/PHASE_5_DOC.md:1910`) | `[V:doc]` `docs/research/v1/RESEARCH.md:1235`; `…/doc/shaders.txt:193` | `overlay_specularPublishedForGbuffersAndShadow` |
| B3-3 | Unit 15 is `noisetex` at **every** stage | §4.2.5, `TextureOverlayKey.NOISE` at all stages (`docs/phase5/v1/PHASE_5_DOC.md:1922`, `:1928`) | `[V:doc]` `docs/research/v1/RESEARCH.md:1247`; `…/doc/shaders.txt:207` | `overlay_noisePublishedAtEveryStage` |
| B3-4 | Units 7–10 carry `gaux1`–`gaux4` custom textures in gbuffers/shadow | §4.3.1 maps those samplers to `TextureOverlayKey.GAUX1`…`GAUX4` (`docs/phase5/v1/PHASE_5_DOC.md:1914`–`:1917`) | `[V:doc]` `docs/research/v1/RESEARCH.md:1239`–`:1242`; `…/doc/shaders.txt:280`–`:283` | `overlay_gauxCustomTexturesPublished` |
| B3-5 | Companions bind **only during world rendering** | §4.1.5 — the overlay is published for the gbuffers and shadow stage ids only; every other stage resolves `Absent(NOT_APPLICABLE_TO_STAGE)` | `[V:observed]` `docs/research/v1/RESEARCH.md:595`; behavioral corroboration at `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:464`–`:465` | `overlay_companionsAbsentOutsideWorldStages` |
| B3-6 | The map is never searched for a free unit | §1.3 and §2.4 invariant 1 | `[D-n]` §G11.4 pre-decided rejection, `docs/design/v3/DESIGN.md:953`–`:954` | `overlay_noDynamicAllocationEver` |

### 3.3 §4.6 — the texture system (`docs/research/v1/RESEARCH.md:593`–`:599`)

| # | Contract surface | Design element | Provenance | Primary test |
|---|---|---|---|---|
| T-1 | Every texture may gain `_n`/`_s` companions | §4.1.2 companion discovery, applied per sprite and, through F5-7, to whole-asset custom textures | `[V:observed]` `docs/research/v1/RESEARCH.md:593`–`:594` | `companion_discoveryPerSprite` |
| T-2 | The blocks atlas allocates **full companion atlases with matching mip chains** | §4.1.3 — layout mirrors the base exactly; mip level count from the base | `[V:observed]` `docs/research/v1/RESEARCH.md:594`–`:595`; behavioral corroboration at `…/SHADER_ENGINE_IMPL.md:459`–`:461`. **Reference-free in Pintonium** — PD §11 keys per bound texture id, not an atlas stitch (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:631`–`:637`); rejected, `[D-P13-4]` | `companion_layoutAndMipChainMatchBase` |
| T-3 | Missing sprite → flat-normal default `0xFF7F7FFF` | §4.1.4 `DefaultFill`; the byte-order question is **recorded, not resolved** — see §11.3 item 3 and §G12.4's C-TX01 (`docs/design/v3/DESIGN.md:1088`) | `[V:observed]` `docs/research/v1/RESEARCH.md:595`; RESEARCH controls | `companion_missingNormalUsesContractDefault` |
| T-4 | Missing sprite → zero-specular default | §4.1.4 `DefaultFill(0x00000000)` | `[V:observed]` `docs/research/v1/RESEARCH.md:595`; PD's identical zero fill is corroboration only (`…/PINTONIUM_DESIGN.md:632`) | `companion_missingSpecularUsesZeroDefault` |
| T-5 | Companions are carried on units 2/3 during world rendering | §4.1.5; see B3-5 | `[V:observed]` `docs/research/v1/RESEARCH.md:595`–`:596` | covered by `overlay_companionsAbsentOutsideWorldStages` |
| T-6 | Noise is `noiseTextureResolution²`, RGB, xorshift-generated, on unit 15 | §4.2.1–§4.2.3 | `[V:observed]` `docs/research/v1/RESEARCH.md:596`–`:597`; generator behavior corroborated at `…/SHADER_ENGINE_IMPL.md:469`–`:474` | `noise_dimensionsFormatAndDeterminism` |
| T-7 | Companion sprites animate with their base sprite | §4.1.7 animation tick | `[V:observed]` implied by T-2's per-sprite companion model; the spec makes it explicit at `docs/design/v3/DESIGN.md:2482`–`:2483`. **No reference** — PD §11 states the per-bound-id approach "does not solve sprite-animation sync" (`…/PINTONIUM_DESIGN.md:634`–`:636`) | `companion_animatedSpriteFramesStayInStep` |

### 3.4 Appendix D and Appendix F.3 rows owned here

| # | Contract surface | Design element | Provenance | Primary test |
|---|---|---|---|---|
| D-1 | `atlasSize` is an `ivec2` "set while the atlas texture is bound" | §4.4 — the value source and its validity window; Phase 6 uploads | `[V:doc]` `docs/research/v1/RESEARCH.md:1367`; `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:177`. **Reference-free** — PD's notifier is a no-op TODO (`…/PINTONIUM_DESIGN.md:638`–`:639`) | `atlasSize_valueAndValidityWindow` |
| F3-1 | `noiseTextureResolution` is a const-whitelist option, default 256 | §4.2.1 consumes Phase 3's `NoiseRequirement(enabled, resolution)` (`docs/phase3/v1/PHASE_3_DOC.md:1493`), whose absent baseline is `noise disabled, resolution 256` (`:1503`) | `[V:doc]` `docs/research/v1/RESEARCH.md:1465`; `…/doc/shaders.txt:422` | `noise_resolutionFromRequirementsAndBaseline` |
| M-1 | `MC_NORMAL_MAP` is defined when the normal map is enabled | §4.1.6 publishes `CompanionMacroState`; Phase 3 emits. Blocked on §5.3 request **R1**; the ungranted fallback is stated there and is honest-absent, never falsely defined | `[V:doc]` `…/doc/shaders.txt:655`; macro-wiring reference `[V:observed — Pintonium common-shaders/.../StandardMacros]` PD §7.6 (`…/PINTONIUM_DESIGN.md:474`) | `macro_normalMapStateFollowsCompanionEnablement` |
| M-2 | `MC_SPECULAR_MAP` is defined when the specular map is enabled | §4.1.6, same mechanism | `[V:doc]` `…/doc/shaders.txt:656`; PD §7.6 | `macro_specularMapStateFollowsCompanionEnablement` |

### 3.5 Appendix E rows 10–11 and the Pintonium do-not-inherit ledger

| # | Contract surface | Design element | Provenance | Primary test |
|---|---|---|---|---|
| E-10 | `TextureMap.func_110551_a` / `func_110571_b` hook sites — companion atlas lifecycle and `atlasSize` | §4.6 rows `H13-ATLAS-01`…`03`; Forge `TextureStitchEvent.Pre/Post` preferred where sufficient, one accessor mixin where it is not | `[V:mcp]` `docs/research/v1/RESEARCH.md:1410`; deferral recorded at `docs/phase7/v1/PHASE_7_DOC.md:1244` | `hook_atlasCatalogCapturedAtStitchPost` |
| E-11 | `TextureAtlasSprite` class-level — per-sprite companion loading | §4.6 rows `H13-SPRITE-01`…`02` | `[V:mcp]` `docs/research/v1/RESEARCH.md:1411`; `docs/phase7/v1/PHASE_7_DOC.md:1245` | `hook_spriteCompanionAndAnimationRows` |
| PD-1 | PD §18 "Noise RNG: `java.util.Random(0)`" — **do not copy** | §4.2.2 specifies the contract xorshift generator; `[D-P13-3]` records the rejection and the single reopening route | `[V:observed — Pintonium common-shaders/.../targets/backed/NoiseTexture]` `…/PINTONIUM_DESIGN.md:621`–`:625`, `:815`; pre-decided at `docs/design/v3/DESIGN.md:955` | `noise_generatorMatchesContractNotRandomZero` |
| PD-2 | PD §18 "Texture units: dynamic per-program allocation" — **do not copy** | §2.4 invariant 1 | `[V:observed — Pintonium]` `…/PINTONIUM_DESIGN.md:808`; pre-decided at `docs/design/v3/DESIGN.md:953`–`:954` | covered by `overlay_noDynamicAllocationEver` |
| PD-3 | PD §7.4 gap: `texture.<stage>.<sampler>` filter/wrap "stripped and ignored" — **do not inherit** | §4.3.5 honors `.mcmeta` filter and wrap in full, and §3.6 records why the wording needed a ruling | `[V:observed — Pintonium]` `…/PINTONIUM_DESIGN.md:447`–`:448`; spec requirement at `docs/design/v3/DESIGN.md:2476`–`:2478` | `custom_mcmetaBlurSetsFilter`, `custom_mcmetaClampSetsWrap` |
| PD-4 | PD §11 `PBRTextureManager` keys per bound texture id — **do not inherit** | §4.1.3; `[D-P13-4]` | `[V:observed — Pintonium]` `…/PINTONIUM_DESIGN.md:631`–`:637` | covered by `companion_layoutAndMipChainMatchBase` |
| PD-5 | PD §11 `atlasSize` notifier is a no-op TODO — **do not inherit** | §4.4 defines a real value source with a defined validity window | `[V:observed — Pintonium]` `…/PINTONIUM_DESIGN.md:638`–`:639` | covered by `atlasSize_valueAndValidityWindow` |
| PD-6 | PD §17 B13 "all 16 colortex always allocated; no used-buffer analysis" | §4.1.1 and §4.8 apply the same scan-driven lesson to companions: the memory is not spent when the active pack cannot observe it | `[V:observed — Pintonium PackRenderTargetDirectives]` `…/PINTONIUM_DESIGN.md:799` | `companion_demandDrivenBuildSkipsUnusedAtlases` |
| PD-7 | PD §17 B10 `addDynamicSampler` overload returns `false` unconditionally | §4.3.6 makes every publication outcome a closed value that the caller must handle; no boolean return exists to be silently wrong | `[V:observed — Pintonium ProgramSamplers.java:320-323]` `…/PINTONIUM_DESIGN.md:796` | `custom_publicationOutcomeIsClosedAndTotal` |
| PD-8 | PD §11 "auto-generated `customtexN` names patched into programs" — **not adopted** | Iris-era naming, not the classic contract: App F.5's `<name>` *is* a documented sampler/unit name (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:86`), so no name is generated and no program source is patched to introduce one. The capability that row demonstrates — several textures declared for one unit — is delivered by the `.0`–`.9` discriminator plus §4.3.4's sampler-type resolution. Iris-era `customtexN` declarations are G8 modern-era work, not v0.5 | `[V:observed — Pintonium]` `…/PINTONIUM_DESIGN.md:627`–`:628`; spec REV1 checklist at `docs/design/v3/DESIGN.md:2474`–`:2476` | covered by `custom_duplicateDiscriminatorNotFoldedIntoSampler` and `custom_oneSamplerTypePerUnitPerProgram` |

### 3.6 Input contradictions found, and the binding rulings

1. **"Filter/wrap suffixes" do not exist in the contract grammar.** The Phase 13 spec's do-not-inherit
   row says `texture.<stage>.<sampler>` filter/wrap *suffixes* "are stripped and ignored there
   (PD §7.4); ours must honor them" (`docs/design/v3/DESIGN.md:2476`–`:2478`), and PD §7.4 uses the
   same phrasing (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:447`–`:448`). The shipped
   pack-author documentation is unambiguous that the key grammar has exactly one suffix and it is not
   a filter/wrap suffix: *"The suffixes \".0\" to \".9\" can be added to `<name>` to avoid duplicate
   property keys"* (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:117`), while
   *"Wrap and filter modes can be configured by adding standard texture \".mcmeta\" files"*
   (`:118`–`:119`). RESEARCH App F.5 agrees on both counts (`docs/research/v1/RESEARCH.md:1484`,
   `:1489`).
   **Ruling:** RESEARCH and the shipped contract win (§G0.1, `docs/design/v3/DESIGN.md:141`–`:143`).
   The intent of the do-not-inherit row is satisfied by honoring **both** halves of what Pintonium
   drops: the `.0`–`.9` discriminator is preserved as a discriminator and never folded into the
   sampler name (F5-2), and `.mcmeta` filter and wrap are honored in full (F5-11, F5-12). Recorded as
   `[D-P13-6]`, with §11.5 request **U1** asking for the DESIGN wording to be corrected upstream. No
   contract behavior is changed by the ruling; only the description of where filter and wrap come
   from.
2. **Phase 5's overlay key domain is narrower than App F.5's binding space.** App F.5 and the shipped
   doc allow a custom texture to be bound to any documented sampler name, including in the
   deferred/composite/final stages — the shipped example is literally
   `texture.composite.colortex1=textures/noise.png`
   (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:91`, with `:86` stating that
   `<name>` *is* the texture-unit name). Phase 5's `TextureOverlayKey` is closed as
   `NORMALS, SPECULAR, GAUX1…GAUX4, NOISE` (`docs/phase5/v1/PHASE_5_DOC.md:1879`) and its
   deferred/composite/final column carries no `Overlay` cell except unit 15 (`:1905`–`:1922`).
   **Ruling:** the contract is designed here in full (§4.3.6) and the interface shortfall is flagged,
   not invented (§G1.1, `docs/design/v3/DESIGN.md:296`–`:298`). §5.3 request **R2** asks Phase 5 to
   widen the key domain and the stage column; until it is granted, out-of-domain bindings are
   diagnosed once per publication and the affected sampler degrades at rung 2a. `[D-P13-11]`
3. **The two dependency documents whose reviews returned literal PASS carry stale closing status
   lines.** `docs/phase3/v1/PHASE_3_DOC.md:2052`–`:2053` and
   `docs/phase5/v1/PHASE_5_DOC.md:2510`–`:2511` both end
   "not verified pending a fresh whole-document review", yet
   `docs/phase3/reviews/PHASE_3_REVIEW_34.md:46`–`:47` and
   `docs/phase5/reviews/PHASE_5_REVIEW_38.md:61`–`:62` *are* those reviews and both returned literal
   `PASS` with `blocking=0; corrections=0; notes=0`.
   **Ruling:** §G1.3's definition governs — "A phase is **verified** when its latest review verdict is
   PASS" (`docs/design/v3/DESIGN.md:357`–`:359`). A literal PASS commissions no fix-up session, so no
   session was owed the footer edit. Phases 3 and 5 are verified dependencies; the footers are stale
   prose, not a verdict. Recorded so a later reviewer does not have to re-derive it. `[D-P13-13]`
4. **PD's flat-normal default disagrees with RESEARCH's.** PD records `0x7F7FFFFF`
   (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:632`); RESEARCH §4.6 specifies `0xFF7F7FFF`
   (`docs/research/v1/RESEARCH.md:595`). This is the same divergence Part I already records as
   conflict **C-TX01**, whose disposition is "RESEARCH §4.6 stays normative; representation/byte order
   remains unresolved and is never silently swapped" (`docs/design/v3/DESIGN.md:1088`).
   **Ruling:** `0xFF7F7FFF` as written in RESEARCH is the value; §4.1.4 states the channel order it
   assumes explicitly so a conformance failure localizes to that one statement rather than to the
   whole companion subsystem. `[D-P13-5]`

---

## 4. Detailed design

### 4.1 Companion `_n`/`_s` atlases

#### 4.1.1 Demand and enablement

Companion atlases are the largest memory line item this phase can add, so their construction is
demand-driven rather than unconditional — the same lesson PD §17 B13 teaches about unconditional
colortex allocation (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:799`).

`CompanionPolicy` is computed once per pipeline construction and is a *pipeline-time* value, never a
runtime-atlas-state value:

```java
public record CompanionPolicy(boolean normalsEnabled, boolean specularEnabled,
                              CompanionDemandSource source) {}
public enum CompanionDemandSource { DECLARED_SAMPLERS, ALWAYS_ON_FALLBACK, CAPABILITY_GATED_OFF }
```

- `DECLARED_SAMPLERS` — the preferred source: a companion kind is enabled when some program in the
  active configuration declares the corresponding sampler (`normals` / `specular`). Phase 3 already
  computes exactly this class of fact and publishes it for other consumers as `ResourceRequirements`
  (`docs/phase3/v1/PHASE_3_DOC.md:1491`–`:1494`), and it already carries the per-source
  `DeclaredUniformCatalog` that the fact would be derived from (`:1408`). §5.3 request **R4** asks for
  a `CompanionMapRequirement(boolean normals, boolean specular)` field on `ResourceRequirements`.
- `ALWAYS_ON_FALLBACK` — the specified behavior if R4 is not granted: build both companions whenever
  a pack is active. This is not a degradation of correctness, only of memory, and it is the cost the
  governing design already accepts ("two extra full atlases is the accepted cost, §4.8 Keep",
  `docs/design/v3/DESIGN.md:2499`–`:2500`). A failed request therefore never stalls this phase.
- `CAPABILITY_GATED_OFF` — both companions off because the capability profile cannot support the
  fixed unit map. Phase 5 already requires `maxTextureImageUnits >= 16`
  (`docs/phase5/v1/PHASE_5_DOC.md:952`); if that gate fails the pack is already off, so this value
  exists for completeness and diagnostics rather than as an independent path.

The enablement decision is deliberately **not** a function of whether the atlas build later
succeeded. Making it one would be circular: `MC_NORMAL_MAP` must be known before preprocessing, which
happens during pipeline construction, while the atlas is stitched during resource reload. §4.1.6
states the cycle-break; §6 states what happens when a build that a macro promised then fails.

#### 4.1.2 Sprite companion discovery

For each base sprite in an `AtlasDescriptor`, and for each enabled `CompanionKind`, the companion
resource identity is the base sprite's identity with `_n` or `_s` appended to the final path segment
before the extension. Discovery asks the resource layer whether that identity exists; the answer is
the only Minecraft-facing part, and it is presented to `:engine` as a plain boolean plus, when
present, the decoded pixels.

Discovery is **total**: every base sprite yields exactly one `CompanionSpriteSource` per enabled kind,
either `CompanionOrigin.Resource` or `CompanionOrigin.DefaultFill`. There is no "absent" third state,
because a companion atlas with a hole in it would leave whatever the allocator happened to leave
there, and packs sample it unconditionally.

Sprite order in the plan is the base atlas's order, which §2.3 fixes as ascending `iconName` by UTF-8
byte order, so a plan fingerprint is stable across runs and across resource-pack sets that produce the
same sprites.

#### 4.1.3 Layout and mip chain

The companion atlas is allocated at the base atlas's exact `width`, `height`, and `mipmapLevels`, and
each companion sprite is uploaded at its base sprite's exact `originX`/`originY`/`width`/`height`.
This is invariant 4 of §2.4, and it is the whole reason the design is an *atlas* rather than a
per-texture side table: the pack samples `texture`, `normals`, and `specular` with the same
interpolated UVs computed from the same vertex, so any layout divergence is a sampling error that no
amount of correct pixel data can repair.

Mip levels are generated by the same box filter the base atlas uses, level by level, so that a
companion mip level covers exactly the base texels its base mip level covers. Normals are **not**
renormalized during downsampling: renormalization would be an "improvement" to a contract-visible
component, which §G4.2 forbids (`docs/design/v3/DESIGN.md:552`–`:558`), and packs that want a
renormalized normal renormalize in the shader. This is tagged `[A]` — it is the behavior the digest
describes (`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:462`–`:463`), and T2
pixel-parity on a normal-mapped classic scene is the check that would overturn it.

The missing-sprite region of the atlas — the area no sprite occupies — is filled with the same
`DefaultFill` value as a missing sprite, so a UV that strays outside a sprite reads a defined value.

Pintonium's approach is rejected here. `PBRTextureManager` keys per *bound texture id* rather than
stitching an atlas (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:631`–`:637`)
`[V:observed — Pintonium common-shaders/.../texture/pbr/PBRTextureManager]`; PD itself records that
this "does not solve sprite-animation sync or per-sprite companions", and both are contract
requirements here. `[D-P13-4]`

#### 4.1.4 Default fills

| Kind | Packed value | Meaning |
|---|---|---|
| `NORMALS` | `0xFF7F7FFF` | flat normal — the contract value at `docs/research/v1/RESEARCH.md:595` |
| `SPECULAR` | `0x00000000` | zero specular — `docs/research/v1/RESEARCH.md:595` |

The normal default is interpreted as a packed RGBA quadruple whose components, in the order the
uploader writes them, are `(0xFF, 0x7F, 0x7F, 0xFF)` — i.e. the byte pattern is written most
significant byte first. This statement exists so a conformance failure localizes: §G12.4's C-TX01
records that "representation/byte order remains unresolved and is never silently swapped"
(`docs/design/v3/DESIGN.md:1088`), so the assumption is stated in exactly one place, tagged `[A]`,
and covered by a dedicated test (`companion_missingNormalUsesContractDefault`) that a T2 run can
contradict without disturbing anything else. RESEARCH's literal value is never edited to match an
implementation.

#### 4.1.5 Publication and stage applicability

Companion atlases are published as overlay entries under Phase 5's closed keys:

| `TextureOverlayKey` | Stages where `Present` | Every other stage |
|---|---|---|
| `NORMALS` | the gbuffers stage ids and the shadow stage id | `Absent(NOT_APPLICABLE_TO_STAGE)` |
| `SPECULAR` | the gbuffers stage ids and the shadow stage id | `Absent(NOT_APPLICABLE_TO_STAGE)` |

When a kind is disabled by `CompanionPolicy`, its entry is `Absent(NOT_CONFIGURED)` at every stage.
When the kind is enabled but the build failed or has not completed, it is
`Absent(PUBLICATION_UNAVAILABLE)`. Phase 5 turns each absence into an explicit
`MissingTextureBinding` and Phase 7 degrades the affected program rather than binding something else
(`docs/phase5/v1/PHASE_5_DOC.md:1924`–`:1931`) — the three absence reasons exist precisely so the
diagnostic says *why*.

Units 2 and 3 in the deferred/composite/final column belong to `colortex2`/`colortex3` and are
Phase-5-owned (`docs/phase5/v1/PHASE_5_DOC.md:1909`–`:1910`); Phase 13 publishes nothing there. That
is the mechanism behind contract row B3-5's "only during world rendering".

#### 4.1.6 `MC_NORMAL_MAP` / `MC_SPECULAR_MAP` wiring

```java
public record CompanionMacroState(boolean normalMap, boolean specularMap) {}
```

`CompanionMacroState` is derived from `CompanionPolicy` — enabled kind, macro defined — and is
therefore available before preprocessing, which is what breaks the circularity described in §4.1.1.
It is published on `TexturePlan` (§2.3) and consumed by Phase 3's `MacroConfiguration.optionMacros`,
which already declares that it emits "normal/specular toggles"
(`docs/phase3/v1/PHASE_3_DOC.md:979`–`:980`) without naming the input. §5.3 request **R1** asks Phase 3
to name Phase 13 as that input.

The macro-wiring shape has a working reference — PD §7.6's `StandardMacros` set defines
`MC_NORMAL_MAP`/`MC_SPECULAR_MAP` alongside the rest of the A–G identity set
(`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:474`)
`[V:observed — Pintonium common-shaders/.../gl/shader/StandardMacros]` — and the contract meaning is
the shipped doc's ("When the normal map is enabled",
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:655`–`:656`). This is a §G11.5
"reuse structure, re-derive values" adoption: the *placement* of the two macros in the standard set is
taken from the reference, the *condition* is re-derived from our own companion policy. It is not
contract-visible in the §G11.4 sense (nothing of Pintonium's behavior is adopted — only the fact that
these two names belong in the option-macro family), but the decision is recorded anyway as
`[D-P13-10]`.

If R1 is not granted, the fallback is that neither macro is defined. That is the honest-flags posture
Phase 3 already applies to FXAA ("normally absent, not falsely set",
`docs/phase3/v1/PHASE_3_DOC.md:980`): packs take their no-companion branch and render correctly
without PBR rather than sampling an undefined path. It is a real conformance shortfall, so R1 is
ranked first in §11.5 and the impl gate in §9 names it.

#### 4.1.7 Sprite animation

Animated sprites replace their atlas region every animation tick. A companion whose pixels are
uploaded once at stitch time therefore desynchronizes from its base within seconds of a pack loading —
the failure mode PD explicitly says its design does not solve
(`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:634`–`:636`).

The design hooks the atlas-wide animation tick, `TextureMap.updateAnimations()V`
(`func_94248_c`), rather than each sprite's own update, for three reasons: it is one hook instead of
one per sprite, it runs after every sprite in `listAnimatedSprites` has advanced, and it gives a
single well-defined moment at which the companion upload batch can be issued.

Per tick:

1. `CompanionAnimationTracker` holds, for each animated base sprite with at least one
   `CompanionOrigin.Resource` companion, the companion frame data decoded once at stitch time.
2. On the tick, for each such sprite, the tracker resolves the base sprite's current frame index and
   uploads the matching companion frame into the companion atlas at the sprite's origin, at every mip
   level.
3. A sprite whose companions are all `DefaultFill` is not tracked at all — its region never changes.
4. Interpolated animations (`TextureAtlasSprite.updateAnimationInterpolated()V`, `func_180599_n`) blend
   between frames. The companion is blended with the identical weight, computed from the same
   frame-counter state, so a companion is never a frame ahead of or behind its base.

Cost is bounded by the number of *animated* sprites with real companions, which in the classic matrix
is a small fraction of the atlas. §7.2 records the allocation posture; §4.8 records the memory.

If the tick hook is absent (a `FEATURE`-class hook that failed application), companion animation is
disabled and the companion atlases retain their frame-0 content — rung 2a, one diagnostic, the pack
keeps rendering (§6).

### 4.2 The noise texture

#### 4.2.1 Sizing and enablement

Resolution and enablement come from Phase 3's `ResourceRequirements.noise`, a
`NoiseRequirement(boolean enabled, int resolution)` (`docs/phase3/v1/PHASE_3_DOC.md:1493`) whose
absent-directive baseline is disabled with resolution 256 (`:1503`). The texture is
`resolution × resolution`, internal format RGB, unsigned-byte transfer, wrap `REPEAT`, filter
`LINEAR` — the contract's noise sampling is a repeating field sampled with interpolation
(`docs/research/v1/RESEARCH.md:596`–`:597`; behavioral corroboration at
`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:469`–`:471`).

#### 4.2.2 The generator, specified so it is reproducible

The spec requires the generator to be specified so it is reproducible
(`docs/design/v3/DESIGN.md:2461`–`:2462`). It is defined as pure arithmetic over 32-bit signed
integers, in `:engine`, with no platform, locale, or iteration-order dependence:

```
xorshift(s):            s ^= s << 13
                        s ^= s >>> 17
                        s ^= s << 5
                        return s

channelSeed(x, y, c):   a = xorshift(x)
                        b = xorshift(y * 19)
                        d = xorshift(c * 23)
                        return (a + b) * d - c

byteAt(x, y, c):        return |xorshift(channelSeed(x, y, c))| mod 128
```

Pixels are produced in row-major order, `y` outer and `x` inner, channels in R, G, B order, so the
resulting byte buffer is fully determined by `resolution` alone. `byteAt` returns 0…127; the
resulting field is a repeatable low-amplitude noise field, which is what packs sample it for.

This is a *behavioral* restatement, permitted by §G7 item 2: it is arithmetic, and it contains no
identifier, class name, or code structure from the decompile. It matches the recurrence and the
per-channel decorrelation the digest describes
(`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:471`–`:474`) and RESEARCH's
"xorshift-generated" (`docs/research/v1/RESEARCH.md:596`) `[V:observed]`. The two derived details —
the exact channel-decorrelation expression and the `mod 128` amplitude — are tagged `[A]`: they are
single-source behavioral observations, and `noise_generatorMatchesContractNotRandomZero` plus a T2
run on a noise-sampling classic pack is what would overturn either.

#### 4.2.3 The rejected alternative, and the one route back

Pintonium seeds `java.util.Random(0)` and takes its bytes
(`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:621`–`:622`)
`[V:observed — Pintonium common-shaders/.../targets/backed/NoiseTexture]`. That is a **pre-decided
rejection** in §G11.4's standing list — "`Random(0)` noise (ours: contract generator)"
(`docs/design/v3/DESIGN.md:955`) — and it is not revisited here. `[D-P13-3]`

PD itself frames the open question correctly: if packs depend on OF's exact noise values the reference
is wrong, and if they only need *a* repeatable field either works (`…/PINTONIUM_DESIGN.md:622`–`:625`).
The Phase 13 spec states the single route back: the contract question is reopened only through the
§G0.1 conflict rule, "never silently" (`docs/design/v3/DESIGN.md:2464`–`:2466`). Concretely, that route
is a T2 pixel-parity failure on a noise-sampling classic pack that a byte-identical generator fixes —
evidence that would be reported as a RESEARCH conflict, not resolved inside an implementation. This
design does not take that route and does not need to: it specifies the contract generator.

#### 4.2.4 The pack override

`NoiseTextureSpec.Override(image, sidecar)` (`docs/phase3/v1/PHASE_3_DOC.md:1461`–`:1463`) replaces the
generated texture entirely: the pack image is decoded, uploaded, and parameterized from its own
`.mcmeta` sidecar if present (§4.3.5), otherwise with the same `REPEAT`/`LINEAR` defaults as the
generated texture. `noiseTextureResolution` does not resize an override; the image's own dimensions
win, because the pack authored it. An override that fails to decode falls back to the generated
texture and diagnoses once — a pack that asked for noise still gets noise.

#### 4.2.5 Publication

Noise is published under `TextureOverlayKey.NOISE` as `Present(handle)` at **every** stage, matching
Phase 5's table (`docs/phase5/v1/PHASE_5_DOC.md:1922`) and the shipped unit tables, which list
`noisetex` at unit 15 in the gbuffers, shadow, and composite/deferred sets
(`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:286`, `:304`, `:325`). When noise is disabled
the entry is `Absent(NOT_CONFIGURED)` at every stage, which Phase 5 renders as a
`MissingTextureBinding` with no substituted object (`docs/phase5/v1/PHASE_5_DOC.md:1928`–`:1931`).

### 4.3 Pack custom textures

#### 4.3.1 From key to binding

Phase 13 consumes Phase 3's parsed algebra unchanged and never reparses. `TextureBindingKey` is
`(stage, sampler, duplicateDiscriminator)` with the stage expansion already fixed by Phase 3:
`GBUFFERS` targets gbuffers **and shadow** programs, `DEFERRED` targets deferred, `COMPOSITE` targets
composite **and final** (`docs/phase3/v1/PHASE_3_DOC.md:1446`–`:1447`). Rows F5-13…F5-15 are satisfied
by consuming that expansion rather than restating it.

The `sampler` string is resolved to a `TextureOverlayKey` through the documented name tables — the
gbuffers/shadow set and the composite/deferred set of `doc/shaders.txt` (`:272`–`:326`) — using the
pack-facing names verbatim per §G4.1. `gaux1`…`gaux4` map to `GAUX1`…`GAUX4`; `noisetex` maps to
`NOISE`; `normals` and `specular` map to `NORMALS`/`SPECULAR`. Names that resolve to a unit outside
Phase 5's current key domain take §4.3.6's path.

The `.0`–`.9` discriminator does **not** select a unit and is never appended to the sampler name
(F5-2). It exists so a pack can declare several differently-typed textures for one sampler without
duplicate property keys (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:113`, `:117`);
§4.3.4 is what actually chooses among them.

#### 4.3.2 The three source forms

| Form | Phase 3 variant | Phase 13 handling |
|---|---|---|
| Pack-relative PNG | `PackPath(key, image, sidecar)` | decode through the pack's own byte source, upload as a 2D texture, parameterize from the sidecar |
| `minecraft:` asset | `MinecraftResource(key, resourceIdentity)` | resolve the identity through the resource layer, decode, upload as a 2D texture |
| `minecraft:` dynamic / atlas | same variant, dynamic identity | resolve to the **live** vanilla texture object and publish its handle; never copy it |
| Raw binary | `Raw(key, bytes, target, internalFormat, dimensions, pixelFormat, pixelType, sidecar)` | §4.3.3 |

The dynamic sub-branch is the one that must not be flattened into "decode a PNG". `dynamic/lightmap_1`
and the atlas paths name objects vanilla owns and mutates every frame
(`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:98`–`:99`), so the correct behavior is to
publish the live handle and let the pack sample whatever vanilla currently has there. Those handles are
**foreign**: Phase 13 binds and labels them, and never allocates into, copies into, or deletes them —
the same permission class Phase 1 defines for foreign textures and Phase 5 consumes
(`docs/phase5/v1/PHASE_5_DOC.md:2033`).

`_n`/`_s` on an asset or atlas path selects the companion variant
(`…/doc/shaders.properties:100`–`:101`). For an atlas path this resolves to §4.1's companion atlas for
that atlas; for an ordinary asset path it resolves to the companion asset if it exists, and otherwise to
a 1×1 texture of the appropriate `DefaultFill` — so a pack that asks for a normal variant of something
that has none samples a flat normal rather than an undefined object.

#### 4.3.3 The raw form

Phase 3 has already validated the token grammar, the closed target/format domains, dimension arity, and
integer-transfer compatibility (`docs/phase3/v1/PHASE_3_DOC.md:1452`–`:1457`). Phase 13 adds exactly the
checks Phase 3 could not make without opening the file:

1. the byte length equals the product of the dimensions and the transfer element size for the declared
   `pixelFormat`/`pixelType`;
2. every dimension is within the capability profile's limit for the declared target;
3. the internal format is supported by the profile, or falls back exactly as Phase 5's format policy
   dictates for the same App B.4 vocabulary (`docs/phase5/v1/PHASE_5_DOC.md:2020`).

`TextureUploadSpec` is a closed sum over the four targets, carrying the dimensions each target actually
uses (1, 2, 3, and 2 respectively). A failure at any of the three checks yields a closed
`TextureFailure` for that entry only; the rest of the publication proceeds (§6).

#### 4.3.4 Sampler-type disambiguation

The contract allows several texture *types* on one unit and requires that a single program use only
one type per unit (`docs/research/v1/RESEARCH.md:1488`–`:1489`;
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:114`–`:116`).

Phase 3 states explicitly that sampler type is not part of `TextureBindingKey` and that "Phases 4/13
derive it from each program's sampler declarations when validating shared texture units"
(`docs/phase3/v1/PHASE_3_DOC.md:1468`–`:1469`). Phase 13 therefore builds, per program, the map
`unit → SamplerType` from the declared-uniform data that Phase 3 publishes per source and Phase 4 merges
per linked program (`docs/phase3/v1/PHASE_3_DOC.md:1408`), and selects, for each unit that program
samples, the candidate entry whose `TextureTarget` matches the declared sampler type:

```java
public enum SamplerType { SAMPLER_1D, SAMPLER_2D, SAMPLER_3D, SAMPLER_2D_RECT }
```

- Exactly one candidate matches → that entry is the program's binding for the unit.
- No candidate matches → the unit has no custom binding for that program; the underlying object
  (companion, colortex, or the documented default) is what Phase 5 resolves. Diagnosed once.
- More than one candidate matches → the last valid declaration wins, matching Phase 3's uniform
  last-valid-wins rule for exact-key properties (`docs/phase3/v1/PHASE_3_DOC.md:1413`), and diagnosed
  once.
- Two *different* declared types for the same unit in one program is a pack error, not an engine
  choice: the program is diagnosed and the unit binds nothing for it.

Because the resolution is per program, `SamplerType` never leaks into the overlay publication, which
stays keyed by `(StageId, TextureOverlayKey)` exactly as Phase 5 defines it.

#### 4.3.5 `.mcmeta` filter and wrap

`TextureSidecarRef` is the normalized `<image-or-bytes>.mcmeta` path that Phase 3 retains without
opening; Phase 3's conformance row states plainly that "filter/wrap information is not stripped"
(`docs/phase3/v1/PHASE_3_DOC.md:740`). Phase 13 opens it:

| Sidecar field | Absent | `false` | `true` |
|---|---|---|---|
| `texture.blur` | `NEAREST` | `NEAREST` | `LINEAR` |
| `texture.clamp` | `REPEAT` | `REPEAT` | `CLAMP_TO_EDGE` |

```java
public record TextureParameterSpec(MinFilter minFilter, MagFilter magFilter, WrapMode wrap) {}
```

The parameters are applied at upload time and are part of the entry's fingerprint. A malformed or
unreadable sidecar yields the defaults plus one diagnostic; it never fails the texture, because a pack
whose sidecar has a typo should still get its texture.

This row is the direct answer to the do-not-inherit item: Pintonium strips and ignores this
information (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:447`–`:448`), and we honor it. §3.6
item 1 records why the spec's "suffix" wording needed a ruling to get here.

#### 4.3.6 Bindings outside Phase 5's current key domain

`TextureOverlayKey` reaches `NORMALS`, `SPECULAR`, `GAUX1`…`GAUX4`, and `NOISE`
(`docs/phase5/v1/PHASE_5_DOC.md:1879`), and the deferred/composite/final column of the unit table has no
`Overlay` cell except unit 15 (`:1905`–`:1922`). App F.5 is broader (§3.6 item 2).

The design is complete on this side of the seam and the shortfall is isolated to one closed value:

```java
public record UnsupportedBinding(TextureBindingKey key, TextureOverlayKey wouldBe,
                                 StageId stage, UnsupportedReason reason) {}
public enum UnsupportedReason { KEY_DOMAIN, STAGE_COLUMN }
```

Every custom-texture entry that resolves to a documented sampler name but cannot be published under the
current Phase 5 domain becomes an `UnsupportedBinding` on the plan. It is diagnosed once per publication
through `schmaloogium.textures`, it is visible in the `resources.*`-style diagnostics, and the affected
sampler simply retains whatever object Phase 5's table already resolves for it — which for a colortex
name is the correct buffer, so the program still renders, just without the pack's override. That is
rung 2a: a feature disabled, the program alive (`docs/design/v3/DESIGN.md:427`–`:434`).

Nothing here invents the wider interface. §5.3 request **R2** asks for it, and §12 item 12 is the
one-line follow-through once it is granted. `[D-P13-11]`

### 4.4 `atlasSize`

`atlasSize` is an `ivec2` that the contract says is "set while the atlas texture is bound"
(`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:177`; App D.3 row at
`docs/research/v1/RESEARCH.md:1367`). Phase 13 owns the *value*; Phase 6 owns the upload
(`docs/design/v3/DESIGN.md:2479`–`:2481`).

```java
public sealed interface AtlasSizeResult {
    record Known(int width, int height) implements AtlasSizeResult {}
    record Unknown() implements AtlasSizeResult {}
}
```

- The value is the base atlas's `width`/`height` from its `AtlasDescriptor` — the same numbers the
  companion atlases are allocated at, so `atlasSize` and the companion layout can never disagree.
- It becomes `Known` at the moment the atlas catalog is captured (§4.6 row `H13-ATLAS-02`) and reverts
  to `Unknown` when the catalog is invalidated by a resource reload.
- The **validity window** is the contract's own: the value is meaningful only for programs drawing
  with the atlas bound, i.e. the gbuffers and shadow stages. Phase 13 exposes the value and the window;
  Phase 6 decides the upload cadence for a uniform whose value is stage-conditional, exactly as it does
  for every other App D row.
- `Unknown` before the first stitch is a real state, not an error: a pack can be loaded before any
  atlas exists. Phase 6 applies its own last-valid/default policy for an unavailable value; Phase 13
  reports rather than inventing a number.

There is no reference for this row — PD's notifier is a no-op with a TODO debating the semantics
(`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:638`–`:639`)
`[V:observed — Pintonium common-shaders/.../uniforms/]` — so it is designed from App D alone, as the
spec directs (`docs/design/v3/DESIGN.md:2480`–`:2481`).

### 4.5 The overlay publication and its lifecycle

#### 4.5.1 The publication object

```java
public record TexturePublication(
    TextureOverlayPublicationId id,      // (estateGeneration, contentFingerprint)
    RegistryFingerprint registryFingerprint,
    TexturePlan plan,
    OverlayTable table) {}               // (StageId, TextureOverlayKey) -> TextureOverlayEntry, total
```

`TextureOverlayFingerprint` is described by Phase 5 as "an opaque Phase-13 value"
(`docs/phase5/v1/PHASE_5_DOC.md:1885`). It is computed over the canonical encoding of the whole
`TexturePlan`: companion plans in atlas-then-kind order, the noise plan, custom entries in Phase 3's
already-fixed ordering (`docs/phase3/v1/PHASE_3_DOC.md:1463`–`:1467`), the unsupported list, and the
macro state. Two publications with equal fingerprints are interchangeable; a fingerprint never depends
on a GL name, an insertion order, or a hash-map iteration order.

`registryFingerprint` is copied from the Phase 5 estate this publication pairs with, because Phase 5
requires the lease's fingerprint to equal the estate's before it will bind
(`docs/phase5/v1/PHASE_5_DOC.md:1889`–`:1891`).

#### 4.5.2 The lease protocol, from Phase 13's side

Phase 5 and Phase 7 define the protocol; Phase 13's obligations within it are:

1. `lease()` returns a `TextureOverlayLease extends TextureOverlaySnapshot, AutoCloseable` — an
   immutable snapshot view over the current publication. It borrows; it never copies handles.
2. `lease()` and `publicationId()` read the same publication atomically. Phase 7 passes both to
   `textureBindings`, and Phase 5 checks the id first, then the registry fingerprint
   (`docs/phase5/v1/PHASE_5_DOC.md:1893`–`:1897`).
3. On `Rejected`, ownership never transferred, and Phase 7 closes the still-caller-owned lease. Phase 13
   treats a rejection as a normal outcome: it means a replacement publication has already happened, and
   the next frame acquires a fresh lease.
4. On `Bound`, ownership moves into the `TextureBindingSnapshot`, which Phase 7 closes in the enclosing
   draw's `finally`. That closure "is Phase 13's deterministic permission to destroy referenced
   handles" (`docs/phase5/v1/PHASE_5_DOC.md:1900`–`:1903`).
5. Phase 13 keeps a lease count per publication. A superseded publication moves to `RETIRING` and its
   handles are destroyed only when its count reaches zero. This is the whole reason destruction is
   never immediate.

#### 4.5.3 Publication state machine

```
ABSENT ──plan──▶ PLANNED ──build──▶ READY ──supersede──▶ RETIRING ──leases==0──▶ CLOSED
   ▲                 │                 │                                             │
   └── build failure ┘                 └── close() ─────────────────────────────────▶┘
```

- `plan` is pure and may run off the render thread; `build`, `supersede`, and `close` are
  render-thread-only.
- A build failure closes any partially created object and leaves the previous `READY` publication
  installed — there is no partial publication, matching Phase 5's discipline.
- `close()` is idempotent. Teardown order is: stop accepting new leases; wait for outstanding leases to
  close; delete owned companion atlases; delete the owned noise texture; delete owned custom textures in
  reverse creation order; drop, never delete, every foreign handle (the dynamic/live textures of
  §4.3.2). The recording backend must prove `noLeakedObjects()` and `noUseAfterDelete()`, exactly as
  Phase 5 requires of itself (`docs/phase5/v1/PHASE_5_DOC.md:1994`).

### 4.6 Hook catalog — App E rows 10 and 11

Written in the Phase 7 catalog format (`docs/phase7/v1/PHASE_7_DOC.md:1075`–`:1098`), whose health
classes are `CORE` (missing → disable the shader group for the session, rung 3), `FEATURE` (missing →
disable that feature only, rung 2a), and `OBSERVER`. All Phase 13 mixins use `require = 0`,
normally `expect = 1`, and are audited by Phase 7's Mixin configuration plugin against this catalog,
exactly like every other phase's rows.

Phase 7 records a preference for the Forge stitch events "where sufficient"
(`docs/phase7/v1/PHASE_7_DOC.md:1269`). They are sufficient for **timing** and insufficient for
**data**: `TextureStitchEvent` exposes only `getMap()` `[V:mcp]`, while the stitched sprite table and
the mip level count are the private fields `mapUploadedSprites` (`field_94252_e`),
`listAnimatedSprites` (`field_94258_i`), and `mipmapLevels` (`field_147636_j`) `[V:mcp]`. The ruling is
therefore: **Forge events for every moment, one accessor-only mixin for the data**. `[D-P13-7]`

| ID / class | SRG target and injection | Dumb bridge call | Health / evidence |
|---|---|---|---|
| `H13-ATLAS-01` `TextureMap` | Forge `TextureStitchEvent.Pre` (no mixin) | `TextureHooks.onAtlasStitchBegin(atlasIdentity)` | `FEATURE`; invalidates the catalog and the `atlasSize` value before vanilla restitches. App E row 10 (`docs/research/v1/RESEARCH.md:1410`) |
| `H13-ATLAS-02` `TextureMap` | Forge `TextureStitchEvent.Post` (no mixin) | `TextureHooks.onAtlasStitched(atlasDescriptor)` | `FEATURE`; the only moment the full sprite table exists and is final; builds the `AtlasCatalog`, sets `atlasSize` `Known`, and schedules the companion build. App E row 10 |
| `H13-ATLAS-03` `TextureMap` | `@Accessor` on `field_94252_e`, `field_94258_i`, `field_147636_j` | none — read-only accessors used by `H13-ATLAS-02`'s handler | `FEATURE`; accessor-only, zero policy, no injection. Needed because `TextureStitchEvent` exposes only `getMap()` `[V:mcp]` |
| `H13-ATLAS-04` `TextureMap` | `func_94248_c()V` (`updateAnimations`) TAIL | `TextureHooks.onAtlasAnimationsUpdated(atlasIdentity)` | `FEATURE`; the companion animation tick of §4.1.7; runs after every animated sprite has advanced |
| `H13-SPRITE-01` `TextureAtlasSprite` | read-only via `H13-ATLAS-03`'s sprite table: `func_94215_i` (`getIconName`), `func_130010_a`/`func_110967_i` (origin), `func_94211_a`/`func_94216_b` (extent), `func_110970_k` (`getFrameCount`), `func_130098_m` (`hasAnimationMetadata`) | none — public methods called from the `H13-ATLAS-02` handler | `FEATURE`; **no mixin is needed** for row 11's per-sprite data, because 1.12.2 exposes it publicly `[V:mcp]`. App E row 11 (`docs/research/v1/RESEARCH.md:1411`) |
| `H13-SPRITE-02` `TextureAtlasSprite` | `func_94219_l()V` (`updateAnimation`) and `func_180599_n()V` (`updateAnimationInterpolated`) — **catalogued, not injected at v0.5** | none | `FEATURE`, dormant; `H13-ATLAS-04` is the chosen granularity (§4.1.7). Recorded so a later session does not mistake absence for omission, exactly as Phase 7's ledger does (`docs/phase7/v1/PHASE_7_DOC.md:1229`–`:1231`) |

Base-atlas handle acquisition uses Phase 1's foreign-texture provider — the same route by which Phase 5
resolves unit 0's `Foreign("texture")` (`docs/phase5/v1/PHASE_5_DOC.md:1907`) — rather than reading
`AbstractTexture.func_110552_b` (`getGlTextureId`) directly, so no GL name crosses the C-1 seam.

**Completeness statement.** App E's class catalog spans `docs/research/v1/RESEARCH.md:1396`–`:1418`;
Phase 7's ledger dispositions all eighteen rows and assigns exactly rows 10 and 11 to this phase
(`docs/phase7/v1/PHASE_7_DOC.md:1244`–`:1245`). Both are specified above. Phase 13 claims no other App E
row.

### 4.7 Lifecycle: pack, option, resource, and resize transitions

Phase 7 owns the reload machinery; Phase 13 reacts to it. `ReloadReason` is closed as
`PACK_SELECTION, OPTION_CHANGE, RESOURCE_RELOAD, DIMENSION_CHANGE, RESOLUTION_MULTIPLIER, …`
(`docs/phase7/v1/PHASE_7_DOC.md:1564`–`:1566`).

| Transition | Trigger | Phase 13 action |
|---|---|---|
| Pack selected or replaced | `PACK_SELECTION` | plan from the new `PackConfiguration`; build; supersede. Companion plans usually survive unchanged (they depend on the atlas, not the pack) — only `CompanionPolicy` and the custom/noise sets change |
| Option changed | `OPTION_CHANGE` | replan; if the new plan's fingerprint equals the current publication's, keep the publication and issue no GL |
| Resource reload | `RESOURCE_RELOAD` | invalidate the atlas catalog and `atlasSize` at `H13-ATLAS-01`; rebuild companions at `H13-ATLAS-02`; re-resolve every `minecraft:` custom texture, because their identities may now resolve to different objects |
| Dimension change | `DIMENSION_CHANGE` | replan: the dimension's `shaders.properties` may declare a different custom-texture set |
| Estate resize | Phase 5 `BufferResizeNotice` | §4.7.1 |
| World unload / shaders off | Phase 7 teardown | `close()`; §4.5.3's teardown order |

Order matters in exactly one place: a `RESOURCE_RELOAD` rebuilds Phase 3 and Phase 9 together
(`docs/phase7/v1/PHASE_7_DOC.md:1034`) *and* restitches the atlas. Phase 13 must not publish a companion
atlas built from the old sprite table paired with a new `PackConfiguration`. The publication fingerprint
is what prevents it: the catalog is part of the plan, so a stale catalog produces a different
fingerprint and the publication is superseded rather than reused.

#### 4.7.1 Resize participation

Phase 13 registers as a Phase 5 resize consumer through
`BufferEstatePublisher.addResizeConsumer(String consumerId, BufferResizeConsumer consumer, …)`
(`docs/phase5/v1/PHASE_5_DOC.md:1807`), implementing
`ResizeConsumerResult resize(BufferResizeNotice notice)` on the render thread without throwing
(`:617`–`:621`). Phase 5's §5.1 names Phase 13 as one of the two intended consumers (`:2018`).

Most Phase 13 objects are size-independent: companion atlases follow the *atlas*, and the noise texture
follows `noiseTextureResolution`. Neither follows the display extent. The consumer therefore:

- returns `SUCCESS` without GL work for `DISPLAY_EXTENT`, `RENDER_QUALITY`, `MAIN_DEPTH_EXTENT`,
  `SHADOW_RESOLUTION`, and `SHADOW_QUALITY`;
- re-derives its publication for `PACK_CONFIGURATION`, `REGISTRY_PLAN`, and
  `COLOR_INVENTORY_OR_FORMAT`, because the new estate carries a new `registryFingerprint` and the old
  publication would be rejected at the next bind;
- returns `FAILED` only when re-deriving the publication fails, which Phase 5 turns into an off
  publication (`:2018`).

Registering also keeps the publication's `registryFingerprint` correct by construction: it is refreshed
at exactly the moment the estate's changes.

### 4.8 Memory posture

The accepted cost is stated by the governing design: "two extra full atlases is the accepted cost,
§4.8 Keep" (`docs/design/v3/DESIGN.md:2499`–`:2500`). This design accepts it and bounds it.

```java
public record TextureMemoryEstimate(long companionBytes, long noiseBytes, long customBytes) {}
```

- **Companions.** Two atlases at the base atlas's extent and mip chain. A mipped RGBA atlas costs
  approximately 4/3 of its base level, so both companions together cost approximately
  `2 × (4/3) × width × height × 4` bytes. §4.1.1's demand-driven enablement is what keeps this from
  being spent when the active pack declares neither sampler.
- **Noise.** `resolution² × 3` bytes, 192 KiB at the default 256 — negligible.
- **Custom textures.** Pack-controlled and unbounded in principle. Every entry is counted in the
  estimate, the total is logged once per publication, and the raw form's dimension checks (§4.3.3)
  are what stop a malformed declaration from attempting an absurd allocation.

The estimate is planning data, not a limit: nothing here refuses a texture for being large. It exists so
the diagnostic is available when a user reports memory pressure, and so §8's tests can assert that a
disabled companion kind allocates nothing.

---

## 5. Cross-phase interfaces

### 5.1 Interfaces exposed by Phase 13

| Exposed contract | Content | Consumer(s) |
|---|---|---|
| `TextureSystem` (`plan`/`build`/`lease`/`publicationId`/`atlasSize`/`close`) | the phase entry point of §2.2; pure planning, render-thread build, atomic publication, idempotent close | Phase 7 composition and teardown |
| `TextureOverlayLease` + `TextureOverlayPublicationId` | Phase 5's types, produced here; read atomically, borrowed never copied, closed by whoever Phase 5's protocol says owns them | Phase 7, which passes both to Phase 5 `textureBindings` |
| `OverlayTable` — total `(StageId, TextureOverlayKey) → TextureOverlayEntry` | `Present(TextureHandle)` or `Absent(NOT_CONFIGURED \| NOT_APPLICABLE_TO_STAGE \| PUBLICATION_UNAVAILABLE)`; total over both domains, no null, no default-through | Phase 5's table resolution |
| `TextureOverlayFingerprint` | the opaque Phase-13 value Phase 5's publication id carries; canonical over the whole `TexturePlan`, never over a GL name or iteration order | Phase 5 validation; Phase 7 publication pairing |
| `AtlasSizeResult` — `Known(width, height)` \| `Unknown` | the App D `atlasSize` value source and its validity window (§4.4); the upload is Phase 6's | Phase 6 through Phase 7's composition |
| `CompanionMacroState(normalMap, specularMap)` | the pipeline-time booleans behind `MC_NORMAL_MAP`/`MC_SPECULAR_MAP`; known before preprocessing by construction (§4.1.6) | Phase 3's `MacroConfiguration`, pending request R1 |
| `BufferResizeConsumer` implementation, registered under a stable consumer id | render-thread, non-throwing, closed `SUCCESS`/`FAILED` (§4.7.1) | Phase 5's resize dispatch |
| `TexturePlan`, `TextureMemoryEstimate`, `UnsupportedBinding`, `TextureFailure` | immutable, handle-free planning and diagnostic projections | Phase 2 harness; Phase 14 profiling |
| The `H13-*` hook rows of §4.6 | the two App E rows Phase 7 deferred, in Phase 7's catalog format and audited by its plugin | Phase 7's application audit |

Nothing above exposes a GL name, a Minecraft type, or a mutable object. `TextureHandle` is Phase 1's
opaque handle type, minted and owned here and borrowed by Phase 5's binding snapshot.

### 5.2 Dependency contracts consumed

#### Phase 3 — verified (`docs/phase3/reviews/PHASE_3_REVIEW_34.md:46`–`:47`, literal `PASS`)

| Consumed contract | Use |
|---|---|
| `PackConfiguration` and its fingerprint (`docs/phase3/v1/PHASE_3_DOC.md:1405`) | sole pack truth and replan identity |
| `CustomTextureSpec` / `TextureBindingKey` / `TextureSidecarRef` (`:1443`–`:1459`) | §4.3 in full; consumed unchanged, never reparsed |
| `NoiseTextureSpec` — `Generated` \| `Override` (`:1461`–`:1463`) | §4.2.4 |
| `ResourceRequirements.noise` = `NoiseRequirement(enabled, resolution)` (`:1493`), absent baseline at `:1503` | §4.2.1 |
| The custom-spec ordering rule (`:1463`–`:1467`) | canonical fingerprint input, §4.5.1 |
| "Phases 4/13 derive [sampler type] from each program's sampler declarations" (`:1468`–`:1469`) | §4.3.4 — this is the explicit grant that makes §4.3.4 a consumption rather than an invention |
| `DeclaredUniformCatalog` per source (`:1408`), merged per linked program by Phase 4 | §4.3.4's `unit → SamplerType` map, and R4's preferred demand source |
| `MacroConfiguration.optionMacros` (`:947`–`:957`, `:979`–`:980`) | §4.1.6, pending R1 |
| Phase 3 owns `.mcmeta` *retention*, Phase 13 owns its *interpretation* (`:300`, `:740`) | §4.3.5 |

#### Phase 5 — verified (`docs/phase5/reviews/PHASE_5_REVIEW_38.md:61`–`:62`, literal `PASS`)

| Consumed contract | Use |
|---|---|
| `TextureOverlayLease` / `TextureOverlaySnapshot` / `TextureOverlayPublicationId` / `TextureOverlayFingerprint` / `TextureOverlayKey` / `TextureOverlayEntry` / `TextureOverlayAbsence` (`docs/phase5/v1/PHASE_5_DOC.md:1879`–`:1885`, `:2017`) | §4.5 in full — the publication shape is Phase 5's, not ours |
| The lease/rejection/ownership-transfer protocol (`:1887`–`:1903`) | §4.5.2; closure is our permission to destroy |
| The fixed App B.3 unit table (`:1905`–`:1922`) and its `Overlay` cells | §4.1.5, §4.2.5, §4.3.1 — which keys exist and where |
| Unit-15 absence semantics (`:1928`–`:1931`) | §4.2.5 |
| `BufferEstateView` and its exposure to Phase 13 (`:2004`) | estate generation and `registryFingerprint` for the publication id |
| `ColorInternalFormat` / `PixelFormat` / `PixelType` vocabulary, named for the "Phase 13 raw-upload adapter" (`:2020`) | §4.3.3 |
| `BufferResizeNotice` / `BufferResizeConsumer` / `ResizeConsumerResult` (`:617`–`:621`, `:1807`, `:2018`) | §4.7.1 |
| "Phase 13 retains registry, allocation, upload, ownership, and destruction" (`:1902`–`:1903`) | the ownership boundary this phase implements |

#### Phase 7 — **not verified; consumed provisionally** (`docs/phase7/reviews/PHASE_7_REVIEW_32.md:299`–`:301`)

| Consumed contract | Use |
|---|---|
| The §4.10.1 hook-catalog format and health classes (`docs/phase7/v1/PHASE_7_DOC.md:1075`–`:1098`) | §4.6's table shape and `require = 0` / `expect = 1` posture |
| The App E deferral ledger rows 10–11 (`:1244`–`:1245`) | §4.6's scope — exactly two rows, no more |
| The atlas event preference (`:1269`) | §4.6's `[D-P13-7]` ruling |
| Executor step 5: the overlay "is leased and merged only through Phase 5's published `textureBindings` contract" (`:931`–`:933`) | §4.5.2 |
| `ReloadReason` and `ShaderReloadController` (`:1564`–`:1568`, `:1661`) | §4.7's transition table |
| The Phase 13 hand-off: "implement overlay/atlas leases behind Phase 5 and the deferred event/mixin rows" (`:2120`) | the scope statement this document answers |

### 5.3 Requested changes to dependency contracts — flagged, never assumed

None of these is assumed to exist. Each names its ungranted fallback, and every fallback is already
specified in §4, so no request is on the critical path.

| # | Target | Request | Ungranted fallback |
|---|---|---|---|
| **R1** | Phase 3 | Name Phase 13's `CompanionMacroState` as the input driving `MacroConfiguration.optionMacros`' normal/specular toggles. Phase 3 already emits the toggles (`docs/phase3/v1/PHASE_3_DOC.md:979`–`:980`) without naming their source; this is plumbing, not a new feature | Neither macro is defined — Phase 3's honest-absent posture (`:980`). Packs take their no-companion branch and render correctly without PBR. A real conformance shortfall; ranked first in §11.5 |
| **R2** | Phase 5 | Widen `TextureOverlayKey` beyond `NORMALS/SPECULAR/GAUX1…GAUX4/NOISE`, and add `Overlay` cells to the deferred/composite/final column, so App F.5's full binding space is reachable (§3.6 item 2) | §4.3.6: out-of-domain bindings become `UnsupportedBinding`, are diagnosed once, and the sampler keeps Phase 5's existing object. Rung 2a |
| **R3** | Phase 1 | Allocate `com.schmaloogium.engine.textures`, `com.schmaloogium.mod.glue.textures`, and `com.schmaloogium.mod.mixin.textures`, following the granted Phase 7/8 trios (`docs/phase1/v14/PHASE_1_DOC.md:1537`, `:1551`, `:1554`) | `com.schmaloogium.mod.mixin` is already allocated to Phase 13 (`:1553`), so the mixins have a home; only the engine/glue package *names* are pending, and no contract depends on them |
| **R4** | Phase 3 | Add `CompanionMapRequirement(boolean normals, boolean specular)` to `ResourceRequirements`, derived from the declared-sampler data Phase 3 already computes (`docs/phase3/v1/PHASE_3_DOC.md:1408`) | §4.1.1's `ALWAYS_ON_FALLBACK`: build both companions whenever a pack is active. Costs memory, not correctness, and it is the cost the design already accepts (`docs/design/v3/DESIGN.md:2499`–`:2500`) |

### 5.4 Phase 7 provisional-contract disclosure

Every row in §5.2's Phase 7 table is provisional, for the reason recorded in §0.2 item 1. The
disclosure is deliberately narrow so a fix-up can be scoped rather than exploratory:

- **What is consumed:** a table shape and health-class vocabulary (§4.6), a two-row scope assignment,
  an event preference, one sentence about how the lease reaches Phase 5, and a closed reload-reason
  enum.
- **What is not consumed:** nothing from `FrameHookSink`, `UniformSignal`, `BlendStateValue`,
  `AnaglyphEye`, or any frame-transaction type — which is precisely the surface round 32 changed.
- **If round 33 contradicts a row:** Phase 13 owes a §G1.3 fix-up session recording the correction
  under that review's `## Resolutions` and adding a `§0.<K>` addendum here. A rebuild is not owed,
  because no structural decision in §4 rests on a Phase 7 type — §4.6's rows would be reformatted, and
  §4.7's table would be re-keyed, and nothing else would move.

### 5.5 Downstream hand-offs

| Phase | Contract handed onward |
|---|---|
| 14 | Sampler objects replacing per-bind parameterization, and asynchronous/PBO upload for the companion animation batch and the raw custom-texture path, are Phase 14's (`docs/design/v3/DESIGN.md:1686`–`:1687`). §4.5.3's teardown order and §4.7.1's resize participation are the seams to preserve; §7.2 names the hot path worth measuring first |
| G8 | labPBR channel semantics remain a pack-side convention that G8 advertises (`docs/design/v3/DESIGN.md:2486`). This phase delivers bytes and interprets none of them, so an advertisement changes no Phase 13 code |
| 2 | `TexturePlan`, `TextureMemoryEstimate`, and `UnsupportedBinding` are handle-free and are the manifest surface a golden run can assert over without a GL context |

---

## 6. Failure modes & degradation

The §G2.4 ladder (`docs/design/v3/DESIGN.md:419`–`:449`), including rung 2a
(`:427`–`:434`), mapped onto this subsystem. Nothing below can reach rung 4 or 5: Phase 13 owns no
capability gate and touches no vanilla framebuffer path.

| Failure | Detection boundary | Rung | Disposition |
|---|---|---|---|
| Companion resource decodes but has the wrong dimensions for its sprite | plan, per sprite | 2a | that sprite falls back to `DefaultFill`; one diagnostic naming the sprite; the atlas still builds |
| Companion atlas allocation fails | build | 2a | that kind publishes `Absent(PUBLICATION_UNAVAILABLE)`; Phase 5 emits `MissingTextureBinding` and Phase 7 degrades the affected program (`docs/phase5/v1/PHASE_5_DOC.md:2116`). The already-emitted `MC_NORMAL_MAP` stays defined — §4.1.6's cycle-break means the macro is a pipeline-time promise, and a pack that samples a missing binding degrades rather than failing to compile |
| Companion mip generation fails at some level | build | 2a | that companion kind is disabled for the publication rather than shipping a half-built mip chain, which would sample garbage at distance |
| Animation tick hook absent or throws | Phase 7 application audit / hook boundary | 2a | companion animation disabled; companions retain frame 0; one diagnostic |
| Noise generation cannot allocate | build | 2a | `NOISE` publishes `Absent(PUBLICATION_UNAVAILABLE)`; a pack sampling `noisetex` degrades locally |
| `texture.noise` override fails to decode | build | 1-adjacent, 2a | fall back to the generated texture; one diagnostic. A pack that asked for noise still gets noise (§4.2.4) |
| Custom texture: file missing, undecodable, or byte length mismatched | plan or build, per entry | 2a | that entry only is dropped with a closed `TextureFailure`; the rest of the publication proceeds; the sampler keeps whatever Phase 5's table already resolves |
| Custom texture: dimension exceeds the capability limit | plan, per entry | 2a | same; the diagnostic names the limit, using Phase 5's canonical limit vocabulary |
| Custom texture: sampler-type ambiguity or conflict in one program | plan, per program | 2a | §4.3.4's resolution rules; the unit binds nothing for that program |
| Custom texture: binding outside Phase 5's key domain | plan | 2a | `UnsupportedBinding`; §4.3.6 |
| `.mcmeta` malformed or unreadable | build, per entry | — | defaults applied, one diagnostic; never fails the texture |
| Atlas catalog unavailable (no stitch yet, or hooks absent) | plan | 2a | companions publish `Absent(NOT_CONFIGURED)`; `atlasSize` reports `Unknown`; noise and custom textures are unaffected because neither depends on the atlas |
| Lease rejected by Phase 5 (`OVERLAY_PUBLICATION_ID_MISMATCH` / `REGISTRY_FINGERPRINT_MISMATCH`) | Phase 5 `textureBindings` | — | not a failure: a replacement publication happened. Phase 7 suppresses that draw and closes the lease; the next frame acquires a fresh one (§4.5.2 item 3) |
| Resize consumer cannot re-derive the publication | `resize` | 3 | return `FAILED`; Phase 5 disposes the estate and produces an off publication (`docs/phase5/v1/PHASE_5_DOC.md:2018`) |
| Whole-publication build failure | build | 3 | closed `TextureFailure`; the previous `READY` publication stays installed; no partial publication ever exists |

`TextureFailure` carries a code, a sanitized message key, the diagnostic id, and the logical texture
identity — never a raw GL number, matching Phase 5's `BufferFailure` discipline. Driver detail goes to
the GUI and the log, never to chat.

**Shaders-off is unconditionally reachable.** Every Phase 13 object is additional; nothing replaces or
mutates a vanilla texture. `close()` deletes only owned objects and drops foreign handles without
deleting them (§4.5.3), so the vanilla atlas, its mip chain, and its bindings are byte-identical before
and after a Phase 13 lifetime. This is §G2.4 rule 5 (`docs/design/v3/DESIGN.md:439`–`:440`) and the
spec's architecture requirement that "companion loading must not regress vanilla atlas stitching when
shaders are off" (`docs/design/v3/DESIGN.md:2498`–`:2499`); `companion_vanillaAtlasUntouchedWithShadersOff`
is the test that enforces it.

---

## 7. Threading & performance notes

### 7.1 Thread ownership

The render thread owns all GL (§G2.3, `docs/design/v3/DESIGN.md:412`–`:417`).

| Component | Thread | Note |
|---|---|---|
| `plan` — companion planning, custom-texture spec resolution, `.mcmeta` interpretation | any thread | pure; no GL, no Minecraft type; this is what makes §8.1 possible |
| Noise byte generation | any thread | pure arithmetic over a `byte[]`; the upload is separate |
| `build` — allocation, upload, parameterization | render thread only | |
| `supersede`, `close`, lease counting | render thread only | `close()` is idempotent |
| `lease()` / `publicationId()` | render thread | called inside Phase 7's draw path |
| `H13-ATLAS-01/02/03` handlers | render thread | resource reload and stitching are render-thread events on 1.12.2 |
| `H13-ATLAS-04` animation handler | render thread | it is the vanilla animation tick |
| `atlasSize(AtlasId)` | render thread | reads an immutable field of the current publication |

Off-thread decode of companion images is deliberately **not** designed here. §G2.3 permits off-thread
texture upload only through Phase 14's shared-context design with its mandatory synchronous fallback
(`docs/design/v3/DESIGN.md:413`–`:414`), and that is Phase 14's to design. §5.5 hands it over.

### 7.2 Allocation posture and hot paths

- **Not a hot path:** planning, building, and superseding happen at pack load, option change, and
  resource reload. Allocation there is unremarkable, and §G2.5 is explicit that clean code comes first
  (`docs/design/v3/DESIGN.md:451`–`:456`).
- **The one per-frame path** is `lease()` plus the lease's own `entry(StageId, TextureOverlayKey)`
  lookups, which Phase 5 performs while resolving its sixteen rows. Both must be allocation-free: the
  lease is a thin view over an already-built immutable table, and `entry` is an array index over the
  two closed enums, not a map lookup. No `Optional`, no boxing, no iterator on that path.
- **The one per-tick path** is `H13-ATLAS-04`'s companion animation upload (§4.1.7). It touches only
  animated sprites that actually have companions, reuses one pre-sized transfer buffer per companion
  atlas rather than allocating per sprite, and issues sub-image uploads at the sprite's origin. It is
  the single most likely candidate for Phase 14's asynchronous transfer work, and §5.5 names it.
- **No array caches, no mutable-pose machinery.** §G2.5 forbids replicating the reference's
  allocation-discipline apparatus; modern GC removes the constraint that produced it.
- Measurement before optimization: the memory estimate of §4.8 and the per-publication diagnostics are
  the evidence surface, and Phase 14 owns any change made on the strength of them.

---

## 8. Testability plan

### 8.1 Headless `:engine` tests

Everything in this list runs with JUnit alone, no GL context, no Minecraft — the D-6 seam paying rent
(§G6, `docs/design/v3/DESIGN.md:704`–`:708`). `AtlasDescriptor` is a plain record, so the entire
companion planner is testable from synthetic atlases.

| Area | Tests |
|---|---|
| Companion planning | `companion_discoveryPerSprite`, `companion_layoutAndMipChainMatchBase`, `companion_missingNormalUsesContractDefault`, `companion_missingSpecularUsesZeroDefault`, `companion_demandDrivenBuildSkipsUnusedAtlases`, `companion_planOrderIsStableAcrossRuns` |
| Noise | `noise_dimensionsFormatAndDeterminism`, `noise_generatorMatchesContractNotRandomZero`, `noise_resolutionFromRequirementsAndBaseline`, `noise_packOverrideReplacesGenerated`, `noise_generatorIsPlatformIndependent` (same bytes on any JVM, any locale) |
| Custom textures | `custom_keyGrammarRoundTrip`, `custom_duplicateDiscriminatorNotFoldedIntoSampler`, `custom_stageExpansionGbuffersIncludesShadow`, `custom_stageExpansionDeferred`, `custom_stageExpansionCompositeIncludesFinal`, `custom_rawArityAndByteLength`, `custom_oneSamplerTypePerUnitPerProgram`, `custom_mcmetaBlurSetsFilter`, `custom_mcmetaClampSetsWrap`, `custom_colortexOverrideDiagnosedOrBound`, `custom_publicationOutcomeIsClosedAndTotal` |
| Overlay model | `overlay_tableIsTotalOverBothDomains`, `overlay_companionsAbsentOutsideWorldStages`, `overlay_noisePublishedAtEveryStage`, `overlay_noDynamicAllocationEver`, `overlay_fingerprintIndependentOfIterationOrder` |
| `atlasSize` | `atlasSize_valueAndValidityWindow`, `atlasSize_unknownBeforeFirstStitch` |
| Macros | `macro_normalMapStateFollowsCompanionEnablement`, `macro_specularMapStateFollowsCompanionEnablement`, `macro_stateKnownBeforePreprocessing` (the cycle-break of §4.1.6) |
| Lifecycle | `lifecycle_supersedeWaitsForOutstandingLeases`, `lifecycle_closeIsIdempotent`, `lifecycle_replanWithEqualFingerprintReusesPublication`, `lifecycle_staleCatalogChangesFingerprint` |

### 8.2 Facade / `GLCapabilityProfile` tests

Against Phase 1's `RecordingGLDevice` and recorded capability profiles, asserting call sequences rather
than pixels:

- `gl_companionUploadsUseBaseOriginsAndAllMipLevels`
- `gl_animationTickUploadsOnlyTrackedSprites`
- `gl_rawCustomTextureUsesDeclaredTargetAndTransferPath`
- `gl_foreignDynamicTexturesAreNeverAllocatedOrDeleted`
- `gl_teardownProvesNoLeakedObjectsAndNoUseAfterDelete`
- `gl_capabilityShortfallDisablesCompanionsCleanly`
- `companion_vanillaAtlasUntouchedWithShadersOff` — the §6 invariant, asserted as "zero facade calls
  targeting the foreign atlas handle other than bind and label"

### 8.3 Hook and integration tests

- `hook_atlasCatalogCapturedAtStitchPost`, `hook_spriteCompanionAndAnimationRows` — the application
  audit sees exactly the §4.6 rows, with the dormant `H13-SPRITE-02` row reported as dormant, not
  missing.
- The Mixin application-count audit is Phase 7's mechanism; Phase 13 contributes its subreport rows.

### 8.4 Conformance tiers

Fixtures obey §G6's derived-artifacts clause without exception
(`docs/design/v3/DESIGN.md:718`–`:725`): no matrix pack is committed or re-hosted, goldens carry no pack
source text, no rendered image enters the repository, and committed oracles are manifests.

| Tier | What it exercises here |
|---|---|
| T0 | the classic matrix loads with companions, noise, and custom textures enabled; no crash, no missing-binding storm |
| T1 | self-baseline regression on a normal-mapped scene and a noise-sampling scene, **including camera-path motion** (§G6's REV1 requirement, `docs/design/v3/DESIGN.md:696`–`:699`) — an animated-sprite scene is exactly where companion desync would show |
| T2 | pixel-parity against OptiFine G6 on classic packs. This is the tier that can overturn §4.2.2's two `[A]`-tagged generator details and §4.1.4's byte-order assumption, and it is the only sanctioned evidence route for reopening the noise question (§4.2.3) |
| T3 | the full classic matrix — the phase's impl gate (§9) |

---

## 9. Milestone staging

Every component carries exactly one tag (§G4.3, `docs/design/v3/DESIGN.md:567`–`:571`). Phase 13's
milestone is v0.5 throughout (`docs/research/v1/RESEARCH.md:951`), which is expected for a phase whose
entire scope lands at one milestone; the table exists so the *ordering within* v0.5 is explicit and so
the two dormant items are visible.

| Component | Milestone | Exit evidence |
|---|---|---|
| Overlay publication, lease, and fingerprint (§4.5) | v0.5 | `lifecycle_*`, `overlay_*`; a Phase 5 `Bound` result on a real frame |
| Noise generator, sizing, override, publication (§4.2) | v0.5 | `noise_*`; T1 on a noise-sampling scene |
| Custom textures, all three forms, `.mcmeta`, stage expansion (§4.3) | v0.5 | `custom_*`; T1 with a pack declaring one of each form |
| Companion atlases: planning, layout, mip chain, defaults (§4.1.1–§4.1.5) | v0.5 | `companion_*`; T1 on a normal-mapped scene |
| `MC_NORMAL_MAP` / `MC_SPECULAR_MAP` state (§4.1.6) | v0.5 | `macro_*`; **gated on R1** |
| Companion animation tick (§4.1.7) | v0.5 | `companion_animatedSpriteFramesStayInStep`; T1 on an animated-sprite scene with camera motion |
| `atlasSize` value source (§4.4) | v0.5 | `atlasSize_*` |
| Hook rows `H13-ATLAS-01`…`04`, `H13-SPRITE-01` (§4.6) | v0.5 | `hook_*` plus Phase 7's application audit |
| Resize-consumer registration (§4.7.1) | v0.5 | `lifecycle_*` under a `PACK_CONFIGURATION` notice |
| `H13-SPRITE-02` per-sprite animation injection | v0.5, **dormant** | catalogued only; activated only if `H13-ATLAS-04`'s granularity proves insufficient at T2 |
| Bindings beyond Phase 5's key domain (§4.3.6) | v0.5, **gated on R2** | `custom_colortexOverrideDiagnosedOrBound` passes either way; the *bound* half needs R2 |
| Off-thread companion decode / PBO upload | **post-v0.5** | Phase 14's; §5.5 |

Nothing in this phase is architected-now-implemented-later in the §G0.3 sense beyond the two rows marked
dormant and gated: Phase 13 is a v0.5 leaf, so its architecture and its implementation land together.

---

## 10. OQ & spike specifications

**Phase 13 is assigned no open question.** The phase row's `OQs` cell is `—`
(`docs/design/v3/DESIGN.md:2438`), and §G10's assignment table gives no OQ an owner of P13
(`docs/design/v3/DESIGN.md:862`–`:886`). No spike is authorized or needed, and per §G4.4 this phase does
not resolve anyone else's open question either.

Two OQs touch this subsystem without belonging to it, recorded so a reader does not go looking:

- **OQ-15** (asynchronous compile and transfer) is Phase 14's (`docs/design/v3/DESIGN.md:878`). §7.1's
  refusal to design off-thread upload and §5.5's hand-off are the deliberate consequence.
- **OQ-7** (identity/macro posture) is Phase 3's for architecture and G8/S3's for the final decision
  (`docs/design/v3/DESIGN.md:870`). §4.1.6 supplies a value into that set; it does not decide the
  posture.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale |
|---|---|---|
| `D-P13-1` | Declare `docs/design/v3/DESIGN.md` as the governing revision in §0, with every coordinate re-derived from v3's own headings | The commissioning request selected v3; Phase 11 v1 set the precedent for a new build adopting it, and §G0.4 step 1 forbids shifting another revision's coordinates by an offset |
| `D-P13-2` | Proceed against the unverified `PHASE_7_DOC.md` under maintainer authorization, marking every Phase-7-derived contract provisional | §G5.3's gating invariant would otherwise block; the maintainer authorized it, and round 32's §5 delta lies entirely outside what this phase consumes (§5.4) |
| `D-P13-3` | Specify the contract xorshift generator; reject `java.util.Random(0)` | §G11.4 pre-decided rejection (`docs/design/v3/DESIGN.md:955`); the only reopening route is §G0.1 with App/observed evidence, and it is not taken here (§4.2.3) |
| `D-P13-4` | Build real companion **atlases** with matching mip chains; reject per-bound-texture-id keying | The contract is atlases (`docs/research/v1/RESEARCH.md:594`–`:595`), and PD itself records that the per-id design solves neither animation sync nor per-sprite companions (`…/PINTONIUM_DESIGN.md:634`–`:636`) |
| `D-P13-5` | Use RESEARCH's `0xFF7F7FFF` flat-normal default, stating the assumed channel order in exactly one place | §G12.4's C-TX01 keeps RESEARCH normative and byte order unresolved (`docs/design/v3/DESIGN.md:1088`); isolating the assumption makes a T2 failure localize to one test |
| `D-P13-6` | Honor `.mcmeta` filter and wrap in full, and treat `.0`–`.9` strictly as a duplicate-key discriminator | The shipped contract has no filter/wrap *suffix*; honoring both halves is what actually satisfies the do-not-inherit row (§3.6 item 1) |
| `D-P13-7` | Forge `TextureStitchEvent.Pre/Post` for timing plus one accessor-only mixin for data | Phase 7 prefers the events "where sufficient" (`docs/phase7/v1/PHASE_7_DOC.md:1269`); they are sufficient for moments and insufficient for the private sprite table `[V:mcp]` |
| `D-P13-8` | Hook the atlas-wide animation tick (`func_94248_c`) rather than per-sprite updates | One hook instead of hundreds, and it runs after every animated sprite has advanced, which is the only moment a batch upload is coherent (§4.1.7) |
| `D-P13-9` | `atlasSize` is the base atlas extent, with `Unknown` as a real pre-stitch state | The contract ties the value to the bound atlas (`…/doc/shaders.txt:177`); inventing a number before the atlas exists would be a fabricated contract value |
| `D-P13-10` | Derive `MC_NORMAL_MAP`/`MC_SPECULAR_MAP` from pipeline-time `CompanionPolicy`, not from atlas build success | Breaks the circularity between preprocessing and stitching (§4.1.6); a §G11.5 "reuse structure, re-derive values" adoption of PD §7.6's macro placement |
| `D-P13-11` | Design the full App F.5 binding space, publish what Phase 5's key domain reaches, and diagnose the rest as `UnsupportedBinding` | §G1.1 forbids inventing a dependency interface (`docs/design/v3/DESIGN.md:296`–`:298`); R2 is the route, rung 2a is the interim |
| `D-P13-12` | Demand-driven companion construction, with unconditional construction as the specified fallback | PD §17 B13's lesson applied to companions; the fallback is the cost the governing design already accepts, so a failed request never stalls the phase (§4.1.1) |
| `D-P13-13` | Treat Phases 3 and 5 as verified on their review verdicts, not on their stale doc footers | §G1.3 defines "verified" by the latest review verdict (`docs/design/v3/DESIGN.md:357`–`:359`); §3.6 item 3 records the provenance so no later session re-derives it |
| `D-P13-14` | Do not renormalize normals during mip generation | Renormalizing would "improve" a contract-visible component, which §G4.2 forbids; tagged `[A]` and falsifiable at T2 (§4.1.3) |

No decision here contradicts RESEARCH's D-1…D-10. D-6 shapes §2.1's split, D-9's compat baseline is
assumed throughout, and D-3's fixed matrix is what §8.4 measures against.

### 11.2 Binding-decision disposition

| Binding decision | Disposition here |
|---|---|
| D-2 (shaders only) | labPBR *semantics* are refused as out of scope (§1.2); MCPatcher-era normal/specular *features* are not implemented — only the pack-contract samplers |
| D-4 (architect the full set, wire a subset) | The overlay table is total over Phase 5's closed domains today, so widening it under R2 adds entries, not structure |
| D-5 (Mixin hooks only, no class replacement) | §4.6 adds one accessor mixin and one TAIL injection; every other moment is a Forge event |
| D-6 (engine/glue seam) | §2.1; the entire planner, generator, and resolver are Minecraft-free |
| D-9 (compat profile) | No core-profile-only path is used; `TEXTURE_RECTANGLE` and 1D targets are the contract's, and the compat profile supplies them |
| §G4.1 (verbatim vocabulary) | `normals`, `specular`, `noisetex`, `gaux1`–`gaux4`, `atlasSize`, `texture.noise` appear verbatim; no synonym is coined |
| §G4.2 (no improving contract-visible components) | `D-P13-14` (no renormalization) and `D-P13-5` (RESEARCH's literal default) are the two places the temptation exists, and both are refused |

### 11.3 Input contradictions found

All four are stated in full in §3.6 with their rulings and provenance: the filter/wrap suffix wording,
Phase 5's narrower key domain, the two stale dependency footers, and PD's divergent flat-normal value
(the recorded C-TX01 conflict). Each is reported here rather than smoothed over, per §G1.1
(`docs/design/v3/DESIGN.md:282`–`:284`).

### 11.4 Open items and hand-offs

- **Phase 14** owns off-thread companion decode and PBO upload for the animation batch (§5.5, §7.2).
- **Phase 6** decides the upload cadence for `atlasSize`, a uniform whose value is stage-conditional
  (§4.4).
- **Phase 4** merges the per-stage `DeclaredUniformCatalog`s that §4.3.4's `unit → SamplerType` map is
  built from; Phase 13 reads that merged layout and reinterprets neither the type algebra nor the
  source.
- **G8** advertises labPBR as a pack-side convention; no Phase 13 change follows from it (§5.5).
- **`H13-SPRITE-02`** stays dormant unless T2 shows the atlas-wide tick granularity is insufficient
  (§9).
- **The four requests R1–R4** are §5.3's; each has a specified fallback already designed, so none is on
  the critical path.

### 11.5 Requested upstream changes

1. **DESIGN (any future revision).** Correct the Phase 13 do-not-inherit wording at
   `docs/design/v3/DESIGN.md:2476`–`:2478`. The contract grammar has no filter/wrap *suffix*: the
   `.0`–`.9` suffix is a duplicate-key discriminator
   (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:117`) and filter/wrap come from
   `.mcmeta` sidecars (`:118`–`:119`). Suggested wording: "PD §7.4 both folds the `.0`–`.9`
   discriminator away and ignores `.mcmeta` filter/wrap; ours must preserve the discriminator and honor
   the sidecar." No behavior changes — §4.3.5 already implements the corrected reading, and §3.6 item 1
   records the ruling. This is the highest-value upstream edit because the current wording invites every
   future reader to look for a suffix that does not exist.
2. **Phase 3 (§5.3 R1).** Name Phase 13's `CompanionMacroState` as the input driving the
   normal/specular option macros. This is the single conformance shortfall of the ungranted set: without
   it, `MC_NORMAL_MAP`/`MC_SPECULAR_MAP` are never defined and packs cannot reach their PBR branch even
   though the companion atlases exist.
3. **Phase 3 (§5.3 R4).** Add `CompanionMapRequirement(boolean normals, boolean specular)` to
   `ResourceRequirements`. Memory-only impact if declined.
4. **Phase 5 (§5.3 R2).** Widen `TextureOverlayKey` and the deferred/composite/final overlay column to
   App F.5's full binding space.
5. **Phase 1 (§5.3 R3).** Allocate the `engine.textures` / `mod.glue.textures` / `mod.mixin.textures`
   trio, following the granted Phase 7 and Phase 8 precedent.

No RESEARCH change is requested. Every RESEARCH statement this phase consumed resolved cleanly at its
cited line.

---

## 12. Implementation checklist

Ordered, independently actionable, each with a milestone tag and a test hook.

1. `[v0.5]` Define the `:engine` texture value types of §2.3 — `AtlasId`, `SpriteDescriptor`,
   `AtlasDescriptor`, `AtlasCatalog`, `CompanionKind`, `CompanionOrigin`, `CompanionSpriteSource`,
   `CompanionAtlasPlan`, `NoisePlan`, `CustomTexturePlanEntry`, `TexturePlan`, `TextureFailure` — all
   immutable, all Minecraft-free. **Test:** `overlay_tableIsTotalOverBothDomains`, plus a C-1 seam
   assertion that the package references no forbidden type.
2. `[v0.5]` Implement the noise generator of §4.2.2 as pure arithmetic over `byte[]`. **Test:**
   `noise_dimensionsFormatAndDeterminism`, `noise_generatorIsPlatformIndependent`,
   `noise_generatorMatchesContractNotRandomZero`.
3. `[v0.5]` Implement companion planning of §4.1.2–§4.1.4: total discovery, layout mirroring, mip
   planning, default fills, stable ordering. **Test:** `companion_discoveryPerSprite`,
   `companion_layoutAndMipChainMatchBase`, `companion_missingNormalUsesContractDefault`,
   `companion_missingSpecularUsesZeroDefault`, `companion_planOrderIsStableAcrossRuns`.
4. `[v0.5]` Implement `CompanionPolicy` derivation of §4.1.1 with both the R4-granted and
   `ALWAYS_ON_FALLBACK` paths, and `CompanionMacroState` of §4.1.6. **Test:**
   `companion_demandDrivenBuildSkipsUnusedAtlases`, `macro_stateKnownBeforePreprocessing`,
   `macro_normalMapStateFollowsCompanionEnablement`, `macro_specularMapStateFollowsCompanionEnablement`.
5. `[v0.5]` Implement custom-texture resolution of §4.3.1–§4.3.3: key-to-overlay-key mapping, stage
   expansion consumed from Phase 3, the three source forms, the dynamic/live sub-branch, and raw
   byte-length/limit/format validation. **Test:** all `custom_*` except the `.mcmeta` and sampler-type
   rows.
6. `[v0.5]` Implement sampler-type disambiguation of §4.3.4 over Phase 4's merged declared-uniform
   layout. **Test:** `custom_oneSamplerTypePerUnitPerProgram`.
7. `[v0.5]` Implement `.mcmeta` interpretation of §4.3.5 including the malformed-sidecar default path.
   **Test:** `custom_mcmetaBlurSetsFilter`, `custom_mcmetaClampSetsWrap`.
8. `[v0.5]` Implement `UnsupportedBinding` of §4.3.6 and its once-per-publication diagnostic. **Test:**
   `custom_colortexOverrideDiagnosedOrBound`, `custom_publicationOutcomeIsClosedAndTotal`.
9. `[v0.5]` Implement the publication, fingerprint, lease, lease counting, and the §4.5.3 state machine
   and teardown order. **Test:** `lifecycle_supersedeWaitsForOutstandingLeases`,
   `lifecycle_closeIsIdempotent`, `lifecycle_replanWithEqualFingerprintReusesPublication`,
   `overlay_fingerprintIndependentOfIterationOrder`, `gl_teardownProvesNoLeakedObjectsAndNoUseAfterDelete`.
10. `[v0.5]` Implement the `:mod` glue: atlas source adapter, resource reader, and the facade-backed
    uploader that performs allocation, sub-image upload, and parameterization. **Test:**
    `gl_companionUploadsUseBaseOriginsAndAllMipLevels`,
    `gl_rawCustomTextureUsesDeclaredTargetAndTransferPath`,
    `gl_foreignDynamicTexturesAreNeverAllocatedOrDeleted`.
11. `[v0.5]` Implement hook rows `H13-ATLAS-01`, `H13-ATLAS-02`, `H13-ATLAS-03`, and `H13-SPRITE-01` of
    §4.6, and register the rows with Phase 7's application audit. **Test:**
    `hook_atlasCatalogCapturedAtStitchPost`, `hook_spriteCompanionAndAnimationRows`.
12. `[v0.5]` Implement `atlasSize` of §4.4 including the `Unknown` pre-stitch state and invalidation at
    `H13-ATLAS-01`. **Test:** `atlasSize_valueAndValidityWindow`, `atlasSize_unknownBeforeFirstStitch`.
13. `[v0.5]` Implement hook row `H13-ATLAS-04` and the companion animation tracker of §4.1.7, including
    the interpolated-animation weight. **Test:** `companion_animatedSpriteFramesStayInStep`,
    `gl_animationTickUploadsOnlyTrackedSprites`.
14. `[v0.5]` Register the §4.7.1 resize consumer and implement its per-reason behavior. **Test:**
    `lifecycle_staleCatalogChangesFingerprint` under a `PACK_CONFIGURATION` notice.
15. `[v0.5]` Implement the §6 failure table end to end: every row a closed value, every diagnostic
    sanitized, no raw GL number. **Test:** the `TextureFailure` matrix plus
    `gl_capabilityShortfallDisablesCompanionsCleanly`.
16. `[v0.5]` Assert the shaders-off invariant. **Test:** `companion_vanillaAtlasUntouchedWithShadersOff`.
17. `[v0.5]` Wire the §4.8 memory estimate and its per-publication log line. **Test:** the estimate is
    zero for a disabled companion kind.
18. `[v0.5]` Run the Phase 13 impl gate: the full classic matrix at T3, and packs using
    `MC_NORMAL_MAP` rendering correctly on the fixed scenes
    (`docs/design/v3/DESIGN.md:2507`–`:2508`). **Test:** Phase 2's named artifacts and manifests; note
    that the `MC_NORMAL_MAP` half of the gate cannot pass until §5.3 request **R1** is granted.
19. `[v0.5, conditional]` On R2 being granted, publish the previously-`UnsupportedBinding` entries.
    **Test:** the bound half of `custom_colortexOverrideDiagnosedOrBound`.
20. `[post-v0.5]` Hand §7.2's per-tick animation upload and the raw-upload path to Phase 14 for
    asynchronous transfer, preserving §4.5.3's teardown order and §4.7.1's resize participation.
    **Test:** Phase 14's, not this phase's.

---

*End of PHASE_13_DOC.md. Initial build under §G1.1; not verified. The next required action is a fresh
§G1.2 verify session writing round one into `docs/phase13/reviews/`. Phases 3 and 5 were consumed
as verified dependencies; Phase 7 was consumed provisionally under the maintainer authorization
recorded in §0.2 item 1, and a §G1.3 fix-up is owed here if Phase 7 round 33 contradicts §5.2's
Phase 7 table.*
