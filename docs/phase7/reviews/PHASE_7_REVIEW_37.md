# Phase 7 — Independent Architecture Review 37

## 1. Reviewed artifact and review boundary

- **Target:** `docs/phase7/v1/PHASE_7_DOC.md`, including all thirteen sections, historical amendments, current §0.46, incorporated §5 semantics, decisions, open gates and implementation checklist.
- **SHA-256:** `ed63364f6fa8b2c14302ed8b1bc133a8607365fdaf82a1f42aae2b7892af9646`.
- **Hash provenance:** Main computed the current checksum during this review and reported an exact match to the assigned frozen snapshot, with no intervening target edits. This is not represented as an independently computed reviewer checksum.
- **Governing authority:** the target header selects `docs/design/v2.0-RC3/DESIGN.md`. This review does not substitute design v3 globally.
- **Review mode:** fresh whole-document §G1.2 architecture verification, not a patch-only review, implementation inspection, runtime validation or acceptance of earlier review receipts.

No files were edited. No builds, tests, formatters, linters or runtime experiments were run.

## 2. Authority and evidence read

The governing read set comprised `AGENTS.md`, `docs/MOVES.md`, RC3 Part I §§G0–G12 and its complete Phase 7 assignment, followed by `docs/research/v1/RESEARCH.md` §§0–1, §4.4, §5.3, §7.1, Appendix A.1 and Appendix E. The relevant fixed-sampler/engine-flag appendices and exact OQ-3/OQ-4 questions were also checked.

Required behavioral evidence included `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §§4, 6.1 and 16, with §3.1 for the expressly assigned dimension-cache mechanism; the permitted Schlorbium `SHADER_ENGINE_IMPL.md` §§2, 5 and 14 and `files.txt`; and the two assigned Pintonium renderer files and two Cleanroom renderer patches.

The pinned local reference directory names were absent. Local Git-object recovery also failed. Rather than treating the available newer trees as the old pins, the exact required files were subsequently recovered and read from their pinned upstream URLs:

- [Pintonium 9c2fcc1 — MixinEntityRenderer_Shaders.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java)
- [Pintonium 9c2fcc1 — MixinRenderGlobal_Shaders.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinRenderGlobal_Shaders.java)
- [Cleanroom 0.6.6-alpha — EntityRenderer.java.patch](https://raw.githubusercontent.com/CleanroomMC/Cleanroom/0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch)
- [Cleanroom 0.6.6-alpha — RenderGlobal.java.patch](https://raw.githubusercontent.com/CleanroomMC/Cleanroom/0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch)

The corresponding files in the available `Pintonium-main` and `Cleanroom-0.6.12-alpha` trees were supplemental observations only. They were not used to authenticate old line pins. Permitted MCP lookups independently confirmed the `renderWorld`, `setupTerrain`, client-tick and texture-tick signatures; notably, the terrain token is the actual `frameCount` argument, not a nonexistent return value.

Current dependency §5 regions and their incorporated semantics were read for Phase 2 **v2**, and Phases 3–6 v1. Additional cross-boundary checks covered current P1 placement/facade grants, P8 shadow invocation/planning/binding, P9 ID admission/publication, P10 lifecycle/declaration delivery, P11 diagnostic projection, P12 reload/options/diagnostic reception and P13 asset/publication/lease/atlas contracts. These checks establish architectural correspondence, not independent whole-phase certification of those owners.

`docs/PHASE_INTEGRATION_REVIEW.md` was read through its original findings, Resolutions and subsequent coordinated follow-ons. Recorded rulings in `U1_TEXTURE_SAMPLING.md`, `GEOMETRY_PRIMITIVE_COMPATIBILITY.md` and `TEXTURE_SIDECAR_DEFAULTS.md` were checked as policy authority rather than runtime evidence.

No transcripts, chatlogs, root-level text transcripts, forbidden Oculus implementation regions, libraries/glsl-relocated material or restricted transformation sources were used.

## 3. Literal document gate and thirteen-section coverage

The document supplies both mandated parts: a loader-neutral frame driver and an exact hook catalog. It does not defer one part to an unwritten Phase 7b artifact.

The thirteen-section structure is complete and substantive: header/read provenance; ownership boundaries; architecture; research conformance; detailed mechanisms; cross-phase interfaces; failure/degradation; threading/performance; testability; milestone staging; OQ spikes; decisions/open items; and implementation checklist.

The literal Phase 7 gate is met architecturally:

- §3 traces all eleven §7.1 hook needs, including explicit later-owner handoffs rather than silently omitting them.
- The complete Appendix-E class ledger distinguishes currently owned hooks from P9/P10/P13 milestones.
- The seven-row Pintonium timeline has explicit adoption or rejection decisions.
- Sky, weather and clouds are designed without pretending Pintonium supplies working evidence for them.
- The owned engine flags have concrete consumers and scoped restoration rules.
- The v0.1 assembly narrative reaches internal-pack rendering, ordinary dispatch, deferred/composite/final, reload/resize/capture and the assigned T1/T0 implementation gates.
- Both OQ-3 and OQ-4 include questions, procedures, success criteria, evidence destinations and safe fallbacks.

Later milestone boundaries remain visible: shadow content belongs to P8, ID values to P9, extended vertex production to P10 and texture content/companions to P13. Explicit empty texture publication before the texture milestone is not represented as completed v0.5 content support.

## 4. Substantive adversarial audit

### 4.1 Frame moments and finalization

The outer boundary wraps the actual `renderWorld` invocation inside `updateCameraAndRender`; it does not mistakenly wrap only the latter method's terminal return. The normal render-pass TAIL and outer invocation `finally` converge through the same idempotent token. Healthy early exits receive the stronger composite/final guarantee, while protocol/backend failure uses abort/off containment rather than committing unwritten buffer sides.

The split between frame-begin sampling, post-clear buffer preparation and post-camera matrix capture is explicit. This avoids inheriting the pinned Pintonium file's pre-camera matrix timing. Shadow execution receives the actual main terrain argument and same-frame camera/celestial samples, and must close its authenticated dynamic extent before main clearing resumes.

Depth-copy moments, translucent/deferred ordering, hand routes, fullscreen state, effective-provider instance counts and final-target ownership remain assigned to their respective owners. Native geometry compatibility does not introduce a second renderer API or repeat traversal/list construction instead of prepared submissions.

### 4.2 Selection, physical binding and ownership

The ordinary path selects once, opens the Phase 5 pass from that selection, acquires the expected-publication overlay lease, binds physically through Phase 5 and activates with the matching context. Rejected, skipped, degraded and bound outcomes are not collapsed into success. Bound alone transfers the acquired lease into the closeable binding; other exits retain the caller's lease-close obligation.

Nested scopes suspend and complete/discard the parent's pass, retain its authenticated selection/context, and reacquire current binding state on restoration. They do not reuse consumed snapshots or silently reselect a different provider. Lifetime invalidation does not erase the remaining close obligation.

The P8 receiver contains the matching branch for the supplied selection/context/publication/lease source. Its real invocation remains separately gated; no obsolete four-row shadow binder is assumed. P9 admits shadow IDs through the alternate authenticated execution path rather than opening a main gbuffers scope. P10 receives declarations only after successful activation and restores/reset invalidated authority explicitly. P13 atlas availability is not mistaken for an actual texture bind: the authenticated binding observer drives the P7→P13→P6 update path.

### 4.3 Construction, reload, failure and retirement

The coordinated transaction is acyclic: preliminary companion macros precede frontend preprocessing, pure shadow planning precedes the uniform provider, and final shadow creation receives the newly produced registry. Registry/estate/ID generations, resource epochs and pipeline versions remain separate identities.

Publication is not declared atomic across independent owners. Instead, admission stays closed until the complete tuple is accepted, and failures—including failures before any acceptance—converge to the documented off/recovery path. Actual Phase 4 generation changes are delivered individually, including Ready followed by compensating Off.

The resource-manager wrapper closes admission and drains users **before** destructive vanilla replacement. Resource-only NONE preserves the exact configuration and same-load assets, while rebuilding resource-backed state; it neither reopens the selected archive nor applies pending option previews. Texture registration/acquisition authority is retired before replacement. P6 nonterminal reset and terminal retirement are not conflated.

The P10 participant has real worker/upload drain semantics, an exclusive recovery-capable transition token, matched lookup/ordinal lifetime and explicit failed-recovery retention. Cancellation requests, empty queues and timeouts do not authorize freeing still-borrowed state. P9 publication follows textures and precedes geometry invalidation/atomic admission. Internal option acceptance and filesystem persistence retain their different ownership and partial-write semantics.

### 4.4 Current schema and capture control

The active receiver contract adopts P3 schema21, including nested identity checks and the unchanged same-load asset capability; it does not upgrade old values by filling fields. P4 requested-slot own-build disposition is transported without confusing deliberate disabling/fallback with an actual build failure.

The current capture protocol is exclusively `/3`. It authenticates the subject jar and complete mod inventory, supplies controlled shader seconds through existing frame inputs, and obtains actual P6/P7 timing evidence rather than echoing plan fields. The control design covers client/server tick permits, texture animation completion, zero-frame checkpoint establishment, contiguous preparation/warm-up/sample steps, callback-bound report validation and artifact-before-acknowledgment ordering.

The added timer/server-loop hooks are not claimed to have been woven or exercised. Exact loop-local capture, pause coverage and acknowledgement behavior remain OQ-4/runtime prerequisites. Their failure disables capture rather than manufacturing determinism. Outer cleanup revokes admission, wakes waits, drains owner-thread work and restores live clocks without claiming success on a wedged thread.

### 4.5 Diagnostics, policy and licensing

P11's source-free diagnostic projection has an actual P7 publication path and P12 presenter consumer, with final-attempt outcomes and stale/foreign-view rejection. Reload receipts and generation listeners similarly have explicit receiving behavior rather than an unconsumed event shape.

U1 authority, texture sidecar defaults, effective lighting policy and conditional geometry topology remain recorded decisions. None is promoted to empirical parity. The LGPL observations are attributed as evidence, the Schlorbium digest remains behavior-only, and the document does not authorize restricted implementation copying.

## 5. Findings

### N37-1 — Stale wire-version label in the scope summary

- **Severity:** note; non-blocking.
- **Location:** `docs/phase7/v1/PHASE_7_DOC.md`, §1.1 item 8, approximately lines 457–460.
- **Claim:** the active scope summary still calls the capture protocol Phase 2 `/2`.
- **Evidence:** §4.13 explicitly accepts only `schmaloogium.capture-plan/3` and `schmaloogium.run-manifest/3`, rejects `/1` and `/2`, and incorporates current P2 v2 receiving semantics. The exposed capture/timing row in §5.1 and current §5.2 consumption agree with `/3`.
- **Impact/disposition:** this is a stale overview label, not an ambiguous executable protocol or a missing receiver. Updating the summary to `/3` would remove the inconsistency. No binding §5 change is required.

No blocking or correction-level architecture finding was established. In particular, historical dependency receipts in the early header were not treated as current verification evidence: the later amendments and active contracts explicitly establish the changed, unverified owner/receiver posture.

## 6. Counts, §5 impact and remaining gates

- **Blocking findings:** 0
- **Correction findings:** 0
- **Notes:** 1
- **§5 impact from this review:** no. No exposed contract amendment is required by the finding.

The reviewed architecture is coherent at its current boundary. This review does not certify other changed phase documents, resolve the final integration gate, authorize implementation consumption of still-unverified dependencies, or supply OQ/runtime/conformance evidence. Those gates remain independently enforceable; their pending evidence is not itself a missing architecture mechanism where the required spike and fallback are already complete.

## 7. Final verdict

**PASS**

## Resolutions

### N37-1 — Resolved 2026-09-08

Phase 7 §1.1 item 8 now names capture `/3`, matching §§4.13/5 and current Phase 2 v2.
This is the requested overview-label correction only; it does not change the protocol.
The original finding, review evidence, counts and literal **PASS** above remain unchanged.

### Separate coordinated §5 receipt — 2026-09-08

Phase 7 §0.47 and D-P7-37/38 separately adopt preserved P8 R5-1 and P6 R25-1 owner
corrections: pure angles/post-camera inputs and required replay collector with before-return
delivery, failure and finalization lifetime. Those changes are not a finding of R37 and
are not certified by its PASS. They require fresh whole-document **R38** and coordinated
owner/receiver review. No runtime, implementation or integration clearance is recorded.