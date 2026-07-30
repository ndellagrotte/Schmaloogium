# Phase 5 Verification Review — Round 29

## 0. Method and reading order

I independently re-derived all three Gate-surviving candidates before consulting prior reviews. I
read the Phase 5 target, focusing on its header and compact addenda, resize declarations,
depth-copy design, and manifest-declared §5 interface region; the RC3 Part I dependency-contract
rules, mandatory template, Phase 5 specification, and document gate; and the manifest-selected
binding regions of Phases 1, 3, and 4. Supporting implementation evidence was unnecessary because
the candidates concern internal consistency and completeness of Phase-5-owned contracts.

Only after settling those independent dispositions did I read Phase 5 reviews 1 through 28 in
numeric order and compare the candidates with their findings and resolutions. The Round-28
resolution establishes the closed depth-copy result contract but does not settle the stale
`FailedSafe` sentence. No prior review settles the stale §0.29 header pointer or enumerates the
resize-reason vocabulary in binding §5.

I used no network access, forbidden source, or prior-session transcript. I did not open
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, because the resolved forbidden-source
rule bars `*.txt` and it was unnecessary. There was no agent fan-out or delegation. In accordance
with the dispatched atomic-role instruction and the verify-loop skill, I did not invoke the loop,
run `scripts/verify`, or start another Codex session. There were no deviations from the resolved
reading contract, no candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — Depth-copy prose retains the undefined `FailedSafe` outcome

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1311`–`:1325`

**Claim.** The Round-28 fix-up did not consistently map out-of-order depth-copy requests to the new
closed `DepthCopyResult` contract. The neighboring prose still names an undefined apparent result
that contradicts the immediately following exhaustive mapping.

**Evidence.** The state-machine discussion says, *"an out-of-order request is `FailedSafe` for the
current shader frame"* (`docs/phase5/v1/PHASE_5_DOC.md:1311`–`:1312`). The adjacent result
contract instead says `Rejected` covers `DEPTH_COPY_OUT_OF_ORDER`, requires Phase 7 to abort the
current shader frame, and declares `Copied`, `DuplicateIgnored`, `Rejected`, and
`BackendDegraded` exhaustive (`docs/phase5/v1/PHASE_5_DOC.md:1317`–`:1325`). `FailedSafe` is not
defined as an alias or frame state elsewhere in the target.

**Required correction.** Replace `FailedSafe` with
`Rejected(DEPTH_COPY_OUT_OF_ORDER)` while retaining Phase 7's duty to abort the current shader
frame.

**Severity:** correction

**touches interface/change-trigger region: no**

### candidate-002 — The header points to the wrong latest fix-up addendum

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:13` and
`docs/phase5/v1/PHASE_5_DOC.md:267`–`:270`

**Claim.** The compact header inaccurately identifies §0.29 as the latest revision even though the
document contains the later Round-28 fix-up at §0.30.

**Evidence.** The header says, *"Last revised: 2026-07-29 (§0.29)"*
(`docs/phase5/v1/PHASE_5_DOC.md:13`). The addendum sequence continues through
`### 0.30 Round-28 fix-up`, which records the closed depth-copy result and clear-request changes
(`docs/phase5/v1/PHASE_5_DOC.md:267`–`:270`). Thus the repeated latest-revision metadata is
internally stale.

**Required correction.** Change the header pointer from `§0.29` to `§0.30`.

**Severity:** correction

**touches interface/change-trigger region: no**

### candidate-003 — Binding §5 omits the ordered closed resize-reason vocabulary

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:401`–`:410` and
`docs/phase5/v1/PHASE_5_DOC.md:1677`

**Claim.** Binding §5 promises a closed resize reason with declaration-order priority for
simultaneous changes, but it supplies neither the enum members nor their declaration order.
Phases 13 and 14 therefore cannot implement that behavior from the dependency contract alone.

**Evidence.** The detailed public declaration orders the closed values as `DISPLAY_EXTENT`,
`RENDER_QUALITY`, `MAIN_DEPTH_EXTENT`, `SHADOW_RESOLUTION`, `SHADOW_QUALITY`,
`PACK_CONFIGURATION`, `REGISTRY_PLAN`, and `COLOR_INVENTORY_OR_FORMAT`
(`docs/phase5/v1/PHASE_5_DOC.md:401`–`:410`). The binding resize row merely promises *"one closed
rebuild reason with declaration-order priority for simultaneous changes"* without listing that
declaration (`docs/phase5/v1/PHASE_5_DOC.md:1677`). RC3 makes dependency §5 the surface downstream
phases build against (`docs/design/v2.0-RC3/DESIGN.md:269`–`:271`) and requires every promise to
dependents to be specified rather than gestured at
(`docs/design/v2.0-RC3/DESIGN.md:291`–`:292`). Because order is behaviorally significant here,
the detailed declaration outside §5 is not equivalent binding coverage.

**Required correction.** Amend the §5.1 resize-contract row to list all eight
`BufferResizeReason` members in the exact order above and state that the listed order is the
priority when multiple changes occur simultaneously.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported new Round-28 surface is otherwise clean on re-derivation. The exact
`ClearRequest` shape and semantics agree between detailed design and binding §5. Apart from the
single stale sentence in candidate-001, the four `DepthCopyResult` variants, state mutations,
fallback behavior, Phase 7 duties, tests, and binding row consistently cover success,
duplicate/no-op, protocol rejection, and backend degradation.

The interface and conformance clean areas also remain clean. The manifest-selected Phase 1,
Phase 3, and Phase 4 binding contracts match their consumption in Phase 5. Publication,
frame/pass, clear, flip, depth, shadow, texture-overlay, sizing, format, and fixed-unit obligations
otherwise have equivalent detailed and binding coverage. The conformance map covers the governing
Appendix B.1, B.2, B.3, and B.4 requirements, including the unit-11 ruling and the Phase 5
document gate.

No candidate clears on re-derivation. `FailedSafe` cannot be harmless shorthand where the prose
assigns a backticked outcome immediately before an exhaustive result algebra. The §0.29 header
pointer is objectively superseded by §0.30. Finally, naming `BufferResizeReason` in §5 does not
make its promised declaration-order priority implementable without the declaration itself.
Prior-round resolutions do not settle any of these defects. No candidate was dropped during
adjudication, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three admitted defects are localized corrections; none requires rebuilding the Phase 5
architecture, so `FAIL` is not warranted. Candidate-001 corrects contradictory depth-copy prose,
candidate-002 repairs revision metadata, and candidate-003 completes a dependent-facing contract.

The supplied trend summary is empty. Direct prior-review comparison shows Round 28 introduced the
closed depth-copy contract and changed §5, while this round finds two residual consistency defects
and one separate omission in that binding surface. Literal convergence has not been reached.

The next required action is a scoped fix-up resolving candidate-001, candidate-002, and
candidate-003 and appending resolutions to this review. Because candidate-003 requires a change to
the manifest-declared `cross-phase-interfaces` region, the change trigger applies: Phase 5 owes a
fresh verification round before it can close.

## Resolutions

### candidate-001 — applied

Re-derived against the neighboring exhaustive `DepthCopyResult` mapping. The stale `FailedSafe`
word was replaced with `Rejected(DEPTH_COPY_OUT_OF_ORDER)`, and the sentence now expressly retains
Phase 7's duty to abort the current shader frame.

### candidate-002 — applied

The header now points to §0.31, the latest addendum after this fix-up. Pointing it only to §0.30
would have become stale as soon as the required Round-29 addendum was added.

### candidate-003 — applied

Binding §5.1 now enumerates all eight `BufferResizeReason` members in their declared order and
states that this order controls priority for simultaneous changes. This changes the
manifest-declared `cross-phase-interfaces` region, so a fresh verify round is required before
Phase 5 can close.

### Notes deferred

None.
