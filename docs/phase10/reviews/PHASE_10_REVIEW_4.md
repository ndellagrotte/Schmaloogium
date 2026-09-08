# Phase 10 Architecture Review — R4 / Frozen Attempt 7

**Owner:** `docs/phase10/v1/PHASE_10_DOC.md`  
**Frozen SHA256:** `afc250ffd3659bc370e4ec2373855080b8c6f2d5336979997ad15efeac0c11e0`  
**Identity basis:** supplied frozen inventory, also recorded in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_7.json`; no checksum command was run.  
**Substantive corrections:** 1. **Notes:** 1. **§5 impact:** YES.

## Scope and selected authority

This was an independent review of the complete current owner, §§0–12, not merely D-P10-29 or the most recent edits. I read its header-selected `docs/design/v3/DESIGN.md` Part I §§G0–G12, the Phase 10 assignment at lines 2162–2277, and its document gate/template. I used `docs/MOVES.md` to distinguish current versioned paths from historical coordinates; v3 was selected because Phase 10 selects it, not because it is the newest design directory.

The governing research checks covered `docs/research/v1/RESEARCH.md` §§0–1, classic attribute/fixed-function semantics, §4.6, §7.4, Appendix C, Appendix E rows 3–9 and their coverage rules, milestone/licensing constraints, and the assigned OQ-5/OQ-14 questions. Additional bounded reads established the lighting precedence, geometry authorization and real vanilla/Forge producer paths. Dependency review covered the consumed Phase 4/7/9 §5 surfaces and their incorporated load-bearing definitions, plus Phase 1's exact vertex-input, restoration and early compatibility grants. Actual receiving branches were inspected rather than accepting receipt labels as proof.

No previous review verdict or claimed fix receipt was used as substantive evidence. No files were edited, no report was written, and no formatter, linter, build, test, runtime experiment or validation command was run.

## Independent whole-owner checks

1. **Classic layout and mathematical contract.** The 56-byte layout, signed-short floating identity delivery at location 10, float midpoint at 11 and normalized short tangent at 12 match the applicable Appendix C contract. The owner preserves all three identity components and separates Phase 9's block stamp from entity/TE uniforms (`PHASE_10_DOC.md:355–405,547–619,1220–1244`). Its ordinary and mirrored-square calculations use the required N×T handedness. Current Pintonium `ChunkVertexExtendedData.java:72–119` instead computes T×N and packs bytes; current `QuadUtil.java:78–117` corroborates the diagonal normal. The owner correctly rejects those numeric differences rather than treating PD's equivalence claim as authority. Degenerate handling and truncation remain disclosed local decisions, not externally verified parity.
2. **Scalar ingress and generated normals.** The inspected vanilla BufferBuilder setters calculate their addresses using the current format element/index and advance that cursor. The owner now selects the authenticated source semantic before storage, preserves float-to-integer color delegation, overrides advancement only within an authenticated setter, initializes each record before its first write, and invalidates exceptional/partial products (`PHASE_10_DOC.md:423–484,1500–1540`). TexturedQuad really emits `pos→tex→normal→endVertex` with OLDMODEL, so endVertex-only repair would be insufficient; the current design does not use that shortcut. Supplied normals remain on non-QUADS, while the already-established generated quad-normal producer remains part of final conventional participation. The remaining defect is the distinct bulk/late-lightmap path described below.
3. **Conventional input authority and native receiving behavior.** The complete plan carries immutable `conventionalInputs`, ordered pointer descriptors and geometry requirement; source identity accompanies saved state, client/VBO products and original/derived list products (`PHASE_10_DOC.md:251–287,654–746`). Phase 1's actual `LIVE_DRAW`, `LIST_CAPTURE` and `LIST_REPLAY_GUARD` laws receive that distinction at `docs/phase1/v14/PHASE_1_DOC.md:4262–4285,4337–4412`. They disable absent COLOR/UV1 arrays without inventing values, isolate unrelated arrays for capture and restore actual predecessor state. Phase 7's receiving branch at `docs/phase7/v1/PHASE_7_DOC.md:2453–2462` retains authenticated participation and rejects mismatches rather than deriving participation from physical 56-byte storage.
4. **Cached models and sky/star products.** The inspected vanilla ModelRenderer has three render callList branches, one renderWithRotation branch, and a postRender-triggered compile route without playback; its compilation traverses ModelBox/TexturedQuad and bakes the first scale. The owner's staging, every-call guard, first-scale retention, incarnation/deletion handling and pointer-free replay restoration cover those distinctions (`PHASE_10_DOC.md:942–1059`). OLDMODEL capture explicitly excludes COLOR/UV1 arrays and constants, preserving then-current fleece color/lightmap on later playback. The inspected RenderGlobal source corroborates three canonical producers, four list sites, three VBO sites, the final lower-list sentinel case and VBO seal-before-reset acquisition; the owner treats each separately (`PHASE_10_DOC.md:1061–1190`). Phase 7 actually receives the model/sky health and drain obligations at `PHASE_7_DOC.md:1791–1802,2389–2414,3776–3782`, not just in a historical acknowledgment.
5. **Geometry, repetition and lifecycle.** Conditional conversion is supported by the explicit maintainer decision, not inferred from instancing. The owner's whole-record `(0,1,3),(1,2,3)` conversion discloses provoking-vertex and primitive-ID changes, validates full ranges before submission and retains paired products through restoration. Phase 4 publishes geometry only after actual linked agreement (`docs/phase4/v1/PHASE_4_DOC.md:1530–1555,1592–1610,2141`); Phase 7 consumes the actually activated effective provider and clears fixed-function authority (`PHASE_7_DOC.md:2438–2451`). Its prepared-submission receiver repeats native operations, not traversal, with saved-parent instance restoration (`PHASE_7_DOC.md:1295–1348`). Phase 7's actual transaction branches require real drain, prepare before acceptance, matched P9 lookup/map activation and invalidation before admission, and off recovery without freeing live borrows (`PHASE_7_DOC.md:2291–2347,3770–3836`). P9's `Present`, `Absent` and `Unrepresentable` payloads, generation and matched ordinal lifetime are concretely consumed rather than re-resolved (`docs/phase9/v1/PHASE_9_DOC.md:287–312,428–445,660–682`). Deferred FastTESR range ownership and the actual sort permutation are also received by P10 and P7; no stored population-time ID token authorizes later drawing.
6. **Remaining owner requirements.** The conformance map, failure ladder, thread ownership, future test plan, milestone staging, both complete spike specifications, growth design, decision log and implementation checklist are present and substantive. Current Pintonium corroborates the actual `celeritas` mod ID and renderer replacement; P1's real early/runtime evaluation branches implement the session latch. Current LightUtil retains both its pair map and static default mapping, so the owner's OQ-14 fallback correctly requires more than clearing a map. Neither spike is falsely declared resolved. Modern producers remain unwired and unadvertised. Exact-current schema23 is consumed through P7 without introducing a P10 parser.

## Substantive correction

### C1 — Admit authenticated partial BakedQuad ingress before its real lightmap producer runs

**Severity:** correction / high (P1).  
**Primary owner location:** `docs/phase10/v1/PHASE_10_DOC.md:507–513`.  
**Related contract:** `PHASE_10_DOC.md:698–707,1637–1651,1932–1933`.

The new bulk rule requires every appended record to agree with the builder's conventional participation and forbids source-absent UV1 becoming a supplied stream. This conflates the BakedQuad's partial storage descriptor with the completed block-rendering producer. Ordinary terrain uses a BLOCK builder, whose final UV1 must be supplied, but ordinary Forge baked quads use ITEM, which has no UV1. The vanilla block renderer appends that ITEM quad first and only then writes the four actual lightmap values with `putBrightness4`. Under the current rule, the authenticated ITEM append is rejected before the real lightmap producer can execute. Choosing an ITEM-participation product instead would disable UV1 and lose the subsequently supplied per-vertex block lighting. This is a required vanilla/Forge terrain path, not an unknown custom format.

**Independent producer-to-consumer evidence:**

- `reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/block/model/FaceBakery.java.patch:47–52` constructs the ordinary baked quad with `DefaultVertexFormats.ITEM`; `block/model/BakedQuad.java.patch:18–28` also gives the legacy constructor ITEM and retains the supplied format.
- [Vanilla DefaultVertexFormats](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/vertex/DefaultVertexFormats.java) defines BLOCK as POSITION/COLOR/UV0/UV1 and ITEM as POSITION/COLOR/UV0/NORMAL/padding. Equal 28-byte size does not make their participation equivalent.
- `reference-src/Cleanroom-0.6.12-alpha/src/main/java/net/minecraftforge/client/model/pipeline/ForgeBlockModelRenderer.java:49–86` actually dispatches both flat and smooth rendering to the vanilla superclass when `forgeLightPipelineEnabled` is false.
- `reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/BlockModelRenderer.java.patch:12–14` retains the ordered `addVertexData(bakedquad.getVertexData())` followed by `putBrightness4(...)`. The [vanilla BlockModelRenderer source](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/BlockModelRenderer.java) independently shows that ordering in both flat and smooth paths. MCP stable_39 resolves the latter writer to `BufferBuilder.func_178962_a(IIII)V`, explicitly setting brightness for the previously stored quad.
- The owner itself requires both Forge lighting modes and preserved lightmap samples (`PHASE_10_DOC.md:1317–1338,2104–2136`); rejection/separate-product language therefore does not supply an equivalent accepted path.

**Observable breakage:** with shader vertex mode active and the ordinary non-Forge-lighting block route selected, valid terrain quads hit the participation rejection before their lightmap write, causing rejected chunk products/shader containment instead of supported terrain. Treating the partial source mask as the final draw mask instead would produce incorrect block lighting.

**Minimal owner fix:** distinguish the authenticated partial ingress descriptor from the authenticated completed producer's participation. Keep the BLOCK builder's immutable final mask, but explicitly admit ITEM BakedQuad conversion within the existing recognized block-model producer where UV1 is supplied by the actual subsequent brightness writer. Track required semantic completion and reject an incomplete product at the appropriate model/seal boundary before upload/draw. Do not infer UV1 from filler, union arbitrary mixed sources, mutate cached quad arrays or change OLDMODEL's genuinely inherited COLOR/UV1 behavior. Update the incorporated §5 semantics and add this ITEM→BLOCK→putBrightness4 case to the planned ingress/conventional-participation behavioral cases. P1/P7 should receive the clarified final-product authority without a new native allocator or public renderer API.

**§5 impact:** YES. The bulk/source-participation law is incorporated by §5.1 and received by P1/P7. This is a fixable owner contract defect, not a structural rebuild.

## Note

### N1 — Historical source pins and licensing confidence remain qualified

**Severity:** note.  
**Owner evidence locations:** `docs/phase10/v1/PHASE_10_DOC.md:42–79,112–117,610–619,942–958`.

The exact historical `reference-src/cleanroom-0.6.6-alpha` and `reference-src/pintonium-9c2fcc1` trees are absent from the inspected inventory. The available `Cleanroom-0.6.12-alpha` and `Pintonium-main` trees provide separately identified current corroboration; they do not prove the historical line pins or the configured transformed runtime. The vanilla mirror likewise supplies a concrete source-level call path, not Cleanroom runtime certification or permission to copy Mojang-derived implementation. Current Pintonium contains GPL and LGPL license texts, but that does not establish a blanket per-file incorporation license. No source was copied, and forbidden transformer/Oculus implementation boundaries, OptiFine decompiled implementation and transcripts were not read. Preserve these limits in any retained evidence and later incorporation review; missing historical pins are not themselves a new architecture correction or a request to run runtime tests at this gate.

## Final assessment

The complete owner satisfies the structural document gate and now has concrete receiving contracts for conventional input isolation, cached native products, geometry compatibility and lifecycle drain. One current contract defect remains: the new bulk participation rule rejects a legitimate partial ITEM BakedQuad before the existing terrain lightmap producer completes it. This review certifies neither sibling owners, implementation readiness, transformed hook coverage, native GL behavior, source-license clearance nor conformance parity.

**Corrections: 1. Notes: 1. §5 impact: YES.**

PASS-WITH-CORRECTIONS

## Resolutions — 2026-09-08 (architecture only, unverified)

- C1: D-P10-30 changes §4.2's actual ingress to distinguish authenticated partial
  ITEM BakedQuad storage from the completed BLOCK producer's immutable participation.
  It admits the real append→func_178962_a(IIII)V sequence, retaining a same-scope,
  same-builder/epoch/range four-vertex UV1 obligation until all real stores succeed.
  Missing/throwing completion invalidates before safe model/seal/state/upload/capture/draw;
  no filler, arbitrary union, cached-array mutation or OLDMODEL inheritance change.
- §4.11 publishes mandatory H10-BRIGHTNESS-4 CORE expected1 in full v0.3/v0.5,
  amended lifecycle/exception coverage and P7/P2 fingerprint receipt requirements.
  §5, behavioral cases and checklist incorporate the changes. The P1 complete
  capturePlan recorder factory is adopted with first-replay authority checks.
- Current Cleanroom patch/Forge dispatch and separately identified vanilla late-writer
  source corroborate the producer law only. N1's confidence/license limits and original
  report/verdict are preserved. No validation command, runtime or conformance proof ran.
