# Schmaloogium — Phase 2: Conformance harness — Review Round 19

## 0. Method and reading order

I independently re-derived the sole gated candidate before reading any prior review. I read the
manifest-selected target, `docs/phase2/v1/PHASE_2_DOC.md`; the governing Part I, Phase 2 target
specification, document gate, and mandatory template in `docs/design/v1.1/DESIGN.md`; the contract
ground truth in `docs/research/v1/RESEARCH.md`; the selected binding dependency in
`docs/phase1/v14/PHASE_1_DOC.md`; and the supporting CI evidence under `.github/workflows/`. Only
after settling the candidate did I read `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
`PHASE_2_REVIEW_18.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. Forbidden
sources were not read. The Gate reported no drops, and no candidate was eliminated before
adjudication.

The prior reviews were considered only after independent judgment. Round 18 settled the missing
Phase 1 consumer grant by removing the diagnostic domain types from §5.2 and adding unaccepted
request R4B. It did not settle the stale neighboring run-manifest definition in §4.5.4, which still
names `EngineDiagnostic` as the source of every diagnostic record.

## 1. Findings

### candidate-001 — Run-manifest definition still claims the ungranted `EngineDiagnostic` consumption

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:820`
- **Claim:** The round-18 fix-up consistently removed Phase 2's reliance on Phase 1 diagnostic
  domain types pending acceptance of R4B.
- **Evidence:** The run-manifest table still defines `diagnostics` as “every `EngineDiagnostic`
  emitted during the run, by severity and channel” (`docs/phase2/v1/PHASE_2_DOC.md:820`). The
  target's R4B request expressly says that, until Phase 1 accepts the request, Phase 2 does not
  consume `EngineDiagnostic`, `DiagnosticSeverity`, or `UserChannel`, or treat those types as the
  implementation source of its Phase-2-owned `diagnostics{code,severity,channel,file,line}` wire
  records (`docs/phase2/v1/PHASE_2_DOC.md:1599–1600`). Phase 1's binding conventions table grants
  those types only to Phases 3, 4, 5, 6, 7, 11, and 12, while adjacent rows explicitly grant common
  contracts to all phases or name Phase 2 where intended
  (`docs/phase1/v14/PHASE_1_DOC.md:4248–4251`). The line-820 definition therefore retains the
  consumption that the current dependency boundary denies.
- **Disposition:** Admitted. Rewrite the §4.5.4 `diagnostics` entry as Phase-2-owned wire records
  with `code`, `severity`, `channel`, `file`, and `line`; if producer provenance is stated, assign
  the mapping/production responsibility without naming `EngineDiagnostic` as a Phase 2 input until
  R4B is accepted.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported closing history is consistent with §0.18–§0.19. R4B itself accurately states
the selected Phase 1 consumer grant, and no other examined target occurrence claims Phase 2
consumption of `DiagnosticSeverity` or `UserChannel`. The interface audit found no additional
defect: accepted Phase 1 consumptions are present in the selected binding contract, pending
requests remain explicitly unaccepted, and the detailed downstream schemas support the §5
promises. The conformance audit found the Phase 2 objective and scope, T0–T3 machinery, fixture and
golden policies, initial scene families, capture and diff procedures, §9 exit-criterion mapping,
before-renderer subset, and OQ-10 spike backed by substantive design and tests.

The sole candidate survives independent re-derivation. The line-820 wording is not harmless
provenance shorthand: it normatively defines the manifest block through an ungranted domain type,
while the current §5 request explicitly forbids treating that type as the implementation source.
Round 18 corrected the binding consumption claim but did not correct this separate §4 definition.
No candidate was cleared, the Gate dropped none, and no finding is created from candidate-free
clean areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted defect is a localized consistency correction rather than a structural miss. It is
outside the declared cross-phase interface/change-trigger region, and the intended Phase-2-owned
wire schema and pending R4B ownership boundary are already specified.

Rounds 15 and 16 reached PASS, but later interface amendments reopened verification. Round 18's
fix-up removed the ungranted §5 consumption yet left this direct type claim in §4.5.4, so the loop
has not converged. The next required action is a scoped fix-up rewriting the run-manifest
`diagnostics` definition and recording the resolution in this review. Because this finding does
not require a §5 change, the interface change trigger is not implicated by the ordered correction;
the normal next review is still required before Phase 2 can close with literal PASS.

## Resolutions

### candidate-001 — Resolved

Re-derived against Phase 1's binding conventions table and the target's pending R4B request. Phase
1 grants `EngineDiagnostic`, `DiagnosticSeverity`, and `UserChannel` only to the named consumers,
which do not include Phase 2. The §4.5.4 run-manifest table now defines `diagnostics` directly as
Phase-2-owned wire records with the already-specified `code`, `severity`, `channel`, `file`, and
`line` fields. No producer provenance was added, so the correction neither presumes acceptance of
R4B nor introduces a new ownership decision. A formulation sweep found no other target claim that
Phase 2 consumes those domain types; the remaining names occur only in R4B's explicit unaccepted
request. Binding §5 was not changed, so the declared interface region and its change trigger are
unaffected.

### Notes deferred

None.
