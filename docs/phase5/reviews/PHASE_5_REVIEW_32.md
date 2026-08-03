# Phase 5 Verification Review — Round 32

## 0. Method and reading order

I independently re-derived the Gate-surviving candidate before consulting prior reviews. I read
the Phase 5 conformance row, shadow-estate planning and lifecycle, sizing semantics, and the
manifest-declared §5 interface region; the RC3 Phase 5 shadow-FBO assignment; Phase 3's independent
shadow-depth and shadow-color minima and binding contract; and the relevant Research Appendix B
shadow-buffer contract. The remaining authoritative and dependency surfaces did not alter the
closed sizing-and-allocation question. No supporting implementation evidence was needed.

Only after settling the candidate's interpretation did I read Phase 5 reviews 1 through 31, with
their resolutions considered last. Round 31 introduced and resolved the depth-only conformance
mapping that exposes this defect; no earlier settled material establishes that shadow-color demand
implies shadow-depth demand or otherwise provides a color-only shadow estate.

I used no network access, forbidden source, or prior-session transcript. In particular, I did not
open the forbidden `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`. There was no agent
fan-out or delegation. In accordance with the dispatched atomic-role instruction and the
verify-loop skill, I did not invoke the loop, run `scripts/verify`, or start another Codex session.
There were no deviations from the resolved reading contract. Candidate-002 was eliminated at
Refute before adjudication. There were no Gate drops.

## 1. Findings

### candidate-001 — Conditional sfb creation ignores shadowcolor-only usage

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:742`–`:743`,
`docs/phase5/v1/PHASE_5_DOC.md:1359`–`:1364`, and
`docs/phase5/v1/PHASE_5_DOC.md:1798`–`:1806`

**Claim.** Phase 5 makes the entire shadow framebuffer estate absent unless Phase 3 reports a
positive shadow-depth minimum, although Phase 3 derives shadow-depth and shadow-color minima
independently and the governing assignment requires the sfb whenever shadow buffers are used. A
pack that references only `shadowcolor` therefore requests color storage that Phase 5 never plans
or allocates.

**Evidence.** The conformance map expressly maps sfb creation only to positive
`shadowDepthBuffers()` while its adjacent row treats shadowcolor as a separately allocated estate
(`docs/phase5/v1/PHASE_5_DOC.md:742`–`:743`). The detailed lifecycle repeats that the sfb is absent
unless at least one shadow depth buffer is reported, and only after that gate allocates zero to two
shadowcolor pairs (`docs/phase5/v1/PHASE_5_DOC.md:1359`–`:1364`). Phase 3 independently maps shadow
depth uniforms to `shadowDepthBuffers` and shadowcolor uniforms to `shadowColorBuffers`
(`docs/phase3/v1/PHASE_3_DOC.md:655`–`:656`), and its public `BufferMinima` retains both as separate
integers (`docs/phase3/v1/PHASE_3_DOC.md:997`–`:1001`). RC3 instead conditions creation on shadow
buffers being used generally and expressly assigns both depth and color structure to the sfb
(`docs/design/v2.0-RC3/DESIGN.md:1630`–`:1633`). Phase 5 also publishes `shadowExtent` absence and
`ShadowEstateNotRequested` semantics through its §5 contracts, so correcting the planning gate
changes the declared interface region (`docs/phase5/v1/PHASE_5_DOC.md:1798`–`:1806`).

**Required correction.** Plan the sfb whenever either `shadowDepthBuffers() > 0` or
`shadowColorBuffers() > 0`. Align §3.2, §4.10, `BufferSizing.shadowExtent`, ordinary
`ShadowEstateNotRequested` absence semantics, tests, and the binding §5 description. Specify how
a color-only sfb obtains any internal depth attachment required for framebuffer completeness
without inflating Phase 3's pack-facing shadow-depth minimum.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. Round 31's shadow-mipmap
filter-restoration repair is consistently propagated through §4.10, binding §5, failure handling,
tests, and the implementation checklist. The consumed Phase 1, Phase 3, and Phase 4 contracts are
otherwise represented consistently in Phase 5 §5. The conformance map otherwise covers the
governing framebuffer, depth/shadow, fixed-unit, routing, format, and lifecycle requirements
reported by the conformance lens.

Candidate-001 is not cleared by prior settled material. Round 31's candidate-002 resolution added
the depth-only mapping but did not test that mapping against Phase 3's independent shadow-color
minimum; it therefore introduced or made explicit the present new surface. The separately supplied
candidate-002 was eliminated by Refute and cannot be admitted. No candidate was dropped during my
independent re-derivation, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a localized planning, lifecycle, and contract correction rather than a
structural rebuild, so `FAIL` is not warranted. The supplied prior-round trend is empty. Direct
comparison shows Round 31's fix-up introduced the depth-only conditional mapping and this fresh
round finds that condition incomplete; convergence is interrupted until the color-only case is
corrected and freshly verified.

The next required action is a scoped fix-up resolving candidate-001 and appending its resolution
to this review. Because the correction changes Phase 5's published sizing/absence semantics in §5,
the `cross-phase-interfaces` change trigger applies and Phase 5 owes a fresh verification round
before it can close.

## Resolutions

### candidate-001 — resolved

Re-derived from RC3's general “shadow buffers are used” condition and Phase 3's independent
`shadowDepthBuffers` and `shadowColorBuffers` minima. The planning predicate is now the union of
positive depth or color demand in §3.2 and §4.10. `BufferSizing.shadowExtent`, ordinary
`ShadowEstateNotRequested` absence, and binding §5 now use the same predicate.

For a color-only request, Phase 5 allocates shadowtex0 as the sfb's owned physical depth attachment
so the framebuffer is complete, but does not alter Phase 3's pack-facing depth minimum or require
an unreferenced depth sampler. The test matrices now cover depth-only, color-only, and both-zero
demand and assert the completeness, extent, and absence outcomes. The §5 interface changed, so a
fresh verify round is required before Phase 5 can close.

### Notes deferred

None.
