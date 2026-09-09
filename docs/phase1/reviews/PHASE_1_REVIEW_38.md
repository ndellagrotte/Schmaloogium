# Phase 1 - architecture review 38

**Date:** 2026-09-08 · **Frozen artifact:** `docs/phase1/v14/PHASE_1_DOC.md` — SHA-256 `05abca4b2fa4c0b7dc1bf3a8f4a13ac8543b5b5e2d64134d3a471217153abb47`, recomputed at wave open and identical to the attempt-11 registry value (`docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_11.json`, review round 38); gate open. · **Verdict: PASS-WITH-CORRECTIONS — 0 blocking, 2 corrections, 1 note.** · **§5 impact: none** — no finding reaches §5; both corrections are citation-accuracy defects (one live, one in a historical §0 addendum) and no exposed or consumed interface, signature, or receipt changes.

---

## 1. Session protocol and hash gate

Whole-owner fresh review of the complete phase document (7,053 lines, all read), under §G1.2's verify-session protocol. The frozen SHA-256 was recomputed before any other read and matched the registry. Governing designs read: `docs/design/v2.0-RC2/DESIGN.md` Part I (§G0–§G10) plus the Phase 1 spec (ll. 957–1067) — RC2 governs this phase per the doc's §0 declaration and the post-§0.11 rule; `docs/design/v1.1/DESIGN.md` was consulted only where the doc's pre-§0.11 addenda record v1.1-era findings. `docs/research/v1/RESEARCH.md` ll. 11–107 read in full, plus every section P1 cites (§1, §3.2, §3.4–§3.5, §4.1–§4.4, §5.1–§5.3, §6.1–§6.2, §7.1–§7.2, §7.4, §9, §10.1–§10.3, §11, §12.2, §12.4–§12.5, Apps A–H at the cited coordinates). PD spot-checks at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` (§2 inventory l. 91, §16 bootstrap/mixin ll. 184, 765, 769, §17 B6 l. 792). Phase 1 is Wave 0: it consumes no other phase document, so the dependency-doc set is the two DESIGN revisions, RESEARCH.md, PD, and template/repo ground truth. Anchor sampling: ~40 dependency citations re-derived at their stated coordinates (line numbers below). No build, test, gradle, GL, or network was executed; no git operation was run; nothing outside this report file was written.

## 2. Doc-gate audit (RC2 ll. 1056–1060)

All five gate elements present and grounded:

1. **Module/package layout with dependency rules as testable constraints** — §2.2 quotes the §G3.1 constraint and the quote is exact (RC2 ll. 453–454: "`:engine` compiles with no classpath entry from Minecraft/Forge/Cleanroom/Mixin/LWJGL, and `:mod` never reaches into `:engine` internals beyond its published interfaces."); §4.2/§4.3 decompose it into mechanically checkable assertions (bytecode forbidden-prefix lists, `seam`-assertion testables, per-layer scan rules).
2. **D-1..D-10 satisfied or deferred with owner named** — §11.2 (ll. 6216–6233) is a complete ten-row disposition: D-1, D-2, D-5 (wiring), D-6, D-7, D-8 satisfied; D-3 → Phase 2, D-4 → Phase 4, D-5 catalog → Phase 7, D-9 → Phases 5+/7, D-10 → Phase 2. Every deferral names its owner.
3. **Pin table with re-verification procedure** — §4.2.6 (ll. 2479–2499): loader pin, Unimined kappa, Gradle, Java 25; re-pin procedure ("no bump" rule, template-repin boundary, reconciliation mechanics) consistent with RC2's Phase 1 spec ll. 981–985 (pin in `build.gradle`'s unimined block, not `gradle.properties`) and RESEARCH.md §5.1 (template pin 0.5.17-alpha vs current 0.6.6-alpha, l. 665).
4. **Glue-seam completeness check against PD §2's inventory** — §4.12/§4.13 present, including the deliberately-excluded table (ll. 5263+) whose D-1 reasoning matches RC2's "we have one backend" count note (ll. 1002–1003, 1054).
5. **Bootstrap sequence adopted or deviation justified** — three-stage bring-up adopted (§4.13); stage-2 ownership analysis and its two recorded third-kind consumer entries match PD l. 769's sequencing and PD l. 792's B6 caution.

## 3. Conformance-map audit (P1 §3)

Phase 1 owns almost no pack-facing contract surface; the map's ~24 rows cover exactly the facade/debug-affordance items in scope. Sampled rows re-derived against RESEARCH.md/RC2 coordinates:

- **Startup probes + mipmap gate** — RESEARCH.md §4.1 step 1 (ll. 476–477) names exactly the four probes and the GL-3.0 mipmap requirement; the §3 rows and the §3.1 flagged-delta ruling match, and the REV2 upstream citation split is real at RC2 ll. 992–997, quoted verbatim by P1's row and §3.1.
- **Macro header / extension set** — RESEARCH.md §3.5 ll. 313–317 (on-demand `MC_<GL_extension>`), grounded.
- **`shaders.debug.save`** — App F.8 l. 1527, grounded; KHR_debug reservation per §G4.5 correctly `[V:design]`-tagged.
- **`centerDepthSmooth`** — §3.2 l. 251, §4.4 l. 559 (per-frame stall), App A.3 l. 1167, §6.2 PBO row l. 773: all four cited coordinates carry the claimed content.
- **Depth moments** — App B.2 ll. 1221–1226 (`depthtex1` before translucents, `depthtex2` before weather): grounded.
- **Texel upload / noise** — §4.1 step 4 l. 488 and App F.5 l. 1491: grounded.
- **Fixed unit map** — App B.3 ll. 1232–1233 (unit 0 `texture`, unit 1 `lightmap`) and the depthtex1-at-unit-11 authority note ll. 1251–1255: grounded, and the doc-vs-doc quirk P1 relies on is real.
- **`ivec2`/`ivec4` uniforms** — App D.1 l. 1332, D.3 l. 1367–1368, D.4 l. 1377: grounded.
- **Fixed-function terminals / absent `final`** — App A.1 ll. 1111–1115 (`(none)`/fixed-pipeline rows) and l. 1141 (`final` passthrough): grounded at the cited range :1106–:1145.
- **ARB geometry form** — ll. 213–215 (layout qualifiers **or** ARB + `maxVerticesOut`), l. 1161 (legacy configuration row), l. 771 (internal-translation opportunity): all grounded; the row's honest "neither grant alone establishes dual-form conformance" limitation matches D-P1-44's scope.
- **Compat profile / no UBOs / `org.lwjglx`** — §6.1 ll. 758–759, 761 and RC2 §G2.2 l. 359: grounded (the `org.lwjglx` prohibition is in both cited sources).
- **`countInstances` routing rows** — §3.2 l. 255, App A.3 l. 1160, App D.4 l. 1378, and RESEARCH.md §4.4 as the only observed loop site: grounded; the composite-only scope of D-P1-33 and the maintainer-approved gbuffers/shadow extension (D-P1-47) are stated as unverified where they are unverified. One defect in this cluster: see F-38-1 (the §3.5 `gl_InstanceID` citation).
- **`alphaTest`/`blend`/`scale` routing (V14-2 corrections)** — RC2 l. 1245 (P3 parse/store), ll. 1334/1365 (P4 carry/lock), l. 1601 (P6 blendFunc observation in scope-in), l. 1683 (P7 composite mipmap/execution), l. 2410 (coverage row "3 (parse), 4 (apply), 7 (execute)"), l. 2400 (v0.1 scope row), l. 1469 (P5's only "mipmap" occurrence inside ll. 1409–1525), l. 2090 ("Pure `:engine` code"), App F.7 l. 1517 (per-program blend form, no per-buffer axis), §3.6.7 l. 430 (`PER_BUFFER_BLENDING`): every sampled coordinate carries the claimed text. The V14-2 deletions are correctly executed corrections, not re-homings.
- **P13/P5-facing rows** — RC2 ll. 2269–2271, 2297 (custom-texture source forms) and l. 570 (§G5.1 registry row): grounded.

## 4. Interface honesty, identities, and scope discipline

**§5 exports/consumes.** Wave 0 consumes no phase interfaces; §5.1's exports carry downstream adoption state and §5.2's consumer entries were checked against the wave's current-identity ground truth: D-P1-61/schema22 is explicitly marked historical and superseded (ll. 5519, 6202), and the current admission is schema23 / `MaterializedSource-v23` / `projectionVersion1` under D-P1-65 (ll. 5523, 6206) — matching the known current identities exactly. The capture-plan/manifest mention (l. 1801) is unversioned module-responsibility wording and pins no stale identity. No schema20/21, no P2-v1-as-current, no stale health/receipt identities anywhere in the document.

**Scope discipline.** §1's ownership table (l. 1713: harness → Phase 2, pack-format → Phase 3, GL policy → Phases 5+, GUI → Phase 12) matches RC2's scope-in/out bullets (ll. 971–1038) with no swallowed neighbor concerns; the Pintonium sections (§4.12–§4.13) evaluate-and-disposition rather than import mechanisms, per §G11.

**Seam/Kirino evidence.** The §2.2 requirement rationale quotes RESEARCH.md §5.2 (393 commits, "will not be compatible with existing render mods", ll. 716–722) and §7.2's wholesale-replacement sentence (l. 833) accurately; the RC2 coordinates it cites (§G3.1 notes ll. 456–464; §G10 OQ-20 row l. 827) and PD l. 91's "not headless-testable" admission all verify. The doc's D-6-portability/D-10-testability split of that evidence is faithful to the sources.

**Licensing/Pintonium compliance.** §4.8: `LICENSE` at repo root is the verbatim GPL-3.0 text (verified by direct read; the D-7 row's `[V:repo]` claim and commit reference are consistent); SPDX header convention, `THIRD-PARTY.md` as the D-8 mechanism with both standing prohibitions (AGPL glsl-transformer never-copy; decompile behavioral-observation-only), and the OQ-12 note plus the Pintonium LGPL-3.0 relationship sentence required by RC2 ll. 1032–1035 are all present (§4.8.1–§4.8.4). Iris LGPL-3.0 / Angelica LGPL-3.0-with-MIT characterizations match RESEARCH.md §10.3 ll. 991–992.

**Template completeness.** All thirteen §G6 sections present (§0–§12, verified by heading scan). §10 carries one full spike specification per assigned OQ — §10.1 OQ-2, §10.2 OQ-12, §10.3 OQ-20, §10.4 OQ-21 — each with procedure, success/failure criteria, and fallback/disposition (§10.4's ll. 6093–6116 read in full).

## 5. Findings

**F-38-1 (correction) — fabricated anchor: "GLSL 120 has no `gl_InstanceID`" cited to RESEARCH.md §3.5, which does not contain it.**
- *Location:* P1 ll. 1921 (§3 conformance-map row), l. 4150 (§4.7.4 instanced-draw verb rejection), l. 6174 (D-P1-33 rationale); the same clause recurs at l. 206 (historical §0 addendum) and l. 6518 (§11.4 hand-off).
- *Claim:* the no-`gl_InstanceID` fact is attributed "(RESEARCH.md §3.5)" (ll. 1921, 4150) and "(RESEARCH.md §3.5)" via "GLSL 120 has neither" (l. 6174).
- *Evidence:* a file-wide search of `docs/research/v1/RESEARCH.md` for `gl_InstanceID`/`InstanceID` returns zero hits, and §3.5 (ll. 305–327) read in full covers the GLSL-120-era fixed-function vocabulary, the macro header, preprocessor support, and the era bridge — no instanceID statement of either polarity. The OpenGL fact itself is true (GLSL 120 has no built-in `gl_InstanceID`); only the citation is unsupported. The neighboring propositions in the same rows (`instanceId` is an `int` *uniform*, App D.4 l. 1378; the loop observed only on composite/deferred, §4.4) are correctly grounded, so D-P1-33's conclusion survives on App D.4 + §4.4 alone.
- *Severity:* correction — five sites must drop or re-source the §3.5 clause (e.g., cite it as engine-general GL knowledge, or RESEARCH.md App D.4 for the uniform fact it does carry). No §5 or §3 routing change follows.

**F-38-2 (correction) — §0.9 input description misattributes the slot count to RESEARCH.md.**
- *Location:* P1 ll. 691–692 (§0.9 fix-up addendum, round ten's input list): "RESEARCH.md **§4.2** (the ~90 built-in uniforms and the 43 slots)" and "**App A.1** (what the 43 actually counts)".
- *Evidence:* RESEARCH.md §4.2 (l. 494) states "60 classic program slots" and App A.1's count line (ll. 1143–1144) reads "Count: 60 named shader/virtual slots"; "43" appears nowhere in RESEARCH.md (file-wide search). The 43-slot figure is DESIGN.md's (RC2 ll. 570, 1317, 1332). The addendum therefore describes its own declared inputs with a number the cited sources do not contain, in both the §4.2 and the App A.1 attributions. The "~90 built-in uniforms" half is accurate (§4.2 l. 505).
- *Severity:* correction, confined to a dated historical addendum with no live-contract repetition (no other "43" in the document; §3/§4/§5 carry no slot-count claims). Related, recorded for upstream rather than charged to P1: RC2 l. 1332 itself attributes "all 43 slots" to RESEARCH.md App A.1, whose own count is 60 — a RESEARCH↔DESIGN numbering divergence the design revision may want to reconcile.

**N-38-1 (note) — §0.12's P2 migration remark is historical only.**
- *Location:* P1 l. 1079: "the one `docs/MOVES.md` names: `PHASE_2_DOC.md` has not migrated to RC2."
- *Evidence:* this is a dated 2026-07-26 record inside the §0.12 addendum; the current Phase 2 document is v2 (`docs/phase2/v2/PHASE_2_DOC.md`, frozen in the attempt-11 registry). The doc's own convention ("historical §0 addenda remain records of what each session was told") covers it; no edit required. Noted so no reader takes the remark as current state.

## 6. Verdict

**PASS-WITH-CORRECTIONS** — 0 blocking, 2 corrections (F-38-1, F-38-2), 1 note (N-38-1).

The document is structurally complete (thirteen §G6 sections, full spike specs for all four assigned OQs, complete D-1..D-10 disposition, pin table with re-verification procedure, PD §2 completeness check, adopted bootstrap sequence) and its contract surface survives a ~40-coordinate anchor audit with the routing rows (alphaTest/blend/scale, mipmap, blendFunc ownership, countInstances scope) verified exactly against RC2, RESEARCH.md, and PD at their stated coordinates. The two corrections are citation-accuracy defects: a five-site fabricated §3.5 attribution for a true GL fact (F-38-1) and a historical addendum's misquote of RESEARCH.md's slot counts (F-38-2); neither touches §5, no interface or receipt is wrong, and the document's current-identity handling (schema23/`MaterializedSource-v23`/`projectionVersion1` current; schema21/22 receipts marked historical) is exactly right. No build, test, gradle, GL, or network check was executed, and none is needed for a documentary verdict; the §5 impact of this review is nil.
