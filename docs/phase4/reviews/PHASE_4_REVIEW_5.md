## 0. Method and reading order

This round independently re-derived both supplied candidates against, in order:

1. the whole target, `docs/phase4/v1/PHASE_4_DOC.md`;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding contracts in `docs/phase1/v14/PHASE_1_DOC.md:3944-4039` and
   `docs/phase3/v1/PHASE_3_DOC.md:890-1015`; and
5. the listed supporting evidence where it bore on the candidates.

Only after settling both interpretations were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_4.md` read, in round order. Their resolved findings establish
the publication, ownership, and barrier-construction surface under review, but neither active
candidate was previously settled or duplicated. There were no reading-set deviations, no network
use, no agent fan-out, no forbidden-source use, no candidates eliminated before adjudication, and
no Gate drops. The canonical engine had already dispatched this atomic adjudication role, so the
verification harness was not invoked.

## 1. Findings

### candidate-001 — Published non-owning registry resolution exposes operational program handles

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:321-341`,
  `docs/phase4/v1/PHASE_4_DOC.md:368-371`,
  `docs/phase4/v1/PHASE_4_DOC.md:877-880`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1080`
- **Claim:** The published registry inspection and activation-result surfaces contradict their
  handle-free contract by returning a value containing an operational `ProgramHandle`.
- **Evidence:** `CompiledProgramBinding.ShaderProgram` has a public `ProgramHandle handle`
  component (`docs/phase4/v1/PHASE_4_DOC.md:321-330`), and
  `ResolvedProgramBinding.effective` embeds that binding
  (`docs/phase4/v1/PHASE_4_DOC.md:338-341`). `ProgramRegistryView.resolve` returns the resolved
  value directly (`docs/phase4/v1/PHASE_4_DOC.md:368-371`), while
  `BarrierResult.Activated` returns the same handle-bearing value after activation
  (`docs/phase4/v1/PHASE_4_DOC.md:877-880`). The binding table nevertheless promises “no
  registry `close` or mutable handle exposure”
  (`docs/phase4/v1/PHASE_4_DOC.md:1080`). Opacity does not make the handle observational:
  `ShaderService.use(ProgramHandle)` and lifecycle operations consume it
  (`docs/phase1/v14/PHASE_1_DOC.md:2752-2761`). Generation/identity checks guard barrier-view
  calls, not later use of a retained handle. The off-thread prohibition at
  `docs/phase4/v1/PHASE_4_DOC.md:1184-1186` is therefore not an API-level enforcement of the
  promised handle-free published surface.
- **Required correction:** Separate the public resolved descriptor from the internal compiled
  binding. Return only requested/effective identities, provider state, sources as appropriate,
  and fallback path through published inspection and externally visible activation results; keep
  `ProgramHandle` confined to the private registry/barrier implementation or another narrowly
  scoped owner-only type. Add API-shape and stale-publication tests proving neither published
  resolution nor an activation result yields an operational handle.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

### candidate-002 — The public all-no-op barrier factory has no enforceable bootstrap boundary

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:397-400`,
  `docs/phase4/v1/PHASE_4_DOC.md:899-917`,
  `docs/phase4/v1/PHASE_4_DOC.md:1012-1016`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1081-1083`
- **Claim:** Phase 7 can construct and publish an all-no-op barrier after Phase 6 integration
  without violating any machine-visible Phase 4 contract.
- **Evidence:** `ProgramStateBarrierFactory` publicly exposes unrestricted
  `createNoOp(ProgramRegistryView)` beside the participant-bearing factory operation
  (`docs/phase4/v1/PHASE_4_DOC.md:899-904`). Prose says this is the sole default only “Until
  Phase 6 integration” (`docs/phase4/v1/PHASE_4_DOC.md:912-917`), but both routes produce the
  same unqualified barrier type. `RegistryPublication.Ready` likewise accepts an unqualified
  `ProgramStateBarrier` (`docs/phase4/v1/PHASE_4_DOC.md:397-400`), and its exhaustive pre-release
  validation checks lifecycle and ownership but not participant completeness
  (`docs/phase4/v1/PHASE_4_DOC.md:1012-1016`). This permits silent omission of sampler, built-in,
  and custom refresh despite the governing barrier contract assigning Phase 6 as their supplier
  and Phases 7/8 as consumers
  (`docs/design/v2.0-RC3/DESIGN.md:1527-1532`). Rejecting partial compositions does not cure the
  indistinguishable complete all-no-op composition.
- **Required correction:** Make bootstrap status enforceable in the §5 API. Prefer confining
  all-no-op construction to a Phase-4-owned bootstrap capability unavailable to Phase 7
  production wiring; alternatively encode participant completeness and require production-ready
  publication to reject an all-no-op barrier. Add a contract test proving an all-no-op
  composition cannot be accepted once Phase 6 integration is required.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

The following examined areas produced no additional admitted finding:

- The mandatory thirteen sections, governing Phase 4 scope, document gate, conformance map, stage
  and pass models, compilation flow, fallback catalog, geometry disposition, generation
  invalidation, publication recovery, and dependency consumption were checked.
- Apart from candidate-001, the prior correction separating the closable owning registry from its
  non-owning published view correctly removes consumer teardown authority and preserves atomic
  generation-coherent snapshots.
- Apart from candidate-002, barrier participant positions, sampler/built-in/custom order, rejection
  of null or partial composition, ownership transfer, Phase 11 handoff, and result taxonomy are
  specified consistently.
- The finder-reported conformance and dependency-interface areas yielded no additional candidate.
  No candidate was refuted or cleared on re-derivation.
- Prior rounds established public traversal/result/publication shapes, release-aware recovery,
  generation-checked barrier access, non-owning registry inspection, and a participant factory.
  They did not settle the operational handle leaked through that inspection value or enforce the
  temporal restriction on the newly added all-no-op factory.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are bounded cross-phase API corrections rather than structural misses
requiring a rebuild. Literal `PASS` is unavailable while two corrections remain.

The correction trend is 5, 3, 1, 3, then 2. This round decreases from round 4, but the sequence is
not strictly convergent and both surviving findings concern the recently corrected publication
and barrier interface. No closure inference is warranted.

The next required action is a scoped Phase 4 fix-up resolving candidates 001 and 002, appending
this review's `## Resolutions`, and adding the required Phase 4 document addendum. Because both
corrections change the `cross-phase-interfaces` change-trigger region, a fresh verification round
is required before Phase 4 can close.

## Resolutions

### candidate-001 — Applied

Re-derivation confirmed that opacity did not prevent the published `ResolvedProgramBinding` from
carrying a `ProgramHandle` accepted by Phase 1's shader service. The target now keeps compiled and
resolved compiled bindings Phase-4-private and exposes `ResolvedProgramDescriptor`, containing
only requested/effective identities, provider state, source fingerprints, and fallback path.
`ProgramRegistryView.resolve`, participant input, and `BarrierResult.Activated` use that handle-free
descriptor. The private registry/barrier implementation alone obtains the operational handle.
API-shape and stale-retention tests now prove that published resolution and activation results
cannot drive `ShaderService.use`.

### candidate-002 — Applied

Re-derivation confirmed that prose timing could not constrain the public `createNoOp` operation.
That operation is removed from the public factory. Bootstrap all-no-op assembly now requires a
package-private capability minted by the Phase-4 composition root, produces a bootstrap-marked
barrier, and is limited to bootstrap tests/bring-up; Phase 7 production wiring cannot name or
obtain it. Public construction requires all three Phase-6 participant positions, and production
publication rejects a bootstrap-marked barrier during pre-release validation. Tests cover both
capability visibility and rejection before old-barrier release.

### Notes deferred

None.
