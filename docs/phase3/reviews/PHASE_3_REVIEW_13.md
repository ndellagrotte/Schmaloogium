# Phase 3 verification review — round 13

## 0. Method and reading order

I independently re-derived both gated candidates from the complete Phase 3 target, the selected
Part I, Phase 3 specification, document gate, and mandatory-template material in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, the selected binding Phase 1 §5 contract, and the permitted
supporting evidence. I searched the target for selection-dependent validation order, equivalent
`Off` and `Internal` directory rules, and any downstream ownership or equivalent semantic coverage
for `shadowDistanceRenderMul`. Only after settling both candidate dispositions did I read prior
reviews 1 through 12, in order, including their resolutions.

There were no deviations from the supplied reading contract, no network use, and no agent
fan-out. The dispatched-role exception in the supplied `verify-loop` skill was followed: I did not
invoke the verification harness or start another session. I read no forbidden source. The Gate
reported no drops, and no candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — Canonical-directory rule contradicts the `Off` load contract

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:257-260`, `:911-920`, and `:933-939`

**Claim:** The binding load interface does not determine whether an `Off` selection succeeds
without accessing other request inputs or first validates `shaderpacksDirectory` and can fail as
`INVALID_REQUEST`.

**Evidence:** The public load contract says an `OFF` request returns `PackLoadResult.Off` without
opening an input or reporting failure (`docs/phase3/v1/PHASE_3_DOC.md:257-260`), and the pipeline
states that `OFF` short-circuits before the steps followed by every other selection
(`docs/phase3/v1/PHASE_3_DOC.md:325-326`). Section 5 instead says both `discover` and `load`
canonicalize and require a readable real `shaderpacksDirectory`, with every invalid directory
making `load` return `Failed(INVALID_REQUEST)`
(`docs/phase3/v1/PHASE_3_DOC.md:911-920`). The same binding region then repeats that `Off`
accesses no other input and scopes the remaining required request data to every non-`Off` request
(`docs/phase3/v1/PHASE_3_DOC.md:933-939`).

An `Off` request carrying a null, nonexistent, unreadable, or resolution-failing host directory
therefore has two incompatible specified outcomes. The directory-failure test is stated for both
operations (`docs/phase3/v1/PHASE_3_DOC.md:1064-1066`) but does not define selection or validation
precedence.

**Severity:** correction. State validation order explicitly. If `Off` is intended to remain the
unconditional short circuit, inspect the selection first and return `PackLoadResult.Off` before
accessing or validating any other request field. Scope host-directory canonicalization and
discovery-generation validation to filesystem loads, and state separately whether `Internal`
ignores the host directory. Add named cases covering `Off` and `Internal` with null, invalid, and
provider-failing host paths.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- Host-directory real-path aliasing, provider-defined case identity, distinct-directory generation
  independence, stale/unknown candidate rejection, and their named test/checklist hooks are
  otherwise coherent.
- The discovery/load surface publishes opaque IDs and generations, closed candidate and selection
  types, immutable results, selection-dependent internal-source access, and attributed failure
  behavior without leaking filesystem paths or leases.
- Appendix F.1, Appendix F.2–F.8, and the structural Appendix A.3 directive-to-field map remain
  complete apart from no admitted additional defect; the required Pintonium pitfalls retain named
  conformance coverage.
- The selected Phase 1 contracts are consumed honestly, and the missing jcpp build allowance
  remains an explicit upstream request rather than an assumed dependency contract.
- `candidate-002` is cleared on re-derivation. Phase 3 preserves the complete numeric value in
  `ShadowConfig.distanceRenderMultiplier`
  (`docs/phase3/v1/PHASE_3_DOC.md:466-470`). The governing requirement characterizes this table as
  directive-to-field wiring coverage (`docs/design/v2.0-RC3/DESIGN.md:1401-1403`), while Phase 3
  explicitly assigns shadow-rendering behavior to Phase 8
  (`docs/phase3/v1/PHASE_3_DOC.md:143-151`) and publishes shadow requirements to that owner
  (`docs/phase3/v1/PHASE_3_DOC.md:903-906`). Requiring this parser phase to interpret or test the
  downstream `> 0` activation rule would cross that ownership boundary; the stored float loses no
  information Phase 8 needs.
- Prior reviews do not settle `candidate-001`. Round 12 introduced the universal directory
  validation wording while preserving the pre-existing unconditional `Off` contract; its
  resolution defines directory identity and failures but does not resolve selection-first
  precedence.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The surviving contradiction is a bounded public-contract correction, not a structural miss
requiring a rebuild. The correction count remains one across Rounds 10–13, so it is not strictly
decreasing and the convergence warning remains active; Round 13 exposes a validation-precedence
contradiction introduced by Round 12's interface repair rather than evidence of literal
convergence. The next required action is a scoped fix-up resolving `candidate-001` and appending
this review's `## Resolutions`. Because the correction changes §5's binding interface region, the
interface change trigger fires and a fresh verification round is required before Phase 3 may
close.

## Resolutions

### candidate-001 — corrected

Re-derivation confirmed that the universal `load` directory rule contradicted the pre-existing
selection-first `Off` contract. Section 5 now requires `load` to validate the selection first:
`Off` returns immediately without reading any other request field, `Internal` ignores
`shaderpacksDirectory`, and only `Filesystem` validates the canonical host-directory identity and
discovery generation. The invalid-directory failure wording is correspondingly scoped to
filesystem loads. Section 8 adds named null, invalid, and provider-failure host-path cases for both
`Off` and `Internal`. Section 0.16 records only the compact correction. The §5 interface changed,
so a fresh verification round is required before Phase 3 may close.

### Notes deferred

None.
