# Phase 12 — Fresh whole-document architecture review R6

**Owner:** `docs/phase12/v1/PHASE_12_DOC.md`  
**Frozen attempt:** 5  
**Frozen owner SHA256:** `ca8c1d87976ed4621d93c65390e67620485f7a76e38c71f14d439e321a9bab1c`  
**Inventory:** `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json:61–65` identifies this owner, checksum and review round 6. The checksum is the supplied frozen identity, not a claim of independently executing a hashing command.

**Verdict: PASS-WITH-CORRECTIONS**

- Structural/blocking findings: **0**
- Required owner corrections: **2**
- Notes: **2**
- §5 impact: **yes**

## Scope and governing authority

Read the complete current Phase 12 document, §§0–12, rather than treating Review 5's correction focus as the review boundary. Applied the header-selected `docs/design/v3/DESIGN.md`, including Part I §§G0–G12 and Phase 12's specification at lines 2357–2432. Read `docs/research/v1/RESEARCH.md` §§0–1, 4.7–4.8, 7.6, 9, OQ-9, Appendix E.2, Appendix F and Appendix H. Read `docs/MOVES.md` to distinguish versioned authorities and historical reference paths.

Re-established the consumed contracts from current Phase 3 §5 and its incorporated option/state/locale/persistence declarations; Phase 1's structural, dependency and diagnostic contracts; Phase 4's publication-generation contract; Phase 7's actual reload, programmatic, Internal-session and diagnostic receivers; and Phase 11's exact GUI projection. Checked the load-bearing reciprocal P2 option adapter and P5/P7/P8/P9/P10/P13 engine-setting receivers. These receiver reads supply evidence for this owner review, not sibling certification. Review 5 was consulted subsequently as history, not as proof that its formerly checked areas remain correct.

No repository file was edited. No build, test, linter, formatter, implementation, GL or pack-tier validation was executed. The findings below are architecture-contract traces, not runtime reproduction claims.

## Independent checks

1. **Review 5's settings/view omission is corrected.** `EngineSettingEntry` now retains decoded typed/raw values and an independent `interactive` flag (`docs/phase12/v1/PHASE_12_DOC.md:340–357`). All seven entries reach the actual selection view, and all three exact-value actions are declared (`:373–393`). The presenter validates variant/key/availability/current ladder before mutation, retains failed pending writes, and submits a successful committed delta once (`:865–894`). Both vanilla and ModularUI receivers explicitly render and dispatch this surface (`:1196–1201`, `:1235–1239`). A valid out-of-ladder current value remains representable at index −1 without an implicit rewrite.
2. **Direct expression diagnostics now reach both adapters separately from P1.** The two-argument `showErrors` declaration (`docs/phase12/v1/PHASE_12_DOC.md:391–392`) is carried through presenter and adapter behavior (`:1147–1183`, `:1392–1404`). Empty declaration names suppress only attribution; empty-entry snapshots retain attempt identity/outcome; absence clears only the P11 display. This matches `docs/phase11/v1/PHASE_11_DOC.md:867–907`, its §5 incorporation at `:1246–1254`, and the actual P7 producer at `docs/phase7/v1/PHASE_7_DOC.md:3308–3323`. P1 retains its distinct message-key/args/detail/channel record and routing (`docs/phase1/v14/PHASE_1_DOC.md:4633–4676`).
3. **Presentation and locale consumption are generally coherent.** Source-ordered star expansion, declaration-based placement, retained empty slots and configured-column floors are specified in `docs/phase12/v1/PHASE_12_DOC.md:589–641`. The 18/19/27/28 slot cases yield 2/3/3/4 under the published P3 resolver (`docs/phase3/v1/PHASE_3_DOC.md:1016–1022`, `:2243–2249`). Per-key requested-locale → en_us → fallback uses presence rather than nonempty text and consumes the complete owner catalog (`docs/phase12/v1/PHASE_12_DOC.md:643–685`; `docs/phase3/v1/PHASE_3_DOC.md:2036–2086`). The payload-free selector is adopted without restoring an entry profile field, although its commit path has C1 below.
4. **Persistence boundaries are preserved.** Catalog-issued typed previews, same-bundle safe access, exact target/catalog credentials, changed-only filesystem output, global all-entry output and last-valid duplicate behavior match `docs/phase3/v1/PHASE_3_DOC.md:2142–2237`, `:3289–3407`. Phase 12's global defaults preserve `handDepthMul=0.125`, exact old-light tokens and reserved zero-only AA storage (`docs/phase12/v1/PHASE_12_DOC.md:833–947`). Unknown-safe persistence fields do not become executable controls. The programmatic key-inventory discrepancy is isolated in C2.
5. **Internal durability and failure distinctions match the actual receiver.** Phase 12's capture/commit route, complete default reset, session acceptance versus disk persistence, preserved preferences across selection/failure, and shutdown/restart lifetime (`docs/phase12/v1/PHASE_12_DOC.md:1430–1458`) match P3's authenticated fresh-catalog rebinding (`docs/phase3/v1/PHASE_3_DOC.md:2088–2138`) and P7's serialized committer (`docs/phase7/v1/PHASE_7_DOC.md:3256–3290`). Preview state never becomes a materialization override.
6. **Reload effect dispatch is closed on both sides.** The Phase 12 matrix and max/independent-OR algebra (`docs/phase12/v1/PHASE_12_DOC.md:978–1081`) match P7's actual receiver (`docs/phase7/v1/PHASE_7_DOC.md:3128–3183`). In particular, merging FULL/true/false with NONE/false/true retains FULL/true/true regardless of the latest diagnostic cause. Resource-only NONE retains the exact configuration/assets, enters the synchronous replacement gate and does not accidentally trigger P3 discovery/load. One final drain outcome does not suppress Ready-then-compensating-Off generation events (`docs/phase4/v1/PHASE_4_DOC.md:2029–2031`, `:2058–2063`; `docs/phase7/v1/PHASE_7_DOC.md:3218–3232`).
7. **Engine behavior ownership remains separate.** The independent companion preference producer is explicit in `docs/phase13/v1/PHASE_13_DOC.md:639–645`, `:1400`; render/shadow quality is supplied through P5's runtime inputs (`docs/phase5/v1/PHASE_5_DOC.md:2646–2651`); P8 consumes the actual shadow allocation (`docs/phase8/v1/PHASE_8_DOC.md:683–693`). Old-hand-light and old-lighting user→pack→local-default policies match the P9 and P10 receivers (`docs/phase9/v1/PHASE_9_DOC.md:690–705`; `docs/phase10/v1/PHASE_10_DOC.md:1153–1187`, `:1471`). No AA algorithm, unpublished choice ladder or optional post-analysis companion API is granted by this review.
8. **Current schema and staging are explicit.** P12 admits exactly schema22, including received nested/inspection identities, and treats earlier receipts as history (`docs/phase12/v1/PHASE_12_DOC.md:533–542`, `:1287–1347`), matching `docs/phase3/v1/PHASE_3_DOC.md:4042–4105`. Same-load assets and the nine source-free trees remain distinct from binary acquisition authority. §§6–12 provide degradation, thread confinement, testability, milestone staging, the OQ-9 procedure/fallback and implementation gates; absent future runtime results are not architecture corrections.

## Required corrections

### C1 — Make a profile-only selection an actionable commit

**Severity:** correction; observable lost profile action. **Owner:** Phase 12. **§5 impact:** yes, through the published `OptionEditSession` semantics and profile handoff.

**Current evidence:** `docs/phase12/v1/PHASE_12_DOC.md:751–764` applies the selected profile's option constraints and promises to preserve its selection for P7's `disabledPrograms` evaluation. However, `:774–778` defines dirty state solely by comparing option preview values with the option baseline; `:782–789` disables Apply when clean and makes Done close without committing. This is incorporated by the §5 row at `:1264`. P3 publishes independent option constraints and disabled-program lists (`docs/phase3/v1/PHASE_3_DOC.md:995–1003`) and explicitly applies disables from the supplied selected profile (`:2938–2952`). P7's actual dispatch freezes that selected profile with the committed batch and evaluates the new configuration (`docs/phase7/v1/PHASE_7_DOC.md:3135–3141`).

**Contract trace:** let the active profile A require `FOO=true`, and the next profile B require the same `FOO=true` plus `!program.gbuffers_water`. This is representable by P3's existing profile algebra. Clicking B changes no option value, so the mandated dirty comparison remains false. Apply is disabled and Done takes its clean/no-reload branch. Consequently the promised selected-profile handoff never reaches P7/P3 and B's disable is not applied. A program-only profile has the same problem. This is not a request for GUI-owned registry mutation or a new P3 profile format.

**Minimal owner fix:** retain pending versus committed profile-selection intent alongside the catalog-issued option preview, and include a changed selection requiring program reevaluation in the session's commit-needed state. Specify Apply/Done, discard, reset and failure behavior for that intent, using the existing filesystem/Internal acceptance and single-REPUBLISH path even when the option-value delta is empty. Preserve the required inferred profile display semantics without using option equality to discard the independently promised selection intent. Update the incorporated §5 row and the architecture testability cases with this exact same-options/different-disabled-programs trace.

### C2 — Admit reserved zero-only AA storage in the programmatic bridge

**Severity:** correction; valid cross-owner input is rejected. **Owner:** Phase 12. **§5 impact:** yes, directly in §5.3(D).

**Current evidence:** `docs/phase12/v1/PHASE_12_DOC.md:1411–1418` restricts programmatic engine edits to the seven user-control keys of §4.6.2 and returns `UNKNOWN_ENGINE_KEY` outside that set. Its own global storage contract recognizes the eighth reserved `antialiasingLevel=0` field (`:896–905`). P3's binding domain includes all eight keys and allows only exact `0` for that reserved field (`docs/phase3/v1/PHASE_3_DOC.md:3344–3356`, `:3398–3407`). The reciprocal P2 bridge contract expressly expects eight-key validation and reserved zero (`docs/phase2/v2/PHASE_2_DOC.md:2718–2734`), while P7 publishes the same eight-key vocabulary with no runtime/UI AA (`docs/phase7/v1/PHASE_7_DOC.md:3303–3306`).

**Observable consequence:** a caller following the P2/P7 contract and submitting a valid explicit `engineOptions={antialiasingLevel:"0"}` is rejected by the P12-owned adapter as an unknown engine key. This also prevents a caller from passing the complete canonical known-setting map unchanged through this seam. P7's capture procedure applies required edits (`docs/phase7/v1/PHASE_7_DOC.md:1872–1876`), so this finding does **not** claim that every capture necessarily forwards the unchanged reserved key or fails; the contradiction is in the publicly admitted request domain itself.

**Minimal owner fix:** distinguish the seven GUI controls from the eight canonical programmatic/storage keys. Accept exact reserved `antialiasingLevel="0"` in §5.3(D), validate it through the existing P3 domain, treat an already-zero value as unchanged, and continue rejecting nonzero values. Keep it absent from `EngineSettingsModel` and all three GUI action-key sets, with no AA runtime behavior or FXAA macro. No new P3 API, behavior feature or optional grant is needed.

## Notes and limitations

### N1 — Preserve the distinction between historical pinned evidence and available corroboration

The exact historical `reference-src/pintonium-9c2fcc1/.../VintageShaderPack{Options,Selection}Screen.java` paths in the owner header are absent. A narrow filename lookup located the permitted files under `reference-src/Pintonium-main/forge122/src/shaders/java/net/irisshaders/iris/gui/`; both were read completely. They corroborate the available vanilla screen structure, navigation, pending queue, lack of sliders and absence of hover-tooltip rendering, but were not established as the historical 9c2fcc1 snapshot. In particular, their current reload implementation is not evidence for the old unconditional `loadRenderers()` attribution. `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:707–728` remains historical mining evidence, not a substitute for that missing pinned source.

Current MCP API reads confirmed `ClientCommandHandler.instance`, `InputEvent.KeyInputEvent` and `ISelectiveResourceReloadListener`'s predicate-bearing callback. The available `reference-src/Cleanroom-0.6.12-alpha/src/main/java/net/minecraftforge/fml/client/registry/ClientRegistry.java:61–64` corroborates single-binding registration; it is not the historical loader pin. The current ModularUI example corpus did not reproduce the old GUI-search result, and current example 1057 concerns animation, so the historical numeric example ID was not promoted into GUI proof. Independently, the permitted upstream [GuiScreenWrapper at f2aa7210](https://github.com/CleanroomMC/ModularUI/blob/f2aa7210f42ffae4458c6777f723a55546a772bc/src/main/java/com/cleanroommc/modularui/screen/GuiScreenWrapper.java) directly establishes a client-only `GuiScreen` wrapper around `ModularScreen`. That establishes an available API shape, not OQ-9 fitness or runtime success.

### N2 — Receiver and implementation gates remain separate

The unpublished render/shadow/hand-depth choice ladders remain explicit inert gates in `docs/phase12/v1/PHASE_12_DOC.md:858–863`, `:1878–1885`. OQ-9, the Phase 1 dependency-configuration work, behavior milestones, actual GUI/persistence/reload execution, runtime pack conformance and final integration remain unclosed. C2 uses P2/P7 as reciprocal evidence and attributes the required change only to P12; it is not a verdict on either sibling. No forbidden transcript, Oculus blocked implementation, transformer implementation, or OptiFine decompile source was read.

## Final disposition

The R5 engine-setting and direct-diagnostic view-seam correction is substantively present in the frozen owner. Two remaining current contract defects are local and fixable without rebuilding the architecture: profile-only changes can be lost before dispatch, and the programmatic bridge rejects the reserved zero field its receivers admit.

**Final literal verdict: PASS-WITH-CORRECTIONS.** Apply C1 and C2 in the Phase 12 owner, preserve the historical evidence, and obtain fresh whole-document verification because the published §5 semantics change. This report grants no implementation, sibling-phase or final-integration clearance.

## Resolutions

**2026-09-08 — architecture-only owner amendment; not a fresh verification.** The original
attempt-5 report, frozen checksum, evidence, notes and literal verdict above are unchanged.
Only `docs/phase12/v1/PHASE_12_DOC.md` and this appended resolution record were edited.
No validation command, implementation, build, test, formatter, linter or runtime was executed.

- **C1 — addressed in the active owner contract, D-P12-34.** §§2.2/4.5/5.1/5.3 now retain
  committed/pending explicit `Optional<ProfileName>` selection independently of option values.
  `isDirty()` is option delta OR selection delta; `pendingChangeCount()` adds one for changed
  selection. Inference still controls ProfileCycle display; the explicit cursor permits cycling
  equal-option profiles and the unsaved summary exposes the separate intent. Apply/Done,
  discard, reset, invalid batches, write/capture failure, retry and postacceptance failure
  explicitly preserve the correct pending/accepted boundary. Filesystem COMMITTED and Internal
  SESSION_ACCEPTED freeze selection through P7's existing transport and submit one REPUBLISH,
  including empty option deltas; no profile persistence key, GUI registry mutation or preview
  materialization argument was added. §8 plans the exact A:FOO=true → B:FOO=true plus
  !program.gbuffers_water boundary, program-only intent, reset/discard/failure and independent
  inference/cursor cases. These are planned architecture checks, not executed tests.
- **C2 — addressed in §5.3(D), D-P12-35.** The programmatic/storage domain is the seven control
  keys plus reserved antialiasingLevel. P3 validates exact `"0"`; already-zero is UNCHANGED,
  nonzero/noncanonical text is INVALID_VALUE, unknown/alias keys remain UNKNOWN_ENGINE_KEY.
  The complete unchanged eight-key map is admitted without REPUBLISH/AA effects; the existing
  NONE receipt remains legal. Seven GUI entries and the three GUI action-key sets exclude AA.
  No runtime AA or MC_FXAA_LEVEL was introduced. §8 plans those behavioral boundaries.
- **Coordinated schema receipt — D-P12-36.** Active admission now requires exact schema23
  containing/nested/inspection identity and MaterializedSource-v23 for P3's range-capable
  selectors. Older numeric receipts are historical; nine metadata-only trees and
  projectionVersion1 remain. P9 alone resolves registries; no unrelated asset/native/option/
  parameter-domain authority changed.

**Receiver receipts outstanding with Main:** P7 must receive the independent accepted profile
intent and single-REPUBLISH behavior even with UNCHANGED value receipts, including reset and
Internal acceptance/failure lifetime. P2 must receive the same-options/different-disabled-programs
boundary and exact reserved-zero eight-key admission. §§11.1/11.4 record decisions and hand-offs.
No health identifier is added or changed. Main owns reciprocal documents and integration records.

**Verification trigger:** §5 changed. Fresh whole-document owner and receiver verification,
implementation/runtime conformance and the existing dependency/OQ/integration gates remain
required. This resolution appendix does not upgrade the original verdict to PASS or grant
implementation, sibling-phase or final-integration clearance.
