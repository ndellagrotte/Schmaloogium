# Phase 5 adversarial review — round 4

## 0. Method and reading order

I independently re-derived every surviving candidate against the complete target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, the binding §5 regions of the Phase 1, 3, and 4 dependencies,
and the cited RESEARCH and supporting-evidence material. Only after settling those judgments did I
read `docs/phase5/reviews/PHASE_5_REVIEW_1.md`,
`docs/phase5/reviews/PHASE_5_REVIEW_2.md`, and
`docs/phase5/reviews/PHASE_5_REVIEW_3.md`, including their resolutions.

I used no network access, no subagents or other agent fan-out, and no forbidden source. In
accordance with the already-dispatched atomic role and the verify-loop skill, I did not invoke the
verification loop, run `scripts/verify`, or start another Codex session. There were no deviations
from the supplied reading contract.

Gate dropped candidate-002 because its finder quote at
`docs/phase5/v1/PHASE_5_DOC.md:1374`–`:1377` did not resolve. It is not admitted or counted.

## 1. Findings

### candidate-001 — Registration prose still names the obsolete one-argument overload

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:286`–`:289` and
`docs/phase5/v1/PHASE_5_DOC.md:1037`–`:1039`

**Claim.** The public declaration requires the resize consumer and its acknowledged sizing and
generation, but the detailed lifecycle presents the same method as a one-argument callable
contract. No overload or shorthand rule reconciles the two forms.

**Evidence.** `BufferEstatePublisher` declares
`addResizeConsumer(BufferResizeConsumer consumer, BufferSizing acknowledgedSizing, long
acknowledgedGeneration)` (`docs/phase5/v1/PHASE_5_DOC.md:286`–`:289`). Section 4.11.2 instead
states, *"`BufferEstatePublisher.addResizeConsumer(BufferResizeConsumer)` is render-thread-only"*
and proceeds to define the returned registration's lifecycle
(`docs/phase5/v1/PHASE_5_DOC.md:1037`–`:1039`). The acknowledged values are load-bearing to the
per-registration retry protocol immediately below, so silently omitting them leaves downstream
consumers with conflicting signatures.

**Required correction.** Replace the obsolete form in §4.11.2 with the full three-argument
signature, or explicitly reference the canonical declaration, while retaining the render-thread,
registration-order, returned-handle, and idempotent-close rules.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-005 — The legacy shadow-depth swizzle is absent from the conformance map

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:404`–`:418` and
`docs/phase5/v1/PHASE_5_DOC.md:957`–`:966`

**Claim.** The old-pack `R,R,R,1` shadow-depth sampling swizzle is an explicit in-scope
requirement and has a detailed design, but §3.2 does not explicitly map it. Adjacent rows for
hardware PCF and filtering do not name or trace the separately required swizzle.

**Evidence.** The governing Phase 5 assignment requires the old-pack swizzle alongside hardware
PCF and real shadow flipping (`docs/design/v2.0-RC3/DESIGN.md:1630`–`:1637`), and the mandatory
template requires every in-scope contract item to appear in the conformance map with zero
unmapped rows (`docs/design/v2.0-RC3/DESIGN.md:804`–`:808`). The target itself lists the swizzle
as a distinct scope item (`docs/phase5/v1/PHASE_5_DOC.md:156`) and §4.10 requires legacy
`R,R,R,1` sampling for depth textures (`docs/phase5/v1/PHASE_5_DOC.md:957`–`:966`). Section
3.2 maps shadow depth contents, colors, PCF/filtering, flips, sizing, and resize lifecycle, but
contains no swizzle row (`docs/phase5/v1/PHASE_5_DOC.md:404`–`:418`).

**Required correction.** Add a §3.2 row explicitly mapping the legacy shadow-depth `R,R,R,1`
swizzle to §4.10, with the governing RC3 assignment and applicable Pintonium provenance.

**Severity:** correction

**touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The mandatory-full-clear state is
consistent across planning, execution, the exposed contract, and tests. Resize publication
failure ordering, per-consumer acknowledgement and retry convergence, off-publication behavior,
shadow lifecycle, fixed unit bindings, formats and transfers, depth copying, flip ownership, and
the consumed Phase 1, 3, and 4 contracts are otherwise coherent.

Candidate-003 is dropped. `ConsumerFailed.consumerId` is publisher-produced diagnostic output,
and the publisher already owns named, tracked per-registration state. It can mint and retain an
internal identity when registration occurs; neither the governing material nor the target
requires caller selection, caller access, or a new public identity seam.

Candidate-004 is dropped. The resize consumer owns its downstream resources and normatively
supplies the sizing and generation those resources acknowledge. In this protocol, “truthful”
means the publisher faithfully reports its recorded per-consumer acknowledgement and advances it
only on success; it does not promise independent authentication of another phase's resource
state. The proposed canonical cross-check, opaque credential, duplicate policy, and closed typed
registration result are additional API policy not required by the governing resize checklist or
the stated contract.

Prior-round findings remain settled by their recorded resolutions. Candidate-001 is a signature
drift introduced by the Round-3 registration-baseline fix-up, not a reopening of Round 2's
resolved missing-registration finding. Candidate-005 identifies a narrower unmapped requirement
not included in Round 1's resolved conformance additions. The Gate-dropped candidate-002 remains
excluded for unverifiable evidence.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Two localized correction-level findings are admitted; neither requires structural rebuilding, so
`FAIL` is not warranted. Candidate-001 corrects an interface signature repetition and triggers
the declared interface region. Candidate-005 is conformance-map-only.

Across prior rounds and this round, correction counts are 4, 5, 2, and 2. The count has not
strictly decreased from Round 3, and the newly revised interface surface still contains a
correction, so convergence has not been reached. The next required action is a scoped fix-up of
candidate-001 and candidate-005, with resolutions appended to this review and the Phase 5
addendum updated. Because candidate-001 changes interface semantics or their binding
representation, the interface change-trigger applies: a fresh verification round is required
before Phase 5 can close.

## Resolutions

### candidate-001 — applied

Re-derived the public declaration and the neighboring retry protocol. Section 4.11.2 now repeats
the canonical three-argument `addResizeConsumer` signature, including the consumer's acknowledged
sizing and generation, while preserving the render-thread, registration-order, returned-handle,
idempotent-close, and non-reentrancy rules. This edit lies in §4, outside §5, but changes the
binding representation of the exposed resize interface; the manifest change trigger therefore
requires a fresh verification round.

### candidate-005 — applied

Re-derived the RC3 shadow checklist and the existing §4.10 design. Section 3.2 now maps the legacy
shadow-depth `R,R,R,1` sampling swizzle explicitly to §4.10 and cites both the governing assignment
and the applicable Pintonium mechanism evidence. The adjacent hardware-PCF/filter and real-flip
rows remain distinct.

### Notes deferred

None.
