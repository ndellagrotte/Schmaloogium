# Schmaloogium — Phase 2: Conformance harness — Review Round 12

## 0. Method and reading order

I independently re-derived all three gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, especially its conformance and milestone requirements.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially §5.
4. The supporting CI workflows under `.github/workflows/`.
5. The complete target, `docs/phase2/v1/PHASE_2_DOC.md`.
6. Only after settling every candidate, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
   `docs/phase2/reviews/PHASE_2_REVIEW_11.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents, agent fan-out, or nested verification run. The canonical
engine supplied the finder, refuter, steelman, and Gate material. The Gate dropped candidate-003
because its quoted evidence did not resolve uniquely; it was not eligible for adjudication.
Forbidden sources were not read.

The prior reviews do not settle the two admitted defects. Round 11 introduced the content-addressed
manual-attestation locator, and candidate-001 tests its newly added trust boundary. Candidate-002
tests OQ-10 criteria not previously corrected. Round 11 does settle the interpretation behind
candidate-004: every tier retains mandatory non-manual `PRIMARY` evidence even when an additional
`MANUAL` record omits its own manifest fields.

## 1. Findings

### candidate-001 — Attestation containment trusts an undefined artifact-directory root

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:533–548`, with test coverage at
  `docs/phase2/v1/PHASE_2_DOC.md:1613`.
- **Claim:** The content-addressed manual-attestation locator prevents a ledger row from resolving
  evidence outside the intended run-output estate.
- **Evidence:** The locator requires the attestation leaf to be a regular file at
  `<artifact-directory>/attestations/<attestationSha256>.attestation`, resolves the normalized
  relative suffix beneath the artifact directory without following links, and validates its hash
  (`docs/phase2/v1/PHASE_2_DOC.md:533–548`). But the ledger itself supplies “artifact directory,”
  and the target never anchors that field beneath a separately trusted root or constrains and
  resolves each of its directory components. The general deterministic-artifact rule prohibits
  absolute paths but does not define a base or reject traversal
  (`docs/phase2/v1/PHASE_2_DOC.md:443–447`). `TierLedgerTest` covers missing, mismatched, linked,
  and non-regular attestation leaves, but not absolute, traversing, or symlinked artifact-directory
  values (`docs/phase2/v1/PHASE_2_DOC.md:1613`). Thus the leaf checks can all pass after a ledger
  row redirects lookup outside the intended output estate.
- **Disposition:** Define the ledger artifact directory as a normalized relative path beneath a
  named trusted run-output root; reject absolute paths and empty, `.` and `..` components; resolve
  every directory component without following links. Add `TierLedgerTest` cases for absolute
  paths, traversal, and symlinked directory components.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-002 — OQ-10 can pass while its real-pack smoke records GL errors

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:1795–1812`, repeated in the checklist at
  `docs/phase2/v1/PHASE_2_DOC.md:2039`.
- **Claim:** Stage D's zero-`GLError` assertion participates in the OQ-10 success/failure decision.
- **Evidence:** Stage D compiles the internal default pack through `Lwjgl3GLDevice`, asserts zero
  recorded `GLError`s, and says this is what makes a CI GL tier worth having
  (`docs/phase2/v1/PHASE_2_DOC.md:1795–1797`). The exhaustive “Success requires all of” table lists
  only S1–S5, and the failure list omits Stage D and recorded GL errors
  (`docs/phase2/v1/PHASE_2_DOC.md:1801–1812`). Checklist item 21 likewise gates the recorded outcome
  only on S1–S5 (`docs/phase2/v1/PHASE_2_DOC.md:2039`). S2's synthetic GLSL-120 pair does not cover
  default-pack compilation, and S5 does not define a Stage D failure as a non-green run. The
  governing Phase 2 specification requires success/failure criteria for the spike
  (`docs/design/v1.1/DESIGN.md:694–696`), so an asserted real-pack failure cannot remain outside
  the decision.
- **Disposition:** Add successful Stage D completion—compiling and linking the internal default
  pack with zero recorded `GLError`s—as an explicit success criterion for the selected successful
  CI configuration, add its inverse to the failure conditions, and update checklist item 21 to
  cite the expanded criteria. Requiring Stage D for every configuration that survives Stage C is
  unnecessary unless cross-configuration comparison is intended.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The new T0 rule is consistent across the addendum, tier predicate, run-manifest schema, §5
restatement, evaluator tests, and decision log: every recorded GL error fails T0, while attribution
remains diagnostic only. The evidence index still requires exactly one non-manual `PRIMARY` record
for every constituent scene and separately permits automated feature-pair and manual evidence.
Phase 2's consumed Phase 1 contracts for module layout, seam constraints, capability-profile
serialization, recording/replay, debug flags, fixture placement, diagnostics, and CI extension
points align with the selected binding contract. The downstream §5 requests remain identified as
requests rather than assumed interfaces.

The conformance sweep otherwise found substantive mappings for the matrix packs, T0–T3 tiers,
harness requirements, scenes, fixture policy, named runs, CI, goldens, OQ-10 fallback, and milestone
exit criteria. No candidate-free finding is added.

Candidate-004 is dropped on independent re-derivation and is also settled by Round 11. Its §11.1
sentence is a tier-level invariant: every constituent scene requires a `PRIMARY` record, and all
non-manual records require a run id and manifest hash
(`docs/phase2/v1/PHASE_2_DOC.md:535–549`). A `MANUAL` record is additional evidence, not a
replacement for those mandatory primary records, so permitting that record's manifest fields to be
empty when no capture applies does not contradict the statement that a recorded tier retains run
and manifest identifiers. Rewriting the decision to suggest manual evidence can replace the
primary evidence would weaken the actual contract. Candidates 001 and 002 survive re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: no

Both admitted defects are bounded fix-up work rather than structural omissions requiring a
rebuild. They affect Phase 2-owned ledger validation and spike criteria outside the declared §5
cross-phase interface region, so the interface change trigger does not fire.

The correction count declined from three in Round 10, to two in Round 11, and remains two in Round
12. The loop is improving from Round 10 but has plateaued rather than converged to literal PASS.
The next required action is a scoped fix-up resolving candidates 001 and 002 and recording their
resolutions in this review, followed by a fresh verification round because corrections remain.

## Resolutions

### candidate-001 — resolved

Re-derived from the ledger lookup and the existing cache layout. `artifact-directory` is now a
normalized relative path beneath the independently resolved trusted `<cache>/runs` root; absolute
paths and empty, `.`, or `..` components are rejected, and every directory component is resolved
without following links. `TierLedgerTest` now explicitly covers absolute paths, traversal, and
symlinked directory components in addition to the existing attestation-leaf cases.

### candidate-002 — resolved

Re-derived from the spike's Stage D assertion and the governing requirement for explicit OQ-10
criteria. S6 now requires the selected successful CI configuration to compile and link the internal
default pack with zero recorded `GLError`s. Its inverse is an explicit failure condition, and
checklist item 21 now gates the outcome on S1–S6.

### Notes deferred

None.
