# Phase 1 — Independent whole-owner architecture review

**Round:** R34 · **Frozen inventory:** attempt7 · **Date:** 2026-09-08  
**Owner:** `docs/phase1/v14/PHASE_1_DOC.md`  
**Supplied SHA-256 identity:** `fad7a5e03f82cb5579d9ff10c23f5414877d7a85061147f41ffe4efdcee92625`

The hash above is the supplied inventory identity from `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_7.json:5-10`, not a checksum calculated by this review.

## Scope and selected authority

I independently reviewed the complete current owner, §§0–12, including the historical addenda, active declarations and incorporated semantics, §5 exports, failure handling, testability plan, milestones, OQ dispositions, decisions and implementation checklist. This is not a review limited to D-P1-66/67/68 or a verification of earlier fix receipts.

The governing design is **`docs/design/v2.0-RC2/DESIGN.md`**, selected by the owner at `docs/phase1/v14/PHASE_1_DOC.md:12-18`. I read its Part I and complete Phase 1 specification, including the architectural doc gate at `docs/design/v2.0-RC2/DESIGN.md:1052-1064`. `docs/MOVES.md:69-110` confirms that authority is selected per owner; global newest v3 is not substituted. Governing research is `docs/research/v1/RESEARCH.md`, including the required §1, §§5.1–5.3, §6.1, §7.2 and §12.2, plus the relevant lifecycle, program/FBO/uniform, geometry, macro and texture contract material. The bounded Pintonium reading covered `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §§2, 16–18 and the permitted service/bootstrap/plugin source corroboration.

Phase 1 declares no dependency phases (`docs/phase1/v14/PHASE_1_DOC.md:75-77`, `:5356-5358`); there is therefore no upstream dependency §5 to import. I nevertheless inspected actual reciprocal receiving algorithms, not just acknowledgment tables, wherever the foundation's current grants cross into downstream owners. Those inspections are evidence for this Phase 1 verdict, not independent certification of those owners.

## Independent checks

1. **Foundation and build conversion.** The three-module dependency graph, closed package placement, C-1 through C-4, first-party engine packaging, conditional pure-JVM jcpp admission, source/resource templates, Mixin configuration agreement, current script placement, logging, diagnostic routing and bail mechanisms were checked against the actual root build/settings/properties/scripts, wrapper, all three workflows, README and template source/resources. The active September ledger at `docs/phase1/v14/PHASE_1_DOC.md:2419-2448` correctly distinguishes wrapper 9.7.0, Unimined 1.4.36-kappa and loader 0.6.10-alpha from the retained July evidence, and explicitly requires reconciling CI's 9.6.1 setup inputs. No historical pin was treated as a newly verified release or runtime result. Current MCP mixin guide/template responses corroborate manifest `MixinConfigs`, built-in CleanMix, phase targets and the separate implementation-time refmap check.
2. **Authority and seam completeness.** The RC2 doc-gate requirements have concrete homes: module/seam design, D-1…D-10 disposition, pin/re-pin procedure, the shim/glsm inventory and justified bootstrap deviation. The current permitted Pintonium service interfaces corroborate the inventory's game-versus-GL distinction. Its current startup mixins corroborate loadOptions HEAD, initializeTextures RETURN and main-menu initGui RETURN. P7's actual hook catalogue receives the FML prerequisites and GL-ready signal at `docs/phase7/v1/PHASE_7_DOC.md:1582-1584`; its compatibility execution receives the real `CompatEvaluation` and terminal bail at `:1886-1895`. P10's early receiver at `docs/phase10/v1/PHASE_10_DOC.md:1363-1378` preserves class-only, no-initialization whole-family veto.
3. **Capabilities and synchronous textures.** The complete target-bearing values, four-target native dispatch, checked byte/range rules, owned/foreign permission split and complete object parameter baseline were reviewed. P2's actual profile refresh/admission rules at `docs/phase2/v2/PHASE_2_DOC.md:2262-2325` receive the mandatory distinct 3D/rectangle keys without guessing. P5's target-axis admission at `docs/phase5/v1/PHASE_5_DOC.md:1420-1485` and P13's pure preparation/native conversion at `docs/phase13/v1/PHASE_13_DOC.md:952-1083` preserve target-specific limits and distinct RECTANGLE dispatch. The Khronos ARB_texture_rectangle specification independently confirms the separate maximum and nonmip/clamp restrictions.
4. **Program/geometry and duration state.** The native pre-link triple, effective linked-input cache, selection lifecycle, fullscreen compatibility and error containment were checked against the Khronos [ARB_geometry_shader4 revision 26 specification](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_geometry_shader4.txt), including its OpenGL 3.2 source-precedence and effective-query distinction. P4's actual build machine at `docs/phase4/v1/PHASE_4_DOC.md:1450-1475` consumes configure/link inspection and all sampler-initialization result branches before validation/publication; `:1530-1547` rejects linked disagreement. The duration-lock path is genuinely received by P4's release-before-acquire activation at `:1780-1805` and P7's concrete nine HEAD/six normal RETURN interception family at `docs/phase7/v1/PHASE_7_DOC.md:1684-1719`, rather than being an immediate setter mislabeled a lock.
5. **Sampler and replay boundaries.** P14's actual receiving algorithms at `docs/phase14/v1/PHASE_14_DOC.md:617-648`, `:692-741` and `:764-776` preserve complete object state, successful-preflight normalization, ordinary fixed-function clearing and replay-before-NONE demotion. P6's upload algorithm and typed-report delivery at `docs/phase6/v1/PHASE_6_DOC.md:1340-1425` preserve cached values, original-error order, honest false attribution and synchronous receiving failure handling. P12's actual panel path at `docs/phase12/v1/PHASE_12_DOC.md:1204-1223`, `:1254-1260` receives P1 diagnostics without inventing another channel.
6. **Vertex isolation and current-value behavior.** I checked complete-plan identity, generic-zero isolation, all-array capture isolation, descriptor/current-value restoration and LIFO/failure semantics against the Khronos [ARB_vertex_shader specification](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_vertex_shader.txt). Its ArrayElement ordering, post-draw indeterminate current values, immediate client-state commands during list compilation and invalid current-generic-zero query support those requirements. P10's real producer/capture/replay rules at `docs/phase10/v1/PHASE_10_DOC.md:698-727` and P7's complete-plan transport at `docs/phase7/v1/PHASE_7_DOC.md:2453-2463` receive D-P1-68. The recorder issuance gap below remains despite those correct receiving requirements.

## Substantive corrections

### C34-1 — Normalize rasterizer discard inside the full-extent clear transaction

**Severity:** P2 · **Owner anchor:** `docs/phase1/v14/PHASE_1_DOC.md:4004-4011` · **Affected export:** `:5427`.

The new typed clear promises to initialize the full attachment, but its explicit actual-state snapshot/normalization list omits rasterizer discard. A concrete admitted case is the GL2 `glClear`-backed route (`:3987-3991`) on a context exposing `GL_EXT_transform_feedback`, entered with `RASTERIZER_DISCARD_EXT` enabled. The Khronos [EXT_transform_feedback specification, Chapter 3 addition “Discarding Rasterization”](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/EXT/EXT_transform_feedback.txt) expressly applies discard to `Clear`; the operation is ignored rather than producing the GL error on which this contract's success check relies. There is no discard-disabled admission precondition or normalization branch in P1, P5 or P7. Thus the exact prescribed clear can return with clean drains and successful restoration without writing the attachment.

**Observable breakage:** P5's actual receiver dispatches each occupied position and treats only exceptions/work/restore errors as failure (`docs/phase5/v1/PHASE_5_DOC.md:1791-1805`). It then clears `estate.fullClearRequired` after apparently successful completion (`:1807-1812`), leaving old or undefined color contents marked initialized. This is a native-success/no-op path, so the existing error scripts do not detect it.

**Minimal owner fix:** Include capability-legal rasterizer-discard state in the existing private clear transaction: snapshot its actual value, disable it before the clear where supported, restore the exact predecessor in finally, and retain existing error/poison rules. Unsupported contexts must neither query nor change the enum. Update the incorporated §5 typed-clear row and the existing planned clear-state cases; no public transform-feedback API or rendering-policy expansion is needed. Synchronize P5's copied restoration contract so the receiving success branch cannot omit the added state obligation.

### C34-2 — Supply the captured complete plan when issuing recorder list sources

**Severity:** P2 · **Owner anchor:** `docs/phase1/v14/PHASE_1_DOC.md:4408-4417` · **Affected exports:** `:5399` and `:5428`.

D-P1-68 now requires `LIST_REPLAY_GUARD` to authenticate the captured **whole** plan, including ordered generic pointers and conventional participation, and forbids reuse after a mask/plan change (`:4263-4282`). The exact recorder factory still accepts only `borrowedVertexList(String label, VertexLayout layout, VertexGeometryInput input)`. Neither the physical layout nor geometry category contains the original-source COLOR/UV1 participation or captured generic union. The closed `ScriptedResponses` surface at `:4117-4134` supplies no missing capture-plan input, despite `:4416-4417` describing these as explicitly scripted replay-safe captures.

**Observable breakage:** The recorder cannot issue and distinguish the two legitimate list products with identical physical CLASSIC56 layout and geometry but different captured conventional masks that P10 expressly defines at `docs/phase10/v1/PHASE_10_DOC.md:698-725`. It therefore cannot perform the promised captured-plan mismatch rejection on the first replay: it must either invent a default, trust the replay request it is supposed to authenticate, reject a valid product, or add an unpublished issuance protocol. P10's actual receiver explicitly requires the exact P1 fixture factories, not locally minted sources (`docs/phase10/v1/PHASE_10_DOC.md:1815-1823`), so a downstream workaround is not a granted route. This undermines the foundation's headless wrong-plan coverage, not merely the wording of a test.

**Minimal owner fix:** Amend recorder list issuance to receive and privately retain the complete immutable authenticated capture-plan fixture alongside the layout/identity, or declare one equally explicit synthetic capture-to-list issuance route carrying that same information. Compare subsequent replay requests to that retained product metadata; do not learn authority from the first replay request or reconstruct it from CLASSIC56 fields. Update the exact §4.7.6 declaration, incorporated §5 vertex/participation rows and P10's fixture receipt. Keep production raw-name adoption forbidden and retain existing source lifetime, geometry and LIFO rules.

## Notes

### N34-1 — Current source corroboration does not reauthenticate the historical pin or blanket license

**Severity:** informational, nonblocking · **Owner evidence:** `docs/phase1/v14/PHASE_1_DOC.md:5124-5133`, `:5289-5298`.

The historical `reference-src/pintonium-9c2fcc1/` path named by the owner is unavailable in this checkout. I inspected the corresponding permitted files under `reference-src/Pintonium-main/` instead, including the complete shim/glsm/version-service interfaces, startup mixins, declarative config/plugin structure, settings and the actual main-framebuffer delegations at `reference-src/Pintonium-main/forge122/src/shaders/java/org/embeddedt/embeddium/impl/MinecraftVintageVersionShimImpl.java:553-560`. Those are present-day corroboration only; they do not establish byte identity with 9c2fcc1 or repeat its historical runtime claims. The replacement snapshot's root `LICENSE:1-4` is GPLv3 text, which is not proof of one license applying to every inherited subtree. No source was copied, no prohibited subtree was inspected, and this review makes no blanket LGPL-reuse or legal-compliance certification. Preserve the owner's historical evidence as historical; any implementation-time incorporation still needs exact file-level provenance and license verification.

## Limitations and verdict

This was a read-only architecture review. No builds, tests, formatters, linters, runtime probes, checksum commands or implementation validation were run. Planned tests and native proofs in §§8–12 remain planned; current MCP recipes and source inspection do not establish transformed-hook cardinality, driver behavior, rendering parity, runtime pin compatibility or source-license clearance. Historical review verdicts and fix acknowledgments were not used as proof. No sibling owner is certified by this report.

The foundation remains structurally coherent under RC2, so a structural FAIL is not justified. Two discrete current contracts require correction: the full-extent clear has a legal silent-no-op state, and recorder list issuance cannot carry the newly mandatory captured-plan authority. Both affect incorporated §5 obligations and require owner/receiver synchronization before a literal PASS can be issued.

**Corrections:** 2 · **Notes:** 1 · **Section 5 impact:** yes.

**Final verdict: PASS-WITH-CORRECTIONS**

## Resolutions — 2026-09-08 (architecture only, unverified)

- C34-1: P1 D-P1-69 amends the actual §4.7.4b transaction and §5 to save,
  disable and independently restore capability-legal rasterizer discard; unsupported
  contexts issue no query/change. Enabled-discard and failure cases enter §8/checklist;
  the exact P5 receiving delta was sent to its owner.
- C34-2: P1 D-P1-70 replaces the exact recorder list factory's geometry argument
  with complete immutable capturePlan, validates layout association and retains it
  before first replay. P10 adopts that exact fixture and first-replay mismatch cases.
  D-P1-71 separately reconciles P14 logical creation/exact-target materialization and
  lifetime-ending deletion; completed P10 BLOCK participation requires real brightness.
- Original findings, notes, frozen identity and PASS-WITH-CORRECTIONS verdict above
  remain historical. No validation command, implementation or native/conformance proof
  was run; source corroboration does not reauthenticate historical pins or licenses.
