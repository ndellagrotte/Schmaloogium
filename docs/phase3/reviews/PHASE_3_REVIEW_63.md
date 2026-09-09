# Phase 3 - architecture review 63

**Date:** 2026-09-08 · **Document:** `docs/phase3/v1/PHASE_3_DOC.md` (5581 lines, `v1` directory) · **SHA-256:** `fe2206dc934f5cbf432048e89b6fda36fec1acfd91c715ad37e724b3d1fd585c` — recomputed at wave open, byte-identical to the Attempt-11 freeze in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_11.json`; gate passed · **Verdict:** PASS-WITH-CORRECTIONS (blocking=0; corrections=2; notes=1) · **§5 impact:** none — both corrections are dependency-evidence anchor repairs inside §0/§11 historical addenda; no §5 row, export, schema, fingerprint payload, or receiver obligation changes, so per §G1.3 no additional re-verification cycle is triggered beyond this wave's own fresh whole-owner review.

---

## 1. Scope, method, and hash gate

Fresh independent adversarial review of the complete Phase 3 document under the Attempt-11 verify-session protocol (RC3 §G1.2 as read through the v3 override selectors, matching the doc's own §0.1 item 2). The frozen hash was recomputed before any reading and matches the registry value above; the review is hash-gated to those bytes.

Read in full or in the assigned slices: the complete `docs/phase3/v1/PHASE_3_DOC.md` (all 5581 lines, §§0–12); `docs/design/v2.0-RC3/DESIGN.md` Part I §G0–§G12 (ll. 92–1109) and the Phase 3 specification (ll. 1316–1470); `docs/design/v3/DESIGN.md` §G0.4 (ll. 195–223), §G1 (ll. 224–368), and the Phase 3 target spec (ll. 1343–1497); `docs/research/v1/RESEARCH.md` ll. 11–107 (§0–§1) plus the App A.3/F.1/F.3/F.5/F.6 rows and §3.5/§4.7 text needed to check conformance-map rows; the dependency doc `docs/phase1/v14/PHASE_1_DOC.md` §5 (ll. 5454–5584) with spot reads of its §4.13 region; incorporated receiver sections cited by Phase 3 in `docs/phase4/v1/PHASE_4_DOC.md` and `docs/phase11/v1/PHASE_11_DOC.md` and `docs/phase13/v1/PHASE_13_DOC.md` at their stated coordinates; shipped-doc anchors under `reference-src/schlorbium-HD_U_G6_pre1/doc/` and `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` at stated coordinates.

Boundaries honored: read-only except this file; no other `PHASE_*_REVIEW_*.md` read (review-62 evidence was taken from the doc's own §0.67 record); no `reference-src/**` beyond the shipped `doc/` contract files the phase spec itself assigns; no chatlogs, root transcripts, builds, tests, Gradle, GL capture, or network. **No build, test, Gradle, GL, capture, or network command was executed; this review is documentary.** Dependencies per §G5.1: Phase 1 is the sole dependency contract; the maintained P5→P6 and P13→P14 edges do not alter Phase 3's input set.

## 2. Template and doc-gate verification

- **§G9 template:** all thirteen sections (0. Header … 12. Implementation checklist) are present with substantive content; §0 records inputs actually read, dependency docs consumed, and the v3-override deviation with reasons; §12 items are ordered, milestone-tagged, and each names its test hook. No missing section.
- **Conformance-map coverage (doc gate):** §3.1 maps every Appendix F.1 flag (all 17 keys at `PHASE_3_DOC.md:1719–1735`) with field, single behavior owner, and named parser test, and closes with the completeness claim at ll. 1737–1740. §3.2 covers F.2–F.8 with no "miscellaneous" bucket (ll. 1742–1789); the const-option whitelist row (l. 1752) matches RESEARCH F.3's twenty-name list exactly. §3.3 covers every App A.3 row including the gdepth upgrade, all three directive syntaxes, DRAWBUFFERS edge cases, and post-v0.5 RENDERTARGETS (ll. 1799–1835). Zero unassigned rows found on audit.
- **Pitfall traces:** B1→`directive_drynessWritesDryness` (ll. 1820, 1844, 4848), B2→`directive_legacyCommentFormsReachFields` (ll. 1845, 4849), B3→`optionRefsDoNotCrossWcc` + `optionAmbiguity_duplicateNamesAcrossComponents` (ll. 1846, 4850), B12→`propertyHashRoundTrip` (ll. 1847, 4851) — each has both the required conformance row and a named test.
- **centerDepthSmooth reservation:** `phase6.centerDepthSmoothRedirect` is specified with exact location, payload validation, and failure semantics (ll. 1585–1592, 2338, 3412); Phase 6 ownership is restated in §1.2 (l. 687) and §11.4 (ll. 5179–5180).
- **Identity posture:** the v0.1 default is stated option-3-shaped with the OQ-7 decision explicitly still open and owned by G8/S3 (ll. 2341, 5044–5045); §10's spike spec carries the verbatim RESEARCH §11 question, procedure, success/failure criteria, and fallback per §G4.4. Pure-`:engine` placement is confirmed (ll. 677–679, 4204); jcpp adoption is stated (l. 661) with the P1 D-P1-49 admission consumed.
- **Milestone staging:** §9's component register P3-C01–P3-C23 assigns exactly one tag per component (ll. 4970–4994).

## 3. Conformance-map audit (spot-checks against cited contract text)

Rows checked against RESEARCH/appendix text at recomputed coordinates; all verified accurate:

- F.5 rows (`PHASE_3_DOC.md:1768–1775`) vs `docs/research/v1/RESEARCH.md:1484–1490` — numeric `.0–.9` discriminators, `.mcmeta` blur/clamp sidecars, stage expansion, shared-unit disambiguation all match. The D-P3-67 documented-mechanism boundary is correctly grounded: neither RESEARCH F.5 nor the shipped grammar (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:103–119`, esp. ll. 117–119) defines property-key filter/wrap tokens.
- F.6 rows (ll. 1776–1779) vs `RESEARCH.md:1492–1496` — kind/type/name/raw-expression capture, upload/intermediate split, precipitation handoff. Accurate.
- Standard-macro family and companion macros (ll. 1865–1866, D-P3-58 at l. 5109) vs `RESEARCH.md:313–319` and `shaders.txt:653–661` — the eight-name option family and the "When the normal/specular map is enabled" phrasing (ll. 655–656) match; old-hand-light/old-lighting quotes (D-P3-64, l. 5115) match `shaders.txt:660–661`; user-priority citations `shaders.properties:9–11` and `:17–29` verified verbatim.
- A.3 directive rows (ll. 1801–1835) vs `RESEARCH.md:1155–1192` — all rows present; D-P3-60's twin quotes verified verbatim (`RESEARCH.md:1159` "enable extended vertex attribute for this program"; `:1186` "per-pass mipmap gen (composite/deferred/final)").
- Locale and superSamplingLevel evidence (D-P3-63 l. 5114; D-P3-66 l. 5117) vs `shaders.properties:189`, `:192–208`, `:277–284`, `:319–324` — verified; the D-P3-66 disposition is a flagged, maintainer-authorized decision (not a silent deviation), satisfying the §G9 rule that deviations are flagged decisions.
- ID-mapping grammar (D-P3-73, l. 5124) vs `shaders.txt:544` → `properties_files.txt:97–122` — reference chain and metadata/property-interval content verified.

## 4. Interface honesty and dependency anchors

**Consumed interfaces (P3 ← P1):** every interface §0.2/§2 claims to consume exists in the current `docs/phase1/v14/PHASE_1_DOC.md` §5: module layout and seam constraints (§5.1), `GLCapabilityProfile` with the exact macro-projection duties Phase 3 assumes (§5.2 row expressly written for Phase 3), `Log`/`EngineDiagnostic`/`DiagnosticReporter` (§5.3, Phase 3 in consumed-by), debug-flag namespace including `saveSources` (§5.3 row assigns flag 3), SPDX/`THIRD-PARTY.md` mechanism (§5.3), and the D-P1-49 jcpp admission row. No invented dependency API found; `GLCapabilityProfile` is the only GL-side input and no facade verb is consumed (ll. 4204–4205).

**Published exports and receiver adoption:** §5.1/§5.3 publish the complete surface gated on `CURRENT_SCHEMA_VERSION = 23` (ll. 754, 4211) with `MaterializedSource-v23` (ll. 3359, 4262) and `projectionVersion=1` (ll. 4091, 4236) — matching the wave's current-identity ground truth. Required dated schema23 receipts are published as obligations, not asserted as completed adoptions (ll. 562, 4269–4272, 5322–5326); the one current receipt cited, P2 D-P2-55 (l. 5283), matches ground truth, and no D-P2-32/D-P2-38 receipt is cited as current. Historical schema19–22 addenda are consistently marked historical with current-version assertions superseded (ll. 573–575, 4245–4256). The P13 "stripped and ignored" phrasing is correctly attributed as no-longer-extent request history: file-wide search of the current P13 doc returns zero matches, and the old coordinates (P13 ll. 531–541, 1647–1653) are retained as dated history, not current pins — handled correctly.

**Anchor spot-checks passed:** RC3 Part I ll. 92–1109 and Phase 3 spec ll. 1316–1470; RC3 `:1404–1412` (strip/ignore gap) and `:2441–2443` / v3 `:2476–2478` ("ours must honor them") verbatim; RC3 `:1417–1421` correctly characterized in D-P3-65 as not supplying Internal durability; `RESEARCH.md:603–605` likewise; PD `:626–630` ("pack PNG with `.mcmeta` blur/clamp"); P11 §5.2 at `docs/phase11/v1/PHASE_11_DOC.md:1184–1203` — heading and closed `CustomExpressionDecl` algebra exactly where §0.33 pins it.

**Anchor drift found (2 corrections, see §6):** the §0.55 P13 pin and the §0.57/D-P3-60 P4 pin both carry stale coordinates whose labels and content disagree; ironically both were asserted as re-derived during the R62 fix-up whose own §0.67 text defers "line-level drift" to "the pending separate citation-anchor re-derivation pass" — this wave.

## 5. Current-identity, scope, licensing, and decision dispositions

- **Scope discipline:** §1.1's ownership list and §1.2's anti-sprawl table cover every adjacent concern the spec scopes out (compilation→P4, buffers→P5, uniform values→P6, aliases→P9, expressions→P11, GUI/persistence→P12, texture loading→P13, async/PBO→P14, compute→G8/S2–S3); the negative vows at ll. 699–700 match the spec's "pure `:engine`" requirement. No scope creep detected; P3-C19/C22/C23 growth hooks are tagged and owned.
- **D-1…D-10:** §11.2 (ll. 5132–5139) disposes all ten binding decisions coherently (D-3 seven-pack gate, D-5 no-mixin, D-6 pure placement, D-7/D-8 licensing posture, D-9 compat-profile macros without GL, D-10 manifests/fixtures to P2).
- **Licensing/Pintonium compliance:** jcpp Apache-2.0 with notice routed through P1's `THIRD-PARTY.md` (ll. 63–64, 4202); Pintonium and Oculus handled as LGPL structure evidence with notice obligations and no 1.12.2-hook inheritance (ll. 65–67); `glsl-transformation-lib` prohibition, stareval clean-room posture, and no-AST statement present (ll. 68–69); the Oculus hard-blocklist boundary is never cited — a doc-wide search finds no `pipeline/transform`, `glsl-relocated`, `libs/`, or glsl-transformer reference. Published ARB/GLSL spec citations in §0.61 are evidence-only with restrictive terms acknowledged (ll. 521–525). Compliant with §G7/§G12.
- **§G1.3 status honesty:** the document nowhere claims self-PASS or implementation clearance; its "verified under §G1.3" statement (ll. 641–643, 5577–5581) is the post-R62 surface status and is exactly what this Attempt-11 whole-owner review supersedes/re-confirms. Open hand-offs (§11.4) correctly leave receiver adoption and Main integration pending.

## 6. Findings and verdict

**F-1 (correction) — stale P13 anchor at §0.55.**
Location: `docs/phase3/v1/PHASE_3_DOC.md:360` (§0.55), reaffirmed at l. 624 (§0.67).
Claim: the Phase-13-owned input is "requested at `docs/phase13/v1/PHASE_13_DOC.md:846-852` (§4.1.6)".
Evidence: recomputed against current P13 bytes — §4.1.6 "MC_NORMAL_MAP / MC_SPECULAR_MAP wiring" begins at `docs/phase13/v1/PHASE_13_DOC.md:865`; the pinned range 846–852 is the tail of §4.1.5 "Publication and stage applicability" (candidate origins, DefaultFill, canonical ordering) and contains none of the quoted phrases; "immediately after engineOptions" is at l. 868, "Off short-circuits" at l. 872, and the materializer wording "Empty/DefineCenterDepthSmooth, not a general macro bag" at ll. 875–876. §0.67 asserts this anchor was repaired "on current bytes"; it was not.
Severity: correction (evidence-anchor repair; the request itself exists verbatim nearby and no grant, §5 row, or schema meaning is affected).
Required cure: re-pin §0.55 (and the §0.67 repair record) to `docs/phase13/v1/PHASE_13_DOC.md:865-877` (or 867-877).

**F-2 (correction) — stale P4 coordinates at §0.57/D-P3-60.**
Location: `docs/phase3/v1/PHASE_3_DOC.md:426` (§0.57), l. 5111 (D-P3-60), l. 629 (§0.67).
Claim: P4's consumption is at "`docs/phase4/v1/PHASE_4_DOC.md:2263-2266` (§5.4 item 2)".
Evidence: recomputed — P4 §5.4 "Requested changes to dependency contracts" begins at `docs/phase4/v1/PHASE_4_DOC.md:2271`; item 2 spans ll. 2279–2283, and the quoted current wording "direct `mipmappedAfterPass()`/`vertices()` values" is verbatim at l. 2282. The pinned 2263–2266 is the tail of the §5.3 consumed-contracts table (its l. 2264 row does mention the grant, but it is not §5.4 item 2). Label and coordinates disagree.
Severity: correction (same anchor-drift class as F-1; quote itself is accurate; no contract change).
Required cure: re-pin to `docs/phase4/v1/PHASE_4_DOC.md:2279-2283` at all three sites.

**N-1 (note) — §0.1 dependency-review status is a build-time record.**
Location: `docs/phase3/v1/PHASE_3_DOC.md:25–26`.
Claim: "the assigned dependency status says `docs/phase1/reviews/PHASE_1_REVIEW_15.md` is literal PASS," justifying that no Phase 1 review was read.
Evidence: Phase 1 has since accrued twenty-two further review rounds (Attempt-11 registry: P1 reviewRound 37 → 38 in this wave) and byte changes (schema23 receipts D-P1-61→D-P1-65 among them). The citation is accurate as the original build session's assignment record but is not marked historical and no longer names Phase 1's current verification surface. Not a correction: this review independently verified the current P1 §5 satisfies every consumed interface (§4 above), which is what actually discharges the dependency gate; the registry's whole-owner re-review supersedes dependency-status bookkeeping wave-wide.

Not findings, verified en route: the §0.56 historical P13 coordinates (ll. 531–541, 1647–1653) are expressly retained as dated history, not current pins; the registry's own "citation-anchor re-derivation" wave language matches the two corrections' class; no `Resolutions` section is created here; fix-up of F-1/F-2 belongs to the standard §G1.3 correction path.

**Verdict: PASS-WITH-CORRECTIONS — blocking=0; corrections=2; notes=1.**

No build, test, Gradle, GL, capture, or network command was executed for this review; all evidence above is documentary, from repo-relative paths at the recomputed line numbers stated.
