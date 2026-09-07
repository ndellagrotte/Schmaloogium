# Phase 3 Adversarial Review — Round 38

## 0. Method and reading order

I independently re-derived every candidate that survived the Gate from, in order:

1. the complete `docs/phase3/v1/PHASE_3_DOC.md`, including its public-shape block, conformance maps,
   detailed design, §5 publication contract, test plan, hand-off, and revision ledger;
2. the resolved manifest-selected authority `docs/design/v3/DESIGN.md`, using Part I, the Phase 3
   target specification, the mandatory template, and the doc gate (the v3 override is authoritative
   for this round);
3. `docs/research/v1/RESEARCH.md` as contract ground truth, especially the dimension/source,
   Appendix A.3, and Appendix F.3 material;
4. the binding contract in `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the permitted Pintonium/Oculus evidence and the supplied candidate records where useful for
   corroboration.

I searched the whole target for alternate declarations, aliases, accessors, and equivalent
semantics before settling each candidate's disposition, severity, and interface classification.
Only after those independent judgments were settled did I read the discovered prior reviews
`PHASE_3_REVIEW_1.md` through `PHASE_3_REVIEW_37.md`, in round order, including their resolutions.
Those reviews were used as historical material, not as authority over the current bytes.

There was no deviation from the resolved source contract, no network use, and no agent fan-out. I
did not invoke `$verify-loop`, a verification harness, another session, or a subagent, and I read no
forbidden transcript, chatlog, root-level `*.txt`, or other prior-session source. The Gate drops
`candidate-001`, `candidate-002`, `candidate-003`, `candidate-004`, and `candidate-008` for the
recorded unverifiable citations; I did not revive or use any of them as findings.

## 1. Findings

### candidate-005 — The option-ambiguity conformance row names an undeclared state

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:435-447` (§2.2 option declarations) and `:912`
  (§3.2 ambiguity row).
- **Claim:** The ambiguity conformance row must name the closed availability state that the public
  option model actually exposes.
- **Evidence:** The public model declares only `OptionAvailability.AVAILABLE` and
  `OptionAvailability.DISABLED_AMBIGUOUS` (`PHASE_3_DOC.md:435-447`). The conformance row instead
  points to `` `Ambiguity.DISABLED` `` (`PHASE_3_DOC.md:912`), and a whole-target search finds no
  `Ambiguity` declaration or alias. The detailed option semantics independently use
  `DISABLED_AMBIGUOUS` while retaining occurrences and forbidding rewrites
  (`PHASE_3_DOC.md:1140-1146`), and RESEARCH Appendix F.3 requires conflicting-default options to
  be disabled (`RESEARCH.md:1454-1467`). The mandatory template requires each conformance row to
  point to an actual design element (`DESIGN.md:831-835`).
- **Severity:** correction. Replace the row's unresolvable design-element token with
  `OptionAvailability.DISABLED_AMBIGUOUS`, retaining the existing occurrence, diagnostic, and
  no-rewrite behavior.
- **Touches interface/change-trigger region:** no. This is a conformance-map label correction
  outside the manifest-declared `cross-phase-interfaces` range; it does not alter the public enum,
  its meaning, or the §5 row.

### candidate-006 — `DimensionConfiguration` has no closed consumer-facing public shape

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:408-420`, `:846-848`, and §5.1 `:1669-1675`.
- **Claim:** Phases 7 and 12 cannot consume the published dimension map and implement base,
  override, and disabled selection without inventing the dimension value model and its source
  linkage.
- **Evidence:** `PackConfiguration` publishes
  `Map<DimensionKey, DimensionConfiguration> dimensions` (`PHASE_3_DOC.md:408-420`), while the
  target declares its §2.2 block to be the canonical public shape (`PHASE_3_DOC.md:341-343`). A
  whole-target search finds no declaration for `DimensionConfiguration`, `DimensionMode`, or an
  equivalent dimension record/accessor. The only dimension value semantics are the high-level rule
  that an existing folder uses only its `.vsh`/`.fsh` files, does not merge base programs, and uses
  `DimensionMode.DISABLED` for an empty folder (`PHASE_3_DOC.md:846-848`). The §5 row promises the
  value to Phases 7 and 12 but gives only “base/override/disabled dimension state”
  (`PHASE_3_DOC.md:1674`), despite the target's binding rule making exact signatures, types,
  absence, ordering, validation, and lifecycle semantics contractual (`PHASE_3_DOC.md:1688-1698`).
  Phase 3 expressly owns dimension source sets and the complete `PackConfiguration`
  (`PHASE_3_DOC.md:273-281`; `DESIGN.md:1352-1368`), so this is not a downstream implementation
  detail.
- **Severity:** correction. Add the exact immutable `DimensionConfiguration` and
  `DimensionMode` declarations (and close `DimensionKey` or source-reference values as needed),
  including base/override/source-key linkage, disabled and absent-map semantics, ordering,
  validation, and consumer operations; incorporate the same contract into §5.1 and apply §5.3 if
  the published component meaning changes.
- **Touches interface/change-trigger region:** yes. The repair closes a named Phase 7/12
  publication contract and must update the monitored dimension row, triggering a fresh verify round.

### candidate-007 — `SourceCatalog` has no consumer-facing path to materialization

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:706-722`, `:804-818`, and §5.1 `:1675`.
- **Claim:** Phase 4 cannot enumerate or obtain the phase-owned source materializer, valid roots,
  source maps, and materialized outputs from the published `SourceCatalog` without guessing APIs or
  reopening the pack.
- **Evidence:** The target says that `SourceCatalog` owns stable source IDs, original lines, the
  include graph, and a `SourceMaterializer` (`PHASE_3_DOC.md:706-707`), but declares only
  `SourceMaterializer.materialize(SourceKey, OptionState, MacroContribution,
  GeometryTranslationRequest)` and its two result variants (`PHASE_3_DOC.md:710-722`). There is no
  public `SourceCatalog` declaration or accessor for roots, source entries, include data, or the
  materializer, and no declaration for `SourceKey`, `MaterializedSource`, or `SourceMap`. The prose
  names `transformedText()`, `sourceMap()`, and `declaredUniforms()` but does not close the
  `MaterializedSource` value or map lookup/lifetime contract (`PHASE_3_DOC.md:804-818`). The §5 row
  nevertheless exposes all of these types to Phase 4 and the Phase 2 harness
  (`PHASE_3_DOC.md:1675`), while the target forbids consumers from reopening the pack, rescanning,
  or bypassing the materializer (`PHASE_3_DOC.md:1876-1879`). Phase 3 owns source preprocessing and
  source attribution (`DESIGN.md:1369-1374`), and Phase 4 is explicitly a consumer of the published
  source data.
- **Severity:** correction. Publish the minimal immutable source surface: ordered root/source
  enumeration, `SourceCatalog` include/materializer access, `SourceKey` identity and stage/dimension
  meaning, `MaterializedSource` fields and lifetime, `SourceMap` numeric-file lookup, and any
  source-attributed site/value operations needed by the already-declared materializer. Retain the
  existing `SourceMaterializer` and geometry request/plan declarations, and bind the exact
  operations, absence, failure, ordering, and lifecycle rules in §5.1.
- **Touches interface/change-trigger region:** yes. The missing catalog/materialization path is a
  Phase 4-facing §5 source-contract correction and requires the manifest's fresh-review trigger.

### candidate-010 — Terminal-`!` tooltip severity is not published

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:440-447`, `:1106-1108`, `:1678`, and `:2046-2053`.
- **Claim:** The public option model and conformance contract must preserve the App F.3 terminal-`!`
  warning/red behavior for the Phase 12 consumer.
- **Evidence:** RESEARCH requires tooltip lines ending in `!` to render red
  (`RESEARCH.md:1454-1459`). The public `OptionDefinition` exposes only `Optional<String> tooltip`
  and has no severity or segmented-tooltip value (`PHASE_3_DOC.md:440-447`). The detailed design
  says that a terminal `!` sets `TooltipSeverity.WARNING` but declares no such public type, accessor,
  marker-preservation rule, or Phase 12 derivation (`PHASE_3_DOC.md:1106-1108`). The option §5 row
  lists closed option/decorations types and sole consumer reads but no tooltip severity channel
  (`PHASE_3_DOC.md:1678`), and the named switch test is generic rather than an assertion of the
  terminal marker and red result (`PHASE_3_DOC.md:2046-2053`). Phase 3 owns option discovery and
  decoration while Phase 12 owns only GUI rendering (`PHASE_3_DOC.md:280-281,313-315`), so Phase 12
  cannot repair an unstated publication convention by reinterpreting source text.
- **Severity:** correction. Publish a closed tooltip projection carrying segments and a closed
  severity, or explicitly retain the terminal `!` in the public string and define Phase 12's exact
  warning/red interpretation and display-text rule. Add a named terminal-`!` conformance assertion
  and update the §5 option row (and §5.3 compatibility data if the public shape or meaning changes).
- **Touches interface/change-trigger region:** yes. This changes the consumer-visible option
  contract incorporated by the monitored §5 row and therefore requires fresh verification.

### candidate-012 — A.3 shadow rows name an undeclared `ShadowConfig`

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:966-970`.
- **Claim:** The A.3 shadow conformance rows must target the actual published resource aggregate,
  not an unbound type name.
- **Evidence:** The five rows target `ShadowConfig.resolution`, `.fov`, `.distance`,
  `.distanceRenderMultiplier`, and `.intervalSize` (`PHASE_3_DOC.md:966-970`). The public resource
  algebra instead declares `ShadowRequirements` with exactly those fields
  (`PHASE_3_DOC.md:1379-1382`), and the binding contract repeats `ShadowRequirements` as the nested
  type and identifies its domains and defaults (`PHASE_3_DOC.md:1783-1809`). A whole-target search
  finds `ShadowConfig` only in those five map cells. The target requires every A.3 row to name one
  typed parser, target field, validation rule, and test (`PHASE_3_DOC.md:949-953,1313-1320`), so the
  stale backticked type is not an equivalent alias.
- **Severity:** correction. Rename the five map targets to the corresponding
  `ShadowRequirements` accessors and align the named tests; do not introduce a second shadow type.
- **Touches interface/change-trigger region:** no. The existing `ShadowRequirements` publication,
  resource semantics, and §5 row remain unchanged; only the §3.3 conformance labels are corrected.

### candidate-013 — The A.3 geometry row exposes undeclared parsed-pair and rewrite-site types

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:959`, `:1332-1337`, `:1371-1374`, and §5.1 `:1675`.
- **Claim:** The legacy geometry directive must publish a closed Phase 3 parse result and attributed
  rewrite-site shape distinct from the Phase 4-supplied translation plan.
- **Evidence:** The A.3 row requires `LegacyGeometryConfig` plus an attributed
  `LegacyGeometryRewriteSite` and separately says materialization consumes Phase 4's selected plan
  (`PHASE_3_DOC.md:959`). `ProgramRequirements` exposes
  `Optional<LegacyGeometryConfig>` (`PHASE_3_DOC.md:1371-1374`), and the scanner prose says it
  retains exact extension/declaration spans as `LegacyGeometryRewriteSite` while Phase 4 owns only
  translation strategy (`PHASE_3_DOC.md:1332-1337`). Neither `LegacyGeometryConfig` nor
  `LegacyGeometryRewriteSite` has a public declaration, closed span/coordinate representation,
  validation, ordering, or source/root identity; the only actual geometry declarations are the
  caller-supplied `GeometryTranslationPlan` and `GeometryTranslationRequest`
  (`PHASE_3_DOC.md:770-782`). The load pipeline must publish the attributed pair before Phase 4
  selects a plan (`PHASE_3_DOC.md:1249-1253`), and the binding rule makes the geometry declarations
  consumer-facing rather than illustrative (`PHASE_3_DOC.md:1688-1698`).
- **Severity:** correction. Add immutable public declarations for `LegacyGeometryConfig` and
  `LegacyGeometryRewriteSite` (including a declared span representation, root/source identity,
  recognized extension/max-vertices data, pair absence/cardinality, validation, ordering, and
  fingerprint ownership), incorporate them into the §5 source/resource row, and retain
  `GeometryTranslationPlan`/`Request` as the separate Phase 4 strategy input.
- **Touches interface/change-trigger region:** yes. These are missing values in the published
  source/resource hand-off, so closing them changes the monitored §5 contract and requires fresh
  verification. This finding is distinct from candidate-007's missing catalog/materializer access
  path: it concerns the parsed geometry pair and attributed rewrite-site value model.

## 2. Checked and clean

The finder-reported clean areas were rechecked against the current bytes. The schema-v5 repeated
identifiers and nested ID-map version rule, §0.37 ledger convention, exact program-family
predicates and wrong-family no-mutation rule, Phase 9 ID-map ownership/provenance, atomic and
no-symlink persistence safety, the closed macro/option/property/resource portions other than the
listed gaps, the consumed Phase 1 seam/capability/diagnostic/logging/debug/SPDX contracts, and the
remaining Appendix F/A.3 rows yielded no additional candidate from the supplied set.

`candidate-009` is dropped on independent re-derivation. The governing Phase 2 assignment makes
that phase responsible for the golden-file format, fixture workflow, and harness reporting
(`docs/design/v3/DESIGN.md:1273-1305`), while the Phase 3 target explicitly assigns Phase 2 the
adapter, CI job, and `:conformance` integration (`PHASE_3_DOC.md:1892-1895`). The monitored §5
source row already identifies the in-process Phase 2 consumer and the exact transformed-text and
source-map semantics (`PHASE_3_DOC.md:1675`); `PackConfiguration` itself publishes schema 5 with
an exact compatibility rule (`PHASE_3_DOC.md:1899-1904`). Section 8.2 is a test-workflow list of
fixtures, manifest-only records, profiles, and matrix runs, not a second Phase 3 runtime/public
parser result. Requiring Phase 3 to invent filenames, wire encoding, or a duplicate golden schema
would cross the Phase 2 ownership boundary. The current target's explicit emission/ownership
statements therefore do not establish a surviving Phase 3 interface defect for this candidate.
The missing types in candidate-007 do not turn the Phase 2-owned test serialization into a
separate Phase 3 wire contract.

`candidate-011` is dropped as an exact duplicate of admitted `candidate-005`, not cleared on its
substance. Both identify the sole `Ambiguity.DISABLED` occurrence at the same row, compare it with
the same `OptionAvailability.DISABLED_AMBIGUOUS` declaration, and require the same one-token map
correction. Counting both would double-count one repair.

The Gate-eliminated `candidate-001`, `candidate-002`, `candidate-003`, `candidate-004`, and
`candidate-008` remain excluded solely for their recorded unverifiable evidence. They are not
converted into findings and are absent from the disposition list. No additional finding is created
from the target header, prior-review history, or any source outside the candidate set.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=6; notes=0
Interface changed: yes

Six admitted findings are bounded documentation and contract-completion corrections; none requires
rebuilding the Phase 3 architecture, so FAIL is not warranted. Candidates 005 and 012 are
conformance-map-only corrections and do not touch the monitored region. Candidates 006, 007, 010,
and 013 close consumer-visible declarations or semantics in §5 and do touch it, making the derived
interface flag `yes` and requiring a fresh whole-document verification after fix-up.

Round 37 had four corrections. Although prior rounds include literal PASS results, the history also
shows new post-fix-up surfaces, and this round rises from the supplied Round 37 count to six
independently admitted corrections. The current document has not converged to literal PASS and is
not eligible for closure or dependent consumption.

The next required action is a scoped fix-up for candidates 005, 006, 007, 010, 012, and 013,
including the corresponding §5.1 updates and any applicable §5.3 schema/compatibility assertions.
The fix-up must record each resolution in its review file. Because the repairs for 006, 007, 010,
and 013 alter the manifest-declared cross-phase interface region, a fresh whole-document
verification round is required before Phase 3 can close.

## Resolutions

The six admitted corrections were applied to `docs/phase3/v1/PHASE_3_DOC.md`. The target received
only a compact §0.38 ledger entry; the contract reasoning is recorded here. No correction was
refused.

### candidate-005 — applied

The §3.2 ambiguity row now names `OptionAvailability.DISABLED_AMBIGUOUS`, the enum actually
declared by the public option model. The existing occurrence retention, diagnostic, and
no-rewrite behavior is unchanged. This is a conformance-label repair and does not touch the
manifest-declared interface region.

### candidate-006 — applied

The public shape now declares immutable `DimensionKey`, `DimensionMode`, and
`DimensionConfiguration` values. `DimensionKey` is either the empty base key or a validated
legacy ID in `[-128,128]`; each configuration carries its key, mode, optional base link, and
ordered source roots. The detailed and §5.1 contracts state that the base entry is always
present, absent world folders have no map entry and select base, non-empty folders are
source-exclusive overrides, and empty folders are disabled entries with no roots. Map/key
validation and base-first/ascending-world ordering are explicit, and consumers select by key
without merging sources. The existing `PackConfiguration.dimensions` component is closed rather
than replaced, so §5.3 records no schema-5 component or meaning change.

### candidate-007 — applied

The target now publishes the minimal immutable catalog path: `SourceId`, `SourceKey`,
`SourceDocument`, `IncludeEdge`, `SourceCatalog` accessors for ordered sources/roots, lookup,
include edges, and the existing materializer, plus `SourceMap`, `MaterializationFingerprint`,
`MaterializedSource`, and the existing result variants. The target defines source identity,
dimension/stage/program validation, deterministic ordering, missing-target absence, numeric
source-file lookup, exact transformed text, diagnostics, defensive snapshots, and validity
after the input lease closes. Phase 4 retains the existing materializer signature and cannot
reopen pack storage. The source row in §5.1 incorporates these operations and lifecycle rules.

### candidate-010 — applied

The chosen permitted projection is explicit marker retention: `OptionDefinition.tooltip()` keeps
the decoded terminal `!` in its existing optional string. The detailed design and §5.1 state that
Phase 12 splits on `. `, classifies a resulting line ending in `!` as warning/red, and removes
only that final marker for display, without trimming other text. The named
`optionTooltip_terminalBangPreservedAndRed` assertion now guards the App F.3 behavior. No
`TooltipSeverity` type or new record component is invented, and §5.3 records no schema change.

### candidate-012 — applied

All five shadow conformance cells now target `ShadowRequirements.resolution`, `.fov`, `.distance`,
`.distanceRenderMultiplier`, and `.intervalSize`. No `ShadowConfig` alias or second shadow type
was introduced; the existing aggregate, validation, defaults, and named tests remain the source
of the contract.

### candidate-013 — applied

The public shape now declares `SourceSpan`, `LegacyGeometryExtension`,
`LegacyGeometryRewriteSite`, and `LegacyGeometryConfig`, while `ProgramRequirements` retains
`Optional<LegacyGeometryConfig>`. The target closes the recognized extension and positive
`maxVerticesOut` data, exact root/source identities, half-open UTF-16 spans and coordinates,
source order/non-overlap validation, one-pair cardinality, absence, structural failure behavior,
and Phase-3 ownership of configuration/site fingerprint inputs. The source/resource §5.1 binding
now exposes the parsed pair and site, while `GeometryTranslationPlan` and
`GeometryTranslationRequest` remain Phase 4's separate strategy input.

Candidates 006, 007, 010, and 013 intentionally changed the monitored
`cross-phase-interfaces` region; the manifest change trigger therefore remains active and a
fresh whole-document verification is required. Candidates 005 and 012 were map-only repairs.

### Notes deferred

None. Round 38 admitted no notes, and no proposed correction was deferred.
