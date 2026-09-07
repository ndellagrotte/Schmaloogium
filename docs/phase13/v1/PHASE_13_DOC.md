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
- **Design status:** coordinated architecture-only rebuild through §0.6; unverified
- **Date:** 2026-09-06

The commissioning request explicitly selected v3, so every `DESIGN.md` coordinate in this document
was re-derived from `docs/design/v3/DESIGN.md`'s own headings and endpoints rather than shifted from
another revision (§G0.4 step 1, `docs/design/v3/DESIGN.md:205`–`:207`). Phase 11 v1 set the
precedent for a new build adopting v3 at its initial build; Phases 3–9 remain anchored to
`docs/design/v2.0-RC3/DESIGN.md` and this document repoints none of them. `docs/MOVES.md` records
the adoption.

### 0.1 Original-build read inventory (historical coordinates; rebuild reads in §0.6)

| Input | Portion read | Why |
|---|---|---|
| `docs/design/v3/DESIGN.md` | Part I §G0–§G12 (`:132`–`:1134`) and the Phase 13 specification (`:2436`–`:2510`); §G9 template at `:817`–`:854` | mandatory §G1.1 reading |
| `docs/research/v1/RESEARCH.md` | §0 (`:11`–`:52`), §1 (`:55`–`:105`), §4.6 texture part (`:593`–`:599`), §9 milestones (`:940`–`:955`), App B.3 (`:1228`–`:1256`), App B.4 (`:1257`–`:1270`), App D.3 (`:1355`–`:1368`), App E (`:1387`–`:1433`) with rows 10–11 at `:1410`–`:1411`, App F.3 const whitelist (`:1454`–`:1470`), App F.5 (`:1482`–`:1492`) | the contract this phase implements |
| `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` (`PD`) | §11 whole (`:619`–`:645`), §7.6 (`:468`–`:489`), §7.4 (`:437`–`:450`), §17 rows B10/B13 (`:796`, `:799`), §18 (`:803`–`:819`) | the §G11.6 P13 reading map row (`docs/design/v3/DESIGN.md:1003`) |
| `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md` | §8 "Texture system" only (`:456`–`:487`) | the one section the spec grants (`docs/design/v3/DESIGN.md:2491`), under §G7 item 2 |
| `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties` | custom-texture and noise block (`:78`–`:125`) | legally clean shipped pack-author doc, citable freely (§G7 item 3, `docs/design/v3/DESIGN.md:743`–`:745`) |
| `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` | sampler/unit tables (`:190`–`:207`, `:272`–`:326`), `atlasSize` row (`:177`), noise const row (`:422`), option-macro rows (`:655`–`:656`) | same shipped-doc grant |
| `docs/phase3/v1/PHASE_3_DOC.md` | §1.2 ownership row (`:309`), §3.2 texture rows (`:749`–`:753`), §3.3 noise row (`:803`), §4.4 macros (`:957`–`:996`), §5.1 in full (`:1422`–`:1625`) | dependency contract |
| `docs/phase5/v1/PHASE_5_DOC.md` | Original-build historical reads: §1.2 ownership (`:382`), resize consumer types (`:617`–`:621`), `addResizeConsumer` (`:1807`), §4.12 (`:1869`–`:1957`), §5.1 (`:2000`–`:2020`), §6 (`:2116`), §9 (`:2321`); coordinated current contracts are cited in §5.2 | historical input record; not current line pins |
| `docs/phase7/v1/PHASE_7_DOC.md` | Original-build §1.2 ownership, hook need9, executor step5, §4.10.1 catalog, AppE10/11 deferral, atlas event, reload and hand-off contracts | originally provisional; Review36 cleared that history, coordinated §5 is newly unverified (§0.2/§5.4) |
| `docs/phase1/v14/PHASE_1_DOC.md` | package tables (`:1526`–`:1558`), seam constraints C-1…C-4 (`:2229`–`:2244`) | module placement and the D-6 seam |
| MCP `cleanroom` | `resolve_symbol` for `TextureMap`, `TextureAtlasSprite`, `AbstractTexture#getGlTextureId`, `TextureManager#getTexture`; `get_class_details` for both texture classes; `search_cleanroom_api("texture stitch", kind=event)` and `get_api_class` for `TextureStitchEvent` | the spec's named MCP recipes (`docs/design/v3/DESIGN.md:2493`–`:2494`) |

Dependency PHASE docs consumed: Phase 3 (custom-texture and noise algebra, macro configuration),
Phase 5 (texture-overlay lease, fixed unit table, format vocabulary, resize consumer), Phase 7
(hook catalog format, App E deferral ledger, reload lifecycle).

### 0.2 Deviations from the assigned reading list, and their reasons

1. **Historical authorized provisional Phase 7 read, now superseded.** The original build
   proceeded under explicit maintainer authorization while Review32 was
   `PASS-WITH-CORRECTIONS`. Review36 subsequently returned literal `PASS`, zero corrections,
   `Interface changed: no` (`docs/phase7/reviews/PHASE_7_REVIEW_36.md:107-123`), clearing that
   historical gate under `docs/design/v3/DESIGN.md:348-359`. D-P13-2 is historical, not a
   directory-roll condition. This coordinated rebuild changes Phase 7 §5 again and therefore
   introduces a **new unverified consumed interface**; fresh review of the new bytes, not the
   directory name, governs future consumption (§5.4).
2. **PD §7.4 was read although the Required-inputs line names only §11 and §7.6.** The Phase 13
   scope body cites §7.4 directly for the filter/wrap do-not-inherit row
   (`docs/design/v3/DESIGN.md:2476`–`:2478`), so the row cannot be honored without reading it. PD
   §17 rows B10/B13 and §18 were read for the same reason: §G11.4 requires the relevant
   do-not-inherit rows to be shown handled in §3 (`docs/design/v3/DESIGN.md:958`–`:960`).
3. **The shipped pack-author docs were read beyond the permitted behavioral digest.** They
   establish discriminator and sidecar grammar, but do not establish the absence of undocumented
   property-key suffix behavior. Phase 3's explicit stripping contract and the assignment's
   suffix-honoring requirement conflict; §3.6 and §11.5 route that conflict without reparsing.
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

### 0.4 Fix-up addendum — review round 1

Applied under §G1.3 from `docs/phase13/reviews/PHASE_13_REVIEW_1.md` (PASS-WITH-CORRECTIONS;
blocking=0, corrections=3, notes=1). Three corrections applied: Phase 3's dependency state
re-derived from `PHASE_3_REVIEW_36.md` and disclosed as provisional (§3.6 item 3, §5.2, §5.4,
`D-P13-13`, §11 closing); Phase 7's dependency-state anchor moved to `PHASE_7_REVIEW_36.md` and the
dead "round 33" conditional retired (§0.2 item 1, §5.2, §5.4, §11 closing); §4.1.4's byte-order
statement reconciled with the "flat normal" label. §5 (the declared interface region) changed, so a
fresh whole-document verify round is required. Reasoning is in that review's `## Resolutions`.

### 0.5 Fix-up addendum — review round 2

Applied under §G1.3 from `docs/phase13/reviews/PHASE_13_REVIEW_2.md` (PASS-WITH-CORRECTIONS;
blocking=0, corrections=3, notes=1). Three corrections applied: the `AtlasId.canonicalName` domain
and a parameterless `atlasSize()` for the world block/item atlas are now stated (§2.2, §4.4, §5.1,
§9); every stale `docs/phase3/v1/PHASE_3_DOC.md` line anchor was re-resolved against the current
Phase 3 v1 text (§0.1, §3.1, §3.4, §4.1.1, §4.1.6, §4.2.1, §4.2.4, §4.3.1, §4.3.3, §4.3.4, §4.5.1,
§5.2, §5.3); and §4.3.6's follow-through pointer now names §12 item 19. §5 changed again, so a fresh
whole-document verify round is still required. Reasoning is in that review's `## Resolutions`.

### 0.6 Coordinated shared-unit rebuild — Review 3

The maintainer authorized an architecture-only rebuild of Phase 4/5/7/13 documents, following
`docs/phase13/reviews/PHASE_13_REVIEW_3.md:304-324` (`FAIL`, blocking=1, corrections=11,
`Interface changed: yes`). Prior addenda remain history; this addendum supersedes any obsolete
provisional/version-roll, collapsed-cell, source-less identity, macro-cycle, or keep-old statement
in that history. Phase 13 remains governed by v3; Phase 4/5/7 retain RC3.

Additional reads for this rebuild: this whole document and Review3; v3 Part I and Phase13
assignment; Phase3's closed declaration algebra, load order, canonical framing and texture §5;
Phase6 sampler/afterBind and retirement contracts; Phase8 invocation and pending R8-2 contracts;
RESEARCH B.3/F.5; PD §§7.4/7.6/11/17/18; the permitted texture digest and shipped texture/macro
rows. The coordinator read MOVES/reviews; document owners read the complete four-document set.
MCP-confirmed sprite-state mappings supplied with the rebuild are recorded in §4.6, not claimed
as executed animation behavior. No code, review, fixture, manifest, directory roll or verification
loop is part of this build.

| Review3 finding | Integrated disposition | Remaining owner action |
|---|---|---|
| 1 | §0.2, §3.6, §5.4, D-P13-2: Review36 cleared historical Phase7 gate | Fresh PASS for changed coordinated §5 |
| 2 | §3.6, §4.3.1/.5: stripped key suffix versus retained sidecar | R1-independent Phase3/DESIGN U1 reconciliation |
| 3 | §2.3, §4.3.2/.3, §4.5: content/source/parameterization/reload identity | Future implementation and review |
| 4 | §2.3, §4.1.7, §4.6: immutable metadata and post-vanilla snapshot/accessors | Hook implementation; frame0 fallback |
| 5 | §4.1.1/.6, §5.3: static preliminary demand before load/jcpp | Phase3 R1 typed option-macro input |
| 6 | §1–§9, §11–§12: effective selector, full-shape candidates, Phase5 bind owner | Phase6 R7-10 and Phase8 R7-12/13 adoption; fresh reviews |
| 7 | §4.3.6: closed unknown/known requested target, no sentinel | Future implementation |
| 8 | §4.2.2: signed wrapping recurrence and byte vectors | Future implementation; no executable check run here |
| 9 | §3 provenance and active citations use full paths and independent ranges | Fresh citation review |
| 10 | §4.3.5 cites Phase3 :750 separately from suffix :749 | None beyond review |
| 11 | §3.5 row-local D-P13-3/16/6/4/9/12/17/15 | Fresh review |
| 12 | T-7 and D-P13-8 are design requirements, not observed synchronization | Future behavioral evidence |

§5 changed in this coordinated rebuild. Unverified; a fresh whole-document review returning literal PASS is required before verified downstream consumption. v1 retained; no directory roll.

---

## 1. Scope & boundaries

### 1.1 What Phase 13 owns

Phase 13 owns the texture estate that is not a framebuffer attachment:

1. **`_n`/`_s` companion atlases** for the block/item atlases — per-sprite companion discovery, full
   companion atlases whose layout and mip chain match the base atlas exactly, the missing-sprite
   default fills, candidates for units 2 and 3 during world rendering, and post-vanilla animation
   snapshots that keep companion frames in step; Phase 5 alone performs draw-time object binding.
2. **The generated noise texture** — the reproducible generator, `noiseTextureResolution²` RGB
   sizing, unit 15, and the `texture.noise=<path>` pack override.
3. **Pack custom textures** in all three App F.5 source forms, their `.mcmeta` filter/wrap sidecars,
   their per-stage expansion, retained target-specific candidates, and lifecycle across pack,
   option and resource reloads. Phase 5 performs effective-program compatibility and precedence.
4. **The `atlasSize` value source** — the ivec2 that is valid only while the atlas texture is bound.
5. **Texture publication production** — source preparation, allocation, upload, content identity,
   expected-id lease acquisition, ownership and retirement. Shared candidate/snapshot/lease/result
   shapes belong to Phase 5; source and parameterization content semantics belong to Phase 13.
6. **App E rows 10 and 11** — the `TextureMap` and `TextureAtlasSprite` hook sites Phase 7 deferred
   to this phase (`docs/phase7/v1/PHASE_7_DOC.md:1365–1366`: “DEFERRED(P13,v0.5)”).

### 1.2 Adjacent ownership — explicit "owned by Phase Y" lines

| Concern this phase touches | Owner outside Phase 13 |
|---|---|
| The sole fixed App B.3 name/policy/resolver, shared candidate/snapshot/lease/result shapes, full-shape compatibility and physical unit binding | **Phase 5**, `docs/phase5/v1/PHASE_5_DOC.md:2360–2363`; Phase 13 produces candidates and never assigns units |
| Uploading sampler integers for those units, and uploading the `atlasSize` ivec2 | **Phase 6**; Phase 13 is the *value source* for `atlasSize` only (`docs/design/v3/DESIGN.md:2479`–`:2481`) |
| Tangent-frame math and any shading that consumes the sampled normals | **Phase 10** (`docs/design/v3/DESIGN.md:2485`–`:2486`) |
| labPBR channel semantics — what the specular channels *mean* | **pack-side convention; engine-neutral.** G8 advertises it (`docs/design/v3/DESIGN.md:2486`); Phase 13 delivers bytes and interprets none of them |
| Parsing texture keys, normalized suffix disposition, retained sidecar references and noise options | **Phase 3** (`docs/phase3/v1/PHASE_3_DOC.md:1482-1510`); Phase 13 never reparses properties |
| Emitting `MC_NORMAL_MAP` / `MC_SPECULAR_MAP` | **Phase 3**, pending §5.3 R1; Phase 13 supplies a static preliminary value before load, not a completed plan |
| Alias-derived id values, held-item and entity id delivery | **Phase 9** |
| Frame/pass transactions, same-selector orchestration and coherent reload publication | **Phase 7**; Phase 13 contributes an owner, immutable publication, restricted lease source and hooks, not frame policy |
| Sampler objects, asynchronous transfers, PBO uploads, and any performance rework of these paths | **Phase 14** (`docs/design/v3/DESIGN.md:1686`–`:1687`) |
| Linked declaration merge, full `ProgramSamplerLayout`, fallback and opaque authenticated select-once capability | **Phase 4**; Phase 13 inspects published metadata only, never reconstructs a unit map |
| Scene definitions, capture, diffing, and fixture download | **Phase 2** |
| Options GUI presentation of anything above | **Phase 12** |

### 1.3 Hard boundaries

- Phase 13 allocates no framebuffer, performs no flip or clear, and attaches nothing. Every texture
  it owns is a sampled object.
- Phase 13 never assigns a texture unit. It retains exact names and Phase 5's `FixedSamplerName`
  lookup. Dynamic allocation remains rejected (`docs/design/v3/DESIGN.md:953-954`).
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
public interface TextureSystemFactory {
    TextureSystemCreationResult create(GLDevice gl, DiagnosticReporter diagnostics);
}
public sealed interface TextureSystemCreationResult {
    record Created(TextureSystem system) implements TextureSystemCreationResult {}
    record Failed(TextureFailure failure) implements TextureSystemCreationResult {}
}
public interface TextureLeaseSource {
    TextureLeaseResult lease(TextureOverlayPublicationId expected, ProgramBindingSelection selection);
}
public interface TextureSystem extends TextureLeaseSource {
    TexturePlanResult plan(TexturePlanRequest request);
    TextureBuildResult build(TextureBuildRequest request);
    AtlasSizeResult atlasSize(AtlasId atlas);
    AtlasSizeResult atlasSize();
    void close();
}
public sealed interface TextureLeaseResult {
    record Acquired(TextureOverlayLease lease) implements TextureLeaseResult {}
    record Rejected(TextureLeaseRejection reason) implements TextureLeaseResult {}
}
public enum TextureLeaseRejection {
    PUBLICATION_UNAVAILABLE, PUBLICATION_ID_MISMATCH,
    REGISTRY_FINGERPRINT_MISMATCH, STALE_SELECTION
}
public record PreliminaryCompanionDemand(
    boolean packActive, boolean fixedUnitCapabilityAvailable) {}
public record CompanionMacroState(boolean normalMap, boolean specularMap) {}
public final class CompanionMacroPolicy {
    public static CompanionMacroState preliminaryMacroState(PreliminaryCompanionDemand demand);
}
```

The factory creates an inactive owner, with `atlasSize` Unknown and no texture GL allocation.
Phase 7 alone retains build/close authority. Its active tuple exposes a non-owning publication
and restricted `TextureLeaseSource`, never a global current-publication observer. Planning is
pure/total: `Planned(TexturePlan)` or `Invalid(TextureFailure)`. Build re-runs planning, validates
the prepared-source/catalog pairing before allocation, and returns `Ready(TexturePublication)`
or `Failed(TextureFailure)` without a partial publication. Close retires first and defers owned
deletion until leases drain; it never blocks the render thread waiting for itself.

### 2.3 Key types and relationships

```java
public record TexturePlanRequest(
    PackConfiguration configuration,
    ProgramRegistryView registry,
    AtlasCatalog atlases,
    TextureSourceCatalog sources,
    CompanionPolicy companionPolicy,
    CompanionMacroState macroState,
    GLCapabilityProfile capabilities,
    RegistryFingerprint registryFingerprint,
    long estateGeneration,
    long registryGeneration,
    long resourceReloadEpoch) {}
public record TextureBuildRequest(TexturePlanRequest planRequest, TextureBuildSources sources) {}

public record AtlasId(String canonicalName) {}
public record AnimationFrameDescriptor(int sourceFrameIndex, int durationTicks) {}
public record SpriteAnimationMetadata(List<AnimationFrameDescriptor> frames, boolean interpolate) {}
public record SpriteDescriptor(
    String iconName, int originX, int originY, int width, int height,
    int frameCount, boolean animated, SpriteAnimationMetadata animation) {}
public record AtlasDescriptor(
    AtlasId id, int width, int height, int mipmapLevels, List<SpriteDescriptor> sprites) {}
public record AtlasCatalog(List<AtlasDescriptor> atlases) {}
public record SpriteAnimationState(
    String iconName, int sequencePosition, int currentSourceFrameIndex,
    int nextSourceFrameIndex, int elapsedTicks, int currentDuration, double nextFrameWeight) {}
public record AtlasAnimationSnapshot(
    AtlasId atlas, long resourceReloadEpoch, long tickSequence, List<SpriteAnimationState> sprites) {}

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
public sealed interface NoisePlan {
    record Generated(int resolution) implements NoisePlan {}
    record FromPack(NormalizedPackPath image, Optional<TextureSidecarRef> sidecar,
                    int declaredResolution) implements NoisePlan {}
    record Disabled() implements NoisePlan {}
}

public enum OwnedTextureSourceKind {
    PACK_PNG, MINECRAFT_DECODED_ASSET, RAW_BYTES,
    GENERATED_NOISE, COMPANION_RESOURCE, DEFAULT_FILL
}
public sealed interface TextureSourceIdentity {
    record OwnedUpload(OwnedTextureSourceKind sourceKind, String logicalSource,
        String contentDigest, String configurationIdentity) implements TextureSourceIdentity {}
    record ForeignLive(String exactResourceIdentity, long resourceReloadEpoch,
        long objectEpoch) implements TextureSourceIdentity {}
}
public record TextureParameterSpec(MinFilter minFilter, MagFilter magFilter, WrapMode wrap) {}
public record TextureParameterFingerprint(String value) {}
public record TextureSourceCatalog(long resourceReloadEpoch, List<TextureSourceAsset> assets) {}
public sealed interface TextureSourceAsset {
    record ReadyAsset(TextureSourceIdentity identity, TextureTarget target,
        List<Integer> dimensions, InternalTextureFormat format,
        DeclaredGlslType.Sampler shape, TextureParameterSpec parameters,
        String contentDigest, String sidecarDigest) implements TextureSourceAsset {}
    record FailedAsset(TextureFailure failure) implements TextureSourceAsset {}
}
public record TextureBuildSources(long resourceReloadEpoch, List<TexturePreparedSource> sources) {}
public sealed interface TexturePreparedSource {
    record Owned(TextureSourceAsset.ReadyAsset asset, TextureUploadPayload payload)
        implements TexturePreparedSource {}
    record Foreign(TextureSourceAsset.ReadyAsset asset, TextureHandle handle)
        implements TexturePreparedSource {}
}
public record CustomTexturePlanEntry(
    TextureBindingKey key, int phase3Ordinal, FixedSamplerName name, Set<StageId> stages,
    DeclaredGlslType.Sampler shape, TextureTarget target, TextureSourceIdentity source,
    TextureUploadSpec upload, TextureParameterSpec parameters,
    TextureParameterFingerprint parameterizationFingerprint) {}
public record TexturePlan(
    TexturePlanRequest inputs, List<CompanionAtlasPlan> companions, NoisePlan noise,
    List<CustomTexturePlanEntry> customTextures, List<UnsupportedBinding> unsupported,
    CompanionMacroState macroState, TextureMemoryEstimate memory) {}
public record TexturePublication(
    TextureOverlayPublicationId id, RegistryFingerprint registryFingerprint,
    long registryGeneration, long resourceReloadEpoch, TexturePlan plan,
    TextureCandidateTable candidates) {}
```

`TexturePlanRequest.registry` is the exact handle-free detached view Phase 7 uses for Phase 5;
its fingerprint must equal `registryFingerprint`; the transaction must attest that it was compiled
from this exact `configuration` (ProgramRegistryView has no configuration accessor),
and `samplerPolicyFingerprint()` must equal Phase 5's policy even for an empty registry.
`estateGeneration` is the actual accepted Phase 5 generation, never the registry generation.
The caller supplies the publisher's actual accepted `registryGeneration`, not a guessed successor.
`TexturePlan.inputs` freezes this pairing and metadata; no plan/fingerprint contains a handle.
`TextureUploadPayload` is immutable decoded pixels/raw transfer bytes plus ordered mip/frame
payloads; each source is paired with its exact `ReadyAsset`, not an unkeyed byte array.
Owned/foreign prepared variants must agree with identity kind and catalog metadata; missing,
extra, duplicate, changed-content, capability or epoch mismatches fail before allocation.
Catalog preparation is mod glue over Phase 3 references without properties reparsing.
Generated/default/companion payloads use the same catalog identity discipline.
The chosen `macroState` is an explicit request input retained unchanged by TexturePlan: an R4
physical-allocation optimization cannot recreate or alter preprocessing provenance. For a
ForeignLive ReadyAsset, contentDigest is the canonical logical source/reload/object identity
digest, never a hash of mutable live pixels; sidecar absence has a distinct canonical tag.
CustomTexturePlanEntry.upload is an object-capability descriptor even for a foreign source;
the Foreign prepared branch validates it but never issues an upload to borrowed storage.

All lists are immutable and canonical. Atlas/sprite order is unsigned UTF-8 identity order;
frame sequences preserve animation sequence order (not source-frame index order). Frame indices
are nonnegative and in copied decoded-pixel bounds; durations are positive. Nonanimated sprites
carry one frame and `interpolate=false`. No Minecraft object crosses this boundary.

The following **Phase-5-owned** protocol is incorporated exactly, not re-owned here:

```java
public record TextureBindingCandidate(
    CandidateOrigin origin, StageId expandedStage, String exactSamplerName,
    FixedSamplerName name, DeclaredGlslType.Sampler shape, TextureTarget target,
    TextureHandleRef handle, TextureSourceIdentity source, TextureParameterSpec parameters,
    TextureParameterFingerprint parameterizationFingerprint, int candidateOrdinal) {}
public sealed interface CandidateOrigin {
    record Custom(TextureBindingKey key, int phase3Ordinal) implements CandidateOrigin {}
    record Companion(AtlasId atlas, CompanionKind kind) implements CandidateOrigin {}
    record Noise() implements CandidateOrigin {}
}
public sealed interface TextureHandleRef {
    record Owned(TextureHandle handle) implements TextureHandleRef {}
    record Borrowed(TextureHandle handle) implements TextureHandleRef {}
}
public interface TextureCandidateTable {
    TextureCandidateEntry entry(StageId expandedStage, FixedSamplerName name);
}
public sealed interface TextureCandidateEntry {
    record Candidates(List<TextureBindingCandidate> candidates) implements TextureCandidateEntry {}
    record Absent(TextureOverlayAbsence reason) implements TextureCandidateEntry {}
}
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
    boolean isCurrent();
    void close();
}
```

Publication values may carry Phase 1 opaque handles. `Owned` means **Phase 13** owns the object,
never that a consumer does; `Borrowed` means vanilla/foreign ownership. Phase 5 owns framebuffer
and neutral objects and performs final physical selection. `isCurrent()` is true only for an
open lease whose issuing owner still has that very publication READY, not RETIRING/CLOSED.
Equal content IDs cannot revive a retired lease. Phase 5's fixed-name/policy/result schemas are
incorporated in §5 alongside the precise consuming operations.

### 2.4 Invariants

1. Fixed names are exact and case-sensitive; only Phase 5 assigns units 0–15. No free-unit search.
2. One immutable candidate cell retains every target; no publication collapses it to one handle.
3. One authenticated Phase 4 selection flows through lease, Phase 5 snapshot/bind and activation.
4. Acquired leases defer owned deletion, not stale drawing. Bound alone transfers closure duty.
5. Same base/companion extent, origin and mip chain; vanilla objects are never rewritten/deleted.
6. Phase 3 alone parses properties. Phase 13 opens retained image/raw/sidecar references only.
7. Failed active-pipeline replacement transitions shaders off; a retiring old publication is not
   a usable fallback pipeline. Content identity and generation authority are separate.

---

## 3. Contract conformance map

Each row independently cites a repository-relative source. The named hooks are **future
architectural checks**, specified with input → observable outcome in §8, not tests run here.

### 3.1 Appendix F.5 — custom textures and noise

| # | Contract → design | Provenance | Check |
|---|---|---|---|
| F5-1 | Parsed normalized key → §4.3.1 exact name retained | `[V:doc]` `docs/research/v1/RESEARCH.md:1484-1484`; `docs/phase3/v1/PHASE_3_DOC.md:1482-1488` | `sharedUnit_exactNameAndOrdinal` |
| F5-2 | Numeric discriminator remains separate → original key/ordinal on each candidate | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:117-117`; `docs/phase3/v1/PHASE_3_DOC.md:1485-1486` | `sharedUnit_exactNameAndOrdinal` |
| F5-3 | Pack-relative PNG → owned decoded upload, §4.3.2 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:91-91` | `publication_contentIdentity` |
| F5-4 | Minecraft decoded asset → owned upload, §4.3.2 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:95-95` | `publication_contentIdentity` |
| F5-5 | Dynamic lightmap → borrowed live object, §4.3.2 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:98-98` | `publication_foreignReloadIdentity` |
| F5-6 | Atlas resource → borrowed live object, §4.3.2 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:99-99` | `publication_foreignReloadIdentity` |
| F5-7 | `_n`/`_s` source variants → companion/default identity, §4.3.2 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:100-101` | `companion_discoveryPerSprite` |
| F5-8 | Raw path/target/internal format/dimensions/transfer → §4.3.3 source-bearing upload | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:104-110` | `sharedUnit_programSpecificTargets` |
| F5-9 | Four raw targets retain arities 1/2/3/2 → §4.3.3 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:106-110`; `docs/phase3/v1/PHASE_3_DOC.md:1493-1498` | `sharedUnit_fullSamplerShape` |
| F5-10 | Several target types per fixed unit, one type per program → §4.3.4 Phase4 layout + Phase5 selection | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:112-116`; `docs/research/v1/RESEARCH.md:1488-1490` | `sharedUnit_programSpecificTargets`, `sharedUnit_incompatibleAliasesBeforeBind` |
| F5-11 | Sidecar blur → §4.3.5 effective filter | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:118-119`; `docs/phase3/v1/PHASE_3_DOC.md:750-750` | `custom_mcmetaBlurSetsFilter` |
| F5-12 | Sidecar clamp → §4.3.5 effective wrap | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:118-119`; `docs/phase3/v1/PHASE_3_DOC.md:750-750` | `custom_mcmetaClampSetsWrap` |
| F5-13 | Gbuffers → gbuffers+shadow copies, §4.3.1 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:83-83` | `custom_stageExpansionExact` |
| F5-14 | Deferred → deferred only, §4.3.1 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:84-84` | `custom_stageExpansionExact` |
| F5-15 | Composite → composite+final copies, §4.3.1 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:85-85` | `custom_stageExpansionExact` |
| F5-16 | Pack noise override → §4.2.4 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:124-124`; `docs/research/v1/RESEARCH.md:1491-1491` | `noise_packOverrideReplacesGenerated` |
| F5-17 | Fullscreen colortex/gaux overrides → §4.3.4 matching custom wins fixed backing | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:86-91`; `[D-P13-15]` PD §11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-629` | `custom_fullscreenFixedOverrides` |

### 3.2 Appendix B.3 — fixed units

| # | Contract → design | Provenance | Check |
|---|---|---|---|
| B3-1 | `normals`2 world/shadow → §4.1.5 companion candidate | `[V:doc]` `docs/research/v1/RESEARCH.md:1234-1234` | `sharedUnit_fixedRangeAndShadowAlias` |
| B3-2 | `specular`3 world/shadow → §4.1.5 companion candidate | `[V:doc]` `docs/research/v1/RESEARCH.md:1235-1235` | `sharedUnit_fixedRangeAndShadowAlias` |
| B3-3 | `noisetex`15 wired shader domains → §4.2.5; virtual/unwired explicit | `[V:doc]` `docs/research/v1/RESEARCH.md:1247-1247` | `sharedUnit_fixedRangeAndShadowAlias` |
| B3-4 | gaux1–4 at7–10, aliases/fullscreen names retained → §4.3.4 | `[V:doc]` `docs/research/v1/RESEARCH.md:1239-1242` | `custom_fullscreenFixedOverrides` |
| B3-5 | Generated companions world/shadow only; not a prohibition on fullscreen custom2/3 → §4.1.5 | `[V:observed]` `docs/research/v1/RESEARCH.md:595-596`; `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:464-465` | `custom_stageExpansionExact` |
| B3-6 | Fixed map, depthtex1=11, gbuffers12 unused; conditional shadow from effective watershadow → Phase5 resolver | `[D-P13-16]` `docs/design/v3/DESIGN.md:953-954`; `docs/research/v1/RESEARCH.md:1236-1255`; `docs/phase6/v1/PHASE_6_DOC.md:993-998` | `sharedUnit_fixedRangeAndShadowAlias` |

### 3.3 Texture-system rows

| # | Contract → design | Provenance | Check |
|---|---|---|---|
| T-1 | Per-sprite `_n`/`_s` discovery → §4.1.2 | `[V:observed]` `docs/research/v1/RESEARCH.md:593-594` | `companion_discoveryPerSprite` |
| T-2 | Full companion atlases and matching mip chains → §4.1.3 | `[V:observed]` `docs/research/v1/RESEARCH.md:594-595`; `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:459-463`; `[D-P13-4]` PD §11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:631-637` | `companion_layoutAndMipChainMatchBase` |
| T-3 | Literal normal default `0xFF7F7FFF` → §4.1.4 unresolved C-TX01 byte order | `[V:observed]` `docs/research/v1/RESEARCH.md:595-595`; `[D-P13-5]` `docs/design/v3/DESIGN.md:1088-1088` | `companion_missingNormalUsesContractDefault` |
| T-4 | Zero specular → §4.1.4 | `[V:observed]` `docs/research/v1/RESEARCH.md:595-595`; PD §11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:632-632` | `companion_missingSpecularUsesZeroDefault` |
| T-5 | World-stage companion use → §4.1.5 | `[V:observed]` `docs/research/v1/RESEARCH.md:595-596` | `custom_stageExpansionExact` |
| T-6 | RGB resolution² xorshift noise15 → §4.2 | `[V:observed]` `docs/research/v1/RESEARCH.md:596-597`; `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:469-474` | `noise_signedRecurrence` |
| T-7 | Companion animation matches base → §4.1.7 post-vanilla snapshot | `[D-P13-8]` `docs/design/v3/DESIGN.md:2482-2483`; PD §11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:631-637`. No permitted positive observed synchronization reference was found | `animation_postVanillaSnapshot` |

### 3.4 Uniform and option rows

| # | Contract → design | Provenance | Check |
|---|---|---|---|
| D-1 | Bound atlas extent as ivec2 → §4.4 Known/Unknown | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:177-177`; `[D-P13-9]` PD §11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:638-639` | `atlasSize_valueAndValidityWindow` |
| F3-1 | Noise absent baseline disabled/256 → §4.2.1 | `[V:doc]` `docs/phase3/v1/PHASE_3_DOC.md:1543-1544`; `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:422-422` | `noise_resolutionFromRequirementsAndBaseline` |
| M-1 | Normal macro option → §4.1.6 preliminary producer and requested Phase3 input | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:655-655`; `[D-P13-10]` PD §7.6, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:470-474` | `macro_beforeJcppAndUngrant` |
| M-2 | Specular macro option → §4.1.6 preliminary producer and requested Phase3 input | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:656-656`; `[D-P13-10]` PD §7.6, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:470-474` | `macro_beforeJcppAndUngrant` |

### 3.5 Hook and Pintonium do-not-inherit ledger

PD rows record evidence, a row-local decision and a contract check; none adopts the decompile's
identifiers or a dependency's ungranted implementation.

| # | Evidence and decision | Contract check → design / future outcome |
|---|---|---|
| E-10 | `[V:mcp]` `docs/research/v1/RESEARCH.md:1410-1410` | §4.6 H13-ATLAS-01..04 → stitch catalog/size and post-tick snapshot |
| E-11 | `[V:mcp]` `docs/research/v1/RESEARCH.md:1411-1411` | §4.6 H13-SPRITE-01 read-only metadata/counter accessors; H13-SPRITE-02 dormant |
| PD-1 | `[V:observed — Pintonium targets/backed/NoiseTexture]` PD §11/§18, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:621-625`, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:815-815`; `[D-P13-3]` | RESEARCH `docs/research/v1/RESEARCH.md:596-597` requires xorshift, not Random(0) → `noise_signedRecurrence` |
| PD-2 | `[V:observed — Pintonium]` PD §18, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808-808`; `[D-P13-16]` | App B.3 `docs/research/v1/RESEARCH.md:1228-1255` rejects dynamic units → `sharedUnit_fixedRangeAndShadowAlias` |
| PD-3 | `[V:observed — Pintonium]` PD §7.4/§11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:447-448`, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-630`; `[D-P13-6]` | Phase3 `docs/phase3/v1/PHASE_3_DOC.md:749-750` strips key suffix, separately retains sidecar; DESIGN `docs/design/v3/DESIGN.md:2476-2478` requires honoring suffix → U1 pending, sidecar checks cover only granted retention |
| PD-4 | `[V:observed — Pintonium texture/pbr/PBRTextureManager]` PD §11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:631-637`; `[D-P13-4]` | RESEARCH `docs/research/v1/RESEARCH.md:594-595` requires full atlases, not per-bound-id keying → `companion_layoutAndMipChainMatchBase` |
| PD-5 | `[V:observed — Pintonium uniforms]` PD §11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:638-639`; `[D-P13-9]` | Shipped `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:177-177` requires real bound extent → `atlasSize_valueAndValidityWindow` |
| PD-6 | `[V:observed — Pintonium PackRenderTargetDirectives]` PD §17, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:799-799`; `[D-P13-12]` | Accepted atlas cost `docs/design/v3/DESIGN.md:2499-2500`; post-analysis unused allocation may be skipped, never change pre-jcpp macro policy → `macro_beforeJcppAndUngrant` |
| PD-7 | `[V:observed — Pintonium ProgramSamplers]` PD §17, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:796-796`; `[D-P13-17]` | App F.5 required binding `docs/research/v1/RESEARCH.md:1488-1490` needs closed outcomes, not false boolean → `binding_absenceVersusIncompatibility` |
| PD-8 | `[V:observed — Pintonium]` PD §11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-629`; `[D-P13-15]` | `docs/design/v3/DESIGN.md:2471-2476` and `docs/research/v1/RESEARCH.md:1488-1490`: accept required v0.5 shared-unit capability through exact pack names, effective linked layouts and typed candidates; reject only generated customtexN names/source patching → `sharedUnit_programSpecificTargets` |

### 3.6 Input contradictions and binding rulings

1. Phase3 publishes the base sampler **"after removing a recognized terminal filter/wrap suffix"**
   and says it is **"stripped and ignored"** (`docs/phase3/v1/PHASE_3_DOC.md:1483-1485`;
   conformance `docs/phase3/v1/PHASE_3_DOC.md:749-749`). Separately, it retains the normalized
   sidecar without opening/interpreting it (`docs/phase3/v1/PHASE_3_DOC.md:1498-1500`;
   `docs/phase3/v1/PHASE_3_DOC.md:750-750`). Phase13 accepts this normalized boundary, never
   reconstructs discarded settings, and honors retained sidecars. DESIGN says suffixes
   **"are stripped and ignored there"** and **"ours must honor them"**
   (`docs/design/v3/DESIGN.md:2476-2478`). These incompatible statements remain routed to
   Phase3/DESIGN U1, not silently reconciled. PD explicitly supports "pack PNG with `.mcmeta`
   blur/clamp" (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-626`); no claim that PD
   ignores sidecars, or that unlisted suffix behavior cannot exist, survives. `[D-P13-6]`
2. Phase5's old narrow domain was an architectural shortfall. The coordinated full
   `FixedSamplerName`/candidate/binding contract now fulfills R2 architecturally, including
   colortex fullscreen overrides. D-P13-11's legal-input fallback is historical/superseded.
   Fresh verification and Phase6/8 consumer grants remain implementation gates.
3. Review36 cleared the original Phase7 gate, and Review38 certified prior Phase5 bytes
   (`docs/phase7/reviews/PHASE_7_REVIEW_36.md:107-123`,
   `docs/phase5/reviews/PHASE_5_REVIEW_38.md:61-62`). Neither certifies this changed §5.
   Phase3 remains provisional under applied Review36 resolutions and no fresh PASS
   (`docs/phase3/reviews/PHASE_3_REVIEW_36.md:270-292`, `docs/phase3/reviews/PHASE_3_REVIEW_36.md:322-328`).
4. C-TX01 remains unresolved: RESEARCH's `0xFF7F7FFF` wins over PD's `0x7F7FFFFF`
   (`docs/research/v1/RESEARCH.md:595-595`, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:632-632`,
   `docs/design/v3/DESIGN.md:1088-1088`). §4.1.4 states byte-order assumption without silently swapping it.

---
## 4. Detailed design

### 4.1 Companion `_n`/`_s` atlases

#### 4.1.1 Demand and enablement

Before controller-owned Phase3 load, Phase7 computes `PreliminaryCompanionDemand` solely from
selected-pack/off state and fixed-unit capabilities. `CompanionMacroPolicy.preliminaryMacroState`
is static, pure and total: both output booleans equal
`demand.packActive() && demand.fixedUnitCapabilityAvailable()`. It needs no owner, atlas, GL,
configuration, registry, linked declarations or completed plan. Choose ALWAYS_ON_FALLBACK before
preprocessing; no post-jcpp `ResourceRequirements` can be a preprocessor producer.

```java
public record CompanionPolicy(boolean normalsEnabled, boolean specularEnabled,
                              CompanionDemandSource source) {}
public enum CompanionDemandSource { DECLARED_SAMPLERS, ALWAYS_ON_FALLBACK, CAPABILITY_GATED_OFF }
```

After analysis, R4 may avoid allocating an unused physical companion by setting DECLARED_SAMPLERS,
using published demand, without changing either already-chosen macro boolean. Without R4, both
companions are allocated for active/capable packs (the accepted cost,
`docs/design/v3/DESIGN.md:2499-2500`); otherwise CAPABILITY_GATED_OFF. Build failure cannot change
already-preprocessed source policy: it follows the coherent transaction's failure rule.

#### 4.1.2 Sprite companion discovery

For each base sprite and enabled kind, append `_n`/`_s` to the final resource path segment before
the extension. Mod glue prepares immutable decoded frame pixels, full animation sequence metadata
and source/content identity at stitch time. The handle-free catalog exposes readiness and metadata;
the paired build sources carry payloads, never a presence bit pretending to contain animation state.

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
This is §2.4's base/companion layout invariant: a full atlas rather than a
per-texture side table lets the pack sample `texture`, `normals`, and `specular` with the same
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
| `NORMALS` | `0xFF7F7FFF` | the contract default for a missing normal sprite — the literal value at `docs/research/v1/RESEARCH.md:595` |
| `SPECULAR` | `0x00000000` | zero specular — `docs/research/v1/RESEARCH.md:595` |

The normal default is interpreted as a packed RGBA quadruple whose components, in the order the
uploader writes them, are `(0xFF, 0x7F, 0x7F, 0xFF)` — i.e. the byte pattern is written most
significant byte first. `[A]` Under that literal reading the value does **not** decode to the
(0, 0, 1) flat normal the phrase "flat normal" would imply; R=0xFF, G=0x7F, B=0x7F decodes to a
normal along +X. That tension is left visible rather than removed by re-encoding, and it is exactly
what `companion_missingNormalUsesContractDefault` and a C-TX01 escalation exist to resolve:
§G12.4's C-TX01
records that "representation/byte order remains unresolved and is never silently swapped"
(`docs/design/v3/DESIGN.md:1088`), so the assumption is stated in exactly one place, tagged `[A]`,
and covered by a dedicated test (`companion_missingNormalUsesContractDefault`) that a T2 run can
contradict without disturbing anything else. RESEARCH's literal value is never edited to match an
implementation.

#### 4.1.5 Publication and stage applicability

Generate `CandidateOrigin.Companion(atlas,kind)` candidates with exact `normals`/`specular` names
and `FixedSamplerName.NORMALS`/`SPECULAR` only for GBUFFERS and SHADOW. They retain actual uploaded
sample/target/comparison capability. No `TextureBindingKey` is fabricated. Disabled kinds yield
`Absent(NOT_CONFIGURED)`; other stage columns yield `Absent(NOT_APPLICABLE_TO_STAGE)`.
An unavailable owner/publication is `PUBLICATION_UNAVAILABLE`, not a present empty table.

Phase5 resolves those names to2/3 and selects a compatible object only when declared by the
effective provider. Generated companions do not enter fullscreen columns, but compatible custom
colortex2/colortex3/gnormal/composite candidates **may override** fullscreen units2/3. A required
missing object suppresses only the program under the typed bind result; no unrelated fallback.

#### 4.1.6 `MC_NORMAL_MAP` / `MC_SPECULAR_MAP` wiring

§2.2 publishes the static preliminary producer and both boolean records. Phase7 adapts its result
to the **requested**, Phase3-owned `CompanionOptionMacros(boolean normalMap,boolean specularMap)`
input before load/jcpp. R1 specifies the exact new PackLoadRequest component and same-build
materialization/fingerprint propagation in §5.3. `TexturePlan.macroState` is provenance of the
already-chosen state, never its producer.

Current Phase3 materialization accepts only `Empty` or `DefineCenterDepthSmooth`
(`docs/phase3/v1/PHASE_3_DOC.md:532-640`) and load runs jcpp before publishing configuration
(`docs/phase3/v1/PHASE_3_DOC.md:644-666`). The singular Phase6 contributor remains untouched.
Until R1 is granted, invoke the unchanged load API, emit neither macro and never patch later
shader text. PBR macro conformance remains explicitly blocked, not “available by construction.”
The placement decision `[D-P13-10]` checks the shipped meaning
(`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:655-656`) against PD §7.6's macro family
(`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:470-474`); it does not adopt a new macro bag.

#### 4.1.7 Sprite animation

`[D-P13-8]` is a design requirement, not observed synchronization. At stitch, copy decoded source
frames/mips and `SpriteAnimationMetadata`; a reordered sequence and unequal durations remain
lossless. Nonanimated sprites carry one frame and no interpolation. All-default companions need
no per-tick upload.

H13-ATLAS-04 runs at vanilla `TextureMap.updateAnimations` TAIL. Mod glue reads base sprite
metadata/counters through H13-SPRITE-01 after vanilla advances; it creates one
`AtlasAnimationSnapshot(atlas,resourceReloadEpoch,tickSequence,sprites)`. Each state records
sequence position, current and next source-frame index, elapsed ticks, current duration and exact
double next-frame weight: elapsed/currentDuration if interpolating, otherwise0. The next sequence
position wraps through the sequence's frame map. No parallel engine clock is advanced.

Before upload validate resource epoch, exact atlas/sprite identities, frame-map membership,
durations, elapsed bounds, finite/matching weight and monotonically increasing tick sequence.
Stale snapshots are discarded with no upload. Unavailable/invalid live state or missing/failed
hook disables animation to frame0 with one diagnostic; a failure after previous ticks **restores**
frame0 rather than freezing the last frame. Valid snapshots drive matching companion source frames,
weight and mip regions in one render-thread batch. Reload invalidates metadata, copied pixels,
snapshots and atlasSize before vanilla replacement. No Minecraft object reaches the engine.

### 4.2 The noise texture

#### 4.2.1 Sizing and enablement

Resolution and enablement come from Phase 3's `ResourceRequirements.noise`, a
`NoiseRequirement(boolean enabled, int resolution)` (`docs/phase3/v1/PHASE_3_DOC.md:1159`, bound at
`:1533`) whose absent-directive baseline is disabled with resolution 256 (`:1543`–`:1544`). The texture is
`resolution × resolution`, internal format RGB, unsigned-byte transfer, wrap `REPEAT`, filter
`LINEAR` — the contract's noise sampling is a repeating field sampled with interpolation
(`docs/research/v1/RESEARCH.md:596`–`:597`; behavioral corroboration at
`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:469`–`:471`).

#### 4.2.2 The generator, specified so it is reproducible

Every shift, XOR, add, subtract and multiply wraps to signed32 at that operation boundary.
Right shift is arithmetic `>>17`. Signed remainder truncates quotient toward zero.

```
xorshift(s):          s = s XOR (s << 13)
                      s = s XOR (s >> 17)
                      s = s XOR (s << 5)
                      return s
channelSeed(x,y,c):   return (xorshift(x) + xorshift(y * 19)) * xorshift(c * 23) - c
channel(x,y,c):       state = xorshift(channelSeed(x,y,c))
                      remainder = state % 128
                      upload low eight bits of remainder as unsigned byte
```

No absolute value and no unconditional0–127 byte guarantee. Required vectors:
`xorshift(-1)=253983`; at `(x,y,c)=(1,1,1)`, final state `-1828175987`, signed remainder
`-115`, uploaded byte `141`. These are arithmetic vectors, not validation of a Java implementation.
The recurrence alone is corroborated by the permitted behavioral digest
(`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:469-474`).
Row-major y/x ordering and channels c=0,1,2 in RGB order are explicit design conventions.
Generated source identity includes resolution and this signed-recurrence schema tag.

#### 4.2.3 The rejected alternative, and the one route back

Pintonium seeds `java.util.Random(0)` and takes its bytes
(`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:621`–`:622`)
`[V:observed — Pintonium]`. That is a **pre-decided
rejection** in §G11.4's standing list — "`Random(0)` noise (ours: contract generator)"
(`docs/design/v3/DESIGN.md:955`) — and it is not revisited here. `[D-P13-3]`

PD frames the repeatable-field question at
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:622-625`.
The Phase 13 spec states the single route back: the contract question is reopened only through the
§G0.1 conflict rule, "never silently" (`docs/design/v3/DESIGN.md:2464`–`:2466`). Concretely, that route
is a T2 pixel-parity failure on a noise-sampling classic pack that a byte-identical generator fixes —
evidence that would be reported as a RESEARCH conflict, not resolved inside an implementation. This
design does not take that route and does not need to: it specifies the contract generator.

#### 4.2.4 The pack override

`NoiseTextureSpec.Override(image, sidecar)` (`docs/phase3/v1/PHASE_3_DOC.md:1502`–`:1503`) replaces the
generated texture entirely: the pack image is decoded, uploaded, and parameterized from its own
`.mcmeta` sidecar if present (§4.3.5), otherwise with the same `REPEAT`/`LINEAR` defaults as the
generated texture. `noiseTextureResolution` does not resize an override; the image's own dimensions
win, because the pack authored it. An override that fails to decode falls back to the generated
texture and diagnoses once — a pack that asked for noise still gets noise.

#### 4.2.5 Publication

Publish `CandidateOrigin.Noise()` under exact `noisetex`/`FixedSamplerName.NOISETEX` for every
supported wired shader stage, with actual RGB sample/target capability and source/parameter digest.
Phase5 resolves15. Disabled noise is `Absent(NOT_CONFIGURED)`; required missing backing degrades
the program rather than binding an unrelated object. Virtual steps have no bindings;
compute/unwired domains remain typed unsupported, not silently composite.

### 4.3 Pack custom textures

#### 4.3.1 From normalized key to candidates

Consume Phase3's `TextureBindingKey(stage,sampler,duplicateDiscriminator)` unchanged.
Recognized terminal filter/wrap suffixes have already been stripped and ignored
(`docs/phase3/v1/PHASE_3_DOC.md:1482-1488`, conformance `docs/phase3/v1/PHASE_3_DOC.md:749-750`).
Do not reparse properties or reconstruct discarded settings.

Enumerate the immutable custom-spec list in Phase3 canonical order: property-stage order,
unsigned-UTF8 sampler, absent discriminator then0..9, source kind PackPath/MinecraftResource/Raw.
Complete-key duplicate last-valid-wins has **already** happened before sorting
(`docs/phase3/v1/PHASE_3_DOC.md:1504-1508`). Assign one zero-based canonical `phase3Ordinal` per
original list entry; every expanded copy retains it and the entire original key.
Expand GBUFFERS→GBUFFERS+SHADOW, DEFERRED→DEFERRED, COMPOSITE→COMPOSITE+FINAL.
Call Phase5's pure exact-name lookup for `FixedSamplerName`; retain exact key/name and enum,
never a unit. SHADOW's conditional alias stays unresolved until the effective provider layout.
No lowercasing, generated sampler names, or source patching occurs.
For published candidates, `candidateOrdinal` equals `phase3Ordinal` for custom origins. After
the complete custom-list range, companion origins receive consecutive ordinals in unsigned-UTF8
AtlasId canonicalName order, then CompanionKind declaration order; Noise receives the next
ordinal. Expanded copies retain the same ordinal. Absent/generated-disabled entries do not
become candidates, but their canonical plan/absence values remain fingerprint inputs. This
producer ordering never ranks a companion or noise ahead of a compatible custom candidate.

#### 4.3.2 Source forms and content identity

| Phase3 source | Prepared source / ownership |
|---|---|
| `PackPath(key,image,sidecar)` | PACK_PNG OwnedUpload: immutable input digest, decoded2D payload, effective sidecar parameters |
| `MinecraftResource(key,resourceIdentity)` decoded asset | MINECRAFT_DECODED_ASSET OwnedUpload: exact logical resource, immutable byte digest and decoded parameters |
| Same variant naming dynamic/lightmap or atlas | ForeignLive: borrowed live object, exact resource identity, reload epoch and logical object epoch; never copy/allocate/delete vanilla storage |
| `Raw(key,bytes,target,internalFormat,dimensions,pixelFormat,pixelType,sidecar)` | RAW_BYTES OwnedUpload plus exact target/format/dimensions/transfer and byte digest |

The source grammar is `docs/phase3/v1/PHASE_3_DOC.md:1490-1500`; live forms and variants are
documented at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:95-101`.
`_n`/`_s` selects the companion atlas or companion resource; an absent asset variant uses an owned
1×1 default-fill object with DEFAULT_FILL identity, not a foreign fabricated handle.
`OwnedUpload.configurationIdentity` is the canonical configuration fingerprint encoding.
`contentDigest` hashes actual immutable input bytes and effective decoded/upload inputs, not
only a path. Raw transfer inputs and sidecar outcome/effective filter/wrap participate.
Companion identity includes ordered sprite origins/extents/mips, source/default content,
animation metadata and atlas resource epoch; noise includes recurrence schema and resolution.

`ForeignLive.objectEpoch` is a monotonic logical replacement counter issued by the resource adapter,
never a GL name. Resource reload increments resourceReloadEpoch even for equal resource strings.
In-place animation/lightmap pixel updates do not change objectEpoch or republish every tick.
Candidate metadata describes actual borrowed/uploaded sample/target/comparison capability.
§2.3's catalog/build pairing rejects changed bytes, object epochs or metadata before allocation.

#### 4.3.3 Raw upload and full capabilities

Phase3 validates grammar, four target arities and integer transfer compatibility
(`docs/phase3/v1/PHASE_3_DOC.md:1493-1498`). Phase13 validates checked byte-size products,
exact payload length, each dimension against target-specific capability limits, and effective
internal format through Phase5's published App B.4 policy. No overflow may turn an invalid size
into an accepted allocation. An entry-local source failure becomes catalog `FailedAsset` evidence;
it is not a source-less upload request.

`TextureUploadSpec` is the closed sum `OneD(width,internalFormat,pixelFormat,pixelType)`,
`TwoD(width,height,internalFormat,pixelFormat,pixelType)`,
`ThreeD(width,height,depth,internalFormat,pixelFormat,pixelType)`,
`Rectangle(width,height,internalFormat,pixelFormat,pixelType)`, with positive dimensions and
Phase3's closed format types. Prepared payloads carry matching transfer metadata; decoded PNG
is TwoD. Fingerprints include the effective validated values.

Compatibility is not dimension alone. D1/D2/D3/RECTANGLE correspond to the four Phase3 targets;
FLOAT/SIGNED_INT/UNSIGNED_INT must match the effective internal format. Shadow comparison
requires actual depth-comparison-capable storage and matching comparison state. Arrayed,
multisample, CUBE, BUFFER and sampler-containing arrays/structs have no AppF5 custom-target
coercion. Ordinary Phase5 backing objects carry the same typed capabilities. Unsupported shapes
produce closed degradation, never an arbitrary sampler2D cast.

#### 4.3.4 Effective-provider shared-unit selection

Phase4 alone merges declarations and publishes complete `ProgramSamplerLayout` on the effective
descriptor. Direct samplers retain every Phase3 field; sampler-containing aggregates remain
lossless and unsupported, never dropped/coerced. Compile-time validation uses Phase5's pure
policy before shader/program creation; incompatible same-unit types retain typed
SAMPLER_LAYOUT/SAMPLER_UNIT_TYPE_CONFLICT evidence and only that provider follows fallback.
Unsupported names/domains/shapes similarly retain SAMPLER_LAYOUT_UNSUPPORTED evidence.
Fallback publishes the ancestor's entire layout/state/sources, never a requested-child overlay.
Driver optimization does not prune declarations.

Phase13 retains **all** target-specific candidates per `(expandedStage,FixedSamplerName)` cell.
Phase5 owns the following selection, after authenticating selection/snapshot/publication:

1. Use selector.effectiveDescriptor/layout, effectiveStage and actualBand; resolve each declared
   exact name through the sole fixed resolver. Aliases share unit identity, never source keys.
2. Filter candidates by full sample/target/flags. Rank compatible Custom entries only by canonical
   phase3Ordinal; greatest wins. This is `[D-P13-19]`, a new decision for distinct discriminators,
   not a claim that Phase3 exposes original property occurrence order.
3. With no compatible custom, distinguish NO_CANDIDATE from INCOMPATIBLE_CANDIDATE. Select only a
   compatible companion/noise/estate/foreign/neutral backing, retaining that diagnostic. A matching
   custom overrides every fixed backing, including fullscreen colortex and deferred gaux.
4. Different declaration shapes on a shared unit are CONFLICTING_SAMPLER_TYPES. Same-shape aliases
   compare final source/object and parameterization identity: different winning objects yield
   CONFLICTING_CANDIDATES; identical object identity coalesces while retaining every exact name
   for integer uploads. Traversal order cannot choose an alias winner.
5. Produce exactly sixteen rows0–15, one compatible object per declared used unit or Unused.
   Missing required backing aggregates into Degraded/SUPPRESS_DRAW; no drawable missing row.
   No free unit search. Undeclared units stay Unused even if candidates exist.

For the shipped raw1D `texture.composite.gaux1` and raw3D `.2` example
(`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:112-116`), both remain in one cell.
A sampler1D provider chooses the first object on7; sampler3D chooses the second on7; both names
stay gaux1. This is the required v0.5 typed equivalent to generated customtexN/source patching,
not G8-only work. `[D-P13-15]`

FixedFunctionEmpty has no declarations/custom inference. Non-final fixed draws use vanilla state;
FINAL's existing colortex0 passthrough uses `BindingPurpose.FIXED_FUNCTION_PASSTHROUGH`, binds the
compatible frozen Phase5 side on0 and uploads no shader sampler integer. Purpose is SHADER,
FIXED_FUNCTION_PASSTHROUGH or NONE; virtual metadata is VirtualNotApplicable and bypasses select.

#### 4.3.5 `.mcmeta` filter and wrap

Phase3 retains `TextureSidecarRef` without opening it
(`docs/phase3/v1/PHASE_3_DOC.md:750-750`, `docs/phase3/v1/PHASE_3_DOC.md:1498-1500`).
This is independent of property-key suffix stripping at `docs/phase3/v1/PHASE_3_DOC.md:749-749`.
Phase13 opens/interprets the sidecar, never properties:

| Field | Absent / false | true |
|---|---|---|
| `texture.blur` | NEAREST | LINEAR |
| `texture.clamp` | REPEAT | CLAMP_TO_EDGE |

§2.3's `TextureParameterSpec` is canonical effective min/mag/wrap state. Apply it at owned upload;
malformed/unreadable sidecar uses defaults with one diagnostic and fingerprints that outcome.
Noise override without sidecar retains §4.2.4's LINEAR/REPEAT baseline. Parameterization digest
has a distinct SHA-256 domain/schema tag. Borrowed objects expose actual foreign parameters;
consumers do not mutate them to make incompatible requests appear compatible.
Phase5 logical-buffer mipmap work cannot overwrite custom/foreign override parameterization.
PD §11 explicitly supports `.mcmeta` (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-630`);
the remaining suffix-honoring conflict is U1, not evidence that PD drops sidecars. `[D-P13-6]`

#### 4.3.6 Closed unsupported diagnostics

```java
public record UnsupportedBinding(TextureBindingKey key, StageId expandedStage,
    RequestedTextureTarget requestedTarget, UnsupportedReason reason) {}
public sealed interface RequestedTextureTarget {
    record UnknownSampler(String exactName) implements RequestedTextureTarget {}
    record KnownSampler(FixedSamplerName sampler) implements RequestedTextureTarget {}
}
public enum UnsupportedReason { KEY_DOMAIN, STAGE_COLUMN }
```

KEY_DOMAIN requires UnknownSampler(exactName); STAGE_COLUMN requires KnownSampler.
Every diagnostic retains the original key, discriminator and expanded stage, and participates
in canonical fingerprinting. No null, sentinel, unrelated enum or invented unit is legal.
The coordinated Phase5 domain admits all legal AppF5 names/columns: R2 is architecturally
fulfilled. This path is solely for genuinely unknown names/unsupported stage domains, e.g.
shadow-only tex expanded into gbuffers yields STAGE_COLUMN there and remains legal in shadow.
Implementation is gated by fresh reviews and external Phase6/8 grants, not old narrow R2.

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
- `AtlasId.canonicalName` is the Minecraft atlas resource-identity string exactly as the game reports
  it at stitch time: H13-ATLAS-01 passes it, H13-ATLAS-04 carries it in the snapshot's atlas field,
  and H13-ATLAS-02 carries it in AtlasDescriptor. Phase13 neither invents nor normalizes it.
- The App D row is about the atlas bound during world rendering, so the parameterless
  `atlasSize()` of §2.2 answers for the block/item atlas Phase 13 built companions for, and is the
  operation Phase 6 uses through Phase 7; `atlasSize(AtlasId)` exists for any other catalogued atlas
  and returns `Unknown` for an id absent from the current catalog. A consumer never has to guess an id.
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

### 4.5 Publication identity, selection and lifetime

#### 4.5.1 Immutable publication and canonical fingerprint

`TexturePublication(id,registryFingerprint,registryGeneration,resourceReloadEpoch,plan,candidates)`
is the non-owning §2.3 value. `TextureOverlayPublicationId.generation` means **estateGeneration**.
Registry generation is separate. A resource-only replacement changes the content fingerprint via
resourceReloadEpoch even with equal estate generation/paths. TexturePlan preserves the exact
registry/configuration/source-catalog pairing; Phase13 may inspect issued sampler metadata for
demand/diagnostics but never reparse/remerge declarations or allocate units.

Reuse Phase3 §4.10's framing (`docs/phase3/v1/PHASE_3_DOC.md:1389-1410`): canonical base10
ASCII scalars/UTF8 strings with decimal byte-length + colon; lists with element count, opening
bracket, length-prefixed elements and closing bracket. SHA-256 with distinct
`phase13.texturePublication/v1`, `phase13.source/v1`, `phase13.parameters/v1` domains is a **new
design choice**, not a claim about an existing implementation. Phase4 sampler/policy domains
are distinct and their fingerprints are inputs here.

Hash every original key/discriminator, stage expansion, canonical candidate order/origin, full
sample shape, target, source/content/configuration identity, effective decoded/upload/transfer
parameters, sidecar outcomes, parameterization digest, ordered companion sprite origins/extents/
mips/source/default/animation metadata, atlas epoch, noise plan/recurrence schema, absence and
diagnostic variants, macro provenance, registry fingerprint and policy fingerprint.
The reload epoch always participates. Never hash opaque handles/GL names, mutable tick/frame
state, lease counts or wall-clock time. Foreign identity hashes logical resource/reload/object
epochs, not mutable pixels. Equal content inputs do not authorize cross-registry/estate/owner
reuse or revival of retired handles. `[D-P13-18]`

#### 4.5.2 Atomic acquisition and bind ownership

`lease(expected,selection)` validates against this active owner **before incrementing its count**:
READY/current publication availability, expected publication id, selector registry fingerprint,
then Phase4 credential/current-selection validity. Rejections are PUBLICATION_UNAVAILABLE,
PUBLICATION_ID_MISMATCH, REGISTRY_FINGERPRINT_MISMATCH or STALE_SELECTION; no lease is returned.
The returned Acquired lease is the exact expected publication's immutable view. Phase7 supplies
its active tuple's non-owning TexturePublication.id and the same selection sent to Phase5/4/6;
there is no two-call lease/id race or global current-program lookup.

Phase4 selects once from an issued context, applying force-shadow before its sole fallback
resolution. The opaque credential retains the private resolved binding. Phase5 uses pure
`ProgramBindingSelections.validateSelection(selection,context)`; public-record equality is not
authentication. Its checks are structural/thread, estate generation, open frame/frameId, issued
pass/depth epoch, selector issuer/generation/context/pass/provider/stage/band/layout, live overlay
lease, expected id, registry fingerprint, configuration/estate/policy pairing, then full-shape
resolution. First failure wins; publication id precedes registry fingerprint inside overlay checks.

`textureBindings`/`shadowBindings` are Phase5's **physical bind operations**: preflight all sixteen
rows without GL, then bind required compatible objects in ascending unit order through
`TextureService.bindToUnit`, using Phase1 authentication/target dispatch/error containment.
Bound alone means every required object bind completed and transfers the lease into the
idempotently closeable binding snapshot. Compatible base-fallback warnings may remain on Bound.
Rejected/Degraded perform zero binds and transfer nothing; BackendFailed may follow partial GL,
retains/transfers nothing and forbids activation/upload/draw. A thrown call before transfer leaves
the caller responsible.

Phase7 then calls `activate(UseProgramRequest(selection,context))`, never re-resolving or manually
rebinding rows. Phase6's unchanged callback receives the effective descriptor and callback-only
uniform access; its requested resolver cutover is R7-10. After Bound, finally closes **binding
only**; otherwise finally closes the acquired lease only. Skipped/fixed-function, lease rejection,
activation failure, draw exception, reload and teardown take the same ownership rule.
Suppression calls main `discardPass(snapshot)` or shadow `abortPass`, never completion/flips for
an undrawn pass. Backend failure uses existing frame/shadow recovery.

Nested suspension closes the parent binding snapshot and ends its physical pass under Phase7's
existing committed-draw completion versus pre-draw discard rule; never discard committed work.
The logical stack retains the original selection/context. Pop reacquires physical sides, lease
and bindings, then reactivates that retained selection; it never selects the effective provider
as a new requested slot or reuses a consumed snapshot.

Snapshot validity is the intersection of open binding/pass/frame/depth/estate/registry epochs,
current selector/publication and its own open state. Completion/abort/neutralization/replacement
invalidates use, not closure duty. No reentrant publication transition is allowed between render-
thread preflight, binding and immediately following activation.

#### 4.5.3 Retirement and coherent pipeline

```
inactive -> planned -> READY -> RETIRING -> (all leases closed) CLOSED
                         | close/off/reload: stop leases first
quiescent rebuild -> P4 accepted -> adopt P6 -> P5 accepted -> P13 Ready -> P9 accepted -> Active
       any failure -----------------------------------------------------------> compensated OFF
```

Phase13 owns the lease count per publication. Retire stops accepting leases immediately;
`isCurrent()` becomes false even while existing leases keep objects alive. At count zero,
delete owned companions/noise/custom objects in reverse creation order and release borrowed
references without deletion. `close()` is idempotent and schedules deferred destruction rather
than blocking the render thread. Phase7 first stops frame admission, unwinds draws/shadow work,
closes binding snapshots, then closes the texture owner while borrowed services still exist.
An old retiring publication exists only to honor outstanding leases, never as an active fallback.

The exact Phase7 transaction is incorporated in §5.3: no partially retired prior composition is
restored after failure. Before any acceptance, close caller-owned candidates and take shaders off.
After acceptance, accepted owners retire their resources, remaining caller-owned candidates close,
Phase8 closes, Phase9 resets/deactivates with full required geometry invalidation, then Phase5
publishes off and Phase4 publishes ShadersOff with an issued release context. Texture failure
must not leave new registry/estate paired with old objects. Phase6 retirement is requested
R7-11, not an invented UniformRuntime.close operation.

### 4.6 Hook catalog — App E rows 10 and 11

Use `docs/phase7/v1/PHASE_7_DOC.md:1206–1219`'s catalog and health classes: CORE disables the shader group, FEATURE disables
the feature, OBSERVER observes. Phase13 uses FEATURE rows, `require=0`, normally `expect=1`;
Phase7's plugin audits application. Forge stitch events supply timing, read-only accessor
mixins supply private data. `[D-P13-7]`

| ID / class | SRG target and injection | Dumb bridge call | Health / evidence |
|---|---|---|---|
| H13-ATLAS-01 TextureMap | Forge TextureStitchEvent.Pre, no injection | `TextureHooks.onAtlasStitchBegin(atlasIdentity)` | FEATURE; invalidate catalog, pixels, animation snapshots and atlasSize before vanilla restitch; AppE10 `docs/research/v1/RESEARCH.md:1410-1410` |
| H13-ATLAS-02 TextureMap | Forge TextureStitchEvent.Post, no injection | `TextureHooks.onAtlasStitched(atlasDescriptor)` | FEATURE; copy final sprites/frame pixels/metadata into current-epoch catalog, mark size Known only after acceptance; candidate build stays transaction-bound |
| H13-ATLAS-03 TextureMap | @Accessor `field_94252_e`, `field_94258_i`, `field_147636_j` | read-only sprite table/animated list/mip count | FEATURE; mapUploadedSprites/listAnimatedSprites/mipmapLevels; Forge event exposes only getMap `[V:mcp]` |
| H13-ATLAS-04 TextureMap | `func_94248_c()V` updateAnimations TAIL | `TextureHooks.onAtlasAnimationsUpdated(AtlasAnimationSnapshot snapshot)` | FEATURE; full post-vanilla snapshot, not identity alone; §4.1.7 |
| H13-SPRITE-01 TextureAtlasSprite | public name/origin/extent/frame methods plus @Accessor `field_110982_k`, `field_110973_g`, `field_110983_h` | stitch copy and post-tick read-only snapshot builder | FEATURE; animationMetadata/frameCounter/tickCounter `[V:mcp]`; AppE11 `docs/research/v1/RESEARCH.md:1411-1411` |
| H13-SPRITE-02 TextureAtlasSprite | `func_94219_l()V` updateAnimation and `func_180599_n()V` updateAnimationInterpolated | none | FEATURE, dormant at v0.5; no per-sprite update injection |

MCP 1.12.2 confirms the three private sprite fields above and AnimationMetadataSection
`getFrameIndex/func_110468_c`, `getFrameTimeSingle/func_110472_a`,
`isInterpolate/func_177219_e`. These are mapping evidence, **not runtime observation** of
synchronization. Existing public frame count alone is insufficient. Mod glue reads exact frame
sequence/duration/interpolation and post-tick counters and copies plain immutable values.
The base handle comes through Phase1's authenticated foreign-texture provider, not a raw GL id.
No Minecraft object crosses the seam. H13-SPRITE-02 remains dormant; unavailable state restores
frame0 rather than silently activating the dormant hooks. Exactly AppE rows10 and11 are owned.

### 4.7 Reload and resize transitions

On PACK_SELECTION, OPTION_CHANGE, RESOURCE_RELOAD, DIMENSION_CHANGE and registry replacement,
Phase7 quiesces first, ends/aborts the old frame and shadow invocation, and invalidates stale
selectors/leases for new drawing before rebuilding. Resource reload advances resourceReloadEpoch
and invalidates catalog/metadata/copied pixels/current snapshots/foreign object identities and
atlasSize **before vanilla replacement**. Only accepted current stitch data may restore Known.
Option equality cannot keep an old publication paired to a new generation; registry/estate
replacement issues a new publication id using the **actual accepted estate generation**.
Unload/off retires then closes as §4.5.3 specifies.

#### 4.7.1 Resize participation

Phase13 implements render-thread non-throwing
`ResizeConsumerResult resize(BufferResizeNotice notice)` with SUCCESS/FAILED, under Phase5's
existing registration protocol. The chosen strategy closes the old registration at the quiescent
boundary, builds the new publication only after estate acceptance, then registers it with the
accepted sizing/generation. A callback outside that prepared path returns FAILED rather than
independently publishing guessed state. Phase5's full synchronous delivery result, including
ConsumerFailed deliveredCount and installed-off ownership, must be handled by Phase7.

BufferResizeNotice contains sizing/newGeneration/prioritized reason, **not a registry fingerprint**.
The consumer compares transaction-bound accepted registry/configuration metadata and newGeneration;
no reason implies an unchanged registry. Display, render-quality, main-depth, shadow sizing,
pack-configuration, registry-plan and inventory/format changes all preserve this pairing rule.

Same-owner size-independent allocations may survive a newly paired publication with no upload
only if full source/parameterization identity is equal and ownership accounting retains them
across **every** live/retiring publication. An old publication's retirement cannot delete an
object still referenced by the new one. Otherwise rebuild; equal hash never grants cross-owner
reuse. Resize/identity/registration failure follows the compensated shaders-off transaction.

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

### 5.1 Exposed producer contracts

Every §2.2/§2.3 declaration is incorporated into these binding rows, including exact component
order/types, closed variants, defaults, validation, ordering, fingerprints and lifetime. Every
consumer-visible change updates this region in the same revision; referenced signatures are
not merely illustrative.

| Contract | Exact incorporated exposure / consumer |
|---|---|
| Inactive owner construction | `TextureSystemFactory.create(GLDevice gl,DiagnosticReporter diagnostics) -> TextureSystemCreationResult.Created(TextureSystem system) \| Failed(TextureFailure failure)`; no allocation, atlasSize Unknown; Phase7 owns |
| Planning/build | `TextureSystem.plan(TexturePlanRequest request) -> TexturePlanResult.Planned(TexturePlan) \| Invalid(TextureFailure)`; `build(TextureBuildRequest request) -> TextureBuildResult.Ready(TexturePublication) \| Failed(TextureFailure)` |
| Frozen inputs | `TexturePlanRequest(PackConfiguration configuration,ProgramRegistryView registry,AtlasCatalog atlases,TextureSourceCatalog sources,CompanionPolicy companionPolicy,CompanionMacroState macroState,GLCapabilityProfile capabilities,RegistryFingerprint registryFingerprint,long estateGeneration,long registryGeneration,long resourceReloadEpoch)`; `TextureBuildRequest(TexturePlanRequest planRequest,TextureBuildSources sources)` |
| Source preparation | Exact §2.3 TextureSourceCatalog/ReadyAsset/FailedAsset, TextureBuildSources/Owned/Foreign and payload pairing; metadata-only plan, pre-allocation identity/capability/epoch validation |
| Texture plan | `TexturePlan(TexturePlanRequest inputs,List<CompanionAtlasPlan> companions,NoisePlan noise,List<CustomTexturePlanEntry> customTextures,List<UnsupportedBinding> unsupported,CompanionMacroState macroState,TextureMemoryEstimate memory)` |
| Custom plan entry | `CustomTexturePlanEntry(TextureBindingKey key,int phase3Ordinal,FixedSamplerName name,Set<StageId> stages,DeclaredGlslType.Sampler shape,TextureTarget target,TextureSourceIdentity source,TextureUploadSpec upload,TextureParameterSpec parameters,TextureParameterFingerprint parameterizationFingerprint)`; handle-free original key/content retained |
| Non-owning publication | `TexturePublication(TextureOverlayPublicationId id,RegistryFingerprint registryFingerprint,long registryGeneration,long resourceReloadEpoch,TexturePlan plan,TextureCandidateTable candidates)`; id.generation is accepted estate generation |
| Restricted lease source | `TextureLeaseSource.lease(TextureOverlayPublicationId expected,ProgramBindingSelection selection) -> TextureLeaseResult.Acquired(TextureOverlayLease lease) \| Rejected(TextureLeaseRejection reason)`; TextureSystem exposes same operation, no separate public id getter |
| Lease failure domain | PUBLICATION_UNAVAILABLE, PUBLICATION_ID_MISMATCH, REGISTRY_FINGERPRINT_MISMATCH, STALE_SELECTION, checked against active issuing owner before incrementing count |
| Source identity | `TextureSourceIdentity.OwnedUpload(OwnedTextureSourceKind sourceKind,String logicalSource,String contentDigest,String configurationIdentity) \| ForeignLive(String exactResourceIdentity,long resourceReloadEpoch,long objectEpoch)`; owned source kind PACK_PNG/MINECRAFT_DECODED_ASSET/RAW_BYTES/GENERATED_NOISE/COMPANION_RESOURCE/DEFAULT_FILL |
| Effective parameters | `TextureParameterSpec(MinFilter minFilter,MagFilter magFilter,WrapMode wrap)` and `TextureParameterFingerprint(String value)`; §4.3.5 and §4.5.1 canonical source/parameter/publication SHA-256 semantics |
| Preliminary macro producer | `CompanionMacroPolicy.preliminaryMacroState(PreliminaryCompanionDemand demand) -> CompanionMacroState`; demand `(boolean packActive,boolean fixedUnitCapabilityAvailable)`, result `(boolean normalMap,boolean specularMap)`; both equal conjunction, before load/jcpp with no configuration/owner/GL |
| Atlas metadata | Exact §2.3 AtlasId/SpriteDescriptor/AtlasDescriptor/AtlasCatalog/AnimationFrameDescriptor/SpriteAnimationMetadata; copied frames and immutable sequence, no Minecraft objects |
| Post-vanilla animation | `AtlasAnimationSnapshot(AtlasId atlas,long resourceReloadEpoch,long tickSequence,List<SpriteAnimationState> sprites)`; state `(String iconName,int sequencePosition,int currentSourceFrameIndex,int nextSourceFrameIndex,int elapsedTicks,int currentDuration,double nextFrameWeight)`; §4.1.7 epoch/frame-map/duration/tick validation |
| Size/close | `TextureSystem.atlasSize(AtlasId atlas)` and `atlasSize() -> AtlasSizeResult.Known(int width,int height) \| Unknown`; exact current-epoch identity/validity window §4.4; `void close()` idempotent retirement/deferred deletion |
| Hooks/resize | Exact H13-* bridge catalog §4.6 and transaction-bound `ResizeConsumerResult resize(BufferResizeNotice notice)` SUCCESS/FAILED; no guessed registry identity |
| Diagnostics/memory | §4.3.6 exact UnsupportedBinding/RequestedTextureTarget/UnsupportedReason; `TextureMemoryEstimate(long companionBytes,long noiseBytes,long customBytes)`; sanitized TextureFailure, no raw GL names |

Phase5 owns the following shared protocol; Phase13 produces these exact values:

```java
record TextureBindingCandidate(CandidateOrigin origin, StageId expandedStage,
    String exactSamplerName, FixedSamplerName name, DeclaredGlslType.Sampler shape,
    TextureTarget target, TextureHandleRef handle, TextureSourceIdentity source,
    TextureParameterSpec parameters, TextureParameterFingerprint parameterizationFingerprint,
    int candidateOrdinal) {}
// CandidateOrigin = Custom(TextureBindingKey key,int phase3Ordinal)
//                 | Companion(AtlasId atlas,CompanionKind kind) | Noise()
// TextureHandleRef = Owned(TextureHandle handle) | Borrowed(TextureHandle handle)
TextureCandidateEntry TextureCandidateTable.entry(StageId expandedStage, FixedSamplerName name);
// TextureCandidateEntry = Candidates(List<TextureBindingCandidate> candidates)
//                       | Absent(TextureOverlayAbsence reason)
interface TextureOverlaySnapshot {
    TextureOverlayPublicationId id();
    RegistryFingerprint registryFingerprint();
    long registryGeneration();
    long resourceReloadEpoch();
    ConfigurationFingerprint configurationFingerprint();
    FixedSamplerPolicyFingerprint policyFingerprint();
    TextureCandidateTable candidates();
}
interface TextureOverlayLease extends TextureOverlaySnapshot, AutoCloseable {
    boolean isCurrent();
    void close();
}
```

`isCurrent` requires an open lease and the same owning publication currently READY, not retiring.
Borrowing never grants deletion. Phase13 owns allocated objects and releases, never deletes,
foreign handles. Plans/fingerprints are handle-free; publication candidates may contain opaque
handles. Closure duty is §4.5.2: Bound closes binding only, every non-transferring exit closes
acquired lease only. Holding a stale lease delays deletion but never authorizes drawing.

### 5.2 Consumed contracts and exact binding hand-off

| Owner / surface | Consumption |
|---|---|
| Phase3 `docs/phase3/v1/PHASE_3_DOC.md:1482-1510` | normalized original keys/discriminators, complete-key duplicate resolution then canonical order, source references; suffix stripping distinct from sidecar retention |
| Phase3 `docs/phase3/v1/PHASE_3_DOC.md:532-666` | full sampler type algebra via Phase4 metadata; singular centerDepthSmooth materializer unchanged; load/jcpp before configuration |
| Phase3 `docs/phase3/v1/PHASE_3_DOC.md:1389-1410`, `docs/phase3/v1/PHASE_3_DOC.md:1532-1558` | canonical framing, configuration identity, noise requirements/defaults; never reparsed |
| Phase4 `docs/phase4/v1/PHASE_4_DOC.md:1785–1796` | exact detached ProgramRegistryView, registry/policy fingerprints, ProgramSamplerLayout and opaque ProgramBindingSelection; line 1787: “resolve is detached handle-free inspection, not selection authority” |
| Phase5 `docs/phase5/v1/PHASE_5_DOC.md:2344–2375` | sole fixed-name/policy/resolver, candidate/binding results, accepted estate generation, formats and resize; line 2363: “Bound alone transfers lease into closeable sixteen-row snapshot with BoundObject/Unused” |
| Phase7 `docs/phase7/v1/PHASE_7_DOC.md:504–520,749–819,2230–2268` | active-tuple owner/publication/lease source, select-once orchestration, transaction; line 2233 incorporates “complete ten numbered steps of §4.1” |
| Phase6 `docs/phase6/v1/PHASE_6_DOC.md:1036–1097,1382–1394` | unchanged afterBind(effective descriptor,context,bound uniform access), three participants/cache/activity-token semantics; resolver cutover requested, not adopted |
| Phase8 `docs/phase8/v1/PHASE_8_DOC.md:560–607,1081–1133` | unchanged optional shadow lifecycle/bridge/traversal; new full shared binding remains requested and real slot gated |

The exact Phase5 name enum is:
TEXTURE→texture, TEX→tex, LIGHTMAP→lightmap, NORMALS→normals, SPECULAR→specular,
SHADOWTEX0→shadowtex0, WATERSHADOW→watershadow, SHADOW→shadow, SHADOWTEX1→shadowtex1,
DEPTHTEX0→depthtex0, GDEPTHTEX→gdepthtex, GAUX1→gaux1, GAUX2→gaux2, GAUX3→gaux3,
GAUX4→gaux4, DEPTHTEX1→depthtex1, DEPTHTEX2→depthtex2, SHADOWCOLOR0→shadowcolor0,
SHADOWCOLOR→shadowcolor, SHADOWCOLOR1→shadowcolor1, NOISETEX→noisetex,
COLORTEX0→colortex0, COLORTEX1→colortex1, COLORTEX2→colortex2, COLORTEX3→colortex3,
COLORTEX4→colortex4, COLORTEX5→colortex5, COLORTEX6→colortex6, COLORTEX7→colortex7,
GCOLOR→gcolor, GDEPTH→gdepth, GNORMAL→gnormal, COMPOSITE→composite.
These are exact case-sensitive spellings, no lowercase normalization or synthesized colortex8–15.
`FixedSamplerLookup = Known(FixedSamplerName) | Unknown(String exactName)`;
`FixedSamplerResolution = Resolved(FixedSamplerName name,int unit) |
UnsupportedDomain(FixedSamplerName name,StageId stage,StageBand band)`.
Phase13 invokes lookup but does not independently resolve units.

| Unit | Gbuffers/shadow names | Deferred/composite/final names |
|---|---|---|
| 0 | texture; tex only shadow | colortex0, gcolor |
| 1 | lightmap | colortex1, gdepth |
| 2 | normals | colortex2, gnormal |
| 3 | specular | colortex3, composite |
| 4 | shadowtex0, watershadow, conditional shadow | shadowtex0, watershadow, conditional shadow |
| 5 | shadowtex1, conditional shadow | shadowtex1, conditional shadow |
| 6 | depthtex0 | depthtex0, gdepthtex |
| 7 | gaux1 | colortex4, gaux1 |
| 8 | gaux2 | colortex5, gaux2 |
| 9 | gaux3 | colortex6, gaux3 |
| 10 | gaux4 | colortex7, gaux4 |
| 11 | depthtex1 | depthtex1 |
| 12 | none | depthtex2 |
| 13 | shadowcolor0, shadowcolor | shadowcolor0, shadowcolor |
| 14 | shadowcolor1 | shadowcolor1 |
| 15 | noisetex | noisetex |

This is a mirror of Phase5's sole authority, checked against
`docs/research/v1/RESEARCH.md:1228-1255`. `shadow`→5 iff the effective layout contains a direct
sampler-compatible watershadow declaration, otherwise4; never a shadow-buffer-count heuristic
(`docs/phase6/v1/PHASE_6_DOC.md:993-998`). Both gbuffers bands share mapping. Virtual has no
binding and unsupported compute/unwired domains return UnsupportedDomain, not composite.

```java
PassSnapshotResult BufferEstateView.snapshot(PassDescriptor pass, ProgramBindingSelection selection);
TextureBindingResult BufferEstateView.textureBindings(PassBufferSnapshot snapshot,
    TextureOverlayLease overlay, TextureOverlayPublicationId expectedOverlay);
PassDiscardResult BufferEstateView.discardPass(PassBufferSnapshot snapshot);
ShadowBeginResult ShadowEstateView.beginPass(long frameId, PassDescriptor pass,
    ProgramBindingSelection selection);
TextureBindingResult ShadowEstateView.shadowBindings(long generation, long frameId,
    ShadowPassSnapshot snapshot, TextureOverlayLease overlay,
    TextureOverlayPublicationId expectedOverlay);
```

PassBufferSnapshot and ShadowPassSnapshot carry the same mandatory selection; shadow beginPass
also accepts it instead of a descriptor. Estate/depth/frame/pass/flip identities remain intact.
Shadow freezes **all** readable main+shadow sides as `Map<LogicalBuffer,TextureHandle>
readableTextures` at acquisition, never reads later live sides. Shared bindings are sixteen
rows, closeable, not the former four-row borrowed view.

`TextureBindingResult = Bound(TextureBindingSnapshot) | Degraded(TextureBindingDegradation) |
Rejected(TextureBindingRejection) | BackendFailed(BufferFailure failure)`.
Degradation retains exact selection, ordered diagnostics and SUPPRESS_DRAW.
`TextureBindingOutcome = BoundObject(TextureHandleRef,DeclaredGlslType.Sampler,
List<ResolvedSamplerBinding>,BindingOrigin) | Unused`.
`ResolvedSamplerBinding` carries exactName/full shape/fixed unit. BindingOrigin distinguishes
CUSTOM/COMPANION/NOISE/ESTATE/FOREIGN/NEUTRAL and compatible-fallback diagnostics.
`MissingTextureBinding(reason,exactName,unit)` is resolution-only, never drawable.
Discard authenticates like completePass, consumes a pre-draw open snapshot without flips or
post-draw mipmaps, and returns Discarded(long frameId) or Rejected(FrameProtocolRejection reason).

Closed diagnostics: NO_CANDIDATE, INCOMPATIBLE_CANDIDATE, CONFLICTING_SAMPLER_TYPES,
CONFLICTING_CANDIDATES, UNSUPPORTED_SAMPLER_NAME, UNSUPPORTED_STAGE_DOMAIN,
UNSUPPORTED_SAMPLER_SHAPE, PUBLICATION_UNAVAILABLE, NOT_CONFIGURED,
NOT_APPLICABLE_TO_STAGE, MISSING_BACKING. Missing publication is not a present empty publication.
Closed binding rejections: INVALID_INPUT, WRONG_THREAD, STALE_ESTATE_GENERATION,
STALE_DEPTH_ATTACHMENT_EPOCH, NO_OPEN_FRAME, WRONG_FRAME_ID, INVALID_PASS_SNAPSHOT,
INVALID_PROGRAM_SELECTION, PROGRAM_SELECTION_MISMATCH, STALE_REGISTRY_GENERATION,
SAMPLER_LAYOUT_MISMATCH, CLOSED_OVERLAY_LEASE, OVERLAY_PUBLICATION_ID_MISMATCH,
REGISTRY_FINGERPRINT_MISMATCH, CONFIGURATION_FINGERPRINT_MISMATCH.
Phase5 maps Phase4 pure validation Rejected(INVALID_ISSUER|STALE_GENERATION|STALE_CONTEXT|
WRONG_STAGE_BAND|PROVIDER_LAYOUT_MISMATCH) into its closed reasons; §4.5.2 preflight order is
binding. Stale/closed lease maps CLOSED_OVERLAY_LEASE even if its content id equals a new one.

### 5.3 Coherent transaction and exact outstanding requests

The following transaction is mirrored by Phase7 §4.1/§5.3, not an independent publication path:

1. Stop admission; finish/abort old frame, drain binding/shadow work, freeze intended
   reload/configuration/resource epoch. Failed rebuild means shaders-off, not restored prior
   composition. Compute preliminary demand before Phase3.load; use granted R1 input or absent macros.
2. Preserve Phase8 policy-before-Phase6 ordering only under registry-independent R7-13. Until
   granted real shadow remains NotInstalled. Create inactive Phase13 owner/adapters, then Phase6
   provider/runtime at current authoritative registry generation; atlasSize Unknown until accepted
   stitch. Candidate adapters never send an event to an old runtime.
3. Preserve Phase9 pure candidate build/frozen validation. Get `FixedSamplerPolicies.appB3()`;
   Phase4 compiles with that policy and the new runtime's existing macro contribution. Phase5
   plans/creates from the detached registry. AwaitingMainDepth publishes nothing and remains gated.
4. Compose barrier with that runtime's three participants. Optional Phase8 construction needs
   grants and the final new registry fingerprint. Revalidate intended configuration/world/resource/
   hook/candidate identities. Precommit failure closes caller-owned candidates and takes old
   composition off; never resume a restitched stale atlas.
5. Close old Phase8 and retire old texture-event/resize registrations and the old texture owner after draw/binding drain at quiescence. Publish
   Phase4 Ready with issued release context; handle Accepted, Rejected and RecoveredOff separately.
   Rejected retains candidate ownership; RecoveredOff is a result, never RegistryPublication input.
   Retire old Phase6 only under requested R7-11, after old barrier invalidation.
6. After Accepted reacquire actual publisher generation; call
   `adoptRegistryGeneration(generation,PACK_REPLACEMENT)`. ADOPTED/ALREADY_CURRENT proceed;
   REJECTED_RETIRED_GENERATION compensates before any event/participant/beginFrame/shadow use.
7. Publish Phase5 and handle its complete synchronous resize outcome. Use actual accepted
   PublishedBufferEstate generation/sizing. ConsumerFailed is installed off, not caller-owned ready.
8. Build Phase13 against accepted registry/estate, frozen sources and resource epoch. Validate
   Ready identity exactly, then register resize consumer with accepted sizing/generation.
   Registration/build failure compensates. Install hook adapters only in pending composition.
9. Publish Phase9 after Phase13 and retain synchronous IdDependentGeometryInvalidator gate.
   Atomically install ActivePipeline only after all publications/registrations/adapters/optional
   states agree; increment PipelineVersion once for final outcome, then admit frames.
10. Any post-acceptance failure retires accepted texture state through owner, closes only still-
    caller-owned candidates, closes Phase8, resets/deactivates Phase9 with required full geometry
    invalidation after ID invalidation failure, calls Phase5.publishOff(BufferFailure), then
    Phase4.publish(RegistryPublication.ShadersOff(cause),issuedReleaseContext) and handles result.
    No accepted object is caller-closed and no old registry revived. Phase6 lifetime uses R7-11.

ActivePipeline and dimension-cache identity include a private Phase13 owner plus non-owning
TexturePublication/TextureLeaseSource, actual registry+estate generations, source/reload identity
and readiness. Phase13 remains a downstream v0.5 slot, not a retroactive declared dependency.
Earlier milestones may use an explicit empty publication through the same selector/lease protocol;
that is not completion of required v0.5 custom/PBR execution.

**R1 — Phase3 typed load-time option input (ungranted).** Add Phase3-owned
`CompanionOptionMacros(boolean normalMap,boolean specularMap)` immediately after engineOptions
in PackLoadRequest; copy into MacroConfiguration option state and every same-build materialization.
Phase7 adapts preliminary state before load. Emit only enabled MC_NORMAL_MAP/MC_SPECULAR_MAP
after #version/extensions and before jcpp/pack #line. Include both booleans in configuration and
materialization fingerprints. Missing required typed data on non-Off inputs is existing
INVALID_REQUEST; Off still short-circuits. Do not overload MacroContribution. Until granted,
call unchanged load, leave macros absent and explicitly block PBR conformance.

**R2 — Phase5 full domain:** fulfilled architecturally by the coordinated FixedSamplerName/
candidate/binding contract. Old narrow fallback/D-P13-11 are historical, not a legal-input path.
Fresh reviews remain required; no implementation closure is claimed.

**R3 — Phase1 package allocation:** retain request for engine.textures/mod.glue.textures/
mod.mixin.textures; existing mod.mixin grant permits mixins, engine/glue names still pending.
No alternate package is invented.

**R4 — Phase3 post-analysis memory optimization:** add
`CompanionMapRequirement(boolean normals,boolean specular)` to ResourceRequirements from
declared sampler analysis. It may avoid unused atlas allocation only, never modify preliminary
macro policy; ungranted fallback builds both for active/capable pack.

**R7-10 — Phase6 fixed resolver migration (ungranted).** Inject Phase5 `FixedSamplerResolver`
immediately after configuration in UniformRuntimeFactory.create. Phase5 provides it before any
registry/estate via `FixedSamplerPolicies.resolver()` alongside appB3(), backed by the same
schema/table and fingerprint:
`resolve(ProgramSamplerLayout layout,StageId stage,StageBand band) ->
FixedSamplerPlanResult.Ready(List<ResolvedSamplerBinding> bindings,FixedSamplerPolicyFingerprint policy)
| Invalid(SamplerLayoutValidation reason)`. Phase6 uses binding.samplerLayout/context inside its
unchanged sampler participant, locates exact names and uploads integers in existing cache/order/
error protocol after object binding. No second map, free allocation, fourth participant or raw
handle loop. Until adopted/freshly reviewed the single-authority upload seam is blocked.

**R7-11 — Phase6 retirement (ungranted).** Request
`UniformRetirementResult retire(UniformRetirementReason reason)`, reasons UNPUBLISHED_ABORT,
REPLACEMENT, SHUTDOWN; results Retired, AlreadyRetired,
Rejected(WRONG_THREAD|ACTIVE_CALLBACK). No GL/barrier operation; permanently disable events,
participants/adoption and release caches/provider references after final callback, before borrowed
services disappear. Candidate abort may precede any publication; replacement follows old barrier
invalidation; shutdown precedes Phase4 teardown. Reconcile existing reset(CLOSE) rule
(`docs/phase6/v1/PHASE_6_DOC.md:1342-1360`), do not add a conflicting alias or invent close().
Until adopted, candidate/replacement runtime lifecycle is an implementation blocker.

**R7-12 — Phase8 full shared shadow migration (ungranted).** Append selection, activationContext,
TexturePublication and TextureLeaseSource to ShadowInvocationContext. Phase7 selects root shadow
once and owns the invocation bridge. Request Phase8 §4.2 steps3–13 and R8-2 use that selector/
context, beginPass(selection), expected-id full overlay lease, five-argument shadowBindings above,
sixteen-row preflight, Phase5 object binds before activation, every closed result, and exactly-once
binding closure in finally. Preserve existing traversal/camera/copied-depth/mipmap/neutralization
and bridge lifetime. This **supersedes** the old four-row non-closeable request
(`docs/phase8/v1/PHASE_8_DOC.md:1102-1116`), not a claim of consumption. Until adoption and fresh
review, real shadow custom execution stays NotInstalled/unavailable with existing neutral-shadow
behavior; never old rows/prior-frame bindings. Unrelated Phase8 requests remain outstanding.

**R7-13 — Phase8 construction cycle (ungranted).** Remove registry from ShadowPlanInput so
remaining fields are ShadowPolicy,ShadowHookHealth; add RegistryFingerprint registry immediately
after ShadowPlan plan in ShadowPassFactory.create. Plan stays registry-independent policy/celestial
metadata; final publication/build identity includes registry. Do not use an old fingerprint to
break the current cycle (`docs/phase8/v1/PHASE_8_DOC.md:225-257`). Until granted, real construction
stays NotInstalled.

**U1 — Phase3/DESIGN suffix conflict:** route the exact incompatible quotations and separate
sidecar grant in §3.6 to both owners; no reconstructed suffix parameters or claim PD drops mcmeta.
Keep Phase3 pending re-verification and Phase4 legacy-geometry/attribute requests outstanding.

### 5.4 Verification state and change triggers

Review36 literal PASS/Interface changed:no cleared historical Phase7 provisional consumption.
The coordinated changes now alter Phase4/5/7/13 §5, so those **new** contracts are unverified;
prior PASS records certify only preceding bytes. Phase3 remains provisional under applied
Review36 resolutions with §5 changed and no fresh PASS.

Actual future triggers are consumed signature/type/ordering/fingerprint/suffix semantics, sampler
policy/selection/lease lifetime changes, hook snapshot/resize/reload transaction changes, owner
adoption of R1/R7-10..13, and fresh-review corrections affecting these contracts. Reconcile them
through governed fix-up/rebuild as required. A directory name or version roll is never the trigger.
Neither Phase7's old footer nor historical PASS overrides the new unverified state.

### 5.5 Downstream hand-offs

Phase14 owns sampler objects and asynchronous/PBO transfer optimization; preserve fixed policy,
source identity, target/parameterization, deferred deletion and post-vanilla snapshot semantics.
Phase2 owns fixtures/conformance adapters; receives handle-free plans/diagnostics and §8's future
checks. G8 advertises labPBR channel semantics only; required AppF5 typed shared-unit behavior
belongs to v0.5, not G8. Phase6 atlasSize cadence is unchanged; Phase7 owns the coherent runtime
adapter and same-selector transaction. No request grants itself by appearing here.

---
## 6. Failure modes & degradation

Apply the governing ladder (`docs/design/v3/DESIGN.md:419-449`) without treating missing required
bindings as drawable success or an active rebuild failure as permission to revive old resources.

| Failure / boundary | Observable disposition |
|---|---|
| Per-sprite companion missing/wrong dimensions in prepared catalog | Use DefaultFill for that sprite with one diagnostic; no uninitialized hole |
| Custom source missing/undecodable/invalid length/limit at preparation | Retain FailedAsset diagnostic; Phase5 may use only a compatible base fallback with NO_CANDIDATE/INCOMPATIBLE_CANDIDATE evidence; missing required backing suppresses that program |
| Malformed/unreadable sidecar | Effective defaults plus one diagnostic and canonical outcome digest; texture remains usable |
| Noise override decode failure | Chosen generated-noise fallback plus diagnostic; allocation failure of required generated object is a build failure |
| Companion/noise/custom allocation/upload/mip build failure; payload/catalog identity mismatch | Failed(TextureFailure), close partial caller-owned allocations; coherent pipeline compensation to off, never install partial textures or keep an old active atlas |
| No accepted atlas catalog | atlasSize Unknown, companion unavailable/not configured evidence; independent noise/custom sources remain representable; required missing sampler degrades locally |
| Missing/invalid live animation state or hook failure | Restore frame0, disable animation, one diagnostic; stale epoch/tick snapshot is discarded without upload |
| Unknown name or unsupported stage | UnsupportedBinding retains key/stage and UnknownSampler or KnownSampler with correct reason; no sentinel/unit |
| Unsupported sampler shape or same-unit incompatible declaration types | Phase4 typed prelink failure before shader/program creation and local provider fallback; Phase5 defensive preflight gives Degraded with zero binds |
| Same-shape aliases choose distinct source/parameterized objects | CONFLICTING_CANDIDATES, zero binds; no traversal winner |
| Selector/pass/epoch/lease/publication/configuration/policy failure | Typed rejection before GL; no transfer. Caller closes acquired lease and locally discards/aborts undrawn pass |
| Required candidate absent/incompatible and no compatible backing | Degraded(selection,diagnostics,SUPPRESS_DRAW); no bind/upload/draw, no flips; unrelated programs remain available |
| Backend error after physical binding begins | BackendFailed(BufferFailure), no transfer/retained lease; no activation/upload/draw, existing frame/shadow containment |
| Texture build/identity/resize-registration failure after P4/P5 acceptance | §5.3 compensation: accepted resources retired by owners, remaining candidates closed, P5/P4 off and no Active tuple |
| Resize callback outside prepared accepted pairing | FAILED; preserve Phase5 synchronous installed-off/ConsumerFailed deliveredCount result |

`TextureFailure` contains closed code, sanitized message key, diagnostic id and logical texture
identity, never a raw GL number. Driver detail goes to GUI/log rather than chat.
Rejected is not necessarily “a replacement happened”: structural misuse and mismatched identity
remain distinguishable. Publication absence differs from an empty configured publication.
Retirement invalidates drawing immediately; deletion waits for every lease, never blocks render
thread and never deletes borrowed vanilla textures. Shaders-off leaves vanilla atlas stitching,
storage, mip chains and final restored state intact (`docs/design/v3/DESIGN.md:2498-2500`).

---

## 7. Threading & performance notes

### 7.1 Thread ownership

Cold source preparation/catalog construction, sidecar interpretation, noise arithmetic, layout/
source hashing and candidate indexing are separate from draw-time work. Pure planning and hashing
may run over frozen Minecraft-free data; current v0.5 resource adapters/stitch capture and all GL
allocation/upload/binding/lease count/retirement/animation mutation run on the render thread.
No live Minecraft object is read by a worker. Off-thread GL remains exclusively Phase14's
shared-context design with synchronous fallback (`docs/design/v3/DESIGN.md:412-417`).
The static preliminary macro producer is pure, independent of any texture owner.
No reentrant publication transition may occur through preflight, bind and activation.

### 7.2 Allocation posture

Precompute immutable ordered candidate lists, exact-name indexes and bounded fixed-unit indexes.
Per draw, no properties parsing, declaration remerge, byte hashing or free-unit scan. Reuse
canonical lists without incidental per-row copies/boxing. A fresh immutable lease and binding
snapshot are bounded lifecycle objects; **zero allocation is not promised**.
Post-vanilla animation uses copied metadata/frame data and pre-sized transfer storage for the
animated sprites with real companions; no independent clock or full-atlas rebuild each tick.
Phase14 owns measured transfer/sampler optimization, not a second binding implementation.
Memory estimates and per-publication diagnostics provide evidence without array-cache machinery.

---

## 8. Testability plan

These are future architectural checks, **not executed tests**. Phase13 owns producer checks;
Phase4 owns merge/prelink/selector checks; Phase5 owns selection/preflight/bind checks; Phase7
owns orchestration/transaction checks. Headless immutable inputs and Phase1 RecordingGLDevice
make outcome and zero-mutation contracts observable; Phase2 owns harness adapters/fixtures.

### 8.1 Shared-unit and producer contracts

| Named check / owner | Input → observable outcome |
|---|---|
| sharedUnit_programSpecificTargets — P13 producer, P5 bind, P7 orchestration | Raw1D `texture.composite.gaux1` and raw3D `.2` → both retained in one cell; sampler1D gets first object7, sampler3D second object7, exact name stays gaux1; no patch/unit reassignment |
| sharedUnit_incompatibleAliasesBeforeBind — P4/P5 | Provider colortex4 sampler1D plus gaux1 sampler3D → typed SAMPLER_UNIT_TYPE_CONFLICT before GL creation and only provider fallback; corrupt metadata at bind → zero binds/uploads/draw |
| sharedUnit_effectiveFallbackLayout — P4/P7 | Failed child, ancestor with different complete layout/state → selector, candidates, activation and P6 callback all use ancestor; child overlays nothing |
| custom_stageExpansionExact — P13 | GBUFFERS/DEFERRED/COMPOSITE original keys → exactly gbuffers+shadow/deferred/composite+final copies preserving key/discriminator/ordinal; tex is legal only shadow, typed stage diagnostic in gbuffers |
| custom_fullscreenFixedOverrides — P5/P7 | Compatible composite/final colortex1 and deferred gaux1 → custom objects bind1/7 instead of base; every documented name/alias reachable |
| sharedUnit_fixedRangeAndShadowAlias — P5/P6 requested adapter | Fixed table plus adding direct compatible watershadow → all rows0–15, depthtex1 remains11, gbuffers12 unused, shadow alias4→5 in same policy/resolver with no allocation |
| sharedUnit_exactNameAndOrdinal — P13/P5 | Compatible same-name absent/0/9 discriminators in shuffled storage → greatest canonical ordinal9 wins after filtering; different alias winning objects → CONFLICTING_CANDIDATES |
| sharedUnit_fullSamplerShape — P4/P5 | Equal target dimension but different signedness/shadow/array/multisample flags → cannot match; aggregate samplers retained and unsupported, not cast/dropped |
| binding_staleBeforeMutation — P4/P5/P13/P7 | Independently stale registry generation/fingerprint, estate/depth/frame/context/provider/band/layout/policy/publication/resource epoch or closed lease → reject before bind; publication id beats registry within overlay checks |
| binding_absenceVersusIncompatibility — P5/P7 | Empty custom cell, incompatible custom, missing publication, unsupported name, incompatible declarations → distinct diagnostics; compatible base draws warned; missing required backing suppresses only program and discards without flips |
| binding_leaseOwnershipAllExits — P13/P7 | Bound vs rejection/degradation/backend failure, then draw/activation error/nested suspend-pop/exception/reload/neutralization/teardown → exactly one owner closes; old lease delays deletion not stale rejection; borrowed objects never deleted |
| publication_contentIdentity — P13 | Same key/path/dimensions with changed bytes or effective upload/filter → different fingerprint; no reuse from equal dimensions/path |
| publication_foreignReloadIdentity — P13 | Same minecraft dynamic/atlas string resolves replacement after reload → reload/object epoch changes identity with no GL-name hash; in-place animation does not republish each tick |
| animation_postVanillaSnapshot — P13 | Reordered frame map with unequal durations, vanilla tick first → companions use exact current/next frames, weight and mips; reload rejects old snapshot, failed hook restores frame0 |
| macro_beforeJcppAndUngrant — P13 producer/P7 orchestration/P3 requested owner | Active/capable preliminary input → both booleans before configuration/load/jcpp; granted input changes same-build branches/fingerprints; ungranted API leaves both undefined and PBR gate incomplete |
| noise_signedRecurrence — P13 | Signed wrapping/arithmetic-shift recurrence → xorshift(-1)=253983; channel(1,1,1) remainder=-115/upload141; unsigned shift or absolute value fails |
| unsupported_noEnumSentinel — P13 | Unknown name → UnknownSampler(exactName)+KEY_DOMAIN; known out-of-stage → KnownSampler+STAGE_COLUMN, no fabricated enum/unit |
| pipeline_textureFailureCompensates — P7 | Texture build/identity/registration failure after P4/P5 acceptance → P5/P4 off, no Active tuple/draw, caller candidates closed, accepted resources owner-retired; preserve ConsumerFailed count/no borrowed deletion |
| shadow_sharedBindingAndGate — P5/P7/P8 requested consumer | Granted migration → one selection, sixteen applicable rows bound before uploads, transferred snapshot closed; no grant → NotInstalled/unavailable, no old four-row success |

### 8.2 Companion, upload and hook checks

| Named check / P13 unless stated | Input → observable outcome |
|---|---|
| companion_discoveryPerSprite | Mixed present/missing `_n`/`_s` resources → one resource/default source per enabled sprite/kind with correct identity |
| companion_layoutAndMipChainMatchBase | Non-square atlas with varied origins/mips → matching companion extent/origins and all mip levels, no independent repack |
| companion_missingNormalUsesContractDefault | Missing normal → literal §4.1.4 byte pattern; C-TX01 color interpretation remains explicit, not silently corrected |
| companion_missingSpecularUsesZeroDefault | Missing specular → zero fill throughout sprite/mips |
| custom_mcmetaBlurSetsFilter | Sidecar blur true/false or malformed → LINEAR/NEAREST/default plus correct outcome digest |
| custom_mcmetaClampSetsWrap | Sidecar clamp true/false or malformed → CLAMP_TO_EDGE/REPEAT/default plus correct outcome digest |
| noise_resolutionFromRequirementsAndBaseline | No noise requirement vs enabled N → no noise allocation versus NxN RGB; absent resolution baseline256 |
| noise_packOverrideReplacesGenerated | Valid override image vs decode failure → exact image dimensions/parameters versus diagnosed generated fallback |
| atlasSize_valueAndValidityWindow | Inactive owner, accepted stitch, resource invalidation → Unknown, exact Known extent while bound, Unknown; unknown AtlasId never guessed |
| companion_vanillaAtlasUntouchedWithShadersOff | Off/reload/close with borrowed atlas → no foreign allocation/upload/delete, restored vanilla binding/state |
| hook_atlasCatalogCapturedAtStitchPost | Pre then Post → invalidated old catalog then copied current metadata/pixels only |
| hook_spriteCompanionAndAnimationRows — P7 audit | Application report → active read-only accessors/atlas TAIL counted, per-sprite update rows explicitly dormant |

### 8.3 Hook integration and lifetime evidence

The two-program target example is traced through P13 candidate production, exact active
publication lease, P5 compatibility/bind and P4 retained-selection activation. Headless records
alone cannot certify actual hook ordering; future mod integration exercises the post-vanilla
TAIL and reload invalidation with a recording backend and then the real surface. No such run
occurred in this architecture rebuild.

### 8.4 Conformance tiers

T0 loads the classic matrix without missing-binding storms; T1 covers normal/noise/animated
scenes with camera motion; T2 checks classic pixel parity and may overturn explicit mip/default
assumptions through the conflict process; T3 is the full v0.5 classic gate, including PBR macros.
Phase2 owns artifacts: no pack source/images committed, manifest-only goldens, explicit
regeneration (`docs/design/v3/DESIGN.md:692-728`). No new fixture or test file is authorized here.

---

## 9. Milestone staging

| Component / owner | Milestone | Future exit check (§8) |
|---|---|---|
| Full sampler metadata, pure fixed policy/resolver, authenticated selector — P4/P5 infrastructure | v0.1 | sharedUnit_fullSamplerShape, sharedUnit_incompatibleAliasesBeforeBind, binding_staleBeforeMutation |
| Same-selector ordinary orchestration and explicit empty publication before texture milestone — P7 | v0.1 | sharedUnit_effectiveFallbackLayout, binding_leaseOwnershipAllExits; empty publication is not v0.5 completion |
| Shared sixteen-row shadow operation — P5; real consumption gated on P8 R7-12/13 | v0.2 | shadow_sharedBindingAndGate |
| Source catalog/prepared payload identity, custom candidates, full-name overrides — P13 | v0.5 | sharedUnit_programSpecificTargets, custom_stageExpansionExact, custom_fullscreenFixedOverrides, publication_contentIdentity |
| Companion layout/defaults/metadata and H13-* hooks — P13 | v0.5 | companion_layoutAndMipChainMatchBase, animation_postVanillaSnapshot |
| Signed noise, override and sidecars — P13 | v0.5 | noise_signedRecurrence, noise_packOverrideReplacesGenerated, custom_mcmetaBlurSetsFilter, custom_mcmetaClampSetsWrap |
| Preliminary macros and requested P3 input | v0.5 | macro_beforeJcppAndUngrant; R1 gate remains |
| Lease/publication/reload/resize coherence — P13/P7 | v0.5 | publication_foreignReloadIdentity, binding_leaseOwnershipAllExits, pipeline_textureFailureCompensates |
| atlasSize | v0.5 | atlasSize_valueAndValidityWindow |
| H13-SPRITE-02 per-sprite injection | v0.5, dormant | explicitly dormant in application audit, not missing |
| Async/PBO transfer and sampler-object optimization — P14 | post-v0.5 | P14-owned checks preserving identity/ownership |

Full AppF5 binding is no longer conditional on old R2. Phase3 macro/suffix/reverification,
Phase6 resolver/retirement, Phase8 construction/consumption, package grants and fresh whole-doc
reviews remain explicit blockers; no architecture-only edit claims implementation closure.
The v0.5 implementation gate remains the full classic matrix at T3 plus correctly rendered
MC_NORMAL_MAP packs (`docs/design/v3/DESIGN.md:2507-2508`).

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

| ID | Decision and contract check |
|---|---|
| D-P13-1 | Retain explicitly adopted v3 governance, derive its own pins; no other phase adoption |
| D-P13-2 | **Historical/superseded:** original authorized provisional P7 read. Review36 literal PASS cleared it; this rebuild creates a new unverified §5 gate, never a directory-roll gate |
| D-P13-3 | Contract signed xorshift, not Random(0); PD §11/§18 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:621-625`, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:815-815` checked against `docs/research/v1/RESEARCH.md:596-597`; reopening only via observed contract conflict |
| D-P13-4 | Full companion atlases, reject per-bound-id side table; PD §11 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:631-637` checked against `docs/research/v1/RESEARCH.md:594-595` |
| D-P13-5 | Preserve literal normal default and explicit byte-order assumption, C-TX01 `docs/design/v3/DESIGN.md:1088-1088` |
| D-P13-6 | Consume stripped/ignored key suffixes separately from retained sidecars; honor sidecars and route assignment conflict U1. Phase3 `docs/phase3/v1/PHASE_3_DOC.md:749-750`, PD §7.4/§11 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:447-448`, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-630` |
| D-P13-7 | Forge stitch events for timing, read-only atlas/sprite accessors for private data; policy stays in engine/glue |
| D-P13-8 | Atlas TAIL post-vanilla full animation snapshot, no independent clock; design requirement `docs/design/v3/DESIGN.md:2482-2483`, not observed sync; frame0 fallback |
| D-P13-9 | Real bound atlasSize Known/Unknown, not PD no-op; `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:177-177`, PD §11 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:638-639` |
| D-P13-10 | Static preliminary active/capable demand before load/jcpp; requested separate P3 option macros, not completed-plan input; checked shipped meanings `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:655-656` and PD §7.6 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:470-474` |
| D-P13-11 | **Historical/superseded:** old narrow P5 domain fallback. Coordinated R2 now fulfills full AppF5 domain architecturally |
| D-P13-12 | Post-analysis demand may save unused atlas allocation without changing macro policy; accepted cost `docs/design/v3/DESIGN.md:2499-2500`, PD B13 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:799-799` |
| D-P13-13 | Verification comes from latest verdict and outstanding interface changes, never stale footers; P3 pending, coordinated P4/5/7/13 fresh PASS required |
| D-P13-14 | Preserve box-filter/no-normal-renormalization assumption in §4.1.3, falsifiable at T2, not an unasked improvement |
| D-P13-15 | Accept required v0.5 shared-unit capability via exact names/effective layouts/typed candidates; reject only generated customtexN names/source patching. Checks `docs/design/v3/DESIGN.md:2471-2476`, `docs/research/v1/RESEARCH.md:1488-1490`, PD §11 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-629` |
| D-P13-16 | P5 sole fixed-name resolver, no dynamic/free units, preserved effective watershadow alias. Check `docs/research/v1/RESEARCH.md:1228-1255`, PD §18 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808-808` |
| D-P13-17 | Closed bind outcomes and Bound-only ownership transfer, never boolean/no-op success; AppF5 `docs/research/v1/RESEARCH.md:1488-1490`, PD B10 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:796-796` |
| D-P13-18 | Actual bytes/parameters/config/source/reload/object identity in canonical SHA-256 domains; content equality never generation/ownership authority |
| D-P13-19 | Compatible custom greatest canonical Phase3 ordinal wins across distinct discriminators; complete-key last-valid-wins already happened upstream; conflicting aliases never traversal-winner |

### 11.2 Binding decisions

D-2 keeps this shaders-only; labPBR channel semantics remain pack-side. D-4 is full architecture
with explicit v0.1/v0.2/v0.5 staging. D-5 retains dumb mixins/events without replacement classes.
D-6 keeps engine Minecraft-free; D-9 retains compatibility-profile targets. Pack names stay
verbatim; RESEARCH D-1..D-10 are not contradicted.

### 11.3 Input contradictions

§3.6 records suffix stripping versus assignment honoring, the now-superseded narrow-domain
shortfall, corrected verification-state history and unresolved C-TX01. No claim that PD ignores
mcmeta or that omitted shipped grammar proves absence survives. No RESEARCH change is requested.

### 11.4 Hand-offs and blockers

P4 owns complete linked metadata and authenticated selection; P5 sole policy/candidate selection/
physical binding; P6 requested fixed-integer upload adapter; P7 orchestration and coherent owner
tuple; P13 source/candidate production and lease retirement. P14 owns later transfer optimization.
P8 unchanged implementation cannot consume a new binding seam without adopting R7-12/13; bridge/
traversal ownership remains unchanged. This does not close unrelated P8 requests, P4 legacy-
geometry/attribute requests, P3 pending re-verification or package grants.

### 11.5 Exact upstream requests

§5.3 is the binding detailed request text; this ledger routes, never grants it:

1. **U1 Phase3/DESIGN:** reconcile Phase3 **"after removing a recognized terminal filter/wrap
   suffix"**, **"stripped and ignored"** (`docs/phase3/v1/PHASE_3_DOC.md:1483-1485`) with DESIGN
   **"ours must honor them"** (`docs/design/v3/DESIGN.md:2476-2478`). Separate sidecar retention
   remains `docs/phase3/v1/PHASE_3_DOC.md:1498-1500` and
   `docs/phase3/v1/PHASE_3_DOC.md:750-750`. PD §11 says
   "pack PNG with `.mcmeta` blur/clamp" (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-626`).
   No downstream reparsing/reconstruction is authorized.
2. **R1 Phase3:** CompanionOptionMacros immediately after engineOptions in load request, same-build
   option/materialization propagation and both fingerprints, INVALID_REQUEST for missing required
   non-Off data; unchanged API/no emitted macros until granted. PBR remains blocked.
3. **R4 Phase3:** post-analysis CompanionMapRequirement memory optimization only; no macro cycle.
4. **R2 Phase5:** fulfilled architecturally by full names/candidates/binds; fresh verification still due.
5. **R3 Phase1:** engine.textures/mod.glue.textures/mod.mixin.textures package grant remains open.
6. **R7-10 Phase6:** inject Phase5 pure FixedSamplerResolver after configuration, unchanged afterBind/
   cache/token/order semantics, no new map or participant; implementation blocked until adoption.
7. **R7-11 Phase6:** exact no-GL retire reason/result operation, reconcile reset(CLOSE); no invented close.
8. **R7-12 Phase8:** same selection/context/TexturePublication/TextureLeaseSource, full expected-id
   acquisition and five-argument sixteen-row physical shadowBindings, close transferred snapshot in
   finally. Supersede old R8-2 four-row view; unchanged traversal/mipmap/neutralization policies.
9. **R7-13 Phase8:** registry-independent ShadowPlanInput(policy,hookHealth); final registry after
   plan in ShadowPassFactory.create. Real shadow NotInstalled until both grants and fresh review.
10. **All four coordinated owners:** fresh whole-document literal PASS before verified downstream
    consumption; prior PASS cannot certify changed §5. No review/manifest/directory roll in this build.

---

## 12. Implementation checklist

Future coding work only; §8 rows specify the observable checks and §9 fixes the milestones.

1. `[v0.1 P4/P5/P7]` Implement sole fixed policy/resolver and full linked sampler layouts with
   candidate-local prelink failure; issued select-once/authenticated activation. Checks:
   sharedUnit_incompatibleAliasesBeforeBind, sharedUnit_fullSamplerShape, sharedUnit_effectiveFallbackLayout.
2. `[v0.1 P5/P7]` Carry selector in snapshots, mutation-free preflight then ascending physical binds,
   Bound-only transfer and local discard; no manual P7 row loop. Checks:
   binding_staleBeforeMutation, binding_absenceVersusIncompatibility, binding_leaseOwnershipAllExits.
3. `[v0.2 P5/P8/P7]` Adopt requested full shadow operation/context and registry-independent planning
   before real installation; preserve bridge/traversal/neutralization. Check: shadow_sharedBindingAndGate.
4. `[v0.5 P13]` Define immutable §2 types, closed unsupported diagnostics, source catalog and paired
   prepared uploads/foreign handles, pre-allocation identity validation. Checks:
   publication_contentIdentity, publication_foreignReloadIdentity, unsupported_noEnumSentinel.
5. `[v0.5 P13]` Preserve every original key/discriminator/canonical ordinal on exact stage expansion
   and retain all target candidates. Checks: custom_stageExpansionExact, sharedUnit_programSpecificTargets,
   sharedUnit_exactNameAndOrdinal, custom_fullscreenFixedOverrides.
6. `[v0.5 P13]` Implement all source forms, raw checked sizes/capabilities, sidecars and actual full
   sampled shape; no parameter mutation of foreign overrides. Checks: publication_contentIdentity,
   sharedUnit_fullSamplerShape, custom_mcmetaBlurSetsFilter, custom_mcmetaClampSetsWrap.
7. `[v0.5 P13]` Implement signed wrapping noise recurrence and override fallback. Checks:
   noise_signedRecurrence, noise_resolutionFromRequirementsAndBaseline, noise_packOverrideReplacesGenerated.
8. `[v0.5 P13/P3/P7]` Static preliminary demand before load; adopt R1 typed macros/fingerprints,
   retain absent fallback until grant; R4 only optimizes physical demand. Check: macro_beforeJcppAndUngrant.
9. `[v0.5 P13]` Discover/build full companions with base extents/mips/default bytes, copied immutable
   animation metadata/pixels. Checks: companion_discoveryPerSprite, companion_layoutAndMipChainMatchBase,
   companion_missingNormalUsesContractDefault, companion_missingSpecularUsesZeroDefault.
10. `[v0.5 P13/P7]` Implement H13 atlas/sprite accessors and post-vanilla snapshot validation; restore
    frame0 on failed live state/hook, invalidate before reload. Checks: animation_postVanillaSnapshot,
    hook_atlasCatalogCapturedAtStitchPost, hook_spriteCompanionAndAnimationRows.
11. `[v0.5 P13]` Provide inactive Unknown/current bound atlasSize and accepted-stitch transitions.
    Check: atlasSize_valueAndValidityWindow.
12. `[v0.5 P13/P7]` Implement expected-id selection-bound lease and all close paths; retire before
    deferred deletion, never delete borrowed resources or reuse by hash alone. Checks:
    binding_leaseOwnershipAllExits, publication_foreignReloadIdentity.
13. `[v0.5 P7/P13]` Implement exact §5.3 accepted-generation pipeline and resize registration strategy,
    compensate every texture build/identity/registration failure off. Check: pipeline_textureFailureCompensates.
14. `[v0.5]` Exercise §6 closed outcomes, memory estimates and shaders-off integrity. Check:
    companion_vanillaAtlasUntouchedWithShadersOff; disabled companion estimate is zero.
15. `[v0.5]` After dependency grants/fresh reviews, run full classic T3 and MC_NORMAL_MAP fixed scenes
    through Phase2. Neither explicit empty publication nor absent macros closes the v0.5 gate.
16. `[post-v0.5 P14]` Optimize measured transfers/sampler objects without changing §4/§5 identity,
    fixed-unit, parameterization, animation or closure contracts.

---

§5 changed in this coordinated rebuild. Unverified; a fresh whole-document review returning literal PASS is required before verified downstream consumption. v1 retained; no directory roll.
