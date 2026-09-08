# Phase 12 — Whole-owner architecture review R8

**Owner:** `docs/phase12/v1/PHASE_12_DOC.md`  
**Frozen round:** attempt7 / R8  
**Frozen SHA256:** `dc10c90e702e2a3a1699b53086990284b010ed80f9a8c8f4fa06d2f05bd51c9f`  
**Identity basis:** supplied inventory identity, also recorded at `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_7.json:72–75`; no checksum command was run.

## Scope and authority

Independently read the complete current owner, §§0–12, including the historical header records, all active detailed contracts, the entire §5, failure handling, threading, testability, milestone/OQ gates, decisions and implementation checklist. This was not a review limited to D-P12-37 or the recent amendments.

The owner's header selects `docs/design/v3/DESIGN.md:6` by reference from `docs/phase12/v1/PHASE_12_DOC.md:6–8`; the governing material reviewed was DESIGN v3 Part I (§§G0–G12) and its Phase 12 specification at `docs/design/v3/DESIGN.md:2357–2432`, with the adjacent Phase 13 scope used only for the named texture-setting boundary. RESEARCH remains superior contract authority: `docs/research/v1/RESEARCH.md:11–105`, `601–650`, `873–878`, `940–955`, and Appendix F at `1437–1528`, particularly F.3/F.4. `docs/MOVES.md` was used to resolve the versioned authorities and reference aliases; its global adoption summary was not substituted for this owner's explicit header selection.

Dependency checks used current published contracts and their incorporated definitions, not historical PASS quotations: Phase 1 §5 plus package, seam, diagnostic and dependency/licensing definitions; Phase 3 §5 and its incorporated option/catalog, locale, persistence, discovery and program-evaluation definitions; Phase 7's actual reload, acceptance and resource-replacement branches; Phase 4's actual selected-profile evaluation and generation rules; and Phase 11's exact direct diagnostic projection. Narrow receiving-side checks additionally covered Phase 2's option adapter and the settings paths in Phases 5, 8, 9, 10 and 13. These reads establish Phase 12 boundary compatibility only, not sibling-owner certification.

## Independent checks

1. **Pending-profile summary reaches both actual view contracts.** `OptionPresentationModel` contains the optional immutable identity/text record at `docs/phase12/v1/PHASE_12_DOC.md:278–286`. The producer at `789–827` specifies presence iff explicit pending and committed identities differ, distinguishes named from cleared intent, retains equal/empty localized names without inventing fallbacks, and requires refresh even when option values and inference do not change. The existing `showOptions` receiving method is at `397–403`; the vanilla and conditional ModularUI obligations at `1248–1264` and `1284–1290` require the prepared status line independently of profile-row presence and aggregate count. §5 explicitly incorporates that contract at `1348`. Discard, failed Apply/reset, acceptance, catalog replacement, locale change and accepted-then-Off are addressed. No correction was found in this newly published summary route.
2. **Inference does not replace explicit program-disable intent.** P3's catalog-issued preview and payload-free selector are published at `docs/phase3/v1/PHASE_3_DOC.md:970–1041`; its inference and runtime evaluation are separate at `2913–2925` and `2964–2979`. P12's independent baselines, cursor, count and acceptance rules at `758–867` preserve that separation. Importantly, the receiving path does not stop at a receipt: P7 forwards the accepted `Optional<ProfileName>` into every P4 build at `docs/phase7/v1/PHASE_7_DOC.md:3213–3227`, and P4 actually evaluates that exact configuration/selection pair before availability or GL at `docs/phase4/v1/PHASE_4_DOC.md:1333–1366`. Equal-option profiles therefore have an explicit program-state consumer rather than a discarded side calculation.
3. **Persistence and settings use the owner codecs.** P12's seven GUI controls and eight storage/programmatic keys at `docs/phase12/v1/PHASE_12_DOC.md:910–1018` and `1497–1530` agree with P3's canonical domains, last-valid-duplicate handling, complete global writes and absent defaults at `docs/phase3/v1/PHASE_3_DOC.md:3457–3537`. Per-pack writes authenticate the exact catalog/target pairing and filter against defaults; global writes do not use changed-only filtering. The durable `shaderPack` reference uses fresh discovery and the closed resolver outcomes instead of display names or old candidate IDs. Filesystem partial-write receipts and Internal session acceptance remain distinct from final rendering success. P7's actual branches at `docs/phase7/v1/PHASE_7_DOC.md:3321–3385` preserve the failed-write/no-queue and accepted-then-Off distinctions. Phase 2's receiver at `docs/phase2/v2/PHASE_2_DOC.md:2777–2807` admits exact reserved AA zero without inventing an AA effect or a programmatic profile field.
4. **Settings reach their behavior owners without a second parser.** P13 computes independent preliminary normal/specular values before P3 load at `docs/phase13/v1/PHASE_13_DOC.md:679–687` and `779–788`; P7 performs that adaptation at `docs/phase7/v1/PHASE_7_DOC.md:853–859`. Buffer quality uses decoded canonical multipliers at `docs/phase5/v1/PHASE_5_DOC.md:2805–2810`; P8 consumes the resulting allocation rather than multiplying again at `docs/phase8/v1/PHASE_8_DOC.md:714–722`. P9's old-hand-light branch at `docs/phase9/v1/PHASE_9_DOC.md:701–718` and P10's lighting/AO branch at `docs/phase10/v1/PHASE_10_DOC.md:1295–1329` preserve typed user-over-pack precedence and effective bake invalidation. P12 correctly keeps unpublished choice ladders inert without rewriting decoded values. These are architecture observations, not parity or milestone-completion claims.
5. **Reload effects survive dispatch and compensation.** P12's matrix and max/OR algebra at `docs/phase12/v1/PHASE_12_DOC.md:1048–1140` have corresponding actual P7 branches at `docs/phase7/v1/PHASE_7_DOC.md:3200–3270`. Resource NONE retains the exact configuration and is coordinated with the pre-destructive gate at `1458–1500`, rather than relying on a post-replacement callback to protect old resources. Generation delivery at `3305–3319` agrees with P4's independent publication-count contract at `docs/phase4/v1/PHASE_4_DOC.md:2162–2166`; one final outcome is not misrepresented as one generation bump.
6. **Diagnostics retain producer identity and receiving branches.** P1's actual channel fan-out is at `docs/phase1/v14/PHASE_1_DOC.md:4906–4947`. P11's source-free projection and selected-pack lifetime are at `docs/phase11/v1/PHASE_11_DOC.md:882–923`; P7's final-outcome projection call is at `docs/phase7/v1/PHASE_7_DOC.md:3402–3417`. P12's `showErrors` contract and both views preserve the separate P1/P11 producers, source-less entries and empty-entry final snapshots at `docs/phase12/v1/PHASE_12_DOC.md:1225–1244`, `1255–1260`, `1281–1282` and `1318–1322`. No channel conversion or inference of active success is needed.
7. **Whole-owner doc gate.** The conformance map and detailed design cover F.3/F.4 constructs, deferred star expansion, retained empty slots, configured column floors, opaque discrete values, tooltip warning markers and per-key empty-preserving localization. The fallback and OQ-9 procedure remain architected rather than declared executed. Schema23 is an exact admission gate; historical schema receipts and dependency-review history are explicitly superseded for current consumption. One remaining presentation-availability defect is detailed below.

## Substantive correction

### C1 — Publish profile and reset availability through the closed options seam

**Severity:** correction (P2).  
**Owner locations:** `docs/phase12/v1/PHASE_12_DOC.md:303–325`, `738–745`, `766–769`, `1248–1258`, `1284–1289`, and the exposed contracts at `1346–1348`/`1354`.

**Defect and trigger:** The owner requires missing `InternalOptionCommitter` to disable Internal pack mutation with a reason, including the profile/reset operations named immediately above that requirement. However, only ordinary option entries carry `interactive`; `ProfileCycle` carries just label, inferred current selection and tooltip, while `OptionEditSession` exposes no mutation-availability query or reset-availability value. `OptionPresentationModel` likewise carries no such state. The actual vanilla receiver always includes Reset and derives Apply enablement only from dirty state; the closed seam forbids adapters from adding their own model extension. A program-only Internal profile can have no ordinary option widgets from which even an accidental enablement inference could be made. Consequently the supplied view inputs do not express the required profile/reset disabled state: a thin adapter must either offer controls that should be inert or obtain/invent availability outside the published contract.

There is a second concrete boundary served by the same missing profile applicability value: P3 deliberately retains `ScreenProfileEntry()` when there are no profile definitions (`docs/phase3/v1/PHASE_3_DOC.md:2913–2919`). P12 always maps it to `ProfileCycle`, whose empty inferred selection is also the normal Custom state, then specifies selecting the first declared profile. It provides neither an empty-definition action branch nor a presenter-owned applicability indication to distinguish that case from actionable Custom.

**Minimal owner fix:** Publish presenter-owned profile applicability and options-session mutation/reset availability, including a prepared disabled reason, through the existing model/session seam. Define the no-profile and missing-committer action branches to preserve preview/intent and perform no persistence or reload; recheck availability when handling callbacks. Require both adapters to render those supplied states, keep Back/discard and global settings usable, and preserve the existing pending-summary semantics. Update the incorporated §5 rows and the focused headless/view acceptance cases. This requires no P3 selector payload, programmatic profile key, new persistence format or P7 runtime interface.

**§5 impact:** yes — the exposed options presentation/session contract must change. This is a localized correction, not a structural rebuild.

## Note

### N1 — Historical reference pins were not reproducible as current source evidence

**Severity:** note.  
**Owner locations:** `docs/phase12/v1/PHASE_12_DOC.md:24–35`, `152–163`, `493–521`, `1299–1336`.

The two exact `reference-src/pintonium-9c2fcc1/.../gui/` paths cited by the owner are absent in this checkout. A bounded filename search located both GUI files under `reference-src/Pintonium-main/forge122/src/shaders/java/net/irisshaders/iris/gui/`, and both were read completely as current corroboration only. That options file corroborates the 1–3 column clamp at line 71, the closed widget dispatch at `199–231`, and per-key translation/prettification at `320–366`; its reload method at `248–254` and the selection file's reload at `190` also demonstrate why current source cannot silently certify the historical line pins. The current tree contains GPL text in `LICENSE` and LGPL supplementary text in `COPYING.LESSER:1–35`; the read GUI files have no file-level license notice. This supports only qualified repository-level provenance, not an exact pinned-file reuse clearance or dependency-license audit.

The current MCP `ModularUI`/`gui` lookup returned no matching example; broader lookup found ModularUI records, but current example 1057 is an animation-direction method, not the historical GUI evidence associated with that ID. Current API lookups did independently corroborate ClientCommandHandler, KeyInputEvent and ISelectiveResourceReloadListener signatures. None of these observations resolves OQ-9 or re-dates the owner's historical MCP/source claims. Keep those claims historical and require exact source/version/license evidence before implementation reuse; no architecture correction is ordered for the unavailable historical pins.

## Limitations and verdict

No files were edited and no report file was written. No formatter, linter, build, test, checksum or validation command was run. Forbidden transcripts, Oculus transformation boundaries, glsl-transformer implementation and OptiFine decompiled implementation were not read. Proposed test cases are architecture obligations, not executed proof. Source-license confidence remains qualified as above. This verdict does not certify implementation, runtime behavior, sibling owners, global integration readiness, optional ModularUI adoption or any historical dependency PASS.

**Correction count: 1. Note count: 1. Section 5 impact: yes.**

The new pending-profile summary route is complete at the architecture boundary, and the main persistence/reload/diagnostic contracts have explicit receivers. The remaining non-option availability gap is discrete and fixable within Phase 12; it does not justify structural FAIL.

**PASS-WITH-CORRECTIONS**

## Resolutions

### C1 — Applied in architecture (2026-09-08), D-P12-38

`docs/phase12/v1/PHASE_12_DOC.md` now publishes `OptionActionAvailability` and
`OptionSessionAvailability` through `OptionPresentationModel`, and explicit applicability/action
availability on `ProfileCycle`. §§4.4.4/4.5 distinguish empty profile definitions from Custom,
make missing Internal committer a session-wide pack-mutation/reset gate even for program-only
profiles, and require callback-time preflight before preview/intent mutation, persistence,
capture or reload. Disabled reset preserves the original desired state; admitted reset failure
retains its reset preview under the existing acceptance law.

Both actual view contracts consume supplied enabled flags and prepared localized reasons,
including status with no ordinary widgets. Availability refresh and later installation do not
replay actions or change pending summary; Back/discard/navigation and independent global
settings remain usable. Public/incorporated §5 contracts, §8 headless/actual-view cases and
§12 checklist carry the same obligations. No P3 selector payload, programmatic profile key,
persistence format or P7 runtime interface was added.

The frozen report body, verdict and N1 source-evidence limitations above are unchanged.
This resolution records architecture edits only: no validation, tests, builds, formatters,
linters or runtime/UI execution was performed. §5 changed; fresh whole-owner/receiver review
remains required, and no implementation readiness, PASS or optional ModularUI adoption is claimed.
