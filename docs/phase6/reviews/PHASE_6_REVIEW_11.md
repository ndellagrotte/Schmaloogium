# Phase 6 verification review — round 11

## 0. Method and reading order

This adjudication first independently re-derived both surviving candidates against the whole
target, `docs/phase6/v1/PHASE_6_DOC.md`; the override-selected Part I, Phase 6 assignment,
document gate, and mandatory template in `docs/design/v3/DESIGN.md`; the relevant contract ground
truth in `docs/research/v1/RESEARCH.md`; and the manifest-selected binding interfaces of Phases 1,
3, and 4. Supporting and Pintonium material was treated only as evidence, never as contract.

Only after settling those interpretations did adjudication read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` through
`docs/phase6/reviews/PHASE_6_REVIEW_10.md`, in numeric order. There were no reading-order
deviations, no network use, and no agent fan-out. Gate dropped no candidates, and no candidate was
eliminated before adjudication. Both candidates were independently re-derived rather than accepted
from their incoming labels.

## 1. Findings

### candidate-001 — Review-10 addendum is omitted from maintenance-provenance markers

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:8`,
  `docs/phase6/v1/PHASE_6_DOC.md:15-18`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1635-1637`
- **Claim:** The document's compact maintenance provenance does not account for the present §0.10
  addendum.
- **Evidence:** The revision field says maintenance extends only through §0.9
  (`docs/phase6/v1/PHASE_6_DOC.md:8`). The opening provenance summary likewise says that
  §§0.3–0.9 record later governed maintenance (`docs/phase6/v1/PHASE_6_DOC.md:15-18`), and the
  closing marker repeats that same range (`docs/phase6/v1/PHASE_6_DOC.md:1635-1637`). Section 0.10
  is nevertheless present and records the Review-10 corrections
  (`docs/phase6/v1/PHASE_6_DOC.md:158-161`). All three current-extent claims are therefore one
  addendum behind the document bytes they describe.
- **Required correction:** Update the revision field to maintenance through §0.10 and both compact
  provenance ranges to §§0.3–0.10.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation:

- Borrowed-provider retention is consistent across construction, custom-bridge lifecycle, reset
  detail, tests, and §5: non-close transitions retain providers and terminal close releases them.
- GL-context loss is consistently included in the lifecycle inventory and detailed reset scopes.
  Equality-only generation comparisons remain coherent.
- The selected Phase 1, Phase 3, and Phase 4 binding contracts support the target's declared
  consumption, and §5 remains an implementable and honest cross-phase surface.
- The complete Appendix D mapping, fixed sampler map, cadence behavior, smoothing formulas,
  center-depth decision, notifier audit, frame ordering, temporal behavior, barrier trace,
  provider seam, and conformance tests remain covered.

Candidate disposition on independent derivation:

- **candidate-002 — dropped.** The diagram labels the transition `RESET`; it does not say that
  every reset reason is recoverable or that adoption is legal afterward
  (`docs/phase6/v1/PHASE_6_DOC.md:437-448`). The detailed lifecycle contract expressly separates
  the three generation-adoption transitions, world-epoch reset, and permanently terminal close,
  including the rule that close cannot be followed by adoption
  (`docs/phase6/v1/PHASE_6_DOC.md:1190-1210`). Section 5 exports the same non-close-versus-close
  distinction, and the tests exercise close release separately from recoverable reset/adoption
  scopes (`docs/phase6/v1/PHASE_6_DOC.md:1226-1227`,
  `docs/phase6/v1/PHASE_6_DOC.md:1424-1426`). Thus `RESET` is an umbrella operation/reason in this
  summary, not a promise of one ordinary recoverable state. A separate `CLOSED` node could improve
  presentation, but its absence is not an architectural defect.

Reading the prior reviews last did not change candidate-001. Round 10 itself treated stale compact
maintenance ranges as a correction, and its resolution created the present §0.10 addendum without
advancing all three markers. Prior reviews did confirm the disposition of candidate-002: Rounds 4
and 8 settled that close is the documented terminal reset and that a separate lifecycle operation
or representation is not required. Round 10 corrected the diagram's objectively incomplete reset
inventory, but did not establish that every reason requires a distinct state node.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The stale maintenance provenance is a bounded correction rather than a structural miss, so `FAIL`
is not warranted. Literal `PASS` is unavailable while that correction remains.

The next required action is a fix-up resolving candidate-001 and appending its resolution to this
review. It does not require changing the manifest-selected `cross-phase-interfaces` region. The
resulting target bytes remain unreviewed until a fresh verification round returns literal PASS.

Trend: Round 9 reported one interface correction, Round 10 reported three non-interface
corrections, and Round 11 reports one non-interface provenance correction. The substantive runtime
contract is stable, but repeated addendum-provenance drift is delaying closure. This is a localized
convergence problem, not grounds for structural escalation; the fix-up should update every compact
current-extent marker together before the next fresh review.

## Resolutions

### candidate-001 — resolved

Re-derived from the target's present section structure, all three compact extent markers were one
step behind §0.10. The header revision field, opening provenance range, and closing provenance
range now advance together. Because this fix-up must also record a compact next-numbered target
addendum, their final extent is §0.11 rather than the pre-fix §0.10 endpoint stated in the finding;
this avoids making the same markers stale by the act of resolving them.

No runtime, dependency, or cross-phase-interface contract changed. The manifest-selected
`cross-phase-interfaces` region was intentionally left untouched. There were no notes to defer and
no correction was refused.
