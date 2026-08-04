## 0. Method and reading order

I first re-derived both candidates from the complete Phase 6 target, the manifest-selected v3
governing selectors, RESEARCH.md, the Phase 1/3/4 binding contracts, and the cited supporting
evidence. I then read Reviews 1–15, in order, only after reaching an independent disposition.
There were no deviations from the resolved contract, no network use, and no agent fan-out. The
Gate reported no drops, and no candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — Terminal `CLOSE` still permits an ordinary Phase 4 publication replacement

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:1228-1235`,
  `docs/phase6/v1/PHASE_6_DOC.md:1258`, and
  `docs/phase6/v1/PHASE_6_DOC.md:1388-1389`
- **Claim:** The Review-15 correction does not consistently restrict terminal `CLOSE` to the
  caller-controlled boundary before Phase 4 teardown; the normative lifecycle and exported
  interface still permit `CLOSE` before ordinary publication replacement.
- **Evidence:** Section 4.14 makes `CLOSE` permanent and forbids later adoption, while assigning
  `PACK_REPLACEMENT` exclusively to `adoptRegistryGeneration`
  (`docs/phase6/v1/PHASE_6_DOC.md:1228-1231`). Nevertheless, its ordering rule says that after
  `CLOSE`, Phase 7 initiates Phase 4's atomic “publication replacement or teardown” operation
  (`docs/phase6/v1/PHASE_6_DOC.md:1232-1235`). The binding §5 interface repeats that same choice
  (`docs/phase6/v1/PHASE_6_DOC.md:1258`), and the threading table separately assigns replacement
  to generation adoption yet still places direct `CLOSE` before “publication replacement or
  teardown” (`docs/phase6/v1/PHASE_6_DOC.md:1388-1389`). This is internally inconsistent with the
  teardown-only Review-15 addendum (`docs/phase6/v1/PHASE_6_DOC.md:180-183`). A Phase 7 consumer
  following §5 could permanently retire the runtime on an ordinary replacement and thereby make
  the required `PACK_REPLACEMENT` adoption impossible. Review 15's resolution introduced the
  broader wording; no earlier settled contract legitimizes closing on replacement.
- **Required correction:** In §§4.14, 5.1, and 7.1, restrict terminal `CLOSE` to the boundary
  before Phase 4 teardown. Keep ordinary publication replacement exclusively on
  `adoptRegistryGeneration(..., PACK_REPLACEMENT)`, and add a lifecycle test assertion that
  replacement does not invoke `CLOSE`.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-002 — The `blendFunc` conformance row cites the wrong Appendix E hook row

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:408`
- **Claim:** The conformance map's exact authoritative citation does not support its claimed
  GlStateManager-based `blendFunc` observation.
- **Evidence:** The map cites `docs/research/v1/RESEARCH.md:1413` for GlStateManager cooperation
  (`docs/phase6/v1/PHASE_6_DOC.md:408`), but that coordinate is the
  `TileEntityRendererDispatcher` per-tile-entity ID hook
  (`docs/research/v1/RESEARCH.md:1413`). The actual GlStateManager class row for blend/alpha state
  observation and the `blendFunc` uniform is at `docs/research/v1/RESEARCH.md:1415`. Equivalent
  architectural coverage in §4.12 does not repair a false authoritative pointer in the required
  conformance map. Prior reviews did not settle this exact citation.
- **Required correction:** Change the citation on the conformance-map row from
  `docs/research/v1/RESEARCH.md:1413` to `docs/research/v1/RESEARCH.md:1415`.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

I rechecked the Review-15 lifecycle edit across §0.15, §4.14, §5.1, §7.1, the Phase 4 atomic
publication/teardown contract, and all target occurrences of `CLOSE`, replacement, teardown,
participant use, reset reasons, and generation adoption. The permanent-close rule, closed reset
reason partition, final-participant-use boundary, world-epoch ordering, and render-thread ownership
are otherwise consistent. The sole remaining lifecycle defect is candidate-001's inclusion of
ordinary replacement.

The governing Phase 6 scope, mandatory template, doc gate, complete conformance map, and binding
Phase 1/3/4 contracts were checked. The Appendix D inventory, cadence and caching model, fixed
sampler maps, frame-begin ordering, smoothing, center-depth decision, provider seam, notifier
audit, barrier trace, tests, and milestone tags have implementable coverage. Candidate-002 is the
only unsupported coordinate found in the conformance map. No candidate was refuted or cleared on
re-derivation, and the prior-review-last reading supplied no settled material that displaces either
finding.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded corrections rather than structural misses requiring a rebuild, so
`FAIL` is not warranted. Literal `PASS` is unavailable while the two corrections remain.
Candidate-001 requires changing the manifest-selected `cross-phase-interfaces` region, so the
interface change trigger applies. The next required action is a governed fix-up resolving both
candidates and appending resolutions to this review, followed by a fresh verification round before
Phase 6 can close.

Trend: correction counts for Rounds 9–16 are `1 -> 3 -> 1 -> 2 -> 1 -> 2 -> 1 -> 2`. The latest
three rounds are `2 -> 1 -> 2`, so corrections are not strictly decreasing. Candidate-001 is
residual inconsistency from the preceding lifecycle correction and candidate-002 is a localized
citation defect. This warrants explicit convergence attention during fix-up, but neither defect
justifies structural escalation.

## Resolutions

### candidate-001 — resolved

Re-derived from Phase 6's closed reset partition and Phase 4's distinct accepted-publication and
teardown paths. Sections 4.14, 5.1, and 7.1 now make `CLOSE` teardown-only and state that ordinary
publication replacement never invokes it; replacement remains exclusively
`adoptRegistryGeneration(..., PACK_REPLACEMENT)`. `ResetLifecycleTest` now asserts that negative
case. The §5 interface region changed intentionally, so a fresh verify round is required.

### candidate-002 — resolved

Checked Appendix E directly: `docs/research/v1/RESEARCH.md:1415` is the GlStateManager class row for
blend/alpha observation and the `blendFunc` uniform. The conformance-map citation now points there.

### Notes deferred

None. The adjudicator admitted no notes.
