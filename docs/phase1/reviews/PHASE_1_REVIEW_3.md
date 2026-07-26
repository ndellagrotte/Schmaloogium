# Schmaloogium — Phase 1 re-verify session review (DESIGN.md §G1.2, third round)

**Document under review:** `docs/phase1/v10/PHASE_1_DOC.md` as it now stands, 2026-07-24.
**Why this session exists:** round two (`PHASE_1_REVIEW_2.md`, V2-1 … V2-10) returned
PASS-WITH-CORRECTIONS with §5-touching corrections, so §G1.3's "re-verify only if §5 changed" rule
requires a fresh pass before Phase 2, Phase 3 or any other dependent consumes the doc. My verdict is
on the **whole document as it now stands**, not on a diff and not on the prior findings lists.
**Reviewer:** fresh session, 2026-07-24. Did not author the doc, did not review it in round one or
round two, did not apply either fix-up, and inherited no context from any of those sessions.
**Deliverable filename:** `PHASE_1_REVIEW_3.md`, matching round two's choice — §G1.2 names only the
first file, but §G1.3 speaks of the *latest* review verdict, so each round needs somewhere to live
without overwriting the previous round's findings and `## Resolutions`.

> **Read this first.** The premise this session was briefed on — "re-verification after the round-2
> fix-up" — is false. **No round-2 fix-up was performed.** `PHASE_1_REVIEW_2.md` carries no
> `## Resolutions` heading, `PHASE_1_DOC.md` carries no round-2 addendum, and all ten of V2-1 … V2-10
> are still present in the document verbatim. This session therefore verified an *unchanged*
> document, and its findings necessarily restate round two's. That is the honest outcome, not a
> failure to look; see §3 F3-1 and the audit in §4.

---

## 0. What I read, and in what order

**Assigned reading, read first and in the order the brief gives:**

- `DESIGN.md` — all of Part I (§G0–§G10, lines 1–575) and the Phase 1 spec in Part II
  (lines 585–658). Other phases: titles / milestone / depends-on / OQ columns from §G5.1's table
  only. I did not read any other Part II spec.
- `RESEARCH.md` §0 and §1; then the Phase 1 spec's Required inputs — §5.1–§5.3, §6.1, §7.2, §12.2.
- Template ground truth, complete: `build.gradle`, `gradle.properties`, `settings.gradle`,
  `gradle/scripts/{dependencies,extra,publishing}.gradle`,
  `gradle/wrapper/gradle-wrapper.properties`, all eight files under `src/**`, all three
  `.github/workflows/*`, `README.md`.
- `PHASE_1_DOC.md` in full.
- **Last, and only after my own findings were fixed:** `PHASE_1_REVIEW_1.md` (structure and its
  `## Resolutions`), then `PHASE_1_REVIEW_2.md` in full.

**Read beyond the assigned list, each with its reason** (§G1.1/§G1.2 recording requirement):

| Extra input | Why |
|---|---|
| `RESEARCH.md` **App D in full (D.1–D.4)**, **App F.1, F.5, F.6, F.7**, **App B.1–B.5**, **§3.2**, **§4.1–§4.4** | The brief's standing licence for the exhaustive contract sweep (§2 below), whether or not the doc cites these rows. This is where F3-2 and F3-3 come from. |
| `RESEARCH.md` §6.2 (whole table + §6's preamble) | The ARB-geometry rule (§3 of the doc, `[D-P1-25]`) rests entirely on one §6.2 row; the preamble governs how §6.2 rows may be used. |
| `RESEARCH.md` §3.1 and App A.3 | To check the ARB-form citations the doc makes; both do carry the `#extension GL_ARB_geometry_shader4` + `maxVerticesOut` fact, so those two citations are sound. |
| Repository state: `LICENSE` (head + line count), `git log`, `git status`, `.gitignore` | The doc makes repo-state claims (§11.2 D-7, §12 item 1, §4.1's `.gitignore` row, §0.3 item 4). Read-only inspection only. |
| Live network: `repo.cleanroommc.com` maven-metadata, GitHub releases API, `maven.arcseekers.com` maven-metadata, `repo1.maven.org` metadata, `search.maven.org/solrsearch`, `maven-central.storage-download.googleapis.com` | The Phase 1 spec *orders* re-verification of the loader pin, and this brief additionally orders the ASM row confirmed or refuted. Permitted for the pin table only, and used for nothing else. |

**Deliberately not read**, and disclosed rather than claimed: `cleanroom-src/src/main/java/com/cleanroommc/`
(the brief bounds exploration there; round two performed the skim and reported that it changed
nothing, which I take at face value only for the *build session's* omission, not as evidence for any
finding of mine), `phase_1_chatlog.md` (not an input to either session type), and the MCP `cleanroom`
recipes — round two independently reproduced every `[V:mcp]` assertion in §4.5 verbatim, and I found
no claim in the document that turns on re-running them a third time. Recorded as a deliberate
omission.

**Hard rules observed:** no code, no builds, no tests, no review or adversarial sub-agents, no scope
creep, no fixes. `PHASE_1_DOC.md`, `RESEARCH.md`, `DESIGN.md` and the existing content of
`PHASE_1_REVIEW_1.md` / `PHASE_1_REVIEW_2.md` are unmodified.

---

## 1. Verification performed, independent of the doc's characterizations

### 1.1 Pins — live, and none has drifted

The brief permits live checks here and notes the loader ships daily. I ran §4.2.6/§10.1's procedure
steps 2–3 exactly as the document writes them; they work as specified.

| Pin | Independent check (2026-07-24) | Result |
|---|---|---|
| Cleanroom loader `0.6.6-alpha` | `repo.cleanroommc.com/releases/com/cleanroommc/cleanroom/maven-metadata.xml` → `<release>` and `<latest>` both `0.6.6-alpha`, versions tail `0.6.4 / 0.6.5 / 0.6.6-alpha`. Cross-checked against `api.github.com/repos/CleanroomMC/Cleanroom/releases`: `0.6.6-alpha` published 2026-07-24T13:37:05Z, `0.6.5-alpha` 2026-07-24T01:30:51Z, `0.6.4-alpha` 2026-07-23, `0.6.3-alpha` 2026-07-22. | ✅ **no drift**; the two sources agree, which is exactly what step 2's "a tag in one and not the other is itself a finding" is designed to detect |
| Unimined kappa `1.4.26-kappa` | `maven.arcseekers.com/releases/…/xyz.wagyourtail.unimined.gradle.plugin/maven-metadata.xml` → `<release>` and `<latest>` both `1.4.26-kappa`; tail `1.4.24 / 1.4.25 / 1.4.26-kappa` | ✅ correct |
| ASM `9.10.1` (test scope, `:engine`) | `repo1.maven.org/maven2/org/ow2/asm/asm/maven-metadata.xml` → **HTTP 403** for me too, so round two's report of the refusal is confirmed. Second-sourced via `search.maven.org/solrsearch` (newest `9.10.1`, ahead of `9.10 / 9.9.1 / 9.9 / 9.8`) **and independently** via Google's Maven Central mirror `maven-central.storage-download.googleapis.com/maven2/org/ow2/asm/asm/maven-metadata.xml` → `<release>` and `<latest>` both `9.10.1` | ✅ **confirmed, and now genuinely multi-sourced** — see F3-12 |

The two release-cadence facts the doc leans on both hold: two loader releases landed on
2026-07-24 itself, and the daily cadence is real. **No pin in §4.2.6 has drifted, so nothing here is
a finding about the pins.** The procedure in §4.2.6/§10.1 is executable as written — I executed the
query half of it — and I have no criticism of it.

### 1.2 Repository claims — all true

`LICENSE` at the repo root is the verbatim GPL-3.0 text (674 lines, opening
"GNU GENERAL PUBLIC LICENSE / Version 3, 29 June 2007", FSF copyright line intact). `git log` shows
`aa917a6 Update LICENSE from MIT to GPL-V3` preceding both doc commits. `git status` shows modified
`PHASE_1_DOC.md` and `PHASE_1_REVIEW_1.md`, untracked `PHASE_1_REVIEW_2.md`, `RESEARCH.md`,
`phase_1_chatlog.md`. `.gitignore` does contain `**/build/`. §11.2's D-7 row (`[V:repo]`), §12 item 1
("verify, it already is") and §4.1's `.gitignore` row therefore describe the repository accurately.
§0.3 item 4's claim that one read-only git inspection was made is consistent with the doc's content.

### 1.3 Template ground truth (§4.1) — re-checked line by line, no misstatement

Every row of §4.1's table is correct against the files as committed: `main` branch with no mixin
JSON and no `MixinConfigs` attribute anywhere; `settings.gradle` has no `include` and derives
`rootProject.name` from the directory name; the Unimined block sits at root with the inline literal
`loader "0.5.17-alpha"`; the AT wiring hardcodes `${rootProject.projectDir}`; Blossom is declared on
`sourceSets.main` only with `property('package', "${root_package}.${mod_id}")`; `jar` takes classifier
`dev` and is `finalizedBy(remapTaskName)`; `contain` copies into `/` with `ContainedDeps`/`NonModDeps`;
there is **no** `src/test/`; lwjglx appears at exactly one `compileOnly` site; all three workflows pin
Gradle 9.6.1 and Temurin 25 and hardcode root-relative `build/libs`.

All four template defects §11.3 reports reproduce independently: `modImplementation` appears only in
the README and a `dependencies.gradle` comment while only `modCompileOnly`/`modRuntimeOnly` are
declared and passed to `mods { remap(...) }`; `extraArgs.split { "\\s+" }` is Groovy's
closure-partitioning `CharSequence.split(Closure)`, not `String.split(String)`;
`gradle/scripts/extra.gradle` documents `assertProperty`/`assertSubProperties`/`setDefaultProperty`
which exist nowhere in `build.gradle` (whose only `ext` entry is `access_transformer_locations`);
`publish_to_local_maven` is read by no script in the repository.

### 1.4 Doc gate, met literally

| Doc-gate criterion | Verdict |
|---|---|
| "module/package layout finalized with dependency rules as testable constraints" | ✅ §2.1's three module tables plus C-1 … C-4 in §4.3, each with a named test in §8.1 and a checklist item in §12 |
| "every D-1..D-10 either satisfied by this phase or explicitly deferred with its owner phase named" | ✅ §11.2 dispositions all ten; every deferral names its owner (D-3→2, D-4→4, D-5→7, D-9→5+/7, D-10→2) |
| "pin table complete with re-verification procedure" | ✅ §4.2.6's thirteen rows plus a seven-step procedure, restated in §G4.4 form at §10.1. Verified live (§1.1) |

### 1.5 Template completeness, OQs, scope, binding decisions

All thirteen §G9 sections (0 through 12) are present and substantive — none is a stub. All four
assigned OQs carry full §G4.4 spikes: OQ-2 (§10.1), OQ-12 (§10.2), OQ-20 (§10.3), OQ-21 (§10.4), each
with a verbatim question, a concrete procedure, explicit success **and** failure criteria, and a
fallback designed now rather than deferred. The OQ-20 `NullGLDevice` drill and the OQ-21
with/without-shim profile comparison are falsifiable procedures, not restatements of the question.

**Scope discipline holds in both directions.** Nothing from the spec's *Scope — out* is designed: the
`:conformance` module and the CI `conformance` job are explicitly empty slots (harness content is
Phase 2's), there is no pack-format work, no GL *policy* beyond facade shape, and §4.8.4 addresses
only the licensing question OQ-12 assigns here while leaving ModularUI fitness to Phase 12. Every
*Scope — in* bullet is answered, including the three the spec poses as questions — ATs for v0.1 (no,
with the re-enable path recorded), `contain` for `:engine` (no, with a comparison table), one mixin
config per phase (yes, with the reason).

**No D-1..D-10 is contradicted, and no contract-visible component is "improved" in the §G4.2 sense.**
The one item where a contract-visible decision rests on shaky provenance is the ARB-geometry rule,
and that is a provenance defect rather than a design change (F3-4).

---

## 2. The contract sweep

The brief's round-3 addition: take the contract end-to-end rather than through the rows the doc
chose to cite, and ask of each — *can the §4.7.4 facade express this, at the milestone the consumer
needs it?* I swept **App D.1–D.4 in full**, **App F.1, F.5, F.6, F.7**, **App B.1–B.5**, **§3.2**,
and **§4.1–§4.4**. Clean rows are named below, per the brief, precisely so the two dirty ones are
visible against them.

### 2.1 App D — the built-in uniform inventory, every row

| Sub-table | Types present | Facade verb | Verdict |
|---|---|---|---|
| **D.1** held item / player | `int` (`heldItemId`, `heldItemId2`, `heldBlockLightValue`, `heldBlockLightValue2`, `isEyeInWater`, `hideGUI`), `float` (`wetness`, `eyeAltitude`, `nightVision`, `blindness`, `screenBrightness`), **`ivec2`** (`eyeBrightness`, `eyeBrightnessSmooth`) | `upload(loc,int)`, `upload(loc,float)`, `upload(loc,int,int)` | ✅ clean |
| **D.2** world / time / weather | `int` (`worldTime`, `worldDay`, `moonPhase`, `frameCounter`, `fogMode`), `float` (`frameTime`, `frameTimeCounter`, `sunAngle`, `shadowAngle`, `rainStrength`, `fogDensity`), `vec3` (`fogColor`, `skyColor`) | `upload(loc,int)` / `(loc,float)` / `(loc,f,f,f)` | ✅ clean. Worth naming: `fogMode`'s value **is** a GL constant (`GL_LINEAR`/`GL_EXP`/`GL_EXP2`), but it crosses the facade as uniform *data*, not as a parameter type, so §4.7.4's "no GL constants appear in any signature" rule is not violated. This row survives scrutiny; it is not an oversight |
| **D.3** camera / matrices / screen | `float`, `vec3` (celestial vectors, camera positions), **`mat4` ×10**, **`ivec2`** (`atlasSize`, `terrainTextureSize`), `int` (`terrainIconSize`) | `uploadMatrix4(loc, float[16], boolean)`, `upload(loc,int,int)`, the scalar/vector overloads | ✅ clean |
| **D.4** per-draw dynamics | `vec4` (`entityColor`), `int` (`entityId`, `blockEntityId`, `instanceId`), **`ivec4` (`blendFunc`)** | `upload(loc,f,f,f,f)`, `upload(loc,int)` … and **nothing for `ivec4`** | ❌ **one row unexpressible — F3-2** |

That is the whole appendix. Nineteen of twenty distinct type-obligations are served by the five
existing overloads plus `uploadMatrix4`. Exactly one is not.

### 2.2 App F — the properties surface

- **F.1 engine flags.** `clouds`, `oldHandLight`, `dynamicHandLight`, `oldLighting`,
  `shadowTranslucent`, `underwaterOverlay`, `sun`, `moon`, `vignette`, `rain.depth`,
  `beacon.beam.depth`, `frustum.culling`, `separateAo` — none has a GL-verb consequence at the facade
  layer: they are render-flow decisions (Phase 7), a CPU-side culling toggle, and a vertex-attribute
  change (Phase 10). ✅ clean. `backFace.solid` / `.cutout` / `.cutoutMipped` / `.translucent` are the
  exception and are discussed under F3-3.
- **F.5 custom textures and noise.** The raw form
  `<path> <TEXTURE_1D|2D|3D|RECTANGLE> <internalFormat> <dims…> <pixelFormat> <pixelType>` maps onto
  `create` → `allocate(t, TextureSpec)` → `upload(t, TextureData)` → `bindToUnit`, and the awkward
  clause — *"Multiple texture types may share a unit (distinguished by sampler type; one type per unit
  per program)"* — **survives precisely because the handles are opaque**: a handle carries its own
  target, so `bindToUnit(7, aTexture3D)` need not disturb the 2D texture already bound at unit 7.
  This is the opaque-handle decision (`[D-P1-15]`/`[D-P1-16]`) paying rent on a row the doc never
  cites. `.mcmeta` blur/clamp sidecars → `setParameters(TextureParameters)`. ✅ clean.
- **F.6 custom uniforms.** Declared types are `float | int | bool | vec2 | vec3 | vec4`; every one
  has an overload (`bool` uploads as an int, as GLSL requires). ✅ clean — and this is the half of
  §4.7.4's `ivec4` justification that is actually true.
- **F.7 per-program render state.** `alphaTest.<prog>` → `alphaTest(AlphaTestState)`;
  `blend.<prog>` → `blend(BlendState)` with "null/absent = disabled" covering `blend.<prog>=off`;
  `scale.<prog>=<scale> [offsetX offsetY]` → `viewport(x,y,w,h)`; `flip.<prog>.<buf>` and
  `program.<prog>.enabled` are registry/ping-pong policy needing no GL verb. ✅ **fully served** — a
  clean set, and the one that makes F3-3's asymmetry visible.

### 2.3 App B — buffers, attachments, the unit map

- **B.1** two textures per buffer, per-buffer clear colours (fog colour / white / transparent black),
  per-buffer clear suppression, blending off while composites write → `clearColor` + `clear(EnumSet)`
  + `drawBuffers` + `blend(null)`. ✅ clean.
- **B.2** `depthtex0/1/2`, `shadowtex0/1`, `shadowcolor0/1`, hardware-PCF compare mode →
  `attachDepth`, `attachColor`, `setParameters`. The **depth copies** taken before translucents and
  before weather (§4.3/§4.4) are expressible through `blit(src, dst, BlitSpec)`. ✅ clean.
- **B.3** the fixed unit map → `bindToUnit(int unit, TextureHandle)` plus `upload(loc,int)` for the
  sampler uniform itself. The facade correctly holds *no* unit numbers — that is Phase 5/6 policy, and
  §4.7.4 says so. ✅ clean.
- **B.4** all 37 internal formats, the `*_INTEGER` pixel formats and the note that "integer internal
  formats require the integer transfer path" → carried by `TextureSpec` and by `TextureData`'s
  `PixelLayout`, which §4.7.4 explicitly says is drawn from the same engine-level format vocabulary.
  ✅ clean *as a shape*; the vocabulary's contents are Phase 5's, which is the correct owner.
- **B.5** draw-buffer index prefixes → `drawBuffers(f, int[])`. ✅ clean.

### 2.4 §3.2 source directives and §4.1–§4.4 lifecycle/flow

`colortexNFormat` → `TextureSpec`; `colortexNClear`/`ClearColor` → `clearColor`/`clear`;
`colortexNMipmapEnabled` → `generateMipmap` (guarded by `supportsMipmapGeneration()`); `DRAWBUFFERS`
/ `RENDERTARGETS` → `drawBuffers`; the shadow consts (`shadowMapResolution`, `generateShadowMipmap`,
`shadowHardwareFiltering`, per-texture `Mipmap`/`Nearest`) → `TextureSpec`/`TextureParameters`/
`generateMipmap`; the implicit resource declarations are front-end scanning (Phase 3) that produce
sizing requirements, not GL calls; `countInstances` → `fullscreenQuadInstanced(int)` for composites,
and a re-render loop over vanilla geometry for gbuffers, which needs no facade verb. §4.1's four
startup probes and the GL-3.0 mipmap gate → `GLCapabilityProfile`. §4.1's uninit ("delete all GL
objects") is fully served by the four `delete` verbs — **which is itself the evidence that the
renderbuffer permit is dead weight** (F3-6). §4.2's pre-link attribute binding at 10/11/12 →
`bindAttributeLocation`; the "use program is the universal state barrier" behavior →
`ShaderService.use`. §4.3's incomplete-format fallback → `check(f)` returning `FramebufferStatus`.
✅ all clean.

The one place §4.4 comes back dirty is the composite/final draw state, discussed as F3-3.

### 2.5 Sweep result

**Two dirty rows out of the whole contract**: `blendFunc` (App D.4) and the composite/final draw
state (§4.4/§4.3). Everything else named above came back clean. I want that ratio on the record,
because it is the fair characterization of this facade: it is a good facade with two holes, not a
leaky one.

---

## 3. Findings

### F3-1 — Round two's corrections were never applied; this session verified an unchanged document · **blocking**

**Location:** `PHASE_1_REVIEW_2.md` (absence of a `## Resolutions` heading); `PHASE_1_DOC.md` §0.4
and the closing paragraph at line 2319.

**Claim under test:** the brief's premise, and §G1.3's cadence — that a fix-up session applied
`PHASE_1_REVIEW_2.md`'s findings and recorded each resolution in that file, making this pass a
re-verification of corrected text.

**Evidence.** `PHASE_1_REVIEW_2.md`'s headings run `## 0` … `## 4. Verdict` and stop; there is no
`## Resolutions` section, whereas `PHASE_1_REVIEW_1.md` has one at line 278. `PHASE_1_DOC.md` §0.4 is
titled "Fix-up session addendum" and names only "`PHASE_1_REVIEW_1.md`'s findings F-1 … F-12"; there is
no §0.5 or equivalent for round two. The document's closing paragraph still reads "A fix-up session
(§G1.3) has since applied findings F-1 … F-12 … Because the fix-up altered **§5**, the next step in
the cadence is a **fresh verify session**" — i.e. it still describes the post-round-one state.
Confirming it directly, each round-two finding is present verbatim in the current text:

| V2 finding | Still present? | Where I confirmed it |
|---|---|---|
| V2-1 `ivec4`/`blendFunc` | yes | line 1226 still reads "The 1.12.2 contract declares none"; `UniformService` (lines 1099–1109) has no four-int overload |
| V2-2 `StateService` | yes | lines 1147–1157 unchanged; no depth-test/cull/colour-mask verb; no deferred-table row for any of them |
| V2-3 ARB provenance | yes | §3's row (line 276) still tagged `[V:doc]`; §5.2 (line 1676) still names only Phase 4 |
| V2-4 build files | yes | §4.2.3 still declares `repositories { mavenCentral() }` in `:engine` only; §4.2.4/§12 item 7 still inject no system properties into `:mod` |
| V2-5 renderbuffer permit | yes | line 1043 permits `RenderbufferHandle`; only four sub-interfaces declared; §12 item 18 still says "five" |
| V2-6 dead `mod-*.jar` | yes | §8.2 line 1809 unchanged |
| V2-7 `eyeBrightness` citation | yes | lines 275 and 1102 both still say App D.3 |
| V2-8 cross-reference slips | yes | §3's last row still points at §4.9; §4.2.5 still calls item 7 "the Impl-gate item" |
| V2-9 / V2-10 | yes | §12 item 14b and §4.5.2's nested packages unchanged |

**Why blocking.** §G1.3 makes a doc verified only on "PASS, or PASS-WITH-CORRECTIONS with all
resolutions recorded". Round two's verdict stands with *no* resolutions recorded, so under §G5.3
`PHASE_1_DOC.md` was already an invalid dependency input before this session started, and it still
is. A dependent that read it would build against a §5 whose defects are documented in a review file
nobody applied. The remedy is not a fourth verify pass — it is the fix-up session that the cadence
requires and that did not happen.

**Touches §5:** the fix-up it demands does (F3-2/F3-3/F3-4 below).

---

### F3-2 — `UniformService` cannot upload `blendFunc`, and the deferred-verbs table denies the uniform exists · **blocking**

**Location:** §4.7.4 — `UniformService` (lines 1099–1109) and the "deliberately deferred verbs"
table row *`ivec3` / `ivec4` / `mat3` uniform uploads* (line 1226); §3's conformance map; surfaced to
dependents by §5.2's "facade's stated non-verbs" row (line 1676).

**Claim under test:** "`ivec3` / `ivec4` / `mat3` uniform uploads | **The 1.12.2 contract declares
none** — App D.3's integer uniforms are all `ivec2`/`int`, and custom uniforms are
`float/int/bool/vec2/vec3/vec4` (App F.6) | whichever phase meets the first one; additive".

**Evidence.** RESEARCH.md **App D.4** — the fourth sub-table of the same appendix — declares:

> `blendFunc` | **ivec4** | current blend srcRGB, dstRGB, srcA, dstA

I reached this from the sweep (§2.1), not from the doc's citations, which is the point: the claim's
narrowing to "App D.3's integer uniforms" is exactly what hides the counterexample. `blendFunc` is
not obscure — DESIGN.md §G4.6 uses it as *the* canonical example of state the engine observes from
`GlStateManager` ("we *observe* its state (e.g. `blendFunc` uniform)"), which means the design
document a dependent reads first names the one uniform the facade cannot upload.

`UniformService` offers `upload(loc,int)` and `upload(loc,int,int)`. An `ivec4` cannot be uploaded
through either; there is no four-integer overload and no generic int-array form. Correspondingly §3's
conformance map has a row for the `ivec2` uniforms and **no row for `blendFunc`** — an unmapped
in-scope contract row by the same standard that justified adding the `ivec2` row.

The owner is Phase 6 (§G5.1: "App D inventory, cadences, smoothing, unit map, value providers",
milestone **v0.1**), with per-draw dynamics shared with Phase 9 at v0.3 — either way inside the
current phase set, and no later than v0.3.

**Why blocking rather than correction.** Two things compound. First, a dependent cannot implement a
contract uniform. Second — and this is what makes it blocking rather than a missing overload — the
table that exists *specifically* so a dependent can distinguish "gap" from "deliberate decision"
tells it, as fact, that its missing verb corresponds to a contract that does not exist. A Phase 6
session reading §5.2 and following it to §4.7.4 would conclude it had misread the contract, not that
the facade was short a verb.

**Touches §5:** **yes** — §5.2's non-verbs row states the same falsehood and the fix adds a verb to
the exposed interface.

---

### F3-3 — `StateService`'s stated exclusion rule is false, and the v0.1 composite/final draw state is not expressible · **correction**

**Location:** §4.7.4 — `StateService` (lines 1147–1157), `DrawService` (lines 1159–1164), the
"`StateService` is deliberately narrow" design rule (lines 1180–1184), and the closing two-part
"what the facade deliberately does NOT contain" split; §5.2 exposes the service set to Phases
4/5/6/7/8/13/14 as the interface they build against.

**Claim under test:** "It exposes only what §G4.6 says we perturb — viewport, clears, depth mask,
blend, alpha test — plus explicit `snapshot`/`restore`. **It exposes no way to set state that
`GlStateManager` caches without going through it**, because §G4.6 forbids exactly that … The
narrowness is the enforcement: you cannot misuse an entry point that does not exist."

**Evidence, in three parts.**

1. **The stated rule does not distinguish the included set from the excluded one.** In 1.12.2,
   `GlStateManager` caches viewport, clear colour, blend enable/func, alpha test enable/func, depth
   mask, depth test, cull face and colour mask alike. So "exposes no way to set state that
   `GlStateManager` caches" is false of *every verb the service already has*. Whatever the real
   criterion for inclusion is, it is not the one written down — and §5's preamble ("per §G5.3 a
   dependent phase reads this section, not the rest of the document") means dependents are handed
   this rule as the test for whether their own state need is in scope. Round two observed that §G4.6
   does not enumerate perturbed state; the sharper problem is that the enumeration the doc substitutes
   is justified by a criterion its own contents refute.

2. **The v0.1 composite/final draw state is not expressible.** RESEARCH.md §4.4: "Composite passes
   draw one fullscreen quad (triangle-strip fallback where quads are unavailable) **under an identity
   ortho, fog/depth/blend disabled**, optional per-buffer mipmap regeneration, optional sub-viewport
   (`scale.<prog>`), `countInstances` instancing loop." Composite and final are v0.1. Of those four
   state elements the facade expresses exactly one:

   | Element | Expressible? |
   |---|---|
   | blend disabled | ✅ `blend(null)` |
   | depth **test** disabled | ❌ `depthMask(boolean)` is depth *writes*, a different bit of state |
   | fog disabled | ❌ no verb |
   | identity ortho | ❌ no verb, and `fullscreenQuad()` is specified only as "the backend picks `GL_QUADS` or the triangle-strip fallback" — not as establishing a draw state |

   RESEARCH.md §4.3 adds one more for `final`: "renders to the vanilla framebuffer (**anaglyph-aware
   colour masking**)" — no colour-mask verb exists. Lower stakes, since a phase might reasonably scope
   anaglyph out, but that decision is recorded nowhere.

   `DrawService.fullscreenQuad()` being specified as *state-establishing* would close most of this
   cleanly — but then exposing `blend` and `alphaTest` separately becomes the odd choice, since they
   belong to the same state block.

3. **App F.1's `backFace.*` flags have a GL consequence and no named owner.** `backFace.solid`,
   `.cutout`, `.cutoutMipped`, `.translucent` are pack-settable, parsed at v0.1, and applied when
   gbuffers terrain renders at v0.1. Face culling is not in the facade. There is a defensible answer —
   that terrain draws through vanilla's path so the flags are applied by `:mod` through
   `GlStateManager`, never through the facade — and I think it is the right answer. But it is
   precisely the answer the (false) exclusion rule would have made legible if it were true, and the
   doc does not give it. `alphaTest.<prog>` and `blend.<prog>` (App F.7) got verbs; the F.1 backface
   flags got neither a verb nor a sentence.

**Severity.** Correction, not blocking. §5.2 does give dependents an escape hatch ("If you need an
assertion **or a facade verb** that is not in §4.7.4/§4.7.5, add it in your own doc's §5 as a
requested change"), so a Phase 5/7 session is not stranded. What it is denied is the ability to tell
gap from decision — which is the exact purpose §4.7.4's closing tables were added to serve.

**Fix, any one of which closes it:** add the verbs; or add deferred-table rows naming the owner and
the reason; or specify `DrawService.fullscreenQuad()` as establishing the whole composite draw state
and say so. Whichever is chosen, the "no state `GlStateManager` caches" sentence needs replacing with
the real criterion.

**Touches §5:** **yes** — §5.2 exposes the service set as the built-against contract.

---

### F3-4 — The ARB-geometry rule over-claims its provenance, and the obligation it creates for Phase 3 is not in §5 · **correction**

**Location:** §3's map row "Geometry programs may declare the **ARB form**" (line 276, tagged
`[V:doc]`); §4.7.4's "No pre-link program-parameter hook, and that is a statement rather than an
omission" (lines 1199–1208); `[D-P1-25]`; §5.2's non-verbs row; §11.4's note to Phase 3.

**Claim under test:** "RESEARCH.md §6.2 records the modernization this project adopts: core GL 3.2
geometry shaders **with internal translation**, the preprocessor continuing to accept both source
forms. The ARB form is therefore handled *upstream of the facade*, as a source-level rewrite in the
Phase 3 front-end / Phase 4 compile path" — carried in §3 with provenance `[V:doc]`.

**Evidence.**

1. **The `[V:doc]` tag is on the other half of the row.** RESEARCH.md §6.2's row reads:
   *"Core geometry shaders (GL 3.2) with internal translation | `ARB_geometry_shader4`
   program-parameter dance | Packs still declare the ARB extension + `maxVerticesOut`; the
   preprocessor must keep accepting both forms `[V:doc]`"*. The tag sits in the **risk-note column**
   and certifies the requirement *on us* (packs declare both forms), not the *feasibility or adoption*
   of internal translation. §6's own preamble is explicit about how its rows may be used: "Highest-
   density `[U]` zone in this document … Each carries a risk note; **none should be promoted into the
   design doc without its OQ row being resolved or the claim spot-checked**." The translation half has
   no OQ row of its own, which places it under **OQ-22** — §G10: *"Spot-check ledger for the §6.2/§6.3
   modernization claims"*, owner **P14**, milestone v0.5. So Phase 1 tags `[V:doc]` a claim RESEARCH.md
   classifies as unspot-checked, and settles at v0.1 a question routed to a phase scheduled at v0.5.
   The doc's §3 row cites §3.1 and App A.3 as well — I checked both and they do carry the ARB-form
   fact, so those two citations are sound. It is the §6.2 leg that over-claims.

2. **The milestone that first compiles a `.gsh` is v0.1**, and the exposure is unstated. RESEARCH.md
   §3.1 makes `.gsh` part of the program set with no carve-out. If the translation is not built by
   then, the facade cannot express the ARB path at the milestone that needs it, and the escape route
   runs Phase 4 → additive request → Phase 1 fix-up → another §5 re-verify, mid-v0.1. The doc calls
   the escape "additive … not a redesign", which is true of the *facade* and silent about the schedule.

3. **The obligated phase cannot see the obligation.** §5's preamble states that a dependent reads §5
   and not the rest of the document. Phase 3 is the phase told to own the source-level rewrite — and
   §5 contains no row placing it there. §5.2's non-verbs row names Phase **4** ("only if the ARB
   geometry form is not translated upstream") and never Phase 3; the obligation appears only in §11.4's
   hand-off prose and §4.7.4. A Phase 3 session obeying §5's own instruction would never learn of it.

**Note on the design itself:** I would not disturb it. Translating ARB-form geometry shaders upstream
of the facade is the right seam, and a pre-link `glProgramParameteriARB` verb would import GL
primitive-type constants into a signature that §4.7.4 forbids them in. The defect is provenance and
surfacing, not the decision.

**Check the brief asks for explicitly:** re-tagging this row does **not** cost §3 its "zero unmapped
in-scope rows" property. The row stays; only its tag column changes (`[V:doc]` for the pack-side
requirement, `[A]`/`[Q:OQ-22]` for the adopted translation), exactly as §3's extension-set row already
does with `[A]`.

**Touches §5:** **yes** — the fix adds a §5 row exposing the translation obligation to Phase 3.

---

### F3-5 — Two specified build files cannot work as written, and §2.1's tree contradicts §8.1 · **correction**

**Location:** §4.2.2 (root aggregator), §12 item 6 (`conformance/build.gradle`), §4.2.3 vs §4.2.4/§8.2
(system-property injection), §8.1's test assignments, §2.1's directory tree.

**Evidence.**

1. **`:conformance` has no repository.** Repositories are per-project in Gradle. §4.2.3 gives
   `:engine` its own `repositories { mavenCentral() }`; `:mod` gets one by applying
   `gradle/scripts/dependencies.gradle` (§4.2.4); the root `subprojects {}` block in §4.2.2 declares
   **no** repositories while adding `testImplementation 'org.junit.jupiter:junit-jupiter:6.0.3'` and
   `testRuntimeOnly 'org.junit.platform:junit-platform-launcher'` to *every* subproject. §12 item 6's
   contents list for `conformance/build.gradle` (`java-library`, JUnit, `implementation
   project(':engine')`, `testImplementation testFixtures(project(':engine'))`, classpath system
   properties) has no repositories block either. As specified, `:conformance` cannot resolve JUnit —
   and item 6's own test hook (`./gradlew :conformance:compileJava`) would not surface it, because the
   failure is in the *test* configuration.

   *(The brief asks whether `repositories` was hoisted into the root `subprojects {}` block. It was
   not — §4.2.3 still declares it locally in `:engine`, and §4.2.2 has none. So the C-1 story is
   unchanged and intact: `:engine` still declares exactly what §4.2.3 claims, which is
   `java-test-fixtures`, `mavenCentral()`, ASM at `testImplementation`, and the three system
   properties. Hoisting would be one valid fix; it has simply not happened.)*

2. **`:mod`'s two bytecode scans have no specified classes-dir source and no specified ASM.** §4.3's
   half-two mechanism and §8.1's `SeamInternalsTest` (C-2) and `SeamLwjglConfinementTest` (C-3) both
   "scan `:mod`'s compiled classes", but the `schmaloogium.test.classesDir` /
   `…compileClasspath` / `…runtimeClasspath` injection is specified only for `:engine` (§4.2.3) and
   for `:conformance` (§8.2, "exactly as `:engine`'s does"). `mod/build.gradle` (§4.2.4, §12 item 7)
   has neither the system-property block nor `testImplementation 'org.ow2.asm:asm:9.10.1'` — and
   `[D-P1-3]` scopes the ASM permission to `:engine` alone, so a reader following the doc literally
   would not add it. Two of the four seam tests are therefore unimplementable as specified.

3. **§2.1's tree does not match §8.1.** The tree gives `engine/src/{main,test}/java` and
   `conformance/src/{main,test}/java` but only `mod/src/main/…`, while §8.1 assigns four tests to
   `:mod` (`SeamInternalsTest`, `SeamLwjglConfinementTest`, `BailRegistryTest`,
   `DiagnosticRoutingTest`). `mod/src/test/java` is missing from the tree.

**Fix:** add a repositories declaration to `:conformance` (or hoist `mavenCentral()` into the root
`subprojects {}`, which also removes the `:engine` duplication); add the same system-property block
*and* the test-scope ASM dependency to `mod/build.gradle`, widening `[D-P1-3]` accordingly; show
`mod/src/test/java` in §2.1's tree.

**Touches §5:** no.

---

### F3-6 — `GLHandle` permits a handle type that is never declared and can never be produced · **note**

**Location:** §4.7.3 (line 1043); §12 item 18 ("`GLHandle` + the **five** sealed sub-interfaces").

**Evidence.** `public sealed interface GLHandle permits ProgramHandle, ShaderHandle, TextureHandle,
FramebufferHandle, RenderbufferHandle {}` — but only **four** sub-interfaces are declared beneath it,
and no service creates, attaches, deletes or otherwise mentions a renderbuffer: `FramebufferService`
attaches colour and depth **textures** only. My §2.4 sweep supports dropping the permit rather than
adding verbs: §4.3's reference architecture makes every attachment a sampleable texture (the pack
contract requires `depthtex0/1/2`, `shadowtex0/1` and `colortex0-7` all to be readable from shaders),
and §4.1's uninit is fully served by the four existing `delete` verbs. A renderbuffer would have no
contract consumer. §12 item 18's "five" makes the inconsistency read as deliberate rather than as a
slip, which is what earns this a finding rather than a shrug.

**Consistency check the brief asks for:** the permit list (5) disagrees with the declared types (4)
and agrees with §12 item 18's count (5). The declarations are right and the other two are wrong.

**Touches §5:** no, if closed by dropping the permit and correcting item 18 to "four". **Yes**, if
closed by adding renderbuffer verbs — in which case it joins F3-2/F3-3/F3-4 in forcing the re-verify.

---

### F3-7 — One half of C-4's classpath pattern can never match · **note**

**Location:** §8.2 — "no classpath entry resolves to the `:mod` project (no `mod/build` path entry,
no `mod-*.jar`)".

**Evidence.** `:mod` keeps the template's `base { archivesName = mod_id }` (§4.2.4 moves the root
machinery "verbatim"; §2.3 sets `mod_id = schmaloogium`), so its artifacts are
`schmaloogium-0.1.0-dev.jar` / `schmaloogium-0.1.0.jar` — never `mod-*.jar`. The `mod/build` path
check catches every real violation, so `SeamConformanceDependencyTest` still works; the second
pattern is simply dead and should be `schmaloogium-*.jar`, or replaced by a comparison against the
resolved project path.

**Touches §5:** no.

---

### F3-8 — `eyeBrightness` is cited to the wrong appendix sub-table · **note**

**Location:** §3's map row "`atlasSize` / `eyeBrightness` are **`ivec2`** uniforms | RESEARCH.md
App D.3" (line 275); the same citation in §4.7.4's `// ivec2 — App D.3: atlasSize, eyeBrightness`
comment (line 1102).

**Evidence.** `atlasSize` is App **D.3** (camera / matrices / screen). `eyeBrightness` and
`eyeBrightnessSmooth` are App **D.1** (held item / player). The *type* is correct in both cases, so
nothing downstream is built wrongly — but this is the depthtex1-unit-11 discipline exactly, and a
Phase 6 session following the citation to D.3 will not find the uniform there. §5.2's pixel-transfer
row names `atlasSize`/`eyeBrightness` without a sub-table citation, so the fix is confined to §3 and
§4.7.4.

**Touches §5:** no.

---

### F3-9 — §4.10 claims Phase 1 ships three bail evaluation points; §9 and §12 ship one · **note**

*(Not raised by either prior round.)*

**Location:** §4.10's closing "What Phase 1 ships" paragraph (line 1591) against §9's milestone table
(lines 1885–1886) and §12 item 36.

**Evidence.** §4.10 states: "**What Phase 1 ships:** the types above, the registry, **the three
evaluation points wired**, the diagnostic routing, and zero registered checks." §9 contradicts it
directly:

> `BailRegistry` evaluation point 1 (bootstrap) | `v0.1`
> `BailRegistry` evaluation points 2 (vertex-format change) and 3 (mixin plugin) | `v0.3` — Both need Phase 10 to exist

and §12's checklist has only item 36 ("Bail evaluation point 1 (pre-bootstrap) wired") at v0.1, with
point 3's host (`SchmaloogiumMixinPlugin`, item 37) tagged v0.3. §G4.3 requires exactly one milestone
tag per component and §G0.3 makes the architected-now/implemented-later split load-bearing, so a
prose sentence asserting a v0.1 scope its own staging table denies is the kind of thing an
implementation session resolves by guessing. §9 and §12 are self-consistent and evidently right;
§4.10's sentence is the one to correct (e.g. "the three evaluation points *designed*, point 1 wired
at v0.1").

**Touches §5:** no — §5.3's row exposes the `CompatCheck`/`BailRegistry` types, not the wiring
schedule.

---

### F3-10 — Two cross-reference slips of the class round one's F-7 fixed · **note**

**Location and evidence:**

- §3, last map row: "Pack-facing vocabulary used **verbatim** in identifiers … Naming convention
  recorded in **§4.9**." §4.9 covers logging channels, debug flags and error channels; it contains no
  identifier-naming convention for pack vocabulary. The row's *substance* — the facade sits below the
  pack-vocabulary layer and so introduces no synonym risk — is correct; only the pointer is wrong.
- §4.2.5: "Recorded against §12 item 7, **which is the Impl-gate item** that depends on this merge."
  Item 7 creates `mod/build.gradle`; the Impl gate is item **15**, as §12's own preamble states
  ("the Impl gate … is reached at item 15").

**Touches §5:** no.

---

### F3-11 — The three mixin configs declare nested packages · **note**

**Location:** §4.5.2's config table — `com.schmaloogium.mod.mixin.preinit` (PRE_INIT),
`com.schmaloogium.mod.mixin` (DEFAULT), `com.schmaloogium.mod.mixin.compat` (MOD).

**Evidence.** The DEFAULT config's `package` is the *parent* of the other two configs' packages.
Mixin implementations register package-based scanning and classloader exclusions per config, and
CleanMix's behaviour with one config's package containing another's is undocumented in every source
this document cites (RESEARCH.md §5.1, the MCP `mixin-setup` guide, the template README all describe
the phase split without addressing nesting). **I did not verify this and I am not asserting it is
broken.** The cheap insurance is sibling packages (`…mixin.preinit` / `…mixin.main` / `…mixin.compat`)
or one added clause on §12 item 30's test hook, which already requires `runClient` to load all three
configs without error — item 33's throwaway-mixin check being the natural moment.

**Touches §5:** no (§5.3 exposes the three config slots and their package placement, so a package
rename would edit §5's text — but not the interface it exposes; I would not let this alone force a
re-verify).

---

### F3-12 — §4.2.6's ASM "single-source verification" caveat can now be retired · **note**

**Location:** §4.2.6's ASM row — "**Single-source verification** (`repo1.maven.org` metadata refused
the request); test-scope only, so a wrong guess would fail the bytecode test loudly and immediately
rather than ship."

**Evidence.** The caveat was honest when written, and `repo1.maven.org` still returns HTTP 403. But
`9.10.1` is now confirmed from two mutually independent sources: the Maven Central search API
(`search.maven.org/solrsearch`, `g:org.ow2.asm a:asm`, gav core → 9.10.1 ahead of 9.10 / 9.9.1 / 9.9 /
9.8) and Google's Maven Central mirror
(`maven-central.storage-download.googleapis.com/maven2/org/ow2/asm/asm/maven-metadata.xml` →
`<release>` and `<latest>` both `9.10.1`). Round two second-sourced via the first; this session adds
the second, which does not share the first's infrastructure. **The pin is correct.** The row's
wording can be relaxed to "second-sourced 2026-07-24 (Central search API and the Google Central
mirror); `repo1.maven.org` metadata refuses direct requests." Recorded as a note because it improves
the record without changing a decision.

**Touches §5:** no.

---

## 4. Audit of round two's resolutions

**There are none.** `PHASE_1_REVIEW_2.md` ends at `## 4. Verdict`; it has no `## Resolutions`
heading, and `PHASE_1_DOC.md` records no round-2 fix-up. §G1.3 requires that a fix-up session "records
each resolution in the review file under a `## Resolutions` heading"; that session did not run.

Accordingly, the two questions this section exists to answer resolve as:

- **Is each claimed resolution real?** No resolution was claimed. All ten of V2-1 … V2-10 stand
  unresolved; I verified each individually against the current text (table in F3-1) before opening
  `PHASE_1_REVIEW_2.md`, having independently re-derived the substance of V2-1, V2-2, V2-3, V2-4,
  V2-5, V2-6 and V2-7 from the contract sweep and the build files.
- **Did any resolution introduce a new defect?** Structurally impossible this round — none was
  applied. The pattern the brief warned about ("both fixes left a neighbouring instance standing")
  therefore did not recur; what happened instead is that no fix was attempted.

**Round one's resolutions, by contrast, are real and present**, and I confirmed the artifacts of each
while reading §4 and §5 for my own purposes: F-1's three pixel-transfer verbs plus
`ScriptedResponses.depthPixel` and `[D-P1-25]`; F-2's `java-test-fixtures` placement plus `[D-P1-26]`;
F-3's §12 item 1 as a *verification* step and §11.2's `[V:repo]` D-7 row (which I checked against the
repository, §1.2); F-4's §5.3 ModularUI row; F-5's `// renderWorld(FJ)V` descriptor; F-6's §0.1/§0.3/
§0.4 header work; F-8's `enable_mixin_debug` property and ASM pin; F-9's plugin-key-ships-with-class
rule; F-10's `SeamConformanceDependencyTest`; F-11's KHR_debug row; F-12's §4.2.5 caveat and item 7
instruction. Round two's judgement that all twelve are real matches what I see, and round one's
residues are exactly the ones round two named — and which this round finds still standing.

---

## 5. Verdict

**PASS-WITH-CORRECTIONS**

The document remains, on its own terms, a strong one, and I want to say so plainly before the
findings list is read as a verdict on its quality. The Doc gate is met literally on all three
criteria. All thirteen §G9 sections are present and substantive. All four assigned OQs carry genuine
§G4.4 spikes with pre-designed fallbacks — the `NullGLDevice` drill and the with/without-shim profile
comparison are falsifiable procedures, not restatements. Scope discipline holds in both directions.
No binding decision is contradicted. The pin table is complete and I re-verified its three volatile
rows against live sources, finding no drift and confirming the ASM row from a source round two did
not use. The contract sweep the brief ordered — App D in full, App F.1/F.5/F.6/F.7, App B.1–B.5,
§3.2, §4.1–§4.4 — came back with **two** dirty rows against several dozen clean ones, and one of the
clean results (App F.5's shared-unit rule surviving because handles are opaque) is the opaque-handle
decision earning its keep on a row the doc never claimed credit for. Nothing here requires rebuilding
the document, so FAIL would be wrong.

What forces the verdict is that the corrections are the *same* corrections. F3-1 is the finding that
matters: round two's fix-up did not happen, so this session re-derived and re-reported defects that
were already on the record. F3-2 remains blocking — the facade cannot upload a built-in uniform that
DESIGN.md §G4.6 itself uses as its canonical example, and the table added to make absences legible
denies the uniform exists. F3-3 and F3-4 are real corrections with bounded fixes. F3-5 makes two
specified build files unbuildable as written. The remaining seven are notes, of which one (F3-9) is
new this round and one (F3-12) improves the record rather than fixing a defect.

I have deliberately not manufactured findings to justify a third pass. The overlap with round two is
the honest result of reviewing an unchanged document, and I would rather report that plainly than
inflate the list to look independently productive.

### §G1.3 line

**The document is NOT verified.** §G1.3 grants that state on "PASS, or PASS-WITH-CORRECTIONS with
all resolutions recorded and no §5 change outstanding". This session's verdict is
PASS-WITH-CORRECTIONS; no resolutions are recorded for round two's findings *or* for this round's;
and §5-touching corrections are outstanding. Under the §G5.3 gating invariant `PHASE_1_DOC.md` is
**not a valid dependency input** for Phase 2, Phase 3, or any other dependent, and no dependent build
session may read it.

**Per-finding §5 disposition, as the brief requires:**

| Finding | Severity | Touches §5? | Consequence |
|---|---|---|---|
| F3-1 missing round-2 fix-up | blocking | the fix-up it demands does | forces the fourth pass by way of F3-2/F3-3/F3-4 |
| F3-2 `blendFunc` / `ivec4` | blocking | **yes** — §5.2's non-verbs row and the exposed `UniformService` | **forces a fourth verify pass** |
| F3-3 `StateService` / composite draw state | correction | **yes** — §5.2 exposes the service set | **forces a fourth verify pass** |
| F3-4 ARB provenance + Phase 3 obligation | correction | **yes** — the fix adds a §5 row for Phase 3 | **forces a fourth verify pass** |
| F3-5 build files + §2.1 tree | correction | no | fix-up only |
| F3-6 `RenderbufferHandle` permit | note | **only if** closed by adding renderbuffer verbs; §5-free if closed by dropping the permit | prefer dropping |
| F3-7 dead `mod-*.jar` pattern | note | no | fix-up only |
| F3-8 `eyeBrightness` → App D.1 | note | no | fix-up only |
| F3-9 §4.10 vs §9 / §12 item 36 | note | no | fix-up only |
| F3-10 two cross-reference slips | note | no | fix-up only |
| F3-11 nested mixin-config packages | note | no (a package rename edits §5's text, not its interface) | fix-up only |
| F3-12 ASM caveat retirable | note | no | fix-up only |

**Therefore:** a fix-up session must run — that is the step the cadence is missing, and it should
apply V2-1 … V2-10 and F3-1 … F3-12 together, recording resolutions in this file and in
`PHASE_1_REVIEW_2.md`. Because F3-2, F3-3 and F3-4 alter §5, §G1.3's "re-verify only if §5 changed"
rule fires again and **a fourth verify pass is required** before Phase 2, Phase 3 or any other
dependent consumes the doc. **There is no route by which a fix-up alone closes this phase.**

One procedural observation for whoever runs that fix-up, offered as a note rather than a finding: the
loop has now produced three verify sessions and one fix-up. The cheapest way to break it is for the
fix-up to close **all** §5-touching items in a single change — F3-2's `ivec4` overload, F3-3's state
verbs or state-establishing `fullscreenQuad`, F3-4's Phase 3 row, and F3-6 by dropping the dead
permit — so that the fourth pass has one coherent §5 to attack rather than a fifth partial edit.

---

*End of PHASE_1_REVIEW_3.md. Per §G1.2 this session stops here and fixes nothing.*

---

## Resolutions

*Recorded by the fix-up session of 2026-07-24 (§G1.3). Nothing above this heading was modified. Each
finding was applied **as `PHASE_1_REVIEW_4.md` dispositioned it** — round four re-attacked this list
finding by finding, refuted two of it, overstated four, and confirmed six. Where round four's ruling
differs from this round's, round four's is the one implemented, and the reason is given.*

| Finding | Round 4's ruling | Disposition |
|---|---|---|
| **F3-1** round-2 fix-up never ran | overstated → note, not §5 | **Applied as a note.** The factual claim was true and is now moot: this fix-up is the missing session. What survived — that §0.4 and the closing paragraph still described the post-round-one state — is fixed: §0.4 now points forward, a new **§0.5** records rounds two, three and four with the inputs read and the reason the defects clustered where they did, and the closing paragraph states the true cadence (four verify sessions, two fix-ups, a fifth pass required) |
| **F3-2** `blendFunc`/`ivec4` | **confirmed**, blocking, §5 | **Applied in full, on round four's stronger evidence.** `UniformService.upload(loc,int,int,int,int)` added; §3 gained a `blendFunc` row (App D.4, §3.4); the deferred row is now `ivec3`/`mat3` with the true reason and an explicit note that `ivec4` is served; §5.2's non-verbs row no longer lists `ivec4` and the pixel-transfer row names **6** and **9**. The citation is `DESIGN.md`'s Phase 6 Scope-in at **v0.1**, not the §G4.6 example. Round four's two corrections to the finding's own text were honoured: §5.2's row did not state the falsehood, and its real defect was the routing, which is fixed |
| **F3-3** `StateService` | overstated → correction, §5, narrowed | **Applied as narrowed. The headline was NOT applied**: `PHASE_1_DOC.md`'s "no way to set state `GlStateManager` caches **without going through it**" is a bypass rule and is correct as written — deleting it would have removed the §G4.6 enforcement claim. What was applied: the inclusion criterion is now stated explicitly (and attributed to this document, not to §G4.6); `depthTest` and `fog` are added; colour mask gets a deferred row naming Phase 7; `fullscreenQuad()` is stated to establish no state. **Sub-claim 3 was not applied** — `DESIGN.md` makes the engine-flag ownership map a Phase 3 deliverable, so naming `backFace.*`'s owner here would be scope creep; instead a deferred row records the absence and defers the assignment. "Identity ortho" was struck |
| **F3-4** ARB provenance | overstated → note, **not** §5 | **Applied as a six-word hedge only.** §3's row now says RESEARCH.md §6.2 *lists* the modernization as an opportunity, adopted here — matching what the doc already said correctly 1 100 lines later. **The proposed §5 row was not applied as written**: it would have obligated Phase 3 to the translation *strategy*, which `DESIGN.md` assigns to Phase 4, and would have reached into pack-format scope this phase is assigned out of. The real §5 gap — that Phase 3's *source-level* obligation is invisible in §5 — is closed under **F4-4** on interface-honesty grounds. The provenance tag was left alone: it qualifies the contract item, which is `[V:doc]` |
| **F3-5** build files | **confirmed**, correction | **Applied, with both of round four's refinements.** `:conformance` gets its own `repositories { mavenCentral() }` in a new §4.2.4a (**not** hoisted — `[D-P1-27]` records why); `mod/build.gradle` gets the system properties, test-scope ASM, and a **forced 9.10.1** so Unimined's inherited `asm-debug-all` 5.x cannot win and fail C-2/C-3 on Java 25 class files; §2.1's tree shows `mod/src/test/java`; §12 item 6's hook is now `compileTestJava` |
| **F3-6** `RenderbufferHandle` | **confirmed**, note | **Applied by dropping the permit** and correcting §12 item 18 to four. No §5 change, as round four preferred |
| **F3-7** dead `mod-*.jar` | **confirmed**, note | **Applied as `schmaloogium-*.jar`** (round four's first option), and the path half is now a segment-pair test rather than a literal `mod/build` substring |
| **F3-8** `eyeBrightness` → App D.1 | **confirmed**, fix under-scoped | **Applied at all five sites** round four identified — §3, §4.7.4's comment, §4.7.4's rationale, `[D-P1-25]`, and §0.1's inputs record — not the two this finding named |
| **F3-9** §4.10 vs §9 | **confirmed**, note | **Applied**, with round four's corrections: §4.10 now says the points are *designed* and only point 1 is wired at v0.1, naming §9 as the authority; the §G4.3 citation was **not** transcribed (no rule is violated — §9 gives each component one tag); and §12 gained item **36b** for evaluation point 2, which had no checklist entry at all |
| **F3-10** cross-reference slips | overstated (half refuted) | **Leg (a) applied by deletion** — "Naming convention recorded in §4.9" is gone, the substantive clause stays, and no section was invented. **Leg (b) not applied** — refuted; repointing §4.2.5 at item 15 would point at an item carrying no merge instruction |
| **F3-11** nested mixin packages | **refuted** | **No change required.** §12 item 30's hook already carries the check this finding proposed adding, and the platform sources state no non-nesting constraint. §4.5.2 now records it as an observation, with the plugin-FQN placement round four raised alongside it |
| **F3-12** ASM caveat | **refuted** as a finding | **No change required as a finding**, but the row's wording was refreshed: second-sourced from the Central search API and the Google Central mirror, with the honest qualification round four insisted on — **both are transports over the same Maven Central dataset**, so this is redundancy, not independence. `repo1.maven.org`'s 403 is still recorded |

**On round three's closing advice** — that the fix-up should close *all* §5-touching items in one
change so the next pass has one coherent §5 to attack — this session did exactly that, over the
larger set round four found: F3-2, F3-3 (narrowed), F4-1, F4-2, F4-3, F4-4, F4-5, F4-7, F4-8, F4-14,
plus the two one-line ride-alongs (`ScriptedResponses.validateFails`, `drawBuffers`' zero-length
"none"). F3-4 and F3-6 were closed **without** touching §5, as round four directed.
