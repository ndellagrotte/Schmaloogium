# Phase 13 Architecture Review 6 — Frozen Attempt 6

## Reviewed owner and disposition

- **Owner:** `docs/phase13/v1/PHASE_13_DOC.md`, complete current document, §§0–12.
- **Frozen owner SHA-256:** `7f6594ad15e6bd0fda767a3a8f6cce57229433c33a5e4c3d8f42dc937f9f41c2`, as recorded for Phase 13 / review round 6 in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_6.json`.
- **Result:** **PASS-WITH-CORRECTIONS** — one substantive owner correction, one non-blocking source-confidence note.
- **§5 impact:** **Yes.** The required correction changes the exact hook target/lifetime incorporated by the owner’s producer and health contracts; reciprocal target/count/fingerprint evidence must follow that change.

This is a fresh whole-document architecture review, not acceptance of the preceding report, certification of siblings, or implementation clearance. I inspected the owner patch and independently traced its current producer contracts into the load-bearing receivers. No repository file was modified.

## Scope and governing authority

The selected authority is the owner header’s `docs/design/v3/DESIGN.md`, not a globally newer design: Part I G0–G12 and the Phase 13 specification at lines 2436–2510. Governing research was checked in `docs/research/v1/RESEARCH.md`, including texture behavior, format and fixed-unit rules, relevant Pintonium findings, hook ownership, and Appendix F.5. I consulted `docs/MOVES.md` for moved references and read the applicable portions of `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`.

Dependency review included the current §5 contracts of `docs/phase3/v1/PHASE_3_DOC.md`, `docs/phase5/v1/PHASE_5_DOC.md`, and `docs/phase7/v1/PHASE_7_DOC.md`, including incorporated source-acquisition and binding definitions. Load-bearing reciprocal checks covered the synchronous device grant in `docs/phase1/v14/PHASE_1_DOC.md`, selector/binding consumers in `docs/phase4/v1/PHASE_4_DOC.md`, atlas uniform delivery in `docs/phase6/v1/PHASE_6_DOC.md`, the shadow binding receiver in `docs/phase8/v1/PHASE_8_DOC.md`, the sampler/object-state contract in `docs/phase14/v1/PHASE_14_DOC.md`, and the owner13 health intake in `docs/phase2/v2/PHASE_2_DOC.md`.

The explicit authority corrections in `docs/decisions/U1_TEXTURE_SAMPLING.md` and `docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md` were reviewed separately from historical research and design language. Earlier schema receipts and superseded U1 statements were not treated as active contradictions.

## Independent checks

1. **Typed source acquisition and current schema.** P13’s D-P13-30/37 acquisition and schema23 receipt were traced to P3’s same-load immutable asset capability and current nested identities. Primary image and adjacent sidecar outcomes remain from one snapshot; P13 does not reopen the selected archive, reinterpret properties keys, or repair older schemas. Resource-only NONE retains the exact configuration/capability. The current receipt at `docs/phase13/v1/PHASE_13_DOC.md:1580–1621` supersedes earlier numeric schema history rather than creating concurrent accepted schemas.
2. **Required synchronous values and native argument conversion.** The source matrix at `docs/phase13/v1/PHASE_13_DOC.md:475–495,996–1062` was compared with `docs/phase1/v14/PHASE_1_DOC.md:4376–4495` and the format/grant contracts in `docs/phase5/v1/PHASE_5_DOC.md:1320–1500,2590–2675`. Raw target dimensions and declared format/type survive conversion; decoded images and companions have explicit RGBA8 transfer semantics; generated noise retains its RGB8 path; atlas mip level count is the captured highest mip index plus one. Foreign resources do not receive owned allocation, upload, parameter mutation, mip generation, or deletion. The synchronous borrowed-buffer lifetime does not imply an async/PBO grant.
3. **Published texture mechanism and parameter legality.** The repository’s shipped OptiFine author documentation and the [published custom-texture/.mcmeta reference](https://shaders.properties/current/reference/buffers/custom_textures/#mcmeta-file) corroborate numeric duplicate discriminators, sidecars, and the documented raw targets. The maintainer decision—not those publications—supplies malformed/unreadable recovery and noise-specific baseline policy. P13 preserves this distinction. Rectangle repeat/mipmap rejection agrees with the [OpenGL texture-parameter errors](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glTexParameter.xhtml). No extra property-key filter syntax or optional API grant was inferred.
4. **Selection, publication, and consumption.** P13 publication/lease values were followed through P4 selector identity, P5 preflight and ordered fixed-unit binding, P4 activation, and P6 sampler delivery, including the P7/P8 routing sites. Non-Bound outcomes do not silently become usable bindings or inherit old-content authority. Resource rebuild admission, retirement, compensation, and current-epoch publication remain distinct from application-health evidence.
5. **Sampler optimization versus ordinary object state.** D-P13-38 and §5.5 were checked against the current P1/P14 grants and P4/P7 consumers. A successful owned parameter setter maintains the complete latest object baseline as well as any sampler cache, so ordinary sampler-zero clearing cannot expose an earlier successful baseline. Demotion replay is a separate obligation; borrowed textures remain non-mutating. This is not permission to execute currently ungranted sampler/async/staging APIs.
6. **Atlas values and animation.** Actual allocation dimensions—not packed sprite extrema—feed the proposed descriptor. MCP independently confirms `TextureUtil.allocateTextureImpl/func_180600_a(IIII)V`, the relevant TextureMap methods/fields, and the sprite animation metadata/counters. P13’s post-vanilla immutable snapshot, no independent animation clock, stale-snapshot rejection, and frame-zero fallback were examined. `atlasSize` remains contingent on authenticated current binding, not merely a completed stitch; the P7/P6/P8 receivers preserve that distinction. The exceptional stitch lifetime itself has correction C1 below.
7. **Application-health receipt.** `docs/phase13/v1/PHASE_13_DOC.md:1507–1515` agrees with `docs/phase7/v1/PHASE_7_DOC.md:1826–1847` and `docs/phase2/v2/PHASE_2_DOC.md:1371–1378`: eight ordered FEATURE entries, seven active and one dormant, event subscriptions versus complete member bundles versus actual injection/wrapper applications, exact canonical fingerprint/disposition semantics, and runtime acceptance outside frozen counts. A corrected wrapper target must preserve that separation and update the target-bound evidence.
8. **Remaining whole-owner obligations.** The review also covered ownership/module seams, defaults and their stated confidence, deterministic noise rules, companion layout/mips, demand and macro ordering, memory posture, diagnostics, reload/shaders-off containment, planned tests, milestones, decision ledger, and follow-through. Optional R4 and P14 extensions remain ungranted; the architecture does not substitute them for its mandatory synchronous baseline. The full classic matrix/T3 milestone remains an implementation gate rather than fabricated present evidence.

## Required correction

### C1 — Enclose Stitch Pre in the exception-safe attempt lifetime

**Severity:** P2 / substantive contract defect. **Owner anchors:** `docs/phase13/v1/PHASE_13_DOC.md:1361–1366,1388–1417`; **interface incorporation:** lines 1507–1519.

H13-ATLAS-06 wraps `TextureMap.loadTextureAtlas/func_110571_b(IResourceManager)V` and is specified to enter and release the private map-load scope. H13-ATLAS-01 nevertheless requires Stitch Pre to start a token in that scope, and D-P13-35 promises invalidation on any stitch/listener failure. The available permitted primary source places Pre outside that method: `reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/texture/TextureMap.java.patch:20–27` emits `ForgeHooksClient.onTextureStitchedPre(this)` in the outer `loadSprites(IResourceManager,ITextureMapPopulator)`, before `iconCreatorIn.registerSprites(this)`, missing-image initialization, and the subsequent inner atlas load. The [official Forge 1.12.x TextureMap patch](https://raw.githubusercontent.com/MinecraftForge/MinecraftForge/1.12.x/patches/minecraft/net/minecraft/client/renderer/texture/TextureMap.java.patch) independently corroborates this placement. MCP identifies that outer method as `func_174943_a(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/renderer/texture/ITextureMapPopulator;)V`.

Consequently the declared scope does not yet exist when the normal Pre event must mint its scope-local token. If glue instead creates pending state before wrapper entry, a later Pre listener or sprite-populator exception can occur before H13-ATLAS-06 is entered, so its finally cannot perform the promised cleanup. The current text specifies neither an authenticated outside-to-inside handoff nor an outer exceptional boundary covering that state. This is a mismatch between the selected hook and the owner’s required state transitions, not a demand for future runtime proof. A strict implementation loses normal token association; a permissive interpretation leaves the stated pre-entry failure cleanup unspecified.

**Minimal owner fix:** define the bounded try/finally attempt lifetime around the real Pre-owning outer `loadSprites` invocation through allocation and Post, or explicitly define an equally exception-safe outer Pre-token lifetime and authenticated handoff to the narrower allocation scope. Preserve the one-token bound, same-map/native-object/resource-epoch checks, Post-only acceptance, poisoning of conflicting nested work, and fail-closed behavior for inner loads lacking the required Pre. Add explicit planned cases for a Pre listener and sprite populator throwing before inner wrapper entry; a HEAD/RETURN pair is not sufficient.

**Cross-owner coordination, not sibling findings:** `docs/phase7/v1/PHASE_7_DOC.md:1843–1850` currently repeats the inner `func_110571_b` scope, and its fingerprint domain fixes catalogue targets/roles at lines 1839–1842. The corrected P13 target/lifetime therefore needs corresponding P7 target-bound evidence and the P2 boundary-scenario receipt at `docs/phase2/v2/PHASE_2_DOC.md:1371–1378`. P13 §5 must record the corrected interface meaning. This report attributes the originating correction only to P13 and does not certify those receivers.

## Non-blocking note

### N1 — Preserve the explicit historical-pin qualification

`docs/phase13/v1/PHASE_13_DOC.md:225–238,1380–1387` correctly distinguishes the available Cleanroom 0.6.12-alpha allocation/Post patch from the absent historical Cleanroom pin and from runtime evidence. The historical Pintonium statement similarly does not authorize arbitrary available `Pintonium-main` reuse. I independently confirmed that the available directory names differ from the historical pins; the available primary patch and official Forge patch provide permitted corroboration, not reconstructed historical evidence. Retain these qualifications when fixing C1. No additional owner correction or historical-source recovery is required by this note.

## Limitations and final disposition

This was a read-only architecture/source investigation. No code, native execution, actual hook application, pack rendering, T0–T3 result, sampler optimization, or sibling certification is claimed. No builds, tests, formatters, or project-wide validation were run. No forbidden chatlogs, root transcripts, Oculus pipeline/transform implementation, relocated GLSL implementation, or glsl-transformer implementation were used. OptiFine evidence was restricted to permitted behavioral digest material and shipped author documentation. Mapping/API information was treated as mapping/API evidence rather than runtime observation; explicit assumptions such as the retained normal-default convention were not silently promoted to observed behavior.

**Final verdict: PASS-WITH-CORRECTIONS.** C1 must be resolved in the owner and its target-bound §5 receipts before this frozen Phase 13 architecture gate can receive literal PASS. The defect is localized and does not justify structural FAIL. Fresh review of changed owner/receiver contracts and final BUILD integration remain separate responsibilities.

## Resolutions

### C1 — Resolved in architecture; fresh owner/receiver review required

2026-09-08, D-P13-40 updates the live owner §4.6 catalogue and attempt algorithm,
incorporated §5.1, §8.2 scenarios, §11 decision ledger and §12 item10. H13-ATLAS-06
now wraps outer `TextureMap.loadSprites` at the complete SRG target
`func_174943_a(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/renderer/texture/ITextureMapPopulator;)V`.
Entry precedes the original body/Pre; a true try/finally encloses Pre listeners,
populator, missing-image/native-object replacement, inner allocation and Post listeners.
Later Pre-listener/populator exceptions before inner entry therefore clear all attempt
state, as do failures after Post. Normal outer completion retains only Post-accepted data
and releases scope/token. One-token bound, matching map/native object/resource epoch,
single matching allocation, Post-only acceptance and nested poisoning remain mandatory;
unscoped/direct inner paths never manufacture token/extent authority.

Eight IDs remain (seven active, one dormant). The changed target advances the canonical
domain to `TextureHookHealth/application-v2`; framing/count meanings remain unchanged,
old-domain evidence is rejected and runtime acceptance never mutates frozen application
counts. P7/P2 target/domain and scenario receivers are Main-owned coordinated edits;
this resolution does not certify their bytes. Planned owner cases explicitly cover both
pre-inner exceptions, normal outer success, direct inner/unscoped work, nesting and stale
application-domain evidence. These are plans, not executed results.

The same coordinated edit records D-P13-41 at §§4.3.3/5.2/8.2/11/12: P1 D-P1-66's
actual native-captured/replayed target maxima feed pure per-axis preflight. 1D/2D use
maxTextureSize, 3D uses max3DTextureSize and RECT uses maxRectangleTextureSize; support
gates, positive-or-unsupported-zero semantics and required exact decimal replay keys are
incorporated without guessed limits, engine GL queries or an optional API grant.

### N1 — Historical-pin qualification retained

The available Cleanroom 0.6.12-alpha patch corroborates the Pre-owning outer sequence;
it is not the absent historical pin or runtime proof. Existing Pintonium and allocation
confidence qualifications remain intact. Frozen report body/verdict above is preserved.
No builds, tests, formatters, linters, checksum/structure checks or other validation ran.
§5 changed; no fresh PASS, implementation clearance or sibling certification is claimed.
