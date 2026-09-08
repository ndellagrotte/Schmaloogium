# Phase 5 — architecture review 47

**Date:** 2026-09-08. **Frozen document:** `docs/phase5/v1/PHASE_5_DOC.md` SHA-256 `ffdc6ba646e8e2aed2c9bb40c3a3f2f63d0c8b4c9365a45cd5027e46bd56c1ee`. **Verdict:** PASS-WITH-CORRECTIONS (3 corrections, 4 notes, 0 blocking). **§5 impact:** none — every correction is cross-document pin/header hygiene outside §5; no exposed or consumed contract shape, grant, or receiver obligation changes, so these corrections alone trigger no fresh §5 re-verification beyond the loop this document already declares open.

---

## 1. Scope, identity and authority actually verified

- Recomputed `sha256sum docs/phase5/v1/PHASE_5_DOC.md` → `ffdc6ba646e8e2aed2c9bb40c3a3f2f63d0c8b4c9365a45cd5027e46bd56c1ee`; matches the frozen hash. 3653 lines.
- Read the complete phase document in ranges: §0 (incl. all fix-up/addendum history through §0.47), §1–§4.13, §5 (§5.1–§5.5 incl. §5.3.1), §6–§8.5, §9–§12, closing status line.
- Read governing design Part I: §G1.2 verify protocol (DESIGN.md:276-319), §G1.3 (321-339), §G2 mission/decision log D-1..D-10 and failure ladder (343-430), §G4 cross-phase contract rules (516-578), §G9 template (790-827); Phase 5 spec DESIGN.md:1572-1688 (objective at :1583 *"Contract-visible almost end to end"* verified verbatim).
- Read `docs/research/v1/RESEARCH.md:11-107` (§0 confidence tags at :24-38, §1 mission/non-goals/decision log).
- Dependency docs sampled at cited coordinates: P1 (`docs/phase1/v14/PHASE_1_DOC.md`), P3 (`docs/phase3/v1/PHASE_3_DOC.md`), P4 (`docs/phase4/v1/PHASE_4_DOC.md`), plus consumers P6/P7/P8/P13/P14 and `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` (PD). `reference-src/**` was NOT read (reviewer boundary); see Note 2.
- Anchor spot-verification executed against current bytes for every family: RESEARCH (≈25 pins), P1 (5489×2, 5494, 5513), P3 (all six current pins: 1733, 1758, 2651, 2711, 3367, 3660), P4 (817, 1794, 2142, 2149), P6 (993-998), P8 (D-P8-26 relay), DESIGN (1583, 1595-1597, 1606-1613, 1621-1629, 1630-1646, 1647-1649, 790-826).
- No build, test, gradle, GL, or network execution was performed. Read-only except this report file.

## 2. Scope and §5 audit

- **Doc gate** (spec :1676-1680): App B.3 reproduced exactly in §4.12.1 (16 rows checked cell-by-cell against RESEARCH:1230-1247, gbuffers unit-12 empty, `tex` shadow-only) with the unit-11 ruling stated and provenance-pinned (RESEARCH:1251-1255 verified: *"Treat **11 as authoritative**"*). Clear/flip specified as explicit state machines (§4.4.1 side table, §4.4.2 transition table, §4.6 side-rules table) with matching §8.1 oracle rows. Every App B.1/B.2/B.4 family mapped in §3.1/§3.2/§3.4 with provenance. Frame-end reconciliation recorded with contract check (D-P5-4, §4.4.3, PD:229-236 verified). Fog-alpha-1.0 in the §4.6 table with the deployed-comment provenance. depthtex0 swap present and traced to PD §5.2 (§4.8). Shadow flip real, B4 stub attribution qualified (D-P5-35). Gate met.
- **Interface honesty, consumed:** §5.2 P1 rows verified against P1 binding §5 and body — `clearColorAttachment` (P1:3408, §4.7.4b at :4017, D-P1-67), captured maxima (D-P1-66 at :2988/:3038), `borrowDepthAttachment`/`attachDepthStencil`/`initializeDepthTextureFromFramebuffer` (:5489), borrowed-permission matrix (:5494), `prepareUnitBindings` (D-P1-60 at :3529/:5500), D-P1-63 (§4.7.7a at :4600), D-P1-64 (:4694), D-P1-59 (:3507), D-P1-69 (:4066), `bindDefault`-is-name-zero warning (:5513). §5.3 P3 rows exist (§5.1 at :3368+, ResourceRequirements :3386, BufferMinima :2743). §5.4 P4 rows exist: `validateSelection` with the five exact reasons at **P4:1794-1800 verified** (and P4:1801-1802 independently confirms P5's §4.12.3 reason mapping), `CompiledRegistryCandidate.view()` at P4:817-820, positional routing at :2142, D-P4-40 `SparseArray` at :510, D-P4-43 `profile-selection-v3` at :1937/:2800.
- **Interface honesty, exposed:** §5.1 rows carry consumers; receiver adoption verified in consumer docs — P6:622 ("Phase 5 sole resolver … no Phase 6 map", citing P5:2726 which lands), P7:1440 (two-field `BufferSizing`), P7:839/1318/1368 (`applyVirtualTransition`/`textureBindings`), P7:1554 (`BufferResizeNotice`), P8 §§0.7-0.8 (R7-12/R7-13; `ShadowPlanInput(policy,hookHealth,requested)` at P8:738 matches the §5.3.1 relay; `ShadowHookHealth/flattened-v3` current at P8:280), P13:196 (`phase13.parameters/v2`), P13:564-583 (`TextureCandidateTable`/`TextureOverlayLease`/`BaseAtlasContext`), P14:668 (D-P14-31, matching D-P5-43's receipt). Adopted-unverified markers are consistently applied.
- **Current-identity discipline:** schema23/`MaterializedSource-v23` current (D-P5-44) with v22/v21/v20/v19 receipts explicitly historical; `RegistryFingerprint/profile-selection-v3` current (D-P5-48) with `positional-route-v2` marked historical in both §5.3 and §11.3; `phase13.parameters/v2` current; `projectionVersion1` retained unchanged; `superSamplingLevel` cutover (D-P5-27) consistently removes the third field with no alias. `TextureHookHealth`, `capture-plan`, `run-manifest` do not appear in this document (N/A). No stale-as-current identity found.
- **Scope discipline:** spec Scope-out items are honored via explicit §1.2 ownership lines; the one ownership movement (physical texture binding / fixed-name policy now Phase-5-owned versus the spec's "custom texture binding (Phase 13)") is the maintainer-authorized §0.39 coordinated rebuild, recorded with decisions D-P5-21/22 and consistently adopted by P6/P7/P8/P13 — a flagged deviation, not silent scope creep (Note 4).
- **Template completeness:** all thirteen §G9 sections present and substantive; §10 correctly states no assigned OQ and invents no spike.

## 3. Required corrections

### Correction 1 — stale Phase 3 anchor family (six sites; this wave's P3 repoints did not land)

- **Sites:**
  1. `PHASE_5_DOC.md:1174` (§3.1, colortex1/gdepth row) cites `docs/phase3/v1/PHASE_3_DOC.md:1758`.
  2. `PHASE_5_DOC.md:1180` (§3.1, explicit-flip row) cites `…PHASE_3_DOC.md:1733`.
  3. `PHASE_5_DOC.md:1246-1247` (§3.5) cites `…PHASE_3_DOC.md:2651`–`:2658`.
  4. `PHASE_5_DOC.md:1195` (§3.2, sfb row) cites `…PHASE_3_DOC.md:2711`–`:2724` as "Phase 3 algebra".
  5. `PHASE_5_DOC.md:2512` (§4.12.2) cites `…PHASE_3_DOC.md:3660`–`:3663`.
  6. `PHASE_5_DOC.md:1195` (§3.2, same row) cites `…PHASE_3_DOC.md:3367` as "binding input".
- **Failure path:** recomputed against current P3 bytes, the cited coordinates hold unrelated content: :1733 is the `screen=<entries>` row (the `flip.<prog>.<buf>` publication is at **:1752**); :1758 is the `-Dshaders.debug.save=true` row (the gdepth→`Explicit(RGBA32F)` publication is at **:1777**); :2651-2658 is the scanner-recognizer table (the prefix→index canonicalization `normalizeColorBufferName` is at **:2670-2677**); :2711-2724 holds ResourceRequirements prose but not the shadow-minima algebra (`BufferMinima(int colorBuffers, int mainDepthTextures, int shadowDepthBuffers, int shadowColorBuffers)` is at **:2730-2745**); :3660-3663 is the `TextureTarget`/`PackPath.image` text (the complete-key last-valid-wins + stage/unsigned-UTF-8/absent-then-0–9/source-kind ordering is at **:3680-3681**); :3367 is a blank line (the §5 publication-surface intro/§5.1 heading is at **:3365-3368**). The consistent +19/+20 displacement indicates the pins predate recent P3 §0 growth and were not re-derived against current bytes, despite this wave's claim of P3-family repoints. Every underlying claim remains true at the corrected coordinates; only the pins are wrong.
- **Owner+receivers:** Phase 5 owner; receivers any future reader following the provenance trail (P3 citations are load-bearing for §3 conformance-map auditability).
- **Minimal resolution:** repoint the six pins to 1752, 1777, 2670-2677, 2730-2745, 3680-3681, and 3365-3368 respectively; change no prose.

### Correction 2 — stale Phase 6 anchor for the conditional-watershadow rule

- **Sites:** `PHASE_5_DOC.md:2478` (§4.12.1) cites `docs/phase6/v1/PHASE_6_DOC.md:993-998`.
- **Failure path:** P6:993-998 is the half-life decay LaTeX block; the actual rule — "The conditional `shadow` rule is evaluated only by Phase 5 from the effective provider's complete sampler layout: a direct sampler-compatible `watershadow` declaration selects unit 5; otherwise unit 4" — is at **P6:1219-1221** (test corroboration at P6:2110). P6 was not among this wave's repoint families, which is consistent with the pin surviving from an older P6 state; it is stale regardless.
- **Owner+receivers:** Phase 5 owner; receivers Phase 6/4 readers cross-checking the shared-unit rule.
- **Minimal resolution:** repoint to `docs/phase6/v1/PHASE_6_DOC.md:1219`–`:1221`; change no prose.

### Correction 3 — header last-revision pointer stale

- **Sites:** `PHASE_5_DOC.md:13` — "**Last revised:** 2026-09-07 (§0.41)".
- **Failure path:** §0.45, §0.46 and §0.47 are dated 2026-09-08, and the D-P5-28…D-P5-50 receipts in §§5.3/11.1 are dated 2026-09-08; the header pointer predates the newest content it must index. §0.36 records that Round 34 corrected exactly this pointer class, so the defect is a recurrence of a known hygiene item.
- **Owner+receivers:** Phase 5 owner; receivers doc-gate readers relying on the header to locate the latest amendment.
- **Minimal resolution:** set "**Last revised:** 2026-09-08 (§0.47)"; change nothing else.

## 4. Notes

1. **§0.42–§0.44 absent.** The fix-up log jumps §0.41 → §0.45 (Attempt-5). No cross-reference anywhere in the document targets §0.42/0.43/0.44, and no content claim depends on them; cosmetic numbering discontinuity only. If desired, a future fix-up may either renumber or record why attempts 2–4 left no sections.
2. **`reference-src/**` pins not re-verified.** Reviewer boundary forbids reading `reference-src/**`; the document's Pintonium/Iris/G6 source pins (BufferFlipper:25/27, ClearPassCreator:21-78, MixinFramebuffer_Shaders:59/:74-100, DepthCopyStrategy:16-30, ShadowRenderTargets:43-72, RenderTargets:148/:166, CommonIrisRenderingPipeline:545-553/:549-551/:1101-1109, shaders.txt rows) were accepted on the strength of PD/RESEARCH corroboration. Directly verified instead: PD:218-259, 263-277, 799, 808-809 all contain exactly the claimed SwapPass/resize-checklist/no-global-quality/16-colortex/shadow-structure/dynamic-unit content.
3. **Historical §0.39 read-record ranges** (`PHASE_3_DOC.md:360-420`, `PHASE_6_DOC.md:325-357`, `PHASE_8_DOC.md:225-257`, `RESEARCH.md:1228-1255,1482-1492`) are dated records of past reads, not current citations; they were not treated as live pins (the RESEARCH ranges were nonetheless confirmed to land).
4. **Ownership split versus spec Scope-out.** The spec assigns "custom texture binding" to Phase 13, while the active document owns fixed-name policy and physical binding (§§1.1/2.4/4.12) with Phase 13 owning production/leasing. This is the maintainer-authorized §0.39 coordinated rebuild after Phase 13 Review 3, recorded as D-P5-21/22 and adopted by P6/P7/P8/P13 — audited as a flagged, ecosystem-consistent deviation rather than silent scope creep.
5. **Quote-drift check clean.** The two load-bearing verbatim quotes were byte-compared: the App F.7 sentence (RESEARCH:1519-1520, "…so later passes **can** read") and the Phase 4 side-ownership warning (P4:2149, *"Phase 5 must not infer a resolved ping-pong side from `explicitFlips`; it owns the side state"*) — no drift.

## 5. Areas audited and found sound

- Frozen identity: SHA-256 match; line count 3653 as assigned.
- Conformance-map semantic fidelity: §3.1 rows against RESEARCH B.1 (:1201-1215), §3.2 against B.2 (:1218-1226) and §4.3 rows (:518-527), §3.3/§4.12.1 against B.3 (:1228-1255 incl. the unit-11 ruling), §3.4 against B.4 (:1261-1268) — every sampled row's claim matches the cited bytes; the 8×4+5=37 format closure is arithmetically and textually exact; the shipped-doc 12-vs-11 contradiction is surfaced, not hidden.
- Anchor families that land: RESEARCH (~25 pins incl. :1181, :1201-1215, :1210-1212, :1213-1215, :1218-1226, :1227-1248, :1251-1255, :1261-1268, :371-386, :517-527, :1484-1490, :1519-1520, :24-38), P1 (:5489×2, :5494, :5513), P4 (:817-820, :1794-1800, :2142, :2149-2151), PD (:218-259, :263-277, :799, :808-809), DESIGN (:1583, :1595-1597, :1606-1613, :1611-1613, :1621-1629, :1630-1633, :1630-1637, :1638-1646, :1647-1649, :790-826, :92-1109, :1572-1685). The two wave-named repoints were specifically confirmed: N-4's unit-11 range `RESEARCH.md:1251-1255` and §5.4's `validateSelection` at `P4:1794-1800`.
- Interface honesty both directions (§2 above): all consumed P1/P3/P4 contracts exist at cited shapes; all §5.1 exports have named consumers with adoption recorded in P6/P7/P8/P13/P14; adopted/unverified markers are uniform and no stale grant is treated as verified.
- D-1..D-10 (§11.2) honored: Cleanroom-only bridge via `mod.glue` (D-1), buffers-only scope (D-2), fixed pack matrix (D-3), growth-shaped identities (D-4), one dumb framebuffer mixin (D-5), pure `engine.buffers` with opaque handles and no engine LWJGL (D-6), GPL posture with the N40-1 GPL-v3 qualification governing reuse (D-7/D-8), compat-profile semantics (D-9), state-machine + recorded-GL testability first-class (D-10, §8).
- Pintonium/licensing compliance: every adopted mechanism carries `[V:observed — Pintonium <path>]` plus PD § citation and a recorded D-P5-k contract check (§3.6 dispositions incl. the rejected SwapPass, 16-colortex allocation, dynamic units, and the qualified B4 stub attribution); no AGPL transformation-library trace; no chatlog/root-txt reads claimed.
- Failure ladder (§6) maps every subsystem failure to a rung with the shaders-off terminal reachable; threading model (§7) is render-thread-only for GL with pure off-thread planning, matching §G2.3.
- §11.6 sampling-authority disposition is internally consistent: evidence table, maintainer disposition quoted with date, and the D-P5-27 cutover table that removes the third `BufferSizing` field — matching P7's adopted two-field consumption at P7:1440.

## 6. Verdict

**PASS-WITH-CORRECTIONS** — 3 corrections (Correction 1 spans six pin sites; Correction 2 one site; Correction 3 one header line), 4 notes, 0 blocking findings. All defects are citation-coordinate and header hygiene; the underlying claims are true at corrected coordinates and no §5 contract bytes change. Per §G1.3, applying the corrections and recording resolutions closes this round's findings without mandating a fresh §5-driven verify cycle beyond the whole-document PASS this document already declares outstanding. No build, test, gradle, GL, or network execution was performed by this review.
