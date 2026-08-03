## 0. Method and reading order

This adjudication independently re-derived the sole surviving candidate against, in order:

1. the whole Phase 4 target, with focused checks of candidate-time availability, fallback
   resolution, detached resolution projections, barrier construction and activation, diagnostics,
   binding §5, tests, and the implementation checklist;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4160-4265` and
   `docs/phase3/v1/PHASE_3_DOC.md:1300-1478`; and
5. the listed supporting evidence where it bore on the candidate.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_19.md` read, in round order. Those reviews establish the
evolving registry, publication, barrier, projection, and interface surfaces. Review 19 introduced
and resolved the general non-driver failure-detail requirement, but its resolution does not
reconcile the later-lifecycle `BARRIER` stage with the immutable candidate-time projection.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — Runtime `BARRIER` failures cannot populate the immutable candidate-time resolution projection

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:598-602`,
  `docs/phase4/v1/PHASE_4_DOC.md:904-906`,
  `docs/phase4/v1/PHASE_4_DOC.md:1190-1193`,
  `docs/phase4/v1/PHASE_4_DOC.md:1233-1235`,
  `docs/phase4/v1/PHASE_4_DOC.md:1418-1422`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1463`
- **Claim:** The document cannot satisfy its requirement that `BARRIER` supply detail to a
  `FAILED` resolution row while preserving value-equal, immutable candidate and accepted-runtime
  resolution projections.
- **Evidence:** Resolution rows are computed from source presence and build evidence and must be
  value-equal between a candidate view and its later accepted published view
  (`docs/phase4/v1/PHASE_4_DOC.md:598-602`). The availability algebra introduces `Failed` only
  after materialization or GL build failure (`docs/phase4/v1/PHASE_4_DOC.md:904-906`). Barrier
  construction instead reports its own `BarrierConstructionResult.Invalid`
  (`docs/phase4/v1/PHASE_4_DOC.md:1190-1193`), and participant activation failures occur through
  the closed post-composition barrier results (`docs/phase4/v1/PHASE_4_DOC.md:1233-1235`); neither
  path converts a failure into candidate-time `PlannedAvailability.Failed`. Nevertheless,
  `ProgramBuildFailure.projectionDetail()` expressly names `BARRIER` as a non-driver stage whose
  detail participates in `FAILED` serialization
  (`docs/phase4/v1/PHASE_4_DOC.md:1418-1422`), while binding §5 requires candidate and accepted
  runtime views to expose the same immutable resolution list
  (`docs/phase4/v1/PHASE_4_DOC.md:1463`). The implementation checklist further demands projection
  detail for every failure stage (`docs/phase4/v1/PHASE_4_DOC.md:1925-1929`). There is therefore
  no specified temporal path by which the named `BARRIER` failure can populate the required row.
- **Required correction:** Define the projection-eligible `ProgramBuildFailure` stages explicitly.
  Exclude runtime barrier failures from `ProgramResolutionProjection` and report them only through
  barrier/publication diagnostics, or introduce a distinctly named pre-snapshot barrier-related
  build failure with an explicit conversion to `PlannedAvailability.Failed`. Narrow “every
  failure stage” to the projection-eligible set and test every member while preserving
  candidate/runtime value equality.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

- The round-19 failed-ancestor correction is coherent for candidate-time build failures. Missing
  and disabled children inherit an unmasked failed-ancestor disposition, a later successful
  ancestor masks earlier failures to `CHAIN`, and each catalog slot is evaluated independently.
- The broadened `driverLog` meaning is consistently described as deterministic sanitized general
  failure detail across availability/projection rules, diagnostics, binding §5, tests, and the
  checklist. The admitted defect is specifically the inclusion of a failure stage that has no
  route into that projection.
- Candidate/runtime ordering, detached handle-free snapshot lifetime, independent
  `sourcePresent`, and direct Phase 2/7 copying remain internally consistent.
- Barrier construction and runtime activation/recovery have coherent closed result protocols when
  considered independently of the projection-detail claim.
- The manifest-selected Phase 1 facade and Phase 3 configuration, materialization, uniform,
  geometry, program-state, resource, fingerprint, and diagnostic contracts remain represented
  without inventing dependency capability.
- Finder-reported conformance and interface areas are otherwise clean: both registry
  configurations, complete classic catalog and fallback edges, compile/link flow, fixed
  attributes, per-program state, barrier duties, and reload generation remain covered.
- No supplied candidate was refuted, cleared, or dropped on re-derivation. Prior Reviews 1-19 do
  not define a candidate-time `BARRIER` failure path or permit mutation of the detached projection.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted contradiction is a bounded public-contract correction rather than a structural miss
requiring a rebuild. Literal `PASS` is unavailable while one correction remains.

Round 19 also had one correction, and this round finds a temporal contradiction introduced by that
fix-up's generalized failure-stage language. The current artifact has therefore not converged and
cannot close, although the correction remains narrowly scoped.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. Because the
correction changes the binding §5 `cross-phase-interfaces` change-trigger region, a fresh
verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — resolved

Re-derivation confirmed that resolution rows are fixed from candidate-time source and build
evidence, whereas barrier construction, activation, and publication occur after the detached
snapshot exists. The correction therefore takes the review's first permitted branch: §4.12 now
defines `ProgramBuildFailure` as candidate-build evidence, enumerates its projection-eligible
stages without `BARRIER`, and confines later barrier/publication failures to their existing closed
results and diagnostic channels. `UNEXPECTED_BACKEND` is eligible only when it occurs during
candidate materialization or GL build.

The same boundary is stated in binding §5. The pure-registry tests and implementation checklist
now cover every enumerated eligible build stage, exclude barrier failures from projection, and
preserve candidate/golden/runtime value equality. The compact §0.24 addendum records only this
outcome. The §5 interface region changed intentionally, so a fresh verification round is required
before Phase 4 can close.

### Notes deferred

None. The adjudicator admitted no notes.
