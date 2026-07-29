# Schmaloogium — Phase 2: Conformance harness — Review Round 15

## 0. Method and reading order

I independently re-derived the gated candidate before reading any prior review. The reading order
was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, especially its conformance and milestone requirements.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially its
   GL-error surface, drain/replay protocol, consumer assignment, and §5 contract.
4. The supporting CI workflows under `.github/workflows/`.
5. The complete target, `docs/phase2/v1/PHASE_2_DOC.md`, with particular attention to the
   run-manifest attribution semantics and §5 requests R4A/R17.
6. Only after settling the candidate, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
   `docs/phase2/reviews/PHASE_2_REVIEW_14.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents, agent fan-out, or nested verification run. The canonical
engine supplied the finder, refuter, steelman, and Gate material. The Gate reported no drops, and
no candidates were eliminated before adjudication. Forbidden sources were not read.

The prior reviews were read last. Round 14 is relevant settled material: its correction required a
total replay-result source rather than inference from `GLError` fields, and its resolution chose an
additive Phase 1 diagnostic contract plus a Phase 7 serialization request. It did not state that
the Phase 1 GL facade executes Phase 6's replay.

## 1. Findings

No candidates were admitted. There are zero blocking findings, zero corrections, and zero notes.

## 2. Checked and clean

The finder-reported new surface is internally consistent: the Round-14 changes propagate the
Git-worktree refusal ordering and GL-error attribution source through detailed design, tests,
checklist, and §5. The interface audit otherwise found the module seam, capability-profile
serialization and fixtures, recording/log contracts, debug flags, CI extension, scene/golden
contracts, and profile contracts aligned with Phase 1. The conformance audit found all Phase 2
scope requirements, tiers, fixtures, scene families, harness requirements, and RESEARCH.md §9 exit
criteria substantively mapped.

### candidate-001 — cleared on re-derivation

- **Location tested:** `docs/phase2/v1/PHASE_2_DOC.md:1470–1473,1514–1517,1547`
- **Claim tested:** Request R4A unrealizably assigns replay-dependent classification to Phase 1
  even though Phase 6 executes the replay.
- **Disposition:** Dropped. The candidate conflates ownership of an additive diagnostic contract
  with runtime execution and production of its values. Phase 1's binding interface already owns
  `GLDevice.drainErrors()`, `GLError`, and the normative drain/replay semantics, while explicitly
  naming Phase 6 as the rung-2 consumer (`docs/phase1/v14/PHASE_1_DOC.md:3977`). R4A requests an
  additive result on that same diagnostic surface; it neither adds a facade verb nor says that the
  facade performs replay (`docs/phase2/v1/PHASE_2_DOC.md:1514–1517`). Its phrase “the producer
  returns” does not identify Phase 1 as the runtime producer. Phase 6 may execute the already
  assigned replay and produce values of the Phase 1-owned result contract, after which Phase 7
  captures and serializes them under R17 (`docs/phase2/v1/PHASE_2_DOC.md:1547`). No handback of
  Phase 6's outcome “to Phase 1” is required merely for an engine diagnostic type and semantics to
  be declared by Phase 1. Accordingly, the specified boundary is realizable as written and no
  interface correction is justified.
- **Final severity:** none
- **touches interface/change-trigger region:** no

No other candidate was available to admit, and no finding is created from the clean-area reports.

## 3. Verdict

# PASS
Counts: blocking=0; corrections=0; notes=0
Interface changed: no

The sole gated candidate is refuted on independent re-derivation. The document preserves the
caller-side Phase 6 replay assignment while placing the shared additive diagnostic contract beside
the Phase 1-owned GL-error surface; Phase 7's capture obligation is explicitly gated on acceptance
of that requested contract.

The prior correction trend plateaued at two corrections in Rounds 11–14, but Round 15 admits none:
the new candidate attacks the ownership model introduced by Round 14 and does not establish a
defect. This round therefore converges to literal PASS. No fix-up is required, the §5
change-trigger region was not changed by this adjudication, and Phase 2 may close as verified.
