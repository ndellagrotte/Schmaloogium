## 0. Method and reading order

This adjudication independently re-derived both supplied candidates against, in order:

1. the whole Phase 4 target;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding contracts in `docs/phase1/v14/PHASE_1_DOC.md:3944-4039` and
   `docs/phase3/v1/PHASE_3_DOC.md:890-1015`; and
5. the listed supporting evidence where it bore on a candidate.

Only after those interpretations were settled were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_9.md` read, in round order. Their resolutions establish the
evolving public registry, barrier, publication, and relationship-map surface, but do not settle
either defect admitted below. There were no reading-set deviations, no network use, no agent
fan-out, no forbidden-source use, and no Gate drops. Candidate-003 was eliminated by Refute before
adjudication. The canonical engine had already dispatched this atomic adjudication role, so the
verification harness was not invoked.

## 1. Findings

### candidate-001 — Relationship map derives generation from the Phase 6 branch

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:490-495`
- **Claim:** The corrected relationship map still contradicts the binding generation ownership
  and consumption contract.
- **Evidence:** The map places the vertical connector to
  `generation → Phase 12/caches` directly beneath and aligned with the `Phase 6` branch
  (`docs/phase4/v1/PHASE_4_DOC.md:490-495`). That visual relationship makes generation appear to
  be derived from Phase 6 rather than from the publication snapshot. The binding interface instead
  defines `PublishedRegistry.generation` as changing on accepted registry/off publication or
  forced `RecoveredOff`, and assigns inequality polling to Phase 12 and derived caches
  (`docs/phase4/v1/PHASE_4_DOC.md:1177`). Correct prose elsewhere confirms the intended contract
  but does not cure the contradictory architecture map.
- **Required correction:** Redraw `generation → Phase 12/caches` as a direct branch from
  `PublishedRegistry`, visually separate from the Phase 5, Phase 6, and Phase 7/8 consumer
  branches.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

### candidate-002 — Phase 10 is promised slot descriptors without a public delivery path

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:270-275`,
  `docs/phase4/v1/PHASE_4_DOC.md:368-373`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1169-1172`
- **Claim:** The binding interface promises `ProgramSlotDescriptor` and
  `ProgramStateBundle` to Phase 10 without specifying how Phase 10 obtains the descriptor.
- **Evidence:** `StageRegistry` traverses and looks up only `PassDescriptor` values
  (`docs/phase4/v1/PHASE_4_DOC.md:270-275`), while `ResolvedProgramDescriptor` exposes requested
  and effective identities, provider state, fingerprints, and fallback path but no requested or
  effective `ProgramSlotDescriptor` (`docs/phase4/v1/PHASE_4_DOC.md:368-373`). Section 5
  nevertheless lists Phase 10 as a consumer of `ProgramSlotDescriptor` and
  `ProgramStateBundle` (`docs/phase4/v1/PHASE_4_DOC.md:1169-1172`), and the relationship map
  independently depicts both as Phase 10 fixed-attribute/state inputs
  (`docs/phase4/v1/PHASE_4_DOC.md:497`). The later hand-off says only that Phase 10 configures
  vertex sources at the fixed locations (`docs/phase4/v1/PHASE_4_DOC.md:1517`), so it may justify
  withdrawing the broader descriptor promise, but it does not supply the missing access path.
- **Required correction:** Reconcile the Phase 10 contract explicitly. If Phase 10 consumes
  `ProgramSlotDescriptor`, expose a narrow immutable lookup/traversal or include the needed
  descriptor in an existing public projection and state how Phase 10 receives it. If Phase 10
  needs only the fixed attribute table, remove the unsupported descriptor/state consumer promise
  from §5 and correct the relationship map accordingly.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

The following examined areas produced no additional admitted finding:

- The round-9 ownership-map correction otherwise holds: only Phase 7 carries the opaque compiler
  candidate through production composition/publication, and downstream phases receive non-owning
  published views.
- Stage scheduling, sparse 0…99 families, compute placeholders and gbuffers exclusion, classic
  catalog/fallback coverage, compile and failure flow, fixed attributes, barrier behavior,
  publication ownership, and generation invalidation otherwise remain coherently represented.
- Dependency consumption otherwise matches the selected Phase 1 and Phase 3 binding regions,
  including the explicitly unresolved legacy-geometry request.
- Finder-reported conformance areas were retained as clean: the G6 and superset schedules,
  lifecycle and compile flow, barrier and fallback rules, reload generation, and every named
  Appendix A.1 program/fallback row remain mapped.
- Candidate-003, alleging omission of the required per-program state inventory, was eliminated
  before adjudication by the strict refuting disposition and is not admitted or counted.
- Prior round 9 corrected the candidate/publication relationship map but did not settle the new
  generation-connector error or provide a public Phase 10 descriptor delivery route.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are bounded corrections rather than structural misses requiring a rebuild.
Literal `PASS` is unavailable while two corrections remain.

The prior correction trend was 5, 3, 1, 3, 2, 2, 2, 2, 1; this round rises to 2. The new
relationship-map error and unresolved Phase 10 delivery contract show that convergence has not
yet been established, so no closure inference is warranted.

The next required action is a scoped Phase 4 fix-up resolving candidates 001 and 002, appending
this review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. Candidate-002
requires a change to the binding §5 `cross-phase-interfaces` change-trigger region, whether the
unsupported promise is withdrawn or a delivery contract is added. A fresh verification round is
therefore required before Phase 4 can close.

## Resolutions

### candidate-001 — corrected

Redrew §2.3 so `PublishedRegistry` has four sibling branches: Phase 5, Phase 6, Phase 7/8, and
`generation → Phase 12/caches`. Generation no longer descends from or aligns as a continuation of
the Phase 6 branch.

### candidate-002 — corrected

Withdrew the unsupported Phase 10 consumer promise from the broad
`ProgramSlotId`/`ProgramSlotDescriptor`/`ProgramStateBundle` §5 row. Phase 10 owns vertex-buffer
layout and attribute enabling, and this document already exposes the only Phase 10 input it
requires: the fixed attribute table at locations 10/11/12. The relationship map now shows that
narrow table delivery, consistent with §1.2 and §11.4; no new descriptor lookup or projection is
introduced.

### Notes deferred

None. The adjudication admitted no notes.
