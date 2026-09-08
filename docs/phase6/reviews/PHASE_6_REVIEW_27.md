# Phase 6 Review 27 — Independent whole-document architecture verification

## 1. Scope, authority, and result

**Target:** `docs/phase6/v1/PHASE_6_DOC.md`, complete current document, including its maintenance receipts, incorporated §5 contracts, decisions, failure rules, and implementation checklist.

**Assigned identity:** SHA-256 `eede6ee69d13cb86d49c0e2faa09ba3417b8b37dc52876e7204d97922464327b`, supplied by the assignment; no checksum was calculated. Intended report destination is `docs/phase6/reviews/PHASE_6_REVIEW_27.md`; this review did not write that file or change any repository file.

**Governing authority:** the target header selects `docs/design/v2.0-RC3/DESIGN.md`, not the newer design candidate. The review used RC3 Part I and the complete Phase 6 assignment, `AGENTS.md`, `docs/MOVES.md`, the assigned RESEARCH sections and complete Appendix D, shipped pack-author uniform/sampler documentation, and the assigned PD §6 and B1/B6 evidence. Current dependency §5 contracts and relevant incorporated semantics were compared with actual receiving/producing routes in Phases 1–5, 7–9, 11, and 13. The entire `docs/PHASE_INTEGRATION_REVIEW.md`, including original findings, Resolutions, and dated follow-ons, was read as an integration-claim ledger, not as fresh PASS evidence.

**Verdict: PASS-WITH-CORRECTIONS — 0 blocking findings, 1 correction, 1 note.** The architecture is coherent enough for a discrete correction rather than structural redesign. It does not yet satisfy the literal point-by-point barrier doc gate: several active Phase 6 passages retain the old after-participant lock/projection algorithm despite the adopted pre-participant effective-state contract. **Section 5 is affected.** No implementation, runtime, hook-application, conformance-tier, or final-integration clearance is granted.

## 2. Literal Phase 6 doc gates

| RC3 requirement | Independent assessment |
|---|---|
| Every Appendix D row has type, semantics, provider, cadence, and milestone | Satisfied architecturally by the complete §4.4 inventory and adjacent provider/event contracts. Later shadow, alias-ID, and atlas value owners are explicitly tagged. `entityColor` remains a real P7 v0.1 responsibility, not silently deferred to P9. |
| Smoothing formulas are explicit | Satisfied. §4.5 separates tick-domain half-lives from supplied render seconds, gives the closed exponential formula, first-sample initialization, directional wet/dry selection, quantization, and reset rules. The reference decisecond convention and B1 wiring defect are not adopted as pack semantics. |
| Barrier fulfillment is traced point by point to P4 | **Correction C1 required.** Sampler/built-in/custom positions, effective-provider identity, bound-only access, activity invalidation, and fixed-function exclusion are otherwise specified. Lock acquisition and effective blend observation currently have contradictory active orders. |
| CPU/GPU center-depth candidates have an evidenced decision | Satisfied as a design decision, not an equivalence experiment. D-P6-1 selects synchronous CPU readback and smoothing. The rejected macro candidate cannot safely substitute the declaration, has no granted reserved sampler, and has no demonstrated bit equivalence. The contributor stays Empty; P14 asynchronous readback remains a future gated item. |
| Notifier-to-producer audit exists | Satisfied with C1 affecting its blend explanation. The table gives distinct frame, post-camera, celestial, shadow, fog, effective blend, atlas, ID, color, held-item, and instance routes. Actual P7/P8/P9/P13 routes were checked rather than treating a producer name alone as delivery proof. |
| Pre-resize/pre-clear frame-begin ordering is exported in §5 | Satisfied. P6 §5.1 explicitly exports the order; P7 §4.3 first calls P6 against the completed prior framebuffer and only later enters P5 resize/clear handling. Current matrices are separately copied after camera setup, not at ordinal-zero clear. |

## 3. Correction C1 — Synchronize the active barrier trace with pre-participant lock acquisition

**Classification:** correction; medium priority; high confidence. **Owner:** Phase 6, coordinated with Phase 4’s separately owned export correction. **Section 5 impact:** yes.

**Affected Phase 6 passages:** §2.3’s flow around lines 570–575; §4.10’s barrier table around lines 1300–1311; §4.12’s final blend paragraph, lines 1499–1504; and §11’s D-P6-12 row around line 2216. These are active instructions, not merely dated historical review accounts.

They still prescribe sampler → built-in → custom → P4 lock application, and require P6 to project the effective provider’s `BlendSpec` onto a last observed underlying state because the lock supposedly has not yet been applied. In contrast, current P4 §4.10, lines 1671–1680, invalidates the old activity token, closes the old lease, binds the retained provider, acquires `StateService.lockAlphaBlend`, samples the actual effective blend through P7, and only then dispatches sampler/built-in/custom. P6’s own D-P6-32 already adopts this effective-state-before-dispatch behavior. P1 D-P1-57 and P7 D-P7-43 make attempted setters on held aspects no-ops with no event/cache mutation; P7’s registered receiving route samples `StateService.effectiveBlend` rather than forwarding attempted setter arguments.

**Trigger and impact:** when an effective program has an alpha/blend override, an implementer following the older P6 trace would run participants before the required lease acquisition/notification transaction and maintain an unnecessary future-state projection instead of consuming the owner’s actual effective state. That disagrees with when acquisition or notification failure must stop activation and with the authoritative value supplied to `blendFunc`. Even where projected factors happen to equal the eventual factors, the callback/failure ordering is a materially different contract. The literal point-by-point doc gate therefore cannot pass as written.

**Required correction:** make every active P6 flow, barrier-table row, blend explanation, and D-P6-12 rationale agree on the current order: authenticate selection; invalidate old activity; close the previous P1 lease; bind the effective program; acquire the new P1 duration lease and receive actual effective blend state through P7; then run exactly the sampler, built-in, and custom callbacks. P6’s built-in upload must consume that effective-state receipt, with no second lock or obsolete projection onto purported underlying state. Preserve immediate effective-change delivery, suppressed-attempt silence, disabled-blend zeros, invalidation-before-restoration, and existing replay-failure containment.

P6 §5.1 incorporates the event and participant semantics and §5.2 consumes P4’s barrier contract, so synchronize those export/receipt rows in the same revision. P4 §5.1’s barrier row at line 2006 independently retains the same old wording and must be corrected by its owner; this is not counted as a second Phase 6 finding. P4’s body, P1’s lock contract, P7’s actual receiver, and P6 D-P6-32 already establish the intended coordinated resolution.

## 4. Cross-boundary verification

### 4.1 Construction, sampler ownership, and activation

The exact eight-argument factory has the P5 resolver immediately after configuration and the required P7 replay observer after diagnostics. P5’s factory receipt and P7’s actual construction transaction agree; neither offers an old overload or no-op observer. P7 obtains the same P5 fixed policy before P4 compilation and injects its matching resolver into P6.

P6 passes the effective P4 sampler layout and authenticated stage/band to P5. Ready declarations preserve full sampler shape and deterministic unit/name order. Invalid results preserve owner evidence and degrade without uploads or a guessed replacement map. Direct compatible `watershadow` declaration controls the `shadow` alias; optimized-out locations do not decide that policy. P5 remains the sole physical shader texture-binding owner. P4 dispatches only the three credentialed positions; P11 feeds the third through P6, not through another participant.

Location acquisition remains callback-only and handle-free. Immediate events use only already-cached locations under P4’s current operation-free activity token. Cache identity is generation plus effective provider plus linked layout, so fallback children do not establish conflicting program caches. C1 is the one outstanding active choreography inconsistency found in this boundary.

### 4.2 Frame timing, snapshots, and celestial values

P7’s frame receiver completes P6 sampling/history/depth before touching P5 and captures current matrices later after `setupCameraTransform`. World transitions and unavailable prior dimensions prevent cross-world readback. Duplicate frame handling does not rotate history or advance counters.

P8’s pure angular policy supplies only angular data before camera capture. P7 retains the same sun/sky sample and authenticates the later actual main-camera association. P8’s computation constructs the unchanged P6 celestial event using that main matrix and publishes celestial/shadow matrix values before shadow activation. No hidden camera query, second frame clock, P6 shadow implementation dependency, or enlarged P6 event is needed.

P7’s finalized `timingReport()` actually calls `UniformRuntime.frameTiming` on the runtime and generation that rendered the frame, validates frame/world/checkpoint/input identities, and rejects absence, retirement, or mismatch. P2’s current v2 `/4` contract consumes the actual report and counters; it does not reconstruct them from sample ordinals or echo plan targets. Historical `/3` receipts do not override the current explicit `/4` cutover.

### 4.3 Replay and its consuming dispatch

P6’s immutable attempted batch is replayed without provider reevaluation. Cleanup errors remain false-only evidence, each original triggering error receives exactly one honest owner verdict, and replay probes are not additional original submissions.

P7’s collector is created before the factory call, has separate candidate/live/retiring endpoints, synchronously copies reports before callbacks return, preserves repeated equal errors and ordering, and routes broader escalation without inventing attribution. Its outer boundary explicitly consumes P6 replay-delivery degradation or immediate exceptions, closes admission, and prevents successful draw/sample acknowledgement. P2’s `/4` writer flattens the original `op`, subject, kind, detail, and attribution verbatim; every true or false error still fails T0. Final restoration and shutdown reports are included before COMPLETE. Thus the new report has a concrete receiver and failure path, not a documented producer with a silent drop.

### 4.4 Custom expressions and dynamic producers

P11’s refresh loop explicitly branches on `Accepted`, `SkippedAbsent`, and `Rejected`, increments all three authoritative counters, and returns matching completed or aborted-prefix counts. The six output command types, distinct bool encoding, fixed-input schema, seven excluded inputs, absent-location behavior, definition order, and P6 whole-batch rejection for invalid counts agree. P11 retains its own lifecycle CLOSE without forwarding CLOSE into P6.

P9 samples held values after accepted frame identity, publishes the exact four-value tuple and old-hand-light mapping, and restores scoped entity/TE predecessors before surrounding admission releases. P7 remains the color owner. P13 Known atlas dimensions are only availability; the actual P7 authenticated binding adapter maps Known/Unknown/non-atlas state into the existing P6 sink and rejects stale evidence. No stitch-only event certifies current binding.

### 4.5 Retirement and terminal release

P6’s terminal retirement is non-GL, permanently guards retained operational capabilities, and releases borrowed references only on successful retirement. P7 consumes the closed results and reason-specific ordering: unpublished abort requires no publication; replacement waits for actual old-barrier invalidation, not a pre-release Rejected result; shutdown retires before P4 atomic teardown. Final restoration events precede retirement, and borrowed adapters remain alive even if their shadow/texture owners have entered earlier quiescence. P8’s terminal fixed-function release is distinct from P4 atomic teardown, so its release-before-platform-restoration obligation does not contradict P6’s shutdown order.

## 5. Note N1 — Preserve the pinned-source licence distinction

**Classification:** note; no §5 API change and no present architecture blocker.

The target’s §0.1 labels its Pintonium reference files LGPL-3.0. The old local `reference-src/pintonium-9c2fcc1` checkout was unavailable, so the allowed files were read from the exact upstream pin rather than substituted with the local moving `Pintonium-main` tree. The pinned [root LICENSE](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/LICENSE) is headed **GNU General Public License, version 3**, not LGPL. The inspected files do not by themselves establish a separate per-file LGPL grant. This is a provenance warning, not a legal determination about each file’s entire history.

The current deliverable is architecture and adopts no copied implementation, so this does not invalidate its pure formulas or observed mechanics. Qualify the blanket source label before future reuse; any incorporation must establish the applicable file licence and satisfy the governing compliance process. A historical PD label or old review cannot supply that missing provenance.

## 6. Independent reference evidence and limits

Pinned permitted reads corroborated the material reference distinctions: [ProgramUniforms](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramUniforms.java) has distinct acquisition/update buckets; [ProgramSamplers](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/program/ProgramSamplers.java) uses a one-time integer initializer and dynamic unit allocation rather than the required P5-owned fixed-map policy; [SmoothedFloat](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/transforms/SmoothedFloat.java) uses decisecond half-lives; [PackDirectives](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/shaderpack/properties/PackDirectives.java) contains the wet/dry assignment defect; and [SystemTimeUniforms](https://raw.githubusercontent.com/Xplodin/Pintonium/9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/SystemTimeUniforms.java) corroborates the counter wrap and accumulated-time reset shape. The nullable notifier hub and its use were inspected, but that alone is not claimed as an exhaustive proof of every assignment in the pinned platform tree.

The permitted matrix/capture, center-depth, smoothing-vector, and common-uniform sources were also inspected. The old first-clear capture location is not treated as proof that current main-camera matrices exist there; P6/P7’s explicit later post-camera route correctly avoids that inference. Reference implementation mechanics remain evidence subordinate to the shipped contract and selected design.

No chatlogs, root transcript files, forbidden transformation directories, relocated GLSL libraries, or copied transformation implementation were read or searched. Searches were restricted to named documents/source paths. No build, test, formatter, linter, checksum, or runtime validation was run; no files were edited. Historical review bodies and integration rulings were not promoted into new passing evidence.

## 7. Disposition

Main should preserve this report, apply C1 in P6 while the P4 owner corrects its matching export, synchronize the affected §5 semantics, and obtain fresh review of the changed current document. The source-licence note remains a future-incorporation guard. Remaining owner reviews, leaf reviews, implementation proof, and final integration are separate gates; this PASS-WITH-CORRECTIONS closes none of them automatically.

## Resolutions

### C1 — Actual pre-participant effective state

D-P6-33 updates the active §2.3 flow, §4.10 table, §4.12 explanation and D-P6-12 disposition:
P4 closes/binds/acquires and publishes coherent effective blend before callbacks; P6 snapshots
the observed cell rather than projecting a later BlendSpec. Failed acquisition/notification
cannot reach participants. Disabled blend remains zero; activity and replay containment remain.
P4 D-P4-35 corrects the matching binding export. Fresh §5 verification remains required.

### N1 — Qualified licence provenance

D-P6-34 and §0.29 record the independently inspected GPLv3 root LICENSE at the cited pin.
The historical LGPL label is not promoted into a file-specific grant. No code was copied;
future reuse requires applicable file licensing/notices. Original review text and verdict
remain unchanged, and this resolution supplies no implementation or final-integration clearance.