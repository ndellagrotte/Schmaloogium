# PHASE_1_DOC.md — Independent whole-document review, round twenty-seven

## 0. Method, authority and inspected state

Target: `docs/phase1/v14/PHASE_1_DOC.md`, complete current document, including historical addenda, incorporated §5 semantics, acceptance requirements and handoffs. Governing authority is the Phase 1 header’s `docs/design/v2.0-RC2/DESIGN.md`; neither another phase’s v3 adoption nor historical review overrides were substituted. This is the commissioned fresh whole-phase architecture review, not an implementation test or §G5.3 integration clearance.

The review inspected the working-tree patch and the complete target context. Required inputs covered RC2 Part I and the complete Phase 1 assignment; Research §§0–1, 5.1–5.3, 6.1, 7.2 and 12.2; PD §§2/16 and the standing §§17/18 cautions; current build/settings/properties, wrapper, all three Gradle scripts, all three workflows, README and all eight template source/resource files. Additional Research reads checked facade capability, geometry, framebuffer, uniform and custom-texture claims. The MCP mixin-setup guide and mixin configuration template were consulted as platform guidance, not authority to restore their older pins.

The limited Cleanroom source inspection covered boot/CleanMix layout and bootstrap after checking its LGPL-2.1 license. Its available 0.6.12-alpha tree is not the configured 0.6.10-alpha runtime. Bounded Pintonium source reads checked the complete MinecraftVersionShimService, GLStateManagerService, RenderSystemService and CeleritasShaderVersionService inventories. The available Pintonium-main archive was not relabeled as historical 9c2fcc1; its root LICENSE contains GPLv3 text, and no source was copied or newly adopted. No restricted transformation source, Oculus prohibited subtree, chatlog or root transcript was used.

The latest `PHASE_1_REVIEW_26.md` was read including its separate Resolutions. Its original PASS-WITH-CORRECTIONS verdict and R26-1 remain historical. The current review additionally followed P3 §5/§11 schema19 native-source and jcpp receipts, P10 §5 and its incorporated draw contract, and P13/P14 §5 mandatory foundation receipts. The U1, geometry-primitive and texture-sidecar decision documents were read as narrowly scoped maintainer authority, not measured G6 behavior.

No file was edited and no build, test, formatter, linter, client or GL experiment was run by this reviewer. The native-array defect below was also identified by the focused NativeLifecycleSeam audit; this review independently checked its owner/consumer clauses and primary specification. It is one defect, not two integration findings.

## 1. Findings

### R27-1 — Isolate interfering arrays inside the borrowed vertex-input scope

**Severity:** correction. **Priority:** P1. **Location:** Phase 1 §4.7.6, lines 4021–4026; receiving Phase 10 §4.6, especially lines 575–579 and the client/VBO/list-capture paths, incorporated by P10 §5.

The new state transaction snapshots fields the P10 plan can perturb and says to configure only that plan. P10’s receiving rule disables only other Phase-10-owned generic arrays at locations 10/11/12; it does not isolate enabled generic attribute zero when the source position is supplied through the conventional vertex array. A concrete admitted predecessor state is an enabled generic attribute-zero array pointing at different, valid vertex data. The new scope installs the authenticated conventional position pointer but leaves that other array enabled. The published [ARB_vertex_shader specification](https://registry.khronos.org/OpenGL/extensions/ARB/ARB_vertex_shader.txt), revision 0.83, §2.7 says attribute zero supplies vertex coordinates, and §2.8’s ArrayElement sequence explicitly selects enabled generic array zero instead of the conventional vertex array. The submitted geometry therefore uses the foreign positions rather than the authenticated source. In LIST_CAPTURE, the same specification transfers every enabled array, so retaining unrelated enabled arrays also defeats the claim that only the authenticated capture plan’s inputs are recorded; such arrays may address ranges that were never admitted for this capture. Restoring their state afterward does not undo wrong vertices already drawn or captured.

**Owning correction:** P1 must extend the bounded native input transaction to temporarily isolate state that interferes with conventional position delivery and to define capture-specific enabled-array admission. P10 must adopt that exact rule in its live/capture plans. Snapshot and restore the actual predecessor, including generic-zero enable/pointer state where touched; do not permanently clear another renderer’s arrays or substitute neutral predecessor values. Preserve the separate LIST_REPLAY_GUARD behavior rather than reconstructing expired pointers. Add planned cases with an enabled, distinct generic-zero source and an unrelated enabled capture array, checking submitted/captured values as well as exact restoration and setup-failure rollback. P7 remains the existing failure-containment owner; no new renderer API or topology authorization is needed.

## 2. Mandatory review checklist

| RC2 §G1.2 check | Assessment |
|---|---|
| Literal Doc gate | Module/package layout and C-1…C-4 constraints are concrete; D-1…D-10 have dispositions; current-pin migration and re-pin procedure are stated; the PD inventory check and justified bootstrap deviations exist. R27-1 is a bounded correction to the new facade semantics, not a missing module architecture. |
| Conformance-map audit | Capability inputs, compatibility-profile baseline, default-block uniforms, owned/foreign texture distinctions and fixed-function terminal mechanics remain mapped. Native-source availability is correctly separate from legal native submission. R27-1 prevents accepting the new input-state/restoration contract as complete. |
| Interface honesty | P3 really grants schema19 native-preserving materialization and adopts D-P1-49; P10 really adopts R10-1; P13/P14 really adopt the complete mandatory texture mapping, with P14’s debug/package receipts present. These are no longer missing-owner findings. The borrowed-source consumer has the substantive native-state defect above. |
| Scope discipline | P1 retains facade/build/compatibility mechanisms; P3 preprocessing, P4 program policy, P7 orchestration, P10 topology/layouts and P13 texture policy retain their owners. The conditional geometry adapter is expressly approved, not a general renderer rewrite. Optional P14 worker/acceleration requests remain separate. |
| Template completeness and OQs | All thirteen required sections are substantive. OQ-2/12/20/21 retain question, procedure, criteria and fallback treatment. Current Buildship placement follows the actual mod/source-set owner rather than the source-set-free root. |
| Binding decisions | No new contradiction of D-1…D-10 was found. Current executable pins are retained rather than upgraded or downgraded from historical evidence. U1 and sidecar recovery/defaults are separately scoped decisions; neither supplies G6 parity. |
| Pintonium compliance | The stronger headless module seam is not confused with Pintonium’s service-based, GL-coupled core. The inventory check does not import renderer policy, alternate attribute locations, dynamic sampler allocation or prohibited transformation dependencies. Historical source observations and current archive evidence remain distinct. |

The adversarial pass checked missing coverage, architectural soundness, unowned scope, invented divergences, non-falsifiable acceptance and unconfirmed runtime assumptions. The actionable result is R27-1; no structural rebuild is justified.

## 3. Resolution and readiness assessment

R26-1’s fullscreen defect has a substantive owner-side resolution: effective successful-link input is acquired at link time, native/core precedence is distinguished, TRIANGLES selects the retained triangle strip even on QUADS-capable profiles, incompatible/unknown input cannot draw, and selection/deletion/restoration/error behavior is specified. This is not evidence that the separately approved vanilla client/VBO/list adaptation works; R27-1 concerns that newly granted input boundary.

D-P1-49 supplies the previously absent jcpp build admission, including implementation-time exact pin, closure/license checks, unchanged seam enforcement and once-only third-party containment distinct from the first-party class merge. It does not claim a resolved dependency graph. D-P1-51 preserves the actual Gradle 9.7.0, Unimined 1.4.36-kappa and Cleanroom 0.6.10-alpha baseline, records CI’s setup-input mismatch and moves the active Buildship callback to the proper owner. D-P1-52/54 and their mandatory P13/P14 receipts are present; this review does not reissue their former missing-grant claims.

Runtime evidence remains separate. Main reports the unchanged template built with Java 25 and that its test task is NO-SOURCE. During this review Main additionally reported successful DISPLAY=:0 direct NVIDIA RTX 3080 GL4.6 access and an actual runClient launch reaching GL, followed by a JEI 4.33.0 construction failure in ProxyInjector.inject because the reflected SidedProxy annotation was null. That is Main-provided runtime evidence, not a reviewer-run reproduction or a proven root cause. Graphics are not unavailable. No successful menu, stable frame loop, pack compilation/rendering or GL-state conformance follows from that launch.

## 4. Verdict

# PASS-WITH-CORRECTIONS

Counts: blocking=0; corrections=1; notes=0.

Interface changed: yes — correcting R27-1 changes the binding §5-incorporated vertex-input state contract and its receiver semantics. No interface was edited by this review.

The complete foundation architecture has one bounded native input-state correction. Preserve this review and earlier history, apply the owner/receiver fix in a separate authorized session, and obtain the required fresh independent whole-document literal PASS. Phase 1 remains unverified for downstream consumption; this report grants neither implementation clearance nor final §G5.3 integration approval.

## Resolutions

**Separate authorized architecture fix-up, 2026-09-08; not reviewer re-verification.**

- **R27-1 / NS-1 — addressed in owner architecture; verification pending.** P1 §0.30,
  D-P1-55 and §4.7.6 require temporary generic-zero disable for live conventional
  positions and a complete capability-legal capture-array allowlist. Every enabled
  non-admitted conventional/client-texture-unit/generic array is isolated regardless
  of owner; P10 gains no general foreign-array management or topology authority.
- **Restoration is actual-predecessor restoration.** §4.7.6 covers every affected
  enable/descriptor/per-pointer buffer association, global binding/client selector and
  legal current-value side effect, including partial setup and selector enumeration.
  Unsupported queries and the nonexistent ARB current generic-zero query are forbidden.
  Capture setup precedes list opening; restoration follows closing/abandonment so it
  cannot bake predecessor constants into retained geometry. Replay stays an external
  guard with no expired-pointer reconstruction. Failed rollback retains P7 containment.
- **Binding and acceptance anchors:** §5.2's vertex-input row incorporates D-P1-55;
  §8.1/§8.4 plan distinct valid generic-zero versus authenticated-position fixtures,
  unrelated enabled capture-array fixtures, captured/submitted value observation,
  complete state comparison and setup-failure rollback. State-write traces or recorder
  success alone are expressly insufficient driver evidence.
- **Receiving status:** §11.4 R-P1→P10-55 requests exact P10 adoption and P7 ordering/
  containment receipt. The P10 writer is commissioned concurrently; this resolution
  does not certify that receiving document. No additional owning API or topology
  prerequisite is missing for this defect; fresh owner/receiver whole-document review
  and live value/restoration proof remain outstanding.
- **Separately coordinated receipt:** at Main's request, active P1 §5.2/§11.4 P3
  references now name schema20 and its immutable post-archive `PackConfiguration.assets()`
  capability, without a P1 I/O grant. Historical schema19 §0.29 remains intact.

Only P1 and this separate append-only section were changed by this fixer. No runtime,
build, test, linter, formatter or validation command was run. The original review,
finding, counts and PASS-WITH-CORRECTIONS verdict remain verbatim above. All changed
contracts are **unverified**; this is neither literal PASS nor implementation/integration
clearance.
