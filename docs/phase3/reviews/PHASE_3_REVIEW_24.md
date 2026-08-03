# Phase 3 Adversarial Review — Round 24

## 0. Method and reading order

I independently re-derived all three candidates from the complete Phase 3 target, then the
manifest-selected governing-design regions, RESEARCH.md, the Phase 1 binding contract, and the
candidate evidence. The permitted Pintonium and Oculus reports were not needed to decide the
candidates. Only after settling each interpretation, severity, and interface classification did I
read prior reviews 1–23, in round order and including their resolutions.

There were no deviations from the required reading order, no network use, no agent fan-out, and no
use of forbidden sources. Under the dispatched-role rule in the supplied `verify-loop` skill, I
did not invoke the verification harness or start another session. No candidate was eliminated
before adjudication, and the Gate reported no drops.

## 1. Findings

### candidate-001 — The sole binding ResourceRequirements contract omits consumer-visible baselines

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1307-1331`.
- **Claim:** Section 5 is not self-sufficient for consumers to construct or interpret absent-directive
  `ResourceRequirements` values, despite expressly claiming to contain every consumer-visible
  default.
- **Evidence:** The binding row supplies zero minima, empty collections, center-depth/noise disabled,
  noise resolution 256, shadow interval 2.0, and created-program instance count 1, but assigns every
  other absence case only an unspecified “typed baseline”
  (`docs/phase3/v1/PHASE_3_DOC.md:1307`). The published record graph includes attachment format,
  clear flag and color; four additional shadow scalars; four smoothing constants; and three world
  constants (`docs/phase3/v1/PHASE_3_DOC.md:1318-1325`) without their absent-directive values.
  Detailed construction text delegates the unlisted values to directive-family defaults
  (`docs/phase3/v1/PHASE_3_DOC.md:1070-1075`), while §5 simultaneously says §§3.3, 4.7, and 4.8 add
  no consumer-visible defaults (`docs/phase3/v1/PHASE_3_DOC.md:1328-1331`). The §3.3 rows for the
  shadow, smoothing, and world fields name destinations but do not state those baselines
  (`docs/phase3/v1/PHASE_3_DOC.md:697-712`). Consumers therefore cannot derive deterministic values
  from the sole binding contract.
- **Severity:** correction. Enumerate in §5 every consumer-visible scalar and created-record
  baseline, including attachment state, shadow projection, smoothing, and world constants, and
  align the explanatory construction text without moving producer grammar into the interface.
- **Touches interface/change-trigger region:** yes.

### candidate-002 — Global revision bookkeeping stops at §0.25 despite the §0.26 fix-up

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:6`, `docs/phase3/v1/PHASE_3_DOC.md:1833-1835`.
- **Claim:** The document-level revision markers do not identify the surface reviewed in Round 24.
- **Evidence:** The header says the last revision is §0.25
  (`docs/phase3/v1/PHASE_3_DOC.md:6`), although the target contains the Round 23 fix-up as §0.26
  (`docs/phase3/v1/PHASE_3_DOC.md:194-197`). The closing status likewise says only that §0.25
  changed §5 and is awaiting a fresh review (`docs/phase3/v1/PHASE_3_DOC.md:1833-1835`), omitting
  the subsequent §0.26 correction. The document remains correctly unverified, but its two global
  pointers identify stale bytes.
- **Severity:** correction. Point the header to §0.26 and revise the closing status to record that
  Round 23 produced the §0.26 surface now pending Round 24 verification.
- **Touches interface/change-trigger region:** no.

### candidate-003 — The sole binding ProgramStateModel contract leaves public types structurally open

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1306-1316`.
- **Claim:** Phases 4 and 5 cannot implement consumption of `ProgramStateModel` and
  `EvaluatedProgramStates` from §5 alone because several named consumer-visible types have no
  binding shape or complete operation contract.
- **Evidence:** The binding row publishes `ViewportScale`, `ProgramEnabledExpression`, and
  `EvaluatedProgramStates` while promising closed variants and value domains
  (`docs/phase3/v1/PHASE_3_DOC.md:1306`). The declarations immediately below close alpha, blend,
  flip keys, and flip overrides only (`docs/phase3/v1/PHASE_3_DOC.md:1313-1316`). Detailed §4.8
  describes expression behavior and evaluation and says Phase 4 receives
  `EvaluatedProgramState` records (`docs/phase3/v1/PHASE_3_DOC.md:1134-1170`), but §5 expressly
  prevents that section from adding consumer-visible shapes or values
  (`docs/phase3/v1/PHASE_3_DOC.md:1328-1331`). Thus the evaluated aggregate/record shapes and
  viewport components remain non-binding, and the expression is neither a closed public algebra
  nor an explicitly opaque value with a complete evaluation API.
- **Severity:** correction. Define in §5 the exact immutable consumer-visible viewport and
  evaluated-result shapes, ordering, defaults, and absence semantics. Define the expression's
  public algebra only if consumers inspect it; otherwise make it explicitly opaque and publish the
  complete evaluation operation and inputs. Producer parser/AST internals need not be exposed.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The finder-reported new-surface, interface, and conformance areas were rechecked. Apart from the
  two admitted §5 completeness defects, `ProgramStateModel` identifiers, closed alpha/blend/flip
  domains, ordering, absence rules, and Phase 4/5 projections are synchronized; the
  `ResourceRequirements` record names, component order, key domains, bounds, units, collection
  order, and consumer assignments are consistent.
- The conformance map covers the examined Appendix F and Appendix A.3 families, the four mandated
  Pintonium pitfalls and tests, discovery, preprocessing, identity macros, ID mappings,
  persistence, and pure-`:engine` placement. No separate unmapped or unsupported contract item
  survives re-derivation.
- Phase 3's declared uses of the Phase 1 module/seam, capability, logging, diagnostics, debug-flag,
  notice, and conformance-extension contracts are supported by the selected dependency interface.
- No candidate was refuted or cleared. Prior Round 23's resolution deliberately made §5 the sole
  binding contract, but it did not settle the newly exposed missing defaults and type shapes;
  those omissions are defects in that repair rather than duplicates of its selector-coverage
  finding. Round 22's PASS applies to the earlier §0.24 surface. Candidate-002 is likewise new
  bookkeeping drift introduced when §0.26 was appended.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three findings are bounded fix-up work; none requires rebuilding the architecture. Round 23
corrected one interface-monitoring defect, but the resulting §0.26 sole-contract repair exposes
two incomplete interface definitions and leaves revision bookkeeping stale. The surface has not
converged to literal PASS.

The next required action is a scoped fix-up resolving all three candidates and appending this
review's `## Resolutions`. Because candidates 001 and 003 require changes inside the declared
cross-phase interface/change-trigger region, a fresh whole-document verification round is required
before Phase 3 may close.

## Resolutions

### candidate-001 — applied

Re-derived from the published record graph and the classic directive contract. Binding §5 now
enumerates every absent-directive value: created attachment state, orthographic shadow state and
all shadow scalars, four smoothing constants, three world constants, feature collections, and
created-program state. The shadow FOV is now `Optional<Float>` because absence selects the
orthographic projection and no finite float can represent that absence without a sentinel. §4.7
now delegates construction defaults to §5 without claiming that §3.3 supplies them.

### candidate-002 — applied

The global header now points to the new §0.27 surface. The compact addendum and closing status
record that Round 23 produced §0.26, Round 24 reviewed it, and this fix-up created §0.27 pending a
fresh whole-document review. This supersedes the requested intermediate §0.26 pointer because the
current fix-up necessarily advances the document revision.

### candidate-003 — applied

Binding §5 now defines `ViewportScale`, makes `ProgramEnabledExpression` explicitly opaque, and
publishes the complete `ProgramStateModel.evaluate` operation with immutable inputs. It closes the
`EvaluatedProgramStates` aggregate and `EvaluatedProgramState` record, including components,
ordering, key union, defaults, absence behavior, expression/profile error dispositions, and the
separate explicit-flip projection consumed by Phase 5. Parser and private AST structure remain
producer internals.

### Interface/change-trigger disposition

Intentional edits changed binding §5 for candidates 001 and 003, including one nested resource
record shape. The declared cross-phase interface trigger therefore fires: a fresh whole-document
verification round is required before Phase 3 can close.

### Notes deferred

None; Round 24 admitted no notes.
