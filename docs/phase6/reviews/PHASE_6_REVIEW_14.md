# Phase 6 — Uniform & sampler system — Verification Review 14

## 0. Method and reading order

I independently re-derived both surviving candidates against the complete target selected by the
manifest, the override-selected v3 Part I, Phase 6 assignment, document gate, mandatory template,
contract ground truth, and the binding Phase 1, 3, and 4 dependency regions. I focused on the
generation-adoption handshake, Phase 4 publication and teardown ownership, the direct-reset
lifecycle, the threading table, and the exported §5 interface. Supporting and Pintonium material
was treated only as evidence, never as contract. The v3 selection is a verification override and
was not treated as adoption by the historically anchored target.

Only after settling both candidates did I read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` through
`docs/phase6/reviews/PHASE_6_REVIEW_13.md`, in numeric order, and compare the interpretations with
settled material. There were no reading-order deviations, no network use, and no agent fan-out.
Per the dispatched-role rule, I did not invoke `$verify-loop`, `scripts/verify`, or another Codex
session. No forbidden source was read. The Gate dropped no candidates, and no candidate was
eliminated before adjudication.

## 1. Findings

### candidate-001 — Generation adoption is ordered before teardown that already occurs during publication

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:1375`
- **Claim:** The threading row contradicts the established generation-adoption handshake and
  Phase 4's atomic publication contract by requiring adoption before old-registry teardown.
- **Evidence:** Phase 6 requires Phase 7 to call `adoptRegistryGeneration` only after Phase 4
  accepts a replacement, using the generation from the newly reacquired authoritative
  publication, and before any frame, event, or participant activation against that replacement
  (`docs/phase6/v1/PHASE_6_DOC.md:443-452`). Phase 4's accepted publication installs the new state,
  increments its generation, transfers ownership, and idempotently closes the old registry within
  that same atomic operation; recovery after release begins likewise installs empty state,
  increments, and closes or quarantines the old state
  (`docs/phase4/v1/PHASE_4_DOC.md:1452-1460`). The threading row instead requires generation
  adoption “before old GL registry teardown” (`docs/phase6/v1/PHASE_6_DOC.md:1375`). Phase 7 cannot
  obtain the accepted authoritative generation before Phase 4 completes the operation that owns
  teardown, so the stated ordering has no implementable point.
- **Required correction:** Require render-thread adoption after accepted publication and
  reacquisition of the authoritative replacement generation, but before the replacement's first
  `beginFrame`, event, or participant activation. Remove the ordering against Phase 4-owned
  teardown.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-002 — Direct-reset contract omits lifecycle ordering

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:1245` and
  `docs/phase6/v1/PHASE_6_DOC.md:1376`
- **Claim:** Consumers cannot safely implement `WORLD_EPOCH` and `CLOSE` direct resets from the
  exported contract without choosing their own ordering relative to frame/activation use and the
  Phase 4 shutdown lifecycle.
- **Evidence:** Section 4.14 defines the state effects and accepted reason partition, but says only
  that world-epoch reset preserves generation and that close permanently retires the runtime
  (`docs/phase6/v1/PHASE_6_DOC.md:1219-1222`). The binding interface gives replacement adoption an
  explicit before-use constraint while exposing direct reset without an equivalent ordering rule
  (`docs/phase6/v1/PHASE_6_DOC.md:1244-1246`). The threading table adds only render-thread affinity
  for direct reset (`docs/phase6/v1/PHASE_6_DOC.md:1374-1376`). Phase 4 guarantees activity-token
  invalidation before later activation, release, off, replacement, or teardown, but does not place
  Phase 6's terminal close or release of its retained bridge within that sequence
  (`docs/phase4/v1/PHASE_4_DOC.md:1568`). Consequently different consumers may reset world state
  before final old-world use or after first new-world use, or close Phase 6 while its participants
  remain reachable, while still satisfying the published Phase 6 text.
- **Required correction:** In §5.1, require `WORLD_EPOCH` reset after final old-world use and before
  the next world's `beginFrame` or activation. Define `CLOSE` against the observable Phase 4
  token-invalidation, participant-detachment, and teardown milestones sufficiently to prevent
  participant use after close or retained activation state. Keep §§4.14 and 7.1 consistent without
  prescribing Phase 4 internals.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The revision marker, §0.13 addendum, end
marker, closed reset-reason partition, and repeated adoption/reset identifiers are synchronized.
The complete Appendix D conformance mapping, providers, cadences, milestones, smoothing rules,
sampler maps, participant ordering, custom bridge boundary, frame-begin ordering, and consumed
Phase 1, 3, and 4 contracts disclosed no further candidate defect. In particular, the direct-reset
row correctly identifies render-thread ownership; its defect is the missing lifecycle sequence,
not its thread assignment.

Neither surviving candidate was refuted, cleared, or subsumed on independent derivation. Prior
reviews were read last. Round 13 settled the operation-name partition in the threading table, but
its fix introduced the impossible pre-teardown adoption ordering challenged by candidate-001; it
did not settle direct-reset ordering. Earlier reviews settled the reset reason representation,
terminal nature of close, generation-adoption handshake, and state scopes, but none defines the
new `WORLD_EPOCH`/`CLOSE` happens-before contract challenged by candidate-002. There were no Gate
drops or candidates eliminated before adjudication.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both defects are bounded lifecycle-contract corrections, not structural misses requiring a
rebuild. Literal `PASS` is unavailable while they remain. Candidate-002 requires changing the
manifest-selected `cross-phase-interfaces` region, so the interface change trigger applies. The
next required action is a governed fix-up resolving both candidates and appending resolutions to
this review, followed by a fresh verification round before Phase 6 can close.

Trend: the recent correction counts are `3 -> 1 -> 2 -> 1 -> 2`. This round increases from Round
13, and the last three rounds (`2 -> 1 -> 2`) are not strictly decreasing. The recurrence reflects
new lifecycle wording and a remaining interface-ordering gap; it warrants explicit convergence
attention during fix-up but does not justify `FAIL`.

## Resolutions

### candidate-001 — resolved

Re-derived against Phase 4's accepted-publication operation, the old registry is already closed or
quarantined within publication before Phase 7 can reacquire the authoritative replacement
generation. Section 7.1 therefore no longer orders adoption before old-registry teardown. It now
requires render-thread adoption after accepted publication and authoritative-generation
reacquisition, but before any use of the replacement. Section 5.1 states the same sequence at the
consumer boundary. This does not change Phase 4 teardown ownership.

### candidate-002 — resolved

Sections 4.14, 5.1, and 7.1 now give both direct-reset reasons explicit lifecycle order.
`WORLD_EPOCH` follows final old-world use and precedes the next world's frame, event, or participant
activation. Terminal `CLOSE` follows Phase 4 activity-token invalidation and detachment of all
three Phase 6 participants, permits no later participant call, and occurs before Phase 4 completes
its owned teardown. This prevents retained activation state or participant reachability after
close without prescribing Phase 4's internal teardown mechanism.

The §5.1 edit intentionally changes the manifest-selected cross-phase interface region. A fresh
verify round is therefore required before Phase 6 can close.

### Notes deferred

None; the adjudicator admitted no notes.
