# Phase 2 — Fresh Whole-Document Architecture Review R43

## Frozen owner and disposition

- **Owner:** `docs/phase2/v2/PHASE_2_DOC.md`
- **Review round:** 43, attempt 5.
- **Frozen owner SHA256:** `e32f5c68c0e8679cccc28f63350fefd1c7bd9737e69a6827235fb362e1236078`, as assigned and recorded in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json:10–14`. This is the supplied frozen identity, not a claim of an independently executed checksum command.
- **Scope:** the entire current owner, sections 0–12, including historical qualifications, detailed formats, all binding §5 interfaces, failure handling, testability, staging, spikes and implementation checklist. D-P2-50–53 were focus items, not the review boundary.
- **Result:** one required owner correction; two notes. The correction changes the binding §5 receiver contract. No structural rebuild is warranted.

## Authority and independent method

The governing design is the revision selected by this owner's header, **`docs/design/v3/DESIGN.md`**, not a globally inferred newest revision. I read its Part I (§G0–G12) and Phase 2 specification (`:1261–1341`), and the governing research: `docs/research/v1/RESEARCH.md` §§0–1, §§8–9, Appendix G, RenderBook inputs, and the cited scene/resource/GLSL/licensing/glossary extents. RESEARCH remains normative; Pintonium's report remains evidence only. `docs/MOVES.md` was used to interpret historical aliases and distinguish historical records from current ownership.

I independently read `docs/phase1/v14/PHASE_1_DOC.md` §5, its relevant profile, recording and CI details, and followed the load-bearing P3/P4/P5/P6/P7/P8/P9/P11/P12/P14 producer and receiver contracts. Existing reviews were not used as proof that a correction works. The reasoning below is based on current owner and reciprocal contract text. No repository file was edited; no build, test, lint, formatter, runtime capture or other validation command was run.

## Independent checks

1. **Whole-phase gate and ownership.** The milestone mapping at `docs/phase2/v2/PHASE_2_DOC.md:529–572`, scene families at `:486–525`, named catalogue at `:1715–1738`, and before-renderer subset at `:2942–2969` provide the architecture required by the selected design. Six-family dense motion is mandatory independently of static classic-pack T2. The document does not require future rendered motion evidence to close this architecture gate. OQ-10 retains a concrete experiment, success/partial/failure conditions and fallback in §§10.1–10.4, without claiming an executed result.
2. **Durable timing, D-P2-50.** `docs/phase2/v2/PHASE_2_DOC.md:1197–1285` makes actual origin, preparation, warm-up and sample observations required core `/4` evidence, distinct from sample-only `frames`. It binds exact coverage, actual cumulative acknowledgments, live validation, sample joins and final restoration to the manifest/plan closure. The comparison payload is regenerated from authenticated manifests rather than synthesized from targets (`:829–847`, `:1271–1285`). The receiving owner at `docs/phase7/v1/PHASE_7_DOC.md:3037–3102` explicitly supplies live report validation, retention and D-P7-50 durable serialization. P6's actual eight-field `UniformFrameTiming` and query grant are present at `docs/phase6/v1/PHASE_6_DOC.md:518–521,1808–1812`. No additional correction was established in this focus area.
3. **Conformance identity and oracle closure.** The runner freezes required evidence domains before execution, rather than accepting whichever artifacts succeeded (`docs/phase2/v2/PHASE_2_DOC.md:683–797`). T0 requires no image oracle; T1/motion/T3 require their appropriate complete image domains; T2 requires every selected static ordinal. Comparison artifacts authenticate effective policy, timing, approval/oracle metadata and external raster inputs (`:799–882`). Complete option-state identity and distinct OFF/ON approvals are preserved (`:1556–1623`); generation descriptors include admitted generation settings and exclude subject-only rebuild identity (`:1418–1447`). Manual G6 timing is explicitly uncontrolled and cannot pass without independent comparability evidence (`:1662–1694`). These are architecture contracts, not observed pack-tier results.
4. **Schema22/BLOCK and source-free inspection, D-P2-51.** Current admission at `docs/phase2/v2/PHASE_2_DOC.md:1981–2028,2479–2494` agrees with `docs/phase3/v1/PHASE_3_DOC.md:2960–3030,3882–4023,4040–4046`: exact current containing/nested/inspection schema, nine trees, projectionVersion 1, same-load archive provenance and no asset-byte export. P3 produces ordinary CLASSIC and isolated forced11300 MODERN lists; P9's actual selector at `docs/phase9/v1/PHASE_9_DOC.md:600–619` uses the alternate only for PRESENT_EMPTY/nonempty alternate. P2 preserves, rather than recreates, that provenance. Historical schema19/20/21 receipts are expressly superseded by the closing current-admission rule (`docs/phase2/v2/PHASE_2_DOC.md:3435–3439`), so they are not separate required corrections.
5. **P4/P5 enrichment and transport.** `docs/phase2/v2/PHASE_2_DOC.md:2105–2179` joins the exact inspected configuration to same-request P4 resolutions and P5 pure planning. This agrees with `docs/phase4/v1/PHASE_4_DOC.md:2201–2249` and `docs/phase5/v1/PHASE_5_DOC.md:2561–2575`. Own-build disposition remains distinct from effective fallback, and PLANNED evidence cannot become REALIZED allocation evidence. P1 profile transport is one escaped canonical text scalar, not an invented flattened parser (`docs/phase2/v2/PHASE_2_DOC.md:1181–1195`; `docs/phase1/v14/PHASE_1_DOC.md:2895–2965`). Replay-error collection preserves original ordering and attribution and fails capture on delivery loss; a successful rendered image cannot repair omitted evidence.
6. **Typed evaluator diagnostics, D-P2-52.** `docs/phase2/v2/PHASE_2_DOC.md:1740–1787` receives P11's required diagnostic sink after metrics, actual ordered kind/ID/location observations, separate load results, SourceLess diagnostics and collector-failure handling. These match `docs/phase11/v1/PHASE_11_DOC.md:1256–1353`. Original vectors, disposition-only local matrix execution and runtime pack conformance remain distinct.
7. **Native versus facade evidence, D-P2-53.** `docs/phase2/v2/PHASE_2_DOC.md:2637–2643` correctly receives source-free measured summaries without claiming private native traffic from facade records. `docs/phase14/v1/PHASE_14_DOC.md:1821–1839,2035–2063` supplies the separate native observation/fault-injection procedure and preserves optional-grant boundaries. No new observation API or optimization result is implied.
8. **Source/API and CI grounding.** The Cleanroom mappings tool independently returned the 1.12.2 `ScreenShotHelper.createScreenshot` SRG `func_186719_a` and descriptor `(IILnet/minecraft/client/shader/Framebuffer;)Ljava/awt/image/BufferedImage;`, matching owner `:1099–1101`. The three actual `.github/workflows/*.yml` files remain template-shaped; the named seam/conformance steps and module artifact retargeting are future Phase 1 architecture (`docs/phase1/v14/PHASE_1_DOC.md:4785–4837`), not executed CI evidence. P2's task split and OQ fallback preserve that distinction in their implementation context.

## Required correction

### C43-1 — Receive the current nine-row shadow health report without dropping H8-REBUILD-01

**Severity:** correction; medium integration impact. **Owner:** Phase 2. **§5 impact:** yes.

**Current owner evidence:** `docs/phase2/v2/PHASE_2_DOC.md:1360–1363` still defines the Phase 8 projection as “all eight rows.” Binding request R18 at `:2710` repeats “eight-row nested report,” and `HookManifestEvidenceTest` at `:2844` requires “exactly eight IDs/counts/dispositions.” These are active serialization/receiver/test requirements, not dated historical quotations.

**Actual producing and forwarding contracts:** `docs/phase8/v1/PHASE_8_DOC.md:1493–1501` now enumerates nine health rows, including the new **H8-REBUILD-01** at `:1496`. Its binding grant at `:1552–1559` expressly requires that row, the revised traversal/accessor evidence and revised fingerprint to pass through P7 to P2. `docs/phase7/v1/PHASE_7_DOC.md:3007–3014` preserves the complete immutable owner subreport, and D-P7-52 at `:4239` explicitly receives the forced-rebuild health change and copies all owner subrows.

**Observable breakage:** when the current Phase 8 owner is installed, P7 must forward nine rows. A P2 reader/test enforcing the current exact-eight requirement rejects otherwise complete current hook evidence, preventing COMPLETE/T0 publication. Truncating the report to satisfy eight instead silently drops required hook evidence and contradicts the field-for-field/fingerprint contract. H8-REBUILD-01 cannot be represented as an omitted historical row or inferred from a successful frame.

**Minimal owner fix:** amend P2 §4.5.4, §5.4 R18 and the §8.1 test contract together to receive the complete current owner-issued row set, explicitly including H8-REBUILD-01 and the revised fingerprint/anchor evidence. Prefer owner-catalog completeness over a permanently hard-coded cardinality; if stating the current number, it is nine. Preserve the existing generic dense wire grammar and owner ordering, without manufacturing health, changing wire major solely for a new row, or rewriting historical reports.

## Notes

### N43-1 — Historical pinned Pintonium evidence remains distinct from available corroboration

**Severity:** note; no owner correction required. The alias `reference-src/pintonium-9c2fcc1/` from `docs/MOVES.md` is absent in this checkout. The available permitted files are `reference-src/Pintonium-main/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java` and `.../MixinRenderGlobal_Shaders.java`; I read their narrow hook surfaces. The former currently separates first-clear preparation from post-setupCameraTransform matrix capture (`:106–125`) and finalizes at the render-world tail (`:170–178`); the latter has translucent and selection-outline hooks. This is available-checkout corroboration, not authentication of the historical commit or proof of entire-renderer feature absence. `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:163–210,374–489,822–840` remains dated report evidence. P2 already qualifies the temporal-blurring README claim and retains the independently normative v3 motion requirement (`docs/phase2/v2/PHASE_2_DOC.md:516–520,3157`). No unavailable historical source was silently upgraded.

### N43-2 — Cross-owner note: reconcile P12 programmatic seven-key admission with complete eight-key capture state

**Severity:** cross-owner note; not counted as a Phase 2 correction or sibling verdict. P2 requires complete resolved engine maps, including defaults (`docs/phase2/v2/PHASE_2_DOC.md:1563–1581,2416,2721–2724`), consistent with P3's eight-key baseline (`docs/phase3/v1/PHASE_3_DOC.md:3381–3385`) and P7's reserved-zero antialiasingLevel inventory (`docs/phase7/v1/PHASE_7_DOC.md:3304–3306`). However, `docs/phase12/v1/PHASE_12_DOC.md:1415–1418` limits the programmatic bridge to the seven user-control keys and says no AA. The P12 owner should distinguish rejecting an AA control/nonzero value from accepting the reserved zero in complete state. I sent this precise reciprocal concern to the Phase 12 reviewer; it is not a demand to weaken P2's complete-state identity and does not certify P12.

## Limitations and final disposition

This is read-only architecture review, not code, native/runtime, CI, OQ, pack-acquisition, calibration, image-oracle or pack-tier certification. Optional ungranted APIs remain ungranted. The assigned owner received no edits. Historical evidence and unavailable pinned sources retain their stated confidence; no decompiled implementation, forbidden Oculus transformation material, chatlogs or root transcripts were used.

**Final verdict: PASS-WITH-CORRECTIONS.** Apply C43-1 in the Phase 2 owner and obtain fresh review for its changed §5 receiver surface. Final integration and implementation clearance remain separate.

## Resolutions

### C43-1 — Complete owner-issued scalar shadow evidence

D-P2-54 replaces active eight-row limits in serialization, R18 and testability with the
complete P8 D-P8-31 canonical flattened scalar catalogue. Current 59 rows preserve each
independent application/member observation, owner ordering, disposition, aggregate and
fingerprint; group labels and runtime witnesses are not substitute rows. P7 D-P7-58
receives and forwards the same catalogue. Existing dense /4 grammar remains.

Other coordinated receipts D-P2-55–60 cover schema23/typed ID projection, independent
profile intent/reserved zero, native ordering evidence, atlas application/runtime separation,
and synchronous texture values. These are authored contracts, not fresh verification.
Original R43 body, notes and PASS-WITH-CORRECTIONS verdict remain unchanged.
