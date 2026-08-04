## 0. Method and reading order

I first independently re-derived both surviving candidates from the complete target,
`docs/phase6/v1/PHASE_6_DOC.md`, then checked the governing Part I, Phase 6 specification,
document gate, and mandatory template in `docs/design/v2.0-RC3/DESIGN.md`, the contract ground
truth in `docs/research/v1/RESEARCH.md`, and the manifest-selected binding contracts of Phases 1,
3, and 4. Listed Pintonium and OptiFine material was treated only as supporting evidence, never as
contract. Focused whole-target searches checked for an equivalent custom-counter failure branch
and for current Phase 1 coverage of the two challenged conformance rows.

Only after reaching independent dispositions did I read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` through
`docs/phase6/reviews/PHASE_6_REVIEW_18.md`, in numeric order, and compare the candidates with
settled material. No prior review settles either current defect. There were no reading-order
deviations, no network use, and no agent fan-out. Per the dispatched atomic-role rule and the
verify-loop skill, I did not invoke `$verify-loop`, `scripts/verify`, or another Codex session. No
forbidden source was read. The Gate reported no drops, and no candidate was eliminated before
adjudication.

## 1. Findings

### candidate-001 — Custom refresh counter mismatch has no defined disposition

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:1228-1233`,
  `docs/phase6/v1/PHASE_6_DOC.md:1303-1316`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1367`
- **Claim:** The Phase 11 custom-refresh boundary requires non-negative counters matching Phase
  6's authoritative sink ledger but does not define executable behavior when the bridge returns a
  negative or mismatched count.
- **Evidence:** The closed result records expose unrestricted `int` counters and provide no
  invalid-result variant (`docs/phase6/v1/PHASE_6_DOC.md:1228-1233`). The detailed contract says
  both counted results must be non-negative and exactly match the authoritative ledger, while also
  assigning count enforcement and diagnostics to Phase 6
  (`docs/phase6/v1/PHASE_6_DOC.md:1303-1316`). It defines accepted-prefix commit behavior for a
  valid `Aborted` result but no branch for a negative or ledger-mismatched result: batch
  commit/discard, surfaced participant degradation, carry-over, and retry behavior are therefore
  indeterminate. The binding §5 row repeats the equality invariant without closing that branch
  (`docs/phase6/v1/PHASE_6_DOC.md:1367`). The failure table has no counter-enforcement case, and
  the headless plan tests only matching `Completed` and `Aborted` counts
  (`docs/phase6/v1/PHASE_6_DOC.md:1455-1472`,
  `docs/phase6/v1/PHASE_6_DOC.md:1564-1566`). Treating equality as merely a Phase 11 precondition
  does not cure the gap because Phase 6 expressly owns enforcement and the public records do not
  enforce their own domain.
- **Required correction:** Define one deterministic Phase 6 disposition for negative or
  ledger-mismatched bridge counters in §§4.13, 5, and 6. State the accepted-batch disposition, the
  diagnostic and Phase 4 participant degradation/result, and carry-over or later-activation retry
  semantics; add a headless mismatch test. A new public result variant is not required if an
  existing closed degradation result is selected explicitly.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-002 — Two conformance-map rows cite unrelated Phase 1 text

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:436` and
  `docs/phase6/v1/PHASE_6_DOC.md:445`
- **Claim:** The P5/P6 ownership and per-uniform upload-isolation rows do not carry provenance that
  supports their mapped requirements.
- **Evidence:** The P5/P6 row cites Phase 1 line 1406
  (`docs/phase6/v1/PHASE_6_DOC.md:436`), but that coordinate is a Phase 7 maintenance-addendum
  heading (`docs/phase1/v14/PHASE_1_DOC.md:1406`). The upload-isolation row cites Phase 1 line
  4119 (`docs/phase6/v1/PHASE_6_DOC.md:445`), which discusses an initialization injection point
  rather than cached replay or attribution (`docs/phase1/v14/PHASE_1_DOC.md:4119-4121`). Current
  equivalent binding coverage exists: Phase 1 line 4206 assigns texture backing to Phase 5 and
  sampler pointing to Phase 6, while lines 4220-4221 specify cached-value replay and its
  replay-aware attribution result (`docs/phase1/v14/PHASE_1_DOC.md:4206`,
  `docs/phase1/v14/PHASE_1_DOC.md:4220-4221`). Equivalent architecture elsewhere does not cure
  false coordinates in the mandatory conformance map, whose rows require provenance
  (`docs/design/v2.0-RC3/DESIGN.md:804-806`).
- **Required correction:** Update only the two §3 citations: point the P5/P6 split row to
  `docs/phase1/v14/PHASE_1_DOC.md:4206` and the upload-isolation row to
  `docs/phase1/v14/PHASE_1_DOC.md:4220-4221`.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The §0.18 additions are otherwise
propagated consistently through the custom schema, boolean command, submission outcomes, binding
interfaces, failure behavior, tests, staging, decisions, handoffs, and checklist. Names, outcome
variants, conservative exclusions, ordering, and valid `Completed`/`Aborted` prefix semantics
agree across those surfaces.

The dependency and interface sweep found the frame-begin, sampler, built-in, provider,
generation-adoption, fixed-schema, boolean-encoding, active-layout absence, and location-absence
contracts otherwise implementable and consistent with the selected Phase 1, 3, and 4 binding
surfaces. The conformance sweep found no unmapped Appendix D uniform, cadence rule, smoothing
decision, center-depth decision, notifier audit, provider seam, or sampler-map requirement. Aside
from candidate-002's two stale coordinates, the spot-checked authoritative mappings were
semantically supported.

Neither candidate was refuted, cleared, subsumed, or dropped on independent derivation. Reading
Reviews 1-18 last found that all earlier corrections carry resolutions and that Round 18 passed
the then-reviewed bytes; it contains no settled disposition for the newly added custom-counter
contract or the presently stale Phase 1 coordinates. There were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded corrections rather than structural misses requiring a rebuild, so
`FAIL` is not warranted. Literal `PASS` is unavailable while two corrections remain.
Candidate-001 requires changing the manifest-selected `cross-phase-interfaces` region;
candidate-002 does not. The next required action is a governed fix-up resolving both candidates
and appending resolutions to this review, followed by a fresh verification round before Phase 6
can close because the interface change trigger applies.

Trend: Round 18 reached literal `PASS` for its reviewed bytes. The subsequent §0.18 custom-uniform
surface and shifted Phase 1 dependency coordinates expose two new corrections in Round 19, one of
them interface-affecting. This is a regression from zero rather than continued convergence, but
both defects remain locally fixable and do not justify escalation to `FAIL`.

## Resolutions

### candidate-001 — resolved

Re-derived against Phase 4's closed `BarrierParticipantResult` contract and the surrounding Phase
6 batch lifecycle. Sections 4.13, 5, 6, 8, and 12 now define one invalid-counter branch for either
counted bridge result: any negative or sink-ledger-mismatched counter emits a stable
counter-contract diagnostic, discards the whole accepted batch with no GL, and returns
`BarrierParticipantResult.Degraded(diagnosticId, "custom uniforms for this activation")`. Nothing
carries over or retries in the same activation; the next successful shader activation starts a
fresh refresh. This intentionally changes the manifest-selected §5 interface region and therefore
requires the declared fresh verification round before Phase 6 can close. No new public result
variant or design decision was needed.

### candidate-002 — resolved

The two §3 provenance coordinates now point to the current Phase 1 binding-contract text: the
P5/P6 ownership row cites `docs/phase1/v14/PHASE_1_DOC.md:4206`, and upload isolation cites
`docs/phase1/v14/PHASE_1_DOC.md:4220`–`:4221`. No contract prose changed for this correction.

### Notes deferred

None. The adjudicator admitted no notes.
