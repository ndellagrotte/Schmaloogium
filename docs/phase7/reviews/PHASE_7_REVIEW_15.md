## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH lifecycle material, the
binding §5 regions of Phases 2–6, and the cited evidence. I searched the whole target for equivalent
world-transition cleanup, generation validation, resize-facade, and Java visibility rules before
settling either candidate. Only after those judgments did I read
`docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_14.md`, in order and last. Round 14 introduced the resize seam;
its resolution does not settle either newly exposed defect.

There were no reading-order deviations, no network use, and no forbidden-source use. There was no
agent fan-out: this was the canonical engine's already-dispatched atomic adjudication role, so the
supplied `verify-loop` skill required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — Pending resize state is not authenticated to a world incarnation

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1230`–`:1264`
- **Claim:** Resize observations and their retained attachment-epoch state cannot distinguish two
  world objects that reuse the same numeric `DimensionKey`, so stale state can be applied or
  coalesced into the later world's pipeline.
- **Evidence:** Phase 7 explicitly states that a new world object increments `worldEpoch` even when
  its numeric dimension repeats (`docs/phase7/v1/PHASE_7_DOC.md:782`–`:785`), and
  `FrameBeginSignal` carries that identity (`docs/phase7/v1/PHASE_7_DOC.md:1089`–`:1092`). By
  contrast, all three `ResizeObservation` variants carry only `DimensionKey` plus extent or
  attachment epoch (`docs/phase7/v1/PHASE_7_DOC.md:1230`–`:1238`), and boundary application likewise
  receives only `DimensionKey` (`docs/phase7/v1/PHASE_7_DOC.md:1226`–`:1229`). The sink retains and
  coalesces its newest values per dimension, but its rejection algebra has no world-generation
  mismatch and the prose defines no atomic reset of pending observations or attachment-epoch state
  on world replacement (`docs/phase7/v1/PHASE_7_DOC.md:1253`–`:1264`). H-WORLD-01's generic “clear
  temporal world state” action (`docs/phase7/v1/PHASE_7_DOC.md:965`) does not bind the resize sink's
  queue or epoch tracker and therefore cannot close the exposed contract by implication.
- **Required correction:** Authenticate observation and boundary application with `worldEpoch` or an
  equivalent active-publication generation and reject mismatches. Alternatively, explicitly bind
  world transition/unload to an atomic clearing of all pending resize observations and resetting of
  per-dimension attachment-epoch state before a reused `DimensionKey` can become active.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the repair changes the resize contract or its
  binding lifecycle semantics inside the manifest-selected §5 region.

### candidate-002 — The resize hook bridge cannot call its package-private sink

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1226`–`:1265`
- **Claim:** The declared cross-package hook flow is not implementable in Java because
  `mod.glue.frame` must submit resize observations through top-level types that are package-private
  in `engine.frame`.
- **Evidence:** The package layout places `FrameHookBridge` in
  `com.schmaloogium.mod.glue.frame`, distinct from `com.schmaloogium.engine.frame`
  (`docs/phase7/v1/PHASE_7_DOC.md:245`–`:255`). The target also declares `FrameHookSink` the sole
  hook-facing engine surface and lists operations that contain no resize submission
  (`docs/phase7/v1/PHASE_7_DOC.md:268`–`:274`). Nevertheless, `ResizeLifecycleSink`,
  `ResizeObservation`, its result types, and rejection enum are declared without `public`
  (`docs/phase7/v1/PHASE_7_DOC.md:1226`–`:1256`), while the binding prose requires
  `mod.glue.frame` to submit H-RESIZE/H-FBO observations and interpret `Recorded(true)`
  (`docs/phase7/v1/PHASE_7_DOC.md:1258`–`:1265`). No equivalent public facade exists. This is a
  concrete accessibility failure, not a request to expose the internal coordinator or the
  frame-boundary application operation.
- **Required correction:** Publish a narrow engine-owned facade callable by `mod.glue.frame`, or add
  an equivalent resize-observation operation to `FrameHookSink`, with public observation/result
  vocabulary and exact glue obligations. Keep `applyPendingAtFrameBoundary` and
  `PipelineCoordinator` internal because `FrameDriver` owns boundary application.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the callable hook-to-engine contract is in the
  manifest-selected §5 interface region.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The conformance map covers the assigned
frame flow, program families, hook needs, engine flags, reference-timeline rows, and Appendix-E
owner/deferral rows. The consumed Phase 2–6 contracts remain consistent for the reviewed uses. The
resize seam otherwise gives coherent render-thread hook order, Phase 6-before-Phase 5 boundary
placement, extent/epoch coalescing, mutation-free rejection, safe-boundary rebuilding, and
mid-frame abort behavior. `Recorded(true)` is sufficiently closed by the existing abort result
algebra and §6 degradation rules; no separate finding is warranted.

No supplied candidate was cleared on independent re-derivation. Prior reviews do not settle either
finding: Round 14 established the previously missing scheduling seam, but its fix introduced the
dimension-only identity and package-private declarations adjudicated here.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are localized, correction-level interface defects; neither requires rebuilding the
Phase 7 architecture. The recent correction trend is 3, 2, 1, 2 across Rounds 12–15, so corrections
are no longer strictly decreasing and the loop has not converged. Apply both corrections in one
scoped fix-up, record their resolutions and the Phase 7 addendum, then run a fresh whole-document
and interface verification round before Phase 7 can close. Literal PASS remains required for
closure.

## Resolutions

### candidate-001 — applied

Re-derived from the target's world-incarnation rule and resize state machine. Every public resize
observation now carries `worldEpoch`; internal boundary application receives the accepted
frame-begin epoch; retained state is keyed by `(worldEpoch, dimension)`; and both paths reject an
epoch other than the active world publication as mutation-free `STALE_WORLD_EPOCH`. Numeric
`DimensionKey` reuse therefore cannot admit or apply a prior world's pending resize state.

### candidate-002 — applied

Published `ResizeObservationPort`, `ResizeObservation`, `ResizeObservationResult`, and
`ResizeLifecycleRejection` as the narrow engine surface callable from `mod.glue.frame`, and made
glue's obligation to handle every closed result explicit. `ResizeLifecycleSink`,
`ResizeBoundaryResult`, boundary application, and `PipelineCoordinator` remain internal.

### Notes deferred

None.
