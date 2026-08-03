## 0. Method and reading order

This adjudication independently re-derived the sole surviving candidate against, in order:

1. the whole Phase 4 target, with focused checks of the registry state model, compile/link flow,
   fixed attribute binding, and the manifest-declared §5 interface region;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4160-4265` and
   `docs/phase3/v1/PHASE_3_DOC.md:1300-1478`; and
5. the listed supporting evidence where it bore on the candidate.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_24.md` consulted as settled prior material. Review 1 dropped a
superficially similar candidate against an earlier Phase 3 contract that assigned the complete
per-program execution data to Phase 4. That disposition does not clear this candidate: the current
manifest-selected Phase 3 §5 is expressly the sole binding consumer contract and now partitions
pass mipmaps to Phase 5 and vertex attributes to Phase 10. Review 12 likewise establishes that the
current binding dependency bytes control a newly exposed consumed-contract mismatch.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — Phase 4 consumes Phase 3 projections not granted to it

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:1109-1117` and
  `docs/phase4/v1/PHASE_4_DOC.md:1546-1548`
- **Claim:** Phase 4's binding consumed-contract inventory and compiler design require direct use
  of per-program mipmap and vertex-attribute projections that Phase 3's binding consumer
  allocation assigns to other phases.
- **Evidence:** Phase 4 says it consumes dimension/program-keyed “routing, mipmaps, attributes,
  instance count, and optional legacy geometry” from the closed `ResourceRequirements` algebra
  (`docs/phase4/v1/PHASE_4_DOC.md:1546-1548`). The dependency's sole binding consumer contract
  instead assigns Phase 4 only routing, geometry, and instances, while assigning pass mipmaps to
  Phase 5 and vertex attributes to Phase 10
  (`docs/phase3/v1/PHASE_3_DOC.md:1319-1320`). This is operational rather than merely tabular:
  Phase 4 conditionally performs fixed pre-link binding for attributes that Phase 3 reports as
  declared (`docs/phase4/v1/PHASE_4_DOC.md:1109-1117`). The governing Phase 4 specification does
  require the registry's composite-mipmap bitmask to be populated from Phase 3 configuration and
  requires the fixed pre-link bindings (`docs/design/v2.0-RC3/DESIGN.md:1495-1498` and
  `docs/design/v2.0-RC3/DESIGN.md:1511-1513`), so deleting those Phase 4 behaviors would violate
  authority rather than reconcile the dependency. Phase 3 also states that its §5 is the sole
  binding consumer contract and that a changed consumer interpretation must update that contract
  (`docs/phase3/v1/PHASE_3_DOC.md:1366-1369`).
- **Required correction:** Record an explicit requested Phase 3 §5 change granting Phase 4 the
  per-program mipmap and declared-vertex-attribute projections required for registry compilation,
  and make Phase 4's consumed-contract wording state that the grant is pending rather than already
  available. Keep the governing registry and compile/link behavior; do not route the data backward
  through downstream Phases 5 or 10. Phase 3 must then be amended and re-verified before Phase 4
  can claim a contract-faithful implementation. Keep the detailed design and §5 inventory aligned.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

- The finder-reported new-surface checks remain clean: the §0.28 addendum, Pintonium disposition
  table, applicable decisions, and closing status agree and correctly retain the fresh-review
  obligation caused by the earlier binding §5 change.
- Apart from this projection-allocation mismatch, Phase 4's consumed Phase 1 and Phase 3 contracts
  are represented in the manifest-selected dependency regions, and its exposed §5 contracts state
  lifecycle, ownership, generation, failure, and handle boundaries for their consumers.
- The governing Phase 4 requirements remain mapped: both stage configurations, the complete
  catalog and fallback model, per-slot state, compile/link sequence, fixed attributes, legacy
  geometry limitation, failure behavior, barrier, and reload invalidation were checked without an
  additional surviving candidate.
- The acknowledged legacy-geometry dependency gap is separately bounded and routed through §5.4;
  it does not cure or duplicate the missing mipmap/attribute grant.
- No candidate was refuted, cleared, or dropped on re-derivation. Prior Review 1's earlier
  disposition depended on superseded Phase 3 consumer-allocation text; settled prior review
  material does not override the current binding contract.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted contract mismatch is a bounded cross-phase correction, not a structural miss that
requires rebuilding Phase 4. Literal `PASS` is unavailable while the correction remains.

The recent correction trend is 1, 1, 2, then 1 for rounds 22–25. Although the count falls from
round 24, it is not strictly decreasing across the window and the artifact has not produced the
zero-correction round required for convergence and closure.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the required compact Phase 4 correction addendum. That fix-up
must expose the pending Phase 3 contract change honestly rather than assuming the projection grant;
the dependency amendment and re-verification are then required before contract-faithful
implementation. Because the correction changes the binding §5 `cross-phase-interfaces` region, a
fresh whole-document verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — corrected

Re-derived against the governing Phase 4 requirements and the manifest-selected Phase 3 §5, the
mismatch is real: Phase 4 must populate composite-mipmap state and conditionally perform fixed
pre-link attribute binding, while Phase 3 currently grants its pass-mipmap projection to Phase 5
and its vertex-attribute projection to Phase 10. The correction therefore preserves both assigned
Phase 4 behaviors but no longer claims that their Phase 3 inputs are already available.

`PHASE_4_DOC.md` §5.3 now limits the presently consumed `ResourceRequirements` projection to
routing, instance count, and optional legacy geometry, and explicitly marks per-program mipmaps
and declared vertex attributes pending. Section 5.4 requests that Phase 3 grant Phase 4 the same
immutable owner-defined `mipmappedAfterPass` and `VertexRequirements` values directly, without
routing data backward through Phases 5 or 10. Sections 4.4, 4.7, 4.9, 11.3, and 12 were aligned so
the detailed design and implementation plan cannot be read as assuming the unverified grant.

This intentionally changes the manifest-declared §5 interface region. Phase 3 must amend and
re-verify its binding §5 before Phase 4 can claim a contract-faithful implementation, and the
changed Phase 4 interface requires a fresh whole-document verification round before closure.

### Notes deferred

None.
