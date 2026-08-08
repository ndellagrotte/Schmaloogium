# Phase 11 adversarial review — round 2

## 0. Method and reading order

I independently re-derived all three surviving candidates against the complete Phase 11 target,
the manifest-selected v3 governing design, the selected RESEARCH.md authority, the Phase 3 and
Phase 6 binding contracts, and relevant permitted repository evidence. Only after settling those
interpretations did I read `docs/phase11/reviews/PHASE_11_REVIEW_1.md` and its resolutions.

I did not use the network, forbidden transcripts, or forbidden path patterns. I did not invoke the
verification harness, start another Codex process, or use agent fan-out. There were no reading-order
deviations. Gate dropped candidate-004 because its quoted evidence did not resolve; I did not revive
it or create a replacement finding.

## 1. Findings

### candidate-001 — The exclusion contradiction and requested clarification are stale

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:371`–`:378`, repeated at `:1134`–`:1137` and
  `:1159`–`:1161`.
- **Claim:** The decisions and requested changes must accurately report authority-chain conflicts.
- **Evidence:** The target says Appendix F.6 names only five exclusions and that Appendix D.4 adds
  `blendFunc` and `instanceId`, then requests clarification. Current Appendix F.6 expressly excludes
  all five D.4 dynamics plus `fogMode` and `fogColor`, and says it neither overrides nor narrows D.4
  (`docs/research/v1/RESEARCH.md:1501`–`:1505`). RESEARCH is controlling authority
  (`docs/design/v3/DESIGN.md:136`–`:143`). The real reportable conflict is that the Phase 11 design
  row still restates only five names (`docs/design/v3/DESIGN.md:2300`–`:2303`).
- **Severity:** correction. Preserve the correct seven-name behavior, but revise §3.3, D-P11-9,
  and §11 to attribute it directly to Appendix F.6 and report/request correction of the stale
  DESIGN restatement rather than clarification of RESEARCH.
- **Touches interface/change-trigger region:** no.

### candidate-002 — The maintainer handoff requests setup already completed

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:1145`–`:1147`, `:1162`–`:1164`, and
  `:1172`–`:1175`.
- **Claim:** Status and maintainer handoffs must describe the repository state that is actually
  driving this verification round.
- **Evidence:** The target says the Phase 11 profile and MOVES adoption do not exist and requests
  their creation before a first paid review. `verification/targets/phase-11.json:1`–`:10` is the
  existing target profile, while `docs/MOVES.md:89`–`:92` records both Phase 11's v3 adoption and
  the v3-derived profile. The target itself acknowledges Round 1 and requires a fresh round
  (`docs/phase11/v1/PHASE_11_DOC.md:99`–`:103`).
- **Severity:** correction. Reconcile §0.2, §11.2, §11.4, and §12 with completed profile creation,
  dry-run validation, and MOVES adoption; remove the obsolete pre-first-review actions.
- **Touches interface/change-trigger region:** no.

### candidate-003 — Published consumer APIs still lack implementable callable shapes

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:836`–`:854` and incorporated declarations.
- **Claim:** APIs published to other phases, and expressly incorporated as exact callable shapes,
  must be sufficiently closed for consumers to implement without inventing contracts.
- **Evidence:** Section 5 publishes `CustomExpressionCompiler`, `PlanBuildResult`,
  `CustomExpressionPlan`, `PlanActivationResult`, schema, random, metrics, and backend-facing types
  (`docs/phase11/v1/PHASE_11_DOC.md:836`–`:844`) and claims exact declarations are incorporated
  (`:850`–`:854`). Yet §4.1 declares only compile-request/source records (`:399`–`:416`), while the
  controller returns undefined `PlanActivationResult` and accepts an undefined plan surface
  (`:803`–`:815`). The compiler invocation, plan accessors, build/activation result algebras, and
  several public support shapes are therefore absent or prose-only. These are load-bearing
  cross-phase relationships for which the governing template requires signatures
  (`docs/design/v3/DESIGN.md:829`–`:834`).
- **Severity:** correction. Define the exact consumer-visible compiler invocation, result variants,
  plan access surface, activation result, schema/random/metrics contracts, ownership/nullability,
  failure behavior, and deterministic Phase 3 declaration adaptation (or assign that adapter to a
  named owner with an exact accepted contract). Classify backend support types explicitly as public
  SPI or implementation-private, and synchronize the affected §5 rows.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

The Round-1 addendum, complete conformance map, exclusion behavior, public declarations and
lifecycle, §5 publication and incorporation, Phase 3 declaration projection, Phase 6 schema/value/
bridge contract, decisions, handoffs, checklist, target profile, and MOVES adoption were checked.
Apart from the findings above, the expression-language coverage is complete, the seven-name
exclusion behavior is correct, and Phase 11 accurately consumes Phase 3's ordered duplicate-
preserving declarations and Phase 6's fixed schema, boolean upload, absent-program outcome,
three-counter accounting, accepted-prefix behavior, and built-ins-first cadence.

No surviving candidate was cleared on re-derivation. Candidate-004 remains eliminated by Gate's
deterministic citation failure and was not adjudicated on its merits. Round 1's settled findings
remain resolved; candidate-003 is a narrower second-order defect exposed by Round 1's new promise
of exact incorporated callable shapes, not a reopening of its original incorporation finding.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three defects are localized corrections rather than structural rebuilds. The correction count
decreased from four in Round 1 to three in Round 2, so there is no divergence warning, but literal
PASS has not converged. Candidate-003 requires changing the declared cross-phase interface region.

Next required action: apply a scoped fix-up, append resolutions to this review, and conduct a fresh
Phase 11 verification round because §5 changes before Phase 11 can close.

## Resolutions

### candidate-001 — applied

Re-derived Appendix F.6 at `docs/research/v1/RESEARCH.md:1501`–`:1505`: it directly excludes all
five D.4 dynamics plus both fog inputs and expressly does not narrow D.4. Updated §3.3, D-P11-9,
§11.2, and §11.4 to preserve the seven-name behavior, remove the false RESEARCH contradiction,
and request correction only of DESIGN's stale five-name restatement.

### candidate-002 — applied

Confirmed `verification/targets/phase-11.json` exists and `docs/MOVES.md:89`–`:92` records Phase
11's v3 adoption. Reconciled §0.2, §11.2, §11.4, and §12 with completed target creation, dry-run
preflight, MOVES adoption, Round 1, and the current fresh-round obligation. Removed obsolete
pre-first-review actions.

### candidate-003 — applied

Added exact compiler invocation, closed build and activation results, immutable plan accessors,
the deterministic Phase 3 field-for-field adapter, context schema, random, and metrics contracts,
including nullability, ownership, failure isolation, and atomic rejected-activation behavior.
Classified evaluator graph/build/value/frame/memo types as implementation-private SPI and exposed
only backend semantic selection. Synchronized the §5 rows. This intentionally changes the declared
cross-phase interface region, so a fresh Phase 11 verification round is required before closure.

### Notes deferred

None; the adjudicator admitted no notes.
