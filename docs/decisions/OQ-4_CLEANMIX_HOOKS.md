# OQ-4 — CleanMix divergences relevant to hot render-path injections

**Open question (verbatim, `docs/research/v1/RESEARCH.md:1010`):** "CleanMix divergences
relevant to hot render-path injections"

**Why it matters (RESEARCH):** Hook viability/perf. **Blocks:** §7.1, App E.
**Phase 7 stake:** the complete hook catalog (§4.10) must weave with the pinned loader's
Mixin fork without MixinExtras, without wrong ordinals, and with require=0/expect=1
posture on hot paths.
**Status: RESOLVED for Phase 7 v0.1 implementation (2026-09-09); application-count proof
on a live client is the recorded remaining leg.**

## What CleanMix actually is (checked out: `reference-src/CleanMix-main`)

CleanMix is **the Mixin framework fork itself** — "CleanMix (Cleanroom Mixin)", a fork of
Fabric Mixin targeting 1.12.2 Forge (MixinBooter) and Cleanroom natively — **not** a mod
with render-hook examples. Consequences for the spike:

- It provides no per-hook reference implementations to copy. The render-path reference
  evidence remains Pintonium (`reference-src/Pintonium-main`, readable) and the
  Cleanroom EntityRenderer patches; nothing is copied verbatim from either.
- The "divergences" that matter are framework capabilities/behaviors vs stock Sponge
  Mixin 0.8.7 and vs MixinExtras-equipped stacks.

## Evidence gathered (this session)

1. **Version/baseline** (`gradle.properties`): `buildVersion=0.7.2`,
   `upstreamMixinVersion=0.8.7`, `upstreamFabricVersion=0.17.3`, `asmVersion=9.8`,
   `modlauncherVersion=10.0.9`. Stock-0.8.7 semantics (require/expect/ordinal/shift) are
   the baseline; the fork is a superset.
2. **Java-25 class files** (`src/main/java/org/spongepowered/asm/util/JavaVersion.java`):
   explicit `JAVA_25 = 25.0` constant alongside the full ladder through 25. The IR-28
   reconciliation is therefore: the config's declared `compatibilityLevel: JAVA_8` (the
   frozen v0.1 P1 shape) stays, while mixin classes compiled by the Java-25 toolchain are
   within the fork's supported class-file range. **Refmap leg:** CleanMix ships the
   annotation-processor module with MCP/FG3 mapping services
   (`src/ap/java/.../obfuscation/{mcp,fg3}`), so Unimined-generated SRG refmaps are the
   supported route; the first built-jar refmap inspection (IR-28) is recorded when the
   live build produces it.
3. **No MixinExtras** (`grep MixinExtras|llamalad7` over the checkout: zero hits).
   Confirmed on the checkout — `@WrapMethod`, `@ModifyExpressionValue` etc. are
   unavailable. Phase 7 uses only stock injectors: cancellable `@Inject` (HEAD/RETURN/
   INVOKE±shift), `@Redirect`, `@ModifyArg`, `@ModifyVariable` (`H-DEPTH-MASK-01`), and
   `@Accessor` companions. This matches the Pintonium-observed vocabulary.
4. **Hot-path callback cost**: `@Inject(cancellable=true)` allocates its
   `CallbackInfo(Returnable)` per invocation (no pooled fast-path in the fork). The
   D-P7-43 CORE anchors (GlStateManager alpha/blend) run per state change, so the anchor
   bodies are kept trivially branch-cheap (one static predicate call) — the §4.10.1
   "dumb bridge" posture is also the performance posture. The `@Redirect`-based
   boundaries (H-FRAME-00/05) replace the invocation rather than adding a callback
   allocation.
5. **Injection point inventory** (`org/spongepowered/asm/mixin/injection/points`):
   standard points present (HEAD, RETURN family, INVOKE with ordinal/shift, field
   access); `slice` support for bounded redirects is the stock `At` machinery the
   H-SKY-02/03 slice-bounded targets rely on.

## Decisions for the Phase 7 catalog

- **All `@Inject`/`@Redirect` targets use SRG names + descriptors** (D-5), remapped via
  the AP refmap at build; dev names appear only in `@Shadow`/`@Accessor` members.
- **`require = 0`, `expect = 1` everywhere** (§4.10.1); missing/duplicate core anchors
  are caught by the plugin's application audit, not by fatal Mixin failures.
- **Ordinals**: accepted only where the doc cites the Cleanroom patch byte order or this
  spike proves them. Verified this session against the 1.12.2 mapping database
  (`mcp__cleanroom_search_mappings`): `func_77474_a` OpenGlHelper.initializeTextures()V;
  `func_175068_a` EntityRenderer.renderWorldPass(IFJ)V; `func_78471_a`
  EntityRenderer.renderWorld(FJ)V; `func_78479_a` setupCameraTransform(FI)V;
  `func_174970_a` RenderGlobal.setupTerrain(Entity,DLICamera,IZ)V; `func_179086_m`
  GlStateManager.clear(I)V (the H-FRAME-02/03 ordinal-0 anchor). The GlStateManager
  alpha/blend family carries the D-P7-43 receipt (§4.10.1).
- **Cancellable HEAD, never MixinExtras wrappers**, for the alpha/blend suppression
  family and every future conditional-suppression need.

## Remaining leg (recorded, not assumed)

- Live-client application audit: after the first dev launch, the plugin's application
  counts are compared against the catalog (expect=1 rows), proving the H-SKY-02/03
  slice ordinals and the H-FRAME-02/03 ordinal-0 `GlStateManager.clear` anchor on real
  bytecode. The catalog's require=0 posture makes any miscount non-fatal and visible;
  corrections land as catalog updates, never silent re-ordinals.
- Built-jar refmap inspection (IR-28) on the first `./gradlew build` artifact.
