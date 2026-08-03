# Phase 8 — Shadow pass — Verification Review 3

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates against the complete Phase 8 target,
`docs/design/v2.0-RC3/DESIGN.md` Part I and the Phase 8 assignment/doc gate, the binding §5
contracts of Phases 4–7, and the cited permitted evidence. Only after settling both judgments did I
read `docs/phase8/reviews/PHASE_8_REVIEW_1.md` and
`docs/phase8/reviews/PHASE_8_REVIEW_2.md`, including their resolutions, last.

There were no deviations from the resolved source contract. I did not use the network, invoke the
verification harness, start another Codex process, or fan out to agents. I did not read forbidden
sources. The Gate reported no drops, and no candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — The generation-scoped shadow slot has no contractually available close operation

- **Location:** `docs/phase8/v1/PHASE_8_DOC.md:246`–`:259`, `:943`–`:955`, `:1051`–`:1059`
- **Claim:** Phase 7 cannot implement the required rollback and publication-teardown lifecycle
  through the exposed contracts because its generation-scoped shadow product has no close
  operation.
- **Evidence:** Successful `ShadowPassFactory.create` returns only
  `ShadowPassBuildResult.Ready(ShadowInvocationSlot)`
  (`docs/phase8/v1/PHASE_8_DOC.md:246`–`:259`), while the binding dependency defines that slot with
  only `invoke(ShadowInvocationContext)` (`docs/phase7/v1/PHASE_7_DOC.md:1178`–`:1180`). Phase 8
  nevertheless requires Phase 7 to close the slot during reload/shutdown, invalidating its epoch
  and releasing retained service references (`docs/phase8/v1/PHASE_8_DOC.md:943`–`:955`), and R8-4
  requires it in reverse-order rollback/close (`:1058`). The target provides no equivalent
  lifecycle owner, teardown token, or factory cleanup operation. Prior reviews settled adjacent
  execution-view, borrowed-binding, and identity issues, but not this missing lifecycle capability.
- **Severity:** correction. The architecture is intact, but the lifecycle contract needs a bounded
  public-surface repair.
- **Required correction:** Expose an explicit generation-scoped, idempotent close capability,
  either by requesting it on the Phase 7-owned slot or by returning a closeable publication owner
  containing the slot. Align the factory result, lifecycle state machine, cleanup ordering, and
  R8-4 on render-thread behavior, closing from permitted states, `INVOKING` handling, epoch
  invalidation, retained-reference release, and reverse-order rollback.
- **Touches interface/change-trigger region:** yes.

### candidate-002 — ShadowPolicy validation admits color buffers into the depth-only PCF contract

- **Location:** `docs/phase8/v1/PHASE_8_DOC.md:326`–`:327`, `:507`–`:513`, `:899`–`:903`
- **Claim:** The public PCF policy admits logical buffers for which no PCF behavior is defined,
  allowing Phase 7 to construct a policy that passes Phase 8 validation but cannot be consumed
  under the Phase 5 depth-compare contract.
- **Evidence:** `ShadowPcfPolicy` calls its members `compareDepthBuffers`
  (`docs/phase8/v1/PHASE_8_DOC.md:326`–`:327`), but the explicit validation accepts depth 0/1 and
  color 0/1 for both mipmap and PCF sets (`:507`–`:513`). The detailed PCF design says hardware
  compare mode and legacy swizzle are applied per depth texture (`:899`–`:903`); it defines no
  color-buffer PCF meaning. No other target rule narrows PCF membership or defines color members as
  a no-op. Because §5.1 exposes the exact policy fields and validation to Phase 7 and Phase 2
  (`:983`–`:989`), this is consumer-visible rather than merely a naming issue.
- **Severity:** correction. A depth-only admissibility rule is a local semantic fix.
- **Required correction:** Restrict `ShadowPcfPolicy` to shadow depth 0 and shadow depth 1, either
  through a dedicated depth-buffer type or explicit validation, and make color membership produce
  the existing deterministic invalid-policy outcome. Align §4.1 and §5.1 on that invariant.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

The round-2 corrections are present: Phase 7 solely opens/closes the execution bridge around
invocation; Phase 8 does not close Phase 5's borrowed binding snapshot; and the complete effective
`ShadowPolicy` consistently supplies the configuration-derived portion of plan identity. The
Phase 4 and Phase 6 consumed contracts remain aligned, and the requested R8-1/R8-2 surfaces remain
clearly flagged rather than assumed. The conformance map, shadow directives, buffers, samplers,
uniforms, camera math, traversal, draw order, depth split, mipmaps, blob suppression, cloud
behavior, and hook ledger remain substantively covered in the finder-reported clean areas.

Neither candidate is refuted, cleared, duplicative of prior settled material, or dropped on
re-derivation. Candidate-001 is the still-missing close capability itself, not the previously
corrected execution-view lifecycle. Candidate-002 is a distinct newly identified public-policy
domain contradiction.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both candidates are admitted as corrections; neither is blocking or a note. Both repairs affect
the declared §5 cross-phase-interface contract, so the interface change trigger fires and a fresh
verify round is required before Phase 8 can close.

The correction count improves from five in round 1 to three in round 2 to two in round 3, with each
prior round's resolutions present, so the trend is convergent but has not reached literal PASS.
FAIL is not warranted because both remaining defects are bounded fix-ups rather than structural
misses.

Next action: perform the governed fix-up for this review, append its `## Resolutions`, add the next
§0 addendum to the Phase 8 document, and run a fresh verification round because the binding
interface region must change.

## Resolutions

### candidate-001 — corrected

Re-derived the missing teardown capability from Phase 8's factory result and Phase 7's publication
ownership. `Ready` now returns a generation-scoped `ShadowPassPublication` that exposes the slot
and an idempotent render-thread `close()`. The lifecycle, cleanup sequence, §5.1 public surface, and
R8-4 now agree: close succeeds from `READY` or `DISABLED_RUNTIME`, invalidates the slot epoch,
releases retained service references, and rejects without mutation during `INVOKING`, which Phase 7
must finish or abort first. Phase 7 owns and reverse-closes the publication rather than being asked
to close an invocation-only dependency interface.

### candidate-002 — corrected

Re-derived the policy domains from the separate mipmap and hardware-compare behaviors. Mipmap
membership remains the four-buffer v0.2 shadow inventory, while
`ShadowPcfPolicy.compareDepthBuffers` admits only shadow depth 0 and shadow depth 1. §4.1, §4.9,
and the §5.1-exposed policy now make any shadowcolor PCF member produce the existing deterministic
invalid-policy outcome.

### Notes deferred

None. The adjudicator admitted no notes.
