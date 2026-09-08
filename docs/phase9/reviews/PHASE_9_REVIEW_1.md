# Phase 9 — Fresh whole-document architecture review 1

**Verdict: PASS-WITH-CORRECTIONS**

- **Owner:** `docs/phase9/v1/PHASE_9_DOC.md`, all §§0–12.
- **Frozen SHA-256:** `43d6eba37f3d1a57a39e5d0d8d4e38353727bd3a3c5808818dbfade1a5bce422`. The parent confirmed that the assigned freeze remains unchanged; this review did not run a checksum/validation command.
- **Counts:** 0 structural blockers, **3 required corrections**, 2 notes.
- **section5Impact: true.** The corrections affect the adopted Phase 3 provenance grant and the Phase 7 ID-hook/submission handoff. Their §5 changes require fresh owner/receiver verification under the governing §G1.3/§G5.3 rules.
- **Readiness:** not ready for a fresh literal PASS. These are bounded architectural corrections, not grounds to rebuild the entire document.

## Authority and coverage

The governing authority is `docs/design/v2.0-RC3/DESIGN.md`, Part I §§G0–G12 and its Phase 9 assignment, especially the scope and doc gate at lines 2035–2125. I read the complete owner document, the governing research §§0–1, §3.7, relevant §3.6.8/§4.7, Appendices C.1–C.2, D.1/D.4 and E rows 13–14, the hand-light policy material, PD §8, and the relevant shipped pack-author documentation. I read Phases 3, 6 and 7 §5 in full and the incorporated mapping, uniform event/retirement, frame, scope, hook and publication rules needed for this owner. The narrow Phase 10 read traced the receiving alias-stack and lifecycle contracts, not that sibling's whole architecture.

All thirteen required owner sections are present and substantive. The conformance map addresses the assigned mapping forms, mod-source precedence, fallback, layers, held values, per-draw IDs/color, alias service and reload safety. The module split keeps Minecraft/Forge/GL policy outside the pure engine. The current schema21 receipt correctly distinguishes containing/nested schema equality from historical schema20/19 receipts and does not authorize binary-asset acquisition. Historical PASS evidence is explicitly retained as provenance, not promoted to current verification.

The producer/receiver trace confirms that P7 §5.1 consumes the accepted-frame slot's Completed/HeldFeatureDisabled/Rejected/Failed outcomes and reset results; its shadow admission matches P9 §4.12's Entered/Rejected model. P6 §4.2 supplies the declared held tuple, including `(max(main,off), off)`, and §4.14 includes final P9 restoration before retirement. P7 §§5.1/5.3 and P10 §§4.4/5 preserve matched lookup/ordinal lifetimes and require actual worker drain and geometry invalidation before admission. P10 explicitly consumes the exact returned stamp words rather than re-resolving aliases. P7 receives all four resolved layer decisions and preserves vanilla on absence. P7, not P9, remains the sole color writer. These aligned boundaries do not cure the three reachability defects below.

## Required corrections

### C1 — Supply a reachable producer for modern ambiguous block provenance

**Owner location:** P9 §§4.6, 5.2 and 5.4; notably lines 546–553 and R9-1's adopted disposition. **Severity:** correction.

P9 makes the modern `minecraft:grass` and `minecraft:redstone_lamp` behavior conditional on `MappingEra.MODERN`, but the adopted producer does not emit that value for blocks. `docs/phase3/v1/PHASE_3_DOC.md` §4.9, lines 2967–2980, explicitly fixes block/item/layer forced lists to empty, ordinary rules to CLASSIC, and only forced ENTITY rules to MODERN. Thus neither the selected pack nor the bounded mod parser can drive P9's modern block branch. A hand-authored MODERN resolver fixture would prove only an unreachable synthetic case. This is an active grant mismatch, not a request to restore the rejected shader-ID heuristic.

**Required change:** reopen the missing block-era part of R9-1 in P9 §5, specify and obtain a concrete Phase-3-owned provenance/selection contract that can produce the required block rules without guessing, and adopt it consistently in resolution, identity and the v0.3 evidence plan. Alternatively, changing the mandatory modern alias obligation requires an explicit governing authority amendment; neutral/classic fallback cannot be called its completion. **Receiver coordination:** Phase 3 owns the producer amendment; this review does not certify Phase 3.

### C2 — Move block-entity scope coverage onto the actual world-render call path

**Owner location:** P9 §4.12 H9-BLOCK-ENTITY-ID-01, line 694; P9 §§5.1/5.5 incorporate that hook. **Severity:** correction.

The sole specified TE hook targets `func_147549_a(TileEntity,DDD,F)`. The ordinary world path does not call it. The official [Forge 1.12.x RenderGlobal patch](https://raw.githubusercontent.com/MinecraftForge/MinecraftForge/1.12.x/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch), block-entity-loop hunks, calls `func_180546_a(TileEntity,F,I)`. The official [TileEntityRendererDispatcher patch](https://raw.githubusercontent.com/MinecraftForge/MinecraftForge/1.12.x/patches/minecraft/net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.java.patch), hunk beginning at original line 117, shows that method directly calling `func_192854_a(TileEntity,DDD,F,I,F)`; the later hunk directly invokes the TESR. MCP independently confirms these distinct descriptors. P7 §4.10.4 H-ENTITY-03 repeats the same insufficient hook, so it provides no alternate admission on this path. Ordinary world TESRs therefore get neither the promised scoped block ID nor that enclosing block-program scope.

**Required change:** bracket the actual renderer-dispatch path with the authenticated P7 admission and P9 ID scope, covering the ordinary world and relevant direct-call overloads without duplicate unbalanced scopes. Update the paired P7 catalog, health cardinality, main/shadow ordering and P9 §5 handoff together. The architecture proof should trace a normal world TE call into the chosen hook; merely resolving the existing candidate symbol is insufficient. Preserve Appendix E as historical candidate evidence and record the refined target, rather than silently rewriting research.

### C3 — Keep each fast TESR's ID valid until its actual native submission

**Owner location:** P9 §4.12's per-TE enter/restore model, lines 694–704, and §§5.1/5.5. **Severity:** correction; distinct from C2.

Correcting the overload alone does not cover Forge's fast-renderer branch. The same dispatcher patch routes `drawingBatch && hasFastRenderer()` to `renderTileEntityFast(..., batchBuffer)`, which appends geometry; `RenderGlobal` calls `drawBatch(pass)` only after the per-TE loops. `drawBatch` then issues the actual tessellator draw. Consequently, ordinary per-TE RETURN restoration has already removed every tile's ID at native submission. Two differently mapped TEs in one batch cannot be represented by one `blockEntityId` uniform. No fast-TESR/batch submission policy is specified in P9 or the receiving P7 hook contract. P10 §4.7's rejection of substituting a constant attribute for mixed block identities does not solve this uniform boundary.

**Required change:** design a bounded shader-active submission route that draws each required ID-homogeneous portion while its authenticated block-ID scope remains active, with exact state/ordering/failure restoration and unchanged shaders-off behavior. The existing immediate `FastTESR.render` route is concrete platform evidence of a per-TE submission mechanism, not permission to invent a general renderer rewrite. Reconcile the route with P7 main/shadow admission and actual draw handling; do not advertise neutral batch IDs as complete per-TE delivery. Add a future implementation case with two differently mapped fast TESRs proving IDs at native submission, not merely during buffer population.

## Notes and limitations

### N1 — Preserve verification and integration boundaries

P9 §§0, 5.4, 10 and 11 already distinguish current adopted-but-unverified contracts from historical PASS reviews. This is correct. The coordinated failure-to-off policy, textures-before-IDs ordering, matched worker-borrow lifetime, schema21 adoption and P7-owned v0.1 color delivery are assessed as current text; superseded historical assertions were not re-raised. This review certifies no sibling, runtime behavior, milestone implementation, BUILD.md execution permission or final IR-01 integration clearance.

### N2 — Primary-reference availability and legal scope

The exact historical `reference-src/pintonium-9c2fcc1/` and `reference-src/cleanroom-0.6.6-alpha/` paths are absent. A targeted path lookup located `reference-src/Pintonium-main/` and `reference-src/Cleanroom-0.6.12-alpha/`. I read the complete permitted IdMap/VintageBlockMaterialMapping files from the former, and relevant public registry APIs, FastTESR and dispatcher/RenderGlobal patches from the latter. These are current corroboration, not claimed byte-identical recovery of the historical snapshots. The load-bearing TE call-path findings are additionally grounded in the official Forge 1.12.x primary patches and MCP signatures. No forbidden transformer source, Oculus pipeline/transform material, libs, glsl-relocated, chatlog or root transcript was read; no transformation code was copied. No files were edited, and no builds, tests, formatters, linters or validation commands were run. Unrun implementation tests are not counted as architecture defects.

## Final assessment

The owner has a coherent resolution and lifecycle architecture, but a mandatory modern block branch has no producer and the TE value-delivery design misses both the ordinary call route and Forge's delayed batch submission. Resolve C1–C3 and the corresponding §5 owner/receiver changes before a fresh PASS; retain the existing independent verification and final integration gates.

## Resolutions — 2026-09-08

Architecture-only correction; original review body and verdict above are preserved.
No validation command, runtime test, fresh PASS or implementation clearance is claimed.

- **C1:** P9 §§4.6–4.7/5.2 receive P3 D-P3-72 schema22 BLOCK/ENTITY dual-era producer.
  Only PRESENT_EMPTY plus nonempty forced11300 rules selects MODERN; ordinary CLASSIC
  results/state remain unchanged. Source and selected-list identity are explicit; §8
  requires real pack-load and bounded-mod parser fixtures reaching modern grass/lamp.
  Same-load assets/options/native boundaries and projectionVersion=1/nine trees remain.
- **C2:** §4.12 targets actual lower `func_192854_a(TileEntity,DDDFIF)V`, reached from
  world `func_180546_a(TileEntity,FI)V`, damaged/direct/shadow paths. One P7-outer/P9-inner
  finally pair per invocation covers nested/throw restoration; outer convenience overloads
  do not duplicate scopes. §5.6 specifies receiving P7 retarget/cardinality/health changes.
- **C3:** §4.12.1 preserves Forge enqueue and original drawBatch position, observes its
  actual full-batch sort permutation, and partitions into contiguous state-owner ranges.
  Fresh authenticated ID scopes enclose each actual native range and all adjacent copies.
  No blanket hasFastRenderer=false, inherited no-op, dropped fast geometry or CPU-only
  ID claim. §5.6 identifies exact P7/P10 receiver amendments; §8 covers differently mapped
  fast renderers, global sorting, geometry preservation, nesting, native IDs and failures.
  Splitting's primitive-ID restart is disclosed rather than hidden.

Decisions D-P9-19…21, active handoffs, failure policy, milestones and implementation
checklist are aligned. Receiving-owner integration and fresh owner/receiver reviews are
still required; this resolution entry does not certify a sibling or final integration.