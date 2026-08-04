# Phase 6 — Uniform & sampler system — Verification Review 15

## 0. Method and reading order

I independently re-derived both surviving candidates against the complete target selected by the
manifest, the override-selected v3 Part I, Phase 6 assignment, document gate, mandatory template,
contract ground truth, and the binding Phase 1, 3, and 4 dependency regions. I focused on Phase
6's terminal `CLOSE` ordering, Phase 4's participant ownership and activity-token semantics, the
atomic publication sequence, and the exported §5 lifecycle contract. Supporting material was
treated only as evidence, never as contract. The v3 selection is a verification override and was
not treated as adoption by the historically anchored target.

Only after settling the candidates did I read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` through
`docs/phase6/reviews/PHASE_6_REVIEW_14.md`, in numeric order, and compare the interpretations with
settled material. There were no reading-order deviations, no network use, and no agent fan-out.
Per the dispatched-role rule and the supplied verify-loop skill, I did not invoke `$verify-loop`,
`scripts/verify`, or another Codex session. No forbidden source was read. The Gate dropped no
candidates, and no candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — `CLOSE` depends on an unexposed Phase 4 teardown midpoint

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:1227-1230`,
  `docs/phase6/v1/PHASE_6_DOC.md:1253`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1384`
- **Claim:** Phase 6 requires Phase 7 to invoke terminal `CLOSE` after Phase 4 invalidates the
  activity token and detaches all three Phase 6 participants, but before Phase 4 completes its
  owned teardown; the verified Phase 4 interface exposes no operation or notification at that
  midpoint.
- **Evidence:** Section 4.14 requires `CLOSE` after token invalidation and participant detachment,
  while allowing Phase 4 to complete registry/barrier teardown only afterward
  (`docs/phase6/v1/PHASE_6_DOC.md:1227-1230`). Section 5.1 makes the same sequence binding on the
  Phase 7 consumer (`docs/phase6/v1/PHASE_6_DOC.md:1252-1253`), and §7.1 repeats it as the direct
  reset's thread/lifecycle boundary (`docs/phase6/v1/PHASE_6_DOC.md:1384`). Phase 4, however,
  exposes publication as one operation: after validation it invalidates the old token, releases
  the old barrier, replaces the publication, increments generation, and closes the old registry
  (`docs/phase4/v1/PHASE_4_DOC.md:1410-1421`). Accepted barrier ownership belongs exclusively to
  the publisher (`docs/phase4/v1/PHASE_4_DOC.md:1288-1292`), snapshot consumers have no teardown
  capability (`docs/phase4/v1/PHASE_4_DOC.md:643-647`), and the retained token confers no lifecycle
  operation (`docs/phase4/v1/PHASE_4_DOC.md:1340-1347`). Thus Phase 7 cannot observe or act at the
  required intermediate state through the consumed contract. Round 14's resolution introduced
  this precise ordering; prior settled material does not supply the missing handoff.
- **Required correction:** Synchronize §§4.14, 5.1, and 7.1 around an existing caller-observable
  boundary. Define `CLOSE` after final Phase 6 participant use and at a point Phase 7 can actually
  control, without assuming access to Phase 4's internal detachment midpoint. If the present
  midpoint is essential, request and await a verified Phase 4 callback or split operation that
  exposes it.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The replacement-generation adoption
ordering, `WORLD_EPOCH` ordering, closed reset-reason partition, and repeated lifecycle terminology
are internally consistent apart from the terminal-`CLOSE` coordination gap. The complete Appendix
D conformance map, providers, cadences, milestones, sampler maps, smoothing rules, participant
ordering, custom bridge boundary, frame-begin ordering, and other consumed Phase 1, 3, and 4
interfaces disclosed no additional candidate defect.

Candidate-002 is dropped as an exact duplicate of candidate-001. It challenges the same Phase 6
sentences, the same absent Phase 4 teardown midpoint, and the same Phase 7 implementability
failure, with the same correction obligation. Consolidating it avoids counting one defect twice;
none of its evidence or interface impact is lost from the admitted finding.

Prior reviews were read last. Round 14 correctly required explicit direct-reset ordering, but its
resolution introduced the presently unimplementable post-detachment/pre-teardown boundary. No
earlier review settles an API or callback that exposes that boundary. There were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a bounded lifecycle-contract correction, not a structural miss requiring a
rebuild. Literal `PASS` is unavailable while it remains. Because the correction reaches the
manifest-selected `cross-phase-interfaces` region, the interface change trigger applies. The next
required action is a governed fix-up resolving candidate-001 and appending its resolution here,
followed by a fresh verification round before Phase 6 can close.

Trend: the recent correction counts are `1 -> 2 -> 1 -> 2 -> 1` for Rounds 11–15. This round
decreases from Round 14, but the last three rounds (`1 -> 2 -> 1`) are not strictly decreasing.
The defect is a direct consequence of the prior lifecycle fix and warrants convergence attention,
but it does not justify `FAIL`.

## Resolutions

### candidate-001 — corrected

Re-derived against Phase 4's atomic publication sequence, the implementable boundary is the one
Phase 7 controls before it invokes that sequence. Sections 4.14, 5.1, and 7.1 now require terminal
`CLOSE` after final use of all three Phase 6 participants; Phase 7 thereafter forbids another
participant call and initiates Phase 4's existing atomic publication replacement or teardown.
This removes the unsupported dependency on Phase 4's internal post-invalidation/post-detachment
midpoint without requesting a new Phase 4 operation. The §5 interface region changed
intentionally, so a fresh verification round is required before Phase 6 can close. Compact §0.15
records the correction and the §G1.3 status now identifies Review 15 as resolved but unverified.

### Notes deferred

None; the adjudicator admitted no notes.
