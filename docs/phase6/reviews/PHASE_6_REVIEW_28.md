# Phase 6 — Fresh Whole-Document Architecture Review 28

## Verdict

**PASS** — the frozen Phase 6 owner architecture is ready as an independently reviewed architecture input, subject to the separately retained dependency-verification and final-integration gates.

- **Owner:** `docs/phase6/v1/PHASE_6_DOC.md`
- **Frozen SHA-256:** `1f2f74dc2eb5da3e882699246b4475ac97c73729ed65b7ca12f39206083702f5` (the coordinator’s independently confirmed frozen state).
- **Blocking findings:** 0
- **Required owner corrections:** 0
- **Receiver correction notes:** 0
- **section5Impact:** false — this review requires no owner interface or incorporated-contract change.

This PASS covers the current whole Phase 6 architecture, not merely the R27 changes. It does not certify implementation, runtime behavior, another phase, or final IR-01 closure.

## Scope and governing evidence

I read the entire assigned document, including all thirteen sections, its historical maintenance addenda, current §5 contracts, failure and lifetime rules, milestones, and implementation checklist. The governing review basis was `docs/design/v2.0-RC3/DESIGN.md`, complete Part I (§G0–§G12) and the complete Phase 6 assignment at lines 1689–1802, including §G1.3 and §G5.3. I also read `AGENTS.md`, `docs/MOVES.md`, the relevant BUILD readiness context, and the required RESEARCH material: §§0–1.3, the barrier/frame sequence, §§8–9, the applicable licensing/risk material, and Appendices A.3, B.3, D and F.6. The current seven-name expression exclusion union was checked in RESEARCH §3.4/F.6.

Dependency review included the complete §5 exports of Phases 1, 3, 4 and 5, with the incorporated declarations and lifetime/failure sections needed to evaluate the Phase 6 contracts. Receiver checks were narrowly scoped to the actual dispatch and consumption paths in Phases 2, 7, 8, 9, 11 and 13. I read the integration report’s original findings, resolutions, and subsequent maintenance entries to distinguish current requirements from preserved historical evidence. Superseded `/3` capture references, old close terminology, and earlier ungranted-interface statements were not treated as alternate active contracts.

## Whole-owner assessment

### 1. Construction, ownership and implementation reachability

Phase 6 §§1–2 and 4.1 establish a loader-neutral provider boundary and a concrete eight-argument factory. The fixed-sampler resolver and replay observer are required construction inputs, not optional future wiring. Their construction failures are defined without allocating a partially usable runtime or starting GL/callback work. Required Phase 1 integer/vector/matrix upload operations are actually present in its `UniformService` contract (`docs/phase1/v14/PHASE_1_DOC.md:3204–3220`). Phase 3 publishes the structural declaration algebra needed for exact-name/type checking (`docs/phase3/v1/PHASE_3_DOC.md:1380–1460`), and Phase 4 exposes the corresponding effective-program layout and scoped uniform capability. No source parsing, raw program handle, or texture-allocation authority is smuggled into Phase 6.

### 2. Uniform inventory, mathematics and temporal semantics

Phase 6 §4.4 covers the governing Appendix D built-ins with types, values, provider ownership, acquisition cadence, activation behavior and milestone placement. I checked the inventory against RESEARCH Appendix D and the shipped `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:123–266` tables, applying the explicit governing resolutions where the historical sampler table differs.

Sections 4.3–4.7 distinguish value acquisition from activation uploads, retain exact-value skipping only where permitted, and always upload matrix values. The smoothing formula has an explicit tick domain, first-value/zero-half-life/regression behavior, independent wetness/dryness selection, and defined eye-brightness truncation. Frame-begin sampling and previous-image depth acquisition precede resize/clear; current camera matrices are captured later at the separate post-camera hook. Missing or singular matrix data has a bounded, specified effect rather than contaminating unrelated uniforms. The accepted timing snapshot is exported from the actual runtime state; P7’s timing receipt reads and validates that snapshot rather than reconstructing it (`docs/phase7/v1/PHASE_7_DOC.md:2960–3014`).

### 3. CPU depth choice and sole sampler policy

Section 4.8 completes both requested center-depth candidates and selects the permitted synchronous CPU path. It explains why the object-like GPU macro would also rewrite a declaration under the actual Phase 3 placement, identifies the absent fixed-unit grant, and returns `MacroContribution.Empty` rather than an unusable redirect. The governing clarification request in §5.4 is not a prerequisite for the selected CPU design.

Section 4.9 consumes Phase 5’s pure `FixedSamplerResolver` as the sole policy authority. Phase 5’s declarations and §4.12.1 (`docs/phase5/v1/PHASE_5_DOC.md:947–998,2276–2335`) supply the matching closed `Ready`/`Invalid` outcomes, declaration-sensitive shadow handling and deterministic unit/name order. Phase 6 handles both outcomes without inventing another name-to-unit table, reparsing GLSL, or rebinding texture objects.

### 4. Barrier order and R27 correction

The active flow in §§2.3, 4.10 and 5 agrees with Phase 4’s incorporated §§4.10–4.11 (`docs/phase4/v1/PHASE_4_DOC.md:1506–1905`): authenticate selection, invalidate the predecessor token, close its lease, bind the selected shader, acquire the effective lease, publish/observe actual effective state, and only then enter sampler, built-in and custom participants. Phase 6 consumes the observed effective blend state; it no longer predicts a lock to be acquired after its upload.

The receiving path is present: P7’s `UniformSignal` dispatch and D-P7-43 route the required effective-state notification to the P6 event sink before participants, and reject failed delivery rather than silently continuing (`docs/phase7/v1/PHASE_7_DOC.md:2750–2820`). Phase 1’s D-P1-57/D-P1-58 contracts provide implementable lease acquisition/restoration and facade-cache behavior (`docs/phase1/v14/PHASE_1_DOC.md:3355–3448`). The former order mismatch is therefore resolved in the actual producer and receiver contracts.

### 5. Closed failure results and replay transport

Phase 6 §4.11 preserves original error order and cardinality, separates attributable replay failures from unattributable/clean replay, and never substitutes probe errors for original evidence. Observer rejection/throw latches `phase6.replay.delivery.failed`, stops further uploads and reaches existing barrier degradation or the immediate-event exception path.

This is consumed, not merely emitted. P7’s D-P7-38 collector receives reports from candidate and retained runtimes, preserves originals before downstream delivery, latches storage failure and prevents drawing on failed delivery (`docs/phase7/v1/PHASE_7_DOC.md:2048–2108`). P8 checks the same failure before shadow draws and carries it through cleanup (`docs/phase8/v1/PHASE_8_DOC.md:829–889`). P2’s current `/4` capture rules flatten the ordered original errors and fail T0/capture on the specified error or evidence-loss paths (`docs/phase2/v1/PHASE_2_DOC.md:1270–1296,2370–2410`). No uncovered no-op receiver was found for these outcomes.

### 6. Custom bridge, events and terminal lifetimes

Section 4.13 has an immutable load-time input schema, the authoritative seven-name exclusion union, exact declared-type validation, boolean encoding owned by P6, and closed submission/refresh outcomes. P11 explicitly distinguishes `Accepted`, `SkippedAbsent` and `Rejected`, as well as `Completed` and `Aborted` prefix counters (`docs/phase11/v1/PHASE_11_DOC.md:700–760,1007–1038`). Invalid counter accounting discards the batch rather than committing an ambiguous prefix.

The notifier audit in §4.12 identifies real producers, including v0.1 entity color rather than a deferred placeholder. The maintained celestial, held-item and atlas routes have receiving contracts: P8’s pure angular policy is separate from matrix production; P9 preserves held-light and nested scope semantics; P7 authenticates atlas evidence before applying P13’s Known/Unknown result to the P6 sink.

Section 4.14 cleanly separates direct world-epoch reset, generation adoption and permanent retirement. Retirement is reason-closed, rejects active callbacks and wrong-thread use, and retains borrowed services on rejection. P7’s retirement consumer handles the distinct outcomes and preserves final notification/lease restoration before retirement (`docs/phase7/v1/PHASE_7_DOC.md:2750–2820,3648–3678`). P11 keeps its own close/reset lifecycle distinct from P6 retirement (`docs/phase11/v1/PHASE_11_DOC.md:1133–1164`).

### 7. Licensing, staging and retained gates

D-P6-34 correctly qualifies the historical LGPL label. The independently read [pinned Pintonium root LICENSE](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/LICENSE) identifies GPL version 3; the document does not treat an old label as a per-file reuse grant. Scoped permitted primary source checks corroborated the historical [smoothing units](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java), [frame counter/time behavior](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/SystemTimeUniforms.java), and [nullable notifier fields](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/state/StateUpdateNotifiers.java). These checks are evidence, not permission to copy implementation.

Sections 6–12 provide the failure ladder, threading/allocation constraints, behavioral verification plan, staged producers, no assigned OQs, decisions and executable implementation sequence. Current §5 preserves outstanding whole-owner verification gates instead of borrowing historical PASS results to certify changed sibling bytes. The milestone split retains actual v0.1 obligations while explicitly staging shadow, alias-ID, expression and atlas/instance work.

## Required changes and limitations

**Required changes: none.** No current evidenced owner defect or receiver correction was found, and no §5 modification is required by this review.

This was a documentary architecture investigation only. No files were edited and no builds, tests, formatters, linters, runtime sessions or implementation-conformance checks were run. No chatlogs, root transcripts, Oculus pipeline/transform source, prohibited libraries or relocated GLSL source were read. The review establishes no broader licensing grant. The proposed tests remain future implementation obligations; sibling architecture verdicts and final IR-01 integration readiness remain the responsibility of their separate review gates.