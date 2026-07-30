# Phase 6 verification review — round 6

## 0. Method and reading order

This adjudication first re-derived both surviving candidates against the whole target,
`docs/phase6/v1/PHASE_6_DOC.md`; the governing Part I, Phase 6 assignment, document gate, and
mandatory template in `docs/design/v2.0-RC3/DESIGN.md`; the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`; the manifest-selected binding interfaces of Phases 1, 3, and 4;
and the canonical engine behavior cited by the interface-trigger candidate. Supporting evidence
was consulted only where material to the candidates and did not displace the governing authority
or dependency contracts.

Only after settling those interpretations did adjudication read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` through
`docs/phase6/reviews/PHASE_6_REVIEW_5.md`, in numeric order. Their resolutions are present in the
target. There were no reading-order deviations, no network use, and no agent fan-out. Gate dropped
no candidates. Both surviving candidates were independently re-derived rather than accepted from
their incoming labels.

## 1. Findings

### candidate-001 — Round-5 addendum left maintenance metadata stale

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:8`,
  `docs/phase6/v1/PHASE_6_DOC.md:15-18`,
  `docs/phase6/v1/PHASE_6_DOC.md:138-146`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1589-1591`
- **Claim:** The document's governed maintenance metadata and current §G1.3 status do not account
  for the §0.7 Review-5 correction.
- **Evidence:** The header still describes the revision only as “dependency adoption at §0.6”
  (`docs/phase6/v1/PHASE_6_DOC.md:8`). The introductory provenance says
  “§§0.3–0.6 record later governed maintenance”
  (`docs/phase6/v1/PHASE_6_DOC.md:15-18`). The paragraph labeled
  “**Current §G1.3 status:**” says only that Round 4's PASS applies to the pre-§0.6 bytes and that
  the §0.6 addendum awaits review, even though §0.7 immediately follows and records the Review-5
  correction (`docs/phase6/v1/PHASE_6_DOC.md:138-146`). The closing provenance likewise stops at
  §§0.3–0.6 (`docs/phase6/v1/PHASE_6_DOC.md:1589-1591`). Review 5, read last, confirms that its
  admitted correction was resolved and that the resulting bytes require Round 6 verification.
- **Required correction:** Update the header and both maintenance ranges to include §0.7. Replace
  the current-status paragraph with the actual state: Review 5 produced the resolved §0.7
  correction, and those bytes awaited this Round 6 verification. The subsequent resolution record
  may state the state resulting from this review without claiming a PASS that this review does not
  issue.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation:

- The substantive Review-5 correction is internally consistent. Conditional `shadow` selection
  uses the effective Phase 4 `ProgramUniformLayout`; stale Phase 3 water-shadow and world-constant
  consumption is absent.
- The selected Phase 1, Phase 3, and Phase 4 contracts support the dependencies claimed in §5.2.
  No invented dependency API or contradictory ownership was found.
- The Appendix D inventory, cadence and upload rules, smoothing semantics, sampler maps,
  frame-begin ordering, temporal snapshots, matrix capture, center depth, notifier audit, custom
  ordering, barrier trace, provider seam, and test plan remain substantively covered.

Candidate disposition on independent derivation:

- **candidate-002 — dropped.** The mandatory template deliberately assigns exact semantics to §4
  and requires §5 to identify exposed named interfaces and data contracts
  (`docs/design/v2.0-RC3/DESIGN.md:809-813`). Phase 6 §5 does so and normatively identifies the
  exact detailed definitions, including the §4.2 schemas and §4.9 maps
  (`docs/phase6/v1/PHASE_6_DOC.md:1184-1191`). More decisively, the candidate incorrectly treats
  interface-region hashes as the sole fresh-review gate. Both the fix-up-continuation path and the
  ordinary multi-round path mark every fix-up unreviewed until the next review regardless of
  whether an interface hash changed
  (`.agents/skills/verify-loop/scripts/engine.mjs:1724-1740`,
  `.agents/skills/verify-loop/scripts/engine.mjs:2239-2256`). Interface hashes add a specific
  change notice; they do not permit an edit to a referenced §4 contract to close without review.
  Duplicating the detailed schemas in §5 or broadening the manifest is therefore not required.

Reading the prior reviews last did not alter these dispositions. Round 5 confirms the provenance
defect's underlying state transition. Earlier rounds do not establish that exact schemas must be
duplicated physically inside §5, and their resolved interface corrections remain present.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The stale governed status and provenance are a bounded metadata correction, not a structural miss,
so `FAIL` is not warranted. Literal `PASS` is unavailable while that correction remains.

The next required action is a fix-up resolving candidate-001 and appending its resolution to this
review. The correction does not require a change to the manifest-selected
`cross-phase-interfaces` region. As with every fix-up, however, the resulting bytes are unreviewed
until a fresh verification round returns literal PASS.

Trend: Rounds 1–3 reported 2, 2, and 3 corrections; Round 4 reported zero; Round 5 reported one
interface correction; and Round 6 reports one non-interface metadata correction. The substantive
contract has converged, but the loop has not reached literal PASS on the current bytes. The
localized remaining correction does not justify escalation to `FAIL`.

## Resolutions

### candidate-001 — resolved

Re-derived against the target's header, maintenance addenda, closing provenance, and the
verification state established by this review. Updated the header and both maintenance ranges
through §0.8; replaced the stale Round-4/§0.6 status with the actual Review-5 → Round-6 transition;
and added compact §0.8 recording this metadata correction. The resulting status does not claim
literal PASS: these fix-up bytes remain unverified until a fresh review returns it. No substantive
contract or §5 cross-phase interface changed.

### Notes deferred

None.
