# Phase 5 Verification Review — Round 15

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
`docs/phase5/reviews/PHASE_5_REVIEW_14.md` last.

I used no network access and no agent fan-out. I did not invoke the verification loop, run
`scripts/verify`, or start another Codex session. I did not read a forbidden source. In
particular, the supplied `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` supporting
source was not opened because the resolved forbidden-source rule bars `*.txt`; it was not needed
to adjudicate either candidate. There were no Gate drops and no candidates eliminated before
adjudication.

## 1. Findings

### candidate-002 — `BufferRuntimeInputs.runtimeRevision` has no defined contract semantics

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:332`–`:336`,
`docs/phase5/v1/PHASE_5_DOC.md:434`–`:441`, and
`docs/phase5/v1/PHASE_5_DOC.md:1394`–`:1396`

**Claim.** Phase 7 cannot construct the exposed `BufferRuntimeInputs` contract, and Phase 5
cannot deterministically consume it, without guessing what `runtimeRevision` represents and
whether it affects planning, equality or reuse, or rebuild classification.

**Evidence.** The public record declares display extent, render quality, shadow quality, and
`long runtimeRevision` (`docs/phase5/v1/PHASE_5_DOC.md:332`–`:336`). The planning contract calls
the request the complete immutable planning input, validates runtime extents and multipliers, and
requires deterministic, value-equal planning from equal planning fields
(`docs/phase5/v1/PHASE_5_DOC.md:434`–`:441`), but supplies no producer, admissible domain,
comparison rule, or semantic effect for the revision. Binding §5 exposes
`BufferRuntimeInputs` as a Phase-7-owned input to `plan/create`
(`docs/phase5/v1/PHASE_5_DOC.md:1394`–`:1396`), while the only later clarification describes
display/render/shadow-quality values and still does not define the revision
(`docs/phase5/v1/PHASE_5_DOC.md:1435`–`:1436`). The governing template requires exact semantics
for public data contracts (`docs/design/v2.0-RC3/DESIGN.md:809`–`:813`).

**Required correction.** Remove `runtimeRevision` if Phase 5 does not consume it. Otherwise,
define its producer and admissible/equality semantics and state its exact participation, or
explicit non-participation, in planning identity, plan reuse, and resize/rebuild classification.
Mirror that rule in §5.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. `BufferSizing` consistently uses
structural equality across main extent, optional shadow extent, and supersampling level.
`BufferInventory` consistently represents each logical texture once without exposing physical
ping-pong sides or GL handles. Candidate inspection remains metadata-only. Registration
rejection, acknowledgement baselines, consumer-failure counts, dependency consumption, the
fixed texture-unit table, and the mapped framebuffer contracts are internally consistent.

Candidate-001 is cleared. The declaration-order rule does not make
`COLOR_INVENTORY_OR_FORMAT` unreachable: creation may replace all requested color formats with
private `RGBA_COMPAT` after an allocation error, incomplete framebuffer, or internal-format
capability rejection (`docs/phase5/v1/PHASE_5_DOC.md:944`–`:952`). The published inventory
exposes the final fallback-resolved formats (`docs/phase5/v1/PHASE_5_DOC.md:443`–`:447`).
Consequently a replacement estate can have a changed effective color format while the pack and
registry fingerprints remain unchanged, leaving neither earlier generic reason applicable. The
candidate's premise that every color format change necessarily coincides with
`PACK_CONFIGURATION` or `REGISTRY_PLAN` is therefore false; reordering the binding reason
priority is not justified.

The governing Phase 5 scope and document gate, all thirteen mandatory sections, the selected
Phase 1/3/4 binding regions, the conformance map, and the exposed/consumed contract tables were
checked. No additional candidate may be created by adjudication. Prior reviews do not settle
candidate-002: `runtimeRevision` appears only in the current public record declaration, and none
of Rounds 1–14 defines its semantics. Round 14's fix-up introduced the current sizing, inventory,
notice, and reason contracts, but it did not address this runtime-input field.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

One localized interface correction is admitted. It does not require rebuilding the Phase 5
architecture, so `FAIL` is not warranted. Rounds 13, 14, and 15 have respectively three, one,
and one correction: the count remains low but is flat in this round, so literal convergence has
not been reached.

The next required action is a scoped fix-up resolving candidate-002 and appending its resolution
to this review. Because the correction removes or defines a field in the declared §5
cross-phase input contract, the interface change trigger applies and Phase 5 owes a fresh
verification round before it can close.

## Resolutions

### candidate-002 — corrected

Re-derivation found no Phase-5 operation that consumes an independent runtime revision: planning
derives its runtime-sensitive sizing and rebuild distinctions solely from display extent, render
quality, and shadow quality. Defining a new revision producer or effect would therefore invent
policy without authority. `runtimeRevision` was removed from `BufferRuntimeInputs`.

The adjacent planning contract now states that the three remaining runtime fields participate by
value in planning identity and reuse and that no separate runtime revision or rebuild trigger
exists. Binding §5 mirrors both the exact field inventory and that identity/reuse rule, and its
Phase 3 handoff clarification now identifies those three values as the complete runtime portion.
The compact §0.17 addendum records only the change. The §5 interface region changed, so Phase 5
requires a fresh verification round before closure.

### Notes deferred

None.
