# Phase 12 — Whole-owner architecture review, Round 7

**Frozen set:** Architecture Review Attempt 6  
**Owner:** `docs/phase12/v1/PHASE_12_DOC.md`  
**Frozen owner SHA256:** `fa3a2dd366620006c880d35c2ecca48b8be6d7243fcc879487dda46e3a6a0e0e`  
**Inventory:** `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_6.json`, Phase 12 entry, reviewRound 7  
**Required corrections:** 1 · **Notes:** 1 · **§5 impact:** yes

## Scope and authority

This is a fresh whole-owner review of all sections 0–12, not a confirmation of previous fixes. I inspected the owner diff, read the complete current owner, and independently traced its load-bearing dependencies and receiving boundaries. The supplied hash and frozen inventory identify the reviewed owner; no checksum/validation command was run. No repository file was edited.

The selected governing design is **`docs/design/v3/DESIGN.md`**, as expressly selected by the Phase 12 header, not a global-newest revision. I read its Part I and Phase 12 specification (`:2357–2432`), plus the adjacent Phase 13 scope for companion-control ownership. Governing research reads covered `docs/research/v1/RESEARCH.md` §§0–1, 4.7–4.8, 7.6, 9, the OQ-9 row, Appendix E.2, Appendix F and the glossary. `docs/MOVES.md` was consulted for historical coordinates.

Dependency review covered Phase 1 §5 and incorporated GUI package/diagnostic/dependency obligations; Phase 3 §5 and incorporated option, catalog, locale, persistence and program-evaluation declarations; Phase 4 §5 generation semantics; Phase 7's reload, programmatic/Internal acceptance and diagnostic receiving dispatch; Phase 11's direct diagnostic projection; and Phase 2's reciprocal option-adapter contract. Targeted receiving checks also covered the Phase 5/8 sizing, Phase 9 hand-light, Phase 10 lighting/bake and Phase 13 companion-preference boundaries. These reads do **not** certify those siblings.

The owner explicitly makes §§0.1–0.7 historical at `docs/phase12/v1/PHASE_12_DOC.md:11–18`. Consequently, old dependency verdicts and numeric schema receipts were not treated as current admission rules or reopened as defects merely for remaining in the history.

## Independent whole-document checks

| Owner area | Review result |
|---|---|
| §§0–1: authority, scope and ownership | Header-selected v3 is explicit; GUI/persistence timing is separated from Phase 3 parsing/codecs and Phase 7 execution. No rendering or binary-acquisition authority is inferred. |
| §§2–3: architecture and conformance map | Engine/view separation and App F.3/F.4 coverage are substantive. The newly required independent profile-intent display has an incomplete final view handoff, identified in C1 below. |
| §§4.1–4.4: model, locale and widgets | Exact-current schema admission, catalog-issued previews, source-order star expansion, all-slot column floors, explicit-empty locale suppression and discrete allowed-value transitions agree with the current Phase 3 contracts. |
| §4.5: profiles and edit sessions | Independent pending/committed selection, option-only inference, atomic valid batches, intent-aware dirty/count, reset/discard, and acceptance-versus-render-failure semantics are defined. The program-only action reaches Phase 7; its promised visible summary does not yet have a specified adapter input. |
| §§4.6–4.7: settings, persistence and reloads | Seven GUI controls and eight canonical programmatic/storage keys are distinguished. Safe target/catalog pairing, changed-only pack writes, all-entry globals, closed resolution outcomes, partial-write reporting and max/OR reload effects are specified. |
| §§4.8–4.10: platform, errors and adapters | Client-command and selective-listener signatures were independently corroborated via the Cleanroom API tools. Resource callbacks are notifications inside Phase 7's destructive-replacement gate, not substitutes for that gate. Separate P1/P11 diagnostic inputs reach both adapters. |
| §5: cross-owner interfaces | Current schema23 and payload-free profile selector consumption are explicit. P7 receives intent-only commits, preserves accepted selection separately from inference, and executes the effect matrix. C1 changes the P12 presentation/view contract and therefore affects §5. |
| §§6–7: failures, threading and performance | Preacceptance failures preserve pending state and queue nothing; postacceptance failure is reported as failed/compensated Off rather than active prior-pipeline reuse. Presentation identity and actual P4 generations are distinct. |
| §§8–9: proof plan and staging | Planned cases cover equal-option/different-disabled-program profiles, zero-only AA admission, persistence failures, Internal lifetime, locale boundaries, source-less diagnostics and compensation. These are plans, not executed evidence. Milestone and availability gates remain explicit. |
| §§10–12: OQ, decisions and implementation gate | OQ-9 includes a procedure, criteria and vanilla fallback. Historical source/authority discrepancies and optional ladders remain openly recorded. The implementation checklist retains fresh-review and implementation gates rather than claiming runtime clearance. |

### Focused producer-to-receiver traces

1. **Equal option values, different disabled programs.** `docs/phase12/v1/PHASE_12_DOC.md:758–817` separates the cycling cursor and accepted explicit selection from `inferProfile`. Its dirty/count and Apply/Done/Reset rules cannot discard an intent-only change merely because the option delta is empty. `docs/phase7/v1/PHASE_7_DOC.md:3180–3198` and `:3334–3357` explicitly receive this selection through the existing committed-batch route, including Internal value-domain UNCHANGED, reset, coalescing and accepted-then-Off retention. Phase 3 evaluates only the new configuration's finalized options and chosen profile (`docs/phase3/v1/PHASE_3_DOC.md:2963–2985`); absence means no disables, while final enablement includes the selected profile's disables. This is an architectural execution path, not a claim that a shader was actually disabled in a running client.
2. **Eight-key programmatic domain versus seven controls.** P12's GUI entry/intents contract is at `:865–925`; the reserved programmatic key is expressly admitted at `:1449–1454`. Phase 3's authoritative known-key validity and default/macro rules are at `docs/phase3/v1/PHASE_3_DOC.md:3449–3514`. Phase 7 receives the canonical inventory at `docs/phase7/v1/PHASE_7_DOC.md:3365–3374`; Phase 2 explicitly receives zero-only unchanged admission and the profile scenario at `docs/phase2/v2/PHASE_2_DOC.md:2769–2777`. No AA GUI entry, nonzero token, runtime effect or FXAA macro is granted.
3. **Schema23.** P12's current consumption and receipt (`:1312–1334`) agree with Phase 3's exact containing/nested/inspection equality and MaterializedSource-v23 contract (`docs/phase3/v1/PHASE_3_DOC.md:4140–4219`). Earlier numeric receipts are expressly historical. P12 neither expands selector ranges nor resolves registries; the nine source-free trees and projectionVersion=1 are not an old-schema compatibility escape.
4. **Reloads and diagnostics.** P12's NONE/REPUBLISH/FULL matrix, independent flags and one-final-outcome rule (`:1000–1090`) reach the explicit Phase 7 dispatch at `docs/phase7/v1/PHASE_7_DOC.md:3203–3241`. Actual P4 generation events remain independent of configuration counts (`docs/phase4/v1/PHASE_4_DOC.md:2115–2119`). P11 source-less and empty-entry snapshots have an explicit direct route through P7 and P12, consistent with `docs/phase11/v1/PHASE_11_DOC.md:1251–1258` and P12 `:1175–1195`, `:1430–1441`.

## Required correction

### C1 — Publish the independent pending-profile summary through the closed options-view seam

**Severity:** correction / P2.  
**Owner anchor:** `docs/phase12/v1/PHASE_12_DOC.md:773–775` (D-P12-34 amendment).  
**§5 impact:** yes.

The owner promises that when the inferred profile remains A while explicit B is pending, “the unsaved summary identifies the separate profile selection change.” The closed options-view contract does not specify a carrier or rendering route for that summary. `OptionPresentationModel` exposes only screens and diagnostics (`:274–277`); `ProfileCycle` exposes the inferred current value, its existing label and tooltip (`:294–295`, `:742–746`); and `OptionEditSession` exposes only a Boolean dirty flag and aggregate count, with no pending-selection or prepared-summary accessor (`:305–316`). `showOptions` receives only those objects (`:390`). `lastActionSummary` belongs to the pack-selection model, not the options model (`:359–363`). Neither the label/tooltip rules nor the diagnostics rules designate an independent intent-summary payload. Section 4.10.1 simultaneously declares this to be the entire seam and forbids view-owned contract decisions (`:1201–1210`).

**Observable breakage:** with profiles A and B both requiring FOO=true but only B disabling gbuffers_water, clicking B leaves all displayed option values and the inferred profile A unchanged. The adapter can discover “one pending change” but cannot identify that it is explicit B rather than an option edit, or distinguish B from another equal-option profile C. The backend can therefore accept and execute B correctly while the final GUI cannot provide the specific pending-intent visibility that D-P12-34 requires. This is a missing producer-to-view contract, not a request for runtime evidence and not a defect attributed to Phase 7.

**Minimal owner fix:** define a presenter-produced immutable optional pending-profile summary on the existing options presentation/session surface, or explicitly designate an existing presentation field with complete summary semantics. It must distinguish selection change from option count and identify the pending profile or cleared selection without replacing the owner-inferred profile label. Specify its recomputation/clearing on cycle, ordinary edit, invalid batch, discard, reset, failed acceptance, successful acceptance and catalog replacement, and require both vanilla and ModularUI adapters to render it. Update the corresponding §§2/4/5/8/12 contract references together. Do not add a field to Phase 3's payload-free `ScreenProfileEntry`, infer explicit intent in the view, or add a programmatic profile-map key. The appropriate observable check is that the same-options A/B/C scenario produces distinguishable presenter output received by both adapters, while inference may remain A.

## Note

### N1 — Preserve the historical source-pin limitation separately from available corroboration

**Severity:** note; no required owner correction and no additional §5 change.

The historical primary paths in `docs/phase12/v1/PHASE_12_DOC.md:27–28` name `reference-src/pintonium-9c2fcc1/...`; that pinned checkout is absent. `docs/MOVES.md` explains the old alias but does not establish that the available `reference-src/Pintonium-main/` is the same revision. I read both available GUI files completely. Their options screen independently corroborates queue-based edits, profile option batching, root discard, the 1–3 column clamp, label fallback/prettification, and the absence of a slider/hover-tooltip implementation. However, their current apply and selection methods call `CeleritasShaderVersionService.INSTANCE.reload()` rather than independently proving the historical direct renderer-reload quotations. These reads are corroboration, not restoration of the missing pinned evidence.

Likewise, current MCP client-command, key registration/event and selective-listener results corroborate the platform surface, but the historical ModularUI example ID 1057 currently resolves to an animation-direction example. Broadened ModularUI searches recover current lifecycle examples, not the original GUI-wrapper receipt. The retained OQ-9 spike/fallback remains necessary; no live ModularUI fitness or historical example identity is certified.

Permitted shipped author documentation was also read directly: `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:126–217`, `:218–326` and `doc/shaders.txt:653–661` corroborate option/profile/screen vocabulary, tooltip markers, disabled programs, widening and the macro descriptions. No OptiFine decompiled implementation was read. Research's confidence tags and expressly local policy/default choices remain distinct from these published facts.

## Limitations and disposition

No builds, tests, formatters, linters, client launches, runtime experiments or pack-tier checks were performed. All owner bytes were left untouched. No forbidden chatlogs/transcripts, Oculus pipeline/transform or blocked library trees, or glsl-transformer implementation were read. Optional UI ladders and other ungranted APIs remain ungranted. Historical reviews were not used as proof of current correctness, and this report issues no sibling verdict.

**Final verdict: PASS-WITH-CORRECTIONS.** The whole-owner architecture is coherent enough for a local fix rather than structural rebuilding, but C1 is required to complete its newly promised GUI behavior. Because that fix changes the presentation/view contract incorporated in §5, a fresh whole-owner review is required after the owner amendment. Final cross-phase integration, implementation clearance and runtime/conformance certification remain separate.

## Resolutions

### C1 — Resolved in owner architecture, 2026-09-08

**Decision:** `D-P12-37`; **§5 changed:** yes. The frozen report above remains historical
and unchanged; this resolution does not convert its verdict into fresh certification.

The owner now declares `PendingProfileSummary(Optional<ProfileName> selection, String displayText)`
and non-null `OptionPresentationModel.pendingProfileSummary: Optional<PendingProfileSummary>`
in §2.2. `OptionEditSession.present(ScreenId)` produces it and the existing
`OptionScreenView.showOptions(OptionPresentationModel, ScreenId, OptionEditSession)` delivers
it to vanilla and conditional ModularUI. Outer absence means no pending selection change;
present/inner-named identifies the exact pending profile; present/inner-empty identifies
cleared intent. Both views render presenter-prepared text separately from aggregate count
and the unchanged inferred `ProfileCycle` label, without reconstructing selection.

§4.5.2 specifies cycle, ordinary edit, invalid batch, discard, reset, failed/successful
acceptance, catalog replacement and locale-change transitions. It preserves per-key locale
fallback and explicit-empty suppression. §§4.10/5.1 bind the final rendering route; §8
plans actual A/B/C view-input, lifetime and locale cases; §12 incorporates the same work.
P3 schema23, payload-free `ScreenProfileEntry`, nine inspection trees, P7 acceptance
transport and programmatic keys remain unchanged. No optional UI adoption is granted.

No validation, implementation, builds, tests, formatters, linters, runtime experiments or
checksum/structure checks were performed. N1's historical source-pin limitation remains
unchanged. A fresh whole-owner review is still required after this §5 amendment; Main owns
consolidated cross-owner checks and readiness.
