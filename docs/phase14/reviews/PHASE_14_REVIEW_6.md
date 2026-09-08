# Phase 14 — architecture review 6

**Date:** 2026-09-08. **Frozen document:** `docs/phase14/v1/PHASE_14_DOC.md` SHA-256 `fca8b8ff579d38821efc53986ea26f713441793797d9d538596bda35f48e89d5`. **Verdict:** PASS-WITH-CORRECTIONS (blocking=0, corrections=4, notes=3). **§5 impact:** the corrections require one owner-local §0 receipt addendum plus re-pinned dependency line citations and two §5 wording fixes (FrameAbortReason count, P2 run-manifest cell); no consumed owner contract changes shape, so no dependency or receiver amendment beyond this document is triggered.

---

## 1. Scope, identity and authority actually verified

- `sha256sum docs/phase14/v1/PHASE_14_DOC.md` recomputed against current bytes returned exactly the frozen hash; 2972 lines. The document was read completely, in ranges.
- Governing design: `docs/design/v3/DESIGN.md` Part I lines 1–1136 (§G0–§G12, including the §G1.2 verify-session protocol, §G2 D-1..D-10, §G4, §G9 template, §G11 rules) and the Phase 14 specification lines 2514–2590. The dependency table row for Phase 14 was verified at `docs/design/v3/DESIGN.md:626` (dependencies 5, 6, 7, 13; OQ-15/OQ-22) and the OQ assignments at `:878`/`:885`.
- `docs/research/v1/RESEARCH.md` lines 11–107 (§0 identity, §1 decisions D-1..D-10).
- Dependency docs, each at §0 header, §1, §5, §11 of current bytes: `docs/phase5/v1/PHASE_5_DOC.md` (3653 lines), `docs/phase6/v1/PHASE_6_DOC.md` (2445), `docs/phase7/v1/PHASE_7_DOC.md` (4736), `docs/phase13/v1/PHASE_13_DOC.md` (2484). Spot-checked conformance-map rows against RESEARCH (`:626`, `:769`–`:778`, `:784`–`:788`, `:951`, `:1021`, `:1028`) and PD pins (`PINTONIUM_DESIGN.md:741`–`:744`, `:745`–`:747`, §15/§16 regions); all resolve with the cited content.
- Consumer status checks: P2 (`docs/phase2` searched for `GlModernizationPlan`/`GlModernizationPolicy` — zero matches), P4 (`PHASE_4_DOC.md` §5.7 at 2367–2372, D-P4-24 at 2781, §11.4 at 2881 — R-P14→P4-1 received/pending, not adopted), P1 (D-P1-52 at `:4493`/`:5471`/`:6160`, D-P1-54 at `:4696`/`:5467`/`:6157`, D-P1-60 prepareUnitBindings at `:3409`/`:3513`–`:3516`; package rows at `:1745`/`:1759`; no `glContext` flag exists, consistent with R-P14→P1-4 being ungranted).
- Recent-change identities cross-checked in the phase document: D-P14-44 five tiers (`:2652`, `:334`, `:1660`, §2.2 record); D-P14-43 three-argument lease with the two added rejection reasons and base-serial currentness (`:1815`–`:1820`, `:2651`, `:2959`) matching P13 §5.1/§2.2 (`lease(expected, selection, AtlasBindingEvidence)`, six-value `TextureLeaseRejection` incl. INVALID_BASE_BINDING/STALE_BASE_BINDING) and P5 D-P5-50; P5 physical binds before Bound return (P14 §4.1.4; P5 `:2729`, P13 `:1756`); all eight resize reasons (P5 §5.1 `:2730`: DISPLAY_EXTENT, RENDER_QUALITY, MIN_DEPTH_EXTENT, SHADOW_RESIZE, SHADOW_QUALITY, PACK_CONFIGURATION, REGISTRY_PLAN, COLOR_INVENTORY_OR_FORMAT); P6 shared resolver, no independent map (D-P14-19; P6 §5.1 `:1809`, P7 `:4023`–`:4034`); P13 exact parameter conversion D-P13-23/29 incl. D-P14-26/31 receipts (P13 §5.5 `:2000`–`:2044`); ten-step shared-unit transaction (P7 §5.3, exactly steps 1–10 at `:3928`–`:3976`; step 8 P13 build/registration with accepted generations); schema23/MaterializedSource-v23 (D-P14-33 at `:1966`–`:1988`), profile-selection-v3 (D-P14-39 at `:1975`, `:2647`), `phase13.parameters/v2` (`:1794`), projectionVersion=1 (`:1985`, `:2004`); synchronous baselines mandatory with every optional extension (async/batching/staging/resumability/sample-age/timing) explicitly ungranted; stall criterion unmet/deferred (D-P14-42 at `:2650`, §9.2 condition 3 at `:2390`–`:2395`); targetless logical issuance vs exact-target native materialization (D-P14-40 at `:2648`) and lifetime-ending deletion with affected bindings zeroed vs binding-neutral operations (D-P14-41 at `:2649`); EMA unit-delta evidence law (D-P14-28 at `:2636`).
- Boundaries honored: read-only except this report file; `reference-src/**`, `docs/**/chatlogs/`, root `*.txt`, and every other `PHASE_*_REVIEW_*.md` were not opened. No build, test, gradle, GL, or network execution was performed.

## 2. Scope and §5 audit

- **Scope discipline is sound.** Phase 14 remains a leaf (`docs/design/v3/DESIGN.md:611`–`:626`); §1.3's no-contract-visible-component claim is upheld by the §3.1 invariance map; A1–A7 map to the specification's Adapt rows and the doc-gate's design+fallback+ledger triple (§7.5 L-1..L-13 with an accurate RESEARCH §6.2/§6.3 coverage check verified row-by-row against `:769`–`:778`/`:784`–`:788`).
- **Interface honesty is substantially sound.** Every optional request is recorded with its true status: R-P14→P4-1 pending/not adopted (matches P4 §5.7/D-P4-24/§11.4), R-P14→P5-2 ungranted, R-P14→P6-1 sample-age ungranted (P6's `CenterDepthResult.Sample(float)` at `:728` carries no frame identity and P6 has no sampledFrameId anywhere), R-P14→P7-1 resumability ungranted (P7 D-P7-56 at `:4102`–`:4107` keeps the synchronous baseline mandatory), R-P14→P13-1 ungranted (P13 D-P13-34 at `:2033`–`:2044` records D-P14-32's two-sync ordering as a condition on a future adopted contract only), R-P14→P1-1/2/3 granted-and-adopted with D-P1-52/54 verified in P1's own §5/§11, R-P14→P1-4 outstanding. Reciprocal receiver receipts in §5.10 (D-P14-26/27/31/32) match P5 D-P5-39/41/43, P13 D-P13-33/38, and P1 D-P1-60/62/64-family rows. Two wording-level exceptions are raised as corrections 2 and 4 below.
- **§5 consumed contracts match owner bytes semantically.** P5 registration/replay (`SUCCESS|FAILED`, `ConsumerFailed.deliveredCount`, `prepareUnitBindings` after full preflight before the BoundObject loop) verified at P5 `:711`–`:724`, `:2362`–`:2364`, §11.1 D-P5-39/41/43; P6 factory/`Sample`/beginFrame ordering/rung-2a verified at P6 `:728`, `:747`–`:781`, `:1060`–`:1064`, `:2031`; P7 ten-step/step-7 resize/step-8 P13/D-P7-56/D-P7-64/IR-26 verified at `:3922`–`:4114`, `:4672`; P13 publication/lease/lifetime/parameter law verified at `:1656`–`:1744`, `:2000`–`:2050`.
- **Licensing/provenance compliance is sound.** Every adopted or rejected Pintonium claim in §3.2/§3.3/§7.5 carries a PD citation plus a `[V:observed — Pintonium <path>:line]` tag; the do-not-inherit rows this phase touches (B7 group imbalance, B9 label misspelling, B11 state save/restore, dynamic unit allocation, §17 stubs, the AGPL `glsl-transformation-lib` boundary) are each shown structurally handled; no AGPL trace exists; Schmaloogium remains GPL-3.0-or-later with LGPL material observed, not copied.
- **Template completeness is sound.** All thirteen §G9 sections are present in order; §10.1 and §10.2 each carry the four mandatory parts with the questions verbatim from RESEARCH §11 (verified at `:1021`/`:1028`); full OQ spike specs (procedure, K1–K5 criteria, designed fallbacks) are complete.

## 3. Required corrections

### Correction 1 — dependency line citations no longer resolve against current owner bytes

- **Sites.** Representative verified instances, phase-doc line → actual current location of the cited content: `:478`/`:706` P6 `:468`, `:473` → 634, 633; `:480`/`:507`–`:515` P6 `:1678`, `:965`–`:968`, `:946`–`:963`, `:1654` → 2225, 1180–1183, ~1145ff, 2201; `:2067`–`:2068` P6 `:1500`, `:585`–`:587` → 2031, 778–781; `:2103` P6 `:1517` → §7.1 region; `:2139`/`:2352` P6 `:1555`, `:1643` → 2090, 2189; `:599`–`:601`/`:664`–`:666` P5 `:1728`–`:1734`, `:1042`–`:1045`, `:534` → 2194–2253, 1431–1432, 623–624; `:1347`–`:1348` P5 `:2167` → 3108; `:1736`–`:1744` P7 `:1363`–`:1372`, `:1383`–`:1386`, `:1671`–`:1693`, `:2166`–`:2168`, `:2194`–`:2195`, `:2320`–`:2323` → 594–604/2593–2603, 2614–2617, 2913/3657, shifted, 4186–4187, 4431–4434; `:2884` P7 `:2312`–`:2313` → shifted; `:1866`–`:1867`, `:1876` P1 `:4222`, `:4220`, `:4322` → shifted (4222 now a recorder row; 4322 now D-P1-68 text); plus ±1 drift at `:450` RESEARCH `:1379`–`:1380` (content at 1380–1381) and `:478` v3 `:954`–`:955` (content at 953–954).
- **Failure path.** P5/P6/P7 (and P1) received further 2026-09-08 fix-ups after this document's §0.4–§0.8 reads; every cited region shifted. A verify session resolving the pins lands on unrelated declarations (e.g. P6 `:468` is now `FrameBeginResult beginFrame(...)`, P5 `:1042`–`:1045` is now the `TextureCandidateEntry` record). §3.4's claim that it "verified all three citations at the line" is no longer true of current bytes. In every instance checked, the quoted content still exists semantically unchanged — this is coordinate rot, not contract drift (with the one substantive exception corrected below).
- **Owner+receivers.** Phase 14 owner-local; no dependency document changes; no receiver adoption triggered (leaf phase, quoted contracts unchanged).
- **Minimal resolution.** One §0 fix-up entry that mechanically re-derives every dependency/RESEARCH/DESIGN line pin against current bytes (quotes retained), and supersedes §3.4's verification sentence with a dated re-verification statement.

### Correction 2 — `FrameAbortReason` exhaustive-set count is stale (six vs current five)

- **Sites.** `PHASE_14_DOC.md:1737` (§5.4 row: "`FrameExitKind { NORMAL, EARLY_RETURN, THROWN }` and `FrameAbortReason`'s six values (`docs/phase7/v1/PHASE_7_DOC.md:1383`–`:1386`)") versus `docs/phase7/v1/PHASE_7_DOC.md:2614`–`:2617`, where current §5.1 declares exactly five values: `PROTOCOL_REJECTION, BACKEND_FAILURE, RESIZE_EPOCH, WORLD_CHANGE, HOOK_UNHEALTHY`. `FrameExitKind` still matches.
- **Failure path.** The row's purpose is the exhaustive exit set that D-P14-4's sampler clear and A5's group drain must cover. A consumer implementing against "six values" enumerates a reason that no longer exists (or misses a rename), and §8.2's terminal-release coverage claims inherit the wrong domain.
- **Owner+receivers.** P7 owns the enum and its current bytes are authoritative; P14 restates consumption; no P7 change is requested or implied.
- **Minimal resolution.** Replace the count and citation with the current five-value enumeration (or a count-free "current `FrameAbortReason` values" reference) in §5.4 and sweep §4.1.5/§4.5.3/§8.2 wording for any other exhaustive-exit enumeration.

### Correction 3 — §0 fix-up receipts missing for the latest applied correction rounds

- **Sites.** `PHASE_14_DOC.md:212`–`:219` (§0 trail ends at "Attempt-6 R3 correction receipt", D-P14-35..38) versus `:2647`–`:2652`, where D-P14-39..44 — including D-P14-44 explicitly marked "R5 C1" and the D-P14-43 lease correction — are applied across §§4/5/8/11/12; `:13` (header Date 2026-09-07 with no last-revised marker); `:2967`–`:2972` (closing trailer speaks only of the IR-15/23/26/27 fix-up).
- **Failure path.** Every earlier correction round in this document is recorded as a §0 addendum naming the review, the findings addressed, and the resulting fresh-verification requirement. The round-4/round-5 corrections broke that trail: a verify session cannot attribute D-P14-39..44 to recorded reads, cannot tell which §5 regions those rounds changed, and finds no recorded fresh-verification obligation for them.
- **Owner+receivers.** Phase 14 owner-local documentation; no dependency or receiver impact (the underlying decisions themselves verify consistently, as §1 records).
- **Minimal resolution.** Add §0.9 (and §0.10 if rounds 4 and 5 were distinct sessions) recording each round's findings→decisions mapping, the reads performed, the §5-region change status, and the fresh whole-document review requirement; advance the header date/last-revised marker accordingly.

### Correction 4 — P2 run-manifest consumption presented as current adoption

- **Sites.** `PHASE_14_DOC.md:1660` (§5.1 consumer cell: "Phase 2's run manifest (as an environment fact, not a result)"). Current `docs/phase2/**` contains zero occurrences of `GlModernizationPlan`/`GlModernizationPolicy`; P14 records no request or hand-off for it — H-P14→P2-1 (`:2886`) covers only scene S-CD-1, and §5.7 claims only procedures.
- **Failure path.** Everywhere else this document marks unadopted consumption explicitly; this cell reads as if P2's manifest schema already carries the plan. P2 owns its run-manifest schema and has adopted no such environment fact, so the exposed-surface table overstates current receiver adoption.
- **Owner+receivers.** P14 wording fix; P2 owns any future manifest amendment (add an explicit proposed-consumption request row if the exposure is to be pursued).
- **Minimal resolution.** Reword the cell to proposed/pending status (e.g. "proposed to Phase 2's run manifest; adoption not recorded in P2's current §5") or add a corresponding ungranted-request row in §5.7/§11.5.

## 4. Notes

1. The reference-source citations (`IrisRenderSystem.java:377`–`:389`, `GLDebug.java:291`, `CommonIrisRenderingPipeline.java:1272`–`:1281`, `GLRenderDevice.java:233`) could not be re-verified because `reference-src/**` is outside this review's read boundary; the PD-side pins they accompany (`PINTONIUM_DESIGN.md:737`–`:749`, `:741`–`:744`, `:745`–`:747`, `:793`–`:808` region) were verified and the tags are correctly formed, so this is a boundary statement, not a finding.
2. P1 line pins were spot-checked (two of four missed their content; see Correction 1) rather than exhaustively re-derived; the D-P1-52/54/60 content claims themselves were verified against P1's current §5/§11 and are accurately described, including "value adoption only" scoping and the absence of any worker/context-flag grant.
3. D-P14-42 and §9.2 condition 3 correctly keep the pack-switch stall criterion unmet/deferred rather than claiming a PASS, and §11.6's six known gaps are honest and specific; both are load-bearing for the doc gate and are recorded as found sound rather than re-litigated.

## 5. Areas audited and found sound

- Frozen-hash identity; complete-document read; G9 thirteen-section skeleton; OQ-15/OQ-22 four-part spike specs with verbatim questions (`RESEARCH.md:1021`/`:1028`).
- §G1.2 posture: "documentation integration fix-up; changed contracts unverified, no implementation or new PASS claim" is stated in §0, §5.4, §11.6, and the closing trailer, and no PASS/verification claim is made anywhere.
- Dependency consumption law: P5 sole policy/physical binding/Bound-only lease transfer and eight resize reasons; P6 shared resolver with no independent map and the ungranted sample-age request; P7 ten-step transaction with step-7 resize delivery and step-8 P13 build against accepted generations; P13 exact parameter conversion (D-P13-23/29), lease-drained lifetime, reverse-order retirement ownership, and the closed six-reason three-argument lease (D-P14-43 correct and consistent in §5.5, §11.1, §12 item 37).
- Identity currency: schema23/MaterializedSource-v23 (D-P14-33), profile-selection-v3 (D-P14-39), `phase13.parameters/v2`, projectionVersion=1, five published tiers (D-P14-44, consistent in §2.1/§2.2/§5.1/§11.1); all historical numeric receipts explicitly retired.
- Optional-extension discipline: every async/batching/staging/resumability/sample-age/timing extension is explicitly ungranted with synchronous baselines mandatory (§5.4/§5.9/§5.10/§9.1/§10.1(4)/§11.6), matching P4 §5.7/D-P4-24, P7 D-P7-56/D-P7-64, P13 D-P13-34, P5 §5.5, and P6's unchanged `Sample(float)`.
- Receiver obligations and evidence boundary: §5.10's coordinated receipts match P5 D-P5-39/41/43, P13 D-P13-33/38, P1 D-P1-60/62; native evidence is separated from facade evidence (D-P14-28) with §8.2 procedures that assert native state rather than recorder echoes.
- Licensing: provenance tags on every Pintonium claim; do-not-inherit rows B7/B9/B11, dynamic allocation, §17 stubs, and the AGPL boundary all structurally handled in §3.3; no Oculus or chatlog material.
- §7.5 ledger coverage check verified row-by-row against RESEARCH §6.2/§6.3, including the correctly reasoned exclusions (`:771`, `:776`, `:777`, `:788`).

## 6. Verdict

**PASS-WITH-CORRECTIONS** — blocking=0, corrections=4, notes=3.

The document's substance verifies against current owner bytes: consumption claims, receiver receipts, ungranted-extension discipline, current identities, template completeness, and licensing posture are all sound. The four corrections are documentation-level — stale dependency line pins (Correction 1), one stale consumed-enum count (Correction 2), the missing §0 receipts for the newest applied correction rounds (Correction 3), and one overstated P2 consumer cell (Correction 4) — and none requires a structural rebuild or changes any consumed owner contract. No build, test, GL, or network execution was performed; no implementation or verification clearance is granted or implied by this review.

## Resolutions

2026-09-08 architecture-only fix-up; the frozen identity, findings, confidence and verdict above
remain unchanged. Process disclosure: the fix-up worker applying these corrections was replaced
mid-task by Main after stalling; its applied edits (Corrections 1, 2 and 4) were verified in place
against current bytes, and Main completed Correction 3's addenda directly. No report body text
above was altered.

- **Correction 1 — applied.** Every dependency/RESEARCH/DESIGN line pin re-derived against the
  2026-09-08 settled bytes of P1/P5/P6/P7/RESEARCH/v3, quotes retained; §3.4's verification
  sentence superseded by the §0.11 dated re-verification statement. Post-fix grep confirms none
  of the review's stale pin values remain (P6 :468/:473/:1678/:965-968/:946-963/:1654/:1500/
  :585-587/:1517/:1555/:1643; P5 :1728-1734/:1042-1045/:534/:2167; P7 :1363-1372/:1383-1386/
  :1671-1693/:2166-2168/:2194-2195/:2320-2323/:2312-2313; P1 :4222/:4220/:4322; RESEARCH
  :1379-1380; v3 :954-955). Recorded as §0.11.
- **Correction 2 — applied.** §5.4's row restated as `FrameExitKind { NORMAL, EARLY_RETURN,
  THROWN }` plus `FrameAbortReason`'s five current values (PROTOCOL_REJECTION, BACKEND_FAILURE,
  RESIZE_EPOCH, WORLD_CHANGE, HOOK_UNHEALTHY) cited at `docs/phase7/v1/PHASE_7_DOC.md:2631`–
  `:2633`; §4.1.5/§4.5.3/§8.2 swept — no other exhaustive-exit enumeration remains.
- **Correction 3 — applied.** §0.9 (Attempt-7 R4: D-P14-39…42) and §0.10 (Attempt-8 R5:
  D-P14-43/44) receipts appended with findings→decisions mapping, read scope, §5-region change
  status and the fresh-review requirement; header Date advanced with a `last revised 2026-09-08`
  marker; closing trailer already names §§0.4–0.11.
- **Correction 4 — applied.** §5.1's consumer cell now reads "Phase 2's run manifest —
  **proposed only**: environment-fact consumption requested here but not adopted — P2's current
  bytes define no such manifest field and record no granted request" at
  `docs/phase14/v1/PHASE_14_DOC.md:1691`.
- **Notes 1–3** recorded, not applied: they are boundary/spot-check statements requiring no
  document change.

No §5 contract shape, grant or receiver obligation changed; the corrections are citation
coordinate hygiene, one consumed-enum restatement, missing §0 receipts and one overstated
consumer cell. No validation command, build, test, checksum, formatter or linter was run; fresh
whole-document review and Main's final integration remain required.
