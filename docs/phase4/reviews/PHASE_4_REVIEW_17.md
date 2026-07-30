## 0. Method and reading order

This adjudication independently re-derived the sole surviving candidate against, in order:

1. the whole Phase 4 target, with focused checks of the public barrier-context declarations,
   issuance and validation rules, publication path, binding §5 region, tests, and implementation
   checklist;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the manifest-selected binding contracts in
   `docs/phase1/v14/PHASE_1_DOC.md:4079-4181` and
   `docs/phase3/v1/PHASE_3_DOC.md:1101-1228`; and
5. the listed supporting evidence where it bore on the candidate.

Only after settling that interpretation were
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` through
`docs/phase4/reviews/PHASE_4_REVIEW_16.md` read, in round order. Those reviews establish the
evolving registry, publication, ownership, and barrier surfaces. Review 16 introduced and resolved
the Phase-4-owned context source, but its resolution does not settle the public API-shape
contradiction or release-context accessor semantics found below.

There were no reading-set deviations, no network use, no agent fan-out, no forbidden-source use,
no candidates eliminated before adjudication, and no Gate drops. The canonical engine had already
dispatched this atomic adjudication role, so neither the verification harness nor another Codex
session was invoked.

## 1. Findings

### candidate-001 — The barrier-context API is not opaque and leaves release `shadowPass()` undefined

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:1027-1040`,
  `docs/phase4/v1/PHASE_4_DOC.md:1220-1228`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1367`
- **Claim:** The round-16 context surface neither implements its promise that callers cannot
  construct or subclass the context family nor defines every public context operation for
  release-kind contexts.
- **Evidence:** The declared API makes `BarrierContextSource`, `FrameBarrierContexts`, and
  `BarrierContext` ordinary public interfaces, and the common `BarrierContext` surface requires
  `shadowPass()`, `stage()`, and `band()` (`docs/phase4/v1/PHASE_4_DOC.md:1027-1040`). The
  normative issuance contract nevertheless calls the context opaque and states that callers
  cannot construct or subclass any of these objects
  (`docs/phase4/v1/PHASE_4_DOC.md:1220-1228`). Source/epoch/kind validation can reject an external
  implementation, but it cannot make a public unsealed interface non-implementable. The same
  passage defines a release context's copied or default stage/band but never defines what its
  mandatory `shadowPass()` accessor returns, including when the copied pair is
  `SHADOW`/`SHADOW`. Binding §5 repeats that only the current issuer can mint the canonical
  release context and describes activation shadow equivalence, but supplies no release accessor
  semantics (`docs/phase4/v1/PHASE_4_DOC.md:1367`). The governing document gate requires the
  barrier contract to be fully specified as an interface
  (`docs/design/v2.0-RC3/DESIGN.md:1559-1563`).
- **Required correction:** Reconcile the public type shape and guarantee: either make the context
  family genuinely non-implementable outside Phase 4 or narrow the promise to state that
  caller-created implementations cannot mint an accepted context. Explicitly define
  `shadowPass()` for release-kind contexts, or split activation and release context views. Align
  binding §5 and add contract tests for the selected API shape and release accessor behavior.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

## 2. Checked and clean

- The round-16 `Activated` correction is coherent: the result now guarantees completion of the
  ordered participant sequence while retaining every isolated degradation for caller reporting.
- Source identity, epoch retirement, context kind, stage/band membership, activation shadow
  equivalence, and rejection-before-work rules are otherwise repeated consistently across the
  detailed design, binding §5, tests, and implementation checklist.
- Consumed Phase 1 facade, result, handle, and recording contracts and Phase 3 configuration,
  materialization, declaration, geometry, macro, state, and resource contracts remain aligned
  with their manifest-selected binding regions.
- The conformance surface remains covered across both stage configurations, the complete classic
  program catalog and fallback rules, compile/link lifecycle, fixed attributes, per-program state,
  generation invalidation, and dormant compute slots.
- No supplied candidate was refuted or cleared on re-derivation. Prior Reviews 1–16 do not define
  release-kind `shadowPass()` behavior or reconcile the ordinary public interfaces with the
  stronger non-construction and non-subclassing promise.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a bounded public-contract correction, not a structural miss requiring a
rebuild. Literal `PASS` is unavailable while one correction remains.

The correction trend through Round 16 is 5, 3, 1, 3, 2, 2, 2, 2, 1, 2, 0, 1, 1, 1, 0, 2.
This round reduces the count to one but does not establish convergence or closure; the new
round-16 context surface still requires correction.

The next required action is a scoped Phase 4 fix-up resolving candidate-001, appending this
review's `## Resolutions`, and adding the next compact Phase 4 fix-up addendum. Because the
correction changes the binding §5 `cross-phase-interfaces` change-trigger region, a fresh
verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — corrected

Re-derivation confirmed that Java's ordinary public interfaces permit external implementations,
while the existing source/epoch/kind checks already provide the intended authority boundary.
The target now calls the context types public views and promises only that Phase 4 can mint an
implementation accepted by the barrier or publisher. It also defines release-kind
`shadowPass()` as always `false`, even when the release context copies a prior
`SHADOW`/`SHADOW` stage/band pair. Binding §5 repeats both rules, and the test inventory plus
implementation checklist now cover caller implementations and release accessor behavior.

The compact §0.21 addendum records the correction. Binding §5 changed, so the manifest's
cross-phase-interface trigger requires a fresh verification round before Phase 4 can close.

### Notes deferred

None.
