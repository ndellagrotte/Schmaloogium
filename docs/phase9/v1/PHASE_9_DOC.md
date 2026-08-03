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

All dependency gates were checked before their binding contracts were consumed:

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
   - Appendix E rows 13–14, lines 1386–1412.
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
- the conditional `MC_VERSION=11300` entity-property retry;
- legacy numeric block fallback when and only when pack `block.properties` is absent;
- immutable publication, generation/fingerprint identity, registry-remap/resource-reload rebuild,
  and stale-consumer rejection;
- the Phase 10 alias lookup that computes the two `mc_Entity` payload words;
- resolved custom render-layer lookup handed to Phase 7;
- main/off-hand alias IDs, static held-block light, `oldHandLight`, and the
  `dynamicHandLight` interoperability disposition;
- balanced per-entity and per-block-entity uniform scopes;
- real `entityColor` delivery from the values vanilla computes before its fixed-function TexEnv
  effect, including neutral restoration;
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

The verified dependency surfaces lack inputs needed by this assignment. R9-1 and R9-2 in §5.4 are
requests, not granted contracts. The 11300 retry, `%` tag parsing, correct absent-vs-present
fallback, and coordinated publication/hook refinement are implementation-gated until their owners
apply and reverify those changes.

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
| standard A–G macros, no option macros | consumed Phase 3 result; forced retry delegates to Phase 3 | `[V:doc]` `docs/research/v1/RESEARCH.md:460`–`:462`; no parser duplication |
| per-mod block/item/entity files | bounded active-mod snapshot and deterministic merge (§4.3) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:537`–`:540`, `:576`–`:595` |
| pack wins over mod | precedence tier 0 before mod tiers (§4.4) | Governing Phase 9 assignment `docs/design/v2.0-RC3/DESIGN.md:2069`–`:2071`; D-P9-2 |
| first-writer-wins and order significance | fill-only table writes within each precedence class (§4.4) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java]` `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java:54`–`:67`; PD §8.1; contract checked by D-P9-2 |
| metadata and property predicates | state filtering before fill-only writes (§4.5) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java]` `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java:50`–`:68`; pack-author syntax above; D-P9-2 |
| explicit entries beat tags | within each pack/mod source, its explicit pass completes before its tag pass (§4.4/§4.7) | `[V:doc]` `docs/research/v1/RESEARCH.md:447`; pack-over-mod remains the outer precedence, Pintonium 1.12 tag proof rejected, D-P9-5 |
| tag expansion | explicit `LegacyTagCatalog`/membership provider, unknown-tag absence (§4.7) | RESEARCH's required shim at `docs/research/v1/RESEARCH.md:447`; not claimed as proven by Pintonium |
| still↔flowing water/lava | symmetric exact candidate expansion (§4.6) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java]` `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java:81`–`:88`; D-P9-3 |
| modern→1.12 alias table and modern `minecraft:grass` case | versioned live-validated catalog and legacy-preserving era evidence (§4.6) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java]` `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/shaderpack/materialmap/VintageBlockMaterialMapping.java:71`–`:119`; PD §8.1; contract check D-P9-3 |
| entity `MC_VERSION=11300` retry when normal present-file result is empty | Phase 3 alternate parse selected by §4.7 | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IdMap.java]` `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IdMap.java:65`–`:73` and `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IdMap.java:132`–`:141`; D-P9-4; R9-1 gated |
| OF-legacy numeric fallback only without pack `block.properties` | explicit mod mappings, then live vanilla numeric fill (§4.8) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IdMap.java]` `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IdMap.java:75`–`:89`; values re-derived, D-P9-6 |
| custom `layer.solid/cutout/cutout_mipped/translucent` | resolved state-layer table (§4.9) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:555`–`:568` |
| solid opaque cubes excluded from custom layer | snapshot predicate rejects layer assignment (§4.9) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:570`–`:574` |
| `heldItemId` / `heldItemId2` | item alias lookup from both hand samples (§4.11) | `[V:doc]` `docs/research/v1/RESEARCH.md:1323`–`:1328`; no Pintonium 1.12 producer inherited |
| `heldBlockLightValue` / `2`, brighter-hand-wins old mode | static held-state light and exact Phase 6 tuple (§4.11) | `[V:doc]` `docs/research/v1/RESEARCH.md:1327`–`:1328`; Phase 6 contract `docs/phase6/v1/PHASE_6_DOC.md:497`–`:505` |
| `dynamicHandLight` | optional external-provider suppression only (§4.11) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:23`–`:25`; dynamic lights non-goal preserved |
| `entityId` | balanced H9-ENTITY-ID scope (§4.12) | `[V:doc]` `docs/research/v1/RESEARCH.md:1373`–`:1375`; App E row 13 `docs/research/v1/RESEARCH.md:1410` |
| `blockEntityId` is current TE's aliased block ID | balanced H9-BLOCK-ENTITY-ID scope (§4.12) | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:178`–`:180`; App E row 14 `docs/research/v1/RESEARCH.md:1411` |
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

A frame borrows one publication generation. Entity/TE/color scopes and held events must echo that
frame and generation. Phase 10 chunk-build work records the lookup generation in its build context;
publication replacement makes work/results with another generation stale. Retired tables remain
alive until already-issued frame/build borrows end, but no new borrow can target them.

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
entries, NUL/absolute/`..` paths, and non-regular files are rejected for that mod. Bytes are decoded
as ISO-8859-1 and passed to Phase 3's public `IdMappingParser`; Phase 9 never implements Java
Properties or preprocessor syntax.

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

For blocks:

- no metadata or properties selects all valid states of the resolved block;
- metadata values select states whose captured `legacyMetadata` is in the rule's set;
- each property name must exist on the block, and all predicates are ANDed;
- comma alternatives within one property are ORed; an integer `a-b` is inclusive and ascending;
- metadata and property predicates together are ANDed, matching the pack-author cross-version form;
- an unknown property/value, descending/overflowing range, or selector that matches no state emits
  one attributed warning and assigns nothing. Dropping an invalid predicate and overmatching is
  forbidden.

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
The provenance may come from a surviving `MC_VERSION>=11300` branch or an explicit dual-era parse;
Phase 9 does not guess from substitute IDs, filenames, pack names, or neighboring tokens. R9-1 asks
Phase 3 to publish that provenance. Until granted, ambiguous tokens keep their 1.12 meaning and warn
once that the modern special case was conservatively unavailable.

This preserves classic semantics while adopting the table mechanism. Extending the catalog requires
a named pack fixture, a live-target proof, a catalog-version increment, and a D-P9 addendum; runtime
name similarity never manufactures an alias.

### 4.7 Tag shim and entity-era retry

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

#### Entity `MC_VERSION=11300` retry

For each pack or mod entity file independently:

1. if file state is `ABSENT`, contribute no entity rules and do not retry;
2. if the ordinary parsed rule list is non-empty, use it and discard the alternate;
3. if file state is `PRESENT` and the ordinary list is empty, use Phase 3's alternate parse produced
   from identical bytes/macros except that exactly `MC_VERSION` is replaced with `11300`;
4. if the alternate is also empty or invalid, contribute an empty list with attributed diagnostics.

The retry never merges both parses and never changes global `RuntimeIdentityData`. It is an
ID-file-local era bridge only. A source fingerprint includes ordinary/alternate results and which
branch was selected. Phase 3 must perform the preprocessing and parsing; R9-1 requests the presence
bit, alternate result, and era provenance because the current §5 surface publishes none of them.

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

`HandLightPolicy` resolves the raw Phase 3 tri-states and future Phase 12 user values:

- an explicit user value wins over the pack value where the pack-author docs grant priority;
- otherwise explicit pack `TRUE/FALSE` wins;
- `oldHandLight=DEFAULT` resolves `true`, the backward-compatible default;
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
| H9-BLOCK-ENTITY-ID-01 `TileEntityRendererDispatcher` | `func_147549_a(TileEntity,D,D,D,F)V`, ordered inside H-ENTITY-03 | after Phase 7 opens `gbuffers_block`, obtain the TE world/position state ordinal, push prior `blockEntityId`, publish mapped-or-0; restore before Phase 7 closes program scope | `FEATURE`; App E row 14; unknown/detached TE uses 0 |
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

### 4.13 Real `entityColor` delivery

Pintonium has no 1.12 producer, and reading TexEnv after vanilla changes it cannot work while a GLSL
program is bound. Phase 9 observes the input to the effect instead:

| ID / class | SRG target and injection | Ordered action | Health / fallback |
|---|---|---|---|
| H9-COLOR-01 `RenderLivingBase` | within `func_177092_a(EntityLivingBase,F,Z)Z`, intercept the invocation of `GlStateManager.func_187448_b(IILjava/nio/FloatBuffer;)V` whose parameter name is `GL_TEXTURE_ENV_COLOR` | copy the four floats at the supplied buffer's current position and forward them to `updateEntityColor`; do not retain/mutate the buffer, reconstruct hurt/creeper formulas, or suppress the original call | `OBSERVER`; expected exactly once when the method returns true and zero times when false |
| H9-COLOR-02 `RenderLivingBase` | `func_177091_f()V` RETURN | restore neutral `(0,0,0,0)` after vanilla unsets the effect | `OBSERVER`; frame reset is the throw fallback |

The adapter duplicates the buffer and reads four absolute values from its current position; it never
changes the original position/limit or retains the buffer. This captures the input to TexEnv, not
post-hoc fixed-function state. Values are finite-checked; a missing, extra, short, or wrong-parameter
observation disables only color delivery for that frame and sends neutral. The original vanilla call
always executes, preserving shaders-off behavior. Nested render layers use a primitive color stack
so reset restores the prior scoped color rather than blindly zeroing an outer scope. Phase 7's
existing H-COLOR-01 RETURN-only description does not expose the buffer or current scope; R9-2
requests the hook refinement before this feature is implemented.

### 4.14 Reload, invalidation, and diagnostics

Rebuild triggers are:

- pack selection/options/resource reload producing a new Phase 3 configuration or fingerprint;
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
| `PerDrawDynamics` / Phase-9 scope tokens | held, entity, TE, color update/reset state machine authenticated to frame/publication | Phase 7 hook glue; Phase 6 sink |
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
| `PackConfiguration` schema/fingerprint discipline | sole selected-pack identity and cache key |
| `UnresolvedIdMappings`, `IdMappingParser` | existing ordinary unresolved pack rules and parsing of Phase-9-provided mod text |
| `ShaderPropertiesModel.engineFlags` | raw `oldHandLight` / `dynamicHandLight` requested states |
| `DiagnosticReporter` and attributed origins | warnings without exceptions or lost source location |

The current exposure is at `docs/phase3/v1/PHASE_3_DOC.md:1103`–`:1125`; consumers may not reopen
the pack at `docs/phase3/v1/PHASE_3_DOC.md:1183`–`:1186`.

#### Phase 6

| Phase 6 §5 contract | Use |
|---|---|
| `UniformRuntime.events()` / `UniformEventSink` | only upload path for held/entity/TE/color values |
| `HeldItemSample` | world/tick-authenticated four-int held tuple |
| `updateEntityId`, `updateBlockEntityId`, `updateEntityColor`, `updateHeldItems` | immediate-if-active replacement plus activation refresh |
| reset/neutral rules | zero outside scope and on missing/later producers |

Phase 6 explicitly exposes its runtime/events to Phase 9 at
`docs/phase6/v1/PHASE_6_DOC.md:1183`–`:1189` and defines the sink methods at
`docs/phase6/v1/PHASE_6_DOC.md:280`–`:292`.

#### Phase 7

| Phase 7 §5 contract | Use |
|---|---|
| `FrameToken` / `ScopeToken` and closed frame/scope outcomes | authenticate Phase-9 scope operations and reset on abort |
| `FrameBeginSignal` | world/tick/frame identity for held sampling and scope generation |
| `UniformSignalBridge` and current uniform runtime composition | route existing frame/color signals without sampling from Phase 9 |
| H-ENTITY-02/03 downstream hand-off | exact rows Phase 9 augments |
| `ShaderReloadController` / pipeline transaction | build/publish/retire identity runtime with the shader pipeline after R9-2 |
| `HookApplicationReport` | report Phase-9 row counts/classes/fallbacks |

The explicit Phase 9 hand-off is `docs/phase7/v1/PHASE_7_DOC.md:1619`–`:1625`; the hook rows are
specified at `docs/phase7/v1/PHASE_7_DOC.md:927`–`:946` and deferred at
`docs/phase7/v1/PHASE_7_DOC.md:1019`–`:1021`.

### 5.3 Publication and integration protocol

After R9-1/R9-2 are granted, Phase 7 extends its candidate transaction in this order:

1. load one Phase 3 configuration and validate its current schema/fingerprint;
2. snapshot registries, tags, active-mod sources, and user hand-light policy;
3. build the Phase 9 candidate off-thread where safe, retaining no Minecraft object;
4. compose existing Phase 4/5/6 candidates and validate matching configuration/registry identity;
5. at a render-thread no-frame boundary publish Phase 4/5/6 as their contracts require, then publish
   the Phase 9 candidate before the pipeline becomes Active;
6. invalidate layer/chunk products from the former Phase 9 generation;
7. open new frames only after all publications are visible.

Any failure before publication closes the new candidate and retains the old whole pipeline. If
Phase 9 publication alone rejects after earlier components accepted, Phase 7 executes its existing
recovered-off path; it never runs a new pipeline with old ID ordinals. Shaders-off closes Phase 9 in
reverse composition order after all scopes are reset.

### 5.4 Requested dependency changes — flagged, never assumed

| ID | Owner | Requested binding change | Blocked feature |
|---|---|---|---|
| R9-1 | Phase 3 | Replace/extend the ID surface with a schema-versioned `IdMappingInput` that preserves per-kind file state (`ABSENT`, `PRESENT_EMPTY`, `PRESENT_RULES`), ordinary and forced-11300 entity parse results, per-rule `MappingEra`, and `SelectorKind.ENTRY/TAG`; expose the same pure parser operation for Phase-9-provided mod bytes. Increment `CURRENT_SCHEMA_VERSION` because the published shape/meaning changes. | exact no-file fallback, entity retry, `%` tags, ambiguous modern `grass`/lamp bridge |
| R9-2 | Phase 7 | Add Phase 9 candidate/publication to the coordinated safe-boundary pipeline lifecycle; call held/reset hooks at the accepted-frame boundary; order ID augmentation inside H-ENTITY-02/03; refine H-COLOR to expose the exact `GlStateManager.glTexEnv(int,int,FloatBuffer)` `GL_TEXTURE_ENV_COLOR` buffer/current scope; add Phase-9 rows to `HookApplicationReport` and registry-remap/resource reasons. | all in-game publication, held/ID scopes, real `entityColor`, coherent layer/chunk invalidation |

Both requests change binding §5 surfaces and therefore require owner fix-up plus fresh verification
before Phase 9 implementation or Phase 10 consumption. Phase 9 does not edit either dependency.

### 5.5 Downstream hand-offs

| Consumer | Required hand-off |
|---|---|
| Phase 7 | consume `PublishedIdRuntime`, layer lookup, held/reset participant, and hook ledger exactly as §§4.9/4.11–4.13 specify |
| Phase 10 | consume only `AliasLookup.mcEntity` plus generation; own all stack/stamp/draw mechanics and stale-mesh rebuild |
| Phase 12 | supply the higher-priority user `oldHandLight` value; do not reinterpret pack tri-state |
| Phase 2/integration review | record catalog/schema/generation fingerprints and exercise precedence, hand, entity, TE, and color scenes without committing pack images |

---

## 6. Failure modes & degradation

| Failure | Detection and contained response | G2.4 rung |
|---|---|---:|
| one malformed/unknown rule, property, alias target, tag, or mod file | warn once; omit only that selector/source; sibling rules continue | 2a |
| duplicate mapping | retain higher-precedence/first assignment and record attributed loser once | 2a |
| entity modern retry empty/invalid | entity map for that source remains empty; IDs use neutral 0 | 2a |
| missing pack `block.properties` | deliberate live vanilla numeric fallback after explicit mod rules | normal fallback |
| registry snapshot inconsistent or unavailable | do not publish candidate; if current ordinals are stale, recover shaders off rather than use them | 4/5 |
| candidate build failure during ordinary reload | keep old whole pipeline when identities remain valid; report failure | 2a |
| Phase 9 publish rejects after partial pipeline publication | Phase 7 recovered-off transaction; close candidates in reverse order | 5 |
| stale alias/frame/build generation | mutation-free rejection; ID draw omitted/neutral, stale mesh discarded and rebuilt | 2a |
| entity/TE/color stack overflow, wrong token, underflow, or throwable leak | reset all three cells to neutral, disable affected producer for frame, Phase 7 drains/aborts safely | 2a |
| Phase 6 per-uniform upload failure | Phase 6 disables that uniform only; Phase 9 keeps balanced logical scopes | 2 |
| held-item/provider exception | send one zero tuple for frame; retry after next valid sample/change | 2a |
| optional dynamic-light compat failure | no-op suppression, warn once; never affect static held uniforms or program | 2a |
| H9 row application count mismatch | entity/color feature disabled when safely isolatable; shaders off if scope balance cannot be guaranteed | 2a/4 |
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
5. `ambiguousGrassAndLamp_requireModernEraProvenance` proves classic exact meaning and modern
   alternate meaning without shader-ID magic.
6. `entity11300Retry_presentEmptyOnly` covers absent, ordinary nonempty, empty→alternate, invalid
   alternate, and no-merge behavior.
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

### 8.3 Conformance and implementation gate

- Headless Phase 2 fixtures resolve representative classic and dual-spec mapping files against the
  scripted registry and record only hashes/provenance, never pack text or images.
- T1 world scenes include at least: two differently mapped blocks with properties/metas; custom
  layer non-opaque block; main/off-hand ID/light swap; two entity types; a tile entity whose block
  ID differs from entity ID; hurt entity; flashing creeper; and nested armor/eye layer.
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
| 11300 entity retry and live legacy fallback | v0.3 | alternate/absence/remap fixtures |
| immutable candidate/publication/reload | v0.3 | transaction/generation tests + R9-2 grant |
| Phase 10 alias/stamp interface | v0.3 | bit-layout/generation tests; consumed by Phase 10 |
| resolved custom render layers | v0.3 | opaque exclusion + terrain scene |
| held IDs/static lights/old mode/dynamic interop disposition | v0.3 | recording sink + hand scenes |
| entity and TE ID scopes | v0.3 | nested/throw hook tests + scenes |
| exact vanilla-argument `entityColor` | v0.3 | H9-COLOR hook test + hurt/creeper scenes |
| further modern alias/tag catalog entries | post-v0.5 | named fixture + catalog version/addendum; never heuristic |

Every component is architected here. The final row is data growth, not a deferred rewrite of the
resolver or publication shape.

---

## 10. OQ & spike specifications

Phase 9 owns no open question in §G10 or its Part II assignment. No spike is authorized or needed.
The missing Phase 3/7 surfaces are requested dependency corrections with deterministic fallback
behavior, not OQs: until granted, their named features are gated and shaders remain on the last
coherent publication or off.

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

### 11.2 Input contradictions and rulings

1. **PD tag claim vs 1.12 source/RESEARCH.** PD §8.1 groups tag expansion with the working identity
   layer, but Pintonium's vintage parser creates no tag and its modern parser owns `%`/tag lookup.
   RESEARCH says tags do not exist on 1.12 and need a shim. RESEARCH wins; §4.7 designs that shim and
   does not cite Pintonium as 1.12 tag proof.
2. **Pintonium fallback vs “re-derive values.”** Its fallback is a curated modern-name list with
   magic compatibility IDs, not a live 1.12 projection. The Phase assignment says re-derive values;
   §4.8 uses live vanilla numeric registry IDs.
3. **Phase 9 assignment vs Phase 7 `entityColor` ownership.** The assignment gives Phase 9 value
   computation/delivery, while Phase 7 already catalogs a RETURN-only H-COLOR producer. The existing
   hook cannot observe the computed operands. R9-2 retains Phase 7 hook ownership but delegates exact
   value capture/policy to this design; no duplicate mixin is assumed.
4. **11300 retry vs Phase 3 no-reopen rule.** The Phase 9 assignment requires a second parse, but
   Phase 3 forbids consumers from reopening the pack and publishes neither raw text nor an alternate.
   R9-1 makes Phase 3 publish the safe alternate, preserving its ownership and seam.

No contradiction with RESEARCH D-1…D-10 was found.

### 11.3 Open items and hand-offs

- R9-1 and R9-2 are hard implementation gates; integration review must verify both were actually
  granted and freshly verified rather than inferred from this document.
- Phase 10 must decide its chunk-build stale-result scheduling but may not change alias bit semantics
  or resolve names itself.
- The v0.3 `LegacyTagCatalog` data set must be derived from the target pack matrix and live 1.12
  registries under the explicit provider rules. Unknown tags already have a complete fallback.
- Phase 12 must supply the higher-priority user old-hand-light setting. Until then the typed default
  is `true`; no GUI is invented here.
- An external dynamic-lights adapter is optional and must be separately compatibility-gated. Its
  absence does not reduce shader identity conformance.

### 11.4 Requested upstream changes

- Apply R9-1 to `docs/phase3/v1/PHASE_3_DOC.md` through §G1.3, increment its schema, and run the fresh
  review owed by the §5 change.
- Apply R9-2 to `docs/phase7/v1/PHASE_7_DOC.md` through §G1.3 and run the fresh review owed by its §5
  and hook-ledger changes.
- In the next design candidate, qualify PD §8's “tag expansion … working in production” summary as
  modern-only in this checkout, with the 1.12 shim still required. This document does not modify
  `DESIGN.md` or PD.

---

## 12. Implementation checklist

1. **[v0.3]** Land and freshly verify Phase 3 R9-1: file states, alternate entity parse,
   `MappingEra`, tag selector, schema bump, and named parser tests.
2. **[v0.3]** Land and freshly verify Phase 7 R9-2: candidate lifecycle, accepted-frame call,
   H-ENTITY ordering, exact H-COLOR observation, reload reasons, and hook report.
3. **[v0.3]** Implement/validate `IdRegistrySnapshot` schema and scripted snapshot tests; add D-6
   forbidden-type checks.
4. **[v0.3]** Implement the bounded jar/directory `ModIdSourceSnapshot` adapter and containment/order
   tests.
5. **[v0.3]** Implement source merge/precedence and conflict diagnostics; pass all pack/mod/tag/
   fallback permutation tests.
6. **[v0.3]** Implement exact name, metadata, property, and legacy numeric resolution with invalid
   predicate no-overmatch tests.
7. **[v0.3]** Implement/version the modern-name and fluid alias catalog; validate live targets and
   classic/modern ambiguous-token fixtures.
8. **[v0.3]** Implement `LegacyTagCatalog` providers and explicit OreDictionary mapping; pass
   entries-before-tags and provider-failure tests.
9. **[v0.3]** Implement the 11300 selection rule and absent-only legacy fallback; test remap identity.
10. **[v0.3]** Implement resolved layer tables and opaque-solid exclusion; wire generation-based
    chunk invalidation with Phase 7/10.
11. **[v0.3]** Implement candidate/publisher ownership, atomic safe-boundary integration, retirement,
    recovered-off rollback, and generation tests.
12. **[v0.3]** Implement the allocation-free `AliasLookup`/`mcEntity` arrays and Phase 10 contract
    tests, including representability and stale work.
13. **[v0.3]** Implement held-hand sampling/policy and recording Phase 6 sink tests; keep dynamic
    interop optional and separately gated.
14. **[v0.3]** Implement H9-ENTITY-ID and H9-BLOCK-ENTITY-ID balanced stacks, injection-order tests,
    throw drain, and hook health rows.
15. **[v0.3]** Implement H9-COLOR exact-argument capture/neutral restoration and hurt/creeper/nested
    layer tests without suppressing vanilla's original FF call.
16. **[v0.3]** Run the headless scripted-registry suite and licensed Phase 2 T0/T1 scenes; record
    manifests/hashes only and meet the full §8.3 gate.
17. **[post-v0.5]** Extend alias/tag catalog data only through named fixtures, live-target proofs,
    version increments, and a Phase 9 addendum; do not add similarity heuristics.
