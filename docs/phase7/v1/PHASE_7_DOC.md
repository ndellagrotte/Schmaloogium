# Schmaloogium — Phase 7: Render-loop integration & frame orchestration — Architecture

## 0. Header

**Phase:** 7, both mandated parts: (a) engine-side frame driver and (b) Mixin hook catalog.  
**Document version:** v1, initial build.  
**Date:** 2026-08-03 · **Last revised:** 2026-09-07 (§0.41).
**Governing design:** `docs/design/v2.0-RC3/DESIGN.md`; its Phase 7 assignment begins at
`docs/design/v2.0-RC3/DESIGN.md:1805` and names dependencies 2–6 at
`docs/design/v2.0-RC3/DESIGN.md:1807`. The heading and ranges were derived from this
file's own headings, not shifted from another `DESIGN.md` revision.  
**Build-session result:** both parts fit in this session; the Phase 7b fallback was not invoked.

### 0.1 Inputs actually read

The assigned reading order was followed. Repository governance was read first:

- `AGENTS.md` in full and `docs/MOVES.md` in full.
- `docs/design/v2.0-RC3/DESIGN.md` Part I, §§G0–G12
  (`docs/design/v2.0-RC3/DESIGN.md:92`–`:1109`), and only the Phase 7 Part-II assignment
  (`docs/design/v2.0-RC3/DESIGN.md:1805`–`:1953`). The mandatory thirteen-section skeleton is at
  `docs/design/v2.0-RC3/DESIGN.md:790`–`:827`.
- `docs/research/v1/RESEARCH.md` §0, §1, §4.4, §5.3, §7.1, Appendix A.1, and Appendix E in
  full. The exact OQ-3/OQ-4 rows in §11 were also read because §G4.4 requires their questions
  verbatim in §10 below.
- `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §4, §6.1, and §16.
- The two assigned LGPL field-evidence files, and no other Pintonium implementation file:
  `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java`
  and
  `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinRenderGlobal_Shaders.java`.
- Cleanroom deltas in
  `reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch`
  and
  `reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch`.
- Only the lifecycle, per-frame flow, and one-frame summary sections of
  `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md` (§2, §5, §14), plus
  `reference-src/schlorbium-HD_U_G6_pre1/files.txt`. The digest was used only for behavioral
  observation. No decompiled implementation source, identifier, or code structure is reproduced.

The `cleanroom` MCP server was used on 2026-08-02 as assigned:

- `resolve_symbol` and `get_method_signature` checked every Appendix-E method consumed or
  explicitly deferred below, plus the supporting sky/weather/hand/particle/fog/blend/beam/glint/
  leash/resize/bootstrap methods. MCP stable_39 confirmed, among others,
  `EntityRenderer.renderWorldPass` → `func_175068_a (IFJ)V`,
  `RenderGlobal.renderSky` → `func_174976_a (FI)V`, and the world-render overload of
  `RenderGlobal.renderBlockLayer` → `func_174977_a
  (Lnet/minecraft/util/BlockRenderLayer;DILnet/minecraft/entity/Entity;)I`.
- `search_cleanroom_api(kind=event)` and `get_api_class` checked
  `RenderWorldLastEvent`, `RenderHandEvent`, `RenderSpecificHandEvent`,
  `DrawBlockHighlightEvent`, `RenderBlockOverlayEvent`, the three fog events,
  `RenderTickEvent`, `WorldEvent.Unload`, and `TextureStitchEvent`. Section 4.11 records each
  event decision; an event is not claimed to cover a moment its published API does not expose.

### 0.2 Dependency PHASE docs consumed

Only verified dependency artifacts were consumed. Their latest reviews were inspected solely to
establish the §G5.3 gate, before their current binding contracts were used:

| Dependency | Current artifact consumed | Binding material actually read | Gate evidence |
|---|---|---|---|
| Phase 2 | `docs/phase2/v1/PHASE_2_DOC.md` | §3.5, §4.5, §4.9, §5 | literal `PASS` at `docs/phase2/reviews/PHASE_2_REVIEW_15.md:67` |
| Phase 3 | `docs/phase3/v1/PHASE_3_DOC.md` | §§2.2–2.4, §§3.1–3.2, §§4.1–4.2, §§4.7–4.9, §5 | literal `PASS` at `docs/phase3/reviews/PHASE_3_REVIEW_22.md:51` |
| Phase 4 | `docs/phase4/v1/PHASE_4_DOC.md` | §§2.2–2.3, §§3.2–3.3, §§4.1–4.2, §4.10, §5 | literal `PASS` at `docs/phase4/reviews/PHASE_4_REVIEW_18.md:57` |
| Phase 5 | `docs/phase5/v1/PHASE_5_DOC.md` | §§2.1–2.3, §§4.4–4.5, §4.9, §5 | literal `PASS` at `docs/phase5/reviews/PHASE_5_REVIEW_30.md:54` |
| Phase 6 | `docs/phase6/v1/PHASE_6_DOC.md` | §§2.1–2.3, §4.2, §§4.6–4.7, §4.12, §5 | literal `PASS` at `docs/phase6/reviews/PHASE_6_REVIEW_7.md:52` |

The dependency rule is applied literally: this document consumes only interfaces present in those
current §5 regions. Section 5.4 flags missing or contradictory interfaces and does not pretend the
requested forms already exist.

### 0.3 Reading-list deviations and reasons

Three narrow additions were necessary and are recorded rather than hidden:

1. `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §3.1 was read because the Phase 7 scope
   itself mandates its per-dimension pipeline-cache/version-counter shape at
   `docs/design/v2.0-RC3/DESIGN.md:1853`–`:1855`, but the Required-inputs list accidentally names
   only PD §4, §6.1, and §16.
2. `docs/research/v1/RESEARCH.md` Appendix F.1 was read because the assignment makes Phase 7 the
   behavior owner for eleven engine flags while the listed inputs otherwise provide only their
   field names. The source says the corresponding video setting wins where one exists
   (`docs/research/v1/RESEARCH.md:1442`–`:1448`).
3. The exact OQ-3/OQ-4 rows at `docs/research/v1/RESEARCH.md:1009`–`:1010` were read to satisfy
   §G4.4's verbatim-question rule.

There was no network use. No `docs/**/chatlogs/**`, root-level `*.txt`, Oculus material, unlisted
Pintonium source, or decompiled OptiFine implementation source was read.

### 0.4 Legal and provenance posture

Schmaloogium remains GPL-3.0-or-later. Pintonium observations below carry an exact
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/...:line]` coordinate; its mechanism is
evidence, never contract. The two assigned
Pintonium files are LGPL evidence and no code is copied. The Schlorbium digest is used only to
restate behavior under the governing clean-room/licensing rules. RESEARCH wins every conflict. Nothing from
`glsl-transformation-lib`, vendored stareval, or any excluded transformation boundary is used.

### 0.5 Round-1 fix-up

Round 1 defines the hook result algebras and the synchronous Phase 8 invocation seam in §5.1, and
corrects `ProgramStateBundle` dependency ownership in §5.2. The §5 interface region changed and
therefore requires a fresh verification round before Phase 7 can close.

### 0.6 Round-2 fix-up

Round 2 closes the diagnostic-ID domain, makes the remaining consumer-facing §5.1 schemas
callable and closed, and adds the two missing lifecycle rows to §3.1. The §5 interface region
changed again, so a fresh verification round is required before Phase 7 can close.

### 0.7 Round-3 fix-up

Round 3 corrects four non-interface identifier and decision-disposition inconsistencies. The §5
interface region is unchanged.

### 0.8 Round-4 fix-up

Round 4 restores Phase 5 ownership of the final draw target and adds row-local provenance to the
two remaining conformance tables. The §5 interface region changed and requires fresh verification.

### 0.9 Round-5 fix-up

Round 5 defines capture-listener installation and corrects readiness generation identity. The §5
interface region changed and requires fresh verification.

### 0.10 Round-6 fix-up

Round 6 makes hook-report classes faithful to the catalog, including mixed-class rows and deferred
owner phases. The §5 interface region changed and requires fresh verification.

### 0.11 Round-7 fix-up

Round 7 qualifies mixed-row deferral and makes the built-in content contract a Phase 3 internal
source. The §5 interface region changed and requires fresh verification.

### 0.12 Round-8 fix-up

Round 8 defines the internal-pack content-digest contract and corpus invariants. The §5 interface
region changed and requires fresh verification.

### 0.13 Round-9 fix-up

Round 9 closes canonical path ordering and requests the missing Phase 3 path projection. The §5
interface region changed and requires fresh verification.

### 0.14 Round-10 fix-up

Round 10 synchronizes R7-9 into the downstream dependency and implementation action lists. The §5
interface region is unchanged.

### 0.15 Round-11 fix-up

Round 11 corrects R7-9 staging, feature-specific checklist gates, and §3.5 provenance tags. The §5
interface region is unchanged.

### 0.16 Round-12 fix-up

Round 12 separates ordinary internal loading from gated manifest production, closes reload intents,
and distinguishes documented engine-flag facts from Phase 7 runtime decisions. The §5 interface
region changed and requires fresh verification.

### 0.17 Round-13 fix-up

Round 13 closes reload-reason validation and makes coordinator publication an internal composition
capability. The §5 interface region changed and requires fresh verification.

### 0.18 Round-14 fix-up

Round 14 closes the resize/attachment observation-to-safe-boundary scheduling seam. The §5
interface region changed and requires fresh verification.

### 0.19 Round-15 fix-up

Round 15 authenticates resize state to a world incarnation and publishes its narrow observation
facade to glue. The §5 interface region changed and requires fresh verification.

### 0.20 Round-16 fix-up

Round 16 defines the canonical active-world identity publication used to authenticate frame and
resize signals. The §5 interface region changed and requires fresh verification.

### 0.21 Round-17 fix-up

Round 17 corrects the §5 fence, closes reload-token polling, and traces first-person overlay.
The §5 interface region changed and requires fresh verification.

### 0.22 Round-18 fix-up

Round 18 synchronizes reload polling into the §5 exposed-contract inventory. The §5 interface
region changed and requires fresh verification.

### 0.23 Maintenance addendum (Phase 9 coordinated integration — 2026-08-03)

Phase 9's R9-2 request is granted across §§1–9, 11, and 12. The safe-boundary transaction now owns
and publishes the Phase 9 ID-runtime candidate as a fourth coherent component, resets per-draw
dynamics on every terminal path, samples held values immediately after Phase 6 accepts a frame,
orders entity and block-entity ID scopes around existing program scopes, and replaces the former
RETURN-only color observer with exact `GL_TEXTURE_ENV_COLOR` operand capture. Registry remap and
ID-source reasons enter the reload queue, Phase-9 hook rows enter `HookApplicationReport`, and a
generation change gates layer/chunk invalidation before another shader frame.

The maintenance read was limited to Phase 9's R9-2 request and the detailed regions it names in
`docs/phase9/v1/PHASE_9_DOC.md` §§4.9 and 4.11–4.14, §§5.1–5.4, and §8. Phase 9 is a downstream
request source, not a declared Phase 7 dependency; Phase 7 consumes its runtime contracts only
after Phase 9 itself is verified. Phase 3's R9-1 surface was re-read only after its fresh literal
PASS in `docs/phase3/reviews/PHASE_3_REVIEW_22.md`.

**Current §G1.3 status:** round nineteen's literal PASS applies only to the pre-§0.23 bytes. This
addendum changes §5, so Phase 7 is **not verified** and is not a valid dependency input until a
fresh verification round returns literal PASS. The version directory remains `v1` while the loop
is open.

### 0.24 Round-20 fix-up

Round 20 corrects the two active Phase 9 ID-row counts to use the declared Mixin injection-anchor
unit. The §5 interface region changed and requires fresh verification.

### 0.25 Downstream-request addendum (Phase 8 coordinated integration — 2026-08-03)

Round twenty-one subsequently returned literal PASS with zero findings
(`docs/phase7/reviews/PHASE_7_REVIEW_21.md`). This maintenance amendment accepts Phase 8 R8-1,
R8-4, and Phase 7's half of R8-5 from `docs/phase8/v1/PHASE_8_DOC.md` §5.5.

The frame driver now lends an immutable `ShadowFrameView` with the exact driver frame ID, main
`setupTerrain` token, copied camera position, and same-sample sky/sun angles. A Phase-7-owned
`ShadowExecutionBridge` opens one authenticated dynamic-extent credential around slot invocation,
closes it before main clear, and makes existing terrain/entity/cloud/frustum hooks bypass only
their main-snapshot policy while that credential is active. Pipeline construction projects one
typed Phase 8 policy without reparsing, plans before constructing the Phase 6 provider, shares the
plan's celestial policy with that provider, and owns the resulting Phase 8 publication in its
rollback/teardown ledger. The application report nests Phase 8's immutable health rows without
renaming or re-keying any Phase 7 row.

**Current §G1.3 status:** round twenty-one's PASS applies to the pre-§0.25 bytes. This amendment
changes binding §5, so Phase 7 is **not verified** and is not a valid dependency input until a
fresh round twenty-two returns literal PASS (or any corrections are fixed and the changed
interface is re-verified). The version directory and manifest remain at `v1` while the loop is
open.

### 0.26 Round-22 fix-up

Round 22 corrects the compact `QUIESCING` diagram to match the binding teardown order: close
Phase 8, reset and deactivate Phase 9, release/publish Phase 5/4 off, then close Phase 6. The §5
interface region is unchanged.

### 0.27 Round-23 fix-up

Round 23 adds Phase 5's consumed resize registration, synchronous delivery, and closed-result
contract to §5.2 and corrects that inventory's binding citation. The §5 interface region changed
and requires fresh verification.

### 0.28 Round-25 fix-up

Round 25 corrects the Phase 2–6 dependency inventories to cite their current binding §5 rows.
The §5 interface region changed and requires fresh verification.

### 0.29 Round-26 fix-up

Round 26 aligns Phase 3 ID-mapping consumption with schema v3 and corrects two metadata citations.
The §5 interface region changed and requires fresh verification.

### 0.30 Round-27 fix-up

Round 27 recognizes Phase 3's R7-9 grant while retaining its pending-reverification production
gate, and corrects three §3.1 provenance coordinates. The §5 interface region changed and requires
fresh verification.

### 0.31 Round-28 fix-up

Round 28 adds the required Phase 6 registry-generation adoption to coordinated publication and
synchronizes the closing status. The §5 interface region changed and requires fresh verification.

### 0.32 Round-29 fix-up

Round 29 synchronizes the header and closing status with the Round-28 correction. The §5 interface
region is unchanged.

### 0.33 Round-30 fix-up

Round 30 synchronizes the frame, overlay, and capture paths with the granted Phase 2/4/5 contracts.
The §5 interface region changed and requires fresh verification.

### 0.34 Round-31 fix-up

Round 31 corrects the §1.1 scope qualifier for the Phase 2 capture point to name the actual
outstanding gates (R7-8 package placement and the Phase 3/R7-9 reverification condition) instead of
the granted §5.4 projections, and adds the public resize-observation row
(`ResizeObservationPort` / `ResizeObservation` / `ResizeObservationResult` /
`ResizeLifecycleRejection`, consumed by `mod.glue.frame`) to the §5.1 exact exposed-contract table.
The §5 interface region changed and requires fresh verification.

### 0.35 Round-32 fix-up

Round 32 re-points stale provenance coordinates against RESEARCH and the Phase 2–6 docs in §0.3,
§3.5–§3.7, §4.1–§4.2, §4.10, §8.3, §10, and §11.5, and corrects the closing status's literal-PASS
accounting. It declares `AnaglyphEye` and `BlendStateValue` in §5.1, reshapes the three
`UniformSignal` variants to Phase 6's binding sample shapes with explicit identity/value sources,
and restates the `UniformSignalBridge` mapping row. The §5 interface region changed and requires
fresh verification.

### 0.36 Round-32 notes fix-up

Round 32's three recorded notes are now applied. §0.3 item 2 and the §3.5 `clouds` row cite
RESEARCH Appendix F.1 as `docs/research/v1/RESEARCH.md:1442`–`:1448`, so the operative
video-setting-precedence sentence at `:1448` falls inside the range. §3.2's Phase 4 provenance
points at the actual prohibition (`docs/phase4/v1/PHASE_4_DOC.md:1579`–`:1580`) instead of the
alpha/blend-lock prose. §4.12's bare "§4.13" is qualified as Phase 8's §4.13 ledger, where the
eight hook-health rows live. All four are citation and wording repairs outside §5; this round does
not change the interface region, and the fresh verification owed by §0.35 remains outstanding.

### 0.37 Round-33 fix-up

Round 33 corrects the H-TERRAIN-02 v0.5 depth-copy gate, aligns Phase 3 ID-mapping consumption to
schema-v4, re-points the Phase 3 §5.1 binding citation, and repairs the §3.5 Phase 3 flag and
precipitation provenance coordinates. The §5 interface region changed and requires fresh verification.

### 0.38 Round-34 fix-up

Round 34 removes the false freshness clause on the consumed Phase 3 exposure in §5.2 and states the
document's actual posture, and synchronizes the header pointer and closing status with the Round-33
fix-up. The §5 interface region changed and requires fresh verification.

### 0.39 Round-35 fix-up

Round 35 re-points the §5.4 and §11.5 Phase 8 grant gates from the stale §0.25 round-twenty-two
condition to the revision-agnostic condition already used in §11.3 and the closing status. The §5
interface region changed and requires fresh verification.

### 0.40 Coordinated shared-unit rebuild

The maintainer authorized an architecture-only rebuild of the Phase 4/5/7/13 documents.
RC3 remains this document's governing revision. Review 36 returned literal PASS, zero blocking
and corrections, `Interface changed: no` (`docs/phase7/reviews/PHASE_7_REVIEW_36.md:107–123`);
that clears the historical Phase 7 gate, not this changed contract. Earlier addenda remain history;
this addendum supersedes their obsolete runtime-close and publication-order descriptions.

Additional reads for this rebuild: the entire owned document and Review 36; the approved shared-unit
plan; RC3 §§G1/G5.3/G9/G11.4/G12 and Phase 7 assignment; Phase 3 public load/materialization/type,
fingerprint and texture contracts (`docs/phase3/v1/PHASE_3_DOC.md:387–405`, `:532–666`,
`:749–750`, `:1389–1410`, `:1422–1520`); Phase 6 factory, sampler and retirement rules
(`docs/phase6/v1/PHASE_6_DOC.md:325–357`, `:970–1097`, `:1342–1443`); Phase 8 planning,
invocation and R8-2 (`docs/phase8/v1/PHASE_8_DOC.md:225–257`, `:560–607`, `:932–981`,
`:1081–1133`); RESEARCH B.3/F.5 (`docs/research/v1/RESEARCH.md:1228–1255`, `:1482–1492`).
The completed coordinated Phase 4 §2.2 selector/descriptor schemas and §5.1 binding rows were also
read for exact consumer matching; prior numeric Phase 4 pins in active text are refreshed here.
These narrow additional reads resolve the sampler, preprocessing and shadow planning cycles;
they do not adopt another governing revision or grant an unchanged dependency a new API.

Integrated §§1–9/11–12 now use one authenticated selection, Phase 5's sole fixed policy and
physical bind operation, Phase 13's expected-publication lease, and one coherent off-on-failure
transaction. R7-10/11 request Phase 6 resolver injection and permanent retirement; R7-12/13 request
Phase 8 full shared binding and registry-independent planning. Phase 13 R1 requests separate
pre-jcpp option macros. All are explicit implementation gates, not inferred grants.

§5 changed in this coordinated rebuild. Unverified; a fresh whole-document review returning literal PASS is required before verified downstream consumption. v1 retained; no directory roll.

### 0.41 Integration-review driver reconciliation (2026-09-07)

Architecture-only adoption of IR-02–IR-14, IR-17–IR-19, IR-21–IR-22, IR-24–IR-26
and IR-28 driver portions. RC3 governance is unchanged. Historical addenda/review verdicts
describe their former bytes; active §§4/5/11 now consume current owner contracts. Inputs read:
integration finding sections; RESEARCH §§0–1/4.3–4.5/App A.3/F.1; RC3 governance and P6–8
assignments; P1 package/compatibility/non-verbs/bootstrap handoffs; P2 v2 capture §§4.5/5;
P3 current §5 and catalog/materialization/persistence contracts; P6 resolver/retirement;
P8 active §§2/4/5/11; P12 reload matrix/§5; coordinated P9–14 owner handoffs.
P3's IR-24 meaning change requires current schema **17**, including nested ID schema;
the earlier schema-16 grants remain historical, not an upgrade path.
**§5 changed; unverified.** Fresh whole-document owner and consumer verification remains
required. No implementation, tests, validation, source/reference inspection or PASS is claimed.
SSAA execution and non-fullscreen countInstances remain explicitly authority-gated (§11.3).

---

## 1. Scope & boundaries

### 1.1 Owned by Phase 7

Phase 7 owns:

1. the pure-`:engine` frame state machine that coordinates Phases 3–6 into one world frame;
2. transactional Phase 3/4/5/6/9/13 pipeline preparation/publication, shaders-off recovery, coherent
   texture and ID-dependent geometry invalidation, the per-dimension slot cache, and its equality-only
   `PipelineVersion` invalidation signal;
3. exact classic-program dispatch, nested push/pop semantics, the deferred trigger, composite/final
   executor, fullscreen draw policy, and the strict composite guarantee;
4. the bounded internal passthrough pack supplied through Phase 3's `InternalPackSource`;
5. Phase 7's engine-flag behavior (`clouds`, four `backFace.*` flags, `sun`, `moon`, `vignette`,
   `underwaterOverlay`, `rain.depth`, `beacon.beam.depth`, and `frustum.culling`);
6. the dumb-hook bridge and the complete 1.12.2 hook catalog, including explicit deferrals for every
   Appendix-E row outside this phase;
7. display/Framebuffer observation, world/dimension transition detection, and reload safe points;
8. the Phase 2 `/2` dense capture/pose-history protocol, readiness, client host and clean shutdown;
   package placement R7-8 is granted/adopted, while current owner verification and R7-9's
   internal-manifest production gate remain (§5.4); and
9. invocation/registration seams consumed by Phases 8–14 so the later subsystem can join without
   changing vanilla hook coordinates.

### 1.2 Explicit adjacent ownership

- **Owned by Phase 1:** module/build enforcement, the GL facade and LWJGL implementation, bootstrap
  stages, diagnostic primitives, Mixin configuration, and the coexistence registry. Phase 7 requests
  package slots but does not rewrite Phase 1.
- **Owned by Phase 2:** scene validation, capture-plan and run-manifest wire schemas, fixture
  acquisition, image diffing, tiers, and runner-side failure manifests. Phase 7 hosts the client
  agent and frame moment only.
- **Owned by Phase 3:** pack discovery/loading, `PackConfiguration`, dimension-folder semantics,
  options/properties, `ResourceRequirements`, and internal-source validation. Phase 7 supplies bytes
  and consumes immutable results; it never reopens or reparses a pack.
- **Owned by Phase 4:** stage/program definitions, fallback, linked full sampler/uniform metadata,
  opaque select-once credentials, private program handles, state barrier, and registry publication.
- **Owned by Phase 5:** sole fixed sampler-name policy/resolver, candidate/lease/binding protocol,
  physical texture binds, FBO/neutral ownership, sides/flips/clears/depth, resize and pass snapshots.
  Phase 7 owns their moments and exhaustive result/closure handling, never a second bind loop.
- **Owned by Phase 6:** uniform catalogs, sampling/caches and the unchanged three barrier participants;
  adopted R7-10 consumes Phase 5's sole sampler resolver, not another map or participant.
- **Owned by Phase 8:** shadow camera, traversal, content, split depth, PCF, and shadow completion.
  Phase 7 leaves and invokes one slot before main gbuffers drawing.
- **Owned by Phase 9:** registry/mod/tag snapshots, alias/layer tables, hand policy, ID-runtime
  candidate/publication, entity/block-entity/held values and ID tokens. Phase 7 owns v0.1
  hurt/flash color production, coordinated publication, accepted-frame/terminal calls, hook
  coordinates and ordering; Phase 9's later IDs reuse that color scope, never a second writer.
- **Owned by Phase 10:** vertex writes, chunk-build push/pop, attribute pointers, and the final
  chunk-renderer coexistence policy. Appendix-E classes 3–9 are catalogued, not specified here.
- **Owned by Phase 11:** custom-expression evaluation. It enters only through Phase 6's custom
  participant; Phase 7 never installs a fourth barrier participant.
- **Owned by Phase 12:** options GUI, persistence, user-facing selection, reload commands, and
  option mutation. Phase 7 exposes the safe reload controller and lifecycle status.
- **Owned by Phase 13:** source/content/parameterization identities, atlas companions, custom/noise
  uploads and owned-object retirement, immutable candidate publications, atomic lease acquisition,
  preliminary companion macro policy and atlas-size events. Appendix-E classes 10–11 remain its hooks.
- **Owned by Phase 14:** optional asynchronous/modernized GL work and performance changes.
  Phase 7 permits JFR attribution of its synchronous driver, not a public per-pass elapsed API;
  resize publications remain Phase 5-owned. Internal debug aggregates are not a timing contract.

### 1.3 Hard boundaries

The frame driver contains no Minecraft, Forge, Cleanroom, Mixin, LWJGL, raw GL integer, native
buffer, or `ProgramHandle`. Mixins contain no dispatch, fallback, flip, reload, or failure policy;
they translate parameters and delegate. Replacing every hook in §4.10 with a Kirino adapter must
leave the driver and all Phase 3–6 interactions byte-for-byte conceptually unchanged. This is the
Phase 7 expression of D-6 and the explicit Part-(a)/Part-(b) seam required at
`docs/design/v2.0-RC3/DESIGN.md:1935`–`:1939`.

---

## 2. Architecture overview

### 2.1 Placement

```text
:engine
  com.schmaloogium.engine.frame
    lifecycle/     FrameDriver, pipeline transactions, PipelineVersion
    dispatch/      RenderSection, phase table, nested scope stack
    fullscreen/    deferred/composite/final plans and viewport math
    flags/         resolved Phase-7 engine-flag policy
    internalpack/  immutable passthrough-pack manifest
    spi/           loader-neutral FrameRenderPort and later-phase bridges

:mod
  com.schmaloogium.mod.glue.frame
    FrameHookBridge, MinecraftFrameRenderPort, fixed-function matrix/world samplers
  com.schmaloogium.mod.mixin.frame
    the dumb Mixins in §4.10
  com.schmaloogium.mod.compat
    hook-health facts and the Phase-1/Phase-10 compatibility verdict adapter
  com.schmaloogium.mod.conformance
    Phase-2-schema capture-plan reader and CaptureAgent host (granted package)
```

The exact `engine.frame`, `mod.glue.frame`, `mod.mixin.frame`, and `mod.conformance`
slots are already granted by Phase 1 §§0.22/0.24 and adopted here, subject to fresh owner
verification. GL uses published Phase 1/5 operations; vanilla-only state/FBO operations use
the loader-neutral `FrameRenderPort` implemented in `mod.glue`, never native calls in engine.

### 2.2 Binding public shape

`FrameDriver` implements the `FrameHookSink` published in §5.1; that is the sole hook-facing engine
surface. Its exact operations are `open`, `beforeFirstClear`, `afterFirstClear`,
`captureMainCamera`, `afterTerrainSetup`, balanced `enter`/`exit`, `finish`, and `abort`. All return
closed result algebras. `FrameToken` and `ScopeToken` are opaque credentials rather than caller-made
frame IDs. The optional v0.3 `IdDynamicsFrameSlot` is a construction dependency of the driver, not
a new vanilla hook surface; it is paired atomically with the Phase 9 publisher and receives only
accepted-frame and terminal-reset calls.

```java
public interface FrameHookSink {
    FrameOpenResult open(FrameBeginSignal signal);
    FrameStepResult beforeFirstClear(FrameToken token);
    FrameStepResult afterFirstClear(FrameToken token, MainDepthPreparation depth);
    FrameStepResult captureMainCamera(FrameToken token, CameraSnapshot camera);
    FrameStepResult afterTerrainSetup(FrameToken token);
    ScopeOpenResult enter(FrameToken token, RenderSection section);
    ScopeCloseResult exit(FrameToken token, ScopeToken scope);
    FrameFinishResult finish(FrameToken token, FrameExitKind exitKind);
    FrameAbortResult abort(FrameToken token, FrameAbortReason reason);
}

public enum RenderSection {
    SKY_BASIC, SKY_TEXTURED, TERRAIN_SOLID, TERRAIN_CUTOUT_MIPPED, TERRAIN_CUTOUT,
    DAMAGED_BLOCK, ENTITIES, ENTITIES_GLOWING, BLOCK_ENTITIES, BEACON_BEAM,
    ARMOR_GLINT, SPIDER_EYES, LEASH, PARTICLES_LIT, PARTICLES_UNLIT, WORLD_BORDER,
    CLOUDS, WEATHER, TERRAIN_TRANSLUCENT, HAND_SOLID, HAND_TRANSLUCENT,
    FIRST_PERSON_OVERLAY
}
```

`RenderSection` is engine vocabulary for vanilla moments, not a replacement for pack-facing program
names or Phase 4's `StageId`/`StageBand`. The immutable `PhaseDispatchTable` maps each value to an
exact `ProgramSlotId` and contained Phase-4 `StageStep`; pack-facing names remain verbatim.

`FrameRenderPort` is the single loader-neutral platform surface. It snapshots/normalizes/restores
vanilla-visible state, binds a typed Phase 5 draw target, and executes immutable fullscreen draws.
World, extent, and camera data enter as copied `FrameBeginSignal`/`CameraSnapshot` values; capture
and shutdown remain `mod.conformance` services called at the published finalization notification.
No raw GL name or Minecraft object crosses into `:engine`.

The load-bearing composition shapes below are incorporated unchanged into §5.1:

```java
record ActivePipeline(
    PublishedRegistry registry, PublishedBufferEstate buffers, UniformRuntime uniforms,
    TextureSystem textureOwner, TexturePublication texturePublication,
    TextureLeaseSource textureLeases, PipelineVersion version,
    Optional<ShadowPassPublication> shadowPublication, ShadowInvocationSlot shadowSlot,
    Optional<ShadowPlan> shadowPlan, Optional<PublishedIdRuntime> ids,
    Optional<IdDynamicsFrameSlot> idDynamics) {}
record SelectedPassContext(PassDescriptor pass, ProgramBindingSelection selection,
    BarrierContext activationContext) {}
```

Both records are internal, immutable compositions, not constructors of dependency credentials.
The texture owner is private to composition; hooks/Phase 8 receive only publication and lease source.
The exact §5.1 ShadowInvocationContext field order includes the appended selection/context/
texture publication/lease source and is part of this §2 public shape. Phase 4's opaque selection
exposes registryGeneration, registryFingerprint, requested, effectiveDescriptor, effectiveStage,
actualBand and originatingContext; no public constructor, ProgramHandle or raw binding exists.

### 2.3 Major relationships

```text
mod.mixin handlers ── dumb call ──> FrameHookBridge (:mod glue)
                                      │ immutable values/callbacks
                                      v
                         FrameDriver / FrameHookSink (:engine)
                         │         │          │             │
                Phase 4 barrier   Phase 5    Phase 6       Phase 9 runtime
                + stage view      estate     runtime/sink  aliases/dynamics
                         │         │          │             │
                         └──── coordinated frame/publication token ─────┘
                                      │
                         FrameRenderPort (:mod glue)
                                      │
                         Minecraft / GlStateManager / facade
```

The driver never asks a hook which program to use. It obtains the issued context and calls Phase 4
`select` once, passes that same credential through Phase 5 snapshot and Phase 13 lease acquisition,
then Phase 5 physically binds before Phase 4 activates the retained private binding (§4.4).
Phase 13 joins this relationship as `FrameDriver -> active TextureLeaseSource -> Phase5
textureBindings -> Phase4.activate`; the private texture owner receives only transaction-bound
source/resize/event inputs and retires before borrowed services disappear.

### 2.4 Invariants

1. At most one shader frame and one Phase-5 pass snapshot are open on the render thread.
2. `UniformRuntime.beginFrame` happens before any active-frame resize, replacement, or clear.
3. Current gbuffer matrices are captured exactly once, after `setupCameraTransform`, never merely
   because the first clear returned.
4. `FrameBarrierContexts.beginFrame()` is called once per shader frame; only its issued activation
   and release contexts are used.
5. A nested phase closes its parent's binding and completes its drawn gbuffers pass, retaining the
   original selection/context logically; pop reacquires physical sides/lease, never reselects.
6. A pass snapshot alone is not draw permission: required Phase 5 `Bound` plus successful Phase 4
   activation is required. Only `Completed` advances flips; undrawn passes use `discardPass`.
7. Deferred/composite families traverse Phase 4's sparse population; no index hole terminates a
   family and no hardcoded count is authoritative.
8. `finish` is idempotent and exactly-once per opened frame. Normal TAIL and the outer `finally`
   race only through the same token.
9. A healthy early exit still executes composite/final. A protocol/backend failure aborts instead;
   it never draws from an untrusted snapshot merely to satisfy the guarantee.
10. No draw occurs between Phase 4 acceptance and matching Phase 5, Phase 13 and installed Phase 9
    publications, resize registration and geometry invalidation; only the coherent Active tuple
    admits frames.
11. Every off/replacement/teardown invalidates Phase 4 activity tokens and increments the Phase-7
    equality-only `PipelineVersion` once.
12. Shaders-off means no Phase 4/5 shader draw, no engine FBO interception, and the unmodified
    Minecraft framebuffer path remains reachable.
13. A Phase 9 generation change is followed by one successful alias/layer-dependent geometry
    invalidation before another shader frame; stale chunk products are never drawn under a new ID
    runtime.
14. Phase 9 per-draw entity/block-entity and Phase 7 color stacks are neutralized before Phase 4 fixed-
    function release on every normal, early, thrown, abort, off, replacement, and teardown path.
15. A selector is handle-free and privately issued, not a descriptor-shaped token. Its context,
    registry generation/fingerprint, provider/stage/band and complete layout remain identical through
    candidate selection, object binds, activation and Phase 6 callbacks.
16. After `Bound`, finally closes only the binding snapshot; every non-transferring result or throw
    leaves an acquired overlay lease with the caller. Invalidating use never silently closes it.
17. A rebuild failure selects shaders-off, not the previous partially retired/restitched pipeline.

---

## 3. Contract conformance map

### 3.1 RESEARCH §4.4 frame-flow rows

| Contract item | Design element | Provenance |
|---|---|---|
| frame world-state sampling | §4.3 `open` calls Phase 6 first | `[V:observed]` `docs/research/v1/RESEARCH.md:533`–`:537` — “sample world state” and previous snapshots |
| fixed gbuffers texture set | Phase 5 snapshot/fixed table binding in §4.6 | `[V:doc]` `docs/research/v1/RESEARCH.md:538` — “fixed unit map” |
| shadow before main gbuffers | §4.3 shadow slot, then main clear/bind | `[V:observed]` `docs/research/v1/RESEARCH.md:538`–`:540` |
| complete gbuffers phase order | §3.2 + §4.4 dispatch table and hook rows | `[V:observed]` `docs/research/v1/RESEARCH.md:540`–`:542` |
| depthtex1 then deferred before translucents | §4.5 `beforeTranslucent` | `[V:observed]` `docs/research/v1/RESEARCH.md:543`–`:544` |
| water, hand solid, hand translucent/depth scale | §4.4/§4.10 H-HAND | `[V:observed]` `docs/research/v1/RESEARCH.md:544`, `docs/research/v1/RESEARCH.md:560`–`:561` |
| composite ping-pong and final | §4.6 | `[V:observed]` `docs/research/v1/RESEARCH.md:545`–`:546` |
| push/pop around leash/glint | `NestedRenderScopeStack` in §4.4 | `[V:observed]` `docs/research/v1/RESEARCH.md:551`–`:552` |
| precise depth copies and center depth | §4.3/§4.5; Phase 6 samples prior center depth at frame begin | `[V:observed]` `docs/research/v1/RESEARCH.md:557`–`:559` |
| identity fullscreen state, mipmaps, scale, instances | `FullscreenPassExecutor` §4.6 | `[V:observed]` `docs/research/v1/RESEARCH.md:562`–`:564` |
| composite guarantee | outer render wrapper + idempotent `finish` | `[V:observed]` `docs/research/v1/RESEARCH.md:566` — “guarantees composites run even on early exits” |
| `(internal)` is a real pack and `Off` is no shaders | §4.7 separates `PackSelection.Internal` from `PackSelection.Off` | `[V:doc]` `docs/research/v1/RESEARCH.md:478`–`:481` |
| dimension cache and all uninit/reinit triggers | §4.8 per-`DimensionKey` cache plus pack-, option-, resource-, dimension-, resolution-, registry-remap-, and ID-source/catalog/policy-triggered safe-point rebuild | `[V:doc]` `docs/research/v1/RESEARCH.md:483`–`:490`; assigned Pintonium mechanism, not contract, at `docs/design/v2.0-RC3/DESIGN.md:1853`–`:1855`; Phase 9 R9-2 adds the identity triggers |

### 3.2 Appendix A.1: every program row

| Appendix-A.1 slot/family | Phase-7 execution route | Provenance |
|---|---|---|
| `<none>` | GUI/menu stays fixed-function; `FIRST_PERSON_OVERLAY` uses Phase 5's draw-buffers-none lease | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `shadow`, `shadow_solid`, `shadow_cutout` | Phase 8 slot; Phase 4's `shadowPass=true` force-selection remains authoritative | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_basic` | leash and selection box | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_textured` | unlit particles | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_textured_lit` | lit particles and world border | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_skybasic` | sky, horizon, stars, void base scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_skytextured` | nested sun/moon scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_clouds` | clouds | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_terrain`, `gbuffers_terrain_solid`, `gbuffers_terrain_cutout_mip`, `gbuffers_terrain_cutout` | the three terrain-layer moments request the exact specific slot; Phase 4 resolves its G6 fallback to `gbuffers_terrain` | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_damagedblock` | damaged-block texture scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_block` | block-entity scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_beaconbeam` | beacon and crystal-beam scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_item` | retained/dormant; no 1.12.2 dispatch claim | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_entities` | entity scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_entities_glowing` | glowing-outline subsection | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_armor_glint` | balanced nested armor-glint scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_spidereyes` | balanced eye-layer scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_hand` | solid arm/item scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_weather` | weather, with PRE_WEATHER copy at v0.5 | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `deferred_pre`, `deferred…deferred15` | virtual pre transition request, then sparse pass traversal at translucent HEAD | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_water` | translucent terrain after deferred | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `gbuffers_hand_water` | translucent hand/item scope | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `composite_pre`, `composite…composite15` | virtual pre transition request, then sparse frame-end traversal | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |
| `final` | final to `PassDrawTarget.Screen.INSTANCE`; absent/fixed terminal performs the required passthrough copy | `[V:doc]` `docs/research/v1/RESEARCH.md:1101`–`:1141` |

The names and fallbacks are not re-resolved here. Phase 4 publishes the entire mapping and warns
Phase 7 not to overlay requested-slot state on the effective provider
(`docs/phase4/v1/PHASE_4_DOC.md:1803–1804`: “Phase 7 must not re-resolve backup chains
or overlay requested-slot state on the effective provider”).

### 3.3 RESEARCH §7.1 hook-needs 1–11

| Need | Disposition | Provenance |
|---:|---|---|
| 1 frame begin/end | H-FRAME-01…07 plus outer-finally H-FRAME-00; v0.3 H9-HELD-01 follows accepted begin and `IdDynamicsFrameSlot.resetFrame` precedes terminal release | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 2 camera matrices | H-FRAME-04 after `setupCameraTransform`, once | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 3 shadow invocation | H-FRAME-05; content deferred to Phase 8/v0.2 | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 4 all per-phase switches | H-SKY, H-TERRAIN, H-DAMAGE, H-LINE, H-ENTITY, H-LEASH, H-GLINT, H-EYES, H-BEAM, H-PARTICLE, H-CLOUD, H-WEATHER, H-BORDER, H-HAND, and H-OVERLAY-01 | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 5 depth copies + center depth | H-WEATHER and H-TERRAIN-02; center depth is Phase 6 frame-begin sampling | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 6 deferred + composite/final | H-TERRAIN-02 and `finish`/H-FRAME-06/00 | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 7 vertex interception | explicit Phase 10 deferrals E5–E8 in §4.10.8 | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 8 chunk-build push/pop | explicit Phase 10 deferrals E3/E4/E9 | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 9 atlas companions | explicit Phase 13 deferrals E10/E11; `TextureStitchEvent` is preferred where sufficient | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 10 display/framebuffer resize | H-RESIZE rows; OQ-3 decides context-layer reach | `[D-5]` `docs/research/v1/RESEARCH.md:796`–`:819` |
| 11 GUI/reload | GUI/screens/F3+R/command owned by P12; P7 implements exact ReloadCoordinator and internal controller drain, no vanilla GUI mixin | `[D-5]` RESEARCH §7.1; §5.1 |

This is zero-unmapped coverage of `docs/research/v1/RESEARCH.md:802`–`:819`.

### 3.4 Seven-row Pintonium timeline disposition

| Reference row | Disposition and contract check |
|---:|---|
| 1 `renderWorldPass` HEAD | **Adopt moment** for dimension observation, phase reset, and Phase 6 begin. Phase 3 remains the only dimension/source truth. `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:71`–`:93]` (`D-P7-3`) |
| 2 before first clear | **Adopt moment** for cache-coherent vanilla state normalization, not policy. `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:95`–`:100]` |
| 3 after first clear | **Split/deviate.** The reference combines capture/begin at this point (`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:102`–`:113`). Buffer preparation may remain after the clear, but matrix capture moves after the subsequent `setupCameraTransform` call because the Cleanroom patch visibly orders clear first (`reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:175`–`:179`). RESEARCH requires capture after camera setup. `D-P7-4`. |
| 4 after `setupTerrain` | **Adopt moment** for the Phase 8 slot, then rebind/clear main. `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:116`–`:121]` |
| 5 translucent layer HEAD | **Adopt moment**, but target the actual four-argument world-render overload. `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinRenderGlobal_Shaders.java:23`–`:33]`; RESEARCH confirmation at `docs/research/v1/RESEARCH.md:543`–`:544`. |
| 6 around first-person item | **Adopt balanced hand scope only. Reject the reference's depthtex2/center-depth timing:** RESEARCH puts depthtex2 before weather and Phase 6 owns center-depth at frame begin. `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:124`–`:142]`; `D-P7-5`. |
| 7 `renderWorldPass` TAIL | **Adopt normal-return fast path and strengthen** with H-FRAME-00 `finally`. The implementation uses a TAIL only (`reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java:144`–`:149`), and PD records no early-exit handling (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:195`–`:197`). `D-P7-2`. |

Sky, weather, and clouds have **no working reference**. PD states that absence directly at
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:199`–`:203`; their catalog rows are marked
high-risk and are front-loaded in v0.1 assembly.

### 3.5 Phase-7 engine flags

| Phase 3 field | Exact owner behavior | Provenance |
|---|---|---|
| `clouds` | resolve `DEFAULT/FAST/FANCY/OFF` once per publication; a corresponding explicit video setting wins; OFF cancels, FAST/FANCY is returned to vanilla's cloud-mode query | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:697` and precedence at `docs/research/v1/RESEARCH.md:1442`–`:1448`; runtime mapping `[D-P7-11]` |
| `backFaceSolid/CutoutMipped/Cutout/Translucent` | TRUE temporarily disables culling for exactly that terrain-layer scope; FALSE/DEFAULT preserves vanilla state; scope exit restores | `[V:doc]` fields/owner at `docs/phase3/v1/PHASE_3_DOC.md:706`–`:709`; runtime mapping `[D-P7-11]` |
| `underwaterOverlay` | FALSE cancels only the WATER `RenderBlockOverlayEvent`; TRUE/DEFAULT preserves vanilla | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:702`; runtime mapping `[D-P7-11]` |
| `sun`, `moon` | FALSE suppresses only the corresponding textured sky draw; TRUE/DEFAULT preserves vanilla | `[V:doc]` fields/owner at `docs/phase3/v1/PHASE_3_DOC.md:703`–`:704`; runtime mapping `[D-P7-11]` |
| `vignette` | FALSE cancels only `RenderGameOverlayEvent.Pre(VIGNETTE)`; TRUE/DEFAULT preserves vanilla | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:705`; runtime mapping `[D-P7-11]` |
| `rainDepth` | FALSE disables depth test only for the weather scope and restores it; TRUE/DEFAULT preserves vanilla | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:710`; runtime mapping `[D-P7-11]` |
| `beaconBeamDepth` | FALSE disables depth test only for beacon/crystal beam scopes and restores it; TRUE/DEFAULT preserves vanilla | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:711`; runtime mapping `[D-P7-11]` |
| `frustumCulling` | FALSE makes only the catalogued world-render frustum queries return visible; TRUE/DEFAULT delegates unchanged | `[V:doc]` field/owner at `docs/phase3/v1/PHASE_3_DOC.md:713`; runtime mapping `[D-P7-11]` |

All are tri-state-preserving; unset never silently becomes TRUE. The precipitation helper retains the
Phase 3 handoff exactly: none for `PPT_NONE`, rain at temperature `>= 0.15`, snow below
(`docs/phase3/v1/PHASE_3_DOC.md:757`).

### 3.6 Input contradictions and binding rulings

| Conflict/gap | Ruling in this document |
|---|---|
| Appendix E lists the one-argument `func_174982_a` overload, while the actual world loop calls the four-argument overload (`reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:200`–`:206`). MCP confirms `func_174977_a (...;DILnet/minecraft/entity/Entity;)I`. | H-TERRAIN targets `func_174977_a`; the one-argument method remains a validated but non-world-loop overload. Requested upstream correction U7-1. |
| RC3/Pintonium row 3 calls the ordinal-zero-clear site a matrix-capture moment, but the Cleanroom patch orders that clear before camera setup (`reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:175`–`:179`). | RESEARCH and Phase 6 win: split the hook and capture after `setupCameraTransform`. |
| Phase 5's depth protocol must follow RESEARCH's world order: weather before translucent (`docs/research/v1/RESEARCH.md:542`–`:559`). | The granted contract now orders `FRAME_BEGUN -> PRE_WEATHER -> PRE_TRANSLUCENT -> FRAME_COMMITTED`; the driver calls those moments in that order. |
| Phase 4 makes `*_pre` virtual with no program (`docs/phase4/v1/PHASE_4_DOC.md:1783`: “no resolved program, and no shader draw”). | Phase 5's granted `applyVirtualTransition` consumes the exact planned descriptor without a program; Phase 7 never fabricates one. |
| Manifest facts have distinct owners. | Phase 7 copies Phase 4 `resolutions()`, Phase 5 `resources()`, Phase 2's replay-aware GL result, and the capture-plan provenance fields verbatim; it performs no parallel introspection. |
| shared units require program-specific full sampler shapes | Phase 4 authenticates the effective layout, Phase 13 retains all candidates and Phase 5 filters before canonical-ordinal ranking; §§4.4/4.6/8.4, D-P7-14. `[V:doc]` `docs/research/v1/RESEARCH.md:1484–1490` |
| fixed App B.3 map is one authority | inject Phase 5 pure policy before compile and adopted resolver before Phase 6 construction; no local map/free-unit search | RESEARCH App B.3; D-P7-14 |
| coherent texture publication and preprocessing inputs | preliminary macros before load, Phase 13 build after actual estate acceptance, failures off; §§4.1/5.3/8.4. `[D-P7-15]`; jcpp precedes configuration at `docs/phase3/v1/PHASE_3_DOC.md:644–666` |

### 3.7 OptiFine replacement-list cross-check

An exhaustive read of `reference-src/schlorbium-HD_U_G6_pre1/files.txt` produced this result rather
than the blanket corroboration asserted by RESEARCH §7.1
(`docs/research/v1/RESEARCH.md:823`–`:825`):

| Set | Result |
|---|---|
| App E rows 1–16 and 18 | all 17 classes are present; representative coordinates are `reference-src/schlorbium-HD_U_G6_pre1/files.txt:20`, `reference-src/schlorbium-HD_U_G6_pre1/files.txt:25`, `reference-src/schlorbium-HD_U_G6_pre1/files.txt:27`, and `reference-src/schlorbium-HD_U_G6_pre1/files.txt:32` for `WorldClient`, `BufferBuilder`, `EntityRenderer`, and `RenderGlobal` |
| App E row 17, `net.minecraft.client.shader.Framebuffer` | **absent** from the complete 112-line list; this contradicts the “every class” claim at `docs/research/v1/RESEARCH.md:823`–`:825` |
| added supporting hook targets | `GuiMainMenu`, `OpenGlHelper`, `GameSettings`, `RenderLiving`, `RenderLivingBase`, armor/eye layers, and `TileEntityBeaconRenderer` are present; representative coordinates are `reference-src/schlorbium-HD_U_G6_pre1/files.txt:11`, `reference-src/schlorbium-HD_U_G6_pre1/files.txt:31`, `reference-src/schlorbium-HD_U_G6_pre1/files.txt:57`–`:69`, `reference-src/schlorbium-HD_U_G6_pre1/files.txt:82`, and `reference-src/schlorbium-HD_U_G6_pre1/files.txt:93` |
| added `Minecraft` resize and `RenderDragon` beam targets | **absent**; H-RESIZE and H-BEAM-02 therefore have MCP/Cleanroom/Pintonium evidence but no `files.txt` corroboration |

Absence from an OptiFine replacement digest does not disprove a vanilla call site. The exact hooks
remain subject to OQ-4 and their health groups. U7-3 requests that RESEARCH narrow its independent-
corroboration statement.

---

## 4. Detailed design

### 4.1 Pipeline lifecycle and publication

`DimensionPipelineManager` is the render-thread composition root. It owns configuration/dimension,
caller-owned candidates, the retained Phase 6 runtime under adopted R7-11, an inactive/active
Phase 13 owner with its registrations, optional Phase 8 publication/bridge and Phase 9 composition,
and one `PipelineVersion`. Publications are non-owning views; no raw program, texture, registry
object or alias table is owned by Phase 7. A failed rebuild always goes shaders-off.

Full rebuild lifecycle (FULL/REPUBLISH or explicitly required runtime rebuild, not resource NONE):

```text
OFF/ACTIVE -> QUIESCING (admission closed; drain frame/bindings/shadow; freeze epochs)
  -> LOADING (preliminary macros before Phase 3)
  -> PREPARING (gated shadow plan; inactive texture owner; uniform; ID; Phase 4/5 candidates)
  -> READY_TO_PUBLISH (barrier and complete identity validation)
  -> Phase 4 Accepted -> adopt actual registry generation -> Phase 5 accepted
  -> Phase 13 build/identity/resize registration -> Phase 9 + geometry invalidation
  -> atomic ACTIVE + one PipelineVersion increment -> admit frames
Any failure -> close only caller-owned candidates; retire texture owner; close Phase 8;
  reset/deactivate Phase 9; Phase 5 off; Phase 4 ShadersOff input;
  retire uniform runtimes under R7-11 -> OFF + one PipelineVersion increment
AwaitingMainDepth -> gated preparation, vanilla only; no partial Active tuple
```

The exact full-rebuild transaction below is incorporated by §5.3. The selector in step1
diverts NONE to §5.1's retained-configuration refresh/no-op branch; it does **not** fall
through steps2–10 or invent a P3 load/P4 replacement. Any owner-required runtime rebuild
from retained configuration follows only the applicable owner operations specified in §5.1.

1. Stop admission; finish/abort the old frame, drain binding/shadow and chunk-worker uses,
   and freeze intended selection, world, resource, hook and option inputs. For FULL discover
   then load; REPUBLISH loads the unchanged current selection without discovery; resource-only
   NONE follows §5.1's refresh branch and retains the configuration. Before each non-Off load,
   decode independent `normalMapEnabled`/`specularMapEnabled` preferences and call Phase 13
   `preliminaryMacroState(PreliminaryCompanionDemand(packActive,fixedUnitCapabilityAvailable,
   normalMapEnabled,specularMapEnabled))`. Each output is active AND capable AND its own
   preference. Supply the required Phase 3 `CompanionOptionMacros(normalMap,specularMap)`
   immediately after `engineOptions`; no old-load fallback, missing-pair default or late
   linked-demand decision exists. Accept only current schema 17 and equal nested ID schema.
   Catalog-bound options, materialized sources, program evaluation and macros all come from
   that same load. Off/load failure/rebuild failure goes off, never back to an old atlas.
2. Adopt R7-13's `ShadowPlanInput(ShadowPolicy,ShadowHookHealth)` before provider construction;
   real shadow remains `NotInstalled` until fresh owner gates close. Create inactive
   `TextureSystemFactory.create(GLDevice,DiagnosticReporter)` with atlasSize Unknown and no
   allocation, then provider/runtime using the current authoritative registry generation and
   adopted R7-10 `FixedSamplerPolicies.resolver()` immediately after uniform configuration.
   The same ready shadow celestial policy or explicit absence reaches that provider.
   No new adapter delivers to the old runtime.
3. Preserve installed Phase 9's frozen input/pure candidate build and validate every configuration/
   registry/tag/mod-source/hand-policy identity. Obtain `FixedSamplerPolicies.appB3()` before
   registry/estate creation; compile Phase 4 with that samplerPolicy immediately after the runtime's
   existing macroContribution. Plan/create Phase 5 from the exact detached registry view, policy
   fingerprint, capabilities, sizing and MainDepthSource. `AwaitingMainDepth` publishes nothing.
4. Compose Phase 4 with exactly that runtime's sampler, built-in and custom participants. Construct
   optional Phase 8 only after owner verification and with the final new registry fingerprint in
   adopted `ShadowPassFactory.create` immediately after plan. Revalidate intended configuration,
   world, resource, hook and all candidate identities. Precommit failure closes caller-owned
   candidates and takes the old composition off; it cannot resume a partially retired pipeline.
5. At the quiescent boundary close old Phase 8 and old texture-event/resize registrations, then retire
   the old texture owner after draw/binding drain (outstanding leases defer deletion, never drawing).
   Reset old Phase 9 per-draw scopes before fixed-function release. Publish Phase 4
   `RegistryPublication.Ready` with its issued release context. Handle `Accepted`, `Rejected`
   (candidate ownership retained) and `RecoveredOff` (already-off RESULT) distinctly. Retire replaced
   Phase 6 under adopted R7-11 after old barrier invalidation; no invented runtime `close()`.
6. After Accepted reacquire the publisher's actual generation and call new
   `UniformRuntime.adoptRegistryGeneration(generation,PACK_REPLACEMENT)`.
   `ADOPTED|ALREADY_CURRENT` proceed; `REJECTED_RETIRED_GENERATION` compensates before any new
   event, participant, beginFrame or shadow use.
7. Publish Phase 5 and handle its full synchronous resize-delivery result, preserving deliveredCount
   and prioritized reason. Use only actual `PublishedBufferEstate` generation/sizing.
   `ConsumerFailed` is already installed off, not a caller-owned ready estate. Admission stays closed.
8. Build Phase 13 against the exact accepted registry view/fingerprint/generation, configuration,
   actual estate generation, frozen `TextureSourceCatalog`, matching `TextureBuildSources` and
   current resourceReloadEpoch. `TextureBuildRequest(planRequest,sources)` verifies decoded upload
   payloads/borrowed handles against catalog source/content/target/parameter identities before GL.
   Accept Ready only after exact identity checks, then register its resize consumer against accepted
   sizing/generation. Registration rejection/failure compensates. Install hook adapters only in the
   pending composition. Build after estate acceptance avoids guessed generations; any failure here
   compensates the already accepted publishers off.
9. Publish installed Phase 9 after Phase 13, then synchronously complete
   `IdDependentGeometryInvalidator` after a generation change. Atomically install ActivePipeline only
   when publications, registrations, required adapters and optional slots agree. Increment
   PipelineVersion once for this final outcome and admit frames. No intervening draw/shadow is legal.
10. Failure after any acceptance retires accepted texture state through its owner, closes only
    still-caller-owned candidates and Phase 8, resets/deactivates appropriate Phase 9, calls
    `Phase5.publishOff(BufferFailure)`, then
    `Phase4.publish(RegistryPublication.ShadersOff(cause),issuedReleaseContext)` and handles its
    actual result. Preserve required full geometry invalidation after an ID invalidation failure.
    Retire candidate/replaced runtimes only under R7-11. No accepted object is caller-closed, old
    registry revived or new registry/estate paired to old texture objects. Shutdown retirement is
    before Phase 4 teardown and before borrowed services disappear.

`ActivePipeline` contains accepted `PublishedRegistry`, `PublishedBufferEstate`, retained
`UniformRuntime`, private Phase 13 owner, non-owning `TexturePublication` and `TextureLeaseSource`,
`PipelineVersion`, optional Phase 8 publication/slot/plan identity, and the paired optional
`PublishedIdRuntime`/`IdDynamicsFrameSlot`. Its identities include configuration, world, hooks,
registry generation/fingerprint, actual estate generation, fixed-policy fingerprint and resource
epoch. Hooks borrow this tuple once for the frame; no global texture-publication lookup assembles
it piecemeal. Phase 13 is a downstream v0.5 slot, not a retroactive declared dependency. Before
that milestone an explicit empty publication still follows this exact selection/lease protocol;
empty candidates do not satisfy required v0.5 custom/PBR functionality.

#### Per-dimension cache and version counter

The required Pintonium shape is adopted as a mechanism, not as dimension semantics
(`D-P7-3`). `DimensionPipelineCache` is a deterministic map from Phase 3 `DimensionKey` to:

```text
Disabled | PlanOnly(configuration + registry/mod/tag/policy + dimension + source/resource-epoch plans)
         | ReadyUnpublished(caller-owned Phase 4/5/9 candidates + inactive Phase 13 owner)
         | Active(non-owning Phase 4/5/9/13 identities; current texture lease source)
```

Only one slot can be `Active`. Phase 4/5/9 do not return accepted candidates to the caller when a
publication is replaced, so switching away demotes that slot to `PlanOnly`; Phase 7 does not invent
a reusable live-object lease. Other slots may retain caller-owned, not-yet-published candidates if
they were prepared on the render thread. Pack reload, option change, capability change, or config
fingerprint inequality, registry remap, or ID-source/catalog/policy fingerprint change closes every
unpublished candidate and clears the map. This retains the
per-dimension lookup and invalidation shape observed at
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:110`–`:114` without contradicting Phase 3's
no-base-merge world-folder contract.

Texture content identity includes actual bytes/effective parameters, source/object/resource epochs,
registry and fixed policy. Equal content fingerprints do not confer authority to reuse a different
estate/registry/owner incarnation. Resource reload advances resourceReloadEpoch and invalidates atlas
catalog, animation snapshot, foreign object identity and atlasSize before vanilla replacement.

`PipelineVersion(long value)` increments exactly once after each fully accepted ready publication,
accepted off publication, or forced recovered-off outcome. Consumers compare equality only; it is
not a clock and ordering/subtraction is forbidden. Phase 4 and Phase 5 generations remain their own
authorities, as does Phase 9's runtime generation; Phase 7 never claims the values are equal.

### 4.2 Per-frame state machine and composite guarantee

The per-frame machine is separate from pipeline lifecycle:

```text
IDLE
  -> SAMPLED            Phase 6 beginFrame accepted; Phase 9 held sample accepted;
                        Phase 4 contexts issued
  -> BUFFER_OPEN        Phase 5 refresh/replacement check then beginFrame(Begun)
  -> MATRICES_CAPTURED  current gbuffer matrices captured after camera setup
  -> SHADOW_DONE        Phase 8 slot returns/absent; main estate clear/bind succeeds
  -> GBUFFERS           hook-driven scoped raster draws
  -> DEFERRED_DONE      PRE_TRANSLUCENT copy result handled; deferred family complete
  -> GBUFFERS_TRANS     water/hand/overlay scopes
  -> FINALIZING         composite family then final, exactly once
  -> COMMITTED          Phase 5 commit + Phase 4 fixed-function release
  -> IDLE

Any state from BUFFER_OPEN onward
  -> ABORTING           consume any open snapshot, restore scopes/state
  -> ABORTED            Phase 5 abort result handled; full-clear fact retained
  -> RECOVERING/OFF     on backend failure; otherwise IDLE for the next full-clear frame
```

`open` rejects a second frame, wrong thread, stale Phase 4/5/9/13 composition or resource/policy
pairing, non-world pass or missing world/camera without GL. A duplicate Phase 6 begin may continue;
`REJECTED_STALE_FRAME` or `REJECTED_GENERATION` forbids shader drawing and reacquires the current
publication as required by `docs/phase6/v1/PHASE_6_DOC.md:869`–`:873`.

H-FRAME-06 calls `finish(NORMAL)` on ordinary TAIL. H-FRAME-00 wraps the containing
`renderWorld` invocation and calls `finish(EARLY_RETURN|THROWN)` from `finally`. Both use the
same opaque token and `compare-and-finish`; the loser is a no-op. If the frame is healthy,
`finish` closes/suspends any current gbuffers scope, runs all remaining composite passes and final,
fires the before-present observer, commits Phase 5, calls Phase 9 `PerDrawDynamics.resetFrame` to
neutralize entity/block-entity/color stacks, releases Phase 4 to fixed function, and restores the
vanilla state lease. Abort, thrown, resize, off, replacement, and teardown paths call the same reset
before fixed-function release and before any later shader draw. This is deliberately stronger than
the Pintonium normal-TAIL behavior.

An engine protocol/backend error is not a healthy “early exit.” It calls `abortFrame`, never commits
an unwritten alt side, releases fixed function, and publishes off if Phase 5 returns
`BackendFailed` or Phase 4 returns `ShadersOff`/`FailedSafe`. An exception thrown by vanilla is
preserved after best-effort healthy finalization; Phase 7 catches and contains only its own failures.

### 4.3 Exact frame-begin ordering

The seven reference moments are split into the following normative sequence:

1. **`renderWorldPass` HEAD:** identify world/dimension/frame. For an active unchanged pipeline,
   issue Phase 4 `FrameBarrierContexts` and call Phase 6 `beginFrame`. This rotates previous values,
   samples world/tick/frame state, and reads the **completed prior framebuffer** before Phase 5 is
   touched. New-world epoch reset uses prior dimensions `0,0` and cannot read cross-world depth.
   Only after Phase 6 returns its accepted/duplicate frame result, call the current Phase 9 held-
   hands operation with that exact `FrameBeginSignal`/runtime generation; glue samples both hands
   once, Phase 9 resolves them, and its Phase 6 sink update completes before shadow or any gbuffers
   activation. A missing/rejected held provider writes the typed zero tuple for this frame and
   disables only held delivery; a stale Phase 9 generation rejects the shader frame.
2. **Before ordinal-zero `GlStateManager.clear`:** normalize only vanilla cached state required for
   a known starting point. No engine buffer is cleared here.
3. **After that clear:** sample `BufferRuntimeInputs`; refresh the borrowed main-depth identity; if
   resizing/replacement is required, abandon this shader frame and perform the safe-point rebuild
   before a later frame. Otherwise call Phase 5 `beginFrame`. Buffer construction during initial/off
   preparation has no completed prior framebuffer to sample; the first active frame passes `0,0`.
4. **After the invocation of `setupCameraTransform(FI)V`:** `FrameHookBridge` copies current
   model-view and projection values into `CameraSnapshot` and calls Phase 6
   `captureGbufferMatrices(frameId, modelView, projection)` exactly once. The Phase-6 provider
   adapter has already retained the copied `FrameUniformSample` and its same-sample `skyAngle`
   under this world/frame identity; it never calls Minecraft again to prepare the shadow view.
5. **Around the exact `RenderGlobal.setupTerrain(...)` invocation:** H-FRAME-05 captures the integer
   token actually passed to vanilla, invokes the original exactly once, then constructs
   `ShadowFrameView(worldEpoch,frameId,partialTicks,mainTerrainFrameToken,cameraPosition,skyAngle,
   sunAngle)`. It rejects a missing/mismatched provider sample before opening shadow execution.
   Under R7-12, the driver issues `activation(shadowStep,true)` and calls root-shadow `select`
   exactly once, then opens its execution bridge and passes the exact selection/context plus active
   registry/estate/TexturePublication/TextureLeaseSource to ShadowInvocationContext. Its finally
   closes the execution view before advancing; Phase 8 separately closes transferred bindings.
   Until R7-12/13 and the required owner reviews land, real shadow remains NotInstalled and uses
   existing typed unavailable/neutral-shadow behavior; no old four-row/prior-frame success is used.
6. **After the shadow slot:** bind the main estate, execute Phase 5's one clear plan using the current
   fog RGB, and enter `GBUFFERS`. This order follows RESEARCH's shadow-before-main-clear flow,
   even though the Pintonium implementation clears earlier.
7. Start sky/terrain drawing only after every prior step has a successful closed result.

Display resize, render-quality change, pack reload, and dimension change never rebuild mid-draw.
They mark `QUIESCING`; the current healthy frame finishes when possible, or renders vanilla only,
then replacement happens at the no-draw boundary. The next active frame samples before any further
clear. A main-depth same-extent `Reattached` invalidates snapshots; the driver abandons/reacquires as
Phase 5 requires. `ResizeRequired`/`Failed` enters the off/rebuild path with no shader draw.

### 4.4 Scoped gbuffers routing and push/pop

`PhaseDispatchTable` is immutable per Phase-4 registry fingerprint. Each entry contains the exact
`RenderSection`, requested `ProgramSlotId`, contained gbuffers `StageStep`, and whether the phase is
allowed before/after deferred. It is validated at pipeline preparation against
`StageRegistry.named`; a missing legal slot is left to Phase 4's fixed/skip terminal rather than
removed from the table.

Entering a scope performs:

1. Suspend an open parent by closing its binding in finally and completing its already-drawn
   gbuffers pass under Phase 5's normal no-flip gbuffers semantics. If undrawn, `discardPass` instead.
   Retain the original requested slot, `ProgramBindingSelection` and originating `BarrierContext`
   on the logical scope stack, never its consumed physical snapshot.
2. Get the contained StageStep/PassDescriptor and issued `activation(step,shadowPass)` context;
   call the published barrier `select(requested,context)` exactly once. `Selected(selection)`
   continues; `Skipped` omits only this operation, `StalePublication` rejects this frame and
   `ShadersOff` enters recovery. Force-shadow happens inside select before its sole fallback.
3. Acquire Phase 5 `snapshot(pass,selection)`. Acquired freezes the physical sides, not permission
   to draw. Bind its draw target/attachments through the existing target operation.
4. Call the active tuple's `TextureLeaseSource.lease(active.texturePublication.id(),selection)`.
   `Acquired(lease)` continues; `Rejected` suppresses this operation and discards the undrawn pass.
   The expected ID is immutable pipeline state, never a two-call current-publication lookup.
5. Call `textureBindings(snapshot,lease,active.texturePublication.id())`. Phase 5 authenticates the
   selector and full publication/estate pairing, resolves all full-shape candidates without GL, then
   physically binds required units in ascending 0–15 order. No Phase 7 texture-row bind loop exists.
   `Bound(binding)` alone transfers lease ownership. `Degraded` or `Rejected` suppresses only this
   program and calls `discardPass`, with no alternate provider, upload, draw or flip.
   `BackendFailed` may follow partial binds: forbid activation/uploads/draw and run frame recovery.
6. Immediately after Bound, call `activate(new UseProgramRequest(selection,context))` with no
   intervening reentrant publication transition. It validates the retained private binding, never
   resolves again. Only `Activated|FixedFunction` permits drawing; `Skipped` discards, while stale,
   off or failed-safe results contain/recover the frame. The three unchanged Phase 6 callbacks see
   `selection.effectiveDescriptor()`; sampler integers follow object binds under adopted R7-10.
7. Apply reversible local cull/depth/matrix state only after activation; on normal drawn completion
   restore it and call `completePass`. Every path has one finally ownership branch: after Bound
   close binding only; before Bound close acquired lease only (including a thrown call).

Pop reacquires pass sides, lease and physical binding using the parent's original selection/context,
then reactivates that same selection. It never selects the effective provider as a new requested slot
or replays old handles. Stale retained selection suppresses its draw; no fallback retry is made.
Snapshot validity intersects open pass/frame/depth/estate/registry epochs, current selection/
publication and its own open state. Completion/abort/reload/neutralization invalidates use without
transferring closure duty. FixedFunctionEmpty has no shader candidates/uploads: non-final uses
vanilla state and BindingPurpose.NONE; final uses the separately typed passthrough in §4.6.

During a Phase 8 traversal, the authenticated `ShadowExecutionView` is the sole guard for bypassing
Phase 7's main-snapshot policies. H-TERRAIN, H-ENTITY, H-CLOUD, and H-FRUSTUM validate that the
current bridge is active for the exact execution identity and slot epoch before returning their
vanilla operation unchanged; they neither open a gbuffers scope nor apply the main frame's
cloud/frustum flags in that dynamic extent. A missing, foreign, stale, wrong-thread, or closed view
does not bypass anything. Under R7-12, Phase 7 issues `activation(shadowStep,true)` and selects root
shadow once before invocation; Phase 8 consumes that exact selection/context. The Phase 4 selector
owns force-shadow/fallback; another mod's direct vanilla call cannot activate this guard.
During that same authenticated shadow extent, P9 ID augmentation uses §5.1's alternate
IdScopeAdmission; it never tries to open a main scope. Nested leash/glint/eyes/beam adapters
likewise preserve the shadow program rather than pushing gbuffers snapshots.

At v0.3 the active pipeline also lends Phase 9's `RenderLayerLookup` to the loader-side block-state
classification adapter. Glue translates the exact state to the current dense ordinal and applies
only the returned `SOLID`/`CUTOUT`/`CUTOUT_MIPPED`/`TRANSLUCENT` decision; absence preserves
vanilla. H-TERRAIN still receives the resulting vanilla `BlockRenderLayer` and never resolves a
name, property, tag, or alias on the draw path. The lookup generation is stamped into the compiled
product, and §5.3's invalidation gate prevents an old classification from reaching a new frame.

### 4.5 Depth-copy and deferred trigger

H-WEATHER calls `beforeWeather` immediately before weather draws. At v0.5 this requests
`copyDepth(PRE_WEATHER, frameId)` and handles all four Phase 5 outcomes: `Copied` continues,
`DuplicateIgnored` continues with the diagnostic, `BackendDegraded` continues with Phase 5's
depthtex0 fallback, and `Rejected` aborts. The granted Phase 5 protocol accepts this as the first
ordered copy point.

At the **four-argument** `RenderGlobal.renderBlockLayer` HEAD for `TRANSLUCENT`, H-TERRAIN-02:

1. closes any opaque gbuffers scope;
2. at v0.5 requests `copyDepth(PRE_TRANSLUCENT, frameId)` and handles its closed result;
3. executes Phase 5's granted `applyVirtualTransition(frameId, deferredPreDescriptor)`;
4. traverses every populated Phase-4 `DEFERRED/BETWEEN_GBUFFERS` raster descriptor in ascending
   sparse order via §4.6;
5. restores the blocks atlas and vanilla blend posture through `FrameRenderPort`; and
6. enters `gbuffers_water` before the original translucent-layer body.

Architecture and deferred pass execution are v0.1; actual copied-depth calls are v0.5 as assigned.
Before v0.5, Phase 5's published fixed binding fallback for unavailable copied-depth targets remains
authoritative; Phase 7 does not bind stale storage.

The reference's hand-time depthtex2 copy is explicitly not used. It would contradict the
before-weather contract. Center-depth is also not read here: Phase 6 already reads the previous
completed image in step 1 of §4.3.

### 4.6 Deferred/composite/final executor

`FullscreenPassExecutor` handles a Phase-4 raster descriptor as one transaction:

1. Obtain issued activation context and `select(requested,context)` once, then acquire
   `snapshot(pass,selection)` as in §4.4. Skipped visits no texture candidates.
2. Pass the snapshot's Phase-5-owned target unchanged to `FrameRenderPort.bind` with the current
   anaglyph mask; Screen carries no engine handle. Preserve preflip/side acquisition ordering.
3. Establish identity model-view and orthographic 0…1 projection, fog/depth/blend off and depth writes
   off under the existing cache-coherent restoration lease.
4. Request Phase 5's logical-buffer mipmap work for the exact frozen readable sides before read;
   it never overwrites custom/foreign override parameterization. Preserve its per-buffer degradation
   and restoration-failure containment, not a new texture mutation owner.
5. Acquire the exact active texture publication lease with the same selection; call Phase 5
   `textureBindings(snapshot,lease,expectedOverlay)`. Bound means all required physical binds
   completed. Handle the same four result branches/local discard/finally ownership as §4.4.
6. Immediately activate `UseProgramRequest(selection,context)`. No row rebinding, second resolve,
   requested-layout overlay or independent Phase 6 program activation is legal.
7. Apply the existing viewport math: mathematical floor for nonnegative normalized origin/extent,
   minimum one pixel for positive scale, bounded by target, default full target.
8. Choose QUADS when supported or the retained four-vertex triangle-strip fallback. Draw exactly
   instanceCount times, updateInstanceId(i) before each, restore zero after the loop. v0.1 count is
   one; the generic loop activates v0.5.
9. Restore viewport/matrix/fog/depth/blend/alpha in finally and close exactly binding-after-Bound or
   lease-before-Bound. Complete only a drawn pass; Completed alone commits flips and post-draw work.
   Binding rejection/degradation, lease rejection or Skipped uses `discardPass`, not abort of
   unrelated programs or false completion. Backend/activation/draw failure takes frame containment.

This schedule accepts only fullscreen deferred/composite/final repetition. Non-fullscreen
gbuffers/shadow countInstances is retained metadata, not an accepted rerender loop (§11.3).
P5 `BufferSizing.superSamplingLevel()` is likewise retained metadata only: level>1 has no accepted
draw/sample/resolve schedule pending IR-12 authority ruling. Do not multiply extents, synthesize
an AA feature, claim single-sample execution honored it, or infer failure semantics while gated.

Virtual deferred_pre/composite_pre bypass select and use VirtualNotApplicable planning metadata;
they execute only `applyVirtualTransition`, never shader binding/upload/draw. Sparse holes are not
visited. FixedFunctionEmpty remains declaration-empty. FINAL's existing colortex0 passthrough uses
`BindingPurpose.FIXED_FUNCTION_PASSTHROUGH`: Phase 5 binds compatible frozen colortex0 at unit0,
without custom inference or sampler integer uploads. Non-final fixed draws use vanilla state.

At frame end:

1. close the last gbuffers scope;
2. execute virtual `composite_pre`;
3. traverse populated `COMPOSITE/FRAME_END` descriptors in ascending sparse order;
4. execute `FINAL/SCREEN` exactly once. If the final shader is absent and Phase 4 resolves fixed
   function, draw the same colortex0 fullscreen passthrough through the fixed-function terminal;
5. after Phase 5 reports final `Completed`, fire `FrameCompletionObserver.beforePresent` and the
   optional CaptureAgent frame grab;
6. commit Phase 5, release Phase 4 to fixed function with the issued release context, and restore
   every remaining platform lease.

The observer is after final pixels exist and before Minecraft presents. `RenderWorldLastEvent` is
not used: the Cleanroom patch dispatches it before hand rendering
(`reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:258`–`:266`),
so it cannot satisfy Phase 2's moment.

### 4.7 Internal default pack and shaders-off

`BuiltInPassthroughPack` implements only Phase 3 `InternalPackSource`, with a stable identity and one
finite, normalized, directory-aware snapshot. It stops immediately at the Phase-3-provided
`PackInputLimits`; bytes are immutable and copied on every `ImmutableBytes.copy()` call.

The clean-room-authored GPL resource set contains:

| Source pair | Semantics |
|---|---|
| `shaders/gbuffers_basic.vsh/.fsh` | compatibility transform + vertex color to colortex0 |
| `shaders/gbuffers_textured.vsh/.fsh` | compatibility transform + base texture × vertex color |
| `shaders/gbuffers_textured_lit.vsh/.fsh` | base texture × lightmap × vertex color |
| `shaders/final.vsh/.fsh` | identity fullscreen input; copy colortex0 to the screen |
| `shaders/shaders.properties` | empty canonical properties surface; no hidden engine override |

All other classic slots intentionally resolve through Phase 4's documented fallback graph. The pack
declares no shadow, copied-depth, custom texture, extended attribute, deferred, composite, geometry,
or modern feature. It therefore tests the backup chain and the final handoff while remaining a true
passthrough. Source text is authored from the published GLSL/pack contract, not the OptiFine
decompile. Every successful snapshot exposes this same immutable corpus; caller limits only accept
or reject it and never truncate it or change its manifest, digest, or identity. Snapshot order, byte
content, manifest identity/paths, and digest are golden-tested.

`PackSelection.Internal` selects this content. `PackSelection.Off` does **not** select it: Phase 3's
successful `Off` result publishes shaders-off and leaves vanilla un-intercepted. A failed external or
internal load likewise goes off; there is no recursive “fallback to internal” loop and no hidden
change of user selection.

### 4.8 Dimension, pack, and resize lifecycle

`FrameHookBridge` is the sole writer of the internal `ActiveWorldIdentityPublication`. Before the
first H-FRAME-01 for a world object it atomically installs `(incremented worldEpoch,
DimensionKey)`; a dimension change atomically replaces that pair before that dimension's first
H-FRAME-01, and `WorldEvent.Unload` clears it before pipeline teardown. Thus no active identity
exists between unload and the next install, and a new world object increments `worldEpoch` even if
the numeric dimension repeats. At H-FRAME-01 the bridge reads one publication snapshot and copies
that snapshot's epoch and dimension, plus `logicalTick`, into `FrameBeginSignal`; it never derives
those identity fields from resize observations. The numeric dimension ID becomes Phase 3
`DimensionKey`; no namespaced/Iris folder rule is introduced.

Dimension change behavior is:

1. mark the active frame vanilla-only and quiesce the old pipeline;
2. drain bindings/shadow, retire texture-event/resize registrations and owner, reset/deactivate
   Phase 9, publish Phase 5/4 off, and retire old Phase 6 under R7-11 exactly as §4.1;
3. look up the new `DimensionKey` in the per-dimension cache;
4. explicit `DimensionMode.DISABLED` publishes off; an explicit override compiles only its `.vsh`/
   `.fsh` set; a missing entry selects base;
5. prepare/publish transactionally per §4.1; and
6. reset Phase 6 with a new world epoch so previous matrices/camera initialize current=current.

Pack/option/quality/resource/remap/source triggers enter one P7-owned queue through the exact
P12 `ReloadCoordinator.submit(ReloadRequest)` bridge (§5.1). Its lifecycle is
`NONE < REPUBLISH < FULL`; world-renderer reload and resource reacquisition are independent OR
flags. Internal world/remap intents use `DriverReloadRequest`, not a second public ReloadRequest.
Ordinary queued work drains on the render thread after the prior frame has finished/aborted
and state is normalized. Destructive vanilla resource replacement instead enters §4.8.1's
synchronous pre-replacement gate; a queued listener notification alone is insufficient.

`RESOURCE_RELOAD` translates to `NONE + resourceReacquire`, never implicit FULL or P3 load.
It closes admission before vanilla resource replacement, drains uses/registrations, resets atlas
size and advances resource epoch, then refreshes P13 resource objects and P9 registry/mod/tag/
policy snapshots from the retained PackConfiguration. Rebuild only affected runtime components,
publish textures before IDs, enforce changed-ID/lighting geometry invalidation, and resume only
with a coherent tuple. A refresh failure goes off. `REGISTRY_REMAP` advances registry snapshot
identity even for equal bytes. Neither reason assumes an unchanged ID snapshot from its name.

Display/main-framebuffer change is observed by H-RESIZE. Phase 7 does not trust logical window size:
it samples the actual main framebuffer extent/version through `MainDepthSource` and Phase 5. On an
active frame, Phase 6 samples the prior depth first; Phase 7 then abandons that shader frame,
rebuilds/publishes at the safe point, and resumes shaders on a later frame. This avoids resizing the
source before center-depth sampling.

All pack/option/resource/dimension/registry replacements quiesce first and invalidate old selectors
and leases for drawing. Resource reload invalidates the Phase 13 atlas/source/animation/foreign
identity before vanilla replacement and advances resourceReloadEpoch even for equal resource paths.
Close the old Phase 13 resize registration before replacement, build with accepted registry/estate,
then register the new publication against accepted sizing/generation. BufferResizeNotice contains
sizing/newGeneration/prioritized reason, not registry fingerprint; use transaction-bound metadata.
A callback outside that prepared path returns FAILED, never independently publishes guessed texture
state. Same-owner size-independent allocations may survive only with equal complete source/
parameterization identity and accounting across every live/retiring publication; equal hash alone
does not allow cross-owner reuse or deletion of an object still referenced by a new publication.

#### 4.8.1 Pre-destructive resource replacement gate

P7 owns the mod-side `ResourceReloadBoundary.invoke(Runnable original)` wrapper around the
entire `SimpleReloadableResourceManager.func_110541_a(Ljava/util/List;)V`
(`reloadResources`) body, H-RESOURCE-01. MCP 1.12.2's mapping and Javadoc confirm that this
method releases current packs, loads replacements, then triggers listeners `[V:mcp]`.
`Minecraft.func_110436_a()V` (`refreshResources`) is a caller, not the only entry to guard.
Mappings establish the target, not successful weaving or runtime ordering; OQ-4 must exercise it.
This boundary is incorporated into §5.1 and consumed by P12 §4.8.3/P13 §4.7.

- The closure captures the original receiver/arguments in mod glue; no resource-manager,
  pack-list or Minecraft object enters engine code. Null, off-client/render-thread, or
  entry from an in-flight shader callback/participant is rejected with `IllegalArgumentException`
  (null) or `IllegalStateException` before calling the original or changing publication state.
  This is an unsafe-entry error, not a successful reload or an automatic retry. Normal reload
  entry must be outside those scopes; OQ-4 verifies that precondition at supported call sites.
  Before engine installation, with no shader-owned state, call original exactly once.
- At outermost installed entry, freeze the pending request slot and merge
  `ReloadRequest(NONE,false,true,RESOURCE_RELOAD)`. Close admission, finish/abort the old frame,
  drain all binding/shadow/restoration uses and P10 workers, then invalidate current-atlas
  evidence, deliver `(0,0)` while P6 is live, retire P13 registrations/acquisition authority
  and advance `resourceReloadEpoch` **before** original executes. Keep borrowed P6 services
  alive. No P3 load or new resource publication occurs during this prelude.
- Invoke original exactly once only after the prelude succeeds. While the body runs, P12
  resource-listener NONE notifications merge into this in-flight resource transaction, not a
  second queued reload. Other new user requests form the next slot. Nested resource-manager
  reload bodies share the outer gate/epoch and execute once each; they never reopen admission,
  drain again or publish intermediate stitch data. P13 Pre/Post events only invalidate/copy
  pending current-epoch data; neither can publish an active texture tuple.
- On normal outer return, all resource listeners have finished. Execute the frozen merged
  effects once: resource-only NONE follows §5.1's retained-configuration refresh; a captured
  REPUBLISH/FULL still honors its stronger load/discovery intent after replacement. Use final
  copied P13 data, publish textures before IDs and required geometry invalidation, then publish
  the final receipt and reopen admission only for a coherent tuple. No extra resource epoch or
  second reacquisition is caused by the listener notification.
- If prelude or original throws, do not admit shader use or publish copied partial data.
  Complete existing coherent-off compensation at the safe boundary, retaining required
  services on rejected retirement; an original exception remains the primary exception and
  cleanup failures are secondary diagnostics. The outer `finally` clears the gate only after
  recovery ownership is established. Never report a successful receipt, roll the resource epoch
  back, reuse the retired tuple, or swallow the original failure as a successful vanilla reload.

A missing H-RESOURCE-01 application is a CORE failure before shader activation: vanilla resource
reload remains vanilla-owned, but shaders cannot enable without the lifetime gate. A selective
listener invoked independently without destructive replacement may still queue resource NONE;
its later drain reacquires existing resources and is not evidence that it guarded a replacement.

### 4.9 Engine-flag execution

`FrameFlagResolver` freezes one `ResolvedFrameFlags` per configuration fingerprint and relevant
video-setting revision. It never consults properties on a hot hook. Video-setting precedence is
applied only where an actual corresponding setting exists; otherwise Phase 3's tri-state controls.

Every mutation uses a scope lease:

- back-face flags suspend culling for the named terrain call and restore the exact prior state;
- rain/beacon depth flags suspend depth testing only for their draw scope, never depth writes outside
  it;
- sun/moon suppression skips the corresponding tessellation call, not merely its texture bind;
- underwater/vignette use the cancellable Forge events in §4.11 and do not cancel unrelated overlay
  elements;
- cloud policy intercepts the exact `GameSettings.shouldRenderClouds()` query within
  `RenderGlobal.renderClouds`, preserving modded provider early-return behavior from the Cleanroom
  patch;
- frustum-culling FALSE redirects only the catalogued `ICamera.isBoundingBoxInFrustum` calls inside
  world setup/entity/block-entity paths. It does not globally replace `ICamera` or affect Phase 8's
  shadow frustum.

An invalid/missing hook disables that flag's effect (rung 2a), except a mismatched state-restore
anchor is critical and turns shaders off. Default/unset always delegates to vanilla.

---


### 4.10 Complete hook catalog

#### 4.10.1 Catalog notation and hook-health classes

Every Mixin target below uses the SRG name **and** descriptor. `HEAD/RETURN` pairs use an
engine-issued token retained in the bridge's render-thread scope stack; RETURN is the normal close
path, while H-FRAME-00's outer `finally` detects, aborts, and drains a scope leaked by a throw.
No mixin instance field owns policy or a scope. `AROUND` means a redirect of one named invocation to
a dumb bridge that calls the original operation inside `try/finally`. `AFTER INVOKE` means `@At(value="INVOKE",
target=..., shift=AFTER)`. Ordinals are accepted only where the Cleanroom patch or the OQ-4
application test proves that exact ordinal.

The catalog has four failure classes:

| Class | Application policy | Runtime policy |
|---|---|---|
| `CORE` | all core anchors form one Mixin health group; any missing or over-matched anchor disables shader rendering for the session | one diagnostic, rung 3 (`Off`), vanilla rendering continues |
| `FEATURE` | each balanced feature family is its own group | disable that program family/flag only (rung 2a), restore vanilla state |
| `OBSERVER` | optional state observer, no cancellation | retain the last valid/default Phase 6 value and diagnose once |
| `DEFERRED(Pn)` | a wholly deferred row has no Phase 7 mixin; on a mixed row only the owner-phase capability or augmentation is absent, while its active Phase 7 hook remains | no false capability claim; the owner phase activates and health-checks its portion |

All Phase 7 mixins use `require = 0`, normally `expect = 1`; the Mixin configuration plugin audits
actual application counts against this catalog after preparation. This makes optional application
non-crashing without making a missing core hook invisible. The registry's coexistence bail-out is
evaluated before the group is enabled (§4.12). The policy satisfies the degradation rule without
depending on Mixin's default fatal-injection behavior.

#### 4.10.2 Bootstrap and core frame transaction

| ID / class | SRG target and injection | Dumb bridge call | Health / evidence |
|---|---|---|---|
| H-BOOT-01 loader events | preInit registration/configuration, then FMLLoadCompleteEvent completion; no GameSettings injection | BootstrapHooks.onEarlyConfigurationLoaded after both prerequisites | FEATURE event prerequisite, not CORE Mixin; report expected2/actual observed event deliveries, no deferred owner; missing prerequisite keeps startup Off |
| H-BOOT-02 `OpenGlHelper` | `func_77474_a()V` (`initializeTextures`) RETURN | `BootstrapHooks.onGlReady()` | `CORE`; no GL work is attempted before it |
| H-BOOT-03 `GuiMainMenu` | `func_73866_w_()V` (`initGui`) RETURN, once | `BootstrapHooks.onClientLoadingComplete()` | `FEATURE`; shader startup may remain deferred |
| H-FRAME-00 `EntityRenderer` | `func_181560_a(FJ)V`, AROUND its `func_78471_a(FJ)V` (`renderWorld`) invocation | `WorldRenderBoundary.invoke(original, partialTicks, finishTimeNano)` | `CORE`; the outer `finally` is the early-exit guarantee |
| H-FRAME-01 `EntityRenderer` | `func_175068_a(IFJ)V` HEAD | `FrameHooks.open(pass, partialTicks, finishTimeNano)` | `CORE`; Pintonium row 1 and App E row 1 (`docs/research/v1/RESEARCH.md:1401`) |
| H9-HELD-01 accepted H-FRAME-01 boundary | no additional Mixin; immediately after Phase 6 accepts the copied frame identity | call current Phase 9 held sampler/resolver once and complete `updateHeldItems` before any program activation | `DEFERRED(P9,v0.3)` until installed, then `FEATURE`; zero tuple on provider failure, stale generation aborts the shader frame |
| H-FRAME-02 `EntityRenderer` | `func_175068_a(IFJ)V`, BEFORE `GlStateManager.clear(I)V`, ordinal 0 | `FrameHooks.normalizeVanillaState(frameToken)` | `CORE`; validated Pintonium row 2 |
| H-FRAME-03 `EntityRenderer` | same invocation, shift AFTER | `FrameHooks.afterFirstClear(frameToken)` | `CORE`; creates/refreshes Phase 5 frame resources but does not claim matrices |
| H-FRAME-04 `EntityRenderer` | `func_175068_a(IFJ)V`, AFTER INVOKE `func_78479_a(FI)V` (`setupCameraTransform`) | `FrameHooks.captureMainCamera(frameToken)` | `CORE`; exact post-camera point, once per frame |
| H-FRAME-05 `EntityRenderer` | `func_175068_a(IFJ)V`, AROUND the exact `RenderGlobal.func_174970_a(Entity,D,ICamera,I,Z)V` invocation | call original once with its exact arguments; on normal return retain its integer terrain token and call `FrameHooks.invokeShadowSlotThenRestoreMain(frameToken,terrainToken)` inside an execution scope whose close is in `finally` | `CORE`; Phase 8 slot, Pintonium row 4 |
| H-FRAME-06 `EntityRenderer` | `func_175068_a(IFJ)V` TAIL | `FrameHooks.finishNormal(frameToken)` | `CORE`; idempotent normal-return fast path |
| H-FRAME-07 `EntityRenderer` | `func_181560_a(FJ)V` wrapper's `finally` after H-FRAME-00 | `FrameHooks.finishGuaranteed(frameToken, exitKind)` | `CORE`; strictly stronger than Pintonium's TAIL-only behavior (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:195`–`:197`) |

H-FRAME-03 deliberately does not implement the governing text's matrix-capture claim at the clear
anchor. Cleanroom visibly places the clear before camera setup
(`reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:175`–`:179`),
so H-FRAME-04 is the first valid capture point. Sampling/previous-frame rotation already happened
at H-FRAME-01, before H-FRAME-03 can resize or clear, preserving the Phase 6 ordering contract.

#### 4.10.3 Sky, terrain, damage, and line scopes

| ID / class | SRG target and injection | Engine action | Health / evidence |
|---|---|---|---|
| H-SKY-01 `RenderGlobal` | `func_174976_a(FI)V` HEAD/RETURN | enter/exit `gbuffers_skybasic` | `FEATURE`; **no working reference**; App E row 2 (`docs/research/v1/RESEARCH.md:1402`) |
| H-SKY-02 `RenderGlobal` | same method, REDIRECT `World.func_72826_c(F)F` | call original, invoke the §5.1 `Celestial` update at this rotation moment, return the angle unchanged | `OBSERVER`; concrete celestial-rotation sub-site; **no working reference** |
| H-SKY-03 `RenderGlobal` | same method, slice-bounded redirects of the sun and moon `Tessellator.func_78381_a()V` draws | enter `gbuffers_skytextured`, honor `sun`/`moon`, call or suppress draw, restore sky-basic | `FEATURE`; ordinals must be fixed by OQ-4; **no working reference** |
| H-TERRAIN-01 `RenderGlobal` | `func_174977_a(BlockRenderLayer,D,I,Entity)I` HEAD/RETURN, only for SOLID/CUTOUT_MIPPED/CUTOUT | while an authenticated Phase 8 execution is active, bypass main-snapshot policy and preserve the vanilla call; otherwise select `gbuffers_terrain_solid`, `gbuffers_terrain_cutout_mip`, or `gbuffers_terrain_cutout` and apply scoped `backFace.*` | `CORE`; actual world-loop overload at `reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:200`–`:206` |
| H-TERRAIN-02 `RenderGlobal` | the same exact method HEAD/RETURN, only for `TRANSLUCENT` | while authenticated shadow execution is active, bypass main copy/deferred/water policy; otherwise at HEAD perform PRE_TRANSLUCENT copy/virtual/deferred sequence then enter `gbuffers_water`, and at RETURN close/restore | `CORE`; Pintonium row 5; copy is enabled at v0.5 under Phase 5's granted `PRE_WEATHER -> PRE_TRANSLUCENT` order |
| H-DAMAGE-01 `RenderGlobal` | `func_174981_a(Tessellator,BufferBuilder,Entity,F)V` HEAD/RETURN | enter/exit `gbuffers_damagedblock` | `FEATURE`; MCP-validated supporting hook |
| H-LINE-01 `RenderGlobal` | `func_72731_b(EntityPlayer,RayTraceResult,I,F)V` HEAD/RETURN | enter/exit `gbuffers_basic` | `FEATURE`; no event is used because `DrawBlockHighlightEvent` has no post scope |

The one-argument `RenderGlobal.func_174982_a(BlockRenderLayer)V` in Appendix E is retained in the
symbol ledger but receives no Phase 7 injection: it is not the world-loop call site. This is the
explicit U7-1 correction request, not an unreported substitution.

#### 4.10.4 Entities, block entities, and balanced nested effects

| ID / class | SRG target and injection | Engine action | Health / evidence |
|---|---|---|---|
| H-ENTITY-01 `RenderGlobal` | `func_180446_a(Entity,ICamera,F)V` HEAD/RETURN; REDIRECT its `RenderManager.func_178632_c(Z)V` calls | while authenticated shadow execution is active, bypass the main entity/glowing program policy and preserve vanilla calls; otherwise enter/exit `gbuffers_entities`, call the original outline toggle, and push/pop `gbuffers_entities_glowing` | `FEATURE`; App E row 2 |
| H-ENTITY-02 / H9-ENTITY-ID-01 `RenderManager` | `func_188388_a(Entity,F,Z)V` and `func_188391_a(Entity,D,D,D,F,F,Z)V` HEAD/RETURN | obtain IdScopeAdmission from accepted main entity scope or authenticated current shadow execution; map exact ordinal and enter P9 entity token; RETURN/finally restores preceding ID | `DEFERRED(P9,v0.3)` then FEATURE; nested overloads retain LIFO; stale admission writes no new ID and triggers neutral drain |
| H-ENTITY-03 / H9-BLOCK-ENTITY-ID-01 `TileEntityRendererDispatcher` | `func_147549_a(TileEntity,D,D,D,F)V` HEAD/RETURN | main path opens gbuffers_block before ID admission; authenticated shadow path obtains alternate admission without main scope/snapshot/activation; enter mapped-or-zero TE ID and restore before closing main scope or shadow release | program FEATURE; IDs DEFERRED(P9,v0.3) then FEATURE; unknown TE zero; current shadow credentials mandatory |
| H-LEASH-01 `RenderLiving` | `func_110827_b(EntityLiving,D,D,D,F,F)V` HEAD/RETURN | suspend parent snapshot, push `gbuffers_basic`, then reacquire parent | `FEATURE`; required push/pop semantics |
| H-GLINT-01 `LayerArmorBase` | `func_188364_a(RenderLivingBase,EntityLivingBase,ModelBase,F,F,F,F,F,F,F)V` HEAD/RETURN | suspend parent, push `gbuffers_armor_glint`, then reacquire parent | `FEATURE`; required push/pop semantics |
| H-EYES-01 `LayerSpiderEyes`, `LayerEndermanEyes`, `LayerEnderDragonEyes` | each `func_177141_a(<concrete entity>;FFFFFFF)V` HEAD/RETURN: `EntitySpider`, `EntityEnderman`, or `EntityDragon` descriptor respectively | suspend parent, push `gbuffers_spidereyes`, then reacquire parent | `FEATURE`; bridge overloads taking `EntityLivingBase` are excluded by descriptor |
| H-BEAM-01 `TileEntityBeaconRenderer` | both `func_188204_a(DDDDDDII[F)V` and `func_188205_a(DDDDDDII[FDD)V` HEAD/RETURN | enter/exit `gbuffers_beaconbeam`; scoped `beaconBeamDepth` | `FEATURE` |
| H-BEAM-02 `RenderDragon` | `func_188325_a(DDDFDDDIDDD)V` HEAD/RETURN | enter/exit `gbuffers_beaconbeam`; scoped `beaconBeamDepth` | `FEATURE`; Pintonium supporting hook |
| H9-COLOR-01 `RenderLivingBase` (stable catalog ID; owner P7) | within `func_177092_a(EntityLivingBase,F,Z)Z`, redirect exact `GlStateManager.func_187448_b(IILjava/nio/FloatBuffer;)V` invocation | exact GL_TEXTURE_ENV/GL_TEXTURE_ENV_COLOR operands copy four floats at duplicate's current position into P7 color scope, call original unchanged, immediately update P6 entityColor | OBSERVER, v0.1; one anchor; effect path only; independent of alias IDs |
| H9-COLOR-02 `RenderLivingBase` (stable catalog ID; owner P7) | `func_177091_f()V` RETURN | restore prior P7 color scope through P6 after vanilla unsets effect; zero if no parent | OBSERVER, v0.1; one anchor; frame finally drains on throw |

`PassScopeStack` implements §4.4 exactly: close the parent's binding, complete its drawn no-flip
gbuffers pass (discard if undrawn), retain the original selection/context, then acquire/bind child.
Pop closes/completes child and reacquires sides/lease/bindings for that same retained parent
selection; it neither selects the provider as a new request nor reuses a consumed snapshot.
Binding suppression omits only the affected operation; backend failure aborts the frame. Local
AROUND finally and H-FRAME-00 leak draining preserve the original throwable and exactly-one closure.

The color/ID augmentation uses one reverse-close order: P7 color, P9 entity/block ID if installed,
then the main program scope, or the still-active shadow scope without a main close. H-FRAME-00
drains the P7 color stack on every terminal path even before P9 exists, then asks installed P9
to resetFrame before fixed release. P7's bounded color stack is keyed by current frame plus
accepted main/entity render or authenticated shadow execution, retaining only four floats and
LIFO depth. It snapshots the previous tuple on an actual effect operand, restores it on unset,
and invokes `UniformEventSink.updateEntityColor(Float4)` immediately for update/restoration.
No active program means P6 caches until activation. Invalid nesting/input writes zero, disables
color delivery for that frame, and preserves vanilla calls. Neutral before hook installation is
degraded bring-up, not permission to defer v0.1 entityColor. P9 does not install a second writer.

H9-COLOR-01 never queries TexEnv after the call and never reconstructs vanilla hurt/creeper
formulas. Glue validates four readable floats at the duplicate's current position, finite-checks
them, and neither mutates nor retains the original buffer. A wrong target/pname, short buffer,
missing/extra observation, or stale scope forwards the original call but disables color delivery
for that frame and publishes neutral. This is the exact operand/current-scope refinement required
by R9-2; the former RETURN-only H-COLOR description is superseded.

#### 4.10.5 Particles, clouds, weather, border, hand, and overlays

| ID / class | SRG target and injection | Engine action | Health / evidence |
|---|---|---|---|
| H-PARTICLE-01 `ParticleManager` | `func_78874_a(Entity,F)V` HEAD/RETURN | enter/exit `gbuffers_textured` | `FEATURE`; App E row 15 (`docs/research/v1/RESEARCH.md:1415`) |
| H-PARTICLE-02 `ParticleManager` | `func_78872_b(Entity,F)V` HEAD/RETURN | enter/exit `gbuffers_textured_lit` | `FEATURE`; MCP-validated companion method |
| H-CLOUD-01 `RenderGlobal` | `func_180447_b(F,I,D,D,D)V` HEAD/RETURN | while authenticated shadow execution is active, bypass the main cloud flag/program policy; otherwise honor resolved `clouds` and enter/exit `gbuffers_clouds` | `FEATURE`; **no working reference** |
| H-CLOUD-02 `GameSettings` | `func_181147_e()I` RETURN modifier, active only during H-CLOUD-01 | return the Phase 3-resolved FAST/FANCY mode; OFF is handled before draw | `FEATURE`; vanilla mode query otherwise unchanged |
| H-WEATHER-01 `EntityRenderer` | `func_78474_d(F)V` HEAD/RETURN | PRE_WEATHER copy, enter/exit `gbuffers_weather`, apply scoped `rainDepth` | `CORE` copy / `FEATURE` routing; **no working reference** |
| H-BORDER-01 `RenderGlobal` | `func_180449_a(Entity,F)V` HEAD/RETURN | enter/exit `gbuffers_textured_lit` | `FEATURE`; App A.1 world-border mapping |
| H-HAND-01 `EntityRenderer` | `func_78476_b(F,I)V`, AROUND `ItemRenderer.func_78440_a(F)V` | invoke the original through `HandSubpassBridge` once with SOLID mask and once with TRANSLUCENT mask; apply depth-scale only to the latter | `CORE`; Pintonium row 6 moment, with reference depth timing rejected |
| H-HAND-02 `ItemRenderer` | inside `func_78440_a(F)V`, REDIRECT calls to `func_187462_a(EntityLivingBase,ItemStack,TransformType,Z)V`, `func_187456_a(F,F,EnumHandSide)V`, `func_187463_a(F,F,F)V`, `func_187461_a(ItemStack)V`, and `func_187465_a(F,EnumHandSide,F,ItemStack)V` | `HandContentClassifier` supplies a copied SOLID/TRANSLUCENT category; call each original only in its matching mask, with bare arms/maps classified SOLID | `FEATURE`; every target/descriptor MCP-validated; no policy in mixin |
| H-OVERLAY-01 `ItemRenderer` | `func_78447_b(F)V` HEAD/RETURN | acquire/release Phase 5's `openDrawBuffersNone` lease around first-person overlays | `FEATURE`; closed rejection/failure degrades the overlay locally and preserves vanilla routing |
| H-OVERLAY-02 Forge event bus | cancelable `RenderBlockOverlayEvent` only for `WATER` when `underwaterOverlay == FALSE` | no program switch | `FEATURE`; event preferred over a mixin |
| H-OVERLAY-03 Forge event bus | cancelable `RenderGameOverlayEvent.Pre` only for `VIGNETTE` when `vignette == FALSE` | no program switch | `FEATURE`; event preferred over a mixin |

The hand bridge never owns the center-depth read or the weather copy. It consumes only the already
sampled Phase 6 value and the two balanced hand routes, preserving the authoritative ordering at
`docs/research/v1/RESEARCH.md:557`–`:561`.

#### 4.10.6 State observation and restoration

| ID / class | SRG target and injection | Engine action | Health |
|---|---|---|---|
| H-BLEND-01 `GlStateManager` | `func_179147_l()V`, `func_179084_k()V`, `func_187401_a(SourceFactor,DestFactor)V`, `func_179112_b(I,I)V`, `func_187428_a(SourceFactor,DestFactor,SourceFactor,DestFactor)V`, `func_179120_a(I,I,I,I)V` RETURN | publish exact blend enabled/function state to Phase 6 | `OBSERVER`; App E row 16 (`docs/research/v1/RESEARCH.md:1416`) |
| H-FOG-01 `GlStateManager` | `func_179127_m()V`, `func_179106_n()V`, `func_187430_a(FogMode)V`, `func_179093_d(I)V`, `func_179095_a(F)V`, `func_179102_b(F)V`, `func_179153_c(F)V` RETURN | publish exact fog state | `OBSERVER` |
| H-FOG-02 Forge event bus | `EntityViewRenderEvent.FogColors`, `FogDensity`, and `RenderFogEvent` | publish fog color/mode/density at event fidelity | `OBSERVER`; event preferred |
| H-STATE-01 `GlStateManager` | `func_179135_a(Z,Z,Z,Z)V`, `func_179089_o()V`, `func_179129_p()V` RETURN | update the bridge's restoration ledger only | `OBSERVER`; driver policy remains engine-side |
| H-FRUSTUM-01 `RenderGlobal` | within `func_174970_a(Entity,D,ICamera,I,Z)V` and `func_180446_a(Entity,ICamera,F)V`, REDIRECT only `ICamera.func_78546_a(AxisAlignedBB)Z` calls | authenticated shadow execution bypasses only the main `frustumCulling` override and delegates to Phase 8's supplied camera; otherwise delegate normally unless `frustumCulling == FALSE`, then return visible | `FEATURE`; no global `ICamera` replacement |

The observer callbacks receive primitives/enums translated by `mod.glue`; no Minecraft, Forge,
LWJGL, or Mixin type crosses into `:engine`. Driver-applied state changes carry an internal guard so
observers report the effective state once rather than recursively re-entering policy.

#### 4.10.7 Resize, framebuffer, world lifetime, and capture

| ID / class | SRG target and injection | Engine action | Health / evidence |
|---|---|---|---|
| H-RESIZE-01 `Minecraft` | `func_147119_ah()V` (`updateFramebufferSize`) RETURN | offer actual framebuffer width/height to `ResolutionCoordinator` | `FEATURE`; fallback OQ-3 path |
| H-RESIZE-02 `Minecraft` | `func_71370_a(I,I)V` (`resize`) RETURN | mark window/display extent changed; actual FBO size still comes from H-RESIZE-01 | `FEATURE`; keeps HiDPI dimensions distinct |
| H-FBO-01 `Framebuffer` | `func_147613_a(I,I)V` (`createBindFramebuffer`) RETURN | increment vanilla-FBO attachment epoch and schedule next safe-boundary rebuild | `CORE`; App E row 17 (`docs/research/v1/RESEARCH.md:1417`) |
| H-FBO-02 `Framebuffer` | `func_147610_a(Z)V` (`bindFramebuffer`) RETURN and `func_147615_c(I,I)V` (`framebufferRender`) HEAD/RETURN | observe final target/presentation boundaries; never retain the vanilla handle beyond its epoch | `OBSERVER`; capture occurs before presentation |
| H-WORLD-01 Forge event bus | `WorldEvent.Unload` for the client world | abort open frame, demote the dimension pipeline to plan-only, clear temporal world state | `CORE`; event preferred over `WorldClient` mixin (App E row 18) |
| H-RESOURCE-01 `SimpleReloadableResourceManager` | `func_110541_a(Ljava/util/List;)V` (`reloadResources`), AROUND entire body with outer finally | `ResourceReloadBoundary.invoke(original)`; synchronous pre-destructive quiescence and post-listener completion (§4.8.1) | `CORE`; expected one wrapper application; MCP confirms target/release-before-listeners contract, OQ-4 must prove weaving and all supported replacement paths |
| H-CAPTURE-01 frame bridge | after `final` completes and before H-FBO-02 presentation | invoke the optional installed `FrameCaptureListener` once with the borrowed view | `FEATURE`; Phase 2 R11; absent listener disables capture |
| H-CAPTURE-02 `Minecraft` | clean client shutdown entry `func_71400_g()V`, invoked through main-thread scheduling | terminate after the final artifact commit | `FEATURE`; Phase 2 R14; no timeout kill |

Resize never mutates an open Phase 5 generation. The coordinator records the newest actual FBO
extent, aborts the current shader frame if an allocation epoch changes mid-frame, and performs one
candidate rebuild/publication at the next H-FRAME-01 boundary. The vanilla render continues during
that degraded frame.

#### 4.10.8 Appendix E owner/deferral ledger (all 18 rows)

This ledger is the completeness proof for Appendix E's class catalog
(`docs/research/v1/RESEARCH.md:1396`–`:1418`). A deferred row is intentionally **not** a Phase 7
injection specification; it records the exact owner and milestone so later work cannot mistake
absence for omission.

| App E row | Class | Phase 7 disposition |
|---:|---|---|
| 1 | `EntityRenderer` | H-FRAME, H-WEATHER, H-HAND implemented v0.1; H9-HELD-01 accepted-frame augmentation activates with Phase 9 at v0.3 |
| 2 | `RenderGlobal` | H-SKY, H-TERRAIN, H-DAMAGE, H-LINE, H-ENTITY, H-CLOUD, H-BORDER; implemented v0.1 |
| 3 | `RenderChunk.func_178581_b(FFF,ChunkCompileTaskGenerator)V` | `DEFERRED(P10,v0.3)`: chunk-build entity-data push/pop |
| 4 | `ChunkRenderDispatcher` | `DEFERRED(P10,v0.3)`: async build-context propagation; exact method chosen by Phase 10 |
| 5 | `BufferBuilder.func_181668_a(I,VertexFormat)V`, `func_181675_d()V`, `func_178981_a([I)V` | `DEFERRED(P10,v0.3)`: stamp attributes at vertex-write boundaries |
| 6 | `Tessellator.func_78381_a()V` | `DEFERRED(P10,v0.3)`: non-VBO draw path (sky's H-SKY redirects are scoped program/cancellation wrappers, not vertex interception) |
| 7 | `WorldVertexBufferUploader.func_181679_a(BufferBuilder)V` | `DEFERRED(P10,v0.3)`: client-array attribute pointers |
| 8 | `VertexBuffer.func_181722_a(ByteBuffer)V`, `func_177358_a(I)V` | `DEFERRED(P10,v0.3)`: VBO upload/draw attributes |
| 9 | `BlockModelRenderer.func_178267_a(IBlockAccess,IBakedModel,IBlockState,BlockPos,BufferBuilder,Z)Z` | `DEFERRED(P10,v0.3)`: per-block push/pop |
| 10 | `TextureMap.func_110551_a(IResourceManager)V`, `func_110571_b(IResourceManager)V` | `DEFERRED(P13,v0.5)`: companion atlas lifecycle and `atlasSize`; prefer `TextureStitchEvent.Pre/Post` where sufficient |
| 11 | `TextureAtlasSprite` class-level lifecycle | `DEFERRED(P13,v0.5)`: per-sprite companions; exact method chosen by Phase 13 |
| 12 | `ItemRenderer.func_78440_a(F)V` | H-HAND/H-OVERLAY; implemented v0.1 |
| 13 | `RenderManager.func_188388_a(Entity,F,Z)V`, `func_188391_a(Entity,D,D,D,F,F,Z)V` | H9-ENTITY-ID-01 is fully ordered/specified here and activates at v0.3; Phase 7's surrounding entity program scope remains H-ENTITY-01 |
| 14 | `TileEntityRendererDispatcher.func_147549_a(TileEntity,D,D,D,F)V` | H-ENTITY-03 program scope at v0.1; H9-BLOCK-ENTITY-ID-01's inside-open/before-close augmentation activates at v0.3 |
| 15 | `ParticleManager.func_78874_a(Entity,F)V` | H-PARTICLE; implemented v0.1 |
| 16 | `GlStateManager` class-level | H-BLEND/H-FOG/H-STATE and stable H9-COLOR-01/02 operand/reset rows are P7-owned v0.1; P9 IDs are independently v0.3 |
| 17 | `Framebuffer.func_147610_a(Z)V` | H-FBO/H-RESIZE; implemented v0.1 |
| 18 | `WorldClient` class-level | Forge `WorldEvent.Unload` plus Phase 6 provider; no Phase 7 world mixin |

Hook need 11 remains `DEFERRED(P12,v0.4)`: pack selection/options/reload GUI, F3+R, and command
integration are pure mod-side work. Phase 7 exposes `ShaderReloadController`; it adds no GUI mixin.

### 4.11 Forge/Cleanroom event choices

| Need | Event decision | Reason |
|---|---|---|
| final trigger | reject `RenderWorldLastEvent` | Cleanroom dispatches it before first-person hand rendering, so it cannot guarantee composite/final after the full Phase 7 frame |
| hand scope | reject `RenderHandEvent` | it is a cancelable pre-event with no balanced post event; H-HAND-01 provides exact `try/finally` scope |
| selection line | reject `DrawBlockHighlightEvent` | pre/cancel only; no balanced post point for program restoration |
| water overlay | use `RenderBlockOverlayEvent(WATER)` | exact cancelable behavior; no program-scope requirement |
| vignette | use `RenderGameOverlayEvent.Pre(VIGNETTE)` | exact cancelable behavior |
| fog state | use `EntityViewRenderEvent` family plus minimal GL observers | events supply view-level values; GL observers preserve exact effective state |
| world teardown | use `WorldEvent.Unload` | exact lifecycle signal, avoids a `WorldClient` mixin |
| registry remap | use `FMLModIdMappingEvent` | enqueue `RebuildActive` with `REGISTRY_REMAP`; the callback never resolves IDs or publishes directly |
| atlas | defer with preference for `TextureStitchEvent.Pre/Post` | Phase 13 decides whether sprite-companion fidelity needs more |
| resize | no suitable event found | H-RESIZE/H-FBO implement the OQ-3 fallback |

The event disposition is based on the pinned Cleanroom/Forge event API queried for this design;
events are not assumed to provide a post point their contract does not expose.

### 4.12 Coexistence, application audit, and hot-path posture

Phase 7 consumes Phase 1 `BailRegistry.evaluate(CompatContext) -> CompatEvaluation(bails,
degradations)` and `CompatVerdict.Ok|Degrade|Bail`. Evaluate post-FMLLoadCompleteEvent before
first pack load, before every P10 vertex-format change, and in `shouldApplyMixin` only for
the owner-approved MOD-phase configurations. `shouldBail()` forces shaders off for the session,
preserves each reason/args for the P1 CHAT error, compat log and P12 view, and forbids retrying
into Active. Degradations preserve their exact feature restriction/diagnostic; empty bails
permit only otherwise valid features. No invented replacement-backend verdict grants an adapter,
no blanket CORE-config veto is inferred, and partial double-injection is forbidden.

`HookApplicationReport` contains catalog ID, target description, expected count, actual count,
failure classes, deferred owner phase where applicable, and fallback. Stable IDs H9-HELD-01,
H9-ENTITY-ID-01 and H9-BLOCK-ENTITY-ID-01 remain DEFERRED(P9), expected/actual zero, VANILLA
until installation, then FEATURE with 1/1 registration, 4/4 and 2/2 Mixin anchors respectively.
H9-COLOR-01/02 retain their historical catalog spelling but are P7-owned v0.1 OBSERVER rows,
expected 1 and actual application count, no deferred owner; failed observation falls back neutral.
Partial ID activation disables the ID/held feature group; color degrades independently.
No dormant row is hidden and no historical primary ID is renamed to imply new hook evidence.

Phase 8 health is nested rather than flattened into the primary row list. A verified Phase 8
contributor supplies exactly one owner-phase-8 `HookApplicationSubreport` copied from its immutable
`ShadowHookHealth`: the canonical fingerprint, aggregate `shadowEnabled` value, and all eight rows
of Phase 8's §4.13 ledger in hook-ID order with their exact expected/actual counts and
`HEALTHY|FEATURE_DISABLED` disposition. Adding a P8 subreport never changes the current P7
primary catalog IDs, target strings, order, counts or classes (including §4.12's color adoption).
Before Phase 8 is installed there is no owner-phase-8 subreport; absence cannot imply healthy shadow
capability. A Phase 8 plan/publication may enable only when its hook fingerprint equals the nested
subreport fingerprint and every Phase-8-required row is healthy. Other owner phases may later use
the same nested shape, with unique positive owner phase and unique fingerprint, without changing
Phase 7 row identities.

The report is frozen before first frame, printed once on the `schmaloogium.hooks` channel, and
included in capture diagnostics. The bridge measures aggregate nanoseconds/call count only when a
debug profiler is enabled; production hot paths allocate no report records and perform one token
check plus one interface call.

OQ-4 does not reopen the engine contract. It may replace a catalog row's injection form while
preserving its ID, moment, bridge call, balance, and failure class. Such a change is a Phase 7 doc
fix-up because exact hooks are binding here.

### 4.13 Capture agent and manifest boundary

`CaptureAgent` is inert without the explicit plan property and lives in `:mod`; its only wire
versions are `schmaloogium.capture-plan/2` and `schmaloogium.run-manifest/2`.
P2 v2 §§4.5.1–4.5.4 are incorporated in full, including scalar/escape/order/availability and
failure rules. Reject `/1`, duplicate/missing/unknown/malformed keys and sparse indices before
world load; no reader/defaulting/migration fallback. Plans admit registry-backed packs only.

1. Apply the copied world/client settings; select the plan's registry-backed pack through the
   controller and await its initial coherent receipt. Obtain current ProgrammaticOptionSnapshot,
   validate/apply pinned deltas through §5.1 bridge against that identity, await the final successful
   receipt, then wait prepTicks. Persistence uses only copied isolated harness roots. For each
   capture in authored order apply held state and first pose; render exactly warmupFrames there.
2. For every dense sample `0..samples.count-1`, install its exact pose immediately before
   exactly one rendered frame. SHOT is already expanded into repeated samples; PATH is dense,
   never interpolated, skipped, teleported only at screenshot time or scheduled by wall clock.
3. At H-CAPTURE-01 after final and before presentation, copy `FramePoseReport` actual current
   and previous render-camera poses; never echo SceneApplier's requested values. Sample zero
   planned previous equals the first pose; later planned previous is sample n-1. Repeated
   first-pose warm-up establishes sample-zero history naturally; do not reset P6 through an
   invented capture lifecycle event. Report actual current/previous values used by frame/camera
   providers, not transforms, shader-offset coordinates, image inference or plan echoes.
4. Emit one `frames.<n>` record for **every** post-warm-up sample, captured or not. Capture
   pixels only in `[captureStartSample,captureStartSample+captureSampleCount)`, through the
   vanilla screenshot adapter. Copy the immutable frame report and owner projections while
   borrowed views are live. Write PNG without metadata; hash canonical raw ARGB pixels,
   not PNG bytes. Image records match captured frame keys one-to-one.
5. Serialize a temporary manifest, atomically commit images, and acknowledge completed samples
   only after their artifacts commit. After the final sample/artifact commit request clean
   H-CAPTURE-02 shutdown. P2 validates provenance/coverage and owns canonical manifest publication
   and failure synthesis; agent errors never invent COMPLETE or terminate the normal client.

Exact wire names are P2's, not record-name derivation: required `captures.count` and dense
`captures.<n>.{kind,id,plannedWarmupFrames,actualWarmupFrames,plannedSamples,actualSamples,
captureStartSample,captureSampleCount}`; `frames.count` and
`frames.<n>.{captureKind,captureId,sampleOrdinal,captured,plannedCurrent.posX,
plannedCurrent.posY,plannedCurrent.posZ,plannedCurrent.yaw,plannedCurrent.pitch,
plannedPrevious.posX,plannedPrevious.posY,plannedPrevious.posZ,plannedPrevious.yaw,
plannedPrevious.pitch,actualCurrent.posX,actualCurrent.posY,actualCurrent.posZ,
actualCurrent.yaw,actualCurrent.pitch,actualPrevious.posX,actualPrevious.posY,
actualPrevious.posZ,actualPrevious.yaw,actualPrevious.pitch,worldTick,partialTicks,
frameCounter,entityCount,durationMillis}`; `images.count` and
`images.<n>.{captureKind,captureId,sampleOrdinal,path,width,height,pixelSha256}`.
First line is the exact schema line, remaining dotted keys lexicographically sorted; JSON-string
escaping, base-10 integers, exact finite fixed-decimal pose normalization follow P2 §4.1.
Frames sort by capture index/sample ordinal. P2 rejects count/pose mismatches before image diff.

Readiness is coherent Active tuple, exact configured finalized warm-up frames, and no failure,
not menu readiness or P4 generation arithmetic. WAITING distinguishes warm-up; timeout/wedge
handling remains runner-owned. Serialize P4 resolutions, P5 resources, total replay-aware GL
results, frozen primary/nested hook reports and runner provenance verbatim with P2's exact
remaining §§4.5.4 grammars. `CompatEvaluation` maps only to wire Continue if reached without
session Bail, Bail if bailed, otherwise NOT_REACHED; no compatible-backend inference.

---

## 5. Cross-phase interfaces

### 5.1 Interfaces and data contracts exposed by Phase 7

The public frame seam is deliberately split in two. `engine.frame` owns policy and transaction
state. `mod.glue.frame` adapts Minecraft/Forge events and the catalogued mixins to primitive or
engine-owned values. `mod.mixin.frame` contains no state beyond a call-local token. A future Kirino
integration replaces the hook producer and `FrameRenderPort`, not `FrameDriver`, the program
schedule, or any Phase 3–6 contract.

```java
public interface FrameHookSink {
    FrameOpenResult open(FrameBeginSignal signal);
    FrameStepResult beforeFirstClear(FrameToken token);
    FrameStepResult afterFirstClear(FrameToken token, MainDepthPreparation depth);
    FrameStepResult captureMainCamera(FrameToken token, CameraSnapshot camera);
    FrameStepResult afterTerrainSetup(FrameToken token);
    ScopeOpenResult enter(FrameToken token, RenderSection section);
    ScopeCloseResult exit(FrameToken token, ScopeToken scope);
    FrameFinishResult finish(FrameToken token, FrameExitKind exitKind);
    FrameAbortResult abort(FrameToken token, FrameAbortReason reason);
}

public record FrameBeginSignal(
    long worldEpoch, long logicalTick, double smoothingTimeTicks, float frameTimeSeconds,
    DimensionKey dimension, int vanillaPass, float partialTicks,
    Extent2i targetView, Extent2i priorCompletedFramebuffer, AnaglyphEye eye) {}
public enum AnaglyphEye { LEFT, RIGHT }

public record CameraSnapshot(
    Matrix4Value modelView, Matrix4Value projection) {}

public enum FrameExitKind { NORMAL, EARLY_RETURN, THROWN }
public enum FrameAbortReason {
    PROTOCOL_REJECTION, BACKEND_FAILURE, RESIZE_EPOCH, WORLD_CHANGE, HOOK_UNHEALTHY
}
public enum FrameState {
    SAMPLED, BUFFER_OPEN, MATRICES_CAPTURED, SHADOW_DONE, GBUFFERS,
    DEFERRED_DONE, GBUFFERS_TRANS, FINALIZING, COMMITTED
}
public record FailureId(String diagnosticId) {
    public FailureId {
        if (diagnosticId == null || diagnosticId.isBlank()) throw new IllegalArgumentException();
    }
}

public sealed interface FrameOpenResult {
    record Opened(FrameToken token) implements FrameOpenResult {}
    record VanillaOnly(FrameOpenRejection reason) implements FrameOpenResult {}
    record Failed(FailureId failure) implements FrameOpenResult {}
}
public enum FrameOpenRejection {
    WRONG_THREAD, FRAME_ALREADY_OPEN, STALE_PUBLICATION, NON_WORLD_PASS,
    MISSING_WORLD_OR_CAMERA, INVALID_INPUT, SHADERS_OFF
}

public sealed interface FrameStepResult {
    record Advanced(FrameState state) implements FrameStepResult {}
    record Rejected(HookRejection reason) implements FrameStepResult {}
    record Aborted(FrameAbortReason reason) implements FrameStepResult {}
    record Failed(FailureId failure) implements FrameStepResult {}
}
public sealed interface ScopeOpenResult {
    record Opened(ScopeToken scope, DrawDisposition draw) implements ScopeOpenResult {}
    record Rejected(HookRejection reason) implements ScopeOpenResult {}
    record Aborted(FrameAbortReason reason) implements ScopeOpenResult {}
    record Failed(FailureId failure) implements ScopeOpenResult {}
}
public enum DrawDisposition { DRAW_SHADER, DRAW_FIXED_FUNCTION, OMIT_OPERATION }
public enum HookRejection { WRONG_TOKEN, WRONG_ORDER, STALE_PUBLICATION, INVALID_SECTION }
public sealed interface ScopeCloseResult {
    record Closed(DrawDisposition resumedParent) implements ScopeCloseResult {}
    record Rejected(HookRejection reason) implements ScopeCloseResult {}
    record Aborted(FrameAbortReason reason) implements ScopeCloseResult {}
    record Failed(FailureId failure) implements ScopeCloseResult {}
}
public sealed interface FrameFinishResult {
    record Finalized(FinalizedFrame frame) implements FrameFinishResult {}
    record AlreadyTerminal() implements FrameFinishResult {}
    record Aborted(FrameAbortReason reason) implements FrameFinishResult {}
    record Failed(FailureId failure) implements FrameFinishResult {}
}
public sealed interface FrameAbortResult {
    record Aborted(FrameAbortReason reason) implements FrameAbortResult {}
    record AlreadyTerminal() implements FrameAbortResult {}
    record Failed(FailureId failure) implements FrameAbortResult {}
}

public interface IdDynamicsFrameSlot {
    IdAcceptedFrameResult afterUniformFrameAccepted(
        FrameToken frame, FrameBeginSignal signal, PublishedIdRuntime runtime);
    IdResetFrameResult resetFrame(
        FrameToken frame, PublishedIdRuntime runtime, IdFrameResetReason reason);
}
public sealed interface IdAcceptedFrameResult {
    record Completed() implements IdAcceptedFrameResult {}
    record HeldFeatureDisabled(FailureId failure) implements IdAcceptedFrameResult {}
    record Rejected(IdFrameRejection reason) implements IdAcceptedFrameResult {}
    record Failed(FailureId failure) implements IdAcceptedFrameResult {}
}
public sealed interface IdResetFrameResult {
    record Reset() implements IdResetFrameResult {}
    record AlreadyNeutral() implements IdResetFrameResult {}
    record Rejected(IdFrameRejection reason) implements IdResetFrameResult {}
    record Failed(FailureId failure) implements IdResetFrameResult {}
}
public enum IdFrameRejection { WRONG_FRAME, STALE_RUNTIME, WRONG_THREAD }
public enum IdFrameResetReason {
    NORMAL, EARLY_RETURN, THROWN, ABORT, RESIZE, OFF, REPLACEMENT, TEARDOWN
}

public interface ShadowInvocationSlot {
    ShadowSlotEpoch slotEpoch();
    ShadowInvocationResult invoke(ShadowInvocationContext context);
}
public record ShadowInvocationContext(
    FrameToken frame,
    ShadowFrameView shadowFrame,
    CameraSnapshot camera,
    PublishedRegistry registry,
    PublishedBufferEstate buffers,
    FrameBarrierContexts barrierContexts,
    ShadowExecutionView execution,
    ProgramBindingSelection selection,
    BarrierContext activationContext,
    TexturePublication texturePublication,
    TextureLeaseSource textureLeases) {}
public record ShadowFrameView(
    long worldEpoch,
    long frameId,
    float partialTicks,
    int mainTerrainFrameToken,
    Double3 cameraPosition,
    float skyAngle,
    float sunAngle) {}
public interface ShadowSlotEpoch {}
public interface ShadowExecutionIdentity {}
public interface ShadowExecutionView {}
public interface ShadowExecutionBridge {
    ShadowExecutionOpenResult open(
        ShadowExecutionIdentity activeExecutionIdentity, ShadowSlotEpoch slotEpoch);
    ShadowExecutionValidationResult validate(
        ShadowExecutionView view,
        ShadowExecutionIdentity activeExecutionIdentity,
        ShadowSlotEpoch slotEpoch);
    ShadowExecutionCloseResult close(ShadowExecutionView view);
}
public sealed interface ShadowExecutionOpenResult {
    record Opened(ShadowExecutionView view) implements ShadowExecutionOpenResult {}
    record Rejected(ShadowExecutionOpenRejection reason) implements ShadowExecutionOpenResult {}
}
public enum ShadowExecutionOpenRejection { WRONG_THREAD, ALREADY_ACTIVE }
public sealed interface ShadowExecutionValidationResult {
    record Valid() implements ShadowExecutionValidationResult {}
    record Rejected(ShadowExecutionValidationRejection reason)
        implements ShadowExecutionValidationResult {}
}
public enum ShadowExecutionValidationRejection {
    WRONG_ISSUER, INACTIVE, WRONG_EXECUTION, STALE_SLOT_EPOCH, WRONG_THREAD
}
public sealed interface ShadowExecutionCloseResult {
    record Closed() implements ShadowExecutionCloseResult {}
    record Rejected(ShadowExecutionCloseRejection reason)
        implements ShadowExecutionCloseResult {}
}
public enum ShadowExecutionCloseRejection { WRONG_ISSUER, INACTIVE, WRONG_EXECUTION }
public sealed interface ShadowInvocationResult {
    record NotInstalled() implements ShadowInvocationResult {}
    record Completed() implements ShadowInvocationResult {}
    record Rejected(ShadowRejection reason) implements ShadowInvocationResult {}
    record Failed(FailureId failure) implements ShadowInvocationResult {}
}
public enum ShadowRejection { WRONG_FRAME, STALE_PUBLICATION, UNSUPPORTED }

public interface FrameRenderPort {
    StateSnapshot snapshotState();
    PortResult normalizeForEngine();
    PortResult bind(PassDrawTarget target, AnaglyphEye eye);
    PortResult drawFullscreen(FullscreenDraw draw);
    PortResult restore(StateSnapshot snapshot);
}

public sealed interface PortResult {
    record Completed() implements PortResult {}
    record Rejected(PortRejection reason) implements PortResult {}
    record Failed(FailureId failure) implements PortResult {}
}
public enum PortRejection { WRONG_THREAD, STALE_TARGET, INVALID_DRAW, UNSUPPORTED }
public interface StateSnapshot {}
public enum FullscreenPrimitive { QUADS, TRIANGLE_STRIP }
public record MipmapSet(Set<Integer> colortexIndices) {}
public record ViewportScale(float x, float y, float width, float height) {}
public record FullscreenDraw(
    PassDescriptor pass, MipmapSet mipmaps, ViewportScale viewport,
    int instanceIndex, int instanceCount, FullscreenPrimitive primitive) {}

// Internal composition contracts; visible only to frame/reload glue and bootstrap.
interface PipelineCoordinator {
    PipelineBuildResult buildAndPublish(PipelineBuildRequest request);
    PipelineStatus current();
}
record PipelineBuildRequest(
    PackSelection selection, PackConfiguration configuration, DimensionKey dimension,
    Extent2i targetExtent, ReloadReasons reasons) {}
sealed interface PipelineBuildResult {
    record Active(PipelineIdentity identity, PipelineVersion version) implements PipelineBuildResult {}
    record Off(PipelineVersion version) implements PipelineBuildResult {}
    record Rejected(PipelineBuildRejection reason) implements PipelineBuildResult {}
    record Failed(FailureId failure) implements PipelineBuildResult {}
}
public enum PipelineBuildRejection { WRONG_THREAD, INVALID_REQUEST, STALE_INTENT, BUILD_IN_PROGRESS }
public record PipelineIdentity(
    PackIdentity pack, DimensionKey dimension, ConfigurationFingerprint configuration) {}
public sealed interface PipelineStatus {
    record Active(PipelineIdentity identity, PipelineVersion version) implements PipelineStatus {}
    record Off(PipelineVersion version) implements PipelineStatus {}
    record Building(ReloadToken token, PipelineVersion priorVersion) implements PipelineStatus {}
}
public interface DimensionPipelineCache {
    Optional<DimensionPipelineRecord> find(DimensionKey key);
    List<DimensionPipelineRecord> snapshot();
}
public record DimensionPipelineRecord(
    DimensionKey key, PipelineCacheState state, ConfigurationFingerprint fingerprint,
    PipelineVersion version, Optional<PipelineIdentity> activeIdentity) {}
public enum PipelineCacheState { PLAN_ONLY, READY_UNPUBLISHED, ACTIVE }
public record PipelineVersion(long value) {}
public record ReloadToken(long value) {}
public enum ReloadReason {
    PACK_SELECTION, OPTION_CHANGE, RESOURCE_RELOAD, DIMENSION_CHANGE, RESOLUTION_MULTIPLIER,
    REGISTRY_REMAP, MOD_ID_SOURCE_CHANGE, TAG_OR_ALIAS_CATALOG_CHANGE, HAND_LIGHT_POLICY_CHANGE
}
public record ReloadReasons(Set<ReloadReason> values) {}

public interface IdDependentGeometryInvalidator {
    IdGeometryInvalidationResult invalidate(IdPublicationChange change);
}
public record IdPublicationChange(
    OptionalLong previousGeneration, long currentGeneration,
    Optional<IdRuntimeFingerprint> previousFingerprint,
    IdRuntimeFingerprint currentFingerprint) {}
public sealed interface IdGeometryInvalidationResult {
    record Completed() implements IdGeometryInvalidationResult {}
    record Rejected(IdGeometryInvalidationRejection reason)
        implements IdGeometryInvalidationResult {}
    record Failed(FailureId failure) implements IdGeometryInvalidationResult {}
}
public enum IdGeometryInvalidationRejection {
    STALE_GENERATION, INVALID_CHANGE, SCHEDULER_UNAVAILABLE
}

public interface ResizeObservationPort {
    ResizeObservationResult observe(ResizeObservation observation);
}
public sealed interface ResizeObservation {
    record DisplayExtentChanged(long worldEpoch, DimensionKey dimension, Extent2i displayExtent)
        implements ResizeObservation {}
    record FramebufferExtentObserved(long worldEpoch, DimensionKey dimension, Extent2i actualExtent)
        implements ResizeObservation {}
    record AttachmentEpochAdvanced(
        long worldEpoch, DimensionKey dimension, Extent2i actualExtent, long attachmentEpoch)
        implements ResizeObservation {}
}
public sealed interface ResizeObservationResult {
    record Recorded(boolean abortOpenShaderFrame) implements ResizeObservationResult {}
    record Duplicate() implements ResizeObservationResult {}
    record Rejected(ResizeLifecycleRejection reason) implements ResizeObservationResult {}
    record Failed(FailureId failure) implements ResizeObservationResult {}
}
sealed interface ResizeBoundaryResult {
    record NoChange() implements ResizeBoundaryResult {}
    record Rebuilt(PipelineIdentity identity, PipelineVersion version)
        implements ResizeBoundaryResult {}
    record Off(PipelineVersion version) implements ResizeBoundaryResult {}
    record Rejected(ResizeLifecycleRejection reason) implements ResizeBoundaryResult {}
    record Failed(FailureId failure) implements ResizeBoundaryResult {}
}
public enum ResizeLifecycleRejection {
    WRONG_THREAD, INVALID_EXTENT, STALE_WORLD_EPOCH, STALE_DIMENSION, REGRESSING_ATTACHMENT_EPOCH,
    NO_ACTIVE_WORLD, NOT_FRAME_BOUNDARY, BUILD_IN_PROGRESS
}

record ActiveWorldIdentity(long worldEpoch, DimensionKey dimension) {}
interface ActiveWorldIdentityPublication {
    Optional<ActiveWorldIdentity> current();
}

interface ResizeLifecycleSink extends ResizeObservationPort {
    ResizeBoundaryResult applyPendingAtFrameBoundary(long worldEpoch, DimensionKey dimension);
}
```

`ActiveWorldIdentityPublication` is one atomically replaced, internal composition object:
`FrameHookBridge` is its sole writer, while `ResizeLifecycleSink` receives it as a private
constructor dependency and reads it independently of every submitted payload. `mod.glue.frame`
submits H-RESIZE-02, H-RESIZE-01, and H-FBO-01 observations in hook order on the render thread
through `ResizeObservationPort` and handles every closed result; `FrameDriver` calls
`applyPendingAtFrameBoundary` once with the accepted `FrameBeginSignal.worldEpoch` at H-FRAME-01, after Phase 6
sampling and before Phase 5 resize/clear. The sink retains the newest display/actual extents and
strictly greatest attachment epoch per `(worldEpoch, dimension)`, rejects an observation or
boundary application against no current publication as `NO_ACTIVE_WORLD`, an unequal epoch as
`STALE_WORLD_EPOCH`, or an unequal dimension as `STALE_DIMENSION`. Those outcomes are
mutation-free. An identical authenticated observation returns `Duplicate` without mutation; the
sink coalesces all accepted changes before that boundary into one internal
`PipelineCoordinator` rebuild/publication. An epoch advance during an open shader frame returns
`Recorded(true)`; glue aborts that frame and continues vanilla. Every rejection or failure is
mutation-free, performs no publication, and follows §6 degradation; no caller receives direct
coordinator access.

```java
public interface FullscreenPassExecutor {
    FullscreenExecutionResult execute(FrameToken frame, FullscreenDraw draw);
}
public sealed interface FullscreenExecutionResult {
    record Completed() implements FullscreenExecutionResult {}
    record Rejected(PortRejection reason) implements FullscreenExecutionResult {}
    record Failed(FailureId failure) implements FullscreenExecutionResult {}
}
public interface InternalPackManifestProducer {
    InternalPackManifest manifest();
}
public enum ContentDigestAlgorithm { SHA_256_V1 }
public record ContentDigest(ContentDigestAlgorithm algorithm, String lowercaseHex) {}
public record InternalPackManifest(PackIdentity identity, ContentDigest digest, List<String> paths) {}

public interface ShaderReloadController {
    ReloadResult request(DriverReloadRequest request);
    ReloadStatus status(ReloadToken token);
}
public sealed interface ReloadIntent {
    record Select(PackSelection selection) implements ReloadIntent {}
    record RebuildActive(PipelineIdentity expectedActive) implements ReloadIntent {}
}
public record DriverReloadRequest(
    ReloadIntent intent, ReloadReasons reasons, long intentSequence) {}
public sealed interface ReloadResult {
    record Accepted(ReloadToken token) implements ReloadResult {}
    record Coalesced(ReloadToken token) implements ReloadResult {}
    record Rejected(ReloadRejection reason) implements ReloadResult {}
}
public enum ReloadRejection { WRONG_THREAD, INVALID_REQUEST, SHUTTING_DOWN }
public sealed interface ReloadStatus {
    record Queued() implements ReloadStatus {}
    record Building() implements ReloadStatus {}
    record Active(PipelineIdentity identity, PipelineVersion version) implements ReloadStatus {}
    record Off(PipelineVersion version) implements ReloadStatus {}
    record Failed(FailureId failure) implements ReloadStatus {}
    record Unknown() implements ReloadStatus {}
}

public record FrameReadiness(
    PipelineIdentity identity, long registryGeneration, long bufferEstateGeneration,
    OptionalLong idRuntimeGeneration,
    TextureOverlayPublicationId texturePublication, long resourceReloadEpoch,
    int consecutiveFinalizedFrames, Optional<FailureId> failure) {}
public record FinalizedFrame(
    long frameId, PipelineIdentity identity, PipelineVersion version,
    Extent2i extent, AnaglyphEye eye, FrameReadiness readiness) {}
public record CameraPose(double posX, double posY, double posZ, double yaw, double pitch) {}
public record FramePoseReport(long frameId, CameraPose actualCurrent, CameraPose actualPrevious) {}
public interface FrameCaptureListener {
    void onFinalized(FrameCaptureView frame);
}
public interface FrameCaptureView {
    FinalizedFrame metadata();
    FramePoseReport poseReport();
    PixelCopyResult copyPixels(PixelCopyTarget target);
}
public enum PixelFormat { RGBA8 }
public record PixelCopyTarget(PixelFormat format, boolean bottomUp) {}
public sealed interface PixelCopyResult {
    record Copied(ImmutableBytes bytes) implements PixelCopyResult {}
    record Rejected(CaptureRejection reason) implements PixelCopyResult {}
    record Failed(FailureId failure) implements PixelCopyResult {}
}
public enum CaptureRejection { EXPIRED, WRONG_THREAD, INVALID_TARGET }

public interface HookReportSource { HookApplicationReport snapshot(); }
public record HookApplicationReport(
    List<HookApplicationRow> rows,
    List<HookApplicationSubreport> subreports) {}
public record HookApplicationRow(
    String catalogId, String target, int expectedCount, int actualCount,
    Set<HookHealthClass> classes, OptionalInt deferredOwnerPhase, HookFallback fallback) {}
public record HookApplicationSubreport(
    int ownerPhase,
    String canonicalFingerprint,
    boolean featureEnabled,
    List<HookApplicationSubrow> rows) {}
public record HookApplicationSubrow(
    String catalogId,
    int expectedCount,
    int actualCount,
    HookSubreportDisposition disposition) {}
public enum HookSubreportDisposition { HEALTHY, FEATURE_DISABLED }
public enum HookHealthClass { CORE, FEATURE, OBSERVER, DEFERRED }
public enum HookFallback { NONE, EVENT, VANILLA, SHADERS_OFF }
public interface UniformSignalBridge {
    SignalResult frame(FrameBeginSignal signal);
    SignalResult camera(FrameToken frame, CameraSnapshot camera);
    SignalResult event(FrameToken frame, UniformSignal signal);
}
public sealed interface UniformSignal {
    record Celestial(Float3 sunPosition, Float3 moonPosition, Float3 shadowLightPosition,
        Float3 upPosition) implements UniformSignal {}
    record Fog(int fogMode, float density, Float3 color) implements UniformSignal {}
    record Blend(BlendStateValue state) implements UniformSignal {}
}
public record BlendStateValue(
    boolean enabled, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha) {}
public sealed interface SignalResult {
    record Accepted() implements SignalResult {}
    record Rejected(HookRejection reason) implements SignalResult {}
    record Failed(FailureId failure) implements SignalResult {}
}
```

`AnaglyphEye` is a closed `:engine` enum over the two vanilla anaglyph eyes: glue derives the
active eye from vanilla per-pass state and `FrameRenderPort.bind` applies/restores its mask
through mod-side vanilla `GlStateManager` state, **not** the P1 facade (color mask is a non-verb).
The non-anaglyph pass carries LEFT; its mask remains vanilla's full mask. Each `UniformSignal`
variant carries exactly the non-identity fields of its Phase 6 binding sample
(`docs/phase6/v1/PHASE_6_DOC.md:593`–`:602`): the bridge copies `worldEpoch`/`frameId` from the
frame's accepted `FrameBeginSignal` and forwards every payload field verbatim —
`Celestial`→`updateCelestial`, `Fog`→`updateFog`, `Blend`→`updateBlend`
(`docs/phase6/v1/PHASE_6_DOC.md:378`–`:383`) — with no resampling. `Float3` is Phase 6's verified
engine record (`docs/phase6/v1/PHASE_6_DOC.md:607`). The four celestial vectors are the active
Phase 8 publication's values, invoked at the H-SKY-02 rotation moment
(`docs/phase6/v1/PHASE_6_DOC.md:1153`); with no authenticated Phase 8 publication (v0.1) no
`Celestial` event is emitted and Phase 6's pending-neutral defaults stand.
`sunAngle`/`shadowAngle`/`rainStrength` are frame/tick-provider values Phase 6 samples itself
(`docs/phase6/v1/PHASE_6_DOC.md:561`–`:562`, `:566`–`:570`), not signal fields, and H-FOG-01's
observed fog start/end are not forwarded because `FogSample` carries no start/end
(`docs/phase6/v1/PHASE_6_DOC.md:598`–`:599`). `BlendStateValue` mirrors `BlendSample`'s
non-identity fields exactly; glue translates the observed `SourceFactor`/`DestFactor` enums into
Phase 6's full-int-domain factor encoding (`docs/phase6/v1/PHASE_6_DOC.md:614`–`:615`).

`Coalesced.token()` is an already-issued controller token and is polled exactly like an
`Accepted.token()`. `status` returns `Unknown` for any token the controller did not issue; that
query is mutation-free. Issued tokens remain queryable through their current closed status; this
contract does not introduce token expiry or a separate supersession history.

`IdPublicationChange.previousGeneration/previousFingerprint` are both empty on first activation and
both present thereafter; partial presence is invalid. `currentGeneration` and its fingerprint must
equal the just-accepted `PublishedIdRuntime`. Every generation change schedules every Phase 10
stamped chunk product from the old generation and invalidates vanilla/compat render-layer
classification, even when the underlying mapping bytes compare equal; consumers authenticate to
the generation, not to a guessed sub-fingerprint. `Completed` is the sole result that permits
Active; rejection/failure follows §5.3 compensation and never leaks a Minecraft object across this
seam.

`BuiltInPassthroughPack` is always constructible and loadable through Phase 3 without a manifest.
`InternalPackManifestProducer` is a separate R7-9-granted capability and must not be implemented,
constructed, or queried until Phase 3 is reverified. `ContentDigest` is immutable
and record equality is its value equality. `SHA_256_V1` requires
exactly 64 lowercase ASCII hexadecimal characters and hashes this canonical byte stream: ASCII
`schmaloogium-internal-pack\0sha256-v1\0` (each `\0` is one zero byte), then every snapshot entry
in ascending unsigned lexicographic order of the exact UTF-8 path bytes defined below; if one byte
sequence is a prefix of another, the shorter sequence sorts first. Snapshot production,
`manifest().paths()`, and digest traversal use this same total comparator.
Each entry is one byte (`0` directory, `1` file), its path's UTF-8 byte length as an unsigned
64-bit big-endian integer, those UTF-8 bytes, and, for a file only, its content length in the same
integer encoding followed by its exact bytes. Each manifest String is exactly Phase 3's granted
`NormalizedPackPath.canonicalString()` projection, and the path
bytes are exactly that String encoded as UTF-8; implicit `toString()` use is forbidden. Until R7-9
is available through a reverified Phase 3, internal-pack manifest/digest production is gated, not inferred.
`manifest().identity()` equals the associated source's `identity()`, and the manifest and digest describe exactly the corpus
returned by every successful `snapshot(limits)`. Insufficient
caller limits reject the snapshot through Phase 3's checked protocol; they never select or hash a
subset.

`Opened` is the only `open` result that creates a live token; both other variants leave the driver
`IDLE`, require vanilla rendering, and forbid all later calls with a fabricated token. `Advanced`
preserves the token and permits the next ordered hook. Every step/scope `Rejected` is mutation-free
and requires the adapter to omit shader work and call `abort` for any still-live frame; `Aborted`
has already drained scopes, aborted Phase 5, released Phase 4/fixed state, and invalidated every
frame/scope token. `Failed` has those same terminal effects and additionally schedules shaders-off
recovery. `Opened.draw` is exhaustive: draw through the acquired shader scope, draw the vanilla
operation under fixed function, or cancel only that named operation. `Closed` consumes exactly its
scope token and reports the restored parent's disposition. `Finalized` commits exactly once and
invalidates all tokens; `AlreadyTerminal` is the mandatory no-op response to the losing TAIL/finally
call. `FrameAbortResult.Aborted` reports completed cleanup, while its `Failed` reports best-effort
cleanup plus shaders-off recovery. No terminal result permits another shader call in that frame.

`IdDynamicsFrameSlot` is construction-installed together with the verified Phase 9 publisher or is
absent together with it before v0.3; partial presence is an invalid composition. Immediately after
Phase 6 accepts a frame, `FrameDriver` calls `afterUniformFrameAccepted` once with the borrowed
current ID runtime. `Completed` permits the next step; `HeldFeatureDisabled` means the slot already
wrote the typed zero held tuple and permits the frame without held delivery; `Rejected` aborts this
shader frame; `Failed` also schedules shaders off. Before every terminal fixed-function release,
the driver calls `resetFrame` with the exact reason. Only `Reset`/`AlreadyNeutral` permit later
shader activation; a reset rejection/failure forces off and no later shader draw occurs until a new
runtime passes the boundary. The slot retains neither frame nor publication and delegates all
sampling/value/stack policy to Phase 9's `PerDrawDynamics` and glue providers.

`ShadowInvocationContext` is a synchronous, invocation-borrowed view. `ShadowFrameView` copies the
current world epoch, driver-assigned frame ID, partial ticks, exact integer passed to the completed
main `setupTerrain` call, unshifted camera position, and same-sample finite sky/sun angles. The
world/frame identity must equal the current token and Phase 6 sample; angles are normalized to
`[0,1)`. Phase 8 may use these values and current publication credentials only during `invoke`,
must not retain or close them, and must return before Phase 7 binds or clears the main estate.

The appended selector/context and texture fields are exact borrowed members of the same Active tuple.
Phase 8 does not close the publication, lease source or execution view; it MUST close an acquired
overlay lease until Bound, or only the transferred closeable TextureBindingSnapshot after Bound.
Phase 8 §§0.7/4.2/5 adopts this R7-12 migration; owner and receiver verification remain open.

`ShadowExecutionBridge` is the sole issuer and owner of the execution credential. Immediately
before `invoke`, the frame driver creates one opaque execution identity, reads the installed slot's
opaque epoch, and calls `open(identity,epoch)`. Open returns `Opened(borrowed view)` or checks
`WRONG_THREAD` before `ALREADY_ACTIVE`; a nested or re-entered invocation never receives a second
credential. `validate(view,identity,epoch)` checks, in order,
`WRONG_ISSUER`, `INACTIVE`, `WRONG_EXECUTION`, `STALE_SLOT_EPOCH`, then `WRONG_THREAD`; `Valid`
proves both supplied identities equal the one currently open. `close(view)` returns `Closed` or
checks `WRONG_ISSUER`, `INACTIVE`, then `WRONG_EXECUTION`; successful close invalidates the view
before returning. One driver-owned `finally` closes it on every post-open exit and before the main
clear. The view is non-closeable to Phase 8, cannot be retained, and authenticates only the dynamic
extent of that one slot invocation.

`NotInstalled` and `Completed` advance to the main clear. `Rejected` is mutation-free and makes
Phase 7 abort this shader frame, restore fixed state, and continue vanilla without forcing the
healthy publication off. `Failed`, a thrown exception, an execution-open/close failure, or a return
after Phase 8 has invalidated a credential is contained as a backend failure: Phase 7 best-effort
aborts, restores state, schedules shaders off, and continues vanilla when safe. Phase 8 owns its
internal failure taxonomy and all shadow camera/traversal/framebuffer/program/uniform policy.

The driver assigns `frameId` and supplies the active registry generation; neither is hook-made.
Inputs reject non-finite time/matrix values and non-positive target extents before mutation. Both
interfaces are `:engine` types. Their values contain booleans, finite scalars, immutable
matrices, extents, closed enums, and verified Phase 1/3/4/5/6 types plus the v0.3 Phase 9 opaque
fingerprint/generation types—never MC, Forge, Mixin, LWJGL, or raw GL integer names. The exact
exposed contracts are:

Every operation above is render-thread-only except `ShaderReloadController.request`, which may be
called on the client main thread and only queues immutable copied values. Null values, blank report
identifiers/targets, negative counts/versions/sequences, empty reason sets, non-positive extents or
instance counts, out-of-range instance indices, and inconsistent optional identities are rejected
before mutation. Returned lists and bytes are immutable snapshots. Frame/scope tokens, capture views
and borrowed publication credentials are non-closeable. Explicit Phase 5 TextureBindingSnapshot and
TextureOverlayLease have the different exactly-one finally closure obligation in §§4.4/5.2;
invalidating pass/frame/publication use does not close them. Candidates and runtimes retain §5.3's
owner-only transfer/retirement rules. A Failed result never transfers partial ownership.
`Rejected` is mutation-free unless its variant explicitly reports a
previously accepted queued token. Dependency-produced non-empty diagnostic IDs are copied verbatim
into `FailureId`; Phase 7 never sanitizes, rewrites, or assigns them a longer lifetime.

`HookApplicationRow.classes` is non-empty and contains every class on its catalog row. It may contain
multiple values; no row is split or collapsed. `deferredOwnerPhase` is present exactly when
`classes` contains `DEFERRED` and preserves that row's `Pn`; otherwise it is empty.
For Mixin rows counts are successfully applied injection anchors. H9-HELD-01's target explicitly
names the accepted-frame participant slot, whose 1/1 count is construction-time registration and
never pretends to be a Mixin. The three P9 ID/held rows and two P7 color rows obey §4.12;
color has 1/1 anchors at v0.1 independently of P9's later 1/1, 4/4 and 2/2 activation.
H-BOOT-01 is explicitly a two-event delivery row, not an injection count; its actual count is
observed preInit/FMLLoadCompleteEvent deliveries before report freeze, and never inferred from
successful frames or from presence/absence of a GameSettings Mixin.

`HookApplicationReport.rows` remains the canonical Phase 7 list. `subreports` is immutable and
sorted by unique positive `ownerPhase`; each fingerprint is exactly 64 lowercase hexadecimal
digits, and each subreport's rows are immutable, non-empty, and sorted by unique catalog ID. The Phase 8 adapter
copies `ShadowHookHealth` into owner phase 8 without changing an ID, count, aggregate enabled bit,
or disposition. Missing owner phase 8 is absence, never a successful audit. The report is frozen
before first frame and is copied verbatim into Phase 2's manifest projection.

At bootstrap, `FrameDriver` receives exactly one `Optional<FrameCaptureListener>` construction
dependency; absence means capture is disabled for that driver, and the listener cannot be replaced
or removed at runtime. For each frame whose final pass completes successfully, the driver invokes
the installed listener exactly once on the render thread at H-CAPTURE-01, before presentation.
The view expires when that call returns. A listener exception is contained as a capture-feature
failure, is reported to the Phase 2 host, and does not prevent Phase 5 commit or schedule shaders
off; Phase 2 retains the artifact-failure and shot-acknowledgement policy in §6.

`FramePoseReport.frameId` equals metadata.frameId; both CameraPose values are non-null immutable
finite values in P2's world-space position/yaw/pitch convention. P7's mod camera adapter records
the actual camera values at the same sampling/camera-install moments used for the rendered frame.
History follows P6's accepted frame-begin rotation, not completion or provider-read count; a
world reset initializes previous=current only as P6 specifies. Yaw/pitch are recorded from the
same actual mod camera samples, never the plan. Repeated warm-up establishes sample-zero history.
Pose report contains no camera object or live
buffer, may be copied for manifest construction, and is available only while FrameCaptureView
is live on the render thread. Expired/off-thread access fails the capture feature, never returns
a made-up pose. Exact normalized equality and formatting remain P2-owned.

`FrameReadiness.registryGeneration` equals the `PublishedRegistry.generation` accepted for the
frame, and `bufferEstateGeneration` equals the accepted `BufferEstateView.generation()` used for
that frame. `idRuntimeGeneration` is empty before the v0.3 participant activates and otherwise
equals the `PublishedIdRuntime.generation()` whose aliases and per-draw dynamics were used. The
present generations are independent equality tokens and are not compared to each other.
`texturePublication` equals the active Phase 13 publication ID (its generation is estateGeneration,
not registryGeneration), and resourceReloadEpoch equals the transaction's accepted source epoch.
Readiness also requires this publication's registry/configuration/policy pairing and current lease
source/registrations, not merely equal hashes. Old leases delay deletion but cannot satisfy readiness.
`FinalizedFrame.version` separately identifies the coordinated Phase 7 publication.

`DriverReloadRequest` is the internal selection/world/remap adapter only. Select requires
PACK_SELECTION; RebuildActive requires exact manager-owned active identity and forbids it.
Reasons are immutable, nonempty diagnostics; they do not override P12 lifecycle/flag effects.
Only the composition root pairs selection/configuration/ID snapshots, and RESOURCE_RELOAD
always takes the retained-configuration branch below unless merged with an actual stronger request.

#### Reload, diagnostics and current-bind adapters

The mod-side resource continuation boundary and every exceptional/nested-entry rule in §4.8.1
are binding here. `submit` is not that gate: resource notifications inside H-RESOURCE-01 join
its frozen transaction, while non-resource submissions remain queued for the next drain.

P7 implements P12's exact `engine.config.ReloadCoordinator` without redefining its value:
`void submit(ReloadRequest request)`, where
`ReloadRequest(ReloadLifecycle lifecycle,boolean worldRendererReload,boolean resourceReacquire,
ReloadCause cause)` and lifecycle is `NONE|REPUBLISH|FULL`. Submit copies a valid immutable
request into one pending slot on the client thread; malformed/null/off-thread submission is
rejected before mutation by `IllegalArgumentException`/`IllegalStateException`, respectively.
P12 adapters marshal to that thread; this validation is not a publication attempt.
Effects merge by max lifecycle and OR flags; latest cause is diagnostics-only. Selection and
pending option state are frozen from the P12-owned edit/selection model when the slot drains,
never paired with caller-made configurations. Newer selection supersedes older selection,
not accumulated resource/geometry flags. Accepted submissions during a drain form the next slot.
The selected profile choice is frozen alongside its committed option batch in that private P12
model. P7 evaluates the new configuration with that profile through P3's current evaluation
operation so disabledPrograms is honored; no preview OptionState enters materialization.

The render-thread safe-boundary drain closes admission and drains prior frame, shadow,
binding and P10 worker uses before destructive work. Matrix translation is exact:

| P12 trigger | P7 effects |
|---|---|
| F3+R, `/reloadShaders`, selection including Off/Internal | FULL + worldRendererReload |
| option apply/Done, profile, reset | REPUBLISH + worldRendererReload iff effective bake inputs change |
| normalMapEnabled/specularMapEnabled | REPUBLISH + resourceReacquire |
| renderResMul/shadowResMul/handDepthMul/oldHandLight | REPUBLISH |
| oldLighting | REPUBLISH + worldRendererReload iff effective bake inputs change; post-load policy comparison ORs any newly discovered required rebuild |
| resource-manager reload | NONE + resourceReacquire |
| view navigation/discard | NONE without flags; no publication |

FULL discovers then calls `resolveFilesystemCandidate(reference,currentDiscoveryResult)`;
Resolved supplies a PackCandidateId, whose row is looked up in that same snapshot before
`packOptionsTarget(id)`. REPUBLISH loads the unchanged authenticated selection without discovery.
Missing/Ambiguous/KindChanged/InvalidSnapshot is explicit off/failure, never display-name matching.
Off/Internal sentinels remain separate. Safe target acquisition and codec work use one P3 bundle
and its authenticated roots; no consumer serializes PackCandidateId. Load uses current schema,
required companion state and P3 finalized persisted options (§4.1). No load returns an older
configuration as a fallback.

NONE without flags returns the unchanged final state, with no P3/P4 publication. Resource NONE
retains **the same PackConfiguration, OptionConfiguration and configuration fingerprint**:
H-RESOURCE-01 first stops admission, drains bindings/shadow/workers, retires P13 registrations
and acquisition authority, invalidates atlas/source/animation/foreign identities and advances
resourceReloadEpoch before vanilla replacement (§4.8.1). After the original returns, freeze
new P13 sources and P9 registry/mod/tag/policy snapshots, rebuild P13 against retained P4/P5 identities, register current
sizing, then publish any changed P9 candidate and required geometry invalidation. Recreate the
P8 invocation publication if its borrowed services changed; never retire or replace P6
merely because resources changed. Reset world/resource-dependent P6/P11 state through their
existing nonterminal reset semantics, not retirement. P4/P5 may remain unchanged; if an actual
estate/registry requirement independently changes, rebuild those runtime components from the
same configuration and record their actual generations—still no P3 discover/load. Resource
failure invokes coherent off compensation, never stale resource reuse. OR flags are honored even
when a stronger lifecycle subsumes their acquisition work; each owner action executes once.
worldRendererReload with no loaded world is a recorded no-op. A loaded-world flag and P10
effective-bake/ID invalidation are combined into one quiescent full model/mesh invalidation before
admission, not two renderer reloads.

Coalescing promises one drained request and one **final composition outcome**, not one P4 bump.
P7 construction-installs immutable listeners before first publication:

```java
public interface RegistryGenerationListener { void changed(long generation); }
public record ReloadDrainReceipt(long drainSerial, ReloadRequest effects,
    ReloadStatus finalOutcome, List<Long> observedRegistryGenerations) {}
public interface ReloadOutcomeSource { Optional<ReloadDrainReceipt> latest(); }
public interface ExpressionDiagnosticGuiSource {
    Optional<ExpressionDiagnosticGuiSnapshot> current();
}
public record ProgrammaticOptionSnapshot(PipelineIdentity identity,
    OptionConfiguration options, EngineOptionData engineOptions) {}
public interface ProgrammaticOptionBridge {
    Optional<ProgrammaticOptionSnapshot> current();
    ProgrammaticApplyResult apply(PipelineIdentity expected,
        Map<String,String> packOptions, Map<String,String> engineOptions);
}
public enum WriteDisposition { UNCHANGED, COMMITTED, FAILED, NOT_ATTEMPTED }
public record OptionPersistenceReceipt(WriteDisposition pack, WriteDisposition global) {}
public sealed interface ProgrammaticApplyResult {
    record Queued(ReloadToken token, OptionPersistenceReceipt persistence)
        implements ProgrammaticApplyResult {}
    record Rejected(ProgrammaticApplyRejection reason) implements ProgrammaticApplyResult {}
    record FailedPersistence(FailureId failure, OptionPersistenceReceipt persistence)
        implements ProgrammaticApplyResult {}
}
public enum ProgrammaticApplyRejection {
    INVALID_REQUEST, STALE_CONFIGURATION, UNKNOWN_OPTION, INVALID_VALUE,
    UNKNOWN_ENGINE_KEY, SHUTTING_DOWN
}
```

P7 samples `ProgramRegistryPublisher.current().generation` after **every** publication result
and synchronously notifies all generation-sensitive P12/diagnostic caches of each actual change,
before further use/admission. Listeners only invalidate detached caches; no GL/reentrant reload.
Failed delivery leaves that cache invalid/unavailable until it polls authoritative current
generation; it cannot retain an older usable cache. Successful Ready normally records g+1;
mutation-free rejection records no change, but a required compensating Off records its actual
increment; Ready followed by post-estate/texture failure and Off can record g+1 then g+2.
RecoveredOff and explicit Off record whatever the publisher actually reports, never a fabricated
increment or suppressed event. P5/P9 generations and PipelineVersion are independent.
Receipt generations are an immutable ordered list of these events; drainSerial orders attempts,
not rendering generations. `latest()` is absent before a completed drain. Only terminal
Active/Off/Failed enters finalOutcome; failure includes final off compensation before receipt.
Every queued/coalesced ReloadToken observes the shared final outcome via status; no receipt
claims success at intermediate Ready. Unchanged NONE may return the unchanged Active/Off version.

The programmatic bridge is P12's headless adapter to **the same** catalog validation, safe
persistence, effect classifier and queue. current is absent with no active configuration.
Apply is client-thread-only; non-null immutable maps are requested deltas against exact expected
active identity, validated in full before writes. P3 catalog constructs updated complete state;
unknown/invalid pack options and unknown engine keys reject (unknown-safe persistence keys are
not executable settings). Decode only P3/P12 current key domains. Rejection changes nothing.
For Internal, P3 supplies baseline pack options and ignores pack persistence; nonempty
programmatic packOptions deltas reject INVALID_REQUEST before any write. No synthetic target
or transient override exists. Global-only Internal settings remain supported through EngineOptionData.
Decoded oldHandLight remains tri-state through this adapter into P9 §4.11: explicit user
TRUE/FALSE wins, else explicit pack TRUE/FALSE, else P9's local true fallback. P7 never
derives that runtime Boolean from MC_OLD_HAND_LIGHT presence or a GUI default.
Persist explicitly, pack first then global, skip unchanged domain; enqueue only after all
required writes COMMITTED. A failed write returns FailedPersistence with each domain's
UNCHANGED/COMMITTED/FAILED/NOT_ATTEMPTED status, preserves the complete pending edit overlay,
does not reload and does not roll back a previously committed file. Retry writes the same
desired complete values. No cross-file atomicity is claimed. P2 opts into isolated copied game/
shaderpacks roots and polls token completion before rendering; there is no transient OptionState
argument to P3 load/materialize. Global settings-only changes still supply EngineOptionData to
the subsequent load. Same-value apply may enqueue NONE and return its normal final receipt.

**Lighting policy (IR-05).** P7 resolves and freezes P10
`ShaderLightingPolicy(boolean oldLighting,boolean separateAo)`: explicit user oldLighting
true/false wins; DEFAULT defers to explicit pack value; both DEFAULT use true. separateAo
uses explicit pack value else false. These final defaults are phase-local P10/P7 decisions
pending conformance, not newly verified external facts. Freeze the pair with P3 vertex
requirements in the P10 bake/transition identity. Any effective pair change quiesces workers,
invalidates model/mesh products and completes worldRendererReload before shader admission.
P10 owns directional lighting and AO-to-alpha behavior; P7 does not rewrite chunk renderer
performance or infer policy from a mesh rebuild. User key names are exactly renderResMul,
shadowResMul, handDepthMul, normalMapEnabled, specularMapEnabled, oldHandLight, oldLighting,
antialiasingLevel; last is reserved zero-only with no AA runtime/UI. No legacy aliases.

**Expression diagnostic delivery (IR-17).** P11 produces the immutable source-free
`ExpressionDiagnosticGuiSnapshot(packFingerprint,configurationFingerprint,attemptSerial,
outcome,entries)`, outcome ACCEPTED|REJECTED; each entry is
`(stableId,kind,severity,declarationName,summary)`, severity WARNING|ERROR, kind and sanitized
template summaries remain P11-owned. After final composition P7 invokes P11
`ExpressionDiagnosticGuiProjector.project(packFingerprint,configurationFingerprint,attemptSerial,
outcome,diagnostics)` with ACCEPTED only for final admitted pipeline, REJECTED for failed/
compensated-off attempt. Attempt configuration fingerprint comes from that P3 load, positive
serial from P7 selected-pack session. A successful expression compile alone cannot choose ACCEPTED.
No compile attempt means no new snapshot. P7 publishes through its construction-installed
ExpressionDiagnosticGuiSource; P12 polls current() with ReloadOutcomeSource when presenting/
refreshing issues, not every frame. Empty clears. New selection/explicit Off/shutdown clears; same-pack failed
attempt may remain REJECTED while shaders are off. Older serials/different selected-pack views
are rejected by P12. WARNING→warning and ERROR→error styling only; existing CHAT_AND_LOG/
LOG_ONLY reporting remains separate, all direct GUI summaries are P11 source-free templates.
No P1 SHADER_GUI store conversion or fourth P11 channel is assumed.

**Authenticated current atlas (IR-21).**

```java
public interface AtlasBindingEvidence {} // opaque, private permitted implementation
public interface AtlasBindingSink {
    SignalResult currentBinding(AtlasBindingEvidence evidence);
}
public interface IdScopeAdmission {} // opaque, private P7-issued implementation
```

The mod binding-observer alone mints AtlasBindingEvidence after a successful **actual**
base-texture bind/restoration, whether vanilla or a Phase5 Bound operation. Private evidence
captures optional P13 AtlasId, current PipelineVersion, resourceReloadEpoch and monotonically
increasing bindSerial. The sink validates issuer, render thread, current composition/epoch,
then latest unconsumed serial before querying P13 `atlasSize(id)`; invalid/foreign/stale evidence
returns SignalResult.Rejected(WRONG_TOKEN or STALE_PUBLICATION), with no mutation. P6 sink
failure returns Failed; no successful upload is invented. Known emits existing
`updateAtlasSize(Int2(width,height))`; Unknown/non-atlas emits `Int2(0,0)`.
P6 immediately updates an active program or caches until next activation when absent.
Off/reload/unbind resets 0,0 at the live quiescent runtime before retirement; new runtime
starts Unknown. Never call a retired sink. Stitch availability alone emits no bind event.
No assumption that gbuffers/shadow uses the base atlas is legal; P5 alone physically binds shader textures.

**Shadow ID admission (IR-22).** P7 issues an IdScopeAdmission only for the current accepted
main entity/block ScopeToken **or** current ShadowExecutionBridge.validate(...)=Valid. The
private token binds frame/pipeline, ID generation, admission kind, execution/slot epoch for
shadow and stack depth. P9 `enterEntity(admission,entityOrdinal)` /
`enterBlockEntity(admission,stateOrdinal)` return Entered(IdScopeToken) or
Rejected(STALE_ADMISSION|STALE_GENERATION|WRONG_THREAD|STACK_LIMIT); leave validates LIFO.
Shadow admission opens no gbuffers scope/snapshot, selects/activates no main program and keeps
P8 Forge pass 0/1 ordering. Nested finally restores preceding ID/color before shadow release;
stale/unbalanced/throw drains IDs to zero and follows existing frame/off containment. Foreign
vanilla calls never obtain an admission. P9 remains the ID value/token owner, P7 admission issuer.

| Exposed contract | Exact content | Consumer(s) |
|---|---|---|
| `FrameDriver` / `FrameHookSink` | render-thread transaction owner; closed open/step/scope/finish/abort results; idempotent normal/finally finish | vanilla Mixin/event adapter; future Kirino adapter |
| `IdDynamicsFrameSlot` and closed accepted/reset results | optional v0.3 slot paired with the Phase 9 publisher; held delivery exactly after Phase 6 frame acceptance and neutral reset before every terminal fixed-function release, without moving value policy into Phase 7 | Phase 9 `PerDrawDynamics`/glue adapter |
| `FrameToken` / `ScopeToken` | opaque frame/epoch/generation and balanced-scope credentials; equality only; invalid after finish/abort/publication | `mod.glue.frame` call stack only |
| `FrameBeginSignal` / `CameraSnapshot` / `ShadowFrameView` / `FrameExitKind` | immutable frame/pass/time/dimension signal, copied main-camera matrices, exact driver/main-terrain token plus same-sample unshifted camera and sky/sun values for Phase 8, and `NORMAL`, `EARLY_RETURN`, `THROWN` exit classification | hook adapters; Phase 6 producer bridge; Phase 8 |
| `RenderSection` | closed vanilla-phase vocabulary mapped to §3.2's exact pack-facing slots, including terrain variants and nested effects; no backup logic | hook adapters; Phases 8/9 may submit owned sections |
| `DimensionPipelineCache` / `DimensionPipelineRecord` / `PipelineVersion` | map by Phase 3 `DimensionKey`; immutable PlanOnly/ReadyUnpublished/Active metadata; equality-only version counter | Phase 12 diagnostics; driver |
| `FullscreenPassExecutor` / `FullscreenDraw` | typed execution of deferred/composite/final descriptors, mipmap set, viewport scale, instance index/count, QUADS-or-triangle-strip primitive choice | driver; recorded-GL tests |
| `BuiltInPassthroughPack` / `InternalPackSource` | always-available Phase 3 source protocol: stable identity and limit-bounded snapshot throwing `InternalPackReadException` for Phase 3 to map to `INTERNAL_SOURCE_INVALID` | bootstrap; Phase 3 loader |
| `InternalPackManifestProducer` | separate deterministic GPL manifest/digest capability using granted `NormalizedPackPath.canonicalString()`; unavailable pending Phase 3 reverification | Phase 2 headless golden input |
| `ShaderReloadController` / internal `DriverReloadRequest` / `ReloadIntent` / `ReloadResult` / `ReloadToken` / `ReloadStatus`; P12 `ReloadCoordinator`; receipt/programmatic adapters above | one main-thread queue, exact max/OR merge and render-thread drain; configuration-preserving resource refresh; final outcome and every actual P4 generation invalidation | Phase 12 GUI/command, P2 capture, resource/ID glue |
| `IdDependentGeometryInvalidator` / `IdPublicationChange` / closed result | synchronous safe-boundary gate after an accepted Phase 9 generation change; schedules alias/layer-dependent vanilla and Phase 10 chunk products before another shader frame, with no registry object crossing the seam | Phase 10 and `mod.glue` chunk scheduler |
| `FrameReadiness` | exact schema above: pipeline identity, independent registry/estate/optional ID generations, TextureOverlayPublicationId and resourceReloadEpoch, finalized count and failure; full coherent texture pairing is mandatory | Phase 2 capture agent |
| `FinalizedFrame` / `FrameCaptureListener` / `FramePoseReport` | exactly-once final-before-presentation callback; borrowed view plus immutable actual current/previous pose report from rendered camera sampling, copied for P2 `/2` every dense sample | P2 capture agent |
| `HookApplicationReport` / `HookApplicationSubreport` | complete current primary catalog including three P9 held/ID rows and two P7 v0.1 color rows with stable H9 spellings; nested owner subreports copied unchanged; no health inference | diagnostics, P2 manifest, P8/P10 |
| `UniformSignalBridge` / `AtlasBindingSink` | frame sample before resize/clear, current matrices only after camera setup; Celestial/Fog/Blend one-to-one P6 updates; authenticated current-bind atlas adapter above; P7 v0.1 hurt/flash color, P9 v0.3 held/IDs | P6 integration; P13 atlas query; P9 value owner |
| `ShadowInvocationSlot` / `ShadowInvocationContext` / `ShadowInvocationResult` | exact §5.1 field order: frame, shadowFrame, camera, registry, buffers, barrierContexts, execution, selection, activationContext, texturePublication, textureLeases; borrowed invocation values; closed NotInstalled/Completed/Rejected/Failed; R7-12 gate and separate Phase-8-owned binding closure | Phase 8 |
| `ShadowExecutionBridge`, `ShadowExecutionIdentity`, `ShadowExecutionView`, and closed open/validate/close results | Phase 7 is sole issuer/owner; one non-nestable dynamic-extent view per installed-slot invocation; exact validation order and `finally` invalidation before main clear; terrain/entity/cloud/frustum main-policy bypass requires `Valid` for the current execution and slot epoch | Phase 8 `mod.glue.shadow` and guarded existing hook adapters |
| `ResizeObservationPort` / `ResizeObservation` / `ResizeObservationResult` / `ResizeLifecycleRejection` | authenticated render-thread display/attachment observations with closed `Recorded`/`Duplicate`/`Rejected`/`Failed` results; duplicate/rejection/failure are mutation-free, `Recorded(true)` aborts the open shader frame, and accepted changes coalesce into one frame-boundary rebuild/publication | `mod.glue.frame` |
| `ActivePipeline` / `SelectedPassContext` (internal) | exact §2.2 field lists incorporated unchanged; privately held texture owner, non-owning publication/lease source and same selection/context; atomic admission only after §5.3 | coordinator, frame driver |

No exposed contract contains a `ProgramHandle`, framebuffer GL name, physical ping-pong side, parsed
source, or Minecraft object. `RenderSection` selects a requested Phase 4 slot; Phase 4 alone resolves
its effective provider.

### 5.2 Dependency contracts consumed

#### Phase 2

| Consumed contract | Use |
|---|---|
| T0–T3 definitions and named-run catalog | §8/§9 implementation and milestone gates |
| scene/capture-plan wire contract | capture-agent input and deterministic shot boundary |
| run-manifest wire schema, including complete `programs.*`, `resources.*`, `gl_errors.*`, and `hooks.*` grammars | serialization target; copy each owner projection directly |
| capture-plan provenance bridge | transport acquisition mode, archive SHA-512, and licence verbatim into the manifest |
| accepted `ReplayAwareGLError` flow | copy Phase 6's replay result; never infer attribution |
| determinism ledger | every new clock/frame/world input is recorded rather than hidden |

Phase 7 does not redefine a pass condition, baseline, scene, timeout, or fixture-acquisition rule.
It accepts R18 by exposing and serializing only the frozen report described in §5.1; successful
frames, images, programs, and diagnostics never synthesize hook health.
The consumed Phase 2 surfaces are `docs/phase2/v2/PHASE_2_DOC.md` §§4.5/5; R19 is adopted here.

#### Phase 3

| Consumed contract | Use |
|---|---|
| `PackFrontEnd`, discovery/load requests/results | bootstrap, selection, reload |
| `PackConfiguration`, `PackIdentity`, `DimensionConfiguration`, fingerprint/version discipline | sole configuration and per-dimension cache truth |
| current-schema `IdMappingInput` (17), nested version equals containing configuration | handed unchanged into Phase 9; reject every other schema before derived state, never infer a schema-16 upgrade |
| `ProgramStateModel`, `ProgramStateEvaluationResult`, `EvaluatedProgramStates` | evaluate using containing configuration's finalized catalog-issued state; no OptionState argument or properties reparse |
| `ResourceRequirements` | Phase 4/5/6 build inputs and Phase 7 world constants/routing |
| `InternalPackSource` / `InternalPackSnapshot` | Phase 7 supplies the built-in bytes through the Phase 3-owned protocol |
| `centerDepthMacroContributor` slot | pass Phase 6's empty contribution into materialization |
| `CompanionOptionMacros`, `MacroConfiguration`, catalog-bound `OptionConfiguration`, `SourceMaterializer` | required same-load companion pair and finalized state; no caller option/materialization override; P3 owns fingerprints and macro emission |
| `properties().textureDeclarations()` and typed executable textures/noise | preserve lossless owner declaration stream; P13 consumes typed specs only, no discarded-suffix reconstruction |
| safe `PackFrontEndServices`, persistence access/target and `FilesystemCandidateReference` resolver | bundle-domain-authenticated load/persistence; restore durable reference against fresh discovery, never display text or serialized candidate IDs |

Current Phase 3 §5.1 and incorporated declarations plus §5.3 schema17 are consumed
architecturally, unverified; fresh whole-document owner/receiver reviews and R7-9 production
gate remain. Older schema defaults, source/option rebinding and raw persistence targets are forbidden.

#### Phase 4

| Consumed contract | Use |
|---|---|
| `ProgramRegistryCompiler`, `RegistryBuildRequest`, opaque `CompiledRegistryCandidate` and detached `view()` | candidate compilation and prepublication Phase 5 planning |
| `StageRegistry`, `StageStep`, `PassDescriptor`, `ProgramSlotDescriptor`, `ResolvedProgramDescriptor` | data-driven schedule traversal and requested/effective program separation |
| `ProgramStateBundle` | effective per-slot state, scale, flips, and instance projection; Phase 7 never overlays requested state or re-resolves it |
| `ProgramRegistryView.resolutions()` | immutable complete `programs.*` manifest projection copied directly |
| `ProductionBarrierComposer` and exactly the three Phase 6 participants | authenticated barrier candidate composition |
| `ProgramRegistryPublisher.publish/current` | publish/off transaction, generation checks |
| `PublishedProgramStateBarrier` and Phase-4-issued `FrameBarrierContexts` | every activation/release; no direct program bind |
| per-slot scale/mipmap/instance metadata | fullscreen executor inputs |

Phase 7 incorporates the coordinated Phase 4 §2/§5 selection contract: `select(ProgramSlotId,
BarrierContext) -> ProgramSelectionResult.Selected(ProgramBindingSelection)|Skipped(ProgramSlotId)|
StalePublication(expectedGeneration,currentGeneration)|ShadersOff(diagnosticId)`.
`activate(UseProgramRequest(ProgramBindingSelection selection,BarrierContext context))` uses the
retained binding, not resolve. `ProgramBindingSelections.validateSelection(selection,context)`
returns `ProgramSelectionValidation.Valid|Rejected(INVALID_ISSUER|STALE_GENERATION|STALE_CONTEXT|
WRONG_STAGE_BAND|PROVIDER_LAYOUT_MISMATCH)` with no GL. Context/frame invalidation, replacement,
off and close expire selection; immediate activation does not. Fixed/virtual layout variants remain
explicit; descriptor/layout fingerprint equality alone cannot authenticate a selector. New
samplerPolicy follows macroContribution in RegistryBuildRequest; registry view retains policy identity
even when empty. Phase 4 §5 is changed/unverified by this rebuild, not certified by its earlier PASS.
The source contracts are `docs/phase4/v1/PHASE_4_DOC.md:1785–1796`, including
“resolve is detached handle-free inspection, not selection authority” at line 1787 and the
exact selection/result/validation exposure at line 1796.

#### Phase 5

| Consumed contract | Use |
|---|---|
| `BufferArchitecture.plan/create`, runtime inputs, opaque `BufferEstateCandidate`/inspection | derive paired estate from detached Phase 4 candidate view |
| `BufferEstatePublisher` / `PublishedBufferEstate` | second half of the coordinated publication |
| `beginFrame`, `snapshot(pass,selection)`, `bind`, `executeClear`, `completePass`, `discardPass`, `commitFrame`, `abortFrame` | same selected frame/pass transaction; Acquired alone does not authorize drawing |
| `refreshMainDepth` / `MainDepthSource` | framebuffer epoch/resize coordination after Phase 6 begin |
| `copyDepth` / `DepthCopyResult` | v0.5 PRE_WEATHER then PRE_TRANSLUCENT points |
| `applyVirtualTransition` / `VirtualTransitionResult` | programless `deferred_pre` and `composite_pre` flips |
| `openDrawBuffersNone` / balanced lease results | first-person overlay routing |
| `BufferResourceProjection` / `resources()` | immutable complete `resources.*` manifest projection copied directly |
| `PassDrawTarget`, including payload-free `PassDrawTarget.Screen.INSTANCE` | unchanged snapshot draw target and anaglyph-aware final handoff |
| `textureBindings(PassBufferSnapshot,TextureOverlayLease,TextureOverlayPublicationId)` | actual Phase-5-owned sixteen-row object binding; snapshot retains mandatory selection; closed Bound/Degraded/Rejected/BackendFailed and exactly-one ownership branch below |
| `shadowBindings(long generation,long frameId,ShadowPassSnapshot,TextureOverlayLease,TextureOverlayPublicationId)` | current full operation adopted in P8; no historical four-row result |
| `FixedSamplerPolicies.appB3()` / `.resolver()` | pure policy before registry/estate; sole schema/map; adopted P6 factory input |
| `BufferResizeNotice`, consumer, registration/delivery/result contracts | §5.3 step7 full synchronous outcome and step8 prepared new texture registration, preserving deliveredCount |

Phase 5 §2/§4.12/§5 owns the exact incorporated candidate/lease/result protocol, newly changed and
unverified. `snapshot(PassDescriptor,ProgramBindingSelection)` and shadow
`beginPass(frameId,pass,selection)` freeze selection plus physical sides; shadow readableTextures is
the immutable complete ordinary+shadow `Map<LogicalBuffer,TextureHandle>`, never a late main-side
read. `discardPass(PassBufferSnapshot) -> PassDiscardResult.Discarded(long frameId)|
Rejected(FrameProtocolRejection reason)` authenticates like completePass but consumes only an
undrawn pass, with no flip or post-draw mipmap work.
The exact producer signatures and incorporated lifecycle are at
`docs/phase5/v1/PHASE_5_DOC.md:2344–2375`; the binding row at line 2363 states
“Bound alone transfers lease into closeable sixteen-row snapshot with BoundObject/Unused”.

`TextureBindingResult` is exactly `Bound(TextureBindingSnapshot)`,
`Degraded(TextureBindingDegradation)`, `Rejected(TextureBindingRejection)`, or
`BackendFailed(BufferFailure failure)`. Degradation names the exact affected selection, ordered
diagnostics and SUPPRESS_DRAW. Rejected/Degraded bind nothing and transfer no lease; BackendFailed
may follow partial GL, retains/transfers nothing and forbids activation/upload/draw. Bound proves
every required bind completed and transfers the lease to its idempotently closeable snapshot.
Sixteen immutable ascending rows carry BoundObject or Unused, with BindingPurpose SHADER,
FIXED_FUNCTION_PASSTHROUGH or NONE. Compatible base fallback may warn; missing required backing
cannot produce a drawable row. Phase 7 never iterates rows to bind physical objects.

Preflight is mutation-free: input/thread, current estate generation, open frame/explicit frameId,
issued pass/depth epoch, Phase 4 issuer/current generation/context/pass/provider/stage/band/layout,
live current overlay lease, expected publication ID, registry fingerprint, configuration/estate/
policy pairing, then full-shape resolution. First failure wins, with publication-ID-before-registry
and shadow generation-before-frame-before-snapshot precedence. `TextureOverlayLease.isCurrent()`
means open AND owning publication currently READY; stale content-equal leases are CLOSED_OVERLAY_LEASE.

The consumed rejection algebra is closed: INVALID_INPUT, WRONG_THREAD,
STALE_ESTATE_GENERATION, STALE_DEPTH_ATTACHMENT_EPOCH, NO_OPEN_FRAME, WRONG_FRAME_ID,
INVALID_PASS_SNAPSHOT, INVALID_PROGRAM_SELECTION, PROGRAM_SELECTION_MISMATCH,
STALE_REGISTRY_GENERATION, SAMPLER_LAYOUT_MISMATCH, CLOSED_OVERLAY_LEASE,
OVERLAY_PUBLICATION_ID_MISMATCH, REGISTRY_FINGERPRINT_MISMATCH,
CONFIGURATION_FINGERPRINT_MISMATCH. The ordered diagnostics remain distinct:
NO_CANDIDATE, INCOMPATIBLE_CANDIDATE, CONFLICTING_SAMPLER_TYPES, CONFLICTING_CANDIDATES,
UNSUPPORTED_SAMPLER_NAME, UNSUPPORTED_STAGE_DOMAIN, UNSUPPORTED_SAMPLER_SHAPE,
PUBLICATION_UNAVAILABLE, NOT_CONFIGURED, NOT_APPLICABLE_TO_STAGE, MISSING_BACKING.
Phase 7 copies these typed results verbatim: compatible base fallback is a Bound diagnostic,
missing required backing is Degraded(SUPPRESS_DRAW), and publication absence is not empty candidates.
Candidate selection uses the exact effective layout/name; compatible Custom with greatest Phase 3
canonical ordinal wins, not original property occurrence order. Aliases cannot rename source keys:
same-unit incompatible types conflict, and same-shape distinct winning object/source/parameter
identities conflict rather than traversal-order override. No free-unit search, scalar coercion,
per-draw byte hashing or fallback retry occurs. Undeclared shader rows are Unused.

#### Phase 13 downstream slot

Consume `docs/phase13/v1/PHASE_13_DOC.md:1147–1163` only after fresh owner verification.
The exact frozen request at line 1150 contains configuration, registry, atlases, sources,
companionPolicy, macroState, capabilities, registryFingerprint, estateGeneration, registryGeneration,
resourceReloadEpoch in that order and the types declared there. `TextureBuildRequest(TexturePlanRequest
planRequest,TextureBuildSources sources)` validates prepared identities before allocation; macroState preserves the pre-jcpp choice.
`TexturePublication` retains id, registryFingerprint, registryGeneration, resourceReloadEpoch,
plan and TextureCandidateTable; no lookup/close authority is delegated by that view.
`TextureLeaseSource.lease(TextureOverlayPublicationId expected,ProgramBindingSelection selection)`
and owner `TextureSystem.lease` return exactly
`TextureLeaseResult.Acquired(TextureOverlayLease lease)|Rejected(TextureLeaseRejection reason)`;
reasons are PUBLICATION_UNAVAILABLE, PUBLICATION_ID_MISMATCH, REGISTRY_FINGERPRINT_MISMATCH,
STALE_SELECTION. Validate active owner/expected ID/selection before incrementing count.
There is no public two-call lease()/publicationId() path. Retire stops acquisitions and invalidates
drawing; outstanding leases defer owned deletion without blocking render-thread close.
Borrowed foreign handles are released, never deleted.

#### Phase 6

| Consumed contract | Use |
|---|---|
| `UniformRuntimeFactory` / `UniformRuntime` | one runtime per pipeline publication, created with the current `PublishedRegistry.generation`, then advanced to an accepted replacement generation through `adoptRegistryGeneration` before use |
| `beginFrame(FrameBeginInput)` | first mutation at H-FRAME-01, before resize/clear |
| `UniformEventSink` | camera, celestial, fog, blend, color, instance and future owned signals |
| sampler/built-in/custom participants | exact Phase 4 composition positions |
| `UniformPlatformProvider` / `CenterDepthSource` | `mod.glue` implementations installed after GL-ready |

Current Phase 6 §5.1/§5.2 incorporates runtime/event/participant, sole resolver and permanent
retirement semantics; §5.4 below records exact adoption and outstanding fresh-review gates.

### 5.3 Candidate composition/publication protocol

`PipelineBuildTransaction` owns only unaccepted candidates and the private texture owner/lifecycle.
The full-rebuild steps of §4.1 are incorporated below with the same branch guard: resource NONE
uses §5.1 and does not fall through creation/publication steps merely because resources changed.

1. Quiesce/drain/freeze, execute §5.1 lifecycle selection: FULL discover/load, REPUBLISH load,
   NONE resource refresh retains configuration. Every load takes required preliminary independent
   companion pair and current schema; no old operation fallback. Failed rebuild goes off.
2. Adopted registry-independent shadow planning precedes inactive texture owner/adapters and
   new Phase 6 provider/runtime; adopted sole resolver injection and same-sample celestial policy.
3. Freeze/validate installed Phase 9 pure candidate inputs; obtain pure Phase 5 appB3 policy, compile
   Phase 4 with samplerPolicy after runtime macroContribution, derive Phase 5 from detached view.
   AwaitingMainDepth gates without publication.
4. Compose exactly three new-runtime participants; optional Phase 8 construction only under grants
   with final new registry fingerprint; revalidate all intended identities. Precommit failure is off.
5. Close old Phase 8 and old texture-event/resize registrations, retire the old texture owner after
   draw/binding drain, then publish Phase 4 Ready with issued release context.
   Accepted/Rejected/RecoveredOff have distinct ownership. Retire old
   Phase 6 under R7-11 after old barrier invalidation.
6. After Accepted reacquire actual registry generation; adoptRegistryGeneration(PACK_REPLACEMENT).
   ADOPTED/ALREADY_CURRENT proceed; REJECTED_RETIRED_GENERATION compensates before any new use.
7. Publish Phase 5; handle full synchronous resize result, actual sizing/generation and deliveredCount.
   ConsumerFailed is installed off, not caller-owned ready. No frame admission.
8. Build Phase 13 using accepted registry/configuration/estate and frozen sources/resource epoch;
   verify Ready, then register consumer with accepted sizing/generation and pending adapters.
   Every build/identity/registration failure compensates earlier acceptance.
9. Publish Phase 9 after Phase 13; enforce synchronous ID geometry invalidation; atomically install
   complete ActivePipeline, increment PipelineVersion once and admit frames.
10. Failure retires accepted texture owner, closes only caller-owned candidates/Phase 8, resets/
    deactivates Phase 9, publishes Phase 5 off then Phase 4 ShadersOff with issued release context,
    handles actual results and retires runtimes under R7-11. Keep full invalidation after ID failure.

The compact state diagram is `quiesce -> preliminary/load -> owner/runtime/candidates -> Phase4
Accepted -> adopt actual generation -> Phase5 accepted -> Phase13 Ready/registration -> Phase9/
invalidation -> atomic Active`; every failure converges through owner retirement and Phase5/4 off,
never old-pipeline restoration. `RecoveredOff` is only a Phase 4 RESULT, never publication input.
Accepted candidates belong to publishers and are not caller-closed. Retiring an old texture
publication preserves outstanding lease deletion duty only, not fallback authority. The render
thread permits no reentrant publication between bind preflight, binds and immediate activation.

Phase 9 is not retroactively added to Phase 7's declared build dependencies. These v0.3 steps are a
binding downstream integration slot: they remain dormant and report `DEFERRED(P9)` until a Phase 9
artifact with a verified §5 supplies `IdRuntimeBuilder`, `IdRuntimeCandidate`, `IdRuntimePublisher`,
`PublishedIdRuntime`, `RenderLayerLookup`, and `PerDrawDynamics`, and its glue installs
`IdDynamicsFrameSlot`. Once installed, the transaction
uses only those contracts and never reads Phase 9 internals.

Phase 8 is likewise a downstream v0.2 integration slot, not a retroactive declared dependency.
The plan/publication steps remain `NotInstalled` until a Phase 8 artifact with a verified §5 is
available; once installed, Phase 7 consumes only its `ShadowPlanFactory`, immutable plan and
celestial policy, `ShadowPassPublication`, invocation slot, health projection, and closed outcomes.

Phase 8 §§0.7/4.2/5 has migrated R7-12: same selection/context, full expected-publication lease,
exact five-argument shadowBindings and sixteen-row preflight/object binds before activation/
sampler upload. Bound transfers closure; other results close the acquired lease and suppress/
contain through existing abortPass paths. R7-13's registry-independent plan/final-registry create
is likewise adopted. Both sides remain unverified; neither falls back to four-row bindings.

### 5.4 Dependency request status — ungranted changes are never assumed

| ID | Owner | Binding outcome | Phase 7 disposition |
|---|---|---|---|
| R7-1 (granted) | Phase 5 | ordered `FRAME_BEGUN -> PRE_WEATHER -> PRE_TRANSLUCENT -> FRAME_COMMITTED` depth-copy protocol | consumed by §4.5 and §5.2 |
| R7-2 (granted) | Phase 5 | programless `applyVirtualTransition(frameId, PassDescriptor)` | consumed by §4.5–§4.6 and §5.2 |
| R7-3 (granted) | Phase 5 | balanced generation/epoch/frame-checked `openDrawBuffersNone` lease | consumed by H-OVERLAY-01 and §5.2 |
| R7-4 (granted) | Phase 4 | immutable complete `ProgramRegistryView.resolutions()` projection | copied directly into `programs.*` |
| R7-5 (granted) | Phase 5 | immutable canonical `resources.*` projection | copied directly without live-allocation inference |
| R7-6 (granted) | Phase 1/2/6 | total replay-aware `GLError + attributed` flow | Phase 7 copies Phase 6's accepted replay result |
| R7-7 (granted) | Phase 2 | authenticated capture-plan provenance bridge | acquisition mode, archive SHA-512, and licence are transported verbatim |
| R7-8 (owner-granted; receiver-adopted/unverified) | Phase 1 §§0.22/0.24 | exact engine.frame/mod.glue.frame/mod.mixin.frame/mod.conformance packages already granted | no missing placement grant; fresh owner/consumer verification remains |
| R7-9 (granted) | Phase 3 | `NormalizedPackPath.canonicalString()` is the sole stable NFC root-relative slash projection, ordered and hashed by exact UTF-8 bytes | grant is unavailable to implementation until Phase 3's pending whole-document review returns literal PASS; `toString()` remains forbidden |
| R7-10 (owner-designed; receiver-adopted/unverified) | Phase 6 §§0.23/5 | sole FixedSamplerResolver injection immediately after uniform configuration | exact factory below; no independent map; fresh reviews gate implementation |
| R7-11 (owner-designed; receiver-adopted/unverified) | Phase 6 §§0.24/4.14/5 | retire(UNPUBLISHED_ABORT/REPLACEMENT/SHUTDOWN), no reset(CLOSE) | exact retirement below; fresh reviews remain |
| R7-12 (owner-designed; receiver-adopted/unverified) | Phase 8 §§0.7/4.2/5 | same selection/context/full expected-publication lease and five-argument sixteen-row binding | migration present; real slot NotInstalled until fresh owner reviews |
| R7-13 (owner-designed; receiver-adopted/unverified) | Phase 8 §§0.8/2/5 | registry-independent plan, final registry after plan in create | cycle closed architecturally; fresh reviews gate execution |
| Phase 13 R1 (owner-designed; receiver-adopted/unverified) | Phase 3 §§0.55/5 | required CompanionOptionMacros after engineOptions; now current schema17 | preliminary pair before every load; missing non-Off data invalid, no unchanged-load fallback |

R7-10's adopted exact factory is `create(long initialRegistryGeneration,
UniformConfiguration configuration, FixedSamplerResolver samplerResolver,
UniformPlatformProvider platform, CenterDepthSource centerDepth, GLDevice gl,
DiagnosticReporter diagnostics) -> UniformBuildResult`. Phase 5 implements
`FixedSamplerResolver.resolve(ProgramSamplerLayout layout,StageId stage,StageBand band) ->
FixedSamplerPlanResult.Ready(List<ResolvedSamplerBinding> bindings,FixedSamplerPolicyFingerprint
policy)|Invalid(SamplerLayoutValidation reason)` using appB3's same table/schema/fingerprint.
The unchanged Phase 6 sampler participant calls it using binding.samplerLayout and context, locates
exact names and uploads fixed integers in its existing unit/name order/cache/error protocol.
`afterBind(ResolvedProgramDescriptor,BarrierContext,BoundProgramUniformAccess)` and
ProgramUniformCacheKey/activity-token invalidation remain unchanged. No second map, raw handle
loop, free-unit allocation, independent activation or fourth participant is granted.

R7-11 adopts `UniformRetirementResult retire(UniformRetirementReason reason)`; reasons are
UNPUBLISHED_ABORT, REPLACEMENT, SHUTDOWN; results Retired, AlreadyRetired,
Rejected(WRONG_THREAD|ACTIVE_CALLBACK). It performs no GL/barrier operation, permanently rejects
future events/participants/adoption and releases cached locations/provider references. Legal only
after final callback and before borrowed services disappear: candidate abort without publication,
replacement after old barrier invalidation, shutdown before Phase 4 teardown. Phase 6 removed
reset(CLOSE); Phase 11's own terminal controller CLOSE is distinct and precedes this retirement
after final custom use. Retired/AlreadyRetired permit release; WRONG_THREAD/ACTIVE_CALLBACK
rejections retain runtime and borrowed dependencies, keep admission closed, and complete retirement
at the legal render-thread quiescent point. Never free dependencies after rejected retirement.

R7-12 appends `ProgramBindingSelection selection, BarrierContext activationContext,
TexturePublication texturePublication, TextureLeaseSource textureLeases` exactly to
ShadowInvocationContext. Phase 7 selects root shadow once before invoke. Phase 8 acquires the full
expected publication lease and calls
`shadowBindings(long generation,long frameId,ShadowPassSnapshot snapshot,TextureOverlayLease
overlay,TextureOverlayPublicationId expectedOverlay) -> TextureBindingResult`, using the same
selection in beginPass and activation. All sixteen rows preflight, then Phase 5 object binds, then
activation/sampler upload; Bound finally closes binding, otherwise finally closes acquired lease.
Suppression uses existing abortPass; backend failure preserves existing containment. Bridge
ownership, traversal/camera, copied depth/mipmap/neutralization remain unchanged; the old four-row
borrowed R8-2 result is superseded by the adopted current full binding.

R7-13's adopted `ShadowPlanInput(ShadowPolicy policy,ShadowHookHealth hookHealth)` is pure,
registry-independent policy/celestial metadata. Adopted factory:
`create(ShadowPlan plan,RegistryFingerprint registry,UniformRuntime uniforms,
ShadowWorldPort world,DiagnosticReporter diagnostics) -> ShadowPassBuildResult`.
Final publication identity and build validation include that registry; planning never borrows an old
registry to break the cycle. Both R7-12 and R7-13 require fresh owner/receiver reviews before use.

Phase 3 grants CompanionOptionMacros as immutable same-load MacroConfiguration state. Emit only
enabled MC_NORMAL_MAP/MC_SPECULAR_MAP before jcpp; P3 owns exact order/fingerprints. Both independent
booleans are preserved through materialization, never derived from ResourceRequirements/linked
program demand. Missing typed non-Off input is INVALID_REQUEST; Off short-circuits. Phase 13 R4
post-analysis allocation optimization is separately gated and cannot revise emitted policy.

R7-1…R7-8 are granted/adopted architecture, not fresh verified implementation permission.
R7-9 manifest production, P3 current schema, P6 resolver/retirement and P8 binding/planning await
fresh whole-document owner/receiver reviews. Native legacy source preservation, typed suffix
semantics, jcpp permission and optional P10/P14 requests remain separate; no blanket grant.

Phase 8's downstream requests R8-1, R8-4, and the Phase-7 half of R8-5 are accepted by §§4.1,
4.3–4.4, 4.12–4.13, and 5.1–5.3. They are not new dependency requests: they fill this owner's
existing v0.2 downstream slot. Because these edits change §5, the grants remain unavailable to
dependents until the most recently changed binding §5 — per the latest §0 fix-up entry — receives a
whole-document review returning literal PASS.

### 5.5 Downstream hand-offs

| Phase | Contract handed onward |
|---:|---|
| 8 | R7-12/13 receiver-adopted/unverified; same selector/context/full shared binding and authenticated alternate ID admission; NotInstalled until fresh reviews |
| 9 | §5.3 textures-before-IDs/off-on-failure; worker-drained lookup lifetime, main/shadow IdScopeAdmission and ID finally restoration, no duplicate color producer |
| 10 | ID-generation and effective ShaderLightingPolicy invalidation before admission; P1 CompatEvaluation before vertex changes |
| 11 | custom participant only; P6 retire mapping after final use; direct source-free diagnostic projection with final receipt |
| 12 | exact §5.1 ReloadCoordinator/drain, programmatic/persistence receipts and independent P4 generation invalidation; never publish owner objects independently |
| 13 | expected-ID leases and quiescent retirement; independent preliminary preferences; exact authenticated current-bind query→P6 atlas update |
| 14 | P5 resize publications and JFR attribution; no public elapsed-per-pass contract; async P4/P7/P13 proposals separately unadopted, synchronous baseline retained |

---

## 6. Failure modes & degradation

| Failure | Detection boundary | Required disposition |
|---|---|---|
| internal/default pack bytes invalid | Phase 3 load | deterministic diagnostic; publish `Off`; vanilla continues |
| pack/preprocess/compile failure | Phase 3/4 closed result | candidate-local compile fallback remains Phase 4-owned; failed registry/rebuild closes caller-owned candidates and transitions whole pipeline off |
| uniform runtime creation/composition failure | Phase 6/4 closed result | caller-owned cleanup and R7-11 retirement; shaders-off, never prior-runtime restoration |
| shadow planning unavailable or construction failure | Phase 8 gate/result | ungranted/NotRequested/Disabled keeps explicit NotInstalled; an actual failed rebuild follows §5.3 off compensation, never restores prior shadow |
| ID snapshot/build failure | Phase 9 prepublication result | close caller-owned transaction resources; old composition off, never partial replacement |
| buffer planning/build failure | Phase 5 closed result | close caller-owned registry/estate candidates, retire texture owner, old composition off |
| Phase 4 publish rejection | publisher result | candidate still caller-owned; close it and compensate old composition off using issued release context |
| Phase 5 reject/ConsumerFailed after Phase 4 accepts | publisher result | retain exact installed-off/result ownership and deliveredCount, publish Phase 5 off then Phase 4 ShadersOff input; no draw |
| texture build/identity/registration failure after acceptance | Phase 13/resize result | retire accepted textures via owner, close caller-owned candidates, Phase 5/4 off; no Active tuple, old texture reuse or borrowed deletion |
| Phase 9 reject after Phase 4/5/13 accept | ID publisher | retire accepted texture owner, reset/deactivate old ID, close caller-owned ID candidate, Phase 5/4 off |
| ID geometry invalidation reject/failure | synchronous gate | deactivate new ID, retire textures, Phase 5/4 off, require full invalidation before retry |
| lease rejection or binding Rejected/Degraded | before draw | no bind for typed preflight failure; suppress only affected selection, discard main pass or abort shadow without flips, caller closes acquired lease |
| binding BackendFailed | after possible partial texture binds | no ownership transfer, no activation/upload/draw; close caller lease and perform frame/shadow containment |
| Bound then activation/draw exception or reload | binding owner finally | close binding only, never double-close transferred lease; stale use forbidden even if retirement waits for close |
| shadow execution open/validate/close rejection or invocation failure | H-FRAME-05 / bridge closed result | never enter main-policy bypass on a rejected credential; best-effort abort shadow, close/invalidate the view, restore main state, and schedule Phase 8 off or the whole pipeline off according to the closed failure; no main clear occurs while the credential remains active |
| missing CORE hook | startup application audit | disable shader group for session (rung 3); vanilla render |
| missing FEATURE hook/event | application/report audit | disable only that phase/flag (rung 2a) and restore vanilla state |
| stale frame/barrier/generation or protocol rejection | closed Phase 4/5 result | abort shader frame, normalize, reacquire only at next frame; never retry inside a draw |
| depth backend degradation | `DepthCopyResult.BackendDegraded` | bind Phase 5's depthtex0 fallback, diagnose, continue feature-locally |
| backend pass/final failure | Phase 5 completion/port result | abort, normalize, recover `Off`; present vanilla if still valid |
| resize/attachment epoch changes mid-frame | refresh/observer result | abort shader frame, schedule one safe-boundary rebuild, continue vanilla |
| nested scope throws | local AROUND `finally` or H-FRAME-00 leaked-scope drain | abort child/frame, restore platform state, preserve the original throwable |
| P9 ID/P7 color scope mismatch or throw | P9 token/P7 color-stack result or terminal drain | restore/zero both before main program drain or shadow release; fail only producer unless stale generation requires frame containment |
| ordinary early return | H-FRAME-00 `finally` with coherent token | run remaining composites and final exactly once, then commit |
| early return after protocol/backend corruption | token marked unhealthy | do not issue unsafe fullscreen draws; abort/normalize and fall back to vanilla |
| capture image/manifest write failure | capture agent commit | Phase 2-owned failure manifest; do not acknowledge shot; clean shutdown remains available |
| event/state sample malformed | Phase 6 typed sink | retain typed fallback/last-valid according to Phase 6; observer feature diagnoses once |

Finalization is non-throwing at the engine boundary: closed failures are returned. The outer wrapper
does not swallow a vanilla/mod throwable. It attempts coherent finalization in `finally`, attaches
any diagnostic without replacing the original failure, and rethrows the original after state
restoration. “Composite guarantee” therefore does not mean “render after the transaction is known
unsafe.”

---

## 7. Threading & performance notes

- Pipeline compile, Phase 4/5/9 candidate publication, Phase 8 plan/publication construction and
  close, all hook signals, shadow bridge open/validate/close, barrier activation,
  fullscreen execution, resize delivery, capture, and teardown occur on the render thread. A reload
  request may be queued elsewhere but crosses as an immutable request and commits only there.
- Phase 9's registry/mod/tag/policy snapshot is taken on its documented loader boundary; its pure
  candidate build may run off-thread only after that snapshot freezes, while publication,
  per-draw calls, and geometry invalidation remain render-thread safe-boundary operations.
- Cold source preparation, complete layout derivation, fingerprints and candidate indexing occur
  before draw; immutable canonical lists and bounded unit indexes are reused. Bind/lease/animation
  delivery and owner retirement are render-thread work. No per-draw parsing, byte hashing, free-unit
  scan, incidental per-row copies or boxing; fresh bounded immutable leases/selections/snapshots
  are permitted lifecycle allocations, not a zero-allocation promise.
- `DimensionPipelineCache` is render-thread-confined. `PipelineVersion` is a 64-bit equality token;
  no greater-than comparison or temporal inference survives wrap.
- Dumb hook dispatch allocates no incidental collections/strings; catalog IDs and RenderSections
  are constants and logical scope storage is bounded. Required lifecycle objects are as above.
- Effective provider selection is one Phase 4 memoized operation per logical scope, not a per-bind
  repeat; Phase 7 never builds a string map or scans all slots during drawing.
- Phase 5 snapshots are acquired per physical pass and closed promptly through the ownership branch.
  They never authorize use after pass/frame/depth/estate/registry/selection/publication invalidation;
  an old still-open lease retains deletion duty only, not permission to replay its handles.
- `ShadowFrameView` and `ShadowExecutionView` are synchronous borrowed values. They are never
  retained, closed by Phase 8, or used outside the one authenticated slot invocation.
- Fullscreen mipmaps are generated only for the descriptor's declared readable set immediately
  before that pass. Scale viewports and instance loops do not allocate per instance.
- The synchronous center-depth read is performed only by Phase 6 at frame begin when declared.
  Phase 7 adds no readback. Image capture is plan-gated and outside ordinary play.
- State snapshot/restore is one outer fullscreen operation plus the minimal balanced scoped deltas;
  observer callbacks are guarded during driver-owned restoration.
- Debug hook timing uses preallocated counters and is off by default. OQ-4 must demonstrate no
  per-call allocation and less than 1% added CPU frame time in its fixed-scene comparison.

---

## 8. Testability plan

### 8.1 Pure `:engine` tests

| Test family | Assertions |
|---|---|
| frame state-machine tables | every legal transition, duplicate finish idempotence, all stale/foreign/wrong-order rejections mutation-free |
| composite guarantee | normal, early-return, and thrown exits finalize exactly once when coherent; corrupted token aborts instead |
| phase-dispatch completeness | every §3.2 program maps to one requested slot/fixed/virtual/deferral; no hidden string fallback |
| nested scope property tests | arbitrary bounded push/pop/throw sequences leave no open Phase 5 snapshot and restore parent/fixed state |
| candidate ownership model | every failure step closes exactly the caller-owned Phase 4/5/9 candidates and optional Phase 8 publication in reverse order; no draw or shadow invocation between publication steps or geometry invalidation |
| shadow composition and bridge | policy projection happens once with no parse/read, planning precedes provider construction, the exact celestial policy reaches that provider, publication follows runtime creation, and open/validate/close covers exact rejection priority, non-nesting, wrong issuer/execution/thread, stale slot epoch, and `finally` invalidation |
| shadow frame identity | driver frame ID, world epoch, partial ticks, exact main setup-terrain token, unshifted camera, sky angle, and sun angle all come from one accepted sample; mismatch prevents bridge open |
| ID-runtime coordination | schema/config/registry fingerprint mismatch, Phase 9 rejection after either earlier acceptance, reverse off order, generation invalidation, and unchanged-generation no-op |
| per-draw dynamics | accepted P6 frame→held sample; nested IDs under main or authenticated shadow admission; no main activation in shadow; independent v0.1 color immediate update/restoration and terminal zero |
| dimension cache | base/override/disabled switches and version equality; failed rebuild goes off, retired texture leases cannot reactivate a slot; demotion retains plans only |
| fullscreen plan | mipmap-before-pass, identity projection, exact scale viewport, instances `0..N-1`, restore `0`, final/passthrough |
| internal pack source/golden | ordinary Phase 3 load and limit rejection require no manifest; after Phase 3 reverification, the granted projection drives exact manifest identity/path list and SHA-256-v1 digest for every accepting limit; rejecting limits cannot yield a subset |
| engine flags | tri-state/default behavior and `finally` restoration for every §3.5 row |
| hook-coverage model | exactly one disposition for §7.1 needs 1–11, Appendix A.1 rows, App E rows 1–18, and Pintonium rows 1–7; owner-phase-8 subreport rows copy without primary-ID drift and absence is never healthy |

### 8.2 Recorded-GL and integration tests

`RecordingGLDevice` tests assert Phase 4 release→bind→three Phase 6 participants→state lock order;
Phase 5 acquire/bind/clear/complete; deferred/composite/final mipmap and viewport order; final uses
`Screen` rather than an engine handle; every state snapshot is restored; and candidate failure leaks
no owned handle. Scripted failures cover every §6 row. Two recorded capability profiles include the
minimum GL 2.1 profile and one without quad support to force triangle strip.

Mixin integration tests launch the Cleanroom dev client with the catalog report enabled and assert
application counts, call order, early-exit finalization, actual-vs-window framebuffer extent, and
event cancellation. Sky/cloud/weather—the three reference-free families—are tested before any pack
matrix run. At v0.2 they additionally assert all eight Phase 8 health rows, the plan/report
fingerprint match, same-sample shadow frame values, main-policy bypass only during a valid dynamic
extent, and close-before-main-clear on normal and thrown paths. At v0.3 they additionally assert the
five H9 report rows and exact expected counts,
`FMLModIdMappingEvent` → queued `REGISTRY_REMAP`, both RenderManager entry methods, TE inside-open/
before-close order, buffer-position-preserving color capture, and reset-before-fixed-function on an
injected throw. OQ-4 is a separate spike gate, not replaced by unit tests.

### 8.3 Phase 2 implementation and milestone gates

- During development: `RUN-SCENE-SELFCHECK` on all fixed scenes, with repeated frames identical.
- Phase 7 implementation gate: `RUN-T1-APPROVE` then `RUN-T1-REGRESS[one classic pack, all scenes]`,
  plus `RUN-T0[classic × all scenes]` exactly as Phase 2 maps v0.1
  (`docs/phase2/v2/PHASE_2_DOC.md` §§3.5/4.9/5.3).
- `RUN-CAPS-GATE` and `RUN-GOLDEN-CORE` remain green for the internal pack and minimum profile.
- COMPLETE/T3 claims consume the granted R7-4…R7-7 projections and remain subject to Phase 2's
  ordinary predicate validation.

No OptiFine image/source is committed. Oracle images remain local fixture artifacts under Phase 2's
licensing and cache rules.

### 8.4 Coordinated shared-unit behavioral checks

These are future architectural checks, not tests executed by this document rebuild. Producer
invariants are tested by Phase 4/5/13 as named; Phase 7 owns orchestration of each trace.

| Test hook / producer owner | Input → observable outcome |
|---|---|
| sharedUnit_programSpecificTargets / P13 retention, P5 selection | canonical texture.composite.gaux1 raw1D and .2 raw3D coexist → sampler1D selects first on7, sampler3D second on7; both names gaux1, no source patch/unit reassignment |
| sharedUnit_incompatibleAliasesBeforeBind / P4, defensive P5 | colortex4 sampler1D plus gaux1 sampler3D → typed SAMPLER_UNIT_TYPE_CONFLICT before GL creation, provider-local fallback; corrupted metadata preflight binds/uploads/draws nothing |
| sharedUnit_effectiveFallbackLayout / P4 | child failed/missing source with unlike ancestor layout → selector, candidate objects, activation and Phase 6 callback all use ancestor layout/state, no child overlay |
| custom_stageExpansionExact / P13 | GBUFFERS/DEFERRED/COMPOSITE inputs → gbuffers+shadow/deferred/composite+final copies preserve original discriminator/ordinal; tex in gbuffers diagnoses stage rather than invented unit |
| custom_fullscreenFixedOverrides / P5 | compatible composite/final colortex1 and deferred gaux1 custom → custom objects at1/7 instead of estate; all published fixed aliases reachable |
| sharedUnit_fixedRangeAndShadowAlias / P5, requested P6 | add direct compatible watershadow → shadow4→5 in object/integer plans together; units remain0–15, depthtex1=11, gbuffers12 Unused |
| sharedUnit_exactNameAndOrdinal / P5 | compatible absent/0/9 discriminators in arbitrary insertion →9 wins after filtering; distinct alias winners on one unit → CONFLICTING_CANDIDATES, zero draw |
| sharedUnit_fullSamplerShape / P4/P5 | equal dimensions but FLOAT/SIGNED_INT/UNSIGNED_INT, shadow/arrayed/multisample differences or aggregates → no dimension-only coercion; typed unsupported/conflict |
| binding_staleBeforeMutation / P4/P5/P13 | independently stale registry generation/fingerprint, estate/depth/frame/context/provider/band/layout/policy/publication/resource epoch or closed lease → reject before binds; publication-ID mismatch precedes registry mismatch |
| binding_absenceVersusIncompatibility / P5 | absent cell, incompatible custom, unavailable publication, unknown name or conflicting declaration → distinct closed diagnostics; compatible base warns/draws, required missing backing suppresses just program and discards without flips |
| binding_leaseOwnershipAllExits / P5/P13, P7 orchestration | Bound versus reject/degrade/backend throw across normal, activation failure, nested suspend/pop, exception, reload, neutralization, teardown → exactly binding-after-Bound or lease-before-Bound closes; old lease delays owned deletion, never stale draw/foreign deletion |
| publication_contentIdentity / P13 | same key/path/dimensions but changed bytes/effective upload/filter → different fingerprint, no hash-only reuse authority |
| publication_foreignReloadIdentity / P13 | same minecraft identity replaced on reload → resource/object epoch changes publication without GL-name hash; in-place animation tick leaves publication stable |
| animation_postVanillaSnapshot / P13, P7 event routing | reordered unequal-duration sequence → after vanilla tick, copied exact frame/weight/mips drive companions; reload rejects prior snapshot and missing hook restores frame0 |
| macro_beforeJcppIndependentPreferences / P13 producer, P7 orchestration | active/capable plus independent normal/specular preferences produces each bit separately before load; required pair absent rejects non-Off; same-load macro fingerprints preserved, no linked-demand cycle |
| resourceReload_preDestructiveGate / P7 with P12/P13 | instrument original replacement and listener calls → old binding/shadow/worker uses drained and atlas reset before first resource release; no publication inside listener; same configuration after NONE; nested notifications produce one epoch/final receipt; original throw preserves exception and converges off; unsafe callback entry never calls original |
| noise_signedRecurrence / P13 | arithmetic shift/wrap/signed remainder → xorshift(-1)=253983; channel(1,1,1) remainder=-115/upload141; unsigned shift/absolute value differs |
| unsupported_noEnumSentinel / P13 | unknown name → UnknownSampler+KEY_DOMAIN; recognized illegal stage → KnownSampler+STAGE_COLUMN, preserving key/stage without fabricated unit |
| pipeline_textureFailureCompensates / P7 | inject Phase13 build/identity/registration failure after Phase4/5 acceptance → owners retire accepted resources, caller candidates close, Phase5/4 off, no Active/draw; ConsumerFailed deliveredCount preserved, no borrowed deletion |
| shadow_sharedBindingAndGate / P5 producer, P7/P8 orchestration | granted full shadow selection/binding → sixteen-row object binds before upload and transferred binding closes once; ungranted → NotInstalled/unavailable, no four-row success |

---

## 9. Milestone staging

### 9.1 v0.1 first end-to-end assembly, in dependency order

| Order | Assembly increment | Observable gate |
|---:|---|---|
| 1 | reverify adopted R7-8 and current P3 before manifest/digest production consumes R7-9 | package/seam proof plus fresh owner literal PASS where §5 changed |
| 2 | install Phase 1 bootstrap/GL-ready providers and the Phase 2 capture-plan skeleton required by D-10 | startup reaches `Off` and exits cleanly without renderer |
| 3 | supply/load Phase 7's internal pack through Phase 3; after Phase 3 owner reverification, consume granted `canonicalString()` to produce its canonical manifest/digest and run the headless golden; build Phase 6 runtime | headless internal golden and uniform-runtime tests |
| 4 | install Phase4/5 sampler-layout/select-once/pure fixed-policy infrastructure at v0.1 under R7-10/11 gates; execute §5.3 coherent transaction with explicit empty texture publication before v0.5; shadow remains NotInstalled under R7-12/13 | §8.4 same-selection, stale-before-mutation and ownership traces |
| 5 | wire H-FRAME core transaction and fixed/final passthrough | empty world renders and early-return test finalizes once |
| 6 | prove reference-free H-SKY, H-CLOUD, H-WEATHER first | dev hook report plus one fixed scene per family |
| 7 | add terrain/damage/entity/effect/particle/border balanced routes | phase-dispatch coverage and recorded activations |
| 8 | execute deferred/composite families and anaglyph-aware final; actual copied-depth remains v0.5-gated | fullscreen order/viewport/state tests |
| 9 | add split hand/overlay routing through the granted lease and wire engine flags | water/hand/overlay fixed scenes |
| 10 | add dimension/reload/resize rebuild transaction, hook audit, capture readiness/shutdown | resize/dimension fault matrix and `RUN-SCENE-SELFCHECK` |
| 11 | run the binding v0.1 Phase 2 gates | ≥1 classic pack T1; classic matrix T0 |

This ordering makes the three unreferenced hot areas fail early, before a visually plausible terrain
path could hide them. It also yields a valid fixed/passthrough frame before optional families.

### 9.2 Later milestones

| Milestone | Phase 7 increment |
|---|---|
| v0.2 | adopt/freshly verify R7-12/13 then execute same-selection full shared shadow bindings before sampler upload; retain traversal/camera/bridge/health/mipmap/neutralization policy; without grant NotInstalled, never four-row substitute |
| v0.3 | P9 candidate/publication, held/reset and entity/TE IDs, remap/source reasons and P10 invalidation; v0.1 P7 color remains the only writer |
| v0.4 | Phase 11 custom bridge and Phase 12 reload/GUI consume existing contracts |
| v0.5 | Phase13 source-bearing custom/companion/noise publication, animation and preliminary-macro gates; full AppF5 not blocked on old Phase13 R2 narrow domain; enable copied depth, scale and instances; all §8.4 producer/orchestrator checks required |
| post-v0.5 | schedule new Phase 4 families data-first; no new catch-all render hook |

---

## 10. OQ & spike specifications

### 10.1 OQ-3 — Cleanroom GL context and framebuffer sizing

**Question, verbatim from RESEARCH.md §11:** “GL context creation mechanics under Cleanroom
(compat request, GLFW hints, lwjglx runtime role, HiDPI)”
(`docs/research/v1/RESEARCH.md:1009`).

**Current evidence.** Pintonium does not alter context creation; PD records no implementation there
(`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:756`–`:779`). This supports the conservative
fallback but does not answer Cleanroom/lwjglx behavior.

**Procedure.** In a pinned Cleanroom dev environment:

1. trace the loader/window path from legacy display creation through lwjglx to GLFW, recording the
   requested version/profile/debug/forward-compatible hints and the actual context flags/version;
2. run a minimal GLSL 120 shader and fixed-function draw to prove compatibility behavior, without
   changing production hints;
3. instrument logical window size, framebuffer pixel size, vanilla `Framebuffer` size, and
   H-RESIZE values on initial creation, ordinary resize, GUI-scale change, fullscreen toggle, and
   movement between 1× and HiDPI monitors;
4. repeat with lwjglx's relevant runtime options toggled one at a time and state exactly which layer
   observes each option; and
5. record source coordinates, logs, hardware/OS/scale, and the recommended hook set in
   `docs/decisions/OQ-3_GL_CONTEXT.md`.

**Success.** The spike identifies the authoritative framebuffer-pixel extent and proves the existing
context is compatibility-profile GL ≥2.1 on the supported matrix. A context-hint change is adopted
only if it is sanctioned by Cleanroom, preserves legacy fixed-function behavior, and passes client
startup/resize on every spike platform.

**Failure/fallback.** Make **no context-flag change**. Probe the context Phase 1 receives, reject an
inadequate profile through normal capability failure, and use only H-RESIZE-01/02 plus H-FBO-01/02.
Framebuffer pixel size, never logical window size, drives Phase 5. This fallback is the default plan
until the spike positively proves more.

### 10.2 OQ-4 — CleanMix hot-path injection viability and overhead

**Question, verbatim from RESEARCH.md §11:** “CleanMix divergences relevant to hot render-path
injections” (`docs/research/v1/RESEARCH.md:1010`). Pintonium proves HEAD/INVOKE/TAIL moments on a
MixinBooter-family 1.12.2 loader; this spike isolates CleanMix specifically.

**Procedure.** With production mappings/refmap and the pinned loader, enable a counter-only spike
mixin for each category:

1. `@Inject` HEAD/RETURN in `EntityRenderer.func_175068_a(IFJ)V`;
2. `@Redirect` of a slice-bounded invocation in `RenderGlobal.func_174976_a(FI)V`;
3. ordinal INVOKE before/after around the first clear and terrain setup;
4. constant/argument interception in a representative `BufferBuilder.func_181668_a(I,VertexFormat)V`
   path (a spike only; Phase 10 owns production); and
5. the same dev runs with CleanMix enabled and disabled as the controlled comparison.

The Mixin plugin records expected/actual counts; tests exercise ten thousand calls per hot category,
verify balanced counters and bytecode/application logs, use JFR/allocation profiling, and compare
fixed-scene CPU frame time after warm-up. Test one early-return and one injected exception. Store
coordinates, alternative form per row, timing distribution, and loader/mapping hashes in
`docs/decisions/OQ-4_CLEANMIX_HOOKS.md`.

**Success.** Every representative applies exactly once, preserves control flow and balanced cleanup,
allocates nothing per hot call, and adds less than 1% CPU frame time on the fixed scene. Catalog IDs,
moments, and bridge calls remain unchanged.

**Failure/fallback.** Replace only the failing form: paired HEAD/RETURN with an outer invocation
redirect; redirect with slice-bounded BEFORE/AFTER injection plus a bridge token; constant hook with
`@ModifyArg` at its consuming invocation or an accessor at a stable call boundary. If no form
preserves a CORE moment, mark the group unavailable and select `Off`; never lower `expect`, broaden a
slice, or continue partially. Any selected alternative is recorded by a Phase 7 fix-up.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale |
|---|---|---|
| D-P7-1 | engine-side `FrameDriver` behind `FrameHookSink`/`FrameRenderPort` | keeps hook policy dumb and makes a Kirino producer replaceable |
| D-P7-2 | outer render-world `finally` plus idempotent TAIL fast path | provides the required early-exit composite guarantee beyond the reference |
| D-P7-3 | per-dimension plan cache with equality-only version counter; only one active GL publication | adopts PD's useful shape without inventing cross-dimension GL ownership |
| D-P7-4 | split post-clear resource begin from post-camera matrix capture | Cleanroom orders the clear before camera setup; RESEARCH/Phase 6 require post-setup matrices |
| D-P7-5 | adopt the reference hand moment but not its depthtex2/center-depth work | authoritative depth moments and Phase 6 ownership differ |
| D-P7-6 | compose paired candidates, publish Phase 4 then Phase 5, forbid intervening draw | follows both dependency ownership/provenance contracts |
| D-P7-7 | use Forge events only when they provide exact fidelity and balanced lifecycle where needed | reduces OQ-4 surface without pretending a pre-event is a scope |
| D-P7-8 | `(internal)` and `Off` are distinct successful selections | required internal fallback is renderable; off bypasses engine GL work |
| D-P7-9 | `require=0` plus independent post-application health groups | missing hooks degrade instead of crashing, but cannot disappear silently |
| D-P7-10 | final fixed/absent slot performs typed passthrough; quad capability selects strip fallback | final is total on the GL 2.1 baseline |
| D-P7-11 | map Phase 3's typed flags to the narrow cancellable draws, scoped GL leases, and catalogued visibility queries in §3.5 | Phase 3 assigns behavior ownership but not hook-level semantics; narrow scopes preserve vanilla defaults and make restoration testable |
| D-P7-12 | publish Phase 9 after Phase 4/5, invalidate ID-dependent geometry before Active, and reset its per-draw stacks before fixed-function release | grants R9-2 without moving alias/value policy into hooks or permitting mixed publication generations |
| D-P7-13 | compose Phase 8 from one typed Phase 3 projection: plan before the Phase 6 provider, publish after the runtime, invoke through a Phase-7-issued dynamic-extent credential, and close first on rollback | one policy/sample/fingerprint lineage prevents shadow/main drift while keeping Phase 8 downstream and hooks dumb |
| D-P7-14 | one authenticated Phase 4 selection through snapshot/lease/Phase5 object bind/Phase4 activation/Phase6 upload | AppF5 needs program-specific shapes; AppB.3 requires sole fixed authority; no second resolve/map/bind loop |
| D-P7-15 | preliminary macros before load; textures built after accepted estate, then ID publication and atomic Active, failures off | removes jcpp/texture and guessed-generation cycles and prevents new programs/estate paired to stale texture objects |
| D-P7-16 | adopt P2 `/2` dense frame/actual-pose protocol and current P3 catalog-bound schema17 | exact wire and same-build state, no historical compatibility path |
| D-P7-17 | adopt P12 max-lifecycle/OR-flags drain with final receipts and independent P4 generation events | one request outcome does not imply one publisher mutation; resource NONE preserves config |
| D-P7-18 | P7 owns v0.1 hurt/flash color producer; authenticated shadow is alternate P9 ID admission | RC3 excludes alias IDs from v0.1, not entityColor; no main pass inside shadow |
| D-P7-19 | direct P11 immutable diagnostic GUI projection and authenticated current-atlas adapter | no fourth channel or physical binder; current bind is evidence, stitch availability is not |
| D-P7-20 | loader events, actual CompatEvaluation, mod-side anaglyph restoration and first-hook checks | adopts P1 owner boundaries, never invented facade/verdict/bootstrap contracts |
| D-P7-21 | SSAA and non-fullscreen countInstances remain authority-gated; JFR fallback accepted | retained metadata is not execution, optional profiling does not gate synchronous baseline |
| D-P7-22 | Wrap the actual resource-manager replacement before it releases packs, not merely its post-release listener | IR-06 verification repair; a queued NONE request cannot protect borrowed texture lifetimes; MCP target is mapped, OQ-4/runtime proof remains owed |

### 11.2 Binding decision disposition

| Decision | Phase 7 disposition |
|---|---|
| D-1 | Cleanroom 1.12.2 is the exclusive loader/runtime target; hook selection uses its verified bytecode and events |
| D-2 | frame orchestration remains shader-pack rendering only and adds none of the written non-goals |
| D-3 | the §3 conformance map and data-driven dispatch target the fixed pack matrix, not open-ended Iris parity |
| D-4 | traverse Phase 4 registry/schedule; never hardcode a terminal slot count |
| D-5 | all required vanilla hooks are Mixins or precise Forge events; no coremod/ASM fork |
| D-6 | pure engine frame policy and narrow glue are structural and tested |
| D-7 | Schmaloogium remains GPL-3.0-or-later (§0.4) |
| D-8 | published and LGPL evidence is cited without copying; OptiFine is behavioral-observation-only and AGPL input is excluded (§0.4) |
| D-9 | compatibility-profile GL is mandatory; capability-driven `QUADS` retains the triangle-strip fallback (§4.6) |
| D-10 | Phase 2 skeleton is an implementation precondition and its v0.1 runs are the gate |

### 11.3 Open items and dependency blockers

- OQ-3 and OQ-4 remain open until their §10 artifacts and acceptance criteria are complete.
- R7-1…R7-7 are granted and consumed; they impose no remaining feature, COMPLETE, or T3 gate.
- R7-8 package placement is granted/adopted; fresh Phase 1/7 verification remains.
- R7-9 is granted; internal-pack manifest/digest production requires fresh Phase 3 PASS.
- R7-10/11 and R7-12/13 are owner-designed/receiver-adopted, unverified; real shadow stays
  NotInstalled pending fresh Phase 4/5/6/7/8/13 owner reviews and applicable upstream gates.
- P3 schema17/companion/lossless/projection adoption is explicit; typed suffix semantics,
  jcpp permission and native legacy source preservation remain separately ungranted.
- **IR-12 unresolved authority gate:** RESEARCH §1.2 excludes AA, App A.3 only names an SSAA
  multiplier, and §4.3 gives display×quality extent. Authority must decide whether pack SSAA
  is in scope and, if so, exact level domain, sample/draw order, camera/history treatment,
  final resolve weighting/target and failure behavior. Until then level>1 is not supported
  execution; no extent multiplication, guessed sample loop or silent single-sample success.
- **IR-18 unresolved authority gate:** gbuffers/shadow countInstances needs an explicit owner,
  milestone and rerender/side-effect contract. Only §4.6 fullscreen repetition is accepted;
  metadata retention or a modern instanced draw is not support for the non-fullscreen case.
- **IR-28 first-hook gate:** before subsequent hook implementation, verify the first actual
  Mixin config generates its refmap in the built jar and reconcile JAVA_8 compatibilityLevel
  with Java-25-produced Mixin bytecode/features. Record actual evidence with OQ-4; neither
  inherited settings nor this architecture amendment claims either check passed.
- Phase 10/13 milestones and Phase 9's own verified §5 plus installed ID geometry invalidator remain
  required; this rebuild does not remove unrelated owner requests or grant implementation closure.

### 11.4 Input contradictions and rulings

Section 3.6 is binding and is not repeated here. No conflict was silently resolved: RESEARCH wins
the depth ordering and matrix semantics; actual Cleanroom byte order selects a later valid injection
site; Phase 4/5 interface gaps become requests; Pintonium remains evidence only.

### 11.5 Requested upstream changes

- **U7-1 — RESEARCH Appendix E:** replace or qualify the `RenderGlobal.renderBlockLayer` row with
  the actual world-loop overload `func_174977_a(BlockRenderLayer,D,I,Entity)I`; retain
  `func_174982_a(BlockRenderLayer)V` only for its actual role. The Cleanroom caller is visible at
  `reference-src/cleanroom-0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch:200`–`:206`.
- **U7-2 — next DESIGN candidate:** split timeline row 3 into post-clear buffer preparation and
  post-`setupCameraTransform` matrix capture. Current RC3 remains immutable; D-P7-4 is the local
  ruling required by higher authority.
- **U7-3 — RESEARCH §7.1:** narrow the “every class” claim at
  `docs/research/v1/RESEARCH.md:823`–`:825`; 17 of 18 Appendix-E classes occur in the assigned
  replacement list, but `net.minecraft.client.shader.Framebuffer` does not. Appendix E's own
  preamble (`docs/research/v1/RESEARCH.md:1390`–`:1392`) already states that qualification, so no
  narrowing is owed there. Section 3.7 records the exhaustive result and separately flags
  supporting hook targets without that corroboration.
- **R7-8:** owner grant adopted; obtain fresh owner/receiver verification, not duplicate placement.
  Granted R7-1…R7-7 remain consumed; R7-9 production remains gated on Phase 3 reverification.
- **Accepted Phase 8 requests R8-1/R8-4/R8-5:** §§4.1, 4.3–4.4, 4.12–4.13, and 5.1–5.3 now grant
  the authenticated execution bridge, exact shadow frame, one-policy composition order, owned
  publication slot, reverse close, and nested immutable hook report. The grant becomes consumable
  only after the most recently changed binding §5 (per the latest §0 fix-up entry) receives a
  literal-PASS whole-document review and Phase 8's own §5 is verified.
- **R7-10/11:** exact Phase 6 owner factory/retirement adopted in §5.4; no reset(CLOSE) alias.
- **R7-12/13:** exact Phase 8 current binding/planning/final-registry construction adopted;
  fresh reviews remain, not a missing consumer design.
- **Phase 13 R1:** Phase 3 required pair adopted with independent preferences before load;
  R4 post-analysis allocation remains separate and cannot provide preprocessing input.
- **IR-12/18:** request the explicit authority decisions in §11.3, without changing authority.
- **IR-26:** accept Phase 14 JFR attribution fallback; no public elapsed-per-pass API is promised.
- **Phase 3/DESIGN suffix reconciliation:** current P3 preserves lossless unresolved declarations
  and separate typed executable sources/sidecars; suffix grammar/precedence/typed sampling
  remains authority-gated. P7 never reparses strings or reconstructs discarded settings.

This integration fix-up changes only the owned P7/P8 architecture documents. No governing,
research, reference, prior review, manifest or code file is edited; no directory roll.

---

## 12. Implementation checklist

| # | Work item | Tag | Test hook |
|---:|---|---:|---|
| 1 | reverify adopted package grant R7-8 and current P3 before item5 R7-9 manifest/digest production | v0.1 prereq | fresh owner/receiver review |
| 2 | create declared engine/glue/mixin/conformance packages and seam rules | v0.1 | forbidden-dependency/package architecture tests |
| 3 | implement immutable frame/scope/result algebra and state machine | v0.1 | transition/property tests |
| 4 | implement `DimensionPipelineCache` and equality-only versions | v0.1 | dimension/base/override/failure matrix |
| 5 | implement project-owned `BuiltInPassthroughPack` as Phase 3 `InternalPackSource` with stable identity and whole-corpus bounded snapshot/checked provider failure; only after Phase 3 reverification add the separate `InternalPackManifestProducer` using granted `canonicalString()` | v0.1 | ordinary Phase 3 load/limit/provider-failure; post-reverification manifest/digest golden |
| 6 | implement pipeline build ownership ledger and reverse close | v0.1 | fault at every build step |
| 7 | implement §5.3 transaction plus §5.1 retained-config resource NONE branch, generation observations and final receipts | v0.1–v0.5 | failure compensation and no intervening draw/shadow |
| 8 | implement FrameRenderPort through facade where granted and mod-side vanilla state/FBO boundary for explicit non-verbs, including anaglyph mask | v0.1 | seam + state restoration checks |
| 9 | implement H-BOOT-01 loader events, H-BOOT-02 required GL-ready RETURN and H-BOOT-03 recommendation; first actual config proves jar refmap and JAVA_8/Java25 compatibility | v0.1 | first-hook evidence before further hook work |
| 10 | implement H-FRAME-00…07 and outer-finally/idempotence | v0.1 | normal/early/throw integration |
| 11 | implement post-camera matrix capture and once-per-frame assertion | v0.1 | camera snapshot ordering test |
| 12 | implement shadow invocation slot with v0.1 `NotInstalled` plus the Phase-7-owned exact-order, non-nestable execution bridge | v0.1 | auth rejection table and main clear resumes correctly |
| 13 | prove H-SKY reference-free family, including celestial redirect | v0.1 | dev application report + sky scene |
| 14 | prove H-CLOUD reference-free family and cloud flag precedence | v0.1 | fast/fancy/off scene matrix |
| 15 | prove H-WEATHER reference-free family; keep actual copy v0.5-gated | v0.1 | rain/snow/depth restoration scenes |
| 16 | implement terrain/deferred trigger and granted virtual transition | v0.1 | solid→deferred→water activation log |
| 17 | implement damaged block/line/entity/block-entity subsection routing; at v0.3 add H9 entity and inside-TE ID augmentation | v0.1/v0.3 | scoped activation, nested overload, and reverse-close tests |
| 18 | implement leash/glint/eyes/beam nested scope stack | v0.1 | injected exception restores parent |
| 19 | implement lit/unlit particle and world-border scopes | v0.1 | recorded slot selection |
| 20 | implement split hand/depth-scale and granted overlay lease | v0.1 | solid/translucent/overlay scene |
| 21 | implement P7 v0.1 color operand/current-scope capture, immediate P6 upload/restoration and throw neutralization; no P9 dependency | v0.1 | nested color/buffer-position/throw cases |
| 22 | implement fullscreen executor: mipmaps, identity ortho, scale plumbing, strip fallback, final passthrough | v0.1 | two recorded capability profiles |
| 23 | implement resize/FBO epochs and safe-boundary rebuild | v0.1 | resize/HiDPI/fullscreen fault matrix |
| 24 | implement §5.1/§4.8 resource NONE quiescence preserving configuration and affected resource/ID refresh, plus full teardown retirement | v0.1–v0.5 | retained-config resource epoch and failed-refresh off |
| 25 | implement actual CompatEvaluation/session Bail and complete frozen report: three dormant P9 ID rows, two P7 v0.1 color rows and nested P8 report | v0.1/v0.2/v0.3 | no inferred health; independent color/ID failures |
| 26 | run and record OQ-3 spike; retain default fallback until success | v0.1 | `docs/decisions/OQ-3_GL_CONTEXT.md` |
| 27 | run and record OQ-4 spike before production hot hooks close | v0.1 | `docs/decisions/OQ-4_CLEANMIX_HOOKS.md` |
| 28 | implement capture readiness/frame boundary/shutdown without inferred manifest fields | v0.1 | capture-plan success/failure integration |
| 29 | run reference-free family integration and `RUN-SCENE-SELFCHECK` | v0.1 | all repeated frames identical |
| 30 | run Phase 7 impl gate: classic T0 matrix and one classic pack T1 | v0.1 | Phase 2 named artifacts/manifests |
| 31 | obtain R7-12/13 and fresh owner reviews, then migrate full shadow selection/context/lease/sixteen-row binds and exactly-once closure; without grant NotInstalled | v0.2 | shadow_sharedBindingAndGate plus bridge/health/camera tests |
| 32 | activate verified P9 candidates/held/ID hooks and P10 invalidation with main/shadow admission; retain P7 color owner | v0.3 | ID/vertex/layer/remap/health cases |
| 33 | connect Phase 11/12 consumers | v0.4 | custom/reload/options tests |
| 34 | enable depth/scale/instances and full Phase13 source/candidate/animation/noise/macro transaction only with required owner grants | v0.5 | every §8.4 producer hook plus P7 orchestration; no empty-publication completeness claim |
| 35 | implement same selection across nested/fullscreen paths, local discard and Bound-only lease transfer after R7-10/11 adoption | v0.1 infrastructure | sharedUnit_effectiveFallbackLayout; binding_leaseOwnershipAllExits; binding_staleBeforeMutation |
| 36 | execute complete §8.4 shared-unit program-specific target, alias/type, ordinal, expansion, override and fixed-range matrix under required owner review gates | v0.1/v0.5 | all §8.4 named input→outcome rows; producer tests remain owner-attributed |

---

*End of PHASE_7_DOC.md. Review36's literal PASS certifies only pre-rebuild bytes.*

§5 changed in this coordinated rebuild. Unverified; a fresh whole-document review returning literal PASS is required before verified downstream consumption. v1 retained; no directory roll.
