# Schmaloogium — Phase 3: Pack front-end — Architecture

## 0. Header

**Phase:** 3 — Pack front-end: ingestion, preprocessing, and configuration model
**Date:** 2026-08-03 · **Last revised:** 2026-09-08 (§0.67)
**Governing design:** `docs/design/v2.0-RC3/DESIGN.md`, Part I §G0–§G12 and the Phase 3
specification only. RC3 governs this phase only; this document does not change the Phase 1 or
Phase 2 governance pins.

### 0.1 Inputs actually read

Read in the assigned order:

1. `docs/design/v2.0-RC3/DESIGN.md` Part I §G0–§G12 (lines 92–1109) and the Phase 3
   specification (lines 1316–1470).
2. `docs/design/v3/DESIGN.md` selectors `part_i` (§G0–§G12), `mandatory_template` (§G9),
   `target_spec` (Phase 3), and `doc_gate`, read as the current verification override authority.
   This additional read does not complete §G0.4 adoption or change the RC3 governing pin.
3. `docs/research/v1/RESEARCH.md` §0, §1, §3.1–§3.3, §3.5, §3.6.8, §3.7, §4.1 steps 2–3,
   §4.7, §7.5, §11 row OQ-7, Appendix A.3, Appendix F in full, and Appendix H. Section 3.6.8
   was additionally read to assess the tag-shim/priority premise and record D-P3-47.
4. `docs/phase1/v14/PHASE_1_DOC.md` §5, then the named supporting material needed here:
   §2.1–§2.4 for module/package placement and §4.9 for logging, diagnostics, and debug flags.
   No Phase 1 review was needed: the assigned dependency status says
   `docs/phase1/reviews/PHASE_1_REVIEW_15.md` is literal PASS.
5. `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §7, §12, and §17 rows B1–B3/B12.
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
6. `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties` in full, followed by only
   the directive, ID-mapping, Standard Macros, and Options portions of
   `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`.
7. `docs/reference/oculus/v1.0/OCULUS_DESIGN.md` §§3–4 and §§12–17.

The original build read no input outside its assignment. This fix-up additionally used the v3
override selectors recorded above as design authority and RESEARCH §3.6.8 for the tag-shim/
priority premise. Neither provenance statement includes a session transcript, a path under
`docs/**/chatlogs/`, a root-level `*.txt`, or `SHADER_ENGINE_IMPL.md`.
The Round 52 correction additionally read only the cited OptiFine G6 screen
resolver/parser/expansion sites as §G7-qualified behavioral observation; it adopts no implementation
structure or identifiers. The Pintonium search excluded the stale refactor artifact and the §G11
prohibited transformation boundaries. Oculus was consumed through its gated report only.

### 0.2 Dependency PHASE docs consumed

`docs/phase1/v14/PHASE_1_DOC.md` is the sole dependency contract. This design consumes only what
its §5 exposes: the three-module layout and seam constraints, `GLCapabilityProfile`, the fixed
logging and loader-neutral diagnostic interfaces, the debug-flag namespace, and the SPDX/third-party
notice mechanism. Phase 3 emits its manifests and fixtures to Phase 2's runnable-before-renderer
harness; it does not consume Phase 1's `:conformance` extension point. A missing build-contract item is
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

### 0.26 Round 23 fix-up

Section 5 now owns the complete binding `ProgramStateModel` and `ResourceRequirements` shapes and
consumer-visible semantics; their detailed-design counterparts are non-competing elaboration.

### 0.27 Round 24 fix-up

Section 5 now closes the evaluated program-state views and every resource-requirement baseline.
Round 23 produced §0.26; round 24 reviewed that surface and required this correction.

### 0.28 Round 25 fix-up

Shadow FOV now has one projection-safe producer and consumer domain, and optional absence wording
covers both orthographic shadow projection and absent legacy geometry.

### 0.29 Round 26 fix-up

The closing verification status now records the Round 25 review and resulting §0.28 surface.

### 0.30 Round 28 fix-up

Section 5 now closes the custom/noise-texture publication algebra and deterministic ordering.

### 0.31 Round 29 fix-up

Texture coexistence now preserves the published key algebra, and the configuration schema is 3.

### 0.32 Round 30 fix-up

The jcpp request now distinguishes missing build/seam work from the existing notice mechanism.

### 0.33 Maintenance addendum (Phase 11 custom-expression publication contract — 2026-08-03)

RESEARCH defines the two declaration families and their upload/intermediate distinction at
`docs/research/v1/RESEARCH.md:1492-1496` ("`uniform.<float|int|bool|vec2|vec3|vec4>.<name>=<expr>`
uploads on program change" and "`variable.<type>.<name>=<expr>` defines reusable intermediates").
Phase 11 then requested the missing dependency boundary at
`docs/phase11/v1/PHASE_11_DOC.md:1184-1203`: its request-time "Before implementation, request a
Phase 3 fix-up to publish" prose has since been superseded by P11 §5.2's direct consumed-contract
statement at those same coordinates, still requiring publication of the closed
enums/record/accessor with "The list must be immutable, source ordered, lossless after Properties
unescaping, retain duplicates" and participation in fingerprinting.
Sections 1–5, 8–9, 11, and 12 now publish that boundary as closed `CustomExpressionKind` and
`CustomExpressionType` enums, a source-attributed `CustomExpressionDecl`, and the immutable
source-ordered `PackConfiguration.customExpressions()` projection. Every valid occurrence,
including an exact-key or same-name duplicate, remains present for Phase 11 diagnostics, and the
ordered collection has an explicit canonical configuration-fingerprint encoding.

Round 31 recorded "# PASS" with "Counts: blocking=0; corrections=0; notes=0" for the §0.32
surface (`docs/phase3/reviews/PHASE_3_REVIEW_31.md:48-56`). This addendum changes binding §5, so
Phase 3 is again **not verified** until a fresh review returns literal PASS. The version directory
remains `v1` while that review loop is open.

### 0.34 Round 32 fix-up

Every successful materialization now exposes its exact final transformed GLSL text as a `String`
paired with the published source map.

### 0.35 Round 33 fix-up

The closing verification ledger now records the Round 32-created §0.34 surface and this fix-up.

### 0.36 Round 36 fix-up

Round 36 closes the clear-color, gdepth, routing, texture-key, public-binding, and Phase 2 hand-off
corrections. The resource algebra now distinguishes clear-color absence and all-used routing, gdepth
has mandatory RGBA32F precedence, and filter/wrap key suffixes are stripped without sidecar conflation.
Phase 3 emits the Phase 2 harness hand-off rather than consuming `:conformance`; the pre-Round 37
surface used schema 4. The version directory remains `v1` pending a fresh whole-document review.

### 0.37 Round 37 fix-up

Round 37 applies the revision-pointer, program-family applicability, public configuration/codec,
and ID-map provenance corrections. The published resources and configuration contracts now use
schema 5; the version directory remains `v1` pending a fresh whole-document review.

### 0.38 Round 38 fix-up

Round 38 closes the published dimension, source-catalog/materialization, tooltip-marker, and legacy-geometry contracts and corrects the ambiguity and shadow conformance labels. The schema remains 5; the §5 interface changes require a fresh whole-document review.


### 0.39 Round 39 fix-up

Round 39 closes the public discovery/load, compatibility, texture-stage, identity, and ID-map
value contracts and replaces the undefined macro-family label with named payload families; schema 5 remains unchanged and §5 requires fresh verification.


### 0.40 Round 40 fix-up

Round 40 closes durable filesystem-candidate resolution and persistence targeting, legal opaque-token issuance, exact option operations and standard option macros, and the scale/flip program-state domains. The program-state meaning changes advance the configuration schema to 6; Phase 3 remains unverified pending a fresh whole-document review.

### 0.41 Round 41 fix-up

Round 41 bounds persistence targets, connects persisted values to atomic load, legalizes capability issuance, stages companion-map macros, closes scale/flip family semantics, corrects provenance, and advances the configuration schema to 7.

### 0.42 Round 42 fix-up

Round 42 adds public service acquisition and exact program-state declarations, restores include-first preprocessing and direct-name persistence, and completes profile provenance.

### 0.43 Round 43 fix-up

Round 43 closes profile-aware evaluation, typed target/safe-file acquisition, bundle-domain construction, and lowercase-PNG validation; the texture meaning change advances the schema to 8.

### 0.44 Round 44 fix-up

Round 44 labels the lowercase-PNG rule as D-P3-38's interpretation, closes catalog-bound option state and invalid discovery generations, and defines one staged-root projection to executable program keys. The option/source meaning changes advance the schema to 9.

### 0.45 Round 45 fix-up

Round 45 canonicalizes file-level source identity, bounds discovery retention, closes the resource
leaves and texture-sidecar projection, maps staged compute-source recognition, and advances the schema to 10.

### 0.46 Round 46 fix-up

Round 46 makes plain RGBA representable as a distinct attachment-format variant, keeps every sized
format explicit, forces `gdepth` to explicit `RGBA32F`, advances the schema to 11, and repairs the latest-surface hand-off and ledger.

### 0.47 Round 47 fix-up

Round 47 completes the resource fingerprint codec and global persistence result matrix, preserves
positional `DRAWBUFFERS` none slots, advances the schema to 12, and records the unresolved
texture-sampling suffix authority gap without certifying destructive normalization.

### 0.48 Round 48 fix-up

Round 48 closes global-option validity, decision and provenance gaps without changing schema 12,
and its binding §5 completion requires a fresh whole-document review.

### 0.49 Round 49 fix-up

Round 49 closes numeric option-macro serialization, provisional tag syntax, the binding option
projection, and the custom-texture nominal algebra; schema 13 and §5 require fresh verification.

### 0.50 Round 50 fix-up

Round 50 closes numeric-tag boundaries, finalized-option runtime use, load-failure diagnostics, and exact standard A–F macro values; schema 13 remains current and the §5 changes require fresh verification.

### 0.51 Round 51 fix-up

Round 51 closes runtime identity encoding and compatibility ordering, null-reporter delivery, and exhaustive load-failure classification; schema 13 remains current and the §5 changes require fresh verification.

### 0.52 Round 52 fix-up

Round 52 binds compatibility fingerprinting, edition accessors, macro-override actions, expanded-slot screen widening, and Phase-3-authored capability vectors. The changed screen meaning advances the schema to 14 and requires fresh §5 verification.

### 0.53 Round 53 fix-up

Round 53 aligns the canonical schema declaration, binding row, and boundary tests with schema 14 and narrows D-P3-54 without changing the macro action matrix.

### 0.54 Round 54 fix-up

Round 54 closes discovery-diagnostic encoding, load-failure diagnostic payloads, and the canonical
pack-buffer spelling projection, and brings the closing ledger through this §0.54 surface. Schema
14 remains current; the binding §5 changes require a fresh whole-document review.

### 0.55 Downstream-request addendum (Phase 13 R1 companion option macros — 2026-09-07)

This maintainer-authorized architecture-only amendment grants the Phase-3-owned input requested
at `docs/phase13/v1/PHASE_13_DOC.md:846-852` (§4.1.6): the new typed pair belongs "immediately
after engineOptions"; Off short-circuits, and §4.1.6's following paragraph keeps the separate
materializer contribution "Empty/DefineCenterDepthSmooth, not a general macro bag". Active §§2–5
now carry that immutable pair through load-time shader analysis, the published option-macro
state, every same-build materialization, and both fingerprints.
The pair replaces Phase 3's former engine-option/renderer-availability emission gates; it does not
add a second macro producer. D-P3-58 records this cutover. The changed nested `MacroConfiguration`
shape and meaning advance the configuration schema from 14 to 15.

Inputs read for this amendment: `docs/MOVES.md`; this Phase 3 document's affected contracts,
testability plan, staging, and decision history; RC3 Part I and Phase 3 specification;
`docs/research/v1/RESEARCH.md` §§0–1 and §3.5; the shipped
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:653-661` option meanings; and Phase 13
§0's governing header, §2.2, §4.1.1, §4.1.6, §5.3 R1, with its own v3 Phase 13 specification. Phase 13 was read
as the expressly assigned downstream request, not as a verified dependency or a new Phase 3
governing authority. RC3 remains this document's pin; all earlier addenda remain historical.

**Current §G1.3 status:** §5 changes require fresh whole-document verification. This amendment
does not perform it, grant other owners' dependencies, or claim downstream adoption; §5.4 records
the remaining gates. No code, review edits, builds, tests, verification runs, or directory rolls
are part of this amendment.

### 0.56 Downstream-request addendum (Phase 13 U1 lossless texture declarations — 2026-09-07)

This maintainer-authorized architecture-only amendment answers the owner-side portion of Phase
13's U1 request. Its request-time wording quoted the older Phase 3 boundary as "stripped and
ignored" (`docs/phase13/v1/PHASE_13_DOC.md:531-541` and `:1647-1653` as of 2026-09-07); after
Phase 13's subsequent revisions — including its adoption of D-P3-67's documented-mechanism
boundary — that phrasing no longer occurs anywhere in current Phase 13, and those coordinates
are retained here as dated request history, not reused as current pins. Round 47 already removed
that destructive normalization; the pre-amendment active §§3.2/4.8/5.1 instead diagnosed and
omitted undocumented keys. Neither rule supplied a lossless sampling-request boundary.

RC3 remains governing. Its Phase 3 specification calls stripping/ignoring a gap
(`docs/design/v2.0-RC3/DESIGN.md:1404-1412`); its Phase 13 requirement says "ours must honor them"
(`docs/design/v2.0-RC3/DESIGN.md:2441-2443`), independently matching Phase 13's v3 authority
(`docs/design/v3/DESIGN.md:2476-2478`). RESEARCH remains supreme: Appendix F.5 specifies
`[.0-9]` and "`.mcmeta` sidecars set blur/clamp"
(`docs/research/v1/RESEARCH.md:1484-1490`). Neither that text nor the shipped grammar
(`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:103-119`) defines property-key
filter/wrap tokens or their values. Absence of that grammar is not proof that such syntax cannot
exist, nor permission to drop DESIGN's honoring requirement.

Active §§2–5 now retain every active, decoded `texture.*` occurrence before key collapse in a
closed, immutable, attributed declaration list, independently of the existing executable spec
projection. Unknown suffix requests are retained with an explicit unresolved disposition, never
stripped, merged into a base binding, or reconstructed downstream. Schema 16 records the new nested
publication shape. `.mcmeta` path retention and Phase 13 interpretation are unchanged. D-P3-59
supersedes D-P3-29's deferral of lossless publication, not its refusal to invent semantics.

Inputs read: `docs/MOVES.md`; this document's active contracts and historical addenda; RC3 Part I
and Phase 3 specification, plus the exact RC3 Phase 13 requirement to resolve U1; RESEARCH §§0–1
and Appendix F.5; the shipped custom-texture grammar; PD §§7.4/11; and the affected portions of
Phase 13 §§0/3.6/4.3 and §§5/11 U1 with its exact v3 requirement. Phase 13 is the assigned downstream request, not a newly
consumed verified dependency. PD is evidence only: it expressly says "pack PNG with `.mcmeta`
blur/clamp" (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:626-630`).

**Current §G1.3 status:** changed §5 requires fresh whole-document verification before dependent
consumption. U1's lossless Phase 3 publication is designed here; actual suffix honoring remains
blocked by the explicit authority and consumer requests in §§5.4/11.5, not declared satisfied by
retention alone. Earlier addenda and §0.55's companion-macro grant are preserved. No code, reviews,
authority documents, builds/tests/verification, or directory rolls are changed or run here.

### 0.57 Downstream-request addendum (Phase 4 per-program projections — 2026-09-07)

This maintainer-authorized architecture-only amendment grants the Phase-3-owned publication
requested at `docs/phase4/v1/PHASE_4_DOC.md:2263-2266` (§5.4 item 2). P4's request-time phrase
"same immutable owner-defined values directly" no longer occurs there: its §5.4 item 2 now
consumes "direct `mipmappedAfterPass()`/`vertices()` values", and §11.3 ruling 6 records the
§0.57 grant as adopted/unverified while Phase 5/10 keep their existing pass-mipmap and
vertex-layout consumer roles. Active §§3/4/5 now explicitly expose
`ProgramRequirements.mipmappedAfterPass()` and `ProgramRequirements.vertices()` to Phase 4 through
the existing `PackConfiguration.resources().programs()` map. The lookup uses the published
dimension and exact program name; Phase 4 neither reparses sources nor calls Phase 5/10 to obtain
the values. No record shape, directive meaning, canonical identity, or fingerprint encoding
changes; schema 16 remains current. D-P3-60 records this publication-only grant.

Inputs read: `docs/MOVES.md`; this document's affected contracts, addenda, testability, staging,
and hand-offs; RC3 Part I and Phase 3 specification; RESEARCH §§0–1 and Appendix A.3; and the
assigned Phase 4 request, its §0 governing declaration, §§4.9/5.3–5.4, and its RC3 specification
to establish the registry/pre-link use. RC3 governs both documents independently. Phase 4 is read
as the assigned downstream requester, not as a newly consumed or verified Phase 3 dependency.
Earlier addenda and existing working-tree changes are preserved.

**Current §G1.3 status:** this grant changes binding §5 and requires fresh whole-document
verification before dependent consumption. Phase 4's own adoption and fresh §5 verification
remain outstanding; its separate complete-legacy-geometry request and the existing ungranted
dependencies in §5.4 are not resolved here. No code, review edits, builds, tests, verification
runs, authority changes, or directory rolls are part of this amendment.

### 0.58 Integration reconciliation — 2026-09-07

Architecture-only IR-03/04/24/29 amendment. Read the integration findings/ledger, the affected
complete §5 contracts and their incorporated declarations, RC3 Phase 3 and §G1.3, RESEARCH
§§1/3.5/4.7/App F.1, and P1/P2 owner contracts. D-P3-61 makes the global old-light settings
tri-state and reserves AA at zero; changed macro/default meaning requires schema **17**.
Schema 16's catalog-bound materialization, required preliminary companion pair, lossless
declarations, direct per-program projections and safe persistence remain the baseline, not
compatibility shims. D-P3-62 publishes the source-free inspection route. Receiving-owner
reconciliation is architectural only: this changed §5 and every affected consumer remain
**unverified pending fresh whole-document review**. No implementation, validation, or PASS
claim is made; historical addenda retain their original schema/evidence.

### 0.59 Locale publication and old-light resolution — 2026-09-07

Architecture-only IR-03/24 amendment under the unchanged RC3 header. D-P3-63 replaces the
single decoration component with the complete locale-keyed catalog; D-P3-64 replaces
explicit-user-only old-light macros with cycle-free effective-policy resolution. D-P3-65 records
approved session-only Internal edits; D-P3-66 records approved option-only sampling and removes
the world supersampling component. These changes share one increment to **schema 18**, including
`IdMappingInput`. §5 incorporates the exact acquisition, fallback and fingerprint rules below.
Research preceded decisions: RC3 Phase 3, RESEARCH §§3.5/4.7/7.5/App F, shipped/published author
docs and the narrowly identified licensed language reader were read. §11 records evidence and
limits; OQ-7 is not self-ratified. Historical addenda/reviews remain unchanged; fresh review is required.

### 0.60 U1 documented-mechanism adoption — 2026-09-07

The maintainer selected **“Correct requirement to documented mechanisms”** in
`docs/decisions/U1_TEXTURE_SAMPLING.md`. D-P3-67 adopts that narrow authority correction for
Phase 3 under its otherwise unchanged RC3 pin. Active requirements retain `.0`–`.9` duplicate
discriminators and Phase-13-owned `.mcmeta` blur/clamp, and no longer require unspecified
filter/wrap property-key suffixes or a future typed sampling-state grant. Historical addenda,
decision evidence, research/design text and reviews remain unchanged; their former U1 pending
statements describe their dated surfaces, not an additional active gate after this adoption.

Inputs read for this adoption: the complete Phase 3 document; RC3 Part I §G0–§G12 and the
Phase 3 specification; RESEARCH §§0–1, 3.1–3.3, 3.5, 3.7, 4.1, 4.7, 7.5, Appendix A.3,
Appendix F in full and Appendix H; and the published
[OptiFine custom-texture block](https://raw.githubusercontent.com/sp614x/optifine/master/OptiFineDoc/doc/shaders.properties)
and [Iris `.mcmeta` section](https://shaders.properties/current/reference/buffers/custom_textures/#mcmeta-file).
OptiFine explicitly says `.0`–`.9` avoid duplicate property keys and `.mcmeta` configures
wrap/filter. Iris documents `blur`/`clamp` and excludes shader-provided sidecars for resource/
atlas textures. This evidence supports the selected mechanism, not modern features, a blanket
source-kind default, or ratification of Phase 13's existing defaults/error policy.

The published parser, typed source specs, sidecar references, lossless declarations, dispositions,
duplicate reduction and fingerprint payload are **unchanged**; schema **18** remains current
under §5.3. No shim, synthesized schema, or sampling API is introduced. This is authority
adoption, not runtime evidence or a PASS. Changed §5 requires fresh independent whole-document
review; IR-01, other owner grants and implementation gates remain in force.

### 0.61 Native-preserving geometry owner grant — 2026-09-07

D-P3-68 answers the complete native-source request in Phase 1 §11.4 and Phase 4 §5.4 item 1.
The two-span translator is removed from the active contract, not relabeled successful.
`GeometrySourceRequest` selects ordinary source or explicit native preservation; each successful
source publishes its language, closed geometry form, effective source-over-API layout and complete
attributed declaration catalog. This is **schema 19**: the nested source/site/map/result graph
and its meaning change. No schema-18 value may be relabeled or upgraded by inference.
Locale, Internal-session, U1, option/old-light and texture semantics are unchanged.

Inputs actually read for this amendment: complete current Phase 3; RC3 Part I and full Phase 3
specification; RESEARCH §§0–1, 3.1–3.2, 3.5 and Appendix A.3; shipped G6_pre1 author
`doc/shaders.txt` Geometry Shader Configuration; Phase 1 §5 and its full §11.4 native-source
request and migration, supporting §4.7.2; Phase 4 §§4.7–4.8 and §§5.2–5.4 as the assigned
requester, not a new dependency. Additional published author specifications, needed to close
the unresolved language/linkage gap, were read at their exact revisions:
[ARB_geometry_shader4](https://registry.khronos.org/OpenGL/extensions/ARB/ARB_geometry_shader4.txt),
revision 26, last modified 2011-01-21 (GLSL amendments and OpenGL 3.2 interactions);
[GLSL 1.20](https://registry.khronos.org/OpenGL/specs/gl/GLSLangSpec.1.20.pdf), revision 8,
2006-09-07, §3.3; and [GLSL 1.50](https://registry.khronos.org/OpenGL/specs/gl/GLSLangSpec.1.50.pdf),
revision 11, 2009-12-04, §§3.2–3.3 and 4.3.8. These are specification evidence, not OSS
implementation licenses. ARB carries Khronos copyright 2008–2013 and its linked
[specification-use terms](https://registry.khronos.org/speccopyright.html) (August 2015);
GLSL 1.50's copyright page permits implementing the functionality; GLSL 1.20 carries
3Dlabs copyright 2002–2006 and restrictive redistribution terms. This amendment cites and
independently specifies behavior; it copies no implementation or specification body.
No prohibited transformer, unresolved library, reference implementation code or transcript was read.

The published-spec facts below carry `[V:web 2026-09-07]`; the request/result design and error
classification are `[D-P3-68]`, not observed target-engine behavior. P4's already-recorded
TRIANGLES/TRIANGLE_STRIP API strategy is retained, not newly inferred from an author specification.
Native source preservation does not solve R26 fullscreen submission or P10 vanilla-QUADS
compatibility. The maintainer separately authorized a conditional quad-to-triangle adapter at
the earliest milestone claiming affected `.gsh` support; P7/P10 own its exact preservation,
topology/provoking-vertex/primitive-ID policy and conformance, not this source grant. Scope
permission is not completed owner/runtime evidence. Those gates, P1 jcpp admission/pinning,
fresh independent owner/consumer reviews and final integration still gate BUILD. No PASS is granted.

### 0.62 Same-load binary acquisition owner grant — 2026-09-08

D-P3-69 addresses TS-1 in `docs/build/reviews/TEXTURE_PARAMETER_SEAM_REVIEW_1.json`.
The existing eager bounded snapshot is now an actual public binary capability:
`PackConfiguration.assets()` immediately follows `sources`. Schema **20** replaces schema 19;
matching nested ID and inspection schema are mandatory. §§2.2/4.1/4.10/5/8 specify acquisition,
closed absence/rejection, read-only shared storage, same-load identity and source-free metadata.
P13 is explicitly granted PNG/raw/noise/adjacent-sidecar acquisition from that capability; P7
passes and retains the exact loaded configuration, including assets, across resource-only `NONE`.
This grants no host-path reopening, resource-epoch relabeling, sampling default, U1 syntax,
geometry change, implementation clearance or PASS. Historical addenda remain historical.
The approved `docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md` policy is preserved: authenticated
optional owned-sidecar-only read failures are retained as typed outcomes for P13's atomic
baseline recovery, one warning and fingerprinting. Safety, bounds, index/container, configuration
and primary-source failures stay fatal. P13 D-P13-30 and P7 D-P7-33 receive acquisition/retention; fresh reviews remain.

### 0.63 Round 55 fix-up — 2026-09-08

D-P3-70 corrects structural usability to accept a usable base **or explicit override**, maps
the documented external `TEXTURE_RECTANGLE` token to the existing typed `RECTANGLE`, and
publishes payload-free `ScreenProfileEntry()`. The nested screen shape requires schema **21**,
matching containing/ID/inspection schemas and the current materialization identity domain.
Assets, sidecar handling and the nine-tree projectionVersion=1 codec retain D-P3-69 meanings.
§§5/11 publish receiving obligations, not completed receiver receipts. R55's original verdict
is preserved with separate Resolutions; changed §5 requires fresh independent whole-document
review. No implementation clearance, self-PASS, build, test or validation evidence is claimed.

### 0.64 BLOCK dual-era producer grant — 2026-09-08

D-P3-72 resolves the producer gap identified by P9 review 1 C1: present BLOCK requests now
publish the same isolated forced-11300 alternate already defined for ENTITY. This is an
explicit local dual-era compatibility decision, not a claim of classic RESEARCH §3.7 behavior
or inferred source-branch provenance. Only the private alternate environment changes;
ordinary state/rules, item/layer behavior and all unrelated parser policies remain unchanged.
Changed component meaning advances the current schema to **22**, including nested IDs,
inspection and materialization identity. Older numeric addenda and D-P3-71's schema21
statement are historical; §5.3 supersedes their current-version assertions only.

Inputs read: this owner's header, parser/schema/identity contracts and affected plans; RC3
§§G0/G1/G4 and Phase 3; RESEARCH §§0–1/3.6.8/3.7; P9 review 1 C1 and owner §§4.6/5.4.
The prior passing P3 R56 review is untouched and does not certify this changed §5.
Receiver obligations are §§5.3/11.4; fresh independent review remains required. This is
architecture only, with no implementation, validation or clearance claim.

### 0.65 R57 range-capable selector correction — 2026-09-08

D-P3-73 resolves C57-1 with bounded immutable metadata sets of inclusive intervals and typed
literal/integer-range property alternatives. One rule per selector, source order/provenance,
dual-era isolation and registry ownership remain unchanged. The changed nested rule shape and
meaning require **schema23**, including containing/nested/inspection identity and
`MaterializedSource-v23`; all prior numeric receipts describe historical surfaces.
The nine metadata-only inspection trees and `projectionVersion=1` remain unchanged.

Inputs read for this correction: the owner contract and R57; RC3 §§G0/G1.3/G4 and the full Phase 3
specification; RESEARCH §§0–1/3.6.8/3.7; shipped G6 `doc/shaders.txt:535–568` and its explicit
`doc/properties_files.txt:82–122` block-matcher reference; P9 §4.5 as the affected receiver,
not a verified dependency. No implementation source or forbidden transcript was needed.
The examples are author-document evidence; exact typed encoding, normalization, bounds and
ambiguity/rejection rules below are local API decisions, not newly claimed reference behavior.
R57's original verdict and N57-1 source/license limitations remain intact. Changed §5 requires
fresh independent producer/receiver review; no implementation, runtime, validation or clearance
is supplied by this documentation-only correction.

### 0.66 R60 override-target whitelist correction — 2026-09-08

This addendum supplies the amendment-ledger entry the R60 C1 fix-up lacked. D-P3-74 makes
§4.4's per-pack override target whitelist authoritative: protected targets are exactly
non-version identity/capability names, so §4.5's reserved intrinsic namespace (`__VERSION__`,
`__FILE__`, `__LINE__`, `GL_core_profile`, `GL_compatibility_profile`,
`GL_ARB_geometry_shader4`) and every other non-whitelisted name are rejected with the same
pre-I/O `INVALID_REQUEST`, closing the blocklist/whitelist conflict. §§4.4/5.1/8.1 state the
one ruling identically; there is no `PackConfiguration` component, meaning, fingerprint or
schema change, so no identity receipt moves.

Inputs read for this record: this owner's §4.4 whitelist action matrix, §4.5's reserved
intrinsic namespace, §5.1's `MacroConfiguration` row, §8.1's override gate and the decision
log, plus review 61 correction 1; no implementation source or forbidden transcript was needed.
No build, test, Gradle, GL, capture or network command ran, and no historical addendum,
review verdict or receipt was rewritten. §8.1's named gate and a fresh independent
whole-document review remain required. This is documentation only: no implementation, runtime
evidence, fresh PASS or implementation clearance is claimed.

### 0.67 Round 62 fix-up — 2026-09-08

Review 62's correction repairs evidence anchors only, applied at the named sites on current
bytes: §0.55 now cites Phase 13 §4.1.6 at `docs/phase13/v1/PHASE_13_DOC.md:846-852` and quotes
P13's current "Off short-circuits" / materializer-contribution wording in place of the
no-longer-verbatim sentence; §0.56 re-attributes P13's "stripped and ignored" phrasing to the
2026-09-07 request revision, recording that it no longer occurs in current Phase 13 and keeping
the old coordinates as dated history rather than current pins; §0.57 and D-P3-60 keep the
resolving `docs/phase4/v1/PHASE_4_DOC.md:2263-2266` pin, anchor P4 §5.4 item 2 and §11.3
ruling 6, quote P4's current "direct `mipmappedAfterPass()`/`vertices()` values", and mark the
request-time phrase as no-longer-extant; §0.33 records P11 §5.2's direct consumed-contract
statement superseding its request prose at unchanged coordinates; §10's duplicated OQ-7
**Fallback:** paragraph is collapsed. No §5 row, export, schema, fingerprint payload, or
receiver obligation changes, so per review 62 no fresh §5 re-verification is triggered.

Inputs read for this repair: review 62 and the current Phase 4/11/13 dependency sections at the
repaired citations. No build, test, Gradle, GL, capture, or network command ran; documentary
only. Resolution records live in `docs/phase3/reviews/PHASE_3_REVIEW_62.md` under
`## Resolutions`.

**Current §G1.3 status:** all review 62 corrections and notes are resolved with no §5 change
outstanding, so Phase 3 is verified for this surface under §G1.3; the pending separate
citation-anchor re-derivation pass covers only line-level drift from this round.

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
- the Phase-3-owned typed companion option-macro snapshot supplied before load by Phase 7;
- jcpp preprocessing for shader sources and a separate properties-safe jcpp adapter;
- complete default-block uniform declaration capture from each final materialized shader, with a
  closed structural GLSL type, declaring source stage, attributed source location, and the exact
  materialization fingerprint;
- closed, immutable custom-expression declarations from the preprocessed Properties stream, in
  source order with exact unescaped text, source ordinal, attribution, and retained duplicates;
- all Appendix A.3 directive recognition and aggregation into requirements/configuration data;
- the complete Appendix F `shaders.properties` model;
- lossless, source-ordered texture-property declarations before normalization or winner selection,
  including unresolved key extensions under existing `UNRESOLVED_KEY` handling;
- schema-versioned unresolved ID-mapping inputs, per-file presence, entry/tag and era provenance,
  ordinary/forced-11300 block and entity parses, and layer rules;
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
| Loading/uploading custom/noise textures and interpreting `.mcmeta` | **Phase 13**; Phase 3 stores specs plus lossless property declarations, owns property-key parsing, and never delegates suffix reconstruction |
| Preliminary companion policy and adaptation into `CompanionOptionMacros` before load | **Phase 13** owns the policy; **Phase 7** supplies the Phase-3-owned value; Phase 3 never waits for a texture plan or infers enablement from shader analysis |
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

The declarations below are the canonical public shapes. Helpers remain private under `.internal`; the package-private front-end and permitted capability implementations remain in the API package where Java sealing requires them.

```java
public final class PackFrontEnds {
    private PackFrontEnds() {}
    public static PackFrontEndServices create() {
        return PackFrontEndServicesProvider.create();
    }
}
public final class PackFrontEndServices {
    private final PackFrontEnd frontEnd;
    private final OptionPersistenceCodec optionPersistence;
    private final GlobalShaderOptionsCodec globalOptions;
    private final IdMappingParser idMappings;
    PackFrontEndServices(PackFrontEnd frontEnd, OptionPersistenceCodec optionPersistence,
        GlobalShaderOptionsCodec globalOptions, IdMappingParser idMappings) {
        this.frontEnd = Objects.requireNonNull(frontEnd, "frontEnd");
        this.optionPersistence = Objects.requireNonNull(optionPersistence, "optionPersistence");
        this.globalOptions = Objects.requireNonNull(globalOptions, "globalOptions");
        this.idMappings = Objects.requireNonNull(idMappings, "idMappings");
    }
    public PackFrontEnd frontEnd() { return frontEnd; }
    public OptionPersistenceCodec optionPersistence() { return optionPersistence; }
    public GlobalShaderOptionsCodec globalOptions() { return globalOptions; }
    public IdMappingParser idMappings() { return idMappings; }
    public PersistenceFileAccessAcquisition persistenceFiles(PersistenceRootConfiguration roots) {
        return PersistenceFileAccessProvider.acquire(this, roots);
    }
}

public interface PackFrontEnd {
    int CURRENT_SCHEMA_VERSION = 23;
    DiscoveryLimits discoveryLimits();
    PackDiscoveryResult discover(PackDiscoveryRequest request);
    FilesystemCandidateResolution resolveFilesystemCandidate(
        FilesystemCandidateReference reference, PackDiscoveryResult current);
    PackOptionsTargetAcquisition packOptionsTarget(PackCandidateId candidate);
    PackLoadResult load(PackLoadRequest request);
    PackInspectionResult inspect(PackLoadRequest request);
}

public record PackDiscoveryRequest(
    Path shaderpacksDirectory,
    DiagnosticReporter diagnostics) {}
public record DiscoveryLimits(
    int maxCandidatesPerSnapshot,
    long maxSnapshotBytes,
    int maxRetainedDirectorySnapshots) {}

public record PackDiscoveryResult(
    DiscoveryGeneration generation,
    List<PackCandidate> candidates,
    List<EngineDiagnostic> diagnostics) {}

public record PackCandidate(
    PackCandidateId id,
    Optional<FilesystemCandidateReference> filesystemReference,
    PackCandidateKind kind,
    String displayName,
    PackCandidateStatus status,
    List<EngineDiagnostic> diagnostics) {}
public enum PackCandidateKind { OFF, INTERNAL, DIRECTORY, ARCHIVE }
public enum PackCandidateStatus { AVAILABLE, UNREADABLE, UNSAFE, LIMIT_EXCEEDED }
public record FilesystemCandidateReference(String canonicalValue) {}
public sealed interface FilesystemCandidateResolution {
    record Resolved(PackCandidateId candidate) implements FilesystemCandidateResolution {}
    record Missing() implements FilesystemCandidateResolution {}
    record Ambiguous() implements FilesystemCandidateResolution {}
    record KindChanged(PackCandidateKind currentKind) implements FilesystemCandidateResolution {}
    record InvalidSnapshot() implements FilesystemCandidateResolution {}
}
public sealed interface PackOptionsTargetAcquisition {
    record Acquired(PackOptionsTarget target) implements PackOptionsTargetAcquisition {}
    record Rejected(PackOptionsTargetRejection reason) implements PackOptionsTargetAcquisition {}
}
public enum PackOptionsTargetRejection {
    NULL_CANDIDATE, FOREIGN_DOMAIN, UNKNOWN_CANDIDATE, SUPERSEDED_GENERATION,
    NON_FILESYSTEM, UNAVAILABLE
}
public sealed interface PackCandidateId permits PackCandidateIdToken {}
public sealed interface DiscoveryGeneration permits DiscoveryGenerationToken {}

public interface InternalPackSource {
    PackIdentity identity();
    InternalPackSnapshot snapshot(PackInputLimits limits) throws InternalPackReadException;
}
public final class InternalPackReadException extends Exception {}
public record PackIdentity(
    NormalizedPackPath selectedRoot,
    Map<NormalizedPackPath, String> contentHashes) {}

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
    CompanionOptionMacros companionOptionMacros,
    RendererFeatureData rendererFeatures,
    PersistenceFileAccess persistenceFiles,
    InternalPackSource internalPackSource,
    Optional<InternalOptionSnapshot> internalOptions,
    DiagnosticReporter diagnostics) {}
public record RuntimeIdentityData(
    int mcMajor,
    int mcMinor,
    int mcPatch,
    String engineEdition,
    String engineVersion,
    OsFamily osFamily,
    Map<String, MacroOverride> perPackIdentityOverrides) {}
public record CompanionOptionMacros(boolean normalMap, boolean specularMap) {}
public record RendererFeatureData(
    boolean normalMapAvailable,
    boolean specularMapAvailable) {}
public enum OsFamily { WINDOWS, MACOS, LINUX, OTHER }
public record PackInputLimits(
    int maxEntries,
    long maxTotalBytes,
    int maxPathLength,
    int maxNestingDepth) {}

public sealed interface PackLoadResult {
    record Off() implements PackLoadResult {}
    record Loaded(PackConfiguration configuration) implements PackLoadResult {}
    record Failed(PackLoadFailure failure) implements PackLoadResult {}
}
public sealed interface PackSelection
    permits PackSelection.Off, PackSelection.Internal, PackSelection.Filesystem {
    record Off() implements PackSelection {}
    record Internal() implements PackSelection {}
    record Filesystem(PackCandidateId candidate) implements PackSelection {}
}
public enum CompatibilityStatus { COMPATIBLE, REQUIRES_NEWER_EDITION }
public record PackLoadFailure(
    PackLoadFailureCode code,
    EngineDiagnostic primaryDiagnostic) {}
public enum PackLoadFailureCode {
    INVALID_REQUEST, INVALID_SELECTION, INPUT_UNREADABLE, INPUT_UNSAFE,
    INPUT_LIMIT_EXCEEDED, INTERNAL_SOURCE_INVALID, STRUCTURALLY_UNUSABLE,
    UNEXPECTED_INTERNAL
}
public record DimensionKey(OptionalInt legacyId) {
    public DimensionKey {
        Objects.requireNonNull(legacyId, "legacyId");
        if (legacyId.isPresent() &&
            (legacyId.getAsInt() < -128 || legacyId.getAsInt() > 128)) {
            throw new IllegalArgumentException("legacy dimension ID outside [-128,128]");
        }
    }
}
public enum DimensionMode { BASE, OVERRIDE, DISABLED }
public record DimensionConfiguration(
    DimensionKey key,
    DimensionMode mode,
    Optional<DimensionKey> baseDimension,
    List<SourceKey> sourceRoots) {}


public record PackConfiguration(
    int schemaVersion,
    PackIdentity pack,
    CompatibilityStatus compatibility,
    Map<DimensionKey, DimensionConfiguration> dimensions,
    SourceCatalog sources,
    PackAssetSnapshot assets,
    OptionConfiguration options,
    MacroConfiguration macros,
    ShaderPropertiesModel properties,
    ResourceRequirements resources,
    IdMappingInput idMappings,
    List<EngineDiagnostic> diagnostics,
    ConfigurationFingerprint fingerprint) {
    public ProgramStateEvaluationResult evaluateProgramStates(
        Optional<ProfileName> selectedProfile,
        DiagnosticReporter diagnostics) {
        return ProgramStateEvaluator.evaluate(this, options.state(), selectedProfile, diagnostics);
    }
}
public record ConfigurationFingerprint(String value) {}

public sealed interface PackAssetSnapshot permits PackAssetSnapshotImpl {
    PackIdentity pack();
    List<PackAssetMetadata> manifest();
    PackAssetAcquisition acquire(NormalizedPackPath path);
}
public record PackAssetMetadata(
    NormalizedPackPath path, PackAssetAvailability availability,
    OptionalInt byteCount, Optional<String> sha256) {}
public enum PackAssetAvailability { AVAILABLE, MISSING, UNREADABLE }
public sealed interface PackAssetAcquisition {
    record Acquired(PackAssetBytes bytes) implements PackAssetAcquisition {}
    record Unreadable(NormalizedPackPath path) implements PackAssetAcquisition {}
    record Missing(NormalizedPackPath path) implements PackAssetAcquisition {}
    record InvalidReference(PackAssetReferenceFailure reason) implements PackAssetAcquisition {}
}
public enum PackAssetReferenceFailure { NULL_PATH, NOT_DECLARED }
public sealed interface PackAssetBytes permits PackAssetBytesImpl {
    PackAssetMetadata metadata();
    java.nio.ByteBuffer openCursor();
}

public record MacroConfiguration(
    MacroIdentityPolicy identityPolicy,
    List<MacroDefinition> baseCompatibilityMacros,
    List<MacroDefinition> optionMacros,
    CompanionOptionMacros companionOptionMacros,
    List<MacroDefinition> capabilityFeatureMacros,
    List<MacroDefinition> engineIdentityMacros,
    Map<String, MacroOverride> perPackOverrides,
    List<String> reservedContributors) {}
public record MacroDefinition(String name, String replacement) {}
public record MacroOverride(MacroOverrideAction action, Optional<String> replacement) {}
public enum MacroOverrideAction { ADD, SUPPRESS, FORCE }
public enum MacroIdentityPolicy { OPTION_1, OPTION_2, OPTION_3 }
public enum TexturePropertyStage { GBUFFERS, DEFERRED, COMPOSITE }
public record TextureBindingKey(
    TexturePropertyStage stage, String sampler, OptionalInt duplicateDiscriminator) {}
public enum TexturePropertyDisposition {
    CUSTOM_SOURCE, NOISE_SOURCE, UNRESOLVED_KEY, INVALID_VALUE
}
public record TexturePropertyDecl(
    String key, String value, int sourceOrdinal, SourceAttribution attribution,
    TexturePropertyDisposition disposition) {}
public enum TextureTarget { TEXTURE_1D, TEXTURE_2D, TEXTURE_3D, RECTANGLE }
public enum ColorInternalFormat {
    R8, RG8, RGB8, RGBA8, R8_SNORM, RG8_SNORM, RGB8_SNORM, RGBA8_SNORM,
    R16, RG16, RGB16, RGBA16, R16_SNORM, RG16_SNORM, RGB16_SNORM, RGBA16_SNORM,
    R16F, RG16F, RGB16F, RGBA16F, R32F, RG32F, RGB32F, RGBA32F,
    R32I, RG32I, RGB32I, RGBA32I, R32UI, RG32UI, RGB32UI, RGBA32UI,
    R3_G3_B2, RGB5_A1, RGB10_A2, R11F_G11F_B10F, RGB9_E5
}
public enum PixelFormat {
    RED, RG, RGB, BGR, RGBA, BGRA,
    RED_INTEGER, RG_INTEGER, RGB_INTEGER, BGR_INTEGER, RGBA_INTEGER, BGRA_INTEGER
}
public enum PixelType {
    BYTE, SHORT, INT, HALF_FLOAT, FLOAT, UNSIGNED_BYTE,
    UNSIGNED_BYTE_3_3_2, UNSIGNED_BYTE_2_3_3_REV, UNSIGNED_SHORT,
    UNSIGNED_SHORT_5_6_5, UNSIGNED_SHORT_5_6_5_REV, UNSIGNED_SHORT_4_4_4_4,
    UNSIGNED_SHORT_4_4_4_4_REV, UNSIGNED_SHORT_5_5_5_1,
    UNSIGNED_SHORT_1_5_5_5_REV, UNSIGNED_INT, UNSIGNED_INT_8_8_8_8,
    UNSIGNED_INT_8_8_8_8_REV, UNSIGNED_INT_10_10_10_2,
    UNSIGNED_INT_2_10_10_10_REV
}
public record TextureSidecarRef(NormalizedPackPath path) {}
public sealed interface CustomTextureSpec
    permits CustomTextureSpec.PackPath, CustomTextureSpec.MinecraftResource,
            CustomTextureSpec.Raw {
    record PackPath(TextureBindingKey key, NormalizedPackPath image,
        Optional<TextureSidecarRef> sidecar) implements CustomTextureSpec {}
    record MinecraftResource(TextureBindingKey key, String resourceIdentity)
        implements CustomTextureSpec {}
    record Raw(TextureBindingKey key, NormalizedPackPath bytes, TextureTarget target,
        ColorInternalFormat internalFormat, List<Integer> dimensions, PixelFormat pixelFormat,
        PixelType pixelType, Optional<TextureSidecarRef> sidecar) implements CustomTextureSpec {}
}
public sealed interface NoiseTextureSpec
    permits NoiseTextureSpec.Generated, NoiseTextureSpec.Override {
    record Generated() implements NoiseTextureSpec {}
    record Override(NormalizedPackPath image, Optional<TextureSidecarRef> sidecar)
        implements NoiseTextureSpec {}
}

public enum OptionKind { SWITCH, VARIABLE, CONSTANT }
public enum OptionAvailability { AVAILABLE, DISABLED_AMBIGUOUS }
public sealed interface OptionValue permits BooleanOptionValue, TextOptionValue {}
public record BooleanOptionValue(boolean value) implements OptionValue {}
public record TextOptionValue(String value) implements OptionValue {}
public record OptionDefinition(
    String name,
    OptionKind kind,
    OptionValue defaultValue,
    List<OptionValue> allowedValues,
    OptionAvailability availability,
    Optional<String> tooltip,
    List<SourceAttribution> occurrences) {}
public sealed interface OptionCatalog permits OptionCatalogValue {
    List<OptionDefinition> definitions();
    Optional<OptionDefinition> find(String name);
    OptionState defaultState();
    OptionStateResult constructState(
        Map<String, OptionValue> values, DiagnosticReporter diagnostics);
    OptionStateResult updateState(
        OptionState baseline, String name, OptionValue value, DiagnosticReporter diagnostics);
    OptionStateValidation validate(OptionState candidate);
    InternalOptionCaptureResult captureInternalOptions(
        OptionState state, DiagnosticReporter diagnostics);
}
public sealed interface OptionState permits OptionStateValue {
    Map<String, OptionValue> values();
    Optional<OptionValue> value(String name);
}
public sealed interface OptionStateResult {
    record Valid(OptionState state) implements OptionStateResult {}
    record Invalid(OptionStateFailure failure) implements OptionStateResult {}
}
public record OptionStateValidation(Optional<OptionStateFailure> failure) {
    public boolean valid() { return failure.isEmpty(); }
}
public enum OptionStateFailure {
    NULL_INPUT, FOREIGN_CATALOG, UNKNOWN_OPTION, MISSING_OPTION, NULL_VALUE,
    KIND_MISMATCH, DISABLED_AMBIGUOUS, UNSAFE_TEXT
}
public sealed interface InternalOptionSnapshot permits InternalOptionSnapshotValue {}
public sealed interface InternalOptionCaptureResult {
    record Captured(InternalOptionSnapshot snapshot) implements InternalOptionCaptureResult {}
    record Invalid(InternalOptionFailure failure) implements InternalOptionCaptureResult {}
}
public enum InternalOptionFailure { NULL_INPUT, FOREIGN_CATALOG, NOT_INTERNAL, INVALID_STATE }
public record OptionConfiguration(
    OptionCatalog catalog,
    OptionState state,
    List<ProfileModel> profiles,
    ScreenModel mainScreen,
    Map<String, ScreenModel> namedScreens,
    SliderSet sliders,
    Map<String, LangDecorations> localizedDecorations) {
    public ProfileInferenceResult inferProfile(OptionState candidate) {
        return ProfileEvaluator.infer(this, candidate);
    }
}
public record ProfileName(String value) {}
public record ProfileConstraint(String optionName, OptionValue requiredValue) {}
public record ProgramDisable(Optional<DimensionKey> dimension, String programName) {}
public record ProfileModel(
    ProfileName name,
    List<ProfileConstraint> constraints,
    List<ProgramDisable> disabledPrograms) {}
public record ProfileInference(Optional<ProfileName> selected, boolean custom) {}
public sealed interface ProfileInferenceResult {
    record Inferred(ProfileInference inference) implements ProfileInferenceResult {}
    record InvalidState(OptionStateFailure failure) implements ProfileInferenceResult {}
}
public sealed interface ScreenEntry
    permits ScreenOptionEntry, ScreenSubscreenEntry, ScreenProfileEntry,
            ScreenEmptyEntry, ScreenAllOptionsEntry {}
public record ScreenOptionEntry(String optionName) implements ScreenEntry {}
public record ScreenSubscreenEntry(String screenName) implements ScreenEntry {}
public record ScreenProfileEntry() implements ScreenEntry {}
public record ScreenEmptyEntry() implements ScreenEntry {}
public record ScreenAllOptionsEntry() implements ScreenEntry {}
public record ScreenModel(OptionalInt explicitColumns, List<ScreenEntry> entries) {
    public int resolvedColumns(int expandedSlotCount) {
        if (expandedSlotCount < 0) throw new IllegalArgumentException("negative slot count");
        int configuredColumns = explicitColumns.orElse(2);
        return Math.max(configuredColumns, Math.ceilDiv(expandedSlotCount, 9));
    }
}
public record SliderSet(List<String> optionNames) {}
public record ValueDecorationKey(String optionName, String value) {}
public record LangDecorations(
    Map<String, String> optionLabels,
    Map<String, String> optionComments,
    Map<ValueDecorationKey, String> valueLabels,
    Map<String, String> prefixes,
    Map<String, String> suffixes,
    Map<ProfileName, String> profileLabels,
    Map<ProfileName, String> profileComments,
    Map<String, String> screenLabels,
    Map<String, String> screenComments) {}

public enum TriState { DEFAULT, TRUE, FALSE }
public enum CloudMode { DEFAULT, FAST, FANCY, OFF }
public record EngineFlags(
    CloudMode clouds,
    TriState oldHandLight,
    TriState dynamicHandLight,
    TriState oldLighting,
    TriState shadowTranslucent,
    TriState underwaterOverlay,
    TriState sun,
    TriState moon,
    TriState vignette,
    TriState backFaceSolid,
    TriState backFaceCutout,
    TriState backFaceCutoutMipped,
    TriState backFaceTranslucent,
    TriState rainDepth,
    TriState beaconBeamDepth,
    TriState separateAo,
    TriState frustumCulling) {}
public record MinimumEditionRule(String minecraftVersion, String minimumEdition) {}
public record IdMappingMacroEnvironment(
    int mcVersion,
    List<MacroDefinition> standardMacros) {}
public record IdMappingFileFingerprint(String value) {}
public record UnknownProperty(String key, String value, SourceAttribution attribution) {}
public record ShaderPropertiesModel(
    EngineFlags engineFlags,
    List<MinimumEditionRule> minimumEditionRules,
    List<CustomTextureSpec> textures,
    List<TexturePropertyDecl> textureDeclarations,
    NoiseTextureSpec noise,
    List<CustomExpressionDecl> customExpressions,
    ProgramStateModel programStates,
    List<UnknownProperty> unknownProperties) {}
public record EngineOptionData(Map<String, String> values) {}

`EngineOptionData` construction, global-codec classification, and load-request validation all
incorporate the single binding validity invariant in §5.1; no caller projection may broaden it.

public record ProgramKey(DimensionKey dimension, String programName) {}
public record ProgramStateModel(Map<ProgramKey, ProgramState> programs) {}
public record ProgramState(
    Optional<AlphaTestSpec> alphaTest,
    Optional<BlendSpec> blend,
    Optional<ViewportScale> scale,
    Map<FlipBufferKey, FlipOverride> flips,
    Optional<ProgramEnabledExpression> enabledExpression) {}
public sealed interface AlphaTestSpec {
    record Off() implements AlphaTestSpec {}
    record Enabled(AlphaFunction function, float reference) implements AlphaTestSpec {}
}
public enum AlphaFunction { NEVER, LESS, EQUAL, LEQUAL, GREATER, NOTEQUAL, GEQUAL, ALWAYS }
public sealed interface BlendSpec {
    record Off() implements BlendSpec {}
    record Enabled(
        BlendFactor sourceColor,
        BlendFactor destinationColor,
        Optional<BlendAlphaFactors> alpha) implements BlendSpec {}
}
public record BlendAlphaFactors(BlendFactor source, BlendFactor destination) {}
public enum BlendFactor {
    ZERO, ONE, SRC_COLOR, ONE_MINUS_SRC_COLOR, DST_COLOR, ONE_MINUS_DST_COLOR,
    SRC_ALPHA, ONE_MINUS_SRC_ALPHA, DST_ALPHA, ONE_MINUS_DST_ALPHA,
    CONSTANT_COLOR, ONE_MINUS_CONSTANT_COLOR, CONSTANT_ALPHA,
    ONE_MINUS_CONSTANT_ALPHA, SRC_ALPHA_SATURATE
}
public record ViewportScale(float scale, float offsetX, float offsetY) {}
public record FlipBufferKey(ColorAttachmentKey attachment) {}
public enum FlipOverride { TRUE, FALSE }
public sealed interface ProgramEnabledExpression permits ProgramEnabledExpressionValue {}
public sealed interface ProgramStateEvaluationResult {
    record Evaluated(EvaluatedProgramStates states) implements ProgramStateEvaluationResult {}
    record InvalidState(OptionStateFailure failure) implements ProgramStateEvaluationResult {}
}
public record EvaluatedProgramStates(
    List<EvaluatedProgramState> programs,
    Map<ProgramKey, Map<FlipBufferKey, FlipOverride>> explicitFlips) {}
public record EvaluatedProgramState(
    ProgramKey key,
    Optional<AlphaTestSpec> alphaTest,
    Optional<BlendSpec> blend,
    Optional<ViewportScale> scale,
    boolean propertyEnabled,
    boolean profileDisabled,
    boolean finalEnabled) {}

public sealed interface PersistenceTarget
    permits PackOptionsTarget, GlobalOptionsTarget {}
public sealed interface PackOptionsTarget extends PersistenceTarget
    permits PackOptionsTargetValue {
    String fileName();
    FilesystemCandidateReference reference();
}
public record GlobalOptionsTarget() implements PersistenceTarget {}
public record PersistenceRootConfiguration(Path shaderpacksDirectory, Path gameDirectory) {}
public sealed interface PersistenceFileAccessAcquisition {
    record Acquired(PersistenceFileAccess files) implements PersistenceFileAccessAcquisition {}
    record InvalidRoots(PersistenceFailure failure) implements PersistenceFileAccessAcquisition {}
}
public sealed interface PersistenceFileAccess permits PersistenceFileAccessValue {
    PersistenceReadSource read(PersistenceTarget target);
    PersistenceWriteReceipt writeAtomically(PersistenceTarget target, ImmutableBytes content);
}
public sealed interface PersistenceReadSource
    permits PersistenceReadSource.Absent, PersistenceReadSource.Present,
            PersistenceReadSource.Failed {
    record Absent() implements PersistenceReadSource {}
    record Present(ImmutableBytes bytes) implements PersistenceReadSource {}
    record Failed(PersistenceFailure failure) implements PersistenceReadSource {}
}
public record PersistenceWriteReceipt(
    PersistenceWriteStatus status,
    Optional<PersistenceFailure> failure,
    boolean atomicMoveUsed) {}
public record PersistenceFailure(PersistenceFailureCode code, String detail) {}
public enum PersistenceReadStatus { ABSENT, APPLIED, FAILED }
public enum PersistenceWriteStatus { COMMITTED, FAILED }
public enum PersistenceFailureCode {
    INVALID_REQUEST, UNREADABLE, UNSAFE_TARGET, INVALID_ENCODING, WRITE_FAILED
}
public interface OptionPersistenceCodec {
    OptionPersistenceReadResult read(OptionPersistenceReadRequest request);
    OptionPersistenceWriteResult write(OptionPersistenceWriteRequest request);
}
public record OptionPersistenceReadRequest(
    PersistenceFileAccess files,
    PackOptionsTarget target,
    OptionCatalog catalog,
    OptionState baseline,
    DiagnosticReporter diagnostics) {}
public record OptionPersistenceWriteRequest(
    PersistenceFileAccess files,
    PackOptionsTarget target,
    OptionCatalog catalog,
    OptionState state,
    DiagnosticReporter diagnostics) {}
public sealed interface OptionPersistenceReadResult {
    record Completed(
        OptionState state,
        PersistenceReadStatus status,
        Optional<PersistenceFailure> failure,
        List<EngineDiagnostic> diagnostics) implements OptionPersistenceReadResult {}
    record InvalidRequest(
        PersistenceFailure failure,
        List<EngineDiagnostic> diagnostics) implements OptionPersistenceReadResult {}
}
public record OptionPersistenceWriteResult(
    PersistenceWriteStatus status,
    Optional<PersistenceFailure> failure,
    List<EngineDiagnostic> diagnostics) {}
public interface GlobalShaderOptionsCodec {
    GlobalShaderOptionsReadResult read(GlobalShaderOptionsReadRequest request);
    GlobalShaderOptionsWriteResult write(GlobalShaderOptionsWriteRequest request);
}
public record GlobalShaderOptionsReadRequest(
    PersistenceFileAccess files,
    GlobalOptionsTarget target,
    EngineOptionData baseline,
    DiagnosticReporter diagnostics) {}
public record GlobalShaderOptionsWriteRequest(
    PersistenceFileAccess files,
    GlobalOptionsTarget target,
    EngineOptionData values,
    DiagnosticReporter diagnostics) {}
public record GlobalShaderOptionsReadResult(
    EngineOptionData values,
    PersistenceReadStatus status,
    Optional<PersistenceFailure> failure,
    List<EngineDiagnostic> diagnostics) {}
public record GlobalShaderOptionsWriteResult(
    PersistenceWriteStatus status,
    Optional<PersistenceFailure> failure,
    List<EngineDiagnostic> diagnostics) {}

// Named immutable projection of ShaderPropertiesModel data; not a record component.
List<CustomExpressionDecl> PackConfiguration.customExpressions();

public enum CustomExpressionKind { UNIFORM, VARIABLE }
public enum CustomExpressionType { FLOAT, INT, BOOL, VEC2, VEC3, VEC4 }

public record CustomExpressionDecl(
    CustomExpressionKind kind,
    CustomExpressionType type,
    String name,
    String rawExpression,
    int sourceOrdinal,
    SourceAttribution attribution) {}

public record SourceAttribution(
    NormalizedPackPath source,
    int physicalLine,
    int physicalColumn) {}

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
// D-P3-72: BLOCK/ENTITY may publish forced11300Rules; ITEM/LAYER never do.
// State is ordinary-derived; only alternate rules are MODERN (§4.9).
// All declarations above are consumer-visible. Collections are defensively copied and enums are
// exhaustive. Candidate IDs, generations, targets, and file access have package-private final
// implementations beside the front end in `com.schmaloogium.engine.pack`. `OptionCatalog`,
// `OptionState`, and `ProgramEnabledExpression` likewise have only package-private final
// implementations beside their interfaces in `.config`; consumers cannot mint a catalog or state.
// The factory/bundle supplies hidden domain, pack-key, generation-kind, and exact-catalog
// credentials internally. Capabilities use identity equality/hash and expose no serialization;
// `FilesystemCandidateReference` alone is serializable.
// `PackIdentity.selectedRoot()` is the chosen validated root and `contentHashes()` is the immutable
// canonical-path-ordered map of non-empty opaque content-hash strings. `PackInputLimits` fields are
// strictly positive. `DiscoveryLimits.maxCandidatesPerSnapshot` is at least two, its other fields
// are positive, and the configured byte limit admits the fixed two-sentinel overflow result.
// `RuntimeIdentityData` accepts only MC `(1,12,2)`; its edition and numeric engine-version strings
// obey the exact grammars and projections in §§4.4/4.8/5.1, and its closed OS/override values remain
// validated. `ConfigurationFingerprint` and `IdMappingFileFingerprint` are non-empty opaque
// canonical strings. `InternalPackReadException` is the provider-only checked signal;
// its detail is caught, diagnosed, and never published in `PackLoadFailure`.

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
    Optional<MetadataConstraint> legacyMetadata,
    List<PropertyPredicate> propertyPredicates,
    MappingEra era,
    MappingOrigin origin,
    int sourceLine,
    int selectorOrdinal) implements MappingRule {}

public record LayerRule(
    RequestedRenderLayer layer,
    SelectorKind selectorKind,
    String selectorToken,
    Optional<MetadataConstraint> legacyMetadata,
    List<PropertyPredicate> propertyPredicates,
    MappingEra era,
    MappingOrigin origin,
    int sourceLine,
    int selectorOrdinal) implements MappingRule {}

public record IntegerRange(int lowerInclusive, int upperInclusive) {}
public record MetadataConstraint(List<IntegerRange> alternatives) {}
public sealed interface PropertyValueConstraint {
    record Literal(String value) implements PropertyValueConstraint {}
    record IntegerInterval(IntegerRange range) implements PropertyValueConstraint {}
}
public record PropertyPredicate(
    String propertyName, List<PropertyValueConstraint> acceptedValues) {}

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
// `IdMappingMacroEnvironment` contains the ordinary integer `MC_VERSION` plus the immutable,
// deterministically ordered OF standard macro definitions other than that typed field; it contains no
// option, pack, parser, or Minecraft value. `PackSelection` and `PackLoadResult` are exhaustive
// closed sums. `CompatibilityStatus.COMPATIBLE` is the absent/no-unmet-rule outcome.
```

`NormalizedPackPath.canonicalString()` is the sole public path projection. Its value is non-empty,
Unicode NFC, root-relative, and slash-separated. It contains no leading/trailing `/`, empty segment,
`.` or `..` segment, backslash, NUL, URI/drive prefix, or host-dependent separator. Construction
validates this grammar and rejects a non-NFC spelling rather than silently rewriting caller data.
Canonical path order is unsigned lexicographic order of the UTF-8 bytes of `canonicalString()`;
identity hashes and wire formats consume those exact bytes. `toString()`, `Path.toString()`, display
names, and platform path objects are not stable projections (`[D-P3-24]`).

`DimensionKey` is a closed engine value, not a Minecraft dimension type: `OptionalInt.empty()`
denotes the base source set and a present value denotes one legacy world ID in `[-128,128]`.
`DimensionConfiguration` links its validated key and mode to an optional base key and to the exact
ordered `SourceKey` roots for that set. `BASE` has no base key; `OVERRIDE` and `DISABLED` point
to the base key, but an override never merges its sources with the base. The dimensions map
always contains the base entry; absent world keys mean no physical override.
An `OFF` request returns `PackLoadResult.Off` without opening an input, publishing a
configuration, or reporting failure; Phase 7/12 replace any prior configuration with their owned
shaders-off state. `InternalPackSource.snapshot` returns one finite, immutable manifest in
canonical-path order, including empty directories. Phase 3 revalidates every `canonicalString()`,
rejects duplicate canonical paths or file/directory collisions, and applies
the same entry-count, byte-count, path-length, and nesting limits used for archives. A provider
limit/read violation becomes an attributed pack-level failure. `ImmutableBytes` exposes size plus
a fresh copy on every `copy()` call; Phase 3 owns that copy, and neither the manifest nor the
published configuration retains provider storage. `identity()` is stable for equal internal
content and configuration. Phase 7 supplies the content. `RuntimeIdentityData` contains the fixed
MC 1.12.2 tuple, canonicalizable engine edition, numeric engine version, OS family, and configured
per-pack identity override as plain values.

`SourceCatalog` is the immutable source snapshot. It owns stable `SourceId`s, original logical
lines, the include graph, and the sole published `SourceMaterializer`:

```java
public record SourceId(NormalizedPackPath path) {}
public record SourceKey(
    DimensionKey dimension,
    String programName,
    ShaderSourceStage stage,
    SourceId source) {}
public record SourceDocument(SourceId id, List<String> originalLogicalLines) {}
public record IncludeEdge(
    SourceId including,
    NormalizedPackPath requested,
    Optional<SourceId> included,
    int logicalLine) {}
public interface SourceCatalog {
    List<SourceDocument> sources();
    List<SourceKey> roots();
    Set<ProgramKey> executablePrograms();
    Optional<SourceDocument> source(SourceId id);
    List<IncludeEdge> includeEdges();
    SourceMaterializer materializer();
}

public interface SourceMaterializer {
    MaterializationResult materialize(
        SourceKey root,
        MacroContribution contribution,
        GeometrySourceRequest geometry);
}

public sealed interface MaterializationResult {
    record Available(MaterializedSource source) implements MaterializationResult {}
    record Unavailable(SourceKey root, List<EngineDiagnostic> diagnostics)
        implements MaterializationResult {}
}
public record SourceMap(Map<Integer, SourceId> files, List<SourceMapping> mappings) {
    public Optional<SourceId> sourceForFileNumber(int fileNumber) {
        return Optional.ofNullable(files.get(fileNumber));
    }
}
public record SourceMapping(
    int startOffset, int endOffset, AttributedSourceLocation location,
    List<AttributedSourceLocation> expansionTrace) {}
public record MaterializationFingerprint(String value) {}
public record MaterializedSource(
    SourceKey root,
    String transformedText,
    SourceMap sourceMap,
    DeclaredUniformCatalog declaredUniforms,
    ShaderLanguage language,
    GeometrySourceForm geometry,
    List<EngineDiagnostic> diagnostics,
    MaterializationFingerprint fingerprint) {}


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
public record SourceSpan(
    SourceId source,
    int startOffset,
    int endOffset,
    int startLine,
    int startColumn) {}
public enum LegacyGeometryExtension { GL_ARB_GEOMETRY_SHADER4 }
public record LegacyGeometrySite(
    SourceKey root,
    SourceSpan extensionSpan,
    SourceSpan maxVerticesSpan) {}
public record LegacyGeometryConfig(
    SourceKey root,
    LegacyGeometryExtension extension,
    int maxVertices,
    LegacyGeometrySite site) {}


public record ShaderLanguage(
    int version, boolean explicitVersion, Optional<GlslProfile> explicitProfile,
    List<ShaderExtensionDirective> extensions) {}
public enum GlslProfile { CORE, COMPATIBILITY }
public enum ExtensionBehavior { REQUIRE, ENABLE, WARN, DISABLE }
public record ShaderExtensionDirective(
    String name, ExtensionBehavior behavior, AttributedSourceLocation location) {}
public record GeometryLayout(
    GeometryInputPrimitive input, GeometryOutputPrimitive output, int maxVertices) {}
public record GeometryLayoutDeclaration(
    Optional<GeometryInputPrimitive> input, Optional<GeometryOutputPrimitive> output,
    OptionalInt maxVertices, AttributedSourceLocation location) {}
public sealed interface GeometrySourceRequest {
    record None() implements GeometrySourceRequest {}
    record PreserveNative(LegacyGeometryConfig expected) implements GeometrySourceRequest {}
}
public sealed interface GeometrySourceForm {
    record None() implements GeometrySourceForm {}
    record CoreLayout(GeometryLayout effective, List<GeometryLayoutDeclaration> declarations)
        implements GeometrySourceForm {}
    record NativeLegacy(LegacyGeometryConfig config, GeometryLayout effective,
        List<GeometryLayoutDeclaration> declarations) implements GeometrySourceForm {}
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
`SourceCatalog.sources()` contains each indexed physical file exactly once, including include-only
files, keyed by its `SourceId`; `roots()` contains only compile roots keyed by `SourceKey`. Sources
order by `SourceId.path`, while roots order by dimension, source path, program name, and stage.
`executablePrograms()` is the immutable distinct ascending projection
`roots().map(root -> ProgramKey(root.dimension(),root.programName()))`, excluding the two virtual-pre
names. One or more `.vsh`, `.fsh`, or `.gsh` roots qualify a key once; partial stage sets still
qualify, and include-only files do not. `SourceKey` is valid only for a root whose non-empty program
name and stage are derived from its filename and whose dimension and `SourceId` match the indexed
physical file. `source(id)` returns empty for an unknown ID. `IncludeEdge` endpoints are canonical
`SourceId`s; a shared include remains one document and graph node, and each physical include
location remains one edge regardless of how many roots reach it. Edges order by including ID and
logical line; a missing target has an empty `included`.
The catalog and its materializer retain defensive snapshots and no reader, archive lease, path
handle, or mutable builder.
The materializer retains the containing configuration's finalized `MacroConfiguration`, including
its `CompanionOptionMacros` value, alongside `options().state()`. Every call uses that same-build
snapshot; there is no companion-state argument, live policy lookup, or post-jcpp shader patch.
Changing either boolean requires a fresh atomic load, not a new `MacroContribution`.
`MaterializedSource.root()` identifies the requested root. `transformedText()` returns the exact
final transformed GLSL `String`; `sourceMap()` describes that exact string and
`sourceForFileNumber(n)` returns the mapped `SourceId` or `Optional.empty()` for an unknown number.
`diagnostics()` is an immutable list of non-fatal source-attributed diagnostics; unavailable roots
are returned only through `MaterializationResult.Unavailable`.
`MaterializedSource.declaredUniforms()` returns the catalog above. Phase 3 first constructs the
`CanonicalDeclaredUniformPayload` from the catalog schema version and immutable declaration list;
that payload has no `materialization` field. It hashes the payload's deterministic canonical
encoding with the other §4.10 materialization inputs, then embeds the completed result in both
`MaterializedSource.fingerprint()` and `DeclaredUniformCatalog.materialization`, which are exactly
equal. The declarations above are canonical; the binding data contract is the closed type algebra,
immutable declaration order, source attribution, canonical payload exclusion, and fingerprint
equality. Widths, matrix dimensions, array extents, sampler combinations, and recursively frozen
struct fields are validated against the source's effective GLSL version and active extensions.
Array extents retain their validated positive constant extent or the closed `UNSIZED` variant; no expression text or mutable syntax
node escapes.
All catalog documents, include edges, maps, diagnostics, and materialized values remain valid after
the input lease closes; no published value retains filesystem or mutable provider storage. A
successful materialization is therefore a self-contained snapshot that Phase 4 may retain only
with its `MaterializationFingerprint`.
`Available` means preprocessing, geometry classification and the **complete** declaration catalog
succeeded; it does not claim GLSL compile/link success or draw compatibility. The exact algorithms,
closed-form invariants and attribution of the new language/geometry/map values are §4.5,
incorporated by §5.1. The removed `GeometryTranslationRequest`, `GeometryTranslationPlan` and
`LegacyGeometryRewriteSite` have no aliases or successful compatibility path in schema `CURRENT_SCHEMA_VERSION`.

### 2.3 Load pipeline

The load is a transaction with no externally visible partial state. `OFF` short-circuits to
`PackLoadResult.Off`; every other selection follows this pipeline:
The selection-first, pre-I/O request validation in §5.1 precedes step 1, including required
`CompanionOptionMacros` on every non-`Off` request; it never validates that field for `Off`.

1. enumerate and resolve the selected candidate;
2. open one bounded `PackInput` lease, locate the effective `shaders/` root, index files, read
   privately owned immutable bytes or bounded per-file read-failure markers, and close the
   folder/archive lease. No deferred reopening occurs; §4.1 classifies markers before publication;
3. build base and `world-128`…`world128` source sets using OF dimension semantics;
4. decode sources, allocate stable source IDs, parse includes, and build the directed include
   graph;
5. compute weakly connected components, discover the option catalog without expanding includes,
   and construct its default baseline;
6. for a filesystem selection, derive the target from the authenticated selected candidate, read
   it through `persistenceFiles`, and finalize the default-plus-persistence `OptionState`; `Internal`
   uses the baseline or reissues the authenticated session snapshot as a fresh catalog-bound state
   under D-P3-65 below. Validate the supplied global engine options separately;
7. assemble standard A–G macros from the validated identity/capability data, retaining the
   required companion pair and global settings as immutable inputs; do not finalize old-light
   option macros yet;
8. preprocess `shaders.properties` exactly once with A–G only and parse it, capturing decoded
   texture declarations before collapse; derive known specs and validate profiles/screens/textures/
   custom declarations/program state while preserving unresolved/invalid texture occurrences.
   Classify retained read failures against actual source duties and freeze the declared-reference
   `PackAssetSnapshot` view under §4.1; perform no new input access or copying of payloads.
   Resolve D-P3-64's old-light booleans from those parsed flags and global preferences, then
   finalize `MacroConfiguration` before any shader jcpp. Acquire every locale under §4.3;
   Option discovery above reads original source declarations and include topology, not shader
   jcpp output. Thus keeping catalog discovery/default/session finalization before this properties
   parse introduces no dependency on old-light macro results; properties never receive option
   macros and shader analysis starts only in the next step.
9. run plan-independent analysis of active shader roots with that same finalized option state
   and companion pair: expand includes with numeric `#line` attribution, establish the standard
   and option macro environment before jcpp,
   rewrite every captured option occurrence in the expanded stream, then evaluate conditionals
   with jcpp, preserving any attributed legacy geometry pair without translating it;
10. scan directives/declarations and fold them into immutable `ResourceRequirements`;
11. snapshot each pack ID-map file's presence, preprocess/parse it with standard A–G macros only,
    and for present block and entity files also produce isolated forced-`MC_VERSION=11300` parses;
12. validate cross-field invariants, compute a fingerprint containing the finalized option state,
    both companion booleans, and the complete retained texture-declaration payload, then atomically
    publish `PackConfiguration`; final-source debug dumping occurs only on later successful
    materialization.

The dimensions map always contains a `BASE` entry. A non-empty `world<id>` directory contributes
one `OVERRIDE` entry whose `sourceRoots` contain only that directory's `.vsh`/`.fsh` roots; it
does not merge base programs. An empty directory contributes one `DISABLED` entry with no roots.
For an ID with no physical directory, the map entry is absent and consumers select `BASE`.
Entries are ordered base first and then ascending world ID; every root's `SourceKey.dimension()`
equals its containing key. Phase 7 performs selection, while Phase 12 may inspect the mode.

### 2.4 Internal data relationships

```text
PackInputSnapshot
  ├─ SourceCatalog ─ IncludeGraph ─ OptionCatalog ─ SourceMaterializer
  ├─ PackAssetSnapshot ─ declared paths + shared immutable bytes / missing / unreadable outcomes
  ├─ ShaderPropertiesModel ─ Option/Profile/Screen/Texture/Expression/ProgramState models
  ├─ DirectiveScan ─ ResourceRequirements
  └─ IdMappingInput (four file states + ordinary/forced block and entity rules)
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
| `version.<mcver>=<edition>` | `MinimumEditionRule.minecraftVersion()` retains the exact decoded key suffix and `minimumEdition()` the exact decoded value; parsed/canonical values are comparison-only under D-P3-52, whose grammar, comparator, aggregation, and malformed-rule behavior remain provisional; status is closed `COMPATIBLE` or `REQUIRES_NEWER_EDITION` | Phase 7 activation UX, Phase 12 list | App F.2 (minimum-edition meaning only); D-P3-52/D-P3-53 (local interoperability/projection policy and authority gap); `compatibilityVersionAndEditionGrammarComparatorVectors`, `minimumEditionRule_sourceAccessorProjections` |
| switch `#define NAME` / `// #define NAME` | `OptionCandidate.Switch`; only confirmed references become options | Phase 12 UI, Phase 4 source request | App F.3; `switchOption_onOffAndTooltip`, `optionTooltip_terminalBangPreservedAndRed` |
| same-file `#ifdef`/`#ifndef` confirmation | A candidate is eligible only from a confirming reference in that original file; WCC analysis scopes duplicate merging and cannot promote an unconfirmed candidate | Phase 3 | App F.3; `switchOption_sameFileConfirmation` |
| variable `#define NAME value // tooltip [values]` | `OptionCandidate.Variable`; default is inserted into allowed values | Phase 12 | App F.3; `variableOption_defaultAutoAdded` |
| const-option whitelist | Explicit table for `shadowMapResolution`, `shadowMapFov`, `shadowDistance`, `shadowDistanceRenderMul`, `shadowIntervalSize`, `generateShadowMipmap`, `generateShadowColorMipmap`, `shadowHardwareFiltering`, `shadowHardwareFiltering0/1`, all documented `shadowtex0/1Mipmap`, `shadowcolor0/1Mipmap`, `shadowtex0/1Nearest`, `shadowcolor0/1Nearest` capitalization aliases, `wetnessHalflife`, `drynessHalflife`, `eyeBrightnessHalflife`, `centerDepthHalflife`, `sunPathRotation`, `ambientOcclusionLevel`, `superSamplingLevel`, and `noiseTextureResolution`; visibility requires values or slider/profile/screen reference | Phase 12 | App F.3; `constOption_completeWhitelistAndAliases` |
| compile-time option application | expand includes with numeric `#line` attribution first, establish the standard macro environment, rewrite every captured switch/value span in the expanded stream, then evaluate conditionals | Phase 4 requests materialization | §4.2/§4.7/App F.3; PD §7.3 `[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/option/OptionAnnotatedSource.java]`; D-P3-4; `optionRewrite_onlyCapturedSpan`, `optionRewrite_includedChangedOptionOrder`, `optionRewrite_roundTripMaterialization` |
| per-pack `shaderpacks/<pack>.txt` | a typed acquisition of an authenticated current, available filesystem candidate yields its exact direct-child entry name plus `.txt`; every rejected candidate is observable before persistence I/O, the load transaction reads changed values into its baseline before option-dependent work, and the codec writes changed pack options only | Phase 7 uses bundle-issued access for load; Phase 12 invokes standalone read/write | §4.7; PD §14; `persistence_targetAcquisitionOutcomesAndLinearization`, `persistence_directNameContainmentAndSymlinkSafety`, `load_persistedOptionsAffectConfiguration`, `persistence_packChangedOnlyRoundTripAndApply` |
| global `optionsshaders.txt` equivalent | separate ISO-8859-1 Properties codec overlays valid persisted entries on its supplied baseline, returns the exact §4.3 result matrix, and writes every validated global entry without mixing pack options | Phase 12 invokes read/write | §4.7; `persistence_globalCodecMatrixAndExactOutput` |
| ambiguous option defaults | `OptionAvailability.DISABLED_AMBIGUOUS`; retains locations and diagnostic, cannot be changed | Phase 12 displays disabled | App F.3; `optionAmbiguity_conflictingDefaultsDisabled` |
| `option.<NAME>[.comment]` | `LangDecorations.option` | Phase 12 | App F.3; `lang_optionLabelsAndComments` |
| `value.<NAME>.<val>` | `LangDecorations.value` | Phase 12 | App F.3; `lang_valueLabels` |
| `prefix.<NAME>` / `suffix.<NAME>` | `LangDecorations.affix` | Phase 12 | App F.3; `lang_prefixSuffix` |
| `profile.<NAME>[.comment]` in `.lang` | `LangDecorations.profile` | Phase 12 | App F.3; `lang_profileLabelsAndComments` |
| `screen.<NAME>[.comment]` in `.lang` | `LangDecorations.screen` | Phase 12 | App F.3; `lang_screenLabelsAndComments` |
| `sliders=<option list>` | Ordered `SliderSet`; unknown/non-variable names diagnose but do not invalidate other entries | Phase 12 | App F.3; `sliders_orderAndUnknownEntry` |
| `profile.NAME=<tokens>` | `ProfileModel` retains one expanded profile; catalog-valid state yields the first exact aggregate match by descending constraint count/source order or `Custom`, while incompatible state returns `InvalidState` | Phase 4 consumes disabled programs; Phase 12 selects | App F.4 (syntax, inference, `Custom`); PD §7.3 `[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/option/ProfileSet.java]` (constraint-count precedence); D-P3-4; `profiles_inferenceMatchCustomAndInvalidState` |
| `screen=<entries>` | main `ScreenModel`; configured columns default to two and are raised to `ceil(expandedSlotCount / 9)` when that minimum is larger | Phase 12 | App F.4 (syntax, default, beyond-18 requirement); D-P3-55 and its §G7-qualified behavioral observation; `screen_mainEntries`, `screen_columnsExpandedSlotFloor` |
| `screen.NAME=<entries>` | named `ScreenModel`; the count is taken after `*` expansion and includes each retained ordinary option, applicable subscreen/profile, and empty layout slot | Phase 12 | App F.4 (entry syntax); D-P3-55 and its §G7-qualified behavioral observation; `screen_subscreenEntriesAndReferences`, `screen_columnsExpandedSlotFloor` |
| `screen.columns=N` | main configured positive column count, default two; the expanded-slot nine-row minimum may raise it | Phase 12 | App F.4 (default and widening requirement); D-P3-55 and its §G7-qualified behavioral observation; `screen_mainColumns`, `screen_columnsExpandedSlotFloor` |
| `screen.NAME.columns=N` | named configured positive column count, default two; the expanded-slot nine-row minimum may raise it | Phase 12 | App F.4 (default and widening requirement); D-P3-55 and its §G7-qualified behavioral observation; `screen_subscreenColumns`, `screen_columnsExpandedSlotFloor` |
| `texture.<gbuffers\|deferred\|composite>.<sampler>[.0-9]` pack-relative PNG path | `CustomTextureSpec.PackPath`; under D-P3-38's explicit interpretation of App F.5, the normalized final segment must have a non-empty stem and literal lowercase `.png` suffix, while the duplicate discriminator remains separate from the sampler | Phase 13 | App F.5 (pack-relative PNG path only); D-P3-38 (extension-case/stem interpretation); `texture_packPathPngCaseAndLastValid` |
| same key, `minecraft:` asset/live texture | `CustomTextureSpec.MinecraftResource`; keeps `_n`/`_s` and dynamic/atlas identity as text | Phase 13 | App F.5; `texture_minecraftDynamicAndCompanionSuffix` |
| same key, raw form | `CustomTextureSpec.Raw` with type, internal format, exact dimensions, pixel format/type; malformed arity or an unknown/incompatible format token warns and omits only the executable effect while retaining an `INVALID_VALUE` declaration | Phase 13 | App F.5; `texture_rawAllFourTypesAndArity`, `texture_rawFormatDomainsAcceptedRejected`, `texture_rawIntegerTransferCompatibility` |
| `.0`–`.9` duplicate discriminators; extra/unknown `texture.*` key segments | Numeric suffixes avoid duplicate property keys, never request filter/wrap. Extra/unknown keys retain exact `UNRESOLVED_KEY` declarations and warnings without executable specs, stripping or base-key aliasing. D-P3-67 removes the unspecified sampling-suffix requirement, not lossless capture | Phase 3 parsing; Phase 13 consumes existing specs | App F.5; `docs/decisions/U1_TEXTURE_SAMPLING.md`; D-P3-59/D-P3-67; `texture_declarationsLosslessBeforeReduction`, `texture_unresolvedNeverAliasesBase` |
| same texture's `.mcmeta` blur/clamp | `TextureSidecarRef` is retained without interpreting it; `assets()` acquires its already-snapshotted bytes, not a reopened path. This is not evidence for property-key suffix grammar | Phase 13 | App F.5; `texture_sidecarReferencePreserved` |
| stage mapping | `GBUFFERS` applies to gbuffers+shadow, `DEFERRED` to deferred, `COMPOSITE` to composite+final | Phases 4/13 | App F.5; `texture_stageExpansion` |
| multiple texture types on one unit | preserve `(stage,sampler,duplicateDiscriminator)` keys; Phases 4/13 derive sampler type from program declarations and later validate one type per unit per program | Phases 4/13 | App F.5; `texture_sharedUnitSamplerTypeDerivedLater` |
| `texture.noise=<pack path>` | `NoiseTextureSpec.Override`; otherwise generated-noise requirement remains | Phase 13 | App F.5; `texture_noiseOverride` |
| `uniform.<float\|int\|bool\|vec2\|vec3\|vec4>.<name>` | source-ordered `CustomExpressionDecl(UNIFORM,type,exactName,unescapedRawExpression,ordinal,attribution)`; all six closed types are accepted and no expression evaluation occurs here | Phase 11 evaluates, Phase 6 uploads | App F.6; `customDecl_allUniformTypesRawExpressionAndAttribution` |
| `variable.<type>.<name>` | the same record with `VARIABLE`; every valid occurrence remains in the immutable list, including exact-key and same-name duplicates | Phase 11 | App F.6; `customDecl_allVariableTypesAndDuplicatesRetained` |
| F.6 constants/parameters/operators/functions/exclusions | expression text after Java-Properties unescaping and declaration occurrence order are lossless; the vocabulary is not pre-validated or evaluated by Phase 3 | Phase 11 | App F.6; `customDecl_expressionTextLosslessAfterUnescape`, `customDecl_orderOrdinalAndFingerprint` |
| F.6 precipitation rule | Preserve the behavior handoff: render precipitation iff `biome_precipitation != PPT_NONE`; classify rain at `temperature >= 0.15`, otherwise snow; this row has no Phase 3 parser/model assertion | Phase 7 | App F.6; Phase 7 test `precipitation_noneAndTemperatureBoundary` |
| `alphaTest.<prog>` | parsed `AlphaTestSpec(OFF or func/ref)` with all documented funcs | Phase 4 | App F.7; `programState_alphaTestAllFuncs` |
| `blend.<prog>` | parsed `BlendSpec(OFF or color pair plus optional alpha pair)` with the 15 documented factors | Phase 4 | App F.7; `programState_blendArityAndFactors` |
| `scale.<prog>` | exactly one token expands to `ViewportScale(scale,0,0)` and exactly three supply both offsets; all values are finite in 0…1 and only executable `DEFERRED`/`COMPOSITE` names are eligible | Phase 4 applies, Phase 5 supplies estate | App F.7; `programState_scaleArityDefaultsRangeAndFamilyOrdering` |
| `flip.<prog>.<buf>` | exact executable deferred/composite and virtual `deferred_pre`/`composite_pre` names are eligible outer `ProgramKey`s; all other families warn/ignore, and `<buf>` uses §4.7's one exact canonical/legacy buffer-name normalizer before constructing the index-backed `FlipBufferKey` | Phases 4/5 | App F.7/App B.5; `bufferNameNormalizer_allCanonicalLegacyAndRejected`, `programState_flipFamilyVirtualPreAndInvalidBuffer` |
| `program.<prog>.enabled` | Phase-3-owned small Boolean-option AST (`!`, `&&`, `\|\|`, parentheses, switch names), evaluated against immutable `OptionState`; disabled means absent to Phase 4 backup chain | Phase 4 | App F.7; `programEnabled_booleanGrammarAndFallbackSignal` |
| `shaders/world<id>/` | closed `DimensionKey`/`DimensionConfiguration`/`DimensionMode` map with base fallback, override-only roots, explicit disabled entries, numeric scan −128…128, only `.vsh`/`.fsh` in overrides, options included | Phase 7 selects | App F.8/§3.1; `dimension_publicShapeOrderingAndAbsentFallback`, `dimension_overrideNoMergeAndEmptyDisables` |
| `#include "relative"` | normalized relative to including file, within shaders root | Phase 3 | App F.8/§3.2; `include_relativeAttribution` |
| `#include "/absolute"` | normalized from shaders root | Phase 3 | App F.8/§3.2; `include_absoluteAttribution` |
| include depth ≤10 / include guards | edge depth is capped at 10; graph cycles fail affected roots even if guarded because includes precede preprocessing | Phase 3 | App F.8/§3.2; `include_depthTenAndCycle` |
| `-Dshaders.debug.save=true` equivalent | Phase 1's `-Dschmaloogium.debug.saveSources`; dumps final processed sources to runtime `shaderpacks/debug/`, never repository fixtures | Phase 3 | App F.8; `debugDump_sanitizedLocalOnly` |

### 3.3 Appendix A.3 directive-to-field map

Every row has a field, an applicability predicate, and a named test. `DirectiveScanner` recognizes
declarations after include expansion, option rewriting, and conditional preprocessing. Known classic comment
directives are accepted as both `/* KEY:value */` and `// KEY:value`; const declarations remain
case-sensitive GLSL identifiers. A malformed or out-of-family occurrence warns and is ignored
without clearing a previously valid value.

| Appendix A.3 directive | `PackConfiguration` target | Named conformance test |
|---|---|---|
| `attribute … mc_Entity / mc_midTexCoord / at_tangent` (`.vsh` only) | per-program `VertexRequirements.attributes`, directly exposed to Phase 4 for declared pre-link attribute binding and retained for Phase 10 vertex layout (§5.1) | `directive_extendedAttributeOptIns`, `directive_attributeWrongStageIgnored`, `resource_programProjectionSnapshotAndIdentity` |
| `const int countInstances=N` (`.vsh` only) | per-program `instanceCount` (positive integer) | `directive_countInstances`, `directive_countInstancesWrongStageIgnored` |
| `#extension GL_ARB_geometry_shader4` + `maxVerticesOut` (`.gsh` only) | one validated `LegacyGeometryConfig` and attributed `LegacyGeometrySite`; explicit `PreserveNative(expected)` retains extension/version/constant/built-ins and publishes `NativeLegacy`, with source-layout precedence; no two-span core translation | `directive_legacyGeometryPair`, `directive_geometryPairWrongStageIgnored`, `geometryNative_pairCardinalityAndIncludes`, `geometryNative_preservationAndMixedLayouts` |
| `uniform … shadow/shadowtex0/shadowtex1/watershadow` | `shadowDepthBuffers` minimum 1/2 | `directive_shadowDepthUniformSizing` |
| `uniform … shadowcolor/shadowcolor0/shadowcolor1` | `shadowColorBuffers` minimum 1/2 | `directive_shadowColorUniformSizing` |
| `uniform … depthtex0/1/2`, `gdepthtex` | `mainDepthTextures` minimum 1/2/3 | `directive_mainDepthUniformSizing` |
| `uniform … colortex0-7` and `gcolor`, `gdepth`, `gnormal`, `composite`, `gaux1-4` | the shared §4.7 normalizer maps the sixteen exact spellings to indices 0–7; `colorBuffers` is highest referenced index + 1 | `directive_colortexAndLegacySizing`, `bufferNameNormalizer_allParserFamilies` |
| `uniform … gdepth` | every active declaration mandates `colortex1` format `Explicit(RGBA32F)`; any explicit format other than `RGBA32F` is diagnosed as a conflict and cannot override the mandatory upgrade | `directive_gdepthUpgradeAlways`, `directive_gdepthWithPriorExplicitFormat`, `directive_gdepthWithSubsequentExplicitFormat`, `directive_attachmentFormatDefaultExplicitAndGdepthFold` |
| `uniform … centerDepthSmooth` | `centerDepthSmoothRequired=true` | `directive_centerDepthSmoothReadback` |
| `shadowMapResolution` / `SHADOWRES` | `ShadowRequirements.resolution` | `directive_shadowResolutionAllForms` |
| `shadowMapFov` / `SHADOWFOV` | `ShadowRequirements.fov`; finite degrees strictly between 0 and 180, otherwise malformed | `directive_shadowFovAllForms`, `directive_shadowFovDomain` |
| `shadowDistance` / `SHADOWHPL` | `ShadowRequirements.distance` | `directive_shadowDistanceAllForms` |
| `shadowDistanceRenderMul` | `ShadowRequirements.distanceRenderMultiplier` | `directive_shadowDistanceRenderMul` |
| `shadowIntervalSize` | `ShadowRequirements.intervalSize` | `directive_shadowIntervalSize` |
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
| `superSamplingLevel` | option/source compatibility only under D-P3-66; no ResourceRequirements field, engine-positive-level restriction, allocation, repeated draw or resolve request. Ordinary option safety/type rules still apply | `option_superSamplingLevelCompatibilityOnly` |
| `noiseTextureResolution` | `NoiseRequirement.resolution` and enabled flag | `directive_noiseTextureResolution` |
| `colortexNFormat` / `gcolorFormat`, `gdepthFormat`, `gnormalFormat`, `compositeFormat`, `gaux1-4Format` | the shared §4.7 normalizer selects the attachment; a valid format becomes `ColorAttachmentFormat.Explicit`, while absent input remains distinct `DefaultRgba` | `directive_colortexFormatsAndAliases`, `directive_colortexFormatDomainAcceptedRejected`, `bufferNameNormalizer_allParserFamilies`, `directive_attachmentFormatDefaultExplicitAndGdepthFold` |
| `colortexNClear=false` | `clear=false` and only for `DEFERRED`/`COMPOSITE` program families; no clear is issued, while any `clearColorOverride` remains explicit published data | `directive_colortexClearFamilyFilterAndOrdering` |
| `colortexNClearColor=vec4(...)` | `clearColorOverride=Optional.of(Vec4f(...))` and only for `DEFERRED`/`COMPOSITE` program families; absence uses the index baseline, so an explicit transparent black remains distinguishable | `directive_colortexClearColorFamilyFilterAndOrdering`, `directive_explicitTransparentBlackRemainsExplicit` |
| `colortexNMipmapEnabled=true` | per-program `mipmappedAfterPass` only for `DEFERRED`/`COMPOSITE`/`FINAL` program families; the same immutable set feeds Phase 4 registry state and Phase 5 pass-mipmap requirements (§5.1) | `directive_colortexMipmapFamilyFilterAndOrdering`, `resource_programProjectionSnapshotAndIdentity` |
| `GAUX4FORMAT` (`RGBA32F`/`RGB32F`/`RGB16`) | colortex7 format request | `directive_gaux4FormatAllCommentForms` |
| no active `DRAWBUFFERS`/`RENDERTARGETS` | `DrawRouting.AllUsed`; the program writes all color buffers it uses, not an empty/no-output route | `directive_drawbuffersAbsentMeansAllUsed` |
| `DRAWBUFFERS` characters `0`–`7` or `N` | `DrawRouting.Explicit` with one ordered `DrawSlot.Attachment` or `DrawSlot.None` per character | `directive_drawbuffersPositionalSlotsAllCommentForms` |
| standalone, repeated, or all-`N` `DRAWBUFFERS` | every `N` remains one positional `DrawSlot.None`, distinct from directive absence | `directive_drawbuffersLeadingMiddleTrailingRepeatedAndAllNone` |
| modern `RENDERTARGETS` (`post-v0.5`) | `DrawRouting.Explicit` with ordered attachment slots for targets 0–15 and the later-active routing occurrence winning | `directive_rendertargetsAndPrecedence` (`post-v0.5`) |

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
| `OFF`, `(internal)`, folder, and zip discovery; nested `shaders/` root | `PackDiscovery`, deterministic bounded root selection, and `InternalPackSource` | RESEARCH §§3.1/4.1; `discovery_sentinelsFolderZipNestedRoot` |
| Archive lifetime and path containment | one bounded `PackArchiveLease`, closed after snapshot; absolute, drive, NUL, `..`, and symlink escapes rejected | RESEARCH §§3.1/4.1; adopted lease discipline PD §7.1, with stricter guards as Phase 3 safety design; `discovery_archiveClosesAndRejectsEscapes` |
| Source identity and compiler attribution | `SourceCatalog` publishes ordered stage roots and one distinct ascending dimension/name `executablePrograms` projection; `SourceMap` and materializer retain attribution | RESEARCH §§3.2/4.2 and App A.2 source presence; `sourceCatalog_programProjectionStagesDimensionsAndAbsence`, `sourceMap_numericLookupAndMaterializedLifetime` |
| Global compute-source recognition (`post-v0.5`) | For each known non-gbuffers program `p`, P3-C19 indexes only base-folder `p.csh` and `p_a.csh`…`p_z.csh`; the `SourceKey` uses associated name `p`, stage `COMPUTE`, and the distinct physical `SourceId`, then materializes through the same include/source-map path. Dimension folders remain `.vsh`/`.fsh` only | RESEARCH §§3.1/3.6.2; `sourceCatalog_computeRootsApplicabilityAndDimensions` (`post-v0.5`) |
| Standard macro identity families | `MacroConfiguration` closes OF A–G plus the exact shader-only option set `MC_NORMAL_MAP`, `MC_SPECULAR_MAP`, `MC_RENDER_QUALITY`, `MC_SHADOW_QUALITY`, `MC_HAND_DEPTH`, `MC_OLD_HAND_LIGHT`, `MC_OLD_LIGHTING`, `MC_FXAA_LEVEL`, and the separately named capability-feature, engine-identity, override, and reserved-contributor families; it adopts PD's version/parser shape but rejects enumerate-all extension emission | RESEARCH §3.5; PD §7.6 `[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/gl/shader/StandardMacros.java]`; D-P3-9; `macro_standardAndShaderPayloadFamiliesClosed`, `macro_optionFamilyMembershipAndValues`, `macro_optionNumericSerializationAliasesAndThresholds` |
| Companion option-macro input | `CompanionOptionMacros` is captured before load-time shader jcpp, retained in `MacroConfiguration`, and reused by same-build materialization with both booleans hashed | RESEARCH §3.5; `docs/phase13/v1/PHASE_13_DOC.md:867-873` states the preliminary demand — the required pair supplied before load/jcpp and retained by Phase 3 §5.1 in `MacroConfiguration`, same-build materialization and both fingerprints — with the R1 adoption record at `docs/phase13/v1/PHASE_13_DOC.md:1961-1965` and its §11.5 R1 row at `:2451`; D-P3-58; `macro_companionTypedStateBeforeJcpp`, `macro_companionMissingNonOffAndOffShortCircuit`, `macro_companionSameBuildAndFingerprints` |
| Conditional preprocessing and substitution | jcpp adapters implement define/undef/if-family/defined/substitution for shaders and properties | RESEARCH §3.5; `preprocess_completeConditionalGrammarAllInputs` |
| Public discovery/load and identity values | sealed tokens, explicit invalid generations, durable references, snapshot lifecycle, closed resolver/load outcomes, identity, and fingerprints | §2.2/§5; `discovery_invalidGenerationOrderingAndPublication`, `discovery_referenceRestartResolutionOutcomes` |
| Block/item/entity short, namespaced, property, and legacy `id:meta` entry rules | D-P3-73 current-schema typed metadata interval unions and Literal/IntegerInterval property alternatives, one rule per selector with unchanged source order/state/origin/era; bounded matching is P9-only | RESEARCH §3.7; shipped `doc/shaders.txt:544` → `doc/properties_files.txt:97–122`; `idMap_allDocumentedRuleForms`, `idMap_typedMetadataSetsAndPropertyRanges`, `idMap_rangeBoundariesNoExpansionOrOvermatch`, `idMap_rangeIdentityAndInspection` |
| `%namespace:path` tag-selector interpretation | Under D-P3-47/D-P3-49, current-schema `IdMappingInput` uses `%` plus the exact provisional lower-case ASCII grammar in §§4.9/5.1 to retain `SelectorKind.TAG`; Phase 9 supplies the 1.12 shim and entries-before-tags priority | RESEARCH §3.6.8 supplies generic shim/priority only; concrete grammar remains local pending clarification; `idMap_selectorKindEntryAndTag`, `idMap_tagIdentifierGrammarBoundaries` |
| Block/entity compatibility preprocessing extension | present BLOCK/ENTITY inputs publish ordinary A–G results and a separate result made by replacing only `MC_VERSION` with `11300`; no selection or merge occurs here | RESEARCH §3.7 supplies the unchanged classic base; D-P3-72 explicitly extends the existing ENTITY bridge to BLOCK for P9 R1 C1; `idMap_entityForced11300Isolated`, `idMap_blockForced11300SameSource` |
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
`PackIdentity` is the immutable pair of the selected validated root and its canonical-path-ordered
content-hash map. Hash strings are opaque non-empty values; equality and fingerprinting use the
selected root and every ordered path/hash entry. It carries no display name or host path.

Every filesystem candidate has a durable `filesystemReference`; both sentinels have
`Optional.empty()`. Its `canonicalValue` is `d:` for a directory or `a:` for an archive followed
by every UTF-8 byte of the NFC-normalized direct-child entry name encoded as uppercase `%HH`.
Construction rejects an empty/noncanonical value. The value contains no host root, separator,
display text, content hash, or generation and is the sole serialized filesystem-selection form.
Distinct physical names that normalize to one value are retained as an ambiguous collision.

`resolveFilesystemCandidate(reference,current)` authenticates `current.generation()` and consults
private snapshot state, never caller-supplied candidates. The latest retained successful generation
for that directory key yields `Resolved`, `Missing`, `Ambiguous`, or `KindChanged`; a foreign,
superseded, evicted, or non-directory-keyed invalid generation yields `InvalidSnapshot`. Failed
discovery still returns fresh sentinel IDs but publishes no keyed snapshot, as binding §5 specifies.
`Resolved.candidate()` is the current snapshot ID, so restart recovery is discover, resolve, load.
`discoveryLimits()` exposes one immutable engine-configured policy:
`maxCandidatesPerSnapshot >= 2`, `maxSnapshotBytes > 0`, and
`maxRetainedDirectorySnapshots > 0`; construction also requires the byte limit to admit the fixed
two-sentinel overflow result.
Candidate count includes both sentinels. Snapshot bytes are the §4.10 canonical encoding of every
candidate and result diagnostic, recursively using declaration order, variant/enum tags, and all
public scalar/string/list fields. If complete sorted enumeration would exceed either per-snapshot
limit, discovery publishes for that real-directory key only fresh `Off` and `Internal` candidates
plus exactly one diagnostic with `ERROR`, `CHAT`, key
`schmaloogium.error.pack.discovery_limit`, `List.of()`, empty detail, and
`schmaloogium.pack`; that compact result must itself fit the configured limits and supersedes the
key's prior generation.

After each successful completion publishes or replaces its key, the bundle marks that key most
recent and evicts least-recently-successfully-completed keys until the retained count is within
`maxRetainedDirectorySnapshots`; completion order breaks all recency ties. Invalid discovery
neither mutates nor refreshes the table. Eviction makes that generation and its candidate IDs
superseded: resolution returns `InvalidSnapshot`, target acquisition returns
`SUPERSEDED_GENERATION`, and load returns `INVALID_SELECTION`. A previously acquired
`PackOptionsTarget` remains valid, and a durable reference can recover only after fresh discovery.

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

**D-P3-69 — immutable binary acquisition (2026-09-08; binding through §5.1).**
The §2.2 types live in `com.schmaloogium.engine.pack`; permitted implementations are
package-private final classes in that package, with no public constructors. P3 alone issues them.
`PackConfiguration.assets` is required, non-null and from the very same load snapshot as
`pack`/`sources`/`properties`; its `pack()` is that exact immutable `PackIdentity`. Configuration
construction rejects foreign-load pairing even if structural identities happen to compare equal.
The private issuance association is not a digest input and has no public token or epoch.

After property validation and last-valid duplicate reduction, the declared lookup domain is
the union of every surviving `PackPath.image`, `Raw.bytes`, and noise `Override.image`, plus
each one's canonical string with literal `.mcmeta` appended **even when absent**. Only these
normalized paths are admitted. Minecraft resources/atlases, generated noise, unresolved or
invalid declarations and shadowed specs add no paths. Paths use exactly the existing
`NormalizedPackPath` grammar and selected-root coordinate system, not host paths or a new
normalizer. Unsafe appended paths still obey the existing fatal safety/length bounds.
Structural path equality, not object identity, controls lookup. Repeated references share one
entry; `manifest()` is deep immutable and ordered by unsigned UTF-8 canonical path bytes.

`acquire(null)` returns `InvalidReference(NULL_PATH)`; an otherwise valid normalized path outside
that domain returns `InvalidReference(NOT_DECLARED)` before checking index existence. Malformed
strings cannot construct `NormalizedPackPath` under its existing constructor contract; they
are never repaired, treated as missing, or used for I/O. An admitted path without an indexed
regular file (including a directory at that path) returns `Missing(path)`. An admitted indexed
regular file returns `Acquired(bytes)`, including a zero-byte file, except a proven sidecar-only
read failure returns `Unreadable(path)` under the classification below. Every manifest row has
`AVAILABLE` with present nonnegative `byteCount` and SHA-256 of exactly those bytes, or
`MISSING`/`UNREADABLE` with both optionals empty. All fields/optionals are non-null. Each acquired
byte view's `metadata()` equals the manifest row; repeated lookup is deterministic and does no I/O.
There is no hidden input provider, filename resolver, exception-bearing result, or deferred retry.
`PackAssetMetadata` construction enforces that closed availability/optional matrix and exactly
64 lowercase hexadecimal digits for a present digest. Result records reject null components.
Snapshot and byte-view capabilities are nonserializable, identity-equal issuer objects; metadata
and closed results use structural record equality. These capability identities are excluded from
all fingerprints. Per-load path/metadata count is bounded by the existing indexed-entry and
validated declaration bounds, including at most one adjacent absent row per distinct primary path.

`PackAssetBytes.openCursor()` returns a fresh **read-only heap `ByteBuffer`**, position zero,
limit/capacity equal to `metadata().byteCount().getAsInt()`, byte order `BIG_ENDIAN`.
Its backing payload is P3-private: writes/array exposure through the buffer or derived views
are prohibited by Java read-only-buffer semantics. Cursor position/limit/order are caller-local;
duplicates/slices share immutable storage, never a mutable alias. Consumers may decode via
buffer reads or copy into their own storage, but no API publishes a mutable byte array or
accepts mutable storage back. Views are immutable/thread-safe; individual cursors follow
ordinary caller-confined `ByteBuffer` cursor rules. No cursor owns a file/archive handle.
The existing inbound `ImmutableBytes.copy()` is not this outgoing capability; Internal input
is defensively copied exactly once into P3-owned storage and is never trusted as immutable.

Every indexed regular-file payload is attempted in step 2 before the lease closes, within the
existing finite `PackInputLimits` and parser/source bounds. Count actual uncompressed bytes
with overflow-checked arithmetic, enforcing limits before allocation/growth; each file must
also fit `Integer.MAX_VALUE` for the declared buffer domain. A filesystem excess is
`INPUT_LIMIT_EXCEEDED`; any Internal provider/recount/domain violation is
`INTERNAL_SOURCE_INVALID`. Bounds apply even to unused files and sidecars. Assets share one
owned payload per path with the private load snapshot; the view/manifest adds only bounded
per-entry metadata, not whole-pack or per-reference copies. Released parsing scratch and
inspection's container bytes are not retained by assets. The bundle maintains no global
asset cache: configuration/derived holders retain only the snapshots they actually need.
Dropping all such references and cursors releases their heap storage; a cursor may keep its
own payload alive, never an archive or the whole snapshot. No close/invalidation operation
can make an already issued immutable view unreadable.

Failure precedence retains unsafe-before-bounds-before-unreadable and all existing fatal
candidate/index/container/configuration/primary-source failures. D-P3-69's only exception is
the approved optional owned-sidecar recovery: step 2 records a bounded path/UNREADABLE marker
for an individually indexed, containment-checked regular file whose read fails. Discard partial
bytes and exception detail, count bytes already consumed, and continue only if enumeration and
the enclosing archive remain trustworthy. Missing/unknown size or failed integrity information
that prevents establishing the existing bounds is fatal, not an exempt sidecar. Container
corruption, index failures and unavailable candidates still fail `INPUT_UNREADABLE`; safety and
limits are never demoted. Internal input has no failure-marker variant: its provider throws/
invalid bytes/manifest still fail `INTERNAL_SOURCE_INVALID` under the existing inbound contract.

After reading the original source/include topology and the single A–G properties parse,
classify each marker against **all** P3 duties, not merely its extension. It qualifies only
if its path is the adjacent sidecar of a surviving owned spec and is not a shader/root/include,
properties/configuration/lang/ID-map input, or any surviving primary PNG/raw/noise source.
Include duties include every statically discovered include edge, not just active branches.
Any unresolved duty/failed prerequisite preventing that proof makes the marker fatal. A path
with both sidecar and non-sidecar duties is fatal; an unreadable unused file also retains the
old fatal rule. Required reads consult markers and fail rather than silently treating them
as absent. This is private staged classification before publication, not parsing partial data;
no shader materialization/public configuration escapes pending classification.
Each proven sidecar-only marker becomes immutable `UNREADABLE` metadata and
`PackAssetAcquisition.Unreadable(path)`. Its indexed regular-file existence still makes the
associated `TextureSidecarRef` present. Its unknown contents have no fabricated hash/size.
P13 alone emits the approved one recovery warning per sidecar outcome, fingerprints it and
atomically retains the usable source with its PNG/raw/noise-specific baseline; P3 emits no
duplicate recovery warning. This is a reachable successful-load path, not post-close I/O.

A missing sidecar has an empty reference Optional and adjacent `Missing`; malformed/empty
successfully read sidecars have present references and `Acquired` for P13's atomic JSON recovery.
Malformed primary PNG/raw bytes likewise remain Acquired for P13 source validation; primary
**read failure** is fatal P3 input failure, never converted to sidecar fallback. P13 preserves
its approved source-before-sidecar decode/legality and diagnostic precedence.

Deleting/replacing the ZIP or folder after `Loaded` cannot affect any acquisition or identity.
Resource-only `NONE` retains and reuses the same configuration/assets; a Minecraft resource
epoch does not replace or relabel pack bytes. A later **full load** obtains a new snapshot
under the normal discovery/request rules; retained old configurations stay unchanged.


### 4.2 Dimensions, source identities, and includes

The source index has a base set plus every physical `world<id>` directory for `id ∈ [-128,128]`.
At v0.1 a root is a regular direct child of the effective base `shaders/` directory named
`<program>.vsh`, `<program>.fsh`, or `<program>.gsh`, or a direct child of a dimension directory
named `<program>.vsh` or `<program>.fsh`. The non-empty exact filename stem is `programName`;
suffix matching is lowercase and case-sensitive. Other indexed text files are include-only unless
reached as roots under a later milestone rule. Dimension sets scan their options and never merge
base sources. A present empty directory is a successful disabled dimension. A directory outside
the numeric range is an ordinary pack path, not a dimension override. The corresponding
`DimensionConfiguration` is validated against its `DimensionKey`, and its ordered `sourceRoots`
are the catalog roots for that exact set.

Every indexed physical text file receives exactly one stable `SourceId` containing its normalized
pack-root-relative path, including any `shaders/world<id>` segment, plus a separate collision-free
integer file number for GLSL `#line`. UTF-8 with optional BOM is accepted for GLSL and `.lang`;
malformed byte sequences produce a source-local diagnostic.
Properties use ISO-8859-1/Java-Properties escape semantics.

Includes are recognized by a line-oriented, comment-unaware scanner before any conditional
preprocessing, matching the documented upstream behavior. Relative paths resolve from the
including file; leading `/` resolves from the effective `shaders/` root. Normalization reuses the
same escape guard as discovery.

`IncludeGraph` stores one node per `SourceId` and one edge per physical include location, with
canonical `SourceId` endpoints; sharing a file across roots never duplicates its node or edges.
Construction records missing-file failures without abandoning unrelated roots. A color/visited DFS
reports the first cycle with the complete edge/source-line chain, then marks every root reaching
that cycle unmaterializable. A separate depth-state DFS rejects an expansion when its longest
active path would exceed ten include edges. Include guards do not waive either check because
expansion precedes preprocessing.

Expansion is memoized by the immutable source snapshot and root; option state is excluded because
expansion precedes option rewriting.
Before included lines and after them, expansion emits numeric `#line` directives selecting the
included file's first line and the parent's next line, respectively, under §4.5's effective-language
line convention (for GLSL 1.20/1.50, write the desired next line minus one).
Every captured option occurrence is then rewritten in
this expanded attributed stream, including occurrences originating in included files.
`MaterializedSource.sourceMap()` maps the numbers to immutable `SourceId` values and lets Phase 4
attribute driver logs without filesystem access.
`SourceCatalog.roots()` is the stage-root enumeration offered to a compiler consumer, while
`executablePrograms()` is its sole stage-free program universe. Source lookup and include-edge
lookup are snapshot operations with no filesystem fallback. The numeric file table is immutable,
strictly positive, and ordered by file number, so `SourceMap.sourceForFileNumber` is the sole
absence-aware lookup for generated `#line` identifiers.

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

Tooltips are split on the literal `. ` for display. `OptionDefinition.tooltip()` preserves the
decoded tooltip string exactly, including a terminal `!`; Phase 12 marks any resulting line ending
in `!` as warning/red and removes only that final marker from displayed text. No other whitespace
or punctuation is trimmed. The locale catalog is acquired and frozen as follows (D-P3-63);
§5 incorporates these rules, not a second language parser or preselected decoration copy.

1. From the same bounded, immutable pack snapshot used by load, enumerate regular immediate
   children of the effective root's `shaders/lang/`; do not recurse, follow links, read dimension
   lang folders or consult Minecraft/resource-pack translations. Paths use existing safety and
   byte/entry limits. Accept exact case-sensitive `.lang` suffix and a nonempty stem matching
   `[A-Za-z]{2,8}(?:[_-][A-Za-z0-9]{1,8})*`. Invalid candidate stems diagnose and are omitted;
   non-lang files are ignored. Normalize only ASCII upper case to lower case and `-` to `_`,
   without trimming, Unicode case folding, locale aliases or JVM-default-locale dependence.
2. Sort eligible canonical pack paths by unsigned UTF-8 bytes. For stems normalizing to the
   same locale, diagnose each later file and retain the first entire file; never merge collision
   files, including when the winner is empty or fails decoding. This is a deliberate local
   deterministic collision policy, not claimed OptiFine behavior.
3. Decode the selected file as strict UTF-8 with optional leading BOM, then Java Properties
   logical-line/escape semantics via a character reader, never ISO-8859-1 byte decoding and
   never jcpp. Byte decoding failure diagnoses and omits that locale. Malformed logical
   occurrences diagnose and are skipped without erasing prior valid entries. Last valid
   duplicate decoded key wins in physical logical-line order; escaped values preserve their
   exact decoded text, including empty strings, spaces, punctuation and terminal `!`.
4. Dispatch exact App F.3 families into the existing nine `LangDecorations` maps:
   `option.NAME[.comment]`, `value.NAME.VALUE`, `prefix.NAME`, `suffix.NAME`,
   `profile.NAME[.comment]`, and `screen.NAME[.comment]`. For value keys split after the option
   identifier, retaining the entire remaining value token (e.g. `0.4f`). `.comment` is a terminal
   discriminator for option/profile/screen families only. Named entries use nonempty exact
   identities; bare `screen` and `screen.comment` map to empty-string keys in `screenLabels`
   and `screenComments`, respectively, for the main screen. This explicit root-key exception
   is local P12 compatibility, not a new named screen. Unknown decoration targets remain for
   later lookup and never mutate catalogs. Unsupported keys diagnose/omit; values stay unchanged.
5. Publish `OptionConfiguration.localizedDecorations()` as the sole deep-immutable authority:
   locale keys ascending unsigned UTF-8; each string-keyed map in that order; `ProfileName`
   keys by their exact `value()` bytes; `ValueDecorationKey` by option-name then value bytes.
   No null map/key/value is legal. A successfully decoded empty/no-recognized-key file produces
   a present locale with nine empty maps. Missing directory, no usable files, or all decode
   failures produce an empty outer map; a missing locale/key is absence, never an empty value.
   Constructors reject noncanonical keys/order-independent conflicting input rather than
   inventing a selected locale. All maps are defensive immutable copies in canonical order.
6. Phase 12 normalizes its requested client locale by rule 1's stem grammar/normalization;
   null/invalid requests act as missing locale. For each individual decoration key, consult
   requested locale then `en_us` (once if identical), testing key presence, not nonempty text.
   A present empty string intentionally suppresses fallback. Do not choose a first available
   locale, infer a parent language, or merge/prepublish fallback maps. If both keys are absent,
   Phase 12 §4.3.5 owns final literal/source/prettification fallback: option label uses nonempty
   source tooltip else prettified name; value uses raw text; named profile/screen uses prettified
   name; Custom uses `profile.Custom` then literal `Custom`. P12 owns the root title fallback.
   Option comment falls back to source tooltip; absent profile/screen comments mean no comment
   and absent affixes empty. Tooltip splitting/warning markers apply after selection, never
   trimming a present translation or treating empty as missing.
7. Locale selection is presentation-only: change locale without load, persistence, shader
   rebuild or source reopening. A presentation cache keys on schema, configuration fingerprint
   and normalized requested locale (one invalid/absent sentinel); reload drops old projections.
   The entire catalog, not a selected/fallback projection, participates in §4.10's fingerprint
   and §5.1.1's source-free inspection. Materializers retain their same-build state/inputs;
   a locale selection never changes configuration or materialization identity.

**Internal session options (D-P3-65, maintainer choice 2026-09-07).** Internal edits survive
switching away/back during this client session and return to defaults after restart. No durable
file, display-name target or filesystem candidate is created. `captureInternalOptions` validates
non-null state/reporter, exact state/catalog identity, Internal-origin catalog, then ordinary
catalog-state validity, in that order, returning the corresponding closed failure above.
Capture is pure, emits no I/O and does not mutate the configuration. The issuer is the existing
catalog: success creates the package-private final sealed snapshot implementation containing
defensive immutable complete option values, the issuing bundle's authentication domain, and that
Internal load's exact `PackIdentity`. The snapshot is opaque, nonserializable, identity-equal,
thread-safe, contains no catalog/reader/provider callback, and outlives selection changes.
`INVALID_STATE` reports any ordinary state-validity failure; no partial snapshot escapes.

Every non-Off load requires a non-null `internalOptions` Optional. Filesystem loads require it
empty. Internal accepts empty (defaults) or an authentic same-bundle snapshot; wrong-domain tokens
fail `INVALID_REQUEST` before provider access. After the bounded Internal snapshot and stable
identity check, a present token must match the exact structural `PackIdentity` (selected root and
entire canonical content-hash map), not display text. Mismatch is `INVALID_REQUEST` before shader
preprocessing, with no silent discard/retry. After original-source option discovery, revalidate
its complete stored values against the fresh catalog using `constructState`; any invalid outcome
is `INVALID_REQUEST`, never partial/default overlay. A valid outcome issues a fresh catalog-bound
state before properties validation, directive analysis or any shader jcpp. Old preview state is
never passed to materialization. Empty and a captured default state have equal semantic
fingerprints; token/domain/object identity is excluded, final option values remain included.

Phase 7 owns one session map keyed by exact `PackIdentity`, with at most one accepted snapshot
per identity, for its long-lived front-end bundle. It selects only the entry for the current
Internal provider identity; a new identity starts from defaults while older entries remain for
a switch back. Identity changing during a load is still the existing provider failure, not a
new key discovered mid-transaction. Switching Off/filesystem retains the map; shutdown or bundle
replacement clears it and restart cannot deserialize it. Phase 12 submits its catalog-issued
preview/profile/reset state to P7's `InternalOptionCommitter` with expected active identity,
global settings and reload effects. P7 serializes admission, authenticates expected active
Internal identity, validates/captures through that configuration's catalog, then writes any
changed globals before atomically accepting the queued republish and session preference.
Global write/capture/identity failure leaves the prior session preference and active state;
no token trust is delegated to GUI callers. Acceptance is `SESSION_ACCEPTED`, not filesystem
`COMMITTED`. A later load/render failure follows shaders-off recovery but retains the accepted
preference for retry. Discard never submits. Reset supplies complete catalog defaults, never a
caller-constructed empty state. Filesystem Apply retains its safe-write-before-reload route.
P3 owns capture/load validation but never mutates P7's session cache or accepts raw preview state
in materialization. P7/P12 own their commit/UI result algebra.

This contract is part of §5's incorporated load/options surface. P7/P12 must propagate each
closed capture/load outcome; no `PackOptionsTarget` is granted for Internal and sentinel
target rejection remains `NON_FILESYSTEM`. Inspection uses the same optional input and projects
only finalized option values, never snapshot credentials or the session map.

`OptionLineRewriter` changes only the captured token/value span, never regex-replaces a name
globally. For traceability it may append the stable comment
`// Schmaloogium: changed option <NAME>`, while `#line` restores pack attribution. Pintonium's
location-aware source rewrite and constraint-count profile ordering are structural references
`[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/option/OptionAnnotatedSource.java]`
`[V:observed — Pintonium common-shaders/src/main/java/net/irisshaders/iris/shaderpack/option/ProfileSet.java]`;
the behavior is checked against App F.3/F.4 under D-P3-4.

Persistence is exposed through the closed `OptionPersistenceCodec` and
`GlobalShaderOptionsCodec` operations in §2.2. `PackFrontEndServices.persistenceFiles(roots)` is
the sole public acquisition route for the sealed Phase-3 implementation. Its non-null
`PersistenceRootConfiguration` supplies a `shaderpacksDirectory` and game directory; acquisition
canonicalizes each with `toAbsolutePath().normalize().toRealPath()`, requires readable directories,
and returns `InvalidRoots(INVALID_REQUEST)` for null, nonexistent, non-directory, unreadable,
resolution, or security failure. `Acquired.files()` is thread-safe, belongs to
that bundle's authentication domain, retains only canonical roots, and opens/closes handles per
operation. Per-pack access is exactly `<shaderpacksDirectory>/<pack>.txt`; global access is exactly
`<gameDirectory>/optionsshaders.txt`. A filesystem load and standalone Phase 12 operations use an
`Acquired` receiver; consumers never implement the access protocol.

`packOptionsTarget(candidate)` linearizes with completed discovery publication on the same
front-end. If acquisition linearizes first, `Acquired(target)` remains valid after later discovery;
if discovery supersedes first, acquisition returns `Rejected(SUPERSEDED_GENERATION)`. The other
exact non-throwing rejections are `NULL_CANDIDATE`, `FOREIGN_DOMAIN`, `UNKNOWN_CANDIDATE`,
`NON_FILESYSTEM` for either sentinel, and `UNAVAILABLE` for a non-`AVAILABLE` filesystem candidate.
Every rejection occurs before invoking persistence access. Success returns a package-local issued
target carrying the candidate's canonical durable reference and a `fileName` made from the selected
direct-child entry's exact host name plus `.txt`; archive extensions are preserved. Since the name
comes only from that authenticated direct child, target construction accepts no separator,
absolute path, dot segment, NUL, or host path.

Read and write resolve only the fixed locations above. They normalize and check parent
containment, reject a symlink target or parent, and never probe a digest, slot, display-name-derived,
caller-sanitized, or migration alias. A host filename limit or other access failure is diagnosed
through the typed failure result rather than changing the pack-facing format. The body is
deterministic ISO-8859-1 Properties with no private identity header.

For a filesystem load, Phase 3 derives the target from the authenticated selected candidate, then
issues the catalog with the same hidden `(bundle domain, candidate-token identity)` pack key;
`Internal` uses a hidden `(bundle domain, PackIdentity)` key. Each catalog receives a fresh private catalog
identity; every state it issues carries that exact identity. The target must match the catalog's
pack key, while a state must match the exact catalog identity.

`OptionCatalog` and `OptionState` are sealed interfaces with package-private final implementations;
callers cannot construct either. `definitions()` is immutable source order, `find` and `value`
require non-null exact names, and `defaultState()` is complete: every definition occurs exactly
once, with `DISABLED_AMBIGUOUS` fixed at its retained default. Switch values are Boolean; variable
and constant values are text. A valid text is a non-empty, newline/NUL-free replacement-token
sequence containing no comment or preprocessing-directive token. Values outside a non-empty
`allowedValues` list remain valid with one warning, per D-P3-14.

`constructState(values,reporter)` requires one entry for every definition and no other entry; an
ambiguous entry is valid only at its retained default. `updateState` first validates its baseline,
rejects every ambiguous target even when the supplied value equals its default, and otherwise
changes exactly one name. Both return `Valid` with a new immutable state or `Invalid` with one
failure, using this priority: null top-level argument or map key → `NULL_INPUT`; wrong catalog
identity → `FOREIGN_CATALOG`; first unknown name in unsigned UTF-8 order → `UNKNOWN_OPTION`; first
missing name in catalog order → `MISSING_OPTION`; first null map value in catalog order →
`NULL_VALUE`; first Boolean/text mismatch in catalog order → `KIND_MISMATCH`; ambiguous mutation or
non-default construction → `DISABLED_AMBIGUOUS`; unsafe text → `UNSAFE_TEXT`. A failed operation
publishes no partial state. `validate` applies the same state checks without mutation or warnings.

`OptionConfiguration` construction requires its `state` to validate against its `catalog`.
`inferProfile` is the deliberately separate preview operation: it returns `InvalidState` for an
incompatible candidate; otherwise `Inferred` contains the first exact profile in descending
expanded-constraint count/source order, or `Custom`. Runtime source materialization and program-state
evaluation take no candidate state and always use the containing configuration's finalized
`options().state()`.

The option codec validates every request field, access, target, catalog, and state before I/O. A
null field, foreign domain, same-domain target with a different candidate credential, or state not
issued by that catalog returns `OptionPersistenceReadResult.InvalidRequest` or write
`FAILED`, with `PersistenceFailureCode.INVALID_REQUEST`; neither result touches a file. A valid
read applies each persisted entry through `updateState`; malformed/unknown/disabled entries warn
and retain the prior state, including safe out-of-list values. `Completed(ABSENT)` returns the
validated baseline; an access/decoding `Completed(FAILED)` diagnoses and returns that baseline.
A filesystem load uses this same protocol before all option-sensitive work. The writer compares a
validated state with `catalog.defaultState()` and emits changed pack options only. Global settings
remain separate.

The global codec first applies the deterministic request-object validation and the sole
`EngineOptionData` validity invariant in §5.1. A failure occurs before I/O and returns read
`values` equal to the valid supplied baseline, or the canonical empty value when that baseline is
invalid, with `status=FAILED` and `failure=INVALID_REQUEST`; a write returns
`FAILED/INVALID_REQUEST`. Diagnostics are the immutable diagnostics emitted by that call.
For every read, `failure` is present exactly when status is `FAILED`; `ABSENT` and `APPLIED` have
no failure.

After a valid request, present bytes are decoded into logical Properties occurrences in file
order, separately from request-object validation. Known typed-valid occurrences and unknown-safe
occurrences overlay a copy of the baseline; unknown-safe entries warn, and the last valid
duplicate wins. A typed-invalid known occurrence, unsafe unknown occurrence, or malformed logical
occurrence warns and is omitted without erasing a prior value. Success returns the overlay with
`APPLIED`; valid absent input returns the baseline with `ABSENT`. Access/framing failure returns
the baseline with `FAILED` and the exact access failure or `INVALID_ENCODING`. A valid write emits
every entry, including unknown-safe entries, using §5.1's exact canonical order and escaping; it
never performs changed-only filtering. The access receipt determines `COMMITTED` versus `FAILED`
and the exact optional failure.

Encoding and safe-write mechanics remain deterministic ISO-8859-1, unsigned-UTF-8 key order, no
timestamps, same-directory exclusive temporary files, closed handles, symlink rejection, and atomic
replacement where supported. Phase 12 owns apply/discard timing; codecs never mutate a published
configuration. `ScreenModel.resolvedColumns` retains negative-input rejection and uses the expanded
screen-slot count under the exact floor rule below.
An `updateState` result is preview/persistence input, not a mutation of a published configuration.
Applying it requires a safe filesystem write or D-P3-65 Internal session acceptance followed
by a fresh atomic `load`; only its new configuration may be materialized or evaluated by Phase 4.

Every main or named `ScreenModel` stores configured columns separately from its ordered entries;
absence means two. Phase 12 expands `*` first, then passes the complete retained screen-array length
as `expandedSlotCount`. Each ordinary option, applicable `[SUBSCREEN]`, applicable `<profile>`, and
`<empty>` occupies one counted slot. Resolution is
`max(configuredColumns, ceil(expandedSlotCount / 9))`: 0–18 slots with the default resolve to 2,
19–27 to 3, and so on. An explicit positive value is therefore a floor, not an unconditional
winner. D-P3-55 records the contract check and qualified behavioral provenance for this exact rule.

### 4.4 Standard macros and OQ-7-shaped identity data

```text
MacroConfiguration
  identityPolicy              // OPTION_1, OPTION_2, or OPTION_3; OQ-7 remains open
  baseCompatibilityMacros    // ordered MacroDefinition list, OF A–G
  optionMacros                // ordered MacroDefinition list, shader sources only
  companionOptionMacros      // immutable typed source for the first two option macros
  capabilityFeatureMacros    // honest supported IRIS_FEATURE_* definitions
  engineIdentityMacros       // SCHMALOOGIUM + version
  perPackOverrides            // immutable name→MacroOverride map
  reservedContributors        // phase6.centerDepthSmoothRedirect
```

The v0.1 default is option-3-shaped while the final OQ-7 decision remains open:

- OF-era behavior: no global `IS_IRIS` and no global `IRIS_VERSION`;
- full OF A–G set, with exact ASCII-decimal `MC_VERSION=11202`; every non-`Off` request rejects an
  MC tuple other than `(1,12,2)` before pack I/O;
- honest `IRIS_FEATURE_*` macros only for capabilities actually implemented;
- `SCHMALOOGIUM` plus `SCHMALOOGIUM_VERSION` projected from the validated decimal engine version;
- a validated per-pack override capable of adding/suppressing identity or capability macros
  without changing the preprocessor; and
- a policy enum that can switch among OQ-7 options 1–3 in G8/S3.

Each macro name is a valid preprocessor identifier; each `MacroDefinition` replacement is non-null
and newline-free. The base merge order is standard A–G, option, capability-feature, then
engine-identity macros; a duplicate across those families is `INVALID_REQUEST`. Overrides apply in
unsigned-UTF-8 name order after that merge and before the reserved Phase 6 contribution.
`MacroOverride` requires a replacement for `ADD`/`FORCE` and forbids one for `SUPPRESS`. The
protected-target check enforces the override-target whitelist: override targets are exactly non-version
identity/capability names, so overrides cannot target standard A–G, `SCHMALOOGIUM_VERSION`, option
macros, the reserved contributor namespace, or §4.5's reserved intrinsic namespace (`__VERSION__`,
`__FILE__`, `__LINE__`, `GL_core_profile`, `GL_compatibility_profile`, `GL_ARB_geometry_shader4`).
"Every other valid name" in the action matrix means exactly every name admitted by that whitelist;
anything else — including every reserved intrinsic and any arbitrary non-identity/capability
name — is rejected with the same pre-I/O `INVALID_REQUEST`. For every admitted name, the complete
action matrix is:

| Action | Name absent after base merge | Name present in capability/identity family |
|---|---|---|
| `ADD` | insert the supplied replacement | reject: `INVALID_REQUEST` |
| `SUPPRESS` | documented no-op | remove the definition |
| `FORCE` | insert the supplied replacement | replace with the supplied replacement |

Validation rejects a null map first; then a null key/value; then visits remaining keys in
unsigned-UTF-8 order and checks identifier grammar, action/replacement shape, protected target
(the whitelist above, so reserved intrinsics and other non-identity/capability names fail here),
and `ADD` collision in that order. The first defect yields non-`Off`
`Failed(INVALID_REQUEST)` before pack or persistence I/O and publishes no partial macro map. These
rules close the macro component without choosing OQ-7; the policy switch remains a configuration
change for G8/S3.

The `optionMacros` family retains its exact eight-name order. Its first two positions project
`MacroConfiguration.companionOptionMacros()`: `normalMap=true` emits `MC_NORMAL_MAP 1`,
`specularMap=true` emits `MC_SPECULAR_MAP 1`; false omits the corresponding definition entirely,
never defines it as `0`. All four boolean pairs are valid, with independent component semantics.
The pair is copied by value from the required load input before any shader jcpp evaluation.
Phase 3 neither recomputes it from `EngineOptionData`/`RendererFeatureData` nor adds their former
gates. The eight global settings use §5.1's amended tri-state/zero-only validation and defaults;
`normalMapEnabled`/`specularMapEnabled` are no longer direct macro producers in Phase 3.
Phase 7 adapts Phase 13's static preliminary state before load, without requiring a configuration,
registry, declarations, atlas, or completed texture plan. The staged v0.1 caller supplies an explicit
`CompanionOptionMacros(false,false)`; active v0.5 policy is supplied before preprocessing, not
inferred from later allocation success. Post-analysis demand may optimize physical allocation only;
it cannot revise the chosen pair. The remaining six option positions use D-P3-64:
`renderResMul`, `shadowResMul`, and `handDepthMul` always emit `MC_RENDER_QUALITY`,
`MC_SHADOW_QUALITY`, and `MC_HAND_DEPTH`. Each old-light setting resolves explicit user
TRUE/FALSE first, else explicit parsed pack TRUE/FALSE, else `true` (the existing P9/P10 local
fallback). Emit `MC_OLD_HAND_LIGHT 1` / `MC_OLD_LIGHTING 1` iff its resolved value is true.
The single A–G-only properties parse precedes this projection, so no shader-preprocessing cycle
exists. Retain the resulting macro list unchanged for every same-build materialization.
`antialiasingLevel=0` omits `MC_FXAA_LEVEL`; there is no positive AA input or runtime AA feature.

A multiplier replacement is exactly `Float.toString(Float.parseFloat(input))` on the accepted
positive finite Java `float`. Under Java 25 this uses plain notation for values in
`[1.0e-3f,1.0e7f)`, otherwise scientific notation with one digit before `.`, uppercase `E`, no
positive exponent sign or exponent leading zero, and the shortest round-tripping digits; it drops
superfluous trailing fractional zeros but retains at least one digit after `.`. Thus `1`, `1.0`,
`1.`, `.1e1`, and `+01e0` all emit `1.0`, while the threshold neighbors distinguish `0.001` from
`1.0E-4` and `9999999.0` from `1.0E7`. The reserved AA value has no emitted replacement.

Missing known settings warn and use, in key order, `true`, `true`, `1.0`, `1.0`, `0.125`,
`default`, `default`, and `0`; typed-invalid persisted occurrences already warned and were omitted by
§5.1. Missing required companion data or renderer-feature data on non-`Off` is `INVALID_REQUEST`,
never implied support or an implicit false pair. All three `MacroIdentityPolicy` values retain
this family; OQ-7 changes only identity/feature payloads.
`perPackOverrides` is limited to non-version identity/capability names and rejects standard A–G,
`SCHMALOOGIUM_VERSION`, and all eight option names.
Option macros enter load-time shader analysis and every same-build shader-source materialization,
never properties or ID-map preprocessing. P3-C08 implements the required typed snapshot and
six non-companion positions (the AA position is always absent); P3-C23 enables companion positions at v0.5. Planned checks cover
typed enablement, pre-jcpp branches, same-build retention, fingerprints, defaults, numeric
serialization, policy, and override-target rejection.

`macro_optionNumericSerializationAliasesAndThresholds` compares exact UTF-8 macro lines: the five
float aliases above all yield replacement `1.0`; `0.001`, `1.0E-4`, `9999999.0`, and `1.0E7`
exercise both notation boundaries. Only exact AA spelling `0` is valid and emits no FXAA macro;
`+0001`, `0001`, `+000`, `000`, and `2147483647` are typed-invalid rather than numeric aliases.

For every non-`Off` request, the MC tuple must be exactly `(1,12,2)` and emits the byte-exact
replacement `11202`; all other tuples return `Failed(INVALID_REQUEST)` before pack I/O. This fixed
domain implements the project's MC 1.12.2 mission without inventing a general three-integer
encoding. `engineVersion` must match `0|[1-9][0-9]{0,9}`, parse in `0..2147483647`, and is itself
the exact ASCII-decimal `SCHMALOOGIUM_VERSION` replacement when that identity family is enabled.
Leading signs, leading zeroes, whitespace, separators, overflow, null, and empty values are invalid.

`MC_GL_VERSION`, `MC_GLSL_VERSION`, vendor, renderer, and extension support come exclusively from
Phase 1 `GLCapabilityProfile`; OS and MC/engine identity come from `RuntimeIdentityData`.
`MC_GL_VERSION` is base-10 `glVersionMajor * 100 + glVersionMinor * 10`; request validation requires
major `> 0`, minor in `0..9`, and a non-overflowing result. Thus `(2,1)`, `(3,2)`, and `(4,5)` emit
`210`, `320`, and `450`.

`glslVersion` must match the anchored ASCII grammar
`^([1-9][0-9]*)\.([0-9]{2})(?:[ \t]+[^ \t\r\n][^\r\n]*)?$`. The first group is the positive major and the
second is the two-digit minor; the replacement is base-10 `major * 100 + minor`, with no leading
zero. Thus `1.20`, `4.60`, and `4.60 NVIDIA 535.98` emit `120`, `460`, and `460`; leading
whitespace, a one- or three-digit minor, an extra dotted component, newline, empty decoration after
the separator, or arithmetic overflow makes the non-`Off` load `Failed(INVALID_REQUEST)` before pack
I/O. No fallback GLSL macro is guessed.

Exactly one empty-replacement OS macro is emitted:
`WINDOWS→MC_OS_WINDOWS`, `MACOS→MC_OS_MAC`, `LINUX→MC_OS_LINUX`, and
`OTHER→MC_OS_OTHER`. Vendor and renderer classifiers apply the following regex rows in order with
Java `Pattern.CASE_INSENSITIVE`; matching is against the complete non-null raw string, `^` is
literal start anchoring, and first match wins:

| Input | Ordered patterns → emitted empty-replacement macro | Fallback |
|---|---|---|
| `vendor` | `^ati.*` → `MC_GL_VENDOR_ATI`; `^intel.*` → `MC_GL_VENDOR_INTEL`; `^nvidia.*` → `MC_GL_VENDOR_NVIDIA`; `^x\.org.*` → `MC_GL_VENDOR_XORG` | `MC_GL_VENDOR_OTHER` |
| `renderer` | `^(amd|ati|radeon).*` → `MC_GL_RENDERER_RADEON`; `^gallium.*` → `MC_GL_RENDERER_GALLIUM`; `^intel.*` → `MC_GL_RENDERER_INTEL`; `^(geforce|nvidia).*` → `MC_GL_RENDERER_GEFORCE`; `^(quadro|nvs).*` → `MC_GL_RENDERER_QUADRO`; `^mesa.*` → `MC_GL_RENDERER_MESA` | `MC_GL_RENDERER_OTHER` |

Empty or unmatched non-null strings take `OTHER`; null strings make the request invalid. The
anchoring is intentional: `NVIDIA Quadro` takes the earlier GEFORCE row, while a decorated prefix
such as `Corporation NVIDIA` takes OTHER. No undocumented `MC_GL_VENDOR_AMD` is emitted.
Supported extensions are emitted on demand only: scan active source tokens for `MC_GL_*`, intersect
with `GLCapabilityProfile.extensions()`, and define the referenced supported names. This rejects
Pintonium's enumerate-everything divergence under D-P3-9 while re-deriving the driver-string
mechanism required by the governing design.
Shader-source payloads contain the named OF standard A–G family (`MC_VERSION`, `MC_GL_VERSION`,
`MC_GLSL_VERSION`, `MC_OS_*`, `MC_GL_VENDOR_*`, `MC_GL_RENDERER_*`, and on-demand
`MC_GL_<extension>` definitions), then the independently named `optionMacros`,
`capabilityFeatureMacros`, `engineIdentityMacros`, validated `perPackOverrides`, and the reserved
`phase6.centerDepthSmoothRedirect` contribution. No H family exists. `shaders.properties` and
ID-mapping files receive only the OF standard A–G family and never option macros. Each selectable
OQ-7 policy changes these named payloads only; the policy does not introduce an unlisted family.

### 4.5 Shader preprocessing with jcpp

jcpp is the selected directive/macro engine. The wrapper accepts only immutable source and an
immutable macro map, creates a new jcpp instance per call, enables comment retention, and converts
all library exceptions into source-local diagnostics.

Processing order:

1. accept §4.2's complete numeric-`#line`-attributed include expansion; no option rewrite,
   macro substitution, or conditional evaluation occurs before expansion;
2. validate that the active root `#version` is the first non-comment/non-whitespace token and that
   no conflicting active version exists;
3. reject any pack text containing the reserved marker namespace;
4. token-rewrite actual directive lines `#version` and `#extension` into invocation-scoped
   `#warning` markers (never global substring replacement);
5. establish standard, option, identity, feature, override, and Phase 6 contribution macros through
   `Preprocessor.addMacro`, not textual lines; option macros include the same-build companion pair.
   Their logical header position is after `#version`/active extensions and before the restored
   pack `#line`; the definitions are installed before jcpp evaluates any pack conditional.
6. rewrite every captured option occurrence in the expanded attributed stream, then run jcpp
   conditional evaluation;
7. collect the active version and extension markers, preserve extension source order, and rebuild:
   `#version`, active `#extension` lines, the logical macro-header boundary, then the processed
   body with a restored root `#line`;
8. classify the final active source and validate the `GeometrySourceRequest` using the exact
   algorithm below. Preserve native source; perform **no** extension removal, constant deletion,
   builtin rename, varying conversion, version/profile upgrade or generated layout insertion;
9. scan the complete final default-block declaration catalog, freeze exact text/map/language/
   geometry/diagnostics, and compute §4.10's route-distinct fingerprint before publishing
   `Available`. Failure at any step returns only `Unavailable`, never a partial successful source.

**Native-preserving source algebra (D-P3-68; binding via §5.1).** The pipeline above is used for
every VERTEX, GEOMETRY and FRAGMENT root of the selected program, with the same load's finalized
options, include snapshot, complete macro snapshot and singular Phase 6 contribution. Each root
keeps its own language and extensions; a native geometry choice does not patch its adjacent stages.
`None` is a request for ordinary processing, **not** a request to discard geometry. Classification
is over active post-jcpp tokens, not filenames alone, comments, inactive branches or original
unexpanded text:

1. Non-GEOMETRY roots publish `GeometrySourceForm.None` for request `None`. Geometry configuration
   directives there warn/ignore as engine directives, without deleting GLSL text or enabling
   a geometry stage. `PreserveNative` on any non-GEOMETRY root is unavailable.
2. On a GEOMETRY root, recognize the existing pair: one active explicit ARB geometry extension
   activation and one top-level `const int maxVerticesOut = N;` with the positive integer value
   accepted by the existing directive grammar after ordinary macro substitution. The active
   activation may use `enable`, `require` or `warn`, whose language-enable meanings come from
   GLSL §3.3; `disable` is not an activation. Effective extension state is folded in source
   order, initially disabled, including `all : disable|warn`. Require one explicit activation
   site and a final non-disabled ARB state; `all : warn` alone is not the documented pair.
   Repeated activation sites, repeated declarations, one missing member, invalid/nonpositive/
   overflowing N, or later effective disable with a declared pair is source-local unavailable.
   Retain all witnesses in diagnostics. No `Optional.empty()` may hide a malformed pair as
   ordinary source. Inactive occurrences do not count. A core shader with neither member has
   pair absence; a stray `maxVerticesOut` without activation is still unmatched, not an API default.
3. Pair spans belong to their original physical `SourceDocument`s, which may be **different
   included files** reached by this root. `SourceSpan` remains zero-based half-open UTF-16 offsets
   and one-based original line/column, bounded by that document. `site.root == config.root`;
   both span sources must be in this root's expansion. There is no same-file, source-offset-order
   or root-file-only restriction. Repeated expansion of a physical member still counts as repeated
   active occurrences and fails cardinality. These are provenance sites, no longer rewrite ranges.
4. Parse geometry interface layout declarations from final global-scope `layout(...) in;` and
   `layout(...) out;` tokens under GLSL 1.50 §4.3.8's grammar: one input primitive; output primitive,
   `max_vertices = integer-constant`, or both; separate/repeated equal declarations are allowed.
   For a macro-generated declaration that has no contiguous physical declaration span, the site
   is its outermost pack macro invocation span; final token mappings retain definition/expansion
   provenance. Never construct a span across files or across noncontiguous source pieces.
   Ordinary macro expansion precedes recognition. Normalize the specification's case-insensitive
   layout identifiers to the existing closed primitive enums, retaining original text unchanged.
   Each `GeometryLayoutDeclaration` retains the optional fields explicitly present and the
   `layout` token location; at least one field is present, input cannot coexist with output/max
   in one declaration, and every present count is positive and fits int. List order is final
   token order; equal redeclarations are retained. Unequal declarations for the same property,
   malformed layout or a geometry layout not representable by this published grammar returns
   source-local unavailable, not last-wins or an inferred layout. Other stages' layout syntax
   is not geometry configuration. No modern invocation/stream feature is granted by this parser.
5. With no pair, require request `None` and a complete source input/output/max triple for
   `CoreLayout`; missing properties make the GEOMETRY root unavailable, not `None`. Core-layout
   syntax requires effective language version at least 150 and P4's GL 3.2 gate. A native request
   when no pair exists fails. With a pair, require `PreserveNative(expected)`, structurally equal
   in **every** field to this configuration's recognized config and to final rediscovery after
   contribution. Wrong root/site/extension/count or contribution-induced disagreement is unavailable.
   `None` with a pair remains unavailable, now with a real preserving alternative.
6. With a valid pair, publish `NativeLegacy(config,effective,declarations)`. Let
   `A = (TRIANGLES, TRIANGLE_STRIP, config.maxVertices)` be the existing P4 native API triple
   and `S` the unique per-property source layout values. For each property `p`,
   **`effective[p] = S[p] if present, otherwise A[p]`**. Any source layout requires language
   version ≥150 and, at P4, GL ≥3.2 as well as the actual ARB extension. No layout means
   `effective=A`. Partial layouts are valid on the native route; complete layouts with the ARB
   pair remain native (not an excuse to skip extension gating/configuration). Source values
   differing from A are retained, never overwritten, clamped or called a pair mismatch.
   P4 passes **A**, not `effective`, to the narrow P1 operation; the linker applies source
   precedence. P4/backend must use the effective linked input for drawing. These existing enum
   domains describe source, not permission to convert terrain or draw arbitrary primitives.

The algebra deliberately removes `Translate`: no complete translator or proof token is offered.
Native preserving `Available` requires the above source contract, **not** successful GL compilation.
P4 gates `NativeLegacy` on `hasExtension("GL_ARB_geometry_shader4")`, not GL version alone,
and retains its drain/configure/drain/link/validate/whole-binding-fallback sequence. Invalid
native/core combinations, unsupported language features, incompatible adjacent stage versions or
varying interfaces and hardware limits can still fail compilation/linking. Preserve those inputs
and source-local diagnostics; never retry a rewritten core program or omit only the `.gsh`.
In particular, GLSL 1.50 §3.3 does not promise linkage with arbitrary 1.20 adjacent stages;
preservation is not an era bridge or OQ-18 resolution.

**Language and attribution invariants.** `ShaderLanguage.version` is the effective source
version, not `MC_GLSL_VERSION` (driver capability). `explicitVersion=false` means no directive,
version 110 and empty profile; otherwise retain the exact declared numeric value and optional
explicit `core|compatibility` as the enum. The emitted version/profile tokens are never changed;
absence is not filled with 150 or a compatibility qualifier. An omitted profile on version 150
retains the language's core default even in a compatibility context. The existing strict
first-significant-token rule and duplicate/conflicting-version rejection still apply.
`extensions` contains every active extension directive in order with exact case-sensitive name
(including `all`), closed behavior and original location; malformed directives fail locally.
Preserve all active extension lines and their order through the marker protocol, not only ARB.
Do not hoist a directive across ordinary GLSL tokens to make an invalid shader valid: the reviewed
GLSL/ARB shader-scope extensions must precede non-preprocessor tokens. Diagnose such misplaced
directives instead. No macro substitution occurs inside `#version` or `#extension`.
For a version-150 source on the retained compatibility baseline, the wrapper also supplies
`GL_core_profile=1` and `GL_compatibility_profile=1`, describing supported profiles rather than
rewriting the requested profile. The native grant does not add an arbitrary modern intrinsic
macro namespace. GLSL intrinsic handling is shader-source-only; the existing properties A–G,
ID-map environments, option/companion and singular contribution contracts remain unchanged.

The jcpp wrapper supplies GLSL's `__VERSION__` from the root's effective version and evaluates
`__LINE__`/`__FILE__` using the attributed logical line/numeric source-ID cursor, never jcpp's
host filename or a generated header line. It supplies `GL_ARB_geometry_shader4=1` iff the retained
profile actually advertises that extension, even when its language use is disabled; unsupported
means undefined, not `0`. This intrinsic is distinct from existing on-demand `MC_GL_*` macros.
Reserved intrinsic names cannot be overridden/undefined by pack or contribution input. These
intrinsic values enter the same load-time analysis and materialization environment **before**
conditional evaluation, including nested macro expansion; they are not an after-jcpp patch.
GLSL variables/functions such as `gl_VerticesIn`, `gl_PositionIn`, `gl_TexCoordIn`,
`gl_FrontColorIn`, `gl_ClipVertexIn`, `EmitVertex`, `EndPrimitive`, user `varying in/out` arrays,
and every adjacent-stage varying remain tokens for the native compiler, never host-evaluated,
renamed, array-resized or synthesized. `maxVerticesOut` remains in executable source because pack
code may read it. Existing option rewrites/macros may naturally substitute tokens, but the selected
geometry strategy itself changes none. This supplies native preservation without a shader AST.

`SourceMap.files` keeps stable positive canonical file numbers; zero remains the driver's original
single emitted source string, not a fabricated physical include. `mappings` is a frozen list
of non-overlapping `[startOffset,endOffset)` UTF-16 ranges in **exact transformedText**, in ascending
offset order. Each significant output token and each retained header directive has an origin
mapping; whitespace/generated `#line` scaffolding need not. Each `location` is the original
token/marker location; an expanded macro uses its invocation location and `expansionTrace`
lists original include call sites outermost-first followed by macro invocation/definition sites
outermost-first, including the source of contributed replacement as its root invocation anchor.
A missing representable origin fails catalog/geometry publication, never guesses an include.
All offsets/coordinates are range-validated; list/trace copies are immutable. Driver numeric
`#line` logs use `sourceForFileNumber`; emitted-string/header errors use final offset mappings.
Without a parseable driver coordinate, P4 retains root attribution plus sanitized log and never
claims a precise token location. The new map changes no diagnostic channel or public P1 type.
For GLSL 1.20/1.50, emitted `#line n f` selects next line `n+1`, hence desired next line L is
encoded with `n=L-1`; jcpp's own line cursor is adapted, not blindly copied. Pack-supplied
numeric `#line` changes are tracked separately from immutable physical provenance; unknown
file numbers remain unknown, never rebound to an arbitrary file. Deterministic wrapper markers/
nonces never escape source, maps, diagnostics or fingerprints.

Load-time analysis follows this same pipeline through active pair/layout discovery with
`MacroContribution.Empty`, but invokes no materializer and chooses no caller request. It publishes
the recognized pair through `ProgramRequirements.legacyGeometry`; private root-failure state
is retained for later `Unavailable`. Resource collection is still load-time, while every
successful materialization rescans its final default-block declarations after the actual
contribution. All records are non-null, structurally equal, defensively immutable snapshots;
old catalogs/materializers remain readable and deterministic after reload or lease closure.
No GL object, parser, callback, filesystem or mutable provider is retained by a source result.

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
source spans remain alongside parsed fields for diagnostics and retained declaration provenance.
Scalar/map properties follow Java-Properties last-valid-value behavior and produce a location-rich
duplicate warning; texture `.0`–`.9` discriminators avoid accidental duplicates. Custom-expression
declarations and `TexturePropertyDecl` capture are collection-valued exceptions: dispatch observes
every logical occurrence before key collapse. Expressions retain every valid declaration for
Phase 11; textures retain every decoded `texture.*` occurrence, even unresolved keys and invalid
values, under §§4.8/5.1. Only Phase 3 reduces the texture stream into executable specs afterward.

### 4.7 Directive scanning and requirement aggregation

The scanner is table-driven:

- declaration recognizer: uniform/attribute declarations and geometry extension;
- const recognizer: typed scalar/vector constants with exact identifier aliases;
- block-comment recognizer: `/* KEY:value */`;
- line-comment recognizer: `// KEY:value`;
- routing recognizer: `DRAWBUFFERS` at `v0.1`; `RENDERTARGETS` recognition and precedence at
  `post-v0.5`.

Its directive/property registry stores, in addition to the parser, field, validation rule, and test
ID, an applicability predicate over a private closed `ProgramFamily` classification:
`SHADOW`, `GBUFFERS`, `DEFERRED_PRE`, `DEFERRED`, `COMPOSITE_PRE`, `COMPOSITE`, `FINAL`, or
`UNKNOWN`. The two `*_PRE` outcomes are the exact virtual flip-control-only names; executable
deferred/composite names have their non-`PRE` outcomes. Classification never infers family from
the `.vsh`/`.fsh` source stage. The affected predicates are `colortexNClear` and
`colortexNClearColor` → `{DEFERRED, COMPOSITE}`, `colortexNMipmapEnabled` →
`{DEFERRED, COMPOSITE, FINAL}`, `scale.<prog>` → `{DEFERRED, COMPOSITE}`, and `flip.<prog>.<buf>` →
`{DEFERRED_PRE, DEFERRED, COMPOSITE_PRE, COMPOSITE}`. `UNKNOWN` is never eligible.

One case-sensitive `normalizeColorBufferName(String)` owns every pack-facing color-buffer spelling:
`colortex0`/`gcolor` → 0, `colortex1`/`gdepth` → 1, `colortex2`/`gnormal` → 2,
`colortex3`/`composite` → 3, `colortex4`/`gaux1` → 4, `colortex5`/`gaux2` → 5,
`colortex6`/`gaux3` → 6, and `colortex7`/`gaux4` → 7. Every other spelling is rejected; at a
buffer-bearing grammar site it produces that site's existing source-attributed warn/ignore outcome
without mutation. Uniform sizing and `gdepth` upgrade, format (including fixed `GAUX4FORMAT` →
index 7), clear, clear-color, mipmap, and `flip` parsing all call this normalizer before constructing
a `ColorAttachmentKey`; no parser keeps a private alias map.

Each recognized key maps to one typed parser, one target field, one validation rule, and one test
ID from §3.3. There are no hand-written setter chains where adjacent half-life fields can be
cross-wired. Duplicate scalar directives use last active source occurrence and diagnose the prior
location. Resource minima aggregate monotonically across active sources; explicit per-program
state remains per program. Ordinary format requests retain diagnostics plus deterministic
last-active occurrence and produce `ColorAttachmentFormat.Explicit(token)`. An active `gdepth`
declaration is the contract exception: it replaces either format variant with
`Explicit(RGBA32F)` regardless of source order; any explicit format other than `RGBA32F` is retained
as a conflict diagnostic and cannot override it.
Routing starts as `DrawRouting.AllUsed` when no active valid routing directive exists. A valid
`DRAWBUFFERS` value becomes `DrawRouting.Explicit` with one source-ordered slot per character:
`0`–`7` becomes `DrawSlot.Attachment(ColorAttachmentKey)` and `N` becomes `DrawSlot.None`.
Standalone, leading, middle, trailing, repeated, and all-`N` forms retain every position; an empty
explicit slot list is not produced by `DRAWBUFFERS`. Later active routing occurrences replace the
earlier route. Malformed routing retains the prior route or the `AllUsed` baseline.

Before applying a clear, clear-color, per-pass-mipmap, or viewport-scale occurrence, its dispatcher
evaluates the registry predicate against the exact program family. A syntactically recognized
occurrence outside that set emits one source-attributed wrong-family diagnostic, is not an active
value, and cannot create or mutate state. This remains true before or after a valid occurrence;
valid source order is considered only among eligible occurrences.

Stage is part of the table key: extended attributes and `countInstances` populate requirements only
from `.vsh`, and the legacy geometry pair only from `.gsh`. A recognized directive in any other
stage is ignored and emits one source-attributed warning; it cannot alter a previously valid value.
For a `.gsh` root, exactly one recognized extension and one matching `maxVerticesOut` declaration
form the pair; zero is absence, while an unmatched or repeated member is a structural source
failure. The scanner retains `LegacyGeometrySite` provenance under §4.5, including members
from included files. P4 selects `PreserveNative(expected)` for the pair; P3 validates it and
publishes native source with source-layout precedence. P3 never chooses a different API
topology or translates the source to core form.


`ResourceRequirements` contains:

- per-colortex format, clear, clear-color, and per-pass mipmap requirements, with family-filtered
  publication for the three scoped directives;
- shadow projection/filter/mipmap configuration;
- center-depth readback requirement;
- per-program draw routing, attributes, instance count, and legacy geometry config;
- half-lives in ticks and world constants; and
- noise requirement.

It never allocates GL resources. Phase 5 consumes the sizing/format/clear/pass-mipmap subset,
Phase 6 the center-depth/half-life declarations, Phase 8 the shadow subset, Phase 13 the noise
enablement and resolution, and Phase 4/7/10 per-program execution data. Phase 4 directly reads
`mipmappedAfterPass()` and `vertices()` from the same frozen `ProgramRequirements` entries used
by Phase 5's pass-mipmap and Phase 10's vertex-layout consumers. Section 5.1 binds the exact
acquisition, key, absence, and consumer rules; no second accumulator or downstream callback is
introduced.

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
public sealed interface ColorAttachmentFormat
    permits ColorAttachmentFormat.DefaultRgba, ColorAttachmentFormat.Explicit {
    record DefaultRgba() implements ColorAttachmentFormat {}
    record Explicit(ColorInternalFormat format) implements ColorAttachmentFormat {}
}
public record ColorAttachmentRequirement(ColorAttachmentFormat format, boolean clear,
    Optional<Vec4f> clearColorOverride) {}
public record ProgramRequirementKey(DimensionKey dimension, String programName) {}
public record ProgramRequirements(DrawRouting routing,
    Set<ColorAttachmentKey> mipmappedAfterPass, VertexRequirements vertices,
    int instanceCount, Optional<LegacyGeometryConfig> legacyGeometry) {}
public sealed interface DrawRouting permits DrawRouting.AllUsed, DrawRouting.Explicit {
    record AllUsed() implements DrawRouting {}
    record Explicit(List<DrawSlot> slots) implements DrawRouting {}
}
public sealed interface DrawSlot permits DrawSlot.Attachment, DrawSlot.None {
    record Attachment(ColorAttachmentKey target) implements DrawSlot {}
    record None() implements DrawSlot {}
}
public record ShadowRequirements(int resolution, Optional<Float> fov, float distance,
    float distanceRenderMultiplier, float intervalSize,
    Set<ShadowTextureKey> mipmapped, Set<ShadowTextureKey> nearest,
    Set<ShadowDepthKey> hardwarePcf) {}
public record CenterDepthRequirements(boolean required) {}
public record SmoothingConstants(float wetnessHalfLifeTicks, float drynessHalfLifeTicks,
    float eyeBrightnessHalfLifeTicks, float centerDepthHalfLifeTicks) {}
public record WorldRenderConstants(float sunPathRotation, float ambientOcclusionLevel) {}
public record NoiseRequirement(boolean enabled, int resolution) {}
public enum VertexAttribute { MC_ENTITY, MC_MID_TEX_COORD, AT_TANGENT }
public record VertexRequirements(Set<VertexAttribute> attributes) {}
public enum ShadowTextureKey { DEPTH_0, DEPTH_1, COLOR_0, COLOR_1 }
public enum ShadowDepthKey { DEPTH_0, DEPTH_1 }
public record Vec4f(float red, float green, float blue, float alpha) {}
```

`VertexRequirements.attributes` is a non-null immutable enum-ordered set; `VertexAttribute` has
exactly `MC_ENTITY`, `MC_MID_TEX_COORD`, and `AT_TANGENT`. `ShadowTextureKey` has exactly
`DEPTH_0`, `DEPTH_1`, `COLOR_0`, `COLOR_1`, and `ShadowDepthKey` has exactly `DEPTH_0`, `DEPTH_1`,
in declaration order. Every resource float rejects a non-finite component and canonicalizes either
signed zero to `+0.0f`.
`ColorAttachmentFormat` is exactly `DefaultRgba` or `Explicit(ColorInternalFormat format)`;
`Explicit.format` is non-null, and `RGBA8` remains an explicit sized format distinct from
`DefaultRgba`. `ColorInternalFormat` has exactly the 37 Appendix-B.4 constants in declaration order
above. Records use structural equality/hash; the enums' declaration order is their deterministic order.
`DrawRouting.AllUsed` denotes no active valid routing directive and writes all color buffers used
by the program; `DrawRouting.Explicit` preserves a non-empty ordered slot list. Each
`DrawSlot.Attachment` names one target and each `DrawSlot.None` preserves one no-output position.

`ColorAttachmentKey` accepts indices 0…15; `ProgramRequirementKey.programName` is the exact,
non-empty pack-facing program name and the dimension disambiguates overrides. Both maps iterate in
ascending dimension ID, then program name, or ascending attachment index; sets iterate in enum or
attachment-index order. All maps, sets, and routing lists are immutable; maps reject duplicate keys.
Every aggregate/default-valued top-level record is present. Absence is represented only by an
empty collection, `Optional.empty()` for orthographic shadow FOV or no legacy geometry pair, or an
explicit typed baseline value; no `null` or sentinel key/value is public. The complete baselines
are fixed in §5; this construction applies them before scanning directives. A shadow-FOV producer
accepts only finite degree values strictly between 0 and 180; all other values are malformed and
retain the prior/baseline value. Other directive bounds, units, aliases, and winning-occurrence
rules are normative at §3.3 and §4.7. Builders and mutable accumulators remain private.

The load-time declaration recognizer above answers only resource-requirement questions. Every
successful `materialize` also runs a distinct token-grammar declaration pass over the final
post-jcpp, post-contribution, geometry-classified stream, under its effective version/extensions.
It collects every active default-block `uniform` declarator, including comma-separated names, arrays, and named/inline
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
The structural geometry failures in §4.5 are the explicit source-local exception: they make the
affected root unavailable and feed P4 rung 3, never abort the pack or silently remove `.gsh`.
No malformed directive aborts a pack.

### 4.8 `ShaderPropertiesModel`

The parser dispatches by exact key/prefix to immutable builders. `ShaderPropertiesModel` is the
public record `(EngineFlags engineFlags, List<MinimumEditionRule> minimumEditionRules,
List<CustomTextureSpec> textures, List<TexturePropertyDecl> textureDeclarations,
NoiseTextureSpec noise, List<CustomExpressionDecl> customExpressions,
ProgramStateModel programStates, List<UnknownProperty> unknownProperties)`.
Every component and nested collection is non-null and immutable; the declaration accessors return
their frozen source-ordered lists. Decoded `texture.*` occurrences belong only to
`textureDeclarations`, not also `unknownProperties`. Other unknown keys remain preserved in
`unknownProperties` and debug-logged, enabling contract growth without data loss.
`EngineFlags` remains the closed record of one
`CloudMode` and sixteen `TriState` fields in §2.2; every absent flag is `DEFAULT`, and no consumer
infers a different raw request.

A minimum-edition key is valid only when its suffix matches
`([1-9][0-9]*)\.(0|[1-9][0-9]*)(?:\.(0|[1-9][0-9]*))?`, each component parses in
`0..2147483647`, and the complete key is exactly `version.<suffix>`. A two-component suffix has
patch zero; otherwise the three parsed components are exact. No leading zero, sign, whitespace,
case fold, or extra component is accepted. A rule matches this phase's runtime only at tuple
`(1,12,2)`, hence only canonical `version.1.12.2` matches.
`MinimumEditionRule.minecraftVersion()` is the exact decoded suffix after `version.`, not the
complete key or a normalized tuple spelling; thus valid `1.12` and `1.12.0` remain distinct accessor
values even though both parse with patch zero. `minimumEdition()` is the exact Java-Properties-
decoded value, so comparison-equal `g006_pre01` and `G6_PRE1` also remain distinct accessors.
Canonical tuples/tokens are temporary comparison values only, and valid rules retain source order.

Edition input is 1–64 ASCII characters matching
`[A-Za-z0-9]+(?:[._-][A-Za-z0-9]+)*`. Canonicalization uppercases ASCII letters and strips leading
zeroes from each maximal digit run, retaining one zero; separators remain. Comparison tokenizes the
canonical form as `[A-Z]+|[0-9]+|[._-]`. Equal-kind tokens compare numeric magnitude, ASCII letters,
or separator code point; unlike kinds rank separator before digit before letter; if all shared
tokens compare equal, the shorter token sequence sorts first. This is a locale-independent total
order: `g006_pre01` equals `G6_PRE1`, `G6_PRE2 < G6_PRE10`, and `G6 < G6-PRE1`.

A matching rule is satisfied when canonical runtime `engineEdition` is at least its canonical
minimum. No unmet matching rule means `COMPATIBLE`; any unmet matching rule means
`REQUIRES_NEWER_EDITION`. A malformed key or edition warns and ignores only that rule. This
grammar, comparator, aggregation, and malformed-rule posture are provisional local interoperability
policy under D-P3-52; Appendix F.2 establishes only the minimum-edition meaning.

Custom texture specs preserve stage, the exact App F.5 sampler-name segment, the optional numeric
duplicate discriminator, source kind, typed raw format values, and sidecar location. Before any
grammar validation, source parsing, normalization, or duplicate reduction, the same dispatcher
captures every active decoded `texture.*` occurrence as `TexturePropertyDecl`. The binding §5.1
defines its exact fields, ordering, closed dispositions, reducer, and non-execution boundary.
The exact case-sensitive raw source form is
`<path> <target> <internalFormat> <dimensions> <pixelFormat> <pixelType>`.
External targets map only as follows: `TEXTURE_1D` → `TextureTarget.TEXTURE_1D` (one dimension),
`TEXTURE_2D` → `TextureTarget.TEXTURE_2D` (two), `TEXTURE_3D` → `TextureTarget.TEXTURE_3D`
(three), and `TEXTURE_RECTANGLE` → `TextureTarget.RECTANGLE` (two). Thus total token counts
are respectively 6, 7, 8 and 7; each dimension is a positive integer. Bare `RECTANGLE`,
case variants, unknown targets, missing/extra dimensions or trailing operands are invalid.
These failures use `INVALID_VALUE` and do not erase an earlier valid complete-key spec.
A surviving `Raw` contributes its normalized bytes path and associated sidecar to D-P3-69's
existing declared asset domain; P13 acquires those same-load bytes and consumes the typed
target, never reparsing external tokens. This is not an enum, suffix or upload-algebra redesign.
This includes unresolved key extensions with their original decoded value and attribution; it
does not invent a suffix grammar. Unknown keys produce a warning and no executable spec, but
their declaration is never omitted or rewritten into a known key. An unresolved property cannot
erase or modify an independently valid base binding or acquire a guessed numeric discriminator.
The ordinary `PackPath` form is accepted only when the already-normalized
path's final segment has at least one code point before a literal, case-sensitive lowercase `.png`
suffix. `.PNG`, `.Png`, a bare `.png`, a trailing suffix, and a non-PNG path warn and omit only
that occurrence's executable effect; the last valid complete-key value is not erased.
This is lexical validation only: Phase 3 snapshots image bytes but does not decode the image. An invalid source
value remains an `INVALID_VALUE` declaration while leaving the prior valid executable spec intact.
The immutable list is semantic losslessness after the existing preprocessing/Properties decoding,
not retention of inactive branches, comments, physical escape spellings, or undecodable lines.
Input-size bounds and safe-path validation still apply; retained raw values grant no file access.
Under D-P3-67, the required sampling mechanism is Phase-13-owned `.mcmeta` blur/clamp, not
property-key suffix parsing. No new typed sampling publication or future U1 grant is required.
`TextureSidecarRef(path)` rejects null and exposes that exact normalized path through `path()`.
For `PackPath`, `Raw`, or noise `Override`, its optional is present iff the indexed pack contains
the regular file at the associated image/bytes canonical string plus literal `.mcmeta`; the record
holds exactly that adjacent path. Phase 3 snapshots but never interprets it. `Raw.internalFormat` uses
the same `ColorInternalFormat` enum as colortex directives: exactly the 37 names in RESEARCH Appendix
B.4. `PixelFormat` is exactly `RED`, `RG`, `RGB`, `BGR`, `RGBA`, `BGRA` and their `_INTEGER`
forms. `PixelType` is exactly `BYTE`, `SHORT`, `INT`, `HALF_FLOAT`, `FLOAT`, `UNSIGNED_BYTE`,
`UNSIGNED_BYTE_3_3_2`, `UNSIGNED_BYTE_2_3_3_REV`, `UNSIGNED_SHORT`,
`UNSIGNED_SHORT_5_6_5`, `UNSIGNED_SHORT_5_6_5_REV`, `UNSIGNED_SHORT_4_4_4_4`,
`UNSIGNED_SHORT_4_4_4_4_REV`, `UNSIGNED_SHORT_5_5_5_1`,
`UNSIGNED_SHORT_1_5_5_5_REV`, `UNSIGNED_INT`, `UNSIGNED_INT_8_8_8_8`,
`UNSIGNED_INT_8_8_8_8_REV`, `UNSIGNED_INT_10_10_10_2`, or
`UNSIGNED_INT_2_10_10_10_REV`. An integer internal format requires the corresponding
`*_INTEGER` pixel format; either mismatch is diagnosed and that occurrence is ignored. Unknown
format/type tokens receive the same disposition. Raw textures and colortex directives therefore
share one nominal internal-format domain; neither realizes or loads a texture here.

Custom uniform/variable declaration dispatch is collection-valued. The decoded key must have
exactly `uniform|variable`, one of `float|int|bool|vec2|vec3|vec4`, and one valid non-empty name
segment; those tokens map only to the closed `CustomExpressionKind` and `CustomExpressionType`
variants in §2.2. `name` is the exact case-sensitive decoded name segment, with no case folding,
aliasing, or normalization. `rawExpression` is exactly the Java-Properties-unescaped value supplied
by the lossless adapter: Phase 3 performs no trim, re-escaping, token normalization, grammar check,
or evaluation.

For every valid occurrence, `sourceOrdinal` is its zero-based index in the published valid-
declaration sequence, so it is contiguous, unique within one configuration, and equal to list
position. `SourceAttribution` contains the declaration file's validated `NormalizedPackPath` and
the one-based physical line/column of the property key's first code point. All components are
non-null; ordinals are non-negative and attribution coordinates are positive. An invalid key,
type, or name warns and omits only that occurrence. An empty decoded value remains a non-null empty
`rawExpression` so Phase 11 can issue the expression diagnostic. Duplicate exact keys, names, kinds,
or types are not invalid here and are never collapsed or reordered; Phase 11 owns duplicate
diagnostics and winner/disable policy. `ShaderPropertiesModel` freezes the resulting list, and
`PackConfiguration.customExpressions()` returns that same immutable source-ordered projection.

Profile includes use a recursion stack and return a partial valid profile with the cyclic edge
ignored and diagnosed. Screen models preserve entry order and validate references without
constructing widgets. `*` expansion is intentionally deferred to Phase 12 because it depends on
placement across screens.
The literal `<profile>` produces `ScreenProfileEntry()` with no operand, profile identity or
selected-state payload. It is retained even with no profile definitions and regardless of
whether current option values infer a named profile or `Custom`; reference validation applies
to actual named references, not this selector. Definitions remain in `profiles()` and current
or preview selection comes only from the existing `inferProfile(candidate)` / `ProfileInference`
contract. P12 owns widget applicability and cycling, not producer invention of a profile.

`OptionConfiguration.inferProfile(OptionState)` returns `InvalidState(NULL_INPUT)` for null and
otherwise validates against its catalog, returning `InvalidState(failure)` without inference on
mismatch. `Inferred` contains the first exact match in descending expanded-constraint count then
source order, or the pack-facing `Custom` outcome. One `ProfileModel` has no list-wide inference
operation; disabled-program tokens do not relax option matching.

`program.*.enabled` is not the full custom-expression language. Phase 3 parses the small Boolean
switch grammar required before Phase 11 exists. Unknown switch names make the condition false with
a warning, so Phase 4 receives a deterministic enabled/disabled value and can apply the backup
chain.

`ProgramStateModel` is the immutable aggregate `Map<ProgramKey, ProgramState> programs`.
`ProgramKey(DimensionKey dimension, String programName)` uses the same dimension/name validity and ascending iteration
order as `ProgramRequirementKey`; exact virtual names `deferred_pre` and `composite_pre` are valid
outer keys only for flip state. Non-flip properties on either virtual key warn and are ignored.
`ProgramState` is `(Optional<AlphaTestSpec> alphaTest, Optional<BlendSpec> blend,
Optional<ViewportScale> scale, Map<FlipBufferKey, FlipOverride> flips,
Optional<ProgramEnabledExpression> enabledExpression)`. `FlipBufferKey` is exactly
`(ColorAttachmentKey attachment)` and has no program-name variant; a virtual `*_pre` token in
`<buf>` is diagnosed and ignored. `FlipOverride` is the closed `TRUE`/`FALSE` enum.
`AlphaTestSpec` is `Off` or `Enabled(AlphaFunction function,float reference)`, where the reference
is finite and `AlphaFunction` is exactly `NEVER`, `LESS`, `EQUAL`, `LEQUAL`, `GREATER`, `NOTEQUAL`,
`GEQUAL`, or `ALWAYS`.
`BlendSpec` is `Off` or `Enabled(BlendFactor sourceColor,BlendFactor destinationColor,
Optional<BlendAlphaFactors> alpha)`; `BlendAlphaFactors` is exactly
`(BlendFactor source,BlendFactor destination)`, and the factor enum is exactly `ZERO`, `ONE`,
`SRC_COLOR`, `ONE_MINUS_SRC_COLOR`, `DST_COLOR`, `ONE_MINUS_DST_COLOR`, `SRC_ALPHA`,
`ONE_MINUS_SRC_ALPHA`, `DST_ALPHA`, `ONE_MINUS_DST_ALPHA`, `CONSTANT_COLOR`,
`ONE_MINUS_CONSTANT_COLOR`, `CONSTANT_ALPHA`, `ONE_MINUS_CONSTANT_ALPHA`, or
`SRC_ALPHA_SATURATE`. Missing alpha factors reuse the color pair. The `Off` variants are distinct
from absence. All final collections are immutable with unique keys.

Properties are processed in file order; for the same exact eligible key, the last valid occurrence
wins, while malformed or ineligible input warns and leaves prior state intact. `scale` accepts
exactly one or three whitespace-separated numeric tokens. One token `s` publishes
`ViewportScale(s,0.0f,0.0f)`; three publish `(s,offsetX,offsetY)`. Two, four-or-more, nonnumeric,
nonfinite, or out-of-range tokens warn and cannot create or replace scale. Scale is eligible only
for executable exact `DEFERRED`/`COMPOSITE`, never either virtual pre-slot.

Flip is eligible only for exact executable or virtual-pre deferred/composite keys. `SHADOW`,
`GBUFFERS`, `FINAL`, and `UNKNOWN` occurrences warn and cannot create or mutate state; malformed or
ineligible duplicates cannot erase a prior valid override. No matching property means
`Optional.empty()`, an empty flip map, and property enablement `true`. A created program entry
retains only explicit eligible property data and iteration is by `ProgramKey`.
`PackConfiguration.evaluateProgramStates(Optional<ProfileName>,DiagnosticReporter)` evaluates only
the receiver's finalized `options().state()`. A null profile wrapper or reporter returns
`InvalidState(NULL_INPUT)` defensively; no caller-selectable state can produce `FOREIGN_CATALOG`.
Otherwise it returns `Evaluated`, using exactly `sources.executablePrograms()` for profile-disable
matching, unknown-program diagnostics, and every `EvaluatedProgramState` member in ascending order.

An absent profile means no disables; an unknown selected profile warns and applies none. A
qualified disable matches its exact projected key, while an unqualified disable matches every
projected key with that name; no match warns. Every projected key is evaluated once, using absent
raw-state baselines where no property exists. A source-absent explicit property key warns and
cannot enter the evaluated list. Its flips are likewise omitted unless the key is one of the two
virtual-pre flip-only slots; `explicitFlips` consists only of eligible projected executable keys
and explicitly present virtual-pre keys, all ascending. Dimension-qualified tokens never cross
dimensions. `finalEnabled` is `propertyEnabled && !profileDisabled`; unknown switches and
expression failures warn, evaluate false, and do not throw.

Phase 4 therefore receives exactly the projected source-backed program universe, independent of
stage multiplicity and raw property presence. A partial stage set remains present here; Phase 4
alone determines compile/link success. Final false means absent to its backup chain. Phase 5
receives only the eligible projected/virtual-pre flip map.

### 4.9 Schema-versioned ID-mapping input

`PackConfiguration.idMappings()` is the current-schema `IdMappingInput` in §2.2, not a flattened list.
Its four `IdMappingFileInput`s have exactly matching `MappingKind`s and immutable source order.
For pack input, paths are exactly `shaders/block.properties`, `shaders/item.properties`,
`shaders/entity.properties`, and `shaders/block.properties`'s `layer.*` family: block rules and
layer rules therefore share bytes/origin but retain independent kind/state/result fingerprints.
The published `parserEnvironment` is the immutable, loader-neutral Standard-Macro A–G environment
used for those parses, including the ordinary integer `MC_VERSION`; it contains no option macro,
pack handle, parser object, Minecraft type, or mutable map.
The short/namespaced/property/legacy entry grammar and A–G preprocessing above are the classic
RESEARCH §3.7 contract. RESEARCH §3.6.8 supplies only the modern tag risk, required 1.12 shim, and
entries-before-tags priority. D-P3-47 is Phase 3's explicit local interpretation for the concrete
`%namespace:path` spelling; Phase 3 preserves it unresolved and Phase 9 supplies the shim.

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
line is invalid are all present-empty. State never depends on the forced parse, so a present BLOCK
or ENTITY input with no ordinary rule and one forced rule remains `PRESENT_EMPTY`. ITEM and LAYER
inputs always have empty `forced11300Rules`, including LAYER parsed from the same block-file bytes.

For every present `BLOCK` or `ENTITY` request the parser runs twice from one owned defensive byte
copy. The ordinary run uses `parserEnvironment` unchanged. The alternate run uses a private
environment copy replacing exactly the `MC_VERSION` macro value with integer `11300`; every other
macro, parser option, source byte, origin and bound is identical. Fresh isolated preprocessor state
for each run prevents source `#define`/`#undef` or parse failure from leaking into the other.
No file/provider is reopened or called for the second run. The results are never merged or
selected in Phase 3, even when ordinary rules are nonempty or byte-identical to the alternate.
Ordinary rules carry `MappingEra.CLASSIC`; alternate rules carry `MappingEra.MODERN`. MODERN
means membership in this explicit dual-era alternate, not proof about a particular conditional,
name, shader ID, filename, pack identity or adjacent token. Ordinary rules never become MODERN.

Both lists retain the actual original `MappingOrigin`, original logical `sourceLine` and
left-to-right `selectorOrdinal` under the existing properties attribution mapping, not positions
in injected macro text or the shortened preprocessed output. Diagnostics use that same source
attribution; a failure without a source line uses the existing absent-location representation,
never a fabricated coordinate. Ordinary diagnostics precede alternate diagnostics in deterministic
run order. Parser state and accepted rule storage are separate; each run has the same finite
source/line/token/rule/diagnostic limits, so the operation has at most two bounded runs and two
bounded result lists, with no retry or unbounded diagnostic multiplication. Invalid selectors
still warn and omit only themselves; a run-level decode/preprocess/parse/bounds failure yields
that run's valid empty list and attributed diagnostic. Alternate failure cannot erase ordinary
rules or change ordinary state. Ordinary failure yields `PRESENT_EMPTY` and does not suppress
the independently bounded alternate. Pre-copy invalid requests yield the existing empty
diagnosed result without either run; existing pack acquisition safety/bounds failures remain
fatal under §5.1, not reclassified as recoverable parser failures. Absent inputs run neither pass.

`IdMappingFileFingerprint` hashes current schema version, kind, ordinary-derived state, origin
identity, exact source bytes/presence, canonical ordinary parser environment, both ordered rule
lists (including era and attribution), and diagnostics that affect rule acceptance, using §4.10's
canonical codec. The alternate environment is determined solely by D-P3-72's fixed replacement,
not a second caller input; schema binds that algorithm. An alternate-only accepted-rule change
therefore changes the file and containing configuration fingerprints even if ordinary state/rules
stay equal. Equal copied bytes/origin/environment/results have equal fingerprints across callers
and service instances. No mutable parser identity, branch-selection history or later Phase 9
selection enters this fingerprint; BLOCK and LAYER remain distinct by kind and payload.

D-P3-72 extends the existing ENTITY bridge to BLOCK without changing classic RESEARCH §3.7.
Phase 9 selects a BLOCK alternate only when ordinary state is `PRESENT_EMPTY` and the alternate
is nonempty; otherwise it uses ordinary rules. It never merges alternates into or overrides
nonempty ordinary rules. The same rule applies independently to each bounded mod contribution;
pack/mod precedence, registry resolution and the later derived selection fingerprint remain P9.

One property line may contain multiple selector tokens. The parser emits one `IdRule` or
`LayerRule` per occurrence, with `selectorOrdinal` preserving left-to-right order after
`sourceLine`; entries and tags never share one opaque collection. Ordinary short, namespaced,
property-bearing, and legacy numeric selectors are `ENTRY`.

A tag token is exactly `"%" tagId`. Provisional `tagId` is either `path` or `namespace ":" path`;
there is at most one colon, and neither side may be empty. `namespace` is lower-case ASCII
`[a-z0-9][a-z0-9._-]*`. `path` is one or more slash-separated segments; each segment matches
`[a-z0-9._-]+` and is neither `.` nor `..`, so leading, trailing, or repeated `/` is invalid.
Uppercase, non-ASCII, whitespace, backslash, a second `%`, properties, and metadata are rejected
rather than folded. The complete numeric exclusion is only an unnamespaced, unslashed `tagId`
matching `[0-9]+`: `%123` is rejected, while `%minecraft:123`, `%123:foo`, and `%123/foo` are
accepted and stored respectively as `minecraft:123`, `123:foo`, and `minecraft:123/foo`.
For every other accepted token the parser removes `%`, expands a short path to `minecraft:<path>`,
preserves an explicit namespace, and stores that canonical string as `SelectorKind.TAG` without
membership resolution. Every rejected token warns and contributes no rule. Phase 9 alone expands
valid tags through its 1.12 shim.

`IdRule` retains the substitute full signed integer shader ID, canonical unresolved selector,
optional `MetadataConstraint`, ordered typed property predicates, era, origin, line, and selector ordinal.
`LayerRule` carries the same selector/provenance fields and exactly one of `SOLID`, `CUTOUT`,
`CUTOUT_MIPPED`, or `TRANSLUCENT`. Unknown namespaces/names are not errors here. Invalid numeric
keys, malformed/overflowing predicates, or invalid layer names warn and omit only that selector;
the parser never drops a bad predicate and broadens the match. Solid-opaque-cube exclusion remains
a Phase 9 resolution constraint because Phase 3 has no registry snapshot. Phase 9 owns ordinary/
alternate selection, pack/mod/entry/tag precedence, registry lookup, unknown-name diagnostics, and
aliases; Phase 7 owns render-layer dispatch.

**D-P3-73 — exact entry grammar and typed constraint semantics.** After the existing
Properties unescape/preprocessing, ASCII whitespace separates selector occurrences; it is never
removed from inside a selector to repair a malformed token. An ENTRY is
`identity [":" metadata] (":" property "=" alternatives)*`. Identity is a short `path`,
`namespace ":" path`, or a legacy unsigned decimal numeric ID. Namespace/path obey the exact
lower-case ASCII namespace and slash-segment grammar above; short paths gain `minecraft:`.
An unnamespaced all-digit identity is legacy numeric, canonicalized to unsigned decimal in
`selectorToken`, not `minecraft:<digits>`.
Legacy numeric IDs fit `0..2147483647`; shader-ID keys retain the existing full signed-int domain.
To disambiguate the optional namespace, take the first two colon components as namespaced identity
only if the second is neither a property component (contains `=`) nor a numeric-list candidate
(begins with a digit, `+`, `-`, or comma). Otherwise take the first as short/numeric identity and
validate every remaining component as a constraint. Thus `leaves:3,7` and `leaves:3` are short
metadata, `minecraft:leaves:3,7` is namespaced metadata, `123:foo` is explicitly namespaced,
and `38:6` / `38:0-3:age=0-3` are legacy numeric forms. Numeric-list candidates never become
named paths after failed validation; an ambiguous all-digit second component is metadata,
not a namespaced numeric path. This ENTRY disambiguation does not alter TAG's separately
specified acceptance of `%minecraft:123`. No namespace fallback, case folding, empty component,
stray colon, second metadata component,
metadata after a property, or trailing operand is accepted. TAG grammar remains exactly D-P3-47/
D-P3-49 and permits neither metadata nor properties.

Metadata is a nonempty comma list of unsigned decimal `n` or `lo-hi`; every endpoint is in
`0..15`. Each atom becomes one `IntegerRange`: `n` becomes `(n,n)`, and `lo-hi` keeps both
inclusive endpoints with `lo <= hi`. Leading zeroes normalize through checked integer parsing;
signs, overflow, descending/open/multi-hyphen ranges and empty alternatives invalidate the
whole selector. `legacyMetadata=Optional.empty()` means unrestricted metadata; a present
`MetadataConstraint` has a nonempty immutable `alternatives` list, never an empty-as-wildcard set.
Its set is the union of those intervals, tested directly against a captured state's metadata.

Each `PropertyPredicate` is one source-ordered `property=alternatives` constraint.
`propertyName` matches `[a-z0-9_]+`; literal alternatives match `[a-z0-9_]+`, without whitespace,
case folding or escape syntax beyond the preceding Properties decoding. A digits-only literal
is checked in `0..2147483647` and canonicalized to decimal in `Literal.value`; other literals
retain exact tokens. A numeric `lo-hi` instead becomes
`PropertyValueConstraint.IntegerInterval(new IntegerRange(lo,hi))`, with unsigned decimal
endpoints in `0..2147483647` and `lo <= hi`. No signed/open/descending/overflowing range or
hyphenated fallback literal is accepted. These lexical closures are D-P3-73 local policy.
`acceptedValues` is nonempty, immutable and in comma order. Preserve interval boundaries and
author alternative order: do not sort, coalesce, enumerate range members or expand one selector
into rules. Reject duplicate normalized alternatives (including metadata `3` versus `3-3`)
and duplicate property names; distinct overlapping intervals remain valid union alternatives.
A property literal and a singleton integer interval remain different typed alternatives.

P9 matches a literal by exact equality to the resolved state's canonical value; an integer
interval matches only a canonical nonnegative decimal state value (`0|[1-9][0-9]*`, checked int)
between its endpoints. This numeric state conversion is registry-value interpretation, not
selector reparsing. Alternatives are ORed; metadata and all property predicates are ANDed.
Missing properties, unknown literal values, an interval with no value in that property's finite
live value domain, or no matching state warn and match nothing for the entire selector. P9 checks
only actual finite registry values; a range may include integers absent from that domain without
being invalid when at least one domain value lies within it. Invalid constraints are never dropped
to broaden a rule. P3 rejects malformed syntax before publication; P9 alone performs the live
domain checks. ITEM/ENTITY constraints use this same typed publication but remain unsupported
at P9: warn and match nothing, never strip them.

All new records reject null/invalid components, use structural equality/hash and defensive list
copies. `IntegerRange` always has `0 <= lowerInclusive <= upperInclusive <= 2147483647`;
`MetadataConstraint` additionally enforces endpoints <=15. Existing finite per-run byte/line/
token/rule limits bound the number of alternatives and predicates before allocation, with one
stored interval per authored range and no endpoint-width-dependent work or allocation. Existing
run-level bound failure remains an attributed empty result, never truncation or partial widening.
This identical grammar, normalization, bounds and matching algebra applies to pack and mod
requests, ordinary and forced BLOCK parses and ordinary LAYER rules; origin, sourceLine,
selectorOrdinal, mapping era, file-state derivation and ITEM/LAYER empty alternates are unchanged.

Exact selector payload examples (each occurrence yields one rule, retaining its original
shader ID/layer, origin, line and ordinal):

| Input selector | `selectorToken` | `legacyMetadata` | `propertyPredicates` |
|---|---|---|---|
| `minecraft:leaves:3,7,11,15` | `minecraft:leaves` | Present `MetadataConstraint([(3,3),(7,7),(11,11),(15,15)])` | `[]` |
| `minecraft:reeds:age=0-3` | `minecraft:reeds` | Absent | `[PropertyPredicate("age",[IntegerInterval(IntegerRange(0,3))])]` |
| `minecraft:reeds:0-3:age=0-3` | `minecraft:reeds` | Present `MetadataConstraint([IntegerRange(0,3)])` | `[PropertyPredicate("age",[IntegerInterval(IntegerRange(0,3))])]` |

The first row's pair notation abbreviates `IntegerRange` records, not expanded metadata rules.
Short `leaves`/`reeds` forms produce the same identity/constraints; original bytes still enter
file identity. Leaves matches metadata 3/7/11/15 only; reeds matches age 0/1/2/3, not literal
text `0-3`; the combined selector requires both filters on the same state.

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
3. structural validation (safe root, readable source index, at least one source configuration
   surviving mandatory decode/include validation in the base **or an explicit OVERRIDE**,
   bounded resource use). Empty `DISABLED` folders alone do not satisfy source usability.

Only level 3 may fail the pack load. Levels 1–2 produce defaults/partial models and diagnostics.
A valid dimension-only pack therefore loads with its empty base entry and usable override;
unusable roots remain source-local and cannot invalidate an unrelated surviving set. This does
not merge sources, require link success, or change §5's ordered safety/bounds/read failures.
The immutable configuration fingerprint hashes pack bytes, normalized paths, the finalized
default-plus-persistence option state, load-time macro policy, both companion booleans,
renderer-feature availability, capability identity fields, parser schema version, the computed
`CompatibilityStatus` as its exact enum name after all minimum-edition rules are evaluated, and the canonical current-schema
`IdMappingInput` including every file state and ordinary/forced rule-list fingerprint.
Hidden domain, pack-key, catalog-identity, and discovery token identities are excluded from
canonical fingerprints; their authenticated semantic values and the complete option value map are
included, so equal pack/configuration inputs remain byte-deterministic across service instances.
The current schema also hashes the canonical complete known engine-setting input after missing-key
defaults, with domain `engineSettings`, schema integer, then the eight `(key,value)` pairs in
§5.1 key order using `seq`/`atom` below. This retains `default` versus explicit preferences even
when their effective macros coincide: their runtime precedence differs. Values are canonical
wire tokens, multipliers use the macro-normalized float spelling, and AA is exact `0`.
The configuration fingerprint also hashes every published component of `ResourceRequirements`
through one canonical payload. Define `atom(s)` as decimal UTF-8 byte length, `:`, then the exact
UTF-8 bytes; define `seq(v...)` as decimal element count, `[`, then each element's decimal encoded
byte length, `:`, and encoded bytes, then `]`. The payload is
`seq(atom("ResourceRequirements"), atom(schemaVersion), encode(resources))`. `encode` maps a record
to `seq(atom(exactRecordName), its components in declaration order)`; a sealed variant to
`seq(atom(exactVariantName), payload components in declaration order)`; an optional to
`seq(atom("Absent"))` or `seq(atom("Present"), encode(value))`; and a list or set to a counted
sequence in its published order. A map is a counted sequence of `seq(encode(key),encode(value))`
entries in its published key order. Booleans are atoms `true`/`false`, integers are canonical
base-10 atoms, enums are exact-name atoms, and strings are exact UTF-8 atoms. A finite float is an
atom containing the eight lowercase hexadecimal digits of `Float.floatToRawIntBits` after
constructors canonicalize either signed zero to `+0.0f`; non-finite values remain invalid. Thus all
fields, keys, optionals, variants, collection counts, and values participate. In particular,
attachment format is exactly `DefaultRgba` with no payload or `Explicit` followed by the exact
`ColorInternalFormat` name. Equivalent maps/sets yield identical bytes regardless of insertion
history, while any published leaf mutation changes the payload. This is producer canonicalization,
not a portable digest or downstream wire-format promise.
The canonical ID rule encoding uses this same record/variant/component/list/optional codec.
`legacyMetadata` encodes Absent or Present `MetadataConstraint` with every ordered `IntegerRange`
endpoint; `PropertyPredicate.acceptedValues` encodes each exact `Literal` or `IntegerInterval`
tag and its payload in authored order. No raw-selector reparse, interval expansion, sorted union
or registry-derived match enters file/configuration identity. Endpoint, variant, order, predicate,
provenance or alternate-list changes therefore change the canonical payload. Fingerprints retain
exact source bytes as before, so equivalent normalized constraints need not equate different files.
The current schema additionally includes
`seq(atom("PackAssetSnapshot"), atom(schemaVersion), encode(assets.manifest()))` exactly once.
Only `PackAssetMetadata` record/enum/path/optional/count/hash leaves enter this payload, using
the codec above; available digests are SHA-256 of same-load original bytes, absent/unreadable rows
have no invented digest. `PackIdentity.contentHashes` uses those same SHA-256 values for successfully
read files (and the same byte-hashing rule for other indexed regular files); missing/unreadable
paths have no content-hash entry. The manifest binds their distinct outcomes into configuration
identity. Hashes remain opaque to ordinary consumers. Successfully acquired whole-pack bytes/identity
already participate in the established configuration input; this additional payload binds the
declared-domain/absence/unreadable distinction, not another binary copy. No snapshot/cursor object identity,
lookup history, public capability graph, or containing configuration fingerprint is encoded.
There is no asset fingerprint back-reference: hash bytes → identity/manifest → configuration
fingerprint → materialization fingerprint, never recursion. Inspection projects these same
metadata leaves without rehashing a pathname or serializing a byte view.
The locale payload is exactly
`seq(atom("localizedDecorations"), atom(schemaVersion), encode(options.localizedDecorations()))`.
The existing codec encodes all nine `LangDecorations` record components in declaration order,
all locale/decorated keys, empty strings, empty inner maps and outer-map presence in their
published canonical order. Missing locale differs from present-empty locale; every retained
translation change changes this payload. Original file bytes/paths already participate in pack
identity, including collision losers and malformed files. No host locale, selected language,
fallback result or lookup history enters it. Locale-only client switching needs no new fingerprint.
The §0.57 Phase 4 projections read the existing `ProgramRequirements` components in that one
payload. They add no consumer tag, duplicated resource payload, lookup history, derived registry
bitmask, or object identity to either fingerprint. Reading a projection cannot change the
configuration or materialization identity; changing its underlying owner value remains covered
by the existing resource codec.
Both configuration and materialization fingerprints additionally include exactly
`seq(atom("CompanionOptionMacros"), atom(normalMap), atom(specularMap))`, using the `true`/`false`
atoms above in record-component order. The materialization payload uses the retained same-build
pair, not a fresh caller value. Neither omitted macro definitions nor identical transformed text
elide this payload: changing either boolean changes both fingerprint inputs even when the root
never references that macro. Equal semantic inputs remain deterministic across service instances.
The configuration fingerprint additionally includes
`seq(atom("textureDeclarations"), atom(schemaVersion), encode(properties.textureDeclarations()))`,
using the same record/list/enum/scalar codec. `SourceAttribution` encodes its path via
`canonicalString()` and its positive line/column in declaration order. Every decoded key/value,
ordinal, attribution, disposition, duplicate, and list position participates, including
`UNRESOLVED_KEY` and `INVALID_VALUE`; neither an unchanged executable spec nor absence of a
sidecar can elide this payload. Equal semantic streams yield equal payloads across service
instances; changing one retained occurrence changes the payload. No downstream GL state or
interpreted `.mcmeta` parameters enter this producer-owned declaration payload.
For every Phase-3-produced `EngineDiagnostic`, all six record components and every argument are
non-null, `args` is immutable, and an empty `detail` is the sole absence form. The closed argument
runtime algebra is exactly `String`, `Boolean`, `Integer`, or `Long` (exact boxed classes);
collections, arrays, maps, enums, other numbers/objects, nested values, and null are rejected by the
Phase 3 diagnostic factory before publication. Diagnostic encoding is
`seq(atom("EngineDiagnostic"), atom(severity.name()), atom(channel.name()), atom(messageKey),
encodeArgs(args), atom(detail), atom(logChannel))`, where `encodeArgs` is
`seq(atom("DiagnosticArgs"), encodeArg...)`. For exact Java class `String`, `Boolean`, `Integer`,
or `Long`, respectively, `encodeArg(v)` is `seq(atom("String"),atom(v))`,
`seq(atom("Boolean"),atom(v))`, `seq(atom("Integer"),atom(v))`, or
`seq(atom("Long"),atom(v))`. String payloads are exact UTF-8, Boolean payloads are `true`/`false`,
and Integer/Long payloads are canonical signed base-10; the tag distinguishes equal text such as
string `"1"`, integer `1`, and long `1L`.

Discovery applies that encoding recursively to diagnostics in candidates and the result. The fixed
overflow snapshot is encoded by the same path, and `DiscoveryLimits` construction rejects any
`maxSnapshotBytes` smaller than its exact encoded size. Unsupported diagnostic arguments cannot
enter a partial snapshot; factory rejection is an implementation invariant tested before counting.

It also hashes `PackConfiguration.customExpressions()` as one canonical list in source order: fixed
payload domain tag `customExpressions`, configuration schema version, list count, then for each
declaration its `kind` and `type` enum tags, exact `name`, exact unescaped `rawExpression`, canonical
base-10 `sourceOrdinal`, attribution path
`canonicalString()` UTF-8 bytes, and canonical positive line/column. No map conversion, duplicate
collapse, or name/type sort is permitted, so changing declaration order, multiplicity, text, type,
kind, ordinal, or attribution deterministically changes the configuration fingerprint. This payload
uses the same length-prefixed canonical scalar/string/list framing defined above. It excludes
downstream macro contributions and geometry requests. Each materialization uses a separate
domain-tagged canonical payload:
`seq(atom("MaterializedSource-v23"), atom(schemaVersion), encode(root),
encode(configurationFingerprint), encode(contribution), encode(geometryRequest),
encode(language), encode(geometryForm), atom(transformedText), encode(sourceMap),
encode(CanonicalDeclaredUniformPayload(schemaVersion,declarations)), encode(diagnostics))`.
The `encode`/`atom`/`seq` rules above apply recursively, with exact record/variant names and
declaration-order components; `OptionalInt` uses the same Absent/Present tags and canonical integer.
`SourceId`/normalized paths use canonical path strings, maps have ascending numeric file-number
order, mappings and traces retain published order. Compute SHA-256 over these exact payload bytes,
exposing lowercase 64-hex `MaterializationFingerprint.value`, then embed that one result in both
`MaterializedSource.fingerprint` and `DeclaredUniformCatalog.materialization`. Neither embedded
result is an input; the uniform payload has no back-reference. Diagnostics use the existing exact
six-component codec. No nonce, object identity, GL handle, host path, query result or mutable state
enters the payload. This materialization digest is a local cache identity, not a new wire format.

The configuration fingerprint still includes original pack bytes/paths, complete same-build
macro/capability/option inputs, dimensions/modes/base links and source-root order, plus every
published `LegacyGeometryConfig` field and both physical-or-invocation site spans. The current schema
invalidates earlier interpretation even when bytes are identical. Materialization adds its
**closed request and result forms**, the recognized native pair, effective/source-declared layout
fields, source version/profile/extension order, exact final text/map, complete attributed catalog
and diagnostics. Thus route, count, site, layout, version, macro, source-map or declaration changes
cannot share the same payload; native and ordinary/core are distinct even if text happens to match.
A private lookup cache keys the immutable containing configuration/root plus canonical contribution
and request, and verifies/returns the immutable fingerprint-bound result; it never reuses a result
by text alone. No caller-supplied plan or raw source scan participates.

When `schmaloogium.debug.saveSources` is true, the materializer writes final sources plus a
source-ID manifest under a sanitized runtime `shaderpacks/debug/<pack-id>/` tree. A dump failure
warns and does not change the configuration. Dumps are local developer artifacts containing pack
source and must never be committed, placed in conformance goldens, or uploaded as reports.

No mutable builder, `Path` rooted inside a pack, zip filesystem, host reader, or output stream is
reachable from the published configuration. D-P3-69 grants only caller-local read-only heap cursors.

## 5. Cross-phase interfaces


The following are the complete Phase 3 publication surface. Every consumer receives the same
`PackConfiguration` instance (or a versioned replacement), not a parallel parser result.

### 5.1 Exposed Phase 3 contracts

| Exposed contract | Content | Consumer(s) |
|---|---|---|
| `PackFrontEnds.create` / `PackFrontEndServices` | dependency-free public acquisition of one immutable, thread-safe, final bundle with no public constructor; it exposes four readable receivers plus typed acquisition of bundle-owned safe persistence access, and owns one bounded authentication lifetime, the published finite discovery-retention policy, and no open handles | Phases 7, 9, 12 |
| `PackFrontEnd.discover` / `discoveryLimits` / `resolveFilesystemCandidate` / `PackDiscoveryRequest` / `PackDiscoveryResult` / `DiscoveryLimits` / `PackCandidate` / `FilesystemCandidateReference` / `FilesystemCandidateResolution` | deterministic immutable discovery snapshots; exact candidate/byte overflow result; completion-LRU bounded directory retention; directory-keyed or explicit non-directory-keyed invalid generations; exact sentinel, supersession, eviction, and invalid-snapshot behavior; durable references and closed resolution outcomes | Phase 7 bootstrap/reload; Phase 12 selection UI |
| `PackFrontEnd` / `CURRENT_SCHEMA_VERSION` / `packOptionsTarget` / `PackOptionsTargetAcquisition` / `PackOptionsTargetRejection` / `PackLoadRequest` / `PackLoadResult` | atomic current-schema load; canonical §2.2 request includes required companion pair and non-Off non-null `internalOptions` Optional. §4.3/§5 bind every selection-first validation, target rejection and safe filesystem persistence rule, plus D-P3-65 Internal fresh-catalog rebinding. Off short-circuits all other fields. No old-shape load fallback | Phase 7 bootstrap/reload; Phase 12 selection; Phase 2 inspection |
| `PackConfiguration` | single validated downstream truth, immutable and fingerprinted; its evaluator takes no option state and uses exactly `options().state()` finalized by the same atomic load; nested `macros()` retains the load's companion pair unchanged, while `properties()` now includes the lossless texture declaration stream. Both booleans and §4.10's complete declaration payload participate in the configuration fingerprint | Phases 4–13 as listed below |
| `PackConfiguration.assets`, `PackAssetSnapshot`, `PackAssetMetadata`, `PackAssetAvailability`, `PackAssetAcquisition`, `PackAssetReferenceFailure`, `PackAssetBytes` | required same-load immutable acquisition capability immediately after `sources`; exact §2.2 signatures and complete §4.1 D-P3-69 declared-domain, failure, read-only-cursor, bounds/provenance/lifetime rules; §4.10 metadata-only acyclic fingerprint | Phase 13 acquires PNG/raw/noise/sidecar bytes; Phase 7 passes/retains exact configuration across resource-only NONE; Phase 2 projects metadata only |
| `PackIdentity`, `CompatibilityStatus`, `DimensionKey`, `DimensionConfiguration`, `DimensionMode` | `PackIdentity` is `(NormalizedPackPath selectedRoot, Map<NormalizedPackPath,String> contentHashes)` with immutable canonical-path order; `CompatibilityStatus` is the closed `COMPATIBLE`/`REQUIRES_NEWER_EDITION` result, and dimensions are the ordered base/override/disabled map; absent world keys select the base entry, overrides expose only their own source roots, and disabled entries expose none | Phases 7, 12 |
| `PackSelection`, `PackCandidateId`, `DiscoveryGeneration`, `RuntimeIdentityData`, `RendererFeatureData`, `PackInputLimits`, `PackLoadFailure`, `PackLoadFailureCode`, `PackCandidateKind`, `PackCandidateStatus`, `ConfigurationFingerprint` | exhaustive selection/failure/value domains; runtime identity accepts exactly MC `(1,12,2)`, the bounded edition grammar, and bounded canonical decimal engine version; the fingerprint includes final `CompatibilityStatus`, so compatibility-changing edition comparisons invalidate retained state, and §4.10's companion payload; renderer-feature shape remains unchanged but no longer gates companion emission; each failure has the exhaustive cause/precedence mapping below and one immutable Phase-1 primary diagnostic, with exact-once delivery only when a non-null reporter exists; permitted token classes and their package-private issuer share `com.schmaloogium.engine.pack` under the unnamed module, authenticate owning instance and generation kind, use identity equality/hash, and are nonserializable | Phases 7, 12 |
| `SourceCatalog`, `SourceDocument`, `SourceKey`, `SourceId`, `IncludeEdge`, `SourceMap`, `SourceMapping`, `MaterializedSource`, `MaterializationFingerprint`, `SourceMaterializer`, `MaterializationResult`, `SourceSpan`, `LegacyGeometryConfig`, `LegacyGeometrySite`, `GeometrySourceRequest`, `GeometrySourceForm`, `GeometryLayout`, `GeometryLayoutDeclaration`, `ShaderLanguage`, `ShaderExtensionDirective`, their closed enums | canonical physical-file/root/edge identities and immutable executable-program projection remain; §§2.2/4.5 define the exact current-schema native-preserving request, final language/geometry/map/result algebra, mixed source-layout precedence, same-load options/macros/contribution, source-local failure, complete final declaration catalog and §4.10 route-distinct fingerprint. No Translate alias, caller option state or source reparse | Phase 4; Phase 7; Phase 12; Phase 2 source-free inspection only |
| `IdMappingInput`, `IdMappingFileInput`, `IdMappingParser`, `IdMappingMacroEnvironment`, mapping rule/state/kind/era/selector types, `MetadataConstraint`, `IntegerRange`, `PropertyValueConstraint`, `PropertyPredicate`, `MappingOrigin` | complete §2.2/§4.9 D-P3-73 immutable typed metadata unions/literal-or-integer-interval predicates, exact grammar, ordering, conjunction, bounds/no-expansion, invalid-selector no-overmatch, canonical identity and pack/mod/ordinary/alternate/layer symmetry; one rule per occurrence. D-P3-72 isolated BLOCK/ENTITY alternates remain MODERN, ordinary CLASSIC/state unchanged, ITEM/LAYER alternates empty; P9 alone selects PRESENT_EMPTY/nonempty alternate and resolves finite registries, never reparses selectors. D-P3-47/D-P3-49 TAG grammar unchanged | Phase 9; Phase 2 source-free inspection; resolved layer results to Phase 7 |
| `MacroConfiguration`, `CompanionOptionMacros`, `MacroDefinition`, `MacroOverride`, `MacroContributor`, `MacroContribution`, reserved `phase6.centerDepthSmoothRedirect` slot | immutable §2.2/§4.4 graph, exact A–G projection, eight-name option family, required independent companion pair and D-P3-64 cycle-free effective old-light macros; reserved zero AA emits no FXAA. Full merge/override validation/action order and same-build fingerprints in §§4.4/4.10 remain binding: the protected-target check enforces §4.4's whitelist exactly — override targets are non-version identity/capability names, and §4.5's reserved intrinsics (`__VERSION__`, `__FILE__`, `__LINE__`, `GL_core_profile`, `GL_compatibility_profile`, `GL_ARB_geometry_shader4`) plus any other non-whitelisted name are rejected with pre-I/O `INVALID_REQUEST`. Singular Phase 6 contribution is unchanged; no consumer-supplied later macro/option state | Phase 6 contributor; Phase 4 materialization; Phase 7 inputs; Phase 12 global settings; Phase 13 preliminary policy through Phase 7; G8/S3 |
| `OptionConfiguration`, sealed `OptionCatalog` / `OptionState`, `OptionDefinition`, option values/enums, `OptionStateResult`, `OptionStateValidation`, `OptionStateFailure`, profiles/screens/sliders/decorations | catalog-issued complete default/constructed/updated states with closed failures and safe out-of-list warnings; profile inference returns `Inferred` or `InvalidState`; `ScreenModel.resolvedColumns(expandedSlotCount)` treats configured columns (default two) as a floor, counts the retained array after `*` expansion including ordinary option, applicable profile/subscreen, and empty slots, and raises the floor to `ceil(expandedSlotCount/9)` | Phase 4; Phase 12; Phase 2 inspection |
| `PersistenceRootConfiguration`, safe-access/target types, both persistence codecs, and their request/result/failure types | exact direct-child files and safe-write lifecycle; option operations authenticate domain and same-pack target/catalog pairing plus exact state/catalog identity before I/O; global operations authenticate access and implement §4.3's baseline-overlay/result matrix, §5.1 value-domain invariant, and exact all-entry output | Phase 7 load; Phase 12 standalone settings |
| `ShaderPropertiesModel`, `EngineFlags`, `MinimumEditionRule`, `UnknownProperty`, `EngineOptionData` | closed immutable Appendix F model includes `List<TexturePropertyDecl> textureDeclarations` immediately after `textures`, with §5.1's exact capture/classification/reduction semantics; decoded `texture.*` occurrences are excluded from `unknownProperties`, whose other-key retention is unchanged. The separate eight-known-setting/unknown-safe global domain now uses D-P3-61 tri-state/zero-only semantics; pack engine flags are unchanged. `MinimumEditionRule` accessors retain exact decoded suffix/value text; source order, canonical map order and nested collections are frozen | behavior owners in §3.1; Phase 12 persistence/UI; Phase 13 texture publication |
| `ProgramStateModel`, `ProgramKey`, state value types, `ProgramStateEvaluationResult`, `EvaluatedProgramStates`, `EvaluatedProgramState` | current-schema declarations; state-free evaluation uses finalized containing configuration; one state per executable catalog key, no source-absent raw properties, only eligible/virtual-pre flips. Complete §2.2/§4.8 and binding rules below remain unchanged apart from containing schema | Phase 4; Phase 5 flip state |
| `ResourceRequirements` | current-schema immutable closed graph and complete leaf/default/fold/ordering rules below and §4.7; includes positional DrawSlot and explicit/unsized format distinction, gdepth RGBA32F force, direct per-program mipmap/vertices projections and full §4.10 codec. D-P3-66 removes `WorldRenderConstants.superSamplingLevel`; D-P3-68 replaces the nested legacy rewrite site with provenance-only `LegacyGeometrySite`; other resource semantics remain unchanged | Phase 4 routing/instances/legacy geometry/mipmap/vertices; Phase 5 sizing/formats/clear/mipmap; Phase 6 depth/smoothing; Phase 8 shadow; Phase 13 noise; Phase 7 execution; Phase 10 vertices |
| `CustomTextureSpec`, `NoiseTextureSpec`, `TexturePropertyStage`, `TextureBindingKey`, `TextureTarget`, `ColorInternalFormat`, `PixelFormat`, `PixelType`, `TextureSidecarRef`, `TexturePropertyDecl`, `TexturePropertyDisposition` | Canonical §2.2 payload with unchanged typed enum and reducer; D-P3-70 corrects exact external raw target spellings, raw formats, PNG rule, stage mapping, sidecar retention and §4.10 fingerprint. D-P3-67 adopts documented numeric discriminators and P13 `.mcmeta` sampling only; all decoded active texture occurrences remain lossless, including unknown keys, without invented suffix support or a new sampling API | Phase 13 existing typed specs, sidecar references and declaration diagnostics; Phase 3 owns property-key parsing |
| `InternalPackSource` / `InternalPackReadException` / `InternalPackSnapshot` / `InternalPackEntry` / `NormalizedPackPath` | stable content identity plus bounded, ordered, directory-aware manifest; `snapshot` may raise only the declared provider-only checked exception, which is reduced to an attributed failure; defensive byte copies and the canonical path projection are binding | Phase 7 supplies content and consumes the projection |
| `PackFrontEnd.inspect`, `PackInspectionResult`, `PackDecisionSnapshot`, `DecisionValue`, `DecisionSource`, `DecisionDiagnostic` | §5.1.1's complete one-load acquisition, allowlisted source-free projection and same-read archive provenance; P4 same-request resolution and P5 pure sizing/capability are separate required enrichments for a complete golden | **Phase 2** |
| `InternalOptionSnapshot`, `InternalOptionCaptureResult`, `InternalOptionFailure`, `OptionCatalog.captureInternalOptions` | D-P3-65's opaque same-bundle/exact-Internal-identity snapshot, closed capture failures and fresh-catalog atomic-load rebinding; optional request input after `internalPackSource`; session-only P7 ownership, no persistence target or materializer state argument | Phase 7 reload/session lifetime; Phase 12 Apply/profile/reset; Phase 2 inspection |
| `OptionConfiguration.localizedDecorations` / `LangDecorations` | sole current-schema deep-immutable locale authority, unchanged since D-P3-63; §4.3's acquisition, normalization, collisions, per-key empty-preserving fallback and cache rules; §4.10 complete fingerprint and §5.1.1 source-free catalog | Phase 12 presentation; Phase 2 inspection |
The public declarations in §2.2 are incorporated into binding rows above, not merely illustrative.
In particular, the options/screens row includes payload-free `ScreenProfileEntry()` as an
explicit P12 **and P2** contract: P12 maps the literal selector to `ProfileCycle`, reads the
declaration-ordered `profiles()` list, and derives selected/Custom from
`inferProfile(previewState)`; it never reads an entry profile component. P2 projects that
zero-component record as `Fields` containing only `$type=ScreenProfileEntry`, with no name,
selection or Custom field. The complete definitions and finalized option state remain in
their existing separate projection nodes. No old-shape constructor/accessor/decoder shim exists.
The dependency-free `PackFrontEnds.create()` factory is the only public construction route for
`PackFrontEndServices`; the final class has no public constructor. Each call creates four non-null,
thread-safe readable receivers that cannot be mixed with receivers from another factory call.
One bundle owns one authentication domain from construction until ordinary reachability ends,
retains at most `discoveryLimits().maxRetainedDirectorySnapshots()` latest directory snapshots
under the completion-LRU rules below, and owns no open pack or persistence handle.
Every returned result remains immutable after later calls. The front end alone issues IDs,
generations, and pack targets. The bundle alone issues sealed `PersistenceFileAccess`; its front
end and both codecs reject access from another domain before file I/O, and the option codec also
rejects a foreign pack target. The global codec authenticates its access receiver but its fixed
`GlobalOptionsTarget` carries no pack credential; the ID parser requires neither target nor access.
Thus Phases 2, 7, 9, and 12 obtain every implementation without constructing a bundle, issuer, or
access provider.

The discovery/load rows bind every exact declaration and operation in §2.2. Candidate kind/status
enums are closed; `PackSelection` is `Off`, `Internal`, or `Filesystem(PackCandidateId)`;
`PackCandidateId`, `DiscoveryGeneration`, `PackOptionsTarget`, and `PersistenceFileAccess` have
only package-private final permitted implementations in `com.schmaloogium.engine.pack`. They are
never consumer-constructed or serialized. Durable reference resolution and typed target
acquisition are the only restart bridge. Macro, option, program-state, source/materialization,
geometry, properties, resource, ID-map, and internal-source declarations are bound by their rows.
Exact signatures, components, variants, defaults, absence, ordering, validation, failure, and
lifecycle semantics are part of those rows.

`PackFrontEndServices.persistenceFiles(PersistenceRootConfiguration)` is the only safe-access
factory. It validates and canonicalizes the supplied shaderpacks/game directories and returns
`Acquired` or `InvalidRoots`; an acquired receiver is immutable, thread-safe, and bound to that
bundle and those roots. It validates direct-child target locations, rejects symlink targets/parents
and path escapes, snapshots bytes, closes all handles before returning, and performs §4.3's safe
temporary-file/atomic-move protocol. Per-pack access is rooted only at the configured shaderpacks
directory; global access is only the direct child `optionsshaders.txt` of the configured game
directory. Codec results expose only immutable state, diagnostics, statuses, and failures; no
reader, stream, host `Path`, or mutable builder crosses §5.

`packOptionsTarget` returns only `Acquired(PackOptionsTarget)` or
`Rejected(PackOptionsTargetRejection)`. Its discovery/acquisition linearization and six exact
rejection reasons are those in §4.3. An acquired target carries the canonical candidate reference
and exact direct-child filename and remains valid after later discovery; a rejection performs no
persistence I/O. Access never accepts display text, a host path, caller-selected sanitization,
digest slots, or aliases.

`OptionCatalog`/`OptionState` are sealed and engine-issued. The catalog operations and deterministic
failure priority are exactly §4.3: defaults are complete; construction requires exactly all known
names; update changes one available name; switches require Boolean values, variable/constants
require safe text, and safe out-of-list text warns but succeeds. `OptionConfiguration` rejects a
state not issued by its catalog. `inferProfile` alone accepts a candidate state for preview.
`SourceMaterializer.materialize` and `PackConfiguration.evaluateProgramStates` accept no state and
use exactly the containing configuration's finalized `options().state()`.
The source/materialization row also binds §2.2's retained same-build macro snapshot and §4.5's
pre-jcpp application. `MacroConfiguration.companionOptionMacros()` is a non-null immutable
`CompanionOptionMacros(boolean normalMap, boolean specularMap)` with value equality and exact
component accessors. Its `optionMacros()` projection must agree with that pair; construction
rejects null or inconsistent publication data, never silently repairs it. The load owns construction
and publishes no partial configuration. Both fingerprints use §4.10's exact pair encoding.
No Phase 13 type, callback, GL handle, plan, or mutable policy object crosses the Phase 3 seam.

For persistence, the hidden catalog pack key must match the target's bundle domain and exact
engine-issued candidate credential (not merely its potentially ambiguous durable reference), and
baseline/current state must match the exact catalog identity. Any null field, same-domain cross-pack
pairing, or foreign/stale state returns read `InvalidRequest` or write `FAILED/INVALID_REQUEST`
before read, rewrite, temporary-file creation, or other I/O. The read rejection carries no state;
the caller retains its baseline. These checks apply equally to load-integrated and standalone use.

The global codec's binding matrix is §4.3's: invalid requests and foreign access are pre-I/O
`FAILED/INVALID_REQUEST`; a read returns the valid baseline (or canonical empty value when the
baseline itself is invalid). `ABSENT` returns the baseline, `APPLIED` returns the last-valid-
duplicate overlay, and access/decode `FAILED` returns the baseline with the exact failure.
Successful writes serialize every validated `EngineOptionData` entry, including retained unknown
safe keys, rather than a changed-only subset. Read failure is present iff status is `FAILED`;
write failure is absent iff status is `COMMITTED`.

The following is the sole `EngineOptionData` and global-codec validity invariant.
`values` is defensively copied into immutable unsigned-UTF-8 key order. The eight known keys are
exactly `normalMapEnabled`, `specularMapEnabled`, `renderResMul`, `shadowResMul`, `handDepthMul`,
`oldHandLight`, `oldLighting`, and `antialiasingLevel`. Only `normalMapEnabled` and
`specularMapEnabled` are Boolean keys, accepting exact lower-case `true` or `false`.
The two old-light keys accept exact lower-case `default`, `true`, or `false`; adapters map
these to typed `DEFAULT`, `TRUE`, or `FALSE`, never serialize enum names. The three `*Mul`
keys accept `[+-]?(?:[0-9]+(?:\.[0-9]*)?|\.[0-9]+)(?:[eE][+-]?[0-9]+)?`, parsed as a finite
Java float strictly greater than zero. `antialiasingLevel` accepts only the exact string `0`:
reserved compatibility storage, not AA enablement. Empty or other known values are invalid.

An unknown-safe key matches `[A-Za-z_][A-Za-z0-9_.-]*`. Its value may be empty but must be a
well-formed Unicode scalar sequence containing none of U+0000..U+001F, U+007F..U+009F, U+2028, or
U+2029. `EngineOptionData` is valid only when its map, every key, and every value are non-null and
every entry satisfies its known or unknown-safe predicate; its canonical empty value is the
immutable empty canonical map. Construction rejects invalid data rather than deleting entries.

Request-object validation is separate from file-occurrence parsing. Read/write validation checks a
non-null request and its fields in declaration order, then own-domain access, exact target, and
`EngineOptionData`. Data failures use this priority: null map/key/value; invalid key; invalid known
value; unsafe unknown value. Within one category the least key by `String.compareTo` supplies the
diagnostic. Any failure is pre-I/O `INVALID_REQUEST`; only an invalid read baseline selects
canonical empty rather than that baseline.

After a valid request, a Properties framing/unescape failure is `FAILED/INVALID_ENCODING`.
Otherwise each decoded logical occurrence is classified independently: known typed-valid overlays;
known typed-invalid warns and is omitted; unknown-safe warns and overlays; invalid-key or
unknown-unsafe warns and is omitted. An omitted occurrence never erases an earlier valid value, and
the last valid duplicate wins. A valid writer emits one `key=value` record per canonical-order
entry, no header or timestamp, and one final LF. Keys need no escaping under their accepted grammar.
For values, printable ASCII is literal except `\`, `=`, `:`, `#`, `!`, and every space, each of
which is backslash-prefixed; every non-ASCII UTF-16 code unit is `\uXXXX` with uppercase hex.
The resulting bytes are ISO-8859-1 and are the exact all-entry output.

The binding option projection begins with `companionOptionMacros.normalMap→MC_NORMAL_MAP 1`
and `companionOptionMacros.specularMap→MC_SPECULAR_MAP 1`, each iff its boolean is true.
False means absent, not `0`; all four pairs are legal, and neither engine options nor renderer
features add a second emission gate. The remaining six positions use `EngineOptionData`:
`renderResMul→MC_RENDER_QUALITY`, `shadowResMul→MC_SHADOW_QUALITY`, and
`handDepthMul→MC_HAND_DEPTH` always;
`oldHandLight→MC_OLD_HAND_LIGHT 1` and `oldLighting→MC_OLD_LIGHTING 1` iff D-P3-64's
user→parsed-pack→true resolution is true. `antialiasingLevel=0` always omits
`MC_FXAA_LEVEL`. Missing-key defaults are
`true,true,1.0,1.0,0.125,default,default,0` in the eight-known-key order above, not defaults
for the required companion pair. The Phase 7/12 global-input path supplies that complete
baseline. Unknown-safe keys round-trip but never project to macros; obsolete GUI spellings
are not aliases. Multiplier replacements are exactly Java-25 `Float.toString` of the accepted
parsed float, with plain notation on `[1.0e-3f,1.0e7f)` and otherwise uppercase-`E` notation.
These exact strings and the family order are visible through `MacroConfiguration.optionMacros()`.
The global `normalMapEnabled`/`specularMapEnabled` entries still validate and round-trip under
the unchanged global codec, but do not override the typed pair in Phase 3. It has no missing-value
default: every non-`Off` caller supplies it explicitly. The v0.1 caller uses `(false,false)`; Phase 7
adapts the v0.5 preliminary policy before load. This replaces D-P3-31's former producer gates.

**D-P3-64 priority and no-AA boundary.** After the one A–G-only properties parse, resolve each
old-light Boolean: explicit user TRUE/FALSE, else explicit pack TRUE/FALSE, else true.
The last fallback matches P9 §4.11 and P10 §5's incorporated §4.8.1 local decisions; it is not
an externally documented missing-value default. Emit the corresponding macro iff enabled.
P7 uses the same inputs and precedence for the P9/P10 runtime policy; no runtime edit of the
immutable macro list, callback into behavior owners, registry, GL state or shader-analysis result
is required. Properties and ID maps never receive option macros, so they cannot depend on this
result. Pack options do not change that A–G-only properties environment.
The complete `engineSettings` fingerprint retains raw preference identity even when effective
macros coincide. G6 author docs explicitly require macros “When ... enabled” and user-over-pack
precedence; they do not specify absent fallback. D-P3-64 therefore replaces D-P3-61's unsupported
explicit-user-only divergence while leaving the fallback openly local. OQ-7 concerns final
renderer identity/feature posture and still requires its upstream pack-matrix experiment.
RESEARCH §1.2's no-AA scope means reserved zero-only storage and no `MC_FXAA_LEVEL`; it grants
no AA control or algorithm. The independent AA/SSAA authority disposition is in §11.5.

The binding standard A–F projection is exact. `MC_VERSION` is ASCII decimal `11202`, and a non-
`Off` MC tuple other than `(1,12,2)` is pre-I/O `INVALID_REQUEST`. `engineVersion` matches
`0|[1-9][0-9]{0,9}`, parses no greater than `2147483647`, and is the exact
`SCHMALOOGIUM_VERSION` replacement when enabled; every other spelling is pre-I/O
`INVALID_REQUEST`. `MC_GL_VERSION` is decimal `glVersionMajor*100 + glVersionMinor*10`, with major
`>0`, minor `0..9`, and no overflow. `MC_GLSL_VERSION` accepts only
`^([1-9][0-9]*)\.([0-9]{2})(?:[ \t]+[^ \t\r\n][^\r\n]*)?$` and emits decimal `major*100+minor`;
malformed, null, or overflowing capability data makes a non-`Off` request
`Failed(INVALID_REQUEST)` before pack I/O. Exactly one empty-replacement OS macro is emitted:
`WINDOWS→MC_OS_WINDOWS`, `MACOS→MC_OS_MAC`, `LINUX→MC_OS_LINUX`, `OTHER→MC_OS_OTHER`.
Vendor first-match patterns are `^ati.*→ATI`, `^intel.*→INTEL`, `^nvidia.*→NVIDIA`,
`^x\.org.*→XORG`, else `OTHER`; renderer patterns are
`^(amd|ati|radeon).*→RADEON`, `^gallium.*→GALLIUM`, `^intel.*→INTEL`,
`^(geforce|nvidia).*→GEFORCE`, `^(quadro|nvs).*→QUADRO`, `^mesa.*→MESA`, else `OTHER`.
Patterns use `Pattern.CASE_INSENSITIVE`, are start-anchored against the whole non-null raw string,
and emit the corresponding `MC_GL_VENDOR_*` or `MC_GL_RENDERER_*` macro with empty replacement.
No other vendor/renderer macro is emitted.

Any edit to an incorporated declaration or consumer-visible semantic must update its §5.1 row in the
same revision, including an explicit `unchanged` entry when the row remains exact; that edit changes
the monitored cross-phase region and requires fresh verification. `PackConfiguration` record/schema
changes additionally follow §5.3.

The binding custom-expression algebra is:

```java
enum CustomExpressionKind { UNIFORM, VARIABLE }
enum CustomExpressionType { FLOAT, INT, BOOL, VEC2, VEC3, VEC4 }
record SourceAttribution(NormalizedPackPath source, int physicalLine, int physicalColumn) {}
record CustomExpressionDecl(CustomExpressionKind kind, CustomExpressionType type,
    String name, String rawExpression, int sourceOrdinal, SourceAttribution attribution) {}
List<CustomExpressionDecl> PackConfiguration.customExpressions();
```

The accessor is a named immutable projection of the expression collection already owned by
`ShaderPropertiesModel`, not an additional `PackConfiguration` record component. Its list order is
the valid declaration-occurrence order after properties preprocessing and Java-Properties
unescaping; list index and `sourceOrdinal` are equal. Neither an exact duplicate property key nor a
repeated name is collapsed. `name` and `rawExpression` preserve decoded text exactly, and the
attribution coordinate anchors the declaration key; expression-relative spans are Phase 11 data.
The canonical fingerprint payload is the fixed `customExpressions` domain tag, configuration schema
version, list count, and then every record in that order, with the fields and scalar/string encoding
fixed in §4.10. No declarations yields an immutable empty list and the canonical zero-count payload.
Consumers may neither sort/deduplicate the list nor infer a new kind/type. Unknown kinds/types are
rejected during configuration construction because both enums are closed executable domains.

`TextureBindingKey` is `(TexturePropertyStage stage, String sampler, OptionalInt duplicateDiscriminator)`.
`TexturePropertyStage` is the closed enum `GBUFFERS`, `DEFERRED`, `COMPOSITE`, in that fixed
validation and ordering sequence. `sampler` is the exact non-empty App F.5 sampler-name segment;
the discriminator is absent or the parsed terminal single decimal digit `0..9`, used only to
avoid duplicate property keys, never as filter/wrap state or part of the sampler. Only a complete key matching
`texture.<gbuffers|deferred|composite>.<sampler>[.0-9]` produces this key. An additional segment
does not get stripped, interpreted, or assigned to a base key: the complete occurrence instead
survives as `UNRESOLVED_KEY` below, with no executable spec. Expansion remains fixed: `GBUFFERS`
targets gbuffers and shadow programs, `DEFERRED` targets deferred programs, and `COMPOSITE`
targets composite and final programs.

**Lossless owner-side texture declaration contract (D-P3-59).** The canonical §2.2
`TexturePropertyDecl(String key,String value,int sourceOrdinal,SourceAttribution attribution,
TexturePropertyDisposition disposition)` is published at
`PackConfiguration.properties().textureDeclarations()`, immediately after `textures` in the
incorporated `ShaderPropertiesModel` declaration. The list is immutable and non-null; no matching
occurrences means an empty list. It has these complete rules:

1. Capture every active logical property whose decoded key begins with exact, case-sensitive
   `texture.`, including exact `texture.noise`, before map insertion or any key normalization.
   `key` and `value` are the exact Java-Properties-unescaped strings from §4.6; no trim, case
   folding, splitting/rejoining, or path normalization changes those fields. Empty values survive.
   A decoding failure is diagnosed by §4.6 and has no invented decoded record.
2. `sourceOrdinal` is the zero-based position in this texture-only list, contiguous and unique.
   Duplicates, invalid values, and unresolved keys all count. Order is active logical source
   order, not canonical executable-spec order. `attribution` is the existing `SourceAttribution`
   with the validated property-file path and positive physical line/column of the key's first
   code point; continuations retain the first-line anchor. Every component is non-null, records
   have structural equality/hash, and construction rejects invalid ordinals/coordinates, a key
   outside the exact prefix, or an unknown disposition. Freeze the list defensively.
3. Classify in Phase 3, in this order: an exact documented custom key or exact `texture.noise`
   with a valid source value is `CUSTOM_SOURCE` or `NOISE_SOURCE`, respectively; the same known
   key with a value rejected by the existing source/path/format rules is `INVALID_VALUE`.
   Any other `texture.*` key is `UNRESOLVED_KEY`, regardless of its value. These are the four
   exhaustive enum variants. Classification is deterministic under the containing schema and
   has no GL dependency. Property-key suffixes are not sampling state under D-P3-67;
   `.0`–`.9` retain only their documented duplicate-discriminator meaning.
4. After capture, Phase 3 folds only `CUSTOM_SOURCE`/`NOISE_SOURCE` occurrences into the existing
   `textures()`/`noise()` projections. The last valid occurrence of the same complete decoded
   key wins; duplicate complete keys receive the existing attributed warning. All occurrences
   remain in the list, including shadowed ones. `INVALID_VALUE` warns without replacing the
   prior valid value; `UNRESOLVED_KEY` retains the existing unsupported-key warning and
   never enters that reducer. Known absent/`.0`/`.9` keys remain distinct, as do differently
   suffixed unknown keys; no grouping of unknown keys by a guessed base binding is permitted.
   Unresolved and invalid occurrences are not duplicated in `unknownProperties`.
5. The list and executable projections are produced atomically by the same front-end transaction.
   Construction rejects internally inconsistent classifications, ordinals, or projections rather
   than accepting caller-supplied reductions; such a producer invariant failure follows the
   existing `UNEXPECTED_INTERNAL` load path, not the pack-authored malformed-line path. The list
   remains readable for the configuration's lifetime after the source lease closes and contains
   no reader, file handle, or executable resource reference. §4.10's canonical declaration
   payload participates in `ConfigurationFingerprint` even when the executable projection is
   unchanged. Existing pack byte limits bound capture; it is never silently truncated.
6. Phase 13 may report a retained unresolved declaration using its disposition and attribution.
   It consumes only the already-typed source specs for texture creation; it may not parse the
   retained strings, reopen Properties bytes, infer suffixes from `.mcmeta`, or reconstruct
   discarded settings. Retention is diagnostic losslessness, not advertised support for unknown
   keys. D-P3-67 closes the former U1 authority request through the documented-mechanism
   correction; no additional typed suffix-state handoff is required.

The sampling boundary remains the existing typed source and `TextureSidecarRef` publication
below. Phase 13 owns `.mcmeta` blur/clamp interpretation and application; Phase 3 neither
decodes sidecar bytes nor synthesizes filter/wrap fields. D-P3-67 leaves the texture records,
unknown-key handling and canonical payload unchanged; D-P3-70 corrects only the external raw target mapping in the current
schema `CURRENT_SCHEMA_VERSION`. Do not reinterpret retained `UNRESOLVED_KEY` records as supported input or add shims.
The correction does not newly ratify any phase-local sidecar defaults or malformed-input policy.

`CustomTextureSpec` is the sealed immutable sum
`PackPath(key,NormalizedPackPath image,Optional<TextureSidecarRef> sidecar)` |
`MinecraftResource(key,String resourceIdentity)` |
`Raw(key,NormalizedPackPath bytes,TextureTarget target,ColorInternalFormat internalFormat,
List<Integer> dimensions,PixelFormat pixelFormat,PixelType pixelType,Optional<TextureSidecarRef> sidecar)`.
A `PackPath.image` final segment has a non-empty stem followed by the exact case-sensitive `.png`
suffix; mixed/uppercase extensions, a bare `.png`, and trailing characters are invalid. Invalid
occurrences warn and do not replace a prior valid complete-key value. This predicate does not open
or decode the image. `resourceIdentity` preserves the exact non-empty `minecraft:` value, including
dynamic/atlas and `_n`/`_s` identity. `TextureTarget` is the unchanged typed enum
`TEXTURE_1D|TEXTURE_2D|TEXTURE_3D|RECTANGLE`; **external** case-sensitive tokens are exactly
`TEXTURE_1D`, `TEXTURE_2D`, `TEXTURE_3D`, `TEXTURE_RECTANGLE`, mapping respectively to those
four variants with 1, 2, 3, 2 positive integer dimensions (6, 7, 8, 7 total source tokens).
Bare `RECTANGLE` is invalid, not an alias. §4.8's complete lexical rejection/disposition and
surviving-spec asset-domain rules are binding here. The other closed format domains and integer
compatibility rule are exactly §4.8's
tables. `TextureSidecarRef` is exactly `record TextureSidecarRef(NormalizedPackPath path)`; `path`
is non-null, structural equality/hash apply, and `path()` returns the associated normalized
`<image-or-bytes>.mcmeta` pack path. Its optional is present exactly when that adjacent regular
file exists in the indexed snapshot; absence means no pack sidecar. Phase 3 snapshots its bytes
under D-P3-69 without interpreting them; a Minecraft resource carries no pack-side sidecar reference.

`NoiseTextureSpec` is `Generated` or
`Override(NormalizedPackPath image,Optional<TextureSidecarRef> sidecar)`; absent `texture.noise`
produces `Generated`, while an override uses the same adjacent-sidecar rule. The custom-spec list
is empty when no custom keys are valid. Otherwise it is immutable and ordered by stage declaration
order above, sampler by unsigned UTF-8 byte order, discriminator absent before `0..9`, then source
kind `PackPath|MinecraftResource|Raw`; duplicate complete keys diagnose and the last valid property
occurrence wins before ordering. No field is null and no consumer infers a default source variant.
Sampler type is not part of `TextureBindingKey`; Phases 4/13 derive it from each program's sampler
declarations when validating shared texture units.

For `ProgramStateModel`, `programs()` is the complete immutable raw map and `ProgramKey` is exactly
`(DimensionKey dimension,String programName)`, ordered by dimension then program name.
`AlphaTestSpec` is `Off|Enabled(AlphaFunction,float reference)` with
`NEVER|LESS|EQUAL|LEQUAL|GREATER|NOTEQUAL|GEQUAL|ALWAYS`; the enabled reference is finite.
`BlendSpec` is `Off|Enabled(BlendFactor sourceColor,BlendFactor destinationColor,
Optional<BlendAlphaFactors> alpha)`; absent alpha factors reuse the color pair, and
`BlendAlphaFactors` is exactly `(BlendFactor source,BlendFactor destination)`. `BlendFactor` is the
15-variant enum declared in §2.2. `FlipBufferKey` is exactly
`(ColorAttachmentKey attachment)`; virtual `deferred_pre` and `composite_pre` are legal outer keys
only for flip. `FlipOverride` is `TRUE|FALSE`, and the `Off` alpha/blend variants differ from
absence. `ViewportScale` is exactly `(float scale,float offsetX,float offsetY)`, each finite in
0…1. One-token input means `(scale,0.0f,0.0f)`; three-token input supplies both offsets; every other
arity is malformed. Scale can be present only for executable exact `DEFERRED` or `COMPOSITE` keys.
`ProgramEnabledExpression` is an immutable opaque engine-issued value: consumers neither construct,
inspect, nor evaluate its private Boolean AST. Raw `ProgramStateModel` has no evaluation method.
`PackConfiguration.evaluateProgramStates(Optional<ProfileName>,DiagnosticReporter)` takes no
candidate state and evaluates exactly the receiver's finalized `options().state()`. A null profile
wrapper or reporter returns `InvalidState(NULL_INPUT)` defensively; no caller-selectable state can
produce `FOREIGN_CATALOG`. Otherwise it returns `Evaluated(states)`. `states.programs` contains
exactly one entry for every key in `SourceCatalog.executablePrograms()`, ordered ascending, whether
or not raw properties or profile disables name it. Qualified/unqualified disables resolve only
against that same set. Source-absent raw property keys warn and are omitted. `explicitFlips`
contains only projected eligible keys and explicit virtual `deferred_pre`/`composite_pre` keys,
ordered likewise; virtual keys never enter `states.programs`. Partial stage sets remain in the
projected set, distinct from Phase 4 compile/link success. Baselines, profile selection, expression
failure, and final enablement are those in §4.8.
Every record rejects null/invalid components and defensively freezes its collections.

The binding `ResourceRequirements` shape is
`(BufferMinima minima, Map<ColorAttachmentKey,ColorAttachmentRequirement> colorAttachments, ShadowRequirements shadow, CenterDepthRequirements centerDepth, Map<ProgramRequirementKey,ProgramRequirements> programs, SmoothingConstants smoothing, WorldRenderConstants world, NoiseRequirement noise)`.
Its nested records are exactly the canonical closed §4.7 declarations, incorporated here with their component order and types: `BufferMinima`, `ColorAttachmentKey`, `ColorAttachmentRequirement`, `ProgramRequirementKey`, `ProgramRequirements`, `ShadowRequirements`, `CenterDepthRequirements`, `SmoothingConstants`, `WorldRenderConstants(float sunPathRotation,float ambientOcclusionLevel)`, and `NoiseRequirement`. D-P3-66 removes the former world supersampling component; no alias or inert resource leaf remains.
`VertexAttribute` is exactly `MC_ENTITY|MC_MID_TEX_COORD|AT_TANGENT`, and
`VertexRequirements` is exactly `(Set<VertexAttribute> attributes)` with a non-null immutable set
iterating in enum order. `ShadowTextureKey` is exactly `DEPTH_0|DEPTH_1|COLOR_0|COLOR_1`;
D-P3-66 retains `superSamplingLevel` only as an ordinary recognized const option and pack source
value. It is not a resource directive, has no engine-positive-level constraint or engine default
in this record graph, and cannot drive P5 sizing or P7 allocation/traversal/final resolve.
The declared source default, allowed values, chosen option value and safe rewrite remain
fingerprinted through source/options. P2 reports those values only when actually discovered.
`ShadowDepthKey` is exactly `DEPTH_0|DEPTH_1`; both iterate in declaration order.
`Vec4f` is exactly `(float red,float green,float blue,float alpha)`, with every component finite.
`ColorInternalFormat` is the closed enum, in order:
`R8|RG8|RGB8|RGBA8|R8_SNORM|RG8_SNORM|RGB8_SNORM|RGBA8_SNORM|R16|RG16|RGB16|RGBA16|`
`R16_SNORM|RG16_SNORM|RGB16_SNORM|RGBA16_SNORM|R16F|RG16F|RGB16F|RGBA16F|`
`R32F|RG32F|RGB32F|RGBA32F|R32I|RG32I|RGB32I|RGBA32I|R32UI|RG32UI|RGB32UI|RGBA32UI|`
`R3_G3_B2|RGB5_A1|RGB10_A2|R11F_G11F_B10F|RGB9_E5`. Records have structural equality/hash.
`ColorAttachmentFormat` is the sealed sum `DefaultRgba|Explicit(ColorInternalFormat format)`.
`DefaultRgba` denotes the unsized/plain-RGBA baseline and has no payload; `Explicit.format` is
non-null and contains one of the 37 values above, so explicit `RGBA8` is not the default.
`DrawRouting` is sealed as `AllUsed` or `Explicit(List<DrawSlot> slots)`; `DrawSlot` is sealed as
`Attachment(ColorAttachmentKey target)` or payload-free `None`. Explicit slot lists are non-empty
and preserve character order and duplicates. Program maps order by ascending dimension then name,
attachment maps/sets by index, and enum sets by enum order. Published resolutions and instance
counts are positive integers; routing attachment indices are 0…7 at v0.1;
ambient occlusion and viewport scale/offset values are 0…1. A present
`ShadowRequirements.fov` selects perspective projection and is finite degrees strictly between 0
and 180; either endpoint is invalid. All other published floats are finite and every resource
constructor canonicalizes either signed zero to `+0.0f`. Smoothing values are
ticks. These are the complete consumer-visible scalar bounds and units; directive spellings and
aliases in §3.3 are producer grammar, not downstream contract.

The complete absent-directive baseline is: all minima zero; attachment and program maps and all
feature sets empty; center depth and noise disabled; noise resolution 256; shadow resolution 1024,
`fov=Optional.empty()` (orthographic projection), distance 160, distance-render multiplier -1, and
interval 2;
smoothing half-lives 600, 200, 10, and 1 ticks; and world constants 0-degree sun-path rotation,
ambient-occlusion level 1. When an attachment entry is first created,
its baseline is `new ColorAttachmentFormat.DefaultRgba()`, clear enabled, and
`clearColorOverride=Optional.empty()`. With clear enabled, Phase 5 resolves clear-color absence by
index: colortex0 uses the current fog color, colortex1 uses solid `Vec4f(1,1,1,1)`, and every other
index uses transparent black `Vec4f(0,0,0,0)`. An explicit `Optional.of(Vec4f(...))` override wins
over that index baseline, including explicit transparent black; `clear=false` suppresses the clear
operation without erasing the override data. Every sized format directive stores
`new ColorAttachmentFormat.Explicit(parsedFormat)`. Every active `gdepth` declaration mandates
`colorAttachments[ColorAttachmentKey(1)].format().equals(new ColorAttachmentFormat.Explicit(ColorInternalFormat.RGBA32F))`.
An explicit colortex1 format other than `RGBA32F` is diagnosed as a conflict and cannot override
that result.
When a program entry is first created, its routing is `DrawRouting.AllUsed`, its sets are empty,
instance count is 1, and geometry is `Optional.empty()`. Explicit `DRAWBUFFERS:N` is
`DrawRouting.Explicit(List.of(new DrawSlot.None()))`; mixed/repeated `N` forms preserve every slot
and remain distinct from the absent baseline.

**Direct per-program projections (D-P3-60).** Phase 4 obtains these values from the same
successfully loaded `PackConfiguration` used for source planning, materialization, and evaluated
program state. The existing record accessors are the complete acquisition surface:
`PackConfiguration.resources()` → `ResourceRequirements.programs()` →
`ProgramRequirements.mipmappedAfterPass()` and `ProgramRequirements.vertices()` →
`VertexRequirements.attributes()`. These are public Phase-3-owned values, not new
`PackConfiguration` components, consumer-specific copies, or Phase 5/10 services.

1. Select the effective `DimensionConfiguration` by the existing dimension rules below. For
   each source program selected from that configuration's `SourceCatalog.executablePrograms()`,
   look up `ProgramRequirementKey(dimension, programName)` using exactly the corresponding
   `ProgramKey.dimension()` and `programName()`. `SourceKey` roots join by that same pair:
   stage is not a resource-key component. An absent world entry selects the base dimension key;
   an override never merges or borrows base requirements, and a disabled dimension selects no
   source program. Preserve exact case-sensitive names and structural key equality; never use a
   registry ordinal, source-path string, display name, or consumer-local alias as identity.
2. The resource map remains sparse and unchanged. A present entry supplies its exact frozen
   values; an absent entry means no recorded requirements, so the two projections have the
   existing empty-set baseline (no mipmap request and `VertexRequirements` with no attributes).
   Do not insert a synthetic entry, densify the map, or alter its fingerprint for a read. Absence
   of a resource entry is not absence of shader source or evidence of compile/link success:
   `executablePrograms()` and Phase 4's build outcomes retain those separate responsibilities.
   Virtual-pre slots remain flip-only, never executable sources with inferred attributes or
   mipmaps. Phase 3 does not resolve fallback chains or relabel an ancestor's requirements as a
   requested slot; Phase 4 owns selection/inheritance of the entire effective configuration.
3. `mipmappedAfterPass()` returns the non-null immutable `Set<ColorAttachmentKey>` already
   supplied to Phase 5, in ascending attachment-index order. Phase 4 may represent that exact
   membership in its registry's composite-mipmap state; it neither adds targets nor narrows the
   `{DEFERRED, COMPOSITE, FINAL}` producer applicability to composite alone. Phase 5 retains
   pass-mipmap consumption and buffer execution ownership; this grant does not move GL work
   into Phase 3.
4. `vertices()` returns the same immutable `VertexRequirements` value already supplied for
   Phase 10 vertex layout. Its attributes retain the closed enum and declaration order above,
   populated only by active `.vsh` declarations. Phase 4 uses those opt-ins to conditionally bind
   declared fixed attributes before link; this is not post-link GL liveness and grants no new
   attribute, location, or vertex-population policy. Phase 10 retains vertex-layout/population
   ownership. Empty attributes require no declared extended-attribute bind.
5. Both reads share one atomic load's finalized-option analysis and remain valid after the pack
   lease closes or a later load publishes a replacement. Neither read reparses directives,
   inspects final GLSL to rediscover opt-ins, uses `DeclaredUniformCatalog` as an attribute
   parser, or waits for Phase 5/10 output. Phase 3 imports no registry/buffer/vertex consumer
   type. Existing canonical key/collection/value equality, schema checks, and configuration/
   materialization fingerprint retention rules apply unchanged; consumer access is not a new
   identity domain.

This §5 text is the sole binding consumer contract for these aggregates. For `ResourceRequirements`,
Phase 5 consumes only attachment/pass records created by eligible exact program families:
`colortexNClear`/`colortexNClearColor` are `{DEFERRED, COMPOSITE}`, and
`colortexNMipmapEnabled` is `{DEFERRED, COMPOSITE, FINAL}`; wrong-family occurrences never reach
the map, and consumers never infer applicability from source stage. Sections 3.3, 4.7, and 4.8
explain producer parsing and construction without adding consumer-visible shapes, values, defaults,
absence, or ordering semantics. Any change there that would alter a published value or consumer
interpretation is an interface change and must update this §5 contract in the same edit.
`PackConfiguration.dimensions()` is an immutable map ordered with the base key first and world keys
in ascending numeric order. `DimensionKey` accepts only the empty base form or a present ID in
`[-128,128]`. The base entry has `mode=BASE`, an empty `baseDimension`, and its complete base
roots. A present non-empty world folder has `mode=OVERRIDE`, a base link, and only its own roots;
an empty folder has `mode=DISABLED` and no roots. An absent world key means no folder and selects
the base entry; no consumer merges an override with base sources.
Structural load success requires a usable source configuration in any base or explicit override
after mandatory decode/include validation, not a usable base specifically. The empty base is
still published for a dimension-only pack; selecting an absent world key selects that empty
base, never the other world's override. Empty disabled folders are not usable shader sources.

`SourceCatalog.sources()` is the immutable ascending-`SourceId.path` list containing every indexed
physical source document exactly once, not binary-only files or retained sidecar read-failure
markers. `source(id)` is its absence-aware lookup. `roots()` is the immutable
dimension/path/program/stage-ordered list of compile-root `SourceKey`s; only roots carry contextual
dimension, program, and stage fields. `executablePrograms()` is the immutable distinct ascending
projection of each non-virtual root's dimension and program name. Any `.vsh`, `.fsh`, or `.gsh`
root qualifies, multiple stages collapse, partial stage sets remain, and include-only/source-absent
names do not qualify. `includeEdges()` uses `SourceId` endpoints, retains missing targets, and has
one edge per physical include location; shared files are never contextually duplicated.
`materializer()` is the only materialization path. It accepts only a published root `SourceKey` and
uses exactly this configuration's finalized `options().state()`; no option-state argument or
same-catalog updated-state runtime path exists. `MaterializedSource` carries exact text, source map,
uniform catalog, language, closed geometry form, diagnostics, and fingerprint; published values remain valid after the input lease
closes.

`OptionDefinition.tooltip()` retains the decoded terminal `!`. Phase 12 splits on `. `, marks a
line ending in `!` warning/red, and removes only that marker for display; the existing
`OptionDefinition` component and its type remain unchanged.

`ProgramRequirements.legacyGeometry` is empty only for true pair absence and otherwise contains
one validated `LegacyGeometryConfig` with one provenance-only `LegacyGeometrySite`. The §4.5
native-preserving source algebra, its language/line/macro/attribution invariants and complete
failure rules are incorporated **in full** into this binding contract. P4 calls
`materialize(root,contribution,new GeometrySourceRequest.PreserveNative(expected))` for a recognized
pair, or `new GeometrySourceRequest.None()` otherwise. It consumes `MaterializedSource.geometry()`
as `None`, `CoreLayout(effective,declarations)`, or `NativeLegacy(config,effective,declarations)`;
the last retains the native API triple separate from source-overridden effective values. It uses
`language()` for version/profile/active extension metadata and `sourceMap()` for exact final text.
No caller supplies option state, chooses source rewrites, derives geometry from a shader rescan,
or treats a missing/malformed `.gsh` as a vertex/fragment-only success. Source-local unavailable
results propagate into P4's complete binding fallback. The singular Phase 6 contribution and
complete final uniform-catalog merge are unchanged. Full exact P4 migration is §11.4.


`discover` requires a non-null `shaderpacksDirectory` and `DiagnosticReporter`. A resolvable request
derives its directory identity by `toAbsolutePath().normalize().toRealPath()`, following a symlink
in the supplied directory path, and requires a readable directory. The real provider `Path` is the
identity; aliases resolving to it share a key and provider-distinct paths do not.

A nonexistent, non-directory, unreadable, unresolvable, or security-denied path returns an immutable
invalid discovery result rather than throwing. It contains exactly fresh `Off` then `Internal`
sentinel candidates, no filesystem candidates, and an attributed diagnostic. Its fresh generation
is an engine-issued, front-end-domain-authenticated **invalid generation** with no directory key;
its sentinel IDs authenticate that generation and have no filesystem reference. Their target
acquisition result is `NON_FILESYSTEM`. A `Filesystem` load of the same bad request remains
`Failed(INVALID_REQUEST)`.

Each discovery completion is one linearization point. A successful completion publishes and
supersedes the prior latest snapshot for its real-directory key, marks that key most recent, then
evicts least-recently-successfully-completed keys until the table has at most
`maxRetainedDirectorySnapshots` entries. A result may contain at most `maxCandidatesPerSnapshot`
candidates including sentinels and at most `maxSnapshotBytes` under §4.1's canonical accounting.
If full enumeration would exceed either limit, the keyed result is exactly fresh `Off`, fresh
`Internal`, and the fixed `ERROR`/`CHAT` diagnostic
`("schmaloogium.error.pack.discovery_limit", List.of(), "", "schmaloogium.pack")`.

The §4.10 closed diagnostic-argument algebra, exact dynamic type tags, non-null field rules, and
record/list framing are binding parts of this discovery contract. Counting includes every encoded
candidate diagnostic and result diagnostic. Below-limit and exactly-at-limit snapshots publish
normally; the first byte above the limit publishes the fixed overflow snapshot, whose exact encoded
size is the minimum permitted `maxSnapshotBytes`.

Eviction immediately supersedes the evicted generation and candidate IDs:
`resolveFilesystemCandidate` returns `InvalidSnapshot`, `packOptionsTarget` returns
`SUPERSEDED_GENERATION`, and a load using such an ID returns `Failed(INVALID_SELECTION)` before
input or persistence I/O. An already acquired `PackOptionsTarget` remains valid under §4.3.
A durable reference regains resolution eligibility only through a fresh successful discovery of
that directory. Concurrent publication and eviction follow completion order.

An invalid completion publishes no directory-keyed snapshot, supersedes none, and does not refresh
recency, including when the same spelling formerly resolved to a valid directory. Thus
invalid-before-valid leaves no keyed snapshot; valid-before-invalid leaves the prior snapshot
current only if it was not independently evicted. `resolveFilesystemCandidate` first authenticates
the reference and generation. Only the calling instance's latest retained successful generation
for that real directory can return `Resolved`, `Missing`, `Ambiguous`, or `KindChanged`; a foreign,
invalid, superseded, or evicted generation returns `InvalidSnapshot`. A retained snapshot may
resolve after its host path disappears, but a later filesystem load re-resolves the directory and
returns `Failed(INVALID_REQUEST)` without I/O. Tokens retain identity equality/hash and remain
package-private, nonconstructible, and nonserializable.

`PackSelection` is closed: `Off`, `Internal`, or
`Filesystem(PackCandidateId candidate)`, where the opaque candidate ID must have come from the
current deterministic discovery result for `shaderpacksDirectory`; stale/unknown IDs fail as
`INVALID_SELECTION`. `load` validates `selection` first. `Off` immediately returns
`PackLoadResult.Off` without accessing or validating any other request field. `Internal` ignores
`shaderpacksDirectory` and `persistenceFiles` and requires `internalPackSource`; `Filesystem`
validates the host directory and discovery generation, requires a bundle-issued
`persistenceFiles` from the same authentication domain whose canonical shaderpacks root equals the
request directory, and never accesses `internalPackSource`. A foreign access receiver or root
mismatch returns `Failed(INVALID_REQUEST)` before file I/O. Validation first rejects a null request,
null/malformed selection shape, or unauthenticated selection token; `Off` then returns immediately.
Before any external I/O, every non-`Off` request validates `diagnostics`, identity, capabilities,
engine options, companion option macros, and renderer features, followed by its selection-dependent
fields. A null `companionOptionMacros` returns `Failed(INVALID_REQUEST)` before pack, persistence,
internal-provider, or jcpp work; no default is synthesized. A null reporter
is the special `INVALID_REQUEST` delivery branch below. Other null or structurally invalid required
data likewise returns `Failed(INVALID_REQUEST)` rather than throwing.
`internalOptions` is validated under D-P3-65: null Optional, a nonempty Filesystem value, or foreign
Internal token is pre-I/O `INVALID_REQUEST`; exact identity and fresh-catalog value checks happen
after provider acquisition/discovery and before jcpp, also `INVALID_REQUEST`. `Off` ignores it.

`RuntimeIdentityData` is the immutable `(mcMajor, mcMinor, mcPatch, engineEdition, engineVersion,
osFamily, perPackIdentityOverrides)` tuple. The MC components must be exactly `(1,12,2)`.
`engineEdition` must match the 1–64-character ASCII grammar in §4.8 and is canonicalized there for
comparison. `engineVersion` obeys §4.4's bounded canonical decimal grammar and exact macro
projection. `osFamily` is the closed macro OS family with `OTHER`; overrides are the validated
add/suppress/force map described in §4.4, and absent overrides become an empty map. Every invalid
value fails before pack or persistence I/O.
`RendererFeatureData` retains its independent normal/specular companion-atlas availability fields
and fingerprint participation, but is not the companion option-macro producer or a second gate.
The required `CompanionOptionMacros` pair is authoritative for emission. `EngineOptionData`
is the immutable canonical-order map valid exactly under §5.1's eight-known-setting and
unknown-safe invariant. Global reads classify logical occurrences only after request validation:
typed-invalid known and unsafe unknown occurrences are omitted with warnings, unknown-safe entries
round-trip with a warning, and the last valid duplicate wins. Absent or failed input returns the
valid baseline.

`PackInputLimits(maxEntries, maxTotalBytes, maxPathLength, maxNestingDepth)` contains strictly
positive Phase-3-owned bounds shared by folder, archive, and internal inputs. Phase 3 constructs
the limits from its configured finite policy and passes them to `snapshot`; Phase 7 must stop
before exceeding any bound, and Phase 3 independently recounts and rejects an over-limit snapshot.
`InternalPackSource.identity()` and `snapshot(limits)` are called only for `Internal`; provider
exceptions, unstable identity, malformed manifests, and limit violations become
`INTERNAL_SOURCE_INVALID`, never an unchecked exception.

`PackLoadFailure` contains one closed `code` and one non-null `primaryDiagnostic`; it never exposes
a provider exception, partial configuration, display string, or synthetic diagnostic identifier.
Every code uses the exact common fields `severity=ERROR`, `channel=CHAT`, `args=List.of()`,
`detail=""`, and `logChannel="schmaloogium.pack"`. The empty detail is a zero-byte bound and
redacts every path, provider exception, library message, stack trace, and cause-specific value.
Only `messageKey` varies:

| Code | Exact `messageKey` |
|---|---|
| `INVALID_REQUEST` | `schmaloogium.error.pack.invalid_request` |
| `INVALID_SELECTION` | `schmaloogium.error.pack.invalid_selection` |
| `INPUT_UNSAFE` | `schmaloogium.error.pack.input_unsafe` |
| `INPUT_LIMIT_EXCEEDED` | `schmaloogium.error.pack.input_limit_exceeded` |
| `INPUT_UNREADABLE` | `schmaloogium.error.pack.input_unreadable` |
| `INTERNAL_SOURCE_INVALID` | `schmaloogium.error.pack.internal_source_invalid` |
| `STRUCTURALLY_UNUSABLE` | `schmaloogium.error.pack.structurally_unusable` |
| `UNEXPECTED_INTERNAL` | `schmaloogium.error.pack.unexpected_internal` |

No cause variant changes that code-selected payload. If `request.diagnostics()` is non-null, the
exact same `EngineDiagnostic` instance is delivered exactly once before the single `Failed` result.
If it is null, Phase 3 returns `Failed(INVALID_REQUEST)` with the table-defined primary diagnostic,
performs no callback or other external I/O, and does not throw. Supplementary diagnostics emitted
before a terminal failure remain reporter events and are not embedded in it.

The cause mapping is exhaustive:

| Code | Exact cause class |
|---|---|
| `INVALID_REQUEST` | null request; null/malformed selection shape such as `Filesystem(null)`; any missing/invalid selection-dependent request field, domain/root mismatch, runtime identity, capability, option, companion option macros, feature, reporter, or persistence receiver |
| `INVALID_SELECTION` | a non-null filesystem ID that is foreign, unknown, stale, superseded, evicted, or names a sentinel; an authenticated candidate status is classified by its specific input code instead |
| `INPUT_UNSAFE` | an `UNSAFE` candidate or selected filesystem input with an absolute, traversal, NUL, drive-qualified, symlink-following, collision, or containment-escaping path/root condition |
| `INPUT_LIMIT_EXCEEDED` | a `LIMIT_EXCEEDED` candidate or selected filesystem/archive input exceeding any configured entry, byte, path, nesting, source, graph, line, token, macro, or diagnostic bound |
| `INPUT_UNREADABLE` | an `UNREADABLE` candidate or, after valid request/selection and absent a higher-specificity unsafe/limit cause, a selected filesystem child/byte stream that cannot be opened/read or has a corrupt archive container; filesystem-provider I/O and corrupt archive are this same code, except §4.1 D-P3-69's positively proven optional owned-sidecar-only per-file marker, retained for P13 recovery |
| `INTERNAL_SOURCE_INVALID` | `InternalPackSource` throws, changes identity, returns null/malformed/duplicate/colliding/noncanonical data, or violates any supplied/recounted limit |
| `STRUCTURALLY_UNUSABLE` | a safely read, bounded, otherwise valid input has no effective `shaders/` root or no shader source configuration in either the base or any explicit `OVERRIDE` surviving mandatory decode/include validation; empty `DISABLED` folders alone do not establish usability |
| `UNEXPECTED_INTERNAL` | an unexpected implementation/library `RuntimeException` escapes a validated stage and is not attributable to filesystem input or the internal provider |

Validation order is null request/selection shape (`INVALID_REQUEST`), token authentication
(`INVALID_SELECTION`), `Off` short-circuit, then non-`Off` in-memory request validity
(`INVALID_REQUEST`), all before external I/O. `Internal` then maps every provider-boundary defect
to `INTERNAL_SOURCE_INVALID`. Filesystem processing uses `INPUT_UNSAFE` before
`INPUT_LIMIT_EXCEEDED`, then `INPUT_UNREADABLE`, then `STRUCTURALLY_UNUSABLE`; canonical path
order chooses the primary cause within one class. `UNEXPECTED_INTERNAL` is the last boundary
catch. The first reached class returns one failure, closes all acquired resources, publishes no
partial configuration, and leaves shaders off.
Per-file read markers may be deferred solely to establish D-P3-69's sidecar-only classification;
they are not earlier public success. Fatal safety/bounds/index/container/prerequisite failures
remain fatal, and any nonqualifying read marker returns this same ordered failure result.

`ConfigurationFingerprint.value()` and `IdMappingFileFingerprint.value()` are non-empty canonical
opaque strings, not provider exceptions, paths, or partial configuration objects.
Consumers must not re-open the pack, rescan directives, reinterpret properties, or bypass the
materializer. A reload publishes a new configuration. A consumer may retain derived state only
when both `schemaVersion` and configuration fingerprint equal the values used to derive it; state
derived from materialized text must also match that source's materialization fingerprint.

### 5.1.1 Source-free inspection and archive provenance (D-P3-62)

Phase 2 is an explicit consumer of `PackFrontEnds.create`, the existing discovery/reference/
safe-persistence/load contracts, and the following additional `engine.pack` operation declared
in §2.2. No separate `PackSource` constructor or parser-private access is needed.

```java
public sealed interface PackInspectionResult {
    record Off() implements PackInspectionResult {}
    record Failed(PackLoadFailure failure, List<DecisionDiagnostic> diagnostics)
        implements PackInspectionResult {}
    record Inspected(PackConfiguration configuration, PackDecisionSnapshot snapshot,
        Optional<String> archiveSha512) implements PackInspectionResult {}
}
public record PackDecisionSnapshot(int projectionVersion, int schemaVersion,
    ConfigurationFingerprint configurationFingerprint, List<DecisionSource> sources,
    Map<String, DecisionValue> sections, List<DecisionDiagnostic> diagnostics) {}
public record DecisionSource(NormalizedPackPath path, int logicalLineCount, String sha256) {}
public record DecisionDiagnostic(String code, DiagnosticSeverity severity, UserChannel channel,
    Optional<SourceAttribution> location) {}
public sealed interface DecisionValue {
    record Bool(boolean value) implements DecisionValue {}
    record IntegerValue(long value) implements DecisionValue {}
    record FloatBits(int bits) implements DecisionValue {}
    record Token(String value) implements DecisionValue {}
    record TextHash(String sha256) implements DecisionValue {}
    record Absent() implements DecisionValue {}
    record Sequence(List<DecisionValue> values) implements DecisionValue {}
    record Fields(Map<String, DecisionValue> values) implements DecisionValue {}
}
```

`frontEnd.inspect(request)` performs **one** existing atomic load, with exactly `load`'s
validation order, bounded input, persisted-state application, same-build macro/materialization
semantics, diagnostics, failure mapping and handle closure. `Off` performs no other work.
`Failed` contains no partial configuration; its diagnostic list is the ordered sanitized
projection of this call's emitted events, including the primary failure exactly once.
`Inspected.configuration` is the single successful load result, retained only by the caller
for downstream compilation. Neither the snapshot nor a golden writer retains it.
Inspection never invokes GL, emits saved-source dumps, resolves P4 fallback, or publishes a
runtime configuration. Null/invalid requests use existing `INVALID_REQUEST`, not exceptions.

For an archive candidate, the bounded immutable container bytes consumed by that very load
are also SHA-512 hashed; `archiveSha512` is their 128-lowercase-hex digest, **not** a second
pathname read, extracted-tree digest or configuration hash. Container bytes obey the same
total-byte bound before retention and are discarded after decode; no open archive escapes.
Folder/internal inspection has `Optional.empty()` rather than an invented archive hash.
All result lists/maps are deep immutable. `projectionVersion=1`; `schemaVersion` and
`configurationFingerprint` equal the returned configuration. Equal bytes and semantic request
inputs produce equal snapshots independent of bundle credentials or host path.

The complete projection is a typed, recursive **allowlist**, not `toString` or reflection.
`sections` has exactly `pack`, `dimensions`, `options`, `properties`, `programStates`,
`resources`, `macros`, `idMappings`, and `assets`. These project the corresponding published configuration
record graphs, except `programStates` uses `evaluateProgramStates(Optional.empty(),reporter)`
on finalized state, not raw enabled-expression execution by P2. Its closed Evaluated/InvalidState
outcome is retained as a variant. Evaluation diagnostics join the inspection diagnostics in
emission order. The `options` projection includes catalog definitions, complete finalized values,
profiles/constraints/disabled programs, main/named screens, sliders and the entire locale catalog;
`properties` includes engine flags, compatibility rules, texture declarations/specs/noise,
custom-expression declarations and unknown-property metadata. No node is silently dropped.
The opaque `ProgramEnabledExpression` within raw properties contributes only `TextHash` of
its exact captured expression bytes, produced by its owning P3 parser; no AST accessor is
granted to P2. Its actual finalized result is already in `programStates`.

For every named public record in those graphs: `Fields` contains its declaration-order
component names plus `$type` as its declared record/variant name; enums use their exact
constant name as `Token`; booleans, integral numbers and binary32 floats use their typed
variants; optional absence is `Absent`; lists/sets preserve the owner's order. Maps become
`Sequence` of `Fields(key,value)` entries in the owner's canonical order, avoiding caller keys
as field names. Sealed variants retain `$type`. Catalog/state capabilities project only their
published definitions/values, never issuer identity; materializers, callbacks and other
operational capabilities are not part of any graph here. **All String leaves use `TextHash`**
(SHA-256 of exact UTF-8 bytes), except schema-owned enum/record names, validated macro names,
validated option/program/uniform identifiers, `NormalizedPackPath.canonicalString`, and the
standard numeric/Boolean macro replacements whose exact grammar is already bound in §5.1.
Opaque/custom macro replacements, option text/ranges, tooltip/lang text, raw expressions,
unknown keys/values and texture declaration text are hashed even when they look harmless.
Hashing preserves ordering/equality/range identity, never grants text redistribution.
The locale catalog uses the same map-entry sequence and record-component projection, preserving
all locales, nine map families, empty strings and missing versus present-empty locale. Locale
keys are `TextHash`, not a new string allowlist exception; translation values remain `TextHash`
even when empty. No selected locale or fallback-rendered labels replace this source-free graph.
The `assets` section is exactly the generic `DecisionValue.Sequence` projection of
`configuration.assets().manifest()` in canonical path order. Metadata records use the existing
`Fields` component names and `$type`; availability is its exact enum token, byte count uses
IntegerValue/Absent, path uses the existing canonical-path allowlist and `sha256` follows the
ordinary String-leaf `TextHash`/Absent rule (hashing the digest string, not emitting binary).
No byte buffer, cursor, `Acquired.bytes`, provider or capability identity enters this graph.
Inspection is current-schema only; older schema snapshots/configurations are rejected before
enrichment or serialization, never upgraded by synthesizing an empty `assets` section.

`sources` is canonical-path ordered, one row per indexed physical source; line count is the
existing logical-line count, and SHA-256 hashes the original bounded source bytes retained by
the load, not joined logical lines. No original lines, transformed GLSL, source-map excerpts,
driver logs, diagnostic args/detail, arbitrary strings or host paths can inhabit the projection.
`DecisionDiagnostic.code` is the engine-authored message key, severity/channel copy P1 exactly,
and location copies already-structured source attribution when available, otherwise empty.
P3 captures attribution while emitting its diagnostic; no consumer parses a message or `args`
to recover coordinates. All SHA-256 fields are exactly 64 lower-case hex digits.

The `idMappings` tree includes the complete D-P3-73 graph in both ordered rule lists:
`MetadataConstraint.alternatives` and `IntegerRange` endpoints use Fields/Sequence/IntegerValue;
`Literal.value` and `PropertyPredicate.propertyName` retain the existing String-leaf TextHash
rule, while `IntegerInterval.range` is the typed interval graph. `selectorToken` and origin
strings likewise keep their existing hashed treatment. This adds no string allowlist exception,
range-member expansion, new tree, health row or projection-version bump.

Phase 2 feeds folder/zip fixtures through fresh discovery of its established external cache
staging directory, resolves the canonical reference, acquires bundle-owned safe persistence
access for isolated fixture/game roots, and supplies every non-Off request input (including
explicit companion preferences). Internal fixtures use the existing bounded Internal source.
For a registry archive it compares the same-read `archiveSha512` to its independently verified
`PackFixture` digest before any golden verdict; missing/mismatch fails provenance. Registry
mode/licence/id/version remain P2-owned facts, joined only after equality. P2 never equates
`PackIdentity.contentHashes` or a configuration fingerprint with the archive digest.

P4 enrichment is a separate same-build adapter input, not a mutation of this snapshot:
P2 passes precisely `Inspected.configuration` to the P4 build request and pairs the resulting
candidate view's `resolutions()` with this snapshot while it owns that candidate.
The adapter retains that exact request/configuration association through the synchronous
result; there is no new fingerprint accessor on `ProgramRegistryView` and no join against
an independently read published registry. `Ready(candidate)` is closed in `finally` after
copying; `ShadersOff(failure)` yields no fabricated rows or runtime publication.
Each row retains independent `sourcePresent`, P4 D-P4-31's required `ownBuild`
(`NOT_APPLICABLE|NO_SOURCE|DISABLED|SUCCEEDED|FAILED`), and effective
`SOURCED|CHAIN|ABSENT|FAILED`. `CHAIN.from` and sanitized failure classification retain
P4's absence rules; intentional disablement is never inferred from CHAIN, and actual own
failure is never hidden by CHAIN. This D-P3-71 receipt changes no P3 snapshot tree or schema:
the complete handle-free P4 row is a separate enrichment input, not a derived P3 field.
A mismatched build/configuration identity,
unavailable P4 result or absent enrichment cannot produce a complete golden. Runtime manifests
use the accepted registry view instead. P2 writes only this snapshot plus the handle-free
P4 projection and runner-owned provenance; source-bearing configuration and candidates never
enter `GoldenDocument`. Partial synthetic goldens remain explicitly partial; complete matrix
goldens require P3/P4 plus the P5 pure resource projection below; no GL or capability verdict
is invented by the front end.

P3's `resources` section is parsed requirements, **not** the final `[sizing].capabilityGate`.
P2 acquires P5 `BufferArchitectures.create()` and calls only
`plan(BufferPlanRequest(configuration,registryView,registryView.fingerprint(),capabilities,runtime))`
with that exact inspected configuration, its P4 candidate view and explicit recorded runtime
inputs. P5 owns complete resource/capability decisions. `Valid(plan,Available)` or
`Invalid(failure,Available)` may provide a complete `OK|SHORTFALL` projection; `Unavailable`
is incomplete, never an inferred pass. No buffer creation, framebuffer/main-depth provider,
publication, physical bind, or live GL context is required by this pure plan. P2 keeps P3's
parsed requirement fields distinct from P5 resolved fields; complete goldens require all
mandatory sections and may record an expected shortfall, not a fictional successful allocation.

### 5.2 Consumed Phase 1 contracts

| Phase 1 §5 contract | Use here |
|---|---|
| `:engine` layout, package rules, C-1…C-4 seam | all Phase 3 production code and tests |
| `GLCapabilityProfile` | entire GL-side input to macro construction: GL/GLSL version, vendor, renderer, extensions |
| `Log`, `Logs`, fixed channels | `schmaloogium.pack`, `.preprocess`, `.config` |
| `EngineDiagnostic` / `DiagnosticReporter` | loader-neutral warnings and pack-level failures |
| `schmaloogium.debug.saveSources` | opt-in processed-source dump |
| SPDX/`THIRD-PARTY.md` mechanism | jcpp notice and any future LGPL-incorporation accounting |

No GL service or handle is consumed.
Phase 3 consumes no Phase 1 `:conformance` module or extension. P3-C20 exposes §5.1.1's
source-free snapshot, synthetic fixtures and capability vectors through §8.2; Phase 2 owns
golden serialization, the adapter, CI job and matrix acquisition/verdicts.

### 5.3 Interface/version discipline

`PackFrontEnd.CURRENT_SCHEMA_VERSION` is `23`, and every configuration produced by this revision
publishes that value. A consumer supports exactly `schemaVersion == CURRENT_SCHEMA_VERSION`; every
other value is rejected before derived state is created or retained. The schema is separate from
the content fingerprint. Any record-component change, changed component meaning/default, or removal
requires the next version and producer/consumer compatibility tests.

Historical schema chronology (the §0 addenda record their amendment dates; none of the older
numeric values below is an active acceptance gate):
The §0.55 R1 amendment required schema 15 for the nested companion macro state. The §0.56 U1
amendment now adds `ShaderPropertiesModel.textureDeclarations`, its closed disposition meaning,
and its canonical fingerprint payload, requiring schema 16. Schema-15 and schema-16 consumers
reject the opposite producer version; older schemas are not upgraded by fabricating an empty
declaration list or inferring missing companion state. The integration cutover requires every
consumer to adopt the current schema explicitly, not merely relabel older data.
The §0.57 grant adds Phase 4 as an explicit reader of two existing per-program components.
It adds no record component or meaning/default and changes no canonical fingerprint payload, so
it does not increment schema 16. It nevertheless changes binding §5's consumer surface and requires
fresh whole-document verification; it does not waive any consumer's outstanding schema adoption.
The §0.58 IR-24 amendment changes old-light global defaults/domains and the option-macro
component meaning, requiring schema 17. Schema 16 and 17 reject one another before deriving
state; no default injection upgrades a configuration. The §0.59 locale component replacement,
effective old-light meaning, session-only load rebinding and removal of engine supersampling
required schema 18 together, including matching `IdMappingInput.schemaVersion`, although its
grammar did not change. Schema17 and earlier could not be upgraded by inventing locale maps
or reinterpreting old macros. Inspection remains
`projectionVersion=1`: its generic graph encoding is unchanged; configuration schema gates the
changed component graph, not a projection-version compatibility escape.
The §0.60 U1 authority adoption removes an unspecified future requirement without changing any
published component, parser behavior, default, disposition or fingerprint payload. It retained
schema 18 at that amendment; §5's revised authority/ownership boundary required fresh review.
The §0.61 native grant now requires **schema 19** for `LegacyGeometrySite`'s included/macro
provenance meaning, `SourceMap.mappings`, `MaterializedSource.language/geometry`, closed
request/result replacement and complete route-distinct materialization identity. It deletes the
incomplete translator instead of admitting old success data. `IdMappingInput.schemaVersion`
equals 19; its grammar, all option/locale/session/U1/texture wire contracts and inspection
`projectionVersion=1` stay unchanged. Schema 18/19 reject one another before deriving state.
Inspection's generic record codec projects the renamed nested resource site with schema19,
but materialized GLSL/token ranges/expansion text do not enter its source-free allowlist.
P4 owns any existing same-request resolution enrichment; this grants no new P4 inspection API.
Historical 2026-09-08 §0.62/D-P3-69 required schema20 for acquisition, including matching nested
ID and inspection schema. Historical 2026-09-07 §0.61/native references above describe schema19.
Historical 2026-09-08 §0.63/D-P3-70 required **schema21** for payload-free
`ScreenProfileEntry()` and incorporated the corrected structural/texture lexical contracts.
Historical §0.64/D-P3-72 required schema22 for BLOCK alternate meaning.
The current §0.65/D-P3-73 cutover requires **schema23** for typed metadata unions and property
intervals; it retains all prior asset/native/option/profile and dual-era semantics without shims.
Every active consumer gates `PackConfiguration`, nested `IdMappingInput`, and
`PackDecisionSnapshot` on exact `schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION`;
nested and containing values must also agree. Reject older, future and mismatched versions
before parsing, deriving, retaining, enriching or serializing state. Never erase an old profile
component, invent a name/selection, or relabel old data to manufacture compatibility.
Materialization uses §4.10's `MaterializedSource-v23` domain and current schema-bound configuration
and uniform-catalog payloads; old materializations and retained derived caches are incompatible.
Missing/null/foreign-load assets remain invalid. No resource epoch or inspection metadata can
reconstruct or relabel a runtime capability. `projectionVersion=1`, all nine source-free trees,
asset field dispatch and metadata/hash/absence meanings remain unchanged; schema equality,
not a codec-version change or shape shim, gates the changed typed selector graph.

Required dated schema23 receipts cover P2 inspection/serialization and P12 selector decoding;
P4 materialization/caches; P5 resources; P6 contribution/catalog consumption; P7 atomic reload,
dimension selection and retained configurations/assets; P9 nested ID parsing; P11 expressions;
P13 typed texture/acquisition; P8/P10 indirect projections; and P14 eventual acceptance.
Each receiver records exact-current rejection before derivation for its owned boundary and
preserves unchanged component semantics. P1 records a changed-§5 reread without a new facade.
These are producer handoffs, not assertions that receipts or fresh reviews are complete.

Round 40's program-state meaning required schema 6; Round 41's program-state and macro-default
changes required schema 7. Round 42 changed binding operations but no component meaning, so retained
schema 7. Round 43 applied D-P3-38's documented interpretation of App F.5's unspecified extension
case and stem grammar to `properties.textures`; that narrowed meaning required schema 8, but is not
claimed as literal Appendix F.5 syntax. Round 44 changes the nested `options` component to sealed,
catalog-bound state and fixes the source-backed evaluator universe exposed through `sources`;
schema 9 is therefore mandatory. Schema-8 consumers reject schema 9.
Round 45 changes the nested source-document/include-edge identity shape from contextual
`SourceKey` to canonical `SourceId`; schema 10 is mandatory, and schema-9 consumers reject it.
Round 46 replaces the nested attachment-format component and its default meaning with the closed
default/plain-versus-explicit algebra; schema 11 is mandatory, and schema-10 consumers reject it.
Round 47 replaces attachment-only routing with positional attachment/none slots and removes the
unsupported destructive texture-suffix meaning; schema 12 is mandatory. Schema-12 consumers reject
schema-11 producer values, and schema-11 consumers reject schema-12 producer values.
Round 48 closes previously undefined `EngineOptionData` validation and logical-occurrence
classification without changing a record component, any eight-setting meaning/default, or the
configuration-fingerprint interpretation; schema 12 therefore remains current.
Round 49 closes `Raw.internalFormat` on the existing `ColorInternalFormat` nominal domain; this
changes the nested texture component type, so schema 13 is mandatory and schema-12/13 consumers
reject the opposite producer version.
Round 50 changes load-failure and runtime operation signatures but no `PackConfiguration` record
component or component meaning/default, so schema 13 remains current.
Round 51 narrows request validity, closes identity/macro projections and compatibility comparison,
and completes failure delivery/classification without changing a `PackConfiguration` component,
meaning, or default; schema 13 therefore remains current.
Round 52 defines previously open edition-accessor and macro-action policies without changing their
record shapes, and adds compatibility status to the fingerprint without changing component meaning.
Its corrected screen resolver changes the established nested `ScreenModel` meaning from an
option-only formula with an unconditional explicit winner to an expanded-slot minimum with configured
columns as a floor; schema 14 is therefore mandatory, and schema-13/14 consumers reject the opposite
producer version.
Round 39 closes the previously named discovery/load, identity, compatibility, texture-stage, and
ID-map value types without adding a `PackConfiguration` component or changing an existing component
meaning or default; schema version remains `5`. The §5 publication contract nevertheless changed
and requires this fresh whole-document review.
Round 38 closes previously named dimension/source/geometry value types and the existing tooltip
string projection at their already stated semantic boundaries; it adds no `PackConfiguration`
record component and changes no schema-5 component meaning or executable default. The exact
closure is covered by the new dimension, source-map, geometry, and terminal-marker compatibility
assertions. The monitored §5 change still requires a fresh review.
This rule may be relaxed only after defining a shape-stable extension mechanism with exact absence
and default semantics. Collection order is deterministic and exposed as immutable insertion order
where pack order matters. Enums intended for forward-compatible storage include `UNKNOWN`, which
is never a silently executable state. Closed executable enums reject unknown values; in particular,
`GeometryInputPrimitive` and `GeometryOutputPrimitive` contain only their declared primitive sets.
`IdMappingInput.schemaVersion` must equal its containing `PackConfiguration.schemaVersion`; Phase 9
rejects a mismatch before parsing mod bytes or building aliases. Every non-current version is
incompatible with the current surface and is never upgraded by inference.
Closing the already-named `NormalizedPackPath` value with its canonical string projection does not
add or reinterpret a `PackConfiguration` component and therefore did not itself increment the
schema. Any future change to that path grammar is still an interface-breaking change. Likewise,
publishing `customExpressions()` does not add or reinterpret a record component: it closes a named
immutable projection of declaration data already retained by `ShaderPropertiesModel`, while the
existing pack bytes already made those declarations configuration-fingerprint inputs. The
custom-expression publication itself did not increment the schema; the value became `13` in Round
49 because that round closed the nested raw-texture component type. Round 52 advanced the value
to `14` for the independently described `ScreenModel` meaning change; §0.55 required `15`
for the companion macro state, and §0.56 requires `16` for lossless texture declarations. The canonical typed-list
encoding in §4.10 makes the existing fingerprint dependency independently executable. Adding the
collection as a new record component or changing its decoded meaning, order, duplicate policy,
attribution, or absence semantics would require the next schema.

### 5.4 Requested changes to the dependency contract

**P1 owner-granted / P3 receiver-adopted, unverified (2026-09-07).** Phase 1
§§4.2.4b/5.1/D-P1-49 grants `org.anarres:jcpp` in pure `:engine` production
`implementation` scope. Before P3 code lands, implementation must select one exact
`jcpp_version`, verify repository/POM/artifact identity and the full runtime closure,
and record licenses/notices and outcomes in `PINS.md` and `THIRD-PARTY.md`.
No range, latest selector, snapshot or inferred vendored version is permitted.

Adopt the entire owner contract: unchanged C-1 scans of compile/runtime closure and bytecode,
no platform-library allowlist bypass, no public jcpp types, and matching compile/headless/client
versions. Ship the verified third-party runtime closure once through `:mod`'s `contain`
mechanism, separately from merged first-party engine classes; preserve LICENSE/NOTICE.
Unverified closure/license identity blocks implementation rather than weakening the seam.
The existing attribution mechanism remains binding. This receiver receipt changes no
configuration payload or current-schema semantics and supplies no dependency/runtime proof.

No new Phase 1 runtime interface is assumed. `RuntimeIdentityData` and `InternalPackSource` are
Phase 3 interfaces supplied by later `:mod` work as plain data.

**R1 grant and remaining dependency gates.** Phase 3 publishes the typed preliminary pair
in §5.1. Phase 7/13's integration amendment adopts pre-load adaptation and independent user
preferences; all consumers must adopt `CURRENT_SCHEMA_VERSION` through symbolic equality, including the
required non-Off `internalOptions` Optional (empty for Filesystem) and complete locale replacement.
This is owner-designed/receiver-adopted architecture, not fresh verification or PBR conformance.
The old load shape is not a fallback. Phase 1 already grants Phase 13 package placement;
that request is not a missing Phase 3 dependency. Phase 13 R4 post-analysis demand remains
genuinely ungranted; it may not become a macro producer. U1 is disposed by D-P3-67 below.
The jcpp owner grant is adopted above; exact implementation pin/closure proof and fresh reviews remain.

**U1 documented-mechanism authority adoption (D-P3-67).** The maintainer's dated correction in
`docs/decisions/U1_TEXTURE_SAMPLING.md` selects the explicit correction route formerly requested
by §11.5 item 3. Phase 3 adopts it here: required keys are the existing
`texture.<gbuffers|deferred|composite>.<sampler>[.0-9]` and `texture.noise`, with existing source
forms. Numeric suffixes are duplicate discriminators; filtering/wrapping uses Phase-13-owned
`.mcmeta` blur/clamp. No unspecified filter/wrap key parser, typed suffix API, or future U1
producer grant is required. Source specs, sidecar references, lossless declaration diagnostics
and unknown-key reduction behavior remain unchanged within schema `CURRENT_SCHEMA_VERSION`.

Phase 13 adopts the same authority correction in its own document and consumes only the existing
typed sources/sidecar references, never reparsing retained declaration strings. That owner
adoption and fresh independent reviews are not performed by this Phase 3 amendment. Existing
Phase 13 defaults/error policy remain phase-local and independently reviewable, not newly
ratified here. Runtime `.mcmeta` conformance still needs implementation evidence; diagnostic
retention is not that evidence. IR-01 and every unrelated implementation/dependency gate remain.
Phase 3 gains no dependency on a Phase 13 type, policy callback or GL operation.

**Phase 4 §5.4 item 2 — owner grant and remaining gates.** The direct per-program mipmap and
vertex projections are granted on the Phase 3 side by §5.1, preserving Phase 5/10 consumption.
Phase 4's integration amendment incorporates the direct projections and catalog-bound
materialization in active §§4.7/5.3 and reconciles its item-2 ledger. Both owner and receiver
remain unverified pending fresh §5 reviews. No Phase 4 dependency on Phase 5 or Phase 10 is needed to
obtain either value, and no Phase 3 reverse dependency is introduced.

Complete legacy geometry remains gated: Phase 1 **does** grant the narrow pre-link
`configureLegacyGeometry` operation, and Phase 4 adopts it conditionally. D-P3-68 now grants the
exact native-preserving source request/result, source-layout precedence, complete uniform
catalog/map and current-schema fingerprint/migration in §§2.2/4.5/4.10/5.1. This closes the **P3 source
API absence**, not runtime or consumer verification. `Translate` is removed; complete legacy-to-core
translation is not granted. jcpp's adopted P1 pin/closure proof, P4 adoption/fresh review and R26/P7/P10
submission conformance remain separate. The maintainer's conditional primitive adapter scope
permission does not constitute a completed adapter or authorization for a renderer rewrite.

**R-P12-5 — owner-granted in schema 18.** §2.2 replaces `lang()` (removed, no alias) with
`Map<String,LangDecorations> localizedDecorations()`. §4.3's complete acquisition, normalization,
collision, ordering, missing/empty and per-key requested-locale→en_us→literal/source fallback
rules are incorporated into §5.1. §4.10 hashes the full catalog, and §5.1.1 preserves it source-free.
Phase 12 adopts those rules without file access or another authoritative language map; Phase 2
projects all locales, not a selected UI result. Internal option durability remains separate.

## 6. Failure modes & degradation

| Failure | Degradation and diagnostic | G2.4 rung |
|---|---|---:|
| Unknown/malformed/out-of-family directive, property, profile token, screen entry, ID rule, texture spec, or persisted option line | warn on the appropriate Phase 1 channel; ignore only that line/edge/occurrence; retain prior/default value; **never abort the pack** | local parse rule supporting rung 5 |
| Unresolved texture key or invalid known texture value after successful Properties decoding | retain exact attributed declaration and disposition; warn, omit only its executable effect, and leave prior valid specs intact; never strip to a base binding or advertise unknown-key support | local parse rule supporting rung 5; unchanged by D-P3-67 |
| Missing include, include depth >10, cycle, bad source encoding, spoofed marker, invalid `#version` | mark affected source roots unavailable with attributed diagnostics; unrelated roots/config remain | hands Phase 4 a rung-3 program failure |
| Malformed/missing/repeated legacy pair member, mismatched PreserveNative request, unsupported/conflicting geometry layout, incomplete final uniform catalog or source attribution | affected root returns `Unavailable` with all attributed diagnostics; configuration remains inspectable, P4 fails/falls back the complete program; no partial source/catalog or `.gsh` drop | hands Phase 4 a rung-3 program failure |
| Ambiguous option | disable that option only and retain diagnostic/locations | feature-local, rung 2a analogue |
| Cyclic profile/subscreen | ignore cyclic edge; retain non-cyclic entries | feature-local, rung 2a |
| Invalid custom uniform expression text at runtime | Phase 3 preserves it; Phase 11 disables that custom uniform only | rung 1, later owner |
| Unmet `version.<mcver>` | configuration remains inspectable with incompatible status; Phase 7 keeps the pack off and Phase 12 displays warning | rung 4 |
| Persisted filesystem reference missing, ambiguous, kind-changed, or resolved against a foreign/stale snapshot | return the typed resolver outcome and keep shaders off; never guess from order or display text | rung 4 |
| Persistence-root acquisition invalid, or target candidate null/foreign/unknown/superseded/sentinel/unavailable | return the exact typed acquisition rejection before persistence I/O; caller retains prior state or shaders-off selection | rung 4 |
| Invalid discovery request | return fresh sentinels under an invalid generation; resolver returns `InvalidSnapshot`; preserve the currently retained directory table and its recency | rung 4 |
| Discovery candidate/byte limit exceeded, or retained-directory table full | publish the bounded keyed sentinel/diagnostic result; after any successful publication evict completion-LRU keys and expose the exact superseded outcomes | rung 4 |
| Null/foreign/malformed option state or same-domain cross-pack persistence pairing | return the closed state/persistence failure before evaluation, rewrite, or I/O; publish no partial result | rung 2a/4 |
| Selected input unsafe, over limit, unreadable/corrupt, internally invalid, or structurally unusable | apply §5.1's exhaustive ordered cause matrix, close all resources, publish no partial configuration, and select shaders-off | rung 4→5 |
| Proven optional owned-sidecar-only per-file read failure | retain `UNREADABLE` in the same-load asset snapshot under D-P3-69; P13 alone applies atomic source-specific baseline recovery, one warning and outcome fingerprint; all other input-read failures retain the fatal row above | approved owned-sidecar local recovery |
| Null load reporter | return `Failed(INVALID_REQUEST)` with its primary diagnostic, no callback, no I/O, and no throw | rung 4 |
| Missing companion option macros on non-`Off` | return `Failed(INVALID_REQUEST)` with the existing primary diagnostic before I/O or preprocessing; `Off` still returns successfully without inspecting that field | rung 4 |
| Debug-dump/persistence write failure | warn; retain in-memory configuration/state; never turn pack off | rung 2a |
| Unexpected parser/library exception | after input/provider attribution, catch at the front-end boundary as `UNEXPECTED_INTERNAL`, close the lease, and return shaders-off | rung 5 |

`STRUCTURALLY_UNUSABLE` applies only after safe bounded readable input succeeds and no effective
root or source configuration in either the base or any explicit `OVERRIDE` survives mandatory
decode/include validation; empty `DISABLED` folders alone are insufficient. It never subsumes
unsafe, limit, unreadable, internal-provider, malformed-line, or unexpected-implementation causes.

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
- Each file is decoded and include-scanned once. WCC is `O(V+E)`. Include expansion is cached by
  source-snapshot/root identity alone; later rewrite/preprocessing results additionally key option
  and macro fingerprints. All caches have bounded entries/bytes and retain no pack filesystem.
- jcpp instances are not shared. Macro maps and source maps are immutable inputs/results.
- Directive scanning is a single pass over active load-time analysis text with precompiled patterns and
  a table lookup; no whole-pack repeated regex sweep per directive.
- Debug dumping is opt-in and excluded from hot reload timings. It may contain pack source and is
  never a derived conformance artifact.
- Discovery candidate count, canonical snapshot bytes, and retained-directory count are bounded by
  `DiscoveryLimits`; completion-LRU eviction bounds aggregate retained discovery state. Limits for
  archive entries, uncompressed bytes, source bytes, graph nodes/edges, line length, macro count,
  and diagnostic count prevent zip bombs and adversarial allocation. Crossing a structural bound
  follows §6 rather than exhausting the client.

## 8. Testability plan

All Phase 3 unit and property tests are headless `:engine` tests. Phase 3 emits manifest and fixture
inputs to Phase 2's harness rather than assigning any test to `:conformance`; they use temporary
directories/zip files and Phase-3-authored synthetic `GLCapabilityProfile` values, with no GL
context or Minecraft type needed.

### 8.1 Unit and property tests

- D-P3-72 / `idMap_blockForced11300SameSource`: the actual ISO-8859-1 fixture
  `block-dual-era-modern-only` consists of these four LF-terminated lines (no injected header):

  ```properties
  #if MC_VERSION >= 11300
  block.31=minecraft:grass
  block.32=minecraft:redstone_lamp
  #endif
  ```

  Load those bytes as selected-pack `shaders/block.properties` with ordinary A–G
  `MC_VERSION=11202`: BLOCK publishes `PRESENT_EMPTY`, empty ordinary rules, and two MODERN
  alternate rules at original lines 2/3 with selector ordinal 0 and the exact pack origin.
  P9's PRESENT_EMPTY/nonempty gate selects those actual producer rules and its live-validated
  catalog maps them to `minecraft:tallgrass`/`minecraft:lit_redstone_lamp`, IDs 31/32.
  Repeat through `IdMappingParser.parse` using bounded mod bytes and
  `ModMappingOrigin("fixture",0,"block.properties")` plus that configuration's exact environment:
  the same rule/state/era/line result retains the mod origin rather than impersonating pack input.
  Origin-distinct file fingerprints differ; equal requests repeat deterministically.
- `idMap_blockOrdinaryWins`: add an `#else` before `#endif` containing
  `block.31=minecraft:grass` and `block.32=minecraft:redstone_lamp`. Both lists are nonempty;
  ordinary state is PRESENT_RULES, ordinary rules remain CLASSIC and P9 selects only them,
  retaining exact 1.12 grass/unlit lamp meanings. No alternate merge or ambiguity heuristic.
  The unconditional two-property variant likewise remains CLASSIC even though the independently
  produced alternate contains identical selectors. Shader-ID changes never choose an era.
- `idMap_blockAlternateIsolationAndFailure`: mutate only the modern conditional branch to
  `block.33=minecraft:grass`; ordinary empty state is unchanged, alternate payload/file and
  configuration fingerprints change. Inspect with current schema23 and projectionVersion=1:
  preserve both source-free ID lists and actual attribution within the same nine-tree shape.
  An alternate-only preprocessing failure returns an empty alternate plus actual attributed
  diagnostic without changing valid ordinary rules; an ordinary-only failure leaves a separately
  valid alternate available under PRESENT_EMPTY. Mutating caller bytes after the defensive copy,
  or source-local define/undef in either pass, cannot alter the other run or published environment.
  Exercise finite per-run limits without extra retries; unsafe acquisition still follows fatal
  load precedence. ABSENT runs neither pass; present comment-only BLOCK has both lists empty.
  Parsing these same conditional bytes as LAYER leaves its alternate empty; ITEM alternates are
  always empty, and existing ENTITY cases remain unchanged. Consumers reject schema22/current23
  in both directions and any mismatched nested/inspection schema before deriving state.
  These are concrete future parser-to-resolver fixture traces, not executed evidence or fabricated
  MODERN resolver records. P3 producer coverage is v0.1; P9 live-alias integration is v0.3.

- D-P3-73 / `idMap_typedMetadataSetsAndPropertyRanges`: use one LF-terminated line
  `block.31=minecraft:leaves:3,7,11,15 minecraft:reeds:age=0-3 minecraft:reeds:0-3:age=0-3`.
  Require precisely three rules with original line 1 and ordinals 0/1/2, exact §4.9 payloads
  and unchanged shader ID 31. P9 finite-state fixtures distinguish leaves metadata 3/7/11/15
  from 2/4/14, reeds ages 0/3 from 4, and combined states (metadata,age) (0,3)/(3,0)
  from (4,0)/(0,4); no cross-state conjunction or literal `0-3` equality is permitted.
  Repeat short/namespaced/legacy `83:0-3:age=0-3` spellings and a namespace-unknown control,
  preserving exact lookup/no fallback. Repeat through bounded mod bytes, an ordinary/forced
  unconditional BLOCK pair, and a modern-only `#if MC_VERSION >= 11300` wrapper (rule line 2).
  Repeat as `layer.cutout=...` in the same bytes: identical constraints, ordinary CLASSIC only,
  with P9's solid-opaque exclusion unchanged. ITEM/ENTITY predicate-bearing controls remain
  typed but match nothing at P9.
- `idMap_rangeBoundariesNoExpansionOrOvermatch`: metadata `0-15` is one interval; property
  `age=0-2147483647` is one interval and visits only finite live values. Cover equal endpoints,
  leading-zero normalization, comma-order retention, mixed `age=0,2-3`, distinct overlapping
  ranges, missing property, unknown literal, interval disjoint from the live domain and an
  in-range subset with absent intermediate domain integers. Reject empty/duplicate alternatives,
  duplicate properties, `15-0`, `0-16` metadata, `3-0` property, `0-2147483648`, `-1`, `+1`,
  `1-`, `-3`, `1-2-3`, extra colons and metadata-after-property; preserve neighboring valid
  selector rules and their original ordinals. Below/at/above existing token bounds cannot expand,
  truncate or publish partially accepted predicates. Mutating caller lists after construction
  cannot affect any retained rule or its identity.
- `idMap_rangeIdentityAndInspection`: alter only an endpoint, variant, alternative order or
  alternate branch and require changed canonical file/configuration/materialization payloads;
  equal complete input repeats deterministically. P2 uses §5.1.1's typed source-free projection
  without new raw strings, and preserves all nine trees/projectionVersion1. Reject schema22/23
  in both directions, future versions and mismatched containing/nested/inspection schemas before
  derivation or serialization; no singleton-scalar upgrade or old-cache reuse. These are planned
  producer-to-consumer boundary cases, not executed tests or fresh review evidence.

- R55-1 / D-P3-70 structural boundaries: a safe bounded pack with only valid
  `shaders/world-1/gbuffers_basic.vsh` and `.fsh` loads an empty base plus usable `OVERRIDE`;
  selection of -1 uses only those roots, while an absent world selects the empty base.
  A usable override survives unrelated base decode/include failures; a usable base likewise
  survives an unusable override. No roots, only empty disabled folders, or all roots invalid
  yields `STRUCTURALLY_UNUSABLE`. Combined unsafe/bounds/read failures retain §5's priority,
  including D-P3-69's narrowly authenticated optional-sidecar exception, never false success.
- R55-2 / D-P3-70 raw boundaries: exact 1D/2D/3D/rectangle tokens accept 1/2/3/2 dimensions.
  `textures/lut.dat TEXTURE_RECTANGLE RGBA8 16 16 RGBA UNSIGNED_BYTE` produces typed `Raw`
  with `RECTANGLE` and `[16,16]`; readable bytes remain acquirable after input lease closure
  through `assets`, with any adjacent sidecar retained. Bare `RECTANGLE`, mixed/lowercase,
  unknown token, zero dimension, missing/extra dimension and trailing operand yield
  `INVALID_VALUE`, no new executable spec, and preserve a previous valid complete-key winner.
- R55-3 / D-P3-70 selector/schema boundaries: literal `<profile>` with zero definitions,
  two definitions plus an inferred selection, or two definitions plus `Custom` always publishes
  the same zero-component selector. P12 obtains current/preview inference separately and cycles
  existing declaration order; P2 emits only the selector's `$type` while retaining definitions
  and finalized values separately. Containing/nested ID/inspection schemas must all equal
  `CURRENT_SCHEMA_VERSION`; older (including schema20), future and mismatched values reject
  before derivation/enrichment/serialization. Old named-selector graphs and old materialization
  identities cannot be shimmed or reused. D-P3-69's nine-tree metadata codec remains unchanged.
  These three boundary sets are architecture test plans, not executed test evidence.

- Binary snapshot grant (D-P3-69): `assets_zipDeletedOrReplacedAfterLoad` acquires exact PNG/raw/
  noise/sidecar bytes after lease closure and host deletion/replacement; a later full load sees
  new bytes while old cursors retain the original. `assets_sameLoadIdentityAndFingerprint`
  rejects cross-load capability pairing, verifies byte SHA-256 against manifest/pack identity
  and distinguishes missing, empty and changed content independently of lookup order/host path.
  `assets_declaredDomainAndCursorIsolation` covers undeclared/null rejection before existence,
  reduced-away/Minecraft/generated references, missing admitted sources, independent positions,
  read-only duplicates/slices and no accessible mutable backing array.
  `assets_boundsAndFatalReadPrecedence` covers entry/total/per-file bounds with checked arithmetic,
  unsafe-before-limit-before-unreadable, unused-file accounting, Internal copy/identity failure,
  and no capability on failed load. `assets_sidecarTimingAndRecovery` distinguishes absent ref/
  Missing, present-empty or malformed JSON/Acquired, and indexed sidecar-only Unreadable with
  usable source, atomic baseline, one P13 warning and distinct fingerprint. A sidecar path also
  used as an include/configuration/primary source or with unprovable duties stays fatal; invalid
  bounds/index/container cannot recover. Malformed primary bytes remain Acquired for P13 validation.
  `assets_resourceOnlyNoneRetainsSnapshot` reuses exact config/assets across resource epochs,
  without acquiring current host pack bytes or mutating its identity.
  `assets_sourceFreeInspectionAndCurrent-schema` rejects older/missing-capability graphs, verifies
  canonical ordered outcome/count/hash metadata and absence distinctions, and proves no binary
  payload, cursor, provider, host path or capability token reaches an inspection artifact.
  These are planned owner/consumer boundary cases, not executed tests or a PASS claim.
- Locale catalog: `lang_allLocalesSameSnapshotAndImmutable` covers multiple locales after lease
  closure; `lang_normalizationCollisionOrder` covers en_US/en-us collision in reversed discovery
  order and a malformed winner; `lang_missingEmptyAndPerKeyFallback` covers absent locale,
  present-empty file, empty translated value suppressing en_us, independent comment/value/affix
  fallback and invalid requested locale; `lang_utf8BomEscapesAndDuplicates` covers non-ASCII,
  duplicate logical keys, malformed occurrence retention and exact whitespace/terminal markers.
  `lang_fingerprintAndSourceFreeInspection` distinguishes missing/present-empty/changed translations
  while locale selection alone leaves configuration/materialization fingerprints unchanged.
- Old-light resolution: `macro_oldLightUserPackFallbackBeforeShaderJcpp` covers all nine user/pack
  tri-state pairs for each key, both-default local true, explicit-false override, one A–G-only
  properties parse, shader branches and unchanged same-build materialization. No registry/GL or
  shader-analysis callback participates; runtime P9/P10 resolution must agree.
- Schema cutover: schema17 producers are rejected by schema18 consumers and vice versa before
  deriving state; no fabricated locale map or legacy explicit-user macro reinterpretation.
- Native schema cutover: schema18/19 reject one another before derivation; no two-span-result
  upgrade, inferred native strategy, fake source map or unchanged-text cache reuse.
- Internal session: `internalOptions_captureRejectsForeignAndFilesystem` covers null/foreign/
  filesystem failures without I/O; `internalOptions_freshCatalogSameIdentity` covers capture,
  fresh-load rebinding and immutable old configuration; `internalOptions_sessionSwitchAndRestart`
  covers Apply→switch away/back, restart defaults, reset defaults, discarded preview,
  preacceptance rejection and postacceptance failed-load preference retention. Changed identity
  and wrong bundle reject before jcpp rather than silently dropping edits.
- Sampling compatibility: changing retained `superSamplingLevel` changes declared/source option
  metadata and fingerprint as appropriate but never derives an engine AA macro, size multiplier,
  extra draw or resolve request; AA reserved zero round-trips with no UI control.

- Discovery/path/lifetime: `discovery_caseFoldThenNatural`,
  `discovery_nestedRootDeterministic`, `discovery_offAndInternal`,
  `discovery_offReturnsSuccessfulNoConfiguration`,
  `discovery_generationSupersessionAndDirectoryIndependence`
  (aliases of one real directory supersede old IDs; distinct retained real directories remain
  independent),
  `discovery_candidateAndByteLimitsReturnBoundedSnapshot`,
  `diagnosticCanonicalCodec_closedArgsTypeTagsAndUnsupportedRejection`
  (all four exact boxed variants, string/integer/long same-text separation, null/nested/other-type
  rejection, non-null field framing, and immutable ordered arguments),
  `discovery_snapshotBytesImmediatelyBelowAtAboveLimit`,
  `discovery_fixedOverflowSnapshotFitsConfiguredMinimum`,
  `discovery_retentionCompletionLruAndFreshRecovery`,
  `discovery_concurrentEvictionCompletionOrder`,
  `discovery_evictedGenerationOutcomesAndAcquiredTargetLifetime`,
  `discovery_tokenIssuanceForgeryAndOwnership`
  (ordinary discovery issues both token types from legal same-package permitted classes; public
  implementations/constructors are unavailable; foreign-instance, unknown, and superseded tokens
  fail before input access),
  `discovery_referenceRestartResolutionOutcomes`
  (round-trip through a new front-end instance and fresh discovery; stable under reorder,
  duplicate display names, and content change; exact missing, NFC-ambiguous, kind-change, foreign,
  and superseded outcomes),
  `discovery_directoryIdentityCaseAndFailures`,
  `discovery_invalidGenerationOrderingAndPublication`
  (invalid-before-valid, valid-before-invalid, formerly valid path unavailable, resolver
  `InvalidSnapshot`, sentinel IDs, and concurrent completion publication),
  `load_offIgnoresHostDirectory`, `load_internalIgnoresHostDirectory`,
  `discovery_staleAndUnknownSelectionInvalid`,
  `internalSnapshot_orderDuplicatesLimitsAndEmptyDirectories`,
  `normalizedPackPath_canonicalStringStableUtf8`,
  `pathRejectsTraversalAbsoluteAndSymlink`, `archiveLeaseClosesOnEveryExit`,
  `compatibilityVersionAndEditionGrammarComparatorVectors`
  (two-/three-component keys, matching/nonmatching 1.12.2, malformed forms, mixed letter/digit
  natural order, separator rank, ASCII case equivalence, leading-zero equivalence, prerelease-style
  order, any-unmet aggregation, and warning/ignore), and
  `minimumEditionRule_sourceAccessorProjections`
  (`1.12` versus `1.12.0` and comparison-equal differently spelled editions remain exact decoded
  accessor values in source order),
  `publicValueTypes_identitySelectionRuntimeAndFailureClosed`,
  `idMap_macroEnvironmentAndFingerprintClosed`,
  `texturePropertyStage_closedDomainAndOrdering`,
  `macro_phase6CenterDepthSlot`, `propertiesModel_engineFlagsDefaultsAndUnknownRetention`,
  `dimension_fullLegacyScanRejectsIrisModel`, `dimension_readsOnlyVshFsh`,
  `dimension_optionsCanUseDistinctNames`.
- Includes/attribution: `sourceCatalog_orderedRootsSourcesIncludesAndMaterializer`,
  `sourceCatalog_programProjectionStagesDimensionsAndAbsence`
  (multi-stage collapse, every partial stage, cross-dimension same-name distinction, virtual-pre
  exclusion, and source-absent property separation),
  `sourceCatalog_sharedIncludeCanonicalPhysicalIdentity`
  (one document/node and one edge per physical location across unlike program, stage, and
  dimension roots),
  `sourceCatalog_computeRootsApplicabilityAndDimensions` (`post-v0.5`; each known non-gbuffers
  program's base and `_a`…`_z` files, associated `programName`, distinct physical IDs, COMPUTE
  materialization, and dimension-folder rejection),
  `sourceMap_numericLookupAndMaterializedLifetime`, `include_relativeAttribution`,
  `include_absoluteAttribution`, `include_depthTenAndCycle`,
  `include_missingAffectsOnlyReachableRoots`, `include_graphAllRootsStable`,
  `preprocess_lineAttribution`.
- Preprocessor/header: `preprocess_allConditionalForms`, `preprocess_definedBothForms`,
  `preprocess_macroSubstitution`, `preprocess_versionExtensionHoist`,
  `preprocess_versionMustLead`, `preprocess_markerSpoofRejected`,
  `preprocess_apiMacrosDoNotShiftLines`, `macro_onDemandExtensionOnly`,
  `macro_mcVersionFixed11202Boundaries`
  (accepts only `(1,12,2)` and exact `11202`; rejects each first-neighbor component, negative,
  zero-major, large, and `Integer.MAX_VALUE` tuples before I/O),
  `macro_engineVersionCanonicalDecimalBoundaries`
  (`0`, `1`, and `2147483647` emit exactly; sign, leading zero, whitespace, separator, empty, null,
  and overflow reject before I/O), `macro_glVersionFormulaAndBounds`,
  `macro_glslVersionAnchoredGrammarAndMalformedLoad`
  (`1.20`, `4.60`, decorated `4.60 NVIDIA 535.98`, leading-zero/whitespace, one-/three-digit minor,
  extra dotted component, empty decoration, newline, null, and overflow),
  `macro_osFamilyExhaustive`, `macro_vendorRendererOrderedClassifiers`
  (every row, ASCII case variants, `NVIDIA Quadro` overlap, prefixed-decoration fallback, empty
  fallback, null rejection, and proof that no unlisted macro is emitted),
  `macro_optionFamilyMembershipAndValues`,
  `macro_optionNumericSerializationAliasesAndThresholds`,
  `macro_companionTypedStateBeforeJcpp`
  (each of the four typed pairs selects the matching load-time resource branch and materialized
  declaration branch; false is absent rather than defined as zero; differing valid global settings
  or renderer availability cannot override the pair; all three identity policies agree; properties
  and ID-map conditionals receive neither macro),
  `macro_companionMissingNonOffAndOffShortCircuit`
  (null pair on valid Internal/Filesystem requests yields `INVALID_REQUEST` before provider,
  persistence, pack, or jcpp work; Off with null other fields returns Off),
  `macro_companionSameBuildAndFingerprints`
  (reload with either bit changed changes both fingerprints, even for macro-unreferencing roots;
  equal inputs across service instances agree; retained old materializers keep the old branch and
  fingerprint after reload; no later plan/contribution can replace the pair),
  `macro_phase6CenterDepthSlot`,
  `directive_shadowFovDomain`
  (accepts finite values immediately above 0 and below 180 in every form; rejects 0, 180,
  out-of-range, NaN, and infinities while retaining the prior/baseline value),
  `materialize_noGeometryPairAcceptsNone`, `materialize_geometryPairRejectsNone`,
  `geometryNative_preservesVersionExtensionConstantUsesBuiltinsAndAdjacentVaryings`,
  `geometryNative_pairCardinalityAndIncludedMacroOrigins`,
  `geometryNative_rejectsWrongRootSiteCountOrContributionMismatch`,
  `geometryNative_partialAndCompleteLayoutsOverrideApiPerProperty`,
  `geometryNative_equalRedeclarationsAllowedConflictsUnavailable`,
  `geometryNative_intrinsicConditionalEnvironmentAndLineMapping`,
  `geometryNative_finalUniformCatalogCompleteOrUnavailable`,
  `load_legacyGeometryPublishesBeforeNativeRequestSelection`,
  `materializedSource_transformedTextAndSourceMapAreExactPair`.
  These planned cases must use active/inactive option/include branches, code reading
  `maxVerticesOut`, native input arrays and varying linkage, source layouts different from API
  defaults, a same-text different-route/schema/map identity case, and malformed pairs that
  leave unrelated programs usable. CPU materialization is not GL proof: P4/P1 must additionally
  exercise preserved VSH/GSH/FSH compile/configure/link with a real advertised ARB context, verify
  source-over-API linked properties and complete fallback/cleanup; P7/P10 own native draw
  primitive/attribute/provoking-vertex/primitive-ID conformance under their separate grant.
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
  `optionTooltip_terminalBangPreservedAndRed`, `switchOption_sameFileConfirmation`,
  `variableOption_defaultAutoAdded`, `constOption_completeWhitelistAndAliases`,
  `optionAmbiguity_conflictingDefaultsDisabled`,
  `optionRefsDoNotCrossWcc`, `optionAmbiguity_duplicateNamesAcrossComponents`,
  `optionRewrite_onlyCapturedSpan`, `optionRewrite_includedChangedOptionOrder`,
  `optionRewrite_roundTripMaterialization`,
  `optionState_catalogBoundConstructionUpdateValidation`
  (all failure variants and priority, ambiguous default/update, safe out-of-list warning),
  `optionState_previewAndPersistenceRejectForeignAndSameDomainCrossPack`
  (profile inference and read/write pre-I/O outcomes, including null catalog/state),
  `runtimeOptionState_requiresReloadForCoherentSnapshot`
  (a same-catalog update changes one conditional resource declaration and
  `program.<name>.enabled`; the old configuration cannot accept it, while persistence plus a fresh
  atomic load changes finalized state, requirements, materialized source, evaluated program state,
  and both fingerprints coherently), `profiles_inferenceMatchCustomAndInvalidState`,
  `screen_columnsExpandedSlotFloor`
  (asserts negative rejection; default 18→2, 19→3, 27→3, 28→4; explicit 1 is raised at 19 while
  explicit 4 remains 4; ordinary, applicable profile/subscreen, and `<empty>` slots count after `*`
  expansion), `optionModels_areImmutableAndAccessorClosed`,
  `macroConfiguration_overrideValidationAndOrdering`,
  `macroConfiguration_overrideActionTargetMatrixAndPrecedence`
  (every action against absent/capability/identity names, protected targets, replacement shapes,
  absent suppression no-op, base/order collisions, first diagnostic, pre-I/O failure, no partial map,
  and §4.5's reserved intrinsics plus arbitrary non-identity/capability names rejected by the §4.4
  whitelist with the same pre-I/O `INVALID_REQUEST`),
  `propertiesModel_engineFlagsDefaultsAndUnknownRetention`,
  `persistence_accessAcquisitionRootsAndDomain`
  (validates exact shaderpacks/game roots, global filename, null/missing/non-directory/unreadable/
  provider failures, own-domain acceptance, foreign-domain pre-I/O rejection, and handle closure),
  `persistence_targetAcquisitionOutcomesAndLinearization`
  (covers null, both sentinels, foreign, unknown, superseded, unavailable, every available
  filesystem kind, acquire-before-supersede survival, and supersede-before-acquire rejection),
  `persistence_directNameContainmentAndSymlinkSafety`
  (exact directory/archive/Unicode names, extensions, long-name access failure, traversal,
  symlink target/parent, and exact `shaderpacks/<pack>.txt` location with no digest/slot probe),
  `persistence_codecRequestsResultsAndAtomicNoSymlink`,
  `loadRequest_selectionDependentValidation`,
  `load_persistedOptionsAffectConfiguration`
  (initial load and reload prove one persisted non-default changes published state, rewritten-source
  analysis, resource requirements, and configuration fingerprint; absent/failed access uses the
  diagnosed baseline), `internalSnapshot_providerBoundaryFailures`,
  `persistence_outOfListValueRetainedAndWarned`,
  `persistence_packChangedOnlyRoundTripAndApply`,
  `persistence_globalCodecMatrixAndExactOutput` (absent, baseline overlay, last-valid duplicate,
  invalid/foreign pre-I/O rejection, access/decode failure, result invariants, unknown retention,
  and exact all-entry output), `persistence_globalValueDomainBoundaries` (every known default,
  Boolean/tri-state case/empty rejection, default/true/false round-trip and explicit-user macro
  projection, decimal exponent/underflow/zero/nonfinite boundaries, exact-zero-only AA rejection,
  invalid read baseline/write/load data and canonical-empty read fallback,
  null map/key/value, deterministic failure priority, and immutable canonical order),
  `persistence_globalLogicalOccurrenceClassification` (typed-invalid known omission,
  unknown-safe empty/unicode round trip, unsafe/control rejection, and invalid duplicates retaining
  the prior valid value), and `persistence_globalExactEscapingAndOrdering` (every escaped ASCII
  metacharacter/space, BMP and supplementary Unicode, unsigned-UTF-8 key order, no header/timestamp,
  final LF, and byte-exact ISO-8859-1 output).
- Properties safety: `propertyHashRoundTrip`, `properties_whitespaceAndHash`,
  `properties_continuationsEscapesAndComments`, `properties_conditionalScreenLayout`,
  `properties_malformedDirectiveWarns`, `properties_standardMacrosButNoOptionMacros`, and
  `texture_packPathPngCaseAndLastValid` (accepts a non-empty-stem lowercase `.png`, rejects case
  variants/bare extension/trailing text/non-PNG, and preserves a prior valid duplicate), and
  `texture_sidecarPathAccessorCustomRawNoise` (exact adjacent normalized path, structural equality,
  absence, and custom/raw/noise access without loading sidecar bytes), and
  `texture_declarationsLosslessBeforeReduction` (active decoded base/noise/unknown keys, empty and
  malformed values, escapes, continuations, whitespace and `#` retain exact text, order, duplicates,
  dispositions and attribution; inactive branches do not appear),
  `texture_unresolvedNeverAliasesBase` (base, `.0`, `.9`, arbitrary extra segments before/after a
  numeric segment, and repeated unknown keys in both orders leave valid sources/discriminators
  unchanged while every unknown occurrence survives; example tokens assert capture, not support),
  `texture_lastValidProjectionKeepsAllDeclarations` (valid → invalid → valid duplicates retain
  all three records but expose only the final valid source; unknown keys never enter the reducer),
  `texture_declarationFingerprintAndLifetime` (mutating any retained field/order/multiplicity,
  including unresolved or invalid occurrences, changes the canonical payload; equal input is
  deterministic, and the frozen list survives source closure),
  and `texture_sidecarIndependentOfUnresolvedKeys` (present/absent adjacent sidecars on accepted
  pack/raw/noise sources retain exact paths without reads or guessed suffix state; Minecraft
  sources gain no pack sidecar). Numeric discriminators must never produce filter/wrap state.
  These are future checks, not executed tests or rendering evidence; Phase 13 separately tests
  its documented `.mcmeta` behavior under D-P3-67, not an invented suffix grammar.
- Custom expressions: `customDecl_allUniformTypesRawExpressionAndAttribution` and
  `customDecl_allVariableTypesAndDuplicatesRetained` cover both kinds, all six types, exact
  case-sensitive names, same-name/different-kind/type declarations, repeated exact keys, and
  positive source coordinates; `customDecl_expressionTextLosslessAfterUnescape` covers escapes,
  continuations, whitespace, `#`, backslashes, and proves no post-unescape trim/normalization;
  `customDecl_orderOrdinalAndFingerprint` proves immutable list order, contiguous ordinals equal to
  list positions, empty-list semantics, deterministic equality, and a fingerprint change when any
  ordered field, attribution coordinate, occurrence order, or duplicate multiplicity changes; and
  `customDecl_invalidOccurrenceIsLocal` proves invalid key/type/name input omits only that
  occurrence while an empty decoded expression remains published, without evaluating expression
  grammar.
- ID mapping input: `idMap_fileStateAbsentEmptyRulesPerKind` distinguishes missing, blank,
  conditional-empty, all-invalid, and nonempty block/item/entity/layer inputs;
  `idMap_entityForced11300Isolated` proves identical bytes/environment except `MC_VERSION`, no
  absent-file parse, ordinary `CLASSIC`, alternate `MODERN`, independent diagnostics, and no merge;
  `idMap_selectorKindEntryAndTag` covers short/namespaced entries, canonical `%` tags, ordering,
  and rejected non-tag forms; `idMap_tagIdentifierGrammarBoundaries` covers every namespace/path
  character class, colon/slash/empty-component boundary, `.`/`..`, ASCII/case rejection, short
  expansion, stored canonical form, doubled `%`, properties, metadata, rejects `%123`, and accepts
  `%minecraft:123→minecraft:123`, `%123:foo→123:foo`, and
  `%123/foo→minecraft:123/foo`;
  `idMap_propertyPredicateAlgebraAndRejection` covers OR-within, AND-between, missing properties,
  canonical ordering, and every invalid predicate shape;
  `idMap_modContributionOriginPreserved` feeds defensive bounded
  ISO-8859-1 bytes through the public operation and gets the same canonical output as pack loading;
  `idMap_originVariantsIdentityValidationAndOrdering` covers pack attribution, canonical mod IDs,
  per-mod ordinals, record equality, invalid construction, and leaves precedence evaluation to Phase 9;
  `idMap_fingerprintCoversPresenceEnvironmentAndBothParses` mutates each load-bearing input; and
  `idMap_schemaAndContainingConfigurationMustMatch` rejects every non-current value, including v1–v22 and future versions, and mismatched nested schemas.
- Appendix F tests: every Phase-3-owned test named in §§3.1–3.2 is required; a parameterized key
  manifest fails if a parser/model row lacks a Phase 3 assertion. Behavior-only handoff rows name
  their downstream owner's test and are excluded from the Phase 3 parser manifest.
- Appendix A.3 tests: every test named in §3.3 is required; a generated mapping manifest asserts
  one parser, one field target, one applicability predicate, and one test ID per row. The
  clear/clear-color/mipmap family tests include eligible and wrong-family occurrences in both
  source orders.
- Pintonium pitfall gates are named exactly:
  `directive_drynessWritesDryness` (B1),
  `directive_legacyCommentFormsReachFields` (B2),
  `optionRefsDoNotCrossWcc` (B3), and
  `propertyHashRoundTrip` (B12).
- Validation/publication: `malformedLineNeverAbortsPack`,
  `structuralFailurePublishesNoPartialConfiguration`,
  `loadFailure_primaryDiagnosticReporterCorrespondenceAndLocalization`
  (every code and cause variant asserts `ERROR`/`CHAT`, its exact table key, immutable empty
  arguments, empty redacted detail, `schmaloogium.pack`, exact instance identity with the single
  reported value, and no display text, synthetic ID, provider exception, or partial configuration),
  `loadFailure_nullReporterNoThrowNoIo`
  (the exact table-defined primary `INVALID_REQUEST` diagnostic, zero callback/I/O, no throw), and
  `loadFailure_causeCodeMatrixAndPrecedence`
  (every matrix cause, corrupt archive and provider I/O, every finite bound, internal invalidity,
  bounded readable no-root/no-surviving-base cases, unexpected exception, canonical same-class
  tie, and unsafe/limit/unreadable/structural multi-cause precedence),
  `packConfigurationIsOnlyLoadOutput`, `fingerprintChangesOnEveryLoadSemanticInput`,
  `fingerprintChangesWhenMinimumEditionCrossesCompatibility`
  (holds pack bytes and every other fingerprint input fixed and asserts both status and fingerprint change),
  `materializedFingerprintChangesWithContributionRouteSiteLayoutAndMap`,
  `materializedFingerprintChangesWithUniformCatalogSchemaOrContent`,
  `schema_currentValuePublished` (accepts `CURRENT_SCHEMA_VERSION`), `schema_recordComponentChangeBumps`,
  `schema_incompatibleChangeBumps`, `schema_unsupportedVersionRejected`,
  `schema7RejectedBySchema8`, `schema8RejectedBySchema9`, `schema9RejectedBySchema10`,
  `schema10RejectedBySchema11`, `schema11ProducerRejectedBySchema12Consumer`,
  `schema12ProducerRejectedBySchema11Consumer`, `schema12ProducerRejectedBySchema13Consumer`,
  `schema13ProducerRejectedBySchema12Consumer`, `schema13ProducerRejectedBySchema14Consumer`,
  `schema14ProducerRejectedBySchema13Consumer`,
  `schema14ProducerRejectedBySchema15Consumer`, `schema15ProducerRejectedBySchema14Consumer`,
  `schema15ProducerRejectedBySchema16Consumer`, `schema16ProducerRejectedBySchema15Consumer`,
  `schema16ProducerRejectedBySchema17Consumer`, `schema17ProducerRejectedBySchema16Consumer`,
  (reject before derived state; never fabricate an empty declaration list for an older producer),
  `schemaMismatchInvalidatesRetainedState`, `texture_sharedUnitSamplerTypeDerivedLater`,
  `texture_publicNominalAlgebraProducerConsumerCompatibility`,
  `publicServices_factoryDependenciesLifetimeAndOwnership`,
  `publicServices_nonPublicConstructionAndDomainIsolation` (no public constructor; each bundle's
  own target/access is accepted; a second bundle's target/access is rejected before I/O), and
  `programState_publicGraphConstructorsImmutabilityAndAccessors`.
- Resource/directive aggregation: `resourceLeaf_publicConstructorsAccessorsEqualityAndOrdering`,
  `resourceLeaf_rejectsNullSetAndNonfiniteVec4`,
  `resourceLeaf_attachmentFormatAlgebraConstructors` (constructs `DefaultRgba`, every explicit
  37-value format including distinct `RGBA8`, and rejects a null explicit format),
  `directive_attachmentFormatDefaultExplicitAndGdepthFold` (covers entry creation by clear,
  clear-color, and mipmap, explicit sized directives, and `gdepth` in both orders),
  `fingerprint_attachmentFormatVariantAndExplicitValue`,
  `fingerprint_resourceCodecEveryLeafAndInsertionOrder`
  (mutates every published scalar/enum/string/optional/variant/list/set/map leaf, distinguishes
  sequence order, equates semantically equal maps/sets with different insertion histories, and
  proves signed-zero canonicalization),
  `resource_programProjectionSnapshotAndIdentity` (future headless producer-contract check:
  load distinct base/world program requirements, including a source-present program with no
  resource entry; observe exact mipmap membership and `.vsh` opt-ins through the Phase 4 access
  path, unchanged Phase 5/10 values, empty baselines without map mutation, and no base borrowing
  for an override. A reload changing an active opt-in/request changes the canonical resource
  payload while the old configuration stays unchanged; projection reads neither change
  fingerprints nor require a downstream service. Reuse the existing wrong-family/stage and
  resource-codec cases rather than adding a second parser fixture suite),
  `directive_colortexFormatDomainAcceptedRejected`,
  `directive_gdepthUpgradeAlways`,
  `directive_gdepthWithPriorExplicitFormat`, `directive_gdepthWithSubsequentExplicitFormat`,
  `directive_colortexClearColorAndIndexBaseline`,
  `directive_explicitTransparentBlackRemainsExplicit`,
  `directive_colortexClearFamilyFilterAndOrdering`,
  `directive_colortexClearColorFamilyFilterAndOrdering`,
  `directive_colortexMipmapFamilyFilterAndOrdering`,
  `directive_drawbuffersAbsentMeansAllUsed`,
  `directive_drawbuffersPositionalSlotsAllCommentForms`, and
  `directive_drawbuffersLeadingMiddleTrailingRepeatedAndAllNone`.
- Program state: `programState_scaleArityDefaultsRangeAndFamilyOrdering` covers one-token zero
  offsets, explicit three-token offsets, two/extra/nonnumeric/nonfinite/out-of-range forms,
  executable deferred/composite, both exact virtual-pre names, other/unknown families, and valid/
  invalid occurrences in both orders; `bufferNameNormalizer_allCanonicalLegacyAndRejected` covers
  all sixteen exact spellings, case variants, out-of-range canonical names, and near-miss aliases;
  `bufferNameNormalizer_allParserFamilies` drives the mappings through uniform sizing, format,
  clear/clear-color/mipmap, fixed `GAUX4FORMAT`, and flip parsing; and
  `programState_flipFamilyVirtualPreAndInvalidBuffer` accepts executable and both virtual-pre
  deferred/composite names, rejects shadow/gbuffers/final/unknown before mutation, and rejects
  virtual names in `<buf>`; and
  `programState_profileAwareAggregateEvaluation` covers qualified/unqualified disables, unknown
  profile/program warnings, expression warnings, null method inputs, and receiver-finalized option
  state; and `programState_executableProjectionMembership` covers every projected key, multi-stage
  collapse, partial stages, cross-dimension names, source-absent properties, both virtual-pre flip
  slots, and ordering. Schema-11/12 and schema-12/13 producers/consumers reject the opposite version.

### 8.2 Golden and matrix tests

P3-C20 exposes §5.1.1's exact inspection projection and the following inputs to Phase 2's
runnable-before-renderer harness; Phase 2 alone owns serialization and `:conformance` integration:

1. synthetic, project-authored fixtures for every directive syntax/key, every eligible and
   wrong-family program case, and every properties grammar edge;
2. source-free inspection snapshots containing typed decisions, hashes and attributed diagnostic
   coordinates; source-bearing configurations remain only same-request P4 compiler inputs;
3. Phase-3-authored synthetic capability-profile values/vectors covering OS/vendor/renderer/
   extension branches, emitted with the selected vectors in this Phase 2 hand-off; and
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
| P3-C01 | deterministic bounded discovery/sentinels, completion-LRU snapshot retention, legally sealed same-package token issuance, durable candidate references, and fresh-snapshot resolution | `v0.1` |
| P3-C02 | safe folder/zip input snapshot, canonical normalized-path projection, root/path guards, lease lifecycle | `v0.1` |
| P3-C03 | OF base/dimension source-set model | `v0.1` |
| P3-C04 | canonical physical source IDs/documents/include edges, root-only contextual keys, WCC, depth/cycle handling | `v0.1` |
| P3-C05 | include expansion and `#line`/source-map attribution | `v0.1` |
| P3-C06 | sealed catalog-bound option state, closed construction/update/validation/inference, and profile/screen/slider/lang model with expanded-slot screen-column floors | `v0.1` |
| P3-C07 | typed current-candidate target acquisition, same-pack catalog/state authentication, safe access, direct-name persistence, closed per-pack/global codec matrices, and atomic load integration | `v0.1` |
| P3-C08 | configurable OF-era/feature/identity macros with fixed `MC_VERSION`, canonical engine-version projection, required typed companion snapshot (explicit false pair at v0.1), six v0.1-effective option macros, the complete override action/state matrix, and unchanged Phase 6 slot | `v0.1` |
| P3-C09 | OQ-7 policy finalizer/per-pack experiment results | `post-v0.5` |
| P3-C10 | jcpp shader-source processor with hoisting/spoof guards and D-P3-68 native GLSL preservation/classification | `v0.1` |
| P3-C11 | properties-safe jcpp adapter and lossless property parser | `v0.1` |
| P3-C12 | table-driven legacy Appendix A.3 directive scanner with exact family applicability and positional `DRAWBUFFERS` slots, excluding `RENDERTARGETS` | `v0.1` |
| P3-C13 | immutable resource-requirement aggregator with exact closed public leaves, positional routing, program-family filters, complete canonical codec, and direct Phase 4 mipmap/vertex projections preserving Phase 5/10 consumers | `v0.1` |
| P3-C14 | complete Appendix F model with exact decoded edition accessors, provisional edition grammar/comparator, source-projected program evaluation, D-P3-38 PNG interpretation, unchanged lossless texture declarations/dispositions and D-P3-67 documented-mechanism scope, closed flags/state, other-key unknown retention, and custom expressions | `v0.1` |
| P3-C15 | current-schema ID-mapping/layer parser, D-P3-73 typed bounded metadata/property intervals without expansion, file-state/selector/era provenance, and isolated forced-11300 BLOCK/ENTITY parses under D-P3-72 | `v0.1` |
| P3-C16 | same-build native-preserving/ordinary source materializer, exact language/geometry/map/catalog projection, route-distinct identity and local processed-source debug dump | `v0.1` |
| P3-C17 | current-schema validation, companion/engine-setting/locale/compatibility-status/resource/custom-expression/texture-declaration fingerprint payloads, native source identity, and atomic `PackConfiguration` publication | `v0.1` |
| P3-C18 | loader-neutral diagnostics, null-reporter handling, and exhaustive load-failure classification/degradation adapter | `v0.1` |
| P3-C19 | global `.csh` source recognition/materialization reserved for G8/S2 | `post-v0.5` |
| P3-C20 | headless manifests, fuzz fixtures, and the specified Phase 2 front-end hand-off | `v0.1` |
| P3-C21 | `(internal)` in-memory source-provider bridge with canonical UTF-8 path identities (content remains Phase 7) | `v0.1` |
| P3-C22 | modern `RENDERTARGETS` recognition and source-order precedence | `post-v0.5` |
| P3-C23 | effective `MC_NORMAL_MAP`/`MC_SPECULAR_MAP` emission from the typed pre-load companion pair, with preliminary policy supplied by Phase 7 rather than completed atlases | `v0.5` |

Later consumers may initially ignore fields, but Phase 3's v0.1 model already preserves all
Appendix A.3/F data and unresolved texture declarations. D-P3-67 retains numeric duplicate
discriminators and Phase 13's v0.5 `.mcmeta` behavior, with no future required suffix parser
or typed sampling-state amendment. Unknown-key retention is not advertised support or runtime
evidence. All review/implementation gates remain. P3-C19 does not change legacy dimension semantics.

## 10. OQ & spike specifications

**Question (verbatim from RESEARCH §11 row OQ-7):**
“Renderer-identity macro + feature-flag posture”

**When:** after v0.1 can load and render the fixed matrix; final decision belongs to G8/S3.

**Procedure:**

1. Freeze identical pack versions, scenes, options, `GLCapabilityProfile`s, and engine build.
2. Use P3-C08's data switch to produce three configurations without code changes:
   - option 1: OF standard A–G identity (`MC_VERSION`, GL/GLSL, OS, vendor, renderer, and on-demand
     extension families), with no `IS_IRIS` and no Iris feature macros;
   - option 2: the same OF standard A–G identity plus `IS_IRIS` and an experimental `IRIS_VERSION`,
     with only explicitly supported feature macros;
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
**Fallback:** choose option 1 globally—OF standard A–G identity (`MC_VERSION`, GL/GLSL, OS, vendor,
renderer, and on-demand extension families), no `IS_IRIS`, no `IRIS_VERSION`, and no Iris feature
macros—while retaining `SCHMALOOGIUM` only if it is demonstrably inert for the matrix. The
configurable named-family fields remain available but disabled, so G8/S3 can revisit the decision
without a preprocessor rewrite.

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
| D-P3-8 | Reject Pintonium's Iris dimension mapping after checking §3.1/App F.8; use OF world −128…128, `.vsh`/`.fsh`-only, empty-disables, no-merge behavior. |
| D-P3-9 | Emit extensions on demand and re-derive GL/GLSL formulas plus exhaustive OS/vendor/renderer values from Phase 1 data and the shipped A–G names; Pintonium supplies only mechanism evidence and its extra macros/enumerate-all behavior are not inherited. |
| D-P3-10 | Use one declarative directive→field registry with one named test per row, because Appendix A.3 and Pintonium B1/B2 show setter chains and dead forms are unacceptable. |
| D-P3-11 | Use Oculus FE-09's deterministic case-insensitive/natural ordering as non-contract discovery structure (`.../ShaderpackDirectoryManager.java`; loader-independent). |
| D-P3-12 | Use Oculus FE-07 only to corroborate include-graph structure after checking §3.2; it supplies no depth, option, or 1.12.2-hook semantics (`.../ShaderPack.java`; loader-independent). |
| D-P3-13 | At post-v0.5, resolve simultaneous active `DRAWBUFFERS`/`RENDERTARGETS` by later source occurrence, adopting Oculus FE-08 only after confirming Appendix A.3 has no contrary precedence and retaining both locations. |
| D-P3-14 | Treat advertised option values as UI guidance, retaining syntactically safe persisted/profile values with a warning, because App F.3 does not define the list as a parser rejection set and Oculus FE-04 records OF parity. |
| D-P3-15 | Reject Oculus FE-01's original-properties semantic split because RESEARCH §3.3 says the properties file itself is preprocessed; original spans are diagnostics only. |
| D-P3-16 | Do not use an AST transformer because GLSL-120 runs natively in the compatibility profile, the fixed contract needs only preprocessing/geometry handling, and the available transformation boundary is prohibited (PD §12). |
| D-P3-17 | Publish only immutable `PackConfiguration` because a single validated downstream source prevents parser/consumer drift and is an explicit Phase 3 architecture requirement. |
| D-P3-18 | Give `shaders.properties` and ID maps the OF standard A–G family (`MC_VERSION`, GL/GLSL, OS, vendor, renderer, and on-demand extension definitions) but no option macros; shader sources additionally receive the separately named option, capability-feature, engine-identity, override, and reserved-contributor families. No undefined H family is advertised. |
| D-P3-19 | Snapshot all pack bytes and close the archive before publication because it retains bounded single-lease lifecycle value without exposing a static filesystem or stale handle. |
| D-P3-20 | Store half-life directives as ticks because Appendix A.3 is normative; alternate units belong to Phase 6's explicitly recorded conflict handling, not this parser. |
| D-P3-21 | Keep modern `.csh` recognition as post-v0.5 P3-C19 and `RENDERTARGETS` recognition/precedence as post-v0.5 P3-C22; the v0.1 routing model remains growth-shaped without implementing either modern parser path. |
| D-P3-22 | Capture complete default-block declaration metadata from the final materialized token stream and bind it to that materialization's fingerprint; load-time resource recognition is too early to describe contribution/rewrite results, while post-link introspection cannot supply source attribution or declared-but-optimized-out entries. |
| D-P3-23 | Publish file presence, entry/tag classification, per-rule era, and both isolated entity parses instead of making Phase 9 reopen bytes or infer preprocessing history; this grants R9-1 while keeping registry resolution and branch selection outside Phase 3. |
| D-P3-24 | Make `NormalizedPackPath.canonicalString()` the sole path projection and define it as validated NFC, root-relative, slash-separated grammar ordered/hashed by exact UTF-8 bytes; `toString()` and host paths are never protocol data. |
| D-P3-25 | Publish every valid custom-expression property occurrence as a closed, source-attributed, immutable ordered projection and hash that exact sequence, because RESEARCH App F.6 assigns capture to Phase 3 while Phase 11 requires duplicates and order to produce deterministic diagnostics/evaluation without reopening Properties input. |
| D-P3-26 | Represent clear-color absence as `Optional<Vec4f>` and resolve it by attachment index so runtime fog, solid white, transparent black, and explicit overrides remain distinct. |
| D-P3-27 | Treat active `gdepth` as a mandatory explicit `RGBA32F` request for colortex1; diagnose and reject any explicit format other than `RGBA32F` to preserve the Appendix A.3 upgrade. |
| D-P3-28 | Use tagged `DrawRouting.AllUsed` versus `DrawRouting.Explicit(List<DrawSlot>)`, preserving every digit or `N` as one attachment/none slot so routing positions are lossless and absence remains distinct. |
| D-P3-29 | Historical refusal to invent texture-key suffix semantics where authority supplies no grammar/value domain. D-P3-59 supersedes the deferred-publication posture with lossless owner-side capture now; the authority gate remains. |
| D-P3-30 | Serialize a filesystem selection as the typed percent-encoded direct-child reference and resolve it only against authenticated fresh discovery; opaque IDs remain live instance capabilities, never persistence data. |
| D-P3-31 | Historical producer rule: keep eight option-macro names policy-invariant, but gate companion macros by engine option and v0.5 renderer capability. D-P3-58 supersedes only those producer gates; the policy-invariant family remains. |
| D-P3-32 | Treat virtual-pre names as flip-only, expand one-token scale to zero offsets, restrict scale to executable deferred/composite, and filter flips to that family plus the two virtual slots. |
| D-P3-33 | Use the contract-required direct `shaderpacks/<pack>.txt` target and preserve safety through canonical direct-child derivation, containment checks, and symlink rejection rather than replacing the observable format. |
| D-P3-34 | Make filesystem load itself read persisted values before every option-sensitive step; a post-publication codec cannot repair an atomic configuration. |
| D-P3-35 | Co-locate package-private permitted capability classes and their issuer with the public API because Phase 1 rejects JPMS and unnamed-module sealing requires one package. |
| D-P3-36 | Evaluate runtime program state and materialize source only through `PackConfiguration`'s finalized option state, because that state alone matches the published requirements, projections, and fingerprint; arbitrary states remain preview/persistence inputs requiring reload. |
| D-P3-37 | Make the service bundle non-publicly constructible and use typed, domain-bound target/file-access acquisition so consumers cannot create mixed authentication domains or implement safe access. |
| D-P3-38 | Interpret App F.5's ordinary PNG path as a non-empty final stem plus literal lowercase `.png`; invalid occurrences degrade locally, and the narrowed published meaning requires schema 8. |
| D-P3-39 | Seal catalogs/states and authenticate exact catalog plus pack key so all option receivers can reject cross-configuration input before work while retaining safe out-of-list values. |
| D-P3-40 | Represent failed discovery with a domain-authenticated non-directory-keyed generation that never supersedes a valid directory snapshot. |
| D-P3-41 | Define executable programs as the distinct dimension/name projection of every indexed stage root; one stage establishes source presence, while compile/link success remains Phase 4. |
| D-P3-42 | Use canonical `SourceId` for every physical document, graph node, and include endpoint, reserving contextual `SourceKey` for compile roots so shared includes are never duplicated. |
| D-P3-43 | Bound directory-keyed discovery with per-result count/bytes and completion-LRU key eviction; invalid calls do not refresh retention and acquired persistence targets survive eviction. |
| D-P3-44 | Return canonical empty `EngineOptionData` when a global read request has no valid baseline, because the total non-variant read result must remain well-formed while reporting `FAILED/INVALID_REQUEST`. |
| D-P3-45 | Canonicalize resource signed zero in constructors and use the exact complete recursive producer-only codec, aligning structural equality with fingerprint input without promising a portable digest or wire format. |
| D-P3-46 | Close global data to eight typed known settings plus a bounded unknown-safe grammar, omitting typed-invalid persisted occurrences, because Phase 7/12 require deterministic request validity, overlay, and round-trip behavior. |
| D-P3-47 | Interpret one leading `%` plus a lower-case short or namespaced identifier as the unresolved tag form after checking RESEARCH §3.6.8's generic tag/shim/priority requirement; reject as numeric only an unnamespaced unslashed `[0-9]+` tag ID. |
| D-P3-48 | Serialize multiplier macro values with Java 25 `Float.toString` after typed parsing. The former positive-FXAA integer projection is superseded by D-P3-61's reserved exact-zero/no-macro contract. |
| D-P3-49 | Until authority ratifies another tag syntax, use §§4.9/5.1's complete lower-case ASCII grammar and exact four numeric-boundary outcomes so mandatory v0.1 classification is executable. |
| D-P3-50 | Use the existing 37-value `ColorInternalFormat` for raw textures as well as colortex directives because §4.8 already requires the same domain; schema 13 records the nominal component change. |
| D-P3-51 | Bind non-`Off` runtime identity to MC `(1,12,2)`/`MC_VERSION=11202` and a bounded canonical decimal `SCHMALOOGIUM_VERSION`, because the project mission is fixed to 1.12.2 and no authority defines a broader tuple or engine-version encoding. |
| D-P3-52 | Provisionally canonicalize and naturally compare bounded ASCII edition tokens under §4.8, while attributing only minimum-edition meaning to Appendix F.2, because neither contract nor permitted references define edition ordering. |
| D-P3-53 | Preserve each valid minimum-edition rule's decoded key suffix and decoded edition spelling in its accessors, using parsed/canonical forms only for comparison, because Appendix F.2 defines meaning but no canonical public projection. |
| D-P3-54 | Apply overrides after the four base families with `ADD` absent/present = insert/reject, `SUPPRESS` = no-op/remove, and `FORCE` = insert/replace; deterministic pre-I/O validation rejects malformed overrides, protected targets, and `ADD` collisions without publishing a partial map. An absent `SUPPRESS` remains an intentional no-op and therefore cannot detect a syntactically valid misspelling. |
| D-P3-55 | After checking Appendix F.4's syntax, default two columns, and beyond-18 requirement, adopt only the §G7-qualified behavior that treats configured columns as a floor and raises it to the nine-row minimum over the expanded retained slot array `[V:observed — OptiFine G6 screen behavior; behavioral-observation-only]`; this is behavior, not implementation structure. |
| D-P3-56 | Give each load-failure code one localized key and a common `ERROR`/`CHAT`, empty-argument, empty-detail, pack-log payload because Phase 1 routes pack-level failures to chat and untrusted cause data must not cross the public failure seam. |
| D-P3-57 | Close Phase 3 diagnostic arguments to four tagged boxed scalar classes and make the fixed discovery-overflow diagnostic argument-free, so exact snapshot accounting cannot depend on erased `Object` values or input-derived detail. |
| D-P3-58 | Grant Phase 13 R1 through Phase-3-owned `CompanionOptionMacros`, captured before jcpp and retained/hash-bound for the whole build, rather than a post-analysis producer or another `MacroContribution`. The shipped `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:655-656` says "When the normal map is enabled" / "When the specular map is enabled"; `docs/research/v1/RESEARCH.md:313-319` places these in the standard shader header. Phase 7 adapts preliminary policy; Phase 3 projects the supplied booleans without reapplying D-P3-31's gates. |
| D-P3-59 | Preserve every active decoded `texture.*` occurrence before collapse in schema-16 `TexturePropertyDecl`, with closed disposition, exact value, order and provenance; only Phase 3 reduces known sources or later decodes approved sampling syntax. RC3 `docs/design/v2.0-RC3/DESIGN.md:2441-2443` says "ours must honor them"; `docs/research/v1/RESEARCH.md:1484-1490` defines numeric discriminators and sidecars, not filter/wrap key grammar. Retention prevents owner-side data loss without falsely claiming honoring; U1 remains an explicit authority/consumer gate. |
| D-P3-60 | Grant Phase 4 direct existing `ProgramRequirements.mipmappedAfterPass()` and `vertices()` reads through the one configuration's dimension/name-keyed resource map, preserving Phase 5/10 consumers rather than introducing reverse dependencies or another parser. `docs/phase4/v1/PHASE_4_DOC.md:2263-2266` (§5.4 item 2) consumes these values as the adopted §0.57 grant; P4's request-time phrase "same immutable owner-defined values directly" no longer occurs there. `docs/research/v1/RESEARCH.md:1159` says "enable extended vertex attribute for this program" and `:1186` specifies "per-pass mipmap gen (composite/deferred/final)". Publication changes, not record meaning, canonical identity, schema 16, or fingerprint encoding. |
| D-P3-61 | Historical schema17 decision: tri-state global old-light settings and zero-only AA storage remain; its explicit-user-only macro projection is **superseded by D-P3-64**. No authority ratification was claimed by that amendment. |
| D-P3-62 | Publish one engine-owned inspection result from the existing load transaction, with an allowlisted source-free projection and same-read archive digest. Phase 2 owns fixture provenance and formatting; Phase 4 supplies separate same-build resolution enrichment. No engine-to-conformance dependency or hash-identity conflation. |
| D-P3-63 | Replace the single language component with the schema-18 immutable full locale catalog and §4.3's explicit local normalization/collision/fallback policy. G6 author `doc/shaders.properties:192–208,277–284,319–324` uses `en_US`; published [OptiFine shaders.properties](https://raw.githubusercontent.com/sp614x/optifine/master/OptiFineDoc/doc/shaders.properties) (read 2026-09-07, language examples) uses `en_us`. `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/LanguageMap.java:25–58,62–69` independently demonstrates immediate files, ROOT lowercase, UTF-8 Properties and immutable locale maps; its nondeterministic collision replacement is not adopted. The checkout's `LICENSE:1–4` identifies GPL v3 (not this document's historical blanket LGPL label); this amendment uses observation only and copies no code. Hyphen normalization, strict grammar, collision winner and empty-preserving per-key fallback are explicit local design decisions, not attributed external guarantees. |
| D-P3-64 | Supersede only D-P3-61's explicit-user-only macro policy: resolve user→pack→local true after the single A–G-only properties parse, before shader jcpp, and emit iff effective true. G6 `doc/shaders.txt:660–661` says “When Old Hand Light is enabled” / “When Old Lighting is enabled”; `doc/shaders.properties:9–11` excludes option macros and `:17–29` gives user priority. Published [OptiFine shaders.txt](https://raw.githubusercontent.com/sp614x/optifine/master/OptiFineDoc/doc/shaders.txt) and [shaders.properties](https://raw.githubusercontent.com/sp614x/optifine/master/OptiFineDoc/doc/shaders.properties) (read 2026-09-07, option macros and old-light settings) corroborate enabled/priority but modern properties preprocessing differs and is not backported. True absent fallback is expressly P9 D-P9-8 / P10 D-P10-11 local policy, not author evidence. Schema 18 records the changed macro meaning; OQ-7 final identity posture remains open under RC3:1351–1354 and RESEARCH:854–871. |
| D-P3-65 | Maintainer explicitly selected session-only Internal edits on 2026-09-07: preserve across selection changes within the client session, restart defaults. §4.3 grants an authenticated opaque snapshot and fresh-catalog load rebinding, no filesystem identity or durable target. RC3:1417–1421 and RESEARCH:603–605 did not supply Internal durability; this recorded maintainer choice supplies the missing product authority, not an invented external behavior. |
| D-P3-66 | Maintainer explicitly selected “Pack-option compatibility only” for `superSamplingLevel` on 2026-09-07: preserve declared option/source use, remove engine SSAA sizing/traversal/resolve effects; AA UI/runtime remain excluded. Shipped G6 `doc/shaders.properties:189` lists the const option and RESEARCH's directive inventory supplies default 1; neither defines an execution algorithm. This approved scope disposition overrides earlier phase-local engine-SSAA assignments without rewriting historical research/design. Retained resource meaning changes share schema18. It does not ratify old-light defaults or OQ-7. |
| D-P3-67 | Adopt the maintainer's 2026-09-07 “Correct requirement to documented mechanisms” decision in `docs/decisions/U1_TEXTURE_SAMPLING.md`: retain `.0`–`.9` duplicate discriminators and Phase-13-owned `.mcmeta` blur/clamp, remove unspecified filter/wrap key-suffix requirements. Published [OptiFine custom-texture documentation](https://raw.githubusercontent.com/sp614x/optifine/master/OptiFineDoc/doc/shaders.properties) explicitly separates those mechanisms; [Iris `.mcmeta` documentation](https://shaders.properties/current/reference/buffers/custom_textures/#mcmeta-file) corroborates blur/clamp and resource/atlas exclusion. Supersede only D-P3-29/D-P3-59's pending authority/future typed-suffix obligation; preserve their historical evidence and the complete lossless stream. Existing parser/payload/defaults/fingerprints stay unchanged, hence schema18; no blanket defaults/error-policy ratification or runtime conformance claim. |
| D-P3-68 | Answer P1 §11.4/P4 §5.4 with native-preserving geometry materialization, not an incomplete core translator. Published ARB rev26/GLSL1.20 rev8/GLSL1.50 rev11 evidence and notices are §0.61; same-build native source, exact final catalog/map, explicit source-layout precedence and schema19 clean cutover are §§2.2/4.5/4.10/5.1. This local API decision is not claimed runtime behavior; topology owner/conformance and review gates remain. |
| D-P3-69 | 2026-09-08 TS-1 owner grant: publish schema20 `PackConfiguration.assets` over bounded same-load owned bytes, with declared-path Acquired/Missing/Unreadable/InvalidReference results, read-only heap cursors and metadata-only acyclic fingerprint/inspection. Refines D-P3-19 only for authenticated optional sidecar-only read markers under `docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md`; all safety/bounds/index/primary/config failures remain fatal. P13 owns recovery diagnostics and interpretation; P7 retains exact assets across resource-only NONE. No host reopening, opaque provider, binary artifact or runtime/PASS claim. |
| D-P3-70 | 2026-09-08 R55 fix-up: preserve usable base OR explicit override under ordered failure precedence; map exact external `TEXTURE_RECTANGLE` to existing `TextureTarget.RECTANGLE` (bare `RECTANGLE` invalid); replace named selector with `ScreenProfileEntry()`, deriving current/Custom solely through existing inference. Nested shape advances schema21 including ID/inspection and materialization identity; D-P3-69 assets/nine-tree meanings stay unchanged. §§5.3/11.4 require coordinated receipts and fresh reviews, not implementation clearance. |
| D-P3-71 | 2026-09-08: receive P4 D-P4-31's required ownBuild disposition in the separate same-build registry enrichment adapter. Copy exact P4 classification without inferring it from sourcePresent/effective CHAIN. P3 snapshot trees/schema21 and golden/projection versioning remain unchanged; P2/P7 own runtime /3 serialization and T3 interpretation. |
| D-P3-72 | 2026-09-08 P9 R1 C1 producer grant: extend the existing copied-byte/private-environment forced11300 mechanism to present BLOCK requests. Ordinary results/state stay classic and unchanged; only explicit alternate rules are MODERN, ITEM/LAYER alternates stay empty. P9 selects alternate only for PRESENT_EMPTY plus nonempty alternate, independently per contribution, never by ID/name heuristic or merge. RESEARCH §3.7 supplies classic preprocessing, not this local compatibility extension. Changed meaning requires schema22, current nested/inspection equality and MaterializedSource-v22; all unrelated contracts remain. §§4.9/5/8/9/11/12 define complete production, evidence and receiver obligations; fresh reviews remain required. |
| D-P3-73 | 2026-09-08 C57-1: replace scalar metadata and literal-only predicates with immutable ordered metadata interval unions and typed Literal/IntegerInterval property alternatives, one rule per selector and no range expansion. Shipped G6 `doc/shaders.txt:544` explicitly incorporates `doc/properties_files.txt:97–122` sets/ranges/conjunction; exact API/normalization/ambiguity/bounds/rejection rules are local policy in §4.9, not source implementation claims. Schema23 and MaterializedSource-v23 bind the changed nested graph; P9 alone resolves live values, P2 projects typed metadata without new strings/trees. §§5.3/11.4 require dated receiver receipts and fresh reviews; no health IDs or unrelated semantics change. |
| D-P3-74 | 2026-09-08 R60 C1: make §4.4's per-pack override target whitelist authoritative — protected targets are exactly non-version identity/capability names, so §4.5's reserved intrinsic namespace (`__VERSION__`, `__FILE__`, `__LINE__`, `GL_core_profile`, `GL_compatibility_profile`, `GL_ARB_geometry_shader4`) and any other non-whitelisted name are rejected with the same pre-I/O `INVALID_REQUEST`, resolving the blocklist/whitelist conflict with no `PackConfiguration` component, meaning, fingerprint or schema change. |

  D-P3-64's effective-mode projection, shared true fallback, unchanged `default` wire baseline,
  distinct DEFAULT/false fingerprint identity and zero-only AA storage were explicitly ratified
  by the maintainer on 2026-09-07. This is product authority, not a fabricated author-doc default.
  The ratification closes IR-24's narrow old-light question and does **not** choose OQ-7 identity.

### 11.2 Binding-decision disposition

D-1/D-2 are preserved by the Cleanroom-exclusive, shaders-only boundary. D-3 controls the seven-pack
gate. D-4 is preserved by growth-shaped source/stage fields and post-v0.5 compute recognition.
D-5 is untouched: this phase has no mixin. D-6 is enforced by pure `:engine` placement. D-7/D-8
govern jcpp/Pintonium/Oculus notices and the transformation prohibition. D-9 supplies compatibility
profile macro semantics without GL calls. D-10 is implemented by Phase 3's row-complete manifests
and fixtures handed to Phase 2, whose adapter/job owns `:conformance`.

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
6. **Texture sampling suffixes (U1), corrected by D-P3-67.** RC3's historical Phase 3 gap
   statement and “ours must honor them” requirement conflicted with the documented mechanisms:
   RESEARCH Appendix F.5 and the shipped/published OptiFine text specify numeric duplicate
   discriminators and `.mcmeta`, not filter/wrap key grammar. The maintainer explicitly selected
   the authority-correction route in `docs/decisions/U1_TEXTURE_SAMPLING.md`; §0.60 and §5 adopt
   it for Phase 3. Historical D-P3-29/D-P3-59 and §0.56 evidence remain intact, while their
   pending suffix-authority obligation is superseded. The lossless stream, `UNRESOLVED_KEY`
   diagnostics and typed sources/sidecar paths remain unchanged. This is neither a claim that
   no extension syntax can exist nor a promise to support unknown extensions. Phase 13 owns
   `.mcmeta` behavior and its independent review; no runtime evidence or broad default
   ratification follows from this narrow documented-mechanism correction.
7. **Two-span core rewrite versus native language.** RESEARCH §3.1 requires the documented
   ARB alternative as well as core layouts. Removing its extension and `maxVerticesOut`
   declaration does not translate GLSL version, `gl_VerticesIn`/input arrays, varyings or uses
   of that constant. Published ARB rev26 independently confirms native forms and source-over-API
   property precedence on GL3.2. D-P3-68 removes the advertised incomplete translation and
   preserves native source. This correction is not a new shader transformer or topology policy;
   maintainer-authorized P7/P10 submission remediation has its own owner and evidence gates.

### 11.4 Open items and hand-offs

- Phase 6 adopts `MacroContribution.Empty` for its CPU center-depth path; the reserved slot
  remains available, not an outstanding mandatory producer decision.
- Phase 4 must merge the verified per-stage `DeclaredUniformCatalog`s into one immutable effective
  linked-program layout, reject same-name/different-type conflicts with both source locations, and
  expose only that handle-free layout to Phase 6. Phase 6 must not reopen source or reinterpret
  the Phase 3 type algebra.
- For source materialization and program-state evaluation, Phase 4 calls
  `SourceMaterializer.materialize(root, contribution, geometry)` with `GeometrySourceRequest` and
  `PackConfiguration.evaluateProgramStates(selectedProfile, diagnostics)` only; both use the same
  finalized option state that produced the configuration's resources and fingerprint; materialization
  also retains that load's companion pair and hashes it. It never calls or reconstructs a raw-model
  evaluator, passes preview state, or patches a later companion decision into shader text.
- Phase 4 obtains per-program `mipmappedAfterPass` and `VertexRequirements` directly through
  §5.1's existing resource-map accessors and exact dimension/name join. Phase 5 pass-mipmap and
  Phase 10 vertex-layout consumers retain the same values and responsibilities. Phase 4's
  integration amendment adopts the direct grant/current schema and reconciles its ledger;
  fresh reviews remain required, with no backward Phase 5/10 route or source reparse.
  D-P3-68's native source grant now requires consumer adoption; other dependency gates remain (§5.4).
- Phase 7 must supply the bounded `InternalPackSource`; for filesystem loads it obtains
  `PersistenceFileAccess` from its factory bundle using the exact game/shaderpacks roots and passes
  only that same-domain receiver into `load`. It persists only
  `FilesystemCandidateReference.canonicalValue()`, performs fresh discovery/resolution after
  restart/reload, supplies an explicit `CompanionOptionMacros(false,false)` at v0.1, and adapts
  Phase 13's preliminary companion state before load at v0.5. Renderer-feature availability remains
  request metadata, not an alternative emission gate. A changed pack or option/policy input
  requires a fresh configuration. P7's resource-only `NONE + resourceReacquire` path preserves
  it while refreshing quiescent resources/IDs; dimension selection uses its existing map.
  Content hashing/serialization uses only `NormalizedPackPath.canonicalString()`.
- Phase 7 selects the published `DimensionConfiguration` by key: an absent world entry falls back
  to base, an override is source-exclusive, and a disabled entry never falls back or merges.
- Phase 9 consumes `IdMappingInput`, selects BLOCK/ENTITY ordinary/forced results under the documented
  present-empty rule, parses bounded mod bytes only through the published operation/environment,
  and owns mod precedence, tag expansion, registry resolution, and aliases.
- Phase 11 consumes `PackConfiguration.customExpressions()` only after the latest Phase 3 revision
  and latest §5 surface receive a fresh literal-PASS review; it owns expression grammar/evaluation
  and expression-relative spans, including the D-P11-27 deterministic first-owner
  duplicate/collision policy and its exact DUPLICATE_NAME/UNKNOWN_NAME diagnostics, and must not
  reopen or reparse Properties bytes.
- Phase 12 obtains receivers and persistence access through `PackFrontEnds.create`. Filesystem
  Apply derives only an authenticated `Acquired` target, writes safe persistence and requests a
  fresh load. Internal Apply/profile/reset uses P7's D-P3-65 commit adapter and no pack target.
  It handles every closed failure without mutating an existing configuration and never supplies
  preview state to its materializer/evaluator.
  It owns discard timing and global-setting UX and uses the retained tooltip
  marker for warning/red classification and display-only removal.
- Phase 13 receives unchanged typed source specs, sidecar references and the lossless attributed
  declaration stream. D-P3-67 adopts numeric discriminators plus `.mcmeta`, not a future typed
  suffix request. Unresolved declarations remain diagnostic data only; no consumer reparses
  properties or splits unknown keys. Phase 13 owns sidecar interpretation/application and adopts
  the authority correction separately; fresh reviews and runtime evidence remain required.
- Phase 13 R1 and P1 package placement are owner-designed and receiver-adopted by the
  coordinated integration amendment, as are Phase 4's direct projections. Current schema
  consumption and all changed §5 surfaces remain unverified. D-P3-68 supplies native source,
  not jcpp admission, consumer adoption, post-analysis R4 or draw-conformance evidence; see §5.4.
- Phase 2 adopts `inspect`/snapshot/archive-provenance in §5.1.1 and P1 diagnostic permission;
  P4 adopts the same-request resolution enrichment. All are architectural receipts, not
  completed matrix evidence. P2 alone owns golden formatting, acquisition metadata and verdicts.
- **R-P12-5 owner grant complete.** P12 replaces all `lang()` calls with the full
  `localizedDecorations()` catalog and §4.3's per-key fallback; P2 projects the entire catalog
  using the existing source-free codec. Neither consumer may synthesize schema18 from schema17.
- **Internal session grant complete (D-P3-65).** P7 owns accepted snapshots for the client
  session across switches; P12 submits Apply/profile/reset to P7's commit adapter, which captures
  through §4.3's closed operation. All non-Off callers supply the Optional; Filesystem uses empty.
  `PackOptionsTarget` still rejects Internal. Restart starts defaults and no session token is
  serialized. Closed rejection and failed-load retention rules replace the former inert controls.
- G8/S3 owns OQ-7's final policy after §10's spike.
- G8/S2 consumes P3-C19; it must not alter legacy dimension-file rules silently.
- **D-P3-68 producer grant / exact receiver migration.** P4 removes all translator plan/request/
  rewrite-site imports, requests and presumed proof tokens. Join the existing selected program
  requirement to its `.gsh` root: a recognized pair selects `PreserveNative(config)`; otherwise
  select `None`; VSH/FSH use `None`. Materialize each present stage once with the same contribution.
  Reject any unavailable stage as a complete program failure before publishing handles. Validate
  catalog/source fingerprint equality and merge every final default-block declaration exactly as
  before, rejecting same-name unequal structural types with both locations. No source reopen.
  `geometry().CoreLayout` needs the existing GL3.2/core-source route; `NativeLegacy` requires the
  actual ARB extension even on GL3.2+, and any nonempty source-layout declarations additionally
  require GL3.2+. Preserve `language()`/text/map from all stages. On a fresh unpublished program,
  before first link, call P1's already-granted
  `configureLegacyGeometry(program, TRIANGLES, TRIANGLE_STRIP, native.config().maxVertices())`
  under P1's owning-context/error/one-time setup rules. Do **not** pass source-overridden counts/
  primitives into that narrow API. Retain source `effective` as declarative input; P1's successful
  link captures authoritative linked input for conditional drawing. P4 adopts these source types
  into its own strategy ledger/contracts, not an invented downstream surface in this document.
  Preserve its diagnostics, deletion, validation and complete binding fallback for native errors.
- **Current-schema propagation, no semantic collateral.** P1 re-reads changed §5 and owns its existing
  pre-link operation/jcpp admission and backend linked metadata; no additional facade is requested.
  P2 adopts current-schema and the renamed included-site projection through the existing source-free
  inspection codec, plus its own golden/revision evidence; never exports shader text.
  P4 changes strategy/materializer/catalog/source-map consumers as above. P5/P6/P8 adopt containing
  current-schema for unchanged sizing/depth/shadow values; P6's contribution and merged catalog interface
  stay unchanged. P7 adopts current-schema for atomic loads/reloads and owns orchestration with P10's
  separately authorized submission adapter; it never rescans GLSL. P9 rejects old/mismatched nested
  schema before unchanged ID mapping. P10 consumes its unchanged vertex requirements and obtains
  draw compatibility through the owning P1/P4/P7 contracts, not P3 source text. P11/P12/P13 adopt
  containing current-schema while preserving expression, locale/option/Internal-session and U1/texture
  semantics. P14's eventual acceptance/inspection consumes the reviewed current schema only.
  Consumer receipts, fresh independent producer/consumer whole-document reviews and final
  integration remain mandatory; P3 cannot declare them completed. Earliest native `.gsh` support
  must include native source and conditional draw compatibility together, not defer either as a
  silent later-milestone escape. No code/runtime evidence is supplied by this architecture amendment.
- **TS-1 acquisition receivers, 2026-09-08 — adopted/unverified.** P13 D-P13-30/§4.3.2/§5.2
  consumes the exact D-P3-69 capability/results, primary-before-sidecar failure precedence,
  read-only original bytes, atomic sidecar-only recovery and one diagnostic. P7 D-P7-33/§5.2
  retains the same configuration/assets across preparation, dimension metadata and resource
  NONE; full load alone replaces pack bytes. P2 D-P2-55 preserves all nine source-free
  projection trees, with assets mapped to `[properties] owner.assets`, never decoded bytes.
  These are current receiving contracts, not fresh whole-phase verification or runtime proof.
- **Historical R55/schema21 receiver handoff — 2026-09-08, owner-designed/unverified.** P12 must consume
  payload-free selector declarations, existing declaration-ordered profiles and separate
  preview-state inference; no fabricated selection/name. P2 must consume the zero-component
  selector through the same generic source-free codec, preserving all nine trees and
  `[properties] owner.assets`. P7/P4 must preserve dimension-only success and exact no-merge
  selection; P13 must receive typed rectangle sources/acquirable bytes, never parse an alias.
  That amendment requested each receiver named in §5.3 to record a dated schema21 receipt with exact-current,
  containing/nested identity checks before derived state, including current materialization
  identity and cache retirement where owned. P1 rereads changed §5; its facade does not change.
  This owner document does not certify sibling adoption. Separate producer/consumer reviews,
  all applicable phase gates, IR-01 and final integration remain required.
- **Historical D-P3-72/schema22 receiver handoff — 2026-09-08, owner-designed/unverified.** P9 must
  adopt §4.9's same-source BLOCK producer and exact PRESENT_EMPTY/nonempty-alternate selection
  in §§4.6/5.2/5.4, its resolved identity and v0.3 evidence plan; an assigned MODERN test record
  is not producer evidence. Parse selected-pack and bounded mod bytes through P3, retain actual
  provenance, and trace the selected grass/lamp rules to the live validated aliases. No P3 registry
  lookup, ordinary-era inference, alternate merge, layer retry or extra parser API is granted.
  All receivers listed in §5.3 must adopt current schema22 before deriving/retaining/serializing
  their owned state, rejecting old/future/mismatched configuration/nested ID/inspection schemas.
  P4 retires incompatible materializations/caches under MaterializedSource-v22; P2 keeps
  projectionVersion=1 and the nine source-free trees, including both ID lists and actual
  attribution under the unchanged codec. P7 preserves exact same-load assets and its existing
  reload/retirement boundary. Other resource/native/option/texture contracts remain unchanged.
  Older numeric receipts are historical; no receiver adoption or new PASS is asserted here.
- **D-P3-73/schema23 receiver handoff — 2026-09-08, owner-designed/unverified.** Main must
  amend P9 §§4.5/5/identity and evidence plans to consume
  `Optional<MetadataConstraint> legacyMetadata`, `List<IntegerRange> alternatives` and
  `PropertyPredicate.acceptedValues: List<PropertyValueConstraint>` (`Literal(value)` /
  `IntegerInterval(range)`) with §4.9's exact finite-domain membership/conjunction and whole-selector
  failure semantics. Keep one rule/provenance ordinal, existing precedence and independent
  alternate selection; no raw selector/Properties parser, expansion or inferred MODERN era.
  P9 derived identity must retain all typed constraints/order/provenance plus its chosen parse
  and live snapshot; consume §8.1's actual pack/mod/ordinary/alternate/layer producer cases.
  Main must amend P2's explicit ID projection dispatch for all new record/variant fields using
  §5.1.1, preserving TextHash for property/literal/selector strings and IntegerValue for interval
  endpoints, all nine trees and `[properties] owner.assets`; no additional health row is granted.
  All §5.3 receivers need exact-current **23** containing/nested/inspection admission before
  owned derivation/reuse/serialization, and current MaterializedSource-v23 retirement where owned.
  Prior numeric receipts, including D-P3-72's above, are historical, not current acceptance gates.
  These required receiving amendments and independent owner/receiver review are pending Main
  integration; this owner resolution does not certify them or grant implementation clearance.

### 11.5 Requested upstream changes

1. **P1 jcpp grant adopted/unverified:** consume §§4.2.4b/5.1/D-P1-49 under this §5.4;
   verify the exact implementation pin, closure, seam, notices and once-only packaging before code.
2. RC3/RESEARCH clarification: state explicitly whether `shaders.properties` receives option
   macros. This design follows the shipped A–G-only rule pending an upstream contract change.
3. **U1 — authority correction selected and adopted by Phase 3 (2026-09-07).** The maintainer
   selected “Correct requirement to documented mechanisms” in
   `docs/decisions/U1_TEXTURE_SAMPLING.md`, the explicit correction route previously requested
   here. D-P3-67 adopts existing numeric duplicate discriminators and Phase-13-owned `.mcmeta`
   blur/clamp, removing the unspecified property-key filter/wrap requirement. No future grammar
   grant, typed sampling schema or parser expansion is required. Existing source forms, lossless
   declarations and `UNRESOLVED_KEY` behavior are unchanged within current-schema; U1 itself added no schema.
   Phase 13 records its own adoption; each changed §5 surface requires fresh independent review,
   and IR-01 remains. Existing sidecar defaults/error policy are not newly ratified, and no
   implementation or rendering conformance is claimed. Historical authority/research/reviews
   and prior addenda remain evidence rather than being rewritten to imply this earlier decision.
4. DESIGN/RESEARCH clarification: either standardize the concrete `%` tag-selector spelling,
   canonicalization, and rejection grammar in D-P3-47 or publish a different pack-visible syntax;
   §3.6.8 currently governs only the generic 1.12 shim and entries-before-tags priority.
5. RESEARCH/App F.2 clarification: ratify or replace D-P3-52's edition grammar,
   canonicalization, comparator, aggregation, and malformed-rule policy; Appendix F.2 currently
   specifies only that `version.<mcver>` declares a minimum edition.
6. **IR-24 resolved; broader OQ-7 remains.** D-P3-64's author-evidence-backed enabled semantics
   and existing shared true fallback were explicitly ratified by the maintainer on 2026-09-07,
   with wire `default` baseline, raw-preference fingerprint distinction and no aliases.
   No narrow default/macro ratification request remains. The broader renderer-identity/feature
   posture experiment remains G8/S3's OQ-7 responsibility, not approval by this phase.
   The maintainer's 2026-09-07 D-P3-66 disposition resolves the AA/SSAA inventory conflict as
   pack-option compatibility only: no engine SSAA; reserved exact-zero global AA storage,
   omitted FXAA macro, and no AA control/runtime. Historical inventory text remains immutable
   evidence, not an active eight-visible-control promise.

## 12. Implementation checklist

Each item is independently actionable and names its test hook.

1. `[v0.1]` Implement P3-C01/P3-C02 and the dependency-free `PackFrontEnds.create()` bundle:
   return a final bundle with four non-null readable receivers and no public constructor, enforce
   one authentication/lifetime domain, apply candidate/byte discovery limits and completion-LRU
   directory retention, issue legal same-package sealed tokens, implement directory-keyed,
   overflow, evicted, and invalid generations with exact outcomes, canonical durable references,
   safe roots, archive limits, and snapshot lifecycle; run
   `discovery_candidateAndByteLimitsReturnBoundedSnapshot`,
   `discovery_retentionCompletionLruAndFreshRecovery`,
   `discovery_concurrentEvictionCompletionOrder`,
   `discovery_evictedGenerationOutcomesAndAcquiredTargetLifetime`,
   `discovery_invalidGenerationOrderingAndPublication`,
   `publicServices_factoryDependenciesLifetimeAndOwnership`,
   `publicServices_nonPublicConstructionAndDomainIsolation`,
   `discovery_tokenIssuanceForgeryAndOwnership`, `discovery_referenceRestartResolutionOutcomes`,
   all other `discovery_*`, `normalizedPackPath_canonicalStringStableUtf8`, `pathRejects*`, and
   `archiveLease*`.
2. `[v0.1]` Implement P3-C03 source-set/dimension indexing with the closed
   `DimensionKey`/`DimensionConfiguration` map, full −128…128 scan, explicit absent fallback,
   and disabled entries; run all `dimension_*`.
3. `[v0.1]` Implement P3-C04 canonical physical source IDs/documents/include edges, root-only
   contextual keys, WCC/depth/cycle diagnostics; run `include_depthTenAndCycle`,
   `include_graphAllRootsStable`, `sourceCatalog_sharedIncludeCanonicalPhysicalIdentity`, and
   `optionRefsDoNotCrossWcc`.
4. `[v0.1]` Implement P3-C05 include expansion and numeric `#line` source maps before macro setup,
   expanded-stream option rewriting, and conditional evaluation; run `include_*Attribution`,
   `optionRewrite_includedChangedOptionOrder`, and `preprocess_lineAttribution`.
5. `[v0.1]` Exercise the adopted Phase 1 jcpp pin/closure/seam grant and record its exact
   dependency/runtime/notice evidence, then implement P3-C10 and D-P3-68's native-preserving source algebra, intrinsic
   environment and complete attributed pair/layout discovery; run all `preprocess_*`, `macro_*`,
   and `geometryNative_*` behavioral cases. No incomplete translator may be implemented as success.
6. `[v0.1]` Implement P3-C08's fixed `(1,12,2)`/`MC_VERSION=11202` validation, canonical decimal
   `SCHMALOOGIUM_VERSION`, exact GL/GLSL formulas, exhaustive OS mapping, ordered vendor/renderer
   classifiers, ordered option projection, Java-25 numeric serialization, six effective option
   macros, complete eight-name family, §4.4's full override action/state matrix and diagnostic
   precedence, protected targets, on-demand extensions, and Phase 6 contributor; run every
   `macro_*` boundary/classifier/action test and prove failures precede I/O and the explicit v0.1
   false pair omits both companion macros. `[v0.5]` implement P3-C23 from the pre-load typed pair;
   run `macro_companionTypedStateBeforeJcpp`, `macro_companionMissingNonOffAndOffShortCircuit`,
   and `macro_companionSameBuildAndFingerprints`; do not add a contribution variant or later patch.
7. `[v0.1]` Implement P3-C06's sealed catalog/state identity, exact construction/update/validation,
   preview-only profile inference, finalized-state-only runtime materialization/evaluation, and
   remaining option/profile/screen models, including D-P3-55's expanded-slot configured-column
   floor; run every F.3/F.4 test, `screen_columnsExpandedSlotFloor`,
   `optionState_catalogBoundConstructionUpdateValidation`,
   `optionState_previewAndPersistenceRejectForeignAndSameDomainCrossPack`, and
   `runtimeOptionState_requiresReloadForCoherentSnapshot`.
8. `[v0.1]` Implement P3-C07's typed target/root acquisition, same-pack catalog/state validation,
   sealed safe access, direct-name codecs, exact global result matrix/value domain/all-entry output,
   and load integration; prove every mismatch returns before I/O, then run all `persistence_*`
   tests, especially the global domain, occurrence-classification, exact-escaping, and initial/
   reload cases.
9. `[v0.1]` Implement P3-C11 protected-token property preprocessing, including `#`/backslash/
   whitespace preservation and narrow hash-comment handling; run `propertyHashRoundTrip` and all
   `properties_*`.
10. `[v0.1]` Implement P3-C14's complete documented Appendix F dispatcher/model, D-P3-52's
    provisional version-key/edition grammar and comparator, D-P3-53's decoded accessor projections,
    D-P3-38 texture-path interpretation, exact `TextureSidecarRef.path()` production,
    source-projected profile/program evaluation, and immutable public state graph. Capture every
    active decoded `texture.*` occurrence before reduction, retain unresolved/invalid declarations
    without stripping, and derive only documented source specs under the §5.1 disposition rules;
    Under D-P3-67 keep `.0`–`.9` as duplicate discriminators only; pass unchanged typed sources
    and sidecar references to Phase 13. Implement no invented filter/wrap key parser, sampling
    API, shim or schema synthesis. Phase 13 owns `.mcmeta` blur/clamp and its runtime tests.
    run `compatibilityVersionAndEditionGrammarComparatorVectors`,
    `minimumEditionRule_sourceAccessorProjections`,
    `texture_declarationsLosslessBeforeReduction`, `texture_unresolvedNeverAliasesBase`,
    `texture_lastValidProjectionKeepsAllDeclarations`, `texture_declarationFingerprintAndLifetime`,
    `texture_sidecarIndependentOfUnresolvedKeys`,
    `programState_profileAwareAggregateEvaluation`,
    `programState_executableProjectionMembership`,
    `programState_publicGraphConstructorsImmutabilityAndAccessors`,
    `texture_packPathPngCaseAndLastValid`, `texture_sidecarPathAccessorCustomRawNoise`,
    `texture_publicNominalAlgebraProducerConsumerCompatibility`, every `customDecl_*`,
    `programState_scaleArityDefaultsRangeAndFamilyOrdering`,
    `bufferNameNormalizer_allCanonicalLegacyAndRejected`,
    `bufferNameNormalizer_allParserFamilies`,
    `programState_flipFamilyVirtualPreAndInvalidBuffer`, and the properties-model test; make the
    manifest fail on any Phase-3-owned parser/model row missing from §3.1/§3.2.
11. `[v0.1]` Implement P3-C12's table-driven declaration/const/block-comment/line-comment and
    positional legacy `DRAWBUFFERS` routing scanner with exact program-family predicates; run the
    leading/middle/trailing/repeated/all-`N` tests and make the v0.1 manifest fail on any missing
    v0.1 §3.3 row, including B1/B2 and
    `directive_colortexClearFamilyFilterAndOrdering`,
    `directive_colortexClearColorFamilyFilterAndOrdering`, and
    `directive_colortexMipmapFamilyFilterAndOrdering`. `[post-v0.5]` Implement P3-C22 and enable
    `directive_rendertargetsAndPrecedence`.
12. `[v0.1]` Implement P3-C13's exact public leaf declarations, attachment-format and positional
    routing algebras, requirement folding, program-family filtering, cross-field diagnostics, and
    complete canonical resource codec and §5.1's direct Phase 4 projections without changing
    Phase 5/10 values; run `resource_programProjectionSnapshotAndIdentity`, every `resourceLeaf_*`,
    `directive_attachmentFormatDefaultExplicitAndGdepthFold`,
    `fingerprint_attachmentFormatVariantAndExplicitValue`,
    `fingerprint_resourceCodecEveryLeafAndInsertionOrder`, all routing-slot tests, eligible/wrong-
    family orderings, and hand-verify one classic pack's resource requirements.
13. `[v0.1]` Implement P3-C15's current-schema `IdMappingInput`, four per-kind file states, pure
    bounded-byte parser, exact provisional tag identifier and numeric-short exclusion grammar,
    classic/modern provenance, entry/tag classification, per-rule era, and isolated forced-11300
    BLOCK and ENTITY results; exercise §8.1's real conditional BLOCK producer traces through P9,
    alternate failure/isolation, D-P3-73's three typed-range boundary plans, and all existing
    `idMap_*` numeric/long/short/property/legacy/layer cases without rule or range-member expansion.
14. `[v0.1]` Implement P3-C16's source-catalog snapshot/materializer access, exact
    `executablePrograms()` projection, retained same-build companion macro snapshot, materialization
    cache and both-boolean fingerprint payload, complete fingerprint-bound `DeclaredUniformCatalog`,
    exact transformed-text/source-map/language/geometry projections, route-distinct identity and local-only dump;
    run `sourceCatalog_orderedRootsSourcesIncludesAndMaterializer`,
    `sourceCatalog_programProjectionStagesDimensionsAndAbsence`,
    `sourceMap_numericLookupAndMaterializedLifetime`,
    `materializedSource_transformedTextAndSourceMapAreExactPair`, every
    `declaredUniformCatalog_*` test, and assert dumps never enter golden manifests.
15. `[v0.1]` Implement P3-C18 fixed-channel logging, §4.10's closed diagnostic codec, and §5.1's
    exact load-failure diagnostic/cause matrices; inject every cause and precedence overlap, assert
    every code-selected field and exact primary-value delivery once to each non-null reporter, and
    prove a null reporter returns its table-defined primary `INVALID_REQUEST` with no callback,
    I/O, or throw.
16. `[v0.1]` Implement P3-C17 current-schema validation/fingerprint/atomic publication, including
    canonical physical source identity, catalog-bound option state, executable-program projection,
    renderer features, both companion booleans, computed compatibility status, the closed custom-texture nominal algebra,
    complete resource codec, custom-expression and texture-declaration payloads; prove `PackConfiguration` is the only
    success output, run `fingerprintChangesWhenMinimumEditionCrossesCompatibility`, and run
    schema-12/schema-13, schema-13/schema-14, schema-14/schema-15, schema-15/schema-16, and
    schema-16/schema-17, schema-17/schema-18, schema-18/schema-19, schema-19/schema-20,
    schema-20/schema-21, schema-21/schema-22 and schema-22/schema-23 producer-consumer rejection directions.
    Implement D-P3-69's required same-load assets, typed sidecar-only failure classification,
    shared read-only cursors, full metadata fingerprint and source-free inspection; exercise
    §8.1's acquisition/lifetime/bounds/sidecar/resource-only cases before claiming this contract.
17. `[v0.1]` Implement P3-C21's in-memory `(internal)` bridge with a synthetic engine-only pack;
    hash/order/serialize only canonical-string UTF-8 bytes and leave actual content to Phase 7.
    Implement D-P3-65 capture/rebind/session identity failures and D-P3-63 full locale acquisition,
    fingerprint and source-free projection; P7 owns session retention, P12 final presentation.
18. `[v0.1]` Implement P3-C20 manifest/fuzz/fixture emission and the specified Phase 2 hand-off;
    Phase 2 runs the seven downloaded pack front-ends and the classic resource-sizing check under
    its existing harness rules.
19. `[post-v0.5]` Run §10's OQ-7 spike and record the result before implementing P3-C09's final
    policy.
20. `[post-v0.5]` Implement P3-C19 global base-folder `p.csh` and `p_a`…`p_z` recognition for
    each known non-gbuffers program `p` when G8/S2 defines execution; retain associated
    `programName=p`, distinct physical IDs, `COMPUTE` materialization, and OF dimension restrictions,
    then run `sourceCatalog_computeRootsApplicabilityAndDimensions`.

---

*Review round 31 returned literal PASS on §0.32. Round 32 reviewed §0.33 and produced §0.34;
round 33 reviewed §0.34 and produced §0.35; round 36 reviewed §0.35 and produced §0.36; round 37
reviewed §0.36 and produced §0.37; round 38 reviewed §0.37 and produced §0.38; round 39 reviewed
§0.38 and produced §0.39; round 40 reviewed §0.39 and produced §0.40; round 41 reviewed §0.40
and produced §0.41; round 42 reviewed §0.41 and produced §0.42; round 43 reviewed §0.42 and
produced §0.43; round 44 reviewed §0.43 and produced §0.44; round 45 reviewed §0.44 and produced
§0.45; round 46 reviewed §0.45 and produced §0.46; round 47 reviewed §0.46 and produced §0.47;
round 48 reviewed §0.47 and produced §0.48; round 49 reviewed §0.48 and produced §0.49; round 50
reviewed §0.49 and produced §0.50; round 51 reviewed §0.50 and produced §0.51; round 52 reviewed
§0.51 and produced §0.52; round 53 reviewed §0.52 and produced §0.53; round 54 reviewed §0.53 and
produced §0.54. Phase 3 v1 is not verified pending a fresh whole-document review; no version roll
occurs while the loop is open.*

*The maintainer-authorized §0.55 R1 amendment changes the incorporated §5 load, macro, materialization,
fingerprint, and schema contracts after that history. Phase 3 v1 remains unverified and requires a
fresh whole-document verify session before dependent consumption. No review or directory was changed.*

*The maintainer-authorized §0.56 U1 amendment adds lossless texture declaration capture, reduction,
fingerprinting, and schema 16 to the incorporated §5 surface. Fresh whole-document verification is
required before dependent consumption. Suffix-honoring authority and downstream adoption remain
explicitly ungranted under §§5.4/11.5; no review, authority document, or directory was changed.*

*The maintainer-authorized §0.57 amendment grants Phase 4 §5.4 item 2 on the owner side through
the existing immutable program-requirement accessors. Schema 16, canonical identities and both
fingerprint semantics remain unchanged. Phase 3 remains unverified: changed §5 requires fresh
whole-document verification before dependent consumption. Phase 4 adoption and separate ungranted
dependencies remain explicit in §5.4; no review, build/test/verification run, or directory roll
was performed.*

*§0.58 integration amendment changes the active §5 surface to schema 17, tri-state/zero-only
global settings and source-free inspection with P4/P5 enrichment. All changed owner/receiver
documents remain unverified pending fresh review. Native-source, jcpp, U1, OQ-7/no-AA authority
clarification and R-P12-5 locale projection gates remain explicit. No tests or verification ran.*

*§0.59 supersedes locale/Internal gates and explicit-user-only old-light policy with schema18
owner contracts, including approved session-only Internal edits and option-only sampling scope.
Only broader OQ-7 identity/feature posture remains open for this slice. No historical
review or audit was rewritten. No builds, tests, formatters or validation ran; Main owns validation.*

*§0.60 adopts the maintainer's documented-mechanism U1 correction under D-P3-67, superseding
only prior pending suffix-authority/future typed-publication requirements. Schema18 and the
published parser/payload remain unchanged. Fresh independent whole-document verification and
IR-01 remain outstanding; no PASS, runtime test or blanket defaults ratification is claimed.
No code, historical review, research/design document, build, test, formatter or validation
command was changed or run in this Phase 3 adoption.*

*§0.61/D-P3-68 closes the P3 native-source API absence with schema19 and removes the incomplete
translator from active contracts. Published-spec evidence is separately attributed; no shader
implementation, code, build, test, formatter or validation command ran. Main owns settled
document validation and consumer integration. New conditional primitive-adapter permission does
not complete P7/P10 owner/conformance work. Fresh independent reviews and remaining implementation
gates remain; neither this grant nor successful preprocessing is a PASS or draw-support claim.*

*§0.63/D-P3-70 records all three R55 repairs and schema21. R55's original
PASS-WITH-CORRECTIONS remains historical with append-only Resolutions, not self-upgraded PASS.
Phase 3 remains unverified because §5 changed; receiver receipts and a fresh independent
whole-document review remain required. Architecture only: no implementation or runtime evidence,
builds, tests, linters, formatters or validation commands were produced by this fix-up.*

*§0.65/D-P3-73 records the R57 C57-1 architecture correction and schema23 clean cutover.
The original R57 PASS-WITH-CORRECTIONS and source/license limitations remain unchanged.
Changed §5, pending receiver receipts and fresh independent reviews remain gates. No validation
commands, implementation, runtime evidence, fresh PASS or implementation clearance are claimed.*

*§0.66/D-P3-74 records the R60 C1 whitelist-authoritative override-target correction and
completes the amendment ledger for it, with no `PackConfiguration` component, meaning,
fingerprint or schema change. The prior review history, including R60, remains historical with
its own verdicts; §8.1's named gate and a fresh independent whole-document review remain
required. No validation commands, implementation, runtime evidence, fresh PASS or
implementation clearance are claimed.*

*§0.67 records the R62 fix-up: evidence-anchor repairs in §0.33/§0.55/§0.56/§0.57/D-P3-60 and
the §10 OQ-7 Fallback collapse, with no binding §5, schema, fingerprint, or receiver change.
Review 62's PASS-WITH-CORRECTIONS resolves to verified under §G1.3 with all corrections resolved
and no §5 change outstanding; Resolutions are recorded in the review file. No validation
commands, implementation, runtime evidence, or implementation clearance are claimed.*
