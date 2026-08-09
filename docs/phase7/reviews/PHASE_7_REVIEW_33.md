# Phase 7 Review — Round 33

## 0. Method and reading order

Sole adjudicator for round 33 of `phase-7`. Reading order followed as instructed:

1. Target `docs/phase7/v1/PHASE_7_DOC.md` at the cited sites (§3.1, §3.3, §3.5, §3.6, §4.5, §4.10,
   §5.1, §5.2, §5.3).
2. Authority `docs/design/v3/DESIGN.md` (Part I, Phase 7 target spec, doc gate, mandatory
   template) and contract ground truth `docs/research/v1/RESEARCH.md` (§4.4 frame flow lines
   529–566; §7.1 hook needs 803–820).
3. Dependencies: `docs/phase3/v1/PHASE_3_DOC.md` (§3.1 flag ownership 688–713, F.6 row 757,
   §4.10 prose 1399–1418, §5.1 binding rows 1420–1452), plus Phase 2/4/5/6 binding regions as
   needed.
4. Only after independent re-derivation, the 32 prior reviews in `docs/phase7/reviews`
   (adjudicator-last), consulted for settled dispositions.

No network use. No subagent fan-out from this session; the candidate set was produced upstream by
the canonical engine. No Gate drops and no pre-adjudication eliminations were reported. Forbidden
sources (`docs/**/chatlogs/**`, `*.txt`) were not read; the finder's note that `files.txt`-backed
§3.7 rows were left unresolved is accepted as correct scope discipline.

Deviation of note: Round 25 and Round 32 both recorded `docs/phase3/v1/PHASE_3_DOC.md:1403`–`:1418`
as containing Phase 3's consumed binding rows. Direct re-read shows that range is §4.10
fingerprint/debug-dump/publication prose and that `## 5. Cross-phase interfaces` begins at line
1420. That prior clearance was mistaken on current bytes and does not settle candidate-004.

## 1. Findings

### Finding 1 (candidate-002) — H-TERRAIN-02 gates the PRE_TRANSLUCENT copy on a Phase 5 correction the document records as granted

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1141` (§4.10 catalog, H-TERRAIN-02 health cell).
- **Claim:** the row's condition "copy remains disabled until Phase 5 order is corrected" is the
  operative gate.
- **Evidence:** §3.6 line 618 records the ordered `FRAME_BEGUN -> PRE_WEATHER -> PRE_TRANSLUCENT ->
  FRAME_COMMITTED` protocol as *granted*; §12 row R7-1 (line 2101) marks it granted and consumed by
  §4.5/§5.2; §4.5 line 916 gates the copy on the v0.5 milestone; Phase 5's binding contract
  (`docs/phase5/v1/PHASE_5_DOC.md:2011`) publishes the corrected order. No passage preserves a
  pending Phase 5 ordering defect, so the stated gate can never clear.
- **Severity:** correction. This is a `CORE` row whose stated blocking condition is false on the
  document's own record; the row is the sole surviving statement of it.
- **Touches interface/change-trigger region:** no (line 1141 is outside 1350–2134).
- **Fix:** replace the trailing clause with the v0.5 gate, e.g. "`CORE`; Pintonium row 5; the actual
  copy is enabled at v0.5 on Phase 5's granted PRE_WEATHER -> PRE_TRANSLUCENT order".

### Finding 2 (candidate-003) — Phase 7 consumes "schema-v3 `IdMappingInput`" but Phase 3 publishes schema-v4

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1971` (§5.2 Phase 3 row) and `:2037` (§5.3 step 1).
- **Claim:** each consumed dependency contract matches the dependency's manifest-selected binding
  region.
- **Evidence:** Phase 3 §5.1 row at `docs/phase3/v1/PHASE_3_DOC.md:1443` (inside the manifest
  region 1420–1689) publishes "schema-v4 per-kind `ABSENT`/`PRESENT_EMPTY`/`PRESENT_RULES`";
  Phase 3 §4 prose at `:1303` and capability P3-C15 at `:1904` corroborate schema-v4 as the only
  published version. Phase 7 names schema-v3 at both sites and nowhere qualifies it. The Round-26
  alignment to v3 (noted at target line 259) is now stale against Phase 3's current bytes.
- **Severity:** correction. A consumer would validate equality against a version the producer does
  not publish; Phase 7 only passes the value through unparsed, so it is not blocking.
- **Touches interface/change-trigger region:** yes (both lines inside 1350–2134).
- **Fix:** change both occurrences to schema-v4.

### Finding 3 (candidate-004) — the Phase 3 "binding" citation points outside Phase 3's §5 region

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1977`–`:1979` (§5.2).
- **Claim:** each consumed dependency contract is anchored to the dependency's binding region.
- **Evidence:** the anchor is `docs/phase3/v1/PHASE_3_DOC.md:1403`–`:1418`; direct read shows that
  range is §4.10 canonical-encoding/debug-dump/publication prose ending at 1418, with
  `## 5. Cross-phase interfaces` at 1420 and the consumed rows running from `:1429`
  (`PackFrontEnd.discover`) through `:1444` (`InternalPackSource`). None of the seven consumed rows
  is resolvable at the cited anchor, which the target declares "binding".
- **Severity:** correction. The consumed rows are individually named and locatable, so no semantics
  are misstated, but a declared binding anchor that resolves to non-interface prose fails
  traceability.
- **Touches interface/change-trigger region:** yes (line 1977 inside 1350–2134).
- **Fix:** re-point to Phase 3 §5.1's consumed rows within 1420–1689 (≈`:1429`–`:1444`), taking the
  end line from the last consumed row.

### Finding 4 (candidate-005) — §3.5 engine-flag rows cite Phase 3 coordinates that resolve to other phases' rows

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:599`–`:610`.
- **Claim:** each row's provenance coordinate identifies the Phase 3 field/owner row it names.
- **Evidence:** the true Phase 3 rows are clouds 697, underwaterOverlay 702, sun 703, moon 704,
  vignette 705, backFace.* 706–709, rain.depth 710, beacon.beam.depth 711, frustum.culling 713, and
  the F.6 precipitation rule 757. §3.5 cites 688 (a section heading), 693–696, 697–700, 701, 702,
  704 and 747 — a uniform nine-line shift. The consequences are not merely imprecise: the backFace
  citation `:697`–`:700` resolves to `clouds`/`oldHandLight`/`dynamicHandLight`/`oldLighting`
  (Phase 7/9/9/10), `rainDepth`'s `:701` resolves to `shadowTranslucent` (Phase 8), and
  `frustumCulling`'s `:704` resolves to `moon`; the precipitation `:747` resolves to a custom-texture
  row. Nothing elsewhere in the target re-points these coordinates.
- **Severity:** correction. The behavioral text is accurate and matches Phase 3's ownership table;
  the provenance column misidentifies rows and, in several cases, contradicts the owner phase.
- **Touches interface/change-trigger region:** no (lines 597–610 outside 1350–2134).
- **Fix:** re-point each coordinate to the rows listed above and the precipitation handoff to 757.

### Finding 5 (candidate-001) — §5.1 says the bridge copies `frameId` from `FrameBeginSignal`, which has no such component

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1763`–`:1764`.
- **Claim:** the Round-32 bridge paragraph is consistent with the declared record and the driver's
  frame-ID ownership rule.
- **Evidence:** `FrameBeginSignal` (lines 1373–1377) declares `worldEpoch` but no `frameId`; line
  1862 states "The driver assigns `frameId` and supplies the active registry generation; neither is
  hook-made"; the bridge's `camera`/`event` operations (lines 1739–1743) already receive the
  driver-issued `FrameToken`. Phase 6's samples (`docs/phase6/v1/PHASE_6_DOC.md:593`–`:602`) do
  require a `frameId`, so the obligation is real but its stated source is impossible.
- **Severity:** note. The governing rule is stated unambiguously in the same section and the record
  listing is normative, so only one reading is implementable; this is imprecise shorthand. I do not
  admit the §5.1 table row at line 1938 as a second site: "world/frame identity from the accepted
  `FrameBeginSignal`" reads as the identity established at acceptance and is not defective.
- **Touches interface/change-trigger region:** yes.
- **Suggested wording (not ordered):** the bridge copies `worldEpoch` from the accepted
  `FrameBeginSignal` and stamps the driver-assigned `frameId` bound to the supplied `FrameToken`.

### Finding 6 (candidate-006) — §3.3's cited RESEARCH range stops one line before hook-need 11

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:565`–`:577`.
- **Claim:** the cited range covers needs 1–11.
- **Evidence:** RESEARCH's needs list runs 805–820 (need 1 at 805, need 10 at 819, need 11 at 820);
  §3.3 cites `:796`–`:819` per row and asserts zero-unmapped coverage of `:802`–`:819`.
- **Severity:** note. Every disposition, including row 11's Phase-12 ownership and
  `ShaderReloadController` exposure, is substantively correct; the defect is a one-line truncation
  of the citation endpoints, mechanically fixable.
- **Touches interface/change-trigger region:** no.

### Finding 7 (candidate-007) — two §3.1 rows cite ranges that omit the mapped RESEARCH lines

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:519` and `:523`.
- **Claim:** each row's cited range contains the contract text it maps.
- **Evidence:** the water/hand row cites `:544` (the depth-copy/deferred line, already claimed by
  the neighbouring row at `:543`–`:544`) and `:560`–`:561`; the translucent-terrain/water-program
  text is on the uncited `:545`, while the hand split is genuinely covered by `:561`. The
  fullscreen-state row cites `:562`–`:564`, but `scale.<prog>` and `countInstances` appear only on
  `:565`.
- **Severity:** note. Partial coordinate omissions in a conformance map whose substantive mappings
  are correct.
- **Touches interface/change-trigger region:** no.

## 2. Checked and clean

Re-derived and confirmed clean, or cleared on re-derivation:

- **New-surface lens:** the Round-32 Phase 6 citations (`:378`–`:383`, `:593`–`:602`, `:607`,
  `:614`–`:615`) resolve to the asserted content; the `Celestial`/`Fog`/`Blend` variants match the
  Phase 6 samples' non-identity fields; `ColorValue` is fully removed with no dangling reference;
  §0.36's note repairs and the closing status/header pointers agree. Only the `frameId` source
  phrasing (Finding 5) survives, at note severity.
- **Interface lens:** Phase 4 (1560–1580), Phase 5 (2002–2018), and Phase 6 (1382–1390) citations
  fall inside their manifest-selected binding regions; the Phase 8/9 downstream slots are correctly
  declared non-dependency integration slots with `NotInstalled`/`DEFERRED(P9)` defaults; §5.1 result
  algebras are closed and callable; the Kirino replacement seam is stated explicitly.
- **Conformance lens:** §3.2 maps every Appendix A.1 slot family with no unmapped rows; §3.3's
  dispositions themselves cover needs 1–11 substantively; §3.4's seven Pintonium rows each carry a
  reference coordinate and correctly flag sky/weather/clouds as reference-free; §3.6/§3.7 rulings
  are supported by their cited Cleanroom-patch and RESEARCH coordinates. `files.txt`-backed §3.7
  rows were not re-resolved (forbidden source).
- **Partially cleared within admitted findings:** the §5.1 exposed-contract row at line 1938
  (candidate-001's claimed second site) is not defective; the hand portion of the §3.1 water/hand
  row and the identity-ortho/mipmap portion of the fullscreen row are correctly cited.
- **Prior-review interaction:** Round 26 correctly moved Phase 7 from schema-v2 to schema-v3 against
  Phase 3's then-current bytes; that clearance does not survive Phase 3's move to schema-v4
  (Finding 2). Round 25/32's clearance of the `:1403`–`:1418` anchor is contradicted by direct read
  and does not settle Finding 3.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=3
Interface changed: yes

Four corrections and three notes; no blocking finding and no structural miss — the document's
thirteen mandatory sections, contract mappings, and interface algebras are intact, so nothing
requires rebuilding. Two corrections (Findings 2 and 3) and one note (Finding 5) fall inside the
declared cross-phase-interfaces region (lines 1350–2134), so the change trigger fires: a fresh
verify round is required before Phase 7 can close.

Trend/convergence: this is round 33 with no prior-round trend supplied to this session. The surviving
defects are of a single class — stale coordinates and version labels drifting against dependencies
that moved after earlier rounds cleared them — rather than new substantive design defects, which is
consistent with a converging document.

Next required action: apply Findings 1–4 (notes 5–7 are not ordered), then re-run verification for
`phase-7` because the interface region changes.


## Resolutions

The four admitted corrections were re-derived against the resolved RC3 authority, RESEARCH §4.4
and §7.1, and the current Phase 3 and Phase 5 binding rows before editing the target.

### Corrections applied

1. **H-TERRAIN-02 health gate.** The target's §3.6 ruling already grants the
   `FRAME_BEGUN -> PRE_WEATHER -> PRE_TRANSLUCENT -> FRAME_COMMITTED` order, and Phase 5's
   binding row makes that order caller-owned. The target's §4.5 design separately places the
   actual depth-copy calls at v0.5. The §4.10.3 health cell now says that the copy is enabled at
   v0.5 under the granted `PRE_WEATHER -> PRE_TRANSLUCENT` order, rather than preserving an
   impossible Phase 5-correction gate.
2. **Phase 3 ID-mapping version.** Phase 3 §4.9 and its §5.1 row publish schema-v4
   `IdMappingInput`; the Phase 7 §5.2 consumer row and §5.3 step 1 now both validate schema-v4.
   The historical §0.29 sentence remains bookkeeping for the then-current Round-26 correction,
   not a second live consumer claim.
3. **Phase 3 binding anchor.** Phase 3 §5.1 begins its exposed rows at `:1429` and the last
   consumed row ends at `:1444`; the Phase 7 §5.2 binding citation now points to that exact
   consumed-row span instead of the §4.10 prose at `:1403`–`:1418`.
4. **Phase 3 flag provenance.** The §3.5 rows now point to the current Phase 3 owner rows:
   `clouds` `:697`, `underwaterOverlay` `:702`, `sun`/`moon` `:703`–`:704`, `vignette`
   `:705`, `backFace.*` `:706`–`:709`, `rain.depth` `:710`, `beacon.beam.depth` `:711`,
   and `frustum.culling` `:713`. The precipitation handoff now cites `:757`, which contains
   the `PPT_NONE` and temperature-boundary rule.

### Notes deferred

- **Finding 5 (frame-ID source):** no edit was made. The suggested wording was a note, while the
  target already states that the driver assigns `frameId` and the adjudication cleared the
  exposed-contract table's identity wording. Rewriting that interface prose would add a
  note-only surface without changing the implementable rule.
- **Finding 6 (RESEARCH endpoint):** no edit was made. The eleven dispositions and their
  ownership are substantively mapped; extending the citation by one line is a note-only
  mechanical repair that was not admitted as a correction.
- **Finding 7 (two §3.1 ranges):** no edit was made. The substantive water/hand and fullscreen
  mappings are correct, and the omitted neighboring coordinates are partial citation notes, not
  unresolved conformance rows.

### Interface trigger and refusal

The target's new §0.37 addendum records the four corrections. Findings 2 and 3 change the declared
§5 cross-phase-interfaces region; Findings 1 and 4 are outside it. The manifest change trigger
therefore remains active and a fresh verification round is required. No correction was refused.