# Phase 7 — Fresh whole-document architecture review R40

## Verdict

**PASS-WITH-CORRECTIONS — 0 structural blockers, 1 required owner correction, 2 cross-owner receiving notes, 1 evidence limitation. `section5Impact: true`.**

This reviews the complete frozen `docs/phase7/v1/PHASE_7_DOC.md`, not merely the latest amendments. The reviewed owner baseline is the Main-confirmed SHA-256 `cc3d506f47ee9c02d67e12ee7d17ff948887909982943396ba46abc991396343`. Its architecture is substantially implementable, but the newly required depth-write override lacks the concrete hook route required by its own complete-catalog contract. This is not an implementation, runtime, sibling-phase, milestone, BUILD.md aggregate, or final IR-01 certification.

## Scope and evidence

Read the entire P7 owner, recovering every owner range through the end of §13; repository governance; header-selected RC3 Part I §§G0–G12 and the P7 assignment at `docs/design/v2.0-RC3/DESIGN.md:1805–1953`; the governing research lifecycle/program/frame sections, §5.3, §7.1, Appendix A.1, all Appendix E, the engine-flag Appendix F.1 and exact OQ-3/OQ-4 rows. Read the required Pintonium design sections, dimension-cache context, and standing bug/divergence exclusions; the behavioral digest’s lifecycle/frame material and replacement-class inventory. Dependency review covered current §5 publications of P1–P6 and incorporated grants from P8–P13, with the relevant P1 state-lease, P4 activation, P5 frame-normalization, P6 retirement and P11 reset/disposal rules read directly. Dependency reading establishes what P7 receives; it does not certify those owners.

The whole-owner assessment covered the thirteen-section structure, the eleven engine flags, all eighteen Appendix-E class entries and their explicit deferred ownership, the replacement-list cross-check, lifecycle and frame seams, nested gbuffers/fullscreen/shadow execution, hand and depth-copy sequencing, reload/publication/off transactions, diagnostics, native geometry and display-list admission, capture clocks/evidence, failure isolation, milestone staging, decisions and worked example. Historical review and schema statements were treated as historical where explicitly superseded.

Permitted primary evidence additionally included the shipped shader-property descriptions at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:53–59`, the current local Cleanroom renderer patches, the two permitted current local Pintonium mixin files, the 1.12.2 mapping lookup for `GlStateManager.depthMask(boolean)` and the official Forge dispatcher patches linked below. No transformation implementation was copied or followed, no Oculus material or chatlogs were read, and no files were edited. No builds, tests, formatters, linters or runtime validation were run.

## Required owner correction

### C1 — Catalog the depth-mask override enforcement route

**Evidence:** P7 `:779–780` correctly maps `rain.depth` and `beacon.beam.depth` to depth writes. P7 `:1496–1505` now additionally requires explicit TRUE/FALSE to survive vanilla mask changes inside the enclosing draw method, says an entry-only setter is insufficient, and requires scoped vanilla attempts to use the resolved value. However, the complete hook catalog specifies only HEAD/RETURN scope hooks for H-BEAM-01/02 (`:1604–1605`) and H-WEATHER-01 (`:1637`). The state-hook catalog at `:1653–1688` supplies alpha/blend interception, fog and restoration observers, and frustum redirection, but no depth-mask interception or exact inner draw wrapper. P1's `StateService.depthMask` at `docs/phase1/v14/PHASE_1_DOC.md:3314` is an immediate setter; the duration lease at `:3395–3445` covers alpha/blend, not depth writes. The fresh 1.12.2 mapping lookup confirms the missing setter target is `GlStateManager.func_179132_a(Z)V`.

**Impact:** Implementing the published catalog cannot meet the new explicit flag contract when the enclosed vanilla renderer changes the mask before drawing. The scope-entry value can be overwritten, while hook health still reports the declared catalog healthy. This is an incomplete realization of the corrected depth-write semantics, not a request to revert to depth-test suppression.

**Required change:** Add a concrete mapped enforcement hook or exact draw-site interception strategy to §4.10 and its audit inventory. Specify how only a live rain/beam scope changes the attempted mask, how DEFAULT delegates, how nested scope restoration and P1-originated restoration avoid self-interception, and how missing enforcement/restoration anchors affect admission. Keep policy in the existing owner/glue split and preserve the explicit prohibition on depth-test interception. Synchronize the §5 hook-report publication/receiving summary and relevant acceptance traces; an unlisted implementation hook is not an acceptable resolution of a document that promises a complete catalog.

**Classification:** Required local architecture correction, P2 priority; not a structural redesign or a new P1 depth-lock API request.

## Cross-owner receiving notes — not additional owner findings

### N1 — Receive P2's persisted timing-evidence correction

P7's timing grant at `:2986–3035` authenticates live P6 reports and explicitly retains preparation/warm-up/sample ledger summaries. Its private control exposes exactly `checkpoint`, `validate` and `close`; the reviewed owner contains no persisted export of the complete preparation/warm-up evidence. The P2 reviewer reports that P2 §4.2.6 requires such retained source evidence while the current `/4` manifest transports post-warm-up frame records. P2 owns the wire/retention correction. P7 must receive the eventual grant if its private ledger is the producer; do not infer that private validation or a detached sample record already supplies a serialized all-phase audit. This is a receiver note, not a fresh certification or duplicate finding against P2.

### N2 — Receive P9's ordinary-dispatch and fast-TESR corrections without losing P7 program routing

P7 H-ENTITY-03/H9-BLOCK-ENTITY-ID-01 at `:1600` names `TileEntityRendererDispatcher.func_147549_a(TileEntity,D,D,D,F)V` and uses that scope both to open `gbuffers_block` and to obtain P9 ID admission. The official [Forge RenderGlobal patch](https://raw.githubusercontent.com/MinecraftForge/MinecraftForge/1.12.x/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch) instead shows ordinary local/global world tile entities dispatched through `func_180546_a(TE,F,I)`; the official [dispatcher patch](https://raw.githubusercontent.com/MinecraftForge/MinecraftForge/1.12.x/patches/minecraft/net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.java.patch) shows that route calling `func_192854_a(TE,DDD,F,I,F)` directly, then the ordinary or fast TESR branch. It also shows native fast-TESR submission deferred to `drawBatch`. The current local Cleanroom RenderGlobal patch corroborates the world call chain and batch boundary.

P9's independent owner review owns the callable-overload and actual-submission identity corrections. P7 needs a coordinated receiver update: its program scope must surround the actual ordinary TESR draw, ID admission must remain balanced and authenticated on both main and shadow paths, and a deferred batch cannot be treated as if it drew inside an already-closed per-TE scope. P7's existing H-ENTITY-03 is not evidence that ordinary world tile entities traverse that convenience overload. This receiving work is recorded separately from C1 rather than duplicating the sibling's owner findings or certifying its replacement protocol.

## Current integrated corrections assessed

- **P1/P4/P6 alpha/blend path:** The backend-implementable lease, concrete three-alpha/six-blend setter family, nine HEAD plus six normal RETURN expectations, construction-installed effective-blend receiver, and old-endpoint lifetime are coherent on the reviewed paper path. P4 closes the prior lease, binds, acquires the new lease, publishes effective state and runs sampler/built-in/custom participants. The formerly missing route and wrong order are not re-raised.
- **P5 snapshot failure:** `PassSnapshotResult.Failed(...,true)` is consumed explicitly through the shared P7 terminal disposition across ordinary acquisition, nested pop, fullscreen and outer cleanup. No parent resume, subsequent draw or second P5 complete/discard/commit/abort is authorized; independent bindings, local leases and P4 release still close. This agrees with P5 §2.2 and §4.4, including cleanup failure.
- **P8 demand and initialization:** P7 receives requested shadow demand from the accepted P3 depth/color minima OR, retains the policy/health/requested plan identity, plans before dependent providers, and gates disabled/uninstalled shadow on actual-estate neutralization before admission. A real allocated but undrawn shadow attachment is not accepted as neutral merely because planning returned disabled.
- **P2 world/tier/image domains:** The current world/save descriptor, pre-load identity checks, exact retained run identities, one GL-profile domain and closed image allocation grammar are received rather than locally reinterpreted. The private timing-validation versus persisted evidence distinction in N1 remains important.
- **Reload and lifetime:** Exact current containing/nested P3 schema equality, same-load assets, resource-only NONE retention, three-position P4 production composition, actual publication generation adoption, compensated-Off failure, P10 worker/product drains and P6/P11 final-use retirement are explicitly represented. Retained owners are not released merely because a timeout or failed callback resembles quiescence. No fourth barrier participant, synthetic generation, fabricated readiness or old-pipeline success fallback was found in these paths.
- **Historical evidence:** Superseded schema versions, old lock design, earlier depth-test wording and prior review outcomes remain provenance, not active competing requirements. No historical PASS was used to approve the current coordinated contracts.

## Section 5 impact and completion boundary

**`section5Impact: true`.** C1 changes the actual complete hook family exported by the canonical `HookApplicationReport` and therefore requires its §5 receiving summary/audit expectations to be synchronized, not merely a hidden implementation detail. N1/N2 may additionally change received contracts once their owners settle them; this review does not pre-authorize those designs. After correction, perform a fresh whole-owner P7 review of the new frozen bytes and the affected receiver interfaces. The current result grants no runtime or final integration clearance.

## Evidence limitation

The historical pinned directories `reference-src/cleanroom-0.6.6-alpha/` and `reference-src/pintonium-9c2fcc1/` are not present in the supplied checkout. A read-only attempt to recover Pintonium commit `9c2fcc1` from the available `reference-src/Pintonium-main` repository failed with `fatal: invalid object name '9c2fcc1'`. The permitted current files and official Forge patches were used only as separately identified evidence, not relabeled as the missing historical snapshots. In particular, current Pintonium camera capture placement is not proof that a historical observation was false. The document's older all-method MCP audit was read as historical evidence; this session independently queried the load-bearing depth-mask mapping, not every previously catalogued method. Actual transformed anchor counts, native cache/state restoration, rendering, capture scheduling and visual parity remain unverified implementation obligations.

## Resolutions

### C1 — Concrete depth-write enforcement

D-P7-49 adds mapped H-DEPTH-MASK-01 argument enforcement, authenticated nested depth
scopes and private restoration bypass. Rain/beam scopes now use local AROUND/finally;
missing enforcement/exit anchors prevent admission and failed restoration contains off.
Depth-test semantics remain untouched. This is a changed §5 contract requiring fresh review.

### Cross-owner receiving corrections

D-P7-50 receives P2's durable actual timing transport; D-P7-51 receives P11 controller
construction/lifecycle and typed diagnostics; D-P7-52 receives P8's forced traversal health;
D-P7-53/54 receive complete P10 sky/native and P9 actual TESR/deferred-ID protocols.
D-P7-55/56 receive explicit sampler normalization and still-gated async lifetime conditions.
Schema22 receipt is D-P7-48. These architectural receipts do not certify sibling owners,
hook application, native behavior or final IR-01. Original report text and verdict remain intact.