# Phase 12 — Fresh whole-document architecture review 5

**Verdict: PASS-WITH-CORRECTIONS**

- **Blocking findings:** 0
- **Required owner corrections:** 1
- **Receiver/readiness notes:** 3
- **section5Impact:** true
- **Reviewed owner:** `docs/phase12/v1/PHASE_12_DOC.md`, all §§0–12, 1,890 lines.
- **Assigned frozen SHA-256:** `8113883e26b466da251de3bfdd2f8f6baf341a6a17895a0340e5b784215fe0fa`. The coordinating agent reported that the freeze still matches; no checksum or validation command was run by this reviewer.

The architecture is substantially coherent, including the current schema21 cutover and its receiving lifecycle contracts. One fixable owner-interface gap prevents a clean PASS: the declared complete view seam cannot deliver or operate all the UI features the document requires. This does not require rebuilding the subsystem, but its correction changes §5 and therefore requires fresh verification.

## Review basis

Read the entire owner document, including its historical header records, conformance tables, algorithms, failure model, testability plan, milestone table, OQ-9 specification and implementation checklist. Applied `docs/design/v3/DESIGN.md` Part I §§G0–G12 and the Phase 12 specification, particularly §G1.2 interface honesty, §G5.3 dependency/final-integration rules and the Phase 12 doc gate. Read `docs/research/v1/RESEARCH.md` §§0–1, 4.7–4.8, 7.6, 9, the OQ-9 register, Appendix E.2, Appendix F and Appendix H.

Dependency reads covered Phase 1 §5 and relevant incorporated package/seam/licensing/diagnostic rules; Phase 3 §5 and incorporated option declarations, §4.3 validation/persistence/locale/Internal semantics and §4.1 asset lifetime; Phase 4 §5 generation/publication contracts; Phase 7 §5, its reload/programmatic/diagnostic dispatch and §4.8.1 destructive-resource gate; Phase 11 §5 and §4.9.1 diagnostic projection; and Phase 2 §5.4’s programmatic receiver. These reads establish the contracts Phase 12 consumes, not independent certification of those owners.

## C1 — Complete the view seam for engine settings and direct diagnostics

**Severity:** correction. **Owner:** Phase 12. **§5 impact:** yes.

**Evidence:** Phase 12 §2.2, lines 340–385, declares `EngineSettingsModel` separately but gives `OptionScreenView` only `showPackSelection(PackSelectionModel, PackSelectionActions)`, `showOptions(...)`, `showErrors(List<EngineDiagnostic>)` and `close()`. `PackSelectionModel` contains only discovery generation, candidate rows, selection and last-action summary. The explicitly closed `PackSelectionActions` has only select, refresh, open-folder, open-options and close. No view input contains the engine-settings model, and no action accepts an engine-setting edit. Nevertheless §4.6.2 and §9 require seven controls, while §4.10.1 and §5.1 explicitly say this is the **entire** view seam and adapters must invent nothing.

The same terminal boundary is incomplete for the separately adopted expression panel: §§4.9/5.3(C) correctly receive the immutable P11 snapshot through P7, but `showErrors(List<EngineDiagnostic>)` cannot carry its attempt outcome, identity or direct entries. `docs/phase11/v1/PHASE_11_DOC.md` §§4.9.1/5.5.1 and `docs/phase7/v1/PHASE_7_DOC.md` §5.1 require direct source-free presentation, expressly not conversion into the P1 SHADER_GUI channel. Receiving the snapshot in a presenter does not deliver it through the declared view interface.

**Impact:** An implementation confined to the promised interchangeable thin adapters cannot render/change the seven settings or render the direct expression diagnostics. It must either omit required v0.4 UI behavior, invent additional interfaces, or violate the prohibited diagnostic conversion. This is a producer-to-final-consumer gap, not missing runtime test evidence.

**Required change:** Publish the engine-settings input and editing intents through the view seam, binding them to the existing canonical validation, persistence and reload classifier. Make the setting presentation preserve the decoded current value independently of its choice index and explicitly represent whether mutation is enabled, as §4.6.2 requires for unavailable behavior/choice ladders. Publish a separate immutable direct-expression diagnostic input or a combined presentation-only diagnostics model that retains its identity/outcome and does not mutate or reclassify either producer’s records. Update the corresponding §2.2 declarations, §5.1 rows, both adapter descriptions and behavioral testability cases together. No P3 codec, P7 lifecycle, or P11 channel change is required.

## Checked and coherent within this owner

- **Doc-gate coverage:** §3 maps 20 Appendix F.3 rows, 13 Appendix F.4 rows and 17 additional rows. `*`, `<empty>`, profile selection, discrete sliders, tooltip warning markers and post-expansion column widening all have algorithms, not merely feature names. The required thirteen document sections are present. OQ-9 has procedure, criteria and a designed vanilla fallback; an unperformed spike is not an architecture defect.
- **Current selector migration:** §§4.3.2/5.2 correctly consume payload-free `ScreenProfileEntry()` through the existing profile-cycle route. Phase 3 §5.1 explicitly publishes the same interpretation. Preview inference stays catalog-bound, and Phase 7 §5.1 freezes the selected profile with its committed option batch before evaluating the newly loaded configuration.
- **Schema/identity:** I-3 and §5.2 require exact schema21, matching nested/inspection schemas and current materialization identity. Override-only structural load success does not invent base sources or merge worlds. Same-load assets remain opaque; resource-only NONE retains the exact configuration/assets. Historical schema19/20 receipts are explicitly superseded rather than active alternative gates.
- **Option and global persistence:** §§4.5–4.7/5.2 consume catalog-issued complete states, closed invalid-state results, authenticated target/catalog pairing, changed-only pack writes and all-entry global writes. Failed commits retain edits and do not queue reload. Durable selection uses fresh discovery and all five resolver outcomes, not display-name matching or serialized candidate IDs.
- **Internal acceptance:** §5.3(E) matches Phase 3 §4.3 and Phase 7 §5.1: capture under the exact current catalog, globals before atomic acceptance, SESSION_ACCEPTED distinct from disk durability, accepted preference retained across switches/failure and cleared on shutdown/bundle replacement. Views must not submit a second reload.
- **Reload dispatch and lifetime:** The full trigger matrix is explicitly received in Phase 7 §5.1. Max-lifecycle and independent OR flags survive dispatch; NONE is not promoted by a resource-reload reason. Phase 12 §4.8.3 correctly treats its listener as post-replacement notification and delegates destructive lifetime protection to Phase 7 §4.8.1. Final receipts and actual Phase 4 generation events remain distinct; compensating Off may generate a second event.
- **Authority/licensing:** Phase 12 retains the seven-control/no-AA boundary, ratified typed old-light policy, owner-only parsing and GPL/LGPL notice obligations. ModularUI is conditional and not bundled. The historical slider-evidence and tooltip-attribution contradictions remain disclosed; neither authorizes reading prohibited transformation material.

## Notes and limitations

1. **Existing receiver gates remain open.** §§4.6.2/11.4 explicitly leave renderResMul, shadowResMul and handDepthMul choice ladders with their behavior owners. This review does not grant those ladders, enable inert controls, resolve OQ-9 or certify a behavior milestone. Those are receiver/readiness notes, not additional owner corrections.
2. **Historical primary evidence is not silently re-certified.** The exact `reference-src/pintonium-9c2fcc1/...` GUI paths were unavailable. A narrow filename lookup found both permitted files under `reference-src/Pintonium-main/...`; both were read fully. They support the available vanilla view/navigation/apply-discard and missing-slider/tooltip observations, but this checkout was not established as the historical 9c2fcc1 snapshot. The current MCP command-handler and selective-listener APIs match §4.8. The required ModularUI GUI query returned no examples; a broadened query returned ModularUI examples, while current example ID 1057 is an animator method, not the historical GUI evidence. Therefore the dated GUI corpus claims remain historical evidence, not newly reproduced platform proof. These limitations do not defeat the independently specified vanilla fallback or justify claiming the ModularUI spike passed.
3. **No implementation or integration clearance.** No files were edited. No validation, build, test, formatter, linter, GUI launch or conformance run was performed. §§0.1–0.7’s older dependency statuses were treated as historical, as the current header directs. A corrected/freshly reviewed Phase 12 can certify only its frozen owner architecture; §G5.3’s other owner reviews and final IR-01 remain separate requirements.

**Disposition:** Apply C1 in Phase 12, preserving the existing producer contracts and historical evidence, then obtain a fresh whole-document review because the published view interface changes.

## Resolutions

### C1 — Complete engine-setting and direct-diagnostic view seam (2026-09-08)

Applied in `docs/phase12/v1/PHASE_12_DOC.md` §§2.2, 4.6.2, 4.9–4.10, 5.1/5.3,
6, 8, 9, 11 and 12 as D-P12-32. The original review above remains unchanged.

- `showPackSelection(PackSelectionModel, EngineSettingsModel, PackSelectionActions)` now
  delivers all seven settings. Every entry has explicit `interactive`; Choice carries
  decoded `rawValue` independently of `valueIndex`, including unavailable/empty ladders.
- Closed actions now include `ApplyOutcome setEngineToggle(String key, boolean value)`,
  `ApplyOutcome setEngineTriState(String key, TriStateValue value)` and
  `ApplyOutcome setEngineChoice(String key, String rawValue)`. Presenter checks current
  variant, key, gate and owner token; valid edits use the existing complete global validation,
  all-entry write-through and reload classifier. Failed writes retain pending values and
  enqueue nothing; only committed deltas submit once, never again from the adapter receipt.
- `showErrors(List<EngineDiagnostic>, Optional<ExpressionDiagnosticGuiSnapshot>)` delivers
  both immutable producers directly to both adapters. P11 identity, final attempt outcome,
  empty-entry snapshots and source-less entries survive; the producer's empty declarationName
  suppresses only the label. P1 records remain separate; no SHADER_GUI conversion or new channel.
- Vanilla and ModularUI descriptions, failure behavior, behavioral cases, milestones and
  implementation checklist now consume those exact signatures instead of implicit extensions.

### Current schema admission receipt (2026-09-08)

D-P12-33 receives P3 schema22/current-constant containing/nested/inspection admission for the
coordinated isolated BLOCK forced11300Rules extension. P12 does not select or reinterpret
mapping eras. Earlier numeric receipts are historical; projectionVersion=1, nine trees,
same-load assets, native ownership, locale/session/options and exact NONE retention stay intact.

Architecture-only correction; §5 changed. No implementation, validation command, build, test,
formatter, linter, GUI launch, fresh verdict or integration clearance is claimed. Fresh
whole-document owner/receiver verification and remaining readiness gates are still required.