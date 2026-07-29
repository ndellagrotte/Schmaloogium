## 0. Method and reading order

This first-round adjudication independently re-derived every supplied candidate against, in
order:

1. the whole target,
   `docs/phase4/v1/PHASE_4_DOC.md:1-1274`;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:3944-4039` and
   `docs/phase3/v1/PHASE_3_DOC.md:890-1015`; and
5. the listed supporting evidence where it bore on a candidate.

Prior reviews were reserved for last as required. None were discovered, so there was no settled
prior material to apply. There were no deviations from the supplied reading set, no network use,
no agent fan-out, no forbidden-source use, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so the verification harness was not invoked.

## 1. Findings

### candidate-001 — The exposed registry traversal types are promised but never specified

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:329-333`,
  `docs/phase4/v1/PHASE_4_DOC.md:490-500`, and
  `docs/phase4/v1/PHASE_4_DOC.md:880-884`
- **Claim:** Consumers cannot implement against the Phase 4 stage/pass registry without
  inventing its traversal and lookup API.
- **Evidence:** `CompiledProgramRegistry.stages()` returns `StageRegistry`, but the public-shape
  block never declares that type
  (`docs/phase4/v1/PHASE_4_DOC.md:329-333`). Section 5 also exposes `StageRegistry` and
  `PassDescriptor` to Phases 5, 7, and 8 and to G8 work
  (`docs/phase4/v1/PHASE_4_DOC.md:880-884`), while the detailed validation rules rely on a pass
  descriptor being contained by a schedule without defining the descriptor or the access path
  (`docs/phase4/v1/PHASE_4_DOC.md:490-500`). This cannot be supplied by downstream invention:
  the governing specification assigns the stage registry, sparse pass arrays, and per-pass data
  model to Phase 4 (`docs/design/v2.0-RC3/DESIGN.md:1485-1491`).
- **Required correction:** Declare immutable `StageRegistry` and `PassDescriptor` contracts.
  Specify deterministic schedule enumeration/lookup, sparse and named pass access, the exact
  relationship among `StageStep`, `PassPopulation`, `PassDescriptor`, and
  `PassResourceAccess`, absence/invalid-key behavior, and construction validation. Mirror the
  resulting guarantees exactly in §5.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

### candidate-002 — The barrier result and participant-isolation contracts have no data model

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:776-824`,
  `docs/phase4/v1/PHASE_4_DOC.md:885-887`, and
  `docs/phase4/v1/PHASE_4_DOC.md:960-968`
- **Claim:** Phases 6–8 cannot implement and consume the never-throwing barrier without inventing
  result and degradation semantics.
- **Evidence:** The public methods return `BarrierParticipantResult` and `BarrierResult`, but
  neither type is declared (`docs/phase4/v1/PHASE_4_DOC.md:776-785`). The target defines an
  ordered state transition and successful activation contract
  (`docs/phase4/v1/PHASE_4_DOC.md:794-824`) and separately requires isolated participant
  failures, recoverable override failures, fixed terminals, `ShadersOff`, and safe-state recovery
  (`docs/phase4/v1/PHASE_4_DOC.md:960-968`). Section 5 nevertheless exports only an unnamed
  “result contract” (`docs/phase4/v1/PHASE_4_DOC.md:885-887`). The Phase 4 document gate requires
  the barrier contract to be fully specified as an interface
  (`docs/design/v2.0-RC3/DESIGN.md:1559-1562`).
- **Required correction:** Define closed `BarrierParticipantResult` and `BarrierResult` variants.
  For every observable success, fixed/skip, isolated-participant degradation, resolution failure,
  unsafe-state failure, and shaders-off outcome, state whether processing continues, the state
  guaranteed on return, diagnostic disposition, and caller obligation. Keep Phase 6 responsible
  for producing these defined results, not defining them.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

### candidate-003 — Registry publication has an undefined input and ownership-transfer contract

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:336-348`,
  `docs/phase4/v1/PHASE_4_DOC.md:674-677`, and
  `docs/phase4/v1/PHASE_4_DOC.md:838-852`
- **Claim:** The reload owner cannot publish ready and shaders-off replacements without inventing
  the publication input, ownership, and barrier-release semantics.
- **Evidence:** `ProgramRegistryPublisher.publish` consumes `RegistryPublication`, but the target
  never declares that type (`docs/phase4/v1/PHASE_4_DOC.md:336-343`). Publication is explicitly
  the point at which an unpublished candidate transfers ownership
  (`docs/phase4/v1/PHASE_4_DOC.md:674-677`). The prose requires ready/off replacement, old-barrier
  release, atomic generation replacement, and old-registry close
  (`docs/phase4/v1/PHASE_4_DOC.md:838-852`), yet it does not connect any of those states to the
  missing input model. Phase 7/12 owns the policy choice after a failed reload, not Phase 4's
  publication mechanics.
- **Required correction:** Define `RegistryPublication` as a closed ready/off input (or reuse a
  suitably closed existing type). Specify registry and barrier ownership transfer, old-barrier
  release, old-registry closure, atomic replacement and generation behavior, return semantics,
  and invalid/failure behavior. Specify rejection or idempotency only if the corrected API permits
  it.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

### candidate-005 — The compiler returns an undefined failure type while §5 exposes another

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:324-327`,
  `docs/phase4/v1/PHASE_4_DOC.md:611`, and
  `docs/phase4/v1/PHASE_4_DOC.md:888-890`
- **Claim:** Compiler failure output is not a single implementable public contract.
- **Evidence:** `RegistryBuildResult.ShadersOff` carries an undeclared
  `RegistryBuildFailure` (`docs/phase4/v1/PHASE_4_DOC.md:324-327`). The target instead uses
  `ProgramBuildFailure` for an individual failed program
  (`docs/phase4/v1/PHASE_4_DOC.md:611`) and exposes that per-program failure to downstream
  consumers (`docs/phase4/v1/PHASE_4_DOC.md:888-890`). Section 4.12 describes the latter's
  per-program fields, but nowhere defines a registry-wide aggregate or a relationship between the
  two.
- **Required correction:** Define a closed, sanitized `RegistryBuildFailure` contract and its
  relationship to constituent `ProgramBuildFailure` values and final pack-wide disposition, then
  expose it in §5. Alternatively, consistently use `ProgramBuildFailure` if the shaders-off result
  truly carries only one such failure.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

### candidate-006 — Compute-slot model omits the authoritative gbuffers exclusion

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:490-500`,
  `docs/phase4/v1/PHASE_4_DOC.md:549-552`, and
  `docs/phase4/v1/PHASE_4_DOC.md:880-884`
- **Claim:** The reserved compute-slot model permits a state forbidden by the modern pack
  contract.
- **Evidence:** The authoritative contract attaches `.csh` companions to every program **except
  gbuffers** (`docs/research/v1/RESEARCH.md:357-361`). Phase 4 owns the dormant placeholder shape
  and says generically that a raster pass may own primary and `a`–`z` descriptors
  (`docs/phase4/v1/PHASE_4_DOC.md:549-552`). Its validation list limits slots to descriptors until
  G8/S2 but never rejects them on gbuffers (`docs/phase4/v1/PHASE_4_DOC.md:490-500`), and §5
  exports that over-broad shape to G8/S2 (`docs/phase4/v1/PHASE_4_DOC.md:880-884`). The governing
  spec places placeholder shape in Phase 4 and defers only wiring
  (`docs/design/v2.0-RC3/DESIGN.md:1485-1490`).
- **Required correction:** State the non-gbuffers eligibility rule in the conformance map,
  detailed model, validation, and §5 contract. Reject compute descriptors on every gbuffers pass
  and add a negative headless test. Leave source, work-group, resource, barrier, and dispatch
  semantics to G8/S2.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

The following examined areas produced no additional admitted finding:

- The mandatory thirteen sections are present and substantive. Both required stage
  configurations, the classic catalog/fallback mapping, fixed attribute locations, compile flow,
  barrier order, Pintonium backup-chain cross-check, generation invalidation, and absence of
  assigned open questions are covered.
- The consumed Phase 1 facade surface examined here matches its binding contract, including opaque
  handles, compile/link/validate results, recording/replay, state snapshot/restore, diagnostics,
  and `ShaderService.useFixedFunction()`.
- The G6 and modern schedules, sparse 0…99 families, whole-provider fallback state, fixed
  10/11/12 attribute locations, and equality-polled generation are substantively mapped.
- Candidate-004 is **dropped on re-derivation**. Phase 3 publishes `PackConfiguration` to Phases
  4–13 (`docs/phase3/v1/PHASE_3_DOC.md:900-903`), and its public record directly contains
  `Map<DimensionKey, DimensionConfiguration>` and `ResourceRequirements`
  (`docs/phase3/v1/PHASE_3_DOC.md:246-258`). Its detailed `ResourceRequirements` contract includes
  per-pass mipmaps and per-program routing, attributes, instance count, and legacy geometry, and
  explicitly assigns per-program execution data to Phase 4
  (`docs/phase3/v1/PHASE_3_DOC.md:781-793`). The compact §5 consumer cell for
  `DimensionConfiguration` does not withdraw types that are components of the
  `PackConfiguration` published to Phase 4, and the same binding section expressly says Phase 4
  selects the materialization root. No Phase 4 correction survives this candidate.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=5; notes=0
Interface changed: yes

All five admitted corrections affect the binding §5 surface or types directly consumed through
it. There is no structural miss requiring a rebuild, so `FAIL` is not warranted; literal `PASS`
is unavailable with five corrections.

This is the first review, so there is no prior trend to compare and no convergence claim. The next
required action is a scoped Phase 4 fix-up resolving candidates 001, 002, 003, 005, and 006,
appending this review's `## Resolutions`, and adding the required Phase 4 document addendum. Because
the corrections change the `cross-phase-interfaces` change-trigger region, a fresh verification
round is required before Phase 4 can close.

## Resolutions

### candidate-001 — resolved

Added immutable `StageRegistry` and `PassDescriptor` public shapes, deterministic schedule and
population traversal, named/indexed lookup, legal-absence behavior, construction rejection rules,
and the exact containment relationship among schedule steps, populations, descriptors, and
resource access. Mirrored those guarantees in §5.

### candidate-002 — resolved

Added closed participant continue/degraded results and closed barrier activated, fixed, skipped,
shaders-off, and failed-safe results. The corrected text states continuation, return-state,
diagnostic, isolation, and caller duties for every outcome; Phase 6 only produces these results.

### candidate-003 — resolved

Added closed ready/off publication inputs and accepted/rejected results. The contract now defines
accepted-only registry/barrier ownership transfer, validation and republish rejection, old-barrier
release, atomic generation replacement, old-registry closure, and candidate ownership on failure.

### candidate-005 — resolved

Added a closed sanitized `RegistryBuildFailure` aggregate with deterministic constituent
`ProgramBuildFailure` ordering and an explicit pack-wide shaders-off disposition, then exposed both
failure levels in §5.

### candidate-006 — resolved

Applied the authoritative non-gbuffers compute eligibility rule to the conformance map, descriptor
construction validation, detailed source-stem model, §5 contract, and a negative headless test.
Compute source, work-group, resource, barrier, and dispatch semantics remain deferred to G8/S2.

### Notes deferred

None. The adjudication admitted no notes.
