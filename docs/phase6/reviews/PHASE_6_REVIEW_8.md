# Phase 6 verification review — round 8

## 0. Method and reading order

This adjudication first independently re-derived the sole surviving candidate against the whole
target, `docs/phase6/v1/PHASE_6_DOC.md`; the override-selected governing Part I, Phase 6
assignment, document gate, and mandatory template in `docs/design/v3/DESIGN.md`; the relevant
contract ground truth in `docs/research/v1/RESEARCH.md`; and the manifest-selected binding
interfaces of Phases 1, 3, and 4. The listed supporting evidence does not own Phase 6's runtime
lifecycle and supplied no contrary contract.

Only after settling that interpretation did adjudication read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` through
`docs/phase6/reviews/PHASE_6_REVIEW_7.md`, in numeric order. There were no reading-order
deviations, no network use, and no agent fan-out. Gate dropped no candidates. Candidate-002 had
already been eliminated by Refute and is not revived. Candidate-001 was independently re-derived
rather than accepted from its incoming label.

## 1. Findings

No candidate was admitted.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation:

- The v3 Phase 6 specification, document gate, mandatory template, and contract-ground-truth
  inputs do not impose a more concrete reset representation or post-close policy than the target
  supplies.
- The target semantically closes the reset-reason set as pack replacement, shaders-off, world
  epoch, GL-context loss, and close; distinguishes non-close retention from close release; and
  transfers the sole runtime lifecycle to the successful factory consumer.
- The selected Phase 1 upload/readback/error contracts, Phase 3 declaration/resource/macro
  contracts, and Phase 4 effective-layout/barrier-participant contracts remain represented
  consistently.
- The complete Appendix D mapping, cadence model, smoothing formulas, sampler maps, notifier
  audit, frame ordering, temporal behavior, barrier trace, and conformance tests remain covered.

Candidate disposition on independent derivation:

- **candidate-001 — dropped.** The target states that the Java signatures are illustrative while
  their data contracts are binding (`docs/phase6/v1/PHASE_6_DOC.md:295-301`). The binding prose
  enumerates all five reset meanings and distinguishes non-close resets from close for retained
  bridge and provider references (`docs/phase6/v1/PHASE_6_DOC.md:1161-1169`,
  `docs/phase6/v1/PHASE_6_DOC.md:1183-1187`). Exact enum-constant spellings and a uniform
  post-close callable policy would be permissible implementation detail, but the governing v3
  assignment does not require either as an additional observable shader-pack contract. More
  importantly, prior Review 4 already adjudicated the same reset-reason and close-lifecycle
  surface on valid citations, holding that the semantic set is closed and that the documented
  close reset is the terminal lifecycle operation. Review 7 expressly preserved that settled
  disposition. The current candidate supplies no changed target bytes, new governing requirement,
  or dependency conflict that warrants reopening it.

Candidate-002 remains eliminated by Refute. The event interface's synchronous and scoped
execution rules remain sufficient for the challenged identity behavior, as settled in prior
review material; it is absent from the surviving set in any event.

Reading the prior reviews last did not change the independently reached disposition; it confirmed
that candidate-001 re-presents a cleared issue rather than identifying a new contract defect.

## 3. Verdict

# PASS
Counts: blocking=0; corrections=0; notes=0
Interface changed: no

The sole surviving candidate is dropped, so literal `PASS` is supported. No interface or
change-trigger-region edit is ordered, and no fix-up or further fresh verification round is owed.

Trend: Rounds 1–3 reported 2, 2, and 3 corrections; Round 4 passed; Rounds 5 and 6 each reported
one localized correction and were resolved; Round 7 passed; Round 8 again reports zero. The
current bytes remain converged under the explicit v3 verification override.
