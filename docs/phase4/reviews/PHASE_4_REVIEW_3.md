## 0. Method and reading order

This round independently re-derived the supplied candidate against, in order:

1. the whole target, `docs/phase4/v1/PHASE_4_DOC.md:1-1274`;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding contracts in `docs/phase1/v14/PHASE_1_DOC.md:3944-4039` and
   `docs/phase3/v1/PHASE_3_DOC.md:890-1015`; and
5. the supplied supporting evidence where relevant.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` and
`docs/phase4/reviews/PHASE_4_REVIEW_2.md` read, in round order. Their resolved findings establish
the corrected publication and barrier surface under review, but neither settles the active-barrier
access omission below. There were no reading-set deviations, no network use, no agent fan-out, no
forbidden-source use, and no Gate drops. The canonical engine had already dispatched this atomic
adjudication role, so the verification harness was not invoked.

## 1. Findings

### candidate-001 — Accepted publication exposes no safe route to invoke the active barrier

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:363-385`,
  `docs/phase4/v1/PHASE_4_DOC.md:942-954`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1010-1022`
- **Claim:** Phases 6–8 cannot implement the mandatory activation/release barrier against the
  published registry without retaining an ownership-ambiguous reference to a transferred
  candidate object.
- **Evidence:** `RegistryPublication.Ready` supplies both a registry and a
  `ProgramStateBarrier`, but accepted publication exposes only a `PublishedRegistry` containing
  generation and optional registry; neither `current()` nor another publisher operation exposes
  or delegates to the accepted barrier (`docs/phase4/v1/PHASE_4_DOC.md:363-385`). Acceptance
  explicitly transfers ownership of the ready registry and barrier to the publisher, while
  rejection or recovery leaves the candidate caller-owned
  (`docs/phase4/v1/PHASE_4_DOC.md:942-954`). Retaining the original barrier reference after
  acceptance is therefore not a specified consumer access model. Section 5 nevertheless requires
  Phases 6–8 to invoke `ProgramStateBarrier`, and expressly forbids Phase 6 from bypassing it
  (`docs/phase4/v1/PHASE_4_DOC.md:1010-1022`). The governing document gate requires the barrier
  contract to be fully specified as an interface
  (`docs/design/v2.0-RC3/DESIGN.md:1559-1563`).
- **Required correction:** Define one generation-safe access route in §2.2 and §5.1: expose a
  non-owning current-barrier view, add publisher-delegated activation/release operations, or
  provide a validated lease/reference. Specify replacement invalidation, render-thread
  restrictions, lack of consumer close authority, and the behavior when the current publication
  is shaders-off or `RecoveredOff`.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

The following examined areas produced no additional admitted finding:

- The governing Phase 4 scope, architecture requirements, document gate, and mandatory
  thirteen-section template remain satisfied apart from the admitted interface omission.
- Consumed Phase 1 facade, handle-lifetime, recording/replay, state, diagnostics, and
  fixed-function contracts match the selected binding region.
- Consumed Phase 3 configuration, materialization, macro, options, program-state,
  resource-requirement, fingerprint, and diagnostics contracts match the selected binding
  region. The legacy-geometry shortfall remains honestly requested rather than assumed.
- The stage schedule, catalog, fallback, compilation, geometry, state-bundle, participant order,
  publication outcomes, generation invalidation, diagnostics, attributes, and instance-count
  promises were checked without another surviving candidate.
- A target-wide search found no barrier accessor, publisher-side activation delegation,
  generation-checked lease, or explicit permission for consumers to retain a non-owning barrier
  reference after ownership transfer. Candidate-001 therefore was not cleared by equivalent
  coverage.
- The round-1 and round-2 resolved findings define publication input, ownership, release context,
  and unsafe-release recovery, but do not provide downstream access to an accepted active barrier.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted omission is a bounded consumer-facing contract correction rather than a structural
miss requiring a rebuild. Literal `PASS` is unavailable while the barrier remains unreachable
through the published interface.

Corrections decreased from five in round 1 and three in round 2 to one in round 3. That is
continued numerical convergence, but the surviving defect is another publication/barrier
interface omission, so the loop has not converged to closure. The next required action is a scoped
Phase 4 fix-up resolving candidate-001, appending this review's `## Resolutions`, and adding the
required Phase 4 document addendum. Because the correction changes the
`cross-phase-interfaces` change-trigger region, a fresh verification round is required before
Phase 4 can close.

## Resolutions

### candidate-001 — Resolved

Re-derived against the publication and barrier contracts, this finding is valid: acceptance
transfers the underlying barrier to the publisher, so downstream callers need a publisher-owned
route rather than an implied retained owner reference.

The target now makes `PublishedRegistry` one atomic generation/registry/barrier snapshot. A ready
snapshot exposes `PublishedProgramStateBarrier`, a non-owning view with activation and release
delegation but deliberately no close authority. Each render-thread-only call validates both the
view generation and identity against the current ready publication before delegating. Replacement
therefore invalidates an old view as `BarrierResult.StalePublication` without GL work; its caller
must not draw and must reacquire `current()`. Accepted shaders-off and `RecoveredOff` snapshots
expose neither registry nor barrier view. The same rules are repeated in the binding §5.1 row,
threading contract, and conformance cases.

This changes `cross-phase-interfaces`, so the declared fresh-verification trigger fires.

### Notes deferred

None.
