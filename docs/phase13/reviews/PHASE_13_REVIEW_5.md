# Phase 13 — Fresh Whole-Document Architecture Review R5

## Frozen owner and disposition

- **Owner:** `docs/phase13/v1/PHASE_13_DOC.md`, sections 0–12, all 2,105 lines.
- **Frozen SHA256:** `7eb214b53502055f155fb80e1cbaef856e937581443f8da808bbb01a75591fd0`, as recorded for Phase 13 / review round 5 in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json:65-69` and supplied by the assignment. This identifies the commissioned artifact; I did not run a separate checksum command.
- **Required corrections:** 2. **Notes:** 1. **Structural blockers requiring rebuild:** 0.
- **§5 impact:** yes. Both corrections affect declarations or hook semantics incorporated into the binding interface.

This was an independent architecture review, not an implementation review or certification of another phase. No repository file was edited and no build, test, formatter, linter, runtime, or pack-tier validation was run.

## Scope and authority

I read the complete owner, its selected governing `docs/design/v3/DESIGN.md` Part I G0–G12 and Phase 13 specification (`:2436-2510`), and the governing research portions: §§0–1, texture-system §4.6, milestone §9, Apps B.3/B.4/D.3/E/F.3/F.5. I read `docs/MOVES.md` and repository source restrictions rather than selecting the globally newest design or assuming historical paths still exist.

The review also read the declared dependencies' complete §5 contracts in `docs/phase3/v1/PHASE_3_DOC.md`, `docs/phase5/v1/PHASE_5_DOC.md`, and `docs/phase7/v1/PHASE_7_DOC.md`, plus relevant incorporated acquisition/binding contracts. Reciprocal P1/P4/P6/P8/P14 interfaces were read for package/facade admission, selection, atlas uniform delivery, shadow lease consumption, complete parameter replay, and optional async ownership. Those reads establish evidence for this owner's consumption only; they do not issue sibling verdicts.

`docs/decisions/U1_TEXTURE_SAMPLING.md` and `docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md` supply the separate, narrow maintainer authorities. The U1 decision supersedes the unspecified property-key sampling-suffix requirement, not sidecar defaults or runtime parity. PD §§7.4/7.6/11 and relevant §§17–18 rows were treated as evidence, not governing contract. OptiFine implementation evidence was limited to the permitted texture behavioral digest and shipped author documentation; no OptiFine implementation source or forbidden Oculus/transformer material was mined.

## Independent checks

1. **D-P13-32/current schema:** `docs/phase13/v1/PHASE_13_DOC.md:1456-1461` receives exact containing/nested/inspection schema22 and MaterializedSource-v22 before derivation, acquisition, retention, or reuse. This agrees with `docs/phase3/v1/PHASE_3_DOC.md:4042-4046,4085-4096`. The decision row at owner `:1985` makes prior numeric receipts historical. I did not count superseded schema21 history as a required correction.
2. **Snapshot acquisition and retention:** owner `:894-944` was compared with P3 `:855-875,1866-1971` and P7 `:3489-3516`. The same-load capability, declared-path domain, immutable heap cursor, post-archive lifetime, primary-source failure versus optional-sidecar-only Unreadable, and exact configuration retention through resource NONE agree. Metadata-only inspection is not accepted as a byte-acquisition capability.
3. **Sidecars and legality:** owner `:1040-1086,1180-1204` retains distinct PNG/raw/noise baselines, omitted versus explicit false, bounded atomic parse/recovery, deterministic outcome identity, and pre-allocation integer/rectangle rejection. These match the separately approved decision and the current [Iris author documentation](https://shaders.properties/current/reference/buffers/custom_textures/#mcmeta-file) for its limited image/raw default claims. Recovery and noise defaults remain local policy rather than measured G6 behavior.
4. **Candidate dispatch and lifetime:** owner candidate/lease declarations `:486-524,1407-1433` agree with P5 `:998-1031`; producer expansion and ordinal rules reach the actual P5 selection loop at `:2342-2394`. P5 physical preflight/bind/transfer at `:2403-2452`, P7's corresponding §5 receiver, and P8 `:1620-1700` preserve full-shape compatibility, same-selector activation, Bound-only transfer, and exactly-once closure. Equal fingerprints do not revive retiring leases.
5. **D-P13-33/demotion:** owner `:1683-1705` adopts P1's complete synchronous state mapping and authenticated latest-state replay, rather than substituting sampler zero for object state. This matches P1 `:4274-4358,5148` and P14 `:720-732,1831-1833`: failed or unproven replay contains/rebuilds; borrowed textures remain untouched.
6. **D-P13-34/async:** owner `:1706-1712` receives fence → producer-context flush → publication and acknowledged cleanup/quarantine requirements. P14 `:1112-1115,1759-1766` and P7 `:3839-3845` retain the same condition. These are requirements on a future grant, not a current worker/staging API. Synchronous upload remains the baseline.
7. **Companions, noise, hooks, and atlas uniform:** research-to-conformance rows, independent preliminary macro preferences, the signed recurrence, frame0 recovery, resource-epoch invalidation, and authenticated current-bind → P13 size → P6 upload were traced. MCP independently confirmed the named TextureMap and TextureAtlasSprite SRG members and TextureStitchEvent's actual API. The two corrections below arise from the remaining producer-to-platform gaps, not from demanding future runtime evidence at this gate.

## Required corrections

### C1 — Capture the actual stitched atlas extent before constructing AtlasDescriptor

**Severity:** correction; high architectural impact. **Owner locations:** `docs/phase13/v1/PHASE_13_DOC.md:1285-1289`, with the required descriptor at `:369-370`, exact-layout obligation at `:680-686`, and binding incorporation at `:1401-1404`.

H13-ATLAS-02 promises to construct the final descriptor, including exact atlas width and height, from TextureStitchEvent.Post; H13-ATLAS-03 adds only the uploaded-sprite map, animation list, and mip count. None supplies the atlas extent. Fresh MCP `get_class_details(TextureMap, 1.12.2)` exposes no retained width/height field or extent getter, while `get_api_class(TextureStitchEvent)` exposes only `getMap()`. The available permitted corroborating patch, `reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/texture/TextureMap.java.patch:144-171,187-189`, obtains dimensions from the local Stitcher when allocating, then sends Post with only the map. The patch is not claimed to be the unavailable historical pinned checkout.

**Observable breakage:** the designed hook inputs cannot populate the exact positive dimensions that both companion allocation and atlasSize require. A successful stitch can therefore reach the publication boundary without the required extent; keeping Unknown loses the required feature, while inventing dimensions risks mismatched companion UV layout and incorrect uniforms. P7/P6's downstream atlas delivery is present, so a receiver does not repair this missing producer input.

**Minimal owner fix:** add an explicit, bounded, read-only observation that captures the actual final atlas width/height at the allocation/stitch-initialization boundary and associates them with the current atlas/resource epoch. MCP confirms `TextureAtlasSprite.initSprite/func_110971_a(IIIIZ)V` as one possible observation surface; the owner must choose and catalog the actual route rather than silently relying on nonexistent TextureMap getters or a new GL query. Retain Post as the acceptance moment, invalidate the captured extent on Pre/reload/failure, and require matching captured dimensions before Known/publication. Update the incorporated §5 hook row and the exact affected receiver health handoff.

### C2 — Close the target-bearing synchronous allocation/upload handoff

**Severity:** correction; high architectural impact for required raw source forms. **Owner locations:** `docs/phase13/v1/PHASE_13_DOC.md:955-971`, `:421-431`, and §5 source-preparation/custom-plan rows at `:1392-1394`.

The owner publishes a new `TextureUploadSpec` sum with OneD/TwoD/ThreeD/Rectangle variants and asserts that the rectangle path reaches existing owned upload. However, the actual facade boundary is `TextureService.allocate(TextureHandle, TextureSpec)` followed by `upload(TextureHandle, TextureData)` (`docs/phase1/v14/PHASE_1_DOC.md:3301-3311`). Its referenced format owner closes TextureSpec as ColorTextureSpec or DepthTextureSpec, with format/transfer/extent/mips but no target discriminator (`docs/phase5/v1/PHASE_5_DOC.md:1396-1404`). Neither receiving contract declares that P13's upload sum is an accepted allocation spec or specifies its adaptation. In particular, 2D and rectangle sources can have identical dimensions and formats but require different native targets; later sampler compatibility cannot recover a target omitted at allocation. The same boundary also publishes `ReadyAsset.format` as `InternalTextureFormat` at owner `:424`, although P3's exact raw field is `ColorInternalFormat` (`docs/phase3/v1/PHASE_3_DOC.md:899-929,3524-3542`) and the consumed P1/P5 contracts grant no project type named InternalTextureFormat.

**Observable breakage:** a legal TEXTURE_RECTANGLE or 3D raw declaration can be acquired and planned but has no exact target-bearing allocation input at the only permitted GL boundary. An implementer must invent a type/conversion or silently collapse targets, defeating required App F.5 support before P5's otherwise-correct candidate selection runs. This is a synchronous API/representation gap, unrelated to the correctly ungranted async optimization.

**Minimal owner fix:** specify the complete P13-to-facade allocation/upload conversion using the actual granted format vocabulary, including target, all dimensions, admitted mip extent, transfer layout and prepared payload. Replace the unbound InternalTextureFormat field with the appropriate exact granted representation, or explicitly request the necessary closed extension. Where P1/P5's current TextureSpec cannot represent a target, record the mandatory owner request in §5/§11 and obtain a reciprocal target-bearing grant before calling this path closed. Preserve distinct rectangle versus 2D dispatch, all raw formats, parameter legality, and existing ownership/failure behavior; do not enable direct GL or an optional async path as a workaround.

## Notes

### N1 — Keep available source corroboration separate from historical pinned evidence

The paths recorded in `docs/MOVES.md:49-55` for Pintonium 9c2fcc1 and Cleanroom 0.6.6 are absent in this checkout. Available `reference-src/Pintonium-main/` and `reference-src/Cleanroom-0.6.12-alpha/` are different evidence sets, not replacements that prove historical pins. Pintonium-main's `common-shaders/src/main/java/net/irisshaders/iris/targets/backed/NoiseTexture.java:24-68` corroborates Random(0), RGB and LINEAR/REPEAT; its `texture/pbr/PBRTextureManager.java:90-115` corroborates integer-keyed holder acquisition; `uniforms/CommonUniforms.java:42-48` corroborates the atlas notifier limitation. None establishes the absent pin's exact bytes or runtime behavior.

The available checkout's `COPYING:1-18` is GPLv3 text, so the owner's broad LGPL statement at `docs/phase13/v1/PHASE_13_DOC.md:89-92` should remain understood as historical governing/PD evidence, not a fresh licence clearance for arbitrary files in Pintonium-main. No implementation was copied in this review. Future reuse needs the applicable source-specific licence and notices. This note is not a sibling correction or a demand to rewrite preserved historical authority.

## Limitations and final disposition

The static interface traces and fresh MCP/allowed-source reads are the proof exercised here. No actual GL upload, atlas stitching, animation synchronization, sampler demotion, worker timeout, or shader-pack conformance was executed. C-TX01 remains the explicitly disclosed governing byte-order conflict, not a newly resolved parity claim. Historical reviews were not used as proof, and optional R4/sampler/async/staging APIs remain ungranted where their owners say so.

The document is structurally complete and the R5 focus receipts are coherent. C1 and C2 are discrete owner/interface corrections rather than grounds for a whole-phase rebuild. They require owner edits, reciprocal grants/adoption where necessary, and fresh review of changed §5 bytes. Implementation clearance and final integration remain separate.

**Final verdict: PASS-WITH-CORRECTIONS.**

## Resolutions

### C1 — Actual stitched atlas extent (2026-09-08; architecture amended, unverified)

P13 §4.6/D-P13-35 now selects bounded read-only TextureUtil.allocateTextureImpl
`func_180600_a(IIII)V` RETURN capture (H13-ATLAS-05), within an exception-safe
TextureMap.loadTextureAtlas scope (H13-ATLAS-06). MCP confirms allocation parameters;
the available Cleanroom-0.6.12-alpha TextureMap patch corroborates local Stitcher
dimensions passed to allocation before Post. This is not the missing historical pin.
One captured positive extent/mip tuple is authenticated to the map/stitch/resource epoch;
Post alone accepts it, and Pre/reload/failure/abnormal or no-Post scope exit invalidates
capture, metadata and size. No TextureMap extent getter, sprite-size inference or GL query.
§5 incorporates all eight ordered H13 rows, including seven active FEATURE rows and the
existing dormant sprite row; P7/P2 receiving edits remain Main's integration responsibility.
Planned padded-atlas, map/epoch mismatch, duplicate/missing allocation, reentry and
exceptional-exit cases are recorded; no atlas stitch/runtime scenario was executed.

### C2 — Target-bearing synchronous allocation/upload (2026-09-08; architecture amended, unverified)

P1 §4.7.7a/D-P1-63 and P5 §4.2/D-P5-42 reciprocally grant closed shared engine
TextureSpec/TextureData/TextureRegion/TextureExtent/PixelLayout/TextureAllocationTarget
values at the existing synchronous facade verbs. P13 §4.3.3/D-P13-36 explicitly converts
every raw 1D/2D/3D/RECT, decoded PNG/static resource/noise override, generated noise,
default fill and companion mip/animation case to those exact values; foreign live sources
remain borrow-only. ReadyAsset now uses the granted ColorInternalFormat rather than
unbound InternalTextureFormat. All37 raw formats and declared transfer layouts are retained;
P5's private RGBA_COMPAT and null-allocation defaults do not replace raw requests.
Dimensions, mip count versus index, regions, exact packed/scalar byte layout, stable
synchronous payload lifetime, target dispatch, borrowed restrictions and error/restoration
are explicit. P5 main/depth defaults and first-copy versus steady-copy semantics remain
separate from P13 upload; no parser, direct engine GL or optional async workaround.
§5/§11 reciprocal receipts and planned boundary cases are recorded.

### Shared receipts and remaining gates

P1/P5/P13 now receive exact containing/nested/inspection schema23 and MaterializedSource-v23,
with earlier numeric receipts historical and nine metadata-only trees/projectionVersion1
unchanged. Their P14 D-P14-31 receipts require complete latest authenticated owned-object
state on every successful setter alongside sampler cache, so ordinary all-unit clearing
cannot expose stale state; borrowed restrictions/failure containment remain.
P13 also receives D-P14-32's future readiness/completion fencing and acknowledged cleanup
conditions without granting async execution. R5 N1's available-source/historical-pin and
licensing distinctions are retained in P13 §0.12; no historical authority or report body
has been rewritten. No validation commands, builds, tests, formatter/linter or runtime
checks were run. Changed §5 requires fresh whole-document owner/receiver review; these
resolutions do not alter the original verdict or claim PASS, conformance or implementation
clearance. Main owns the remaining P7/P2/P6/depth-consumer/integration receiving records.
