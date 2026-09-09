# Schmaloogium — Phase 9: ID aliasing & per-draw dynamics — Architecture

## 0. Header

**Phase:** 9 — ID aliasing & per-draw dynamics
**Milestone:** v0.3
**Date:** 2026-08-03
**Governing design:** `docs/design/v2.0-RC3/DESIGN.md`, Part I §G0–§G12 and the Phase 9
assignment at lines 2035–2125. This phase deliberately adopts RC3 for its initial build; it does not
change the governing revision of an earlier phase.
**Declared dependencies:** Phases 3, 6, and 7.
**Assigned open questions:** none.

**Integration fix-up (2026-09-07): unverified.** IR-03/04/09/11/22/24 change active
§5 contracts below (D-P9-13…15). Historical dependency PASS evidence is provenance only;
current producer amendments and this consumer adoption require fresh individual verification.

**Whole-owner review-1 correction (2026-09-08): unverified.** C1–C3 are addressed by
D-P9-19…21: schema22 BLOCK dual-era production/selection, the actual TE dispatch target,
and ordered deferred FastTESR native ranges. §5.6 records exact P7/P10 receiver changes;
these coordinated amendments are already receiver-adopted in current P7/P10 (§5.6); the
remaining step is fresh owner/receiver verification, not implementation clearance. Earlier
numeric schema receipts and initial reading claims below are historical, not claims about
this correction's inputs.

At initial authorship, dependency gates were checked against the then-current contracts:

- Phase 3 closes with literal `PASS`, zero findings, in
  `docs/phase3/reviews/PHASE_3_REVIEW_20.md:56`–`:67`.
- Phase 6 closes with literal `PASS`, zero findings, in
  `docs/phase6/reviews/PHASE_6_REVIEW_7.md:50`–`:64`.
- Phase 7 closes with literal `PASS`, zero findings, in
  `docs/phase7/reviews/PHASE_7_REVIEW_19.md:38`–`:49`.

### 0.1 Inputs actually read

Read in the mandated order:

1. `docs/design/v2.0-RC3/DESIGN.md`:
   - all of Part I, §G0–§G12, lines 92–1109;
   - the Phase 9 assignment, lines 2035–2125;
   - only the phase titles/dependency table outside that assignment, lines 580–608.
2. `docs/research/v1/RESEARCH.md`:
   - §0, lines 11–54, and §1, lines 55–107;
   - §3.7, lines 453–462;
   - §4.7, lines 600–618;
   - Appendix D.1, lines 1318–1337;
   - Appendix D.4, lines 1369–1382;
   - Appendix E rows 13–14, lines 1387–1414.
3. Phase-assigned Pintonium and pack-author evidence:
   - `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §8, lines 490–535;
   - `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IdMap.java`,
     whole file;
   - `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java`,
     whole file;
   - the uniform and ID-mapping regions of
     `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:118`–`:182` and `:535`–`:604`.
4. Cleanroom/Forge registry evidence:
   - `reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/fml/common/registry/ForgeRegistries.java:37`–`:55`;
   - `reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/registries/IForgeRegistry.java:37`–`:69`;
   - `reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/fml/common/Loader.java:458`–`:470`,
     `:694`–`:697`, and `:739`–`:742`;
   - `reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/fml/common/ModContainer.java:50`–`:85`;
   - `reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/fml/common/registry/EntityEntry.java:30`–`:68`;
   - `reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/fml/common/registry/EntityRegistry.java:336`–`:385`;
   - `reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/fml/common/event/FMLModIdMappingEvent.java:35`–`:89`.
5. Verified dependency contracts and only the detailed regions needed to interpret them:
   - `docs/phase3/v1/PHASE_3_DOC.md` §5, plus §3.1, §3.5, §4.6, and §4.9;
   - `docs/phase6/v1/PHASE_6_DOC.md` §5, plus §2.2–§2.3, §4.2, §4.4.1,
     §4.4.4, §4.12, and §4.14;
   - `docs/phase7/v1/PHASE_7_DOC.md` §5, plus §4.10.4 and §4.10.8.

Cleanroom MCP was used to re-resolve the load-bearing vanilla symbols rather than inferring them
from patches: `RenderManager.func_188388_a(Entity,F,Z)V`,
`RenderManager.func_188391_a(Entity,D,D,D,F,F,Z)V`,
`TileEntityRendererDispatcher.func_147549_a(TileEntity,D,D,D,F)V`,
`RenderLivingBase.func_177092_a(EntityLivingBase,F,Z)Z`,
`RenderLivingBase.func_177091_f()V`, `RenderLivingBase.field_177095_g`,
`GlStateManager.func_187448_b(IILjava/nio/FloatBuffer;)V`,
`GlStateManager.func_179131_c(FFFF)V`,
`EntityList.func_191306_a(Class)ResourceLocation`, `Block.func_176201_c(IBlockState)I`,
`IBlockProperties.func_185901_i()EnumBlockRenderType`,
`IBlockProperties.func_185906_d()I`, and `ItemBlock.func_179223_d()Block`.

### 0.2 Deviations, extra reads, and reference rulings

The following narrow extra reads closed genuine gaps and are part of this document's provenance:

- `docs/research/v1/RESEARCH.md:438`–`:451` was read because the assignment imports Pintonium's
  tag-expansion claim, while RESEARCH says 1.12.2 has no datapack tags and requires an
  ore-dictionary-style shim with entries-before-tags priority.
- `docs/research/v1/RESEARCH.md:1277`–`:1305` was read to specify the exact Phase 10 service output:
  `renderType<<16 | aliased-block-id` plus metadata.
- `docs/research/v1/RESEARCH.md:1434`–`:1445` and
  `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:13`–`:29` were read because
  `oldHandLight` and `dynamicHandLight` are assigned here but Appendix D alone does not explain the
  policy flags.
- `reference-src/schlorbium-HD_U_G6_pre1/doc/properties_files.txt:85`–`:122` was read because the
  assigned ID-mapping section delegates metadata/property matching syntax to that file.
- The following Pintonium files were read narrowly to verify three load-bearing PD claims:
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/org/embeddedt/embeddium/compat/iris/IBlockEntry.java`,
  `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/BlockEntry.java`,
  `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/IrisVintage.java:131`–`:205`,
  `reference-src/pintonium-9c2fcc1/modern/src/main/shaders_java/net/irisshaders/iris/shaderpack/materialmap/BlockEntry.java`,
  `reference-src/pintonium-9c2fcc1/modern/src/main/shaders_java/net/irisshaders/iris/IrisModern.java:84`–`:105`,
  and
  `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/materialmap/LegacyIdMap.java`.
  They show that `%` tag entries and actual tag lookup are modern-only, while the 1.12.2 parser
  always reports `isTag=false`; the common resolver's tag branch is therefore not a proven 1.12.2
  implementation. They also expose the exact modern-grass ambiguity and show that the fallback
  values are a curated modern table rather than a live 1.12.2 registry projection. The affected
  mechanisms are re-derived in §§4.6–4.8 rather than copied.
- `docs/phase8/v1/PHASE_8_DOC.md` §0,
  `docs/phase8/reviews/PHASE_8_REVIEW_4.md`, `docs/MOVES.md`, and
  `verification/targets/phase-8.json` were inspected only to establish checkout state before the
  Phase 9 scope was known. Phase 8 is not a dependency and no Phase 8 contract or design claim is
  consumed here.
- Checkout rename (2026-09-08): the reference trees cited in this document as
  `reference-src/pintonium-9c2fcc1/**` and `reference-src/cleanroom-0.6.6-alpha/**` now sit
  under `reference-src/` as `Pintonium-main/` and `Cleanroom-0.6.12-alpha/`. Every
  `pintonium-9c2fcc1` and `cleanroom-0.6.6-alpha` path above and in §§3–4 (including the §3
  provenance tags and the §4.2/§4.3/§4.14 citations) is declared an alias of the corresponding
  current checkout name; the recorded line numbers are historical readings of the trees as read
  and are not re-derived here. The rename implies no content change in this document.

The Pintonium file `IdMap.java` imports a transformation-package debug helper. That import was not
followed, no transformation source was read, and the debug-dump path contributes nothing to this
design. No Oculus source/report section, other phase specification, forbidden transcript, root
`*.txt`, web source, or decompiled implementation source was read. No build, test, verify loop, or
adversarial agent was run; this is the build session.

### 0.3 Legal and provenance posture

- RESEARCH is contract authority. Pintonium is LGPL-3.0 evidence, never contract. Each adopted
  contract-visible mechanism has a recorded decision and contract check in §11.
- The two Pintonium transformation boundaries and `glsl-transformation-lib` were not read or used.
  No AGPL material contributes to this design.
- The Schlorbium files read are shipped pack-author documentation. No decompiled class structure,
  method structure, or identifier is reproduced.
- All new implementation is GPL-3.0-or-later. Any later incorporation of LGPL code must preserve
  notices and mark modifications; the algorithms below are independently specified from the
  contract and live-registry abstractions.

Correction evidence: the permitted Cleanroom 0.6.12-alpha
`patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch` (normal/global TE
loops and batch boundary), its dispatcher patch, and
`src/main/java/net/minecraftforge/client/model/animation/FastTESR.java:34–67` were inspected.
The [official Forge dispatcher patch](https://raw.githubusercontent.com/MinecraftForge/MinecraftForge/1.12.x/patches/minecraft/net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.java.patch)
independently establishes `func_180546_a` → `func_192854_a` and deferred fast drawing.
MCP resolves their descriptors to `(TileEntity,FI)V` and `(TileEntity,DDDFIF)V`.
The [vanilla BufferBuilder source mirror](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/BufferBuilder.java)
corroborates that sort produces an ordered quad-index permutation before moving records;
the adapter observes that permutation, never reproduces a second distance comparator.
This is platform behavioral evidence, not copied implementation or transformer code.
Historical 0.6.6-alpha paths above are not claimed recovered or byte-identical.

---

## 1. Scope & boundaries

### 1.1 Owned by Phase 9

Phase 9 owns the complete identity and per-draw value subsystem:

- immutable snapshots of the live block, block-state, item, entity, legacy-tag, and mod-source
  universes, projected through the D-6 seam;
- resolution of Phase 3's ordered unresolved block/item/entity/layer rules against those snapshots;
- deterministic pack-plus-mod merging, precedence, conflict disposition, and diagnostics;
- short names, namespaced names, metadata, property predicates, legacy numeric `id:meta`,
  still/flowing fluid aliases, a modern-to-1.12 name bridge, and the tag shim;
- the conditional `MC_VERSION=11300` BLOCK/ENTITY property selection from Phase 3;
- legacy numeric block fallback when and only when pack `block.properties` is absent;
- immutable publication, generation/fingerprint identity, registry-remap/resource-reload rebuild,
  and stale-consumer rejection;
- the Phase 10 alias lookup that computes the two `mc_Entity` payload words;
- resolved custom render-layer lookup handed to Phase 7;
- main/off-hand alias IDs, static held-block light, `oldHandLight`, and the
  `dynamicHandLight` interoperability disposition;
- balanced per-entity and per-block-entity uniform scopes;
- the Phase 9 specification of real `entityColor` semantics consumed by Phase 7's existing
  color-only producer at v0.1; Phase 9 adds no second writer or v0.3 deferral;
- Phase-9-specific hook health, warn-once keys, and conformance evidence.

### 1.2 Explicit adjacent ownership

- **Owned by Phase 3:** ID-file discovery inside the selected pack, ISO-8859-1/properties-safe
  preprocessing, grammar parsing, A–G macros, source locations/order, `PackConfiguration`, schema
  versioning, and configuration fingerprints. Phase 9 never reopens the selected pack or parses a
  property line independently.
- **Owned by Phase 6:** uniform declarations, locations, activation refresh, redundant-upload
  suppression, GL error isolation, neutral cells, and the `UniformEventSink`. Phase 9 supplies typed
  values and scope order only; it never uploads GL directly.
- **Owned by Phase 7:** the world-frame transaction, current program/scope selection, surrounding
  `gbuffers_entities`/`gbuffers_block` scopes, frame abort/reset, reload coordination, and hook-health
  publication. Phase 9 augments H-ENTITY-02/03 and consumes the safe integration points requested
  in §5.4.
- **Owned by Phase 10:** chunk-build entity stack, 56-byte vertex format, writing the two Phase 9
  payload words into vertices, VBO/client-array attribute delivery, and stale-mesh rebuild mechanics.
- **Owned by Phase 12:** GUI and persistence for user-overridable shader settings. Phase 9 defines
  the typed hand-light policy input and defaults, not a screen.
- **Owned by another dynamic-lights mod:** changing world lighting or injecting a dynamic hand light.
  Dynamic lights remain a written non-goal (`docs/research/v1/RESEARCH.md:72`–`:80`). Phase 9 may
  suppress an explicitly installed compatible provider when `dynamicHandLight=false`; it never
  implements that feature.
- **Owned by Forge/Cleanroom glue:** enumeration of live registries/mod containers, jar/directory
  resource reads, Minecraft-object-to-dense-ordinal identity maps, block-state inspection, hand
  sampling, and exact Mixin/event adapters.

### 1.3 Hard boundaries

No Minecraft, Forge, Cleanroom, Mixin, LWJGL, `ResourceLocation`, `ItemStack`, `IBlockState`,
`Entity`, or `TileEntity` type crosses into `:engine`. Resolution never mutates Phase 3 data or a
published table. Mixins contain no alias, precedence, light, or color policy. Phase 9 does not stamp
a vertex, select a program, change a render layer directly, rescan shader-pack bytes, create a
dynamic-light implementation, or observe TexEnv state after the fact.

R9-1 is owner-designed and receiver-adopted, unverified, under current Phase 3 §5.
R9-2's coordinated transaction and hook surfaces are receiver-adopted from current Phase 7
§§4.1/5; the shadow admission and color reconciliation below are coordinated unverified amendments.
Fresh owner/consumer reviews remain implementation gates, not missing-API fallbacks.

---

## 2. Architecture overview

### 2.1 Placement

Pure policy and algorithms live under the existing `engine.config` root, in an `id` subpackage:

```text
:engine / engine.config.id
  IdRuntimeBuilder        immutable merge/resolution candidate construction
  IdRegistrySnapshot      loader-neutral registry/state/tag projection
  ModIdSourceSnapshot     bounded, attributed per-mod source corpus
  CompatibilityAliasCatalog / LegacyTagCatalog
  PublishedIdRuntime      immutable hot lookups + generation/fingerprint
  PerDrawDynamics         balanced uniform-value state machine

:mod / mod.glue.id
  ForgeIdSnapshotProvider       ForgeRegistries/EntityList/BlockState projection
  ForgeModIdSourceProvider      active-mod jar/directory resource snapshot
  HeldHandsProvider             player hands + ItemBlock/default-state light
  IdHookBridge                  frame/entity/TE/color adapters
  IdReloadBridge                resource reload + FMLModIdMappingEvent queuing

:mod / mod.mixin.frame
  the already catalogued H-ENTITY/H-COLOR injections, ordered as §4.12 specifies
```

No new Gradle project or loader-facing engine package is introduced.

### 2.2 Public shape

The signatures are illustrative Java; the value/lifecycle semantics are binding.

```java
public interface IdRuntimeBuilder {
    IdBuildResult build(IdBuildRequest request);
}

public record IdBuildRequest(
    IdMappingInput mappings,
    IdRegistrySnapshot registries,
    ModIdSourceSnapshot modSources,
    CompatibilityAliasCatalog aliases,
    LegacyTagCatalog tags,
    HandLightPolicy handLightPolicy,
    DiagnosticReporter diagnostics) {}

public sealed interface IdBuildResult {
    record Built(IdRuntimeCandidate candidate) implements IdBuildResult {}
    record Failed(IdBuildFailure failure) implements IdBuildResult {}
}

public interface IdRuntimeCandidate extends AutoCloseable {
    IdRuntimeView view();
    void close();
}

public interface IdRuntimePublisher {
    IdPublishResult publish(IdRuntimeCandidate candidate, IdPublishContext context);
    Optional<PublishedIdRuntime> current();
    IdDeactivateResult deactivate(IdPublishContext context);
}

public interface PublishedIdRuntime extends AutoCloseable {
    long generation();
    IdRuntimeFingerprint fingerprint();
    AliasLookup aliases();
    RenderLayerLookup renderLayers();
    PerDrawDynamics perDraw();
    void close();
}

public interface AliasLookup {
    long generation();
    AliasValue blockId(int blockStateOrdinal);
    AliasValue itemId(int itemOrdinal);
    AliasValue entityId(int entityTypeOrdinal);
    BlockStampResult mcEntity(int blockStateOrdinal);
}

public record AliasValue(boolean present, int shaderId) {}
public sealed interface BlockStampResult {
    record Present(int packedRenderTypeAndId, int metadata) implements BlockStampResult {}
    record Absent(int packedRenderTypeAndZero, int metadata) implements BlockStampResult {}
    record Unrepresentable(int packedRenderTypeAndZero, int metadata)
        implements BlockStampResult {}
}

public interface RenderLayerLookup {
    Optional<ResolvedRenderLayer> layer(int blockStateOrdinal);
}
public enum ResolvedRenderLayer { SOLID, CUTOUT, CUTOUT_MIPPED, TRANSLUCENT }
```

`AliasValue.present=false` always carries `shaderId=0`; shader ID zero may also be explicitly mapped,
so presence is never inferred from the integer. `current()` returns a borrowed publication; Phase 7
owns its lifetime. A successful `publish` transfers the candidate exactly once. All failed or
rejected publications leave the candidate caller-owned and do not alter the current runtime.

### 2.3 Registry projection

`IdRegistrySnapshot` is a deep-immutable, schema-versioned collection of plain values:

```java
public record IdRegistrySnapshot(
    long registryGeneration,
    RegistryFingerprint fingerprint,
    List<BlockTypeRecord> blocks,
    List<BlockStateRecord> blockStates,
    List<ItemTypeRecord> items,
    List<EntityTypeRecord> entities,
    TagMembershipSnapshot tags) {}

public record BlockTypeRecord(
    int blockOrdinal, RegistryName name, int liveLegacyNumericId,
    List<Integer> stateOrdinals) {}
public record BlockStateRecord(
    int stateOrdinal, int blockOrdinal, int legacyMetadata,
    SortedMap<String, String> properties, int renderType,
    boolean solidOpaqueCube, int emittedLight) {}
public record ItemTypeRecord(
    int itemOrdinal, RegistryName name, OptionalInt placedBlockDefaultStateOrdinal) {}
public record EntityTypeRecord(int entityTypeOrdinal, RegistryName name) {}
public record RegistryName(String namespace, String path) {}
```

Lists are sorted by `(namespace,path)` and then canonical state-property tuple, not registry iterator
order. Ordinals are dense and snapshot-local. The glue retains separate identity maps from canonical
Minecraft objects/classes to those ordinals; they never cross the seam. `renderType` is the live
`EnumBlockRenderType.ordinal()` value and `legacyMetadata` is the live
`Block.getMetaFromState(state)` value. Light is the state's zero-context emitted value, clamped only
after range validation. The snapshot rejects duplicate names/ordinals, non-dense state ordinals,
invalid property tuples, light outside 0–15, and inconsistent fingerprints before resolution.

### 2.4 Major relationships

```text
Phase 3 PackConfiguration + requested IdMappingInput
        ModIdSourceSnapshot ──Phase 3 IdMappingParser──┐
Forge IdRegistrySnapshot ──────────────────────────────┼─ IdRuntimeBuilder
alias/tag/policy catalogs ─────────────────────────────┘        │
                                                         immutable candidate
                                                                │ safe boundary
Phase 7 reload/frame coordinator ─────────────────────── IdRuntimePublisher
                                                                │
                          ┌─────────────────────────────────────┼──────────────┐
                          ▼                                     ▼              ▼
                    Phase 10 mc_Entity                  Phase 7 layers   Phase 6 events
                    lookup + generation                 + chunk rebuild  held/entity/TE/color
```

The core invariant is that one published runtime is derived from exactly one Phase 3 schema and
configuration fingerprint, one registry fingerprint, one mod-source fingerprint, and one alias/tag
policy version. A consumer never combines components from different identities.

---

## 3. Contract conformance map

| In-scope contract/evidence item | Satisfying design element | Provenance and disposition |
|---|---|---|
| `block.properties`, `item.properties`, `entity.properties` | Phase 3 `IdMappingInput` plus §4.3 merge | `[V:doc]` `docs/research/v1/RESEARCH.md:455`–`:460`; grammar remains Phase 3-owned |
| short `red_flower`, namespaced `minecraft:red_flower` | `RegistryName` normalization and exact selector pass (§4.5) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:546`–`:553` |
| property-matched block forms | typed predicate compilation against canonical state tuples (§4.5) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/properties_files.txt:109`–`:122` |
| legacy numeric `id:meta` | live numeric-ID index plus metadata filter (§4.5) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:550`–`:553` |
| standard A–G macros, no option macros | consumed Phase 3 result; forced retry delegates to Phase 3 | `[V:doc]` `docs/research/v1/RESEARCH.md:463`; no parser duplication |
| per-mod block/item/entity files | bounded active-mod snapshot and deterministic merge (§4.3) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:537`–`:540`, `:576`–`:595` |
| pack wins over mod | precedence tier 0 before mod tiers (§4.4) | Governing Phase 9 assignment `docs/design/v2.0-RC3/DESIGN.md:2069`–`:2071`; D-P9-2 |
| first-writer-wins and order significance | fill-only table writes within each precedence class (§4.4) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java]` `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java:54`–`:67`; PD §8.1; contract checked by D-P9-2 |
| metadata and property predicates | state filtering before fill-only writes (§4.5) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java]` `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java:50`–`:68`; pack-author syntax above; D-P9-2 |
| explicit entries beat tags | within each pack/mod source, its explicit pass completes before its tag pass (§4.4/§4.7) | `[V:doc]` `docs/research/v1/RESEARCH.md:448`; pack-over-mod remains the outer precedence, Pintonium 1.12 tag proof rejected, D-P9-5 |
| tag expansion | explicit `LegacyTagCatalog`/membership provider, unknown-tag absence (§4.7) | RESEARCH's required shim at `docs/research/v1/RESEARCH.md:448`; not claimed as proven by Pintonium |
| still↔flowing water/lava | symmetric exact candidate expansion (§4.6) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java]` `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java:81`–`:88`; D-P9-3 |
| modern→1.12 alias table and modern `minecraft:grass` case | versioned live-validated catalog and legacy-preserving era evidence (§4.6) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java]` `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java:71`–`:119`; PD §8.1; contract check D-P9-3 |
| entity `MC_VERSION=11300` retry when normal present-file result is empty | Phase 3 alternate parse selected by §4.7 | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IdMap.java]` `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IdMap.java:65`–`:73` and `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IdMap.java:132`–`:141`; D-P9-4; R9-1 gated |
| OF-legacy numeric fallback only without pack `block.properties` | explicit mod mappings, then live vanilla numeric fill (§4.8) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IdMap.java]` `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IdMap.java:75`–`:89`; values re-derived, D-P9-6 |
| custom `layer.solid/cutout/cutout_mipped/translucent` | resolved state-layer table (§4.9) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:555`–`:568` |
| solid opaque cubes excluded from custom layer | snapshot predicate rejects layer assignment (§4.9) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:570`–`:574` |
| `heldItemId` / `heldItemId2` | item alias lookup from both hand samples (§4.11) | `[V:doc]` `docs/research/v1/RESEARCH.md:1323`–`:1328`; no Pintonium 1.12 producer inherited |
| `heldBlockLightValue` / `2`, brighter-hand-wins old mode | static held-state light and exact Phase 6 tuple (§4.11) | `[V:doc]` `docs/research/v1/RESEARCH.md:1329`; Phase 6 contract `docs/phase6/v1/PHASE_6_DOC.md:800`–`:802` plus `:811`–`:815` |
| `dynamicHandLight` | optional external-provider suppression only (§4.11) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:23`–`:25`; dynamic lights non-goal preserved |
| `entityId` | balanced H9-ENTITY-ID scope (§4.12) | `[V:doc]` `docs/research/v1/RESEARCH.md:1373`–`:1375`; App E row 13 `docs/research/v1/RESEARCH.md:1413` |
| `blockEntityId` is current TE's aliased block ID | balanced H9-BLOCK-ENTITY-ID scope (§4.12) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:178`–`:180`; App E row 14 `docs/research/v1/RESEARCH.md:1414` |
| `entityColor` hurt/flash multiplier | exact vanilla computed-color argument capture (§4.13) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:178`; reference-free gap acknowledged, D-P9-7 |
| per-draw values refresh at hooks and are excluded from custom expressions | Phase 6 sink plus balanced immediate update/restoration | `[V:doc]` `docs/research/v1/RESEARCH.md:1369`–`:1382` |
| Phase 10 alias service: `renderType<<16 | aliasedBlockId`, metadata | `AliasLookup.mcEntity` exact bit contract (§4.10) | `[V:observed]` `docs/research/v1/RESEARCH.md:1297`–`:1305`; D-P9-10 |
| unknown names never crash | absence bit, once diagnostics, old/neutral behavior (§4.14/§6) | G2.4 and Phase 9 assignment `docs/design/v2.0-RC3/DESIGN.md:2110`–`:2112` |
| tables rebuilt, never mutated in place | candidate/publication lifecycle (§4.1) | Phase 9 architecture requirement `docs/design/v2.0-RC3/DESIGN.md:2110`–`:2112`; D-P9-1 |

The PD claim that tag expansion is “working in production” is not admitted as 1.12.2 evidence. The
listed 1.12 source creates only non-tag entries, while the modern source owns `%` parsing and tag
lookup. RESEARCH's shim requirement therefore controls.

---

## 4. Detailed design

### 4.1 Candidate, publication, and reset state machine

The lifecycle is closed:

```text
NO_RUNTIME
  └─ build immutable inputs ─► CANDIDATE
       ├─ validation/build failure ─► NO_RUNTIME (old publication unchanged)
       ├─ close by caller ──────────► CLOSED
       └─ publish at safe boundary ─► PUBLISHED(generation)
              ├─ replacement ──────► RETIRED ─► CLOSED after borrowers expire
              ├─ shaders off ──────► RETIRED ─► CLOSED
              └─ context/world close ► RETIRED ─► CLOSED
```

Build performs no publication and mutates no prior table. A candidate contains primitive arrays,
immutable key maps, conflict/diagnostic summaries, and its complete identity. `view()` is
operation-free and does not transfer ownership. Publication occurs on the render thread only after
Phase 7 has validated matching pack/configuration/registry identities and before a new frame may
open. Generation is a monotonically increasing positive `long`; overflow is terminal shaders-off,
not wraparound.

A frame borrows one publication generation. Entity/TE scopes and held events must echo that
frame and generation; P7's independent color-only scope does not require an ID runtime.
Phase 10 chunk-build work records the lookup generation in its build context;
publication replacement makes work/results with another generation stale. Retired tables remain
alive until already-issued frame/build borrows end, but no new borrow can target them.
The lookup and its glue-owned Minecraft-object-to-ordinal map are one matched borrow lifetime.
Phase 7 stops new Phase 10 work, revokes upload/draw eligibility, cancels queued tasks/uploads,
drains running workers without holding pool locks, and only then closes the retired lookup/map.
An incomplete drain keeps both alive and admission closed; off recovery never frees arrays still
borrowed by a worker. Equal fingerprints do not authorize pairing an old ordinal map with a new
lookup. Pure candidate failure leaves the local publisher untouched, not permission for Phase 7
to resume the old pipeline after a failed coordinated rebuild.

### 4.2 Live-registry snapshot and D-6 adapter

`ForgeIdSnapshotProvider` runs after registries are frozen and again after a reported ID remap. It
enumerates `ForgeRegistries.BLOCKS`, `.ITEMS`, and `.ENTITIES`, which Cleanroom exposes explicitly at
`reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/fml/common/registry/ForgeRegistries.java:45`–`:54`.
Names and values come through `IForgeRegistry.getEntries/getKey/getValue`, not private GameData
maps (`reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/registries/IForgeRegistry.java:45`–`:59`).

For every block, glue enumerates all valid canonical states and records:

1. the registry name and current numeric registry ID;
2. `Block.getMetaFromState(state)`;
3. a sorted property-name → serialized-value tuple using each `IProperty`'s canonical name;
4. `state.getRenderType().ordinal()`;
5. `state.isFullCube() && state.isOpaqueCube()` for the custom-layer exclusion; and
6. `state.getLightValue()` for zero-context held-block light.

Provider exceptions or inconsistent enumeration fail the snapshot atomically. The previous snapshot
may be reused only when the registry fingerprint is identical. The runtime identity maps
`IBlockState`, `Item`, and exact entity registration class to dense ordinals. `EntityList.getKey`
and the Forge `EntityEntry.getRegistryName/getEntityClass` pairing provide the entity type; an
unregistered runtime subclass is absent rather than attributed to a guessed superclass.

### 4.3 Per-mod source acquisition

`ForgeModIdSourceProvider` takes an immutable snapshot of active `ModContainer`s. Cleanroom exposes
an active list (`Loader.getActiveModList`) and each container's mod ID and resource file/directory at
`reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/fml/common/Loader.java:739`–`:742`
and `reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/fml/common/ModContainer.java:56`–`:85`.

For each valid lowercase mod ID, it reads only these exact paths from that container's resource root:

```text
assets/<modid>/shaders/block.properties
assets/<modid>/shaders/item.properties
assets/<modid>/shaders/entity.properties
```

Jar and directory sources share path-containment, entry-count, per-file-byte, total-byte, and
decompression-ratio limits. Symlinks that leave a directory root, duplicate normalized archive
entries, NUL/absolute/`..` paths, and non-regular files are rejected for that mod. Pass bounded
`ImmutableBytes` to `IdMappingParser.parse` with kind, attributed origin, exact published
parserEnvironment and reporter; Phase 3 alone decodes ISO-8859-1 and parses Properties/macros.

Sources are ordered by Unicode code-point `modid`, then mapping kind. Duplicate active mod IDs are
reported as an invalid mod-source snapshot; Forge normally rejects them, but Phase 9 does not rely
on that side effect. One unreadable/invalid mod file warns once and contributes no rules while
sibling mods and mapping kinds continue.

### 4.4 Merge and precedence algebra

Resolution uses this fixed precedence, highest first:

1. explicit pack entry selectors, in Phase 3 source order;
2. pack tag selectors, in source order;
3. for each mod in §4.3 order: that mod's explicit entry selectors, then that mod's tag selectors,
   each in source order;
4. legacy numeric block fallback, only under §4.8's absence condition.

Layer rules use tiers 1–3 only. Items and entities have no tier 4. “Explicit” includes exact names,
legacy numeric selectors, metadata/property selectors, and validated compatibility-name expansions.
Entry-before-tag precedence applies inside one pack/mod contribution; pack-over-mod is the outer rule,
so a pack tag still beats any mod entry. This is the only ordering that satisfies the assignment's
unqualified “pack wins over mod” rule without losing RESEARCH's entries-before-tags rule inside the
source for which a tag is an alternate selector.

Each tier iterates rules and candidate live objects deterministically. A successful match performs
`assign-if-absent`; a later rule never overwrites the cell. Pack therefore wins over all mods,
earlier mod IDs win between mods, explicit entries win over tags from the same source, and first
writer wins within each class. A losing assignment is not an error; one attributed conflict
diagnostic records winner and loser origins. Duplicate candidates within one selector are
de-duplicated before assignment.

### 4.5 Selector resolution

Short names receive namespace `minecraft`; namespaced names are lower-case validated
`namespace:path` pairs. Resolution never silently falls back from an unknown explicit namespace to
`minecraft`. Exact registry lookup occurs before compatibility-name expansion.
One era-provenance exception exists: for an ambiguous token under §4.6, a rule carrying
validated `MappingEra.MODERN` provenance replaces exact lookup with its alias expansion for
that selector, while the CLASSIC/neutral path keeps the token's exact 1.12 meaning and every
other selector keeps exact-before-expansion.

For blocks:

- absent metadata/properties selects all valid states of the resolved block;
- present `MetadataConstraint.alternatives` is an immutable authored-order union of inclusive
  `IntegerRange` values; test captured `legacyMetadata` directly, never enumerate range members;
- every property name must exist. `PropertyValueConstraint.Literal.value` matches exact canonical
  captured value text; `IntegerInterval.range` matches only canonical nonnegative decimal state
  values (`0|[1-9][0-9]*`, checked int) within its endpoints;
- OR alternatives within each property; AND metadata and all property predicates;
- validate every literal against the finite live property domain and require each interval to
  intersect that domain. Missing property, unknown literal, disjoint interval or no matching
  state produces one attributed warning and assigns nothing for the whole selector. Absent
  intermediate integers within an intersecting interval are legal. Never drop invalid predicates.

**D-P9-23 typed selector receipt:** consume P3 D-P3-73's exact `Optional<MetadataConstraint>`,
`IntegerRange`, `PropertyPredicate` and closed `PropertyValueConstraint` variants. P3 already
validates syntax/ascending/overflow/duplicates/metadata0..15 and publishes one rule per selector.
P9 interprets captured registry values only; no raw selector reparse, endpoint expansion or
replacement grammar. Preserve typed variants, all boundaries/order/provenance and selected parse
with the live snapshot in derived identity. Apply identically to pack/mod, ordinary/alternate
BLOCK and layer rules before existing precedence and publication.

Legacy numeric `id:meta` first resolves the block whose **current snapshot numeric ID** equals the
numeric selector, then applies the same metadata filter. Registry remaps therefore require a new
snapshot/publication.

Items and entities support short and namespaced exact names. A predicate-bearing item/entity rule is
unsupported on 1.12.2: it warns and matches nothing, never strips the predicate and overmatches. This
retains Pintonium's useful warning posture without inheriting its missing held/TE producers.

### 4.6 Fluid and modern-name compatibility aliases

`CompatibilityAliasCatalog` is immutable, versioned, fingerprinted data. Each symbolic source/target
pair is validated against the live snapshot; a missing target disables only that pair with one
diagnostic. The v0.3 catalog contains exactly the assignment's seeds:

| Pack-facing source | 1.12 target |
|---|---|
| `minecraft:grass_block` | `minecraft:grass` |
| `minecraft:short_grass` | `minecraft:tallgrass` |
| `minecraft:tall_grass` | `minecraft:double_plant` |
| `minecraft:dead_bush` | `minecraft:deadbush` |
| `minecraft:sugar_cane` | `minecraft:reeds` |
| `minecraft:lily_pad` | `minecraft:waterlily` |
| `minecraft:cobweb` | `minecraft:web` |
| `minecraft:redstone_lamp` in a proven modern branch | `minecraft:lit_redstone_lamp` |

`minecraft:water` and `minecraft:flowing_water` each expand to both live water entries; lava is
identical. Exact candidate order is the written token first, counterpart second, but §4.4
assign-if-absent still controls.

The ambiguous tokens `minecraft:grass` and `minecraft:redstone_lamp` already have 1.12 meanings.
RESEARCH's fixed classic contract wins over a heuristic. They retain their exact 1.12 meaning unless
Phase 3 supplies `MappingEra.MODERN` provenance for that rule. With that provenance,
`minecraft:grass` targets `minecraft:tallgrass` and `minecraft:redstone_lamp` targets the lit block.
The sole modern provenance producer is Phase 3's current isolated BLOCK alternate:
identical copied bytes/environment except `MC_VERSION=11300`, with alternate rules MODERN
and ordinary rules CLASSIC. §4.7 selects it only for `PRESENT_EMPTY` with a nonempty alternate.
Neither surviving-branch inference nor IDs, filenames, pack names, or neighboring tokens prove
era. Ordinary nonempty blocks retain classic meanings. Classic/neutral fallback is not completion
of the mandatory modern special case; producer-backed conditional fixtures must reach it.

This preserves classic semantics while adopting the table mechanism. Extending the catalog requires
a named pack fixture, a live-target proof, a catalog-version increment, and a D-P9 addendum; runtime
name similarity never manufactures an alias.

### 4.7 Tag shim and BLOCK/ENTITY era selection

#### Tag shim

Phase 3 must classify `%namespace:path` as `SelectorKind.TAG` without resolving it. Phase 9's
`LegacyTagCatalog` maps that key to one or more named membership providers. A provider returns only
registry names present in the same `IdRegistrySnapshot`; all membership is frozen into
`TagMembershipSnapshot` before resolution.

The v0.3 provider set is:

- explicit GPL-owned compatibility definitions derived and tested against the pack matrix;
- exact Forge OreDictionary memberships through a versioned, explicit tag-key ↔ ore-name table;
- optional mod contributors registered before snapshot freeze through a loader-side API.

There is deliberately no automatic camel-case/path conversion between modern tags and OreDictionary
names: that would silently conflate different taxonomies. Missing provider/key, provider exception,
or empty expansion warns once and assigns nothing. A tag expansion is sorted by registry name and
then enters the delayed tag tiers in §4.4. This is the ore-dictionary-style shim RESEARCH requires,
not a claim that 1.13 datapack tags exist.

#### BLOCK/ENTITY `MC_VERSION=11300` selection

For each pack or bounded mod BLOCK and ENTITY input independently, before §4.4 precedence:

1. `ABSENT`: contribute nothing and never select an alternate.
2. `PRESENT_RULES`: use ordinary rules only, even if the alternate differs or matches more objects.
3. `PRESENT_EMPTY` with nonempty `forced11300Rules`: select that list alone.
4. `PRESENT_EMPTY` with empty/invalid alternate: contribute no rules, retain attributed diagnostics.

Phase 3 supplies both lists using the same copied bytes, origin, finite bounds, parser options
and exact published A–G environment, replacing only MC_VERSION with integer 11300 in the private
alternate. Ordinary state never changes; ordinary rules are CLASSIC, alternate rules MODERN.
ITEM/LAYER have no alternate. Zero registry matches in an ordinary nonempty list do not trigger
retry. Never merge lists, override ordinary rules, retry a selected pack read or mutate
RuntimeIdentityData/options/macros. The same rule applies to every bounded mod parser result.
Phase 3 fingerprints bytes/environment/state/both lists; P9's derived source/runtime identity
additionally encodes selected-list discriminator, source fingerprint and alias catalog version.
The current selector schema retains projectionVersion=1 and the nine source-free inspection trees.

### 4.8 Legacy numeric fallback

Fallback activates exactly when the **pack** `block.properties` state is `ABSENT`. `PRESENT_EMPTY`
does not activate it. Explicit mod rules still run before fallback so the Forge extension point can
override the default for its own blocks.

For every still-unassigned state whose block namespace is `minecraft`, fallback assigns the block's
current live numeric registry ID as the shader ID. Metadata remains the state's live captured
metadata. Non-vanilla mod blocks remain absent unless a mod rule assigns them; their load-order IDs
are not advertised as stable pack-facing defaults. This re-derives 1.12 values from the live
registry, avoids Pintonium's modern curated/magic list, and naturally rebuilds after a mapping event.

If a vanilla live ID is outside the representable Phase 10 low-word range, the block uniform alias
remains the full `int`, but `mcEntity` returns `Unrepresentable` and a zero low word; §4.10 defines
the representation boundary.

### 4.9 Custom render layers

Layer selectors use the same exact/compatibility/tag resolution and precedence as block IDs, but
write a separate `ResolvedRenderLayer[]`. A state with `solidOpaqueCube=true` is always excluded,
even from `layer.solid`, and produces one source-attributed warning. Unknown/unsupported selectors
do not move a state.

The lookup is a decision, not a renderer mutation. Phase 7 asks it during terrain dispatch/chunk
classification and otherwise preserves vanilla's layer. A publication-generation change invalidates
the layer decision alongside aliases and requires Phase 10/vanilla chunk rebuild scheduling before
stale compiled chunks are drawn.

### 4.10 Phase 10 alias service

For a valid state ordinal, `blockId` returns the full mapped `int` plus presence. `mcEntity` creates
the two exact values Phase 10 stamps:

```text
lowId       = aliasedBlockId & 0xffff
highType    = renderType & 0xffff
packedWord  = (highType << 16) | lowId
metadata    = legacyMetadata & 0xffff
```

The representable alias domain for the 16-bit vertex field is `-32768..65535`, interpreted by low
16-bit pattern. `renderType` and metadata must be `0..65535`. A full alias outside that domain is
not truncated silently: `Unrepresentable` carries a zero low word, keeps validated render type and
metadata, and emits one diagnostic per rule/generation. An absent alias returns `Absent` with the
same zero low word. Invalid/stale ordinals are programmer/protocol rejections and never index an
array.

`AliasLookup.generation` must equal the Phase 10 build-context generation. The chunk result records
that generation; Phase 10 discards a result whose generation is not current and schedules rebuild.
Lookups are O(1) primitive-array reads, allocation-free in the implementation, and do not inspect a
registry, property map, or diagnostic set on the hot path.

### 4.11 Held items and hand-light policy

`HeldHandsProvider` samples the current view player's main/off-hand stacks after Phase 6 accepts the
frame identity and before the first shader draw. It emits immutable values:

```java
public record HeldStackValue(boolean empty, int itemOrdinal, int staticLight) {}
public record HeldHandsValue(
    long worldEpoch, long logicalTick,
    HeldStackValue main, HeldStackValue off) {}
```

An empty or unknown item has alias ID 0 and light 0. A non-empty item gets its ID from the current
item table. Static light is 0 unless the item has a captured `ItemBlock` default-state relation, in
which case it is that state's validated 0–15 emitted light. Phase 9 does not invent luminosity for
non-block items.

`HandLightPolicy` resolves Phase 3's pack tri-state and decoded Phase 12 user setting:

- wire key `oldHandLight` accepts exactly `default|true|false`, absent means `default`;
- explicit decoded user TRUE/FALSE wins, else explicit pack TRUE/FALSE wins;
- both DEFAULT resolve `true`, the local backward-compatible fallback (D-P9-8), not
  a claim that the shipped documentation specifies a missing-value default;
- Phase 3 resolves load-time `MC_OLD_HAND_LIGHT` from this same user→pack→true rule after
  its option-macro-free Properties parse, before shader preprocessing. The runtime still
  resolves typed values, never infers policy from macro presence or rewrites finalized macros;
- `dynamicHandLight=DEFAULT` resolves `true`, but has an effect only when a recognized external
  `DynamicHandLightInterop` is installed.

For lights `(main,off)`, old mode sends `(max(main,off), off)`; normal mode sends `(main,off)`,
exactly matching Phase 6's binding contract. Item IDs never swap when old mode chooses the brighter
hand. The resulting `HeldItemSample` uses the current world/tick identity and enters
`UniformEventSink.updateHeldItems`. It is recomputed on hand change, alias publication, policy
change, world epoch, or runtime publication; equal tuples do not generate another event.

When `dynamicHandLight=false`, Phase 9 asks an explicitly installed compat bridge to suppress that
external mod's dynamic contribution during the Phase 7 hand scope. Absence/failure of the bridge is
a feature-level diagnostic and no-op. Phase 9 never changes world light, creates a light source, or
pretends the static held-light uniforms are dynamic lights.

### 4.12 Entity and block-entity ID scopes

The hook ledger is binding:

| ID / class | SRG target and injection | Ordered action | Health / fallback |
|---|---|---|---|
| H9-ENTITY-ID-01 `RenderManager` | both `func_188388_a(Entity,F,Z)V` and `func_188391_a(Entity,D,D,D,F,F,Z)V`, HEAD/RETURN with Phase 7 boundary drain on throw | resolve exact entity type ordinal; push prior `entityId`; call `updateEntityId(mapped-or-0)`; on exit validate LIFO token and restore prior value | `FEATURE`; App E row 13; disable ID feature + reset 0 on mismatch |
| H9-BLOCK-ENTITY-ID-01 `TileEntityRendererDispatcher` | AROUND `func_192854_a(TileEntity,D,D,D,F,I,F)V`, inside the correspondingly retargeted P7 H-ENTITY-03 | authenticate main block or current shadow admission; resolve TE world/position ordinal; enter mapped-or-0 scope, execute actual dispatch, restore in finally before admission release; deferred branch additionally uses §4.12.1 | one method wrapper, one finally, runtime one balanced pair per invocation; unavailable/unbalanced wrapper prevents shader admission; detached TE uses 0 |
| H9-HELD-01 Phase 7 accepted-frame boundary | immediately after Phase 6 `beginFrame` and before shadow/gbuffers activation | sample both hands once, resolve tuple, publish changed `HeldItemSample` | `FEATURE`; invalid sample uses zeros for this frame |

Both RenderManager methods are hooked because either may be an external entry point. If one calls
the other, the nested same-ID scopes are legal and restore same-ID then parent-ID. Phase-9 scope
tokens contain frame ID, runtime generation, stack kind, depth, and epoch; only the exact LIFO token
can close. Fixed-capacity primitive stacks cover ordinary nesting and grow only at a safe point up to
a finite cap. Overflow, wrong order, stale generation, or underflow disables that ID producer for the
frame, writes neutral zero, reports once, and asks Phase 7 to drain it at the frame boundary.

Every normal RETURN restores in reverse order. Because HEAD/RETURN Mixins cannot guarantee RETURN on
a throwable, Phase 7's H-FRAME finally/abort path calls `PerDrawDynamics.resetFrame`, which clears
both stacks and sends zero before any later shader draw. Mixins only forward object ordinals and
call-local tokens; all validation/policy lives in glue/engine.

Main and shadow admission are distinct capabilities, not matching numeric records:

```java
interface IdScopeAdmission {} // opaque, issued and validated by Phase 7 glue
enum IdScopeRejection { STALE_ADMISSION, STALE_GENERATION, WRONG_THREAD, STACK_LIMIT }
sealed interface IdScopeResult {
    record Entered(IdScopeToken token) implements IdScopeResult {}
    record Rejected(IdScopeRejection reason) implements IdScopeResult {}
}
// Operations on PerDrawDynamics:
IdScopeResult enterEntity(IdScopeAdmission admission, int entityOrdinal);
IdScopeResult enterBlockEntity(IdScopeAdmission admission, int stateOrdinal);
void leave(IdScopeToken token);
```

Phase 7 issues main admission only from the accepted entity/block scope and shadow admission
only after `ShadowExecutionBridge.validate(...)` returns `Valid` for the exact current
`ShadowExecutionView`. Issuance/entry checks thread, frame, pipeline/ID generation, slot/execution
epoch and live dynamic extent. `IdScopeToken` additionally authenticates stack kind/depth and
admission kind; it is opaque, single-close, invalidated on drain. Unknown ordinal uses 0, whereas
stale generation rejects before lookup. Shadow entry never opens a gbuffers scope, acquires a main
snapshot or activates/restores a main program. Nested entity/TE exits restore the immediately
preceding IDs before shadow admission releases, preserving Phase 8 Forge pass ordering. Invalid
leave, overflow or throw neutralizes the affected cells and drains scopes before any later draw;
Phase 7/P8 failure containment owns whether the shadow pass aborts.

**Actual dispatch coverage.** Normal and global RenderGlobal TE loops call
`func_180546_a(TileEntity,F,I)V`, which performs distance/load/light checks then calls
`func_192854_a(TileEntity,D,D,D,F,I,F)V` directly. Damaged-block rendering supplies the
destroy-stage argument through that world overload; it reaches the same lower dispatch.
The direct `func_147549_a(TileEntity,D,D,D,F)V` convenience overload forwards to the lower
dispatch, as do callers explicitly supplying destroyStage/alpha. Hook only the lower
dispatch: outer overloads own no additional P9 scope. Nested renderer dispatches each
receive their own token and restore the parent, including same-TE nesting. Culled/no-renderer
calls draw nothing; normal, damaged and direct calls retain their original arguments and
renderer choice. The P7 wrapper surrounds the P9 wrapper; both use finally, including a
renderer exception wrapped by vanilla. Frame drain is a backstop, not deferred restoration
before another renderer. Shadow traversal reaches this same hook under current shadow
admission, without main program activation. Appendix E row14 remains historical candidate
evidence; `func_147549_a` is not the active hook target.

#### 4.12.1 FastTESR: preserve deferred ordering with ID-homogeneous native ranges

Forge's `drawingBatch && te.hasFastRenderer()` calls the registered renderer's
`renderTileEntityFast(...,batchBuffer)`; the later dispatcher `drawBatch(int pass)` performs
the native draw. FastTESR's final immediate `render` uses a standalone builder and draw,
but not every fast renderer subclasses FastTESR, and immediate submission loses full-batch
translucent sorting and its position after ordinary TESRs. Therefore do not force
`hasFastRenderer=false`, call an inherited no-op renderer, or move the batch draw earlier.
Use the following bounded deferred route for every admitted Forge fast renderer.

1. On authenticated shader-active `preDrawBatch()`, attach batch serial, frame, ID generation,
   world/resource epoch and Forge pass to the dispatcher-owned builder; keep that publication
   borrowed until draw cleanup. Shaders-off uses Forge unchanged and allocates no sidecar.
2. Around each actual `renderTileEntityFast` invocation retain its original arguments and
   supplied builder. P9's dispatch token is valid during population, but is not stored for
   later replay. The P10 append adapter records the current call's state ordinal per complete
   quad, including bulk append; nested appends take their own owner. Restore the parent owner
   in finally. Do not assign one post-call interval over nested contributions. Existing
   geometry bytes, positions, translation, format and lighting remain untouched.
3. Bound owner storage to checked quad count under the existing builder/source byte cap,
   with at most one fixed-width ordinal per quad plus fixed batch identity. Reserve capacity
   before publishing appended ownership; growth may occur only with builder growth. Require
   complete quads, one owner per quad and complete coverage. Do not infer ownership from
   vertex contents, shader ID, coordinate or current uniform after the call returns.
4. At the original `drawBatch(pass)` point preserve atlas binding, lighting, blending,
   culling, AO shade choice, translation and Forge cleanup. For pass>0 perform the original
   full-batch sort once. At `BufferBuilder.func_181674_a(FFF)V`, observe the actual
   destination→source quad permutation after sorting indices and before record movement;
   apply that same bijection to the owner sidecar. Do not sort each TE separately, recompute
   distances, compare coordinates to rediscover owners or change equal-distance ordering.
   Pass0 keeps insertion order. Save/restore/reset carry or invalidate the sidecar alongside
   the exact builder state; an unobserved permutation or mutation fails admission.
5. Seal the batch once. Scan sorted owners into maximal contiguous equal-stateOrdinal ranges;
   never group nonadjacent ranges just because IDs match. Before any native draw, validate all
   ranges, identity, bounds, layout and primitive plans. At most quadCount ranges; no
   unbounded renderer list or copied TileEntity object graph. Resolve each ordinal only
   through the borrowed matched P9 runtime; world state is not re-sampled at flush.
6. P7 opens fresh authenticated main block admission at this original batch point, or validates
   the still-current shadow execution without main activation. For each range P9 enters
   `enterBlockEntity(admission,stateOrdinal)`; only Entered permits submission. P10 binds
   the sealed client source and range through its existing native adapter. P6 immediately
   uploads that range's full-int ID. Submit all adjacent countInstances copies while the
   ID token remains live, then restore ID in finally. The order is A0…A(N−1), B0…B(N−1),
   including repeated nonadjacent A ranges; never repeat renderer/population or the whole
   batch. Pointer/source setup and Forge preDraw/postDraw/reset retain their once-only
   lifetime around all ranges; each native copy is the existing prepared-submission unit.
7. P10's existing QUADS→TRIANGLES preparation, when demanded by effective geometry input,
   applies after full sorting, preserves whole vertex records and each range's quad
   ownership, and uses its established diagonal. Splitting inherently restarts
   gl_PrimitiveIDIn at each range/copy; disclose this shader-visible boundary rather than
   inventing a primitive-ID offset or claiming unsplit primitive-ID parity. No shaders
   or `mc_Entity` constants are patched to replace `blockEntityId`.
8. Finally restore last ID/instance/pointer state, perform Forge batch cleanup/reset once,
   invalidate sidecar and release the borrow. Preparation/owner/permutation failure before
   drawing aborts the batch through P7 main or P8 shadow containment; partial native failure
   stops remaining ranges/copies with no original-batch replay or duplicate geometry.
   Retain source storage until pointers no longer reference it. Failed restoration closes
   shader admission. A missing required hook disables the shader path before frame admission,
   not a nominally supported frame with missing fast geometry or neutral batch IDs.

Direct/damaged nonbatched calls continue invoking the original renderer under the lower
dispatch scope, so FastTESR's immediate draw is already enclosed. Shader-active external
batch callers must have current authenticated main/shadow admission and the same complete
batch protocol; untracked batches cannot bypass preflight. No new worker GL, Forge traversal,
event, draw-pass override, or replacement TESR implementation is introduced.

### 4.13 Real `entityColor` delivery — Phase 7 color-only producer

The following capture semantics are specified here for the governing Phase 9 assignment and
implemented once by Phase 7 at v0.1, independently of alias-runtime installation. The governing
RC3 Phase 6 assignment requires the full inventory at v0.1 and does not defer color as it does
alias IDs; the Phase 9 assignment repeats value-delivery responsibility, not permission to regress
the earlier milestone. Pintonium has no 1.12 producer. Observe vanilla's input, never query TexEnv:

| ID / class | SRG target and injection | Ordered action | Health / fallback |
|---|---|---|---|
| H9-COLOR-01 `RenderLivingBase` (Phase 7 owner) | within `func_177092_a(EntityLivingBase,F,Z)Z`, intercept the invocation of `GlStateManager.func_187448_b(IILjava/nio/FloatBuffer;)V` whose parameter name is `GL_TEXTURE_ENV_COLOR` | copy four floats at the buffer position into Phase 6 `updateEntityColor`; retain no buffer, reconstruct no formula and preserve the original call | `OBSERVER`; exactly once on true and zero on false |
| H9-COLOR-02 `RenderLivingBase` (Phase 7 owner) | `func_177091_f()V` RETURN | pop to preceding scoped color, or neutral outside all scopes | `OBSERVER`; enclosing finally/frame reset drains on throw |

The adapter duplicates the buffer and reads four absolute values from its current position; it never
changes the original position/limit or retains the buffer. This captures the input to TexEnv, not
post-hoc fixed-function state. Values are finite-checked; a missing, extra, short, or wrong-parameter
observation disables only color delivery for that frame and sends neutral. The original vanilla call
always executes, preserving shaders-off behavior. Phase 7's color-only primitive stack restores
the prior scoped color rather than blindly zeroing an outer scope; Phase 6 uploads immediately
when active and at activation otherwise. Phase 9 aliases borrow this established surrounding
color scope and never gate it on ID publication. Neutral before producer installation is not
v0.1 feature completion. The frozen catalog strings H9-COLOR-01/02 retain historical identity
despite ownerPhase=7/v0.1; they do not denote a second P9 writer or deferred owner. No duplicate
hooks are installed.

### 4.14 Reload, invalidation, and diagnostics

Rebuild triggers are:

- pack selection/options producing a new Phase 3 configuration or fingerprint; resource reload
  refreshes registry/mod/tag inputs and resource epochs without rediscovery/reparse when Phase 12
  requests `NONE + resourceReacquire`;
- any changed per-mod source fingerprint;
- `FMLModIdMappingEvent`, whose documentation explicitly directs mods to update ID-dependent caches
  (`reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/fml/common/event/FMLModIdMappingEvent.java:35`–`:40`);
- a changed registry/tag/compatibility-catalog fingerprint;
- hand-light user policy change; and
- GL/world teardown only insofar as it closes the pipeline publication and per-draw state.

Main-thread/resource callbacks enqueue immutable reasons; they never publish. Repeated reasons
coalesce. Phase 7 builds all candidate inputs, publishes at a render-thread no-open-frame boundary,
then issues layer/chunk invalidation. Registry generation and runtime generation are independent and
both appear in the fingerprint.

Diagnostics use `schmaloogium.config` for resolution/source issues and `schmaloogium.frame` for scope
protocol failures. Warn-once keys are stable tuples of `(configuration fingerprint, registry
fingerprint, mapping kind, origin, line, selector, reason)`. A new generation with unchanged
fingerprints does not repeat warnings; a genuinely changed input may.

---

## 5. Cross-phase interfaces

### 5.1 Interfaces exposed by Phase 9

| Exposed contract | Exact content | Consumer(s) |
|---|---|---|
| `IdRuntimeBuilder` / `IdBuildRequest` / `IdBuildResult` | immutable, no-publication resolution entry point; closed Built/Failed ownership | Phase 7 pipeline/reload composition; headless tests |
| `IdRegistrySnapshot` and component records | schema-versioned loader-neutral registry/state/tag projection with dense ordinals and complete validation | `mod.glue.id`; builder/tests |
| `ModIdSourceSnapshot` | bounded, origin-preserving active-mod source bytes/fingerprints in deterministic mod order | `mod.glue.id`; builder |
| `IdRuntimeCandidate` / `IdRuntimeView` | sole prepublication owner and operation-free inspection | Phase 7 composition |
| `IdRuntimePublisher` / `PublishedIdRuntime` | render-thread safe-boundary publication, generation, borrow/retire/close rules | Phase 7 frame/reload owner |
| `AliasLookup` / `AliasValue` / `BlockStampResult` | O(1) full-int block/item/entity alias queries and exact two-word `mc_Entity` result; generation-stamped | Phase 10; Phase 9 per-draw bridge |
| `RenderLayerLookup` / `ResolvedRenderLayer` | optional custom layer per state ordinal after opaque-solid exclusion | Phase 7 terrain dispatch; Phase 10 rebuild invalidation |
| `PerDrawDynamics` / `IdScopeAdmission` / `IdScopeResult` / tokens | exact §4.12 main-or-authenticated-shadow entry/leave, nested restoration, neutral failure/drain; held/entity/TE state uses Phase 6 sink; color is the established Phase 7 writer | Phase 7/P8 hook glue; Phase 6 sink |
| `HandLightPolicy` / `HeldHandsValue` / `DynamicHandLightInterop` | typed user/pack policy, static values, optional external-provider suppression only | Phase 7 hand scope; Phase 12 settings; compat glue |
| alias/tag catalog versions and diagnostic summary | deterministic provenance and capability evidence without live MC objects | Phase 2 manifests/conformance; diagnostics |

The Phase 10 contract is exact: it borrows one `AliasLookup`, records `generation()`, translates its
Minecraft state to the matching ordinal through glue, calls `mcEntity`, stamps only the two returned
integers, and rejects/discards work if the generation changes. It never re-resolves names,
properties, fallback, or precedence.

### 5.2 Dependency contracts consumed

#### Phase 3

| Phase 3 §5 contract | Use |
|---|---|
| `PackConfiguration` current schema/fingerprint discipline | accept only `schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION` (23 under D-P9-22); reject every other version before derivation, never infer an upgrade |
| `IdMappingInput`, `IdMappingFileInput`, `IdMappingParser.parse(IdMappingParseRequest)` | current nested schema equals configuration schema; per-kind ABSENT/PRESENT_EMPTY/PRESENT_RULES, ordered ordinary/forced11300 rules, ENTRY/TAG and CLASSIC/MODERN provenance; use exact parserEnvironment for bounded mod bytes |
| `MetadataConstraint`, `IntegerRange`, `PropertyPredicate`, `PropertyValueConstraint.Literal/IntegerInterval` | D-P9-23 finite-domain OR/AND membership, whole-selector no-overmatch and exact identity; no parser or unbounded expansion |
| `ShaderPropertiesModel.engineFlags` | raw `oldHandLight` / `dynamicHandLight` requested states |
| `EngineOptionData` current codec | decoded oldHandLight DEFAULT/TRUE/FALSE, explicit user priority; use exactly §4.11 runtime policy, not macro presence as a resolved Boolean |
| `DiagnosticReporter` and attributed origins | warnings without exceptions or lost source location |

Phase 3 §§2.2/4.9/5.1/5.3 are the exact-current schema owner contracts.
Catalog-bound materialization and lossless declarations remain required alongside IR-24's amended codec.
Only selected configuration `idMappings()` is consumed. No selected-pack reopening, macro
reconstruction, flattened old list, fabricated alternate list or absence default is allowed.

**Schema23 receiver receipt — D-P9-22, 2026-09-08 (unverified).** Receive P3's typed
selector-range amendment: containing configuration, nested IdMappingInput and any received
inspection snapshot must each equal CURRENT_SCHEMA_VERSION=23 and match configuration
identity before mod parsing, selection, derivation, reuse or reporting. Adopt exactly
§4.7's BLOCK/ENTITY alternate semantics, not a synthetic MODERN fixture or an inferred
schema upgrade. Carry MaterializedSource-v23 and configuration identity opaquely; retain
same-load assets, all option/native contracts, projectionVersion=1 and all nine source-free
trees. No source/binary acquisition authority or new evidence getters arise. Numeric
schema22/21/20/19 receipts and D-P9-16…19 are historical only; their unrelated asset,
codec, identity and lifetime requirements continue under this exact-current admission.

**Schema21 receiver receipt — D-P9-18, 2026-09-08 (unverified).** Adopt P3
§0.63/D-P3-70/§5.3: require `IdMappingInput.schemaVersion == configuration.schemaVersion`
and each equal `PackFrontEnd.CURRENT_SCHEMA_VERSION` (21 at this adoption) before parsing
mod bytes, selecting ordinary/forced11300 branches, building aliases, retaining or reusing
derived state. Any received inspection snapshot must match the same current schema and
configuration identity before reporting. Reject old, future and mismatched values, never
relabel data, erase a profile field or synthesize empty/default components. The dated
schema20/19 receipts below are history only. Carry current metadata-derived configuration
identity unchanged; `MaterializedSource-v21` is an upstream identity cutover, not a P9 source
parser. P4's `RegistryFingerprint/own-build-v1` is opaque identity in normal composition,
not this phase's live ID-registry fingerprint and not a new resolution-evidence requirement.
D-P3-69 same-load assets and nine source-free trees remain unchanged, without binary access.
ID grammar, ordering, precedence, provenance, hand-light policy, actual provider semantics and
matched lookup/ordinal lifetime remain unchanged. Fresh owner/receiver review is required.

**Schema20 receiver receipt — D-P9-17, 2026-09-08 (unverified).** Adopt P3
§0.62/D-P3-69/§5, superseding earlier current-version assertions; the schema19 receipt below
is historical only. Require `IdMappingInput.schemaVersion == configuration.schemaVersion == 20`
before mod-byte parsing, branch selection, alias derivation or reuse. Required non-null
`assets` after `sources` remains the exact same-load P3 capability with the exact containing
`PackIdentity`; reject foreign-load pairing even when structurally equal. Carry the owner's
metadata-derived configuration fingerprint unchanged; no dummy field, empty-manifest upgrade,
reconstructed capability or resource-epoch relabeling. P9 acquires/decodes no binary assets.
ID grammar, ordered ordinary/forced11300 rules, precedence, provenance and hand-light semantics
remain unchanged. Source-free inspection preserves P3's actual ninth canonical assets metadata
section, digest strings via TextHash, original eight meanings and projectionVersion=1, never
bytes/cursors/providers. P13 owns optional owned-sidecar-only recovery; other P3 safety/bounds/
index/container/source/configuration errors remain fatal. Historical authority/reviews stay
intact; fresh owner/receiver reviews and IR-01 remain, with no current PASS claim.

**Schema19 receiver receipt — 2026-09-07:** adopt P3 D-P3-68/§0.61 and §11 migration.
`IdMappingInput.schemaVersion` must equal both its containing configuration and 19 before
mod-byte parsing, branch selection or alias derivation; a current container never repairs an
old/mismatched nested input. Mapping grammar/ordering/era/provenance/forced11300 behavior
and runtime hand-light policy remain unchanged. No native source parser or translator token
is introduced. Historical schema18 receipts remain evidence; fresh reviews and IR-01 remain.

#### Phase 6

| Phase 6 §5 contract | Use |
|---|---|
| `UniformRuntime.events()` / `UniformEventSink` | only upload path for held/entity/TE/color values |
| `HeldItemSample` | world/tick-authenticated four-int held tuple |
| `updateEntityId`, `updateBlockEntityId`, `updateEntityColor`, `updateHeldItems` | immediate-if-active replacement plus activation refresh |
| reset/neutral rules | zero outside scope and on missing/later producers |

Phase 6 §§4.2/5.1 publish these sink/value contracts; P7 is the sole color writer,
while P9 writes held/entity/TE values. Immediate-active upload and restoration stay P6-owned.

#### Phase 7

| Phase 7 §5 contract | Use |
|---|---|
| `FrameToken` / `ScopeToken` and closed frame/scope outcomes | authenticate Phase-9 scope operations and reset on abort |
| `FrameBeginSignal` | world/tick/frame identity for held sampling and scope generation |
| `UniformSignalBridge` and current uniform runtime composition | route existing frame/color signals without sampling from Phase 9 |
| H-ENTITY-02/03 downstream hand-off | exact rows Phase 9 augments |
| `ShaderReloadController` / pipeline transaction | current publish-after-textures, failed-rebuild-to-off and matched worker-drain protocol in §5.3 |
| `HookApplicationReport` | report Phase-9 row counts/classes/fallbacks |

Current Phase 7 §§4.10/5.1/5.5 publish the hook/integration handoff; §§4.4/5.1
authenticate shadow admission. Historical line anchors do not preserve old ungranted APIs.

### 5.3 Publication and integration protocol

Phase 7's current §§4.1/5.3 ten-step transaction is canonical:

1. Close admission and drain frame, bindings, shadow and Phase 10 workers/queued uploads;
   freeze matching configuration/world/resource/hook identities and ordinal projections.
2. Load/re-publish the current Phase 3 configuration only when its reload algebra requires it;
   `NONE + resourceReacquire` preserves that configuration while rebuilding resource/ID inputs.
3. Snapshot registries/tags/mod sources/user policy and build a pure Phase 9 candidate alongside
   the coordinated Phase 4/5/6 preparation. Validate all identities before publication.
4. At the safe boundary follow Phase 7: Phase 4 acceptance, actual generation adoption by the
   new Phase 6 runtime, Phase 5 acceptance, then Phase 13 texture build/registration.
5. Publish Phase 9 **after** the texture stage, install its matched lookup/ordinal bridge and
   complete Phase 10 `IdDependentGeometryInvalidator` before atomic Active and frame admission.
   Before the texture milestone the explicit empty slot preserves this ordering.

Every failed coordinated rebuild converges off, including failure before any acceptance. A pure
builder's no-mutation guarantee does not retain the old renderable pipeline. Rejected candidates
remain caller-owned; accepted publications are retired by their publishers, never caller-closed.
After partial acceptance Phase 7 resets/deactivates IDs, completes required geometry invalidation
and executes its compensating-off path. Shaders-off resets scopes and retires lookup/map only
after the final frame and worker borrows drain. No failure pairs a new registry/atlas with old IDs.

### 5.4 Dependency adoption ledger — unverified

| ID | Owner | Current disposition | Remaining gate |
|---|---|---|---|
| R9-1 | Phase 3 | Current schema23 typed selector grant received by D-P9-22/§5.2; real BLOCK/ENTITY alternate provenance retains §4.7 selection | fresh Phase 3/9 verification; P3 owns selector/tag grammar, P9 registry resolution |
| R9-2 | Phase 7 | existing publication/held/reset/color handoff retained; C2/C3 retarget and deferred-batch amendments specified in §5.6 are receiver-adopted in current P7/P10 (`PHASE_7_DOC.md:1748`, `:1756`–`:1758`; P10 D-P10-26) | fresh Phase 7/9 verification of the adopted rows; receiver adoption is present, so no integration step remains |
| R10-3 | Phase 10 → Phase 9 | adopted §§4.1/5.3: failure-to-off and matched lookup/ordinal lifetime until worker drain | fresh Phase 9/10 verification; no new publisher or generation equality |

### 5.5 Downstream hand-offs

| Consumer | Required hand-off |
|---|---|
| Phase 7 | consume `PublishedIdRuntime`, layer lookup, held/reset participant, and hook ledger exactly as §§4.9/4.11–4.13 specify |
| Phase 10 | consume only `AliasLookup.mcEntity` plus generation for stamps; own native ranges/owner sidecars in §5.6 without interpreting aliases or writing P9 uniforms |
| Phase 12 | supply the higher-priority user `oldHandLight` value; do not reinterpret pack tri-state |
| Phase 2/integration review | record catalog/schema/generation fingerprints and exercise precedence, hand, entity, TE, and color scenes without committing pack images |

### 5.6 C2/C3 exact coordinated receiver amendments

These changes are specified here and already receiver-adopted in the receivers' current
documents: P7's H-ENTITY-03 lower-dispatch row (`PHASE_7_DOC.md:1748`) and owner7 CORE
`H7-TE-BATCH-*` rows (`PHASE_7_DOC.md:1756`–`:1758`), and P10's native-range receiver under
D-P10-26. They are not permissions to invoke absent APIs and no integration step remains;
fresh owner/receiver verification of the adopted rows stays mandatory.

- **P7 H-ENTITY-03 (receiver-adopted, `PHASE_7_DOC.md:1748`):** the row already carries one
  AROUND/finally on
  `TileEntityRendererDispatcher.func_192854_a(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V`
  with "no duplicate convenience-overload hook", matching the semantics specified here.
  Verification confirms its main `gbuffers_block` or authenticated shadow admission outside
  H9-BLOCK-ENTITY-ID-01, one method target and one enclosing finally per row, and runtime
  invocations — including nested and damaged calls — each balancing, not a fixed per-frame count.
- **P7 batch rows (receiver-adopted, `PHASE_7_DOC.md:1756`–`:1758`):** owner7 CORE
  `H7-TE-BATCH-BEGIN` on `preDrawBatch()V`, `H7-TE-BATCH-FAST` around the dispatcher's single
  `renderTileEntityFast` invocation, and `H7-TE-BATCH-DRAW` around `drawBatch(I)V` already
  exist, each one static application, with the semantics specified here: BEGIN borrows exact
  current frame/runtime; FAST conveys the current P9 state ordinal to native owner attribution;
  DRAW opens fresh admission at the original batch point and performs §4.12.1 range scopes,
  adjacency and finally/drain. No stored ID token survives enqueue. Installed v0.3 support
  requires all three plus the corrected dispatch rows healthy.
- **P10 native receiver:** extend its existing private client uploader preparation with a
  batch-only sealed range sequence `(firstQuad,quadCount,stateOrdinal)` and one authenticated
  batch identity. State ordinal is opaque to P10; P7/P9 resolve and upload it before each
  native range. Required owner10 CORE `H10-TE-BATCH-OWNER` append/bulk/save/restore/reset
  subrows attach ownership; `H10-TE-BATCH-SORT` observes one permutation point in
  `BufferBuilder.func_181674_a(FFF)V`; `H10-TE-BATCH-RANGES` replaces only the final native
  call in `WorldVertexBufferUploader.func_181679_a(BufferBuilder)V` for that authenticated
  batch, after Forge preDraw and before postDraw/reset. Existing ordinary draw paths stay
  unchanged. Enumerate each concrete append/state subtarget with expected=1 in the owner10
  catalog; do not hide partial instrumentation behind one aggregate successful count.
- **Native range semantics:** validate the entire partition, checked first/count/byte range,
  source borrow, topology and current binding before submission. Reuse existing client
  facade/native draw facilities; no new public engine method, GL allocator, native handle
  or P1 grant is assumed. Retain sealed original/converted source through all copies and
  restoration. P7's private prepared-submission guard remains sole count/instance owner.
  P9 ID scope outlives that guard for each range. A range is one adjacent-repeat unit,
  never a request to replay the Java uploader. Restore parent ID and instance on nested
  submissions; all preparation and Forge cleanup happen once for the batch.
- **Health/evidence:** the catalog/fingerprint/report and v0.3 installation gate must include
  every row above. Missing permutation/append/native coverage is a pre-frame shader-path
  rejection, not an accepted fast renderer whose geometry is skipped. No receipt here
  claims current hook application, tests, native behavior or sibling verification.

For avoidance of an opaque append aggregate, the required `H10-TE-BATCH-OWNER` subtargets
are `BufferBuilder.endVertex()V`, `addVertexData([I)V`, Forge `putBulkData(ByteBuffer)V`,
`getVertexState()BufferBuilder.State`, `setVertexState(BufferBuilder.State)V`,
`begin(int,VertexFormat)V` and `reset()V`, each one
method wrapper and finally where state is borrowed. EndVertex attributes the completed vertex
to the current owner; commit a quad only after all four vertices have that owner. Bulk append
attributes every inserted complete quad to its current owner. A nested call may append only
at a complete-quad boundary; partial/mixed-owner quads are invalid rather than assigned to
the enclosing TE. Save/restore copies ownership with the same source incarnation; begin/reset
clear it. Byte-only color/light/position changes keep owners; count/order-changing mutations
must flow through these append/state/sort paths, never silently retain obsolete attribution.
Forge bulk append carries the same checked current-owner attribution as array append;
it is a seventh independently audited state/append subrow, not an uninstrumented shortcut.
P10 D-P10-26 and P7 D-P7-54 now receive these native/ID lifetime obligations; fresh
verification remains required and no runtime coverage is claimed.

---

## 6. Failure modes & degradation

| Failure | Detection and contained response | G2.4 rung |
|---|---|---:|
| one malformed/unknown rule, property, alias target, tag, or mod file | warn once; omit only that selector/source; sibling rules continue | 2a |
| duplicate mapping | retain higher-precedence/first assignment and record attributed loser once | 2a |
| BLOCK/ENTITY alternate empty/invalid with PRESENT_EMPTY ordinary state | source contributes no rules; attributed diagnostics retained; no legacy block fallback for a present file and no modern-completion claim | 2a |
| missing pack `block.properties` | deliberate live vanilla numeric fallback after explicit mod rules | normal fallback |
| registry snapshot inconsistent or unavailable | do not publish candidate; if current ordinals are stale, recover shaders off rather than use them | 4/5 |
| candidate build failure during ordinary reload | local publisher unchanged, but Phase 7 coordinated rebuild takes the whole composition off; report failure | 5 |
| Phase 9 publish rejects after partial pipeline publication | Phase 7 recovered-off transaction; close candidates in reverse order | 5 |
| stale alias/frame/build generation | mutation-free rejection; abort affected admitted TE/batch submission through P7/P8 rather than drawing stale/neutral as completion; stale mesh discarded and rebuilt | 2a/4 |
| entity/TE/color stack overflow, wrong token, underflow, or throwable leak | reset all three cells to neutral, disable affected producer for frame, Phase 7 drains/aborts safely | 2a |
| Phase 6 per-uniform upload failure | Phase 6 disables that uniform only; Phase 9 keeps balanced logical scopes | 2 |
| held-item/provider exception | send one zero tuple for frame; retry after next valid sample/change | 2a |
| optional dynamic-light compat failure | no-op suppression, warn once; never affect static held uniforms or program | 2a |
| H9 row application count mismatch | entity/color feature disabled when safely isolatable; shaders off if scope balance cannot be guaranteed | 2a/4 |
| fast-batch ownership/permutation/range/preflight failure | before draw abort via P7/P8; stop all remaining ranges after partial native failure, no original-batch replay; reset IDs/instances/builder and release borrows after pointer restoration | 4 |
| missing required TE dispatch/fast-batch hook coverage | reject shader-frame admission before execution; shaders-off retains original complete Forge geometry, not omitted fast renderers | 4 |
| shader program compile/link failure | no Phase 9 action; Phase 4 backup chain owns it | 3 |

Unknown names are expected compatibility misses, not exceptions. No failure leaves a nonzero ID or
color outside its authenticated scope, and no stale ordinal indexes a new registry snapshot.

---

## 7. Threading & performance notes

- Forge registry and Minecraft-object projection occurs on the client/main thread after freeze or
  at an authenticated remap boundary. It copies all values before off-thread resolution.
- Jar/directory reads, Phase 3 parsing of mod text, deterministic table construction, tag expansion,
  and fingerprinting may run off-thread from immutable inputs. Diagnostics are buffered and
  published in deterministic source order.
- Candidate publication, held sampling, Phase 6 event calls, entity/TE/color scope operations, and
  retirement are render-thread-only. Resource/remap callbacks enqueue reasons only.
- Hot block/item/entity queries are bounds checks plus primitive-array reads. Glue identity maps are
  built once per registry snapshot. No `ResourceLocation`, property-map traversal, string parsing,
  registry lookup, logging, stream, `Optional`, or heap scope object is permitted per draw/vertex.
- Resolution is `O(rules × matched candidates)` at reload. Name indexes and per-block property
  indexes bound it to affected block states; target registries are small enough that clarity wins
  over mutable caches.
- Scope stacks are primitive arrays with ordinary depth preallocated. Growth occurs only before a
  value is published; a hard cap prevents hostile recursive renderers from unbounded allocation.
- Published arrays are final and safely published through one atomic reference. Readers never need
  locks. Retirement uses Phase 7's frame/build borrow accounting, not reference counting per lookup.
- Diagnostics and provenance objects live off the hot arrays. Warn-once lookup occurs at build or
  first protocol failure, never for every missing draw.

---

## 8. Testability plan

### 8.1 Pure `:engine` tests

Use a scripted `IdRegistrySnapshot`, Phase 3 parser fixture, and recording diagnostic reporter:

1. `allDocumentedForms_shortNamespacedMetadataPropertyLegacy` covers every §3.7 example and
   combined metadata/property ranges.
2. `precedence_packThenEachMod_entriesBeforeTags_thenFallback_firstWriter` proves every §4.4 tier,
   two mod IDs, source order, per-source explicit-over-tag behavior, and attributed conflicts.
3. `modOrder_unicodeModIdStableAcrossEnumerationOrder` permutes loader input and gets identical
   tables/fingerprint.
4. `modernAliasCatalog_liveTargetsAndFluidPairs` covers every seed, missing targets, and symmetric
   still/flowing assignments.
5. `ambiguousGrassAndLamp_requireModernEraProvenance` uses real P3 pack-load and bounded-mod
   parser bytes with `#if MC_VERSION >= 11300` around grass/lamp rules: ordinary state is
   PRESENT_EMPTY, alternate MODERN, selected aliases reach tallgrass/lit_redstone_lamp.
   An ordinary nonempty classic control retains grass/unlit lamp; no manual era injection.
6. `blockAndEntity11300Retry_presentEmptyOnly` covers absent, ordinary nonempty (including
   zero registry matches), empty→alternate, invalid alternate and no-merge behavior for
   independent pack/mod contributions; source/selection fingerprints differ as specified.
7. `legacyFallback_absentOnly_modBeforeFallback_vanillaOnly` distinguishes absent from present
   empty and registry remap.
8. `tagShim_entriesBeatTags_unknownNoMatch_providerFailureIsolated` covers explicit definitions,
   OreDictionary map, deterministic expansion, and missing provider.
9. `layerRules_allFourAndOpaqueSolidExcluded` covers precedence and state-level exclusion.
10. `aliasLookup_fullIntAndMcEntityBitLayout` covers explicit zero, absence, negative/unsigned
    representable patterns, out-of-range result, metadata, render type, and stale ordinal.
11. `candidatePublication_atomicOwnershipAndRetirement` exercises build failure, rejected publish,
    replacement borrows, reverse close, and generation overflow guard.
12. `heldHands_normalOldModeAndPolicyPriority` covers both IDs, actual off light, brighter main,
    user-over-pack priority, publication recompute, and non-block light zero.
13. `perDraw_nestedEntityTeColorScopesRestoreLifo` covers nested RenderManager entry points, TE
    nesting, color nesting, wrong tokens, throw drain, and neutral reset.
14. `unknownName_warnOnceFingerprintScoped` proves no log spam and changed-input re-reporting.

Property-based tests permute registry/rule/mod order where order is declared irrelevant and preserve
order where first-writer semantics are declared. Fuzzed malformed selectors never throw or broaden a
match.

### 8.2 Loader/glue and hook tests

- A temporary directory and synthetic jar verify exact mod resource paths, ISO-8859-1 bytes,
  containment, duplicate entries, symlink escape, decompression/size limits, and deterministic mod
  ordering.
- A scripted Forge snapshot verifies names, state metadata/properties, render ordinals, opaque/full
  cube, light, entity classes, and remap fingerprint changes.
- Mixin application tests assert one H9-ENTITY-ID HEAD/RETURN pair per exact descriptor, ordered
  H9-BLOCK-ENTITY-ID inside H-ENTITY-03, the exact H9-COLOR
  `GlStateManager.glTexEnv(int,int,FloatBuffer)` invocation and parameter guard, H9-COLOR reset, and Phase
  7 frame drain.
- A recording Phase 6 sink asserts update order: Phase 7 program scope open → ID/color update → draw
  → color/ID restore → program scope close, including original throw paths.
- D-6 seam tests reject every Minecraft/Forge/Mixin/LWJGL type from `engine.config.id` public and
  private class signatures.
- Trace normal/global/damaged world calls and both direct overloads into the lower dispatch;
  prove one balanced scope per invocation in main/shadow, nested same/different TE and throw.
- Two differently mapped fast renderers, including one not subclassing FastTESR, append
  interleaved-distance quads. At the native boundary assert sorted ownership A/B/A, equal-distance
  stability, complete unchanged geometry bytes, full-int IDs and A0,A1,B0,B1 ordering at N=2.
  Prove no renderer replay, duplicate batch draw, lost quad, stale ID or repeated pre/postDraw.
  Cover pass0/pass1, nested append, bulk append, direct immediate FastTESR, damaged rendering,
  save/restore, shader-off, malformed ownership, failed preflight and failure after first copy.

### 8.3 Conformance and implementation gate

- Headless Phase 2 fixtures resolve representative classic and dual-spec mapping files against the
  scripted registry and record only hashes/provenance, never pack text or images.
- T1 world scenes include at least: two differently mapped blocks with properties/metas; custom
  layer non-opaque block; main/off-hand ID/light swap; two entity types; a tile entity whose block
  ID differs from entity ID; hurt entity; flashing creeper; and nested armor/eye layer.
- Modern block evidence must originate in actual current-schema pack/mod P3 preprocessing, not
  resolver-only MODERN fixtures. Fast TE evidence records IDs at actual native submissions
  and camera-dependent full-batch ordering, not only uniforms during CPU population.
- P3-origin cases include leaves metadata3/7/11/15, reeds age0..3, and combined metadata0..3
  AND age0..3 in pack/mod ordinary/alternate/layer paths; sparse live integer domains may
  intersect without containing every integer. Unknown literal, disjoint interval and missing
  property invalidate the whole selector, including mixed valid/invalid alternatives.
- Motion variants change hand, entity, and TE across consecutive frames to catch stale per-draw
  values. A throw-injected renderer proves neutral restoration before the next draw.
- The v0.3 gate is the governing one: scripted-registry headless tests pass and in-game
  `heldItemId`/`entityId` values are verified on the Phase 2 scene set
  (`docs/design/v2.0-RC3/DESIGN.md:2119`–`:2121`). Add `blockEntityId`, `entityColor`, layer, and
  off-hand assertions because they are in scope even though the short impl-gate sentence names only
  two uniforms.
- T0/T1 runs use the full matrix acquisition policy; no pack or rendered image is committed.

---

## 9. Milestone staging

| Component | Milestone | Exit evidence |
|---|---:|---|
| Phase 3 R9-1 mapping-input/schema extension | v0.3 prerequisite | Phase 3 fresh literal PASS + parser fixtures |
| registry/mod/tag snapshots and bounded source reader | v0.3 | pure/glue snapshot tests |
| exact forms, precedence, properties/meta, fluid/name aliases | v0.3 | headless resolver suite |
| tag shim and pack-matrix compatibility definitions | v0.3 | entries-before-tags fixtures + T0 mapping manifests |
| current-schema BLOCK/ENTITY 11300 selection and live legacy fallback | v0.3 | actual pack/mod parser→modern grass/lamp fixtures; exact alternate/absence/remap identity |
| immutable candidate/publication/reload | v0.3 | transaction/generation tests + R9-2 grant |
| Phase 10 alias/stamp interface | v0.3 | bit-layout/generation tests; consumed by Phase 10 |
| resolved custom render layers | v0.3 | opaque exclusion + terrain scene |
| held IDs/static lights/old mode/dynamic interop disposition | v0.3 | recording sink + hand scenes |
| lower-dispatch entity/TE scopes and deferred fast ranges | v0.3 | §5.6 receiver integration; main/shadow/damaged/direct/nested throw evidence, native IDs on two fast renderers, full-batch sort and complete geometry |
| exact vanilla-argument `entityColor` | v0.1 Phase 7 producer, reused v0.3 | P7 H-COLOR hook test + hurt/creeper scenes; no ID-runtime prerequisite |
| further modern alias/tag catalog entries | post-v0.5 | named fixture + catalog version/addendum; never heuristic |

Every component is architected here. The final row is data growth, not a deferred rewrite of the
resolver or publication shape.

---

## 10. OQ & spike specifications

Phase 9 owns no open question in §G10 or its Part II assignment. No spike is authorized or needed.
Current producer grants are adopted as unverified contracts in §5.4, not new OQs.
Until fresh owner/consumer verification their implementation remains gated; a failed
coordinated rebuild goes off, never resumes the old pipeline.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision and rationale |
|---|---|
| D-P9-1 | Build and publish whole immutable ID runtimes; never patch maps in place. This directly enforces the assignment's reload-safety requirement and makes generation checks testable. |
| D-P9-2 | Use pack (entries then tags) → each deterministic mod (entries then tags) → fallback with assign-if-absent. Contract check: §3.7 grants pack/mod mappings, the assignment states pack wins without qualification, and RESEARCH §3.6.8 requires entries-before-tags inside the tag-capable source. Pintonium's `putIfAbsent` is adopted only as mechanics. |
| D-P9-3 | Adopt a versioned live-validated modern-name/fluid alias catalog, but require Phase 3 era provenance for tokens that already have a 1.12 meaning. Contract check: classic long names resolve live 1.12 registry names, so a shader-ID heuristic would violate §3.7; the unambiguous table remains a compatible extension. |
| D-P9-4 | Adopt the entity `MC_VERSION=11300` retry only for a present file whose normal result is empty, never merge both parses. Contract check: §3.7 requires entity mappings and preprocessing; the retry changes only conditional selection, not mapping grammar or precedence. |
| D-P9-5 | Design an explicit legacy tag provider and delay tags after entries; reject PD's implication that the 1.12 parser proves tags. RESEARCH §3.6.8 is authoritative and explicitly requires a shim. |
| D-P9-6 | When pack `block.properties` is absent, fill unassigned vanilla states from live numeric IDs after mod rules. This preserves the OF legacy fallback purpose without copying Pintonium's modern curated/magic values. |
| D-P9-7 | Deliver `entityColor` by copying the exact four-float `GL_TEXTURE_ENV_COLOR` buffer vanilla passes into `GlStateManager.glTexEnv`, not by querying FF state afterward or duplicating hurt/creeper formulas. The pack contract requires the multiplier; the mechanism makes the invisible FF value explicit without changing it. |
| D-P9-8 | Static held light is the `ItemBlock` default state's emitted light; non-block items are zero absent an external provider. `dynamicHandLight` only suppresses an explicitly installed external dynamic-lights provider because dynamic lights are a binding non-goal. |
| D-P9-9 | Sort mod contributions by code-point mod ID. The contract defines pack-over-mod but no mod/mod winner; a stable documented order is preferable to jar discovery or hash iteration order. |
| D-P9-10 | Publish dense-ordinal primitive lookups and the exact packed `mc_Entity` result. This keeps `:engine` MC-free, makes stale generations explicit, and prevents name/property work on vertex hot paths. |
| D-P9-11 | Resolve custom layers per block state and refuse every solid opaque cube before publication. Phase 7 consumes the decision; Phase 9 never mutates vanilla layers directly. |
| D-P9-12 | Hook both RenderManager entry methods and make scopes nestable/idempotent for the same entity. MCP/App E show both are valid entry points; assuming only their current internal call relation would be brittle. |
| D-P9-13 | Adopt current P3 schema/input and P7 publish-after-textures/off-on-failure; preserve local pure-builder no mutation and drain matched lookup/ordinal borrows before close (IR-03/04/09). |
| D-P9-14 | Admit ID scopes through authenticated main or shadow capability, restore nested values before release, and never activate main programs in shadow (IR-22). |
| D-P9-15 | Reuse the P7-owned v0.1 color-only producer; P9 defines operand-capture semantics but adds no writer or later-milestone deferral. Preserve exact oldHandLight default/true/false user-over-pack codec and local true fallback (IR-11/24). |
| D-P9-16 | Adopt P3 schema18 load-time old-hand-light projection after option-macro-free Properties parsing; retain typed runtime user→pack→true resolution and identical held-light behavior. No macro-presence adapter, older-schema reuse or runtime mutation of shader macros (IR-03/24). |
| D-P9-17 | 2026-09-08: adopt P3 D-P3-69 schema20 and exact containing/nested ID equality before parsing/derivation, with same-load assets/configuration identity in §5.2. Supersede earlier current-schema assertions only; no mapping/hand-light change, binary authority or historical PASS promotion. |
| D-P9-18 | 2026-09-08: adopt P3 D-P3-70 exact-current schema21 containing/nested-ID equality before mod parsing and all derivation/reuse in §5.2; retain current configuration identity and opaque upstream materialization/P4 registry cutover without new evidence getters. Older receipts are historical; ID/provider/lifetime semantics unchanged; fresh review required. |
| D-P9-19 | Receive P3 D-P3-72 schema22 BLOCK dual-era grant and apply PRESENT_EMPTY/nonempty-alternate-only selection to each contribution; no fake provenance, ordinary override, heuristic or classic-fallback completion. RESEARCH classic mapping meanings remain unchanged; mandatory modern ambiguous aliases become reachable. |
| D-P9-20 | C2 replaces App E's historical convenience candidate with actual lower TE dispatch, one P7-outer/P9-inner finally pair for normal/global/damaged/direct/shadow/nested paths. No research rewriting or duplicate outer scopes. |
| D-P9-21 | C3 retains Forge deferred batch and exact global sort permutation, then submits contiguous owner-homogeneous ranges with fresh P9 IDs live through native adjacent copies. Immediate FastTESR alone cannot prove all fast renderers or global sort parity; reject blanket disabling. §5.6 requests exact receiving operations, preserving shaders-off geometry and honest range primitive-ID boundaries. |
| D-P9-22 | Receive schema23/current-constant typed selector constraints and MaterializedSource-v23; P3 remains the sole parser, P9 resolves registry membership. Previous numeric receipts are historical; alternate selection and matched publication lifetime remain. |
| D-P9-23 | Receive P3 D-P3-73 exact typed metadata/property alternatives and intervals; resolve only finite live domains with OR alternatives and AND constraints. Preserve order/provenance/identity, no source reparse or range expansion, and no invalid-predicate widening. |

### 11.2 Input contradictions and rulings

1. **PD tag claim vs 1.12 source/RESEARCH.** PD §8.1 groups tag expansion with the working identity
   layer, but Pintonium's vintage parser creates no tag and its modern parser owns `%`/tag lookup.
   RESEARCH says tags do not exist on 1.12 and need a shim. RESEARCH wins; §4.7 designs that shim and
   does not cite Pintonium as 1.12 tag proof.
2. **Pintonium fallback vs “re-derive values.”** Its fallback is a curated modern-name list with
   magic compatibility IDs, not a live 1.12 projection. The Phase assignment says re-derive values;
   §4.8 uses live vanilla numeric registry IDs.
3. **Color assignment overlap.** RC3 Phase 6's full-inventory v0.1 obligation and Phase 9's
   explicit value-delivery assignment are reconciled by §4.13: P7 installs the color-only producer
   at v0.1 using these semantics, P9 reuses it at v0.3. No authority rewrite is implied.
4. **11300 retry versus no-reopen.** Phase 3 now publishes the alternate and presence/era data;
   §5.2 adopts that grant instead of retaining the historical missing-surface claim.

No contradiction with RESEARCH D-1…D-10 was found.

### 11.3 Open items and hand-offs

- R9-1/R9-2 and R10-3 are adopted/unverified as §5.4 records; fresh owner/consumer reviews,
  not another invented API, gate implementation.
- Phase 10 owns stale-result scheduling and matched lookup/ordinal-map worker draining; it
  may not change alias bits or resolve names itself.
- The v0.3 `LegacyTagCatalog` data set must be derived from the target pack matrix and live 1.12
  registries under the explicit provider rules. Unknown tags already have a complete fallback.
- Phase 12 supplies the higher-priority decoded oldHandLight tri-state under the current codec;
  P3's current-schema shader macro projection uses the same rule after its independent Properties
  pass; global `default` remains distinct from explicit false in canonical identity.
  DEFAULT delegates to the pack then §4.11's local true fallback, not an unconditional GUI true.
- An external dynamic-lights adapter is optional and must be separately compatibility-gated. Its
  absence does not reduce shader identity conformance.

### 11.4 Requested upstream changes

- Freshly verify Phase 3's current ID and engine-option schema grant and this receiver's §5.2.
- Freshly verify the coordinated Phase 7/9/10 §5 lifecycle, shadow-admission and color producer
  amendments; no historical PASS is promoted to current integration clearance.
- Freshly verify §5.6's adopted P7 dispatch/batch catalog rows (`PHASE_7_DOC.md:1748`,
  `:1756`–`:1758`) and P10's owner-sidecar/permutation/native-range receiver (D-P10-26) before
  implementation; the rows are already receiver-adopted, so verification — not integration — is
  the outstanding step.
- In the next design candidate, qualify PD §8's “tag expansion … working in production” summary as
  modern-only in this checkout, with the 1.12 shim still required. This document does not modify
  `DESIGN.md` or PD.

---

## 12. Implementation checklist

1. **[v0.3]** Freshly verify adopted Phase 3 R9-1/current schema, parser and engine-option codec.
2. **[v0.3]** Freshly verify adopted Phase 7 R9-2 and Phase 10 R10-3: lifecycle/drain,
   accepted-frame hooks, main/shadow ID ordering, existing P7 color producer and hook report.
3. **[v0.3]** Implement/validate `IdRegistrySnapshot` schema and scripted snapshot tests; add D-6
   forbidden-type checks.
4. **[v0.3]** Implement the bounded jar/directory `ModIdSourceSnapshot` adapter and containment/order
   tests.
5. **[v0.3]** Implement source merge/precedence and conflict diagnostics; pass all pack/mod/tag/
   fallback permutation tests.
6. **[v0.3]** Implement exact name, metadata, property, and legacy numeric resolution with invalid
   predicate no-overmatch tests.
7. **[v0.3]** Implement/version the modern-name and fluid alias catalog; validate live targets and
   classic/modern ambiguous-token fixtures produced by current-schema P3 pack/mod parsing.
8. **[v0.3]** Implement `LegacyTagCatalog` providers and explicit OreDictionary mapping; pass
   entries-before-tags and provider-failure tests.
9. **[v0.3]** Implement BLOCK/ENTITY PRESENT_EMPTY-only 11300 selection and absent-only fallback; prove source/selected-list/remap identity.
10. **[v0.3]** Implement resolved layer tables and opaque-solid exclusion; wire generation-based
    chunk invalidation with Phase 7/10.
11. **[v0.3]** Implement candidate/publisher ownership, atomic safe-boundary integration, retirement,
    recovered-off rollback, and generation tests.
12. **[v0.3]** Implement the allocation-free `AliasLookup`/`mcEntity` arrays and Phase 10 contract
    tests, including representability and stale work.
13. **[v0.3]** Implement held-hand sampling/policy and recording Phase 6 sink tests; keep dynamic
    interop optional and separately gated.
14. **[v0.3]** Implement corrected lower-dispatch P7/P9 wrappers and §5.6 batch/native receiver
    rows, exact sort permutation/owner partition, balanced scopes and throw/drain; prove
    complete fast geometry and truthful IDs at actual native ranges in main/shadow.
15. **[v0.3]** Verify reuse of P7's v0.1 H9-COLOR-01/02 exact-argument capture/restoration
    in alias scenes; preserve the original FF call and install no duplicate color hooks.
16. **[v0.3]** Run the headless scripted-registry suite and licensed Phase 2 T0/T1 scenes; record
    manifests/hashes only and meet the full §8.3 gate.
17. **[post-v0.5]** Extend alias/tag catalog data only through named fixtures, live-target proofs,
    version increments, and a Phase 9 addendum; do not add similarity heuristics.
