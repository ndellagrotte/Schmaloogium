# Phase 14 — Independent whole-owner architecture review

**Round:** R4 — frozen attempt7  
**Owner:** `docs/phase14/v1/PHASE_14_DOC.md`  
**Frozen inventory SHA-256:** `2da318b267e6b10b9e51d09ff01ba4927de768a4207dd5edaf3bd466e99e8d41`  
**Identity source:** `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_7.json:84–87`. This is the supplied inventory identity, not a checksum execution claimed by this review.

## Scope and selected authority

This is a fresh review of the complete current owner, §§0–12, not a review limited to recent correction receipts. The governing design is **`docs/design/v3/DESIGN.md`**, selected by the owner's header at `docs/phase14/v1/PHASE_14_DOC.md:11` and explained at lines 23–36. I used the per-owner resolution rule in `docs/MOVES.md:80–100`, rather than treating the newest design revision as globally governing. The authority read included v3 Part I, its Phase 14 specification at lines 2514–2588, the assigned research sections in `docs/research/v1/RESEARCH.md`, and the bounded Pintonium design/reference material relevant to A1–A5 and the do-not-inherit rows.

I read the current dependency §5 contracts and their incorporated definitions and followed the actual receiving behavior: Phase 5 physical binding and resize/lifecycle dispatch; Phase 6 `CenterDepthSource` and accepted frame-begin use; Phase 7 build/frame/fixed-function/recovery routing; and Phase 13 parameter, publication, lease and retirement handling. Phase 1's facade, complete parameter and target-bearing allocation contracts, Phase 4's synchronous compile/fixed-function receiver, Phase 11's measurement handoff, and Phase 2's evidence boundary were also checked where the owner crosses those interfaces. No sibling owner is certified by this review.

## Independent checks

- **A1:** traced complete owned-object parameter establishment through the sampler-backed path and the actual sampler-zero fixed-function receiver. The current design no longer relies on demotion or an invented rebind to make ordinary FINAL safe. Rejected/degraded Phase 5 preflight does not reach the successful physical-bind branch; optional batching is not treated as granted.
- **A2:** checked the strategy boundary, active-unit/cache cooperation, target-bearing allocation admission and native object lifetime semantics. The two concrete lifecycle defects below remain.
- **A3:** checked the current synchronous Phase 6 receiver and its prohibition on an uncontracted queue. Sample-age adoption remains an explicit prerequisite. Reviewed complete identity invalidation, four-byte pack-layout normalization/restoration, failed-transfer/map/unmap nonpublication, completion-based warm-up, timeout accounting and synchronous fallback. No asynchronous age grant or native imperceptibility result is inferred.
- **A4:** followed synchronous Phase 4/7/13 construction as the current receiving branch. Split compilation and step-8 upload remain unadopted proposals. The future two-direction fence/producer-flush ordering, identity revalidation, cancellation ownership and quarantine conditions do not authorize present asynchronous execution.
- **A5:** checked the adopted capability-plus-flag activity gate against Phase 1 and the permitted debug reference. Reviewed UTF-8 object-label budgeting and real/virtual group recovery. Native texture labeling is nevertheless scheduled before the target-bearing facade can instantiate the texture; correction C14-R4-1 addresses that ordering conflict.
- **A6/A7 and §§6–12:** reviewed measurement exclusions, ownership attribution, ledger decisions, native-versus-recorded evidence separation, failure containment, threading, OQ specifications, milestones and implementation checklist. Optional group instrumentation is not mistaken for an existing receiving branch. The implementation-gate waiver in C14-R4-3 remains inconsistent with the selected authority.

## Substantive corrections

### C14-R4-1 — Defer target-specific texture creation and native labeling until the target is known

**Severity:** P2 — concrete facade/backend lifecycle conflict.  
**Owner anchors:** `docs/phase14/v1/PHASE_14_DOC.md:799–815` and `:1320–1326`; receiving-value receipt at `:1795–1810`; exposed debug contract at `:1629–1630`.

The strategy requires `createTexture(int target)` and selects `glCreateTextures`, while A5 requires the backend to issue the native object label at facade creation time. The actual facade is `TextureService.create(String debugLabel)` followed by `allocate(TextureHandle, TextureSpec)` (`docs/phase1/v14/PHASE_1_DOC.md:3364–3367`); its target is supplied by the later `TextureSpec`, and the first successful allocation fixes that target (`:4549–4555`, `:4577–4578`). This matters for the admitted 1D/2D/3D/RECTANGLE sources, not merely a hypothetical future texture type. A backend cannot choose a native creation target at `create(String)` without inventing it. Generating an untyped name does not solve the native-label timing requirement: the [Khronos `glIsTexture` reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glIsTexture.xhtml) explicitly says a generated but never-bound name is not yet the name of a texture, and [KHR_debug §5.5.8](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/KHR/KHR_debug.txt) rejects an invalid object name. The resulting creation-time label errors can disable labels for the session through the owner's own failure branch at `:2030`; binding a guessed 2D target instead would make later non-2D allocation invalid.

**Minimal owner fix:** distinguish logical handle/label creation from native texture materialization. Retain the original label at `create`, instantiate the native object only when the first admitted allocation supplies its exact target, and emit the native label after that object exists. State the corresponding existing depth-initialization path's known 2D target and deletion-before-materialization behavior. Carry this timing law into §5 and the coverage criteria without adding a second caller-owned naming path or inventing a facade target default.

### C14-R4-2 — Exclude deletion of bound objects from unconditional binding neutrality

**Severity:** P2 — impossible native invariant.  
**Owner anchor:** `docs/phase14/v1/PHASE_14_DOC.md:861–868`; repeated decision at `:2553` and evidence contract at `:2181`.

D-P14-7 includes `delete` in the unconditional promise that observable texture-unit and framebuffer bindings are identical before and after every call under every tier. The consumed facade has `bindToUnit` and `delete(TextureHandle)` with no unbound-object precondition (`docs/phase1/v14/PHASE_1_DOC.md:3376–3379`). For deletion of a currently bound texture, the [Khronos `glDeleteTextures` reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glDeleteTextures.xhtml) requires the binding to revert to zero. DSA does not alter that behavior. Saving and rebinding the deleted name cannot preserve the deleted object's identity or contents and is not a valid restoration. Consequently the stated invariant and its universal native check cannot both be implemented for the admitted bind/delete sequence. Existing owner-controlled binding drains, such as `docs/phase5/v1/PHASE_5_DOC.md:2382–2389` and `:2595–2598`, are useful retirement discipline but do not make native deletion universally binding-neutral.

**Minimal owner fix:** restrict binding neutrality to creation/edit/copy operations for which preservation is possible. Describe deletion separately as lifetime-ending cleanup with the existing binding/cache cooperation and retirement-drain requirements; never restore a deleted binding. If an unbound-only deletion precondition is intended instead, explicitly contract its admission/rejection behavior and reconcile the receiving cleanup paths. Update the decision and native evidence criteria so a required deletion side effect is not classified as a backend neutrality defect.

### C14-R4-3 — Keep the stall-reduction implementation criterion unsatisfied until its waiver is adopted

**Severity:** P2 — implementation acceptance can be falsely satisfied.  
**Owner anchor:** `docs/phase14/v1/PHASE_14_DOC.md:2330–2334`; upstream request at `:2705–2709`; propagated no-block statement at `:2441–2442`.

The selected governing implementation gate requires measurable pack-switch stall reduction without T1 regressions (`docs/design/v3/DESIGN.md:2583–2584`). The owner correctly recognizes that a failed OQ-15 or declined receiver extension may prevent that result, but then says the gate **must be read as satisfied** by conditions 1–2 while condition 3 is deferred. The only authority offered is an outstanding request to change the governing design. Thus a failed spike can currently produce a passing Phase 14 implementation gate without the result the selected gate requires. Designing and shipping the safe synchronous fallback is valid; declaring the separate performance criterion satisfied is not established by that fallback.

**Minimal owner fix:** retain the synchronous fallback and explicit upstream clarification request, but leave the stall criterion unmet/deferred and prohibit a full implementation-gate PASS until either it is demonstrated or a governing amendment is explicitly adopted. Make the related milestone/no-block wording distinguish safe fallback delivery from satisfying the complete selected gate. This does not request running the spike at this architecture gate.

## Notes

### N14-R4-1 — Preserve the optional group-instrumentation boundary

**Severity:** informational, not an additional correction.  
**Anchors:** `docs/phase14/v1/PHASE_14_DOC.md:1571–1577`, `:1724–1736`, `:2748–2749`; actual Phase 7 receiving summary at `docs/phase7/v1/PHASE_7_DOC.md:3948` and disposition at `:4499`.

A7's frame/pass segmentation depends on A5 group records. The owner explicitly leaves those Phase 7 call sites optional/unadopted; the actual receiver presently supplies JFR attribution, not a public per-pass timing or debug-group contract. I therefore treat the audit as a specified, instrumentation-dependent procedure, not an executable capability already received by Phase 7. The synchronous baseline is not blocked and no acknowledgment text is used as proof that the missing call sites exist.

### N14-R4-2 — Current source corroboration is not historical-pin or license clearance

**Severity:** informational evidence limitation.  
**Anchors:** historical source inventory at `docs/phase14/v1/PHASE_14_DOC.md:48–53`; bounded reference rows at `:493–498`; path history at `docs/MOVES.md:45–54`.

The available `reference-src/Pintonium-main/` material corroborates the sampler, DSA, debug and fence patterns inspected, including the rejected pop-then-conditional-push shape. It does not independently reproduce the owner's historical `pintonium-9c2fcc1` identity or turn historical deployed observations into current backend evidence. Source-license confidence remains qualified: this review grants neither permission to copy implementation nor a license-compliance certification. The clean-room pattern boundary and forbidden-source restrictions remain in force.

## Limitations and disposition

No files were edited. No formatter, linter, build, test suite, native experiment or validation command was run; this is architecture review, not implementation/runtime certification. Forbidden chatlogs/transcripts, Oculus pipeline/transform implementation, relocated/glsl-transformer implementation and OptiFine decompiled implementation were not used. Prior reviews and correction receipts were not verdict authority. Driver correctness, allocation claims, async imperceptibility and OQ outcomes remain subject to the owner's specified implementation evidence.

The owner has a complete architecture and bounded corrections; structural FAIL is not justified. **Counts: corrections=3; notes=2. Section 5 impact: yes**, chiefly the target/materialization/native-label lifecycle and its receiving contract.

**PASS-WITH-CORRECTIONS**

## Resolutions — all13 attempt7 architecture correction (2026-09-08)

The frozen review body and **PASS-WITH-CORRECTIONS** verdict above are preserved.
These are owner documentation corrections, not fresh review approval or runtime evidence.

- **C14-R4-1 — addressed by D-P14-40.** P14 §§4.2.1/4.5.2 distinguish targetless
  logical handle/retained label creation from first admitted exact-target native
  materialization, then native labeling and storage. All 1D/2D/3D/RECTANGLE targets
  are explicit; known2D first depth initialization follows the same sequence.
  Preflight rejection creates no name, never-materialized deletion issues no native
  delete, and a name created before failure remains accounted for exactly-once owned
  cleanup with no usable-storage publication or retargeting. §5.1's debug/lifetime
  rows and §5.5 carry the receiver law; §8.1/§8.2 and §12 items5/6/12 carry model
  and future-native coverage. P1 coordination is routed to FoundationIngress;
  this resolution does not claim an unobserved reciprocal grant.
- **C14-R4-2 — addressed by D-P14-41 and narrowed D-P14-7.** §4.2.3 restricts
  binding neutrality to creation/edit/copy and separates deletion as lifetime-ending:
  affected texture-unit/read-draw-FBO bindings become zero with cooperating cache
  updates, unrelated bindings/active unit survive, and no deleted name is restored.
  Existing P5/P13 leases, reverse-order retirement and borrowed nondeletion remain.
  §5.1/§5.5, §8's model/native deletion and failure cases, §11.1 and §12 agree;
  required native deletion side effects are not classified as neutrality defects.
- **C14-R4-3 — addressed by D-P14-42.** §9.2 condition3 remains unmet/deferred
  until measured or changed by an explicitly adopted governing amendment. §10.1's
  failed-spike outcome, §11.4's unadopted request and §12's gate work item/milestone
  summary distinguish safe synchronous delivery from complete implementation-gate
  PASS. No pending request supplies authority, no async API is granted, and no spike
  result, stall reduction or full gate satisfaction is claimed.

No validation commands, build, tests, formatter, linter or native experiment were run.
The corrections retain the review's confidence/source restrictions and optional
group-instrumentation boundary; changed architecture awaits Main's fresh review.

**Reciprocal receiver receipt:** FoundationIngress reports P1 **D-P1-71** landed in
actual §4.7.3 lifecycle, §5 and §8/checklist, receiving D-P14-40/41's exact-target
materialization/labeling, failed-name cleanup, unmaterialized deletion, zero-binding/
cache cooperation and final-use lease-drain law without a facade signature or async
grant. This is an owner documentation receipt, not native verification.
