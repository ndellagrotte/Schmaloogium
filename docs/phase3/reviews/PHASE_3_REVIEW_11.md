# Phase 3 verification review — round 11

## 0. Method and reading order

I independently re-derived the gated candidate from the complete Phase 3 target, the selected
Part I, Phase 3 specification, document gate, and mandatory-template material in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, and the selected binding Phase 1 §5 contract. I searched the whole
target for equivalent tests and implementation hooks covering discovery generations, stale or
unknown candidate IDs, normalized-directory independence, and `INVALID_SELECTION`. Only after
settling the candidate's disposition did I read prior reviews 1 through 10, in order, including
their resolutions.

There were no deviations from the supplied reading contract, no network use, and no agent fan-out.
The dispatched-role exception in the supplied `verify-loop` skill was followed: I did not invoke
the verification harness or start another session. I read no forbidden source. The Gate reported
no drops. `candidate-001` was eliminated at Refute and was not revived.

## 1. Findings

### candidate-002 — Latest-generation and invalid-selection behavior lacks a named test

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:909-920`, `:1042-1047`, and `:1280-1283`

**Claim:** The newly published discovery-generation contract is not backed by executable,
reviewable verification of generation supersession and stale or unknown filesystem selections.

**Evidence:** Section 5 makes a filesystem candidate ID valid only for the latest completed
discovery result from the same `PackFrontEnd` instance and normalized `shaderpacksDirectory`;
a later result for that directory makes an older ID stale, and stale or unknown IDs must fail as
`INVALID_SELECTION` (`docs/phase3/v1/PHASE_3_DOC.md:909-920`). The discovery test list covers
ordering, nested roots, sentinels, successful `Off`, internal snapshots, path safety, archive
lifetime, and archive limits, but names no test for generation supersession, stale or unknown IDs,
directory normalization, or directory independence (`docs/phase3/v1/PHASE_3_DOC.md:1042-1047`).
The closest other hook, `loadRequest_selectionDependentValidation`, is merely named without
assertions tying it to any of those behaviors or to the required failure code
(`docs/phase3/v1/PHASE_3_DOC.md:1069-1073`). Finally, the discovery implementation checklist
directs implementers only to the existing generic `discovery_*`, `pathRejects*`, and
`archiveLease*` families (`docs/phase3/v1/PHASE_3_DOC.md:1280-1283`).

**Severity:** correction. Add explicit named headless coverage for supersession after a later
completed discovery of the same normalized directory, stale and unknown IDs returning
`INVALID_SELECTION`, and independence across distinct normalized directories. Reference the exact
test hooks from the discovery implementation checklist. Defining these assertions under the
existing selection-validation test is acceptable if the document states them explicitly.

**Touches interface/change-trigger region:** no. The binding §5 behavior is already specified;
this correction makes its verification and implementation hooks executable without changing that
contract.

## 2. Checked and clean

- The Round 10 discovery publication is internally coherent: it exposes an immutable deterministic
  snapshot, opaque identity and generation values, consumer-required display/status data, and
  attributed diagnostics without leaking paths, roots, hashes, or archive lifetimes.
- The latest-completed-generation rule consistently binds validity to the same normalized
  directory and `PackFrontEnd` instance, gives Phase 7 and Phase 12 valid discovery entry points,
  and closes stale or unknown selection behavior as `INVALID_SELECTION`.
- The selected Phase 1 contracts consumed by Phase 3 remain supported, and the missing jcpp build
  allowance remains an explicit upstream request rather than an assumed dependency contract.
- The conformance sweep found no additional unmapped in-scope contract family or unsupported
  mapping in the Phase 3 conformance map.
- `candidate-001`, concerning omissions from the complete publication surface, remains cleared by
  Refute and was not admitted. Whole-target re-derivation found the newly public discovery and
  selection contracts present in §5.
- Prior reviews do not settle `candidate-002`. Round 10 added the generation and stale-ID behavior
  to the interface, but its resolution did not add a named test or exact assertions for that new
  behavior.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The surviving issue is a bounded test-plan correction, not a structural miss. The correction
count remains one from Round 10 to Round 11: the prior interface omission was repaired, and this
round identifies the narrowly consequent executable-coverage gap, so literal convergence has not
yet been reached. The next required action is a scoped fix-up adding the named assertions and
checklist hook, followed by a fresh verification round. Because the correction does not require a
change to §5, the interface change trigger does not fire in this adjudication.

## Resolutions

### candidate-002 — applied

Re-derived from the existing §5 validity rule and Phase 3's headless-test obligation. Section 8.1
now names coverage proving that a later completed discovery supersedes IDs only for the same
normalized directory, that distinct normalized directories remain independent, and that stale or
unknown filesystem IDs return `INVALID_SELECTION`. Section 12 names both exact hooks in the
discovery implementation item.

The `cross-phase-interfaces` region is unchanged, so its fresh-verification change trigger does
not fire from this fix-up. A fresh round remains required to review the new test-plan prose.

### Notes deferred

None; the adjudicator admitted no notes.
