# Phase 6 — Independent Architecture Review 26

## 1. Reviewed artifact and remit

- **Reviewed document:** `docs/phase6/v1/PHASE_6_DOC.md`, complete, including the current §0.27 amendment and incorporated contracts.
- **Review destination:** `docs/phase6/reviews/PHASE_6_REVIEW_26.md`.
- **Frozen SHA-256 supplied with the assignment:** `bb74571b4b1b02fc154a973d9e5cf98e21f40d9155b1f2f2663ad13e5902780a`. This identifies the commissioned input; this read-only review did not independently recompute the hash.
- **Governing design:** `docs/design/v2.0-RC3/DESIGN.md`, as declared by the target. DESIGN v3 was not substituted as global authority.
- **Method:** fresh whole-document §G1.2 verification, not a patch-only review or acceptance of prior review receipts.

This report assesses architecture completeness and contract consistency. It does not certify executable uniforms, driver behavior, completed conformance runs, the verification status of other phases, or final §G5.3 integration eligibility.

## 2. Authority and evidence read

### 2.1 Governing and required material

Read `AGENTS.md` before reference-source mining, `docs/MOVES.md`, RC3 Part I and the complete Phase 6 assignment, including its literal Doc gate. Read RESEARCH §§0–1, the assigned uniform/barrier/frame material in §§3.4/4.2/4.4, Appendix B.3 and complete Appendix D, with Appendix A.3 and F.6 material needed for center-depth and expression contracts. Read the assigned shipped pack-author uniform documentation at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:123–266`.

Read the complete current target, including scope, exhaustive inventory, detailed algorithms, §5 incorporation rules, failures, threading/performance, tests, milestones, OQ disposition, decisions and implementation checklist. All thirteen mandatory sections, numbered §0 through §12, are present and substantive.

### 2.2 Dependency and receiving contracts

Read complete public §5 contracts of:

- Phase 1: `docs/phase1/v14/PHASE_1_DOC.md:4953–5073`, following the uniform, readback, replay, handle-lifetime and related incorporated semantics.
- Phase 3: `docs/phase3/v1/PHASE_3_DOC.md:3184–4189`, including current schema/configuration identity, declaration/materialization publication and macro constraints.
- Phase 4: `docs/phase4/v1/PHASE_4_DOC.md:1970–2208`, following bound uniform access, activity tokens, participant dispatch, effective layouts and publication/retirement semantics.
- Phase 5: `docs/phase5/v1/PHASE_5_DOC.md:2367–2651`, following its pure fixed-sampler resolver, shared policy identity and relevant binding/lifetime contracts.

The maintained Phase 5 dependency is explicitly declared and does not masquerade as part of RC3's original dependency graph. Additional receiving-side reads followed the actual Phase 7 timing/event/replay/lifecycle routes, Phase 8 angular and camera contracts, Phase 11 submission/result and terminal-controller behavior, and Phase 2's replay-report serialization and failed-capture routing. These are interface checks, not independent whole-phase certification of those recipients.

Read the original integration findings and the complete appended Resolutions/follow-ons in `docs/PHASE_INTEGRATION_REVIEW.md`, including the recorded native-source/jcpp decisions, prepared-submission instance ruling, subsequent schema changes and latest celestial/replay remediation. Read `docs/decisions/U1_TEXTURE_SAMPLING.md`. Historical findings, review verdicts and structural-check receipts were treated as claims requiring current contract evidence, not as substitutes for it.

### 2.3 Pintonium and provenance

Read PINTONIUM_DESIGN §6, required §17 landmines and the relevant fixed-unit divergence. The former local pinned source path was not assumed to identify the differently named available checkout. Load-bearing source evidence was recovered from the exact `9c2fcc1` revision of [Xplodin/Pintonium](https://github.com/Xplodin/Pintonium/tree/9c2fcc1), using only permitted areas.

Sources checked included ProgramUniforms, ProgramSamplers, Uniform, CommonUniforms, FrameUpdateNotifier, CapturedRenderingState, MatrixUniforms, SystemTimeUniforms, SmoothedFloat, SmoothedVec2f, CenterDepthSampler, StateUpdateNotifiers, the relevant PackDirectives assignment, and the Forge 1.12.2 capture/accessor files. The pinned README/license material was checked for provenance. No forbidden transformation implementation, pipeline source, `libs`, `glsl-relocated`, transcript, chatlog or repository-root text transcript was used.

## 3. Substantive adversarial audit

### 3.1 Literal Doc gate and research fidelity

**Evidence:** target §§3, 4.4–4.12 and 5.1; RC3 Phase 6 Doc gate; RESEARCH Appendix D and B.3.

Every Appendix D row has a pack-facing type, meaning, provider/acquisition route, activation behavior and milestone. Later-owned values remain explicit future producer obligations rather than claimed support through neutral defaults. Documented-unused terrain metrics remain represented. The v0.1 `entityColor` producer is not incorrectly deferred with the v0.3 alias IDs.

The architecture separates acquisition from activation: provider sampling and smoothing do not repeat merely because a program is activated, but each successful activation visits the required values. Location/value caching remains effective-program/generation scoped; matrices retain the always-upload exception. The typed custom participant follows built-ins and does not acquire a fourth barrier position.

The sole sampler policy remains Phase 5's App B.3 resolver. Phase 6 receives the effective sampler layout and authenticated context, writes integer uniforms and neither allocates units nor binds texture objects. The unit-11 `depthtex1` ruling and stage-dependent policy are preserved rather than replaced by Pintonium's dynamic allocation.

The current N-total, IDs `0…N−1` instance contract differs from older research wording for a recorded reason: the integration follow-on explicitly ratifies that prepared-submission behavior. It is not an unexplained local reinterpretation. Nested predecessor restoration and the N=1 case are retained.

### 3.2 Temporal state, smoothing and center depth

**Evidence:** target §§4.4.2, 4.5–4.8; Phase 7 receiving contracts; pinned SmoothedFloat, SystemTimeUniforms and matrix/capture references.

The smoothing equations specify tick-domain exponential decay, independent wet/dry rates, initialization, paused time, invalid/regressing inputs, quantization and world reset. Pintonium's decisecond input is not silently adopted as the contract unit. Wetness and dryness remain independent, avoiding B1.

The frame protocol samples previous depth and rotates temporal snapshots before resize/clear; current gbuffer matrices are captured later, after camera setup. Duplicate/stale/generation-mismatched begins cannot rotate history or advance clocks. The actual timing query reports accepted cadence-engine values without resampling or deriving a replacement clock from requested capture parameters.

Center-depth Candidate A is fully specified and selected. Candidate B is rejected with concrete declaration-rewrite and fixed-unit prerequisites, not merely a preference for CPU execution. The empty macro contribution and synchronous fallback require no ungranted shader transformation. Phase 14's optional asynchronous measurement remains a separate, honest future obligation.

### 3.3 Celestial and notifier ownership

**Evidence:** target §4.2, especially D-P6-28, and §4.12; Phase 7 provider/invocation routing; Phase 8 pure angular and camera-math contracts.

Pre-camera `shadowAngle` now comes from the Phase-8-owned pure angular policy through Phase 7's mod provider. Later vector computation receives the actual associated main camera and retained frame sample. It does not fabricate a frame-bound `CelestialSample` before camera capture, query hidden current GL state, or introduce an engine-to-shadow implementation dependency.

The notifier table has receiving operations and named producing owners. Matrix capture is not confused with ordinal-zero clear; blend observation accounts for the effective Phase 4 lock; authenticated current atlas binding is distinct from atlas availability. Immediate uploads require a current activity token and cached locations; inactive signals update state for later activation without rebinding.

### 3.4 Replay evidence and consuming-side dispatch

**Evidence:** target §§2.2/4.11/5.1; Phase 1 replay attribution law; Phase 7 D-P7-38 at §5.1; Phase 2 §§4.5.4/5.1.2.

The required eight-argument factory supplies a real Phase-7-owned `UniformReplayErrorSink`. Its detached report preserves original errors, occurrence order and repeated equal entries. Attribution requires the Phase 1 replay law; labels and effective-program context are not treated as proof. Cleanup evidence stays false, and replay probes do not become duplicate triggering submissions.

The new producer was traced through the receiver rather than accepted at the emission point. Phase 7 installs the collector before construction, accepts reports synchronously, handles ordinary-mode escalation as well as capture, and checks delivery failure before another draw or successful acknowledgment. Phase 2 consumes the ordered flattened values in the existing `/3` error grammar; every error still fails T0.

Observer failure is not hidden by ordinary participant-degradation containment: Phase 6 retains pending evidence, latches failure, stops new attempts, and exposes the existing degradation or immediate exception. Phase 7 explicitly closes admission and prevents successful capture completion. Final restoration callbacks and retiring-runtime reports remain covered. No missing dispatch branch or silent-drop path was found in the reviewed contracts.

### 3.5 Ownership, retirement and custom bridge

**Evidence:** target §§4.10/4.13/4.14; Phase 4 publication semantics; Phase 7 composition; Phase 11 §§4.8/4.12/5.3–5.4.

Callback-scoped lookup, retainable activity proof and cached location lifetime remain distinct capabilities. Generation adoption does not impersonate terminal retirement. Candidate abort, accepted replacement/compensation and shutdown have separate ordering, and retirement performs no GL or borrowed-service disposal.

Wrong-thread and active-callback retirement rejections preserve ownership. Previously returned events, participants and custom sinks cannot revive a retired runtime or call released services. Final-use obligations include restoration, not just the last draw. Replay-delivery failure cannot be erased through reset/adoption.

The expression bridge publishes the permitted input schema and six upload forms, including boolean encoding, normal absence, duplicate/type rejection and exact accepted/skipped/rejected counters. Phase 11 explicitly consumes all three outcomes and the accepted-prefix/invalid-counter rules. Its own terminal CLOSE is not forwarded as a removed Phase 6 reset value. The seven-name expression exclusion agrees with current RESEARCH F.6, rather than relying on the historical discrepancy.

### 3.6 Scope, OQs and licensing

The document keeps parsing, shader compilation, physical texture management, aliases, hooks and expression evaluation with their assigned owners. No assigned OQ is omitted: RC3 assigns none to Phase 6. The required center-depth decision and fallback are complete; planned runtime/conformance evidence is not misreported as an architecture defect or completed proof.

Pintonium mechanics are contract-checked rather than treated as authority. The dynamic-unit divergence, B1 wet/dry misassignment, B6 nullable notifier hazard and sampler overload landmine have explicit dispositions. No restricted-source-derived mechanism or unexplained permission expansion was found.

## 4. Findings

No blocking finding, correction or note is raised. The audit found no actionable missing mechanism, unresolved required authority without a valid fallback, or incompatible producer/consumer contract in the reviewed Phase 6 surface.

**Exact counts:** blocking **0**; corrections **0**; notes **0**.

## 5. §5 impact and remaining boundaries

**§5 impact from this review: no.** No change to §5 or its incorporated consumer-visible semantics is requested.

The current document already contains amended interfaces whose review obligation motivated this round. This report does not certify other owners' changed bytes, convert historical review receipts into present verification, or close IR-01. Current coordinated owner grants were assessed as written; affected owner reviews, final integration and implementation/runtime gates remain independently required.

Verification here was documentary and read-only. No file was edited, no Resolutions were appended, and no build, test, formatter, linter, client, GL experiment or validation command was run.

## 6. Final verdict

**PASS**