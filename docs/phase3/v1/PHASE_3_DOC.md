# Schmaloogium — Phase 3: Pack front-end — Architecture

## 0. Header

**Phase:** 3 — Pack front-end: ingestion, preprocessing, and configuration model
**Date:** 2026-08-03 · **Last revised:** 2026-08-03 (§0.25)
**Governing design:** `docs/design/v2.0-RC3/DESIGN.md`, Part I §G0–§G12 and the Phase 3
specification only. RC3 governs this phase only; this document does not change the Phase 1 or
Phase 2 governance pins.

### 0.1 Inputs actually read

Read in the assigned order:

1. `docs/design/v2.0-RC3/DESIGN.md` Part I §G0–§G12 (lines 92–1109) and the Phase 3
   specification (lines 1316–1470).
2. `docs/research/v1/RESEARCH.md` §0, §1, §3.1–§3.3, §3.5, §3.7, §4.1 steps 2–3,
   §4.7, §7.5, §11 row OQ-7, Appendix A.3, Appendix F in full, and Appendix H.
3. `docs/phase1/v14/PHASE_1_DOC.md` §5, then the named supporting material needed here:
   §2.1–§2.4 for module/package placement and §4.9 for logging, diagnostics, and debug flags.
   No Phase 1 review was needed: the assigned dependency status says
   `docs/phase1/reviews/PHASE_1_REVIEW_15.md` is literal PASS.
4. `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §7, §12, and §17 rows B1–B3/B12.
   The assigned Pintonium package was surveyed with the §G11 exclusions. Load-bearing source
   reads were:
   - `shaderpack/ShaderPack.java`
   - `shaderpack/LanguageMap.java`
   - `shaderpack/IdMap.java`
   - `shaderpack/include/{AbsolutePackPath,IncludeGraph,IncludeProcessor}.java`
   - `shaderpack/preprocessor/{JcppProcessor,PropertiesPreprocessor}.java`
   - `shaderpack/option/{OptionAnnotatedSource,ShaderPackOptions,ProfileSet}.java`
   - `shaderpack/parsing/DispatchingDirectiveHolder.java`
   - `shaderpack/properties/{PackDirectives,ProgramDirectives,ShaderProperties}.java`
5. `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties` in full, followed by only
   the directive, ID-mapping, Standard Macros, and Options portions of
   `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`.
6. `docs/reference/oculus/v1.0/OCULUS_DESIGN.md` §§3–4 and §§12–17.

No input outside that assignment was read. In particular, no session transcript, no path under a
`docs/**/chatlogs/` directory, no root-level `*.txt`, no implementation/decompile material, and no
`SHADER_ENGINE_IMPL.md` was read. The Pintonium search excluded the stale refactor artifact and the
§G11 prohibited transformation boundaries. Oculus was consumed through its gated report only; no
Gate-dropped material is used or reconstructed here.

### 0.2 Dependency PHASE docs consumed

`docs/phase1/v14/PHASE_1_DOC.md` is the sole dependency contract. This design consumes only what
its §5 exposes: the three-module layout and seam constraints, `GLCapabilityProfile`, the fixed
logging and loader-neutral diagnostic interfaces, the debug-flag namespace, the SPDX/third-party
notice mechanism, and the `:conformance` extension point. A missing build-contract item is
requested in §5.4 rather than assumed.

### 0.3 Legal and provenance posture

- `jcpp` is adopted as an Apache-2.0 dependency; its notice belongs in Phase 1's
  `THIRD-PARTY.md` mechanism.
- Pintonium is LGPL-3.0 structure evidence. Every Pintonium-derived claim below carries a source
  path, and any future source incorporation must preserve notices and mark modifications.
- Oculus is LGPL-3.0-only loader-independent evidence. It supplies no 1.12.2 hook.
- No code or dependency from `glsl-transformation-lib` is permitted. No unverified stareval code
  is used. This phase performs no AST transformation.
- OptiFine's shipped `doc/` files are contract documents and are cited for semantics; no
  decompiled implementation structure is used.

### 0.4 Round 1 fix-up

The public configuration now exposes its schema version, and §3 maps the previously implicit
discovery, preprocessing, macro, source-attribution, and ID-mapping contract families.

### 0.5 Round 2 fix-up

The schema contract is executable, the internal-pack provider has a complete engine-only protocol,
and directive scanning enforces the shader-stage restrictions in RESEARCH Appendix A.3.

### 0.6 Round 3 fix-up

The load protocol, internal snapshot, macro-contributor publication, and profile inference are now complete.

### 0.7 Round 4 fix-up

Materialization and the reserved macro slot now have executable result shapes; load-result wording
and option rewrite/persistence traceability are consistent.

### 0.8 Round 5 fix-up

Legacy-geometry rewriting, precipitation ownership, and the exact screen widening boundary are explicit.

### 0.9 Round 6 fix-up

Geometry-plan execution, downstream-only test ownership, and texture-format validation are explicit.

### 0.10 Round 7 fix-up

Geometry-plan absence, closed-enum handling, and option-count screen widening are explicit.

### 0.11 Round 8 fix-up

Load-time analysis is plan-independent, and §5 now carries the executable screen-column semantics.

### 0.12 Round 9 fix-up

Screen-column resolution and the public load-request/result contracts are now executable.

### 0.13 Round 10 fix-up

Filesystem-pack discovery now has a public, immutable consumer contract.

### 0.14 Round 11 fix-up

Discovery-generation selection behavior now has explicit headless coverage and checklist hooks.

### 0.15 Round 12 fix-up

Discovery generations now use one executable host-directory identity rule.

### 0.16 Round 13 fix-up

Selection-first load validation now confines host-directory checks to filesystem selections.

### 0.17 Maintenance addendum (Phase 6 declared-uniform catalog — 2026-07-29)

Phase 6's verified dependency request identified that Phase 3 already recognizes uniform
declarations for resource requirements but does not publish the complete declaration metadata of
each final materialized source. Sections 2, 4, 5, 8, 11, and 12 now add an immutable,
source-attributed `DeclaredUniformCatalog` tied to the materialization fingerprint. Phase 4 may
merge those catalogs into a linked-program layout without reopening or rescanning source; the
catalog deliberately does not claim post-link GL activity.

**Current §G1.3 status:** round fourteen's literal PASS applies only to the pre-§0.17 bytes.
Rounds fifteen through eighteen required the §0.18 through §0.21 corrections. Phase 3 is therefore
**not verified** and is not a valid dependency input until a fresh verification round returns literal
PASS. The version directory remains `v1` while the loop is open.

### 0.18 Round 15 fix-up

Declared-uniform fingerprinting now hashes a schema-versioned canonical declaration payload that
excludes the fingerprint field, making the published equality acyclic and independently testable.

### 0.19 Round 16 fix-up

`ResourceRequirements` now has a closed immutable public algebra, deterministic key and collection
semantics, executable absence/default rules, and an explicit consumer projection map.

### 0.20 Round 17 fix-up

Phase 13 now explicitly consumes the generated-noise enablement and resolution.

### 0.21 Round 18 fix-up

The immutable program-state aggregate and its Phase 4/5 projections are now executable contracts.

### 0.22 Round 19 fix-up

Record-component changes now bump the configuration schema, and `RENDERTARGETS` work is post-v0.5.

### 0.23 Maintenance addendum (Phase 9 ID-mapping input — 2026-08-03)

Phase 9's R9-1 dependency request is applied across §§1–5, 8–9, 11, and 12. The former unresolved
list aggregate is replaced by schema-versioned `IdMappingInput`: it preserves `ABSENT` versus
`PRESENT_EMPTY` versus `PRESENT_RULES` independently for every mapping kind, classifies entry and
tag selectors, carries per-rule classic/modern era provenance, and includes both ordinary and
forced-`MC_VERSION=11300` entity parses. The same pure parser operation is exposed for bounded
Phase-9-provided mod bytes. Because the published `PackConfiguration` component meaning and ID
surface change, `PackFrontEnd.CURRENT_SCHEMA_VERSION` is incremented from 1 to 2.

**Current §G1.3 status:** round twenty's literal PASS applies only to the pre-§0.23 bytes. This
addendum changes §5, so Phase 3 is **not verified** and is not a valid dependency input until a
fresh verification round returns literal PASS. The version directory remains `v1` while the loop
is open.

### 0.24 Maintenance addendum (closed ID-mapping value types — 2026-08-03)

The schema-v2 publication now closes `PropertyPredicate` and `MappingOrigin`: predicates expose
their canonical property name and ordered accepted values, while origins distinguish the selected
pack from an ordered Phase-9 mod contribution and carry stable attribution identity. Matching,
validation, equality, and construction semantics are fixed in §§2.2, 4.9, 5.1, and 8.

**Historical status:** review round 22 subsequently returned literal **PASS** with zero findings
(`docs/phase3/reviews/PHASE_3_REVIEW_22.md`). The amendment below supersedes that verified surface.

### 0.25 Downstream-request addendum (canonical normalized-pack-path projection — 2026-08-03)

Phase 7 R7-9 requires a stable, slash-separated string for internal-pack hashing and serialization.
The previously named but undeclared `NormalizedPackPath` now exposes exactly
`canonicalString()`: an NFC, root-relative, non-empty path using `/` separators and no ambiguous
segments. Consumers hash that projection's UTF-8 bytes and never `toString()` or a host `Path`.
`[D-P3-24]` records the decision.

This closes an existing value type rather than adding or changing a `PackConfiguration` record
component, so `PackFrontEnd.CURRENT_SCHEMA_VERSION` remains 2. Because binding §5 changes, however,
Phase 3 v1 is **not verified** after round 22's historical PASS; the directory remains `v1` pending
a fresh whole-document review.

## 1. Scope & boundaries

### 1.1 What Phase 3 owns

Phase 3 owns the complete pure-JVM path from a selected pack location to one immutable, validated
`PackConfiguration`:

- deterministic discovery of `OFF`, `(internal)`, folder, and zip candidates;
- safe pack-root resolution, nested-root tolerance, archive lifetime, and dimension source sets;
- source identities, include graphs, include expansion, cycle/depth handling, and `#line`
  attribution;
- source option discovery, decoration, persisted values, profile/screen models, and source-line
  option rewriting;
- the configurable standard/identity macro environment and the reserved Phase 6
  `centerDepthSmooth` macro contribution point;
- jcpp preprocessing for shader sources and a separate properties-safe jcpp adapter;
- complete default-block uniform declaration capture from each final materialized shader, with a
  closed structural GLSL type, declaring source stage, attributed source location, and the exact
  materialization fingerprint;
- all Appendix A.3 directive recognition and aggregation into requirements/configuration data;
- the complete Appendix F `shaders.properties` model;
- schema-versioned unresolved ID-mapping inputs, per-file presence, entry/tag and era provenance,
  ordinary/forced-11300 entity parses, and layer rules;
- per-pack/global persistence formats;
- processed-source debug dumping; and
- validation, diagnostics, and the atomic publication of `PackConfiguration`.

All production types are in `:engine`, under `com.schmaloogium.engine.pack`,
`.preprocess`, and `.config`. They use only JDK, Phase 1 `:engine` interfaces, and the Apache-2.0
jcpp library. They contain no Minecraft, Forge, Cleanroom, Mixin, or LWJGL type.

### 1.2 Adjacent ownership — explicit anti-sprawl boundaries

| Concern touched by Phase 3 data | Owner outside Phase 3 |
|---|---|
| Program slots, backup chains, compile/link/validate, applying alpha/blend/scale/flip/enabled | **Phase 4** |
| GL buffer allocation, formats, clears, colortex/depth/shadow objects, ping-pong and flip execution | **Phase 5**; Phase 3 emits requirements only |
| Built-in uniform values, smoothing, samplers, and the decision whether to use the reserved `centerDepthSmooth` redirect | **Phase 6** |
| Frame/dimension transitions, `(internal)` pack contents, render flags, sun/moon/cloud/overlay/culling behavior | **Phase 7** |
| Shadow rendering and `shadowTranslucent` behavior | **Phase 8** |
| Registry resolution/merge of block/item/entity mappings and hand-light behavior | **Phase 9** |
| Vertex population and `oldLighting`/`separateAo` behavior | **Phase 10** |
| Evaluation of `uniform.*`/`variable.*` expressions | **Phase 11**; Phase 3 stores typed declarations plus raw expressions |
| GUI widgets, navigation, slider interaction, apply/discard, and reload UX | **Phase 12**; Phase 3 owns the model and persistence codec |
| Loading/uploading custom/noise textures and interpreting `.mcmeta` | **Phase 13**; Phase 3 stores lossless specs |
| Async/PBO work or preprocessor performance optimization | **Phase 14** |
| Modern compute/storage execution and the final identity decision | **G8/S2 and G8/S3**; Phase 3 reserves compatible data shapes now |

Phase 3 does not scan Forge registries or mod jars, invoke GL, render a GUI, compile GLSL, allocate a
buffer, evaluate a custom expression, or load texture pixels.

## 2. Architecture overview

### 2.1 Invariants

1. **One publication boundary.** A load returns `PackLoadResult.Off`,
   `PackLoadResult.Loaded(configuration)`, or `PackLoadResult.Failed(failure)`. Only `Loaded`
   publishes configuration; downstream phases never consume parser objects, filesystem handles,
   or partially built configuration.
2. **One source of downstream truth.** Every source, option value, directive result, property,
   mapping rule, compatibility status, and diagnostic retained for later work is reachable from
   `PackConfiguration`.
3. **Untrusted input throughout.** Pack paths, archive entries, source markers, include paths,
   properties, and persisted option files are hostile until validated.
4. **Contract before reference.** Pintonium/Oculus structure is retained only where it reproduces
   RESEARCH/Appendix semantics. Their divergent dimension, properties, option-confirmation, and
   transformation behavior is not inherited.
5. **No hidden Minecraft seam.** Inputs that originate in `:mod` cross as immutable strings,
   enums, numbers, byte sources, or Phase 1 `GLCapabilityProfile`; never as MC objects.

### 2.2 Public shape

Illustrative signatures name the contracts; implementations remain private under `.internal`.

```java
public interface PackFrontEnd {
    int CURRENT_SCHEMA_VERSION = 2;
    PackDiscoveryResult discover(PackDiscoveryRequest request);
    PackLoadResult load(PackLoadRequest request);
}

public record PackDiscoveryRequest(
    Path shaderpacksDirectory,
    DiagnosticReporter diagnostics) {}

public record PackDiscoveryResult(
    DiscoveryGeneration generation,
    List<PackCandidate> candidates,
    List<EngineDiagnostic> diagnostics) {}

public record PackCandidate(
    PackCandidateId id,
    PackCandidateKind kind,
    String displayName,
    PackCandidateStatus status,
    List<EngineDiagnostic> diagnostics) {}

public interface InternalPackSource {
    PackIdentity identity();
    InternalPackSnapshot snapshot(PackInputLimits limits) throws InternalPackReadException;
}

public record NormalizedPackPath(String canonicalString) {
    public NormalizedPackPath {
        // Validate the canonical grammar below; never normalize a rejected value implicitly.
        Objects.requireNonNull(canonicalString, "canonicalString");
    }
}

public record InternalPackSnapshot(List<InternalPackEntry> entries) {}

public sealed interface InternalPackEntry {
    NormalizedPackPath path();
    record File(NormalizedPackPath path, ImmutableBytes bytes) implements InternalPackEntry {}
    record Directory(NormalizedPackPath path) implements InternalPackEntry {}
}

public interface ImmutableBytes {
    int size();
    byte[] copy();
}

public record PackLoadRequest(
    Path shaderpacksDirectory,
    PackSelection selection,
    RuntimeIdentityData runtimeIdentity,
    GLCapabilityProfile capabilities,
    EngineOptionData engineOptions,
    InternalPackSource internalPackSource,
    DiagnosticReporter diagnostics) {}

public sealed interface PackLoadResult {
    record Off() implements PackLoadResult {}
    record Loaded(PackConfiguration configuration) implements PackLoadResult {}
    record Failed(PackLoadFailure failure) implements PackLoadResult {}
}

public record PackConfiguration(
    int schemaVersion,
    PackIdentity pack,
    CompatibilityStatus compatibility,
    Map<DimensionKey, DimensionConfiguration> dimensions,
    SourceCatalog sources,
    OptionConfiguration options,
    MacroConfiguration macros,
    ShaderPropertiesModel properties,
    ResourceRequirements resources,
    IdMappingInput idMappings,
    List<EngineDiagnostic> diagnostics,
    ConfigurationFingerprint fingerprint) {}

public record IdMappingInput(
    int schemaVersion,
    IdMappingMacroEnvironment parserEnvironment,
    IdMappingFileInput blocks,
    IdMappingFileInput items,
    IdMappingFileInput entities,
    IdMappingFileInput layers) {}

public record IdMappingFileInput(
    MappingKind kind,
    MappingFileState state,
    List<MappingRule> ordinaryRules,
    List<MappingRule> forced11300Rules,
    IdMappingFileFingerprint fingerprint) {}

public interface IdMappingParser {
    IdMappingFileInput parse(IdMappingParseRequest request);
}

public record IdMappingParseRequest(
    MappingKind kind,
    Optional<ImmutableBytes> source,
    MappingOrigin origin,
    IdMappingMacroEnvironment environment,
    DiagnosticReporter diagnostics) {}

public sealed interface MappingRule permits IdRule, LayerRule {
    SelectorKind selectorKind();
    String selectorToken();
    MappingEra era();
    MappingOrigin origin();
    int sourceLine();
    int selectorOrdinal();
}

public record IdRule(
    int shaderId,
    SelectorKind selectorKind,
    String selectorToken,
    OptionalInt legacyMetadata,
    List<PropertyPredicate> propertyPredicates,
    MappingEra era,
    MappingOrigin origin,
    int sourceLine,
    int selectorOrdinal) implements MappingRule {}

public record LayerRule(
    RequestedRenderLayer layer,
    SelectorKind selectorKind,
    String selectorToken,
    OptionalInt legacyMetadata,
    List<PropertyPredicate> propertyPredicates,
    MappingEra era,
    MappingOrigin origin,
    int sourceLine,
    int selectorOrdinal) implements MappingRule {}

public record PropertyPredicate(String propertyName, List<String> acceptedValues) {}

public sealed interface MappingOrigin permits PackMappingOrigin, ModMappingOrigin {}
public record PackMappingOrigin(PackIdentity pack, NormalizedPackPath source)
    implements MappingOrigin {}
public record ModMappingOrigin(
    String modId, int contributionOrdinal, String sourceName) implements MappingOrigin {}

public enum MappingKind { BLOCK, ITEM, ENTITY, LAYER }
public enum MappingFileState { ABSENT, PRESENT_EMPTY, PRESENT_RULES }
public enum SelectorKind { ENTRY, TAG }
public enum MappingEra { CLASSIC, MODERN }
public enum RequestedRenderLayer { SOLID, CUTOUT, CUTOUT_MIPPED, TRANSLUCENT }
```

`NormalizedPackPath.canonicalString()` is the sole public path projection. Its value is non-empty,
Unicode NFC, root-relative, and slash-separated. It contains no leading/trailing `/`, empty segment,
`.` or `..` segment, backslash, NUL, URI/drive prefix, or host-dependent separator. Construction
validates this grammar and rejects a non-NFC spelling rather than silently rewriting caller data.
Canonical path order is unsigned lexicographic order of the UTF-8 bytes of `canonicalString()`;
identity hashes and wire formats consume those exact bytes. `toString()`, `Path.toString()`, display
names, and platform path objects are not stable projections (`[D-P3-24]`).

`DimensionKey` is an engine value containing the numeric legacy dimension ID, not a Minecraft
dimension type. An `OFF` request returns `PackLoadResult.Off` without opening an input, publishing a
configuration, or reporting failure; Phase 7/12 replace any prior configuration with their owned
shaders-off state. `InternalPackSource.snapshot` returns one finite, immutable manifest in
canonical-path order, including empty directories. Phase 3 revalidates every `canonicalString()`,
rejects duplicate canonical paths or file/directory collisions, and applies
the same entry-count, byte-count, path-length, and nesting limits used for archives. A provider
limit/read violation becomes an attributed pack-level failure. `ImmutableBytes` exposes size plus
a fresh copy on every `copy()` call; Phase 3 owns that copy, and neither the manifest nor the
published configuration retains provider storage. `identity()` is stable for equal internal
content and configuration. Phase 7 supplies the content. `RuntimeIdentityData` contains the MC version tuple,
engine edition, engine version, OS family, and configured per-pack identity override as plain values.

`SourceCatalog` owns stable `SourceId`s, original logical lines, the include graph, and a
`SourceMaterializer`:

```java
public interface SourceMaterializer {
    MaterializationResult materialize(
        SourceKey root,
        OptionState options,
        MacroContribution contribution,
        GeometryTranslationRequest geometryTranslation);
}

public sealed interface MaterializationResult {
    record Available(MaterializedSource source) implements MaterializationResult {}
    record Unavailable(SourceKey root, List<EngineDiagnostic> diagnostics)
        implements MaterializationResult {}
}

public record DeclaredUniformCatalog(
    MaterializationFingerprint materialization,
    List<DeclaredUniform> declarations) {}

public record CanonicalDeclaredUniformPayload(
    int schemaVersion,
    List<DeclaredUniform> declarations) {}

public record DeclaredUniform(
    String exactName,
    DeclaredGlslType type,
    ShaderSourceStage declaringStage,
    AttributedSourceLocation location) {}

public sealed interface DeclaredGlslType {
    record Scalar(ScalarKind kind) implements DeclaredGlslType {}
    record Vector(ScalarKind component, int width) implements DeclaredGlslType {}
    record Matrix(ScalarKind component, int columns, int rows) implements DeclaredGlslType {}
    record Sampler(SampledKind sample, TextureDimension dimension,
                   boolean arrayed, boolean shadow, boolean multisample)
        implements DeclaredGlslType {}
    record Image(SampledKind sample, TextureDimension dimension,
                 boolean arrayed, boolean multisample)
        implements DeclaredGlslType {}
    record AtomicCounter() implements DeclaredGlslType {}
    record Array(DeclaredGlslType element, List<ArrayExtent> extents)
        implements DeclaredGlslType {}
    record Struct(Optional<String> declaredName, List<StructField> fields,
                  DeclaredStructFingerprint shape)
        implements DeclaredGlslType {}
}

public sealed interface ArrayExtent {
    record Sized(int positiveConstant) implements ArrayExtent {}
    record Unsized() implements ArrayExtent {}
}

public record StructField(String exactName, DeclaredGlslType type) {}
public record DeclaredStructFingerprint(String value) {}
public record AttributedSourceLocation(SourceId source, int logicalLine, int column) {}

public enum ScalarKind { BOOL, SIGNED_INT, UNSIGNED_INT, FLOAT, DOUBLE }
public enum SampledKind { FLOAT, SIGNED_INT, UNSIGNED_INT }
public enum TextureDimension { D1, D2, D3, CUBE, RECTANGLE, BUFFER }
public enum ShaderSourceStage { VERTEX, GEOMETRY, FRAGMENT, COMPUTE }

public record GeometryTranslationPlan(
    SourceKey root,
    GeometryInputPrimitive input,
    GeometryOutputPrimitive output,
    int maxVertices) {}

public sealed interface GeometryTranslationRequest {
    record None() implements GeometryTranslationRequest {}
    record Translate(GeometryTranslationPlan plan) implements GeometryTranslationRequest {}
}

public enum GeometryInputPrimitive { POINTS, LINES, LINES_ADJACENCY, TRIANGLES, TRIANGLES_ADJACENCY }
public enum GeometryOutputPrimitive { POINTS, LINE_STRIP, TRIANGLE_STRIP }

public interface MacroContributor {
    MacroContribution contribute(PackConfiguration configuration);
}

public sealed interface MacroContribution {
    record Empty() implements MacroContribution {}
    record DefineCenterDepthSmooth(String replacementTokens) implements MacroContribution {}
}
```

The only reserved contributor name in this phase is
`phase6.centerDepthSmoothRedirect`. Its location is after `#version` and active hoisted
`#extension` directives, in the logical macro header, before the first restored pack `#line`.
Phase 6 returns `Empty` or one `DefineCenterDepthSmooth` operation. The latter defines only the
object-like macro `centerDepthSmooth`; its replacement must be a non-empty, newline-free,
tokenizable GLSL expression with no preprocessing directive or comment token. Invalid input
produces `Unavailable` with an attributed diagnostic. Application is deterministic at the stated
location and no other contributed name or operation is accepted. Materialization catches
include/preprocessor/validation failures and returns `Unavailable` with the root and all stable,
source-attributed diagnostics; it never throws for unavailable or malformed pack source.
`MaterializedSource.declaredUniforms()` returns the catalog above. Phase 3 first constructs the
`CanonicalDeclaredUniformPayload` from the catalog schema version and immutable declaration list;
that payload has no `materialization` field. It hashes the payload's deterministic canonical
encoding with the other §4.10 materialization inputs, then embeds the completed result in both
`MaterializedSource.fingerprint()` and `DeclaredUniformCatalog.materialization`, which are exactly
equal. The signatures are illustrative; the binding data contract is the closed type algebra,
immutable declaration order, source attribution, canonical payload exclusion, and fingerprint
equality. Widths, matrix dimensions, array extents, sampler combinations, and recursively frozen
struct fields are validated against the source's effective GLSL version. `ArrayExtent` is either a
positive constant extent or the closed `UNSIZED` variant; no expression text or mutable syntax
node escapes.

### 2.3 Load pipeline

The load is a transaction with no externally visible partial state. `OFF` short-circuits to
`PackLoadResult.Off`; every other selection follows this pipeline:

1. enumerate and resolve the selected candidate;
2. open one bounded `PackInput` lease, locate the effective `shaders/` root, index files, read
   immutable bytes, and close the folder/archive lease;
3. build base and `world-128`…`world128` source sets using OF dimension semantics;
4. decode sources, allocate stable source IDs, parse includes, and build the directed include
   graph;
5. compute weakly connected components and discover options without expanding includes;
6. decode per-pack changed values and global engine options;
7. create the standard macro environment from `RuntimeIdentityData` and
   `GLCapabilityProfile`;
8. safely preprocess and parse `shaders.properties`, then validate profiles/screens/textures/
   custom declarations/program state;
9. finalize the active option snapshot and run plan-independent analysis of active shader roots
   through line rewriting, include expansion, and jcpp, preserving any attributed legacy geometry
   pair without translating it;
10. scan directives/declarations and fold them into immutable `ResourceRequirements`;
11. snapshot each pack ID-map file's presence, preprocess/parse it with standard A–G macros only,
    and for a present entity file also produce the isolated forced-`MC_VERSION=11300` parse;
12. validate cross-field invariants, compute a content fingerprint, and atomically publish
    `PackConfiguration`; final-source debug dumping occurs only on later successful materialization.

If a dimension folder exists, its `DimensionConfiguration` is built solely from `.vsh`/`.fsh`
files in that folder. It does not inherit base programs. An empty folder is represented explicitly
as `DimensionMode.DISABLED`, not as a missing map entry.

### 2.4 Internal data relationships

```text
PackInputSnapshot
  ├─ SourceCatalog ─ IncludeGraph ─ OptionCatalog ─ SourceMaterializer
  ├─ ShaderPropertiesModel ─ Option/Profile/Screen/Texture/Expression/ProgramState models
  ├─ DirectiveScan ─ ResourceRequirements
  └─ IdMappingInput (four file states + ordinary/forced entity rules)
                         ↓ validate/freeze
                  PackConfiguration
                         ↓ only downstream input
       P4  P5  P6  P7  P8  P9  P10  P11  P12  P13
```

## 3. Contract conformance map

### 3.1 Appendix F.1 engine flags and behavior ownership

All flags are parsed by Phase 3. The “behavior owner” is the single phase that must wire the
render-visible effect; the raw tri-state remains in `PackConfiguration.properties().engineFlags()`.
`clouds` uses `DEFAULT/FAST/FANCY/OFF`; the others use `DEFAULT/TRUE/FALSE`. An in-game setting
with documented higher priority is resolved by the behavior owner, not by this MC-free parser.

| Appendix F.1 key | Phase 3 field | Behavior owner | Named parser test |
|---|---|---:|---|
| `clouds` | `EngineFlags.clouds` | Phase 7 | `engineFlag_cloudsFourStates` |
| `oldHandLight` | `EngineFlags.oldHandLight` | Phase 9 | `engineFlag_oldHandLightTriState` |
| `dynamicHandLight` | `EngineFlags.dynamicHandLight` | Phase 9 | `engineFlag_dynamicHandLightTriState` |
| `oldLighting` | `EngineFlags.oldLighting` | Phase 10 | `engineFlag_oldLightingTriState` |
| `shadowTranslucent` | `EngineFlags.shadowTranslucent` | Phase 8 | `engineFlag_shadowTranslucentTriState` |
| `underwaterOverlay` | `EngineFlags.underwaterOverlay` | Phase 7 | `engineFlag_underwaterOverlayTriState` |
| `sun` | `EngineFlags.sun` | Phase 7 | `engineFlag_sunTriState` |
| `moon` | `EngineFlags.moon` | Phase 7 | `engineFlag_moonTriState` |
| `vignette` | `EngineFlags.vignette` | Phase 7 | `engineFlag_vignetteTriState` |
| `backFace.solid` | `EngineFlags.backFaceSolid` | Phase 7 | `engineFlag_backFaceSolidTriState` |
| `backFace.cutout` | `EngineFlags.backFaceCutout` | Phase 7 | `engineFlag_backFaceCutoutTriState` |
| `backFace.cutoutMipped` | `EngineFlags.backFaceCutoutMipped` | Phase 7 | `engineFlag_backFaceCutoutMippedTriState` |
| `backFace.translucent` | `EngineFlags.backFaceTranslucent` | Phase 7 | `engineFlag_backFaceTranslucentTriState` |
| `rain.depth` | `EngineFlags.rainDepth` | Phase 7 | `engineFlag_rainDepthTriState` |
| `beacon.beam.depth` | `EngineFlags.beaconBeamDepth` | Phase 7 | `engineFlag_beaconBeamDepthTriState` |
| `separateAo` | `EngineFlags.separateAo` | Phase 10 | `engineFlag_separateAoTriState` |
| `frustum.culling` | `EngineFlags.frustumCulling` | Phase 7 | `engineFlag_frustumCullingTriState` |

This is the complete Appendix F.1 ownership map. Pintonium parsing is corroborating evidence only;
its unconsumed `dynamicHandLight` does not satisfy an owner
`[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/properties/ShaderProperties.java]`
(PD §7.4).

### 3.2 Appendix F.2–F.8 key map

No Appendix F item is left to an implicit “miscellaneous” parser.

| Contract key/form | Phase 3 design element and exact disposition | Downstream owner | Provenance / named test |
|---|---|---|---|
| `version.<mcver>=<edition>` | `MinimumEditionRule` plus `CompatibilityStatus`; parse remains successful and produces a pack-list warning/status | Phase 7 activation UX, Phase 12 list | App F.2; `versionGate_matchingNonmatchingAndMalformed` |
| switch `#define NAME` / `// #define NAME` | `OptionCandidate.Switch`; only confirmed references become options | Phase 12 UI, Phase 4 source request | App F.3; `switchOption_onOffAndTooltip` |
| same-file `#ifdef`/`#ifndef` confirmation | A candidate is eligible only from a confirming reference in that original file; WCC analysis scopes duplicate merging and cannot promote an unconfirmed candidate | Phase 3 | App F.3; `switchOption_sameFileConfirmation` |
| variable `#define NAME value // tooltip [values]` | `OptionCandidate.Variable`; default is inserted into allowed values | Phase 12 | App F.3; `variableOption_defaultAutoAdded` |
| const-option whitelist | Explicit table for `shadowMapResolution`, `shadowMapFov`, `shadowDistance`, `shadowDistanceRenderMul`, `shadowIntervalSize`, `generateShadowMipmap`, `generateShadowColorMipmap`, `shadowHardwareFiltering`, `shadowHardwareFiltering0/1`, all documented `shadowtex0/1Mipmap`, `shadowcolor0/1Mipmap`, `shadowtex0/1Nearest`, `shadowcolor0/1Nearest` capitalization aliases, `wetnessHalflife`, `drynessHalflife`, `eyeBrightnessHalflife`, `centerDepthHalflife`, `sunPathRotation`, `ambientOcclusionLevel`, `superSamplingLevel`, and `noiseTextureResolution`; visibility requires values or slider/profile/screen reference | Phase 12 | App F.3; `constOption_completeWhitelistAndAliases` |
| compile-time option application | `OptionLineRewriter` rewrites only captured switch/value spans before include expansion and preprocessing, retaining `#line` attribution | Phase 4 requests materialization | §4.7/App F.3; `optionRewrite_onlyCapturedSpan`, `optionRewrite_roundTripMaterialization` |
| per-pack `shaderpacks/<pack>.txt` | ISO-8859-1 Properties codec reads changed values into reload merge and writes changed pack options only | Phase 12 invokes read/write | §4.7; `persistence_packChangedOnlyRoundTripAndApply` |
| global `optionsshaders.txt` equivalent | separate ISO-8859-1 Properties codec reads/applies engine-global settings and never mixes them with pack options | Phase 12 invokes read/write | §4.7; `persistence_globalRoundTripAndApply` |
| ambiguous option defaults | `Ambiguity.DISABLED`; retains locations and diagnostic, cannot be changed | Phase 12 displays disabled | App F.3; `optionAmbiguity_conflictingDefaultsDisabled` |
| `option.<NAME>[.comment]` | `LangDecorations.option` | Phase 12 | App F.3; `lang_optionLabelsAndComments` |
| `value.<NAME>.<val>` | `LangDecorations.value` | Phase 12 | App F.3; `lang_valueLabels` |
| `prefix.<NAME>` / `suffix.<NAME>` | `LangDecorations.affix` | Phase 12 | App F.3; `lang_prefixSuffix` |
| `profile.<NAME>[.comment]` in `.lang` | `LangDecorations.profile` | Phase 12 | App F.3; `lang_profileLabelsAndComments` |
| `screen.<NAME>[.comment]` in `.lang` | `LangDecorations.screen` | Phase 12 | App F.3; `lang_screenLabelsAndComments` |
| `sliders=<option list>` | Ordered `SliderSet`; unknown/non-variable names diagnose but do not invalidate other entries | Phase 12 | App F.3; `sliders_orderAndUnknownEntry` |
| `profile.NAME=<tokens>` | `ProfileModel`; supports `OPTION`, `!OPTION`, `OPTION:value`, `OPTION=value`, `profile.OTHER`, dimension-qualified `!program.*`; copies are cycle-guarded; inference tests profiles by descending constraint count with source order as the tie-break, returning the first exact option-state match or `Custom` when none matches | Phase 4 consumes disabled programs; Phase 12 selects | App F.4; `profiles_allTokenFormsAndCycles`, `profiles_inferenceMatchAndCustom` |
| `screen=<entries>` | main `ScreenModel`; an explicit valid column count wins, otherwise resolved columns are `max(2, ceil(actualOptionCount / 9))` | Phase 12 | App F.4; `screen_mainEntries`, `screen_columnsBoundaryMixed18And19Options` |
| `screen.NAME=<entries>` | named `ScreenModel` with `[SUBSCREEN]`, `<profile>`, `<empty>`, `*`, option entries; navigation/layout entries do not count toward widening | Phase 12 | App F.4; `screen_subscreenEntriesAndReferences`, `screen_columnsBoundaryMixed18And19Options` |
| `screen.columns=N` | main explicit positive column count; valid `N` bypasses auto-widening | Phase 12 | App F.4; `screen_mainColumns`, `screen_columnsBoundaryMixed18And19Options` |
| `screen.NAME.columns=N` | named explicit positive column count; valid `N` bypasses auto-widening | Phase 12 | App F.4; `screen_subscreenColumns`, `screen_columnsBoundaryMixed18And19Options` |
| `texture.<gbuffers\|deferred\|composite>.<sampler>[.0-9]` pack path | `CustomTextureSpec.PackPath`; duplicate discriminator is separate from sampler and never discarded | Phase 13 | App F.5; `texture_packPathAndDuplicateSuffix` |
| same key, `minecraft:` asset/live texture | `CustomTextureSpec.MinecraftResource`; keeps `_n`/`_s` and dynamic/atlas identity as text | Phase 13 | App F.5; `texture_minecraftDynamicAndCompanionSuffix` |
| same key, raw form | `CustomTextureSpec.Raw` with type, internal format, exact dimensions, pixel format/type; malformed arity or an unknown/incompatible format token warns/ignores that line | Phase 13 | App F.5; `texture_rawAllFourTypesAndArity`, `texture_rawFormatDomainsAcceptedRejected`, `texture_rawIntegerTransferCompatibility` |
| same texture's `.mcmeta` blur/clamp | `TextureSidecarRef` is retained without loading it; filter/wrap information is not stripped | Phase 13 | App F.5; `texture_sidecarReferencePreserved` |
| stage mapping | `GBUFFERS` applies to gbuffers+shadow, `DEFERRED` to deferred, `COMPOSITE` to composite+final | Phases 4/13 | App F.5; `texture_stageExpansion` |
| multiple texture types on one unit | model keys by `(stage,sampler,type)` and validates one sampler type per unit per program later | Phases 4/13 | App F.5; `texture_sharedUnitDistinctTypes` |
| `texture.noise=<pack path>` | `NoiseTextureSpec.Override`; otherwise generated-noise requirement remains | Phase 13 | App F.5; `texture_noiseOverride` |
| `uniform.<float\|int\|bool\|vec2\|vec3\|vec4>.<name>` | `CustomExpressionDecl(UNIFORM,type,name,rawExpression)`; no evaluation here | Phase 11 evaluates, Phase 6 uploads | App F.6; `customDecl_allUniformTypesRawExpression` |
| `variable.<type>.<name>` | `CustomExpressionDecl(VARIABLE,...)`; duplicate/type/name validation only | Phase 11 | App F.6; `customDecl_allVariableTypesAndDuplicates` |
| F.6 constants/parameters/operators/functions/exclusions | Expression text and declaration order are lossless; the vocabulary is not pre-validated or evaluated by Phase 3 | Phase 11 | App F.6; `customDecl_expressionTextLossless` |
| F.6 precipitation rule | Preserve the behavior handoff: render precipitation iff `biome_precipitation != PPT_NONE`; classify rain at `temperature >= 0.15`, otherwise snow; this row has no Phase 3 parser/model assertion | Phase 7 | App F.6; Phase 7 test `precipitation_noneAndTemperatureBoundary` |
| `alphaTest.<prog>` | parsed `AlphaTestSpec(OFF or func/ref)` with all documented funcs | Phase 4 | App F.7; `programState_alphaTestAllFuncs` |
| `blend.<prog>` | parsed `BlendSpec(OFF or color pair plus optional alpha pair)` with the 15 documented factors | Phase 4 | App F.7; `programState_blendArityAndFactors` |
| `scale.<prog>` | `ViewportScale(scale,offsetX,offsetY)`, each in 0…1 | Phase 4 applies, Phase 5 supplies estate | App F.7; `programState_scaleFormsAndRange` |
| `flip.<prog>.<buf>` | tri-state explicit flip override; virtual `*_pre` names retained; no copy-back semantic introduced | Phases 4/5 | App F.7; `programState_flipVirtualPreAndLastWriter` |
| `program.<prog>.enabled` | Phase-3-owned small Boolean-option AST (`!`, `&&`, `\|\|`, parentheses, switch names), evaluated against immutable `OptionState`; disabled means absent to Phase 4 backup chain | Phase 4 | App F.7; `programEnabled_booleanGrammarAndFallbackSignal` |
| `shaders/world<id>/` | explicit base/override/disabled dimension model, numeric scan −128…128, only `.vsh`/`.fsh` in overrides, options included | Phase 7 selects | App F.8/§3.1; `dimension_overrideNoMergeAndEmptyDisables` |
| `#include "relative"` | normalized relative to including file, within shaders root | Phase 3 | App F.8/§3.2; `include_relativeAttribution` |
| `#include "/absolute"` | normalized from shaders root | Phase 3 | App F.8/§3.2; `include_absoluteAttribution` |
| include depth ≤10 / include guards | edge depth is capped at 10; graph cycles fail affected roots even if guarded because includes precede preprocessing | Phase 3 | App F.8/§3.2; `include_depthTenAndCycle` |
| `-Dshaders.debug.save=true` equivalent | Phase 1's `-Dschmaloogium.debug.saveSources`; dumps final processed sources to runtime `shaderpacks/debug/`, never repository fixtures | Phase 3 | App F.8; `debugDump_sanitizedLocalOnly` |

### 3.3 Appendix A.3 directive-to-field map

Every row has a field and a named test. `DirectiveScanner` recognizes declarations after option
rewrite/include expansion/conditional preprocessing. Known classic comment directives are accepted
as both `/* KEY:value */` and `// KEY:value`; const declarations remain case-sensitive GLSL
identifiers. A malformed occurrence warns and is ignored without clearing a previously valid value.

| Appendix A.3 directive | `PackConfiguration` target | Named conformance test |
|---|---|---|
| `attribute … mc_Entity / mc_midTexCoord / at_tangent` (`.vsh` only) | per-program `VertexRequirements.attributes` | `directive_extendedAttributeOptIns`, `directive_attributeWrongStageIgnored` |
| `const int countInstances=N` (`.vsh` only) | per-program `instanceCount` (positive integer) | `directive_countInstances`, `directive_countInstancesWrongStageIgnored` |
| `#extension GL_ARB_geometry_shader4` + `maxVerticesOut` (`.gsh` only) | `LegacyGeometryConfig` plus attributed `LegacyGeometryRewriteSite`; materialization executes Phase 4's selected `GeometryTranslationPlan` and emits deterministic transformed core-form source | `directive_legacyGeometryPair`, `directive_geometryPairWrongStageIgnored`, `geometryRewrite_deterministicAttributedOrUnavailable` |
| `uniform … shadow/shadowtex0/shadowtex1/watershadow` | `shadowDepthBuffers` minimum 1/2 | `directive_shadowDepthUniformSizing` |
| `uniform … shadowcolor/shadowcolor0/shadowcolor1` | `shadowColorBuffers` minimum 1/2 | `directive_shadowColorUniformSizing` |
| `uniform … depthtex0/1/2`, `gdepthtex` | `mainDepthTextures` minimum 1/2/3 | `directive_mainDepthUniformSizing` |
| `uniform … colortex0-7` and legacy names | `colorBuffers` highest referenced index + 1 | `directive_colortexAndLegacySizing` |
| `uniform … gdepth` | format request for colortex1 `RGBA32F` if still default RGBA | `directive_gdepthUpgradeOnlyDefault` |
| `uniform … centerDepthSmooth` | `centerDepthSmoothRequired=true` | `directive_centerDepthSmoothReadback` |
| `shadowMapResolution` / `SHADOWRES` | `ShadowConfig.resolution` | `directive_shadowResolutionAllForms` |
| `shadowMapFov` / `SHADOWFOV` | `ShadowConfig.fov` | `directive_shadowFovAllForms` |
| `shadowDistance` / `SHADOWHPL` | `ShadowConfig.distance` | `directive_shadowDistanceAllForms` |
| `shadowDistanceRenderMul` | `ShadowConfig.distanceRenderMultiplier` | `directive_shadowDistanceRenderMul` |
| `shadowIntervalSize` | `ShadowConfig.intervalSize` | `directive_shadowIntervalSize` |
| `generateShadowMipmap` / `generateShadowColorMipmap` | aggregate shadow depth/color mipmap requests | `directive_generateShadowMipmaps` |
| `shadowHardwareFiltering[0/1]` | per-shadow-depth hardware-PCF request | `directive_shadowHardwareFilteringAliases` |
| `shadowtex0/1Mipmap`, `shadowcolor0/1Mipmap` + capitalization aliases | per-texture mipmap bits | `directive_shadowPerTextureMipmapAliases` |
| `shadowtex0/1Nearest`, `shadowcolor0/1Nearest` + all named aliases | per-texture nearest bits | `directive_shadowPerTextureNearestAliases` |
| `wetnessHalflife` / `WETNESSHL` | `SmoothingConstants.wetnessHalfLifeTicks` | `directive_wetnessHalflifeAllForms` |
| `drynessHalflife` / `DRYNESSHL` | **distinct** `SmoothingConstants.drynessHalfLifeTicks` | `directive_drynessWritesDryness` |
| `eyeBrightnessHalflife` | `SmoothingConstants.eyeBrightnessHalfLifeTicks` | `directive_eyeBrightnessHalflife` |
| `centerDepthHalflife` | `SmoothingConstants.centerDepthHalfLifeTicks` | `directive_centerDepthHalflife` |
| `sunPathRotation` | `WorldRenderConstants.sunPathRotation` | `directive_sunPathRotation` |
| `ambientOcclusionLevel` | `WorldRenderConstants.ambientOcclusionLevel` constrained 0…1 | `directive_ambientOcclusionLevel` |
| `superSamplingLevel` | `WorldRenderConstants.superSamplingLevel` positive integer | `directive_superSamplingLevel` |
| `noiseTextureResolution` | `NoiseRequirement.resolution` and enabled flag | `directive_noiseTextureResolution` |
| `colortexNFormat` / legacy-name format | validated per-colortex internal-format request | `directive_colortexFormatsAndAliases`, `directive_colortexFormatDomainAcceptedRejected` |
| `colortexNClear=false` | per-colortex clear-enabled override | `directive_colortexClear` |
| `colortexNClearColor=vec4(...)` | per-colortex clear color | `directive_colortexClearColor` |
| `colortexNMipmapEnabled=true` | per-program colortex mipmap requests | `directive_colortexMipmapEnabled` |
| `GAUX4FORMAT` (`RGBA32F`/`RGB32F`/`RGB16`) | colortex7 format request | `directive_gaux4FormatAllCommentForms` |
| `DRAWBUFFERS` | ordered routing list 0–7/`N` | `directive_drawbuffersAllCommentForms` |
| modern `RENDERTARGETS` (`post-v0.5`) | ordered routing list 0–15 | `directive_rendertargetsAndPrecedence` (`post-v0.5`) |

The half-life fields are stored in **ticks** exactly as Appendix A.3 states. Phase 6 owns the
smoothing formula; this phase does not import an alternate unit.

### 3.4 Reference evidence, contract checks, and pitfall dispositions

| Evidence item | Disposition in this design | Contract check and decision | Named test |
|---|---|---|---|
| Pintonium B1: `drynessHalflife` writes `wetnessHalfLife` `[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/properties/PackDirectives.java]` | Independent table rows/field setters; no shared half-life consumer | Appendix A.3 distinct fields; D-P3-10 | `directive_drynessWritesDryness` |
| Pintonium B2: comment/uniform handlers are no-ops `[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/parsing/DispatchingDirectiveHolder.java]` | Dedicated block-comment, line-comment, const, declaration scanners feed one typed dispatch table | §3.2/App A.3 requires all forms; D-P3-10 | `directive_legacyCommentFormsReachFields` |
| Pintonium B3: WCC returns the whole graph `[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/include/IncludeGraph.java]` | Real undirected adjacency + deterministic DFS components; same-file confirmation cannot leak across files/components | App F.3 same-file rule; D-P3-5 | `optionRefsDoNotCrossWcc` and `optionAmbiguity_duplicateNamesAcrossComponents` |
| Pintonium B12: properties path strips `#` `[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/preprocessor/PropertiesPreprocessor.java]` | Separate protected-token properties adapter preserves every data-line `#` and backslash | §3.3/App F; D-P3-6 | `propertyHashRoundTrip` |
| Pintonium include graph/cycle diagnostics `[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/include/IncludeGraph.java]` | Adopt graph shape and DFS diagnostics; add the contract depth cap and root-local failure | §3.2/App F.8; D-P3-2 | `include_depthTenAndCycle` |
| Pintonium jcpp marker hoist and macro API `[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/preprocessor/JcppProcessor.java]` | Adopt with token-aware markers, a spoof guard, strict `#version` validation, and source-ID restoration | §3.2/§3.5; D-P3-1/D-P3-3 | `preprocess_versionExtensionHoist`, `preprocess_markerSpoofRejected`, `preprocess_lineAttribution` |
| Pintonium Iris dimension model `[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/ShaderPack.java]` | Reject; retain OF −128…128, `.vsh`/`.fsh`-only, no-merge semantics | §3.1/App F.8; D-P3-8 | `dimension_fullLegacyScanRejectsIrisModel` |
| Oculus FE-07 include-graph structure `[V:observed — Oculus reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/ShaderPack.java; loader-independent]` | Corroborates graph architecture only; no source-era hook inference | §3.2 depth/same-file rules still control; D-P3-12 | `include_graphAllRootsStable` |
| Oculus FE-09 deterministic ordering `[V:observed — Oculus reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/discovery/ShaderpackDirectoryManager.java; loader-independent]` | Case-insensitive order with natural-order tie-break | Discovery order is non-contract UI structure; D-P3-11 | `discovery_caseFoldThenNatural` |
| Oculus FE-08 source-order routing precedence `[V:observed — Oculus reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/ProgramDirectives.java; loader-independent]` | If both active routing forms occur, the later source occurrence wins; both remain in diagnostics | Appendix A.3 defines both and no contrary precedence; D-P3-13 | `directive_rendertargetsAndPrecedence` |
| Oculus FE-04 unchecked stored values `[V:observed — Oculus reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/option/values/MutableOptionValues.java; loader-independent]` | Allowed values guide UI; a syntactically safe persisted value is retained as an external current value with a warning | App F.3 does not make the UI list a parser rejection set; D-P3-14 | `persistence_outOfListValueRetainedAndWarned` |
| Oculus FE-01 original-layout split `[V:observed — Oculus reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/ShaderProperties.java; loader-independent]` | Reject as the parse source; all keys, including screen/profile layout, obey active preprocessing. Original spans remain only for diagnostics | §3.3 says the file itself is preprocessed; D-P3-15 | `properties_conditionalScreenLayout` |
| Oculus PB-03/PB-05/PB-06 loader-independent pitfalls (OD §4.2) | Enforce first-significant-token `#version`; never trim property data lines; suppress only recognized hash comments, never malformed directives | §3.3/§3.5; D-P3-3/D-P3-6 | `preprocess_versionMustLead`, `properties_whitespaceAndHash`, `properties_malformedDirectiveWarns` |

### 3.5 Remaining owned RESEARCH §3 contract families

| Contract item | Satisfying design element | Provenance / named test |
|---|---|---|
| `OFF`, `(internal)`, folder, and zip discovery; nested `shaders/` root | `PackDiscovery`, deterministic bounded root selection, and `InternalPackSource` | RESEARCH §3.1; `discovery_sentinelsFolderZipNestedRoot` |
| Archive lifetime and path containment | one bounded `PackArchiveLease`, closed after snapshot; absolute, drive, NUL, `..`, and symlink escapes rejected | RESEARCH §3.1; `discovery_archiveClosesAndRejectsEscapes` |
| Source identity and compiler attribution | stable `SourceId`/file numbers, include-boundary `#line`, and `SourceMap` | RESEARCH §3.2; `include_lineAttributionAcrossNestedFiles` |
| Standard macro identity families | configurable `MacroConfiguration` emits MC/GL/GLSL, OS, vendor, renderer, on-demand extension, and option macros | RESEARCH §3.5; `macro_standardFamiliesExactIdentity` |
| Conditional preprocessing and substitution | jcpp adapters implement define/undef/if-family/defined/substitution for shaders and properties | RESEARCH §3.5; `preprocess_completeConditionalGrammarAllInputs` |
| ID-map macro restriction | properties-safe preprocessing supplies standard A–G macros and no option macros | RESEARCH §3.7; `idMap_standardMacrosOnly` |
| Block/item/entity short, namespaced, property, legacy `id:meta`, and `%namespace:path` tag rules | schema-v2 `IdMappingInput` retains typed rules, selector order/kind, file state, origin, and classic/modern era for Phase 9 | RESEARCH §3.7; `idMap_allDocumentedRuleForms`, `idMap_selectorKindEntryAndTag` |
| Entity compatibility preprocessing | a present entity file publishes ordinary A–G results and a separate result made by replacing only `MC_VERSION` with `11300`; no selection or merge occurs here | RESEARCH §3.7 preprocessing requirement; `idMap_entityForced11300Isolated` |
| Mod-provided ID-map contributions | public pure `IdMappingParser` accepts optional bounded bytes, mapping kind, the published parser environment, and `MappingOrigin` from Phase 9 | RESEARCH §3.7; `idMap_modContributionOriginPreserved` |
| `layer.solid/cutout/cutout_mipped/translucent` and opaque-solid exclusion | typed `LayerRule` with the same selector/era provenance plus deferred resolution constraint | RESEARCH §3.7; `idMap_layersAndOpaqueSolidExclusion` |

## 4. Detailed design

### 4.1 Pack discovery, roots, and lifetime

`PackDiscovery` always returns two logical sentinels first (`OFF`, `(internal)`), followed by
filesystem candidates in case-insensitive/natural tie-break order. `OFF` produces the successful
non-configuration `PackLoadResult.Off` transition defined in §2.2.
`(internal)` selects `InternalPackSource`; Phase 7 supplies the bytes and stable identity.

A folder/zip candidate is valid when a bounded recursive search finds at least one directory named
`shaders`. Search limits are configurable but finite (entry count, total uncompressed bytes, path
length, nesting depth). If several roots exist, the shallowest wins, ties use normalized
lexicographic path, and ignored roots generate a warning. The chosen root and content hashes form
`PackIdentity`; display names never become paths.

Every entry path is slash-normalized, Unicode-normalized for comparison, rejected if absolute,
drive-qualified, contains NUL, escapes through `..`, or resolves outside the chosen root. Folder
symlinks are not followed during discovery. Zip entries are read through one `PackArchiveLease`
for the selected pack. The lease closes on every success/failure path after bytes are snapshotted;
no zip filesystem survives in `PackConfiguration`. This preserves Pintonium's valuable single-FS
lifecycle discipline without a global static filesystem
`[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/ShaderPack.java]`
(PD §7.1).

Unreadable/corrupt candidates remain list entries with a diagnostic; selecting one returns
`PackLoadResult.Failed(failure)` and leaves shaders off. No archive error escapes to the client.

### 4.2 Dimensions, source identities, and includes

The source index has a base set plus every physical `world<id>` directory for `id ∈ [-128,128]`.
Base recognizes `.vsh`, `.fsh`, and `.gsh` at v0.1. Dimension sets recognize only `.vsh` and
`.fsh`, scan their options, and never merge base sources. A present empty directory is a successful
disabled dimension. A directory outside the numeric range is an ordinary pack path, not a
dimension override.

Every decoded text file receives a stable `SourceId` derived from its normalized root-relative
path, with a separate collision-free integer file number for GLSL `#line`. UTF-8 with optional BOM
is accepted for GLSL and `.lang`; malformed byte sequences produce a source-local diagnostic.
Properties use ISO-8859-1/Java-Properties escape semantics.

Includes are recognized by a line-oriented, comment-unaware scanner before any conditional
preprocessing, matching the documented upstream behavior. Relative paths resolve from the
including file; leading `/` resolves from the effective `shaders/` root. Normalization reuses the
same escape guard as discovery.

`IncludeGraph` stores one node per file and one edge per include location. Construction records
missing-file failures without abandoning unrelated roots. A color/visited DFS reports the first
cycle with the complete edge/source-line chain, then marks every root reaching that cycle
unmaterializable. A separate depth-state DFS rejects an expansion when its longest active path
would exceed ten include edges. Include guards do not waive either check because expansion
precedes preprocessing.

Expansion is memoized by `(root, optionFingerprint)`. Before included lines, it emits
`#line 1 <includedFileNumber>`; after them it emits
`#line <nextParentLine> <parentFileNumber>`. `MaterializedSource.sourceMap()` maps those numbers
back to normalized paths, so Phase 4 can attribute driver logs without filesystem access.

### 4.3 Option discovery, weak components, decoration, and persistence

Each original file is parsed independently:

- switch candidates capture name, default, source span, tooltip, and exact commented/uncommented
  line shape;
- only an `#ifdef NAME` or `#ifndef NAME` in that same original file confirms that file's switch
  candidate; `#if`/`#elif` do not confirm;
- variable candidates capture current/default values, comments, and bracket values;
- const candidates must match the explicit App F.3 whitelist.

The include graph is converted to undirected adjacency and partitioned by stable-path DFS. Within
each weakly connected component, confirmed occurrences with the same name merge into one logical
option and retain every rewrite location. An unconfirmed occurrence never becomes confirmed merely
because another file/component references the name. After component-local merging, pack-level
names are reconciled: equal definitions share one option; conflicting defaults/types become one
disabled ambiguous option with every location reported. This uses component analysis for
correctness without weakening App F.3's same-file rule.

Tooltips split on `. `; a terminal `!` sets `TooltipSeverity.WARNING`. `LanguageMap` loads only
immediate `shaders/lang/*.lang` files as UTF-8, normalizes locale codes, and retains the complete
App F.3 decoration key families. Language fallback choice is Phase 12's.

`OptionLineRewriter` changes only the captured token/value span, never regex-replaces a name
globally. For traceability it may append the stable comment
`// Schmaloogium: changed option <NAME>`, while `#line` restores pack attribution. Pintonium's
location-aware source rewrite and constraint-count profile ordering are structural references
`[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/option/OptionAnnotatedSource.java]`
`[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/option/ProfileSet.java]`;
the behavior is checked against App F.3/F.4 under D-P3-4.

Persistence codecs use deterministic Java-Properties/ISO-8859-1 escaping:

- `shaderpacks/<sanitized-pack-id>.txt` contains changed pack options only;
- `optionsshaders.txt`-equivalent data contains engine-global settings only;
- reading never constrains a syntactically safe current value to the UI's advertised list;
- writing is stable-order, temporary-file + atomic-move where supported, and never follows a
  symlink.

Phase 3 owns codecs and file models. Phase 12 owns when a user applies/discards and invokes writes.
Reload merges persisted values into a new immutable `OptionState`; it never mutates a published
configuration.

Every main or named `ScreenModel` stores explicit columns separately from its ordered entries.
A valid explicit positive column count wins. Otherwise resolved columns are
`max(2, ceil(actualOptionCount / 9))`: 0–18 actual options resolve to 2, 19–27 to 3, and so on.
`[SUBSCREEN]`, `<profile>`, `<empty>`, and other navigation/layout entries do not count. Phase 12
retains ownership of deferred `*` expansion; options produced by that expansion do count before
Phase 12 evaluates the formula.

### 4.4 Standard macros and OQ-7-shaped identity data

`MacroConfiguration` is data, not a hard-coded set:

```text
MacroConfiguration
  baseCompatibilityMacros       // OF A–G
  optionMacros                  // OF H, shader sources only
  capabilityFeatureMacros       // honest supported IRIS_FEATURE_* names
  engineIdentityMacros          // SCHMALOOGIUM + version
  perPackOverrides              // add/suppress/force, validated
  reservedContributors          // phase6.centerDepthSmoothRedirect
```

The v0.1 default is option-3-shaped while the final OQ-7 decision remains open:

- OF-era behavior: no global `IS_IRIS` and no global `IRIS_VERSION`;
- full OF A–G set, with `MC_VERSION=11202` for MC 1.12.2 using the documented
  five-digit “1.9.4 → 10904” formatting algorithm;
- honest `IRIS_FEATURE_*` macros only for capabilities actually implemented;
- `SCHMALOOGIUM` plus a numeric `SCHMALOOGIUM_VERSION`;
- a validated per-pack override capable of adding/suppressing identity or capability macros
  without changing the preprocessor; and
- a policy enum that can switch among OQ-7 options 1–3 in G8/S3.

`MC_GL_VERSION`, `MC_GLSL_VERSION`, vendor, renderer, and extension support come exclusively from
Phase 1 `GLCapabilityProfile`; OS and MC/engine identity come from `RuntimeIdentityData`.
Vendor/renderer matching uses ordered, case-insensitive regex data with an explicit OTHER result.
Supported extensions are emitted on demand only: scan active source tokens for `MC_GL_*`, intersect
with `GLCapabilityProfile.extensions()`, and define the referenced supported names. This retains
the shipped OF behavior rather than Pintonium's enumerate-everything divergence. The version
format/parser shape is informed by PD §7.6 but values are re-derived from the Phase 1 profile
`[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/IrisDefines.java]`.

OF option macros include normal/specular toggles, render/shadow quality, hand depth, old-light
flags, and FXAA only if present. Since FXAA is a non-goal, it is normally absent, not falsely set.
Shader sources receive A–H plus identity/feature/contributor macros. The shipped
`shaders.properties` contract explicitly says only Standard Macros A–G and no option macros;
ID-mapping files say the same. Both receive A–G only. The RC3 wording ambiguity is recorded in
§11.3.

### 4.5 Shader preprocessing with jcpp

jcpp is the selected directive/macro engine. The wrapper accepts only immutable source and an
immutable macro map, creates a new jcpp instance per call, enables comment retention, and converts
all library exceptions into source-local diagnostics.

Processing order:

1. validate that the active root `#version` is the first non-comment/non-whitespace token and that
   no conflicting active version exists;
2. reject any pack text containing the reserved marker namespace;
3. token-rewrite actual directive lines `#version` and `#extension` into invocation-scoped
   `#warning` markers (never global substring replacement);
4. add standard, option, identity, feature, override, and Phase 6 contribution macros through
   `Preprocessor.addMacro`, not textual lines;
5. run jcpp over the option-rewritten, `#line`-annotated include expansion;
6. collect the active version and extension markers, preserve extension source order, and rebuild:
   `#version`, active `#extension` lines, the logical macro-header boundary, then the processed
   body with a restored root `#line`;
7. when a root has no attributed legacy geometry pair,
   `GeometryTranslationRequest.None` succeeds without a geometry rewrite. For a `.gsh` legacy
   pair, require `Translate(plan)` and apply its immutable plan at the attributed rewrite sites;
   applicability requires the same root, one recognized legacy pair, a positive `maxVertices`
   equal to the recognized declaration, and a closed input/output enum.
   Phase 3 removes the legacy extension and replaces the attributed declaration with the core
   input/output layout declarations generated from those fields. `None` for a recognized pair, or
   a mismatched, unsupported, overlapping, or out-of-range plan, returns `Unavailable` with
   source-local diagnostics rather than exposing untransformed ARB source;
8. retain transformed text, `SourceMap`, rewrite diagnostics, and plan fingerprint in
   `MaterializedSource`, making equal inputs byte-identical.

Rewrites are applied in source-span order and may touch only the two spans in
`LegacyGeometryRewriteSite`; pack text supplied through a plan is impossible. The plan fingerprint
is Phase-3-generated canonical encoding of the plan schema, root, enum names, positive vertex
count, and rewrite-site identities/spans—not caller hashing or object identity. Equal sites and
fields therefore produce equal transformed bytes and fingerprints.

Load-time analysis uses the same deterministic pipeline through directive discovery but stops
before step 7's geometry rewrite. It records the attributed pair and scan results in the published
configuration; it neither calls `SourceMaterializer` nor requires a translation request.
Plan-dependent materialization begins only after Phase 4 receives that configuration and selects
`None` or `Translate(plan)`.

The marker namespace includes a generated nonce plus a fixed prefix checked before rewriting.
Unknown/spoofed markers are fatal for that source, not trusted as header material. NUL characters
are diagnosed and removed source-locally because real classic packs contain them; their removal is
recorded in the source fingerprint.

This adopts Pintonium's active, pack-tested jcpp path and the two proven techniques
`[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/preprocessor/JcppProcessor.java]`
(PD §7.2), with the
strict-version correction demanded by the loader-independent Oculus PB-03 record (OD §4.2).

### 4.6 Properties-safe preprocessing and parsing

Shader sources and Java-properties text do not share a lexer. `PropertiesPreprocessor` therefore
uses jcpp for conditionals/macros through a lossless adapter:

1. parse physical lines into Java-properties logical lines without trimming;
2. classify only a leading `#` followed by a supported preprocessor keyword as a directive;
3. classify an ordinary leading `#`/`!` as a property comment;
4. replace every data-line `#`, backslash, and adapter-sensitive literal with an
   invocation-scoped protected token after rejecting marker spoofing;
5. run jcpp with A–G macros and comment retention;
6. restore protected literals before Java-Properties key/value unescaping;
7. report a malformed preprocessor directive and ignore only that logical line.

Thus `url=https://host/path#fragment`, color strings, comments inside a value, escaped separators,
continuations, and leading/trailing value whitespace survive. Unlike the observed Pintonium/Oculus
paths, no `trim()` or `replace("#","")` is allowed
`[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/preprocessor/PropertiesPreprocessor.java]`
`[V:observed — Oculus reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/preprocessor/PropertiesPreprocessor.java; loader-independent]`.

The complete preprocessed property stream—not an original unpreprocessed copy—is the semantic
input for flags, profiles, screens, sliders, textures, expressions, and program state. Original
source spans remain alongside parsed fields solely for diagnostics. Duplicate keys follow Java
Properties last-value behavior but produce a location-rich warning; texture `.0`–`.9`
discriminators avoid accidental duplicates.

### 4.7 Directive scanning and requirement aggregation

The scanner is table-driven:

- declaration recognizer: uniform/attribute declarations and geometry extension;
- const recognizer: typed scalar/vector constants with exact identifier aliases;
- block-comment recognizer: `/* KEY:value */`;
- line-comment recognizer: `// KEY:value`;
- routing recognizer: `DRAWBUFFERS` at `v0.1`; `RENDERTARGETS` recognition and precedence at
  `post-v0.5`.

Each recognized key maps to one typed parser, one target field, one validation rule, and one test
ID from §3.3. There are no hand-written setter chains where adjacent half-life fields can be
cross-wired. Duplicate scalar directives use last active source occurrence and diagnose the prior
location. Resource minima aggregate monotonically across active sources; explicit per-program
state remains per program. Format conflicts are retained as diagnostics plus deterministic
last-active occurrence, not silently merged.

Stage is part of the table key: extended attributes and `countInstances` populate requirements only
from `.vsh`, and the legacy geometry pair only from `.gsh`. A recognized directive in any other
stage is ignored and emits one source-attributed warning; it cannot alter a previously valid value.
The geometry recognizer retains exact extension/declaration spans as `LegacyGeometryRewriteSite`;
it does not choose topology or other translation policy. Phase 4 owns that strategy and supplies
the plan, while Phase 3 alone performs the source rewrite before publication to GL.

`ResourceRequirements` contains:

- main color/depth and shadow depth/color minima;
- per-colortex format, clear, clear-color, and per-pass mipmap requirements;
- shadow projection/filter/mipmap configuration;
- center-depth readback requirement;
- per-program draw routing, attributes, instance count, and legacy geometry config;
- half-lives in ticks and world constants; and
- noise requirement.

It never allocates anything. Phase 5 consumes the sizing/format/clear subset, Phase 6 the
center-depth/half-life declarations, Phase 8 the shadow subset, Phase 13 the noise enablement and
resolution, and Phase 4/7/10 per-program execution data.

Its public immutable algebra is:

```java
public record ResourceRequirements(
    BufferMinima minima,
    Map<ColorAttachmentKey, ColorAttachmentRequirement> colorAttachments,
    ShadowRequirements shadow,
    CenterDepthRequirements centerDepth,
    Map<ProgramRequirementKey, ProgramRequirements> programs,
    SmoothingConstants smoothing,
    WorldRenderConstants world,
    NoiseRequirement noise) {}
public record BufferMinima(int colorBuffers, int mainDepthTextures,
    int shadowDepthBuffers, int shadowColorBuffers) {}
public record ColorAttachmentKey(int colortexIndex) {}
public record ColorAttachmentRequirement(ColorInternalFormat format, boolean clear,
    Vec4f clearColor) {}
public record ProgramRequirementKey(DimensionKey dimension, String programName) {}
public record ProgramRequirements(DrawRouting routing,
    Set<ColorAttachmentKey> mipmappedAfterPass, VertexRequirements vertices,
    int instanceCount, Optional<LegacyGeometryConfig> legacyGeometry) {}
public record ShadowRequirements(int resolution, float fov, float distance,
    float distanceRenderMultiplier, float intervalSize,
    Set<ShadowTextureKey> mipmapped, Set<ShadowTextureKey> nearest,
    Set<ShadowDepthKey> hardwarePcf) {}
public record CenterDepthRequirements(boolean required) {}
public record SmoothingConstants(float wetnessHalfLifeTicks, float drynessHalfLifeTicks,
    float eyeBrightnessHalfLifeTicks, float centerDepthHalfLifeTicks) {}
public record WorldRenderConstants(float sunPathRotation, float ambientOcclusionLevel,
    int superSamplingLevel) {}
public record NoiseRequirement(boolean enabled, int resolution) {}
```

`ShadowTextureKey` is closed over depth 0/1 and color 0/1; `ShadowDepthKey` over depth 0/1.
`VertexRequirements` is an immutable set over the closed
`MC_ENTITY`, `MC_MID_TEX_COORD`, and `AT_TANGENT` enum. `DrawRouting` is the ordered attachment-or-
`NONE` sequence from the winning `DRAWBUFFERS`/`RENDERTARGETS` occurrence. `ColorInternalFormat`
is the closed accepted token domain exercised by `directive_colortexFormatDomainAcceptedRejected`;
`Vec4f` contains four finite floats.

`ColorAttachmentKey` accepts indices 0…15; `ProgramRequirementKey.programName` is the exact,
non-empty pack-facing program name and the dimension disambiguates overrides. Both maps iterate in
ascending dimension ID, then program name, or ascending attachment index; sets iterate in enum or
attachment-index order. All maps, sets, and routing lists are immutable and reject duplicate keys.
Every aggregate/default-valued top-level record is present. Absence is represented only by an
empty collection, `Optional.empty()` for no legacy geometry pair, or an explicit typed baseline
value; no `null` or sentinel key/value is public. The baseline is zero minima, no attachment or
program entries, all feature bit sets empty, center-depth and noise disabled, noise resolution
256, shadow interval 2.0, instance count 1 when a program entry is created, and otherwise the
directive-family defaults fixed by the corresponding §3.3 row. Directive bounds, units, aliases,
and winning-occurrence rules are normative at §3.3 and §4.7; malformed occurrences retain the
baseline/prior value as stated there. Builders and mutable accumulators remain private.

The load-time declaration recognizer above answers only resource-requirement questions. Every
successful `materialize` also runs a distinct token-grammar declaration pass over the final
post-jcpp, post-contribution, post-geometry-rewrite stream. It recognizes every top-level
default-block `uniform` declarator, including comma-separated names, arrays, and named/inline
struct shapes; it excludes uniform-block declarations and block members. Inactive conditional
branches and comments are already absent. The exact identifier token supplies the source-map
location, and the root supplies `ShaderSourceStage`. One immutable `DeclaredUniform` is retained
per declarator in final token order, so repeated declarations are evidence rather than silently
collapsed.

The scanner canonicalizes each declared type into the closed structural algebra in §2.2. A
syntactically active default-block declaration that cannot be parsed, source-attributed, or
represented makes that root `Unavailable` with an attributed diagnostic; Phase 3 never publishes
an incomplete catalog. This is declaration metadata, not post-link liveness: an optimized-out
uniform remains in the catalog, and Phase 1's `UniformLocation.isAbsent()` remains authoritative
after link. Neither Phase 4 nor Phase 6 may reopen materialized source to reconstruct this data.

Malformed directive handling is uniform: emit `WARN/LOG_ONLY` on
`schmaloogium.preprocess`, ignore the occurrence, retain the prior/default value, and continue.
No malformed directive aborts a pack.

### 4.8 `ShaderPropertiesModel`

The parser dispatches by exact key/prefix to immutable builders. Unknown keys are preserved in an
`UnknownProperty` list and debug-logged, enabling future contract growth without data loss.

`MinimumEditionRule` compares normalized MC version keys and edition tokens supplied in
`RuntimeIdentityData`. An unmet rule produces `CompatibilityStatus.REQUIRES_NEWER_EDITION`;
parsing completes so Phase 12 can show the warning and Phase 7 can keep shaders off. Malformed
rules warn/ignore only that rule.

Custom texture specs preserve stage, sampler, duplicate suffix, source kind, typed raw format
values, and sidecar location. The accepted internal formats are exactly the 37 names in RESEARCH
Appendix B.4; pixel formats are `RED`, `RG`, `RGB`, `BGR`, `RGBA`, `BGRA` and their `_INTEGER`
forms; pixel types are `BYTE`, `SHORT`, `INT`, `HALF_FLOAT`, `FLOAT`, `UNSIGNED_BYTE`,
`UNSIGNED_BYTE_3_3_2`, `UNSIGNED_BYTE_2_3_3_REV`, `UNSIGNED_SHORT`,
`UNSIGNED_SHORT_5_6_5`, `UNSIGNED_SHORT_5_6_5_REV`, `UNSIGNED_SHORT_4_4_4_4`,
`UNSIGNED_SHORT_4_4_4_4_REV`, `UNSIGNED_SHORT_5_5_5_1`,
`UNSIGNED_SHORT_1_5_5_5_REV`, `UNSIGNED_INT`, `UNSIGNED_INT_8_8_8_8`,
`UNSIGNED_INT_8_8_8_8_REV`, `UNSIGNED_INT_10_10_10_2`, and
`UNSIGNED_INT_2_10_10_10_REV`. An integer internal format requires the corresponding
`*_INTEGER` pixel format; either mismatch is diagnosed and that texture line is ignored.
Unknown format/type tokens receive the same disposition. Colortex format directives accept the
same 37-value internal-format enum. They never open, decode, or realize a texture. Custom uniform/variable
declarations validate key structure/type/name and preserve the expression after Java-Properties
unescaping; they never invoke Phase 11 grammar.

Profile includes use a recursion stack and return a partial valid profile with the cyclic edge
ignored and diagnosed. Screen models preserve entry order and validate references without
constructing widgets. `*` expansion is intentionally deferred to Phase 12 because it depends on
placement across screens.

`ProfileModel.infer(OptionState)` orders valid profiles by descending expanded constraint count
and then source order, returns the first whose option constraints exactly match the current
values, and returns the pack-facing `Custom` outcome when none matches. Disabled-program tokens do
not relax option matching.

`program.*.enabled` is not the full custom-expression language. Phase 3 parses the small Boolean
switch grammar required before Phase 11 exists. Unknown switch names make the condition false with
a warning, so Phase 4 receives a deterministic enabled/disabled value and can apply the backup
chain.

`ProgramStateModel` is the immutable aggregate `Map<ProgramKey, ProgramState> programs`.
`ProgramKey(dimensionId, programName)` uses the same dimension/name validity and ascending iteration
order as `ProgramRequirementKey`. `ProgramState` is
`(Optional<AlphaTestSpec> alphaTest, Optional<BlendSpec> blend,
Optional<ViewportScale> scale, Map<FlipBufferKey, FlipOverride> flips,
Optional<ProgramEnabledExpression> enabledExpression)`. `FlipBufferKey` is either a
`ColorAttachmentKey` or the exact validated virtual `*_pre` pack name; it orders attachment keys
by index before virtual names lexically. `FlipOverride` is the closed `TRUE`/`FALSE` enum.
`AlphaTestSpec` is `Off` or `Enabled(AlphaFunction, finiteRef)`, where `AlphaFunction` is exactly
`NEVER`, `LESS`, `EQUAL`, `LEQUAL`, `GREATER`, `NOTEQUAL`, `GEQUAL`, or `ALWAYS`.
`BlendSpec` is `Off` or `Enabled(srcColor,dstColor,Optional<(srcAlpha,dstAlpha)>)`; each factor is
exactly `ZERO`, `ONE`, `SRC_COLOR`, `ONE_MINUS_SRC_COLOR`, `DST_COLOR`,
`ONE_MINUS_DST_COLOR`, `SRC_ALPHA`, `ONE_MINUS_SRC_ALPHA`, `DST_ALPHA`,
`ONE_MINUS_DST_ALPHA`, `CONSTANT_COLOR`, `ONE_MINUS_CONSTANT_COLOR`, `CONSTANT_ALPHA`,
`ONE_MINUS_CONSTANT_ALPHA`, or `SRC_ALPHA_SATURATE`. Missing alpha factors reuse the color pair.
The `Off` variants are distinct from absence. All final collections are immutable with unique keys.

Properties are processed in file order; for the same exact key, the last valid occurrence wins,
while a malformed occurrence warns and leaves the prior value intact. No matching property means
`Optional.empty()`, an empty flip map, and property enablement `true`. A created program entry
therefore retains only explicit property data; iteration is by `ProgramKey`.
`evaluate(OptionState, Optional<ProfileName>)` returns an immutable `EvaluatedProgramStates`: the
selected valid profile's expanded `!program.*` set becomes `profileDisabled` (empty when no
profile is selected), and each program is enabled iff its property expression evaluates true and
its key is not profile-disabled. Dimension-qualified profile tokens affect only that dimension;
unqualified tokens apply to every matching program name. The evaluated key set is the union of
explicit property keys and expanded profile-disabled keys, so a profile-only disable is never
lost; its otherwise absent `ProgramState` uses the baselines above. Unknown profile/program
references warn and have no effect. Profile expansion/cycle and expression-error rules remain
those above.

Phase 4 receives ordered `EvaluatedProgramState` records containing the key, typed alpha/blend/
scale values, property-enabled result, profile-disabled bit, and final enabled result; final false
means absent to its backup chain. Phase 5 receives only the ordered
`Map<ProgramKey, Map<FlipBufferKey, FlipOverride>>`; absence means no explicit flip override.
Neither view defaults render state beyond these stated absence results.

### 4.9 Schema-versioned ID-mapping input

`PackConfiguration.idMappings()` is the schema-v2 `IdMappingInput` in §2.2, not a flattened list.
Its four `IdMappingFileInput`s have exactly matching `MappingKind`s and immutable source order.
For pack input, paths are exactly `shaders/block.properties`, `shaders/item.properties`,
`shaders/entity.properties`, and `shaders/block.properties`'s `layer.*` family: block rules and
layer rules therefore share bytes/origin but retain independent kind/state/result fingerprints.
The published `parserEnvironment` is the immutable, loader-neutral Standard-Macro A–G environment
used for those parses, including the ordinary integer `MC_VERSION`; it contains no option macro,
pack handle, parser object, Minecraft type, or mutable map.

`IdMappingParser.parse` is the one pure operation used both by `PackFrontEnd.load` and by Phase 9
for a bounded mod contribution. `source=Optional.empty()` returns `ABSENT`, two empty rule lists,
and the canonical absent fingerprint without preprocessing or a diagnostic. A present byte source
is decoded as ISO-8859-1, properties-safe preprocessed, and parsed under the supplied environment.
The parser defensively copies `ImmutableBytes`, applies Phase 3's finite source/line/token/
diagnostic limits, returns immutable data, and catches every decode/preprocess/parse failure as an
attributed diagnostic plus a valid empty result; it retains no caller storage. Phase 9 supplies
only already containment-checked bounded bytes, an origin, the mapping kind, the exact
`IdMappingInput.parserEnvironment`, and a reporter. It neither reconstructs macros nor parses Java
Properties syntax.

`MappingFileState` is derived solely from the ordinary parse: no bytes is `ABSENT`; present bytes
with zero valid ordinary rules is `PRESENT_EMPTY`; present bytes with at least one valid ordinary
rule is `PRESENT_RULES`. Whitespace/comments, a conditionally empty file, and a file whose every
line is invalid are all present-empty. State never depends on the forced parse, so a present entity
file with no ordinary rule and one forced rule remains `PRESENT_EMPTY`—the distinction Phase 9
needs for its conditional retry. Block, item, and layer inputs always have an empty
`forced11300Rules` list.

For a present `ENTITY` request the parser runs twice from the same copied bytes. The ordinary run
uses `parserEnvironment` unchanged. The alternate run makes a private copy and replaces exactly
the `MC_VERSION` macro value with integer `11300`; every other macro, parser option, source byte,
origin, and bound is identical. The results are never merged or selected in Phase 3. Ordinary
rules carry `MappingEra.CLASSIC`; alternate rules carry `MappingEra.MODERN`. An invalid alternate
run is diagnosed and represented by an empty alternate list without changing the ordinary result.
For an absent entity file neither run occurs. `IdMappingFileFingerprint` hashes schema version,
kind, state, origin identity, exact source bytes/presence, canonical parser environment, both
ordered rule lists, and diagnostics that affect rule acceptance; this Phase-3 fingerprint does not
encode which list Phase 9 later selects.

One property line may contain multiple selector tokens. The parser emits one `IdRule` or
`LayerRule` per selector occurrence, with `selectorOrdinal` preserving left-to-right order after
`sourceLine`; rules never combine entries and tags in one opaque collection. An ordinary short,
namespaced, property-bearing, or legacy numeric selector is `SelectorKind.ENTRY`. A token beginning
with exactly one `%` must contain a valid lower-case short name or `namespace:path`; the parser
removes `%`, canonicalizes a short name to the `minecraft` namespace, and publishes it as
`SelectorKind.TAG` without resolving membership. Empty, doubled-`%`, malformed, property-bearing,
metadata-bearing, or numeric tag tokens warn and contribute no rule. Phase 9 alone expands tags
through its 1.12 shim.

`IdRule` retains the substitute full signed integer shader ID, canonical unresolved selector,
optional legacy metadata, ordered property predicates, era, origin, line, and selector ordinal.
`LayerRule` carries the same selector/provenance fields and exactly one of `SOLID`, `CUTOUT`,
`CUTOUT_MIPPED`, or `TRANSLUCENT`. Unknown namespaces/names are not errors here. Invalid numeric
keys, malformed/overflowing predicates, or invalid layer names warn and omit only that selector;
the parser never drops a bad predicate and broadens the match. Solid-opaque-cube exclusion remains
a Phase 9 resolution constraint because Phase 3 has no registry snapshot. Phase 9 owns ordinary/
alternate selection, pack/mod/entry/tag precedence, registry lookup, unknown-name diagnostics, and
aliases; Phase 7 owns render-layer dispatch.

Each `PropertyPredicate` is one parsed `property=value` constraint in source order.
`propertyName` is the non-empty lower-case registry property identifier; `acceptedValues` is the
non-empty, immutable, comma-order list of distinct non-empty value tokens after grammar whitespace
is removed. A predicate matches only when the resolved state exposes that property and its
canonical value equals one listed token; predicates on a rule are ANDed. Invalid names, empty or
duplicate values, an empty list, or duplicate property names within one selector invalidate that
selector as described above; Phase 3 never publishes a predicate it cannot interpret this way.

`MappingOrigin` is closed. Pack loading alone constructs
`PackMappingOrigin(selected PackIdentity, normalized mapping-file path)`. Phase 9 alone constructs
`ModMappingOrigin(non-empty canonical modId, non-negative contributionOrdinal, non-empty sanitized
sourceName)`; the ordinal is its stable order within that mod's contribution snapshot. Null,
malformed, or out-of-range components make the parse request invalid and yield an empty diagnosed
result. Record equality is origin identity; diagnostics render the pack/path or mod/source name.
Phase 9 uses the closed origin variant, `modId`, and ordinal as precedence inputs under its own
policy—Phase 3 assigns no pack/mod or inter-mod precedence and does not compare origins.

### 4.10 Validation, fingerprints, debug dump, and publication

Validation has three levels:

1. field validation already applied per line;
2. cross-model validation (screen/profile references, texture uniqueness, option ambiguity,
   source availability, dimension modes, requirement bounds);
3. structural validation (safe root, readable source index, at least one usable base or explicit
   dimension mode, bounded resource use).

Only level 3 may fail the pack load. Levels 1–2 produce defaults/partial models and diagnostics.
The immutable configuration fingerprint hashes pack bytes, normalized paths, active options,
load-time macro policy, capability identity fields, parser schema version, and the canonical
schema-v2 `IdMappingInput` including every file state and ordinary/forced rule-list fingerprint. It excludes
downstream macro contributions and geometry plans; each `MaterializedSource` fingerprint includes
the contribution and canonical plan actually used. It also includes the deterministic canonical
encoding of `CanonicalDeclaredUniformPayload(schemaVersion, declarations)`: fixed field order,
declaration token order, and exact record/variant/enum names as tags. Every scalar is its canonical
base-10 ASCII form or UTF-8 string, prefixed by its decimal byte length and `:`; every list is
prefixed by its decimal element count and `[`, with length-prefixed elements, then `]`. The
encoding excludes `DeclaredUniformCatalog.materialization`. A parser/schema change therefore
cannot reuse a stale linked layout even when emitted source text is equal, and no fingerprint
input contains the result. Phase 3 hashes these inputs first and then embeds the one result in the
materialized source and catalog. The configuration fingerprint is the reload/change detector.

When `schmaloogium.debug.saveSources` is true, the materializer writes final sources plus a
source-ID manifest under a sanitized runtime `shaderpacks/debug/<pack-id>/` tree. A dump failure
warns and does not change the configuration. Dumps are local developer artifacts containing pack
source and must never be committed, placed in conformance goldens, or uploaded as reports.

No mutable builder, `Path` rooted inside a pack, zip filesystem, reader, or output stream is
reachable from the published configuration.

## 5. Cross-phase interfaces

### 5.1 Exposed interfaces and data contracts

The following are the complete Phase 3 publication surface. Every consumer receives the same
`PackConfiguration` instance (or a versioned replacement), not a parallel parser result.

| Exposed contract | Content | Consumer(s) |
|---|---|---|
| `PackFrontEnd.discover` / `PackDiscoveryRequest` / `PackDiscoveryResult` / `PackCandidate` | deterministic immutable discovery snapshot with opaque IDs, display/status data, and attributed diagnostics | Phase 7 bootstrap/reload; Phase 12 selection UI |
| `PackFrontEnd` / `PackLoadRequest` / `PackLoadResult` | atomic entry point; `OFF` is an explicit successful no-configuration result | Phase 7 bootstrap/reload; Phase 12 selection |
| `PackConfiguration` | single validated downstream truth, immutable and fingerprinted | Phases 4–13 as listed below |
| `PackIdentity`, `CompatibilityStatus`, `DimensionConfiguration` | selected source, `OFF`/`(internal)`, base/override/disabled dimension state | Phases 7, 12 |
| `SourceCatalog`, `SourceKey`, `MaterializedSource`, `SourceMap`, `SourceMaterializer`, `MaterializationResult`, `LegacyGeometryRewriteSite`, `GeometryTranslationRequest`, `GeometryTranslationPlan` | load-time analysis records attributed legacy pairs without translation or a request; after publication Phase 4 selects `None` for a root without a pair or `Translate(plan)` for one with a pair. A plan is `(root, closed input primitive, closed output primitive, positive maxVertices)` and must match that pair; `None` succeeds only without a pair, while a pair without `Translate` is `Unavailable`. Phase 3 validates the request, emits only the fixed core layout rewrite, and fingerprints the contribution, plan, and sites in the materialized source rather than the configuration | Phase 4; Phase 2 harness |
| `DeclaredUniformCatalog`, `CanonicalDeclaredUniformPayload`, `DeclaredUniform`, `DeclaredGlslType`, `ShaderSourceStage`, attributed locations | every successful `MaterializedSource` carries every final active default-block declarator in token order: exact name, closed structural GLSL type, declaring source stage, identifier location, and a catalog fingerprint exactly equal to the materialized-source fingerprint. Phase 3 deterministically encodes the schema-versioned ordered declaration payload without the catalog's `materialization` field, hashes it with the other materialization inputs, then embeds the completed result in both places. Repeated declarations remain represented; uniform blocks are excluded; optimized-out activity is not claimed. An unrepresentable active declaration makes materialization unavailable rather than publishing a partial catalog | Phase 4 merges linked stages; Phase 6 consumes the resulting Phase 4 layout |
| `MacroConfiguration`, `MacroContributor`, `MacroContribution`, reserved `phase6.centerDepthSmoothRedirect` slot | Phase 6 returns `Empty` or one validated `DefineCenterDepthSmooth(replacementTokens)`; Phase 4 passes that singular value to materialization | Phase 6 contributor; Phase 4 materialization; G8/S3 |
| `OptionConfiguration` (`OptionCatalog`, immutable `OptionState`, profiles/screens/sliders/lang) | source options and organization model; absent screen columns resolve to 2 and widen only above 18 actual options, excluding navigation/layout entries and including options produced when Phase 12 expands deferred `*` | Phase 4 enable/source inputs; Phase 12 GUI/reload |
| `OptionPersistenceCodec`, `GlobalShaderOptionsCodec` | ISO-8859-1 changed-only and global formats | Phase 12 |
| `ShaderPropertiesModel.engineFlags` | all Appendix F.1 raw requested states | behavior owners in §3.1 |
| `ProgramStateModel`, `ProgramKey`, `ProgramState`, `EvaluatedProgramStates` | closed immutable §4.8 aggregate keyed by dimension/program; typed alpha/blend/scale/flip/property-enabled declarations; last-valid-wins parsing; selected-profile disablement combines with property enablement by logical AND. Phase 4 receives the ordered full evaluated view and treats final-disabled programs as absent to its backup chain; Phase 5 receives only the ordered explicit flip map, where absence means no override | Phase 4; Phase 5 flip estate |
| `ResourceRequirements` and the closed §4.7 algebra | immutable typed minima, indexed attachment requirements, shadow/center-depth/noise records, smoothing/world constants, and dimension/program-keyed routing, mipmaps, attributes, instances, and optional legacy geometry; §4.7 defines defaults, absence, and deterministic order | Phase 4: program routing/geometry/instances; Phase 5: minima/attachments/pass mipmaps; Phase 6: center depth/smoothing; Phase 7: routing/instances/world constants; Phase 8: shadow; Phase 10: vertex attributes; Phase 13: noise enablement/resolution |
| `CustomTextureSpec`, `NoiseTextureSpec` | lossless specs only | Phase 13 |
| `CustomExpressionDecl` | typed name + raw expression | Phase 11, then Phase 6 |
| `IdMappingInput`, `IdMappingFileInput`, `IdMappingParser`, mapping rule/state/kind/era/selector types, `PropertyPredicate`, closed `MappingOrigin` variants | schema-v2 per-kind `ABSENT`/`PRESENT_EMPTY`/`PRESENT_RULES`; ordered entry/tag rules and predicates; pack or ordered mod-contribution origin; ordinary and isolated forced-11300 entity results; canonical parser environment and fingerprints. The same pure bounded-byte operation parses Phase-9-provided mod sources without reopening the pack or reconstructing macros | Phase 9; resolved layer result later Phase 7 |
| `InternalPackSource` / `InternalPackSnapshot` / `InternalPackEntry` / `NormalizedPackPath` | stable content identity plus bounded, ordered, directory-aware manifest; defensive byte copies and attributed failure. `NormalizedPackPath.canonicalString()` is the only stable projection: non-empty NFC root-relative `/` grammar, ordered and hashed by its exact UTF-8 bytes; consumers never serialize `toString()` or a host path (`[D-P3-24]`) | Phase 7 supplies content and consumes the projection |

`discover` requires a non-null `shaderpacksDirectory` and `DiagnosticReporter`. It and a
`Filesystem` load derive the directory identity by calling `toAbsolutePath().normalize()`, then
`toRealPath()` (which follows a symlink in the supplied directory path), and requiring the result
to be a readable directory. The resulting real `Path` is the identity and is compared by that
filesystem provider's `Path.equals`; no independent case-folding occurs. Thus relative/absolute
and redundant segment aliases, and symlinks that resolve to the same directory, share an identity,
while provider-distinct real paths remain independent. A nonexistent, non-directory, or unreadable
path, or any absolute/real-path resolution or security failure, is invalid request data:
`discover` returns an immutable result with no filesystem candidates and an attributed diagnostic,
while a `Filesystem` load returns `Failed(INVALID_REQUEST)`; neither throws. The result always
orders `Off` and `Internal` first, then filesystem candidates by §4.1's deterministic order.
`PackCandidateKind` is closed as `OFF`, `INTERNAL`, `DIRECTORY`, or `ARCHIVE`;
`PackCandidateStatus` is closed as `AVAILABLE`, `UNREADABLE`, `UNSAFE`, or `LIMIT_EXCEEDED`. Every
candidate carries a sanitized display name and immutable diagnostic list; display names are never
accepted back as paths. `PackCandidateId` and `DiscoveryGeneration` are opaque engine values. A
filesystem ID is valid only with the latest completed discovery result for the same directory
identity from that `PackFrontEnd` instance; a later completed discovery for that identity
supersedes the generation, so an older ID is stale. Discovery does not expose roots, hashes,
archive leases, or paths. Phase 12 invokes it to populate/refresh selection UI; Phase 7 may invoke
it at bootstrap or reload and must obtain a current result before loading a persisted filesystem
selection.

`PackSelection` is closed: `Off`, `Internal`, or
`Filesystem(PackCandidateId candidate)`, where the opaque candidate ID must have come from the
current deterministic discovery result for `shaderpacksDirectory`; stale/unknown IDs fail as
`INVALID_SELECTION`. `load` validates `selection` first. `Off` immediately returns
`PackLoadResult.Off` without accessing or validating any other request field. `Internal` ignores
`shaderpacksDirectory` and requires `internalPackSource`; `Filesystem` validates the host directory
and discovery generation and never accesses `internalPackSource`. Every non-`Off` request requires
non-null identity, capabilities, engine options, and diagnostics. Null or structurally invalid
required request data returns `Failed(INVALID_REQUEST)` rather than throwing.

`RuntimeIdentityData` is the immutable `(mcMajor, mcMinor, mcPatch, engineEdition, engineVersion,
osFamily, perPackIdentityOverrides)` tuple. Version components are non-negative, edition/version
are non-empty sanitized strings, `osFamily` is the closed macro OS family with `OTHER`, and
overrides are the validated add/suppress/force map described in §4.4; invalid values fail the
request and absent overrides become an empty map. `EngineOptionData` is an immutable stable-order
map of the decoded global `optionsshaders.txt`-equivalent string values; absent input is empty,
unknown safe keys are retained for round-trip with a warning, and malformed entries are ignored
with a warning before typed defaults from §3.1 are applied.

`PackInputLimits(maxEntries, maxTotalBytes, maxPathLength, maxNestingDepth)` contains strictly
positive Phase-3-owned bounds shared by folder, archive, and internal inputs. Phase 3 constructs
the limits from its configured finite policy and passes them to `snapshot`; Phase 7 must stop
before exceeding any bound, and Phase 3 independently recounts and rejects an over-limit snapshot.
`InternalPackSource.identity()` and `snapshot(limits)` are called only for `Internal`; provider
exceptions, unstable identity, malformed manifests, and limit violations become
`INTERNAL_SOURCE_INVALID`, never an unchecked exception.

`PackLoadFailure` contains a closed `code`, sanitized user-facing summary, and immutable
diagnostic IDs; it never exposes a provider exception or partial configuration. Codes are
`INVALID_REQUEST`, `INVALID_SELECTION`, `INPUT_UNREADABLE`, `INPUT_UNSAFE`,
`INPUT_LIMIT_EXCEEDED`, `INTERNAL_SOURCE_INVALID`, `STRUCTURALLY_UNUSABLE`, and
`UNEXPECTED_INTERNAL`. The detailed cause is reported through `DiagnosticReporter`; every failure
returns exactly one `Failed` and leaves callers in shaders-off state.

Consumers must not re-open the pack, rescan directives, reinterpret properties, or bypass the
materializer. A reload publishes a new configuration. A consumer may retain derived state only
when both `schemaVersion` and configuration fingerprint equal the values used to derive it; state
derived from materialized text must also match that source's materialization fingerprint.

### 5.2 Consumed Phase 1 contracts

| Phase 1 §5 contract | Use here |
|---|---|
| `:engine` layout, package rules, C-1…C-4 seam | all Phase 3 production code and tests |
| `GLCapabilityProfile` | entire GL-side input to macro construction: GL/GLSL version, vendor, renderer, extensions |
| `Log`, `Logs`, fixed channels | `schmaloogium.pack`, `.preprocess`, `.config` |
| `EngineDiagnostic` / `DiagnosticReporter` | loader-neutral warnings and pack-level failures |
| `schmaloogium.debug.saveSources` | opt-in processed-source dump |
| SPDX/`THIRD-PARTY.md` mechanism | jcpp notice and any future LGPL-incorporation accounting |
| `:conformance` extension point | headless golden/materialization runs |

No GL service or handle is consumed.

### 5.3 Interface/version discipline

`PackFrontEnd.CURRENT_SCHEMA_VERSION` is `2`, and every configuration produced by this revision
publishes that value. A consumer supports exactly `schemaVersion == CURRENT_SCHEMA_VERSION`; every
other value is rejected before derived state is created or retained. The schema is separate from
the content fingerprint. Any change to the published `PackConfiguration` record-component set,
including an added component, requires incrementing the constant and consumer compatibility tests.
Changing a component's meaning or executable default, or removing one, has the same requirement.
This rule may be relaxed only after defining a shape-stable extension mechanism with exact absence
and default semantics. Collection order is deterministic and exposed as immutable insertion order
where pack order matters. Enums intended for forward-compatible storage include `UNKNOWN`, which
is never a silently executable state. Closed executable enums reject unknown values; in particular,
`GeometryInputPrimitive` and `GeometryOutputPrimitive` contain only their declared primitive sets.
`IdMappingInput.schemaVersion` must equal its containing `PackConfiguration.schemaVersion`; Phase 9
rejects a mismatch before parsing mod bytes or building aliases. Version 1 configurations are
incompatible with the R9-1 surface and are never upgraded by inference. Closing the already-named
`NormalizedPackPath` value with its canonical string projection does not add or reinterpret a
`PackConfiguration` component and therefore does not increment schema version 2. Any future change
to that path grammar is still an interface-breaking change.

### 5.4 Requested changes to the dependency contract

Phase 1 §5 does not expose a dependency-declaration/notice contract for a new pure-`:engine`
library. Request a Phase 1 fix-up to expose and implement the following before Phase 3 code lands:

1. allow/pin `org.anarres:jcpp` for `:engine` at the implementation-selected verified version;
2. record Apache-2.0 attribution in `THIRD-PARTY.md`; and
3. ensure the engine seam test permits this pure-JVM library while still rejecting
   Minecraft/Forge/Cleanroom/Mixin/LWJGL dependencies.

No new Phase 1 runtime interface is assumed. `RuntimeIdentityData` and `InternalPackSource` are
Phase 3 interfaces supplied by later `:mod` work as plain data.

## 6. Failure modes & degradation

| Failure | Degradation and diagnostic | G2.4 rung |
|---|---|---:|
| Unknown/malformed directive, property, profile token, screen entry, ID rule, texture spec, or persisted option line | warn on the appropriate Phase 1 channel; ignore only that line/edge/occurrence; retain prior/default value; **never abort the pack** | local parse rule supporting rung 5 |
| Missing include, include depth >10, cycle, bad source encoding, spoofed marker, invalid `#version` | mark affected source roots unavailable with attributed diagnostics; unrelated roots/config remain | hands Phase 4 a rung-3 program failure |
| Ambiguous option | disable that option only and retain diagnostic/locations | feature-local, rung 2a analogue |
| Cyclic profile/subscreen | ignore cyclic edge; retain non-cyclic entries | feature-local, rung 2a |
| Invalid custom uniform expression text at runtime | Phase 3 preserves it; Phase 11 disables that custom uniform only | rung 1, later owner |
| Unmet `version.<mcver>` | configuration remains inspectable with incompatible status; Phase 7 keeps the pack off and Phase 12 displays warning | rung 4 |
| Unreadable/corrupt zip, unsafe root/path, expansion-limit breach, no usable shaders root | return `PackLoadResult.Failed(failure)`, close all resources, select shaders-off, chat/log via loader routing | rung 4→5 |
| Debug-dump/persistence write failure | warn; retain in-memory configuration/state; never turn pack off | rung 2a |
| Unexpected parser/library exception | catch at front-end boundary, close lease, emit sanitized pack failure with cause in log, return shaders-off | rung 5 |

“Structurally unusable” is deliberately narrow: the pack cannot be bounded/read safely or cannot
provide any usable source configuration. A malformed directive or property line never qualifies.

## 7. Threading & performance notes

- Discovery, archive reads, decoding, graph work, jcpp, parsing, validation, hashing, and debug
  dumping may run off the render thread. `PackFrontEnd.load` is synchronous from the caller's
  perspective but has no GL affinity.
- One load transaction owns its filesystem/archive lease. No static mutable zip filesystem or
  process-global preprocessor exists.
- `PackConfiguration`, source nodes, option state, and parsed models are deeply immutable and safe
  to publish across threads. Publication/replacement is the caller's atomic reference operation.
- Persistence writes are serialized per pack identity; a writer never mutates the active
  configuration.
- Each file is decoded and include-scanned once. WCC is `O(V+E)`. Expansion and preprocessing are
  cached by content/option/macro fingerprint with bounded entries/bytes; no cache key holds a pack
  filesystem.
- jcpp instances are not shared. Macro maps and source maps are immutable inputs/results.
- Directive scanning is a single pass over active load-time analysis text with precompiled patterns and
  a table lookup; no whole-pack repeated regex sweep per directive.
- Debug dumping is opt-in and excluded from hot reload timings. It may contain pack source and is
  never a derived conformance artifact.
- Limits for archive entries, uncompressed bytes, source bytes, graph nodes/edges, line length,
  macro count, and diagnostic count prevent zip bombs and adversarial allocation. Crossing a
  structural bound follows §6 rather than exhausting the client.

## 8. Testability plan

All tests are headless `:engine` tests unless explicitly assigned to `:conformance`. They use
temporary directories/zip files and Phase 1 recorded `GLCapabilityProfile` fixtures; no GL context
or Minecraft type is needed.

### 8.1 Unit and property tests

- Discovery/path/lifetime: `discovery_caseFoldThenNatural`,
  `discovery_nestedRootDeterministic`, `discovery_offAndInternal`,
  `discovery_offReturnsSuccessfulNoConfiguration`,
  `discovery_generationSupersessionAndDirectoryIndependence`
  (a later completed discovery through relative/absolute, redundant-segment, or symlink aliases
  supersedes IDs for the same real directory, while distinct real directories remain independent),
  `discovery_directoryIdentityCaseAndFailures`
  (uses provider case semantics and covers nonexistent, non-directory, unreadable, and failed
  absolute/real-path resolution for both operations),
  `load_offIgnoresHostDirectory`
  (null, invalid, and provider-failing host paths still return `Off`),
  `load_internalIgnoresHostDirectory`
  (null, invalid, and provider-failing host paths still load from the internal provider),
  `discovery_staleAndUnknownSelectionInvalid`
  (stale and unknown filesystem IDs return `INVALID_SELECTION`),
  `internalSnapshot_orderDuplicatesLimitsAndEmptyDirectories`,
  `normalizedPackPath_canonicalStringStableUtf8`
  (round-trip canonical strings, NFC rejection, slash grammar, unsigned UTF-8 ordering, and proof
  that `toString()`/host separators never enter identity or serialization),
  `pathRejectsTraversalAbsoluteAndSymlink`, `archiveLeaseClosesOnEveryExit`,
  `archiveBombLimitsFailGracefully`.
- Dimensions: `dimension_overrideNoMergeAndEmptyDisables`,
  `dimension_fullLegacyScanRejectsIrisModel`, `dimension_readsOnlyVshFsh`,
  `dimension_optionsCanUseDistinctNames`.
- Includes/attribution: `include_relativeAttribution`, `include_absoluteAttribution`,
  `include_depthTenAndCycle`, `include_missingAffectsOnlyReachableRoots`,
  `include_graphAllRootsStable`, `preprocess_lineAttribution`.
- Preprocessor/header: `preprocess_allConditionalForms`, `preprocess_definedBothForms`,
  `preprocess_macroSubstitution`, `preprocess_versionExtensionHoist`,
  `preprocess_versionMustLead`, `preprocess_markerSpoofRejected`,
  `preprocess_apiMacrosDoNotShiftLines`, `macro_onDemandExtensionOnly`,
  `macro_mcVersion11202Format`, `macro_vendorRendererOther`,
  `macro_phase6CenterDepthSlot`, `geometryRewrite_deterministicAttributedOrUnavailable`,
  `materialize_noGeometryPairAcceptsNone`, `materialize_geometryPairRejectsNone`,
  `geometryRewrite_allPrimitivePairsAndCanonicalFingerprint`,
  `geometryRewrite_rejectsRootSiteVertexAndRangeMismatch`,
  `load_legacyGeometryPublishesBeforePhase4PlanSelection`.
- Declared uniforms: `declaredUniformCatalog_deadBranchAndCommentsAbsent`,
  `declaredUniformCatalog_defaultBlockOnlyAndCommaDeclaratorsComplete`,
  `declaredUniformCatalog_arraysStructsAndSamplerKindsAreClosed`,
  `declaredUniformCatalog_includeIdentifierHasAttributedLocation`,
  `declaredUniformCatalog_duplicateDeclaratorsRetainTokenOrder`,
  `declaredUniformCatalog_fingerprintEqualsMaterializedSource`,
  `declaredUniformCatalog_fingerprintReconstructsFromPayloadWithoutEmbeddedResult`,
  `declaredUniformCatalog_unrepresentableActiveDeclarationIsUnavailable`, and
  `declaredUniformCatalog_optimizedOutStatusIsNotClaimed`.
- Options/components: `switchOption_onOffAndTooltip`,
  `switchOption_sameFileConfirmation`, `variableOption_defaultAutoAdded`,
  `constOption_completeWhitelistAndAliases`, `optionAmbiguity_conflictingDefaultsDisabled`,
  `optionRefsDoNotCrossWcc`, `optionAmbiguity_duplicateNamesAcrossComponents`,
  `optionRewrite_onlyCapturedSpan`, `optionRewrite_roundTripMaterialization`,
  `profiles_inferenceMatchAndCustom`, `screen_columnsBoundaryMixed18And19Options`
  (asserts absent 18→2, 19→3, 27→3, 28→4 after `*`, and explicit positive `N` wins),
  `loadRequest_selectionDependentValidation`, `internalSnapshot_providerBoundaryFailures`,
  `persistence_outOfListValueRetainedAndWarned`,
  `persistence_packChangedOnlyRoundTripAndApply`, `persistence_globalRoundTripAndApply`.
- Properties safety: `propertyHashRoundTrip`, `properties_whitespaceAndHash`,
  `properties_continuationsEscapesAndComments`, `properties_conditionalScreenLayout`,
  `properties_malformedDirectiveWarns`, `properties_standardMacrosButNoOptionMacros`.
- ID mapping input: `idMap_fileStateAbsentEmptyRulesPerKind` distinguishes missing, blank,
  conditional-empty, all-invalid, and nonempty block/item/entity/layer inputs;
  `idMap_entityForced11300Isolated` proves identical bytes/environment except `MC_VERSION`, no
  absent-file parse, ordinary `CLASSIC`, alternate `MODERN`, independent diagnostics, and no merge;
  `idMap_selectorKindEntryAndTag` covers short/namespaced entries, canonical `%` tags, ordering,
  and every rejected tag form; `idMap_propertyPredicateAlgebraAndRejection` covers OR-within,
  AND-between, missing properties, canonical ordering, and every invalid predicate shape;
  `idMap_modContributionOriginPreserved` feeds defensive bounded
  ISO-8859-1 bytes through the public operation and gets the same canonical output as pack loading;
  `idMap_originVariantsIdentityValidationAndOrdering` covers pack attribution, canonical mod IDs,
  per-mod ordinals, record equality, invalid construction, and leaves precedence evaluation to Phase 9;
  `idMap_fingerprintCoversPresenceEnvironmentAndBothParses` mutates each load-bearing input; and
  `idMap_schemaAndContainingConfigurationMustMatch` rejects v1 and mismatched nested schemas.
- Appendix F tests: every Phase-3-owned test named in §§3.1–3.2 is required; a parameterized key
  manifest fails if a parser/model row lacks a Phase 3 assertion. Behavior-only handoff rows name
  their downstream owner's test and are excluded from the Phase 3 parser manifest.
- Appendix A.3 tests: every test named in §3.3 is required; a generated mapping manifest asserts
  one parser, one field target, and one test ID per row.
- Pintonium pitfall gates are named exactly:
  `directive_drynessWritesDryness` (B1),
  `directive_legacyCommentFormsReachFields` (B2),
  `optionRefsDoNotCrossWcc` (B3), and
  `propertyHashRoundTrip` (B12).
- Validation/publication: `malformedLineNeverAbortsPack`,
  `structuralFailurePublishesNoPartialConfiguration`,
  `packConfigurationIsOnlyLoadOutput`, `fingerprintChangesOnEveryLoadSemanticInput`,
  `materializedFingerprintChangesWithContributionOrGeometryPlan`,
  `materializedFingerprintChangesWithUniformCatalogSchemaOrContent`.
  `schema_currentValuePublished`, `schema_recordComponentChangeBumps`,
  `schema_incompatibleChangeBumps`, `schema_unsupportedVersionRejected`,
  `schemaMismatchInvalidatesRetainedState`.

### 8.2 Golden and matrix tests

Phase 2's runnable-before-renderer harness receives:

1. synthetic, project-authored fixtures for every directive syntax/key and every properties
   grammar edge;
2. manifest-only golden records containing input provenance, expected hashes, normalized
   diagnostics, source-map entries, and `PackConfiguration` summaries—never pack source;
3. recorded capability profiles covering OS/vendor/renderer/extension branches; and
4. download-at-test-time runs over all seven matrix packs.

The Phase 3 implementation gate is:

- all seven matrix packs discover, preprocess, and produce a `PackConfiguration` end-to-end
  without an uncaught error;
- one classic pack's resource requirements are hand-verified against its active directives; and
- SEUS Renewed, Chocapic13 V9, and projectLUMA individually exercise classic block/line-comment
  directives.

Matrix packs are never committed or re-hosted. Golden files contain no source text. Rendered
images remain local/CI-cache artifacts under the Phase 2 policy, and golden regeneration is never
automatic. Debug source dumps are excluded from the harness entirely.

### 8.3 Fuzz and differential tests

Property-line, include-path, directive-token, profile graph, and option-line fuzzers assert:
termination, bounds, no path escape, no uncaught exception, and line-local degradation. A
licensed-reference differential test may compare normalized option/directive results against
Pintonium for mutually supported modern-const cases, but expected behavior for classic comments,
WCC, dimensions, and `#` values comes from RESEARCH/shipped docs, not Pintonium.

## 9. Milestone staging

This is the exhaustive component register. Each designed component has exactly one implementation
milestone.

| ID | Component | Milestone |
|---|---|---|
| P3-C01 | deterministic pack discovery and sentinels | `v0.1` |
| P3-C02 | safe folder/zip input snapshot, canonical normalized-path projection, root/path guards, lease lifecycle | `v0.1` |
| P3-C03 | OF base/dimension source-set model | `v0.1` |
| P3-C04 | source IDs, include graph, WCC, depth/cycle handling | `v0.1` |
| P3-C05 | include expansion and `#line`/source-map attribution | `v0.1` |
| P3-C06 | option discovery, ambiguity, rewrite, profile/screen/slider/lang model | `v0.1` |
| P3-C07 | per-pack/global persistence codecs | `v0.1` |
| P3-C08 | configurable OF-era/feature/`SCHMALOOGIUM` macro environment and Phase 6 slot | `v0.1` |
| P3-C09 | OQ-7 policy finalizer/per-pack experiment results | `post-v0.5` |
| P3-C10 | jcpp shader-source processor with hoisting/spoof guards | `v0.1` |
| P3-C11 | properties-safe jcpp adapter and lossless property parser | `v0.1` |
| P3-C12 | legacy Appendix A.3 directive scanner, excluding `RENDERTARGETS` | `v0.1` |
| P3-C13 | immutable resource-requirement aggregator | `v0.1` |
| P3-C14 | complete Appendix F `ShaderPropertiesModel` | `v0.1` |
| P3-C15 | schema-v2 ID-mapping/layer parser, file-state/selector/era provenance, and forced-11300 entity parse | `v0.1` |
| P3-C16 | source materializer and local processed-source debug dump | `v0.1` |
| P3-C17 | validation, fingerprint, and atomic `PackConfiguration` publication | `v0.1` |
| P3-C18 | logging/loader-neutral diagnostics and degradation adapter | `v0.1` |
| P3-C19 | global `.csh` source recognition/materialization reserved for G8/S2 | `post-v0.5` |
| P3-C20 | headless manifests, fuzz fixtures, and seven-pack front-end harness adapter | `v0.1` |
| P3-C21 | `(internal)` in-memory source-provider bridge with canonical UTF-8 path identities (content remains Phase 7) | `v0.1` |
| P3-C22 | modern `RENDERTARGETS` recognition and source-order precedence | `post-v0.5` |

Later consumers may initially ignore fields, but Phase 3's v0.1 model already preserves all
Appendix A.3/F data. P3-C19 does not change legacy dimension semantics.

## 10. OQ & spike specifications

### 10.1 OQ-7 — identity posture

**Question (verbatim from RESEARCH §11 row OQ-7):**
“Renderer-identity macro + feature-flag posture”

**When:** after v0.1 can load and render the fixed matrix; final decision belongs to G8/S3.

**Procedure:**

1. Freeze identical pack versions, scenes, options, `GLCapabilityProfile`s, and engine build.
2. Use P3-C08's data switch to produce three configurations without code changes:
   - option 1: OF A–H identity, no `IS_IRIS`, no Iris feature macros;
   - option 2: `IS_IRIS` plus an experimental `IRIS_VERSION`, with only explicitly supported
     features;
   - option 3: OF-era identity, honest supported `IRIS_FEATURE_*`, `SCHMALOOGIUM`, and no global
     `IS_IRIS`, plus documented per-pack overrides.
3. For all seven matrix packs, capture processed-source hashes, macros actually queried, branch
   selection traces, compile/fallback diagnostics, and T0/T1 results. For classic packs, also run
   T2 scenes; for dual-spec packs, include camera-path motion scenes.
4. Inspect every branch selected because of an Iris identity/feature macro. Mark whether the
   assumed feature is completely implemented; any unsupported assumed branch is a failure even if
   a still image looks plausible.
5. Repeat option 3 with and without each per-pack override to prove overrides are narrow and
   configuration-only.
6. Record the evidence and final status in RESEARCH §11 and append the chosen policy/result to this
   section; do not silently change defaults.

**Success criteria:** a candidate has no classic-pack regression relative to option 1; no pack
selects an unsupported path; all advertised feature flags correspond to implemented/tested
behavior; and it improves or preserves dual-spec T0/T1 results on the same scenes. Option 3 is
accepted only if its global configuration meets those conditions and overrides are required solely
for named pack exceptions.

**Failure criteria:** `IS_IRIS` or any feature macro causes an unsupported semantic assumption,
classic T1/T2 regression, new compile/fallback failures, pack-specific hidden heuristics, or an
override broad enough to amount to engine impersonation.

**Fallback:** choose option 1 globally—OF-era A–H identity, no `IS_IRIS`, no `IRIS_VERSION`, and
no Iris feature macros—while retaining `SCHMALOOGIUM` only if it is demonstrably inert for the
matrix. The configurable data model and override fields remain, disabled, so G8/S3 can revisit the
decision without a preprocessor rewrite.

This spike does not resolve OQ-7 here. Pintonium's absence of `IRIS_VERSION` is supporting evidence,
not a decision (PD §7.6).

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision and one-line rationale |
|---|---|
| D-P3-1 | Adopt jcpp for conditional/macro processing because Apache-2.0 licensing is verified and Pintonium demonstrates active pack-tested use (`common-shaders/src/main/java/net/irisshaders/iris/shaderpack/preprocessor/JcppProcessor.java`, PD §7.2). |
| D-P3-2 | Adopt a directed include graph/DFS diagnostic structure after checking it against §3.2/App F.8, adding the required depth-10 cap and root-local degradation. |
| D-P3-3 | Adopt Pintonium's marker-hoist and `addMacro` techniques after checking §3.2/§3.5, but require token-aware markers, spoof rejection, first-significant `#version`, and restored `#line` attribution. |
| D-P3-4 | Adopt location-based option rewriting and constraint-count profile precedence after checking App F.3/F.4; captured spans, not global text replacement, define the behavior. |
| D-P3-5 | Implement real WCC analysis but keep App F.3 same-file confirmation authoritative, because Pintonium/Oculus whole-graph confirmation is an observed contract conflict. |
| D-P3-6 | Write a separate lossless properties adapter because App F and the shipped file permit property values containing `#`, while Pintonium strips them and Oculus trims data. |
| D-P3-7 | Make identity/capability macros configurable option-3-shaped data while leaving OQ-7 open, so G8/S3 changes policy rather than architecture. |
| D-P3-8 | Reject Pintonium's Iris dimension mapping after checking §3.1/App F.8; use OF world −128…128, `.vsh`/`.fsh`-only, empty-disables, no-merge behavior. |
| D-P3-9 | Emit extension macros on demand and re-derive GL/GLSL/vendor/renderer values from Phase 1 `GLCapabilityProfile`, because Pintonium's enumerate-all set diverges from the shipped macro contract. |
| D-P3-10 | Use one declarative directive→field registry with one named test per row, because Appendix A.3 and Pintonium B1/B2 show setter chains and dead forms are unacceptable. |
| D-P3-11 | Use Oculus FE-09's deterministic case-insensitive/natural ordering as non-contract discovery structure (`.../ShaderpackDirectoryManager.java`; loader-independent). |
| D-P3-12 | Use Oculus FE-07 only to corroborate include-graph structure after checking §3.2; it supplies no depth, option, or 1.12.2-hook semantics (`.../ShaderPack.java`; loader-independent). |
| D-P3-13 | At post-v0.5, resolve simultaneous active `DRAWBUFFERS`/`RENDERTARGETS` by later source occurrence, adopting Oculus FE-08 only after confirming Appendix A.3 has no contrary precedence and retaining both locations. |
| D-P3-14 | Treat advertised option values as UI guidance, retaining syntactically safe persisted/profile values with a warning, because App F.3 does not define the list as a parser rejection set and Oculus FE-04 records OF parity. |
| D-P3-15 | Reject Oculus FE-01's original-properties semantic split because RESEARCH §3.3 says the properties file itself is preprocessed; original spans are diagnostics only. |
| D-P3-16 | Do not use an AST transformer because GLSL-120 runs natively in the compatibility profile, the fixed contract needs only preprocessing/geometry handling, and the available transformation boundary is prohibited (PD §12). |
| D-P3-17 | Publish only immutable `PackConfiguration` because a single validated downstream source prevents parser/consumer drift and is an explicit Phase 3 architecture requirement. |
| D-P3-18 | Give `shaders.properties` and ID maps Standard Macros A–G but no option macros because the shipped contract states that restriction; shader sources alone receive A–H. |
| D-P3-19 | Snapshot all pack bytes and close the archive before publication because it retains bounded single-lease lifecycle value without exposing a static filesystem or stale handle. |
| D-P3-20 | Store half-life directives as ticks because Appendix A.3 is normative; alternate units belong to Phase 6's explicitly recorded conflict handling, not this parser. |
| D-P3-21 | Keep modern `.csh` recognition as post-v0.5 P3-C19 and `RENDERTARGETS` recognition/precedence as post-v0.5 P3-C22; the v0.1 routing model remains growth-shaped without implementing either modern parser path. |
| D-P3-22 | Capture complete default-block declaration metadata from the final materialized token stream and bind it to that materialization's fingerprint; load-time resource recognition is too early to describe contribution/rewrite results, while post-link introspection cannot supply source attribution or declared-but-optimized-out entries. |
| D-P3-23 | Publish file presence, entry/tag classification, per-rule era, and both isolated entity parses instead of making Phase 9 reopen bytes or infer preprocessing history; this grants R9-1 while keeping registry resolution and branch selection outside Phase 3. |
| D-P3-24 | Make `NormalizedPackPath.canonicalString()` the sole path projection and define it as validated NFC, root-relative, slash-separated grammar ordered/hashed by exact UTF-8 bytes; `toString()` and host paths are never protocol data. |

### 11.2 Binding-decision disposition

D-1/D-2 are preserved by the Cleanroom-exclusive, shaders-only boundary. D-3 controls the seven-pack
gate. D-4 is preserved by growth-shaped source/stage fields and post-v0.5 compute recognition.
D-5 is untouched: this phase has no mixin. D-6 is enforced by pure `:engine` placement. D-7/D-8
govern jcpp/Pintonium/Oculus notices and the transformation prohibition. D-9 supplies compatibility
profile macro semantics without GL calls. D-10 is implemented by the row-complete manifests and
Phase 2 adapter.

### 11.3 Input contradictions and rulings

1. **Properties option macros.** The RC3 Phase 3 sentence parenthesizes “standard macros only” for
   ID maps, which can imply pack option macros are available to `shaders.properties`; the shipped
   `shaders.properties` explicitly says Standard Macros A–G and “Option macros are not available.”
   RESEARCH §3.3 merely requires preprocessing and does not contradict the shipped rule. D-P3-18
   follows the shipped contract and requests upstream clarification below.
2. **Same-file vs whole/component confirmation.** RESEARCH App F.3 says same file; the observed
   Pintonium/Oculus graph path is pack-global because WCC is stubbed. RESEARCH wins. WCC scopes
   merging/ambiguity and never promotes an unconfirmed file.
3. **`#version` hoisting caveat.** The reference technique can accept a misplaced version. The
   contract says the standard header follows `#version`; strict drivers require valid placement.
   This design validates first-significant placement before adopting the hoist.
4. **Dimension semantics/depth cap.** Pintonium's dimension map and uncapped graph differ from
   §3.1/§3.2. The contract behavior is retained.
5. **Half-life units.** Appendix A.3 says ticks. Oculus reports an alternate smoothing-unit
   behavior; Phase 6 owns that conflict. Phase 3 stores the normative ticks unchanged.

### 11.4 Open items and hand-offs

- Phase 6 must decide whether to populate `phase6.centerDepthSmoothRedirect`; the empty slot is
  valid and tested.
- Phase 4 must merge the verified per-stage `DeclaredUniformCatalog`s into one immutable effective
  linked-program layout, reject same-name/different-type conflicts with both source locations, and
  expose only that handle-free layout to Phase 6. Phase 6 must not reopen source or reinterpret
  the Phase 3 type algebra.
- Phase 7 must supply the bounded `InternalPackSource` snapshot, consume only
  `NormalizedPackPath.canonicalString()` for hashing/serialization, combine its owned engine flags
  with higher-priority game settings, and publish new configurations on pack/dimension/reload
  transitions.
- Phase 9 consumes `IdMappingInput`, selects an entity ordinary/forced result under its documented
  present-empty rule, parses bounded mod bytes only through the published operation/environment,
  and owns mod precedence, tag expansion, registry resolution, and aliases.
- Phase 12 must define apply/discard timing and global setting UX without changing the codecs.
- G8/S3 owns OQ-7's final policy after §10's spike.
- G8/S2 consumes P3-C19; it must not alter legacy dimension-file rules silently.

### 11.5 Requested upstream changes

1. Phase 1 fix-up: expose/pin jcpp and its Apache-2.0 notice path as requested in §5.4.
2. RC3/RESEARCH clarification: state explicitly whether `shaders.properties` receives option
   macros. This design follows the shipped A–G-only rule pending an upstream contract change.

## 12. Implementation checklist

Each item is independently actionable and names its test hook.

1. `[v0.1]` Implement P3-C01/P3-C02 discovery, safe root selection, canonical
   `NormalizedPackPath.canonicalString()` validation/projection, archive limits, and snapshot
   lifecycle; run `discovery_generationSupersessionAndDirectoryIndependence`,
   `discovery_directoryIdentityCaseAndFailures`, `discovery_staleAndUnknownSelectionInvalid`, all
   other `discovery_*`, `normalizedPackPath_canonicalStringStableUtf8`, `pathRejects*`, and
   `archiveLease*`.
2. `[v0.1]` Implement P3-C03 source-set/dimension indexing with the full −128…128 scan; run all
   `dimension_*`.
3. `[v0.1]` Implement P3-C04 source IDs/include graph/WCC/depth/cycle diagnostics; run
   `include_depthTenAndCycle`, `include_graphAllRootsStable`, and
   `optionRefsDoNotCrossWcc`.
4. `[v0.1]` Implement P3-C05 expansion and numeric `#line` source maps; run
   `include_*Attribution` and `preprocess_lineAttribution`.
5. `[v0.1]` Apply the Phase 1 dependency fix-up, add jcpp/notice, then implement P3-C10 and the
   attributed legacy-geometry plan rewrite; run all `preprocess_*`, `macro_*`, and
   `geometryRewrite_*` tests.
6. `[v0.1]` Implement P3-C08 macro data, on-demand extensions, per-pack overrides, and the Phase 6
   reserved contributor; run `macro_phase6CenterDepthSlot` and identity snapshots.
7. `[v0.1]` Implement P3-C06 option/const discovery, same-file confirmation, WCC merge,
   ambiguity, span rewrite, profiles/screens/sliders/lang; run every F.3/F.4 named test.
8. `[v0.1]` Implement P3-C07 deterministic ISO-8859-1 persistence and atomic writes; run
   round-trip, out-of-list, traversal, and write-failure tests.
9. `[v0.1]` Implement P3-C11 protected-token property preprocessing, including `#`/backslash/
   whitespace preservation and narrow hash-comment handling; run `propertyHashRoundTrip` and all
   `properties_*`.
10. `[v0.1]` Implement P3-C14's complete Appendix F dispatcher/model; make the manifest fail on
    any Phase-3-owned parser/model row missing from §3.1/§3.2, while retaining downstream-only
    behavior rows as handoff-test obligations.
11. `[v0.1]` Implement P3-C12's table-driven declaration/const/block-comment/line-comment and
    legacy `DRAWBUFFERS` routing scanner; make the v0.1 manifest fail on any missing v0.1 §3.3
    row, including B1/B2 tests. `[post-v0.5]` Implement P3-C22 and enable
    `directive_rendertargetsAndPrecedence`.
12. `[v0.1]` Implement P3-C13 requirement folding and cross-field conflict diagnostics; test one
    hand-verified classic-pack expectation.
13. `[v0.1]` Implement P3-C15's schema-v2 `IdMappingInput`, four per-kind file states, pure bounded-
    byte parser, entry/tag classification, per-rule era, and isolated forced-11300 entity result;
    run all `idMap_*` tests, including long/short/property/legacy/layer and malformed-line cases.
14. `[v0.1]` Implement P3-C16 materialization cache, the complete fingerprint-bound
    `DeclaredUniformCatalog`, and local-only sanitized debug dump; run every
    `declaredUniformCatalog_*` test and assert dumps never enter golden manifests.
15. `[v0.1]` Implement P3-C18 fixed-channel logging and loader-neutral diagnostics; inject failures
    for every §6 row.
16. `[v0.1]` Implement P3-C17 validation/fingerprint/atomic publication and prove
    `PackConfiguration` is the only success output.
17. `[v0.1]` Implement P3-C21's in-memory `(internal)` bridge with a synthetic engine-only pack;
    hash/order/serialize only canonical-string UTF-8 bytes and leave actual content to Phase 7.
18. `[v0.1]` Implement P3-C20 manifest/fuzz/harness adapter, then run the seven downloaded pack
    front-ends and the classic resource-sizing check under Phase 2's rules.
19. `[post-v0.5]` Run §10's OQ-7 spike and record the result before implementing P3-C09's final
    policy.
20. `[post-v0.5]` Implement P3-C19 global `.csh` recognition only when G8/S2 defines its execution
    contract; retain the OF dimension restrictions and existing schema compatibility.

---

*Review round 22 returned literal PASS on the §0.24 surface. The §0.25 maintenance amendment then
changed binding §5, so Phase 3 v1 is **not verified** pending a fresh whole-document review; no
version roll occurs while the loop is open.*
