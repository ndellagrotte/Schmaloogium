# Phase 2 verification review — round 25

## 0. Method and reading order

I first re-derived both candidates from the manifest-selected whole target,
`docs/phase2/v1/PHASE_2_DOC.md`; the governing Part I, Phase 2 target specification, document gate,
and mandatory template in `docs/design/v1.1/DESIGN.md`; the contract ground truth in
`docs/research/v1/RESEARCH.md`; the supporting CI evidence under `.github/workflows`; and the
binding Phase 1 contract in `docs/phase1/v14/PHASE_1_DOC.md`. Only after settling those independent
judgments did I read the discovered prior reviews,
`docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
`docs/phase2/reviews/PHASE_2_REVIEW_24.md`, in round order, and compare the candidates with settled
findings and resolutions.

There were no reading-order deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. No
forbidden source was consulted. The canonical engine reported no candidates eliminated before
adjudication and no Gate drops.

Prior review 24's resolution says it advanced the closing history through round 24 and added
§0.25, but that advancement is absent from the current target bytes. No prior review settled or
cleared the distinct v0.3 terrain-scene mapping defect.

## 1. Findings

### candidate-001 — Closing verification history was not advanced after the round-24 fix-up

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:193–195,2245–2247`
- **Claim:** The closing paragraph does not accurately identify the surface reviewed in the
  preceding round, its applied correction, or the current review round.
- **Evidence:** Section 0.25 is headed “Round-24 fix-up” and says it corrected the closing
  verification history after the round-23 fix-up
  (`docs/phase2/v1/PHASE_2_DOC.md:193–195`). The sole operative closing paragraph instead says
  round 23 reviewed §0.23, required §0.24, and round 24 is current
  (`docs/phase2/v1/PHASE_2_DOC.md:2245–2247`). That paragraph describes the pre-§0.25 lifecycle and
  contradicts both the applied addendum and this manifest-resolved round-25 review.
- **Disposition:** Admitted. State that round 24 reviewed the §0.24 surface and required the
  now-applied non-interface §0.25 correction, and that round 25 is the current fresh
  whole-document review. Preserve the unverified and no-version-roll statements.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-002 — The v0.3 mapping makes a non-terrain scene a mandatory exit condition

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:450–455,472,1175`
- **Claim:** The conformance map strengthens the authoritative v0.3 exit criterion by adding the
  separately classified `entities-blocks` scene to the mandatory gate.
- **Evidence:** The authoritative v0.3 criterion is “Classic packs at T2 within tolerance on
  terrain scenes” (`docs/research/v1/RESEARCH.md:948`). The target classifies `terrain-day` as
  “terrain + sky” but classifies `entities-blocks` as “per-draw identity”
  (`docs/phase2/v1/PHASE_2_DOC.md:444–450`), then expressly says `entities-blocks` exceeds the
  specified families and is first gated at v0.3 for Phase 9/10 and `mc_Entity` regression coverage
  (`docs/phase2/v1/PHASE_2_DOC.md:452–455`). Nevertheless, the v0.3 map requires
  `terrain-day` plus `entities-blocks` (`docs/phase2/v1/PHASE_2_DOC.md:472`), while `RUN-T2`
  requires a pass on every shot in the milestone-declared scene set
  (`docs/phase2/v1/PHASE_2_DOC.md:1175`). Useful supplementary coverage does not make that
  separately classified scene part of the authority's terrain-only milestone criterion.
- **Disposition:** Admitted. Remove `entities-blocks` from the mandatory v0.3 exit-criterion
  mapping and retain it as an optional or separately reported Phase 9/10 implementation or
  regression gate unless authoritative upstream text broadens the milestone.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The header's last-revised marker and §0.25 addendum are internally consistent, Phase 2 remains
explicitly unverified without a version roll, and no equivalent round-25 closing statement cures
candidate-001. The selected Phase 1 binding contract grants Phase 2's declared consumptions;
ungranted needs remain requests. The complete §5 interface region is internally consistent with
the detailed designs and neither admitted correction changes it.

The Appendix G registry mapping, T0–T3 tier mapping, general harness-requirement mapping, and all
other examined RESEARCH.md §9 exit-criterion rows remain supported. Candidate-002 was tested
against the scene-family definitions, the stated reason for extra entity coverage, and the
`RUN-T2` pass rule; none makes `entities-blocks` a terrain scene or optional within the mapped
v0.3 gate. The mandatory thirteen-section structure, fixture policy, before-renderer subset,
OQ-10 spike, and document gate otherwise remain intact.

Both candidates survive re-derivation and comparison with prior settled material. No candidate
was refuted or cleared, the Gate dropped none, and no finding is created from candidate-free clean
areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: no

Both defects are localized corrections rather than structural misses requiring a rebuild. Neither
touches the manifest-declared cross-phase interface/change-trigger region.

Recent correction counts for rounds 20–25 are 2, 1, 1, 1, 1, and 2. Corrections are not strictly
decreasing, and the increase in this round—together with the recurring one-round-late closing
history—is a convergence warning. It does not justify dropping either evidence-backed correction
or escalating these localized defects to FAIL.

Next required action: apply both non-interface fix-ups and record their resolutions in this review.
Phase 2 remains unverified. After fix-up, run a fresh whole-document verification round; do not
roll the version while the loop remains open.

## Resolutions

### candidate-001 — resolved

Advanced the operative closing paragraph to state that round 24 reviewed the §0.24 surface,
required the now-applied non-interface §0.25 correction, and that round 25 is the current fresh
whole-document review. The unverified status and no-version-roll rule remain unchanged. Added the
compact §0.26 addendum and advanced the header marker.

### candidate-002 — resolved

Re-derived the v0.3 criterion from `docs/research/v1/RESEARCH.md` §9 and removed
`entities-blocks` from its mandatory `RUN-T2` mapping. Swept the related §3.4 explanation,
implementation schedule, and decision-log row so that the scene remains separately reported Phase
9/10 implementation and regression coverage, not part of the terrain-scene exit criterion.

### Notes deferred

None.

The cross-phase interface region is unchanged. Phase 2 v1 remains unverified pending a fresh
whole-document review, and the version directory remains `v1` while the loop is open.
