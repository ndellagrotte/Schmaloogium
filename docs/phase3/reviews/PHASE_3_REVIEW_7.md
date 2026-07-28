# Phase 3 verification review — round 7

## 0. Method and reading order

I independently re-derived all three gated candidates against the complete Phase 3 target,
the selected Phase 3 specification and document gate in
`docs/design/v2.0-RC3/DESIGN.md`, the contract ground truth in
`docs/research/v1/RESEARCH.md`, and the selected binding Phase 1 §5 contract. I searched the
complete target for equivalent semantics before deciding each disposition. Only after settling
those judgments did I read prior reviews 1 through 6, in order, with their resolutions last.

There were no deviations from the supplied contract, no network use, and no agent fan-out. I did
not use any forbidden source. The Gate reported no drops, and no candidate was eliminated before
adjudication.

## 1. Findings

### candidate-001 — New closed geometry enums contradict the blanket `UNKNOWN` rule

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:253-254,869-878`

**Claim:** The geometry primitive declarations and the binding interface/version discipline give
consumers contradictory enum contracts.

**Evidence:** The target declares `GeometryInputPrimitive` and `GeometryOutputPrimitive` as closed
public enums without an `UNKNOWN` member
(`docs/phase3/v1/PHASE_3_DOC.md:253-254`). Section 5.3 nevertheless states universally that
“All enums have `UNKNOWN`,” then limits that member to forward storage and forbids it as a silently
executable state (`docs/phase3/v1/PHASE_3_DOC.md:869-878`). The latter purpose clause does not
grammatically narrow “All enums,” and these geometry enums are part of the published
materialization contract. A consumer therefore cannot implement both declarations literally.

**Severity:** correction. Qualify §5.3 so `UNKNOWN` is required only for enums intended for
forward-compatible storage, and state that closed executable enums, including the geometry
primitive enums, reject unknown values. Retain the closed geometry sets.

**Touches interface/change-trigger region:** yes.

### candidate-002 — `SourceMaterializer` has no no-translation input for ordinary stages

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:233-251,644-650,839-840`

**Claim:** Phase 4 cannot invoke the published materializer for every shader root without
inventing a geometry plan for roots that have no legacy geometry rewrite.

**Evidence:** Every `SourceMaterializer.materialize` call requires a concrete
`GeometryTranslationPlan`, whose record contains a root, two closed geometry primitives, and a
positive vertex count (`docs/phase3/v1/PHASE_3_DOC.md:233-251`). The execution protocol gives that
plan meaning only for a `.gsh` legacy pair and treats a missing plan as unavailable without
limiting that rule to a root that contains such a pair
(`docs/phase3/v1/PHASE_3_DOC.md:644-650`). The §5 publication row repeats only the concrete-plan
shape and matching rule (`docs/phase3/v1/PHASE_3_DOC.md:839-840`). A whole-target search found no
overload, nullable/optional contract, no-op variant, or equivalent absence semantics for ordinary
`.vsh`, `.fsh`, or non-legacy `.gsh` materialization.

**Severity:** correction. Add an explicit no-translation state to the public materialization
contract and §5.1. Its absence branch must succeed for roots with no attributed legacy geometry
pair, while a recognized legacy pair without a matching translation request returns
`Unavailable`. The concrete representation may be an optional value or a sealed none/translate
request.

**Touches interface/change-trigger region:** yes.

### candidate-003 — Screen auto-widening counts entries instead of options

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:375-378,579-582`

**Claim:** The Phase 3 conformance model changes Appendix F.4's widening threshold from option
count to heterogeneous screen-entry count.

**Evidence:** Ground truth distinguishes option names from `[SUBSCREEN]`, `<profile>`, `<empty>`,
and `*`, then says columns auto-widen beyond 18 **options**
(`docs/research/v1/RESEARCH.md:1475-1477`). All four Phase 3 screen rows instead use “resolved entry
count” (`docs/phase3/v1/PHASE_3_DOC.md:375-378`). The detailed model repeats that 18 entries remain
at two columns and 19 entries widen, and directs Phase 12 to render the result without
reinterpretation (`docs/phase3/v1/PHASE_3_DOC.md:579-582`). Mixed control/layout entries can
therefore trigger widening before the authoritative option threshold.

**Severity:** correction. Define widening by actual option count exceeding 18, excluding
non-option navigation/layout entries. Preserve Phase 12 ownership of deferred `*` expansion, but
state that options produced by that expansion contribute to the downstream option count, and make
the 18/19 boundary test cover mixed entry types.

**Touches interface/change-trigger region:** no.

## 2. Checked and clean

- The Round 6 geometry-plan fields, applicability checks, fixed rewrite ownership, canonical
  fingerprint inputs, and failure disposition are otherwise consistent across the declarations,
  pipeline, tests, checklist, and §5 publication row.
- Raw texture internal-format, pixel-format, and pixel-type domains match the authoritative lists,
  including integer-transfer compatibility and line-local rejection.
- Precipitation is consistently classified as a Phase 7 behavioral handoff rather than a Phase 3
  parser/model assertion.
- Phase 3's consumed Phase 1 contracts for the module seam, `GLCapabilityProfile`, logging,
  diagnostics, debug flag, notice mechanism, and conformance extension point exist in the selected
  binding region. The jcpp dependency remains an honest requested change rather than an assumed
  contract.
- The remaining Appendix F and Appendix A.3 mappings, engine-flag ownership, option/property
  families, directive inventory, ID-map grammar, and required reference pitfalls yielded no
  additional admitted finding.
- No candidate was refuted or cleared on independent re-derivation. Prior rounds do not settle
  candidate-001 or candidate-002: Round 6 introduced the concrete closed plan types but did not
  reconcile the universal enum rule or define plan absence. Round 5's screen finding stated the
  authoritative option threshold, but its resolution changed the target to resolved-entry
  counting; Round 6 merely checked that internally repeated formulation, so candidate-003 is a
  distinct surviving conformance defect in settled material.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three admitted defects are bounded fix-ups rather than structural rebuilds. Candidates
`candidate-001` and `candidate-002` affect the declared cross-phase interface/change-trigger
region; `candidate-003` does not. The round therefore does not converge to PASS, and the prior
correction trend continues: later concrete contracts have exposed two new interface ambiguities,
while the earlier screen correction preserved the wrong counting unit.

The next required action is a scoped fix-up resolving all three findings and appending this
review's `## Resolutions`. Because the fix-up must change §5 for the first two findings, a fresh
verify round is required before Phase 3 can close.

## Resolutions

### candidate-001 — applied

Re-derived the conflict from the declared closed primitive sets and §5.3's universal wording.
Section 5.3 now limits `UNKNOWN` to forward-storage enums and explicitly requires closed executable
enums, including both geometry primitive enums, to reject values outside their declared sets.

### candidate-002 — applied

Re-derived ordinary-stage materialization from the public signature and the legacy-pair execution
protocol. Added the sealed `GeometryTranslationRequest` with `None` and `Translate(plan)` states.
`None` succeeds for a root without an attributed legacy pair; a recognized pair requires a
matching `Translate(plan)` and otherwise returns `Unavailable`. The signature, execution protocol,
§5.1 publication row, and named tests now state the same contract.

### candidate-003 — applied

Re-derived the threshold from RESEARCH Appendix F.4. All screen conformance rows and detailed
semantics now count actual options, excluding navigation/layout entries. Phase 12 still owns
deferred `*` expansion, and options it produces contribute to its resolved count. The renamed
boundary test requires mixed entry types at 18 and 19 options.

### Notes deferred

None.

The §5 cross-phase interface changed for candidates 001 and 002, so the manifest's change trigger
fires: a fresh verify round is required before Phase 3 can close. No correction was refused.
