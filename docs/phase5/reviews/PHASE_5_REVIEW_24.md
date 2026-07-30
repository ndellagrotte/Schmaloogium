# Phase 5 Verification Review — Round 24

## 0. Method and reading order

I independently re-derived the sole Gate-surviving candidate before consulting prior reviews. I
read the Phase 5 target's public estate API, fixed texture-unit model, overlay validation and lease
lifecycle, and manifest-declared §5 interface region; the RC3 Part I rules, mandatory template,
Phase 5 specification, and document gate; the applicable RESEARCH.md contract material; and the
manifest-selected binding regions of Phases 1, 3, and 4. Supporting evidence was not needed to
decide this interface-representability candidate.

Only after settling that interpretation did I read Phase 5 reviews 1 through 23, in numeric order,
to compare the candidate with prior dispositions and resolutions. I used no network access,
forbidden source, or prior-session transcript. In particular, I did not open the supplied
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, because the resolved forbidden-source
rule bars `*.txt` and it was unnecessary. There was no agent fan-out or delegation. In accordance
with the dispatched atomic-role instruction and the verify-loop skill, I did not invoke the loop,
run `scripts/verify`, or start another Codex session. There were no deviations from the resolved
reading contract, no candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — `textureBindings` cannot represent its specified mismatch outcome

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:540`–`:543`,
`docs/phase5/v1/PHASE_5_DOC.md:1514`–`:1520`,
`docs/phase5/v1/PHASE_5_DOC.md:1553`–`:1558`, and
`docs/phase5/v1/PHASE_5_DOC.md:1615`

**Claim.** The exposed `textureBindings` operation returns only a successful
`TextureBindingSnapshot`, while its contract also requires whole-call publication-ID or
registry-fingerprint mismatch to produce `MissingTextureBinding` and suppress the draw. The
document defines no result algebra, exception or nullable convention, or rejected snapshot state
that can carry that distinct outcome, and it does not settle ownership of the supplied overlay
lease when validation rejects the call.

**Evidence.** The public method is declared solely as
`TextureBindingSnapshot textureBindings(PassBufferSnapshot snapshot, TextureOverlayLease overlay,
TextureOverlayPublicationId expectedOverlay)` (`docs/phase5/v1/PHASE_5_DOC.md:540`–`:543`).
Detailed design requires exact ID and registry-fingerprint equality and says mismatch yields
`MissingTextureBinding` and no draw, while only a successful snapshot is said to retain the lease
(`docs/phase5/v1/PHASE_5_DOC.md:1514`–`:1520`). The snapshot itself is defined as successful,
pass-coherent binding data with immutable unit-to-handle rows consumed before drawing
(`docs/phase5/v1/PHASE_5_DOC.md:1553`–`:1558`), not as a discriminated validation result. Binding
§5 names the validation checks, successful snapshot retention, and `MissingTextureBinding`, but
still exposes no carrier connecting the failure outcome to the operation
(`docs/phase5/v1/PHASE_5_DOC.md:1615`).

**Required correction.** Define a closed result returned by `textureBindings`, with explicit
success and validation-rejection variants and lease-ownership rules for every outcome.
Alternatively, define an unambiguous rejected `TextureBindingSnapshot` state, including its typed
row shape, exact Phase 7 no-draw test, and lease handling. Mirror the selected representation in
§5.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported new-surface area remains clean on re-derivation. The §0.25
dependency-adoption changes are consistently propagated through borrowed-depth issuance and
operations, candidate-view ownership, consumed dependency contracts, tests, staging, decisions,
and the implementation checklist. The selected Phase 1 and Phase 4 binding contracts match those
adoptions.

The interface and conformance clean areas also remain clean. Main frame/pass lifecycle, depth,
shadow, clear, resize, publication, format, sizing, flip, and fixed-unit contracts are otherwise
represented coherently. The conformance map covers the governing Appendix B.1, B.2, B.3, and B.4
requirements, including the authoritative unit-11 ruling, plus the additional RC3 Phase 5
requirements.

The strongest clearing interpretation was that every mismatch returns a normal
`TextureBindingSnapshot` containing `MissingTextureBinding` rows. It fails because the snapshot is
defined in terms of immutable unit-to-handle rows and successful pre-draw consumption, while
`MissingTextureBinding` elsewhere represents individual unavailable bindings; no text defines a
snapshot-wide provenance-rejection state or discriminator. Prior Round 12 established the exact
overlay comparisons, successful lease retention, and mismatch/no-draw promise, but its resolution
did not define how the public return type represents rejection or who closes the lease on that
path. Later reviews did not settle that carrier gap. Candidate-001 is therefore admitted rather
than cleared. No candidate was dropped on re-derivation, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The sole admitted defect is a localized cross-phase interface correction and does not require
rebuilding the Phase 5 architecture, so `FAIL` is not warranted. The supplied prior-round trend
data is empty; direct prior-review comparison nevertheless shows Round 23 passed with zero
findings, so this newly exposed representability gap means convergence is no longer established.

The next required action is a scoped fix-up resolving candidate-001 and appending its resolution
to this review. Because the correction must change or clarify binding §5's exposed
`textureBindings` result contract, the `cross-phase-interfaces` change trigger applies: Phase 5
owes a fresh verification round before it can close.

## Resolutions

### candidate-001 — resolved

Re-derived from the public operation, overlay validation rules, snapshot lifecycle, and binding §5:
a successful-only return type could not carry the two whole-call validation failures, and the
existing per-unit `MissingTextureBinding` rows did not define lease ownership on whole-call
rejection.

`BufferEstateView.textureBindings` now returns the closed `TextureBindingResult`, exactly
`Bound(TextureBindingSnapshot)` or
`Rejected(TextureBindingRejection, MissingTextureBinding)`. The rejection reason is exactly
`OVERLAY_PUBLICATION_ID_MISMATCH` or `REGISTRY_FINGERPRINT_MISMATCH`, checked in that order so the
publication-ID reason wins when both mismatch. Rejection is pre-bind, suppresses the draw,
transfers no ownership, and leaves Phase 7 responsible for closing its lease; success transfers
the lease into the snapshot, which releases it on idempotent close after the enclosing draw.
Binding §5, tests, the Phase 6 hand-off, and the implementation checklist mirror that
representation. This changes the declared cross-phase interface, so a fresh verification round is
required before Phase 5 can close.

### Notes deferred

None.
