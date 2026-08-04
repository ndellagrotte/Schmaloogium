## 0. Method and reading order

I first independently re-derived both surviving candidates from the complete target,
`docs/phase6/v1/PHASE_6_DOC.md`, then checked the governing Part I, Phase 6 specification,
document gate, and mandatory template in `docs/design/v2.0-RC3/DESIGN.md`, the contract ground
truth in `docs/research/v1/RESEARCH.md`, and the manifest-selected binding contracts of Phases 1,
3, and 4. Listed Pintonium and OptiFine sources were treated only as supporting evidence, never as
contract. Focused target and dependency searches checked the maintenance range, the complete
custom-upload command disposition, and the Phase 1 `UniformService` overload set and absent-verbs
ledger.

Only after reaching independent dispositions did I read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` through
`docs/phase6/reviews/PHASE_6_REVIEW_19.md`, in numeric order, and compare the candidates with
settled material. Prior rounds reinforce the maintenance-marker treatment but do not settle away
either current defect. There were no reading-order deviations, no network use, and no agent
fan-out. Per the dispatched atomic-role rule and the verify-loop skill, I did not invoke
`$verify-loop`, `scripts/verify`, or another Codex session. No forbidden source was read. The Gate
reported no drops, and no candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — Introductory maintenance range omits §0.19

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:15-18`
- **Claim:** The compact introductory provenance marker does not acknowledge the latest governed
  maintenance addendum.
- **Evidence:** The introduction says that `§§0.3–0.18` record later governed maintenance
  (`docs/phase6/v1/PHASE_6_DOC.md:15-18`), but the document contains `§0.19 Review-19 corrections`
  (`docs/phase6/v1/PHASE_6_DOC.md:218-222`). The parallel closing marker correctly says
  `§§0.3–0.19` (`docs/phase6/v1/PHASE_6_DOC.md:1813-1817`), confirming that the opening range is
  stale rather than intentionally narrower. Prior settled rounds consistently treat an exhaustive
  compact range that trails a new addendum as a correction-level provenance defect.
- **Required correction:** Change the introductory range from `§§0.3–0.18` to `§§0.3–0.19`.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-002 — Published `Int3` command has no consumed Phase 1 upload verb

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:1215-1228`,
  `docs/phase6/v1/PHASE_6_DOC.md:1294-1301`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1392-1400`
- **Claim:** Phase 6 publishes an executable `Int3` custom-upload command but cannot execute a
  matching, located `ivec3` declaration through its consumed Phase 1 contract.
- **Evidence:** The closed `CustomUploadCommand` algebra includes `Int3`
  (`docs/phase6/v1/PHASE_6_DOC.md:1215-1228`), and the exhaustive submission rules require every
  type-correct present location to return `Accepted` and enter the activation batch
  (`docs/phase6/v1/PHASE_6_DOC.md:1294-1301`). Phase 1's `UniformService` exposes integer scalar,
  two-component, and four-component overloads, but no three-component integer overload
  (`docs/phase1/v14/PHASE_1_DOC.md:2928-2938`); its absent-verbs ledger explicitly classifies
  `ivec3` uploads as awaiting an additive request from the first consumer
  (`docs/phase1/v14/PHASE_1_DOC.md:3369`). Phase 6 nevertheless says no Phase 1 change is requested
  and that every required overload already exists (`docs/phase6/v1/PHASE_6_DOC.md:1392-1400`). No
  generic equivalent upload route or target-side unsupported-`Int3` rejection rule closes the gap.
- **Required correction:** If `Int3` remains in the published custom-upload algebra, request and
  consume an additive Phase 1 `UniformService.upload(UniformLocation,int,int,int)` operation.
  Otherwise remove `Int3` and narrow the corresponding custom-upload mapping. Align §§5.1 and 5.2
  with the selected contract; do not remove separately useful fixed-schema or expression-value
  vocabulary unless the chosen contract also makes it unsupported.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The Review-19 invalid-counter correction
is synchronized across the detailed custom-refresh rules, binding §5 interface, degradation
behavior, tests, and implementation checklist. Its batch discard, diagnostic, Phase 4 degradation,
no-same-activation retry, and next-activation fresh retry semantics are deterministic. Both
corrected Phase 1 conformance-map coordinates resolve to the intended current binding rows.

The governing Phase 6 conformance surface remains otherwise complete: every Appendix D uniform
has provider, cadence, semantics, and milestone coverage; smoothing, temporal snapshots,
center-depth disposition, sampler maps, frame-begin ordering, notifier audit, and provider seams
remain represented. Phase 3 declaration/resource/macro/custom-expression contracts and Phase 4
layout, cache, activity-token, participant, and degradation contracts are consumed consistently.
The Phase 1 facade otherwise supplies the upload, readback, error, recording, and diagnostics
operations claimed by Phase 6.

Neither candidate was refuted, cleared, subsumed, or dropped on independent derivation. Reading
Reviews 1-19 last found no settled material that supplies an `ivec3` upload or exempts the stale
opening range. Earlier provenance resolutions instead confirm candidate-001's classification.
There were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded corrections rather than structural misses requiring a rebuild, so
`FAIL` is not warranted. Literal `PASS` is unavailable while two corrections remain.
Candidate-002 requires changing the manifest-selected `cross-phase-interfaces` region;
candidate-001 does not. The next required action is a governed fix-up resolving both candidates
and appending resolutions to this review, followed by a fresh verification round before Phase 6
can close because the interface change trigger applies.

Trend: Round 18 reached literal `PASS` for its reviewed bytes. Round 19 then found two corrections
in the new §0.18 surface, and Round 20 remains at two corrections after that fix-up: one localized
maintenance-range regression and one newly exposed dependency/interface mismatch. The count is
flat rather than converging, and repeated compact-marker drift warrants explicit attention during
fix-up, but both defects remain locally repairable and do not justify escalation to `FAIL`.

## Resolutions

### candidate-001 — resolved

Re-derived from the target's maintenance headings and both compact provenance markers. The opening
range ended at §0.18 while §0.19 already existed. Because this fix-up itself requires the next
compact addendum, the header, opening marker, and closing marker now consistently end at §0.20;
§0.20 records this correction without importing another phase's addendum conventions.

### candidate-002 — resolved

Re-derived from the closed custom command algebra and the verified Phase 1 facade. A present,
type-correct `Int3` must execute, but Phase 1 publishes integer uploads only for one, two, and four
components and explicitly leaves `ivec3` to its first consumer. Retaining `Int3` preserves Phase
6's already-published `IVEC3` schema/value vocabulary and the closed custom type surface. Section
5.2 therefore requests and consumes the exact additive
`UniformService.upload(UniformLocation,int,int,int)` verb, forbids any raw-GL substitute, and makes
Phase 6 implementation of `Int3` wait for the Phase 1 grant and its required fresh verification.
The binding §5.1 row, decision ledger, contradiction/gap ledger, and requested-upstream list now
state the same dependency. This intentionally changes the manifest-selected
`cross-phase-interfaces` region, so a fresh verify round is required before Phase 6 can close.

### Notes deferred

None. The adjudicated review admitted no notes.
