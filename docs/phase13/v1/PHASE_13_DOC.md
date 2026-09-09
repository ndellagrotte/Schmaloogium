# Schmaloogium — Phase 13: Texture systems — Architecture

## 0. Header

- **Phase:** 13 — Texture systems
- **Milestone:** v0.5 (`docs/design/v3/DESIGN.md:2438`; RESEARCH v0.5 row at
  `docs/research/v1/RESEARCH.md:951`)
- **Module/package:** `:engine` texture policy plus `:mod` glue and mixins — §2.1 states the exact
  placement and Phase 1's owner-designed/receiver-adopted, unverified texture-package grant
- **Declared dependencies:** Phases 3, 5, 7 (`docs/design/v3/DESIGN.md:625`)
- **Assigned open questions:** none — the phase row's `OQs` cell is `—`
  (`docs/design/v3/DESIGN.md:2438`)
- **Governing design:** `docs/design/v3/DESIGN.md`, Part I §G0–§G12 and the Phase 13 specification
  only
- **Design status:** architecture-only integration amendment through §0.16 plus the 2026-09-08 attempt-11 fix-up; verified per §G1.3
- **Date:** 2026-09-07

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
| `docs/phase3/v1/PHASE_3_DOC.md` | §1.2 ownership row (`:663`), §3.2 texture rows (`:1737`–`:1744`), §3.3 noise row (`:1795`), §4.4 macros (`:2296`–`:2442`), §5.1 in full (`:3368`–`:4161`) | dependency contract |
| `docs/phase5/v1/PHASE_5_DOC.md` | Original-build historical reads: §1.2 ownership (`:382`), resize consumer types (`:617`–`:621`), `addResizeConsumer` (`:1807`), §4.12 (`:1869`–`:1957`), §5.1 (`:2000`–`:2020`), §6 (`:2116`), §9 (`:2321`); coordinated current contracts are cited in §5.2 | historical input record; not current line pins |
| `docs/phase7/v1/PHASE_7_DOC.md` | Original-build §1.2 ownership, hook need9, executor step5, §4.10.1 catalog, AppE10/11 deferral, atlas event, reload and hand-off contracts | originally provisional; Review36 cleared that history, coordinated §5 is newly unverified (§0.2/§5.4) |
| `docs/phase1/v14/PHASE_1_DOC.md` | package tables (`:1723`–`:1796`), seam constraints C-1…C-4 (`:2541`–`:2559`) | module placement and the D-6 seam |
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
  (`docs/phase7/v1/PHASE_7_DOC.md:18`).
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

### 0.7 Integration consumer amendment — IR-03/04/06/08/21/23/24

Active §§1–5/8/9/11/12 adopt Phase 3's catalog-bound same-build configuration and lossless
texture declarations, schema18 after the IR-03/24 locale/session/macro cutover; independent
decoded normal/specular preferences precede load. Phase 1 package, Phase 3 R1, Phase 6
R7-10/11 and Phase 8 R7-12/13 grants are owner-designed and receiver-adopted here, **unverified**,
not still absent. P7 owns authenticated current-bind delivery to P6; P5 retains all physical
draw binding. P14 consumes exact parameter/lifetime policy only, with optional extensions gated.
Reads: current owner grants and §5 contracts, P12 reload/codec and P3 incorporated option,
texture/persistence contracts, RESEARCH §§0–1 and v3 Phase 12/13 scopes. Historical addenda
and quotations above remain history, not active stripped-suffix or unchanged-load fallbacks.
§5 changed; fresh whole-document verification remains required. No implementation or validation
was performed and no PASS/conformance claim is made.

### 0.8 U1 documented-mechanism adoption — 2026-09-07

The maintainer selected **Correct requirement to documented mechanisms** in
`docs/decisions/U1_TEXTURE_SAMPLING.md`. `[D-P13-26]` adopts that narrow correction for
this v3-governed phase: required custom keys are `texture.<stage>.<sampler>[.0-.9]`,
where the optional terminal digit is a duplicate discriminator, never sampling state.
Phase3 retains parsing, typed source specs and complete lossless declarations; Phase13
retains `.mcmeta` blur/clamp interpretation. No extra filter/wrap key syntax, typed suffix
API, or future required U1 grant is part of full v0.5 scope.

Reads for this adoption: complete Phase13, v3 Part I and Phase13 spec; RESEARCH §§0–1,
§4.6 texture part, §9, Apps B.3/B.4/D.3/E/F.3/F.5; PD §§7.4/7.6/11 and relevant
§17/§18 rows; permitted texture digest; Phase3 texture contracts and U1 correction route.
Primary published documentation read on 2026-09-07:
[OptiFine custom-texture block](https://raw.githubusercontent.com/sp614x/optifine/master/OptiFineDoc/doc/shaders.properties)
explicitly distinguishes `.0`–`.9` from `.mcmeta`, and
[Iris Docs `.mcmeta` section](https://shaders.properties/current/reference/buffers/custom_textures/#mcmeta-file)
describes blur/clamp. Only these documented mechanisms are adopted, not modern features.

This notice supersedes earlier U1 requirement/gate statements in historical addenda and
decisions; those records, old reviews, RESEARCH and original design text remain unchanged.
Existing defaults/error policy are not newly ratified (§11.3 records the separate concern).
No parser/payload behavior or type changes: schema18 remains. §5 changes received their fresh whole-document
owner/receiver reviews at the attempt-11 wave; IR-01's disposition is recorded 2026-09-09. No PASS,
implementation, build, test or verification claim is made.

### 0.9 Source-specific sidecar policy and schema19 receipt — 2026-09-07

`[D-P13-27]` adopts the maintainer's separate local compatibility selections in
`docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md`; §4.3.5 replaces the generic absent/false
table, §4.5.1 advances `phase13.parameters/v1` to `phase13.parameters/v2`, and §5
incorporates the exact unchanged parameter type with the new meaning. Recovery/noise defaults
are maintainer policy, not G6 author-document or runtime evidence. Generated noise is unchanged.
`[D-P13-28]` adopts P3 D-P3-68/§0.61 schema19, including matching nested IdMappingInput;
all prior schema18 receipts remain historical and are superseded for current consumption.
Reads: complete P13, v3 globals/P13 specification, RESEARCH §§0–1/App F.5, the decision,
P3 complete §5/§11 and exact native provenance declarations, and complete affected consumer §5.
No implementation, validation, PASS or conformance claim; reviews closed at the attempt-11 wave; IR-01 disposition recorded 2026-09-09.

### 0.10 Snapshot-bound binary acquisition — 2026-09-08

D-P13-30 adopts P3 §0.62/D-P3-69's actual `PackConfiguration.assets()` grant and
schema20, with matching nested IDs. It closes TS-1's missing preparation input:
P13 reads same-load owned bytes after archive closure, not a reopened selected path.
§§4.3.2/5.2 incorporate the exact acquisition/failure/lifetime contract; P7 retains
the configuration and its capability through resource-only NONE. Historical schema19
receipts describe the earlier native grant, not current acceptance. No binary data
enters source-free inspection. Architecture adoption is unverified.

### 0.11 Round 55 schema21 receiver — 2026-09-08

D-P13-31 adopts P3 §0.63/D-P3-70/§5; earlier dated schema receipts are provenance only.
Current gates require schema21, including nested identities. The documented rectangle token now
reaches the existing typed acquisition/upload path; assets and nine-tree inspection meanings
remain D-P3-69's. No implementation, validation, fresh PASS or optional execution grant is claimed.

### 0.12 Attempt-5 allocation and atlas corrections — 2026-09-08

D-P13-35/36 resolve R5 C1/C2 at the owner boundary: bounded actual allocation-extent
capture accepted only at stitch Post, and reciprocal P1 D-P1-63/P5 D-P5-42 mandatory
target-bearing synchronous values with exact source conversion. D-P13-37 receives
P3 CURRENT_SCHEMA_VERSION=23/MaterializedSource-v23; all prior numeric receipts are
historical. D-P13-38 receives P14 D-P14-31's complete owned-object baseline on every
successful setter, including ordinary fixed-function clearing, not only demotion.
Selected evidence is the governing v3 P13 specification, RESEARCH texture/format/hook
rows, affected P1/P5 contracts, R5, MCP TextureUtil.allocateTextureImpl signature and
the available Cleanroom-0.6.12-alpha TextureMap patch allocation/Post boundary. That
available patch is corroboration, not the absent historical Cleanroom pin or runtime proof.
The historical Pintonium licensing statement concerns the governing PD/pinned evidence;
it is not clearance to reuse arbitrary available Pintonium-main files (R5 N1).
§5 changes remain unverified: fresh owner/receiver whole-document reviews and implementation
proof are required. No build, tests, validation commands, fresh PASS or implementation
clearance is claimed; original review bodies and authority are preserved.

---
### 0.13 Attempt-6 outer atlas lifetime and limit receipt — 2026-09-08

D-P13-40 resolves Review6 C1 by moving H13-ATLAS-06 to the Pre-owning outer
loadSprites invocation, with a true try/finally covering Pre listeners, sprite population,
inner allocation and Post listeners. Eight IDs remain; the target-bound application domain
advances to `TextureHookHealth/application-v2`. D-P13-41 receives P1's target-specific
immutable capability limits in actual pure preparation. §§4/5/8/11/12 carry the live
contracts. Reads: selected v3 globals/P13 scope, Review6, current affected owner/receiver
contracts, and the available Cleanroom 0.6.12-alpha TextureMap patch's Pre/populator
sequence. Historical pin/confidence qualifications remain; no implementation, validation,
fresh PASS, optional API grant or change to P3/schema23/nine inspection trees is claimed.

### 0.14 Attempt-7 companion policy and binding correction — 2026-09-08

D-P13-42/43 resolve Review7 C1/C2 with explicit owner sampling and actual accepted-base
selection through P5/P7/P8. Reads: selected v3 globals/P13 scope, permitted texture digest §8,
RC3 receiving scope, owned algorithms/declarations and affected P5/P7 contracts. The digest
corroborates matching mip chains and available filter choices, not this local default policy.
No new property grammar, source-pin/licence upgrade, implementation, runtime proof, validation
command, fresh PASS or optional modernization grant is claimed. §5 changes remain unverified.

### 0.15 Attempt-9 fix-up — Review 9 coordinate and identity corrections — 2026-09-08

Applied under §G1.3 from `docs/phase13/reviews/PHASE_13_REVIEW_9.md` (PASS-WITH-CORRECTIONS;
blocking=0, corrections=2, notes=3; resolutions recorded in that review's `## Resolutions`).
C1: three P7 coordinates D-P13-45 had claimed re-resolved were stale and are re-pointed with no
semantic claim change — §1.1 item 6 and D-P13-45 now cite the `DEFERRED(P13,v0.5)` rows at
`docs/phase7/v1/PHASE_7_DOC.md:1863-1864`, §4.6's catalog/health sentence cites the §4.10.1
class table, audit and plugin rows at `:1657-1680`, and §5.2's Phase 7 row cites `:3901-3993`
(downstream slot plus complete ten-step transaction). C2: the document's only named P4 registry
identity domain was the superseded own-build-v1; §5.2's Phase 4 consumption row now records P4
D-P4-43 `RegistryFingerprint/profile-selection-v3` as the current opaque identity, D-P13-46
records the receiver adoption, and D-P13-31's historical own-build-v1 sentence in §5.2 is marked
superseded. The review's notes were recorded, not applied; none is incorporated by a correction.

§5 changed in this fix-up (one §5.2 Phase 4 consumption row). Per §G1.3 the document requires a
fresh whole-document verify session before verified downstream consumption; the standing
unverified posture above covers these bytes.


### 0.16 Attempt-10 fix-up — Review 10 coordinate corrections — 2026-09-08

Applied under §G1.3 from `docs/phase13/reviews/PHASE_13_REVIEW_10.md` (PASS-WITH-CORRECTIONS;
blocking=0, corrections=3, notes=3; resolutions recorded in that review's `## Resolutions`).
All three corrections are coordinate repoints of the Review9 P7 anchors with no interface shape,
semantic, ownership, or identity change. Re-verified at fix-up time against current P7 bytes:
§1.1 item 6 cites the `DEFERRED(P13,v0.5)` rows at `docs/phase7/v1/PHASE_7_DOC.md:1863-1864`
(ledger heading `:1845`, rows `:1863`/`:1864`); §4.6's catalog/health sentence cites the §4.10.1
class table and plugin audit at `:1657-1680` (table `:1667-1674`, `require=0`/`expect=1` audit
`:1676-1680`); §5.2's Phase 7 consumption row cites `:3901-3993` (downstream slot `:3901-3922`
plus the complete ten-step transaction `:3945-3993`). D-P13-45 records Review9's stale
intermediate values as historical. The review's notes were recorded, not applied: N1 is a
Phase 7-side coordinate defect left to the Phase 7 owner (current P7 bytes repoint it to this
document's §5.1 frozen-inputs row, which verifies); N2 independently confirms §4.2.2's arithmetic;
N3's history-preservation and fresh-verify mechanics are honored here. No new decision IDs.

§5 changed in this fix-up wave (one §5.2 Phase 7 consumption-row coordinate,
`docs/phase7/v1/PHASE_7_DOC.md:3880-3972` → `:3901-3993`). Per §G1.3 the document requires a
fresh whole-document verify session before verified downstream consumption; the standing
unverified posture above covers these bytes. **Attempt-11 sweep note (2026-09-08):** the P7 coordinates recorded in §0.15/§0.16 above (`:1863-1864`, `:1657-1680`, `:3901-3993` and their sub-ranges) are the pre-sweep fix-up-time values, now historical; the attempt-11 anchor sweep repointed the live §1.1/§4.6/§5.2 citations to `:1884-1885`/`:1690-1698`/`:3923-4015`, all verified against current P7 bytes. Recorded in place — no byte above the P3-pinned `:865-877` window moved.

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
   Phase3-owned `.0`–`.9` discriminators remain distinct keys; `.mcmeta` blur/clamp is the
   sampling mechanism. Extra filter/wrap property-key syntax is neither required nor supported
   under the adopted U1 correction (§0.8).
4. **The `atlasSize` value source** — the ivec2 that is valid only while the atlas texture is bound.
5. **Texture publication production** — source preparation, allocation, upload, content identity,
   expected-id lease acquisition, ownership and retirement. Shared candidate/snapshot/lease/result
   shapes belong to Phase 5; source and parameterization content semantics belong to Phase 13.
6. **App E rows 10 and 11** — the `TextureMap` and `TextureAtlasSprite` hook sites Phase 7 deferred
   to this phase (`docs/phase7/v1/PHASE_7_DOC.md:1884–1885`: “DEFERRED(P13,v0.5)”).

### 1.2 Adjacent ownership — explicit "owned by Phase Y" lines

| Concern this phase touches | Owner outside Phase 13 |
|---|---|
| The sole fixed App B.3 name/policy/resolver, shared candidate/snapshot/lease/result shapes, full-shape compatibility and physical unit binding | **Phase 5**, `docs/phase5/v1/PHASE_5_DOC.md:2734–2743`; Phase 13 produces candidates and never assigns units |
| Uploading sampler integers for those units, and uploading the `atlasSize` ivec2 | **Phase 6**; Phase 13 is the *value source* for `atlasSize` only (`docs/design/v3/DESIGN.md:2479`–`:2481`) |
| Tangent-frame math and any shading that consumes the sampled normals | **Phase 10** (`docs/design/v3/DESIGN.md:2485`–`:2486`) |
| labPBR channel semantics — what the specular channels *mean* | **pack-side convention; engine-neutral.** G8 advertises it (`docs/design/v3/DESIGN.md:2486`); Phase 13 delivers bytes and interprets none of them |
| Parsing texture keys, lossless declaration disposition, retained sidecar references and noise options | **Phase 3 §5.1**; current schema23 retains same-load owned binary acquisition and typed external rectangle mapping; Phase13 never reparses properties |
| Emitting `MC_NORMAL_MAP` / `MC_SPECULAR_MAP` | **Phase 3 §5.1**, R1 owner-designed and adopted here, unverified; Phase 13 supplies independent preliminary values before load, not completed linked demand |
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
| `:engine` | `com.schmaloogium.engine.textures` | companion planning, noise, custom spec resolution, sidecar policy, candidate/atlas values, failures; no Minecraft/Forge/LWJGL |
| `:mod` | `com.schmaloogium.mod.glue.textures` | atlas/resource adapters, stitch and actual-bind observation, facade-backed uploads |
| `:mod` | `com.schmaloogium.mod.mixin.textures` | dumb accessors/tick observations in §4.6 |

Phase 1 §§0.24/2.1/5.1 grants these exact three homes. R3 is owner-designed and
receiver-adopted, **unverified**; no alternate package or missing-name fallback remains.
The grant adds no facade verb or module edge and does not relax C-1…C-4.

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
    TextureLeaseResult lease(TextureOverlayPublicationId expected, ProgramBindingSelection selection,
        AtlasBindingEvidence baseBinding);
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
    REGISTRY_FINGERPRINT_MISMATCH, STALE_SELECTION, INVALID_BASE_BINDING, STALE_BASE_BINDING
}
public record PreliminaryCompanionDemand(
    boolean packActive, boolean fixedUnitCapabilityAvailable,
    boolean normalMapEnabled, boolean specularMapEnabled) {}
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
public enum MinFilter {
    NEAREST, LINEAR, NEAREST_MIPMAP_NEAREST, LINEAR_MIPMAP_NEAREST,
    NEAREST_MIPMAP_LINEAR, LINEAR_MIPMAP_LINEAR
}
public enum MagFilter { NEAREST, LINEAR }
public enum WrapMode { REPEAT, CLAMP_TO_EDGE }
public record TextureParameterFingerprint(String value) {}
public record TextureFailure(TextureFailureCode code, String messageKey,
    String diagnosticId, String logicalTextureIdentity) {}
public enum TextureFailureCode {
    INVALID_REQUEST, WRONG_THREAD, OWNER_UNAVAILABLE, IDENTITY_MISMATCH,
    SOURCE_UNAVAILABLE, SOURCE_DECODE_FAILED, SOURCE_SIZE_INVALID,
    TARGET_FORMAT_UNSUPPORTED, PARAMETERIZATION_UNSUPPORTED,
    BACKEND_FAILURE, UNEXPECTED_INTERNAL
}
public record TextureSourceCatalog(long resourceReloadEpoch, List<TextureSourceAsset> assets) {}
public sealed interface TextureSourceAsset {
    record ReadyAsset(TextureSourceIdentity identity, TextureTarget target,
        List<Integer> dimensions, ColorInternalFormat format,
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
The `ColorInternalFormat` in ReadyAsset is the exact P5/P1 shared allocation value,
not the undefined InternalTextureFormat. P3 Raw's 37 enumerants convert one-for-one;
the private RGBA_COMPAT allocation value is never accepted from a raw declaration.
The concrete owned payload contract is `TextureUploadPayload(List<TextureData> initialUploads,
List<TextureFramePayload> animationFrames)` with `TextureFramePayload(String iconName,
int sourceFrameIndex,List<TextureData> uploads)`. Lists are immutable and byte views
are stable read-only owned preparation data. Initial uploads are ascending mip order;
animation rows are ascending iconName UTF-8 bytes then sourceFrameIndex, each row's
uploads ascending mip. No duplicates, gaps in required initialized coverage or out-of-bounds
regions; sprite frame payloads preserve exact atlas mip origins/extents. P1 borrows each
TextureData cursor synchronously, without content/cursor mutation or retention.
Metadata plans/fingerprints contain no bytes/cursors. Foreign preparation carries no payload.
Owned/foreign prepared variants must agree with identity kind and catalog metadata; missing,
extra, duplicate, changed-content, capability or epoch mismatches fail before allocation.
Catalog preparation is mod glue over Phase3 typed references and the exact same configuration's `assets()` (§4.3.2), without properties reparsing or selected-pack reopening.
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
    record DefaultFill(CompanionKind kind) implements CandidateOrigin {}
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
    BaseAtlasContext baseAtlasContext();
    Optional<TextureHandleRef> baseTexture();
    BaseAtlasContext atlasContext(TextureHandleRef base);
    boolean isCurrent();
    void close();
}
public sealed interface BaseAtlasContext {
    record Atlas(AtlasId atlas) implements BaseAtlasContext {}
    record NonAtlas() implements BaseAtlasContext {}
    record Unavailable() implements BaseAtlasContext {}
}
```

Publication values may carry Phase 1 opaque handles. `Owned` means **Phase 13** owns the object,
never that a consumer does; `Borrowed` means vanilla/foreign ownership. Phase 5 owns framebuffer
and neutral objects and performs final physical selection. `isCurrent()` is true only for an
open lease whose issuing owner still has that very publication READY, not RETIRING/CLOSED,
and whose accepted actual-base serial remains current (§4.5.2).
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
| F5-1 | Typed complete key → §4.3.1 exact name retained | `docs/phase3/v1/PHASE_3_DOC.md` §5.1 TextureBindingKey / lossless declaration contract | `sharedUnit_exactNameAndOrdinal` |
| F5-2 | Numeric discriminator remains separate → original key/ordinal on each candidate | `docs/phase3/v1/PHASE_3_DOC.md` §5.1 exact discriminator and canonical-order contract | `sharedUnit_exactNameAndOrdinal` |
| F5-3 | Pack-relative PNG → owned decoded upload, §4.3.2 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:91-91` | `publication_contentIdentity` |
| F5-4 | Minecraft decoded asset → owned upload, §4.3.2 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:95-95` | `publication_contentIdentity` |
| F5-5 | Dynamic lightmap → borrowed live object, §4.3.2 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:98-98` | `publication_foreignReloadIdentity` |
| F5-6 | Atlas resource → borrowed live object, §4.3.2 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:99-99` | `publication_foreignReloadIdentity` |
| F5-7 | `_n`/`_s` source variants → companion/default identity, §4.3.2 | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:100-101` | `companion_discoveryPerSprite` |
| F5-8 | Raw path/target/internal format/dimensions/transfer → §4.3.3 source-bearing upload | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:104-110` | `sharedUnit_programSpecificTargets` |
| F5-9 | Four raw targets retain arities1/2/3/2 → §4.3.3 | `docs/phase3/v1/PHASE_3_DOC.md` §5.1 CustomTextureSpec.Raw | `sharedUnit_fullSamplerShape` |
| F5-10 | Several target types per fixed unit, one type per program → §4.3.4 Phase4 layout + Phase5 selection | `[V:doc]` `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:112-116`; `docs/research/v1/RESEARCH.md:1488-1490` | `sharedUnit_programSpecificTargets`, `sharedUnit_incompatibleAliasesBeforeBind` |
| F5-11 | Sidecar blur → §4.3.5 source-specific omitted/false/true and atomic recovery policy | `[V:doc]` primary `.mcmeta` section linked in §0.8 for fields/image/raw defaults; `[D-P13-27]` separately supplies recovery/noise policy | `custom_mcmetaBlurSetsFilter`, `sidecar_atomicRecovery` |
| F5-12 | Sidecar clamp → §4.3.5 effective wrap; no property-key wrap syntax | `[V:doc]` primary `.mcmeta` section linked in §0.8; Phase3 §5.1 TextureSidecarRef; `[D-P13-26]` | `custom_mcmetaClampSetsWrap` |
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
| B3-6 | Fixed map, depthtex1=11, gbuffers12 unused; conditional shadow from effective watershadow → Phase5 resolver | `[D-P13-16]` `docs/design/v3/DESIGN.md:953-954`; `docs/research/v1/RESEARCH.md:1236-1255`; `docs/phase6/v1/PHASE_6_DOC.md:1221-1225` | `sharedUnit_fixedRangeAndShadowAlias` |

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
| F3-1 | Noise absent baseline disabled/256 → §4.2.1 | `docs/phase3/v1/PHASE_3_DOC.md` §5.1 ResourceRequirements baseline | `noise_resolutionFromRequirementsAndBaseline` |
| M-1 | Normal macro option → §4.1.6 independent preliminary preference and adopted Phase3 input | `[V:doc]` Phase3 §5.1; `[D-P13-10]` | `macro_independentPreferencesBeforeJcpp` |
| M-2 | Specular macro option → §4.1.6 independent preliminary preference and adopted Phase3 input | `[V:doc]` Phase3 §5.1; `[D-P13-10]` | `macro_independentPreferencesBeforeJcpp` |

### 3.5 Hook and Pintonium do-not-inherit ledger

PD rows record evidence, a row-local decision and a contract check; none adopts the decompile's
identifiers or a dependency's ungranted implementation.

| # | Evidence and decision | Contract check → design / future outcome |
|---|---|---|
| E-10 | `[V:mcp]` `docs/research/v1/RESEARCH.md:1410-1410` | §4.6 H13-ATLAS-01..04 → stitch catalog/size and post-tick snapshot |
| E-11 | `[V:mcp]` `docs/research/v1/RESEARCH.md:1411-1411` | §4.6 H13-SPRITE-01 read-only metadata/counter accessors; H13-SPRITE-02 dormant |
| PD-1 | `[V:observed — Pintonium targets/backed/NoiseTexture]` PD §11/§18, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:621-625`, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:815-815`; `[D-P13-3]` | RESEARCH `docs/research/v1/RESEARCH.md:596-597` requires xorshift, not Random(0) → `noise_signedRecurrence` |
| PD-2 | `[V:observed — Pintonium]` PD §18, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808-808`; `[D-P13-16]` | App B.3 `docs/research/v1/RESEARCH.md:1228-1255` rejects dynamic units → `sharedUnit_fixedRangeAndShadowAlias` |
| PD-3 | Historical Pintonium suffix-gap claim; `[D-P13-6]` superseded for U1 by `[D-P13-26]` | `docs/decisions/U1_TEXTURE_SAMPLING.md` corrects the requirement to documented discriminators plus sidecars (§0.8); Phase3 retains unknown segments as UNRESOLVED_KEY without executable support → `declarations_losslessWithoutSuffixParsing` |
| PD-4 | `[V:observed — Pintonium texture/pbr/PBRTextureManager]` PD §11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:631-637`; `[D-P13-4]` | RESEARCH `docs/research/v1/RESEARCH.md:594-595` requires full atlases, not per-bound-id keying → `companion_layoutAndMipChainMatchBase` |
| PD-5 | `[V:observed — Pintonium uniforms]` PD §11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:638-639`; `[D-P13-9]` | Shipped `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:177-177` requires real bound extent → `atlasSize_valueAndValidityWindow` |
| PD-6 | `[V:observed — Pintonium PackRenderTargetDirectives]` PD §17 B13; `[D-P13-12]` | Optional post-analysis unused allocation may be skipped, never enable a disabled preference or change pre-jcpp macro policy → `macro_independentPreferencesBeforeJcpp` |
| PD-7 | `[V:observed — Pintonium ProgramSamplers]` PD §17, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:796-796`; `[D-P13-17]` | App F.5 required binding `docs/research/v1/RESEARCH.md:1488-1490` needs closed outcomes, not false boolean → `binding_absenceVersusIncompatibility` |
| PD-8 | `[V:observed — Pintonium]` PD §11, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-629`; `[D-P13-15]` | `docs/design/v3/DESIGN.md:2471-2476` and `docs/research/v1/RESEARCH.md:1488-1490`: accept required v0.5 shared-unit capability through exact pack names, effective linked layouts and typed candidates; reject only generated customtexN names/source patching → `sharedUnit_programSpecificTargets` |

### 3.6 Input contradictions and binding rulings

1. Current Phase3 §5.1 retains the complete decoded `texture.*` declaration stream before
   collapse, including unresolved keys and invalid values. Its typed `textures()`/`noise()`
   projections do not strip guessed suffixes into a base key. Phase13 adopts both surfaces
   without reparsing. `[D-P13-26]` applies `docs/decisions/U1_TEXTURE_SAMPLING.md` to
   DESIGN v3's historical **"ours must honor them"** requirement: only documented `.0`–`.9`
   duplicate discriminators and `.mcmeta` blur/clamp are required. Extra/unknown key segments
   remain UNRESOLVED_KEY, not sampling support. No new typed suffix API or required U1 grant
   remains. This is an authorized requirement correction, not an inference that undocumented
   behavior cannot exist; existing sidecar defaults/error policy remain separate (§11.3).
2. Phase5's full FixedSamplerName/candidate/binding protocol fulfills R2 architecturally.
   Phase6 R7-10/11 and Phase8 R7-12/13 are owner-designed and receiver-adopted, unverified,
   not missing grants. Old narrow fallback D-P13-11 is historical.
3. Historical PASS covers historical bytes only. Current Phase3 schema23, Phase1 package
   grants and coordinated owner/consumer §5 contracts all require fresh whole-document reviews.
4. C-TX01 remains unresolved: RESEARCH's `0xFF7F7FFF` wins over PD's `0x7F7FFFFF`
   (`docs/research/v1/RESEARCH.md:595-595`, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:632-632`,
   `docs/design/v3/DESIGN.md:1088-1088`). §4.1.4 states byte-order assumption without silently swapping it.

---
## 4. Detailed design

### 4.1 Companion `_n`/`_s` atlases

#### 4.1.1 Demand and enablement

Before controller-owned Phase3 load, Phase7 obtains decoded independent user preferences
`normalMapEnabled` and `specularMapEnabled` from Phase3's global codec (each absent-key
default true), plus selected-pack/off state and fixed-unit capability. The static pure policy
computes `normalMap = packActive && fixedUnitCapabilityAvailable && normalMapEnabled` and
`specularMap = packActive && fixedUnitCapabilityAvailable && specularMapEnabled` `[D-P13-20]`.
It needs no texture owner, atlas, GL operation, configuration, registry or linked declaration.
All four preference pairs are meaningful. Never use completed linked demand before preprocessing.

```java
public record CompanionPolicy(boolean normalsEnabled, boolean specularEnabled,
                              CompanionDemandSource source) {}
public enum CompanionDemandSource { DECLARED_SAMPLERS, ALWAYS_ON_FALLBACK, CAPABILITY_GATED_OFF }
```

Without optional R4, allocate each enabled kind and **only** that kind according to the
already-chosen pair (ALWAYS_ON_FALLBACK); inactive/incapable yields CAPABILITY_GATED_OFF.
With R4, physical demand may only reduce an enabled kind's allocation, never enable a false
macro or revise preprocessing provenance. Disabled kind discovery/upload/animation allocation
is zero and its companion cell is `Absent(NOT_CONFIGURED)`. P5 still owns required compatible
neutral/fallback binding and typed suppression; disabled companions never authorize stale unit
reuse. Explicit pack custom textures remain a separate source policy, not a backdoor companion.
Build failure follows coherent compensation; it cannot alter the same-build macro pair.

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

**D-P13-42 — local owned sampling baseline.** Both full companion kinds derive minification
from the accepted `AtlasDescriptor.mipmapLevels`: greater than zero → NEAREST_MIPMAP_LINEAR,
zero → NEAREST. Magnification is NEAREST and every used wrap axis is REPEAT in either case.
Standalone 1×1, level-zero kind-default textures use NEAREST/NEAREST/REPEAT. A missing sprite
filled inside a full atlas uses that atlas object's mipped/nonmipped policy, not the standalone
policy. These are owner-authored compatibility choices; permitted digest §8
(`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:458-468,485-486`) corroborates
matching mip chains, filter vocabulary and fallback existence, not these selected defaults.
No base-object parameter query/copy, new pack property, sidecar or per-pack filter override is added.

Preparation derives the complete `TextureParameterSpec` from the accepted immutable mip count,
stores it in ReadyAsset and the produced candidate, and hashes baseline=effective with
NOT_APPLICABLE sidecar under §4.5.1. Build uses that exact prepared value in the mandatory
complete P1 setParameters conversion (§5.5), after allocating/uploading all required levels and
before publication. Failed allocation/upload/setter means no Ready candidate. Parameter recovery
cannot borrow a GL default. Any accepted mip-count/policy change rebuilds identity/parameters.

#### 4.1.5 Publication and stage applicability

Generate `CandidateOrigin.Companion(atlas,kind)` candidates with exact `normals`/`specular` names
and `FixedSamplerName.NORMALS`/`SPECULAR` only for GBUFFERS and SHADOW. They retain actual uploaded
sample/target/comparison capability. No `TextureBindingKey` is fabricated. Disabled kinds yield
`Absent(NOT_CONFIGURED)`; other stage columns yield `Absent(NOT_APPLICABLE_TO_STAGE)`.
An unavailable owner/publication is `PUBLICATION_UNAVAILABLE`, not a present empty table.
For every enabled kind also emit one `CandidateOrigin.DefaultFill(kind)` 1×1 level-zero owned
candidate in those two stages, with the exact §4.1.4 bytes/policy and DEFAULT_FILL role.
It is not tagged with an arbitrary AtlasId. Disabled kinds allocate neither full atlases nor
standalone defaults; custom precedence remains available. Canonical generated order is
Companion entries by unsigned-UTF8 AtlasId then kind, DefaultFill entries by kind, then Noise;
candidateOrdinal is deterministic enumeration, never companion-selection authority.

Phase5 resolves names to2/3 only when declared by the effective provider. D-P13-43 requires
its actual §4.12.2 branch to resolve the winning base unit0 object first, then consume this
lease's accepted Atlas/NonAtlas/Unavailable association. Only the corresponding atlas/kind
companion can win after compatible custom precedence. NonAtlas/Unavailable or absent compatible
matching companion uses only the kind's explicit default; absent/incompatible required default
suppresses the draw. Equal shape/dimensions and candidateOrdinal never choose another atlas.
Generated companions/defaults do not enter fullscreen columns; compatible custom fullscreen
colortex2/colortex3/gnormal/composite overrides remain unchanged.

#### 4.1.6 `MC_NORMAL_MAP` / `MC_SPECULAR_MAP` wiring

§2.2 publishes the preliminary producer. Phase7 adapts its result to Phase3-owned required
`CompanionOptionMacros(boolean normalMap,boolean specularMap)` immediately after engineOptions
in `PackLoadRequest`, **before load/jcpp**. Phase3 §5.1 retains the exact pair in
MacroConfiguration and same-build materialization and both fingerprints. TexturePlan retains
that provenance unchanged and rejects a pair inconsistent with its configuration.
Missing non-Off data is INVALID_REQUEST; Off short-circuits; earlier milestones pass explicit
(false,false), never call an old API. R1 is adopted/unverified, not absent.

The separate materializer contribution remains Empty/DefineCenterDepthSmooth, not a general
macro bag. P13 injects no macro text, uses no renderer-feature second gate, and never selects
macros from linked-program demand. `[D-P13-10]` preserves P3 emission ownership.

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

Resolution and enablement come from Phase3 §5.1's `ResourceRequirements.noise`,
`NoiseRequirement(boolean enabled,int resolution)`, whose absent-directive baseline
is disabled/resolution256. The texture is `resolution × resolution`, internal format
RGB, unsigned-byte transfer, wrap `REPEAT`, filter `LINEAR` — authority: the design
decision record (D-P13-27's noise-role baseline; PD §11 "GL_RGB, LINEAR/REPEAT, `Random(0)`"
at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:621-625`), behaviorally
corroborated at `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:469-471`).

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

`NoiseTextureSpec.Override(image, sidecar)` (Phase3 §5.1) replaces the
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

Consume Phase3 §5.1 `TextureBindingKey(stage,sampler,duplicateDiscriminator)` from typed
`textures()` unchanged. Separately retain `properties().textureDeclarations()`:
`TexturePropertyDecl(key,value,sourceOrdinal,attribution,disposition)` with exact decoded
occurrence order and closed CUSTOM_SOURCE/NOISE_SOURCE/UNRESOLVED_KEY/INVALID_VALUE.
Use disposition/attribution for diagnostics only; **never** parse key/value strings, collapse
them, reconstruct suffix state or infer sidecar precedence. Only typed executable specs
create candidates. Unresolved declarations can change configuration identity even when
executable textures do not change `[D-P13-21]`.

Enumerate the immutable custom-spec list in Phase3 canonical order: property-stage order,
unsigned-UTF8 sampler, absent discriminator then0..9, source kind PackPath/MinecraftResource/Raw.
Complete-key duplicate last-valid-wins has **already** happened before sorting
(Phase3 §5.1). Assign one zero-based canonical `phase3Ordinal` per
original list entry; every expanded copy retains it and the entire original key.
Expand GBUFFERS→GBUFFERS+SHADOW, DEFERRED→DEFERRED, COMPOSITE→COMPOSITE+FINAL.
Call Phase5's pure exact-name lookup for `FixedSamplerName`; retain exact key/name and enum,
never a unit. SHADOW's conditional alias stays unresolved until the effective provider layout.
No lowercasing, generated sampler names, or source patching occurs.
For published candidates, `candidateOrdinal` equals `phase3Ordinal` for custom origins. After
the complete custom-list range, companion origins receive consecutive ordinals in unsigned-UTF8
AtlasId canonicalName order, then CompanionKind declaration order; standalone DefaultFill
origins follow by kind and Noise receives the next ordinal. Expanded copies retain the same
ordinal. Absent/generated-disabled entries do not
become candidates, but their canonical plan/absence values remain fingerprint inputs. This
producer ordering never ranks a companion or noise ahead of a compatible custom candidate.

#### 4.3.2 Source forms and content identity

| Phase3 source | Prepared source / ownership |
|---|---|
| `PackPath(key,image,sidecar)` | PACK_PNG OwnedUpload: immutable input digest, decoded2D payload, effective sidecar parameters |
| `MinecraftResource(key,resourceIdentity)` decoded asset | MINECRAFT_DECODED_ASSET OwnedUpload: exact logical resource, immutable byte digest and decoded parameters |
| Same variant naming dynamic/lightmap or atlas | ForeignLive: borrowed live object, exact resource identity, reload epoch and logical object epoch; never copy/allocate/delete vanilla storage |
| `Raw(key,bytes,target,internalFormat,dimensions,pixelFormat,pixelType,sidecar)` | RAW_BYTES OwnedUpload plus exact target/format/dimensions/transfer and byte digest |

The source grammar is Phase3 §5.1 `CustomTextureSpec`; live forms and variants are
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

**Snapshot acquisition — D-P13-30/TS-1.** Preparation receives the exact P3-issued
configuration retained by P7. Before decoding, enforce exact CURRENT_SCHEMA_VERSION equality
(23 under D-P13-37), matching nested IdMappingInput, required same-load assets,
its exact configuration pairing and `assets.pack()`. Reject 22/all other schemas without repair.
A digest or copied inspection manifest cannot manufacture that capability. Cross-load/
missing capability is `IDENTITY_MISMATCH`, not absent texture or permission to reopen.

P3's exact `PackAssetSnapshot` exposes `pack()`, canonical immutable `manifest()`, and
`acquire(NormalizedPackPath)`. The closed result is `Acquired(PackAssetBytes bytes)`,
`Missing(NormalizedPackPath path)`, `Unreadable(NormalizedPackPath path)`, or
`InvalidReference(PackAssetReferenceFailure reason)` (`NULL_PATH|NOT_DECLARED`).
`PackAssetBytes.metadata()` is the exact manifest row
`PackAssetMetadata(path,availability,OptionalInt byteCount,Optional<String> sha256)`;
availability is `AVAILABLE|MISSING|UNREADABLE`. Only AVAILABLE has count/hash.
`openCursor()` returns an independent position-zero read-only heap ByteBuffer over
immutable same-load bytes, not an archive stream, native upload buffer or mutable array.
Its initial order is BIG_ENDIAN; raw transfer decoding follows the already specified
PixelType contract, not an accidental cursor default.

For PackPath, acquire `image`; for Raw, acquire `bytes`; for noise Override, acquire
`image`. Decode/validate readable primary bytes before interpreting its sidecar.
Missing primary produces entry-local `SOURCE_UNAVAILABLE` (noise retains §4.2's
explicit diagnosed generated fallback). Primary read failure already prevents P3 Loaded;
an impossible primary Unreadable result or metadata/ref mismatch is `IDENTITY_MISMATCH`,
not a source recovery shortcut. InvalidReference indicates broken consumer/configuration
pairing and is `IDENTITY_MISMATCH`; never retry another path or reconstruct a declaration.
Existing image decode/size/format failures retain their closed codes and precedence.

Only the retained optional `TextureSidecarRef.path` is acquired for sidecar parsing.
Empty Optional is absent/baseline; P3 additionally records adjacent Missing metadata.
Present reference requires Acquired or Unreadable from that same snapshot; Missing
under a present reference contradicts P3 issuance and is `IDENTITY_MISMATCH`.
Acquired supplies all bounded immutable bytes to §4.3.5. Unreadable is P3's positively
classified optional-sidecar-only failure: apply atomic baseline, exactly one warning,
and §4.5.1 `UNREADABLE/NONE/IO/DISCARDED/DISCARDED` outcome, with no fake digest of
partial bytes. P3 emits no duplicate warning. A sidecar path also required as shader,
include, configuration, locale, ID or primary asset remains a fatal P3 load failure,
as do unsafe/bounds/index/container failures; this is not a broad input-error waiver.

P3 manifest SHA-256 is evidence of original retained bytes; P13 contentDigest additionally
incorporates the existing decoded/upload/role/parameter inputs. Neither digest is a
resource generation or authorization token. Share acquisition/parse evidence per same-load
path; do not copy whole payloads per stage/discriminator, hash per draw or share mutable
cursors. Role-specific baselines still apply independently to PNG/raw/noise consumers.
After decoded immutable payloads/metadata are prepared, release unneeded cursors and
scratch; configuration retains its assets for later NONE refresh. No archive handle is
kept open, and host replacement/deletion cannot change the prepared bytes or fingerprint.
Minecraft/resource/atlas/companion acquisition remains its existing owner channel;
generated noise never calls this pack-binary capability.

#### 4.3.3 Raw upload and full capabilities

Phase3 validates grammar, four target arities and integer transfer compatibility
(Phase3 §5.1 CustomTextureSpec.Raw). Phase13 validates checked byte-size products,
exact payload length, each dimension against target-specific capability limits, and effective
internal format through Phase5's published App B.4 policy. No overflow may turn an invalid size
into an accepted allocation. An entry-local source failure becomes catalog `FailedAsset` evidence;
it is not a source-less upload request.

**D-P13-41 — actual pure target-limit receipt.** Preparation consumes P1
§§4.7.2/4.7.7a/D-P1-66's immutable `GLCapabilityProfile`, including `int max3DTextureSize`
and `int maxRectangleTextureSize` immediately after `maxTextureSize`; it performs no
GL query or guessed fallback. Check positive arity/checked size/exact payload first.
For a supported target, compare every used axis with its exact recorded maximum:
1D width and 2D width/height use `maxTextureSize`; 3D width/height/depth use
`max3DTextureSize`; RECT width/height use `maxRectangleTextureSize`. Decoded images,
generated noise, defaults and actual captured companion extents use the 2D maximum too.
An over-limit supported extent is SOURCE_SIZE_INVALID before allocation. Zero in either
new field means unsupported target, not a zero-sized extent or a fallback to maxTextureSize;
skip that unsupported target's limit comparison and classify TARGET_FORMAT_UNSUPPORTED
at §6's target/format stage after source/decode/structural-size checks.

The 3D gate is GL≥1.2. Rectangle support is GL≥3.1 OR GL_ARB_texture_rectangle OR
GL_EXT_texture_rectangle OR GL_NV_texture_rectangle. P1 captures GL_MAX_3D_TEXTURE_SIZE
or GL_MAX_RECTANGLE_TEXTURE_SIZE only when the corresponding gate is true, with a
mandatory positive result and required image/subimage entrypoints. False gate means zero
without querying an unsupported enum/entrypoint. Advertised support with missing entrypoints,
query failure or a nonpositive maximum fails profile capture, never publishes unsupported-zero.
Required canonical decimal profile keys `max.3DTextureSize` and `max.rectangleTextureSize`
serialize/replay exactly; absent/invalid/inconsistent values cannot be repaired from the 2D
maximum, a driver guess, or legacy defaults. P2 owns replay; P13 consumes the same validated
profile facts in native and headless planning. This receipt grants no new native allocator,
renderer or optional API, and leaves P3/schema23/nine metadata-only trees unchanged.

**D-P13-31 rectangle receiving route.** P3 alone parses exact external `TEXTURE_RECTANGLE`
with two positive dimensions into existing `CustomTextureSpec.Raw` with `TextureTarget.RECTANGLE`.
Bare external `RECTANGLE` is INVALID_VALUE upstream, not an alias or P13 recovery input.
P13 never reparses source tokens or retained declaration strings. The surviving typed Raw
acquires its actual `bytes` from §4.3.2's same-load assets, validates payload size/capabilities,
and yields existing `TextureUploadSpec.Rectangle` and RECTANGLE candidate capability.
Legal effective §4.3.5 parameters reach owned upload and existing P5 compatible sampler2DRect
selection/binding. No new P3 target enum or sampling default is needed: floating-format raw with baseline LINEAR and
CLAMP_TO_EDGE is legal; repeat/mipmap or incompatible integer filtering still fails before
allocation as PARAMETERIZATION_UNSUPPORTED, never coerced to manufacture rectangle success.

`TextureUploadSpec` remains a handle-free P13 planning sum, not a facade argument:
`OneD(int width,ColorInternalFormat internalFormat,PixelFormat pixelFormat,PixelType pixelType)`,
`TwoD(int width,int height,ColorInternalFormat internalFormat,PixelFormat pixelFormat,PixelType pixelType)`,
`ThreeD(int width,int height,int depth,ColorInternalFormat internalFormat,PixelFormat pixelFormat,PixelType pixelType)`,
`Rectangle(int width,int height,ColorInternalFormat internalFormat,PixelFormat pixelFormat,PixelType pixelType)`.
Formats here are the exact P5/P1 closed shared engine allocation/transfer values after
one-to-one conversion from P3's typed enumerants. Positive dimensions and the following
mandatory conversion are binding, not an implied acceptance of this sum by allocate.
Fingerprints include effective validated target/format/extent/layout/mips, never handles.

**D-P13-36 — exact synchronous source-to-facade conversion.** P1 §4.7.7a/D-P1-63
and P5 §4.2/D-P5-42 grant target-bearing TextureSpec/TextureData/TextureRegion and
PixelLayout. Every owned route creates its own handle, allocates the following exact
ColorTextureSpec, uploads prepared initial TextureData, applies the complete legal owner
parameters, and publishes only after clean drains and restoration. No engine GL, extra
format policy/parser, inferred target, new async API or source-less payload is permitted.

| Prepared source / planning variant | Exact allocation and initial upload |
|---|---|
| Raw OneD | target TEXTURE_1D, extent(width,1,1), mipLevels1, declared ColorInternalFormat and PixelLayout.Color(declared PixelFormat,declared PixelType); one level0 region(0,0,0,width,1,1) with unchanged acquired bytes |
| Raw TwoD | target TEXTURE_2D, extent(width,height,1), mipLevels1, same declared format/layout; level0 region(0,0,0,width,height,1) |
| Raw ThreeD | target TEXTURE_3D, extent(width,height,depth), mipLevels1, same declared format/layout; level0 region(0,0,0,width,height,depth), x then y then z byte order |
| Raw Rectangle | target RECTANGLE, extent(width,height,1), mipLevels1, same declared format/layout; level0 region(0,0,0,width,height,1), never 2D despite equal dimensions |
| Pack PNG; decoded static Minecraft asset, including a found `_n`/`_s` resource; pack noise Override | TwoD → TEXTURE_2D, actual decoded extent(width,height,1), RGBA8, Color(RGBA,UNSIGNED_BYTE), mipLevels1; level0 full region containing decoded RGBA channel bytes, no noise-resolution rescale |
| Generated noise | TEXTURE_2D, extent(resolution,resolution,1), RGB8, Color(RGB,UNSIGNED_BYTE), mipLevels1; level0 full region containing the exact §4.2 recurrence bytes, unchanged generator/defaults |
| Owned 1×1 missing companion/default-fill resource | TEXTURE_2D, extent(1,1,1), RGBA8, Color(RGBA,UNSIGNED_BYTE), mipLevels1; level0 full region with §4.1.4's literal channel order/default, no C-TX01 reinterpretation |
| Full normal/specular companion atlas | TEXTURE_2D, captured accepted extent(width,height,1), RGBA8, Color(RGBA,UNSIGNED_BYTE), mipLevels=`AtlasDescriptor.mipmapLevels+1` (vanilla value is highest index); each level0..highest gets its complete assembled atlas bytes at region(0,0,0,max(1,width>>l),max(1,height>>l),1) |
| Foreign live Minecraft atlas/lightmap/resource | authenticate exact resource/object/epoch and retain borrowed handle; no create/allocate/upload/setParameters/generateMipmap/delete. The handle-free TwoD descriptor is compatibility metadata only, never guessed storage authority |

All TextureData values repeat the exact allocation target, carry their actual mip index
and Color layout, and reference the paired immutable prepared bytes. Companion animation
uses the existing TextureFramePayload's sprite-local regions at each admitted level,
origin `(originX>>l,originY>>l,0)` and extent `(max(1,spriteWidth>>l),
max(1,spriteHeight>>l),1)`; §4.1.7 interpolation produces a stable prepared region batch
with the same layout. Atlas bounds/coverage and matching base mip chain are validated
before upload; there is no atlas GL readback or uninitialized companion gap.
Nonmipped custom/noise sources remain one level; no sidecar value silently generates
mipmaps. RECT base/max=0, legal clamp/nonmip filters and integer-nearest rules still
reject before allocation under §4.3.5, rather than normalizing a source to make it legal.

P1's count/region/native dispatch/byte layout and P5's complete transfer legality are
incorporated: tightly packed rows/slices, alignment1/zero unpack strides and skips/no
swap, scalar component width versus one packed word, exact checked remaining byte length.
Raw byte sequences are not reinterpreted according to the PackAssetSnapshot cursor's
BIG_ENDIAN default; native upload consumes those bytes unchanged. Decoded RGBA and
generated RGB emit explicit bytes, preserving §4.1.4's independent C-TX01 assumption.
Same-load source/parameter/epoch checks precede GL. Source/decode/size/target/parameter
failure retains its existing exact TextureFailureCode; native allocation/upload/set/restore
failure is BACKEND_FAILURE with no published candidate. Reverse-order owned cleanup,
retiring leases, no borrowed deletion and P7's off compensation remain unchanged.
Upload owns no pixel memory after synchronous return; initial and animation preparations
retain their own lifetime until no future upload can use them.

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
3. With no compatible custom, retain NO_CANDIDATE versus INCOMPATIBLE_CANDIDATE. For generated
   world/shadow normals/specular consume §4.1.5's exact accepted-base association and matching
   atlas/kind or explicit kind-default rule, never ordinal selection among atlases. Resolve
   custom/foreign unit0 first; when undeclared use the observed lease base context. All other
   compatible noise/estate/foreign/neutral rules remain. A compatible custom overrides every
   fixed backing, including fullscreen colortex and deferred gaux.
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

Phase3 §5.1 retains `TextureSidecarRef` and snapshots its bytes without interpreting JSON.
Phase13 acquires that same-load snapshot input through §4.3.2, never a host path or properties.
`texture.blur` and `texture.clamp` below name members of the sidecar's JSON `texture` object, not dotted shaders.properties keys.
The published `PackPath`, `Raw` and noise `Override` retain sidecar references;
`MinecraftResource` has none. Shader-provided sidecars do not configure resource-pack,
atlas or dynamic sources; borrowed objects retain actual foreign parameters.
`[D-P13-27]` adopts source-specific baselines from the separately approved decision:

| Owned source / policy role | Absent sidecar; omitted field in valid sidecar |
|---|---|
| Ordinary PackPath PNG (`PACK_PNG`) | min/mag NEAREST; wrap REPEAT |
| Raw (`RAW_BYTES`) | min/mag LINEAR; wrap CLAMP_TO_EDGE |
| Pack noise override (`PACK_PNG`, noise role) | min/mag LINEAR; wrap REPEAT |

An explicit Boolean blur true selects LINEAR for both min/mag; false selects NEAREST.
An explicit Boolean clamp true selects CLAMP_TO_EDGE; false selects REPEAT. Missing fields
retain their own baseline, never false. No mipmaps are requested by these two fields.
Noise's distinct role participates in identity even though its source kind remains PACK_PNG.

The exact local parser contract is one strict UTF-8 JSON object (optional leading UTF-8 BOM,
JSON whitespace only outside the object; no comments, trailing commas, extra document or
nonfinite number tokens). `texture` may be absent (valid, both fields omitted); if present it
must be an object. Inside it `blur` and `clamp` may be absent or JSON booleans only: null,
strings, numbers, arrays and objects are malformed, never coerced. Duplicate decoded member
names in any object are malformed, not last-wins. Unknown root/texture members are ignored
semantically without diagnostics, but their values must be valid bounded JSON; they grant no
animation, mipmap, anisotropy or other sampling feature. Invalid UTF-8/unpaired surrogate
escapes are malformed. Decode all input before committing either override.

Use the existing bounded snapshot/preparation discipline: never follow a new host path or
evade P3 pack entry/byte/path limits. P13 additionally limits each sidecar to 1,048,576 bytes
and JSON container depth 64 (root depth 1), counting ignored values too; these are local
finite parser limits, not claimed author limits. A streaming bounded parse must stop at
the first excess without truncating into a valid document. Exceeding either is a malformed
sidecar outcome, not permission to retry unbounded. P3 D-P3-69 retains only proven optional
owned-sidecar-only read failures for this recovery; all other fatal load failures remain P3 failures.

Absent sidecar uses baseline without a diagnostic. Malformed or unreadable retained sidecar
atomically discards **all** overrides, retains the usable source at its source-specific
baseline and emits one sanitized warning per retained sidecar outcome per preparation
transaction, shared across stage/discriminator copies. No valid earlier field survives a
later failure. Do not reopen/retry during planning or per draw. Fingerprint the outcome as
§4.5.1 specifies; diagnostics contain logical identity, not bytes, parser exception or host path.
Generated noise retains §4.2's existing parameters/generator with no sidecar parse.
Minecraft decoded assets use their resource-owner metadata, never shader-sidecar defaults;
borrowed objects expose actual foreign parameters and consumers never mutate them.
Phase5 logical-buffer mipmap work cannot overwrite custom/foreign override parameterization.

`TextureParameterSpec` remains exact effective min/mag/wrap state, applied only after
§4.3.3 target/format validation. RECTANGLE rejects REPEAT and mipmap minification; integer
storage requires nearest-only filtering (min NEAREST or NEAREST_MIPMAP_NEAREST and mag
NEAREST). A default is not an exemption: raw integer baseline LINEAR is incompatible until
a valid explicit blur=false supplies NEAREST. Invalid combinations become entry-local
FailedAsset before allocation, with the existing compatible-fallback/missing-backing
disposition in §6, not silent coercion or whole-pack parser failure. Required unavailable
backing still suppresses that program. §6 closes the exact PARAMETERIZATION_UNSUPPORTED
failure; P1's complete synchronous parameter mapping remains mandatory (§11.3).
PD §11 explicitly supports `.mcmeta` (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-630`);
the historical suffix-gap requirement is corrected by `[D-P13-26]`, not evidence that PD
drops sidecars. No property-key sampling precedence or decoder is introduced.

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
Implementation remains gated by fresh owner/receiver reviews of adopted Phase6/8 grants,
not missing grants or old narrow R2.

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
- `atlasSize()` is a convenience query for the catalogued block/item atlas;
  `atlasSize(AtlasId)` queries any exact current-epoch catalogued atlas and otherwise returns
  Unknown. Known means **size available**, not evidence that the atlas is currently bound.
- Stitch publishes catalog metadata only. Resource invalidation resets it to Unknown.
- Runtime uniform validity requires authenticated **actual current binding**, not stage name,
  a prior stitch or a linked sampler declaration. Entity textures, custom overrides and
  shadow draws need not bind the atlas.
- P7 publishes opaque `AtlasBindingEvidence` with private Optional<AtlasId>, current
  PipelineVersion, resourceReloadEpoch and monotonically increasing bindSerial. Only its
  installed mod binding-observer can mint evidence after successful actual P5 Bound
  physical binding/restoration or observed vanilla base-texture binding. It exposes no
  forgeable public record/constructor; P13 supplies exact atlas identity/catalog association.
- `AtlasBindingSink.currentBinding(AtlasBindingEvidence) -> SignalResult` is P7-owned.
  It authenticates issuer, render thread, current composition/resource epoch and latest
  serial before querying this owner's atlasSize(id). Known maps to P6
  `updateAtlasSize(new Int2(width,height))`; absent atlas, unknown catalog, non-atlas binding
  or reset maps to `updateAtlasSize(new Int2(0,0))`.
  P6 uploads immediately when a program is active, otherwise updates its cache for next bind.
- Release/restoration, resource reload, Off and owner replacement invalidate the previous
  evidence and publish/reset Unknown before readmission. Stale or foreign evidence is rejected,
  never allowed to restore an old atlas value. Candidate adapters never update the old runtime.
  The observer performs no physical bind; P5 remains the sole shader physical-binding owner.
  This query/issuer-adapter agreement is `[D-P13-22]`; it adds no P6 channel or fourth participant.

D-P13-43 extends that same observer/issuer association, not a second atlas resolver.
The private accepted catalog maps authenticated base-object incarnation plus resource epoch to
AtlasId only after matched allocation/Post acceptance. Replacement/failure invalidates the
mapping; equal resource strings, sizes or numeric native names never revive it. Evidence carries
the observed opaque base handle when available and distinguishes known NonAtlas from Unavailable.
The installed P7 issuer adapter supplies P13 authenticated observation/currentness access at
construction; public evidence remains opaque. No engine code reads Minecraft objects or GL.
For a prospective P5 winning unit0 handle the lease's pure `atlasContext(base)` looks up this
same immutable accepted map: exact accepted base → Atlas, authenticated known non-atlas →
NonAtlas, absent/unprovable association → Unavailable. This does not itself assert a physical bind.
P5's successful transaction verifies/commits actual winning base observation before Bound;
failure cannot publish a predicted association as actual atlasSize.

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

Reuse Phase3 §4.10's canonical framing: canonical base10
ASCII scalars/UTF8 strings with decimal byte-length + colon; lists with element count, opening
bracket, length-prefixed elements and closing bracket. SHA-256 with distinct
`phase13.texturePublication/v1`, `phase13.source/v1`, `phase13.parameters/v2` domains is a **new
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

`phase13.parameters/v2` supersedes v1 because omitted-field and recovery meaning changed.
No v1 digest may be relabelled or reused as v2; regenerate from the same validated prepared
source. `TextureParameterFingerprint.value` is the lowercase 64-hex SHA-256 of the §4.5.1
length-framed ordered tuple: domain; source-kind tag; policy-role tag
`CUSTOM_PNG|RAW|NOISE_OVERRIDE|GENERATED_NOISE|RESOURCE_OWNER|COMPANION|DEFAULT_FILL|FOREIGN_OWNER`;
baseline min/mag/wrap enum names; sidecarDigest; effective min/mag/wrap enum names.
For COMPANION/DEFAULT_FILL the baseline and effective triples are exactly D-P13-42's full-atlas
or standalone rule, with NOT_APPLICABLE sidecar; GENERATED_NOISE retains §4.2's explicit policy.
Other no-sidecar roles retain representable actual resource/foreign owner parameters, never guessed.

`ReadyAsset.sidecarDigest` is lowercase SHA-256 of the same framed tuple with domain
`phase13.sidecar/v1`; optional normalized path (explicit ABSENT or PRESENT+canonicalString);
outcome `NOT_APPLICABLE|ABSENT|VALID|MALFORMED|UNREADABLE`; byte evidence
`NONE|COMPLETE` plus SHA-256 of all bounded bytes when COMPLETE; failure class
`NONE|ENCODING|JSON|SHAPE|DUPLICATE_MEMBER|BYTE_LIMIT|DEPTH_LIMIT|IO`; and blur then clamp
presence tags `OMITTED|FALSE|TRUE|DISCARDED`. VALID retains omission/Boolean distinctions;
MALFORMED/UNREADABLE uses DISCARDED for both. Absent/not-applicable uses OMITTED and no bytes.
Unreadable/over-byte-limit uses NONE (no partial-byte hash); complete malformed bounded input
uses COMPLETE. First failure in byte/decode/parse order wins; field semantics check texture,
then blur, then clamp after full syntax/duplicate validation. No exception message, diagnostic
serial or wall time enters either digest. Equal malformed outcomes without complete bytes
may share a digest; that is not proof of byte equality or permission to revive ownership.
Source/publication domains retain v1 framing but include this v2 parameter digest and sidecar
digest, so policy/outcome changes propagate through existing content identity without a type change.

#### 4.5.2 Atomic acquisition and bind ownership

`lease(expected,selection,baseBinding)` validates against this active owner **before incrementing its count**:
READY/current publication availability, expected publication id, selector registry fingerprint,
then Phase4 credential/current-selection validity, then P7 evidence issuer/thread/composition,
then latest actual-base serial/resource epoch. Rejections in that order are PUBLICATION_UNAVAILABLE,
PUBLICATION_ID_MISMATCH, REGISTRY_FINGERPRINT_MISMATCH, STALE_SELECTION,
INVALID_BASE_BINDING, STALE_BASE_BINDING; no lease is returned on rejection.
The old two-argument operation is removed. Every ordinary/fullscreen/nested/shadow caller supplies
the installed observer's current evidence, including explicit Unavailable, never a forged empty value.
Acquired snapshots `baseAtlasContext`, optional `baseTexture` and the immutable accepted-object
association for `atlasContext(base)`; invalid handle input is Unavailable, not Atlas. These
lease-local observations are not fields of the static TexturePublication or its content digest.
`isCurrent()` additionally requires the latest authenticated base serial. External actual bind/
restoration, reload, off, owner replacement or execution invalidation revokes it before next draw,
without discharging ownership. P5's own transaction defers observer dispatch until completion,
commits its resulting serial into the private lease currentness token and publishes actual atlasSize
once; expected echoes do not recursively refresh. The retained observation/value/map are immutable,
while the private currentness token follows only this authenticated transaction commit.
Binding.close is ownership-only, performs no physical base bind/restore and cannot change the
triggering serial. Before-draw refresh closes old binding, obtains current evidence (same triggering
actual object/serial), acquires a new lease and reruns P5 on the same open pass/retained selection.
Only Bound transfers; other results/throws close caller lease, suppress remainder and take ordinary
frame or shadow containment. Already drawn gbuffers work is completed through its normal no-flip
terminal; an undrawn pass is discarded; no failure invents a committed draw or extra activation.
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
uniform access; its adopted resolver cutover is R7-10. After Bound, finally closes **binding
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
must not leave new registry/estate paired with old objects. Phase6 retirement uses adopted
R7-11 retire(reason) and closed outcomes, never UniformRuntime.close/reset(CLOSE).

### 4.6 Hook catalog — App E rows 10 and 11

Use `docs/phase7/v1/PHASE_7_DOC.md:1690–1698`'s catalog and health classes: CORE disables the shader group, FEATURE disables
the feature, OBSERVER observes. Phase13 uses FEATURE rows, `require=0`, normally `expect=1`;
Phase7's plugin audits application. Forge stitch events supply timing, read-only accessor
mixins supply private data. `[D-P13-7]`

| ID / class | SRG target and injection | Dumb bridge call | Health / evidence |
|---|---|---|---|
| H13-ATLAS-01 TextureMap | Forge TextureStitchEvent.Pre, no injection | `TextureHooks.onAtlasStitchBegin(atlasIdentity)` | FEATURE; invalidate catalog/pixels/animation/atlasSize and prior extent; start the scoped map/resource-epoch stitch token; AppE10 |
| H13-ATLAS-02 TextureMap | Forge TextureStitchEvent.Post, no injection | `TextureHooks.onAtlasStitched(atlasDescriptor)` after §4.6 acceptance | FEATURE; require exact current map/token/epoch and one matching successful allocation capture; Post alone accepts copied descriptor/size; no inferred width/height |
| H13-ATLAS-03 TextureMap | @Accessor `field_94252_e`, `field_94258_i`, `field_147636_j` | read-only sprite table/animated list/mip count | FEATURE; mapUploadedSprites/listAnimatedSprites/mipmapLevels; event exposes only getMap, no extent getter `[V:mcp]` |
| H13-ATLAS-04 TextureMap | `func_94248_c()V` updateAnimations TAIL | `TextureHooks.onAtlasAnimationsUpdated(AtlasAnimationSnapshot snapshot)` | FEATURE; full post-vanilla snapshot, not identity alone; §4.1.7 |
| H13-ATLAS-05 TextureUtil | `func_180600_a(IIII)V` allocateTextureImpl RETURN | private glue authenticates native texture argument, then `TextureHooks.onAtlasStorageDefined(width,height,mipmapLevels)` on current scoped token | FEATURE; one application, exactly one matching successful allocation per stitch; no GL query or mutation |
| H13-ATLAS-06 TextureMap | `func_174943_a(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/renderer/texture/ITextureMapPopulator;)V` loadSprites, outer original-body wrapper with try/finally | private glue enters before the original body/Pre and exits after the whole Pre→populator→inner allocation→Post lifetime; finally invalidates unsuccessful/no-Post token and releases scope | FEATURE; one wrapper application and bounded outer attempt scope; every original/listener exception is enclosed, HEAD/RETURN pair alone cannot satisfy cleanup |
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

**D-P13-35 — bounded actual allocation extent.** MCP 1.12.2 independently confirms
TextureUtil.allocateTextureImpl/func_180600_a `(IIII)V` parameters as native texture id,
mipmapLevels, width, height. The available
`reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/texture/TextureMap.java.patch:144-188`
corroborates local Stitcher dimensions passed into that allocation before Post; it is
not the absent historical pin, an implementation copied here, or runtime proof.
Observe the allocation method's successful RETURN within the scoped map load; do not
infer extent from sprites or introduce a nonexistent TextureMap getter/new GL dimension query.

**D-P13-40 — Pre-owning outer attempt.** The available Cleanroom patch above at
`:20-27` places Stitch Pre before the populator inside loadSprites, outside
loadTextureAtlas. H13-ATLAS-06 therefore wraps the complete outer
`func_174943_a(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/renderer/texture/ITextureMapPopulator;)V`,
not inner `func_110571_b(Lnet/minecraft/client/resources/IResourceManager;)V`.
Enter the private scope before invoking any original body; its try/finally encloses
Pre dispatch and every listener, registerSprites, missing-image initialization/native
object replacement, inner atlas allocation, Post dispatch and every listener, and return.
This is permitted patch/mapping evidence, not recovery of the historical pin or runtime proof.

All scope/map/native-identity work is private mod glue. The wrapper retains at most one
active map token, one captured `(width,height,mipmapLevels)` and fixed scalar counters,
not a per-sprite or per-allocation log. Outer entry reserves the map/epoch scope and
invalidates prior availability before original work; only matching Pre may mint its one
scope-local stitch serial. Pre invalidates previous accepted/pending data and binds the
same map and current resourceReloadEpoch. Duplicate Pre poisons rather than remints.
The authenticated platform provider supplies the current native identity privately at
allocation capture (after the outer body's possible object replacement), not an old
pre-replacement name; raw names/TextureMap objects never cross the seam. Allocation
captures only that active Pre-started map's authenticated object, positive dimensions,
nonnegative admitted mip count and target capability limits. Unrelated allocations are
ignored. Duplicate matching allocations, reentry/nested conflicting map loads, mismatched
token/object/epoch or invalid extents poison this attempt; there is no last-observation win.
Nested work runs vanilla unchanged with the feature unavailable; it neither replaces nor
releases the outer owner's scope, creates another token, nor restores availability when
it returns. No unbounded scope stack or guessed outer capture is permitted.
Direct inner loadTextureAtlas calls without the required outer scope and matching Pre,
or unscoped Pre/Post, fail closed: no token/capture/descriptor/Known or retained pending
state; invalidate affected atlas availability without manufacturing an outside-to-inside
handoff. An allocation that throws has no RETURN capture; a native failure during the
attempt also invalidates it.

Post is the only acceptance moment: matching current map/token/epoch/authenticated native
object, exactly one valid capture, mip count equal to H13-ATLAS-03 and every copied
sprite/mip region within that extent are mandatory before constructing AtlasDescriptor or
setting Known. Post consumes the token's acceptance once; it only stages catalog data
while P7's resource gate is closed. It cannot clear poison or reopen consumed acceptance.
The outer owner's finally clears its private scope on every exit, including a later Pre
listener or populator throwing before any inner load begins. Original completion is marked
only after the whole outer body returns normally; any exception (including a later Post
listener), failure, poison, or missing/duplicate Post discards pending and accepted data
from the attempt and leaves atlasSize Unknown. Cleanup preserves vanilla exception/lifecycle
ownership. Normal completion after one valid Post retains accepted data but no private
scope/token. Pre, resource reload before replacement, owner close and any stitch/listener
failure invalidate capture, catalog, animation and size. Only a later valid current-epoch
outer attempt with matching Post can restore availability; outer resource failure still
discards staged data per §4.7.
Hook failures never cancel/replace vanilla stitching or allocate companions while shaders-off.

P7 must catalogue the exact ordered rows H13-ATLAS-01, -02, -03, -04, -05, -06,
H13-SPRITE-01, H13-SPRITE-02: eight rows, seven active FEATURE rows plus one dormant
FEATURE row. The target change retains these eight IDs/count meanings but supersedes
application-v1 with `TextureHookHealth/application-v2`; old-domain reports are stale,
never relabeled. Application expectation for each new injected row is require=0/expect=1;
event rows remain event subscriptions, not fictitious injection counts. Runtime capture
expectation is one matching allocation per accepted stitch, not one globally per frame.
P7's frozen HookApplicationReport forwards application counts only for both new scalar
rows, unchanged after application. Runtime capture/mismatch is separate P13 catalog
availability, diagnostics and atlasSize Unknown under existing feature containment;
it never overwrites frozen counts or invents a health field. Missing/duplicate capture
or missing exceptional wrapper prevents atlas size/companion publication, not unrelated
noise/custom sources. P2 receives the same exact IDs/order and planned boundary scenarios;
P7 receives the runtime acceptance gate separately, not as application-health evidence.
Planned cases: equal-sized sprite sets in differently padded atlases report actual extent;
two maps/epochs cannot exchange captures; missing/duplicate allocation, changed mip count,
Post without Pre, direct/unscoped inner load, Pre-listener/populator throw before inner
entry, throw before/after Post, nested load, reload failure and stale animation remain
Unknown/unpublished with no retained scope; a normal outer Pre→populator→allocation→Post
return retains the exact accepted dimensions and releases its one scope.

### 4.7 Reload and resize transitions

On PACK_SELECTION, OPTION_CHANGE, RESOURCE_RELOAD, DIMENSION_CHANGE and registry replacement,
Phase7 quiesces first, ends/aborts the old frame and shadow invocation, and invalidates stale
selectors/leases for new drawing before rebuilding. Resource reload advances resourceReloadEpoch
and invalidates catalog/metadata/copied pixels/current snapshots/foreign object identities and
atlasSize **before vanilla replacement**. Only accepted current stitch data may restore Known.
Option equality cannot keep an old publication paired to a new generation; registry/estate
replacement issues a new publication id using the **actual accepted estate generation**.
Unload/off retires then closes as §4.5.3 specifies.

P12's `ReloadRequest(NONE,false,true,RESOURCE_RELOAD)` is adopted as resource-only work:
the exact PackConfiguration/schema/fingerprint, its assets capability and same-build macro pair remain unchanged. Pack PNG/raw/noise/sidecar input stays frozen even if the selected host pack changed; resource-owner data is reacquired at the new resource epoch.
P7 drains texture bindings, shadow work and P9/P10 ID/geometry readers before resource replacement.
The adopted P7 §4.8.1/§5.1 H-RESOURCE-01 wrapper performs that drain and retirement
synchronously before the resource-manager body releases old packs. It advances the epoch
before release and holds admission closed until the outer body and every listener return.
H13-ATLAS-01/02 only invalidate/copy pending current-epoch data during that gate; they do
not publish live resources or stand in for pre-destructive quiescence. Nested replacement
bodies share the outer gate. A failed original discards pending data and follows P7 off
compensation; listener notification does not trigger a duplicate refresh.
After the outer body returns, P7 refreshes the P13 catalog/publication and resource-derived IDs
at the new resourceReloadEpoch and atomically re-admits only the coherent refreshed tuple. No P3 discovery/load, old pair
recalculation or implicit FULL promotion is permitted. `worldRendererReload` remains a separate
OR flag; P7/P10 execute any required ID invalidation barrier even when that GUI flag is false.
Failure compensates off; the previous resource publication is never resumed.

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

- **Companions.** Each enabled kind costs one atlas at the base extent and mip chain.
  A mipped RGBA atlas costs approximately `4/3 × width × height × 4` bytes; both enabled
  kinds cost twice that. Without optional R4, independent preliminary preferences in §4.1.1
  determine allocation even when no linked sampler demands it. Disabled kinds cost zero;
  declaration-based allocation reduction remains gated by R4 and never changes the macro pair.
  Sum that full-atlas cost over every accepted atlas and add exactly four RGBA8 level-zero bytes
  per enabled kind for its one standalone default; those owned objects enter the same build/
  retirement ledger and remain retained until every sharing publication lease drains.
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

**D-P13-39/40 P7 health receipt:** P7 receives §4.6's eight ordered FEATURE entries
through ownerPhase13's existing nested shape, with active expected1 and dormant SPRITE02
expected0. Event rows count subscriptions, member rows complete independently verified
target-bound bundles, injected rows exact applied anchors/wrappers; no compensating sums.
Receive `TextureHookHealth/application-v2` (superseding D-P7-65's application-v1) canonical fingerprint/disposition/aggregate
law. Failed application health makes atlas observation/companions unavailable, not unrelated
noise/custom source preparation. Runtime Post-only extent acceptance remains separate
catalog/diagnostic/Unknown state, never frozen count mutation or a new health field.
The domain binds H13-ATLAS-06 to the complete outer loadSprites SRG descriptor in §4.6,
with Pre/populator/inner allocation/Post and all exceptions enclosed. Canonical bytes remain
UTF-8 domain plus LF, then ordered `id|expected|actual|disposition` plus LF per row,
then `featureEnabled=true` or `featureEnabled=false` plus LF; lowercase SHA-256, unchanged
canonical decimal counts and exact disposition/aggregate laws. P7/P2 must reject stale
application-v1 evidence, retain eight IDs/seven active/one dormant, and receive §8.2's
pre-inner failure/normal outer/direct-inner scenarios independently of frozen counts.

Every §2.2/§2.3 declaration is incorporated into these binding rows, including exact component
order/types, closed variants, defaults, validation, ordering, fingerprints and lifetime. Every
consumer-visible change updates this region in the same revision; referenced signatures are
not merely illustrative.
U1 changed requirement authority only; the separate `[D-P13-27]` adoption now changes effective
parameter meaning and digest to `phase13.parameters/v2`, not the three-field type.
§4.3.5's complete parser/default/atomic recovery/legality policy and §4.5.1's exact outcome
encoding are binding here. P5/P7/P14 consume that result, never choose defaults independently.

| Contract | Exact incorporated exposure / consumer |
|---|---|
| Inactive owner construction | `TextureSystemFactory.create(GLDevice gl,DiagnosticReporter diagnostics) -> TextureSystemCreationResult.Created(TextureSystem system) \| Failed(TextureFailure failure)`; no allocation, atlasSize Unknown; Phase7 owns |
| Planning/build | `TextureSystem.plan(TexturePlanRequest request) -> TexturePlanResult.Planned(TexturePlan) \| Invalid(TextureFailure)`; `build(TextureBuildRequest request) -> TextureBuildResult.Ready(TexturePublication) \| Failed(TextureFailure)` |
| Frozen inputs | `TexturePlanRequest(PackConfiguration configuration,ProgramRegistryView registry,AtlasCatalog atlases,TextureSourceCatalog sources,CompanionPolicy companionPolicy,CompanionMacroState macroState,GLCapabilityProfile capabilities,RegistryFingerprint registryFingerprint,long estateGeneration,long registryGeneration,long resourceReloadEpoch)`; `TextureBuildRequest(TexturePlanRequest planRequest,TextureBuildSources sources)` |
| Source preparation | Exact §2.3 TextureSourceCatalog/ReadyAsset/FailedAsset, TextureBuildSources/Owned/Foreign and payload pairing; metadata-only plan, pre-allocation identity/capability/epoch validation |
| Texture plan | `TexturePlan(TexturePlanRequest inputs,List<CompanionAtlasPlan> companions,NoisePlan noise,List<CustomTexturePlanEntry> customTextures,List<UnsupportedBinding> unsupported,CompanionMacroState macroState,TextureMemoryEstimate memory)` |
| Custom plan entry | `CustomTexturePlanEntry(TextureBindingKey key,int phase3Ordinal,FixedSamplerName name,Set<StageId> stages,DeclaredGlslType.Sampler shape,TextureTarget target,TextureSourceIdentity source,TextureUploadSpec upload,TextureParameterSpec parameters,TextureParameterFingerprint parameterizationFingerprint)`; handle-free original key/content retained |
| Non-owning publication | `TexturePublication(TextureOverlayPublicationId id,RegistryFingerprint registryFingerprint,long registryGeneration,long resourceReloadEpoch,TexturePlan plan,TextureCandidateTable candidates)`; id.generation is accepted estate generation |
| Restricted lease source | `TextureLeaseSource.lease(TextureOverlayPublicationId expected,ProgramBindingSelection selection,AtlasBindingEvidence baseBinding) -> TextureLeaseResult.Acquired(TextureOverlayLease lease) \| Rejected(TextureLeaseRejection reason)`; TextureSystem exposes the identical required operation, no old overload or separate public id getter |
| Lease failure domain | PUBLICATION_UNAVAILABLE, PUBLICATION_ID_MISMATCH, REGISTRY_FINGERPRINT_MISMATCH, STALE_SELECTION, INVALID_BASE_BINDING, STALE_BASE_BINDING; exact §4.5.2 precedence before incrementing count |
| Source identity | `TextureSourceIdentity.OwnedUpload(OwnedTextureSourceKind sourceKind,String logicalSource,String contentDigest,String configurationIdentity) \| ForeignLive(String exactResourceIdentity,long resourceReloadEpoch,long objectEpoch)`; owned source kind PACK_PNG/MINECRAFT_DECODED_ASSET/RAW_BYTES/GENERATED_NOISE/COMPANION_RESOURCE/DEFAULT_FILL |
| Effective parameters | `TextureParameterSpec(MinFilter minFilter,MagFilter magFilter,WrapMode wrap)` / `TextureParameterFingerprint(String value)`; exact enum domains §2.3, canonical source/parameter/publication hashes §§4.3.5/4.5.1; P14 conversion/gate §5.5 |
| Preliminary macro producer | `preliminaryMacroState(PreliminaryCompanionDemand(packActive,fixedUnitCapabilityAvailable,normalMapEnabled,specularMapEnabled)) -> CompanionMacroState(normalMap,specularMap)`; each output is active AND capable AND its independent decoded preference, before P3 load/jcpp; no linked-demand/default invention |
| Atlas metadata | Exact §2.3 AtlasId/SpriteDescriptor/AtlasDescriptor/AtlasCatalog/AnimationFrameDescriptor/SpriteAnimationMetadata; copied frames and immutable sequence, no Minecraft objects |
| Post-vanilla animation | `AtlasAnimationSnapshot(AtlasId atlas,long resourceReloadEpoch,long tickSequence,List<SpriteAnimationState> sprites)`; state `(String iconName,int sequencePosition,int currentSourceFrameIndex,int nextSourceFrameIndex,int elapsedTicks,int currentDuration,double nextFrameWeight)`; §4.1.7 epoch/frame-map/duration/tick validation |
| Size/close | `atlasSize(AtlasId)` / `atlasSize() -> Known(width,height) \| Unknown` supplies metadata only; §4.4's authenticated P7 current-binding adapter delivers P6 updateAtlasSize immediately/next-bind as appropriate. `close()` retires immediately and defers owned deletion until all leases drain |
| Hooks/resize | Exact H13-* bridge catalog §4.6 and transaction-bound `ResizeConsumerResult resize(BufferResizeNotice notice)` SUCCESS/FAILED; no guessed registry identity |
| Actual atlas extent / health — D-P13-35/40 | §4.6 exact bounded map/resource-epoch token, H13-ATLAS-05 allocation-return capture + H13-ATLAS-06 full outer loadSprites SRG wrapper enclosing Pre/populator/inner allocation/Post and all exceptions; Post-only acceptance, native-object authentication and fail-closed direct inner loads; P7/P2 receive eight ordered H13 rows (seven active, one dormant), application-v2 and separate runtime scenarios, no extent query |
| Mandatory owned upload — D-P13-36 | P1 §4.7.7a/D-P1-63 and P5 §4.2/D-P5-42 exact target-bearing ColorTextureSpec/TextureData/region/layout grant, §4.3.3 complete source conversion and §2.3 concrete payloads; ReadyAsset uses granted ColorInternalFormat; synchronous owned-only, unchanged failure/restoration/borrowed lifetime |
| Companion baseline/association — D-P13-42/43 | §§4.1.4–5/4.4/4.5.1–2: mipped NEAREST_MIPMAP_LINEAR versus level-zero NEAREST; mag NEAREST/wrap REPEAT; standalone NEAREST/NEAREST/REPEAT; complete ReadyAsset/fingerprint/setParameters flow. P5-owned BaseAtlasContext and lease observations/currentness select exact accepted unit0 atlas/kind after custom precedence, else explicit DefaultFill; P7/P8 refresh through P5 before next draw with unchanged activation/closure ownership |
| Diagnostics/memory | §4.3.6 exact UnsupportedBinding/RequestedTextureTarget/UnsupportedReason; §2.3 TextureFailure/TextureFailureCode and §6 exhaustive mapping, deterministic source-free payload and sidecar warnings; `TextureMemoryEstimate(long companionBytes,long noiseBytes,long customBytes)`; no raw GL names |

Phase5 owns the following shared protocol; Phase13 produces these exact values:

```java
record TextureBindingCandidate(CandidateOrigin origin, StageId expandedStage,
    String exactSamplerName, FixedSamplerName name, DeclaredGlslType.Sampler shape,
    TextureTarget target, TextureHandleRef handle, TextureSourceIdentity source,
    TextureParameterSpec parameters, TextureParameterFingerprint parameterizationFingerprint,
    int candidateOrdinal) {}
// CandidateOrigin = Custom(TextureBindingKey key,int phase3Ordinal)
//                 | Companion(AtlasId atlas,CompanionKind kind) | DefaultFill(CompanionKind kind) | Noise()
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
    BaseAtlasContext baseAtlasContext();
    Optional<TextureHandleRef> baseTexture();
    BaseAtlasContext atlasContext(TextureHandleRef base);
    boolean isCurrent();
    void close();
}
```

`isCurrent` requires an open lease, the same owning publication READY and the authenticated
latest base serial under §4.5.2. BaseAtlasContext has exactly Atlas(AtlasId), NonAtlas, Unavailable.
Borrowing never grants deletion. Phase13 owns allocated objects and releases, never deletes,
foreign handles. Plans/fingerprints are handle-free; publication candidates may contain opaque
handles. Closure duty is §4.5.2: Bound closes binding only, every non-transferring exit closes
acquired lease only. Holding a stale lease delays deletion but never authorizes drawing.

### 5.2 Consumed contracts and exact binding hand-off

| Owner / surface | Consumption |
|---|---|
| Phase3 §§2.2/4.1/5.1/5.3 | Exact schemaVersion == CURRENT_SCHEMA_VERSION=23 under D-P13-37, including equal nested IdMappingInput and received PackDecisionSnapshot; reject every other version before derivation/acquisition/retention/reuse. Typed rectangle/assets grants remain binding; no parser, reconstructed capability or inferred upgrade |
| Phase3 §§4.10/5.1 | Canonical framing/fingerprints and exact noise/resource requirements; lossless declarations affect identity even with identical executable projection; no inferred older-schema upgrades |
| Phase3 `PackConfiguration.assets()` | Exact PackAssetSnapshot/metadata/closed acquisition/read-only bytes §4.3.2; same-load provenance, bounds, sidecar-only error classification, acyclic fingerprint and post-archive lifetime. P13 interprets; P7 retains/transports. Inspection's ninth assets section is metadata only, never preparation input |
| Phase1 §§0.24/2.1/5.1 | Exact texture package trio granted and adopted here, unverified; seam constraints unchanged |
| Phase1 §§4.7.2/4.7.7a/D-P1-66 — D-P13-41 | Exact immutable maxTextureSize/max3DTextureSize/maxRectangleTextureSize and target gates, native positive-or-unsupported-zero capture, required decimal replay keys; §4.3.3 pure per-axis comparison/failure classification applies to every prepared owned source and atlas capture, never guessed 2D limits for 3D/RECT |
| Phase4 `docs/phase4/v1/PHASE_4_DOC.md:2144` | exact detached ProgramRegistryView, registry/policy fingerprints, ProgramSamplerLayout and opaque ProgramBindingSelection; line 2144: “resolve is detached handle-free inspection, not selection authority”. Current opaque identity is P4 D-P4-43 `RegistryFingerprint/profile-selection-v3` (`docs/phase4/v1/PHASE_4_DOC.md:1953-1954`), received opaquely at the existing identity checks under D-P13-46; D-P13-31's historical own-build-v1 mention is superseded |
| Phase5 `docs/phase5/v1/PHASE_5_DOC.md:2734–2743` | sole fixed-name/policy/resolver, candidate/binding results, accepted estate generation, formats and resize; line 2737: “Bound alone transfers lease into closeable sixteen-row snapshot with BoundObject/Unused” |
| Phase7 `docs/phase7/v1/PHASE_7_DOC.md:3923–4015` | active-tuple owner/publication/lease source, select-once orchestration, complete ten-step transaction |
| Phase6 §§0.23–0.24/5 | Adopted FixedSamplerResolver input and retire(reason); unchanged afterBind descriptor/context/uniform access, three participants, caches/tokens; fresh owner/receiver reviews required |
| Phase8 §§0.7–0.8/5 | Adopted registry-independent planning and full shared selector/context/publication/lease invocation; traversal and neutralization unchanged; real slot still gated on implementation/verification |

**Schema23 receiver receipt — D-P13-37.** Exact containing/nested-ID/received inspection
schema23 and MaterializedSource-v23 are current; all earlier numeric receipts, including
D-P13-32 below, are historical. P3 alone owns range-capable selector parsing; P13 consumes
the resulting typed same-load configuration. Nine metadata-only trees/projectionVersion1,
assets/native/options/parameter domains and P7's exact NONE retention remain unchanged.

**Historical schema22 receiver receipt — D-P13-32, superseded by D-P13-37.** P3 D-P3-72 supplied
containing/nested-ID/inspection schema22 and MaterializedSource-v22. Enforce owner-constant
equality before derivation, acquisition, retention or reuse; reject old/future objects.
BLOCK alternate provenance is not texture policy. Preserve same-load asset acquisition,
sidecar recovery, native/option inputs, nine trees/projectionVersion1 and texture parameters.
Older numeric/domain receipts below are historical; current owner identities remain opaque.

**Historical schema21 receiver receipt — D-P13-31, 2026-09-08 (unverified).** Adopt P3
§0.63/D-P3-70/§5 and MaterializedSource-v21 identity; earlier dated20 assertions remain
historical only. All assets acquisition/failure/provenance/lifetime and nine source-free
projectionVersion=1 tree meanings remain D-P3-69's, including metadata-only ninth assets tree,
TextHash digests and no bytes/cursors/providers. Profile selector shape changes only at its owner;
P13 invents no profile identity. A usable explicit OVERRIDE can load with empty base;
consume the selected typed configuration without requiring/merging base roots or selecting
another world's override for an absent key. Preserve exact P7 configuration/assets through NONE.
Carry opaque P4 RegistryFingerprint/own-build-v1 and invalidate older identity caches; requested
own-build evidence changes identity, not fallback, selector ownership or candidate compatibility.
Superseded 2026-09-08 (Review9 C2, D-P13-46): the current P4 identity domain is D-P4-43's
`RegistryFingerprint/profile-selection-v3`, carried opaquely with older-cache invalidation keyed
to it (§5.2's Phase 4 consumption row); own-build-v1 above is historical only.
Sidecar parameter/fingerprint domains, generated noise and foreign-resource ownership are unchanged.
No old-schema repair, empty-field upgrade, new timing API or optional grant follows; fresh reviews,
IR-01, optional R4 optimization and sampler/async/staging execution gates remain.

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
(`docs/phase6/v1/PHASE_6_DOC.md:1221-1225`). Both gbuffers bands share mapping. Virtual has no
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

The following full configuration/runtime replacement transaction is mirrored by Phase7
§4.1/§5.3, not an independent publication path. A resource-only NONE request instead takes
§4.7's quiescent same-configuration branch: no P3 work or P6 retirement solely for reacquisition.
That branch rejoins P7's final receipt/admission/error discipline, not these load steps.

1. Stop admission; finish/abort old frame, drain binding/shadow work, freeze intended
   reload/configuration/resource epoch. Failed rebuild means shaders-off, not restored prior
   composition. Compute independent preliminary preferences before Phase3.load and always
   supply the required R1 pair.
2. Consume adopted registry-independent R7-13 policy-before-Phase6 ordering. Real shadow
   still needs owner/consumer verification and implementation. Create inactive Phase13
   owner/adapters then Phase6 runtime; atlas metadata starts Unknown, and even accepted
   stitch does not emit a bound-atlas event. Candidate adapters never signal an old runtime.
3. Preserve Phase9 pure candidate build/frozen validation. Get `FixedSamplerPolicies.appB3()`;
   Phase4 compiles with that policy and the new runtime's existing macro contribution. Phase5
   plans/creates from the detached registry. AwaitingMainDepth publishes nothing and remains gated.
4. Compose barrier with that runtime's three participants. Optional Phase8 construction uses
   adopted interfaces and the final new registry fingerprint. Revalidate intended configuration/world/resource/
   hook/candidate identities. Precommit failure closes caller-owned candidates and takes old
   composition off; never resume a restitched stale atlas.
5. Close old Phase8 and retire old texture-event/resize registrations and the old texture owner after draw/binding drain at quiescence. Publish
   Phase4 Ready with issued release context; handle Accepted, Rejected and RecoveredOff separately.
   Rejected retains candidate ownership; RecoveredOff is a result, never RegistryPublication input.
   Retire old Phase6 through adopted R7-11 after old barrier invalidation.
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

**R1 — Phase3 typed load input: owner-designed / receiver-adopted, unverified.**
Phase3 §§0.55/5.1 grants required CompanionOptionMacros immediately after engineOptions,
retained in MacroConfiguration and same-build materialization/fingerprints. This document
adopts it under current schema23. Non-Off missing pair is INVALID_REQUEST, Off short-circuits;
earlier milestones supply explicit false/false. No unchanged-load/absent-input fallback.

**R2 — Phase5 full domain:** fulfilled architecturally by the coordinated FixedSamplerName/
candidate/binding contract. Old narrow fallback/D-P13-11 are historical, not a legal-input path.
Fresh reviews remain required; no implementation closure is claimed.

**R3 — Phase1 packages: owner-designed / receiver-adopted, unverified.** §§0.24/2.1/5.1
grant engine.textures/mod.glue.textures/mod.mixin.textures exactly; §2.1 adopts them.

**R4 — Phase3 post-analysis memory optimization:** add
`CompanionMapRequirement(boolean normals,boolean specular)` to ResourceRequirements from
declared sampler analysis. It may avoid unused atlas allocation only, never modify preliminary
macro policy; genuinely ungranted fallback allocates each preliminary-enabled kind only.

**R5 — Phase3 retained owned binary acquisition: granted by D-P3-69, receiver-adopted
under D-P13-30, unverified.** Exact signatures, outcomes, provenance, bounds, failure
precedence and lifetime are §4.3.2 and P3 §5.1. P7 transports/retains the same configuration
through preparation and NONE; no unowned source-population step or deferred pack reader remains.

**R7-10 — Phase6 resolver: owner-designed / receiver-adopted, unverified.**
UniformRuntimeFactory.create receives Phase5 FixedSamplerResolver immediately after configuration.
Phase5 provides it before registry/estate via `FixedSamplerPolicies.resolver()` alongside
appB3(), backed by the same schema/table and fingerprint:
`resolve(ProgramSamplerLayout layout,StageId stage,StageBand band) ->
FixedSamplerPlanResult.Ready(List<ResolvedSamplerBinding> bindings,FixedSamplerPolicyFingerprint policy)
| Invalid(SamplerLayoutValidation reason)`. Phase6 uses binding.samplerLayout/context inside its
unchanged sampler participant, locates exact names and uploads integers in existing cache/order/
error protocol after object binding. No second map, free allocation, fourth participant or raw
handle loop. Adoption is complete at the document boundary; fresh review/implementation remain gates.

**R7-11 — Phase6 retirement: owner-designed / receiver-adopted, unverified.**
`retire(UNPUBLISHED_ABORT|REPLACEMENT|SHUTDOWN)` returns Retired, AlreadyRetired or
Rejected(WRONG_THREAD|ACTIVE_CALLBACK). It is non-GL, terminally disables events/adoption/
participants and releases references after final use. Rejected means retirement did not occur:
quiesce/leave the callback and retry at the lawful render-thread boundary before releasing
borrowed services. Abort precedes publication, replacement follows old barrier invalidation,
shutdown precedes P4 teardown. No reset(CLOSE) alias or invented runtime close survives.

**R7-12 — Phase8 shared binding: owner-designed / receiver-adopted, unverified.**
The current ShadowInvocationContext carries selection, activationContext, TexturePublication
and TextureLeaseSource. Phase7 selects root shadow once; Phase8 consumes that exact selector/
context, beginPass(selection), expected-id full overlay lease, five-argument shadowBindings above,
sixteen-row preflight, Phase5 object binds before activation, every closed result, and exactly-once
binding closure in finally. Preserve existing traversal/camera/copied-depth/mipmap/neutralization
and bridge lifetime. It supersedes the old four-row non-closeable request, not a compatible
runtime alternative. Fresh owner/receiver review and implementation still gate real shadow;
NotInstalled remains explicit before that milestone, never prior-frame binding reuse.

**R7-13 — Phase8 planning: owner-designed / receiver-adopted, unverified.**
Current ShadowPlanInput(policy,hookHealth,requested) is registry-independent; P8 D-P8-26
derives requested from accepted P3 shadow minima before provider construction. ShadowPassFactory.create
takes the final RegistryFingerprint immediately after plan. §5.3 consumes this acyclic order.
No old-registry fingerprint workaround or still-ungranted claim remains.

**U1 — authority correction adopted, not an outstanding required grant.**
`docs/decisions/U1_TEXTURE_SAMPLING.md` and `[D-P13-26]` retain Phase3's existing
typed source specs, optional numeric discriminator, lossless stream and UNRESOLVED_KEY
behavior under exact current schema23 (D-P13-37). Phase13 interprets retained `.mcmeta` blur/clamp only.
No new typed sampling-state publication or property-key parser is required or authorized.
The separate D-P13-27 receipt adopts defaults/recovery; neither decision closes IR-01 or reviews.

### 5.4 Verification state and change triggers

Current P1/P3/P4/P5/P6/P7/P8/P13 owner and receiver amendments are **unverified**; historical
PASS records do not certify changed §5. R1/R3/R7-10..13 are adopted architecture, not executed
features. Real implementation, fresh whole-document reviews and conformance remain gates.
Future signature/type/order/fingerprint/grammar/lease/hook/epoch changes require reciprocal
owner/consumer amendments; directory names/version rolls are not verification triggers.

### 5.5 Downstream hand-offs

D-P13-42 is incorporated in the actual P1/P14 hand-off: accepted full atlas mipmapLevels>0
uses NEAREST_MIPMAP_LINEAR/NEAREST/REPEAT, mipmapLevels=0 uses NEAREST/NEAREST/REPEAT;
standalone kind default is level-zero NEAREST/NEAREST/REPEAT. ReadyAsset/candidate/fingerprint
and complete setParameters use the identical prepared triple. No downstream policy choice,
base-atlas parameter inheritance or new grammar is permitted.

**Phase14 parameters/retirement (IR-23)** `[D-P13-23]`: consume exact
TextureParameterSpec min/mag/wrap and its fingerprint, not a guessed general sampler state.
Map min/mag directly; apply the single wrap to target-applicable axes (S for 1D, ST for
2D/RECT, STR for 3D) under P1 §§4.7.7/5/D-P1-52's now-granted exact mapping.
RECT cannot execute REPEAT or mipmap min filters; integer LINEAR is likewise rejected,
never normalized. **D-P13-29 receiver adoption (2026-09-08):** owned uploads map all
six min modes, both mag modes and applicable wraps by identical enum names; unused axes
CLAMP_TO_EDGE. Remaining fields are compare NONE/LEQUAL, zero border, LOD -1000/+1000,
bias0, anisotropy1, base0, max equal to admitted contiguous last mip (0 when nonmipped),
and IDENTITY color swizzle. No storage immutability inference or foreign-live setter.
The complete owner prevalidation/error/drain/binding-restoration/recording contract is incorporated.
Sampler-object execution remains a separate optional grant; unknown borrowed state uses sampler0.
P5 alone performs physical texture/sampler selection under its adopted extension, if any.
P13 remains the texture owner: close stops leases immediately (READY→RETIRING, isCurrent false),
owned deletion waits for all live/retiring leases, and borrowed references are released
without deletion. Content equality never revives a retired lease. P1's mandatory synchronous
mapping is now adopted/unverified; sampler0 never substitutes for legal, complete object state.
**D-P13-33 runtime demotion receipt.** P14 D-P14-26/P1 D-P1-62 require replay of each
affected owned texture's complete authenticated latest owner parameters through baseline
mapping before NONE drawing. Source/parameter/publication identities and live/retiring
accounting remain valid; borrowed textures are never reparameterized. Failed or unproven
replay takes existing off/rebuild containment, not a successful resize or retirement receipt.
Optional async/PBO staging and sampler-object acceleration require separate adopted contracts.
**D-P13-38 ordinary-boundary baseline receipt.** P14 D-P14-31/P1 D-P1-64 now require
every successful owned setParameters to maintain the entire latest authenticated object
baseline alongside sampler cache state, not object-only base/max/swizzle. P13's full
source-specific conversion above is unchanged. Ordinary FINAL/useFixedFunction all-unit
sampler clearing can therefore expose only current owner-equivalent object state; no
boundary rebind/reparameterization or demotion prerequisite is added. Partial set/restore
failure invalidates the receipt and blocks sampling through existing containment; D-P13-33
demotion replay/check remains defensive. Borrowed restrictions and retiring lease accounting
remain exact. Planned A→B setter/cache then ordinary sampler-zero sampling must see B;
failed B must not advertise A or B as drawable. No execution or fresh review is claimed.
**D-P13-34 optional async receipt.** Any future adopted upload split must include P14's
producer fence→producer-context flush→publication order and retention after failed handoff.
Watchdog failure is logical failure, not disposal: keep uncertain worker/context/fence/buffer/
object ownership until acknowledged quiescence and detachment permit cleanup. No in-session
replay, context destruction or successful-shutdown claim follows from timeout alone.
The async request remains ungranted; current synchronous upload is unchanged.
Receive P14 D-P14-32's additional future owned-upload ordering: main allocation →
readiness fence → main-context flush → worker-observed signaled readiness before worker
bind/upload, then completion fence → worker-context flush → publication. Readiness and
completion syncs both remain owned/accounted until acknowledged GPU-safe cleanup, or
quarantine on uncertainty. This is a condition on a future adopted async contract only;
the mandatory synchronous grant D-P13-36 does not acquire any worker/staging permission.

**2026-09-07 receiver receipt required (D-P13-27):** P5 candidate comparison/binding and
P7 coherent publication/reuse must preserve `phase13.parameters/v2` and the outcome digest;
P14 consumes the same validated effective triple and legality disposition, including omitted
versus false and raw/noise baselines. No downstream defaults, v1 conversion or foreign mutation.
This changes semantics/identity, not the exact published parameter/candidate/lease types.

Phase2 receives handle-free plans/diagnostics and future checks, not source bytes. G8 only
advertises labPBR channel convention; required AppF5 typed binding remains v0.5. P7/P6 adopt
§4.4's authenticated current-bind→atlasSize→updateAtlasSize contract, including Unknown/reset
and immediate-active upload, without transferring P5 binding ownership.

---
## 6. Failure modes & degradation

Apply the governing ladder (`docs/design/v3/DESIGN.md:419-449`) without treating missing required
bindings as drawable success or an active rebuild failure as permission to revive old resources.

| Failure / boundary | Observable disposition |
|---|---|
| Per-sprite companion missing/wrong dimensions in prepared catalog | Use DefaultFill for that sprite with one diagnostic; no uninitialized hole |
| Custom source missing/undecodable/invalid length/limit at preparation | Retain FailedAsset diagnostic; Phase5 may use only a compatible base fallback with NO_CANDIDATE/INCOMPATIBLE_CANDIDATE evidence; missing required backing suppresses that program |
| Malformed/unreadable sidecar | Atomic source-specific baseline reset, one warning and canonical outcome digest (§4.3.5); usable bytes retained, but independent target/format legality still required |
| Explicit/default sampling incompatible with target/format | Entry-local FailedAsset before allocation; retain requested state as diagnostic evidence, no coercion. P5 only compatible fallback, otherwise required-program suppression |
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

`TextureFailure` is the exact immutable §2.3 record. Its closed code domain maps as follows:

| Code | Complete cause class / boundary |
|---|---|
| INVALID_REQUEST | Null/malformed factory/plan/build inputs, malformed scalar/collection/parameter values; reject before work |
| WRONG_THREAD | Render-thread-only factory/build entry from another thread; no GL/ownership mutation |
| OWNER_UNAVAILABLE | Plan/build on retired/closed owner; inactive fresh owner remains plannable |
| IDENTITY_MISMATCH | Configuration/schema/nested-ID, registry/policy/macro, epoch, catalog/prepared source/content/metadata or generation pairing mismatch, including missing/extra/duplicate prepared sources |
| SOURCE_UNAVAILABLE | Missing/unreadable custom bytes or unavailable resource-owner acquisition; entry-local FailedAsset, not sidecar I/O recovery |
| SOURCE_DECODE_FAILED | Bounded readable image/raw source cannot decode under its source format; entry-local FailedAsset |
| SOURCE_SIZE_INVALID | Nonpositive/overflowing dimensions, wrong exact raw byte length, capability extent limit or invalid mip/frame extent; entry-local FailedAsset |
| TARGET_FORMAT_UNSUPPORTED | Unsupported target/format/transfer/sample/comparison capability after valid grammar; entry-local FailedAsset |
| PARAMETERIZATION_UNSUPPORTED | Structurally valid requested/default effective parameters incompatible with target/storage, including RECT repeat/mipmap and integer LINEAR; entry-local FailedAsset before allocation |
| BACKEND_FAILURE | Facade allocation/upload/parameterization/mip/deletion/restoration failure after preflight; no partial Ready, release owned partial resources and use existing compensated-off containment |
| UNEXPECTED_INTERNAL | Unexpected implementation failure not attributable to any class above; public boundary closes partial ownership, fails without throwing into vanilla |

Preflight first rejects structural request defects, then wrong thread where applicable,
owner unavailability, identity pairing, and source validation in canonical source order:
unavailable, decode, size, target/format, parameterization. First reached class wins; no GL
before all required prepared-input preflight completes. Pure plan has no render-thread check.
FailedAsset stays entry-local and contributes absence/incompatibility evidence, never a
partial upload; allocation failure remains whole-build Failed. Companion DefaultFill,
noise generated fallback and animation frame0 recovery retain their existing §6 scope,
not a new exception-to-success path. Lease/binding/resize rejections retain their existing
owner enums; they are not recoded into this domain.

Every field is non-null; logicalTextureIdentity is lowercase SHA-256 of the framed existing
logical source identity, or the fixed token `TEXTURE_SYSTEM` for a non-source-specific failure.
`messageKey` is exactly `schmaloogium.error.texture.` plus the ASCII-lowercase enum name;
`diagnosticId` is lowercase SHA-256 of domain `phase13.failure/v1`, code name and that logical
identity under §4.5.1 framing. Reject invalid record payloads, never sanitize arbitrary caller
text into a valid failure. No source text, host path, GL number or exception message crosses
this record. Report one error for the selected terminal failure, or one warning for its
entry-local FailedAsset disposition, through existing diagnostics; repeated planning/candidate
expansion reuses the diagnostic identity rather than emitting another warning.

Sidecar recovery is **not** a TextureFailure/FailedAsset. Emit one WARNING with message key
`schmaloogium.warning.texture.sidecar_malformed` or `schmaloogium.warning.texture.sidecar_unreadable`,
empty args/detail and existing log destination; stable diagnostic id is SHA-256 of framed
`phase13.sidecarDiagnostic/v1` and sidecarDigest. No success diagnostic for absent/valid metadata.
If the recovered baseline is independently illegal, also report PARAMETERIZATION_UNSUPPORTED
as its separate source-legality failure; the one-sidecar-warning rule cannot hide that failure.
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

D-P13-42/43 planned cases: zero-mip and mipped full atlases produce exact baseline/effective
fingerprints and complete setters; missing-sprite fill inherits full-atlas policy while standalone
fill stays level-zero NEAREST. Two equal-shape atlases with different content select corresponding
normal/specular objects under A→B transitions; non-atlas/unavailable selects exact defaults.
Compatible custom remains winner, incompatible custom falls through, custom base changes association.
Exercise ordinary, nested pop and retained-root shadow refresh; no new activation, stale serial
rejection before binds, backend partial failure containment and exactly-one closure on every exit.

| Named check / owner | Input → observable outcome |
|---|---|
| sharedUnit_programSpecificTargets — P13 producer, P5 bind, P7 orchestration | Raw1D `texture.composite.gaux1` and raw3D `.2` → both retained in one cell; sampler1D gets first object7, sampler3D second object7, exact name stays gaux1; no patch/unit reassignment |
| sharedUnit_incompatibleAliasesBeforeBind — P4/P5 | Provider colortex4 sampler1D plus gaux1 sampler3D → typed SAMPLER_UNIT_TYPE_CONFLICT before GL creation and only provider fallback; corrupt metadata at bind → zero binds/uploads/draw |
| sharedUnit_effectiveFallbackLayout — P4/P7 | Failed child, ancestor with different complete layout/state → selector, candidates, activation and P6 callback all use ancestor; child overlays nothing |
| custom_stageExpansionExact — P13 | GBUFFERS/DEFERRED/COMPOSITE original keys → exactly gbuffers+shadow/deferred/composite+final copies preserving key/discriminator/ordinal; tex is legal only shadow, typed stage diagnostic in gbuffers |
| custom_fullscreenFixedOverrides — P5/P7 | Compatible composite/final colortex1 and deferred gaux1 → custom objects bind1/7 instead of base; every documented name/alias reachable |
| sharedUnit_fixedRangeAndShadowAlias — P5/P6 adopted adapter | Fixed table plus adding direct compatible watershadow → all rows0–15, depthtex1 remains11, gbuffers12 unused, shadow alias4→5 in same policy/resolver with no allocation |
| sharedUnit_exactNameAndOrdinal — P13/P5 | Compatible same-name absent/0/9 discriminators in shuffled storage → greatest canonical ordinal9 wins after filtering; different alias winning objects → CONFLICTING_CANDIDATES |
| sharedUnit_fullSamplerShape — P4/P5 | Equal target dimension but different signedness/shadow/array/multisample flags → cannot match; aggregate samplers retained and unsupported, not cast/dropped |
| binding_staleBeforeMutation — P4/P5/P13/P7 | Independently stale registry generation/fingerprint, estate/depth/frame/context/provider/band/layout/policy/publication/resource epoch or closed lease → reject before bind; publication id beats registry within overlay checks |
| binding_absenceVersusIncompatibility — P5/P7 | Empty custom cell, incompatible custom, missing publication, unsupported name, incompatible declarations → distinct diagnostics; compatible base draws warned; missing required backing suppresses only program and discards without flips |
| binding_leaseOwnershipAllExits — P13/P7 | Bound vs rejection/degradation/backend failure, then draw/activation error/nested suspend-pop/exception/reload/neutralization/teardown → exactly one owner closes; old lease delays deletion not stale rejection; borrowed objects never deleted |
| publication_contentIdentity — P13 | Same key/path/dimensions with changed bytes or effective upload/filter → different fingerprint; no reuse from equal dimensions/path |
| publication_foreignReloadIdentity — P13 | Same minecraft dynamic/atlas string resolves replacement after reload → reload/object epoch changes identity with no GL-name hash; in-place animation does not republish each tick |
| animation_postVanillaSnapshot — P13 | Reordered frame map with unequal durations, vanilla tick first → companions use exact current/next frames, weight and mips; reload rejects old snapshot, failed hook restores frame0 |
| macro_independentPreferencesBeforeJcpp — P13/P7/P3 | All four preference pairs with active/capable, plus off/incapable → independent exact required pre-load pair, matching same-build branches/fingerprints, no disabled-kind companion allocation/candidate or stale binding |
| noise_signedRecurrence — P13 | Signed wrapping/arithmetic-shift recurrence → xorshift(-1)=253983; channel(1,1,1) remainder=-115/upload141; unsigned shift or absolute value fails |
| unsupported_noEnumSentinel — P13 | Unknown name → UnknownSampler(exactName)+KEY_DOMAIN; known out-of-stage → KnownSampler+STAGE_COLUMN, no fabricated enum/unit |
| pipeline_textureFailureCompensates — P7 | Texture build/identity/registration failure after P4/P5 acceptance → P5/P4 off, no Active tuple/draw, caller candidates closed, accepted resources owner-retired; preserve ConsumerFailed count/no borrowed deletion |
| shadow_sharedBindingAndGate — P5/P7/P8 adopted consumer | One selection and sixteen-row preflight, physical binds before uploads, transferred snapshot closed; unimplemented/unverified real slot remains NotInstalled, never old four-row success |
| declarations_losslessWithoutSuffixParsing — P3/P13 | Duplicate/invalid/unresolved occurrences remain ordered and fingerprinted; absent/`.0`/`.9` typed keys stay distinct with unchanged sampling parameters for equal sources/sidecars; extra unknown key segments remain UNRESOLVED_KEY and produce no candidate or sampling effect |
| resource_nonePreservesConfiguration — P12/P7/P13/P9 | NONE+resourceReacquire advances resource epoch after reader drain, retains exact configuration/pair, refreshes resources/IDs and resets atlas validity, no P3 call |
| assets_afterPackRemoval — P3/P7/P13 | Load real archive then replace/delete it before preparation: PNG/raw/noise/sidecar preparation still uses exact retained bytes; full reload may obtain new bytes, NONE does not |
| assets_failureAndPairing — P3/P13 | Missing primary vs retained optional-sidecar Unreadable vs invalid/cross-load capability: source failure, atomic one-warning baseline, or identity rejection respectively; source/input fatal failures cannot become sidecar recovery |
| assets_cursorIsolation — P3/P13 | Independent cursor positions/limits and forbidden writes preserve bytes/digests across duplicate consumers; same-path distinct policy roles share input evidence, not effective baselines |
| schema23_exactNestedGate — P13 | Matching CURRENT_SCHEMA_VERSION23 accepted; containing/nested ID/inspection22 or any other version/mismatch rejects before preparation/retention/reuse; no reconstructed assets or relabeled fingerprints; nine metadata-only trees/projectionVersion1 unchanged |
| rectangle_documentedTokenToOwnedBinding — P3/P13/P5 | Exact TEXTURE_RECTANGLE floating Raw with two dimensions, legal parameters and real retained bytes → Rectangle upload and compatible sampler2DRect binding after archive removal; bare RECTANGLE remains upstream INVALID_VALUE with no candidate, repeat/mipmap/integer-LINEAR remains pre-allocation rejection without coercion |
| configuration_overrideOnly — P3/P13/P7 | Usable explicit override with empty base loads; selected override uses only its own typed inputs, absent world selects empty base, no base-usability rejection or source merge |

### 8.2 Companion, upload and hook checks

| Named check / P13 unless stated | Input → observable outcome |
|---|---|
| atlas_actualExtentEpochAcceptance — P13/P7 | Identical sprite content with different padded allocation extents yields actual captured Known at matching Post only; other map/epoch, missing/duplicate capture, bad mip, reentry, throw or failed reload stays Unknown/unpublished |
| atlas_scopeExceptionalExit — P13/P7 | A later Pre listener throws after token mint, or populator throws before inner load; inner body throws, or a later Post listener throws after acceptance → outer finally invalidates capture/catalog/Known and releases all private scope/token state, no companion publication; vanilla exception/lifecycle ownership unchanged |
| atlas_normalOuterAndUnscopedInner — P13/P7/P2 | Normal outer Pre→populator→one allocation→Post→return retains exact accepted extent with no retained scope; direct inner load without outer scope/Pre, unscoped Post, duplicate Pre/Post and nested conflicting load never mint replacement authority or publish; poison lasts until outer cleanup |
| atlas_applicationDomain — P7/P2 | Same eight IDs/counts with old application-v1 or inner-wrapper target evidence rejects as stale; application-v2 exact outer wrapper application is audited independently of runtime successes/failures, which never mutate frozen counts |
| upload_targetAndTransferBoundary — P1/P5/P13 | Same-sized 2D and RECT dispatch distinctly; all four raw target arities and all37 formats retain declared transfer words; 3D mip/region or packed/scalar byte mismatch rejects before GL |
| upload_targetSpecificLimits — P1/P2/P13 | Recorded 2D/3D/RECT maxima deliberately differ: each supported target accepts its exact boundary and rejects boundary+1 as SOURCE_SIZE_INVALID before GL; unsupported-zero 3D/RECT yields TARGET_FORMAT_UNSUPPORTED, not guessed 2D admission; native/replayed profiles make identical pure decisions, missing/invalid keys fail profile admission |
| upload_ownedFailureContainment — P1/P13 | Borrowed destination rejects before mutation; allocation/upload/set/restore failure prevents candidate publication and releases only owned resources; initial/animation data stays stable through synchronous return |
| parameters_ordinaryFixedFunctionBaseline — P1/P5/P13/P14 | Successful complete setter A→B under sampler caching then ordinary all-unit sampler clearing samples owner-equivalent B; partial B/restore failure blocks drawing instead of exposing stale A/defaults |
| companion_discoveryPerSprite | Mixed present/missing `_n`/`_s` resources → one resource/default source per enabled sprite/kind with correct identity |
| companion_layoutAndMipChainMatchBase | Non-square atlas with varied origins/mips → matching companion extent/origins and all mip levels, no independent repack |
| companion_missingNormalUsesContractDefault | Missing normal → literal §4.1.4 byte pattern; C-TX01 color interpretation remains explicit, not silently corrected |
| companion_missingSpecularUsesZeroDefault | Missing specular → zero fill throughout sprite/mips |
| custom_mcmetaBlurSetsFilter | Per-source absent/omitted/true/false → PNG NEAREST baseline, raw/noise LINEAR baseline, true LINEAR, false NEAREST; omission differs from explicit false |
| custom_mcmetaClampSetsWrap | Per-source absent/omitted/true/false → PNG/noise REPEAT baseline, raw CLAMP_TO_EDGE baseline; explicit booleans override independently |
| sidecar_atomicRecovery | Valid blur then invalid clamp, invalid JSON/type/duplicates/encoding/limits, or unreadable sidecar → both fields reset to source baseline, one warning per outcome, deterministic digest; no partial override |
| sidecar_identityAndOwnership | Equal role/bytes/outcome → equal v2 digest; omitted vs false, changed bytes/role/outcome → distinct inputs; generated noise unchanged, resource/foreign sources never shader-sidecar-mutated |
| sidecar_targetFormatLegality | Raw RECT clamp=false or integer raw omitted blur → entry-local pre-allocation rejection, no coercion; legal clamp=true/blur=false respectively retains exact state; missing required compatible backing suppresses program |
| texture_failureDisposition | Malformed sidecar plus illegal raw-integer baseline → one sidecar warning and distinct PARAMETERIZATION_UNSUPPORTED FailedAsset, no allocation; invalid request/identity mismatch precedes source work; backend failure yields no partial Ready and retains existing compensation |
| noise_resolutionFromRequirementsAndBaseline | No noise requirement vs enabled N → no noise allocation versus NxN RGB; absent resolution baseline256 |
| noise_packOverrideReplacesGenerated | Valid override image vs decode failure → exact image dimensions/parameters versus diagnosed generated fallback |
| atlasSize_valueAndValidityWindow | Stitch makes metadata Known but performs no uniform update; authenticated actual atlas bind uploads Known, non-atlas/restoration/reset uploads0,0; old epoch/issuer/serial rejected, active update immediate and inactive next-bind cached |
| companion_vanillaAtlasUntouchedWithShadersOff | Off/reload/close with borrowed atlas → no foreign allocation/upload/delete, restored vanilla binding/state |
| hook_atlasCatalogCapturedAtStitchPost | Pre then Post → invalidated old catalog then copied current metadata/pixels only |
| hook_spriteCompanionAndAnimationRows — P7 audit | Application report → active read-only accessors/atlas TAIL counted, per-sprite update rows explicitly dormant |

These cases check D-P13-27's maintainer-selected local policy, not measured G6 parity.
P13/P5/P7/P14 owner/receiver review and actual classic-matrix conformance remain required.

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
| Signed noise, override and sidecars — P13 | v0.5 | noise_signedRecurrence, noise_packOverrideReplacesGenerated, custom_mcmetaBlurSetsFilter, custom_mcmetaClampSetsWrap, sidecar_atomicRecovery, sidecar_identityAndOwnership, sidecar_targetFormatLegality, texture_failureDisposition |
| Independent preliminary macros and adopted P3 R1 input | v0.5 | macro_independentPreferencesBeforeJcpp; fresh owner/receiver review and implementation remain |
| Lease/publication/reload/resize coherence — P13/P7 | v0.5 | publication_foreignReloadIdentity, binding_leaseOwnershipAllExits, pipeline_textureFailureCompensates |
| atlasSize | v0.5 | atlasSize_valueAndValidityWindow |
| H13-SPRITE-02 per-sprite injection | v0.5, dormant | explicitly dormant in application audit, not missing |
| Async/PBO transfer and sampler-object optimization — P14 | post-v0.5 | P14-owned checks preserving identity/ownership |

Full AppF5 binding is no longer conditional on old R2. R1/R3/R7-10..13 are owner-designed
and receiver-adopted, unverified. U1's documented-mechanism correction is adopted (§0.8);
no missing suffix grant gates full v0.5 scope. IR-01's disposition is recorded (2026-09-09); fresh
whole-document reviews closed 2026-09-08; optional R4/P14 extensions, actual implementation and §11.3's parameter-legality closure remain distinct.
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
| D-P13-6 | **Historical U1 gate, superseded by D-P13-26:** consume current P3 lossless declaration dispositions and typed source projections without parsing suffixes; independent sidecars honored; the former authority/typed-sampling prerequisite is not active |
| D-P13-7 | Forge stitch events for timing, read-only atlas/sprite accessors for private data; policy stays in engine/glue |
| D-P13-8 | Atlas TAIL post-vanilla full animation snapshot, no independent clock; design requirement `docs/design/v3/DESIGN.md:2482-2483`, not observed sync; frame0 fallback |
| D-P13-9 | Real bound atlasSize Known/Unknown, not PD no-op; `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:177-177`, PD §11 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:638-639` |
| D-P13-10 | Independent preliminary user preferences before load/jcpp; adapt adopted P3 CompanionOptionMacros and retain same-build provenance, never linked-demand-derived (§4.1.6) |
| D-P13-11 | **Historical/superseded:** old narrow P5 domain fallback. Coordinated R2 now fulfills full AppF5 domain architecturally |
| D-P13-12 | Post-analysis demand may save unused atlas allocation without changing macro policy; accepted cost `docs/design/v3/DESIGN.md:2499-2500`, PD B13 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:799-799` |
| D-P13-13 | Verification comes from latest verdict and outstanding interface changes, never stale footers; P3 pending, coordinated P4/5/7/13 fresh PASS required |
| D-P13-14 | Preserve box-filter/no-normal-renormalization assumption in §4.1.3, falsifiable at T2, not an unasked improvement |
| D-P13-15 | Accept required v0.5 shared-unit capability via exact names/effective layouts/typed candidates; reject only generated customtexN names/source patching. Checks `docs/design/v3/DESIGN.md:2471-2476`, `docs/research/v1/RESEARCH.md:1488-1490`, PD §11 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-629` |
| D-P13-16 | P5 sole fixed-name resolver, no dynamic/free units, preserved effective watershadow alias. Check `docs/research/v1/RESEARCH.md:1228-1255`, PD §18 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808-808` |
| D-P13-17 | Closed bind outcomes and Bound-only ownership transfer, never boolean/no-op success; AppF5 `docs/research/v1/RESEARCH.md:1488-1490`, PD B10 `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:796-796` |
| D-P13-18 | Actual bytes/parameters/config/source/reload/object identity in canonical SHA-256 domains; content equality never generation/ownership authority |
| D-P13-19 | Compatible custom greatest canonical Phase3 ordinal wins across distinct discriminators; complete-key last-valid-wins already happened upstream; conflicting aliases never traversal-winner |
| D-P13-20 | Preliminary macro state = active AND capable AND each independent decoded user preference; disabled kind allocates no companion, optional demand only reduces enabled allocation (§4.1.1) |
| D-P13-21 | Lossless declaration occurrence order is distinct from executable candidate canonical order; unresolved input affects identity but is never downstream executable syntax (§4.3.1) |
| D-P13-22 | Atlas size metadata is not current-bind proof; authenticated P7 actual-bind evidence selects P13 query and existing P6 sink, P5 retains physical binding (§4.4) |
| D-P13-23 | Exact min/mag/wrap parameter projection and all-live-lease retirement are P14 constraints, not permission for sampler/async APIs; synchronous sampler0 baseline until grants (§5.5) |
| D-P13-24 | Reconcile granted owner ledgers as adopted/unverified; its historical missing-U1 disposition is superseded by D-P13-26. Optional R4/P14 proposals remain separate (§§5.3/11.5) |
| D-P13-25 | Adopt P3 schema18 configuration/fingerprint cutover, including locale-indexed data, session-only Internal options and old-light projection; independent preliminary normal/specular preference computation remains unchanged (IR-03/24) |
| D-P13-26 | 2026-09-07: adopt maintainer U1 **Correct requirement to documented mechanisms**, `docs/decisions/U1_TEXTURE_SAMPLING.md`, checked against RESEARCH App F.5 and primary sources in §0.8. P3 owns existing keys/discriminators/lossless diagnostics; P13 owns retained sidecar interpretation. No extra filter/wrap syntax, typed suffix API, schema bump or future required U1 grant; no new defaults/error-policy ratification; IR-01/fresh reviews remain |
| D-P13-27 | 2026-09-07: adopt `TEXTURE_SIDECAR_DEFAULTS.md` source-specific omission/Boolean policy, atomic baseline recovery, noise-specific baseline and unchanged generated/foreign ownership. Specify strict bounded JSON and canonical outcome identity locally; increment parameter domain v1→v2, not parameter type or P3 schema. Recovery/noise policy is maintainer authority, not author or runtime evidence; target/format rejection remains mandatory |
| D-P13-28 | 2026-09-07: adopt P3 D-P3-68/§0.61 schema19 including nested IDs; reject every other schema before derivation, no schema18 synthesis. Texture/noise/companion/locale contracts remain unchanged by native source grant; fresh reviews remain |
| D-P13-29 | Adopt P1 D-P1-52's complete synchronous owned-texture mapping, neutral remaining sampler fields and exact object mip/swizzle policy; no foreign mutation or optional sampler execution grant |
| D-P13-30 | Adopt P3 D-P3-69/schema20 snapshot-bound PNG/raw/noise/sidecar acquisition and complete failure/lifetime/identity contract; close TS-1 without selected-pack reopening. Preserve approved reachable unreadable-sidecar recovery, P3 fatal safety/primary duties, P7 NONE retention and metadata-only inspection |
| D-P13-31 | 2026-09-08: adopt P3 D-P3-70 schema21/nested inspection identity, MaterializedSource-v21 and exact TEXTURE_RECTANGLE→typed Raw/RECTANGLE→existing assets/upload/binding route; D-P3-69 assets/nine trees and parameter domains unchanged. Reject old schemas, retain legality/fallback/ownership and opaque current P4 registry identity; no optional grant or fresh PASS |
| D-P13-32 | Receive P3 D-P3-72 schema22/current materialization and exact containing/nested/inspection admission; no mapping-era logic or asset/sidecar/parameter change; prior numeric receipts historical |
| D-P13-33 | Receive P14/P1 authenticated complete owned-parameter replay before runtime sampler demotion; no borrowed mutation or false successful fallback/retirement |
| D-P13-34 | Receive producer-flush and acknowledged-disposal/quarantine requirements for future async proposal; no optional API grant or timeout-as-cleanup |
| D-P13-35 | R5 C1: bounded actual TextureUtil allocation-return extent paired to scoped map/resource epoch, Post-only acceptance and Pre/reload/failure invalidation; exact H13-ATLAS-05/06 and P7/P2 health receipts, no getter/query invention |
| D-P13-36 | R5 C2: receive P1 D-P1-63/P5 D-P5-42 mandatory target-bearing synchronous allocation/upload grant; exact every-source conversion, granted ColorInternalFormat, prepared region/transfer payloads and unchanged owned/borrowed/failure laws |
| D-P13-37 | Receive current schema23/MaterializedSource-v23 containing/nested/inspection equality; earlier numeric receipts historical; nine metadata-only trees/projectionVersion1 and unrelated domains unchanged |
| D-P13-38 | Receive P14 D-P14-31/P1 D-P1-64 complete latest object baseline on each successful setter, alongside sampler cache; ordinary all-unit clearing never exposes stale owned state |
| D-P13-39 | Receive P7 D-P7-65 exact eight-entry application-health framing and separate runtime acceptance; events/members/injections retain distinct count meanings, no fabricated dimensions or health |
| D-P13-40 | R6 C1: Pre-owning full-descriptor outer loadSprites try/finally encloses Pre/populator/inner allocation/Post and all exceptions; bounded token/native-object/epoch and Post-only acceptance preserved, direct/unscoped inner loads fail closed; eight IDs retained, target-bound application-v2 and reciprocal P7/P2 evidence required |
| D-P13-41 | Receive P1 target-specific immutable max3DTextureSize/maxRectangleTextureSize limits alongside maxTextureSize in pure preparation; native gated capture, exact replay and unsupported-zero semantics, no guessed limits or engine GL query |
| D-P13-42 | R7 C1: explicit local companion/default min/mag/wrap policy derived from accepted mip count; same ReadyAsset, fingerprint and complete setter, no grammar or confidence expansion |
| D-P13-43 | R7 C2: actual accepted base-object/atlas association through opaque P7 evidence and context-bound lease; P5 exact matching/default branch and sole before-draw refresh, reciprocal P7/P8 lifetime receiver |
| D-P13-44 | R8 C1: §4.3.2 acquisition gate enforces exact CURRENT_SCHEMA_VERSION=23 equality under D-P13-37 — matching nested IdMappingInput, required same-load assets, exact configuration pairing and `assets.pack()` — and rejects 22/all other schemas without repair; no other gate text moves |
| D-P13-45 | R8 C2, coordinates re-corrected 2026-09-08 under Review9 C1: stale sibling-phase anchors re-resolved to current frozen coordinates (P4 `:2144`; P5 `:2734-2743` ownership rows and `:2737` Bound-only sentence; P7 `:1884-1885` DEFERRED(P13,v0.5) rows, `:1690-1698` §4.10.1 catalog/health classes and audit, `:3923-4015` Phase13 downstream slot plus complete ten-step transaction; P6 `:1221-1225`); unverifiable P7 line-quote dropped, no semantic claim changed; the R8 P7 values (`:1792-1793`, `:1598-1605,2933`, `:3880-3928`) had drifted and are historical; R10 C1/C2/C3 (2026-09-08): Review9's landed intermediate values (`:1846-1847`, `:1650-1660`, `:3880-3972`) were themselves stale and are historical, and the three re-pointed coordinates above re-verified against current P7 bytes (ledger heading `:1866`, literal DEFERRED rows `:1884-1885`; §4.10.1 class table `:1690-1695` and require=0/plugin-audit `:1697-1698`; slot `:3923-3944` plus complete ten-step transaction `:3967-4015`) |
| D-P13-46 | 2026-09-08 Review9 C2 receiver: current opaque P4 registry identity is D-P4-43's `RegistryFingerprint/profile-selection-v3` (`docs/phase4/v1/PHASE_4_DOC.md:1953-1954`), carried and compared opaquely at the existing identity checks with older-cache invalidation keyed to that domain; D-P13-31's own-build-v1 mention historical/superseded, mirroring P5 D-P5-48 and P7; no profile codec, inference, schema or cross-phase signature change |

### 11.2 Binding decisions

D-2 keeps this shaders-only; labPBR channel semantics remain pack-side. D-4 is full architecture
with explicit v0.1/v0.2/v0.5 staging. D-5 retains dumb mixins/events without replacement classes.
D-6 keeps engine Minecraft-free; D-9 retains compatibility-profile targets. Pack names stay
verbatim; RESEARCH D-1..D-10 are not contradicted.

### 11.3 Input contradictions

§3.6 records the authorized correction of the historical suffix requirement, the superseded
narrow-domain shortfall, historical verification and C-TX01. PD and original authority text
remain historical evidence; §0.8 adopts the dated decision rather than rewriting them.

**Attempt-5 reciprocal receipt (D-P13-35..38).** P1/P5 allocation/value grant and local
receiver conversion are now owner-designed/receiver-adopted, unverified. Main must receive
§4.6's exact H13-ATLAS-05/06 rows and eight-row ordered catalog in P7/P2, plus Post-only
extent acceptance through the existing authenticated P7→P13 size→P6 uniform route.
P7/P8 depth consumers retain P1's unchanged copy semantics with explicit 2D region.
P2 recorder/scenario planning receives target/layout/mip/restoration evidence, not bytes.
P14 receives the mandatory synchronous values for value compatibility only and the exact
complete-object baseline receipt; async remains ungranted. No original authority, original
review verdict, historical quotation, C-TX01 or sidecar-default decision is amended.

**Separate sidecar policy resolved locally, not by U1:** the maintainer explicitly selected
`docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md` on 2026-09-07. D-P13-27/§4.3.5 adopt
PNG NEAREST/REPEAT, raw LINEAR/CLAMP_TO_EDGE, noise override LINEAR/REPEAT, omission
preservation, explicit Boolean overrides and whole-sidecar recovery. Published Iris docs
support image/raw defaults and resource exclusion, not this recovery/noise policy; no
measured G6 parity follows. Prior §0.8/D-P13-26 evidence remains historical.

**Separate mandatory legality closure:** RECTANGLE repeat/mipmap and integer LINEAR requests,
including defaults/fallback baselines, are entry-local rejected under §§4.3.3/.5/6, never
normalized into support. D-P13-27 closes TextureFailure's full code/record/payload and
classification in §§2.3/5/6, including PARAMETERIZATION_UNSUPPORTED and separate recovery warnings.
P1 §§4.7.7/5/D-P1-52 now grants the mandatory synchronous mapping; §5.5 adopts it under
D-P13-29. Fresh owner/receiver review and actual legality/state proof remain; acceleration is separate.
No missing failure enum or local recovery decision remains.
Sampler0 alone does not legalize an incompatible owned texture; it is only the optional
sampler-object fallback for an otherwise valid object. C-TX01 is unchanged.

### 11.4 Hand-offs and blockers

- **IR-03/04:** §§2.1/4.1/4.3/5 adopt P3 current schema23/lossless/same-build/assets contracts and
  P1/P3/P6/P8 owner grants; all receiver and owner verification remains open.
- **IR-06/08/24:** §§4.1.1/4.7 adopt independent canonical codec preferences before load,
  disabled allocation/candidates, and P12 resource-only NONE with configuration-preserving
  quiescent P7/P9 refresh. Macro emission/fingerprints remain P3-owned.
- **IR-21:** §4.4 binds P7 opaque actual-bind evidence to P13 current metadata query and
  existing P6 Int2 sink; stale/unknown/reset and immediate-active timing are explicit.
- **IR-23:** §5.5 publishes P14 exact parameter/retirement constraints; P5 owns physical
  binding, P13 owns texture lifetime, optional sampler/async APIs remain separate gates.

### 11.5 Exact upstream requests

§5.3 is the binding detailed disposition; this ledger routes, never grants itself:

| Request | Owner status / receiving disposition / remaining gate |
|---|---|
| U1 Phase3/DESIGN | Dated authority correction adopted in §§0.8/3.6/5.3 and D-P13-26; current schema23 keys/discriminators/lossless declarations remain, D-P13-31's typed rectangle correction is retained. No missing U1 grant. Separate D-P13-27 adopts defaults/recovery; legality and fresh reviews remain |
| R1 Phase3 | Required typed pair, same-build materialization/fingerprints, non-Off INVALID_REQUEST and Off short-circuit granted and adopted under schema23; fresh reviews/implementation remain |
| R4 Phase3 | Optional post-analysis memory optimization genuinely ungranted; baseline builds each preliminary-enabled kind only, no macro cycle |
| R5 Phase3 / TS-1 | D-P3-69 same-load immutable asset acquisition granted and adopted through D-P13-30/§4.3.2; P7 must retain exact capability through preparation/NONE. Current bytes require fresh owner/receiver reviews |
| R2 Phase5 | Full policy/candidate/binding protocol fulfilled architecturally and adopted, unverified |
| R3 Phase1 | Exact texture package trio granted and adopted, unverified; no pending-name fallback |
| R7-10 Phase6 | Sole FixedSamplerResolver injection granted and adopted; unchanged three-participant afterBind/cache/token semantics |
| R7-11 Phase6 | Terminal retire(reason) closed outcomes granted and adopted; no reset(CLOSE), rejected retirement is not success |
| R7-12/13 Phase8 | Full selector/context/lease binding and registry-independent planning granted and adopted; preserve traversal/neutralization; actual real slot requires implementation and fresh reviews |
| IR-21 Phase7/6 | Authenticated current-bind query/sink adapter adopted §§4.4/5.5, unverified |
| IR-23 Phase14/1 | P1 D-P1-52 complete synchronous mapping granted and adopted/unverified; exact P13 parameter/lifetime adoption retained. Optional sampler/async/staging execution permission remains separate |
| All affected owners | Fresh whole-document verification of current bytes before implementation consumption; no prior PASS or directory roll substitutes for it |

---

## 12. Implementation checklist

Future coding work only; §8 rows specify the observable checks and §9 fixes the milestones.

1. `[v0.1 P4/P5/P7]` Implement sole fixed policy/resolver and full linked sampler layouts with
   candidate-local prelink failure; issued select-once/authenticated activation. Checks:
   sharedUnit_incompatibleAliasesBeforeBind, sharedUnit_fullSamplerShape, sharedUnit_effectiveFallbackLayout.
2. `[v0.1 P5/P7]` Carry selector in snapshots, mutation-free preflight then ascending physical binds,
   Bound-only transfer and local discard; no manual P7 row loop. Checks:
   binding_staleBeforeMutation, binding_absenceVersusIncompatibility, binding_leaseOwnershipAllExits.
3. `[v0.2 P5/P8/P7]` Implement adopted full shadow operation/context and registry-independent
   planning before real installation; preserve traversal/neutralization. Check: shadow_sharedBindingAndGate.
4. `[v0.5 P13]` Define immutable §2 types, closed unsupported diagnostics, source catalog and paired
   prepared uploads/foreign handles, pre-allocation identity validation. Checks:
   publication_contentIdentity, publication_foreignReloadIdentity, unsupported_noEnumSentinel.
5. `[v0.5 P13]` Preserve every original key/discriminator/canonical ordinal on exact stage expansion
   and retain all target candidates; consume P3's numeric discriminators, never interpret them
   as filter/wrap. Preserve UNRESOLVED_KEY diagnostics without candidate/sampling effects.
   Checks: custom_stageExpansionExact, sharedUnit_programSpecificTargets,
   sharedUnit_exactNameAndOrdinal, custom_fullscreenFixedOverrides, declarations_losslessWithoutSuffixParsing.
6. `[v0.5 P13]` Implement all three source forms, raw checked sizes/capabilities, retained
   `.mcmeta` blur/clamp and actual full sampled shape; no extra filter/wrap property-key syntax,
   typed suffix API or parameter mutation of foreign overrides. Implement D-P13-27's strict bounded
   JSON, source-specific omission/false policy and atomic recovery; adopt P1's mandatory synchronous
   parameter mapping and §6 closed failure disposition. Consume D-P13-41's exact gated,
   recorded target maxima in pure source/atlas preflight; never infer 3D/RECT limits from 2D.
   Checks: publication_contentIdentity, upload_targetSpecificLimits,
   custom_mcmetaBlurSetsFilter, custom_mcmetaClampSetsWrap, sidecar_atomicRecovery,
   sidecar_identityAndOwnership, sidecar_targetFormatLegality.
7. `[v0.5 P13]` Implement signed wrapping noise recurrence and override fallback. Checks:
   noise_signedRecurrence, noise_resolutionFromRequirementsAndBaseline, noise_packOverrideReplacesGenerated.
8. `[v0.5 P13/P3/P7]` Compute independent preliminary preferences before load and supply adopted
   R1 typed pair/fingerprints; R4 only reduces enabled allocation. Check: macro_independentPreferencesBeforeJcpp.
9. `[v0.5 P13]` Discover/build full companions with base extents/mips/default bytes, copied immutable
   animation metadata/pixels. Checks: companion_discoveryPerSprite, companion_layoutAndMipChainMatchBase,
   companion_missingNormalUsesContractDefault, companion_missingSpecularUsesZeroDefault.
10. `[v0.5 P13/P7]` Implement H13 accessors/snapshots and D-P13-40's complete outer
    loadSprites try/finally: Pre/listener/populator/inner/Post failures release bounded scope,
    direct inner/unscoped work fails closed; restore frame0 on failed live state and invalidate
    before reload. Receive application-v2 in P7/P2 separately from runtime acceptance. Checks:
    animation_postVanillaSnapshot, hook_atlasCatalogCapturedAtStitchPost,
    hook_spriteCompanionAndAnimationRows, atlas_scopeExceptionalExit,
    atlas_normalOuterAndUnscopedInner, atlas_applicationDomain.
11. `[v0.5 P13/P7/P6]` Provide Known metadata independently from authenticated actual-bind
    uniform delivery/reset and current-epoch rejection. Check: atlasSize_valueAndValidityWindow.
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
17. `[v0.5 P13/P5/P7/P8]` Implement D-P13-42/43 complete sampling and context-bound lease cutover,
    all accepted-atlas/custom/default selection and serial refresh/closure branches in §8.1.
    No two-argument lease overload, inferred current atlas or alternate native binder remains.

---

§5 changed in this coordinated rebuild. Unverified; a fresh whole-document review returning literal PASS is required before verified downstream consumption. v1 retained; no directory roll.

**Verification status — 2026-09-08 (attempt-11 wave close-out):** the attempt-11 fresh whole-owner review (`docs/phase13/reviews/PHASE_13_REVIEW_11.md`, frozen SHA-256 `71710eb9aa5e39f4c0fa390a4913ebeaea9a9dccdeb3af43c72f264847774fe7`) returned PASS-WITH-CORRECTIONS — 0 blocking, 2 corrections, 2 notes. This fix-up wave applied every correction and recorded resolutions in the review file; no §5 bytes changed and no §5 change is outstanding. Per §G1.3 the document is **verified**.
