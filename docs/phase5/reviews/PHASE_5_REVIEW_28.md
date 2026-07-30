# Phase 5 Verification Review — Round 28

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates before consulting prior reviews. I read
the complete Phase 5 target where needed, with particular attention to its public estate
signatures, clear and depth-copy designs, and manifest-declared §5 interface region; the RC3 Part I
rules, mandatory template, Phase 5 specification, and document gate; the applicable contract
ground truth; and the manifest-selected binding regions of Phases 1, 3, and 4. Supporting
implementation material was unnecessary because both candidates concern the completeness and
representability of Phase-5-owned public contracts.

Only after settling both independent dispositions did I read Phase 5 reviews 1 through 27 in
numeric order and compare the candidates with their findings and resolutions. No prior review
defines `DepthCopyResult` or orders the exact `ClearRequest` value shape into §5. I used no
network access, forbidden source, or prior-session transcript. In particular, I did not open
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, because the resolved forbidden-source
rule bars `*.txt` and it was unnecessary. There was no agent fan-out or delegation. In accordance
with the dispatched atomic-role instruction and the verify-loop skill, I did not invoke the loop,
run `scripts/verify`, or start another Codex session. There were no other deviations from the
resolved reading contract, no candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — `DepthCopyResult` is promised but never defined

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:544`–`:567`,
`docs/phase5/v1/PHASE_5_DOC.md:1275`–`:1300`, and
`docs/phase5/v1/PHASE_5_DOC.md:1648`

**Claim.** Phase 5 exposes `copyDepth` to Phase 7 with return type `DepthCopyResult`, but never
declares that type or maps the documented success, duplicate, ordering-rejection, and backend-copy
failure conditions to closed observable outcomes. Phase 7 therefore cannot implement exhaustive
handling without inventing part of the Phase-5-owned interface.

**Evidence.** `BufferEstateView.copyDepth(DepthCopyPoint point, long frameId)` returns
`DepthCopyResult`, while the adjacent clear operation has a declared closed
`ClearExecutionResult` enum (`docs/phase5/v1/PHASE_5_DOC.md:544`–`:567`). The depth-copy state
machine distinguishes successful first and steady copies, duplicate calls, out-of-order requests,
and copy failure with degradation to depthtex0
(`docs/phase5/v1/PHASE_5_DOC.md:1275`–`:1300`), but the target contains no declaration or
exhaustive result mapping for `DepthCopyResult`. Binding §5 merely names the type and says that
Phase 7 owns the call moments (`docs/phase5/v1/PHASE_5_DOC.md:1648`).

The governing assignment gives Phase 7 only the copy moments while Phase 5 owns the textures,
copy mechanics, and lifecycle (`docs/design/v2.0-RC3/DESIGN.md:1617`–`:1619`). The mandatory
template requires §5 to expose the named interfaces and data contracts dependents consume
(`docs/design/v2.0-RC3/DESIGN.md:811`–`:813`). Downstream timing ownership therefore does not cure
the missing result contract.

**Required correction.** Define a closed public `DepthCopyResult` and exhaustively map successful
copy, duplicate/no-op, ordering or protocol rejection, and backend degradation/failure to its
outcomes. Specify state mutation, fallback, diagnostics, and Phase 7 duties for every outcome, and
reproduce the complete binding semantics in §5. A compact taxonomy is acceptable if the mapping
remains exhaustive.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-002 — The §5 clear contract omits the constructible `ClearRequest` value shape

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1020`–`:1029` and
`docs/phase5/v1/PHASE_5_DOC.md:1647`

**Claim.** Phase 7 must construct `ClearRequest`, but the binding §5 region only names the type and
omits its five-field immutable shape. Although §4 presently supplies the declaration, a
load-bearing change to that declaration could occur outside the only interface region whose
alteration mandates renewed verification.

**Evidence.** Detailed design declares the public value as
`ClearRequest(long frameId, float fogRed, float fogGreen, float fogBlue, boolean fullClear)`
(`docs/phase5/v1/PHASE_5_DOC.md:1020`–`:1029`). Binding §5 exposes `ClearRequest` to Phase 7 but
records only effective-full-clear and execution semantics, not the fields Phase 7 must supply
(`docs/phase5/v1/PHASE_5_DOC.md:1647`). The mandatory template assigns exposed data contracts to
§5 (`docs/design/v2.0-RC3/DESIGN.md:811`–`:813`), and the governing process requires fresh
verification specifically when §5 changes
(`docs/design/v2.0-RC3/DESIGN.md:327`–`:329`). Present whole-document readability therefore does
not cure the change-trigger incompleteness.

**Required correction.** Add the exact immutable
`ClearRequest(long frameId, float fogRed, float fogGreen, float fogBlue, boolean fullClear)` shape
to the §5 clear-contract row and briefly define each field's meaning. Keep the detailed clear
algorithm in §4.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported Round-27 surface remains clean on re-derivation. `Extent2i` is now named and
defined consistently in §5, without inventing constructor validation, and the §0.29 addendum
correctly records that interface change and this fresh-review obligation.

The dependency and conformance clean areas also remain clean. Phase 5's consumption of the
manifest-selected Phase 1 borrowed-depth and facade contracts, Phase 3 configuration and resource
requirements, and Phase 4 detached candidate view and registry contracts matches their binding
regions. The conformance map covers the governing Appendix B.1, B.2, B.3, and B.4 requirements,
including the fixed unit-11 ruling, and the detailed flip, clear, sizing, resize, depth, shadow,
format, and Final-framebuffer designs are otherwise coherent.

Neither surviving candidate clears on re-derivation. For candidate-001, prose describing the
depth-copy state machine is not an observable substitute for the undefined public return type.
For candidate-002, §4 makes the request constructible to a whole-document reader today but does
not make the manifest-declared binding and change-trigger region independently complete. Prior
reviews settled related clear execution and other result-carrier defects, but none settled these
two omissions. No candidate was dropped on re-derivation, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are localized cross-phase data-contract corrections and do not require
rebuilding the Phase 5 architecture, so `FAIL` is not warranted. The supplied trend summary is
empty. Direct comparison shows Round 27 admitted one correction, while this round admits two
separate omissions from older public surface; literal convergence has not been reached.

The next required action is a scoped fix-up resolving candidate-001 and candidate-002 and
appending resolutions to this review. Because both corrections must change or clarify the
manifest-declared §5 interface region, the `cross-phase-interfaces` change trigger applies:
Phase 5 owes a fresh verification round before it can close.

## Resolutions

### candidate-001 — resolved

Defined `DepthCopyResult` as a closed four-variant public result and mapped every documented path:
successful initialization/steady copy advances the ordered point and validates the destination;
duplicate calls return a diagnosed mutation-free no-op; stale generation/attachment epoch,
no-frame, wrong-frame, and out-of-order calls return a pre-copy mutation-free rejection that Phase
7 must answer by aborting the shader frame; backend failure returns a diagnosed degradation,
marks the affected destination `DEGRADED_TO_DEPTHTEX0`, installs depthtex0 as its fixed-unit
fallback, and permits the frame to continue with that fallback. The complete observable mapping
and Phase 7 duties are repeated in binding §5.

### candidate-002 — resolved

Expanded the binding §5 clear row with the exact immutable
`ClearRequest(long frameId, float fogRed, float fogGreen, float fogBlue, boolean fullClear)` shape.
It now defines `frameId` as the open-frame identity, the three fog fields as colortex0 RGB, and
`fullClear` as caller intent while retaining the existing estate-owned full-clear combination.

### Notes deferred

None.

Both corrections changed the manifest-declared `cross-phase-interfaces` region. The fresh-review
change trigger applies before Phase 5 can close.
