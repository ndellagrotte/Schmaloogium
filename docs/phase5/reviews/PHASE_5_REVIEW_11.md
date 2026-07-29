# Phase 5 adversarial review — round 11

## 0. Method and reading order

I independently re-derived both surviving candidates against the complete target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, and the binding §5 regions of the Phase 1, 3, and 4
dependencies. The supplied supporting evidence was not needed to decide these two internal
contract inconsistencies. Only after settling both judgments did I read
`docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_10.md`, including their resolutions.

I used no network access, no subagents or other agent fan-out, and no forbidden source. In
accordance with the already-dispatched atomic role and the verify-loop skill, I did not invoke the
verification loop, run `scripts/verify`, or start another Codex session. The only
reading-contract deviation was that the supporting
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` was not opened because the supplied
forbidden-source rule bars `*.txt`; it was unnecessary to adjudicate either candidate. There were
no candidates eliminated before adjudication and no Gate drops.

## 1. Findings

### candidate-001 — Same-extent depth-format refresh omits required copy-target reallocation

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:928`–`:943`,
`docs/phase5/v1/PHASE_5_DOC.md:986`–`:992`, and
`docs/phase5/v1/PHASE_5_DOC.md:1110`–`:1114`

**Claim.** The public same-extent main-depth refresh transition detects a format change but can
return `Reattached` without reallocating the Phase-5-owned depthtex1/depthtex2 copy targets into
the new depth format. Marking their existing storage uninitialized does not make its format match
the new main depth.

**Evidence.** The successful same-extent transition is stated as an exhaustive six-step sequence:
it reattaches owned FBOs that use main depth, explicitly leaves the depthtex1/depthtex2 destination
FBOs attached to their owned targets, and merely marks both copy targets uninitialized
(`docs/phase5/v1/PHASE_5_DOC.md:928`–`:936`). `refreshMainDepth` compares the cached format as well
as version, handle identity, and extent, yet routes every new same-extent available snapshot
through those same six steps before returning `Reattached`
(`docs/phase5/v1/PHASE_5_DOC.md:938`–`:943`). Elsewhere, both copy targets must match main depth's
depth/depth-stencil format and are reallocated on a format or version change
(`docs/phase5/v1/PHASE_5_DOC.md:986`–`:992`); the change-classification table likewise requires
copy-target reallocation when the same-extent format changes
(`docs/phase5/v1/PHASE_5_DOC.md:1110`–`:1114`). The recorded-GL refresh oracle repeats the
incomplete identity/version path without exercising format-sensitive allocation
(`docs/phase5/v1/PHASE_5_DOC.md:1466`–`:1470`).

**Required correction.** Make same-extent format-sensitive copy-target reallocation part of the
refresh transaction before `Reattached` can be returned. Define cleanup and reuse the existing
`Failed(BufferFailure)` poisoned-estate recovery if allocation or reattachment fails. Align §4.8,
§4.9, the §5.1 refresh contract, and the recorded-GL test with that behavior.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-002 — Resize-consumer registration has no observable rejection contract

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:318`–`:329`,
`docs/phase5/v1/PHASE_5_DOC.md:1125`–`:1137`, and
`docs/phase5/v1/PHASE_5_DOC.md:1283`

**Claim.** The exported resize-consumer registration operation requires rejection of blank or
duplicate identities and future or unknown acknowledged generations, but it does not define how
Phases 13 and 14 observe rejection or what state remains unchanged.

**Evidence.** `BufferEstatePublisher.addResizeConsumer` returns only
`BufferResizeRegistration`; the declaration supplies no rejected variant or other failure carrier
(`docs/phase5/v1/PHASE_5_DOC.md:318`–`:329`). The detailed contract then mandates a nonblank,
live-unique `consumerId` and says registration rejects a future or unknown generation, without
specifying a result, exception type, diagnostic, or state effect for those cases
(`docs/phase5/v1/PHASE_5_DOC.md:1125`–`:1137`). The binding §5 row repeats the identity
preconditions for downstream consumers but still exposes only the successful registration
contract (`docs/phase5/v1/PHASE_5_DOC.md:1283`). General containment of unexpected runtime
failures is not a stable rejection protocol that consumers can intentionally implement.

**Required correction.** Define one exact observable rejection mechanism for the existing blank
ID, duplicate live ID, and future or unknown acknowledged-generation cases, either through a
closed registration result with enumerated reasons or a specified stable exception contract.
State that rejection installs no registration and changes no acknowledgement or publication
state, update the method contract and §5 row, and test each rejection. Do not add new null-input
preconditions merely as part of this correction.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. Round 10's refresh-failure
containment is aligned across §4.8, §5.1, §6, and §8.3: every `Failed` outcome advances the
attachment epoch, preserves the prior cached identity, poisons the estate, forbids further shader
draws, and requires shaders-off replacement. `ResizeRequired` consistently performs no GL or
estate mutation and uniquely carries `MAIN_DEPTH_RESIZE_REQUIRED`. Successful same-extent
identity-only reattachment consistently invalidates snapshots and requires reacquisition before
drawing.

The consumed Phase 3 and Phase 4 contracts match their binding regions, and Phase 5 records its
Phase 1 facade gaps as requested changes rather than assuming unavailable operations. The main
publication, frame/pass snapshot, depth-copy, shadow-estate, texture-overlay, resize-notification,
fixed-unit, flip, clear, sizing, fallback, and conformance-map surfaces are otherwise coherent.
Appendix B.1, B.2, and B.4 are mapped, and Appendix B.3 includes the governing unit-11 ruling.

Neither candidate clears on re-derivation. For candidate-001, §4.9's general format/reallocation
rule does not complete §4.8's explicitly enumerated success transition; instead it proves that
the transition can retain storage of the wrong format. For candidate-002, Java implementation
conventions cannot choose among exceptions, result carriers, or state effects on behalf of an
exposed cross-phase contract.

The prior reviews do not settle either finding. Round 9 introduced the public main-depth refresh
carrier, and Round 10 repaired its success and failure lifecycle, but neither resolution covered
format-sensitive copy-target allocation. Earlier resize-consumer corrections defined ordered
delivery, acknowledgement baselines, failure accounting, and stable identities; none defined
registration-time rejection observability.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Two localized correction-level findings are admitted. Neither requires rebuilding the Phase 5
architecture, so `FAIL` is not warranted. Both corrections change or clarify public contracts
consumed by dependent phases and therefore activate the declared interface change trigger.

Round 10 had three corrections; this round has two distinct corrections, so the count decreases
but literal convergence has not been reached. The next required action is a scoped fix-up
resolving candidates 001 and 002 and appending resolutions to this review. Because that fix-up
must change or clarify §5, a fresh verification round is required before Phase 5 can close.

## Resolutions

### candidate-001 — resolved

Re-derived §4.8 against §4.9's existing invariant that depthtex1/depthtex2 match main depth and are
reallocated on a version change. The same-extent refresh transaction now allocates both copy
targets in the new format, attaches and checks their destination FBOs, and deletes superseded
targets only after success. Allocation, attachment, and completeness failures delete newly
created partial targets, poison the estate, retain its prior cached depth identity, and use the
existing `Failed(BufferFailure)` shaders-off replacement path. The §5 refresh row remains the
closed public recovery contract, and the recorded-GL oracle now covers identity-only and
format-changing success plus allocation/attachment/check failure cleanup.

### candidate-002 — resolved

Re-derived the exposed registration operation as a closed result rather than relying on an
implementation convention. `addResizeConsumer` now returns `Registered` or `Rejected` with one of
four stable reasons: blank ID, duplicate live ID, future acknowledged generation, or unknown
acknowledged generation. Every rejection installs no registration and changes no acknowledgement,
publication, generation, or drawing state. The §5 row exports the result and reason types, and the
test contract exercises each reason and the no-mutation invariant. No null-input precondition was
added.

### Notes deferred

None.

The target received compact §0.13 reporting only. Both corrections changed the binding §5 region,
so a fresh verify round is required before Phase 5 can close.
