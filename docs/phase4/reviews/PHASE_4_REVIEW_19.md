## 0. Method and reading order

This adjudication independently re-derived the sole surviving candidate against, in order:

1. the whole Phase 4 target, with focused checks of availability, fallback resolution, the
   per-slot resolution projection, diagnostics, binding §5, tests, and implementation checklist;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4160-4265` and
   `docs/phase3/v1/PHASE_3_DOC.md:1300-1478`; and
5. the listed supporting evidence where it bore on the candidate.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_18.md` read, in round order. Those reviews establish the
evolving registry, publication, barrier, projection, and interface surfaces, but none settles the
projection-totality defect below. Review 18's literal PASS predates the present §0.22 amendment.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — Per-slot resolution projection does not define a valid row for all failure paths

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:887-934`,
  `docs/phase4/v1/PHASE_4_DOC.md:1391-1400`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1437`
- **Claim:** The public four-status projection is not total for every requested catalog slot and
  planned/build outcome.
- **Evidence:** Planning admits `Failed` after either materialization or GL build, and fallback
  walks past an unavailable requested slot to seek a successful ancestor
  (`docs/phase4/v1/PHASE_4_DOC.md:887-918`). The projection then defines `FAILED` only when the
  requested source failed, requires `sourcePresent=true`, and requires a sanitized non-empty
  driver build log; `ABSENT`, conversely, excludes any unmasked build failure
  (`docs/phase4/v1/PHASE_4_DOC.md:921-934`). Those rules do not assign an unambiguous valid row to
  a missing or disabled requested slot whose fallback walk encounters a failed ancestor and then
  terminates without a successful shader. They also do not guarantee a valid `FAILED` payload for
  `MATERIALIZE`, `CAPABILITY`, `BARRIER`, or another non-driver failure: the failure model lists
  those stages while merely carrying a sanitized driver log, without requiring it to be non-empty
  or defining a general failure serialization into that field
  (`docs/phase4/v1/PHASE_4_DOC.md:1391-1400`). This is consumer-visible because binding §5 exposes
  one complete catalog-ordered list for direct copying by Phases 2 and 7
  (`docs/phase4/v1/PHASE_4_DOC.md:1437`).
- **Required correction:** Define a deterministic projection for every complete fallback walk,
  explicitly including failed ancestors reached from missing or disabled requested slots and
  stating when such failures are masked, confined to the ancestor's own row, or propagated to the
  requesting row. Ensure every failure stage supplies the projection's required non-empty
  sanitized detail, either through a deterministic general serialization in the existing field or
  a deliberate interface revision. Add focused tests for missing/disabled child → failed ancestor
  → terminal resolution and for non-driver materialization/capability failures.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

- The §0.22 virtual-pre amendment is coherent across the typed `PassDescriptor` shape,
  construction invariants, binding §5 exposure, tests, decision D-P4-17, hand-offs, and checklist.
- Candidate/runtime projection ordering, detached handle-free lifetime, independent
  `sourcePresent`, candidate/published value equality, and direct Phase 2/7 consumption are
  consistently specified. The admitted defect is confined to total failure-status grammar.
- Consumed Phase 1 facade, result, handle, recording, state, diagnostic, and fixed-function
  contracts remain within the selected binding surface. Phase 3 configuration, materialization,
  uniform-catalog, geometry, state, resource, fingerprint, and diagnostic contracts are consumed
  without inventing dependency capability.
- The governing Phase 4 conformance surface remains covered across both stage configurations,
  sparse pass families, complete classic program and fallback mappings, compile/link/validate
  lifecycle, fixed attributes, whole-provider state, barrier behavior, ownership, and generation
  invalidation.
- Finder-reported new-surface, interface, and conformance areas are otherwise clean on
  re-derivation. No supplied candidate was refuted, cleared, or dropped. Prior Reviews 1–18 do not
  define the failed-ancestor projection or a guaranteed non-empty projection payload for every
  non-driver failure stage.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a bounded public-contract correction, not a structural miss requiring a
rebuild. Literal `PASS` is unavailable while one correction remains.

The prior correction trend is 5, 3, 1, 3, 2, 2, 2, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, then 0.
This round returns to one correction because the subsequently added §0.22 projection surface is
not total; convergence and closure therefore no longer hold for the current artifact.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. Because the
correction changes the binding §5 `cross-phase-interfaces` change-trigger region, a fresh
verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — corrected

Re-derived against the Phase 4 assignment's complete-catalog projection requirement and the
existing fallback rule that `Missing`, `Disabled`, and `Failed` are all absent while walking to an
ancestor. Section 4.6 now assigns exactly one row after every complete walk: a later successful
ancestor masks earlier failures and yields `CHAIN`; without a successful provider, any encountered
failure takes precedence over an absent terminal and yields `FAILED` for every requesting row on
that walk. Each catalog slot's own row is evaluated independently. Consequently a missing or
disabled child may validly be `FAILED` with `sourcePresent=false` or true, while the failed
ancestor's row reflects its own walk.

The existing `driverLog` wire field is retained to avoid an unnecessary shape revision, but §4.12
now defines its `FAILED` value as deterministic non-empty sanitized general failure detail.
`ProgramBuildFailure.projectionDetail()` serializes the failure stage and stable diagnostic ID,
plus a driver log only when present; multiple failures are joined in fallback-path order. This
covers `MATERIALIZE`, `CAPABILITY`, `BARRIER`, and all other non-driver stages without fabricating
a driver message. Section 5's public contract, the focused test inventory, and the implementation
checklist were updated consistently.

The binding §5 cross-phase interface was intentionally changed. A fresh verification round is
required before Phase 4 can close.

### Notes deferred

None.
