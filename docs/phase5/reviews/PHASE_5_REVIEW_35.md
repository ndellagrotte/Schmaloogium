# Phase 5 Verification Review — Round 35

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates before consulting prior reviews. I read
the complete Phase 5 target selected by the manifest, with focused rechecks of the §0 input ledger,
revision history, mandatory thirteen-section structure, recent clear-protocol repair, and the
manifest-declared §5 interface region. I compared those surfaces with the RC3 Part I boundary,
mandatory template, Phase 5 specification and document gate, the current `docs/MOVES.md` path
manifest, the authoritative research contract, and the selected binding §5 regions of Phases 1,
3, and 4. No supporting implementation evidence was needed because both surviving candidates
concern target-local input metadata and governing-document coordinates.

Only after settling those interpretations did I read Phase 5 reviews 1 through 34 and their
resolutions, last. No prior review settled or intentionally preserved either the now-stale
collision count or the overbroad Part I coordinate. Rounds 33 and 34 confirm that the recent
`clearPlan(ClearRequest)` interface repair and latest-revision pointer are now consistent, but
they do not clear the two current defects.

I used no network access, forbidden source, or prior-session transcript. In particular, I did not
open any source matching the forbidden `*.txt` pattern. There was no agent fan-out or delegation.
In accordance with the dispatched atomic-role instruction and the verify-loop skill, I did not
invoke the loop, run `scripts/verify`, or start another Codex session. There were no deviations
from the resolved reading contract. Gate dropped candidate-003 before adjudication because its
finder quote did not resolve; it was not eligible to become a finding.

## 1. Findings

### candidate-001 — Input ledger understates the current `DESIGN.md` collision count

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:27`

**Claim.** The §0 input ledger makes a current factual claim about `docs/MOVES.md`, but still says
that manifest warns of four colliding `DESIGN.md` basenames after addition of the fifth design
revision.

**Evidence.** The target describes `docs/MOVES.md` as the *"Path/version manifest and the
four-`DESIGN.md` collision warning"* (`docs/phase5/v1/PHASE_5_DOC.md:27`). The manifest now says,
*"five files are now named `DESIGN.md`"* and records that the fifth joined on 2026-08-03
(`docs/MOVES.md:15`–`:16`). The target contains no equivalent updated collision-count statement.
Because §G9 requires the header to identify inputs actually read, this is a concrete stale fact in
required provenance metadata, not merely optional historical commentary
(`docs/design/v2.0-RC3/DESIGN.md:790`–`:799`).

**Required correction.** Replace `four-\`DESIGN.md\`` with `five \`DESIGN.md\`` in the input
ledger and record the correction in the next required fix-up addendum.

**Severity:** correction

**touches interface/change-trigger region: no**

### candidate-002 — “All of Part I” coordinate extends into Part II

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:28`

**Claim.** The §0 input ledger labels RC3 lines 92–1119 as “All of Part I,” although Part I ends
at line 1109 and lines 1110–1119 are Part II material.

**Evidence.** The target declares *"All of Part I, §G0–§G12
(`docs/design/v2.0-RC3/DESIGN.md:92`–`:1119`)"*
(`docs/phase5/v1/PHASE_5_DOC.md:28`). RC3's Part I begins at line 92; its closing separator is at
line 1108, and `## Part II — Phase specifications` begins at line 1110
(`docs/design/v2.0-RC3/DESIGN.md:92`–`:95`, `docs/design/v2.0-RC3/DESIGN.md:1108`–`:1116`). The
validated Part I selector consequently ends at line 1109. The stated range is objectively
overbroad and mislabels ten lines of Part II as Part I.

**Required correction.** Replace the Part I coordinate with
`docs/design/v2.0-RC3/DESIGN.md:92`–`:1109`. If lines 1110–1119 were separately read, disclose
that Part II preamble as a separate read rather than including it in the Part I range.

**Severity:** correction

**touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The §0.36 pointer identifies the
newest addendum and accurately records Round 34's header-only repair. The Round-33
`clearPlan(ClearRequest)` correction remains consistent among `BufferEstateView`, the detailed
clear protocol, binding §5, and the implementation checklist. The binding §5 region consistently
represents the consumed Phase 1, Phase 3, and Phase 4 contracts, including the shadow, mipmap,
neutralization, clear, publication, resize, and fixed-unit surfaces. The mandatory thirteen
sections are present. The conformance map and mapped design remain complete for the in-scope
Appendix B.1, B.2, B.3, and B.4 requirements, including the unit-11 ruling, flip and clear
behavior, depth/shadow contents, formats, fallback, sizing, and lifecycle.

Neither surviving candidate clears on re-derivation. Candidate-001 is the target's only current
collision-count statement, and the present-tense input ledger cannot be read as a frozen historical
description of an older `docs/MOVES.md`. Candidate-002 cannot be cured by the possibility that the
author also read the Part II preamble: the defect is the explicit classification of lines
1110–1119 as Part I. Both repairs are localized metadata corrections outside the interface
change-trigger region. No candidate was dropped during adjudication. Candidate-003 remains only a
Gate drop for unverifiable evidence and contributes no finding or count.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: no

Both admitted defects are localized input-ledger corrections and do not require structural
rebuilding, so `FAIL` is not warranted. Neither correction touches the manifest-declared §5
cross-phase interface region.

Rounds 33 and 34 each reported one correction. Their fix-ups are clean on this round's
re-derivation, but two newly exposed metadata defects remain, so literal convergence has not been
reached. The next required action is a scoped fix-up resolving candidate-001 and candidate-002,
appending their resolutions to this review, and adding the required next §0 addendum. Because the
ordered corrections do not touch an interface change-trigger region, this review does not itself
create an interface-triggered re-verification obligation; closure still requires the normal
post-fix-up verification outcome prescribed by the loop.

## Resolutions

### candidate-001 — resolved

Updated the §0.1 `docs/MOVES.md` ledger row from four colliding `DESIGN.md` basenames to five. This
matches the manifest's current statement at `docs/MOVES.md:15`–`:16`; the correction is provenance
metadata only and does not alter a Phase 5 design or interface contract.

### candidate-002 — resolved

Changed the §0.1 RC3 Part I range endpoint from line 1119 to the validated selector endpoint at
line 1109. The separately listed Phase 5 assignment remains the only disclosed Part II material;
the ledger does not claim a separate read of the Part II preamble.

### Fix-up record

Advanced the header's latest-addendum pointer to §0.37 and added the compact §0.37 Round-35
fix-up entry. No §5 cross-phase-interface text changed.

### Notes deferred

None. The adjudicator admitted no notes.
