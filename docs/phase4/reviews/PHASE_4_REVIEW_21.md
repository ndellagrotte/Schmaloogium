## 0. Method and reading order

This adjudication independently re-derived the sole surviving candidate against, in order:

1. the whole Phase 4 target, with focused checks of detached resolution projections, candidate
   snapshots, barrier/publication failure timing, binding §5, the §8 test inventory, and the §12
   implementation checklist;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4160-4265` and
   `docs/phase3/v1/PHASE_3_DOC.md:1300-1478`; and
5. the listed supporting evidence where it bore on the candidate.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_20.md` read, in round order. Those reviews establish the
evolving registry, publication, barrier, and projection surfaces. Review 14 established detached
candidate-view lifetime tests, and Reviews 19-20 established the total candidate-build projection
and its separation from later barrier/publication failures; none settles the focused publication
failure test-coverage omission below.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — Publication failures lack explicit projection-immutability coverage

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:1647-1649`,
  `docs/phase4/v1/PHASE_4_DOC.md:1705-1707`,
  `docs/phase4/v1/PHASE_4_DOC.md:1744-1753`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1942-1947`
- **Claim:** The test plan does not fully discharge its explicit requirement to prove that runtime
  publication failures cannot alter candidate, golden, or runtime resolution projections.
- **Evidence:** The focused projection inventory proves non-empty detail, barrier-failure
  immutability, and candidate/golden/runtime value equality, but names no publication-failure
  projection test (`docs/phase4/v1/PHASE_4_DOC.md:1647-1649`). The retained-view tests cover
  detached candidate metadata across rejection and `RecoveredOff`
  (`docs/phase4/v1/PHASE_4_DOC.md:1705-1707`), while the publication tests cover rejection,
  release recovery, generation, ownership, and whether a runtime barrier view exists
  (`docs/phase4/v1/PHASE_4_DOC.md:1744-1753`). Those are useful adjacent invariants, but none
  states that candidate, golden, and retained/runtime resolution rows are captured and compared
  before and after rejected or recovered-off publication, including an unexpected backend failure
  in later publication work. Section 12 nevertheless expressly orders proof that runtime
  barrier/**publication** failures cannot alter all three projections
  (`docs/phase4/v1/PHASE_4_DOC.md:1942-1947`). The combined tests therefore leave that named proof
  implicit rather than independently actionable.
- **Required correction:** Add explicit projection-before/after assertions to the relevant
  rejection and recovered-off publication tests, including the later unexpected-backend path, or
  add a dedicated test covering those scenarios. Assert the detached candidate/golden/runtime
  rows remain unchanged and value-equal wherever those snapshots exist; do not create projection
  rows for the empty recovered-off publication.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

## 2. Checked and clean

- The architecture itself consistently fixes resolution projections from candidate-time source
  and build evidence, makes them detached and handle-free, and excludes later barrier/publication
  failures from creating or mutating rows.
- Candidate and accepted runtime views are specified as value-equal for one accepted candidate;
  candidate snapshots remain safe after close, rejection, `RecoveredOff`, and accepted transfer.
- The Round 19 totality correction is coherent: unmasked failed ancestors produce `FAILED`, later
  success masks them to `CHAIN`, and every eligible candidate-build stage supplies sanitized
  non-empty detail.
- The Round 20 timing correction is coherent: `BARRIER` is not projection-eligible, and
  `UNEXPECTED_BACKEND` is eligible only during candidate materialization or GL build. Later
  barrier/publication failures follow their closed result protocols.
- Publication ownership, release recovery, generation invalidation, barrier-view availability,
  and candidate caller-ownership outcomes otherwise have focused tests.
- The finder-reported interface and conformance areas remain clean. Phase 4 consumes only the
  selected Phase 1 and Phase 3 binding contracts, and its §5 surface remains implementable and
  internally consistent.
- The strongest refutation was that detached candidate-view tests, publication lifecycle tests,
  and the baseline equality test jointly imply the required invariant. That clears the runtime
  contract itself but not §12's explicit proof obligation: the named tests do not direct an
  implementation to observe all three projection copies across publication failure. Candidate-001
  is therefore admitted as one test-plan correction, not an interface defect.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted omission is a bounded test-plan correction, not a structural miss requiring a
rebuild. Literal `PASS` is unavailable while one correction remains.

Rounds 19, 20, and 21 each contain one correction. The last two corrections concern the same
newly clarified candidate-time versus runtime-failure boundary, so the artifact has not yet
demonstrated convergence despite the bounded and non-interface nature of this finding.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. The correction can
be confined to the test plan and checklist and does not require changing the binding §5
`cross-phase-interfaces` region; after fix-up, a fresh verification round is required by the loop
before Phase 4 can close.

## Resolutions

### candidate-001 — resolved

Added three focused publication-failure tests in §8.4. They capture and compare the detached
candidate, golden, and existing runtime resolution lists across pre-release rejection,
`RecoveredOff` release failure, and an unexpected backend failure during later publication work.
The §12 implementation step now makes the before/after and value-equality assertions explicit and
states that the empty recovered-off publication creates no projection rows. This re-derives the
existing contract that publication happens after detached candidate projection creation; it does
not change binding §5 or the manifest-declared interface region.

### Notes deferred

None; the adjudication admitted no notes.
