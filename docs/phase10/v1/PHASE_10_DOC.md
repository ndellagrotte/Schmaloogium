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
| GL facade ownership and replay vocabulary | **Owned by Phase 1.** R10-1 requests the missing vertex service; direct GL in a Mixin is not an alternative |
| Async GL, profiling optimizations and modern backend integration | **Owned by Phase 14 / G8.** Workers here never issue GL; no replacement chunk renderer |
| Modern attribute semantics and capability advertisement | **G8/S4 and Phase 3/4 cutover.** Slots are designed now; unsupported features are not advertised |

The stock chunk renderer, scheduling, culling and topology remain vanilla/Forge-owned.
Vanilla/Forge still compute lighting/AO samples; Phase 10 owns only the shader-visible
fixed-shade and AO-channel policy in §4.8.1. This is no terrain performance rewrite,
triangle conversion, global array cache or core-profile rewrite.

## 2. Architecture overview

All Phase-10-owned components in §§2–8 are **v0.3** unless explicitly identified as
post-v0.5 in §4.10/§9. Existing dependency components retain their own milestones.

### 2.1 Placement and responsibilities

Full package prefix is `com.schmaloogium`. R10-1 requests the exact new package homes;
Phase 1's current closed package allocation does not yet grant an `engine.vertex` root.

| Proposed home | Components |
|---|---|
| `:engine / engine.vertex` | Immutable `VertexLayout`, `VertexField`, `VertexEpoch`; `QuadAttributeWriter`; builder/mesh state policy; `VertexInputPlan`; closed outcomes |
| `:engine / engine.vertex.internal` | Primitive stack storage, dirty-range tracking and layout validation; private to this phase |
| `:engine / engine.gl` | Phase-1-owned requested `VertexInputService` and recording support; no Minecraft or LWJGL types |
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
record VertexInputPlan(String layoutFingerprint, List<AttributePointer> pointers) {}
record AttributePointer(String name, int location, int byteOffset,
                        int components, StorageType storage, boolean normalized) {}
```

Fingerprints identify immutable layout content, never act as credentials. `serial`
changes on every geometry-affecting publication, including off/on and resource reload
with unchanged alias bytes. `idGeneration` is Phase 9's independent generation, not
Phase 4's registry generation. `worldEpoch` prevents an old world/chunk coordinate from
matching a newly loaded world. No equality of fingerprints permits reusing stale work.

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
| E5 BufferBuilder | H10-BEGIN/END/ARRAY/BULK/STATE/SEAL/RESET | `[V:mcp]` App E5; `[V:observed]` Cleanroom bulk patch |
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

The Forge VertexFormat projection exposes ordinary position/color/UV/lightmap/normal
semantics. Extension bytes are non-FF storage with no automatic generic-array side
effect: use padding elements for the trailing extension span, with Phase 10 descriptors
owning its interpretation. Forge's generic element handlers must not enable arrays
behind the declaration plan's back. OQ-14 includes this projection through both
lighting paths. Do not assign midpoint an extra FF UV index or identity a normalized
Forge normal element merely to get storage.

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

**Incremental writes.** At `endVertex`, after vanilla has completed/count-incremented
the vertex and any buffer growth, stamp the current identity into that vertex. Track
indices, never cached native pointers across growth. Initialize extension bytes before
a vertex can be observed. On every fourth vertex in QUADS mode finalize the four
vertices using §4.3. This runs even if the current render-thread program does not use
tangents: chunk compilation is not tied to a particular future program.

**Bulk writes.** Both `addVertexData(int[])` and Forge `putBulkData(ByteBuffer)` are
first-class ingress. Snapshot the append start/count, let the supported input-format
adapter perform the ordinary append, then stamp **only the appended vertices** with
the current stack value and finalize every newly complete quad, including a quad
started by incremental writes. Never mutate the caller's array/buffer contents, change
its ownership, or stamp IDs into cached BakedQuad data. Preserve each API's ordinary
position/limit consumption behavior. Do not replace Cleanroom's extra-vertex growth
allowance (`BufferBuilder.java.patch:18,68`) with a smaller allocation.

There is no stride inference from length divisibility: 56 ints could describe eight
vanilla vertices or four extended vertices. The ordinary unannotated raw-array API
means the current builder format, as vanilla defines it. A BakedQuad adapter receives
the quad's actual `getFormat()`, and a bulk byte adapter carries its source descriptor
in a call-local glue token. Only those authenticated source formats permit conversion.
Unknown raw formats are not guessed to be BLOCK or ITEM.

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
not alter the tangent frame. The hook-health inventory includes any such mutation
adapter selected from the implementation's actual writers; no whole-layer second
re-encode is the default algorithm.

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

`BufferBuilder.State` copies carry layout, epoch, vertex count and seal status through
an attached immutable stamp. State restore accepts only matching current epoch and
format; it must not restamp saved vertices with today's neutral/current block ID.
Sorting moves whole records/quads and therefore all attributes together. It never
recomputes identities from the empty stack used during translucent resort.

Before translucent state capture, finalize dirty quads. Before restoring/resorting,
validate world/ID/layout/serial. A stale saved translucent state triggers full rebuild,
not a resort attempt. A state from an uninstrumented external producer has no Phase 10
stamp: it may enter a vanilla builder, or an explicitly declared source-format adapter;
it is not trusted as a current extended chunk merely because its stride equals 56.

The queued upload carries the sealed VertexEpoch and the source's remaining byte range.
Validate both when queued and when executed on the render thread, since a toggle can
occur between them. Require whole records, valid count/range and the live chunk task's
identity. Reject stale data before modifying a VBO, compiling a display list or marking
a CompiledChunk ready. Cancel/discard via the existing task cancellation path, and
schedule a replacement; do not report an exceptional future to vanilla's crash-report
callback for an expected stale-generation event.

`VertexBuffer` owns a sidecar containing the actual uploaded layout/epoch/count. Set it
only after successful upload and error handling. Compute count from the uploaded byte
range and that stored stride, never a now-mutated global default format. Empty uploads
clear count; failed uploads clear eligibility. Existing VBOs and display lists from the
old epoch become ineligible atomically before new frame admission, even while rebuilds
are pending. Skipping a stale chunk is safer than interpreting its old bytes differently.

### 4.6 Draw paths and restoration

#### Shared declaration plan

Phase 4 binds `mc_Entity=10`, `mc_midTexCoord=11`, `at_tangent=12`. Phase 10 consumes
only this published table directly. R10-2 asks Phase 7 to deliver the effective
provider's already-scanned declaration set after successful activation. The Phase 4
`ProgramStateBundle.attributes` is available to Phase 7, but Phase 4's §5 does not
currently grant Phase 10 a current-program lookup.

For a live draw, enable exactly the intersection of declared names and physically
present supported fields. Disable every other Phase-10-owned generic array. Missing
identity on a non-extended draw uses floating neutral `(0,0,0,1)` with its array
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

1. validate sealed builder, source range, epoch, layout and current input scope;
2. use a borrowed client-data view with stride 56 and the offsets above; establish
   array-buffer binding zero before client pointers;
3. retain the direct buffer and pointer ranges for the complete call;
4. install declared generic pointers and masks, perform the original draw once;
5. in `finally`, restore generic array/pointer/current-value state and buffer bindings;
   run the normal Forge element `postDraw` cleanup and builder reset even on a
   shader-side failure. Do not duplicate a draw after partial execution.

Ordinary FF elements still go through Forge's `VertexFormatElement.EnumUsage.preDraw`
and `postDraw`. The patch replaced the old uploader switch; a design targeting that
removed switch would miss the actual path. The outer uploader cleanup guard covers
an exception before the inner draw wrapper is reached.

#### VBO path

At upload, authenticate the exact source and retain its actual layout metadata.
At `VertexBuffer.drawArrays`, the facade uses a borrowed native VBO identity issued by
the mod adapter, binds that source, and configures FF pointers from its stored layout
plus generic pointers from the current declaration plan. Pointer arguments are byte
offsets, not client addresses. A 56-byte VBO must never inherit VboRenderList's vanilla
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
activation. It installs client arrays only for the capture operation and restores them
immediately afterward. Compile a list only while shader vertex mode is active; an
epoch/mask change invalidates and rebuilds it.

At replay, the recorded vertex attributes are already part of the list; there is no
live client pointer to its former builder memory. Apply the current shader's declared
input policy to live array state, invoke the list, then restore generic current values
that recorded attribute commands may have changed. Undeclared inputs are unused by
the program, not live enabled arrays. Do not replay the Java uploader per frame or
retain freed client memory to emulate a list.

This capture-vs-live-draw distinction is D-P10-7. It must be exercised with VBOs off
and different main/shadow declaration sets. If the actual compat driver cannot capture
these generic arrays correctly, shaders fail safely for this path; **forcing VBOs is
not an acceptable completion of the required two-path implementation**.

#### Facade boundary

R10-1 requests one typed vertex-state scope from the existing GLDevice, implemented
and recorded under Phase 1's facade. It saves only the affected domains: generic
arrays/current values/pointers, relevant FF client arrays and pointers, array-buffer
binding, and client-active texture. It preserves the VAO binding rather than resetting
it to zero or clearing all sixteen attributes as Pintonium does. Nested scopes restore
the immediately preceding state in LIFO order. GL state cached by GlStateManager is
changed/restored through it. Current constants are explicitly initialized because array
draws can leave generic current values indeterminate; no previous entity may leak.

The existing `StateService.snapshot()` covers frame state, **not** this complete
vertex domain. It is not cited as proof of restoration. Driver errors use bounded
facade drain windows; a batched error is not falsely attributed to one attribute.

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

One Phase 7 no-frame transaction owns the transition. The additional Phase 10 phases
are `VANILLA`, `QUIESCING`, `PREPARED`, `ACTIVE`, `RECOVERING`, and session `BAILED`.
A request during a frame queues; it cannot mutate format fields in place.

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
6. Follow the **current Phase 7 §5.3** publication order. After Phase 9 accepts its new
   generation, install its exact lookup and ordinal map in the prepared vertex epoch,
   invoke `IdDependentGeometryInvalidator`, and admit only matching tasks/products.
7. Return `Completed` to Phase 7 once every old product is ineligible, caches/layers
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
pack TRUE/FALSE wins, else use **false**. P3's pre-load `MC_OLD_LIGHTING` macro reflects
only explicit user true and is not a post-parse effective-policy query. Default/false
omit the macro; P10 never mutates same-build macro state after parsing.

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
The early/late split and the mapping from actual Phase 1 verdicts to Phase 7's prose
compatibility outcomes need the explicit clarification requested in R10-1/R10-2.

Incompatible vanilla-target mutation mixins are vetoed as one group when possible.
Early nullable attachments, if unavoidable before MOD phase, must be inert and perform
no format mutation; OQ-5 determines that placement on the pinned loader. A missing
or overmatched required P10 anchor disables the entire extended pipeline. No half-set
of stamping and pointer mixins is an admissible configuration.

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
| H10-END | E5 `BufferBuilder.func_181675_d` | AFTER completed vertex; stamp and finalize newly complete quad |
| H10-ARRAY | E5 `BufferBuilder.func_178981_a` | AROUND bulk append; capture old count, adapt explicitly typed input, stamp only appended region |
| H10-BULK | E5 Forge `BufferBuilder.putBulkData` | Same bulk protocol; preserve extra-growth allowance and buffer consumption |
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

Exact mapping results `[V:mcp — 2026-09-07, MC 1.12.2]`:

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
| `renderer.BufferBuilder.finishDrawing` / `reset` | `func_178977_d` / `func_178965_a` | `()V` / `()V` |
| `renderer.BufferBuilder.getVertexState` | `func_181672_a` | `()Lnet/minecraft/client/renderer/BufferBuilder$State;` |
| `renderer.BufferBuilder.setVertexState` | `func_178993_a` | `(Lnet/minecraft/client/renderer/BufferBuilder$State;)V` |
| `renderer.BufferBuilder.sortVertexData` | `func_181674_a` | `(FFF)V` |
| `renderer.Tessellator.draw` | `func_78381_a` | `()V` |
| `renderer.WorldVertexBufferUploader.draw` | `func_181679_a` | `(Lnet/minecraft/client/renderer/BufferBuilder;)V` |
| `renderer.vertex.VertexBuffer.bufferData` / `drawArrays` | `func_181722_a` / `func_177358_a` | `(Ljava/nio/ByteBuffer;)V` / `(I)V` |
| `renderer.RenderList.renderChunkLayer` / `renderer.VboRenderList.renderChunkLayer` | `func_178001_a` | `(Lnet/minecraft/util/BlockRenderLayer;)V` |

Owner prefixes in the table are `net.minecraft.client.`. `updateChunkNow` is a mapped
coverage entry, not another stack owner; its invoked worker/rebuild guards do the work.

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

## 5. Cross-phase interfaces

This section is the monitored interface region. Detailed semantics in §§4.1–4.11 are
incorporated by the rows below. All **new** interfaces here are Phase 10 proposals;
requests name the owner that must adopt them. No dependency private type is consumed.

### 5.1 Exposed Phase 10 contracts

| Contract | Exact content and ownership | Consumer |
|---|---|---|
| `VertexLayout` / `VertexField` / `VertexInputPlan` | Immutable named fields, byte stride, scalar storage/conversion, validation and fingerprint; classic values §4.1 | mod adapters, requested facade, conformance; G8 growth |
| `VertexEpoch` | Independent vertex serial, world epoch, ID generation, layout fingerprint; equality necessary, not sufficient, for product admission | task/upload/state adapters; Phase 7 composition |
| `VertexPipelineLifecycle` | Quiesce/prepare/activate/recover protocol below; no active frame or stale worker can overlap mutation | Phase 7 requested participant |
| `VertexProgramInputSink` | Activation-scoped declaration delivery below, reset on every non-shader/abort/close transition; no program selection or GL upload | Phase 7 requested adapter |
| `IdDependentGeometryInvalidator` implementation | Implements the **existing Phase 7 interface**, exact `IdPublicationChange` and closed results; Completed only after old-product ineligibility, layer invalidation and successful scheduling | Phase 7 |
| Builder/task/state/mesh sidecar protocol | §4.2/§4.4/§4.5: immutable handoff metadata, exclusive writers, no source mutation, expected stale cancellation | `mod.glue` vanilla adapters, not a public Minecraft API in engine |
| `ChunkRendererCompatCheck` | Existing Phase 1 `CompatCheck`, fixed check ID `schmaloogium.chunk_renderer`, table §4.9, session Bail | Phase 1 registry, Phase 7 admission, Phase 12 diagnostics |
| Owner-phase-10 hook subreport | Exact shared `HookApplicationSubreport` shape, fingerprint and rows §4.11 | Phase 7 report composer / Phase 2 manifests |
| Growth producer requirements | Position/UV/block-context prerequisites, immutable name/layout cutover; no modern runtime producer in v0.3 | G8/S4, Phases 3/4 |
| `ShaderLightingPolicy` | immutable effective `oldLighting`/`separateAo`; exact shader-visible RGB/AO-alpha semantics, local defaults and precedence in §4.8.1; captured in transition/bake identity and never sampled per vertex from mutable settings | Phase 7 preparation, Phase 12 reload policy, mod bake/build adapters |

Proposed lifecycle operations (render thread only):

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
`Drained`, `Failed(diagnosticId)`. Prepare outcomes: `Prepared` or `Failed`;
activate outcomes: `Activated(epoch)` or `Rejected(STALE_EPOCH|NOT_PREPARED|
LOOKUP_GENERATION_MISMATCH|HOOK_UNHEALTHY|BAILED)`. Recovery outcomes:
`VanillaReady` or `Failed(diagnosticId)`; recovery failure keeps shader admission
closed and discards old geometry, never authorizes unsafe reuse. Only Drained permits
prepare/format mutation. Prepare owns no new alias publication; activate borrows one
kept alive by Phase 7. Terminal outcomes invalidate the token.

This does not add an ID runtime publisher or bypass Phase 7's atomic pipeline install.
The lifecycle's `Activated` means vertex readiness, not independent frame admission.
`IdDependentGeometryInvalidator.Completed` preserves Phase 7's scheduling-only gate;
its existing rejections remain `STALE_GENERATION`, `INVALID_CHANGE`,
`SCHEDULER_UNAVAILABLE`, and failures carry Phase 7 `FailureId`.

Proposed declaration delivery:

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
`InputScopeResult` is `Entered(opaque scope)` or
`Rejected(STALE_EPOCH|NO_ACTIVE_SHADER|INVALID_DECLARATION|WRONG_THREAD)`.
Nested enter/leave is LIFO; leave restores the preceding effective declaration set.
`reset` invalidates every scope. Draw adapters borrow the current valid scope for the
operation only. LIST_CAPTURE has a separate prepared-epoch union plan, never an
Entered shader credential. The sink performs no GL and is **not** a Phase 4 barrier
participant; the requested Phase 7 adapter invokes it at its established activation
and restoration boundaries.

### 5.2 Consumed contracts: existing, not inferred

| Dependency | Actual exposed input | Use / limitation |
|---|---|---|
| Phase 4 §5.1 fixed attribute row | 10/11/12 exactly | Direct Phase 10 consumption; does **not** grant active-program query or registry internals |
| Phase 7 §5.1 / §5.5 | `IdDependentGeometryInvalidator`, `IdPublicationChange`, closed invalidation results | Concrete implementation supplied here; all generations invalidate, even identical bytes |
| Phase 7 §5.1 | `FrameToken`, frame abort taxonomy, `ShaderReloadController` queuing and report/subreport shapes | Glue uses existing orchestration and diagnostic vocabulary; no independent frame driver |
| Phase 7 §5.3 | Quiesced coordinated publication, IDs after textures/invalidation before atomic Active, every failed rebuild to off | Adopted alongside amended Phase 9 §5.3; all remain unverified |
| Phase 7 §4.10/§5.5 | Deferred App E rows 3–9 and hook conventions | Claimed by §4.11; extension APIs still require R10-2 |
| Phase 9 §5.1 / §4.10 | `AliasLookup.generation()` and `mcEntity(int)`, `BlockStampResult` | One immutable lookup per task, exact two words, no alias re-resolution |
| Phase 1 §5.3 / §4.10, narrow extra input | `CompatCheck`, `CompatContext`, `CompatVerdict`, `BailRegistry` | Existing mechanism only; no fabricated `Compatible` enum from Phase 7 prose |
| Phase 1 §5.1/§5.2 | D-6 package seam, `GLCapabilityProfile.maxVertexAttribs`, diagnostics/error and recorder conventions | Proposed vertex verbs are explicitly absent until R10-1 adoption |
| Phase 3 §§4.8/5 engine flags and codec, through Phase 7 | schema17 `oldLighting` decoded user tri-state and pack oldLighting/separateAo tri-states | Phase 7 resolves §4.8.1 pair, preserving current same-build macro/materialization identity; no pack reopening or private parser |

Phase 3 attribute names still flow through Phase 4's effective state; the narrow
lighting/codec authority is consumed through Phase 7's immutable resolved input,
not a new direct front-end dependency. Phase 9's glue-owned ordinal map is installed
with the exact lookup and retained until the final worker borrow ends.

### 5.3 Required owner changes and adoption status

| Request | Owner | Required binding change | Gate |
|---|---|---|---|
| R10-1 | Phase 1 | Grant `engine.vertex`, `mod.glue.vertex`, `mod.mixin.compat.vertex`; add typed vertex-input facade scope/recording contract below; clarify early class-only compatibility evaluation and MOD/preinit inert-attachment placement | All native pointer work and final package placement; no direct-GL workaround |
| R10-2 | Phase 7 | Install lifecycle and declaration sinks with exact §5.1 sequencing, including immutable ShaderLightingPolicy at prepare; quiesce workers before retiring matched ID lookup/map; format/resource/world/video/effective-lighting transitions compensate off; owner-10 subreport and actual BailRegistry verdict | Phase 7 adoption/fresh verification gates in-game vertex integration |
| R10-3 | Phase 9 | Owner-designed/receiver-adopted unverified in P9 §§4.1/5.3/5.4: publish after textures, every rebuild failure to off, matched lookup/map close after workers drain | Fresh Phase 9/10 verification remains; no longer a missing owner design |
| R10-4 | Phases 3/4, via future G8/S4 | Extend declared-name and pre-bind catalogs in the same change that adds a modern producer/layout; no fixed-location Phase 10 private enum extension | post-v0.5 attribute activation only |

Requested Phase-1 facade addition, deliberately limited to vertex input rather than a
new renderer:

```java
interface VertexInputService {
    VertexBindResult bind(VertexSource source, VertexLayout layout,
                          VertexInputPlan plan, VertexBindMode mode);
    void restore(VertexBinding binding);
}
enum VertexBindMode { LIVE_DRAW, LIST_CAPTURE, LIST_REPLAY_GUARD }
```

`GLDevice` supplies the service. `VertexSource` is a closed client-range, borrowed-VBO,
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

The owner may refine these names in its fix-up, but cannot omit the state/restoration,
source provenance or no-worker-GL semantics. Corresponding §5 changes require fresh
owner verification and Phase 10 interface reconciliation. All three current dependency
exceptions remain authoring-only until that process completes.

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
conversion pass. Plans are precomputed per immutable layout and effective declaration
set; no temporary Sets or pointer lists per draw. Necessary bulk format conversion
copies directly into destination once and never clones already matching input merely
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
| T10-INGRESS | Equivalent incremental, typed int[] and ByteBuffer input yields the same shader-visible quad; a quad split across incremental/bulk boundaries completes once; cached source bytes remain unchanged |
| T10-STACK | Nested states A/B/A, two interleaved builders and two workers; a throw/cancel restores the previous depth, and the next unrelated task starts neutral |
| T10-STATE | Translucent reorder moves identities/tangents with whole quads; save/restore does not stamp the neutral resort stack over existing IDs; stale state is discarded |
| T10-EPOCH | Queue under epoch A, activate B, run A's queued upload: no upload/draw occurs, rebuild is scheduled. Include identical alias fingerprints with unequal generations and world change with repeated coordinates |
| T10-TRANSITION | Worker holds old alias borrow; prepare cannot swap until drain; failure after partial publication converges off, releases sources and leaves no stale eligible mesh |
| T10-LIGHTING | Explicit user false overrides pack true; user DEFAULT delegates to pack; both DEFAULT use local true oldLighting/false separateAo. Render original project-owned diagnostic geometry with known tint/AO/lightmap: separateAo changes only the declared RGB/alpha split, fixed shade applies once, lightmap stays intact. Change policy while a worker/upload is pending; old baked/mesh products cannot draw under the new pair |
| T10-DECLARATIONS | Effective fallback declares only midpoint while requested slot declares identity: only location 11 enabled. Shadow and nested restoration choose their actual provider; shader-to-fixed transition clears old arrays |
| T10-GL-STATE | Recorder starts with nondefault source bindings and unrelated enabled attributes. Nested client/VBO setup plus injected error restores only the touched state, leaves unrelated state intact, rejects stale/forged source before native commands |
| T10-COMPAT | Actual `celeritas` ID alone, exact renderer class alone, throwing probe, late positive before format swap, and clean vanilla. A package string is not a class probe; positive results latch off |
| T10-OFF | Run ordinary builder lifecycle with shaders off: bytes/count match vanilla, no sidecar allocation/TLS lookup/extended facade calls or changed defaults |
| T10-GROWTH | Test-only appended named field changes stride; unchanged binder/copy/state algorithms deliver its supplied values without editing consumers. This proves extensibility, not modern pack support |

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
6. **Global module layout lacks the vertex policy home and facade operations.**
   Request the precise homes/service under R10-1; do not conceal GL or Minecraft
   types in the pure math layer to avoid the request.
7. **Governance files mention a retired executable profile system.** MOVES records
   its removal and per-header authority. Initial v3 adoption is declared and indexed;
   no dry-run output or target-profile modification is claimed.
8. **Current dependencies are unverified despite older PASS files.** The maintainer
   explicitly extended the exception after this was discovered. All consumed current
   surfaces remain provisional, including incorporated details with shifted lines.

### 11.3 Open items and integration gates

- OQ-5/OQ-14 remain open; their procedures and fallbacks are complete specifications,
  not a promise that classloading, cache switches or display-list capture already work.
- R10-1/R10-2 require owner adoption and fresh review before implementation; R10-3
  is adopted but unverified. R10-4 gates only modern growth.
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

### 11.4 Hand-offs

- **Phase 7:** lifecycle/activation adapters including `prepare(token,layout,lighting)`;
  resolve user/pack/default pair before bake preparation, preserve frame timing and
  three-participant barrier, and invalidate after any effective pair change.
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

1. **[v0.3]** Reconcile freshly verified Phase 4/7/9 inputs and adopt R10-1…R10-3 in
   their owners; verify exact consumed/exposed §5 symmetry before coding (integration
   review; reverify Phase 10 if its §5 changes).
2. **[v0.3]** Implement immutable classic layout, format projections and endian-explicit
   encoders; exercise T10-LAYOUT and the max-used-location boundary.
3. **[v0.3]** Implement pure quad math with §4.3's square/mirror/degenerate oracles;
   exercise T10-MATH before touching buffer hooks.
4. **[v0.3]** Implement nullable builder attachment, incremental and both bulk ingress
   adapters, dirty-tail sealing and source immutability; exercise T10-INGRESS/T10-OFF.
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
