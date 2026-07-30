## 0. Method and reading order

This adjudication independently re-derived the sole supplied candidate against, in order:

1. the whole Phase 4 target;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4079-4181` and
   `docs/phase3/v1/PHASE_3_DOC.md:890-1015`; and
5. the listed supporting evidence where it bore on the candidate.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_12.md` read, in round order. Those reviews establish the
evolving registry, publication, relationship-map, and interface surface. Review 9 settled the
opaque candidate's ownership/publication route and downstream published-view routes, but it
predates §0.15's additional non-owning pre-publication candidate-view relationship and therefore
does not clear the present omission.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — Relationship map still routes Phase 5 only through the published registry

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:501-522`
- **Claim:** The architecture relationship map does not integrate §0.15's pre-publication
  `CompiledRegistryCandidate.view()` relationship for Phases 5 and 7.
- **Evidence:** Section 0.15 states that `CompiledRegistryCandidate.view()` is the non-owning,
  pre-publication `ProgramRegistryView` consumed by Phases 5 and 7
  (`docs/phase4/v1/PHASE_4_DOC.md:174-180`). Binding §5.1 exposes that exact route separately from
  the post-publication `PublishedRegistry.registry` route
  (`docs/phase4/v1/PHASE_4_DOC.md:1200-1201`), and the Phase 5 hand-off requires the candidate view
  for pre-publication derivation and validation
  (`docs/phase4/v1/PHASE_4_DOC.md:1537-1540`). The §2.3 map instead shows the candidate proceeding
  only through “Phase 7 production composition + publication” and places Phase 5 solely below
  `PublishedRegistry` (`docs/phase4/v1/PHASE_4_DOC.md:512-522`). Because the mandatory
  architecture overview must show key types and their relationships
  (`docs/design/v2.0-RC3/DESIGN.md:802-803`), this omission leaves the overview inconsistent with
  the corrected contract.
- **Required correction:** Add a clearly non-owning `CompiledRegistryCandidate.view()` branch
  from the candidate to Phases 5 and 7 for pre-publication derivation/validation. Preserve the
  opaque candidate ownership path through Phase 7 production composition/publication and the
  existing post-publication `PublishedRegistry` consumer branches. Do not change §5.1.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

## 2. Checked and clean

- The §0.16 correction accurately aligns §5.3 with Phase 3's source/materialization, geometry,
  fingerprint, and `ResourceRequirements` contracts.
- The candidate-view contract is otherwise consistent across §0.15, its public signature,
  ownership rules, binding §5.1, the Phase 5 and Phase 7 hand-offs, tests, requested-change
  disposition, and implementation checklist.
- The candidate view remains non-owning and pre-publication-only. It exposes no handle,
  publication generation, private-registry accessor, close operation, or compiler-origin
  credential, and it does not transfer candidate ownership.
- The manifest-declared §5 interface region adequately states the candidate-view and published-view
  contracts. The admitted defect is confined to §2.3's explanatory relationship map.
- Finder-reported interface and conformance areas remain clean: dependency consumption, stage and
  program coverage, fallback behavior, compile/link flow, barrier duties, per-program state, and
  generation invalidation produced no additional surviving candidate.
- No candidate was refuted or cleared on re-derivation. Review 9's earlier map correction does not
  settle a relationship added later by §0.15.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted defect is a bounded architecture-map correction, not a structural miss requiring a
rebuild. Literal `PASS` is unavailable while one correction remains.

The prior correction trend was 5, 3, 1, 3, 2, 2, 2, 2, 1, 2, 0, 1. This round remains at one
correction after Round 12, so the present evidence does not establish convergence or permit
closure.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. The correction does
not alter the binding §5 `cross-phase-interfaces` change-trigger region. A subsequent fresh review
is nevertheless required to establish the literal zero-correction `PASS` needed to close the
verification loop.

## Resolutions

### candidate-001 — resolved

Re-derived §0.15, §5.1, and the Phase 5/7 hand-offs against the governing architecture-overview
requirement. Section 2.3 now branches `CompiledRegistryCandidate` into a non-owning `view()` route
to Phases 5 and 7 for pre-publication derivation/validation and a separate opaque-candidate route
through Phase 7 production composition/publication. The existing post-publication
`PublishedRegistry` consumer branches remain intact. Binding §5.1 is unchanged.

### Notes deferred

None.
