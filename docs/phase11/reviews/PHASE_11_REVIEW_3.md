# Phase 11 adversarial review — round 3

## 0. Method and reading order

I independently re-derived all four surviving candidates against the complete Phase 11 target,
the manifest-selected v3 governing design selectors, the selected RESEARCH.md authority, the
binding Phase 3 and Phase 6 contracts, and the relevant permitted supporting evidence. Only after
settling those interpretations did I read `docs/phase11/reviews/PHASE_11_REVIEW_1.md` and
`docs/phase11/reviews/PHASE_11_REVIEW_2.md`, including their resolutions.

I did not use the network, forbidden transcripts, or forbidden path patterns. I did not invoke the
verification harness, start another Codex process, or use agent fan-out. There were no reading-order
deviations, pre-adjudication eliminations, or Gate drops.

## 1. Findings

### candidate-001 — Repeated verification-status prose was not synchronized after Round 2

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:67`–`:69` and `:1209`–`:1210`.
- **Claim:** Current-state prose must consistently identify the completed review chronology and the
  latest §5 change that creates the outstanding fresh-verification obligation.
- **Evidence:** The Round-2 addendum says Round 2 corrected the contracts and, because §5 changed,
  another fresh verification round is required (`docs/phase11/v1/PHASE_11_DOC.md:102`–`:106`).
  Section 0.2 instead still says Round 1 caused the “current fresh verification round”
  (`docs/phase11/v1/PHASE_11_DOC.md:67`–`:69`), while §11.2 says “this round” was required by Round
  1 and “this fix-up” changes §5 (`docs/phase11/v1/PHASE_11_DOC.md:1209`–`:1210`). These are
  current-relative assertions, not labeled historical snapshots. The governing process makes the
  latest §5-changing fix-up and its fresh review material to verification state
  (`docs/design/v3/DESIGN.md:354`–`:358`).
- **Severity:** correction. Synchronize both passages with §0.5: Rounds 1 and 2 completed, the
  Round-2 fix-up made the latest §5 change, and Round 3 is the fresh verification it triggered.
- **Touches interface/change-trigger region:** no.

### candidate-002 — Current dependency-gate references cite superseded PASS reviews

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:20`–`:24`, with the repeated input-table entries
  at `:34` and `:36` and the implementation hook at `:1231`–`:1232`.
- **Claim:** Current dependency verification references must identify the newest literal-PASS
  reviews for the binding documents, as the target's own implementation hook requires.
- **Evidence:** Phase 11 presents Phase 3 Review 31 and Phase 6 Review 18 as verification of the
  current dependency documents (`docs/phase11/v1/PHASE_11_DOC.md:20`–`:24`) and repeats those
  reviews in its inputs, while §12 requires their newest reviews to remain literal PASS
  (`docs/phase11/v1/PHASE_11_DOC.md:1231`–`:1232`). Phase 3 Review 34 is a later literal PASS with
  zero findings (`docs/phase3/reviews/PHASE_3_REVIEW_34.md:44`–`:48`) and expressly reviewed the
  binding §5 surface without changing it (`:50`–`:56`). Phase 6 Review 22 is likewise a later
  literal PASS (`docs/phase6/reviews/PHASE_6_REVIEW_22.md:48`–`:52`) and records convergence for
  the current bytes (`:57`–`:63`).
- **Severity:** correction. Replace current-state references to Reviews 31 and 18 with Reviews 34
  and 22, respectively, using verified line ranges.
- **Touches interface/change-trigger region:** no.

### candidate-003 — Public backend selection omits the interpreter ID and unsupported-ID result

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:417`–`:422`, `:814`–`:817`, and
  `:901`–`:909`.
- **Claim:** Phase 7 must be able to invoke the published compiler's semantic-ID selector without
  inventing either a valid v0.4 value or its failure behavior.
- **Evidence:** Both public compiler entry points require a `backendSemanticId`
  (`docs/phase11/v1/PHASE_11_DOC.md:417`–`:422`), and §4.11 says consumers select only through that
  stable ID while backend implementation types remain private (`:814`–`:817`). The binding §5 row
  repeats the semantic-ID selector (`:901`–`:909`), but the whole target provides no canonical
  v0.4 interpreter ID and no deterministic result or diagnostic for an unsupported ID. Phase 14
  already owns any compiled implementation (`:143`) and is told to prototype it behind the
  existing private SPI (`:1156`–`:1158`); that internal handoff does not require a public backend
  registry or registration lifecycle.
- **Severity:** correction. Define the canonical v0.4 interpreter semantic ID and the exact
  `PlanBuildResult` and diagnostic behavior for an unsupported ID in the incorporated public
  declarations and §5. No public Phase 14 extension mechanism is ordered by this finding.
- **Touches interface/change-trigger region:** yes.

### candidate-004 — The `pi` conformance row specifies the wrong binary32 value

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:286`.
- **Claim:** The conformance map's constant must match the shipped pack-author contract under the
  target's exact binary32 semantics.
- **Evidence:** Phase 11 defines `pi` as `Math.PI` rounded to binary32
  (`docs/phase11/v1/PHASE_11_DOC.md:286`). The shipped documentation defines it as decimal
  `3.1415926` (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:332`–`:336`). These
  are not binary32-equivalent: rounding `Math.PI` yields bits `0x40490fdb`, whereas converting the
  documented decimal yields `0x40490fda`.
- **Severity:** correction. Define `pi` by converting documented decimal `3.1415926` to binary32,
  update `piConstantBinary32` to assert `0x40490fda`, and cite the shipped documentation directly.
- **Touches interface/change-trigger region:** no.

## 2. Checked and clean

The Round-2 addendum and neighboring chronology, compiler and Phase 3 adapter, backend handoff,
§5 publication, dependency verification references, decisions and checklist, full conformance map,
Phase 3 declaration projection, and Phase 6 schema/bridge contracts were checked. Apart from the
four findings above, the deterministic equal-name Phase 3 enum adaptation has a closed diagnostic
path; the seven-name expression-input exclusion is consistent; and the Phase 3 and Phase 6 binding
contracts remain truthfully consumed.

No whole candidate was cleared on re-derivation. Candidate-003 was narrowed: its public Phase 7
selection gap survives, but Phase 14's assigned internal implementation behind Phase 11's private
SPI does not establish a need for a public registry, factory, or registration lifecycle. Reading
Rounds 1 and 2 last showed their settled findings resolved and supplied no basis to drop or expand
the present findings. There were no eliminated candidates or Gate drops to revisit.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: yes

All four admitted defects are bounded corrections rather than structural misses. Candidate-003
requires changing the incorporated public backend-selection contract and therefore the declared
§5 interface/change-trigger region; the other three do not.

Trend/convergence: corrections moved from four in Round 1 to three in Round 2 and back to four in
Round 3. The loop has not converged to literal PASS; the increase is a bounded non-convergence
warning, driven by stale repeated state, dependency provenance, one second-order selector omission,
and one exact constant mismatch rather than a need to rebuild the architecture.

Next required action: apply a scoped fix-up, append resolutions to this review, and conduct a fresh
Phase 11 verification round because §5 changes before Phase 11 can close.

## Resolutions

### candidate-001 — applied

Synchronized §0.2 and §11.2 with the completed chronology: Rounds 1 and 2 preceded this review,
the Round-2 fix-up made the latest prior §5 change, and Round 3 was the fresh review thereby owed.
The new compact §0.6 records this round's correction and the next verification obligation.

### candidate-002 — applied

Re-derived the dependency state from the declared supporting reviews. Replaced every current
Review-31/Review-18 dependency reference with Phase 3 Review 34 (`PASS`, zero findings, unchanged
binding §5) and Phase 6 Review 22 (`PASS`, zero findings, converged current bytes), including their
verified verdict/state ranges. The implementation checklist already requires the newest literal
PASS reviews generically and needed no wording change.

### candidate-003 — applied; interface intentionally changed

Published `schmaloogium:typed-ast-interpreter-v1` as the sole v0.4 semantic ID. An unsupported ID
now deterministically produces `PlanBuildResult.Failure`, exactly one `UNSUPPORTED_BACKEND` error
diagnostic, compile-request attribution with an empty span, and no parsing or plan. The diagnostic's
stable-ID inputs are fixed. Added the diagnostic kind and synchronized the incorporated §5 backend
row. No public registry or Phase 14 registration mechanism was added: Phase 14's candidate remains
behind Phase 11's private SPI. This intentional §5 edit fires the manifest's fresh-review trigger.

### candidate-004 — applied

Re-derived the constant from the shipped pack-author documentation rather than `Math.PI`. The
conformance row now converts decimal `3.1415926` to binary32, specifies bits `0x40490fda`, retains
the `piConstantBinary32` test, and cites the documentation's constant definition directly.

### Notes deferred

None. The adjudication admitted no notes, and no correction required refusal or a new design
decision.
