# Phase 9 — Whole-owner architecture review R3

## Frozen artifact and disposition

- **Owner:** `docs/phase9/v1/PHASE_9_DOC.md`
- **Review:** Phase 9, round 3, architecture-review attempt 6.
- **Frozen SHA256:** `471d6547fc3a43a16062e2575833b72dc3e7325e482eaa7895d1d147c02e4b2e`.
- **Inventory:** `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_6.json`, Phase 9 entry. The hash is the supplied/inventory frozen identity; no checksum or validation command was run.
- **Required corrections:** 0. **Notes:** 1. **Required §5 changes:** none.
- **Disposition:** whole-owner architecture PASS only. No implementation, native execution, pack-tier, sibling, or final-integration certification is conveyed.

## Scope and authority

Independently reviewed the complete current Phase 9 owner, §§0–12, including scope, public shapes, conformance map, resolution and publication algorithms, per-draw/native lifetime, dependency receipts, failure handling, threading, testability, milestones, decisions, and implementation checklist. D-P9-22/23 were a focus, not the review boundary. Prior review verdicts were not used as proof of current correctness.

The selected authority is **`docs/design/v2.0-RC3/DESIGN.md`**, as the owner declares at lines 8–11, not the globally newer design. Read its Part I §§G0–G12 and Phase 9 assignment at lines 2035–2125. Governing research checks covered `docs/research/v1/RESEARCH.md:11–107`, `438–462`, `600–618`, `1277–1337`, `1369–1412`, and `1434–1445`. `docs/MOVES.md` was consulted for revision and moved-path interpretation.

Dependency review traced P3's actual typed parser/schema/identity publication, P6's event and retirement contracts, and P7's current frame/publication/admission receivers. Load-bearing reciprocal checks also covered P10's alias/native-range receiver, P8's shadow ID admission, and P12's hand-policy transport. Those reads establish this owner's interface consistency, not independent verdicts on those owners.

## Independent contract checks

### 1. Exact typed selector membership and finite work

`docs/phase9/v1/PHASE_9_DOC.md:520–550` consumes the exact P3 records and closed variants published at `docs/phase3/v1/PHASE_3_DOC.md:1294–1332`, with grammar and resolution obligations at `:3089–3167` and the binding §5 row at `:3353`.

The current contract distinguishes absent metadata from a present nonempty union; tests captured metadata against inclusive endpoints; preserves literal versus integer-interval variants; ORs alternatives while ANDing metadata and property predicates; and validates against actual finite live property domains. It explicitly rejects missing properties, unknown literal alternatives, disjoint intervals, and an empty combined match for the whole selector. A sparse domain may intersect a range without containing every intermediate integer. Neither endpoint-width expansion nor dropping a bad alternative is permitted.

The documented leaves `3,7,11,15`, reeds `age=0-3`, and combined metadata/property forms are supported by shipped primary syntax at `reference-src/schlorbium-HD_U_G6_pre1/doc/properties_files.txt:97–122`. Their producer-origin, sparse-domain, mixed-valid/invalid, pack/mod, ordinary/alternate, and layer cases are expressly included in the owner's architecture test plan at `docs/phase9/v1/PHASE_9_DOC.md:1230–1249`. These are unexecuted implementation obligations, not test-pass evidence.

### 2. Schema23, identity, and source-free cases

The current admission at `docs/phase9/v1/PHASE_9_DOC.md:930–954` agrees with `docs/phase3/v1/PHASE_3_DOC.md:4152–4205`: containing configuration, nested ID input, and any received inspection snapshot must all equal the current constant, presently 23, before derivation, reuse, or reporting. Old numeric receipts are expressly historical rather than alternate admission routes.

Typed variants, endpoints, ordering, provenance, source/selection identity, and live snapshot identity survive derivation (`docs/phase9/v1/PHASE_9_DOC.md:540–546`, `620–637`). This agrees with P3's canonical typed encoding at `docs/phase3/v1/PHASE_3_DOC.md:3224–3229`. P3's inspection contract at `:3987–4135` retains all nine metadata trees and `projectionVersion=1`; interval endpoints remain typed integral values, while selector/property/origin strings retain their existing hashed treatment. P9 neither turns inspection metadata into source authority nor acquires binary assets. MaterializedSource-v23 is carried opaquely, not reparsed or relabeled.

### 3. Names, aliases, precedence, tags, and fallback

The owner specifies short/namespaced and live numeric resolution, metadata/property filtering, deterministic pack-before-mod precedence, per-source entries-before-tags ordering, and first-writer assignment at `docs/phase9/v1/PHASE_9_DOC.md:469–550`. This covers the shipped mapping and layer contract at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt:535–604` without copying reference parser repairs or allowing unknown predicates to overmatch.

Fluid-pair expansion, the fixed modern-name seeds, and the ambiguous grass/lamp cases are explicit at `docs/phase9/v1/PHASE_9_DOC.md:552–591`. P3 produces real isolated MODERN BLOCK rules; selection at `:613–637` uses them only for PRESENT_EMPTY with a nonempty alternate. P3's producer at `docs/phase3/v1/PHASE_3_DOC.md:3010–3058` agrees. Ordinary nonempty rules are neither overridden nor retried merely because registry resolution finds nothing. ITEM/LAYER receive no invented alternate.

The tag shim at `docs/phase9/v1/PHASE_9_DOC.md:593–612` retains the research requirement without treating Pintonium's modern tag branch as proven 1.12.2 support. The absent-pack-only live vanilla numeric fallback at `:639–655` remains distinct from PRESENT_EMPTY, runs after mod rules, and does not advertise unstable mod numeric IDs. Layer resolution at `:657–668` preserves the solid-opaque exclusion and hands decisions to the receiving classifier rather than mutating rendering itself.

### 4. Publication, ordinal lifetime, and downstream dispatch

Candidate ownership and matched lookup/ordinal-map borrowing at `docs/phase9/v1/PHASE_9_DOC.md:409–445` agree with P7's canonical transaction at `docs/phase7/v1/PHASE_7_DOC.md:3733–3800` and P10's lifecycle at `docs/phase10/v1/PHASE_10_DOC.md:1583–1679`. Publication follows texture preparation, geometry invalidation precedes atomic admission, and coordinated failure goes off rather than reviving a locally untouched old publisher. Resource-only NONE preserves the actual configuration while rebuilding resource-derived inputs. Failed worker drain retains borrowed lookup/map storage and closes admission.

The alias service distinguishes explicit zero, absent, and unrepresentable values and defines the exact two-word vertex boundary (`docs/phase9/v1/PHASE_9_DOC.md:670–695`). P10 consumes that lookup plus its matched generation, not names or a second resolver (`docs/phase10/v1/PHASE_10_DOC.md:1660–1679`). P7's classifier at `docs/phase7/v1/PHASE_7_DOC.md:1188–1191` handles all four resolved layers and preserves vanilla on absence.

### 5. Held values, entity/TE scopes, and color

The hand-policy algebra and four-int tuple at `docs/phase9/v1/PHASE_9_DOC.md:697–741` match P6's actual receiving schema and old-mode mapping at `docs/phase6/v1/PHASE_6_DOC.md:795–821`; P12 preserves default/true/false transport at `docs/phase12/v1/PHASE_12_DOC.md:873–882`. The local true fallback is identified as a decision, not misrepresented as a shipped missing-value default. External dynamic-light suppression remains optional; no dynamic-light implementation or ungranted API is assumed.

The corrected TE lower-dispatch contract at `docs/phase9/v1/PHASE_9_DOC.md:743–799` is received by `docs/phase7/v1/PHASE_7_DOC.md:1618–1619`. Independently resolved MCP descriptors confirm `func_180546_a(TileEntity,FI)V` and `func_192854_a(TileEntity,DDDFIF)V`. The [official Forge dispatcher patch](https://raw.githubusercontent.com/MinecraftForge/MinecraftForge/1.12.x/patches/minecraft/net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.java.patch) confirms the world-overload-to-lower-dispatch path and fast-render branch. Main/shadow capabilities and nested restoration also agree with `docs/phase7/v1/PHASE_7_DOC.md:3459–3468` and `docs/phase8/v1/PHASE_8_DOC.md:1329–1341`.

Color remains one P7-owned v0.1 producer, not a second P9 writer. The exact operand capture at `docs/phase9/v1/PHASE_9_DOC.md:869–893` agrees with the recipient at `docs/phase7/v1/PHASE_7_DOC.md:1625–1654`. The separately identified [vanilla RenderLivingBase mirror](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/entity/RenderLivingBase.java) corroborates the flipped four-float buffer passed to GL_TEXTURE_ENV_COLOR; this is operand evidence, not evidence of applied hooks or runtime delivery.

### 6. Deferred sorted FastTESR native protocol

The entire protocol at `docs/phase9/v1/PHASE_9_DOC.md:801–867`, with concrete receiver obligations at `:1065–1120`, was traced through `docs/phase10/v1/PHASE_10_DOC.md:1115–1143`, `1531–1548`, and `1581`, and `docs/phase7/v1/PHASE_7_DOC.md:1627–1629`, `1792–1808`.

The receiving contracts explicitly cover seven append/state subrows including Forge bulk append, the actual sort-permutation point, and the final native range dispatch. They preserve nested complete-quad ownership, full-batch sorting once, maximal contiguous owner ranges, fresh authenticated IDs live across adjacent native copies, once-only Forge setup/cleanup, and source lifetime through restoration. Missing instrumentation rejects shader admission rather than silently dropping fast geometry. The per-range primitive-ID restart is disclosed, not misrepresented as unsplit parity.

Permitted primary corroboration includes `reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/RenderGlobal.java.patch:63–83`, the corresponding dispatcher patch, and `reference-src/Cleanroom-0.6.12-alpha/src/main/java/net/minecraftforge/client/model/animation/FastTESR.java:34–67`. The [vanilla BufferBuilder mirror](https://raw.githubusercontent.com/KealJones/mc-1.12.2-source_files/master/src/minecraft/net/minecraft/client/renderer/BufferBuilder.java) shows the sorted quad-index array before record movement, supporting observation of the actual permutation rather than a duplicate comparator.

## Required corrections

None. No substantive current Phase 9 contract defect was established. In particular, historical schema receipts, outstanding implementation evidence, and deliberately optional external compatibility do not constitute architecture-gate corrections.

## Notes

1. **Evidence limitation — historical source pins are not recovered (non-blocking).** The owner already distinguishes historical Cleanroom 0.6.6-alpha inputs from permitted later corroboration at `docs/phase9/v1/PHASE_9_DOC.md:134–150`. This checkout exposes `reference-src/Cleanroom-0.6.12-alpha` and `reference-src/Pintonium-main`, not the historical pinned directories. Available registry APIs, remap documentation, IdMap, VintageBlockMaterialMapping, vintage BlockEntry, and LegacyIdMap were read independently. They corroborate mechanisms such as ordered assignment, conditional entity parsing, false vintage tag classification, and curated fallback data, but do not prove byte identity with `pintonium-9c2fcc1` or Cleanroom 0.6.6-alpha. **Observable breakage:** none established. **Minimal owner fix:** none; retain the existing evidence qualification and obtain exact-pin hook/application evidence at implementation. This note is not a request to rewrite historical receipts or a verdict on another owner.

## Limitations and final disposition

No repository files were edited. No build, test, formatter, linter, validation command, runtime, native draw, or pack acquisition was executed. The architecture's future fixtures and conformance scenes remain unexecuted. No forbidden transcript, Oculus pipeline/transform, libs, glsl-relocated, or glsl-transformer implementation was read; no OptiFine decompile mining was performed. Imported debug-helper references in permitted Pintonium files were not followed.

The owner remains unchanged by this review. Current dependency/receiver adoption was checked as contract text only; fresh sibling reviews and final integration remain separate gates. No required §5 amendment follows from this review.

**Final verdict: PASS.**
