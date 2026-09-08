# Phase 7 — Review 38

## 1. Reviewed artifact and qualification

- **Target:** `docs/phase7/v1/PHASE_7_DOC.md`, complete document, §§0–12.
- **Frozen SHA-256:** `6e1ff8a9b6775b58d99a326115717bc7bf91b2b849d5caf038c1b035c4f75a89`, confirmed by Main.
- **Governing design:** the target-header-selected `docs/design/v2.0-RC3/DESIGN.md`, not a globally substituted v3 design.
- **Review type:** architecture/document investigation only. No files were edited and no build, test, lint, formatter, game execution, or runtime spike was performed.

**This report cannot certify a pristine independent §G1.2 review.** A search accidentally returned a short excerpt of excluded conversational material from a `docs/**/chatlogs/**` path. That material was not subsequently opened, is not reproduced here, and is not evidence for any finding below. The exposure was disclosed to Main. A clean fresh review is required after corrections regardless of this report's verdict.

## 2. Authority and read set

The permitted authority inspected included:

1. Repository licensing/provenance instructions in `AGENTS.md` and `docs/MOVES.md` before reference mining; RC3 Part I, including the §G1.2 procedure, thirteen-section document requirements, dependency/interface rules, and the Phase 7 assignment and required-input list.
2. `docs/research/v1/RESEARCH.md` §0–§1, assigned frame-flow/architecture/hook material in §§4.4, 5.3, and 7.1, Appendix A.1, Appendix E, the assigned OQ-3/OQ-4 questions, and relevant flag/sampler contract appendices.
3. `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` assigned material; the two permitted pinned Pintonium mixin files; Cleanroom `EntityRenderer` and `RenderGlobal` patches and the narrowly relevant hand patch; permitted Schlorbium behavioral documentation, `files.txt`, and the flag documentation in `doc/shaders.properties`.
4. Current dependency §5 contracts and incorporated semantics in Phase 1 v14, Phase 2 v2, and Phases 3–6 v1; relevant receiving/ownership contracts in Phases 8–13. Phase 2 v1 was also inspected to distinguish the historical artifact named by the target header from the current v2 receiver actually incorporated in target §5.2.
5. The integration review's relevant original findings, resolutions, follow-ons, and recorded decision rulings. These were treated as claims to check against current owner/receiver text, not as verification certificates.

The Pintonium reference pin was independently recovered as commit `9c2fcc1a4814cafc0242370757e9e05ea83c5be3` in [Xplodin/Pintonium](https://github.com/Xplodin/Pintonium/commit/9c2fcc1a4814cafc0242370757e9e05ea83c5be3). The permitted [EntityRenderer mixin](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1a4814cafc0242370757e9e05ea83c5be3/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java), [RenderGlobal mixin](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1a4814cafc0242370757e9e05ea83c5be3/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinRenderGlobal_Shaders.java), and [LGPL license](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/COPYING.LESSER) were checked. They supply field evidence, not higher-priority contract authority. No forbidden implementation source is cited below.

## 3. Substantive adversarial audit

### 3.1 Literal document gate, scope, and research conformance

All thirteen required top-level sections are present. The document covers both mandated halves—engine-side frame driving and a hook catalog—rather than substituting a mixin list for lifecycle architecture. Its conformance tables account for the eleven research hook needs and all eighteen Appendix E rows, including explicit Phase 10/13 ownership rather than pretending deferred hooks are implemented.

The document correctly distinguishes adoption of a reference hook moment from adoption of reference behavior. In particular, it moves camera capture after camera setup, targets the four-argument world-render terrain overload, rejects the reference hand-depth timing, and strengthens normal-return finalization with outer cleanup. Its `files.txt` discrepancy disclosure is materially useful: absence of `Framebuffer` is not hidden behind the research document's broader corroboration claim.

The flag mapping nevertheless contains a concrete depth-state error, reported below. The dependency inventory also overstates the verification standing of current incorporated contracts.

### 3.2 Boundary dispatch, ownership, and failure paths

The review traced the current selection-based draw protocol through the receiving side rather than treating production of a selection or texture result as sufficient. The architecture requires one issued selection/context, a Phase 5 snapshot, acquisition of the matching texture publication, and physical binding before activation. It distinguishes binding suppression from backend failure, and transfers lease ownership only on `Bound`. Parent-scope resumption reacquires physical resources using retained selection authority rather than retaining consumed snapshots or resolving the effective provider again.

The target separately handles virtual transitions, fixed-function terminals, final presentation, frame commit/abort, resize invalidation, and publication compensation. Resource replacement is protected by a synchronous pre-destructive boundary, not merely a later queued reload notice. Its retained-configuration `NONE` path does not silently reload pack bytes. Worker drain and cleanup failures retain necessary ownership rather than fabricating successful retirement.

Current replay-report handling includes synchronous receipt, ordered accumulation, attribution preservation, late cleanup reports, final-use drain, and retirement-before-disposal. The earlier pixel-copy callback is expressly not successful sample acknowledgment. Celestial signals have an explicit receiver mapping to Phase 6, and shadow execution has an authenticated dynamic-extent guard rather than a public Boolean bypass.

These are architecture observations, not runtime validation. A remaining alpha/blend enforcement gap appears between activation-time application and the admitted vanilla body; it is reported below.

### 3.3 OQs, decisions, and integration eligibility

The OQ-3/OQ-4 sections supply questions, procedures, success criteria, evidence obligations, and failure/fallback treatment. Pending context, weaving, ordinal, hook-health, or game-run evidence is therefore not itself a missing architecture mechanism. The document does not earn implementation clearance merely by specifying these spikes.

Recorded owner grants can be assessed for present interface agreement, but changed owner §5 regions remain unverified until their own fresh reviews complete. The target correctly says this in several current contract passages; its header must be made consistent with that position. Final integration and implementation remain independently gated.

## 4. Findings

### R38-1 — Correction: Map rain and beacon flags to depth writes, not depth testing

**Location:** `docs/phase7/v1/PHASE_7_DOC.md:767–768`, repeated at `:1402–1404`; consumers include H-WEATHER-01 at `:1535` and H-BEAM-01/02 at `:1497–1498`.

**Claim and evidence:** The target says FALSE disables depth testing for weather and beam scopes, while TRUE/DEFAULT preserves vanilla. The permitted property documentation instead says that `rain.depth` enables rain/snow to **write to the depth buffer** and `beacon.beam.depth` enables beacon beams to **write to the depth buffer** (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:53–59`). The owner facade explicitly separates `depthMask(boolean)`—depth writes—from `depthTest(boolean)`—a different state bit (`docs/phase1/v14/PHASE_1_DOC.md:3309–3310`).

**Impact:** A pack's depth-write option changes fragment visibility testing instead of controlling the promised writes. For a FALSE setting, geometry that should remain depth-tested can be drawn without that test; for TRUE, merely preserving vanilla does not establish the requested write behavior. Scope restoration does not repair the pixels/depth produced during the draw.

**Required correction:** Specify the explicit flag branches through the depth-write mask, preserve the separate depth-test policy, and restore the prior write state on every exit. Update both the conformance row and execution rule so the weather/beam adapters cannot implement different meanings. This uses an existing Phase 1 verb; no new public facade is required.

### R38-2 — Correction: Replace the stale current-dependency verification claim

**Location:** `docs/phase7/v1/PHASE_7_DOC.md:56–70`, particularly the “Only verified dependency artifacts” statement and the Phase 2 row at `:62`.

**Claim and evidence:** The header labels historical review receipts as evidence for the current binding artifacts and names Phase 2 v1 as the current consumed document. Target §5.2 instead explicitly incorporates Phase 2 **v2** at `:3107`. It also explicitly says the adopted Phase 4 §5 is changed/unverified at `:3189`, and the Phase 5 incorporated protocol is newly changed/unverified immediately below its consumption table. Thus the document itself provides direct evidence that the header's blanket claim is no longer true.

**Impact:** A reader applying the dependency gate from the designated inventory is directed to a different Phase 2 architecture and can mistake historical PASS receipts for certification of current coordinated interface changes. This is not a claim that Phase 2 v1 is missing, or that the current grants cannot be assessed; both distinctions matter.

**Required correction:** Update the inventory to the actual current dependency versions and distinguish historical verified baselines from subsequently changed, currently unverified owner contracts. Keep the explicit fresh-owner-review and implementation gates. This is an authority/status correction, not a request to remove otherwise complete coordinated contracts.

### R38-3 — Correction: Specify how the per-program alpha/blend lock survives draw-body setters

**Location:** `docs/phase7/v1/PHASE_7_DOC.md:1081–1089` and H-BLEND-01 at `:1551`; associated receiving contract at `:2580–2596` and Phase 4 consumption in §5.2.

**Claim and evidence:** Phase 4 activation snapshots and applies effective-provider alpha/blend overrides (`docs/phase4/v1/PHASE_4_DOC.md:1665–1672`) and explicitly defines an absent override as “do not lock” and OFF as “lock it disabled,” with restoration on transition/release (`:1757–1760`). Phase 7 then admits the vanilla body. Its only catalogued blend-setter hook is a RETURN observer that publishes the changed state, and the receiver forwards that payload to Phase 6 `updateBlend`; neither operation enforces the held override. Phase 1 exposes ordinary immediate state mutation and snapshot/restore, not a duration-lock operation (`docs/phase1/v14/PHASE_1_DOC.md:3305–3318`), with backend mutations routed through `GlStateManager` (`:3411–3436`).

**Impact:** If an admitted render body invokes a blend/alpha setter after activation, the specified mechanism permits that setter to replace the supposedly locked state before subsequent geometry is drawn. Snapshotting once and restoring at exit cannot enforce the override during the body. This finding concerns the missing architecture mechanism; it does not claim a particular unexecuted vanilla/weather runtime trace was observed.

**Required correction:** Give the lock an explicit owning and receiving mechanism covering intervening `GlStateManager` mutations for both alpha and blend, including nested activation, release, exception cleanup, and recursion/cache coherence. Phase 7's catalog and §5 consumption must either adopt that concrete owner mechanism or specify the necessary authenticated adapter seam. An observer-only hook must not be presented as enforcement. This requires coordinated §5 clarification and fresh affected-owner review, not a structural rewrite of the frame driver.

## 5. Notes and review limitations

### N38-1 — Note: Excluded-source exposure invalidates pristine-review certification

**Location:** This review session's read process, not a defect in target architecture.

A search returned excluded conversational text. No finding relies on it, and no such content is reproduced. Nevertheless, absence of subsequent use cannot undo the exposure. This report is qualified investigative evidence only; Main must obtain a clean fresh §G1.2 review after corrections. This note is counted separately from document corrections.

## 6. Counts, §5 impact, and disposition

- **Blocking findings:** 0.
- **Corrections:** 3.
- **Notes:** 1.
- **§5 impact:** **Yes.** R38-3 needs a concrete cross-owner enforcement/consumption contract; R38-2 also corrects the authority standing of interfaces incorporated by §5. R38-1 can use an already granted facade verb.
- **Runtime/OQ certification:** None. Planned spikes and testability clauses were reviewed as specifications only.
- **Independent-review certification:** Withheld because of N38-1. A clean fresh review remains mandatory after corrections.

The frame-driver architecture does not require a structural rebuild, but its depth-flag semantics, lock enforcement mechanism, and dependency-verification inventory need correction. Current coordinated grants and historical PASS receipts do not remove the separate owner-review, final-integration, or implementation gates.

**Final verdict: PASS-WITH-CORRECTIONS.**

## Resolutions

### R38-1 — Corrected in architecture (2026-09-08; D-P7-39)

Independently read permitted shipped `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties`
lines 53–59: both properties promise writes to the depth buffer. P7 §§3.5/4.9 now use P1
`depthMask`: TRUE forces writes, FALSE forbids writes, DEFAULT leaves vanilla mask behavior.
The scoped adapters cover intervening vanilla mask setters and restore the exact prior mask on
every normal/early/nested/exceptional exit. Depth testing is not changed. Crystal-beam routing
remains explicitly P7 policy, not a fabricated author-doc statement.

### R38-2 — Corrected in architecture (2026-09-08; D-P7-39)

P7 §0.2 names current P1 v14, P2 v2 and P3–6 v1 contracts and distinguishes historical PASS
baselines from current changed/unverified owner grants. Fresh owner/receiver review, IR-01,
final §G5.3 and implementation gates remain open.

### R38-3 — Pending coordinated receiving integration (2026-09-08)

P1/P4 own the lock mechanism. Main must integrate their final grant at P7 §4.4,
H-BLEND-01/§4.11, §5.1 UniformSignalBridge and §5.2 P1/P4 consumption; no second enforcement
mechanism is introduced by this slice. P7 D-P7-40 separately receives P8 R6-1's terminal release,
and D-P7-41 adopts `/4` wire labels; final P2/P5 evidence and mipmap fields remain Main's
integration work. This receipt does not mark R38-3 resolved.

### Qualification preserved

N38-1 and the original verdict above are unchanged. This report still cannot certify pristine
§G1.2 review; a clean fresh review is mandatory. These are documentary correction receipts,
not validation or implementation evidence. No builds, tests, linters, formatters or validation
commands were run for this fix-up.

### R38-3 — Resolved architecturally by coordinated receiver (2026-09-08; D-P7-43)

This follow-on supersedes the pending disposition above without changing the original finding,
verdict, authority or review history. P7 §§4.4/4.10.6/4.11/5.1/5.2 now receive P1 D-P1-57 and
P4 D-P4-33/34: nine concrete cancellable HEAD descriptors cover alpha enable/disable/function
and blend enable/disable/both factor overload families; six blend normal RETURN descriptors
observe effective state. The 1.12.2 MCP/SRG mapping database supplied the exact descriptors;
one entry/normal-exit site per listed descriptor is a required application-audit expectation,
not observed transformed-bytecode cardinality or implementation evidence.

P1 alone owns held-aspect predicates, private cache-coherent bypass, exact snapshots, rollback,
error drains and poisoned failure containment. The complete hook family is CORE, not optional
observation. P7's existing GL-ready provider composition now has an explicit required private
registration from AlphaBlendOverrideHooks.publishEffectiveBlend to UniformSignalBridge.event
and the exact live P6 updateBlend sink. Before-participant values, absent/uninstalled behavior,
tuple identity, cancelled/no-change/enum-delegation observations, notification failures, final
restoration and endpoint retention through successful retirement are specified. P4 closes before
bind/acquire/participants; nested scopes close/reacquire rather than stacking leases. No second
lock policy, public bypass, argument echo, invented frame identity or promised future receiver.

D-P7-44 separately receives P5's exact generateMainMipmaps(snapshot) total operation before
binding/activation, including already-aborted Failed without later complete/discard/abort,
scheduled BackendDegraded depth-copy consumption and repeat no-op, staged `/4` resource
evidence and compatible neutral lifetime. D-P7-42 option/profile/plan receipt is unchanged.

N38-1's excluded-source qualification and original review verdict remain intact. No excluded
implementation source or chatlog was read for this receipt. These are architectural resolutions,
not a pristine §G1.2 certification, runtime proof, implementation clearance or PASS. No validation
commands, builds, tests, formatting or lint ran; clean fresh owner/receiver review remains required.