# Phase 11 adversarial review — round 1

## 0. Method and reading order

I independently re-derived each gated candidate against the complete Phase 11 target, then the
manifest-selected v3 governing design selectors, the selected RESEARCH.md selectors, and the
binding §5 regions of Phase 3 and Phase 6. I used the supplied supporting evidence only where it
was relevant to the candidate. This is the first review, so there were no prior reviews to read
last.

I did not use the network, forbidden transcripts, or forbidden path patterns. I did not invoke the
verification harness, start another Codex process, or use agent fan-out. There were no reading-order
deviations. Gate dropped candidate-006 before adjudication because its quoted evidence did not
resolve; I did not revive it or create a replacement finding.

## 1. Findings

### candidate-001 — Phase 11 requests a Phase 3 declaration contract that Phase 3 already publishes

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:841`–`:871`, with repeated consequences at
  `:1155`–`:1169`.
- **Claim:** Every consumed dependency contract must be compared with the manifest-selected
  binding region and represented truthfully.
- **Evidence:** Phase 11 says Phase 3 §5 does not close the declaration enums, ordered accessor,
  attribution, ordinal, duplicate retention, or fingerprint participation
  (`docs/phase11/v1/PHASE_11_DOC.md:843`–`:853`) and consequently requests a dependency fix-up and
  fresh verification. Phase 3's binding interface already publishes all of those properties,
  including the closed enums, exact fields, ordering, duplicates, and fingerprint participation
  (`docs/phase3/v1/PHASE_3_DOC.md:1415`–`:1416`). The contradiction ruling and open handoff repeat
  the stale premise (`docs/phase11/v1/PHASE_11_DOC.md:1155`–`:1157`, `:1168`–`:1169`).
- **Severity:** correction. This can wrongly block implementation and reopen a verified dependency,
  but the repair is localized: rewrite §5.2 to consume the existing grant and remove only the
  fix-up, verification, ruling, gate, and handoff text predicated on its supposed absence.
- **Touches interface/change-trigger region:** yes.

### candidate-002 — Phase 11 marks three already-granted Phase 6 interfaces as absent

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:890`–`:914`, with related decision and handoff text.
- **Claim:** Phase 11 must consume the actual manifest-selected Phase 6 binding contract without
  inventing unresolved prerequisites.
- **Evidence:** Phase 11 §5.4 requests a fixed compile-time schema, `Bool1` encoding, and a normal
  absent-program skip, and requires a Phase 6 fix-up plus fresh verification
  (`docs/phase11/v1/PHASE_11_DOC.md:890`–`:914`). Phase 6's binding rows already grant
  `FixedExpressionInputSchema`, `Bool1` with Phase 6-owned integer encoding, and normal no-warning,
  no-GL `SkippedAbsent` (`docs/phase6/v1/PHASE_6_DOC.md:1389`–`:1391`).
- **Severity:** correction. Replace §5.4 with accurate adoption of those existing grants and remove
  only the corresponding pending-prerequisite language from decisions, handoffs, and checklist
  material. Preserve Phase 6's valid ownership of layout validation and GL encoding.
- **Touches interface/change-trigger region:** yes.

### candidate-003 — Refresh completion has the wrong closed result shape

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:630`–`:648`.
- **Claim:** The detailed bridge algorithm must implement the exact closed result algebra consumed
  from Phase 6.
- **Evidence:** Phase 11 counts normal absence skips but returns only
  `Completed(submitted,rejected)` (`docs/phase11/v1/PHASE_11_DOC.md:637`–`:648`). Phase 6 requires
  `Completed(accepted, skippedAbsent, rejected)` and
  `Aborted(diagnosticId, accepted, skippedAbsent, rejected)`
  (`docs/phase6/v1/PHASE_6_DOC.md:1244`–`:1249`); its binding §5 row additionally requires these
  counters to match the authoritative sink ledger (`docs/phase6/v1/PHASE_6_DOC.md:1389`).
- **Severity:** correction. In §4.8, track and return the three authoritative counters and specify
  the same three submitted-prefix counters for every `Aborted` result.
- **Touches interface/change-trigger region:** no. The incompatible algorithm is in §4.8 and can be
  repaired without changing §5's consumed bridge declaration.

### candidate-004 — The declared interface region does not publish implementable Phase 11 contracts

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:823`–`:839`.
- **Claim:** The manifest-declared change-trigger region must contain, or precisely incorporate,
  enough consumer-visible detail to implement against Phase 11 and detect later interface changes.
- **Evidence:** §5.1 supplies names and summaries but omits callable shapes, record fields, closed
  variants, and enum members (`docs/phase11/v1/PHASE_11_DOC.md:825`–`:836`). Load-bearing controller
  and reset declarations instead appear only at `docs/phase11/v1/PHASE_11_DOC.md:794`–`:805`, and
  provider request/result/snapshot schemas only at `:710`–`:729`. The governing template assigns
  exposed named interfaces and data contracts to §5 (`docs/design/v3/DESIGN.md:836`–`:840`). Unlike
  the verified Phase 6 publication pattern, Phase 11 §5 neither precisely incorporates the external
  declarations nor requires synchronized §5 updates for every consumer-visible change; compare
  `docs/phase6/v1/PHASE_6_DOC.md:1396`–`:1399`.
- **Severity:** correction. Precisely incorporate the detailed declarations into §5 and impose
  same-revision synchronization for every consumer-visible API, schema, variant, lifecycle,
  ownership, or semantic change; duplicate full declarations only where incorporation is ambiguous.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

The governing scope, mandatory thirteen-section structure, Phase 11 doc gate, RESEARCH expression
surface, full §3 conformance map, smooth state machine, evaluator choice and measurement criteria,
error isolation, licensing disposition, OQ-22 handoff, and Phase 3/Phase 6 binding regions were
checked. Apart from the findings above, Phase 11 correctly covers the Appendix F.6 language,
inputs and exclusions, program-switch cadence, variable behavior, smooth persistence, bridge
ordering, accepted-prefix ownership, GL separation, and Phase 7 composition handoff.

Candidate-005 is cleared. The v3 doc gate specifically requires every App F.6 token/function/
operator in the conformance map, while separately requiring the evaluator interface and decision
criteria and error semantics to be written (`docs/design/v3/DESIGN.md:2344`–`:2348`). The mandatory
template scopes map completeness to RESEARCH §3/Appendix contract rows
(`docs/design/v3/DESIGN.md:831`–`:833`). Phase 11 maps the declaration forms and the full App F.6
language surface, including numeric promotion, finite-value constraints, and zero-divisor behavior
(`docs/phase11/v1/PHASE_11_DOC.md:272`–`:286`), while §§4.3, 4.9, 4.11, and 10 provide the separately
required detailed typing, error, backend, decision, and OQ-22 content. Extra navigation rows could
be helpful, but their absence is not a correction-level contract failure.

Candidate-006 remains dropped on Gate's deterministic citation failure and was not adjudicated on
its merits.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: yes

The four admitted defects are locally repairable and do not require architectural rebuilding.
Three corrections alter the declared §5 interface/change-trigger region; candidate-003 can be
fixed wholly in §4.8. There is no prior-round trend on this first review, so convergence cannot yet
be inferred.

Next required action: run a scoped fix-up for this review, record resolutions, and then conduct a
fresh Phase 11 verification round because §5 changes before Phase 11 can close.

## Resolutions

### candidate-001 — resolved

Re-derived against Phase 3's binding §5 publication, which already closes the declaration enums,
record fields, ordered accessor, duplicate retention, attribution/ordinal, and fingerprint
participation. Phase 11 §5.2 now consumes that grant. The stale dependency-fix-up premise and its
decision, contradiction, handoff, requested-change, and checklist consequences were removed.

### candidate-002 — resolved

Re-derived against Phase 6's binding §5 rows. Phase 11 §5.4 now consumes the already-published
`FixedExpressionInputSchema`, `Bool1` with Phase 6-owned encoding, `SkippedAbsent`, and the three
authoritative result counters. Related pending-prerequisite language was removed while Phase 6's
ownership of active-layout validation, GL encoding, diagnostics, and upload isolation was kept.

### candidate-003 — resolved

Section 4.8 now increments the authoritative `accepted`, `skippedAbsent`, and `rejected` counters
from sink outcomes and returns `Completed(accepted,skippedAbsent,rejected)`. Every structural
`Aborted` result is required to carry the same three ledger counters for its submitted prefix.

### candidate-004 — resolved

Section 5.1 now precisely incorporates the detailed public declarations and semantics in §§2.3,
4.1, and 4.9–4.12, including record fields, closed variants/enums, callable shapes, lifecycle,
ownership, and error semantics. It also requires every consumer-visible change to update the
corresponding §5 row in the same revision. This intentionally changes the manifest-declared §5
interface region, so a fresh verification round is required before Phase 11 can close.

### Notes deferred

None. The adjudicated review admitted no notes.
