## 0. Method and reading order

This round independently re-derived every supplied candidate against, in order:

1. the whole target, `docs/phase4/v1/PHASE_4_DOC.md:1-1274`;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding contracts in `docs/phase1/v14/PHASE_1_DOC.md:3944-4039` and
   `docs/phase3/v1/PHASE_3_DOC.md:890-1015`; and
5. the listed supporting evidence where it bore on a candidate.

Only after settling all four dispositions were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md`,
`docs/phase4/reviews/PHASE_4_REVIEW_2.md`, and
`docs/phase4/reviews/PHASE_4_REVIEW_3.md` read, in round order. Their resolved findings establish
the publication and barrier surface under review, but do not settle or duplicate this round's
candidates. There were no reading-set deviations, no network use, no agent fan-out, no
forbidden-source use, no candidates eliminated before adjudication, and no Gate drops. The
canonical engine had already dispatched this atomic adjudication role, so the verification
harness was not invoked.

## 1. Findings

### candidate-001 — Equal-generation publication replacement lacks an explicit test

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:403-415` and
  `docs/phase4/v1/PHASE_4_DOC.md:1245-1250`
- **Claim:** The observable stale-view contract is specified, but its identity-based protection
  against equal generation values is not explicitly exercised.
- **Evidence:** Every view method must verify both generation and identity against the current
  ready publication, and a replaced view must return `StalePublication` without GL work
  (`docs/phase4/v1/PHASE_4_DOC.md:409-415`). This independently compared current-publication
  identity is sufficient for the architecture; its private representation need not be exposed.
  The test inventory covers ordinary replacement and generation inequality but does not force
  distinct ready publications to carry the same generation
  (`docs/phase4/v1/PHASE_4_DOC.md:1245-1250`).
- **Recommended hardening:** Add an equal-generation, distinct-publication test proving that the
  superseded view returns `StalePublication`, performs no GL work, and requires snapshot
  reacquisition. Optionally name the publisher-private reference/token identity more explicitly.
- **Severity:** note
- **Touches interface/change-trigger region:** no

### candidate-002 — Published snapshots expose publisher-owned registry teardown authority

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:362-379`,
  `docs/phase4/v1/PHASE_4_DOC.md:969-981`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1037-1041`
- **Claim:** A snapshot consumer can prematurely close the current registry even though accepted
  publication transfers its ownership and teardown duty to the publisher.
- **Evidence:** `PublishedRegistry.registry` exposes `CompiledProgramRegistry`, the same public
  interface whose `close()` deletes Phase-4-owned handles
  (`docs/phase4/v1/PHASE_4_DOC.md:362-379`). On acceptance the publisher owns the registry and
  closes the old registry during replacement or recovery
  (`docs/phase4/v1/PHASE_4_DOC.md:969-981`). Section 5 nevertheless gives multiple consumer phases
  access through `CompiledProgramRegistry.resolve`
  (`docs/phase4/v1/PHASE_4_DOC.md:1037-1041`). Idempotence prevents duplicate deletion, not
  premature deletion while the publication remains current.
- **Required correction:** Expose published registry inspection/resolution through a non-owning
  interface without `close()`, retaining a closable owning type for compilation candidates and
  publisher transfer. State in §5 that consumers receive no registry teardown capability and
  that close authority remains publisher-owned.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

### candidate-003 — Ordered Phase 6 participants have no barrier composition boundary

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:388-390`,
  `docs/phase4/v1/PHASE_4_DOC.md:854-888`,
  `docs/phase4/v1/PHASE_4_DOC.md:1038-1039`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1422-1425`
- **Claim:** Phase 6 cannot contractually supply its three participants to the Phase-4 barrier,
  and the publication owner cannot construct that barrier from the exposed surface.
- **Evidence:** Ready publication requires an already-constructed `ProgramStateBarrier`
  (`docs/phase4/v1/PHASE_4_DOC.md:388-390`). The target declares participant callbacks, barrier
  operations, and a mandatory sampler/built-in/custom order, but no factory, immutable composition
  input, or installation operation connects them
  (`docs/phase4/v1/PHASE_4_DOC.md:854-888`). Section 5 exports the callbacks and order without that
  delivery route (`docs/phase4/v1/PHASE_4_DOC.md:1038-1039`), while the implementation checklist
  itself says Phase 6 participant installation remains to be exposed
  (`docs/phase4/v1/PHASE_4_DOC.md:1422-1425`). The governing gate requires the barrier contract to
  be fully specified as an interface (`docs/design/v2.0-RC3/DESIGN.md:1559-1562`).
- **Required correction:** Add to §5 a narrow Phase-4-owned construction or installation contract
  through which Phase 6 supplies the three participants in fixed order. Specify construction
  ownership, lifecycle, defaults, invalid/null input behavior, failure behavior, and the Phase 11
  custom-uniform handoff; the concrete barrier implementation may remain private.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

### candidate-004 — Geometry conformance row falsely claims legacy ARB acceptance

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:455` and
  `docs/phase4/v1/PHASE_4_DOC.md:782-800`
- **Claim:** The conformance map reports both geometry forms as accepted although the detailed
  design marks the legacy ARB path unavailable.
- **Evidence:** The map unqualifiedly states that legacy and core forms are accepted
  (`docs/phase4/v1/PHASE_4_DOC.md:455`). The detailed strategy says complete legacy semantic
  support cannot honestly be claimed, classifies `LegacyArbRequired` as `Unavailable`, and routes
  it through rung 3 and the backup chain pending a dependency change
  (`docs/phase4/v1/PHASE_4_DOC.md:782-800`). The authoritative contract requires GL 3.2 layout
  qualifiers **or** `GL_ARB_geometry_shader4` plus `maxVerticesOut`
  (`docs/research/v1/RESEARCH.md:212-215`). Recognition plus fallback is not acceptance.
- **Required correction:** Revise the conformance row to state that core-layout geometry is
  accepted while legacy ARB geometry is recognized but currently unavailable and follows
  fallback. Identify the Phase 1/3 dependency needed for full acceptance, and do not imply that
  the requirement is satisfied until a dependency route is verified.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

## 2. Checked and clean

The following examined areas produced no additional admitted finding:

- The governing Phase 4 scope, mandatory template, and document gate were checked, with the
  participant-composition and geometry-accounting exceptions admitted above.
- The consumed Phase 1 and Phase 3 binding contracts remain named and used consistently; the
  detailed geometry section correctly reports rather than silently invents their missing
  capability.
- The modern/G6 stage configurations, sparse 0–99 families, compute placeholders, complete
  Appendix A.1 catalog, fallback semantics, fixed attribute locations, per-program state fields,
  reload generation, publication outcomes, and barrier activation/release result taxonomy remain
  substantively covered.
- Candidate-001 was narrowed on re-derivation. The target already specifies the complete observable
  superseded-view behavior and an identity comparison independent of generation
  (`docs/phase4/v1/PHASE_4_DOC.md:409-415`,
  `docs/phase4/v1/PHASE_4_DOC.md:921-922`); only explicit equal-generation collision testing
  survives as a note.
- No candidate was dropped. The three corrections are distinct: registry teardown capability,
  participant composition, and false geometry conformance accounting.
- The three prior reviews' resolved findings do not clear these candidates. They respectively
  established the base interface/result/ownership shapes, release-aware publication state
  machine, and non-owning barrier view; none supplied a non-owning registry view, participant
  composition route, accurate legacy-geometry conformance row, or equal-generation collision
  test.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=1
Interface changed: yes

The three admitted defects are bounded corrections rather than structural misses requiring a
rebuild. The note is optional assurance hardening and is not ordered for fix-up. Literal `PASS`
is unavailable while three corrections remain.

The prior correction trend was five, three, then one; this round rises to three, including two
newly exposed publication/barrier interface defects. The loop therefore has not converged, and
the numerical regression warrants another adversarial round rather than a closure inference.

The next required action is a scoped Phase 4 fix-up resolving candidates 002, 003, and 004,
appending this review's `## Resolutions`, and adding the required Phase 4 document addendum.
Candidate-001 may be deferred as a note. Because candidates 002 and 003 change the
`cross-phase-interfaces` change-trigger region, a fresh verification round is required before
Phase 4 can close.

## Resolutions

### candidate-002 — resolved

Re-derived the ownership conflict from the target's candidate-transfer and replacement rules.
`ProgramRegistryView` now carries non-owning traversal/resolution, while
`CompiledProgramRegistry` is its closable owning subtype. `PublishedRegistry` exposes only the
view, and §5 states that snapshot consumers receive no teardown authority; candidate ownership
transfers exclusively to the publisher on acceptance. A headless API/handle-lifetime test was
added.

### candidate-003 — resolved

Added a Phase-4-owned `ProgramStateBarrierFactory` taking one immutable
`BarrierParticipants(sampler, builtIns, customs)` composition. The contract fixes invocation
order, provides only an all-no-op default, rejects null/partial input without GL work or retained
references, converts construction failures to a closed result, and specifies caller-to-publisher
ownership transfer. Phase 11 feeds the Phase-6-owned custom participant rather than installing a
fourth participant. §5 now exports the construction route and lifecycle, and §8/§10 cover its
implementation tests.

### candidate-004 — resolved

Corrected the conformance map: only verified core-layout geometry is accepted. Legacy ARB form is
recognized but currently unavailable and follows rung-3 fallback until either Phase 3 publishes
a verified complete-source core-compatibility result or Phase 1 publishes a legacy pre-link
configuration route. This matches the already-detailed §4.8 dependency request and makes no claim
that recognition satisfies the pack contract.

### Notes deferred

- candidate-001 is deferred because the identity-and-generation stale-view behavior is already
  normative and the adjudicator classified only an equal-generation collision test as optional
  assurance hardening. It is not needed to correct an architectural interface or behavior.

### Fix-up status

All three admitted corrections are applied. The `cross-phase-interfaces` region changed, so the
manifest's change trigger requires a fresh verification round before Phase 4 can close.
