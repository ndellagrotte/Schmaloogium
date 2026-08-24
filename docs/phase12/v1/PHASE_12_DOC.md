# Schmaloogium — Phase 12: Options GUI, persistence & reload — Architecture

## 0. Header

**Phase:** 12 — Options GUI, persistence & reload · **Milestone:** v0.4 · **OQs:** OQ-9
**Governing design:** `docs/design/v3/DESIGN.md` (v3, unadopted overall per §G0.4; this build
session was assigned v3 and its Part II Phase 12 spec at ll. 2357–2432).
**Date:** 2026-08-09 · **Session type:** build (§G1.1) — **re-run** against
`docs/phase12/reviews/PHASE_12_REVIEW_1.md`'s `FAIL`, per §G1.3 (see §0.4 item 5).

### 0.1 Inputs actually read

| Input | Extent |
|---|---|
| `docs/design/v3/DESIGN.md` | All of Part I (§G0–§G12, ll. 125–1136) and the Phase 12 spec (ll. 2357–2434). Additionally the Phase 4 reload-safety row (ll. 1560–1563) and the Phase 7 frame-driver/hook spec (ll. 1832–1985), both under the §G5.3 item 3 exception below |
| `docs/research/v1/RESEARCH.md` | §0 (ll. 11–52), §1 (ll. 55–105), §4.7 (ll. 601–618), §4.8 (ll. 620–650), §7.6 (ll. 873–878), §9 (ll. 940–955), §11 OQ-9 row (l. 1015), App E.2 (ll. 1420–1433), App F.1–F.8 (ll. 1437–1528), App H (ll. 1554–1587) |
| `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` | §14 (ll. 707–728), plus the §7.4 slider statement and §17/§18 rows cited in §3.4 |
| `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/gui/VintageShaderPackOptionsScreen.java` | whole file (409 lines), under §G11.2/§G11.4 |
| `reference-src/pintonium-9c2fcc1/forge122/src/shaders/java/net/irisshaders/iris/gui/VintageShaderPackSelectionScreen.java` | whole file (322 lines), under §G11.2/§G11.4 |
| MCP `cleanroom` | `search_cleanroom_api` for `ClientCommandHandler`, `ClientRegistry#registerKeyBinding`, `ISelectiveResourceReloadListener`, `InputEvent.KeyInputEvent`; `get_api_class(net.minecraftforge.client.ClientCommandHandler)`; `search_mod_examples(query="ModularUI", category=gui)` and follow-ups; `get_mod_example(1057)` |
| `docs/phase12/reviews/PHASE_12_REVIEW_1.md` | Whole (ll. 1–293). Added to this session's Required inputs by §G1.3's FAIL rule (DESIGN.md ll. 352–353); see §0.4 item 5 |
| Verification ledgers (verdict blocks and closing status only, read to establish dependency state under §G1.3 — no design content taken) | `docs/phase1/reviews/PHASE_1_REVIEW_25.md` §3 (ll. 59–63); `docs/phase3/reviews/PHASE_3_REVIEW_34.md` §3 (ll. 44–48), `PHASE_3_REVIEW_35.md` §3 (ll. 275–279), `PHASE_3_REVIEW_36.md` §3 and `## Resolutions` (ll. 268–352); `docs/phase7/reviews/PHASE_7_REVIEW_36.md` §3 (ll. 105–123); `docs/phase3/v1/PHASE_3_DOC.md` closing status (ll. 2119–2121) and `docs/phase7/v1/PHASE_7_DOC.md` closing status (ll. 2502–2506) |

### 0.2 Dependency PHASE docs consumed

| Doc | Verified state at read time | Extent read |
|---|---|---|
| `docs/phase1/v14/PHASE_1_DOC.md` | **verified** — `docs/phase1/reviews/PHASE_1_REVIEW_25.md` l. 61 literal `PASS`, l. 63 `Interface changed: no`; round 25 is the latest Phase 1 round | §2.1 package tables (ll. 1527–1563), §4.9.4 diagnostics (ll. 3776–3797), §4.2.6/§4.8.4 dependency mechanics as reached from §5.3, §5 whole (ll. 4175–4283), §10.2 OQ-12 (ll. 4580–4610), §11.3 item 2 and §11.4's "To Phase 12" block (ll. 5167–5171), §9 row for `SHADER_GUI` (l. 4513) |
| `docs/phase3/v1/PHASE_3_DOC.md` | **NOT verified** — latest round is `docs/phase3/reviews/PHASE_3_REVIEW_36.md` l. 270 `PASS-WITH-CORRECTIONS`, l. 272 `Interface changed: yes`, with a §5 change outstanding. Ledger below | §2.2 public shape (ll. 335–641), §3.1 flag-ownership map (ll. 688–718), §3.2 rows for sliders/profiles/screens (ll. 740–745), §4.3 (ll. 907–955), §4.8 (ll. 1203–1300), §5 whole (ll. 1420–1689), §6 (ll. 1690–1706), §7 (ll. 1707–1729), §11.4 (ll. 2026–2046), §11.5 (ll. 2048–2053) |

**Phase 3 is a hard dependency (DESIGN.md §G5.1 l. 624), it is not verified, and this session
consumed it anyway.** That is recorded here as a breach, not argued away.

**The ledger, re-resolved this session.**

| Round | Verdict | `Interface changed` | Standing |
|---|---|---|---|
| 34 — `PHASE_3_REVIEW_34.md` | l. 46 literal `PASS` | l. 48 `no` | superseded |
| 35 — `PHASE_3_REVIEW_35.md` | l. 277 `FAIL` | l. 279 `yes` | superseded |
| 36 — `PHASE_3_REVIEW_36.md` | l. 270 `PASS-WITH-CORRECTIONS` | l. 272 `yes` | **latest** |

Round 36 admitted six corrections and records all six as applied (`## Resolutions`, ll. 294–346),
but its own verification trigger (ll. 348–352, repeating ll. 287–292) states that because every
resolution changes the monitored §5 region, *"a fresh whole-document verification round is required
before Phase 3 can close or be consumed by a dependent."* Under §G1.3 (DESIGN.md ll. 354–359) a
phase is verified when its latest verdict is `PASS`, or `PASS-WITH-CORRECTIONS` with resolutions
recorded **and no §5 change outstanding**. Round 36 leaves a §5 change outstanding, so Phase 3 is
**not verified**. Phase 3's own closing ledger says the same (`PHASE_3_DOC.md` ll. 2119–2121);
doc and reviews agree, and so does this document.

**The §G5.3 consequence, stated plainly.** §G5.3 item 1 (DESIGN.md ll. 659–663) makes verified
dependency status a gating invariant binding on the consuming build session: only verified docs are
valid dependency inputs. The single exception §G5.3 grants is item 3 (ll. 668–671), and it covers
the Phase 7 **soft** dependency alone. Nothing in §G5.3 sanctions consuming an unverified **hard**
dependency, and this document claims no such sanction. Therefore:

1. Every row of §5.2 is a **provisional** consumption. The rows marked **⟳** there sit on text
   round 36's corrections rewrote and owe re-derivation the moment Phase 3 obtains a fresh literal
   `PASS`.
2. Phase 12's own §5 cannot be certified until Phase 3's §5 settles, because the presentation model
   in §5.1 is derived wholesale from Phase 3's §5.1 option surface.
3. This document therefore owes a §G1.3 pass after that `PASS` — a fix-up if the re-derivation
   confirms every consumed semantic, a rebuild if the option-model surface has moved (§11.4). No
   §12 item may start before it (§12 gate).

**Where round 36's six corrections reach into this document.**

| Round-36 resolution | Reaches |
|---|---|
| candidate-003 — `ColorAttachmentRequirement.clearColorOverride`; **the configuration schema is now 4** (`PHASE_3_REVIEW_36.md` l. 304) | §5.2's `PackConfiguration` row and the I-3 schema gate (§4.1, §4.2); the `ResourceRequirements` aggregate |
| candidate-004 — Phase 3 no longer consumes Phase 1's `:conformance` extension | §8.2 only; Phase 2 still owns the harness, so nothing here moves |
| candidate-006 — §2.2's public declarations are incorporated into their §5.1 rows (P3 §5.1 ll. 1446–1452) | §5.2's `PackFrontEnd.discover`, `PackFrontEnd`/`PackLoadRequest`/`PackLoadResult` and `EngineOptionData` rows; it does **not** cover the `OptionConfiguration` row, so §5.4 request 1(a) stands |
| candidate-008 — recognized terminal filter/wrap property-key suffixes stripped and ignored | nothing consumed here (Phase 13's surface) |
| candidate-009 — an active `gdepth` mandates `colortex1 RGBA32F` | the `ResourceRequirements` aggregate only |
| candidate-010 — `DrawRouting` is `AllUsed \| Explicit` | the `ResourceRequirements` aggregate only |

### 0.3 Phase 7 — the sanctioned soft-dependency exception

Phase 12's dependency on Phase 7 is **soft** (DESIGN.md §G5.1 l. 624, ll. 630–632): it needs only
Phase 7's reload-lifecycle section. `PHASE_7_DOC.md` is **verified** —
`docs/phase7/reviews/PHASE_7_REVIEW_36.md` l. 107 literal `PASS`, l. 109 `Interface changed: no`,
l. 121 *"Next required action: none for Phase 7"* — and round 36 is the latest Phase 7 round,
superseding round 32's `PASS-WITH-CORRECTIONS` (`PHASE_7_REVIEW_32.md` l. 299, `Interface changed:
yes` at l. 301) that the first build round of this document cited. Phase 7's own closing note
(`PHASE_7_DOC.md` ll. 2502–2506) has not caught up; §G1.3 defines the state by the reviews.

`PHASE_7_DOC.md` is nonetheless **not a Required input** for Phase 12 — the spec's list
(ll. 2409–2417) names `PHASE_1_DOC.md` and `PHASE_3_DOC.md` only — and §G1.1 forbids reading beyond
the assigned list absent a genuine gap. This session therefore takes **§G5.3 item 3, the one
sanctioned exception** (DESIGN.md ll. 668–671), on its own terms: it designs against Phase 7's
*specification in DESIGN.md* (ll. 1850–1886), exactly as the Phase 12 spec instructs
(ll. 2387–2388), does **not** read `PHASE_7_DOC.md`, and flags every consumed assumption in §5.3(B).

Because Phase 7 is now verified, that flag is immediately actionable rather than hypothetical: a
§G1.3 fix-up session can check §5.3(B)'s four assumptions against Phase 7's published §5 today. If
one is contradicted, this doc takes a fix-up, not a rebuild (§11.4).

### 0.4 Deviations from the assigned reading list, with reasons

1. **Phase 4's and Phase 7's specs in DESIGN.md** were read beyond the Phase 12 spec. §G1.1 forbids
   reading "other phases' specs beyond their titles in G5" absent a genuine gap. The gap is
   assigned: the Phase 12 spec itself directs consumption of "Phase 4's version-counter
   invalidation (PD §3.1 analog) as the reload-safety mechanism" (l. 2389) and of "the Phase 7
   lifecycle … design against Phase 7's spec here" (ll. 2387–2388). Both reads are exactly the
   text those instructions point at, and nothing else from either spec is used.
2. **`OCULUS_DESIGN.md` was NOT read.** §G11.5 l. 978 records sliders as "Answered by gated
   Oculus evidence", but OD is absent from Phase 12's *Required inputs* (ll. 2409–2417), the Phase
   12 doc gate still states sliders have **no** reference (ll. 2426–2427), and §G12.6 ll. 1113–1116
   states its per-phase map "does not amend any phase's current Required inputs" and applies only
   to "a brief that explicitly assigns OD". No such assignment exists. Sliders are therefore
   designed from RESEARCH App F.3 alone (§4.4.3), and the contradiction is filed as a requested
   upstream change (§11.5 item 1).
3. **Operator-supplied notes.** The session prompt carried a summary of prior exploration
   (dependency-gating states, PD/RESEARCH pointers, engine-setting ownership). It was used as a
   pointer set only; every load-bearing citation in it was re-resolved against the cited file at
   the line before use, and one was corrected in the process — App E.2's "need 11 is pure mod-side
   GUI (no vanilla injection)" is at `RESEARCH.md` l. 1427, not l. 1424. No transcript file was
   read: the §G1.1 forbidden-sources rule (`docs/**/chatlogs/`, root-level `*.txt`) was honored,
   and the untracked root-level `*.txt` files present in this checkout were not opened.
4. **MCP beyond the listed recipe.** The spec lists `search_mod_examples(query="ModularUI",
   category=gui)`. Three further platform lookups were run because the reload paths in
   *Scope — in* name concrete platform mechanisms this doc must not assume:
   `ClientCommandHandler`, `ClientRegistry#registerKeyBinding` /
   `InputEvent.KeyInputEvent`, and `ISelectiveResourceReloadListener`. Results in §4.8.
5. **This is a re-run build session, not a fix-up.** `docs/phase12/reviews/PHASE_12_REVIEW_1.md`
   returned `FAIL` (l. 268), so per §G1.3 (DESIGN.md ll. 352–353) the build session was rerun with
   the review file added to its Required inputs. It was read whole (ll. 1–293) and each of its
   findings F-1…F-8 is applied **in place**: there is no fix-up addendum section, because a rebuild
   states its corrected posture directly rather than appending one. F-1 and F-3 obliged a full
   re-resolution of every Phase 3 locator against the current `docs/phase3/v1/PHASE_3_DOC.md`; that
   was done individually — the offsets are not uniform — and the results are what §0.2, §1.2, §3,
   §4 and §5.2 now cite. The review's §2 "checked and clean" areas were left intact.
6. **Two dependency closing-status paragraphs were read outside the assigned extents.**
   `PHASE_3_DOC.md` ll. 2119–2121 and `PHASE_7_DOC.md` ll. 2502–2506, each a single closing
   paragraph, were read solely to check whether the dependency's own ledger agrees with its latest
   review (§0.2, §0.3). No design content was taken from either, and `PHASE_7_DOC.md` was not
   otherwise opened.

### 0.5 Legal and provenance posture

Pintonium is LGPL-3.0 and both GUI files read here are inside the main tree, so they are readable
**and** reusable with compliance (§G7 item 7, §G11.2 rule 1): notices preserved, modifications
marked, combining into GPL-3.0-or-later. Nothing here traces to
`org.taumc:glsl-transformation-lib` (§G11.2 rule 2) or to vendored `kroppeb/stareval` (rule 3);
neither is reachable from the GUI package. Claims taken from those two files carry
`[V:observed — Pintonium <path>:<line>]`. PD's own §14 summary is treated as a pointer and was
**verified at the source**, which produced one correction to it (§11.3 item 1).

ModularUI is LGPL-3.0 (§G7 item 5). This document records its dependency arrangement in
`[D-P12-14]` and takes on the obligation that arrangement carries (§4.10.3).

### 0.6 Round-2 fix-up

Applied from `docs/phase12/reviews/PHASE_12_REVIEW_2.md` (`PASS-WITH-CORRECTIONS`); reasoning is in
that review's `## Resolutions`.

1. F-1 — §4.7.3's `F3+R keybind` and `/reloadShaders` rows now set `worldRendererReload` = **yes**,
   matching the `FULL` rule stated below the matrix and §8.1's
   `reload_fullAlwaysSetsWorldRendererReload`.
2. F-2 — the persisted pack selection is now bound: a `shaderPack` block in §4.6.3 and a new §5.1
   row (key, tokens, absent-key meaning, durable identity, re-resolution rule). This changes §5.
3. F-3 — §4.5.3's "write-through on change" is re-attributed to `docs/design/v3/DESIGN.md` l. 2381
   (Phase 12 Scope-in), not RESEARCH §4.7 l. 604.

Notes F-4 and F-5 were not ordered and are not applied.

### 0.7 Round-3 fix-up

Applied from `docs/phase12/reviews/PHASE_12_REVIEW_3.md` (`PASS-WITH-CORRECTIONS`); reasoning is in
that review's `## Resolutions`.

1. F-1 — §3.3 row C-15's design-element cell now states truthfully where each §G4.1 term is
   honored: six terms as §2.2 identifiers, `<empty>` as `Blank`, and `*`/`prefix`/`suffix` in the
   §4.3.3–§4.3.5 prose and their App F.3/F.4 rows. No design content changed.

Notes F-2 and F-3 were not ordered and are not applied.

---

## 1. Scope & boundaries

### 1.1 What Phase 12 owns

1. **The presentation model** — a pure, headless, UI-framework-agnostic tree of screens, entries,
   and bindings derived from Phase 3's `OptionConfiguration`, including the `*`-expansion Phase 3
   deferred to us and the post-expansion column resolution.
2. **The view adapters** — a vanilla-`GuiScreen` view (designed in full) and a ModularUI view
   (designed to the same adapter interface, adoption conditional on OQ-9).
3. **Pack-selection UX** — the candidate list, sentinels, status/compatibility display, and the
   seven engine-settings entries with their key names, domains, and defaults.
4. **Edit-session semantics** — pending changes, apply, discard, reset, and *when* the Phase 3
   codecs are invoked.
5. **Reload triggers and their lifecycle classification** — F3+R, `/reloadShaders`,
   resource-manager reload, and every GUI-originated change, each mapped to exactly one lifecycle,
   plus the coalescing rules of the request value handed to Phase 7.
6. **GUI-side error surfacing** — rendering Phase 1's `SHADER_GUI` per-pack diagnostic store.
7. **The OQ-9 spike specification** and the ModularUI dependency-arrangement decision.

### 1.2 Adjacent ownership — explicit anti-sprawl boundaries

| Concern touched here | Owned by |
|---|---|
| Option discovery, same-file confirmation, WCC merge, ambiguity, value lists, source rewriting | **Phase 3** (§4.3, §4.8) |
| Persistence **formats** and both codecs (`OptionPersistenceCodec`, `GlobalShaderOptionsCodec`), ISO-8859-1 escaping, atomic write mechanics | **Phase 3** (§4.3 ll. 938–944). We own only *when* they are invoked (P3 §4.3 l. 946) |
| `ScreenModel`/`ProfileModel`/`SliderSet`/`LanguageMap` parsing and the column formula itself | **Phase 3** (§3.2 ll. 740–745, §4.3 ll. 950–955) |
| Profile inference algorithm (`ProfileModel.infer`) | **Phase 3** (§4.8 ll. 1254–1257) |
| What a reload *does* internally — recompilation, buffer teardown, uniform rebind | **Phases 4–6** |
| Version-counter invalidation mechanism | **Phase 4** (DESIGN.md ll. 1560–1563) |
| Frame driver, pack/dimension lifecycle, the drain point and thread on which a reload executes, the shaders-off render path | **Phase 7** (DESIGN.md ll. 1850–1886) |
| Behavior of each engine setting: `normalMap`/`specularMap` → **Phase 13**; `renderQuality` → **Phase 5** sizing with **Phase 7** viewport; `shadowQuality` → **Phase 8**; `handDepth` → **Phase 7**; `oldHandLight` → **Phase 9**; `oldLighting` → **Phase 10** (the last two per P3 §3.1 ll. 698, 700) | as listed |
| The `SHADER_GUI` channel, its per-pack store, `EngineDiagnostic`/`DiagnosticReporter` | **Phase 1** (§4.9.4). We are the sink, not the store |
| The `modImplementation` Gradle configuration defect and the SPDX/`THIRD-PARTY.md` mechanism | **Phase 1** (§11.3 item 2, §12 item 43; §4.8.2–§4.8.3) |
| Anti-aliasing and anisotropic filtering | **nobody** — they do not exist (RESEARCH.md §1.2 l. 80; DESIGN.md l. 2374) |

### 1.3 What "Scope — out" removes, restated

Per the spec's *Scope — out* (ll. 2406–2407): option semantics and parsing are Phase 3's, reload
internals are Phases 4–7's, and the ModularUI licensing *note* is Phase 1's / §G7's. This document
designs none of them. Where it needs one, it consumes the dependency's published contract or files
a request in §5.4 — never an invented interface (§G1.1 "Dependency docs are contracts").

---

## 2. Architecture overview

### 2.1 Two layers, one seam — the OQ-9 hedge made structural

The spec's architecture requirement (ll. 2419–2421) is that the screen *model* live engine-side and
be headless-testable while the view is a thin adapter. That is realized as a hard package seam,
both halves of which Phase 1 has already granted:

```
:engine  com.schmaloogium.engine.config      ← P1 §2.1 l. 1534 assigns this package "Phase 3 (+12)"
           OptionPresentationModel            the whole tree: screens, entries, bindings, labels
           OptionEditSession                  pending changes, apply/discard/reset
           ReloadRequest / ReloadLifecycle    the classified request value + its merge algebra
           PackSelectionModel / PackSelectionActions   the selection view model + its intents
         (pure JVM; no Minecraft, Forge, Mixin, LWJGL, and no GL facade use at all)

:mod     com.schmaloogium.mod.gui            ← P1 §2.1 l. 1556 assigns this package "Phase 12"
           OptionScreenView (interface)       what a view must do; both adapters implement it
           VanillaOptionScreens               GuiScreen-based view — the OQ-9 fallback
           ModularUiOptionScreens             ModularUI-based view — adopted only if OQ-9 succeeds
           ShaderPackKeyBindings              F3+R chord observation
           ReloadShadersCommand               /reloadShaders
           ShaderResourceReloadListener       resource-manager forwarding
```

No new package grant is requested: both packages exist in Phase 1's §2.1 tables with Phase 12
named. The `.internal` rule and seam constraints C-1…C-4 (P1 §5.1 l. 4200) apply without exception.

The layer split is what makes the OQ-9 fallback cheap: **the entire contract surface — every App
F.3/F.4 construct, every label, every value transition, every reload classification — is decided in
`engine.config` and tested with JUnit alone.** A view adapter receives a fully resolved,
already-labelled tree and reports user intents back. Swapping ModularUI for the vanilla view
replaces `OptionScreenView`'s implementation and nothing else.

### 2.2 Public shape

Illustrative signatures; implementations stay private under `.internal`.

```java
// ---------- engine.config : the presentation model ----------

public record OptionPresentationModel(
    PresentationScreen mainScreen,
    Map<ScreenId, PresentationScreen> subScreens,
    List<EngineDiagnostic> diagnostics) {}

public record PresentationScreen(
    ScreenId id,                       // null-free; ScreenId.MAIN for the root
    String title,                      // already localized and decorated
    int resolvedColumns,               // post-`*`-expansion, per §4.3.4
    List<PresentationEntry> entries) {}

public sealed interface PresentationEntry {
    record SwitchOption(OptionId id, String label, boolean value,
                        Tooltip tooltip, boolean interactive) implements PresentationEntry {}
    record ValueOption(OptionId id, String label, String rawValue, String displayValue,
                       List<String> allowedValues, int valueIndex,
                       Tooltip tooltip, boolean interactive) implements PresentationEntry {}
    record SliderOption(OptionId id, String label, String rawValue, String displayValue,
                        List<String> allowedValues, int valueIndex,
                        Tooltip tooltip, boolean interactive) implements PresentationEntry {}
    record ProfileCycle(String label, Optional<ProfileName> current,
                        Tooltip tooltip) implements PresentationEntry {}
    record SubScreenLink(ScreenId target, String label, Tooltip tooltip) implements PresentationEntry {}
    record Blank() implements PresentationEntry {}                     // `<empty>`
}

public record Tooltip(List<TooltipLine> lines) {}
public record TooltipLine(String text, TooltipSeverity severity) {}    // WARNING renders red

// ---------- engine.config : the edit session ----------

public interface OptionEditSession {
    OptionPresentationModel present(ScreenId screen);
    void toggle(OptionId id);          // switch options
    void cycle(OptionId id, int step); // value options and sliders; step is +1/-1
    void setValueIndex(OptionId id, int index);
    void cycleProfile();
    boolean isDirty();
    int pendingChangeCount();
    ApplyOutcome apply();              // writes changed-only, then yields a ReloadRequest
    void discard();
    ApplyOutcome resetToPackDefaults();
}

public record ApplyOutcome(boolean persisted, Optional<ReloadRequest> reload,
                           List<EngineDiagnostic> diagnostics) {}

// ---------- engine.config : the reload request ----------

public enum ReloadLifecycle { NONE, REPUBLISH, FULL }   // ordered: NONE < REPUBLISH < FULL

public record ReloadRequest(
    ReloadLifecycle lifecycle,
    boolean worldRendererReload,       // additive: renderGlobal.loadRenderers() equivalent
    boolean resourceReacquire,         // additive: texture re-acquisition signal
    ReloadCause cause) {

    public static ReloadRequest merge(ReloadRequest a, ReloadRequest b) { /* §4.7.4 */ }
}

public enum ReloadCause { KEYBIND, COMMAND, RESOURCE_RELOAD, PACK_SELECTION,
                          OPTION_APPLY, OPTION_RESET, ENGINE_SETTING, PROFILE_APPLY }

// ---------- engine.config : global engine settings ----------

public record EngineSettingsModel(List<EngineSettingEntry> entries) {}

public sealed interface EngineSettingEntry {
    String key();                      // the stable optionsshaders.txt-equivalent key
    record Toggle(String key, String label, boolean value, Tooltip tooltip)
        implements EngineSettingEntry {}
    record TriState(String key, String label, TriStateValue value, Tooltip tooltip)
        implements EngineSettingEntry {}
    record Choice(String key, String label, List<String> allowedValues, int valueIndex,
                  Tooltip tooltip) implements EngineSettingEntry {}
}

public enum TriStateValue { DEFAULT, ON, OFF }   // DEFAULT defers to the pack's App F.1 flag

// ---------- engine.config : the pack-selection view model and its intents ----------

public record PackSelectionModel(
    DiscoveryGeneration generation,        // the generation these rows were built from (§4.6.1)
    List<PackSelectionRow> rows,           // Phase 3's order, never re-sorted
    PackCandidateId selected,              // the active row; `Off` and `(internal)` are rows too
    Optional<String> lastActionSummary) {} // Phase 3's sanitized failure summary, when there is one

public record PackSelectionRow(
    PackCandidateId id,
    PackCandidateKind kind,                // OFF / INTERNAL / DIRECTORY / ARCHIVE — the kind badge
    String displayName,                    // Phase 3-sanitized; never accepted back as a path
    PackCandidateStatus status,            // AVAILABLE / UNREADABLE / UNSAFE / LIMIT_EXCEEDED
    Optional<CompatibilityStatus> compatibility,  // present once a load produced one for this row
    List<EngineDiagnostic> diagnostics,    // the candidate's attributed diagnostics
    boolean interactive) {}

public interface PackSelectionActions {   // the closed set of intents a view may report
    void selectCandidate(PackCandidateId candidate);  // re-runs discover first (`[D-P12-8]`)
    void refresh();                                   // explicit re-discovery
    void openPackFolder();
    void openOptions();                               // only for a loadable non-`Off` row
    void close();
}

// ---------- mod.gui : the view seam ----------

public interface OptionScreenView {
    void showPackSelection(PackSelectionModel model, PackSelectionActions actions);
    void showOptions(OptionPresentationModel model, ScreenId screen, OptionEditSession session);
    void showErrors(List<EngineDiagnostic> shaderGuiDiagnostics);
    void close();
}
```

### 2.3 Data relationships

```
PackFrontEnd.discover ──→ PackDiscoveryResult ──→ PackSelectionModel ──→ view (pack list)
                                                          │
PackFrontEnd.load ──→ PackConfiguration ──┬── options() ──┴→ OptionPresentationModel ─→ view
                                          ├── compatibility() ─────→ pack-list warning row
                                          └── properties()/resources() → (other phases)

OptionEditSession ─ pending Map<OptionId,String> ─ apply ─→ OptionPersistenceCodec.write
                                                        └─→ ReloadRequest ─→ Phase 7 drain
EngineSettingsModel ─ apply ─→ GlobalShaderOptionsCodec.write ─→ EngineOptionData (next load)
```

---

## 3. Contract conformance map

Every in-scope contract row, the design element satisfying it, and its provenance. **Zero unmapped
rows.** "P3" means the row is parsed and modelled by Phase 3 and *displayed or actuated* here;
Phase 12 never re-parses.

### 3.1 RESEARCH Appendix F.3 — options

| # | Contract item (App F.3, ll. 1454–1470) | Design element | Provenance |
|---|---|---|---|
| F3-1 | Switch `#define NAME // tooltip` — default ON | `SwitchOption` with `value=true` from `OptionState`; toggle flips it (§4.4.1) | `[V:doc]` App F.3 l. 1456 |
| F3-2 | `// #define NAME // tooltip` — default OFF | same entry kind; the default originates in P3's catalog, never re-derived here | `[V:doc]` App F.3 ll. 1456–1457 |
| F3-3 | Recognized only when the same file `#ifdef`/`#ifndef`s it | Not re-checked: the presentation model is built from P3's confirmed catalog only (P3 §4.3 ll. 913–914) | P3 §4.3 |
| F3-4 | Tooltips split on `". "` | `TooltipBuilder.split` produces one `TooltipLine` per segment (§4.3.5) | `[V:doc]` App F.3 l. 1457 |
| F3-5 | Lines ending `"!"` render **red** | `TooltipSeverity.WARNING` → red in both views; P3 sets the severity (§4.3 l. 926), Phase 12 chooses the colour (§4.3.5) | `[V:doc]` App F.3 l. 1458; P3 §4.3 l. 926 |
| F3-6 | Variable `#define NAME <value> // tooltip [v1 v2 v3]` | `ValueOption` cycling the ordered `allowedValues` list (§4.4.2) | `[V:doc]` App F.3 l. 1459 |
| F3-7 | Default auto-added to the value list | Consumed as given; Phase 12 never inserts or removes a value | `[V:doc]` App F.3 l. 1459; P3 §3.2 |
| F3-8 | Const options, explicit whitelist | Rendered as `ValueOption`/`SliderOption` when visible; the whitelist is P3's | `[V:doc]` App F.3 ll. 1460–1466 |
| F3-9 | Const visible **only** when carrying a value list or referenced by a slider/profile/screen | The visibility predicate is applied at model-build time and is what makes a const eligible for `*` (§4.3.3) | `[V:doc]` App F.3 ll. 1465–1466 |
| F3-10 | Ambiguous options (conflicting defaults) are **disabled** | Rendered with `interactive=false`, label suffixed with the ambiguity marker, tooltip carrying P3's reported locations; excluded from `*` expansion (§4.3.3, `[D-P12-4]`) | `[V:doc]` App F.3 l. 1467; P3 §4.3 ll. 921–923, §6 l. 1696 |
| F3-11 | Lang `option.<NAME>` | Label resolution step 1 (§4.3.5) | `[V:doc]` App F.3 l. 1468 |
| F3-12 | Lang `option.<NAME>.comment` | Tooltip source (§4.3.5) | `[V:doc]` App F.3 l. 1468 |
| F3-13 | Lang `value.<NAME>.<val>` | `displayValue` resolution (§4.3.5) | `[V:doc]` App F.3 l. 1468 |
| F3-14 | Lang `prefix.<NAME>` | Prepended to `displayValue` (§4.3.5) | `[V:doc]` App F.3 l. 1469 |
| F3-15 | Lang `suffix.<NAME>` | Appended to `displayValue` (§4.3.5) | `[V:doc]` App F.3 l. 1469 |
| F3-16 | Lang `profile.<NAME>` | `ProfileCycle.label` value part (§4.3.5) | `[V:doc]` App F.3 l. 1469 |
| F3-17 | Lang `profile.<NAME>.comment` | `ProfileCycle` tooltip (§4.3.5) | `[V:doc]` App F.3 l. 1469 |
| F3-18 | Lang `screen.<NAME>` | `PresentationScreen.title` and `SubScreenLink.label` (§4.3.5) | `[V:doc]` App F.3 l. 1469 |
| F3-19 | Lang `screen.<NAME>.comment` | `SubScreenLink` tooltip (§4.3.5) | `[V:doc]` App F.3 l. 1469 |
| F3-20 | `sliders=<option list>` renders listed **variable** options as sliders | `SliderOption` emitted instead of `ValueOption` for members of P3's `SliderSet`; unknown/non-variable entries are diagnosed by P3 and simply do not become sliders (§4.4.3, `[D-P12-5]`) | `[V:doc]` App F.3 l. 1470; P3 §3.2 l. 740 |

### 3.2 RESEARCH Appendix F.4 — profiles and screens

| # | Contract item (App F.4, ll. 1472–1480) | Design element | Provenance |
|---|---|---|---|
| F4-1 | `profile.NAME=` tokens `OPTION` / `!OPTION` / `OPTION:value` / `OPTION=value` | Applied as a batch into the pending set on profile click (§4.5.2), from P3's expanded constraint list | `[V:doc]` App F.4 ll. 1474–1475; P3 §3.2 l. 741 |
| F4-2 | `profile.OTHER` copy, cycle-guarded | Consumed post-expansion; P3 ignores the cyclic edge and diagnoses (§6) | `[V:doc]` App F.4 l. 1475; P3 §4.8 l. 1249 |
| F4-3 | `!program.<name>`, optionally dimension-prefixed | **Not actuated here** — Phase 4 consumes disabled programs. Phase 12 applies only the *option* constraints of a profile and never mutates program state (§4.5.2) | `[V:doc]` App F.4 ll. 1475–1476; P3 §3.2 l. 741 |
| F4-4 | Current profile inferred from option values; otherwise **"Custom"** | `ProfileCycle.current` = `ProfileModel.infer(pendingOptionState)`, evaluated against the **pending** state so the label tracks unapplied edits (§4.5.1) | `[V:doc]` App F.4 ll. 1476–1477; P3 §4.8 ll. 1254–1257 |
| F4-5 | `screen=<entries>` main screen | `PresentationScreen` with `ScreenId.MAIN` (§4.3.1) | `[V:doc]` App F.4 l. 1478 |
| F4-6 | `screen.NAME=<entries>` subscreens | `subScreens` map, keyed by declared name (§4.3.1) | `[V:doc]` App F.4 l. 1478 |
| F4-7 | Entry: option names | `SwitchOption`/`ValueOption`/`SliderOption` (§4.3.2) | `[V:doc]` App F.4 l. 1479 |
| F4-8 | Entry: `[SUBSCREEN]` | `SubScreenLink` + navigation stack (§4.3.2, §4.6.2) | `[V:doc]` App F.4 l. 1479 |
| F4-9 | Entry: `<profile>` | `ProfileCycle` (§4.5) | `[V:doc]` App F.4 l. 1479 |
| F4-10 | Entry: `<empty>` | `Blank` — occupies a grid cell, never interactive (§4.3.2) | `[V:doc]` App F.4 l. 1479 |
| F4-11 | Entry: `*` — all unplaced options | **Phase 12's** deferred expansion, specified completely in §4.3.3 (`[D-P12-1]`, `[D-P12-2]`, `[D-P12-3]`) | `[V:doc]` App F.4 l. 1479; deferred to us by P3 §4.3 ll. 953–955 and §4.8 ll. 1251–1252 |
| F4-12 | `screen[.NAME].columns=N`, default 2 | Explicit valid positive `N` wins; absent → P3's formula (§4.3.4) | `[V:doc]` App F.4 l. 1480; P3 §4.3 ll. 950–952 |
| F4-13 | Auto-widens beyond 18 options | `max(2, ceil(actualOptionCount / 9))`, counted **after** `*` expansion and excluding navigation/layout entries: 0–18 → 2, 19–27 → 3 (§4.3.4) | `[V:doc]` App F.4 l. 1480; P3 §4.3 ll. 952–955 |

### 3.3 Remaining in-scope contract rows

| # | Contract item | Design element | Provenance |
|---|---|---|---|
| C-1 | App F.2 `version.<mcver>=<edition>` → pack-list warning | Candidate row renders `CompatibilityStatus.REQUIRES_NEWER_EDITION` as a warning badge + tooltip; the pack stays off (§4.6.1) | `[V:doc]` App F.2 l. 1452; P3 §4.8 ll. 1208–1211, §6 l. 1699 |
| C-2 | §4.7 "only changed options persist to `shaderpacks/<pack>.txt`" | `apply()` writes exactly the changed set through `OptionPersistenceCodec`; Phase 12 computes the change set, Phase 3 owns the format (§4.7.2) | `[V:observed]` §4.7 ll. 603–605; P3 §4.3 ll. 938–944 |
| C-3 | §4.7 "global engine settings in `optionsshaders.txt`" | The seven entries round-trip through `GlobalShaderOptionsCodec` → `EngineOptionData`, with their exact wire text bound in §4.6.3 | `[V:observed]` §4.7 l. 605; P3 §5.1 ll. 1601–1604 |
| C-4 | §4.7 GUI: pack list | `PackSelectionModel` over `PackFrontEnd.discover` (§4.6.1) | `[V:observed]` §4.7 l. 608 |
| C-5 | §4.7 GUI: engine options — **8 minus AA/AF = 7** | The seven entries of §4.6.2; AA/AF are absent by construction | `[V:observed]` §4.7 ll. 608–609; `[D-2]` §1.2 l. 80; DESIGN.md l. 2374 |
| C-6 | §4.7 GUI: screens generated from `screen.*` config with sliders/subscreens/profiles | §4.3–§4.5 in full | `[V:observed]` §4.7 ll. 609–610 |
| C-7 | §4.7 GUI: tooltips from lang files | §4.3.5's resolution chain | `[V:observed]` §4.7 l. 610 |
| C-8 | §4.7 "F3+R / `/reloadShaders` reload" | §4.8.1 (chord observation) and §4.8.2 (client command); both classify to lifecycle `FULL` (§4.7.3) | `[V:observed]` §4.7 l. 611; `[V:mcp]` platform symbols, §4.8 |
| C-9 | §4.7 failure handling: invalid programs **delete themselves and fall back through backup chains** | **Not actuated here** — Phase 4 deletes the program and walks the backup chain (P1 §6 l. 4294). Phase 12's obligation is the §G4.5 GUI channel: the per-program compile/link/validate diagnostics Phase 4 emits accumulate in Phase 1's `SHADER_GUI` store and are rendered by §4.9, driver log included | `[V:observed]` §4.7 ll. 612–614; DESIGN.md §G4.5 ll. 584–585 (per-program compile errors → the shader GUI, "per RESEARCH.md §4.7") ; P1 §4.9.4 l. 3790, §6 l. 4294 |
| C-10 | §4.7 failure handling: capability gate → chat error | Not ours — `UserChannel.CHAT`. The GUI additionally shows the resulting shaders-off state (§4.9) | `[V:observed]` §4.7 ll. 612, 615; P1 §4.9.4 l. 3797 |
| C-11 | §4.7 interlock matrix "ceases to exist" | No interlock logic exists anywhere in this design | `[V:observed]` §4.7 ll. 617–618; §1.2 ll. 82–86 |
| C-12 | §4.8 "Options/profiles/screens/lang, per-pack persistence" → **Keep** | Kept verbatim; nothing is "improved" (§G4.2) | `[V:doc]` §4.8 l. 632 |
| C-13 | §4.8 "Hand-rolled GUI on 2012-era screens" → **Skip**, ModularUI candidate | Both views designed; adoption gated on OQ-9 (§10) | `[V:doc]` §4.8 l. 649; §7.6 ll. 873–878 |
| C-14 | §G4.5: the shader GUI is one of three user-facing channels | §4.9 | DESIGN.md ll. 582–585 |
| C-15 | §G4.1: pack-facing vocabulary used verbatim | `profile`, `screen`, `slider`, `columns`, `option`, `value` survive unrenamed in §2.2's type and field names (`ProfileCycle`, `PresentationScreen`/`ScreenId`, `SliderOption`, `resolvedColumns`, `OptionId`, `rawValue`); `<empty>` is realized as `Blank` with the pack token retained in the adjacent comment (l. 277) because `<empty>` is not a legal Java identifier (see row F4-10); `*`, `prefix` and `suffix` are used verbatim in the §4.3.3/§4.3.4/§4.3.5 prose and rows F3-14/F3-15/F4-10/F4-11 | DESIGN.md ll. 545–550 |
| C-16 | App E.2: hook need 11 is **pure mod-side GUI, no vanilla injection** | No Mixin is authored by this phase; every platform touchpoint in §4.8 is a Forge event, a Forge registry call, or a Forge listener | `[V:mcp]` App E.2 l. 1427 |
| C-17 | RESEARCH §9 v0.4 exit: "options round-trip persistence" | §8.2's round-trip test set and the impl gate in §9 | `[D]` §9 l. 950 |

### 3.4 Reference evidence, contract checks, and do-not-inherit dispositions

Every Pintonium mechanism this design touches, with its §G11.4 disposition. **No mechanism adopted
here is contract-visible in the §G4.2 sense** — the contract fixes *what* the options model means,
not how a button is drawn — so no §G11.4 contract-check-plus-decision gate fires. The rows are
recorded anyway because §G11.4 requires consuming phases to show the relevant do-not-inherit rows
handled.

**Path abbreviation used below and in §4.** `…/VintageShaderPack{Selection,Options}Screen.java`
abbreviates the full coordinates stated once in §0.1 —
`Pintonium/forge122/src/shaders/java/net/irisshaders/iris/gui/<file>` — where `Pintonium/` is the
§G0.2 alias for `reference-src/pintonium-9c2fcc1/`. Line numbers are as read this session.

| Mechanism | Disposition | Evidence |
|---|---|---|
| Lang fallback chain: current game language → `en_us` → literal fallback | **Adopted** as the resolution order (§4.3.5), then checked against App F.3 l. 1468 — which names the decoration key families but not a locale policy, so no contract is displaced | `[V:observed — Pintonium forge122/…/gui/VintageShaderPackOptionsScreen.java:324-345]` |
| Name prettification (`_`/`.`/`-` → space, lowercase, capitalize each word) as the last-resort label | **Adopted** (§4.3.5) | `[V:observed — …VintageShaderPackOptionsScreen.java:347-367]` |
| Pending-change queue keyed by option id, Apply enabled only when non-empty | **Adopted** as the edit-session model (§4.5) | `[V:observed — …VintageShaderPackOptionsScreen.java:138,203,212,376]` |
| Escape / back-at-root **clears** the queue (discard); Done applies then closes | **Adopted** as the apply/discard UX (`[D-P12-9]`, §4.5.3) | `[V:observed — …VintageShaderPackOptionsScreen.java:137-141,170,237]` |
| Profile click writes the profile's whole option set into the queue | **Adopted** (§4.5.2) | `[V:observed — …VintageShaderPackOptionsScreen.java:215-223]` |
| `<empty>` rendered as a present-but-disabled cell | **Adopted** (§4.3.2) | `[V:observed — …VintageShaderPackOptionsScreen.java:95]` |
| Column count **clamped to 1…3** | **Rejected.** App F.4 l. 1480 sets the default at 2 and requires widening past 18 options, which P3's formula continues past 3 (28+ options → 4). A clamp would silently violate the widening contract | `[V:observed — …VintageShaderPackOptionsScreen.java:71]` vs `[V:doc]` App F.4 l. 1480 |
| `renderGlobal.loadRenderers()` on **every** apply and **every** pack selection | **Rejected as unconditional; adopted as conditional.** §4.7.3 fires the world-renderer reload only when a bake-time input actually changed. This is a non-contract internal (§G4.2 permits modernizing it) and the reason is stated in `[D-P12-11]` | `[V:observed — …VintageShaderPackOptionsScreen.java:249-253]`, `[V:observed — …VintageShaderPackSelectionScreen.java:192-195]` |
| `sliders=` support | **Absent from the reference — do not inherit a gap.** Neither GUI file contains a slider element type; the option screen dispatches over exactly four element kinds plus `EMPTY` (ll. 199–233). This confirms PD §7.4's "functionally dead" statement at the source. Sliders are designed from App F.3 alone (§4.4.3) | PD §7.4; `[V:observed — …VintageShaderPackOptionsScreen.java:199-233]` |
| "Lang-file **tooltips** with `en_us` fallback" (PD §14 l. 712) | **Correction, not adoption.** Neither file renders a hover tooltip; the `en_us` chain feeds *labels*, and `option.getComment()` is used as a label fallback (ll. 320–322), not as tooltip text. The tooltip contract stands on App F.3 alone. Reported in §11.3 item 1 | PD §14 l. 712 vs `[V:observed — …VintageShaderPackOptionsScreen.java:288-345]` |
| Engine-settings block in the selection screen | **No reference.** Pintonium's selection screen has no equivalent of OF's engine options (whole file). Designed from RESEARCH §4.7 alone, consistent with §G11.5 l. 985 listing render-quality multipliers under "No help available" | `[V:observed — …VintageShaderPackSelectionScreen.java]` (absence) |
| `(internal)` pack entry | **No reference.** Pintonium hardcodes `isInternal()` false (PD §7.1) and its list is a plain directory scan (ll. 230–248). We render P3's `PackCandidateKind.INTERNAL` sentinel from `discover` (§4.6.1) | PD §7.1; P3 §5.1 ll. 1575–1576 |
| PD §18 divergence table | **Not engaged.** Every row (attribute locations, dynamic unit map, dimension-folder semantics, `Random(0)` noise, 16-colortex allocation, missing `version.<mcver>` gate, missing `(internal)` pack) is outside this subsystem, except the last two — and both are *satisfied* here: `version.<mcver>` is displayed (C-1) and the `(internal)` sentinel is listed (§4.6.1) | DESIGN.md §G11.4 ll. 952–957 |
| PD §17 bug catalogue B1–B13 | **Not engaged.** No B-row lies in the GUI/persistence subsystem; the GUI-adjacent risk PD names is the dead `sliders=` (§7.4), handled above | PD §17 |

---

## 4. Detailed design

### 4.1 What is consumed, and the invariants it establishes

The presentation model is built from exactly two published artifacts and nothing else:

- `PackConfiguration.options()` — Phase 3's `OptionConfiguration`: the `OptionCatalog`, the
  immutable `OptionState`, and the profiles / screens / sliders / lang models (P3 §5.1 l. 1436);
- `PackConfiguration.compatibility()` and `PackConfiguration.pack()` — for the selection screen's
  status row (P3 §5.1 l. 1432).

Four invariants follow, and every algorithm below is written to preserve them.

- **I-1 — no re-parsing.** Phase 12 never opens a pack file, never rescans directives, never
  reinterprets properties, and never bypasses the materializer. P3 §5.1 l. 1621 states this as a
  binding consumer rule; the presentation model is a pure function of the published configuration.
- **I-2 — no mutation.** `PackConfiguration` and `OptionState` are deeply immutable (P3 §7
  l. 1714). An edit produces a *pending overlay*, never an in-place change. A reload publishes a
  new configuration (P3 §5.1 l. 1622).
- **I-3 — schema and fingerprint gating.** Derived state (a built presentation model) is retained
  only while both `schemaVersion` and the configuration fingerprint equal the values used to
  derive it (P3 §5.1 ll. 1622–1624). The model carries both and is rebuilt, never patched, when
  either changes. **The schema gate is symbolic, never a literal:** a configuration is admitted iff
  `schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION`, which this Phase 3 revision declares as
  `4` (P3 §2.2 l. 341, §5.3 ll. 1644–1645); versions 1, 2 and 3 are incompatible with the current
  surface and are never upgraded by inference (P3 §5.3 ll. 1659–1660), and any other value is
  rejected before a model is built. The constant moves whenever Phase 3's published record
  components or their meanings move — round 36's clear-colour correction is exactly why it is now
  `4` and not `3` (`PHASE_3_REVIEW_36.md` l. 304) — so Phase 12 hard-codes the number nowhere.
- **I-4 — determinism.** Two builds from the same configuration produce byte-identical models,
  including entry order, expanded-`*` order, and resolved column counts. This is what makes §8's
  golden tests meaningful and what the ordering rulings in §4.3.3 exist to guarantee.

### 4.2 Screen identity and the model build

`ScreenId` is either the reserved `MAIN` or a declared subscreen name, taken verbatim from P3's
screen model (§G4.1 forbids renaming). The build is a single pass:

1. Reject the configuration unless `schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION` (I-3).
2. Resolve the **locale** once for the whole build (§4.3.5) — every label in one model comes from
   one locale resolution, so a model never mixes languages.
3. Compute the **placement set**: the union of every option id appearing as an option entry in the
   main screen or any declared subscreen (§4.3.3).
4. Compute the **expansion set** and assign it to the winning `*` entry (§4.3.3).
5. For each screen, materialize entries in declared order, substituting the expansion set at the
   `*` position, then resolve columns (§4.3.4).
6. Emit diagnostics for every anomaly encountered (orphaned screens, surplus `*`, unresolved
   subscreen links), all at warning severity — none of them ever fails the build.

**A model build never fails.** The worst outcome is a main screen containing only the entries that
survived, which is the §6 rung-5 posture: a broken `screen.*` block must never make the options
screen unreachable.

### 4.3 The generated option screens

#### 4.3.1 Screens and navigation

The main screen is the entry point; `[SUBSCREEN]` entries link to declared subscreens. Navigation
is a **stack**, pushed on link activation and popped on Back, exactly as the reference does it
`[V:observed — Pintonium …/VintageShaderPackOptionsScreen.java:224-246]`. Popping past the root
leaves the options screen entirely. A `[SUBSCREEN]` naming an undeclared screen is diagnosed and
its entry is rendered as a disabled link rather than dropped, so a pack author sees the mistake
instead of a silently missing row `[D-P12-6]`.

**Subscreen cycles** cannot occur in the model: P3 ignores the cyclic edge and diagnoses it
(P3 §6 l. 1697). The navigation stack is additionally depth-bounded at 32 as a defence in depth;
exceeding it refuses the push and warns.

#### 4.3.2 Entry kinds

Declared entries map one-to-one onto `PresentationEntry` variants (§3.2 rows F4-7…F4-11).
`<empty>` becomes `Blank`, which **occupies a grid cell** and is never interactive — the layout
device pack authors use it for. An option entry naming an id absent from the catalog is diagnosed
and dropped (it is not a placement, so the id — being absent — cannot reappear via `*` either).

#### 4.3.3 `*` expansion — Phase 12's deferred obligation

App F.4 l. 1479 defines `*` as "all unplaced options". Phase 3 explicitly defers it: *"`*`
expansion is intentionally deferred to Phase 12 because it depends on placement across screens"*
(P3 §4.8 ll. 1251–1252), and *"Phase 12 retains ownership of deferred `*` expansion; options
produced by that expansion do count before Phase 12 evaluates the formula"* (P3 §4.3 ll. 953–955).
App F.4 does not close three questions; each is decided here and recorded.

**Eligibility.** An option is a candidate for expansion when it is (a) in the catalog, (b) *visible*
per App F.3 — switch and variable options always, const options only when they carry a value list
or are referenced by a slider, profile, or screen (App F.3 ll. 1465–1466) — and (c) **not
ambiguous**. Ambiguous options are disabled by Phase 3 (P3 §4.3 ll. 921–923); auto-placing a
permanently non-interactive row into every pack's main screen is noise, so `*` skips them, while an
*explicitly* placed ambiguous option still renders as a disabled row with its locations in the
tooltip `[D-P12-4]`.

**Placement is by declaration, not by reachability** `[D-P12-1]`. An option named in *any* declared
screen is placed, even if no `[SUBSCREEN]` links to that screen. App F.4 defines screens by
declaration and says nothing about reachability, and the alternative — treating an unreachable
screen's options as unplaced — would silently duplicate them into the main screen and change what
the pack author wrote. Orphaned screens are diagnosed separately so the author still learns of the
mistake.

**Exactly one `*` wins** `[D-P12-2]`. If `*` appears more than once — within one screen or across
screens — the **first occurrence in build order** (main screen first, then declared subscreens in
declaration order; within a screen, entry order) receives the entire expansion set. Every later `*`
expands to nothing and is diagnosed. Splitting the set across several `*` entries has no contract
definition, and duplicating it would place the same option twice.

**Expansion order** `[D-P12-3]`. Expanded options are emitted in `OptionCatalog` iteration order.
Phase 3's §5.1 does not publish that order (§5.4 request 1), so until it does, the fallback is a
stable sort on the option name by **unsigned UTF-8 byte order** — the tie-break vocabulary P3
already uses for published collections (P3 §5.1 l. 1506). I-4 requires *some* total order; this one
is deterministic, locale-independent, and does not depend on hash iteration.

**Interaction with columns.** Expanded options count toward `actualOptionCount` before the column
formula runs (P3 §4.3 ll. 953–955) — see §4.3.4.

#### 4.3.4 Column resolution

Per App F.4 l. 1480 and P3 §4.3 ll. 950–952:

1. A screen with an **explicit valid positive** `columns=N` uses `N`. This wins unconditionally,
   including after `*` expansion.
2. Otherwise, `resolvedColumns = max(2, ceil(actualOptionCount / 9))`.
3. `actualOptionCount` counts **option entries only** — switch, value, slider — *including* every
   option produced by `*` expansion, and *excluding* `[SUBSCREEN]`, `<profile>`, and `<empty>`
   (P3 §4.3 l. 953).

The boundary is exact and is a named test: 18 actual options → 2 columns; 19 → 3; 27 → 3; 28 → 4.
A screen whose entries are all navigation/layout has `actualOptionCount = 0` and resolves to 2.

#### 4.3.5 Labels, values, and tooltips

**Locale resolution.** One locale per model build. The chain is: the client's configured language,
lowercased; then `en_us`; then the literal fallback. Adopted from the reference
`[V:observed — Pintonium …/VintageShaderPackOptionsScreen.java:324-345]`; App F.3 l. 1468 fixes the
key families but states no locale policy, so nothing is displaced. Phase 3 loads and normalizes the
lang files and leaves the fallback choice to us (P3 §4.3 ll. 926–928).

**Label chain**, per entry kind:

| Entry | Key | Fallback |
|---|---|---|
| option name | `option.<NAME>` | the option's own comment text if non-empty, else prettified `<NAME>` |
| option value | `value.<NAME>.<val>` | the raw value verbatim |
| profile name | `profile.<NAME>` | prettified `<NAME>`; the "Custom" outcome uses `profile.Custom` then the literal `Custom` |
| screen title / link | `screen.<NAME>`; the main screen uses the bare key `screen` | prettified `<NAME>` |

**Prettification** replaces `_`, `.` and `-` with spaces, lowercases, and upper-cases the first
character of each word `[V:observed — Pintonium …/VintageShaderPackOptionsScreen.java:347-367]`. It
is applied **only** as a last-resort fallback — never over a lang-provided string.

**Value decoration.** `displayValue = prefix.<NAME> + valueLabel + suffix.<NAME>`, with each affix
omitted when absent (App F.3 l. 1469). Decoration applies to the *displayed* value only; the
persisted value is always the raw one (§4.7.2).

**Tooltips.** The tooltip source is `option.<NAME>.comment` when present, else the option's
in-source comment as captured by Phase 3. The text is split on `". "` into `TooltipLine`s
(App F.3 l. 1457). A line whose original text ends in `!` carries `TooltipSeverity.WARNING`; Phase 3
sets that severity at parse time (P3 §4.3 l. 926) and both views render `WARNING` lines **red**
(App F.3 l. 1458). Severity is a model-level fact so the headless tests assert it without a view.
The `.comment` families for `profile.` and `screen.` feed the corresponding entries' tooltips
identically.

### 4.4 Widgets and value transitions

All three interactive option kinds share one rule: **a value transition selects an element of the
option's published list; no value is ever invented, interpolated, or clamped into existence.**

#### 4.4.1 Switch options

Two states. `toggle(id)` writes the opposite of the current effective value into the pending set as
the canonical `"true"`/`"false"` text the codec round-trips. Label shows the state.

#### 4.4.2 Variable and const value options

`cycle(id, +1)` moves to the next index modulo the list length; `cycle(id, -1)` moves back —
`Math.floorMod` semantics, matching the reference's forward cycle
`[V:observed — Pintonium …/VintageShaderPackOptionsScreen.java:206-214]` and extending it with a
backward step (right-click / shift-click in the views).

**Out-of-list current values.** Phase 3's codec "never constrains a syntactically safe current
value to the UI's advertised list" (P3 §4.3 l. 942). A pack option can therefore legitimately hold
a value that is not in `allowedValues`. The rule `[D-P12-7]`: such a value is *displayed*
(decorated as usual, marked with the out-of-list indicator in the tooltip) and is **retained until
the user cycles it**; the first cycle from an out-of-list value moves to index 0 of the list. It is
never silently rewritten, because doing so would persist a change the user never made.

#### 4.4.3 Sliders — designed from App F.3 alone

App F.3 l. 1470 says only: *"`sliders=<option list>` renders listed variable options as sliders."*
There is **no working reference**: PD §7.4 records Pintonium's `sliders=` as functionally dead, and
this session confirmed it at the source — the option screen dispatches over exactly four element
kinds plus `EMPTY` and contains no slider type
`[V:observed — Pintonium …/VintageShaderPackOptionsScreen.java:199-233]`. The Oculus evidence that
§G11.5 l. 978 says answers sliders was not assigned to this phase and was not read (§0.4 item 2).

The design is therefore the minimal reading of the contract `[D-P12-5]`:

- A slider is a **discrete selector over the option's ordered `allowedValues` list**. Its track has
  exactly `allowedValues.size()` stops; the handle sits at `valueIndex`.
- **No interpolation and no numeric parsing.** A slider never produces a value absent from the
  list, and never treats the values as numbers — App F.3 lists them as an opaque ordered value
  list, and const options in the whitelist include non-numeric forms.
- Drag maps the pointer position to the nearest stop; arrow keys step ±1; the displayed label is
  the same decorated `displayValue` a `ValueOption` would show.
- Because a slider differs from a value option only in its *affordance*, `SliderOption` carries an
  identical payload to `ValueOption`. **A view that cannot draw a slider may render it as a value
  option with no loss of contract fidelity** — which is precisely what makes the OQ-9 fallback
  complete (§10.1 part 4).
- `sliders=` naming an unknown or non-variable option is diagnosed by Phase 3 (P3 §3.2 l. 740) and
  simply yields no slider; other entries in the list are unaffected.

#### 4.4.4 Non-interactive rows

`interactive=false` is set for ambiguous options (F3-10) and for entries whose behavior owner has
not shipped yet (§4.6.2). Both carry a tooltip line explaining why, so a disabled row is never
mysterious.

### 4.5 Profiles

#### 4.5.1 Inference and the label

`ProfileCycle.current` is `ProfileModel.infer(state)` where `state` is the **pending** option state
— current values overlaid with unapplied edits — so the label flips to `Custom` the moment an edit
diverges from the active profile, and back when it returns. Phase 3 owns the algorithm: descending
expanded-constraint count, source order as tie-break, first exact match wins, `Custom` otherwise
(P3 §4.8 ll. 1254–1257). Phase 12 re-implements none of it.

#### 4.5.2 Click-to-cycle

Activating a `<profile>` entry advances to the next profile and writes **that profile's whole
expanded option constraint set** into the pending set — the reference's behavior
`[V:observed — Pintonium …/VintageShaderPackOptionsScreen.java:215-223]`. Only the *option*
constraints are applied: `!program.<name>` tokens are Phase 4's input, not a user-editable value,
and Phase 12 never touches program state (row F4-3).

The cycle order is the profile declaration order, with the entry after the last wrapping to the
first. When the current state is `Custom`, the first click selects the **first declared** profile.

**This requires two things Phase 3's §5 does not publish** — the ordered profile list and each
profile's expanded option constraints. `ProfileModel.infer` alone answers "which profile am I on?"
but cannot answer "what does clicking to the next one set?". Filed as §5.4 request 1(b) with an
interim assumption stated there.

#### 4.5.3 Apply, discard, reset

The edit session holds `pending: Map<OptionId, String>` of raw values, keyed by option id, and
`isDirty()` is `!pending.isEmpty()` — the reference's queue shape
`[V:observed — Pintonium …/VintageShaderPackOptionsScreen.java:138,203,376]`.

| Action | Effect | Lifecycle (§4.7) |
|---|---|---|
| **Apply** | Merge pending over current, write the changed-only set through `OptionPersistenceCodec`, clear pending, emit a `ReloadRequest` | `REPUBLISH` |
| **Done** | Apply if dirty, then close | `REPUBLISH` if dirty, else `NONE` |
| **Escape / Back at the root screen** | Discard: clear pending, no write, no reload | `NONE` |
| **Reset** | Clear pending and write an empty changed-set (the pack reverts to its in-source defaults), then emit a request | `REPUBLISH` |

Done-applies / Escape-discards is `[D-P12-9]`, adopted from the reference (ll. 137–141, 170, 237)
and made explicit in the UI: the Apply control is enabled only when dirty (reference l. 376), and a
dirty session shows an unsaved-change count so Escape is never a surprise. There is **no**
write-on-every-click: the Phase 12 Scope-in "Persistence round-trip" requirement of
"write-through on change" (`docs/design/v3/DESIGN.md` l. 2381; §4.7.2) is satisfied at apply time,
which is the change the user committed. Reset writes an empty changed set rather than deleting the
file, so the pack's persistence state is explicit rather than inferred from absence.

### 4.6 The pack-selection screen

#### 4.6.1 The candidate list

Populated from `PackFrontEnd.discover`, which Phase 3 states we invoke to populate and refresh
selection UI (P3 §5.1 ll. 1583–1584). Consumed properties, all of them Phase 3's:

- Result ordering is already correct: `Off` and `Internal` first, then filesystem candidates in
  Phase 3's deterministic order (P3 §5.1 l. 1575). **Phase 12 does not re-sort.**
- `PackCandidateKind` is closed — `OFF`, `INTERNAL`, `DIRECTORY`, `ARCHIVE` (P3 §5.1 l. 1576) — and
  each gets a distinct row icon/prefix.
- `PackCandidateStatus` is closed — `AVAILABLE`, `UNREADABLE`, `UNSAFE`, `LIMIT_EXCEEDED`
  (P3 §5.1 l. 1577). Non-`AVAILABLE` candidates render disabled with the status and the candidate's
  attributed diagnostics in the tooltip.
- Display names are sanitized by Phase 3 and are **never** accepted back as paths (P3 §5.1
  ll. 1578–1579). The view keeps them as display strings only.
- `PackCandidateId` is opaque and is valid only against the latest discovery generation for that
  directory; a later discovery supersedes it (P3 §5.1 ll. 1579–1582). The selection screen holds
  the `DiscoveryGeneration` it rendered and **re-runs `discover` before acting on any selection**,
  so a stale id never reaches `load` (`[D-P12-8]`). A `Refresh` control re-runs discovery
  explicitly.
- A `LOAD` that fails returns exactly one `Failed` and leaves the caller in shaders-off (P3 §5.1
  l. 1619). The screen shows the sanitized summary and stays open with `(off)` selected.

The `(off)` sentinel is always present, always enabled, always selectable — this is the GUI's half
of §G2.4 rung 5 ("shaders-off must always be a reachable state").

`CompatibilityStatus.REQUIRES_NEWER_EDITION` — the `version.<mcver>` outcome — renders as a warning
badge on the candidate row plus a tooltip naming the required edition. The pack remains selectable
and remains off; Phase 3 keeps the configuration inspectable and Phase 7 keeps shaders off
(P3 §4.8 ll. 1208–1211, §6 l. 1699), so the user can read the reason rather than face a silent
no-op (row C-1).

The screen also offers `Open folder` and a link into the options screen, enabled only when a
loadable non-`Off` pack is selected — the reference's gating shape
`[V:observed — Pintonium …/VintageShaderPackSelectionScreen.java:77-79,287-290]`.

#### 4.6.2 The seven engine settings

RESEARCH §4.7 ll. 608–609 lists OF's eight; §1.2 l. 80 and DESIGN.md l. 2374 delete AA and AF.
**Seven remain.** Phase 12 owns their key names, domains, defaults, and UX; each row's *behavior*
belongs to another phase, and this table is the authoritative statement of that split. The exact
**wire text** each value persists as is bound in §4.6.3; the two tables together are the whole
engine-settings contract (§5.1).

| Setting | Key | Domain | Default | Behavior owner |
|---|---|---|---|---|
| Normal map | `normalMap` | `Toggle` | on | **Phase 13** (`_n` companion atlases, `MC_NORMAL_MAP`) |
| Specular map | `specularMap` | `Toggle` | on | **Phase 13** (`_s` companion atlases, `MC_SPECULAR_MAP`) |
| Render quality | `renderQuality` | `Choice` over Phase 5's published multiplier list | the list's identity entry | **Phase 5** (buffer sizing) with **Phase 7** (viewport) |
| Shadow quality | `shadowQuality` | `Choice` over Phase 8's published multiplier list | the list's identity entry | **Phase 8** |
| Hand depth | `handDepth` | `Choice` over Phase 7's published list | the list's identity entry | **Phase 7** (the depth-scale matrix, DESIGN.md l. 1868) |
| Old hand light | `oldHandLight` | `TriState` | `DEFAULT` | **Phase 9** (P3 §3.1 l. 698) |
| Old lighting | `oldLighting` | `TriState` | `DEFAULT` | **Phase 10** (P3 §3.1 l. 700) |

**The tri-state is the documented priority rule made explicit** `[D-P12-10]`. App F.1 l. 1448
states that "corresponding in-game video settings win where both exist", and P3 §3.1 l. 693 assigns
that resolution to the behavior owner rather than to its MC-free parser. `DEFAULT` therefore means
"no in-game setting exists — use the pack's App F.1 flag"; `ON`/`OFF` mean "the in-game setting
exists and wins". The behavior owner receives an unambiguous three-valued input and needs no
convention of its own.

**The three `Choice` domains are not invented here.** §G11.5 l. 985 lists render-quality
multipliers under "No help available", and no legally citable source fixes OF's exact multiplier
ladder. Phase 12 renders whatever ordered list the owning phase publishes and falls back to a
single identity entry when the list is absent, which keeps the row visible-but-inert rather than
missing. The three lists are handed to Phases 5, 8 and 7 in §11.4. Until an owner publishes its
list *and* ships its behavior, the row renders with `interactive=false` and a tooltip naming the
milestone (§4.4.4) — `normalMap`/`specularMap` are Phase 13 work at v0.5, so they are visible and
inert through v0.4.

#### 4.6.3 Global persistence and the exact wire spellings

The seven settings round-trip through `GlobalShaderOptionsCodec` into the `EngineOptionData` that
`PackLoadRequest` already carries (P3 §2.2 l. 392, §5.1 ll. 1601–1604). Two consequences from
Phase 3's stated behavior:

- `EngineOptionData` is a stable-order map of **decoded string values**; unknown safe keys are
  retained for round-trip with a warning, and malformed entries are ignored with a warning
  (P3 §5.1 ll. 1601–1604). So the seven keys travel as strings and each behavior owner parses its
  own domain. Phase 12 does not need Phase 3 to know the key set, and no interface change is
  requested for this.
- Because `engineOptions` is an *input to `load`*, changing one requires publishing a new
  configuration — lifecycle `REPUBLISH`, not a runtime poke (§4.7.3). The pack selection itself is
  persisted in the same global file.

**Precisely because Phase 3 models `EngineOptionData` as an opaque decoded-string map, Phase 12
owns the token text as well as the key names** — and leaving the tokens unstated would make every
behavior owner guess at them. They are therefore bound here. This table, not prose elsewhere, is
what Phases 5, 7, 8, 9, 10 and 13 parse against:

| Key | Entry kind | Exact persisted tokens | An absent key means |
|---|---|---|---|
| `normalMap` | `Toggle` | `true` \| `false` | `true` |
| `specularMap` | `Toggle` | `true` \| `false` | `true` |
| `renderQuality` | `Choice` | the owner-published list entry, byte-for-byte | the list's identity entry |
| `shadowQuality` | `Choice` | the owner-published list entry, byte-for-byte | the list's identity entry |
| `handDepth` | `Choice` | the owner-published list entry, byte-for-byte | the list's identity entry |
| `oldHandLight` | `TriState` | `default` \| `true` \| `false` | `default` |
| `oldLighting` | `TriState` | `default` \| `true` \| `false` | `default` |

The rules that make that table executable `[D-P12-19]`:

- **Exact, case-sensitive, untrimmed match**, compared byte-for-byte after Phase 3's
  Java-Properties decoding. `True`, `TRUE`, `On`, `on`, `1` and `yes` are **not** accepted for a
  `Toggle` or a `TriState`; an unrecognized token warns on `schmaloogium.config` and is treated as
  if the key were absent. Phase 12 writes only the tokens above.
- **The boolean text is §4.4.1's.** `Toggle` reuses the canonical `"true"`/`"false"` the pack-option
  switch path round-trips, so the engine has exactly one boolean spelling everywhere.
- **`TriStateValue` persists `DEFAULT → "default"`, `ON → "true"`, `OFF → "false"`.** This is the
  one place a reader could go wrong, so it is stated rather than implied: Phase 3's adjacent
  App F.1 tri-state names its states `DEFAULT/TRUE/FALSE` (P3 §3.1 l. 692) while Phase 12's enum is
  `DEFAULT/ON/OFF`. Those are two *enum vocabularies over the same three states*; neither is the
  wire text. The wire text is the lowercase triple above, and `"default"` is the token carrying
  "no in-game setting exists — use the pack's App F.1 flag" (`[D-P12-10]`).
- **A `Choice` persists the owning phase's published list entry verbatim** — no prefix, suffix,
  case-folding, numeric reformatting, or locale decoration. `displayValue` decoration (§4.3.5) is a
  display concern and never reaches the file. When the owner has published no list the row is inert
  (§4.6.2) and Phase 12 **writes no entry for that key at all**, so an absent key keeps meaning
  "the owner's own default" instead of a Phase-12-invented placeholder.
- **Round-trip stays lossless for keys we do not own.** Phase 3 retains unknown safe keys, so a
  global file written by a later revision survives a v0.4 write cycle (P3 §5.1 ll. 1601–1604).

**The persisted pack selection**, bound on the same ownership argument (§4.7.2 makes Phase 12 its
writer, and Phase 3 sees only opaque strings):

| Key | Exact persisted tokens | An absent key means |
|---|---|---|
| `shaderPack` | `off` \| `(internal)` \| the candidate's Phase-3 sanitized display name, byte-for-byte | `off` — shaders off |

`PackCandidateId` is **not** persistable: it is opaque and valid only against the latest discovery
generation (P3 §5.1 ll. 1579–1582). The durable identity of a filesystem pack is therefore its
sanitized display name, which Phase 3 publishes per candidate and which is never fed back as a path
(P3 §5.1 ll. 1578–1579). A reader restoring the selection runs `PackFrontEnd.discover` first and
matches that name — exactly, case-sensitively — against the current result to obtain a live
`PackCandidateId`; this is the duty P3 §5.1 ll. 1583–1585 already assigns Phase 7. An unmatched or
non-`AVAILABLE` name resolves to `off` with a warning on `schmaloogium.config`; if two candidates
sanitize to the same name, the first in Phase 3's deterministic order (l. 1575) wins. This is a
Phase-12-owned file convention, so no Phase 3 interface change is requested.

### 4.7 Persistence timing and the reload model

#### 4.7.1 What Phase 3 owns, restated

Formats, escaping, atomic-write mechanics, symlink refusal, and stable ordering are Phase 3's
(P3 §4.3 ll. 938–944). *"Phase 3 owns codecs and file models. Phase 12 owns when a user applies/
discards and invokes writes"* (P3 §4.3 l. 946), and *"Phase 12 must define apply/discard timing and
global setting UX without changing the codecs"* (P3 §11.4 l. 2044). This section is the answer to
exactly that.

#### 4.7.2 When a write happens

| Write | Trigger | Codec |
|---|---|---|
| Per-pack changed-only options → `shaderpacks/<sanitized-pack-id>.txt` | `apply()`, `Done`-while-dirty, `resetToPackDefaults()` | `OptionPersistenceCodec` |
| Global settings + current pack selection | any engine-setting change; any pack selection change | `GlobalShaderOptionsCodec` |

Never on hover, navigation, or discard. The persisted per-pack set is the **changed-only** set
(RESEARCH §4.7 ll. 604–605): options whose effective value differs from the option's pack default.
An option edited and then returned to its default is *removed* from the file, not written with its
default value — otherwise the file grows monotonically and stops meaning "what the user changed".

A write failure warns and **retains the in-memory state** — never turns the pack off, never loses
the edit (P3 §6 l. 1701; §6 row 7 below).

#### 4.7.3 The trigger × lifecycle matrix

Three ordered lifecycles and two additive flags, per §2.2:

- **`NONE`** — no engine work; view-local only.
- **`REPUBLISH`** — `PackFrontEnd.load` with the **unchanged** selection and the new option/engine
  inputs, producing a new `PackConfiguration`; one Phase 4 version-counter bump; Phases 4/5/6/13
  rebuild as their version polls dictate. No re-discovery.
- **`FULL`** — `PackFrontEnd.discover` **then** `load`; everything `REPUBLISH` does, plus a fresh
  discovery generation, plus Phase 7's dimension re-initialization, plus the shaders-off transition
  when the selection is `Off`.
- **`worldRendererReload`** (additive) — the `renderGlobal.loadRenderers()` equivalent; only
  meaningful when a world is loaded.
- **`resourceReacquire`** (additive) — re-acquire textures whose backing Minecraft object may have
  changed; changes no configuration.

Republish rather than in-place patching is forced by the contract, not chosen: options are applied
by **rewriting source lines at compile time** (RESEARCH §4.7 ll. 603–604), and Phase 3's
`SourceMaterializer.materialize` takes `OptionState` as an input (P3 §2.2 ll. 532–538). A changed
option is therefore a changed program source. There is no cheaper correct path.

| Trigger | Lifecycle | worldRendererReload | resourceReacquire | Notes |
|---|---|---|---|---|
| **F3+R keybind** | `FULL` | **yes** | no | Author-facing: re-reads the pack from disk, so discovery must re-run (row C-8) |
| **`/reloadShaders`** | `FULL` | **yes** | no | Identical semantics to F3+R by contract (RESEARCH §4.7 l. 611) |
| **Resource-manager reload** (F3+T, resource-pack change) | `NONE` | no | **yes** | Shader packs live in `shaderpacks/`, not in resource packs — a resource reload invalidates no pack. It *does* invalidate Minecraft-owned textures the engine borrows, so the signal is forwarded and nothing else happens (`[D-P12-12]`) |
| **Pack selection changed** (incl. → `Off`, → `(internal)`) | `FULL` | **yes** | no | Also writes the global file. `Off` additionally routes through Phase 7's shaders-off path |
| **Option apply / Done-while-dirty** | `REPUBLISH` | if bake-set changed | no | §4.5.3 |
| **Profile applied** | `REPUBLISH` | if bake-set changed | no | A profile is an option batch; nothing is special about it after the batch lands in pending |
| **Reset to pack defaults** | `REPUBLISH` | if bake-set changed | no | §4.5.3 |
| **Engine setting: `normalMap` / `specularMap`** | `REPUBLISH` | no | **yes** | Changes the macro header (`MC_NORMAL_MAP` / `MC_SPECULAR_MAP`) → new materialization; Phase 13 rebuilds companion atlases |
| **Engine setting: `renderQuality` / `shadowQuality`** | `REPUBLISH` | no | no | Buffer sizing is Phase 5/8's resize path, driven at Phase 7's frame begin |
| **Engine setting: `handDepth`** | `REPUBLISH` | no | no | Phase 7 reads the published value |
| **Engine setting: `oldHandLight`** | `REPUBLISH` | no | no | Phase 9 resolves it against the pack flag |
| **Engine setting: `oldLighting`** | `REPUBLISH` | **yes** | no | Bake-time: Phase 10 owns lighting that is baked into chunk meshes |
| **Screen navigation, scrolling, hover, discard** | `NONE` | no | no | — |
| **Dimension switch** | *not a Phase 12 trigger* | — | — | Phase 7 owns it (DESIGN.md ll. 1879–1882); listed so the matrix is closed |

**The bake-set predicate** `[D-P12-11]`. `worldRendererReload` fires on a `REPUBLISH` only when a
chunk-bake input actually changed between the old and new published configurations:

```
bakeSetChanged(old, new) =
      unionOf(new.resources().programs()[*].vertices()) != unionOf(old.…)   // MC_ENTITY | MC_MID_TEX_COORD | AT_TANGENT
   || effective(separateAo, new) != effective(separateAo, old)
   || effective(oldLighting, new) != effective(oldLighting, old)
```

`VertexRequirements` is closed over exactly those three attributes (P3 §5.1 l. 1535), and
`separateAo`/`oldLighting` are the two App F.1 flags whose effect is baked into chunk geometry
(behavior owners Phase 10 / Phase 10, P3 §3.1 ll. 700, 712). The reference reloads renderers on
*every* apply and *every* selection
`[V:observed — Pintonium …/VintageShaderPackOptionsScreen.java:249-253]`,
`[V:observed — …/VintageShaderPackSelectionScreen.java:192-195]`; that is correct-but-coarse, and
chunk re-meshing is the most expensive thing a shader GUI can do. Nothing contract-visible depends
on it (§G4.2), so the conditional form is adopted. Two honesty notes: on a **`FULL`** reload the
flag is unconditional, because the pack — hence every requirement — may have changed wholesale; and
under Phase 3's current ruling that `shaders.properties` does *not* receive option macros
(P3 §11.5 item 2, ll. 2052–2053), the flag terms cannot change on an option-only apply, so today
degenerates to the vertex-set term. It is written in the general form so it survives that ruling
being revisited.

#### 4.7.4 Coalescing and the merge algebra

PD §14 l. 717 records the reference merging changed options "through a queue on reload", and the
Phase 12 spec names coalescing as part of reload safety (DESIGN.md ll. 2382–2384, 2389–2390). The
mechanism here is a **single-slot pending request**, not an unbounded queue:

```
merge(a, b) = ReloadRequest(
    lifecycle          = max(a.lifecycle, b.lifecycle),          // NONE < REPUBLISH < FULL
    worldRendererReload= a.worldRendererReload || b.worldRendererReload,
    resourceReacquire  = a.resourceReacquire  || b.resourceReacquire,
    cause              = b.cause)                                // the latest cause, for diagnostics
```

`merge` is associative, commutative in its effect fields, and idempotent (`merge(a,a) = a` except
for `cause`), which is what makes "N rapid clicks produce one reload" a *property* rather than a
hope, and is asserted as such in §8.1.

Two invariants follow, and they are the whole of "reload safety" for this phase:

- **RS-1 — one bump per published configuration.** A drain publishes at most one new
  `PackConfiguration` and therefore causes exactly one Phase 4 version-counter increment
  (DESIGN.md ll. 1560–1563). Downstream caches poll that counter; a burst of GUI activity must not
  produce a burst of invalidations.
- **RS-2 — never mid-frame.** The drain happens at a frame boundary, before Phase 7's frame-begin
sequence. Publication is an atomic reference swap (P3 §7 l. 1715), so no consumer ever observes a
  half-replaced configuration.

**Phase 12 owns the request value, its classification, and its merge algebra. Phase 7 owns the
drain point, the executing thread, and everything downstream of publication** — that is the
*Scope — out* line "what reload does internally (Phases 4–7)". §5.2 states the seam; §5.3 records
that Phase 7's doc does not yet carry it.

### 4.8 Platform bindings

All three are `mod.gui` and all three are Forge mechanisms — **no Mixin is authored by this phase**,
which is App E.2's ruling that hook need 11 is "pure mod-side GUI (no vanilla injection)"
(RESEARCH.md l. 1427, row C-16). DESIGN.md ll. 1967–1969 additionally directs preferring a Forge
event over a mixin wherever one covers the need.

#### 4.8.1 F3+R

1.12.2's `KeyBinding` system cannot express a **chord**: vanilla's `F3+X` combinations are handled
inside `Minecraft`'s own key dispatch, and a registered `KeyBinding` is a single key. Reproducing
OF's F3+R therefore means observing key input, not registering a binding
`[V:mcp]`. The design:

- Subscribe to `net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent` `[V:mcp]` — fired
  for client key input outside a GUI screen.
- On each event, when the shader engine is active and no `GuiScreen` is open, test whether the
  debug key is held **and** `R` transitioned to pressed; if so enqueue a `FULL` request with
  `cause = KEYBIND` and emit a chat/log acknowledgement.
- `net.minecraftforge.fml.client.registry.ClientRegistry#registerKeyBinding(KeyBinding)` `[V:mcp]`
  is **also** used, but for a separate, rebindable single-key "open shader pack screen" binding —
  a convenience that is not part of the contract. F3+R itself is not a `KeyBinding`.
- **Gating** `[D-P12-13]`: the chord is observed only while the engine is in a state where a reload
  is meaningful. When shaders are off, F3+R is ignored and the key is left entirely alone, so the
  mod never shadows a combination another mod or a future vanilla version may claim. F3+R is
  unused by vanilla 1.12.2's debug combinations.

#### 4.8.2 `/reloadShaders`

Registered against `net.minecraftforge.client.ClientCommandHandler.instance` — a `public static
final` field on a `CommandHandler` subclass whose javadoc states client commands take precedence
over identically named server commands `[V:mcp]`. The command enqueues the same `FULL` request with
`cause = COMMAND`. Registration happens once during client init; the command is client-side only,
consistent with the client-only mission (§1.2 l. 78).

**What the command replies with, exactly.** `ReloadCoordinator.submit` returns `void` (§5.1) and is
deliberately not given a return type: the drain is asynchronous and Phase 7 owns the drain point,
the executing thread and everything downstream of publication (§4.7.4), so there is no outcome to
return at call time. The reply is therefore a **local acknowledgement built from the request
Phase 12 has just classified** — one chat line naming the lifecycle (`FULL`) and the cause
(`COMMAND`) — or, when no coordinator is installed, the inert-trigger message of §6 instead. The
reload's *result* is not a command return value: a successful reload is silent, and per-program
failures surface through Phase 1's `SHADER_GUI` store in §4.9's panel. `ApplyOutcome` (§2.2) is not
involved here; it is `apply()`'s value, not the coordinator's.

#### 4.8.3 Resource-reload integration

A `net.minecraftforge.client.resource.ISelectiveResourceReloadListener` `[V:mcp]` — which extends
`IResourceManagerReloadListener` and adds
`onResourceManagerReload(IResourceManager, Predicate<IResourceType>)` — is registered so the engine
can react to a resource-pack change without reloading the shader pack. It emits
`ReloadRequest(NONE, worldRendererReload=false, resourceReacquire=true, RESOURCE_RELOAD)` and
nothing else (`[D-P12-12]`, §4.7.3). The *selective* variant is chosen so the listener can ignore
reload types that cannot affect textures at all.

### 4.9 GUI-side error surfacing

Phase 1 already provides the store: `EngineDiagnostic` carries a `UserChannel` of
`CHAT | SHADER_GUI | LOG_ONLY`, and `SHADER_GUI` "accumulates into a per-pack error store that
Phase 12's screen renders" (P1 §4.9.4 ll. 3776, 3790). P1 §5.3 l. 4268 names Phase 12 as the
channel consumer and §11.4 l. 5167 states plainly: *"the `SHADER_GUI` diagnostic channel and its
per-pack error store exist; your screen is the sink."* P1 §9 l. 4513 tags the routing `v0.4` with
the store existing at v0.1 — the milestones line up exactly.

Design:

- The pack-selection and options screens both carry an **issues indicator** showing the count and
  worst severity in the current pack's store; it is absent when the store is empty.
- Activating it opens a scrollable diagnostics panel: severity, subsystem, message, and — for
  per-program compile/link/validate failures — the driver log Phase 4 attached (P1 §6 l. 4294).
  Long driver logs are scrollable and selectable, never truncated to a single line.
- A **capability-gate** failure is `ERROR`/`CHAT` (P1 §4.9.4 l. 3797), so it does not reach this
  panel. The GUI's contribution is to show the resulting state honestly: the pack row reads as
  off, with the chat message quoted in its tooltip.
- Phase 12 **renders** diagnostics; it never creates, mutates, filters by policy, or clears them.
  Store lifetime is Phase 1's and per-pack scoping means a reload naturally replaces the set.

### 4.10 The view adapters

#### 4.10.1 The seam

`OptionScreenView` (§2.2) is the entire surface a view must implement. A view receives fully
resolved models — labels localized, tooltips split and severity-tagged, columns resolved, `*`
already expanded — and reports intents back to `OptionEditSession` / `PackSelectionActions`. **A
view makes no contract decision.** That sentence is the OQ-9 hedge in one line, and §8.1's tests
enforce it by covering every App F.3/F.4 row with no view present.

#### 4.10.2 The vanilla `GuiScreen` view — the OQ-9 fallback, designed in full

Two `GuiScreen` subclasses, structured after the two working reference screens (LGPL-3.0, §G11.2
rule 1 permits incorporation with compliance; the files are cited per element in §3.4).

*Pack selection.* Fixed-width centred list; the `(off)` sentinel pinned first; a selection marker
prefix on the active row; up/down buttons plus mouse-wheel scrolling with the row window computed
from screen height; `Open folder`, `Refresh`, `Shader pack settings…`, `Done` along the bottom;
labels trimmed with an ellipsis to the button width; a status line for the last action; kind and
compatibility badges per row (ours, not the reference's). `doesGuiPauseGame()` returns `false` so
the world keeps rendering while the user changes packs — which is what makes live pack switching
usable `[V:observed — Pintonium …/VintageShaderPackSelectionScreen.java:163-165]`.

*Options.* Grid of `resolvedColumns` buttons per row, sized from the column count; the row window
and scroll offset computed from screen height; up/down plus wheel; `Back`, `Reset`, `Apply`, `Done`
along the bottom, with `Apply` enabled only when dirty; hover renders the tooltip through vanilla's
hover-text facility with `WARNING` lines in red (this is ours — the reference draws no tooltips,
§3.4); Escape backs out of a subscreen and discards at the root.

Two deliberate departures from the reference, both already justified in §3.4: **columns are not
clamped to 3** (App F.4's widening contract), and **sliders exist** — drawn as a track with
discrete stops, or degraded to a cycling button if a view cannot draw one (§4.4.3).

This view is **zero-dependency**: it needs nothing beyond vanilla and Forge, so it is always a
reachable state even if ModularUI is absent at runtime.

#### 4.10.3 The ModularUI view — conditional on OQ-9

The MCP survey establishes the shape and the risk (`[V:mcp]`, all from the LGPL-3.0
CleanroomMC/ModularUI corpus):

- **A container-less client screen path exists.** ModularUI's own JEI integration branches on
  `GuiContainer.class.isAssignableFrom(clz)`, handling `<T extends GuiScreen & IMuiScreen>` through
  `ModularScreenJEIHandler` and container screens through `ContainerScreen`; both
  `GuiScreenWrapper` and `GuiContainerWrapper` are registered as wrapper types. A shader GUI is
  client-only with no inventory, so `GuiScreenWrapper` is the target path — **not** the
  `IGuiHolder`/`GuiManager.open(…, EntityPlayerMP)` flow the tile-entity examples show, which is
  server-initiated and would be wrong here.
- **Layout and scrolling primitives exist**: `ModularPanel`, `ModularScreen(modId, panel)`, `Flow`
  with axis resizers and relative sizing, `AbstractScrollWidget` with horizontal/vertical scroll
  data, tooltip builders with auto-update.
- **No slider widget appears in the indexed corpus.** The spike must confirm one exists or that a
  discrete slider is composable from the primitives; if not, the slider degrades to a cycling
  button (§4.4.3) and ModularUI still passes on the other two criteria.

**Dependency arrangement `[D-P12-14]`: ModularUI is an ordinary mod dependency, not bundled.**
Phase 1 deliberately left it unpinned and handed the decision here (P1 §4.2.6 as indexed by §5.3
l. 4274). Of the two arrangements P1 §4.8.4 describes, the mod-dependency form is **mere
aggregation** and carries no LGPL-3.0 redistribution obligation, whereas `contain` jar-in-jar *is*
distribution of the LGPL-3.0 work and brings notice, modification-marking and relink obligations
with it — plus a version-collision risk against a user-installed copy. It is also the fallback P1
already designed for exactly this case (P1 §10.2 l. 4604). **Blocking defect, restated:** the
template declares only `modCompileOnly`/`modRuntimeOnly` — *no `modImplementation` configuration
exists* — and P1 §12 item 43 is the fix (P1 §11.3 item 2, §5.3 l. 4274). P1 §11.4 l. 5169 predicted
this "will likely bite you first". §5.4 request 4 restates it as a dependency.

If the spike fails, **no ModularUI dependency is declared at all** and the vanilla view ships —
the arrangement decision then costs nothing.

---

## 5. Cross-phase interfaces

### 5.1 Exposed interfaces and data contracts

| Exposed contract | Content | Consumer(s) |
|---|---|---|
| `OptionPresentationModel`, `PresentationScreen`, `PresentationEntry` (5 variants + `Blank`), `Tooltip`, `TooltipLine`, `ScreenId`, `OptionId` | The complete resolved screen tree: `*` already expanded, columns already resolved, labels already localized and decorated, tooltips already split and severity-tagged. Deeply immutable; a pure function of one `PackConfiguration` plus one locale; deterministic per I-4 | the two view adapters; **Phase 2** harness (headless golden runs of the model) |
| `OptionEditSession`, `ApplyOutcome` | Pending-change overlay with `toggle`/`cycle`/`setValueIndex`/`cycleProfile`/`apply`/`discard`/`resetToPackDefaults`. `apply` invokes Phase 3's codec and yields at most one `ReloadRequest`. Never mutates a published configuration | the view adapters |
| `ReloadLifecycle` (`NONE < REPUBLISH < FULL`), `ReloadRequest`, `ReloadCause`, `ReloadRequest.merge` | The classified reload value and its merge algebra (§4.7.4). `merge` is associative and idempotent in its effect fields; `lifecycle` takes the maximum and the two additive flags take the union. **This is what Phase 12 hands the frame driver** | **Phase 7** (executes it); **Phase 4** (the resulting single version-counter bump) |
| `ReloadCoordinator` — `void submit(ReloadRequest request)` | Declared in `engine.config`, **implemented by Phase 7** and installed into the GUI layer at bootstrap. Phase 12 never executes a reload, chooses a thread, or touches GL. The two invariants the implementation must uphold are RS-1 (one published configuration ⇒ one version bump) and RS-2 (drain at a frame boundary, never mid-frame) | **Phase 7** |
| `EngineSettingsModel`, `EngineSettingEntry` (`Toggle`/`TriState`/`Choice`), `TriStateValue` | The seven engine settings with their **stable key names, domains and defaults** (§4.6.2's table) *and* their **exact persisted wire text** (§4.6.3's table: `true`/`false` for `Toggle`, `default`/`true`/`false` for `TriState`, the owner-published list entry verbatim for `Choice`, an absent key meaning the stated default). Values travel to consumers as decoded strings inside `EngineOptionData`; each behavior owner parses its own key against those two tables and nothing else — no other spelling is written and none is accepted. `TriStateValue.DEFAULT` (wire `default`) means "defer to the pack's App F.1 flag"; `ON`/`OFF` (wire `true`/`false`) mean "the in-game setting exists and wins" (App F.1 l. 1448) | **13** (`normalMap`, `specularMap`), **5**+**7** (`renderQuality`), **8** (`shadowQuality`), **7** (`handDepth`), **9** (`oldHandLight`), **10** (`oldLighting`) |
| `PackSelectionModel`, `PackSelectionRow`, `PackSelectionActions` | The selection screen's view model over `PackDiscoveryResult` — kind badge, status, compatibility badge, per-candidate diagnostics, current selection, last-action summary — plus `PackSelectionActions`, the **closed** set of intents a view reports back: select-candidate (by `PackCandidateId`), refresh, open-folder, open-options, close. **All three are declared in §2.2**, so a view adapter implements `showPackSelection` from the published shape and invents nothing. Contains no path, no root, no archive lease, and no unsanitized name (P3 §5.1 ll. 1578–1579, 1582) | the view adapters |
| Persisted pack selection — the `shaderPack` key in the global file | Phase 12 writes it (§4.7.2) and binds it (§4.6.3): tokens `off` \| `(internal)` \| the candidate's Phase-3 sanitized display name byte-for-byte; an absent key means `off`. `PackCandidateId` is never persisted, so a reader re-runs `PackFrontEnd.discover` and matches that name to obtain a live id, resolving to `off` with a warning if it does not match an `AVAILABLE` candidate | **Phase 7** (restores the selection at bootstrap/reload, P3 §5.1 ll. 1583–1585) |
| `OptionScreenView` | The entire view seam (§2.2, §4.10.1). Implementing it is the whole cost of swapping UI frameworks — this is the OQ-9 hedge stated as an interface | `mod.gui` implementations only |

Nothing here exposes a Minecraft, Forge, Mixin or LWJGL type: every row above `OptionScreenView`
lives in `:engine` and satisfies seam constraints C-1…C-4 (P1 §5.1 l. 4199).

### 5.2 Consumed from Phase 3 (`docs/phase3/v1/PHASE_3_DOC.md`, **not verified** — §0.2)

Phase 3 is not verified, so **every row below is a provisional consumption** (§0.2). Each locator
was re-resolved individually against the current dependency document this session (§5 = ll. 1420–
1689; the drift from the superseded revision was not a uniform offset, so no bulk shift was
applied). Rows marked **⟳** sit on text that round 36's admitted corrections rewrote and owe
re-derivation the moment Phase 3 obtains a fresh literal `PASS` (§11.4); the unmarked rows were
checked against the current text and carry the semantics this design assumed, but none of them is
*certified* until that `PASS` exists.

| Phase 3 §5.1 row | Use here |
|---|---|
| **⟳** `PackFrontEnd.discover` / `PackDiscoveryRequest` / `PackDiscoveryResult` / `PackCandidate` (l. 1429) | Populates and refreshes the selection list (§4.6.1). Consumed properties: the `Off`/`Internal`-first ordering (l. 1575), closed `PackCandidateKind` (l. 1576), closed `PackCandidateStatus` (l. 1577), sanitized display names never accepted back as paths (ll. 1578–1579), and opaque `PackCandidateId` valid only against the latest generation (ll. 1579–1582). **⟳** round 36 candidate-006 incorporated §2.2's declarations into this row (ll. 1446–1452) |
| **⟳** `PackFrontEnd` / `PackLoadRequest` / `PackLoadResult` (l. 1430) | Every `REPUBLISH` and `FULL` lifecycle is a `load`. `PackSelection` is closed to `Off`/`Internal`/`Filesystem(id)` (ll. 1587–1588); `Off` returns `PackLoadResult.Off` without touching other request fields (ll. 1590–1591). **⟳** same candidate-006 incorporation |
| **⟳** `PackConfiguration` (l. 1431) | The single input from which the presentation model is derived; consumed with the **symbolic** `schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION` gate — `4` in this revision (§2.2 l. 341, §5.3 ll. 1644–1645), with 1/2/3 rejected and never upgraded by inference (§5.3 ll. 1659–1660) — and the fingerprint retention rule (§5.1 ll. 1622–1624). **⟳** round 36 candidate-003 is what moved the constant to `4` |
| `PackIdentity`, `CompatibilityStatus`, `DimensionConfiguration` (l. 1432) | Selection-screen status row; `REQUIRES_NEWER_EDITION` drives the `version.<mcver>` warning (row C-1) |
| `OptionConfiguration` — `OptionCatalog`, immutable `OptionState`, profiles/screens/sliders/lang (l. 1436) | The whole of §4.3–§4.5. The row explicitly assigns us the `*` expansion and states that expansion-produced options count toward widening. Round 36's candidate-006 incorporation does **not** cover this row, so §5.4 request 1 stands unchanged |
| `OptionPersistenceCodec`, `GlobalShaderOptionsCodec` (l. 1437) | §4.7.2. Invoked, never modified — P3 §11.4 l. 2044 |
| **⟳** `PackLoadRequest.engineOptions` / `EngineOptionData` (§2.2 l. 392, §5.1 ll. 1601–1604) | The seven engine settings' transport: stable-order decoded strings, unknown safe keys retained for round-trip, malformed entries warned and ignored. The token text on that transport is Phase 12's and is bound in §4.6.3. **⟳** same candidate-006 incorporation |
| **⟳** `ResourceRequirements.programs[*].vertices` / `VertexRequirements` (l. 1535) | The vertex-attribute term of the bake-set predicate (§4.7.3). **⟳** round 36 candidates 003, 009 and 010 all rewrote the surrounding `ResourceRequirements` §5 text; the `VertexRequirements` domain itself (`MC_ENTITY`/`MC_MID_TEX_COORD`/`AT_TANGENT`) is unchanged |
| `ShaderPropertiesModel.engineFlags` (l. 1438) | Read only for the two bake-time flags in the predicate (`separateAo`, `oldLighting`); behavior remains each owner's per P3 §3.1 |
| `PackLoadFailure` + closed codes (ll. 1614–1619) | Sanitized failure summaries in the selection screen; every failure leaves shaders off |
| Consumer rules (ll. 1621–1624) | I-1/I-2/I-3 in §4.1 |
| `ProfileModel.infer(OptionState)` (§4.8 ll. 1254–1257) | The profile label (§4.5.1) |
| Column formula and its counting rule (§4.3 ll. 950–955) | §4.3.4 |
| Tooltip split and `TooltipSeverity.WARNING` (§4.3 l. 926) | §4.3.5 |
| Threading and immutability guarantees (§7 ll. 1709–1717) | §7 below |

### 5.2b Consumed from Phase 1 (`docs/phase1/v14/PHASE_1_DOC.md`, verified)

| Phase 1 §5 row | Use here |
|---|---|
| Module layout + §2.1 package table (§5.1 l. 4198) | `engine.config` is granted to "Phase 3 (+12)" (l. 1534) and `mod.gui` to Phase 12 (l. 1556) — §2.1. No new package is requested |
| Seam constraints C-1…C-4 and the package-placement/`.internal` rule (§5.1 ll. 4199–4200) | The model/view split in §2.1 |
| `EngineDiagnostic`, `DiagnosticSeverity`, `UserChannel`, `DiagnosticReporter` (§5.3 l. 4268, which names **12** as "GUI is a channel consumer") | §4.9 — the `SHADER_GUI` store is our sink |
| `Log` / `Logs` / the fixed channel list (§5.3 ll. 4266–4267) | GUI-side warnings during model build and persistence |
| Mod-dependency declaration mechanics (P1 §5.3 l. 4274, which indexes P1 §4.2.6, P1 §4.8.4 and P1 §11.3 item 2) | `[D-P12-14]` in §4.10.3 and request 4 below |
| Naming: `mod_id = schmaloogium`, root package `com.schmaloogium` (§5.1 l. 4204) | Type placement throughout |
| SPDX header convention + `THIRD-PARTY.md` mechanism (§5.3 l. 4275) | Applied to any incorporated LGPL-3.0 view code (§0.5) |

**No GL service, handle, or facade verb is consumed.** The presentation model has no GL surface at
all, and the views draw through vanilla/ModularUI, not through `engine.gl`.

### 5.3 Consumed from non-dependency phases — flagged assumptions

The Phase 12 spec directs both of these consumptions and requires them flagged here
(DESIGN.md ll. 2387–2390).

**(A) Phase 4 — reload-safety version counter.** DESIGN.md ll. 1560–1563 assigns Phase 4 a
monotonically increasing pipeline version counter that downstream caches poll to invalidate on
reload, and states outright: *"Phase 12's reload paths depend on this; expose it in doc §5."*
Phase 12 consumes it **indirectly and by consequence only**: invariant RS-1 (§4.7.4) says one
published configuration causes exactly one bump. Phase 12 neither reads nor increments the counter;
the coupling is that a coalescing failure here would become an invalidation storm there. If Phase
4's §5 does not expose the counter, this is the request that says it must — and §G5.3 item 4 names
"P4's version-counter invalidation vs P12's reload paths" as a final-integration-review checklist
item, so the seam is already scheduled for audit.

**(B) Phase 7 — the reload lifecycle (soft dependency, §G5.3 item 3).** `PHASE_7_DOC.md` is
verified as of `PHASE_7_REVIEW_36.md` but is **not a Required input** for Phase 12 and was not read
(§0.3), so this design is written against Phase 7's *spec* in DESIGN.md ll. 1850–1886.
Four assumptions, each of which becomes a §G1.3 fix-up trigger if Phase 7's verified doc
contradicts it:

1. **A frame-boundary point exists at which a configuration may be published.** Assumed from the
   frame-begin lifecycle (l. 1851) and the ordering constraint that world-state sampling precedes
   resize/clear (ll. 1854–1855). RS-2 depends on it.
2. **Phase 7 owns pack/dimension transitions and publishes new configurations on them.** Assumed
   from the dimension-switch lifecycle (ll. 1879–1882) and corroborated independently by P3 §11.4
   ll. 2034–2037 — itself provisional text, per §0.2. `FULL` delegates to this.
3. **Phase 7 owns the shaders-off path** (l. 1877, G2.4 rule 5). Selecting `Off` hands Phase 7 a
   `FULL` request; Phase 12 does not implement the transition.
4. **Phase 7 will implement `ReloadCoordinator`** (§5.1) and own the drain thread. This is a *new*
   obligation: it is not in Phase 7's spec text, so it is also recorded as a hand-off (§11.4) and
   will need adopting whether or not the rest of Phase 7 changes.

`worldRendererReload` and `resourceReacquire` are likewise executed by Phase 7 and Phase 13
respectively; Phase 12 only classifies.

### 5.4 Requested changes to the dependency contract

Flagged here, not assumed (§G1.1: *"If it is missing something you need, flag the request in your
doc §5; do not invent the missing interface as if it existed."*).

**Request 1 — Phase 3: close the option-model publication surface.** P3 §5.1 l. 1436 *names*
`OptionConfiguration` (`OptionCatalog`, immutable `OptionState`, profiles/screens/sliders/lang) and
l. 1437 names both codecs, but — unlike `ProgramStateModel` (ll. 1512–1530), `ResourceRequirements`
(ll. 1532–1558) and `CustomTextureSpec` (ll. 1490–1500), each closed with executable shapes and
declared ordering — these rows bind **behaviors only**. Round 36's candidate-006 fix incorporated
§2.2's declarations into the front-end, materialization, macro and internal-source rows
(ll. 1446–1452) but **not** into the option-model row, so this request is unaffected by it. Four
specifics are load-bearing here:

  (a) **Executable shapes** for `OptionConfiguration`, `OptionCatalog`, `OptionState`,
      `ScreenModel`, `ProfileModel`, `SliderSet`, `LanguageMap`, `OptionPersistenceCodec` and
      `GlobalShaderOptionsCodec`, in the same style as the aggregates above.
  (b) **The ordered profile list and each profile's expanded option constraints.**
      `ProfileModel.infer(OptionState)` answers "which profile is active"; click-to-cycle (§4.5.2)
      additionally needs "what does the next profile set", which nothing in §5 publishes.
  (c) **`OptionCatalog`'s published iteration order**, on which `*`-expansion determinism rests
      (§4.3.3, I-4).
  (d) **The per-option projection** the GUI reads: kind (switch/variable/const), pack default,
      ordered allowed values, ambiguity flag with its reported locations, and tooltip text with its
      `TooltipSeverity`. §3.2/§4.3 describe these; §5 does not bind them.

  *Interim assumption:* Phase 12 builds against the behaviors §3.2/§4.3/§4.8 describe, and against
  (b)/(c) as stated in §4.3.3 and §4.5.2 with their fallbacks. Every one of those points is a
  named test in §8.1, so a mismatch surfaces as a test failure rather than as silent drift.

**Request 2 — Phase 3: close where persisted per-pack options enter a load.** P3 §4.3 l. 947 states
*"Reload merges persisted values into a new immutable `OptionState`"*, but the binding
`PackLoadRequest` (§2.2 ll. 387–394, §5.1 ll. 1590–1595) carries only the **global**
`engineOptions` — there is no per-pack persisted-options field, and §5 never says `load` reads
`shaderpacks/<pack>.txt` itself. One of the two must be stated, because the reload matrix cannot be
closed without knowing which:

  - either add a per-pack persisted-options field to `PackLoadRequest`, or
  - state in §5 that `load` performs the read through `OptionPersistenceCodec` from the resolved
    pack identity.

  *Interim assumption:* the **second**. Phase 12 therefore performs **write-then-reload**:
  `apply()` completes the codec write *before* submitting the `ReloadRequest`, so the values
  `load` merges are the ones just applied. This ordering is a hard requirement of the interim
  assumption and is asserted in §8.1 (`apply_writeCompletesBeforeReloadSubmitted`). If the first
  form is chosen instead, only `apply()`'s call shape changes; no other design element moves.

  *Cost note:* adding a request field is **not** a change to `PackConfiguration`'s record-component
  set, so by P3 §5.3 ll. 1647–1649 it does not by itself increment `CURRENT_SCHEMA_VERSION`. It is
  still a §5 change and therefore owes a fresh verify session under §G1.3.

**Request 3 — Phase 4: expose the version counter in its §5**, per DESIGN.md l. 1563. See §5.3(A).

**Request 4 — Phase 1: land the `modImplementation` fix.** P1 §11.3 item 2 records that the
template declares only `modCompileOnly`/`modRuntimeOnly` — *no `modImplementation` configuration
exists* — and P1 §12 item 43 is the fix; P1 §11.4 l. 5169 anticipated that it "will likely bite you
first". `[D-P12-14]`'s mod-dependency arrangement needs it. This is a restatement of an
already-recorded Phase 1 work item, not a new interface request; no Phase 1 §5 row changes.

---

## 6. Failure modes & degradation

The §G2.4 ladder — including **rung 2a**, feature-level failure (DESIGN.md ll. 427–434) — applied
case by case. Rung 5 is the invariant every row serves: **nothing here ever crashes the client, and
shaders-off is always reachable from every screen.**

| Failure | Degradation and diagnostic | Rung |
|---|---|---:|
| Malformed / unresolvable screen entry, unknown option name in a screen, `[SUBSCREEN]` naming an undeclared screen | Warn on the `schmaloogium.config` channel; render an unresolved link as **disabled** and drop an unknown option entry; every other entry on the screen survives. The model build never fails (§4.2) | 2a |
| More than one `*`, or `*` on a screen whose expansion set is empty | First occurrence wins and later ones expand to nothing, each diagnosed (§4.3.3). No duplication, no crash | 2a |
| Ambiguous option | Rendered non-interactive with its reported locations in the tooltip, excluded from `*` (§4.3.3). Phase 3 already disabled it (P3 §6 l. 1696) | 2a |
| Cyclic profile or subscreen reference | Phase 3 ignores the cyclic edge and retains the rest (P3 §6 l. 1697); the navigation stack is additionally depth-bounded at 32 (§4.3.1) | 2a |
| Lang file missing, locale absent, or key absent | Fall through current locale → `en_us` → prettified name (§4.3.5). A pack with no lang files is fully usable | 2a |
| Current option value is not in the advertised list | Displayed and retained; first cycle moves to index 0 (`[D-P12-7]`). Never silently rewritten | 2a |
| **Per-pack or global persistence write fails** | Warn; **retain the in-memory state**; keep the pending set intact so the user can retry; never turn the pack off, never lose the edit. Matches P3 §6 l. 1701 | 2a |
| `PackFrontEnd.load` returns `Failed` | Show the sanitized summary and the failure code; the screen stays open with `(off)` selected; the prior configuration is not replaced. Phase 3 guarantees the caller is left shaders-off (P3 §5.1 l. 1619) | 4 |
| Discovery returns no candidates, or the shaderpacks directory is invalid | The list shows `(off)` and `(internal)` only, plus the attributed diagnostic; `discover` never throws (P3 §5.1 ll. 1571–1574) | 4 |
| Stale `PackCandidateId` (a later discovery superseded the generation) | Re-run `discover` before acting (`[D-P12-8]`); a stale id that still reaches `load` fails as `INVALID_SELECTION` and is reported, not retried blindly | 4 |
| `version.<mcver>` unmet | Warning badge, pack stays off, configuration remains inspectable (row C-1; P3 §6 l. 1699) | 4 |
| Per-program compile/link/validate failure | Not ours to handle — Phase 4 deletes the program and resolves through the backup chain (P1 §6 l. 4294). Ours is to **display** the `SHADER_GUI` record with its driver log (§4.9) | 3 (displayed) |
| Capability gate fails at init | `ERROR`/`CHAT` (P1 §4.9.4 l. 3797). The GUI shows the resulting off state honestly (§4.9) | 4 |
| `ReloadCoordinator` absent or not yet installed at bootstrap | Reload triggers are inert and log once; `/reloadShaders` replies with that inert-trigger message rather than an acknowledgement (§4.8.2); the GUI still opens, still reads, still persists. A GUI that cannot reload is degraded, not broken | 2a |
| View-layer exception (either adapter) | Caught at the view boundary, logged, and the screen closes to the parent rather than propagating into vanilla's GUI stack | 5 |
| ModularUI absent at runtime | The vanilla view is used. It has no dependencies beyond vanilla and Forge, so the GUI is always reachable (§4.10.2) | 2a |

---

## 7. Threading & performance notes

| Component | Thread ownership |
|---|---|
| `OptionPresentationModel`, `OptionEditSession`, `EngineSettingsModel`, `ReloadRequest` | Pure values and pure logic. Deeply immutable except the edit session's pending map, which is confined to the client thread. No GL affinity, no Minecraft affinity — this is what makes §8's headless tests possible |
| Both view adapters | **Client thread only** (vanilla `GuiScreen` and ModularUI are both client-thread constructs) |
| Key-input, command, and resource-listener bindings | Client thread; each does nothing but build a `ReloadRequest` and call `ReloadCoordinator.submit` |
| `PackFrontEnd.discover` / `load` | **Not Phase 12's choice.** Phase 3 states both may run off the render thread and that `load` has no GL affinity (P3 §7 ll. 1709–1711); publication is the caller's atomic reference operation (l. 1715). Phase 7 owns which thread executes a drained request |
| Persistence writes | Serialized per pack identity by Phase 3 (P3 §7 l. 1716); a writer never mutates the active configuration |
| `SHADER_GUI` diagnostics | Phase 1 hops `CHAT` and `SHADER_GUI` deliveries to the client thread (P1 §7 l. 4319); the panel reads an already-client-thread-confined store |

**Performance posture** (§G2.5: clean code first, optimize with evidence):

- The presentation model is built **once per (configuration fingerprint, locale)** and cached under
  I-3's gate. Navigation, scrolling and hover allocate nothing; a value cycle mutates one map entry
  and rebuilds only the affected screen's entry list.
- The expensive operation in this subsystem is not the GUI — it is chunk re-meshing. That is
  exactly why `worldRendererReload` is predicated rather than unconditional (`[D-P12-11]`), and why
  coalescing exists (§4.7.4): a user dragging a slider across ten stops and pressing Apply once
  must cost one reload, not ten.
- `doesGuiPauseGame()` is `false` on both screens, so the world keeps rendering while options
  change. This is a UX requirement (you cannot judge a shader setting on a frozen frame) and it is
  what makes RS-2's frame-boundary drain a real, frequently-exercised path rather than a corner.
- No per-frame work exists in this phase at all: nothing here participates in the frame loop except
  the drain that Phase 7 owns.

---

## 8. Testability plan

All tests are headless `:engine` JUnit tests unless marked otherwise. None needs a GL context, a
`GLCapabilityProfile`, or a Minecraft type — the model layer's purity is the point (§2.1).

### 8.1 Headless model tests

**`*` expansion and placement**
- `optionStar_expandsOnlyUnplacedOptions` — options placed on any screen never reappear.
- `optionStar_placementIsByDeclarationNotReachability` — an option placed on an orphaned subscreen
  is **not** expanded, and the orphan is diagnosed (`[D-P12-1]`).
- `optionStar_firstOccurrenceWinsAcrossScreens` — main-screen `*` wins over a subscreen `*`;
  the later one expands to nothing and is diagnosed (`[D-P12-2]`).
- `optionStar_firstOccurrenceWinsWithinOneScreen` — entry order decides.
- `optionStar_orderIsDeterministic` — two builds from one configuration produce identical order
  (I-4), including under the unsigned-UTF-8 fallback (`[D-P12-3]`).
- `optionStar_excludesAmbiguousOptions` and `optionStar_includesReferencedConstWithoutScreen` —
  the eligibility predicate, both halves (`[D-P12-4]`, App F.3 ll. 1465–1466).

**Columns**
- `screenColumns_explicitCountWinsAfterExpansion`.
- `screenColumns_boundary18Yields2And19Yields3` and `..._27Yields3And28Yields4` — the exact
  widening boundary with expansion-produced options counted (App F.4 l. 1480; P3 §4.3 ll. 952–955).
- `screenColumns_navigationAndLayoutEntriesDoNotCount` — `[SUBSCREEN]`, `<profile>`, `<empty>`.

**Entries and labels**
- `screenEntries_allFiveKindsPlusEmpty` — every App F.4 entry form materializes.
- `screenEntries_emptyOccupiesACellAndIsNotInteractive`.
- `screenEntries_unresolvedSubscreenLinkRendersDisabled` (`[D-P12-6]`).
- `lang_localeThenEnUsThenPrettifiedFallback` — all three rungs, plus
  `lang_oneLocalePerModelBuild` (`[D-P12-15]`).
- `lang_prettifyUnderscoreDotDash`.
- `lang_valuePrefixAndSuffixDecoration` — App F.3 l. 1469.
- `tooltip_splitsOnDotSpace` and `tooltip_trailingBangIsWarningSeverity` — App F.3 ll. 1457–1458.

**Values and sliders**
- `valueOption_cycleForwardAndBackwardWrapsModulo`.
- `valueOption_outOfListValueRetainedThenFirstCycleGoesToIndexZero` (`[D-P12-7]`).
- `slider_isDiscreteOverAllowedValuesOnly` — no value outside the list is ever produced, and no
  numeric parsing occurs (`[D-P12-5]`).
- `slider_degradesToValueOptionWithoutContractLoss` — the payload equivalence that makes the OQ-9
  fallback complete.
- `sliders_unknownOrNonVariableEntryYieldsNoSliderAndDoesNotAffectOthers` — App F.3 l. 1470.

**Profiles**
- `profile_labelTracksPendingStateAndFallsBackToCustom` — inference over the *pending* state.
- `profile_clickAppliesWholeOptionConstraintSet`.
- `profile_clickNeverTouchesProgramState` — `!program.*` tokens are inert here (row F4-3).
- `profile_cycleWrapsAndCustomSelectsFirstDeclared`.

**Edit session and persistence**
- `apply_writesChangedOnlySet` and `apply_optionReturnedToDefaultIsRemovedFromFile` — the
  changed-only contract (RESEARCH §4.7 ll. 604–605).
- `apply_writeCompletesBeforeReloadSubmitted` — the ordering the §5.4 request-2 interim assumption
  requires.
- `discard_writesNothingAndEmitsNoReload`; `done_appliesWhenDirtyAndClosesOtherwise`
  (`[D-P12-9]`).
- `reset_writesEmptyChangedSet` (`[D-P12-16]`).
- `persistenceWriteFailure_retainsPendingStateAndPackStaysOn` — §6 row 7.
- `roundTrip_optionsSurviveWriteThenReload` — **the v0.4 exit criterion** (RESEARCH §9 l. 950),
  driven through Phase 3's codecs against a temporary directory.

**Reload classification and coalescing**
- `reload_triggerMatrixIsTotal` — a table-driven test asserting one classification for **every**
  row of §4.7.3, failing on any unclassified trigger.
- `reload_mergeTakesMaxLifecycleAndUnionOfFlags`, `reload_mergeIsAssociative`,
  `reload_mergeIsIdempotentInEffectFields` (`[D-P12-17]`).
- `reload_burstOfEditsCoalescesToOneRequest` — RS-1 as a property.
- `bakeSetPredicate_firesOnVertexAttributeChange`, `..._firesOnSeparateAoChange`,
  `..._firesOnOldLightingChange`, `..._doesNotFireOnUnrelatedOptionChange` (`[D-P12-11]`).
- `reload_resourceReloadRequestsReacquireOnly` (`[D-P12-12]`).
- `reload_fullAlwaysSetsWorldRendererReload`.
- `command_reloadShadersRepliesWithClassificationOnly` and
  `command_reloadShadersRepliesWithInertMessageWhenNoCoordinatorInstalled` — the §4.8.2 reply shape,
  asserted against a `void submit` seam.

**Selection and engine settings**
- `selection_offSentinelAlwaysPresentAndSelectable` — the rung-5 guarantee.
- `selection_orderingIsPhase3sAndIsNotResorted`.
- `selection_staleCandidateIdTriggersRediscoveryBeforeLoad` (`[D-P12-8]`).
- `selection_nonAvailableStatusRendersDisabledWithDiagnostics`.
- `selection_requiresNewerEditionShowsWarningAndStaysOff` — row C-1.
- `selection_modelCarriesNoPathRootLeaseOrUnsanitizedName` and
  `selection_actionSurfaceIsExactlyTheFivePublishedIntents` — the §2.2 `PackSelectionModel` /
  `PackSelectionActions` shapes a view adapter is entitled to rely on.
- `engineSettings_sevenEntriesExactlyAndNoAaOrAf` — the count and the absence, asserted literally.
- `engineSettings_triStateDefaultDefersToPackFlag` (`[D-P12-10]`).
- `engineSettings_roundTripThroughGlobalCodec` and
  `engineSettings_unknownKeyIsPreservedForRoundTrip` — P3 §5.1 ll. 1602–1604.
- `engineSettings_absentOwnerListRendersInertIdentityEntry` — §4.6.2's fallback.
- `engineSettings_wireTokensAreExactlyTheSevenPublishedSpellings` — the §4.6.3 table asserted
  literally, including `TriState` writing `default`/`true`/`false` and never `DEFAULT`/`ON`/`OFF`
  (`[D-P12-19]`).
- `engineSettings_unrecognizedTokenWarnsAndIsTreatedAsAbsent` — `True`, `on`, `1`, `yes` are all
  rejected for a `Toggle` or `TriState`.
- `engineSettings_choiceWritesOwnerListEntryVerbatimAndOmitsAbsentList` — no decoration reaches the
  file, and an unpublished owner list writes no key at all.

**Structural**
- `model_schemaVersionMismatchIsRejectedBeforeDerivation` — I-3, P3 §5.3 ll. 1644–1645.
- `model_schemaGateReadsTheConstantNotALiteral` — the admitted value is
  `PackFrontEnd.CURRENT_SCHEMA_VERSION` (`4` in this Phase 3 revision), so a future Phase 3 bump
  fails a compatibility assertion instead of silently admitting a stale configuration.
- `model_buildNeverThrowsOnAnyMalformedScreenConfiguration` — a fuzz test over screen blocks
  (`[D-P12-18]`).
- `model_isDeterministicAcrossBuilds` — I-4, byte-identical model snapshots.

### 8.2 Conformance-harness tests (`:conformance`, Phase 2)

- **Presentation-model goldens** for the seven App G matrix packs: the resolved model is serialized
  as a manifest (screen ids, entry kinds and order, resolved columns, label keys resolved) and
  diffed. Per §G6's derived-artifacts clause the golden carries **no pack source text**
  (`[D-P2-5]`) and **no images** (`[D-P2-6]`); regeneration is explicit via `-PupdateGoldens`.
- **Round-trip fixture run**: write a changed set for each matrix pack, reload through
  `PackFrontEnd`, assert the values survive — the v0.4 impl gate.
- Packs are downloaded at test time under the §G6 fixture policy; none is committed.

### 8.3 What is *not* headless

Only the view adapters, and only their drawing. The OQ-9 spike (§10) is the manual/dev-environment
procedure that exercises ModularUI; the vanilla view is exercised by the same manual checklist.
Neither view contains a contract decision, so neither is on the critical path for conformance.

---

## 9. Milestone staging

Phase 12's milestone is **v0.4** (DESIGN.md l. 2359); RESEARCH §9 l. 950 puts "options GUI (§7.6)"
and "options round-trip persistence" in v0.4. The table splits *architected now* from
*implemented later* per §G0.3.

| Component | Tag | Note |
|---|---|---|
| Presentation model, `*` expansion, column resolution | `v0.4` | The whole contract surface |
| Lang/label/tooltip resolution | `v0.4` | |
| Switch and value options | `v0.4` | |
| Sliders | `v0.4` | Discrete; degrades to a cycling button if the view cannot draw one |
| Profiles: inference display and click-to-cycle | `v0.4` | |
| Edit session: apply / discard / reset | `v0.4` | |
| Per-pack changed-only persistence | `v0.4` | The v0.4 exit criterion |
| Pack-selection screen incl. sentinels, status, `version.<mcver>` badge | `v0.4` | |
| The seven engine-settings **entries** and their global round-trip | `v0.4` | Each entry's **behavior** lands at its owner's milestone; `normalMap`/`specularMap` are Phase 13 `v0.5`, so those two rows are visible-and-inert through v0.4 (§4.4.4, §4.6.2) |
| Reload triggers: F3+R, `/reloadShaders`, resource listener | `v0.4` | |
| Reload classification + merge algebra + `ReloadCoordinator` call | `v0.4` | Execution is Phase 7's, at Phase 7's milestone |
| `SHADER_GUI` diagnostics panel | `v0.4` | Matches P1 §9 l. 4513 (store at v0.1, routing at v0.4) |
| Vanilla `GuiScreen` view | `v0.4` | Always present; the zero-dependency floor |
| ModularUI view + the dependency declaration | `v0.4`, **conditional on OQ-9** | Not built if the spike fails |
| Presentation-model goldens in `:conformance` | `v0.4` | Needs the Phase 2 harness |
| Engine-setting **behavior** for `renderQuality`/`shadowQuality`/`handDepth`/`oldHandLight`/`oldLighting` | owner's tag | Hand-offs in §11.4 |

Nothing in this phase is `post-v0.5`.

---

## 10. OQ & spike specifications

### 10.1 OQ-9 — ModularUI fitness

**(1) The question, verbatim from RESEARCH.md §11 l. 1015:**

> *ModularUI fitness for generated option screens/sliders/profiles*

with §7.6 ll. 875–878 naming what is unproven: *"dynamically generated screens from `screen.*`
config, sliders bound to option values, profile cycling, tooltip-from-lang plumbing"*, and §G10
l. 872 recording that the fallback risk is ≈ zero so **the spike now purely judges ModularUI
upside**.

**(2) Procedure.** In a dev environment with Cleanroom pinned per Phase 1 and ModularUI declared as
a mod dependency (`[D-P12-14]`), prototype **one** options screen driven by the real
`OptionPresentationModel` built from a real pack's `screen.*` configuration — a matrix pack with
subscreens, a `sliders=` list, and at least two profiles. Implement `OptionScreenView` against
ModularUI and nothing else; the model layer is untouched. Then exercise, in order:

  a. **Container-less client screen.** Open the screen with no inventory, no `Container`, and no
     server round-trip. The target path is `GuiScreenWrapper` / `<T extends GuiScreen & IMuiScreen>`
     — *not* `IGuiHolder` + `GuiManager.open(…, EntityPlayerMP)`, which is server-initiated
     (`[V:mcp]`, §4.10.3). Record whether a client-only open path works without a synthetic
     container.
  b. **Generated screen.** Build the grid from `resolvedColumns` and the entry list at runtime —
     including the auto-widened case (≥ 19 options) and a scrolling screen — with no
     compile-time-fixed layout.
  c. **Slider.** Bind a discrete slider to an option's `allowedValues` with exactly
     `allowedValues.size()` stops. Note that no slider widget appears in the indexed ModularUI
     corpus (§4.10.3), so this sub-step may require composing one from the widget primitives; record
     which.
  d. **Subscreen.** Navigate into and back out of a `[SUBSCREEN]`, with the stack preserved.
  e. **Profile cycling.** Click the `<profile>` entry, observe the label move through the declared
     profiles and to `Custom`, and confirm the whole option batch lands in the pending set.
  f. **Tooltips.** Render a multi-line tooltip from lang, with a trailing-`!` line **red**.
  g. **Cost.** Record the jar-size delta, the added startup time, and the line count of the adapter
     versus the vanilla view.

**(3) Success and failure criteria.**

- **Success** — (a) opens client-only without a synthetic container, **and** (b), (d), (e) all bind
  cleanly, **and** (f) renders the severity distinction. These are the spec's three named criteria
  (DESIGN.md ll. 2391–2393) plus the container-less precondition the MCP survey exposed.
- **Partial** — everything above succeeds but (c) needs a hand-composed slider. **Still adopt**:
  §4.4.3's slider carries the same payload as a value option, so a composed slider is a view
  detail, and a missing one degrades to a cycling button with no contract loss.
- **Failure** — any of: a synthetic `Container` is required to open a client screen; the grid
  cannot be built at runtime from a variable column count; subscreen navigation or profile cycling
  requires model changes; or the adapter costs more than the vanilla view for no user-visible gain.
- **Ruling.** The spike **does not** get to change the presentation model. If ModularUI's shape
  would require altering `OptionPresentationModel`, that is a failure of the spike, not a redesign
  of the model — the model is bound by App F.3/F.4, not by a UI framework.

**(4) Fallback design — already complete.** The vanilla `GuiScreen` view (§4.10.2) ships instead:
two `GuiScreen` subclasses, zero dependencies, structured after two files that are *already working
on 1.12.2* under LGPL-3.0 (PD §14; verified at the source, §3.4). Adopting the fallback costs
exactly one thing — not writing `ModularUiOptionScreens` — and **removes** the ModularUI dependency
declaration entirely. No milestone stalls, which is §G4.4's whole requirement.

**(5) Recording.** Per §G4.4, the result goes back into RESEARCH.md §11's status column for OQ-9 by
the implementation effort, plus an addendum note in this doc.

### 10.2 OQs owned elsewhere that touch this phase

**OQ-12** (GPL-3.0-or-later on an LGPL-2.1 platform with an LGPL-3.0 GUI dependency; jar-in-jar
implications) is **Phase 1's** (§G10 l. 875) and Phase 1 has written its considered note (§10.2).
`[D-P12-14]` selects the arrangement Phase 1's note describes as obligation-free; it does not
re-open OQ-12.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale | Where |
|---|---|---|---|
| `D-P12-1` | `*` placement is by **declaration**, not reachability | App F.4 defines screens by declaration and is silent on reachability; the alternative silently duplicates an orphaned screen's options into the main screen, changing what the author wrote. Orphans are diagnosed instead | §4.3.3 |
| `D-P12-2` | Exactly one `*` wins — the first in build order; later ones expand to nothing, diagnosed | Splitting the set has no contract definition; duplicating it would place an option twice | §4.3.3 |
| `D-P12-3` | Expansion order is `OptionCatalog` order; fallback is a stable sort on name by unsigned UTF-8 byte order | I-4 requires a total order; the fallback matches P3's own ordering vocabulary (P3 §5.1 l. 1506) and is locale-independent | §4.3.3, §5.4 req. 1(c) |
| `D-P12-4` | Ambiguous options are excluded from `*` but render disabled when explicitly placed | Phase 3 disables them (§4.3 ll. 921–923); auto-placing permanently inert rows in every pack's main screen is noise, while an author who *placed* one should see it and its locations | §4.3.3 |
| `D-P12-5` | A slider is a **discrete selector over `allowedValues`** — no interpolation, no numeric parsing; identical payload to a value option | App F.3 l. 1470 is the only contract text and treats values as an opaque ordered list (const options include non-numeric forms). The payload equivalence is what makes the OQ-9 fallback complete. **No working reference exists** — PD §7.4 confirmed at the source (§3.4) | §4.4.3 |
| `D-P12-6` | An unresolved `[SUBSCREEN]` link renders **disabled**, not dropped | A silently missing row hides the authoring error; a disabled row surfaces it | §4.3.1 |
| `D-P12-7` | An out-of-list current value is displayed and retained; the first cycle moves to index 0 | P3 §4.3 l. 942 forbids constraining a safe current value to the advertised list. Rewriting it on open would persist a change the user never made | §4.4.2 |
| `D-P12-8` | Re-run `discover` before acting on any selection | `PackCandidateId` is valid only against the latest generation (P3 §5.1 ll. 1579–1582); the alternative is an avoidable `INVALID_SELECTION` | §4.6.1 |
| `D-P12-9` | **Done applies; Escape/Back-at-root discards; Apply is enabled only when dirty** | Adopted from the working reference (§3.4) and made legible with an unsaved-change count, so a destructive Escape is never a surprise | §4.5.3 |
| `D-P12-10` | Engine settings that shadow an App F.1 flag are **tri-state** (`DEFAULT`/`ON`/`OFF`, wire `default`/`true`/`false`) | App F.1 l. 1448 gives the in-game setting priority "where both exist", and P3 §3.1 l. 693 leaves the resolution to the behavior owner. Tri-state is that rule made executable, so the owner needs no private convention | §4.6.2, §4.6.3 |
| `D-P12-11` | `worldRendererReload` is **predicated** on the bake-set, not unconditional | The reference reloads renderers on every apply and every selection (§3.4) — correct but coarse. Chunk re-meshing is the most expensive act a shader GUI can trigger, nothing contract-visible depends on it (§G4.2), and the predicate is written generally so it survives P3 §11.5 item 2 (ll. 2052–2053) being revisited | §4.7.3 |
| `D-P12-12` | A resource-manager reload triggers **texture re-acquisition only** — never a pack reload | Shader packs live in `shaderpacks/`, outside the resource-pack system; F3+T must not silently recompile a pack | §4.7.3, §4.8.3 |
| `D-P12-13` | F3+R is observed via `InputEvent.KeyInputEvent`, gated on the engine being active; a **separate, rebindable** `KeyBinding` opens the screen | 1.12.2's `KeyBinding` cannot express a chord `[V:mcp]`, and App E.2 l. 1427 forbids a vanilla injection for hook need 11. Gating means the mod never shadows a combination it is not using | §4.8.1 |
| `D-P12-14` | **ModularUI is an ordinary mod dependency, not jar-in-jar** | Mere aggregation carries no LGPL-3.0 redistribution obligation, while `contain` is distribution of the LGPL-3.0 work with notice/modification/relink obligations plus a version-collision risk. It is also the arrangement P1 §10.2 l. 4604 already designed as the fallback. Operator-confirmed at session start | §4.10.3 |
| `D-P12-15` | One locale resolution per model build | A model must never mix languages mid-screen; it also makes the model a pure function of (configuration, locale) for caching and goldens | §4.2, §4.3.5 |
| `D-P12-16` | Reset writes an **empty changed set** rather than deleting the file | Makes "user reset this pack" an explicit persisted state instead of one inferred from a missing file | §4.5.3 |
| `D-P12-17` | Coalescing is a **single-slot merge**, not an unbounded queue | The merge algebra makes "N clicks ⇒ one reload" a provable property (§8.1) and makes RS-1 structural rather than a discipline | §4.7.4 |
| `D-P12-18` | A presentation-model build **never fails** | A malformed `screen.*` block must never make the options screen unreachable — that is rung 5 applied to the GUI | §4.2, §6 |
| `D-P12-19` | The seven engine settings persist **exactly** `true`/`false` (`Toggle`), `default`/`true`/`false` (`TriState`), and the owner's published list entry verbatim (`Choice`); matching is byte-exact and case-sensitive, an unrecognized token warns and reads as absent, and an unpublished `Choice` list writes no key | Phase 3 publishes `EngineOptionData` as an opaque decoded-string map, so the token text has exactly one possible owner — this phase. Phase 3's adjacent App F.1 tri-state spells its states `DEFAULT/TRUE/FALSE` (P3 §3.1 l. 692) against Phase 12's `DEFAULT/ON/OFF`, so an unpublished encoding would let a behavior owner parse a spelling Phase 12 never writes | §4.6.3, §5.1 |

### 11.2 Binding-decision disposition (D-1 … D-10)

None contradicted. Specifically: `D-2` (shaders only) — the seven engine settings are exactly
OF's eight minus the AA/AF non-goal, and no MCPatcher-adjacent surface is added. `D-5` — this phase
authors **no Mixin** (App E.2 l. 1427). `D-6` — the model/view seam is the D-6 seam applied inside
one phase; the whole contract surface is headless-testable. `D-7`/`D-8` — §0.5 and `[D-P12-14]`.
`D-10` — §8's conformance-harness rows.

No contract-visible component is "improved" (§G4.2). The two places this design departs from the
reference — unclamped columns and the predicated renderer reload — are, respectively, a *return* to
the contract and a change to a non-contract internal.

### 11.3 Input contradictions found, and their rulings

1. **PD §14 l. 712 overstates the reference's tooltips.** It reports "lang-file tooltips with
   `en_us` fallback". Direct reading of both cited files finds **no hover-tooltip rendering at
   all**: the `en_us` chain feeds *labels* (`…/VintageShaderPackOptionsScreen.java:324-345`) and the
   option's comment is used as a *label* fallback (ll. 320–322). **Ruling:** the fallback-chain
   technique and prettification are adopted (§4.3.5); the tooltip contract rests on RESEARCH App
   F.3 ll. 1457–1458 alone, which is normative and needs no reference. §G0.1a's rule applies —
   reported, not silently smoothed. DESIGN.md ll. 2398–2399 repeats PD's phrasing, so it is also
   §11.5 item 2.
2. **Phase 3 is not verified, and this document consumed it anyway.** The first build round of this
   doc recorded Phase 3 as verified on `PHASE_3_REVIEW_34.md`'s literal `PASS` and dismissed
   Phase 3's own "not verified" closing line as stale bookkeeping. `PHASE_12_REVIEW_1.md` F-1
   (ll. 32–63) established that this was wrong, and independent re-resolution confirms it: round
   34's `PASS` (l. 46, `Interface changed: no` at l. 48) was superseded by round 35's `FAIL`
   (l. 277, `Interface changed: yes` at l. 279) and then by round 36's `PASS-WITH-CORRECTIONS`
   (l. 270, `Interface changed: yes` at l. 272), whose own verification trigger (ll. 348–352)
   requires *"a fresh whole-document verification round … before Phase 3 can close or be consumed
   by a dependent"*. Under §G1.3 (DESIGN.md ll. 354–359) that is a §5 change outstanding, so
   Phase 3 is **not verified**. **Ruling:** there is no contradiction left to adjudicate — Phase 3's
   closing ledger (`PHASE_3_DOC.md` ll. 2119–2121) and reviews 35/36 agree, and this document
   agrees with both. Phase 3 is recorded in §0.2 as a **hard dependency consumed while unverified**,
   which §G5.3 item 1 (DESIGN.md ll. 659–663) forbids and for which §G5.3 grants no exception; the
   §5.2 rows round 36's corrections touched are marked **⟳** and owe re-derivation once Phase 3
   obtains a fresh literal `PASS` (§11.4, §12 gate). The concrete consequence already visible in
   this revision is the schema constant: it is `4`, not `3` (§4.1 I-3).
3. **Sliders: §G11.5 vs the Phase 12 doc gate.** §G11.5 l. 978 records sliders as "Answered by
   gated Oculus evidence"; the Phase 12 doc gate ll. 2426–2427 says slider handling has **no**
   reference; and OD is absent from Phase 12's Required inputs while §G12.6 ll. 1113–1116 restricts
   its map to briefs that explicitly assign OD. **Ruling:** the Required-inputs list and the doc
   gate govern this session, so sliders are designed from App F.3 alone (`[D-P12-5]`) and OD was
   not read. §11.5 item 1.
4. **RESEARCH's own engine-option count.** §4.7 ll. 608–609 lists "8 engine options (AA, …)" while
   §1.2 l. 80 makes AA/AF a non-goal. This is already resolved upstream — DESIGN.md l. 2374 states
   "AA/AF explicitly do not exist per §1.2" — and this doc implements seven. Recorded as a **note**,
   not an open contradiction, so a verify session sees it was noticed and reconciled.

### 11.4 Open items and hand-offs

- **Phase 7** — implement `ReloadCoordinator` (§5.1) and own the drain point, the executing thread,
  and everything downstream of publication, upholding RS-1 and RS-2 (§4.7.4). Also publish the
  ordered `handDepth` value list (§4.6.2). This obligation is not in Phase 7's spec text; it needs
  adopting regardless of Phase 7's other outcomes (§5.3(B) item 4).
- **Phase 4** — expose the version counter in §5, per DESIGN.md l. 1563 (§5.3(A)).
- **Phase 5** — publish the ordered `renderQuality` multiplier list; **Phase 8** — the same for
  `shadowQuality`. §G11.5 l. 985 records that render-quality multipliers have no reference, so the
  ladders are theirs to derive from RESEARCH. Until published, the rows render inert (§4.6.2).
- **Phase 9** — consume `oldHandLight` as a tri-state; **Phase 10** — the same for `oldLighting`,
  and note that `oldLighting` and `separateAo` are the two bake-time flags in `[D-P12-11]`'s
  predicate.
- **Phase 13** — consume `normalMap`/`specularMap` (they change the macro header, hence a
  `REPUBLISH`), and consume the `resourceReacquire` flag (§4.7.3, `[D-P12-12]`).
- **Phase 2** — the presentation-model golden manifest format (§8.2) is a new golden family; it
  carries no pack source text and no images, per §G6's derived-artifacts clause.
- **Phase 3** — the four sub-requests of §5.4 request 1 and the merge-point closure of request 2.
- **Phase 3 — the re-derivation this document owes.** Phase 3 must complete a fresh whole-document
  verification round (`PHASE_3_REVIEW_36.md` ll. 348–352). The moment it returns a literal `PASS`,
  §5.2's **⟳** rows must be re-derived against the passed text and §0.2's breach note closed. If
  that re-derivation finds a consumed semantic changed, the change lands as a §G1.3 fix-up; if it
  finds the option-model surface reshaped, this doc rebuilds. Nothing in §12 starts before it.
- **Phase 7 — now verified, so §5.3(B) is checkable today.** `PHASE_7_REVIEW_36.md` l. 107 is a
  literal `PASS` with l. 109 `Interface changed: no`, so a fix-up session can compare §5.3(B)'s four
  assumptions against Phase 7's published §5 immediately. Assumption 4 (`ReloadCoordinator`) is the
  one that is a *new* obligation rather than a confirmation.
- **Final integration review (§G5.3 item 4, ll. 680–681)** — "P4's version-counter invalidation vs
  P12's reload paths" is already on the checklist; add the P12 → P7 `ReloadCoordinator` edge, which
  is an interface Phase 12 exposes to a phase that does not depend on it and which per-phase
  verification therefore cannot catch.
- **This doc's fix-up trigger** — if Phase 7's verified doc contradicts any of §5.3(B)'s four
  assumptions, this doc takes a §G1.3 fix-up session, not a rebuild (§G5.3 item 3).

### 11.5 Requested upstream changes

1. **DESIGN.md** — resolve the slider-evidence conflict: either add OD §14 to Phase 12's *Required
   inputs* (making §G11.5 l. 978's "sliders answered" actionable), or correct the doc-gate line at
   ll. 2426–2427 which still states sliders have no reference. Today they contradict each other and
   a build session cannot honor both (§11.3 item 3).
2. **DESIGN.md ll. 2398–2399** — the Phase 12 spec inherits PD §14's "lang-file tooltips with
   `en_us` fallback" description of the Pintonium screens. Direct reading shows the `en_us` chain
   feeds labels and that neither screen renders a tooltip (§11.3 item 1). Suggest "lang-file
   **labels** with `en_us` fallback"; the tooltip requirement itself is unaffected because App F.3
   supplies it.
3. **RESEARCH.md §4.7 ll. 608–609** — the GUI bullet lists "8 engine options (AA, …)" with no
   pointer to §1.2's AA/AF non-goal, so the count reads as 8 until the reader reaches DESIGN.md
   l. 2374. A parenthetical cross-reference would make the Schmaloogium count (7) legible in place.
   Minor, and for that document's maintainer rather than a phase session.

---

## 12. Implementation checklist

Ordered, independently actionable, each with a milestone tag and its test hook.

**Gate before item 1.** Phase 3 is **not verified** (§0.2). No item below may start until Phase 3
returns a fresh literal `PASS` and §5.2's **⟳** rows have been re-derived against the passed text
(§11.4). Everything here is architected against a provisional dependency surface; implementing it
first would bake that provisionality into code.

1. `[v0.4]` Stand up `com.schmaloogium.engine.config` Phase-12 types: `ScreenId`, `OptionId`,
   `Tooltip`/`TooltipLine`, `PresentationEntry` variants, `PresentationScreen`,
   `OptionPresentationModel`. Assert C-1…C-4 still hold with no Minecraft/Forge/LWJGL import.
   Hook: the Phase 1 seam test, plus `model_isDeterministicAcrossBuilds`.
2. `[v0.4]` Implement the model builder's schema/fingerprint gate and the never-fails posture.
   Hook: `model_schemaVersionMismatchIsRejectedBeforeDerivation`,
   `model_buildNeverThrowsOnAnyMalformedScreenConfiguration`.
3. `[v0.4]` Implement placement computation and `*` expansion with `[D-P12-1]`…`[D-P12-4]`.
   Hook: every `optionStar_*` test.
4. `[v0.4]` Implement column resolution including the explicit-count override and the exact
   widening boundary. Hook: every `screenColumns_*` test.
5. `[v0.4]` Implement locale resolution, the four label chains, prettification, prefix/suffix
   decoration, and tooltip splitting with severity. Hook: every `lang_*` and `tooltip_*` test.
6. `[v0.4]` Implement switch/value/slider entries and their transitions, including the out-of-list
   rule and the slider's discrete-only guarantee. Hook: `valueOption_*`, `slider_*`, `sliders_*`.
7. `[v0.4]` Implement the profile entry: inference display over pending state, click-to-cycle,
   option-constraints-only application. Hook: every `profile_*` test.
   *Blocked on* §5.4 request 1(b) or its stated interim assumption.
8. `[v0.4]` Implement `OptionEditSession` — pending overlay, apply/discard/reset, changed-only
   computation, and the write-before-submit ordering. Hook: `apply_*`, `discard_*`, `done_*`,
   `reset_*`, `persistenceWriteFailure_*`, `roundTrip_optionsSurviveWriteThenReload`.
9. `[v0.4]` Implement `ReloadLifecycle`/`ReloadRequest`/`merge` and the §4.7.3 classification table
   as data, so an unclassified trigger is a compile-or-test failure rather than a silent `NONE`.
   Hook: `reload_triggerMatrixIsTotal` and every `reload_*` / `bakeSetPredicate_*` test.
10. `[v0.4]` Implement `EngineSettingsModel` with the seven entries, the tri-state semantics, the
    §4.6.3 wire spellings (`[D-P12-19]`), and the global round-trip through
    `GlobalShaderOptionsCodec`. Hook: every `engineSettings_*` test.
11. `[v0.4]` Implement `PackSelectionModel`/`PackSelectionRow` over `discover` and the
    `PackSelectionActions` intent surface (§2.2), including sentinel ordering (consumed, not
    re-derived), status/kind badges, the `version.<mcver>` badge, and the stale-generation guard.
    Hook: every `selection_*` test.
12. `[v0.4]` Land the Phase 1 `modImplementation` fix (P1 §12 item 43) — §5.4 request 4 — **before**
    any ModularUI work. Hook: a build-configuration test that the dependency resolves at both
    compile and runtime without a double declaration.
13. `[v0.4]` Implement `mod.gui`'s `OptionScreenView` seam and the **vanilla `GuiScreen` view** in
    full (§4.10.2). Hook: manual checklist; no contract test, by construction.
14. `[v0.4]` Implement the three platform bindings: the `InputEvent.KeyInputEvent` F3+R observer
    with its active-engine gate, the rebindable open-screen `KeyBinding`, the
    `ClientCommandHandler` registration for `/reloadShaders` including its §4.8.2 reply, and the
    `ISelectiveResourceReloadListener`. Hook: `reload_resourceReloadRequestsReacquireOnly`,
    `command_reloadShaders*`, plus a manual chord check.
15. `[v0.4]` Implement the `SHADER_GUI` diagnostics panel against Phase 1's per-pack store,
    including full driver logs for compile/link failures. Hook: a `DiagnosticRoutingTest` extension
    asserting `SHADER_GUI` records reach the panel model.
16. `[v0.4]` Wire `ReloadCoordinator` to Phase 7's implementation and verify RS-1/RS-2 end to end.
    Hook: `reload_burstOfEditsCoalescesToOneRequest` plus a Phase 7 integration check that one
    drain publishes one configuration.
17. `[v0.4]` Add the `:conformance` presentation-model goldens and the round-trip fixture run for
    the App G matrix packs, under §G6's derived-artifacts rules. Hook: `-PupdateGoldens` behavior
    test (regeneration must still fail the run it regenerates in).
18. `[v0.4]` **Run the OQ-9 spike** (§10.1) and record the result in RESEARCH.md §11's status
    column plus an addendum here. Only on success: implement `ModularUiOptionScreens` against the
    same `OptionScreenView` seam and declare the ModularUI mod dependency. On failure: delete the
    dependency declaration and ship the vanilla view.

---

*Build session complete per §G1.1 step 3 — a **re-run** of the build against
`docs/phase12/reviews/PHASE_12_REVIEW_1.md`'s `FAIL` (§0.4 item 5). This document is unverified: it
awaits a §G1.2 verify session, which must not share this session's context. It carries two standing
obligations beyond that: the Phase 3 re-derivation owed once Phase 3 obtains a fresh literal `PASS`
(§0.2, §5.2, §11.4), and the §G1.3 fix-up trigger on §5.3(B)'s Phase 7 assumptions, which Phase 7's
round-36 `PASS` now makes immediately checkable (§0.3).*

