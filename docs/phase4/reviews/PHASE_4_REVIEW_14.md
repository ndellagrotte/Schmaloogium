## 0. Method and reading order

This adjudication independently re-derived the sole surviving candidate against, in order:

1. the whole Phase 4 target, with focused checks of the candidate-view declaration, ownership and
   publication state machine, binding §5 region, tests, and Phase 5/7 hand-offs;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4079-4181` and
   `docs/phase3/v1/PHASE_3_DOC.md:890-1015`; and
5. the listed supporting evidence where it bore on the candidate.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_13.md` read, in round order. Those reviews establish the
evolving registry, publication, relationship-map, and interface surface. Reviews 12 and 13
establish and integrate the candidate view, but neither defines the behavior of an already-retained
view after its stated availability boundary is crossed.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
and no Gate drops. The canonical engine had already dispatched this atomic adjudication role, so
neither the verification harness nor another Codex session was invoked. Candidate-002 was
eliminated before adjudication by the strict refuting disposition; it is not revived or counted.

## 1. Findings

### candidate-001 — Retained candidate-view behavior is undefined after its availability boundary

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:432-445`,
  `docs/phase4/v1/PHASE_4_DOC.md:1131-1152`,
  `docs/phase4/v1/PHASE_4_DOC.md:1207`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1544-1553`
- **Claim:** The exported pre-publication view has no deterministic semantics once its candidate
  is closed or ceases to be caller-owned, although consumers can retain the view across those
  transitions.
- **Evidence:** `CompiledRegistryCandidate.view()` returns the ordinary
  `ProgramRegistryView`, whose operations expose no validity result or lifetime token
  (`docs/phase4/v1/PHASE_4_DOC.md:432-445`). Binding §5 limits the view to the period while the
  candidate is “open and caller-owned” but does not state whether a retained view becomes invalid,
  fails closed, or remains a safe detached metadata projection after that condition becomes false
  (`docs/phase4/v1/PHASE_4_DOC.md:1207`). This omission is observable because publication defines
  distinct transitions: `Accepted` transfers candidate ownership, while pre-release rejection and
  `RecoveredOff` leave it caller-owned for later close
  (`docs/phase4/v1/PHASE_4_DOC.md:1131-1152`). The Phase 5/7 hand-offs authorize substantive
  pre-publication derivation through the view and prohibit particular operations, but do not
  prohibit retention or define post-transition method behavior
  (`docs/phase4/v1/PHASE_4_DOC.md:1544-1553`). Existing tests cover published-view safety and
  retained descriptors, not a retained candidate view crossing close or ownership transfer
  (`docs/phase4/v1/PHASE_4_DOC.md:1401-1403`).
- **Required correction:** Choose and specify one lifecycle model in §2 and binding §5. Either
  make `view()` an immutable detached metadata snapshot that remains safe after candidate
  close/transfer, or make every operation on the live view deterministically fail closed once the
  candidate is closed or no longer caller-owned. Align the Phase 5/7 hand-offs and add
  representative tests for retention across candidate close and successful publication, plus
  rejection/recovery transitions wherever the chosen model makes them behaviorally distinct.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

- The finder-reported new-surface checks otherwise hold: the relationship map consistently
  distinguishes non-owning Phase 5/7 inspection from the opaque candidate route through
  composition and publication.
- The candidate view exposes no publication generation, close operation, `ProgramHandle`,
  private-registry accessor, or compiler-origin credential. Obtaining it does not itself publish,
  authenticate, or transfer the candidate.
- Consumed Phase 1 and Phase 3 contracts remain aligned with their selected binding regions.
  Barrier composition, publication provenance, ownership transfer, recovery, generation,
  fixed-attribute, compiler, and dependency-gap hand-offs otherwise provide concrete outcomes.
- The governing Phase 4 conformance surface remains covered: both registry configurations, the
  classic program and fallback mappings, fixed attributes, compile/link lifecycle, failure
  handling, barrier obligations, per-program state, and generation invalidation are represented.
- Candidate-002 remains eliminated before adjudication. Its title overlaps the surviving
  retained-view issue, but it supplied no separately surviving candidate disposition and therefore
  cannot become an additional finding.
- Prior Reviews 12 and 13 do not clear candidate-001. They established the view's non-owning,
  pre-publication purpose and corrected its relationship-map branch; neither settled retained-view
  behavior after close or ownership transfer.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted lifecycle ambiguity is a bounded interface correction, not a structural miss
requiring a rebuild. Literal `PASS` is unavailable while one correction remains.

The prior correction trend is 5, 3, 1, 3, 2, 2, 2, 2, 1, 2, 0, 1, 1. This round remains at one
correction, so convergence and closure are not established despite the earlier Round 11 PASS.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. Because the
correction changes the binding §5 `cross-phase-interfaces` change-trigger region, a fresh
verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — resolved

Re-derived from the target's metadata-only `ProgramRegistryView` surface and immutable descriptor
contract, the selected lifecycle is an immutable detached metadata snapshot. Section 2 now states
that `CompiledRegistryCandidate.view()` copies metadata at the call and that a retained snapshot
remains safe and unchanged after candidate close, pre-release rejection, `RecoveredOff`, or
accepted ownership transfer. It retains neither the candidate/private registry nor a GL handle,
does not observe publication state, and leaves the opaque candidate as the sole authenticated
composition/publication product.

Binding §5 now publishes the same lifecycle to Phases 5 and 7. Their §11 hand-offs distinguish
retained metadata from live publication state, and §8 adds representative retention tests across
close, accepted publication, and rejection/recovery. The implementation checklist carries the
same obligations. This changes the declared `cross-phase-interfaces` region, so the manifest's
fresh-verification trigger fires.

### Notes deferred

None.
