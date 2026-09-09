# Phase 6 — architecture review 35

**Date:** 2026-09-08
**Frozen artifact:** `docs/phase6/v1/PHASE_6_DOC.md` — SHA-256 `5e8fed4690d3cf5eb8d1bfeb5c0b2ead81319055baf68f4877b830d3a8fef8e2`, recomputed at wave open against the attempt-11 registry (`docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_11.json`, phase 6, reviewRound 35) and identical.
**Verdict: PASS-WITH-CORRECTIONS** (blocking=0; corrections=4; notes=2)
**§5 impact:** none — every correction is confined to §0 provenance markers/line anchors or §3 row wording; no §5 row, schema, or incorporated §2.2/§4 declaration changes, so per §G1.3 (`docs/design/v2.0-RC3/DESIGN.md:327-332`) a fix-up closes the phase without a further verify session.
**Method:** documentary only. No build, test, Gradle, GL, capture, or network command was executed. `reference-src/**`, `docs/**/chatlogs/`, root `*.txt`, and all other `PHASE_*_REVIEW_*.md` files were not opened (wave read boundaries); see note N2 for the resulting verification scope.

---

## 1. Doc gate and template completeness

- Hash gate: recomputed `sha256sum` on the frozen path before any other read; identical to the registry value. Wave entry condition met.
- §G9 skeleton (`docs/design/v2.0-RC3/DESIGN.md:790-827`): all thirteen sections 0–12 are present and substantive at `docs/phase6/v1/PHASE_6_DOC.md` ll.3, 355, 413, 613, 650, 1807, 2010, 2045, 2102, 2189, 2213, 2224, and 2413. Header records inputs actually read (§0.1), dependency docs consumed, and deviations with reasons (§0.2).
- Spec doc gate (`docs/design/v2.0-RC3/DESIGN.md:1791-1795`), item by item:
  - Every Appendix D row mapped to provider/acquisition, activation policy, and milestone: §4.4.1–§4.4.4 tables (ll.886–980) carry all three columns for each of the ~50 rows and together cover RESEARCH Appendix D.1–D.4 with no missing or invented row (D.4's five dynamics appear exactly as `docs/research/v1/RESEARCH.md:1372-1376` lists them).
  - Smoothing formulas written out: §4.5 closed-form decay with edge rules (ll.995–1016).
  - Barrier fulfillment traced to Phase 4's interface point by point: §4.10 obligation table (ll.1318–1327) walks selection, invalidation, bind, lease, and the three participant positions against Phase 4's published sequence.
  - `centerDepthSmooth` decision recorded with contract-check evidence: §4.8 candidates A/B with all four rejection grounds and `[D-P6-1]` (ll.1145–1187).
  - Notifier→producer audit table present: §4.12 (ll.1456–1472), with the mandatory blend row and the B6 structural fix.
  - Frame-begin ordering constraint exported in §5: §5.1 "Frame-begin ordering contract" row (l.1818).
- REV1 additions all present: externally validated cadence model with the acquisition/activation separation (§2.4, §4.3), FF-matrix capture moments with explicit non-ordinal-zero-clear rule (§4.6), GPU candidate gated by §G11.4 (§4.8), closed-form smoothing with the PD B1 misassignment avoided (§4.5), previous-frame snapshot overwrite semantics with §8 camera-path coverage (§8.2 ll.2152-2154).
- OQs: none assigned (`docs/design/v2.0-RC3/DESIGN.md:1691`, "**OQs:** —", verified verbatim); §10 is therefore legitimately short and owes no spike spec under §G4.4.

## 2. Conformance-map audit (anchor spot-checks)

Sampled citations were re-verified at their stated coordinates; all content quoted below was found at the cited lines unless a finding says otherwise.

- RESEARCH anchors, all faithful to current bytes: `:47` ("The contract"), `:100` (D-6 seam), `:290` (built-in inventory pointer), `:534` ("frame start"), `:537` ("snapshot previous-frame camera + matrices"), `:558` ("synchronous center-depth readback"), `:1167` (`centerDepthSmooth` enables readback), `:1228`/`:1230` (App B.3 heading and table), `:1254` ("Treat **11 as authoritative**"), `:1319` (Appendix D heading), `:1326`/`:1332` (D.1 table, `eyeBrightness` "x block / y sky light, 0–240"), `:1346`/`:1348` (720720 / 3600 wraps), `:1360` ("0.05 / renderDistance×16"), `:1366` (centerDepthSmooth row), `:1370` (D.4 exclusion heading), `:1380-1383` (cadence model: everything refreshes, matrices always upload, custom after built-ins), `:1416` (App E row 16 GlStateManager blendFunc observation), `:1495` (declaration grammar).
- The seven-name expression exclusion union: P6 §4.13's claim that current RESEARCH §3.4 and F.6 adopt it and that F.6 does not narrow D.4 is verified against current RESEARCH §3.4 item 4 (ll.296–302) and F.6 (ll.1495–1506). Finding 4 below concerns only §0.18's unmarked stale description of the pre-resolution bytes.
- DESIGN anchors: `:327-332` (fresh verify before dependent consumption), `:669` (camera-path motion), `:691` (derived-artifact policy), `:790` (skeleton), `:888` (AGPL never-copy), `:1691`, `:1698`, `:1702`, `:1721-1725` (frame-begin before resize/clear), `:1733` (per-tick decay), `:1746-1747` (Candidate B wording and non-bit-identity), `:1771`, `:1776` (scope-out) — all resolve to the quoted content.
- Phase 1 anchors: `:3053`, `:5550`, `:5790` ("recorded-GL run" mechanism addressed to Phases 4/5/6), `:3422` (`readDepthPixel` verb), `:3819`/`:5599`/`:6173` (D-P1-32 "values already computed for this sweep"), `:5509` (`ReplayAwareGLError` row). All exact.
- Phase 3 anchors: `:1485` (`DeclaredUniformCatalog`), `:1586-1587` (macro header placement before the first restored pack `#line`), `:1820` (`SmoothingConstants.drynessHalfLifeTicks`), `:3405-3406` (`CURRENT_SCHEMA_VERSION` row; `PackConfiguration` "single validated downstream truth"). All exact.
- Phase 4 anchors: `:332` ("Unverified"), `:1032` ("one immutable compiled binding"), `:1662-1672` (`locate`/`afterBind` shape), `:1767-1771` (three positions), `:1803-1804` ("invoke sampler, built-in, custom in that order"), `:1829` (participants run on every activation), `:1841-1858` (token invalidation inventory), `:1926` (merged layout fingerprints), `:1973`/`:1977` (invalidate token before close-the-old-registry), `:2147` (access/token/participant row). All exact.
- Phase 5 anchors: `:1029` (`ResolvedSamplerBinding`), `:2449` ("They share one table/schema"), `:2486` ("Both gbuffers bands share this map"), `:2507-2510` ("Invalid retains the complete validation evidence"; unsupported domains never fall back), `:2734` (§5.1 row: "same fingerprint, exact spellings and conditional watershadow rule"; "no second map or free-unit allocation"), `:338-354` ("that evidence does not certify this rebuild"). All exact; the conditional `shadow` 5/4 rule is consistent across RESEARCH B.3, P5 §4.12.1, and P6 §4.9/§8.1.
- Pintonium design anchors: `:288` (ONCE/PER_TICK/PER_FRAME/dynamic buckets), `:312` (§6.3), `:333` (§6.4 deciseconds), `:344` (dynamic unit allocation), `:808` (§18 divergence row: fixed App B.3 map, depthtex1 at 11). All exact. The B1/B6/B10 and `reference-src` source quotes were not opened by this reviewer (N2).
- Phase 7 anchors: `:4072` ("No second map"), `:4081` ("after final callback and before borrowed services disappear"), `:4077-4088` (R7-11 paragraph) — exact. Two further §0 anchors are stale — findings 1 and 2.

## 3. Interface honesty

- Consumed interfaces exist in dependency §5s: P1 (drain/`ReplayAwareGLError`/D-P1-32 law, `readDepthPixel`, `UniformService` overloads incl. ivec4, `UniformLocation.isAbsent()`, `RecordingGLDevice`/`ScriptedResponses`, capability-profile serialization with consumers 2/4/5/6); P3 (`PackFrontEnd.CURRENT_SCHEMA_VERSION` = 23 at ll.754/3405/4211, `PackConfiguration`, closed `ResourceRequirements` row naming "Phase 6 depth/smoothing", reserved `phase6.centerDepthSmoothRedirect` slot at `:3412`, `CustomExpressionDecl` at `:1286`); P4 (descriptor/layout/sampler-layout family, `ProgramUniformCacheKey`, `BarrierContext`, participant/access/token rows, `Degraded`, per-slot `instanceCount`); P5 (`FixedSamplerPolicies.resolver()`/`appB3()` and result algebra, §5.1 row `:2734` naming "Phase 6 R7-10 adopted/unverified participant").
- Exports specified with receiver adoption: P7 §5.4 receives R7-10 with the byte-identical eight-argument factory (P7 ll.4062–4066 vs P6 §2.2 ll.444–454) and R7-11 with the identical retirement algebra and "Phase 6 removed reset(CLOSE)" current-bytes clause (P7 ll.4077–4088 vs P6 §0.24). P2's current `schmaloogium.capture-plan/4` and `run-manifest/4` schemas (P2 v2 ll.1063–1064, 1144–1146) match D-P6-31/D-P6-38's "current `/4`" rows. P8 publishes `CelestialMath.angles`/`CelestialMath.sample` (P8 ll.517, 564–568, 585) matching D-P6-37's signatures. P13 publishes `atlasSize(AtlasId)`/`atlasSize()` Known/Unknown (P13 ll.396–397) matching §4.12's adapter.
- Current-identity receipts: D-P6-36 receives exactly P3's current schema23/`MaterializedSource-v23` identity (P3 ll.585–589, 4254–4262); D-P6-24 (schema20), D-P6-26 (schema21), and D-P6-35 (schema22) are explicitly demoted to historical in §5.2 ("prior numeric receipts are historical"). `projectionVersion1` (ll.1866, 1889) matches P3's current inspection identity.
- §3 status wording defect: two conformance-map rows label Phase 4's current coordinated bytes "verified" — finding 3.

## 4. Scope discipline, D-1..D-10, and decisions

- Scope-out respected: no custom-expression evaluation (Phase 11 owns it; P6 publishes only the §4.13 extension point required by `DESIGN.md:1714`); no hook ownership (P7/P8 invoke; §4.12 is the audit side); alias-derived values stay with P9; `atlasSize` value source with P13; no second sampler map or unit allocation (§4.9); no texture-object binding, flip, or buffer work (P5); no program selection/activation (P4). Nothing from Scope-in (`DESIGN.md:1704-1774`) is silently dropped.
- D-1..D-10: D-6 seam held (§2.1 — no Minecraft/Forge/Mixin/LWJGL/JOML/raw-GL type crosses `:engine`); D-9 FF-stack cooperation (§4.7); D-8 licensing posture (no glsl-transformation-lib use, §0.2; PD reuse recorded with compliance and D-P6-34's conservative label qualification); D-10 tier coverage incl. camera-path motion (§8.2); D-2/D-3/D-4/D-5/D-7 uncontradicted. No contract-visible semantic is "improved": unit numbers, wrap values, cadences, and neutral values follow the cited rows.
- Decision log D-P6-1..D-P6-38 is coherent; R7-10/R7-11 are consistently marked adopted-but-unverified; the reset/adoption/retirement partition is internally consistent across §§2.2, 4.14, 5.1, 7.1, and 11.3; §G1.3 status blocks correctly supersede earlier ones and all state fresh whole-document verification is owed — which this review is.
- §G5.1 dependency discipline: declared deps 1/3/4 match `DESIGN.md:591`; Phase 5 is carried as an explicitly declared maintained dependency with owner gates (§0 header, §5.2), not a silent graph edit.

## 5. Identity, licensing, and current-state discipline

- No stale current-identity citations found outside the findings below: no `D-P2-32`/`D-P2-38` citations anywhere (the only P2 decision referenced is the D-P2-39-era `/3` envelope, twice explicitly marked retired history, ll.2320 and 2327); no `docs/phase2/v1` path citations; `RegistryFingerprint`/`profile-selection` carry no superseded version labels; ShadowHookHealth/TextureHookHealth identities are not cited at all; P13 sidecar wording (ll.1890, 1968) matches the maintained edge without version claims.
- Licensing/Pintonium compliance: PD-derived claims carry provenance tags and paths; §17 B1/B6/B10 and §18 rows are demonstrably handled in the §3 map; the GPU `centerDepthSmooth` §G11.4 gate shows its contract check and recorded decision (D-P6-1); the AGPL transformer and stareval are unused; D-P6-34 records the pinned-root-LICENSE GPLv3 observation and requires per-file licensing establishment before future incorporation — the conservative, compliant posture.

## 6. Findings

**F1 (correction) — stale Phase 7 anchor in §0.23.**
Location: `docs/phase6/v1/PHASE_6_DOC.md:244`.
Claim: cites `docs/phase7/v1/PHASE_7_DOC.md:4047` for the quote "The unchanged Phase 6 sampler participant calls it using binding.samplerLayout and context".
Evidence: that sentence is at `docs/phase7/v1/PHASE_7_DOC.md:4069` in current bytes; line 4047 is the §5.4 table separator. The quote text itself is verbatim-present, so this is a coordinate drift (+22), not a content loss — inconsistent with the neighboring §3/§4.14/§5.2 P7 pins, which Phase C re-derived correctly.
Severity: correction. Repoint to `:4069` (or mark the §0.23 citation adoption-time, as §0.18 does for its P11 citation).

**F2 (correction) — stale Phase 7 anchor in §0.24.**
Location: `docs/phase6/v1/PHASE_6_DOC.md:277`.
Claim: cites `docs/phase7/v1/PHASE_7_DOC.md:4055` for "UniformRetirementResult retire(UniformRetirementReason reason)".
Evidence: that text is at `docs/phase7/v1/PHASE_7_DOC.md:4077`; line 4055 is the R7-8 table row. Same drift class as F1.
Severity: correction. Repoint to `:4077`.

**F3 (correction) — §3 conformance rows overstate the verification status of Phase 4's current bytes.**
Location: `docs/phase6/v1/PHASE_6_DOC.md:620-621`.
Claim: the rows say "verified dependency contracts at `docs/phase4/v1/PHASE_4_DOC.md:1926`" and "verified Phase 4 contract at `docs/phase4/v1/PHASE_4_DOC.md:2147`".
Evidence: both cited lines sit inside P4's coordinated §5, which P4 itself marks "Unverified; a fresh whole-document review returning literal PASS is required" (`docs/phase4/v1/PHASE_4_DOC.md:332`), and P6's own §5.2 (ll.1942–1951) and §2.2 (l.575)/§4.10 (l.1280) correctly say the current coordinated owner bytes await fresh-owner review, with the R18/R20 passes historical only (§5.3, l.1978). The §3 wording as it stands contradicts §5.2's binding gate.
Severity: correction. Reword to "granted in current bytes; fresh owner verification owed per §5.2" (mechanics historically verified under §5.3).

**F4 (correction) — §0.18 retains an unmarked stale current-state assertion and a stale F.6 description.**
Location: `docs/phase6/v1/PHASE_6_DOC.md:208-212`.
Claim: §0.18 still says Appendix F.6 "separately names only `entityColor entityId blockEntityId fogMode fogColor`" at `RESEARCH.md:1501-1505`, and that "the conflicting protected-source wording is reported, not claimed resolved, and still requires an explicitly authorized RESEARCH-maintainer action".
Evidence: current `docs/research/v1/RESEARCH.md` F.6 (ll.1495–1506) names all five D.4 entries plus `fogMode`/`fogColor` and states the union explicitly with "Appendix F.6 does not override or narrow Appendix D.4"; P6's own §4.13 (ll.1608–1610), §11.2 item 8, §11.4 receipt, and D-P6-30 record the discrepancy "resolved upstream, not a pending clarification". The two states cannot both be current; §0.18's trailing sentence is present-tense and carries no historical marker, unlike comparable superseded §0 blocks (§§0.23/0.24).
Severity: correction. Mark the sentence historical (or reword to the resolved-upstream state); the schema contract itself is identical in both states, so no §5 consequence.

**N1 (note) — D-P6-26 decision-log row retains pre-supersession phrasing.**
Location: `docs/phase6/v1/PHASE_6_DOC.md:2255` (and §0.26, l.323).
Claim/evidence: the D-P6-26 row records "Adopt P3 R55 schema21" without the inline historical qualifier that D-P6-24's row carries; supersession is nevertheless explicit in §5.2 (D-P6-36: "prior numeric receipts historical") and the §5.2 D-P6-26 paragraph is labeled "Historical". No contradiction reaches a consumer; adding the same qualifier to the §11.1 row would make the log self-contained.
Severity: note.

**N2 (note) — verification scope of `reference-src` and review-file citations.**
Location: this review.
Claim/evidence: per the wave's read boundary, `reference-src/**` was not opened, so the Pintonium/Schlorbium source-coordinate quotes in §3/§4 (e.g., `ProgramUniforms.java:200`, `ProgramSamplers.java:320`, `SmoothedFloat.java:45/53`, `shaders.txt:169/176`) and the Pintonium-design-derived rows were checked only for internal consistency, not against source bytes. Likewise, citations of `PHASE_3_REVIEW_20.md`, `PHASE_4_REVIEW_18.md`, and `PHASE_6_REVIEW_18.md` (§§0.6, 5.3) were not opened; their claims are consistent with the documents' own §G1.3 status blocks. No defect is asserted either way.
Severity: note.

No blocking findings. All corrections are local text/anchor repairs; none requires structural rebuild, and none touches §5.

**Verdict: PASS-WITH-CORRECTIONS** (blocking=0; corrections=4; notes=2). No build, test, Gradle, GL, capture, or network command was executed; this review is documentary only.
