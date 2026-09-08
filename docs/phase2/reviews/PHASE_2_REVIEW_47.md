# Phase 2 — architecture review 47

**Date:** 2026-09-08. **Frozen document:** `docs/phase2/v2/PHASE_2_DOC.md` SHA-256 `bc6a8379e10f39a9d285e407e36641ff94f16b6c323ec30cded272e445101484`. **Verdict:** PASS-WITH-CORRECTIONS (0 blocking, 2 corrections, 3 notes). **§5 impact:** no contract semantics change; both corrections are provenance/currency wording repairs, one of them inside the binding §5.1.2 receipt text, so the §5 surface is correct only after those two wording fixes and the document remains unverified until the next fresh whole-document review.

---

## 1. Scope, identity and authority actually verified

- **Hash recomputation:** `sha256sum docs/phase2/v2/PHASE_2_DOC.md` → `bc6a8379e10f39a9d285e407e36641ff94f16b6c323ec30cded272e445101484`, equal to the frozen assignment hash. 3566 lines. Review proceeded.
- **Governing-design confirmation at the doc header:** the header (lines 3–8) declares the §G1.1 rebuild adopting `docs/design/v3/DESIGN.md` for Phase 2 after Review 36's FAIL, with no source code written and no paid round launched. §0.1 (lines 18–19) records the whole of Part I and the whole Phase 2 spec as read. This is the correct governing-design declaration for this phase.
- **Complete reading list actually performed for this review:**
  - `docs/phase2/v2/PHASE_2_DOC.md` lines 1–3566 in full (all thirteen §G9 sections).
  - `docs/design/v3/DESIGN.md` Part I lines 1–1136 (§G0–§G12, incl. §G1.2 protocol, §G2 D-1..D-10, §G4, §G9 template, §G11.4) and Part II Phase 2 spec lines 1261–1342.
  - `docs/research/v1/RESEARCH.md` lines 11–107 (§0–§1) plus spot-check targets cited by the conformance map: §5.1 (655+), §8.1–§8.3 (891–938), §9 (940–958), §10.1, §11 OQ-10 row (line 1016), §12.5, Appendix G (1532–1551).
  - P1 `docs/phase1/v14/PHASE_1_DOC.md` §0 header (lines 5–31), §1 (1648–1703), §5 (5420–5563), §11 (6102–6303).
  - Consumer adoption sites: P7 `docs/phase7/v1/PHASE_7_DOC.md` §4.13/§4.13.1 (2025–2264) and §5.1 (2268–2273, row 3917, D-P7-34/37/38 at 4509–4513, 4622–4629); P11 `docs/phase11/v1/PHASE_11_DOC.md` §5.6 (1302–1433); P3 `docs/phase3/v1/PHASE_3_DOC.md` §5.1.1 (3994–4183); P4 `docs/phase4/v1/PHASE_4_DOC.md` §5.6 (2314–2433).
- **Boundaries respected:** read-only except this file; no `reference-src/**`, no chatlogs, no other review files, no build/test/GL/network execution. This review is documentary only: **no build, test, GL, capture or approval was executed.**

## 2. Scope and §5 audit

**Doc-gate criteria (spec lines 1330–1337):** all met literally. Every §9 exit criterion is traceable to a specified harness run (§3.5, all ten rows, each citing the executing run). Licensing is structural, not procedural (§4.10.3–§4.10.5, [D-P2-9] never-rehost grammar). The before-renderer subset is explicitly listed (§9.2, ten items). OQ-10 spike spec is complete with a designed-now fallback ([D-P2-21], §10.3 items 1–6). Motion scenes cover all six rendered families (§3.4, each with a dense explicit camera path) and shadow/sky/weather families are explicitly covered despite no working reference (§3.4 reference-gap classification).

**Conformance-map audit:** zero unmapped in-scope rows found. Spot-checks against current bytes passed: §3.1 rows match RESEARCH Appendix G verbatim (packs, versions, licence stances, including Sildur's `MODRINTH` honestly marked "pending verification" against its github.io source and §11.3 item 1); §3.2 tier gates quote RESEARCH §8.2 verbatim (lines 916–919); §3.3 maps §8.3 and §G6 accurately (RenderBook extension existence vs unproven CI viability kept distinct, lines 927–929); §3.5 quotes RESEARCH §9 exit criteria verbatim (lines 947–952). §3.4's Pintonium reference-gap rows carry `[V:observed — Pintonium/forge122/…]` provenance tags and N40-1 is properly de-verified (D-P2-44).

**Interface honesty — consumed:** every P1 item the doc consumes exists in P1's current §5: module/package layout and `com.schmaloogium.mod.conformance` ([D-P1-41]), C-4, `GLCapabilityProfile` + target maxima ([D-P1-66]), testFixtures placement ([D-P1-26]/[D-P1-27]), `RecordingGLDevice`/`GLCallLog`/`ScriptedResponses`/`ReplayAssertions` with the [D-P1-57] drawBuffers signature, [D-P1-67/69] typed clears, [D-P1-68/70] `borrowedVertexList(String, VertexLayout, VertexInputPlan)` (exact match), [D-P1-71] native texture lifecycle, [D-P1-63] synchronous target-bearing values, [D-P1-42] `ReplayAwareGLError`, [D-P1-45] `EngineDiagnostic`, the `schmaloogium.conformance` channel, the `recordGL`/`dumpCapabilities` debug flags, `CapabilityProbe`, the CI extension point and the version pin table. P3 §5.1.1 consumption is exact (`PackFrontEnds.create`, closed `Off|Failed|Inspected`, `CURRENT_SCHEMA_VERSION` 23, nine sections incl. `assets`, `projectionVersion` 1, archive join, `evaluateProgramStates(Optional.empty(), reporter)`); P4 §5.6 adoption is exact (explicit `Optional.empty()` profileSelection, same-request join proof, `ProgramResolutionProjection` incl. `ownBuild`, `ShadersOff` no-fabrication, close-in-finally, `INVALID_PROGRAM_STATE`); P7 §4.13/§4.13.1 and D-P7-34/37/38 adoption matches the receiver side line-for-line (50000000 ns / 1 tick / 0 partial initial schedule; warm-up `max(60, ceil(8·largestHalfLifeTicks/ticksPerFrame))`; `UniformReplayErrorSink` required factory sink, callback-before-return, ordered exact P1 verdicts); P11 §5.6 adoption is exact (interface shape, step variants, verdict enum, result record, mandatory seven families, all six D-P11-27 vectors).

**Interface honesty — promised:** P7 has adopted capture-plan/4 + run-manifest/4 as its only wire versions and incorporates P2 §§4.5.1–4.5.4 and §5.1.1's R39/R55/R40 receiving contracts (P7 lines 2027–2036); receiver adoption of the recent grants is recorded on the receiving side (P7 D-P7-34/35/37/38, 4509–4513, 4622–4629). P11 §5.6, P3 §5.1.1 and P4 §5.6 all record their adoption of P2's published rows. No promised-but-unspecified surface found.

**Scope discipline:** nothing foreign to §1's boundaries; the adjacency table maps every tempting spill (pack-parsing internals → P3, reference-render gap → research-level, benchmarking → §7-explicit-out, scene content tuning → §3.4 declared out). Nothing in-scope silently dropped: tier machinery, scene spec, capture automation, diff, T2 oracle protocol, fixtures, headless golden harness, RenderBook evaluation, milestone mapping and the impl gate are all specified.

**Template completeness:** all thirteen §G9 sections substantive; every assigned OQ (OQ-10) carries a full spike spec with verbatim RESEARCH §11 question (line 1016 matches §10.3 item 1 exactly), concrete procedure A–D, success criteria S1–S6 and fallback [D-P2-21].

**Binding decisions D-1..D-10:** §11.2 dispositions present for all ten; D-8 honored (black-box OF oracle only, §4.2.6/§4.7.2/§4.8.2), no contradiction found.

**Licensing/Pintonium compliance:** no glsl-transformer/AGPL trace; no new parsing dependency ([D-P2-14]); RenderBook MIT attribution routed through P1's THIRD-PARTY mechanism with §8.1 archive-deletion and attribution rules; OF G6 black-box only; do-not-inherit rows respected (§G11.4 not invoked — §4.11.7 states this explicitly and correctly).

**Current-identity discipline:** schema23 + `MaterializedSource-v23` (§4.11.4 line 2012, §5.1.1, D-P2-55), `RegistryFingerprint`/profile-selection-v3 (§4.11.4, §5.1 R32/R39), ShadowHookHealth flattened-v3 with 66 rows / 65 non-CLOUD (D-P2-62/67), TextureHookHealth application-v2 (D-P2-59/63), capture-plan/4 + run-manifest/4 as the only current majors with `/1`–`/3` unrepaired, `projectionVersion1` nine-tree identity, `ownBuild` in manifest program rows, option-state `optionStateSha256` baselines (D-P2-41), authenticated comparison/oracle/approval evidence (D-P2-42), `gl.profile_text` sole transport on /4 (D-P2-43), qualified historical README attribution (D-P2-44), exact tier domains (D-P2-47), complete resolved generation identity (D-P2-48), sole canonical image layout (D-P2-49), combined P3+P4+P5 matrix-golden provenance with runtime sizing inputs and capability projection without live GL (§4.11.4) — all cited as current and confirmed against the receiver sections. Two defects found, both below.

## 3. Required corrections

**C47-1 — Round-46 review cited by D-P2-72 is unrecorded in §0.1 and the closing history.**
- *Sites:* `docs/phase2/v2/PHASE_2_DOC.md:3283` (D-P2-72 cites "C46-1"); §0.1 input table lines 29–34 records `PHASE_2_REVIEW_40..45.md` but no `PHASE_2_REVIEW_46.md`; closing history lines 3558–3566 names R43/R44/R45 only.
- *Failure path:* D-P2-72 exists to satisfy C46-1's "provenance truth" demand, yet the very review that produced C46-1 is absent from the provenance record, while strictly older rounds (40–45) are recorded. A reader reconciling the decision log against §0.1 cannot source D-P2-72's finding identifier; this repeats the exact defect class §0.21–§0.25's history shows this document was repeatedly corrected for.
- *Owner + receivers:* P2 fix-up session owns the repair; receivers are the §0 provenance record and any reviewer tracing D-P2-72.
- *Minimal resolution:* add the round-46 review to the §0.1 inputs table with its extent/verdict, and add the corresponding sentence to the closing history (verdict record only; no contract change).

**C47-2 — §5.1.2 cites stale `P3 schema21` in unqualified current-tense prose.**
- *Sites:* `docs/phase2/v2/PHASE_2_DOC.md:2612` ("P3 schema21, golden schema1 and all nine source-free projectionVersion1 trees remain unchanged.") versus §4.11.4 line 2012 ("**23**, current receipt; preceding numeric receipts are historical"), §5.1.1's D-P2-55 receipt, and closing history lines 3554–3556 ("current admission is exclusively schema23").
- *Failure path:* §5 is the binding interface region. The clause groups `schema21` with genuinely current identities (`golden schema1`, `projectionVersion1`) under "remain unchanged", so a receiver reading §5.1.2 alone could admit schema-21 documents as current, contradicting D-P2-55 and the document's own closing-history rule that schema-21 receipts describe their dated amendment only.
- *Owner + receivers:* P2 fix-up session owns the wording repair; receivers are P3/P4/P7 consumers of the §5.1.2 receipt.
- *Minimal resolution:* qualify the clause the same way the closing history does — e.g. "the receipts' then-current P3 schema21 admission is historical (superseded by D-P2-55's schema23); golden schema1 and all nine source-free projectionVersion1 trees remain unchanged."

## 4. Notes

1. Line 1368: "Every row expects one;65 non-CLOUD rows" — missing space ("one; 65"). Cosmetic.
2. D-P2-39's decision-log row retains "into unchanged `/3` gl_errors" wording. This is accurate as a dated 2026-09-08 record and is adequately superseded in the body: the R40 receipt explicitly supersedes the capture-wire portion of D-P2-34/39 and §5.1.2 states "Current capture-plan/4 and run-manifest/4 supersede those receipts' historical `/3`". The current envelope is confirmed to be `/4`, with the dense gl_errors grammar carried on the /4 manifest (§4.5.4).
3. The `/4` profile-text transport is attributed to D-P2-43 in this document, with D-P2-44 carrying the N40-1 README-attribution qualification; the two are internally consistent throughout body and log. External references that label the transport "D-P2-44" should use the document's own numbering.

## 5. Areas audited and found sound

Doc-gate conformance (all six literal criteria); §3.1–§3.5 conformance-map fidelity against RESEARCH Appendix G/§8/§9 current bytes; P1 consumption (all fifteen consumed surfaces verified against P1 §5 current text); P3 §5.1.1, P4 §5.6, P7 §4.13/§4.13.1/§5.1 and P11 §5.6 receiver adoption recorded on the receiving side; §5.1 exposed-row promises (R1–R60 family) all specified with receivers; scope in/out discipline; thirteen-section §G9 completeness; OQ-10 spike completeness incl. fallback; D-1..D-10 dispositions; D-P2-39..72 decision-chain coherence (incl. the /4 envelope confirmation above); licensing posture (never-rehost grammar, golden no-source rules, oracle-image rules, RenderBook MIT handling, no AGPL/glst trace); Pintonium provenance tagging and §G11.4 non-invocation; current-identity discipline for all twelve shared identities listed in the assignment (two exceptions corrected above); §6 failure ladder incl. the 2a rung; §7 threading rules; §8 test plan; §9 staging and impl gate; §12 checklist ordering.

## 6. Verdict

**PASS-WITH-CORRECTIONS** — 0 blocking, 2 corrections (C47-1, C47-2), 3 notes.

The document is structurally sound, interface-honest in both directions, and current on every shared identity checked except the two provenance/currency wording defects above, both fixable without contract change. This review is documentary only: no build, test, GL, capture or approval was executed, and this verdict does not constitute integration clearance or supersede fresh whole-document review.

---

## Resolutions

**Date:** 2026-09-08. Applied by the Phase 2 fix-up session; the report body above is unchanged. All anchors below are recomputed against the post-fix bytes of `docs/phase2/v2/PHASE_2_DOC.md`.

- **C47-1 — applied.** Round-46 review is now recorded in both provenance surfaces: a `PHASE_2_REVIEW_46.md` row was added to the §0.1 inputs table as its last row (`docs/phase2/v2/PHASE_2_DOC.md:35`, after the REVIEW_45 row) recording the whole-owner assessment of the frozen attempt8 owner with its PASS-WITH-CORRECTIONS verdict (required provenance correction C46-1, repaired as D-P2-72 and extended to R46 itself as D-P2-73; historical verdict, not certification) — extent/verdict verified against `PHASE_2_REVIEW_46.md`'s own header (whole-owner, 3,552-line frozen attempt8 owner, PASS-WITH-CORRECTIONS, 1 correction + 5 notes). The corresponding closing-history paragraph was appended after the R44/R45 paragraph at the end of the document (`:3570-3575`) recording R46's verdict as historical over the bytes it read. The repair is recorded as new decision `D-P2-73` in the §11.1 ledger (`:3286`, next unused id after the max D-P2-72): "C47-1: record R46 incorporation in §0.1 inputs and the closing history". Verdict record only; no contract, §5 or schema change.
- **C47-2 — applied.** The stale clause at the §5.1.2 receipt (formerly `:2612`, now `:2613-2614`) was rewritten from "P3 schema21, golden schema1 and all nine source-free projectionVersion1 trees remain unchanged." to "the receipts' then-current P3 schema21 admission is historical (superseded by D-P2-55's schema23); golden schema1 and all nine source-free projectionVersion1 trees remain unchanged.", matching the closing-history rule (schema-21 receipts describe their dated amendment only; current admission is exclusively schema23/current-constant equality under D-P2-55, per §4.11.4's "**23**, current receipt; preceding numeric receipts are historical" and the §5.1.1 D-P2-55 receipt). Wording-only currency repair; no contract semantics change.
- Notes 1–3 were not applied (no correction incorporates them), per the report's own assessment that they are cosmetic/nonblocking.
