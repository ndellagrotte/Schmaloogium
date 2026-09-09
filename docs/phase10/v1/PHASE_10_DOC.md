# Schmaloogium — Phase 10: Extended vertex pipeline — Architecture

## 0. Header

**Phase:** 10 — Extended vertex pipeline  
**Document version:** v1, initial build  
**Date:** 2026-09-07  
**Milestone:** v0.3; modern attribute producers remain post-v0.5  
**Governing design:** `docs/design/v3/DESIGN.md`, Part I §§G0–G12
(lines 132–1135), Phase 10 specification (2162–2276), mandatory template
(817–854), and document gate (2266–2270). These coordinates were derived from this
revision's headings, not shifted from another revision.  
**Declared dependencies:** Phases 4, 7, and 9.  
**Assigned questions:** OQ-5 and OQ-14; neither is resolved by this document.  
**Status:** architecture authored; unverified. No implementation, build, test, spike,
verification loop, or adversarial review was performed in this build session.

**Integration fix-up (2026-09-07), unverified:** IR-03/04/05/09/24 amend active
lighting and owner/consumer contracts in §§4.8.1/5/11 (D-P10-11/12). No implementation,
fresh PASS, lighting parity or OQ-5/OQ-14 result is claimed.

### 0.1 Dependency gate and maintainer-authorized exception

The maintainer explicitly authorized consumption of the unverified Phase 9 document,
then extended that exception to the current Phase 4 and Phase 7 rebuilds after their
headers exposed the newer verification obligations. This is a **design-authoring
exception to §G5.3**, not a PASS, implementation approval, or grant of any missing API.

| Input | Current artifact and evidence | Consumption status |
|---|---|---|
| Phase 4 | `docs/phase4/v1/PHASE_4_DOC.md`, §0.35 coordinated rebuild, 2026-09-06; closing status says §5 changed and is unverified | Provisional. `docs/phase4/reviews/PHASE_4_REVIEW_31.md` passes the older §0.34 surface, not this rebuild |
| Phase 7 | `docs/phase7/v1/PHASE_7_DOC.md`, §0.40 coordinated rebuild, 2026-09-06; closing status says §5 changed and is unverified | Provisional. `docs/phase7/reviews/PHASE_7_REVIEW_36.md` predates the rebuild |
| Phase 9 | `docs/phase9/v1/PHASE_9_DOC.md`, initial build, 2026-08-03; no Phase 9 review artifact found | Provisional under the explicit exception |

Phase 10 must be reconciled against the freshly verified versions of all three before
implementation. If reconciliation changes this document's §5, a fresh Phase 10 verify
session is required. An upstream request recorded here is never an existing dependency
contract merely because authoring was authorized.

### 0.2 Inputs actually read

Primary inputs, with paths relative to the repository:

- `docs/design/v3/DESIGN.md`: all Part I, the Phase 10 assignment, front matter,
  and the phase index; no other Part-II phase specification was consumed.
- `docs/research/v1/RESEARCH.md`: §§0–1; §4.6 vertex portion; §7.4;
  Appendix C in full; Appendix E rows 3–9 and their table headings.
- `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` (PD): §§8.2 and 9.
- `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md`: §7 only.
  Only observed layout, population, lifecycle, and drawing behavior is used below;
  no decompiled implementation structure or proprietary identifier is adopted.
- Cleanroom patch root
  `reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/`:
  `BufferBuilder.java.patch`, and `chunk/{RenderChunk,ChunkRenderDispatcher,ChunkRenderWorker}.java.patch`.
- Dependency inputs were read by binding section and necessary incorporated detail,
  rather than front-to-back: Phase 4 §5.1 and §§4.9–4.10 attribute/activation surface;
  Phase 7 §5.1 geometry-invalidation, frame, reload and report declarations, §5.3,
  §5.5, and §§4.10–4.12 hook conventions; Phase 9 §§2.1–2.3, §4.10 and §5,
  together with the generation-retirement and scope-boundary statements.
- Cleanroom MCP `resolve_symbol` / `get_method_signature`, MC 1.12.2:
  every vanilla symbol in §4.11 was resolved, including the chosen worker and upload
  methods missing from Appendix E's class-only dispatcher row. Mapping evidence
  establishes names/descriptors, not bytecode injection cardinality or runtime success.

Load-bearing LGPL source evidence, beneath
`reference-src/pintonium-9c2fcc1/`:

| File | Region used |
|---|---|
| `common/src/main/java/org/embeddedt/embeddium/impl/render/chunk/vertex/format/ChunkVertexExtendedData.java` | Thread-local data, midpoint encoding, tangent formula/packing, mid-block formula, reset values |
| `common/src/main/java/org/embeddedt/embeddium/impl/util/QuadUtil.java` | `calculateNormal` overloads |
| `forge122/src/main/java/org/taumc/celeritas/impl/render/terrain/compile/VintageChunkBuildContext.java` | Layer builders, range metadata, full conversion and its helper context; only normal/midpoint and range-vs-stack evidence is adopted |
| `forge122/src/main/java/org/taumc/celeritas/mixin/core/terrain/RenderGlobalMixin.java` | Attribute index at 72, generic reset/set at 434–460, replacement methods at 175–220, distance-read redirect at 92–95 |
| `forge122/src/main/java/org/taumc/celeritas/CeleritasVintage.java` | `@Mod` and `MODID`, 32–34 |
| `forge122/src/main/resources/mcmod.info` | Actual 1.12.2 mod ID `celeritas` |

### 0.3 Narrow extra reads and reasons

These are explicit deviations from the assigned reading list, closing real gaps:

1. RESEARCH §3.4 item 1, §3.6.5, Appendix D.4, the v0.3/post-v0.5 milestone rows,
   and the exact §11 OQ-5/OQ-14 rows: resolve the attribute/uniform distinction, modern
   slot semantics, implementation gate, and required verbatim spike questions.
2. `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` attribute declarations
   at 115–121 and vertex configuration at 342–348: confirm floating GLSL input types
   and source-driven opt-in. The initial 1–95 read also returned program-table context;
   it supplies no new Phase 10 policy.
3. Cleanroom `WorldVertexBufferUploader.java.patch` and
   `block/model/BakedQuad.java.patch` beneath the renderer patch root, plus
   `reference-src/cleanroom-0.6.6-alpha/src/main/java/net/minecraftforge/client/model/pipeline/LightUtil.java:37–210`:
   bulk-format ownership, Forge preDraw/postDraw delegation, and OQ-14 cache lifetime.
4. `docs/phase1/v14/PHASE_1_DOC.md`: §2.1 package allocation, §4.7.2 capability
   profile, §4.10 bail mechanism, and §5 structural/facade/convention index. The
   assignment explicitly requires Phase 1's bail registry without declaring Phase 1
   as a dependency. This narrow read avoids inventing its interface; proposed changes
   remain requests. No claim of newly verifying that document is made.
5. The latest Phase 4/7 review files named in §0.1, the Phase 9 directory inventory,
   and `docs/MOVES.md:85–146`: establish dependency status and the retirement of
   `verification/targets/` and `scripts/verify`. The phase header is now the governing
   revision declaration; no removed target manifest is recreated or dry-run claimed.
6. Additional MCP symbols for state save/restore, sorting, builder reset/finalization,
   `BlockRendererDispatcher.renderBlock`, and client/VBO chunk-layer replay: necessary
   to cover fluids, translucent resort, and non-VBO display-list playback rather than
   only an immediate Tessellator draw.

`AGENTS.md` was requested but is absent. Forbidden `docs/**/chatlogs/**` and root
`*.txt` inputs were not read. No Oculus evidence was consumed; §G12 assigns Phase 10
no gated addition. No transformation-library source or binary was read or adopted.
The Pintonium tree is LGPL evidence; any later code incorporation must preserve notices
and mark modifications. The formulas below are independently specified from RESEARCH.

### 0.4 Adoption accounting

This new document explicitly selects v3 as requested. Earlier phase headers and all
DESIGN/RESEARCH/reference reports remain unchanged. `docs/MOVES.md` records Phase 10's
initial adoption. DESIGN §G0.4's executable-profile steps cannot be performed because
that machinery was retired; the discrepancy is recorded in §11, not worked around by
inventing a harness. Initial build stays in `docs/phase10/v1/`.

### 0.5 R10-2 receiver adoption and geometry scope correction (2026-09-07)

P7 §0.43/§5.1 grants the existing lifecycle/declaration proposal; this receiver adopts its
exact operations, token failure semantics, registration lifetime and ten-step placement.
Read P7/P10 completely including §§8/11, RC3/v3 Part I and respective specifications,
RESEARCH §§0–1/4.1–4.6/7.1–7.4/Apps C/E/F.1, P9 §§2.2–2.3/4.1/5 and P1 §§4.10/5.3.
No new source mining, implementation or validation ran. **Both §5 surfaces unverified.**
R10-1 remains awaiting P1's separate grant. The maintainer additionally authorized conditional
quad-to-triangle submission compatibility at the earliest milestone claiming affected .gsh
support; §4.6 records bounded adapter semantics and the still-required P4 metadata grant.
P3 D-P3-68's landed §5/§11.4 grant and exact geometry declarations were additionally read.
Active consumption is **schema19**, rejecting schema18/earlier before derivation. Geometry
source forms/effective layouts are consumed by P4, not a new P10 parser. Previous schema18
decisions remain historical; vertex requirements, lighting, options and U1 semantics are unchanged.

### 0.6 Settled R10-1 and R10-5 receiver receipts (2026-09-07)

D-P10-18 adopts P1 §§4.7.6/4.10/5/D-P1-50's exact package, borrowed-input,
restoration, recorder and class-only early compatibility grant. D-P10-19 adopts
P4 §§4.8/4.9/5/D-P4-28's required effective `geometryInput` after actual linked agreement.
These supersede the pending-owner status recorded in §0.5, not historical review verdicts.
Both receipts and the P7 intermediary remain unverified. No runtime/conformance claim.

### 0.7 Native array and cached-model correction (2026-09-08)

D-P10-20 adopts P1 D-P1-55's R27-1/NS-1 isolation operation; D-P10-21 closes
NS-2 locally with authenticated ModelRenderer capture, lifetime and every-call playback
coverage. Original reviews and decisions remain historical. §5/§8/§11 change; P7 D-P7-32
now grants R10-6, adopted here unverified. This is architecture correction,
not a whole-phase PASS, implementation clearance or native execution result.
D-P10-22 advances active containing-configuration and IdMappingInput consumption to
schema20 through P7, rejecting schema19 and every older schema before derivation.
P3's immutable same-load `PackConfiguration.assets()` is retained by its owner; P10
does not read binary assets or serialize them. Earlier schema19 receipts are historical.

### 0.8 Canonical sky/star correction and current schema (2026-09-08)

D-P10-24 resolves review R1-1 by extending the existing bounded native-product owner
to all three RenderGlobal sky/star products and all seven live list/VBO sites. R10-7
publishes exact P7 receiving obligations; it is not an already-received P7 grant.
D-P10-25 receives schema22/current-constant admission through P7. Earlier numeric
schema19/20/21 receipts, including D-P10-23, are historical. Original review bodies
remain unchanged; §5 changes require fresh owner/receiver review. No implementation,
validation, transformed cardinality, native parity or implementation clearance is claimed.

### 0.9 Attempt-5 C1 incremental ingress correction (2026-09-08)

D-P10-27 specifies source-semantic setter dispatch before physical writes, not an
endVertex-only cursor reset. D-P10-28 receives exact-current schema23 through P7;
earlier numeric receipts, including D-P10-25/schema22, are historical.
Read this complete owner and R2/C1, selected governing v3 Phase 10/G9 and RESEARCH
Appendices C/E, and the separately identified vanilla BufferBuilder mirror linked in
C1; MCP stable_39 resolved every added setter/advancement target. These source/mapping
observations do not certify the unavailable historical pinned trees or transformed runtime.
§§4.2/4.11/5/8/11 change; R10-8 requires Main's P7 health receiver receipt and fresh
owner/receiver review. No validation commands, implementation, runtime or PASS claim.

### 0.10 Attempt-6 C1 conventional participation correction (2026-09-08)

D-P10-29 separates authenticated conventional-input participation from physical
CLASSIC_56 storage. §§2.2/4.1–4.6/5/7/8/11/12 now preserve inherited current COLOR/UV1
through live draws and cached playback. R10-9 specifies exact P1/P7 receipts without
granting a new allocator, renderer or native submission API. Selected v3 authority,
the current owner algorithms, R3/C1 and P1 §4.7.6 were read for this correction.
Historical reports/confidence and schema23 remain unchanged; no implementation,
validation, runtime evidence or fresh PASS is claimed.

### 0.11 Attempt-7 R4 completion correction (2026-09-08)

D-P10-30 amends the real ingress, completion/health, §5, fixture receipt, cases and
checklist. Selected v3 Phase10 authority, R4, P1's affected capture-plan contract,
current Cleanroom block-renderer patch/Forge dispatch and the separately identified
vanilla late writer were read. Historical source/license qualifications and verdicts
remain; no validation or runtime proof was run. Changed owner/receiver contracts
require fresh review.

## 1. Scope & boundaries

### 1.1 Owned here

- The 56-byte classic vertex layout, semantic field lookup, encoders, and declared-input
  binding plans for VBO, immediate client arrays, and non-VBO chunk display lists.
- A lazy side channel on every BufferBuilder, per-vertex identity stamping, per-quad
  normal/tangent/midpoint calculation, bulk input adaptation, and saved-state provenance.
- Task-local entity-data stacks, per-block scopes including fluids and nested model
  rendering, worker cancellation and handoff, stale mesh rejection and rebuild scheduling.
- Format transitions on pack enable/disable/reload; coordination with world-renderer,
  baked-quad and lighting-cache invalidation; OQ-14's fallback.
- Shaders-only `oldLighting` fixed directional shading and `separateAo` color-channel
  policy, effective user/pack/default resolution and matched bake/mesh invalidation.
- The Phase 1 bail registry's chunk-renderer checks, message text and OQ-5 spike.
- Exact owner-phase-10 hook ledger for Appendix E rows 3–9, plus the minimal helper
  targets needed to make those rows complete.
- Growable layout/producers with `at_midBlock` designed but not wired.

### 1.2 Explicit adjacent owners

| Concern touched | Boundary |
|---|---|
| Source scan, preprocessing and declared attribute names | **Owned by Phase 3.** No regex scan or pack reopening in Phase 10 |
| Pre-link attribute locations, effective provider and backup chain | **Owned by Phase 4.** Consume 10/11/12; never rebind locations or resolve a program here |
| Frame admission, activation, shadow routing, draw repetition and reload transaction | **Owned by Phase 7.** Phase 10 contributes vertex state and geometry invalidation, not a fourth barrier participant |
| Shadow traversal and camera | **Owned by Phase 8.** Its terrain reuses the same completed meshes and vertex input service |
| Alias calculation, ordinal projection and per-entity/TE uniforms | **Owned by Phase 9.** Stamp its exact `mcEntity` result; do not infer aliases from names, materials or entity uniforms |
| GUI, options persistence and reload requests | **Owned by Phase 12.** Report failures; do not create a settings screen |
| `_n`/`_s`, atlas dimensions and companion textures | **Owned by Phase 13.** This phase supplies the tangent frame; it creates no texture |
| GL facade ownership and replay vocabulary | **Owned by Phase 1.** R10-1's exact §4.7.6 service is receiver-adopted under §5.3; no direct-GL Mixin workaround |
| Async GL, profiling optimizations and modern backend integration | **Owned by Phase 14 / G8.** Workers here never issue GL; no replacement chunk renderer |
| Modern attribute semantics and capability advertisement | **G8/S4 and Phase 3/4 cutover.** Slots are designed now; unsupported features are not advertised |

The stock chunk renderer, scheduling, culling and topology remain vanilla/Forge-owned.
Vanilla/Forge still compute lighting/AO samples; Phase 10 owns only the shader-visible
fixed-shade and AO-channel policy in §4.8.1. This is no terrain performance rewrite,
global array cache or core-profile rewrite. The only topology exception is the maintainer-
authorized conditional submission conversion in §4.6; canonical quad construction is unchanged.

## 2. Architecture overview

Phase-10-owned components in §§2–8 are v0.3 except the explicit v0.1 base-layout primitive
compatibility infrastructure in §4.6/§9 and post-v0.5 growth in §4.10. Dependencies retain their milestones.

### 2.1 Placement and responsibilities

Full package prefix is `com.schmaloogium`. P1 D-P1-50 grants these exact package homes
and the engine seam; this receiver adopts them without Minecraft/GL types in engine policy.

| Granted home | Components |
|---|---|
| `:engine / engine.vertex` | Immutable `VertexLayout`, `VertexField`, `VertexEpoch`; `QuadAttributeWriter`; builder/mesh state policy; `VertexInputPlan`; closed outcomes |
| `:engine / engine.vertex.internal` | Primitive stack storage, dirty-range tracking and layout validation; private to this phase |
| `:engine / engine.gl` | Phase-1-owned `VertexInputService` and recording support under P1 §4.7.6; no Minecraft or LWJGL types |
| `:mod / mod.glue.vertex` | Builder/state/VBO sidecars, format catalog and semantic conversion, task handoff, Forge cache invalidation, ordinal bridge, native facade implementation |
| `:mod / mod.mixin.compat.vertex` | MOD-phase dumb hooks, gated together by the existing plugin slot |
| `:mod / mod.compat` | `ChunkRendererCompatCheck`, evidence table, session-latched bail |

The ThreadLocal that associates a running chunk task with its context lives in `:mod`,
not `:engine`. Pure engine objects have explicit owners and arguments. No registry,
IBlockState, VertexFormat, raw GL name, or worker executor crosses the engine seam.

### 2.2 Principal values

Illustrative signatures define shape; semantics in §4/§5 bind implementation:

```java
record VertexEpoch(long serial, long worldEpoch, long idGeneration,
                   String layoutFingerprint) {}
record VertexField(String name, int byteOffset, int components,
                   StorageType storage, Delivery delivery) {}
enum StorageType { FLOAT32, UINT8, INT8, INT16 }
enum Delivery { FIXED_FUNCTION, FLOAT_VALUE, NORMALIZED_FLOAT, PADDING }
record VertexLayout(String id, int strideBytes, List<VertexField> fields,
                    String fingerprint) {}
record VertexInputPlan(String layoutFingerprint, List<AttributePointer> pointers,
                       Set<ConventionalInput> conventionalInputs,
                       VertexGeometryInput expectedGeometryInput) {}
enum ConventionalInput { POSITION, COLOR, UV0, UV1, NORMAL }
enum VertexGeometryInput {
    NONE, POINTS, LINES, LINES_ADJACENCY, TRIANGLES, TRIANGLES_ADJACENCY
}
record AttributePointer(String name, int location, int byteOffset,
                        int components, StorageType storage, boolean normalized) {}
```

Fingerprints identify immutable layout content, never act as credentials. `serial`
changes on every geometry-affecting publication, including off/on and resource reload
with unchanged alias bytes. `idGeneration` is Phase 9's independent generation, not
Phase 4's registry generation. `worldEpoch` prevents an old world/chunk coordinate from
matching a newly loaded world. No equality of fingerprints permits reusing stale work.
The plan's pointers and conventionalInputs are immutable defensive copies, nonnull and
without null members. Conventional participation is authenticated source semantics,
not inferred from occupied bytes or a caller-provided Set. Complete plan identity includes
layout fingerprint, ordered pointer descriptors, conventionalInputs in enum order and
expectedGeometryInput; equality is necessary, never source authority.

The stack stores two primitive integers per entry, plus scope identity/depth. A build
context captures one `AliasLookup`, its matching glue ordinal map, a VertexEpoch, and
its owning task. It never samples the global current lookup per vertex.

### 2.3 Data flow

```text
Phase 7 quiesced transaction + Phase 9 publication
    -> immutable vertex epoch + matching ordinal bridge
    -> task context -> per-block push/pop -> builder writes and quad finalization
    -> sealed mesh/state, carrying epoch and stride
    -> render-thread upload validation -> VBO or display-list product
Phase 7 successful effective-program activation
    -> requested vertex-input scope -> current layout + declared-name plan
    -> immediate draw / VBO draw / list replay -> finally restore vertex state
```

Three invariants dominate: no mixed stride within a builder; no mixed epoch within a
mesh; no generic array pointer used with a source other than the one it describes.

## 3. Contract conformance map

`[D-P10-n]` denotes a local decision, not reference evidence. Pintonium provenance
below is expanded by the path table in §0.2 and the precise comparisons in §4.3.

| In-scope contract item | Satisfying element | Provenance / disposition |
|---|---|---|
| C.1 position: float3 at 0 | `CLASSIC_56` fixed-function position field | `[V:observed]` RESEARCH App C.1 |
| C.1 color: ubyte4 at 12 | Unchanged RGBA bytes and normalized FF color pointer | `[V:observed]` App C.1 |
| C.1 primary UV: float2 at 16 | Semantic adapter and FF UV0 pointer | `[V:observed]` App C.1 |
| C.1 lightmap: short2 at 24 | Preserve raw lightmap values; FF client texture unit 1 | `[V:observed]` App C.1 |
| C.1 normal: byte3 plus pad at 28 | Diagonal normal encoder; pad zero | `[V:observed]` App C.1/C.2 |
| C.1 midpoint: float2 at 32, location 11 | Quad UV average; non-normalized float pointer | `[V:observed]` App C; Pintonium u16 packing rejected, D-P10-2 |
| C.1 tangent: short4 at 40, location 12 | Normalized signed-short pointer, xyz plus handedness | `[V:observed]` App C; Pintonium byte packing and opposite handedness rejected, D-P10-2 |
| C.1 identity: two words at 48, location 10 | Three short components at 48/50/52, zero pad at 54 | `[V:observed]` App C; `[V:doc]` shaders.txt:119; ambiguous “2 used” qualified in §11 |
| C.2 current entity stack stamped per vertex | Builder-associated stack, Phase 9 `mcEntity` payload | `[V:observed]` App C.2; provisional Phase 9 §5.1 |
| C.2 normal = normalized diagonal cross | §4.3 exact formula and worked examples | `[V:observed]` App C.2; `[V:observed — Pintonium common/.../QuadUtil.java:78–117]` |
| C.2 UV-edge tangent/bitangent; `normal × tangent` handedness | §4.3 uses full-precision normal and required cross order | `[V:observed]` App C.2; PD §9's equivalence claim conflicts with source, D-P10-2 |
| C.2 average of four UVs | Sum in vertex order, multiply by 0.25; no sprite-bounds substitution | `[V:observed]` App C.2; Pintonium conversion at 179–220 |
| C.2 stride 56, offsets 32/40/48, both draw paths | §4.6, common plan and source-specific binding | `[V:observed]` App C.2 / §4.6 |
| C.3 `at_midBlock`, block-center offset; optional emission | §4.10 named producer and appended slot, unwired | `[V:web]` RESEARCH §3.6.5/App C.3; Pintonium math evidence only |
| C.3 `at_velocity`, entity/BE view-space previous offset | Reserved named semantic, no producer or advertisement | `[V:web]` App C.3; post-v0.5 |
| C.3 `mc_chunkFade` | Future constant −1.0 only; no simulated fade | `[V:web]` App C.3; post-v0.5 |
| C.3 core-profile `va*` family | Unsupported/unadvertised in classic layout; distinct future layout | `[V:web]` App C.3; D-9 |
| §4.6 format enable/disable and cache interaction | Quiesced transition, full renderer/model invalidation, OQ-14 | `[V:observed]`, `[Q:OQ-14]`; §4.8/§10.2 |
| §7.4 source-driven per-program enablement | Effective-provider declaration plan, never requested-slot union at a live draw | `[V:doc]` shaders.txt:346–348; provisional Phase 4 §4.9; R10-2 |
| §7.4 grow without rewriting consumers | Descriptor-driven byte access, source adapters and bind-plan iteration | `[D-P10-1]`; §4.10 |
| E3 RenderChunk | H10-BUILD/H10-BLOCK; task lifetime plus all block render calls | `[V:mcp]` App E3, live MCP; Cleanroom RenderChunk patch |
| E4 ChunkRenderDispatcher | H10-TASK/H10-UPLOAD; synchronous and worker paths share context rules | `[V:mcp]` App E4 plus live worker/upload resolution |
| E5 BufferBuilder | H10-BEGIN/END/ARRAY/BULK/STATE/SEAL/RESET plus seven H10-WRITER-* rows | `[V:mcp]` App E5 and D-P10-27 setter/advancement mapping; `[V:observed]` Cleanroom bulk patch |
| E6 Tessellator | H10-TESS delegates once to uploader, retains cleanup boundary | `[V:mcp]` App E6 |
| E7 WorldVertexBufferUploader | H10-CLIENT around actual draw after Forge element setup | `[V:mcp]` App E7; Cleanroom preDraw/postDraw patch |
| E8 VertexBuffer | H10-VBO-UPLOAD/DRAW; actual stored layout and epoch | `[V:mcp]` App E8 |
| E9 BlockModelRenderer | H10-MODEL nested identity scope and dirty-tail finalization | `[V:mcp]` App E9 |
| Generic constant entity/TE candidate | Not adopted; zero neutral constants only for missing streams | PD §8.2 checked against App C and D.4, D-P10-3 |
| Chunk-renderer replacement | Detect-and-bail via existing Phase 1 mechanism, §4.9 | `[A]` RESEARCH §7.4; assigned v0.3 policy; OQ-5 remains open |
| Shaders-off has no extended work | Ordinary vanilla formats, null sidecar fast path, no TLS/GL/quad work | DESIGN Phase 10 architecture requirement; D-P10-4 |

Relevant do-not-inherit dispositions: no Pintonium 11–14 numbering, 40/48-byte terrain
formats, u16 midpoint, byte tangent, unconditional re-encode, heuristic material aliasing,
liquid/non-liquid renderType substitution, or shader AST transformation. PD §8.2's
undelivered blockEntityId/entityColor are not borrowed: Phase 9 owns those real uniform
producers. PD's successful entity bridge is not proof of classic attribute equivalence.

## 4. Detailed design

### 4.1 Layout and exact packing

`CLASSIC_56` is immutable, 56 bytes / 14 ints per vertex. Every decoder and pointer
builder uses its named fields; only the layout declaration and byte-oracle fixtures
contain the classic offsets. Vanilla block storage is 28 bytes; normal at 28 is also
new storage, not part of an unchanged 32-byte vanilla prefix.

| Bytes | Field | Storage / delivery |
|---|---|---|
| 0–11 | position | 3 float32, FF position |
| 12–15 | color | 4 unsigned bytes, FF color |
| 16–23 | UV0 | 2 float32, FF texture coordinate set 0 |
| 24–27 | lightmap | 2 signed shorts, unnormalized FF texture coordinate set 1 |
| 28–30 | normal | 3 signed normalized bytes, FF normal |
| 31 | padding | zero |
| 32–39 | `mc_midTexCoord` | 2 float32, location 11, normalized=false |
| 40–47 | `at_tangent` | 4 signed shorts, location 12, normalized=true |
| 48–49 | `mc_Entity.x` | low 16 bits of Phase 9 packed word, location 10 component 0 |
| 50–51 | `mc_Entity.y` | high 16 bits of Phase 9 packed word, component 1 |
| 52–53 | `mc_Entity.z` | low 16 bits of Phase 9 metadata word, component 2 |
| 54–55 | padding | zero, not a fourth identity component |

Identity uses the floating-input API equivalent of
`glVertexAttribPointer(10,3,GL_SHORT,false,56,48)`, **not** an integer-input pointer
and not normalized shorts. Classic GLSL declares `attribute vec3 mc_Entity`.
The remaining two pointers are `(11,2,GL_FLOAT,false,56,32)` and
`(12,4,GL_SHORT,true,56,40)`. These are native mappings inside the requested facade,
not a license to call LWJGL outside it.

Identity values are **bit patterns**. Phase 9 permits alias domain −32768…65535;
for example 65535 and −1 both yield `0xffff`, observed by a signed-short floating
attribute as −1.0. Phase 10 neither changes this to unsigned nor repeats alias-range
validation with different rules. `Present`, `Absent`, and `Unrepresentable` all supply
their defined two words; the latter two preserve render type and metadata with zero
low ID. Invalid/stale ordinals reject the build, not an array-index exception.

Native ByteBuffer order is established once. Multi-component identity is written as
three ordered shorts, not a host-endian `putLong` that can exchange components. On the
target little-endian platform these bytes equal writing the two Phase 9 ints at
48 and 52. The high metadata half is already zero. Normal/tangent components likewise
use ordered byte/short stores. Float bits and byte channel order are preserved by
semantic copy, not by treating a packed RGBA int as endian-independent.

The Forge VertexFormat projection exposes only admitted ordinary position/color/UV/
lightmap/normal semantics from §4.6's conventionalInputs. Keep every physical offset
and stride unchanged: source-absent COLOR/UV1 slots are padding in the draw projection,
so Forge preDraw/postDraw neither enables them nor resets their current values.
Extension bytes likewise remain non-FF padding; Phase 10 descriptors own interpretation.
Forge generic handlers cannot enable arrays behind the plan's back. The format catalog
keys projections by authenticated original descriptor and participation, not CLASSIC_56
alone. OQ-14 covers this projection through both lighting paths. Midpoint is not an
extra FF UV index and identity is not a normalized Forge normal.
This is the storage/draw projection, **not the source writer sequence**. Installing
it requires §4.2's dispatch before each scalar write; resetting a physical cursor only
at endVertex cannot fix an OLDMODEL tex call that would already address COLOR.

### 4.2 Builder state and ingress

A mixin attachment is a nullable pointer, initially null. A vanilla-only builder has
no allocated Phase 10 stack/scratch and does no ThreadLocal access. `begin` resolves
whether the supplied known format has an active shader projection. Only that path
creates/reuses its private sidecar. Once a build begins, its mode, layout, epoch and
owner are frozen until reset; a global toggle never changes its stride mid-write.

State machine:

```text
IDLE -> WRITING(layout, epoch, owner) -> SEALED -> uploaded/copied -> IDLE
                    |                    |
                    +---- INVALID -------+ -> discard/reset, schedule rebuild
```

`begin` starts at vertex zero with neutral identity. Chunk scopes provide a context;
immediate render-thread builders receive a local context with neutral stack unless
an actual block-model scope supplies a Phase 9 lookup. Each builder maintains its own
stack cursor, pending quad count, first-unfinalized vertex, and dirty tail. A nested
builder never borrows another builder's mutable cursor.

**Incremental writes — D-P10-27.** Retain the authenticated immutable source descriptor
at begin, before installing its physical projection. If begin receives an owner-issued
projected default, recover its original descriptor from that same format-catalog identity,
not its field order or stride. Reuse the existing nullable builder sidecar and semantic
adapter: retain source usage/index/type → destination field mapping, source cursor,
per-vertex written mask and setter-scope depth. No second layout, parser, format guess
or public source credential is introduced. Known BLOCK fluid writers remain
`pos→color→tex→lightmap→endVertex`; authenticated TexturedQuad OLDMODEL writers remain
`pos→tex→normal→endVertex`. Neither caller gains synthetic color/lightmap/normal calls.

The chosen hook is an exception-safe method wrapper around each scalar setter.
Before invoking its ordinary scalar body, select its authenticated **source semantic**
(pos=POSITION0, color=COLOR0, tex=UV0, lightmap=UV1, normal=NORMAL0), then select that
semantic's physical destination element/index. Never choose the destination from the
previous physical cursor. The catalog admits these built-in source/destination scalar
encodings as compatible; a differing custom encoding needs its existing explicit
semantic adapter, not execution of a wrong-type scalar body. Preserve ordinary position
translation, float-to-int color delegation/quantization, byte order, lightmap component
ordering, scalar normal encoding and fluent return identity. Quad-derived normal replacement
still happens only under §4.3; non-QUADS retain the supplied source normal.

Wrap `nextVertexFormatIndex` too: inside an authenticated scalar body, replace physical
advancement with one logical source-element advancement (skip source padding, wrap in
the source descriptor), without walking the CLASSIC_56 additions. On scalar exit,
restore the physical cursor to the projection's first ordinary element in finally;
the next setter again dispatches by semantic, independent of both cursor positions.
The logical cursor records source traversal, not an obligation to issue setters for
destination-only or omitted fields. The float color wrapper retains its original
delegation to integer color; only the integer body writes/advances once. `noColor`
keeps the ordinary no-write/no-advance behavior; the next tex still selects UV0.
An advancement outside the authenticated setter scope on an active projected builder
is unsupported ingress and invalidates the product, not silent physical traversal.

Initialize each new destination record once, before its first scalar write (or
endVertex if untouched), with §4.2's white color, zero lightmap/normal, zero padding
and neutral extension fields; absent UV storage is zero, never invented sprite UVs.
Missing required geometric inputs remain unsupported under the existing producer
requirements. Omitted optional semantics never reuse previous-vertex bytes. Reserve
the whole destination record before writes using the existing growth/cap rules; keep
indices, not addresses, across growth. A successful endVertex calls vanilla count/growth
once, then stamps identity and finalizes every newly complete quad. Only after success
clear the written mask/pending-vertex flag and reset both cursors to their respective
first ordinary elements. Initialization of the next vertex is lazy. Source normals,
colors and lightmap cannot be overwritten by initialization after their setter ran.
These white/zero source-absent COLOR/UV1 bytes are storage hygiene only: initialization
never adds conventional participation, samples a current value or authorizes its reset.

Begin, each setter (including float delegation), advancement, endVertex and both bulk
wrappers cover exceptional exits: any failed active write/growth/stamp/finalization
marks the entire product INVALID, clears transient setter ownership in finally and
propagates the original error through §6 cleanup. Never stamp a failed vertex, rewind
the count to present a successful prefix, seal/upload/capture invalid bytes or resume
writing until reset/new successful begin. Reset clears source cursor/mask/association;
save/sort/seal require a vertex boundary, and authenticated state restore reinstates
the matching source descriptor with a fresh boundary cursor, not stale writer state.
Inactive/null builders execute the original setters and advancement unchanged, with
no sidecar allocation, descriptor dispatch, TLS or extended initialization. The v0.1
base adapter does not install this v0.3 extended writer machinery.

**Bulk writes.** Both `addVertexData(int[])` and Forge `putBulkData(ByteBuffer)` are
first-class ingress. Snapshot the append start/count, let the supported input-format
adapter perform the ordinary append, then stamp **only the appended vertices** with
the current stack value and finalize every newly complete quad, including a quad
started by incremental writes. Never mutate the caller's array/buffer contents, change
its ownership, or stamp IDs into cached BakedQuad data. Preserve each API's ordinary
position/limit consumption behavior. Do not replace Cleanroom's extra-vertex growth
allowance (`BufferBuilder.java.patch:18,68`) with a smaller allocation.
Bulk entry requires no pending incremental vertex; a partially written vertex is not
implicitly ended or overwritten. A quad may span incremental/bulk boundaries only
between completed vertices. Authenticate and append whole records using the existing
bulk adapter, then return with source/physical cursors at their respective first elements,
an empty written mask and no pending scalar vertex. Bulk data never changes begin's
incremental source descriptor: a subsequent fluid/model setter resumes that source's
semantics. Failed conversion/append invalidates the entire product with the same finally
cleanup as scalar ingress; successful prior bytes cannot be sealed as a fallback.

There is no stride inference from length divisibility: 56 ints could describe eight
vanilla vertices or four extended vertices. The ordinary unannotated raw-array API
means the current builder format, as vanilla defines it. A BakedQuad adapter receives
the quad's actual `getFormat()`, and a bulk byte adapter carries its source descriptor
in a call-local glue token. Only those authenticated source formats permit conversion.
**D-P10-30 — partial ingress versus completed producer.** The builder retains its
immutable final participation from the authenticated completed producer, not each
intermediate BakedQuad's storage descriptor. In the recognized block-model producer,
admit an authenticated ITEM BakedQuad into a BLOCK builder: ITEM supplies POSITION,
COLOR, UV0 and its source normal; BLOCK's UV1 is supplied by the real subsequent
`BufferBuilder.func_178962_a(IIII)V` (`putBrightness4`), not zero initialization.
Before append, authenticate that producer's existing model scope, builder/epoch,
actual quad format and exact four-vertex aligned append range. Attach one private
pending-UV1 obligation for those four destination indices to that scope; complete any
previous obligation before another append can move the last-quad target. Preserve
begin's BLOCK descriptor and final mask throughout. This is not arbitrary mixed-source
union: other mismatches require a separately bounded registered producer or rejection.
Unknown raw formats are never guessed from stride, byte count or method arguments.

The exception-safe brightness wrapper checks the same builder/scope/epoch and exact
last-four destination range, then invokes the real writer using the projected format's
UV1 offset and stride. Mark all four UV1 values complete only after successful return;
their four original packed integer values, including zero when genuinely supplied,
remain unchanged. An ordinary brightness mutation on an already complete authenticated
BLOCK quad stays supported; it cannot discharge another scope's pending obligation.
No filler, normal bytes, current lightmap or copied ITEM mask constitutes completion.
On a partial/throwing write, invalidate the entire product, propagate the original
exception through existing cleanup, and clear transient completion ownership in finally.
Missing completion at normal model-scope exit, outer block exit, save/sort/seal,
upload/capture/draw admission or any operation that would displace the pending last
quad rejects/invalidate before publication. Completion cannot cross nested model
scopes, resets, state restoration, cancellation or epoch retirement. Failure exits
discard the obligation and invalid product; they never expose a successful prefix.
Scalar BLOCK/Forge-lighting producers retain their real UV1 setter path, and genuinely
uncolored OLDMODEL keeps COLOR/UV1 absent and inherited rather than acquiring an obligation.

Current source corroboration (not historical-pin/runtime or copying permission):
`reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/BlockModelRenderer.java.patch:12–14`
retains append then brightness; its `ForgeBlockModelRenderer.java:49–86` dispatches
flat and smooth paths to vanilla when Forge lighting is disabled. The separately read
[vanilla BufferBuilder source](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/BufferBuilder.java)
shows `putBrightness4` writing four packed values at last-quad UV1 offset/stride.
This adapts only destination bytes; cached BakedQuad arrays remain immutable.

**BLOCK, ITEM and textured variants.** Build immutable projections from a semantic
source-format catalog. Known block and item/textured quad paths use the classic
positions for shared semantics and initialize missing lightmap/color/normal semantics
explicitly (zero lightmap, white color, zero normal before quad calculation). Existing
source values, including item normal at its different vanilla offset, are copied by
semantic usage/index/type. Missing UVs do not acquire invented sprite coordinates.
Custom layouts retain their ordinary draw path unless a registered semantic adapter
proves the projection. A shader demanding unavailable per-vertex geometry data enters
§6's unsupported-format failure; it does not read offsets 32+ from a short record.

**Late mutation.** Forge/model renderers may change the last quad's position, UV or
normal after bulk append. The model scope closes with that touched tail marked dirty;
the outer per-block scope finalizes any remaining dirty tail before pop. The seal
boundary also finalizes dirty complete quads before sorting, state capture, upload,
or drawing. Uniform translations need no recomputation, but a generic mutation
adapter must conservatively dirty the affected quad. Color/lightmap-only mutations do
not alter the tangent frame, but brightness still discharges D-P10-30's semantic
completion obligation before any safe model/seal boundary. H10-BRIGHTNESS-4 is mandatory;
other selected actual late writers also enter the hook-health inventory, including
their exceptional cleanup. No whole-layer second re-encode is the default algorithm.

**Primitive boundaries.** Group quads from vertex zero of each `begin`, never across
builders, scopes, or draw modes. A block scope must start/end on a quad boundary for
QUADS geometry. A partial quad at a block boundary or seal is malformed: reject that
product, not combine adjacent blocks into one tangent computation. Non-QUADS paths
still stamp identity per vertex; preserve source normal and use initialized neutral
midpoint/tangent where no quad-derived contract exists. Do not apply four-vertex math
to line/triangle streams. A pack path requiring quad-derived inputs from such a stream
is diagnosed as unsupported rather than advertised as equivalent.

### 4.3 Quad algorithms, packing, and reference cross-check

Let positions be `p0…p3`, UVs `(ui,vi)`, in emitted order. Evaluate from stored position
and UV floats before quantization:

```text
a = p2 - p0                 b = p3 - p1
N = normalize(a × b)
e1 = p1 - p0                e2 = p2 - p0
du1 = u1-u0                 dv1 = v1-v0
du2 = u2-u0                 dv2 = v2-v0
d = du1*dv2 - du2*dv1
T = normalize((dv2*e1 - dv1*e2) / d)
B = normalize((-du2*e1 + du1*e2) / d)
w = sign(dot(B, N × T))
midU = (u0+u1+u2+u3)*0.25
midV = (v0+v1+v2+v3)*0.25
```

All four vertices receive the same N, `(T,w)` and midpoint. Preserve position, color,
UV0, lightmap and per-vertex identity. No Gram–Schmidt step, triangle averaging,
sprite-bound midpoint, or smoothing across adjacent faces is added. A non-planar
quad still uses the specified diagonal normal and first-three-vertex UV edges.

Encode a finite normal component `x` as
`(byte) trunc(clamp(x,-1,1)*127)` and tangent component as
`(short) trunc(clamp(x,-1,1)*32767)`; no use of −128/−32768 as normalized extrema.
Compute handedness with unquantized N/T/B, then quantize it with the other tangent
components. `sign(0)=0`; negative and positive dot products yield −1 and +1.
Padding is always zero. The exact truncation choice is D-P10-2; the assigned digest
specifies storage widths but not every numeric conversion detail.

**Degenerate domain.** Zero-length diagonal cross yields zero N. Zero UV determinant,
zero-length tangent/bitangent or non-finite intermediate yields zero tangent and `w=0`,
not NaN/Inf. Retain a finite valid normal and midpoint independently. Non-finite input
positions/UVs reject the geometry product before GL rather than substituting a valid
looking face. These degenerate rules are explicit local containment, not claimed
observed OF behavior; T2 evidence may require an upstream contract clarification.
There is no arbitrary epsilon that collapses small but nonzero determinants.

**Worked square.** Positions `(0,0,0),(1,0,0),(1,1,0),(0,1,0)` and UVs
`(0,0),(1,0),(1,1),(0,1)` produce:

- diagonals `(1,1,0)` and `(−1,1,0)`, cross `(0,0,2)`, N=`(0,0,1)`;
- e1=`(1,0,0)`, e2=`(1,1,0)`, UV deltas `(1,0),(1,1)`, d=1;
- T=`(1,0,0)`, B=`(0,1,0)`, N×T=`(0,1,0)`, w=+1;
- midpoint `(0.5,0.5)`, normal bytes `(0,0,127,0)`, tangent shorts
  `(32767,0,0,32767)` at every vertex.

For identity `(alias=31,renderType=3,metadata=2)`, Phase 9 supplies `0x0003001f,2`.
The little-endian identity bytes are `1f 00 03 00 02 00 00 00`, observed by the shader
as `(31,3,2)`.

**Mirrored-U square.** Keep positions, replace UVs with
`(1,0),(0,0),(0,1),(1,1)`. Now d=−1, T=`(−1,0,0)`, B=`(0,1,0)`, w=−1;
midpoint and N are unchanged. Tangent shorts are `(−32767,0,0,−32767)`.
This is the distinguishing normal-map handedness case, not a second same-path example.

**What Pintonium actually proves.**

- `[V:observed — Pintonium common/src/main/java/org/embeddedt/embeddium/impl/util/QuadUtil.java:78–117]`
  matches the diagonal cross and zero-length-normal guard.
- `[V:observed — Pintonium forge122/src/main/java/org/taumc/celeritas/impl/render/terrain/compile/VintageChunkBuildContext.java:179–220]`
  sums the four UVs and passes their quarter-average onward. Its u16 midpoint encoding
  is not adopted; ours remains float32 per App C.
- `[V:observed — Pintonium common/src/main/java/org/embeddedt/embeddium/impl/render/chunk/vertex/format/ChunkVertexExtendedData.java:62–105]`
  uses the same first-two-edge tangent/bitangent equations, **but lines 98–100 compute
  T×N, not N×T**. Its ordinary square therefore has w=−1, opposite the required +1.
  It also packs bytes, substitutes reciprocal 1 when d=0, uses a quantized normal
  supplied by the caller, and maps zero dot to +1. None of those is silently copied.

Thus PD §9 and DESIGN Phase 10 overstate numeric equivalence. RESEARCH App C.2 wins.
D-P10-2 records the structure-only adoption and the rejected numeric differences.

### 4.4 Entity-data stack and task ownership

Each builder's active stack begins at neutral `(0,0)`; the existing Phase 9 per-draw
uniforms are not consulted to populate this stack. A block push obtains the ordinal
from the context's matching immutable glue map, calls `AliasLookup.mcEntity`, and
pushes exactly its words. The glue map must describe the same registry publication;
an ordinal from a later map is invalid even if within bounds.

H10-BLOCK brackets the `BlockRendererDispatcher.renderBlock` invocation inside chunk
rebuild. This covers fluids as well as baked models. H10-MODEL additionally brackets
`BlockModelRenderer.renderModel`, covering direct model callers and nested alternate
states. When both fire, nested equal values are legal; the inner pop restores the
outer state. Do not suppress nesting by state equality: a different builder or nested
state may intervene. Forge's per-layer loop remains intact and every eligible layer
gets its own balanced call scope; do not use the default state's layer as a shortcut.

A scope captures prior depth and owner token. `finally` restores that exact prior
depth, on success, false return, cancellation or exception. Wrong-thread/out-of-order
pop invalidates the product and unwinds to the task boundary; it cannot leak the
previous block's material into the next model. Storage grows geometrically from a
small primitive array; the observed ten-entry stack is not a contract limit. Allocation
failure terminates the shader operation safely; no silently dropped pushes.

A `ChunkRenderWorker.processTask` outer guard installs the task context and clears it
in `finally`. RenderChunk rebuild has its own guard, so direct synchronous invocations
also work. The dispatcher owns queue transfer, not the stack. Never use
InheritableThreadLocal or a render-thread stack in chunk workers. A worker borrowing a
pooled RegionRenderCacheBuilder binds the epoch/owner separately to every layer builder,
and resets attachments before returning it to the pool.

The original task remains vanilla-owned. Sidecars travel with task/builder/compiled
product identities; no global map keyed only by thread ID or chunk coordinates. At
handoff, publish sealed metadata with the bytes under the same task synchronization.
A worker neither uploads GL nor closes a Phase 9 publication. During reload, Phase 7
must keep the borrowed publication alive until Phase 10 quiescence has drained its
workers and pending upload consumers (§4.8, R10-2).

### 4.5 Saved states, resort, and upload admission

`BufferBuilder.State` copies carry layout, epoch, vertex count, seal status, authenticated
original source descriptor and complete immutable input-plan identity through an attached
stamp. Restore accepts only matching current epoch, source participation and format; it
must not restamp vertices with today's block ID or rebuild participation from filler bytes.
Sorting moves whole records/quads and therefore all attributes together. It never
recomputes identities from the empty stack used during translucent resort.

Before translucent state capture, finalize dirty quads. Before restoring/resorting,
validate world/ID/layout/serial. A stale saved translucent state triggers full rebuild,
not a resort attempt. A state from an uninstrumented external producer has no Phase 10
stamp: it may enter a vanilla builder, or an explicitly declared source-format adapter;
it is not trusted as a current extended chunk merely because its stride equals 56.

The queued upload carries the sealed VertexEpoch, authenticated source descriptor,
conventionalInputs, complete prepared plan identity and source's remaining byte range.
Validate all when queued and when executed on the render thread, since a toggle can
occur between them. Require whole records, valid count/range and the live chunk task's
identity. Reject stale data before modifying a VBO, compiling a display list or marking
a CompiledChunk ready. Cancel/discard via the existing task cancellation path, and
schedule a replacement; do not report an exceptional future to vanilla's crash-report
callback for an expected stale-generation event.

`VertexBuffer` owns a sidecar containing actual uploaded layout/epoch/count, authenticated
source descriptor, conventionalInputs and complete prepared plan identity. Set it
only after successful upload and error handling. Compute count from the uploaded byte
range and that stored stride, never a now-mutated global default format. Empty uploads
clear count; failed uploads clear eligibility. Existing VBOs and display lists from the
old epoch become ineligible atomically before new frame admission, even while rebuilds
are pending. Skipping a stale chunk is safer than interpreting its old bytes differently.

### 4.6 Draw paths and restoration

#### Shared declaration plan

Phase 4 binds `mc_Entity=10`, `mc_midTexCoord=11`, `at_tangent=12`. Phase 10 consumes
only this published table directly. P7's accepted R10-2 grant delivers the effective
provider's already-scanned declaration set after successful activation. The Phase 4
`ProgramStateBundle.attributes` is available to Phase 7, but Phase 4's §5 does not
currently grant Phase 10 a current-program lookup.

**Conventional participation — D-P10-29.** Derive the nonnull immutable
`Set<ConventionalInput>` once from the authenticated completed producer descriptor and
policy. POSITION/UV0 retain their established supported-source requirements; COLOR/UV1
are members iff that producer supplies them, including D-P10-30's real late BLOCK UV1.
An intermediate ITEM BakedQuad descriptor is partial ingress, not the BLOCK draw mask;
completion is mandatory before product eligibility and never inferred from filler.
NORMAL remains a member for a supplied source normal or the existing §4.3 generated
quad-normal producer; non-QUADS preserve source normals and existing neutral/unsupported
policy. No new normal, tangent, midpoint, identity or other attribute policy is introduced.
Thus projected OLDMODEL admits POSITION/UV0/NORMAL, not COLOR/UV1; projected BLOCK
admits POSITION/COLOR/UV0/UV1 plus its established generated NORMAL. The per-vertex
written mask/noColor is not the source participation mask and cannot alter it.

In LIVE_DRAW, bind only these conventional streams; temporarily disable absent COLOR/UV1
arrays even if enabled on entry, without setting their pointers or current values.
They inherit the caller's current primary color and current texture coordinate on unit1.
P1 restores the actual prior enables/selectors and any affected state in finally.
Forge setup/cleanup must not touch absent streams, including color-reset side effects.
LIST_CAPTURE uses this same participation alongside its prepared generic union: absent
COLOR/UV1 arrays stay disabled, their filler bytes are never captured, and no constant
command substitutes the compile-time current values. Each LIST_REPLAY_GUARD receives
the authenticated captured plan and current effective geometry requirement; playback
uses the then-current inherited values without rebinding old arrays or baking the first
entity's color/lightmap. Current-value changes alone do not invalidate or rebuild a product.

The full source participation and complete plans belong to authenticated saved-state,
sealed client source, VBO and original/derived list product identity, including base
products before v0.3. Generic live plans may be derived only from that authenticated
source and the effective declaration scope; capture uses the prepared union. Admission
compares the appropriate complete plan, not equality between a capture union and a
smaller live declaration set. Any source/participation change invalidates old products;
same layout fingerprint/stride is insufficient. Whole-record topology conversion retains
the source participation exactly; copying filler never promotes it to input.

For a live draw, enable exactly the intersection of declared names and physically
present supported fields, disable the other Phase-10 generic arrays, and invoke P1
D-P1-55's isolation operation: conventional positions require generic attribute zero
temporarily disabled. LIST_CAPTURE additionally admits only the complete capture plan's
conventional, per-client-texture-unit and generic arrays; all other enabled arrays are
temporarily isolated by that owner operation, not merely locations 10/11/12. Save/restore
every affected actual predecessor field and roll back partial setup as P1 specifies.
Missing identity on a non-extended draw uses floating neutral `(0,0,0,1)` with its array
disabled; midpoint/tangent use zero components, with the ordinary fourth default
component where not explicitly supplied. No terrain alias is fabricated from
`entityId`. A declared geometric attribute that should exist on supported terrain but
is missing is a failed mesh, not a neutral-success path.

The input scope is valid only for the current successful Phase 7 activation and
current geometry epoch. It changes on fallback, shadow forcing, scope restoration,
program release, frame abort and reload. A shader-off/fixed-function result disables
Phase 10 arrays even if the preceding shader declared all three. No `glGet*` scan,
source parse or dependency private-state access finds the active program on each draw.

Capability requirements are declaration-specific: highest used location must be less
than `maxVertexAttribs` (10 needs 11, 11 needs 12, 12 needs 13). An unused location 12
must not reject a simpler program. Phase 4 owns compile/program fallback; Phase 10
validates each emitted pointer before native calls and rejects inconsistent plans.

#### Client-array path

`Tessellator.draw` retains its normal finish/draw/reset role. It does not bind arrays
in parallel with `WorldVertexBufferUploader.draw`. After Forge's element `preDraw`
setup, H10-CLIENT wraps the actual draw invocation:

1. validate sealed builder, source range, epoch, layout, authenticated participation,
   complete plan and current input scope;
2. use a borrowed client-data view with stride 56 and the offsets above; establish
   array-buffer binding zero before client pointers;
3. retain the direct buffer and pointer ranges for the complete call;
4. apply the complete P1 live plan including generic-zero isolation, then submit the
   prepared draw through P7 §4.6's countInstances boundary at v0.5 (one ordinary draw earlier);
5. in `finally`, restore every affected actual predecessor field through P1;
   run the normal Forge element `postDraw` cleanup and builder reset even on a
   shader-side failure. Do not duplicate a draw after partial execution.

Only participating FF elements go through Forge's `VertexFormatElement.EnumUsage.preDraw`
and `postDraw`, using §4.1's source-keyed projection. Absent COLOR/UV1 remain padding,
including during cleanup; do not invoke color postDraw merely because bytes 12–15 exist.
The patch replaced the old uploader switch; targeting that removed switch misses the
actual path. The outer uploader cleanup guard covers failures before the inner wrapper.

#### VBO path

At upload, authenticate the exact source and retain its actual layout and participation.
At `VertexBuffer.drawArrays`, the facade uses a borrowed native VBO identity issued by
the mod adapter, binds that source, and configures only admitted FF pointers from the
stored conventionalInputs plus generic pointers from the current authenticated plan.
Pointer arguments are byte offsets, not client addresses. A 56-byte VBO must never
inherit VboRenderList's vanilla
28-byte FF stride; the P10 wrapper overrides the complete relevant pointer state
immediately around the actual draw, after any vanilla setup.

Restore all perturbed pointer and enable state on success/failure. Borrowed VBO handles
confer no create/delete permission. Deletion/reset invalidates their device-side
provenance and the associated mesh stamp before reuse. Wrong source, stride, epoch or
out-of-range byte extent rejects before any draw.

#### Non-VBO chunk display lists

Immediate client arrays are not the entire VBO-off chunk path. During dispatcher
upload, vanilla may compile a display list using the uploader. Compile with the
complete current layout and the **union of supported classic attributes declared by
the accepted pipeline** so the list captures every attribute later needed by main or
shadow draws. This is an explicit `LIST_CAPTURE` mode, not a forged current-program
activation. Stage and validate the complete source/plan before opening a list; invoke
P1's capture bind/isolation before `glNewList(GL_COMPILE)`, issue only admitted geometry,
close `glEndList`, then restore outside the list. No predecessor-restoration commands are
recorded. Compile extended chunk lists only while shader vertex mode is active; an
epoch/mask change invalidates and rebuilds them. The model and sky/star base-format
exceptions below also track products first compiled off, without extended vertex work.
Count expansion and instance-uniform uploads are forbidden during LIST_CAPTURE. Capture
geometry once; count belongs to authenticated live playback, not a compiled list epoch.

At replay, the recorded vertex attributes are already part of the list; there is no
live client pointer to its former builder memory. Use P1 `LIST_REPLAY_GUARD` and current
authenticated geometryInput without reconstructing or restoring expired capture pointers,
then at v0.5 submit the prepared list playback through P7's adjacent-repeat boundary;
restore affected generic current values afterward. Undeclared inputs are unused by the
program, not live enabled arrays. Do not replay the Java uploader per frame or retain
freed client memory to emulate a list.

This capture-vs-live-draw distinction is D-P10-7. It must be exercised with VBOs off
and different main/shadow declaration sets. If the actual compat driver cannot capture
these generic arrays correctly, shaders fail safely for this path; **forcing VBOs is
not an acceptable completion of the required two-path implementation**.

#### Prepared-submission repetition — IR-18

P7 owns count/admission/instance/failure policy; these existing draw adapters supply the final
native submission **after** validation, uploads and pointer setup, and **before** teardown,
Forge postDraw or builder reset. At v0.5 repeat that prepared operation N times, not the Java
uploader/Tessellator/entity/render-layer method. N comes from the current successfully activated
effective provider through P7, not a P10 registry query or requested-child metadata.
The same P7 private submission guard prevents duplicate forwarding wrappers from multiplying
N²; distinct nested submissions retain saved-parent instance restoration.
No new public renderer interface or native facade extension is granted by this policy.

For display lists, repeat playback only while current main admission or authenticated shadow
admission remains valid. The list must not record program changes or instance uploads that
would overwrite the live per-copy value. Unproven mixed-state lists cannot claim count support
by drawing once. Capture/setup and postDraw/reset occur once, depth/blend effects occur once
per native copy, and no new Forge traversal/event is emitted. Shadow uses the active root-shadow
selection and never opens a main gbuffers scope. Failure stops copies, restores pointers and
saved instance value in finally, then invokes P7 main containment or P8 shadow abort/neutralization.
R10-1/R10-2 are owner-granted/receiver-adopted, unverified; actual driver/list/hook
behavior requires runtime verification. Count authority alone does not grant a facade operation.

#### Conditional primitive compatibility — maintainer scope correction

The maintainer authorizes this narrow exception to the earlier no-conversion boundary,
recorded in `docs/decisions/GEOMETRY_PRIMITIVE_COMPATIBILITY.md`: at the earliest milestone
claiming an affected .gsh path (v0.1 native geometry support), convert only an otherwise
admitted QUADS submission whose actually active effective geometry input is TRIANGLES.
This base-layout submission adapter/facade infrastructure is v0.1; CLASSIC_56, P9 stamps
and the extended-format lifecycle stay v0.3. No chunk scheduling/culling/renderer rewrite.
P7's existing private activation/submission boundary supplies the authenticated category
independently of optional extended-vertex installation, so absent P9 is not a fabricated
lookup or a reason to claim early .gsh coverage without the adapter.

P4 D-P4-28 grants R10-5 exactly: append required non-null
`GeometryInputRequirement geometryInput` after `Optional<LegacyGeometryConfig> legacyGeometry`
in ProgramStateBundle. The enum is NONE, POINTS, LINES, LINES_ADJACENCY, TRIANGLES,
TRIANGLES_ADJACENCY. P7 consumes `Activated.binding().state().geometryInput()` under
the current accepted selection; FixedFunction clears it. It describes the actual effective
provider from P3 finalized geometry materialization, not a requested child or source scan.
Unknown geometry must not become NONE. R10-5 is owner-granted (P4 D-P4-28) and
receiver-adopted (D-P10-19), unverified; these names are the owner's exact grant, not locally
minted private access. P1 remains authoritative for actual native linked-state
validation. Mismatched or unavailable evidence suppresses submission through ordinary
main/shadow containment, never issues an incompatible GL draw.

The mod-side adapter translates the authenticated category into a P10-owned immutable
submission plan without introducing registry types into engine.vertex. NONE preserves
the original primitive; TRIANGLES accepts existing triangles/triangle strips/fans and
converts QUADS only. POINTS, LINES, LINES_ADJACENCY and TRIANGLES_ADJACENCY accept only
the corresponding GL-compatible original primitive families. No points/lines/adjacency
synthesis exists. Unsupported mismatch rejects before draw and is reported, not retried
under another program. Primitive count/range, layout, source issuance/lifetime, world/
resource/product epoch and current activation must all be valid before preparation.

VertexInputPlan.expectedGeometryInput is required and is this exact category translated by
mod glue to VertexGeometryInput. P1's separately granted bind contract must compare it to
private actual linked metadata for LIVE_DRAW/LIST_REPLAY_GUARD before native work; LIST_CAPTURE
uses prepared demand and cannot infer a live program. This is a binding preflight, not a
new public metadata lookup or independent activation.

**Topology and pack observation.** Finish all original four-vertex normal/tangent/midpoint
computation, late mutation and translucent quad ordering first. For each canonical quad
q=(0,1,2,3), emit triangles **(0,1,3), (1,2,3)** in that order, then q+1. Copy whole vertex
records bit-for-bit including IDs, padding, color, lightmap and unknown supported fields;
never recompute attributes per triangle or modify source bytes/descriptor/count. This
preserves winding for an ordinarily wound quad and uses the original last vertex3 as
both triangles' last provoking vertex. The adapter requires the established last-vertex
provoking convention; it does not change GL provoking state. A context/path using an
unproven or incompatible convention cannot claim support. Nonplanar/concave quads follow
this explicit diagonal, not a claim of identical unspecified native QUADS rasterization.

The geometry stage observes two input primitives per original quad, in emitted order.
Within one draw `gl_PrimitiveIDIn` is 2q then 2q+1 (plus preceding triangle primitives in
that same draw, if any); it is not the old quad ID. IDs restart at ordinary draw boundaries
and each adjacent instance submission. No source patch, hidden ID remap, geometry invocation
merging or parity claim hides that visible change. Geometry-stage output provoking behavior
stays the shader/GL contract. Real conformance must cover flat outputs, IDs, winding,
nonplanar faces and ordering before parity is claimed.

**Bounded client/VBO/list adapters.** Compute q=count/4 and destination count=6q with checked
arithmetic; require original range start/count to contain complete canonical quads, validate
all byte ranges and native count limits before any allocation or draw. Destination bytes
are exactly 6q×stride (1.5× source), subject to the existing source/native buffer limits.
Do not split a submission to fit memory: that changes primitive IDs and instance ordering.
An unrepresentable/allocation-failed plan follows existing failed-operation containment.
Allocate derived storage only when the prepared pipeline can require triangle conversion;
ordinary no-geometry/off paths retain the original bytes and no conversion work.

- Client arrays: derive one call-owned direct-buffer stream after source sealing and before
  pointer setup. Bind its actual layout/range through the P1 vertex facade, draw TRIANGLES,
  and retain both source borrow and derived storage through all adjacent copies and pointer
  restoration. Release in finally after restoration, then ordinary Forge postDraw/reset.
- VBOs: derive from the validated sealed upload source while CPU bytes are still available,
  not per-frame readback. Glue owns an optional companion converted product in the same
  vanilla upload lifecycle; canonical VBO remains untouched. Only the existing observed
  vanilla-owned upload path creates/uploads/deletes it; a borrowed facade source grants no
  allocation/deletion authority. Publish eligibility only after all required uploads succeed.
  Retain paired actual stride/count/source epoch; converted draw borrows its own same-device
  handle. Reupload/delete/reset/reload invalidates and retires both after last use. If demand
  changes and no converted product exists, rebuild from canonical producer; do not read back,
  reinterpret the original VBO or issue an incompatible draw while rebuilding.
- Display lists: while the proven canonical capture stream is live, compile distinct original
  and conditional converted geometry-only lists, each once, through the same observed
  upload/capture owner. LIST_CAPTURE uses prepared pipeline demand, never current program
  topology or instance values. Release source memory only after both captures and restoration.
  At playback choose exactly one matching epoch/actual category, then repeat that prepared
  list under P7 adjacent repetition. Unknown externally authored or mixed-state lists that
  can bind programs, alter instance/provoking state, or mix unrecorded primitive runs are
  unsupported for this path. Do not replay them to discover content, mutate them, force VBOs,
  or silently play once. Retain canonical producer ownership to rebuild supported geometry;
  absent such provenance is explicit incompatible-path containment.

Nested forwarding shares the same prepared-submission guard; conversion/preparation happens
once before N repeats. Emit A0…A(N−1),B0…B(N−1), never interleave triangle batches across copies.
All copies reuse the same derived source and ordinary render state. Errors stop remaining
copies without duplicate fallback, restore source/pointer/current/instance state in finally,
then P7 main containment or P8 abort/neutralization. Failure to restore keeps shaders off;
borrowed storage remains owned until restoration no longer references it. No worker GL.
P10 owns these adapters; P1 owns facade restoration/native validation, P7 admission, P4
effective geometry metadata, P13 resources and P9 identity values remain their owners.

#### Cached vanilla model lists — NS-2

This is a second vanilla list owner, not a chunk RenderList alias. Evidence:
[Minecraft 1.12.2 ModelRenderer source](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/model/ModelRenderer.java)
`compileDisplayList` generates one GLAllocation list, opens GL_COMPILE (4864), iterates
`cubeList` calling ModelBox.render with the supplied scale, closes the list and sets
`compiled=true`. `render` contains three branch-local callList sites and child traversal;
`renderWithRotation` contains one; their transforms are outside the compiled body.
`postRender` can trigger compilation but only applies transforms, with no list playback.
[TexturedQuad.draw source](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/model/TexturedQuad.java)
begins QUADS with OLDMODEL_POSITION_TEX_NORMAL, scales positions on the CPU and invokes
Tessellator.draw per quad. The permitted Cleanroom 0.6.12-alpha ModelRenderer patch
confirms the GLAllocation capture entry; MCP 1.12.2 independently resolves the methods
and fields below. These are source/mapping observations, not proof of the configured
Cleanroom 0.6.10-alpha transformed hooks.

**Authenticated owner and source incarnation.** H10-MODEL-LIST-CAPTURE observes every
compileDisplayList invocation, including off-mode and postRender-triggered compilation.
Mod glue attaches a private same-device capture identity to that exact ModelRenderer
object, list allocation incarnation and successful canonical compilation. Numeric list
names, `compiled=true`, matching epochs or a caller-supplied marker cannot mint provenance.
Record the exact first-compile scale bits, canonical producer revision, ordered draw runs,
actual layout/count, original source descriptor, conventionalInputs and complete capture
plan, configuration/resource/world identity, primitive-demand epoch and, at v0.3,
VertexEpoch/layout/attribute-union identity. Base v0.1 identity does not invent
P9 aliases/VertexEpoch. No epoch relabeling promotes an off/NONE/QUADS-only list into a
TRIANGLES product. Configuration/layout/demand changes make old shader products ineligible
before admission; unchanged identity permits repeated playback without recompilation.
Changing animation transforms or a later render(scale) argument alone does not re-bake:
vanilla bakes its first compile scale and uses current scale only in live transforms.
Rebuilding that captured producer uses the recorded scale, never an accidental later
argument; an actual new canonical compile establishes a new scale/incarnation.
Off-mode observation allocates only bounded model-owner provenance, not builder sidecars,
extended formats, converted storage or TLS work; D-P10-4's extended-work fast path
remains intact. Rebuild requires the same proven canonical producer revision; unobserved
custom cube/quad mutation cannot be certified by object identity and is unsupported,
not silently treated as unchanged captured content.

**Actual capture route.** The admitted model adapter brackets the known canonical
compile operation, not glCallList discovery playback. When a shader product is needed,
stage one canonical cubeList→ModelBox→TexturedQuad→Tessellator/uploader traversal with
native submission suppressed and the original glNewList/glEndList envelope deferred.
H10-CLIENT in this authenticated staging scope copies each sealed run before reset;
no live activation, instance upload, P9 event or GL draw occurs during staging. Retain
run boundaries/order and whole records, validate the entire body before native capture,
and produce checked original/conditional triangle streams by the existing conversion law.
Known homogeneous model runs may share one contiguous source/complete pointer plan but
remain distinct native draws (primitive IDs retain original per-draw restart). Unsupported
custom/mixed-state producers, reentrancy into list compilation, untracked native calls or
incomplete capture fail before any playback; do not execute an early accepted prefix.
Every run uses a range within the same authenticated contiguous source and unchanged complete
pointer plan. A differing descriptor cannot trigger an inside-list bind; it requires a separately
bounded admitted product or rejection. For chunks, the canonical
`ChunkRenderDispatcher.uploadDisplayList(BufferBuilder,int,RenderChunk)` receives the complete
builder before opening its list: stage/derive and bind before that envelope, retaining the same
balanced chunk modelview commands and original draw boundaries in each product. Uploader
pointer setup/reset/restoration stays outside the retained body.
The implementation must establish the pinned canonical route/coverage, not trust a
subclass merely because it inherits ModelRenderer. No general renderer rewrite is granted.

Each original/derived list is captured separately under GL_COMPILE, never
GL_COMPILE_AND_EXECUTE, never nested glNewList. P1 bind/isolation precedes glNewList;
all streams/plans are prepared first, geometry-only commands run inside, glEndList
precedes restoration. No transforms, program/instance/owner9 uniforms or restored
predecessor constants may enter the body. Each body is captured once, not N times.
In particular, an OLDMODEL run captures neither COLOR nor UV1 arrays or constants.
The complete authenticated participation/plan is retained with each original/derived
list, not reconstructed from its 56-byte storage. A later fleece color or current
lightmap change remains live at every playback, even when the native list is reused.
Publish the pair/compiled metadata atomically only after every demanded capture and
restoration succeeds. On allocation/capture/end/restore failure invalidate the whole
candidate, close an actually opened list best-effort, retire partial names without
calling them, and invoke ordinary main/off or shadow-abort containment. A failed
recompile never leaves `compiled=true` pointing at a failed/deleted candidate.

**Every cached call.** H10-MODEL-LIST-PLAYBACK wraps all callList invocations in render
and renderWithRotation, even on later frames that never reach an uploader. Before the
first native call, require current authenticated main/root-shadow geometryInput and
owner incarnation, validate the full list body and select a same-epoch compatible
product. An off-compiled or missing-converted product can rebuild once from its proven
canonical owner at this safe pre-call boundary using the retained first-compile scale;
otherwise suppress the call and contain it, never submit stale QUADS to TRIANGLES.
Unknown preinstallation lists are unproven, not adopted by raw name. With no authenticated
shader, ordinary vanilla playback remains ordinary, but stale/failed owned names are
never played. Rebuild may not occur inside an open list or active submission borrow.

Run P1 LIST_REPLAY_GUARD against the current effective linked requirement; retain the
chosen source through all copies and restore only replay-affected state, not dead CPU
pointers. At v0.5 repeat this one geometry-list call under P7, preserving A0,A1,B0,B1,
not the ModelRenderer method, children, matrix operations or capture. P7/P9 surrounding
entity/TE and color rows are established before first playback and restored after their
ordinary enclosing scope; their P6 immediate-if-active/activation refresh timing is
unchanged. No per-model/per-copy owner9 push, re-resolution, upload or captured row
value is added; absent pre-v0.3 owner9 remains the admitted neutral state.

**Retirement.** H10-MODEL-LIST-LIFETIME observes canonical replacement and the existing
[GLAllocation.deleteDisplayLists single/range path](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/GLAllocation.java),
invalidating every overlapping authenticated name/incarnation before native deletion.
Glue owns generated companion names through that same allocation/deletion path, never
through borrowed VertexSource. Model-owner collection queues render-thread retirement
(no GC-thread GL and no registry strong reference keeping models alive); configuration,
world/resource/layout retirement, detach and shutdown also retire all companions after
last submission/binding use. No finalizer is assumed on vanilla ModelRenderer.
Canonical replacement/deletion invalidates companions; reused integer names never
resurrect metadata. Deleting a canonical name also clears its live owner's compiled
eligibility so the next ordinary render cannot call a deleted list. Deletion failure
retains ownership for safe retirement and keeps shader admission closed.

**Transaction placement.** P7 step1 closes admission and drains model capture, playback,
bindings and retirement borrows alongside existing worker/upload borrows. Step4 freezes
configuration/layout/attribute-union/primitive demand; step9 activation and required
invalidation make every old model product ineligible before atomic admission. Lazy
first-use rebuild is permitted only behind the unconditional pre-call guard, not as
permission to submit old products. Step10 off recovery drops derived eligibility and
restores safe canonical vanilla ownership; Pending/Failed retains owners and recovery
tokens, never claims Drained/VanillaReady from cancellation alone. Resource-only NONE
reuses retained configuration but a new resource epoch still invalidates native products.
This also applies to the v0.1 private base adapter without pulling v0.3 P9 installation
forward. P7 must explicitly adopt this lifetime/health extension under R10-6.

#### Cached vanilla sky/star products — R1-1

**Closed producer family and evidence.** The separately read
[vanilla 1.12.2 RenderGlobal source](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/RenderGlobal.java)
has three independent products: upper `generateSky` → `glSkyList`/`skyVBO`,
lower `generateSky2` → `glSkyList2`/`sky2VBO`, and `generateStars` →
`starGLCallList`/`starVBO`. Constructor calls create all three before shader admission;
`loadRenderers` regenerates all three when `vboEnabled` changes. Upper/lower use
`renderSky(BufferBuilder,float,boolean)` with `(16,false)` / `(-16,true)`;
stars use `renderStars(BufferBuilder)` with their canonical fixed-seed producer.
Each emits one POSITION/QUADS run. MCP 1.12.2 resolves the exact methods in §4.11.
The separately identified Cleanroom 0.6.12-alpha RenderGlobal patch adds a custom
sky-handler early return; it does not establish canonical provenance for that handler.
These are source/mapping observations, not configured transformed-runtime proof.

**Incarnation and preparation.** Reuse the model/chunk bounded capture registry, P1
issuance and paired-native lifetime; add no public sky service or raw-name adoption.
Private identity includes exact RenderGlobal owner, device, product kind, canonical
producer revision/arguments, allocation incarnation, actual POSITION descriptor/count,
configuration/world/resource epoch, backend mode, authenticated conventionalInputs,
complete capture/draw plan and prepared primitive demand;
v0.3 additionally uses the existing vertex/layout/attribute-union epoch. Numeric
fields (including a reused list name), object class, or matching epoch alone grant
no source authority. Observe all three generation methods even off-first, retaining
only bounded provenance off: no extended builder sidecars, TLS, conversion or extra
geometry traversal. Do not promote an off/NONE original into a converted product.

At shader-required generation, defer the original GL_COMPILE envelope and suppress
the uploader's native submission while the one canonical producer fills its builder.
Stage/seal the complete POSITION run before reset or native capture. For VBO generation,
the producer calls finishDrawing/reset/bufferData directly, without an uploader:
the generation scope retains sealed count/format/range before reset and authenticates
the exact bytes handed to `VertexBuffer.bufferData`, rather than relying on reset state
or buffer-length divisibility. Checked whole-record expansion follows §4.6 unchanged.
Prepare original and demanded triangle source once from this one run, then capture
separate geometry-only GL_COMPILE lists or upload paired VBOs through the existing
observed owner. No second `renderSky(float,int)` traversal, no second star generation
for the derived product, no playback/readback to discover bytes and no forced VBO mode.
List bind/isolation occurs before glNewList; end occurs before external restoration.
The star producer's surrounding push/pop matrix remains outside both list bodies.
All program, instance, current color, fog, live celestial transforms and restoration
remain outside capture. Publish only after every demanded product and restoration
succeeds; release staged bytes only afterward. Partial capture/upload/end/restore
failure revokes candidates and retires partial names after last borrow, never calls them.

**Every live native site.** `renderSky(float,int)` has four callList anchors:
upper sky, brightness-positive stars, below-horizon lower sky, and final translated
lower sky. It also has three VBO drawArrays anchors for the first three branches.
All seven have distinct health rows; repeated use of `glSkyList2` is not one anchor.
The final lower call is unconditional in the observed source, even in VBO mode where
the canonical field is `-1`. That known absent product emits no native call and is
not authenticated by the sentinel; do not invent a fourth VBO draw to repair vanilla.
If a final lower list actually exists, it requires the same complete provenance,
compatibility and count guard irrespective of mode. Unexpected field replacement
is unproven and contains, not the known absent case.

Before any existing product's first native call, borrow current P7 effective main
geometryInput (or already-authenticated root-shadow authority, never create a sky
shadow traversal), validate incarnation/full run/epoch and select original for NONE
or checked triangles for TRIANGLES. Other geometry categories reject as §4.6 specifies.
An off-first/demand-stale product may regenerate once through its exact canonical
generation method at the safe pre-call boundary, before borrowing the native product;
it must not be inside another capture or submission. This regenerates that bounded
product only, never the sky/world method, events, brightness calculation or sun/moon.
After successful publication, unchanged subsequent calls reuse it; inability to prove
or safely rebuild the producer suppresses the incompatible call and invokes P7
containment before any accepted prefix. NONE/off does not license a failed/deleted
owned name. Unknown custom-sky products are not covered by these canonical rows.

Lists use P1 LIST_REPLAY_GUARD; VBO sites use the existing H10-VBO-DRAW LIVE_DRAW
adapter with actual authenticated POSITION stride/range, not an assumed chunk layout.
At v0.5 P7 repeats only the chosen prepared call adjacently: A0,A1,B0,B1, including
the two distinct lower-sky placements. Site wrappers and VBO delegation share the
single-submission guard, never N². Preparation/upload/capture, transforms, current
color/fog and enclosing sky selection happen once; no capture-time instance uploads.
Keep product/source borrows through every copy and P1 external restoration, then
release. List replay restores affected current values without dead capture pointers;
VBO rollback/restoration includes affected pointers/bindings/arrays, generic0 and
selectors. P7 restores saved instance state. Failure stops copies without duplicate
fallback and uses existing main containment or authenticated P8 abort/neutralization.

**Exact lifetime receivers.** Each generation scope revokes its previous product
before canonical replacement/deletion. Shared GLAllocation single/range deletion
invalidates overlapping sky incarnations before native deletion, with forwarding
deduplicated; VertexBuffer.deleteGlBuffers does the same for all three authenticated
VBO owners and companions. Companion allocation/deletion stays with the observed
native owner, never a borrowed VertexSource. `loadRenderers` brackets the actual
mode-change assignment/regeneration; unchanged mode alone does not regenerate sky.
`onResourceManagerReload` only updates damage icons in vanilla: its P10 observer
explicitly queues resource-epoch invalidation through P7, not a fictional vanilla
sky rebuild. `setWorldAndLoadRenderers` observes world replacement/null detach.
`deleteAllDisplayLists` is empty in this vanilla source, so it is an explicit P10
retirement observer, not proof vanilla deletes sky resources. These named boundaries
invalidate eligibility immediately and defer deletion/mutation until real final-use
drain. Existing P7 removal/shutdown also retires all remaining owned companions;
no GC-thread GL or strong registry reference extending owner lifetime.

R10-7 requires P7 step1 to close/drain sky generation, staging, native playback,
bindings and retirement borrows; step4 freezes backend/configuration/resource/
primitive demand; step9 invalidates old shader eligibility before atomic admission;
step10 restores safe canonical ownership and drops derived eligibility only after
actual drain. Resource-only NONE preserves configuration but changes resource epoch
and invalidates products. Pending/Failed retains owners and recovery tokens, never
claims Drained/VanillaReady; failed deletion/restore keeps admission closed. Lazy
canonical rebuild is allowed only behind the unconditional live guard. These rules
apply already to the v0.1 base adapter, without fictitious P9 installation.

#### Facade boundary

R10-1 adopts the exact `GLDevice.vertexInputs()` service in P1 §4.7.6, including
its source admission and rollback/restore law. It saves the affected domains: generic
arrays/current values/pointers, relevant FF client arrays and pointers, array-buffer
binding, and client-active texture. It preserves the VAO binding rather than resetting
it to zero or clearing all sixteen attributes as Pintonium does. Nested scopes restore
the immediately preceding state in LIFO order. GL state cached by GlStateManager is
changed/restored through it. Current constants are explicitly initialized because array
draws can leave generic current values indeterminate; no previous entity may leak.

The existing `StateService.snapshot()` covers frame state, **not** this complete
vertex domain. It is not cited as proof of restoration. Driver errors use bounded
facade drain windows; a batched error is not falsely attributed to one attribute.

#### Authenticated deferred FastTESR ranges — D-P10-26

Receive P9 §4.12.1/§5.6 and P7 D-P7-54 through the existing private client uploader.
Only an authenticated dispatcher batch attaches the bounded primitive ordinal sidecar;
ordinary builders and shaders-off remain unchanged. Per complete quad retain the active
P9-provided state ordinal, opaque to P10 and never encoded into mc_Entity or vertex bytes.
Nested append owners must align to complete quads; mixed/partial ownership fails preparation.
Track endVertex, addVertexData and Forge putBulkData, plus begin/reset and State save/restore.
Capacity is checked/reserved alongside builder growth under its existing byte cap.

At the original full-batch sort, copy the actual destination-to-source permutation before
record movement and apply that bijection to owners. No distance recomputation, per-owner
sort or equal-distance reordering is allowed. Seal once, form maximal contiguous
(firstQuad,quadCount,stateOrdinal) ranges, and validate the entire partition/identity/source/
layout/topology before any draw. Save/restore carries an immutable corresponding ownership
stamp; count/order changes invalidate or update it through the listed hooks.

At the final native call after Forge preDraw, bind the sealed original or conditionally
converted client source through P1. Checked range offsets/counts refer to four original or
six converted vertices per quad, preserving complete records and the existing diagonal.
P7/P9 enter the fresh range ID scope before its P10 prepared native guard. Repeat only that
native range adjacently, then restore instance and ID before the next range. Do not replay
the Java uploader, population or Forge setup. The complete source remains borrowed through
all ranges and restoration; Forge postDraw/reset run once in finally.
Any preparation failure forbids the whole batch; a later draw/restore failure stops remaining
ranges without original-batch retry, runs independent cleanup and P7/P8 containment.
Per-range primitive-ID restart follows P9's disclosed boundary, not an invented shader offset.

### 4.7 Constant entity/TE attribute candidate: rejected

**Decision D-P10-3: do not adopt Pintonium's alias-valued generic constant.**

Contract check:

1. App C defines a per-vertex **block identity/renderType/metadata** triple from the
   builder stack. App D.4 separately defines `entityId` and `blockEntityId` uniforms.
   Those are not interchangeable namespaces or channels.
2. `shaders.txt:119` declares floating `vec3`. Pintonium's
   `glVertexAttribI3i` is an integer-current-attribute operation at **11**, while the
   classic contract is floating delivery at **10**. Neither literal can be imported.
3. A single draw may batch vertices from several block identities; replacing a stream
   with a draw constant destroys that distinction. Native entity/display-list draws
   lacking a block stack do not thereby acquire an alias-valued block attribute.
4. The assigned behavior digest covers extended block/item/textured buffers but does
   not prove that every native entity render acquires an entity alias in `mc_Entity`.
   There is insufficient evidence to assert equivalence for arbitrary entity/TE
   renderers. PD itself reports missing 1.12.2 blockEntityId/hurt-color plumbing.

Therefore supported buffer-backed geometry retains stream delivery and neutral stack
outside a real block scope. Non-buffer native entity geometry retains neutral generic
inputs and Phase 9's real per-draw uniforms. Neutral constants prevent stale state;
they are not adoption of the alias-delivery trick. Any future optimization needs a
new decision with proof of a homogeneous triple, floating-equivalent conversion,
per-scope reset, and unchanged pack observation. No AGPL transform is part of that path.

### 4.8 Format transition and cache lifecycle

One Phase 7 no-frame transaction owns the transition. P10's VANILLA/QUIESCING/PREPARED/
ACTIVE/RECOVERING labels describe its participant state inside that transaction; session
BAILED is P1's latch, not a second lifecycle or verdict enum. In-frame requests only queue.

1. Stop accepting old-epoch build work and revoke its upload/draw eligibility. Cancel
   queued work, request cooperative cancellation of running tasks, drain/release queued
   uploads, then await worker completion without holding task/builder pool locks.
   Render-thread upload tasks must be cancelled/drained before waiting on workers that
   may depend on them. Polling a transition is explicit; a bounded drain failure
   enters recovery/off, never an infinite render-thread wait.
2. Keep old borrowed alias tables and ordinal maps alive until their last task ends.
   Restore/reset all builder stacks, scopes and saved states. No GL on workers.
3. Re-evaluate the bail registry before changing formats. A latched bail prohibits
   shader activation but never prohibits cleanup/restoring vanilla formats.
4. Construct immutable format projections; save original vanilla defaults exactly once.
   Swap references only at the quiesced boundary. Never mutate a VertexFormat object
   that is a key in LightUtil's map or is retained by a BakedQuad.
5. Invalidate Forge format caches and baked/model products; request a full supported
   model/resource rebuild where their old format references survive. Recreate world
   renderers/chunk VBOs/lists with the new format. Do not assume `loadRenderers` alone
   replaces baked models. OQ-14 selects the proven concrete invalidation strategy.
6. Follow current Phase 7 §5.1/§5.3 exactly: begin/poll at step1, prepare immutable
   layout/lighting/bake identity at step4 before acceptance; after textures and Phase 9
   acceptance at step9 install the exact lookup/map/epoch tuple, require Activated, then
   invoke IdDependentGeometryInvalidator and complete all other required invalidation.
7. Return the existing invalidator Completed once every old product is ineligible, caches/layers
   invalidated and required rebuilds successfully scheduled. It means scheduling
   completed, not every visible chunk already rebuilt. Missing chunks rebuild normally;
   no old chunk is temporarily drawn with the new stride or alias generation.
8. Admit a frame only after Phase 7 atomically installs the coherent pipeline. Any
   failure compensates to shaders-off; no resurrection of the retired old pipeline.

On disable, restore vanilla format references, invalidate models/caches and old meshes,
and rebuild vanilla renderers through the same quiescence boundary. On dimension/world
change revoke old-world products even when ID generation is unchanged. Resource reload
also invalidates UV-bearing products even if aliases match. A VBO-setting change
recreates path-specific products; it does not reinterpret a display list as a VBO.
Video/lighting changes that alter format or baking must enter this transition, not
just call `BufferBuilder.begin` with a newly mutated global object.

#### 4.8.1 Shader lighting/AO policy and bake identity

Phase 3's current master flag map assigns `oldLighting` and `separateAo` here.
RESEARCH Appendix F.1 requires tri-state flags, video-setting priority and AO in
`color.a`; shipped `doc/shaders.properties:27–29` calls old lighting a fixed
block-lighting multiplier with higher-priority user control. These sources establish
behavior/precedence, not complete numeric defaults. D-P10-11 therefore explicitly
chooses the missing-value fallback and fixed-face policy below as local compatibility
decisions, subject to conformance review, never verified external observations.

```java
record ShaderLightingPolicy(boolean oldLighting, boolean separateAo) {}
```

Phase 7 resolves this immutable pair before vertex preparation: user wire key
`oldLighting` is exactly `default|true|false` (absent=`default`) through Phase 3's
codec; explicit user TRUE/FALSE wins, else explicit pack `EngineFlags.oldLighting`
wins, else use **true**. `EngineFlags.separateAo` has no invented user key: explicit
pack TRUE/FALSE wins, else use **false**. P3 resolves its load-time `MC_OLD_LIGHTING`
after the option-macro-free Properties parse with the same user→pack→true rule, before
shader preprocessing. P10 still consumes typed policy, never macro presence, and never
mutates same-build macro state at runtime; `default` and false remain fingerprint-distinct.

At each supported block/model/fluid vertex, retain vanilla/Forge's sampled lightmap
and tint/base color, identify AO separately from the fixed directional shade, then:

- `oldLighting=true`: apply the fixed directional shade once to RGB; false: omit that
  multiplier (factor 1), without disabling lightmap, tint or AO computation.
- The local fixed face factors are DOWN=0.5, UP=1.0, NORTH/SOUTH=0.8,
  WEST/EAST=0.6. Respect an explicitly unshaded model/quad (factor 1); do not infer a
  face from a transformed tangent or rescale already shaded RGB a second time.
- `separateAo=false`: multiply AO into RGB once and retain original alpha.
  `separateAo=true`: do not multiply AO into RGB; write the sampled AO factor to
  color alpha. Where AO is not applied, the factor is 1. This shader-mode channel
  contract intentionally replaces color alpha; the shaders-off path remains untouched.
- Preserve vanilla/Forge sample/interpolation and color quantization order. If an
  adapter cannot identify the pre-AO/pre-directional components of a custom model,
  report incompatibility and fail shader preparation safely; do not divide arbitrary
  packed color, guess missing AO or silently leave one producer on old policy.

The pair is frozen with the prepared vertex epoch, captured by every bake/build task
and incorporated in bake/mesh cache identity. The effective pair changing (or shaders
on/off) invokes §4.8's complete worker/queued-upload drain, invalidates retained baked
models and lighting/format caches, rebuilds world-renderer products, and prevents old
products drawing even if layout/ID bytes are equal. P12 submits REPUBLISH for oldLighting,
with `worldRendererReload` iff effective bake inputs change; P7 ORs in any further required
invalidation found after load, including a changed separateAo policy. `Completed` means invalidation and
rebuild scheduling completed, not all chunks rebuilt. No worker observes a mutable
global setting. Failed preparation converges off and restores vanilla policy/formats.
OQ-14 still gates the concrete safe Forge cache/bake adapter; it is not permission to
replace the renderer or claim lighting parity without exercising these cases.

**Known OQ-14 risk.** Cleanroom BakedQuad retains a final `VertexFormat` reference;
LightUtil retains a ConcurrentMap keyed by format pairs **and** static
`DEFAULT_FROM`, `DEFAULT_TO`, `DEFAULT_MAPPING`. Clearing the map alone is not a
complete fallback. R10-1's platform adapters must either refresh all affected caches
and regenerate retained quads, or bypass the static default-map fast path for shader
format generations with an explicit correct mapping and perform full model rebuild.
Never overwrite old quads' format references while leaving their byte arrays unchanged.
If neither path can be proven safe, leave shaders off and restore the vanilla path.

### 4.9 Coexistence policy and diagnostic

The v0.3 policy is **detect-and-bail**, not attempt integration and not patch the
other renderer. Install `ChunkRendererCompatCheck` through Phase 1's
`BailRegistry.register(CompatCheck)` and return `CompatVerdict.Bail(reasonKey,args)`.
The registry's established `shouldBail()` aggregation and session latch remain owners
of the decision. No competing registry or custom warn-and-continue mode is introduced.

| Evidence / probe | Status and rule |
|---|---|
| `celeritas` mod ID | **Required positive probe.** Actual `forge122` `@Mod`/`mcmod.info` in this checkout declares it; one hit bails |
| `embeddium`, `celeritas_shaders` mod IDs | Retain the DESIGN/PD candidate probes; absence does not clear Pintonium because its actual 1.12.2 ID is `celeritas` |
| `org.taumc.celeritas.impl.render.terrain.CeleritasWorldRenderer` | Exact class-presence probe, verified path in `forge122`; one hit conservatively bails |
| `org.taumc.celeritas.CeleritasVintage` | Exact alternate class-presence probe; do not initialize it |
| `org.embeddedt.embeddium`, `org.taumc.celeritas` roots | Investigation signatures, **not class names** accepted by `isClassPresent`; never pass a package to that API or scan/load its entire tree |
| RenderGlobal world-entry `renderBlockLayer` and `setupTerrain` replacement | Source-confirmed `@Overwrite` evidence; post-transform hook audit catches missing/altered anchors, not reflection on the final method's annotations |
| `loadRenderers` distance behavior | Reference redirects one read of `renderDistanceChunks` to zero, avoiding vanilla storage; it does not establish that the user's setting is assigned zero |
| Nothirium / Vintagium | Known replacement families per assignment. Conservative mod-ID checks `nothirium` / `vintagium` are provisional catalog entries, not source-verified IDs here; OQ-5 must validate actual artifacts and concrete class probes |
| Future Kirino replacement | No guessed ID or package; unsupported backend/hook-health outcome prevents activation until an explicit future adapter exists |

Evaluate at the three Phase 1 points: MOD-phase plugin veto, post-load pre-bootstrap,
and before every format transition. Early probes inspect class resources/metadata
without initializing renderer classes; mod IDs are consulted only when available.
A thrown/indeterminate probe is not compatibility evidence and fails safe. Do not
construct a GL capability profile before a context exists just to run class probes.
P7 §5.1 consumes the actual CompatEvaluation with no replacement enum. Adopt P1
§4.10's `EarlyCompatCheck`/`EarlyCompatContext` and `BailRegistry.evaluateEarly`:
register once before first MOD-plugin evaluation, resource/metadata probes only,
throwing checks Bail, and retain the terminal whole-family veto for the session.
No game/mod-list/GL initialization is legal in the early context.

The dependent vertex hook family is gated together in `mod.mixin.compat.vertex`
under the existing MOD config. Attachments stay inert until P7 admission; this grant
populates no preinit hook. If target load-order evidence requires earlier attachment,
obtain a separately reviewed owner amendment rather than applying ungated hooks.
A missing/overmatched required anchor disables the entire dependent pipeline;
no half-set of stamping and pointer mixins is admissible.

Diagnostic key: `schmaloogium.compat.chunk_renderer_replaced`.

> Schmaloogium shaders are disabled for this session because {renderer} replaces the
> chunk renderer. This version supports the vanilla Cleanroom chunk pipeline only.
> Remove the conflicting renderer to use Schmaloogium shaders, or keep shaders off.
> See the log for the detected mod/class and hook details.

No player yet: follow Phase 1's log-only routing and retain the reason for Phase 12's
GUI; do not fabricate a chat delivery or buffer chat forever. A class-only positive
probe may conservatively reject an inert bundled renderer; that false-positive risk
is an explicit OQ-5 measurement, preferable to corrupting vertex input state.

### 4.10 Growth design — producers post-v0.5

The layout builder appends aligned named fields, validates no overlap, and derives
stride/fingerprint. Existing consumers iterate descriptors and use semantic names;
none assumes `14` ints outside `CLASSIC_56` declaration and its classic oracles.
Producer registry dependencies are explicit: position/UV/quad/block context. Missing
required producer inputs reject capability advertisement; they do not emit an
unimplemented attribute filled with plausible values.

**Canonical addition: `at_midBlock`.** A future layout appends a signed-byte xyz field
at offset 56, with one byte reserved for optional block emission, yielding an example
60-byte stride. The conceptual value is
`64 * (blockCenter - vertexPosition)` in the **same chunk-local coordinate system**.
The block center is local block origin plus `(0.5,0.5,0.5)`, not builder-translated
world coordinates subtracted from a local vertex. The example byte producer uses
unnormalized floating conversion; OF reads vec3, and an independently supported Iris
vec4 variant carries emission 0…15 in w. Out-of-range geometry must get a deliberately
specified representable encoding before that future feature ships; no silent clamp
is promoted to a modern contract here.

`[V:observed — Pintonium common/src/main/java/org/embeddedt/embeddium/impl/render/chunk/vertex/format/ChunkVertexExtendedData.java:42–48]`
proves center-minus-position times 64 as a working mechanism. It does not assign our
location, packing overflow rule or feature advertisement. The future Phase 3 scan and
Phase 4 name/location catalog must adopt this field together; location 13 is a design
candidate only, never a binding in v0.3. Add one descriptor and one producer, then the
existing writers, bulk adapters, fingerprints, saved-state copies and both binders
use the new stride without consumer edits. No new field is enabled in `CLASSIC_56`.

The same name-addressable model reserves `at_velocity`, `mc_chunkFade`, and the `va*`
family. Velocity requires prior entity/BE geometry history and stays unwired. Fade's
only honest 1.12.2 fallback is −1.0. Core-profile inputs need a separate future layout,
not renaming FF fields in classic packs. These are post-v0.5 components, not v0.3 stubs.

### 4.11 Hook ledger — Appendix E rows 3–9

Notation follows Phase 7 §4.10.1: **AROUND** is a dumb delegate that calls the original
inside `try/finally`; AFTER observes successful completion; HEAD/RETURN alone is not
exception-safe scope management. Targets use SRG names plus descriptors. Below, Java
reference names in descriptor explanations expand to their fully qualified internal
names; the exact load-bearing descriptors are provided in the next table. Forge-added
methods are readable-name targets with `remap=false`, not fabricated SRG symbols.

| Hook ID | E row / target | Moment and responsibility |
|---|---|---|
| H10-TASK | E4 helper `ChunkRenderWorker.func_178474_a` | Outermost task guard, create/install task context, detach in finally; synchronous worker and background workers share it |
| H10-BUILD | E3 `RenderChunk.func_178581_b` | Rebuild guard; verify/bind context, handle direct calls, seal/reset layer builders; no duplicate lease when already guarded |
| H10-BLOCK | E3 invocation of `BlockRendererDispatcher.func_175018_a` | AROUND each actual per-block render call inside rebuild; includes fluids and all Forge layers; push/finalize/pop |
| H10-MODEL | E9 `BlockModelRenderer.func_178267_a` | Nested per-builder state scope; finalize dirty appended tail on exit; outer call guards guarantee throw cleanup |
| H10-BEGIN | E5 `BufferBuilder.func_181668_a` | Resolve shader projection before format installation; initialize sidecar after successful begin; no mid-build swap |
| H10-END | E5 `BufferBuilder.func_181675_d` | AROUND count/growth and successful stamp/finalization; reset per-vertex dispatch only on success, invalidate on exception |
| H10-ARRAY | E5 `BufferBuilder.func_178981_a` | AROUND bulk append; capture old count, adapt explicitly typed input, stamp only appended region |
| H10-BULK | E5 Forge `BufferBuilder.putBulkData` | Same bulk protocol; preserve extra-growth allowance and buffer consumption |
| H10-WRITER-* | E5 seven exact setter/advancement rows below | Before scalar storage select authenticated source semantic's destination; source-only advancement, delegation once and exception invalidation |
| H10-BRIGHTNESS-4 | E5 `BufferBuilder.func_178962_a(IIII)V` | AROUND actual last-quad UV1 writes; authenticate pending range, complete only after all four real stores return, invalidate on throw; D-P10-30 |
| H10-SEAL | E5 `BufferBuilder.func_178977_d` | Before finish; finalize dirty tail, reject partial quads, seal metadata |
| H10-STATE | E5 `func_181672_a`, `func_178993_a`, `func_181674_a` | Save/restore epoch/format without restamping; validate and seal before sort/state copy |
| H10-RESET | E5 `BufferBuilder.func_178965_a` | Clear transient state and owner; do not clear independent immutable copied-state stamp |
| H10-TESS | E6 `Tessellator.func_78381_a` | Cleanup guard around its finish/uploader/reset sequence; uploader remains sole draw binder |
| H10-CLIENT | E7 `WorldVertexBufferUploader.func_181679_a` | AROUND real draw after Forge preDraw; explicit LIVE or LIST_CAPTURE mode; finally restore and ensure postDraw/reset |
| H10-UPLOAD | E4 `ChunkRenderDispatcher.func_188245_a` | Validate when queued and on render-thread execution; carry epoch into VBO/list product; stale work cancels normally |
| H10-VBO-UPLOAD | E8 `VertexBuffer.func_181722_a` | Validate source/count/layout before upload, publish metadata only on successful upload |
| H10-VBO-DRAW | E8 `VertexBuffer.func_177358_a` | AROUND actual draw; actual stored stride for both FF and generic pointers, finally restore |
| H10-LIST-REPLAY | E6/E7 path completion, `RenderList.func_178001_a` | Epoch guard before list execution; finally restore recorded generic current values |
| H10-VBO-LAYER | E8 path completion, `VboRenderList.func_178001_a` | Validate layer admission; per-VBO draw wrapper overrides vanilla fixed-stride setup, never duplicate the draw |
| H10-MODEL-LIST-CAPTURE | `model.ModelRenderer.func_78788_d(F)V` | AROUND full compile including glNewList/end/compiled publication; authenticated staging/capture, off/NONE identity and failure cleanup |
| H10-MODEL-LIST-PLAYBACK | `model.ModelRenderer.func_78785_a(F)V`, `func_78791_b(F)V`, every callList invocation | Pre-call full-product/current geometry guard, safe rebuild or containment; adjacent native repeats only |
| H10-MODEL-LIST-LIFETIME | ModelRenderer canonical replacement plus GLAllocation deletion single/range paths | Revoke provenance before deletion/reuse, retire paired products after final use; no GC-thread GL |
| H10-SKY-CAPTURE | RenderGlobal generateSky/generateSky2/generateStars | Full generation staging, replacement and off-first provenance; exact expanded rows below |
| H10-SKY-PLAYBACK | RenderGlobal.func_174976_a(FI)V | Four list and three VBO sites, each guarded before native execution; no whole-sky repetition |
| H10-SKY-LIFETIME | Exact deletion/resource/world/mode/teardown anchors below | Revoke before replacement/reuse; final-use retirement through existing owner lifecycle |

Exact mapping results `[V:mcp — 2026-09-07, MC 1.12.2; ModelRenderer/GLAllocation and RenderGlobal/VertexBuffer deletion additions 2026-09-08]`:

| Owner and readable method | SRG | JVM descriptor |
|---|---|---|
| `renderer.chunk.ChunkRenderWorker.processTask` | `func_178474_a` | `(Lnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V` |
| `renderer.chunk.RenderChunk.rebuildChunk` | `func_178581_b` | `(FFFLnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V` |
| `renderer.chunk.ChunkRenderDispatcher.updateChunkNow` | `func_178505_b` | `(Lnet/minecraft/client/renderer/chunk/RenderChunk;)Z` |
| `renderer.chunk.ChunkRenderDispatcher.uploadChunk` | `func_188245_a` | `(Lnet/minecraft/util/BlockRenderLayer;Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/client/renderer/chunk/RenderChunk;Lnet/minecraft/client/renderer/chunk/CompiledChunk;D)Lcom/google/common/util/concurrent/ListenableFuture;` |
| `renderer.BlockRendererDispatcher.renderBlock` | `func_175018_a` | `(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z` |
| `renderer.BlockModelRenderer.renderModel` | `func_178267_a` | `(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;Z)Z` |
| `renderer.BufferBuilder.begin` | `func_181668_a` | `(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V` |
| `renderer.BufferBuilder.endVertex` | `func_181675_d` | `()V` |
| `renderer.BufferBuilder.addVertexData` | `func_178981_a` | `([I)V` |
| `renderer.BufferBuilder.putBulkData` | Forge-added, `remap=false` | `(Ljava/nio/ByteBuffer;)V`, from Cleanroom patch rather than MCP |
| `renderer.BufferBuilder.putBrightness4` | `func_178962_a` | `(IIII)V`; R4 stable_39 resolution, separately corroborated ordinary last-quad writer |
| `renderer.BufferBuilder.finishDrawing` / `reset` | `func_178977_d` / `func_178965_a` | `()V` / `()V` |
| `renderer.BufferBuilder.getVertexState` | `func_181672_a` | `()Lnet/minecraft/client/renderer/BufferBuilder$State;` |
| `renderer.BufferBuilder.setVertexState` | `func_178993_a` | `(Lnet/minecraft/client/renderer/BufferBuilder$State;)V` |
| `renderer.BufferBuilder.sortVertexData` | `func_181674_a` | `(FFF)V` |
| `renderer.Tessellator.draw` | `func_78381_a` | `()V` |
| `renderer.WorldVertexBufferUploader.draw` | `func_181679_a` | `(Lnet/minecraft/client/renderer/BufferBuilder;)V` |
| `renderer.vertex.VertexBuffer.bufferData` / `drawArrays` | `func_181722_a` / `func_177358_a` | `(Ljava/nio/ByteBuffer;)V` / `(I)V` |
| `renderer.RenderList.renderChunkLayer` / `renderer.VboRenderList.renderChunkLayer` | `func_178001_a` | `(Lnet/minecraft/util/BlockRenderLayer;)V` |
| `model.ModelRenderer.compileDisplayList` | `func_78788_d` | `(F)V` |
| `model.ModelRenderer.render` / `renderWithRotation` / `postRender` | `func_78785_a` / `func_78791_b` / `func_78794_c` | `(F)V` each; postRender is compile coverage, not a playback anchor |
| `renderer.GLAllocation.generateDisplayLists` | `func_74526_a` | `(I)I` |
| `renderer.GLAllocation.deleteDisplayLists` single / range | `func_74523_b` / `func_178874_a` | `(I)V` / `(II)V` |
| `renderer.RenderGlobal.generateSky` / `generateSky2` / `generateStars` | `func_174980_p` / `func_174964_o` / `func_174963_q` | `()V` each |
| `renderer.RenderGlobal.renderSky` producer / live | `func_174968_a` / `func_174976_a` | `(Lnet/minecraft/client/renderer/BufferBuilder;FZ)V` / `(FI)V` |
| `renderer.RenderGlobal.renderStars` | `func_180444_a` | `(Lnet/minecraft/client/renderer/BufferBuilder;)V` |
| `renderer.RenderGlobal.loadRenderers` / `deleteAllDisplayLists` | `func_72712_a` / `func_72728_f` | `()V` each |
| `renderer.RenderGlobal.onResourceManagerReload` | `func_110549_a` | `(Lnet/minecraft/client/resources/IResourceManager;)V` |
| `renderer.RenderGlobal.setWorldAndLoadRenderers` | `func_72732_a` | `(Lnet/minecraft/client/multiplayer/WorldClient;)V` |
| `renderer.vertex.VertexBuffer.deleteGlBuffers` | `func_177362_c` | `()V` |

Owner prefixes in the table are `net.minecraft.client.`. `updateChunkNow` is a mapped
coverage entry, not another stack owner; its invoked worker/rebuild guards do the work.

**C1 exact writer expansion — D-P10-27.** MCP stable_39/SRG resolved 2026-09-08;
all owners are `net.minecraft.client.renderer.BufferBuilder`. `B` in descriptors below
expands to `Lnet/minecraft/client/renderer/BufferBuilder;`, not a literal JVM type.
Every row is CORE, ownerPhase=10, expected=1 method wrapper, required in the full
v0.3 ledger and retained at v0.5; none enters the v0.1 base-only subset.

| Exact catalog ID | Mapped target and descriptor | Coverage |
|---|---|---|
| H10-WRITER-POS | `func_181662_b(DDD)B` | POSITION0 dispatch before the first store; translation/growth/failure |
| H10-WRITER-COLOR-INT | `func_181669_b(IIII)B` | COLOR0 stores, noColor no-write/no-advance, one advancement |
| H10-WRITER-COLOR-FLOAT | `func_181666_a(FFFF)B` | Original integer delegation and exception guard; no second store/advance |
| H10-WRITER-TEX | `func_187315_a(DD)B` | UV0 dispatch independent of physical COLOR/lightmap/normal cursor |
| H10-WRITER-LIGHTMAP | `func_187314_a(II)B` | UV1 dispatch and preserved component order |
| H10-WRITER-NORMAL | `func_181663_c(FFF)B` | NORMAL0 dispatch independent of omitted COLOR/UV1 |
| H10-WRITER-ADVANCE | `func_181667_k()V` | Active setter source-only advancement; reject unscoped active advance; original off path |

Setter wrappers call ordinary bodies only after semantic selection. ADVANCE explicitly
replaces the original physical traversal on the active path; AROUND's ordinary delegation
rule otherwise remains intact. Each wrapper's success/exception/finally behavior is part
of its one health row, not a healthy HEAD plus unaudited cleanup. These are method-wrapper
counts, not runtime invocation totals or guessed RETURN counts. Existing H10-BEGIN,
H10-END, H10-ARRAY, H10-BULK, H10-RESET, H10-SEAL and H10-STATE save/restore/sort
subrows also audit §4.2's pending-vertex, initialization, transition and invalidation
coverage; reset-only or missing nested-color cleanup is unhealthy.

R10-8 requires P7 to receive all seven new IDs plus the amended existing row semantics
in its single installed owner10 report, sort the full unique catalog, renew its canonical
SHA-256 fingerprint and compare exact required expected/actual rows. Missing, duplicate,
overmatched or incomplete exception coverage prevents full v0.3/v0.5 shader admission;
an older full-ledger fingerprint cannot clear it. Preserve P7 primary IDs, scalar row
shape, HEALTHY/FEATURE_DISABLED vocabulary and unchanged v0.1 base fingerprint.

**D-P10-30 health delta:** add exact ID `H10-BRIGHTNESS-4`, ownerPhase=10,
CORE, expected=1 exception-safe method wrapper, target
`net.minecraft.client.renderer.BufferBuilder.func_178962_a(IIII)V`. Full v0.3/v0.5
catalog/fingerprint only; the seven H10-WRITER-* rows and v0.1 base subset remain.
Amend H10-MODEL/BLOCK/ARRAY/BULK/STATE/SEAL/RESET and upload/capture/draw admission
coverage to audit pending UV1 ownership, all-real-store completion and failure cleanup.
P7 receives this exact additional row through the same installed owner10 subreport;
P2 forwards it without treating source mapping as transformed cardinality. Missing,
duplicate, overmatched or non-exception-safe completion coverage makes the full CORE
group unhealthy/off; no old fingerprint or Forge-lighting-only waiver is accepted.

All listed rows are one required **CORE vertex health group** at v0.3. Initial expected
cardinality is one per selected method/semantic invocation anchor; multi-method rows
expand into named subrows, each one. Exact RETURN counts, delegation ordinals and
queued-upload callback anchors must be established from the pinned transformed
bytecode at implementation, never inferred from mapping existence. Use Phase 7's
`require=0` plus audit policy rather than fatal injection application. Unmatched,
overmatched, missing exception cleanup or uninstrumented effective upload callbacks
means unhealthy/off, not silent success.

Export ownerPhase=10 through Phase 7's existing `HookApplicationSubreport`: sorted
unique catalog IDs, canonical SHA-256 fingerprint, expected/actual counts, aggregate
featureEnabled, and `HEALTHY|FEATURE_DISABLED`. Preserve Phase 7 primary row identities.
The grouping is CORE for activation safety even though the shared subreport vocabulary
calls the disabled disposition FEATURE_DISABLED. No stale report claims healthy Phase
10 before its hooks are installed. Optional profiler hooks are not health evidence.

The v0.1 primitive adapter activates the base-format portions of H10-BEGIN/SEAL/STATE/RESET,
H10-TESS/CLIENT/UPLOAD/VBO-UPLOAD/VBO-DRAW/LIST-REPLAY/VBO-LAYER and
H10-MODEL-LIST-CAPTURE/PLAYBACK/LIFETIME and the seventeen H10-SKY-* expansions
below, plus H10-TASK/BUILD solely for source/paired-product cancellation and final-use
tracking. Model and sky rows are CORE, ownerPhase=10, in both the v0.1 required subset
and full v0.3 ledger. It performs no extended stamping/block stack/quad-attribute math.
These exact anchor rows form its required health subset; missing/overmatched cleanup
or upload coverage prevents the affected .gsh path.
Its owner10 subreport fingerprints that explicit milestone subset, not the absent extended
feature. At v0.3 the full ledger is required, and the new fingerprint invalidates old admission.
P7 must check the installed milestone's exact required rows, never infer extended readiness
from the earlier base report. No new health enum or second owner10 report is introduced.
Model subrows identify render's three call sites and renderWithRotation's one separately;
compile coverage includes calls from postRender, and lifetime subrows cover single/range
deletion forwarding without double retirement. These source counts are evidence, not
guessed transformed cardinalities. Missing any effective branch, staging envelope,
deletion/reuse or exception-cleanup coverage prevents affected .gsh admission before
the first model draw; it cannot report only chunk-list coverage as healthy.
Canonical catalog expansions are H10-MODEL-LIST-CAPTURE-COMPILE;
H10-MODEL-LIST-PLAYBACK-RENDER-ZERO, -RENDER-PIVOT, -RENDER-ROTATED,
-WITH-ROTATION; and H10-MODEL-LIST-LIFETIME-REPLACE, -DELETE-SINGLE,
-DELETE-RANGE. The full prefixes apply to every abbreviated suffix. Owner10's
fingerprint includes each expanded ID, with the ordinary §4.11 audit/cardinality law.

**R1-1 exact sky expansion (MCP/source evidence read 2026-09-08).** Each ID below is
CORE, ownerPhase=10, required in the v0.1 base subset, retained in the full v0.3
subset and still required for v0.5 adjacent repetition. They are additive to every
existing required row, not a replacement milestone report. Each named semantic
anchor has expected count one; actual transformed cardinalities and cleanup paths
must be audited under the preceding law before admission. Shared deletion method
instrumentation can serve model and sky logical rows but cannot double-retire.

| Exact catalog ID | Exact selected anchor and obligation |
|---|---|
| H10-SKY-CAPTURE-UPPER | AROUND RenderGlobal.func_174980_p()V, including constructor/direct/rebuild invocation |
| H10-SKY-CAPTURE-LOWER | AROUND RenderGlobal.func_174964_o()V, same complete scope |
| H10-SKY-CAPTURE-STARS | AROUND RenderGlobal.func_174963_q()V, same complete scope |
| H10-SKY-PLAYBACK-LIST-UPPER | func_174976_a(FI)V callList consuming glSkyList |
| H10-SKY-PLAYBACK-LIST-STARS | Same method callList consuming starGLCallList inside brightness-positive branch |
| H10-SKY-PLAYBACK-LIST-LOWER-HORIZON | Same method callList consuming glSkyList2 inside below-horizon branch |
| H10-SKY-PLAYBACK-LIST-LOWER-FINAL | Same method final translated callList consuming glSkyList2, outside VBO conditional |
| H10-SKY-PLAYBACK-VBO-UPPER | Same method skyVBO.drawArrays(I)V invocation, forwarding once to H10-VBO-DRAW |
| H10-SKY-PLAYBACK-VBO-STARS | Same method starVBO.drawArrays(I)V invocation, same guard |
| H10-SKY-PLAYBACK-VBO-LOWER-HORIZON | Same method sky2VBO.drawArrays(I)V invocation, same guard |
| H10-SKY-LIFETIME-DELETE-SINGLE | GLAllocation.func_74523_b(I)V, authenticated sky overlap invalidation |
| H10-SKY-LIFETIME-DELETE-RANGE | GLAllocation.func_178874_a(II)V, same range/incarnation rule |
| H10-SKY-LIFETIME-DELETE-VBO | VertexBuffer.func_177362_c()V, authenticated owner/companion invalidation |
| H10-SKY-LIFETIME-VBO-MODE | AROUND RenderGlobal.func_72712_a()V actual vboEnabled change/regeneration scope |
| H10-SKY-LIFETIME-RESOURCE | AROUND RenderGlobal.func_110549_a(IResourceManager)V, queued epoch retirement |
| H10-SKY-LIFETIME-WORLD | AROUND RenderGlobal.func_72732_a(WorldClient)V, replacement/null-detach retirement |
| H10-SKY-LIFETIME-DELETE-ALL | AROUND RenderGlobal.func_72728_f()V, explicit observer of otherwise empty vanilla method |

The v0.3 owner10 CORE report additionally enumerates each batch-only row below, expected=1
per static target/anchor, with no aggregate count hiding missing subtargets:

| ID | Exact target / action |
|---|---|
| H10-TE-BATCH-OWNER-END | BufferBuilder.func_181675_d()V, completed vertex ownership |
| H10-TE-BATCH-OWNER-ARRAY | BufferBuilder.func_178981_a([I)V, checked bulk ownership |
| H10-TE-BATCH-OWNER-BULK | Forge BufferBuilder.putBulkData(ByteBuffer)V, remap=false, checked bulk ownership |
| H10-TE-BATCH-OWNER-SAVE | BufferBuilder.func_181672_a()BufferBuilder.State, copy ownership stamp |
| H10-TE-BATCH-OWNER-RESTORE | BufferBuilder.func_178993_a(BufferBuilder.State)V, restore matched stamp |
| H10-TE-BATCH-OWNER-BEGIN | BufferBuilder.func_181668_a(IVertexFormat)V, start/reset association |
| H10-TE-BATCH-OWNER-RESET | BufferBuilder.func_178965_a()V, clear association |
| H10-TE-BATCH-SORT | actual permutation point in BufferBuilder.func_181674_a(FFF)V before record movement |
| H10-TE-BATCH-RANGES | final native invocation in WorldVertexBufferUploader.func_181679_a(BufferBuilder)V after preDraw and before postDraw/reset |

AROUND/finally applies wherever ownership/source state is borrowed. Existing H10 hook
implementations may share the adapter, but every listed application is independently
audited/fingerprinted. Missing coverage keeps v0.3 shader admission closed, not geometry omitted.

Replacement is part of each of the three CAPTURE scopes, not an unnamed extra hook.
VBO source sealing-before-reset and all upload calls are obligations of those scopes
plus existing H10-SEAL/RESET/VBO-UPLOAD; no uploader-only health substitutes for them.
P7 removal/shutdown is the already-installed lifecycle operation, not a guessed
Minecraft hook. All seventeen rows enter the sorted unique owner10 fingerprint at
each milestone; old chunk/model-only fingerprints cannot admit affected sky .gsh.
Missing/overmatched sites, cleanup, off-first observation or VBO source coverage close
affected admission before playback, not merely emit a FEATURE_DISABLED label and draw.

## 5. Cross-phase interfaces

This section is the monitored interface region. Detailed semantics in §§4.1–4.11 are
incorporated by the rows below. R10-2 is owner-granted/receiver-adopted, unverified;
R10-1 and R10-5 are now owner-granted/receiver-adopted, unverified. No dependency private type is consumed.

### 5.1 Exposed Phase 10 contracts

| Contract | Exact content and ownership | Consumer |
|---|---|---|
| `VertexLayout` / `VertexField` / `VertexInputPlan` | Immutable named physical fields/stride and complete plan: layoutFingerprint, pointers, nonnull immutable Set<ConventionalInput> conventionalInputs, expectedGeometryInput; enum POSITION,COLOR,UV0,UV1,NORMAL. §§2.2/4.1/4.6 D-P10-29 source participation and full authenticated product identity incorporated; CLASSIC_56 remains 56 | mod adapters, P1 existing facade, P7 prepared submission, conformance; G8 growth |
| `VertexEpoch` | Independent vertex serial, world epoch, ID generation, layout fingerprint; equality necessary, not sufficient, for product admission | task/upload/state adapters; Phase 7 composition |
| `VertexPipelineLifecycle` | Exact accepted P7 §5.1 begin/poll/prepare/activate/restore operations below; no active frame or stale worker overlaps mutation | P7 installed participant, unverified |
| `VertexProgramInputSink` | Exact accepted P7 §5.1 authenticated main/shadow enter/leave/reset; no program selection or GL upload | P7 installed adapter, unverified |
| `IdDependentGeometryInvalidator` implementation | Implements the **existing Phase 7 interface**, exact `IdPublicationChange` and closed results; Completed only after old-product ineligibility, layer invalidation and successful scheduling | Phase 7 |
| Builder/task/state/mesh sidecar protocol | §4.2/§4.4/§4.5 including D-P10-27 source-semantic scalar dispatch independent of CLASSIC_56 cursor, omitted-field initialization, vertex-boundary bulk/state transitions and exception invalidation; immutable handoff, exclusive writers, no source mutation | `mod.glue` vanilla adapters, not a public Minecraft API in engine |
| `ChunkRendererCompatCheck` | Existing Phase 1 `CompatCheck`, fixed check ID `schmaloogium.chunk_renderer`, table §4.9, session Bail | Phase 1 registry, Phase 7 admission, Phase 12 diagnostics |
| Owner-phase-10 hook subreport | Exact shared `HookApplicationSubreport` shape, fingerprint and rows §4.11; R10-8 adds seven H10-WRITER-* CORE rows and amended ingress cleanup coverage to full v0.3/v0.5, not v0.1 base | Phase 7 report composer / Phase 2 manifests; receiver-adopted per P7 D-P7-62/D-P7-75 with identical P2 forwarding, unverified; fresh applicable review remains |
| Growth producer requirements | Position/UV/block-context prerequisites, immutable name/layout cutover; no modern runtime producer in v0.3 | G8/S4, Phases 3/4 |
| `ShaderLightingPolicy` | immutable effective `oldLighting`/`separateAo`; exact shader-visible RGB/AO-alpha semantics, local defaults and precedence in §4.8.1; captured in transition/bake identity and never sampled per vertex from mutable settings | Phase 7 preparation, Phase 12 reload policy, mod bake/build adapters |
| Conditional primitive submission plan | §4.6 category translation, checked complete-record expansion, topology/provoking/primitive-ID policy and client/VBO/list paired lifetime; no source mutation | P7 private submission adapter; P1 facade; v0.1 affected .gsh paths |
| Authenticated cached-model products | §4.6 ModelRenderer capture/incarnation/scale, paired-product selection, every-call guard and final-use retirement; exact CORE hook rows in §4.11 | P7 R10-6 lifecycle/health receiver; no public renderer API |
| Authenticated cached sky/star products | §4.6 three canonical producers, off-first incarnation, full staged source, paired lists/VBOs, seven guarded sites, external restoration and retirement; all seventeen §4.11 CORE rows | P7 R10-7 exact lifecycle/health receiver; existing P1 facade only |
| Deferred FastTESR ownership/ranges | D-P10-26 incorporates P9 §4.12.1/§5.6 complete sorted per-quad ownership, sealed partition and native adjacency; seven owner-state subrows plus sort/ranges, no vertex alias substitution | P7 D-P7-54 and P9 actual native ID scopes |
| Inherited conventional-input delivery (R10-9) | D-P10-29 §§4.1–4.6: source-absent COLOR/UV1 storage is padding/hygiene, never automatically enabled, reset or captured; disable foreign arrays temporarily without changing inherited current values. Source-provided BLOCK streams and existing normal/tangent policy preserved. Complete source participation/plan authenticates state/client/VBO/capture/replay products; each playback uses current color/UV1, not first-compile values | P1 complete bind/restore receipt for all modes; P7 existing lifecycle/prepared-submission receipt, no new hook IDs or native API |
| Completed block-model producer — D-P10-30 | §4.2 authenticated ITEM partial append→BLOCK final participation→func_178962_a real four-value UV1 completion; same builder/scope/epoch/range obligation, immutable cached source, rejection before safe model/seal/state/upload/capture/draw on missing/throwing completion. §4.11 H10-BRIGHTNESS-4 CORE expected1 full-v0.3/v0.5 receipt and amended cleanup rows incorporated; OLDMODEL inheritance unchanged | P1 complete-plan authority, P7 owner10 health/admission, P2 evidence |

Accepted R10-2 lifecycle operations (render thread only, unverified):

```java
interface VertexPipelineLifecycle {
    TransitionStart beginTransition(VertexTransitionRequest request);
    QuiesceResult pollQuiescence(VertexTransitionToken token);
    PrepareResult prepare(VertexTransitionToken token, VertexLayout layout,
                          ShaderLightingPolicy lighting);
    ActivateResult activate(VertexTransitionToken token, VertexEpoch epoch,
                            AliasLookup aliases);
    RecoveryResult restoreVanilla(VertexTransitionToken token);
}
```

`VertexTransitionRequest` carries reason, target mode VANILLA/SHADER, world epoch,
expected previous vertex serial and the target layout fingerprint. Glue freezes the
matching ordinal bridge alongside it; the engine never receives Minecraft objects.
The token is opaque, issued by this lifecycle instance, exclusive and non-reusable.
Closed start outcomes: `Started(token)` or `Rejected(WRONG_THREAD|FRAME_OPEN|
TRANSITION_OPEN|STALE_EPOCH|INVALID_REQUEST)`. Quiescence outcomes: `Pending`,
`Drained`, `Failed(diagnosticId)`. Prepare outcomes: `Prepared` or `Failed(diagnosticId)`;
activate outcomes: `Activated(epoch)` or `Rejected(STALE_EPOCH|NOT_PREPARED|
LOOKUP_GENERATION_MISMATCH|HOOK_UNHEALTHY|BAILED)`. Recovery outcomes:
`VanillaReady` or `Failed(diagnosticId)`; recovery failure keeps shader admission
closed and discards old geometry, never authorizes unsafe reuse. Only Drained permits
prepare/format mutation. Prepare owns no new alias publication; activate borrows one
kept alive by Phase 7. Only Activated/VanillaReady consumes the token. Intermediate
failure retains a recovery-only token for poll/restore, never shader preparation retry.
After Activated, a later composition failure starts a new VANILLA transition against
the actual serial. P7 §5.1's exact null/foreign/thread/order errors, partial/absent
installation, removal/shutdown and failed-drain lifetime rules are incorporated unchanged.
No Failed or timeout frees a still-borrowed lookup/map, fakes Drained or certifies vanilla
geometry safe. Restore requires actual drain; failed recovery retains its token and owners.

This does not add an ID runtime publisher or bypass Phase 7's atomic pipeline install.
The lifecycle's `Activated` means vertex readiness, not independent frame admission.
`IdDependentGeometryInvalidator.Completed` preserves Phase 7's scheduling-only gate;
its existing rejections remain `STALE_GENERATION`, `INVALID_CHANGE`,
`SCHEDULER_UNAVAILABLE`, and failures carry Phase 7 `FailureId`.

Accepted R10-2 declaration delivery:

```java
interface VertexProgramInputSink {
    InputScopeResult enter(VertexActivationInput input);
    void leave(VertexInputScope scope);
    void reset();
}
```

`VertexActivationInput` contains vertex epoch, registry generation, effective provider
identity, immutable exact declared names, and whether this is a successful shader
activation. Issuance/validation is by the Phase 7 adapter holding that successful
activation; a public record with matching numbers alone cannot authorize a draw.
The same authenticated input additionally carries P4 GeometryInputRequirement unchanged
under adopted R10-5; only mod glue translates it to a registry-independent P10 plan.
Before extended P10 installation, P7's existing private activation/submission adapter carries
this geometry fact independently, without a VertexEpoch/AliasLookup or fake input scope.
`InputScopeResult` is `Entered(opaque scope)` or
`Rejected(STALE_EPOCH|NO_ACTIVE_SHADER|INVALID_DECLARATION|WRONG_THREAD)`.
Nested enter/leave is LIFO; leave restores the preceding effective declaration set.
`reset` invalidates every scope. Draw adapters borrow the current valid scope for the
operation only. LIST_CAPTURE has a separate prepared-epoch union plan, never an
Entered shader credential. The sink performs no GL and is **not** a Phase 4 barrier
participant; P7's accepted adapter invokes it after actual successful main/fullscreen or
authenticated P8 root-shadow activation and at restoration boundaries. P7 §5.1's complete
LIFO/suspension/reset, invalid-leave containment and no-call-after-detach semantics bind here.
P7 copies effective attributes, not requested-child state; no main activation is opened
inside shadow. Sink reset is non-GL, not proof of native array restoration.

P7 §5.1 owns the all-or-none installed lifecycle/sink/invalidator/owner10 bundle. Its exact
step1 real worker/upload drain, step4 prepare(token,layout,lighting), step9 accepted P9
lookup/map/epoch/policy tuple→Activated→invalidation→atomic admission, and step10 off
recovery are receiver-adopted. Apply to every format/resource/world/video/effective-lighting
change, including unchanged ID bytes, and coalesce renderer invalidation once. P6 final-use
retirement and P13 resource ownership are unchanged. P10 only borrows P9 publications.
Missing P9/provider before v0.3 is inert absence, never fake health or empty aliases;
missing required participant at v0.3 prevents vertex shader admission.

### 5.2 Consumed contracts: existing, not inferred

| Dependency | Actual exposed input | Use / limitation |
|---|---|---|
| Phase 4 §5.1 fixed attribute row | 10/11/12 exactly | Direct Phase 10 consumption; does **not** grant active-program query or registry internals |
| Phase 7 §5.1 / §5.5 | `IdDependentGeometryInvalidator`, `IdPublicationChange`, closed invalidation results | Concrete implementation supplied here; all generations invalidate, even identical bytes |
| Phase 7 §5.1 | `FrameToken`, frame abort taxonomy, `ShaderReloadController` queuing and report/subreport shapes | Glue uses existing orchestration and diagnostic vocabulary; no independent frame driver |
| Phase 7 §5.3 | Quiesced coordinated publication, IDs after textures/invalidation before atomic Active, every failed rebuild to off | Adopted alongside amended Phase 9 §5.3; all remain unverified |
| Phase 7 §5.1/§5.3/§5.5 | R10-2 lifecycle/declaration/lighting, exact placement, owner10 subreport and actual BailRegistry | adopted unverified; all hook rows claimed in §4.11, no missing R10-2 owner design |
| Phase 9 §5.1 / §4.10 | `AliasLookup.generation()` and `mcEntity(int)`, `BlockStampResult` | One immutable lookup per task, exact two words, no alias re-resolution |
| Phase 1 §5.3 / §4.10, narrow extra input | `CompatCheck`, `CompatContext`, `CompatVerdict`, `BailRegistry`, `EarlyCompatCheck`, `EarlyCompatContext` | Exact ordinary/early evaluation, retained whole-family MOD veto and bootstrap-safe diagnostics adopted; no fabricated `Compatible` |
| Phase 1 §5.1/§5.2 / §4.7.6 | D-P1-50 package seam and exact `GLDevice.vertexInputs()` service/source/result/recorder contract; `maxVertexAttribs`, diagnostic/error conventions | Receiver-adopted/unverified; entire provenance, pre-mutation rejection, expected-vs-linked comparison, partial rollback and LIFO restoration contract incorporated |
| Phase 7 §4.6 / §5.1 countInstances | Authenticated effective count and prepared-submission policy, saved instance restoration, single-wrapper guard and main/shadow failure routing | Existing client/VBO/list-playback adapters integrate at v0.5; no whole traversal, capture or upload repetition |
| Phase 3 §§4.8/5 engine flags and codec, through Phase 7 | exact-current configuration and nested IdMappingInput receipt (23 adopted by D-P10-28); `oldLighting` user tri-state and pack oldLighting/separateAo tri-states; reject every other schema/mismatch before derivation; no P10 binary-asset access | Phase 7 resolves §4.8.1 pair, preserving current same-build macro/materialization identity; no pack reopening or private parser |

Phase 3 attribute names still flow through Phase 4's effective state; the narrow
lighting/codec authority is consumed through Phase 7's immutable resolved input,
not a new direct front-end dependency. Phase 9's glue-owned ordinal map is installed
with the exact lookup and retained until the final worker borrow ends.

**Schema23 receiver receipt — D-P10-28, 2026-09-08 (unverified).** Receive P3's
range-capable selector shape through P7; P9 alone resolves registries and P10 does not
select mapping rules. Composition requires containing configuration and nested
`IdMappingInput` each to satisfy `schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION`
(23 at this adoption) and agree before extracting lighting/vertex inputs, preparation,
retention or reuse. Any received inspection snapshot must match that same current schema and
configuration identity before reporting. Reject old/future/mismatched data without relabeling,
deleting old profile fields or manufacturing defaults/empty components. Earlier dated schema22/21/20/19
addenda and decisions are historical, not competing current gates. Preserve current
configuration and MaterializedSource-v23 identity; old derived caches cannot be
renamed current. P4 D-P4-43's `RegistryFingerprint/profile-selection-v3` remains opaque identity
carried normally through P7 (D-P4-31's own-build fields retained within it), not a P10 parser,
new getter or resolution-evidence gate. Effective-provider
geometry/attribute delivery, lighting, vertex semantics, actual matched lookup/worker/product
lifetimes and provider behavior are unchanged. Same-load assets stay owner-held; D-P3-69's nine
source-free trees and projectionVersion=1 are unchanged. No binary acquisition, timing API or implementation clearance;
fresh owner/receiver review remains required.

### 5.3 Required owner changes and adoption status

| Request | Owner | Required binding change | Gate |
|---|---|---|---|
| R10-1 | Phase 1 | D-P1-50 §§4.7.6/4.10/5 exact package, facade and early context grant incorporated below | Owner-granted/receiver-adopted, unverified; fresh review and native proof remain |
| R10-2 | Phase 7 | Owner-granted §0.43/§5.1/§5.3, receiver-adopted here: exact lifecycle/declaration/lighting, real worker/upload drain, matched P9 lifetime, all transitions/off and owner10/Bail | Fresh P7/P10 verification gates integration; no longer missing owner adoption |
| R10-3 | Phase 9 | Owner-designed/receiver-adopted unverified in P9 §§4.1/5.3/5.4: publish after textures, every rebuild failure to off, matched lookup/map close after workers drain | Fresh Phase 9/10 verification remains; no longer a missing owner design |
| R10-4 | Phases 3/4, via future G8/S4 | Extend declared-name and pre-bind catalogs in the same change that adds a modern producer/layout; no fixed-location Phase 10 private enum extension | post-v0.5 attribute activation only |
| R10-5 | Phase 4 via P7 | D-P4-28 §§4.8/4.9/5 exact ProgramStateBundle.geometryInput after actual linked agreement, effective provider only, FixedFunction clears | Owner-granted/receiver-adopted, unverified; no private active-program access |
| R10-6 | Phase 7 | Adopt §4.6 cached-model lifetime/transaction placement and §4.11 H10-MODEL-LIST-CAPTURE/PLAYBACK/LIFETIME as CORE owner10 rows in v0.1 base and v0.3 full fingerprints; current main/root-shadow geometryInput before every cached call | P7 D-P7-32 grants exact expanded rows and ten-step placement; receiver-adopted, unverified |
| R10-7 | Phase 7 | Complete sky/star law and all seventeen owner10 CORE rows with actual current geometry/count at seven sites | P7 D-P7-53 grants receiver/lifecycle/fingerprint placement; adopted, unverified |
| P9 deferred native ranges | Phases 9/7 | D-P10-26 receives P9 §4.12.1/§5.6 and P7 D-P7-54; actual full sort permutation, complete append/state coverage and fresh ID scope per native range | Coordinated adoption, unverified; no new public facade or renderer API |
| R10-8 | Phase 7 / Phase 2 evidence forwarding | Receive seven §4.11 H10-WRITER-* IDs and amended existing ingress row coverage in full v0.3/v0.5 owner10 catalog/fingerprint; exact per-row count/cleanup audit, unchanged v0.1 subset | Receiver-adopted per P7 D-P7-62 (all seven H10-WRITER-* rows, fingerprint recompute, unchanged v0.1 subset, identical P2 forwarding) and D-P7-75 (H10-BRIGHTNESS-4 full v0.3/v0.5 only), unverified; fresh applicable review remains required |
| R10-9 | Phases 1/7 | Receive D-P10-29 complete immutable conventionalInputs plan/domain and authenticated source/product identity, source-keyed Forge setup/cleanup, absent COLOR/UV1 live inheritance and capture exclusion across client/VBO/list; current values remain live at every cached playback | P1 D-P1-68 §4.7.6/§5.2 exact receipt read and adopted, unverified; P7 D-P7-70 conventionalInputs receipt adopted, unverified; fresh applicable review remains, no new native ownership API |

**R10-1 receiver adoption.** P1 §4.7.6's complete declarations and semantics are binding,
not a receiver-owned alternative. The core signatures are:

```java
interface VertexInputService {
    VertexBindResult bind(VertexSource source, VertexLayout layout,
                          VertexInputPlan plan, VertexBindMode mode);
    void restore(VertexBinding binding);
}
enum VertexBindMode { LIVE_DRAW, LIST_CAPTURE, LIST_REPLAY_GUARD }
```

`GLDevice.vertexInputs()` supplies the service. `VertexSource` is a closed client-range, borrowed-VBO,
or display-list-replay source family: client source owns no memory and retains its
live ByteBuffer range for the scope; VBO/list sources are opaque same-device handles
issued only by the glue that owns the vanilla object. No public raw-name adoption,
delete, upload, or lifecycle privilege is granted by this input service. Upload stays
vanilla-owned and observed through the upload adapter. A retained source expires on
vanilla object deletion, buffer reallocation, task reset or epoch retirement.

`VertexBindResult` is `Bound(binding)` or
`Rejected(INVALID_SOURCE|STALE_SOURCE|INVALID_LAYOUT|INVALID_PLAN|WRONG_THREAD|
OUT_OF_RANGE|UNSUPPORTED_INPUT)` or `Failed(diagnosticId)`. Validate all provenance,
range/count/stride, capability and mode conditions before touching GL. Binding is
opaque, LIFO, one-close and render-thread-only; restore also handles partially applied
state after a backend failure. The backend maps storage to native enums and records
source kind/identity, layout, offsets, normalization, enable set and restored state.
Use existing `drainErrors` semantics for driver evidence; wrong provenance appends no
native call. Recorder scripts need pointer/setup/restore failures and source lifetime
rejection; facade recording cannot by itself prove native GL display-list capture.

P1's public marker categories are untrusted without its private issuance authentication.
Use its exact `VertexBindRejection` domain and recorder fixture factories/events, not
locally minted raw-name sources. Source/range/epoch lifetime extends through restoration
and every adjacent instance.
The exact list fixture is
`borrowedVertexList(String label, VertexLayout layout, VertexInputPlan capturePlan)`
(P1 D-P1-70): issue with the completed immutable captured plan, including ordered
generic union and conventionalInputs, and its matching layout. P1 validates and
privately retains that authority before the first replay; geometry comes from the
plan. Remove the former geometry-only overload; never teach the recorder its plan
from a replay request. Identical CLASSIC56 layouts with distinct masks are distinct
authenticated fixture products. This receipt does not authorize production raw names.
LIVE_DRAW/LIST_REPLAY_GUARD compare nonnull plan
`expectedGeometryInput()` to cached actual linked metadata before mutation and submission;
LIST_CAPTURE uses its authenticated prepared-epoch plan without live shader authority.
Every setup/restore drain failure prevents submission or triggers P7 off containment.
Partial setup rolls back internally before Failed; no binding escapes, and failed rollback
invalidates admission. Strict one-close LIFO restoration consumes the binding, even on error.
R27-1/NS-1 receiver receipt: P1 D-P1-55 owns the exact actual-state snapshot and
isolation implementation. LIVE_DRAW conventional positions disable generic0;
LIST_CAPTURE isolates every enabled conventional/client-unit/generic array absent
from the complete capture plan. Bind before glNewList, restore after glEndList; full
affected predecessor restoration and partial-setup rollback include isolation changes.
LIST_REPLAY_GUARD never reconstructs expired pointers. No duplicate P10 native API.
This is the completed P10 receiving receipt for R-P1→P10-55, still unverified;
the whole owner §4.7.6 law includes capability-gated enumeration and implicit current
effects, including the prohibition on a current generic-zero query. Enable-only
isolation does not rewrite foreign descriptors.
Fresh P1/P7/P10 review and native list/state proof remain required; recorder success is not parity.
P1 D-P1-53 additionally wires the real MOD-plugin class/key and class-only checks at v0.1
before this phase's geometry-only native adapter family applies. Adopt that exact split:
no permissive/absent plugin, no early preinit mutation; CLASSIC56 and format transitions
remain v0.3. Base-subset hook-health and target-load-order proof still gate early .gsh claims.


## 6. Failure modes & degradation

| Failure | Required response | G2.4 rung |
|---|---|---|
| Custom/built-in uniform failure | Leave Phase 11/6's isolated policy intact; never turn a uniform error into a vertex rewrite | 1 / 2, other owner |
| Finite degenerate quad | §4.3 explicit zero frame components, retain independent valid values; bounded diagnostic counter, no NaN | Local geometry containment; not an invented uniform rung |
| One optional future attribute producer fails | Disable that advertised feature only when its contract defines a safe absence; classic identity/tangent are not quietly optional on terrain that requires them | 2a |
| Attribute setup unsupported for one effective program | Reject draw before execution, route failure to Phase 7/4's program failure/backup-chain machinery; no private fallback shader or format guessing | 3 |
| Missing CORE hook, inconsistent mesh layout, stack corruption or unrecoverable pointer restoration | Close admission, invalidate products, restore vanilla through coordinated off transition; never draw a partially stamped batch | 4/5 |
| Replaced chunk renderer / detector exception | Existing session-latched `CompatVerdict.Bail`, chat/log/GUI reason, vanilla or other renderer left unmodified | 4/5 |
| Insufficient maxVertexAttribs | Declaration-specific Phase 4 failure/fallback; pack off if no safe effective program; do not renumber | 3 then 4 |
| Old epoch at queue/upload/draw/state restore | Expected cancellation/discard and rebuild, not client crash or alias zeroing to hide staleness | 5 preservation |
| Worker throws in shader side channel | Mark task failed, unwind owned stacks and release builder pool borrow; notify render-thread coordinator to recover off | 5 |
| Original mod/model renderer throws | Always unwind our scopes; do not swallow or misattribute an unrelated vanilla/mod exception as successful geometry | Preserve cleanup; no claim to repair foreign failures |
| Cache invalidation/transition fails | Run §10.2 fallback, restore vanilla references and invalidate affected products; remain off if safe shader rebake cannot be established | 4/5 |
| Unattributed/batched GL error | Report actual drain evidence, restore state; do not claim a specific attribute failed from the last call name | 2a/3/5 by actual containment |
| Sky/star unproven/stale product, missing converted companion, failed capture/upload/deletion or unhealthy site | Safe canonical product-only rebuild before borrow if proven; otherwise suppress incompatible call, stop repeats, restore externally and use existing main/shadow containment. Retain failed-retirement owners; never raw-name adoption, forced VBO mode or skipped count expansion | 3 then 4/5 |

No recovery path reuses an extended VBO with vanilla stride or returns a builder to a
pool with an active stack. No setup error triggers an automatic duplicate of a draw
that may already have executed. Messages carry generation, hook ID and source/layout
identity, not per-vertex log spam or pack source text.

## 7. Threading & performance notes

| Component | Owner / handoff |
|---|---|
| Layout and declaration plans | Immutable, prepared outside hot draws; shared safely |
| Builder and primitive stack | Exclusive task worker or render thread for the whole `begin`/seal/reset lifetime; never concurrent writers |
| ThreadLocal context slot | `mod.glue.vertex`, installed around a task/direct rebuild and removed in finally; no off-path `get()` |
| Alias lookup and ordinal bridge | Immutable borrowed matched pair, captured once per task; publisher retirement waits for quiescence |
| Saved state and mesh metadata | Immutable at seal; published with bytes under existing task synchronization |
| Upload/list compilation/drawing/facade | Render thread only, including state inspection and error drain |
| Format/default/cache swap and model reload | Render-thread no-frame safe boundary after workers drained |
| Compatibility checks | Bootstrap/plugin/transition only; early class probes never initialize target classes or issue GL |

Shaders off means no enlarged default formats, extra copy/normal calculation, stack
push, TLS lookup, declaration-plan lookup, or vertex GL call. Instrumented methods
still pay an unavoidable predictable branch on inactive/null state; “zero-cost” means
zero shader work/allocation, not a false claim that an injected branch has zero CPU
cost. Session-vetoed mixins need not pay even that branch.

During active chunk writes, identity stamping is two words' worth of stores, no per-
vertex allocation. Quad calculation touches four emitted records; no separate mesh
conversion pass. Plans are precomputed per immutable layout, authenticated source
participation and effective declaration set; no temporary Sets or pointer lists per draw.
Necessary bulk format conversion copies directly into destination once and never clones
already matching input merely
to stamp it. No pooled global scratch or contended mutable registry is introduced.

Native addresses are never cached across builder growth, state replacement or upload.
Resource/alias transitions are deliberately more expensive than draw paths: full
invalidation is preferred to a complex partial-cache optimization without evidence.
The 56-byte terrain footprint is exactly twice vanilla's 28 bytes before allocator
capacity; the document makes no measured FPS claim. Debug timing/counters are opt-in.

## 8. Testability plan

This section specifies future verification; **none of these tests or experiments was
run during authorship**. Permanent tests should defend a plausible packing, lifetime,
or state-transition bug. Loader/driver questions need actual runtime evidence.

### 8.1 Headless behavioral cases

| ID | Scenario and observable oracle |
|---|---|
| T10-LAYOUT | Decode produced CLASSIC_56 bytes as the shader input types; verify field preservation, all three identity components, signed 0xffff behavior and zero padding. Include an ITEM source with its different normal offset to catch blind prefix copying |
| T10-MATH | §4.3 ordinary and mirrored square: normal, midpoint and opposite handedness after short normalization; non-planar diagonal normal; degenerate UV/geometry produces no NaN |
| T10-INGRESS | Real BLOCK fluid `pos→color→tex→lightmap→endVertex` for successive vertices and OLDMODEL `pos→tex→normal→endVertex` preserve positions/UVs/color/lightmap/source normal before quad derivation, then yield the same shader-visible quad as authenticated int[]/ByteBuffer adaptation. Distinct consecutive inputs detect stale omitted fields or cursor drift; test non-QUADS source-normal preservation, float color delegation/noColor, source padding and translated positions. Complete vertices can split one quad across scalar→bulk→scalar without double finalization; partial-vertex bulk/save/seal rejects. Growth and injected scalar/advance/end/bulk failure invalidate before seal/capture/upload, then reset/new begin starts clean. Cached source bytes/consumption stay ordinary; shaders-off both real chains remain vanilla with no extended sidecar/TLS work |
| T10-STACK | Nested states A/B/A, two interleaved builders and two workers; a throw/cancel restores the previous depth, and the next unrelated task starts neutral |
| T10-STATE | Translucent reorder moves identities/tangents with whole quads; save/restore does not stamp the neutral resort stack over existing IDs; stale state is discarded |
| T10-EPOCH | Queue under epoch A, activate B, run A's queued upload: no upload/draw occurs, rebuild is scheduled. Include identical alias fingerprints with unequal generations and world change with repeated coordinates |
| T10-TRANSITION | Worker holds old alias borrow; prepare cannot swap until actual drain; failure after partial publication converges off, retains sources until last borrow ends and leaves no stale eligible mesh |
| T10-DRAIN-FAILURE | Worker awaiting a render-thread upload; cancellation request/timeout is not Drained. Pending/Failed retains lookup/map, recovery token and services through off/removal/shutdown; no format mutation or false VanillaReady before last borrow ends |
| T10-INSTALL | Absent pre-v0.3 extended provider stays inert; partial bundle and missing v0.3 P9 reject admission. Successful geometry-only base adapter before v0.3 requires no fake aliases; detach waits for final source/scope borrow |
| T10-PUBLICATION | prepare freezes lighting before acceptance; P13→P9 matched tuple→Activated→all required invalidation→atomic admission. Inject failure at each stage, including after Activated: recovery uses correct token/serial and never restores the old pipeline |
| T10-TOPOLOGY | Canonical quad attribute records survive (0,1,3),(1,2,3) expansion; both winding directions, last vertex3 flat value, two sequential geometry primitive IDs and nonplanar diagonal; no geometry preserves original source/primitive |
| T10-TOPOLOGY-LIFETIME | Client/VBO/list paired source expiry, queued upload across reload, insufficient range/overflow/allocation/capture/restore failure: no incompatible draw, no freed source still referenced, no list discovery replay; unknown mixed-state lists explicitly unsupported |
| T10-LIGHTING | Explicit user false overrides pack true; user DEFAULT delegates to pack; both DEFAULT use local true oldLighting/false separateAo. Render original project-owned diagnostic geometry with known tint/AO/lightmap: separateAo changes only the declared RGB/alpha split, fixed shade applies once, lightmap stays intact. Change policy while a worker/upload is pending; old baked/mesh products cannot draw under the new pair |
| T10-DECLARATIONS | Effective fallback declares only midpoint while requested slot declares identity: only location 11 enabled. Shadow and nested restoration choose their actual provider; shader-to-fixed transition clears old arrays |
| T10-GL-STATE | Recorder starts with nondefault source bindings and unrelated enabled attributes. Nested client/VBO setup plus injected error restores only the touched state, leaves unrelated state intact, rejects stale/forged source before native commands |
| T10-COMPAT | Actual `celeritas` ID alone, exact renderer class alone, throwing probe, late positive before format swap, and clean vanilla. A package string is not a class probe; positive results latch off |
| T10-OFF | Run ordinary builder lifecycle with shaders off: bytes/count match vanilla, no sidecar allocation/TLS lookup/extended facade calls or changed defaults |
| T10-GROWTH | Test-only appended named field changes stride; unchanged binder/copy/state algorithms deliver its supplied values without editing consumers. This proves extensibility, not modern pack support |
| T10-INSTANCES | At v0.5 two prepared submissions with N=2 observe A0,A1,B0,B1; client/VBO/list paths have one preparation/reset and two native copies each. Compile list once without instance upload, then replay with current count; no N² forwarding. Nested distinct submission restores parent ID; failure after first copy stops remaining copies and contains rather than replays |
| T10-ARRAY-ISOLATION | Enabled generic0 points to distinct valid positions on client/VBO entry; submitted positions come from admitted conventional source. Capture with unrelated conventional/client-unit/generic arrays enabled records only the complete plan. Nested success and partial setup failure restore exact actual predecessor fields, selectors, bindings and current values through P1 |
| T10-MODEL-LIST | First compile off/NONE/QUADS, then effective TRIANGLES main and root-shadow reuse: no stale QUADS call; one safe recapture then same-epoch reuse without recompiling. Preserve first-compile scale, per-quad draw boundaries, transforms/children and A0,A1,B0,B1 copies. P9 row timing unchanged |
| T10-MODEL-RETIRE | Capture failure after first run, glEndList/restore failure, configuration/layout/demand switch, postRender-first compile, deletion/range deletion and numeric name reuse: no partial rejected body executes, metadata never outlives incarnation, borrowed source survives last copy/restore, unknown producer contains before first illegal draw |
| T10-SKY-PRODUCTS | All three constructor/off-first POSITION/QUADS products later face effective TRIANGLES: one canonical generation per needed product, original/derived from same run, no stale native QUADS and no repeated sky traversal; unchanged later epochs reuse. NONE selects originals; incompatible adjacency rejects before GL |
| T10-SKY-SITES | Exercise all four list and three VBO sites, including both lower placements, star brightness branch and final VBO-mode absent sentinel. At v0.5 verify A0,A1,B0,B1 with current colors/transforms, one preparation, no N² forwarding, no invented fourth VBO draw |
| T10-SKY-RETIRE | Source sealing before VBO reset; list/VBO deletion/reuse, mode flip, resource-only NONE, world detach and shutdown; inject second-product/end/restore/deletion failure. No rejected prefix or stale incarnation executes; borrows survive last copy/restoration, failed drain retains owners |
| T10-CONVENTIONAL-INHERITED | OLDMODEL client/VBO and cached model-list playback under a shader exposing gl_Color and UV1: reuse identical geometry with fleece colors A then B and current unit1 values L1 then L2; each draw observes its current pair, not white/zero or first-capture values. Seed enabled foreign COLOR/UV1 arrays; they cannot override inheritance, and setup/failure/cleanup restores predecessor enables without resetting current color/UV1. Release staging memory before replay and exercise original/derived topology and main/root-shadow reuse |
| T10-CONVENTIONAL-PROVIDED | Authenticated BLOCK vertices supply distinct per-vertex color/lightmap while current values differ: client/VBO/list original and derived products observe supplied streams, including after save/restore/resort. Equal CLASSIC_56 stride with OLDMODEL participation cannot reuse that product; forged/mismatched mask rejects before submission. Existing quad normals/tangents and source normal on non-QUADS remain unchanged |
| T10-PARTIAL-BLOCK | Through actual flat and smooth non-Forge-lighting block-model scopes, append authenticated cached ITEM quad into BLOCK, then write four distinct real packed brightness values using func_178962_a; sealed client/VBO/list and derived products supply exactly those values despite differing current UV1. The ITEM array remains byte-identical, final BLOCK mask never changes, and all four zero values also count only after a real successful writer. Missing writer, throw after a partial store, wrong builder/range/scope, nested-scope escape, cancellation/reset and attempted early state/sort/seal/upload/capture/draw produce no eligible product. Repeat Forge-lighting scalar UV1 and cached OLDMODEL A/B color/lightmap playback to preserve both provided and inherited contracts |
| T10-LIST-FIXTURE-AUTHORITY | Issue P1 list fixtures with identical layout/geometry and different full captured masks/unions. Own-plan first replay succeeds, cross-plan first replay rejects without submission, issuance rejects layout mismatch and caller mutation cannot rewrite retained metadata; no first-request learning or old overload |

**2026-09-08 paper trace (not runtime/test execution):** the source-evidenced off-first
compile has no converted product; subsequent TRIANGLES playback reaches the unconditional
model-call guard before GL, and unequal capture/demand identity cannot select the original
QUADS list. A successful complete recapture publishes the triangle product; equal-epoch
later calls select it without capture. Failed staging/second capture publishes neither
candidate and invokes containment, so no accepted prefix or original-list fallback executes.
Deletion revokes incarnation before integer-name reuse; replay restoration references
only its current-value guard, not released staging bytes. These are design traces for
the acceptance scenarios, not substitutes for the planned native cases.

Use Phase 1's recording/error conventions and capability fixtures. Test at the actual
highest-used attribute boundary, not only on an abundant-capability profile. Do not
assert source text, private field names, or mock echoes as the contract oracle.

### 8.2 Runtime scenarios


- OQ-5 and OQ-14 run exactly as §10 specifies, on pinned Cleanroom and native LWJGL3.
- With VBOs on **and off**, render terrain, fluids, cutout foliage, damaged blocks,
  multi-layer Forge models, item/block models, entities and tile entities. Use a tiny
  project-owned diagnostic shader to visualize material ID, midpoint and handedness;
  do not infer delivery from successful GL calls alone.
- VBO-off additionally switches main/shadow programs with disjoint attribute sets and
  releases builder memory after list compilation. Correct playback must not depend on
  that memory; restoration must prevent generic state leaking to later entity/UI draws.
- Move the camera through translucent terrain; trigger chunk resorts, pending uploads,
  world unload, dimension change, pack on/off, resource reload and VBO-setting change.
- Exercise all four effective oldLighting/separateAo pairs with AO on/off, shaded/unshaded
  models, fluids, all face directions, and a custom Forge baked model. Compare shader-visible
  color/lightmap and off-mode vanilla restoration, not only successful mesh scheduling.
- Inject a shader-side append/setup failure and confirm shaders-off remains reachable
  with clean vanilla geometry and no pool/stack leak. Do not claim recovery from every
  unrelated mod exception.
- At earliest affected .gsh support, exercise client/VBO/list conditional conversion with a
  diagnostic geometry shader exposing PrimitiveIDIn, winding and flat/provoking values.
  Verify actual linked input against P4 metadata; core and native-preserved geometry routes,
  absent geometry and disjoint main/shadow inputs. Conformance must report changed triangle
  primitive semantics, not infer parity from successful linking or bytes alone.
- Exercise every ModelRenderer.render branch, renderWithRotation and postRender-first
  compile on actual vanilla entities and root-shadow users. Start shaders off, enable
  TRIANGLES, switch provider/demand/configuration, then reuse unchanged epochs. Observe
  submitted topology, capture count, transforms/scale, per-draw primitive IDs, current
  entity/color values and deletion/reallocation—not merely successful glCallList.
- Seed distinct generic0 and unrelated conventional/client-unit/generic sources before
  live/capture scopes; verify rendered/captured values and exact restoration after
  injected setup/capture failure. Recorder-only success is insufficient native proof.
- Reuse one cached sheep wool model for changing dyed/rainbow fleece RGB and changing
  current lightmap between entities/frames; a diagnostic shader observes current color/
  UV1 on every playback without recapture. Contrast source-provided BLOCK color/lightmap
  with different current values on client, VBO and list paths, including topology conversion.
- Exercise all T10-SKY-* scenarios on pinned transformed Cleanroom with VBOs on/off,
  nighttime/zero brightness and above/below-horizon views; seed unrelated arrays and
  generic0 before list capture/VBO draw. Observe exact native topology, seven-site
  coverage, current-value restoration and bounded capture counts, not source-only health.

### 8.3 Conformance tiers and artifacts

Implementation exit is RESEARCH §9 v0.3: **SEUS Renewed, Chocapic13 V9 and projectLUMA
at T2 within the established Phase 2 tolerance on terrain scenes**, with both draw
paths exercised. T0 load is insufficient evidence for vertex delivery. T1 self-baselines
cover reload/motion regression; T2 uses manually acquired OF G6 oracle captures, never
Pintonium as the numeric oracle. Phase 13 later exercises the tangent frame with
companion atlases; this phase does not wait until v0.5 to test handedness itself.

Reuse the actual Phase 2 runner/scene protocol at implementation, rather than inventing
new command names here. Record pack version/hash, capability profile, draw path, vertex
layout fingerprint, vertex/ID/world generations and owner-10 hook fingerprint in local
run evidence. Matrix packs are downloaded/cached, never committed or re-hosted. No pack
source text in goldens and no rendered images in the repository; committed oracles are
hash/provenance manifests. `-PupdateGoldens` is explicit and its regeneration run still
fails, per DESIGN §G6. Driver screenshots and diagnostic packs remain appropriately
licensed local/CI artifacts.

## 9. Milestone staging

Each row has exactly one implementation tag. The architecture is specified now; the
rows do not authorize code during this build session.

| Component | Tag | Staging meaning |
|---|---|---|
| Classic layout, semantic projections and scalar packing | v0.3 | Full 56-byte floor, not a placeholder |
| Lazy builder sidecar, typed bulk ingress, dirty-tail math | v0.3 | Incremental and bulk paths together |
| Task/builder stacks and ordinal/lookup borrowing | v0.3 | Worker-safe before activation |
| Saved states, resort, queued-upload and mesh epoch checks | v0.3 | No stale geometry admission |
| Client/VBO/list capture/replay and facade restoration | v0.3 | Both required draw paths, not a VBO-only milestone |
| Declaration delivery and format/reload participant | v0.3 | After owner grants/fresh verification |
| Base-layout conditional topology adapter and facade | v0.1 | Required before earliest affected native .gsh claim; no extension fields/P9 dependency; client/VBO/list proof required |
| Canonical sky/star base capture, seventeen health rows and seven playback guards | v0.1 | R10-7 receiver required; includes off-first/list/VBO/resource/mode lifetimes, no second sky traversal |
| Canonical sky/star native adjacent repetition | v0.5 | Same prepared seven-site adapters under P7 count/instance policy; full v0.3 health remains required |
| Geometry invalidator and Forge cache adapters | v0.3 | OQ-14 gates usable toggle lifecycle |
| Bail checks/plugin policy and hook subreport | v0.3 | OQ-5 runs against actual replacement artifacts |
| Layout/producer extensibility infrastructure | v0.3 | Descriptor-driven classic implementation |
| `at_midBlock` producer and modern slot catalog | post-v0.5 | First actual appended field, with explicit modern contract |
| `at_velocity` producer/history | post-v0.5 | Entity/BE history required; no fake velocity |
| `mc_chunkFade` constant-fallback capability | post-v0.5 | −1.0 only on this backend |
| Core-profile `va*` layout and renderer adapters | post-v0.5 | Separate future compatibility decision |

## 10. OQ & spike specifications

### 10.1 OQ-5 — chunk-renderer coexistence

**Question, verbatim (RESEARCH §11):**
“Chunk-renderer coexistence policy (Celeritas now, Kirino later)”

**Assigned design policy:** v0.3 detect-and-bail. The spike establishes reliable
mechanics and costs of false positives; it does not quietly replace that policy with
integration because one scene happens to work.

**Procedure:**

1. Build an isolated developer probe against the Phase-1-pinned Cleanroom loader,
   with actual MOD/preinit Mixin phases and both shaders-off/selected-pack launch
   cases. Use a disposable world and preserve the ordinary install.
2. Baseline vanilla Cleanroom plus Schmaloogium. Record class-probe results, when mod
   IDs become queryable, which target classes were already transformed, the full
   expected/actual owner-10 hook report, and whether a format swap occurred.
3. Install the local Pintonium source build in that environment. Verify `celeritas`
   alone detects it; test exact renderer-class presence independently of the ID
   path. Do not compile or copy its prohibited transformation dependency into
   Schmaloogium. If a runnable artifact cannot be legally/operationally obtained,
   record that missing prerequisite; source inspection is not runtime success.
4. Test available Celeritas/Embeddium variants and Nothirium/Vintagium artifacts;
   record their actual IDs/classes and target replacements, including disabled
   renderer settings and inert/shaded-class false positives.
5. Compare noninitializing class-resource probes with `Class.forName(...,false,...)`
   in the disposable probe. Measure whether lookup triggers class transformation,
   missing-dependency linkage or early renderer initialization. Prefer the resource
   approach unless actual loader evidence requires another safe method.
6. Inject an unavailable/throwing detector and a late positive at the pre-swap point.
   Confirm no format mutation, no partially active hooks, no client crash from the
   checker, and a stable session-latched off result with log/GUI reason.
7. Verify the supplied message, normal other-renderer/vanilla operation with
   Schmaloogium shaders off, and zero Phase 10 vertex work in that state.

**Success:** vanilla is admitted; every tested active replacement is detected before
unsafe mutation; early veto is effective or early attachments are demonstrably inert;
all errors fail safe; actual Pintonium coexistence produces the specified bail message
and never activates shader formats. Report actual tested artifacts and false positives.

**Failure:** an active replacement evades both detection and hook health; probes cause
unsafe initialization; the Mixin phase is too late to prevent mutation; off still
changes another renderer's bytes; or only a source check was performed.

**Fallback designed now:** retain the session-latched deny policy; use verified mod
IDs after loader discovery and exact noninitializing class probes where valid, keep
all pre-MOD attachments inert, require healthy transformed vanilla anchors before
activation, and gate shader startup when detector health is unknown. No claim to
auto-detect every unknown future renderer. If even this cannot prevent interference,
do not enable the shader vertex pipeline on that loader/backend combination. A real
backend adapter is G8 work, not the spike fallback.

**Result recording:** implementation records evidence/status in RESEARCH §11 and a
Phase 10 addendum; owner/API changes also update §5 and trigger fresh verification.

### 10.2 OQ-14 — format switches and Forge caches

**Question, verbatim (RESEARCH §11):**
“Forge baked-quad/LightUtil cache interplay on vertex-format switch under Cleanroom”

**Procedure:**

1. In a disposable pinned Cleanroom dev world, exercise vanilla cubes, fluids, items,
   normals, a Forge multi-layer model, a custom explicit-format BakedQuad, and the
   old default-format constructor. Instrument format identities/sizes, source byte
   lengths and cache-route selection, without recording pack sources.
2. Run with Forge lighting pipeline **on and off**, and with VBOs **on and off**.
   Warm model caches and both the LightUtil map and static default fast path first.
3. Toggle off→classic56→off repeatedly while chunks build. Separately change packs,
   resource/lighting settings, world/dimension and VBO mode; force saved translucent
   state/resort and leave an upload queued across the transition.
4. Check byte/stride invariants, normals/tangents/material visualization, color and
   lightmap parity, model/cache retained format references, and restored vanilla
   output. Watch for stale-cache corruption, missing faces, wrong winding/lighting,
   buffer under/overrun, worker leaks and native GL errors.
5. Test full invalidation: drain workers, replace immutable defaults, clear the pair
   map, rebuild models/world renderers, and explicitly refresh or bypass the static
   default-map path for the new generation. Verify retained BakedQuad arrays still
   match their own descriptors; never change descriptors alone.
6. Test fallback pinning of one demonstrably safe Forge lighting route for the active
   shader session, with full invalidation/rebake on transitions. Save and restore the
   user's original setting at the safe boundary; no persistence change. An unsupported
   mod-specific model stays a reported incompatibility, not silent normal-data loss.
7. Repeat recovery after interrupted/failed invalidation. Confirm vanilla formats and
   meshes are restored, no stale work becomes drawable, and a later clean launch is
   usable even when shader activation is refused.

**Success:** all exercised switches are corruption-free with both lighting modes and
both draw paths; current layouts match actual byte records; stale tasks cancel; the
normal and fallback routes have explicit evidence and cleanup. Static-default and
pair-map caches are both accounted for, not merely cleared by name.

**Failure:** any retained quad/format mismatch, stale state/upload admission, lighting
or tangent corruption, incomplete cache invalidation, deadlock or need to assume
Pintonium's unrelated replacement mesh path proves safety.

**Fallback designed now:** force the proven full cache/model/world-renderer invalidation
on every format swap, including replacement/bypass of the LightUtil static fast path;
if required, pin the verified Forge lighting route only while shaders are active and
restore the user's setting on exit. Do not guess which route is safe before running
the spike. If neither route preserves custom/vanilla geometry safely, return the
coordinated shader-off result, restore vanilla formats and rebake. Merely disabling
VBO-off or relying on a restart-only toggle does not satisfy this phase's implementation
gate. The unresolved runtime prerequisite is reported, not declared solved.

**Result recording:** implementation updates RESEARCH §11 and a Phase 10 addendum with
the tested loader/Forge patches, formats, modes, invalidation adapter and remaining
incompatibilities. Changes to exposed lifecycle requirements require fresh review.

## 11. Decisions & open items

### 11.1 Decision log

| ID | Decision and rationale |
|---|---|
| D-P10-1 | Immutable named CLASSIC_56 layout and semantic source adapters. Exact App C values, with descriptor-driven growth instead of stride literals spread through consumers |
| D-P10-2 | Reuse Pintonium quad-math structure only. App C.2's N×T controls handedness; retain float midpoint/short tangent, full-precision math, explicit truncation/degenerate policy. Contract check is §4.3 |
| D-P10-3 | Reject alias-valued generic entity/TE constants. App C's block triple and App D's entity uniforms are distinct; integer constant at 11 is not classic floating input at 10; no equivalence evidence (§4.7) |
| D-P10-4 | Lazy builder state and no off-path TLS. The required idle side channel performs no extended work; do not copy Pintonium's always-paid mesh re-encode |
| D-P10-5 | Per-task context plus per-builder balanced stack, primitive storage, explicit finally cleanup. Thread-local association is mod glue only; range recording cannot represent nesting |
| D-P10-6 | Quiesce, immutable format swap and full epoch invalidation; no keep-old-geometry fallback after accepted ID change. Matches current Phase 7 publication/failure policy |
| D-P10-7 | Separate list capture from live shader activation. Capture the accepted pipeline's required attribute union once; replay remains epoch-checked and restores generic state; both draw paths remain required |
| D-P10-8 | Detect-and-bail through Phase 1, adding actual `celeritas` to assigned candidates. OQ-5 validates mechanisms; no performance-mod integration is smuggled into shaders-only scope |
| D-P10-9 | Missing facade, activation sink and lifecycle grants are explicit owner requests. Neither a public state record nor user-authorized provisional design consumption grants private dependency access |
| D-P10-10 | Preserve the current build-authoring exception as provenance, not a verified label; reconcile fresh Phase 4/7/9 interfaces before implementation |
| D-P10-11 | Accept shaders-only oldLighting/separateAo behavior from P3's master map and RESEARCH F.1. Local defaults true/false and fixed face factors are explicit compatibility choices; user default delegates to pack, AO moves to alpha, and effective policy participates in every bake epoch. No renderer performance work or verified-parity claim (IR-05/24). |
| D-P10-12 | Adopt P9's amended off-on-failure and matched lookup/ordinal worker lifetime; preserve current P3 schema17 and catalog-bound same-build inputs through P7. P9 R10-3 is adopted/unverified, not still missing (IR-03/04/09). |
| D-P10-13 | Adopt schema18 old-light macro projection while keeping typed runtime policy and bake invalidation unchanged; load-time shader macros follow the same user→pack→true rule after P3's independent Properties pass. Historical D-P10-12 schema17 adoption is superseded, with no inferred upgrade (IR-03/24). |
| D-P10-14 | Adopt maintainer-approved IR-18 prepared-submission repetition through P7's existing authenticated scopes at v0.5. Client/VBO final draw and list playback repeat; construction/upload/capture/reset/Forge traversal do not. This neither invents a renderer API nor grants R10 native/lifecycle extensions. |
| D-P10-15 | Adopt P7's exact R10-2 grant, real drain and matched lookup/map lifetime, authenticated declarations, immutable preparation and recovery-token semantics |
| D-P10-16 | Adopt maintainer-authorized conditional submission conversion with (0,1,3),(1,2,3), last vertex3 provoking and explicit two-triangle primitive IDs; no parity assertion before real conformance |
| D-P10-17 | Adopt P3 containing schema19 via P7 without changing vertex/lighting/U1 semantics; P4 effective geometry category remains a separately tracked owner grant |
| D-P10-18 | Adopt P1 D-P1-50's complete R10-1 package/service/source/recorder/early-context grant; preserve its exact validation, lifetime, mode and failure/restoration laws |
| D-P10-19 | Adopt P4 D-P4-28's effective linked geometry category through authenticated P7 activation; FixedFunction clears, failed/stale outcomes never authorize drawing |
| D-P10-20 (2026-09-08) | Adopt P1 D-P1-55's complete R27-1/NS-1 affected-state isolation/rollback law; live conventional positions disable generic0, capture is a complete array whitelist, replay never restores expired pointers |
| D-P10-21 (2026-09-08) | Resolve NS-2 through authenticated ModelRenderer capture/incarnation, retained compile scale, every-call compatible-product guard and paired lifetime; P7 D-P7-32/R10-6 hook/transaction grant adopted. No runtime/PASS claim |
| D-P10-22 (2026-09-08) | Active containing configuration/IdMappingInput receipt is schema20 through P7; older schemas rejected before derivation. P3 assets remain same-load owner-held binary capability, never read or serialized by P10; historical schema19 native-source decisions preserved |
| D-P10-23 (2026-09-08) | Adopt P3 D-P3-70 exact-current schema21/nested-ID and current configuration/materialization identity through P7 in §5.2; carry opaque P4 D-P4-31 registry identity normally. Historical schema20/19 receipts stay historical; vertex/provider/lifetime semantics unchanged; fresh review required |
| D-P10-24 (2026-09-08) | Resolve R1-1 with three canonical RenderGlobal products, single-source paired capture/upload, all four list plus three VBO playback sites and seventeen milestone CORE rows. R10-7 requests exact P7 receiving lifecycle/fingerprint expansion; no raw-handle authority, sky traversal duplication or native proof claim |
| D-P10-25 (2026-09-08) | Receive P3 schema22/current-constant containing/nested-ID admission through P7 for isolated forced11300Rules BLOCK extension. D-P10-23 numeric21 and earlier receipts become historical; projectionVersion=1, nine trees, binary assets, lighting/vertex/provider/option semantics remain unchanged |
| D-P10-26 | Receive P9/P7 deferred FastTESR native-range protocol, including Forge bulk append, exact sort permutation and all source/ID lifetimes; no fast geometry omission, per-owner resort or alias-valued vertex substitution |
| D-P10-27 (2026-09-08) | Resolve R2/C1 by authenticated source-semantic setter dispatch before storage, source-only advancement, omitted-field initialization and per-vertex reset; bulk/state transitions and exceptions cannot expose a partial product. Seven exact writer CORE rows require R10-8 P7 receipt; no caller rewrite, second layout or reset-only workaround |
| D-P10-28 (2026-09-08) | Receive exact-current schema23 containing/nested/inspection equality and MaterializedSource-v23 through P7. Earlier numeric receipts are historical; nine metadata-only trees/projectionVersion=1, assets and vertex/lighting/provider policy unchanged; P9 alone resolves registries |
| D-P10-29 (2026-09-08) | Resolve R3/C1 by immutable authenticated conventionalInputs distinct from physical CLASSIC_56 storage; absent COLOR/UV1 remain inherited on live draws and every cached playback, never filler arrays or capture-time constants. Complete source/plan identity follows Forge projection, state/client/VBO and original/derived capture products; preserve source-provided BLOCK and existing normal/tangent/other-attribute/off policy. R10-9 requests exact P1/P7 receipts; unverified architecture only |
| D-P10-30 (2026-09-08) | Resolve R4 C1 by separating authenticated partial ITEM BakedQuad ingress from final BLOCK producer participation, with real func_178962_a four-vertex UV1 completion before eligibility and exact CORE health receipt; no filler promotion, arbitrary union, cached-source mutation or OLDMODEL inheritance change. Adopt P1 D-P1-70 complete capturePlan recorder factory before first replay; architecture only, unverified |
| D-P10-31 (2026-09-08) | Resolve R5 C1 by naming the current `RegistryFingerprint/profile-selection-v3` domain (P4 D-P4-43; D-P4-31's own-build fields retained within it) as the opaque identity carried normally through P7 in §5.2; ledger-text repair only, no schema or carrier-semantics change |
| D-P10-32 (2026-09-08) | Resolve R5 C2 by restating the R10-8/R10-9 receiver receipts as received in §5.1/§5.3/§11.3 — P7 D-P7-62 (seven H10-WRITER-* rows), D-P7-75 (H10-BRIGHTNESS-4 full-v0.3/v0.5-only), D-P7-70 (conventionalInputs) and P2's planned forwarding (docs/phase2/v2/PHASE_2_DOC.md:2962) — receiver-adopted/unverified with fresh applicable review retained as outstanding, and naming H10-BRIGHTNESS-4 in the §11.4 enumeration |

### 11.2 Input contradictions and rulings

1. **PD/DESIGN tangent equivalence is false at the cross order.** Pintonium source
   computes T×N; RESEARCH requires N×T. §4.3 includes the distinguishing worked
   square. RESEARCH wins; no reference report is edited by this session.
2. **Detection anchors omit the actual 1.12.2 mod ID.** `CeleritasVintage.java` and
   `mcmod.info` declare `celeritas`. Keep `embeddium`/`celeritas_shaders` as candidates,
   but do not build a detector that misses the assigned adversary. Also qualify
   “zeroes render distance”: the observed hook substitutes zero for a read, not a
   user-setting assignment.
3. **App C.1 says “3×short (2 used)” while C.2 supplies two integers and the public
   declaration names xyz.** Interpret “two” as payload words, not permission to drop
   metadata: bytes 48/50/52 carry all three specified components and 54 is padding.
   This follows both the value formula and `shaders.txt:119`; request upstream wording
   clarification without changing RESEARCH here.
4. **Phase 9's older publication contradiction.** P9 §§4.1/5.3 now adopts Phase 7's
   publish-after-textures/failure-to-off and matched worker-drain lifetime. R10-3 is
   receiver-adopted/unverified; fresh reviews, not another ownership request, remain.
5. **Phase 7's older compatibility outcome mismatch is reconciled.** P7 §4.12 now
   consumes Phase 1's `CompatVerdict.Ok|Degrade|Bail` and `CompatEvaluation` at the
   owner-approved evaluation points. Receiver adoption is unverified; no separate
   replacement-backend enum or ungranted backend integration follows from `Degrade`.
6. **P1 now grants the vertex policy home and facade operations (D-P1-50).**
   §5.3 adopts the exact contract under R10-1; fresh reviews/native proof remain.
   No GL or Minecraft types are concealed in the pure math layer.
7. **Governance files mention a retired executable profile system.** MOVES records
   its removal and per-header authority. Initial v3 adoption is declared and indexed;
   no dry-run output or target-profile modification is claimed.
8. **Current dependencies are unverified despite older PASS files.** The maintainer
   explicitly extended the exception after this was discovered. All consumed current
   surfaces remain provisional, including incorporated details with shifted lines.

### 11.3 Open items and integration gates

- OQ-5/OQ-14 remain open; their procedures and fallbacks are complete specifications,
  not a promise that classloading, cache switches or display-list capture already work.
- R10-1/R10-2/R10-3 are owner-granted/receiver-adopted, unverified.
  R10-4 gates only modern growth; fresh owner/receiver verification remains.
- The maintainer's conditional topology permission is explicit, not external parity evidence.
  R10-5's P4 effective metadata projection is adopted but needs fresh owner/receiver review;
  real client/VBO/list topology, flat/provoking and primitive-ID conformance is mandatory
  at the earliest affected .gsh milestone. Unknown/mixed-state external lists remain
  explicitly unsupported rather than silently converted or drawn incompatibly.
- IR-05/24 policy/default choices in §4.8.1 require conformance review and the OQ-14
  safe bake/cache adapter. Neither numeric parity nor successful invalidation is claimed.
- Exact transformed injection cardinalities, exception-safe wrapper support on the
  pinned CleanMix toolchain, and effective upload callback anchors require runtime
  application evidence. Missing evidence disables activation; it is not guessed here.
- Numeric packing/degenerate behavior not explicitly fixed by the assigned sources is
  a recorded local decision. T2 discrepancies must be investigated against the
  contract and reported upstream, not hidden by per-pack handedness heuristics.
- The assignment's soft phrase “zero-cost” is satisfied as no shader work/allocation
  with a predictable inactive guard. A literal zero-cycle claim is not made.
- R10-6 P7 cached-model CORE hook-health and lifecycle grant is received through D-P7-32;
  both owner and receiver remain unverified, with actual transformed/native coverage still owed.
- R10-7 requires P7 receipt of the complete sky/star lifetime and seventeen-row health
  expansion; existing R10-6 model receipt does not grant it implicitly. §5 is changed,
  and fresh owner/receiver review remains required before integration.
- R10-8 receipts are received, not outstanding: P7 D-P7-62 grants all seven H10-WRITER-*
  IDs with exact descriptors, fingerprint recompute, unchanged v0.1 subset and identical P2
  forwarding, and D-P7-75 grants H10-BRIGHTNESS-4 full-v0.3/v0.5-only with P2's planned
  evidence case (docs/phase2/v2/PHASE_2_DOC.md:2962). Receiver-adopted, unverified; fresh applicable review,
  T10-INGRESS and transformed/runtime proofs remain future obligations.
- R10-9/C1's conventional participation surface requires exact P1/P7 receiver review;
  T10-CONVENTIONAL-INHERITED/PROVIDED remain planned observable proof, not executed evidence.

### 11.4 Hand-offs

- **Phase 7:** lifecycle/activation adapters including `prepare(token,layout,lighting)`;
  resolve user/pack/default pair before bake preparation, preserve frame timing and
  three-participant barrier, and invalidate after any effective pair change.
  Adopt exact `CURRENT_SCHEMA_VERSION` equality for P3 configuration/IdMappingInput and received inspection snapshots (23 at D-P10-28) and reject every other version/mismatch before preparation; shader macro projection is
  frozen by P3 before shader preprocessing, not recomputed by a vertex adapter.
  At v0.5 use P7 §4.6's prepared-submission count policy in §§4.6/5.2, including
  root-shadow-only admission, saved-parent instance restoration and failure containment.
  R10-6 requires exact owner10 CORE H10-MODEL-LIST-CAPTURE/PLAYBACK/LIFETIME
  in v0.1 base and v0.3 full subsets, with branch/deletion subrows and fingerprint
  renewal. Step1 drains model capture/playback/retirement borrows; step4 freezes
  capture demand; step9 invalidates before admission; step10 retains failed-recovery
  owners. Deliver current geometryInput at every cached main/root-shadow call,
  preserve owner9 surrounding row timing and P7 adjacent-copy/containment policy.
  R10-7 additionally requires all seventeen H10-SKY-* IDs in §4.11 in the installed
  v0.1 base, v0.3 full and v0.5 count-enabled fingerprints, preserving P7 primary IDs
  and one owner10 report. Step1 drains sky generation/staging/playback/binding/
  retirement; step4 freezes backend and resource/configuration/primitive demand;
  step9 invalidates before admission; step10 retains failed-recovery owners and
  restores safe canonical ownership only after drain. Receive resource-only NONE,
  world/mode/deletion and teardown invalidation, off-first bounded provenance and
  safe product-only rebuild; deliver current geometry/count at all seven sites,
  including final lower list, while H-SKY-01/03 remain program/cancellation wrappers.
  Do not expand P7 sky traversal or authorize arbitrary custom-sky list adoption.
  R10-8 additionally receives H10-WRITER-POS, H10-WRITER-COLOR-INT,
  H10-WRITER-COLOR-FLOAT, H10-WRITER-TEX, H10-WRITER-LIGHTMAP,
  H10-WRITER-NORMAL and H10-WRITER-ADVANCE, each expected=1, CORE/owner10;
  D-P10-30's H10-BRIGHTNESS-4 completes the same full-v0.3/v0.5-only receipt per
  P7 D-P7-75, with the v0.1 base subset unchanged.
  Amend existing begin/end/array/bulk/reset/seal/state coverage per §4.11; renew the
  full v0.3/v0.5 sorted-unique catalog fingerprint and reject stale/incomplete reports.
  Preserve v0.1 base subset and all prior model/sky/batch rows; forward the identical
  owner catalogue/counts to P2, with no new report, row compression or fake runtime evidence.
  R10-9 additionally preserves the complete authenticated conventionalInputs plan across
  prepared client/VBO/capture/replay submissions and existing invalidation/drain lifetimes.
  Absent COLOR/UV1 inherit each playback's current values; do not capture or re-bake them.
  No hook IDs, report shape, schema23 or nine inspection trees change.
- **Phase 1:** R10-9 receives §2.2's exact conventionalInputs component/domain and §4.6's
  full authenticated plan for all bind modes; source-absent COLOR/UV1 require temporary
  enable isolation, not pointer/current-value setup or Forge reset. Preserve per-playback
  inheritance and capture exclusion through existing bind/restore, without new native APIs.
- **Phase 9:** adopted matched lookup/ordinal lifetime and exact payload semantics;
  no lookup result may outlive its safe task borrow.
- **Phase 12:** canonical oldLighting tri-state wire setting and REPUBLISH, with renderer
  reload iff effective bake inputs change; pack separateAo changes use the same invalidation.
- **Phase 13:** unquantized derivation then classic quantized tangent frame is ready
  for normal/specular mapping; no atlas sampling or emissive-light policy was added.
- **Phase 2:** both draw paths, moving-camera/translucent scenes, current generation
  and hook evidence; continue manifests-not-images and download-at-test-time policy.
- **G8/S4:** append a named `at_midBlock` producer/layout after scanner/binder cutover;
  do not inherit Pintonium numbering, overflow or emission advertisement unexamined.

### 11.5 Requested upstream document changes

In addition to R10-1…R10-4: correct DESIGN Phase 10/PD §9's tangent equivalence claim,
add the actual 1.12.2 `celeritas` detection anchor and distance-read qualification,
clarify RESEARCH App C.1's “2 used” wording, and bring DESIGN's retired harness/adoption
instructions into line with MOVES. These are requests only. No RESEARCH, DESIGN,
reference report, dependency document, or review verdict was changed during authorship.

## 12. Implementation checklist

Ordered independently actionable future work. Every item carries a milestone and a
specific verification hook; this list does not authorize implementation before §11.3.

0. **[v0.1]** Verify adopted R10-5 metadata and P1 base-layout facade grants; implement
   §4.6 conditional topology adapters before affected .gsh support. Exercise T10-TOPOLOGY/
   T10-TOPOLOGY-LIFETIME and real client/VBO/list flat/ID/winding conformance, without moving
   CLASSIC_56/P9 stamping ahead of v0.3.
   Include all seventeen H10-SKY-* rows and all seven cached sky/star native sites;
   receive R10-7 and exercise T10-SKY-PRODUCTS/RETIRE with constructor off-first capture,
   resource/world/VBO switches and exact external restoration. **[v0.5]** additionally
   exercise T10-SKY-SITES adjacent copies under P7, without repeated sky traversal.
1. **[v0.3]** Reconcile freshly verified Phase 4/7/9 inputs and P1 R10-1, and verify
   the already-adopted R10-2/R10-3 owner/receiver symmetry before coding (integration
   review; reverify Phase 10 if its §5 changes).
2. **[v0.3]** Implement immutable classic layout, format projections and endian-explicit
   encoders; exercise T10-LAYOUT and the max-used-location boundary.
   Include D-P10-29 source-keyed conventionalInputs and padding-only absent COLOR/UV1
   projection; obtain R10-9 P1/P7 receipts and exercise T10-CONVENTIONAL-INHERITED/PROVIDED.
   Implement D-P10-30's scope-bound pending UV1 completion and mandatory H10-BRIGHTNESS-4
   wrapper; obtain P7 full-catalog/P2 evidence receipt and exercise T10-PARTIAL-BLOCK
   plus T10-LIST-FIXTURE-AUTHORITY. Missing/throwing completion cannot seal or publish.
3. **[v0.3]** Implement pure quad math with §4.3's square/mirror/degenerate oracles;
   exercise T10-MATH before touching buffer hooks.
4. **[v0.3]** Implement nullable builder attachment, authenticated source-semantic scalar
   dispatch and all seven writer hooks, both bulk ingress adapters, boundary reset,
   exception invalidation and dirty-tail sealing; exercise expanded T10-INGRESS/T10-OFF
   with real fluid/OLDMODEL chains and obtain R10-8 exact health receiver review.
5. **[v0.3]** Implement per-builder stacks, task context/pooled-builder ownership and
   finally cleanup around model/fluid calls; exercise T10-STACK on concurrent workers.
6. **[v0.3]** Implement stamped saved states, translucent resort and upload admission;
   exercise T10-STATE/T10-EPOCH with pending work crossing publication.
7. **[v0.3]** Implement Phase-1-adopted vertex facade and recorder/native restoration;
   exercise T10-GL-STATE on client/VBO sources including injected partial setup failure.
8. **[v0.3]** Implement live declaration plans and Phase 7 sink integration; exercise
   T10-DECLARATIONS across fallback, nested scope, shadow and fixed-function release.
9. **[v0.3]** Implement VBO metadata/draw and complete immediate/list client paths;
   run diagnostic material/tangent visualization with VBOs on/off, release capture
   memory before replay, and confirm generic state does not leak afterward.
   Authenticate the complete source participation/plan through saved state, upload and
   original/derived capture identities; changing cached fleece RGB/current UV1 must stay
   live on every playback without recapture, while BLOCK uses supplied vertex streams.
10. **[v0.3]** Implement quiesced format/default swaps, model/cache adapters and existing
    `IdDependentGeometryInvalidator`; exercise T10-TRANSITION and run OQ-14, selecting
    its evidence-backed fallback where necessary.
11. **[v0.3]** Install actual compat checks/plugin policy and complete owner-10 hook
    audit; exercise T10-COMPAT and run OQ-5 against Pintonium and available replacements.
12. **[v0.3]** Run the classic terrain T2 gate through the current Phase 2 harness in
    both draw modes, plus resource/world/reload and camera-motion regressions. Record
    only permitted provenance/manifests in the repository; fix observed contract bugs.
13. **[post-v0.5]** Adopt modern scanner/binder/layout contracts, then implement the
    first `at_midBlock` producer with validated encoding/capability policy; extend
    T10-GROWTH into real diagnostic delivery before advertising support. Treat velocity,
    fade and core-profile families as separate future implementations.

---

**Build-session result:** complete Phase 10 architecture authored under the explicit
provisional-dependency exception. **Unverified.** Fresh review, upstream interface
grants and dependency reconciliation remain required; no spike or runtime success is
claimed by this document.
