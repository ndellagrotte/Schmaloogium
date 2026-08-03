## 0. Method and reading order

I independently re-derived all four Gate-surviving candidates from the whole target, the manifest-selected portions of `docs/design/v2.0-RC3/DESIGN.md`, the binding §5 regions of Phases 2–6, and the cited supporting evidence. I then checked the discovered prior-review list last; it was empty because this is Phase 7's first review. I used no network sources, forbidden sources, transcripts, or evidence outside the resolved contract. There was no agent fan-out: this was the canonical engine's already-dispatched atomic adjudication role, so the supplied `verify-loop` skill required that I not invoke the loop or delegate. There were no reading-order deviations, candidates eliminated before adjudication, or Gate drops.

## 1. Findings

### candidate-001 — The public frame result algebras are undefined

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:998`–`:1042`
- **Claim:** The sole hook-facing engine surface is not implementable without guessing because six named closed result types have no defined variants, payloads, state effects, or exhaustive caller obligations.
- **Evidence:** `FrameHookSink` returns `FrameOpenResult`, `FrameStepResult`, `ScopeOpenResult`, `ScopeCloseResult`, `FrameFinishResult`, and `FrameAbortResult` (`docs/phase7/v1/PHASE_7_DOC.md:998`–`:1009`). The surrounding contract specifies validation and value-boundary rules (`docs/phase7/v1/PHASE_7_DOC.md:1033`–`:1037`) and calls these results closed (`docs/phase7/v1/PHASE_7_DOC.md:1041`–`:1042`), but neither it nor the detailed-design occurrence defines their algebra. Phase 7 expressly owns this sole hook-facing surface (`docs/phase7/v1/PHASE_7_DOC.md:195`–`:200`), while the governing template requires exact semantics and exposed data contracts (`docs/design/v2.0-RC3/DESIGN.md:809`–`:813`). Independent consumers therefore cannot distinguish acceptance, rejection, degradation, backend failure, duplicate finalization, or abort effects from the binding contract.
- **Required correction:** Define each public result algebra in §5.1 with its exact closed variants and payloads, mutation and token-lifetime effects, and mandatory caller action for every state-machine outcome already required by the document. Reuse outcomes where their semantics are identical; do not invent an unnecessary variant per prose scenario.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the missing definitions belong to the manifest-selected §5 contract, so the Phase 7 interface change trigger applies.

### candidate-002 — `ShadowInvocationSlot` is not an implementable Phase 8 hand-off

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1054` and `:1167`–`:1177`
- **Claim:** Phase 8 cannot implement the shadow integration solely from Phase 7's binding interface because the named slot has no callable signature or caller-visible context/result contract.
- **Evidence:** The exposed-contract table promises only one call carrying current frame/publication context and a v0.1 `NotInstalled` default (`docs/phase7/v1/PHASE_7_DOC.md:1053`–`:1054`). The downstream hand-off merely repeats the slot name and the requirement to return before the main clear (`docs/phase7/v1/PHASE_7_DOC.md:1167`–`:1172`). Elsewhere the target fixes the invocation moment and delegates v0.2 result handling to Phase 8 (`docs/phase7/v1/PHASE_7_DOC.md:556`–`:560`), but never defines the operation, typed context, ownership, synchronous outcome, or Phase 7 cleanup response. The governing scope excludes Phase 8's shadow-pass content while expressly requiring Phase 7 to leave its invocation slot (`docs/design/v2.0-RC3/DESIGN.md:1913`–`:1915`); defining that seam is therefore Phase 7 work, not shadow-content work.
- **Required correction:** Publish in §5 an exact `ShadowInvocationSlot` callable and minimal closed context/outcome types. Specify frame/publication credentials, ownership, synchronous completion before the main clear, `NotInstalled`, and Phase 7's continuation/cleanup response to rejection, failure, or exception. Leave shadow camera, traversal, framebuffer, program, uniform, content policy, and purely internal Phase 8 failure taxonomy to Phase 8.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — this adds the missing downstream contract inside §5.

### candidate-003 — `ProgramStateBundle` is attributed to the wrong dependency

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1074`–`:1102`
- **Claim:** Phase 7's dependency-consumption ledger incorrectly identifies Phase 3 as the owner of `ProgramStateBundle`, contrary to the selected binding contracts.
- **Evidence:** The Phase 3 table lists “`ProgramStateModel` / `ProgramStateBundle` projections” (`docs/phase7/v1/PHASE_7_DOC.md:1076`–`:1082`). Phase 3 exposes `ProgramStateModel`, `ProgramKey`, `ProgramState`, and `EvaluatedProgramStates`, but not `ProgramStateBundle` (`docs/phase3/v1/PHASE_3_DOC.md:1119`–`:1121`). Phase 4 expressly exposes `ProgramSlotId`, `ProgramSlotDescriptor`, and `ProgramStateBundle` to Phase 7 (`docs/phase4/v1/PHASE_4_DOC.md:1368`–`:1371`). Phase 7's Phase 4 table mentions generic per-slot metadata but does not cure the explicit type/owner mismatch. An implementer following the binding ledger would look for the named type in a dependency that does not publish it.
- **Required correction:** Remove `ProgramStateBundle` from the Phase 3 row and identify the actual Phase 3 projections used. Add `ProgramStateBundle` explicitly to the Phase 4 consumption table as the effective per-slot state/scale/flip/instance projection, without implying that Phase 7 overlays or re-resolves requested state.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the correction changes dependency attribution in the selected §5 interface region.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation:

- **Interfaces:** Phase 2 tier/named-run/capture-schema consumption; Phase 3 discovery/load and internal-pack consumption; Phase 4 barrier composition and requested/effective-provider separation; the explicitly gated Phase 5 main-depth/depth-copy limitations; and Phase 6 frame-begin ordering and three-participant consumption are consistent with the selected dependency contracts. Ungranted requests and their feature gates remain explicit.
- **Conformance:** The Phase 7 specification, RESEARCH frame flow and hook needs, program dispatch, Appendix E catalog, seven-row Pintonium disposition, engine-flag ownership, deferral ledger, Forge/Cleanroom event treatment, OQ-3/OQ-4 spikes, and v0.1 assembly narrative yielded no additional candidate-backed defect.
- **Document gate:** All thirteen mandatory sections are present and substantive; hook needs, timeline dispositions, reference-free sky/weather/cloud risks, assembly narrative, and both assigned spikes meet the selected gate.

`candidate-004` is cleared on re-derivation. Phase 5 states that acceptance transfers ownership and caller `close()` becomes harmless (`docs/phase5/v1/PHASE_5_DOC.md:540`–`:548`). It also states that `ConsumerFailed` replaces and closes the failed estate before returning its recovery signal (`docs/phase5/v1/PHASE_5_DOC.md:1509`–`:1518`). Phase 7 separately recognizes that consumer failure publishes both systems off and repeats that caller close is harmless after transfer (`docs/phase7/v1/PHASE_7_DOC.md:469`–`:472`). Thus §5.3's generic “closes the rejected Phase 5 candidate” wording is less explicit than an exhaustive switch, but it cannot produce the alleged double disposal or ownership failure. No correction or note is admitted for that candidate.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

Three implementability/contract-honesty corrections are required; none requires structural rebuilding. Because all three corrections touch the manifest-declared cross-phase interface region, a fresh verification round is required before Phase 7 can close. With no prior round, there is no convergence trend to compare. The next required action is a scoped fix-up of this review, including its `## Resolutions` record and Phase 7 addendum, followed by a fresh round that re-verifies the changed interface and the corrected whole document.

## Resolutions

### candidate-001 — resolved

Added the six closed hook-result algebras to §5.1, including their payloads, mutation and token-life
effects, and exhaustive adapter obligations. The definitions reuse terminal `Aborted`/`Failed`
semantics where those effects are identical and give duplicate finish/abort the explicit
mutation-free `AlreadyTerminal` result. This changes the declared interface region.

### candidate-002 — resolved

Published `ShadowInvocationSlot.invoke`, its minimal invocation-borrowed context, and the closed
`NotInstalled`, `Completed`, `Rejected`, and `Failed` outcomes in §5.1. The contract now fixes
synchronous return before the main clear, forbids retention/closure of credentials, and defines
Phase 7 continuation, abort, restoration, vanilla fallback, exception containment, and off-recovery
behavior while leaving all shadow content and internal failure taxonomy to Phase 8. This changes
the declared interface region.

### candidate-003 — resolved

Removed `ProgramStateBundle` from the Phase 3 ledger row, named the actual Phase 3 projections used,
and added `ProgramStateBundle` to Phase 4 as the effective per-slot state/scale/flip/instance
projection without permitting requested-state overlay or fallback re-resolution. This changes the
declared interface region.

### Notes deferred

None. The adjudicator admitted no notes.

### Fix-up status

All three admitted corrections were applied. Because §5 changed, Phase 7 is not verified and a
fresh verification round is required before it can close.
