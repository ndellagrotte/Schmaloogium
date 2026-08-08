# Phase 7 verification review — Round 26

## 0. Method and reading order

I first re-derived every candidate from the complete target at
`docs/phase7/v1/PHASE_7_DOC.md`, the manifest-selected `docs/design/v3/DESIGN.md` Part I,
Phase 7 specification, document gate, and mandatory template, the relevant
`docs/research/v1/RESEARCH.md` contract text, and the binding §5 regions of Phases 2–6. The v3
design selection was the supplied verification-only override; the target's own historical
governing-revision declaration was not treated as rewritten. I then checked the supplied supporting
evidence where relevant. Only after settling the candidates independently did I read discovered
prior reviews 1–25, in order, with Round 25 last.

There was no network use, no agent fan-out, and no reading of a forbidden transcript,
`docs/**/chatlogs/**`, or root `*.txt`. The already-dispatched atomic adjudication role was completed
directly; neither `$verify-loop` nor `scripts/verify` was invoked. The supplied Gate reported no
drops. Candidates 003 and 004 were independently re-derived but dropped as exact duplicates of the
same schema-version defect admitted under candidate-001; they add no distinct location, claim,
remedy, severity, or interface consequence.

## 1. Findings

### candidate-001 — Phase 7 consumes schema-v2 after Phase 3 moved `IdMappingInput` to schema-v3

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1891` and `:1953`
- **Claim:** Phase 7's binding dependency inventory and candidate-publication protocol identify an
  obsolete, rejected version of the Phase 3 ID-mapping input.
- **Evidence:** The inventory calls the consumed contract “schema-v2 `IdMappingInput`”
  (`docs/phase7/v1/PHASE_7_DOC.md:1891`), and publication step 1 requires validation of its
  “schema-v2 ID input” (`docs/phase7/v1/PHASE_7_DOC.md:1953`). Phase 3's binding inventory instead
  exposes the type as schema-v3 (`docs/phase3/v1/PHASE_3_DOC.md:1417`). Its version discipline fixes
  `PackFrontEnd.CURRENT_SCHEMA_VERSION` at 3 (`docs/phase3/v1/PHASE_3_DOC.md:1593`–`:1595`), requires
  the nested version to equal the containing configuration, and expressly rejects versions 1 and 2
  (`docs/phase3/v1/PHASE_3_DOC.md:1604`–`:1606`). Phase 7's earlier symbolic current-version check
  (`docs/phase7/v1/PHASE_7_DOC.md:626`–`:628`) therefore conflicts with, rather than cures, the two
  later normative literals.
- **Required correction:** Replace both schema-v2 requirements with schema-v3, or consistently bind
  them to Phase 3's authoritative current/nested schema-version rule, while preserving Phase 7's
  validate-and-forward-without-parsing behavior.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — both contradictory requirements are inside the
  manifest-declared §5 cross-phase interface region, so its fresh-review trigger applies.

### candidate-002 — Header revision metadata remains one addendum behind

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:7`
- **Claim:** The document's singular `Last revised` marker does not identify its latest fix-up.
- **Evidence:** The header says `Last revised: 2026-08-03 (§0.27)`
  (`docs/phase7/v1/PHASE_7_DOC.md:7`), but the document contains the later `0.28 Round-25 fix-up`
  (`docs/phase7/v1/PHASE_7_DOC.md:252`–`:255`). The marker is therefore internally stale even
  though the mandatory template does not independently require that optional parenthetical.
- **Required correction:** Advance the parenthetical from §0.27 to §0.28, updating the date only if
  the repository's revision-date convention requires it.
- **Severity:** correction
- **touches interface/change-trigger region: no** — this is header metadata outside §5.

### candidate-005 — The lifecycle conformance citation stops before the resolution trigger

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:471`
- **Claim:** The conformance row's authoritative citation does not support the complete set of
  uninit/reinit triggers that the row claims to map.
- **Evidence:** The row maps “dimension cache and all uninit/reinit triggers,” expressly including
  resolution changes, but cites only `docs/research/v1/RESEARCH.md:483`–`:489`
  (`docs/phase7/v1/PHASE_7_DOC.md:471`). RESEARCH places the resolution-multiplier trigger on line
  490 (`docs/research/v1/RESEARCH.md:486`–`:490`). Section 4.8 substantively implements the trigger
  (`docs/phase7/v1/PHASE_7_DOC.md:976`–`:978`), which limits this to a provenance correction but
  does not repair this row's incomplete citation under the mandatory conformance-map rule
  (`docs/design/v3/DESIGN.md:831`–`:833`).
- **Required correction:** Extend the RESEARCH citation through line 490 while retaining the
  separate provenance for the additional Phase 9 identity triggers.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the correction is confined to §3's conformance
  map.

## 2. Checked and clean

The new-surface pass found the corrected Phase 2, Phase 4, Phase 5, and Phase 6 dependency ranges
faithful to their current binding rows. Phase 3's compact cited range is also contiguous and
correct; its sole surviving problem is Phase 7's stale schema-v2 wording. The interface pass found
no other contract-honesty defect in §5.1–§5.5 or the binding dependency regions. The conformance
pass found the frame-flow map, complete Appendix-A.1 inventory, hook-needs 1–11, seven-row timeline
including the split camera anchor, engine flags, v0.1 assembly narrative, and OQ-3/OQ-4 spikes
otherwise covered.

Candidates 003 and 004 were cleared as duplicative presentations of candidate-001. All three cite
the same two target occurrences, the same Phase 3 schema-v3 contract, require the same correction,
and trigger the same §5 review consequence; counting them separately would triple-count one defect.
Prior reviews contain no settled disposition that clears any of the three distinct admitted
findings on the current post-Round-25 bytes.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

The three admitted defects are bounded corrections, not a structural miss requiring rebuild.
Relative to Round 25's one correction, the count rises to three because the corrected Phase 3
provenance exposed a semantic schema mismatch and the fresh surface also revealed two independent
document-consistency defects. Convergence has therefore not been restored and cannot be softened to
PASS. The next required action is a scoped fix-up of candidate-001, candidate-002, and
candidate-005, with a `## Resolutions` record and a new target addendum. Because candidate-001
changes §5, a fresh whole-document and interface verification round is required before Phase 7 can
close.

## Resolutions

### candidate-001 — applied

Re-derived from Phase 3's binding version discipline at
`docs/phase3/v1/PHASE_3_DOC.md:1591`–`:1606`: `PackFrontEnd.CURRENT_SCHEMA_VERSION` is 3,
the nested `IdMappingInput.schemaVersion` must equal its containing configuration, and versions 1
and 2 are rejected. Both stale schema-v2 literals in Phase 7 were changed to schema-v3. The
validate-and-forward-without-parsing behavior is unchanged. These edits intentionally change the
manifest-declared §5 interface region, so a fresh verification round is required before closure.

### candidate-002 — applied

The header's `Last revised` marker now names the latest addendum, §0.29, and uses the fix-up date
2026-08-04. A compact §0.29 addendum records this round without importing another phase's
bookkeeping convention.

### candidate-005 — applied

The §3.1 lifecycle row's RESEARCH citation now extends through line 490, which includes the
resolution-multiplier uninit trigger. Its separate Phase 9 provenance remains unchanged.

### Notes deferred

None. The adjudicator admitted no notes, and all three corrections were applicable without a new
design decision or conflict with authority.
