# Phase 5 adversarial review — round 3

## 0. Method and reading order

I independently re-derived both surviving candidates against the complete target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, the binding §5 regions of the Phase 1, 3, and 4 dependencies,
and the cited RESEARCH and supporting-evidence material. Only after settling those judgments did I
read `docs/phase5/reviews/PHASE_5_REVIEW_1.md` and
`docs/phase5/reviews/PHASE_5_REVIEW_2.md`, including their resolutions.

I used no network access, no subagents or other agent fan-out, and no forbidden source. In
accordance with the already-dispatched atomic role and the verify-loop skill, I did not invoke the
verification loop, run `scripts/verify`, or start another Codex session. There were no deviations
from the supplied reading contract.

Gate dropped candidate-003 because its finder quote at
`docs/phase5/v1/PHASE_5_DOC.md:281`–`:301` did not resolve uniquely. It is not admitted or counted.

## 1. Findings

### candidate-001 — Mandatory full-clear state is not bound to clear planning or successful execution

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:677`–`:679`,
`docs/phase5/v1/PHASE_5_DOC.md:709`–`:759`,
`docs/phase5/v1/PHASE_5_DOC.md:1038`–`:1042`, and
`docs/phase5/v1/PHASE_5_DOC.md:1156`–`:1163`

**Claim.** Phase 5 owns `fullClearRequired`, creates that mandatory state after lifecycle and
failure events, and owns color-clear planning and execution. Its exposed contract nevertheless
lets Phase 7 supply `ClearRequest.fullClear` without requiring `clearPlan` to combine that request
with the estate-owned bit or defining when successful execution consumes the bit. A conforming
caller can therefore request an ordinary clear while a mandatory full clear is pending.

**Evidence.** `abortFrame` explicitly *"marks `fullClearRequired`"* and makes the next frame's
mandatory full clear the safety boundary (`docs/phase5/v1/PHASE_5_DOC.md:677`–`:679`).
`ClearRequest` exposes a caller-supplied `boolean fullClear`
(`docs/phase5/v1/PHASE_5_DOC.md:709`–`:715`), while the policy promises that clear disabling does
not suppress a full clear after creation, resize, abort, or format fallback
(`docs/phase5/v1/PHASE_5_DOC.md:732`–`:734`). The executor contract validates provenance and
exactly-once use but does not derive effective full-clear intent from estate state or specify
success-only consumption (`docs/phase5/v1/PHASE_5_DOC.md:753`–`:759`). The resize checklist also
names the full-clear bit as Phase-5-owned state
(`docs/phase5/v1/PHASE_5_DOC.md:1038`–`:1042`), and clear failure must abort and mark full clear
(`docs/phase5/v1/PHASE_5_DOC.md:1260`).

**Required correction.** Specify that planning derives effective full-clear intent from
`request.fullClear || estate.fullClearRequired`, or replace the caller boolean with an intent that
cannot bypass estate policy. Clear the estate bit only after every required batch succeeds for the
validated generation. Retain or re-establish it on rejection, partial execution, or backend
failure. State this guarantee in §5.1 and add success/failure state-transition tests.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-002 — Partial resize delivery leaves retry without a common sizing baseline

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1020`–`:1036`,
`docs/phase5/v1/PHASE_5_DOC.md:1058`–`:1060`, and
`docs/phase5/v1/PHASE_5_DOC.md:1159`–`:1166`

**Claim.** When a resize consumer fails, earlier consumers have accepted the failed generation's
new sizing while later consumers have not. Phase 5 then publishes an off generation without a
notice and explicitly permits Phase 7 to retry, but every later publication supplies one global
`oldSizing`. The contract defines no rollback, replay, per-consumer acknowledgement, or
registration-reset precondition that restores a truthful common baseline.

**Evidence.** Accepted publication sends the same
`BufferResizeNotice(oldSizing,newSizing,newGeneration,reason)` to registered consumers in order
(`docs/phase5/v1/PHASE_5_DOC.md:1020`–`:1027`). On failure, dispatch stops after some consumers
have received the notice, the off publication sends no notice, and Phase 7 may choose retry
(`docs/phase5/v1/PHASE_5_DOC.md:1029`–`:1036`). Equality-only generation comparison
(`docs/phase5/v1/PHASE_5_DOC.md:1058`–`:1060`) detects divergence but does not repair the consumers'
different sizing states. Section 5 exposes the publisher failure signal and resize protocol to
Phase 7 and Phases 13/14 without a convergence rule
(`docs/phase5/v1/PHASE_5_DOC.md:1159`–`:1166`). The test plan proves partial delivery but does not
test recovery or retry (`docs/phase5/v1/PHASE_5_DOC.md:1349`–`:1354`).

**Required correction.** Define one deterministic recovery invariant after partial delivery:
track each consumer's acknowledged sizing/generation and construct truthful retry notices,
compensate or replay callbacks to restore a common baseline, or forbid retry until all surviving
registrations are closed and recreated. Make the retry precondition and `oldSizing` semantics
explicit in §5.1, expose only the recovery state the selected protocol requires, and test recovery
after failure at every callback position.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The revised ready-candidate
publication text consistently distinguishes driver-incapable ownership swap from consumer
failure. Final/anaglyph ownership is consistent across the scope, conformance map, detailed
design, and handoff. Composite blend-state ownership remains with Phase 7/4. The clear result
vocabulary and stale/exactly-once executor checks are internally consistent apart from
candidate-001's missing mandatory-state transition.

Dependency consumption is otherwise honest. Phase 5 distinguishes existing Phase 1 facade
contracts from requested additions, and its Phase 3/4 consumption matches their binding §5
surfaces. The ready-to-off resize transition, shadow operations, registry-recovery ownership,
fixed App B.3 table, App B.1/B.2/B.4 conformance coverage, format fallback, depth-copy tiers, and
cardinality-independent buffer design are coherent.

Neither surviving candidate is dropped on re-derivation. Prior-round findings are settled as
recorded by their resolutions; the present findings identify semantic gaps left in the Round-2
clear-executor and partial-delivery recovery surfaces rather than reopening the earlier omissions.
Candidate-003 remains excluded solely because Gate could not verify its finder evidence.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted findings are localized correction-level contract omissions; neither requires a
structural rebuild, so `FAIL` is not warranted. Both require changes to the declared §5
cross-phase interface or its semantics.

The correction count decreased from four in Round 1 and five in Round 2 to two in Round 3, which is
positive movement, but the loop has not converged because both findings remain on the newly revised
interface surface. The next required action is a scoped fix-up resolving candidate-001 and
candidate-002, appending resolutions to this review, and updating the Phase 5 addendum. Because the
fix-up must change §5 or equivalent declared interface semantics, the interface change-trigger
applies: a fresh verification round is required before Phase 5 can close.

## Resolutions

### candidate-001 — resolved

Re-derived against Phase 5's ownership of `fullClearRequired` and its clear executor. Section 4.6
now makes effective full-clear intent the caller request OR the estate-owned requirement, consumes
the bit only after every required batch succeeds for the validated generation, and retains or
re-establishes it on rejection, partial execution, or backend failure. Section 5.1 exposes the same
guarantee, and §8 adds the required success/failure transition coverage.

### candidate-002 — resolved

Selected the per-consumer acknowledgement protocol rather than rollback or registration reset.
Section 4.11 now tracks each registration's last successful sizing/generation, initializes new
registrations from their supplied acknowledged baseline, rejects unknown/future baselines, advances
only successful consumers, and defines retry `oldSizing` per consumer. This preserves truthful
baselines after partial delivery while converging all consumers on the retry's common `newSizing`.
Section 5.1 exposes that recovery contract, and §8 tests failure at every callback position
followed by retry convergence.

### Notes deferred

None. The adjudicator admitted no notes.
