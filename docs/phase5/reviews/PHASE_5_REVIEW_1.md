# Phase 5 adversarial review — round 1

## 0. Method and reading order

I independently re-derived every surviving candidate against the complete target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, the binding §5 regions of the Phase 1, 3, and 4 dependencies,
and the cited RESEARCH and supporting-evidence material. Only after that derivation did I check the
discovered-prior-review set; it is empty because this is the first review.

I used no network access, no subagents or other agent fan-out, and no forbidden source. I did not
invoke the verification loop or start another Codex session. There were no Gate drops. The one
candidate eliminated before adjudication, candidate-001, remains cleared on re-derivation as
recorded in §2.

## 1. Findings

### candidate-002 — Phase 13 overlay objects lack a typed supply seam

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:170`–`:171`,
`docs/phase5/v1/PHASE_5_DOC.md:290`–`:299`,
`docs/phase5/v1/PHASE_5_DOC.md:987`–`:1014`, and
`docs/phase5/v1/PHASE_5_DOC.md:1390`–`:1392`

**Claim.** Phase 5 promises to resolve Phase 13 companion, custom, and noise objects through an
overlay contract and tells Phase 13 to supply typed objects, but it defines no typed input,
installed provider, or equivalent publication seam through which those objects reach
`textureBindings`. Phase 13 may own registration, allocation, upload, and destruction, but the
cross-phase handoff consumed by Phase 5 must be implementable.

**Evidence.** The ownership boundary says, *"Phase 5 reserves and resolves their fixed-map slots
through an overlay contract"* (`docs/phase5/v1/PHASE_5_DOC.md:170`–`:171`). The fixed table then
uses `Overlay(...)` for units 2, 3, and 7–10 and assigns unit 15 to the Phase 13 `noisetex` slot
(`docs/phase5/v1/PHASE_5_DOC.md:987`–`:1014`). Yet the only binding operation is
`TextureBindingSnapshot textureBindings(PassBufferSnapshot snapshot);`
(`docs/phase5/v1/PHASE_5_DOC.md:298`), with no overlay argument or separately defined provider.
The downstream handoff still requires Phase 13 to *"supply typed overlay objects for units 2/3,
7–10, and 15"* (`docs/phase5/v1/PHASE_5_DOC.md:1391`). The governing scope leaves custom-texture
binding internals to Phase 13 (`docs/design/v2.0-RC3/DESIGN.md:1658`–`:1660`), which narrows the
fix but does not supply the missing receiving boundary.

**Required correction.** Define a narrow immutable Phase 13 overlay input/provider accepted at the
Phase 5/7 composition boundary. It must cover stage/key lookup, typed absence, opaque bindable
handles, and the generation or fingerprint validity and publication ordering Phase 5 needs when
constructing a pass-coherent `TextureBindingSnapshot`. Leave Phase 13's internal registry,
allocation, ownership, upload, and destruction design to Phase 13.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-003 — `BufferResizeNotice` has no delivery contract

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:947`–`:962`,
`docs/phase5/v1/PHASE_5_DOC.md:1081`, and
`docs/phase5/v1/PHASE_5_DOC.md:1438`–`:1443`

**Claim.** The document defines the notice payload, publication condition, and consumers but no
publisher operation, listener registration, queue, stream, or generation-based polling mechanism.
Consequently Phases 13 and 14 cannot reliably learn of an accepted resize at the required
lifecycle boundary.

**Evidence.** The change table says Phase 5 will *"publish `BufferResizeNotice`"* and defines
`BufferResizeNotice(oldSizing,newSizing,newGeneration,reason)` as immutable and published only
after the new estate is ready (`docs/phase5/v1/PHASE_5_DOC.md:947`–`:962`). Section 5 exposes only
the value's payload and names Phases 13 and 14 as consumers
(`docs/phase5/v1/PHASE_5_DOC.md:1081`). The implementation roadmap nevertheless calls for a
Phase 13 *"resize listener"* (`docs/phase5/v1/PHASE_5_DOC.md:1442`) without specifying the
producer-side endpoint that listener uses. This fails the governing requirement for named
cross-phase interfaces and exact lifecycle semantics
(`docs/design/v2.0-RC3/DESIGN.md:809`–`:813`).

**Required correction.** Define the producer-side delivery or generation-based retrieval
operation in §5, including render-thread and publication ordering, missed-generation/coalescing
semantics, and the gate before a dependent resource is used for drawing. Require acknowledgement
only if the selected mechanism is asynchronous.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-004 — `ShadowEstateView` is not an implementable Phase 8 contract

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:168`–`:169`,
`docs/phase5/v1/PHASE_5_DOC.md:285`–`:300`,
`docs/phase5/v1/PHASE_5_DOC.md:636`–`:641`,
`docs/phase5/v1/PHASE_5_DOC.md:895`–`:921`, and
`docs/phase5/v1/PHASE_5_DOC.md:1079`

**Claim.** Phase 5 names shadow bind, clear, split-copy, and snapshot operations and returns an
`Optional<ShadowEstateView>`, but never defines that view, its unavailable form, operation
inputs/results, or how a successful or failed shadow pass completes the real shadowcolor flip
machine. Phase 8 would have to invent Phase-5-owned lifecycle and transition semantics.

**Evidence.** The target assigns Phase 8 the camera, traversal, pass execution, and copy moment
while stating, *"Phase 5 supplies sfb and its operations"*
(`docs/phase5/v1/PHASE_5_DOC.md:168`–`:169`). The public estate shape returns
`Optional<ShadowEstateView>` (`docs/phase5/v1/PHASE_5_DOC.md:299`) but supplies no definition
comparable to the main-estate API. Section 4.10 merely says to expose operations to bind, clear,
copy shadowtex1, and snapshot shadowcolor sides (`docs/phase5/v1/PHASE_5_DOC.md:895`–`:906`);
§5 repeats those nouns (`docs/phase5/v1/PHASE_5_DOC.md:1079`). Although the generic flip machine
is said to apply to shadowcolor (`docs/phase5/v1/PHASE_5_DOC.md:636`–`:641`), no shadow snapshot,
completion, or failure boundary maps onto it. The governing design explicitly assigns shadow
structure, lifecycle, and real state-machine-tested flips to Phase 5 while leaving pass wiring to
Phase 8 (`docs/design/v2.0-RC3/DESIGN.md:1630`–`:1637`).

**Required correction.** Define `ShadowEstateView` and `ShadowEstateUnavailable`, including
bind/clear/snapshot/copy entry points, inputs, results, and success/failure completion rules.
Explicitly map these operations onto the existing generation, epoch, stale-token, abort, and
generic flip protocol. Retain Phase 8 ownership of camera, traversal, pass order, and the moment it
invokes the shadowtex1 copy.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-005 — The conformance map omits explicit in-scope requirements

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:342`–`:428`,
`docs/phase5/v1/PHASE_5_DOC.md:651`–`:709`, and
`docs/phase5/v1/PHASE_5_DOC.md:923`–`:959`

**Claim.** The detailed design covers clear overrides and batching, main/shadow sizing,
`superSamplingLevel`, resize triggers, and the Final-to-Minecraft-framebuffer handoff, but §3 does
not map those distinct governing requirements. This violates the mandatory zero-unmapped
conformance-map rule; it is a traceability defect, not a missing architecture.

**Evidence.** The mandatory template requires *"every in-scope contract item"* in the conformance
map with *"ZERO unmapped rows"* (`docs/design/v2.0-RC3/DESIGN.md:804`–`:808`). The Phase 5
assignment expressly includes clear overrides, flip-aware clears, and batching
(`docs/design/v2.0-RC3/DESIGN.md:1606`–`:1613`), as well as sizing, supersampling, resize lifecycle,
and the Final handoff (`docs/design/v2.0-RC3/DESIGN.md:1638`–`:1649`). The target implements the
clear behavior and `maxDrawBuffers` batching (`docs/phase5/v1/PHASE_5_DOC.md:690`–`:709`), the
sizing formulas, supersampling ruling, and trigger matrix
(`docs/phase5/v1/PHASE_5_DOC.md:923`–`:959`), and the Final terminal behavior
(`docs/phase5/v1/PHASE_5_DOC.md:651`–`:657`). Section 3's existing rows and mechanism-disposition
table do not map each of those governing requirements to those design elements.

**Required correction.** Add explicit §3 conformance rows for clear overrides and
`maxDrawBuffers` batching; main and shadow sizing; `superSamplingLevel`; resize/recreate triggers
and the owned-object invalidation checklist; and Final rendering to Minecraft's framebuffer. Each
row must cite its governing requirement and exact detailed-design realization.

**Severity:** correction

**touches interface/change-trigger region: no**

## 2. Checked and clean

The dependency gate and consumed Phase 1/3/4 contracts are coherent. Phase 5 honestly flags the
Phase 1 facade operations it still needs rather than silently assuming them. The main-estate
frame/pass snapshot and stale-token rules, fixed App B.3 unit table including unit 11, borrowed
main-depth SPI, format vocabulary and fallback, depth-copy tiers, clear/flip state machines,
frame-end rebase decision, fog-alpha rule, and cardinality-independent buffer identities are
substantive and consistent with the selected governing material.

All thirteen mandatory sections are present and substantive. There are no assigned open questions.
The selected document gate is otherwise met: Appendix B.1/B.2/B.4 semantics are architected, the
fixed unit map is complete, depthtex0 has an explicit 1.12.2 bridge, and shadow flips are real
rather than the Pintonium stub.

Candidate-001 remains cleared. The document already specifies exact registry-fingerprint
acceptance for the candidate, caller ownership on rejection, ready-candidate composition ordering,
and a no-GL Phase 5 publication after Phase 4 publication
(`docs/phase5/v1/PHASE_5_DOC.md:240`–`:278`,
`docs/phase5/v1/PHASE_5_DOC.md:326`–`:338`). A separate public provenance-preflight method is not
required by the settled material.

No surviving candidate was dropped during adjudication. There were no Gate drops and no prior
review findings to settle or reopen.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: yes

Four correction-level findings are admitted. None requires structural rebuilding, so `FAIL` is
not warranted. Findings candidate-002, candidate-003, and candidate-004 require changes to the
declared §5 cross-phase interface region; candidate-005 is a conformance-map-only correction.

There is no prior-round trend on this first review, so convergence cannot yet be claimed. The next
required action is a scoped fix-up of all four findings with resolutions appended to this review
and the Phase 5 version addendum updated. Because the fix-up must change §5, the interface
change-trigger applies: a fresh verification round is required before Phase 5 can close.

## Resolutions

### candidate-002 — resolved

Added the immutable `TextureOverlaySnapshot` supply seam at the Phase 7 composition boundary. It
defines stage/key lookup, typed absence, opaque bindable handles, Phase 13 generation/fingerprint
validation, ready-before-publication and through-draw lifetime ordering, and fail-closed snapshot
construction. Phase 13 retains its internal registry and all object ownership.

### candidate-003 — resolved

Selected synchronous render-thread delivery through
`BufferEstatePublisher.addResizeConsumer(BufferResizeConsumer)`. Each accepted generation is
installed, delivered exactly once to every consumer, and only then opened for drawing; therefore
there is no coalescing, missed-generation recovery, or acknowledgement protocol. Consumer failure
keeps drawing gated and publishes shaders off.

### candidate-004 — resolved

Defined the typed available/unavailable shadow result and the `ShadowEstateView` operation surface,
including snapshot contents, clear and split-copy entry points, completion/abort outcomes, and
generation/epoch/token rejection. Successful completion alone applies recorded shadowcolor flips;
abort closes the token without flipping and requires a full shadow clear. Phase 8 still owns
camera, traversal, schedule, and copy moment.

### candidate-005 — resolved

Added explicit §3 rows mapping clear overrides, flip-aware side selection,
`maxDrawBuffers` batching, main/shadow sizing, `superSamplingLevel`, resize triggers and complete
owned invalidation, and the Final-to-Minecraft-framebuffer terminal.

### Notes deferred

None. The adjudicator admitted no notes.
