## 0. Method and reading order

This adjudication independently re-derived both supplied candidates against, in order:

1. the whole Phase 4 target, with focused checks of the §0.19 barrier-result, context,
   activation, publication, test, hand-off, and binding §5 surfaces;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4079-4181` and
   `docs/phase3/v1/PHASE_3_DOC.md:1101-1228`; and
5. the listed supporting evidence where it bore on the candidates.

Only after settling those interpretations were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_15.md` read, in round order. The prior reviews establish the
evolving barrier, publication, ownership, and interface surface, but none settles the two new
§0.19 issues below. Review 15's literal PASS predates the present amendment.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — `Activated` permits degradation while guaranteeing successful participant work

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:1047-1056` and
  `docs/phase4/v1/PHASE_4_DOC.md:1190-1193`
- **Claim:** The `Activated` result does not consistently state whether isolated participant
  degradation is compatible with its success guarantee.
- **Evidence:** The public result model permits `Activated` to carry a list of
  `BarrierParticipantResult.Degraded` outcomes
  (`docs/phase4/v1/PHASE_4_DOC.md:1047-1056`). The operational prose says each `Degraded` outcome
  disables its named participant-owned uniform/expression scope and continues, but then says
  `Activated` guarantees “successful participant work”
  (`docs/phase4/v1/PHASE_4_DOC.md:1190-1193`). Because an activated result can contain participant
  work that degraded and disabled scope, the unqualified guarantee cannot distinguish successful
  completion of the participant sequence from success by every participant.
- **Required correction:** Replace “successful participant work” with an exact guarantee such as
  completion of the participant sequence with any isolated degradations recorded. Preserve the
  existing `Activated.degradations` model and caller reporting duty.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

### candidate-002 — `BarrierContext` freshness and consistency validation are undefined

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:1019-1023`,
  `docs/phase4/v1/PHASE_4_DOC.md:1247-1250`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1330`
- **Claim:** Consumers cannot deterministically construct the caller-supplied context that
  activation and publication promise to validate.
- **Evidence:** `BarrierContext` contains caller-supplied `shadowPass`, `stage`, `band`, and
  `frameId` fields but exposes no authoritative source or validation rule
  (`docs/phase4/v1/PHASE_4_DOC.md:1019-1023`). Publication nevertheless rejects a missing,
  stale-frame, or stage/band-inconsistent context before release
  (`docs/phase4/v1/PHASE_4_DOC.md:1247-1250`). Binding §5 repeats that all four fields are
  caller-supplied without defining the current-frame comparison, the legal stage/band/shadow
  invariant, or the release context to use when replacement occurs outside ordinary activation
  (`docs/phase4/v1/PHASE_4_DOC.md:1330`). Stage metadata provides only partial ingredients; it
  does not define this acceptance predicate. The governing doc gate requires the barrier contract
  to be fully specified as an interface
  (`docs/design/v2.0-RC3/DESIGN.md:1559-1562`).
- **Required correction:** Define either an authoritative `BarrierContext` source/factory or an
  exact acceptance predicate covering frame freshness and stage/band/shadow consistency,
  including replacement outside ordinary activation. Publish the implementable consumer rule in
  §5 and add tests for accepted current context and each defined rejection class.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

- The §0.19 uniform-layout merge, structural conflict, layout-fingerprint, callback-scoped
  lookup, generation/provider/layout cache identity, token invalidation, retained-location, and
  fixed-function empty-layout rules are otherwise internally consistent.
- Consumed Phase 1 facade/result/handle contracts and Phase 3 configuration, materialization,
  declaration-catalog, geometry, macro, program-state, and resource contracts otherwise match
  their manifest-selected binding regions.
- Candidate-view ownership, opaque composition/publication provenance, generation invalidation,
  uniform-layout projection, and the recorded dependency-change request are represented
  consistently in §5.
- The conformance map covers the governing modern stage shape, sparse indexing, compute
  placeholders, lifecycle, compile/barrier behavior, classic program/fallback rows, and
  Phase-4-owned Appendix A.2/A.3 state.
- Neither candidate was refuted, cleared, or dropped on independent re-derivation. Prior Reviews
  1–15 do not define §0.19's participant-success wording or the missing `BarrierContext`
  acceptance rule.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are bounded corrections rather than structural misses requiring a rebuild.
Literal `PASS` is unavailable while two corrections remain.

The prior correction trend was 5, 3, 1, 3, 2, 2, 2, 2, 1, 2, 0, 1, 1, 1, then 0. This round
returns to two corrections after Round 15's PASS because the subsequently added §0.19 surface is
not yet internally complete; convergence and closure therefore no longer hold for the current
artifact.

The next required action is a scoped Phase 4 fix-up resolving candidates 001 and 002, appending
this review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. Because
candidate-002 changes the binding §5 `cross-phase-interfaces` change-trigger region, a fresh
verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — corrected

Re-derived the result semantics from the barrier's fixed three-position sequence and its existing
isolated `Degraded` outcome. In `docs/phase4/v1/PHASE_4_DOC.md` §4.10, `Activated` now guarantees
that the program and provider locks are active and that the ordered participant sequence completed,
with every isolated degradation recorded in `Activated.degradations`. It no longer implies that
every participant completed without degradation. The caller's draw and UI/log reporting duties
are unchanged, and §8 adds a representative result test.

### candidate-002 — corrected

Re-derived freshness from the fact that Phase 4 must validate the barrier input while Phase 7 owns
frame orchestration. Sections 4.10, 4.11, and binding §5 now expose a Phase-4-owned,
render-thread-only `BarrierContextSource`: Phase 7 begins exactly one private epoch per frame and
can obtain only opaque activation contexts minted from published `StageStep`s or a canonical
release-kind context. The contract defines current-source/epoch and kind checks, exact slot
stage/band membership, the shadow equivalence, prior-epoch retirement, and a canonical
`FINAL`/`SCREEN` release context when replacement occurs before any ordinary activation.
Publication accepts only the old publication source's current release-kind context, including for
out-of-band replacement. All foreign, retired, wrong-kind, unknown-step, stage/band, and shadow
mismatches fail before token invalidation, GL/state work, or publication release. Section 8 covers
the accepted current activation/release cases and every defined rejection class; §12 assigns the
implementation matrix.

### Notes deferred

None. The adjudication admitted no notes.
