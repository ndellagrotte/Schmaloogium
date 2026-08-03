## 0. Method and reading order

This adjudication independently re-derived both surviving candidates against, in order:

1. the whole Phase 4 target, with focused checks of the publication API, compact algorithm,
   detailed state machine, diagnostics, binding §5 interface region, and publication tests;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4160-4265` and
   `docs/phase3/v1/PHASE_3_DOC.md:1300-1478`; and
5. the listed supporting evidence where it bore on publication and barrier behavior.

Only after settling those interpretations were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_28.md` consulted as settled prior material. Those reviews
establish the surrounding publication ownership, provenance, context, recovery, and absent-barrier
contracts. They do not settle the remaining validation-order contradiction or define a cause type
capable of representing publication-protocol failures.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so the verification loop and `scripts/verify` were not
invoked and no other Codex session was started.

## 1. Findings

### candidate-001 — Compact publication invalidates old activity before required candidate validation

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:1401-1407`
- **Claim:** The compact publication algorithm contradicts the detailed state machine by
  invalidating the old activity token before all publisher-side pre-release validation succeeds.
- **Evidence:** Compact step 2 proceeds from caller acceptance to context authentication and old
  activity-token invalidation (`docs/phase4/v1/PHASE_4_DOC.md:1401-1407`). The detailed state
  machine separately requires the publisher to validate compiler origin, product state, barrier
  provenance and composition, exact identities, private-barrier state, ownership, thread, context,
  and the bootstrap marker before release begins; any failure must leave the old publication
  provably usable and unchanged (`docs/phase4/v1/PHASE_4_DOC.md:1427-1439`). Caller acceptance
  cannot incorporate those checks because the text expressly assigns them to the publisher after
  transfer. An implementation following the compact ordering can therefore invalidate the old
  activity token and later reject the candidate, violating the stated unchanged-and-usable result.
- **Required correction:** Amend compact step 2 so the publisher completes every stated candidate,
  provenance, composition, identity, state, ownership, render-thread, and context validation before
  invalidating the old activity token or beginning old-barrier release. Preserve rejection with
  generation/current and the old publication unchanged, and preserve the absent-barrier zero-work
  branch.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

### candidate-002 — Publication results cannot represent their declared failure causes

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:619-625`
- **Claim:** The consumer-visible publication result is not implementable for all declared
  pre-release rejection and post-release recovery failures because both failure variants require a
  candidate-build aggregate with incompatible closed semantics.
- **Evidence:** `PublicationResult.Rejected` and `PublicationResult.RecoveredOff` both require a
  `RegistryBuildFailure` (`docs/phase4/v1/PHASE_4_DOC.md:619-625`). Yet publication must reject
  credential, provenance, composition, identity, ownership, thread, context, state, and bootstrap
  validation failures, and must report failures after old-barrier release begins
  (`docs/phase4/v1/PHASE_4_DOC.md:1427-1450`). `RegistryBuildFailure` has only
  `NO_REQUIRED_TERMINAL`, `CAPABILITY`, `UNSAFE_STATE`, and `UNEXPECTED_BACKEND`, and is expressly
  defined to mean that no complete registry is publishable and that the final pack-wide disposition
  is `ShadersOff` (`docs/phase4/v1/PHASE_4_DOC.md:1491-1508`). A pre-release protocol rejection can
  retain the old publication and does not imply that build disposition, so no exhaustive honest
  construction exists for the published result. Prior Review 1 introduced the build aggregate and
  publication result shapes, but did not define this later-required semantic mapping.
- **Required correction:** Define a separate closed publication-failure type with exhaustive kinds
  for pre-release validation and post-release recovery failures, use it in `Rejected` and
  `RecoveredOff`, and publish the exact shape and caller obligations in §5.1. Broadening and
  renaming the existing aggregate is acceptable only if its candidate-build semantics and all
  affected consumer contracts are revised consistently.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

- The finder-reported new-surface area is otherwise clean: §0 points to §0.32, and the detailed
  state machine plus binding §5 consistently authenticate the old context, skip release when the
  old off publication has no barrier, perform zero old-barrier GL work in that branch, and do not
  treat barrier absence alone as `RecoveredOff`.
- Dependency consumption is otherwise honest. Phase 4 uses the manifest-selected Phase 1 and
  Phase 3 contracts actually exposed and records missing legacy-geometry, mipmap, and vertex
  projections as requested changes rather than assumed APIs.
- The conformance map covers the governing modern and G6 configurations, complete program catalog,
  fallback semantics, compile/link flow, geometry disposition, failure handling, barrier duties,
  reload generation, and per-program state. No additional conformance finding survives.
- Candidate-001 is not cleared as a harmless summary: its affirmative ordering permits observable
  invalidation before checks that the detailed normative text requires to precede release. Prior
  Review 28 corrected only the absent-old-barrier branch of the compact step.
- Candidate-002 is not cleared by treating publication failures as downstream diagnostics. Phase 4
  owns publication, and its binding result requires an exact cause value for each closed outcome;
  the existing build aggregate neither enumerates the protocol cases nor has compatible meaning.
- No candidate was refuted, cleared, or dropped on re-derivation, and no finding was admitted beyond
  the supplied candidate set.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are bounded corrections rather than structural misses requiring a rebuild.
Literal `PASS` is unavailable while either correction remains. Candidate-002 requires changing the
manifest-declared `cross-phase-interfaces` region, so its change trigger applies and a fresh verify
round is required before Phase 4 can close.

Rounds 27-29 have correction counts 1, 1, and 2. Corrections increased and remain nonzero, so the
artifact is not converging under the strict decreasing/zero-correction criterion. The long-running
publication-interface correction pattern also warrants particular care that the fix-up updates the
detailed model, API shapes, binding §5, diagnostics, tests, and addendum consistently rather than
introducing another local patch.

The next required action is a scoped Phase 4 fix-up resolving candidate-001 and candidate-002,
appending this review's `## Resolutions`, and adding the required correction addendum. Because the
fix-up must change §5 for candidate-002, a fresh whole-document verification round is mandatory
afterward before Phase 4 can close.

## Resolutions

### candidate-001 — resolved

Re-derived the publication boundary from the detailed state machine and preserved its stronger
unchanged-old-publication guarantee. Compact §4.11 now completes the full publisher-side
candidate, provenance, composition, identity, state, ownership, render-thread, and context
validation set before activity-token invalidation or old-barrier release. Any validation failure
returns rejection with generation/current and the usable old publication unchanged. Only after
that validation succeeds may release begin; an absent authenticated old barrier still performs
zero old-barrier GL work.

### candidate-002 — resolved; interface changed

Re-derived candidate-build and publication semantics as disjoint failure domains.
`RegistryBuildFailure` remains the closed aggregate meaning that no complete candidate registry is
publishable and the pack-wide disposition is `ShadersOff`. New `PublicationFailure` and closed
`PublicationFailureKind` instead cover every declared pre-release validation cause and every
declared post-release recovery cause. `PublicationResult.Rejected` and `RecoveredOff` now carry
that publication-specific value. Sections 4.12 and binding §5.1 specify exact cause mapping,
sanitization, caller reporting/close/recovery duties, and the rule that publication failures never
alter candidate-build disposition or resolution projections. The publication result API, binding
interface table, diagnostics row, and exhaustive tests were updated consistently.

This intentionally changes the manifest-declared `cross-phase-interfaces` region. A fresh
whole-document verification round is therefore required before Phase 4 can close.

### Notes deferred

None. The adjudication admitted no notes.
