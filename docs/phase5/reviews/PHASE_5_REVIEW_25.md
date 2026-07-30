# Phase 5 Verification Review — Round 25

## 0. Method and reading order

I independently re-derived the sole Gate-surviving candidate before consulting prior reviews. I
read the Phase 5 target's fixed texture-unit model, overlay validation and lease lifecycle,
recorded-GL test obligations, and manifest-declared §5 interface region; the RC3 Part I rules,
mandatory template, Phase 5 specification, and document gate; and the manifest-selected binding
regions of Phases 1, 3, and 4. Supporting evidence was not needed to decide this internal
result-contract candidate.

Only after settling that interpretation did I read Phase 5 reviews 1 through 24, in numeric order,
to compare the candidate with prior dispositions and settled resolutions. I used no network
access, forbidden source, or prior-session transcript. In particular, I did not open the supplied
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, because the resolved forbidden-source rule
bars `*.txt` and it was unnecessary. There was no agent fan-out or delegation. In accordance with
the dispatched atomic-role instruction and the verify-loop skill, I did not invoke the loop, run
`scripts/verify`, or start another Codex session. There were no deviations from the resolved
reading contract, no candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — Whole-call validation rejection requires an undefined `MissingTextureBinding`

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1525`–`:1529`,
`docs/phase5/v1/PHASE_5_DOC.md:1627`, and
`docs/phase5/v1/PHASE_5_DOC.md:1841`–`:1845`

**Claim.** The closed `TextureBindingResult` requires every publication-ID or
registry-fingerprint rejection to carry a `MissingTextureBinding`, but those checks reject the
whole call before any bind or per-unit lookup. The document defines `MissingTextureBinding` only
as the result of resolving a particular unavailable fixed-table object, so it supplies neither a
value nor construction semantics for the mandatory rejection payload.

**Evidence.** Section 4.12 defines the result as exactly
`Bound(TextureBindingSnapshot)` or
`Rejected(TextureBindingRejection, MissingTextureBinding)`, then states that either validation
failure rejects before any bind (`docs/phase5/v1/PHASE_5_DOC.md:1525`–`:1529`). The binding §5
contract repeats `Rejected(reason,missing)` while associating `MissingTextureBinding` with the
three unit-15 overlay absence values
(`docs/phase5/v1/PHASE_5_DOC.md:1627`). Elsewhere, `MissingTextureBinding` is likewise produced by
a fixed-table `Missing` entry or an unallocated optional buffer
(`docs/phase5/v1/PHASE_5_DOC.md:1503`–`:1509`,
`docs/phase5/v1/PHASE_5_DOC.md:1556`–`:1563`). None of those per-unit cases exists when
publication validation rejects before lookup. The test plan proves the rejection reason,
no-bind/no-draw behavior, and caller closure, but gives no oracle for constructing or checking the
required `MissingTextureBinding` payload
(`docs/phase5/v1/PHASE_5_DOC.md:1841`–`:1845`).

**Required correction.** Prefer a whole-call rejection variant of
`Rejected(TextureBindingRejection)` with no per-unit missing payload. Alternatively, define a
distinct closed validation-failure payload and its exact pre-lookup construction semantics.
Mirror the chosen shape in the public operation, §4.12, binding §5, and the recorded-GL tests.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported new-surface area is otherwise clean on re-derivation. The Round-24 fix-up
consistently specifies validation priority, pre-bind rejection, draw suppression, caller-owned
lease closure on rejection, lease transfer on success, and snapshot-finally closure. Its addendum
correctly records that the changed §5 surface requires this fresh round.

The interface and conformance clean areas also remain clean. Phase 5's consumed Phase 1, 3, and 4
claims match the manifest-selected binding contracts, and its other dependent-facing contracts
are backed by detailed public shapes and lifecycle semantics. The conformance map covers the
in-scope Appendix B.1, B.2, B.3, and B.4 requirements plus the governing flip, clear, sizing,
resize, growth, shadow, depth, and Final-framebuffer requirements.

The strongest clearing interpretation was that the rejection payload could reuse an ordinary
per-unit missing value. It fails because every defined `MissingTextureBinding` case identifies a
fixed-table lookup with no bindable object, whereas publication-ID and registry-fingerprint
validation reject the whole operation before such lookup. Phase 5 owns this closed result
contract, so Phase 7 cannot choose an unstated sentinel or construction rule. Prior Round 24
introduced the result algebra and explicitly required the disputed payload, but its resolution
does not define that payload for validation rejection. Candidate-001 is therefore admitted. No
candidate was dropped on re-derivation, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The sole admitted defect is a localized correction to the exported `textureBindings` result
contract and does not require rebuilding the Phase 5 architecture, so `FAIL` is not warranted.
The supplied prior-round trend data is empty; direct prior-review comparison shows Round 23 passed,
Round 24 admitted one correction, and this round finds the Round-24 result shape still incomplete.
Convergence is therefore not established.

The next required action is a scoped fix-up resolving candidate-001 and appending its resolution
to this review. Because the correction must change or clarify binding §5's exposed result shape,
the `cross-phase-interfaces` change trigger applies: Phase 5 owes a fresh verification round before
it can close.

## Resolutions

### candidate-001 — resolved

Re-derived from the result lifecycle rather than adopting the finding's argument: publication-ID
and registry-fingerprint validation occurs before fixed-table resolution, while every
`MissingTextureBinding` value denotes a particular unavailable table object. A validation
rejection therefore has no truthful per-unit missing value.

The public `textureBindings` operation continues to return the closed `TextureBindingResult`, but
§4.12 now defines its whole-call failure arm as `Rejected(TextureBindingRejection)`. Binding §5
mirrors exactly `Bound(snapshot)` or `Rejected(reason)`, retains the two closed reasons and their
priority, and leaves `MissingTextureBinding` solely on the per-unit absence path. The recorded-GL
test oracle now requires `Rejected(reason)` with no missing-binding payload, plus the existing
reason, pre-bind/no-draw, caller-close, priority, and success-transfer checks. The compact §0.27
addendum records the correction.

This changes the manifest-declared cross-phase interface region, so Phase 5 remains unverified and
owes a fresh verification round before closure.

### Notes deferred

None.
