# Phase 6 — Whole-owner Architecture Review R30

## Frozen artifact and disposition

- **Attempt:** 6, frozen owner-set review.
- **Owner:** `docs/phase6/v1/PHASE_6_DOC.md`.
- **Round:** R30.
- **Frozen SHA256:** `a40c305fbb74d8b57fc22643f94e9b0504fcf8f3895e9e47ae3fc32458cf3fdf`, as supplied in the assignment and recorded in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_6.json`.
- **Required corrections:** 0.
- **Non-blocking notes:** 1.
- **Required §5 impact:** none.
- **Verdict:** **PASS**.

This is a fresh review of the complete current owner, not a confirmation of an earlier fix or a verdict on another phase. No repository file was edited. No build, test, formatter, linter, validation command, runtime or pack-tier exercise was performed. The frozen identity above is the supplied inventory identity, not a claim of a separately executed checksum command.

## Scope and authority

I read the whole Phase 6 document, §§0–12, including the historical maintenance record, full built-in inventory, all detailed algorithms, incorporated §5 contracts, failure rules, threading/performance requirements, testability plan, staging, decisions and implementation checklist. Long §5 rows were recovered without truncation.

The governing design is the owner's selected `docs/design/v2.0-RC3/DESIGN.md`, not the globally newest design. I read its Part I and complete Phase 6 assignment at lines 1689–1801. Governing research was checked directly in `docs/research/v1/RESEARCH.md`: confidence/mission/decisions, §3.4, §4.2, frame flow §4.4, declaration trigger Appendix A.3, the entire fixed sampler table Appendix B.3, the entire built-in inventory Appendix D, and the relevant current Appendix F.6 expression rules. `AGENTS.md` and `docs/MOVES.md` supplied the source restrictions and moved-citation rules.

I independently read the affected dependency §5 publications and their incorporated declarations/semantics in Phase 1 v14, Phase 3 v1, Phase 4 v1 and Phase 5 v1. Reciprocal receiving checks covered Phase 7 v1, Phase 13 v1 and Phase 14 v1, plus the load-bearing Phase 2 v2 capture, Phase 8 v1 shadow, Phase 9 v1 ID/held-item and Phase 11 v1 expression boundaries. These reads establish interface correspondence for this owner; they do not certify those owners.

Historical review conclusions were not used as proof. Earlier numeric-schema, `/3` capture, CLOSE and source-license receipts were read in light of their explicit current superseding contracts, rather than reported again as unresolved historical defects.

## Independent checks

### 1. Whole-document gate and complete uniform coverage

The required thirteen sections are present and substantive. The inventory at `docs/phase6/v1/PHASE_6_DOC.md:873–975` covers every Appendix D name, with exact GLSL type, value source, acquisition cadence, activation behavior and milestone. I compared it with `docs/research/v1/RESEARCH.md:1318–1382` and the permitted shipped author specification `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:123–266`.

This includes the easily omitted off-hand light value, both previous camera/matrix families, all shadow matrices and inverses, unused terrain metrics, atlas size, blend factors and instance ID. Pending later-milestone values remain explicitly neutral/degraded rather than being represented as implemented support. Phase 7 retains v0.1 entityColor ownership; Phase 9 owns alias values, Phase 13 supplies atlas dimensions, and Phase 8 supplies later shadow/celestial values. The staging and checklist at owner lines 2181–2203 and 2399–2433 preserve those boundaries.

### 2. D-P6-36 and schema23 cutover

Owner lines 1846–1862 require exact current containing/nested-ID/inspection schema admission and `MaterializedSource-v23` before derivation/reuse. They leave range-selector parsing and BLOCK alternate interpretation with Phases 3/9. This matches `docs/phase3/v1/PHASE_3_DOC.md:4152–4217`, including rejection of older/future/mismatched values, unchanged nine metadata-only inspection trees and `projectionVersion=1`.

The configuration-to-final-declaration route remains intact: Phase 3's closed declaration algebra and fingerprint-linked catalog at lines 1435–1478 and 1567–1578 feed Phase 4's effective layout publication at `docs/phase4/v1/PHASE_4_DOC.md:2067–2092`; Phase 6 consumes that layout without reopening source. Current schema receipts in P1/P4/P5/P7/P13/P14 do not create a second version authority or add a P6 parser. No old-schema shim or fabricated capability is required by the owner.

### 3. Uniform and sampler ownership after P1/P4/P5 changes

The Phase 6 factory, resolver contract and runtime participant remain coherent at owner lines 434–571 and 1184–1339. The exact eight-argument factory matches the receiving call shape in `docs/phase7/v1/PHASE_7_DOC.md:3832–3845` and the Phase 5 receipt at `docs/phase5/v1/PHASE_5_DOC.md:2857–2870`.

The sole resolver is actually published with the matching `Ready`/`Invalid` algebra and complete sampler shape at Phase 5 lines 990–1004. Its fixed policy at lines 2344–2395 preserves exact-name aliases, declaration-based watershadow routing, unit 11 for depthtex1, no gbuffers/shadow unit-12 sampler, and rejection rather than invented mappings for unsupported shapes/domains. Phase 6 routes each result explicitly, retaining Invalid as degradation, not an empty success.

Phase 5's newly published candidate initialization assignments at lines 2352–2364 and Phase 1's initialization/normalization contracts do not displace Phase 6's **runtime** sampler-integer ownership. Candidate initialization is a separate unpublished-program transaction. Phase 6 still performs runtime exact-name lookup, cached integer uploads and attributed replay, with no texture-object binder, raw handle, allocator or fourth participant.

### 4. Barrier and effective-blend receiving path

The full consuming dispatch was checked, not just the producer declarations. Phase 4's executable architecture at `docs/phase4/v1/PHASE_4_DOC.md:1711–1753,1784–1835` authenticates the retained selection, invalidates predecessor activity, closes the preceding lease, binds, acquires the effective lease, publishes/samples effective blend and dispatches sampler → built-in → custom. Callback lookup and retained activity-token lifetimes match owner lines 1270–1339.

Phase 1's effective-state/suppression/rollback law at `docs/phase1/v14/PHASE_1_DOC.md:3455–3487` and Phase 7's registered receiving route at `docs/phase7/v1/PHASE_7_DOC.md:2888–2918` support owner lines 1469–1515. Suppressed setter operands do not become state events; acquisition/notification failures do not reach successful uniform dispatch. P6 now consumes observed effective state rather than predicting a later blend lock.

### 5. Frame clocks, smoothing and temporal matrices

Owner lines 904–921, 977–1137 and 1811–1812 keep the three clocks distinct: accepted-frame count, rendered elapsed seconds, and monotonic game-time smoothing ticks. The actual timing query returns only the latest accepted owner's values, with generation/frame/thread/reset/retirement guards; duplicate or rejected begins cannot advance it.

The receiving controlled-capture route at `docs/phase7/v1/PHASE_7_DOC.md:2037–2088,3094–3130` supplies the existing seconds input only for an authenticated isolated capture session and copies actual P6 counters. It does not reset a used runtime or derive counters from sample ordinal. Phase 2's required timing and `/4` manifest contract at `docs/phase2/v2/PHASE_2_DOC.md:1174–1229,2427–2428` is consistent with that evidence flow.

Frame sampling, previous snapshots and the synchronous center read complete before resize/clear; current gbuffer matrices arrive later after camera setup. Repeated activation does not rotate history. First valid new-world samples initialize previous=current, and singular inversion affects only the inverse. The exact EMA, asymmetric wet/dry fields, pause/zero-time behavior and eye-brightness quantization are specified; rounding is correctly presented as a local reference-supported decision rather than independently proven OptiFine behavior.

### 6. Replay/error transport and terminal retirement

Owner lines 1341–1441 preserve Phase 1's one-verdict-per-triggering-error law, original error objects, ordered repeated occurrences, false attribution for foreign/ambiguous/non-reproducing evidence and no replay resampling. Phase 1's typed law is present at `docs/phase1/v14/PHASE_1_DOC.md:3377–3403,5255–5256`.

Phase 7's actual collector and failure branch at `docs/phase7/v1/PHASE_7_DOC.md:2162–2237` receive all participant and immediate-event reports synchronously, preserve cleanup evidence without probe double-counting, and inspect delivery-failure degradation/exception before drawing. Phase 2 copies the ordered verdicts rather than inferring attribution (`docs/phase2/v2/PHASE_2_DOC.md:1379–1384`). Current owner line 1415 and D-P6-31 select `/4`; earlier `/3` receipts are not an alternate current encoding.

The retirement algebra and precedence at owner lines 1673–1793 remain implementable and agree with P7's receiving lifecycle at lines 3847–3858: unpublished abort needs no publication, replacement waits for actual old-barrier invalidation, shutdown retirement precedes atomic teardown after final restoration use, and rejection retains services/admission closure. Retained event/participant capabilities cannot resurrect a retired runtime. Reset/adoption cannot clear the replay-delivery failure latch.

### 7. Remaining event consumers and optional modernization

- Phase 8's exact angular result and main-camera computation match the P6 provider receipt (`docs/phase8/v1/PHASE_8_DOC.md:1651–1655,1678–1688`). Its actual event dispatch at lines 1420–1440 publishes current-frame celestial/shadow data before activation; P6 retains matrix inversion/upload ownership.
- Phase 9's held tuple at `docs/phase9/v1/PHASE_9_DOC.md:713–717` matches `(max(main,off),off)` in old-hand mode. Its concrete held hook occurs before activation and ID scopes restore their predecessors (lines 729–738), so P6's held next-activation policy is usable without inventing another sampler/uniform owner.
- Phase 11's dispatch at `docs/phase11/v1/PHASE_11_DOC.md:741–781` branches on Accepted/SkippedAbsent/Rejected, preserves exact counts and maps bool to Bool1. Its §5 receipt at lines 1186–1216 matches the P6 immutable schema/view, six upload types and conservative seven-name exclusion. Current RESEARCH §3.4/F.6 explicitly resolves that exclusion union.
- Phase 13's size metadata does not establish current binding. Its contract at `docs/phase13/v1/PHASE_13_DOC.md:1196–1222,1538–1543` joins Phase 7's authenticated actual-bind dispatch at `docs/phase7/v1/PHASE_7_DOC.md:3438–3460`; Known, Unknown/non-atlas, stale and failed cases reach the correct existing P6 outcomes.
- D-P6-1 remains the synchronous baseline. Phase 14 explicitly leaves its sample-age request ungranted and PBO_FENCE off until permission/evidence exist (`docs/phase14/v1/PHASE_14_DOC.md:1584,1625–1657`). No optional PBO, async compile, worker or texture API is implicitly granted by this review.

## Permitted primary evidence and confidence

The local historical Pintonium source path requested for SystemTimeUniforms was absent. I recovered permitted evidence directly from the **same pinned `9c2fcc1` upstream revision**, not from a newer branch or a previous review:

- [SystemTimeUniforms.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/SystemTimeUniforms.java): initialization, 720720 frame wrap and reset-to-zero at 3600 seconds.
- [SmoothedFloat.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java) and [CommonUniforms.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CommonUniforms.java): exponential shape/decisecond units, component integer casts and blend notifier consumption.
- [ProgramUniforms.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramUniforms.java), [FloatUniform.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/uniform/FloatUniform.java), and [ProgramSamplers.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramSamplers.java): cadence buckets, scalar caching, queued integer uploads, rejected dynamic allocation and the inert overload hazard.
- [PackDirectives.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/properties/PackDirectives.java) and [StateUpdateNotifiers.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/state/StateUpdateNotifiers.java): independent confirmation of the wet/dry misassignment and nullable notifier hazard. The narrow reads do not independently prove the mining report's repository-wide negative assertion that no assignment exists anywhere.
- [ActiveRenderInfoAccessor.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/forge122/src/main/java/org/taumc/celeritas/mixin/core/terrain/ActiveRenderInfoAccessor.java), [MixinEntityRenderer_Shaders.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java), and [MatrixUniforms.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/MatrixUniforms.java): actual buffer accessor/copy machinery and supplier-read previous-state advancement, which P6 deliberately does not inherit as its frame-boundary policy.
- [CenterDepthSampler.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pathways/CenterDepthSampler.java): real R32F/1×1 candidate machinery, without reading the prohibited transformer.
- [Pinned LICENSE](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/LICENSE): GPL version 3, consistent with the owner's current D-P6-34 qualification. This does not establish a blanket file-specific reuse licence.

Published contracts, mining claims and independently observed source facts remain distinct. No forbidden chatlog, root transcript, Oculus blocked directory, unresolved transformation library or glsl-transformer implementation was read. No OptiFine decompile mining was performed.

## Required corrections

**None.** I found no substantive current Phase-6-owned contract defect requiring correction before this architecture gate can pass.

## Non-blocking notes

### N30-1 — Keep matrix-always-upload authority separate from Pintonium cache evidence

**Severity:** note; no observable breakage in the current owner. `docs/phase6/v1/PHASE_6_DOC.md:615,631,942–951` correctly grounds unconditional matrix uploads in RESEARCH and implements that rule. However, the broader mining description at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:288–291` says matrices are always uploaded, while the independently recovered pinned [MatrixUniform.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/uniform/MatrixUniform.java), method `updateValue`, guards its native upload with matrix equality. The primary source therefore corroborates general cache mechanics, not that particular mining claim. **Minimal owner disposition:** retain the existing RESEARCH-controlled unconditional matrix rule; optionally annotate the Pintonium-mechanics evidence distinction during later authorised documentation maintenance. Do not copy the reference's matrix equality skip or edit protected mining history as part of this review. No required §5 change follows.

## Limitations and final disposition

This review establishes architecture coherence for the specified frozen Phase 6 owner only. Source reads are not executions; test plans are not passing tests; declared neutral values are not feature completion. Actual hooks, GL behavior, latency, allocation claims, screenshots, pack compatibility, optional modernization and file-specific licence compliance remain implementation/evidence obligations. Other owners' fresh reviews and final integration are separate gates.

**Final verdict: PASS — 0 required corrections, 1 non-blocking note, no required §5 change.** Owner bytes were left unchanged. This result grants no runtime, implementation, pack-tier, sibling or final-integration certification.
