# Phase 6 verification review — round 9

## 0. Method and reading order

This adjudication first independently re-derived the sole surviving candidate against the whole
target, `docs/phase6/v1/PHASE_6_DOC.md`; the override-selected governing Part I, Phase 6
assignment, document gate, and mandatory template in `docs/design/v3/DESIGN.md`; the relevant
contract ground truth in `docs/research/v1/RESEARCH.md`; and the manifest-selected binding
interfaces of Phases 1, 3, and 4. Supporting evidence was treated only as evidence and was not
needed to establish the lifecycle defect.

Only after settling that interpretation did adjudication read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` through
`docs/phase6/reviews/PHASE_6_REVIEW_8.md`, in numeric order. There were no reading-order
deviations, no network use, and no agent fan-out. Gate dropped no candidates. Candidate-001 was
independently re-derived rather than accepted from its incoming label.

## 1. Findings

### candidate-001 — The runtime cannot establish or replace its current registry generation

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:239-261`,
  `docs/phase6/v1/PHASE_6_DOC.md:741-759`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1163-1187`
- **Claim:** Phase 7 cannot implement the exported runtime lifecycle and generation validation
  without inventing how Phase 6 adopts the initial or replacement registry generation.
- **Evidence:** Factory construction receives configuration and services but no publication or
  generation, while the runtime exposes frame begin and a reason-only reset
  (`docs/phase6/v1/PHASE_6_DOC.md:239-261`). `beginFrame` must first validate the supplied registry
  generation against an already-current value and return `REJECTED_GENERATION` on mismatch; a
  rejection performs no mutation and requires Phase 7 to reacquire the current publication
  (`docs/phase6/v1/PHASE_6_DOC.md:741-759`). The reset section says that registry replacement
  discards generation-scoped caches and that GL-context loss requires a new publication, but it
  supplies no generation-bearing adoption input or ordering rule
  (`docs/phase6/v1/PHASE_6_DOC.md:1163-1169`). Section 5 nevertheless exports factory/runtime
  reload and reset behavior plus generation rejection to Phase 7
  (`docs/phase6/v1/PHASE_6_DOC.md:1185-1187`). Phase 4 owns the authoritative generation, changes
  it on accepted publication/off transitions, and requires consumers to compare for inequality
  (`docs/phase4/v1/PHASE_4_DOC.md:1570`); that ownership does not establish Phase 6's private
  current value. Consequently neither the first `beginFrame` nor a post-replacement one can be
  classified without an invented Phase 7-to-Phase 6 handshake.
- **Required correction:** Add a generation-bearing lifecycle contract to construction or the
  runtime and export it in §5. Define how the initial authoritative generation is installed and
  how a replacement is adopted before `beginFrame`, including ordering, equality/monotonicity or
  stale-input rules, idempotence, and retained/discarded state. Passing an authenticated
  generation is sufficient if Phase 6 does not otherwise need the full `PublishedRegistry`.
  Preserve the already-settled semantic reset-reason set; exact enum spellings are not independently
  required by this finding.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation:

- The Appendix D provider/cadence/milestone inventory, conformance map, smoothing math, sampler
  variants, frame ordering, temporal snapshots, notifier ownership, custom-uniform ordering, and
  barrier integration remain substantively covered.
- Sampler, built-in, and custom participant ordering; callback-scoped program access;
  effective-provider cache identity; Phase 3 declaration/resource consumption; resize/clear
  ordering; and typed event ownership are internally consistent.
- The selected Phase 1 GL facade, Phase 3 configuration/declaration contracts, and Phase 4
  effective-layout, cache-key, barrier, and participant contracts otherwise support the target's
  claimed consumption.

No candidate was dropped on independent derivation. Prior Reviews 4 and 8 settled only whether the
five reset meanings require exact Java enum spellings and whether close needs a separate method.
They did not examine, settle, or supply the generation-adoption handshake. Candidate-001 is
therefore admitted only for that distinct consumer-visible omission, not as a reopening of the
cleared representation issue.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The missing generation-adoption handshake is a bounded cross-phase contract correction, not a
structural miss requiring rebuild, so `FAIL` is not warranted. Literal `PASS` is unavailable while
the correction remains.

The next required action is a fix-up resolving candidate-001 and appending its resolution to this
review. Because the correction must update the exported lifecycle in the
`cross-phase-interfaces` change-trigger region, a fresh verification round is required before
Phase 6 can close.

Trend: Rounds 7 and 8 returned literal PASS after earlier corrections converged, but Round 9
exposes one previously unexamined interface omission. Convergence is interrupted rather than
structurally reversed: the remaining defect is localized, but closure cannot be claimed until its
fix-up receives a fresh literal-PASS review.

## Resolutions

### candidate-001 — resolved

Re-derived against Phase 4's binding equality-only generation protocol, this correction does not
impose numeric monotonicity. `UniformRuntimeFactory.create` now installs the generation from the
current authoritative publication. `UniformRuntime.adoptRegistryGeneration` atomically installs a
replacement generation and its semantic reset scope before any frame or activation: current-value
repetition is idempotent, a previously retired value rejects without mutation, and a different
unseen value adopts while retiring the prior value. Phase 7 must supply the freshly reacquired
authoritative publication rather than a candidate or delayed snapshot; rejection forbids drawing.

The target now specifies ordering and retained/discarded state in §§4.1 and 4.14, makes frame
validation depend on the adopted-current generation in §4.6, exports construction and adoption in
§5.1, and extends lifecycle test/checklist coverage. The §5 interface region changed
intentionally, so the declared fresh-verification trigger fires. The existing five semantic reset
reasons were preserved.

### Notes deferred

None.
