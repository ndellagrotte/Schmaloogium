# Phase 7 verification review — Round 32

## 0. Method and reading order

I independently re-derived every surviving candidate from the complete target at
`docs/phase7/v1/PHASE_7_DOC.md` (1–2441), the manifest-selected `docs/design/v3/DESIGN.md` Part I,
Phase 7 target spec (1832–1986), document gate (1971–1976), and G9 mandatory template (817–855),
RESEARCH ground truth (`docs/research/v1/RESEARCH.md` — §7.1 hook strategy, §11 open questions,
Appendix E/E.1, Appendix F.1), the governing binding regions of Phase 2/3/4/5/6 docs, and the
supporting Pintonium/Cleanroom reference evidence only where a candidate required it. I settled
each candidate's interpretation, severity, and interface classification from those sources before
reading `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through `docs/phase7/reviews/PHASE_7_REVIEW_31.md`,
in order and last, and then checked the candidates against that settled material.

The selected v3 design revision is the supplied verification-only override; it does not rewrite the
target's declared adoption state. I did not read `reference-src/schlorbium-HD_U_G6_pre1/files.txt`,
because the resolved contract forbids every `*.txt` source and no admitted candidate depends on its
bytes; the `files.txt` cross-check claims are corroborated by RESEARCH's own preamble. I also did not
read `reference-src/pintonium-9c2fcc1/...` mixin sources or the Cleanroom `.java.patch` files because
no surviving candidate challenges the injection-timeline or patch-site evidence; the Pintonium
timeline claims are not in dispute this round. There was no network use, forbidden-transcript use,
or agent fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
supplied `verify-loop` instructions required completing only this role without invoking the loop or
delegating.

One candidate was eliminated at the Gate before adjudication (`candidate-004`, unverifiable
evidence against RESEARCH.md:545–546) and receives no finding here. No other candidate was eliminated
before adjudication. Every surviving candidate's core denial was tested byte-level against the
current files; seven distinct defects are admitted, with eight further candidate submissions dropped
as duplicative presentations of those seven (see §1).

## 1. Findings

The surviving candidates resolve into twelve distinct defects. Where multiple candidate IDs
submitted the same location+claim, the retention ID below absorbs them and the duplicate
submissions are recorded as dropped in §2. All straight citation-coordinate defects lie outside the
manifest-declared interface region (PHASE_7_DOC.md:1330–2092) unless stated otherwise.

### F1 — candidate-001 — §4.10 Appendix-E row citations are three lines stale (rows 1–17) and §4.10.8's range omits rows 16–18

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1098` (H-FRAME-01), `:1117` (H-SKY-01), `:1134`–`:1135`
  (H-ENTITY-02/03), `:1169` (H-PARTICLE-01), `:1189` (H-BLEND-01), `:1205` (H-FBO-01), and the
  §4.10.8 ledger at `:1218`–`:1219`.
- **Claim:** Each numbered "App E row N (docs/research/v1/RESEARCH.md:NNNN)" pointer resolves to the
  corresponding Appendix E.1 class-table row, and §4.10.8's completeness range covers the full
  18-row catalog.
- **Evidence (verified verbatim):** `| H-FRAME-01 \`EntityRenderer\` | \`func_175068_a(IFJ)V\` HEAD |
  \`FrameHooks.open(pass, partialTicks, finishTimeNano)\` | \`CORE\`; Pintonium row 1 and App E row 1
  (\`docs/research/v1/RESEARCH.md:1398\`) |` (`:1098`). RESEARCH Appendix E.1 rows physically occupy
  `1401` (row 1), `1402` (row 2), `1413` (row 13), `1414` (row 14), `1415` (row 15), `1416` (row 16),
  `1417` (row 17), `1418` (row 18); `1399` is the table header and `1398` is blank. Every cited
  coordinate is exactly three lines below its row: `:1398`/`:1399` → blank/header, and `:1410`/`:1411`/
  `:1412`/`:1413`/`:1414` land on rows 10–14 (TextureMap, TextureAtlasSprite, ItemRenderer,
  RenderManager, TileEntityRendererDispatcher) instead of rows 13–17. §4.10.8 states
  "This ledger is the completeness proof for Appendix E's class catalog
  (\`docs/research/v1/RESEARCH.md:1396\`–\`:1415\`)" (`:1218`–`:1219`), a range that excludes rows
  16–18 at 1416–1418.
- **Required correction:** Re-point H-FRAME-01 → `:1401`, H-SKY-01 → `:1402`, H-ENTITY-02 → `:1413`,
  H-ENTITY-03 → `:1414`, H-PARTICLE-01 → `:1415`, H-BLEND-01 → `:1416`, H-FBO-01 → `:1417`, and change
  §4.10.8's span to `:1396`–`:1418` (or cite the E.1 table head at `:1397`). Record in `## Resolutions`.
- **Severity:** correction
- **touches interface/change-trigger region: no** — all rows lie in §4.10, outside 1330–2092.
- **Folded duplicate:** candidate-013 (same seven offsets, plus its optional wording to re-derive the
  §4.10.8 span) is dropped as a duplicative presentation of this finding.

### F2 — candidate-002 — §3.7 and U7-3 misattribute RESEARCH's "every class" claim to RESEARCH.md:1390, whose preamble already states the 17-of-18 caveat

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:605`–`:606` (§3.7 intro), `:611` (§3.7 row 17),
  `:2380`–`:2383` (§11.5 U7-3).
- **Claim:** §3.7's assertion that RESEARCH.md:1390 makes an "every class" claim contradicted by
  files.txt, and U7-3's request to narrow the claim at `:1390`–`:1391`, are accurate against
  RESEARCH's current bytes.
- **Evidence (verified verbatim):** `| App E row 17, \`net.minecraft.client.shader.Framebuffer\` |
  **absent** from the complete 112-line list; this contradicts \`docs/research/v1/RESEARCH.md:1390\`'s
  "every class" claim |` (`:611`). RESEARCH `:1390`–`:1392` reads "Seventeen of the eighteen classes
  below also appear in OF's \`files.txt\` replacement list … \`net.minecraft.client.shader.Framebuffer\`
  does not appear in the complete list" — the caveat §3.7 claims to contradict. The uncaveated "Every
  class there also appears in OF's 112-replaced-classes list" sentence is §7.1 at `:823`–`:824`.
  U7-3 requests "narrow the claim at \`docs/research/v1/RESEARCH.md:1390\`–\`:1391\`" (`:2380`–`:2382`),
  a narrowing already in place there.
- **Required correction:** Re-point the §3.7 intro, row 2, and U7-3 to RESEARCH §7.1 `:823`–`:825`;
  note that the Appendix E preamble already states the 17-of-18/Framebuffer-absent qualification so no
  preamble narrowing is owed; redraft U7-3 to target the §7.1 sentence. Record in `## Resolutions`.
- **Severity:** correction
- **touches interface/change-trigger region: no**
- **Folded duplicate:** candidate-018 (same §3.7/U7-3 re-attribution) is dropped as an exact duplicate.

### F3 — candidate-003 — OQ-3/OQ-4 verbatim-question citations are off by one line in §0.3, §10.1, and §10.2

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:84` (§0.3 item 3), `:2253` (§10.1), `:2286` (§10.2).
- **Claim:** The quoted OQ-3/OQ-4 rows are cited at RESEARCH.md:1008–1009 / :1008 / :1009.
- **Evidence (verified verbatim):** `3. The exact OQ-3/OQ-4 rows at
  \`docs/research/v1/RESEARCH.md:1008\`–\`:1009\` were read to satisfy` (`:84`); §10.1 cites
  `(\`docs/research/v1/RESEARCH.md:1008\`)` for OQ-3 (`:2253`) and §10.2 cites
  `(\`docs/research/v1/RESEARCH.md:1009\`)` for OQ-4 (`:2286`). RESEARCH's §11 table places OQ-2 at
  `:1008`, OQ-3 at `:1009`, OQ-4 at `:1010`; the verbatim question text itself is correct for the
  intended rows.
- **Required correction:** §0.3 → `:1009`–`:1010`; §10.1 → `:1009`; §10.2 → `:1010`. Record in
  `## Resolutions`.
- **Severity:** correction
- **touches interface/change-trigger region: no**
- **Folded duplicate:** candidate-014 (identical OQ off-by-one, same three cites) is dropped as an
  exact duplicate.

### F4 — candidate-005 — Five dependency-document citation groups are stale in §3.5, §3.6, §4.1, §4.3, and §8.3

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:580`–`:591` (§3.5 engine-flag rows + precipitation
  handoff), `:600` (§3.6 virtual-pre row), `:710` (§4.1 Phase 5 ordering), `:779`–`:780` (§4.3 Phase 6
  rejection), `:2203`–`:2204` (§8.3 Phase 2 v0.1 mapping).
- **Claim:** The cited Phase 2–6 coordinates point at the supporting content they name; none do.
- **Evidence (verified verbatim):**
  - §3.5 flag rows cite `docs/phase3/v1/PHASE_3_DOC.md:477` and `:482`–`:493` and the precipitation
    handoff at `:536`. Phase 3 `:477` is `List<PropertyPredicate> propertyPredicates,` inside the
    MappingRule record, `:482`–`:493` the same mapping-record block, and `:536` is a blank line above
    `public record DeclaredUniformCatalog(` (`:537`). The Appendix F.1 field/owner table occupies
    `:686`–`:704` (`clouds`=688, `underwaterOverlay`=693, `sun`=694, `moon`=695, `vignette`=696,
    `backFace.*`=697–700, `rainDepth`=701, `beaconBeamDepth`=702, `frustumCulling`=704) and the F.6
    precipitation handoff row is at `:747`.
  - §3.6 row 4 cites `docs/phase4/v1/PHASE_4_DOC.md:663`–`:666` for "*_pre virtual with no program".
    Phase 4 `:663`–`:666` is barrier/StalePublication prose; the virtual-`*_pre` VIRTUAL_FLIP_CONTROL
    ruling is at `:456`–`:459` (and invariant 5 at `:366`–`:367`).
  - §4.1 anchors "The Phase 5 ordering is binding at
    \`docs/phase5/v1/PHASE_5_DOC.md:1781\`–\`:1785\`" (`:710`). Phase 5 `:1781`–`:1785` is
    §4.11.1 supersampling-extent prose; the ordered
    `FRAME_BEGUN -> PRE_WEATHER_COPIED -> PRE_TRANSLUCENT_COPIED -> FRAME_COMMITTED` protocol is at
    `:1517`–`:1524`, the §5.1 publication-ordering row (BufferEstateCandidate/Publisher, "composes and
    publishes Phase 4 first … permits no shader draw until both publications complete") at `:2003`, and
    the §5.1 copyDepth row ("caller owns the ordered `FRAME_BEGUN -> PRE_WEATHER -> PRE_TRANSLUCENT ->
    FRAME_COMMITTED` moments") at `:2011`.
  - §4.3 step 1 cites `docs/phase6/v1/PHASE_6_DOC.md:754`–`:759` for the reject/reacquire rule. Phase 6
    `:754`–`:759` is the gbuffer/shadow matrix uniform table; the
    `REJECTED_STALE_FRAME`/`REJECTED_GENERATION` "must abandon the shader draw and reacquire the
    current publication" contract is at `:869`–`:873` (and `:517`–`:521`, §5.1 row `:1384`).
  - §8.3 cites `docs/phase2/v1/PHASE_2_DOC.md:417`–`:422` for "exactly as Phase 2 maps v0.1". Phase 2
    `:417` is the `## 3. Contract conformance map` heading; the v0.1 milestone→run rows
    (`RUN-T1-APPROVE`/`RUN-T1-REGRESS` and `RUN-T0[classic × all scenes]`) are at `:509`–`:510`.
- **Required correction:** Re-point §3.5 flag rows to Phase 3 `:686`–`:704` (per-row as listed) and the
  precipitation handoff to `:747`; §3.6 row 4 to Phase 4 `:456`–`:459`; §4.1 to Phase 5 `:1517`–`:1524`
  (or the §5.1 rows `:2003`/`:2011`); §4.3 to Phase 6 `:869`–`:873`; §8.3 to Phase 2 `:509`–`:510`.
  Record in `## Resolutions`.
- **Severity:** correction
- **touches interface/change-trigger region: no** — every cite is in §3/§4/§8, outside 1330–2092, and
  the §5.2 binding inventories (which the earlier rounds re-pinned) target different, still-correct
  rows.
- **Folded duplicates:** candidate-012 (the §3.5 leg, incl. its per-row precision), candidate-010 and
  candidate-016 (the §4.1 Phase-5-ordering leg, incl. the `:2003` vs `:2011` refinement), candidate-017
  (the §4.3 Phase-6 leg), and candidate-019 (the §8.3 Phase-2 leg) are dropped as duplicative
  presentations of this composite finding; their precision is folded into the required correction.

### F5 — candidate-006 — Closing status "Twenty-one review rounds ended in PASS before §0.25" over-counts literal-PASS rounds

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:2438` (closing status).
- **Claim:** Asserting that twenty-one review rounds ended in PASS before §0.25 is consistent with the
  document's own §0.5–§0.25 fix-up record.
- **Evidence (verified verbatim):** `*End of PHASE_7_DOC.md. Twenty-one review rounds ended in PASS
  before §0.25. Corrections through` (`:2438`). The addenda record: rounds 1–18 each carry a
  "Round-k fix-up" that "corrects …" (§0.5–§0.22); §0.23 records "round nineteen's literal PASS applies
  only to the pre-§0.23 bytes"; §0.24 ("Round 20 corrects the two active Phase 9 ID-row counts");
  §0.25 records "Round twenty-one subsequently returned literal PASS with zero findings". Before §0.25
  there were 21 rounds total, but exactly two (19 and 21) ended in literal PASS; the other nineteen
  carried corrections.
- **Required correction:** Split count from attribution, e.g. "Twenty-one review rounds preceded
  §0.25; only rounds nineteen and twenty-one returned literal PASS among them, corrections being
  applied for rounds 1–18 and 20", while keeping the trailed operative claims (corrections through
  Round 31 applied, Round 31 most recently changed binding §5, v1 unverified, no roll). Record in
  `## Resolutions`.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the closing note is outside §5.

### F6 — candidate-008 — §5.1 `UniformSignal` payloads cannot be mapped onto Phase 6's binding `UniformEventSink` samples without guessing

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1724`–`:1726` (UniformSignal records) and `:1896`
  (exposed-contract row).
- **Claim:** The exposed `UniformSignalBridge` — promised to "map frame/camera/celestial/fog/blend
  signals to Phase 6's \`UniformEventSink\` without resampling" — is implementable against Phase 6's
  binding sink without guessing.
- **Evidence (verified verbatim):** §5.1 declares `record Celestial(float sunAngle, float shadowAngle,
  float rainStrength)`, `record Fog(float start, float end, ColorValue color)`, and `record
  Blend(BlendStateValue state)` (`:1724`–`:1726`); the exposed row reads "\`UniformSignalBridge\` |
  maps frame/camera/celestial/fog/blend signals to Phase 6's \`UniformEventSink\` without resampling;
  Phase 9 delivers held/entity/TE/color through authenticated \`PerDrawDynamics\`" (`:1896`). Phase 6's
  binding sink takes `updateCelestial(CelestialSample)`, `updateFog(FogSample)`, `updateBlend(
  BlendSample)` (`:378`–`:383`) with `CelestialSample(worldEpoch, frameId, Float3 sunPosition,
  Float3 moonPosition, Float3 shadowLightPosition, Float3 upPosition)`, `FogSample(worldEpoch,
  frameId, int fogMode, float density, Float3 color)`, and `BlendSample(worldEpoch, frameId, boolean
  enabled, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha)` (`:593`–`:602`). The declared payloads
  share only color with the fog sample; fogMode/density are categorically absent (Phase 7's own
  H-FOG-01 observer captures them at `:1190` yet the record drops them), `BlendStateValue` is an
  opaque undeclared carrier with no stated path to the four blend factor ints, and no text names the
  source of the celestial position vectors that `updateCelestial` requires (Phase 6 §4.12 defers those
  to Phase 8/v0.2). This is a binding-data gap an adapter implementer must fill by guessing.
- **Required correction:** Reshape the three `UniformSignal` variants to the applicable Phase 6
  binding sample shapes (carry world/frame identity from `FrameBeginSignal`; source celestial vectors
  from the Phase 8 publication per Phase 6:1153 or declare an explicit vector carrier; give fog the
  fogMode/density fields H-FOG-01/H-FOG-02 already produce; either define `BlendStateValue` with the
  `BlendSample` fields or drop the record in favour of Phase 6's own type), and restate the one-to-one
  mapping in the §5.1 row — or explicitly document the reduction and its data sources so an adapter is
  not left to guess. This changes the §5 interface region.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — both the declarations and the exposed row sit
  inside the manifest-declared interface region 1330–2092, and the corrective reshaping changes §5.

### F7 — candidate-009 — §5.1 exposed contracts reference undeclared types `AnaglyphEye` and `BlendStateValue`

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1355`–`:1356` (FrameBeginSignal), `:1501`–`:1502`
  (FrameRenderPort.bind), `:1680`–`:1681` (FinalizedFrame), `:1726` (UniformSignal.Blend).
- **Claim:** Every type used by the exposed contracts is declared in §5.1 or is a verified Phase
  1/3/4/5/6 binding type.
- **Evidence (verified verbatim):** `Extent2i targetView, Extent2i priorCompletedFramebuffer,
  AnaglyphEye eye) {}` (`:1356`); `PortResult bind(PassDrawTarget target, AnaglyphEye eye);`
  (`:1502`); `Extent2i extent, AnaglyphEye eye, FrameReadiness readiness) {}` (`:1681`);
  `record Blend(BlendStateValue state) implements UniformSignal {}` (`:1726`). A repo-wide search of
  `docs/` finds `AnaglyphEye` only at `:1356`/`:1502`/`:1681` and `BlendStateValue` only at `:1726` —
  all usage sites, no declaration; the §5.1 allowed-value carve-out names only "closed enums, and
  verified Phase 1/3/4/5/6 types" (`:1823`) and neither type matches any Phase 1/3/4/5/6 carrier
  (Phase 1's blend carrier is `BlendState`, Phase 6's is `BlendSample`). Glue constructing
  `FrameBeginSignal`/`UniformSignal.Blend`, the Phase 2 capture boundary reading `FinalizedFrame`, and
  a `FrameRenderPort.bind` implementer must guess both closed domains.
- **Required correction:** Declare `AnaglyphEye` (closed enum over the two anaglyph eyes with their
  color-mask semantics, engine-owned vs glue-local made explicit) and define or replace
  `BlendStateValue` (closed carrier mirroring the Phase 6 `BlendSample` fields, or reference that
  binding type by its exact name) inside §5.1 beside the contracts that use them. This changes the §5
  interface region.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — all usage sites are inside 1330–2092 and the
  corrective declarations change §5.

### F8 — candidate-007 — §4.12's "all eight §4.13 rows" is an unqualified cross-document reference to Phase 8's §4.13

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1286` (§4.12), `:1305` (§4.13 heading), Phase 8
  `docs/phase8/v1/PHASE_8_DOC.md:986`–`1001` (ledger).
- **Claim:** "all eight §4.13 rows" resolves inside this document, whose own §4.13 contains the eight
  Phase 8 hook-health rows.
- **Evidence (verified verbatim):** §4.12 says "all eight §4.13 rows in hook-ID order with their exact
  expected/actual counts" (`:1286`); Phase 7's own `### 4.13 Capture agent and manifest boundary`
  (`:1305`) contains no hook rows; the eight rows (H8-SLOT-01 … H8-FORGE-01) live in Phase 8's `### 4.13
  Additional hook and accessor ledger` (`PHASE_8_DOC.md:986`), which is the intended but unnamed target.
  Phase 7's §5.4 uses bare "4.12–4.13" for its own sections (`:2075`), so the document-local convention
  makes an unqualified "§4.13" point at the capture-agent section.
- **Required correction:** Qualify the pointer, e.g. "all eight rows of Phase 8's §4.13 ledger"
  (matching the wording Phase 8's R8-5 request already uses at `PHASE_8_DOC.md:1087`); optionally
  mirror in §5.4/§11.5. No content or count change is needed.
- **Severity:** note
- **touches interface/change-trigger region: no**

### F9 — candidate-011 — §3.2's Phase 4 provenance for the overlay-requested-state warning points at alpha/blend-lock prose

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:538`–`:540` (§3.2 closing note).
- **Claim:** The conformance-map provenance points at the location carrying the no-overlay warning.
- **Evidence (verified verbatim):** §3.2 reads "Phase 4 publishes the entire mapping and warns Phase 7
  not to overlay requested-slot state on the effective provider
  (\`docs/phase4/v1/PHASE_4_DOC.md:1383\`–\`:1385\`)" (`:540`). Phase 4 `:1382`–`:1385` is §4.10
  alpha/blend-lock prose ("\`releaseToFixedFunction\` restores any lock before binding fixed function");
  the prohibition sentence "Phase 7 must not re-resolve backup chains or overlay requested-slot state
  on the effective provider" is §5.1 at `:1579`–`:1580`, already cited correctly as `:1576`–`:1580`
  in Phase 7's own §5.2 (`:1952`–`:1954`).
- **Required correction:** Re-point the §3.2 parenthetical to `docs/phase4/v1/PHASE_4_DOC.md:1579`–`:1580`
  (or mirror `:1576`–`:1580`), or delete it as duplicative of the §5.2 citation.
- **Severity:** note
- **touches interface/change-trigger region: no** — the contract is already consumed correctly in the
  binding §5.2 citation, so this is a redundant mis-coordinate confined to §3.2.

### F10 — candidate-020 — §3.5 clouds row (and §0.3 note) cite the video-setting-precedence at RESEARCH:1439–1445, two lines short of the sentence at :1448

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:580` (§3.5 clouds row), `:82`–`:83` (§0.3 note).
- **Claim:** RESEARCH:1439–1445 contains the "corresponding in-game video setting wins" sentence.
- **Evidence (verified verbatim):** `clouds` row provenance "precedence at
  \`docs/research/v1/RESEARCH.md:1439\`–\`:1445\`" (`:580`) and §0.3 "The source says the corresponding
  video setting wins where one exists (\`docs/research/v1/RESEARCH.md:1439\`–\`:1445\`)" (`:82`–`:83`).
  RESEARCH `:1442` is the `### F.1` header, `:1444`–`:1447` the flag list, and the operative sentence
  "— corresponding in-game video settings win where both exist." is `:1448`, outside the cited range.
- **Correction/precision fix:** Extend both citations to `:1442`–`:1448` (or pin `:1448`).
- **Severity:** note
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas survived independent re-derivation. Round 31's fix-up sites remain
self-consistent: the §1.1 item 8 qualifier names the outstanding gates (R7-8 package placement, the
Phase 3/R7-9 reverification condition) consistently with §5.4/§9.1/§12/§11.3, and the §5.1 exact
exposed-contract table ends with the resize-observation family row, matching the §5.1 code block and
§0.34. The §5.2 binding inventories (Phase 2 :1597–1606, Phase 3 :1403–1418, Phase 4 :1560–1580,
Phase 5 :2002–2018, Phase 6 :1382–1390) all still contain their consumed rows — this is why the
§4.x micro-coordinate defects in F4 survived unnoticed: the §5.2 consumption is correct while the
§3/§4/§8 provenance pointers are mis-cited. The Pintonium timeline and the Cleanroom patch sites
cited by §3.4/§3.6 are not challenged by any candidate and were consistent on inspection. The §0.x
addenda record (§0.5–§0.34) matches the review-file history for rounds 1–31 read last, with no
addendum touching F1–F10's coordinates or types.

Dropped on derivation (final_severity none), each an exact/subset duplicate folded into an admitted
finding: candidate-010 and candidate-016 (duplicates of F4's Phase-5-ordering leg), candidate-012
(duplicate of F4's §3.5 leg), candidate-013 (duplicate of F1), candidate-014 (duplicate of F3),
candidate-015 (decomposes into F4's §3.6 leg and F9), candidate-017 (duplicate of F4's §4.3 leg),
candidate-018 (duplicate of F2), candidate-019 (duplicate of F4's §8.3 leg). candidate-004 was
eliminated at the Gate (unverifiable evidence) and receives no finding.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=7; notes=3
Interface changed: yes

Seven admitted corrections and three notes, zero blocking findings. No finding requires structural
rebuilding; every correction is a bounded citation-coordinate or §5.1 typing repair. Corrections this
round rise from Round 31's two because the fresh whole-document surface exposes (a) systematic stale
provenance pointers against immutable RESEARCH and dependency bytes that earlier focused rounds never
re-resolved at the line, and (b) two §5.1 contract-typing gaps in the signal-mapping surface. Because
candidate-008 and candidate-009 change the manifest-declared §5 cross-phase-interface region, a fresh
whole-document and interface verification round is required before Phase 7 can close; the interface
change trigger is engaged. The next required action is a scoped fix-up of candidate-001, candidate-002,
candidate-003, candidate-005, candidate-006, candidate-008, and candidate-009, recording resolutions
under this review and adding a target addendum, with the three notes (candidate-007, candidate-011,
candidate-020) recorded but not ordered for fix-up. No version roll may occur until the loop exits.

## Resolutions

Fix-up session, 2026-08-08. Every admitted coordinate was re-derived against the current files
before any edit; all seven corrections and all three notes confirmed exactly as §1 states
(RESEARCH E.1 rows at 1401–1402 and 1413–1418 with the table head at 1399; the uncaveated §7.1
sentence at 823–825 versus the already-qualified Appendix E preamble at 1390–1392; OQ-3/OQ-4 at
1009/1010; Phase 3's field/owner table at 686–704 and precipitation handoff at 747; Phase 4's
virtual-`*_pre` ruling at 456–459; Phase 5's ordered protocol at 1517–1524; Phase 6's
reject/reacquire rule at 869–873; Phase 2's v0.1 milestone rows at 509–510; Phase 6's sink methods
at 378–383, samples at 593–602, `Float3` at 607, and the full-int-domain rule at 614–615). All
edits are confined to `docs/phase7/v1/PHASE_7_DOC.md`; a compact `§0.35` addendum records the
round, and the header's last-revised pointer now names §0.35.

### F1 — applied

§4.10 re-pointed: H-FRAME-01 → `:1401`, H-SKY-01 → `:1402`, H-ENTITY-02 → `:1413`, H-ENTITY-03 →
`:1414`, H-PARTICLE-01 → `:1415`, H-BLEND-01 → `:1416`, H-FBO-01 → `:1417`, and §4.10.8's
completeness span extended to `docs/research/v1/RESEARCH.md:1396`–`:1418`. Swept for other App-E
coordinate citations: H-ENTITY-01 and H-WORLD-01 cite rows 2/18 without coordinates and were
already correct; no other stale offsets exist.

### F2 — applied

§3.7's intro now attributes the blanket claim to RESEARCH §7.1
(`docs/research/v1/RESEARCH.md:823`–`:825`); the Framebuffer row's contradiction now cites that
same range instead of `:1390`; U7-3 is redrafted to target the §7.1 sentence and records that
Appendix E's own preamble (`docs/research/v1/RESEARCH.md:1390`–`:1392`) already states the
17-of-18 qualification, so no preamble narrowing is owed.

### F3 — applied

§0.3 item 3 → `:1009`–`:1010`; §10.1 (OQ-3) → `:1009`; §10.2 (OQ-4) → `:1010`.

### F4 — applied

§3.5 rows re-pointed per-row into Phase 3's field/owner table (`clouds`→`:688`,
`backFace.*`→`:697`–`:700`, `underwaterOverlay`→`:693`, `sun`/`moon`→`:694`–`:695`,
`vignette`→`:696`, `rainDepth`→`:701`, `beaconBeamDepth`→`:702`, `frustumCulling`→`:704`) and the
precipitation handoff to `:747`; §3.6 row 4 → Phase 4 `:456`–`:459`; §4.1 → Phase 5
`:1517`–`:1524`; the rejection sentence → Phase 6 `:869`–`:873`; §8.3 → Phase 2 `:509`–`:510`.
Location note: the finding labels the Phase 6 leg "§4.3 step 1"; the cited bytes actually sit in
§4.2's "`open` rejects …" paragraph (then `:780`, now `:790`). The bytes and the required
correction were exactly as stated; only the section label differed.

### F5 — applied

The closing status now reads "Twenty-one review rounds preceded §0.25; only rounds nineteen and
twenty-one returned literal PASS among them, corrections being applied for rounds 1–18 and 20."
The trailed operative claims advance with this fix-up as the standing convention requires:
corrections through Round 32 are applied, Round 32 most recently changed binding §5, v1 remains
unverified pending a fresh whole-document review, and no version roll occurs until the loop exits.

### F6 — applied (changes the §5 interface region)

The `UniformSignal` variants now carry exactly the non-identity fields of Phase 6's binding
samples: `Celestial(Float3 sunPosition, Float3 moonPosition, Float3 shadowLightPosition, Float3
upPosition)`, `Fog(int fogMode, float density, Float3 color)`, `Blend(BlendStateValue state)`. A
new §5.1 paragraph documents the one-to-one mapping onto `updateCelestial`/`updateFog`/`updateBlend`
(`docs/phase6/v1/PHASE_6_DOC.md:378`–`:383`), identity copied from the frame's accepted
`FrameBeginSignal`, the Phase 8 publication as the source of the four celestial vectors
(`docs/phase6/v1/PHASE_6_DOC.md:1153`) with no `Celestial` emission at v0.1, and the two
reductions: `sunAngle`/`shadowAngle`/`rainStrength` are Phase 6 frame/tick-provider samples
(`docs/phase6/v1/PHASE_6_DOC.md:561`–`:562`, `:566`–`:570`), and H-FOG-01's observed fog start/end
are not forwarded because `FogSample` carries no start/end
(`docs/phase6/v1/PHASE_6_DOC.md:598`–`:599`). The `UniformSignalBridge` exposed-contract row
restates the mapping. Neighbor sweep: H-SKY-02's "publish the returned celestial angle" was the
only formulation made inconsistent by the reshape; the row now invokes the §5.1 `Celestial` update
at that rotation moment and returns the angle unchanged (§4.10, outside the interface region).
H-FOG-01/H-FOG-02's action cells needed no change.

### F7 — applied (changes the §5 interface region)

`AnaglyphEye { LEFT, RIGHT }` is declared in §5.1 beside `FrameBeginSignal` as a closed `:engine`
enum; the new paragraph states that glue derives the active eye from vanilla's per-pass state,
that `FrameRenderPort.bind` applies that eye's color mask through the Phase 1 facade, and that the
single non-anaglyph pass carries `LEFT`. `BlendStateValue` is defined beside `UniformSignal.Blend`
mirroring `BlendSample`'s non-identity fields exactly, with glue translating the observed
`SourceFactor`/`DestFactor` enums into Phase 6's full-int-domain factor encoding
(`docs/phase6/v1/PHASE_6_DOC.md:614`–`:615`). The now-unused `ColorValue` declaration was removed;
no phase document references it.

### Notes applied

Follow-on fix-up session, 2026-08-08, recorded under `§0.36`. §3's adjudication ordered only the
seven corrections and recorded the three notes "but not ordered for fix-up"; this session elected
to apply them anyway rather than leave known-real defects for a later round to re-find. That was a
fix-up decision, not an adjudication order — the verdict, its counts, and the ordered-action list
in §3 stand unchanged. Each note's coordinates were re-derived against the current files before
the edit, and all three edits lie outside the manifest-declared interface region, so this round
does not re-engage the change trigger.

- **F8 (candidate-007), §4.12's unqualified "§4.13" pointer** — applied. The sentence now reads
  "all eight rows of Phase 8's §4.13 ledger", matching the wording Phase 8's R8-5 already uses at
  `docs/phase8/v1/PHASE_8_DOC.md:1087`. No content or count changed. Swept the document's other
  §4.1x references: §5.4 and §11.5's "4.12–4.13" name Phase 7's own granting sections and are
  correct unqualified, so neither was touched.
- **F9 (candidate-011), §3.2's Phase 4 provenance parenthetical** — applied. Re-pointed to
  `docs/phase4/v1/PHASE_4_DOC.md:1579`–`:1580`, the exact prohibition sentence, rather than
  deleted; §3.2 keeps its own provenance and the binding §5.2 citation's broader
  `:1576`–`:1580` is unchanged.
- **F10 (candidate-020), RESEARCH `:1439`–`:1445` vs the `:1448` sentence** — applied. Both
  citations (§0.3 item 2 and the §3.5 `clouds` row) now read
  `docs/research/v1/RESEARCH.md:1442`–`:1448`, spanning the `### F.1` header at `:1442`, the flag
  list at `:1444`–`:1447`, and the operative sentence at `:1448`.

**Downstream coordinate drift caused by this round.** Inserting `§0.36` at `:301` shifts every
line below it in `PHASE_7_DOC.md` by **+10**. Documents that cite Phase 7 by line number are
therefore off by ten from `:301` down. Those citations are not edited here — `AGENTS.md:155`
forbids this session from modifying another phase's doc — so they are recorded for their owners:
`docs/phase13/v1/PHASE_13_DOC.md` (`:53`, `:127`, `:357`–`:358`, `:909`, `:916`, `:929`, `:937`,
`:944`, `:956`, `:1059`, `:1292`) was built 2026-08-08 against post-§0.35 coordinates and was
accurate until this round; its `:78` cite of `PHASE_7_DOC.md:19` is above the insertion and stays
correct. `docs/phase8/v1/PHASE_8_DOC.md` (`:183`, `:449`, `:1076`) and
`docs/phase9/v1/PHASE_9_DOC.md` (`:796`–`:798`) were **already stale before this round** — spot-
checked at HEAD, `PHASE_9_DOC.md:796`'s "explicit Phase 9 hand-off" at `:1619`–`:1625` resolves to
`ActiveWorldIdentityPublication`, and `PHASE_8_DOC.md:1076`'s shadow-invocation context at
`:1178`–`:1190` resolves to the particle hook table — so earlier fix-up rounds drifted them and
this round is not their cause. Line-cited references into a doc under active fix-up go stale on
every addendum; they are re-derived by the citing phase's own session, not by this one.

The seven corrections above (F1–F7) were confirmed still applied and byte-correct against the
current sources before this session began; none required re-work. Phase 7 v1 remains unverified,
for the reason §0.35 and the closing status already state: Round 32's F6/F7 changed binding §5, so
a fresh whole-document verification round is owed before the phase can close. This notes fix-up
does not discharge it.

### Interface/change-trigger disclosure

The F6/F7 edits intentionally change the manifest-declared §5 cross-phase-interface region
(declared at `docs/phase7/v1/PHASE_7_DOC.md:1330`–`:2092` before this fix-up): the `AnaglyphEye`
declaration after `FrameBeginSignal`, the reshaped `UniformSignal` variants, the new
`BlendStateValue` declaration, the removal of `ColorValue`, the new mapping/identity/reduction
paragraph, and the restated `UniformSignalBridge` exposed-contract row. The change trigger
declared in `verification/targets/phase-7.json` is engaged, and §0.35 plus the closing status
record that a fresh verification round is required before Phase 7 can close. F1–F5 touch only §0,
§3, §4, §8, §10, §11, and the closing note — all outside the interface region.
