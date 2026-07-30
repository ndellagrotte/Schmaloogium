## 0. Method and reading order

This adjudication independently re-derived the sole supplied candidate against, in order:

1. the whole Phase 4 target;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4079-4181` and
   `docs/phase3/v1/PHASE_3_DOC.md:890-1015`; and
5. the listed supporting evidence where it bore on the candidate.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_11.md` read, in round order. Those reviews establish the
evolving registry, publication, and interface surface. They do not settle the present mismatch
between the current Phase 4 consumed-contract table and the current manifest-selected Phase 3
binding contract.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — The consumed Phase 3 table assigns legacy geometry to `ResourceRequirements`

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:1233-1238`
- **Claim:** Phase 4's binding consumed-contract inventory attributes data to a Phase 3 contract
  that does not publish that data.
- **Evidence:** Phase 4 separately lists the source/materialization contracts and
  `GeometryTranslationRequest` / `GeometryTranslationPlan`, but then says
  `ResourceRequirements` supplies “legacy geometry and allocation bounds needed for validation”
  (`docs/phase4/v1/PHASE_4_DOC.md:1233-1238`). The manifest-selected Phase 3 binding surface
  instead assigns the attributed legacy pair, rewrite sites, translation selection and
  validation, and resulting fingerprints to `SourceMaterializer`, `MaterializedSource`,
  `LegacyGeometryRewriteSite`, `GeometryTranslationRequest`, and `GeometryTranslationPlan`
  (`docs/phase3/v1/PHASE_3_DOC.md:903-903`). Its `ResourceRequirements` row publishes only
  “sizing, formats, clears, routing, shadow/center-depth/constants/attributes”
  (`docs/phase3/v1/PHASE_3_DOC.md:909-909`). The Phase 4 row therefore instructs implementers to
  expect legacy-geometry data on the wrong dependency interface; its additional “allocation
  bounds” wording is likewise not an explicitly published Phase 3 §5 field.
- **Required correction:** Remove “legacy geometry” and the unsupported allocation-bounds
  attribution from the `ResourceRequirements` row, limiting that row to concerns Phase 3 §5
  actually publishes there. Attribute legacy-pair discovery, rewrite sites, translation
  selection and validation, and materialized fingerprints to the source/materialization and
  geometry-translation contracts exactly as Phase 3 §5 specifies.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

- The finder-reported new-surface checks remain clean: the §0.15
  `CompiledRegistryCandidate.view()` amendment is consistent across its signature, ownership
  lifecycle, §5 exposure, hand-offs, tests, and checklist.
- The candidate view remains non-owning and pre-publication-only. It exposes neither handles nor
  publication credentials, does not transfer ownership, and preserves the compiler candidate as
  the authenticated composition/publication product.
- The governing Phase 4 conformance surface remains covered: the modern and G6 stage shapes,
  classic registry and fallback behavior, compile/link flow, legacy-geometry dependency
  limitation, barrier contract, per-program state, and generation invalidation are represented.
- Other inspected Phase 1 and Phase 3 consumptions did not produce an admitted mismatch.
- No candidate was cleared or dropped on re-derivation. The adjacent dedicated geometry row does
  not cure the affirmative and consumer-visible misattribution in the
  `ResourceRequirements` row.
- Prior Review 11's literal PASS does not clear this candidate. The current defect is supported by
  the present manifest-selected dependency bytes and lies in the binding interface region; a
  prior zero-finding round is evidence of the state it reviewed, not authority to override a
  newly surviving, citation-resolved contract mismatch.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a bounded consumed-contract correction, not a structural miss requiring a
rebuild. Literal `PASS` is unavailable while one correction remains.

The prior correction trend was 5, 3, 1, 3, 2, 2, 2, 2, 1, 2, then 0. This round returns to one
correction after the prior PASS, so the present evidence does not support continued closure or a
convergence claim.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. Because the
correction changes the binding §5 `cross-phase-interfaces` change-trigger region, a fresh
verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — resolved

Re-derived against the manifest-selected Phase 3 §5 table, the consumed-contract inventory now
places legacy-pair discovery, attributed rewrite sites, and materialized-source
contribution/plan/site fingerprints on the source/materialization contracts, and translation
selection and validation on `GeometryTranslationRequest` / `GeometryTranslationPlan`.
`ResourceRequirements` is limited to Phase 3's published sizing, formats, clears, routing,
shadow/center-depth/constants/attributes concerns. No new dependency field or design decision was
introduced.

The target records this bounded correction in §0.16. Because §5.3 changed, the
`cross-phase-interfaces` change trigger fires and a fresh verification round is required before
Phase 4 can close.

### Notes deferred

None.
