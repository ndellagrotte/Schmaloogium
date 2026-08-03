## 0. Method and reading order

This adjudication independently re-derived the sole supplied candidate against, in order:

1. the Phase 4 target, including the public `ProgramStateBundle`, the state-adaptation step, and
   the manifest-declared cross-phase interface region;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in `docs/phase1/v14/PHASE_1_DOC.md:4160-4265` and
   `docs/phase3/v1/PHASE_3_DOC.md:1300-1478`; and
5. the listed supporting evidence where it bore on the candidate.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_21.md` read, in round order. Their settled corrections do not
define an absent viewport-scale mapping and do not clear the present candidate.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — `ProgramStateBundle` cannot represent an absent viewport scale

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:444-453`,
  `docs/phase4/v1/PHASE_4_DOC.md:988-990`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1475-1479`
- **Claim:** Phase 4 cannot losslessly and deterministically adapt Phase 3's evaluated per-program
  state into the exported `ProgramStateBundle` when the viewport-scale property is absent.
- **Evidence:** Phase 3's binding contract defines each evaluated record with
  `Optional<ViewportScale> scale` and explicitly says an otherwise absent state has empty
  optionals (`docs/phase3/v1/PHASE_3_DOC.md:1334-1341`). It also says neither downstream view
  defaults render state beyond its stated absence results
  (`docs/phase3/v1/PHASE_3_DOC.md:1183-1187`). Phase 4 nevertheless declares
  `ProgramStateBundle.viewportScale` as a required `ViewportScale` while retaining optionals for
  alpha and blend (`docs/phase4/v1/PHASE_4_DOC.md:444-453`). Phase 4 expressly owns adapting the
  evaluated state into that bundle (`docs/phase4/v1/PHASE_4_DOC.md:988-990`), but supplies no
  default, sentinel, or normalization for an empty scale. The bundle and its scale are exposed to
  Phases 5, 6, 7, and 8 (`docs/phase4/v1/PHASE_4_DOC.md:1475-1479`), so downstream implementers
  would have to invent whether absence means an identity viewport or some other concrete value.
- **Required correction:** Make `ProgramStateBundle.viewportScale` an
  `Optional<ViewportScale>` and specify in the detailed adaptation and §5 contract that empty
  means no viewport override. Alternatively, define an exact semantics-preserving normalization,
  including whether the distinction between absence and an explicitly configured identity scale
  is intentionally discarded, and apply it consistently in both locations.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

- The finder-reported new-surface examination remains clean: the §0.25 publication-failure test
  additions match the detached-projection and recovered-off lifecycle and do not independently
  change §5.
- The remaining exposed stage traversal, descriptor, candidate/publication ownership, generation,
  uniform-layout, barrier, fixed-attribute, and Phase 1 facade contracts examined by the interface
  lens produced no additional admitted finding.
- The conformance map covers the governing stage registry, classic catalog and fallback, compile
  and link lifecycle, attributes, geometry, per-program state, barriers, failure handling, and
  reload invalidation requirements; no conformance-map finding survives.
- No candidate was cleared or dropped on re-derivation. Prior reviews contain no settled
  absent-scale rule that reconciles Phase 3's optional input with Phase 4's required field.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a bounded interface-contract correction, not a structural miss requiring a
rebuild. Literal `PASS` is unavailable while one correction remains.

The prior correction trend is 5, 3, 1, 3, 2, 2, 2, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 0, 1, 1, 1.
This round remains at one correction, so the evidence does not establish convergence or permit
closure.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. Because the
correction changes `ProgramStateBundle` and the binding §5 `cross-phase-interfaces` region, a
fresh verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — resolved

Re-derived against Phase 3's binding `EvaluatedProgramState`: its scale is optional, absence is a
defined state, and downstream consumers may not invent a default. `ProgramStateBundle.viewportScale`
is now `Optional<ViewportScale>`. The §4.7 adapter preserves the Phase 3 optional as-is and states
that empty means no viewport override rather than an identity scale; §3.3 distinguishes absence
from a present identity value. Binding §5 exposes the same absence semantics to consumers.

This intentionally changes the manifest-declared cross-phase interface region. Phase 4 remains
unverified until a fresh whole-document review returns literal PASS.

### Notes deferred

None.
