# Phase 4 — Independent Whole-Document Review 33

## 1. Verdict and reviewed input

**Verdict: PASS-WITH-CORRECTIONS.** There are **0 blocking findings, 2 corrections, and 0 notes**. The corrections affect the binding §5 surface, directly or through its explicit incorporation of normative detail; fresh verification is required after correction.

Reviewed target: `docs/phase4/v1/PHASE_4_DOC.md`, complete, including §§0–12 and the current coordinated receipts. Frozen SHA-256 supplied by Main: `3ab003ae221419b113a7eb8970f3a3b85188d6f9a43a737d57ef02bf5f2b43fd`. Main confirmed that its independently recomputed hash still matches the frozen input; this reviewer did not run a checksum command.

The document meets the structural Phase 4 assignment. Its remaining defects are localized contradictions in the exported activation order and the active evidence handoff, not omissions requiring a rebuild. This verdict is an architecture review, not implementation authorization, runtime certification, or closure of IR-01/final integration.

## 2. Authority, coverage, and independent evidence

The target header selects `docs/design/v2.0-RC3/DESIGN.md`; its Part I and Phase 4 assignment at lines 1471–1570 govern this review. The review covered the assigned RESEARCH sections and Appendix A, the author-facing Shader Programs table, PD §§3 and 13, the current Phase 1 and Phase 3 §5 dependencies with the detailed contracts they incorporate, and the relevant actual receiving paths in current Phases 2, 5, 6, 7, and 8. The geometry decision, BUILD readiness/navigation document, and the complete integration review, including its dated follow-ons, were read as context. Historical review verdicts and receipts were not treated as new evidence that the frozen current owner/receiver set passes.

The inspection checked both stage configurations, sparse families, all classic catalog rows, whole-provider fallback state, source/build disposition versus effective resolution, compile/link cleanup, native/core geometry admission and cached linked-input agreement, positional routing identity, opaque selection authentication, candidate/publication ownership, generation invalidation, activity and duration-lease lifetime, callback dispatch, virtual/fixed terminals, staged evidence, and pending asynchronous/repetition handoffs. The receiving-side checks included P7's explicit selection and activation result dispatch, P5's shared binding and virtual-transition operations, P6's effective-state receiver, P8's root-shadow selection and terminal-release containment, and P2/P7's current manifest boundary.

The historical local `reference-src/pintonium-9c2fcc1` directory is absent. It was not silently replaced by `Pintonium-main`. The relevant source claims were independently checked through explicit source-file URLs at actual commit **`9c2fcc1a4814cafc0242370757e9e05ea83c5be3`** in [Xplodin/Pintonium](https://github.com/Xplodin/Pintonium/tree/9c2fcc1a4814cafc0242370757e9e05ea83c5be3): `ProgramFallbackResolver.java`, `ProgramId.java`, `Program.java`, `ProgramCreator.java`, `WorldRenderingPhase.java`, and the cited ranges of `CommonIrisRenderingPipeline.java`, `CompositeRenderer.java`, `PipelineManager.java`, and `IrisChunkProgramOverrides.java`. These confirm the bounded claims actually used here: memoized recursive fallback, the named parent edges, bind-before-refresh shape, incompatible reference attribute numbering, separate phase override, pass metadata, and generation-inequality cache invalidation. In particular, the [pinned program-use source](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1a4814cafc0242370757e9e05ea83c5be3/common-shaders/src/main/java/net/irisshaders/iris/gl/program/Program.java) is structural evidence, not authority to override the coordinated duration-lock contract; the [pinned attribute creator](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1a4814cafc0242370757e9e05ea83c5be3/common-shaders/src/main/java/net/irisshaders/iris/gl/shader/ProgramCreator.java) supports rejecting its 11/12/13/14 numbering in favor of the governing 10/11/12 locations.

No implementation files were edited. No builds, tests, linters, formatters, validation commands, or runtime exercises were run. Searches used explicit document/file allowlists. No forbidden chatlogs, root transcripts, Oculus source, transformation-library source, relocated GLSL source, or copied transformation implementation was read or searched.

## 3. C33-1 — Export the before-participant duration-lock order consistently

**Severity:** correction.

**Location:** `docs/phase4/v1/PHASE_4_DOC.md:2006`, §5.1, the `PublishedRegistry.barrier` / `ProgramStateBarrier` export row.

**Claim under review:** The binding export says activation uses the retained binding, “calls three participants, applies effective lock.” This retains the superseded participant-before-lock order.

**Evidence:** The same document's normative §4.10 sequence at lines 1671–1680 requires authentication, predecessor activity invalidation, predecessor lease closure, program binding, successful `StateService.lockAlphaBlend`, effective-state notification/sampling, and only then sampler/built-in/custom callbacks. D-P4-34 and the §12 implementation checklist agree with that newer sequence. Phase 1 §4.7.4 at lines 3386–3414, incorporated by its §5 duration-lock export, requires transactional acquisition and effective-state notification before participants/drawing. The actual orchestrator recipient, `docs/phase7/v1/PHASE_7_DOC.md:1101–1114`, explicitly consumes acquire → publish/sample effective blend → participants → Activated. P7's §5 D-P7-43 receipt repeats that order. P6's current D-P6-32 receiver at `docs/phase6/v1/PHASE_6_DOC.md:1458–1464` likewise requires successful acquisition/release notification before built-in dispatch.

**Impact:** A dependent implementing §5 literally dispatches callbacks before the lease has been successfully acquired and before the actual effective-state receipt exists. That violates the receiving-side precondition and permits participant effects on an activation whose subsequent acquisition or notification fails. Even where older blend projection can predict a value, prediction does not establish the required successful duration lock or its failure boundary. The owner currently exports two incompatible activation transactions.

**Required correction:** Replace the §5.1 summary with the §4.10 transaction: authenticate → invalidate predecessor activity → close predecessor lease → bind retained program → acquire effective provider lease → publish/sample effective blend → sampler/built-in/custom callbacks → Activated. Preserve the fixed-function branch's no-lease/no-participant behavior and the existing failure containment. Remove the obsolete after-participant wording rather than offering both orders. The corresponding stale P6 descriptions are a separately owned coordinated receiver correction, not an additional finding counted against P4 here.

**§5 impact:** **Yes, directly.** The incorrect order is in the binding exported-interface row. Its correction requires fresh §G1.3 verification before dependent consumption.

## 4. C33-2 — Route current own-build evidence through the current `/4` envelope

**Severity:** correction.

**Locations:** `docs/phase4/v1/PHASE_4_DOC.md:1251–1254`, §4.6; and §11.4.1, lines 2692–2701. The D-P4-31 decision-log entry also retains the older `/3` receipt without a current cutover qualification.

**Claim under review:** The active §4.6 handoff instructs P2/P7 to serialize the required `programs.<n>.ownBuild` token in `run-manifest/3` with `capture-plan/3`. The current receiving receipt still directs P7's `/3` serializer. This is normative handoff text, not merely a preserved historical review quotation.

**Evidence:** Current `docs/phase2/v2/PHASE_2_DOC.md:994–995` and `1074–1076` define only `schmaloogium.capture-plan/4` and `schmaloogium.run-manifest/4`. Its §5 export at lines 2161–2162 makes `/1`–`/3` unsupported history, while lines 2196 and 2221–2224 retain the exact P4 own-build enum and its requested-slot meaning. The actual P7 capture dispatch boundary at `docs/phase7/v1/PHASE_7_DOC.md:1762–1766` explicitly rejects `/1`, `/2`, and `/3` before world load and offers no migration/defaulting path. P7's current P4 consumption rows at lines 3340–3344 and its D-P7-35 receipt already copy ownBuild into `/4`.

**Impact:** Evidence produced according to P4's active handoff uses an envelope that both current receivers reject. This prevents the registry evidence from entering the coordinated capture/T3 path even when every ownBuild value is correct. It also makes the owner receipt falsely describe the frozen receiver set as coordinated on `/3`.

**Required correction:** Change the active §4.6 and §11.4.1 handoffs to the exact current P2-owned `/4` envelopes and explicitly supersede the older D-P4-31 wire-version receipt where necessary. Preserve P4's classification matrix, token spelling, requested-slot failure semantics, source-free same-request projection, registry identity rules, and P2's independent golden schema. Do not add a `/3` compatibility reader, change P3's configuration schema, or create a P4-owned option/profile/comparison codec; those `/4` fields remain P2/P7 receiving obligations.

**§5 impact:** **Yes, through incorporation.** The §5.1 resolution export incorporates §4.6's total evidence contract, and the receipt explicitly describes that §5 handoff. Correct the incorporated active contract and make the current envelope unambiguous at the export/receipt boundary; no change to the `ProgramResolutionProjection` Java field shape is needed.

## 5. Integration disposition

The frozen owner has a coherent current architecture beneath these two stale contract surfaces. No additional structural blocker was established in the reviewed scope. The positional route, effective-provider metadata, source-versus-linked geometry distinction, shared selection, and terminal ownership paths remain architectural obligations, not proof that their implementations or conformance gates have been exercised.

Main should preserve this report, apply C33-1 and C33-2 in the owner/integration correction phase, coordinate the separately reviewed P6 order cleanup, and obtain fresh whole-document verification because §5 is affected. Historical PASS statements do not close that requirement, and this review does not certify unrelated leaf documents or authorize implementation.

## Resolutions

### C33-1 — One exported activation order

D-P4-35 aligns §5.1 with §4.10: authenticate, invalidate predecessor activity, close its
lease, bind retained program, acquire effective lease, publish/sample effective blend,
then sampler/built-in/custom participants and Activated. Fixed function acquires no new
lease or participant dispatch; existing failure containment remains. P6 D-P6-33 receives
that same order without predictive descriptor overlay.

### C33-2 — Current evidence envelope

Active §4.6 and §11.4.1 now name only capture-plan/4 and run-manifest/4. D-P4-35 explicitly
supersedes the older D-P4-31 wire receipt; ownBuild classification/projection and P3/golden
schemas remain unchanged. No old reader or alternate codec is added. Changed §5 requires
fresh whole-document verification; the original R33 verdict is preserved.