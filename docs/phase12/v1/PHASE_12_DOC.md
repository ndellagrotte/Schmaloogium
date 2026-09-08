# Schmaloogium — Phase 12: Options GUI, persistence & reload — Architecture

## 0. Header

**Phase:** 12 — Options GUI, persistence & reload · **Milestone:** v0.4 · **OQs:** OQ-9
**Governing design:** `docs/design/v3/DESIGN.md` (v3, unadopted overall per §G0.4; this build
session was assigned v3 and its Part II Phase 12 spec at ll. 2357–2432).
**Date:** 2026-08-09 · **Session type:** build (§G1.1) — **re-run** against
`docs/phase12/reviews/PHASE_12_REVIEW_1.md`'s `FAIL`, per §G1.3 (see §0.4 item 5).

**Current amendment (2026-09-07):** architecture-only IR-03/05/06/07/08/16/17/24/25
consumer cutover. §5 and its incorporated active contracts changed; this document is
**unverified** and prior reviews certify historical bytes only. Current consumption uses
Phase 3 §5 schema 17 (including catalog-bound state, lossless declarations and the IR-24
codec amendment), Phase 4 generation ownership, Phase 7 reload/atlas orchestration and
Phase 11 direct diagnostics. No implementation, validation or fresh PASS is claimed.
The original reading/status records in §§0.1–0.7 are historical; current adoption/gates are
§§5/11. Additional authority read: RESEARCH §§0–1 and DESIGN v3 Phase 12/13 scopes.

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
   seven engine-settings entries, consuming Phase 3's canonical wire vocabulary.
4. **Edit-session semantics** — pending changes, apply, discard, reset, and *when* the Phase 3
   codecs are invoked.
5. **Reload triggers and their lifecycle classification** — F3+R, `/reloadShaders`,
   resource-manager reload, and every GUI-originated change, each mapped to exactly one lifecycle,
   plus the coalescing rules of the request value handed to Phase 7.
6. **GUI-side error surfacing** — Phase 1's `SHADER_GUI` store and Phase 11's direct immutable projection.
7. **The OQ-9 spike specification** and the ModularUI dependency-arrangement decision.

### 1.2 Adjacent ownership — explicit anti-sprawl boundaries

| Concern touched here | Owned by |
|---|---|
| Option discovery, same-file confirmation, WCC merge, ambiguity, value lists, source rewriting | **Phase 3** (§4.3, §4.8) |
| Persistence **formats** and both codecs (`OptionPersistenceCodec`, `GlobalShaderOptionsCodec`), ISO-8859-1 escaping, atomic write mechanics | **Phase 3** (§4.3 ll. 938–944). We own only *when* they are invoked (P3 §4.3 l. 946) |
| `ScreenModel`/`ProfileModel`/`SliderSet`/`LangDecorations` parsing and column formula | **Phase 3 §5.1**; GUI consumes the exact ordered typed projections |
| Profile inference (`OptionConfiguration.inferProfile`) | **Phase 3 §5.1**; preview Inferred/InvalidState result |
| What a reload *does* internally — recompilation, buffer teardown, uniform rebind | **Phases 4–6** |
| Version-counter invalidation mechanism | **Phase 4** (DESIGN.md ll. 1560–1563) |
| Frame driver, pack/dimension lifecycle, the drain point and thread on which a reload executes, the shaders-off render path | **Phase 7** (DESIGN.md ll. 1850–1886) |
| Behavior of each engine setting: `normalMapEnabled`/`specularMapEnabled` → **Phase 13**; `renderResMul` → **Phase 5** sizing with **Phase 7** viewport; `shadowResMul` → **Phase 8**; `handDepthMul` → **Phase 7**; `oldHandLight` → **Phase 9**; `oldLighting` and pack `separateAo` → **Phase 10** | Phase 3 owns wire validation/projection; Phase 12 owns controls and apply timing |
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
    String title,                      // already decorated; locale selection gated by R-P12-5
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
| F4-4 | Current profile inferred from option values; otherwise **"Custom"** | `OptionConfiguration.inferProfile` over the same-catalog pending preview; Inferred/InvalidState, never runtime source evaluation (§4.5.1) | Phase 3 §5.1; App F.4 |
| F4-5 | `screen=<entries>` main screen | `PresentationScreen` with `ScreenId.MAIN` (§4.3.1) | `[V:doc]` App F.4 l. 1478 |
| F4-6 | `screen.NAME=<entries>` subscreens | `subScreens` map, keyed by declared name (§4.3.1) | `[V:doc]` App F.4 l. 1478 |
| F4-7 | Entry: option names | `SwitchOption`/`ValueOption`/`SliderOption` (§4.3.2) | `[V:doc]` App F.4 l. 1479 |
| F4-8 | Entry: `[SUBSCREEN]` | `SubScreenLink` + navigation stack (§4.3.2, §4.6.2) | `[V:doc]` App F.4 l. 1479 |
| F4-9 | Entry: `<profile>` | `ProfileCycle` (§4.5) | `[V:doc]` App F.4 l. 1479 |
| F4-10 | Entry: `<empty>` | `Blank` — occupies a grid cell, never interactive (§4.3.2) | `[V:doc]` App F.4 l. 1479 |
| F4-11 | Entry: `*` — all unplaced options | **Phase 12's** deferred expansion, specified completely in §4.3.3 (`[D-P12-1]`, `[D-P12-2]`, `[D-P12-3]`) | `[V:doc]` App F.4 l. 1479; deferred to us by P3 §4.3 ll. 953–955 and §4.8 ll. 1251–1252 |
| F4-12 | `screen[.NAME].columns=N`, default 2 | Positive N is configured floor; invoke owner resolvedColumns after expansion (§4.3.4) | Phase 3 §5.1; App F.4 |
| F4-13 | Auto-widens beyond 18 | max(configured floor, ceil(expandedSlotCount/9)); every retained option/profile/subscreen/empty slot counts, including star results | Phase 3 §5.1 |

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
| Lang fallback chain: current game language → `en_us` → literal fallback | Desired resolution policy; **owner publication gated** by R-P12-5 (§4.3.5), not executable from current single LangDecorations | `[V:observed — Pintonium forge122/…/gui/VintageShaderPackOptionsScreen.java:324-345]` |
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
- **I-3 — schema and fingerprint gating.** Admit only
  `schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION` (17 in the current owner amendment);
  reject every other schema before deriving or retaining state, with no inferred upgrade.
  Retain presentation state only while schema, configuration fingerprint and locale match.
  A changed configuration replaces its catalog and invalidates pending catalog-issued preview
  states. Materialization-derived state additionally requires its materialization fingerprint;
  genuine registry-sensitive diagnostics observe Phase 4 generations separately (§5.3).
- **I-4 — determinism.** Two builds from the same configuration produce byte-identical models,
  including entry order, expanded-`*` order, and resolved column counts. This is what makes §8's
  golden tests meaningful and what the ordering rulings in §4.3.3 exist to guarantee.

### 4.2 Screen identity and the model build

`ScreenId` is either the reserved `MAIN` or a declared subscreen name, taken verbatim from P3's
screen model (§G4.1 forbids renaming). The build is a single pass:

1. Reject the configuration unless `schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION` (I-3).
2. Freeze current typed decoration maps once for the build. Locale-indexed resolution is
   unavailable until R-P12-5; §4.3.5 gives the desired chain without consumer file access.
3. Compute the **placement set**: the union of every option id appearing as an option entry in the
   main screen or any declared subscreen (§4.3.3).
4. Compute the **expansion set** and assign it to the winning `*` entry (§4.3.3).
5. For each screen, materialize entries in declared order, substituting the expansion set at the
   `*` position, then resolve columns (§4.3.4).
6. Emit diagnostics for every anomaly encountered (orphaned screens, surplus `*`, unresolved
   subscreen links), all at warning severity — none of them ever fails the build.

After the schema gate, malformed presentation blocks cannot fail the model build: the worst
outcome is a main screen containing surviving entries. A rejected schema yields an unavailable
options view with shaders-off still reachable, not a guessed model.

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

**Expansion order** `[D-P12-3]`. Consume `OptionCatalog.definitions()`'s published immutable
source order (Phase 3 §§4.3/5.1). There is no fallback sort or consumer-invented catalog order.
Expansion-produced options occupy slots before `ScreenModel.resolvedColumns` is called.

#### 4.3.4 Column resolution

Consume Phase 3 §5.1's `ScreenModel.resolvedColumns(expandedSlotCount)` directly
`[D-P12-20]`. Expand the winning `*`, remove invalid entries according to the owner model,
then count the **entire retained presentation array**: ordinary options, applicable subscreen
links, profile entries and `<empty>` each occupy one slot; an empty/surplus star contributes none.
A retained disabled link still occupies its cell. Pass that non-negative count to the resolver:
`max(explicitColumns.orElse(2), ceil(expandedSlotCount / 9))`.

Configured columns are a **floor**, not an unconditional override. Default 18/19/27/28 slots
resolve to 2/3/3/4; explicit 1 with 19 slots resolves to 3, explicit 4 with 19 resolves to 4.
An empty default screen resolves to 2; a layout-only screen still counts all retained slots.

#### 4.3.5 Labels, values, and tooltips

**Locale resolution — owner gate R-P12-5.** The desired per-build chain is client language,
lowercased, then en_us, then literal fallback, retained as the reference-derived policy
(`[V:observed — Pintonium …/VintageShaderPackOptionsScreen.java:324-345]`).
Current P3 §5 publishes **one** `LangDecorations`, not locale-indexed data or a locale selector.
Use its typed maps only as already-projected decorations; do not claim they identify a locale
or implement the chain, and never reopen lang files. Missing typed values use literal fallbacks.
Full locale selection requires the separate future owner publication in §5.4; it is not
silently backfilled into schema17 or claimed complete by this consumer correction.

**Label chain**, per entry kind:

| Entry | Key | Fallback |
|---|---|---|
| option name | `LangDecorations.optionLabels()[name]` | published OptionDefinition.tooltip text if non-empty, else prettified name |
| option value | `value.<NAME>.<val>` | the raw value verbatim |
| profile name | `profile.<NAME>` | prettified `<NAME>`; the "Custom" outcome uses `profile.Custom` then the literal `Custom` |
| screen title / link | `screen.<NAME>`; the main screen uses the bare key `screen` | prettified `<NAME>` |

**Prettification** replaces `_`, `.` and `-` with spaces, lowercases, and upper-cases the first
character of each word `[V:observed — Pintonium …/VintageShaderPackOptionsScreen.java:347-367]`. It
is applied **only** as a last-resort fallback — never over a lang-provided string.

**Value decoration.** `displayValue = prefix.<NAME> + valueLabel + suffix.<NAME>`, with each affix
omitted when absent (App F.3 l. 1469). Decoration applies to the *displayed* value only; the
persisted value is always the raw one (§4.7.2).

**Tooltips.** Use `OptionDefinition.tooltip()` and `LangDecorations.optionComments()`:
the owner preserves decoded terminal `!`, not a precomputed GUI severity. Phase 12 splits
on literal `". "`, marks a resulting line ending in `!` as `TooltipSeverity.WARNING`,
removes only that final marker for display, and otherwise preserves whitespace/punctuation.
Both views render `WARNING` lines red (Phase 3 §5.1; App F.3). The profile/screen comment
maps feed their corresponding tooltips identically.

### 4.4 Widgets and value transitions

All three interactive option kinds share one rule: **a value transition selects an element of the
option's published list; no value is ever invented, interpolated, or clamped into existence.**

#### 4.4.1 Switch options

Two states. `toggle(id)` supplies the opposite `BooleanOptionValue` through the current
catalog's typed update operation; its issued state becomes the pending preview. The codec,
not the widget, owns canonical true/false persistence. Label shows the state.

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

`interactive=false` applies to ambiguous options and unshipped behavior owners. Internal-pack
option edits/reset/profile apply are also inert: current P3 Internal load uses its catalog
baseline and has no safe filesystem target or transient-option input. Display its inspectable
model with a reason; global engine settings still work. No fake internal persistence success.
Programmatic attempts to mutate Internal pack options reject INVALID_REQUEST before I/O.

### 4.5 Profiles

#### 4.5.1 Inference and the label

`ProfileCycle.current` comes from `OptionConfiguration.inferProfile(previewState)`.
The preview is issued by the same `OptionCatalog`, built via `updateState`/`constructState`;
`Inferred(ProfileInference)` supplies selected/custom, while `InvalidState` rejects the edit
without fabricating a profile or partial state. The owner chooses first exact match in
descending expanded-constraint count/source order. Runtime evaluation and materialization
never consume this preview; only a new load's finalized state may affect programs.

#### 4.5.2 Click-to-cycle

Activating a `<profile>` entry advances to the next profile and writes **that profile's whole
expanded option constraint set** into the pending set — the reference's behavior
`[V:observed — Pintonium …/VintageShaderPackOptionsScreen.java:215-223]`. Only the *option*
constraints are applied: `!program.<name>` tokens are Phase 4's input, not a user-editable value,
and Phase 12 never touches program state (row F4-3).

The cycle order is the profile declaration order, with the entry after the last wrapping to the
first. When the current state is `Custom`, the first click selects the **first declared** profile.

Phase 3 §5.1 incorporates `OptionConfiguration.profiles()` in source order and each
`ProfileModel.constraints()` expanded typed option list. Apply the complete batch through
the catalog; any invalid constraint rejects the whole batch. Preserve the profile selection
for Phase 7's new-configuration program-state evaluation, including its `disabledPrograms`;
the GUI never mutates a registry. No missing-profile-publication assumption remains.

#### 4.5.3 Apply, discard, reset

The edit session retains its exact catalog, its issued baseline and a catalog-issued preview.
Pending differences are typed `OptionValue`s (`BooleanOptionValue` for switches,
`TextOptionValue` otherwise), not consumer-constructed `OptionState`s. Each edit uses
`updateState`; batch/profile operations use `constructState` over the complete known-name map.
Invalid results retain the prior preview. Dirty count compares preview with the baseline.

| Action | Effect | Lifecycle (§4.7) |
|---|---|---|
| **Apply** | Validate the same-catalog preview and safe target, invoke the option codec, and only on `COMMITTED` clear pending and submit one request; `FAILED` retains edits and emits no reload | `REPUBLISH` |
| **Done** | Apply if dirty; close only on successful commit or when clean, otherwise retain open edit session and failure | `REPUBLISH` after successful dirty apply, otherwise `NONE` |
| **Escape / Back at the root screen** | Discard: clear pending, no write, no reload | `NONE` |
| **Reset** | Use `catalog.defaultState()` as the preview and commit it through the same safe codec; the codec emits an empty changed set, and only `COMMITTED` submits a request | `REPUBLISH` |

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
  the `DiscoveryGeneration` it rendered. Before refreshing a filesystem selection, retain its
  `FilesystemCandidateReference`, re-run discovery, then use the closed resolver (§4.6.3) to
  obtain a **new** current id; never forward the displayed stale id to `load` `[D-P12-8]`.
  A `Refresh` control follows the same rule.
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

Phase 12 owns controls and apply timing, not a second codec. Phase 3 §5.1 is the sole
canonical key/domain/default authority, amended with schema 17 for IR-24 `[D-P12-21]`.
The seven user controls are:

| Setting | Canonical key | Domain / absent-key value | Behavior owner |
|---|---|---|---|
| Normal map | `normalMapEnabled` | exact `true`/`false`; `true` | Phase 13 preliminary preference |
| Specular map | `specularMapEnabled` | exact `true`/`false`; `true` | Phase 13 preliminary preference |
| Render quality | `renderResMul` | positive finite Java float; `1.0` | Phase 5 sizing / Phase 7 viewport |
| Shadow quality | `shadowResMul` | positive finite Java float; `1.0` | Phase 8 / Phase 5 sizing |
| Hand depth | `handDepthMul` | positive finite Java float; `0.125` | Phase 7 |
| Old hand light | `oldHandLight` | exact `default`/`true`/`false`; `default` | Phase 9 |
| Old lighting | `oldLighting` | exact `default`/`true`/`false`; `default` | Phase 10 |

`TriStateValue.DEFAULT/ON/OFF` maps exactly to wire `default/true/false` and to the behavior
owner's `DEFAULT/TRUE/FALSE`. Explicit user true/false wins over the pack; DEFAULT delegates
to its pack flag, then the behavior owner's documented fallback. Phase 9/10's fallback
decisions are local policy, not newly verified external behavior. `separateAo` has no global
control and remains a pack flag resolved by Phase 10. Only explicit user true emits the P3
`MC_OLD_* 1` macro; default/false omit it, so preprocessor policy is not confused with the
later user-over-pack runtime decision.

Choice ladders remain genuine owner gates: Phase 5/8/7 must publish ordered UI choices.
Until then the row is inert, displaying the decoded current value or the codec default
above (hand depth is **not** an identity multiplier). Do not rewrite a valid out-of-ladder
value on open. A missing ladder prevents GUI edits, not reading a valid codec value.
All controls remain inert until their behavior milestone ships; the independent companion
controls are Phase 13 v0.5. No ladder, AA feature or compatibility alias is invented.

#### 4.6.3 Global persistence and the exact wire spellings

Use the same Phase-3 bundle's `GlobalShaderOptionsCodec` and authenticated
`PersistenceFileAccess`, with the complete known-key baseline
`true,true,1.0,1.0,0.125,default,default,0` in the Phase-3 known-key order. The eighth
key, `antialiasingLevel`, is **reserved exact `0` only**, absent 0, no GUI entry and no
runtime AA. `MC_FXAA_LEVEL` remains omitted. RESEARCH §1.2's no-AA boundary prevails;
any request for a nonzero value requires authority change, not a hidden control.

Keys/tokens are case-sensitive and untrimmed after Properties decoding. The multiplier
grammar and positive-finite-float validation are exactly Phase 3 §5.1; GUI choices write
owner-list tokens without localization or decoration. Old GUI spellings `normalMap`,
`specularMap`, `renderQuality`, `shadowQuality`, `handDepth` are **not aliases** and have no
runtime projection. Unknown-safe keys may survive owner round-trip but acquire no meaning.

Request validation rejects invalid `EngineOptionData` before I/O; do not silently remove
bad entries from a programmatically constructed value. File reads instead classify each
decoded occurrence: typed-invalid warns and is omitted without erasing a prior valid value;
last valid duplicate wins. `ABSENT` returns the valid baseline; `APPLIED` returns its
overlay; `FAILED` returns baseline plus failure. Invalid baseline selects the owner's
canonical empty value, not a fabricated successful read. Writes serialize **all** validated
global entries, including retained unknown-safe entries, in owner order/escaping.
`COMMITTED` alone changes the committed GUI baseline; failed writes retain pending edits
and submit no reload. Per-pack changed-only filtering does not apply to global data.

**Durable pack selection** `[D-P12-22]`:

| Key | Exact decoded value | Absent |
|---|---|---|
| `shaderPack` | `off` or `(internal)` or `FilesystemCandidateReference.canonicalValue()` verbatim | `off` |

The reference is Phase 3 §4.1's `d:` (directory) or `a:` (archive), followed by **every**
UTF-8 byte of the NFC direct-child name encoded uppercase `%HH`, including ordinary ASCII.
Example: `a:%70%61%63%6B%2E%7A%69%70`. Its owner constructor rejects noncanonical text.
The global Properties codec handles outer escaping; neither candidate IDs nor generations,
display labels, host paths, safe targets or access receivers are serialized.

Restore by fresh `discover` and `resolveFilesystemCandidate(reference, currentDiscoveryResult)`:
`Resolved(PackCandidateId candidate)` looks up its row in that same snapshot, proceeds only if
AVAILABLE, then calls `packOptionsTarget(candidate)`;
`Missing`, `Ambiguous`, `KindChanged` and `InvalidSnapshot` each keep shaders off and report
the distinct reason. Malformed reference and unavailable candidate likewise stay off.
No first collision wins. Target `Acquired` alone enables filesystem option I/O;
`Rejected(NULL_CANDIDATE|FOREIGN_DOMAIN|UNKNOWN_CANDIDATE|SUPERSEDED_GENERATION|NON_FILESYSTEM|UNAVAILABLE)`
does no I/O. A target obtained before later discovery remains valid only for its exact
catalog/pack credential; matching durable strings do not authenticate a foreign catalog.
Off/Internal sentinels never request a filesystem target.

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
| Per-pack changed-only options → Phase-3-issued `PackOptionsTarget` (exact direct-child host name plus `.txt`, archive extension retained) | `apply()`, `Done`-while-dirty, `resetToPackDefaults()` | `OptionPersistenceCodec` using same-bundle access and same-pack/catalog-issued state |
| Global settings + current pack selection | any engine-setting change; any pack selection change | `GlobalShaderOptionsCodec` |

Never on hover, navigation, or discard. The persisted per-pack set is the **changed-only** set
(RESEARCH §4.7 ll. 604–605): options whose effective value differs from the option's pack default.
An option edited and then returned to its default is *removed* from the file, not written with its
default value — otherwise the file grows monotonically and stops meaning "what the user changed".

A write failure warns and retains the pending in-memory preview without changing the active pack
or submitting a reload. A successful filesystem apply commits before load; Phase 3 reads that
safe target, validates persisted values against its newly issued catalog and finalizes options
**before** option-sensitive shader analysis. No old preview is passed to materialization.

#### 4.7.3 The trigger × lifecycle matrix

Three ordered lifecycles and two additive flags, per §2.2:

- **`NONE`** — no configuration discovery/load/publication; additive work still executes.
- **`REPUBLISH`** — `PackFrontEnd.load` with unchanged authenticated selection and new
  engine inputs (including the independently computed required companion pair). Filesystem
  options enter through Phase 3's integrated safe codec read. No re-discovery; a stale selection
  is a typed rejection, not a hidden promotion to FULL.
- **`FULL`** — discover then resolve/load, retaining the discovery-versus-republish distinction;
  Phase 7 owns dimension re-initialization and Off transition.
- **`worldRendererReload`** — independent additive P10 geometry invalidation, meaningful with
  a loaded world and executed at quiescence.
- **`resourceReacquire`** — independent additive resource/ID refresh: preserve configuration
  under NONE, drain users before invalidating borrowed objects, rebuild P13 publication and
  P9 resource-derived IDs under the existing configuration, and reset atlas validity.

Republish is required because option changes are compile-time source changes. The current
`SourceMaterializer.materialize` accepts no `OptionState`: it uses exactly the containing
configuration's finalized state and retained same-build macros. No runtime patch/preview route exists.

| Trigger | Lifecycle | worldRendererReload | resourceReacquire | Notes |
|---|---|---|---|---|
| **F3+R keybind** | `FULL` | **yes** | no | Author-facing: re-reads the pack from disk, so discovery must re-run (row C-8) |
| **`/reloadShaders`** | `FULL` | **yes** | no | Identical semantics to F3+R by contract (RESEARCH §4.7 l. 611) |
| **Resource-manager reload** (F3+T, resource-pack change) | `NONE` | no | **yes** | Preserve PackConfiguration; Phase 7 quiesces bindings/ID readers, refreshes resource-derived P13/P9 state and atlas validity without P3 load/discovery |
| **Pack selection changed** (incl. → `Off`, → `(internal)`) | `FULL` | **yes** | no | Also writes the global file. `Off` additionally routes through Phase 7's shaders-off path |
| **Option apply / Done-while-dirty** | `REPUBLISH` | if bake-set changed | no | §4.5.3 |
| **Profile applied** | `REPUBLISH` | if bake-set changed | no | A profile is an option batch; nothing is special about it after the batch lands in pending |
| **Reset to pack defaults** | `REPUBLISH` | if bake-set changed | no | §4.5.3 |
| **Engine setting: `normalMapEnabled` / `specularMapEnabled`** | `REPUBLISH` | no | **yes** | Independent decoded preferences enter P13 preliminary policy before P3 load/jcpp; disabled kinds allocate no companion, macros retain the same-build pair |
| **Engine setting: `renderResMul` / `shadowResMul`** | `REPUBLISH` | no | no | Phase 7 dispatches Phase 5/8 resize with the current owner reason |
| **Engine setting: `handDepthMul`** | `REPUBLISH` | no | no | Phase 7 reads the decoded published value |
| **Engine setting: `oldHandLight`** | `REPUBLISH` | no | no | Phase 9 resolves it against the pack flag |
| **Engine setting: `oldLighting`** | `REPUBLISH` | if effective bake-set changed | no | Phase 10 resolves user-over-pack policy and supplies bake/invalidation behavior |
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

`VertexRequirements.attributes()` retains MC_ENTITY/MC_MID_TEX_COORD/AT_TANGENT. Compare the
same-load Phase-3 requirements and Phase-10 effective `oldLighting`/`separateAo` policy, never
raw wire equality: DEFAULT→explicit value may leave effective lighting unchanged. Phase 7
computes the post-load bake predicate and ORs it with submitted `worldRendererReload` before
publication/admission. This avoids requiring GUI preview state to masquerade as a new configuration.
FULL remains unconditional. Option macros still do not preprocess properties; engine setting
overrides and changed pack properties can nevertheless alter effective bake policy.
Phase 10 owns render-visible lighting/AO and synchronous invalidation, not chunk-renderer
performance rewrites. The coarse reference behavior and its conditional replacement remain
the rationale for `[D-P12-11]`, not permission to skip a required invalidation.

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

Two invariants define reload safety `[D-P12-23]`:

- **RS-1 — one drained request, one final composition outcome.** A burst merges into one
  drain and at most one newly loaded configuration. P4 generation is not a configuration count:
  Ready Accepted adds one; Rejected adds none for that call; RecoveredOff/accepted explicit Off
  adds one; Ready Accepted then compensating Off can add two. Phase 7 reports actual observed
  owner generations and publishes one final composition outcome; it never suppresses a bump.
- **RS-2 — never mid-frame.** Phase 7 stops admission and drains at its quiescent frame boundary,
  then atomically installs the final composition before any new frame. Resource-only NONE follows
  the same quiescence discipline without pretending to publish a new configuration.

Phase 12 owns request classification and merge; Phase 7 owns the exact adapter, drain,
additive execution, failure compensation and final receipts (§5.3). P4-sensitive UI/diagnostic
caches invalidate on every P4 generation event, separately from I-3's configuration/locale cache.
The latest cause is diagnostic only; it cannot erase an effect field or choose a weaker lifecycle.

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
`ReloadRequest(NONE, worldRendererReload=false, resourceReacquire=true, RESOURCE_RELOAD)`.
This listener is a post-replacement notification, **not** a pre-destructive gate.
P7 §4.8.1/§5.1's H-RESOURCE-01 wraps the actual resource-manager replacement, drains old
shader uses and retires acquisition authority before vanilla releases packs, and keeps
admission closed through all listeners. P7 merges this callback into that in-flight
transaction, then refreshes once after the outer body returns; no second queued reacquisition.
Outside a destructive replacement (for example listener registration), the notification
may queue ordinary NONE work. The selective predicate may ignore non-texture notifications,
but cannot substitute for or bypass H-RESOURCE-01's lifetime gate (`[D-P12-12]`, §4.7.3).

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

**Direct expression diagnostics** `[D-P12-24]`: Phase 11 additionally publishes the immutable,
source-free `ExpressionDiagnosticGuiSnapshot(packFingerprint, configurationFingerprint,
attemptSerial, outcome, entries)`, outcome `ACCEPTED|REJECTED`; each entry carries
`stableId, kind, severity (WARNING|ERROR), declarationName, summary`. Phase 11 constructs
summary from its whitelist kind template, never raw expression/source/span/path/dependency-chain
text. Both existing P11 channels (`CHAT_AND_LOG`, `LOG_ONLY`) are eligible; this is a direct
view input, not a conversion to SHADER_GUI or a fourth channel. Phase 7 publishes once for the
final load attempt and exposes the immutable snapshot to this panel. Selection/explicit off
clears it; a same-pack rejected attempt remains labelled REJECTED, never as active-plan data.
Identity and attempt serial prevent an old pack's late result from replacing current diagnostics.
The panel combines displayed counts/severities with P1 issues without mutating either producer.

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
| `OptionPresentationModel`, `PresentationScreen`, `PresentationEntry` (5 variants + `Blank`), `Tooltip`, `TooltipLine`, `ScreenId`, `OptionId` | Immutable resolved screen tree: expanded stars, resolved columns, current typed decorations and split/severity-tagged tooltips. Deterministic under I-4; locale selection explicitly R-P12-5-gated (§4.3.5) | two view adapters; Phase2 headless presentation projection |
| `OptionEditSession`, `ApplyOutcome` | Pending-change overlay with `toggle`/`cycle`/`setValueIndex`/`cycleProfile`/`apply`/`discard`/`resetToPackDefaults`. `apply` invokes Phase 3's codec and yields at most one `ReloadRequest`. Never mutates a published configuration | the view adapters |
| `ReloadLifecycle`, `ReloadRequest`, `ReloadCause`, `ReloadRequest.merge` | Exact §2.2 values and §4.7 max-lifecycle/independent-OR algebra; cause is diagnostic only | Phase 7 adapter/drain |
| `ReloadCoordinator` — `void submit(ReloadRequest request)` | Phase 7 implements and installs this `engine.config` seam; RS-1 means one drain/final outcome, not one P4 bump; RS-2 requires quiescence | Phase 7 |
| `EngineSettingsModel`, `EngineSettingEntry`, `TriStateValue` | Seven controls consuming Phase 3 schema-17 canonical keys and exact §4.6.2–4.6.3 domains, baseline, user-over-pack tri-state priority and no-AA reserved-zero rule; no old spelling aliases | Phases 5/7/8/9/10/13 |
| `PackSelectionModel`, `PackSelectionRow`, `PackSelectionActions` | The selection screen's view model over `PackDiscoveryResult` — kind badge, status, compatibility badge, per-candidate diagnostics, current selection, last-action summary — plus `PackSelectionActions`, the **closed** set of intents a view reports back: select-candidate (by `PackCandidateId`), refresh, open-folder, open-options, close. **All three are declared in §2.2**, so a view adapter implements `showPackSelection` from the published shape and invents nothing. Contains no path, no root, no archive lease, and no unsanitized name (P3 §5.1 ll. 1578–1579, 1582) | the view adapters |
| Persisted pack selection — `shaderPack` | §4.6.3 exact Off/Internal tokens or Phase-3 `FilesystemCandidateReference.canonicalValue()`; fresh discovery and all five closed resolver outcomes before safe-target acquisition | Phase 7 restart/selection |
| `OptionScreenView` | The entire view seam (§2.2, §4.10.1). Implementing it is the whole cost of swapping UI frameworks — this is the OQ-9 hedge stated as an interface | `mod.gui` implementations only |
| Direct `ExpressionDiagnosticGuiSnapshot` input | Phase 11 shape and source-free policy in §4.9; Phase 7 publishes the final attempt and owns pack-scoped lifetime; no SHADER_GUI conversion/new channel | Phase 12 panel |
| Programmatic option adapter | §5.3(D) applies the same catalog validation, explicit safe persistence, classification and queue as GUI, without rendering a view | Phase 2 through Phase 7 |

Nothing here exposes a Minecraft, Forge, Mixin or LWJGL type: every row above `OptionScreenView`
lives in `:engine` and satisfies seam constraints C-1…C-4 (P1 §5.1 l. 4199).

### 5.2 Consumed from Phase 3 — current owner-designed, receiver-adopted, unverified

This inventory replaces the old open publication/merge assumptions. Consume the complete
`docs/phase3/v1/PHASE_3_DOC.md` §5.1 declarations and incorporated §§2.2/4.3 semantics:

| Owner contract | Exact adoption |
|---|---|
| `PackFrontEnds.create()` / `PackFrontEndServices` | One bundle/authentication domain supplies front end and codecs; only bundle-issued safe access, no consumer implementation or mixed receivers |
| Discovery/reference/target | Preserve owner ordering and limits; IDs/generations nonserializable; §4.6.3 adopts durable canonical reference, five resolution outcomes, acquisition rejection and exact target/catalog pairing |
| `PackLoadRequest` / `PackLoadResult` | REPUBLISH loads without discovery; FULL discovers/resolves/loads. Required companion pair immediately follows engineOptions; no missing-value fallback on non-Off. Off short-circuits. Filesystem options load through safe persistence before option-sensitive work |
| `PackConfiguration` / source materializer | Symbolic CURRENT_SCHEMA_VERSION (=17), no inferred older-schema defaults; configuration/materialization fingerprints and same-build finalized option/macro retention |
| `OptionDefinition` / `OptionCatalog` / `OptionState` | Definition source order, typed Boolean/text values, availability/default/allowed list/tooltip/occurrences; catalog-issued complete defaults and construct/update/validate with closed failures; safe out-of-list values warn and survive |
| `OptionConfiguration` / profiles | `profiles()` source order, expanded `constraints()` and `disabledPrograms()`; preview `inferProfile` returns Inferred/InvalidState; GUI changes options only, P7 runtime evaluates the new configuration and selected profile |
| `ScreenModel` / `SliderSet` / `LangDecorations` | Ordered entry algebra; deferred star expansion; all expanded retained slots counted, configured floor; typed decoration maps and retained tooltip marker processed by GUI |
| Option persistence | Validate access domain, target's exact catalog pack credential and state's exact catalog identity before I/O. InvalidRequest has no state; Completed ABSENT/FAILED retains validated baseline. Writer computes changed-only against catalog defaults. GUI reload only after COMMITTED |
| Global persistence / `EngineOptionData` | §4.6.3 owner baseline-overlay/result matrix, last-valid duplicate, all-entry writing and exact codec domains; GUI owns no alternate parsing |
| Requirements / flags / compatibility / failure | Same-build vertex sets; P10 resolved lighting/AO bake policy; compatibility warning; P3 closed failures and primary sanitized diagnostic, never caller path/exception prose |

The older R1 option-model and R2 load-merge requests are **fulfilled architecturally** by
current P3 §5; this document adopts them, unverified. The current IR-24 meaning change
requires schema 17; schema-16 data is not silently upgraded. Fresh owner and receiver
whole-document reviews remain required before implementation consumption.

### 5.2b Consumed from Phase 1 — owner-designed, receiver-adopted, unverified

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

### 5.3 Cross-owner adapters — adopted architecture, unverified

**(A) Phase 4 generation.** P4 already exposes its publisher generation. P7's
`RegistryGenerationListener.changed(long generation)` notifies P12 synchronously for **every**
mutation before frame admission, including Ready then compensating Off. Registry-sensitive
diagnostic caches discard entries on inequality and never infer generation from configuration
count. I-3 presentation caches remain independently keyed by configuration/schema/locale.
Rejected publication changes no generation for that call; any subsequent compensation is a
separate real event. P12 neither increments nor suppresses the counter.
Listeners only invalidate detached caches, never reenter reload or GL. Failed listener delivery
leaves that cache unavailable until it polls authoritative P4 generation, not stale-but-usable.

**(B) Phase 7 reload translation/drain.** P7 implements `ReloadCoordinator.submit` and keeps
its internal intent/reasons vocabulary private. Translate **effect fields**, not the latest
cause: NONE performs no P3 work, REPUBLISH load only, FULL discovery→reference resolution→load.
For every §4.7.3 row preserve independent world-renderer and resource flags through merges.
The pure NONE/false/false request is view-only. Resource NONE/false/true keeps the exact
configuration, quiesces bindings/shadow/ID/geometry readers, refreshes resource-backed P13/P9
state and current atlas validity, then atomically readmits the composition. Any failure follows
P7 compensated-off behavior, not prior-pipeline reuse. No RESOURCE_RELOAD reason may implicitly
promote this to FULL or force a P3 refresh.

P7 owns the thread and single pending drain, computes the post-load P10 bake predicate and
ORs it with the submitted flag. Each drained effect yields one final
`ReloadDrainReceipt(long drainSerial, ReloadRequest effects, ReloadStatus finalOutcome,
List<Long> observedRegistryGenerations)`. `ReloadOutcomeSource.latest()` returns
`Optional<ReloadDrainReceipt>`; the generation list records actual chronological P4 events,
including multiple events on compensation, not a synthesized counter. GUI command submission
acknowledges queuing only; final outcome is observed through the receipt.

**(C) Phase 11 diagnostics.** Consume the exact immutable
`ExpressionDiagnosticGuiSnapshot(String packFingerprint,String configurationFingerprint,
long attemptSerial,ExpressionDiagnosticAttemptOutcome outcome,List<ExpressionDiagnosticGuiEntry> entries)`,
with positive attempt serial and outcome ACCEPTED/REJECTED for the **final P7 composition**.
No expression compile attempt means no projection. Phase 7 publishes it once for that attempt;
pack switch/explicit off clears it. Same-pack rejected attempts remain visibly rejected, never
active-plan success. §4.9 specifies the exact fields, redaction and direct-view routing.
P7 installs `ExpressionDiagnosticGuiSource.current() -> Optional<ExpressionDiagnosticGuiSnapshot>`
into the P12 diagnostics presenter at construction. The presenter reads it on present/refresh
with `ReloadOutcomeSource`, not from a new per-frame participant. Empty clears its view.
It rejects different-pack/older-serial values; shutdown clears as explicit Off does.

**(D) Phase 2 programmatic bridge through Phase 7.** P7 exposes
`ProgrammaticOptionBridge.current() -> Optional<ProgrammaticOptionSnapshot>` with immutable
snapshot `(PipelineIdentity,OptionConfiguration,EngineOptionData)`, absent when no editable
configuration exists; `apply(PipelineIdentity expected,Map<String,String> packOptions,
Map<String,String> engineOptions) -> ProgrammaticApplyResult`.
P12 implements the validation/apply adapter behind it. Exact case-sensitive pack names resolve
through the current catalog; switch text decodes only true/false, other values become safe
TextOptionValue, batch construction validates the complete state. Engine edits accept only
the canonical user-control keys in §4.6.2; no aliases/AA/unknown-key control. P7 checks expected
current pipeline identity before accepting the batch. Closed results are
`Queued(ReloadToken token,OptionPersistenceReceipt persistence)`,
`Rejected(INVALID_REQUEST|STALE_CONFIGURATION|UNKNOWN_OPTION|INVALID_VALUE|UNKNOWN_ENGINE_KEY|SHUTTING_DOWN)`
or `FailedPersistence(FailureId failure,OptionPersistenceReceipt persistence)`.
`OptionPersistenceReceipt(WriteDisposition pack,WriteDisposition global)` records each domain
as `UNCHANGED|COMMITTED|FAILED|NOT_ATTEMPTED`; P7 owns these exact result types.

This bridge **explicitly persists like GUI apply**; it is not an ungranted transient P3 option
input. Validate both domains completely first, serialize changed pack then global writes,
skip unchanged domains, and enqueue one merged request only after all required writes commit.
Failure keeps the complete pending overlay and reports each domain's committed/failed/not-attempted
state, performs no reload and makes no cross-file atomicity/rollback claim. Retrying writes the
same complete desired values. P2 uses isolated copied game/shaderpacks roots and polls the
returned token/final receipt. A P3 new load, never the preview, produces same-build sources.

### 5.4 Dependency request disposition

**Requests 1–2 — Phase 3 option model / persisted merge:** fulfilled and adopted in §5.2,
unverified; no interim OptionState input or missing-model fallback survives.

**Request 3 — Phase 4 counter:** already published; adopted through P7's generation listener
in §5.3(A), not an outstanding request to create a counter.


**Request 4 — Phase 1: land the `modImplementation` fix.** P1 §11.3 item 2 records that the
template declares only `modCompileOnly`/`modRuntimeOnly` — *no `modImplementation` configuration
exists* — and P1 §12 item 43 is the fix; P1 §11.4 l. 5169 anticipated that it "will likely bite you
first". `[D-P12-14]`'s mod-dependency arrangement needs it. This is a restatement of an
already-recorded Phase 1 work item, not a new interface request; no Phase 1 §5 row changes.

**R-P12-5 — Phase 3 locale projection (genuinely ungranted).** Replace the single
`OptionConfiguration.lang()` component in a future owner/schema amendment with
`Map<String,LangDecorations> localizedDecorations` and its exact accessor. Keys are the
owner-normalized locale codes in canonical unsigned-UTF-8 order; values retain the existing
typed decoration-map semantics and are deeply immutable. Empty map means no lang files,
missing key means no such locale. P3 owns parsing, duplicate/error policy and canonical
fingerprinting of every locale payload. No consumer parser or second competing lang authority.
P12 then resolves locale→en_us→literal from that map at one model build. Until the owner grant
and next schema, §4.3.5 uses only current projected values and explicitly gates locale selection.
This newly identified shape gap is separate from fulfilled historical requests 1–2.

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
| Current decoration map/key absent | Use literal fallback (§4.3.5); a pack with no decoration data remains usable. Locale-chain behavior requires R-P12-5, not a consumer lang-file reader | 2a |
| Current option value is not in the advertised list | Displayed and retained; first cycle moves to index 0 (`[D-P12-7]`). Never silently rewritten | 2a |
| **Per-pack or global persistence write fails** | Warn; **retain the in-memory state**; keep the pending set intact so the user can retry; never turn the pack off, never lose the edit. Matches P3 §6 l. 1701 | 2a |
| `PackFrontEnd.load` returns `Failed` | P7 owns final Failed/compensated-Off outcome; display its sanitized failure and Off state, never continue the prior pipeline as active. An inspectable detached prior model carries no runtime authority | 4 |
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
| Key-input, command, and resource-listener bindings | Client thread; build a request and call `ReloadCoordinator.submit`. Resource callbacks are notifications inside P7 §4.8.1's separately owned synchronous replacement gate, not its prelude or permission to replace resources |
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
  (I-4), retaining owner source order without a fallback sort (`[D-P12-3]`).
- `optionStar_excludesAmbiguousOptions` and `optionStar_includesReferencedConstWithoutScreen` —
  the eligibility predicate, both halves (`[D-P12-4]`, App F.3 ll. 1465–1466).

**Columns**
- `screenColumns_configuredFloorAfterExpansion` — explicit1 with19 retained slots→3, explicit4→4.
- `screenColumns_expandedSlotBoundaries` — default18/19/27/28 slots→2/3/3/4.
- `screenColumns_navigationProfileAndEmptySlotsCount` — navigation/layout can cross a widening
  boundary without adding an ordinary option; surplus star contributes no slot.

**Entries and labels**
- `screenEntries_allFiveKindsPlusEmpty` — every App F.4 entry form materializes.
- `screenEntries_emptyOccupiesACellAndIsNotInteractive`.
- `screenEntries_unresolvedSubscreenLinkRendersDisabled` (`[D-P12-6]`).
- `lang_projectedMapsThenLiteralFallback` — current published shape; separately gated
  R-P12-5 future locale→en_us→literal conformance, not runnable schema17 coverage.
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
- `apply_committedWritePrecedesReload` — failed write preserves preview and active config;
  committed filesystem state is read by the new atomic load before same-build materialization.
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
- `engineSettings_missingLadderPreservesDecodedCurrentValue` — inert display keeps valid current
  float text and handDepthMul absent default0.125; no identity rewrite.
- `engineSettings_ownerCodecRoundTripAndDuplicatePrecedence` — explicit tri-state wire,
  invalid duplicate does not erase valid prior value, unknown-safe entries survive all-entry write.
- `selection_durableReferenceOutcomesNeverChooseFirstCollision` — all resolver outcomes, same
  sanitized label with distinct reference, stale snapshot and kind change retain shaders off.
- `programmatic_partialPersistenceDoesNotQueueReload` — pack commit/global failure reported,
  pending overlay retained, no false rollback; retry commits and polls final P7 result.
- `diagnostics_expressionRejectedAttemptIsNotActiveSuccess` — selected-pack/attempt replacement,
  no source leakage, no extra channel, and explicit Off clears direct projection.
- `reload_compensatingOffInvalidatesBothRegistryGenerations` — observe Ready then Off events
  without equating them to two configurations or suppressing the second invalidation.

**Structural**
- `model_schemaVersionMismatchIsRejectedBeforeDerivation` — I-3, P3 §5.3 ll. 1644–1645.
- `model_schemaGateRejectsPreviousSchema` — CURRENT_SCHEMA_VERSION17 accepted, all earlier
  schemas including16 rejected before retaining presentation state; no fabricated codec defaults.
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
| Seven engine-settings entries and global round-trip | `v0.4` | Independent normalMapEnabled/specularMapEnabled behavior lands at P13 v0.5; entries are inert before behavior availability |
| Reload triggers: F3+R, `/reloadShaders`, resource listener | `v0.4` | |
| Reload classification + merge algebra + `ReloadCoordinator` call | `v0.4` | Execution is Phase 7's, at Phase 7's milestone |
| `SHADER_GUI` diagnostics panel | `v0.4` | Matches P1 §9 l. 4513 (store at v0.1, routing at v0.4) |
| Vanilla `GuiScreen` view | `v0.4` | Always present; the zero-dependency floor |
| ModularUI view + the dependency declaration | `v0.4`, **conditional on OQ-9** | Not built if the spike fails |
| Presentation-model goldens in `:conformance` | `v0.4` | Needs the Phase 2 harness |
| Engine-setting behavior for renderResMul/shadowResMul/handDepthMul/oldHandLight/oldLighting | owner's tag | Canonical codec and hand-offs §§4.6/11.4 |

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
| `D-P12-3` | Consume OptionCatalog.definitions() source order without fallback sort | Current P3 owner publishes the exact ordering | §4.3.3 |
| `D-P12-4` | Ambiguous options are excluded from `*` but render disabled when explicitly placed | Phase 3 disables them (§4.3 ll. 921–923); auto-placing permanently inert rows in every pack's main screen is noise, while an author who *placed* one should see it and its locations | §4.3.3 |
| `D-P12-5` | A slider is a **discrete selector over `allowedValues`** — no interpolation, no numeric parsing; identical payload to a value option | App F.3 l. 1470 is the only contract text and treats values as an opaque ordered list (const options include non-numeric forms). The payload equivalence is what makes the OQ-9 fallback complete. **No working reference exists** — PD §7.4 confirmed at the source (§3.4) | §4.4.3 |
| `D-P12-6` | An unresolved `[SUBSCREEN]` link renders **disabled**, not dropped | A silently missing row hides the authoring error; a disabled row surfaces it | §4.3.1 |
| `D-P12-7` | An out-of-list current value is displayed and retained; the first cycle moves to index 0 | P3 §4.3 l. 942 forbids constraining a safe current value to the advertised list. Rewriting it on open would persist a change the user never made | §4.4.2 |
| `D-P12-8` | Re-run `discover` before acting on any selection | `PackCandidateId` is valid only against the latest generation (P3 §5.1 ll. 1579–1582); the alternative is an avoidable `INVALID_SELECTION` | §4.6.1 |
| `D-P12-9` | **Done applies; Escape/Back-at-root discards; Apply is enabled only when dirty** | Adopted from the working reference (§3.4) and made legible with an unsaved-change count, so a destructive Escape is never a surprise | §4.5.3 |
| `D-P12-10` | Engine settings that shadow an App F.1 flag are **tri-state** (`DEFAULT`/`ON`/`OFF`, wire `default`/`true`/`false`) | App F.1 l. 1448 gives the in-game setting priority "where both exist", and P3 §3.1 l. 693 leaves the resolution to the behavior owner. Tri-state is that rule made executable, so the owner needs no private convention | §4.6.2, §4.6.3 |
| `D-P12-11` | `worldRendererReload` is **predicated** on the bake-set, not unconditional | The reference reloads renderers on every apply and every selection (§3.4) — correct but coarse. Chunk re-meshing is the most expensive act a shader GUI can trigger, nothing contract-visible depends on it (§G4.2), and the predicate is written generally so it survives P3 §11.5 item 2 (ll. 2052–2053) being revisited | §4.7.3 |
| `D-P12-12` | Resource reload preserves PackConfiguration; additive quiescent resource/ID refresh only | F3+T is not shader-pack rediscovery/recompilation; P7 owns reader draining and coherent resource replacement | §§4.7/5.3 |
| `D-P12-13` | F3+R is observed via `InputEvent.KeyInputEvent`, gated on the engine being active; a **separate, rebindable** `KeyBinding` opens the screen | 1.12.2's `KeyBinding` cannot express a chord `[V:mcp]`, and App E.2 l. 1427 forbids a vanilla injection for hook need 11. Gating means the mod never shadows a combination it is not using | §4.8.1 |
| `D-P12-14` | **ModularUI is an ordinary mod dependency, not jar-in-jar** | Mere aggregation carries no LGPL-3.0 redistribution obligation, while `contain` is distribution of the LGPL-3.0 work with notice/modification/relink obligations plus a version-collision risk. It is also the arrangement P1 §10.2 l. 4604 already designed as the fallback. Operator-confirmed at session start | §4.10.3 |
| `D-P12-15` | Freeze decoration context per model build; locale-chain policy is R-P12-5-gated | Current LangDecorations lacks locale selection; no fabricated provenance or consumer parser | §§4.2/4.3.5/5.4 |
| `D-P12-16` | Reset writes an **empty changed set** rather than deleting the file | Makes "user reset this pack" an explicit persisted state instead of one inferred from a missing file | §4.5.3 |
| `D-P12-17` | Coalescing is a **single-slot merge**, not an unbounded queue | The merge algebra makes "N clicks ⇒ one reload" a provable property (§8.1) and makes RS-1 structural rather than a discipline | §4.7.4 |
| `D-P12-18` | Malformed presentation blocks cannot fail a schema-admitted model | Schema mismatch is separately rejected; off remains reachable | §§4.2/6 |
| `D-P12-19` | GUI emits owner-validated exact wire tokens; invalid file occurrences do not erase prior valid values | P3 owns codec validation and duplicate/result rules; no consumer parser or alias table | §4.6.3 |
| `D-P12-20` | Configured columns are a floor over all expanded slots | Adopt current ScreenModel rather than superseded option-only semantics | §4.3.4 |
| `D-P12-21` | Canonical P3 schema-17 engine vocabulary, tri-state priority and reserved zero-only AA field | No AA feature; keep optional UI ladders gated rather than inventing ranges/defaults | §4.6 |
| `D-P12-22` | Persist canonical FilesystemCandidateReference, never display names or first collisions | Durable resolution and safe target are P3's authenticated restart bridge | §4.6.3 |
| `D-P12-23` | Coalesce one drain/final composition outcome; invalidate on every independent P4 generation | Compensation may publish Ready then Off, so config counts cannot stand in for cache generations | §§4.7/5.3 |
| `D-P12-24` | Direct immutable P11 diagnostics projection via P7 final-attempt publication | Preserve source-free channel/lifetime ownership without a fourth channel | §§4.9/5.3 |
| `D-P12-25` | Programmatic applies use explicit safe persistence and identical reload algebra | P2 gets executable owner semantics, not a GUI simulation or transient-state bypass | §5.3(D) |
| `D-P12-26` | Re-derived lang/Internal boundaries are explicit owner gates, not guessed adapters | Current single LangDecorations cannot select locales; Internal has no persisted option input. Gate locale publication and disable unavailable Internal pack mutations while preserving global controls | §§4.3.5/4.4.4/5.4 |

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
2. **Dependency history is not current clearance.** The older consumption breach and its
   review quotations remain in §0.2. Current P3 §5 closes the model/merge surface and is
   re-derived in §5.2 with schema 17; its newer meaning and this receiving amendment remain
   unverified. Historical PASS cannot certify these bytes or waive implementation gates.
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

- **IR-03/16/25 — Phase 3:** §5.2 adopts current catalog-issued state, profiles, expanded-slot
  resolver, same-build options and safe persistence/reference outcomes. Current owner and
  receiver whole-document verification remain required; no open old-shape fallback.
  R-P12-5 remains genuinely ungranted: current single LangDecorations cannot realize a
  locale-indexed selection chain. §4.3.5 states the reachable baseline without claiming parity.
- **IR-06/07 — Phase 7/4:** §§4.7/5.3 adopt ReloadCoordinator, exact effect translation,
  additive quiescent resource/ID refresh, one final receipt and all actual P4 generation events.
  P7 owns execution and P4 owns generations; no configuration-count equivalence.
- **IR-06 — Phase 2:** §5.3(D) programmatic adapter explicitly persists in isolated harness
  roots, reports partial write failure, and polls P7 token/final outcome; §8.2 presentation
  goldens remain source-free artifacts, not a claim of executed conformance.
- **IR-05/08/24 — Phases 3/5/7/8/9/10/13:** §4.6 canonical keys/defaults/tri-state priority,
  independent pre-load normal/specular preferences and no-AA zero-only reserved field.
  Ordered renderResMul/shadowResMul/handDepthMul UI ladders remain unpublished/inert gates;
  codec-valid persisted values still read without silent rewriting.
- **IR-17 — Phases 11/7:** §§4.9/5.3(C) direct source-free immutable expression diagnostics,
  final-attempt outcome and selected-pack lifetime, no store/channel invention.
- **Remaining gates:** fresh whole-document owner/consumer review, actual implementation and
  conformance, OQ-9 ModularUI spike/dependency work, and the separate §11.5 authority requests.
  None is closed by this architecture-only amendment.


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

**Gate before item 1.** Current owner and receiver §5 amendments are unverified (§§0/5/11).
Fresh whole-document verification must certify the adopted current schemas and interfaces before
implementation consumption. Historical reviews and resolved old publication requests do not waive it.

1. `[v0.4]` Stand up `com.schmaloogium.engine.config` Phase-12 types: `ScreenId`, `OptionId`,
   `Tooltip`/`TooltipLine`, `PresentationEntry` variants, `PresentationScreen`,
   `OptionPresentationModel`. Assert C-1…C-4 still hold with no Minecraft/Forge/LWJGL import.
   Hook: the Phase 1 seam test, plus `model_isDeterministicAcrossBuilds`.
2. `[v0.4]` Implement the model builder's schema/fingerprint gate and the never-fails posture.
   Hook: `model_schemaVersionMismatchIsRejectedBeforeDerivation`,
   `model_buildNeverThrowsOnAnyMalformedScreenConfiguration`.
3. `[v0.4]` Implement placement computation and `*` expansion with `[D-P12-1]`…`[D-P12-4]`.
   Hook: every `optionStar_*` test.
4. `[v0.4]` Invoke the owner column resolver after expansion over all retained slots, preserving
   configured floor and exact widening boundaries. Hook: every `screenColumns_*` case.
5. `[v0.4]` Implement locale resolution, the four label chains, prettification, prefix/suffix
   decoration, and tooltip splitting with severity. Hook: every `lang_*` and `tooltip_*` test.
6. `[v0.4]` Implement switch/value/slider entries and their transitions, including the out-of-list
   rule and the slider's discrete-only guarantee. Hook: `valueOption_*`, `slider_*`, `sliders_*`.
7. `[v0.4]` Implement the profile entry: inference display over pending state, click-to-cycle,
   option-constraints-only application. Hook: every `profile_*` test.
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
15. `[v0.4]` Render P1 SHADER_GUI and the separate P11 direct immutable projection, with P7
    final-attempt/scoping lifetime and no fabricated routing channel. Hook: selected-pack and
    rejected-attempt diagnostic cases in §8.
16. `[v0.4]` Wire P7 reload/programmatic bridges, persistence receipts and every P4 generation
    invalidation; prove one drain/final composition outcome and resource-only configuration
    preservation, including Ready-then-compensating-Off. Hook: §8 reload/programmatic cases.
17. `[v0.4]` Add the `:conformance` presentation-model goldens and the round-trip fixture run for
    the App G matrix packs, under §G6's derived-artifacts rules. Hook: `-PupdateGoldens` behavior
    test (regeneration must still fail the run it regenerates in).
18. `[v0.4]` **Run the OQ-9 spike** (§10.1) and record the result in RESEARCH.md §11's status
    column plus an addendum here. Only on success: implement `ModularUiOptionScreens` against the
    same `OptionScreenView` seam and declare the ModularUI mod dependency. On failure: delete the
    dependency declaration and ship the vanilla view.

---

*Architecture integration amendment complete; §5 changed and this document remains unverified.
Prior build/review history remains in §0. Current adopted owner contracts and remaining gates
are §§5/11; no implementation, test execution, fresh PASS or integration clearance is claimed.*

