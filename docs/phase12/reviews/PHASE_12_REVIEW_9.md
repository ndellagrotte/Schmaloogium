# Phase 12 — architecture review 9

**Date:** 2026-09-08. **Frozen document:** `docs/phase12/v1/PHASE_12_DOC.md` SHA-256 `8f65d719d3d9fc3f9a4815db4e7ce4cbce75d7d7b10387e373f3735cf58d91e8`. **Verdict:** PASS-WITH-CORRECTIONS (1 corrections, 3 notes). **§5 impact:** yes — §5.1's OptionEditSession row, §4.5.2's initialization rule and §5.3(E) all attribute session-selection (re-)initialization to 'P7's accepted selection' / 'P7's existing selected-profile transport', but Phase 7's current §5 publishes no accepted-selection read-back (ProgrammaticOptionSnapshot deliberately carries no profile field per D-P7-60). The corrected initialization route must be restated in §5 — either presenter-owned persistence of committed selection across I-3 invalidation, or a new P7 read-back grant Main coordinates with the Phase 7 owner.

---

# Phase 12 — Whole-owner architecture review R9 (attempt 8 re-run)

**Owner:** `docs/phase12/v1/PHASE_12_DOC.md` · **Frozen SHA256:** `8f65d719d3d9fc3f9a4815db4e7ce4cbce75d7d7b10387e373f3735cf58d91e8` (verified with `sha256sum` before reading; matches `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_8.json` phase 12, reviewRound 9). Read-only review: no file was edited and no build, test, formatter, linter or validation command was run.

## 1. Governing design and authority

1.1 The header selects `docs/design/v3/DESIGN.md` — consistent with the current phase-header mapping (P2/P10–P14 on v3). Read: Part I governance (§G1.3, §G2.4, §G4.1–G4.6, §G5.1–G5.3, §G11) and the Phase 12 spec at ll. 2357–2432, plus the two header-claimed exception reads (Phase 4 reload-safety row ll. 1558–1563; Phase 7 lifecycle spec ll. 1845–1990).
1.2 RESEARCH remains the superior contract authority; App F.3/F.4 (ll. 1454–1480) and §4.7 (ll. 601–618) were re-read and the §3.1/§3.2 conformance rows check against them row by row (all 33 rows, zero unmapped; the doc gate's constructs — `*`, `<empty>`, red-`!`, auto-widen past 18, sliders, columns floor — are all covered).
1.3 Current integration Resolutions in `docs/PHASE_INTEGRATION_REVIEW.md` were read (IR-03/05/06/07/08/16/17/24/25 and the attempt5–7 resolution sections). All P12-relevant IR corrections are incorporated in the current bytes: IR-16 vocabulary (`normalMapEnabled…antialiasingLevel`, baseline `true,true,1.0,1.0,0.125,default,default,0`), IR-25 durable `FilesystemCandidateReference`, IR-06 exact ReloadCoordinator translation, IR-07 observed-generation receipts, IR-17 direct P11 projection, IR-24 tri-state old-light, IR-08 independent companion preferences.

## 2. Scope and §5 audit performed

2.1 The complete 2253-line document was read, §§0–12, including the D-P12-37/38 amendments.
2.2 Consumed contracts verified against current owner bytes (not decision-log prose): **P1 v14** — `engine.config` granted "Phase 3 (+12)" (l. 1736), `mod.gui` granted Phase 12 (l. 1765), `SHADER_GUI` per-pack store with Phase 12 as sink (ll. 4979–4986, 5847, 6693); **P3 v1 §5.1/§5.3** — `CURRENT_SCHEMA_VERSION == 23`, nested `IdMappingInput`/`PackDecisionSnapshot` equality, `MaterializedSource-v23`, `ScreenModel.resolvedColumns` floor formula `max(explicitColumns.orElse(2), ceilDiv(slots,9))`, `inferProfile` Inferred/InvalidState, `localizedDecorations()` per-key requested→en_us→fallback with present-empty suppression, resolver outcomes `Resolved/Missing/Ambiguous/KindChanged/InvalidSnapshot`, target rejections `NULL_CANDIDATE|FOREIGN_DOMAIN|UNKNOWN_CANDIDATE|SUPERSEDED_GENERATION|NON_FILESYSTEM|UNAVAILABLE`, candidate enums, `captureInternalOptions` failure domain `NULL_INPUT|FOREIGN_CATALOG|NOT_INTERNAL|INVALID_STATE`, canonical reference encoding (`d:`/`a:` + uppercase `%HH` of every NFC UTF-8 byte), eight-key global invariant; **P7 v1 §5.1** — exact `ReloadCoordinator.submit(ReloadRequest)`, the trigger→effects translation matrix matching §4.7.3 row by row, `RegistryGenerationListener`, `ReloadDrainReceipt`/`ReloadOutcomeSource`, `ExpressionDiagnosticGuiSource`, `ProgrammaticOptionBridge`/`ProgrammaticOptionSnapshot`, `InternalOptionCommitter` with validation order and SESSION_ACCEPTED rules, `ProgrammaticApplyRejection` domain, D-P7-60/67 profile-intent receipt; **P4 v1 §5** — `RegistryBuildRequest.profileSelection`, `evaluateProgramStates` exact-pair evaluation, `RegistryFingerprint/profile-selection-v3` (D-P4-43), generation-per-accepted-publication; **P11 v1 §5.6** — exact `ExpressionDiagnosticGuiSnapshot` record and source-free template law; **P2 v2 §5** — R15/R16 adopted through P12 §5.3(D–E)/P7 bridge, D-P2-56 seven-vs-eight-key boundary, `RUN-OPTIONS-ROUNDTRIP` named run.
2.3 **D-P12-38 scrutiny.** The presenter-owned availability architecture is complete and internally consistent across all seven landing sites: §2.2 declarations (`OptionActionAvailability`, `OptionSessionAvailability`, `ProfileCycle.applicable/availability`, model field); §4.4.4 producer/gate-composition/reason-precedence/preflight/no-op/REJECTED laws (reset=mutation, apply=mutation∧isDirty, done=clean-enables, profile=applicability∧mutation; absent-iff-enabled reason; no-widget-count inference); §4.5.1 no-definitions≠Custom; §4.5.2 availability-transition row; §4.5.3 preflight-before-every-mutating-step; §4.10.1–4.10.3 both views binding supplied flags/reasons and the pending summary independently of grid/profile-row presence while preserving Back/discard/global settings; §5.1 incorporation; §8.1 cases (`profile_emptyCatalogIsNotCustom`, `profile_internalProgramOnlyMissingCommitter`, `options_staleEnabledCallbackRechecks`, `reset_availabilityBeforePreviewMutation`, `options_availabilityRefreshPreservesSummary`) plus §8.3 actual-view obligations; §12 items 1/7/8/13. The callback-preflight list covers every mutating session operation (`toggle`, `cycle`, `setValueIndex`, `cycleProfile`, dirty apply/Done, `resetToPackDefaults`); `discard`/navigation/global settings are deliberately and consistently ungated. No defect found in this area. D-P12-37's pending-summary contract likewise re-verifies cleanly against P3's locale catalog and P7's unchanged transport.

## 3. Correction

### C1 — Session-selection initialization names a P7 input that P7 does not publish

**Severity:** correction (P2). **Locations:** `docs/phase12/v1/PHASE_12_DOC.md:839` (§4.5.2, D-P12-34), `:887` (§4.5.2 catalog-replacement transition), `:925-927` (§4.5.3), `:1421` (§5.1 OptionEditSession row: "P7 existing selected-profile transport"), `:1632-1633` (§5.3(E)).

**Defect.** The owner requires the committed/pending explicit `Optional<ProfileName>` baselines to be *initialized from P7's accepted selection for the exact pack identity, not inferProfile* (l. 839), and after catalog replacement to be *"re-resolved through the existing P7/new-owner route"* (l. 887). No such read-back exists in any dependency's published contracts: P7's §5.1 surface (`ReloadCoordinator`, `ReloadDrainReceipt`, `ReloadOutcomeSource`, `ExpressionDiagnosticGuiSource`, `ProgrammaticOptionBridge.current() → ProgrammaticOptionSnapshot(PipelineIdentity, OptionConfiguration, EngineOptionData)`, `InternalOptionCommitter`) returns no `ProfileName` anywhere, and D-P7-60 explicitly states "Programmatic maps add no profile field and preserve the accepted choice." P7's only selection transport is one-directional — it *freezes* the choice from the P12-owned edit/selection model at drain (`PHASE_7_DOC.md:3293-3298`) and forwards it into P4's `RegistryBuildRequest`. P4's §5 likewise exposes no selection query. §5's grant claim of an "existing selected-profile transport" serving initialization is therefore unfounded on the read side.

**Failure path.** User applies profile B (equal options to A, plus `!program.gbuffers_water`); P7 freezes B, P4 disables the program. A FULL reload replaces the catalog (I-3). The options GUI reopens and the new session must initialize both selection identities (l. 887). With no published source for the accepted selection, the initializer must either use `inferProfile` — explicitly forbidden at l. 839, and wrong here since equal options infer A — or use empty. Either way the committed baseline is mislabeled (GUI shows clean/A while the runtime holds B), and a subsequent ordinary edit + Apply submits selection A-or-empty, silently re-enabling the program B disabled without the user ever selecting that. The same gap applies to an Internal pack reopened after an Off/filesystem round-trip, where P7's identity-keyed session preference (P7 ll. 3443-3461) is the only store of the accepted intent and is equally unpublished.

**Minimal resolution (owner P12, receiver P7; Main coordinates if option (b) is chosen).** (a) Preferred, P12-local: declare in §§4.5.2/4.5.3/5.1/5.3(E) that the *presenter-owned* edit/selection model — the same "P12-owned edit/selection model" P7 §5.1 freezes from — survives catalog replacement, retaining the committed selection and revalidating its `ProfileName` against the new catalog's `profiles()` (dropping it only if the name no longer resolves), so initialization never needs a P7 read-back; or (b) request a P7-published accepted-selection accessor (e.g., extending `ProgrammaticOptionSnapshot` or a new source), which requires a reciprocal P7 §5 change. The §5.1 row text "P7 existing selected-profile transport" must stop implying a read route that does not exist.

## 4. Notes (optional; no architecture correction ordered)

**N1 — P2 presentation-golden family remains an unadopted receiver hand-off.** §8.2 states "P2 owns the manifest adapter and update policy" for presentation-model goldens and §5.1 row 1 names "Phase2 source-free presentation projection" as a consumer, but P2's current §5.1 golden-format rows list P3/P4/P5 as content owners only and contain no presentation family (P2 §4.11.4 acknowledges only locale-catalog consumption). Pre-existing, tracked by the integration process as a pending new-family proposal, and P12 §11.4 does not claim executed conformance — so a note; Main should reconcile before §12 item 17 executes.

**N2 — GUI-message localization mechanism unnamed.** §4.4.4/§4.5.2 require presenter-prepared reasons/summaries "in the model's frozen locale" using "existing GUI localization", but no GUI message catalog (vanilla I18n keys vs. literals) is specified anywhere in this phase or P1. Presenter-prepared text makes this implementation-level; record the catalog decision before §12 item 7.

**N3 — Historical reference pins remain non-reproducible.** `reference-src/pintonium-9c2fcc1/` does not exist in this checkout (only `Pintonium-main/`), and the P1/P3 numeric locators quoted in §§0.1–0.5/3–5 predate the current 6975-/5510-line revisions. This repeats Review 8 N1 verbatim: keep those pins historical; section-level references were re-resolved and remain accurate.

## 5. §5 grant sufficiency

Current §5 grants are **sufficient for every consumed contract except the single input named in C1**: P3 schema23 option/locale/persistence/discovery surface, P7 coordinator/committer/programmatic-bridge/receipt/listener surface, P4 generation identity, P11 snapshot, P1 channel/package grants and P2's adopted R15/R16/D-P2-56 boundary all reciprocate exactly. The accepted-profile-selection read-back attributed to P7 at `PHASE_12_DOC.md:839,887,1421,1632-1633` is the one §5-incorporated claim without an owner-published source, and it must be restated (C1) before §5 can be called complete.

## 6. Limitations / unexecuted evidence

No implementation, native or runtime verification exists or is claimed; no MCP lookups or Pintonium/ModularUI source reads were performed this round (N3 limits historical pins to recorded evidence); the OQ-9 spike, §8 test cases and §8.3 view checklists are architecture obligations, not executed proof. Phase 3/7 and this document themselves remain unverified per §G1.3; nothing here certifies sibling owners, implementation readiness, or the final eligible G5.3 integration review, and it does not waive the §12 pre-item-1 fresh-verification gate.

## 7. Verdict

The D-P12-37/38 architecture is complete, coherent and correctly landed across declarations, producer laws, view obligations, §5 incorporation, §8 cases and the §12 checklist; every other consumed contract matches the current owner bytes. One discrete §5-impacting boundary defect (C1) remains: the session-selection initialization route names a P7 input that P7 does not publish. Localized and fixable within Phase 12 (or via one coordinated P7 grant); it does not justify structural FAIL.

**PASS-WITH-CORRECTIONS — 1 correction, 3 notes, §5 impact: yes.**

## Resolutions

### C1 — Applied in architecture, option (a) P12-local (2026-09-08), D-P12-39

`docs/phase12/v1/PHASE_12_DOC.md` now attributes session-selection (re)initialization to the
presenter-owned edit/selection model instead of the unpublished P7 read-back. The model — the
same P12-owned edit/selection model P7 freezes from at drain — survives catalog replacement
retaining the committed explicit `Optional<ProfileName>` selection, revalidated against the
new catalog's `profiles()` and dropped only if the name no longer resolves. Edited sites:
§4.5.2's `[D-P12-34]` initialization rule (former l. 839) and catalog-replacement transition
row (former l. 887), §4.5.3's new-catalog tail (former ll. 925–927), the §5.1
`OptionEditSession` row (former l. 1421, which no longer implies a P7 read route and names
the one-way drain freeze) and §5.3(E) (former ll. 1632–1633). Initialization never uses
inferProfile and never needs a P7 read-back; P7's published §5 contracts are untouched and
the one-directional selection freeze is unchanged. Recorded as `D-P12-39` in §11.1 and
announced in the §0 header amendment history; no §8 case referenced this route, so none
required synchronization, and no schema or identity format changed.

The frozen report body, verdict and notes above are unchanged. This resolution records
architecture edits only: no validation, tests, builds, formatters, linters or runtime/UI
execution was performed. §5 rows changed textually; fresh whole-owner review remains
required, and no implementation readiness, PASS or optional ModularUI adoption is claimed.
