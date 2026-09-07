# Phase 3 verification review — round 37

## 0. Method and reading order

I first re-derived every surviving candidate from the current target, using a whole-target search
plus the cited and adjacent sections of `docs/phase3/v1/PHASE_3_DOC.md`. I then checked the
manifest-selected `docs/design/v3/DESIGN.md` Part I, Phase 3 specification, mandatory template,
doc gate, and adoption/provenance rules; the relevant contract rows in
`docs/research/v1/RESEARCH.md`; the resolved Phase 1 binding contract; and the permitted Pintonium
and Oculus reports. The decisive target areas were the §0 revision/input ledger, the canonical
public-shape block, the Appendix A.3 and ID-mapping conformance tables, the directive scanner,
the properties and persistence design, and the §5 publication/version rules.

Only after settling each candidate's interpretation, severity, and interface classification did I
read the discovered prior reviews 1–36 and their resolutions as historical material. The prior
history was used to distinguish actually settled surfaces from the current §0.36 surface; it was
not treated as authority over the current target. The supplied prior-round trend is empty. There
were no network calls, no agent fan-out, and no invocation of `$verify-loop`, a verification
harness, another session, or a subagent. I used no forbidden transcript, chatlog, root-level text,
or implementation/decompile source, and made no source-list deviation relevant to adjudication.

The Gate drops were not revived: `candidate-002`, `candidate-005`, and `candidate-006` were
dropped for unverifiable citations. `candidate-009` was eliminated before adjudication at Refute
for lack of live severity and was likewise not reconstructed or used as a finding.

## 1. Findings

### candidate-001 — The last-revised pointer still names §0.35

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:6-9,255-261,2119-2121` and
`docs/design/v3/DESIGN.md:195-220`.

**Claim:** The header's revision pointer must identify the latest addendum actually present in the
document, while the governing-design declaration must follow the selected design's adoption rule.

**Evidence:** The target header still says `Last revised: ... (§0.35)` and declares
`docs/design/v2.0-RC3/DESIGN.md` as the governing design (`PHASE_3_DOC.md:6-9`). The target then
contains a §0.36 Round 36 fix-up (`PHASE_3_DOC.md:255-261`) and its closing ledger says Round 36
reviewed §0.35 and produced §0.36 (`PHASE_3_DOC.md:2119-2121`). The selected v3 design expressly
states that v3 is unadopted, that Phases 3–9 retain RC3 governance until the adoption procedure is
completed, and that selecting v3 does not itself migrate downstream governance
(`DESIGN.md:195-220`). Thus the RC3 path is not the surviving defect; the §0.35 pointer is stale.

**Severity:** correction. Change the header's last-revised marker to §0.36. Retain the RC3
governing-design declaration and its historical input coordinates unless a separate, complete v3
adoption is performed.

**Touches interface/change-trigger region:** no. This is a header/provenance correction and does
not order an edit to the manifest-declared `cross-phase-interfaces` region.

### candidate-003 — Clear and mipmap directives omit the required program-family scope

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:804-807,1091-1106,1111-1123,1435-1440,1532-1563` and
`docs/research/v1/RESEARCH.md:1155-1192`.

**Claim:** The directive conformance map and scanner must preserve the Appendix A.3 applicability
of `colortexNClear`, `colortexNClearColor`, and `colortexNMipmapEnabled`; source-language stage
handling alone is not that program-family rule.

**Evidence:** RESEARCH Appendix A.3 restricts clear and clear-color directives to composite or
deferred scope, and restricts per-pass mipmap generation to composite, deferred, or final scope
(`RESEARCH.md:1183-1186`). The target's three conformance rows specify only value-to-field
mappings and positive test names (`PHASE_3_DOC.md:804-807`). Its only concrete stage restrictions
say that attributes and `countInstances` are vertex-only and the legacy geometry pair is
geometry-only; the generic wrong-stage sentence does not identify the allowed composite/deferred/
final program families (`PHASE_3_DOC.md:1091-1106`). The target's source-stage enum is only
`VERTEX`, `GEOMETRY`, `FRAGMENT`, and `COMPUTE` (`PHASE_3_DOC.md:587-590`), so it cannot by itself
classify a fragment source as a deferred, composite, final, gbuffers, or shadow program.

Phase 3 owns all Appendix A.3 recognition and aggregation (`PHASE_3_DOC.md:278-288`), and the
affected clear, clear-color, and per-program mipmap values are published in the `ResourceRequirements`
contract consumed by Phase 5 (`PHASE_3_DOC.md:1438-1440,1532-1558`). An out-of-family occurrence
can therefore change a downstream-visible attachment or program requirement. Section 5 expressly
requires a same-edit binding update whenever producer text changes a published value or consumer
interpretation (`PHASE_3_DOC.md:1560-1563`).

**Severity:** correction. Add an explicit program-family predicate to the directive registry and
scanner: clear and clear-color accept only deferred/composite programs, while per-pass mipmap
accepts deferred/composite/final programs. A recognized out-of-family occurrence must be
source-diagnosed, ignored, and unable to replace a valid prior/default value. Add wrong-family
fixtures, including valid-plus-wrong ordering cases, update the conformance and §4.7 text, and
update the §5 `ResourceRequirements` semantics and consumer assertions. The current §5.3 rule for
a changed published resources meaning must also be reconciled with the current schema value of 4.

**Touches interface/change-trigger region:** yes. The correction changes the published
`ResourceRequirements` interpretation and therefore the manifest-declared `cross-phase-interfaces`
region and its fresh-review trigger.

### candidate-007 — Published configuration components and persistence codecs are not type-closed

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:337-414,606-613,907-969,1203-1247,1427-1455` and
`docs/design/v3/DESIGN.md:836-840,1431-1445`.

**Claim:** Consumers named for the macro, option, properties, and persistence portions of
`PackConfiguration` must receive closed public types and callable codec contracts rather than
infer them from conceptual prose and format labels.

**Evidence:** The canonical public `PackConfiguration` record exposes `MacroConfiguration`,
`OptionConfiguration`, and `ShaderPropertiesModel` components (`PHASE_3_DOC.md:337-414`), and the
target says that this block contains the canonical public shapes (`PHASE_3_DOC.md:335-337`). Yet
`MacroConfiguration` is only a text diagram of macro groups, overrides, and a reserved contributor
(`PHASE_3_DOC.md:959-969`); there is no public immutable collection/entry/override shape, ordering,
validation, or accessor contract for that component. `MacroContributor` and `MacroContribution`
are closed (`PHASE_3_DOC.md:606-613`), but they do not close `MacroConfiguration` itself.

The option and properties sections describe discovery, screen behavior, expressions, profiles, and
program state, but do not declare a public `OptionConfiguration`, `OptionCatalog`, `OptionState`,
or `ShaderPropertiesModel`/`EngineFlags` data shape and accessor set (`PHASE_3_DOC.md:907-955,
1203-1247`). The §5 rows nevertheless expose `OptionConfiguration` and
`ShaderPropertiesModel.engineFlags` to later phases (`PHASE_3_DOC.md:1435-1438`). Persistence is
similarly owned by Phase 3: the detailed text gives ISO-8859-1 formats and atomic/symlink policy,
then assigns codec ownership to Phase 3 and invocation timing to Phase 12, but gives no
`read`/`write` signatures, request/input acquisition, immutable result or failure types, validation
outcomes, or lifecycle contract (`PHASE_3_DOC.md:938-948`). The binding table names
`OptionPersistenceCodec` and `GlobalShaderOptionsCodec` only as format labels
(`PHASE_3_DOC.md:1435-1438`), even though it says exact signatures, types, defaults, absence,
ordering, validation, failure, and lifecycle semantics are binding (`PHASE_3_DOC.md:1446-1452`).
The governing Phase 3 scope assigns the complete properties model and both persistence formats to
this phase, so this cannot be deferred to Phase 12 or another owner (`DESIGN.md:1431-1445`).

**Severity:** correction. Add canonical immutable declarations and accessors for the published
macro, option/catalog/state, properties/engine-flag, and related screen/profile models, or replace
the opaque components with explicitly typed projections. Close both persistence codecs with exact
read/write request/result/failure and acquisition/lifecycle semantics, including changed-only versus
global scope, validation, deterministic ordering, and safe atomic-write behavior. Bind the same
contracts in §5.1 and apply the existing schema/version rule if the clarification changes a
published component meaning or default. This is a substantial interface completion but remains a
localized specification fix-up, not a structural rebuild.

**Touches interface/change-trigger region:** yes. The required declarations and codec operations
are exposed in §5.1 to Phases 4, 11, 12, and other consumers, so the correction changes the
manifest-declared interface/change-trigger region.

### candidate-010 — ID-map extensions are attributed only to RESEARCH §3.7

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:840-848,157-165,1303-1360` and
`docs/research/v1/RESEARCH.md:439-464`.

**Claim:** Every behavior in the ID-mapping conformance rows must carry the authority that actually
supports it; modern/downstream extensions must not be presented as unqualified RESEARCH §3.7
requirements.

**Evidence:** The target's §3.5 rows attribute both percent-tag selectors and the isolated
forced-`MC_VERSION=11300` entity parse to `RESEARCH §3.7` (`PHASE_3_DOC.md:840-848`). RESEARCH
§3.7 supports the classic block/item/entity entry, namespaced, property-matched, legacy `id:meta`,
layer, mod-extension, and standard A–G preprocessing rules, but it contains neither the percent-tag
syntax nor the forced-11300 alternate parse (`RESEARCH.md:454-464`). RESEARCH separately classifies
1.12 tag support as a modern-risk feature requiring an ore-dictionary-style shim and an
entries-before-tags priority rule (`RESEARCH.md:439-448`). The governing v3 design qualifies tag
evidence as modern-only and separately places the entity era bridge in the Phase 9/PD §8.1 design
(`DESIGN.md:2088-2103`); the target itself records both behaviors as the later Phase 9 R9-1 addition
(`PHASE_3_DOC.md:157-165`).

The parser and Phase 9 hand-off can remain as specified. What is missing is row-level provenance and
classification distinguishing the RESEARCH §3.7 classic contract from the modern/downstream
extensions.

**Severity:** correction. Split or annotate the conformance rows: cite RESEARCH §3.7 for the classic
grammar and A–G preprocessing, cite the modern-risk/shim rule for tags, and cite the governing
Phase 9/PD §8.1 source for the forced-11300 bridge. Label the latter two as extensions and preserve
the existing unresolved parser/Phase 9 ownership boundary.

**Touches interface/change-trigger region:** no. This orders a conformance/provenance correction
only; it does not change the published `IdMappingInput` shape or the Phase 9 hand-off.

## 2. Checked and clean

- The new-surface checks remain coherent for the current schema value 4, tagged `DrawRouting`,
  source-order `gdepth` precedence, texture-key filter/wrap stripping versus independent sidecar
  retention, the Phase 2 harness hand-off, the OQ-7-shaped macro identity architecture, and the
  thirteen mandatory sections. The stale §0.35 header pointer is the exception admitted above.
- The Phase 1 runtime inputs named by the target—`GLCapabilityProfile`, loader-neutral diagnostics,
  fixed logging channels, `saveSources`, and the SPDX/third-party notice mechanism—have matching
  target bindings in the resolved dependency contract. The already closed detailed contracts for
  `ProgramStateModel`, `ResourceRequirements`' existing algebra, custom expressions, ID-mapping
  values, fingerprints, and discovery/load failures yielded no additional current candidate.
- The conformance clean areas remain clean for the F.1 ownership map, the other F.2–F.8 rows,
  the remaining A.3 directive rows, discovery/dimension/include handling, preprocessing and
  persistence format behavior, the OQ-7 spike, pure-`:engine` placement, the failure ladder,
  named tests, milestones, and checklist. Candidate-007 is specifically a public type/operation
  closure gap, not a newly invented persistence-format or preprocessing defect.
- `candidate-004` is dropped on independent re-derivation. The sentence “No input outside that
  assignment was read” scopes the following “In particular” exclusions to unassigned material; the
  named Pintonium source reads are inside the assignment. The target also explicitly distinguishes
  permitted Pintonium structure evidence from forbidden OptiFine decompiled implementation
  structure (`PHASE_3_DOC.md:39-64`), consistent with the governing rule that Pintonium is readable
  and reusable (`DESIGN.md:893-904`). The wording could be clarified, but no contractual
  contradiction or note-worthy defect survives.
- `candidate-008` is dropped as an exact duplicate of admitted `candidate-003`: both identify the
  same missing composite/deferred/final program-family applicability for the same three directives,
  cite the same target and RESEARCH rows, and require the same scanner/map/§5 repair. Its substance
  is counted once rather than double-counted as an independent finding.
- The earlier historical clearance of a colortex-scope concern in Round 14/31 does not settle the
  present candidate: the current target still has no directive-specific family predicate or
  wrong-family test, and Round 35's subsequent review likewise recorded the same live omission.
  Round 36's applied corrections concern clear-color algebra, gdepth, routing, texture-key
  handling, public-binding coverage, and the Phase 2 hand-off; none adds the missing family rule.
  The prior reviews therefore do not displace the independent admission above.
- No candidate outside the supplied surviving set was created. The Gate-dropped and Refute-dropped
  candidates remain excluded for their recorded reasons.

## 3. Verdict
# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: yes

Four correction-sized findings remain: the stale revision pointer, the missing clear/mipmap
program-family filter, the unclosed PackConfiguration/options/properties/codec surface, and the
ID-map provenance classification. None requires rebuilding the Phase 3 architecture, so FAIL is
not warranted. The supplied trend is empty, and the historical sequence repeatedly reached literal
PASS before later fix-up surfaces exposed additional omissions; this round therefore does not
establish convergence or permit closure.

The next required action is a scoped fix-up for `candidate-001`, `candidate-003`, `candidate-007`,
and `candidate-010`, with the corrections recorded in that fix-up's resolutions. The fix-up must
update §5 and its compatibility/version assertions for the interface-touching repairs, apply the
current schema discipline to the corrected `ResourceRequirements` meaning, and keep the ID-map
provenance repair shape-stable. Because admitted corrections change the manifest-declared
cross-phase interface/change-trigger region, a fresh whole-document verification round is required
before Phase 3 can close or be consumed by a dependent.
## Resolutions

### candidate-001 — applied
The stale header pointer was corrected: the target now identifies the newly appended current
fix-up as §0.37 while retaining `docs/design/v2.0-RC3/DESIGN.md` as the governing design. The
new subsection records the round's changes and leaves the `v1` directory in its pending-review
state; it does not adopt the selected but unadopted v3 revision.

### candidate-003 — applied
Re-derivation against Research Appendix A.1 and A.3 added a private closed `ProgramFamily`
classifier using exact program names, including `deferred_pre` and `composite_pre`, rather than
source-stage inference. The clear and clear-color registry predicates are
`DEFERRED|COMPOSITE`; the per-pass mipmap predicate is `DEFERRED|COMPOSITE|FINAL`. An
out-of-family occurrence now gets one attributed diagnostic, is not active, cannot create or
mutate a published entry, and is excluded from valid source-order selection even when placed
before or after an eligible occurrence. The §3.3 map, §4.7 scanner, §5.1 row and consumer
assertions, tests, fixtures, milestones, and checklist all state this rule. Because the
consumer-visible `resources` meaning changed, the target and compatibility assertions advance
the live schema from 4 to 5; the monitored cross-phase region intentionally changed.

### candidate-007 — applied
The target now gives the macro, option/catalog/state/profile/screen/slider/language,
`EngineFlags`/`ShaderPropertiesModel`, and persistence surfaces canonical immutable records,
closed variants/enums, and explicit accessors/defaults/order/validation semantics. Both codec
interfaces have callable `read`/`write` request and result types, typed failure/status values,
and a sole `PersistenceFileAccess` acquisition boundary. The detail and §5.1 contract bind
pack changed-only versus global-only writes, ISO-8859-1 decoding, safe-value retention,
deterministic ordering, byte snapshot and handle closure, same-directory temporary files,
atomic replacement, and no-symlink/path-escape handling. The schema-5 revision records the
newly closed consumer-visible types and codec lifecycle as well as the resource correction.

### candidate-010 — applied
The ID-map contract now separates the classic block/item/entity/layer grammar and Standard
Macro A–G preprocessing, which are sourced to RESEARCH §3.7, from the modern-risk
`%namespace:path` tag extension and its unresolved Phase 9 shim/entries-before-tags policy.
The isolated forced-`MC_VERSION=11300` entity parse is labeled as the Phase 9/PD §8.1
era bridge rather than as a classic §3.7 requirement. The `IdMappingInput` shape, parser
operation, unresolved values, and Phase 9 ownership boundary remain unchanged; its displayed
schema number follows the independent schema-5 compatibility bump.

### Notes deferred
None. Round 37 admitted no notes, so no note required deferral.
