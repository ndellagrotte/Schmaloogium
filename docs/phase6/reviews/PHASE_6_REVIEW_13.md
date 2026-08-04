# Phase 6 — Uniform & sampler system — Verification Review 13

## 0. Method and reading order

I independently re-derived the sole surviving candidate against the complete target selected by
the manifest, with focused checks of the public runtime declarations, lifecycle operation
partition, threading rules, tests, and cross-phase interface region. I checked the override-selected
v3 Part I, Phase 6 assignment, document gate, and mandatory template; the relevant contract ground
truth; and the binding Phase 1, 3, and 4 dependency regions. Supporting and Pintonium material was
treated only as evidence, never as contract. The v3 selection is a verification override and was
not treated as adoption by the historically anchored target.

Only after settling the candidate did I read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` through
`docs/phase6/reviews/PHASE_6_REVIEW_12.md`, in numeric order, and compare the interpretation with
settled material. There were no reading-order deviations, no network use, and no agent fan-out.
Per the dispatched-role rule, I did not invoke `$verify-loop`, `scripts/verify`, or another Codex
session. No forbidden source was read. The Gate dropped no candidates, and no candidate was
eliminated before adjudication.

## 1. Findings

### candidate-001 — Threading table still describes generation transitions as direct resets

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:1371`
- **Claim:** The normative threading table contradicts the now-closed lifecycle operation
  partition by calling registry replacement and GL-context loss a reset instead of generation
  adoption.
- **Evidence:** Section 4.14 assigns registry replacement, shaders-off, and GL-context loss
  exclusively to `adoptRegistryGeneration`, while direct `reset` accepts exactly `WORLD_EPOCH`
  and `CLOSE`; every other direct-reset reason fails fast without mutation
  (`docs/phase6/v1/PHASE_6_DOC.md:1205-1218`). Section 5 exports the same closed reason domain and
  operation partition to consumers (`docs/phase6/v1/PHASE_6_DOC.md:1241`). The threading table
  nevertheless instructs “reset after registry replacement/context loss” on the render thread
  (`docs/phase6/v1/PHASE_6_DOC.md:1364-1371`). In a normative implementation table, “reset” can
  select the expressly forbidden public operation rather than merely name a generic lifecycle
  event. Earlier reviews permitted umbrella reset terminology before the public reason identifiers
  and exact operation partition were closed; Round 12's resolved interface change is new settled
  material and makes this remaining instruction contradictory.
- **Required correction:** Rewrite the row to name registry-generation adoption for pack/registry
  replacement and GL-context loss. If direct-reset thread affinity is retained in the table, state
  it separately and limit it to `WORLD_EPOCH` and `CLOSE`.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The five public reset-reason identifiers,
the adoption-result identifiers, and the accepted operation subsets agree across §§2.2, 4.14, and
5.1. The current §G1.3 status correctly says that the post-fix-up bytes await a fresh review. The
public runtime and lifecycle surface, manifest-declared cross-phase interface region, Phase 1/3/4
binding consumptions, complete Appendix D inventory and cadence mapping, sampler maps, smoothing
rules, notifier audit, temporal behavior, barrier fulfillment, and center-depth decision disclosed
no further candidate defect. No surviving candidate was refuted or cleared on re-derivation, and
there were no Gate drops to carry forward.

Prior reviews were read last. Rounds 4, 7, and 8 settled that the semantic reason set did not then
require a distinct concrete representation and that `RESET` could serve as lifecycle-summary
terminology. Round 9 introduced generation adoption, Round 10 corrected the summary inventory, and
Round 12 subsequently closed the public identifiers and exact adoption/direct-reset partition.
Those earlier dispositions therefore do not clear the present, narrower conflict in an actionable
threading row.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted inconsistency is a bounded wording correction, not a structural miss requiring a
rebuild. Literal `PASS` is unavailable while it remains. The manifest-selected
`cross-phase-interfaces` region need not change. The next required action is a governed fix-up that
resolves candidate-001 and appends its resolution to this review, followed by a fresh verification
round because the resulting target bytes will be unreviewed.

Trend: the recent correction counts are `3 -> 1 -> 2 -> 1`. This round decreases from Round 12,
but the last three rounds (`1 -> 2 -> 1`) are not strictly decreasing. The remaining issue is a
localized consistency residue from the newly closed lifecycle API, not grounds for `FAIL`; closure
still requires its fix-up and a fresh literal-PASS review.

## Resolutions

### candidate-001 — resolved

Re-derived against the closed operation partition in §§2.2, 4.14, and 5.1. The §7.1 threading row
was an actionable lifecycle instruction, so its generic “reset” wording could direct callers to an
operation that rejects those reasons. The row now assigns pack/registry replacement and
GL-context loss to generation adoption, and a separate row restricts direct reset to
`WORLD_EPOCH` and `CLOSE`. This does not change the manifest-declared cross-phase interface region.

The compact §0.13 addendum, revision marker, maintenance status, and end marker record this fix-up.
The resulting target bytes await a fresh verification round.

### Notes deferred

None.
