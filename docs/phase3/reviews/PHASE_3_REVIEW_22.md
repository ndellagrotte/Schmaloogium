# Phase 3 Adversarial Review — Round 22

## 0. Method and reading order

I independently re-derived the sole candidate from the complete target, the manifest-selected
governing-design regions, RESEARCH.md, and the Phase 1 binding contract. The supplied Pintonium and
Oculus reports were permitted supporting evidence but were not needed to decide the candidate.
Only after settling the candidate disposition did I read prior reviews 1–21, in round order and
including their resolutions.

There were no deviations from the required reading order, no network use, no agent fan-out, and no
use of forbidden sources. Under the dispatched-role rule in the supplied `verify-loop` skill, I did
not invoke the verification harness or start another session. The Gate reported no drops, and no
candidate was eliminated before adjudication.

## 1. Findings

No findings were admitted. `candidate-001` is dropped on independent re-derivation.

## 2. Checked and clean

- `candidate-001` conflates Phase 3's owned parse/publication contract with behavior expressly
  assigned downstream. The Phase 3 specification excludes uniform values and assigns them to
  Phase 6 (`docs/design/v2.0-RC3/DESIGN.md:1430-1432`). The target preserves the directive's
  Phase 3-owned effect as a positive per-program `instanceCount`, restricts it to vertex sources,
  and names positive and wrong-stage tests (`docs/phase3/v1/PHASE_3_DOC.md:650-654`).
- The binding Phase 1 contract supplies the semantics the candidate says were lost: for
  composite/deferred programs, Phase 7 performs the caller-side repeat loop and Phase 6 supplies
  the between-copy incrementing `instanceId` upload; for gbuffers/shadow programs, Phase 3 detects
  the directive, Phase 4 carries the per-slot count, and Phase 7 owns the open re-render case
  (`docs/phase1/v14/PHASE_1_DOC.md:1579-1580`). Phase 3's binding publication correspondingly routes
  instances to Phases 4 and 7 (`docs/phase3/v1/PHASE_3_DOC.md:1271`). Repeating downstream
  uniform-upload mechanics in Phase 3's §5 is therefore not required to preserve the contract and
  would cross its explicit anti-sprawl boundary.
- The authoritative Appendix A.3 row remains satisfied: Phase 3 recognizes and publishes the
  count, while the binding dependency already assigns the instanced re-render and `instanceId`
  behavior (`docs/research/v1/RESEARCH.md:1156-1160`). The mandatory conformance-map rule requires
  an in-scope satisfying design element, not duplication in every phase of contracts owned by
  adjacent phases (`docs/design/v2.0-RC3/DESIGN.md:804-808`).
- The finder-reported new-surface, interface, and conformance clean areas were rechecked. The
  schema-v2 `PropertyPredicate` and `MappingOrigin` repair is coherent across declarations,
  semantics, §5 publication, tests, milestones, and decisions. No separate interface-honesty or
  conformance defect survives.
- Prior-review material does not change the independent disposition. Round 15 records an earlier
  `countInstances` candidate as eliminated before adjudication, and subsequent literal-PASS rounds
  reviewed the unchanged directive/publication surface. Round 21's distinct schema-v2 interface
  correction has a recorded resolution and is coherent in the current target.

## 3. Verdict

# PASS
Counts: blocking=0; corrections=0; notes=0
Interface changed: no

The sole candidate is dropped, leaving zero blocking findings, corrections, and notes. Round 21's
one interface correction is resolved, and this fresh round finds no surviving defect, so the
current bytes converge to literal PASS. This adjudication orders no interface change.

No fix-up or additional verification round is required by this review. Phase 3 satisfies the
§G1.3 literal-PASS condition and may close as a verified dependency input.
