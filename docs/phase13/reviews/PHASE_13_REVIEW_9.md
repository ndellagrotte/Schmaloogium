# Phase 13 — architecture review 9

**Date:** 2026-09-08. **Frozen document:** `docs/phase13/v1/PHASE_13_DOC.md` SHA-256 `b7fba6e88581fd49b6a867c4d11f157b7a46f29c56a12d1ebe2cd0995ed8c2dd` (recomputed, matches). **Verdict:** PASS-WITH-CORRECTIONS (blocking=0, corrections=2, notes=3). **§5 impact:** neither correction changes §5's consumed or exposed contract semantics — one re-anchors three Phase 7 citations in §1/§4.6/§5.2 and D-P13-45, the other adds a current-identity sentence to §5.2's Phase 4 consumption row and §11.1; the doc remains unverified with a fresh whole-document review outstanding, which this round is.

---

## 1. Scope, identity and authority actually verified

- `sha256sum docs/phase13/v1/PHASE_13_DOC.md` recomputed against the frozen value: match. The complete 2484-line document was read, in ranges, with all elided middles recovered; no section was skipped.
- Governing design: `docs/design/v3/DESIGN.md` Part I (§G0–§G12, lines 1–1136) and the Phase 13 spec (lines 2436–2513) read in full. `docs/research/v1/RESEARCH.md` §0–§1 (lines 11–107) read.
- Dependency docs read at §0 header/§1/§5/§11 depth: `docs/phase3/v1/PHASE_3_DOC.md` (§0.55–§0.65 addenda, §1, §5.1 texture/asset region, §5.3 discipline, §11 decisions), `docs/phase5/v1/PHASE_5_DOC.md` (§0.45–§0.47, §1, §2.4, §4.12 area, §5.1), `docs/phase7/v1/PHASE_7_DOC.md` (§0.45–§0.50, §1, §4.10 catalog region, §5.2/§5.3). Consumers spot-read: `docs/phase6/v1/PHASE_6_DOC.md` (§0.23–0.24, §4.9, §5.1), `docs/phase14/v1/PHASE_14_DOC.md` (§5.5), plus targeted verification in P1 (`D-P1-43/52/63/64/66`, package tables), P4 (`:2128`, D-P4-31/43), P8 (`D-P8-26`), P12 (`ReloadRequest`).
- Conformance-map rows spot-checked against cited text: RESEARCH §4.6 texture part (`:593-599`), App B.3 (`:1228-1256`), App D.3 row (`:1367`), App E rows 10–11 (`:1410-1411`), App F.5 (`:1482-1492`); PD §11 (`:619-645`), §7.4 (`:437-449`), §17 B10/B13 (`:796`, `:799`), §18 (`:803-819`). Decision authorities `docs/decisions/U1_TEXTURE_SAMPLING.md` and `docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md` exist and were read; their content matches the D-P13-26/27 adoptions.
- Hard boundaries honored: read-only except this file; `reference-src/**`, `docs/**/chatlogs/`, root `*.txt`, and all other `PHASE_*_REVIEW_*.md` were not opened. Consequently the doc's line-granular `reference-src/...` citations and its citations into `PHASE_7_REVIEW_36.md`/`PHASE_13_REVIEW_3.md` were **not** recomputed (see §4 note 4); every anchor inside RESEARCH.md, PD, and the phase dependency docs was recomputed against current bytes.
- No build, test, gradle, GL, or network execution was performed.

## 2. Scope and §5 audit

- **Doc gate** (spec `:2502-2505`): all three custom-texture forms + `.mcmeta` + stage mapping are in the conformance map (F5-3…F5-15); companion-atlas lifecycle load/stitch/reload/animate is specified (§4.1, §4.6, §4.7, §4.1.7); hook sites are in the Phase 7 App E catalog format (§4.6 table); the filter/wrap row is present in its authorized U1-corrected form (§0.8, §3.6.1, §4.3.5) with the conflict reported, not silently resolved; the noise-generator decision is recorded with provenance (§4.2.2/§4.2.3, D-P13-3). Gate met.
- **Scope discipline:** every spec scope-in item is owned and designed (companions, macros wiring, noise, custom textures incl. per-unit multi-type and reload lifecycle, `atlasSize`, animation, App E rows 10–11); scope-out lines are explicit (§1.2: units to P5/P6, tangent math to P10, labPBR pack-side). The G2.4 rule-5 requirement (no vanilla-stitch regression with shaders off) is honored by §4.6's closing rule and §8.2 `companion_vanillaAtlasUntouchedWithShadersOff`. Memory posture documented (§4.8).
- **Template completeness:** all thirteen §G9 sections (0–12) present and substantive; §10 correctly records that no OQ is assigned to P13 (spec `OQs: —`, §G10 table) and fences OQ-15/OQ-7 as touch-not-own.
- **§5 interface honesty — consumed side verified to exist at current coordinates:** P3 `CompanionOptionMacros` (`:790`,`:804`, grant §0.55), `TextureBindingKey` (`:909-910`), `TexturePropertyDecl` with the closed disposition enum (`:912-917`), `CustomTextureSpec`/`NoiseTextureSpec.Override`/`TextureSidecarRef` (`:938-954`), `NoiseRequirement(boolean,int)` (`:2753`), `PackAssetSnapshot`/`PackAssetAcquisition` (Acquired/Missing/Unreadable/InvalidReference, `NULL_PATH|NOT_DECLARED`, `:874-898`, `:1907`), `openCursor` read-only heap cursor (`:1925`), `CURRENT_SCHEMA_VERSION = 23` (`:704`, `:4161`), `MaterializedSource-v23` (`:3309`, `:4212`); P4 `:2128` quote exact; P5 §5.1 ownership rows `:2726-2735` with the `:2729` Bound-only sentence exact, `FixedSamplerName` (`:984`), `beginPass`/`shadowBindings` signatures exact, D-P5-42/43/50 present; P6 `updateAtlasSize(Int2)` (`:533`, `:1493`), adapter row (`:1823`), `retire` domain (`:489`,`:499`,`:277`), R7-10/R7-11 adoptions (§0.23/0.24); P7 `AtlasBindingEvidence`/`AtlasBindingSink.currentBinding` (`:3602-3609`), eight ordered H13 rows with application-v2 fingerprint law and D-P7-69's outer-loadSprites receipt (`:1935-1960`, `:4544`); P8 D-P8-26; P12 `ReloadRequest(NONE, worldRendererReload=false, resourceReacquire=true, RESOURCE_RELOAD)` (`:1270`); P14 §5.5 symmetric to P13 §5.5 (parameter domains, D-P13-27 semantics, D-P13-43 rejection set, D-P14-26/31/32); P1 package trio (§2.1 `:1744-1764`, §5.1 `:5448`) and D-P1-52/63/64/66 all present.
- **§5 exposed side:** P14 §5.5 and P7 §5.2's Phase-13 slot consume exactly what §5.1 publishes (request field order matches the actual record at `:389-400`); P6's adapter row matches §4.4's Known→`Int2(width,height)` / reset→`Int2(0,0)` law. No invented interface found; no dependency §5 surface is assumed beyond what exists.
- **Recent-change identities:** schema23 acquisition gate current (§4.3.2 `:986-991`, D-P13-44); `phase13.parameters/v2` + `phase13.sidecar/v1` current with v1 regeneration forbidden (§4.5.1 `:1344-1381`); independent preference bits from decoded user preference, never linked demand (§4.1.1, D-P13-20); outer loadSprites try/finally enclosing Pre/listeners/populator/inner allocation/Post with eight rows on `TextureHookHealth/application-v2` (§4.6 D-P13-40, §5.1 D-P13-39/40 — matches P7 `:1935-1960`); U1 baselines exactly as decided (PNG NEAREST/REPEAT, raw LINEAR/CLAMP_TO_EDGE, noise override LINEAR/REPEAT, explicit boolean overrides, atomic one-warning recovery, owned-sidecar-only failures, §§4.3.5/6); actual accepted base-object association through context-bound leases (D-P13-43, §§4.1.5/4.4/4.5.2, matching P5 D-P5-50); local companion/default sampling fix (D-P13-42). All current.
- **Binding decisions and licensing:** D-1…D-10 honored (§11.2); no contract-visible "improvement" (no-renormalization is tagged `[A]` falsifiable at T2, §4.1.3); every PD-derived claim carries `PD §n` + `[V:observed — Pintonium <path>]`; §3.5 ledger handles E-10/E-11 and PD-1…PD-8 with row-local decisions and contract checks; §G11.4 pre-decided rejections and §18 rows demonstrably not adopted; no AGPL trace; §0.2 item 6 records the deliberate OD non-read with C-TX01 reaching the doc through Part I.

## 3. Required corrections

### C1 — D-P13-45's P7 anchors are not re-resolved to current coordinates

- **Sites:** §1.1 item 6 (`:286`); §4.6 first sentence (`:1475`); §5.2 Phase 7 row (`:1757`); D-P13-45 (`:2347`).
- **Claim:** D-P13-45 states the stale sibling anchors were "re-resolved to current frozen coordinates". For three of its six listed anchors this is false against current bytes.
- **Evidence:** `docs/phase7/v1/PHASE_7_DOC.md:1792-1793` currently holds P7's D-P7-43 alpha/blend enforcement receipt; the "DEFERRED(P13,v0.5)" rows P13 §1.1 quotes are at `:1846-1847`. P7 `:1598-1605` is §4.9 engine-flag/depth-mask prose and `:2933` is a `ReloadStatus` record line; the CORE/FEATURE/OBSERVER catalog/health classes are at `:1654-1656`. P7 `:3880-3928` covers the "Phase 13 downstream slot" (`:3884-3921`) but the "complete ten-step transaction" spans `:3922-3972`, past the stated end. (P4 `:2128`, P5 `:2726-2735`/`:2729`, P6 `:1217-1221` were verified correct.)
- **Failure path:** a fix-up or implementation session trusting D-P13-45's assertion opens `:1792-1793` or `:1598-1605,2933`, finds unrelated contract text, and either mis-binds the deferral/health-class semantics or re-audits from scratch; the receipt that exists precisely to prevent stale-coordinate consumption is itself stale.
- **Owner+receivers:** Phase 13 owns; no dependency text changes; P7/P2 need no reciprocal amendment because the semantic claims are unchanged and true at the correct locations.
- **Minimal resolution:** repoint §1.1 to `docs/phase7/v1/PHASE_7_DOC.md:1846-1847`, §4.6 to `:1654-1656` (health classes; extend to the enclosing §4.10 catalog rows), and §5.2 to `:3884-3921,3922-3972` (or `:3880-3972`); amend D-P13-45's coordinate list accordingly with a dated correction note. No semantic claim changes.

### C2 — the only named RegistryFingerprint domain in the document is superseded

- **Sites:** §0.11 / D-P13-31 receipt (`:1782-1783`): "Carry opaque P4 RegistryFingerprint/own-build-v1 and invalidate older identity caches"; no other location in the document names a fingerprint domain.
- **Claim:** the block instructs receivers to carry `RegistryFingerprint/own-build-v1`, but P4's current registry-fingerprint domain is `RegistryFingerprint/profile-selection-v3` (`docs/phase4/v1/PHASE_4_DOC.md:1938`, advanced by D-P4-43 at `:2800`, "distinct from every earlier registry domain"), which P5 (D-P5-48, `:3394`, `:3452`) and P7 (`:3784`, `:3800`) already receive as the current opaque identity. P13 never names `profile-selection-v3` anywhere.
- **Failure path:** a consumer implementing §0.11's carry-forward instruction against P4's actual publication compares or records identity tokens under a domain P4 no longer mints; identity-cache invalidation keys off the wrong domain name while every sibling phase keys off `profile-selection-v3`. The "Historical" label on the §0.11 receipt does not neutralize the operative carry-forward sentence inside it.
- **Owner+receivers:** Phase 13 owns the receipt sentence; P4 owns the domain and has already published it; P5/P7 receipts are unaffected; no cross-phase signature changes.
- **Minimal resolution:** add one current-identity sentence to §5.2's Phase 4 consumption row (the type is already consumed opaquely there) and a dated receiver line in §11.1 recording that the current opaque identity is P4 D-P4-43 `RegistryFingerprint/profile-selection-v3`, with D-P13-31's own-build-v1 mention marked superseded — mirroring what P5 D-P5-48 and P7 already recorded. If applied to §5.2, note it per §G1.3; the document's standing unverified/fresh-review posture already requires the whole-document review this round supplies.

## 4. Notes

1. P7's own §5.2 "Phase 13 downstream slot" consumes P13 by the stale pointer `docs/phase13/v1/PHASE_13_DOC.md:1147-1163` ("the exact frozen request at line 1150"); the actual `TexturePlanRequest` record is at `:389-400` and `:1147-1163` is §4.3.4 selection prose. The eleven described fields match the real record's order and types, so the consumption is semantically sound; the coordinate defect is P7-side and belongs in P7's next review or the §G5.3 integration audit, not in this document's corrections.
2. §0.1's Phase 5 read inventory honestly labels its original-build line references "historical input record; not current line pins" and cites current contracts in §5.2 — verified correct behavior, recorded as the pattern C1 should follow.
3. The `texture.<stage>.<sampler>` filter/wrap conflict is routed through the dated maintainer decision (`docs/decisions/U1_TEXTURE_SAMPLING.md`) with the requirement superseded "only to that extent" and G6/RESEARCH text left unrewritten; §3.6.1 reports rather than silently resolves, satisfying §G0.1. The decision file's binding scope matches what §4.3.5/§3.6/§12 implement.
4. Not recomputed under the review's hard boundaries: the document's `reference-src/...` line citations (shaders.properties, shaders.txt, SHADER_ENGINE_IMPL.md §8) and its citations into `PHASE_7_REVIEW_36.md:107-123` / `PHASE_13_REVIEW_3.md:304-324`. Their load-bearing content is independently corroborated through RESEARCH Apps and PD, which match; the verification-state claims rest on §5.4's "historical PASS covers historical bytes only" posture, so the unread review citations are not decision-bearing here.
5. §8's checks are future architectural checks, consistently labeled "not executed tests" (§8 preamble, §8.3); the D-P13-8 animation row correctly carries "no permitted positive observed synchronization reference was found" rather than claiming observed sync (T-7).

## 5. Areas audited and found sound

- Hash identity; complete-document coverage; governing-design conformance of the v3 selection claim (§0 header re-derivation statement, `docs/MOVES.md` adoption note).
- Conformance map F5-1…F5-17, B3-1…B3-6, T-1…T-7, D-1, F3-1, M-1/M-2 against RESEARCH §4.6/App B.3/App D.3/App E/App F.5 — including the depthtex1=11 shipped-doc inconsistency handling, the 16-row unit mirror (verified against B.3 directly), stage expansions, per-unit multi-type disambiguation, and zero unmapped in-scope rows.
- C-TX01 disposition: literal `0xFF7F7FFF` preserved, byte-order assumption stated once, tagged `[A]`, covered by `companion_missingNormalUsesContractDefault` and an escalation path; never silently swapped (§4.1.4, §3.6.4).
- Noise: signed-wrapping xorshift specified to bit-exact reproducibility with arithmetic vectors; `Random(0)` rejection cites the §G11.4 standing list; the single sanctioned route back (§G0.1 conflict via T2 evidence) stated without taking it (§4.2).
- §4.3.2 acquisition contract against P3's actual `assets()`/`PackAssetSnapshot`/cursor/Metadata shapes, including the sidecar-only failure waiver, fatal-class preservation, and post-NONE retention through P7.
- §4.3.3/§4.3.5 raw/parameterization legality (RECT REPEAT/mipmap and integer-LINEAR rejections incl. baselines), D-P13-41 target-maxima receipt against P1 D-P1-66, D-P13-36 conversion against P1 D-P1-63/P5 D-P5-42.
- Lease/binding/retirement lifecycle (§4.5.2/§4.5.3) against P5 §5.1's Bound-only transfer, sixteen-row preflight, closed rejection domains, and D-P13-43's lease-local base association; ten-step transaction mirror matches P7 §4.1/§5.3 step-for-step.
- `atlasSize` chain: P7-owned opaque evidence minting, sink law, reset/stale rules, and P6's adapter row — three-way consistent (P13 §4.4, P7 `:3602-3609`/`:3884ff`, P6 `:533`/`:1468`/`:1493`/`:1823`).
- §5.3 outstanding-request ledger (R1/R4/R5/R2/R3/R7-10…13/U1/IR-21/IR-23): each disposition verified against the owning doc's current §5; nothing self-granted.
- Failure taxonomy closure (§6 code table, preflight order, sidecar-recovery separation from `TextureFailure`), threading posture (§7), testability naming (§8), milestone staging (§9), implementation checklist coherence (§12).
- Licensing/provenance posture (§0.3), do-not-inherit ledger completeness, §G11.5 trust-tier discipline (structure reused, values re-derived, heuristics absent).

## 6. Verdict

**PASS-WITH-CORRECTIONS** — blocking=0, corrections=2, notes=3. The document's architecture, contract fidelity, interface honesty, identity gates, and licensing posture are sound; both corrections are coordinate/identity-receipt hygiene (C1: three stale Phase 7 anchors inside a receipt claiming they were re-resolved; C2: the sole named RegistryFingerprint domain is the superseded own-build-v1 with no current profile-selection-v3 receipt). Neither requires a structural rebuild. The document remains unverified pending the fresh whole-document review this round initiates; no build, test, or GL execution was performed.

## Resolutions

Recorded 2026-09-08 by the §G1.3 fix-up session. Both required corrections applied to
`docs/phase13/v1/PHASE_13_DOC.md`; the notes were recorded and, not being incorporated by a
correction, were not applied.

- **C1 (stale D-P13-45 P7 anchors) — applied.** All three coordinates recomputed against current
  P7 bytes and repointed at every named site, no semantic claim changed: §1.1 item 6 now cites
  `docs/phase7/v1/PHASE_7_DOC.md:1846–1847` (verified: the `DEFERRED(P13,v0.5)` App E rows 10–11;
  the old `:1792-1793` is D-P7-43's alpha/blend enforcement receipt); §4.6's first sentence now
  cites `:1650–1660` (verified: §4.10.1 four-class table with CORE/FEATURE/OBSERVER at
  `:1654-1656`, extended over the enclosing catalog rows through the `require=0`/`expect=1`
  plugin-audit sentences; the old `:1598-1605` is §4.9 depth-mask prose and `:2933` is a
  `ReloadStatus` record line); §5.2's Phase 7 row now cites `:3880–3972` (verified: the Phase 13
  downstream slot begins `:3884` and the complete ten-step transaction spans `:3922-3976`, past
  the stated `:3928` end); D-P13-45's coordinate list amended with a dated 2026-09-08 correction
  note that preserves the drifted R8 values as historical. P4 `:2128`, P5 `:2726-2735`/`:2729`
  and P6 `:1217-1221` were re-verified correct and untouched.
- **C2 (superseded RegistryFingerprint domain) — applied.** §5.2's Phase 4 consumption row now
  carries the current-identity sentence: P4 D-P4-43 `RegistryFingerprint/profile-selection-v3`
  (verified at `docs/phase4/v1/PHASE_4_DOC.md:1938`) is the current opaque identity, received
  opaquely at the existing identity checks under new D-P13-46 (§11.1, next unused ID after
  D-P13-45), mirroring P5 D-P5-48 and P7's receipts; D-P13-31's historical own-build-v1 sentence
  in §5.2 carries a dated superseded marker pointing at that row and D-P13-46. A §0.15 fix-up
  addendum records the round and, per §G1.3, that §5 changed (one §5.2 Phase 4 row), so a fresh
  whole-document verify session is owed; the document's standing unverified posture covers the
  new bytes.
