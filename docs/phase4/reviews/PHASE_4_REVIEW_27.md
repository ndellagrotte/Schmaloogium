## 0. Method and reading order

This adjudication independently re-derived the sole surviving candidate against, in order:

1. the whole Phase 4 target, with focused checks of the publication state model, barrier context
   authentication, release/recovery algorithm, tests, and manifest-declared §5 interface region;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4160-4265` and
   `docs/phase3/v1/PHASE_3_DOC.md:1300-1478`; and
5. the listed supporting evidence where it bore on the candidate.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_26.md` consulted as settled prior material. Those reviews do
not define or clear publication from an already-off state; prior publication corrections concern
context supply, release failure, recovery, ownership, and later status/interface maintenance but
leave the optional-old-barrier branch at issue here unspecified.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — Publication from an existing shaders-off state has no defined release path

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:625-629`,
  `docs/phase4/v1/PHASE_4_DOC.md:1402-1435`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1512-1515`
- **Claim:** The publisher contract is not implementable for a ready or off publication whose old
  publication is accepted shaders-off or `RecoveredOff`, because that old state has no barrier
  while the replacement algorithm unconditionally requires old-barrier release.
- **Evidence:** `PublishedRegistry` makes the barrier optional while retaining a mandatory
  `BarrierContextSource` (`docs/phase4/v1/PHASE_4_DOC.md:591-595`), and the target expressly says
  accepted shaders-off and `RecoveredOff` contain no barrier view
  (`docs/phase4/v1/PHASE_4_DOC.md:625-629`). The detailed publication contract nevertheless
  requires a current release-kind context for ready and off replacement and says the publisher
  passes it to the old barrier's `releaseToFixedFunction`
  (`docs/phase4/v1/PHASE_4_DOC.md:1407-1413`); replacement is then permitted only after that
  old-barrier release returns `FixedFunction`
  (`docs/phase4/v1/PHASE_4_DOC.md:1429-1435`). No branch specifies what happens when the old
  publication has no barrier. The mandatory context source solves credential issuance and
  validation, but cannot supply the missing release target. Binding §5 exports the optional
  ready-only barrier and mandatory-context publication call to Phases 7 and 12 without defining
  the absent-barrier behavior (`docs/phase4/v1/PHASE_4_DOC.md:1512-1515`). The governing document
  gate requires the barrier contract to be fully specified as an interface
  (`docs/design/v2.0-RC3/DESIGN.md:1559-1562`).
- **Required correction:** Define off-to-ready and off-to-off publication in the detailed
  algorithm and binding §5. Validate the release context against the old off publication's
  retained current `BarrierContextSource`, explicitly skip `releaseToFixedFunction` when its
  optional old barrier is absent, and state the zero old-barrier GL work, acceptance/recovery,
  generation, ownership, and caller semantics. Add a representative off-to-ready publication
  test.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

- The finder-reported new-surface checks are clean: the header correctly names §0.30 as latest,
  the closing status correctly distinguishes status-only §0.30 from the latest binding-§5 change
  in §0.29, and the pending Phase 3 grants remain consistently represented.
- Apart from the admitted absent-old-barrier branch, the compiler, candidate ownership, detached
  views, resolution projections, uniform access, participant composition, context authentication,
  generation invalidation, fixed attributes, instance counts, and pending dependency requests
  examined by the interface lens remain coherent.
- The conformance map covers both required stage configurations, every Appendix A.1 row, and the
  Appendix A.2/A.3 state mappings examined by the conformance lens; no additional conformance
  defect survives.
- The candidate is not cleared by the always-present context source: that source can authenticate
  the mandatory release context, but the old off publication still has no barrier on which to
  perform the unconditionally specified release. It is not cleared by the existing off-state or
  release-failure tests, which do not define an off-to-ready transition.
- Prior Reviews 1–26 contain no settled absent-old-barrier branch. No supplied candidate was
  refuted, cleared, or dropped on re-derivation, and no finding is admitted beyond the supplied
  candidate set.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a bounded publication-state correction rather than a structural miss that
requires rebuilding Phase 4. Literal `PASS` is unavailable while the correction remains.

Rounds 23–27 have correction counts 1, 2, 1, 1, and 1. Corrections are not strictly decreasing,
and the artifact has not achieved the zero-correction round required for convergence and closure.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the required compact Phase 4 correction addendum. Because
the correction must change the binding §5 `cross-phase-interfaces` region, its change trigger
fires and a fresh whole-document verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — Applied

Re-derived the state mismatch from the target's own publication model: an old accepted
shaders-off or `RecoveredOff` snapshot retains its issuer but has neither registry nor barrier, so
context authentication remains meaningful while barrier release cannot be called. Section 4.11
now authenticates the mandatory release context in both branches, conditionally calls
`releaseToFixedFunction` only when the old barrier exists, and defines the absent-barrier branch
as zero old-barrier GL work followed by ordinary ready/off acceptance. That acceptance increments
generation once, transfers a ready candidate once, and has no old registry to close; pre-release
validation failure remains `Rejected` with caller ownership, and barrier absence alone is not a
recovery failure. The existing post-release `RecoveredOff` path is unchanged.

Binding §5 now states the authenticated off-publication branch and its ready/off acceptance
semantics for Phases 7 and 12. This intentional edit changes the manifest-declared
`cross-phase-interfaces` region, so its fresh-verification trigger fires. Section 8 adds the
representative off-to-ready recorded test covering authentication, skipped release/zero release
GL work, one generation increment, and one ownership transfer. Compact §0.31 records only the
correction scope and re-verification duty.

### Notes deferred

None; the adjudication admitted no notes.
