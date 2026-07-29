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
`docs/phase4/reviews/PHASE_4_REVIEW_8.md` read, in round order. Their resolutions establish the
evolving registry, barrier, and publication surface. Review 1 also contains settled adjudication
of the dependency-publication theory raised again by candidate-002, as recorded in §2 below.
There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — Relationship map exposes the private compilation candidate directly to downstream phases

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:480-487`
- **Claim:** The architecture overview contradicts the corrected ownership model by depicting
  Phases 5, 6, 7, 8, and 10 between the private `CompiledRegistryCandidate` and publication.
- **Evidence:** The relationship map draws the private candidate directly above “Phase 5,”
  “Phase 6,” and “Phase 7/8/10,” with those branches joining at `PublishedRegistry`
  (`docs/phase4/v1/PHASE_4_DOC.md:480-487`). The adjacent lifecycle instead confines the private
  registry to Phase 4 and, after acceptance, the publisher, and gives snapshot consumers only
  non-owning views (`docs/phase4/v1/PHASE_4_DOC.md:452-460`). The detailed production route assigns
  Phase 7's composition root the compiler-issued candidate
  (`docs/phase4/v1/PHASE_4_DOC.md:965-970`), while §5 gives Phases 5, 6, 7, and 8 non-owning
  published views and assigns candidate composition/publication to Phase 7
  (`docs/phase4/v1/PHASE_4_DOC.md:1163-1166`). Because the mandatory architecture overview must
  communicate key-type relationships (`docs/design/v2.0-RC3/DESIGN.md:802-803`), the stale map is
  materially misleading rather than decorative shorthand.
- **Required correction:** Redraw the relationship map so Phase 7 alone carries the opaque
  `CompiledRegistryCandidate` through production composition and publication. Show downstream
  phases consuming `PublishedRegistry` views or their separately exposed descriptor/state
  contracts, including Phase 10's separate fixed-attribute/state inputs. Leave §5 unchanged.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

## 2. Checked and clean

The following examined areas produced no additional admitted finding:

- Review 8's corrected `BarrierPublicationCandidate` close lifecycle, compiler and publisher entry
  points, request/context fields, ownership rules, and §5 inventory remain consistent.
- Compiler-candidate provenance, exact registry/barrier pairing, production composition,
  non-owning published views, closed barrier outcomes, generation behavior, registry-build
  request fields, and caller duties agree across the detailed API, lifecycle prose, §5, tests,
  and implementation plan apart from the stale relationship map admitted above.
- The governing Phase 4 scope and document gate remain mapped: both stage configurations, the
  classic catalog and fallback rules, compile/link flow, dual-form geometry disposition, barrier
  duties, per-program state, and generation invalidation are substantively covered.
- The consumed Phase 1 contracts and the remainder of the Phase 3 configuration/materialization
  surface examined here are represented without another surviving mismatch. The already disclosed
  legacy-geometry dependency limitation and Phase 12 graph discrepancy remain requests rather
  than silently assumed contracts.
- **candidate-002 is dropped against settled prior material.** Review 1 already independently
  adjudicated the same theory and held that Phase 3's publication of `PackConfiguration` to
  Phases 4–13 carries the public record's component types, including
  `Map<DimensionKey, DimensionConfiguration>`, and that the compact §5 consumer cell for
  `DimensionConfiguration` does not withdraw those components
  (`docs/phase4/reviews/PHASE_4_REVIEW_1.md`). Phase 3 still publishes `PackConfiguration` to
  Phase 4 (`docs/phase3/v1/PHASE_3_DOC.md:899-903`), and no later settled correction reverses that
  disposition. Recasting the same nominal-component argument as a missing `DimensionKey` row does
  not establish a new Phase 4 defect.
- Finder-reported clean areas were retained: the Review 8 lifecycle fixes are otherwise coherent;
  Phase 1 dependencies used by Phase 4 are present; and no additional unmapped in-scope
  conformance requirement survived re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The surviving defect is a bounded architecture-diagram correction, not a structural miss
requiring a rebuild. Literal `PASS` is unavailable while that correction remains.

The prior correction trend was 5, 3, 1, 3, 2, 2, 2, 2; this round returns to 1 after the
four-round plateau. That is improvement, but one correction remains and therefore does not
establish convergence or closure.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. The admitted
correction does not alter the binding §5 `cross-phase-interfaces` change-trigger region. A
subsequent review is still required to establish the literal zero-correction `PASS` needed to
close the verification loop.

## Resolutions

### candidate-001 — resolved

Re-derived against the ownership lifecycle in §2.2, the production composition route in §4.10,
the exposed contracts in §5.1, and the mandatory architecture-overview requirement. The §2.3
relationship map now gives only Phase 7 the opaque `CompiledRegistryCandidate`, carrying it
through production composition and publication. Phases 5, 6, 7, and 8 appear downstream of
`PublishedRegistry`'s non-owning registry/barrier views, while Phase 10 receives the separately
exposed `ProgramSlotDescriptor` and `ProgramStateBundle` fixed-attribute/state inputs. No §5
contract changed.

The compact §0.13 addendum records the correction without adding a new design decision.

### Notes deferred

None.
