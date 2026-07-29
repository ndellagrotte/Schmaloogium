# Phase 5 Verification Review — Round 16

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates before consulting prior reviews. The
first pass read the whole target selected by the manifest, the governing Part I, Phase 5
specification, document gate, and mandatory template in
`docs/design/v2.0-RC3/DESIGN.md`, the applicable ground-truth contracts in
`docs/research/v1/RESEARCH.md`, and the binding §5 regions of
`docs/phase1/v14/PHASE_1_DOC.md`, `docs/phase3/v1/PHASE_3_DOC.md`, and
`docs/phase4/v1/PHASE_4_DOC.md`. Supporting material was treated only as evidence, never as
contract. After fixing the independent dispositions, I read
`docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_15.md` last.

I used no network access and no agent fan-out. I did not invoke the verification loop, run
`scripts/verify`, or start another Codex session. I did not read a forbidden source. In
particular, the supplied `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` supporting
source was not opened because the resolved forbidden-source rule bars `*.txt`; it was not needed
to adjudicate either candidate. There were no Gate drops and no candidates eliminated before
adjudication.

## 1. Findings

### candidate-001 — §5 omits the binding SCREEN-terminal handoff to Phase 7

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1405`

**Claim.** A Phase 7 consumer cannot derive from the binding §5 interface region that
`StageId.FINAL` yields no engine FBO and instead requires a platform-owned SCREEN handoff to
Minecraft's framebuffer with Minecraft's anaglyph-aware color mask.

**Evidence.** The governing Phase 5 specification classifies Final rendering to the vanilla
framebuffer as a handoff contract with Phase 7
(`docs/design/v2.0-RC3/DESIGN.md:1647`–`:1649`), and the mandatory template makes §5 the location
for contracts exposed to dependents (`docs/design/v2.0-RC3/DESIGN.md:811`–`:813`). The detailed
design states that `StageId.FINAL` returns no engine FBO, its `SCREEN` terminal requires Phase 7
to bind Minecraft's framebuffer and apply the anaglyph-aware color mask, and the dormant
`SHADOWCOMP`, `PREPARE`, `BEGIN`, and `SETUP` identities remain unwired
(`docs/phase5/v1/PHASE_5_DOC.md:863`–`:865`). The corresponding §5 row says only that one
immutable side snapshot drives FBO and sampler bindings
(`docs/phase5/v1/PHASE_5_DOC.md:1405`), omitting the terminal exception and its caller duties.

**Required correction.** Amend the Phase 7-facing §5 contract to state that
`StageId.FINAL` supplies a `SCREEN` terminal and no engine FBO, and that Phase 7 must bind
Minecraft's framebuffer through the platform path and apply Minecraft's anaglyph-aware color
mask before drawing. Record the dormant-stage disposition there as part of the same routing
surface.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-002 — §5 does not expose the required cross-subsystem publication transaction

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1401`–`:1402`

**Claim.** Phase 7 cannot safely compose and publish the Phase 4 registry and Phase 5 estate from
the binding §5 region because that region exposes component operations but omits their required
cross-subsystem transaction.

**Evidence.** The detailed design requires Phase 7 to build the Phase 5 candidate from the
Phase 4 candidate's read-only view, validate provenance before Phase 4 publication, compose the
Phase 4 barrier, publish Phase 4 first, close the still-caller-owned Phase 5 candidate if that
publication fails, publish the ready Phase 5 candidate next, and permit no intervening draw
(`docs/phase5/v1/PHASE_5_DOC.md:533`–`:537`). Phase 5 also expressly assigns registry-to-estate
publication ordering to Phase 7 (`docs/phase5/v1/PHASE_5_DOC.md:1745`–`:1747`). Section 5,
however, describes planning/build results and the local candidate-publisher ownership outcomes
without stating that composition order, failure cleanup, pre-publication provenance check, or
draw gate (`docs/phase5/v1/PHASE_5_DOC.md:1401`–`:1402`). The mandatory template distinguishes
detailed design from the contracts exposed to dependents
(`docs/design/v2.0-RC3/DESIGN.md:809`–`:813`).

**Required correction.** Add a concise §5 Phase 7 consumer obligation that expresses the
existing transaction over the existing interfaces: inspect and validate registry provenance
before Phase 4 publication, compose and publish Phase 4 first, close the caller-owned Phase 5
candidate if Phase 4 publication fails, publish the ready Phase 5 candidate after Phase 4
succeeds, and keep shader drawing gated until both publications complete. Do not invent a new
combined publisher API.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported new-surface area remains clean on re-derivation. The Round-15 removal of
`runtimeRevision` is consistent throughout the target: `BufferRuntimeInputs` contains exactly
display extent, render quality, and shadow quality, and all three participate by value in
planning identity and reuse. No residual independent runtime revision or rebuild trigger remains.

The conformance map, format and transfer vocabularies, allocation fallback, main and shadow flip
state machines, clear behavior, framebuffer fallback, depth-copy lifecycle, sizing and resize
lifecycle, fixed texture-unit table, and dependency consumption were checked and are otherwise
coherent. The selected Phase 1, Phase 3, and Phase 4 contract names align with their binding
regions; unavailable Phase 1 operations remain explicit requested changes rather than silently
assumed APIs. All thirteen mandatory sections are present, and there are no assigned open
questions.

Neither candidate clears against prior settled material. Round 2 previously ordered the
Final/anaglyph handoff into binding §5, but its resolution records changes only to §§1.2, 1.3,
3.2, and 4.5; the present §5 omission is therefore an incompletely applied settled correction,
not a duplicate finding. Round 1 cleared the narrower proposition that a separate public
provenance-preflight method was required. Candidate-002 instead concerns the already-specified
consumer transaction that composes the existing Phase 4 and Phase 5 interfaces, so that earlier
clearance does not dispose of it.

No surviving candidate was dropped on independent re-derivation. There were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are localized omissions from the declared cross-phase interface region. They do
not require rebuilding the Phase 5 architecture, so `FAIL` is not warranted. Across Rounds 13
through 16 the correction counts are 3, 1, 1, and 2. The count has increased after remaining
flat, so convergence has not been reached; the increase is explained in part by reopening
Round 2's incompletely applied §5 correction.

The next required action is a scoped fix-up resolving candidate-001 and candidate-002 and
appending resolutions to this review. Because both corrections change the declared §5
cross-phase interface region, Phase 5 owes a fresh verification round before it can close.

## Resolutions

### candidate-001 — applied

Amended the §5.1 frame/pass contract to expose the already-designed terminal exception:
`StageId.FINAL` supplies `SCREEN` and no engine FBO; Phase 7 must bind Minecraft's framebuffer
through the platform path and apply Minecraft's anaglyph-aware color mask before drawing. The same
row now records that `SHADOWCOMP`, `PREPARE`, `BEGIN`, and `SETUP` remain dormant and unwired.

### candidate-002 — applied

Amended the existing §5.1 candidate/publication contract, without adding a combined publisher API.
Phase 7 must validate registry provenance before Phase 4 publication, compose and publish Phase 4
first, close its still-owned Phase 5 candidate if that publication fails, publish the ready Phase 5
candidate after Phase 4 succeeds, and gate shader drawing until both publications complete.

### Notes deferred

None. The adjudicator admitted no notes.

Both corrections changed the declared `cross-phase-interfaces` region, so the manifest's change
trigger fires and Phase 5 requires a fresh verify round before closure.
