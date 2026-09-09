# Phase 11 — architecture review 18

**Date:** 2026-09-08 · **Frozen target:** `docs/phase11/v1/PHASE_11_DOC.md` · **SHA-256:** `840850c8ac575f974d4d7c71f96dd0e679f1c9646eb25708265fe91177a714be` — recomputed at wave open against `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_11.json`; exact match, hash gate passed.

**Verdict: PASS-WITH-CORRECTIONS** — 0 blocking, 1 correction, 3 notes.

**§5 impact:** none — the single correction is a §0.1/§0.2 provenance-record addition; no `## 5` text changes, so under §G1.3 the fix-up closes the phase without a fresh verify session. The document's own standing requirement (§0.13's fresh verification round before closure) is unaffected by this review.

**No build, test, gradle, GL, or network command was executed; no implementation clearance is asserted. This review is documentary.**

---

## 1. Protocol and reading performed

Fresh independent adversarial reviewer per §G1.2 (`docs/design/v3/DESIGN.md:303`–`:342`); no author context; refutation posture. Read completely: the frozen phase doc (all 1764 lines). Governing design: `docs/design/v3/DESIGN.md` Part I §G1–§G12 and the Phase 11 spec (`:2279`–`:2353`). `docs/research/v1/RESEARCH.md` ll. 11–107 (§0–§1, D-1..D-10) plus every RESEARCH range the doc cites. Dependency docs: `docs/phase3/v1/PHASE_3_DOC.md` and `docs/phase6/v1/PHASE_6_DOC.md` at §0/§1/§5/§11 plus every cited byte range; incorporated sections followed where cited (P6 §4.13/§4.14, P3 §2.2/§5.1/§5.3, P4 `:1953`–`:1954`, PD §14, MOVES version-label record). Hard boundaries observed: no `reference-src/**`, no `docs/**/chatlogs/`, no root `*.txt`, no other `PHASE_*_REVIEW_*.md`; nothing outside this report file was modified.

## 2. Doc-gate compliance

Every literal criterion of the spec's Doc gate (`docs/design/v3/DESIGN.md:2344`–`:2348`) is met:

- **Conformance map complete.** Every App F.6 token/operator/function appears: both declaration forms (`docs/research/v1/RESEARCH.md:1495`–`:1496` verified verbatim), all operators (`:1506`), and the exact function list at `:1507`–`:1510` maps 1:1 onto the 32 rows of `PHASE_11_DOC.md` §3.2 — zero unmapped rows, zero extras (the doc explicitly rejects reference-only extensions, §3.2 tail).
- **`smooth()` state machine specified** — §4.7 gives exact arity resolution, key identity, per-cell transition steps 1–6, clock/lazy-timestamp/transactional-commit semantics, and reset set.
- **Evaluator interface + decision criteria written** — §4.11: backend SPI, canonical semantic ID, unsupported-ID behavior, measured budget (p95 ≤ 0.25 ms, p99 ≤ 0.50 ms, zero steady allocation), interpreter-first per G2.5 (`DESIGN.md:451`–`:456`, anchor verified).
- **Error semantics per the ladder** — §6 maps every failure to rungs 1/2/2a/feature-2a; rung 1 quoted against `DESIGN.md:419`–`:425` (verified).
- **REV1 stareval outcome recorded** — §0.2 item 3 + §0.3 + §11.2 item 3: unverifiable → clean-room, matching `DESIGN.md:920`–`:922` (anchor verified); `IrisFunctions` checklist cross-referenced (§3.2/§3.4) with PD §14 (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:719`–`:725`, verified).
- **Template completeness** — all thirteen §G9 sections (0–12) present and substantive; §10 carries a full spike spec (question/procedure/owner/success/trigger/fallback) for the unassigned-OQ OQ-22 handoff, correctly noting `DESIGN.md:2281` assigns none ("OQs: —", verified).

## 3. Conformance-map and anchor audit

Spot-checks of mapped rows against cited text — all verified semantically exact at the stated coordinates:

- Cadence: `RESEARCH.md:1380`–`:1383` ends "custom uniforms on every program switch after built-ins" (§2.2's quote).
- Fourteen view booleans: `:1498`–`:1501` matches §3.3's list name-for-name.
- Seven-name exclusion: `:1501`–`:1505` (five D.4 dynamics + `fogMode`/`fogColor`, "does not override or narrow D.4") — the doc's D-P11-9 correctly follows RESEARCH over DESIGN's stale five-name restatement at `DESIGN.md:2300`–`:2303` (confirmed stale; §11.4's requested upstream correction is justified).
- §3.4 item 4 independently restates the same exclusion union and "updated on program change".
- `pi` = decimal `3.1415926` → binary32 `0x40490fda`: independently recomputed, correct.
- OQ-22 row: `RESEARCH.md:1028` includes "expression-engine compilation"; owner P14 per `DESIGN.md:885`; §6.3 row `:787` tags MethodHandle/bytecode `[U]` (§4.11's citation exact).
- §5.6 vectors: every hex literal recomputed and correct (`-2`=`c0000000`, `1`=`3f800000`, `0.75`=`3f400000`, `-1`=`bf800000`, `0.25`=`3e800000`, `9`=`41100000`, `4.5`=`40900000`, `2`=`40000000`, `3`=`40400000`); `%` vs `fmod` on (−5,3) numerically verified (−2 vs 1); the three `EXPR-SMOOTH` trace tables (clocks 0,1,1,2,2; committed times; raw bits) re-derived from §4.7's steps and hold, including lazy-timestamp retention and zero-dt same-frame repeats; cycle/invalid-dependency/isolation expectations match §§4.1/4.5; "these six D-P11-27 vectors" is exactly the six shared-graph rows.
- Dependency anchors at current bytes, all verified: P3 `:1776`–`:1779` (declaration rows: types validated, raw unescaped text + occurrence order lossless, "no expression evaluation occurs here") and `:1779` (precipitation → Phase 7); P3 `:1281`–`:1292` record algebra matches §5.2's quote field-for-field; P6 `:1539`–`:1543`, `:1546`–`:1564`, `:1565`–`:1589`, `:1592`–`:1599`, `:1605`–`:1610` (seven-name union), `:1663`–`:1672` (accepted prefix + superseding invalid-counter branch), `:1525`–`:1676`, §4.14 retirement algebra (`retire(UNPUBLISHED_ABORT|REPLACEMENT|SHUTDOWN)` → `Retired|AlreadyRetired|Rejected(WRONG_THREAD|ACTIVE_CALLBACK)`, final-use ordering, replacement-follows-actual-barrier-invalidation) all match §§4.8/4.12/5.3/5.4. P4 `:1953`–`:1954` confirms D-P4-43's `RegistryFingerprint/profile-selection-v3`; `docs/MOVES.md:89`–`:92` records P11 v1 §0's v3 declaration.

## 4. Interface honesty, current identities, scope, licensing

- **Consumed interfaces exist.** P3: `CustomExpressionDecl`/`customExpressions()` in §2.2/§5.1 (`:3406` row: complete declaration payload participates in the configuration fingerprint); schema discipline in §5.3 (`CURRENT_SCHEMA_VERSION = 23` at `:754`, `MaterializedSource-v23` domain, `projectionVersion=1`). P6: bridge, schema, view, sink, result, and retirement algebra all in §4.13/§4.14/§5.1. `fogMode`/`fogColor` confirmed inside RESEARCH Appendix D (D.2), so "D.1–D.3 minus fog two" (§4.4) is accurate.
- **Exports specified with receiver adoption.** P2 v2 (current document) adopts `RUN-EXPRESSION-CONFORMANCE` at D-P2-28 (`docs/phase2/v2/PHASE_2_DOC.md:247`, decision log `:3241`, §4.9 row `:1758` consuming "the complete P11 §5.6 contract" `:1763`) — §0.15 C-3's rejection of C-3 stands verified. P7 (5 hits) and P12 (6 hits) adopt the §4.9.1 GUI projection; P14 accepts the §10.1 methodology and `ExpressionMetricsSink`/L-11 (`docs/phase14/v1/PHASE_14_DOC.md:1921`, ledger `:2213`) — the doc's "documented/unverified" characterization is accurate.
- **Current-identity discipline.** No stale identity is presented as current: schema23/`MaterializedSource-v23` current (D-P11-25, header, §5.2); D-P11-19/20/21 receipts explicitly historical; `own-build-v1` receipt-time only, `profile-selection-v3` current (D-P11-31); `projectionVersion=1` matches; P2 cited only at v2; no ShadowHookHealth/TextureHookHealth/capture-plan/run-manifest/phase13-parameters claims exist to go stale. Dependency set = §G5.1 row 11 exactly (3, 6, no 7; `DESIGN.md:623`).
- **Scope discipline.** Scope-in fully designed (grammar, binding, variables, typing, cadence, evaluator, isolation, provider seam); Scope-out untouched (P6 acquisition, P3 capture — §1.2 records both; GUI presentation → P12).
- **D-1..D-10 honored**; no contract-visible component "improved" — the smooth correction is a tagged behavior-only restatement (D-P11-7, `[V:observed]`-tagged digest), the language surface is pinned to App F.6.
- **Licensing/Pintonium compliance.** stareval gate closed clean-room per `DESIGN.md:920`–`:922`; no glsl-transformation-lib trace; OF material consumed only as the behavior digest ("No decompiled source was read", §0.2); PD §14's "full checklist" claim refuted from the reference source and recorded (§11.2 item 2); §3.4 dispositions cover the §G11.5 do-not-inherit rows relevant to P11.
- **Verification posture honest.** The doc claims no PASS, no implementation, no measurement; P3/P6 current bytes are themselves marked unverified (P3 `:5565`–`:5568`, P6 `:314`), matching §5.3's "current, unverified" adoption language.

## 5. Findings

- **F18-1 (correction).** Location: `docs/phase11/v1/PHASE_11_DOC.md:27`–`:43` (§0.1 inputs table) and `:47`–`:64` (§0.2), versus the claim at `:224`–`:226`. Claim: §1.2 states "this fix-up reads the current P7 lifecycle solely for its published consumer handoff", but §0.1 records no `PHASE_7_DOC.md` input and §0.2's "three genuine gaps" enumeration does not include it. Evidence: §G9 §0 requires inputs actually read and deviations from the assigned reading list to be recorded; the spec's Required inputs (`DESIGN.md:2332`–`:2337`) name P3/P6, not P7, and P11 declares no Phase 7 dependency — the read is legitimate handoff-only reading (the P12-soft-7 precedent, `DESIGN.md:630`–`:632`) but is unrecorded. Severity: correction. Add one §0.1/§0.2 provenance row; no semantic or §5 change.
- **F18-2 (note).** Location: `:171`–`:189` (§0.15/§0.16). Claim: the Review-16/17 fix-up narratives record then-current anchor coordinates (`P3 :1748`; P6 `:1537`–`:1541`, `:1544`–`:1562`, `:1590`–`:1597`) that no longer match current dependency bytes (+28 in P3 after D-P3-73/74; +2 in P6 after §0.28/§0.29 and the Phase-C re-derivation). Evidence: live citations re-verified correct at current bytes (§3 above); P3 `:1748` is now the `version.<mcver>` row. Under the doc's own addendum convention (§0.11: pointers rely on the newest restatement, narratives are historical) no live pin is wrong. Severity: note — optionally tag the two narratives as coordinate-historical.
- **F18-3 (note).** Location: `:1267` (§5.3) and `:49` (§0.2). Claim: two citation ranges are wider than their targets — "(§4.13, `:1521`–`:1673`)" starts at §4.12's closing prose (§4.13 heading at `:1525`), and "RESEARCH.md `:999`–`:1028`" starts in §10.3's tail (OQ ledger table begins at `:1005`). Evidence: all claimed content is contained and verified; range hygiene only. Severity: note.
- **F18-4 (note).** Location: §0.1/§0.2/§0.3/§3.2/§3.3/§4.7 provenance cells citing `reference-src/schlorbium-HD_U_G6_pre1/**` and `reference-src/pintonium-9c2fcc1/**`. Claim: shipped-doc and Pintonium-source anchors (e.g. `shaders.properties:326`–`:425`, `SHADER_ENGINE_IMPL.md:597`–`:601`, `IrisFunctions.java` ranges, author example `421`–`422`) could not be independently verified because this wave's hard boundary forbids `reference-src/**` reads. Evidence: tagging discipline (`[V:doc]`/`[V:observed]`), internal consistency, and every independently checkable derived value (pi bits, all §5.6 hex, smooth tables) check out; recorded as unreviewable evidence, not a defect. Severity: note.

## 6. Verdict

**PASS-WITH-CORRECTIONS** — 0 blocking, 1 correction (F18-1), 3 notes (F18-2..F18-4). The document is structurally complete, contract-faithful everywhere this reviewer could independently verify, current in every identity it presents as current, and honest about its unverified status; the single correction is a §0 bookkeeping addition with no §5 impact, so the §G1.3 fix-up closes the phase without re-verification while §0.13's standing fresh-verification requirement remains open exactly as the document states. No build, test, gradle, GL, or network evidence was used or needed; no Resolutions section follows (fix-up work is not performed by this review).
