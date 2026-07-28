# Phase 3 verification review — round 8

## 0. Method and reading order

I independently re-derived both gated candidates against the complete Phase 3 target, the selected
Part I, Phase 3 specification, document gate, and mandatory-template material in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, and the selected binding Phase 1 §5 contract. I searched the
complete target for equivalent lifecycle and interface semantics before deciding each disposition.
Only after settling those judgments did I read prior reviews 1 through 7, in order, including their
resolutions.

There were no deviations from the supplied contract, no network use, and no agent fan-out. I did
not invoke the verification harness or use any forbidden source. The Gate reported no drops, and
no candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — Load-time materialization cannot obtain the required legacy-geometry translation plan

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:303-308`, `:654-662`, and §5.1

**Claim:** The documented atomic load transaction has a sequencing cycle for an active root with
an attributed legacy geometry pair.

**Evidence:** The load pipeline finalizes options and materializes active roots at step 9, then
scans directives into `ResourceRequirements` at step 10 and publishes `PackConfiguration` only at
step 12 (`docs/phase3/v1/PHASE_3_DOC.md:303-308`). Materialization of a recognized legacy pair
requires `GeometryTranslationRequest.Translate(plan)` and rejects `None`
(`docs/phase3/v1/PHASE_3_DOC.md:654-662`). However, `PackLoadRequest` provides neither a plan nor a
plan-selection callback (`docs/phase3/v1/PHASE_3_DOC.md:190-197`), while the data-flow diagram
makes the published configuration Phase 4's only downstream input
(`docs/phase3/v1/PHASE_3_DOC.md:316-325`). The publication contract then assigns selection of the
request to Phase 4 and validation/rewrite to Phase 3
(`docs/phase3/v1/PHASE_3_DOC.md:843-851`). Phase 4 therefore cannot select the value that eager
load-time materialization requires before the configuration through which Phase 4 receives the
pack model exists.

**Severity:** correction. Define one non-circular lifecycle and align the load sequence,
directive discovery, failure behavior, fingerprints, tests, and §5 contract. Viable repairs
include splitting plan-independent discovery from plan-dependent materialization or exposing an
explicit pre-publication plan-selector input; the architecture need not be rebuilt.

**Touches interface/change-trigger region:** yes.

### candidate-002 — Screen column-resolution semantics are absent from the declared interface region

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:851-854`

**Claim:** The complete Phase 3 publication surface does not state the executable `ScreenModel`
column semantics that Phase 12 must consume.

**Evidence:** Detailed design requires absent columns to resolve to 2, widening only above 18
actual options, exclusion of navigation/layout entries, and inclusion of options produced by
Phase 12's deferred `*` expansion (`docs/phase3/v1/PHASE_3_DOC.md:588-592`). Section 5 identifies
`OptionConfiguration` only as a “source options and organization model” for Phase 12
(`docs/phase3/v1/PHASE_3_DOC.md:851-854`), although it declares its table the complete publication
surface (`docs/phase3/v1/PHASE_3_DOC.md:843-844`) and forbids consumers from reinterpreting
properties (`docs/phase3/v1/PHASE_3_DOC.md:863-865`). Its version discipline also classifies
changed executable defaults as interface changes (`docs/phase3/v1/PHASE_3_DOC.md:883-888`).
Consequently, the operative downstream rules remain outside the manifest-declared
interface/change-trigger region.

**Severity:** correction. Expand the §5 publication contract, either in the
`OptionConfiguration` row or a dedicated `ScreenModel` row, with the default, threshold,
exclusions, and deferred-expansion counting rule already defined in §4.3.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The edited screen semantics are internally consistent outside §5: widening uses actual option
  count, excludes navigation/layout entries, and counts options produced by deferred `*`
  expansion when Phase 12 resolves the columns.
- The geometry request variants, closed primitive sets, plan applicability checks, fixed rewrite
  ownership, canonical fingerprint inputs, and `Unavailable` disposition are individually
  consistent. The surviving defect is their placement in the eager publication lifecycle.
- Phase 3's consumed Phase 1 contracts for the module seam, `GLCapabilityProfile`, logging,
  diagnostics, debug flag, notice mechanism, and conformance extension point exist in the selected
  binding region. The jcpp dependency remains an explicit requested change rather than a silently
  assumed contract.
- The conformance sweep found no additional defect in Appendix F or Appendix A.3 coverage, the
  engine-flag ownership map, standard macros, option/property families, ID-map grammar, or the
  required reference pitfalls.
- Neither candidate was refuted or cleared on independent re-derivation. Prior rounds do not
  settle candidate-001: Round 7 added `None`/`Translate` absence semantics but did not provide the
  required plan during eager load. They also do not settle candidate-002: Round 7 corrected the
  screen counting unit in detailed semantics, but its resolution did not place those semantics in
  §5's declared publication contract.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded fix-up work rather than structural rebuilds, but both require changes to
the declared cross-phase interface/change-trigger region. Round 8 reduces the correction count
from three to two, yet the document has not converged to literal PASS: the previous geometry
interface repair exposed a lifecycle cycle, and the previous screen repair remains incomplete in
§5.

The next required action is a scoped fix-up resolving both findings and appending this review's
`## Resolutions`. Because §5 must change, a fresh verification round is required before Phase 3
may close.

## Resolutions

### candidate-001 — corrected

Re-derived against the atomic-publication rule and Phase 4 ownership: a geometry plan cannot be a
load input because Phase 4 selects it only after receiving `PackConfiguration`. Section 2.3 now
runs a plan-independent analysis pass at load time, preserving the attributed legacy pair and
producing directive/resource data without invoking `SourceMaterializer`. Section 4.5 fixes the
plan-dependent boundary after publication; §4.10 separates the configuration fingerprint from
materialization inputs; §5.1 binds that lifecycle and the corresponding cache-validity rule.
Tests now require a legacy-geometry pack to publish before Phase 4 selects its plan and distinguish
load-semantic from contribution/plan fingerprint changes.

### candidate-002 — corrected

The `OptionConfiguration` row in §5.1 now binds the already-derived screen rules: absent columns
resolve to 2, widening occurs only above 18 actual options, navigation/layout entries are excluded,
and options produced by Phase 12's deferred `*` expansion are included.

### Notes deferred

None; the adjudicator admitted no notes.

### §G1.3 status

Both admitted corrections are applied. Because §5.1 changed, the manifest's interface trigger
requires a fresh verify round before Phase 3 can close.
