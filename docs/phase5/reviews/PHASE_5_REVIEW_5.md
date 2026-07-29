# Phase 5 adversarial review — round 5

## 0. Method and reading order

I independently re-derived both surviving candidates against the target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, the binding §5 regions of the Phase 1, 3, and 4 dependencies,
and the cited RESEARCH and supporting-evidence material. I also checked the manifest definition
and the target's actual end of file to test the supplied resolved-selector boundary. Only after
settling both judgments did I read
`docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_4.md`, including their resolutions.

I used no network access, no subagents or other agent fan-out, and no forbidden source. In
accordance with the already-dispatched atomic role and the verify-loop skill, I did not invoke the
verification loop, run `scripts/verify`, or start another Codex session. The only deviation forced
by the dispatched contract is recorded as candidate-001: its purported `whole_document` range
does not reach the actual end of the target. There were no Gate drops.

## 1. Findings

### candidate-001 — Validated whole-document selector stops 117 lines before EOF

**Location:** resolved target selector boundary at
`docs/phase5/v1/PHASE_5_DOC.md:1449`

**Claim.** The dispatched `whole_document` selector does not cover the complete Phase 5
document. It ends at line 1449, in the middle of the phase-local decision table, while substantive
decisions, hand-offs, requested upstream changes, and the implementation checklist continue
through line 1566. This round therefore cannot establish whole-document verification.

**Evidence.** Lines 1449–1451 continue the decision table with D-P5-10 through D-P5-12
(`docs/phase5/v1/PHASE_5_DOC.md:1449`–`:1451`). The target continues through the implementation
gate and its dependency-order prohibition at lines 1563–1566
(`docs/phase5/v1/PHASE_5_DOC.md:1563`–`:1566`). The manifest defines `whole_document` with only a
start anchor (`verification/targets/phase-5.json:8`–`:17`), so fresh resolution should reach EOF;
the supplied resolved end at line 1449 is stale or inconsistent with the artifact dispatched for
review.

**Required action.** Re-resolve and validate the selector against the current 1566-line target,
investigate why the validated contract retained the stale endpoint, and restart Round 5 so all
lines receive Attack, Refute, Gate, and adjudication coverage. Edit the manifest only if fresh
resolution still fails to reach EOF.

**Severity:** blocking

**touches interface/change-trigger region: no**

### candidate-002 — Binding §5 omits the resize-registration entry-point signature

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1183`–`:1193`

**Claim.** Phase 13 and Phase 14 cannot derive the callable resize-registration contract from
Phase 5's binding §5 alone. The row names resize types and acknowledgement semantics but omits
`BufferEstatePublisher.addResizeConsumer` and does not state that acknowledged sizing and
generation are caller-supplied registration parameters.

**Evidence.** The governing design makes dependency §5 the contract dependents build against
(`docs/design/v2.0-RC3/DESIGN.md:269`–`:272`) and requires exact interfaces exposed to dependent
phases (`docs/design/v2.0-RC3/DESIGN.md:225`–`:228`). The target's public declaration gives the
concrete method
`addResizeConsumer(BufferResizeConsumer consumer, BufferSizing acknowledgedSizing, long
acknowledgedGeneration)` (`docs/phase5/v1/PHASE_5_DOC.md:284`–`:294`), and its detailed lifecycle
repeats that full signature and returned `BufferResizeRegistration`
(`docs/phase5/v1/PHASE_5_DOC.md:1042`–`:1044`). Section 5.1 instead exposes only the related type
names and general semantics (`docs/phase5/v1/PHASE_5_DOC.md:1183`–`:1193`), omitting the callable
entry point and the origin of its baseline arguments.

**Required correction.** Add
`BufferEstatePublisher.addResizeConsumer(BufferResizeConsumer consumer, BufferSizing
acknowledgedSizing, long acknowledgedGeneration)` to §5.1, state that it returns
`BufferResizeRegistration`, and include the initial-baseline validation already specified in the
detailed design. This §5 edit activates the configured fresh-verification trigger.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported conformance areas remain clean on re-derivation: the in-scope Phase 5
requirements are mapped, and the inspected formats, flip and clear semantics, depth copying,
shadow behavior, sizing, resize, growth, and final-framebuffer handoff have supporting design
coverage. The corrected three-argument resize signature is consistent between the public API
declaration and detailed lifecycle prose. The legacy shadow-depth `R,R,R,1` swizzle is now
consistently present in the conformance map, detailed design, and implementation roadmap.

Consumed Phase 1, Phase 3, and Phase 4 contracts are represented honestly, including Phase 1
additions that remain explicitly gated prerequisites. Other inspected Phase 5 consumer promises
have sufficient corresponding detail.

Neither candidate is cleared on re-derivation. Candidate-001 is a verification-surface failure,
not an assertion that the start-only manifest selector intentionally ends at line 1449.
Candidate-002 remains an independent binding-contract omission even though the full method is
specified elsewhere in the target. Prior-round resolutions do not settle either issue: Round 4
corrected the detailed signature outside §5, while the present candidate tests what dependents
are permitted to build against in §5 itself.

## 3. Verdict

# FAIL
Counts: blocking=1; corrections=1; notes=0
Interface changed: yes

Candidate-001 is a blocking structural failure of the dispatched verification surface: 117
substantive lines were outside the purported whole-document selector, so this round cannot verify
the target. Candidate-002 is a localized interface correction independently confirmed within the
reviewed material. The interface flag is therefore `yes`.

Across prior rounds the correction counts were 4, 5, 2, and 2; this round has one correction plus
a newly detected blocking coverage failure. The trend does not demonstrate convergence, and the
coverage failure prevents a meaningful substantive closure judgment. The next required action is
to repair or refresh selector resolution and restart Round 5 over the complete target. If the
restarted adjudication again admits candidate-002, apply its scoped §5 correction and require the
configured fresh verify round before Phase 5 closes.
