# Phase 3 Adversarial Review — Round 5

## 0. Method and reading order

I independently re-derived every surviving candidate from, in order:

1. `docs/phase3/v1/PHASE_3_DOC.md`;
2. the selected Part I, Phase 3 specification, doc-gate, and mandatory-template material in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the relevant ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding interface region of `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the supplied candidate records and permitted supporting evidence.

Only after settling those judgments did I read
`docs/phase3/reviews/PHASE_3_REVIEW_1.md`,
`docs/phase3/reviews/PHASE_3_REVIEW_2.md`,
`docs/phase3/reviews/PHASE_3_REVIEW_3.md`, and
`docs/phase3/reviews/PHASE_3_REVIEW_4.md`, including their resolutions. I made no deviation from
the assigned reading order, used no network access, performed no agent fan-out, and read no
forbidden source.

The Gate dropped `candidate-001` because its finder quote did not resolve uniquely at the cited
location. I did not revive it or derive a finding from it.

## 1. Findings

### candidate-002 — The assigned ARB-geometry front-end rewrite is absent

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:393` and §5.1

**Claim:** Phase 3 neither implements nor formally contests an obligation assigned by the binding
Phase 1 contract.

**Evidence:** Phase 1 assigns Phase 3 the front-end half of rewriting the legacy
`#extension GL_ARB_geometry_shader4` plus `maxVerticesOut` form upstream of GL, distinguishes that
work from Phase 4's translation strategy, and requires Phase 3 to flag the issue in §5 if it
rejects the placement (`docs/phase1/v14/PHASE_1_DOC.md:3878`). Phase 3 instead maps the pair only
to `LegacyGeometryConfig(extensionEnabled,maxVerticesOut)`
(`docs/phase3/v1/PHASE_3_DOC.md:389-394`). Its declared complete publication surface promises
deterministic materialized source but no ARB-to-core transformation or corresponding handoff
(`docs/phase3/v1/PHASE_3_DOC.md:779-788`), while §5.4 requests only jcpp-related dependency
changes and expressly assumes no new Phase 1 runtime interface
(`docs/phase3/v1/PHASE_3_DOC.md:828-839`).

**Severity:** correction. Specify the Phase 3 front-end transformation in the detailed design and
§5 contract, including deterministic transformed output, attribution, diagnostics, and named
tests, while leaving translation strategy to Phase 4. Alternatively, explicitly contest the
placement in §5.4 and request the additive Phase 1 facade capability.

**Touches interface/change-trigger region:** yes.

### candidate-003 — The precipitation rule has no render-behavior owner

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:370`

**Claim:** The Appendix F.6 precipitation behavior is incorrectly disposed as expression-language
work rather than assigned to a rendering owner.

**Evidence:** The authoritative rule is executable render behavior: precipitation renders when
`biome_precipitation != PPT_NONE`, with rain at `temperature >= 0.15` and snow otherwise
(`docs/research/v1/RESEARCH.md:1505-1506`). Phase 3's sole precipitation row groups that rule with
lossless expression text, assigns it only to Phase 11, and names only an expression-losslessness
test (`docs/phase3/v1/PHASE_3_DOC.md:368-370`). That conflicts with the target's own ownership
boundary, which assigns general render behavior to Phase 7 and limits Phase 11 to evaluation of
`uniform.*` and `variable.*` expressions (`docs/phase3/v1/PHASE_3_DOC.md:118-122`). The governing
doc gate requires every Appendix F key to have an assigned disposition
(`docs/design/v2.0-RC3/DESIGN.md:1451-1453`).

**Severity:** correction. Split precipitation into its own conformance row, preserve the exact
predicate and threshold, assign it to the appropriate render-behavior phase, and name a behavioral
test or explicit downstream handoff.

**Touches interface/change-trigger region:** no.

### candidate-004 — Screen auto-widening omits the exact 18-option boundary

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:359`

**Claim:** The published screens model does not preserve the exact Appendix F.4 auto-widen
semantics.

**Evidence:** The authoritative contract says `screen[.NAME].columns` defaults to 2 and
automatically widens beyond 18 options (`docs/research/v1/RESEARCH.md:1475-1477`). Phase 3 records
only an unspecified “auto-widen hint” for the main screen and gives the named-screen row no
equivalent default or widening rule (`docs/phase3/v1/PHASE_3_DOC.md:357-360`). The complete §5
surface publishes screens through `OptionConfiguration` to Phase 12
(`docs/phase3/v1/PHASE_3_DOC.md:779-790`) and forbids consumers from reinterpreting properties
(`docs/phase3/v1/PHASE_3_DOC.md:799-801`), so Phase 12 cannot independently recover the omitted
boundary from the pack.

**Severity:** correction. Define the main and named-screen handoff so an absent column count
defaults to 2 and automatic widening occurs only above 18 resolved options, with a named boundary
test for 18 versus 19 entries.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The Round 4 `PackLoadResult` algebra is consistent across signatures, invariants, failure prose,
  interface publication, degradation, and tests.
- `MaterializationResult` consistently represents deterministic available or unavailable source
  with attributed diagnostics.
- Option captured-span rewriting and both persistence models have matching ownership and named
  tests.
- The reserved macro contribution is singular and its empty/defined variants, validation,
  insertion point, and downstream ownership are otherwise coherent.
- The remaining Phase 1 module/seam, capability-profile, logging, diagnostics, debug-flag, notice,
  and conformance contracts match the selected binding region.
- Appendix F.1 ownership, the remaining Appendix F and Appendix A.3 rows, the four required
  Pintonium pitfalls, discovery, includes, standard macros, preprocessing, ID mappings, and
  persistence yielded no additional finding.
- Prior-round resolutions do not settle the three admitted candidates: none defines the
  ARB-to-core front-end rewrite, precipitation owner, or 18/19 screen boundary.
- No surviving candidate was refuted or cleared on re-derivation. Gate-dropped `candidate-001`
  remains excluded because its evidence was unverifiable.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three findings are bounded fix-up work rather than structural rebuilds. Although prior rounds
resolved their admitted findings, Round 5 identifies three distinct remaining contract defects,
including two in the binding interface/change-trigger region, so convergence has not reached
literal PASS. The next required action is a scoped fix-up resolving all three corrections,
followed by a fresh verification round because §5 must change. Phase 3 may close only after a
later round returns literal `PASS` with zero blocking findings and zero corrections.

## Resolutions

### candidate-002 — resolved

Re-derived from the binding Phase 1 interface, the split is Phase 3 execution versus Phase 4
strategy. The target now retains attributed legacy-geometry rewrite sites, requires Phase 4 to
supply an immutable translation plan, and makes Phase 3 deterministically transform the `.gsh`
source or return attributed `Unavailable`. The §5 publication contract and named tests expose
that handoff without adding a Phase 1 GL verb.

### candidate-003 — resolved

The Appendix F.6 row now separates lossless expression declarations from precipitation behavior.
It preserves the exact non-`PPT_NONE` predicate and `0.15` rain/snow boundary, assigns execution
to Phase 7, and names a boundary behavior test.

### candidate-004 — resolved

Main and named `ScreenModel`s now resolve absent columns to 2 and enable auto-widening only above
18 resolved entries. The §3 map, detailed model, interface-consumer rule, and named 18/19 boundary
test agree.

### Notes deferred

None. Both §5 corrections changed the declared interface region, so the manifest's fresh-verify
trigger applies before Phase 3 can close.
