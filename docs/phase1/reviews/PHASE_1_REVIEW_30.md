# Phase 1 — Independent whole-document review 30

## 1. Verdict and reviewed identity

**PASS-WITH-CORRECTIONS**

**Counts:** blocking = 0; corrections = 1; notes = 1. **Section 5 impact: yes.**

Reviewed owner: `docs/phase1/v14/PHASE_1_DOC.md`, including its historical addenda, active §§1–12, incorporated public contracts, decisions, handoffs and closing status material. Assigned SHA-256: `7d52d7dc8a69b9e6c1e76aecb439d928a255f2d376ffa0e0148db806ef34ab89`. This identity is the commissioning identity supplied by Main; this reviewer did not run a checksum command.

The governing authority is the owner's header-selected `docs/design/v2.0-RC2/DESIGN.md`, Part I and the Phase 1 assignment. Phase 1 has **no declared dependency PHASE documents**. Receiving phases were read to verify outgoing contracts, not retroactively made dependencies.

The architecture does not require rebuilding. One newly published opaque type needs a reachable, seam-preserving issuance design before the document can receive a literal PASS. The finding changes a declaration incorporated by §5.2 and therefore requires the corresponding §5 update and fresh verification.

## 2. Evidence and review boundaries

The review covered the whole owner rather than only D-P1-57. Evidence included the governing Phase 1 Doc gate and global rules; RESEARCH §0–§1, the assigned platform/seam inputs and relevant contract/licensing material; PD §§2/16 and its standing bug/divergence catalogue; the actual root build/settings/properties, dependency/extra/publishing scripts, wrapper, all three workflows, README and the eight template source/resource files. The active D-P1-51 migration ledger was compared with current files rather than treating July pin observations as current executable facts.

`docs/PHASE_INTEGRATION_REVIEW.md` was read through the original findings, resolutions and all dated follow-ons, including the final corrected-core resource/state/evidence entry. Those records identify historical claims and remaining gates; none was substituted for independent evidence that the present owner or a recipient passes.

The receiving-side investigation followed P2's profile/log/diagnostic consumption, P4's activation and lease lifecycle, P5's logical-to-physical route realization, P6's effective-blend event behavior, P7's concrete interception and registered event receiver, and P8's terminal release. Existing P3 jcpp, P10 vertex-input/isolation, P13 texture-parameter and P14 debug/value grants were also checked at their receiving contract declarations. This is not a whole-document certification of those recipients.

**Source-provenance limitation:** the owner's historical `reference-src/pintonium-9c2fcc1/` path is not present in this checkout. The available `reference-src/Pintonium-main/` shim and both state-service interfaces, together with its three startup Mixins, were read directly. They corroborate the relevant inventory and injection shapes, but this review does **not** assert that their bytes equal the historical 9c2fcc1 snapshot. Likewise the bounded Cleanroom bootstrap skim used the available `reference-src/Cleanroom-0.6.12-alpha/`, not an invented 0.6.6 checkout. MCP's setup guide and identified template snapshot provide separate documentation evidence, not proof of current runtime behavior. The correction below depends on the actual Phase 1 declarations and package boundary, not on either unavailable historical pin.

All searches used explicit document or permitted source-directory allowlists. No chatlog, root transcript, forbidden Oculus transformation/library tree, relocated GLSL source or copied transformation code was read. Published pack-author documentation was treated as contract evidence; PD/source observations were not promoted into pack-contract authority. No source was copied into an implementation. No file was edited and no build, test, formatter, linter, validation command, checksum or runtime experiment was run.

## 3. Doc-gate and whole-owner assessment

### 3.1 Structure, build and seam

The three-module layout, closed package allocations, engine/headless dependency direction and C-1–C-4 enforcement responsibilities are substantive. The engine remains free of Minecraft/loader/Mixin/LWJGL types; backend adaptation remains in `:mod`. First-party class merging is distinguished from third-party `contain` packaging. D-P1-49 correctly makes jcpp's exact version, dependency closure, notices and distribution an implementation-time prerequisite rather than claiming an unperformed artifact verification.

D-P1-51 accurately distinguishes the current wrapper/plugin/loader baseline from historical values: the inspected files contain Gradle 9.7.0, Unimined 1.4.36-kappa and Cleanroom 0.6.10-alpha, while workflow setup inputs still contain 9.6.1. The document orders a migration instead of claiming those files already agree. The mod-only placement and lazy behavior of the Buildship script are consistent with the inspected script.

### 3.2 Contract coverage, ownership and bootstrap

The conformance map supplies the foundation mechanisms without transferring pack parsing, routing policy, uniform cadence, texture allocation, shadow policy or hook-catalog ownership into Phase 1. The opaque owned/ordinary-foreign/borrowed-depth distinctions are maintained; a public marker is not authentication. The linked-geometry/fullscreen contract has a real consuming dispatch: TRIANGLES selects the triangle-strip path, while unsupported/unknown input rejects drawing rather than silently using QUADS.

The PD inventory check remains a checklist rather than adoption of its GL-coupled core. The available reference interfaces corroborate the two-part shim/state-service inventory, and the actual startup Mixins corroborate GameSettings HEAD, OpenGlHelper RETURN and GuiMainMenu RETURN. Phase 1's loader-event deviation and deferred stage-three placement are argued explicitly. Neither those reference hooks nor MCP documentation proves application on the selected runtime; first-hook/refmap/compatibility evidence remains due.

### 3.3 Failures, lifetimes, recording and staging

The public error model distinguishes driver errors from replay attribution, preserves the one-result-per-triggering-error law and does not infer attribution from names. P2 receives those values rather than manufacturing verdicts. The recorder's stable vocabulary and explicit responses remain separate from live driver observations. Texture and vertex operations retain origin checks, actual-predecessor restoration, failure containment and no raw-name escape hatch.

The OQ-2/12/20/21 specifications contain questions, procedures, success/failure criteria and fallback/disposition. In particular, OQ-20 keeps the paper mapping at v0.1 and the non-vacuous NullGLDevice exercise at the later milestone with engine logic. No spike completion is inferred from its specification. Required synchronous features and optional modernization remain distinct. The numbered scope, design, failure, performance, testability, milestone, decision and implementation-checklist sections are present and substantive.

## 4. Correction

### R30-1 — Make override issuance reachable across the backend seam

**Severity:** correction. **Confidence:** high. **Section 5 impact:** yes.

**Location:** `docs/phase1/v14/PHASE_1_DOC.md:3330–3333`, incorporated by §5.2's D-P1-57 row at line 5069; compare §2.1 and §4.7.3's backend-placement explanation at lines 3000–3015, and §4.7.5's recorder package.

**Claim under review:** `AlphaBlendOverride` is a backend-issued, opaque, render-thread-only **final class**, with **no public constructor or factory**. `StateService.lockAlphaBlend(...)` returns that token. The real backend belongs to `com.schmaloogium.mod.glue`; the recorder belongs to `com.schmaloogium.engine.gl.record`.

**Evidence and impact:** neither backend package can subclass a final `engine.gl` class or call its non-public constructor. No callable trusted issuance path is published for this token. The document already explains precisely this cross-package implementation constraint for its handle interfaces, but the new lease reintroduces it. This is not a complaint that an illustrative signature omits method bodies: the expressly forbidden public construction/factory path and expressly final representation together leave the named issuers without a specified legal construction route. [INFERENCE] Implementing the published contract literally therefore requires changing its type or adding an uncontracted access bridge before the first successful lock can return its required lease. P4's legitimate `lockAlphaBlend` consumer, and the recording backend needed for its headless tests, both encounter the gap.

**Required correction:** choose and specify one reachable issuance mechanism without weakening the engine/backend seam. The existing opaque-interface pattern is sufficient: a public `AlphaBlendOverride extends AutoCloseable` interface with backend-private concrete implementations, keeping the close signature and the complete single-device, non-nestable, idempotence, rollback, poisoning and notification law. Alternatively specify a complete engine-safe trusted issuer if the final class is retained. Do not use reflection, public raw state/bypass access, or a `:engine` dependency on `:mod` to evade the boundary. Update the incorporated declaration and its §5.2 row together, and synchronize recipients only where the chosen public shape actually changes their consumption.

## 5. Receiver-scoped note

### R30-N1 — Phase 6 still describes the superseded post-participant lock order

**Severity:** note in this Phase 1 review; a Phase 6 reconciliation item. **Confidence:** high. **Phase 1 Section 5 impact:** none.

`docs/phase6/v1/PHASE_6_DOC.md:1458–1464` correctly receives D-P6-32: successful acquisition/release publishes effective state **before built-in dispatch**. However, the active text at lines 1499–1504 still states that Phase 4 applies its lock **after the three participants**, with matching older explanations in §2.3 and §4.10. The current Phase 4 activation steps at `docs/phase4/v1/PHASE_4_DOC.md:1671–1677` and Phase 7's receipt at `docs/phase7/v1/PHASE_7_DOC.md:1106–1109` explicitly close → bind → acquire → publish/sample → participants.

This does not establish a missing Phase 1 notification mechanism: Phase 7 now declares the concrete nine HEAD/six RETURN hook family and, at lines 2725–2760, the installed P1 bridge → UniformSignalBridge → exact P6 runtime receiver, including no-frame behavior, result handling and endpoint lifetime through final restoration. The stale Phase 6 explanations should be reconciled by its owner review/integration; Phase 1 must not be changed back to the obsolete order to make them true. No Phase 6 PASS is issued here.

## 6. Current cross-boundary conclusions

- **Positional routing:** P5 §4.5 preserves output ordinals while packing only actual attachments. Its explicit `0N2` example produces `[Attachment(0), None, Attachment(1)]`; leading/trailing/all-none positions survive, and capacity checks distinguish slot count from attached-object count. P1 encodes the typed physical vector; P2 consumes that exact vector and does not reinterpret logical indices or raw sentinels.
- **Duration enforcement:** P1 owns held-aspect guards, private cache-coherent bypass and transactional restoration. P4 closes the predecessor before acquiring the next lease and invalidates activity before restoration. P7 registers actual receiving hooks and the effective-state endpoint. P8 receives terminal release before platform restoration. These are coherent architectural lifetimes apart from R30-1's token construction gap; they are not evidence that interception or restoration has executed.
- **Staged evidence and modernization:** P2's source-free profile/log use does not create a second P1 profile grammar. Existing texture/vertex/debug grants have receiving declarations, while optional optimization, native-state proof and complete conformance remain separately gated.

## 7. Required disposition

Apply R30-1 in a separate owner fix-up, record its resolution, and obtain fresh whole-document verification because the binding §5 surface changes. Route R30-N1 to Phase 6 without treating it as a Phase 1 producer correction. Historical PASSes and integration resolutions remain historical. This review does not close IR-01, certify any dependent phase, establish shader implementation readiness, or claim a runtime/conformance result.

## Resolutions

### R30-1 — Backend-reachable opaque lease issuance

D-P1-58 replaces the final engine-package lease declaration with an unsealed
`AlphaBlendOverride` interface. Each backend returns its private implementation from
`StateService.lockAlphaBlend`; issuing-device identity/state remain private, no public
factory or constructor bypass is added, and no caller-supplied lease is accepted as authority.
The existing thread/lifetime/rollback/idempotence/poisoning contract and §5 incorporation
remain binding. This is an architecture fix requiring fresh review, not a PASS.

### R30-N1 — Receiver ordering

P6 D-P6-33 aligns its diagram, table and active explanation with P4's pre-participant lease
acquisition and actual effective-state receipt. P4 D-P4-35 fixes its matching export summary.
Original findings and verdict above remain unchanged; no receiver verification is inferred.