# Phase 8 — Fresh whole-document architecture review 8

## Verdict and counts

**Verdict: PASS-WITH-CORRECTIONS**

- **Blocking findings: 0**
- **Owner corrections: 1**
- **Receiver notes: 1**
- **§5 impact: true**

Reviewed owner: `docs/phase8/v1/PHASE_8_DOC.md`, entire document (§§0–12), against the commissioned frozen SHA-256 `804056345983d0799c986acd2ebde915123e018d5503e6147bb6991ff03df700`. This is a whole-owner architecture verdict, not certification limited to the latest amendments. The document is substantially complete, but its second vanilla terrain setup is not guaranteed to execute the traversal it describes. That is a localized, repairable implementation-reachability defect, not grounds for rebuilding the architecture.

## Review basis and coverage

Read the header-selected `docs/design/v2.0-RC3/DESIGN.md` Part I §G0–§G12 and Phase 8 assignment, including §G1.2, §G1.3 and §G5.3. Read governing `docs/research/v1/RESEARCH.md` §§0–1, §4.5, Appendix A.3, B.2/B.3, D.2/D.3, the actual E.1 rows 1–2, and F.1. Read Phase 4–7 §5 contracts, including the incorporated activation/release, shadow-estate terminal results, uniform event/retirement/failure rules, and Phase 7 construction and frame-dispatch paths needed to assess Phase 8. Checked the exact Phase 1 shadow-package grant.

The review covered all owner sections: scope and adjacent ownership; every public shape and core invariant; all four conformance tables; construction, invocation, world sampling, reversible state, camera/celestial math, frustum and traversal, content ordering, prepared submissions, mipmaps/PCF, uniforms/barrier, blob suppression, teardown, and hook ledger; cross-phase exposure/consumption; failure, threading/performance, tests, milestones, OQs, decisions, handoffs, and implementation checklist.

Permitted evidence included PD §10, the shipped pack-author shadow/uniform/texture/directive tables, the shadow-only behavioral digest, narrowly selected available Pintonium camera/celestial/render-sequence files, current available Cleanroom Forge pass sources/patch, and Cleanroom MCP symbol/API queries. The assigned `render pass entity` event query returned no matching event; the broader API lookup confirmed `ForgeHooksClient.setRenderPass(int)`. Its getter and the patched entity/tile-entity dispatch substantiate the specified Forge pass protocol.

## Correction R8-1 — Force the shadow-specific traversal to run

**Owner locations:** `docs/phase8/v1/PHASE_8_DOC.md` §4.2 step 8 (line 837), §4.7 (lines 1187–1199), §4.13 H8-TRAVERSE-01/H8-RESTORE-01 (lines 1433–1434), and the incorporated `ShadowWorldPort`/hook contracts in §5.1.

**Claim under review:** after the main `RenderGlobal.setupTerrain` has run, Phase 8 invokes it a second time with the shadow frustum, redirects the traversal's visit/neighbor/visibility operations, draws the resulting shadow list, and restores the main list.

**Evidence:** vanilla 1.12.2 `RenderGlobal.setupTerrain` rebuilds `renderInfos` and enters its traversal only when its cached dirty condition is true. Its dirty calculation uses existing dirtiness, pending chunk updates, and view-entity position/pitch/yaw changes; the supplied `ICamera` is not an invalidation input. See the independently read [RenderGlobal.java, lines 883–895](https://github.com/KealJones/mc-1.12.2-source_files/blob/master/src/minecraft/net/minecraft/client/renderer/RenderGlobal.java#L883-L895), with the traversal body immediately following. Cleanroom MCP independently resolves `RenderGlobal.displayListEntitiesDirty` to `field_147595_R` and its public setter to `func_174979_m()V`. The available `reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch` changes the relevant traversal's Forge behavior but does not replace this rebuild condition. As portable corroboration only, `reference-src/Pintonium-main/modern/src/main/shaders_java/net/irisshaders/iris/shadows/ModernShadowRenderer.java` explicitly requests a terrain update before its second setup; it is not evidence for a 1.12.2 hook.

**Trigger and impact:** in a stationary, fully rebuilt world with no pending chunk updates, the completed main setup leaves the dirty condition false. Passing another frustum and the same captured frame token does not change that condition. The second setup therefore skips the traversal entirely, so the listed H8-TRAVERSE-01 redirects are never reached. Phase 8 reuses the main-camera visible list; its optional allowed-set post-filter cannot add omitted casters. Off-camera chunks that should cast into the visible receiving region disappear from the shadow pass. `FullLoadedView` is not a remedy because it uses this same skipped setup. The fresh whole-owner requirement to specify an executable shadow traversal is consequently not yet met.

**Required correction:** specify an authenticated shadow-only mechanism that forces this second setup to rebuild its traversal regardless of main-camera cache cleanliness, including its exact vanilla operation/hook and restoration semantics. Preserve the main `renderInfos` and legitimate chunk-update scheduling/dirtiness; do not solve the problem by altering vanilla frame indices, repeatedly traversing the world, or substituting a replacement chunk renderer. Incorporate the mechanism into §4.7, the health/accessor ledger and §5.1, with the corresponding future implementation fixture in §8: first perform main setup in a clean stationary world, then prove the shadow setup actually visits an off-main-frustum caster and restores the main list. This is a future test specification, not a test executed by this review.

**Severity:** correction. **§5 impact:** yes, because the executable world-port behavior and hook-health surface are incorporated into §5; the change requires fresh owner review under §G1.3.

## Receiver note N8-1 — Keep any revised hook evidence synchronized

P7 owns the execution bridge and copies the Phase 8 health subreport without reinterpretation (`docs/phase7/v1/PHASE_7_DOC.md` §5.1, shadow execution and `HookApplicationReport` rules). If R8-1 adds an injection/accessor or changes a required anchor count/fingerprint, propagate the actual revised Phase 8 projection through that existing P7→P2 reporting path. Do not infer success from a completed shadow frame. This is a receiver consequence of the owner correction, not a second P8 defect and not a certification of P7 or P2. A new public execution credential or alternate texture-binding API is unnecessary.

## Other architecture results

- **Demand and construction:** §§2.2/4.1 now distinguish `(policy, hookHealth, requested)` and derive demand from the same accepted P3 shadow minima as P5, including color-only demand. P7 §4.1/D-P7-46 and §5.3 explicitly receive the triple and the accepted-estate pre-admission neutralization branches. Available, unavailable and not-requested states are no longer conflated. Registry-independent planning precedes provider/runtime/compile; final registry identity belongs to construction/publication. No remaining cycle or missing receiver dispatch was found on this path.
- **Celestial dataflow:** the pure angular record has no frame/camera authority. P7's pre-camera provider and its associated post-camera invocation use the same retained sun/sky sample. P8's sole compute signature produces the unchanged P6 celestial event from actual main-camera matrices. P6 §4.2/§5 and P7 §4.3/§5 receive that contract. The previous celestial amendment is not re-raised.
- **Binding, release and failures:** P8 branches on all four P5 physical-binding results, preserves Bound-only ownership transfer and closes the independent lease/binding owner after every terminal outcome. The exhaustive terminal P4 release obligation precedes platform restoration; only FixedFunction plus successful cleanup permits continuation. P7's receiving dispatch forbids main bind/clear/draw after Failed or execution-close failure. Replay-delivery failures are explicitly distinguished from allowed per-uniform degradation. No additional missing result branch was found in those changed paths.
- **Shadow semantics:** the document supplies explicit orthographic/perspective matrices, signed-remainder orthographic snapping, day/night boundaries, eye-space celestial vectors, light-parallel synthesized planes, a conservative numeric fallback, terrain SOLID→CUTOUT_MIPPED→CUTOUT, Forge pass 0 before the single split, optional translucent terrain and independent Forge pass 1 after it. P5 remains the sole extent, texture, fixed-unit, compatible-neutral-backing, PCF and mipmap owner. P8 consumes already-neutralized mipmap results without duplicate terminal calls.
- **Lifetime and ownership:** P8 publication closure is distinct from P7's invocation bridge and P6 retirement. Current release/lock ordering agrees with P4's close→bind→acquire→notify→participants rule. Borrowed publication/selection/context/execution values are not closed by P8. Texture invalidation does not discharge closure duties. ID/color/instance restoration remains before shadow release.
- **Scope, authority and milestones:** all thirteen required document sections are substantive. No OQ is assigned to P8. The v0.2 subsystem, later ID/vertex/texture wiring, approved v0.5 prepared native submissions and post-v0.5 shadowcomp boundary are designed without assigning P8 a second binder or chunk renderer. The document records the Pintonium/RESEARCH terrain-order and perspective/snapping differences rather than silently inheriting them. GPL/LGPL and behavioral-observation boundaries are preserved. Current amendments supersede the explicitly historical versions and PASS quotations; none was treated as an active contradictory contract.

## Evidence limitations and readiness boundary

The historical exact paths `reference-src/pintonium-9c2fcc1/...` and `reference-src/cleanroom-0.6.6-alpha/...` are not present in this workspace. The available trees are `reference-src/Pintonium-main/...` and `reference-src/Cleanroom-0.6.12-alpha/...`; they were used only as explicitly identified corroborating evidence, not claimed to be byte-identical replacements or authority to upgrade the pinned loader. The vanilla cache-gate read was a narrow additional investigation to answer an implementation-reachability gap; no transformation source or code was copied. No chatlogs, root transcripts, blocked Oculus paths or transformation libraries were read.

No files were edited. No validation commands, builds, tests, formatters, linters, gameplay or GL execution were run. Source/control-flow evidence supports the correction; no runtime reproduction or conformance result is claimed. Unrun implementation tests and absence of a working 1.12.2 Pintonium shadow renderer are limitations, not additional architecture findings.

This verdict does not close sibling reviews, current receiver verification, implementation gates, IR-01 or the final §G5.3 integration review. Phase 8 requires the one owner correction and fresh review of its changed §5 before it can receive a fresh PASS.

## Resolutions

### R8-1 — Forced shadow traversal (2026-09-08)

Resolved architecturally by P8 D-P8-29 in §§0.14/4.2/4.4/4.7/4.13/5.1/6/8.2/9/11/12.
The port now invokes the actual mapped public dirty setter `func_174979_m()V` immediately
before one authenticated second setup. Scoped debug-freeze/capture suppression makes the
rebuild gate reachable even with frozen main frustum state; seed/path-occlusion overrides
make the traversal queue usable independently of main visibility. A single rebuild-entry
witness distinguishes a real traversal from a returned cache-only setup; no retries or frame
token changes are allowed.

The original main list reference, five view-cache fields and debug state are restored;
pending updates are merged losslessly and dirtiness preserves genuine invalidation, including
throws before vanilla's scheduling-tail merge. Failure restores before main admission and
uses existing release/abort/off containment rather than continuing with a shadow or stale list.
The future stationary-world fixture explicitly exercises an off-main-frustum caster in both
strategies, debug freeze, opaque camera seed and exceptions. No fixture was run.

### N8-1 — Revised receiver projection

P8 §5.1 grants P7 the unchanged execution bridge plus narrower setup-only authentication,
H8-TRAVERSE-01 expected vector `(3,1,1,1,1)`, expanded H8-RESTORE-01 field/accessor coverage,
and H8-REBUILD-01 setter resolution/branch anchor, each with revised fingerprints. P7 must
copy actual nested health through its existing P2 reporting path without inferring success
from a completed frame. Receiver integration/fresh review remains required.

D-P8-30 also receives exact-current schema22 admission; earlier numeric receipts remain
historical and unrelated P3 asset/native/option contracts are preserved. Original review body,
counts and verdict above are unchanged. §5 changed and requires fresh review. No implementation,
validation command, build, test, formatter, linter, gameplay or GL execution was performed;
this resolution records source/mapping-grounded architecture, not implementation clearance.