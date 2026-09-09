# Phase 5 — architecture review 48

**Date:** 2026-09-08 · **Frozen artifact:** `docs/phase5/v1/PHASE_5_DOC.md` — SHA-256 `0a187a72712f5755a66e6bee0d8ad623404199fa7665045092a4a6f7b5a904b3`, recomputed at wave open and identical to the attempt-11 registry value (gate passed; review proceeded) · **Verdict: PASS-WITH-CORRECTIONS** — 0 blocking, 2 corrections, 2 notes · **§5 impact:** none — both corrections are header/input-ledger accuracy fixes; the outstanding whole-document verify gate on the §0.39-coordinated rebuild is unchanged.

---

## 1. Doc gate

- **Hash gate.** `sha256sum docs/phase5/v1/PHASE_5_DOC.md` → `0a187a72712f5755a66e6bee0d8ad623404199fa7665045092a4a6f7b5a904b3`, byte-identical to the frozen `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_11.json` row (phase 5, reviewRound 48). Review ran against the frozen bytes.
- **Governing revision.** The doc selects `docs/design/v2.0-RC3/DESIGN.md` (§0 header, line 18; reaffirmed §0.39). Correct per `docs/MOVES.md` version-label rows: RC3 governs Phases 3–9; v3 is adopted only by Phase 2 v2 and Phases 10/11/13. Assignment coordinates re-derived: the Phase 5 spec begins at `docs/design/v2.0-RC3/DESIGN.md:1572`, the objective phrase *"Contract-visible almost end to end"* is at `:1583`, the §G9 template at `:790`, and the assigned range `:1572`–`:1685` matches the spec's own extent.
- **Spec doc-gate items, each verified literally:**
  - *App B.3 reproduced exactly incl. the unit-11 ruling* — §4.12.1's sixteen-row table matches `docs/research/v1/RESEARCH.md:1230`–`:1247` row for row (unit 12 = none/depthtex2; `tex` shadow alias at 0; alias cells at 4/5), and §3.3/§4.12.1 state the shipped-doc contradiction (`doc/shaders.txt:283` ID table "12" vs `:203` uniform table and `:321` composite table "11") with RESEARCH `:1251`–`:1255` ruling 11 authoritative.
  - *Clear/flip as testable state machines* — §4.4.1 side-interpretation table, §4.4.2 transition table (incl. virtual `*_pre` rows), §4.6 side-rules table, and §8.1 items 1–2/6 exhaustively test the matrices.
  - *Every App B.1/B.2/B.4 row in the conformance map* — §3.1–§3.4 map all rows; no unmapped in-scope row found.
  - *Frame-end decision with provenance and contract check* — D-P5-4 (§4.4.3, §11.3 item 2) with the App F.7 sentence verified verbatim at `docs/research/v1/RESEARCH.md:1519`–`:1520`.
  - *Fog-alpha-1.0 rule in the clear-color table* — §4.6 default table with the deployed comment pinned (`CommonIrisRenderingPipeline.java:549`–`:551`).
  - *depthtex0 swap design traced to the PD §5.2 reference* — §4.8 pins `MixinFramebuffer_Shaders.java:59` and `:74`–`:100`; PD §5.2 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:245`.
  - *Shadow flip semantics real, not the B4 stub* — D-P5-8/D-P5-35 with §4.10 generic machine and §8.1 item 5's no-op-flip impossibility test; B4 attribution qualified, not silently inherited.

## 2. Conformance-map audit

Sampled rows re-derived against the cited RESEARCH/appendix bytes; all semantically faithful:

- §3.1 rows: colortex0 fog clear (`RESEARCH.md:1204`), colortex1 white + conditional RGBA32F (`:1205`), colortex2–7 transparent black (`:1206`–`:1208`), ≥4/8 (`:1210`), main/alt pairs (`:1211`), gbuffers/deferred/composite read-write-flip (`:1211`–`:1212`), composite blend caveat (`:1213`–`:1215`), last-writer flip (`:1519`–`:1520`).
- §3.2 rows: depthtex0/1/2 and shadowtex0/1/shadowcolor contents (`:1218`–`:1226`), sfb creation on either shadow minimum (`:1224`, P3 `BufferMinima` algebra at `docs/phase3/v1/PHASE_3_DOC.md:2761`–`:2774` verified), hardware PCF (`:524`–`:526`), clears-both-sides-when-flipped (`:521`–`:523`), Final/anaglyph (`:527`), sizing formulas (`:518`), growth identities (`:371`–`:386`).
- §3.3: App B.3 controlling table and unit-11 ruling verified as above; D-P5-9 rejects the dynamic map with PD §6.5/§18 rows pinned (`PINTONIUM_DESIGN.md:342`, `:808`).
- §3.4: all eight 4-member families plus 5 mixed formats match `RESEARCH.md:1261`–`:1265`; 4×8+5 = 37 count verified; pixel-format/type vocabulary matches `:1267`–`:1268` and `doc/shaders.txt:495`–`:533`; incomplete-FBO fallback matches `:517`–`:520`.
- §3.6 dispositions: PD §5.1 pair/flip (`:218`–`:236`), §5.2 depth swap (`:245`–`:254`), copy tiers (`:256`–`:259`), resize checklist (`:263`–`:266`), fixed-1.0 render quality (`:267`–`:270`), 16-always (`:271`–`:272`, B13 at `:799`), shadow structure (`:273`–`:277`) — all confirmed at the cited coordinates.
- §3.5/§4.9 world order (weather copied before translucent) matches the RESEARCH §4.4 frame flow (`:543`–`:544`), supporting D-P5-17.
- App F.5 shared-unit/expansion text (`:1484`–`:1490`) and App A.3 `superSamplingLevel` = "SSAA multiplier" (`:1181`) match their uses in §§3.3/4.11.1/11.6.

No depthtex1-class semantic error found in any sampled row.

## 3. Interface honesty

**Consumed interfaces exist in the dependency §5 surfaces (verified at stated coordinates):**

- Phase 1 (`docs/phase1/v14/PHASE_1_DOC.md`): §0.18/§0.19 borrowed-depth additions at `:5507`; borrowed/foreign permission matrix at `:5512`; non-verbs table incl. the `bindDefault`-is-name-0 warning at `:5531`; D-P1-57 routes (`:5506`), D-P1-59 sampler initialization (`:5517`), D-P1-60 `prepareUnitBindings` (`:5518`), D-P1-62 demotion (`:5520`), D-P1-63 target-bearing values (`:5521`), D-P1-64 baseline (`:5522`), D-P1-65 schema23 (`:5523`), D-P1-66 captured maxima (`:5524`), D-P1-67/69 typed clear + rasterizer-discard (`:5525`).
- Phase 3 (`docs/phase3/v1/PHASE_3_DOC.md`): `flip.*` publication row at `:1783`; gdepth RGBA32F force at `:1806`–`:1810`; B.5-prefix canonicalization at `:2719`–`:2724`; `ResourceRequirements`/`BufferMinima` algebra at `:2761`–`:2774`; binding §5 `ResourceRequirements` row at `:3417`; complete-key ordering at `:3710`–`:3713`; `CURRENT_SCHEMA_VERSION = 23` at `:754`/`:4211` with `MaterializedSource-v23`.
- Phase 4 (`docs/phase4/v1/PHASE_4_DOC.md`): activation/selector flow at `:1794`–`:1800`; selector rejections (`INVALID_ISSUER`/`STALE_GENERATION`/`STALE_CONTEXT`/`WRONG_STAGE_BAND`/`PROVIDER_LAYOUT_MISMATCH`) at `:2153`; `CompiledRegistryCandidate.view()` at `:2143`; positional `DrawRouting` at `:2158`; the exact "must not infer a resolved ping-pong side" warning at `:2165`; `initializationAssignments(ProgramSamplerLayout.Shader)` at `:683` (consumed by D-P5-38); D-P4-40 `virtualPrelude` and D-P4-43 `RegistryFingerprint/profile-selection-v3` at `:378`/`:527` and `:1954`/`:2816`.

**Exports are specified and receiver-adopted:**

- Phase 6: R7-10 sole-resolver adoption (§0.23) and R7-11 retirement (§0.24) in `docs/phase6/v1/PHASE_6_DOC.md`; the watershadow→4/5 rule P5 preserves is P6's at `:1221`–`:1223`.
- Phase 7: D-P7-44 P5 `/4` resource receipt and `BufferResourceSnapshot` adoption (`docs/phase7/v1/PHASE_7_DOC.md:2165`, `:3868`), `refreshMainDepth` consumption (`:3863`), R7-5 marked granted (`:4051`).
- Phase 8: R7-12/R7-13 adopted in `docs/phase8/v1/PHASE_8_DOC.md` §0.7/§0.8; current `ShadowHookHealth/flattened-v3` (`:280`) matches P5 §5.3.1's consumed shape.
- Phase 13: `phase13.parameters/v2` (`docs/phase13/v1/PHASE_13_DOC.md:196`, `:1397`), `phase13.sidecar/v1` (`:1408`), `TextureHookHealth/application-v2` (`:246`, `:1600`), D-P13-43 base-serial lease (`:856`, `:1351`), and the exact `TextureLeaseSource.lease(...)` signature (`:1732`) match P5 §§2.4/5.3.1.
- Phase 14: D-P14-31 complete object baseline and D-P14-26 replay-before-NONE present in `docs/phase14/v1/PHASE_14_DOC.md` (`:637`/`:668`, `:797`).
- Phase 2: P5 asserts no P2 receipt; P2 v2's current `D-P2-55` schema23 receipt and `capture-plan/4` + `run-manifest/4` identities (`docs/phase2/v2/PHASE_2_DOC.md:2431`, `:1063`, `:1144`) are consistent with P5's §4.1.1 `/4` grammar and §5.1 serialization claims.

No consumed interface was found missing from a dependency §5; no export lacks its claimed receiver adoption.

## 4. Scope discipline and template completeness

- **Scope — out respected:** clear/copy moments owned by Phase 7 (§4.6, §4.9, §1.2); sampler uniform re-pointing by Phase 6 (§1.2, §2.4); shadow camera/pass by Phase 8 (§1.2, §4.10); custom texture binding by Phase 13 (§1.2, §4.12); sampler objects/async transfers by Phase 14 (§1.2, §11.4). No designed element encroaches.
- **Scope — in complete:** all ten spec bullets are architected — dfb main/alt + flips + virtual pre; frame-end reconciliation (D-P5-4); clear rules incl. fog-alpha quirk and batching; 37 formats + fallback; depthtex0 swap + depthtex1/2 mechanics; sfb structure/policies/real shadowcolor flip; sizing + resize lifecycle; growth posture; App B.3 binding table; Final/SCREEN handoff.
- **Template:** all thirteen §G9 sections present and substantive (§0 Header through §12 Implementation checklist). §10 correctly records that RC3 assigns Phase 5 no OQ (`docs/design/v2.0-RC3/DESIGN.md:590`, §G10) and invents no spike — consistent with §G4.4. §12 rows carry milestone tags and test hooks; §9 aggregates the same tags.
- **Adjacent ownership:** §1.2 carries explicit "Owned by Phase Y" lines for every adjacent concern touched (P1/P3/P4/P6/P7/P8/P13/P14, G8/S1), plus the §1.3 hard boundary; §11.4 hand-offs name P2/P6/P7/P8/P13/P14/G8/integration duties.

## 5. Binding decisions, licensing, and current-identity audit

- **D-1…D-10 (§11.2):** dispositions consistent with RESEARCH §1.3 and §G2.1; no contradiction. D-6 honored structurally (pure `engine.buffers`, opaque handles, no LWJGL in `:engine`, dumb `mod.mixin` delegation); D-8's row correctly defers to the §11.3 GPL-3.0 license qualification for future Pintonium reuse instead of the historical blanket-LGPL wording.
- **Pintonium compliance (§G11):** every PD-derived contract claim carries `[V:observed — Pintonium <path>]` provenance; every §G11.4-gated adoption/rejection carries a recorded decision with contract check (D-P5-1/2/4/5/6/7/8/9/13/14); PD §17 rows B4/B13 and §18 flip/texture-unit rows are dispositioned in §3.6; nothing traces to the AGPL transformation-lib; §G11.3 repository traps respected; the one verbatim LGPL-source quote (Sildur's comment) is attributed.
- **Current-identity discipline (§4.2 list):** every identity P5 cites as current matches its owner's current bytes — schema23/`MaterializedSource-v23` (P3 `:754`/`:4211`); `RegistryFingerprint/profile-selection-v3` (P4 `:1954`/`:2816`, D-P5-48); `ShadowHookHealth/flattened-v3` (P8 `:280`); `TextureHookHealth/application-v2` (P13 `:246`/`:1600`); `capture-plan/4` + `run-manifest/4` (P2 `:1063`/`:1144`); `projectionVersion1`; `phase13.parameters/v2` + `phase13.sidecar/v1` (P13 `:196`/`:1397`/`:1408`, D-P5 receipt at §5.3). Every older receipt inside P5 — schema19/20/21/22, `positional-route-v2`, `own-build-v1`, flattened-v2-era models, the four-row R8-2 grant — is explicitly labeled historical or superseded. No stale identity is cited as current.

## 6. Findings

| # | Location | Claim | Evidence | Severity |
|---|---|---|---|---|
| 48-1 | `docs/phase5/v1/PHASE_5_DOC.md:235` (§0.25) and `:3530`–`:3532` (§11.5 item 3) | "`verification/targets/phase-5.json` is already-present" / "exists, resolves RC3 and this artifact, and has already driven the completed Phase 5 reviews" | The `verification/` directory does not exist anywhere in the workspace (full-tree search including ignored paths; no `phase-*.json` exists). The claimed GRANTED tooling anchor is contradicted by repository state. Reword to requested/pending or repoint; the claim appears twice and both instances need the same cure. No §5 impact. | correction |
| 48-2 | `docs/phase5/v1/PHASE_5_DOC.md:27` (§0.1 input ledger) | "the five `DESIGN.md` collision warning" | `docs/MOVES.md:15` and the `:67` heading say **six** files are named `DESIGN.md` (v1.1, RC1, RC2, RC3, RC4, v3 — all present under `docs/design/`). Round 35's count correction predates v3's promotion and was never refreshed through §0.48. Update "five" → "six". No §5 impact. | correction |
| 48-3 | §0.3/§3/§4/§11 throughout | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/…]` pins | §0.41 itself records that the historical `pintonium-9c2fcc1` snapshot directory is absent (`reference-src/` holds only `Pintonium-main`), so pinned method-body observations (e.g., D-P5-35's `BufferFlipper` toggle, the Sildur's comment pin) cannot be re-derived against local bytes and rest on the recorded snapshot plus §11.3's upstream URL check. The doc discloses the limitation and routes reuse authority through the §11.3 GPL-3.0 qualification rather than RC3 §G11.2's LGPL characterization; recorded as an evidence limitation, not a defect. | note |
| 48-4 | §0 (lines 370–421) | Addendum subsection numbering jumps 0.41 → 0.45 (no 0.42–0.44) | Cosmetic anchor-hygiene gap only; the header's latest-revision pointer (§0.48, line 13) is itself correct. | note |

**Verification statement.** No build, test, Gradle, GL, or network execution was performed and none is relied on as evidence; all findings above are documentary, grounded in file reads at the stated repo-relative coordinates, and the only file written by this review is `docs/phase5/reviews/PHASE_5_REVIEW_48.md`. The bold header carries the review's single verdict; both corrections are doc-internal accuracy fixes that do not touch binding §5, so the doc's existing outstanding whole-document verification gate is neither discharged nor widened by them.
