# Phase 8 — Fresh Whole-Document Architecture Review R9

## Frozen owner and disposition

- **Owner:** `docs/phase8/v1/PHASE_8_DOC.md`, all sections **0–12**, including current D-P8-22–30 amendments and incorporated interfaces.
- **Frozen attempt-5 SHA256:** `77c6a9f48d37d5a2499953ed4afa1813b9915ff7dccd1e57c71ac7c4d5a0b44f`.
- **Inventory authority:** `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json:40-44` identifies this owner and review round 9. The digest is the supplied frozen identity; this review did not run a checksum-validation command.
- **Required corrections:** 2. **Nonblocking notes:** 1. **§5 impact:** yes.
- No repository files were edited. No build, test, lint, formatter, implementation, or runtime-validation command was run.

## Scope and governing authority

This was an independent whole-owner review, not merely a check of the R8 resolution. I read the complete owner before concluding, its header-selected `docs/design/v2.0-RC3/DESIGN.md` Part I §G0–G12 and Phase 8 assignment at `:1957-2034`, and the governing `docs/research/v1/RESEARCH.md` §§0–1/4.5, Appendix A.3, B.2/B.3, D.2/D.3, E.1 rows 1–2 and the applicable F.1 flags. RC3 remains this owner's governing design; no global-newest design was substituted. `docs/MOVES.md` was consulted to distinguish moved citations from genuinely unavailable historical reference trees.

I read the current Phase 4/5/6/7 §5 contracts and the incorporated load-bearing declarations and dispatch paths. Reciprocal checks additionally covered P1's package/duration-lock grant, P2's nested report consumer, P9's authenticated shadow ID/deferred-batch receiver, and P13's publication/lease/retirement receiver. Historical PASS records and prior review conclusions were not used as current grants or proof. Those adjacent reads establish P8 interface compatibility only; they do not certify another phase.

## Independent checks

1. **Forced traversal reaches the actual vanilla rebuild.** P8 `:1203-1257` now explicitly handles the clean dirty bit, frozen debug helper/capture flag, camera-cell seed visibility, ordinary path/compiled occlusion and all three visit-marker sites. The vanilla control flow independently supports the correction: [RenderGlobal.java:840-1020](https://github.com/KealJones/mc-1.12.2-source_files/blob/master/src/minecraft/net/minecraft/client/renderer/RenderGlobal.java#L840-L1020) tests the debug helper and dirty bit before replacing `renderInfos`, reads `renderChunksMany`, consults seed facings, and performs pending-update scheduling after traversal. Its [dirty setter at :2639-2642](https://github.com/KealJones/mc-1.12.2-source_files/blob/master/src/minecraft/net/minecraft/client/renderer/RenderGlobal.java#L2639-L2642) sets the actual dirty bit. Cleanroom MCP independently resolves `func_174979_m()V`, `field_175612_E`, the five last-view caches, both debug fields, `renderInfos`, `viewFrustum` and `chunksToUpdate` to the names used here. The authenticated exactly-one rebuild witness is correctly distinct from application-health evidence. The remaining defect is its published health representation, C1 below—not a renewed claim that merely changing the frustum forces traversal.
2. **Visibility/cache/queue restoration is substantively specified.** P8 `:1212-1252` preserves the exact main-list reference rather than reconstructing it; keeps main setup position inputs unchanged; restores the five view caches and both debug fields; merges captured pending identities into the current queue even after exceptions; and conservatively retains real dirtiness. Successful shadow forcing alone does not force another clean main rebuild. Failure forbids main admission without retry or traversal replay. These rules match the inspected scheduling tail rather than rolling back completed chunk work.
3. **Camera, culling and render-order contract.** P8 §§4.5–4.9 retain explicit ortho/perspective matrices, column-major serialization, finite angular boundaries, signed-remainder orthographic-only snapping, light-parallel plane synthesis, conservative degeneracy handling, full/prism traversal, exact opaque layer order, Forge pass0/single depth split/optional translucent/pass1, and P5-owned PCF/mipmap operations. The frame-bound compute signature and unchanged P6 event match `docs/phase6/v1/PHASE_6_DOC.md:762-808` and `docs/phase7/v1/PHASE_7_DOC.md:2917-2944`. Available permitted Pintonium sources corroborate the portable formulas and the reference's perspective-entry/order discrepancies; they are not treated as target-runtime or historical-pin proof.
4. **Actual Forge receiver.** The mandated event query returned no event; a second framework lookup found `ForgeHooksClient.setRenderPass(int)`. Available source confirms the setter/getter at `reference-src/Cleanroom-0.6.12-alpha/src/main/java/net/minecraftforge/client/ForgeHooksClient.java:234-238` and `MinecraftForgeClient.java:50-53`, and consuming pass predicates/startup behavior in that tree's `patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch:3-86`. P8 preserves pass/counter restoration and does not invent an event or repeat world callbacks for prepared submissions.
5. **Planning, neutralization and terminal receiver.** The registry-independent `(policy,hookHealth,requested)` input, accepted-minima provenance and pre-admission disabled-estate disposition are received in P7 `:862-894`, `:926-937` and §5.3. Current schema22 rejection is received in P7 `:3479-3486`, without an older-schema fallback. P8's terminal release at `:911-948` matches P4's authenticated release/duration lease at `:1695-1707` and `:1761-1790`, P1's actual guarded override at `:3448-3477`, and P7's terminal dispatch at `:2967-2981`. Only FixedFunction plus successful independent cleanup permits continuation; release failure cannot be hidden by successful platform restoration. C2 identifies the remaining incorrect estate receiver on the new construction path.
6. **Ownership and lifetime.** The selected-program/expected-publication lease/five-argument physical bind/Bound-only transfer flow agrees with P5 `:1980-2125` and P13 `:1210-1274`. Completion, abort and neutralization invalidate use but not binding closure. P8 retains neither invocation credentials nor P13 owner authority. P9 `:750-760` and `:813-832` preserve authenticated shadow IDs and original deferred-batch order beneath P8's single traversal. Optional ungranted async/batch APIs remain ungranted.

## Required corrections

### C1 — Specify a lossless health-row encoding and synchronize its actual cardinality

**Severity:** correction / P2. **Owner patch anchors:** `docs/phase8/v1/PHASE_8_DOC.md:1494-1496` and `:1553-1560`. Supporting owner evidence: `:494-497`, `:1485-1489`, `:1504-1508`, `:1523`, `:1752`.

D-P8-29 requires the actual `(3,1,1,1,1)` traversal vector, each restoration member/accessor and the separate setter-resolution/rebuild-anchor evidence to cross the owner boundary unchanged. The only published `ShadowHookRow` still contains one `int expected` and one `int actual` per hook ID. There is no declared vector field, anchor-child algebra, or canonical flattened anchor-ID table. Moreover, the ledger now has **nine** top-level IDs after adding H8-REBUILD-01, while P8 §5.5 still promises **eight**. This is not resolved by the receiver's prose receipt: P7 `docs/phase7/v1/PHASE_7_DOC.md:1758-1767` says to copy rows/subrows, but its actual `HookApplicationSubrow` at `:2784-2793` is scalar-only and `:1836-1845` still requires eight rows. P2 `docs/phase2/v2/PHASE_2_DOC.md:1354-1367` likewise receives scalar rows and all eight existing IDs.

**Observable contract failure:** an implementation cannot both preserve every required actual anchor count unchanged and satisfy the declared eight-row scalar projection. It must omit a new row, collapse distinct observations, or invent ungranted fields/IDs. A summed count cannot distinguish a missing anchor compensated by an overmatched anchor, precisely the condition §4.13 forbids. A fingerprint can commit to hidden detail but cannot transmit those required exact counts.

**Minimal P8-owned fix:** retain the existing scalar record and publish a complete canonical flattened row table with unique stable IDs for every independently checked anchor/accessor/resolution observation, including separate evidence for the three visit sites and forcing setter/rebuild entry. Define expected counts, actual-count meaning, ordering, aggregate enablement and fingerprint inputs. Treat the top-level H8 IDs as documented groups rather than an eight-row wire constraint. Alternatively, explicitly grant a typed vector/child-row algebra, but that requires a larger receiver/schema cutover. Update P8 §§2/4.13/5 and its test/checklist references together. Request matching P7 copy/freeze/validation and P2 dense-row/count/fingerprint receiver changes; neither may infer or silently drop evidence. Keep the per-invocation rebuild witness separate from frozen transformation-health counts.

**Cross-owner disposition:** P7/P2 require coordinated receiving edits and fresh review after the P8 owner chooses the exact encoding. This is one P8-owned correction, not certification or an additional finding against those owners.

### C2 — Unwrap the published estate before dispatching the shadow result

**Severity:** correction / P2. **Owner patch anchor:** `docs/phase8/v1/PHASE_8_DOC.md:749-754`. Related active consumption: `:819-823`, `:1591-1593`.

The new D-P8-27 construction branch explicitly calls `PublishedBufferEstate.shadow()`, and the binding §5 table advertises that as a consumed P5 operation. P5 grants no such member. Its exact `PublishedBufferEstate` declaration at `docs/phase5/v1/PHASE_5_DOC.md:658-661` contains only `generation`, `Optional<BufferEstateView> estate`, and `resources`; `shadow()` belongs to `BufferEstateView` at `:739-764`. P5 §5 incorporates those exact declarations at `:2515-2518` and `:2546-2552`. Off publication's absent estate is separately described at `:731-734`; it is not `ShadowEstateNotRequested` or a coherent shadow-only unavailable result.

**Observable contract failure:** the new pre-admission neutralization path cannot be implemented against the granted publication API as written, and the missing optional-estate branch leaves no specified disposition for an absent/off wrapper before the three-way shadow switch. Introducing a forwarding method would silently widen P5 rather than consuming its actual contract.

**Minimal P8-owned fix:** explicitly obtain and authenticate the same accepted publication's present `estate()` once, verify the view/publication generation and intended tuple, then invoke that `BufferEstateView.shadow()` and use the returned `ShadowEstateAvailable.view()` for neutralization. Empty/off or mismatched estate must follow existing failed-composition/off containment before admission; invocation must reject invalid pre-mutation publication input rather than treating it as normal shadow absence. Preserve the accepted wrapper/view—do not fetch another current estate to repair failure. Correct the same receiver name and unwrap rule in invocation §4.2 and incorporated §§5.3/5.4. No P5 extension is needed.

**Cross-owner disposition:** P7's D-P7-46 receiver at `docs/phase7/v1/PHASE_7_DOC.md:926-937` and incorporated composition path must receive the explicit wrapper-to-view dispatch. P5's declaration itself is not assigned a correction here.

## Nonblocking note

### N1 — Historical pinned reference absence is not equivalent to absent permitted corroboration

The historical `reference-src/pintonium-9c2fcc1/...` files named by P8 §0.1 and the historical Cleanroom 0.6.6 tree were unavailable at those paths. MOVES does not authenticate today's differently named snapshots as byte-identical replacements. Available permitted corroboration was read narrowly from `reference-src/Pintonium-main/modern/src/main/shaders_java/net/irisshaders/iris/shadows/ShadowMatrices.java:1-175`, `ModernShadowRenderer.java:107-135,225-454`, and its `common-shaders/.../CommonShadowRenderer.java:107-129` and `CelestialUniforms.java:20-124`; current Cleanroom source and MCP supply the separately qualified platform evidence above. `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:591-613` continues to establish the documented limitation of that historical 1.12.2 shadow reference. No current snapshot was represented as the missing pinned commit. This is an evidence limitation, not a demand for future runtime execution at this architecture gate and not another required correction.

## Final verdict

**PASS-WITH-CORRECTIONS** — two localized current contract defects, both affecting incorporated §5 interfaces. The forced dirty/debug/occlusion traversal and restoration design is substantively specified and independently corroborated; the health transport and publication-wrapper receiver still require the concrete corrections above. No structural rebuild is justified. Owner bytes remain unchanged. Fresh corrected-owner and affected-receiver review remains required; implementation, native/runtime behavior, pack tiers, sibling certification, IR-01 and final §G5.3 integration clearance are not granted by this review.

## Resolutions

Architecture-only fix-up, 2026-09-08. The frozen identity, original findings/counts, independent
checks, N1 and PASS-WITH-CORRECTIONS verdict above are preserved as historical review evidence.

- **C1 — corrected in owner architecture, D-P8-31.** P8 §§2/4.13/5 now retain the existing
  scalar `ShadowHookRow` and publish §4.13.1's full 59-row ASCII-ordered stable catalogue.
  Each independently audited target has expected=1 and its actual count preserved: three visit
  sites separately, every restoration/entity field resolution and getter/setter, forcing setter
  resolution and rebuild-entry transformation, and remaining ledger resolutions/anchors.
  Nine top-level IDs are group labels only. Exact dispositions, non-cloud aggregate, dense
  rejection and `ShadowHookHealth/flattened-v1` fingerprint encoding are binding. Runtime
  rebuild-entry witness stays invocation-local and never replaces frozen transformation evidence.
  §§8/9/11/12 carry behavioral boundary cases, staging and receiving obligations. P7/P2 must
  adopt the owner catalogue unchanged without eight/nine-row limits or inferred/summed counts.
- **C2 — corrected in owner architecture, D-P8-32.** §§4.1/4.2/5.3/5.4 obtain the same
  accepted `PublishedBufferEstate.estate()` once, authenticate its present `BufferEstateView`,
  accepted wrapper/view generation and intended tuple, then call `BufferEstateView.shadow()`.
  Available uses its own `view()` and accepted generation for neutralization. Off/empty or
  mismatched estate contains composition before main admission; invocation rejects before
  mutation. No replacement-current lookup, forwarding member or P5 extension is introduced.
  §§8/11/12 specify the boundary fixtures and P7 D-P7-46 receiving update.
- **Coordinated receipt — D-P8-33.** Exact-current schema23 for containing configuration,
  nested IDs and received inspection, plus `MaterializedSource-v23`; prior numeric receipts
  historical. Nine metadata-only trees/projectionVersion=1 and sole P9 resolution remain.
- **N1 disposition.** No historical reference claim is upgraded. This fix-up reads no forbidden
  implementation or reference replacement and makes no new pinned-source/runtime claim.

**§5 changed; unverified.** Main owns the required P7/P2 receiving edits and integration
records. Fresh corrected-owner/affected-receiver whole-document review is still required.
No build, test, formatter, lint or validation command was run; no self-PASS, implementation,
native/runtime, pack-tier, sibling, IR-01 or final §G5.3 clearance is claimed.
