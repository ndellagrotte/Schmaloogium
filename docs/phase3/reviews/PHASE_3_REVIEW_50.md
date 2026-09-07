# Phase 3 verification review — round 50

## 0. Method and reading order

I independently re-derived all five surviving candidates from the current target and resolved source
contract before consulting historical reviews. The load-bearing sources were:

1. `docs/phase3/v1/PHASE_3_DOC.md`, including the canonical declarations, load and materialization
   pipelines, option-state protocol, conformance maps, standard-macro and ID-map rules, the complete
   manifest-declared §5 interface region, tests, decisions, hand-offs, and checklist;
2. the manifest-selected `docs/design/v3/DESIGN.md` Part I, mandatory template, Phase 3 target
   specification, document gate, contract-visibility rules, and Pintonium trust rules;
3. `docs/research/v1/RESEARCH.md` as contract ground truth, especially the standard-macro, compile
   flow, option, ID-map, and Appendix F.6 precipitation material;
4. the binding §5 contract and incorporated diagnostic/capability declarations in
   `docs/phase1/v14/PHASE_1_DOC.md`; and
5. `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §7.6 as permitted supporting evidence for
   the StandardMacros mechanism.

I searched the complete target for equivalent definitions of numeric tag forms, option-state/configuration
coherence, diagnostic identifiers and localization, precipitation ownership, and exact standard-macro
values before settling each interpretation, severity, and interface classification. Only after those
judgments were settled did I read all discovered prior reviews `PHASE_3_REVIEW_1.md` through
`PHASE_3_REVIEW_49.md`, including their resolutions, as the final historical step.

There were no source-list deviations, no network use, and no agent fan-out. I did not invoke
`$verify-loop`, run `scripts/verify`, start another session, or read a forbidden transcript,
`docs/**/chatlogs/**` path, or `*.txt` source. No candidate was eliminated before adjudication, and
the Gate reported no drops.

## 1. Findings

### candidate-001 — The provisional numeric-tag exclusion has no executable boundary

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §4.9, the §5.1 `IdMappingInput` row, and the
  `idMap_tagIdentifierGrammarBoundaries` test description.
- **Claim:** The local D-P3-47/D-P3-49 grammar does not give every digit-bearing `%` token one
  mechanically derivable classification.
- **Evidence:** Section 4.9 defines `tagId` as either a slash-separated `path` or
  `namespace ":" path`, permits digits throughout both character classes, and then separately says
  that “numeric tag forms” are rejected (`PHASE_3_DOC.md:2237-2244`). A negative rule may validly
  narrow a positive production—the neighboring explicit `.`/`..` exclusion demonstrates that
  pattern—but “numeric tag forms” has no corresponding predicate. The target therefore does not
  determine whether the exclusion covers only an all-digit short ID such as `%123`, an all-digit
  path component, a numeric namespace, or combinations such as `%minecraft:123`, `%123:foo`, and
  `%123/foo`. The §5 row repeats the digit-permitting production without closing that boundary
  (`:2358`), while the test description merely lists “numeric tags” without expected accept/reject
  vectors (`:3031-3034`). D-P3-49 requires the provisional rule to make v0.1 classification
  executable (`:3272-3274`), so the unresolved term cannot be delegated to Phase 9 registry
  resolution.
- **Severity:** correction.
- **Required correction:** Choose and define one complete predicate for digit-only/digit-bearing
  tag identifiers, then align §4.9, D-P3-47/D-P3-49 as applicable, the binding §5.1 ID-map row, and
  explicit boundary assertions. At minimum, state outcomes for `%123`, `%minecraft:123`,
  `%123:foo`, and `%123/foo`, along with canonical output for every accepted form. Do not rely on
  the undefined phrase “numeric tag forms.”
- **Touches interface/change-trigger region:** yes. The repair changes or closes tag acceptance
  semantics incorporated into the monitored Phase 9-facing §5 contract.

### candidate-002 — Same-catalog option updates can desynchronize materialized source from the published configuration

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §§2.3, 4.3, 4.8, 4.10, and the §5.1
  `PackConfiguration`/`SourceMaterializer` contracts.
- **Claim:** The sole Phase 4 materialization path admits option-dependent source that need not
  correspond to the option state, resource requirements, program projections, and configuration
  fingerprint published together by the owning `PackConfiguration`.
- **Evidence:** The atomic load finalizes one default-plus-persistence `OptionState`, performs
  option rewriting and conditional evaluation with that same state, scans the resulting declarations
  into `ResourceRequirements`, and fingerprints the state and requirements before publication
  (`PHASE_3_DOC.md:1255-1273,2285-2293`). The catalog can subsequently issue another valid state
  through `updateState` (`:1645-1654`). `SourceMaterializer.materialize` nevertheless takes a
  caller-supplied state (`:1084-1090`), and the binding contract requires only validation against
  the configuration's catalog, not semantic equality with `options().state()`
  (`:2409-2414,2639-2642`). The same catalog-only boundary appears on runtime program-state
  evaluation (`:2556-2564`). RESEARCH orders option rewriting and conditional evaluation before
  declaration/resource scanning (`RESEARCH.md:497-500`), so a changed state can alter source and
  resource needs. A distinct materialization fingerprint identifies the transformed artifact but
  does not republish matching `ResourceRequirements` or make a mixed configuration coherent; §5
  also forbids consumers from rescanning the pack (`PHASE_3_DOC.md:2740-2743`).
- **Severity:** correction.
- **Required correction:** Bind the runtime materializer to `PackConfiguration.options().state()`—
  preferably by removing the caller-supplied state—or reject a supplied state unless its complete
  value map is semantically equal to that finalized state, not merely issued by the same catalog.
  Apply the same finalized-state rule to runtime program-state evaluation, while separating any
  deliberately supported preview API from runtime configuration consumption. State that applying
  an updated option requires persistence/application followed by a fresh atomic load before Phase 4
  materializes. If arbitrary-state runtime materialization is intentional, replace the current
  result with a Phase-3-produced coherent analysis/configuration snapshot containing matching option
  state, resource requirements, projections, and fingerprints. Add a test in which one option
  changes a conditional resource declaration and `program.<name>.enabled`.
- **Touches interface/change-trigger region:** yes. The accepted state semantics of
  `SourceMaterializer` and `PackConfiguration.evaluateProgramStates` are binding §5 consumer
  behavior.

### candidate-003 — `PackLoadFailure` invents diagnostic-ID and user-facing-text protocols absent from Phase 1

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §2.2 and §5.1 `PackLoadFailure`; consumed Phase 1
  diagnostics at `docs/phase1/v14/PHASE_1_DOC.md` §4.9.4/§5.3.
- **Claim:** The failure envelope cannot be correlated or localized through the selected binding
  `EngineDiagnostic`/`DiagnosticReporter` contract as written.
- **Evidence:** The canonical record exposes `String summary` and `List<String> diagnosticIds`
  (`PHASE_3_DOC.md:555-558`). Binding §5 calls the summary “user-facing,” calls the strings
  diagnostic IDs, and says the detailed cause is reported through `DiagnosticReporter`
  (`:2729-2737`). The consumed Phase 1 value has severity, channel, `messageKey`, arguments,
  detail, and log channel but no identifier; `DiagnosticReporter.report` returns `void`
  (`PHASE_1_DOC.md:3774-3785`). Phase 1 additionally requires message keys rather than user-facing
  message text to cross the engine seam (`:3793-3797`). The target defines no issuer, embedding,
  lookup, uniqueness scope, lifetime, or mapping for its ID strings, and expressly says no new Phase
  1 runtime interface is assumed (`PHASE_3_DOC.md:2831-2832`). Thus neither interpreting the strings
  as occurrence IDs nor treating the summary as localized output is supported by the dependency
  contract.
- **Severity:** correction.
- **Required correction:** Express failed-load diagnostics using the existing Phase 1 vocabulary:
  retain the closed failure code and carry either a primary immutable `EngineDiagnostic`, an
  immutable ordered diagnostic list, or only the reporter delivery contract. Any user-visible
  primary message must use `messageKey` plus immutable arguments rather than engine-authored display
  text. If stable correlation IDs are genuinely required, request and specify a Phase 1
  `DiagnosticId`/ID-bearing reporter extension, including issuance, association, uniqueness,
  ordering, lifetime, and lookup, before consuming it. Align the canonical declaration, §5.1
  semantics, and failed-load correspondence tests.
- **Touches interface/change-trigger region:** yes. This changes the Phase 7/12-facing load-failure
  declaration or its binding semantics in §5.

### candidate-005 — The standard A–G macro values and classifiers remain under-specified

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §§3.5, 4.4, 5.1, and 8.1.
- **Claim:** `MacroConfiguration` does not yet make the required `MC_GL_VERSION`,
  `MC_GLSL_VERSION`, OS, vendor, and renderer definitions reproducible from its published inputs.
- **Evidence:** The governing Phase 3 specification requires the full standard identity set and
  specifically calls for reuse of the driver-string regex mechanism for GL/GLSL versions
  (`DESIGN.md:1375-1388`); its trust rule permits reuse of the StandardMacros structure only while
  requiring values to be re-derived (`:971-973`). Phase 1 supplies a GL major/minor pair and raw
  driver-reported GLSL, vendor, and renderer strings, and explicitly assigns macro formatting to
  Phase 3 (`PHASE_1_DOC.md:2636-2668`). The target instead states only that these values come from
  `GLCapabilityProfile`, that unspecified ordered case-insensitive regex data classifies vendor and
  renderer, and that wildcard macro families are emitted (`PHASE_3_DOC.md:1770-1783`). It gives no
  exact GL integer formula, GLSL input grammar/encoding or malformed-input disposition, ordered
  vendor/renderer patterns and precedence, exhaustive result macro names/replacements, or exact
  `OsFamily`-to-`MC_OS_*` mapping. Pintonium §7.6 is supporting mechanism evidence, not a substitute
  for the required re-derived values. The concrete test list covers `MC_VERSION`, extensions, and
  only a vendor/renderer OTHER case, not the omitted conversions and classifier branches
  (`PHASE_3_DOC.md:2938-2946`). Different implementations can therefore select different
  pack-visible preprocessor branches while satisfying the present prose.
- **Severity:** correction.
- **Required correction:** Define in §4.4 and the binding §5.1 macro contract the exact
  `MC_GL_VERSION` derivation, anchored GLSL-version parser and encoding with malformed-input result,
  exhaustive `OsFamily` mapping, and ordered case-insensitive vendor/renderer classification table,
  including every emitted macro name, replacement, overlap precedence, and OTHER behavior. Add
  exact boundary vectors for version strings, malformed/decorated GLSL inputs, every OS and
  classifier result, case behavior, overlap, and fallback. Keep these OF compatibility values
  distinct from OQ-7's still-open identity-policy choice.
- **Touches interface/change-trigger region:** yes. These name/replacement values are published
  through `MacroConfiguration` and consumed by materialization, so closing them requires a monitored
  §5 edit.

## 2. Checked and clean

- `candidate-004` is dropped on independent re-derivation and against specifically settled
  material. Section 5 is the phase's exposed named interface/data publication surface, not a list
  of every rendering rule assigned to another phase. The precipitation row explicitly says it has
  no Phase 3 parser/model assertion and gives the exact downstream-owned predicate and test to Phase
  7 (`PHASE_3_DOC.md:1364-1369`), consistent with the ownership table (`:364-378`) and the
  behavior-only manifest exclusion (`:3043-3045`). RESEARCH itself describes precipitation as a
  rendering rule documented alongside F.6 (`RESEARCH.md:1493-1512`). Rounds 5–7 specifically
  created, classified, and freshly reviewed this same behavior-only handoff. No later change to that
  premise justifies reopening it as a missing Phase 3 §5 API.
- The refutation of `candidate-001` correctly observes that an explicit exclusion can narrow a
  positive character class. It does not cure the finding because the excluded “numeric tag forms”
  are never defined with the precision used for `.`/`..`, and Round 49 introduced the current
  grammar without explicit outcome vectors.
- `candidate-002` is not cleared by the earlier catalog-authentication repairs. Those repairs make
  states engine-issued and reject foreign catalogs, but deliberately allow same-catalog updates;
  they do not require equality with the finalized option map from which the configuration's
  requirements and fingerprint were derived.
- `candidate-003` is consistent with the unresolved diagnostic-ID issue identified in Round 35.
  Later public-type closure retained `summary` and `diagnosticIds` but supplied neither an ID-bearing
  Phase 1 protocol nor message-key localization semantics.
- Earlier broad clean statements about standard macro families do not specifically settle
  `candidate-005`'s exact conversion/classifier omission. Round 49 repaired option-macro values and
  StandardMacros provenance, but its resulting A–G text still contains only wildcard families and
  unspecified regex data.
- The finder-reported clean areas were rechecked. Round 49's Java-25 option serialization, exact
  eight-key projection, six-versus-eight milestone split, schema-13 texture nominal algebra,
  37-value `ColorInternalFormat`, schema-12/13 rejection tests, conformance links, decisions, and
  checklist propagation remain internally consistent apart from the distinct findings above.
- The remaining examined dependency seams, discovery and persistence protocols, dimensions,
  source/include identities, geometry translation, custom textures and expressions, resource
  aggregates, ID-map provenance, program-state shapes, mandatory thirteen-section structure,
  Pintonium pitfall rows, pure-`:engine` placement, and OQ-7 spike yielded no additional finding
  from the supplied candidate set.
- No candidate was eliminated before adjudication, no Gate drop was revived, and no admitted
  candidate duplicates another admitted repair.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: yes

Four correction-sized findings survive. Each is locally repairable without rebuilding the Phase 3
architecture, so `FAIL` is not warranted; the nonzero correction count makes literal `PASS`
unavailable. All four ordered repairs change declarations or consumer-visible semantics in the
manifest-declared `cross-phase-interfaces` region.

The recent correction trend remains non-converged: Rounds 48–50 move `5 -> 6 -> 4`, which improves
in this round but is not strictly decreasing across the window and has not reached zero. The next
required action is a scoped fix-up resolving candidates 001, 002, 003, and 005 and recording the
resolutions in this review. Because those repairs alter the monitored §5 interface contract, a fresh
whole-document Phase 3 verification round is required before the phase can close or be consumed as
a verified dependency.

## Resolutions

### candidate-001 — applied

Sections 4.9, 5.1, 8.1, 11.1, and 12 define numeric exclusion as exactly an unnamespaced,
unslashed `[0-9]+` tag ID. `%123` rejects; `%minecraft:123`, `%123:foo`, and `%123/foo` accept as
`minecraft:123`, `123:foo`, and `minecraft:123/foo`; RESEARCH still does not settle the spelling.

### candidate-002 — applied

The public materializer and runtime program evaluator no longer accept caller-supplied option state.
Both use the owning configuration's finalized `options().state()`. Catalog updates remain available
for preview/persistence, but applying one requires persistence followed by a fresh atomic load.
The added coherence test changes a conditional resource and `program.<name>.enabled` together and
checks finalized state, requirements, materialized source, evaluated state, and fingerprints.

### candidate-003 — applied

`PackLoadFailure` now carries its closed code and one non-null Phase-1 `EngineDiagnostic`, not a
display summary or invented ID list. Phase 3 freezes loader-neutral arguments, sanitizes detail,
reports that exact value once, and leaves localization to Phase 7/12 via `messageKey` plus `args`.
Tests cover every failure code, reporter correspondence, immutability, and prohibited payloads.

### candidate-005 — applied

Sections 4.4 and 5.1 bind the GL formula, anchored GLSL grammar and invalid-request outcome,
exhaustive OS map, and ordered case-insensitive vendor/renderer tables, including emitted A–F
names, replacements, overlap, and OTHER behavior. Boundary tests cover version limits, decorated/
malformed GLSL, every classifier row, case, overlap, fallback, and unlisted-macro absence; these OF
compatibility values remain separate from open OQ-7 policy.

### Interface and schema disposition

All four repairs intentionally change the manifest-monitored §5 interface region, so the declared
fresh whole-document verification trigger fires. The failure/runtime method changes do not alter a
`PackConfiguration` record component or its meaning/default; schema 13 therefore remains current.

### Notes deferred

None. The adjudicator admitted no notes in round 50.
