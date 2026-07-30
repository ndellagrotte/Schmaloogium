# Phase 6 verification review — round 2

## 0. Method and reading order

This adjudication first re-derived every surviving candidate against the whole target,
`docs/phase6/v1/PHASE_6_DOC.md`; the governing Part I, Phase 6 assignment, document gate, and
mandatory template in `docs/design/v2.0-RC3/DESIGN.md`; the contract ground truth in
`docs/research/v1/RESEARCH.md`; the manifest-selected binding interfaces of Phases 1, 3, and 4;
and the listed supporting evidence where relevant. Supporting and Pintonium material was treated
as evidence, never as contract.

Only after settling those interpretations did adjudication read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md`. Its two corrections are resolved in the target and were
not reopened. There were no reading-order deviations, no network use, and no agent fan-out. Gate
dropped no candidates. Candidate-001 and candidate-003 were independently re-derived together
because they allege the same event-identity defect; candidate-002 was evaluated as the
`FrameBeginResult` subset of candidate-004.

## 1. Findings

### candidate-004 — Construction and frame-begin result contracts have no closed schemas

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:194-225`,
  `docs/phase6/v1/PHASE_6_DOC.md:680-691`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1048-1053`
- **Claim:** Phase 7 cannot implement construction and frame orchestration without inventing the
  concrete outcomes and payloads of `UniformBuildResult` and `FrameBeginResult`.
- **Evidence:** The public surface returns both named result types, but declares only
  `FrameBeginInput` (`docs/phase6/v1/PHASE_6_DOC.md:194-225`). The lifecycle distinguishes an
  accepted new frame, an idempotent duplicate, and rejected stale/generation-mismatched calls
  (`docs/phase6/v1/PHASE_6_DOC.md:680-691`,
  `docs/phase6/v1/PHASE_6_DOC.md:1161-1162`), yet no declaration maps those dispositions to values
  Phase 7 can inspect. Likewise, the factory promise that no partial operational runtime is
  returned does not define construction success/failure payloads or ownership
  (`docs/phase6/v1/PHASE_6_DOC.md:1050-1052`). Whole-target identifier search found no equivalent
  definitions. The governing template requires exact lifecycle semantics and named exposed data
  contracts (`docs/design/v2.0-RC3/DESIGN.md:809-814`).
- **Required correction:** Define closed `UniformBuildResult` and `FrameBeginResult` schemas.
  Specify construction success/failure payloads and ownership, and map the already-documented
  accepted, duplicate/idempotent, and rejected frame-begin cases to exact returned values,
  mutation behavior, and Phase 7 duties. Align §§2.2, 4.6, 5.1, and 6. The reset-reason set and
  principal effects already exist in §4.14, so this finding does not require their reinvention.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-005 — The Phase 11 custom-uniform bridge protocol is not implementable

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:1001-1024`,
  `docs/phase6/v1/PHASE_6_DOC.md:1057-1058`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1387-1388`
- **Claim:** Phase 11 cannot implement `CustomUniformBridge` or return typed custom uploads without
  inventing the view, sink, and result contracts that Phase 6 owns.
- **Evidence:** The extension-point signature names `CustomRefreshResult`,
  `BuiltInExpressionView`, and `CustomUniformUploadSink`, but the surrounding text supplies no
  callable view/absence contract, supported typed sink operations, validation and duplicate/order
  rules, or closed result outcomes (`docs/phase6/v1/PHASE_6_DOC.md:1001-1024`). Whole-target
  search finds the result and sink only in that signature; the view receives whitelist prose but
  no lookup or value representation. Section 5 nevertheless exports the bridge and view to Phase
  11 (`docs/phase6/v1/PHASE_6_DOC.md:1057-1058`), and the onward handoff requires Phase 11 to
  consume the view and return typed commands (`docs/phase6/v1/PHASE_6_DOC.md:1387-1388`). The
  governing assignment expressly requires Phase 6 to define this extension point
  (`docs/design/v2.0-RC3/DESIGN.md:1713-1714`).
- **Required correction:** Define the minimum closed upload-boundary contracts for
  `BuiltInExpressionView`, `CustomUniformUploadSink`, and `CustomRefreshResult`: lookup,
  value-type, and absence semantics; supported immutable typed upload commands; ownership of
  location/type validation, ordering, duplicate handling, diagnostics, and per-uniform GL-error
  isolation; and concrete result variants. Export the same contract in §5. Leave custom-expression
  syntax and evaluation policy to Phase 11.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation:

- The corrected old-hand-light mapping is consistent across the addendum, exact event schema,
  inventory, §5 contract, and onward handoff. Optional shadow-angle and fog-fallback rules do not
  establish another correction.
- Phase 6 accurately consumes the selected Phase 1 upload/readback/error contracts and Phase 4
  ordering/fallback contracts. It reports the missing Phase 3 declaration metadata and Phase 4
  activity/location capability as requested dependency changes instead of silently assuming them.
- The full Appendix D mapping, smoothing formulas, fixed sampler maps (including `depthtex1` unit
  11), notifier audit, and frame-begin ordering export satisfy the examined conformance surface.

Candidate dispositions on independent derivation:

- **candidate-001 — dropped.** It duplicates candidate-003, and its premise does not survive the
  target's explicit execution contract. “All event identities” constrains identities carried by
  identity-bearing records; it does not state that every synchronous setter must acquire an
  identity field (`docs/phase6/v1/PHASE_6_DOC.md:436-449`). The challenged draw-state setters are
  render-thread-only, unqueued observations written synchronously before the consuming activation
  (`docs/phase6/v1/PHASE_6_DOC.md:1182-1190`), with scoped push/pop or reset producer lifetimes
  (`docs/phase6/v1/PHASE_6_DOC.md:975-980`) and a current Phase 4 activity-token guard for immediate
  uploads (`docs/phase6/v1/PHASE_6_DOC.md:491-497`). Adding payload identities is therefore not
  required by the documented semantics.
- **candidate-002 — dropped as subsumed.** Its valid `FrameBeginResult` omission is fully included
  in admitted candidate-004; admitting both would double-count one correction.
- **candidate-003 — dropped.** It is the same event-identity claim as candidate-001. Runtime-current
  identity is sufficient here because the document expressly forbids queued/asynchronous delivery
  and binds the value-only operations to synchronous draw scopes. Section 5's collective summary
  of the event surface does not override those more exact per-method lifetime semantics.

Round 1 was read last. Its provider/event-schema and held-light findings have explicit resolutions,
and the corrected material is present; neither settled correction is revived.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

The two admitted omissions are bounded interface corrections, not structural defects requiring a
rebuild, so `FAIL` is not warranted. Literal `PASS` is unavailable while they remain.

Both corrections affect contracts exported through the `cross-phase-interfaces` change-trigger
region. The next required action is a fix-up resolving candidate-004 and candidate-005, appending
the resolution record to this review, and updating the target's binding definitions consistently.
Because §5 must change, a fresh verification round is required before Phase 6 can close.

Trend: Round 1 had two interface corrections and Round 2 again has two, but the earlier pair is
resolved and the new pair concerns previously underspecified lifecycle and extension-point types.
The count is flat rather than converging to literal PASS; no escalation to `FAIL` is justified,
but closure cannot be claimed.

## Resolutions

### candidate-004 — resolved

Added closed `UniformBuildResult` and `FrameBeginResult` definitions in §2.2. Construction now
distinguishes success, which transfers the sole runtime lifecycle while retaining only borrowed
service references, from failure, which returns a non-empty stable diagnostic ID, no runtime, and
performs no GL work. Section 4.6 maps a new frame, an exact duplicate, stale frame/world identity,
and generation mismatch to exact enum values and states mutation and Phase 7 continuation duties.
Sections 5.1 and 6 now export and degrade through those same outcomes. This changes §5, so a fresh
verification round is required before Phase 6 can close.

### candidate-005 — resolved

Expanded §4.13 with exact-name `BuiltInExpressionView` lookup, closed present/absent and typed-value
results, immutable scalar/vector/mat4 upload commands, sink acceptance/rejection, and closed refresh
outcomes. The contract assigns definition order and expression policy to Phase 11, while Phase 6
owns location/type validation, first-wins duplicate rejection, diagnostics, accepted-command order,
and per-command GL-error isolation. Sections 5.1, 6, and 11.3 now carry the same boundary. Custom
expression syntax and evaluation policy remain Phase 11-owned. This changes §5, so a fresh
verification round is required before Phase 6 can close.

### Notes deferred

None.
