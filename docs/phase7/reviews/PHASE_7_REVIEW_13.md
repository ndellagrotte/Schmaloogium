# Phase 7 Verification Review — Round 13

## 0. Method and reading order

I independently re-derived both supplied candidates from the whole Phase 7 target, the governing
RC3 Phase 7 specification and mandatory-document rules, and the manifest-selected Phase 2–6
binding contracts. I searched the whole target for equivalent validation, visibility, ownership,
and capability rules before deciding either candidate. Only after reaching those judgments did I
read `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_12.md`, in order, to compare against settled material.

There were no reading-order deviations, no network use, and no agent fan-out. The canonical engine
reported no Gate drops. `candidate-003` had been eliminated during Refute and therefore was not
eligible to become a finding.

## 1. Findings

### candidate-001 — `ReloadIntent.Select` does not require its defining `PACK_SELECTION` reason

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1234`–`:1245`, `:1379`–`:1384`
- **Claim:** The exposed reload request is not a closed, internally consistent distinction between
  selection changes and active-pipeline rebuilds because it admits a `Select` intent whose reason
  set omits `PACK_SELECTION` without defining that mismatch as invalid.
- **Evidence:** `ReloadRequest` independently carries a `ReloadIntent` and `ReloadReasons`, and the
  `Select` variant itself imposes no reason invariant (`docs/phase7/v1/PHASE_7_DOC.md:1234`–`:1239`).
  The detailed interface rule rejects the opposite mismatch by forbidding `PACK_SELECTION` on
  `RebuildActive`, but gives no complementary requirement or rejection disposition for `Select`
  (`docs/phase7/v1/PHASE_7_DOC.md:1379`–`:1384`). The lifecycle design distinguishes
  selection-change intents from identity-matched active rebuilds while retaining trigger reasons
  for diagnostics (`docs/phase7/v1/PHASE_7_DOC.md:787`–`:791`). The generic validation rule rejects
  only an empty reason set, so a nonempty but semantically mismatched set remains valid under the
  written contract (`docs/phase7/v1/PHASE_7_DOC.md:1350`–`:1354`).
- **Required correction:** Require every `ReloadIntent.Select` request to include
  `ReloadReason.PACK_SELECTION` and define omission as `ReloadRejection.INVALID_REQUEST`; retain the
  existing prohibition on `PACK_SELECTION` for `RebuildActive`.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the validation rule belongs to the exposed
  reload contract in §5, so a fresh verify round is required before Phase 7 can close.

### candidate-002 — Phase 12 is simultaneously granted and forbidden direct publication access

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1179`–`:1185`, `:1392`–`:1397`, `:1526`
- **Claim:** The binding interface both exposes a caller-paired pipeline publication capability to
  Phase 12 and requires Phase 12 to mutate pipelines only through `ShaderReloadController`.
- **Evidence:** `PipelineCoordinator.buildAndPublish` accepts a `PipelineBuildRequest` containing
  independently supplied `PackSelection` and `PackConfiguration`
  (`docs/phase7/v1/PHASE_7_DOC.md:1179`–`:1185`). The exposed-contract table expressly names
  “Phase 12 reload/selection” as a consumer of that mutation-capable contract, while separately
  promising that Phase 12's `ShaderReloadController` never accepts a caller-paired
  selection/configuration (`docs/phase7/v1/PHASE_7_DOC.md:1392`–`:1397`). The downstream hand-off is
  categorical: Phase 12 must use `ShaderReloadController` and never publish Phase 3/4/5 objects
  independently (`docs/phase7/v1/PHASE_7_DOC.md:1520`–`:1527`). The scope boundary likewise says
  Phase 7 exposes Phase 12 only the safe reload controller and lifecycle status
  (`docs/phase7/v1/PHASE_7_DOC.md:211`–`:212`). No whole-target visibility or capability rule makes
  the coordinator internal or limits Phase 12 to its read-only `current()` operation.
- **Required correction:** Make `PipelineCoordinator`, `PipelineBuildRequest`, and their publication
  operation internal composition contracts consumed only by `ShaderReloadController` and bootstrap;
  remove Phase 12 from their mutation-contract consumer list. If Phase 12 needs coordinator
  diagnostics, expose a separate read-only status view that cannot build or publish.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the conflicting capability and consumer
  assignments are in §5, so a fresh verify round is required before Phase 7 can close.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The internal-pack source/manifest split and
R7-9 gate are synchronized across the interface, tests, staging, decisions, and checklist. The
engine-flag ownership, decision provenance, and conformance rows are consistent. The remaining
Phase 2–6 dependency consumptions align with their manifest-selected binding contracts, and the
pending R7-1 through R7-9 changes remain explicitly gated. The Phase 8–14 hand-offs other than the
admitted Phase 12 coordinator contradiction are bounded by named ownership and lifecycle rules.
The frame-flow, Appendix A.1 programs, §7.1 hook needs, seven-row reference timeline, internal/off
lifecycle, engine flags, Appendix E coverage, OQ-3/OQ-4 spikes, and milestone gates remain mapped.

No supplied adjudication candidate was cleared on independent re-derivation. Prior reviews do not
settle either admitted defect: Round 12 introduced the closed reload intents but did not establish
the `Select` reason invariant, and no prior disposition reconciles the newly explicit Phase 12
coordinator consumer assignment with the controller-only downstream hand-off. The eliminated
`candidate-003` remains dropped and yields no finding.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are localized interface corrections; neither requires a structural rebuild. The
correction count moves from three in Round 12 to two in Round 13, but the longer trend remains
non-convergent (Rounds 11–13: 3 → 3 → 2, not strictly decreasing). Apply both corrections in one
fix-up, record their resolutions, and because both alter the §5 change-trigger region, run a fresh
verification round before Phase 7 can close. Literal PASS remains required for closure.

## Resolutions

### candidate-001 — applied

Re-derived the reload algebra across §4.8 and §5.1. `Select` now requires
`ReloadReason.PACK_SELECTION`, with omission rejected as `ReloadRejection.INVALID_REQUEST`; the
existing prohibition on `PACK_SELECTION` for `RebuildActive` remains. This closes both mismatch
directions while preserving reasons as diagnostics.

### candidate-002 — applied

Re-derived the capability boundary from §1.2, the §5.1 schemas/table, and the Phase 12 hand-off.
`PipelineCoordinator`, `PipelineBuildRequest`, and publication results are now explicitly internal
composition contracts available only to `ShaderReloadController` and bootstrap, and Phase 12 was
removed from their consumer row. Phase 12 retains only the safe controller mutation surface and the
already separate read-only cache diagnostics surface.

Both corrections change §5, so the manifest change trigger requires a fresh verify round before
Phase 7 can close.

### Notes deferred

None.
