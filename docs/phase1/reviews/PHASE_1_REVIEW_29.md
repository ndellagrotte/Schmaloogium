# Phase 1 — Independent whole-document verification, round 29

## 1. Review identity

- **Reviewed document:** `docs/phase1/v14/PHASE_1_DOC.md`, complete current document, including §§0.30–0.31 and every incorporated §5 semantic contract.
- **Frozen SHA-256:** `0361b7c866688e897431b4f97cf77a16163a55a472eaad150e8cfe055e303fd1`. This is the supplied review identity; Main confirmed its direct checksum matched and that no target writes occurred during this review. This reviewer did not run a checksum command.
- **Governing design:** `docs/design/v2.0-RC2/DESIGN.md`, as declared by the target header. RC3 and v3 were not substituted as global authority.
- **Method:** fresh §G1.2 whole-document adversarial review, not a patch-only check or acceptance of previous review resolutions.
- **Finding counts:** **0 blocking, 0 correction, 1 note**. The note identifies a separately owned receiver-side integration gap; it requests no Phase 1 amendment.

## 2. Authority and read set

Read the governing RC2 Part I, complete Phase 1 specification and literal doc gate; Research §0–§1 and the assigned §§5.1–5.3, 6.1, 7.2 and 12.2; and the additional contract, lifecycle, licensing, OQ and appendix material incorporated by Phase 1. Targeted checks included the capability probes and standard macro inputs, Appendix B's fixed sampler ownership, Appendix D's upload/readback requirements, Appendix A's program/source semantics, and the applicable texture and state contracts.

Read PD §2 and §16 and the standing §17/§18 do-not-inherit lists. Independently inspected the available Pintonium shim/glsm service interfaces and startup injection anchors. The available source trees are `reference-src/Pintonium-main` and `reference-src/Cleanroom-0.6.12-alpha`, not the historical snapshots named in older provenance entries. Read the available Cleanroom bootstrap layout and the reference license texts; these current trees were not relabeled as historical source or the selected runtime pin. Pintonium's current root COPYING contains GPLv3 text, so this review does not treat the historical LGPL characterization as current incorporation permission.

Read actual template build/settings/properties, dependency/extra/publishing scripts, wrapper and CI configuration, README, and all eight source/resource templates. Checked the Cleanroom MCP mixin guide and project-template material. These establish the planned migration's inputs, not an implemented module split or successful client launch.

Read `docs/PHASE_INTEGRATION_REVIEW.md`, including its original findings, resolutions and follow-ons; the geometry, U1 and texture-sidecar decision rulings; and Phase 1's prior review as historical evidence only. Read the relevant current producer/receiver §5 contracts and incorporated semantics in P2, P3, P4, P5, P6, P7, P10, P13 and P14. These bounded cross-phase reads were used to test handoffs, not to issue whole-document verdicts for those phases.

Checked the published [ARB_vertex_shader revision 0.83 specification](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_vertex_shader.txt) for generic-zero precedence, enabled-array consumption, display-list behavior and legal current-state queries.

No file was edited. No build, test, linter, formatter, validation command or client run was performed. No transcript, prohibited transformation source, Oculus pipeline/transform source, restricted library tree or decompiled implementation was used.

## 3. Literal Phase 1 doc gate

| Criterion | Independent assessment |
|---|---|
| Final module/package layout with testable dependency rules | Met. §§2.1–2.2 and §4.3 define the three modules and C-1 through C-4; §8 supplies the enforcement plan. The vertex, texture, frame and modernization placements retain the platform-free engine and conformance-to-engine-only edges. |
| D-1 through D-10 satisfied or deferred with named owner | Met. §11.2 provides every disposition, supported by the scope and handoff sections. Mechanism availability is not presented as later-phase implementation. |
| Complete pin table and re-verification procedure | Met. §4.2.6a explicitly controls the current wrapper/plugin/loader values and supersedes the dated July executable assertions. The CI mismatch, mod-only generated-source/Buildship placement, re-pin procedure and rollback are specified. Conditional jcpp admission requires a verified exact implementation-time pin, dependency closure, licensing and packaging; it is not an unpinned production dependency. |
| Glue-seam completeness against PD §2 | Met. §4.12 accounts for the version shim and both glsm inventories through facade operations, named policy/provider owners and explicit exclusions. Vanilla framebuffer binding and foreign textures are not confused with framebuffer zero or owned allocations. |
| Adopted bootstrap or justified deviation | Met. §4.13 separates FML lifecycle setup, the required post-`OpenGlHelper.initializeTextures` capability moment and the optional loading-complete hook. The departure from early GameSettings initialization and rejection of implicit class-scan registration are reasoned and carried into §5. |

The doc gate is distinct from the implementation gate. Planned seam tests, module builds and CI changes are not execution evidence.

## 4. Substantive adversarial audit

### 4.1 Research conformance and scope

No unmapped in-scope foundation contract or semantic inversion was established. The four startup probes are distinguished from the separately justified extension inventory used by standard macros. P5 retains texture-object/unit policy, P6 sampler uploads and synchronous center-depth behavior, P3 source/pack interpretation, P4 program state, and P7/P8 execution and traversal.

The compatibility baseline is preserved: no new UBO or instanced-draw contract substitutes for classic uniform semantics. Approved countInstances behavior is adjacent repetition of prepared native submissions under P7's authenticated scopes; it does not repeat world traversal, list capture, upload or event construction. Geometry conversion remains conditional, narrowly authorized and separately subject to primitive and runtime evidence.

All thirteen G9 sections are present and substantive. The assigned OQ-2, OQ-12, OQ-20 seam-hardness share and OQ-21 contain question, procedure, criteria and fallback/disposition material. Deferred runtime experiments are not missing architecture merely because they remain unexecuted. The documented fallback paths do not constitute authority to silently revise binding decisions during implementation.

### 4.2 Native geometry and vertex-input lifetime

The native source route, pre-link configuration and post-link effective-input inspection remain distinct. §4.7.4a rejects failed or unknown linked geometry rather than interpreting it as absence. Fullscreen submission uses the effective linked input, including the triangle-strip route for TRIANGLES on otherwise QUADS-capable profiles; incompatible categories cannot silently take the old path.

Independently re-derived the R27 failure: enabled generic attribute zero supersedes conventional position submission, and list capture consumes enabled arrays beyond the intended shader plan. Current §4.7.6 closes both mechanisms before consumption. LIVE_DRAW isolates generic zero; LIST_CAPTURE uses a complete capability-legal enabled-array allowlist and rejects safely unisolatable families before mutation.

The same contract covers actual per-pointer buffer associations, changed descriptors and enables, selectors, legally queryable current-value side effects and partial-setup rollback. It excludes nonexistent current-position/generic-zero queries. Setup occurs before list opening, closure before restoration, and replay remains a separately authenticated guard without reconstruction of expired pointers. P10's receiving design provides the required prepared capture input; P7's lifecycle and cached-model hook grant include the relevant invalidation and quiescence boundary. Their architecture adoption does not prove native driver behavior.

### 4.3 Texture, diagnostics and optional modernization

Owned, ordinary foreign and authenticated borrowed-depth handles retain distinct permissions. P5's receiver uses same-device borrowed-depth issuance, reattachment/version ownership and owned-only copy destinations; it does not turn a public marker into authority or delete a borrowed object.

§4.7.7's complete TextureParameters value and P5/P13 conversions retain target/format/capability legality, admitted mip extent, compare/swizzle state and binding restoration. P13 owns sidecar interpretation and effective identity. U1 does not invent suffix syntax, and the separate source-specific-default decision does not authorize illegal parameter coercion or foreign-object mutation. P14 receives the complete accepted value, not a three-field approximation.

The debug backend gate requires supported installed GL4.3/KHR behavior and the explicit flag, without an unnecessary debug-context prerequisite. Optional worker, asynchronous and extra-operation requests remain distinct from granted value/package placement and the mandatory synchronous baseline.

The GL-error contract preserves per-context ambiguity, cached-value replay and false attribution for clean or ambiguous replay. P6's existing replay algorithm does not resample providers. The separate typed delivery gap identified in §5 below remains unresolved at its receiver owner; this review does not convert P7's receipt into proof that delivery exists.

### 4.4 Schema21 receipt and interface honesty

D-P1-56 and §0.31 now refer active schema authority to P3 §5.3/CURRENT_SCHEMA_VERSION. The inspected P3 owner publishes 21 and requires matching containing configuration, nested ID and received inspection versions. Its payload-free profile selector, exact external TEXTURE_RECTANGLE spelling and usable-base-or-explicit-override classification are represented accurately by the receipt.

P1 neither becomes a new configuration parser nor acquires binary assets. Native `None/PreserveNative` requests, `None/CoreLayout/NativeLegacy` forms, source-layout precedence and same-build materialization identity remain the P3/P4 contract; Translate is not revived. The required assets capability remains same-load P3 ownership, transported or acquired only by the assigned owners. A historical schema number, receipt or inspection tree cannot upgrade old data or manufacture runtime authority.

## 5. Findings

### N1 — Preserve the unresolved P6 replay-result delivery gate

- **Severity:** note, external receiver/integration obligation; no Phase 1 correction requested.
- **Location:** Phase 1 §5.2, `ReplayAwareGLError` row, approximately line 5007; Phase 6 §4.11 and §§2.2/5; Phase 7 §5.2 and R7-6.
- **Claim:** Phase 1 specifies a complete immutable replay-result value and requires one result per triggering drained error, but the inspected Phase 6 publication surface does not provide the typed delivery mechanism that Phase 7 says it consumes.
- **Evidence:** P6 §4.11 ends in uniform-disable, WARN/Degraded and escalation behavior. Its runtime/factory declarations and exposed §5 contracts contain no ReplayAwareGLError sink, result collection or accessor. P7's R7-6 and consumption row nevertheless refer to copying P6's accepted replay result. A textual adoption claim is not a callable path. The independent P6 reviewer confirmed the same gap and owns its correction.
- **Impact and action:** P6 must specify typed production/delivery and its lifetime/failure semantics, with reciprocal P7 adoption, before the total attributed capture flow can be considered integrated. P1's existing type/cardinality/evidence contract is sufficient and should not absorb P6's transport ownership. This finding prevents an end-to-end replay-evidence claim, not the foundation doc gate.

No blocking or corrective finding was established against the reviewed Phase 1 architecture.

## 6. §5 impact and applicability

**§5 impact: yes.** D-P1-56 changes the monitored source/schema receipt, and this review includes its incorporated semantics as well as the current native, vertex, texture, debug, compatibility, diagnostics and placement contracts. This satisfies the purpose of a fresh whole-document review after that receipt changed; it does not merely reuse R28.

No Phase 1 §5 amendment is requested by this review. N1 requires a separately owned P6 publication amendment and receiver adoption, not a foundation redesign. Any subsequent substantive Phase 1 §5 change must pass the applicable fresh-review trigger again.

This result certifies only the reviewed Phase 1 documentation. It does not certify changed dependency/receiver documents, close the final fourteen-phase integration review, establish a working renderer or satisfy implementation clearance. Native client/VBO/list restoration, topology parity, OQ runtime work and T0–T3 evidence remain independently gated.

## 7. Final verdict

**PASS**

The frozen Phase 1 document meets its literal RC2 doc gate and the whole-document architecture checks. Exact counts are **0 blocking, 0 correction, 1 note**; the note records a real, separately owned P6 receiver gap without misclassifying it as a Phase 1 defect or claiming integrated runtime evidence.