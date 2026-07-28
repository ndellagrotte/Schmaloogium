# Schmaloogium — Phase 4: Stage/program registry & compilation — Architecture

## 0. Header

**Phase:** 4 — Stage/program registry & compilation

**Date:** 2026-07-28

**Milestone:** v0.1, with the full modern-superset shape present but later families dormant

**Governing assignment:** `docs/design/v2.0-RC3/DESIGN.md:1471`, through the Phase 4
specification ending at line 1570. RC3 is the governing revision for this new phase document; it
does not migrate Phase 1 or Phase 2.

This is a build-session architecture document. It changes no source file, build file, research or
design revision, reference report, dependency phase document, or review.

### 0.1 Inputs actually read

The mandatory and assigned inputs were read as follows:

- `AGENTS.md`, completely.
- `docs/MOVES.md`, including the four-`DESIGN.md` warning, current version paths, roll procedure,
  and three-line dangling-reference baseline.
- `docs/design/v2.0-RC3/DESIGN.md` Part I, §G0–§G12, and the Phase 4 specification at
  `docs/design/v2.0-RC3/DESIGN.md:1471`.
- `docs/research/v1/RESEARCH.md` §0, §1, §3.1, §3.6.1, §4.1 steps 4–5, §4.2, §7.3, and
  Appendix A in full.
- `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, especially the legally clean
  “Shader Programs” table at line 61 and the geometry-shader declarations at lines 349–356.
- `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §3 and §13.
- The load-bearing LGPL Pintonium sources named below, after applying §G11's exclusions:
  - `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/programs/ProgramFallbackResolver.java`
  - `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/loading/ProgramId.java`
  - `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/Program.java`
  - `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/shader/ProgramCreator.java`
  - `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/WorldRenderingPhase.java`
  - `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java`
  - `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CompositeRenderer.java`
  - `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/PipelineManager.java`
  - `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/compat/sodium/impl/shader_overrides/IrisChunkProgramOverrides.java`

The source checks establish only the following claims:

- the fallback resolver recursively follows an explicit parent and memoizes the result
  (`ProgramFallbackResolver.java:35`, `"if (source == null)"`; line 39,
  `"source = resolveNullable(fallback)"`; line 43, `"cache.put(id, source)"`);
- its declared relevant edges include `ShadowSolid → Shadow`, `ShadowCutout → Shadow`,
  `TerrainSolid/TerrainCutout → Terrain`, `Water → Terrain`, and `HandWater → Hand`
  (`ProgramId.java:11`–`13`, `:24`–`26`, and `:45`–`46`);
- the program-use operation binds, then refreshes uniforms, samplers, and images
  (`Program.java:28`, `"public void use()"`; lines 30–34);
- Pintonium pre-binds the incompatible locations 11/11/12/13/14
  (`ProgramCreator.java:21`–`25`), confirming the warning rather than supplying values to copy;
- the per-pass object records routing, viewport scale, flip snapshot, and mipmap set
  (`CompositeRenderer.java:156`–`163` and `:431`–`442`);
- pipeline destruction increments a generation (`PipelineManager.java:78`–`88`) and a downstream
  cache invalidates when its stored value differs
  (`IrisChunkProgramOverrides.java:135`–`139`).

All Pintonium-derived claims in this document carry
`[V:observed — Pintonium <repo-relative path>]`. Nothing is derived from the prohibited
transformation dependency, vendored stareval, the deleted refactor archive, the stray VintageFix
configs, or Pintonium's stale `DESIGN.md`.

### 0.2 Dependency PHASE docs consumed

- `docs/phase1/v14/PHASE_1_DOC.md` is verified by the literal PASS in
  `docs/phase1/reviews/PHASE_1_REVIEW_15.md`. Its §5 was read completely. Its §4.7
  `engine.gl` facade was read to obtain the exact service and recording signatures that §5 makes
  binding.
- `docs/phase3/v1/PHASE_3_DOC.md` is verified by the literal PASS, zero findings, and
  no-interface-change disposition in
  `docs/phase3/reviews/PHASE_3_REVIEW_14.md`. Its current path has not yet been rolled after that
  loop, so this document cites the artifact actually verified in the worktree. Its §5 was read
  completely; §2.2, §3.2–§3.3, and §4.5/§4.7–§4.10 were read to clarify the published
  `PackConfiguration`, materializer, geometry, per-program state, and fingerprint contracts.

Only the dependencies' §5 surfaces bind this phase. Clarifying prose is not silently promoted to
an interface.

### 0.3 Deviations, extra reads, and tool disposition

Three genuine gaps required narrow extra reads:

1. `docs/research/v1/RESEARCH.md` §3.2 and §6.2 were read because the Phase 4 scope itself cites
   them for dual-form geometry shaders, while the Required-input list omits them.
2. `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md` §3.1 lines 145–153 was read
   only to resolve the legacy geometry topology absent from the author-facing declaration:
   it behaviorally records `TRIANGLES → TRIANGLE_STRIP`. This is decompilation-derived digest
   evidence, not copied structure or identifiers.
3. PD §§1–2 and the opening of §4 were displayed while locating §3 by line range. They supplied
   provenance/trap orientation only; no Phase 4 design claim depends on §4.

A broad Pintonium source-location search emitted filenames/matches from unrelated areas before the
search was narrowed. No transformation file was opened, quoted, summarized, cited, or used.

No Cleanroom MCP symbol query was needed. This phase has no vanilla type, method, field, hook, or
loader API: all code is in pure `:engine` packages and all GL access is through Phase 1's facade.
Inventing a vanilla query would add an out-of-scope input rather than resolve a symbol. Phase 7 and
Phase 8 own the vanilla hook sites that call the interfaces defined here.

`docs/reference/oculus/v1.0/OCULUS_DESIGN.md` and the Oculus source tree were not read. RC3 §G12.6
explicitly says its reading map does not amend a phase's Required inputs without a future
adoption/fix-up or explicit brief. No forbidden `docs/**/chatlogs/` directory or root `*.txt` was
read.

### 0.4 Legal and provenance posture

The pack-author document is contract evidence and may be cited. Pintonium is LGPL-3.0 evidence and
may be incorporated only with notices preserved and modifications marked. This design adopts
small structural mechanisms, not source text. The OptiFine-derived digest is used only to restate
observed behavior. The AGPL-risk transformation boundary is prohibited absolutely. These
dispositions follow D-7/D-8 and `docs/design/v2.0-RC3/DESIGN.md` §G7/§G11.

## 1. Scope & boundaries

### 1.1 What Phase 4 owns

Phase 4 owns:

- the pure `engine.registry` representation of the complete modern stage superset;
- a schedule representation that permits one stage identity, notably `gbuffers`, to occur in
  more than one frame band;
- sparse pass families whose legal indices are 0…99 without a 16- or 8-element structural limit;
- dormant compute companion slots for the primary `.csh` and `_a`…`_z` forms;
- the classic program catalog, source-stem mapping, stage membership, fallback graph, virtual
  flip-control slots, and effective-program resolution;
- immutable per-program state assembled exclusively from Phase 3 output;
- validation of draw routing, attribute-location capability, source-stage composition, and
  geometry strategy before GL publication;
- render-thread compile, attach, fixed pre-link attribute binding, link, validate, cleanup, and
  rung-3 failure conversion through `engine.gl`;
- immutable compiled-registry publication and teardown of Phase-4-owned shader/program handles;
- the “use program” state-barrier interface, including shadow override, program selection,
  alpha/blend lock lifetime, and ordered extension points for Phase 6;
- a monotonically changing pipeline generation consumed by caches and reload paths; and
- diagnostic data needed for the log and shader GUI.

### 1.2 Explicit adjacent ownership

| Concern touched here | Owner |
|---|---|
| Pack discovery, includes, macro preprocessing, option rewriting, directive scanning, source maps, `shaders.properties`, and source materialization | **Phase 3**; Phase 4 requests materialized roots and never reparses or reopens a pack |
| Texture/FBO creation, actual ping-pong side selection, clear rules, framebuffer routing, and realization of flip snapshots | **Phase 5** |
| Sampler-unit map, sampler re-pointing, built-in locations/values/uploads, and uniform error isolation | **Phase 6** |
| Custom-uniform expression evaluation | **Phase 11**, integrated through Phase 6's barrier participant |
| Pass execution, fullscreen draws, `countInstances` loops/re-renders, hook-to-slot mapping, final-to-Minecraft-FBO handoff, and fixed-function restoration around the frame | **Phase 7** |
| Shadow camera, traversal, FBO use, and invocation | **Phase 8**; Phase 4 only supplies force-selection semantics |
| Vertex-buffer layout and enabling the attributes pre-bound here | **Phase 10** |
| Options UI, persistence, and reload triggers | **Phase 12**; Phase 4 supplies the generation signal |
| Modern pass-family activation, compute dispatch, images/SSBOs, and work-group/barrier policy | **G8/S1 and G8/S2**; Phase 4 reserves slots only |
| Shared-context asynchronous compilation | **Phase 14/OQ-15**; Phase 4's v0.1 compiler is synchronous |
| Internal default-pack GLSL and the full frame bootstrap | **Phase 7**; this phase does not grow a second pack source |

No Minecraft, Forge, Cleanroom, Mixin, LWJGL, raw GL constant, framebuffer object, texture object,
uniform value provider, or render hook enters `engine.registry`.

## 2. Architecture overview

### 2.1 Invariants

1. **The schedule is data, not enum ordinal control flow.** `StageId.GBUFFERS` can occur before and
   after deferred without duplicating its identity.
2. **The modern shape exists at v0.1.** Dormant families are represented and headless-tested; adding
   `prepare37` or a `_q.csh` companion changes configuration data, not types or algorithms.
3. **Pack truth enters once.** Program state and source availability come from
   `PackConfiguration`; Phase 4 never scans source text or properties.
4. **A slot and its effective provider are distinct.** A hook requests a logical slot. Backup
   resolution may return an ancestor, and the ancestor's entire state bundle travels with its
   program.
5. **Virtual slots never masquerade as programs.** `deferred_pre` and `composite_pre` can mutate
   flip policy but cannot compile, bind, or become fallback providers.
6. **GL candidates are transactional.** No partially compiled registry is published. Every
   unpublished handle is deleted on every exit.
7. **Every program activation crosses one barrier.** Hook sites never repeat sampler, uniform,
   custom-uniform, or alpha/blend policy.
8. **The registry is pure; the compiler is render-thread-bound.** Descriptor construction and
   source materialization may run off-thread. Facade calls may not.

### 2.2 Public shape

Illustrative signatures name the binding design. Implementations live under
`com.schmaloogium.engine.registry.internal`.

```java
package com.schmaloogium.engine.registry;

public enum StageId {
    SETUP, BEGIN, SHADOW, SHADOWCOMP, PREPARE,
    GBUFFERS, DEFERRED, COMPOSITE, FINAL
}

public enum StageBand {
    LOAD_OR_RESIZE,
    FRAME_BEGIN,
    SHADOW,
    AFTER_SHADOW,
    BEFORE_GBUFFERS,
    GBUFFERS_OPAQUE,
    BETWEEN_GBUFFERS,
    GBUFFERS_TRANSLUCENT,
    FRAME_END,
    SCREEN
}

public record StageStep(
    StageId stage,
    StageBand band,
    PassPopulation population) {}

public sealed interface PassPopulation {
    record Singleton() implements PassPopulation {}
    record NamedPrograms(List<ProgramSlotId> slots) implements PassPopulation {}
    record SparseArray(int highestLegalIndex, int highestPopulatedIndex)
        implements PassPopulation {}
}

public record PassIndex(int value) {
    public PassIndex {
        if (value < 0 || value > 99) throw new IllegalArgumentException("pass index");
    }
}

public record BufferRef(BufferDomain domain, int index) {}
public enum BufferDomain { COLORTEX, SHADOWCOLOR, SHADOWTEX, DEPTH, SCREEN, EXTERNAL }

public record PassResourceAccess(
    Set<BufferRef> readable,
    Set<BufferRef> writes,
    Map<BufferRef, Boolean> explicitFlips,
    Set<BufferRef> mipmappedBeforeRead) {}

public sealed interface ComputeDispatchSlot {
    record Primary() implements ComputeDispatchSlot {}
    record Companion(char suffix) implements ComputeDispatchSlot {
        public Companion {
            if (suffix < 'a' || suffix > 'z') throw new IllegalArgumentException("suffix");
        }
    }
}
```

`highestLegalIndex` is 99 for modern array families. `highestPopulatedIndex` is configuration:
15 for classic deferred/composite, `-1` for dormant modern families, and at most 99. Population is
sparse; absence at index 4 does not suppress index 5.

Program-side types:

```java
public record ProgramSlotId(String packName) {}

public enum ProgramSlotKind {
    RASTER, VIRTUAL_FLIP_CONTROL, FIXED_FUNCTION_SENTINEL
}

public record ProgramSlotDescriptor(
    ProgramSlotId id,
    StageId stage,
    ProgramSlotKind kind,
    Optional<String> sourceStem,
    Optional<ProgramSlotId> fallback,
    Set<StageBand> permittedBands) {}

public sealed interface DrawRouting {
    record AllUsedBuffers(BufferDomain domain) implements DrawRouting {}
    record Explicit(List<BufferRef> buffers) implements DrawRouting {}
}

public record ProgramStateBundle(
    DrawRouting drawRouting,
    Set<BufferRef> compositeMipmaps,
    int instanceCount,
    Set<ExtendedAttribute> attributes,
    Optional<AlphaTestSpec> alphaTest,
    Optional<BlendSpec> blend,
    ViewportScale viewportScale,
    Map<BufferRef, Boolean> explicitFlips,
    Optional<LegacyGeometryConfig> legacyGeometry) {}

public sealed interface CompiledProgramBinding {
    ProgramStateBundle state();
    ProgramSlotId provider();

    record ShaderProgram(
        ProgramSlotId provider,
        ProgramHandle handle,
        ProgramStateBundle state,
        List<MaterializationFingerprint> sources)
        implements CompiledProgramBinding {}

    record FixedFunction(
        ProgramSlotId provider,
        ProgramStateBundle state)
        implements CompiledProgramBinding {}
}

public record ResolvedProgramBinding(
    ProgramSlotId requested,
    CompiledProgramBinding effective,
    List<ProgramSlotId> fallbackPath) {}
```

`AlphaTestSpec`, `BlendSpec`, `ViewportScale`, `LegacyGeometryConfig`, and
`ExtendedAttribute` are Phase 3 values or lossless Phase-4-owned mirrors created by an explicit
adapter. They never contain GL constants.

The registry build/publication boundary:

```java
public interface ProgramRegistryCompiler {
    RegistryBuildResult compile(RegistryBuildRequest request);
}

public record RegistryBuildRequest(
    PackConfiguration configuration,
    DimensionKey dimension,
    MacroContribution macroContribution,
    GLCapabilityProfile capabilities,
    GLDevice device,
    DiagnosticReporter diagnostics) {}

public sealed interface RegistryBuildResult {
    record Ready(CompiledProgramRegistry registry) implements RegistryBuildResult {}
    record ShadersOff(RegistryBuildFailure failure) implements RegistryBuildResult {}
}

public interface CompiledProgramRegistry extends AutoCloseable {
    StageRegistry stages();
    Optional<ResolvedProgramBinding> resolve(ProgramSlotId requested);
    RegistryFingerprint fingerprint();
    void close(); // render thread; idempotent; deletes only Phase-4-owned handles
}

public interface ProgramRegistryPublisher {
    PublishedRegistry current();
    PublishedRegistry publish(RegistryPublication publication);
}

public record PublishedRegistry(
    long generation,
    Optional<CompiledProgramRegistry> registry) {}
```

`publish` is render-thread-only and increments generation exactly once for every accepted
replacement, including an accepted shaders-off publication. Failed candidate compilation alone
does not mutate publication state. Equality, never ordering or subtraction, is the cache protocol;
eventual signed-`long` wrap does not make a stale equality likely within the life of a process.

### 2.3 Relationship map

```text
PackConfiguration + DimensionKey + MacroContribution + GLCapabilityProfile
             │
             ├─ StageRegistryDefinition ── G6 / modern configuration
             ├─ ClassicProgramCatalog  ── fallback graph
             └─ SourceMaterializer requests (Phase 3)
                              │
                       RegistryBuildPlan       (pure/off-thread)
                              │
                       ProgramCompiler         (render thread, GLDevice)
                              │
                    CompiledProgramRegistry
                       │       │         │
                 Phase 5    Phase 6   Phase 7/8/10
                  state     barrier      selection
                       └────────┬────────┘
                         PublishedRegistry
                         generation → Phase 12/caches
```

## 3. Contract conformance map

### 3.1 Stage, lifecycle, compile, and barrier contracts

| In-scope contract item | Design element | Provenance |
|---|---|---|
| Modern order `setup → begin → shadow → shadowcomp → prepare → gbuffers opaque → deferred → gbuffers translucent → composite → final` | `ModernSupersetConfiguration` supplies ten `StageStep`s; `GBUFFERS` appears twice under distinct bands | `[D-4]`, `docs/research/v1/RESEARCH.md:336`–`:355` |
| `setup`, `begin`, `shadowcomp`, `prepare` arrays accept 0…99 | `PassIndex`, `SparseArray(99, …)`, sparse map storage | `[V:doc/web]`, `docs/research/v1/RESEARCH.md:340`–`:353` |
| G6 order and five active identities | `ClassicG6Configuration` in §4.2 | `[V:doc]`, `docs/research/v1/RESEARCH.md:220`–`:224` |
| Per-pass read/write sets and flip bookkeeping | immutable `PassResourceAccess`; exact writes and flips from Phase 3, stage-readable sets from the stage policy | `[D-4]`, `docs/research/v1/RESEARCH.md:835`–`:841` |
| Dormant `.csh`, `_a`…`_z` companions | `ComputeDispatchSlot`; no compiler/executor path before G8/S2 | `[D-4]`, Phase 4 assignment at `docs/design/v2.0-RC3/DESIGN.md:1486`–`:1491` |
| Pack-load initialization compiles, resolves fallbacks, then publishes | transactional compiler and publisher | `[V:observed]`, `docs/research/v1/RESEARCH.md:483`–`:488` |
| Pack/option/dimension/resolution uninit deletes GL objects | registry `close`, candidate cleanup, generation publication | `[V:observed]`, `docs/research/v1/RESEARCH.md:489`–`:491` |
| Compile → attach → bind 10/11/12 → link → validate | state machine in §4.7 through Phase 1 `ShaderService` | `[V:observed]`, `docs/research/v1/RESEARCH.md:497`–`:505` |
| Core program/shader object facade, no ARB object entry points | only Phase 1 `ShaderService` is consumed | `[U]` opportunity adopted by governing spec; `docs/research/v1/RESEARCH.md:766`–`:770` |
| Legacy and core geometry forms are accepted | plan selection in §4.8; unresolved interface contradiction is explicit, not hidden | `[V:doc]`, `docs/research/v1/RESEARCH.md:213`–`:216`, App A.3 |
| Invalid compile/link/validate result deletes program and reports to GUI/log | `ProgramBuildFailure`, cleanup ledger, backup re-resolution | `[V:observed]`, `docs/research/v1/RESEARCH.md:501`–`:505` |
| Program use re-points samplers, refreshes built-ins, evaluates customs, locks alpha/blend | ordered `ProgramStateBarrier` in §4.10 | `[V:observed]`, `docs/research/v1/RESEARCH.md:505`–`:507`; adoption `D-P4-5` |
| Shadow pass overrides hook-requested program | barrier selection step 1 | `[V:observed]`, `docs/research/v1/RESEARCH.md:506`–`:507` |
| Reload invalidates downstream derived caches | `PublishedRegistry.generation` equality protocol | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/PipelineManager.java:87, "versionCounterForSodiumShaderReload++"]`; `D-P4-8` |

### 3.2 Appendix A.1 program/fallback map

The table below maps every named Appendix A.1 row. “Fixed” means the contract has no shader
ancestor and requires fixed-function/no-program behavior; it does not mean Phase 4 assumes a facade
verb that Phase 1 has not published.

| Slot or family | Stage/band | Fallback/effective absence | State/source design |
|---|---|---|---|
| `<none>` | external GUI/menu sentinel | fixed | `FIXED_FUNCTION_SENTINEL`, never compiled |
| `shadow` | shadow | fixed; root itself never inherits | raster state from `shadow.*` |
| `shadow_solid` | shadow | `shadow` | full provider-state inheritance |
| `shadow_cutout` | shadow | `shadow` | full provider-state inheritance |
| `gbuffers_basic` | gbuffers | fixed | base raster slot |
| `gbuffers_textured` | gbuffers | `gbuffers_basic` | full provider-state inheritance |
| `gbuffers_textured_lit` | gbuffers | `gbuffers_textured` | full provider-state inheritance |
| `gbuffers_skybasic` | gbuffers opaque | `gbuffers_basic` | named raster slot |
| `gbuffers_skytextured` | gbuffers opaque | `gbuffers_textured` | named raster slot |
| `gbuffers_clouds` | gbuffers, executor-selected band | `gbuffers_textured` | named raster slot |
| `gbuffers_terrain` | gbuffers opaque | `gbuffers_textured_lit` | named raster slot |
| `gbuffers_terrain_solid` | gbuffers opaque, dormant in G6 | `gbuffers_terrain` | retained from day one |
| `gbuffers_terrain_cutout_mip` | gbuffers opaque, dormant in G6 | `gbuffers_terrain` | retained from day one |
| `gbuffers_terrain_cutout` | gbuffers opaque, dormant in G6 | `gbuffers_terrain` | retained from day one |
| `gbuffers_damagedblock` | gbuffers opaque | `gbuffers_terrain` | named raster slot |
| `gbuffers_block` | gbuffers, executor-selected band | `gbuffers_terrain` | named raster slot |
| `gbuffers_beaconbeam` | gbuffers, executor-selected band | `gbuffers_textured` | named raster slot |
| `gbuffers_item` | gbuffers, dormant in G6 | `gbuffers_textured_lit` | retained from day one |
| `gbuffers_entities` | gbuffers, executor-selected band | `gbuffers_textured_lit` | named raster slot |
| `gbuffers_entities_glowing` | gbuffers, executor-selected band | `gbuffers_entities` | named raster slot |
| `gbuffers_armor_glint` | gbuffers, executor-selected band | `gbuffers_textured` | named raster slot |
| `gbuffers_spidereyes` | gbuffers, executor-selected band | `gbuffers_textured` | named raster slot |
| `gbuffers_hand` | gbuffers translucent-side frame portion | `gbuffers_textured_lit` | executor owns exact hook time |
| `gbuffers_weather` | gbuffers translucent-side frame portion | `gbuffers_textured_lit` | executor owns exact hook time |
| `gbuffers_water` | gbuffers translucent | `gbuffers_terrain` | named raster slot |
| `gbuffers_hand_water` | gbuffers translucent | `gbuffers_hand` | named raster slot |
| `deferred_pre` | deferred prelude | no program/fallback | virtual flip-control state only |
| `deferred` … `deferred15` | between gbuffers occurrences | absent pass is skipped | `PassIndex(0…15)` under generic 0…99 family |
| `composite_pre` | composite prelude | no program/fallback | virtual flip-control state only |
| `composite` … `composite15` | frame end | absent pass is skipped | `PassIndex(0…15)` under generic 0…99 family |
| `final` | screen | absent means fixed-function passthrough | terminal raster/fixed binding |

The exact names and edges above come from
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:61`–`:108` and
`docs/research/v1/RESEARCH.md:1106`–`:1141` `[V:doc]`.

Pintonium cross-validation is deliberately narrower than this table. Its source directly confirms
the shadow, terrain-solid/cutout, water, and hand-water edges
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/loading/ProgramId.java:11-13, :24-26, :45-46]`.
Its extra modern slots and missing classic `terrain_cutout_mip` row do not alter the contract.

### 3.3 Appendix A.2 and A.3 program-state map

| Contract row | Phase 4 field/algorithm | Provenance / decision |
|---|---|---|
| Empty source inherits nearest non-empty ancestor's **entire configuration** | `ResolvedProgramBinding` points at the ancestor's one immutable `CompiledProgramBinding`; no child field is overlaid | App A.2 `[V:observed]`; `D-P4-4` |
| Disabled/profile-disabled is absent | availability filter runs before graph resolution | App A.2; Phase 3 §5 `ProgramStateModel` |
| `shadow` never inherits | `shadow` has no parent; its two children may inherit it as the table says | App A.1/A.2 |
| `mc_Entity` | `ExtendedAttribute.MC_ENTITY` → bind location 10 when declared | App A.3 `[V:doc]` |
| `mc_midTexCoord` | `ExtendedAttribute.MC_MID_TEX_COORD` → 11 when declared | App A.3 `[V:doc]` |
| `at_tangent` | `ExtendedAttribute.AT_TANGENT` → 12 when declared | App A.3 `[V:doc]` |
| `countInstances` | positive `instanceCount`, exposed unchanged; Phase 7 executes | App A.3 `[V:doc]` |
| ARB geometry extension + `maxVerticesOut` | `LegacyGeometryStrategy`; topology is triangles → triangle strip, count is Phase 3's positive value | App A.3 plus behavioral digest; `D-P4-7` |
| `DRAWBUFFERS` / `RENDERTARGETS` | `DrawRouting.Explicit`, validated in order and without deduplication | App A.3 `[V:doc]` |
| Absent routing | `DrawRouting.AllUsedBuffers` resolved by Phase 5 against its estate | §3.2 `[V:doc]` |
| `colortexNMipmapEnabled` | immutable `compositeMipmaps`; generation is Phase 7, texture policy Phase 5 | App A.3 `[V:doc]` |
| `alphaTest.<prog>` | optional lock value, including explicit OFF | App F.7 through Phase 3 §5 |
| `blend.<prog>` | optional lock value, including explicit OFF | App F.7 through Phase 3 §5 |
| `scale.<prog>` | immutable `ViewportScale`; Phase 7 applies to Phase 5's estate | App F.7 through Phase 3 §5 |
| `flip.<prog>.<buf>` | exact tri-state result represented as only explicit map entries; Phase 5 owns default/side realization | App F.7 through Phase 3 §5 |
| `program.<prog>.enabled` and profile disable | compile availability; false behaves exactly like missing source | App F.7/App A.2 through Phase 3 §5 |

The resource-allocation directives elsewhere in Appendix A.3 remain Phase 5/6/8/13 inputs.
Phase 4 retains only the per-program fields its assignment names.

### 3.4 Pintonium mechanism disposition

| Reference mechanism | Contract check | Phase 4 disposition |
|---|---|---|
| Recursive memoized fallback | Checked edge by edge against App A.1/A.2; contract adds rows the reference lacks | Adopt structure under `D-P4-4`; never import reference enum values |
| `Program.use()` as universal barrier | Checked against RESEARCH §4.2's four barrier duties | Adopt ordered interface under `D-P4-5`; images/memory barriers remain G8/S2 |
| `CompositeRenderer.Pass` field bundle | Compared with App A.1/F.7 and Phase 3 §5 | Routing, viewport, mipmap and flip-config shapes retained. Framebuffer and resolved flip side belong to Phase 5. Per-buffer blend and compute execution are not current Phase 3 inputs |
| 26-value `WorldRenderingPhase` + override/deferred-pop | Compared with D-4 and Phase 7 ownership | Structural cross-check only. Stage identity and hook-time rendering phase remain separate types |
| Locations 11/11/12/13/14 | Conflicts with App A.3's 10/11/12 | Pre-decided rejection `D-P4-6` |
| Generation counter | Reload invalidation is not pack syntax but satisfies the assigned cache contract | Adopt equality-based long generation under `D-P4-8` |
| Generated legacy-compat shaders selected by pack-layout heuristics | Heuristics have no App A/F contract and are a recorded bug source | Reject. Fixed fallback and explicit backup edges only |

No PD §17 dead/stub feature or PD §18 divergence is inherited.

## 4. Detailed design

### 4.1 Stage identity is separate from schedule occurrence

`StageId` answers “which pack stage owns this pass?” `StageBand` answers “where can the executor
place it?” The distinction is load-bearing because the contract has one `gbuffers` program family
on both sides of deferred.

`StageRegistryDefinition` validates:

1. all nine `StageId` values exist exactly once as identities;
2. a schedule may contain multiple occurrences of one identity only when the definition permits
   it (`GBUFFERS` is the initial permitted case);
3. array populations use 0…99 and contain no duplicate index;
4. a pass descriptor's stage matches the schedule identity that contains it;
5. `FINAL` is singleton and last in every frame schedule;
6. `SETUP` is outside the per-frame schedule under `LOAD_OR_RESIZE`;
7. virtual pre slots precede their corresponding indexed family; and
8. compute slots are descriptors only unless the configuration explicitly enables G8/S2.

Pintonium's typed rendering phase is evidence that a typed, overrideable phase state works, but it
does not collapse these two concepts. Its enum is a fine-grained draw-phase list
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/WorldRenderingPhase.java:3, "public enum WorldRenderingPhase"]`,
while override selection is separate
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java:1244, "if (overridePhase != null)"]`.

### 4.2 The two required configurations

The G6 configuration is:

| Order | Stage step | Population |
|---:|---|---|
| 1 | `SHADOW / SHADOW` | named `shadow`, `shadow_solid`, `shadow_cutout` |
| 2 | `GBUFFERS / GBUFFERS_OPAQUE` | hook-selected named slots before deferred |
| 3 | `DEFERRED / BETWEEN_GBUFFERS` | virtual `deferred_pre`, then sparse indices 0…15 |
| 4 | `GBUFFERS / GBUFFERS_TRANSLUCENT` | hook-selected translucent-side named slots |
| 5 | `COMPOSITE / FRAME_END` | virtual `composite_pre`, then sparse indices 0…15 |
| 6 | `FINAL / SCREEN` | singleton `final` |

“Five stages” counts identities: shadow, gbuffers, deferred, composite, final. Six schedule steps
are required because gbuffers occurs twice.

The full-shape configuration is:

| Order/cadence | Stage step | Legal population |
|---|---|---|
| load + resize | `SETUP / LOAD_OR_RESIZE` | sparse 0…99, compute-only |
| 1 | `BEGIN / FRAME_BEGIN` | sparse 0…99 |
| 2 | `SHADOW / SHADOW` | named shadow programs |
| 3 | `SHADOWCOMP / AFTER_SHADOW` | sparse 0…99 |
| 4 | `PREPARE / BEFORE_GBUFFERS` | sparse 0…99 |
| 5 | `GBUFFERS / GBUFFERS_OPAQUE` | named programs |
| 6 | `DEFERRED / BETWEEN_GBUFFERS` | virtual pre + sparse 0…99 |
| 7 | `GBUFFERS / GBUFFERS_TRANSLUCENT` | named programs |
| 8 | `COMPOSITE / FRAME_END` | virtual pre + sparse 0…99 |
| 9 | `FINAL / SCREEN` | singleton |

At v0.1, `SETUP`, `BEGIN`, `SHADOWCOMP`, and `PREPARE` have
`highestPopulatedIndex = -1`; their ability to hold descriptors, access sets, flips, and compute
companions is nevertheless tested. G8/S1 changes only population and executor wiring.

### 4.3 Sparse pass families and source stems

Index zero uses the unsuffixed name: `deferred`, `composite`, `begin`, and so on. Positive indices
append the decimal value without zero padding. The naming function is total only for 0…99 and is
round-trip-tested.

A raster pass may own compute descriptors `Primary` and `Companion('a'…'z')`. These map to the
pack's primary `.csh` and suffixed `_a`…`_z.csh` names. The slots contain no source, work-group
dimensions, image binding, SSBO binding, indirect pointer, or dispatch function at v0.1. That
information is intentionally impossible to construct before G8/S2.

No registry array is allocated at a constant 8 or 16. A compact implementation may use an array
sized `highestPopulatedIndex + 1` for one immutable configuration, but every API and validation
rule is expressed through `PassIndex` and the definition's limit.

### 4.4 Resource-access model

`PassResourceAccess` is an execution contract, not a claim that Phase 4 knows every sampler
declaration:

- `readable` is the complete set the stage is allowed to sample from. For classic deferred,
  composite, and final it is all allocated colortex buffers; for shadowcomp it is all allocated
  shadow depth/color inputs; for gbuffers it is external/game textures and any stage-legal pack
  textures. A symbolic range is expanded only after Phase 5 supplies the estate size.
- `writes` is exact when routing is explicit. `AllUsedBuffers(domain)` remains symbolic until
  Phase 5 supplies the configured used-buffer set.
- `explicitFlips` is only the Phase 3 tri-state override entries. Absence is not serialized as
  false.
- `mipmappedBeforeRead` is the per-program request set Phase 3 publishes. Phase 7 performs the
  operation using Phase 5 textures after checking Phase 1 capabilities.

The registry never stores a “main” or “alt” texture choice. That snapshot depends on previous
passes and belongs to Phase 5. This is the deliberate difference from Pintonium's per-pass
`stageReadsFromAlt` field at `CompositeRenderer.java:159`.

### 4.5 Classic catalog construction and cardinality independence

`ClassicProgramCatalog` is constructed from declarative rows and validates unique names, legal
stages, known fallback parents, acyclic edges, virtual-slot isolation, and source-stem uniqueness.
It is not constructed with a fixed catalog-size array.

The corrected governing inputs agree:

- `docs/research/v1/RESEARCH.md:1142` says
  "Count: 60 named shader/virtual slots, excluding the external `<none>` sentinel";
- that table names 3 shadow slots, 22 gbuffers slots, 17 deferred entries including pre,
  17 composite entries including pre, and final: **60** named shader/virtual slots, excluding the
  `<none>` sentinel; and
- the pack-author table at
  `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:61`–`:108` independently lists those
  same names and both arrays through index 15.

The concrete contract rows remain controlling. The catalog therefore contains every table row,
reports both `declaredClassicCount = 60` and `enumeratedClassicCount = 60` in a contract test, and
makes no behavior depend on either number. The historic 43/60 contradiction is resolved upstream,
without dropping any named row or turning the corrected summary count into an allocation constant.
Catalog rows are constructed first; the numeric total remains validation metadata only.

### 4.6 Availability and backup-chain resolution

For each non-virtual slot, planning produces one availability:

```java
sealed interface PlannedAvailability {
    record Present(ProgramBuildPlan plan) implements PlannedAvailability {}
    record Missing() implements PlannedAvailability {}
    record Disabled(DisableReason reason) implements PlannedAvailability {}
    record Failed(ProgramBuildFailure failure) implements PlannedAvailability {}
}
```

`Missing` means no `.vsh`, `.gsh`, or `.fsh` source exists for the stem. `Disabled` covers both
`program.<name>.enabled=false` and active-profile disable. `Failed` is introduced after
materialization or GL build failure. All three are absent for fallback purposes.

Resolution is a memoized depth-first walk over the immutable descriptor graph:

1. a present, successfully compiled slot resolves to itself;
2. otherwise follow its parent;
3. return the first successful ancestor;
4. retain the requested-to-provider path for diagnostics;
5. if the chain ends, return the descriptor's terminal action: fixed, skip, passthrough, or
   unavailable; and
6. a cycle is a catalog construction error, never a pack error.

The result points to the provider's *single* immutable `CompiledProgramBinding`. It does not copy
the handle and overlay the child's alpha, blend, routing, scale, flips, mipmaps, attributes, or
instance count. This is how “entire configuration” remains literal.

“Shadow never inherits” means the `shadow` root has no parent. It does not erase the explicit
`shadow_solid → shadow` and `shadow_cutout → shadow` edges in App A.1.

This algorithm adopts the reference's recursive/memoized shape
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/programs/ProgramFallbackResolver.java:39, "source = resolveNullable(fallback)"]`
only after the App A check recorded as `D-P4-4`.

### 4.7 Source planning, compilation, and cleanup

Planning is pure:

1. select the exact `DimensionConfiguration`; disabled dimension returns a shaders-off plan;
2. select the Phase 3 `SourceKey`s for the program stem without merging base and override;
3. obtain Phase 3's already-evaluated enabled/profile-disabled state;
4. choose `GeometryTranslationRequest.None` or the geometry strategy in §4.8;
5. call `SourceMaterializer.materialize` once per available source stage with the exact active
   `OptionState`, singular Phase 6 contribution, and geometry request;
6. retain each `MaterializedSource`, source map, diagnostics, and materialization fingerprint;
7. adapt Phase 3's per-program state into `ProgramStateBundle`; and
8. validate routing and attribute requirements against the supplied capability profile.

Consumers never read a source from `SourceCatalog` and parse it themselves. An unavailable
materialization makes that program `Failed` and eligible for fallback.

GL compilation is render-thread-only and uses this state machine:

```text
PLANNED
  → create program
  → for each available vsh/gsh/fsh in deterministic VERTEX, GEOMETRY, FRAGMENT order:
      create shader → compile
      failure: record + delete all created shaders/program → FAILED
      success: attach
  → bind each DECLARED extended attribute at 10/11/12
  → link
  → validate
  → delete shader handles (program retains linked executable)
  → label program through DebugService
  → READY
```

Every exit uses a local ownership ledger. Shader handles are deleted whether compile, link,
validate, attribute binding, diagnostic reporting, or an unexpected backend exception fails.
Successful programs remain owned by the unpublished candidate until publication transfers
ownership to `CompiledProgramRegistry`. Candidate teardown is idempotent.

Compile/link/validate use Phase 1's never-throwing results and retain driver logs in
`ProgramBuildFailure`; the backend is still treated as untrusted at the facade boundary, so
runtime exceptions are converted to `UNEXPECTED_BACKEND_FAILURE` and cleanup runs.

No geometry source is required. A program with at least one source stage is attempted; the link
result decides whether that combination is executable. A program with no stages is `Missing`.

### 4.8 Dual-form geometry strategy

Core-form geometry source already containing valid layout qualifiers receives
`GeometryTranslationRequest.None` and compiles as supplied.

For Phase 3's recognized legacy pair, the Phase 4 strategy is deterministic:

```text
input primitive  = TRIANGLES
output primitive = TRIANGLE_STRIP
max vertices     = the positive Phase 3 maxVerticesOut value
```

The topology is behavioral observation from
`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:151`–`:152`, not copied
implementation structure. The pack-author contract supplies the extension and maximum declaration
at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:349`–`:356`.

Phase 3's current §5 contract accepts this plan and rewrites only the attributed extension and
`maxVerticesOut` spans to core layout declarations. That is enough for configuration, but its
published guarantee does **not** translate extension-era built-ins such as geometry input arrays,
nor does it guarantee a GLSL version in which core geometry syntax is legal. Phase 1 simultaneously
publishes no pre-link program-parameter verb. Consequently this phase cannot honestly claim
semantic support for every legal ARB-form pack.

The architecture therefore has two strategy outcomes:

- `CoreLayout(plan)` only when Phase 3 can publish a materialized source whose declared
  compatibility proves that the complete source is core-geometry compatible and the profile is
  at least GL 3.2; and
- `LegacyArbRequired(plan)` otherwise, currently `Unavailable` because the binding facade has no
  operation that can express it.

§5 requests a Phase 3 compatibility result and, alternatively, a Phase 1 engine-level legacy
geometry configuration operation. Until one is verified, a legacy geometry program follows rung 3
and its backup chain. It is never passed to GL with a knowingly incomplete rewrite. This is a
reported upstream contradiction, not a phase-local decision to weaken the contract.

### 4.9 Draw routing, attributes, and state-bundle validation

Routing validation is deterministic:

1. preserve the declared order;
2. permit an explicit empty list for `N`/no attachments;
3. reject duplicate attachment indices as a Phase 3 invariant breach rather than silently
   deduplicating;
4. require every index to be in the Phase 3 resource domain for that buffer family;
5. require route length ≤ `maxDrawBuffers`;
6. require every routed color index < `maxColorAttachments`; and
7. keep `AllUsedBuffers` symbolic for Phase 5.

A route invalid for one program fails that program and permits fallback. A pack-wide estate that
cannot fit the capability profile is a Phase 5 capability-gate result, not re-decided here.

Attribute binding occurs only for attributes Phase 3 reports declared:

| Attribute | Location | Required `maxVertexAttribs` |
|---|---:|---:|
| `mc_Entity` | 10 | at least 11 |
| `mc_midTexCoord` | 11 | at least 12 |
| `at_tangent` | 12 | at least 13 |

Binding undeclared attributes is unnecessary and would reject a simple program on hardware that
cannot expose an unused high location. The numeric values never come from Pintonium. Its conflicting
bindings at `ProgramCreator.java:21`–`:25` are the negative test fixture.

`instanceCount` is always positive; absent means 1. It is carried for every slot, including
gbuffers/shadow. Phase 7 owns both the v0.5 composite/deferred loop and the still-open
non-composite re-render case already handed off by Phase 1.

Pintonium's per-buffer blend override is not adopted: App F.7 and Phase 3 §5 publish one
program-level `BlendSpec`, and Phase 1 has no indexed blend-state verb. The slot shape reserves no
unfillable field. A future Iris contract extension can add a distinct optional collection with
its own capability and owner.

### 4.10 The use-program state barrier

The barrier API is:

```java
public record UseProgramRequest(
    ProgramSlotId requested,
    BarrierContext context) {}

public record BarrierContext(
    boolean shadowPass,
    StageId stage,
    StageBand band,
    long frameId) {}

public interface ProgramBindingParticipant {
    BarrierParticipantResult afterBind(
        ResolvedProgramBinding binding,
        BarrierContext context);
}

public interface ProgramStateBarrier {
    BarrierResult activate(UseProgramRequest request);
    BarrierResult releaseToFixedFunction(BarrierContext context);
}
```

Phase 6 supplies three participants in fixed order:

1. `SamplerRepointParticipant`;
2. `BuiltInUniformRefreshParticipant`; and
3. `CustomUniformRefreshParticipant` (fed by Phase 11 when available).

Activation order is normative:

1. if `shadowPass`, replace the requested slot with `shadow` before resolution;
2. resolve the effective binding, including a fixed/skip terminal;
3. restore the previous alpha/blend lock before changing the program;
4. bind the effective program through `ShaderService.use`, or bind fixed function through the
   requested Phase 1 addition;
5. for a `ShaderProgram` binding, invoke sampler, built-in, then custom participants; a fixed
   binding has no program locations and skips all three;
6. snapshot the underlying alpha/blend aspects and apply the effective provider's explicit
   overrides through `StateService`; and
7. publish the active logical/effective pair for diagnostics.

Participants run on every successful `activate`, even when the same handle remains active.
World state, per-draw values, texture sides, and custom expressions may have changed. An equality
fast path may skip only the actual `use` call if Phase 6 explicitly proves that doing so preserves
its refresh contract; v0.1 does not take that optimization.

`ProgramBindingParticipant.afterBind` has the stated precondition that
`binding.effective()` is `CompiledProgramBinding.ShaderProgram`; the barrier enforces it before
dispatch so participants never branch on or invent fixed-function behavior.

An absent alpha/blend override means “do not lock that aspect.” Explicit `OFF` means lock it
disabled. A transition restores exactly the snapshot taken before the prior lock; it never
snapshots the prior override as the new underlying state. `releaseToFixedFunction` restores any
lock before binding fixed function.

The order is a contract exposed to Phases 6–8. It adopts the shape validated by
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/Program.java:30-34]`
after checking RESEARCH §4.2 (`D-P4-5`). Pintonium's compute memory barrier and image refresh are
not invoked at v0.1; their slots remain G8/S2.

### 4.11 Publication, reload generation, and cache keys

`RegistryFingerprint` hashes:

- Phase 3 schema version and configuration fingerprint;
- active dimension identity/mode;
- materialization fingerprints for every attempted stage;
- the Phase 6 macro contribution;
- canonical geometry strategies;
- capability fields that affect validation/build; and
- the registry schema version.

It does not hash handles, driver logs, object identity, generation, timestamps, or source text.

Publication is:

1. build a complete candidate;
2. if caller accepts it, restore/release the old barrier;
3. atomically replace `PublishedRegistry`;
4. increment generation once;
5. close the old registry on the render thread; and
6. notify no cache directly—consumers poll equality.

The publisher also accepts `ShadersOff`, represented by an empty registry and a new generation.
This makes “off” observable to every cache. A failed candidate may leave the prior publication
active until Phase 7/12 chooses old-registry retention or shaders-off; that user-facing policy is
not Phase 4's.

Generation adoption is structurally cross-checked against
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/compat/sodium/impl/shader_overrides/IrisChunkProgramOverrides.java:136-139]`,
where inequality deletes cached shaders. No Pintonium dimension cache or renderer coupling is
inherited.

### 4.12 Diagnostics

`ProgramBuildFailure` contains:

- requested slot and source stem;
- failure stage (`MATERIALIZE`, `CAPABILITY`, `COMPILE`, `ATTRIBUTE_BIND`, `LINK`,
  `VALIDATE`, `BARRIER`, `UNEXPECTED_BACKEND`);
- shader stage when applicable;
- sanitized driver log;
- source-map diagnostic IDs, not pack source text;
- fallback path and final disposition; and
- a stable diagnostic ID.

The detailed log goes to `schmaloogium.compile`. A concise error goes through
`DiagnosticReporter` to the shader GUI/user channel. Debug saved sources remain Phase 3's opt-in
local artifact and never enter this diagnostic.

## 5. Cross-phase interfaces

### 5.1 Exposed interfaces and data contracts

| Exposed contract | Exact content | Consumer(s) |
|---|---|---|
| `StageRegistry`, `StageId`, `StageBand`, `StageStep`, `PassIndex` | immutable modern-shape and G6 configurations; sparse 0…99 families; duplicated gbuffers occurrence | Phases 5, 7, 8; G8/S1/S2 |
| `PassDescriptor`, `PassResourceAccess`, `ComputeDispatchSlot` | stage-readable domain, exact/symbolic writes, flip config, mipmap set, dormant primary + a…z compute companions | Phases 5, 7; G8/S2 |
| `ProgramSlotId`, `ProgramSlotDescriptor`, `ProgramStateBundle` | exact pack-facing name, stage/bands, fallback, routing, mipmaps, instance count, attributes, alpha/blend, scale, flips, geometry | Phases 5, 6, 7, 8, 10 |
| `CompiledProgramRegistry.resolve` / `ResolvedProgramBinding` | requested/effective identity, entire provider state, fallback path; no mutable handle exposure beyond the binding | Phases 5, 6, 7, 8 |
| `ProgramStateBarrier.activate/releaseToFixedFunction` | ordered shadow override → resolve → restore → bind → Phase 6 participants → alpha/blend lock | Phases 6, 7, 8 |
| `ProgramBindingParticipant` and result contract | never-throwing post-bind contribution point; fixed sampler/built-in/custom order; participant owns its rung-1/2 isolation | Phase 6, then Phase 11 through Phase 6 |
| `PublishedRegistry.generation` | changes once per accepted registry/off publication; consumers compare for inequality | Phase 12 reload paths and every derived program/uniform cache |
| `RegistryFingerprint` | deterministic derivation identity distinct from generation | Phases 5, 6, 7, 12 |
| `ProgramBuildFailure` | sanitized, source-attributed program failure and fallback disposition | Phase 7 activation; Phase 12 shader GUI |
| Fixed attribute table | `mc_Entity=10`, `mc_midTexCoord=11`, `at_tangent=12` | Phase 10 |
| Per-slot `instanceCount` | positive count retained for all programs; no execution semantics hidden here | Phase 7 executes; Phase 6 uploads `instanceId` |

Phase 5 must not infer a resolved ping-pong side from `explicitFlips`; it owns the side state.
Phase 6 must not bypass the barrier to refresh a program. Phase 7 must not re-resolve backup chains
or overlay requested-slot state on the effective provider. Phase 10 must not renumber attributes.

### 5.2 Consumed Phase 1 contracts

| Phase 1 §5 contract | Use here |
|---|---|
| `:engine` layout, package rules, C-1…C-4 seam | all registry/compiler types and tests |
| `GLDevice`/seven services | `ShaderService`, `StateService`, and no-op `DebugService` call sites only |
| `GLCapabilityProfile` + serialization | route/attribute/geometry validation and recorded-profile tests |
| Opaque `ProgramHandle`/`ShaderHandle` lifetime | candidate ownership ledger and registry teardown |
| `CompileResult` / `LinkResult` / `ValidateResult` | never-throwing build state machine |
| `RecordingGLDevice`, `ScriptedResponses`, `GLCallLog` | headless compile/failure/barrier tests |
| `ReplayAssertions` | call order, no leaks, no use-after-delete |
| `StateService.snapshot/restore`, `alphaTest`, `blend` | per-program lock |
| `DiagnosticReporter` and fixed channels | sanitized program/user errors on `.compile` and `.gl` |
| SPDX/`THIRD-PARTY.md` mechanism | any future LGPL-derived implementation, though this design copies no source |

Phase 1 exposes no fixed-function selection operation. That missing contract is requested in
§5.4; this phase does not assume that `use(null)`, a magic handle, or integer zero is legal.

### 5.3 Consumed Phase 3 contracts

| Phase 3 §5 contract | Use here |
|---|---|
| `PackConfiguration`, schema/fingerprint discipline | sole registry-build truth and cache key |
| `DimensionConfiguration` | exact no-base-merge source selection or disabled outcome |
| `SourceCatalog`, `SourceKey`, `SourceMaterializer`, `MaterializedSource`, `SourceMap` | source planning/materialization without reopening or rescanning |
| `GeometryTranslationRequest` / `GeometryTranslationPlan` | legacy configuration rewrite request |
| singular `MacroContribution` | Phase 6 contribution passed unchanged to every materialization |
| `OptionConfiguration` / immutable `OptionState` | exact materialization option state |
| `ProgramStateModel` | alpha/blend/scale/flip/enabled/profile-disabled |
| `ResourceRequirements` | routing, mipmaps, instance count, attributes, legacy geometry and allocation bounds needed for validation |
| materialization/configuration fingerprints | derivation identity and cache reuse |
| diagnostics | source-attributed unavailable/failure conversion |

Phase 4 never reopens the pack, rescans a directive, interprets properties, or bypasses the
materializer.

### 5.4 Requested changes to dependency contracts

Two changes are required before a fully contract-faithful implementation can close:

1. **Phase 1 — fixed-function selection.** Add an engine-level
   `ShaderService.useFixedFunction()` (name refinable, semantics not) that binds program zero
   through the backend without exposing a raw integer. It is required by App A.1's `<none>`,
   missing-root, missing-shadow, and missing-final passthrough dispositions and by barrier release.
   Add the call to `RecordingGLDevice` and replay assertions. Until verified, those terminal
   actions remain uncallable rather than being silently encoded as `use(null)`.
2. **Phase 3 or Phase 1 — complete legacy geometry.** Preferred: Phase 3 publishes whether its
   complete materialized legacy source is core-geometry compatible and, if it claims translation,
   translates every required extension semantic under a source-map/fingerprint contract.
   Alternative: Phase 1 adds an engine-enum pre-link legacy geometry configuration operation
   (triangles, triangle-strip, positive max vertices), whose LWJGL backend may map to the extension
   without exposing ARB constants. The current two-span rewrite plus no pre-link operation is not
   enough to claim the published dual-form contract.

These are requests, not assumed APIs. They require the dependency fix-up/re-verification route
because both change binding §5 surfaces.

### 5.5 Design-graph note

The Phase 4 assignment requires the generation for Phase 12, while §G5.1 does not list Phase 4 as
a Phase 12 dependency. This document exposes the interface as assigned and requests a future
DESIGN correction in §11; it does not edit the graph.

## 6. Failure modes & degradation

| G2.4 rung | Phase 4 case | Required response |
|---|---|---|
| 1 | custom-uniform participant reports one expression/uniform failure | Phase 11/6 disables only that custom uniform; barrier continues |
| 2 | built-in uniform participant reports one upload failure | Phase 6 disables only that built-in uniform under Phase 1's replay protocol; program remains active |
| 2a | alpha/blend override application or optional debug label fails | restore the saved state, disable only that override/feature for this registry generation, diagnose, keep the program |
| 3 | materialize/compile/link/validate/attribute/geometry failure for one program | delete candidate objects for that program, emit user-visible error, mark it failed, resolve the entire binding through its backup chain |
| 3 | absent deferred/composite indexed pass | skip it; absence is normal, not an error |
| 3 | unavailable root with a fixed terminal | select fixed function only after Phase 1 exposes the operation |
| 4 | registry-wide capability failure—e.g. required estate cannot fit, required high attribute has no fallback, or every required terminal is unavailable | return `ShadersOff`, chat error through caller, do not publish partial registry |
| 5 | unexpected exception, cleanup failure, stale/use-after-delete invariant, or barrier cannot restore safe state | catch at public boundary, close all owned candidate objects, request shaders-off publication, leave/restore vanilla framebuffer path; never crash client |

Compilation failure never substitutes a pack-layout heuristic or Pintonium-style generated
lighting guess. A generated passthrough is legal only when it is a deterministic, explicitly owned
internal program with contract-defined use; Phase 7 owns that internal content.

Closing a registry is idempotent. A second close warns in debug but issues no second delete.
Using it after close is a programming error caught by recording tests and converted to shaders-off
at the public barrier.

## 7. Threading & performance notes

### 7.1 Thread ownership

- Catalog construction, fallback-graph validation, source-key planning, Phase 3 materialization,
  state adaptation, and fingerprints are pure and may run off-thread on immutable inputs.
- Every `GLDevice` call, candidate publication, registry close, and barrier call runs on the render
  thread.
- The v0.1 compiler is synchronous. Phase 14 may move the compile portion to a shared context only
  behind OQ-15's successful spike and mandatory synchronous fallback; publication and vanilla-state
  interaction remain render-thread work.
- `PublishedRegistry` is an immutable snapshot. The publisher may expose it safely to readers, but
  no off-thread reader may dereference a GL handle or call the barrier.

### 7.2 Allocation and hot paths

Compilation is cold and may allocate readable immutable plans and diagnostics. The barrier is hot:

- slot lookup is by stable catalog ordinal plus pass-family index, not a per-switch string map;
- resolved backup results are memoized in the immutable registry;
- participant list and effective state bundle are immutable arrays/records;
- no stream, list copy, boxing, formatted log message, or source-map work occurs on a clean
  activation;
- the active lock stores at most one state snapshot and logical/effective pair; and
- generation polling is one primitive equality comparison.

No global catalog- or pass-family-sized sweep runs on every program switch. Compile iterates
configured slots; activation touches one resolved binding.

### 7.3 Driver interaction

Attribute binds occur only for declared attributes. Shader stages are created in deterministic
order for stable recorded logs. Phase 1's backend obligation to route cached state through
`GlStateManager` applies to alpha/blend restoration and texture binding performed by Phase 6; this
phase never introduces raw LWJGL calls.

## 8. Testability plan

### 8.1 Pure registry tests

- `classicSchedule_hasFiveIdentitiesAndTwoGbuffersOccurrences`
- `modernSchedule_instantiatesAllNineIdentitiesWithoutStructuralChange`
- `sparseFamilies_acceptZeroNinetyNineAndHoles`
- `g6DeferredComposite_populateThroughFifteenWithoutSixteenSizedType`
- `computeSlots_primaryAndAThroughZ_areDormant`
- `stageAccess_neverStoresMainOrAltTextureSide`
- `classicCatalog_mapsEveryAppendixA1Row`
- `classicCatalog_declaredAndEnumeratedCountsAgreeAtSixty`
- `fallback_exactAppendixAEdges`
- `fallback_missingDisabledAndFailedUseNearestAncestor`
- `fallback_inheritsProviderStateWithoutChildOverlay`
- `fallback_shadowRootFixedButChildrenMayUseShadow`
- `fallback_cycleRejectedAtCatalogConstruction`
- `virtualPre_neverCompilesOrProvidesFallback`
- `programEnabledFalseAndProfileDisableAreEquivalentToAbsent`
- `drawRouting_preservesOrderEmptyAndAllUsedSymbol`
- `drawRouting_rejectsDuplicateOutOfDomainAndCapabilityOverflow`
- `attributeLocations_areExactlyTenElevenTwelve`
- `attributeCapability_checkedOnlyWhenDeclared`
- `instanceCount_retainedForEveryProgramFamily`
- `fingerprint_equalInputsEqualAndGenerationExcluded`

The cardinality test proves every table row exists and the corrected declared count agrees with the
enumerated count; runtime behavior still depends on the rows and selected slot, not either count.

### 8.2 Geometry tests

- `geometry_coreLayoutUsesNone`
- `geometry_legacyPlanIsTrianglesTriangleStripAndExactMax`
- `geometry_legacyNoneRejectedByPhase3Materializer`
- `geometry_corePlanRequiresGL32AndCompatibilityProof`
- `geometry_incompleteTwoSpanRewriteIsUnavailableNotCompiled`
- future dependency-fix test:
  `geometry_legacyBuiltinsCompileThroughVerifiedStrategy`

Fixtures contain minimal original test shaders written for this project, never matrix-pack source
or copied reference code.

### 8.3 Recorded-GL compiler tests

Against Phase 1 `RecordingGLDevice` and recorded `GLCapabilityProfile`s:

- successful V/G/F flow records create, compile, attach, 10/11/12 binds as declared, link,
  validate, shader deletes, and program label in order;
- compile failure deletes every created handle and never links;
- link failure deletes shaders/program and resolves fallback;
- validate failure deletes the program and resolves fallback;
- missing fragment or geometry stage is not invented; scripted link result controls outcome;
- disabled/missing slots emit no GL call;
- `ReplayAssertions.noLeakedObjects()` holds after candidate failure and registry close;
- `noUseAfterDelete()` holds across reload publication;
- negative Pintonium fixture asserts locations 13/14 and double-bound 11 never appear; and
- source/driver logs are sanitized and pack source is absent from rendered call logs.

### 8.4 Barrier and generation tests

- `barrier_order_shadowResolveRestoreBindSamplerBuiltinCustomLock`
- `barrier_shadowPassForcesShadowBeforeFallback`
- `barrier_sameHandleStillRefreshesParticipants`
- `barrier_requestedChildUsesEntireProviderState`
- `barrier_absentOverrideDoesNotLock_explicitOffDoes`
- `barrier_transitionRestoresUnderlyingStateNotPriorOverride`
- `barrier_participantFailureIsIsolatedByOwner`
- `barrier_fixedTerminalRequiresPublishedFacadeOperation`
- `publication_acceptIncrementsOnce_failureDoesNot`
- `publication_shadersOffInvalidatesEveryPriorGeneration`
- `cache_pollInvalidatesOnInequality`
- `registryClose_isIdempotent`

### 8.5 Conformance tiers and fixture policy

- **T0:** all three classic matrix packs and then dual-spec packs materialize their active G6
  program sets; each requested slot reaches linked, fallback, skip, or fixed terminal with no
  unmapped directive.
- **T1:** Phase 7 scenes exercise real slot transitions, fallback paths, deferred/composite holes,
  and shadow override without a crash or state leak.
- **T2:** classic-pack image parity detects wrong fallback state, routing, attribute numbering, or
  barrier ordering.
- **T3:** pack-feature manifests confirm every shipped source stem and program state has a recorded
  disposition.

Matrix packs are downloaded/cached under Phase 2 policy and never committed or re-hosted. No source
text or rendered image becomes a repository golden. Committed oracles are manifests/hashes only;
updates require explicit `-PupdateGoldens` and still fail the updating run.

The Phase 4 implementation gate is the assigned recorded-GL compile of a classic pack's program
set to linked/resolved state with zero unmapped directives, plus no leaked/use-after-delete handles.

## 9. Milestone staging

| Component | Milestone | Architected now / implementation boundary |
|---|---|---|
| Nine-identity stage model, multi-occurrence schedule, `PassIndex` 0…99 | `v0.1` | implemented and tested now |
| Classic G6 configuration and full App A catalog | `v0.1` | implemented now; row coverage is cardinality-independent |
| Sparse modern configuration fixture with dormant families | `v0.1` | implemented as data/test, not executed |
| Program-state adapter, route/attribute validation, fallback resolver | `v0.1` | implemented now |
| Synchronous materialize/compile/link/validate transaction | `v0.1` | implemented after §5.4 dependency gaps close |
| Barrier, alpha/blend lock, fixed Phase 6 participant slots | `v0.1` | interface and P4 mechanics now; P6 implementations later |
| Shadow force-selection branch | `v0.1` | interface/mechanics now; Phase 8 invokes at `v0.2` |
| Pipeline generation and fingerprints | `v0.1` | implemented now; Phase 12 consumes at `v0.4` |
| `instanceCount` storage/exposure | `v0.1` | stored now; Phase 7 composite execution at `v0.5`, non-composite case remains its handoff |
| Debug labels at creation sites | `v0.1` | calls exist; Phase 14 activates backend at `v0.5` |
| `shadowcomp`, `prepare`, `begin`, setup population | `post-v0.5` | no type change, G8/S1 data/wiring |
| Compute compile/dispatch, `_a`…`_z`, images/SSBO/barriers | `post-v0.5` | slots only now; G8/S2 owns semantics |
| Arrays/buffers beyond current Phase 3 limits | `post-v0.5` | registry already unbounded to 99/by `BufferRef`; front-end/estate growth later |
| Shared-context async compiler | `v0.5` | Phase 14/OQ-15, synchronous fallback retained |

## 10. OQ & spike specifications

Phase 4 has no assigned open question in §G10 or its phase specification. The catalog cardinality
is resolved upstream; fixed-function facade and legacy-geometry contract gaps still require
governed dependency corrections through §G1.3, recorded in §11.

Phase 14's OQ-15 may later change compiler threading but not registry, fallback, barrier, or
publication semantics.

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale / contract check |
|---|---|---|
| D-P4-1 | Separate `StageId` from ordered `StageBand` occurrences | G6 and modern orders contain gbuffers on both sides of deferred; enum ordinal cannot model that without duplicate identities |
| D-P4-2 | Build the classic catalog from every explicit App A.1/pack-author row, never a fixed-size array | Concrete `[V:doc]` names are contract-visible; the corrected 60-row summary is a test oracle, not an allocation constant |
| D-P4-3 | Sparse pass families use a validated 0…99 key space and configuration population | Satisfies D-4 without hardcoded 16/8 while keeping compact immutable implementations possible |
| D-P4-4 | Adopt recursive memoized backup resolution, but resolve to the ancestor's whole immutable binding | Contract check: every App A.1 edge is explicit, App A.2 requires entire configuration, disabled programs are absent, and shadow root has no parent. Pintonium supplies working structural evidence, not values |
| D-P4-5 | Adopt one ordered program-use barrier with fixed participant slots | Contract check: RESEARCH §4.2 requires sampler re-point, built-in refresh, custom evaluation, alpha/blend lock, and shadow override. Pintonium lines 28–34 validate the bind/refresh shape |
| D-P4-6 | Bind only declared `mc_Entity`/`mc_midTexCoord`/`at_tangent` at 10/11/12; reject Pintonium numbering | App A.3 is contract; Pintonium's 11–14 layout depends on owning a replacement chunk VAO |
| D-P4-7 | Legacy observed topology is triangles → triangle strip with Phase 3's positive `maxVerticesOut` | This restates behavioral digest evidence only; it does not claim the current two-span rewrite is semantically complete |
| D-P4-8 | Publish an equality-polled long generation on every accepted registry or off replacement | Makes reload invalidation explicit and adopts Pintonium's working counter shape without its renderer coupling |
| D-P4-9 | Compile into an unpublished ownership-ledger candidate and transfer ownership only on publication | Guarantees no partial registry or handle leak across any failure |
| D-P4-10 | Keep fixed stage-readable sets in Phase 4 but resolved ping-pong side/FBO in Phase 5 | Satisfies D-4's read/write/flip shape without stealing buffer-estate lifecycle |
| D-P4-11 | Do not adopt Pintonium per-buffer blending or compute execution at v0.1 | Neither is a current Phase 3/App F.7 input; compute is explicitly G8/S2 |
| D-P4-12 | A barrier refreshes participants even when the same effective handle remains bound | Uniforms, sampler sides, and custom values can change independently of program identity |
| D-P4-13 | Fixed-function terminals remain explicit variants and never magic/null handles | Phase 1's opaque-handle seam forbids smuggling program zero through an integer or null convention |

### 11.2 D-1…D-10 disposition

| Decision | Disposition |
|---|---|
| D-1 Cleanroom-exclusive | No other loader path; no loader type enters engine |
| D-2 shaders only | Registry/compiler only; no performance renderer or unrelated feature |
| D-3 fixed pack matrix | T0–T3 tests name the matrix and contract rows |
| D-4 modern stage registry from day one | Central architecture invariant; both configurations shown |
| D-5 Mixin hooks only | No hook authored; Phase 7/8 call interfaces from dumb mixins/glue |
| D-6 hard seam/headless | all policy in pure `engine.registry`; GL only through facade/recorder |
| D-7 GPL-3.0-or-later | honored |
| D-8 legal reuse | LGPL evidence cited; AGPL/OF restrictions honored |
| D-9 compatibility profile | fixed-function terminal and GLSL-era behavior retained; no core-profile rewrite |
| D-10 conformance week one | pure and recorded-GL tests plus tier handoffs specified |

### 11.3 Input contradictions and rulings

1. **RESOLVED UPSTREAM — 43 versus 60 explicit slots.** §4.5 preserves all 60 contract rows and
   cardinality-independent behavior; RESEARCH and RC3 now remove the erroneous 43 summary.
2. **Fixed-function behavior versus facade.** App A requires no-program/fixed/passthrough
   terminals; Phase 1 exposes `use(ProgramHandle)` only. Ruling: request an opaque engine verb,
   assume no null/magic handle.
3. **Dual-form geometry versus current handoff.** The assigned design requires internal
   translation; Phase 3 exposes only a two-span configuration rewrite and Phase 1 exposes no
   legacy parameter operation. Ruling: never claim incomplete translation; request a verified
   complete path.
4. **Pintonium per-pass inventory versus current contract.** PD §3.3 includes framebuffer,
   resolved flip snapshot, per-buffer blend, and compute companions. Phase 3/App F.7 publish only
   the current global program state. Ruling: use the inventory as a cross-check and assign/reject
   fields according to current contracts, not by imitation.
5. **Phase 12 consumption versus dependency graph.** The Phase 4 assignment says Phase 12 depends
   on generation; §G5.1 omits Phase 4 from Phase 12 dependencies. Ruling: expose as assigned and
   request graph correction.

### 11.4 Hand-offs and open items

- **Phase 5:** resolve symbolic `AllUsedBuffers`, realize FBO routing and ping-pong sides, apply
  virtual-pre flips, and never store foreign/FBO handles in Phase 4 state.
- **Phase 6:** implement the three ordered participants, preserve the “every activation refreshes”
  rule, and surface per-uniform isolation results without throwing.
- **Phase 7:** map hook phases to exact logical slots and gbuffers bands; execute pass arrays,
  scale, mipmaps, fixed/passthrough terminals, and both `countInstances` cases; choose old-registry
  retention versus shaders-off on failed reload.
- **Phase 8:** set `shadowPass=true` and rely on force selection; do not duplicate it in hooks.
- **Phase 10:** configure vertex sources/pointers at the fixed locations; no renumbering.
- **Phase 12:** poll generation inequality and discard every derived program/uniform/UI compile
  diagnostic cache on change.
- **G8/S1:** populate dormant stage families without changing registry structure.
- **G8/S2:** define compute sources, work groups, access/barrier sets, images/SSBOs, and execution;
  do not infer them from the placeholders.
- **Phase 14:** async compilation may implement the same compiler contract after OQ-15; it may not
  weaken synchronous fallback or render-thread publication.

### 11.5 Requested upstream changes

- **GRANTED — 2026-07-28 maintainer correction.** `docs/research/v1/RESEARCH.md` now states the
  60-row Appendix A.1 count, and `docs/design/v2.0-RC3/DESIGN.md` now uses cardinality-neutral
  Phase 4 wording. Every named row remains; §4.5 and §8 retain equality/coverage tests without
  making the corrected count behavioral.
- Apply and re-verify the Phase 1 fixed-function facade request in §5.4.
- Apply and re-verify one complete legacy-geometry path from §5.4; update Phase 3/Phase 1
  interfaces consistently.
- Add Phase 4 to Phase 12's declared dependency list, or state the generation is consumed
  indirectly through a declared Phase 7 interface.
- Add a future `verification/targets/phase-4.json` profile anchored to RC3 before the separate
  verify session. No Phase 4 target existed at build-session start, and creating verification
  harness configuration is outside this document-only deliverable.

## 12. Implementation checklist

1. **[v0.1]** Create pure stage identity/band/index/access types; test bounds and immutability.
2. **[v0.1]** Implement the G6 and full-superset configuration constructors; run all
   `classicSchedule_*`, `modernSchedule_*`, and sparse-family tests.
3. **[v0.1]** Encode every Appendix A.1 row declaratively, including virtuals and `<none>`;
   run row-coverage and declared-versus-enumerated equality tests without fixed-size allocation.
4. **[v0.1]** Implement catalog validation and memoized backup resolution; run every
   `fallback_*` and `virtualPre_*` test.
5. **[v0.1]** Implement the Phase 3 adapter for program state, routing, attributes, instance count,
   source keys, and fingerprints; prohibit rescanning/reopening by package/API tests.
6. **[v0.1]** Resolve the Phase 1 fixed-function request and add recorder support before
   implementing terminal actions.
7. **[v0.1]** Resolve the legacy-geometry dependency request; implement only the verified strategy
   and all `geometry_*` tests.
8. **[v0.1]** Implement pure build planning/materialization and deterministic V/G/F ordering;
   test dimension no-merge and unavailable-source fallback.
9. **[v0.1]** Implement render-thread compiler ownership ledger through Phase 1 `ShaderService`;
   script compile/link/validate failures and prove no leak/use-after-delete.
10. **[v0.1]** Bind only declared extended attributes at 10/11/12 with exact capability gates;
    run negative Pintonium-numbering test.
11. **[v0.1]** Implement immutable compiled registry, idempotent close, fingerprints, and
    transaction-only publication.
12. **[v0.1]** Implement generation publication for registry and shaders-off replacements; run
    cache inequality tests.
13. **[v0.1]** Implement barrier selection/restore/bind/participant/lock order with no-op
    participants; run recorded state tests.
14. **[v0.1]** Expose Phase 6 participant installation without global mutable pack state; add
    never-throw result and ordering tests.
15. **[v0.1]** Add `ProgramBuildFailure` diagnostics to `.compile` and shader-GUI channels,
    sanitizing source and driver data.
16. **[v0.1]** Add dormant compute companion descriptors and prove no compile/dispatch GL calls.
17. **[v0.1]** Run the recorded-GL classic program-set implementation gate with a downloaded/local
    fixture under Phase 2 policy; require zero unmapped directives and clean replay assertions.
18. **[v0.2]** Integrate Phase 8 shadow invocation solely through barrier context and test forced
    selection/fallback.
19. **[v0.5]** Let Phase 7 execute composite/deferred `instanceCount` loops with Phase 6
    `instanceId`; retain the separately named non-composite case.
20. **[post-v0.5]** Populate S1 stage families as data only after their governing design exists;
    rerun the unchanged-structure tests.
21. **[post-v0.5]** Implement S2 compute/SSBO/image/barrier semantics behind the reserved slots;
    do not promote placeholders into behavior without the new contract and capability gates.
