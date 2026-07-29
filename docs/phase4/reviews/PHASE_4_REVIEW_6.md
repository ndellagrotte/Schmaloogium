## 0. Method and reading order

This adjudication independently re-derived every supplied candidate against, in order:

1. the Phase 4 target;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding contracts in `docs/phase1/v14/PHASE_1_DOC.md:3944-4039` and
   `docs/phase3/v1/PHASE_3_DOC.md:890-1015`; and
5. the listed supporting evidence where it bore on a candidate.

Only after those interpretations were settled were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_5.md` read, in round order. Their resolved findings establish
the evolving registry, publication, and barrier surface, but do not settle the two admitted
bypasses below. There were no reading-set deviations, no network use, no agent fan-out, no
forbidden-source use, no candidates eliminated before adjudication, and no Gate drops. The
canonical engine had already dispatched this atomic adjudication role, so neither the verification
harness nor another Codex session was invoked.

## 1. Findings

### candidate-001 — Public barrier composition cannot authenticate Phase 6 participants

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:881-934` and
  `docs/phase4/v1/PHASE_4_DOC.md:1099-1102`
- **Claim:** The public factory cannot enforce its promise that a production barrier contains
  Phase 6's sampler, built-in, and custom participants.
- **Evidence:** `ProgramBindingParticipant` is a freely implementable public interface, and its
  public result algebra includes a constructible `Continue`
  (`docs/phase4/v1/PHASE_4_DOC.md:881-891`). `BarrierParticipants` then accepts three values of
  that same unrestricted type, and `ProgramStateBarrierFactory.create` accepts the record without
  any provenance, typed capability, or opaque production credential
  (`docs/phase4/v1/PHASE_4_DOC.md:910-924`). Consequently, a caller can submit three
  always-`Continue` implementations through the production `create` path. The package-private
  bootstrap capability and bootstrap marker described at
  `docs/phase4/v1/PHASE_4_DOC.md:927-934` distinguish only products created through that
  privileged bootstrap assembler; they cannot identify equivalent caller-authored no-op
  participants. Section 5 nevertheless binds consumers to the claim that public construction
  requires all three Phase 6 participants
  (`docs/phase4/v1/PHASE_4_DOC.md:1099-1102`), and the implementation plan requires that
  production-participant requirement to be proved
  (`docs/phase4/v1/PHASE_4_DOC.md:1495-1498`).
- **Required correction:** Replace the forgeable public composition with an opaque production
  participant bundle or another genuinely unforgeable credential supplied through the controlled
  Phase 6 composition path. The Phase 4 factory must be able to distinguish that production
  product from caller-authored participants without introducing an engine dependency on Phase 6
  implementation classes. Retain the separate package-private bootstrap capability and add a
  negative API/contract test proving synthesized no-op participants cannot produce a publishable
  production barrier. Update §5 accordingly.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

### candidate-002 — Production publication cannot authenticate a factory-created barrier

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:409-413`,
  `docs/phase4/v1/PHASE_4_DOC.md:915-934`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1031-1036`
- **Claim:** A consumer can bypass the factory entirely by placing an arbitrary implementation of
  the public barrier interface into a ready publication.
- **Evidence:** `RegistryPublication.Ready` publicly accepts a raw `ProgramStateBarrier`
  (`docs/phase4/v1/PHASE_4_DOC.md:409-413`). The factory also returns only that public interface
  in `BarrierConstructionResult.Ready`
  (`docs/phase4/v1/PHASE_4_DOC.md:915-924`). Although the prose says production publication
  accepts only a factory result containing all three Phase 6 positions, the specified publisher
  checks cover lifecycle state, prior publication, and negative rejection of a bootstrap marker
  (`docs/phase4/v1/PHASE_4_DOC.md:929-934`,
  `docs/phase4/v1/PHASE_4_DOC.md:1031-1036`). No positive factory-issued provenance, sealed
  implementation, inaccessible publication candidate, or credential lets the publisher reject an
  unrelated public `ProgramStateBarrier` implementation. This is independent of candidate-001:
  even a corrected participant-composition input would remain bypassable through the raw
  publication constructor.
- **Required correction:** Make ready production publication carry an unforgeable Phase-4-issued
  product or credential rather than an unauthenticated public barrier. Require positive factory
  provenance and complete production composition before old-barrier release, while preserving the
  existing bootstrap rejection and ownership/lifecycle rules. State the construction-to-
  publication path in §5 and add a negative test proving an arbitrary barrier implementation
  cannot enter production publication.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

The following examined areas produced no additional admitted finding:

- The handle-free descriptor exposes derivation metadata but no operational GL capability;
  compiled bindings and `ProgramHandle` remain private to Phase 4.
- Fallback resolution consistently inherits the effective provider's whole state. Marked bootstrap
  products are rejected before old-publication release. The stage/catalog, compile flow,
  generation, ownership, failure, dependency-interface, and conformance surfaces examined by the
  finders yielded no additional surviving candidate.
- Candidates 001 and 002 are not duplicates. Candidate 001 forges the factory's composition input;
  candidate 002 bypasses the factory through the raw publication input. Closing only either route
  leaves the other consumer-reachable.
- **candidate-003 is dropped on re-derivation.** The manifest actually defines
  `cross-phase-interfaces` with content anchors from the unique
  `## 5. Cross-phase interfaces` heading to the `## 6. Failure modes & degradation` heading
  (`verification/targets/phase-4.json:203-212`). Those anchors currently span all of §5
  (`docs/phase4/v1/PHASE_4_DOC.md:1091-1172`). The supplied resolved coordinates
  `876-955` are inconsistent with the on-disk content-anchored selector, but they do not establish
  the candidate's claimed manifest defect and do not justify a Phase 4 target correction.
- Prior round 5 confined the explicit bootstrap constructor and added negative bootstrap marking,
  but it did not establish positive provenance for caller-supplied participants or arbitrary
  barriers. No prior resolution therefore clears candidates 001 or 002.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are bounded corrections to the production barrier/publication contract, not
structural misses requiring a rebuild. Literal `PASS` is unavailable while two corrections remain.

The correction trend is 5, 3, 1, 3, 2, then 2. The count has plateaued and the surviving findings
remain in the repeatedly revised barrier/publication interface, so the loop has not demonstrated
convergence. No closure inference is warranted.

The next required action is a scoped Phase 4 fix-up resolving candidates 001 and 002, appending
this review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. Because both
corrections change the binding §5 `cross-phase-interfaces` region, a fresh verification round is
required before Phase 4 can close.

## Resolutions

### candidate-001 — resolved

Re-derived as a real provenance gap: null/position validation and the bootstrap marker could not
distinguish three caller-authored no-op implementations from the intended Phase 6 composition.
The target now replaces the publicly constructible participant record with opaque final
`ProductionBarrierParticipants`. Only a package-private Phase-4 production assembler, reached by
the controlled application composition root, mints it from the three Phase 6 participant
interfaces; its credential is checked by the factory, while Phase 4 does not name or depend on
Phase 6 implementation classes. The bootstrap assembler and capability remain separate. The
negative contract test now proves a synthesized no-op bundle cannot reach factory-ready state.

### candidate-002 — resolved

Re-derived independently: even authenticated factory input was insufficient while public
publication accepted a raw barrier interface. `BarrierConstructionResult.Ready` now carries an
opaque final `BarrierPublicationCandidate`, and `RegistryPublication.Ready` accepts that product
rather than `ProgramStateBarrier`. Before releasing the old barrier, the publisher positively
checks Phase-4 factory provenance, authenticated production composition, exact registry identity,
open/nonpublished state, and the separate bootstrap marker. An arbitrary barrier has no conversion
to the candidate type. Ownership prose, §5, implementation work, and a negative API-shape test
were updated accordingly.

### Notes deferred

None.
