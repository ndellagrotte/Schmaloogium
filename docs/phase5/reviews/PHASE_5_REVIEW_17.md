# Phase 5 Verification Review — Round 17

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates before consulting prior reviews. The
first pass read the target's relevant public-shape, lifecycle, routing, and complete §5 interface
material; the governing Part I, Phase 5 specification, document gate, and mandatory template in
`docs/design/v2.0-RC3/DESIGN.md`; the applicable ground-truth contracts in
`docs/research/v1/RESEARCH.md`; and the manifest-selected binding regions of
`docs/phase1/v14/PHASE_1_DOC.md`, `docs/phase3/v1/PHASE_3_DOC.md`, and
`docs/phase4/v1/PHASE_4_DOC.md`. Supporting material was treated only as evidence, never as
contract. After fixing the independent dispositions, I consulted
`docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_16.md` last.

I used no network access and no agent fan-out. In accordance with the verify-loop skill's
atomic-role rule, I did not invoke the verification loop, run `scripts/verify`, or start another
Codex session. I did not read a forbidden source. In particular, the supplied
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` was not opened because the resolved
forbidden-source rule bars `*.txt`; it was unnecessary to adjudicate these candidates. There
were no Gate drops and no candidates eliminated before adjudication.

## 1. Findings

### candidate-001 — The SCREEN terminal is not representable by `PassBufferSnapshot`

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:506`–`:514`

**Claim.** The public pass-snapshot contract cannot represent the promised `StageId.FINAL`
terminal: every snapshot structurally requires an engine framebuffer handle, while FINAL
expressly has no engine FBO and supplies `SCREEN`.

**Evidence.** The public estate API has only one snapshot operation and it returns
`PassBufferSnapshot` (`docs/phase5/v1/PHASE_5_DOC.md:469`–`:481`). That record's final field is
the mandatory `FramebufferHandle framebuffer`, with no target discriminator, SCREEN variant, or
documented nullable/sentinel convention (`docs/phase5/v1/PHASE_5_DOC.md:506`–`:514`). Conversely,
the hard boundary assigns Phase 5 responsibility for returning a `SCREEN` terminal and Phase 7
responsibility for the subsequent platform bind (`docs/phase5/v1/PHASE_5_DOC.md:251`–`:259`), and
the routing design says FINAL returns no engine FBO
(`docs/phase5/v1/PHASE_5_DOC.md:868`–`:870`). Binding §5 repeats both the SCREEN terminal and the
absence of an engine FBO (`docs/phase5/v1/PHASE_5_DOC.md:1410`). Phase 7 therefore cannot consume
the promised terminal without inventing an uncontracted representation.

**Required correction.** Give the public pass result an explicit closed target representation,
such as engine-FBO and SCREEN variants, and define snapshot creation, binding handoff,
generation/epoch/frame validation, completion, and flip-transition behavior for SCREEN. Update
the §5 contract to name that representation and its caller duties.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-002 — Frame lifecycle result types are undefined

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:472`–`:486`

**Claim.** Phase 7 cannot implement the exposed frame lifecycle without guessing because
`FrameBeginResult` and `FrameEndResult` are return types in the public contract but have no
declarations, closed outcomes, payloads, or caller-duty mapping.

**Evidence.** The document expressly says its illustrative signatures define the cross-phase
contract (`docs/phase5/v1/PHASE_5_DOC.md:281`–`:284`). `BufferEstateView.beginFrame` returns
`FrameBeginResult`, while `commitFrame` and `abortFrame` return `FrameEndResult`
(`docs/phase5/v1/PHASE_5_DOC.md:472`–`:486`). Those names occur nowhere else as declarations:
the target provides neither records/enums/sealed variants nor an equivalent closed outcome set.
The lifecycle prose says `beginFrame` can refuse a non-normalized state and describes successful
abort effects (`docs/phase5/v1/PHASE_5_DOC.md:848`–`:850`), but it does not specify how refusal,
success, stale/protocol rejection, abort diagnostics, or backend failure are represented and
handled. Binding §5 exposes `FrameBeginResult` and the frame-end operations to Phase 7 but does
not even name `FrameEndResult` or fill in those semantics
(`docs/phase5/v1/PHASE_5_DOC.md:1410`). This falls short of the mandatory named data-contract and
exact-lifecycle requirements (`docs/design/v2.0-RC3/DESIGN.md:802`–`:813`) for a flip state
machine the Phase 5 specification makes contract-visible
(`docs/design/v2.0-RC3/DESIGN.md:1671`–`:1674`).

**Required correction.** Define closed `FrameBeginResult` and `FrameEndResult` outcomes, their
payloads and state effects, stale/protocol/backend-failure behavior, and Phase 7 caller duties.
Mirror the complete lifecycle contract in §5 without prescribing private implementation
structure.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported new-surface area remains otherwise clean on re-derivation. The Round-16
publication ordering is internally covered: candidate inspection supports provenance validation
before Phase 4 publication, candidate ownership remains with Phase 7 until Phase 5 acceptance,
failure cleanup is assigned, and shader drawing remains gated until both publications complete.

The dependency-interface lens is otherwise clean. Consumed Phase 3 and Phase 4 symbols match their
selected binding surfaces. Phase 1 limitations involving borrowed depth attachment and depth-copy
operations are disclosed as requested dependency changes rather than silently assumed.
Resize notices, texture-overlay leases, main-depth refresh, and clear execution have materially
specified consumer contracts.

The conformance map and its detailed format, pixel-transfer, flip, clear, depth/shadow, sizing,
growth, and fixed-unit designs were checked and are otherwise coherent. Every in-scope Appendix
B.1, B.2, B.3, and B.4 requirement is mapped, including the unit-11 ruling, the complete format
and pixel-transfer vocabularies, and the fixed texture-unit table.

Neither candidate is cleared by prior settled material. Round 16 corrected the omission of the
SCREEN handoff from §5 but did not change the mandatory-framebuffer snapshot shape, so
candidate-001 identifies the representational consequence of that correction rather than
reopening the settled prose requirement. Earlier reviews repeatedly required closed, named
outcome contracts for adjacent public operations, but no resolution defines
`FrameBeginResult` or `FrameEndResult`; candidate-002 is therefore neither duplicate nor settled.
No surviving candidate was dropped on re-derivation, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are localized public-contract defects and do not require rebuilding the Phase 5
architecture, so `FAIL` is not warranted. The round is not converged: Round 17 retains two
corrections after Round 16's two corrections, and one is a newly exposed representational
inconsistency in the corrected SCREEN surface.

The next required action is a scoped fix-up resolving candidate-001 and candidate-002 and
appending resolutions to this review. Because both corrections change the declared §5
cross-phase interface or its public types, the interface change trigger applies and Phase 5 owes
a fresh verification round before it can close.

## Resolutions

### candidate-001 — applied

Re-derivation confirmed the mismatch: the governing assignment requires Final to hand off to the
vanilla framebuffer, while the former `PassBufferSnapshot` shape required an engine framebuffer
handle. The target now defines closed `PassDrawTarget.EngineFramebuffer(handle)` and
`PassDrawTarget.Screen.INSTANCE` alternatives and carries that target in every snapshot.
`StageId.FINAL` produces SCREEN with no engine handle. SCREEN remains an ordinary open snapshot for
generation, depth-attachment-epoch, frame-token, exactly-once completion, and recorded flip
transition purposes; Phase 7 performs the platform bind and anaglyph-aware color mask before the
draw and then completes it. §4.5 and binding §5.1 now state the same handoff and caller duties.

### candidate-002 — applied

Re-derivation against the mandatory named-data and lifecycle requirements confirmed that public
return-type names alone were insufficient. The target now declares closed `FrameBeginResult` and
`FrameEndResult` variants plus `FrameProtocolRejection`. It specifies successful token
installation, commit rebase/normalization, abort consumption of an open snapshot and mandatory
full clear, rejection-without-mutation, backend-failure normalization/staleness/recovery, and the
Phase 7 duty for every outcome. Binding §5.1 mirrors those state effects and duties without
exposing private implementation structure.

### Notes deferred

None.

### Fix-up status

Both admitted corrections were applied. The compact `§0.19` addendum records only their scope.
Because binding §5.1 changed, the `cross-phase-interfaces` change trigger fires and Phase 5 owes a
fresh verification round before it can close.
