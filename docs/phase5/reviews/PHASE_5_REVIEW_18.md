# Phase 5 Verification Review — Round 18

## 0. Method and reading order

I independently re-derived the sole Gate-surviving candidate before consulting prior reviews. The
first pass read the target's public frame/pass API, pass-transition design, recovery table, and
complete §5 interface region; the governing Phase 5 specification, document gate, and mandatory
template in `docs/design/v2.0-RC3/DESIGN.md`; the applicable ground-truth material in
`docs/research/v1/RESEARCH.md`; and the manifest-selected binding regions of
`docs/phase1/v14/PHASE_1_DOC.md`, `docs/phase3/v1/PHASE_3_DOC.md`, and
`docs/phase4/v1/PHASE_4_DOC.md`. Supporting material was treated only as evidence, never as
contract. After settling the candidate's disposition, I consulted
`docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_17.md` last.

I used no network access and no agent fan-out. In accordance with the verify-loop skill's
atomic-role rule, I did not invoke the verification loop, run `scripts/verify`, or start another
Codex session. I did not read a forbidden source. In particular, the supplied
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` was not opened because the resolved
forbidden-source rule bars `*.txt`; it was unnecessary to adjudicate the candidate. There were no
deviations from the resolved reading contract, no candidates eliminated before adjudication, and
no Gate drops.

## 1. Findings

### candidate-001 — Pass snapshot acquisition and completion lack observable rejection outcomes

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:483`–`:486`,
`docs/phase5/v1/PHASE_5_DOC.md:553`–`:560`, and
`docs/phase5/v1/PHASE_5_DOC.md:1465`

**Claim.** Phase 7 cannot implement the promised closed, generation/attachment-epoch/frame-checked
pass lifecycle because snapshot acquisition returns an unconditional `PassBufferSnapshot` and
completion returns `void`. Neither operation can report whether Phase 5 accepted the operation or
rejected a stale, wrong-frame, foreign, duplicate, or otherwise invalid snapshot.

**Evidence.** The public view declares
`PassBufferSnapshot snapshot(PassDescriptor pass, ResolvedProgramDescriptor program)` and
`void completePass(PassBufferSnapshot snapshot)`
(`docs/phase5/v1/PHASE_5_DOC.md:483`–`:486`). The detailed contract says every mutating method
rejects stale generation, attachment epoch, or frame token before GL; `completePass` accepts
exactly one open snapshot; duplicate or foreign snapshots are diagnosed without mutation; and a
SCREEN snapshot remains generation/epoch/frame checked
(`docs/phase5/v1/PHASE_5_DOC.md:553`–`:560`). The binding §5 contract exposes this as a closed,
checked lifecycle to Phase 7 and requires Phase 7 to complete SCREEN under the same validation and
flip rules (`docs/phase5/v1/PHASE_5_DOC.md:1465`).

No alternate result, sentinel, exception, or pre-validation contract closes the gap. The recovery
table requires a stale accepted view or pass snapshot to perform no GL work and directs the caller
to reacquire the current publication (`docs/phase5/v1/PHASE_5_DOC.md:1569`), while the document
also states that no buffer failure throws through the frame driver
(`docs/phase5/v1/PHASE_5_DOC.md:1574`–`:1575`). A diagnostic alone cannot tell Phase 7 whether
snapshot acquisition succeeded or completion consumed the open token and applied its flip
transition.

**Required correction.** Define closed observable outcomes for pass snapshot acquisition and pass
completion, including success and every applicable `FrameProtocolRejection`, and state Phase 7's
branching and recovery duties consistently in §§2.2, 4.4.2, and 5.1. If a particular invalid state
is structurally impossible at one operation, narrow the corresponding rejection promise and
document the enforcing invariant instead.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported new-surface area remains otherwise clean on re-derivation. The Round-17
`FrameBeginResult` and `FrameEndResult` variants, commit/abort asymmetry, SCREEN target
representation, FINAL routing, recovery duties, and repeated identifiers are internally
consistent.

Dependency consumption is otherwise honest. Phase 5 confines the unavailable Phase 1 operations
to explicit requested changes, and its Phase 3 and Phase 4 consumption matches their selected
binding contracts. The conformance map and detailed format, transfer, flip, clear, depth/shadow,
sizing, resize, FINAL-handoff, and fixed-unit designs were checked and are otherwise coherent.
Every in-scope Appendix B.1, B.2, B.3, and B.4 requirement is mapped, including the unit-11
ruling. All thirteen mandatory sections are present, and there are no assigned open questions.

Prior settled material does not clear candidate-001. Round 17 added closed results for frame begin
and frame end and added the SCREEN draw-target representation, but its resolution left pass
snapshot acquisition unconditional and pass completion `void`. No earlier resolution defines an
equivalent result, sentinel, or exception contract for these two operations. No surviving
candidate was dropped on re-derivation, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

One localized cross-phase contract correction is admitted. It does not require rebuilding the
Phase 5 architecture, so `FAIL` is not warranted. The correction count falls from two in Round 17
to one in Round 18, which is directional improvement, but literal convergence has not been
reached because a correction remains.

The next required action is a scoped fix-up resolving candidate-001 and appending its resolution
to this review. Because the correction changes the public pass-lifecycle contract and declared
§5 cross-phase interface region, the interface change trigger applies: Phase 5 owes a fresh
verification round before it can close.

## Resolutions

### candidate-001 — resolved

Re-derived from the public lifecycle rather than accepting the finding as evidence. The
unconditional acquisition and `void` completion signatures could not let Phase 7 distinguish
accepted operations from mutation-free protocol rejection, while the existing no-throw recovery
rule excludes an implicit exception channel.

The target now defines `PassSnapshotResult` (`Acquired` / `Rejected`) and
`PassCompletionResult` (`Completed` / `Rejected`), changes both public method signatures, and
enumerates the applicable rejection sets. Stale generation, stale attachment epoch, wrong-frame,
no/open-frame, and foreign/duplicate/consumed-token cases are observable; the latter share the
closed `INVALID_PASS_SNAPSHOT` reason. Every rejection is explicitly no-GL and mutation-free.
Phase 7 binds or draws only after `Acquired`, advances only after `Completed`, corrects ordering or
reacquires the current publication after acquisition rejection, and aborts the frame before
completion-rejection recovery. The same rule now explicitly covers SCREEN completion.

The correction was swept through §§2.2, 4.4.2, 5.1, and 6. A compact §0.20 addendum records only
the change. The §5 interface changed, so the manifest trigger requires a fresh verification round
before Phase 5 can close.

### Notes deferred

None.
