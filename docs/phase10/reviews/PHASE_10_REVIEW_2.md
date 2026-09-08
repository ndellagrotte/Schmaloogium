# Phase 10 — Fresh Architecture Review R2

## Frozen owner and disposition

- **Owner:** `docs/phase10/v1/PHASE_10_DOC.md`.
- **Frozen SHA256:** `e0b49ad48f764d5d5d615816f1a94e5837fafbffb69b508428f2469da65e04a0`, as assigned and recorded for phase 10/reviewRound 2 in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json:52–56`.
- **Scope:** fresh independent review of the complete owner, §§0–12, not merely D-P10-24…26. The owner was not edited. The digest above is the frozen inventory identity, not a claim that a hashing command was executed.
- **Result:** one required, locally repairable architecture correction; one evidence note. The correction affects the §5-incorporated ingress/hook contract. No structural rebuild is warranted.

## Authority and independent evidence

The owner selects `docs/design/v3/DESIGN.md:132–1135` and its Phase 10 specification at `2162–2276`; that revision, including the mandatory template and document gate, governed this review. I read those requirements, `docs/research/v1/RESEARCH.md` §§0–1, the vertex requirements at `583–591` and `844–852`, all Appendix C at `1278–1316`, Appendix E’s relevant rows and coverage notes at `1387–1434`, and the associated attribute, lighting, milestone and OQ requirements. `docs/MOVES.md` was consulted to distinguish moved citations and retired workflow machinery from current governance. The narrow topology authorization in `docs/decisions/GEOMETRY_PRIMITIVE_COMPATIBILITY.md` was read separately; it grants bounded conversion, not arbitrary native APIs or parity.

Dependency checking used actual current grants and consuming routes: P4’s fixed attribute/effective geometry contracts (`docs/phase4/v1/PHASE_4_DOC.md:2017–2041`); P1’s complete vertex service, source authentication, isolation and restoration law (`docs/phase1/v14/PHASE_1_DOC.md:4093–4282`, incorporated by §5); P7’s lifecycle/declaration/health contract (`docs/phase7/v1/PHASE_7_DOC.md:2182–2383`), sky and deferred-batch receivers (`1769–1796`) and transaction (`3661–3739`); and P9’s stamp and deferred-range contracts (`docs/phase9/v1/PHASE_9_DOC.md:650–672,776–840,893–1100`). These reads establish receiver symmetry only; they are not sibling certification.

Permitted source checks included the OptiFine behavioral digest’s vertex section only (`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:413–455`), shipped floating attribute declarations, PD §§8.2/9, scoped current Pintonium math/renderer evidence, current Cleanroom renderer/Forge patches, and the separately identified vanilla 1.12.2 source mirror. MCP 1.12.2 independently resolved the principal Appendix E targets, BufferBuilder’s setter/advancement surface, VertexBuffer, all three sky generators, and ModelRenderer compilation. Mapping results were not treated as transformed-cardinality evidence.

## Independent whole-document checks

1. **Layout and arithmetic:** the classic offsets/stride, signed-short floating identity delivery, three identity components, normal and tangent encodings, ordinary/mirrored-UV examples, and deliberate rejection of Pintonium’s opposite cross order are consistent with Appendix C. The source reference’s different packing/degenerate behavior is not silently adopted. The incremental producer integration still has correction C1 below.
2. **Ownership and transitions:** task-local lookup/ordinal pairing, builder-exclusive stacks, immutable handoff metadata, saved-state provenance, queue-time plus execution-time stale rejection, and real worker/upload drain precede format/resource mutation. P7’s token results, recovery-only failures, post-Activated compensation and final-use retention are received rather than replaced by invented success states.
3. **Draw and capture boundaries:** client setup is placed after Forge preDraw; VBO pointers use the actual stored layout; list capture and live replay are separate; P1 owns generic-zero isolation, complete capture-array isolation, partial rollback and current-value restoration. Conditional conversion preserves whole records, uses the disclosed `(0,1,3),(1,2,3)` diagonal and primitive-ID consequences, and precedes adjacent native copies. Unknown mixed-state products are not granted provenance by a raw name.
4. **D-P10-24 / sky-star completeness:** `docs/phase10/v1/PHASE_10_DOC.md:913–1027` covers upper sky, lower sky and stars, off-first observation, one canonical source, distinct original/derived products and actual VBO sealing-before-reset. I independently traced the [RenderGlobal source](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/RenderGlobal.java): three generation methods, four list sites and three VBO sites are real. The final lower-list site is distinct and remains outside the VBO conditional. The known absent sentinel is not promoted into a native product. The seventeen exact rows at owner `1395–1424` comprise three capture, seven playback and seven lifetime rows. Resource reload and the empty vanilla delete-all method are correctly specified as explicit P10 observers, not fictional vanilla rebuild/deletion behavior. P7 D-P7-53 at `1769–1778`, and its §5.3 drain incorporation at `3667–3673`, receive those obligations.
5. **D-P10-26 / deferred FastTESR:** owner `1042–1069,1426–1443,1475–1479,1605–1608` receives opaque per-quad state ownership, all seven append/state subtargets, the actual destination-to-source sort permutation, maximal contiguous ranges, complete partition validation, and four-versus-six-vertex range arithmetic. P9 `776–840,1046–1100` and P7 `1781–1796` agree that fresh ID scopes enclose the actual range’s adjacent native copies, with one original batch sort and one Forge setup/cleanup lifetime. No retained enqueue token, per-owner resort, whole-uploader replay, alias-valued vertex substitute or unsplit primitive-ID parity is assumed. The current Cleanroom dispatcher and BufferBuilder patches, together with the vanilla sort body, corroborate the delayed flush and permutation acquisition points.
6. **D-P10-25 / schema:** owner `1572–1591` receives exact-current containing/nested schema equality before derivation or reuse. P3’s current authority at `docs/phase3/v1/PHASE_3_DOC.md:4040–4045,4084–4099` and P7’s receipt at `docs/phase7/v1/PHASE_7_DOC.md:3468–3473` agree on schema22. P10 neither selects BLOCK alternate rules nor acquires binary assets. Historical numeric receipts do not create additional admission gates.
7. **Remaining document gate:** lighting/AO precedence and bake invalidation are explicit local policy rather than unearned numerical parity. Both OQ-5 and OQ-14 have questions, procedures, success/failure criteria and safe fallbacks; OQ-14 accounts for LightUtil’s static default mapping as well as its map and retained BakedQuad formats. Growth remains named-descriptor-based and unwired. Failure, threading, testing, milestone and implementation sections are substantive and keep implementation/conformance gates separate.

## Required correction

### C1 — Preserve the source writer sequence when installing CLASSIC_56

**Severity:** correction, high impact. **Owner locations:** `docs/phase10/v1/PHASE_10_DOC.md:365–371,395–425,1298–1301`; the newly specified canonical ModelRenderer staging path at `844–852` consumes this ingress contract. §5 incorporates it at `1467–1469`.

**Defect:** the document installs a physical projection with ordinary position/color/UV/lightmap/normal elements and describes incremental handling only after vanilla `endVertex`. It specifies semantic copying for supplied source records, but does not specify how the existing incremental setters retain their source-format traversal when the physical element sequence gains or reorders elements. The hook ledger likewise has begin/end/bulk hooks, not a semantic writer/cursor adaptation boundary.

**Independent source proof:** [BufferBuilder](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/BufferBuilder.java) uses `vertexFormatIndex` and the selected element’s type/offset in `pos`, `color`, `tex`, `lightmap` and `normal`. Each setter advances through `nextVertexFormatIndex`, which skips only PADDING; `endVertex` increments the count and grows storage but does not restore the element cursor. The current permitted `reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/BufferBuilder.java.patch:1–72` does not supply the missing semantic dispatch.

There are two concrete counterexamples, not hypothetical custom formats:

- [BlockFluidRenderer](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/BlockFluidRenderer.java) emits `pos→color→tex→lightmap→endVertex`. With an ordinary NORMAL element appended after lightmap, the next vertex’s `pos` addresses NORMAL rather than POSITION. An AFTER endVertex identity stamp cannot correct that subsequent write.
- The explicitly admitted [TexturedQuad.draw](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/model/TexturedQuad.java) emits `pos→tex→normal` using OLDMODEL_POSITION_TEX_NORMAL. Projecting that stream directly to the canonical physical `position→color→UV→lightmap→normal` order sends its first `tex` write into COLOR, before any endVertex hook runs. Its Cleanroom patch only changes side annotations, not this writer chain.

**Observable consequence:** at v0.3, supported fluid geometry and the newly authenticated model capture can contain corrupted positions/UVs/normals before quad calculation and sealing. Safe final validation can at best reject otherwise supported vanilla products; it cannot reconstruct their lost inputs. The source-memory, topology and playback guarantees therefore do not yet establish correct captured geometry.

**Minimal owner fix:** specify a source-format-aware incremental write contract independent of the physical CLASSIC_56 element cursor. Define the exact adaptation for existing setter chains, omitted semantics, end-of-vertex reset, and transitions between incremental and bulk ingress, preserving callers and shaders-off behavior. Add the selected concrete setter/advancement interception targets and exception/reset coverage to §4.11, incorporate the amended ingress/health obligation in §5, and extend the existing T10-INGRESS design with the real fluid and OLDMODEL call sequences. A reset-only fix is insufficient for the reordered OLDMODEL fields. P7 must subsequently receive the amended owner10 health set; that is a cross-owner integration obligation, not a finding attributed to P7 here.

## Notes and limitations

### N1 — Available corroboration is not the historical pinned source snapshot

The exact `reference-src/cleanroom-0.6.6-alpha/` and `reference-src/pintonium-9c2fcc1/` trees cited by the owner’s historical reading record were not available in this working tree. Available permitted corroboration was separately identified as `reference-src/Cleanroom-0.6.12-alpha/` and `reference-src/Pintonium-main/`. The latter corroborates diagonal-normal math, T×N tangent handedness, quarter-average midpoint processing, the actual `celeritas` mod ID and renderer replacement anchors; it is not certified byte-identical to 9c2fcc1. Likewise the current Cleanroom patches and vanilla mirror are not proof of the configured transformed runtime. This evidence distinction is a note, not a demand to rerun future implementation gates or rewrite historical authority.

No builds, tests, linters, formatters, runtime launches, pack-tier runs or native GL experiments were executed. The concrete ingress counterexamples are source-derived control/data-flow traces. OQ outcomes, transformed hook counts, driver capture/restoration, configured-loader compatibility and pack parity remain implementation evidence obligations. No forbidden chatlogs/transcripts, Oculus transformer boundaries, libraries or glsl-transformer implementation were read.

## Final verdict

**PASS-WITH-CORRECTIONS** — one required owner correction, one note; **§5 impact: yes**. Apply C1 in a separate owner fix-up and reconcile its exact health receiver before fresh applicable review. The D-P10-24…26 sky/schema/deferred-range receipts otherwise survive this independent architecture review. This report grants neither implementation clearance nor final integration clearance.

## Resolutions

### C1 — Owner correction recorded 2026-09-08; fresh verification pending

`docs/phase10/v1/PHASE_10_DOC.md` D-P10-27 amends §§4.2/4.11/5,
T10-INGRESS and §11: authenticated source-semantic dispatch selects the physical
destination **before** each scalar setter, while wrapped advancement traverses only
the retained source descriptor. BLOCK fluid `pos→color→tex→lightmap` and OLDMODEL
`pos→tex→normal` remain unchanged callers; destination-only/omitted semantics are
initialized once, not copied from the preceding vertex. Successful endVertex resets
per-vertex state; scalar/bulk transitions require a completed vertex, and failed
write/growth/stamp/advance invalidates the entire product before seal/capture/upload.
Float-color delegation writes/advances only once, noColor retains no-write/no-advance,
and inactive builders execute vanilla bodies without extended sidecar/TLS work.
This is not a reset-only repair or a second layout/source-authentication policy.

R10-8 publishes seven exact CORE ownerPhase=10 method-wrapper rows, expected=1 each:
H10-WRITER-POS, H10-WRITER-COLOR-INT, H10-WRITER-COLOR-FLOAT, H10-WRITER-TEX,
H10-WRITER-LIGHTMAP, H10-WRITER-NORMAL and H10-WRITER-ADVANCE. Existing
begin/end/array/bulk/reset/seal/state coverage additionally includes pending-vertex
and exceptional-exit cleanup. Main must receive these obligations into P7's full
v0.3/v0.5 sorted unique owner10 catalogue/fingerprint and forward identical evidence
to P2, retaining the v0.1 base subset and every prior required row.

D-P10-28 separately receives exact-current schema23 containing/nested/inspection
equality and MaterializedSource-v23 through P7; prior numeric receipts are historical.
Nine metadata-only trees/projectionVersion=1 and owner-held binary assets are unchanged;
P9 alone resolves registries. No selector parser or new vertex/lighting policy is added.

Evidence used for the correction is the complete owner/R2, selected v3 design and
RESEARCH Appendix C/E requirements, C1's separately identified vanilla BufferBuilder
mirror, and newly resolved MCP stable_39 setter/advancement mappings. N1's distinction
from unavailable historical pinned snapshots is retained. Source/mapping reads are not
transformed-cardinality or runtime proof. No validation commands, builds, tests,
formatters, linters or runtime launches ran. This appended owner resolution does not
alter the frozen review body/verdict, certify P7 receipt, claim fresh review/PASS,
resolve OQ-5/OQ-14 or grant implementation/integration clearance.
