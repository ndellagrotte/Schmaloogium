# Phase 5 adversarial review — round 12

## 0. Method and reading order

I independently re-derived every surviving candidate against the complete target
`docs/phase5/v1/PHASE_5_DOC.md`, the selected governing ranges in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, and the binding §5 regions of the Phase 1, 3, and 4 dependencies.
The supplied supporting evidence was not needed to decide these internal consistency and interface
completeness defects. Only after settling all three judgments did I read
`docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_11.md`, including their resolutions.

I used no network access, no subagents or other agent fan-out, and no forbidden source. In
accordance with the already-dispatched atomic role and the verify-loop skill, I did not invoke the
verification loop, run `scripts/verify`, or start another Codex session. The only reading-contract
deviation was that the supporting
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` was not opened because the supplied
forbidden-source rule bars `*.txt`; it was unnecessary to adjudicate these candidates. There were
no candidates eliminated before adjudication and no Gate drops.

## 1. Findings

### candidate-001 — Change classification contradicts the depth-copy refresh transaction

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:947`–`:953`,
`docs/phase5/v1/PHASE_5_DOC.md:1134`–`:1138`, and
`docs/phase5/v1/PHASE_5_DOC.md:1494`–`:1497`

**Claim.** A same-extent main-depth identity-only version change has two incompatible rules for
the owned depth-copy targets. The normative refresh transaction and its test oracle reallocate
both targets for every new version, while the change-classification table says to reallocate them
only if the format changed.

**Evidence.** Section 4.8 says that on a new same-extent version Phase 5 allocates both copy
targets in the new main-depth format, reattaches their destination FBOs, and marks them
uninitialized (`docs/phase5/v1/PHASE_5_DOC.md:947`–`:953`). The neighboring classification row
instead says, *"reallocate copy targets if format changed"*
(`docs/phase5/v1/PHASE_5_DOC.md:1134`–`:1138`). The recorded-GL oracle removes any plausible
identity-only reuse interpretation by requiring both copy targets to be reallocated after both
identity-only and format-changing versions (`docs/phase5/v1/PHASE_5_DOC.md:1494`–`:1497`).

**Required correction.** Align the classification row with the settled transaction: every new
same-extent main-depth version reallocates both copy targets in the current main-depth format,
reattaches their destination FBOs, advances the attachment epoch, and forces a full clear.

**Severity:** correction

**touches interface/change-trigger region: no**

### candidate-002 — Registration rejection does not define the known-generation domain

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1149`–`:1167`,
`docs/phase5/v1/PHASE_5_DOC.md:1202`–`:1204`,
`docs/phase5/v1/PHASE_5_DOC.md:1310`, and
`docs/phase5/v1/PHASE_5_DOC.md:1511`–`:1514`

**Claim.** The exported registration result distinguishes future from unknown acknowledged
generations, but the publisher contract never defines which non-future generations are known,
how long their sizing facts remain retained, or whether the supplied sizing must match the fact
associated with the supplied generation. Callers and implementations therefore cannot derive the
same acceptance decision.

**Evidence.** The API enumerates `FUTURE_ACKNOWLEDGED_GENERATION` and
`UNKNOWN_ACKNOWLEDGED_GENERATION`, and the detailed text repeats that a future or unknown
generation is rejected (`docs/phase5/v1/PHASE_5_DOC.md:1149`–`:1167`). The general generation
rule supplies monotonic installation and equality comparison only
(`docs/phase5/v1/PHASE_5_DOC.md:1202`–`:1204`); it supplies no retained generation-to-sizing
ledger. The binding interface exports both rejection categories to Phases 13 and 14 without
adding the missing partition (`docs/phase5/v1/PHASE_5_DOC.md:1310`). The test plan requires exact
future and unknown outcomes but likewise provides no boundary oracle
(`docs/phase5/v1/PHASE_5_DOC.md:1511`–`:1514`).

**Required correction.** Define the accepted acknowledged-generation domain, the retention
lifetime of its generation-to-sizing facts, the exact future-versus-unknown partition, and whether
`acknowledgedSizing` must equal the recorded sizing for `acknowledgedGeneration`. Mirror that
rule in §5.1 and test current, retained historical, future, unknown or expired, and sizing-mismatch
cases.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-003 — Texture overlay handoff lacks an implementable data and lifetime contract

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:408`–`:410`,
`docs/phase5/v1/PHASE_5_DOC.md:1216`–`:1224`,
`docs/phase5/v1/PHASE_5_DOC.md:1249`–`:1253`, and
`docs/phase5/v1/PHASE_5_DOC.md:1310`

**Claim.** Phase 5 directly accepts a Phase 13 overlay while owning the fixed unit-to-object
selection, but the handoff remains schematic. It lacks a concrete immutable lookup/value shape,
closed absence reasons, exact generation and fingerprint comparison operands, and a deterministic
signal that allows Phase 13 to release handles after the enclosing draw.

**Evidence.** `TextureOverlaySnapshot` is a direct input to the exposed `textureBindings`
operation (`docs/phase5/v1/PHASE_5_DOC.md:408`–`:410`). Detailed prose says it maps stage/key
pairs to present or typed-absent values, carries generation and fingerprint, and keeps referenced
handles alive through the enclosing draw, but declares neither the lookup surface and closed
absence vocabulary nor a lease, completion callback, or explicit link to pass completion
(`docs/phase5/v1/PHASE_5_DOC.md:1216`–`:1224`). The output `TextureBindingSnapshot` receives
specific coherence and pass identity semantics (`docs/phase5/v1/PHASE_5_DOC.md:1249`–`:1253`),
whereas §5 exports only the overlay nouns and general lifetime/ordering promise
(`docs/phase5/v1/PHASE_5_DOC.md:1310`). Phase 13's ownership of allocation and destruction does
not make those two sides interoperable without the missing shared protocol.

**Required correction.** Define the shared overlay boundary in §§4.12 and 5: closed keys and
absence reasons, immutable entry/lookup API, concrete generation and fingerprint types with the
exact expected comparison sources, mismatch behavior, and a render-thread retention protocol.
That protocol may be a closable lease, a completion callback, or an explicit linkage to pass
completion, but it must unambiguously tell Phase 13 when the referenced handles may be released.
Keep registry, allocation, upload, and destruction implementation owned by Phase 13.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. Round 11's depth-refresh failure
cleanup, stale-estate recovery, registration no-mutation guarantee, stable consumer identity, and
delivered-count semantics are internally consistent. The consumed Phase 1, Phase 3, and Phase 4
contracts are honestly identified, and the unavailable Phase 1 depth operations remain explicit
requests rather than silent assumptions. The publication, frame/pass, clear, depth-copy, shadow,
sizing, fallback, flip, and fixed-unit surfaces are otherwise coherent. The conformance map
covers the selected App B.1/B.2/B.4 requirements and the App B.3 table retains the authoritative
unit-11 ruling.

No candidate clears on re-derivation. Candidate-001 is a localized contradictory summary row, not
an alternative optimization, because both the detailed transaction and test oracle require
identity-only reallocation. Candidate-002 is not resolved by monotonic generations or equality
comparison because neither defines retained historical membership or its associated sizing.
Candidate-003 is not delegated away by Phase 13 ownership: Phase 5 owns the binding-table
selection and accepts the overlay directly, so the shared handoff and lifetime boundary must be
implementable.

Prior reviews do not settle these defects. Round 11's depth-refresh resolution established
unconditional copy-target reallocation, which exposes candidate-001's stale classification row.
Its registration resolution added observable rejection reasons but did not define the known set
needed to distinguish them. Round 1 introduced the overlay seam and described intended typed
absence, ordering, and through-draw lifetime, but the target still lacks the concrete data and
release protocol required by candidate-003. Reopening that incomplete resolution is therefore
evidence-based rather than duplicative.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

Three localized correction-level findings are admitted. None requires rebuilding the Phase 5
architecture, so `FAIL` is not warranted. Candidates 002 and 003 change or clarify contracts in
the declared §5 interface region and therefore activate the interface change trigger;
candidate-001 is outside that region.

Round 11 had two corrections and this round has three, including two incompletely specified
interfaces created or revised by prior fix-ups. The count has increased and literal convergence
has not been reached. The next required action is a scoped fix-up resolving candidates 001, 002,
and 003 and appending resolutions to this review. Because that fix-up must change §5, a fresh
verification round is required before Phase 5 can close.

## Resolutions

### candidate-001 — resolved

Re-derived from the detailed refresh transaction and its recorded-GL oracle. The §4.11.2
classification row now requires both copy targets to be reallocated in the current main-depth
format and their destination FBOs reattached on every same-extent identity or format version
change. This removes the stale format-only qualification without changing the settled transaction.

### candidate-002 — resolved

The publisher now retains every successfully installed ready generation→sizing fact for its
lifetime. Generations above the current generation are future; non-future generations absent from
that ledger, including off and never-installed generations, are unknown. Registration additionally
requires exact equality with the known sizing fact and exposes
`ACKNOWLEDGED_SIZING_MISMATCH`. Section 5.1 mirrors the partition and retention rule, and the test
matrix covers current, retained historical, future, unknown, and sizing-mismatch cases with the
existing no-mutation invariant.

### candidate-003 — resolved

The overlay boundary now defines closed keys and absence reasons, a total immutable lookup,
`TextureOverlayPublicationId(generation, TextureOverlayFingerprint)`, and the exact comparisons:
the lease ID must equal Phase 7's ID from the same atomic Phase 13 publication, and its
`RegistryFingerprint` must equal the buffer estate's fingerprint. A successful binding snapshot
retains the Phase-13-owned lease; Phase 7 closes it in the enclosing draw's `finally`, after draw
completion, which is the deterministic handle-release signal. Mismatch produces
`MissingTextureBinding` and no draw. Section 5.1 and the conformance matrix carry the same contract;
Phase 13 continues to own registry, allocation, upload, and destruction.

### Notes deferred

None.
