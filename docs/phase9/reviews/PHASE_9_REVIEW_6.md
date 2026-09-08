# Phase 9 — architecture review 6

**Date:** 2026-09-08. **Frozen document:** `docs/phase9/v1/PHASE_9_DOC.md` SHA-256 `471d6547fc3a43a16062e2575833b72dc3e7325e482eaa7895d1d147c02e4b2e` (recomputed before review; matches the frozen value; 1395 lines). **Verdict:** PASS-WITH-CORRECTIONS (4 corrections, 2 notes). **§5 impact:** corrections restate the §5.4 adoption-ledger disposition and receiver-state description only — no exposed or consumed contract changes, so no additional re-verification round is forced beyond the fix-up itself.

---

## 1. Scope, identity and authority actually verified

- Recomputed `sha256sum docs/phase9/v1/PHASE_9_DOC.md` = `471d6547…e4b2e`; identical to the frozen hash. The document is the assigned 1395-line artifact.
- Read in full: the phase doc (lines 1–1395); the governing design `docs/design/v2.0-RC3/DESIGN.md` Part I (lines 92–1109: §G0–§G12 incl. §G1.2, §G2 D-1..D-10, §G4, §G9, §G11, §G12) and the Phase 9 spec (lines 2035–2125); `docs/research/v1/RESEARCH.md` lines 11–107 plus every phase-doc-cited region (§3.6.8 :438–451, §3.7 :453–462, §4.7 :600–618, App C.1/C.2 :1277–1306, App D :1319–1348, D.4 :1369–1383, App E :1387–1433, F.1 :1437–1448, §1.2 non-goals :68–86).
- Dependency/consumer docs read at the assigned sections, against current bytes: P3 §0 addenda, §2.2 (records :1249–1345), §3.1 engine-flag ownership (:1662–1690), §4.6/:2334–2349 (MC_OLD_HAND_LIGHT), §4.9/:2994–3170 (ID grammar, SelectorKind/MappingEra/forced11300), §5 rows (:3360–3368, :3452–3498, :4095–4218), §11 D-P3-47/72/73 (:5047–5077), migration prose (:5161–5164, :5264–5268). P6 §2.2/:439–535, :785–833, §4.14/:1745–1749, §5.1/:1808–1818, §11 D-P6-36. P7 §4.1/:860–951, §4.6–4.8/:1060–1141, :1500–1524, §4.10.4/:1677–1734, :1859–1934, §5.1/:2570–2740, :2969–3046, :3152–3245, :3261–3267, :3634–3667, :3927–3991, :4094–4101, §11.3. P10 §2.2/:284–296, :376–386, §4.x/:660–740, :1203–1327, §5.1/:1795–1838, §11.1 D-P10-26..31, §11.3/:2323–2327. P2 §4.5.4/:902–982, §5.4 R18/:1712–1713. P12 :233–237, :999–1003, :1158–1160, D-P12-36.
- PD §8 (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:490–535`) read to adjudicate the Pintonium provenance claims.
- Spot-checked directory existence for cited evidence trees (`reference-src/` top level only; no reference-tree content was read): `Pintonium-main/`, `Cleanroom-0.6.12-alpha/`, `schlorbium-HD_U_G6_pre1/`, `Oculus-1.12-1.12.2/` present; `pintonium-9c2fcc1/` and `cleanroom-0.6.6-alpha/` absent (finding C3).
- Not verifiable under this review's boundaries: line citations into `reference-src/**` (Pintonium/Cleanroom/Schlorbium bytes) and into `doc/shaders.txt`/`doc/properties_files.txt`/`doc/shaders.properties`. Their semantic content was adjudicated through RESEARCH and PD quotes only. Other review files, chatlogs, and root `*.txt` were not read. No build, test, gradle, GL, or network execution was performed; nothing outside this report was modified.

## 2. Scope and §5 audit

- **Doc gate: met.** The conformance map (§3) covers every §3.7 form (block/item/entity properties :376, short/namespaced :377, property-matched :378, legacy `id:meta` :379, A–G macros :380, per-mod files :381), the merge and precedence rules (:382–:386), fluid/alias/11300/fallback rows (:387–:390), layers (:391–:392), held-item rows (:393–:395), per-draw rows (:396–:399), the Phase 10 alias service (:400), degradation/rebuild rows (:401–:402). The alias-service interface is fully specified (§2.2 records, §4.10 bit contract, §5.1 consumer column). Both REV1 dual-spec mechanisms appear as designed features with PD provenance (rows :388–:389, PD §8.1). Held-item and `entityColor` reference-free status is acknowledged (rows :393/:398; §4.13 "Pintonium has no 1.12 producer"; PD §8.1's stub disclosure).
- **Scope discipline: met.** §1.1/§1.2 match the spec's in/out lists: vertex stamping and the chunk stack → P10; uniform upload mechanics → P6; grammar/pack reopening → P3; layer *dispatch* → P7; GUI → P12; dynamic lights → non-goal (`RESEARCH.md:73`, correctly cited at :72–:80). The entity-data stack is P10's; the id computation stays here (§1.1, §4.10). The App E-format hook ledger for rows 13–14 exists even though P7 already specified those rows — recorded as augmentation, not re-specification (§4.12), and the color-overlap contradiction is ruled in §11.2.3.
- **Interface honesty: verified in both directions.** Consumed P3 surface exists with matching semantics: `IdMappingInput`/`IdMappingFileInput`/`MappingFileState`/`SelectorKind`/`MappingEra`/`forced11300Rules` BLOCK/ENTITY-only (`PHASE_3_DOC.md:1249–1263`, :1341–1343, D-P3-72 comment), replace-only-MC_VERSION alternate (:1821, :3026–3035), `%`→`SelectorKind.TAG` without membership resolution (:3081–3084), typed `MetadataConstraint`/`IntegerRange`/`PropertyValueConstraint` (D-P3-73, :3122–3157), `EngineFlags.oldHandLight/dynamicHandLight` tri-states with Phase 9 as behavior owner (:1670–1671), `EngineOptionData` `default|true|false` old-light keys (:3455–3458), load-time user→pack→true `MC_OLD_HAND_LIGHT` projection before shader preprocessing (:2345–2348), `CURRENT_SCHEMA_VERSION = 23` (:704, :4161), `MaterializedSource-v23` (:3309, :4212), `projectionVersion=1` + nine source-free trees (:4215). Consumed P6 surface exists exactly: `UniformRuntime.events()` (:470), `UniformEventSink.updateEntityId/updateBlockEntityId/updateEntityColor/updateHeldItems` (:529–:534), `HeldItemSample(worldEpoch, logicalTick, 4 ints)` (:796–:798), held-light old-mode pair contract `(max(main,off), off)` / `(main,off)` (:809–:813 — identical to §4.11), full-int alias IDs with 0–15 held light (:807–:809), immediate-if-active/next-activation policy (:1815), retired-sink rejection (:1746). Consumed P7 surface exists: H-ENTITY-02/03 rows carrying the H9 IDs (:1709–:1710), `IdScopeAdmission`/rejection enum/stale-generation semantics identical to §4.12 (:3634–:3643), `ShadowExecutionBridge.validate(...)=Valid` gate (:2726–:2734), `PerDrawDynamics.resetFrame` on finish/abort (:1067–:1071), held sampling exactly after Phase 6 frame acceptance (:1098–:1102), ten-step §4.1 transaction with publish-P9-after-Phase-13 and failure-to-off (:923–:951), `IdDependentGeometryInvalidator` before atomic admission (:2422–:2428), matched lookup/ordinal-map retention through worker drain (:2403–:2411, :2442–:2443), `FrameToken`/`ScopeToken`/`FrameBeginSignal`/`UniformSignalBridge`/`ShaderReloadController`/`HookApplicationReport` all published in §5.1. P10 consumes exactly what §5.1 promises: `AliasLookup.generation()`/`mcEntity`/`BlockStampResult`, one lookup+ordinal map per task, matched-borrow lifetime, low/high 16-bit word split at :377–:379 and :1800, D-P10-26 receiving the deferred-batch protocol. P12 publishes the `oldHandLight` `default|true|false` user setting routed to Phase 9 (`PHASE_12_DOC.md:1000`, :1159). P2's manifest carries P7's frozen hook report including deferred-owner rows (`PHASE_2_DOC.md:954–:979`, R18 :1713) — consistent with §5.5.
- **Recent-change identities: verified in the bytes.** Texture-before-ID publication and failure-to-off composition (P7 §4.1 step 9 / §5.3, mirrored in P9 §5.3 steps 4–5); lookup/ordinal-map storage through worker draining (P7 :2403–:2411 ↔ P9 §4.1); tuple admitted only after required geometry invalidation (P7 :2422–:2428 ↔ P9 §5.3 step 5); schema23 alternate BLOCK provenance reachable (P3 D-P3-72/73 + CURRENT_SCHEMA_VERSION=23 ↔ P9 D-P9-22/§4.7/§8.1 test 5); actual TE dispatch `func_192854_a` with App E row 14 demoted to historical (P7 :1710, :1850 ↔ P9 §4.12 "Actual dispatch coverage"); sorted FastTESR native ID ranges (P7 H7-TE-BATCH rows :1718–:1720 ↔ P9 §4.12.1/§5.6); entityColor operand capture with P7 v0.1 ownership (P7 :1716–:1717, D-P7-18 ↔ P9 §4.13).
- **Template: complete.** All thirteen G9 sections present and substantive; §10 correctly empty (spec assigns no OQ). Header records inputs actually read, deviations with reasons, legal posture, and demotes historical dependency PASS receipts to provenance.
- **D-1..D-10 and licensing: no violation found.** No silent overturn of any binding decision (doc §11.2 claim checked — confirmed). Dynamic-lights non-goal enforced with a suppression-only disposition. GPL-3.0-or-later posture, §G11.4 recorded decisions with contract checks (D-P9-2/3/4/6), PD §17 stubs not re-inherited, PD §18 rejections untouched, no AGPL trace (the `IdMap.java` transformation import explicitly not followed, §0.2/§0.3), tag-expansion "working in production" rejected as 1.12.2 evidence with RESEARCH controlling and an upstream request filed (§11.2.1, §11.4). No Oculus material used.

## 3. Required corrections

### C1 — Conformance-map and §0.1 line citations point at the wrong rows (correction)

- **Sites:** `docs/phase9/v1/PHASE_9_DOC.md:46`–`:48` (§0.1 item 2), `:385`, `:386`, `:394`, `:396`, `:397` (§3 map).
- **Claim:** rows are semantically faithful but their RESEARCH/Phase 6 anchors are off-row against current bytes — exactly the citation class §G1.2 exists to catch.
- **Evidence:** the tag-shim/entries-beat-tags rule is `docs/research/v1/RESEARCH.md:448` ("…ore-dictionary-style shim + Iris's entries-beat-tags priority rule"); `:447` is the Distant Horizons row, cited by both :385 and :386. App E row 13 (RenderManager) is at `:1413`, not `:1410` (TextureMap); row 14 (TileEntityRendererDispatcher) is at `:1414`, not `:1411` (TextureAtlasSprite); §0.1's claimed reading range "Appendix E rows 13–14, lines 1386–1412" ends three lines short of row 13. The held-light contract row (:394) cites `docs/phase6/v1/PHASE_6_DOC.md:497`–`:505`, which is the `UniformRetirementRejection`/`FrameBeginResult` enum region; the exact Phase 6 tuple and old-mode mapping are at `:796`–`:798` and `:809`–`:813`.
- **Failure path:** a fix-up or receiver session re-verifying provenance anchors hits unrelated rows and must re-derive every anchor from scratch; the §0.1 reading-range record misstates what the row-13/14 evidence covers.
- **Owner+receivers:** Phase 9 doc only (author-side citation repair); no dependency change.
- **Minimal resolution:** change `:447`→`:448` (rows :385/:386), `:1410`→`:1413` (:396), `:1411`→`:1414` (:397), the §0.1 range to cover :1387–:1414 (or :1399–:1414), and row :394's Phase 6 anchor to `docs/phase6/v1/PHASE_6_DOC.md:796`–`:798` plus `:809`–`:813`. No semantic text changes required.

### C2 — §5.4/§5.6/§11.4 describe the C2/C3 receiver integration as outstanding when current P7/P10 bytes already contain it (correction)

- **Sites:** `docs/phase9/v1/PHASE_9_DOC.md:1046` (R9-2 remaining gate), `:1058`–`:1074` (§5.6 preamble "Main integrates them into P7/P10"; bullet 1 "replace the convenience-overload hook"; bullet 2 "add owner7 CORE `H7-TE-BATCH-*`"), `:1353`–`:1354` (§11.4 item 3 "Integrate §5.6's corrected P7 dispatch/batch catalog … their prior grants alone did not cover delayed ID draws").
- **Claim:** the amendments §5.6 requests are already adopted in the receivers' current documents; only fresh owner/receiver verification remains.
- **Evidence:** `docs/phase7/v1/PHASE_7_DOC.md:1710` — H-ENTITY-03/H9-BLOCK-ENTITY-ID-01 is already "AROUND/finally on `func_192854_a(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V` … Normal, global, damaged, direct and nested calls use this lower dispatch; no duplicate convenience-overload hook"; `:1718`–`:1720` — owner7 CORE `H7-TE-BATCH-BEGIN`/`FAST`/`DRAW` rows already exist with semantics matching §5.6 (BEGIN "borrow current batch/frame/runtime identity under P9 §4.12.1"; FAST lends the P9 ordinal; DRAW "submit authenticated sorted ranges with fresh P9 IDs and P10 native repeats"); `:1850` App E row 14 already maps to the actual lower dispatch; `:1906`–`:1907` P7 receives the owner10 H10-TE-BATCH rows "individually in v0.3 CORE health"; `:4094`–`:4095` P7 §11.3 records the whole set as receiver-adopted/unverified. `docs/phase10/v1/PHASE_10_DOC.md:1800` consumes §5.1/§4.10 exactly; D-P10-26 receives the deferred-batch protocol.
- **Failure path:** a fix-up session following §11.4 item 3 or R9-2's gate would attempt to "integrate" rows that already exist, risking duplicate catalog entries or a false correction history; the adoption ledger misstates the only outstanding gate.
- **Owner+receivers:** Phase 9 doc (ledger/disposition text). P7/P10 bytes need nothing.
- **Minimal resolution:** restate §5.6 as specified-here/receiver-adopted with pointers to `PHASE_7_DOC.md:1710`, `:1718`–`:1720` and P10 D-P10-26; change R9-2's remaining gate to "fresh Phase 7/9 verification" (receiver adoption present); rewrite §11.4 item 3 to request fresh verification of the adopted rows rather than their integration.

### C3 — Evidence anchors cite reference trees that no longer exist under those names (correction)

- **Sites:** `docs/phase9/v1/PHASE_9_DOC.md:51`–`:56` (§0.1 item 3 Pintonium paths), `:58`–`:65`, `:451`, `:453`, `:474`–`:475`, `:887` (§0.1 item 4 and §4.2/§4.14 Cleanroom paths under `reference-src/cleanroom-0.6.6-alpha/`), with §4.6/§4.8 Pintonium provenance tags reusing the `pintonium-9c2fcc1` prefix (:383–:390).
- **Claim:** the current checkout holds `reference-src/Pintonium-main/` and `reference-src/Cleanroom-0.6.12-alpha/`; neither cited tree name resolves, so those anchors cannot be recomputed against current bytes. (Verified by top-level directory listing only.)
- **Evidence:** `reference-src/` contains exactly `Nothirium-main/`, `Angelica-master/`, `CleanMix-main/`, `Pintonium-main/`, `Cleanroom-0.6.12-alpha/`, `Iris-26.1/`, `schlorbium-HD_U_G6_pre1/`, `Oculus-1.12-1.12.2/`. Mitigations present in the doc: it followed the governing §G0.2 aliases (which themselves still name the old trees), and §0.3 already discloses "Historical 0.6.6-alpha paths above are not claimed recovered or byte-identical" while using 0.6.12-alpha for the correction evidence.
- **Failure path:** every Pintonium/Cleanroom provenance tag in §3/§4 is unresolvable for a fresh reviewer; the mixed 0.6.6/0.6.12 usage leaves the registry-evidence inventory (ForgeRegistries/Loader/EntityEntry/FMLModIdMappingEvent line ranges) without a resolvable anchor.
- **Owner+receivers:** Phase 9 doc; upstream note to the design's §G0.2 alias table is optional (the design file is not modified by this review).
- **Minimal resolution:** in §0.2 record the checkout rename and declare the cited `pintonium-9c2fcc1`/`cleanroom-0.6.6-alpha` paths as aliases of `Pintonium-main/`/`Cleanroom-0.6.12-alpha/`, or re-anchor the load-bearing §4.2/§4.14 Cleanroom citations to the current tree with re-derived line numbers. Mark no content change as implied by the rename.

### C4 — §4.5's ordering rule contradicts §4.6's MODERN-provenance mapping for the ambiguous tokens (correction)

- **Sites:** `docs/phase9/v1/PHASE_9_DOC.md:524` ("Exact registry lookup occurs before compatibility-name expansion", unconditional) vs `:577`–`:580` ("With that provenance, `minecraft:grass` targets `minecraft:tallgrass` and `minecraft:redstone_lamp` targets the lit block") and `:1180`–`:1183` (test 5 expects "selected aliases reach tallgrass/lit_redstone_lamp").
- **Claim:** both `minecraft:grass` and `minecraft:redstone_lamp` exist in the live 1.12 registry, so under §4.5's literal ordering exact lookup always wins and a MODERN-provenance rule could never reach the alias — contradicting §4.6 and the test's own expectation.
- **Evidence:** §4.5 states the ordering with no era exception; §4.6 defines the token-specific override only in prose ("They retain their exact 1.12 meaning unless Phase 3 supplies `MappingEra.MODERN` provenance"), never amending §4.5's pipeline statement; §4.6's closing line (:586) itself demands "producer-backed conditional fixtures must reach it".
- **Failure path:** an implementer following §4.5 literally produces a resolver that fails the doc's own `ambiguousGrassAndLamp_requireModernEraProvenance` fixture, or silently reorders lookup per token without a recorded rule.
- **Owner+receivers:** Phase 9 doc only; P3's MODERN provenance grant is unaffected.
- **Minimal resolution:** add one sentence to §4.5 (or §4.6) stating that a validated era-provenance expansion of an ambiguous token replaces exact-lookup for that selector while the CLASSIC/neutral path keeps exact-1.12 meaning, and that all other selectors keep exact-before-expansion.

## 4. Notes

- **N1 — P4 registry-fingerprint domain naming.** The only RegistryFingerprint-domain citation in the doc is `RegistryFingerprint/own-build-v1` (`PHASE_9_DOC.md:964`, inside the schema21 receipt D-P9-18). D-P9-22 correctly declares D-P9-16…19 "historical only", but the surviving current admission never names the successor domain that P7/P10 carry — `RegistryFingerprint/profile-selection-v3` (P4 D-P4-43; `docs/phase7/v1/PHASE_7_DOC.md:3784`, `:3800`; `docs/phase10/v1/PHASE_10_DOC.md:1821`). No current claim cites own-build-v1 as current, so this is not a stale-identity violation; a one-line addition to D-P9-22 (current domain named, or P4 identity explicitly out of P9's concern) would remove the last ambiguity. No change required for verification.
- **N2 — Web sources as correction evidence.** §0.3 grounds the TE-dispatch and BufferBuilder-sort corrections in two web URLs (upstream Forge patch; a vanilla source mirror). §G1.1 permits web reading only when a listed input is missing or contradictory; the use here is disclosed, narrowly scoped to platform behavior (patch-established dispatch chain, sort permutation), with MCP symbol resolution as the primary channel and explicit non-reproduction statements. Acceptable as recorded; keep future corrections anchored to MCP/cleanroom patches first.

## 5. Areas audited and found sound

- **Lifecycle and publication (§2.2, §4.1, §5.3):** closed candidate/publish/retire state machine; single-borrow publication; generation monotonicity with terminal-overflow shaders-off; retirement only after frame/build borrows drain; no pairing of old ordinal maps with new lookups; coherent with P7's ten-step transaction, worker-drain retention, and invalidation-before-admission.
- **Merge/precedence algebra (§4.4):** the pack→(entries,tags)→per-mod→fallback ladder is the only ordering satisfying both the assignment's unqualified "pack wins over mod" and RESEARCH's entries-before-tags rule; assign-if-absent with attributed conflict diagnostics; layer rules restricted to tiers 1–3.
- **Selector resolution (§4.5) and P3 boundary:** typed-consumption receipt D-P9-23 matches P3 D-P3-73's exact shapes (`Optional<MetadataConstraint>`, interval unions, Literal/IntegerInterval), no reparse/expansion/overmatch; metadata tested directly against captured values; whole-selector failure semantics symmetric with P3's "one rule per selector".
- **Era selection (§4.6–§4.7):** BLOCK/ENTITY dual-era selection is exactly P3's D-P3-72 grant (replace-only-MC_VERSION, ordinary=CLASSIC, alternate=MODERN, PRESENT_EMPTY-only per contribution, no merge/heuristic); modern ambiguous aliases are reachable through real parser bytes; tag shim is a genuine provider design (OreDictionary table, explicit definitions, no camelCase conflation), with the PD production-tag claim properly rejected and reported upstream.
- **Legacy fallback (§4.8):** absent-only activation, mod rules first, live vanilla numeric IDs only, `minecraft:` namespace scoping, Unrepresentable hand-off to §4.10 — re-derives values rather than inheriting Pintonium's curated list (D-P9-6), matching PD §8.1's fallback purpose.
- **Phase 10 alias service (§4.10, §5.1):** bit-exact `renderType<<16 | aliasedBlockId` per RESEARCH App C.2 :1300–:1301 with an explicit representability domain and per-rule diagnostics; allocation-free O(1) lookups; generation-stamped stale-work rejection — mirrors P10 :377–:379, :660–:668, :1800.
- **Held items and hand-light policy (§4.11):** exact P6 tuple and old-mode pair construction (`PHASE_6_DOC.md:796`–`:813`); user-over-pack priority per P3 §5.1/P12; load-time macro projection left to P3 with no macro-presence inference; dynamic-lights suppression-only disposition keeps the non-goal intact.
- **Per-draw scopes (§4.12) and fast-batch protocol (§4.12.1):** hook ledger matches P7's current catalog row-for-row (admission issuance, rejection enum, LIFO tokens, frame-drain backstop, shadow admission without main activation); dispatch coverage reasons from actual RenderGlobal loops; the deferred-batch route preserves Forge sort/batch semantics with owner-homogeneous contiguous ranges, checked permutation observation at `func_181674_a(FFF)V`, and honest gl_PrimitiveIDIn disclosure — consistent with P7 :1718–:1720/:1906–:1910 and P10 D-P10-26.
- **entityColor (§4.13):** operand capture at the `GL_TEXTURE_ENV_COLOR` invocation matches P7's v0.1 rows :1716–:1717 exactly (duplicate buffer, absolute four-float read, original call preserved, nested restore, frame drain); no TexEnv post-hoc query, no second writer, v0.1 ownership preserved with no deferral — satisfying the assignment's REV1 warning and PD §8.2's gap.
- **Failure/degradation (§6):** every row maps to a G2.4 rung correctly (2a feature-level, 2 uniform, 3 owned by P4, 4/5 for scope-balance and pipeline failures); no crash paths; shaders-off always reachable.
- **Threading (§7):** off-thread restricted to parsing/snapshot work from immutable inputs; render-thread-only publication/sampling/scope operations; hot-path allocation bans stated and testable; D-6 seam guard in §8.2.
- **Testability (§8):** headless coverage maps 1:1 onto the design's decision points (precedence permutation, era provenance through real parser bytes, fallback absent-only, tag shim, bit layout, publication ownership, hand policy, scope LIFO, warn-once scoping); impl gate honestly quotes the assignment's two-uniform sentence and adds the in-scope extras explicitly.
- **Milestones (§9):** every component architected now, implemented v0.3, color producer v0.1 reused, catalog growth post-v0.5 gated by named fixtures — consistent with §G0.3/§G4.3 and G5.1.
- **Identity discipline:** schema23/MaterializedSource-v23/projectionVersion=1/nine trees all current; superseded numeric receipts explicitly demoted; P2 wire `/4`, ShadowHookHealth/flattened-v3, TextureHookHealth/application-v2, and phase13 identities either correctly cited by their owners or not cited at all by this doc.

## 6. Verdict

**PASS-WITH-CORRECTIONS.** The document's architecture, scope discipline, dependency honesty, degradation design, licensing posture, and recent-change integration are sound; the four corrections are localized accuracy repairs (five citation anchors, the §5.4/§5.6 receiver-state restatement, evidence-tree re-anchoring, and one resolution-ordering sentence) and do not touch any contract surface. No structural rebuild is required. Per §G1.3, the fix-up applies C1–C4 and records resolutions here; since no §5 contract changes, no additional re-verification round is triggered by this verdict beyond the fix-up itself. State: no build, test, GL, or network execution was performed by this review.

## Resolutions

Recorded 2026-09-08 by the §G1.3 fix-up session. The report body above is byte-identical. The
fix-up touched only `docs/phase9/v1/PHASE_9_DOC.md` (SHA-256 before fix-up `471d6547…e4b2e`,
matching the frozen value; after fix-up `f1a18d42…724593`, 1415 lines). No decision IDs were
added, removed, or renumbered (D-P9-23 remains the maximum); the §0–§12 section sequence and all
fences are intact; no other document, review, design file, or reference tree was modified.

- **C1 — applied.** All five anchors recomputed against current bytes, then repointed: the
  entries-beat-tags/tag-shim row is `RESEARCH.md:448` (the §3 "explicit entries beat tags" and
  "tag expansion" rows now cite `:448`; `:447` is the Distant Horizons row, verified); App E row
  13 (RenderManager) is `:1413` — `:1410` is TextureMap — and row 14 (TileEntityRendererDispatcher)
  is `:1414` — `:1411` is TextureAtlasSprite; the §3 `entityId`/`blockEntityId` rows now cite
  `:1413`/`:1414`; §0.1 item 2's Appendix E reading range now reads "lines 1387–1414"; and the §3
  held-light row's Phase 6 anchor now reads `PHASE_6_DOC.md:796`–`:798` plus `:809`–`:813`
  (verified: `:796`–`:798` is the `HeldItemSample` record and `:809`–`:813` the old-mode pair
  construction; `:497`–`:505` is the `UniformRetirementRejection`/`FrameBeginResult` region). No
  semantic text changed.
- **C2 — applied.** §5.4's R9-2 row now states the §5.6 C2/C3 retarget and deferred-batch
  amendments are receiver-adopted in current P7/P10 with pointers (`PHASE_7_DOC.md:1710`,
  `:1718`–`:1720`; P10 D-P10-26), and its remaining gate is "fresh Phase 7/9 verification of the
  adopted rows; receiver adoption is present, so no integration step remains". The §5.6 preamble
  is restated as specified-here/receiver-adopted, and the H-ENTITY-03 and P7 batch-row bullets are
  restated as receiver-adopted with the same pointers, all §4.12.1 semantics preserved; the
  remaining §5.6 bullets are covered by that preamble. §11.4 item 3 now requests fresh
  verification of the adopted rows rather than their integration. Ground truth confirmed in
  current bytes before editing: `PHASE_7_DOC.md:1710` ("Normal, global, damaged, direct and nested
  calls use this lower dispatch; no duplicate convenience-overload hook"), `:1718`–`:1720` (owner7
  CORE `H7-TE-BATCH-BEGIN`/`FAST`/`DRAW`), `:1850` (App E row 14 on the actual lower dispatch),
  `:1906`–`:1907` (owner10 H10-TE-BATCH rows individually in v0.3 CORE health),
  `PHASE_10_DOC.md:2245` (D-P10-26) and its §11.3 "Coordinated adoption, unverified" row.
  Disclosed one-site extension: the §0 review-1 header paragraph carried the same
  "requiring receiving-owner integration" claim; its one clause was aligned to
  receiver-adopted/verification-remains so the document does not contradict its corrected §5.4–5.6
  (§1.2 already stated receiver adoption; P7/P10 bytes needed nothing).
- **C3 — applied (alias option).** §0.2 gained a checkout-rename ruling that records the rename
  and declares every `reference-src/pintonium-9c2fcc1/**` and
  `reference-src/cleanroom-0.6.6-alpha/**` citation in the document — §0.1 items 3–4 (:52–:66),
  the §3 provenance tags (now :391–:398), §4.2 (:459/:461), §4.3 (:482–:483), and §4.14 (:895) —
  an alias of `reference-src/Pintonium-main/` / `reference-src/Cleanroom-0.6.12-alpha/`, states
  the recorded line numbers are historical readings that are not re-derived, and marks that the
  rename implies no content change. Re-deriving line numbers against the current trees was not
  performed, consistent with this review's top-level-only verification; the historical path names
  are therefore intentionally kept and now resolvable through the alias declaration.
- **C4 — applied in §4.5.** One sentence added directly after "Exact registry lookup occurs
  before compatibility-name expansion." stating the sole exception: for an ambiguous token under
  §4.6, a rule carrying validated `MappingEra.MODERN` provenance replaces exact lookup with its
  alias expansion for that selector, while the CLASSIC/neutral path keeps the token's exact 1.12
  meaning and every other selector keeps exact-before-expansion. §4.6, test 5
  (`ambiguousGrassAndLamp_requireModernEraProvenance`), and P3's MODERN provenance grant are
  unchanged and now mutually consistent.
- **Notes N1/N2 — recorded, not applied.** No correction incorporates either note, so the
  `RegistryFingerprint/profile-selection-v3` naming suggestion (N1) and the web-source posture
  (N2, already disclosed in §0.3) stand as review observations without document change.
