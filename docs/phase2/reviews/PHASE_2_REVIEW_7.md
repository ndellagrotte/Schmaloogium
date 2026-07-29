# Schmaloogium — Phase 2: Conformance harness — Review Round 7

## 0. Method and reading order

I independently re-derived both gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, including §8, §9, and Appendix G.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially §5.
4. The whole selected target, `docs/phase2/v1/PHASE_2_DOC.md`.
5. Only after settling both candidates, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
   `docs/phase2/reviews/PHASE_2_REVIEW_6.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents or other agent fan-out and did not invoke the verification
harness. The canonical engine supplied the finder, refuter, steelman, and Gate material. The Gate
dropped no candidates. Candidate-002 was eliminated during Refute before adjudication. Forbidden
sources were not read.

The prior reviews do not settle the present defects. Round 6 corrected checklist item 36's
milestone tag and separated implementation timing from recurring execution cadence, but its
resolution did not address the item's contradictory placement under “Later milestones.” No prior
review selects one Complementary variant as the primary target; earlier broad clean-area statements
about the pack matrix therefore do not settle candidate-003's narrower attribution question.

## 1. Findings

### candidate-001 — A v0.1 implementation item remains under “Later milestones”

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md` §12, item 36.
- **Claim:** The checklist consistently communicates that dual-spec `RUN-T0` and
  `RUN-T1-REGRESS` are implemented at v0.1, separately from their provisional recurring
  v0.1–v0.5 release-gate cadence.
- **Evidence:** The milestone table explicitly gives the dual-spec run definitions the single
  implementation tag `v0.1` and separately states their recurring release-gate execution
  (`docs/phase2/v1/PHASE_2_DOC.md:1620`). Checklist item 36 repeats that `v0.1` tag but places the
  item beneath the “Later milestones” heading alongside v0.2 and v0.4 implementation work
  (`docs/phase2/v1/PHASE_2_DOC.md:1995–2004`). The governing template defines §12 as an
  **ordered** implementation checklist (`docs/design/v1.1/DESIGN.md:540–541`), while milestone
  tags mean “implemented at that milestone” (`docs/design/v1.1/DESIGN.md:320–322`). The heading
  placement therefore supplies a sequencing signal that conflicts with the explicit tag.
- **Disposition:** Admitted. Move item 36 into the “As soon as v0.1 renders” group beside the
  other T0/T1 work, retaining both its `v0.1` tag and its provisional recurring v0.1–v0.5
  release-gate qualification.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-003 — The conformance map attributes an unsupported primary-target variant choice

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:295–300`.
- **Claim:** The cited authority supports designating Complementary Unbound specifically as the
  primary compatibility target.
- **Evidence:** The conformance map lists both Complementary Reimagined and Complementary Unbound,
  but attaches “primary target (§8.1, Angelica precedent)” only to Unbound
  (`docs/phase2/v1/PHASE_2_DOC.md:295–300`). Research §8.1 instead says Angelica standardized on
  “Complementary + Euphoria Patches” and recommends that family-level target for Schmaloogium
  (`docs/research/v1/RESEARCH.md:899–905`). Appendix G explicitly includes both Complementary
  variants and again names only **Complementary** as the primary compatibility target
  (`docs/research/v1/RESEARCH.md:1538–1541`). Nothing in the cited authority chooses Unbound over
  Reimagined.
- **Disposition:** Admitted. Remove the Unbound-only authority attribution and represent
  Complementary as the family-level primary target across both variants, or explicitly mark a
  single-variant choice as a separate implementation-time decision supported by additional
  evidence.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The Round-6 run-manifest correction is consistent across §4.2.1, §4.5.4, §5.1, and §8.1:
`gl_errors.count` counts all dense records and the unattributable count is derived from required
boolean `attributed` fields without a redundant scalar. The canonical fixture path follows the
existing capture-plan wire-fixture convention. Apart from item 36's subsection placement, the
dual-spec `v0.1` implementation tag, provisional v0.1–v0.5 release-gate cadence, named runs, and
§11.3 qualification are repeated consistently.

The interface sweep found the examined Phase 1 consumption claims grounded in its binding §5,
including the module seam, `GLCapabilityProfile` serialization and fixture placement, recording
machinery, capture flags, diagnostics, logging, and CI extension point. The edited run-manifest
surface supplies implementable wire detail for unattributable-error derivation. The conformance
sweep otherwise found substantive support for the tier rows, harness-requirement mapping,
initial-scene coverage, milestone exit criteria, fixture policy, and dual-spec cadence.

Candidate-002, concerning the manifest-declared interface-region boundary, was eliminated during
Refute and is cleared rather than converted into a finding. Both surviving candidates held on
independent re-derivation. The Gate dropped none, and no candidate-free finding is added.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: no

Both findings are bounded local corrections, not structural misses requiring a rebuild. Neither
changes the manifest-declared cross-phase interface/change-trigger region.

The fresh round required by Round 6's interface change has now occurred, and the revised interface
surface was clean in the examined areas. The overall loop has nevertheless not converged to PASS:
the recent correction counts are not strictly decreasing (rounds 5–7 are 3 → 2 → 2), and two
non-interface corrections remain. The next required action is a scoped fix-up resolving both
findings and recording their resolutions in this review, followed by the next fresh verification
round. Phase 2 cannot close until a round returns literal PASS with zero blocking findings and zero
corrections.

## Resolutions

### candidate-001 — Resolved

Moved dual-spec `RUN-T0` / `RUN-T1-REGRESS` from “Later milestones” into “As soon as v0.1
renders,” immediately after the other v0.1 T0/T1 work. Retained the `v0.1` implementation tag and
the provisional recurring v0.1–v0.5 release-gate cadence, added the evaluator predicates as its
test hook, and renumbered the later items to preserve §12's ordered checklist.

### candidate-003 — Resolved

Re-derived the designation from Research §8.1 and Appendix G: both identify Complementary at the
family level, while neither selects Reimagined or Unbound individually. Applied
`primary-target family` to both Complementary variant rows and removed the unsupported
Unbound-only attribution.

### Notes deferred

None.

### Interface status

The manifest-declared `cross-phase-interfaces` region was not edited. No interface change trigger
fires from this fix-up.
