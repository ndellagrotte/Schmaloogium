# Schmaloogium — Phase 4: Stage/program registry & compilation — Architecture

## 0. Header

**Phase:** 4 — Stage/program registry & compilation

**Date:** 2026-07-29 · **Last revised:** 2026-09-08 (§0.42)

**Milestone:** v0.1, with the full modern-superset shape present but later families dormant

**Governing assignment:** `docs/design/v2.0-RC3/DESIGN.md:1471`, through the Phase 4
specification ending at line 1570. RC3 is the governing revision for this new phase document; it
does not migrate Phase 1 or Phase 2.

This is the maintainer-authorized architecture-only coordinated Phase 4/5/7/13 rebuild.
This owner changes only this document; no source, build, review, authority, or directory changes.

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

- `docs/phase1/v14/PHASE_1_DOC.md`: Review 20's literal PASS
  (`docs/phase1/reviews/PHASE_1_REVIEW_20.md`) verified the §0.19/§0.20-era surface
  historically; per P1's own §G1.3 status (P1 §0.35), the current P1 bytes carry later §5
  amendments and are not verified until a fresh whole-document review returns literal PASS.
  Its §5 was read completely. Its §4.7 `engine.gl` facade was read to obtain the exact service
  and recording signatures that §5 makes binding, including the now-published
  `ShaderService.useFixedFunction()` operation and its zero-argument recorder event. The
  post-Review-20 grants this phase consumes are already tracked as owner-granted and unverified
  in §5.2/§5.4; Phase 1's framebuffer/depth amendment is outside Phase 4's use.
- Historical dependency read: Phase3 Review20 verified the surface originally consumed here.
  Current `docs/phase3/v1/PHASE_3_DOC.md` has Review36 applied resolutions and changed §5
  without a fresh PASS; it is provisional under this rebuild authorization, not freshly verified.
  Its published closed declaration/materialization contracts are preserved, with outstanding
  legacy-geometry/attribute grants retained in §5.4.

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

### 0.5 Review 1 correction addendum

Review 1 closes five interface omissions: immutable stage/pass traversal, closed barrier results,
publication ownership, registry-wide failure aggregation, and the gbuffers compute exclusion.
The corrected contracts are integrated into §§2–6 and the test plan. No governing decision,
dependency contract, or version-directory path changes.

### 0.6 Review 2 correction addendum

Review 2 supplies publication's release context, separates pre-release rejection from forced-off
recovery after release begins, and repairs two conformance citations. The corrected contracts are
integrated into §§2–5 and §8.

### 0.7 Review 3 correction addendum

Review 3 exposes the accepted barrier through a generation-checked, non-owning publication view.
The corrected access, invalidation, off-state, and thread contracts are integrated into §§2, 4,
5, 7, and 8.

### 0.8 Review 4 correction addendum

Review 4 separates published registry access from teardown authority, supplies immutable Phase 6
barrier composition, and corrects legacy-geometry conformance accounting. The corrections are
integrated into §§2–5, §8, and §10.

### 0.9 Review 5 correction addendum

Review 5 makes published resolution handle-free and confines the all-no-op barrier to
Phase-4-owned bootstrap assembly. The corrections are integrated into §§2, 4, 5, 8, and 12.

### 0.10 Review 6 correction addendum

Review 6 makes both the production participant bundle and the factory-to-publication candidate
opaque Phase-4-issued products. The corrections are integrated into §§2, 4, 5, 8, and 12.

### 0.11 Review 7 correction addendum

Review 7 supplies the cross-package production-composition route and authenticates compiler
origin through an opaque registry product. The corrections are integrated into §§4, 5, 8, and 12.

### 0.12 Review 8 correction addendum

Review 8 makes barrier-candidate cleanup callable and completes §5's compiler, publisher, request,
context, and ownership inventory. The corrections are integrated into §§4, 5, 8, and 12.

### 0.13 Review 9 correction addendum

Review 9 corrects the architecture relationship map to distinguish Phase 7's opaque-candidate
publication route from downstream non-owning views and separately exposed state contracts.

### 0.14 Review 10 correction addendum

Review 10 corrects the generation branch and narrows Phase 10's input to the fixed attribute table.

### 0.15 Maintenance addendum (Phase 5 candidate-view binding — 2026-07-29)

Phase 5's verified dependency request identified that §2 already declared
`CompiledRegistryCandidate.view()`, but binding §5 exposed only the post-publication
`PublishedRegistry.registry` route. Section 5.1 now publishes the existing method explicitly as
the non-owning, pre-publication `ProgramRegistryView` consumed by Phases 5 and 7 while the
compiler-issued candidate remains caller-owned. This clarification does not add a registry or
handle accessor, transfer or weaken candidate ownership, expose the compiler-origin credential,
or assign a publication generation to an unpublished view.

### 0.16 Review 12 correction addendum

Review 12 aligns §5.3's Phase 3 inventory with the dependency's binding surface: legacy-pair
discovery, rewrite sites, translation selection/validation, and materialized fingerprints stay
with the source/materialization and geometry contracts, while `ResourceRequirements` is narrowed
to the concerns that Phase 3 publishes there.

### 0.17 Review 13 correction addendum

Review 13 adds the missing non-owning pre-publication candidate-view branch to §2.3's relationship
map while preserving the opaque candidate's Phase 7 composition/publication path.

### 0.18 Review 14 correction addendum

Review 14 defines `view()` as an immutable detached metadata snapshot whose safe lifetime is
independent of later candidate close, rejection, recovery, or ownership transfer. The lifecycle
contract and representative retention tests are integrated into §§2, 5, 8, 11, and 12.

**Current §G1.3 status:** round fourteen's correction is fixed, but because §5 changed, Phase 4 is
**not verified** and is not a valid dependency input until a fresh round fifteen returns literal
PASS. The version directory remains `v1` while the loop is open.

### 0.19 Maintenance addendum (Phase 6 uniform-layout and bound-access contracts — 2026-07-29)

Round fifteen returned literal PASS for the candidate-view surface. Phase 6's verified dependency
request and Phase 3's now-verified declaration catalog require two additional binding contracts:
Phase 4 merges final per-stage declarations into a handle-free effective
`ProgramUniformLayout`, and each successful shader activation supplies the three Phase 6
participants with callback-scoped `BoundProgramUniformAccess` plus a retainable, non-operational
activity token. Sections 2, 4, 5, 7, 8, 11, and 12 now close merge conflicts, cache identity,
lookup lifetime, invalidation, and failure behavior without exposing a `ProgramHandle`.

**Current §G1.3 status:** round fifteen's literal PASS applies to the pre-§0.19 bytes. This
addendum changes binding §5, so Phase 4 is **not verified** and is not a valid dependency input
until a fresh round sixteen returns literal PASS (or any corrections are fixed and the changed
interface is re-verified). The version directory remains `v1` while the loop is open.

### 0.20 Review 16 correction addendum

Review 16 makes `Activated` compatible with recorded isolated participant degradations and replaces
caller-invented barrier context fields with opaque, Phase-4-issued per-frame activation and release
contexts. Sections 4, 5, 8, and 12 define context issuance, freshness, consistency, replacement,
and rejection coverage.

### 0.21 Review 17 correction addendum

Review 17 defines public barrier-context interfaces as acceptance-checked views and fixes
release-kind `shadowPass()` at `false`. Sections 4, 5, 8, and 12 align the contract and tests.

**Historical status:** review round 18 subsequently returned literal **PASS** with zero findings
(`docs/phase4/reviews/PHASE_4_REVIEW_18.md`). The amendment below supersedes that verified surface.

### 0.22 Downstream-request addendum (program evidence and virtual-pre coordination — 2026-08-03)

Phase 7 R7-4 grants Phase 2 R10 through an immutable, handle-free per-slot resolution projection:
`SOURCED`, `CHAIN`, `ABSENT`, or `FAILED`, independently paired with whether the requested slot's
source exists. Candidate and published runtime views expose the identical deterministic list, so
goldens and manifests copy one owner-defined truth. Phase 7 R7-2's Phase-4 half is also closed:
the existing virtual `deferred_pre`/`composite_pre` `PassDescriptor` is the typed transition input;
it has flips but no resolved program and is passed unchanged to Phase 5. `[D-P4-16]` and
`[D-P4-17]` record the decisions.

This amendment changes binding §5. Round 18's PASS is therefore historical; Phase 4 v1 is **not
verified** pending a fresh whole-document review, and the version directory remains unchanged.

### 0.23 Review 19 correction addendum

Review 19 makes the per-slot resolution projection total when a fallback walk encounters failed
ancestors and gives every projection-eligible failure stage deterministic non-empty sanitized
detail. The correction is integrated into §§4–5, §8, and §12 and changes binding §5; a fresh
review is owed.

### 0.24 Review 20 correction addendum

Review 20 confines resolution-projection failure detail to candidate-time build stages. Runtime
barrier construction and activation failures remain in their closed diagnostics and cannot alter
the detached candidate/runtime value list. Sections 4–5, §8, and §12 align the contract and tests.

### 0.25 Review 21 correction addendum

Review 21 adds focused before/after projection assertions for rejected and recovered-off
publication, including later unexpected-backend failure. Sections 8 and 12 carry the test duty.

### 0.26 Review 22 correction addendum

Review 22 preserves an absent Phase 3 viewport-scale override through the Phase 4 state adapter.
The correction is integrated into §§2–5 and changes binding §5; a fresh review is owed.

### 0.27 Review 23 correction addendum

Review 23 corrects the header's latest-revision marker. No contract or interface changes.

### 0.28 Review 24 correction addendum

Review 24 repairs the closing verification status and adds row-level PD provenance and decisions
to §3.4. No contract or interface changes.

### 0.29 Review 25 correction addendum

Review 25 marks Phase 4's mipmap and declared-attribute projections as pending Phase 3 grants.
Sections 4, 5, 11, and 12 retain the assigned behavior while requesting the dependency change.

### 0.30 Review 26 correction addendum

Review 26 corrects the closing verification status to include §0.29's binding-§5 change.

### 0.31 Review 27 correction addendum

Review 27 defines publication when the authenticated old off publication has no barrier. The
correction is integrated into §§4–5 and §8 and changes binding §5; a fresh review is owed.

### 0.32 Review 28 correction addendum

Review 28 aligns §4's compact publication algorithm with its detailed absent-barrier branch.

### 0.33 Review 29 correction addendum

Review 29 orders all pre-release validation before old-publication mutation and separates closed
publication-protocol failures from candidate-build failures. The corrections change binding §5.

### 0.34 Review 30 correction addendum

Review 30 replaces two undefined hybrid provenance tags with the defined live-web evidence tag.

### 0.35 Coordinated shared-unit rebuild

The maintainer authorizes the four-document rebuild following
`docs/phase13/reviews/PHASE_13_REVIEW_3.md:139-169,304-324`: one structural shared-unit
blocker and eleven corrections. This document integrates its sampler metadata, pure policy callback,
pre-GL failure evidence, select-once credential and consumer hand-offs throughout §§1–12.
Prior addenda remain historical; this addendum supersedes their incompatible ownership/activation
wording. `docs/phase4/reviews/PHASE_4_REVIEW_31.md:52-67` returned literal PASS,
zero findings, Interface changed: no for the preceding bytes, not this rebuild.

Additional reads: this whole document and Review31; `AGENTS.md` and `docs/MOVES.md` in full;
the whole Phase13 Review3; RC3 G1/G5.3/G9/G11.4/G12 and Phase4 assignment at
`docs/design/v2.0-RC3/DESIGN.md:198-341,630-660,790-828,916-934,979-1109,1471-1571`;
Phase3 sampler/materialization/load and canonical framing at
`docs/phase3/v1/PHASE_3_DOC.md:532-666,1389-1410,3233-3249`;
`docs/research/v1/RESEARCH.md:1228-1255,1482-1492` for fixed units/shared targets; and
`docs/phase6/v1/PHASE_6_DOC.md:970-1097,1342-1360` for the existing alias, participants,
cache and retirement gap; `docs/phase8/v1/PHASE_8_DOC.md:225-257,560-607,932-981,2028-2033`
for the current planning cycle, invocation and ungranted four-row request. These narrow extra reads
are needed to close the coordinated seam.
RC3 remains governing even though Review31 used a verification-only v3 override. Phase3's
Review36 applied resolutions remain provisional with changed §5 and no fresh PASS; this
authorization permits coordinated drafting, not verified consumption.

§5 changed in this coordinated rebuild. Unverified; a fresh whole-document review returning literal PASS is required before verified downstream consumption. v1 retained; no directory roll.

### 0.36 Integration remediation (2026-09-07)

IR-03/04/07/18/27/29 reconcile current producer grants and narrow unaccepted execution promises.
RC3 remains governing. Active §§4/5/11 adopt current schema-17 catalog-bound materialization, direct
Phase 3 projections and Phase 1's native legacy configure operation; native source preservation
remains ungranted. Historical addenda/reviews are preserved. This is documentation-only:
§5 changed, this document remains unverified, and fresh whole-document owner verification is
required before implementation consumption. No build, test, formatter or fresh PASS is claimed.

### 0.37 IR-18 authority and schema18 cutover (2026-09-07)

RC3 remains governing. Research precedes the decision: §11.5 records published author docs and
bounded licensed draw/source checks, then the maintainer's explicit prepared-submission/v0.5
selection. §§4/5/11 adopt that choice and P3 schema18; §0.36's schema17/open-case posture is
historical. P7 owns policy/P10 existing adapters/P8 one traversal. No renderer extension API,
reference-code adoption, build/test/formatter/validation run or fresh PASS is claimed.

### 0.38 Native source and effective geometry adoption (2026-09-07)

D-P4-27 adopts P3 §0.61/D-P3-68 schema19 and its complete incorporated §5 source grant;
D-P4-28 publishes effective-provider `geometryInput` and adopts P1's cached linked-input
comparison and R26 fullscreen contract. D-P4-29 adopts the narrowly approved conditional
submission conversion in `docs/decisions/GEOMETRY_PRIMITIVE_COMPATIBILITY.md`; P7/P10 own
the adapter, not this registry. These decisions supersede §0.36/§0.37's source-absence and
schema17/18 posture without rewriting historical addenda or reviews.
Inputs read: this complete P4 document; RC3 Part I and full P4 specification; P3 §0.61,
§5 in full, incorporated native algebra/language/map declarations in §§2.2/4.5 and §11.4;
P1 §5 and incorporated native configure/link/fullscreen contracts plus §11.4 and coordinated
R26 metadata grant; `AGENTS.md` and the named primitive decision. No reference implementation
or forbidden source is newly used. All amended surfaces remain owner-designed/receiver-adopted,
unverified. No implementation, validation command, runtime compatibility or fresh PASS is claimed.

### 0.39 Own-build evidence and schema21 receipt (2026-09-08)

D-P4-31 grants P2 R39-3's architecture-only owner correction: distinguish requested-slot
intentional disablement from own build failure independently of effective fallback. D-P4-32
adopts the coordinated P3 R55 schema21 correction. §§2/4/5/11 publish the required field,
total classification, registry identity cutover and exact receiving contract. RC3 remains
governing; historical decisions and reviews are unchanged. These binding changes are
unverified and grant neither implementation clearance nor T3/runtime authority. No validation
commands, builds, tests, lint or formatters were run for this documentation amendment.

### 0.40 Review 35 sparse-prelude correction and schema23 receipt (2026-09-08)

C35-1 is corrected by D-P4-40: one sparse population contains an optional exact named
virtual prelude and indexed members, never another deferred/composite occurrence. §§2.2/4.1/
4.2/5.1 close construction, both lookups and prelude-first traversal; §§8/11/12 carry the
0/5/99 receiver acceptance trace. D-P4-41 receives the coordinated P3 range-capable selector
schema23/MaterializedSource-v23 cutover, superseding older numeric receipts only.
Inputs read: the complete current owner and R35, AGENTS/MOVES, RC3's full P4 assignment,
Research §3.6.1 and Appendix A.1, and P3's current schema/inspection seam. No reference
implementation was newly mined; N35-1's historical pinned-source limitation remains.
RC3 and source restrictions are unchanged. This documentation-only §5 amendment is unverified;
fresh whole-document producer/receiver review remains required. No validation commands,
implementation, runtime compatibility, fresh PASS or integration clearance is claimed.

### 0.41 Review 36 explicit profile ingress (2026-09-08)

D-P4-43 resolves C36-1 by carrying explicit immutable profile intent immediately after
configuration in every compiler request. §4.7 evaluates that exact pair before availability,
materialization or allocation; §§4.11/4.12/5 publish identity and closed failure handling.
P7 forwards accepted frozen intent; P2 inspection explicitly supplies absent intent for its
unchanged P3 snapshot contract. §8.1 and §12 cover equal-option/different-disable profiles.
RC3, P3 schema23, virtual preludes, provider-wide fallback and historical evidence remain
unchanged. N36-1's pinned-source limitation remains; no new reference mining was needed.
This architecture-only correction is unverified: no validation, implementation, fresh PASS
or integration clearance is claimed.

### 0.42 Review 40 correction addendum

Review 40 (`docs/phase4/reviews/PHASE_4_REVIEW_40.md`, 2026-09-08) returned PASS-WITH-CORRECTIONS
over the full document with no §5 impact. All eight corrections are resolved: four dependency-pin
repoints (§3.1's P3 declaration/materialization row to the P3 §5.1 source/materialization row plus
the closed type algebra, §3.1's sampler algebra row to P3's `DeclaredGlslType`, §3.1's P6 consumer
row to P6 §4.9, §11.5's recorder/replay pin to P1's published events), one pin verified already
landed at current dependency bytes (§4.7's P3 framing pin `3233-3249`), one range completion
(§4.5's RESEARCH slot-count quote to `1143-1145`), and two verification-status rewordings (§0.2 and
§11.5 now state P1's actual posture per P1 §0.35; §3.1's P6 row reads "adopted downstream request"
with the P6 §5.2 owner-review gate open). No contract, interface, decision-ID, or §5 change;
resolutions are recorded in the review file.

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
- deterministic merge of Phase 3's final per-stage declared-uniform catalogs into one immutable
  effective linked-program layout, with source-attributed type-conflict rejection;
- a lossless sampler-only projection of that merge, validated before GL by the Phase5-owned
  fixed policy through a Phase4-owned pure callback, never a copied unit table;
- render-thread compile, attach, fixed pre-link attribute binding, link, validate, cleanup, and
  rung-3 failure conversion through `engine.gl`;
- immutable compiled-registry publication and teardown of Phase-4-owned shader/program handles;
- the “use program” state-barrier interface, including shadow override, program selection,
  alpha/blend lock lifetime, ordered extension points for Phase 6, and a narrow bound-uniform
  lookup/activity capability;
- a monotonically changing pipeline generation consumed by caches and reload paths; and
- diagnostic data needed for the log and shader GUI.

### 1.2 Explicit adjacent ownership

| Concern touched here | Owner |
|---|---|
| Pack discovery, includes, macro preprocessing, option rewriting, directive scanning, source maps, `shaders.properties`, and source materialization | **Phase 3**; Phase 4 requests materialized roots and never reparses or reopens a pack |
| Texture/FBO creation, actual ping-pong side selection, clear rules, framebuffer routing, and realization of flip snapshots | **Phase 5** |
| Sole fixed-name/unit policy, compatible candidate selection, texture-object binding and binding-lease snapshots | **Phase 5**; Phase 4 owns only the callback interface and immutable derived sampler metadata |
| Sampler integer uploads, built-in locations/values/uploads, and uniform error isolation | **Phase 6**; adopted/unverified R7-10 uses Phase 5's resolver, preserving three participants |
| Custom-uniform expression evaluation | **Phase 11**, integrated through Phase 6's barrier participant |
| Pass execution, fullscreen and prepared-submission `countInstances`, hook-to-slot mapping, final-to-Minecraft-FBO handoff and frame restoration | **Phase 7**; approved v0.5 policy integrates existing Phase 10 draw adapters (§4.9/§11.5) |
| Shadow camera, traversal, FBO use, and invocation | **Phase 8**; Phase 4 only supplies force-selection semantics |
| Vertex-buffer layout and enabling the attributes pre-bound here | **Phase 10** |
| Options UI, persistence, and reload triggers | **Phase 12**; Phase 4 supplies the generation signal |
| Modern pass-family activation, compute dispatch, images/SSBOs, and work-group/barrier policy | **G8/S1 and G8/S2**; Phase 4 reserves slots only |
| Shared-context asynchronous compilation | **Phase 14/OQ-15**; Phase 4's v0.1 compiler is synchronous |
| Internal default-pack GLSL and the full frame bootstrap | **Phase 7**; this phase does not grow a second pack source |
| Custom/companion/noise candidates and source/content/lease retirement | **Phase 13**; no texture handle enters Phase 4 |

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
9. **Select once, execute that selection.** A privately issued handle-free selector retains the
   effective binding; texture preflight, object binds, activation and callbacks cannot disagree
   by resolving fallback again.
10. **Sampler declarations are not stage-wide permission.** Every full shape remains lossless,
    including unsupported aggregates; only Phase 5 resolves fixed names and selects physical objects.

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
    record SparseArray(int highestLegalIndex, int highestPopulatedIndex,
        Optional<ProgramSlotId> virtualPrelude) implements PassPopulation {}
}

public interface StageRegistry {
    List<StageStep> schedule(); // immutable, execution order
    List<PassDescriptor> passes(StageStep step); // immutable population order
    Optional<PassDescriptor> named(StageStep step, ProgramSlotId id);
    Optional<PassDescriptor> indexed(StageStep step, PassIndex index);
}

public record PassDescriptor(
    StageStep step,
    ProgramSlotId slot,
    Optional<PassIndex> index,
    PassResourceAccess resources,
    Set<ComputeDispatchSlot> computeSlots) {}

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

`highestLegalIndex` is 99 for modern array families. `highestPopulatedIndex` describes indexed
descriptor membership only: the greatest present index, or `-1` iff none exists; never a count
or a prelude index; require `-1 <= highestPopulatedIndex <= highestLegalIndex == 99`.
Classic configurations populate through 15; sparse fixtures may end at 0,
5 or 99, and holes do not suppress later members. All components, optionals and members are
non-null. `virtualPrelude` is `Optional.of(ProgramSlotId("deferred_pre"))` only for
`DEFERRED / BETWEEN_GBUFFERS`, or `Optional.of(ProgramSlotId("composite_pre"))` only for
`COMPOSITE / FRAME_END`; every other sparse stage requires empty. The optional names membership,
not a second descriptor payload: present requires exactly one matching contained descriptor;
empty forbids a virtual member. Required G6/full-shape configurations include their exact prelude.

The named `deferred_pre` and `composite_pre` descriptors are the only v0.1 virtual transitions.
Their catalog slots have `ProgramSlotKind.VIRTUAL_FLIP_CONTROL`, empty source stem and fallback;
their descriptors have empty `index`, `computeSlots`, `readable`, `writes` and
`mipmappedBeforeRead`, retaining only Phase-3-derived `PassResourceAccess.explicitFlips`.
They never resolve to a `ResolvedProgramDescriptor`, compile, select, bind, draw or provide
fallback. Phase 7 obtains the contained descriptor by `passes` or the exact prelude `named`
lookup and passes it unchanged to Phase 5's `applyVirtualTransition(frameId, pass)`.
Neither receiver synthesizes a missing prelude, program, indexed surrogate or altered flip state
(`[D-P4-17]`, `[D-P4-40]`). Prelude-only and completely empty sparse populations both use
`highestPopulatedIndex=-1`, distinguished by `virtualPrelude`.

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
    record Explicit(List<DrawRoutingSlot> slots) implements DrawRouting {}
}
public sealed interface DrawRoutingSlot {
    record Attachment(BufferRef buffer) implements DrawRoutingSlot {}
    record None() implements DrawRoutingSlot {}
}

public record ProgramStateBundle(
    DrawRouting drawRouting,
    Set<BufferRef> compositeMipmaps,
    int instanceCount,
    Set<ExtendedAttribute> attributes,
    Optional<AlphaTestSpec> alphaTest,
    Optional<BlendSpec> blend,
    Optional<ViewportScale> viewportScale,
    Map<BufferRef, Boolean> explicitFlips,
    Optional<LegacyGeometryConfig> legacyGeometry,
    GeometryInputRequirement geometryInput) {}

public enum GeometryInputRequirement {
    NONE, POINTS, LINES, LINES_ADJACENCY, TRIANGLES, TRIANGLES_ADJACENCY
}

public record ProgramUniformLayout(
    ProgramUniformLayoutFingerprint fingerprint,
    Map<String, ProgramUniformDeclaration> declarations) {}

public record ProgramUniformDeclaration(
    String exactName,
    DeclaredGlslType type,
    List<ProgramUniformDeclarationSite> sites) {}

public record ProgramUniformDeclarationSite(
    ShaderSourceStage declaringStage,
    AttributedSourceLocation location,
    MaterializationFingerprint materialization) {}

public record ProgramUniformLayoutFingerprint(String value) {}

public record ProgramSamplerDeclaration(
    String exactName, DeclaredGlslType type, int declarationOrder,
    List<ProgramUniformDeclarationSite> sites) {}
public record ProgramSamplerLayoutFingerprint(String value) {}
public record FixedSamplerPolicyFingerprint(String value) {}
public sealed interface ProgramSamplerLayout {
    ProgramSamplerLayoutFingerprint fingerprint();
    FixedSamplerPolicyFingerprint policyFingerprint();
    record Shader(ProgramSamplerLayoutFingerprint fingerprint,
        FixedSamplerPolicyFingerprint policyFingerprint, StageId effectiveStage,
        Set<StageBand> validatedBands, List<ProgramSamplerDeclaration> declarations,
        SamplerLayoutValidation validation) implements ProgramSamplerLayout {}
    record FixedFunctionEmpty(ProgramSamplerLayoutFingerprint fingerprint,
        FixedSamplerPolicyFingerprint policyFingerprint) implements ProgramSamplerLayout {}
    record VirtualNotApplicable(ProgramSamplerLayoutFingerprint fingerprint,
        FixedSamplerPolicyFingerprint policyFingerprint) implements ProgramSamplerLayout {}
}
public record FixedUnitSamplerConflict(
    int unit, List<ProgramSamplerDeclaration> witnesses) {}
public enum SamplerLayoutIssueCode {
    UNSUPPORTED_NAME, UNSUPPORTED_STAGE_DOMAIN, UNSUPPORTED_SHAPE
}
public record SamplerLayoutIssue(SamplerLayoutIssueCode code, StageBand band,
    ProgramSamplerDeclaration declaration) {}
public sealed interface SamplerLayoutValidation {
    record Valid() implements SamplerLayoutValidation {}
    record ConflictingTypes(List<FixedUnitSamplerConflict> conflicts,
        List<SamplerLayoutIssue> otherIssues) implements SamplerLayoutValidation {}
    record Unsupported(List<SamplerLayoutIssue> issues) implements SamplerLayoutValidation {}
}
public interface FixedSamplerLayoutPolicy {
    FixedSamplerPolicyFingerprint fingerprint();
    SamplerLayoutValidation validate(StageId effectiveStage, StageBand effectiveBand,
        List<ProgramSamplerDeclaration> declarations);
    List<SamplerUnitAssignment> initializationAssignments(ProgramSamplerLayout.Shader layout);
}

sealed interface CompiledProgramBinding { // Phase-4-private
    ProgramStateBundle state();
    ProgramSlotId provider();

    record ShaderProgram(
        ProgramSlotId provider,
        ProgramHandle handle,
        ProgramStateBundle state,
        ProgramUniformLayout uniformLayout,
        ProgramSamplerLayout samplerLayout,
        List<MaterializationFingerprint> sources)
        implements CompiledProgramBinding {}

    record FixedFunction(
        ProgramSlotId provider,
        ProgramStateBundle state)
        implements CompiledProgramBinding {}
}

record ResolvedCompiledProgramBinding( // Phase-4-private
    ProgramSlotId requested,
    CompiledProgramBinding effective,
    List<ProgramSlotId> fallbackPath) {}

public record ResolvedProgramDescriptor(
    ProgramSlotId requested,
    ProgramSlotId effective,
    ProgramStateBundle state,
    ProgramUniformLayout uniformLayout,
    ProgramSamplerLayout samplerLayout,
    List<MaterializationFingerprint> sources,
    List<ProgramSlotId> fallbackPath) {}

public final class ProgramBindingSelection {
    private ProgramBindingSelection(/* private publication/context/binding credential */);
    public long registryGeneration();
    public RegistryFingerprint registryFingerprint();
    public ProgramSlotId requested();
    public ResolvedProgramDescriptor effectiveDescriptor();
    public StageId effectiveStage();
    public StageBand actualBand();
    public BarrierContext originatingContext();
}
public sealed interface ProgramSelectionResult {
    record Selected(ProgramBindingSelection selection) implements ProgramSelectionResult {}
    record Skipped(ProgramSlotId requested) implements ProgramSelectionResult {}
    record StalePublication(long expectedGeneration, long currentGeneration)
        implements ProgramSelectionResult {}
    record ShadersOff(String diagnosticId) implements ProgramSelectionResult {}
}
public enum ProgramSelectionRejection {
    INVALID_ISSUER, STALE_GENERATION, STALE_CONTEXT, WRONG_STAGE_BAND,
    PROVIDER_LAYOUT_MISMATCH
}
public sealed interface ProgramSelectionValidation {
    record Valid() implements ProgramSelectionValidation {}
    record Rejected(ProgramSelectionRejection reason) implements ProgramSelectionValidation {}
}
public final class ProgramBindingSelections {
    public static ProgramSelectionValidation validateSelection(
        ProgramBindingSelection selection, BarrierContext context);
}
public record UseProgramRequest(
    ProgramBindingSelection selection, BarrierContext context) {}

public enum ProgramResolutionStatus { SOURCED, CHAIN, ABSENT, FAILED }
public enum ProgramOwnBuildDisposition {
    NOT_APPLICABLE, NO_SOURCE, DISABLED, SUCCEEDED, FAILED
}

public record ProgramResolutionProjection(
    ProgramSlotId slot,
    ProgramResolutionStatus status,
    Optional<ProgramSlotId> from,
    boolean sourcePresent,
    ProgramOwnBuildDisposition ownBuild,
    String driverLog) {}
```

`AlphaTestSpec`, `BlendSpec`, `ViewportScale`, `LegacyGeometryConfig`, and
`ExtendedAttribute` are Phase 3 values or lossless Phase-4-owned mirrors created by an explicit
adapter. They never contain GL constants.
`DeclaredGlslType`, `ShaderSourceStage`, `AttributedSourceLocation`, and
`MaterializationFingerprint` are the Phase3-owned values; Phase4 neither reparses source nor
redefines their type algebra. `ProgramUniformLayout.declarations` is immutable and iterates by
exact uniform name in Unicode code-point order. Each declaration's `sites` preserves linked-stage
order `VERTEX`, `GEOMETRY`, `FRAGMENT`, then `COMPUTE`, and source token order within a stage.
Fixed-function descriptors use the canonical empty layout; shader descriptors carry the effective
provider's merged layout.
`geometryInput` is required and non-null: the effective provider's actual linked GEOMETRY
input, checked against P3's final geometry form before candidate publication (§4.8).
`NONE` means no linked geometry stage (including fixed-function sentinel), never unknown,
unavailable or failed geometry. The optional `legacyGeometry` is provenance/configuration,
not a predicate for geometry presence: an ordinary core geometry provider has a non-NONE
input and empty legacy config. Virtuals have no program bundle or fabricated geometry.
Detached descriptors/views retain this enum safely after close/replacement, not a draw capability.
Pure planning retains the expected input in the private build plan; it does not publish a
`ProgramStateBundle` as linked truth before §4.8's agreement/validation. Final bundles and
every descriptor/selection/detached projection are minted from that checked provider binding.
The sampler projection excludes nonsamplers, retains direct `DeclaredGlslType.Sampler` values
and losslessly retains sampler-containing arrays/structs as unsupported shapes. It never casts
an aggregate to sampler2D. `declarationOrder` is the zero-based first-occurrence ordinal in the
sampler projection, using VERTEX/GEOMETRY/FRAGMENT/COMPUTE then token order; equal name/type
repetitions coalesce with all sites retained. Lists/maps/sets are immutable. Conflicts have units
0–15 in ascending order and every witness in declaration order. Issues are canonical by
StageBand declaration order, declarationOrder, then issue-code declaration order. Empty conflict
or unsupported variants, unrelated witnesses/bands, malformed/duplicate payloads and nulls are
invalid callback output. A conflict plus unsupported evidence uses `ConflictingTypes`, not loss
of either domain. `Shader.validatedBands` is every provider-permitted band only after all validate;
failed layout evidence retains the attempted bands and validation payload, never successful status.
Fixed and virtual variants have distinct canonical encodings. Virtual metadata belongs to
planning, never to a fabricated descriptor or selection.

The registry build/publication boundary:

```java
public interface ProgramRegistryCompiler {
    RegistryBuildResult compile(RegistryBuildRequest request);
}

public record RegistryBuildRequest(
    PackConfiguration configuration,
    Optional<ProfileName> profileSelection,
    DimensionKey dimension,
    MacroContribution macroContribution,
    FixedSamplerLayoutPolicy samplerPolicy,
    GLCapabilityProfile capabilities,
    GLDevice device,
    DiagnosticReporter diagnostics) {}

public sealed interface RegistryBuildResult {
    record Ready(CompiledRegistryCandidate candidate) implements RegistryBuildResult {}
    record ShadersOff(RegistryBuildFailure failure) implements RegistryBuildResult {}
}

public interface ProgramRegistryView {
    StageRegistry stages();
    Optional<ResolvedProgramDescriptor> resolve(ProgramSlotId requested);
    List<ProgramResolutionProjection> resolutions();
    RegistryFingerprint fingerprint();
    FixedSamplerPolicyFingerprint samplerPolicyFingerprint();
}

public interface CompiledProgramRegistry extends ProgramRegistryView, AutoCloseable {
    void close(); // render thread; idempotent; deletes only Phase-4-owned handles
}

public final class CompiledRegistryCandidate implements AutoCloseable {
    CompiledRegistryCandidate(/* private registry + compiler-origin credential */);
    public ProgramRegistryView view(); // immutable detached metadata snapshot
    public void close(); // caller-owned before acceptance; idempotent
    // opaque product; no public/protected constructor, factory, subclass, or registry accessor
}

public interface ProgramRegistryPublisher {
    PublishedRegistry current();
    PublicationResult publish(
        RegistryPublication publication,
        BarrierContext releaseContext);
}

public record PublishedRegistry(
    long generation,
    Optional<ProgramRegistryView> registry,
    Optional<PublishedProgramStateBarrier> barrier,
    BarrierContextSource contexts) {}

public interface PublishedProgramStateBarrier {
    long generation();
    ProgramSelectionResult select(ProgramSlotId requested, BarrierContext context);
    BarrierResult activate(UseProgramRequest request);
    BarrierResult releaseToFixedFunction(BarrierContext context);
    // non-owning view: deliberately no close operation
}

public sealed interface RegistryPublication {
    record Ready(CompiledRegistryCandidate registry, BarrierPublicationCandidate barrier)
        implements RegistryPublication {}
    record ShadersOff(RegistryBuildFailure cause) implements RegistryPublication {}
}

public sealed interface PublicationResult {
    record Accepted(PublishedRegistry published) implements PublicationResult {}
    record Rejected(PublishedRegistry unchanged, PublicationFailure cause)
        implements PublicationResult {}
    record RecoveredOff(PublishedRegistry published, PublicationFailure cause)
        implements PublicationResult {}
}
```


`profileSelection` is a required non-null wrapper; empty explicitly means no selected profile,
not inferred profile intent. The configuration is immutable and carries no hidden selection.
All construction sites supply this component, including selection-only rebuilds with unchanged
options. §4.7 binds evaluation to this exact pair; no preview `OptionState` is accepted.

`publish` is render-thread-only and increments generation exactly once for every accepted
replacement, including accepted shaders-off, and once for forced `RecoveredOff`; pre-release
rejection and failed candidate compilation alone do not mutate publication state. Equality, never
ordering or subtraction, is the cache protocol; eventual signed-`long` wrap does not make a stale
equality likely within the life of a process.

`current()` returns one atomic snapshot: a ready publication contains a non-owning registry view
and barrier view with the same generation; accepted shaders-off and `RecoveredOff` contain
neither. The candidate owner receives only `CompiledRegistryCandidate`; only Phase-4 internals and,
after acceptance, the publisher can reach its private `CompiledProgramRegistry`. Snapshot
consumers have no registry or barrier teardown capability.
`ProgramRegistryView.resolve` projects the private compiled binding to an immutable descriptor:
requested/effective identities, provider state, handle-free uniform and sampler layouts, source
fingerprints, and fallback path only. It is inspection, never authority to select or bind.
`ProgramRegistryView.resolutions()` is an immutable detached list in complete classic/superset
catalog order, with exactly one row per `ProgramSlotDescriptor`, including virtual and fixed
sentinels. The list is computed from source-presence and build evidence before fallback is folded;
it contains no GL handle or publication generation. A candidate view and the later accepted
published view expose value-equal rows for the same registry candidate.
Neither that descriptor nor any barrier result contains `ProgramHandle`; only the private registry
and barrier implementation can obtain the handle used for `ShaderService.use`.
`CompiledRegistryCandidate.view()` copies that immutable metadata into a detached snapshot at the
call. The snapshot remains safe and unchanged after candidate close, pre-release rejection,
`RecoveredOff`, or accepted ownership transfer; it neither observes later publication state nor
keeps the candidate, private registry, or any GL handle alive.
Every barrier-view method is render-thread-only and first verifies that both its generation and
identity still match the current ready publication. A replaced barrier view returns
the operation's `StalePublication` result without a GL call or barrier-state change; the caller
must not draw and must reacquire `current()`. `select` uses `ProgramSelectionResult`, while
activation/release use `BarrierResult`. No retained view can act on a replacement generation.

### 2.3 Relationship map

```text
PackConfiguration + DimensionKey + MacroContribution + samplerPolicy + GLCapabilityProfile
             │
             ├─ StageRegistryDefinition ── G6 / modern configuration
             ├─ ClassicProgramCatalog  ── fallback graph
             └─ SourceMaterializer requests (Phase 3)
                    └─ DeclaredUniformCatalogs
                              │
           ProgramUniformLayout + ProgramSamplerLayout ← Phase5 pure fixed policy
                              │
                       RegistryBuildPlan       (pure/off-thread)
                              │
                       ProgramCompiler         (render thread, GLDevice)
                              │
                CompiledRegistryCandidate
                    (private registry)
                    ├─ non-owning view() ── Phase 5/7
                    │                       pre-publication derivation/validation
                    │
                    └─ opaque candidate ── Phase 7 production composition + publication
                                              │
                                      PublishedRegistry
                                   (non-owning registry/barrier views)
                                        ├─ Phase 5
                                        ├─ Phase 6
                                        ├─ Phase 7/8
                                        └─ generation → Phase 12/caches
                                              └─ select once → ProgramBindingSelection
                                                   → Phase5 object binds → activate retained binding
                                                   → Phase6 exact effective-layout callbacks

Fixed attribute table ── Phase 10 vertex-source inputs
```

## 3. Contract conformance map

### 3.1 Stage, lifecycle, compile, and barrier contracts

| In-scope contract item | Design element | Provenance |
|---|---|---|
| Modern order `setup → begin → shadow → shadowcomp → prepare → gbuffers opaque → deferred → gbuffers translucent → composite → final` | `ModernSupersetConfiguration` supplies ten `StageStep`s; `GBUFFERS` appears twice under distinct bands | `[D-4]`, `docs/research/v1/RESEARCH.md:336`–`:355` |
| `setup`, `begin`, `shadowcomp`, `prepare` arrays accept 0…99 | `PassIndex`, `SparseArray(99, …, Optional.empty())`, sparse map storage | `[V:web]`, `docs/research/v1/RESEARCH.md:340`–`:353` |
| G6 order and five active identities | `ClassicG6Configuration` in §4.2 | `[V:doc]`, `docs/research/v1/RESEARCH.md:220`–`:224` |
| One deferred/composite occurrence contains virtual pre then sparse raster indices | §2.2 `SparseArray.virtualPrelude`, §4.1 exact membership/lookups, §8.1 pre/0/5/99 trace | Research §3.6.1/App A.1 `[V:doc]`; owner representation D-P4-40 |
| Per-pass read/write sets and flip bookkeeping | immutable `PassResourceAccess`; exact writes and flips from Phase 3, stage-readable sets from the stage policy | `[D-4]`, `docs/research/v1/RESEARCH.md:835`–`:841` |
| Dormant `.csh`, `_a`…`_z` companions | `ComputeDispatchSlot`; no compiler/executor path before G8/S2 | `[D-4]`, Phase 4 assignment at `docs/design/v2.0-RC3/DESIGN.md:1486`–`:1491` |
| `.csh` companions exist for every eligible program except gbuffers | construction rejects every compute descriptor whose pass has `StageId.GBUFFERS` | `[V:web]`, `docs/research/v1/RESEARCH.md:357`–`:361` |
| Pack-load initialization compiles, resolves fallbacks, then publishes | transactional compiler and publisher | `[V:observed]`, `docs/research/v1/RESEARCH.md:483`–`:488` |
| Pack/option/dimension/resolution uninit deletes GL objects | registry `close`, candidate cleanup, generation publication | `[V:observed]`, `docs/research/v1/RESEARCH.md:488`–`:491` |
| Compile → attach → bind 10/11/12 → link → validate | state machine in §4.7 through Phase 1 `ShaderService` | governing sequence at `docs/design/v2.0-RC3/DESIGN.md:1511`–`:1514`; failure/barrier evidence at `docs/research/v1/RESEARCH.md:497`–`:505` |
| Core program/shader object facade, no ARB object entry points | only Phase 1 `ShaderService` is consumed | `[U]` opportunity adopted by governing spec; `docs/research/v1/RESEARCH.md:766`–`:770` |
| Core-layout and native ARB geometry retain their distinct source forms | §4.8 consumes current P3 preserving materialization; actual linked input must agree before READY, with full fallback on failure | `[V:doc]`, `docs/research/v1/RESEARCH.md:213`–`:216`, App A.3; D-P4-27/28/30/32 |
| Invalid compile/link/validate result deletes program and reports to GUI/log | `ProgramBuildFailure`, cleanup ledger, backup re-resolution | `[V:observed]`, `docs/research/v1/RESEARCH.md:501`–`:505` |
| Final materialized declarations are not reopened or inferred from driver activity | Merge Phase3 DeclaredUniformCatalogs into attributed uniform and sampler layouts; reject unequal structural types before GL, keep optimized-out declarations | Phase3 closed published algebra/materialization contract at `docs/phase3/v1/PHASE_3_DOC.md:3379,1591-1594`; current provisional state in §0.2 |
| Program use re-points samplers, refreshes built-ins, evaluates customs, locks alpha/blend | ordered `ProgramStateBarrier` in §4.10 | `[V:observed]`, `docs/research/v1/RESEARCH.md:505`–`:507`; adoption `D-P4-5` |
| Phase 6 participants require bound lookup and between-activation activity proof without a program handle | callback-scoped `BoundProgramUniformAccess`, generation/provider/layout cache key, and retainable operation-free epoch token | D-6; adopted downstream request (P6 §5.2 owner-review gate open, P6 currently unverified) at `docs/phase6/v1/PHASE_6_DOC.md:1279`–`:1299` |
| Shadow pass overrides hook-requested program | barrier selection step 1 | `[V:observed]`, `docs/research/v1/RESEARCH.md:506`–`:507` |
| Reload invalidates downstream derived caches | `PublishedRegistry.generation` equality protocol | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/PipelineManager.java:87, "versionCounterForSodiumShaderReload++"]`; `D-P4-8` |
| Per-program target-specific custom textures on fixed units | Full sampler projection + pure Phase5 policy + authenticated single selection; §8.6 sharedUnit_programSpecificTargets/effectiveFallbackLayout | `[D-P4-18]`, `[D-P4-19]`; `docs/research/v1/RESEARCH.md:1484-1490` |
| Same-unit incompatible sampler declarations | Pre-GL SAMPLER_LAYOUT evidence and ordinary provider-local fallback; §8.6 sharedUnit_incompatibleAliasesBeforeBind/fullSamplerShape | `[D-P4-18]`; `docs/research/v1/RESEARCH.md:1488-1490`; complete algebra `docs/phase3/v1/PHASE_3_DOC.md:1499-1515` |
| Fixed0–15 map and conditional shadow alias | Phase5 alone validates/resolves names; Phase6 consumer migration pending; §8.6 sharedUnit_fixedRangeAndShadowAlias | `[D-P4-18]`; `docs/research/v1/RESEARCH.md:1228-1255`; `docs/phase6/v1/PHASE_6_DOC.md:1187-1226` |
| Stale selection/lease must not authorize mutation or draw | Pure credential check, same selection through Phase5 bind then activation, coherent off compensation; §8.6 binding_* and pipeline_textureFailureCompensates | `[D-P4-19]`; coordinated architecture decision, not observed implementation |

### 3.2 Appendix A.1 program/fallback map

The table below maps every named Appendix A.1 row. “Fixed” means the contract has no shader
ancestor and requires fixed-function/no-program behavior. Phase 4 selects that terminal through
Phase 1's published `ShaderService.useFixedFunction()` operation; it never encodes program zero as
`use(null)`, a raw integer, or a sentinel `ProgramHandle`.

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
| `final` | screen | absent means downstream-owned passthrough copy | terminal raster/passthrough binding |

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
| Empty source inherits nearest non-empty ancestor's **entire configuration** | the private resolved binding points at the ancestor's one immutable compiled binding; its public `ResolvedProgramDescriptor` projection overlays no child field | App A.2 `[V:observed]`; `D-P4-4` |
| Disabled/profile-disabled is absent | availability filter runs before graph resolution | App A.2; Phase 3 §5 `ProgramStateModel` |
| `shadow` never inherits | `shadow` has no parent; its two children may inherit it as the table says | App A.1/A.2 |
| `mc_Entity` | `ExtendedAttribute.MC_ENTITY` → bind location 10 when declared | App A.3 `[V:doc]` |
| `mc_midTexCoord` | `ExtendedAttribute.MC_MID_TEX_COORD` → 11 when declared | App A.3 `[V:doc]` |
| `at_tangent` | `ExtendedAttribute.AT_TANGENT` → 12 when declared | App A.3 `[V:doc]` |
| `countInstances` | positive total `instanceCount`, exposed unchanged; Phase 7 v0.5 fullscreen/prepared-submission policy, with no repeated world traversal | App A.3 `[V:doc]`; maintainer decision §11.5 |
| ARB geometry extension + `maxVerticesOut` | `LegacyGeometryStrategy`; topology is triangles → triangle strip, count is Phase 3's positive value | App A.3 plus behavioral digest; `D-P4-7` |
| `DRAWBUFFERS` / `RENDERTARGETS` | `DrawRouting.Explicit`, validated in order and without deduplication | App A.3 `[V:doc]` |
| Absent routing | `DrawRouting.AllUsedBuffers` resolved by Phase 5 against its estate | §3.2 `[V:doc]` |
| `colortexNMipmapEnabled` | immutable `compositeMipmaps`; generation is Phase 7, texture policy Phase 5 | App A.3 `[V:doc]` |
| `alphaTest.<prog>` | optional lock value, including explicit OFF | App F.7 through Phase 3 §5 |
| `blend.<prog>` | optional lock value, including explicit OFF | App F.7 through Phase 3 §5 |
| `scale.<prog>` | optional immutable `ViewportScale`; empty means no viewport override, while a present identity value remains explicit; Phase 7 applies present values to Phase 5's estate | App F.7 through Phase 3 §5 |
| `flip.<prog>.<buf>` | exact tri-state result represented as only explicit map entries; Phase 5 owns default/side realization | App F.7 through Phase 3 §5 |
| `program.<prog>.enabled` and profile disable | compile availability; false behaves exactly like missing source | App F.7/App A.2 through Phase 3 §5 |

The resource-allocation directives elsewhere in Appendix A.3 remain Phase 5/6/8/13 inputs.
Phase 4 retains only the per-program fields its assignment names.

### 3.4 Pintonium mechanism disposition

| Reference mechanism | Contract check | Phase 4 disposition |
|---|---|---|
| Recursive memoized fallback | Checked edge by edge against App A.1/A.2; contract adds rows the reference lacks | Adopt structure under `D-P4-4` (PD §3.1); never import reference enum values |
| `Program.use()` as universal barrier | Checked against RESEARCH §4.2's four barrier duties | Adopt ordered interface under `D-P4-5` (PD §3.2); images/memory barriers remain G8/S2 |
| `CompositeRenderer.Pass` field bundle | Compared with App A.1/F.7 and Phase 3 §5 | Routing, viewport, mipmap and flip-config shapes retained under `D-P4-10` (PD §3.3). Framebuffer and resolved flip side belong to Phase 5. Per-buffer blend and compute execution are rejected under `D-P4-11` |
| 26-value `WorldRenderingPhase` + override/deferred-pop | Compared with D-4 and Phase 7 ownership | Structural cross-check only (PD §3.1). Stage identity and hook-time rendering phase remain separate types |
| Locations 11/11/12/13/14 | Conflicts with App A.3's 10/11/12 | Pre-decided rejection `D-P4-6` (PD §§3.2, 18) |
| Generation counter | Reload invalidation is not pack syntax but satisfies the assigned cache contract | Adopt equality-based long generation under `D-P4-8` (PD §3.1) |
| Generated legacy-compat shaders selected by pack-layout heuristics | Heuristics have no App A/F contract and are a recorded bug source | Reject under `D-P4-4` (PD §§13, 19.2). Fixed fallback and explicit backup edges only |

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
7. sparse virtual-prelude membership and descriptor invariants obey §2.2 and the rules below;
8. compute slots are descriptors only unless the configuration explicitly enables G8/S2; and
9. no descriptor in either `GBUFFERS` occurrence has a compute slot.

`StageRegistry.schedule()` is the sole deterministic traversal: configuration order, including
both distinct gbuffers `StageStep` values. Each DEFERRED and COMPOSITE appears once; no prelude
creates another stage occurrence. `passes(step)` accepts only a contained schedule step and
returns an immutable list: NamedPrograms in declared catalog order, Singleton once, SparseArray's
present virtual prelude first followed by all populated indexed members in ascending index order,
with holes omitted. A prelude is not index 0 and does not affect either index bound.

`named(step,id)` accepts declared NamedPrograms keys; on SparseArray it accepts only the exact
stage-specific prelude key above, returning its contained descriptor when present and empty
when legally omitted. Sparse stages without a legal prelude reject named lookup.
`indexed(step,index)` accepts only SparseArray and addresses only indexed members, returning
empty for a legal hole (including above highestPopulatedIndex through highestLegalIndex).
Singleton has neither lookup kind. Named lookup of raster stems, arbitrary/cross-family prelude
names, or indexed lookup on non-sparse populations is rejected, not converted into absence.
Foreign steps, null/illegal keys and wrong lookup kinds reject at the lookup boundary before
any operation; invalid definitions reject during construction before publication.

Construction requires the prelude's optional name and exactly one descriptor to agree in both
directions, exact family/band/catalog-kind membership, and all §2.2 empty-index/no-program
invariants. Every indexed member has a present unique legal index and exact §4.3 family stem;
deferred/composite indexed members are RASTER, never virtual. Other sparse families retain their
existing compute-only/dormant contracts. Reject a virtual member in NamedPrograms/Singleton,
a virtual indexed member, raster/fixed sentinel as prelude, wrong/extra/duplicate prelude,
descriptor with a foreign step, wrong stage/band, duplicate slot/index, inconsistent maximum,
or any descriptor outside its declared population. Every returned descriptor's `step()` is the
identical contained schedule value. Lookup and traversal return that same contained descriptor,
not independently synthesized records; resource access and compute sets remain complete.

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
| 3 | `DEFERRED / BETWEEN_GBUFFERS` | `SparseArray(99,15,Optional.of(deferred_pre))`: exact virtual pre, then populated raster indices 0…15 |
| 4 | `GBUFFERS / GBUFFERS_TRANSLUCENT` | hook-selected translucent-side named slots |
| 5 | `COMPOSITE / FRAME_END` | `SparseArray(99,15,Optional.of(composite_pre))`: exact virtual pre, then populated raster indices 0…15 |
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
| 6 | `DEFERRED / BETWEEN_GBUFFERS` | `SparseArray(99,h,Optional.of(deferred_pre))`: pre then sparse raster 0…99 |
| 7 | `GBUFFERS / GBUFFERS_TRANSLUCENT` | named programs |
| 8 | `COMPOSITE / FRAME_END` | `SparseArray(99,h,Optional.of(composite_pre))`: pre then sparse raster 0…99 |
| 9 | `FINAL / SCREEN` | singleton |

The table's prelude tokens abbreviate exact `ProgramSlotId` values; `h` is the greatest
populated indexed member or -1. All other sparse rows use `Optional.empty()`.

At v0.1, `SETUP`, `BEGIN`, `SHADOWCOMP`, and `PREPARE` have
`highestPopulatedIndex = -1`; their ability to hold descriptors, access sets, flips, and compute
companions is nevertheless tested. G8/S1 changes only population and executor wiring.

### 4.3 Sparse pass families and source stems

Index zero uses the unsuffixed name: `deferred`, `composite`, `begin`, and so on. Positive indices
append the decimal value without zero padding. The naming function is total only for 0…99 and is
round-trip-tested.

A non-gbuffers raster pass may own compute descriptors `Primary` and `Companion('a'…'z')`; every
gbuffers descriptor is rejected if that set is non-empty. Eligible slots map to the pack's primary
`.csh` and suffixed `_a`…`_z.csh` names. The slots contain no source, work-group
dimensions, image binding, SSBO binding, indirect pointer, or dispatch function at v0.1. That
information is intentionally impossible to construct before G8/S2.

No registry array is allocated at a constant 8 or 16. A compact implementation may use an array
sized `highestPopulatedIndex + 1` for one immutable configuration, but every API and validation
rule is expressed through `PassIndex` and the definition's limit.

### 4.4 Resource-access model

`PassResourceAccess` describes stage-wide readable permission, not the program's declaration set.
The effective `ProgramSamplerLayout` is the separate exact declaration contract:

- `readable` is the complete set the stage is allowed to sample from. For classic deferred,
  composite, and final it is all allocated colortex buffers; for shadowcomp it is all allocated
  shadow depth/color inputs; for gbuffers it is external/game textures and any stage-legal pack
  textures. A symbolic range is expanded only after Phase 5 supplies the estate size.
- `writes` is exact when routing is explicit. `AllUsedBuffers(domain)` remains symbolic until
  Phase 5 supplies the configured used-buffer set.
- `explicitFlips` is only the Phase 3 tri-state override entries. Absence is not serialized as
  false.
- `mipmappedBeforeRead` is the per-program request set Phase 4 must receive from Phase 3 once the
  §5 grant requested below is verified. Phase 7 requests Phase 5's logical-buffer mipmap
  operation; Phase 5 preserves custom/foreign override parameterization rather than modifying it.

The registry never stores a “main” or “alt” texture choice. That snapshot depends on previous
passes and belongs to Phase 5. This is the deliberate difference from Pintonium's per-pass
`stageReadsFromAlt` field at `CompositeRenderer.java:159`.
Stage-readable permission does not force binds for undeclared units. Phase5 resolves exact declared
names using FixedSamplerName, filters each cell's multiple candidates by every sampler field, then
chooses greatest compatible Phase3 canonical ordinal for custom entries; aliases do not rename
source keys. Compatible custom may override fullscreen backing. Equal-shape aliases on one unit
must select the same source/parameterization identity or return CONFLICTING_CANDIDATES.
Unsupported aggregate/arrayed/multisample/CUBE/BUFFER shapes never become arbitrary 2D binds.
Each shader uses only declared units; absent/incompatible required backing yields typed suppression.

### 4.5 Classic catalog construction and cardinality independence

`ClassicProgramCatalog` is constructed from declarative rows and validates unique names, legal
stages, known fallback parents, acyclic edges, virtual-slot isolation, and source-stem uniqueness.
It is not constructed with a fixed catalog-size array.

The governing inputs now agree:

- `docs/research/v1/RESEARCH.md:1143-1145` says
  "Count: 60 named shader/virtual slots, excluding the external `<none>` sentinel";
- that table names 3 shadow slots, 22 gbuffers slots, 17 deferred entries including pre,
  17 composite entries including pre, and final: **60** named shader/virtual slots, excluding the
  `<none>` sentinel; and
- the pack-author table at
  `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:61`–`:108` independently lists those
  same names and both arrays through index 15.

The concrete contract rows remain controlling. The catalog therefore contains every table row,
reports both `declaredClassicCount = 60` and `enumeratedClassicCount = 60` in a contract test, and
makes no behavior depend on either number. The former 43/60 conflict no longer exists in the
governing inputs: **60** is the authoritative summary count. No named row is dropped, and the count
does not become an allocation constant. Catalog rows are constructed first; the numeric total
remains validation metadata only.

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

`Missing` means no `.vsh`, `.gsh`, or `.fsh` source exists for the stem. `Disabled` means a
present source was intentionally disabled by Phase 3's evaluated property/profile state.
`Failed` covers candidate-local planning, materialization, capability and GL build failures.
All three are absent for fallback purposes; absence for resolution is not evidence of disablement.

In parallel, Phase 4 records `sourcePresent` before materialization, enablement, compilation, or
fallback: it is true iff the selected dimension's pack source set contains at least one source
stage for the requested slot's stem. It therefore remains true when that source is disabled or
fails. Virtual/fixed sentinels have no source stem and report false.

Classify the requested slot **before fallback**, in this strict order:

1. Virtual/fixed catalog sentinels: `NOT_APPLICABLE`, `sourcePresent=false`, no own build.
   A real slot ending at a fixed terminal is not itself a fixed sentinel.
2. Real slot with no selected source stages: `NO_SOURCE`, `sourcePresent=false`, even if its
   evaluated enablement is false. Missing overrides are never filled by merging base sources.
3. Present real slot intentionally profile/property-disabled by P3: `DISABLED`,
   `sourcePresent=true`; do not materialize or attempt its build.
4. Present enabled real slot: attempt its complete own planning/build. Only completed successful
   validation is `SUCCEEDED`; every candidate-local rejection/error is `FAILED`, both with
   `sourcePresent=true`. This includes MATERIALIZE, CAPABILITY, SAMPLER_LAYOUT, COMPILE,
   ATTRIBUTE_BIND, LINK, VALIDATE and candidate-build UNEXPECTED_BACKEND, uniform conflicts,
   unsupported geometry/strategy, native configure errors and linked-input disagreement.

Engine capability limitations or build errors cannot be relabeled intentional `DISABLED`.
Registry-wide failures that prevent a Ready candidate produce the existing `ShadersOff`
incomplete result, not manufactured per-slot dispositions. Barrier/publication failures occur
after this immutable snapshot and cannot change it. No `PENDING`/null/default disposition exists.
The field always describes the requested slot, never the effective provider or worst ancestor.

Resolution is a memoized depth-first walk over the immutable descriptor graph:

1. a present, successfully compiled slot resolves to itself;
2. otherwise follow its parent;
3. return the first successful ancestor;
4. retain the requested-to-provider path for diagnostics;
5. if the chain ends, return the descriptor's terminal action: fixed, skip, passthrough, or
   unavailable; and
6. a cycle is a catalog construction error, never a pack error.

The result points to the provider's *single* immutable `CompiledProgramBinding`. It does not copy
the handle and overlay the child's alpha, blend, routing, scale, flips, mipmaps, attributes,
instance count, geometryInput, sources, uniformLayout or samplerLayout. This is how “entire configuration”
remains literal. A failed child's sampler or geometry evidence never overlays a successful ancestor.

After resolution, each catalog slot receives exactly one canonical evidence row:

- `SOURCED`: the requested slot's own shader compiled successfully; `sourcePresent=true`, `from`
  empty, `driverLog` empty.
- `CHAIN`: an effective ancestor shader is used; `from` is that effective provider,
  `sourcePresent` retains the requested slot's independent pre-build fact, and `driverLog` is empty.
  Missing inheritance has `ownBuild=NO_SOURCE`; present intentional disablement has
  `ownBuild=DISABLED`; failed own source has `ownBuild=FAILED`. The failure detail remains
  in normal diagnostics, but the fact of own failure is never masked in this required field.
- `ABSENT`: no shader is selected and no unmasked build failure remains—covering disabled/missing
  terminal actions plus virtual/fixed sentinels. `from` and `driverLog` are empty;
  `sourcePresent` remains the independent fact and may be true for an explicitly disabled source.
- `FAILED`: no shader is selected and the completed fallback walk encountered at least one failed
  slot. This status propagates to every requesting row whose walk contains that unmasked failure,
  including a missing or disabled child, so `sourcePresent` remains independent and may be false.
  `from` is empty and `driverLog` contains deterministic non-empty sanitized failure detail as
  defined in §4.12. No source text is included.

A failure is masked only for effective status when the same walk later finds a successful
ancestor: that requesting row is `CHAIN`, retaining its own disposition unchanged and failure
detail in normal diagnostics. The failed slot's own row is evaluated by its own complete walk,
not forced to match a descendant's disposition. If a walk ends without success, any failure takes
precedence over an otherwise `ABSENT` terminal action. Thus every completed walk produces exactly
one row without depending on which slot originally supplied the failure.

These invariants validate at construction. Rows are immutable and ordered by the registry catalog,
not maps or completion order. Phase 2's `GoldenProjectionAdapter` and Phase 7's runtime manifest
serialization consume the same `ProgramRegistryView.resolutions()` values without reconstructing
status from `resolve`, diagnostics, images, or handles (`[D-P4-16]`).

The exhaustive observable matrix (ancestor success means the first success on the completed
walk; failure-without-success means any failed ancestor and no successful provider) is:

| Requested slot condition | Ancestor walk | sourcePresent | ownBuild | status | from | driverLog |
|---|---|---|---|---|---|---|
| Virtual/fixed sentinel | Not applicable | false | NOT_APPLICABLE | ABSENT | empty | empty |
| Real missing | Successful ancestor | false | NO_SOURCE | CHAIN | provider | empty |
| Real missing | No success or failure | false | NO_SOURCE | ABSENT | empty | empty |
| Real missing | Failure without success | false | NO_SOURCE | FAILED | empty | ordered failure detail |
| Present intentionally disabled | Successful ancestor | true | DISABLED | CHAIN | provider | empty |
| Present intentionally disabled | No success or failure | true | DISABLED | ABSENT | empty | empty |
| Present intentionally disabled | Failure without success | true | DISABLED | FAILED | empty | ordered failure detail |
| Present enabled own success | Not visited | true | SUCCEEDED | SOURCED | empty | empty |
| Present enabled own failure | Successful ancestor | true | FAILED | CHAIN | provider | empty |
| Present enabled own failure | No successful ancestor | true | FAILED | FAILED | empty | ordered failure detail |

These are the only allowed combinations. Earlier ancestor failures followed by a success use
the successful-ancestor rows without changing the requested disposition. Terminal fixed, skip,
passthrough and unavailable actions all obey the no-success rows; none rewrites a real slot as
NOT_APPLICABLE. Candidate, detached inspection and published views carry identical rows.
P2/P7 copy the exact case-sensitive enum token to required `programs.<n>.ownBuild` in
`run-manifest/4`; `capture-plan/4` is the coordinated plan receiver. No `/1`–`/3` compatibility
default or inference from `sourcePresent+CHAIN` is permitted. T3 accepts intentional disablement
under its other requirements and rejects actual own FAILED even beneath CHAIN. Registry
evidence alone does not certify T3: recorded compiler success is not live-driver, rendered
capture, timing, environment, resource or oracle-comparability evidence.

“Shadow never inherits” means the `shadow` root has no parent. It does not erase the explicit
`shadow_solid → shadow` and `shadow_cutout → shadow` edges in App A.1.

This algorithm adopts the reference's recursive/memoized shape
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/programs/ProgramFallbackResolver.java:39, "source = resolveNullable(fallback)"]`
only after the App A check recorded as `D-P4-4`.

### 4.7 Source planning, compilation, and cleanup

Planning is pure. Before availability, materialization, callback planning, compilation or GL
allocation, validate current-schema admission and evaluate exactly once
`request.configuration().evaluateProgramStates(request.profileSelection(),request.diagnostics())`.
The request rejects a null profile wrapper; it never normalizes null to empty. Exhaustively consume
P3's closed result: `Evaluated(states)` supplies the one immutable state snapshot for this build;
`InvalidState(failure)` returns `ShadersOff` with `INVALID_PROGRAM_STATE` (§4.12), no per-slot
projection and zero materialization/GL work. Do not retry with absent intent, infer a profile,
apply profile constraints to mutate the configuration, substitute a preview state, or reuse
P7's earlier evaluation as a side channel.

P3's exact semantics bind: absent selection disables nothing; unknown selected name warns and
disables nothing. Property evaluation still applies, with unknown switches/expression failures
warning and evaluating false rather than becoming evaluation failures. Qualified disables match
only their exact projected key; unqualified disables match every projected key with that name.
`finalEnabled = propertyEnabled && !profileDisabled`. Source-absent property keys do not become
executable entries. Preserve `states.explicitFlips()` including eligible virtual-pre entries;
virtual preludes never enter `states.programs()` or acquire a build/fallback provider.

Then:

1. select the exact `DimensionConfiguration`; disabled dimension returns a shaders-off plan;
2. select the Phase 3 `SourceKey`s for the program stem without merging base and override;
3. use the matching exact `ProgramKey` from that evaluation for §4.6 availability, including
   every backup ancestor; a present disabled slot is DISABLED without materialization/build,
   while a source-absent slot remains NO_SOURCE;
4. join the selected program's `ProgramRequirements.legacyGeometry()` to its GEOMETRY root:
   a recognized pair chooses `GeometrySourceRequest.PreserveNative(config)`; otherwise use
   `GeometrySourceRequest.None`. Every VERTEX/FRAGMENT root uses `None`;
5. call the containing configuration's `sources().materializer().materialize(SourceKey root,
   MacroContribution contribution, GeometrySourceRequest geometry)` once per
   available source stage. There is no `OptionState` argument: the catalog-bound materializer
   retains this build's finalized options and macro snapshot, including companion options;
6. retain each `MaterializedSource`, exact final source map, language, geometry form, diagnostics,
   materialization fingerprint and complete `DeclaredUniformCatalog`; require the catalog's
   materialization fingerprint to equal its containing source fingerprint;
7. merge the stage catalogs into the immutable `ProgramUniformLayout` and derive the ordered
   sampler projection in the same pass; validate the projection through `samplerPolicy` for every
   provider-permitted band before GL, retaining immutable `ProgramSamplerLayout`;
8. adapt Phase 3's evaluated per-program state into `ProgramStateBundle` losslessly, preserving
   `EvaluatedProgramState.scale()` as-is; empty means no viewport override and is not normalized
   to an identity `ViewportScale`; and
9. look up Phase 3's sparse resource row using the selected source program's exact
   `ProgramKey.dimension()` and `programName()` in `ProgramRequirementKey`, after effective
   dimension selection (base when the world entry is absent; never base/override merging).
   A present row supplies routing and the exact `mipmappedAfterPass()`/`vertices()` projections
   for capability validation. An absent row uses the owner's empty mipmap/attribute baseline,
   without dereference, synthetic insertion, fingerprint mutation or treating source as absent.

Consumers never read a source from `SourceCatalog` and parse it themselves. An unavailable
materialization makes that program `Failed` and eligible for fallback.

The uniform-layout merge is pure and precedes every GL call. It visits materialized stages in
`VERTEX`, `GEOMETRY`, `FRAGMENT`, `COMPUTE` order and declarations in each catalog's published
token order. Repeated exact names with structurally equal `DeclaredGlslType` values merge into one
`ProgramUniformDeclaration` while retaining every attributed site and its catalog/materialization
fingerprint. The same exact name with unequal structural types is a source-attributed
`UNIFORM_TYPE_CONFLICT`: the program becomes `Failed`, no shader/program object is created, and
the ordinary backup chain applies. Case is significant; different names remain different.

The layout fingerprint hashes a schema tag plus the canonical exact-name order, Phase 3
structural type values, ordered sites, and their materialization fingerprints. It contains no GL
handle, location, post-link activity result, generation, or object identity. Equal merged inputs
produce an equal fingerprint. A driver may optimize any declared name out after link; the
declaration remains in the layout and a later Phase 1 `UniformLocation.isAbsent()` is
authoritative for upload activity.

The policy comes from Phase5's pure `FixedSamplerPolicies.appB3()` before an estate or GL object
exists. Phase4 owns `FixedSamplerLayoutPolicy`, not the map. `fingerprint()` is non-null/stable
and identifies the same schema/table used by Phase5's `FixedSamplerPolicies.resolver()`.
The complete registry planning pass precedes allocation: null policy, changing fingerprint,
thrown callback or malformed output yields typed `RegistryFailureKind.INVALID_SAMPLER_POLICY`
and no GL calls, never silently skipped validation. Phase4 retains derived immutable metadata
and fingerprints only, not the callback or request object.

For each provider call `validate(effectiveStage, band, declarations)` in canonical band order.
Both gbuffers bands have identical mapping. Union all issues and unit conflicts without losing
witnesses, canonically coalescing repeated identical conflict witnesses across bands.
Any conflict produces candidate-local `SAMPLER_LAYOUT` failure with
`SAMPLER_UNIT_TYPE_CONFLICT`; unsupported names/domains/shapes produce that stage with
`SAMPLER_LAYOUT_UNSUPPORTED`. If both occur, retain all evidence in `ConflictingTypes` and use
the conflict diagnostic as primary. No shader/program for that provider is created. Successful
providers have `Valid` and all permitted bands validated; ordinary backup chooses another valid
provider. Phase5 still defensively checks corrupted/unverified layouts before texture mutation.
**D-P4-36 — fixed-unit initialization plan.** For each Valid shader layout, call
`initializationAssignments(layout)` during this same complete pre-GL planning pass.
P5 derives the list through its sole resolver over every validated band; all bands must
agree on exact-name/unit assignments. Output is immutable, sorted by exact name's UTF-8 bytes,
contains every distinct declared sampler exactly once (including subsequently optimized-out
names), uses units 0–15, and contains no extra names. Compatible aliases may share a unit.
P4 validates shape/completeness/order and policy identity, not a copied map. Null, malformed,
inconsistent or throwing output is INVALID_SAMPLER_POLICY before any registry allocation.
Retain the frozen list with the private compiler plan, not the callback; empty layouts use
an empty list. Existing policy/layout identities already include declarations and table identity.
No production compiler knowingly links conflicting fixed-unit types. Direct samplers retain
sample kind, dimension, arrayed, shadow and multisample; aggregate samplers are retained with
UNSUPPORTED_SHAPE (the UnsupportedShape disposition), never dropped or scalar-coerced.

The sole Phase5 policy preserves exact case-sensitive names and the complete AppB3 domain, with
no synthesized colortex8–15 mapping or free-unit scan. Conditional `shadow` is5 only when the
effective provider's complete layout contains a direct sampler-compatible `watershadow`,
otherwise4, never from shadow-buffer count. Virtuals have `VirtualNotApplicable` and no bindings;
compute/unwired domains remain explicitly unsupported, not silently composite.

The new sampler/policy digests use SHA-256 as a design choice, with distinct
`ProgramSamplerLayout/v1` and `FixedSamplerPolicy/v1` domain/schema tags and Phase3 §4.10's
length-prefixed scalar/string/list framing
(`docs/phase3/v1/PHASE_3_DOC.md:3233-3249`). Hash provider identity, effective stage, permitted
bands in enum declaration order, full ordered declarations/sites/materialization fingerprints,
policy fingerprint, and the validation variant/full canonical payload. FixedFunctionEmpty and
VirtualNotApplicable have distinct tags and include policy identity. Requested child identity is
selection identity, not a change to its provider's layout digest. The uniform fingerprint and
`ProgramUniformCacheKey` remain unchanged. Optimized-out declarations remain in both layouts.

GL compilation is render-thread-only and uses this state machine:

```text
PLANNED (complete uniform merge + all-band sampler policy validation before any GL)
  → create program
  → for each available vsh/gsh/fsh in deterministic VERTEX, GEOMETRY, FRAGMENT order:
      create shader → compile
      failure: record + delete all created shaders/program → FAILED
      success: attach
  → bind each DECLARED extended attribute at 10/11/12
  → for an admitted native legacy strategy only: drain preceding build errors,
      configureLegacyGeometry(TRIANGLES, TRIANGLE_STRIP, exact positive maxVerticesOut),
      drain immediately; any rejection/error aborts and cleans this candidate before link
  → link
  → read cached ShaderService.linkedGeometryInput(program), compare with P3 expected input
      mismatch/rejection: record + delete all candidate objects → FAILED, never READY
  → initializeSamplerUnits(program, frozenAssignments)
      Completed: previous selection restored; continue
      Failed(...,true): record VALIDATE-stage SAMPLER_INITIALIZATION_FAILED, clean candidate → FAILED
      Failed(...,false): poisoned selection; clean independently, abort registry → ShadersOff
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

P1 D-P1-59 owns the temporary candidate selection, location/int upload/error windows and
exact previous-selection restoration. P4 calls no published barrier or P6 participant on this
unpublished candidate. No validate/READY follows failed initialization. A restored failure
is candidate-local VALIDATE-stage evidence with the explicit initialization diagnostic;
unrestored state or unexpected throw requires registry-wide safe/off containment, not another
provider activation. Runtime P6 still refreshes all sampler integers after P5 object binding.
The real-driver case must include simultaneously active sampler2D colortex0 and sampler1D gaux1
on legal distinct units 0/7; default-success recorder validation alone cannot prove that case.

No geometry source is required. A program with at least one source stage is attempted; the link
result decides whether that combination is executable. A program with no stages is `Missing`.

### 4.8 Dual-form geometry strategy

P3's current `GeometrySourceRequest` is exactly `None()` or
`PreserveNative(LegacyGeometryConfig expected)`. Successful `MaterializedSource.geometry()`
is exactly `None()`, `CoreLayout(GeometryLayout effective,
List<GeometryLayoutDeclaration> declarations)`, or `NativeLegacy(LegacyGeometryConfig config,
GeometryLayout effective,List<GeometryLayoutDeclaration> declarations)`.
P3's complete §5-incorporated §4.5 classification, language, attribution and failure rules bind:
`None` is ordinary processing, not geometry removal; only a non-GEOMETRY root returns form None.
A GEOMETRY root requires a complete core layout or the validated native pair. Conflicting,
unclassifiable or unavailable geometry fails the whole program, never a VSH/FSH-only retry.
The removed two-span translator, rewrite-site API and presumed core-proof token are not consumed.

For `CoreLayout`, require GL ≥3.2 and compile the materialized source unchanged. For
`NativeLegacy`, require the actual `GL_ARB_geometry_shader4` extension even on GL ≥3.2;
nonempty source layout declarations additionally require GL ≥3.2. Preserve every stage's exact
version/profile/extensions, built-ins, varying interfaces and same-load option/include/macro/
contribution processing. Source preservation is not a promise of successful GLSL linkage or OQ-18.

The native **API** triple is always `(TRIANGLES, TRIANGLE_STRIP, config.maxVertices())`.
This is the recorded behavioral topology from
`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:151`–`:152`; the author pair is
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:349`–`:356`. P3 separately retains each
explicit source-layout property and its attribution: source wins per property, otherwise API
triple supplies the default. Equal redeclarations remain; conflicting properties are unavailable.
Do not replace the API input/output/count with `effective` values. On a fresh unpublished program,
after attach and fixed attributes, configure P1 exactly once before first link with
`LegacyGeometryInputPrimitive.TRIANGLES`, `LegacyGeometryOutputPrimitive.TRIANGLE_STRIP`,
and the unchanged positive native count. Drain/handle preceding errors, configure, drain
immediately; rejection or any error aborts and deletes candidate objects before link.
No clamping, partial triple, core retry, old executable reuse or geometry-only disable is allowed.

**Linked agreement before publication (D-P4-28).** P3 `CoreLayout.effective().input()` and
`NativeLegacy.effective().input()` map exhaustively by matching names to the five non-NONE
`GeometryInputRequirement` values. No attached GEOMETRY source yields expected NONE.
After successful `link` and before `validate`/READY, call P1's
`ShaderService.linkedGeometryInput(ProgramHandle)` returning
`Optional<LinkedGeometryInputPrimitive>`. This is the owner-granted cached successful-link
projection, not a `LinkResult` accessor or raw GL query. Empty maps only to NONE; a present value
maps by the same five names. P1 link success requires authoritative metadata acquisition
(effective core query once inside GL3.2+ link, clean configured input on pre-3.2 native).
Require exact equality with expected input, including geometry presence. A mismatch is a
candidate `LINK` failure with stable `GEOMETRY_INPUT_MISMATCH`, expected/actual enum evidence,
source attribution and sanitized diagnostic; delete all candidate objects and resolve the whole
fallback binding. Accessor rejection/backend exception follows existing build failure cleanup.
Never overwrite the expected enum to hide disagreement or publish a drawable mismatched provider.
Only after agreement and successful validation freeze the actual enum in `ProgramStateBundle`.

P1 owns its selected-program tracker: use/fixed selection, nested predecessor reselection,
delete and unknown-state invalidation. P4 drains selection errors before participants or
`Activated`; failure takes existing safe/off containment. State snapshot/restore does not
restore a program; nested activation reselects the authenticated predecessor. No untracked
program change is permitted between activation and draw.

Fullscreen always uses P1's unchanged `fullscreenQuad()` operation: no geometry retains its
QUADS/strip policy; TRIANGLES uses the existing strip even when QUADS is available; other inputs
or unknown selection reject without native draw. P7 drains/contains failure before completion,
flips or post-draw work. Native submitted quads instead use the separately approved P7/P10
conditional adapter (§4.9), never silently submit incompatible QUADS. Source, compiler, adapter,
real-context conformance and fresh owner/receiver review gates are all required for support claims.

### 4.9 Draw routing, attributes, and state-bundle validation

Routing validation is deterministic:

1. Adapt each P3 DrawSlot.Attachment to DrawRoutingSlot.Attachment and each DrawSlot.None
   to DrawRoutingSlot.None, one-for-one; preserve leading/middle/trailing/repeated/all-none slots.
2. Explicit is nonempty and immutable; `N` is `[None]`, never empty or AllUsedBuffers.
3. Reject duplicate non-None logical attachments as backend/program validation, not a P3
   invariant breach. Never silently deduplicate or reject repeated None.
4. Check each Attachment against its P3 family/resource domain; None is not a resource.
5. Require full positional length, including None, ≤ maxDrawBuffers.
6. Require the number of non-None attachments ≤ maxColorAttachments; P5 packs physical
   attachments, so a logical index is not itself a physical-capability index.
7. Keep AllUsedBuffers symbolic for P5; only Attachment slots contribute writes/flips/minima.

A route invalid for one program fails that program and permits fallback. A pack-wide estate that
cannot fit the capability profile is a Phase 5 capability-gate result, not re-decided here.

Attribute binding occurs only for attributes declared by the direct Phase 3
`ProgramRequirements.vertices()` projection:

| Attribute | Location | Required `maxVertexAttribs` |
|---|---:|---:|
| `mc_Entity` | 10 | at least 11 |
| `mc_midTexCoord` | 11 | at least 12 |
| `at_tangent` | 12 | at least 13 |

Binding undeclared attributes is unnecessary and would reject a simple program on hardware that
cannot expose an unused high location. The numeric values never come from Pintonium. Its conflicting
bindings at `ProgramCreator.java:21`–`:25` are the negative test fixture.

`instanceCount` is always positive; absent means 1. It is retained for every slot, including
gbuffers/shadow, and fallback carries the effective provider's complete count/state.
The maintainer's 2026-09-07 decision adopts N adjacent submissions of the same prepared geometry,
IDs `0..N-1`, at v0.5. P7 owns policy through existing P10 draw adapters; P8 retains one shadow
traversal. The complete authentication, list-capture exclusion, restoration and failure contract
in §11.5 is incorporated here and by §5.1. No extension API or modern instanced draw is granted.

The separate conditional geometry conversion approval is adopted under D-P4-29, not inferred
from `countInstances`: v0.1 base/native submissions and v0.3 extended records must be supported
at their first affected `.gsh` claim. P7 reads only `Activated.binding().state().geometryInput()`
from its successful authenticated selection; FixedFunction clears the shader requirement to
NONE. Failed/stale/off/skipped outcomes authorize no shader draw. Force-shadow and nested
restoration use the actual selected provider, never the requested child or a retained view.
P7 maps the six categories exactly to P10's `VertexGeometryInput` for
`VertexInputPlan.expectedGeometryInput()`; P1's vertex service compares that expectation against
its active linked metadata at native binding, without source parse/per-draw raw query.
LIST_CAPTURE is explicit capture metadata, not a live-program query or draw authority.
P10 owns original-quad attribute computation followed by checked complete-record expansion
to `(0,1,3),(1,2,3)`, preserving winding and original vertex3 as last/provoking vertex for
both triangles; each quad produces two primitive IDs, not one emulated ID. Its 1.5× storage,
paired converted VBO/list lifetime, unsupported-input rejection and native restoration contract
remain P10's. P4 grants the enum, not an alternative conversion algorithm or GL handle.
Prepared-submission repetition stays adjacent and v0.5; no repeated traversal/build/upload.
This is approved local compatibility policy, not evidence of OptiFine diagonal or pixel parity.

Pintonium's per-buffer blend override is not adopted: App F.7 and Phase 3 §5 publish one
program-level `BlendSpec`, and Phase 1 has no indexed blend-state verb. The slot shape reserves no
unfillable field. A future Iris contract extension can add a distinct optional collection with
its own capability and owner.

### 4.10 The use-program state barrier

The barrier API is:

```java
public interface BarrierContextSource {
    FrameBarrierContexts beginFrame();
}

public interface FrameBarrierContexts {
    BarrierContext activation(StageStep step, boolean shadowPass);
    BarrierContext release();
}

public interface BarrierContext {
    boolean shadowPass();
    StageId stage();
    StageBand band();
}

public record ProgramUniformCacheKey(
    long registryGeneration,
    ProgramSlotId effectiveProvider,
    ProgramUniformLayoutFingerprint linkedLayout) {}

public interface BoundProgramActivityToken {
    boolean isCurrent();
}

public interface BoundProgramUniformAccess {
    ProgramUniformCacheKey cacheKey();
    UniformLocation locate(String exactName);
    BoundProgramActivityToken activityToken();
}

public interface ProgramBindingParticipant {
    BarrierParticipantResult afterBind(
        ResolvedProgramDescriptor binding,
        BarrierContext context,
        BoundProgramUniformAccess uniforms);
}

public sealed interface BarrierParticipantResult {
    record Continue() implements BarrierParticipantResult {}
    record Degraded(String diagnosticId, String disabledScope)
        implements BarrierParticipantResult {}
}

public sealed interface BarrierResult {
    record Activated(ResolvedProgramDescriptor binding,
                     List<BarrierParticipantResult.Degraded> degradations)
        implements BarrierResult {}
    record FixedFunction(List<ProgramSlotId> fallbackPath) implements BarrierResult {}
    record Skipped(ProgramSlotId requested) implements BarrierResult {}
    record ShadersOff(String diagnosticId) implements BarrierResult {}
    record FailedSafe(String diagnosticId) implements BarrierResult {}
    record StalePublication(long expectedGeneration, long currentGeneration)
        implements BarrierResult {}
}

public interface ProgramStateBarrier {
    ProgramSelectionResult select(ProgramSlotId requested, BarrierContext context);
    BarrierResult activate(UseProgramRequest request);
    BarrierResult releaseToFixedFunction(BarrierContext context);
}

public final class ProductionBarrierParticipants {
    ProductionBarrierParticipants(/* three position-named participants + credential */);
    // opaque product; no public/protected constructor, factory, subclass, or component access
}

public interface ProgramStateBarrierFactory {
    BarrierConstructionResult create(
        CompiledRegistryCandidate registry,
        ProductionBarrierParticipants participants);
}

public interface ProductionBarrierComposer {
    BarrierConstructionResult compose(
        CompiledRegistryCandidate registry,
        ProgramBindingParticipant samplers,
        ProgramBindingParticipant builtIns,
        ProgramBindingParticipant customs);
}

public sealed interface BarrierConstructionResult {
    record Ready(BarrierPublicationCandidate candidate) implements BarrierConstructionResult {}
    record Invalid(String diagnosticId) implements BarrierConstructionResult {}
}

public final class BarrierPublicationCandidate implements AutoCloseable {
    BarrierPublicationCandidate(/* private barrier + registry identity + provenance */);
    public void close(); // caller-owned before acceptance; idempotent
    // opaque, inactive, non-operational input; no public/protected constructor/factory/subclass
}
```

Phase 7's `com.schmaloogium.mod.core` composition root calls the public
`com.schmaloogium.engine.registry.ProductionBarrierComposer.compose` operation with the
compiler-issued candidate and Phase 6's three implementations in the signature's fixed
sampler/built-in/custom order. The facade delegates to the Phase-4-owned package-private production
assembler, which alone mints `ProductionBarrierParticipants`, and then to the package-private
factory implementation. It returns only `BarrierConstructionResult`, never the bundle or its
credential. Null inputs, a closed or non-compiler-issued registry product, and an internal missing
member return `Invalid` without GL work or retained participant references. Exactly one successful
production candidate may be composed per registry product; a repeated call returns `Invalid`.
Failure retains nothing. Success leaves the registry product and opaque barrier candidate
caller-owned until publication, and closing either unpublished product is idempotent. Neither
Phase 6 nor Phase 7 can construct, subclass, or synthesize the opaque bundle or compiler product.
The factory checks both private credentials and creates a private barrier that invokes the
positions in sampler/built-in/custom order.

The separate package-private bootstrap assembler may supply three Phase-4-owned `Continue`
participants only to bootstrap tests and bring-up; it requires the existing package-private
bootstrap capability and marks the result bootstrap-only. Phase 7 production wiring cannot name
or obtain that capability. Partial defaults are forbidden. A null registry/bundle, a bundle with
the wrong provenance, or an internal missing member returns `Invalid` without GL work or retained
references. Factory/backend exceptions are likewise diagnosed as `Invalid`, never thrown.

`CompiledRegistryCandidate` is minted only by the compiler around its private
`CompiledProgramRegistry` and compiler-origin credential. `BarrierPublicationCandidate` is the
factory's opaque, final proof that the private barrier was created from an authenticated
production bundle for that exact compiler product and registry identity. It exposes no barrier
operation or credential. A candidate is inactive and caller-owned until supplied with that same
compiler product in `RegistryPublication.Ready`; rejection leaves both caller-owned and
acceptance transfers both to the publisher. An unpublished barrier has acquired no GL handle,
lock, or participant-owned resource; `close()` marks it closed and drops its private barrier and
participant references without GL work. Close is idempotent before transfer. After successful
transfer, caller `close()` is an idempotent no-op and cannot affect the publication; the publisher
alone releases the accepted barrier during replacement/recovery. Participant activation failures
use the closed results below and never alter that lifecycle. Phase 11 does not install a fourth
participant: it supplies custom-expression evaluation to Phase 6, whose `customs` participant
performs that handoff.

The three positions are:

1. `SamplerRepointParticipant`;
2. `BuiltInUniformRefreshParticipant`; and
3. `CustomUniformRefreshParticipant` (fed by Phase 11 when available).

Selection and activation are distinct, normative operations:

1. Phase7 prepares an issued activation `BarrierContext` and calls `select(requested,context)`
   exactly once. The wrapper first checks its current publication identity/generation, then the
   current issued frame/context and legal request. Apply force-shadow BEFORE the sole fallback
   resolution; preserve the original requested slot separately from the forced root/provider.
   A valid shadow context can force a gbuffers hook request to shadow; stage/band membership is
   checked against the forced root and final provider, never the unforced hook.
   The selected descriptor's requested field preserves that original hook slot; fallbackPath
   describes the forced root-to-provider walk. The selector and descriptor are one published
   selection, not independently interchangeable metadata records.
2. `Selected` privately retains the already-resolved binding and exact context identity. Public
   accessors are immutable handle-free metadata, not a constructor credential. Fixed terminals
   return Selected with explicit FixedFunctionEmpty; absent indexed passes return Skipped.
   Virtual steps bypass select and use VirtualNotApplicable planning metadata. Invalid request/
   issuer/context and unavailable required selection return ShadersOff(diagnosticId), mutation-free;
   stale wrapper returns StalePublication. These outcomes authorize no draw or stale-state reuse.
3. Phase5 authenticates the selector and binds compatible textures before activation, through its
   shared typed sixteen-row operation. Phase7 never manually binds rows. Rejected/Degraded
   suppress that draw with main discardPass or shadow abortPass, no flips; BackendFailed takes
   containment. No alternative fallback provider is selected after this point.
4. `activate(UseProgramRequest(selection,context))` checks issuer, current generation/fingerprint,
   identical originating context, actual band/stage and private provider/layout membership.
   It uses only the retained private binding, never calls resolve. Invalid selection returns
   ShadersOff without token/GL/lock mutation; a stale wrapper returns StalePublication.
5. Only after authentication invalidate the previous BoundProgramActivityToken and close the
   previous P1 AlphaBlendOverride. A failed close stops activation; never acquire over it.
   Bind ShaderService.use or useFixedFunction and check backend failure.
6. For a shader call StateService.lockAlphaBlend with the effective provider's optionals,
   retaining its opaque lease. Sample StateService.effectiveBlend through P7's existing effective
   blend notification path before the built-in participant. Mint callback access/activity and
   invoke sampler, built-in, custom in that order with the same selection/context/key.
   Fixed function acquires no lease and skips participants.
7. Publish Activated only after lock acquisition, notifications and participants succeed.
   Any later failure invalidates the new activity token, closes the newly acquired lease,
   and enters existing safe/off recovery; no texture bind alone authorizes drawing.

`ProgramBindingSelections.validateSelection(selection,context)` is a pure private-credential
check with no GL and no new publisher argument. First failure wins: null/unissued selector or
foreign issuer → INVALID_ISSUER; retired/replaced/off/closed originating registry or unequal
generation/fingerprint → STALE_GENERATION; nonidentical context, retired frame, wrong context kind
or source → STALE_CONTEXT; wrong actual stage/band or shadow relation → WRONG_STAGE_BAND;
provider identity, sources/state/uniform/sampler-layout or policy membership mismatch →
PROVIDER_LAYOUT_MISMATCH; otherwise Valid. Public-record equality alone never authenticates.
Phase5 maps those respectively to INVALID_PROGRAM_SELECTION, STALE_REGISTRY_GENERATION,
PROGRAM_SELECTION_MISMATCH, PROGRAM_SELECTION_MISMATCH, SAMPLER_LAYOUT_MISMATCH.

Selection validity ends on originating frame/context invalidation or registry replacement/off/
close, not merely on the immediately following or a later same-frame activation. Logical nested
scopes retain their original selection/context; suspension closes physical bindings, and pop
reacquires physical snapshot/lease and reactivates that same selector, never selecting its provider
as a new requested slot. An old selector never revives by equal layout hash. No reentrant
publication transition is permitted between Phase5 preflight/binding and immediate activation.
After Bound, Phase7 finally closes only the binding snapshot; before Bound it closes any acquired
lease itself, including thrown calls. Retirement may delay deletion for leases, never stale drawing.

Participants run on every successful `activate`, even when the same handle remains active.
World state, per-draw values, texture sides, and custom expressions may have changed. An equality
fast path may skip only the actual `use` call if Phase 6 explicitly proves that doing so preserves
its refresh contract; v0.1 does not take that optimization.

`ProgramBindingParticipant.afterBind` receives the handle-free descriptor and bound-uniform
access only after the private resolved binding is a shader program; the barrier enforces that
condition before projection and dispatch, so participants never receive an operational handle or
invent fixed-function behavior. `cacheKey()` is stable for every activation of the same effective
provider/layout in one published generation; fallback children therefore share the provider's
key. Locations returned by `locate` remain cacheable only until generation inequality.

`BoundProgramUniformAccess` is render-thread and callback scoped. During each participant call,
`locate(exactName)` accepts only a non-empty name present in the effective
`ProgramUniformLayout`, delegates internally to
`UniformService.locate(privateProgramHandle, exactName)`, and returns the Phase 1 present/absent
opaque location. A blank/undeclared name or use outside the currently executing callback fails
before any GL lookup; the barrier converts a participant misuse/exception to that participant's
`Degraded` result, closes its callback window, and continues later positions. The access object,
private handle, and lookup operation may not be retained. Returned `UniformLocation`s and the
`ProgramUniformCacheKey` may be retained under the generation rule.

Only the opaque `BoundProgramActivityToken` may be retained to authorize Phase 6's already-cached
immediate signal uploads between activations. Its `isCurrent()` is a pure thread-safe epoch
comparison with no GL query or side effect. The token becomes current after the successful bind
and before the first participant, remains current through the enclosing draw, and is invalidated
before every later activation (including the same effective program), fixed-function release,
failed-safe/off transition, ready/off publication replacement, or registry teardown. Invalidation
precedes program unbind/rebind and handle deletion. A stale token never becomes current again and
confers no lookup, bind, upload, delete, or lifecycle operation.

These results never throw. `Continue` proceeds; `Degraded` disables only its named participant-owned
uniform/expression scope, records the diagnostic, and processing continues. `Activated` guarantees
the program and provider locks are active and the ordered participant sequence completed, with
every isolated degradation recorded in `degradations`; the caller draws only after the matching
Phase5 Bound outcome (or the explicit no-binding vanilla terminal) and retains diagnostics.
`FixedFunction` guarantees locks restored and fixed function bound; the caller continues its
declared terminal. Selection Skipped is mutation-free and requires discard/explicit release,
not a claim that old state was restored. `BarrierResult.Skipped` remains a no-draw closed result,
not the normal selected-activation path. ShadersOff from invalid selection is pre-mutation;
operational failure restores fixed-function/vanilla-safe state and requires off publication.
Failure to prove restoration returns `FailedSafe`, emits its diagnostic, forbids shader drawing,
and requires immediate shaders-off/vanilla-path recovery. `StalePublication` is
produced only by a superseded published view before delegation; it performs no GL work, forbids the
draw, and requires the caller to reacquire the atomic publication snapshot.

`BarrierContextSource` is Phase-4-owned and render-thread-only. Phase 7 calls `beginFrame()` exactly
once at frame entry; it monotonically advances a private epoch, permanently retires the prior
`FrameBarrierContexts`, and returns the sole issuer for that frame. `activation(step, shadowPass)`
accepts only a `StageStep` from the current published `StageRegistry`; it requires
`shadowPass == (step.stage() == SHADOW && step.band() == SHADOW)` and issues a context view
carrying the source identity, current epoch, context kind, stage, and band. `release()` issues the
frame's canonical release-kind context and copies the most recently issued activation stage/band,
or uses `FINAL`/`SCREEN` when none was issued; its `shadowPass()` is always `false`, including when
the copied pair is `SHADOW`/`SHADOW`. These are ordinary public interfaces, so callers may
implement them, but only Phase 4 can mint an implementation accepted by a barrier or publisher;
no public operation accepts a caller-supplied frame number, source identity, epoch, or kind.

Selection accepts only an activation-kind context from its current source/epoch and checks stage/
band after force-shadow. Activation authenticates the retained selector against that identical
context rather than a new slot. Release/publication accept only release-kind context from the
same current source/epoch. Wrong source, retired epoch, kind, step, provider/stage/band or shadow
relation rejects before token invalidation, GL work, lock change or publication release.

An absent alpha/blend override means “do not lock that aspect.” Explicit `OFF` means lock it
disabled. A transition restores exactly the snapshot taken before the prior lock; it never
snapshots the prior override as the new underlying state. `releaseToFixedFunction` restores any
lock before binding fixed function.
The actual lock is P1 D-P1-57, not snapshot-plus-immediate-setter emulation. Ordinary vanilla
alpha/blend mutations of held aspects are suppressed before cache/native mutation, not queued;
unheld aspects remain ordinary. P1 privately bypasses interception through GlStateManager for
acquire/restore and owns exact snapshots/cache coherence. P7 registers the P1 HEAD hooks and
forwards effective-state notifications; it owns no lock policy. Suspend/pop closes/reacquires
leases and participants for the retained selector, never stacks leases or snapshots overrides.
Release, off, replacement, teardown and failed activation invalidate activity before closing;
failed restore forbids further shader drawing and forces safe/off containment, never Activated.
Successful acquisition is effective before built-ins observe blendFunc, and the activity token
remains current only while this successful activation and its lease remain live.

The order is a contract exposed to Phases 6–8. It adopts the shape validated by
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/Program.java:30-34]`
after checking RESEARCH §4.2 (`D-P4-5`). Pintonium's compute memory barrier and image refresh are
not invoked at v0.1; their slots remain G8/S2.

### 4.11 Publication, reload generation, and cache keys

`RegistryFingerprint` hashes:

- Phase 3 schema version and configuration fingerprint;
- explicit profile-selection discriminator `Absent` or `Present` plus exact `ProfileName.value()`
  bytes, and the evaluated state snapshot from that exact configuration/selection pair:
  ascending `ProgramKey` entries with typed alpha/blend/scale optional values and all three
  propertyEnabled/profileDisabled/finalEnabled booleans, followed by ordered explicitFlips;
  use canonical length-prefixed framing, optional/variant tags and enum names, never inferred
  profile labels, object identity or diagnostics;
- active dimension identity/mode;
- materialization fingerprints for every attempted stage;
- canonical merged `ProgramUniformLayout` fingerprints for every shader binding;
- canonical `ProgramSamplerLayout` fingerprints for every provider/fixed/virtual planning entry,
  typed failed-layout evidence and the registry-wide fixed-policy fingerprint, even if empty;
- the Phase 6 macro contribution;
- canonical geometry request/form discriminator, native API triple, source effective layout
  and ordered declarations/sites, and the agreed actual provider `geometryInput` enum;
- capability fields that affect validation/build;
- catalog-ordered `(slot,status,from,sourcePresent,ownBuild)` resolution evidence, encoding the
  required disposition by exact enum name, not ordinal; and
- the registry schema version.

It does not hash handles, locations, post-link activity, driver logs, object identity, generation,
timestamps, or source text. `ProgramUniformCacheKey` is the separate activation/upload-cache
identity `(published generation, effective provider, linked layout fingerprint)`. Generation
inequality invalidates every cached location; within one generation, requested fallback children
that resolve to the same effective provider/layout intentionally share the key.
The registry schema/domain advances for the new required bundle component; no old registry
or selector is reusable by adding NONE. Geometry payloads use §4.7's canonical length-prefixed
framing and enum names, not ordinal/raw GL numbers. Current P3 fingerprints transitively
cover exact source/language/map/catalog and native route; native and core routes cannot alias.
The linked input is executable interface metadata, not excluded uniform activity. Changed
input, API count, declaration, source, extension capability or route invalidates registry reuse.
P4 consumes P3's exact current `MaterializedSource-v23` payload identity (§4.10), including schema,
root, configuration fingerprint, contribution, request, language, form, text, map, canonical
uniform payload and diagnostics. It checks the shared source/catalog digest rather than hashing
a partial replacement payload or equating same-text native/core results.

D-P4-43 supersedes D-P4-33's historical `RegistryFingerprint/positional-route-v2` with
`RegistryFingerprint/profile-selection-v3`, distinct from every earlier registry domain. Within it,
§4.7's canonical framing covers all preceding payload fields, including selection, evaluated state
and required ownBuild. Equal configurations with different explicit intent cannot alias even when
their final availability happens to agree; differing availability is retained in detached rows.
Old cached candidates, fingerprints and selectors are invalid; do not append a default field
to upgrade them. This owner-data change does not bump P3's schema or its inspection
`projectionVersion=1`, nor the uniform/sampler layout domains. P3's separate current-schema
cutover changes the consumed configuration/materialization identity independently.
D-P4-33 additionally hashes each provider's routing discriminator, domain and Explicit length
followed by each ordered tag `None` or `Attachment` plus buffer domain/index using the existing
length-prefixed framing. Holes and their positions are identity-bearing; `[None]`, `[None,None]`,
AllUsedBuffers and routes with shifted attachments never alias. Prior own-build fields remain.

Publication is:

1. build a complete candidate;
2. if caller accepts it, complete every publisher-side candidate, provenance, composition,
   identity, state, ownership, render-thread, and context validation; reject with the old
   generation/current publication unchanged and usable on any failure;
3. only after all validation succeeds, invalidate the old activity token and restore/release the
   old barrier when present, otherwise skip it with zero old-barrier GL work;
4. atomically replace `PublishedRegistry`;
5. increment generation once;
6. close the old registry on the render thread; and
7. notify no cache directly—consumers poll equality.

The publisher also accepts `ShadersOff`, represented by an empty registry and a new generation.
Its barrier view is empty too. This makes “off” observable to every cache. `RecoveredOff` has the
same empty registry/view shape. The low-level publisher retains the old publication on a
pre-release rejection, but the coordinated Phase7 transaction never resumes it after a failed
rebuild: frame admission stays closed and Phase7 compensates the composition shaders-off.
Phase7 publishes Phase4 Ready, adopts the actual generation in its new Phase6 runtime, accepts
Phase5's actual estate, builds Phase13 with that registry/estate/resource epoch, and only then
installs one coherent ActivePipeline after the Phase9 gate. A texture failure after acceptance
retires accepted owners and publishes Phase5 off then Phase4 ShadersOff using issued release
context; Accepted/Rejected/RecoveredOff are handled distinctly. RecoveredOff is a result, never a
RegistryPublication input. No accepted resource is caller-closed or old registry revived.

`publish(publication, releaseContext)` requires a non-null current release-kind context issued by
the old publication's `BarrierContextSource` for both ready and off replacement, including
replacement outside ordinary activation. The publisher validates source identity, current private
frame epoch, and release kind. If the old publication has a barrier, it then passes the context
unchanged to that barrier's `releaseToFixedFunction`. If the authenticated old publication is
accepted shaders-off or `RecoveredOff`, its barrier is absent: the publisher skips
`releaseToFixedFunction` and performs zero old-barrier GL work. A missing, foreign, retired, or
activation-kind context is a pre-release validation failure in either branch.

`RegistryPublication.Ready` transfers its compiler-issued registry product and factory-issued
barrier candidate to the publisher
only when `Accepted` is returned; `ShadersOff` transfers no GL object. Before release begins, the
publisher independently validates the registry product's compiler-origin credential and open,
unpublished state, then validates the barrier candidate's Phase-4 provenance, authenticated
production composition, exact product and registry identity, open private-barrier state, and
nonpublication, as well as render-thread ownership and context. It separately rejects a
bootstrap-marked candidate. A public `CompiledProgramRegistry` implementation has no conversion to
`CompiledRegistryCandidate`, even when paired with genuine participants. An
arbitrary `ProgramStateBarrier` implementation has no conversion to
`BarrierPublicationCandidate` and cannot enter this path. A validation failure returns `Rejected`,
leaves generation/current and the provably usable old publication unchanged, and leaves the
candidate with the caller for idempotent close.

When an old barrier exists and its release begins, only `FixedFunction` permits the requested
atomic replacement. When no old barrier exists, successful authentication and pre-release
candidate validation permit that replacement directly; barrier absence alone cannot produce
`RecoveredOff`. Acceptance in either branch installs ready or empty state, increments once,
transfers ready-candidate ownership, and idempotently closes any old registry (none exists for an
old off publication). `ShadersOff`, `FailedSafe`, an exception, or any protocol-invalid result
after old-barrier release begins instead installs an empty publication and increments once as
`RecoveredOff`; the old barrier and registry are quarantined from all shader-path use, the old
registry is idempotently closed, and the ready candidate remains caller-owned for idempotent close.
The caller must report the returned publication failure and immediately continue through the vanilla
recovery path; `FailedSafe` never claims that GL restoration succeeded. Republish of a transferred
object is rejected. Old-registry close failure is diagnosed after replacement/recovery and cannot
roll back or increment again.

Generation adoption is structurally cross-checked against
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/compat/sodium/impl/shader_overrides/IrisChunkProgramOverrides.java:136-139]`,
where inequality deletes cached shaders. No Pintonium dimension cache or renderer coupling is
inherited.

### 4.12 Diagnostics

`ProgramBuildFailure` is candidate-build evidence produced before the detached resolution snapshot
is created. It contains:

- requested slot and source stem;
- projection-eligible failure stage (`MATERIALIZE`, `CAPABILITY`, `SAMPLER_LAYOUT`, `COMPILE`,
  `ATTRIBUTE_BIND`, `LINK`, `VALIDATE`, `UNEXPECTED_BACKEND`);
- shader stage when applicable;
- sanitized driver log;
- source-map diagnostic IDs, not pack source text;
- fallback path and final disposition; and
- a stable diagnostic ID.
- `Optional<ProgramSamplerLayout> samplerLayout()`: present and mandatory for SAMPLER_LAYOUT
  with full typed conflicting/unsupported evidence; absent for failures preceding that projection.

Geometry failures use the retained P3 map, never a second parse: numeric file coordinates resolve
through `sourceForFileNumber`, emitted-string offsets through final token mappings. Unknown or
unparseable coordinates retain root attribution plus sanitized log, not guessed token precision.
`LINK/GEOMETRY_INPUT_MISMATCH` retains expected/actual enum names in sanitized failure detail;
no GLSL, token range text or expansion text reaches resolution rows.

Every `ProgramBuildFailure` also supplies `projectionDetail()`: a deterministic, sanitized,
non-empty single-line serialization of failure stage, stable diagnostic ID, and sanitized driver
log when one exists. For non-driver failures such as MATERIALIZE, CAPABILITY or SAMPLER_LAYOUT,
stage and diagnostic ID provide non-empty detail; SAMPLER_LAYOUT additionally serializes its
canonical validation tags, fixed units, exact names/full types and issue codes without source text.
For a `FAILED` walk with multiple
failures, `driverLog` joins their `projectionDetail()` values in fallback-path order with a fixed
delimiter. Sanitization removes source text and line breaks; blank stage/ID/log combinations are
rejected. The projection retains the established `driverLog` field name as wire compatibility,
but its value is this general failure detail rather than a promise that every failure came from a
driver.

`BARRIER` is not a `ProgramBuildFailure` stage and is never projection-eligible. Barrier
construction `Invalid` and activation/publication failures occur after candidate snapshot creation
and are reported only through their closed barrier/publication results and normal diagnostic
channels. They cannot create or mutate a `ProgramResolutionProjection` row. `UNEXPECTED_BACKEND`
is projection-eligible only when raised during candidate materialization or GL build; an unexpected
backend failure during later barrier/publication work follows that work's closed result protocol.

`RegistryBuildFailure` is a closed, sanitized aggregate:

```java
public record RegistryBuildFailure(
    RegistryFailureKind kind,
    List<ProgramBuildFailure> programFailures,
    String diagnosticId,
    String userMessage) {}

public enum RegistryFailureKind {
    NO_REQUIRED_TERMINAL, CAPABILITY, INVALID_PROGRAM_STATE, INVALID_SAMPLER_POLICY,
    UNSAFE_STATE, UNEXPECTED_BACKEND
}
```

Its program list is immutable, deterministically ordered by requested slot, and may be empty for
pack-wide capability/unsafe-state failures. Each member retains its own fallback disposition;
the aggregate means no complete registry is publishable and the final pack-wide disposition is
`ShadersOff`. Driver logs and source text never enter `userMessage`.
`INVALID_PROGRAM_STATE` is the registry-wide reduction of P3 `InvalidState(failure)`: empty
programFailures, stable diagnostic ID identifying the P3 failure reason, sanitized userMessage,
no Ready candidate or manufactured resolution rows. It is not a per-program FAILED/DISABLED.
Warnings for unknown profile names or false expressions remain P3 `Evaluated`, not this failure.

Publication protocol failures use a separate closed, sanitized value:

```java
public record PublicationFailure(
    PublicationFailureKind kind,
    String diagnosticId,
    String userMessage) {}

public enum PublicationFailureKind {
    NULL_PUBLICATION, WRONG_RENDER_THREAD,
    CONTEXT_MISSING, CONTEXT_SOURCE, CONTEXT_EPOCH, CONTEXT_KIND,
    COMPILER_ORIGIN, REGISTRY_STATE,
    BARRIER_PROVENANCE, COMPOSITION_PROVENANCE,
    PRODUCT_IDENTITY, REGISTRY_IDENTITY, BARRIER_STATE,
    OWNERSHIP, BOOTSTRAP_CANDIDATE,
    RELEASE_SHADERS_OFF, RELEASE_FAILED_SAFE, RELEASE_PROTOCOL_INVALID,
    RELEASE_EXCEPTION, POST_RELEASE_UNEXPECTED_BACKEND
}
```

The kinds through `BOOTSTRAP_CANDIDATE` are exhaustive pre-release rejection causes and therefore
produce `Rejected`; the remaining kinds are exhaustive failures after old-barrier release begins
and therefore produce `RecoveredOff`. `diagnosticId` is stable and non-empty, `userMessage` is
sanitized and contains neither source text nor driver logs, and neither field changes candidate
build disposition or any resolution projection. Callers report every cause; after `Rejected` they
retain and may close both candidates; the coordinated rebuild then compensates off rather than
resuming the unchanged low-level publication. After
`RecoveredOff` they close both caller-owned candidates and immediately take the vanilla recovery
path.

The detailed log goes to `schmaloogium.compile`. A concise error goes through
`DiagnosticReporter` to the shader GUI/user channel. Debug saved sources remain Phase 3's opt-in
local artifact and never enter this diagnostic.

## 5. Cross-phase interfaces

### 5.1 Exposed interfaces and data contracts

| Exposed contract | Exact content | Consumer(s) |
|---|---|---|
| `StageRegistry`, `StageId`, `StageBand`, `StageStep`, `PassPopulation`, `PassIndex` | Exact §2.2 signatures, including `SparseArray(int highestLegalIndex,int highestPopulatedIndex,Optional<ProgramSlotId> virtualPrelude)`, and complete §4.1 construction/lookup/traversal rules are binding. One deferred/composite occurrence; optional exact named prelude first, then populated indices ascending through 99; legal absence returns empty, wrong keys/kinds reject | Phases 5, 7, 8; G8/S1/S2 |
| `PassDescriptor`, `PassResourceAccess`, `ComputeDispatchSlot` | Exact §2.2 component order and §4.1 membership invariants bind. Virtual pre is VIRTUAL_FLIP_CONTROL with empty index/source/fallback/compute/read/write/mipmap sets, exact explicit flips and no resolved program, selection or draw. P7 forwards the exact contained descriptor to P5 unchanged; no downstream synthesis. Indexed raster members retain present indices; dormant primary + a…z companions remain outside gbuffers | Phases 5, 7; G8/S2 |
| `ProgramSlotId`, `ProgramSlotDescriptor`, `ProgramStateBundle`, `GeometryInputRequirement` | exact §2.2 component types/order; required `GeometryInputRequirement geometryInput` appended after `Optional<LegacyGeometryConfig> legacyGeometry`. Closed enum NONE, POINTS, LINES, LINES_ADJACENCY, TRIANGLES, TRIANGLES_ADJACENCY; incorporates §4.8 linked agreement and §4.9 effective-provider/live-activation contract. NONE only absent linked geometry/fixed sentinel, never failed/unknown | Phases 5, 6, 7, 8; Phase 10 through Phase 7; Phase 2 detached inspection |
| `ProgramRegistryCompiler.compile(RegistryBuildRequest)` | Exact §2.2 request order: PackConfiguration configuration, Optional<ProfileName> profileSelection, DimensionKey dimension, MacroContribution macroContribution, FixedSamplerLayoutPolicy samplerPolicy, GLCapabilityProfile capabilities, GLDevice device, DiagnosticReporter diagnostics. Non-null selection wrapper; §4.7 exact-pair P3 evaluation and closed InvalidState handling precede availability/materialization/all GL. Synchronous render-thread build returns Ready(caller-owned candidate) or ShadersOff(failure). Planning validates all callback outputs before any GL; retains only immutable derived values, never callback/request. P7 forwards accepted frozen explicit selection even for equal-option profiles, never inferred labels or preview state; P2 explicitly supplies Optional.empty() under §5.6's unchanged inspection contract | Phases 2, 7, 12 |
| `CompiledRegistryCandidate.view()` | while the compiler-issued opaque candidate is open and caller-owned, returns an immutable detached metadata snapshot implementing `ProgramRegistryView`. Phases 5 and 7 may derive and validate candidate-dependent buffer/pass and composition state before publication without acquiring ownership. A retained snapshot remains safe and unchanged after candidate close, pre-release rejection, `RecoveredOff`, or accepted ownership transfer; it retains no candidate, private registry, or GL handle and does not observe publication. It has no publication generation, `close`, `ProgramHandle`, private-registry accessor, or compiler-origin credential; obtaining it neither publishes nor transfers the candidate, and the candidate remains the sole authenticated ownership/provenance product accepted by composition and publication | Phases 5, 7 |
| `PublishedRegistry.registry`, `ProgramRegistryView`, `ResolvedProgramDescriptor`, `ProgramUniformLayout` | Incorporates exact §2.2 signatures: descriptor(requested,effective,state,uniformLayout,samplerLayout,sources,fallbackPath), all with their declared types/order. resolve is detached handle-free inspection, not selection authority; every field except requested/path is the complete provider's. ProgramRegistryView.samplerPolicyFingerprint() retains identity even for empty registries. Fixed descriptors use empty uniform and FixedFunctionEmpty sampler layouts | Phases 5, 6, 7, 8, 13 |
| `ProgramRegistryView.resolutions()` / `ProgramResolutionProjection` / `ProgramResolutionStatus` / `ProgramOwnBuildDisposition` | Incorporates exact §2.2 field order and §4.6 total classification and exhaustive matrix. Complete immutable catalog-ordered rows preserve independent sourcePresent and required requested-slot ownBuild; status/from/driverLog retain their effective-walk meanings. Successful fallback masks effective FAILED to CHAIN, never ownBuild=FAILED. Candidate and accepted runtime views expose identical detached evidence; barrier/publication failures cannot mutate it. §4.11 defines its P4-only identity cutover; §5.6 defines same-request enrichment | Phases 2, 7 |
| `PublishedRegistry.barrier`, `PublishedProgramStateBarrier`, `ProgramStateBarrier`, contexts, `UseProgramRequest`, `BarrierResult`, `ProgramUniformCacheKey` | Incorporates exact §2.2 and §4.10 signatures/results and credential/lifetime rules. Both barriers expose select(ProgramSlotId requested,BarrierContext context) → ProgramSelectionResult and activate(UseProgramRequest(ProgramBindingSelection selection,BarrierContext context)) → BarrierResult; releaseToFixedFunction unchanged. Select authenticates then forces shadow before sole resolution. After Phase5 object binds, activate authenticates the identical retained selection/context, invalidates predecessor activity, closes the predecessor lease, binds the retained program without resolution, acquires the effective provider lease, publishes/samples effective blend, then calls sampler/built-in/custom participants before Activated. Fixed function acquires no new lease and dispatches no participants. Existing failure containment, issued frame/release contexts and mutation-free stale wrapper checks remain mandatory. Cache key stays exactly generation + effective provider + uniform-layout fingerprint | Phases 5, 6, 7, 8, 13 |
| `ProductionBarrierComposer.compose`, `ProgramStateBarrierFactory`, opaque `ProductionBarrierParticipants`, `BarrierConstructionResult`, `ProgramBindingParticipant`, `BoundProgramUniformAccess`, `BoundProgramActivityToken`, `BarrierParticipantResult` | Phase 7 calls the public Phase-4 facade with one compiler product and exactly the Phase-6 sampler/built-in/custom implementations; package-private assembly mints the credentialed bundle and factory candidate without exposing either credential; one success per registry product, while null/closed/repeated/provenance failure has no GL or retention. Each shader callback receives invocation-only `locate` over the private bound program and one retainable non-operational epoch token; locations cache only within the generation, and the token invalidates before every later activation/release/off/replacement/teardown. No handle or program operation is exposed. Phase 11 feeds Phase 6's custom participant rather than installing separately | Phase 6 supplies/consumes participants, Phase 11 feeds customs through Phase 6, Phase 7 composes |
| `ProgramRegistryPublisher.current` / `publish`; `RegistryBuildResult.Ready` / opaque `CompiledRegistryCandidate`; `RegistryPublication`, `BarrierPublicationCandidate`, `PublicationResult`, `PublicationFailure`, `PublicationFailureKind` | render-thread-only publisher entry points return the current non-owning snapshot or accept a publication plus mandatory caller-supplied release context. Compiler alone mints the registry product; ready publication accepts it only with the factory product paired to that exact product/registry identity. Before activity-token invalidation or release, the publisher validates the closed pre-release set: non-null publication; render thread; context presence/source/epoch/kind; compiler origin and registry state; barrier and production-composition provenance; exact product/registry identities; barrier state; ownership; and non-bootstrap status. Failure returns `Rejected(unchanged, cause)`. An absent authenticated old barrier skips release with zero old-barrier GL work. Once old-barrier release begins, `ShadersOff`, `FailedSafe`, protocol-invalid result, exception, or unexpected backend failure returns `RecoveredOff(empty, cause)`. Each case maps to its exact closed `PublicationFailureKind`; caller reports every cause, retains/closes rejected or recovered-off candidates, and immediately follows vanilla recovery after `RecoveredOff`. Accepted transfer makes caller close harmless and publisher owns teardown | Phases 7, 12 |
| `PublishedRegistry.generation` | changes once per accepted registry/off publication or forced `RecoveredOff`; pre-release rejection does not change it; consumers compare for inequality | Phase 12 reload paths and every derived program/uniform cache |
| `RegistryFingerprint` / `ProgramUniformLayoutFingerprint` | deterministic registry derivation and exact linked-declaration identities, both distinct from generation and GL activity | Phases 5, 6, 7, 12 |
| `ProgramBuildFailure`, `RegistryBuildFailure`, `PublicationFailure` | Incorporates §4.12's complete closed stages and fields: SAMPLER_LAYOUT is projection-eligible with mandatory Optional<ProgramSamplerLayout> samplerLayout() evidence, SAMPLER_UNIT_TYPE_CONFLICT or SAMPLER_LAYOUT_UNSUPPORTED, and canonical nonempty sanitized projectionDetail. INVALID_SAMPLER_POLICY is registry-wide pre-GL failure. Runtime barrier/publication failures cannot change resolution projections; separate publication cause retains stable diagnostic ID/sanitized message | Phases 5, 7, 12, 13 |
| `ProgramSamplerDeclaration`, `ProgramSamplerLayout`, fingerprints, `FixedUnitSamplerConflict`, `SamplerLayoutIssue`, `SamplerLayoutValidation`, `FixedSamplerLayoutPolicy` | All exact field types/orders, variants and method signatures in §2.2 are incorporated verbatim as monitored interfaces. Declaration retains full Phase3 type/order/sites; Shader(fingerprint,policyFingerprint,effectiveStage,validatedBands,declarations,validation), FixedFunctionEmpty(fingerprint,policyFingerprint), VirtualNotApplicable(fingerprint,policyFingerprint). Valid / ConflictingTypes(conflicts,otherIssues) / Unsupported(issues) preserve all canonical evidence. validate(StageId,StageBand,List<ProgramSamplerDeclaration>) and fingerprint() are pure callbacks implemented solely by Phase5; §4.7 defines mandatory all-band pre-GL checks and SHA-256/framing | Phase5 supplies; Phases5/6/7/13 consume metadata |
| `ProgramBindingSelection`, `ProgramSelectionResult`, `ProgramBindingSelections`, `ProgramSelectionValidation`, `ProgramSelectionRejection` | Exact §2.2 accessors and variants are incorporated: Selected(selection), Skipped(requested), StalePublication(expectedGeneration,currentGeneration), ShadersOff(diagnosticId). Opaque selection exposes registryGeneration,registryFingerprint,requested,effectiveDescriptor,effectiveStage,actualBand,originatingContext; no constructor/handle/teardown. Static validateSelection(selection,context) → Valid or Rejected(INVALID_ISSUER / STALE_GENERATION / STALE_CONTEXT / WRONG_STAGE_BAND / PROVIDER_LAYOUT_MISMATCH). §4.10's private-origin checks/order/lifetime bind every consumer, not public equality | Phases5/7/8/13 |
| Fixed attribute table | `mc_Entity=10`, `mc_midTexCoord=11`, `at_tangent=12` | Phase 10 |
| Per-slot `instanceCount` | positive total count from the effective provider; incorporates §4.9/§11.5 approved v0.5 adjacent prepared-submission contract as well as fullscreen repetition; metadata is not live draw authority | Phase 7 policy; Phase 10 existing draw adapters; Phase 6 existing `instanceId` event; Phase 8 single shadow traversal |
| Phase 2 inspection candidate view | §5.6 explicitly permits `candidate.view().resolutions()` on the same inspection build, detached immutable rows and caller candidate closure without runtime publication; no new configuration-fingerprint accessor | Phase 2 |
| Effective geometry draw handoff — R10-5 | P7 takes `Activated.binding().state().geometryInput()` only under the matching successful selection, carries the actual effective/forced-shadow provider and clears to NONE on FixedFunction. Exact enum mapping into P10 `VertexGeometryInput`/`VertexInputPlan.expectedGeometryInput()`; no raw query, GLSL rescan, retained-view authority or requested-child overlay. Fullscreen stays P1-owned | Phases 7, 10 |
| `DrawRouting.Explicit(List<DrawRoutingSlot> slots)`, `DrawRoutingSlot.Attachment(BufferRef buffer)`, `None()` | §4.9 lossless positional P3 adaptation, nonempty list, holes consume output capacity; packed attachment count and explicit duplicate validation; routing remains identity-bearing in §4.11's profile-selection-v3 domain | Phases 5, 7, 2 |

D-P4-43 also incorporates §4.11's exact selection/evaluated-state identity and §4.12's closed
`INVALID_PROGRAM_STATE` reduction into the fingerprint, failure and resolution rows above.
Candidate and accepted evidence must come from that same evaluation; no profile payload or new
column is added to P3's nine inspection trees or P2/P7's existing source-free resolution grammar.

Phase 5 must not infer a resolved ping-pong side from `explicitFlips`; it owns the side state.
For virtual pre descriptors it consumes `explicitFlips` through its typed transition operation and
must not request or synthesize a `ResolvedProgramDescriptor`.
Phase 6 must not bypass the barrier to refresh a program. Phase 7 must not re-resolve backup chains
or overlay requested-slot state on the effective provider. Phase 10 must not renumber attributes.
The shared lifecycle is not permission to replay handles: Phase5 snapshots expire with their pass/
frame/depth/estate/registry/selection/publication and own open state. Phase13 leases are current only
when open and their owner is currently READY, not retiring. Non-final FixedFunctionEmpty uses
vanilla state with no candidates; FINAL alone uses Phase5's separately typed
BindingPurpose.FIXED_FUNCTION_PASSTHROUGH, frozen compatible colortex0 on0, without declarations,
custom inference or sampler uploads. Shader bindings use BindingPurpose.SHADER; an operation with
no binding purpose uses BindingPurpose.NONE.

One drained Phase 7 request is not one publisher call. Each accepted Ready/off publication or
`RecoveredOff` independently increments once; a pre-release `Rejected` increments zero.
Ready followed by compensating Off may therefore increment twice. No GUI coalescing rule
suppresses either change. Phase 7 reacquires actual generations for cache invalidation and final
composition outcome; configuration-keyed GUI presentation need not pretend to be a program cache.

### 5.2 Consumed Phase 1 contracts

| Phase 1 §5 contract | Use here |
|---|---|
| `:engine` layout, package rules, C-1…C-4 seam | all registry/compiler types and tests |
| `GLDevice`/seven services | `ShaderService.use(ProgramHandle)`, `ShaderService.useFixedFunction()`, `UniformService.locate`, `StateService`, and no-op `DebugService` call sites only |
| `UniformLocation` present/absent opaque value | callback-scoped lookup result passed to Phase 6 without exposing the private program handle; locations may be cached only for the current publication generation |
| `GLCapabilityProfile` + serialization | route/attribute/geometry validation and recorded-profile tests |
| Opaque `ProgramHandle`/`ShaderHandle` lifetime | candidate ownership ledger and registry teardown |
| `CompileResult` / `LinkResult` / `ValidateResult` | never-throwing build state machine |
| `SamplerUnitAssignment`, `SamplerInitializationResult`, `ShaderService.initializeSamplerUnits` | P1 D-P1-59 exact candidate-only transaction; Completed required before validate, restored failure is local VALIDATE evidence, unproven restoration poisons admission and aborts registry; no runtime participant or GL handle escapes |
| `ShaderService.configureLegacyGeometry` and closed topology enums; `shaders.configureLegacyGeometry` recorder event | complete §4.8 native strategy, Phase 1 §4.7.4 preconditions/extension checks and mandatory drain-configure-drain-abort protocol; owner-designed/unverified, receiver-adopted/unverified with current P3 source grant |
| `ShaderService.linkedGeometryInput(ProgramHandle)` → `Optional<LinkedGeometryInputPrimitive>`; R26 §4.7.4a | cached successful-link result, empty iff no geometry; exact five-enum domain, pre-GL handle/thread/context rejection and deletion invalidation; compare before validate/READY. Link acquisition failure, selection/restoration tracking and fullscreen failure semantics are adopted in full; no LinkResult getter invented |
| `RecordingGLDevice`, `ScriptedResponses`, `GLCallLog` | headless compile/failure/barrier tests, including the zero-argument `shaders.useFixedFunction` event |
| `ReplayAssertions` | call order, no leaks, no use-after-delete, and fixed-terminal distinction from handle-bearing `shaders.use` |
| `StateService.lockAlphaBlend`, opaque `AlphaBlendOverride.close`, `effectiveBlend` | P1 D-P1-57 duration enforcement; P4 owns single-lease lifetime, exact restore and failure containment, not immediate-setter emulation |
| `DiagnosticReporter` and fixed channels | sanitized program/user errors on `.compile` and `.gl` |
| SPDX/`THIRD-PARTY.md` mechanism | any future LGPL-derived implementation, though this design copies no source |

Phase 1 exposes `ShaderService.useFixedFunction()` as the handle-free selection operation for
program zero. Phase 4 consumes that verified contract directly and continues to forbid
`use(null)`, a magic handle, or a raw integer.

**D-P4-42 ordinary fixed-terminal receipt:** P14 D-P14-31/P1 maintain complete latest
authenticated owner-equivalent texture-object parameters alongside sampler cache state.
P4 retains P5-bind→useFixedFunction→draw, with binding-neutral all-unit sampler clearing and
result-checked failure containment. This ordinary transition needs no demotion, rebinding,
sampler left attached or new callback. Borrowed objects remain unmodified.

### 5.3 Consumed Phase 3 contracts

The current Phase 3 dependency remains provisional. Consume exactly
`schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION`; reject every other schema
before derivation or retention. The entire nested graph, including
`IdMappingInput.schemaVersion`, must agree; do not synthesize geometry, companion, locale or
declaration defaults or infer upgrades. Same-request inspection obeys this same gate.

**Schema23 receiver receipt — D-P4-41.** Receive the coordinated P3 range-capable selector
cutover with CURRENT_SCHEMA_VERSION=23 and MaterializedSource-v23. Require exact-current equal
containing configuration, every nested schema (including IdMappingInput), and inspection snapshot
before derivation, retention, reuse or enrichment; reject older/future/mismatched objects without
conversion. P9 alone resolves registry selectors; P4 adds no parser or selector expansion.
Nine metadata-only trees, projectionVersion1, same-load assets, options and native contracts remain
unchanged. D-P4-38's schema22/MaterializedSource-v22 and every earlier numeric receipt are
historical, not alternative acceptance gates.

**Historical schema21 receiver receipt — D-P4-32, 2026-09-08 (unverified).** Adopt the coordinated
P3 R55 correction and §5. CURRENT_SCHEMA_VERSION is 21 for this receipt, superseding
D-P4-30's current schema20 assertion only; D-P3-69 assets and nine-tree meanings remain.
Configuration, `IdMappingInput.schemaVersion` and inspection snapshot must equal the owner
constant. Consume the current materialization domain without upgrading old objects.
Structural load success means usable base **or** at least one usable explicit override.
A valid override-only pack is not rejected for missing base; this does not create a base source
or merge dimensions. Effective world selection can still be disabled where no usable entry
applies. Structural success is not successful compilation, live rendering or T3.
`ScreenProfileEntry()` is a payload-free selector of the configuration's global profiles,
not a list of selected IDs or a cache of profile payloads. The external case-sensitive token
`TEXTURE_RECTANGLE` maps to existing `TextureTarget.RECTANGLE`; bare external `RECTANGLE`
is invalid. P4 consumes these owner-issued typed values only: no independent property reads,
selector reinterpretation, target aliases or property grammar in the registry.
Required non-null `assets`, immediately after `sources`, remains P3's exact same-load
capability with the exact containing `PackIdentity`; reject foreign-load pairing even if
structural identities compare equal. Carry its metadata-derived configuration fingerprint
unchanged into registry/materialization identity; no dummy asset fields, reconstructed
capability, old-schema upgrade or resource-epoch relabeling is permitted. P4 gains no binary
acquisition/decoding authority. P13 alone interprets owned bytes and sidecar recovery; P3's
other safety/bounds/index/container/source/configuration failures remain fatal.
Historical decisions, authority and review quotations remain evidence, not current PASS.
Fresh producer/receiver whole-document review and IR-01 still gate implementation.

| Phase 3 §5 contract | Use here |
|---|---|
| `PackConfiguration`, schema/fingerprint discipline | sole registry-build truth and cache key |
| `DimensionConfiguration` | exact no-base-merge source selection or disabled outcome |
| `SourceCatalog`, `SourceKey`, `SourceMaterializer`, `MaterializedSource`, `SourceMap`, `SourceMapping`, `LegacyGeometrySite` | once-per-present-stage same-build materialization; provenance-only pair sites may span different included files, exact final token/expansion mappings and route-distinct fingerprints; no rewrite-range interpretation or source rescan |
| `DeclaredUniformCatalog`, `DeclaredUniform`, `DeclaredGlslType`, `ShaderSourceStage`, attributed locations | complete final per-stage declaration metadata and materialization identity; Phase 4 merges equal types, rejects same-name unequal types before GL, and never infers optimized-out activity |
| `GeometrySourceRequest`, `GeometrySourceForm`, `GeometryLayout`, `GeometryLayoutDeclaration`, `ShaderLanguage`, closed input/output/profile/extension enums | complete §4.8 request/result and P3 §5-incorporated §4.5 language/attribution/classification rules; NativeLegacy.config differs from per-property source-overridden effective layout; no translation/proof API |
| singular `MacroContribution` | Phase 6 contribution passed unchanged to every materialization |
| `OptionConfiguration` / catalog-issued immutable `OptionState` | finalized same-build state retained inside the source catalog; materialization and evaluated-program-state operations accept no replacement state |
| `ProgramStateModel` / ordered `EvaluatedProgramStates` | §4.7 calls configuration.evaluateProgramStates(profileSelection,diagnostics) once on the exact request pair, consuming P3's closed result without replacement OptionState. Exact dimension/program key, typed alpha/blend/scale, property-enabled/profile-disabled/final-enabled results; final false is absent to fallback. Phase 5 alone receives the separate flip-only projection |
| closed `ResourceRequirements` algebra | direct `resources().programs()` lookup by `ProgramRequirementKey(dimension, exactProgramName)` supplies routing, instance count, optional legacy geometry, `mipmappedAfterPass()` and `vertices()`; the latter two are the §0.57 grant, not downstream Phase 5/10 queries |
| materialization/configuration/catalog fingerprints | registry/layout derivation identity and cache reuse |
| diagnostics | source-attributed unavailable/failure conversion |

Phase 4 never reopens the pack, rescans a directive, interprets properties, or bypasses the
materializer.

### 5.4 Requested changes to dependency contracts

1. **Phase 1 native configuration and cached linked metadata: owner-granted/unverified,
   receiver-adopted/unverified.** Consume §5.2's configure transaction and
   `ShaderService.linkedGeometryInput(ProgramHandle)` exact Optional projection under §4.8.
   The formerly missing producer projection was requested during coordination and is now granted
   in P1 §4.7.4a/§5.2; no outstanding accessor request or invented `LinkResult` member remains.
   Failed acquisition or expected/actual disagreement cannot reach drawable publication.
2. **Phase 3 native source and registry projections: owner-granted/unverified,
   receiver-adopted/unverified.** D-P3-68/§0.61 closes native-source absence. §§4.7/4.8/5.3
   consume the exact current algebra, complete same-build catalogs/maps/fingerprints and direct
   `mipmappedAfterPass()`/`vertices()` values. All earlier schemas remain historical only.
   Complete core translation is neither required nor granted; no old translation alias survives.

Fresh Phase 1/3/4 and affected receiver reviews, implementation-time jcpp pin/closure evidence,
conditional native-submission implementation and real-context draw conformance remain required.
Source and linked-metadata API absence are closed, not verification or implementation clearance.

**Coordinated Phase5 consumption (new, unverified).** Phase7 passes
`FixedSamplerPolicies.appB3()` as the §2.2 samplerPolicy, and Phase5 planning/creation compare
`ProgramRegistryView.samplerPolicyFingerprint()` against that same policy.
`FixedSamplerPolicies.resolver()` returns the Phase5-owned pure `FixedSamplerResolver`:
`resolve(ProgramSamplerLayout layout,StageId stage,StageBand band)` →
`FixedSamplerPlanResult.Ready(List<ResolvedSamplerBinding> bindings,FixedSamplerPolicyFingerprint policy)`
or `Invalid(SamplerLayoutValidation reason)`. Both factories exist before runtime/registry/estate
creation, use one fixed table/schema, and agree on fingerprint. No backwards runtime dependency
or Phase4 copy of the unit map is introduced.

**R7-10/11: owner-designed/unverified, receiver-adopted/unverified.** Phase 6 §§4.9/4.14/5
now consume the required resolver immediately after configuration in `UniformRuntimeFactory.create`
and retain the unchanged `afterBind` callback; no second map or participant exists. It publishes
`retire(UNPUBLISHED_ABORT|REPLACEMENT|SHUTDOWN)` returning `Retired`, `AlreadyRetired` or
`Rejected(WRONG_THREAD|ACTIVE_CALLBACK)`. There is no CLOSE alias. Phase 7 drains final callbacks/
restoration, retains borrowed services after rejection, and obeys Phase 6's reason-specific
old-token invalidation/atomic-teardown ordering. Fresh owner verification remains required.

**R7-12/13: owner-designed/unverified, receiver-adopted/unverified.** Phase 8 §§0.7–0.8/5
accepts the same selection/activationContext/TexturePublication/TextureLeaseSource and Phase 5's
five-argument sixteen-row `shadowBindings`; object binding precedes activation, Bound alone
transfers the lease and binding closes once. Its pure
`ShadowPlanInput(ShadowPolicy policy,ShadowHookHealth hookHealth,boolean requested)` remains
registry-independent; requested comes from accepted P3 shadow minima under P8 D-P8-26.
`ShadowPassFactory.create` receives final registry immediately after plan.
The old four-row proposal is superseded. Real shadow remains NotInstalled/typed unavailable until
remaining owner/authority gates and fresh verification close, never prior-frame binding success.

Phase 1 package/jcpp admission and Phase 3 companion/lossless/projection/native-source grants
are present but unverified; exact jcpp pin/closure and runtime evidence remain gates. The 2026-09-07
maintainer correction in `docs/decisions/U1_TEXTURE_SAMPLING.md` removes the unspecified
filter/wrap key-suffix requirement; numeric discriminators and P13-owned sidecars remain.
Phase 4 preserves the singular centerDepthSmooth contribution and same-build macro state unchanged;
it never patches generated customtexN source.

### 5.5 Design-graph note

The Phase 4 assignment requires the generation for Phase 12, while §G5.1 does not list Phase 4 as
a Phase 12 dependency. This document exposes the interface as assigned and requests a future
DESIGN correction in §11; it does not edit the graph.

### 5.6 Source-free inspection enrichment (IR-29)

Phase 4 adopts Phase 2/3's inspection route without an engine dependency on conformance types.
Phase 3 `PackFrontEnd.inspect(PackLoadRequest)` returns its closed
`Off | Failed(PackLoadFailure,List<DecisionDiagnostic>) |
Inspected(PackConfiguration,PackDecisionSnapshot,Optional<String> archiveSha512)` result.
Phase 2 uses the exact `Inspected.configuration` as `RegistryBuildRequest.configuration` and
explicit `Optional.empty()` as `profileSelection`, matching P3's present absent-profile inspection
evaluation. Retain the snapshot's schema/configuration-fingerprint association and that explicit
absence with the same synchronous request until return. This same-request association is the
join proof: a `ProgramRegistryView` does not expose a configuration-fingerprint accessor, and an
arbitrary `publisher.current()` or named-profile build cannot substitute for this result.
A named-profile inspection requires a separately amended P3/P2 inspection contract; none is
granted here. P3 schema23 and its nine trees/projectionVersion1 remain unchanged.

On `RegistryBuildResult.Ready`, while owning/opening the candidate, copy
`candidate.view().resolutions()` unchanged: immutable catalog-ordered
`ProgramResolutionProjection(slot,status,from,sourcePresent,ownBuild,driverLog)` where `ownBuild`
is the required exact §4.6 enum token describing the requested slot, and `status` is
`SOURCED|CHAIN|ABSENT|FAILED`, `from` is optional and present only for CHAIN, and driverLog contains
only §4.12's sanitized deterministic failure projection for FAILED. Close the caller-owned
candidate in finally on its owning render thread; do not publish it merely for inspection.
The detached list remains safe after close. On `ShadersOff(failure)`, no complete resolution
list is published: report the sanitized closed build failure as incomplete evidence, not
fabricated all-ABSENT or FAILED rows. Off/frontend failure likewise supplies no compiler rows.

The harness may perform the synchronous build against Phase 1's RecordingGLDevice with a bound
scripted owner/render thread; it labels recorded evidence separately from actual-driver evidence.
Only source-free snapshot/evidence reaches the golden writer, never PackConfiguration, source
catalog/text/maps, live runtime, handles or arbitrary driver log. The archive SHA-512 comes only
from Phase 3's exact-byte inspection result and must match Phase 2's independently verified matrix
archive; registry/configuration/materialization hashes are not substitutes. Folder/internal or
partial synthetic inspection cannot claim a complete verified-archive matrix golden.
This producer/consumer adoption is unverified and remains subject to fresh owner reviews.
The current P3 schema migrates the containing snapshot and nested identities together;
`projectionVersion=1` remains P3-owned and unchanged. D-P4-31 changes P4's resolution field
grammar and registry fingerprint domain, not the nine-tree frontend projection. Detached descriptors also carry
the agreed geometry enum but gain no source/handle/liveness authority or new golden column.
Materialized language/token maps/catalogs remain compiler inputs, not new golden allowlist entries.
Geometry acquisition/mismatch failures follow the same FAILED/CHAIN masking and incomplete
ShadersOff rules. P2 uses P1's `ScriptedResponses.linkedGeometryInput` for actual core/override
inputs and the new cached-inspection/fullscreen recorder events; synthetic golden updates remain
explicit and are not proof of live geometry behavior.

D-P4-32 requires `PackDecisionSnapshot.schemaVersion == configuration.schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION`
before enrichment. Preserve the actual P3 snapshot's ninth `assets` section from that same
load alongside the original eight sections with unchanged meaning. It is only the canonical
`assets().manifest()` metadata `Sequence`: record fields and `$type`, availability enum,
byte count IntegerValue/Absent, canonical path, and sha256 String-leaf TextHash/Absent
(hash the digest string). `projectionVersion=1` remains unchanged. No bytes, cursors,
providers, capability identities, shader text or synthesized empty-assets upgrade enter
inspection. Existing same-request resolution enrichment and its failure rules stay unchanged.

### 5.7 Optional Phase 14 compiler split request (IR-27)

**R-P14→P4-1 received, pending/not adopted.** Before enabling async, Phase 14 requests that Phase 4
own `prepare(request)`, freezing/validating every source/materialization/sampler-policy output
before GL into a compiler-issued immutable batch; workers would only create/source/compile
shaders, collect status/log and establish a visibility fence. Phase 4's render thread would
consume ready batches, wait for visibility, create/attach/link/validate/query layouts and mint
the caller-owned candidate. Tokens/results must be opaque without public handles; Phase 4
retains deterministic failure/fallback, cancellation and late-result cleanup ownership.
Workers must retain no callback or call Phase 3/5/runtime. P7's matching step-3 suspension request
is separately gated. This is an exact owner-change request, not new callable API: operation
signatures, closed cancellation/visibility/cleanup outcomes and OQ-15 evidence require an explicit
Phase 4 owner amendment/review before adoption. Until then only current synchronous
`compile(RegistryBuildRequest)` is exposed; Inline/current synchronous fallback and render-thread
publication remain mandatory.

**D-P4-39 optional split receipt.** Any future adoption also requires P14's producer
fence→producer-context flush→publication order, retained ownership on failed publication,
and nonblocking completion admission. Watchdog expiry fails logically but retains uncertain
worker/context/fence/object ownership until acknowledged quiescence and detachment permit
disposal. No synchronous replay or successful shutdown is inferred from timeout; unresolved
native work remains quarantined. These are pending proposal requirements, not new callable API.

## 6. Failure modes & degradation

| G2.4 rung | Phase 4 case | Required response |
|---|---|---|
| 1 | custom-uniform participant reports one expression/uniform failure | Phase 11/6 disables only that custom uniform; barrier continues |
| 2 | built-in uniform participant reports one upload failure | Phase 6 disables only that built-in uniform under Phase 1's replay protocol; program remains active |
| 2 | one participant misuses callback-scoped lookup or throws | close that callback window, record its `Degraded` scope, continue later participant positions; never expose or retry with a handle |
| 2a | optional debug label fails | disable only that debug feature for the generation, diagnose, keep the program |
| safe/off | duration alpha/blend acquire or effective notification fails | no participant dispatch or Activated; follow §4.10 rollback and FailedSafe/ShadersOff containment, never silently omit the required override |
| poisoned | duration lease close/rollback cannot restore | invalidate activity, forbid shader admission until safe recovery/reconstruction; attempt independent cleanup, never keep drawing the program |
| 3 | materialize/declaration-layout conflict/compile/link/validate/attribute/geometry failure for one program | delete candidate objects for that program, emit source-attributed/user-visible error, mark it failed, resolve the entire binding through its backup chain |
| 3 | absent deferred/composite indexed pass | skip it; absence is normal, not an error |
| 3 | unavailable root with a fixed terminal | select fixed function through `ShaderService.useFixedFunction()` and continue the declared terminal/fallback disposition |
| 4 | registry-wide capability failure—e.g. required estate cannot fit, required high attribute has no fallback, or every required terminal is unavailable | return `ShadersOff`, chat error through caller, do not publish partial registry |
| 5 | unexpected exception, cleanup failure, stale/use-after-delete invariant, or barrier cannot restore safe state | catch at public boundary, close all owned candidate objects, request shaders-off publication, leave/restore vanilla framebuffer path; never crash client |
| 3 | fixed-unit type conflict or unsupported sampler name/domain/full shape | typed SAMPLER_LAYOUT evidence before any provider GL creation; candidate-local fallback to the ancestor's entire layout |
| 4 | null/invalid/throwing fixed-policy callback | INVALID_SAMPLER_POLICY with zero registry GL allocation; no validation bypass |
| protocol | stale/foreign/mismatched selector | pure rejection before binding/upload/draw; caller suppresses and discards the undrawn pass without flips |
| protocol | Phase5 Rejected/Degraded versus BackendFailed | first two bind nothing and do not transfer a lease; BackendFailed may partially bind, transfers nothing and requires containment; never activate or choose another provider |
| transaction | texture rebuild fails after registry/estate acceptance | Phase7 compensates owners off; low-level publication rejection never authorizes resuming an incoherent pipeline |

A stale activity token is a normal no-upload condition, not a failure rung. Phase 6 retains its
cached value for the next activation and issues no GL call.

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
- Sampler projection, all-band policy validation, canonical hashing and Phase5 candidate indexing
  are cold immutable preparation; no per-draw parsing, byte hashing or free-unit allocation.
- Every `GLDevice` call, candidate publication, registry close, and barrier call runs on the render
  thread.
- `BoundProgramUniformAccess.locate` is legal only in the current render-thread participant
  callback. The retained activity token's epoch comparison is thread-safe and side-effect-free,
  but a true result does not waive the render-thread rule of the upload facade.
- The compiler remains synchronous. Phase 14 shared-context execution requires OQ-15 evidence
  **and explicit adoption/review of R-P14→P4-1 (§5.7)**; a successful spike alone grants no
  split API. Synchronous fallback, publication and vanilla-state interaction remain render-thread work.
- `PublishedRegistry` is an immutable snapshot. The publisher may expose it safely to readers, but
  no off-thread reader may dereference a GL handle or call its non-owning barrier view; consumers
  must treat `StalePublication` as a no-draw signal and reacquire the snapshot.

### 7.2 Allocation and hot paths

Compilation is cold and may allocate readable immutable plans and diagnostics. The barrier is hot:

- slot lookup is by stable catalog ordinal plus pass-family index, not a per-switch string map;
- resolved backup results are memoized in the immutable registry;
- participant list and effective state bundle are immutable arrays/records;
- merged uniform layouts and location-cache keys are immutable cold-build products; no source
  token, source map, declaration merge, or layout hash runs during activation;
- no stream, list copy, boxing, formatted log message, or source-map work occurs on a clean
  activation;
- the active lock stores at most one state snapshot and logical/effective pair; and
- generation polling and activity-token checks are primitive epoch comparisons.

No global catalog- or pass-family-sized sweep runs on every program switch.
Opaque selections and the bounded per-draw lease/snapshot lifecycle objects may allocate. Reuse
precomputed declaration lists and fixed-unit indexes; no promise of zero allocation for a fresh
immutable selection/lease/snapshot, and no incidental per-row copy or boxing.
Compile iterates configured slots; activation touches one resolved binding.

### 7.3 Driver interaction

Attribute binds occur only for declared attributes. Shader stages are created in deterministic
order for stable recorded logs. Phase 1's backend obligation to route cached state through
`GlStateManager` applies to alpha/blend restoration and texture binding performed by Phase5; this
phase never introduces raw LWJGL calls.

## 8. Testability plan

### 8.1 Pure registry tests

- `classicSchedule_hasFiveIdentitiesAndTwoGbuffersOccurrences`
- `modernSchedule_instantiatesAllNineIdentitiesWithoutStructuralChange`
- `sparseFamilies_acceptZeroNinetyNineAndHoles`
- `g6DeferredComposite_populateThroughFifteenWithoutSixteenSizedType`
- `computeSlots_primaryAndAThroughZ_areDormant`
- `computeSlots_onEveryGbuffersPassAreRejected`
- `stageAccess_neverStoresMainOrAltTextureSide`
- `classicCatalog_mapsEveryAppendixA1Row`
- `classicCatalog_declaredAndEnumeratedCountsAgreeAtSixty`
- `fallback_exactAppendixAEdges`
- `fallback_missingDisabledAndFailedUseNearestAncestor`
- `fallback_inheritsProviderStateWithoutChildOverlay`
- `fallback_shadowRootFixedButChildrenMayUseShadow`
- `fallback_cycleRejectedAtCatalogConstruction`
- `virtualPre_neverCompilesOrProvidesFallback`
- `virtualPre_descriptorIsTypedFlipOnlyTransition`
- `sparsePrelude_preThenZeroFiveNinetyNine_exactReceiverTrace`: construct one DEFERRED step
  with `SparseArray(99,99,Optional.of(deferred_pre))`, inserting raster descriptors 99, 0, 5
  out of order; traversal returns exactly pre,0,5,99. Repeat with COMPOSITE/composite_pre.
  `named` returns the identical first descriptor; indexed 0/5/99 return the corresponding
  contained raster descriptors; holes 1/4/98 return empty. P7 forwards that exact pre descriptor
  once to P5 before raster execution, without select/activate/draw for pre or synthesized state.
- `sparsePrelude_emptyAndPreludeOnly_doNotInventIndexZero`: absent pre plus no indexed members
  gives an empty traversal and legal pre lookup empty; present pre plus no indexed members
  gives only pre. Both have maximum -1; absent pre plus 0/5/99 gives only those indices.
- `sparsePrelude_illegalMembershipRejectedBeforePublication`: reject wrong/cross-family/duplicate
  preludes, missing descriptor for present optional, extra descriptor for empty optional,
  indexed virtual, raster/fixed prelude, nonempty pre index/source/fallback/compute/read/write/
  mipmap data, virtual in named/singleton population, mismatched maximum or stage/band, duplicate
  indices/slots and second deferred/composite occurrence. Wrong-kind/key lookup rejects rather
  than looking absent. §2.2's no-program invariant also leaves `resolve(pre)` empty.

The three sparse-prelude cases are planned behavioral boundaries and receiver traces, not
executed tests or health rows. The remaining pure-registry cases are:
- `resolutionProjection_allStatusesAndExactFieldGrammar`
- `resolutionProjection_chainPreservesSourcePresentTrueAndFalse`
- `resolutionProjection_missingChildFailedAncestorTerminalIsFailedWithSourceAbsent`
- `resolutionProjection_disabledChildFailedAncestorTerminalIsFailedWithSourceFactPreserved`
- `resolutionProjection_failedAncestorThenSuccessfulAncestorIsChainAndFailureDiagnosticOnly`
- `resolutionProjection_materializeAndCapabilityFailuresHaveNonEmptySanitizedDetail`
- `resolutionProjection_everyEligibleBuildStageHasNonEmptySanitizedDetail`
- `resolutionProjection_barrierFailuresCannotAlterCandidateOrRuntimeRows`
- `resolutionProjection_candidateGoldenRuntimeValueEqualAndCatalogOrdered`
- `programEnabledFalseAndProfileDisableAreEquivalentToAbsent`
- `profileSelection_equalOptionsDistinctAvailability`: one immutable configuration contains valid
  equal-constraint profiles Keep, NoWater, and NoWaterOrTerrain, with present valid water, terrain
  and textured_lit sources and deliberately different provider state. Keep yields water
  SUCCEEDED/SOURCED; NoWater yields water DISABLED/CHAIN from terrain without a water build;
  NoWaterOrTerrain skips both disabled sources and yields water DISABLED/CHAIN from textured_lit.
  Preserve sourcePresent=true and the entire effective provider state/path, not child overlays.
  Exact profile intent and evaluated states give distinct fingerprints; candidate and accepted
  rows remain equal. P7's selection-only accepted change reaches the compiler unchanged.
- `profileSelection_absentUnknownAndInvalidRemainDistinct`: explicit empty disables no program
  by profile, while property false still yields DISABLED; unknown selected name warns/applies no
  profile disables but retains distinct identity. Null wrapper rejects, never becomes empty;
  P3 InvalidState yields INVALID_PROGRAM_STATE before materialization/GL and no projection.
  No configuration mutation, inference, preview-state substitution or virtual-pre program entry.
- `profileSelection_inspectionKeepsAbsentAssociation`: P2's exact Inspected configuration plus
  explicit empty selection enriches only its associated snapshot; a named-profile candidate
  cannot be joined even if option values and configuration fingerprint coincide.
- `drawRouting_preservesOrderEmptyAndAllUsedSymbol`
- `drawRouting_rejectsDuplicateOutOfDomainAndCapabilityOverflow`
- `attributeLocations_areExactlyTenElevenTwelve`
- `attributeCapability_checkedOnlyWhenDeclared`
- `instanceCount_retainedForEveryProgramFamily`
- `uniformLayout_sameTypeAcrossStagesMergesAllAttributedSites`
- `uniformLayout_sameNameDifferentTypeFailsBeforeGL`
- `uniformLayout_exactNameCaseSensitiveAndDeterministicallyOrdered`
- `uniformLayout_fingerprintEqualInputsEqualAndExcludesActivity`
- `fallback_descriptorUsesEffectiveProviderUniformLayout`
- `fixedFunction_descriptorUsesCanonicalEmptyUniformLayout`
- `fingerprint_equalInputsEqualAndGenerationExcluded`

The cardinality test proves every table row exists and the corrected declared count agrees with the
enumerated count; runtime behavior still depends on the rows and selected slot, not either count.

### 8.2 Geometry tests

- Ordinary complete core layout uses request None, returns CoreLayout and requires GL3.2;
  non-GEOMETRY roots alone return form None; missing/conflicting geometry never drops the stage.
- PreserveNative matches every config/root/site/count field after the singular contribution;
  native version/extensions/built-ins/varyings/constants, included sites and macro mappings survive.
- Native without source layouts configures exact TRIANGLES/TRIANGLE_STRIP/count; partial/full
  source overrides change effective metadata but never the API triple or extension gate.
- Absent ARB support rejects native even on GL3.2; source layouts additionally require GL3.2.
- Configure rejection/error, compile/interface/output-limit/link/validate failure clean every
  candidate object, preserve source attribution and resolve the whole fallback provider.
- Script actual linked input with P1 `linkedGeometryInput`; metadata acquisition failure or
  expected/actual mismatch (including no-stage versus present) prevents validate/READY and
  produces source-free LINK failure evidence, never a fabricated geometry-free success.
- Core/native route, effective input, API count, source/map/catalog or capability change
  invalidates fingerprints; non-current schema or nested/inspection mismatch rejects before derivation.
- Failed child with different geometry and forced shadow both expose the actual ancestor/root
  input; FixedFunction clears it; detached retained views authorize no draw.
- P1 fullscreen TRIANGLES selects strip on QUADS-capable compatibility contexts; points/lines/
  adjacency/unknown reject without draw and cannot commit flips. Selection failure and nested
  restoration preserve/invalidate metadata with program lifetime, not standalone enum copies.
- P7/P10 native client-array/VBO/display-list conversion and original per-quad attributes,
  provoking vertex/two primitive IDs, storage lifetime and adjacent instance ordering require
  real-context checks at the claimed milestones; recorder success alone proves no GLSL/parity.

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
- a cross-stage uniform type conflict emits an attributed program failure and no create/compile/
  link/locate GL call;
- a fixed terminal records `shaders.useFixedFunction` after lock restoration and records no
  handle-bearing `shaders.use`;
- `ReplayAssertions.noLeakedObjects()` holds after candidate failure and registry close;
- `noUseAfterDelete()` holds across reload publication;
- negative Pintonium fixture asserts locations 13/14 and double-bound 11 never appear; and
- source/driver logs are sanitized and pack source is absent from rendered call logs.

### 8.4 Barrier and generation tests

- `publishedRegistryView_hasNoCloseAndCannotDeleteCurrentHandles`
- `publishedResolutionAndActivatedResult_apiShapeContainsNoProgramHandle`
- `retainedDescriptorAfterStalePublicationCannotDriveShaderServiceUse`
- `candidateView_retainedAcrossCandidateCloseRemainsDetachedMetadata`
- `candidateView_retainedAcrossAcceptedPublicationRemainsDetachedMetadata`
- `candidateView_retainedAcrossRejectionAndRecoveredOffRemainsDetachedMetadata`
- `bootstrapNoOpCapability_isPackagePrivateAndUnavailableToPhase7`
- `productionPublication_rejectsBootstrapBarrierBeforeRelease`
- `productionParticipants_synthesizedNoOpBundleCannotReachFactoryReady`
- `productionComposer_crossPackagePhase7RouteHasFixedParticipantOrder`
- `productionComposer_nullClosedOrRepeatedCallIsInvalidWithoutRetention`
- `externalCompiledRegistry_evenWithGenuineParticipantsCannotComposeOrPublish`
- `productionPublication_arbitraryBarrierCannotFormReadyInput`
- `barrierFactory_rejectsNullInvalidOrWrongProvenanceWithoutGLOrRetention`
- `barrierFactory_acceptTransfersOwnership_rejectRetainsCallerOwnership`
- `barrierCandidate_closeBeforeTransferIsIdempotentAndDropsReferencesWithoutGL`
- `barrierCandidate_closeAfterAcceptedTransferCannotAffectPublication`
- `barrierFactory_customPositionReceivesPhase11ThroughPhase6`
- `barrier_order_selectBindAuthenticateCloseUseLockSamplerBuiltinCustom`: effective selection
  and P5 binds → same provider; effective blend visible before built-ins, no second resolution.
- `barrier_shadowPassForcesShadowBeforeFallback`
- `barrier_contextCurrentActivationAccepted`
- `barrier_contextForeignRetiredWrongKindStageBandAndShadowRejectedBeforeWork`
- `barrier_publicContextImplementationsCannotMintAcceptedContext`
- `barrier_releaseContextCurrentAcceptedOutsideActivation`
- `barrier_releaseContextShadowPassAlwaysFalseIncludingCopiedShadowStep`
- `barrier_releaseContextMissingForeignRetiredAndActivationKindRejectPublication`
- `barrier_sameHandleStillRefreshesParticipants`
- `barrier_allThreeParticipantsReceiveSameBoundAccessKeyAndToken`
- `boundUniformAccess_locateDelegatesPrivateHandleWithoutExposingIt`
- `boundUniformAccess_blankUndeclaredOrOutOfCallbackDegradesWithoutLookup`
- `boundUniformAccess_locationsCacheOnlyUntilGenerationInequality`
- `activityToken_currentThroughDrawAndStaleBeforeEveryLaterActivation`
- `activityToken_invalidBeforeFixedReleaseOffReplacementFailedSafeAndTeardown`
- R32: vanilla attempts enable/disable/functions/factors during OFF/explicit/partial locks
  cannot alter effective draw state; unlocked aspects work, exact predecessor returns on release.
- R32: acquisition, participant, notification and restore failures invalidate activity, close
  at most one lease and contain drawing; nested suspend/pop never captures prior override.
- R32: 0N2/N0/0N/NN preserve location and digest; duplicate attachments and full positional
  capacity reject pre-GL, repeated None does not. These are planned checks, not PASS evidence.
- `activityToken_staleNeverRevalidatesAndHasNoOperations`
- `fallback_childrenShareEffectiveProgramUniformCacheKey`
- `sameProviderNewGenerationHasDifferentUniformCacheKey`
- `barrier_requestedChildUsesEntireProviderState`
- `barrier_absentOverrideDoesNotLock_explicitOffDoes`
- `barrier_transitionRestoresUnderlyingStateNotPriorOverride`
- `barrier_participantFailureIsIsolatedByOwner`
- `barrier_activatedRecordsIsolatedDegradationAfterSequenceCompletes`
- `barrier_fixedTerminalRestoresThenCallsUseFixedFunction`
- `publication_releaseReceivesCurrentContext_forReadyAndOff`
- `publication_offToReadyAuthenticatesContextSkipsReleaseAndTransfersOnce`
- `publication_preReleaseValidationRejectsWithoutGenerationOrOwnershipChange`
- `publication_eachPreReleaseFailureKindMapsToRejectedAndPreservesOldPublication`
- `publication_rejectionPreservesCandidateGoldenAndRuntimeResolutionRows`
- `publication_releaseFixedFunctionPermitsRequestedReplacement`
- `publication_releaseShadersOffRecoversEmptyAndIncrementsOnce`
- `publication_releaseFailedSafeOrPartialQuarantinesOldAndRecoversEmpty`
- `publication_eachPostReleaseFailureKindMapsToRecoveredOffAndVanillaRecovery`
- `publication_recoveredOffLeavesReadyCandidateCallerOwnedAndClosesOld`
- `publication_recoveredOffPreservesDetachedCandidateGoldenAndPriorRuntimeRows`
- `publication_unexpectedBackendRecoveryPreservesDetachedProjectionRows`
- `publication_acceptIncrementsOnce_preReleaseValidationDoesNot`
- `publication_shadersOffInvalidatesEveryPriorGeneration`
- `publication_readyExposesMatchingNonOwningBarrierView`
- `publication_offAndRecoveredOffExposeNoBarrierView`
- `publication_replacedBarrierViewReturnsStaleWithoutGL`
- `publication_barrierViewRequiresRenderThreadAndCannotClose`
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

### 8.6 Coordinated shared-unit architectural checks (future, not executed)

These input → observable-outcome contracts are linked from §3 and staged by §9/§12. Phase4 owns
metadata/selection producer checks, Phase5 owns bind/resolver checks, Phase13 owns texture-source/
animation/noise producers, and Phase7 owns every cross-owner orchestration check below.

| Hook / owner | Input → observable outcome |
|---|---|
| sharedUnit_programSpecificTargets / P13+P5, P7 orchestration | texture.composite.gaux1 raw1D and texture.composite.gaux1.2 raw3D remain in one cell → sampler1D program binds first handle on7, sampler3D binds second on7, exact gaux1 unchanged; no patch or unit reassignment |
| sharedUnit_incompatibleAliasesBeforeBind / P4+P5, P7 orchestration | colortex4 sampler1D plus gaux1 sampler3D → typed SAMPLER_UNIT_TYPE_CONFLICT before provider GL creation and only that provider falls back; invalid published metadata yields zero defensive binds/uploads/draw |
| sharedUnit_effectiveFallbackLayout / P4, P7 orchestration | Missing/failed child and different-layout ancestor → selector, candidates, activation and all Phase6 callbacks name only ancestor layout/state |
| custom_stageExpansionExact / P13+P5 | GBUFFERS/DEFERRED/COMPOSITE keys → exact gbuffers+shadow/deferred/composite+final copies with original discriminator/ordinal; tex is shadow-only and gbuffers receives typed stage diagnostic, not an invented unit |
| custom_fullscreenFixedOverrides / P5, P7 orchestration | Compatible composite/final colortex1 and deferred gaux1 customs → custom objects on1/7 instead of estate backing, all fixed table names reachable |
| sharedUnit_fixedRangeAndShadowAlias / P5+P6, P4 validation | Full legal names with/without direct compatible watershadow → only0–15, depthtex1=11, gbuffers12 unused, conditional shadow4→5 consistently without allocation |
| sharedUnit_exactNameAndOrdinal / P5 | Same compatible name absent/0/9 discriminators in arbitrary insertion order → discriminator9's greatest canonical-list ordinal wins after filtering; different alias winners on one unit → CONFLICTING_CANDIDATES |
| sharedUnit_fullSamplerShape / P4+P5 | Equal dimensions but float/signed/unsigned or shadow/arrayed/multisample differences → no dimension-only compatibility; sampler aggregates stay typed and unsupported |
| binding_staleBeforeMutation / P4+P5+P13, P7 orchestration | Independently stale issuer, registry generation/fingerprint, estate/depth/frame/context/provider/band/layout/policy/publication/resource epoch or closed lease → no binds; publication-ID mismatch precedes registry mismatch in overlay checks |
| binding_absenceVersusIncompatibility / P5, P7 orchestration | Empty custom cell, incompatible candidate, unavailable publication, unknown name or incompatible aliases → distinct diagnostics; compatible base draws with warning, missing backing suppresses only program and discards without flips |
| binding_leaseOwnershipAllExits / P5+P13, P7 orchestration | Bound versus rejection/degradation/backend error and draw/activation exception/suspend/pop/reload/neutralization/teardown → only Bound transfers; exactly owner closes; old lease delays deletion but not stale rejection, borrowed objects never deleted |
| publication_contentIdentity / P13 | Equal key/path/dimensions, changed immutable bytes or effective upload/filter parameters → different texture fingerprint and no identity-only reuse |
| publication_foreignReloadIdentity / P13 | Same dynamic/atlas string after object replacement → reload/object epoch changes identity without GL-name hashing; in-place animation does not republish every tick |
| animation_postVanillaSnapshot / P13 | Reordered sequence/unequal durations after vanilla tick → companion source frame/interpolation/mips match snapshot; reload invalidates and missing hook restores frame0 |
| macro_beforeJcppSameBuild / P13 producer, P7 orchestration | Independent decoded preferences plus preliminary active/capability policy before load → required companion pair drives same-build branches/fingerprints without linked demand; missing non-Off input is INVALID_REQUEST, never undefined-macro fallback |
| noise_signedRecurrence / P13 | Wrapping signed arithmetic/right shift/remainder → xorshift(-1)=253983 and channel(1,1,1) remainder=-115/upload141; unsigned shift/absolute value fails |
| unsupported_noEnumSentinel / P13 | Unknown name versus known out-of-stage name → UnknownSampler+KEY_DOMAIN versus KnownSampler+STAGE_COLUMN, no sentinel/fabricated unit |
| pipeline_textureFailureCompensates / P7 | Texture build/identity/registration failure after Phase4/5 accept → both publishers off, no Active tuple/draw, caller candidates closed and accepted owners retired; ConsumerFailed deliveredCount preserved and borrowed objects not deleted |
| shadow_sharedBindingAndGate / P5, P7 orchestration, P8 adoption | Adopted R7-12/13: exact selector → sixteen applicable rows bind before upload and binding closes once; remaining verification/dependency gate → NotInstalled/unavailable, never four-row success |
| samplerPolicy_invalidBeforeGL / P4 | Null/changing fingerprint/malformed/throwing callback, including empty registry → typed INVALID_SAMPLER_POLICY before all GL; valid empty registry preserves policy identity |
| selection_privateCredentialAndLifetime / P4, P7 orchestration | Public descriptor copy/foreign context fails; legitimate parent selector survives suspend/new activation in same frame but needs fresh physical bindings; frame/replacement/off/close permanently invalidates it |
| samplerLayout_canonicalEvidence / P4 | Repeated equal declarations across V/G/F retain every site; reordered first occurrences or full-type/policy changes alter digest; fallback child identity does not alter provider digest; conflict+unsupported retains both |

## 9. Milestone staging

| Component | Milestone | Architected now / implementation boundary |
|---|---|---|
| Sampler projection/policy/fingerprint, select-once private credentials | `v0.1` | architecture infrastructure; implement after fresh reviews, §8.6 sharedUnit_*, selection_*, samplerPolicy_* |
| Shared shadow binding execution | `v0.2` | Phase8 grants required; §8.6 shadow_sharedBindingAndGate, no old four-row route |
| Typed custom/companion/noise and coherent texture publication | `v0.5` | Phase13 producers/Phase5 binds/Phase7 orchestration; all remaining §8.6 producer/lifetime/compensation hooks; explicit empty earlier publication does not complete v0.5 |
| Nine-identity stage model, multi-occurrence schedule, `PassIndex` 0…99 | `v0.1` | implemented and tested now |
| Classic G6 configuration and full App A catalog | `v0.1` | implemented now; row coverage is cardinality-independent |
| Sparse modern configuration fixture with dormant families | `v0.1` | implemented as data/test, not executed |
| Program-state/declaration adapter, uniform-layout merge, route/attribute validation, fallback resolver | `v0.1` | implemented now; conflicts fail before GL |
| Synchronous materialize/compile/link/validate transaction | `v0.1` | implemented after the §5.4 legacy-geometry dependency gap closes |
| Barrier, bound-uniform lookup/activity capability, alpha/blend lock, fixed Phase 6 participant slots | `v0.1` | interface and P4 mechanics now; P6 implementations later |
| Shadow force-selection branch | `v0.1` | interface/mechanics now; Phase 8 invokes at `v0.2` |
| Pipeline generation and fingerprints | `v0.1` | implemented now; Phase 12 consumes at `v0.4` |
| Per-slot resolution/source-presence evidence projection | `v0.1` | one catalog-ordered handle-free value list serves candidate goldens and runtime manifests (`[D-P4-16]`) |
| Typed virtual-pre descriptors in sparse populations | `v0.1` | D-P4-40 exact optional prelude, pre-first/ascending traversal and empty-index/no-program invariants; P7 forwards unchanged to P5; §8.1 pre/0/5/99 acceptance remains planned |
| `instanceCount` storage/exposure | `v0.1` | stored now; Phase 7 fullscreen and prepared-submission execution at `v0.5`, approved §11.5; no repeated world traversal |
| Debug labels at creation sites | `v0.1` | calls exist; Phase 14 activates backend at `v0.5` |
| `shadowcomp`, `prepare`, `begin`, setup population | `post-v0.5` | no type change, G8/S1 data/wiring |
| Compute compile/dispatch, `_a`…`_z`, images/SSBO/barriers | `post-v0.5` | slots only now; G8/S2 owns semantics |
| Arrays/buffers beyond current Phase 3 limits | `post-v0.5` | registry already unbounded to 99/by `BufferRef`; front-end/estate growth later |
| Shared-context async compiler | `v0.5` | Phase 14/OQ-15, synchronous fallback retained |

## 10. OQ & spike specifications

Phase 4 has no assigned open question in §G10 or its phase specification. The catalog cardinality
and fixed-function facade are resolved upstream. Native source, linked metadata and declared
projections are now granted/adopted; §5.4 retains verification, pinning and draw-conformance gates.

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
| D-P4-14 | Merge verified Phase 3 declared-uniform catalogs before GL and reject same-name structural type conflicts with all attributed sites | Phase 6 needs exact declared types and stages without reopening source; post-link lookup cannot recover optimized-out declarations or source attribution |
| D-P4-15 | Give participants callback-scoped bound lookup plus a retainable operation-free epoch token | Phase 1 lookup requires the private handle, while Phase 6 needs generation-cached locations and between-activation immediate uploads; this capability supplies only those facts and preserves D-6 |
| D-P4-16 | Publish one immutable catalog-ordered per-slot resolution projection, preserving `sourcePresent` independently from `SOURCED/CHAIN/ABSENT/FAILED`, identically on candidate and accepted views | Phase 2's T3/golden grammar and Phase 7's manifest need owner-defined evidence; reconstructing from an optional effective descriptor loses failed, absent, and pre-fallback source facts |
| D-P4-17 | Use the existing virtual `PassDescriptor` as the sole typed `deferred_pre`/`composite_pre` transition input, with explicit flips and no program | It preserves Phase 4's “virtual never masquerades as program” invariant while giving Phase 5 enough typed policy to execute flips and Phase 7 no reason to fabricate a resolved binding |
| D-P4-18 | Publish lossless sampler layouts and validate through Phase5's sole pure fixed policy before GL; retain all conflict/unsupported evidence | RESEARCH B.3 fixed units and F.5 one type per unit/program (`docs/research/v1/RESEARCH.md:1228-1255,1484-1490`); no copied map, source patch or optimized-out pruning |
| D-P4-19 | Select once with a private publication/context credential and activate only the retained binding after Phase5 objects bind | Entire ancestor configuration and stale-use safety require texture and sampler-upload choices to use the same provider; content hash is not generation/lifetime authority |
| D-P4-20 | Adopt current schema-17 catalog-bound materialization and direct Phase 3 program mipmap/vertex projections | IR-03/04/24; same-build state cannot be replaced through a materialize argument; no inferred schema-16 upgrade |
| D-P4-21 | Adopt P1 native configure without pretending P3 preserves native source | IR-04; exact pre-link error transaction, explicit source and review gates |
| D-P4-22 | Count every actual publication independently and limit execution promises to adopted fullscreen repetition | IR-07/18; compensated Ready→Off adds two, non-fullscreen authority remains open |
| D-P4-23 | Adopt source-free golden enrichment by exact same inspection/build request association and detached resolution rows | IR-29; no invented view fingerprint, archive hash or runtime publication |
| D-P4-24 | Receive R-P14→P4-1 explicitly, keep it pending and preserve synchronous compiler | IR-27; conceptual worker ownership is not an adopted callable async API |
| D-P4-25 | Adopt the maintainer's 2026-09-07 adjacent prepared-submission choice, superseding D-P4-22's non-fullscreen gate | RESEARCH requires N total; the maintainer settles the boundary not supplied by published/OSS evidence. P7 policy/P10 adapters/v0.5, P8 one traversal, exact §11.5 semantics. |
| D-P4-26 | Adopt P3 schema18, superseding only D-P4-20's schema17 assertion | Same-build catalog/materialization and effective-provider state remain unchanged; locale/session/source-macro changes enter through the new configuration identity, with no schema17 upgrade. |
| D-P4-27 | Adopt P3 D-P3-68/schema19 and remove the incomplete translator from current compiler/inspection contracts | Supersedes D-P4-20/21/26's old schema/source gates; exact same-build source, attributed catalogs/maps, native API/source precedence and complete fallback remain |
| D-P4-28 | Require P1 cached linked input agreement before READY; publish required effective-provider geometryInput | P1 grants the exact Optional accessor; no private handle, rescan or requested-child inference reaches P7/P10. Detached/source-free identity changes together |
| D-P4-29 | Adopt only the maintainer-approved conditional native-submission adapter | Named geometry decision preserves D-9 and limits conversion to necessary compatibility; P7/P10 own policy, storage and real conformance, P1 owns fullscreen, no parity inferred |
| D-P4-30 | 2026-09-08: adopt P3 D-P3-69 schema20, matching nested IDs and same-load assets identity; consume MaterializedSource-v20 and preserve the actual ninth source-free inspection section | Supersedes earlier current-schema assertions only; §§4.8/4.11/5.3/5.6 preserve native and resolution semantics, no binary acquisition authority or historical PASS promotion |
| D-P4-31 | 2026-09-08: grant R39-3 required `ProgramOwnBuildDisposition ownBuild` immediately after sourcePresent, total §4.6 classification/matrix and `RegistryFingerprint/own-build-v1` cutover | Requested-slot failure survives successful fallback without conflating intentional disablement; P2/P7 receive exact tokens in /3, not inferred failure or registry-only T3 authority. Architecture-only, unverified |
| D-P4-32 | 2026-09-08: adopt coordinated P3 R55 schema21 structural-load success, payload-free ScreenProfileEntry and exact TEXTURE_RECTANGLE mapping | Same-load typed configuration only; preserve D-P3-69 assets/nine-tree meanings, nested schema equality and current materialization identity; no independent property parsing or old-schema upgrade. Unverified |
| D-P4-33 | R32-1: publish positional DrawRoutingSlot and positional-route-v2 registry domain, preserve P3 holes and validate actual backend capacities/duplicate attachments | P5 packs Attachment entries only; P1 D-P1-57 encodes slots without raw sentinels |
| D-P4-34 | R32-2: acquire P1's opaque duration lease before participants; close predecessor before next acquire; invalidate activity before release/failure | Vanilla interception/private bypass/effective observation are P1 mechanisms, P7 registration only; §5 awaits fresh review |
| D-P4-35 | R33 C33-1/2: align §5's activation order with §4.10 and route current ownBuild evidence through P2 `/4` only | D-P4-31's `/3` receipt is historical and superseded; no Java projection, classification, P3 schema or golden-version change |
| D-P4-36 | R34 C34-1: pre-GL P5 initializationAssignments and P1 candidate-only initialization before validate, with exact restoration and safe/fatal failure split | No duplicate map, runtime participant reuse or mixed-type unit-zero validation failure |
| D-P4-37 | R34 C34-2: separate optional debug degradation from duration-lock safe/off and poisoned failure | §6 now matches §4.10 and P1 lease obligations |
| D-P4-38 | Receive P3 D-P3-72 schema22 and MaterializedSource-v22; older numeric receipts historical | Exact-current admission and opaque owner identities, no block parsing or old-object upgrade |
| D-P4-39 | Receive P14 producer flush and acknowledged-disposal/quarantine conditions for optional compiler split | Current synchronous compiler remains; no timeout-based destruction or API self-grant |
| D-P4-40 | R35 C35-1: extend SparseArray with Optional<ProgramSlotId> virtualPrelude, not a second stage occurrence | Exact deferred_pre/composite_pre membership, no-program/empty-index invariants, named-pre/indexed-raster lookups and pre-first ascending traversal close the previously unrepresentable required configurations; §§8.1/11.4.2 carry P5/P7 acceptance |
| D-P4-41 | Receive coordinated P3 schema23 and MaterializedSource-v23 range-capable selector cutover | Supersedes D-P4-38 and earlier numeric receipts; containing/nested/inspection exact-current equality, nine metadata-only trees/projectionVersion1 unchanged, P9 alone resolves registries |
| D-P4-42 | Receive P14/P1 complete object baseline for ordinary fixed-terminal normalization | Existing P5-bind/P4-activate order and failure containment remain; no stale sampler-zero state |
| D-P4-43 | R36 C36-1: require Optional<ProfileName> profileSelection immediately after configuration; evaluate the exact immutable pair before availability/build, consume closed P3 failure, and advance RegistryFingerprint/profile-selection-v3 | Equal-option profiles carry independent disable intent; P7 forwards accepted selection, P2 inspection explicitly uses absent selection, no preview/config mutation or P3 schema/tree change. Virtual preludes and whole-provider fallback remain |

**R32 receiving receipt — 2026-09-08, unverified.** P1 D-P1-57 and P5's coordinated
positional realization are adopted here. P7 must integrate HEAD hooks/effective notifications,
retain existing nested selector semantics and adopt the new before-participant lock ordering.
P2/P7/P5 treat positional-route-v2 as opaque changed identity; no old-object upgrades.
IR-01/final G5.3 remain open; this owner correction is not implementation authorization.

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
2. **RESOLVED UPSTREAM — fixed-function behavior versus facade.** App A's fixed terminals require
   no-program selection, while absent `final` remains a downstream-owned passthrough copy. Phase 1
   now exposes `ShaderService.useFixedFunction()` alongside `use(ProgramHandle)`. Ruling: consume
   the published handle-free verb for fixed terminals; retain the ban on null, magic handles, and
   raw program zero.
3. **Dual-form source API gap closed, verification remains.** P3 D-P3-68 supplied schema19
   native-preserving materialization, now consumed in the current P3 domain under D-P4-32; P1 grants native configuration and cached linked input.
   §4.8 adopts exact source-layout precedence and mismatch failure before publication. No
   complete translator or runtime proof is claimed; conditional draw gates remain in §5.4.
4. **Pintonium per-pass inventory versus current contract.** PD §3.3 includes framebuffer,
   resolved flip snapshot, per-buffer blend, and compute companions. Phase 3/App F.7 publish only
   the current global program state. Ruling: use the inventory as a cross-check and assign/reject
   fields according to current contracts, not by imitation.
5. **Phase 12 consumption versus dependency graph.** The Phase 4 assignment says Phase 12 depends
   on generation; §G5.1 omits Phase 4 from Phase 12 dependencies. Ruling: expose as assigned and
   request graph correction.
6. **Phase 4-required projections granted, unverified.** Phase 3 §0.57/§5 now directly
   publishes per-program mipmaps/vertices to Phase 4; §§4.7/4.9/5.3 adopt them without a
   Phase 5/10 dependency. Fresh owner verification remains due.

### 11.4 Hand-offs and open items

- **Phase 5:** use `CompiledRegistryCandidate.view()` for non-owning pre-publication derivation and
  validation, resolve symbolic `AllUsedBuffers`, realize FBO routing and ping-pong sides, apply
  virtual-pre flips from the unchanged typed `PassDescriptor` through
  `applyVirtualTransition(frameId, pass)`, and never store foreign/FBO handles in Phase 4 state.
  Do not close, publish,
  unwrap, or assign a generation to the candidate view. It may retain the detached metadata
  snapshot after candidate close or transfer, but must not treat it as live publication state.
- **Phase 6:** implement the three ordered participants against the effective
  `ProgramUniformLayout`; preserve the “every activation refreshes” rule; cache returned locations
  only by `ProgramUniformCacheKey`; retain only the activity token for immediate signals; and
  surface per-uniform isolation results without retaining access or receiving a handle.
- **Phase 7:** map hook phases to exact logical slots and gbuffers bands; execute pass arrays,
  scale, mipmaps, fixed/passthrough terminals, fullscreen and approved prepared-submission `countInstances`;
  use the candidate view for pre-publication composition/validation while retaining ownership of the opaque
  candidate. A retained snapshot remains metadata-only across rejection, recovery, close, or
  accepted transfer; reacquire `current()` for publication state. Quiesce before all rebuilds
  and compensate shaders-off on failure, never restore an old texture-incoherent registry.
  Copy resolutions directly, pass virtual descriptors unchanged and preserve the same issued
  selection across snapshot/lease/bind/activate and nested logical-scope restoration.
  Consume the required effective geometry field under §5.1/R10-5, clear on FixedFunction and
  use the same selected provider through nested restoration; adopt P1-owned fullscreen policy.
- **Phase 8:** consume Phase 7's selected root shadow/context under adopted R7-12/13;
  never resolve again or use four-row binding; real slot remains gated on remaining owner reviews.
- **Phase 10:** configure fixed attribute locations without renumbering; consume geometry only
  through P7's authenticated handoff. Conditional native conversion follows the approved §4.9
  boundary at v0.1 base/v0.3 extended, preserving v0.5 adjacent repetition; no source rescan.
- **Phase 12:** invalidate genuinely program/generation-derived caches by actual generation
  inequality, directly or through Phase 7's final-outcome publication. Configuration-keyed
  presentation/profile/selection state remains keyed to configuration, not a fictional +1 counter.
- **G8/S1:** populate dormant stage families without changing registry structure.
- **G8/S2:** define compute sources, work groups, access/barrier sets, images/SSBOs, and execution;
  do not infer them from the placeholders.
- **Phase 14:** R-P14→P4-1 is received/pending in §5.7, not adopted. Supply the exact prepared
  batch/result/cancellation/visibility/cleanup owner amendment and OQ-15 evidence before async;
  no P7 suspension permission alone changes P4's synchronous contract.
- **Phases 2/3:** §5.6 adopts inspection enrichment from the same exact configuration/build,
  unchanged detached resolutions and bounded verified-archive provenance. No compiler rows on
  ShadersOff, no source-bearing golden input and no publication solely for inspection.
- **Current geometry closure:** P3 source API and P1 configure/cached-link projection are granted
  and adopted; P4 publishes R10-5. Fresh owner/receiver reviews, P1 jcpp pin/closure checks,
  native/fullscreen real-context coverage, conditional adapter conformance and final integration
  remain gates. IR-01 stays open; no BUILD clearance or G6 parity result is implied.

#### 11.4.1 R39-3 / R55 receiving receipt — 2026-09-08

Owner grant D-P4-31 is complete at the architecture surface: §5.1 incorporates §4.6's total
matrix and immutable required ownBuild field; §5.6 copies the same-request row including it,
and §4.11 advances only the P4 registry domain for this evidence change. P2's golden adapter
and P7's `/4` run-manifest serializer must retain the exact enum, including CHAIN+DISABLED
versus CHAIN+FAILED and FAILED+NO_SOURCE. Neither reconstructs it from logs or descriptors.
P2 retains its separately versioned golden/1; this does not authorize omission of the required
owner field or any `/1`–`/3` manifest alias. Other consumers of registry identity invalidate old caches;
provider uniform/sampler identity and fallback selection semantics are unchanged.
D-P4-32's schema21 receipt in §5.3 is a separate P3-owned configuration cutover, including
nested IdMappingInput and same-request inspection; assets retain D-P3-69 semantics.
Original reviews remain historical, and fresh whole-document producer/receiver reviews,
IR-01 and final integration remain required. No implementation clearance or PASS is claimed.

#### 11.4.2 C35-1 / schema23 receiving receipt — 2026-09-08

D-P4-40 is owner-designed/unverified. P5 must derive planned virtual membership from the same
candidate `StageRegistry` sparse prelude and accept only its exact contained descriptor through
`applyVirtualTransition(frameId,pass)`; altered/raster/missing substitutes remain rejected.
P7 must traverse each single DEFERRED/COMPOSITE step pre-first, then ascending populated indices,
forward pre unchanged exactly once before raster execution, and never synthesize a pre or select/
activate it. Both receivers must carry §8.1's pre/0/5/99, pre-only, absent-pre and illegal-member
boundaries in their architecture acceptance plans. Main owns the P7 receipt/integration update;
the P5 owner must receive this contract separately. No new hook-health IDs or count changes arise.
D-P4-41 receives containing/nested/inspection schema23 and MaterializedSource-v23, while all nine
metadata-only trees and projectionVersion1 remain intact. No registry parser is added outside P9.
These are architecture obligations, not completed receiver verification; fresh whole-document
owner/receiver review and final integration remain due. Historical R35 findings/verdict and
N35-1's narrower corroboration are preserved; no fresh PASS, implementation or runtime clearance.

### 11.5 Requested upstream changes

- **GRANTED — 2026-07-28 maintainer correction.** `docs/research/v1/RESEARCH.md` now states the
  60-row Appendix A.1 count, and `docs/design/v2.0-RC3/DESIGN.md` now uses cardinality-neutral
  Phase 4 wording. Every named row remains; §4.5 and §8 retain equality/coverage tests without
  making the corrected count behavioral.
- **GRANTED — Phase 1 §0.15.**
  `docs/phase1/v14/PHASE_1_DOC.md:3324` publishes
  `ShaderService.useFixedFunction()`, and
  `docs/phase1/v14/PHASE_1_DOC.md:4220-4226` publishes its distinct
  `shaders.useFixedFunction` recorder/replay semantics. Review 20's literal PASS at
  `docs/phase1/reviews/PHASE_1_REVIEW_20.md:61`–`:75` verified the §0.19/§0.20-era surface
  historically; per P1's own §G1.3 status (P1 §0.35), current P1 bytes carry later §5 amendments
  and are not verified until a fresh whole-document review returns literal PASS, with the
  post-Review-20 grants this phase consumes tracked as owner-granted/unverified in §5.2/§5.4.
- **GRANTED AND VERIFIED — Phase 5 candidate-view clarification.** Section 5.1 publishes the
  existing `CompiledRegistryCandidate.view()` as the non-owning, generationless pre-publication
  `ProgramRegistryView` for Phases 5 and 7 without exposing registry/handle access or weakening
  candidate ownership, opacity, or compiler provenance. Because this grant changed binding §5,
  round fifteen supplied the required fresh literal PASS at
  `docs/phase4/reviews/PHASE_4_REVIEW_15.md:59`–`:71`.
- **GRANTED LOCALLY — Phase 6 uniform-layout and bound-access request.** Sections 2, 4, and 5 now
  merge Phase 3's verified final declaration catalogs into an attributed, handle-free effective
  `ProgramUniformLayout`, reject structural type conflicts before GL, and give the fixed three
  participants callback-scoped `BoundProgramUniformAccess` plus a retainable non-operational
  activity token. Locations are generation-scoped and no `ProgramHandle` crosses the boundary.
  A fresh Phase 4 review is owed because this grant changes binding §5.
- **GRANTED LOCALLY — Phase 2 R10 / Phase 7 R7-4.** Sections 2, 4, 5, 8, 9, 11, and 12 expose
  catalog-complete `ProgramResolutionProjection` rows with independent source presence on both
  candidate and runtime views. Phase 2 and Phase 7 copy the same values without inference.
- **COORDINATED LOCALLY — Phase 7 R7-2.** Phase 4's half is the existing immutable virtual
  `PassDescriptor`, now bound explicitly as the no-program typed transition input. Phase 5 owns
  `applyVirtualTransition`; Phase 7 passes the descriptor unchanged.
- **IR-18 — maintainer-approved prepared-submission contract (2026-09-07).** Geometry repetition
  is already required by `docs/research/v1/RESEARCH.md` §3.2 (N total renders), not conditional
  on discovering a convenient pack. After the bounded evidence below was reported, the
  maintainer explicitly chose **Repeat prepared submissions**: for A and B with N=2,
  `A0,A1,B0,B1`, not whole-subpass `A0,B0,A1,B1`. This settles architecture by explicit
  authority, not a fabricated reference-behavior claim. No whole-world/Forge traversal repeats.

  **Evidence and limits.** The published [OptiFine author specification](https://raw.githubusercontent.com/sp614x/optifine/master/OptiFineDoc/doc/shaders.txt),
  “Vertex Shader Configuration”, says geometry is rendered several times when countInstances>1;
  its common-uniform `instanceId` row identifies zero as original. It names no submission,
  display-list or traversal boundary. Its “1-N = copies” shorthand is not authority for N+1 draws
  against RESEARCH's explicit N total. The [uniform documentation](https://optifine.readthedocs.io/shaders_dev/uniforms.html)
  repeats the shorthand and supplies no missing ordering rule.
  Scoped independent licensed checks: Angelica
  [ProgramDirectives](https://github.com/GTNewHorizons/Angelica/blob/37a10eee3fc3d986475269b283b072b6adf7fbdc/src/main/java/net/coderbot/iris/shaderpack/ProgramDirectives.java)
  and [Iris ProgramDirectives](https://github.com/IrisShaders/Iris/blob/1.21.1/common/src/main/java/net/irisshaders/iris/shaderpack/properties/ProgramDirectives.java)
  expose no countInstances handling in those files. Angelica
  [DisplayListVBO.SubVBO.render](https://github.com/GTNewHorizons/Angelica/blob/37a10eee3fc3d986475269b283b072b6adf7fbdc/glsm/src/main/java/com/gtnewhorizons/angelica/glsm/recording/DisplayListVBO.java)
  prepares/binds and issues one VAO draw; its
  [TessellatorStreamingDrawer.draw](https://github.com/GTNewHorizons/Angelica/blob/37a10eee3fc3d986475269b283b072b6adf7fbdc/glsm/src/main/java/com/gtnewhorizons/angelica/glsm/streaming/TessellatorStreamingDrawer.java)
  prepares/uploads and resets the tessellator, not a countInstances loop.
  Angelica's [license](https://github.com/GTNewHorizons/Angelica/blob/37a10eee3fc3d986475269b283b072b6adf7fbdc/LICENSE)
  is LGPL-3.0. The checked Pintonium `reference-src/Pintonium-main/{common-shaders,forge122,modern}/src`
  contain no `countInstances`/`instanceId` matches. These bounded negative findings supply no
  interoperable replay-order oracle; no source code, renderer replacement or transformation
  library is adopted.

  **Approved owner/executor contract:** repeat each already-prepared vanilla geometry submission
  N times with IDs `0..N-1`, P7 policy with P10's existing draw-hook ownership, at v0.5.
  This is the explicitly approved timing, consistent with RC3:1537–1538, not inferred from
  the fullscreen loop. P8 owns one unchanged shadow traversal. The adapter is private,
  synchronous and mod-side beneath existing authenticated scopes; no new public renderer API.
  The binding receiving obligations are:
  - Count comes from the already-authenticated effective P4 selection, never the requested
    child's metadata. Missing/fixed-function execution is one ordinary draw, not inherited
    repetition from an unavailable shader; virtual passes have no geometry.
  - P7 requires its live accepted main scope, or its currently Valid shadow execution plus
    supplied root-shadow selection/context. P8 never opens main gbuffers scopes inside shadow.
    Revalidate current frame/generation/selection/activity before copies; no public equality,
    saved handle, stale token or retry with another provider authorizes drawing.
  - P10's VBO/client-array routes repeat only the final native submission after pointer setup
    and before teardown. Build/upload/reset and higher-level entity/layer calls run once.
    Display-list compilation records once, without count expansion or instanceId uploads;
    prepared geometry-list playback is the submission repeated N times under the live uniform.
    A private dynamic-extent guard excludes nested lower wrappers, so each draw is multiplied
    once, never N². Lists must preserve the same effective program and replay-stable geometry/
    state; a mixed stateful list is not silently treated as repeatable. Hook coverage must
    establish the supported vanilla paths before implementation claims; no renderer replacement.
  - Preserve original draw ordering outside the chosen unit, vertex/attribute pointers, matrices,
    textures, alpha/blend/depth state and all IDs/colors. Only instanceId changes between copies.
    Use P6's existing event, not raw uniform handles; nested scope exit restores its predecessor
    and outermost exit restores zero before program release. A loop must not replay ticks,
    chunk scheduling, Forge callbacks/predicates, entity counters, uploads, clears, depth copies,
    pass flips or mipmap generation.
  - P8 keeps SOLID→CUTOUT_MIPPED→CUTOUT, configured clouds, Forge pass0, one depth split,
    optional translucent terrain, Forge pass1 and postprocessing, with prior-pass/counter
    restoration unchanged. Main scopes remain bypassed; shadow admission is not main admission.
  - Before mutation, reject stale admission without draw. On a copy failure stop remaining
    copies, restore nested instance/state in finally, and use the existing P7 frame-abort/off
    or P8 abort/neutralization/result containment; never report a partially repeated pass as
    complete or restart at zero. Failure after mutation is not a mutation-free rejection.
    Preserve P6's existing per-uniform isolation semantics rather than inventing an upload result.

  **Acceptance scenarios (planned, not run):** N=1 and N=3 exact total/IDs; overlapping A/B
  observes adjacent ordering; fallback count differs from requested child; nested restoration
  resumes outer ID; stale main/shadow credentials draw nothing; second-copy failure does not draw
  the third; VBO/client-array/display-list playback totals match with one build/reset and no
  count captured during list compilation; shadow Forge predicates/counters, split/clear/flip/
  mipmap counts remain identical to N=1. P7/P10 receiving contracts and P8's unchanged-traversal
  clause must match this decision. Fresh owner/receiver verification remains due; this is not
  implementation proof, an extension API, or a changed v0.2 shadow exit milestone.
- Re-verify the adopted complete current-schema/native-configure/linked-agreement path from §5.4;
  retain source/compiler/draw evidence separately, not a still-missing source API.
- Add Phase 4 to Phase 12's declared dependency list, or state the generation is consumed
  indirectly through a declared Phase 7 interface.
- **GRANTED — verification target.** `verification/targets/phase-4.json` is anchored to RC3 and
  drives the separate review/fix-up loop.

- **Coordinated seam, not verification:** Phase5 supplies the sole policy/resolver before estate
  creation; Phase4 exposes §2/§5 metadata/selection; Phase13 produces target-specific candidates.
  Phase 6 R7-10/11 and Phase 8 R7-12/13 grants are owner-designed/unverified and receiver-adopted/
  unverified. Phase 1 packages/native configure and Phase 3 companion/lossless/direct projections
  exist, including current-schema native source and P1 jcpp admission; pin/closure, draw evidence and fresh reviews remain open. Adopt
  `docs/decisions/U1_TEXTURE_SAMPLING.md` for U1: no required typed suffix API or key-suffix
  execution; retain existing typed texture sources, numeric discriminators and sidecars.

## 12. Implementation checklist

1. **[v0.1]** Create pure stage identity/band/index/access types; test bounds and immutability.
2. **[v0.1]** Implement the G6 and full-superset configuration constructors; run all
   `classicSchedule_*`, `modernSchedule_*`, sparse-family and §8.1 `sparsePrelude_*` boundaries,
   including unchanged P5 delivery of pre before 0/5/99 within one deferred/composite occurrence.
3. **[v0.1]** Encode every Appendix A.1 row declaratively, including virtuals and `<none>`;
   run row-coverage and declared-versus-enumerated equality tests without fixed-size allocation.
4. **[v0.1]** Implement catalog validation and memoized backup resolution; run every
   `fallback_*`, `virtualPre_*`, and `resolutionProjection_*` test; prove candidate/golden/runtime
   equality, both `CHAIN` values of `sourcePresent`, total failed-ancestor projection for missing
   and disabled children, masking by a later successful ancestor, and non-empty sanitized detail
   for every projection-eligible candidate-build failure stage; prove runtime barrier/publication
   failures cannot alter candidate/golden/runtime rows.
5. **[v0.1]** After the §5.4 projection grant is verified, implement the Phase 3 adapter for
   evaluated program state, routing, mipmaps, attributes, instance count, source keys,
   declared-uniform catalogs, and fingerprints. Require explicit non-null profileSelection
   immediately after configuration at every compiler construction; evaluate the exact pair once
   before availability/materialization/allocation, handle closed InvalidState as §4.12 defines,
   and run §8.1 profileSelection_* boundaries including P7 selection-only changes and P2 absent
   inspection association. Commit exact selection/evaluated state in profile-selection-v3 and
   preserve immutable resolution evidence, virtual flips and provider provenance; merge the exact
   effective `ProgramUniformLayout`, reject attributed type conflicts before GL, and prohibit
   rescanning/reopening by package/API tests.
6. **[v0.1]** Implement fixed-terminal actions through Phase 1's verified
   `ShaderService.useFixedFunction()` contract and assert the distinct
   `shaders.useFixedFunction` recorder event; never encode the terminal as null, a magic handle,
   or a raw integer.
7. **[v0.1]** After fresh owner/receiver verification, implement current-schema native/core materialization,
   configure/link/cached-input comparison before validate, effective geometry bundle identity and
   §8.2 checks; coordinate P7/P10 conditional submissions before claiming affected native support.
8. **[v0.1]** Implement pure build planning/materialization and deterministic V/G/F ordering;
   test dimension no-merge and unavailable-source fallback.
9. **[v0.1]** Implement render-thread compiler ownership ledger through Phase 1 `ShaderService`;
   script compile/link/validate failures and prove no leak/use-after-delete.
10. **[v0.1]** After the §5.4 projection grant is verified, bind only declared extended
    attributes at 10/11/12 with exact capability gates; run negative Pintonium-numbering test.
11. **[v0.1]** Implement immutable compiled registry, opaque compiler-origin product, idempotent
    close, fingerprints, and transaction-only publication. Prove that
    `CompiledRegistryCandidate.view()` returns an immutable detached metadata snapshot with no
    generation, close, handle, private-registry accessor, or provenance credential; retained
    snapshots remain safe after candidate close, rejection/recovery, and accepted transfer.
    Candidate ownership and compiler-origin authentication remain unchanged until
    composition/publication accepts the opaque candidate itself.
12. **[v0.1]** Implement generation publication for registry and shaders-off replacements; run
    cache inequality tests. For pre-release rejection, release failure, and later unexpected
    backend failure, capture every existing detached candidate/golden/runtime resolution list
    before publication and assert it is unchanged and value-equal afterward. `RecoveredOff`
    remains an empty publication and creates no replacement projection rows.
13. **[v0.1]** Implement the authoritative per-frame context source and its complete
    source/epoch/kind/stage/band/shadow rejection matrix, including rejection of caller
    implementations and release-kind `shadowPass() == false`, then barrier
    select-once/Phase5-object-bind/authenticate/token-invalidate/close/use/lock/participant order, callback-scoped private-handle
    lookup, generation/layout/provider cache keys, and the retainable operation-free activity token
    with the package-private bootstrap composition; run recorded state and full invalidation tests.
14. **[v0.1]** Implement the Phase-4-owned composition facade and immutable barrier factory;
    prove cross-package Phase 7 wiring, compiler-product and production-bundle provenance,
    factory-issued publication-candidate provenance, external-registry/synthesized-participant/
    arbitrary-barrier rejection, bootstrap confinement, handle-free results, invalid/repeated-call
    behavior, idempotent pre-transfer barrier-candidate close, harmless post-transfer close, fixed
    order, Phase 11 handoff, and publication ownership lifecycle.
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

22. **[v0.1]** Implement lossless sampler merge/all-band policy/evidence/framing, empty policy identity
    and private selection issuer/validation/lifetime; exercise §8.6 sharedUnit_incompatibleAliasesBeforeBind,
    sharedUnit_effectiveFallbackLayout, sharedUnit_fullSamplerShape, samplerPolicy_invalidBeforeGL,
    samplerLayout_canonicalEvidence and selection_privateCredentialAndLifetime.
23. **[v0.1/v0.2/v0.5]** After owner grants/fresh PASS, integrate one exact selection through Phase5
    bind and Phase6 upload, Phase7 discard/closure/coherent publication and Phase13 candidates.
    Exercise every §8.6 hook at its §9 milestone, including binding_staleBeforeMutation,
    binding_leaseOwnershipAllExits, pipeline_textureFailureCompensates and shadow_sharedBindingAndGate.
    Until grants, keep honest typed gates and never claim empty texture publication completes v0.5.
---

§5 changed in this coordinated rebuild; Review 40 (2026-09-08) then audited the full document: PASS-WITH-CORRECTIONS with no §5 impact. All eight corrections are applied in this revision and note N1 is dispositioned (§0.42; resolutions recorded in `docs/phase4/reviews/PHASE_4_REVIEW_40.md`). With all corrections resolved and no §5 change outstanding, Phase 4 satisfies the §G1.3 fix-up verification condition. v1 retained; no directory roll.
