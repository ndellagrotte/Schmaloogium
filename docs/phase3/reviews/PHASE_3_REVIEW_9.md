# Phase 3 verification review — round 9

## 0. Method and reading order

I independently re-derived both gated candidates from the complete Phase 3 target, the selected
Part I, Phase 3 specification, document gate, and mandatory-template material in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, the selected binding Phase 1 §5 contract, and the permitted
supporting evidence. I searched the whole target for equivalent screen-resolution and public
request/result semantics before deciding either disposition. Only after settling those judgments
did I read prior reviews 1 through 8, in order, including their resolutions.

There were no deviations from the supplied reading contract, no network use, and no agent fan-out.
I did not invoke the verification harness or use any forbidden source. The Gate reported no drops,
and no candidate was eliminated before adjudication. Pre-existing worktree changes outside the
review allowlist were observed and left untouched.

## 1. Findings

### candidate-001 — Screen auto-widening has a trigger but no executable result

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:389-392`, `:593-597`, and `:866`

**Claim:** Phase 12 cannot deterministically resolve the column count exported by
`OptionConfiguration` after the auto-widening threshold is crossed.

**Evidence:** All four conformance rows say that absent columns default to 2 and auto-widen only
above 18 actual options, but none states the widened integer or an algorithm that produces it
(`docs/phase3/v1/PHASE_3_DOC.md:389-392`). The detailed model repeats the threshold, counting
exclusions, and deferred-`*` rule without defining the resulting column count or whether an
explicit `screen[.NAME].columns=N` bypasses widening
(`docs/phase3/v1/PHASE_3_DOC.md:593-597`). The binding publication row repeats the same incomplete
rule (`docs/phase3/v1/PHASE_3_DOC.md:866`). The assigned pack-author contract confirms an
observable switch to “3 or more columns” above 18 options
(`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:299-305`), but that lower bound is
not itself a unique consumer algorithm. Because §5 exports this as executable screen behavior,
leaving the choice to Phase 12 would require it to invent pack-facing semantics.

**Severity:** correction. Define one deterministic resolved-column contract in §4.3 and §5.1:
state that an explicit valid `N` wins (if intended), give the exact result or calculation for an
absent property above 18 actual options, and make the 18/19 boundary test assert resolved integer
values, including the post-expansion `*` case.

**Touches interface/change-trigger region:** yes.

### candidate-002 — The complete publication surface omits request-input data contracts

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:194-201`, `:856-874`, and `:918-919`

**Claim:** Phase 7 and Phase 12 cannot construct and interpret the public `PackLoadRequest`
contract from Phase 3's binding §5 without guessing constituent types and selection-dependent
rules.

**Evidence:** The public entry-point signature requires `PackSelection`, `RuntimeIdentityData`,
`EngineOptionData`, `InternalPackSource`, and other inputs
(`docs/phase3/v1/PHASE_3_DOC.md:194-201`). Yet §5, while declaring itself the complete Phase 3
publication surface, describes the request/result trio only as an atomic entry point and does not
define those request constituents (`docs/phase3/v1/PHASE_3_DOC.md:856-874`). A whole-target search
finds no definitions for `PackSelection`, `EngineOptionData`, `PackInputLimits`, or
`PackLoadFailure`; `RuntimeIdentityData` receives only a prose field list outside §5. Section 5.4
expressly identifies `RuntimeIdentityData` and `InternalPackSource` as Phase 3 interfaces supplied
by later `:mod` work (`docs/phase3/v1/PHASE_3_DOC.md:918-919`), confirming that these are
cross-phase contracts rather than private implementation details. The governing verification
standard requires everything promised to dependents to be specified rather than gestured at
(`docs/design/v2.0-RC3/DESIGN.md:291-294`), and the mandatory template assigns exposed named data
contracts to §5 (`docs/design/v2.0-RC3/DESIGN.md:811-813`).

**Severity:** correction. Make §5 self-sufficient for callers by defining the closed
`PackSelection` variants, `RuntimeIdentityData`, `EngineOptionData`, and caller-visible
`PackLoadFailure` semantics, including fields, suppliers, validation/defaults, and the
OFF/filesystem/internal rules for `InternalPackSource`. Define `PackInputLimits` under the
provider boundary to the extent Phase 7 must honor it. Exact normative references to complete
definitions elsewhere are acceptable, but the currently undefined public types still require
definitions.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The Round 8 geometry lifecycle repair is consistent across load-time analysis, post-publication
  materialization, geometry request validation, fingerprints, §5, and its named tests.
- Screen counting correctly uses actual options, excludes navigation/layout entries, and includes
  options produced by deferred `*` expansion. The surviving defect is only the unresolved widened
  value and explicit-column precedence.
- Consumed Phase 1 contracts are honestly matched to the selected dependency §5, including the
  module seam, `GLCapabilityProfile`, diagnostics/logging, debug flags, licensing mechanism, and
  conformance support. The missing jcpp build allowance remains requested rather than assumed.
- The Appendix F and Appendix A.3 conformance maps, engine-flag ownership, standard macros,
  option/property families, ID-map grammar, OQ-7 architecture, required Pintonium pitfalls, and
  reserved `centerDepthSmooth` contribution yielded no additional admitted finding.
- No candidate was refuted or cleared on re-derivation. Round 8 moved the screen threshold rule
  into §5 but did not define its result. Earlier request/provider repairs defined the internal
  snapshot protocol, OFF outcome, and schema handling, but did not define the remaining public
  `PackLoadRequest` constituent types or their selection-dependent construction rules.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted findings are bounded interface-contract fix-ups, not structural misses requiring a
rebuild. Although prior rounds have resolved each admitted issue, Round 9 still exposes two
consumer-hittable omissions in the binding publication surface; the document therefore has not
converged to literal PASS. The next required action is a scoped fix-up resolving both corrections
and appending this review's `## Resolutions`, followed by a fresh verification round because §5's
change trigger fires. Phase 3 may close only after a later round returns literal `PASS` with zero
blocking findings and zero corrections.

## Resolutions

### candidate-001 — applied

Re-derived from the Appendix F.4 default, the documented switch to three or more columns above 18
actual options, and the existing exclusion/counting rules. Sections 3.2, 4.3, and 5.1 now define
one result: a valid explicit positive `N` wins; otherwise columns resolve as
`max(2, ceil(actualOptionCount / 9))`. The named boundary test now asserts 18→2, 19→3, 27→3,
28→4 after deferred `*` expansion, and explicit-column precedence.

### candidate-002 — applied

Re-derived from the public signature, the engine-only seam, selection lifecycle, hostile-input
rules, and Phase 1 diagnostic boundary. Section 5.1 now closes `PackSelection`, defines validation
and defaults for `RuntimeIdentityData` and `EngineOptionData`, specifies selection-dependent
`InternalPackSource` access, defines the positive shared `PackInputLimits` provider boundary, and
closes caller-visible `PackLoadFailure` codes and sanitization. Named tests cover request
validation and provider-boundary failures.

The `cross-phase-interfaces` region changed, so the manifest's fresh-verification trigger fires.

### Notes deferred

None; the adjudicator admitted no notes.
