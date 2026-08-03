# Phase 2 verification review — round 30

## 0. Method and reading order

I first re-derived both supplied candidates from the complete target surface in
`docs/phase2/v1/PHASE_2_DOC.md`, the resolved governing selectors in
`docs/design/v1.1/DESIGN.md`, the contract ground truth in
`docs/research/v1/RESEARCH.md`, the CI evidence under `.github/workflows`, the selected Phase 1
binding contract in `docs/phase1/v14/PHASE_1_DOC.md`, and the resolved interface selector in
`verification/targets/phase-2.json`. Only after settling both independent judgments did I inspect
discovered prior reviews 1–29, last, and compare the candidates with their settled findings and
resolutions.

There were no reading-order deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. The
canonical engine supplied the finder, refuter, steelman, and Gate material. The Gate dropped no
candidates, and there were no pre-adjudication eliminations. Forbidden sources were not read.

Prior review 29 settles the addendum convention and says its fix-up added §0.30 and advanced the
closing marker. Section 0.30 is present, but the marker remains at the round-29-current state, so
that resolution does not clear candidate-001. No prior review considered whether the sole physical
interface selector covers the normative detailed contracts catalogued by §5; repeated statements
that §5 points to substantive detail do not settle candidate-002's change-detection question.

## 1. Findings

### candidate-001 — Round-29 fix-up leaves the closing verification marker one round behind

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:213–215,2266–2268`
- **Claim:** The §0.30 addendum and operative closing marker consistently describe the completed
  round-29 review/fix-up transition and round 30 as the current fresh review.
- **Evidence:** Section 0.30 is headed “Round-29 fix-up” and says it advanced the closing history
  for the round-29 review surface (`docs/phase2/v1/PHASE_2_DOC.md:213–215`). The sole closing marker
  instead says round 28 reviewed §0.28 and required §0.29, while round 29 “is the current fresh
  whole-document review” (`docs/phase2/v1/PHASE_2_DOC.md:2266–2268`). That is the pre-§0.30 state
  and conflicts with this manifest-resolved round-30 review.
- **Disposition:** Admitted. Advance only the closing marker to say that round 29 reviewed the
  §0.29 surface and required the now-applied non-interface §0.30 correction, and that round 30 is
  the current fresh whole-document review. Preserve the unverified and no-version-roll statements.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-002 — The declared interface region excludes the canonical wire contracts it exposes

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:790–806,848–877,1563–1694`;
  `verification/targets/phase-2.json:139–147`
- **Claim:** The manifest-selected §5 range is sufficient to trigger the required dependent-safety
  re-verification whenever a normative consumer-facing Phase 2 contract changes.
- **Evidence:** The governing process says to re-verify if the cross-phase interfaces section
  changes (`docs/design/v1.1/DESIGN.md:157–159`). Section 5 exposes the tier definitions, named-run
  catalogue, scene format, golden format, fixtures, run-manifest wire schema, and capture-plan wire
  schema by pointing consumers to detailed §4 definitions
  (`docs/phase2/v1/PHASE_2_DOC.md:1569–1577`). The implementable capture-plan grammar is normative
  in §4.5.2 (`docs/phase2/v1/PHASE_2_DOC.md:790–806`), and the implementable run-manifest grammar is
  normative in §4.5.4 (`docs/phase2/v1/PHASE_2_DOC.md:848–877`). Yet the target profile's sole
  interface selector physically spans only §5 (`verification/targets/phase-2.json:139–147`). A
  field, cardinality, parser rule, or wire meaning can therefore change while §5's still-accurate
  catalogue bytes remain unchanged. Cross-references provide navigation, not change detection.
- **Disposition:** Admitted. Expand the declared interface/change-trigger coverage to include the
  exact normative detailed-design regions defining exposed contracts, while retaining §5 as the
  inventory and ownership map. Relocating the definitions into §5 is also sound but unnecessary;
  duplicating the schemas is not required.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The selected Phase 1 binding contract supports the explicitly consumed module/seam layout,
`GLCapabilityProfile` serialization and fixtures, recording/replay facilities, replay-aware GL
errors, debug flags, capability probe, logging channel, and CI extension point. Ungranted needs
remain requests. The exposed Phase 2 wire contracts themselves are detailed enough to implement;
candidate-002 concerns the reliability of their configured change trigger, not missing schema
content.

The conformance audit found no unmapped or semantically unsupported in-scope requirement across
the tier machinery, fixture matrix, fixed scenes, harness, milestone gates, OQ-10, or the
before-renderer subset. The mandatory thirteen-section structure and document gate remain intact.
The header and §0.30 agree that the round-29 fix-up was applied, and no equivalent round-30 status
elsewhere cures candidate-001's stale closing marker.

Both supplied candidates survive independent re-derivation and comparison with prior settled
material. Neither was refuted or cleared, the Gate dropped none, and no finding is created from
candidate-free clean areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both defects are bounded fix-up work rather than structural misses requiring a rebuild.
Candidate-001 is a non-interface lifecycle correction. Candidate-002 concerns the declared
interface/change-trigger coverage and therefore carries the interface disposition.

Rounds 26–29 each admitted the same one-round-late lifecycle correction, and round 30 admits it
again; that sequence is not converging. Candidate-002 is newly exposed and independently supported,
so neither the trend nor prior clean statements clear it. Apply both corrections and record their
resolutions in this review. Because interface/change-trigger coverage must change, run a fresh
whole-document verification round before Phase 2 can close or its interface can be consumed as
verified. Do not roll the version while the loop remains open.

## Resolutions

### candidate-001 — resolved

Re-derived from the document's §0.30 addendum and closing marker. The marker was the stale part:
it still described round 29 as current after the round-29 fix-up had produced the §0.30 surface.
The closing marker now states that round 29 reviewed §0.29 and required the applied §0.30
correction, and that round 30 is current. The unverified and no-version-roll statements remain.
A compact §0.31 addendum records this round-30 fix-up.

### candidate-002 — refused with cause

The defect re-derives: the manifest's sole interface selector covers only §5, while §5 exposes
normative wire and harness contracts whose detailed definitions live in §4. Changes confined to
those definitions can evade the configured change trigger. The admitted remedy is therefore a
change to `verification/targets/phase-2.json`, but this atomic role may modify only the Phase 2
document and this review. The manifest is immutable for this session. Relocating or duplicating
large normative definitions merely to fit the existing selector would be a new structural design
choice, would enlarge unreviewed surface, and would not implement the adjudicator's requested
selector expansion. No interface text was changed. A maintainer/orchestrator-authorized manifest
edit remains required before this correction is resolved, followed by the declared fresh review.

### Notes deferred

None; the adjudicator admitted no notes.
