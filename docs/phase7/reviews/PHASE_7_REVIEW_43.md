# Phase 7 Architecture Review — Round 43 — Frozen attempt7

**Owner:** `docs/phase7/v1/PHASE_7_DOC.md`  
**Frozen SHA-256:** `6c439398ccf1b9f281aa00c22396a89f288705d66c78cf2d43371be8b84f3079`  
**Review:** fresh, independent, read-only whole-owner architecture review. The hash is the supplied inventory identity recorded at `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_7.json:42–45`; this reviewer did not run a checksum command.

## Scope and selected authority

Read the complete current Phase 7 owner, §§0–12, not merely its latest amendments. Followed `AGENTS.md` and `docs/MOVES.md`; used the header-selected `docs/design/v2.0-RC3/DESIGN.md`, Part I §§G0–G12 (`:92–1109`) and the Phase 7 assignment (`:1805–1953`), rather than substituting the globally newest design. Re-established the governing RESEARCH lifecycle, frame ordering, platform questions, hook needs, program-stage inventory, celestial cadence, and Appendix E coverage from `docs/research/v1/RESEARCH.md`, including §§0–1, 4.4, 5.3, 7.1, the OQ-3/OQ-4 rows, and Appendices A.1, D, E and the relevant F.1 flag semantics.

Read dependency §5 contracts for P1–P6 and the incorporated load-bearing definitions. Traced the actual reciprocal architecture branches in P8–P13 for shadow admission/restoration, IDs and deferred TESR ranges, vertex lifecycle/input participation, expression retirement, profile acceptance and texture acquisition/atlas lifetime. These checks establish P7 integration findings only; they do not certify sibling owners.

## Independent checks

1. **Frame and publication ownership.** Checked the complete preparation/publication transaction, actual accepted registry generation, P6 adoption, P5 acceptance, P13 readiness, P9/P10 activation and compensation-to-off. Examined ordinary/nested/fullscreen selection and snapshot paths, including P5's already-frame-consumed `Failed` result and the independent-cleanup obligation. Checked virtual-pre sequencing, copied-depth consumed-point order, adjacent native instance execution, and final capture before presentation. No additional substantive P7 defect was established in these paths.
2. **Explicit profile selection reaches the compiler.** P7 freezes and forwards accepted intent in `RegistryBuildRequest` at `docs/phase7/v1/PHASE_7_DOC.md:3217–3223`, with registry-wide `INVALID_PROGRAM_STATE` containment at `:3633–3640`. This is consumed by the actual pre-materialization/pre-GL evaluator branch at `docs/phase4/v1/PHASE_4_DOC.md:1333–1435`, using P3's exact absent/named/unknown-profile and closed-result semantics at `docs/phase3/v1/PHASE_3_DOC.md:2965–2983`. P12's independent committed/pending intent and acceptance behavior were checked at `docs/phase12/v1/PHASE_12_DOC.md:747–836`. Equal option values do not erase different accepted profile intent.
3. **Shadow outline exclusion and health.** P7's receiving branch at `docs/phase7/v1/PHASE_7_DOC.md:1772–1789` agrees with P8's actual exact-receiver, non-reentrant entity-call predicate guard at `docs/phase8/v1/PHASE_8_DOC.md:1302–1370`, rather than merely suppressing newly collected glowing entities. Checked the complete 60-row scalar catalogue and 59-non-CLOUD aggregate at P8 `:1547–1680`, and P2's actual preservation/rejection rules at `docs/phase2/v2/PHASE_2_DOC.md:1354–1371`. The available current Cleanroom RenderGlobal patch corroborates why the retained-outline predicate must be excluded before later tile entities. Fresh MCP resolution confirmed `RenderGlobal.isRenderEntityOutlines` / `func_174985_d()Z`.
4. **Atlas outer lifetime and application evidence.** P7 `docs/phase7/v1/PHASE_7_DOC.md:1834–1863` receives the eight-entry `TextureHookHealth/application-v2` catalogue and the outer `loadSprites` scope. Checked P13's actual pre-Pre entry, allocation capture, matched-Post acceptance and exceptional outer-finally invalidation at `docs/phase13/v1/PHASE_13_DOC.md:1389–1494`; checked P2's receiver at `docs/phase2/v2/PHASE_2_DOC.md:1372–1384`. Fresh MCP confirmed the exact `TextureMap.loadSprites` / `func_174943_a` descriptor. Current TextureMap patch ordering corroborates Pre/population inside the required lifetime. Frozen application counts remain distinct from runtime extent acceptance.
5. **Conventional inputs remain inherited when absent from the source.** P7's complete-plan receipt at `docs/phase7/v1/PHASE_7_DOC.md:2453–2464` was checked against actual client/VBO/list paths at `docs/phase10/v1/PHASE_10_DOC.md:698–816` and P1's source/plan authentication and capture isolation at `docs/phase1/v14/PHASE_1_DOC.md:4231–4390`. Physical CLASSIC_56 filler does not independently enable COLOR/UV1, invoke absent-stream Forge cleanup, or bake the first entity's current values into reusable lists.
6. **Typed clear completion is all-success.** P7's dispatch at `docs/phase7/v1/PHASE_7_DOC.md:1096–1101` admits GBUFFERS only for `SUCCESS`. Checked actual finite realized-format conversion, typed per-route dispatch and full-clear consumption at `docs/phase5/v1/PHASE_5_DOC.md:1709–1811`, and the native-tier/state-restoration grant at `docs/phase1/v14/PHASE_1_DOC.md:3964–4048`. Rejection, partial work and inner/outer restoration failure cannot authorize ordinary rendering or consume mandatory full-clear.
7. **Replay and final-use lifecycle.** Checked required P6 replay delivery, failure latching and original-error preservation against `docs/phase6/v1/PHASE_6_DOC.md:1341–1506`; checked retirement/adoption and service retention at `:1673–1800`, including P11's actual close-before-P6-retire receiver at `docs/phase11/v1/PHASE_11_DOC.md:1036–1106`. P13's actual Bound-only lease transfer and independent close obligations were checked at `docs/phase13/v1/PHASE_13_DOC.md:1318–1387`. P7 capture remains on the current `/4` contract and cannot substitute planned or inferred timing/resource/health evidence for actual observations.

## Substantive corrections

### C1 — Deliver main celestial vectors without requiring shadow-buffer demand

**Severity:** P2 / medium. **Section 5 impact:** yes.  
**Owner anchor:** `docs/phase7/v1/PHASE_7_DOC.md:2904–2910`; corresponding producer hook at `:1606`.

The binding main-sky route permits H-SKY-02 only to repeat the sample constructed for an authenticated P8 publication. That leaves a valid v0.2-or-later pack declaring `sunPosition`, `moonPosition` or `upPosition`, but requiring neither shadow-depth nor shadow-color buffers, without any celestial sample. P8's actual planning/construction branch returns `NotRequested` and creates no ready publication when both minima are zero (`docs/phase8/v1/PHASE_8_DOC.md:630–820`). Its invocation receiver also returns `Completed` for `ShadowEstateNotRequested` or `ShadowEstateUnavailable` before camera computation and the `updateCelestial` event (`:853–895`). P7 supplies no alternate authenticated main-sky producer, so P6 continues to expose pending-neutral vectors rather than the required eye-space values. This is a main lighting/sky contract failure, not merely missing shadows.

The controlling contract separates the main sky celestial update from shadow-camera setup: `docs/design/v2.0-RC3/DESIGN.md:1705–1711,1832–1835` and `docs/research/v1/RESEARCH.md:1361,1380–1383,1428–1430`. P6 stages these values at v0.2, not only for shadow-demanding packs (`docs/phase6/v1/PHASE_6_DOC.md:936–939`). The intentional v0.1 neutral staging is not the finding.

**Minimal owner fix:** revise P7's §4/§5 main-sky dispatch so that, once the v0.2 celestial producer is installed, current-frame/main-camera celestial values can be acquired and delivered at H-SKY-02 independently of shadow requested/enabled/estate availability. Preserve one owner-authorized celestial formula and the unchanged P6 event identity/payload. Obtain the corresponding narrow P8 value-production grant and reciprocal P6/P8 adoption if needed; do not manufacture a Ready shadow plan, allocate shadow targets for a no-shadow pack, or duplicate private shadow math in P7. Keep before-shadow-activation delivery for real shadow work and equal-sample reuse where applicable.

## Notes

### N1 — Historical source pins and license confidence remain qualified

**Severity:** informational; no corrective count. The historical Pintonium and Cleanroom pins named at `docs/phase7/v1/PHASE_7_DOC.md:27–34` are not present in this checkout. Available `reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch`, `RenderGlobal.java.patch`, and `texture/TextureMap.java.patch` provide current-version corroboration only, not proof of those historical revisions. The bounded Pintonium design mining report and permitted author docs/behavioral digest establish documented or summarized behavior, not an independent license clearance or exact-source-copy audit. No forbidden implementation source, chatlog, root transcript, Oculus pipeline/transform, relocated GLSL library or glsl-transformer implementation was inspected.

## Limitations and verdict

This is architecture/source-contract inspection, not implementation or runtime certification. No files were edited, no report was written, and no build, formatter, linter, test suite, checksum or validation command was run by this reviewer. MCP symbol/signature results establish mappings, not injection application counts or native behavior. OQ-3/OQ-4, real-context rendering, capture correctness and source-license clearance retain their separate gates. Historical reviews and fix receipts were not used as verdict authority.

The owner has a coherent overall structure and the five emphasized attempt7 receiving changes have concrete matching branches. One localized but substantive main-celestial dispatch defect remains; structural rejection of the entire owner is not warranted.

**Corrections: 1. Notes: 1. Section 5 impact: yes.**

**PASS-WITH-CORRECTIONS**

## Resolutions

2026-09-08 architecture-only fix-up. The original review body, note and verdict remain unchanged.

- **C1 / P7R43C1:** D-P7-72 receives P8 D-P8-38 public CelestialMath at actual H-SKY-02
  using accepted same-frame/main-camera inputs and finite configured rotation, independently
  of shadow demand/Ready plan/estate/health. P6 payload/identity is unchanged; real-shadow
  before-activation delivery and same-association sample reuse remain.
- D-P7-73 additionally receives P13/P5 exact companion baseline/actual-base context and
  ordinary/nested/fullscreen/shadow lease/refresh dispatch, including P8's required invocation
  receiver. No extra root-shadow activation, manual native binder or stale serial reuse.
- D-P7-74 receives66/65 non-CLOUD flattened-v3 with six independent sort-cache GET/SET rows.
  D-P7-75 receives H10-BRIGHTNESS-4 and final completed BLOCK input authority, not partial ITEM/
  filler participation; P1 recorder complete-plan factory is incorporated.
- Algorithms, exhaustive declarations, §5, planned no-shadow/two-atlas/failure cases and checklist
  change together. No validation commands or runtime claims; fresh whole-document owner/receiver
  reviews and all existing implementation/source-confidence gates remain.
