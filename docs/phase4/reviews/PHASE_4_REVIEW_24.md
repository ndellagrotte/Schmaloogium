## 0. Method and reading order

This adjudication independently re-derived both surviving candidates against, in order:

1. the whole Phase 4 target, with focused checks of its addenda, Pintonium mechanism
   disposition table, decision log, binding §5 region, and closing verification status;
2. the governing Part I, Phase 4 specification, document gate, mandatory template, and §G11.4
   evidence/adoption rules in `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4160-4265` and
   `docs/phase3/v1/PHASE_3_DOC.md:1300-1478`; and
5. the listed supporting evidence, including the relevant Pintonium design and source evidence.

Only after settling those interpretations were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_23.md` consulted as settled prior material. Those reviews
establish the prior interface and correction history, but none resolves either defect below.
Review 23 corrected the distinct header revision marker and did not update the closing status.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — Closing verification status omits the §0.26 binding-interface correction

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:266-269` and
  `docs/phase4/v1/PHASE_4_DOC.md:2023-2025`
- **Claim:** The operative closing status does not accurately identify all post-PASS binding
  changes responsible for the current fresh-review obligation.
- **Evidence:** Section 0.26 expressly says the Review 22 correction was integrated into §§2–5,
  changed binding §5, and requires a fresh review
  (`docs/phase4/v1/PHASE_4_DOC.md:266-269`). The closing §G1.3 status instead attributes the
  current unverified state to the §0.22 maintenance amendment and §0.23–§0.24 corrections only
  (`docs/phase4/v1/PHASE_4_DOC.md:2023-2025`). Although that status correctly says the document
  is unverified, its enumeration of the changes underlying that state is stale. Review 23's
  settled fix changed only the header's latest-revision marker and does not cure this separate
  closing statement.
- **Required correction:** Update the closing status to include §0.26 among the post-round-18
  binding-§5 changes requiring fresh whole-document verification. Do not modify §5 solely for
  this status correction.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

### candidate-002 — Pintonium disposition rows omit mandatory corresponding PD citations

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:769-779`
- **Claim:** The §3 conformance map records adoption or rejection of Pintonium mechanisms without
  carrying the corresponding row-level PD citations required by the governing template.
- **Evidence:** The mandatory template requires every conformance-map row that adopts or rejects
  a Pintonium mechanism to carry the corresponding PD citation and, for contract-visible items,
  the §G11.4 decision reference (`docs/design/v2.0-RC3/DESIGN.md:804-808`). Section 3.4 is part of
  the target's contract conformance map and expressly adopts recursive fallback, the program-use
  barrier, pass-bundle shapes, and generation-counter invalidation; rejects Pintonium attribute
  numbering and heuristic legacy-compat selection; and records a rendering-phase structural
  comparison (`docs/phase4/v1/PHASE_4_DOC.md:769-777`). None of those rows carries a corresponding
  `PD §n` citation. Existing `D-P4-4`, `D-P4-5`, `D-P4-6`, and `D-P4-8` references satisfy a
  distinct decision requirement and do not replace PD provenance; the general post-table
  reference to PD §§17–18 does not attach the corresponding evidence to each qualifying row.
- **Required correction:** Add a verified corresponding `PD §n` citation directly to every §3.4
  row that adopts or rejects a Pintonium mechanism, and retain or add the applicable `D-P4-k`
  decision reference for every contract-visible disposition. Validate each PD mapping against
  `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` rather than applying citations mechanically;
  a genuinely neutral structural cross-check need not be mislabeled as an adoption or rejection.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

## 2. Checked and clean

- The header correctly identifies §0.27 as the latest revision; §0.27 accurately describes its
  header-only change; the numbered addenda are sequential; and all thirteen mandatory sections
  remain present.
- The complete binding §5 region remains coherent with the detailed registry, candidate-view,
  resolution-projection, barrier, publication, generation, ownership, uniform-layout, and
  optional viewport-scale semantics. The consumed Phase 1 and Phase 3 contracts match their
  manifest-selected binding regions.
- The target maps both the classic G6 and modern-superset stage shapes, every Appendix A.1 catalog
  row including virtual pre-slots, Appendix A.2 fallback semantics, assigned Appendix A.3
  directives, and the compilation, failure, barrier, and generation requirements.
- Neither supplied candidate is refuted, cleared, or dropped on re-derivation. Candidate-001 is
  not cured merely because the omitted §0.26 change is independently documented or because
  earlier changes already sufficed to invalidate round 18. Candidate-002 is not cured by general
  PD inventory references, nearby observed-source citations, or D-P4 decisions because §G9
  separately requires the corresponding PD citation on each qualifying conformance-map row.
- Prior Reviews 1–23 do not settle the stale closing-status enumeration or provide the missing
  row-level PD citations. No additional finding is admitted beyond the supplied candidate set.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: no

Both admitted defects are bounded document corrections. Neither requires rebuilding the Phase 4
architecture or changing the manifest-declared `cross-phase-interfaces` region, so `FAIL` is not
warranted; literal `PASS` is unavailable while two corrections remain.

Rounds 22 and 23 each had one correction, while this round has two. The count has increased and
the artifact has not achieved the required zero-correction convergence round. The issues are
non-interface provenance/status defects, but they still prevent closure.

The next required action is a scoped Phase 4 fix-up resolving candidate-001 and candidate-002,
appending this review's `## Resolutions`, and adding the required compact Phase 4 correction
addendum. The corrections can be confined to closing status, §3.4 provenance/decision references,
and associated revision metadata; they do not themselves trigger the interface-change rule. The
verification loop nevertheless requires a fresh whole-document review after fix-up, and Phase 4
can close only when a round returns literal `PASS` with zero blocking findings and zero
corrections.

## Resolutions

### candidate-001 — resolved

Updated the closing §G1.3 status to enumerate §0.26 with the other post-round-18 binding-§5
changes. The status remains unverified pending a fresh whole-document review. Section 5 itself was
not changed.

### candidate-002 — resolved

Re-derived each §3.4 disposition against PD §§3.1–3.3, §13, §18, and §19.2. Added the
corresponding PD citation directly to every adoption or rejection row and retained or added the
applicable contract-visible decisions: `D-P4-4`, `D-P4-5`, `D-P4-6`, `D-P4-8`, `D-P4-10`, and
`D-P4-11`. The `WorldRenderingPhase` row remains expressly a neutral structural cross-check, with
PD §3.1 supplied as provenance rather than relabeling it as an adoption or rejection.

### Interface disposition

The manifest-declared §5 cross-phase interface region was not changed. The compact §0.28 addendum
records these status/provenance corrections, and the header now identifies §0.28 as latest.

### Notes deferred

None; the adjudication admitted no notes.
