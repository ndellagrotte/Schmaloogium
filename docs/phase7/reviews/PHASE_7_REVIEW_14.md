## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH material, the binding §5
regions of Phases 2–6, and the cited supporting evidence. Only after settling those judgments did I
read `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_13.md`, in order and last. Those prior reviews materially affect
candidate-002: Rounds 6 and 8 already adjudicated and cleared the same proposed external shutdown
seam, and the current target preserves the settled internal ownership and sequencing.

There were no reading-order deviations, no network use, no forbidden source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
`verify-loop` instructions required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — Resize and attachment observations have no declared scheduling seam

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1184`–`:1219`
- **Claim:** The binding interface does not specify an implementable route from the resize and
  framebuffer hooks to the required safe-boundary rebuild/publication while its internal
  `PipelineCoordinator` capability is restricted to the reload controller and bootstrap.
- **Evidence:** The hook catalog sends actual framebuffer dimensions to a named
  `ResolutionCoordinator` and requires the framebuffer allocation hook to increment an attachment
  epoch and schedule a rebuild (`docs/phase7/v1/PHASE_7_DOC.md:956`–`:958`). The lifecycle then says
  “the coordinator” records the newest actual extent and performs one candidate rebuild/publication
  at the next frame boundary (`docs/phase7/v1/PHASE_7_DOC.md:964`–`:967`). Section 5, however,
  declares only an internal `PipelineCoordinator` visible to `ShaderReloadController` and bootstrap,
  with build/publication and status operations but no observation or scheduling operation
  (`docs/phase7/v1/PHASE_7_DOC.md:1184`–`:1188`). The public frame signal carries a dimension and two
  extents (`docs/phase7/v1/PHASE_7_DOC.md:1084`–`:1087`), but it carries no attachment epoch and does
  not define how the out-of-frame H-RESIZE/H-FBO observations reach the one-shot safe-boundary
  scheduler. The closed reload reason vocabulary likewise has no actual-display/FBO-resize or
  attachment-epoch reason (`docs/phase7/v1/PHASE_7_DOC.md:1216`–`:1218`). The failure table requires
  a refresh/observer result to schedule the rebuild (`docs/phase7/v1/PHASE_7_DOC.md:1550`–`:1553`),
  but no such Phase-7-owned callable/result contract is declared in §5.
- **Required correction:** Keep direct publication inaccessible to Phase 12, but define in §5 the
  internal frame/resize lifecycle seam that accepts typed dimension, actual-FBO-extent, and
  attachment-epoch observations and coalesces them into one safe-boundary rebuild/publication.
  Specify its thread, ordering, duplicate/coalescing, and closed rejection/failure behavior. An
  explicit route through `ShaderReloadController` is also acceptable only if its closed request and
  reason vocabulary is extended for these signals and the render-thread handoff is specified.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the missing hook-to-orchestrator contract
  belongs in the manifest-selected §5 interface region.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. `ReloadIntent.Select` consistently requires
`PACK_SELECTION`, `RebuildActive` forbids it and reuses manager-owned configuration, and Phase 12
cannot directly publish through the now-internal `PipelineCoordinator`. The contract map, hook
catalog, assigned OQ-3/OQ-4 spikes, Appendix E ledger, dependency ownership and publication rules,
frame flow, program dispatch, and mandatory document structure yielded no other candidate-backed
defect.

`candidate-002` is dropped. Phase 7 hosts `CaptureAgent` in `:mod` and explicitly assigns it the
post-commit sequence: atomically write the image, serialize the manifest, acknowledge the shot, and
schedule the next shot or H-CAPTURE-02 shutdown on the client thread
(`docs/phase7/v1/PHASE_7_DOC.md:1038`–`:1046`). H-CAPTURE-02 identifies the clean Minecraft shutdown
entry, main-thread scheduling, post-final-artifact-commit authorization, and no-timeout-kill posture
(`docs/phase7/v1/PHASE_7_DOC.md:959`–`:962`). That is an encapsulated Phase-7-owned implementation
responsibility of the mod-side capture agent, not a call made by an external Phase 2 component;
therefore a separate public shutdown request/result contract in §5 would invent an unnecessary
consumer seam. This is the same issue settled on independent re-derivation in Rounds 6 and 8, and
no intervening target change has invalidated that disposition.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted resize-routing omission is bounded and correction-level; the architecture already
defines the intended safe-boundary behavior, so it does not require a structural rebuild. Its fix
must change the declared cross-phase interface region, which triggers another fresh verification
round before Phase 7 can close.

The recent correction trend is 3, 3, 2, 1 across Rounds 11–14. It is decreasing, but literal
convergence has not been reached because one interface correction remains. The next required action
is a scoped fix-up of this review, including its `## Resolutions` record and Phase 7 addendum,
followed by fresh whole-document and interface verification. Literal PASS remains required for
closure.

## Resolutions

### candidate-001 — applied

Added the internal `ResizeLifecycleSink` contract in `docs/phase7/v1/PHASE_7_DOC.md` §5.1. Its
closed observation algebra separately carries typed dimension, display extent, actual framebuffer
extent, and attachment epoch from H-RESIZE-02/H-RESIZE-01/H-FBO-01; its boundary operation is
callable only by the frame/reload glue. The accompanying rules require render-thread hook order,
Phase-6-sampling-before-Phase-5-resize/clear boundary order, identical-observation no-op behavior,
newest-value/strictly-greatest-epoch coalescing into exactly one internal coordinator rebuild, and
mid-frame epoch abort followed by vanilla continuation. Rejections and failures are closed,
mutation-free, and publish nothing. Direct `PipelineCoordinator` access remains unavailable to
Phase 12 or any external consumer. The compact §0.18 addendum records the interface change, which
requires a fresh verification round.

### Notes deferred

None; the adjudicator admitted no notes.
