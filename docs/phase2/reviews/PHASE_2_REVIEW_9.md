# Schmaloogium — Phase 2: Conformance harness — Review Round 9

## 0. Method and reading order

I independently re-derived both gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, especially its conformance tiers and milestone requirements.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially §5.
4. The supporting CI workflows under `.github/workflows/`.
5. The complete selected target, `docs/phase2/v1/PHASE_2_DOC.md`.
6. Only after settling both candidates, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
   `docs/phase2/reviews/PHASE_2_REVIEW_8.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents, agent fan-out, or nested verification run. The canonical
engine supplied the finder, refuter, and Gate material. The Gate reported no drops, and no
candidates were eliminated before adjudication. Forbidden sources were not read.

The prior reviews do not settle either present defect. Their earlier run-manifest corrections
clarified other blocks and unattributable-error counting, but did not define the `resources`
serialization. Their broad tier and reporting clean-area conclusions do not resolve the direct
contradiction between the scene-set-qualified tier identity in §1.3 and the narrower concrete
`TierLedger` row in §4.2.5.

## 1. Findings

### candidate-001 — The exposed run-manifest schema does not canonically encode its required resources block

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:701–746`, with the referenced sizing catalogue at
  `docs/phase2/v1/PHASE_2_DOC.md:1148–1159`.
- **Claim:** A Phase 4 or Phase 7 consumer can implement
  `schmaloogium.run-manifest/1` without guessing how resource-sizing records are serialized.
- **Evidence:** The manifest catalogue requires `resources` to carry colour-buffer formats,
  depth and shadow allocation, `centerDepthSmooth`, and noise sizing
  (`docs/phase2/v1/PHASE_2_DOC.md:713–716`). The canonical repeated-record grammar enumerates
  environment, option, program, frame, GL-error, image, and diagnostic records but no resource
  records; it only says that `resources.available` applies to §4.11.3's sizing keys
  (`docs/phase2/v1/PHASE_2_DOC.md:736–746`). That sizing table contains indexed buffers,
  structured clear colours, a set-like attribute value, program-keyed instance counts, and a
  capability shortfall, without defining complete line-wire names, scalar types, cardinalities,
  encodings, or absence rules (`docs/phase2/v1/PHASE_2_DOC.md:1148–1159`). Section 5 exposes the
  run-manifest wire schema to Phases 4 and 7
  (`docs/phase2/v1/PHASE_2_DOC.md:1350–1354`), so downstream implementations could produce
  incompatible manifests while each follows the current prose.
- **Disposition:** Admitted. Define the canonical `resources.*` wire keys, scalar and enum/value
  formats, indexing or map rules, structured clear-colour and capability-shortfall encodings, and
  presence/absence behavior for both values of `resources.available`. Ensure §5 points consumers
  to the complete schema.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-002 — TierLedger cannot represent evidence for scene-set-wide gates

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:520–527`.
- **Claim:** The mapped `TierLedger` design provides complete per-pack tier-state tracking for
  tier gates evaluated across a scene set.
- **Evidence:** The target defines tier state as per
  `(pack, version, tier, scene set)` (`docs/phase2/v1/PHASE_2_DOC.md:160–162`) and defines T0,
  T1, T2, and T3 named runs over pack-by-scene-set inputs
  (`docs/phase2/v1/PHASE_2_DOC.md:984–990`). A capture plan and resulting run manifest identify
  one scene (`docs/phase2/v1/PHASE_2_DOC.md:658–664`,
  `docs/phase2/v1/PHASE_2_DOC.md:703–708`). The concrete ledger nevertheless keys a row only by
  `(packId, packVersion, tier)` and retains one run id and one manifest hash
  (`docs/phase2/v1/PHASE_2_DOC.md:522–527`). It therefore cannot identify the scene set whose
  tier was established or deterministically point to all constituent scene evidence. This leaves
  the governing requirement for per-pack tier-state tracking
  (`docs/design/v1.1/DESIGN.md:671–673`) incompletely implementable.
- **Disposition:** Admitted. Add canonical scene-set identity to each ledger row and a
  deterministic aggregate evidence reference covering every constituent scene manifest. Specify
  invalidation, machine/human reporting, and `TierLedgerTest` expectations accordingly.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The new-surface sweep found the Round-7 Complementary family correction consistent across the
fixture matrix, exit-criteria mapping, run catalogue, milestone staging, handoffs, and ordered
implementation checklist. Decision IDs, request IDs, scene and fixture counts, milestone tags,
and examined cross-references were consistent.

The dependency/interface sweep found Phase 2's consumption of Phase 1's module seam,
`GLCapabilityProfile` serialization, recording/replay facilities, debug flags, fixture placement,
logging, and CI extension point honestly represented. The capture-plan schema and the
non-resource run-manifest blocks were substantially implementable, and requested interfaces were
kept separate from existing dependencies.

The conformance sweep otherwise found substantive mappings for the Appendix G fixtures, T0–T3
semantics, fixed-scene family coverage, fixture policy, OQ-10, the before-renderer subset, and §9
exit criteria. Both candidates survived independent re-derivation; neither was refuted or cleared.
The Gate dropped none, and no candidate-free finding is added.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both defects are bounded fix-up work rather than structural omissions requiring a rebuild.
Candidate-001 changes the consumer-facing run-manifest wire contract in the declared interface
region. Candidate-002 repairs Phase 2's internal tier evidence model and does not itself require
an interface change.

Round 8's stale whole-document dispatch blocker is absent from this round: the resolved selector
now covers lines 1–2017. The loop nevertheless has not converged, because two fresh corrections
remain after the prior FAIL. The next required action is a scoped fix-up resolving both findings
and recording their resolutions in this review. Because the run-manifest interface must change, a
fresh verification round is required before Phase 2 can close.

## Resolutions

### candidate-001 — resolved

Re-derived the resource domain from §4.11.3 and completed `schmaloogium.run-manifest/1` in §4.5.4.
The schema now fixes every `resources.*` key, scalar type, enum, dense-record cardinality and sort
rule; clear colours have four finite numeric components; per-texture shadow properties, per-program
attributes and instance counts, and capability shortfalls have canonical record encodings.
`resources.available=false` now requires all other resource keys absent, while `true` requires the
complete block. Section 5 explicitly exposes this complete grammar to Phases 4 and 7. This changes
the declared cross-phase interface and fires its fresh-verification trigger.

### candidate-002 — resolved

Changed the ledger identity to `(packId, packVersion, tier, sceneSetId, sceneSetSha256)` and replaced
the single-run pointer with a hashed canonical evidence index containing every constituent
`sceneId`, `runId`, and manifest hash. Exact membership, duplicate rejection, hash validation, and
invalidation on scene-set or constituent-evidence drift are now defined. Invalid rows remain
auditable but evaluate as `NOT_ATTEMPTED`; both machine and human reports expose the aggregate and
constituent pointers. `TierLedgerTest` now covers identity, completeness, invalidation, reporting,
sorting, and same-scene-set inconsistency detection.

### Notes deferred

None.
