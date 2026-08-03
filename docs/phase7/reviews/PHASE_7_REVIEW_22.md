## 0. Method and reading order

I independently re-derived the Gate-surviving candidate from the whole Phase 7 target, the
manifest-selected RC3 governing sections (including the Phase 7 specification, document gate, and
mandatory template), RESEARCH ground truth, and the binding §5 contracts of Phases 2–6. I checked
the cited detailed lifecycle and binding teardown protocol against the compact lifecycle diagram
before reading prior reviews. Only after settling that judgment did I read
`docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_21.md`, in order and last.

The only reading-list deviation was
`reference-src/schlorbium-HD_U_G6_pre1/files.txt`: the resolved contract also forbids every
`*.txt` source, so I did not read it. It was immaterial to this internal lifecycle-order candidate.
There was no network use, forbidden-source use, or agent fan-out. This was the canonical engine's
already-dispatched atomic adjudication role, so the supplied `verify-loop` instructions required
completing only this role without invoking the loop or delegating. No candidates were eliminated
before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — Quiescing lifecycle diagram reverses the required Phase 9 deactivation order

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:602`–`:604`
- **Claim:** The compact lifecycle diagram contradicts the document's normative teardown order by
  releasing fixed function before Phase 9 is deactivated.
- **Evidence:** The sequential `QUIESCING` diagram orders “reset Phase 9 dynamics,” then “release
  fixed function,” and only afterward “deactivate Phase 9 then publish Phase 5/4 off”
  (`docs/phase7/v1/PHASE_7_DOC.md:602`–`:604`). The detailed frame lifecycle requires reset before
  fixed-function release on abort, resize, off, replacement, and teardown paths
  (`docs/phase7/v1/PHASE_7_DOC.md:733`–`:736`). More decisively, the binding teardown protocol
  requires Phase 7 to “reset and deactivate Phase 9” before releasing Phase 5/4 in reverse
  composition order (`docs/phase7/v1/PHASE_7_DOC.md:1969`–`:1972`). Because the diagram represents
  an ordered lifecycle transition, an implementer following it can release Phase 4 to fixed
  function while Phase 9 remains active. Correct normative coverage elsewhere establishes the
  intended order but does not cure the contradictory implementation-facing summary.
- **Required correction:** Reorder the `QUIESCING` diagram so it closes Phase 8, resets and
  deactivates Phase 9, then releases/publishes Phase 5/4 off, and finally closes Phase 6.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the contradictory diagram is outside the
  manifest-selected §5 region, whose binding teardown protocol already states the correct order.

## 2. Checked and clean

The finder-reported clean areas survived independent re-derivation apart from the admitted compact
diagram inconsistency. The new Phase 8 planning, publication, authenticated execution, hook-health,
rollback, teardown, milestone, decision, and checklist terminology is otherwise internally
consistent. The §5 Phase 8 hand-off defines its borrowed frame view, non-nestable execution extent,
validation, cleanup, construction/publication ordering, ownership rollback, and nested health
projection without assuming an unverified Phase 8 dependency. Consumed Phase 2–6 contracts remain
within their binding surfaces, and pending dependency gaps remain explicitly gated.

The governing frame-flow and lifecycle requirements, Appendix A.1 program routes, all eleven hook
needs, seven-row reference timeline, engine-flag slice, Appendix E dispositions, and OQ-3/OQ-4
spikes remain mapped with substantive detailed-design coverage. No supplied candidate was cleared
or dropped on re-derivation. Prior reviews do not settle this contradiction: Round 21 verified the
then-current correction surface, while the present Phase 8 maintenance text leaves §§4.2 and 5.3
aligned and only the neighboring lifecycle diagram out of order.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The single admitted defect is a localized lifecycle-summary correction and does not require a
structural rebuild or a change to the declared cross-phase interface. Round 21 reached zero
corrections, but the current post-PASS maintenance surface introduces one correction, so literal
convergence is no longer established. The next required action is a scoped fix-up of
candidate-001, including this review's `## Resolutions` record and the Phase 7 addendum, followed
by a fresh whole-document verification round to regain literal PASS. The correction itself does
not fire the §5 interface change trigger; any fix-up that changes §5 would do so independently.

## Resolutions

### candidate-001 — applied

Re-derived against the detailed frame-finalization rule in §4.2 and the binding teardown protocol
in §5.3. The `QUIESCING` diagram in §4.1 now closes Phase 8, resets and deactivates Phase 9,
releases fixed function while publishing Phase 5/4 off, and finally closes Phase 6. This removes
the contradictory interval in which fixed function could be released while Phase 9 remained
active. The detailed lifecycle and §5 contract required no change.

The compact §0.26 addendum records the correction, and the closing status now requires a fresh
whole-document review. The manifest-selected cross-phase interface region is byte-unchanged, so
this fix does not fire its change trigger.

### Notes deferred

None; the adjudicator admitted no notes.
