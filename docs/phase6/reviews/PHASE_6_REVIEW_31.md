# Phase 6 — Independent Whole-Owner Architecture Review

**Round:** R31  
**Frozen attempt:** attempt7  
**Owner:** `docs/phase6/v1/PHASE_6_DOC.md`  
**Frozen SHA-256:** `a40c305fbb74d8b57fc22643f94e9b0504fcf8f3895e9e47ae3fc32458cf3fdf`  
**Identity qualification:** This is the supplied frozen inventory identity, not a checksum computed by this review.

## Scope and selected authority

Independently read the complete Phase 6 owner, §§0–12, including historical maintenance qualifications, active inventory and algorithms, incorporated §5 interfaces, degradation, threading, testability, staging, decisions, and implementation handoffs. This was not limited to the latest amendments or to unchanged owner bytes relative to another attempt.

The owner's header selects **`docs/design/v2.0-RC3/DESIGN.md`**, not global-newest v3. I read RC3 Part I and the complete Phase 6 assignment at lines 1689–1801, applying its inventory, cadence, smoothing, barrier, provider, center-depth decision, and notifier-audit gates. Governing research included `docs/research/v1/RESEARCH.md` §§0–1, §3.4, §4.2, §4.4, Appendix A.3's declaration trigger, Appendix B.3, Appendix D, and the relevant current Appendix F.6 exclusion/type contract. `AGENTS.md` and `docs/MOVES.md` supplied repository restrictions and versioned citation resolution.

Dependency inspection covered the current P1/P3/P4/P5 §5 contracts and the incorporated definitions needed by this owner. Reciprocal inspection followed actual P7 dispatch, lifecycle and capture handling, P11 submission/controller behavior, P13 atlas queries and P7 binding authentication, and the bounded P8/P9 producer contracts. Historical PASS records and claimed resolutions were not used as proof of current correctness.

## Independent checks and evidence

### 1. Complete inventory, cadence and temporal semantics

The complete inventory at `docs/phase6/v1/PHASE_6_DOC.md:873–975` supplies exact types, acquisition, activation policy, provider ownership and milestones for Appendix D's held/player, world/time/weather, camera/matrix/screen and per-draw groups. I compared it with `docs/research/v1/RESEARCH.md:1318–1383` and the permitted shipped author document, `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:123–266`.

The separation of acquisition from every-activation refresh at owner lines 833–871 preserves cached scalar/vector uploads without resampling providers on switches. Matrices retain unconditional upload. The equations and edge rules at lines 977–1046 retain tick-domain half-lives, distinct wet/dry fields, first-valid initialization, continuous eye-brightness accumulators and explicit integer quantization. Temporal rotation, duplicate/rejected-frame no-ops, missing/duplicate capture handling, singular-inverse isolation and world-reset history are specified at lines 1048–1138. No missing Appendix D owner row or substantive type/cadence mismatch was established.

The receiving sequence is concrete: `docs/phase7/v1/PHASE_7_DOC.md:1052–1077` samples P6 before buffer mutation, supplies zero prior dimensions after world reset, completes held delivery before activation, and captures matrices only after `setupCameraTransform`. Its hook rows at lines 1586–1598 distinguish the earlier clear from the later camera capture; they do not merely acknowledge P6's ordering.

### 2. Declaration truth and exact-current configuration admission

P6's current schema23 receipt at owner lines 1858–1862 matches `docs/phase3/v1/PHASE_3_DOC.md:4152–4158` and the current materialization/schema rules at lines 4193–4219. Older numeric receipts are expressly historical, not alternate acceptance paths.

The P3 catalog/type definitions at `docs/phase3/v1/PHASE_3_DOC.md:1435–1481`, materialization/copy rules at lines 1563–1588, and center-depth/smoothing records at lines 2706–2744 supply the values P6 needs. The actual P4 consumer at `docs/phase4/v1/PHASE_4_DOC.md:1356–1398` merges final attributed declarations before GL, rejects unequal structural types, preserves optimized-out declarations and publishes a handle-free layout. P6 therefore need not reopen source, invent a declaration parser, or infer driver activity from declaration presence.

### 3. Sole sampler policy and changed P1/P4/P5 boundaries

Owner lines 1184–1268 consume the required P5 resolver using the effective descriptor's sampler layout and authenticated stage/band. The exact P5 algebra exists at `docs/phase5/v1/PHASE_5_DOC.md:974–1013`; actual policy and result behavior are at lines 2403–2478. These retain unit 11 for `depthtex1`, the direct-declaration `watershadow` condition, shadow-only `tex`, full sampler shapes, aliases sharing a unit, deterministic order and explicit Invalid outcomes.

The changed candidate initialization path does not transfer runtime integer ownership away from P6. P4 freezes P5-derived assignments before allocation at `docs/phase4/v1/PHASE_4_DOC.md:1415–1425`, while P5 explicitly retains the unchanged P6 runtime resolver/participant at `docs/phase5/v1/PHASE_5_DOC.md:2410–2421`. P6 still owns location caching and runtime integer uploads; P5 remains the physical object binder. I found no second sampler map, accidental dynamic allocator, fourth participant or source-based alias reconstruction in the active owner.

### 4. Effective blend, barrier dispatch and immediate activity

Owner lines 1270–1339 and 1443–1518 agree with the actual P4 dispatch at `docs/phase4/v1/PHASE_4_DOC.md:1782–1789`: invalidate predecessor activity, close its lease, bind, acquire effective alpha/blend state, publish/sample that state, then dispatch sampler/built-in/custom. The callback-only lookup, retained location lifetime and operation-free activity token are defined and consumed at P4 lines 1817–1846.

P1's duration-lock mechanism at `docs/phase1/v14/PHASE_1_DOC.md:3513–3540` suppresses held-aspect setter attempts before cache/native mutation and publishes successful effective changes. P7's actual installed receiver at `docs/phase7/v1/PHASE_7_DOC.md:2915–2943` converts the full observed value and routes it to the exact P6 runtime, with explicit rejection/failure containment. Thus P6's current blend value is observed effective state, not a prediction of a later lock or the operands of a suppressed setter.

### 5. Replay cardinality, delivery and downstream failure handling

P6's attempted-command replay and ordered report protocol at owner lines 1341–1441 preserve already-computed values, per-program/name isolation, original triggering-error cardinality, honest false attribution, cleanup evidence and no replay-probe double counting. These match P1's upload surface at `docs/phase1/v14/PHASE_1_DOC.md:3271–3283`, immutable verdict at lines 3449–3455, and cached-replay/non-reproduction law at lines 3749–3776.

The P7 receiver is substantive, not acknowledgment-only: `docs/phase7/v1/PHASE_7_DOC.md:2178–2249` retains originals, copies/classifies synchronously, preserves observation order, checks the delivery-failure diagnostic/latch before drawing, and retains evidence through final restoration and retirement. The final P2 receiving branch at `docs/phase2/v2/PHASE_2_DOC.md:1385–1400` flattens the exact verdicts into current `/4` fields, preserves repeated equal observations and fails T0 for every recorded error. Owner D-P6-31 at lines 333–336 and 2252 supersedes the earlier `/3` handoff references; P6 is not a wire serializer.

### 6. P11 schema, submissions and controller lifetime

Owner lines 1520–1671 publish the fixed schema, matching runtime values, exact six upload variants, distinct boolean encoding, normal absent-program/location outcomes, duplicate/type rejection and authoritative three-way counters. Current research explicitly supports the seven-name exclusion union at `docs/research/v1/RESEARCH.md:1494–1505`.

The actual P11 refresh loop at `docs/phase11/v1/PHASE_11_DOC.md:743–800` submits in definition order, branches on Accepted/SkippedAbsent/Rejected, preserves exact completed/aborted-prefix counts and uses Bool1 rather than an Int1 convention. Its controller lifecycle and forwarding map at lines 1041–1104 reset/deactivate before new-state participation, keep P11 CLOSE local, and call P6's reason-specific retirement only after final use. No incompatible controller close alias or alternate P6 participant was found.

### 7. Retirement and reciprocal producer ownership

Owner lines 1673–1800 distinguish nonterminal generation adoption from terminal runtime retirement, define wrong-thread/already-retired/in-flight precedence, permanently guard retained capabilities and release borrowed references without invoking services. Replacement requires actual old-barrier invalidation; a pre-release Rejected result cannot establish it. Shutdown retains its distinct retire-before-atomic-teardown contract.

The receiving composition sequence at `docs/phase7/v1/PHASE_7_DOC.md:3790–3828` branches on actual publication ownership, retires the old runtime, adopts the accepted generation on the replacement, and compensates failures off rather than reviving old authority. P11's matching service/retirement rules are cited above.

For later producers, P9's actual held-light construction at `docs/phase9/v1/PHASE_9_DOC.md:712–717` preserves `(max(main,off),off)` without swapping item IDs. P8's provider route at `docs/phase8/v1/PHASE_8_DOC.md:1012–1021` and celestial computation at lines 1120–1155 use the same frame sample and actual main camera, not pre-camera fabricated vectors. The P13 query at `docs/phase13/v1/PHASE_13_DOC.md:1220–1260` remains metadata-only; the actual P7 receiver at `docs/phase7/v1/PHASE_7_DOC.md:3465–3488` authenticates current binding and dispatches Known/Unknown to existing P6 atlas events. Stale evidence cannot mutate the uniform.

### 8. Center-depth decision, provenance and remaining sections

Owner lines 1140–1182 select the specified synchronous fallback and reject the declaration-unsafe GPU macro route. P3's actual contributor placement at `docs/phase3/v1/PHASE_3_DOC.md:1525–1544` confirms that a plain object-like replacement would also reach the declaration. The fixed-unit contract provides no unilaterally reserved sampler. The pending DESIGN clarification is therefore not a missing prerequisite for the selected CPU path.

I independently retrieved permitted pinned Pintonium source through the upstream URLs after the local historical source path was unavailable. The inspected [SmoothedFloat](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java), [ProgramUniforms](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramUniforms.java), [ProgramSamplers](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramSamplers.java), [MatrixUniforms](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/MatrixUniforms.java), [SystemTimeUniforms](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/SystemTimeUniforms.java), CommonUniforms, PackDirectives, StateUpdateNotifiers, CenterDepthSampler and the bounded forge122 capture source corroborate the adopted mechanics and rejected traps. This does not authenticate every historical local line coordinate or certify runtime behavior.

The [pinned root LICENSE](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/LICENSE) is GPL version 3, consistent with the owner's qualification at lines 341–349 and D-P6-34. A root license and historical LGPL label do not establish applicable per-file incorporation rights; future reuse still requires file-specific licensing and notice review. No implementation was copied.

Owner §§6–12 remain substantive: failure cases preserve local isolation and no-draw containment, thread/lifetime boundaries are stated, temporal and cross-boundary cases have planned verification coverage, later producers are milestone-tagged rather than falsely completed by neutral values, no assigned OQ is omitted, and implementation remains gated separately from architecture review.

## Substantive corrections

None. No concrete current Phase-6-owned contract defect requiring correction was established.

## Notes

None requiring owner maintenance. Explicit historical supersession, existing implementation gates and the source-license qualification are not counted as new findings.

## Limitations and disposition

This is a read-only architecture assessment. No files were edited; no formatter, linter, build, test, checksum or validation command was run. No forbidden transcript, Oculus transformation/binary tree, glsl-transformer implementation or OptiFine decompiled implementation was used. Dependency and reciprocal reads establish the compatibility of the inspected Phase 6 boundary only; this review does not certify sibling owners, implemented APIs, hook application, actual GL behavior, pack compatibility or performance. Their independent owner reviews and the final integration/implementation gates remain separate.

**Corrections:** 0  
**Notes:** 0  
**§5 impact:** No — no owner interface change is requested. The current incorporated §5 surface was nevertheless reviewed against its actual providers and receivers.

**Verdict: PASS**

PASS
