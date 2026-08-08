# Phase 7 verification review — Round 29

## 0. Method and reading order

I independently re-derived the sole Gate-surviving candidate from the complete target at
`docs/phase7/v1/PHASE_7_DOC.md`, the manifest-selected `docs/design/v3/DESIGN.md` Part I, Phase 7
specification, document gate, and mandatory template, RESEARCH ground truth, the binding §5
contracts of Phases 2–6, and the relevant permitted supporting evidence. I settled the candidate's
interpretation, severity, and interface classification before reading
`docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_28.md`, in order and last, and then checked the candidate against
that settled material.

The selected v3 design revision is the supplied verification-only override; it does not rewrite
the target's declared adoption state. I did not read
`reference-src/schlorbium-HD_U_G6_pre1/files.txt`, because the resolved contract forbids every
`*.txt` source and that source is immaterial to the surviving metadata candidate. There was no
network use, forbidden-transcript use, or agent fan-out. This was the canonical engine's
already-dispatched atomic adjudication role, so the supplied `verify-loop` instructions required
completing only this role without invoking the loop or delegating. There were no other reading-order
deviations.

Gate dropped candidate-002 (retired-generation rollback sequence) because its cited Phase 6 quote
did not resolve, and candidate-003 (Phase 5 R7-1–R7-3 interface status) because its cited Phase 7
quote did not resolve. They remain excluded as unverifiable evidence and were not converted into
findings.

## 1. Findings

### candidate-001 — Round-28 fix-up left the header and terminal status at Round 27

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:7` and `:2413`–`:2415`
- **Claim:** The Round-28 fix-up did not synchronize the document's current revision metadata and
  closing status with the extant §0.31 addendum.
- **Evidence:** The header still says “Last revised: 2026-08-04 (§0.30)”
  (`docs/phase7/v1/PHASE_7_DOC.md:7`), while the document contains a later “0.31 Round-28 fix-up”
  addendum that says Round 28 changed §5 and synchronized the closing status
  (`docs/phase7/v1/PHASE_7_DOC.md:268`–`:271`). The terminal status nevertheless says only that
  corrections through Round 27 are applied and that Round 27 most recently changed binding §5
  (`docs/phase7/v1/PHASE_7_DOC.md:2413`–`:2415`). The document correctly remains marked unverified,
  but these two current-status fields directly contradict §0.31.
- **Required correction:** Change the header's latest revision marker from §0.30 to §0.31. Update
  the terminal status to say that corrections through Round 28 are applied and Round 28 most
  recently changed binding §5, while retaining the statement that v1 remains unverified pending a
  fresh whole-document review and that no version roll occurs until the loop exits.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the correction synchronizes metadata outside
  the manifest-declared §5 region and does not alter any cross-phase contract.

## 2. Checked and clean

The finder-reported clean areas survived independent re-derivation. The Round-28 Phase 6
registry-generation adoption repair is coherent: runtime creation uses the current published
generation, Phase 4 acceptance is followed by authoritative generation reacquisition and adoption,
`ADOPTED` and `ALREADY_CURRENT` may proceed, and retired-generation rejection enters recovery
before activation. Phase 4's detached candidate view remains generationless. No additional
interface-honesty defect was established in the selected Phase 2–6 inventories, coordinated
publication/rollback protocol, or Phase 8/9 hand-offs.

The conformance map continues to provide zero-unmapped coverage for the in-scope frame flow,
Appendix A.1 program inventory, hook needs 1–11, corrected split injection timeline, and assigned
engine flags. The substantive target remains internally consistent apart from candidate-001's
stale metadata. Prior reviews do not settle or clear that defect: Round 28's resolution claims the
terminal status was synchronized, but the current post-fix-up bytes still contain the Round-27
wording and the header still names §0.30.

No surviving candidate was refuted or cleared on re-derivation. Gate-dropped candidate-002 and
candidate-003 remain excluded for the unresolved citations stated in §0 and are not counted.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted metadata defect is a bounded correction and does not require structural rebuilding or
an interface change. Relative to Round 28, corrections fall from two to one, but literal convergence
has not been reached because one correction remains; the result cannot be softened to PASS. The
next required action is a scoped fix-up of candidate-001, including this review's `## Resolutions`
record and a new target addendum. Because this correction itself does not touch §5, it does not
independently trigger interface reverification; after fix-up, the canonical loop must determine the
next review obligation from the resulting document state. No version roll may occur while that loop
remains open.

## Resolutions

### candidate-001 — resolved

Re-derived from the target's §0 sequence and terminal status. Updated the header to name §0.32,
the newly required compact Round-29 addendum, rather than leaving it at either stale §0.30 or the
now-penultimate §0.31. Updated the terminal status to state that corrections through Round 28 are
applied and that Round 28 most recently changed binding §5. The existing unverified-pending-review
and no-version-roll statements remain intact.

This fix changes only header/addendum/terminal metadata. It does not alter the manifest-declared
§5 cross-phase interface region and therefore does not independently fire its change trigger.

### Notes deferred

None.
