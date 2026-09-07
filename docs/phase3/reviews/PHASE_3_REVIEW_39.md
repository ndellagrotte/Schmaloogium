# Phase 3 verification review — round 39

## 0. Method and reading order

I independently re-derived the surviving candidates from the complete target document before
consulting historical reviews. I read the target's header and public-shape declarations, the
Appendix F/A.3 conformance maps, the detailed texture, properties, source, resource, identity,
and publication contracts, the §5 cross-phase interface and version discipline, the OQ-7 spike,
and the decision log. I searched the whole target for declarations, aliases, opaque-type
classifications, and equivalent semantics for every candidate claim.

I then checked the resolved authority `docs/design/v3/DESIGN.md`, including Part I governance and
adoption rules, the Phase 3 target specification, the mandatory template, and the doc gate; the
contract ground truth in `docs/research/v1/RESEARCH.md`; and the binding Phase 1 interface in
`docs/phase1/v14/PHASE_1_DOC.md`. The permitted Pintonium and Oculus documents were not needed to
settle these candidates. After those judgments were settled, I read the discovered prior reviews
`PHASE_3_REVIEW_1.md` through `PHASE_3_REVIEW_38.md` in order, including their resolutions, as
historical material only.

There were no source-list deviations, no network use, and no agent fan-out. I did not invoke
`$verify-loop`, run a verification harness, start another session, or read a forbidden transcript,
chatlog, root-level `*.txt`, or other prior-session source. The Gate drops `candidate-001`,
`candidate-006`, and `candidate-007` for the recorded unverifiable evidence; I did not revive or
use those candidates.

## 1. Findings

### candidate-002 — `TextureBindingKey` uses an undeclared `TexturePropertyStage` type

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:346-704` (§2.2 public shape), `:1853-1859`
  (texture binding algebra), and §5.1 `:1802`.
- **Claim:** The texture configuration algebra is a closed, implementable cross-phase contract
  for Phase 13.
- **Evidence:** The target declares its §2.2 block to contain the canonical public shapes
  (`:346-348`). It publishes `TextureBindingKey` to Phase 13 and defines that key as
  `(TexturePropertyStage stage, String sampler, OptionalInt duplicateDiscriminator)`
  (`:1802,1853`). A whole-target search finds no declaration or alias for
  `TexturePropertyStage`; the nearby public enum declarations end with the mapping enums
  (`:699-703`). The later prose supplies only the value list `GBUFFERS|DEFERRED|COMPOSITE` and
  expansion rules (`:1854-1859`). That is a domain description, not the nominal public type
  required by the target's rule that exact exposed types, enum domains, ordering, validation,
  and other semantics are binding (`:1807-1817`). Phase 3 owns the lossless texture specs while
  Phase 13 owns loading, so the missing stage type cannot be deferred as a texture-realization
  detail (`:307-321`).
- **Severity:** correction. Add an immutable public `TexturePropertyStage` enum with the three
  values, or replace the component with another explicitly declared stage type, and bind its
  fixed order, validation, and existing expansion semantics in the §5.1 texture row. Preserve
  the existing suffix, discriminator, source-variant, and Phase 13 loading boundaries.
- **Touches interface/change-trigger region:** yes. The repair changes the consumer-visible
  `TextureBindingKey` contract and its §5.1 publication row, so the manifest-declared
  `cross-phase-interfaces` trigger fires.

### candidate-003 — `PackConfiguration` exposes an undeclared `CompatibilityStatus` domain

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:429-441` (`PackConfiguration`), `:1559-1562`
  (version-gate behavior), and §5.1 `:1792-1793`.
- **Claim:** The compatibility result carried to Phases 7 and 12 is a closed, inspectable public
  value rather than an unresolved type name.
- **Evidence:** The canonical configuration record has a required `CompatibilityStatus
  compatibility` component (`:429-441`), and the publication table explicitly exposes
  `CompatibilityStatus` to Phases 7 and 12 (`:1792-1793`). The version-gate text names only the
  unmet outcome `CompatibilityStatus.REQUIRES_NEWER_EDITION` and says malformed rules are
  ignored (`:1559-1562`). No target declaration, alias, compatible/default outcome, or
  aggregation rule exists for `CompatibilityStatus`; the public declaration block defines
  `MinimumEditionRule` and `ShaderPropertiesModel` but not this type (`:529-538`). The governing
  v3 Phase 3 specification assigns the full `shaders.properties` model, including the version
  gate, to Phase 3 (`DESIGN.md:1431-1439`), and the target makes `PackConfiguration` the single
  validated downstream truth (`DESIGN.md:1473-1476`). A consumer therefore cannot construct or
  interpret a passing compatibility result without inventing the public domain.
- **Severity:** correction. Declare a closed public status with an explicit compatible/default
  outcome and `REQUIRES_NEWER_EDITION`, define matching, unmet-rule aggregation, and malformed
  rule behavior, and incorporate that meaning into the existing §5.1 row and compatibility
  assertions. Do not add a second configuration component or move the version gate to Phases 7
  or 12.
- **Touches interface/change-trigger region:** yes. This closes a public `PackConfiguration`
  component consumed by later phases and changes the monitored §5.1 publication contract.

### candidate-005 — Remaining discovery/load and identity value types are not publicly closed

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:350-441` (canonical public signatures),
  §5.1 `:1790-1805`, and `:1967-2021` (discovery/load and identity semantics).
- **Claim:** Every public type crossing the Phase 3 boundary is specified precisely enough for
  downstream implementers to use without guessing.
- **Evidence:** The broad candidate is overstated for the already closed program-state, texture,
  resource, ID-map, source, and option families, and for intentionally opaque discovery IDs. The
  residual gap is nevertheless real. Canonical public signatures expose `PackIdentity`,
  `PackInputLimits`, `InternalPackReadException`, `PackSelection`, `RuntimeIdentityData`,
  `PackLoadFailure`, and `ConfigurationFingerprint` (`:373-375,398-441`) without declaring their
  record, sealed, enum, exception, or opaque shapes. The ID-map signatures likewise expose
  `IdMappingMacroEnvironment` and `IdMappingFileFingerprint` (`:634-658`), but the target does
  not declare them. §5.1 calls itself the complete publication surface and makes exact
  signatures, component types, variants, defaults, absence, ordering, validation, failure, and
  lifecycle semantics binding (`:1785-1817`). The later prose gives useful tuple/domain
  descriptions—for example `PackSelection` and `RuntimeIdentityData` (`:1989-2003`) and
  `PackLoadFailure` codes (`:2016-2021`)—but it does not establish the canonical public
  declarations or complete construction/accessor contracts. A whole-target search finds no
  inherited Phase 1 declaration for these Phase 3-owned values; the dependency supplies
  `GLCapabilityProfile` and the engine conventions, not these pack-front-end types.
- **Severity:** correction. Narrowly close the remaining consumer-visible constructed or
  inspected types with exact immutable declarations/accessors and binding rows: at minimum
  `PackIdentity`, `PackSelection`, `RuntimeIdentityData`, `PackInputLimits`, `PackLoadFailure`,
  `ConfigurationFingerprint`, `IdMappingMacroEnvironment`, and
  `IdMappingFileFingerprint`, plus the declared provider exception contract. Explicitly mark
  `PackCandidateId` and `DiscoveryGeneration` opaque if that remains their intended use. Do not
  duplicate the already adequate algebra for program state, textures, resources, source
  materialization, or ID-map rules.
- **Touches interface/change-trigger region:** yes. These are types in the published discovery,
  load, configuration, and ID-map boundaries; closing them changes the §5.1 consumer contract
  and requires fresh verification.

### candidate-008 — The shader macro conformance surface inconsistently advertises an undefined A–H family

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1263-1310` (§4.4), `:2346-2379` (OQ-7
  procedure/fallback), and D-P3-18 at `:2407`.
- **Claim:** The macro configuration gives shader sources a completely specified, authoritative
  macro set while OQ-7 remains open.
- **Evidence:** RESEARCH §3.5 enumerates named standard and option families—`MC_VERSION`, GL,
  OS, vendor, renderer, extension, and option macros—and says only the identity posture is the
  OQ-7 decision (`RESEARCH.md:313-319`). The target's macro model instead defines an OF A–G base
  family plus separately named option, capability, engine, and per-pack families
  (`:1268-1275`), then says shader sources receive “A–H” while properties and ID maps receive
  A–G (`:1305-1310`). The option-1 spike and fallback repeat “OF A–H” (`:2349-2354,
  :2376-2379`), but no H family, member list, provenance, injection scope, or semantics is
  declared anywhere in the target or the cited research contract. The §5.1 macro row publishes
  these families to Phase 4, Phase 6, and G8/S3 (`:1796`), so the unresolved label is an
  implementation/conformance ambiguity rather than harmless shorthand.
- **Severity:** correction. Keep OQ-7 open, but make every selectable policy a complete macro
  payload: replace A–H with an explicit enumeration of the named OF A–G and separately named
  option/capability/engine/contributor families, or define a genuine H family with its names,
  replacements, availability, provenance, and test. Align §4.4, the OQ procedure and fallback,
  D-P3-18, and macro conformance tests without advertising unsupported identity or feature
  semantics.
- **Touches interface/change-trigger region:** yes. The macro-family membership and injection
  surface are published through the monitored `MacroConfiguration` row to downstream
  materialization and G8/S3.

## 2. Checked and clean

The finder-reported clean areas were rechecked. The v3 Phase 3 objective, thirteen-section
mandatory template, doc gate, pure-`:engine` boundary, complete App F.1 ownership map, and the
App A.3 row coverage are aligned. The Phase 1 seam, `GLCapabilityProfile`, diagnostics, logging,
debug flags, SPDX/notice mechanism, and the stated Phase 1 ownership boundaries are used without
inventing a dependency runtime interface. The dimension/source/geometry/tooltip closure, resource
baselines and family filtering, program-state evaluation, persistence isolation, ID-map provenance
and lifecycle, and the Phase 2 harness hand-off yielded no additional finding from the supplied
candidate set.

The detailed program-state, texture-source variants and sidecars, resource aggregate, and ID-map
records are not revived as findings under candidate-005: their fields, variants, ordering,
absence, validation, and ownership are explicitly closed in the current target. Likewise,
`PackCandidateId` and `DiscoveryGeneration` are expressly opaque in the discovery semantics; the
residual candidate-005 finding does not require consumers to inspect or construct those IDs.

`candidate-004` is dropped on independent re-derivation. Although this round selects v3 as the
authority for review, v3's G0.4 rule explicitly says it is unadopted, that current Phase 3–9
governance remains RC3 as declared by phase headers and target manifests, and that source selection
does not itself migrate a downstream phase (`DESIGN.md:195-220`). The target's RC3 header and
actual-input ledger are therefore not made incorrect by the review-source override. No header
rebasing or v3 adoption is ordered; a future formal G0.4 adoption would be a separate operation.

The Gate-eliminated `candidate-001`, `candidate-006`, and `candidate-007` remain excluded solely
for their recorded unverifiable evidence. They are not converted into findings. No candidate
outside the four surviving IDs was created, and no prior review was treated as authority over the
current target bytes.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: yes

Four admitted findings remain, all correction-sized and all requiring a monitored interface
refresh. Candidates 002 and 003 close missing nominal public domains; candidate-005 is admitted
only in its narrowed residual public-type form, not its overstated claim about already closed
algebras; and candidate-008 resolves an undefined published macro-family label. None requires
rebuilding the Phase 3 architecture, so FAIL is not warranted. Literal PASS is unavailable because
four nonzero corrections remain.

The prior history shows repeated literal PASS rounds followed by newly exposed post-fix-up
contract surfaces. Round 38 itself applied six corrections and left the monitored region active;
this round independently finds four further interface corrections. The history therefore does not
establish convergence or permit Phase 3 closure.

The next required action is a scoped fix-up for `candidate-002`, `candidate-003`, `candidate-005`,
and `candidate-008`, recording each resolution in the target's next review/addendum. The fix-up
must update the relevant §5.1 rows and any applicable schema/compatibility assertions, align all
A–H references and tests, and then run a fresh whole-document verification round before Phase 3
can close or be consumed by dependents.

## Resolutions

Round 39's four admitted corrections are applied to the target. The additions close named
consumer-visible types and revise the monitored §5 publication surface; no PackConfiguration
component or schema-5 meaning/default changed, so `CURRENT_SCHEMA_VERSION` remains 5. The
intentional §5 change requires the fresh verification stated by the review.

### candidate-002 — resolved

`TexturePropertyStage` is now a public closed enum with `GBUFFERS`, `DEFERRED`, and `COMPOSITE`
in fixed validation/order sequence. `TextureBindingKey` and its §5.1 row bind that domain,
sampler suffix stripping, discriminator domain, and the existing expansion to gbuffers/shadow,
deferred, and composite/final programs. The new conformance hook is
`texturePropertyStage_closedDomainAndOrdering`.

### candidate-003 — resolved

`CompatibilityStatus` is now closed to `COMPATIBLE` and `REQUIRES_NEWER_EDITION`. The target
defines exact MC-tuple matching, case-insensitive natural ordering for sanitized edition tokens,
any-unmet matching-rule aggregation, the compatible/no-rule result, and warn-and-ignore handling
for malformed rules. The §5.1 row and `compatibilityStatus_matchingAggregationAndMalformed`
assertion carry the same contract.

### candidate-005 — resolved

The public shape now declares immutable `PackIdentity`, `PackSelection`, `RuntimeIdentityData`,
`OsFamily`, `PackInputLimits`, `PackLoadFailure`/`PackLoadFailureCode`,
`ConfigurationFingerprint`, `IdMappingMacroEnvironment`, and `IdMappingFileFingerprint`, along
with the previously exposed candidate kind/status enums and the provider-only checked
`InternalPackReadException`. `PackCandidateId` and `DiscoveryGeneration` are explicitly opaque.
Validation, accessors, failure reduction, ordering, and immutability are repeated in the detailed
contracts and §5.1 rows, with `publicValueTypes_identitySelectionRuntimeAndFailureClosed` and
`idMap_macroEnvironmentAndFingerprintClosed` hooks.

### candidate-008 — resolved

Every shader payload now enumerates the OF standard A–G family and the separately named option,
capability-feature, engine-identity, per-pack-override, and reserved-contributor families; no H
family is advertised. Properties and ID maps receive only the OF standard family. The OQ-7
procedure and fallback, D-P3-18, §3.5, §4.4, §5.1, and the macro conformance hooks use the same
named payload model while leaving OQ-7 open.

### Notes deferred

No notes were recorded in round 39; nothing is deferred.

No refusal: all four admitted corrections were applicable without contradicting authority or
requiring a new design decision.
