# Phase 8 — Independent whole-document architecture review 7

## 1. Reviewed artifact and authority

- **Target:** `docs/phase8/v1/PHASE_8_DOC.md`, all 2,203 lines and all thirteen numbered sections.
- **Assigned frozen SHA-256:** `bb5bfeb2d2ee85880e9b6f05e07ec54e039626bf640dcdf39ea50797abdef1ff`. This records the supplied snapshot identity; no checksum command was executed.
- **Report destination:** `docs/phase8/reviews/PHASE_8_REVIEW_7.md`. This report is returned for Main to preserve; the reviewer wrote no repository file.
- **Governing design:** the target-selected `docs/design/v2.0-RC3/DESIGN.md`, including Part I and the complete Phase 8 assignment. No global adoption of Design v3 was inferred.
- **Method:** fresh independent whole-document architecture verification, not a patch-only check or implementation review.

The review includes the current amendments, incorporated §5 contracts, failure paths, milestones, test plans, decisions and handoffs. The complete `docs/PHASE_INTEGRATION_REVIEW.md` was read, including its original findings, Resolutions and every dated follow-on. Preserved Phase 8 reviews 5 and 6 and their resolutions were examined as historical claims, not current PASS evidence.

## 2. Evidence and actual receiving boundaries

### 2.1 Governing and supporting material

Read RC3 Part I §G0–§G12 and the Phase 8 assignment; the applicable RESEARCH §§0–1 and §4.5, Appendix A.3, B.2–B.3, D.2–D.3, E rows 1–2 and F.1; and Pintonium design §10 and its provenance introduction. Read the assigned published G6 shader documentation and only the bounded shadow-pass portion of `SHADER_ENGINE_IMPL.md` as behavioral evidence.

Read the recorded U1 texture-sampling, texture-sidecar-default and geometry-primitive-compatibility decisions, together with Phase 4 §11.5's explicit prepared-submission ruling. These establish the scope and owner obligations adopted by the current documents; they do not establish runtime parity or executed conformance.

### 2.2 Current owner and recipient contracts

Read the complete current §5 regions of Phases 4, 5, 6 and 7, following their incorporated detailed semantics. The cross-boundary audit covered:

- **P4:** private selection authentication, effective-provider metadata, the same selection through binding and activation, all barrier results, activity-token lifetime, current release-kind context, duration-lock ordering and positional-route fingerprint identity.
- **P5:** declaration-driven shadow demand; available/not-requested/unavailable estate dispatch; actual extent; frozen ordinary and shadow readable sides; the full sixteen-row physical binding protocol; depth split, completion/abort, mipmaps and generation-checked neutralization; complete-policy neutral backings and their lifetime.
- **P6:** pure provider construction, accepted frame values, unchanged celestial/matrix events, immediate-upload authority, the sole P5 sampler resolver, replay-result delivery and failure handling, generation adoption and final-use retirement.
- **P7:** plan-before-provider construction, final-registry publication, actual post-camera capture, execution-bridge issuance and close, shadow result dispatch, main-hook bypass, terminal continuation/containment, replay collector and current reporting receipt.
- **Additional current recipients:** P1 v14's package and concrete alpha/blend interception grants; P2 v2's `/4` reporting and eight-row owner-8 projection; P3's public shadow minima and default-valued resource projection; P9's alternate shadow ID admission; P10's actual native/model-list guards; and P13's expected-publication lease and atlas-binding routes.

The current P2 recipient assessment uses `docs/phase2/v2/PHASE_2_DOC.md`. A narrow navigation read of the older v1 document was not used as authority for the current wire contract. These dependency reads are interface verification for this target, not whole-document certifications of those owners.

### 2.3 Independently recovered reference evidence

The historical version-named local Pintonium and Cleanroom reference directories were absent. Newer local trees were not relabeled as the required pins. The relevant historical files were instead recovered at their explicit upstream revisions:

- [Pintonium commit identity](https://api.github.com/repos/Xplodin/Pintonium/commits/9c2fcc1) resolves the required short revision to `9c2fcc1a4814cafc0242370757e9e05ea83c5be3`; its [pinned README](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/README.md) states LGPL-3.0 licensing.
- Read the complete pinned [ShadowMatrices.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/modern/src/main/shaders_java/net/irisshaders/iris/shadows/ShadowMatrices.java), the assigned camera/pass-order regions of [ModernShadowRenderer.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/modern/src/main/shaders_java/net/irisshaders/iris/shadows/ModernShadowRenderer.java), and the relevant angular/vector regions of [CommonShadowRenderer.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shadows/CommonShadowRenderer.java) and [CelestialUniforms.java](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/CelestialUniforms.java).
- [Cleanroom tag identity](https://api.github.com/repos/CleanroomMC/Cleanroom/git/ref/tags/0.6.6-alpha) identifies commit `75e899b8a56ad5f1d97dc2e9e2586bb8c69022d0`. Read the relevant pinned [RenderGlobal patch](https://raw.githubusercontent.com/CleanroomMC/Cleanroom/0.6.6-alpha/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch), [ForgeHooksClient setter](https://raw.githubusercontent.com/CleanroomMC/Cleanroom/0.6.6-alpha/src/main/java/net/minecraftforge/client/ForgeHooksClient.java) and [MinecraftForgeClient getter](https://raw.githubusercontent.com/CleanroomMC/Cleanroom/0.6.6-alpha/src/main/java/net/minecraftforge/client/MinecraftForgeClient.java).

The assigned framework event query returned no matching render-pass event. A broader API query found the public setter, and the pinned source establishes the setter/getter protocol. MCP queries also corroborated RenderGlobal fields and the exact `setupTerrain` and world-loop `renderBlockLayer` signatures. Mapping existence is not evidence of transformed hook application.

No chatlog, root text transcript, forbidden Oculus transformation/library tree, relocated GLSL tree or copied transformation code was read or searched. Searches used explicit document/source allowlists. No implementation code was copied.

## 3. Substantive whole-document assessment

### 3.1 Literal document gate, camera and traversal

The target has substantive scope, architecture, conformance, detailed design, interface, failure, performance, test, milestone, risk, decision and implementation-checklist sections. Its camera equations, directive/uniform mappings, traversal policy and hook ledger address the Phase 8 document gate. No research OQ is assigned to this phase; the listed experiments remain implementation-risk work rather than invented prerequisite OQs.

The orthographic and perspective branches are distinguished, with explicit near/far values, column-major conventions, signed-remainder snapping restricted to orthographic projection, and normalized angular input rejection. The target deliberately corrects the reference's inconsistent perspective bottom-right entry rather than copying it. It also preserves the governing SOLID → CUTOUT_MIPPED → CUTOUT order instead of inheriting the modern renderer's different order.

The inward-plane construction, light-direction convention, positive-coefficient silhouette planes, AABB predicate and conservative degeneracy fallback are specified consistently. The sun-aligned traversal defines bounded versus full-view behavior, toroidal position validation, identity deduplication and separate visitation state. This is architectural specification, not proof that the proposed second vanilla terrain setup or far-caster completeness has worked in a client.

### 3.2 Current celestial and terminal-release corrections

The R5 celestial repair supplies actual dataflow: pure `ShadowCelestialAngles` before provider sampling, followed by the sole camera computation with the accepted frame and real post-camera `CameraSnapshot`. Frame identity and eye-space vectors no longer have to be synthesized from a pure angular input. The P7 provider/camera recipient and unchanged P6 event agree on that division.

The R6 terminal repair is also present at both ends. P8 arms release before its first platform mutation, restores per-draw ID/color/instance scopes while activity remains current, and calls the current frame's canonical `releaseToFixedFunction` before platform restoration. Only `FixedFunction` proves successful release. Stale/off/failed/unexpected results and throws remain failed outcomes while independent binding, traversal, Forge-pass and platform cleanup is attempted. P7 consumes `Failed` without main bind/clear/draw and retains sole ownership of execution-view closure. Neither predecessor finding is repeated.

### 3.3 Binding, locks, mipmaps and lifetime

The shared selector passes unchanged through P7 selection, P8 validation, P5 snapshot/binding and P4 activation. P5 owns physical binding; P6 only uploads the corresponding sampler integers. All four texture-binding results are handled, and only `Bound` transfers lease ownership. Completion, abort, neutralization and invalidation do not erase the separate finally-close obligation.

P8's current duration-lock receipt matches P1's enforced held-aspect guards, P7's concrete interception/effective-blend delivery and P4's acquire-before-participants order. Terminal release closes that ownership interval before P8 restores platform state. P8 does not introduce another lock or an immediate-setter substitute.

The single pre-translucent depth copy, independent Forge pass 1, post-draw mipmap ordering and already-neutralized no-double-terminal branch are present. The neutral cache has the required complete sampling-policy identity, up to two depth and two color backings, preinitialization and owner-managed retirement. The remaining neutralization defect is not the cache design: it is the missing construction-time feature-disable transition identified in R7-2.

Replacement and shutdown preserve final callback/restoration use before P6 retirement and borrowed-service disposal. Current generation checks remain distinct from content fingerprints, and stale equal-content publications cannot be revived.

### 3.4 Adjacent receivers and evidence boundaries

P7's authenticated shadow execution bypasses main terrain/entity/cloud/frustum policy without requiring a main gbuffers snapshot. P9 receives alternate shadow ID admission, while P7 retains independent color ownership and restoration. P10's native/model-list recipient preserves the approved adjacent prepared-submission boundary without replaying world traversal, Forge predicates, list capture, depth copies or mipmaps.

P6 replay reports have an explicit P7 receiving mandate, including the stable delivery-failure diagnostic, collector failure latch and P8/P9/P13 handling before another draw. This review does not infer executed coverage from that mandate or count generic activation wording alone as proof that the incorporated prerequisite is bypassed.

P8's eight-row health report reaches the actual P7 frozen report and P2 v2 `/4` grammar. P2/P7 retain option-state, canonical profile text, comparison and staged P5 resource evidence ownership. P8 neither reconstructs realized formats/fog clears nor creates another codec or clock. The maintainer's option-only supersampling, texture-sampling/sidecar and prepared-submission decisions remain narrow scope rulings, not runtime certification.

## 4. Findings

### R7-1 — Carry shadow-request presence into the pure planning boundary

**Severity:** correction. **§5 impact:** yes.

**Target locations:** `docs/phase8/v1/PHASE_8_DOC.md:327–347`, `652–681`, and the exported contract at `1383`.

**Claim under test:** the pure planner deterministically distinguishes ordinary shadow absence from a requested shadow feature and supplies the corresponding ready celestial policy or explicit absence before P6 construction.

**Evidence:** the exact input is only `ShadowPlanInput(ShadowPolicy policy, ShadowHookHealth hookHealth)`. The complete `ShadowPolicy` contains camera, traversal, cloud/translucency, mipmap and PCF values, but no request/minima discriminator. Nevertheless §4.1 requires ordinary absence to return `NotRequested` and routes that result to explicit provider absence. The actual demand producer is separate: P3 publishes `BufferMinima.shadowDepthBuffers` and `shadowColorBuffers` (`docs/phase3/v1/PHASE_3_DOC.md:2662–2672`), while all shadow policy aggregates remain present with ordinary defaults (`2715–2732`, `3570–3574`). P5 plans the sfb exactly when either shadow minimum is positive (`docs/phase5/v1/PHASE_5_DOC.md:1892–1924`). P7's actual construction receiver supplies the same two-field plan input before provider/runtime construction (`docs/phase7/v1/PHASE_7_DOC.md:862–868`); it does not supply a separate demand value or define a demand-based bypass of the planner.

**Trigger and impact:** one configuration can have no shadow declaration and another can declare `shadowtex0`, with identical default camera/flag/mipmap/PCF values and identical hook health. The published pure inputs are then identical although the required planning outcomes are NotRequested and Ready. The planner cannot satisfy both without an undeclared state/configuration lookup, a sentinel convention forbidden by the current shapes, or treating one case incorrectly. The later P5 invocation-time absence branch cannot repair the earlier provider-construction decision.

**Required correction:** expose an immutable request/demand discriminator derived from the same accepted P3 minima, or explicitly assign the demand-based bypass and NotRequested production to P7 through a binding typed contract. Define absence/validation precedence and fingerprint participation consistently. Preserve registry-independent planning and final-registry construction; no registry lookup or new compilation cycle is needed. Update the exact P8 §2/§5 shape and every incorporated owner/receiver reference, particularly P7's construction and provider handoff.

### R7-2 — Neutralize a requested estate when shadow planning disables the feature

**Severity:** correction. **§5 impact:** yes.

**Target locations:** `docs/phase8/v1/PHASE_8_DOC.md:670–691`, `1746–1750`; related P5 consumption at `1456` and P7 result contract at `1555–1556`.

**Claim under test:** disabling only the real shadow feature preserves the main pipeline through the Phase-5-owned coherent unavailable/neutral disposition, rather than leaving a real but undrawn shadow estate available.

**Evidence:** unhealthy required H8 hooks cause a disabled plan while the main pipeline remains valid. P8 §4.1 sends explicit absence to the provider and produces no installed publication for that result. P7's receiving construction path independently plans and publishes P5 from configuration/registry inputs (`docs/phase7/v1/PHASE_7_DOC.md:862–911`), and its failure table maps NotRequested/Disabled to `NotInstalled` (`3667`). Its invocation dispatcher then advances `NotInstalled` without shadow activation (`2855`). None of these branches changes P5's estate state.

P5, however, creates the real sfb whenever either P3 shadow minimum is positive; hook health and P8 plan disposition are not its planning inputs. Creation failure or an explicit `degradeToNeutral` transition establishes `ShadowEstateUnavailable` and neutral unit backings (`docs/phase5/v1/PHASE_5_DOC.md:1892–1939`, `2070–2081`). P5 already publishes `ShadowNeutralReason.EXPLICIT_FEATURE_DISABLE` (`2025`). P8's current neutralization protocol only calls the operation for invocation/backend failure reasons (`1771–1779`). The actual main binding fallback continues to choose real-or-neutral shadow backing from estate state (`docs/phase5/v1/PHASE_5_DOC.md:2337–2340`); absence of an installed P8 slot is not a neutralization operation.

**Trigger and impact:** a valid pack requests shadow buffers, sfb allocation succeeds, but a required H8 hook is missing or overmatched. P7 admits the main pipeline with `NotInstalled`, while P5 remains `ShadowEstateAvailable` and no shadow pass renders that estate. The specified main binding path therefore retains the real backing instead of establishing the promised neutral fallback. The same missing transition affects positive-demand NotInstalled gating. This is a producer-to-recipient state-transition gap, not a complaint that the neutral textures themselves are underspecified.

**Required correction:** carry the disabled/uninstalled disposition into P7's composition transaction and, before first main-frame admission, establish P5's coherent neutral state for a requested available estate using the actual accepted estate generation and existing `EXPLICIT_FEATURE_DISABLE` operation. Handle `Neutralized`, `AlreadyNeutral` and rejection explicitly; inability to establish safe fallback must use existing off containment. Distinguish genuine zero-demand NotRequested, which needs no neutralization. Synchronize P8's construction/failure/§5 contract with P7's actual consumer and P5's owner semantics. Do not add a P8-owned texture, per-frame clear/bind workaround or invented candidate API.

## 5. Counts, cadence and verification limits

| Classification | Count |
|---|---:|
| Blocking | 0 |
| Correction | 2 |
| Note | 0 |
| **Total findings** | **2** |

**§5 impact: yes.** R7-1 changes the pure planning/dataflow boundary; R7-2 changes the construction-time feature-disposition and receiving transaction. Both require coordinated owner/receiver amendment and fresh review of the affected §5 contracts. Neither requires discarding the underlying shadow architecture.

No file was edited. No build, test, linter, formatter, checksum, validation command, render session or runtime experiment was executed. Documentary, mapping and pinned-reference inspection is the evidence supplied here. Traversal/application proofs, real GL behavior, conformance tiers, dependency acceptance and final fourteen-document integration remain separate obligations.

## 6. Final verdict

**PASS-WITH-CORRECTIONS.** The complete current architecture substantially satisfies its document gate, and the celestial, terminal-release, duration-lock, shared-binding and evidence receipts have actual receiving contracts. Two bounded construction/dataflow corrections remain: represent shadow demand at the pure planning boundary and convert disabled requested shadows into P5's coherent neutral estate before main-frame admission. This report grants no implementation, runtime, dependent-wide or final-integration clearance.

## Resolutions — 2026-09-08 architecture correction receipt

The original review body, evidence, counts (0 blocking, 2 corrections, 0 notes) and
PASS-WITH-CORRECTIONS verdict above remain unchanged historical review output.
These owner amendments are unverified architecture, not a replacement review or PASS.

- **R7-1 — addressed in P8 D-P8-26, coordinated P7 receipt required.**
  `ShadowPlanInput(ShadowPolicy policy, ShadowHookHealth hookHealth, boolean requested)`
  now receives the same accepted P3 minima predicate as P5:
  `shadowDepthBuffers > 0 || shadowColorBuffers > 0`. §§2/4.1/5 specify structural
  validation before ordinary absence, then requested-only policy/hook gating, triple
  input/cache identity and Ready fingerprint participation of `requested=true`.
  Provider planning remains registry-independent; final-registry construction is unchanged.
  The ownership diagram, invariants, §8 fixtures, §9 staging, §11 and checklist agree.
- **R7-2 — addressed in P8 D-P8-27, actual P7 receiver integration required.**
  §4.1 step 5 and §§5/6 require positive-demand Disabled/NotInstalled disposition to
  neutralize an Available accepted P5 estate with its actual current estate generation
  and existing `EXPLICIT_FEATURE_DISABLE`, after publication and before main admission.
  Unavailable already supplies neutral proof; genuine zero-demand NotRequested needs
  no transition. Neutralized/AlreadyNeutral, rejected/stale/mismatched/unproven outcomes
  and unexpected construction-time open-snapshot abortion have explicit continuation
  or off-containment branches. No P8 texture, candidate API or per-frame workaround is added.
- **Existing receiver mandate clarified, not a third finding:** D-P8-28 makes P7 §5.1's
  replay-delivery prerequisite explicit after Activated and immediate shadow events.
  `phase6.replay.delivery.failed` or collector failure forbids the next draw and returns
  Failed through existing containment. Ordinary isolated uniform degradation remains
  allowed; terminal release and independent cleanup still run. This addresses P7 R39's
  receiver note without changing this review's count or inventing an API.

Grounding was limited to the header-selected RC3 Phase 8 assignment, RESEARCH §4.5,
accepted P3 minima, P5 shadow estate/neutralization and P7 synchronous replay receiver.
No validation commands, builds, tests, linters, formatters or runtime verification ran.
Fresh coordinated owner/receiver review remains required; IR-01 and final G5.3 stay open.