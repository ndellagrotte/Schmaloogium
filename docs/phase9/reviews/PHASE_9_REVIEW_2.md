# Phase 9 — Fresh Whole-Document Architecture Review R2

## Frozen owner and disposition

- **Owner:** `docs/phase9/v1/PHASE_9_DOC.md`
- **Review round:** 2, architecture-review attempt 5.
- **Frozen SHA256:** `51b8c14f8641b2a993625006b49c22e71c0ebacb11b0467b870a148d9b382e4f`, as supplied by the assignment and recorded in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json:48–51`. This report does not claim a separately executed checksum validation.
- **Required corrections:** 0. **Notes:** 1. **New §5 correction impact:** none.
- **Disposition:** literal **PASS** for this owner's architecture. No owner bytes were edited. This is neither sibling certification nor implementation/final-integration clearance.

## Scope and authority

This was a fresh review of the complete owner, §§0–12, not merely the R1 correction passages. I read its selected governing revision, `docs/design/v2.0-RC3/DESIGN.md`, Part I §§G0–G12 and the Phase 9 assignment at lines 2035–2125. RC3—not the globally newest design—governs this owner. I read `docs/research/v1/RESEARCH.md` §§0–1, §3.7, §4.7, Appendix D.1/D.4, Appendix E's applicable rows, and the tag, vertex-payload and hand-policy context needed to resolve the contracts. I also read PD §8 and the permitted shipped pack-author mapping, predicate and hand-light documentation.

The dependency examination covered current Phase 3, 6 and 7 §5 contracts and their load-bearing incorporated declarations, plus Phase 10's reciprocal native-range and alias-service contracts. Current producer/receiver text was independently compared; historical PASS statements and previous-review resolution claims were not used as proof. `docs/MOVES.md` was consulted to distinguish historical paths and per-phase design adoption.

## Independent checks

### 1. Mapping forms, precedence and immutable publication

The complete contract map and detailed resolver retain short/namespaced names, live numeric IDs with metadata, conjunctive property predicates, invalid-predicate no-overmatch behavior, still/flowing aliases, explicit tag-shim ownership, all four layer variants and opaque-solid exclusion (`docs/phase9/v1/PHASE_9_DOC.md:361–397,463–645`). The pack-over-mod outer ordering, explicit-before-tag ordering within each contribution, deterministic mod ordering and fill-only winner rule are specified rather than delegated to unspecified map iteration. These agree with the assignment and the author documentation at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:535–604` and `doc/properties_files.txt:85–122`.

Absent pack BLOCK input alone enables the live-vanilla numeric fallback; a present-empty file does not. Full integer uniform identity is distinct from the deliberately bounded vertex representation. `Present`, `Absent` and `Unrepresentable` are explicitly consumed by Phase 10, retaining render type and metadata and using the specified low-word bit patterns (`docs/phase9/v1/PHASE_9_DOC.md:622–670`; `docs/phase10/v1/PHASE_10_DOC.md:325–360`). The layer receiver expressly applies each returned enum and preserves vanilla on absence, with generation-stamped invalidation (`docs/phase7/v1/PHASE_7_DOC.md:1184–1190`). No unmatched new result/enum branch was found at these receivers.

Candidate ownership, publication transfer, monotonically increasing generation, retirement and matched lookup/ordinal-map borrowing remain coherent. The owner does not reinterpret pure-builder failure as permission to resume an old coordinated pipeline. Phase 7's resource-NONE branch and full-rebuild transaction retain configuration identity, publish IDs after textures, invalidate dependent geometry before admission, and retain borrowed data until actual worker/source drain (`docs/phase9/v1/PHASE_9_DOC.md:400–433,1007–1025`; `docs/phase7/v1/PHASE_7_DOC.md:3162–3180,3661–3721`).

### 2. BLOCK alternate provenance is reachable and receiver-compatible

The former hypothetical modern provenance is replaced by a concrete current producer. Phase 3's `IdMappingInput`/`IdMappingFileInput` declarations and parser law publish ordinary and isolated forced-11300 results for present BLOCK and ENTITY requests, preserve ordinary-derived state, mark only alternate rules MODERN, and leave ITEM/LAYER alternates empty (`docs/phase3/v1/PHASE_3_DOC.md:1229–1245,2958–3032,3249`). Phase 9 receives precisely that schema22 contract before mod parsing, branch selection, derivation, reuse or reporting (`docs/phase9/v1/PHASE_9_DOC.md:918–943`).

The selection trace is closed: ABSENT contributes nothing; PRESENT_RULES retains ordinary rules even when registry matches are zero; PRESENT_EMPTY selects only a nonempty alternate; an empty/failed alternate contributes no rules. The same rule runs independently for each pack/mod contribution before precedence. Consequently a real `MC_VERSION >= 11300` BLOCK fixture can reach MODERN grass/lamp resolution, while an ordinary nonempty classic control cannot be overwritten. No selected-pack reopen, manual MODERN injection, era heuristic, alternate merge or older-schema repair is required (`docs/phase9/v1/PHASE_9_DOC.md:565–620,1158–1166`; producer fixture plan at `docs/phase3/v1/PHASE_3_DOC.md:4302–4332`). This is an architectural trace, not a claim that those planned fixtures have executed.

### 3. The TE hook reaches actual rendering

MCP independently resolved `func_180546_a` as `(Lnet/minecraft/tileentity/TileEntity;FI)V` and `func_192854_a` as `(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V`. The available Cleanroom RenderGlobal patch shows both normal/global TE calls and the enclosing batch boundaries; the [official Forge 1.12.x dispatcher patch](https://raw.githubusercontent.com/MinecraftForge/MinecraftForge/1.12.x/patches/minecraft/net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.java.patch) independently shows the world overload calling the lower dispatch and the lower dispatch choosing fast versus ordinary rendering.

The active P9 hook targets that lower dispatch, not Appendix E's historical convenience candidate. P7's matching wrapper surrounds the P9 ID scope, and both restore locally in finally; nested/direct/damaged/main/shadow cases preserve the original arguments and do not add duplicate outer-overload scopes (`docs/phase9/v1/PHASE_9_DOC.md:714–775,1052–1057`; `docs/phase7/v1/PHASE_7_DOC.md:1608–1610,1748–1749`).

### 4. Deferred FastTESR ownership survives to native submission

The available Forge/Cleanroom dispatcher and `FastTESR.java:34–67` corroborate the distinction between deferred registered fast renderers and FastTESR's immediate method. The owner correctly rejects forcing all fast rendering through the latter: doing so would not preserve the original deferred batch position or its full-batch translucent ordering.

P9 now specifies bounded per-quad state-ordinal ownership, nested-owner restoration, exact original sort-permutation application, complete partition preflight, contiguous—not globally regrouped—owner ranges, and fresh ID admission at the original native flush (`docs/phase9/v1/PHASE_9_DOC.md:777–843`). The [vanilla BufferBuilder mirror](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/BufferBuilder.java) corroborates the sorted destination-to-source index array before record movement; a second distance comparator is unnecessary and expressly prohibited.

The ownership protocol includes every enumerated append/state path, including Forge `putBulkData(ByteBuffer)V`, rather than treating array append as complete bulk coverage (`docs/phase9/v1/PHASE_9_DOC.md:1091–1108`). The available Cleanroom `BufferBuilder.java.patch` independently exposes that Forge bulk path. The uploader patch establishes the native call between Forge preDraw and postDraw, matching the proposed replacement boundary.

Both reciprocal receivers are concrete: P10 retains opaque owner ordinals, observes the same permutation, validates original/converted vertex ranges and replaces only the authenticated batch's native submission; P7 opens fresh range admission, permits only Entered scopes, keeps the ID alive through adjacent copies and restores instance before ID (`docs/phase10/v1/PHASE_10_DOC.md:1042–1068,1425–1441,1475,1604`; `docs/phase7/v1/PHASE_7_DOC.md:1617–1619,1781–1795`). Seven independent ownership subrows plus sort/ranges are included in health. Partial failure stops remaining submissions without original-batch replay; source storage survives pointer restoration. The existing P7 countInstances contract remains v0.5 and single-owner, avoiding N-squared forwarding (`docs/phase7/v1/PHASE_7_DOC.md:1271–1319`).

The disclosed per-range `gl_PrimitiveIDIn` restart is an explicit boundary, not falsely claimed unsplit parity. It is already received by P7/P10 and is not reported here as an unintentional defect.

### 5. Held, entity and color dynamics

Held IDs remain full integers; static light remains separately bounded. The user→pack→local-true oldHandLight resolution and `(max(main,off),off)` tuple agree with the current P3 codec/projection and P6 sample contract (`docs/phase9/v1/PHASE_9_DOC.md:672–710`; `docs/phase3/v1/PHASE_3_DOC.md:3350–3410`; `docs/phase6/v1/PHASE_6_DOC.md:795–812`). Held values arrive before shader activation; P6 owns next-activation held refresh and immediate active ID/color uploads (`docs/phase6/v1/PHASE_6_DOC.md:859–865`). Optional dynamic-light interoperability remains optional and does not manufacture a dynamic-light implementation.

Entity/TE scope authentication separates main and shadow capabilities, restores preceding values on nesting and rejects stale generations before lookup. Color is the existing P7-owned v0.1 operand-capture producer, independent of P9 installation, not a second writer or post-hoc TexEnv query (`docs/phase9/v1/PHASE_9_DOC.md:721–758,845–872`; `docs/phase7/v1/PHASE_7_DOC.md:1615–1616,3397–3406`). Failure, threading, tests, milestones, decisions and implementation-checklist sections cover the subsystem without claiming future runtime evidence already exists.

## Required corrections

None. No substantive current owner contract defect was established by this review.

## Notes

### N1 — Preserve the distinction between historical pinned evidence and available corroboration

**Severity:** note; no required owner change. The historical roots `reference-src/pintonium-9c2fcc1` and `reference-src/cleanroom-0.6.6-alpha` named in the original header are not the available checkout roots. Current permitted roots include `reference-src/Pintonium-main` and `reference-src/Cleanroom-0.6.12-alpha`. I read the current IdMap/VintageBlockMaterialMapping files as corroboration only, and current Cleanroom registry, remap, dispatcher, builder, uploader and FastTESR evidence. I did not establish historical byte identity or silently upgrade those observations to pinned-source proof. P9 already distinguishes the old reading claims and replacement evidence at `docs/phase9/v1/PHASE_9_DOC.md:18–23,136–148`. Preserve that qualification in implementation evidence. This is not a cross-owner correction or a reason to demand runtime proof at the architecture gate.

## Limitations and final verdict

No builds, tests, formatters, linters, runtime/native experiments or pack-tier validation were executed. No repository edits or report-file writes were made. No forbidden transcript, Oculus transformation boundary, glsl-transformer implementation or OptiFine decompiled implementation was read. Source/API inspection establishes architectural feasibility and reciprocal contract coverage, not applied Mixin cardinality, renderer conformance, exact native state restoration or historical source identity. Those remain the explicitly planned implementation evidence.

**PASS** — Phase 9 review R2 has zero required corrections. Current §5 producer/receiver amendments are coherent for this owner's responsibilities; independent sibling reviews and final integration remain separate gates.
