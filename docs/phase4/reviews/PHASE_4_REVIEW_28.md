## 0. Method and reading order

This adjudication independently re-derived the sole surviving candidate against, in order:

1. the whole Phase 4 target, with focused checks of §0.31, the publication state machine and
   compact algorithm, the binding §5 interface region, publication tests, and closing status;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4160-4265` and
   `docs/phase3/v1/PHASE_3_DOC.md:1300-1478`; and
5. the listed supporting evidence where it bore on publication and barrier behavior.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_27.md` consulted as settled prior material. Prior Review 27
established and resolved the absent-old-barrier branch, but it does not clear the compact
publication algorithm's still-unqualified release step.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had
already dispatched this atomic adjudication role, so the verification harness and skill were not
invoked and no other Codex session was started.

## 1. Findings

### candidate-001 — Compact publication algorithm unconditionally releases an absent old barrier

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:1397-1405`
- **Claim:** The normative compact publication algorithm contradicts §0.31's detailed
  absent-barrier branch by directing every accepted publication to restore/release “the old
  barrier,” even when the authenticated old off publication has no barrier.
- **Evidence:** Step 2 says that, whenever the caller accepts a complete candidate, publication
  invalidates the old activity token, supplies the current render-thread `BarrierContext`, and
  restores/releases the old barrier (`docs/phase4/v1/PHASE_4_DOC.md:1397-1405`). The immediately
  following detailed contract instead defines a valid accepted shaders-off or `RecoveredOff`
  branch whose barrier is absent, for which the publisher skips `releaseToFixedFunction` and
  performs zero old-barrier GL work (`docs/phase4/v1/PHASE_4_DOC.md:1413-1420`). Section 0.31 says
  this correction was integrated into §4 (`docs/phase4/v1/PHASE_4_DOC.md:289-292`), and the
  governing gate requires the barrier contract to be fully specified as an interface
  (`docs/design/v2.0-RC3/DESIGN.md:1559-1563`). The detailed text and binding §5 make the intended
  implementation recoverable, but do not make the earlier normative algorithm internally true.
- **Required correction:** Qualify publication step 2 so every accepted publication authenticates
  the release context and invalidates the old activity token, but restores/releases the old
  barrier only when it exists; state explicitly that an absent barrier skips that operation and
  performs zero old-barrier GL work.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

## 2. Checked and clean

- The finder-reported new-surface checks are otherwise clean. Section 0.31, the detailed
  absent-barrier branch, binding §5.1, the off-to-ready test entry, and closing status consistently
  require context authentication, skip absent-barrier release with zero old-barrier GL work,
  preserve ordinary ready/off acceptance, and forbid barrier absence alone from causing
  `RecoveredOff`.
- The interface checks are clean apart from the non-interface summary inconsistency admitted
  above. The consumed Phase 1 and Phase 3 contracts are represented honestly, including pending
  dependency grants, and binding §5 already exports the corrected absent-barrier semantics.
- The conformance map and equivalent coverage across §§4-5, §8, §9, §11, §12, and Appendix A were
  checked without finding another surviving defect.
- Candidate-001 is not cleared by treating the compact list as shorthand. Its step 2 affirmatively
  orders an operation on “the old barrier” for every accepted candidate, while the detailed state
  machine permits an accepted old publication for which that object does not exist. The detailed
  branch resolves implementation intent but leaves a localized normative contradiction.
- Prior Review 27's resolution created the missing absent-barrier branch and changed binding §5;
  it did not condition the earlier six-step algorithm. No supplied candidate was refuted,
  cleared, or dropped on re-derivation, and no finding is admitted beyond the supplied candidate
  set.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

The admitted defect is a bounded internal-coherence correction rather than a structural miss or
an interface change. Literal `PASS` is unavailable while the correction remains.

Rounds 24-28 have correction counts 2, 1, 1, 1, and 1. Corrections remain nonzero and are not
strictly decreasing, so the artifact has not achieved the zero-correction round required for
convergence and closure.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the required compact Phase 4 correction addendum. The fix
must align §4's compact algorithm with the already-correct detailed semantics and binding §5; it
does not require changing the manifest-declared `cross-phase-interfaces` region. A fresh
whole-document verification round is required after fix-up before Phase 4 can close.

## Resolutions

### candidate-001 — resolved

Re-derived against the detailed publication state machine and binding §5, the compact algorithm
was the inconsistent formulation. Phase 4 §4.11 step 2 now requires authentication of the current
render-thread `BarrierContext` before old-token invalidation, conditions barrier restoration and
release on the old barrier being present, and explicitly gives the absent-barrier branch zero
old-barrier GL work. Section 0.32 records the correction and the header's revision marker now
points to it. The manifest-declared `cross-phase-interfaces` region was intentionally unchanged;
the correction only aligns earlier §4 summary prose with that existing binding contract.

### Notes deferred

None. The adjudicator admitted no notes.
