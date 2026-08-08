## 0. Method and reading order

I first re-derived all three Gate-surviving candidates from the current target, the selected v3
governing design, RESEARCH.md, and the manifest-selected Phase 3 binding contract. I then read the
discovered Phase 7 reviews 1–26 last and compared the candidates with settled material. I used no
network access, no subagents or other agent fan-out, and no forbidden source. There were no Gate
drops or pre-adjudication eliminations. The only deviation from a candidate's proposed disposition
is the narrowed remedy for candidate-002: Phase 3 has granted the projection but has not yet passed
the reverification that Phase 7 independently requires.

## 1. Findings

### candidate-001 — Composite-guarantee citation remains off by one line

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:474`
- **Claim:** The §3.1 composite-guarantee row does not cite the authoritative line containing its
  quoted lifecycle guarantee.
- **Evidence:** The row cites `docs/research/v1/RESEARCH.md:565` for “guarantees composites run even
  on early exits” (`docs/phase7/v1/PHASE_7_DOC.md:474`). Line 565 instead ends the preceding
  fullscreen-rendering detail, while “Frame end guarantees composites run even on early exits” is
  at `docs/research/v1/RESEARCH.md:566`. The underlying mechanism is specified; the defect is the
  conformance map's resolvability.
- **Required correction:** Change the provenance coordinate from
  `docs/research/v1/RESEARCH.md:565` to `docs/research/v1/RESEARCH.md:566`.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the correction is confined to §3.1, outside
  the declared §5 interface region.

### candidate-002 — R7-9 is still represented as ungranted after Phase 3 exposed the projection

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1720`–`:1734`, `:2024`–`:2029`, and related
  manifest-production staging
- **Claim:** Phase 7's binding contract and staging prose still describe R7-9 as awaiting a Phase 3
  grant even though Phase 3 now exposes exactly the requested canonical projection. The separate
  reverification precondition, however, remains unsatisfied and must not be removed.
- **Evidence:** Phase 3's binding surface defines `NormalizedPackPath.canonicalString()` as the only
  stable projection, fixes its NFC root-relative slash grammar and exact UTF-8 ordering/hashing,
  and expressly identifies Phase 7 as its consumer (`docs/phase3/v1/PHASE_3_DOC.md:1417`–`:1418`).
  Phase 7 nevertheless calls the producer a “post-R7-9 capability,” says it cannot be used until
  R7-9 “is granted,” and lists R7-9 as a dependency request
  (`docs/phase7/v1/PHASE_7_DOC.md:1720`–`:1734`, `:2024`–`:2029`). Phase 3's current closing status
  also expressly says it is **not verified** pending a fresh whole-document review
  (`docs/phase3/v1/PHASE_3_DOC.md:2051`–`:2053`). Thus the grant representation is stale, but Phase
  7's conjunctive grant-and-reverification production gate is not yet fully satisfied.
- **Required correction:** Update §5 and related staging prose to consume
  `NormalizedPackPath.canonicalString()` as granted and mark R7-9 granted but unavailable for
  implementation until Phase 3 receives its pending literal-PASS whole-document review. Retain the
  manifest/digest implementation gates until that reverification occurs, as well as the exact
  canonical UTF-8, bounded-snapshot, whole-corpus, ordering, digest, and failure semantics.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the stale dependency status and consumption
  language occur in the declared §5 interface region, so its fresh-review trigger applies.

### candidate-003 — Two adjacent §3.1 provenance citations end before their claimed requirements

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:464`–`:465`
- **Claim:** The frame-flow conformance rows' citations do not reach the lines supporting previous
  snapshots and the fixed gbuffers unit map.
- **Evidence:** The world-state row cites `docs/research/v1/RESEARCH.md:533`–`:536` while its
  description also claims previous snapshots, which occur on line 537. The adjacent fixed-gbuffers
  row cites line 537, but the fixed-unit-map statement is on line 538
  (`docs/research/v1/RESEARCH.md:533`–`:538`). Section 4.3 supplies the intended implementation
  ordering, so this is a provenance defect rather than a missing mechanism.
- **Required correction:** Extend the first row's range through line 537 and change the
  fixed-gbuffers row's coordinate to line 538. The first row may instead omit the snapshot wording
  if snapshots are not intended to be part of that mapping.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the correction is confined to §3.1.

## 2. Checked and clean

The new-surface review found the schema-v3 `IdMappingInput` references internally consistent with
Phase 3's current-version discipline, and found §5.3's construction, publication, compensation,
and ownership ordering coherent. The interface review found no new defect in the selected Phase 2,
4, 5, or 6 inventories. The conformance review found no other unmapped or unsupported row: the
Appendix A.1 slots, hook needs 1–11, seven reference-timeline rows, reference-free sky/weather/cloud
flags, assigned engine flags, OQ-3/OQ-4 coverage, and Phase 10/12/13 deferrals remain mapped.

All three candidates survive independent re-derivation. Candidate-002 is admitted only in the
narrowed form above: the Phase 3 grant is present, but prior reviews and Phase 3's current status do
not permit removal of the still-unsatisfied reverification gate. Prior Phase 7 reviews contain no
settled disposition that clears these present-byte defects.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

The three admitted findings are bounded corrections and do not require structural rebuilding.
Relative to Round 26's three corrections, the count remains flat at three, so convergence has not
been restored and the result cannot be softened to PASS. The next required action is a scoped
fix-up of candidate-001, candidate-002, and candidate-003, including this review's
`## Resolutions` record and a new target addendum. Because candidate-002 changes the declared §5
interface region, a fresh whole-document and interface verification round is required before Phase
7 can close.

## Resolutions

### candidate-001 — resolved

Changed the §3.1 composite-guarantee provenance coordinate from
`docs/research/v1/RESEARCH.md:565` to `:566`, where the quoted early-exit guarantee actually
appears. This is outside §5.

### candidate-002 — resolved

Re-derived Phase 3's current binding surface and status: its §5 grants
`NormalizedPackPath.canonicalString()` as the sole stable NFC root-relative slash projection with
exact UTF-8 ordering/hashing, while its closing status still requires a fresh whole-document
review. Phase 7 now marks R7-9 granted throughout §5 and the related test, staging, blocker,
upstream-change, and checklist formulations. It continues to forbid construction or use of the
manifest/digest producer until Phase 3 returns literal PASS, and preserves the exact bounded-
snapshot, whole-corpus, ordering, digest, checked-failure, and no-`toString()` semantics.

This intentionally changes the declared `cross-phase-interfaces` region. A fresh Phase 7 review
is therefore required before Phase 7 can close.

### candidate-003 — resolved

Extended the §3.1 world-state provenance range through
`docs/research/v1/RESEARCH.md:537`, which supplies the previous-frame snapshot claim, and moved the
fixed-gbuffers coordinate to `docs/research/v1/RESEARCH.md:538`, which supplies the fixed-unit-map
claim. This is outside §5.

### Notes deferred

None. The adjudicator admitted no notes.
