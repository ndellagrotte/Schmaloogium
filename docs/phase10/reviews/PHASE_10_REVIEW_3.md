# Phase 10 — Whole-owner Architecture Review R3

**Review:** attempt 6, Phase 10, round 3, 2026-09-08  
**Frozen owner:** `docs/phase10/v1/PHASE_10_DOC.md`  
**Frozen SHA256:** `978d095e1221a6f8671a206633da2cd388841537cabba1e4683cc4e32e3ce1c2`  
**Inventory:** `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_6.json`, Phase 10 entry. The hash is the supplied frozen identity, corroborated by the inventory; no checksum/validation command was run.  
**Required corrections:** 1. **Notes:** 2. **§5 impact:** yes.  
**Disposition:** owner correction and fresh applicable owner/receiver review required before integration. No structural rebuild is indicated.

## Scope and authority

I read the complete current Phase 10 owner, sections 0–12, rather than treating D-P10-27/28 or the previous findings as the review boundary. The review covered classic layout and encoding, scalar/bulk ingress, late mutation, task stacks, state/sort/upload provenance, client/VBO/list submission, cached model and sky/star products, deferred FastTESR ranges, native restoration, geometry conversion, lighting/cache transitions, compatibility detection, milestone staging, both spikes, decisions and implementation obligations.

The selected authority is **`docs/design/v3/DESIGN.md`**, as declared by this owner—not the newest design selected globally. I read its Part I G0–G12 and Phase 10 specification at lines 2164–2274, including the document gate. Governing research included `docs/research/v1/RESEARCH.md` §§0–1, §4.6 vertex material, §7.4, complete Appendix C and Appendix E rows 3–9, together with the relevant attribute, lighting, per-draw and OQ rows. `docs/MOVES.md` establishes versioned citation resolution and the retired harness boundary.

Dependency and reciprocal checks used the current versioned contracts: `docs/phase4/v1/PHASE_4_DOC.md` §5 and effective geometry/attribute handoff; `docs/phase7/v1/PHASE_7_DOC.md` §5 and incorporated lifecycle, health and prepared-submission receivers; `docs/phase9/v1/PHASE_9_DOC.md` §5 plus exact stamping and deferred-range semantics; and the load-bearing foundation grant in `docs/phase1/v14/PHASE_1_DOC.md` §§4.7.6/5. The downstream evidence receiver was checked in `docs/phase2/v2/PHASE_2_DOC.md:1344–1370`. These are boundary checks, not sibling verdicts.

## Independent checks

### 1. Scalar ingress and source-only advancement

The seven current writer targets in `docs/phase10/v1/PHASE_10_DOC.md:1415–1455` agree with independently queried MCP 1.12.2 BufferBuilder mappings: position, both color overloads, UV0, UV1, normal and `nextVertexFormatIndex`. The public vanilla [BufferBuilder source](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/BufferBuilder.java) independently establishes that ordinary scalar bodies compute addresses using the current physical element/index, float color delegates to integer color, `noColor` returns without advancement, and physical advancement skips padding recursively.

The current design at owner lines 412–465 addresses that actual mechanism: semantic selection precedes stores; advancement is replaced on the active path rather than merely corrected at `endVertex`; the floating overload does not write/advance twice; finally restores transient ownership; and both cursors reset at a successful vertex boundary. This fixes the source-sequence problem for the real [fluid chain](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/BlockFluidRenderer.java) `pos→color→tex→lightmap` and [TexturedQuad chain](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/model/TexturedQuad.java) `pos→tex→normal`. However, preservation of omitted *storage* does not yet preserve omitted conventional-input *delivery*; C1 below remains.

### 2. Bulk, partial vertices, state and failures

Owner lines 455–489 require whole-product invalidation on exceptional scalar/end/bulk paths, forbid sealing a successful prefix, reject bulk entry with a pending scalar vertex, and allow scalar/bulk quad composition only across completed vertices. Save/sort/seal require a vertex boundary; restore retains authenticated source identity; reset clears transient association. The current Cleanroom corroborating patch, `reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/BufferBuilder.java.patch:12–22,63–70`, confirms the extra-record growth allowance and Forge ByteBuffer ingress that those rules must preserve. I found no additional required correction in these transition contracts.

### 3. Layout, math and identity

Appendix C's 56-byte layout, locations 10/11/12, three identity components and both draw paths are mapped. Owner lines 355–375 preserve Phase 9's signed-short bit interpretation instead of changing aliases into unsigned floating values. This agrees with `docs/phase9/v1/PHASE_9_DOC.md:661–682` and its §5.1 receiver contract at lines 921–924.

The ordinary and mirrored-square calculations in owner §4.3 use the required `N×T` handedness. Available permitted Pintonium source independently corroborates the documented contrast: `reference-src/Pintonium-main/common/src/main/java/org/embeddedt/embeddium/impl/util/QuadUtil.java:78–117` uses the diagonal normal; `.../render/chunk/vertex/format/ChunkVertexExtendedData.java:70–113` uses `T×N`, packed byte tangents and different degenerate handling; and `reference-src/Pintonium-main/forge122/src/main/java/org/taumc/celeritas/impl/render/terrain/compile/VintageChunkBuildContext.java:179–237` computes the quarter-average UVs during conversion. These corroborate the rejection of numeric equivalence, not the identity of the unavailable historical checkout. The classic floating attribute declarations and opt-in are independently present in `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:115–121,342–348`.

### 4. Native capture, geometry and lifetime

The permitted vanilla [ModelRenderer source](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/model/ModelRenderer.java) confirms three `render` playback branches, one `renderWithRotation` branch, and compilation from `postRender` without playback. The [RenderGlobal source](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/RenderGlobal.java) independently confirms all three producer families, VBO sealing/reset before upload, the four list plus three VBO playback anchors, and the final lower-list call outside the VBO conditional. It also confirms that resource reload updates damage icons and that `deleteAllDisplayLists` is empty: neither is misrepresented as an existing vanilla sky rebuild/deletion implementation by the current design.

Owner §4.6 specifies authenticated incarnations, full staging before capture, original/derived atomic eligibility, first-compile model scale retention, every-call guards, same-source conversion, final-use retirement and failure containment. Conditional conversion follows the explicit maintainer scope permission in `docs/decisions/GEOMETRY_PRIMITIVE_COMPATIBILITY.md`; it does not claim native-QUADS parity. P1's actual service at `docs/phase1/v14/PHASE_1_DOC.md:4104–4281` grants source authentication, mode separation, generic-zero/capture isolation, pre-mutation validation and restoration. C1 identifies a remaining mismatch between those sound isolation mechanics and P10's supplied conventional-input plan.

### 5. Receivers, publication and health

The P4 effective-provider handoff exists at `docs/phase4/v1/PHASE_4_DOC.md:2073,2086–2089`; P7's receiving branch at `docs/phase7/v1/PHASE_7_DOC.md:2422–2438` takes the category only from actual successful activation and clears fixed-function authority. P10 does not invent a current-program query.

The lifecycle/result domains and placement agree across P10 lines 1581–1660 and P7 lines 2240–2368: real worker/upload/source drain, prepare before acceptance, matched P9 lookup/map installation, Activated followed by invalidation before atomic admission, and recovery ownership retained on failed drain. The exact invalidator rejection domain also agrees with P7 lines 2682–2697. Deferred FastTESR ownership and native-range boundaries are received in P10 lines 1114–1141 and 1533–1549 against P9 lines 1073–1112 and P7 lines 1791–1806.

**The complete R10-8 receiver exists.** P7 D-P7-62 at lines 1808–1822 explicitly receives all seven H10-WRITER IDs, each expected=1 complete method wrapper, the amended existing ingress cleanup semantics, full sorted-unique fingerprint renewal, unchanged v0.1 subset, retained model/sky/batch rows and unchanged forwarding to P2. P7 §5.1 at lines 2359–2370 and its report types at lines 2830–2849 preserve the single owner10 subreport. P2's generic complete-subreport receiver at lines 1354–1370 can carry the added rows without a new schema or compressed aggregate. This is architecture receiver completeness, not observed hook application.

D-P10-28's current schema23 admission at owner lines 1681–1698 agrees with P7 D-P7-57 at lines 3538–3541 and current P9 §5.2. Earlier numeric receipts are not competing current gates. P10 does not acquire binary assets or resolve ID selectors.

### 6. Remaining document gate

The growth design preserves named descriptors and defers modern producers; the constant-attribute candidate is explicitly rejected with an App C/App D contract check; compatibility detection and its user message are specified; and both OQ-5/OQ-14 have questions, procedures, success/failure criteria and safe fallbacks. Available Cleanroom `LightUtil.java:122–158` and `BakedQuad.java.patch:20–26,38–56` corroborate the retained-format/static-fast-path cache risk. The owner correctly does not treat Pintonium's replacement renderer as proof that Forge cache switching works. Planned tests, runtime cases and T2 exits remain future implementation obligations rather than architecture-gate prerequisites.

## Required correction

### C1 — Preserve inherited conventional inputs when projecting colorless formats

**Severity:** correction, high impact (P1). **Owner:** Phase 10. **§5 impact:** yes.

**Current owner evidence:** `docs/phase10/v1/PHASE_10_DOC.md:377–386` exposes COLOR and lightmap as ordinary FF elements in the projection; lines 443–453 and 492–496 initialize source-missing color/lightmap to white/zero; lines 710–713 retain ordinary Forge `preDraw`/`postDraw`; lines 719–726 configure VBO FF pointers from the stored layout. The model capture path at lines 918–942 captures those projected runs for later playback. The source semantic is retained for writing, but the draw/capture contract does not distinguish a physically allocated filler slot from a conventional input actually supplied by the original source.

**Independent affected path:** [DefaultVertexFormats](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/vertex/DefaultVertexFormats.java) defines OLDMODEL with POSITION/UV0/NORMAL/PADDING, **without COLOR or UV1**. [LayerSheepWool.doRenderLayer](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/entity/layers/LayerSheepWool.java) establishes the current fleece RGB using `GlStateManager.color`, then renders its model. [ModelSheep1](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/model/ModelSheep1.java) inherits [ModelQuadruped.render](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/model/ModelQuadruped.java), which reaches the canonical ModelRenderer → [ModelBox](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/model/ModelBox.java) → TexturedQuad route admitted by P10.

The actual permitted Forge consumer at `reference-src/Cleanroom-0.6.12-alpha/src/main/java/net/minecraftforge/client/ForgeHooksClient.java:482–490` enables the color array and each projected UV array. Consequently, enabling the new white COLOR stream replaces the live fleece tint with white vertex input. The [Khronos glDrawArrays specification](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Refpages/main/gl2.1/glDrawArrays.xml), Description and Notes, establishes both that enabled array values are used and that their necessary data is captured into display lists. A later replay guard or restoring current color afterward cannot undo white color already embedded in the geometry. This loses dyed/rainbow sheep coloration for a shader consuming ordinary `gl_Color`; it is not restricted to unknown/custom native producers. Zero-filled missing UV1 has the same delivery-design problem: allocating storage is not authority to replace an inherited current conventional coordinate.

**Minimal owner fix:** retain explicit source-semantic conventional participation through the authenticated projection, layout/input plan and saved/native product identity. Physical white/zero initialization may remain as storage hygiene, but a source-absent COLOR/UV1 must not automatically become an enabled conventional stream. Specify the correct inherited-current behavior for live draws and keep those attributes absent from model-list capture so current values remain effective at each playback; do not bake the first entity's current values as a substitute. Ensure Forge setup and cleanup do not enable/reset source-absent conventional inputs, and that P1 receives the same complete admitted plan for client, VBO and capture paths. Preserve real source-provided BLOCK color/lightmap, existing quad attributes and shader-off behavior. Amend the incorporated §5.1 projection/input/product contracts and corresponding §8 cases, with a changing fleece color across reuse of one cached model as the observable regression scenario. Obtain applicable P1/P7 receiver review if the plan representation changes; no new native allocator or renderer API is needed merely to fix this policy.

## Notes

### N1 — Historical pinned evidence remains unavailable, with permitted corroboration available

**Severity:** note; no required correction. Owner provenance at `docs/phase10/v1/PHASE_10_DOC.md:43–96` cites `reference-src/pintonium-9c2fcc1/` and `reference-src/cleanroom-0.6.6-alpha/`. Those exact paths are absent in the current reference inventory; the configured 0.6.10-alpha source tree is likewise unavailable locally. Available `reference-src/Pintonium-main/` and `reference-src/Cleanroom-0.6.12-alpha/` sources corroborate the concrete facts identified above, but are not silently relabeled as the historical pinned artifacts. The original dated observations remain historical evidence. No restoration of forbidden binaries, decompile mining or transformed-runtime conclusion is warranted.

### N2 — Pending-receipt prose is stale, but the current reciprocal grants are present

**Severity:** note; cross-owner status accounting only, not a sibling verdict or required behavioral correction. Owner lines 808–814 still include a pending-P4 sentence despite its adopted R10-5 row at lines 1707–1710. R10-8 remains described as awaiting Main in owner lines 1450–1455, 1713 and 2167–2169, whereas current P7 D-P7-62 explicitly receives the complete contract at `docs/phase7/v1/PHASE_7_DOC.md:1808–1822`. Likewise P7 D-P7-53 at lines 1778–1789 receives the sky/star extension and owner §5.3 already records it. These lagging status sentences are not evidence that the receiver is missing, and are not counted as substantive contract corrections. Preserve historical addenda/reports; integration may reconcile active status summaries without promoting architecture receipts into runtime proof.

## Limitations and final disposition

Read-only review: no repository files were edited, no report file was written, and frozen owner bytes were left untouched. No builds, tests, formatters, linters, validation commands, runtime experiments or checksum commands were run. MCP results establish mapping identities only; public vanilla mirrors and available Cleanroom patches establish separately identified source behavior, not the configured transformed target. OptiFine access was limited to the permitted vertex behavioral digest and shipped author documentation. No prohibited Oculus/transformer material or session transcripts were consumed.

This review does not certify implementation, native driver behavior, pack tiers, OQ outcomes, sibling phases or final integration. C1 is a concrete current input-delivery defect, not a request for unexecuted runtime evidence at the architecture gate. The remainder of the reviewed architecture is sufficiently structured for a local correction rather than a rebuild.

**Final literal verdict: PASS-WITH-CORRECTIONS.** One required Phase 10 correction remains; its incorporated §5 contract impact requires fresh applicable review before final integration.

## Resolutions

### C1 — Inherited conventional inputs (2026-09-08)

Resolved architecturally by **D-P10-29** in the current Phase 10 owner, not by changing
the frozen evidence or verdict above. §2.2 adds immutable nonnull
`Set<ConventionalInput> conventionalInputs` to `VertexInputPlan`, with the closed domain
POSITION/COLOR/UV0/UV1/NORMAL. §§4.1–4.6 now make source semantics authoritative for
Forge projection/setup/cleanup, live client/VBO binding, saved-state/upload identity
and original/derived model capture/replay. CLASSIC_56 remains 56 bytes.

Source-absent COLOR/UV1 filler is storage hygiene, never enabled/reset/captured as a
stream or replaced by first-compile constants. Foreign arrays are temporarily isolated
through P1; each cached playback inherits the caller's then-current color/lightmap.
Source-provided BLOCK color/lightmap and existing generated-normal/tangent, other
attributes and shaders-off behavior are preserved. Complete source participation and
plan identity accompany authenticated products, not stride-only acceptance.

§5 exposes **R10-9** for exact P1/P7 receipts through existing bind/restore and prepared
submission/lifecycle seams; no new native allocator/renderer API or hook IDs are granted.
§8 adds T10-CONVENTIONAL-INHERITED/PROVIDED and cached-fleece/current-UV1 runtime cases;
§12 carries the corresponding implementation obligations. These are planned observable
cases, not executed tests. No implementation or validation was run; fresh applicable
owner/receiver review remains required. N1 historical evidence limitations are unchanged.

P1's subsequently landed **D-P1-68**, §4.7.6/§5.2, was read and adopted in P10's
§5.3 R10-9 row: exact component/domain, all-mode inheritance/capture behavior and
complete product identity agree. This is reciprocal architecture receipt, not validation;
P7 integration and fresh applicable review remain separately owned.
