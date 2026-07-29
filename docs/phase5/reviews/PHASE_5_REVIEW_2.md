# Phase 5 adversarial review — round 2

## 0. Method and reading order

I independently re-derived every candidate against the complete target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, the binding §5 regions of the Phase 1, 3, and 4 dependencies,
and the cited RESEARCH and supporting-evidence material. Only after settling those judgments did I
read `docs/phase5/reviews/PHASE_5_REVIEW_1.md` and its resolutions.

I used no network access, no subagents or other agent fan-out, and no forbidden source. In
accordance with the atomic-role instruction in the verify-loop skill, I did not invoke the
verification loop or start another Codex session. There were no deviations from the supplied
reading contract, no candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — Resize-consumer failure has no complete publisher transition

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:345`–`:349`,
`docs/phase5/v1/PHASE_5_DOC.md:992`–`:998`, and
`docs/phase5/v1/PHASE_5_DOC.md:1008`–`:1021`

**Claim.** The ready-candidate publication statement is correctly limited to driver failure, so
there is no literal claim that publication is universally infallible. A narrower defect remains:
the publisher installs the new estate before invoking synchronous resize consumers, but its
lifecycle defines only pre-publication failure. It does not define the publisher state after a
consumer fails, ownership and disposal of the installed estate, whether remaining consumers
receive that generation, or the generation accounting for the consequent shaders-off
publication.

**Evidence.** The ordering contract says the ready Phase 5 candidate is published after Phase 4
and that the no-GL swap *"cannot fail for a driver reason"*
(`docs/phase5/v1/PHASE_5_DOC.md:345`–`:349`). The resize protocol then installs the estate, invokes
consumers, and on failure keeps drawing gated and publishes shaders off
(`docs/phase5/v1/PHASE_5_DOC.md:992`–`:998`). The lifecycle enumerates
`READY_UNPUBLISHED -> PUBLISHED -> REPLACED -> CLOSED`, but gives a failure transition only for
*"pre-publication failure"* (`docs/phase5/v1/PHASE_5_DOC.md:1008`–`:1017`). General idempotent
close, stale-view, and off-generation rules (`docs/phase5/v1/PHASE_5_DOC.md:1018`–`:1021`) provide
safety primitives, not the missing post-install transition or callback-delivery result.

**Required correction.** Extend the Phase 5 publisher state machine and exposed result contract
for post-install consumer failure. Specify installed-estate replacement/disposal, generation and
notice accounting, whether dispatch continues, the precise resulting off publication, and the
recovery signal returned to Phase 7. Leave Phase 7's registry replacement or rollback policy to
Phase 7.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-002 — Resize registration is absent from the declared publisher API

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:269`–`:275`,
`docs/phase5/v1/PHASE_5_DOC.md:992`–`:998`, and
`docs/phase5/v1/PHASE_5_DOC.md:1127`

**Claim.** Phase 5 exposes resize-consumer registration to Phases 13 and 14, but the illustrative
signatures that define the cross-phase contract omit `addResizeConsumer`, and the returned
registration handle has no named type or removal operation.

**Evidence.** `BufferEstatePublisher` declares only `current`, `publish`, and `publishOff`
(`docs/phase5/v1/PHASE_5_DOC.md:269`–`:275`). Later prose introduces
`BufferEstatePublisher.addResizeConsumer(BufferResizeConsumer)` and an idempotent registration
handle (`docs/phase5/v1/PHASE_5_DOC.md:992`–`:998`), while binding §5 exposes only the unnamed
phrase *"registration handle"* to downstream consumers
(`docs/phase5/v1/PHASE_5_DOC.md:1127`). This does not meet the mandatory named-interface contract
(`docs/design/v2.0-RC3/DESIGN.md:811`–`:812`).

**Required correction.** Add `addResizeConsumer(BufferResizeConsumer)` to
`BufferEstatePublisher`, define its named return type and idempotent removal/close operation, state
the render-thread constraints, and use the same identifiers consistently in §§4.11.2 and 5.1.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-003 — Color-clear planning has no execution boundary

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:297`–`:303`,
`docs/phase5/v1/PHASE_5_DOC.md:721`–`:728`, and
`docs/phase5/v1/PHASE_5_DOC.md:1122`–`:1124`

**Claim.** Phase 5 owns color-clear policy and describes restoration performed by its clear plan,
but its public surface exposes only `clearPlan(ClearRequest)`. Neither the signatures nor binding
§5 identify an executor or give Phase 7 a complete GL-neutral command protocol to execute.

**Evidence.** The estate API returns `ClearExecutionPlan` but has no clear execution operation
(`docs/phase5/v1/PHASE_5_DOC.md:297`–`:303`). Detailed design assigns Phase 7 the depth-clear
moment and Minecraft state bracketing, while saying Phase 5's clear plan clears color attachments
and restores framebuffer binding and viewport (`docs/phase5/v1/PHASE_5_DOC.md:721`–`:728`).
Binding §5 exposes only the request and plan data, unlike the adjacent explicit `copyDepth`
operation (`docs/phase5/v1/PHASE_5_DOC.md:1122`–`:1124`). A consumer must therefore guess whether
the plan is side-effecting or how it is translated and restored.

**Required correction.** Define the execution boundary in the public shape and §5: either expose
a render-thread Phase 5 clear executor with frame/generation validation and guaranteed state
restoration, or explicitly assign execution to Phase 7 and specify a complete ordered GL-neutral
command model and caller obligations. Amend shadow clearing only if it shares this unresolved
mechanism.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-004 — The conformance map omits mandatory composite blend disabling

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:366`

**Claim.** The App B.1 row maps the simultaneous-read/write artifact diagnostic but does not map
the independent rule that blending is disabled while composite passes write color attachments.

**Evidence.** The authoritative contract states both requirements separately:
*"Blending is disabled while composites write color attachments; reading a buffer the same
composite writes produces artifacts"* (`docs/research/v1/RESEARCH.md:1212`–`:1214`). The target
row records only a read/write-intersection diagnostic and says Phase 7/4 own blend state
(`docs/phase5/v1/PHASE_5_DOC.md:366`). The governing doc gate requires every App B.1 row in the
conformance map (`docs/design/v2.0-RC3/DESIGN.md:1676`–`:1680`).

**Required correction.** Correct the §3.1 row to state both obligations: Phase 7/4 must disable
blending while a composite pass writes color attachments, and Phase 5 supplies the separate
read/write-intersection diagnostic. The existing stage/pass and program-state contracts already
identify the execution owners, so this candidate does not independently establish a missing
Phase 5 API or require a §5 change.

**Severity:** correction

**touches interface/change-trigger region: no**

### candidate-005 — Final handoff omits anaglyph-aware color masking

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:188`–`:193` and
`docs/phase5/v1/PHASE_5_DOC.md:385`

**Claim.** The governing Final contract couples rendering to Minecraft's framebuffer with
anaglyph-aware color masking. Phase 5's handoff to Phase 7 communicates only the framebuffer
destination, so a conforming consumer could omit the required mask.

**Evidence.** RESEARCH requires *"Final renders to the vanilla framebuffer (anaglyph-aware color
masking)"* (`docs/research/v1/RESEARCH.md:526`). The target's boundary requires only that Phase 7
bind Minecraft's framebuffer (`docs/phase5/v1/PHASE_5_DOC.md:188`–`:193`), and its conformance row
likewise maps only the `SCREEN` terminal and platform bind
(`docs/phase5/v1/PHASE_5_DOC.md:385`). Phase 1 confirms that color-mask execution belongs to
Phase 7 and is not supplied by its facade (`docs/phase1/v14/PHASE_1_DOC.md:3986`); that ownership
does not remove the Phase 5 target specification's required Final handoff.

**Required correction.** Extend the `SCREEN`/Final contract and binding §5 handoff to require
Phase 7 to apply the anaglyph-aware color mask when drawing Final to Minecraft's framebuffer,
while leaving Minecraft-facing state execution to Phase 7. Update the conformance row with the
authoritative RESEARCH provenance.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. In particular, the revised shadow
surface consistently uses generation/epoch/token-checked bind, clear, split-copy, completion, and
abort operations without taking Phase 8's scheduling role. The Phase 13 overlay seam preserves
typed absence, ordering, and ownership. The App B.3 fixed unit map retains `depthtex1` at unit 11
and does not introduce dynamic allocation. App B.2 depth/shadow contents, all 37 App B.4 formats
and transfer vocabularies, resize sizing, depth bridge/copy tiers, real shadow flips, growth
posture, RGBA fallback, and the main flip/clear state machines are otherwise coherent.

Consumed Phase 1, 3, and 4 names resolve to their binding regions. The target continues to flag
unavailable Phase 1 operations as requested changes rather than assuming them. Registry
fingerprint pairing, Phase 4/Phase 5 publication ownership, stale-view containment, fixed texture
ownership, and the general shaders-off fallback are otherwise sound.

No candidate is dropped on re-derivation. Candidate-001's broad title is narrowed: the
driver-qualified publication statement is not contradicted, but its publisher-transition defect
survives. Candidate-004 survives as a conformance-map correction but does not require an interface
change. Round 1's four findings are settled by its recorded resolutions; this review does not
reopen them. The present findings concern omissions or ambiguities introduced or exposed by that
fix-up, plus incomplete conformance details in the revised surface. There were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=5; notes=0
Interface changed: yes

All five admitted findings are localized corrections; none requires structural rebuilding, so
`FAIL` is not warranted. Candidates 001, 002, 003, and 005 require changes to the declared
cross-phase contract or its change-trigger region. Candidate-004 is a conformance-map-only
correction.

The loop has not converged: the correction count increased from four in Round 1 to five in Round
2, with several findings on the newly revised interface surface. The next required action is a
scoped fix-up resolving all five findings, appending resolutions to this review, and updating the
Phase 5 addendum. Because the fix-up must change §5 or an equivalent declared interface contract,
the interface change-trigger applies and a fresh verification round is required before Phase 5
can close.

## Resolutions

### candidate-001 — resolved

Re-derived the gap against the publisher and lifecycle rather than treating the review as
authority. Section 4.11 now defines an unopened installed generation, ordered dispatch stopping at
the first failure, immediate replacement by a separately numbered shaders-off publication,
eventual disposal of the failed estate, partial-delivery accounting, and the precise
`ConsumerFailed` recovery signal returned to Phase 7. Phase 7 retains registry replacement and
retry policy. The two-generation rule removes ambiguity without retracting the narrower
driver-failure statement.

### candidate-002 — resolved

Added `addResizeConsumer` to `BufferEstatePublisher` and named
`BufferResizeRegistration`, `BufferResizeConsumer`, and `ResizeConsumerResult` in the public shape.
Registration, idempotent removal, callbacks, and their non-reentrant safe point are explicitly
render-thread-only. Sections 4.11.2 and 5.1 now use the same identifiers.

### candidate-003 — resolved

Chose the Phase-5 executor boundary because Phase 5 already owns the GL facade operations and
restoration claim. `BufferEstateView.executeClear` now accepts a generation/epoch/frame-bound,
exactly-once plan, rejects stale or foreign plans before GL, executes only ordered color batches,
and guarantees framebuffer/viewport restoration on success or failure. Phase 7 still owns the
clear moment, depth clear, and Minecraft state bracketing. No shadow text changed because its
separate operations were already explicit.

### candidate-004 — resolved

The §3.1 conformance row now records both independent App B.1 obligations: Phase 7/4 disable
blending while composites write color attachments, while Phase 5 separately diagnoses a
read/write intersection. No new Phase 5 blend-state API was invented.

### candidate-005 — resolved

Sections 1.2, 1.3, 3.2, and 4.5 now require Phase 7 to apply Minecraft's anaglyph-aware color mask
when the `SCREEN` terminal draws Final to Minecraft's framebuffer, with direct RESEARCH
provenance. Minecraft-facing execution remains outside Phase 5.

### Notes deferred

None. The adjudication admitted no notes, and all five corrections were applied without an
authority conflict or new open design decision.
