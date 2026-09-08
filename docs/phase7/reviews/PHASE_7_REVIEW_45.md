# Phase 7 — architecture review 45

**Date:** 2026-09-08. **Frozen document:** `docs/phase7/v1/PHASE_7_DOC.md` SHA-256 `90c02eef5320f9d4e8f5d361c46a859bc9fba86dff85036ac6964d046c573d9f`. **Verdict:** PASS-WITH-CORRECTIONS (6 correction sites, 5 notes, 0 blocking). **§5 impact:** no §5 contract addition, removal, or semantic change is required — every correction is provenance/record hygiene (stale upstream-request bookkeeping and stale line anchors whose quoted content now lives at different coordinates in the dependency docs); the consumed and exposed interfaces remain exactly as specified.

---

## 1. Scope, identity and authority actually verified

- **Hash gate:** recomputed `sha256sum docs/phase7/v1/PHASE_7_DOC.md` = `90c02eef5320f9d4e8f5d361c46a859bc9fba86dff85036ac6964d046c573d9f`; matches the frozen value. 4736 lines confirmed.
- **Complete phase-document read:** all of `docs/phase7/v1/PHASE_7_DOC.md` in ranges (§0 header and fix-up log 1–480, §1 481–552, §2 553–709, §3 710–847, §4 848–2267 including §4.1–§4.3, §4.8, §4.10.1–4.10.8, §4.11–§4.13, §5 2268–4117, §6–§7 4118–4194, §8 4195–4366, §9–§10 4367–4469, §11 4470–4684, §12 4685–4736).
- **Governing design:** `docs/design/v2.0-RC3/DESIGN.md` Part I lines 1–1109 (§G1 session/fix-up protocol, §G2 D-1..D-10, §G3–§G5, §G9 template at :790–:827, §G11 rules of engagement, §G12 boundary) and the Phase 7 Part-II spec at :1805–:1953.
- **RESEARCH:** `docs/research/v1/RESEARCH.md` lines 11–107 (§0 reading guide, confidence tags, D-table) plus targeted verification of every cited anchor audited in §3/§5 of this review (":533–:552", ":543–:544", ":538–:540", ":796–:820", ":823–:825", ":1009–:1010", ":1228–:1255", ":1390–:1418", ":1402", ":1420–:1433", ":1442–:1448", ":1482–:1492").
- **Dependency docs** (§0 header, §1, §5, targeted §2/§4/incorporated sections): `docs/phase2/v2/PHASE_2_DOC.md` (capture-plan/4 at :1062–:1063, run-manifest/4 at :1143–:1145, dense-sample law, D-P2-50 at :1200–:1232 including P7-issued `checkpointId` at :1212, ownBuild rows :699/:1133/:1171, final-before-present at :3403, §0.26 R39/R55 receipts at :315–:320); `docs/phase3/v1/PHASE_3_DOC.md` (`CURRENT_SCHEMA_VERSION = 23` at :704, `MaterializedSource-v23` at :582, `NormalizedPackPath.canonicalString` at :192–:198 and :764–:771, `PackConfiguration.assets()` after sources at :536/:858, `InternalPackSource`/`InternalPackSnapshot` at :755–:776, `CompanionOptionMacros` at :804, "No old-shape load fallback" at :3355 and fatal-read at :1981); `docs/phase4/v1/PHASE_4_DOC.md` (selection/activate/validateSelection :1759–:1811, `ProgramStateBundle.geometryInput` after `legacyGeometry` at :2125, `ownBuild` disposition :736/:1302/:1319, `resolutions()` :808/:880/:2129, "resolve is detached handle-free inspection" at :2128, "Phase 7 must not re-resolve backup chains or overlay requested-slot state" at :2152–:2153, virtual pre "no resolved program, selection or draw" at :2124 and :240, `RegistryFingerprint/profile-selection-v3` at :1938, `INVALID_PROGRAM_STATE` at :1340/:2078, `profileSelection` request order at :2126); `docs/phase5/v1/PHASE_5_DOC.md` (`textureBindings(snapshot,overlay,expectedOverlay)` :789–:792, `shadowBindings` five-arg :2145/:2234, `applyVirtualTransition` :777/:1615, `openDrawBuffersNone` :778/:1714, `generateMainMipmaps`/`MainMipmapResult` :780/:803–:807, `copyDepth`/`DepthCopyResult` :785/:819–:825, `ClearExecutionResult` :796, `discardPass` :782, `BufferEstateView` :768, `ShadowEstateResult` :2131–:2134, `degradeToNeutral` :2151/:2259, D-P5-46/P1 D-P1-67 typed clear :398/:1495/:1761/:1783, D-P5-50 base-atlas :2543/:2735, Bound-only lease transfer sentence :2729, resize ledger :2344–:2375); `docs/phase6/v1/PHASE_6_DOC.md` (eight-arg factory with trailing `UniformReplayErrorSink` at :449 and §5.1 row :1809, `FrameBeginResult` enum :502–:503, rejection semantics :1067 and §5.1 row :1812 "rejection forbids shader draw", `UniformFrameTiming` :518/:1089, sink methods :525–:528, sample records :786–:795, `FrameUniformSample` :742–:746, D-P6-37 celestial receipt :762–:777, R7-10/11 adoption §0.23–0.24, `retire(reason)` :477/:1704, D-P6-38 `/4` envelope).
- **Consumer docs:** `docs/phase8/v1/PHASE_8_DOC.md` (D-P8-38 at :274/:2547, `CelestialMath`/`ShadowCelestialAngles` :512–:515, eleven-field `ShadowInvocationContext` §5.4 :1985–:1997, `ShadowExecutionView.currentBaseBinding()`/`installBaseBindingReceiver` :1981, §5.4 bridge row :2037); `docs/phase9/v1/PHASE_9_DOC.md` (TexEnv capture :1215, failure-to-off R10-3 :1047); `docs/phase10/v1/PHASE_10_DOC.md` (worker drain :696/:1291–:1294, invalidator :1310, step1 drain rows :1095/:1201/:1204); `docs/phase11/v1/PHASE_11_DOC.md` (§5.5 composition handoff :1265–:1288, exposed compiler/controller rows :1143–:1152); `docs/phase12/v1/PHASE_12_DOC.md` (§0.3 sanctioned soft dependency :101–:109); `docs/phase13/v1/PHASE_13_DOC.md` (`BaseAtlasContext` :563–:572, atlas bind adapter :1704); `docs/phase14/v1/PHASE_14_DOC.md` (D-P14-19 ten-step construction points :2627, JFR :1527–:1530).
- **Conformance-map spot checks:** every RESEARCH anchor cited by P7 §3 was opened against current bytes; results are in §2/§3 below.
- **Hard boundaries honored:** no file outside this review was modified; `reference-src/**`, `docs/**/chatlogs/`, root `*.txt`, and all other `PHASE_*_REVIEW_*.md` were not read; no build, test, gradle, GL, or network execution of any kind.

## 2. Scope and §5 audit

- **Doc gate:** §0.1 declares the assigned reading order with the two sanctioned Pintonium mixin files, the two Cleanroom patches, and only the permitted schlorbium sections; §0.3 records three narrow deviations with reasons; §0.2 explicitly labels its dependency receipts "not a verified-consumption certificate" and marks every historical PASS as historical. No PASS/verification claim is made anywhere in the document (closing status at :4734–:4736 demands a fresh literal-PASS review). Gate posture is honest.
- **Scope discipline:** §1's owned/not-owned partition matches the Phase 7 assignment; adjacent concerns (P1 facade/GL, P3 parsing, P4 selection, P5 estate/binding, P6 runtime, P8 shadow values, P9 alias/IDs, P10 vertex work, P11 expressions, P13 atlas, P14 optimization) are each explicitly excluded and consumed, never reimplemented.
- **§5 consumed-interface honesty (all verified to exist in current dependency bytes):** P2 capture-plan/4 + run-manifest/4 + D-P2-50 P7-issued `checkpointId`; P3 schema23/current-constant, `canonicalString()`, `assets()`, `InternalPackSource`, `CompanionOptionMacros`, no unchanged-load fallback; P4 `profileSelection` request slot, `geometryInput` after `legacyGeometry`, `ownBuild` five-value disposition, `resolutions()` detached rows, `profile-selection-v3`, `INVALID_PROGRAM_STATE`; P5 three-arg `textureBindings`, five-arg `shadowBindings`, `applyVirtualTransition(frameId,pass)`, `openDrawBuffersNone`, `generateMainMipmaps` total result, `copyDepth`/`BackendDegraded`, typed `ClearExecutionResult`, D-P5-50 lease/base refresh, Bound-only lease transfer; P6 eight-argument factory with required `UniformReplayErrorSink` after diagnostics, `FrameBeginResult` rejection semantics, `retire(UNPUBLISHED_ABORT|REPLACEMENT|SHUTDOWN)`, D-P6-37 angular receipt; P8 D-P8-38 pure `CelestialMath.angles`/`sample` and the eleven-field `ShadowInvocationContext`; P9/P10/P11/P12/P13/P14 receiver rows as listed in §1. Every export named in P7 §5.1/§5.4 has a recorded receiver row on the consumer side that I could locate (P8 :1981/:2037, P6 :762–:777, P2 §0.26, P11 :1265, P13 :1704, P14 :2627).
- **Current-identity discipline:** all recent identities are cited as current in live sections — `ShadowHookHealth/flattened-v3` (66 rows, 65 non-CLOUD, RESTORE37; :1861–:1882, :3667, :4210, :4549), `TextureHookHealth/application-v2` (:1945, :4544; v1 explicitly "rejected, not rehashed" at :1957–:1958), `capture-plan/4` + `run-manifest/4` (:2028, D-P7-41 at :4516 "rejecting /3 and earlier"), `RegistryFingerprint/profile-selection-v3` (:3784, :3800, :4714), P3 schema23/`MaterializedSource-v23` (D-P7-57 :4532), `phase13.parameters/v2` + `phase13.sidecar/v1` (:2571–:2572), `projectionVersion1` (:3719), `/4` `gl_errors` envelope (:2334). Stale-era mentions of schema21//3/flattened-v2/application-v1/schema22 occur only inside dated §0 fix-up entries or superseded §11.1 decision rows, which the document itself demotes to historical (:4580–:4582). Compliant.
- **Recent-change cross-checks (all verified in depth):** D-P7-76 (main-estate bind + Phase 5 one fog-RGB clear before the vanilla sky site, ESTATE_CLEARED gating at :1035 and §4.3 step 4, frame-begin `ShadowFrameView` reserving `mainTerrainFrameToken` by head-reading `field_175084_ae` at :1117/:1137, H-FRAME-05 token verification :1678, §3.6 ruling row :827, §3.4 row 4 :789) with receivers P5 :3050 containment and P8 slot consumption; D-P7-77 (pre-camera `shadowAngle` through installed pure `CelestialMath.angles`, plan-independent, §4.1 step 2 :1110–:1117, P6 D-P6-37 :762–:777, P8 D-P8-38 :2547) — adopted on both owner sides; D-P7-78 (H-SKY-02 REDIRECT slice-bounded to the single sun/moon rotation site, excluding sunrise color, §4.10 row at :1396–:1399 and rationale :4553). D-P7-42/45/46/47 (complete option maps + retained plan hash :4512/:4515–:4517, shared consumed-frame state, demand/neutralization, pre-load world authentication) all present with §4.13/§5.3 incorporation. D-P7-32/33/38 (owner10 rows, schema20-era assets retention across resource-NONE, replay sink receiver), D-P7-34/35 (capture-only permits; `/3` row superseded by D-P7-41's `/4`-only cutover), H-RESOURCE-01 wrapping `func_110541_a` (:1546–:1548, CORE row :1819, :3387), shadow execution as distinct ID-scope admission (IR-22 at :3634), reload adapter `NONE < REPUBLISH < FULL` with independent OR flags and receipts (:1513, :858–:881, D-P7-17, D-P7-22/IR-06) — all verified.
- **Template completeness:** all 13 §G9 sections present (§0–§12; "sections may be short, never absent" — none is). §10.1/§10.2 each carry the verbatim RESEARCH question, procedure, success criteria, failure/fallback, and artifact path. §12 items are ordered, tagged, and test-hooked. D-1..D-10 dispositions table present (:4555–:4568) and consistent with the governing constraints.
- **Licensing/Pintonium compliance:** §0.4 states GPL-3.0-or-later, `[V:observed — Pintonium …:line]` provenance format, LGPL-evidence-only reading of the two sanctioned files, clean-room digest use, and exclusion of `glsl-transformation-lib`/AGPL input; §3.4 rows carry per-row PD coordinates; no AGPL trace and no copied mechanism found. Do-not-inherit handling is explicit (IR-12 scope reduction :4596–:4602; closing note :4734).

## 3. Required corrections

All six sites are correction-severity. None alters a contract: the design content each anchor points at was verified to exist at its current location.

### C1 — Stale contradiction record and already-satisfied upstream request U7-1

- **Sites:** `docs/phase7/v1/PHASE_7_DOC.md:819` (§3.6 row 1) and `:4644–:4647` (§11.5 U7-1); compare `docs/phase7/v1/PHASE_7_DOC.md:1702`.
- **Claim:** "Appendix E lists the one-argument `func_174982_a` overload, while the actual world loop calls the four-argument overload," and U7-1 asks RESEARCH to "replace or qualify the `RenderGlobal.renderBlockLayer` row with the actual world-loop overload … retain `func_174982_a` only for its actual role."
- **Evidence:** current `docs/research/v1/RESEARCH.md:1402` (Appendix E.1 row 2) already reads: `renderBlockLayer(BlockRenderLayer,D,I,Entity)I` -> `func_174977_a` (**world-loop entry**); `renderBlockLayer(BlockRenderLayer)V` -> `func_174982_a` (private per-layer draw helper, not the world-loop entry). That is precisely the qualification U7-1 requests, in both parts. `func_174982_a` occurs nowhere else in RESEARCH.md (grep: single hit at :1402). The document's own U7-3 reasoning (:4651–:4656) correctly applies this exact "already qualified upstream, no narrowing owed" logic to Appendix E's preamble, so the omission on row 2 is an internal inconsistency in the doc's stale-contradiction bookkeeping, not a policy question.
- **Failure path:** a maintainer acting on U7-1 would search for a defect that does not exist; §3.6's premise mischaracterizes the governing input bytes and would surface as a false contradiction in the final §G5.3 integration review.
- **Owner+receivers:** Phase 7 owns the §3.6 row and the U7-1 entry; RESEARCH is the upstream text; H-TERRAIN's targeting of `func_174977_a` (and MCP confirmation at :47–:48) is unaffected and correct.
- **Minimal resolution:** re-anchor §3.6 row 1 as a historical round-1 observation that current RESEARCH.md:1402 has already resolved (or delete the row), and close U7-1 as satisfied-by-upstream with the :1402 coordinate, mirroring the U7-3 treatment.

### C2 — §3.2 provenance anchor stale; §0.36 fix-up note contradicts the section it claims to have fixed

- **Sites:** `docs/phase7/v1/PHASE_7_DOC.md:760–:762` (§3.2) and `:308` (§0.36 note).
- **Claim:** §3.2 cites `docs/phase4/v1/PHASE_4_DOC.md:1803–1804` for the quoted prohibition "Phase 7 must not re-resolve backup chains or overlay requested-slot state on the effective provider." §0.36 (:308) asserts this provenance was re-pointed to `docs/phase4/v1/PHASE_4_DOC.md:1579–1580`.
- **Evidence:** the quoted sentence exists verbatim at `docs/phase4/v1/PHASE_4_DOC.md:2152–:2153`, not at :1803–:1804 (current bytes there are the selection-validity paragraph). §3.2 still carries `:1803–:1804`, so §0.36's claimed re-point is not reflected in the section text. Additionally, `:1579–:1580` in current P4 bytes is the attribute-location table (`mc_Entity`/`mc_midTexCoord`), not the prohibition — so the recorded fix-up target would also have been wrong.
- **Failure path:** the fix-up log asserts an amendment the document does not contain; downstream verifiers reconciling §0.36 against §3.2 hit an unexplainable diff, and anyone following either coordinate lands on unrelated text.
- **Owner+receivers:** Phase 7 owns both the §3.2 anchor and the §0.36 note; Phase 4 is the quoted owner (sentence at :2152–:2153, adjacent context :2150–:2153).
- **Minimal resolution:** update §3.2's citation to `docs/phase4/v1/PHASE_4_DOC.md:2152–:2153` and correct the §0.36 note (or add a dated superseding §0 entry) to record the actual final coordinate.

### C3 — §3.6 row 4 mis-quotes and mis-anchors Phase 4's virtual-pre condition

- **Site:** `docs/phase7/v1/PHASE_7_DOC.md:822`.
- **Claim:** cites `docs/phase4/v1/PHASE_4_DOC.md:1783` with the quotation "no resolved program, and no shader draw."
- **Evidence:** current P4 :1783 is the activation step "previous P1 AlphaBlendOverride. A failed close stops activation; never acquire over it." The virtual-pre condition appears at `docs/phase4/v1/PHASE_4_DOC.md:2124` ("Virtual pre is VIRTUAL_FLIP_CONTROL with … no resolved program, selection or draw") and `:240` ("it has flips but no resolved program and is passed unchanged to Phase 5"). The quoted fragment, as quoted, matches neither location.
- **Failure path:** the ruling itself (Phase 5's `applyVirtualTransition` consumes the planned descriptor without a program; Phase 7 never fabricates one) is correct and contract-backed, but the citation fails byte verification and would mislead any reviewer re-deriving the ruling.
- **Owner+receivers:** Phase 7 owns the row; Phase 4 owns the anchor text (:2124, :240); no downstream consumer reads this row's coordinates.
- **Minimal resolution:** re-point the row to `:2124` (primary) with the exact quoted fragment "no resolved program, selection or draw".

### C4 — §5.2 D-P7-42/45-47 source-contract anchors off by ~340 lines; in-line quote attributed to the wrong line

- **Site:** `docs/phase7/v1/PHASE_7_DOC.md:3788–:3790`.
- **Claim:** "The source contracts are `docs/phase4/v1/PHASE_4_DOC.md:1785–1796`, including 'resolve is detached handle-free inspection, not selection authority' at line 1787 and the exact selection/result/validation exposure at line 1796."
- **Evidence:** the quoted sentence is at `docs/phase4/v1/PHASE_4_DOC.md:2128` (§5.1 `PublishedRegistry.registry`/`ProgramRegistryView` row); current :1787 is activation step 6's blend-notification prose. The selection/result/validation exposure (`validateSelection` with first-failure-wins results) spans :1794–:1802, so :1796 lands mid-listing rather than at the exposure. The incorporated contract substance (detached resolve, exact result enums, selection exposure) is otherwise fully present and matches what P7 consumes.
- **Failure path:** coordinate-based re-verification of the D-P7-42/45-47 receipt fails; the drift is a symptom of the same 2026-09-08 dependency renumbering wave as C2/C3/C5.
- **Owner+receivers:** Phase 7 owns the paragraph; Phase 4 owns the anchor texts (:2128, :1794–:1802); P2 copies `resolutions()` evidence through this receipt.
- **Minimal resolution:** re-point to `:2128` for the quote and `:1794–:1802` for the exposure.

### C5 — §4.2/§5.4 Phase 6 anchor cluster: six stale coordinates (semantics all verified present)

- **Sites:** `docs/phase7/v1/PHASE_7_DOC.md:1062`, `:3014`, `:3017`, `:3029`, `:3031`, `:3033`.
- **Claims and evidence (current P6 bytes):**
  - `:1062` cites `:869–:873` for "REJECTED_STALE_FRAME or REJECTED_GENERATION forbids shader drawing"; actual P6 :869–:873 is the Pintonium bucket-order note. Rejection semantics live at `docs/phase6/v1/PHASE_6_DOC.md:1067` and the §5.1 row :1812 ("rejection forbids shader draw"; enum :502–:503).
  - `:3014` cites `:593–:602` for binding-sample non-identity fields; actual sample records are :786–:795 (`CelestialSample`, `FogSample`, `BlendSample`); P6 :593–:602 is §2.4 cadence prose. The claimed field projection itself is exact.
  - `:3017` cites `:378–:383` for the updateCelestial/updateFog/updateBlend mapping; actual sink methods are :525–:528; P6 :378–:383 is §1.2 scope bullets.
  - `:3029` cites `:561–:562` and `:566–:570` for sunAngle/shadowAngle/rainStrength provider values; actual `FrameUniformSample` (sunAngle :745, `OptionalFloat shadowAngle` :746) and tick sample sit at :736–:746; P6 :561–:570 is §2.3/§2.4 prose.
  - `:3031` cites `:598–:599` for `FogSample` carrying no start/end; actual record :791–:792 (worldEpoch, frameId, fogMode, density, color — no start/end, confirming the claim).
  - `:3033` cites `:614–:615` for the full-int-domain factor encoding; actual support is the `BlendSample` int fields :793–:795 and the validation sentence :807; P6 :614–:615 is a §3 conformance row.
- **Failure path:** the bridge fields themselves match P6's current records one-for-one (verified field-by-field), so no behavioral divergence exists — but every coordinate check on the §5.4 bridge fails, and this is precisely the drift class round-32's own note (:343) claims was re-pointed.
- **Owner+receivers:** Phase 7 owns the anchors; Phase 6 owns the current locations (:502–:503, :525–:528, :736–:746, :786–:795, :807, :1067, :1812); P2 flattening consumes the unchanged `/4` path.
- **Minimal resolution:** re-point the six anchors to the coordinates above (or replace line-anchors with §-anchors, which are renumber-stable), in one dated §0 fix-up entry.

### C6 — §3.4 row 5 cites the wrong RESEARCH evidence for the four-argument overload

- **Site:** `docs/phase7/v1/PHASE_7_DOC.md:790`.
- **Claim:** "…target the actual four-argument world-render overload … RESEARCH confirmation at `docs/research/v1/RESEARCH.md:543–:544`."
- **Evidence:** RESEARCH :543–:544 is the depth-copy ordering text ("copy depth->depthtex1 then DEFERRED…"); the four-argument overload confirmation lives in Appendix E.1 row 2 at `docs/research/v1/RESEARCH.md:1402` (and Pintonium row 5, cited correctly in the same §3.4 row). The disposition itself is correct.
- **Failure path:** an evidence audit of the timeline row fails on the RESEARCH leg while the Pintonium leg passes.
- **Owner+receivers:** Phase 7 owns the row; RESEARCH :1402 is the actual confirming text; no receiver consumes this coordinate.
- **Minimal resolution:** change the citation to `:1402`.

## 4. Notes

1. **§3.3 aggregate citation one line short:** `[D-5] docs/research/v1/RESEARCH.md:796–:819` (rows :770, and the D-5 range) omits need-11 at :820; the doc maps need 11 separately with its own provenance (DEFERRED P12), so coverage is complete — only the aggregate range is short by one line.
2. **§0.47 historical identities not inline-demoted:** :440–:441 states "P3 schema21, capture-plan/3, run-manifest/3, golden schema1 … remain unchanged" as a dated round-5/6-era fix-up record. Later entries (D-P7-41 :4516, D-P7-57 :4532, :4580–:4582) correctly supersede and demote these, and §0 entries are dated, but this specific sentence is the only place a stale identity appears in prose that is not itself marked historical; a one-word "(historical)" would prevent misreading.
3. **Unverifiable-in-scope evidence in §3.6 row 6:** the vanilla `renderWorldPass` sequence (:827) cites mirror `KealJones/mc-1.12.2-source_files` coordinates (`EntityRenderer.java:1345/:1347/:1367/:1379/:1387`). The mirror is not in the repository and `reference-src/**` is outside this review's read scope, so those five coordinates could not be byte-verified. The ruling is internally consistent with RESEARCH :538–:540, the Cleanroom patch citations, and H-FRAME-05's anchor; flagged for the final integration review's evidence pass, not as a defect.
4. **Bidirectional coordinate drift in receivers (not P7 defects):** `docs/phase6/v1/PHASE_6_DOC.md:622` cites `docs/phase7/v1/PHASE_7_DOC.md:2316–2320` for the R7-10 "No second map" text, which now lives at :4030–:4034; `docs/phase14/v1/PHASE_14_DOC.md:1530` cites `docs/phase7/v1/PHASE_7_DOC.md:2342` for P7's JFR naming, which now lives in §10.2 (:4453). Both belong to the same 2026-09-08 renumbering wave as C2–C5 and should be swept in the §G5.3 final integration review.
5. **Flag-count wording:** §0.3's "eleven engine flags" reconciles with §3.5's twelve-row table only because `clouds` is a three-value enum rather than a tri-state flag; a parenthetical "(eleven tri-state flags plus the `clouds` enum)" would prevent a recurring miscount.

## 5. Areas audited and found sound

- **Hash and freeze discipline** (§1 above); document self-identifies its frozen state and demands fresh review.
- **Doc-gate honesty:** no consumed contract is assumed verified; §0.2/§0.3/§11.3 consistently mark coordinated grants unverified and keep IR-01, final §G5.3, OQ-4 runtime proof, and implementation gates open.
- **§5.1 exposure set:** `FrameHookSink` family including `captureMainCamera(FrameCameraSnapshot)` returning `ESTATE_CLEARED`, `FrameBeginSignal`, eleven-field shadow invocation bridging to P8 §5.4's `ShadowInvocationContext` (field-by-field match), `UniformSignalBridge` with tuple-pinned final-use retirement, `ShaderReloadController`, flattened-health forwarding, capture-plan provenance fields — each with recorded receivers (P8 :1981/:2037/:2547, P6 :762–:777/:1809, P2 §0.26, P11 §5.5, P13 :1704, P14 D-P14-19).
- **§5.2 dependency receipts:** every contract P7 consumes exists in the current dependency bytes with matching shapes (verified signatures enumerated in §1); the reload adapter (`NONE < REPUBLISH < FULL`, OR flags, receipts), H-RESOURCE-01 wrapper, IR-22 shadow ID admission, and typed-clear/copy/mipmap total-result consumptions all match owner text.
- **Conformance map:** §3.2–§3.5 rows verified against current RESEARCH/dependency bytes (with C2/C3/C6 coordinate corrections); §3.6 rulings all independently corroborated except as noted (N3); §3.7's exhaustive `files.txt` cross-check correctly narrows RESEARCH §7.1's blanket claim and keeps U7-3 (whose "already qualified" reasoning is byte-accurate at :1390–:1392).
- **Recent-change block (D-P7-76/77/78 and the D-P7-42/45-47/32/33/34/35/38 set):** every element of the assignment's cross-check list is present, internally consistent, and adopted by its named receivers; the frame-begin terrain-token head-read, ESTATE_CLEARED gating, pre-sky bind/clear ordering, plan-independent celestial angles, and slice-bounded H-SKY-02 are all specified at both the §4 mechanism level and the §4.10 catalog level with §8 test hooks (items 38–40, §8.1 shadow/celestial families).
- **Template, OQ spike specs, decision log, upstream requests, implementation checklist:** complete per §G9/§G4.4; U7-2/U7-3 and all receipt requests are properly channeled; the checklist's v0.1→v0.5 staging matches §9.
- **Licensing and provenance:** compliant throughout (§0.4 format enforced in §3.4 rows; two-file Pintonium boundary respected; AGPL excluded; do-not-inherit items explicitly dispositioned).
- **Current-identity usage:** enumerated in §2 — no stale identity is cited as current in any live (non-historical) section.

## 6. Verdict

**PASS-WITH-CORRECTIONS** — 0 blocking, 6 corrections (C1–C6), 5 notes. The architecture, contracts, and recent-change integrations are sound and receiver-adopted; the corrections are confined to stale provenance coordinates and one stale upstream-request record (U7-1), all resolvable by a single dated §0 fix-up that re-points anchors to the coordinates verified in this review. No structural rebuild is required. Per §G1.2 this review executes a fresh whole-document verification pass only; **no build, test, gradle, GL, or network command was executed**, and no file other than this report was created or modified.

---

## Resolutions

Date: 2026-09-08, applied by the attempt-9 fix-up wave (fresh Phase 7 fix-up session). The
report body above is byte-identical; the header's frozen SHA-256 describes the pre-fix-up
bytes. Every re-pointed anchor was re-verified against current dependency bytes before
editing.

- **C1 — applied.** §3.6 row 1 (now `PHASE_7_DOC.md:836`) re-anchored as a historical
  round-1 observation recording that current `docs/research/v1/RESEARCH.md:1402` already
  qualifies both `renderBlockLayer` overloads (four-argument `func_174977_a` world-loop entry;
  one-argument `func_174982_a` private per-layer draw helper — byte-verified at `:1402`, the
  only `func_174982_a` occurrence in RESEARCH.md); the ruling column closes U7-1 as satisfied
  by upstream. §11.5 U7-1 (now `:4661`–`:4667`) rewritten as closed 2026-09-08 /
  satisfied-by-upstream with the `:1402` coordinate, mirroring the U7-3 treatment. §4.10.3's
  paragraph (now `:1717`–`:1719`) was intentionally left byte-identical — this report cites it
  as the correct model.
- **C2 — applied.** §3.2 (now `:778`) cites `docs/phase4/v1/PHASE_4_DOC.md:2152–2153`; the
  quoted prohibition “Phase 7 must not re-resolve backup chains or overlay requested-slot
  state on the effective provider” is byte-verified verbatim across those two lines. The
  §0.36 note (now `:308`) was corrected in place from the never-matching `:1579`–`:1580` to
  `:2152`–`:2153`, and the new dated §0.51 entry (`:481`–`:496`) records the supersession.
- **C3 — applied.** §3.6 row 4 (now `:839`) re-pointed to `docs/phase4/v1/PHASE_4_DOC.md:2124`
  with the exact quoted fragment “no resolved program, selection or draw” (byte-verified; P4
  `:240` carries the consistent unchanged-passage summary).
- **C4 — applied.** §5.2 receipt (now `:3805`–`:3807`) re-pointed: “resolve is detached
  handle-free inspection, not selection authority” byte-verified at
  `docs/phase4/v1/PHASE_4_DOC.md:2128`; the selection/result/validation exposure at
  `:1794`–`:1802` (byte-verified span).
- **C5 — applied.** All six Phase 6 anchors re-pointed and recorded in §0.51: §4.2
  (now `:1079`) → `docs/phase6/v1/PHASE_6_DOC.md:1066`–`:1071` (§5.1 row `:1812`); this
  report's `:1067` was widened to the byte-verified paragraph span carrying both the
  enum-outcome mapping and “abandon the shader draw and reacquire the current publication”;
  §5.4 (now `:3031`) → `:786`–`:795`; (`:3034`) → `:525`–`:528`; (`:3046`) → `:736`–`:746`
  (the two stale ranges collapse into one verified span covering `FrameUniformSample`
  sunAngle/`OptionalFloat shadowAngle` and `TickUniformSample` rainStrength); (`:3048`) →
  `:791`–`:792`; (`:3050`) → `:793`–`:795` plus the full-int-domain validation sentence
  `:807`. Field semantics confirmed unchanged against current P6 records.
- **C6 — applied.** §3.4 row 5 (now `:807`) RESEARCH confirmation re-pointed from
  `:543`–`:544` (byte-verified there as the depth-copy ordering art) to
  `docs/research/v1/RESEARCH.md:1402`. §3.1's independent `:543`–`:544` depth-copy citation
  (now `:737`) is a different, still-correct reference and was left untouched.
- **No rejections.** Each correction's quoted content was found at its stated current
  location; none was factually wrong.
- **Main structural check — superseded, not applied.** The brief originally directed
  renumbering the duplicate `D-P7-43`/`D-P7-60` definitions to D-P7-79/80. A Main IRC
  correction superseded that instruction before any edit was made: the duplicate-definition
  finding was a checker false positive (bold receipt/discussion paragraphs are not second
  definitions; §11.1 defines each ID exactly once — `D-P7-43` ledger row, now `:4535`;
  `D-P7-60`, now `:4552` — each covering the aspects the prose paragraphs discuss). No D-P7
  ID was renumbered; every `D-P7-43`/`D-P7-60` occurrence remains byte-identical. The
  separately broadcast P7 `:1927`/`:4550` D-P10-30 recipient edit was likewise skipped per
  the same correction (that renumbering is being reverted upstream; D-P10-30 remains correct).
- **§5 impact — yes, coordinates only.** §5.2 `:3805`–`:3807` (C4) and §5.4 `:3031`, `:3034`,
  `:3046`, `:3048`, `:3050` (C5) changed; no exposed or consumed interface shape, field,
  enum, or semantic changed anywhere. All other edits sit in §0 (`:308`, new §0.51
  `:481`–`:496`), §3 (`:778`, `:807`, `:836`, `:839`), §4.2 (`:1079`), and §11.5
  (`:4661`–`:4667`). Document grew 4736 → 4756 lines; `## 0.`–`## 12.` sequence, balanced
  code fences, and decision-ID uniqueness preserved.
- **Files touched:** `docs/phase7/v1/PHASE_7_DOC.md` and this review file (this Resolutions
  append only). No other phase document, review, `docs/PHASE_INTEGRATION_REVIEW.md`,
  `docs/build/**`, or `reference-src` was modified; `docs/phase12/v1/PHASE_12_DOC.md` and the
  P4/P6/RESEARCH dependencies were read read-only for verification only. No build, test,
  gradle, GL, or network execution.
