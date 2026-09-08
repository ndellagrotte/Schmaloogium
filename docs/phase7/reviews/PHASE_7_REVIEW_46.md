# Phase 7 — architecture review 46

**Date:** 2026-09-08 · **Reviewer:** fresh independent §G1.2 session (attempt-10, round 46; no author context). **Frozen artifact:** `docs/phase7/v1/PHASE_7_DOC.md`, 4756 lines, SHA-256 `fb1600d66a7eed5cbd9c1a057ebe7f8e328d793b984756eccddbd77daf3593e0` — recomputed before review and matched. **Verdict: PASS-WITH-CORRECTIONS — 0 blocking, 5 corrections, 3 notes.** All corrections are citation-coordinate repairs (anchor hygiene); no contract, semantic, structural, or scope defect was found. **§5 impact:** corrections C1–C3 edit prose inside §§5.1/5.2 and C4–C5 edit §3.5/§3.6 provenance columns, so §5 bytes change; per §G1.3's re-verify rule the interface region's fresh-verification requirement remains open after fix-up. No build, test, compile, gradle, GL, or network command was executed by this review.

---

## 1. Scope, identity and authority actually verified

- **Hash gate:** `sha256sum docs/phase7/v1/PHASE_7_DOC.md` → `fb1600d6…593e0` (match); `wc -l` → 4756 (matches assignment).
- **Complete phase doc read:** all 4756 lines in sequential chunks (§0 through §12, including every §0.x addendum, the full hook catalog §4.10.1–4.10.8, §4.13.1, the §5.1 code blocks, §5.2–5.5, §6–§12). Two §3.6 table rows exceeding the reader's line width were re-extracted verbatim via `sed` before judging them.
- **Governing design:** `docs/design/v2.0-RC3/DESIGN.md` Part I lines 1–1109 (G0–G12 complete, including the §G9 skeleton at :790–827 and §G1.2/§G1.3 protocol) and the Phase 7 spec :1805–1956 complete.
- **RESEARCH.md:** §0–§1 (:11–107) complete; anchor regions re-read and byte-verified: §4.1/§4.4 (:477–566), §7.1 (:795–828), §11 OQ rows (:1005–1015), App A.1 (:1100–1145), App B.3 (:1227–1255), App E (:1387–1433), App F.1 (:1442–1448), App F.5 (:1481–1492).
- **Dependency docs** (regions actually read, beyond §0/§1/§5 spot-checks): P2 v2 (§4.5/§5.1.1 identity rows via targeted search), P3 v1 (:643–717, :1082–1093, :1638–1646, :1682–1751, schema regions :580–583/:722–726/:3328/:4223–4240), P4 v1 (:1787–1809, :2117–2160, :1937–1941, :2205–2209), P5 v1 (:2336–2378, :2712–2743), P6 v1 (:519–533, :729–815, :1058–1082, :1807–1819).
- **Consumers** (interface-honesty cross-checks, targeted): P8 v1 (:248–282, :1692–1732, :1781–1785, :1857, :2036–2057, :2536–2551), P13 v1 (:246–251, :1681–1706, :1773–1784), P2 v2 (:1063–1146, :1367–1380, :2426–2447, :2530–2531).
- **Not read (per hard boundaries):** `reference-src/**` (Pintonium/Cleanroom/schlorbium file-level pins in the doc are therefore recorded as unverified-by-me, not contradicted), `docs/**/chatlogs/**`, root `*.txt`, other `PHASE_*_REVIEW_*.md`, `docs/build/**`, `docs/PHASE_INTEGRATION_REVIEW.md`. The `cleanroom` MCP server was not used by this review.
- **Read-only discipline:** the only file written is this report.

## 2. Scope and §5 audit

- **Doc gate (spec :1941–1945):** all criteria met — §7.1 needs 1–11 each traced to hook-site spec, Forge event, or explicit deferral with owner phase and milestone (§3.3 + §4.10.8); v0.1 assembly narrative complete (§9.1, 11 ordered increments); both spike specs complete with question/procedure/success/failure/fallback (§10.1 OQ-3, §10.2 OQ-4); the 7-row Pintonium timeline adopted per row with deviations justified (§3.4 rows 3/6/7 split/reject/strengthen); sky/weather/clouds flagged "no working reference" in §3.4, §4.10.3/4.10.5 and front-loaded in §9.1 step 6.
- **Template completeness:** all thirteen §G9 sections present and substantive (§0 inputs/deviations; §1 ownership + 12 adjacent-owner lines; §2 placement in G3 layout; §3 conformance; §4 detailed design; §5 exposed+consumed; §6 degradation ladder mapping incl. rung 2a; §7 threading; §8 three-way testability; §9 staging; §10 OQs; §11 decisions/contradictions/upstream; §12 checklist with tags/test hooks).
- **Conformance-map audit:** §3.1 rows re-checked against RESEARCH §4.4 bytes (:533–537, :538–540, :540–542, :543–546, :551–552, :557–561, :562–566, :478–490) — semantically faithful, zero unmapped. §3.2 vs App A.1 :1101–1141 — every slot mapped, zero unmapped. §3.3 vs §7.1 :796–819 — zero unmapped (need 11 correctly section-cited). §3.7 vs :823–825 — the "every class" over-claim contradiction is correctly reported with the exhaustive 17/18 result. §3.4 App-E row pins (:1401/:1402/:1415/:1417) exact.
- **Interface honesty:** P4's consumed selection/validate/barrier contracts exist verbatim at P4 :2123–2137/:1794–1802; P5's `TextureBindingResult` algebra and sixteen-row/Bound-transfer law exist at P5 :2713–2716/:2729; P6's sink/sample schemas exist at :525–533/:788–797; P13's frozen request field order matches P7's §5.2 list exactly; P2's `/4` schemas exist at P2 :1063/:1144. Consumers reference P7 surfaces consistently (P8 :1783–1785 vs P7 `ShadowInvocationSlot`/D-P7-76; P13 three-argument lease vs P7 :3910; P2 :2426–2427 vs P7 §4.13).
- **Scope discipline:** spec Scope—out honored — shadow content is a slot only (§1.2, §5.3 NotInstalled gating), App E rows 5–8 catalogued `DEFERRED(P10)` not specified, rows 10–11 `DEFERRED(P13)`, ID values P9-owned; the later-phase integration material is framed as owner-granted/downstream slots with unverified standing, matching the §0.23+ addendum history.
- **Current-identity discipline (cross-verified in owners/consumers):** schema23 + `MaterializedSource-v23` (P3 :581–582/:723/:3328; P4 :2206; P8 :2051–2055; P13 :1779–1782; P2 :2530–2531 — P7 D-P7-57 :3731–3737 consistent); `RegistryFingerprint/profile-selection-v3` (P4 :1937–1938; P8 :1857; P13 :1773/:1802–1803; P2 :2146 — P7 :3801/:3817–3818 with the D-P4-33 supersession recorded); `ShadowHookHealth/flattened-v3` 66/65/RESTORE-37 (P8 :279–280/:1782; RESTORE family counted = 37 rows at P8 :1693–1729, six sort GET/SET insertions after 147595-R-SET in ASCII order verified; P2 :1367–1370); `TextureHookHealth/application-v2` (P13 :246/:1579/:1681; P2 :1378); `capture-plan/4` + `run-manifest/4` (P2 :1063/:1144/:2426–2427; P8 :2077/:2536); `projectionVersion1` (P3 :583; P8 :2057; P2 :2431; P13 :1782); `phase13.parameters/v2` + `phase13.sidecar/v1` (P13 :196/:1362/:1376/:1387).
- **Binding decisions:** §11.2 disposes D-1…D-10 completely; none contradicted (D-5 catalog shape, D-6 seam §1.3/§2.1, D-9 compat profile with P1-owned routing, D-7/D-8 licensing posture §0.4).
- **Licensing/Pintonium:** §0.4 posture intact; §3.4 rows carry exact `[V:observed — Pintonium …:line]` coordinates; §0.3 records all three reading deviations with reasons; no AGPL glsl-transformation-lib, stareval, or excluded-boundary usage; §4.13.1's Forge-patch URL is flagged as target-era mechanics only.

## 3. Required corrections

**C1 — §5.1 Phase 6 signal-anchor cluster is systematically two lines short; falsifies §0.51's byte-verification claim for this cluster.**
- **Sites:** `docs/phase7/v1/PHASE_7_DOC.md:3031` (cites P6 `:786–:795`), `:3034` (cites `:525–:528`), `:3046` (cites `:736–:746`), `:3048` (cites `:791–:792`), `:3050` (cites `:793–:795` and `:807`); §0.51 at `:492–495` asserts these were byte-verified.
- **Failure path:** at `docs/phase6/v1/PHASE_6_DOC.md` current bytes — `updateFog`/`updateBlend` are at :529/:530 (outside `:525–:528`); `sunAngle`/`shadowAngle` at :747/:748 (outside `:736–:746`); `:791–:792` is `ShadowMatrixSample`, while `FogSample` (the claim's subject, "carries no start/end") is :793–794; `BlendSample`'s non-identity fields are :795–797 (not `:793–:795`); the full-int-domain validation sentence is :809–810 (not :807); `:786–:795` truncates `BlendSample` at its header line. Verified by direct read and independent pattern search.
- **Owner + receivers:** owner Phase 7; receivers P6 (quoted schemas) and any consumer tracing the UniformSignal bridge.
- **Minimal resolution:** re-point to `:525–:530`, `:736–:748`, `:793–:794`, `:795–:797`, `:809–:810`, `:788–:797` respectively (coordinates only; the semantic claims themselves match P6 content), and qualify §0.51's "every re-pointed quote was byte-verified" sentence for the P6 cluster.

**C2 — §5.2 Phase 5 receipt cites the wrong passage and wrong binding-row line.**
- **Site:** `docs/phase7/v1/PHASE_7_DOC.md:3863–3865`.
- **Failure path:** claims "the exact producer signatures and incorporated lifecycle are at `docs/phase5/v1/PHASE_5_DOC.md:2344–2375`; the binding row at line 2363 states 'Bound alone transfers lease into closeable sixteen-row snapshot with BoundObject/Unused'". P5 :2344–2375 is the resize-consumer/`ConsumerFailed.deliveredCount` passage; :2363 is the deliveredCount sentence. The quoted row lives at P5 :2729 (§5 exposed `textureBindings` row; corroborated by P13's own citation of `:2726–2735` at `docs/phase13/v1/PHASE_13_DOC.md:1774`); the snapshot/discardPass producer rows are P5 :2713–2716 (signatures :779–785).
- **Owner + receivers:** owner Phase 7; receivers P5/P13 lease-and-binding consumers.
- **Minimal resolution:** re-point the sentence to the §5 rows (`:2712–2716`, `:2726–2735`) with the binding row at :2729; coordinates only.

**C3 — §5.2 Phase 13 slot cites the wrong passage for the frozen build request.**
- **Site:** `docs/phase7/v1/PHASE_7_DOC.md:3903–3906`.
- **Failure path:** cites `docs/phase13/v1/PHASE_13_DOC.md:1147–1163` and "the exact frozen request at line 1150". P13 :1147–1163 is the upload-failure/compatibility and shared-unit-selection prose; :1150 is a lifetime sentence. The frozen `TexturePlanRequest(…)` field list — whose order matches P7's enumeration exactly (configuration, registry, atlases, sources, companionPolicy, macroState, capabilities, registryFingerprint, estateGeneration, registryGeneration, resourceReloadEpoch) — is P13 :1706 ("Frozen inputs" row; record declaration :419).
- **Owner + receivers:** owner Phase 7; receiver Phase 13.
- **Minimal resolution:** re-point to `:1706` (and :419 for the `TextureBuildRequest` record); coordinates only — semantic fidelity confirmed.

**C4 — §3.5 engine-flag provenance pins into Phase 3 are stale (P3 growth rot).**
- **Sites:** `docs/phase7/v1/PHASE_7_DOC.md:819` (clouds → P3 `:697`), `:820` (`:706–:709`), `:821` (`:702`), `:822` (`:703–:704`), `:823` (`:705`), `:826` (`:713`), `:828–830` (`:757`).
- **Failure path:** P3 `:697–:713` is now the §2.2 `PackFrontEnds`/`PackFrontEndServices` declaration block and `:757` falls inside the same code block — none is a flag field/owner row. Current coordinates: `EngineFlags` tri-state fields P3 :1082–1093; Appendix F.1 ownership map (clouds :1683, underwaterOverlay :1693, sun :1694, moon :1695, vignette :1696, backFace.* :1697–1700, frustumCulling :1704); precipitation handoff row :1748. The RESEARCH precedence pins (:1442–:1448) in the same rows are verified correct.
- **Owner + receivers:** owner Phase 7; receiver Phase 3 (field/owner authority).
- **Minimal resolution:** re-point the seven `field/owner` pins to the :1683–1704 map (fields :1082–1093) and the precipitation pin to :1748; coordinates only.

**C5 — §3.6 row 8's jcpp-ordering pin into Phase 3 is stale.**
- **Site:** `docs/phase7/v1/PHASE_7_DOC.md:843`.
- **Failure path:** cites `docs/phase3/v1/PHASE_3_DOC.md:644–666` for "jcpp precedes configuration"; that range is §1.1 tail/§1.2 ownership table/§2.1 invariants. The load-order steps establishing option/catalog finalization and the macro environment before jcpp are P3 :1638–1646; companion-macro-before-jcpp at :1835.
- **Owner + receivers:** owner Phase 7; receiver Phase 3.
- **Minimal resolution:** re-point to `:1638–1646` (optionally `:1835`); coordinates only.

## 4. Notes

- **N1 (§11.1 ledger order):** `D-P7-56` sits between `D-P7-49` and `D-P7-50` (`:4542–4543`), breaking the otherwise ascending numeric order of the table. All 78 IDs remain unique single rows; referencing for the wave's named decisions was traced and is coherent — `D-P7-43` (ledger :4535) referenced at :449, :1807, :3052, :3809, :4590, :4734; `D-P7-60` (ledger :4552) referenced at :3374, :3514 — no renumbering artifacts, no dangling references. Cosmetic only.
- **N2 (§4.2 :1079):** the parenthetical "(§5.1 row `:1812`)" points at P6's `UniformRuntime` row, which does list `beginFrame(FrameBeginInput) -> FrameBeginResult`; the more specific `FrameBeginInput`/`FrameBeginResult` semantics row ("rejection forbids shader draw") is P6 :1814. Adjacent-row precision note; content exists at the cited coordinate.
- **N3 (§3.3 provenance):** rows 1–10 pin RESEARCH `:796–:819` while hook need 11 sits at :820 and correctly carries a section-level citation — no coverage gap; noted so a future repoint does not mistake the range for the full list.
- Reference-`reference-src/**` pins (Pintonium mixin coordinates, Cleanroom patch lines, schlorbium `shaders.properties:53–59`, `files.txt` rows, vanilla `EntityRenderer.java` mirror lines) were not re-verified: the review's hard boundaries forbid reading `reference-src/**`. None is contradicted by anything read; their OQ-4/application-proof gating is stated in the doc itself.
- The six §0.51 P6 repoints include `:1066–:1071`, which **is** byte-correct (P6 :1068–1072: `REJECTED_STALE_FRAME`/`REJECTED_GENERATION` … "reacquire the current publication"); the P4 repoints (`:2152–:2153`, `:2124`, `:2128`, `:1794–:1802`) and the RESEARCH `:1402` repoints (§3.4 row 5, §3.6 row 1) are all byte-verified exact, including the quoted prohibition and virtual-pre sentences.

## 5. Areas audited and found sound

- **Hash/identity gate** and frozen-bytes discipline (§0.2 keeps every dependency honestly unverified; closing status :4754–4756 claims no current PASS).
- **Wave targets:** U7-1 closed as satisfied-by-upstream — RESEARCH :1402 verbatim carries the four-argument/one-argument qualification claimed; §3.6 row 1 correctly re-anchored as a historical round-1 observation; §11.5 U7-1 rewritten closed 2026-09-08 mirroring the U7-3 treatment (whose App E preamble pin :1390–:1392 also verifies). §0.51 addendum present and structurally accurate except the C1 cluster; §0.36's in-place `:2152–:2153` correction confirmed. D-P7-43/D-P7-60 remain single, coherently referenced decisions.
- **Conformance maps** §3.1/§3.2/§3.3/§3.4/§3.7 against RESEARCH bytes (semantic fidelity, zero unmapped rows, over-claims reported not smoothed).
- **D-P7-76/77/78 (R44) machinery** internally consistent: §4.2 `ESTATE_CLEARED` gating, §4.3 steps 1/4/5/6 ordering, `field_175084_ae` token reservation and H-FRAME-05 verification, H-SKY-02 slice-bounding with the twice-called `func_72826_c` rationale.
- **Interface algebra completeness:** `FrameHookSink`/result algebras closed and exhaustive; `ShadowInvocationContext` field order matches §2.2/§5.1 listings; `UniformSignal`/`BlendStateValue` field fidelity to P6 schemas (modulo C1 coordinates); resize/reload/programmatic-adapter surfaces closed.
- **Cross-phase identity discipline** (all eight current-identity items verified against owner and consumer bytes, §2 above).
- **§4.10.6/§4.12 hook-report integrity:** nine HEAD + six RETURN cardinality internally consistent with the catalog; `HookApplicationReport`/subreport shapes closed; deferred-row honesty (zero-expected DEFERRED rows, H9-COLOR ownership spelling preserved).
- **D-1…D-10 dispositions, licensing/Pintonium provenance, reading-deviation transparency (§0.3), forbidden-source hygiene.**
- **§6 degradation mapping incl. rung 2a; §7 render-thread confinement; §8.1–8.7 planned-case coverage; §9.1 dependency-ordered assembly; §12 checklist tags/test hooks.**

## 6. Verdict

**PASS-WITH-CORRECTIONS.** Five coordinate-level corrections (C1–C5: one P6 §5.1 anchor cluster ending two lines short of its quoted content plus the §0.51 byte-verification overclaim; two §5.2 dependency-receipt pins — P5 :2344–2375/:2363 and P13 :1147–1163/:1150 — pointing at unrelated passages; two §3.5/§3.6 P3 provenance clusters rotted by P3's subsequent growth) and three notes. No blocking finding: the doc's contracts, conformance maps, scope boundaries, decision ledger, identity discipline, and licensing posture are sound and internally coherent, and every semantic claim behind the mis-pinned citations was confirmed true at its actual coordinates. Because C1–C3 edit §5.1/§5.2 prose, the corrected document requires a fresh §G1.2 verification round before Phase 7 closes, per §G1.3. No build, test, or GL execution was performed by this review.
