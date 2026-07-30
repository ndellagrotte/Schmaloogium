# Phase 3 verification review — round 16

## 0. Method and reading order

I independently re-derived both surviving candidates from the complete Phase 3 target, the
selected Part I, Phase 3 specification, document gate, and mandatory-template material in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, the selected binding Phase 1 §5 contract, and the permitted
supporting evidence. I searched the complete target for a later current-status statement and for
any declaration or equivalent accessor contract closing the public `ResourceRequirements` type
graph.

Only after settling both candidate dispositions did I read prior reviews 1 through 15, in order,
including their resolutions, with Round 15 read last. There were no deviations from the required
reading order, no network use, and no agent fan-out. The dispatched-role exception in the supplied
`verify-loop` skill was followed: I did not invoke the verification harness or start another
session. I read no forbidden source.

The Gate reported no drops, and no candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — The current verification status still points to round 15

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:129-137`

**Claim:** The document's sole current §G1.3 status no longer identifies the verification round
owed after the latest fix-up.

**Evidence:** The status says that the §0.17 interface addition is unverified “until a fresh round
fifteen returns literal PASS” (`docs/phase3/v1/PHASE_3_DOC.md:129-132`). The immediately following
addendum is headed “Round 15 fix-up” and records a correction to the declared-uniform fingerprint
contract (`docs/phase3/v1/PHASE_3_DOC.md:134-137`). Round 15 therefore did not return a literal PASS
for the current bytes: it produced a correction and the §0.18 fix-up. No later current-status
statement supersedes the one at lines 129–132.

**Severity:** correction. Update the current status to record that Round 15 required the §0.18
fix-up and that the corrected bytes require a fresh Round 16 literal PASS before Phase 3 is
verified and valid as a dependency input. Retain the `v1` open-loop directory rule.

**Touches interface/change-trigger region:** no.

### candidate-002 — `ResourceRequirements` lacks a closed public data contract

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:1013`

**Claim:** The complete Phase 3 publication surface does not give its six named consumers an
implementable shared representation for `ResourceRequirements`.

**Evidence:** `PackConfiguration` directly publishes a `ResourceRequirements resources` member
(`docs/phase3/v1/PHASE_3_DOC.md:268-280`). The detailed design says only that
`ResourceRequirements` “contains” seven thematic groups—minima, per-colortex requirements, shadow
configuration, center-depth state, per-program execution data, constants, and noise—and assigns
subsets to downstream phases (`docs/phase3/v1/PHASE_3_DOC.md:860-872`). Section 5 calls itself the
complete publication surface (`docs/phase3/v1/PHASE_3_DOC.md:995-998`) but its corresponding row
again supplies only the subjects “sizing, formats, clears, routing,
shadow/center-depth/constants/attributes” and names Phases 4, 5, 6, 7, 8, and 10 as consumers
(`docs/phase3/v1/PHASE_3_DOC.md:1011-1015`).

No declaration or equivalent accessor contract elsewhere in the target closes the top-level
components, component types, program and attachment key types, absence/default representation, or
field-level collection semantics. The general schema discipline makes field meaning, executable
defaults, deterministic order, and enum closure public compatibility concerns
(`docs/phase3/v1/PHASE_3_DOC.md:1094-1105`), so consumers cannot consistently invent those details.
This conflicts with the governing requirement that `PackConfiguration` be the single downstream
source for buffer requirements (`docs/design/v2.0-RC3/DESIGN.md:1446-1449`).

**Severity:** correction. Define in §5, backed by the detailed design, the immutable public
`ResourceRequirements` type algebra or a normatively equivalent accessor contract: its top-level
components and accessors, key types, absence/default representation, deterministic collection
semantics, and each named consumer's subset. Existing directive-level bounds, units, and defaults
may be incorporated by precise references rather than duplicated. Keep parser builders private.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The Round 15 fingerprint-cycle correction is coherent across the illustrative records,
  construction procedure, canonical payload encoding, §5 equality promise, named reconstruction
  test, hand-off, and implementation checklist. No further defect in that new surface survived.
- Phase 3's consumed Phase 1 contracts match the selected dependency interface. In particular,
  `GLCapabilityProfile` supplies the complete macro-header input claimed here, no GL service or
  handle is assumed, and the missing jcpp dependency/notice allowance remains an explicit upstream
  request.
- The discovery/load, internal-pack, materialization, geometry-translation, declared-uniform,
  macro-contribution, schema/fingerprint, and failure contracts otherwise retain substantive
  consumer-facing detail.
- The Appendix F and Appendix A.3 mappings, directive stage restrictions, option/profile/screen
  semantics, texture forms, dimension/include behavior, sizing directives, half-life units, and
  standard macro inputs showed no additional surviving conformance defect.
- Prior reviews do not settle either admitted candidate. Round 15's resolution created the need
  for the status update identified by `candidate-001`. Earlier reviews repaired several specific
  publication contracts, and Round 14 found no then-surviving interface candidate, but none
  declares or specifically re-derives the missing `ResourceRequirements` type/accessor algebra.
  A general clean-area statement is not equivalent settled coverage of this newly evidenced
  compile-time contract gap.
- No candidate was refuted or cleared on re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded specification repairs rather than structural misses requiring a rebuild.
Round 14 reached literal PASS, Round 15 found one defect in the later §0.17 interface addition, and
Round 16 rises from one to two corrections: one is the stale status left by that fix-up, while the
other is a distinct public-contract gap. The loop has therefore not converged on the current
bytes.

The next required action is a scoped fix-up resolving both candidates and appending this review's
`## Resolutions`. Because `candidate-002` requires changing the binding §5
cross-phase-interface region, the interface change trigger fires and a fresh verification round is
required before Phase 3 may close.

## Resolutions

### candidate-001 — resolved

Re-derived from the document's addendum sequence and §G1.3 state rather than treating the finding
as authority. The sole current status now records that Round 15 required §0.18, Round 16 required
this fix-up's §0.19, and the resulting bytes require a fresh Round 17 literal PASS. It continues
to state that Phase 3 is not a valid dependency input and that `v1` remains in place while the
loop is open.

### candidate-002 — resolved

Added the immutable public `ResourceRequirements` algebra in §4.7 and made the §5 publication row
point to it. The contract now closes all top-level components; dimension/program and attachment
keys; ordered routing, maps, and sets; the optional legacy-geometry case; typed default/absence
representation; private-builder ownership; and each named consumer's exact projection. Existing
§3.3/§4.7 directive bounds, units, aliases, precedence, and defaults remain normative by precise
reference rather than being duplicated.

This changes the declared `cross-phase-interfaces` region. The manifest trigger therefore fires:
a fresh verification round is required before Phase 3 can close.

### Notes deferred

None.
