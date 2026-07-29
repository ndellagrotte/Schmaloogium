# Phase 5 adversarial review — round 10

## 0. Method and reading order

I independently re-derived all three surviving candidates against the complete target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, and the binding §5 regions of the Phase 1, 3, and 4
dependencies. I checked the supplied supporting evidence where needed. Only after settling those
judgments did I read `docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_9.md`, including their resolutions.

I used no network access, no subagents or other agent fan-out, and no forbidden source. In
accordance with the already-dispatched atomic role and the verify-loop skill, I did not invoke the
verification loop, run `scripts/verify`, or start another Codex session. The only
reading-contract deviation was that the supporting
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` was not opened because the supplied
forbidden-source rule bars `*.txt`; it was unnecessary to adjudicate these lifecycle defects.
There were no candidates eliminated before adjudication and no Gate drops.

## 1. Findings

### candidate-001 — Same-extent mid-frame refresh has contradictory recovery semantics

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:924`–`:939` and
`docs/phase5/v1/PHASE_5_DOC.md:1355`

**Claim.** The newly exposed `Reattached` result does not define one consistent lifecycle when a
same-extent main-depth version change is detected between snapshot creation and a shader draw.
The refresh path completes in-place recovery and returns `Reattached`, while the failure table
requires every mid-frame version change to abort/normalize and rebuild before another draw. It
also closes the caller's open pass snapshot without telling Phase 7 whether to reacquire and
continue or abandon the frame.

**Evidence.** The same-extent path closes every open pass snapshot, reattaches affected FBOs,
invalidates depth copies, forces a full clear, advances `depthAttachmentEpoch`, and returns
`Reattached` (`docs/phase5/v1/PHASE_5_DOC.md:924`–`:939`). A closed snapshot cannot later be
completed: every mutating method rejects a stale epoch or frame token, and `completePass` accepts
exactly one open snapshot (`docs/phase5/v1/PHASE_5_DOC.md:413`–`:415`). In contrast, §6 states
without a same-extent exception that a main-depth version change mid-frame must reject the stale
snapshot, abort/normalize the frame, and force rebuild/full clear before the next shader draw
(`docs/phase5/v1/PHASE_5_DOC.md:1355`). The recorded-GL test checks the reattachment mechanics but
does not place refresh after snapshot creation or establish the caller transition
(`docs/phase5/v1/PHASE_5_DOC.md:1447`–`:1449`).

**Required correction.** Select one same-extent mid-frame policy. If successful `Reattached`
permits same-frame continuation, narrow §6 and require Phase 7 to abandon the invalidated
snapshot and reacquire current pass and binding snapshots before drawing. Otherwise require
abort/rebuild and make `Reattached` non-resumable. Reflect the selected obligation in §5.1 and add
a recorded-GL test with refresh after snapshot creation and before draw.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-002 — `Failed` does not define Phase 7's no-draw recovery transition

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:934`–`:939` and
`docs/phase5/v1/PHASE_5_DOC.md:1259`

**Claim.** The public `Failed(BufferFailure)` outcome groups unavailable current depth and
reattachment failure but does not tell Phase 7 what transition is mandatory or when another
shader draw becomes legal. Section 5 gives the complete no-draw recovery protocol only for
`ResizeRequired`.

**Evidence.** `refreshMainDepth` runs both before `beginFrame` and before each shader draw, and an
unavailable snapshot or reattachment failure returns the same `Failed(BufferFailure)` with only a
general reference to §6 (`docs/phase5/v1/PHASE_5_DOC.md:934`–`:939`). The binding row specifies
abort/normalize plus prepare/build/publication before drawing only for `ResizeRequired`
(`docs/phase5/v1/PHASE_5_DOC.md:1259`). Section 6 maps main-depth unavailability to shaders off
and a mid-frame version change to abort/rebuild, but it does not define a single caller action for
a returned reattachment failure, including one encountered before frame entry
(`docs/phase5/v1/PHASE_5_DOC.md:1353`–`:1355`). The acceptance plan tests `Reattached` and
`ResizeRequired`, not either `Failed` caller protocol
(`docs/phase5/v1/PHASE_5_DOC.md:1447`–`:1452`).

**Required correction.** Define the permitted `Failed` codes and a Phase 7 transition for each,
or one conservative transition for every `Failed`: abort/normalize any active frame, perform no
further shader draw, and remain on or publish the vanilla/shaders-off path until the prescribed
safe-point recovery succeeds. Put the obligation in §5.1 and test unavailable-current-depth and
injected reattachment-failure outcomes.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-003 — Reattachment `Failed` has no safe estate postcondition

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:924`–`:939`

**Claim.** Independently of the caller transition in candidate-002, a handled same-extent
reattachment failure has no transaction postcondition. Phase 7 cannot know whether attachments
were rolled back, the estate was poisoned or made stale, or its cached depth identity and epoch
remain usable.

**Evidence.** Same-extent refresh is a multi-step mutation: it closes snapshots, reattaches
multiple FBOs, then rechecks completeness, while invalidating copies and advancing the epoch only
in later steps (`docs/phase5/v1/PHASE_5_DOC.md:924`–`:932`). Nevertheless any reattachment
failure merely returns `Failed(BufferFailure)` (`docs/phase5/v1/PHASE_5_DOC.md:934`–`:939`).
Unlike the adjacent `ResizeRequired` contract, which guarantees no GL call and no mutation
(`docs/phase5/v1/PHASE_5_DOC.md:941`–`:948`), neither §4.8 nor §6 defines rollback, cached
identity, epoch, staleness, or usability after an expected completeness/reattachment failure.
The generic publish-off rule is limited to an unexpected backend exception
(`docs/phase5/v1/PHASE_5_DOC.md:1364`–`:1365`).

**Required correction.** Define expected reattachment-failure transaction semantics in §4.8 and
expose the resulting guarantee in §5.1: either roll all modified attachments and cached state back
atomically, or mark the estate stale/unusable and require replacement or off publication. State
the epoch and cached-identity result, prohibit draws until recovery, and add recorded-GL failures
at the first, middle, and final reattachment/check steps, including refresh before `beginFrame`.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The new `ResizeRequired` carrier is
consistent across its declaration, lifecycle, §5.1 contract, and recorded-GL oracle: it uniquely
carries `MAIN_DEPTH_RESIZE_REQUIRED`, performs no GL or estate/frame/epoch mutation, and blocks
drawing until replacement publication. The consumed Phase 3 and Phase 4 contracts match their
binding regions. Phase 5 honestly records the three unavailable Phase 1 operations as requested
dependency changes rather than assuming them. The App B.1/B.2/B.4 conformance map, complete fixed
App B.3 table including depthtex1 at unit 11, flip and clear machines, depth-copy tiers, shadow
estate, sizing, fallback, and growth posture are otherwise supported.

No admitted findings are duplicates. Candidate-002 concerns the consumer's mandatory no-draw
transition after the closed `Failed` result. Candidate-003 concerns the producer estate's
mutation, epoch, identity, and usability postcondition after a failure partway through the
multi-FBO operation; specifying only either side would leave the other unimplementable.
Candidate-001 is separate because it concerns the successful `Reattached` outcome and the
conflict between in-place continuation and blanket mid-frame rebuild.

Prior reviews do not settle these findings. Round 9 introduced and then resolved the public
refresh carrier, precise `ResizeRequired` semantics, and Phase 7's resize-required recovery. Its
resolution did not select the successful same-extent mid-frame policy or define either the
caller transition or estate postcondition for `Failed`. No candidate was cleared on
re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

Three localized correction-level findings are admitted. They require lifecycle and contract
clarification, not structural rebuilding, so `FAIL` is not warranted. All three affect the
Phase-7-facing refresh contract in §5.1 and therefore activate the declared interface change
trigger.

Round 9 had one correction and introduced the refresh surface now under review; this round finds
three defects in that newly changed interface, so literal convergence has not been reached. The
next required action is a scoped fix-up resolving candidates 001, 002, and 003 and appending
resolutions to this review. Because the fix-up must change or clarify §5, a fresh verification
round is required before Phase 5 can close.

## Resolutions

### candidate-001 — resolved

Selected same-frame continuation after successful same-extent reattachment. Section 4.8 now
advances the attachment epoch before mutation, makes `Reattached` invalidate the open pass and
binding snapshots, and requires Phase 7 to abandon rather than complete the invalidated pass,
reacquire both snapshots, and only then draw. Section 6 narrows its former blanket mid-frame
rebuild rule to this transition, §5.1 exposes the consumer obligation, and §8.3 places the
recorded-GL refresh between snapshot creation and draw.

### candidate-002 — resolved

All `Failed` results now use one conservative Phase 7 transition: abort/normalize an active frame,
publish shaders off, perform no further shader draw, and wait for successful safe-point
prepare/build/publication. The rule applies to unavailable current depth before frame entry as
well as mid-frame and to reattachment/check failure. Sections 5.1 and 6 expose the transition,
and §8.3 tests both unavailable-depth timings and injected failures.

### candidate-003 — resolved

Selected a poisoned-estate postcondition instead of rollback. The epoch advances before the
multi-FBO mutation. On an expected failure, the prior cached depth identity remains unchanged,
open snapshots close, the estate becomes stale and unusable, no new snapshot or mutating
operation is accepted, and no partially reattached FBO may be drawn. Section 8.3 injects failure
at the first, a middle, and final reattachment/check step and checks the producer postcondition
and caller recovery.

### Notes deferred

None.
