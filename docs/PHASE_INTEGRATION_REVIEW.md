# Phase integration review

**Fix-up reader notice:** §§0–6 and Appendix A below retain the original provisional audit,
including its original input fingerprints, line coordinates, finding counts and verdict.
The subsequent `## Resolutions` section records the commissioned IR-02–IR-29 remediation
against amended phase documents; use its versioned paths and section anchors for current
contracts. IR-01 is excluded from this fix-up. Neither amendments nor recorded dispositions
constitute fresh individual verification or implementation clearance.

## 0. Method, authority and eligibility

Requested review: DESIGN §G5.3 item 4. Scope is the current fourteen architecture documents, not executable shader support or a replacement for individual phase verification. Main integrated four independent read-only contract audits (P1–4, P5–8, P9–11, P12–14), covering every document's complete §1, §5 and §11 and following their incorporated contracts and recipient sections where needed. The audit checked all 36 hard dependency edges plus the soft P7→P12 edge, additional published sibling/reverse handoffs, and all six expressly named seams.

Research remains authoritative (`docs/research/v1/RESEARCH.md:24-37,55-104`). The review rule is identical in the selected revisions: `docs/design/v2.0-RC2/DESIGN.md:626-638`, `docs/design/v2.0-RC3/DESIGN.md:645-657`, and `docs/design/v3/DESIGN.md:672-684`. Phase headers select RC2 for P1, RC3 for P3–9, and v3 for P2/P10–14. A review's v3 override does not adopt v3 for its target. MOVES is navigation, not authority; its older count/adoption summaries do not override P12/P14's own headers.

**Eligibility: NOT MET.** Eleven current documents are unverified. This is therefore a provisional integration audit of the requested current inputs, not the eligible final clearance described by §G5.3. The commissioning request is not recorded as a waiver of verification or implementation gates. Existing authoring exceptions permit the documented provisional architecture work, not implementation consumption. A retained local PASS for P2/P11/P12 does not certify their dependencies' newer contracts or their integration.

No code, phase contract, authority document, prior review or resolution was changed. No transcripts, forbidden reference sources, external implementation source, build, test suite, client or GL runtime was used. Statements below concern specified behavior, not observed rendering. Existing spikes and implementation tasks are not claimed executed. `[INFERENCE]` identifies predicted effects of incompatible contracts; quoted declarations and ownership statements are repository evidence.

### 0.1 Current inputs and individual verification status

| Phase | Current document / governing revision | Latest individual review | Current status and evidence |
|---|---|---|---|
| 1 | `docs/phase1/v14/PHASE_1_DOC.md` / RC2 | R25, PASS | **Unverified:** subsequent binding grants §§0.24–0.25; `docs/phase1/v14/PHASE_1_DOC.md:1474-1479,1522-1526` |
| 2 | `docs/phase2/v2/PHASE_2_DOC.md` / v3 | R38, PASS | Local PASS retained; `docs/phase2/reviews/PHASE_2_REVIEW_38.md:49-63`; P1's newer bytes are not certified |
| 3 | `docs/phase3/v1/PHASE_3_DOC.md` / RC3 | R54, PASS-WITH-CORRECTIONS, resolutions recorded | **Unverified:** §5-changing fix-up and §§0.55–0.57 grants; `docs/phase3/reviews/PHASE_3_REVIEW_54.md:241-244,267-270`; `docs/phase3/v1/PHASE_3_DOC.md:437-441` |
| 4 | `docs/phase4/v1/PHASE_4_DOC.md` / RC3 | R31, PASS | **Unverified:** coordinated rebuild §0.35; `docs/phase4/v1/PHASE_4_DOC.md:305-329` |
| 5 | `docs/phase5/v1/PHASE_5_DOC.md` / RC3 | R38, PASS | **Unverified:** coordinated rebuild §0.39; `docs/phase5/v1/PHASE_5_DOC.md:336-359` |
| 6 | `docs/phase6/v1/PHASE_6_DOC.md` / RC3 | R24, PASS | **Unverified:** resolver and retirement grants §§0.23–0.24; `docs/phase6/v1/PHASE_6_DOC.md:296-302` |
| 7 | `docs/phase7/v1/PHASE_7_DOC.md` / RC3 | R36, PASS | **Unverified:** coordinated rebuild §0.40; `docs/phase7/v1/PHASE_7_DOC.md:329-355` |
| 8 | `docs/phase8/v1/PHASE_8_DOC.md` / RC3 | R4, PASS | **Unverified:** binding/planning grants §§0.7–0.8; `docs/phase8/v1/PHASE_8_DOC.md:168-175` |
| 9 | `docs/phase9/v1/PHASE_9_DOC.md` / RC3 | None | **Unverified:** no review directory/numbered review present |
| 10 | `docs/phase10/v1/PHASE_10_DOC.md` / v3 | None | **Unverified:** initial architecture; `docs/phase10/v1/PHASE_10_DOC.md:15-16` |
| 11 | `docs/phase11/v1/PHASE_11_DOC.md` / v3 | R10, PASS, four unordered notes | Local PASS retained; `docs/phase11/reviews/PHASE_11_REVIEW_10.md:158-178`; newer P3/P6 integration remains subject to this review |
| 12 | `docs/phase12/v1/PHASE_12_DOC.md` / v3 | R4, PASS, one unordered note | Local PASS retained with explicit P3 re-confirmation condition; `docs/phase12/reviews/PHASE_12_REVIEW_4.md:94-113` |
| 13 | `docs/phase13/v1/PHASE_13_DOC.md` / v3 | R3, FAIL | **Unverified:** coordinated rebuild after FAIL, not an outstanding unreconstructed historical artifact; `docs/phase13/v1/PHASE_13_DOC.md:118-150` |
| 14 | `docs/phase14/v1/PHASE_14_DOC.md` / v3 | None | **Unverified:** initial architecture (`docs/phase14/v1/PHASE_14_DOC.md:12-13`); reviews directory empty |

## 1. Findings

Counts: **blocking=1; corrections=24; notes=4**. All findings have high confidence in the cited textual mismatch/absence; proposed implementation consequences are marked `[INFERENCE]`. Notes order no contract change. Corrections require owner reconciliation before integration clearance; their severity does not mean the incompatible path is safe to implement. No finding establishes a need to rebuild an entire phase document, so the structural-rebuild verdict is not selected.

| ID | Severity | Finding |
|---|---|---|
| IR-01 | blocking | Final integration eligibility is unmet |
| IR-02 | correction | The harness and capture agent speak incompatible protocol versions |
| IR-03 | correction | Configuration consumers have not completed the current Phase 3 cutover |
| IR-04 | correction | Recent producer grants remain open or absent in consumer ledgers |
| IR-05 | correction | oldLighting and separateAo have no accepting behavior owner |
| IR-06 | correction | The GUI reload algebra has no adopted frame-driver adapter |
| IR-07 | correction | One configuration is not necessarily one Phase 4 generation increment |
| IR-08 | correction | Normal/specular GUI settings are erased by preliminary macro policy |
| IR-09 | correction | Phase 9 still retains the old pipeline where Phase 7 requires off |
| IR-10 | correction | Expression shutdown calls a removed Phase 6 lifecycle operation |
| IR-11 | correction | The notifier table disagrees with actual matrix and entity-color producers |
| IR-12 | correction | Supersampling execution is handed to Phase 7 but never accepted there |
| IR-13 | correction | Anaglyph binding assumes a deliberately absent facade verb |
| IR-14 | correction | Bootstrap recreates a hook the foundation deliberately replaced |
| IR-15 | correction | The expression-backend measurement handoff is rejected by its recipient |
| IR-16 | correction | GUI column resolution still implements the superseded owner semantics |
| IR-17 | correction | Expression diagnostics have no published path to the GUI |
| IR-18 | correction | The non-fullscreen countInstances handoff overstates agreement |
| IR-19 | correction | Compatibility handling consumes outcomes the foundation never published |
| IR-20 | correction | Evaluator-specific conformance runs have no receiving harness contract |
| IR-21 | correction | atlasSize has a query but no adopted bind-to-uniform producer |
| IR-22 | correction | Shadow traversal cannot satisfy the tile-entity ID scope precondition |
| IR-23 | correction | Modernization still consumes the pre-cutover binding and lifecycle model |
| IR-24 | correction | Engine setting keys, domains and defaults disagree across the codec boundary |
| IR-25 | correction | Restart pack selection uses display text instead of the durable reference |
| IR-26 | note | The advertised timing seam has no public receiving contract |
| IR-27 | note | Async compilation lacks an explicit Phase 4 owner-change request |
| IR-28 | note | Foundation first-hook verification duties are not explicitly accepted |
| IR-29 | note | The source-free golden snapshot handoff is not a published frontend API |

### IR-01 — Final integration eligibility is unmet

- **Severity:** blocking.
- **Location:** All fourteen phase inputs; §0.1 of this report.
- **Claim:** The final review may clear implementation only after all fourteen phases are verified.
- **Evidence:** DESIGN §G5.3 item 4 requires this explicitly. Eight formerly reviewed phases changed binding contracts; P9/P10/P14 have no review. Local PASS remains only for P2/P11/P12, with changed dependency integration still open.
- **Required disposition / owners:** Maintainer and all affected phase owners: finish owner/consumer amendments and their individual verification, then rerun integration on that verified set. Do not relabel historical PASS or treat this provisional report as clearance.

### IR-02 — The harness and capture agent speak incompatible protocol versions

- **Severity:** correction.
- **Location:** P2 §§5.1/5.4 R19; P7 §4.13, incorporated capture seam.
- **Claim:** P2 publishes only /2; P7 still consumes/emits /1 and schedules shots rather than dense camera-path samples.
- **Evidence:** `docs/phase2/v2/PHASE_2_DOC.md:1752-1758,1862-1868`; `docs/phase7/v1/PHASE_7_DOC.md:1436-1457`. The producer explicitly rejects /1 compatibility. [INFERENCE] The specified agent cannot execute the current plan or emit a manifest the current reader accepts.
- **Required disposition / owners:** P7: adopt R19 end-to-end—/2 parsing, dense samples, warm-up, actual current/previous pose report, frame-end capture and exact manifest serialization. P2: reconcile the handoff only after receiver adoption. Keep historical /1 evidence, not a runtime compatibility path.

### IR-03 — Configuration consumers have not completed the current Phase 3 cutover

- **Severity:** correction.
- **Location:** P3 §§5.1/5.3/5.4; P4 §§4.7/5.3; P7 §§4.1/5.2; P12 §§4.3–4.7/5.2–5.4; P13 §§5.3/11.5.
- **Claim:** The current owner publishes schema 16, catalog-bound materialization, required companion-option inputs and lossless texture declarations; consumers still describe older operation shapes or publication meanings.
- **Evidence:** `docs/phase3/v1/PHASE_3_DOC.md:1210-1215,3243-3257,3337-3367` rejects inferred upgrades. P4 still treats OptionState as a materialization input (`docs/phase4/v1/PHASE_4_DOC.md:1160-1163,1846`) and lacks granted projections (`:1848,1866-1871`). P7 specifies an unchanged-load fallback and schema-v4 IDs (`docs/phase7/v1/PHASE_7_DOC.md:749-757,2097-2098`). P12 still relies on unclosed option/persistence contracts (`docs/phase12/v1/PHASE_12_DOC.md:1270-1314`). Its symbolic schema check is not itself a hardcoded-4 bug.
- **Required disposition / owners:** P4/P7/P9/P11/P12/P13 and other configuration consumers: reconcile every consumed current shape, operation and semantic against P3 §5; adopt schema 16 where consumed, with no fabricated older-schema defaults. In particular, preserve same-build options/macros and take P4 mipmap/vertex values directly from P3. P12 must rederive presentation/profile/persistence behavior, not merely update line numbers or its displayed schema number.

### IR-04 — Recent producer grants remain open or absent in consumer ledgers

- **Severity:** correction.
- **Location:** P1/P3/P6/P8 producer grants versus P4/P5/P7/P8/P9/P13 active requests.
- **Claim:** Several requests now exist on the owner side, but recipients continue to say ungranted. Owner adoption is not yet verified consumer adoption.
- **Evidence:** P1 package/legacy grants: `docs/phase1/v14/PHASE_1_DOC.md:1447-1479,1506-1526`. P3 companion/lossless/projection grants: `docs/phase3/v1/PHASE_3_DOC.md:355-441`. P6 R7-10/11: `docs/phase6/v1/PHASE_6_DOC.md:239-302`. P8 R7-12/13: `docs/phase8/v1/PHASE_8_DOC.md:122-175`. Consumer examples: `docs/phase4/v1/PHASE_4_DOC.md:1886-1907`; `docs/phase5/v1/PHASE_5_DOC.md:2867-2879`; `docs/phase13/v1/PHASE_13_DOC.md:1341-1364`.
- **Required disposition / owners:** Receiving owners: migrate executable contract descriptions and reconcile each request as owner-designed/unverified, receiver-adopted/unverified, or still genuinely missing. Keep fresh-review gates. Do not mark all requests granted: native legacy source preservation, typed suffix semantics, jcpp dependency permission, and new P10/P14 proposals remain separate work.

### IR-05 — oldLighting and separateAo have no accepting behavior owner

- **Severity:** correction.
- **Location:** P3 engine-flag map; P10 §§1/5; P12 bake/reload policy.
- **Claim:** P3 assigns both flags to P10; P10 designs vertex transport and invalidation but does not accept either flag or its render-visible policy.
- **Evidence:** `docs/phase3/v1/PHASE_3_DOC.md:1433-1458` assigns oldLighting/separateAo to P10. `docs/phase10/v1/PHASE_10_DOC.md:118-150,936-960` has no such input and leaves lighting vanilla-owned. `docs/phase12/v1/PHASE_12_DOC.md:959-975` assumes P10 owns both bake-time effects. [INFERENCE] Rebuilding meshes cannot apply a policy no producer computes.
- **Required disposition / owners:** P10 with P3/P7/P12: explicitly accept the two behaviors, higher-priority oldLighting setting, effective defaults/precedence and bake/invalidation handoff, or route a justified ownership correction through the relevant authority/owner process. Preserve the prohibition on chunk-renderer performance rewrites; shader lighting/AO policy is not permission for those.

### IR-06 — The GUI reload algebra has no adopted frame-driver adapter

- **Severity:** correction.
- **Location:** P12 §§4.7/5.1/5.3(B); P7 §§4.1/5.1/5.3.
- **Claim:** P12 assigns ReloadCoordinator.submit(ReloadRequest) to P7, carrying NONE/REPUBLISH/FULL plus worldRendererReload/resourceReacquire. P7 exposes a distinct reload intent/reasons protocol without that adoption.
- **Evidence:** `docs/phase12/v1/PHASE_12_DOC.md:925-1018,1176-1177,1244-1263`; `docs/phase7/v1/PHASE_7_DOC.md:1689-1692`. The P12→P7 handoff explicitly says this is a new obligation; similarity of reason names is not an adapter. Resource-manager reload is P12 NONE plus resource reacquisition, not an implicit FULL reload. P7 explicitly forces P3/P9 refresh on RESOURCE_RELOAD (`docs/phase7/v1/PHASE_7_DOC.md:1142-1148,2037-2045`), directly contradicting P12 `NONE` plus resource reacquisition (`docs/phase12/v1/PHASE_12_DOC.md:938-950`).
- **Required disposition / owners:** P7/P12/P13: publish one exact translation/drain contract covering every matrix row, both additive flags and merging; preserve discovery versus republish distinctions and quiescent resource retirement. Supply the requested programmatic option/engine-setting bridge for P2 rather than assuming GUI intents implement it.

### IR-07 — One configuration is not necessarily one Phase 4 generation increment

- **Severity:** correction.
- **Location:** P12 RS-1; P4 publisher generation; P7 coordinated rollback.
- **Claim:** P12 equates one published configuration with exactly one P4 counter bump, but P7 can accept a registry then compensate it off before admitting the pipeline.
- **Evidence:** `docs/phase12/v1/PHASE_12_DOC.md:1005-1013,1234-1242`; `docs/phase7/v1/PHASE_7_DOC.md:777-809,847-850`. A post-estate/texture failure reaches both Ready publication and ShadersOff publication. P7 explicitly keeps its equality-only PipelineVersion separate from P4/P5/P9 generations. P4 also requires P12 generation-sensitive cache invalidation (`docs/phase4/v1/PHASE_4_DOC.md:2314-2315`), while P12 explicitly observes only an indirect count consequence.
- **Required disposition / owners:** P12/P7: express coalescing as one drained request/one final composition outcome, and specify observed P4 generations for successful, rejected, compensated-off and explicit-off paths. Retain every owner-required cache invalidation; do not suppress a real generation change to make RS-1 true. P4 already exposes its generation—the outstanding defect is not a missing counter. Publish a direct or P7-mediated invalidation route for genuinely generation-sensitive UI/diagnostic caches, or narrow P4's promise if P12 has only configuration-keyed state.

### IR-08 — Normal/specular GUI settings are erased by preliminary macro policy

- **Severity:** correction.
- **Location:** P12 settings/reload table; P13 preliminary producer; P3 granted typed pair; P7 adaptation.
- **Claim:** P12 publishes independent normalMap/specularMap toggles, but P13 preliminary demand contains only packActive and fixedUnitCapabilityAvailable and sets both booleans to their conjunction.
- **Evidence:** `docs/phase12/v1/PHASE_12_DOC.md:807-808,853-854,955`; `docs/phase13/v1/PHASE_13_DOC.md:259-265,1159`; `docs/phase7/v1/PHASE_7_DOC.md:749-756`. P3 preserves the supplied pair rather than supplying the missing preference decision (`docs/phase3/v1/PHASE_3_DOC.md:355-376`). [INFERENCE] With an active/capable pack, toggling either preference off leaves its macro enabled.
- **Required disposition / owners:** P13/P7/P12: carry decoded independent user preferences into the pre-jcpp policy and specify disabled companion allocation/binding consistently. P3 retains ownership of macro emission/fingerprint propagation. Never decide the macros from completed linked-program demand; that would reintroduce the documented construction cycle.

### IR-09 — Phase 9 still retains the old pipeline where Phase 7 requires off

- **Severity:** correction.
- **Location:** P9 §5.3; P7 §5.3 and incorporated §4.1; P10 R10-3.
- **Claim:** P9 says any prepublication failure retains the old whole pipeline. Current P7 forbids resuming it on any failed rebuild and installs textures before IDs.
- **Evidence:** `docs/phase9/v1/PHASE_9_DOC.md:800-816`; `docs/phase7/v1/PHASE_7_DOC.md:749-809`; `docs/phase10/v1/PHASE_10_DOC.md:943,959`. This is already explicitly flagged by P10, but not repaired in P9.
- **Required disposition / owners:** P9: adopt current composition order and off-on-failure policy without changing its valid local pure-builder rule that building a candidate does not mutate a publication. Reconcile R9-1/R9-2 adoption and bind ordinal-map/lookup lifetime to P10 worker draining.

### IR-10 — Expression shutdown calls a removed Phase 6 lifecycle operation

- **Severity:** correction.
- **Location:** P11 §4.12 incorporated by §5.1; P6 §§4.14/5.
- **Claim:** P11 sends terminal CLOSE to P6 after closing its own controller. P6 removed CLOSE and publishes retire(reason) instead.
- **Evidence:** `docs/phase11/v1/PHASE_11_DOC.md:965-980,994,1005-1009`; `docs/phase6/v1/PHASE_6_DOC.md:272-302`. This is not a demand to remove P11-owned CLOSE; only the P6 call and composition mapping are obsolete.
- **Required disposition / owners:** P11/P7: map unpublished abort, replacement and shutdown to P6 retirement after final participant use; preserve P11 controller terminal semantics, custom state resets, and rejected-retirement handling. Do not restore reset(CLOSE) as a compatibility alias.

### IR-11 — The notifier table disagrees with actual matrix and entity-color producers

- **Severity:** correction.
- **Location:** P6 §4.12; P7 hook catalog; P9 color scope.
- **Claim:** P6 still locates matrix capture at the ordinal-zero clear and prices entityColor as P7/v0.1; P7 separates the later post-camera capture and defers real color to P9/v0.3.
- **Evidence:** `docs/phase6/v1/PHASE_6_DOC.md:1287-1303`; `docs/phase7/v1/PHASE_7_DOC.md:1231-1242,1272-1273`. Current P7 explicitly says clear precedes camera setup. Remaining typed frame/fog/blend/shadow/held/atlas producers were traced, not inferred from nullable listeners. P6 §4.6 already supplies the correct post-camera semantics; the matrix phrase is residual table drift, not evidence that the current frame design captures pre-camera matrices.
- **Required disposition / owners:** P6/P7/P9: synchronize the notifier table to the split timeline and one explicit entityColor owner/milestone; check the governing milestone before accepting any deferral. Preserve frame sampling before resize/clear, later current-matrix capture, immediate scoped color upload/restoration, and neutral behavior before owner installation.

### IR-12 — Supersampling execution is handed to Phase 7 but never accepted there

- **Severity:** correction.
- **Location:** P5 §§4.11.1/11.4; P7 scope/frame/fullscreen/interface sections.
- **Claim:** P5 retains SupersamplingPlan.level without changing extent and assigns the draw/sample sequence to P7; P7 contains no supersampling/SSAA execution contract.
- **Evidence:** `docs/phase5/v1/PHASE_5_DOC.md:1986-1992,2867-2874,2894-2898`. The full P7 audit found no receiving operation or schedule for this handoff. P5 labels the authority clarification nonblocking; that does not implement the independently promised execution path.
- **Required disposition / owners:** P5/P7: settle the existing clarification against authority, then expose/consume the actual sample/draw schedule, level, final resolve and failure behavior. Do not silently multiply allocation extents or mark retention of the integer as execution.

### IR-13 — Anaglyph binding assumes a deliberately absent facade verb

- **Severity:** correction.
- **Location:** P7 §5.1 FrameRenderPort; P1 §5.2 non-verbs; P5 platform boundary.
- **Claim:** P7 says the eye color mask is applied through the P1 facade. P1 explicitly excludes color mask.
- **Evidence:** `docs/phase7/v1/PHASE_7_DOC.md:1875-1877`; `docs/phase1/v14/PHASE_1_DOC.md:4391-4394`. [INFERENCE] A consumer cannot implement the specified call through the published facade.
- **Required disposition / owners:** P7: use the existing mod-side vanilla/platform state-restoration boundary, consistent with P5, or request an explicit owner grant if a facade operation is truly necessary. Do not invent a native call inside engine code.

### IR-14 — Bootstrap recreates a hook the foundation deliberately replaced

- **Severity:** correction.
- **Location:** P1 §11.4 bring-up handoff; P7 H-BOOT-01.
- **Claim:** P1 replaces the early GameSettings loadOptions hook with preInit/FMLLoadCompleteEvent; P7 reinstates a CORE GameSettings RETURN hook while claiming P1 ownership of the sequence.
- **Evidence:** `docs/phase1/v14/PHASE_1_DOC.md:5204-5221`; `docs/phase7/v1/PHASE_7_DOC.md:1223-1227`. The GL-ready RETURN requirement and main-menu recommendation otherwise match.
- **Required disposition / owners:** P7/P1: reconcile stage-one production and health classification with the explicit loader-event decision, or request and justify a revised foundation contract. Do not require a redundant CORE injection whose absence would disable shaders.

### IR-15 — The expression-backend measurement handoff is rejected by its recipient

- **Severity:** correction.
- **Location:** P11 §§1.2/5.1/10.1/11.3; P14 §7.5 L-11.
- **Claim:** P11 assigns OQ-22 measurement to P14; P14 marks compiled-expression evaluation out of scope and sends ownership back to P11.
- **Evidence:** `docs/phase11/v1/PHASE_11_DOC.md:189,999,1314`; `docs/phase14/v1/PHASE_14_DOC.md:1777`; `docs/design/v3/DESIGN.md:2310-2314` expressly places the compiled path in P14 methodology.
- **Required disposition / owners:** P14: adopt the measurement ledger method/metrics consumer and record its decision point. P11 retains expression semantics and backend SPI; do not require a compiled evaluator absent evidence that the interpreter misses the budget.

### IR-16 — GUI column resolution still implements the superseded owner semantics

- **Severity:** correction.
- **Location:** P12 §4.3.4; current P3 ScreenModel and §5.1 publication.
- **Claim:** P12 gives explicit columns unconditional precedence and counts only option entries; current P3 defines configured columns as a floor over all expanded slots.
- **Evidence:** `docs/phase12/v1/PHASE_12_DOC.md:608-623`; `docs/phase3/v1/PHASE_3_DOC.md:843-856,3243-3289`. For explicit columns=1 with 19 expanded slots, P12 specifies 1 and P3 specifies 3. [INFERENCE] The same configuration produces incompatible GUI layout.
- **Required disposition / owners:** P12: consume the current ScreenModel resolver/counting contract and rederive presentation, star-expansion interactions and planned behavioral cases. Do not retain the old unconditional override or patch only the schema label.

### IR-17 — Expression diagnostics have no published path to the GUI

- **Severity:** correction.
- **Location:** P11 §§5.1/11.3; P12 §§4.9/5.1.
- **Claim:** P11 asks P12 to display immutable expression load diagnostics, but P12 consumes only P1 SHADER_GUI store entries and no owner publishes the conversion or direct view input.
- **Evidence:** `docs/phase11/v1/PHASE_11_DOC.md:770-788,992,1000,1313`; `docs/phase12/v1/PHASE_12_DOC.md:1075-1095`. P11 channels are CHAT_AND_LOG or LOG_ONLY, not P1 SHADER_GUI. Different diagnostic enums are valid; the missing handoff is their explicitly owned delivery/projection.
- **Required disposition / owners:** P11/P12/P7 and P1 if its store is used: specify the source-free immutable projection, pack identity/lifetime, severity/channel mapping and publication owner, or a direct typed GUI input. Do not assume equal-looking enum names or invent a fourth P11 channel.

### IR-18 — The non-fullscreen countInstances handoff overstates agreement

- **Severity:** correction.
- **Location:** P4 §11.4 versus P1 open case and P7 fullscreen executor.
- **Claim:** P4 promises both countInstances cases to P7; P7 specifies composite/deferred repetition, while P1 preserves the gbuffers/shadow re-render case as open.
- **Evidence:** `docs/phase4/v1/PHASE_4_DOC.md:1324-1326,2303-2310`; `docs/phase1/v14/PHASE_1_DOC.md:5223-5236`; P7 §4.6 contains the fullscreen loop, not a non-fullscreen re-render contract.
- **Required disposition / owners:** P4/P7/P1: explicitly disposition the non-composite case and synchronize the promise. Do not infer an extension API, modern instanced draw, or new milestone from the already specified fullscreen loop. Keep the unresolved case visible to its authority owner.

### IR-19 — Compatibility handling consumes outcomes the foundation never published

- **Severity:** correction.
- **Location:** P7 §4.12; P1 compatibility mechanism; P10 §11.2 item 5.
- **Claim:** P7 attributes Compatible/ReplaceableBackendDetected/UnsafeRendererDetected to P1; the owner publishes Ok/Degrade/Bail and CompatEvaluation.
- **Evidence:** `docs/phase7/v1/PHASE_7_DOC.md:1398-1403`; `docs/phase1/v14/PHASE_1_DOC.md:3968-4009`; `docs/phase10/v1/PHASE_10_DOC.md:1282-1285`. P10 already names the exact mismatch and declines to invent a backend API.
- **Required disposition / owners:** P7: consume actual CompatEvaluation/session Bail and the owner-approved evaluation points. If a distinct adapter is required, publish and own its mapping; do not grant backend replacement from an invented enum.

### IR-20 — Evaluator-specific conformance runs have no receiving harness contract

- **Severity:** correction.
- **Location:** P11 §11.3; P2 §4.9/§5.3.
- **Claim:** P11 requests scripted-provider, matrix/error and function/smooth golden runs. P2 publishes front-end goldens, tiers and motion but no run invoking this evaluator surface.
- **Evidence:** `docs/phase11/v1/PHASE_11_DOC.md:1310-1314`; `docs/phase2/v2/PHASE_2_DOC.md:1307-1338,1797-1811`. Generic shader load or Properties parsing is not execution of evaluator functions and smooth transitions.
- **Required disposition / owners:** P11/P2: promote the outgoing handoff to an exact §5 request and adopt a named run or specified adapter using P11-owned vectors/providers. Keep pack material out of committed goldens. No real-pack result is required or claimed in this documentation review.

### IR-21 — atlasSize has a query but no adopted bind-to-uniform producer

- **Severity:** correction.
- **Location:** P6 notifier/hand-off; P13 §§4.4/5.5; P7 uniform signal adapter.
- **Claim:** P13 exposes Known/Unknown size and assigns the runtime adapter to P7; P6 requires current-atlas bind events, immediate upload when active and reload reset. No owner joins the bind event to that sink.
- **Evidence:** `docs/phase6/v1/PHASE_6_DOC.md:1295-1303`; `docs/phase13/v1/PHASE_13_DOC.md:919-950,1423-1426`; `docs/phase7/v1/PHASE_7_DOC.md:1857-1882`. Stitch-time availability is not evidence that the atlas is currently bound. The current signal algebra carries only Celestial/Fog/Blend.
- **Required disposition / owners:** P13/P7/P6: publish one authenticated bind/current-atlas→existing updateAtlasSize adapter, including Unknown/reset and immediate-active-upload timing. Preserve P5 sole physical binding ownership; do not assume every gbuffers/shadow draw uses the base atlas.

### IR-22 — Shadow traversal cannot satisfy the tile-entity ID scope precondition

- **Severity:** correction.
- **Location:** P8 §11.4; P7 shadow guard and H-ENTITY-03; P9 tile-entity augmentation.
- **Claim:** P8 requires shadow-aware entity/TE scopes. During shadow execution P7 forbids opening gbuffers scopes, yet the current TE-ID augmentation requires an accepted gbuffers_block scope.
- **Evidence:** `docs/phase8/v1/PHASE_8_DOC.md:1757-1764`; `docs/phase7/v1/PHASE_7_DOC.md:987-994,1265-1266`; `docs/phase9/v1/PHASE_9_DOC.md:677-685`. P9 has no alternate authenticated shadow-entry contract. [INFERENCE] The specified main-scope precondition cannot deliver the promised shadow TE IDs without violating shadow routing.
- **Required disposition / owners:** P7/P8/P9: accept the existing authenticated shadow execution as a distinct ID-scope admission path, with exact nested restoration and no main snapshot/program activation. Preserve Phase 8 Forge pass ordering and failure neutralization.

### IR-23 — Modernization still consumes the pre-cutover binding and lifecycle model

- **Severity:** correction.
- **Location:** P14 §§4.1/5.2–5.5; current P5/P6/P7/P13 owners.
- **Claim:** P14 assigns physical binds to P7, map ownership to P6, omits current resize reasons, and targets an eighteen-step transaction; the current owners publish a different ten-step shared-unit protocol.
- **Evidence:** `docs/phase14/v1/PHASE_14_DOC.md:610-631,1473-1496,1532-1546`; `docs/phase5/v1/PHASE_5_DOC.md:2360-2364`; `docs/phase7/v1/PHASE_7_DOC.md:749-809`. P5 performs binds and Bound-only lease transfer, and adds SHADOW_QUALITY/PACK_CONFIGURATION/REGISTRY_PLAN to the resize domain. P13 now publishes exact parameters (`docs/phase13/v1/PHASE_13_DOC.md:1158`).
- **Required disposition / owners:** P14: consume current P5 policy/physical-binding and all invalidation reasons, exact P13 parameters/retirement, and current P7 construction points. Do not add a parallel frame binder, revive retired leases or infer async permission. Keep the explicit synchronous/sampler-0 fallback for unadopted extensions.

### IR-24 — Engine setting keys, domains and defaults disagree across the codec boundary

- **Severity:** correction.
- **Location:** P12 §§4.6/5.1; P3 §5.1 EngineOptionData invariant.
- **Claim:** P12 owns seven GUI key spellings and a default/true/false old-light tri-state; P3 now recognizes a different eight-key vocabulary with Boolean-only old-light values and fixed defaults.
- **Evidence:** `docs/phase12/v1/PHASE_12_DOC.md:797-859,869-881`; `docs/phase3/v1/PHASE_3_DOC.md:2728-2778`. P12 renderQuality/shadowQuality/handDepth differ from P3 renderResMul/shadowResMul/handDepthMul; normalMap/specularMap differ from normalMapEnabled/specularMapEnabled. Known oldHandLight=default and oldLighting=default are invalid to P3. Unknown-safe GUI quality keys may round-trip but do not project to macros.
- **Required disposition / owners:** P3/P12 with P5/P7/P8/P9/P10/P13: agree one key/domain/default/priority vocabulary and exact adapters, preserving user-over-pack DEFAULT semantics and shaders-only scope. Explicitly reconcile the antialiasingLevel/MC_FXAA_LEVEL declaration versus the no-AA product boundary; do not add an AA feature or retain mismatched keys as aliases by default.

### IR-25 — Restart pack selection uses display text instead of the durable reference

- **Severity:** correction.
- **Location:** P12 persisted shaderPack contract; P3 durable reference and target acquisition.
- **Claim:** P12 persists a sanitized display name and chooses the first collision. P3 makes durable reference resolution the only restart bridge and returns explicit ambiguity/kind/snapshot outcomes.
- **Evidence:** `docs/phase12/v1/PHASE_12_DOC.md:883-898,1180`; `docs/phase3/v1/PHASE_3_DOC.md:2671-2696,3112-3128`. Display text is expressly not an accepted persistence target. [INFERENCE] A sanitization collision can restore a different pack under P12's rule.
- **Required disposition / owners:** P12/P7: persist the owner-defined FilesystemCandidateReference representation, resolve it against fresh discovery, and handle Resolved/Missing/Ambiguous/KindChanged/InvalidSnapshot explicitly before obtaining a safe target. Retain Off/Internal sentinels; never serialize candidate IDs or choose a first display-name collision.

### IR-26 — The advertised timing seam has no public receiving contract

- **Severity:** note.
- **Location:** P7 §1.2 versus §5; P14 R-P14→P7-2.
- **Claim:** P7 promises timing and resize seams, but per-phase elapsed timing remains internal; P14 has already filed the missing public timing request and supplies JFR fallback.
- **Evidence:** `docs/phase7/v1/PHASE_7_DOC.md:415-417`; `docs/phase14/v1/PHASE_14_DOC.md:1548-1564`. The resize consumer is P5-owned; frame counts are not elapsed per-pass timing.
- **Required disposition / owners:** P7/P14: expose the small read-only timing contract or narrow the promise and reciprocally accept JFR attribution. Optional performance tooling is not a reason to block the synchronous rendering baseline.

### IR-27 — Async compilation lacks an explicit Phase 4 owner-change request

- **Severity:** note.
- **Location:** P14 CompileExecutor consumer promise; P4 compiler; P7 resumability request.
- **Claim:** P14 names P4 as a submit/poll consumer but routes the new contract only to P7 resumability and P13 upload staging. The synchronous P4 compiler has no accepted prepare/worker/link split.
- **Evidence:** `docs/phase14/v1/PHASE_14_DOC.md:1458,1539-1546`; `docs/phase4/v1/PHASE_4_DOC.md:1784,2319-2320`. P4 allows later acceleration subject to its existing contract, not an invented asynchronous compiler interface.
- **Required disposition / owners:** P14/P4/P7: route the exact P4 split/ownership/adoption request before enabling async. Preserve Inline/current synchronous fallback and render-thread publication; a declined optional optimization does not require rebuilding the core.

### IR-28 — Foundation first-hook verification duties are not explicitly accepted

- **Severity:** note.
- **Location:** P1 §11.3 items 8–9 and §11.4; P7 hook bring-up/OQ-4.
- **Claim:** P1 hands P7 first-config refmap generation and JAVA_8-versus-Java-25 checks; P7 only mentions refmaps in its broader OQ-4 procedure.
- **Evidence:** `docs/phase1/v14/PHASE_1_DOC.md:5023-5035,5192-5195`. The full P7 scope/interface/handoff audit found no explicit acceptance of both duties. This is missing future-task accounting, not an observed bytecode/refmap failure.
- **Required disposition / owners:** P7: acknowledge those two implementation-time checks at the first real hook/configuration gate or identify the exact owner that already performs them. No build result is required in this architecture review.

### IR-29 — The source-free golden snapshot handoff is not a published frontend API

- **Severity:** note.
- **Location:** P2 §§4.11.4/5.4 R5–R9; P3 §5.2 and §8.2.
- **Claim:** P2 requires an engine-owned snapshot/acquisition route with P4 enrichment. P3 supplies component data and artifact intent, but no accepted complete P2 inspection/projection boundary.
- **Evidence:** `docs/phase2/v2/PHASE_2_DOC.md:1518-1538,1838-1851`; `docs/phase3/v1/PHASE_3_DOC.md:3225-3239`. Per-file/configuration hashes are not the P2-owned verified archive SHA-512. The request is explicit, not an existing producer promise.
- **Required disposition / owners:** P2/P3/P4 and P1 for requested diagnostic consumption: publish or explicitly adopt the complete source-free projection/acquisition route and archive-provenance bridge. Keep partial synthetic goldens distinct from complete matrix verdicts; no new parser-private or conformance dependency in engine.

## 2. Dependency-edge symmetry

**All 37 §G5.1 edges audited: 36 hard plus P7→P12 soft.** The P5→P6 maintained edge is additionally shown, not counted as a new original graph edge. Other sibling/reverse requests are accounted in §4; an implementation/composition callback is not automatically a build-order dependency. “Matched” means specified interfaces agree at the audited bytes, not verified runtime readiness or clearance of other findings.

Citation shorthand in the coverage tables: **Pn** resolves to the full current document path in §0.1/Appendix A; **R** means `docs/research/v1/RESEARCH.md`; **D3** means `docs/design/v3/DESIGN.md`; RC2/RC3 mean the full respective versioned DESIGN paths in §0. Section references are controlling; accompanying numeric locators are audit navigation aids, not a claim that old coordinates quoted inside a phase remain current. All finding IDs resolve to §1. The four component audits are integrated below; their individual impact labels do not override this report's consolidated severities.

### 2.1 Foundation/front-end consumers

| Directed edge | Matched producer promises / consumer adoption | Mismatch or pending item | Disposition |
|---|---|---|---|
| **P1→P2** | Module/classpath seam, test-fixture placement, capability serialization, recording stability/capture flags, CI extension and replay-aware GL evidence: P1 §§5.1–5.3 4354–4439 ↔ P2 §5.2 1766–1795. P2 copies attribution rather than deriving it; derived-artifact policy matches P1 §11.4 and P2 §4.11. | R2 conformance system properties, R3 second conformance task acknowledgement, R4 opt-in mod GL task and R4B diagnostic type consumption remain explicit requests at P2 1824–1835. P1 current §5 does not grant these. R1 package and R4A attribution are granted and recognized by P2. | **Matched existing edge; pending explicit additions**, not contradictions. |
| **P1→P3** | Pure package/seam, entire capability profile as macro input, log/diagnostic types, saveSources name and notices: P1 4384–4385,4427–4438 ↔ P3 §5.2 3225–3239. All standard GL macro fields, not only extensions, are projected by P3 2786–2800. | jcpp pin/build/seam admission is explicitly requested by P3 3321–3333 and §11.5 item1; P1 recognizes licensing but publishes no build pin (P1 3739–3741,3798–3800,5188). P1's new native-preserving geometry source request 5117–5145 remains absent from P3's closed None/Translate algebra 1306–1320,3075–3081. | **Matched existing edge; pending explicit build/source additions.** Native source gap is not a claimed existing API. |
| **P1→P4** | Opaque handles, never-throwing compile/link/validate results, fixed-function use, state restore/lock, recorder/replay, profile and diagnostics: P1 §5.2 ↔ P4 §5.2 1813–1831. No raw/null program-zero bypass. | P1 now grants `configureLegacyGeometry` at 4380 and detailed migration at 5156–5189; P4 1278–1291 and 1859–1865 still say no pre-link operation exists. Source preservation is still genuinely ungranted, so unavailable legacy fallback remains honest, but the producer/consumer grant ledger is no longer symmetric. | **Correction IR-04; native compilation remains explicitly gated.** |
| **P3→P4** | One finalized configuration, no-merge dimensions, materialized source/type catalogs, whole-provider state, singular macro contribution, metadata fingerprints: P3 §5.1 ↔ P4 §5.3 1833–1853 and uniform merge 1176–1195. P4 correctly rejects type conflicts before GL and retains source attribution. | P4 planning still supplies explicit `OptionState` to materialize (1160–1163), while P3's exact API 1210–1214/3058–3064 and handoff 3978–3983 forbid any state argument. New direct mipmap/vertices grant P3 2999–3040 is still pending/denied in P4 1848,1866–1871,2283–2287. Legacy source completion remains separately ungranted. | **API correction IR-03 and grant-reconciliation IR-04.** No need for reverse P5/P10 reads. |

### 2.2 Render-core consumers

`Matched` means current architectural promises agree, not verified/implemented readiness. Finding references resolve to §1 of this report.

| Producer→consumer | Matched current promises | Mismatch/disposition |
|---|---|---|
| 1→5 | P1§5.2:4380–4393 grants authenticated borrowed depth, combined depth/stencil, initial/steady copies, opaque lifetimes; P5§5.2:2377–2393 consumes precisely these and retains format/cadence/restoration ownership. | Matched; historical missing-facade finding is closed in current endpoints. |
| 3→5 | P3§5.1:2949–3056 supplies closed minima/formats/clear/routing/shadow/half-life/world values; P5§5.3:2395–2410 uses immutable configuration, flip projection and requirements, keeping runtime quality separate. | Core buffers matched. P5§5.3.1:2444–2458 still describes old ungranted macro/suffix producer state: IR-03/IR-04; no invented Phase3 runtime-quality field. |
| 4→5 | P4§5.1:1782–1796 grants detached candidate view, full effective state/layout, pure policy pairing, selector validation and virtual descriptors; P5§5.4:2460–2475 consumes all without handles/fallback re-resolution. | Matched select-once/virtual transition/metadata ownership. Additional maintained reverse5→4 is pure appB3 policy, not a runtime cycle. |
| 1→6 | P1§5.2 provides typed uploads, readDepthPixel, absent locations, replay-aware errors; P6§5.2:1633–1646 consumes them. | Matched. Synchronous center-depth is deliberately sufficient; async age extension stays explicit pending proposal. |
| 3→6 | P3§5.1 final declared uniform metadata, resource half-lives and reserved contributor; P6§5.2:1648–1657 consumes through effective P4 layout; P6§5.1:1625 contributes Empty. | Matched; no GLSL reparse or local GPU redirect. Current-schema adoption must not be inferred from historical references. |
| 4→6 | P4§5.1:1787–1796 effective uniform/sampler layouts, bound callback access/activity token, fixed three callbacks; P6§5.2:1659–1679 and §4.9 consume unchanged afterBind. | Matched sampler/built-in/custom ordering, whole-provider fallback and token lifetime. Stale R7-10/11 request labels elsewhere are IR-04, not missing current P6 APIs. |
| 2→7 | Capture-after-final/readiness/shutdown, immutable resources/program/error/hook projections match P2§5 R11/R12/R14/R17/R18 and P7§5.1/§4.13. | IR-02: P2 requires capture-plan/2, run-manifest/2 and pose/history; P7 active§4.13 still /1. R19 explicitly requests migration: do not describe it as secretly assumed new API. |
| 3→7 | Single immutable configuration, no base/override merge, internal source/path, runtime macro materialization boundary match. | IR-03: P7§5.2:2102 schema-v4 conflicts with P3 schema16; required CompanionOptionMacros exists now, while P7§4.1:754–756/§5.4:2307,2351–2359 still permits unchanged old load. P3 texture suffix state also changed; downstream stale references are not producer authority. |
| 4→7 | Current compile request samplerPolicy, detached view, credentialed three-participant composition, actual Accepted/Rejected/RecoveredOff and select→bind→activate are mirrored P4§5.1/P7§5.2:2113–2136 and §5.3. | Matched main transaction. P4 projection/legacy pending ledger must be synchronized to current P1/P3 owner grants; these are explicit gates, not fabricated consumption. |
| 5→7 | P5§5.1 and P7§5.2:2138–2197/§4.3–4.6 match actual-generation publication, no-intervening-draw, depth PRE_WEATHER→PRE_TRANSLUCENT, exact virtual transitions, balanced overlay, SCREEN, resource projection, Bound-only lease transfer/discard. | IR-12: P5 hands SSAA execution to P7 but P7 has no corresponding contract. IR-13: anaglyph color-mask implementation route falsely names unavailable P1 facade (SCREEN target itself matches). |
| 6→7 | beginFrame before resize/clear; copied post-camera matrices once; no program-switch resampling; three callbacks; fog/blend and instance loops match. P7 requested exact resolver/retirement shapes match now-adopted P6. | IR-11 entityColor v0.1 producer vs only v0.3 P9 hooks; IR-04 stale R7-10/11 consumed inventory and lifecycle caller migration; IR-21 atlas delivery remains unclosed through composition. |
| 4→8 | P8§5.2:1224–1237 receives borrowed selection/context, validates rather than reselects, activates P4 only after physical bind; registry fingerprint separate from live generation. | Matched; fixed shadow produces no sampler candidates/uploads. |
| 5→8 | P8§5.3:1239–1310/§4.2:686–790 adopts five-argument shadowBindings, all16 rows, four results, frozen ordinary+shadow sides, mipmap Neutralized/abort semantics and exactly-one closure. | Matched active consumer. P5§4.12.4/§11 still calls P8 unchanged/unadopted: IR-04 correction only. No current four-row binding defect. |
| 6→8 | P6 primary shadow event plus inverse ownership matches P8§4.10:1111–1140 and §5.4:1313–1330: after FF setup, before first shadow activation. Shared celestial policy created before provider; same frame/sample identities. | Matched. “All four matrices” means two primary matrices plus P6-derived inverses, not four uploaded source events. |
| 7→8 | Exact11-field invocation, driver-owned bridge, one shadow selection, supplied lease source, after-main-setup/before-main-clear, nonnesting and finally lifetime match P7§5.1:1576–1633,1952–1998 / P8§5.4:1332–1373. Registry-independent plan + final-registry create now match. | IR-04 stale P7 R7-12/13 and P8 package/reporting request statuses. IR-22 outgoing shadow ID scope augmentation remains orphaned in P9; not a defect in the new binding transaction. |
| 5→6 (maintained) | Exact resolve(layout,stage,band)→Ready(bindings,policy)/Invalid(reason), required factory injection after configuration, same appB3 fingerprint; P5§2.4:899–917,§4.12.1:2118–2172/P6§4.9:1078–1163,§5.2:1681–1699. | Fully adopted in P6 current bytes. No second map. P5§5.5 item4 still says pending: IR-04. |

### 2.3 ID/vertex/expression consumers

| Edge | Producer → consumer matching promises | Mismatch/disposition |
|---|---|---|
| **3→9** | P3 §5.1:2647 and §4.9:2433–2493 supply schema-v16 IdMappingInput, all three file states, entry/tag distinction, classic/modern era, isolated forced11300 entity rules, bounded pure parser with published macro environment. P9 §4.7:571–581 and §5.4:822 request exactly those semantics; §5.2:764 accepts symbolic schema discipline. | **Architectural grant adopted upstream, consumer ledger stale.** P9 §5.2:765 still consumes removed UnresolvedIdMappings and §§1.3/5.4/11 say the inputs are absent. R9-1 needs consumer reconciliation, not a duplicate producer grant. No fresh-verification conclusion. IR-04. |
| **6→9** | P6 event methods 469–474 and HeldItemSample 709–726 match P9 §5.2:776–779 and §4.11:637–664: full-int IDs, world/tick identity, 0–15 static lights, old mode `(max(main,off),off)`, ID values not swapped. P6 §4.14.3:1566–1578 explicitly includes P9 restoration events in final use. | **Matched value/upload seam.** P9 returns real values through P6, no GL or provider resampling. Current P6 retirement must follow final P9 scope resets; P9's own publication close is not a UniformRuntime.close call. No additional value-ABI contradiction found. |
| **7→9** | P7 accepted held boundary 1230; entity/TE scopes 1265–1266; exact TexEnv capture/restoration 1272–1294; reason domain 1687–1692; geometry gate 1899–1907; and §5.3:2270–2276 adopt R9-2's downstream slot. P9 §5.4:823 and §§4.11–4.14 specify the corresponding values/hooks. | **Transaction mismatch:** P9 §5.3:802–815 and §6:848 retain the previous pipeline after prepublication build failure; current P7 §5.3:2235–2267 always converges off and places P13 before P9 publication. R9-2's historical absent-grant assertions are also stale. IR-09/IR-04. |
| **4→10** | P4 §5.1:1797 publishes exactly mc_Entity=10, mc_midTexCoord=11, at_tangent=12. P10 §4.1:279–292 and §5.2:940 consume that table only. P4 provider metadata/selection at 1784–1796 is for P7; P10 routes declared names through requested P7 adapter. | **Matched existing contract.** P10 does not invent direct active-program/private registry access. New declaration sink is R10-2; future modern name/location cutover is R10-4. Both are expressly ungranted, not contradictions. |
| **7→10** | P7 invalidator/result algebra 1695–1710 and invariant 1899–1907 match P10 §5.1:874,900–906 and §4.8:685–697: every ID generation invalidates old eligibility/layers even with equal bytes; Completed means scheduling complete, not all chunks rebuilt. P7 App-E rows 3–9 at 1358–1364 are fully taken by P10 §4.11:800–859. | **Existing contracts matched; extensions pending.** R10-2 adds worker-drain lifecycle and effective declaration delivery, neither silently assumed. Separately P7 1398–1403 claims a nonexistent Phase1 compatibility outcome domain; P10 §11.2 item5 correctly flags it. IR-19. |
| **9→10** | P9 §4.10:613–633 + §5.1:753–756 supply packed alias/renderType and metadata words, bit-pattern domain and generation. P10 §4.1:282–305 preserves all three shorts, neutral/Unrepresentable type+metadata, signed floating conversion and exact lookup result; §4.4:466–498 borrows matching ordinal map; §4.5:503–530 discards stale upload/state products and schedules replacement. | **Payload and stale scheduling adopted.** P9 already promises tables survive issued build borrows (§4.1:410–413). R10-3 explicitly requests stronger matched ordinal-map/worker-drain lifetime and P7 publication reconciliation; do not report that future lifetime precision as an invented grant. Existing transaction conflict is IR-09. |
| **3→11** | P3 §5 custom algebra 2803–2825 and P11 §5.2:1013–1032 match every field and both enum domains; immutable original occurrence order, duplicates retained, lossless Properties unescaping, attribution and ordered fingerprint participation. | **Matched current semantics.** P11's quoted P3 line anchors are stale, but current endpoints supply the same required projection. Do not infer stale schema literal: P11 consumes the published projection, not a new private parser. |
| **6→11** | P6 §5.1:1621–1623 and §4.13:1344–1478 match P11 §5.3–5.4:1036–1059: built-in-first/custom-third callback, exact schema/view, seven-name exclusion union, Bool1, normal SkippedAbsent, authoritative three counters and accepted-prefix commit. | **Terminal lifecycle mismatch:** P11 §4.12:976–977, incorporated by §5.1:993,1006–1009, still requires P6 terminal CLOSE. P6 §§2.2/4.14:429–435,1483–1487 explicitly remove CLOSE and expose retire(UNPUBLISHED_ABORT|REPLACEMENT|SHUTDOWN). IR-10. |

### 2.4 GUI/texture/modernization consumers

| Producer → consumer | Matched promises | Mismatch / disposition |
|---|---|---|
| P1 → P12 | P1 §5.1 4358–4360 package/seam contract; §5.3 4430–4438 diagnostics, logging, ModularUI dependency and notice mechanisms match P12 §5.2b 1214–1227. GUI remains GL-free. | No new integration mismatch found. Existing modImplementation work remains an acknowledged P1 implementation obligation, not an invented P12 API. |
| P3 → P12 | Immutable same-configuration option/state consumption and symbolic schema gate match; changed-only per-pack persistence remains owned by P3. P3 now actually publishes the option model and safe file-access contracts requested by P12. | Engine-setting keys/types/defaults differ; persisted pack identity differs; columns differ; profile/state/codec API calls are obsolete. IR-24, IR-16/25/03. Schema literal commentary is stale (4 versus current 16), but P12's actual equality rule is symbolic, so do not report a hard-coded-v4 bug. |
| P7 → P12 (soft) | P7 §4.1/§4.8 has safe quiescent boundaries, pack/dimension lifecycle, off recovery and no GUI-thread GL. P12 assumptions 1–3 are broadly met. | New ReloadCoordinator not adopted; P7 explicitly exposes ShaderReloadController instead. RESOURCE_RELOAD contradicts P12 NONE-only classification. P7's RebuildActive reuse contract does not supply P12's promised same-selection new-option load. IR-06/07. |
| P3 → P13 | Typed source variants, exact case-sensitive keys/discriminators, per-stage expansion, sidecar references and generated-noise data remain valid input families. R1 is now owner-granted as CompanionOptionMacros in current P3 §5. | P13 still instructs unchanged old load/no macros until an allegedly ungranted R1; it also still describes destructive suffix stripping, superseded by P3's lossless declaration publication. U1's execution grammar remains deliberately unresolved. IR-04; no claim that mere retention implements suffix honoring. |
| P5 → P13 | P5 §5.1 2360–2366 and P13 §5.1–5.2 1170–1294 agree on sole resolver, all names/targets, sixteen rows, actual estate generation, exact expected-publication lease, preflight before physical binds and Bound-only ownership transfer. Resize protocol agrees at P5 2008–2050 and P13 1090–1111. | R2 fulfilled architecturally. No independent unit map or lease-close mismatch found in this pair. P6/P8 requests mentioned around it need status synchronization, not a rollback to the old four-row model. |
| P7 → P13 | Current ten-step transaction is mirrored at P7 749–809/2230–2268 and P13 1296–1339; accepted generations precede texture build; admission follows owner/registration/ID gates; failure compensates off without old resources. | Both consumers still label already granted P3/P6/P8 requests unavailable. P12 setting input is absent from preliminary policy; atlas-size producer delivery remains orphaned across P13→P7→P6 (coordinated with the core audit). IR-08/IR-04. |
| P5 → P14 | Estate metadata, fixed sixteen-unit domain, filter policy, generation and synchronous resize notices remain supplied. | P14 §4.1.4 still says P7 binds the snapshot; current P5 operation physically binds before returning Bound, with closeable transferred leases. P14's five-reason list omits SHADOW_QUALITY/PACK_CONFIGURATION/REGISTRY_PLAN from current eight-reason domain. IR-23. |
| P6 → P14 | CenterDepthSource current exact Sample(float)/Unavailable SPI and read-before-resize order remain; CPU EMA means PBO work is not obviated. | Sample-age permission remains explicitly requested, not granted (P6 1838–1839). P14 correctly ships synchronous until grant; not a contradiction. P14's claim P6 owns an independent fixed map is obsolete after current resolver adoption. IR-23. |
| P7 → P14 | Frame exit/finally, closed reload status, composition and resize orchestration exist. | Async resumability and debug/timing extensions remain proposals. Timing is independently promised in P7 §1.2 416–417 but not exposed in §5; IR-26. P14's eighteen-step transaction and old step numbers must be reconciled to current ten-step owner contract; IR-23. |
| P13 → P14 | P13 owns textures, parameter semantics, lifetime, animation and downstream optimization delegation (1419–1425), matching P14 scope. TextureParameterSpec now exists (P13 1158), contrary to P14's active 'interfaces do not exist' statement. | Stageable submit/poll upload remains unadopted; synchronous build is the current API. Parameter value exists, but P14 has not adopted its exact conversion/lifetime contract. Mixed sampler-0/compile-only fallbacks remain deliberately valid pending proposals; no automatic async permission. IR-23/ledger below. |

## 3. Named §G5.3 seams

| Required seam | Consolidated result |
|---|---|
| P5/P6 texture-unit-map split | Current core policy/binding/upload ownership agrees; P14 and pending-request ledgers need migration (IR-04/23) |
| P7 deferred App E rows → P10/P13 | Every row 3–11 has an accepting owner; new owner activation grants remain explicitly gated |
| P3 engine-flag ownership | 15 of 17 flags accepted; oldLighting and separateAo orphaned at P10 (IR-05); settings wire conflicts are IR-24 |
| P3 macro injection ↔ P6 centerDepthSmooth | Coherent Empty/CPU choice; P14 PBO item remains live and separately gated; companion macro policy is a different seam (IR-08) |
| P6 notifier → producer ↔ P7 hooks | Frame/shadow/fog/blend/held/instance paths traced; entity-color milestone, atlas-bind delivery and shadow ID scopes need reconciliation (IR-11/21/22) |
| P4 version invalidation ↔ P12 reload | Counter exists; request mapping, resource-refresh meaning, generation counts and cache observation do not agree (IR-06/07) |

### 3.1 Fixed sampler and physical binding split

**Architecturally matched now.** P5 has the sole spelling/unit/shape authority; its pure resolver is available before runtime/registry/estate construction. P4 validates effective sampler layouts with that policy before GL. P7/P8 carry the same privately authenticated selection/context through snapshot, expected-publication lease, P5 physical binding, P4 activation and P6 upload. P6 only locates exact names and uploads Ready integers using matching layout/context/policy, retaining existing cache and activity-token rules. It never walks physical rows or binds handles. Both gbuffers bands agree; `depthtex1=11`; unit12 is fullscreen-only depthtex2; noisetex15; `shadow=5` iff the effective complete sampler layout directly declares sampler-compatible watershadow, independent of driver location activity and allocation count. Aliases preserve exact source names, same-unit type/object conflicts are diagnosed, greatest compatible custom canonical ordinal wins. Bound alone transfers the lease; suppression performs no binds/activation/flips; partial GL binding failure is BackendFailed, not mutation-free Rejected. Fixed FINAL passthrough is purpose-separated from shader declarations; shadow fixed function uses NONE. No current substantive old-map/four-row migration defect should be reported. Stale ledger labels remain IR-04; P14's removed-map consumption is IR-23.

### 3.2 All seventeen engine flags

P3 §3.1 1433–1468 says it is the complete Appendix F.1 map. There are **17 flags**, with **12** assigned to P7, **2** to P9, **2** to P10, and **1** to P8. P5/P6/P13 are not assigned F.1 flags by this table; they own separate resource/smoothing/noise/companion data. Do not manufacture flag obligations for them merely because they consume `PackConfiguration`.

| P3 key/field | Named owner | Current owner evidence | Disposition |
|---|---|---|---|
| clouds | P7 | §3.5:678, H-CLOUD-01/02 1303; four states, explicit setting priority | Adopted |
| oldHandLight | P9 | §4.11:650–665; §5.2:766; §5.1:750 | Adopted; external setting codec mismatch belongs to P3→P12 seam |
| dynamicHandLight | P9 | §4.11:657–669; §1:170–172; §5.2:766 | Adopted as optional installed-provider suppression; absence is a documented feature-level no-op, not a new dynamic-lights implementation |
| oldLighting | P10 | P10 §1.2:148–150 leaves lighting vanilla-owned; complete §5.2:937–955 has no flag input; exact and alternate document-wide searches found no ownership/effect contract | **Orphan; IR-05** |
| shadowTranslucent | P8 | §3.1:543; §4.8.4:1080–1083 draws translucent terrain conditionally but entity pass1 independently; `ShadowPolicy` field 303 | Adopted |
| underwaterOverlay | P7 | §3.5:680, scoped WATER overlay behavior | Adopted |
| sun | P7 | §3.5:681, suppress only corresponding sky draw | Adopted |
| moon | P7 | §3.5:681, same separated draw rule | Adopted |
| vignette | P7 | §3.5:682, only VIGNETTE pre-event | Adopted |
| backFace.solid | P7 | §3.5:679 and §4.9:1167 onward; layer-scoped cull override/restoration | Adopted |
| backFace.cutout | P7 | Same exact four-field row | Adopted |
| backFace.cutoutMipped | P7 | Same exact four-field row | Adopted |
| backFace.translucent | P7 | Same exact four-field row | Adopted |
| rain.depth | P7 | §3.5:683; H-WEATHER-01:1304 | Adopted |
| beacon.beam.depth | P7 | §3.5:684; H-BEAM-01/02:1270–1271 | Adopted |
| separateAo | P10 | No occurrence or alternative AO effect contract in entire P10; P12 only invalidates bake products for it (969–975,1206), explicitly leaving behavior to P10 | **Orphan; IR-05** |
| frustum.culling | P7 | §3.5:685; H-FRUSTUM-01:1324 explicitly preserves authenticated P8 camera bypass | Adopted |

Additional owned values mentioned in the assignment are accounted for separately: P5 receives buffer/sizing/clear/pass-mipmap data (P3 2653 ↔ P5 §5.3); P6 receives center-depth/smoothing (P3 2653 ↔ P6 1651–1653); P13 receives noise and preliminary companion policy (P3 2648,2653–2654 ↔ P13 1159,1204–1207). They are not missing F.1 rows.

### 3.3 All deferred vertex/texture hook rows

| App E row | Current P7 promise | Recipient adoption and outcome |
|---:|---|---|
| 3 | P7:1358 RenderChunk rebuild and entity-data push/pop | P10 §4.11:801–803 H10-BUILD + H10-BLOCK; §4.4:466–498 exact alias push/pop including fluids, Forge layers, direct synchronous and nested calls. **Adopted, R10 activation gates retained.** |
| 4 | P7:1359 ChunkRenderDispatcher async build context; exact method left to P10 | P10 H10-TASK on worker processTask plus H10-UPLOAD on dispatcher uploadChunk, §4.11:800–814; exact dispatcher/worker descriptors 824–827. §4.4:488–498 establishes transfer/owner/finally and drain, §4.5 rejects stale at queue and execution. **Adopted, not orphaned because helper worker is outside the catalog class.** |
| 5 | P7:1360 BufferBuilder begin/endVertex/addVertexData | P10 H10-BEGIN/END/ARRAY/BULK/SEAL/STATE/RESET §4.11:804–810; whole-record copying, seal and state provenance §4.5:503–530. **Adopted; helper APIs complete the three named methods.** |
| 6 | P7:1361 Tessellator non-VBO route | P10 H10-TESS and H10-LIST-REPLAY §4.11:811,816; client-list distinction §4.6:599–615. **Adopted for immediate and actual VBO-off chunk display lists, not merely immediate arrays.** |
| 7 | P7:1362 WorldVertexBufferUploader pointers | P10 H10-CLIENT §4.11:812, separate LIVE/LIST_CAPTURE and finally restoration; §4.6:599–615 explicitly refuses forcing VBOs as a substitute. **Adopted.** |
| 8 | P7:1363 VertexBuffer upload/draw | P10 H10-VBO-UPLOAD/DRAW and VBO-LAYER §4.11:814–817, stored-stride/epoch upload metadata §4.5:523–530. **Adopted.** |
| 9 | P7:1364 BlockModelRenderer per-block scope | P10 H10-MODEL §4.11:803; §4.4:471–485 preserves nested equal and alternate-state scopes with prior-depth restoration. **Adopted.** |
| 10 | P7:1365 TextureMap lifecycle/atlasSize; prefer Forge stitch events | P13 §1.1:173–174; §3.5:518; §4.6:1062–1065 H13-ATLAS-01/02 Pre/Post, read-only atlas accessors and updateAnimations TAIL supplying post-vanilla snapshot. **Adopted at v0.5; event substitution is exactly allowed.** |
| 11 | P7:1366 TextureAtlasSprite lifecycle, recipient chooses method | P13 §3.5:519; §4.6:1066–1079 H13-SPRITE-01 read-only metadata/frame/tick accessors plus atlas post-tick snapshot. H13-SPRITE-02 per-sprite update injections intentionally dormant. **Adopted; dormancy is deliberate replacement by one atlas snapshot, not a missing animation implementation.** |

P10 preserves primary P7 hook row identities and exports existing ownerPhase=10 subreport vocabulary (§4.11:853–859); P2's generic nested owner schema supports that vocabulary (1053–1066). Runtime injection counts and driver display-list behavior remain honestly unproven implementation evidence, not integration defects.

### 3.4 Center-depth macro decision

**Result: coherent deliberate rejection, not a contradiction.**

- P3's exact contributor algebra is only `Empty` or `DefineCenterDepthSmooth` (1314–1320). Injection is after version/extensions and before restored pack `#line` (1324–1333), and defines an object-like macro. Binding §5.1:2648 incorporates that unchanged placement/algebra.
- P6 §4.8:1054–1076 correctly notes that an expression replacement also rewrites `uniform float centerDepthSmooth;`, and no declaration-aware removal/rename API is granted. It also identifies lack of a reserved sampler unit, differing observable GPU value and prohibited transformer provenance. The selected path is synchronous CPU readback plus tick-domain EMA.
- P6 §5.1:1628–1629 and §5.2:1653 expressly return `MacroContribution.Empty`; P4 §5.3:1845 passes that singular contribution unchanged. P3 §11.4:3972–3973's decision handoff is therefore **adopted**.
- P14 §3.4:442–457 and §5.3:1484–1518 preserve that decision. A3/PBO is **not obviated**. Its sample-age permission is a separate explicit request; until granted PBO_FENCE remains FORCE_OFF and synchronous behavior ships. No contradiction should be raised for the reserved inactive macro, or for that deliberate PBO gate.
- The companion `MC_NORMAL_MAP`/`MC_SPECULAR_MAP` pair is a separate P3 load-time input, not a second contribution bag. Its recent grant still needs downstream adoption; it does not change the center-depth conclusion.

### 3.5 Every notifier row

| Table row (P6 lines1289–1303) | Current producer / disposition |
|---|---|
| frame begin | Adopted P7 H-FRAME-01 §4.3:900–908 and §4.10.2:1228. P6 sampling/previous depth precedes P5 resize/clear. |
| gbuffer matrix capture | Adopted H-FRAME-04:1232 after setupCameraTransform, exactly once; P7§4.3:915–919. P6§4.6:994–1006 makes semantic post-camera requirement decisive. Its residual “at first clear” phrasing is not a requirement to capture known-wrong matrices; D-P7-4 resolves it. |
| celestial rotation | Adopted H-SKY-02:1247 and P7§5.1:1875–1893, using P8's copied formula/sample (§4.5.4:909–921), not a second live GL/matrix read. P8 sends same sample before shadow activation; main-sky may repeat equal values. |
| shadow camera | Adopted P8§4.2 step8:722–727,§4.10:1111–1123; after FF camera, before activation; P6 derives inverses. |
| fog | Adopted H-FOG-01/02:1324–1325 plus P6 frame fallback. Start/end observed but intentionally not transported because FogSample contains only mode/density/color; P7§5.1 explains this. |
| blend | Adopted H-BLEND-01:1323 covers enable/disable and enum/int factor overloads; P6§4.12:1313–1319 overlays effective P4 BlendSpec before uploads, despite lock application after participants. |
| texture bind | Orphan event delivery IR-21; query and stitch invalidation alone do not fulfill bind/current timing. |
| normal/specular change | Explicit future custom/texture-bridge extension, no current built-in consumer. P13 candidates/lifecycle adopted; do not elevate absent future callback into current uniform failure. |
| render phase change | Explicit future custom-extension/G8 signal; no Appendix D renderStage. No matching present callback found in P7/P11 (alternate searches), but no current executable consumer promised. Pending future proposal/non-current surface, not blocking. |
| entity ID | Adopted H9-ENTITY-ID-01:1267 and P9§4.12:677; v0.3, LIFO restoration. Shadow extension incomplete IR-22. |
| block entity ID | Adopted main H9-BLOCK-ENTITY-ID-01:1268 and P9:678; shadow entry has incompatible accepted-main-scope precondition IR-22. |
| entity color | IR-11: P6 promises real P7/v0.1; only current P7 producer is deferred H9-COLOR-01/02 P9/v0.3:1274–1275. |
| instance | Adopted P7§4.6:1050–1054, update i before each instance, restore0, v0.5 loop. |
| held items | Adopted H9-HELD-01:1229/P7§4.3:903–908 and P9§4.12:679. Accepted frame samples once, emits changed tuple with tick identity; no program-switch resampling. |
| atlas size | Orphan bind→sink path IR-21; reload invalidation/Unknown adopted P13§4.4/4.6, but no current exact producer of P6 updateAtlasSize. |

### 3.6 Every reload path and failure transition

Notation: `Rg` = P4 PublishedRegistry.generation; `Pv` = P7 PipelineVersion; `Tx` = P13 publication/resource identity. P4 §5.1:1792 increments Rg per accepted Ready, accepted Off or forced RecoveredOff, NOT per frontend configuration. P7 800–809/847–850 increments Pv once for the final coordinated result. For a normal successful replacement, Rg +1/Pv +1; if Ready is accepted and later texture/estate/ID work fails, compensating Off causes another Rg increment while final Pv increments once. Off/load failure may increment Rg with zero new PackConfiguration. A low-level pre-release rejection changes no Rg and retains caller ownership; P7 nevertheless keeps admission closed and compensates off rather than resuming the old pipeline.

| P12 trigger (945–978) | P12 classification | Actual current receiving route and invalidation | Disposition |
|---|---|---|---|
| IR-06+R | FULL + worldRendererReload | P7 offers Select or RebuildActive intents, but no FULL/discovery/world-rebuild flag mapping. If coordinated replacement runs, normal Rg/Pv/Tx rules apply. | Missing adapter/mandatory refresh semantics, IR-06. |
| /reloadShaders | Same FULL route | Same as keybind; P7 token result differs from P12 void submit. | IR-06; command cannot truthfully report completion through P12's seam. |
| Resource-manager reload / IR-06+T / resource-pack change | NONE + resourceReacquire; no config change | P7 1144 and 2044–2045 REQUIRE P3+P9 refresh, then coherent replacement. P7/P13 advance resource epoch and invalidate atlas metadata, animation, borrowed object identity and atlasSize before vanilla replacement. Rg/Pv may change even if paths/bytes match. | Direct contradiction IR-06. |
| Pack selection → filesystem/internal | FULL + unconditional renderer reload | P7 Select owns load and publication. Rg/Pv/Tx replacement supported; discovery ID and global persisted identity mapping are incompatible/missing. | IR-06/IR-25. |
| Pack selection → Off | FULL + renderer reload | P3 Off short-circuits without a new configuration (P3 3130–3145); P7/P4 accepted Off invalidates tokens/generation, P13 retires. | Off is safe, but disproves config-count/Rg equivalence; IR-07. |
| Option Apply | REPUBLISH; renderer reload iff bake-set changes | P12 write-then-load promises a NEW finalized configuration. P7 binding RebuildActive says reuse manager-owned configuration (2040–2044); no new-option load branch/flag contract is stated, unlike explicit RESOURCE_RELOAD exception. | IR-06/IR-03; no complete accepted route for changed option inputs. |
| Done while dirty | Same as Apply | Same; Done clean produces no request. | IR-06/IR-03. |
| Profile applied | REPUBLISH; conditional bake rebuild | Same reload problem; also P12 invokes nonexistent ProfileModel.infer rather than OptionConfiguration.inferProfile. | IR-06/IR-03. |
| Reset defaults | REPUBLISH; conditional bake rebuild | P3 now supplies catalog-issued default state + safe changed-only codec contract, not caller-constructed empty state. Replacement must reload that write. | IR-06/IR-03. |
| normalMap/specularMap | REPUBLISH + resourceReacquire, no world reload | P13 preliminary state and P7 call use ONLY packActive&&fixedUnitCapabilityAvailable; user values cannot independently disable either macro/atlas. P3 now accepts all four independent typed pairs. | IR-24/IR-08; rebuilding cannot remedy wrong policy input. |
| renderQuality/shadowQuality | REPUBLISH, no additive flags | P5 owns runtime sizing values and generation-sensitive rebuild; P7 observes/resolves actual extent. No owner choice ladders or key-decoding adapter for P12 spellings. | Explicit inert-list proposals remain pending; key mismatch IR-24. |
| handDepth | REPUBLISH | P7 hand behavior exists, but no ordered choice list/current P12 key adoption. P3 macro reads handDepthMul, not handDepth. | Pending list + IR-24. |
| oldHandLight | REPUBLISH | P9 higher-priority user/pack policy exists and its change participates in coherent P7 ID invalidation. P3 rejects P12 wire 'default' for this known Boolean key. | IR-24; typed handoff adopted in principle, wire boundary not joined. |
| oldLighting | REPUBLISH + renderer reload | P10 contains generic lighting/bake lifecycle but no current oldLighting/separateAo semantics. P3 known key rejects 'default'. | IR-24 plus orphan behavior handoff. |
| Navigation/scroll/hover/discard | NONE, no flags | No request, no Rg/Pv/Tx changes. | Matched. |
| Dimension switch (not P12-owned) | Delegated | P7 1123–1130 explicitly demotes/off-publishes before target prepare/publication; independent generations and cache lifetime apply. | Ownership matched; no equality between Rg/Pv/config count assumed. |
| Coalesced mixtures | max lifecycle, union flags, latest diagnostic cause | P7 coalesces matching expected-active identities, supersedes Select intents, retains all reasons, validates nonempty reason/intent relation. No translation from P12 effect algebra exists. | IR-06. |
| Write failure before submission | Retain in-memory edits | No successful write→reload means existing active configuration retained; this is not a failed pipeline rebuild. P3 pre-I/O InvalidRequest/FAILED results must be consumed. | Safety intent matched; codec calls need IR-03 cutover. |
| Preacceptance build/rejection failure | P12 generic failed load/shaders off | Caller closes unaccepted candidates only; old composition is taken off even if low-level P4 Rejected leaves registry usable. Tx owner/registrations retire; no old active fallback. | Safe across P4/P7/P13; IR-07 count rule needs correction. |
| Postacceptance texture/resize/ID failure | P12 generic shaders-off | Accepted resources are publisher-owned; retire Tx, close only unaccepted candidates, P5 Off then P4 Off; full ID geometry invalidation if needed. Rg may advance twice for one attempted config, Pv once. | Safe compensation; IR-07. |

Old/new lifetime result: P4 1621–1680, P7 749–809/1126–1165 and P13 1034–1111 agree that quiescence stops drawing, old texture leases become noncurrent, deletion waits for every lease, borrowed handles are never deleted, new texture resources pair only to actual accepted generations, and no failed rebuild revives the old registry/atlas. P6's CURRENT retirement grant additionally requires all restoration callbacks before retirement and adapters alive through retirement (1560–1596); synchronize those exact distinctions, especially Rejected versus Accepted/RecoveredOff, into stale P7/P13 request prose. No rollback-to-old-resource defect was found in the coordinated current transaction itself.

## 4. Complete §11 adoption ledger

Every originating §11 subsection, numbered request, recipient handoff and decision group is dispositioned below. A local decision with no separate recipient is explicitly non-cross-phase; related decision IDs are grouped only when they share the same disposition. “Adopted” means present in current architecture, **never** verified or implemented. “Pending explicit proposal” preserves an honest gate rather than claiming a hidden existing API. “Orphan” identifies an outgoing obligation without receiving adoption. Optional optimization/future G8 work is not promoted into current required functionality. Repeated restatements of the same request are cross-accounted, not extra findings.

Required current implementation gates still include the genuinely missing producer/consumer contracts listed here: native legacy source preservation and P4 migration; jcpp build/seam permission; U1 authority and typed suffix execution; applicable P2 evidence/API grants; P10 facade/lifecycle/lifetime grants; and the unfinished composition/settings/reload paths in §1. A fallback closes only the scope explicitly permitted by its owning contract. It cannot satisfy required matrix behavior by substituting empty publications or neutral values. Optional R4 allocation savings, async/timing improvements and post-v0.5 G8 extensions retain their recorded fallback/defer dispositions.

### 4.1 Phase 1

#### P1 §11.1–§11.3

| Item | Disposition |
|---|---|
| §11.1 D-P1-1…44 | Phase-local decisions except obligations explicitly indexed in §11.4/§5; every cross-phase obligation is disposed below. D-P1-39/40/41/42/43/44 are specifically tracked as fixed-function/depth/package/replay/package/native-geometry grants. No independent orphan is inferred from historical rationale. |
| §11.2 D-1,2,6,7,8 | Non-cross-phase foundation conventions. |
| §11.2 D-3 and D-10→P2 | Adopted by P2 §1.1/§5.1–5.2 (234–261,1744–1795). |
| §11.2 D-4→P4 | Adopted by P4 §1.1 modern-superset/sparse registry and §5.1:1782–1784. |
| §11.2 D-5→P7,10,13 | Hook ownership adopted by P7 §4.10, P10 §4.11, P13 texture homes/hooks; package-ledger drift separately below. |
| §11.2 D-9→P5+/7 | Adopted as compatibility/fixed-function path in P4/P5/P7 §5; not a new core-profile rewrite. |
| §11.3 item1 extension-citation conflict | Adopted upstream at RC2 DESIGN 992–997. |
| §11.3 items2–7 template/build defects | Non-cross-phase implementation obligations except item2→P12: P12 §1.2:212 and §5.2b:1222 acknowledge Phase1 ownership. |
| §11.3 items8–9 JAVA_8/refmap duties→P7 | **Orphaned as explicit startup duties.** P7 mentions production refmap only in OQ-4 procedure (2619), not the first-config generation check or JAVA_8/Java25 spot-check. See note IR-28; this is not a verdict about actual refmap correctness. |
| §11.3 item10 Gradle mechanism experiment | Non-cross-phase implementation task. |
| §11.3 item11/B11 real state snapshot | Non-cross-phase backend obligation; consumer state restore exists. |
| §11.3 item11/B7 debug symmetry→P14 | Adopted by P14 debug failure/depth-counter/finally design (1650–1700; §3.3:414). |
| §11.3 item11/B10 overload trap | Non-cross-phase facade design rule. |
| §11.3 item11/B6 blend producer→P6/P7 | Adopted: P6:884 and P7 H-BLEND-01:1320 plus UniformSignalBridge:2064. |

#### P1 §11.4 5100–5441

| Handed item | Disposition + recipient evidence |
|---|---|
| P2 module/JUnit/CI/profile/log-stability slots | Adopted, P2 §5.2:1766–1795. |
| P2 no-source/no-images/explicit fail-after-regenerate artifacts | Adopted, P2 §4.11.1:1453–1467, §4.11.5:1544–1549, §11.3:2302–2310. |
| P3 saveSources and complete capability-header input | Adopted, P3 §5.2:3225–3239; exact standard projection 2786–2800. |
| P3 native-preserving source request, including same-build pipeline, retained semantics/constants, attributed final catalogs, route-distinct fingerprints, mixed-form/schema migration | **Pending explicit proposal**, P1:5117–5145 versus P3 None/Translate 1306–1320 and 3075–3081. No native grant or synthetic Available exists. |
| P3 engine-flag ownership map | Adopted as producer, P3 §3.1; downstream oldLighting/separateAo orphan IR-05. |
| P3 vertex-stage countInstances detection | Adopted by the per-program resource record P3:2947–2953 and parser map; no composite-only narrowing imposed. |
| P4 fixed-function grant | Adopted, P4 §5.2:1828–1831; fixed terminal has no participants. |
| P4 native migration item1: ordinary/core vs recognized legacy route | Pending explicit consumer/source amendment; P4 §4.8:1255–1293 retains gated alternatives. |
| P4 native migration item2: typed primitives/count adaptation | Pending consumer amendment, IR-04; operation granted by P1:4380 but absent from P4 §5.2. |
| P4 native migration item3: extension gate and drain/configure/drain before link, cleanup/fallback | Pending consumer amendment plus source grant; do not claim recorder/source support complete. |
| P4 native migration item4: registry identity strategy/count/capability/source inputs | Pending consumer amendment plus source grant. |
| P4 native migration item5: consumed rows/disposition and future fixtures | Pending consumer amendment; factual no-verb ledger is stale, IR-04. Future tests are not evidence presently. |
| P4 same-build materialize call migration | **Unadopted existing API requirement**, IR-03; P4:1160–1163 still supplies OptionState. |
| P7 mixin slots/bail frame integration | Bail/hook ownership adopted through P7 catalog and compatibility integration; refmap/JAVA_8 duties orphaned as above. |
| P8 package grant | Adopted in P8 §2.1:253–269. |
| P7 bootstrap stage2 requirement | Adopted H-BOOT-02:1226. |
| P7 bootstrap stage3 recommendation | Adopted H-BOOT-03:1227 as optional/deferred loading-complete signal. |
| P7 bootstrap stage1 FML deviation | **Mismatch IR-14**: P1:5204–5220 versus P7 H-BOOT-01:1225 (GameSettings RETURN). |
| P7 composite countInstances v0.5 | Adopted P7 §4.6:1049–1051 with updateInstanceId/restore-zero; P6 upload at 885. |
| P7 existing R7-8 package grant | Pending consumer ledger reconciliation: P1:4362/5242–5253 grants; P7:2302 and 2362 still blocks placement. IR-04. |
| P7 replay-aware evidence | Adopted P7 §5.2:2077–2090 and R7-6:2300; no label-based inference. |
| P7 optional frame-boundary GL drain, with elision limitations | **Pending explicit proposal/decision**, not an orphaned mandatory operation: P1:5255–5291 leaves whether/where to P7 and warns it cannot force a query. No force-drain API or fake guarantee found. |
| P7 open gbuffers/shadow countInstances case | Pending explicit decision, not mandated by P1. P7 only designs fullscreen repetition. P4's stronger “both cases” promise creates IR-18 separately. |
| P5 vanilla fixed-unit texture vocabulary/object ownership | Adopted P5 §5.2:2379–2393, sole policy §5.1; P6 resolver consumer §4.9/§5.1 leaves object binding in P5. |
| P5 borrowed depth/combined attachment/first-vs-steady copy | Adopted P5 §5.2:2385–2390. |
| P5 ordinary foreign textures not deleted/reallocated or attached; borrowed depth narrowly distinct | Adopted exact permission row P5:2390 and surrounding depth rows. |
| P6 sampler-pointing rather than object ownership | Adopted P6 §4.9:1078 onward, §5.1:1618, sole P5 resolver. |
| P6 blendFunc notifier and P7 producer | Adopted P6:884 ↔ P7:1320/2064. |
| P10 bail mechanism/policy and plugin veto | Adopted P10 §5.1:877 and §5.2:949–950; no renderer-integration scope invented. |
| P13 R3 exact package homes | Pending consumer ledger reconciliation: P1:4363/5372–5383 grants, P13:1354–1356 still calls engine/glue pending. IR-04. |
| Placement-only boundary and unrelated request inventory | Non-cross-phase scope limit, but **inventory stale**: P3 now grants R1/direct registry projections, P6 grants resolver/retire, P8 grants shared shadow/construction split. Do not infer completion from a package grant; reconcile status-only assertions separately. |
| P13 transfer verbs, texture channel, possible readback request | Adopted functional creation/upload and borrowed/static split in P13 §4.3:790–812/§5; texture-readback extension remains only an invitation, not a missing mandatory API. |
| P13 ForeignTextureProvider live vocabulary/nonownership vs owned companions | Adopted behavior P13:794–811; exact producer binding surface is not a license to delete/overwrite vanilla storage. |
| P12 SHADER_GUI sink | Adopted P12 §4.9:1075–1082 and §5.2b:1220. |
| P12 ModularUI notices/mechanics/missing modImplementation | Adopted as dependency mechanics P12 §5.2b:1222–1225; framework decision remains P12's. |
| P14 DebugService/glLabels implementation and balanced groups | Adopted P14 §3.3:414, failure/threading 1650–1700. |
| P14 additive async readback/compile and synchronous fallback | Adopted architecture; enabling dependencies remain explicit requests P14 §5.3–5.4. PBO still relevant, §4 above. |
| G8/S5 backend-swap drill/coarsening | Pending explicitly future G8/S5 proposal; outside current 14-phase recipient set, not orphaned to a present phase. |
| Candidate license-header lint | Pending explicit optional proposal, no owner/implementation promised. |

#### P1 §11.5 5442–5499

1. RESEARCH OQ-2 pointer to repin/PINS ledger: pending explicit documentation proposal; RESEARCH:1008 retains standing item without that pointer.
2. RESEARCH OQ-12 resolved-by-note: pending explicit documentation proposal; RESEARCH:1018 remains open. No verification judgement inferred.
3. Extension probe citation split: adopted RC2 DESIGN:992–997.
4. Feature-level rung2a: adopted RC2 DESIGN:381–388; P2 current §6 names all six semantic rungs (1878–1882).
5. Closing migration/no-new-request paragraphs: non-cross-phase scope explanation.
6. §0.25 geometry authority interpretation: pending explicit authority clarification only if maintainer intends to prohibit hidden geometry-only backend use; does not grant P3 source preservation or P4 consumer migration.

### 4.2 Phase 2

| Item | Disposition + current counterpart |
|---|---|
| §11.1 D-P2-1…26 | Local harness decisions, with all outgoing process/snapshot/scene/codec obligations enumerated below. D-P2-24/25 current motion-/2 contract is load-bearing, not historical static-shot behavior. |
| §11.2 D-1…10 | Non-cross-phase convention/disposition; downstream test vocabulary is exposed §5.1, no additional hidden interface. |
| §11.3 item1 Sildur acquisition | Pending explicit RESEARCH clarification and implementation-time pin choice, not a phase API. |
| §11.3 items2–4 paths, historical dependency exception, old camera narrowing | Non-cross-phase closed historical records; item3 not used as verification evidence. |
| §11.3 items5–9 images, pins, artifact governance, rate limits, vanilla scene assumptions | Non-cross-phase policy/implementation research. No renderer fix inferred. |
| §11.3 item10 dual-spec cadence | Pending explicit RESEARCH schedule clarification; P2 provisional release gates are stated, not silently changed. |
| §11.4→P3 R5 source-free engine snapshot | **Pending explicit API proposal**, not adopted: P2 §4.11.4:1518–1538/§5.4:1841–1849 versus P3 §5.2:3237–3239 and §8.2:3746–3769, which promise artifact handoffs but publish no inspect/snapshot API or P2 recipient grant. IR-29 note. |
| →P3 R6 folder/zip pure PackSource | Behavior exists in P3 load/discovery, but P2 harness construction/consumption grant is pending with R5; no MC resource manager is required. |
| →P3 R7 stable pack hash agreeing with archive SHA-512 | Pending exact identity bridge. P3 PackIdentity at 2644 is selectedRoot + per-file content hashes, not a published archive SHA-512. P2 owns verified archive provenance (1757); do not equate content/config hash to archive digest. |
| →P3 R8 smoothing constants | Values are published by P3 2947–2953/2974–2984; **pending P2 snapshot projection/adoption** under R5, not missing producer values. |
| →P3 R9 structured diagnostics | P3 supplies structured source-attributed diagnostics and exact load-failure schema; **pending snapshot/API grant and P2 R4B diagnostic-type permission** (P1 §5.3:4430 excludes P2). Source-text-free policy agrees. |
| →P3 eight micro-packs | Adopted intent via P3 §8.2 synthetic grammar fixtures; specific eight-pack acceptance not named. Optional supplied test inputs, not an orphaned runtime dependency. |
| →P4 R10 resolution/source-presence pair | Adopted P4 §5.1:1788 and §11.5:2345–2347. `CHAIN` preserves independent source presence; failed rows carry sanitized detail. P2 request ledger still says request but producer explicitly names adapter consumer. |
| →P5 R10A live resources | Adopted P5 §5.1:2347; complete Available/Unavailable owner projection; P7 captures it directly (§5.2:2148). |
| →P7 R11 after-final-before-present capture | Adopted H-CAPTURE-01:1339 and frame-end §4.6:1068–1070. |
| →P7 R12 readiness | Adopted capture contract/readiness path P7 §4.13:1442 and §5.1 capture rows; no success inferred from mere pack selection. |
| →P7 R13 conditional partialTicks override | Pending explicit conditional proposal, not currently requested/required. |
| →P7 R14 clean shutdown | Adopted H-CAPTURE-02:1340. |
| →P7 R17 replay-aware evidence | Adopted P7 §5.2:2077–2090; copies producer result. |
| →P7 R18 primary/nested frozen hook evidence | Adopted P7 §5.1:2063 and §5.2:2086–2089; no health inference. |
| →P7 R19 capture/manifest /2 motion migration | **Pending explicit required consumer migration with incompatible active /1 contract**, P2:1869–1870 and 2344–2353 ↔ P7:1438–1440/2090. §0.1 establishes detailed P2→P7 finding; current new contract cannot run through old consumer. |
| →P7 P3/P4/P5 evidence aggregation | P4/P5 accepted projections adopted; P3 snapshot boundary remains pending as above. |
| →P12 R15 programmatic settings get/set/validation | Pending explicit proposal; P12 §5.1 publishes GUI edit/settings models, not an adopted headless harness get/set/validation endpoint. |
| →P12 R16 changed-only pack persistence | Behavioral intent adopted (P12:449,914–920) but current codec/settings/target cutover is mismatched with P3; The reload audit covers the detailed finding. |
| →P8 night-shadows scenes | Adopted scene intent, P8 §8.3:1613–1633 includes night caster, movement and water split. No exact new run invented here. |
| →P9 entities-blocks scenes | Adopted scene intent, P9 §8.3:940–949 includes block/meta, hand, entity, TE/color cases. |
| →P10 entities-blocks scenes | Adopted scene intent, P10 §8.2–8.3:1082–1102 includes entities/TE and both draw paths. |
| →P13 weather-rain/water-translucent textures | Adopted motion/normal/noise/animation scene intent, P13 §8.4:1544–1550; exact /2 execution still depends on P7 migration. |
| →implementation registry pins/OQ10 spike | Non-cross-phase implementation research; no results claimed. |
| →OQ10 operator capture every available capability profile | Non-cross-phase future spike duty, not a missing phase producer. |
| §11.5 RESEARCH item1 Sildur acquisition | Pending explicit documentation proposal. |
| §11.5 RESEARCH item2 dual-spec timing | Pending explicit authority clarification. |
| §11.5 DESIGN none / former path+artifact requests | Non-cross-phase closed governance disposition; no current requested phase fix. |

### 4.3 Phase 3

| Item | Disposition + current counterpart |
|---|---|
| §11.1 D-P3-1…60 | Local design decisions; cross-phase projections and grants disposed below. Especially D58/R1, D59/lossless declarations and D60/P4 projections are **producer grants**, not missing producer APIs. |
| §11.2 D-1…10 | Non-cross-phase convention disposition; D10's P2 evidence handoff remains pending exact API R5 as above. |
| §11.3 item1 properties option macro authority | Pending explicit upstream clarification; current A–G/no-option policy is explicit, not an integration contradiction. |
| §11.3 items2–4 confirmation/version/dimension rules | Non-cross-phase parser rulings; P4 dimension consumer matches. |
| §11.3 item5 half-life units→P6 | Adopted P6 D-P6-3:1966 and tick-domain CPU path §4.8; no seconds conversion inferred. |
| §11.3 item6 U1 suffix authority | Pending explicit authority and future typed-state proposal; current lossless capture is a distinct completed producer grant, not suffix support. |
| §11.4 bullet1 P6 macro decision | Adopted P6:1073–1076,1629,1653; Empty, CPU, P14 not obviated. |
| bullet2 P4 declaration merge/P6 no reparse | Adopted P4:1176–1195/1843; P6:1652/1659–1668. |
| bullet3 P4 source/evaluator uses finalized state only | **Materializer unadopted**, IR-03. P4 current §4.7:1160–1163 still passes OptionState. P4 consumes evaluated state but does not specify an alternative raw-model evaluator; no extra unsupported evaluator claim made. |
| bullet4 P4 mipmap/vertices direct projections | Pending consumer amendment, IR-04: P3:2999–3040/3370–3383 grants; P4:1848/1866–1871 still pending. P5/10 values are not to be reverse-routed. |
| bullet5 P7 bounded internal source | Adopted P7 §5.1:2057–2058 and §5.2:2101; canonical path projection recognized R7-9:2303. |
| bullet5 P7 bundle-owned persistence / durable reference / schema16 load arguments | Pending current consumer cutover; P7 §5.2:2096–2105 still carries schema-v4 wording and incomplete old load consumption. Preliminary-pair fallback explicitly invokes unchanged load (751–757,2352–2359), which is no longer a valid non-Off P3 schema16 call. |
| bullet5 P7 v0.1 false pair / v0.5 preliminary pair before load | Pending explicit adoption; producer grant exists, P7/P13 R1 ledgers remain ungranted. No post-analysis or final TexturePlan macro producer permitted. |
| bullet5 P7 pack/dimension/option/reload publication | Adopted P7 §4.1 and §4.8/§5.3 transaction; lifecycle-specific sibling findings remain separate. |
| bullet6 P7 base/override/disabled dimension semantics | Adopted P7:1126–1128 and §5.2:2097; no base merging. |
| bullet7 P9 mapping state/ordinary+forced parse/typed parser | Pending consumer grant-ledger/current-type migration: P9 §5.2:765 still consumes UnresolvedIdMappings; §5.4:821 still requests R9-1. P3:2647/3306–3310 and D23 already grant schema16 input. this review/sibling owns detailed P3→P9 seam. |
| bullet8 P11 customExpressions ordered declarations, duplicates, attribution/no reparse | Adopted P11 §5.2:1011–1035 exact matching algebra and ownership; P11 adds grammar/evaluation, not Properties parsing. |
| bullet9 P12 factory, issued targets, codec results, preview-only updates/fresh reload, tooltip marker | Pending current consumer API cutover; P12 §§5.2/5.4 1187–1295 still requests already-closed option shapes and uses earlier model/codec assumptions. Detailed findings owned by the reload audit. |
| bullet10 P13 typed specs + new lossless declaration stream | Typed existing source specs adopted; lossless stream **not adopted**, P13 §5.2:1204 still states suffix stripping. Unresolved records are not permission to parse suffixes. |
| bullet10 future approved typed sampling and U1 | Pending explicit authority + subsequent producer/consumer changes, not an already-granted rendering feature. |
| bullet11 P13 R1/P7 schema16 grant ledger | Pending consumer reconciliation: P3:3337–3350 versus P13:1341–1348 and P7:2308/2352–2359. Post-analysis R4 remains separate optional allocation proposal. |
| bullet12 G8/S3 OQ7 final identity | Pending explicitly future owner, not a current-phase orphan. |
| bullet13 G8/S2 P3-C19/legacy dimensions | Pending future activation; dormant recognition remains post-v0.5, not contradictory current support. |
| §11.5 item1 jcpp pin/seam | Pending explicit P1 build grant; notices already adopted. |
| §11.5 item2 properties standard/option macro clarification | Pending explicit RC3/RESEARCH authority request. |
| §11.5 item3 U1 exact grammar, precedence, association, source applicability | Pending explicit authority; no consumer can grant it by inventing enums or inferring .mcmeta. |
| §11.5 item4 % tag syntax standardization | Pending explicit authority; current provisional bounded grammar is published. |
| §11.5 item5 edition grammar/comparison ratification | Pending explicit RESEARCH authority; current policy is published provisionally rather than attributed falsely to App F.2. |

### 4.4 Phase 4

| Item | Disposition + recipient evidence |
|---|---|
| §11.1 D-P4-1…19; §11.2 D1…10 | Local registry design/convention disposition; outgoing contract obligations below. |
| §11.3 item1 43 vs60 slots | Closed upstream ruling retained as non-cross-phase history; no cardinality behavior inferred. |
| §11.3 item2 fixed-function facade | Adopted P1:4379 and P4:1828–1831. |
| §11.3 item3 dual-form geometry | Pending complete source/consumer path; P1 native verb grant must replace no-verb premise (IR-04). |
| §11.3 item4 reference per-pass inventory | Non-cross-phase decision to retain owner boundaries, not a demand to copy reference state. |
| §11.3 item5 P12 generation dependency | **Not reconciled**: P12 §5.3:1235–1247 chooses indirect consequence and explicitly does not read the counter; P4 §11.4 still orders polling. IR-07. |
| §11.3 item6 Phase3 projection allocation | Pending consumer amendment; producer already grants direct reads, IR-04. |
| §11.4 P5 candidate.view + symbolic routing + physical sides | Adopted P5 §5.1:2344–2346 and P7 §5.2:2113/2140; candidate snapshot not a live generation. |
| §11.4 P5 unchanged virtual descriptor/applyVirtualTransition | Adopted P5:2351 and P7:1015–1017/1057–1058. |
| §11.4 P6 three participants/effective layout/cache/token/error isolation | Adopted P6 §5.1:1613–1628 and §5.2:1659–1668; no retained bound-access/program handle. |
| §11.4 P7 slots/bands/pass arrays/scale/mipmaps/fixed terminals | Adopted P7 §4.4–4.6 and §5.2:2114–2121. |
| §11.4 P7 both countInstances cases | Fullscreen adopted (1049–1051); gbuffers/shadow **unclaimed**, IR-18. P1 labels that case open, so reconcile rather than declare an unapproved extension mandatory. |
| §11.4 P7 detached view/reacquire publication/quiesce/failure-off | Adopted P7 §4.1:749–819 and §5.3:2235–2268. |
| §11.4 P7 resolution-copy/virtual unchanged/same selection through bind/activate/restoration | Adopted P7 §5.2:2116–2137,§4.6:1029–1058. |
| §11.4 P8 root-shadow/context/shared binding/no resolve | **Adopted in current P8** §§5.2–5.4:1224–1333 and invocation:745–773. P4 §5.4/§11.5 still calls owner adoption absent; consumer-side ledger correction only, not a new grant needed from P8. |
| §11.4 P10 fixed attribute locations | Adopted P10 §5.2:940 (10/11/12 exactly); native vertex service remains separately requested. |
| §11.4 P12 poll generation/discard derived caches | **Unadopted promised dependency**, IR-07; P12:1240–1244 expressly never reads it. |
| §11.4 G8/S1 dormant family population | Pending explicit future activation, not a current 14-phase orphan. |
| §11.4 G8/S2 compute/workgroup/barrier/image semantics | Pending explicit future architecture; placeholders not treated as implementation. |
| §11.4 P14 async contract/sync fallback/render publication | Adopted intent P14 §3.1:386 and §§5.3–5.4; resumable-build/compiler changes remain explicit requests, not a contradiction with present synchronous compile. |
| §11.5 corrected count grant | Non-cross-phase closed upstream record. |
| §11.5 fixed-function grant | Adopted P1→P4 as above. |
| §11.5 candidate-view grant | Adopted P5/P7 as above. |
| §11.5 P6 uniform layout/bound access grant | Adopted P6 as above. |
| §11.5 P2 R10 / P7 R7-4 resolution grant | Adopted P2 request contract:1854–1857 and P7:2116. |
| §11.5 virtual R7-2 | Adopted P5:2351/P7:1015–1017. |
| §11.5 complete legacy geometry | Pending source/consumer completion; native facade half now granted. |
| §11.5 graph correction or indirect P7 generation path | Pending precise owner reconciliation; P12 selects indirect model but P4 retains direct poll requirement (IR-07). |
| §11.5 verification target | Non-cross-phase governance; deliberately not audited. |
| §11.5 coordinated policy/resolver and P13 candidates | Adopted policy architecture P5:2359/P6:1611,1618/P13:1208–1226. |
| §11.5 R7-10/11 P6 still called ungranted | Current P6 §5.1:1611–1614,1618 **adopts** resolver and retirement. P4's pending ledger is stale; dependent wiring still needs synchronization, not a new P6 grant. |
| §11.5 R7-12/13 P8 still called ungranted | Current P8 construction:625–660 and §5.2–5.4 **adopt** both. P4 ledger stale; actual gates elsewhere remain. |
| §11.5 P13 package / preliminary macros / P4 attributes | Producer grants now exist in P1/P3; consumer amendments pending. Suffix/complete geometry remain truly ungranted. Do not lump all under a single outstanding-owner-grant claim. |

Grouped numbered/decision rows below explicitly cover every item in each subsection; local rationale/provenance is non-cross-phase unless identified.

### 4.5 Phase 5

| Subsection/items | Disposition |
|---|---|
| §11.1 D-P5-1…9,11…14,17…21 | Local policy decisions with cross-boundary consequences adopted in P4§5.1/P6§4.9/P7§4.3–4.6/P13§5.1–5.3 as applicable; no independent unassigned handoff. D-P5-9/21 fixed-map adoption explicitly matched above. |
| D-P5-10 | SSAA allocation/execution separation: retained P5 value, **orphan P7 execution** IR-12; authority clarification explicitly pending. |
| D-P5-15 | MainDepthSource preparation adopted P7§4.1:767–770/§4.3:911–914/§5.2:2143; no extent mismatch accepted. |
| D-P5-16,22 | Old four-row binding expressly superseded; current shared protocol adopted P8§4.2/§5.3. Last sentence saying not granted is IR-04. |
| §11.2 D1…D10 | Non-cross-phase governing constraint dispositions; no new handoffs. |
| §11.3 items1–4,8–9 | Non-cross-phase reference/authority rulings; unit11, rebase and real shadow flips reflected in current consumers. |
| §11.3 items5–6 | Adopted upstream P1§5.2:4380–4393. |
| §11.3 item7 | Pending explicit nonblocking authority proposal; no new allocation inference; SSAA recipient gap IR-12 separate. |
| §11.3 items10–11 | Runtime quality/preparation adopted P7§4.1/4.3 and §5.2. |
| §11.3 item12 | P8 migration adopted P8§5.3; stale unadopted wording IR-04. |
| §11.4→P6 | Adopted P6§5.1:1612,§5.2:1681–1699; stale pending label IR-04. |
| §11.4→P7 | Timing/copy/clear/SCREEN/try-finally/runtime quality/prepare/publication/virtual/overlay/resource projection adopted P7§4.1–4.6/§5.2. SSAA alone orphan IR-12; facade route IR-13. |
| §11.4→P8 | All requested selection/binding/finally semantics adopted P8§4.2/§5.3; IR-04 status correction. |
| §11.4→P13 | Adopted P13§5.1–5.3, full domains and post-accepted estate registration. |
| §11.4→P14 | Policy-preserving optimization adopted P14§5.2; its optional H-P14→P5-1 remains explicit unadopted optimization, not buffer contradiction. |
| §11.4→G8/S1/S2 | Pending explicit growth proposal; D3§G8:779–800 records nonbinding provisional slices and real shadow ping-pong. No pre-v0.5 implementation promise. |
| §11.4→final review | Discharged by this integration investigation and this review's final artifact; no code work. |
| §11.5 items1,4 | Adopted P1§5.2 and P4§5.1:1786. |
| §11.5 item2 | Pending explicit nonblocking RESEARCH/DESIGN clarification; R1181 still only says SSAA multiplier. |
| §11.5 item3 | Non-cross-phase historical verification machinery; excluded from this status audit. |
| §11.5 item5 | Shared replacement adopted P8§5.3; stale migration status IR-04. |
| §11.5 items6–7 | Adopted P7§5.4 R7-1/2/3/5 and P2§5.4 R10A. |
| §11.5 item8 | P6 R7-10/11 and P8 R7-12/13 adopted; P3 R1 and P1 P13 R3 now owner-granted; projection grant now P3§5.1:3001–3047. Suffix executable semantics and legacy complete producer/consumer cutover remain explicit pending proposals. Synchronization IR-03/09, not universal closure. |

### 4.6 Phase 6

| Subsection/items | Disposition |
|---|---|
| §11.1 D-P6-1…5,7…14 | Local cadence/cache/neutral/error/math decisions; corresponding frame/shadow main hooks adopted as notifier table. D-P6-6 resolver adopted P5/P7 seam; D-P6-15…17 custom schema/Bool1/counts adopted P11§5.3–5.4. D-P6-18 retirement adopted in owner but P7 inventory and P11 CLOSE need IR-10/09. |
| §11.2 items1–3,7 | Non-cross-phase reference/math rulings. |
| §11.2 item4 | Pending explicit nonblocking DESIGN GPU-candidate clarification, CPU baseline selected. |
| §11.2 items5–6 | Current declaration/access metadata adopted P3/P4§5. |
| §11.2 item8 | Upstream exclusion discrepancy resolved R301–303,1501–1505; stale pending narrative authority-disposition ledger. |
| §11.2 item9 | Adopted recipient P11§5.3–5.4. |
| §11.2 items10–11 | R7-10/11 architecturally adopted, current P5/P7 consumer synchronization outstanding IR-04; P11 operation mismatch IR-10. |
| §11.3→P7 | Sampling/matrices/fog/blend/celestial/instance/three callbacks adopted; entityColor staging IR-11; atlas adapter IR-21; exact resolver/retirement pending consumer migration IR-04. |
| §11.3→P8 | Adopted P8§4.10:1111–1140, primary values before activation, P6 inverses. |
| §11.3→P9 | Adopted P9§5.2/§4.12, LIFO parent then0 restoration; no requirement to zero an outer nested scope prematurely. Shadow-specific extension IR-22. |
| §11.3→P11 | Fixed schema, seven exclusions, ordered typed Bool1 and three counts adopted P11§5.3–5.4; lifecycle mismatch IR-10. |
| §11.3→P13 | Value/query and invalidation adopted, bind delivery orphan IR-21. |
| §11.3→P14 | Baseline/PBO ledger adopted P14§5.3; truthful age extension pending explicitly, synchronous fallback remains mandatory. |
| §11.3→P6 implementers | Non-cross-phase implementation instruction. |
| §11.4 bullet1 | Already adopted upstream in R301–303/1501–1505; authority-disposition ledger. |
| §11.4 bullet2 | Pending explicit nonblocking DESIGN clarification; no rejected GPU implementation promised. |
| §11.4 bullet3 | Current P3/P4 declaration/access grants adopted; review evidence not reassessed. |
| §11.4 bullets4–5 | P5/P7 R7-10 and P7/P4 R7-11 synchronization outstanding, IR-04; retirement API itself present. |
| §11.4 bullet6 | P11 schema/Bool1/absence grant adopted P11§5.4. |
| §11.4 bullet7 | Non-cross-phase verification-manifest history; intentionally not audited. |

### 4.7 Phase 7

| Subsection/items | Disposition |
|---|---|
| §11.1 D-P7-1…12,14–15 | Local orchestration decisions, current P4/P5/P6/P13 endpoints mostly matched; D-P7-12 P9 old-failure narrative is the vertex audit's mismatch. |
| D-P7-13 | P8 adopted pure plan→provider→compile→registry-bound create (§5.1/§4.1); stale R7-13 status IR-04. |
| §11.2 D1…D10 | Non-cross-phase governing dispositions. |
| §11.3 bullet1 | OQ3/OQ4 remain explicitly pending implementation experiments, not missing cross-phase APIs. |
| §11.3 bullet2 | R7-1…7 adopted current P1/P2/P4/P5/P6 endpoints; capture /2 is separate R19. |
| §11.3 bullet3 | R7-8 already P1-granted, IR-04. |
| §11.3 bullet4 | R7-9 owner grant exists P3§5.1 internal source/path; no verification conclusion here. |
| §11.3 bullets5–6 | P8 R7-12/13 and P6 R7-10/11 adopted current owners; synchronize ledgers/consumption IR-04. |
| §11.3 bullet7 | P3 macro R1/P1 texture package/P3 P4 projections now granted at owners; remaining complete legacy/suffix and post-analysis demand are explicit pending. IR-03/09. |
| §11.3 bullet8 | P10/P13 milestone and P9 invalidator gates are deliberate downstream extensions; P10 R10-2 explicitly pending. |
| §11.4 | Cross-reference to local §3.6 rulings, non-new handoff; timing semantics matched, current upstream dispositions below. |
| §11.5 U7-1 | Adopted R1402 (correct world-loop overload); stale open wording authority-disposition ledger. |
| U7-2 | Adopted in requested next candidate D3:1899–1900; not a grant to repin P7 from RC3. authority-disposition ledger disposition correction only. |
| U7-3 | Pending explicit authority correction; R823–825 still claims every class. |
| §11.5 dependency R7-8 and granted1…7/9 | Package now granted P1§5.1; remaining interface grants reflected above. |
| §11.5 accepted R8-1/4/5 | Adopted P8§5.4; P2 R18 now explicitly includes all8 shadow rows (§5.4:1870), so P8's ungranted-P2-half text is stale IR-04. |
| §11.5 R7-10/11 | Adopted P6 owner; consumer inventory/caller migration IR-04 and P11 IR-10. |
| §11.5 R7-12/13 | Adopted P8 owner; IR-04. |
| §11.5 P13 R1/R4 | R1 owner-granted P3§5.1:2642/2786–2803; R4 post-analysis memory optimization remains explicit pending with fallback. |
| §11.5 suffix reconciliation | Partially adopted P3 lossless stream; honoring suffixes/typed sampling remains explicit pending U1. P7 “stripped and ignored” description no longer matches producer schema16 (IR-03). |
| §5.5→P8 | Adopted new binding/planning; deliberate unrelated gates remain. |
| §5.5→P9 | Hooks/publication supplied; P9 old precommit retain-old narrative unresolved (the vertex audit). |
| §5.5→P10 | E3–9/invalidation adopted, R10-2 lifecycle/declaration integration pending explicit proposal; the vertex audit deep-check. |
| §5.5→P11 | Adopted one custom participant P11§5.3/5.5. |
| §5.5→P12 | Not adopted as existing controller consumption; recipient makes explicit different ReloadCoordinator proposal, P12§5.3(B). The reload audit covers. |
| §5.5→P13 | Adopted ten-step protocol P13§5.3. |
| §5.5→P14 | Broad profiling/resize adopted P14§5.4, exact timing/group hooks explicitly pending R-P14→P7-2. Do not imply timing API already exists. |

### 4.8 Phase 8

| Subsection/items | Disposition |
|---|---|
| §11.1 D-P8-1…11 | Local math/traversal/failure/PCF/scope decisions; P5 owns objects, P7 driver owns frame/bridge. D-P8-12/13 adopted shared binding and pure planning match P5/P7 current exact request shapes. |
| §11.2 D1…D10 | Non-cross-phase governing dispositions; no independent handoffs. |
| §11.3 items1–5,9 | Non-cross-phase evidence/algorithm rulings; no extra GL flush promised. |
| §11.3 item6 | Context gap closed P7§5.1:1576–1633,1952–1998. |
| §11.3 item7 | Binding/mipmap gap closed P5§5.1:2356–2359. |
| §11.3 item8 | PCF split adopted P5§5.1/P8§4.9:1089–1108; future wording clarification optional. |
| §11.3 item10 | Planning cycle closed P8§4.1/P7§5.4 R7-13 exact requested shape. |
| §11.3 item11 | Current P6 adoption acknowledged; package/P2-reporting still erroneously called ungranted IR-04. Review status not adjudicated. |
| §11.4→P9 | Orphan shadow-aware-scope handoff IR-22. |
| §11.4→P10 | Adopted P10§1.2:140,§4.6:548–551,599–615, subject explicit R10-2. |
| §11.4→P13 | Adopted P13§5.2 and P7 supplied expected-ID lease source; no P8 ownership leak. |
| §11.4→P14 | Optional profiling proposal within P14 broad JFR/audit scope; no exact second-setup timing output granted; explicit R-P14→P7-2/JFR fallback, not blocker. |
| §11.4→G8/S1 | Pending explicit nonbinding growth proposal D3:779–787; real flips must retain v0.2 split, not current contradiction. |
| §11.5 R8-1/2/4 | Adopted current P7/P5 endpoints. |
| §11.5 R7-12/13, P6 R7-10/11 | Adopted current owner surfaces, pending consumer synchronization IR-04. |
| §11.5 R8-3 | Already granted P1§5.1:4359, IR-04. |
| §11.5 P2 R8-5 half | Already adopted P2§5.4 R18:1870; IR-04. /2 frame capture migration remains IR-02. |
| §11.5 final PCF wording | Pending explicit optional next-DESIGN clarification; existing policy split matches and no RESEARCH change requested. |

### 4.9 Phase 9

| Item | Disposition + current recipient evidence |
|---|---|
| §11.1 D-P9-1 | **Adopted with mismatch:** immutable publication/retirement adopted P7 §5.3:2270–2276; whole-pipeline failure divergence IR-09. |
| D-P9-2 | **Non-cross-phase:** local deterministic merge/precedence decision. P3 preserves syntax/order without choosing winners (2433–2493). |
| D-P9-3 | **Adopted input:** P3 era provenance 2469–2470; catalog contents/validation remain P9-local. Consumer R9-1 status stale. |
| D-P9-4 | **Adopted input:** P3 2457–2473 provides isolated ordinary/forced parses and exact presence distinctions; branch selection remains P9. |
| D-P9-5 | **Adopted split:** P3 2481–2494 publishes unresolved tags; shim is P9-local. Governing-candidate qualification adopted DESIGN v3:2084–2092. |
| D-P9-6 | **Adopted input:** P3 file states 2457–2462 permit absent-only fallback; fallback policy remains P9. |
| D-P9-7 | **Adopted:** P7 H9-COLOR-01/02 1272–1294 capture exact operands and restore; former RETURN-only conflict no longer exists. |
| D-P9-8 | **Adopted core:** P6 held semantics 718–726, P12 override 812–820. Optional external dynamic-light suppression stays explicit compatibility extension. |
| D-P9-9 | **Non-cross-phase:** mod/mod ordering local to P9. |
| D-P9-10 | **Adopted:** P10 §4.1:282–305 and §4.4:466–469 consume exact two words and generation. |
| D-P9-11 | **Adopted:** P7 §5.1:1899–1907 and §5.5:2377 layer/ID invalidation; P10 §4.8:685–697 implements gate. |
| D-P9-12 | **Adopted:** P7:1265 both RenderManager methods and nested ID scope semantics. |
| §11.2 item1 PD tag claim | **Non-cross-phase ruling plus adopted upstream qualification:** DESIGN v3:2084–2092 now qualifies vintage/modern evidence; PD:495–497 remains historical wording. |
| §11.2 item2 magic fallback | **Non-cross-phase:** local reference rejection, no producer API change. |
| §11.2 item3 P7 entityColor ownership | **Adopted:** exact operand hook now P7:1272–1294; P9 text is stale history if read as current absence. IR-04. |
| §11.2 item4 no-reopen/11300 | **Adopted:** P3 2433–2473 safe alternate and public pure parser; P9 must reconcile absent-grant wording. IR-04. |
| §11.3 bullet1 R9-1/R9-2 hard gates | **Architecturally adopted; current verification pending §0.1's gate.** P3:2647/3304–3306; P7:194–205/2270–2276. Do not demand repeat grants or infer PASS. |
| §11.3 bullet2 P10 stale scheduling | **Adopted:** P10 §4.5:503–530 and §4.8:669–697 define cancellation/discard/rebuild and scheduling-only Completed; bit semantics unchanged. |
| §11.3 bullet3 LegacyTagCatalog dataset | **Non-cross-phase implementation data item:** P9 owns pack/live-registry derivation; defined unknown-tag fallback 561–566. No recipient orphan. |
| §11.3 bullet4 P12 oldHandLight | **Adopted:** P12 §4.6.2:812–820 explicit higher-priority ON/OFF, DEFAULT delegates pack; wire values 858; §11:1691. |
| §11.3 bullet5 external dynamic-light adapter | **Non-cross-phase optional extension:** no other phase promised its implementation; absence explicitly does not reduce identity conformance. |
| §11.4 bullet1 apply R9-1/schema | **Adopted upstream, consumer stale:** P3 2647/3304–3306; IR-04. |
| §11.4 bullet2 apply R9-2 | **Adopted upstream, current transaction divergence:** P7 1265–1294/2235–2276; IR-09/IR-04. |
| §11.4 bullet3 qualify PD summary in next design candidate | **Adopted in candidate:** DESIGN v3:2084–2092 (also MOVES version entry 122). P9 deliberately remains RC3, so this is not authority migration and does not justify rewriting historical PD. |

P9 §5.5 extra handoff to P2 (832–835): entity/held scene machinery and generic hook evidence are adopted (P2:1752,2025,2255,1037–1066). Catalog/schema/ID-generation fingerprint manifest fields are **not adopted** by P2's closed current block list (929–973,1025–1066). Track as explicit pending evidence-schema proposal, not runtime correctness contradiction.

### 4.10 Phase 10

| Item | Disposition + recipient evidence |
|---|---|
| §11.1 D-P10-1 | **Adopted existing inputs/local design:** P4 table 1797 and P9 payload 613–633; format design itself P10-local. |
| D-P10-2 | **Non-cross-phase reference ruling:** numeric handedness corrected locally; upstream correction remains §11.5 proposal. |
| D-P10-3 | **Non-cross-phase rejected optimization:** preserves separation from P9 per-draw uniforms; no alias-valued constant promised. |
| D-P10-4 | **Non-cross-phase:** lazy allocation/TLS posture. |
| D-P10-5 | **Non-cross-phase implementation ownership:** task/per-builder stacks; shared lifetime tightening is R10-2/3 pending. |
| D-P10-6 | **Adopted current owner policy:** P7 §5.3:2235–2267 failures off/all generations invalidated; P9 reconciliation remains IR-09/R10-3. |
| D-P10-7 | **Non-cross-phase design with pending R10-2 input:** live versus list union plan fully designed; P7 does not yet grant lifecycle/declaration sinks. |
| D-P10-8 | **Adopted mechanism / explicit new policy:** P1 3968–4008 owns Ok/Degrade/Bail and session bail; P10 owns detector. P7 outcome translation IR-19 remains. |
| D-P10-9 | **Pending explicit proposals:** R10-1 facade/package and R10-2 adapters absent from current recipients; no false consumption claim. |
| D-P10-10 | **Non-cross-phase governance:** authoring exception not verification; this review determines present gate. |
| §11.2 item1 tangent cross-order claim | **Pending explicit upstream correction:** DESIGN v3:2185–2189 and PD:570–573 still say matching formulas. No re-mining of reference source; report only current requested-text status. |
| §11.2 item2 celeritas/distance-read anchors | **Pending explicit upstream correction:** DESIGN v3:2238–2241 and PD:577–580 retain old list/zeroes wording. |
| §11.2 item3 “2 used” | **Pending explicit upstream wording clarification:** RESEARCH App C:1296 versus 1300–1301; P9→P10 actual payload is internally aligned. |
| §11.2 item4 P9 publication old prose | **Actual mismatch, already explicitly flagged R10-3:** IR-09, P9:802–815 versus P7:2235–2267. |
| §11.2 item5 P7 compatibility outcomes | **Actual mismatch, already explicitly flagged:** IR-19, P7:1398–1403 versus P1:3968–3993. |
| §11.2 item6 vertex package/facade | **Pending explicit R10-1:** existing P1 §5 facade inventory 4375–4395 does not grant VertexInputService; no direct-GL workaround assumed. |
| §11.2 item7 retired governance | **Non-cross-phase governance / explicit documentation proposal:** MOVES:95–102 supplies retired-profile interpretation; no operational validation belongs in this audit. |
| §11.2 item8 current dependency verification | **Non-cross-phase governance:** §0.1's current verification gate, not inferred from historical PASS. |
| §11.3 bullet1 OQ-5/OQ-14 | **Non-cross-phase pending experiment:** no architectural implementation claim. |
| §11.3 bullet2 R10-1…3 / R10-4 | **Pending explicit owner proposals:** R10-1 P1; R10-2 P7; R10-3 P9; R10-4 P3/P4 only future G8/S4. Modern gating is deliberate, not blocking current classic semantic coverage. |
| §11.3 bullet3 injection counts/toolchain/runtime anchors | **Non-cross-phase implementation evidence gate:** correctly not guessed from mapping existence. |
| §11.3 bullet4 numeric/T2 discrepancies | **Non-cross-phase local decision + conditional evidence escalation:** P2 RUN-T2 exists 1325; no current discrepancy claimed. |
| §11.3 bullet5 zero-cost wording | **Non-cross-phase qualification:** no literal zero-cycle promise. |
| §11.4 P7 | **Part adopted, part explicit proposal:** existing invalidator taken from P7:1695–1710,1899–1907; lifecycle/declaration sinks still R10-2. |
| §11.4 P9 | **Exact payload adopted; lifetime/transaction tightening pending R10-3:** P9:410–413,613–633 versus P10:488–498,669–697. |
| §11.4 P13 tangent frame | **Adopted ownership boundary:** P13 §1.2:182 assigns tangent frame to P10 and handles only texture bytes. No new sampler/atlas interface needed. |
| §11.4 P2 | **Part adopted; explicit remaining evidence proposal:** P2 moving scenes 1320,1752 and nested hook schema 1053–1066 adopted; no closed wire field for vertex/ID epoch or named mandatory VBO-on/off matrix in current run catalog 1314–1327. No invented conformance success. |
| §11.4 G8/S4 | **Pending explicit future proposal:** P4 current table remains three names 1797; modern scanner/binder cutover is R10-4, deliberately post-v0.5. |
| §11.5 R10-1…4 restatement | **Pending explicit owner proposals, same dispositions as above.** |
| §11.5 tangent equivalence | **Pending explicit documentation correction**, DESIGN/PD endpoints as §11.2 item1. |
| §11.5 detection anchor/distance read | **Pending explicit documentation correction**, endpoints as §11.2 item2. |
| §11.5 App C “2 used” | **Pending explicit clarification**, endpoints as §11.2 item3. |
| §11.5 harness/adoption prose | **Non-cross-phase governance proposal**, MOVES/overlay already provide interpretation; no need to elevate preserved historical revisions into a phase API contradiction. |

### 4.11 Phase 11

| Item | Disposition + recipient evidence |
|---|---|
| §11.1 D-P11-1/2/3/4 | **Non-cross-phase:** clean-room/license, reference-shape, arithmetic and symbol policy respectively. Each is local evaluator policy, no additional recipient grant. |
| D-P11-5 | **Adopted:** P6 definition-order upload semantics 1435–1478 and P3 occurrence ordering 2814–2825. |
| D-P11-6 | **Adopted:** P6 accepted-prefix versus invalid-counter behavior 1458–1478; expression error isolation remains P11. |
| D-P11-7 | **Non-cross-phase:** smooth algorithm/state is evaluator-local. |
| D-P11-8 | **Owner mismatch on later escalation:** P11 §10 to P14 versus P14 L-11:1777 returning ownership to P11; IR-15. Interpreter fallback remains valid. |
| D-P11-9 | **Adopted behavior:** P6 schema excludes same seven names 1401–1421; DESIGN summary correction still pending, no phase-to-phase exclusion mismatch. |
| D-P11-10 | **Adopted:** P6 Bool1/SkippedAbsent 1367–1381,1443–1458. |
| D-P11-11/12/13 | **Non-cross-phase:** transactional smooth/random ordering, key identity and language semantic-version/local typing decisions. No recipient grant assumed. |
| §11.2 item1 five-name DESIGN restatement | **Pending explicit upstream correction:** DESIGN v3:2301–2303 still lists five; P6/P11 executable seven-name surfaces match. |
| §11.2 item2 PD full function checklist | **Non-cross-phase provenance ruling:** P11 independently owns full language; no cross-phase implementation mismatch. No forbidden source re-mining performed. |
| §11.2 item3 stareval license | **Non-cross-phase:** clean-room fallback resolves the feature, no request for library reuse. |
| §11.2 item4 verification state | **Non-cross-phase governance:** this review determines live gate; stale historical preflight wording is not adopted verification. |
| §11.3 mod.glue/later composition | **Part adopted, remainder explicit unadopted handoff:** P7:409–410,2379 accepts one P6 custom participant; no current P7 contract names ExpressionContextProvider, controller activation/reset map or fresh RandomSource. P11 §5.5 explicitly does not assume P7 internals. Track as pending composition adoption, with actual removed-CLOSE mismatch IR-10 separately. |
| §11.3 P2 scripted/matrix/error/function/smooth golden runs | **Orphan at recipient, explicit outgoing handoff:** P2 catalog 1314–1327 has front-end goldens, tiers and motion, not evaluator scripted/golden execution. Its §5.3:1800–1814 requires a request/fix-up for missing named runs. Generic mp-properties parsing/custom runtime error evidence is not evaluator-function coverage. IR-20. |
| §11.3 P12 immutable load diagnostics | **Orphan route at recipient:** P12 only renders P1 SHADER_GUI store 1075–1100/1220; P11 closed channel is CHAT_AND_LOG|LOG_ONLY 769–789 and §5.5:1073 only asks chat/log forwarding. No current controller/diagnostic projection adoption in P7/P12. IR-17. |
| §11.3 P14 OQ-22 method | **Conflicting handoff/orphan:** P11 explicitly assigns measurement and conditional compiler to P14 (188,1229,1240–1267,1314); P14 L-11:1777 explicitly excludes it and assigns P11. IR-15. |
| §11.4 item1 DESIGN exclusions correction | **Pending explicit upstream proposal:** DESIGN v3:2301–2303 remains five-name list; current P6/P11 seven-name agreement means no new dependency fix required. |

### 4.12 Phase 12

| §11 item | Disposition and recipient evidence |
|---|---|
| §11.1 D-P12-1…19 | Phase-local decisions, not separate hand-offs. Cross-phase-bearing D3/D7/D8/D10/D11/D12/D16/D17/D19 are covered by IR-06/07/24/08/16/25/03 and reload matrix; D14 consumes existing P1 dependency/notice contract (4437–4438). |
| §11.2 binding decisions | Non-cross-phase governance statement. |
| §11.3.1 tooltip provenance | Non-phase upstream correction proposal; DESIGN still says tooltips at 2398–2399. No source re-verification performed. |
| §11.3.2 P3 verification history | Current gate decision belongs to this review; no historical header used to infer it. Semantic resynchronization required by current P3 §5 irrespective of gate history. |
| §11.3.3 sliders | Pending explicit DESIGN proposal: v3 978 versus 2426–2427 still differ. No forbidden Oculus read. |
| §11.3.4 engine-option count | Non-cross-phase upstream note, but current P3 antialiasingLevel contract now makes actual P3/P12 mismatch IR-24 distinct from historical RESEARCH count. |
| §11.4 P7 ReloadCoordinator/drain/RS1/RS2 | Coordinator unadopted proposal; safe boundary/off behavior adopted by P7 749–809/1132–1140; exact queue and counter mismatch IR-06/07. |
| §11.4 P7 ordered handDepth list | Pending explicit proposal; no list in current P7 §5. Inert fallback is deliberate. |
| §11.4 P4 counter | ADOPTED: P4 §5.1 1792. P12 request closure/count semantics need IR-07, not a new counter. |
| §11.4 P5 renderQuality list | Pending explicit proposal; P5 publishes runtime scalar sizing (2344/2405–2407), not an ordered GUI ladder. Inert fallback deliberate. |
| §11.4 P8 shadowQuality list | Pending explicit proposal; P8 current §5.1 1198–1223 publishes shadow policy/plan, not a GUI ladder. Inert fallback deliberate. |
| §11.4 P9 oldHandLight | ADOPTED behavioral precedence at P9 652–663 and §5 750/834; exact P12 string→typed policy adapter is not closed and P3 wire rejects default (IR-24). |
| §11.4 P10 oldLighting/separateAo bake behavior | ORPHAN: P10 §5 860–878 supplies vertex lifecycle, not these two behavior policies. Whole-document alternate lighting searches found only generic bake/lighting-cache transition (696–698), not named flags. Distinguish generic rebake support from semantic adoption. |
| §11.4 P13 normal/spec toggles | Promised but mismatched, IR-08. P13's current preliminary producer has no user input. |
| §11.4 P13 resourceReacquire | Resource epoch invalidation adopted at P13 1084–1111 and P7 1156–1165; P12 flag/request itself unadopted and texture-only classification contradicted, IR-06/IR-06. |
| §11.4 P2 presentation-model goldens | Pending explicit new-family proposal: P12 1489–1499 defines intended content; P2 §5 1754–1755 currently exposes parser/resource goldens, not presentation family. P2 R15/R16 1870–1874 separately requests options setters/validation/roundtrip. Do not call RUN-OPTIONS-ROUNDTRIP adoption of a new screen-manifest schema. |
| §11.4 P3 request1(a) executable model/codec shapes | ADOPTED owner-side: P3 §2.2 819–855 and §5 2649–2650/2656/2670–2730. P12 consumer update IR-03. |
| §11.4 P3 request1(b) profile order/constraints | ADOPTED current profile list/constraints (819–845) and incorporated §5 shape; P12 still claims absent. |
| §11.4 P3 request1(c) catalog iteration | ADOPTED owner-issued catalog and deterministic ordering via incorporated §5 2649/2698–2704; obsolete P12 fallback must defer to producer. |
| §11.4 P3 request1(d) option projection | ADOPTED exact OptionDefinition/state/decorations surface in §5 2649 and incorporated §2.2; P12 must rederive types/closed outcomes. |
| §11.4 P3 request2 persisted option merge point | ADOPTED load-integrated safe persistence path: P3 2679–2725 and 3134–3145. P12 write-before-reload ordering remains right, exact receiver/target/call shape needs migration. |
| §11.4 P3 re-derivation duty | Still actionable consumer synchronization; current gate owned by this review. |
| §11.4 P7 assumption check/fix-up trigger | Completed by this audit: safe boundary/dimension/off exist; request/classification do not match. IR-06/07. |
| §11.4 final integration checklist | Covered here by all-reload matrix and P12→P7 extra edge. |
| §11.5.1 DESIGN slider evidence | Pending explicit upstream proposal; present v3 conflict cited above. |
| §11.5.2 DESIGN tooltip wording | Pending explicit upstream prose proposal, current 2398–2399. |
| §11.5.3 RESEARCH eight-option cross-reference | Pending non-phase upstream clarification, RESEARCH 608–611. |

### 4.13 Phase 13

| §11 item | Disposition and recipient evidence |
|---|---|
| §11.1 D-P13-1…19 | Phase-local decisions. D2/D11 expressly historical/superseded; do not resurrect narrow unit domain. Cross-phase decisions D6/D10/D12/D13/D15–19 resolved by edges and requests below. D5/C-TX01 remains deliberate authority/byte-order uncertainty, not a new integration contradiction. |
| §11.2 | Non-cross-phase binding-decision statement. |
| §11.3 suffix/old domain/status/C-TX01 | U1 owner-side boundary changed (P3 2837–2894), execution semantics still gated; old narrow-domain problem superseded by P5 full domain; status belongs this review; C-TX01 remains DESIGN v3 1088 unresolved. |
| §11.4 P4 metadata/select capability | ADOPTED: P4 1787/1795–1796; P13 exact consumption 1206–1210. |
| §11.4 P5 sole policy/selection/binding | ADOPTED: P5 2360–2363; matched shared-unit edge. |
| §11.4 P6 integer adapter | ADOPTED owner-side: P6 1612/1618/1680–1687; P13 still calls ungranted. |
| §11.4 P7 owner tuple/orchestration | ADOPTED: P7 811–819/2230–2268. Atlas bind event delivery remains orphaned as described above. |
| §11.4 P13 source/lease retirement | Non-cross-phase own responsibility, exposed to P5/P7 and matched by current protocols. |
| §11.4 P14 later transfer optimization | ADOPTED scope only, P14 §1 175–208/§5.5 1566–1612. Async stageable operation not granted; explicit synchronous fallback. |
| §11.4 P8 binding/construction | ADOPTED owner-side: P8 1198–1223/1350–1399. Existing fresh-review/other-owner gates remain separate. |
| §11.4 unrelated P4 legacy/attribute, P3 review, package gates | Do not infer closure from texture transaction. Package R3 now granted; verification is §0.1's gate. Other P4 grant synchronization is owned by the front-end audit. |
| §11.5.1 U1 P3/DESIGN | PARTIALLY ADOPTED: P3 lossless declaration capture and same-boundary future migration (2837–2894). Pending explicit authority proposal for grammar/precedence/typed sampling-state handoff; current owner forbids downstream reparsing and does not claim honored suffix execution. |
| §11.5.2 R1 P3 companion macros | ADOPTED: P3 2642/2705–2712/2759–2778. False ungranted status is IR-04; independent toggle input still IR-08. |
| §11.5.3 R4 P3 unused-companion optimization | Pending explicit proposal; no CompanionMapRequirement in current ResourceRequirements publication. Building both under existing fallback is deliberate accepted cost, not contradictory missing optimization. |
| §11.5.4 R2 P5 full domain | ADOPTED architecturally: 2360–2363; no implementation verification claimed. |
| §11.5.5 R3 P1 packages | ADOPTED: P1 4363 and 5370–5390. |
| §11.5.6 R7-10 P6 resolver | ADOPTED: P6 1612/1618/1680–1698. |
| §11.5.7 R7-11 P6 retirement | ADOPTED: P6 1613–1614, final-use ordering 1560–1596. Current old/new-service refinements must be consumed, not just enum names. |
| §11.5.8 R7-12 P8 shared shadow | ADOPTED: P8 1208/1350–1384 exact same selection/context/publication/lease and Bound-only closure. |
| §11.5.9 R7-13 P8 planning cycle | ADOPTED: P8 1200–1223/1385; registry removed from pure plan, final registry supplied to create. |
| §11.5.10 all-four-owner review duty | Non-design verification handoff to this review; no header inference made. |

### 4.14 Phase 14

| §11 item | Disposition and recipient evidence |
|---|---|
| §11.1 D-P14-1…18 | Local optimization decisions; no independent owner grant. D1/D3–4/D8/D10–11/D14/D18 touch seams covered by edges and requests. |
| §11.2 C1 old absence of P13 | Historical premise is not current design evidence. Current P13 §5 exists; P14 reconciliation is owed (IR-23). |
| §11.2 C2 governing revisions | Non-cross-phase governance note; each phase §0 respected, no substitute v3 authority imposed. |
| §11.2 C3 DebugService gate | Pending explicit owner-change proposal, P1 3165 still debug-context+flag, P14 1622/2128 uses capability+flag. No owner adoption. Its refusal can disable A5; do not say P1 silently grants corrected semantics. |
| §11.2 C4 wave/dependency | Pending DESIGN schedule clarification; current v3 647 versus 2516 still requires P13→P14. §0.1 establishes gate handling. |
| §11.2 C5 timing | Existing promised-but-unexposed seam, IR-26. |
| §11.2 C6 DSA RESEARCH row | Pending non-phase upstream addition; RESEARCH 765–778 still no DSA row. |
| §11.3 binding decisions/CPU conditional | Non-cross-phase framing, matched P6 1073–1076 keeps CPU/sync and PBO row live. |
| §11.4.1 DESIGN wave | Pending explicit proposal, not adopted. |
| §11.4.2 DESIGN reading estimate | Non-cross-phase editorial proposal; current v3 2586 retains its estimate. No historical size assertion independently endorsed. |
| §11.4.3 contingent stall gate | Pending explicit proposal; DESIGN 2583–2584 remains unconditional while async row is gated. |
| §11.4.4 DESIGN debug gate | Pending explicit proposal; P1 request below remains required. |
| §11.4.5 RESEARCH DSA | Pending explicit proposal, as C6. |
| §11.4.6 OQ22 write-back | Non-cross-phase implementation handoff; RESEARCH OQ22 remains open. No results fabricated. |
| §11.4.7 PD fence/readback wording | Pending non-phase prose proposal; PD 748 still 'No PBO/async readback anywhere'. No new third-party mining. |
| §11.4.8 P5 stale trailer | Historical verification-status request; not accepted as current state. this review must decide current gate after coordinated rebuild, not old PASS. |
| §11.4.9 P5 simplification | Pending optional H-P14→P5-1 below. |
| §11.4.10 P6 stale status | Historical verification-status request; §0.1 establishes actual current gate, not Review24 alone. |
| §11.4.11 R-P14→P6-1 sample age | Pending explicit proposal: Sample(float) at P6 657–660 and no uncontracted queue at 1838–1839. P14 synchronous fallback is correct, intentional. |
| §11.4.12 R-P14→P7-1 resumability | Pending explicit proposal; current P7 ten-step synchronous composition 2230–2268 has no submit/poll suspension. Inline is deliberate fallback; related P4 request orphan IR-27. |
| §11.4.12 R-P14→P7-2 groups | Pending explicit call-site proposal; P1 interface exists but does not supply P7 call sites. |
| §11.4.12 R-P14→P7-2 timing | Pending explicit proposal against existing unfulfilled P7 scope promise, IR-26. |
| §11.4.13 R-P14→P1-1 debug gate | Pending explicit proposal; P1 3161–3166 unchanged. |
| §11.4.13 R-P14→P1-2 placement | Pending explicit P14 grant: P1 closed package table grants engine.gl to P1; new P13 grant does not grant P14. |
| §11.4.13 R-P14→P1-3 TextureParameters | Pending explicit exact-type publication; current P1 has setParameters signature at 3117 but no declared field set. P13 TextureParameterSpec is not automatically that facade type. |
| §11.4.13 R-P14→P1-4(a) recorder threading | Pending explicit proposal; P1 4485–4486 single/render-thread recording and explicit request requirement. Worker bypass fallback is intentional. |
| §11.4.13 R-P14→P1-4(b) glContext flag | Pending explicit namespace proposal; P1 §5.3 4433 reserves four flags, not this one. |
| §11.4.13 R-P14→P1-4(c) scoped debug group | Pending optional proposal; P1 3161–3166 exposes push/pop only; manual balance fallback remains. |
| §11.4 MOVES paragraph | Non-cross-phase adoption-record bookkeeping; no change requested. |
| §11.5 H→P5-1 sampler-tier mipmap simplification | Pending optional proposal. P5 2358–2359 still publishes Degraded/Neutralized base-filter restoration behavior; NONE fallback must preserve it. Not an implementation contradiction. |
| §11.5 H→P13-1 / R→P13-1 stageable upload | Pending explicit proposal: P13 §5.1 Ready/Failed build exposes no submit/poll/allocated-unfilled state; P14 compile-only fallback intentional. |
| §11.5 H→P13-1 / R→P13-2 filter value | Owner supplies derivable min/mag/wrap+fingerprint (P13 1158, 880–900). Exact P14 adapter and facade TextureParameters grant not adopted; mixed sampler-0 fallback intentional. This is partial satisfaction, not zero current contract and not automatic full adoption. |
| §5.5 R→P13-3 inventory (incorporated by H→P13-1) | Same pending conversion covers noise/custom forms; no separate async permission or second request. |
| §11.5 H→ALL-1 allocation findings | Non-cross-phase future measurement-routing process; no measured finding to adopt. |
| §11.5 H→ALL-2 redundant-state findings | Same; no present defect implied. |
| §11.5 H→G8-1 feasibility evidence | ADOPTED scope/evidence in DESIGN G8/S2 788–793; no current-phase feature grant. |
| §11.5 H→G8-2 reuse modernization plan | Pending future G8 design proposal; current G8/S2 scope names capability gates but does not adopt this exact record. |
| §11.5 H→IMPL-1 OQ15 spike/allowlist | Non-cross-phase implementation handoff; RESEARCH OQ15 remains open. |
| §11.5 H→IMPL-2 OQ22 write-back | Non-cross-phase implementation handoff; no outcomes claimed. |
| §11.5 H→P2-1 S-CD-1 scene | Pending explicit scene proposal: P2 §5.1 1752 has current six-family /2 scene set, no S-CD-1 adoption. Existing moving paths are not adoption of this specific scene. |
| §11.5 H→REVIEW-1 integration | Fulfilled by this edge matrix and P13 proposal dispositions. |
| §11.6.1 unspecified parameters | Pending P1 proposal, as above. |
| §11.6.2 nonexistent P13 interfaces | Obsolete active statement; current §5 exists, IR-23. |
| §11.6.3 provisional P7 | Gate belongs this review; semantic reconciliation of actual ten-step transaction is required regardless. |
| §11.6.4 DSA driver proof | Non-cross-phase runtime evidence obligation; no tests executed. |
| §11.6.5 K3 threshold judgement | Non-cross-phase spike criterion, not upstream promise. |
| §11.6.6 unread RESEARCH appendices | Historical input-scope disclosure; no current integration adoption implied. |

Additional P14 §5.1 consumers not in its §11 ledger: GlModernizationPlan as P2 run-manifest environment fact is unadopted in current P2 §5/schema contract; Phase12 display and optional GlModernizationPolicy GUI are also unadopted optional surfaces, not contradictions with the seven pack-setting rows. Route exact schema/display additions before assuming them. P13 §5.5 future handle-free plan/diagnostic fixture adapters likewise have no bespoke P2 adapter contract yet; current P2 scene families and fixture policy are available, but are not evidence that every texture-specific projection has been adopted.

## 5. Verdict and required next action

# PASS-WITH-CORRECTIONS

**Counts: blocking=1; corrections=24; notes=4.**

**Implementation clearance: withheld.** IR-01 is the unmet review-eligibility gate; the correction findings additionally prevent treating the current sibling contracts as one coherent implementation specification. This is the verdict for the provisional current-input audit, not a statement that §G5.3's all-verified prerequisite was satisfied. The defects have identified owner-local or coordinated contract repairs; this review does not order wholesale reconstruction of any phase, and it does not recycle P13's historical FAIL as a new verdict on its rebuilt bytes.

The next work is a **fresh fix-up session under §G1.3**, not implementation:

1. Resolve current core interface mismatches: source/schema/setting/identity boundaries, reload/coalescing/generation and compensation rules, removed lifecycle calls, and shadow/atlas producer gaps. Preserve the coherent shared-unit transaction; do not weaken it to fit obsolete consumers.
2. Accept or explicitly disposition orphaned ownership/evidence handoffs and update receiving §5 contracts. Reconcile owner-designed grants separately from recipient adoption and fresh verification. Keep genuinely optional/future proposals explicitly gated; do not silently drop required contract behavior.
3. Record each finding's resolution in this review under a future `## Resolutions` section. Notes may be left with recorded reasons. No resolutions are claimed by the reviewing session itself.
4. Re-verify every phase whose §5 or incorporated consumer contract changes, and complete missing initial reviews. A local retained PASS does not certify a newly changed dependency; follow the source/consumer contracts and their explicit adoption triggers.
5. Rerun the complete integration audit against fourteen verified, fixed input documents, including every dependency edge, all six named seams and every remaining §11 request. Only resolved integration findings plus the individual gates permit implementation under §G5.3.

Historical reviews, quotes, addenda and governing revision pins remain unchanged. Authority corrections requested in §11 require their own recorded maintainer decision; this report grants none.

## 6. Verification of this deliverable

Documentary verification, not runtime testing: the report's input and coverage inventory was computed from the current repository; all 14 phase documents contain the 42 audited mandatory sections; the graph contains 36 hard edges plus one soft edge; the report accounts for each edge, all 17 engine flags, all 9 deferred App E rows 3–11, every notifier/reload row, and all 14 originating §11 ledgers. Findings carry both-endpoint evidence or an explicit whole-recipient absence check.

A programmatic report check validates finding IDs/counts, complete edge/phase coverage, referenced repository paths and explicit line-range bounds. SHA-256 comparison checks that the 14 phase inputs and 11 latest individual review files are unchanged from audit start. Artifact readback is checked against the validated report content. These checks validate review coverage and preservation, **not** the correctness of the proposed shader engine or per-phase PASS status. No Gradle build, test, client, GL experiment or retired verification tool was invoked: none could exercise these unimplemented architecture contracts. No temporary script/file was created; the only authored repository artifact is this report.

## Appendix A. Audited input sections and fingerprints

Ranges refer to the phase document in the same row. SHA-256 identifies the exact input bytes; a later amendment requires re-grounding affected findings. The required §1/§5/§11 reading set totals 6,684 lines across 42 sections. Incorporated declarations and receiving-owner sections were additionally followed as cited above.

| Phase | Input | Complete mandatory ranges | SHA-256 |
|---|---|---|---|
| 1 | `docs/phase1/v14/PHASE_1_DOC.md` | §1 1530–1585; §5 4335–4446; §11 4916–5499 | `8053a6f4c9945a4dbf0edf5615704bcedac4f7b8d623e116fd042dafbb108305` |
| 2 | `docs/phase2/v2/PHASE_2_DOC.md` | §1 234–292; §5 1744–1877; §11 2232–2384 | `4c5d4b80ce4bf2ef58203466b48094a4c99a326265564398bc271e830a2fa71c` |
| 3 | `docs/phase3/v1/PHASE_3_DOC.md` | §1 443–499; §5 2630–3383; §11 3864–4053 | `22942b98efd7c712a1b701ebcf013810eb1048a58f0164355e2d7bbfdd108ad1` |
| 4 | `docs/phase4/v1/PHASE_4_DOC.md` | §1 331–380; §5 1776–1918; §11 2224–2364 | `7bb0bf0214e0d41b704b46b6e1eea58ba4edeaf1a0e42dee142ba0156f87be51` |
| 5 | `docs/phase5/v1/PHASE_5_DOC.md` | §1 361–427; §5 2338–2519; §11 2784–2922 | `6b88fc6fe1a94b4b2ed3e61cc33ce76a56c81159835f1d2c4f35304413a4fd80` |
| 6 | `docs/phase6/v1/PHASE_6_DOC.md` | §1 306–362; §5 1606–1752; §11 1958–2102 | `a265d35a1a6998372905c81d3d8f4c2edcbb3c94d10c309522da1eb895c5e057` |
| 7 | `docs/phase7/v1/PHASE_7_DOC.md` | §1 359–429; §5 1461–2385; §11 2647–2747 | `9ed594d1626d4b36ad7c5d62d281ab633ff0bf000c7beacfa03635b064c16867` |
| 8 | `docs/phase8/v1/PHASE_8_DOC.md` | §1 179–245; §5 1196–1452; §11 1681–1786 | `a4c937281a9dacf17eed5f9919b0d1b576e0dd703d18198f9fdc80c91f90b009` |
| 9 | `docs/phase9/v1/PHASE_9_DOC.md` | §1 127–191; §5 736–838; §11 989–1051 | `135454c7a555ab8f38592d0d3f66702deb9ab269dbe15f151e92a951cbf03d00` |
| 10 | `docs/phase10/v1/PHASE_10_DOC.md` | §1 116–151; §5 860–997; §11 1245–1332 | `a8eca1e17d5cf773231d35bfc857af5fa28f4a70af1a62d80d911c0c1a816f60` |
| 11 | `docs/phase11/v1/PHASE_11_DOC.md` | §1 156–211; §5 985–1078; §11 1271–1324 | `d3207c4b1aa154ae9bd2f27e10e9fe3a7acc62da282129a8d0a047d8f5c1f178` |
| 12 | `docs/phase12/v1/PHASE_12_DOC.md` | §1 180–223; §5 1168–1325; §11 1606–1732 | `0bbf28460611b4ca1e8cfec85b0926d8c678cfd59075e20935cc2c49f65d1aa5` |
| 13 | `docs/phase13/v1/PHASE_13_DOC.md` | §1 154–204; §5 1137–1428; §11 1595–1672 | `ab1973d38901337fd5a382d6e6a62b8bc0bc11413a20f210d558b74486e5fa1b` |
| 14 | `docs/phase14/v1/PHASE_14_DOC.md` | §1 163–253; §5 1445–1639; §11 2101–2354 | `a0ac95dde89d09d768b8f13c9e6401d3785752bd9a09d348c8b880fc89cfd325` |

## Resolutions

### Commission, scope and disposition vocabulary

The maintainer commissioned systematic remediation of every finding **except IR-01**.
This fix-up changes the fourteen current phase documents, not the shader implementation.
The original audit above, its counts/verdict, prior reviews and authority documents remain
historical evidence. IR-01's finding text and implementation-clearance restriction are unchanged.

**Remediated** below means the identified architecture mismatch has an owner/receiver repair,
not that code exists or a phase has passed fresh verification. **Partial** identifies remaining
required behavior or authority decisions that this session could not honestly supply.
**Dispositioned** records the permitted note outcome without pretending an optional API or
future experiment has run. The result is **21 remediated, 4 partial, 3 dispositioned** across
IR-02–IR-29. Every changed §5 or incorporated contract remains unverified.

P3's setting domains, defaults and macro meaning changed under IR-24. Its mandatory schema
discipline therefore required **schema 17**, including the nested ID schema. The earlier
schema-16 catalog/materialization, companion-input, lossless-declaration and safe-persistence
grants are retained in schema 17; there is no inferred upgrade or old-schema fallback.

Current citation key for this section (section anchors, **not** the original audit's line numbers):

| Key | Amended document | Fix-up notice |
|---|---|---|
| P1 | `docs/phase1/v14/PHASE_1_DOC.md` | §0.26 |
| P2 | `docs/phase2/v2/PHASE_2_DOC.md` | §0.38 |
| P3 | `docs/phase3/v1/PHASE_3_DOC.md` | §0.58 |
| P4 | `docs/phase4/v1/PHASE_4_DOC.md` | §0.36 |
| P5 | `docs/phase5/v1/PHASE_5_DOC.md` | §0.40 |
| P6 | `docs/phase6/v1/PHASE_6_DOC.md` | §0.25 |
| P7 | `docs/phase7/v1/PHASE_7_DOC.md` | §0.41 |
| P8 | `docs/phase8/v1/PHASE_8_DOC.md` | §0.9 |
| P9 | `docs/phase9/v1/PHASE_9_DOC.md` | integration fix-up in §0 |
| P10 | `docs/phase10/v1/PHASE_10_DOC.md` | integration fix-up in §0 |
| P11 | `docs/phase11/v1/PHASE_11_DOC.md` | integration fix-up in §0 |
| P12 | `docs/phase12/v1/PHASE_12_DOC.md` | integration fix-up in §0 |
| P13 | `docs/phase13/v1/PHASE_13_DOC.md` | integration fix-up in §0 |
| P14 | `docs/phase14/v1/PHASE_14_DOC.md` | §0.4 |

### IR-02 — Remediated: capture protocol

P7 §4.13/§5.1 consumes only P2's `/2` plan and manifest contracts: dense samples,
first-pose warm-up, actual current/previous frame poses, final-before-present capture and
exact serialization/provenance. P2 §5.4 R19/§11.4 records receiver adoption. `/1` remains
historical, not a compatibility reader. P2's hook-evidence case now copies the current P7
catalog, including H-RESOURCE-01, without rewriting historical reports.

### IR-03 — Partial: current configuration cutover; locale and Internal options gated

P4 §§4.7/5.3 now materializes the containing configuration without an `OptionState`
argument and consumes direct P3 mipmap/vertex projections. Sparse resource rows use P3's
empty projection baseline and the selected effective dimension, not a null dereference or
synthetic row. P5–P13 adopt the relevant current schema-17 projections, same-build macros,
ID input and lossless texture declarations. P12 §§4.1–4.6/5.2 rederive catalog-issued
previews, profile inference and exact safe-codec outcomes rather than retaining old APIs.

Re-derivation exposed a real missing owner surface: current P3 publishes one `LangDecorations`,
not locale-indexed data. P3 §§5.4/11.4 and P12 §§4.3.5/5.4/11 record **R-P12-5** for a
future single-authority locale-map replacement, including normalization/collision, missing/
empty, selection and fingerprint rules. Current decoration data is usable, but the requested
locale→en_us chain is not falsely claimed. P3 also supplies no Internal pack-option persistence
or transient override target: P12/P7 keep Internal pack mutations inert/rejected while global
settings remain available. These are explicit remaining contract limitations, not hidden parsers.

### IR-04 — Remediated: grant ledgers and actual consumption

P1/P3 owner handoffs and P4/P5/P7/P8/P9/P12/P13 active consumers distinguish
**owner-designed/unverified**, **receiver-adopted/unverified** and genuinely missing grants.
P4 §§4.8/5.2 consumes P1's exact legacy pre-link configure operation, capability gate and
drain/configure/drain failure transaction; P6 resolver/retirement and P8 shared-binding/
registry-independent planning grants are no longer universally described as absent.
Native source preservation, jcpp permission, U1 typed suffix execution and optional future
proposals remain separate gates. Ledger repair does not grant those features.

### IR-05 — Remediated: lighting and AO ownership

P10 §§4.8.1/5 explicitly owns immutable `ShaderLightingPolicy`, directional shading and
AO-to-alpha behavior, user-over-pack oldLighting precedence, bake identity and full
quiescent invalidation. P7 §5.1 and P12 §§4.6/4.7 accept the pair and effective-change
predicate. The P10 reload handoff is **REPUBLISH**, with renderer reload only when effective
bake inputs change, plus P7's post-load OR of any newly discovered required invalidation.
Unspecified fallback values/face factors are labeled local D-P10-11 compatibility decisions;
OQ-14 still owes concrete Forge bake-adapter and parity evidence. No renderer-performance
rewrite is authorized.

### IR-06 — Remediated: reload adapter and pre-destructive resource gate

P7 §5.1 adopts P12's exact NONE/REPUBLISH/FULL maximum plus independent OR flags,
selection/profile freezing, safe persistence, programmatic option bridge and final receipts.
FULL rediscovers; REPUBLISH loads the same authenticated selection; resource NONE retains
the identical configuration while refreshing quiescent resources and resource-derived IDs.
P12 §§4.7/5.3 and P13 §§4.7/5.3 reciprocally adopt these distinctions.

Endpoint verification caught a remaining ordering hole: a resource listener that only queues
work cannot quiesce before vanilla replacement. P7 §4.8.1/§4.10.7/§5.1 now publishes
**H-RESOURCE-01 / `ResourceReloadBoundary.invoke(Runnable original)`**, wrapping
`SimpleReloadableResourceManager.func_110541_a(Ljava/util/List;)V` before packs are released.
It drains prior uses, retires acquisition authority and advances the epoch before original;
listeners contribute pending data/effects, outer completion publishes once, nested bodies share
the gate, and exceptions preserve the original failure while recovery converges off.
P12 §4.8.3/§7 and P13 §4.7 distinguish notification from the gate. MCP confirmed the method
descriptor and release/load/listener order; actual weaving/call-site safety remains OQ-4
implementation evidence, not a result of this documentation task.

### IR-07 — Remediated: generation accounting and cache invalidation

P4 §5.1/§11.4 and P7/P12 §5 distinguish one drained request/final outcome from publisher
mutations. A rejected pre-release attempt changes no P4 generation; accepted Ready then
compensating Off can change it twice. Explicit Off/RecoveredOff use actual owner results.
P7's generation listener and ordered receipt notify genuinely generation-derived caches
before further use; failed delivery leaves them unavailable until authoritative polling.
Configuration-keyed presentation state is not falsely made generation-keyed.

### IR-08 — Remediated: independent companion preferences

P13 §§4.1.1/4.1.6/5.1 computes each preliminary bit independently from active pack,
fixed-unit capability and its decoded user preference. P7 §§4.1/5.3 passes the required
P3 pair before jcpp; P12 §4.6 uses the same settings. Disabled kinds allocate/discover/
upload/animate nothing and cannot reuse stale bindings; P5 owns neutral binding/suppression.
Optional linked-demand allocation savings remain R4-gated and cannot change macros.
P13 §4.8's memory estimate now follows this policy instead of claiming ungranted savings.

### IR-09 — Remediated: ID publication and borrowed lifetime

P9 §§4.1/5.3/6 adopts texture-before-ID publication and P7's failure-to-off composition.
Its pure candidate builder still does not mutate publication; that local rule no longer
promises recovery of the old whole pipeline. P9/P10 §5 retain matched lookup/ordinal-map
storage through worker draining, and admit the new tuple only after required geometry
invalidation. R9-1/R9-2/R10-3 adoption is recorded as unverified.

### IR-10 — Remediated: uniform retirement

P11 §4.12/§5 retains its own terminal controller CLOSE but maps P6 disposal only to
UNPUBLISHED_ABORT, REPLACEMENT or SHUTDOWN. P7/P8 lifecycle handoffs consume P6 §4.14's
reason-specific ordering: final restoration first; replacement after actual old-barrier
invalidation; shutdown before atomic P4 teardown. Rejected retirement retains services and
closed admission. No P6 CLOSE alias or invented `UniformRuntime.close()` was restored.

### IR-11 — Remediated: notifier timing and real color milestone

P6 §§4.6/4.12/5 and P7's hook catalog separate pre-clear frame sampling from later
post-camera current-matrix capture. P7 owns real hurt/flash color at **v0.1**, preserving
stable H9-COLOR-01/02 catalog spellings; P9's aliases/held/ID scopes remain v0.3.
P9 §§4.13/5.2 agrees on exact operands, immediate active upload and nested restoration.
Neutral missing-producer behavior is degraded bring-up, not a new deferral of required color.

### IR-12 — Partial: unsupported supersampling promise removed

P5 §§4.11.1/5.1/11.5 and P7 §§4.6/11 now name the actual
`BufferSizing.superSamplingLevel()` metadata; there is no invented `SupersamplingPlan`
or accepted sample/draw schedule. RESEARCH's “SSAA multiplier” does not settle its no-AA
boundary or execution semantics. P5 D-P5-23 explicitly requests scope, level domain,
sizing/rounding, sample/camera/uniform cadence, coverage, accumulation/final resolve,
depth/flip effects, cleanup and owner/milestone. **Level>1 execution and conformance remain
unresolved.** No guessed algorithm, extent multiplication, silent normalization or unapproved
runtime rejection policy was substituted.

### IR-13 — Remediated: anaglyph state boundary

P7 §5.1 `FrameRenderPort` and its render-state prose use mod-side cache-coherent
`GlStateManager` color masking/restoration, consistent with P5. Engine code does not call
a nonexistent P1 facade verb or introduce a native GL bypass.

### IR-14 — Remediated: bootstrap sequence

P1 §§5.1/11.4 and P7 §4.10.2 adopt preInit/FMLLoadCompleteEvent prerequisites, not a
redundant GameSettings CORE injection. H-BOOT-01 reports actual loader-event delivery,
not fabricated injection success. GL-ready RETURN remains required; the menu signal remains
recommended/deferable. Historical hook evidence is preserved.

### IR-15 — Remediated: expression measurement recipient

P11 §§5.1/10.1/11.3 and P14 §5.8/§7.5 L-11/§10.2 accept the exact OQ-22 method and
metrics handoff. P14 owns measurement/decision and a conditional candidate experiment;
P11 retains semantics and its private backend SPI. Interpreter cleanup and a demonstrated
real-pack budget miss precede any compiled-backend experiment. No measurement or compiled
evaluator is claimed.

### IR-16 — Remediated: screen column resolution

P12 §4.3.3–§4.3.4/§5.2 consumes P3's resolver:
`max(explicitColumns.orElse(2), ceil(expandedSlotCount/9))`. All expanded slots count,
including empty/profile/subscreen entries, with the adopted star-expansion rules.
Thus 19 expanded slots with configured columns=1 resolves to 3, not 1.
Presentation and planned behavioral cases no longer retain unconditional explicit override.

### IR-17 — Remediated: direct source-free expression diagnostics

P11 §§4.9.1/5.5.1 publishes `ExpressionDiagnosticGuiSnapshot` with pack/configuration
fingerprints, attempt serial, final ACCEPTED/REJECTED outcome and immutable safe entries.
P7 §5.1 publishes `ExpressionDiagnosticGuiSource.current()` after final composition;
P12 §§4.9/5.3 consumes it on presentation/refresh. Selection/Off/shutdown clear its lifetime;
raw expressions, paths/spans/attribution/dependency chains do not cross this projection.
P11's chat/log channels are unchanged; no fourth channel or speculative P1 conversion exists.

### IR-18 — Partial: non-fullscreen countInstances remains authority-open

P1 §11.4, P4 §§4.9/11.4–11.5 and P7 §§4.6/11 remove the claim that both cases already
have an accepting executor. The existing fullscreen loop stays specified; gbuffers/shadow
re-rendering remains an explicit authority/owner/milestone request covering traversal
side effects, authenticated admission, instance IDs, restoration and failures.
Retained metadata neither supplies that behavior nor authorizes instanced draws or a new
extension API. No whole-phase rebuild was substituted for this missing decision.

### IR-19 — Remediated: actual compatibility outcomes

P7 §4.12 consumes P1's `BailRegistry.evaluate(CompatContext)`, `CompatEvaluation` and
`CompatVerdict.Ok/Degrade/Bail` at owner-approved evaluation points, including session Bail.
P10 §11.2 now records adoption rather than repeating the old missing-adapter assertion.
Degrade preserves its actual restriction; it does not grant renderer-backend replacement.

### IR-20 — Remediated: evaluator-specific conformance contract

P11 §5.6 publishes the original immutable vectors, scripted provider/random inputs,
closed activation/refresh/reset/close steps and expected observable outcomes.
P2 §§4.9/5.1/5.3 adopts **RUN-EXPRESSION-CONFORMANCE** using the existing compiler,
controller and P6 bridge, with per-case PASS/FAIL/UNSUPPORTED results.
Original vectors are distinct from local matrix runs; a matrix run without an independent
oracle records a disposition, not a golden verdict. No real-pack run was performed here.

### IR-21 — Remediated: authenticated current atlas producer

P7 §5.1, P13 §§4.4/5.5 and P6 §§4.12/5 connect actual bind/restoration evidence through
an opaque pipeline/resource-epoch/serial credential to P13 Known/Unknown size and the existing
P6 `updateAtlasSize(Int2)` sink. Unknown/nonatlas/reset maps to `(0,0)`; stale evidence
cannot mutate the sink. Active consumers upload immediately; inactive consumers retain the
current value. P5 remains the physical binder, and P8 preserves the same adapter in shadow
scope. Stitch-time availability alone is not binding evidence.

### IR-22 — Remediated: shadow ID admission

P7 §5.1, P8 §§4.8.2/5/11.4 and P9 §§4.12/5.1 admit IDs through either an accepted main
scope or separately authenticated current shadow execution. Opaque admission/tokens enforce
generation/thread validity and exact LIFO restoration. Shadow tile entities require no
gbuffers_block scope, main snapshot or main program activation. Existing Forge pass ordering
and failure neutralization remain intact.

### IR-23 — Remediated: modernization owner cutover

P14 §§3.1/4.1.2–4.1.4/4.3.4/5.2–5.5 consumes P5 sole policy/physical binding,
Bound-only transferred lease closure, all eight resize reasons, P6's shared resolver,
P13's exact parameter domains/conversion and lease-drained lifetime, and P7's current
ten-step construction/compensation points. Active old-map/absent-interface/eighteen-step
claims were removed. Missing complete P1 parameter conversion retains sampler 0; unadopted
batching, staging, resumability and sample-age extensions retain their synchronous baseline.

### IR-24 — Partial: canonical settings adopted; authority ratification remains

P3 §§4.4/4.10/5.1/D-P3-61 and P12 §4.6 publish one vocabulary:
normalMapEnabled, specularMapEnabled, renderResMul, shadowResMul, handDepthMul,
oldHandLight, oldLighting, antialiasingLevel. Defaults are
`true,true,1.0,1.0,0.125,default,default,0`.
Old-light wire values are exact `default|true|false`; explicit user values win at runtime,
DEFAULT delegates to P9/P10 pack/default policy. Companion preferences remain independent.
No old GUI key spellings are introduced as aliases.

The pre-load policy independently emits MC_OLD_* only for explicit user true; DEFAULT and
false omit it, but remain fingerprint-distinct. This avoids a post-preprocessing pack-policy
cycle. AA is reserved at exact zero and MC_FXAA_LEVEL is absent: no AA control/feature is added.
These are explicit phase-local compatibility decisions, **not OQ-7 ratification**. P3 §11.5
item 6 retains the authority question about macro/default semantics and RESEARCH's AA inventory
versus no-AA boundary. Cross-phase vocabulary is reconciled; that authority decision remains open.

### IR-25 — Remediated: durable restart selection

P12 §4.6.3/§5.2 and P7 §5.1 persist the owner-defined
`FilesystemCandidateReference`, resolve it against fresh discovery, and handle
Resolved/Missing/Ambiguous/KindChanged/InvalidSnapshot before safe target acquisition.
Off/Internal retain distinct sentinels. Display labels are presentation only; no candidate-ID
serialization, first-collision selection or consumer-chosen path sanitization survives.

### IR-26 — Dispositioned: timing promise narrowed

P7 §§1.2/11.5 and P14 §5.4/D-P14-21 reciprocally accept JFR attribution rather than
claiming a public elapsed-per-pass API. Internal timing and P5-owned resize notices are not
misrepresented as that API. Optional timing records/debug groups remain separate proposals
and do not block the synchronous rendering baseline.

### IR-27 — Dispositioned: explicit compiler request received, not enabled

P14 §5.9/§11.4 publishes R-P14→P4-1 and P4 §5.7/§11.4 receives it explicitly as pending:
prepare/worker/link ownership, cancellation/late-result cleanup, visibility and render-thread
candidate/publication rules must precede async adoption. P7 resumability is a separate matched
dependency. Current synchronous compile/Inline remains the only callable baseline; OQ-15
evidence and an exact adopted split are still required before enabling the optimization.

### IR-28 — Dispositioned: first-hook duties explicitly accepted

P7 §11.3/§12 accepts P1's first-real-configuration refmap-generation/artifact check and
JAVA_8-versus-Java-25 Mixin compatibility check at the first hook gate. Broader OQ-4 wording
no longer substitutes for those named duties. This records future implementation work,
not a build, refmap or bytecode result.

### IR-29 — Remediated: complete source-free inspection and provenance route

P3 §5.1.1 publishes `inspect(PackLoadRequest)` with Off/Failed/Inspected outcomes,
same-build configuration, recursively allowlisted source-free snapshot and optional archive
SHA-512 obtained from the same bounded immutable archive read used for decoding.
P1 §§4.9.4/5.3 grants diagnostic consumption without detail/argument leakage.
P4 §5.6 enriches the exact inspected configuration/build-request association with detached
resolution rows, without inventing a view fingerprint accessor or publishing a live registry.

P5 §§2.2/5.1 now exposes `BufferArchitectures.create()` for the existing stateless pure
planner. P2 §4.11.4/§5.4 combines **P3 + P4 + P5**, including exact runtime sizing inputs
and final capability projection, without live GL or buffer creation. Available SHORTFALL
can be complete evidence; Unavailable cannot. P2 independently verifies fixture archive
provenance and requires equality with the inspection digest; configuration/content hashes
are not archive hashes. Partial synthetic goldens remain distinct from complete matrix verdicts.

### Remaining decisions and verification boundary

The four partial findings above are deliberately not marked closed:

1. **IR-03:** P3 locale-indexed publication/selection and Internal pack-option mutation
   contracts remain absent; receiver behavior is explicitly limited until owner amendments.
2. **IR-12:** SSAA level>1 scope and execution semantics require an authority decision.
3. **IR-18:** non-fullscreen repetition still needs an accepting owner and traversal contract.
4. **IR-24:** OQ-7 macro/default ratification and no-AA inventory reconciliation remain open.

Unrelated already-recorded gates also remain: native-preserving legacy source completion,
jcpp build/pin/seam permission, U1 typed suffix execution, P10 concrete facade/lifecycle/bake
adapters, optional GUI choice ladders and P14 optimization extensions. They were not silently
granted by this fix-up. Local choices and retained metadata are not conformance evidence.

Six phase-owned editing slices worked concurrently with explicit cross-owner contracts, followed
by three read-only endpoint reviews covering every IR-02–IR-29 finding. Verification caught and
repaired the lighting FULL/REPUBLISH mismatch, sparse projection null path, nonexistent SSAA
accessor, stale modernization map ownership, and missing pre-destructive resource gate.
Main additionally reconciled P10's stale compatibility ledger and P13's misleading allocation
estimate. The lifecycle reviewer re-read the new gate and its P12/P13 endpoints and found no
remaining actionable mismatch in that slice. These checks are scoped fix-up verification,
**not** fresh whole-phase reviews or a rerun of the original all-edge eligibility audit.

Programmatic documentary checks cover all 28 resolution IDs, all 42 mandatory §1/§5/§11
sections across fourteen phase documents, balanced fenced blocks, unique phase decision-table
IDs, resolution-path existence and preservation of the original audit text. SHA-256 checks
preserve the research document, the three governing design revisions and the eleven latest
individual review files; IR-01's body is unchanged. No Gradle build, test suite, client,
rendering experiment or retired verification tool was run: the amended contracts have no
repository implementation to exercise. The two MCP method lookups establish mappings only.
All fourteen amended documents still require the applicable fresh verification before
implementation consumption; this session does not attempt to remediate or waive IR-01.

### 2026-09-07 follow-on — IR-03, IR-12, IR-18 and IR-24

This is a **dated follow-on disposition**, not an alteration of the original audit or the
preceding partial-resolution records. Those records describe their then-current bytes.
Only these four partial findings were reopened. IR-01, historical review verdicts and
whole-phase eligibility remain untouched. The work changes documentary contracts, not the
template implementation; no phase PASS or implementation-consumption clearance is granted.

Owner §5 surfaces and their incorporated semantics/§11 ledgers were reconciled under each
phase's own header authority: P1 RC2, P2 v3, P3–9 RC3 and P10–14 v3. Research and all three
governing design files remain unchanged. Where evidence did not select policy, the maintainer
made the following explicit choices **after** the bounded evidence was reported:

| Finding | Maintainer choice on 2026-09-07 | Current disposition |
|---|---|---|
| IR-03 | Internal pack options are session-only, survive selection changes, restart at defaults | **Remediated architecturally**; locale publication and authenticated mutation/load contracts now exist |
| IR-12 | `superSamplingLevel` is pack-option/source compatibility only; remove engine SSAA requirement | **Remediated by approved scope correction**, not by implementing or claiming SSAA |
| IR-18 | Repeat each prepared native geometry submission adjacently, N total IDs0…N−1, at v0.5 | **Remediated architecturally**; ownership, admission, ordering and side effects are bound |
| IR-24 | Ratify effective-mode old-light macros and user→pack→true fallback; retain eight keys and zero-only AA reservation | **Remediated for this finding**; broader OQ-7 renderer identity/feature experiment is not ratified |

#### IR-03 follow-on — one locale authority and session-only Internal options

`docs/phase3/v1/PHASE_3_DOC.md` §§2.2/4.3/4.10/5.1/5.1.1/5.3/11
(D-P3-63/65) publishes schema18:

- `OptionConfiguration.localizedDecorations(): Map<String,LangDecorations>` replaces the
  single `lang()` component with no alias. One bounded immutable load acquires all locales.
  Canonical locale/path ordering, deterministic whole-file collision winner, strict UTF-8/
  Properties escapes, all nine maps, absent versus present-empty distinctions, per-key
  requested→en_us→P12 fallback and locale-only cache invalidation are explicit.
- The complete catalog participates in configuration fingerprints and the existing recursive
  source-free inspection. String leaves use `DecisionValue.TextHash(String sha256)` of exact
  UTF-8 bytes, including locale keys and empty translations; no translated pack text or
  invented text-length field enters committed goldens.
- `PackLoadRequest` takes required non-null `Optional<InternalOptionSnapshot>` immediately
  after `internalPackSource`. `OptionCatalog.captureInternalOptions` issues an opaque
  same-bundle/exact-PackIdentity token with complete values and closed failures. Load
  authenticates it and rebinds into a fresh catalog before preprocessing. Filesystem input
  is empty; no fake candidate, path, `PackOptionsTarget` or serialized token exists.

`docs/phase7/v1/PHASE_7_DOC.md` §§4.1/5.1/5.2/11 and
`docs/phase12/v1/PHASE_12_DOC.md` §§4.3–4.7/5.2–5.4/8/11 adopt the exact
`InternalOptionCommitter(expected,preview,globals,effects)` result/lifetime contract.
Validate before I/O; changed globals write first, then queue/session preference acceptance.
`SESSION_ACCEPTED` is distinct from file `COMMITTED` and final pipeline success. Capture/
global-write failure leaves the prior preference; accepted-load failure retains it for retry
but never silently retries defaults. Off/filesystem switches retain the session map; shutdown/
bundle replacement clears it. Profiles/reset/discard and coalescing use the same path.

`docs/phase2/v2/PHASE_2_DOC.md` §§4.3.3/4.11.4/5.4/8/11 adopts inspection and
the executable option bridge without relaxing complete **P3+P4+P5** matrix-golden or registry-only
capture provenance requirements. P12 owns final display fallback, not another language parser.
R-P12-5 and the missing Internal mutation contract are fulfilled architecturally, unverified.

#### IR-12 follow-on — approved removal of engine SSAA semantics

`docs/phase5/v1/PHASE_5_DOC.md` §§4.11.1/5/8/11.6 (D-P5-26/27) records the
independent evidence and its limits: shipped G6 author documentation's unexplained constant
row; the pinned official OptiFine author document; licensed Pintonium/Iris option recognition.
These establish spelling/option compatibility, **not** an engine-level domain, N versus N²
samples, jitter, accumulation or resolve. No decompile implementation or generic SSAA
assumption fills that gap. Exact source paths, revisions, URLs and licensing limits are in §11.6.

The maintainer explicitly removed the old engine interpretation. P5 now publishes only
`BufferSizing(Extent2i mainExtent,Optional<Extent2i> shadowExtent)`; P3 publishes only
`WorldRenderConstants(float sunPathRotation,float ambientOcclusionLevel)`. No sampling
accessor, structural-equality member, positive-level engine constraint or forced level1 remains.
Eligible pack options and pack-authored source use still work under ordinary option rules.
P7 §4.6 and P2 §4.11.4 consume this cutover: unchanged ordinary extent/quality, frame/history,
shadow cadence, depth-copy/flip/mipmap/final and `/2` capture scheduling. The option name
alone adds no engine draw/allocation/resolve or AA UI/runtime. Historical App A.3/RC3 wording
is preserved, with the approved authority correction recorded in §11 rather than silently edited.

#### IR-18 follow-on — prepared submissions, not repeated world traversal

`docs/phase1/v14/PHASE_1_DOC.md` §5/D-P1-47,
`docs/phase4/v1/PHASE_4_DOC.md` §5/§11.5/D-P4-25 and
`docs/phase8/v1/PHASE_8_DOC.md` §4.8.5/§5/D-P8-16 accept the contract consumed
by P7 §§4.6/5.1, P6 §§4.4.4/4.12/5 and P10 §§4.6/5/8/11.
P4 §11.5 records published author evidence and bounded Angelica/Iris/Pintonium observations:
they do not determine replay ordering. **Adjacent order is the maintainer's explicit choice.**

P7 owns a private synchronous mod-side adapter beneath existing authenticated main scopes
or Valid shadow execution plus the active root-shadow selection. Effective-provider N governs
N native copies with IDs0…N−1: A0…A(N−1), then B0…B(N−1). P10 client-array/VBO final
submissions and geometry-list playback integrate at v0.5; list capture/build/upload/reset and
Forge/entity/world/shadow traversal occur once. A call-local guard prevents N² forwarding.
Unknown mixed-state lists cannot count as supported by silently drawing once.

Use P6's existing void instance event, restore the saved parent value (outer zero), and do
not add activation participants, expression/history refresh, clears, depth splits, flips or
mipmaps per copy. Propagated draw/protocol failures stop copies and take main abort/off or
shadow abort/neutralization with finally restoration; no post-mutation Rejected/restart.
P6's existing internally isolated per-uniform GL degradation is preserved, not turned into
a fabricated event status or automatic abort. Recorded errors still affect conformance.
No public renderer API or unrelated P10 facade/lifecycle grant is created.

#### IR-24 follow-on — ratified effective macros, no AA feature

P3 §§2.3/4.4/5/11 (D-P3-64), P9 §4.11/§5/§11, P10 §4.8.1/§5/§11 and
P12 §4.6/§5/§11 agree: discover original-source options; parse Properties with standard
macros A–G only; resolve old-light user→pack→true; emit `MC_OLD_HAND_LIGHT`/
`MC_OLD_LIGHTING` as1 iff enabled **before shader jcpp**. Reuse immutable same-build
materialization inputs. Runtime consumes typed tri-states and resolves the same rule,
never reverse-decodes macro presence or feeds a post-load callback into preprocessing.

The exact known keys remain `renderResMul`, `shadowResMul`, `handDepthMul`,
`normalMapEnabled`, `specularMapEnabled`, `oldHandLight`, `oldLighting`,
`antialiasingLevel`. `default` remains distinct from explicit `false` in storage/fingerprints;
the last key is reserved zero-only, not an eighth UI control or enabled FXAA macro.
Unknown-safe storage keys still warn/round-trip in P3; P7/P12 reject unknown executable
delta keys, and P2 preflight checks the published inventory/domains without mutating apply.
P5's option-only sampling correction closes the conflicting AA inventory implication.
The maintainer's narrow ratification is policy authority, not an OQ-7 experiment or parity result.

#### Follow-on verification and remaining boundary

Three exclusive owner-editing slices and Main's receiver integration were followed by three
read-only focused reviews of the settled locale/session, draw/sampling and schema/macro edges.
The reviews identified and the fix-up corrected: a nonexistent P12 text-length projection,
P2 codec-versus-executable-bridge rejection ownership, P6 upload-isolation ambiguity and old
instance range, and P7's missing binding incorporation/current revision marker. The draw/
sampling reviewer re-read those final P6/P7 corrections and reported no remaining actionable
contradiction within that slice. These are scoped documentary checks, not whole-phase PASSes.

Programmatic documentary checks confirm the 42 mandatory §1/§5/§11 sections across fourteen
phase documents, balanced fences, unique §11 decision-table IDs, referenced phase-path existence,
and preservation of this review's entire pre-follow-on byte prefix and the four authority files.
P14's conditional sizing/lifecycle/metrics interfaces introduce no changed accessor/payload and
are intentionally unchanged. No implementation, Gradle build/test, client, GL experiment, matrix
capture, image or retired verification tooling was run or produced.

**All four former partial findings now have a complete architectural disposition.** Broader OQ-7,
IR-01 and the preceding unrelated native-source/jcpp/typed-suffix/P10/optional-UI/P14 gates remain.
Changed phase §5 surfaces still require their applicable fresh whole-document verification
before implementation consumption; this follow-on neither waives those gates nor rewrites
the original audit's result.

### 2026-09-07 prerequisite-remediation follow-on — U1 authority correction

The maintainer has now commissioned the prerequisite architecture remediation that the
earlier IR-02–IR-29 commission excluded: fresh independent phase reviews, separate fix-ups,
owner/consumer grant reconciliation and a final integration review. This authorizes the
review work; it does not itself resolve IR-01 or permit implementation consumption.

After an independent published-document investigation, the maintainer separately selected
**“Correct requirement to documented mechanisms”** for U1. The exact narrow ruling,
primary-source evidence and limits are recorded in
`docs/decisions/U1_TEXTURE_SAMPLING.md`. Existing numeric `.0`–`.9` duplicate discriminators
and `.mcmeta` `blur`/`clamp` remain required. The unspecified filter/wrap property-key
suffix requirement and its future typed-suffix grant are removed through the previously
requested authority-correction route. Historical research, design, review and prior
integration text remain unchanged. This does not ratify unrelated sampling defaults,
malformed-sidecar behavior or modern features.

Phase 3 §0.60/§5/§11 (D-P3-67) and Phase 13 §0.8/§5/§11 (D-P13-26) adopt this correction.
Phase 4/5/6/7/8 active §5 and applicable §11 ledgers adopt the same scope. Existing typed
sources, sidecar references, lossless unknown-key declarations and parser behavior remain;
schema18 is unchanged by this correction alone. No consumer acquires a suffix parser.
This closes the **U1 authority requirement**, not the texture implementation or its
independent conformance obligations. Phase 13 separately records its sampling-default/
error-policy evidence concern; U1 does not decide it.

Settled documentary checks confirmed all 42 mandatory §1/§5/§11 sections, balanced fences
and unique phase decision-table IDs across fourteen documents, and preservation of the
271 pre-existing governing-authority/historical-review files and this review's original
byte prefix. These are structural/preservation checks, not independent whole-phase PASSes.

The separate fresh Phase 1 review is
`docs/phase1/reviews/PHASE_1_REVIEW_26.md`, **PASS-WITH-CORRECTIONS**. Its native-geometry
TRIANGLES-input versus fullscreen-QUADS finding requires an owner/consumer repair and
fresh verification; the review does not clear Phase 1. Native-source completion, jcpp,
vertex/lifecycle grants, other affected phase reviews and final integration remain due.

Independent permitted preparation ran the unchanged template's `./gradlew --version`
and `./gradlew build` with the cached Eclipse Adoptium Java25.0.3 toolchain. Gradle9.7.0
reported `BUILD SUCCESSFUL`; `test` reported `NO-SOURCE`. The remapped template artifact
is `build/libs/modid-1.0.0.jar`, with `ModType: CRL` and `FMLAT: modid_at.cfg`.
No source/build configuration was changed and no client, GL experiment, pack capture,
T0–T3 verdict or human-approved baseline was produced. This build is not shader support.

### 2026-09-08 prerequisite follow-on — native, texture and runtime evidence

The separately approved geometry and sidecar decisions are recorded in
`docs/decisions/GEOMETRY_PRIMITIVE_COMPATIBILITY.md` and
`docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md`. They preserve their narrow scope:
conditional complete-record submission conversion and explicit source-specific sampling/
atomic recovery, not a renderer rewrite, guessed property suffixes or measured G6 parity.

Owner amendments now supply P1 R26 linked/fullscreen policy, exact jcpp pin/closure admission,
R10-1 borrowed-input/early compatibility, full synchronous TextureParameters, and P14
debug activity/package grants. P3 D-P3-68 supplies schema19 native-preserving source and
explicit source/API precedence. P4 publishes checked actual effective geometryInput;
P7 grants R10-2 real-drain/lifecycle/declarations; P10 receives the matching grants.
P13 publishes source-specific strict sidecar recovery, closed failure vocabulary and
phase13.parameters/v2 plus phase13.sidecar/v1 identity, received by P5/P7/P14.
All fourteen current documents have schema19 receipts. These are authored/adopted, **unverified**.

The next independent whole P1 review is R27. Two additional focused provisional audits
have produced concrete current-contract findings, preserved in
`docs/build/reviews/NATIVE_LIFECYCLE_SEAM_REVIEW_1.json` and
`docs/build/reviews/TEXTURE_PARAMETER_SEAM_REVIEW_1.json`:

- NS-1: generic attribute0 can override an admitted conventional vertex source; list capture
  also needs isolation from unrelated enabled arrays. P1 owns transaction correction; P10 receives it.
- NS-2: cached ModelRenderer display-list playback bypasses chunk RenderList/uploader guards.
  P10 owns actual capture/lifetime/playback coverage, P7 its required owner10 health/admission.
- TS-1: P3 closes archive access while publishing texture/sidecar references but no
  snapshot-bound binary acquisition operation. P13's valid preparation success path and
  P7 configuration-preserving resource refresh need an explicit producer/receiver contract.

These reports are not full-phase PASSes. Neither historical findings nor pre-existing gaps
are excused by calling the review patch-only. Main's settled structural/preservation checks
passed all 42 mandatory sections, fences and unique decision-table IDs; all 271 pre-existing
authority/historical-review files, this original review prefix and original R26 text survive.

Independent unchanged-template runtime preparation found working direct NVIDIA RTX3080
OpenGL4.6 at DISPLAY=:0, then an actual pre-menu client crash in JEI/HEI4.33.0 proxy injection.
The diagnostic launch proves AppClassLoader loads JustEnoughItems while LaunchClassLoader
loads ProxyInjector, with duplicate Side and SidedProxy definitions across both loaders.
The current dependency is declared through forced-classpath modLibrary; the precise trace,
crash path and build/runtime distinction are in `docs/build/READINESS.md`. No source or
dependency configuration changed, and no menu, shader, pack tier or human baseline passed.

IR-01, fresh individually eligible phase reviews, separate fixes and final integration
remain open. The implementation prompt has not been narrowed or cleared by this follow-on.

### 2026-09-08 settled correction wave and fresh foundation PASS

P1 round27 independently identified the same array-isolation defect as NS-1. Its original
PASS-WITH-CORRECTIONS report is preserved at `docs/phase1/reviews/PHASE_1_REVIEW_27.md`,
with a separate fix-up Resolutions section. D-P1-55 supplies complete affected-state
isolation/restoration; P10 D-P10-20 and P7 D-P7-32 receive it. P10 D-P10-21 additionally
specifies whole-source staged ModelRenderer capture, original scale/incarnation, every
cached-call geometry guard and paired lifetime. P7 R10-6 explicitly grants all eight
expanded model capture/playback/replacement/deletion CORE owner10 rows and their
quiescence/preparation/invalidation/retirement placement. P8 receives root-shadow coverage.

TS-1 now has an actual P3 D-P3-69 owner capability: `PackConfiguration.assets()` immediately
after sources, with exact Acquired/Missing/Unreadable/InvalidReference results and independent
read-only cursors over shared immutable same-load bytes. Only positively proven optional
owned-sidecar-only read failures survive for the approved atomic recovery; safety, bounds,
index/container and shader/configuration/primary duties remain fatal. P13 D-P13-30 consumes
the bytes and owns one recovery warning; P7 D-P7-33 retains the exact configuration/assets
through resource-only NONE, not a later same-path archive. Full load alone obtains new pack bytes.

Current configuration, nested IDs and inspection are **schema20** in all fourteen receiving
contracts. P3's source-free projection gains a ninth assets-manifest section; P2 D-P2-32
maps it into `[properties] owner.assets`, using the existing typed codec and TextHash for
digest strings. P4 D-P4-30 preserves that exact same-load projection through resolution
enrichment. No binary/cursor/provider enters artifacts; projectionVersion1 and unrelated
capture/golden grammars are unchanged. Dated schema19 native decisions remain historical.
P14's stale missing-full-conversion status is corrected to its already granted D-P1-52/
D-P14-23 receipt; optional execution/equivalence and stageable-upload grants stay separate.

The new independent whole-document foundation review
`docs/phase1/reviews/PHASE_1_REVIEW_28.md` returns literal **PASS**:
blocking0, correction0, note0. It reviews current §5, confirms R27-1 architecture correction,
and explicitly distinguishes that result from driver proof or integration clearance.
P1 is verified on those reviewed bytes; do not propagate its old unverified footer over
the actual §G1.3 review state. Fresh P2 R39 and P3 R55 whole-document reviews are commissioned.
Other phases and final §G5.3 integration still require verification; IR-01 remains open.

Settled checks passed all 42 mandatory §1/§5/§11 sections, balanced fences, unique decision
table IDs and fourteen schema20 receipts. All 271 pre-existing protected files and original
integration/R26/R27 report prefixes remain unchanged. These checks are document structure/
preservation evidence only. Baseline build/client failure and candidate jcpp API inspection
remain separately recorded in `docs/build/READINESS.md`; no shader milestone or tier passed.

### 2026-09-08 R39/R55 correction receipts and renewed verification

The preceding schema20/R28 snapshot is historical, not current implementation clearance.
P2 R39 and P3 R55 returned PASS-WITH-CORRECTIONS (five and three corrections respectively).
Separate fix-up sessions appended Resolutions without changing either original report.

P3 D-P3-70 publishes schema21, exact-current containing/nested inspection/ID equality and
MaterializedSource-v21; payload-free ScreenProfileEntry() uses the existing definitions/
inference route. Usable explicit dimension-only overrides survive; documented external
TEXTURE_RECTANGLE maps to the existing typed rectangle target. Assets and all nine
source-free trees retain D-P3-69 meanings. All fourteen contracts now receive current P3
ownership; P1 D-P1-56 changes only its §5 receipt and therefore requires R29 after R28.
P3 D-P3-71 also receives the complete separate P4 enrichment row without changing its snapshot.

P4 D-P4-31 distinguishes requested-slot ownBuild from effective fallback status and changes
the registry hash domain.
P2/P7 migrate capture-plan/3 and run-manifest/3, with required ownBuild in manifest program
rows; older wire majors are not repaired. P6 D-P6-25 publishes actual accepted frame timing;
P7 D-P7-34/35 specifies authenticated capture-only tick/clock permits, checkpoint0 origin,
actual acknowledgments and retained timing evidence. Normal gameplay and real host deadlines
remain unchanged. Installed Cleanroom hook viability and real capture execution are future
OQ/runtime evidence, not established by these publications.

P2 D-P2-33–38 separates motion document presence from later mandatory execution, preserves
full subject provenance while excluding only the authenticated subject from external comparison
identity, completes all six tolerance fields, and requires independent timing comparability
for uncontrolled manual G6 oracle evidence. No unavailable baseline, calibration, timer or
compile evidence may become a tier PASS. Scene/2, golden1 and projectionVersion1 remain separate.

The settled fourteen documents retain substantive numbered §0–§12 sections and balanced
fences. The 271 pre-existing protected files and original integration/R26/R27/R39/R55 prefixes
remain unchanged. Fresh independent whole-phase verification is now required on this settled
contract set before the final integration review. No author receipt, structural check or
older review closes IR-01 or permits implementation.

### 2026-09-08 recovered reviews and celestial/replay correction receipts

The first fourteen-review attempt returned two actual reports and twelve provider
`usage_limit_reached` infrastructure failures. Independent smaller retries and one resumed
worker produced three further complete reports. No empty infrastructure result is a phase
FAIL or PASS. Exact attempted hashes/rounds and recovery outcomes are in
`docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_1.json`.

P1 R29 is literal PASS (one receiver-scoped note, no P1 correction); P3 R56 is literal PASS
(no findings). Their current bytes remain exactly those reviewed. P7 R37 is literal PASS
with one stale scope-label note; subsequent §5 receiving changes below require R38 and are
not certified by that PASS. P8 R5 requires one correction; P6 R25 requires two corrections
and records two notes. All five complete independent reports are preserved at their numbered
phase review paths, with original verdicts/bodies intact and separate fix-up Resolutions.

P8 D-P8-22 separates pure ShadowCelestialAngles from P6's frame-bound CelestialSample.
The sole compute call now takes the actual main CameraSnapshot and immutable ShadowPlan
alongside frame/extent. P7 supplies the existing authenticated invocation association;
the provider obtains shadowAngle before camera capture from the same pure policy, while
the later eye-space vectors use the actual post-camera matrix and current frame identity.
No hidden GL query, new camera identity field or changed P6 event record is granted.

P6 D-P6-27 publishes required UniformReplayErrorSink/UniformReplayReport and the eight-argument
factory, carrying the existing P1 ReplayAwareGLError values in exact original-drain order.
Delivery precedes barrier/immediate return, preserves repeated observations, separates logging
from evidence, and retains failed/pending delivery through explicit non-complete containment.
P7 D-P7-38 owns the collector and final-callback drain; P2 D-P2-39 flattens unchanged values
into existing /3 gl_errors and never turns evidence loss into COMPLETE. P5's exact factory
receipt agrees. No new GL verb, fourth participant, attribution inference or wire/schema bump.
P6's active instance instructions now use N total values 0…N−1; its authority/tooling notes
and P7's stale /2 scope label are reconciled without rewriting historical records.

Settled structural checks found all fourteen numbered section sequences, balanced fences
and unique scanned decision-table IDs; all 271 protected files and recent original report
prefixes remain preserved. These checks do not prove semantic completeness. Fresh P2/P4/P5/
P6/P7/P8 reviews precede remaining leaf verification and final integration; IR-01 stays open.
Independent canonical fixture preparation is recorded source-free in
`docs/build/reviews/CLASSIC_FIXTURE_PREPARATION_1.json`; archives stay outside the repository.
No shader implementation, runtime tier or final integration verdict is claimed.

### 2026-09-08 corrected-core reviews and resource/state/evidence repairs

The six reports commissioned on the next frozen core are preserved verbatim at P2 R40,
P4 R32, P5 R39, P6 R26, P7 R38 and P8 R6. Exact assigned identities, counts and outcomes
are in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_2.json`. P6 R26 returned literal
PASS with no findings; the other five returned PASS-WITH-CORRECTIONS. P7 R38 is qualified
investigative evidence only: its search exposed excluded conversational text. No excerpt
is reproduced or accepted as authority, and that report cannot certify pristine §G1.2
review. A clean fresh review is required regardless of its correction disposition.

R32 exposes positional DRAWBUFFERS loss and the absence of an enforceable alpha/blend
duration lock. P1 D-P1-57 and P4 D-P4-33/34 now publish typed attachment/none slots, the
opaque lock lifetime and concrete backend interception rather than treating immediate
setters as locks. P5 adopts the positional route and preserves explicit no-output positions.
P5 R39 additionally requires a legal DefaultRgba baseline, a main-mipmap owner operation,
honest planned/realized resource evidence, scheduled consumption of degraded depth-copy
points, and policy-compatible neutral shadow objects. D-P5-30–35 author those mechanisms
and qualify the historical pinned flip-stub attribution without rewriting authority.

P2 D-P2-41–44 author distinct complete-option-state baselines, authenticated comparison/
oracle/approval evidence, the `/4` profile-text transport and qualified historical README
attribution. P7 D-P7-42 receives complete actual option maps and retained plans plus P1's
canonical profile string; P2 alone owns comparison decisions. P6 D-P6-31/32 and P8 D-P8-25
receive the corresponding current wire/effective-state/neutral-backend contracts. P7/P8
also correct depth-write flags, dependency-status inventory and explicit shadow terminal
release before platform restoration. No speculative hand-event change was adopted.

Final P7 backend-hook/mipmap and P2 resource-wire receiving integration is still in progress.
All changed contracts require fresh whole-document verification; P6 R26 and P1 R29 remain
proof only for their prior frozen bytes. Six leaf reviews and final integration remain
outstanding. IR-01 stays open; no implementation, conformance tier or runtime result follows
from these architecture fixes.

Independent fixture preparation now includes the canonical SEUS Renewed 1.0.1 archive after
explicit user EULA authorization. Its external-cache bytes passed ZIP integrity checking;
the source-free provenance JSON records size/SHA-512 and preserves the earlier unacquired
state. No shader source, pack archive or rendered image was added to the repository.

### 2026-09-08 renewed core corrections and thirteen-owner review freeze

P1 R30, P2 R41, P4 R33, P5 R40, P6 R27 and P8 R7 returned
PASS-WITH-CORRECTIONS. Clean P7 R39 returned PASS with a receiver-scoped note.
Reports remain intact with separate Resolutions; their frozen inputs/outcomes are recorded
in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_3.json`.

P1 D-P1-58 makes the opaque lease backend-implementable. P4 D-P4-35 and P6 D-P6-33
align active pre-participant lock order and actual effective-state receipt; P4 uses `/4`
for current evidence. P2 D-P2-47–49 define exact tier domains, complete resolved generation
identity and sole canonical image layout. P5 D-P5-36/37 add mutation-bearing snapshot failure
and correct staged comparison/sizing. P8 D-P8-26–28 add minima-derived requested state,
accepted-estate neutralization before disabled/uninstalled main admission, and explicit
replay-delivery containment. P7 D-P7-45–47 receive shared consumed-frame state, demand/
neutralization and pre-load world authentication. P4/P5/P13 receive the three-field P8 input.
Pinned licence qualifications preserve historical sources without granting future reuse.

Structural verification found all fourteen phase sections/fences valid, all 271 protected
files unchanged, and original core review bodies plus this integration prefix preserved.
Fresh frozen whole-owner reviews are now commissioned for P1/P2/P4–P14; P3 R56 remains
unchanged. The freeze is recorded in ARCHITECTURE_REVIEW_ATTEMPT_4.json. These checks and
authored fixes are not semantic PASSes. Individual results and final §G5.3 integration remain
pending; IR-01 stays open and no implementation or runtime result is claimed.

### 2026-09-08 thirteen-owner findings corrected; all-owner re-verification required

The frozen attempt-4 reports are preserved. P1 R31, P5 R41, P6 R28 and P13 R4 returned PASS;
P2 R42, P4 R34, P7 R40, P8 R8, P9 R1, P10 R1, P11 R11, P12 R5 and P14 R1 required corrections.
Original bodies/verdicts remain immutable; separate owner resolutions record authored changes.

Current corrections include candidate-only sampler initialization before validation; complete
sampler normalization/demotion; durable actual preparation/warmup timing; concrete depth-mask
enforcement; forced shadow traversal/restoration; reachable schema22 alternate BLOCK provenance;
actual TESR dispatch and sorted FastTESR native ID ranges; full sky/star source/playback/lifetime;
per-cell smoothing time and typed/source-less diagnostic delivery; complete settings/error view;
and native-evidence, EMA, producer-flush, quarantine and current-identity modernization fixes.
P7's actual P11 construction/install/compile/reset/activation/retirement receiver is now explicit.
Optional async APIs remain ungranted and synchronous baselines remain mandatory.

All fourteen owners changed or received changed dependencies, including P3 after its R56 PASS.
Fresh whole-owner verification is required on the next frozen set. Structural section/fence/
decision checks passed; independent P11/P14 arithmetic examples matched; all 271 protected
files and original review/integration prefixes remain preserved. No native behavior, hook
application, implementation milestone or final integration PASS is established. IR-01 remains open.

### 2026-09-08 attempt5 completed; selector, population, ingress and allocation corrections

Attempt5's exact frozen inputs and fourteen reports are preserved. P1 R32/P5 R42/P6 R29/
P7 R41/P9 R2/P11 R12 passed; P2 R43/P3 R57/P4 R35/P8 R9/P10 R2/P12 R6/P13 R5/P14 R2
required localized corrections. Original verdicts and bodies remain immutable.

Separate owners now author schema23 typed selector alternatives/ranges; a contained optional
sparse virtual prelude; canonical flattened scalar shadow health and present-estate dispatch;
source-semantic vertex writers; profile-only commit and reserved-zero programmatic admission;
actual stitched allocation extent and complete target-bearing synchronous values; complete
texture-object baselines through ordinary fixed FINAL; and bidirectional upload readiness/
completion ownership. Main has coordinated the affected P2/P7/P9 and other receiving contracts.
P13 application health and actual Post acceptance remain distinct; no runtime count overwrites
a frozen report. P1/P5/P13/P14 share the same allocation and parameter laws.

Independent contract-model checks exercise finite-domain ranges, exact pre/0/5/99 forwarding,
profile intent despite equal values, and the 59-row shadow catalogue's compensating-count and
cloud-only boundaries. Structural section/fence/decision checks passed; protected authority and
original review/integration prefixes remain preserved. These are not implementation/native tests.
All fourteen current owners changed or received changed dependencies. A renewed whole-owner
freeze and final eligible G5.3 review remain required; IR-01 is open and no milestone is implemented.

### 2026-09-08 attempt6 completed; typed clears, inherited inputs and lifetime corrections

All fourteen frozen attempt6 reports are preserved with their original verdicts in the
numbered owner review files and `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_6.json`.
P2 R44/P3 R58/P6 R30/P7 R42/P9 R3 passed. P1 R33/P4 R36/P5 R43/P8 R10/P10 R3/
P11 R13/P12 R7/P13 R6/P14 R3 required twelve corrections in total. Original report
bodies remain unchanged; separate Resolutions record owner amendments.

P1/P5 now define captured target-specific limits and mandatory typed per-attachment clears
with finite realized-format conversion, core/EXT routes, lossless restoration, recorder
events and full-clear failure retention. P10's complete source-authenticated conventional
participation preserves inherited COLOR/UV1 through client/VBO and cached model playback;
P1/P7/P2 receive the plan and evidence without changing physical CLASSIC56.
P4 receives explicit accepted profileSelection and commits evaluated state in
RegistryFingerprint/profile-selection-v3; P7 forwards accepted intent, while current P2
inspection deliberately supplies absent intent. All explicit domain receivers are updated.
P12's immutable pending-profile summary reaches both views independently of inferred display.

P8's exact shadow entity-call outline predicate guard excludes the entire vanilla outline
subpass before tile entities, adding an independently counted60th row and flattened-v2
domain. P13's outer loadSprites try/finally encloses Pre/listener/populator failures and
actual allocation/Post, retaining eight application rows with application-v2 target evidence.
P2/P7 preserve those exact reports separately from runtime acceptance. P11 now uses exact
SCC membership, reverse-reader invalidity and prerequisite-first evaluation. P14 corrects
pack-state isolation, strict UTF-8 label bounds, virtual-group drain and completion-based
warm-up while keeping optional asynchronous APIs ungranted.

Independent contract models exercised graph order/cycle isolation, typed integer saturation,
numeric-class separation, target maxima,60-row health completeness/compensating counts,
UTF-8 bounds and overflow-plus-abort group draining. Fourteen section/fence/decision checks
passed;271 protected files, fourteen attempt6 original report bodies and this original
integration prefix remain preserved. These are model/documentary checks, not native tests.
Eleven owners changed; the three unchanged owners have changed dependency boundaries.
Renewed whole-owner verification and final eligible G5.3 integration remain required.
IR-01 is open; no implementation milestone, client/GL success or pack-tier result is claimed.

### 2026-09-08 attempt7 completed; actual input, atlas and native-state corrections

All fourteen frozen reports are preserved in numbered owner files and
`docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_7.json`. P2 R45/P3 R59/P4 R37/P6 R31/
P9 R4 passed. P1 R34/P5 R44/P7 R43/P8 R11/P10 R4/P11 R14/P12 R8/P13 R7/P14 R4
required thirteen corrections. Frozen verdicts/bodies remain unchanged; separate Resolutions
record the authored fixes, not fresh certification.

P1/P5 normalize capability-legal rasterizer discard around typed clears and retain all three
existing allocation fallback triggers. P1 recorder lists retain complete capture plans before
first replay. P10 authenticates partial ITEM ingress and real four-corner BLOCK brightness
completion before publication; P7/P2 receive full-only H10-BRIGHTNESS-4 health and evidence.
P8 restores three native translucent sort caches independently on all exits, adding six
GET/SET observations:66 rows,65 non-CLOUD,37 RESTORE and flattened-v3. P7/P2 receive that
exact catalogue. P8 public pure celestial math serves actual P7 H-SKY-02 independently of
shadow planning/estate/health, with unchanged P6 events and real-shadow preactivation delivery.

P11 shares one uniform/variable namespace and dependency graph, converted per-refresh memo,
exact SCC/error isolation and declaration-order uploads; P2 receives six original vectors.
P12 publishes prepared session/action/profile availability and rechecks callbacks before any
preview/intent/I/O/reload. P13 fixes local companion/default sampling and authenticates actual
accepted base-object association through context-bound leases. P5 selects matching atlas/kind
after custom precedence or explicit defaults; P7/P8 refresh through the same binder before
next draw, without root activation replay or restoring over the triggering base binding.

P1/P14 distinguish targetless logical texture issuance from exact-target native materialization/
label/storage, and lifetime-ending deletion from ordinary binding-neutral operations.
Affected bindings/cache become zero; unrelated state survives and deleted names are not
resurrected. P14's stall criterion remains unmet/deferred absent measurement or an explicitly
adopted governing amendment; safe synchronous fallback cannot certify full implementation PASS.

Independent contract models and documentary checks exercised state/failure transitions,
capture identity, actual brightness completion, graph isolation/conversion, atlas association,
native lifecycle ordering/zeroing and66-row health boundaries. All fourteen section/fence/
decision checks passed;271 protected files, fourteen original attempt7 reports and the original
integration prefix remain preserved. No implementation/native behavior is proved by these checks.
Eleven phases changed; P3/P4/P9 have changed dependencies. Renewed whole-owner verification
and final eligible G5.3 review remain required. IR-01 stays open; no milestone is implemented.

### 2026-09-08 attempt8 completed; fourteen fresh whole-owner reports, corrections authored

The first attempt8 launch returned provider usage-limit failures for all fourteen workers and
changed no verdict; one partial finding (P14's removed two-argument lease receipt) was corrected
as D-P14-43 before the retry. Retried in smaller batches on the same frozen hashes, all fourteen
independent reports completed and are preserved verbatim in the numbered owner files and
`docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_8.json`: P4 R38, P5 R45, P8 R12 and P9 R5
PASS; P1 R35, P2 R46, P3 R60, P6 R32, P7 R44, P10 R5, P11 R15, P12 R9, P13 R8 and P14 R5
PASS-WITH-CORRECTIONS (seventeen findings).

Separate owners authored every correction with append-only Resolutions: D-P1-72/73 (LWJGL
confinement as a package tree; recorded-only labels on unmaterialized owned texture handles),
D-P2-72 (R43–R45 incorporation recorded), D-P3-74 (override-target whitelist authoritative,
reserved intrinsics rejected pre-I/O), D-P6-38 (current /4 replay envelope in §5.1),
D-P7-76/77/78 (main-estate bind/clear before the vanilla sky site with ESTATE_CLEARED and a
frame-begin ShadowFrameView reserving the terrain token by head-reading field_175084_ae, plus
the §3.6 shadow-before-main-clear ruling; plan-independent pre-camera angles; H-SKY-02
slice-bounded to the sun/moon rotation site), D-P10-31/32 (profile-selection-v3 identity;
R10-8/9 receipts recorded as received), D-P11-28/29/30 (schema23 current; dependency anchors
current; shared-graph scope), D-P12-39 (presenter-owned selection survives catalog replacement
by revalidation; no P7 read-back implied), D-P13-44/45 (schema23 acquisition gate; current
anchors) and D-P14-44 (five published modernization tiers). Main synchronized two review
notes: P3's Phase-11 receiver phrase and P6's P5 resolver citations.

Structural section/fence/unique-decision checks passed on all ten changed documents; frozen
review bodies and verdicts are preserved with append-only Resolutions; the working tree shows
only the fourteen phase documents and this review modified. These are documentary checks, not
implementation, native, client or pack-tier evidence. Ten phases changed and P4/P5/P8/P9 have
changed dependency boundaries, so all fourteen require another frozen whole-owner review
before the final eligible G5.3 integration. IR-01 remains open; no milestone is implemented.
