# Schmaloogium — Phase 1: Foundation & project architecture — Architecture

---

## 0. Header

**Phase:** 1 — Foundation & project architecture
**Milestone:** v0.1 · **Depends on:** — (Wave 0; this doc feeds every other phase)
**Assigned OQs:** OQ-2, OQ-12, OQ-20 (seam hardness), OQ-21
**Date:** 2026-07-24
**Deliverable:** this document, per DESIGN.md §G9.

### 0.1 Inputs actually read

| Input | What was read |
|---|---|
| `Schmaloogium/DESIGN.md` | All of Part I (§G0–§G10, lines 1–575) and the Phase 1 spec in Part II (lines 585–658). Phase titles only from §G5.1 for other phases. |
| `Schmaloogium/RESEARCH.md` | §0 (reading guide, confidence tags), §1 (mission, non-goals, decision log D-1..D-10), §4.1 (lifecycle — the GL capability probe set), §4.2 (program-registry mechanics — read for the facade's "use program is the universal state barrier" implication), §5.1, §5.2, §5.3, §6.1, §7.1 (the D-5 sentence only), §7.2, §7.4 (the coexistence sentence only), §8.3, §9, §10.1–§10.3, §11 (full OQ register), §12.2, §12.4, App H (glossary rows for platform terms). |
| `Schmaloogium/build.gradle` | Complete. |
| `Schmaloogium/settings.gradle` | Complete. |
| `Schmaloogium/gradle.properties` | Complete. |
| `Schmaloogium/gradle/scripts/dependencies.gradle` | Complete. |
| `Schmaloogium/gradle/scripts/extra.gradle` | Complete. |
| `Schmaloogium/gradle/scripts/publishing.gradle` | Complete. |
| `Schmaloogium/gradle/wrapper/gradle-wrapper.properties` | Complete. |
| `Schmaloogium/.github/workflows/build.yml` | Complete. |
| `Schmaloogium/.github/workflows/release.yml` | Complete. |
| `Schmaloogium/.github/workflows/release-to-cf-mr.yml` | Complete. |
| `Schmaloogium/README.md` | Complete. |
| `Schmaloogium/.gitignore` | Complete. |
| `Schmaloogium/src/**` | All eight files: `main/java/com/example/modid/{ExampleMod.java, proxy/{IProxy,CommonProxy,ClientProxy}.java}`, `main/java-templates/com/example/modid/Reference.java`, `main/resource-templates/{mcmod.info,pack.mcmeta}`, `main/resources/modid_at.cfg`. |
| MCP `cleanroom` | `get_porting_guide("mixin-setup")`, `get_project_template("mixins.json")`, `get_project_template("checklist")`, `explain_concept("mcmod.info", loader="cleanroom")`. |

### 0.2 Dependency PHASE docs consumed

None. Phase 1 has no dependencies (§G5.1).

### 0.3 Deviations from the assigned reading list, with reasons

1. **`cleanroom-src/src/main/java/com/cleanroommc/` was NOT skimmed.** The spec lists it as
   "skim only" and the Context budget line says "do not spend it exploring cleanroom-src beyond
   the listed skim." Nothing in this phase's scope turned out to need loader-internal boot/mixin
   bootstrap layout: the `MixinConfigs` manifest contract is fully specified by RESEARCH.md §5.1
   and the MCP `mixin-setup` guide, both of which are same-day-fresh and agree. Recorded here as a
   deliberate omission rather than claimed as done. If the Phase 7 spike on CleanMix divergences
   (OQ-4) finds the manifest contract underspecified, that is the session that should read the
   bootstrap.

2. **Web and Maven lookups were performed** beyond the listed inputs, for the OQ-2 re-pin only.
   §G1.1 permits web use when "a listed input is missing or contradictory"; more directly, the
   Phase 1 spec *orders* re-verification ("re-verify the current Cleanroom loader release …
   daily cadence, so check again"). Sources queried, all 2026-07-24:
   - `https://api.github.com/repos/CleanroomMC/Cleanroom/releases` — release list + notes.
   - `https://repo.cleanroommc.com/releases/com/cleanroommc/cleanroom/maven-metadata.xml` — the
     resolvable artifact list and `<release>` marker.
   - `https://maven.arcseekers.com/releases/xyz/wagyourtail/unimined/xyz.wagyourtail.unimined.gradle.plugin/maven-metadata.xml`
     — the kappa-fork version list.
   - `https://maven.wagyourtail.xyz/releases/…` — checked and found to carry only upstream
     (non-kappa) Unimined; recorded in §4.2's pin table because it is a real re-pin trap.

3. **`RESEARCH.md` §4.2 and §7.4 were read beyond the assigned §1/§5.1–§5.3/§6.1/§7.2/§12.2.**
   §4.2 because the facade's shape is constrained by the "use program is the universal state
   barrier" behavior that Phase 4/6 will drive through it; §7.4 because the `mod.compat` bail
   registry is assigned to this phase and §7.4 is where its "detect and disable with a clear
   message" premise lives. Both are one-paragraph reads.

4. **No git operations, builds, tests, or review agents were run** (§G1.1). One read-only
   inspection of the working tree's git status was made and produced a finding recorded in §11.3.

---

## 1. Scope & boundaries

### 1.1 What Phase 1 owns

Phase 1 owns the *frame* that every other phase is built inside, and nothing that happens within
it. Concretely:

- The Gradle module split (`:engine`, `:mod`, `:conformance`) and the package layout inside each.
- The **seam** (D-6) stated as a testable constraint, plus its enforcement mechanism.
- The `engine.gl` facade's **shape**: interface set, handle model, `GLCapabilityProfile`, and the
  recording/replay implementation used for headless tests.
- Template conversion: root package, mod id, Blossom templating, `mcmod.info`/`pack.mcmeta`,
  access-transformer posture.
- The GPL-3.0-or-later license swap (D-7) and the source-header / third-party-notice conventions.
- The version pin table and the re-pin procedure (OQ-2).
- Mixin **wiring** (manifest attribute, config-file layout, SRG policy, refmap handling, dev flags).
- The lwjglx posture (OQ-21).
- The headless JUnit baseline in `:engine` and `:conformance`.
- Logging channel names, debug-flag namespace, and the user-facing error-channel convention.
- The `mod.compat` bail-registry **mechanism**.
- CI workflow adjustments for the module split, with extension points left for Phase 2.

### 1.2 Adjacent concerns, and who owns them

Every concern this document touches but does not own — the §G9 anti-sprawl device:

| Concern this doc brushes against | Owned by |
|---|---|
| Conformance harness content: scenes, capture drivers, image diff, fixture downloader, golden-file format and update workflow, headless-GL-in-CI viability | **Phase 2** |
| Everything pack-format: discovery, `#include`/preprocessing, option discovery, `shaders.properties` model, identity macros | **Phase 3** |
| Stage registry contents, the 43 program slots, backup-chain semantics, compile/link flow | **Phase 4** |
| All GL *policy*: texture formats, the fixed texture-unit map, ping-pong/flip rules, clear colors, buffer sizing, resize | **Phase 5** (buffers) and **Phase 6** (uniforms/samplers) |
| The Mixin **hook catalog** — which classes, which methods, which `@At` targets (App E) | **Phase 7** (with additions from **Phase 10** and **Phase 13**) |
| GL context creation mechanics, HiDPI, resize (OQ-3); CleanMix divergences on hot injections (OQ-4) | **Phase 7** |
| Coexistence **policy**: which mod ids bail, detection mechanics, the user-visible message text (OQ-5) | **Phase 10** |
| GUI framework evaluation — whether ModularUI is fit for generated screens (OQ-9) | **Phase 12** |
| KHR_debug labels/groups, sampler objects, async compile, GC posture | **Phase 14** |
| Kirino backend port itself (as opposed to the seam that makes it possible) | **G8/S5** |

### 1.3 A note on what "foundation" does *not* mean here

Phase 1 does not pre-decide anything a later phase is assigned. Where this document names a type
that a later phase will fill (`StageRegistry`, `PackConfiguration`), it names only the *package it
lives in*, never its contents. The one place this rule is deliberately stretched is the
`engine.gl` facade, because the spec assigns its design here and every later phase's headless tests
depend on it existing.

---

## 2. Architecture overview

### 2.1 The three modules

The §G3.1 layout, refined with concrete Gradle project paths, source roots, and Java packages.
Names in §G3.1 are preserved verbatim; this section adds `engine.log`, `engine.diag`, and the
`.internal` convention, which are refinements the spec's "everything else in G3 is yours to
refine" clause permits.

```
Schmaloogium/                    (root Gradle project — aggregator only, no code)
├── engine/                      :engine
│   └── src/{main,test}/java/com/schmaloogium/engine/…
├── mod/                         :mod
│   ├── src/main/java/com/schmaloogium/mod/…
│   ├── src/main/java-templates/com/schmaloogium/Reference.java
│   ├── src/main/resource-templates/{mcmod.info,pack.mcmeta}
│   └── src/main/resources/{schmaloogium.*.mixin.json,assets/…}
└── conformance/                 :conformance
    └── src/{main,test}/java/com/schmaloogium/conformance/…
```

**`:engine`** — pure JVM. Java 25. Zero dependencies on Minecraft, Forge, Cleanroom, Mixin, or
LWJGL. Testable headless with JUnit alone.

| Package | Contents | Filled by |
|---|---|---|
| `com.schmaloogium.engine.pack` | pack discovery, file model, dimension folders, sources | Phase 3 |
| `com.schmaloogium.engine.preprocess` | `#include`, macro header, preprocessor, option discovery/rewrite | Phase 3 |
| `com.schmaloogium.engine.config` | `shaders.properties` model, options/profiles/screens, ID-file grammar, persistence | Phase 3 (+12) |
| `com.schmaloogium.engine.registry` | stage registry (modern-superset shape), program slots, backup chains, per-program state | Phase 4 |
| `com.schmaloogium.engine.buffers` | framebuffer/color-buffer *policy* — ping-pong, flips, clears, formats, sizing | Phase 5 |
| `com.schmaloogium.engine.uniforms` | built-in uniform model, cadences, smoothing math, value-provider interfaces | Phase 6 |
| `com.schmaloogium.engine.expr` | custom-uniform expression language | Phase 11 |
| `com.schmaloogium.engine.gl` | **the GL facade** — interfaces, `GLCapabilityProfile`, recording/replay impl | **Phase 1** |
| `com.schmaloogium.engine.log` | the zero-dependency `Log`/`LogSink` SPI and channel constants | **Phase 1** |
| `com.schmaloogium.engine.diag` | `EngineDiagnostic` and the user-facing-channel vocabulary | **Phase 1** |

**`:mod`** — the Cleanroom mod. Depends on `:engine`.

| Package | Contents | Filled by |
|---|---|---|
| `com.schmaloogium.mod.core` | `@Mod` entry, lifecycle, config, engine bootstrapping | Phase 1 (skeleton) / Phase 7 |
| `com.schmaloogium.mod.glue` | adapters: world-state sampling, Forge registries, resources, **the LWJGL3 implementation of `engine.gl`** | Phases 1 (facade impl shape), 6, 7, 9 |
| `com.schmaloogium.mod.mixin` | all Mixin classes, SRG-targeted, declared via the `MixinConfigs` manifest attribute | Phases 7, 10, 13 |
| `com.schmaloogium.mod.gui` | pack selection + options screens | Phase 12 |
| `com.schmaloogium.mod.compat` | coexistence detection, **bail registry** | Phase 1 (mechanism) / Phase 10 (policy) |

**`:conformance`** — the Phase 2 harness. Depends on `:engine`. Never ships in the mod jar.
Phase 1 stands up the module, its JUnit wiring, and its dependency edge; Phase 2 fills it.

### 2.2 The dependency graph, and the seam

```
:conformance ──→ :engine ←── :mod
                    ↑            ↑
              (no MC, ever)   (all MC lives here)
```

The §G3.1 constraint, quoted exactly and adopted unchanged as this phase's central deliverable:

> **`:engine` compiles with no classpath entry from Minecraft/Forge/Cleanroom/Mixin/LWJGL, and
> `:mod` never reaches into `:engine` internals beyond its published interfaces.**

§4.3 turns each half of that sentence into a mechanically checkable test.

**Why this is a requirement and not hygiene** `[V:web §5.2]` `[Q:OQ-20]`. RESEARCH.md §5.2
confirms Kirino-Engine as a real CleanroomMC artifact (393 commits, updated 2026-07-24) whose
README states it will "not be compatible with existing render mods" — it replaces the whole
pipeline. RESEARCH.md §7.2 states the consequence directly: "the render backend under Schmaloogium
may be replaced wholesale within the mod's lifetime — the core must survive a backend swap." The
seam is therefore load-bearing against the project's highest-weight strategic risk, not a code-style
preference. That is why §4.3 spends three enforcement layers on it and why §10.3 specifies a
backend-swap drill rather than treating OQ-20 as somebody else's problem.

### 2.3 Naming: root package and mod id

`[D-P1-1]` `mod_id = schmaloogium`, `root_package = com.schmaloogium`.

The template derives the generated `Reference` class's package as `"${root_package}.${mod_id}"`
`[V:template build.gradle blossom block]`, which for these values would produce
`com.schmaloogium.schmaloogium`. `:mod`'s Blossom block therefore **overrides the `package`
property to `root_package` alone**, so `Reference` lands at `com.schmaloogium.Reference` — a single
class at the namespace root, shared by all three modules' notion of "the mod's identity", and
consistent with the `com.schmaloogium.{engine,mod,conformance}` tree. Publishing follows: group
`com.schmaloogium`, artifact `schmaloogium` (the template's `publishing.gradle` already does
`setGroupId(root_package)` / `setArtifactId(mod_id)` `[V:template]`).

### 2.4 Key types introduced by this phase

| Type | Module | Role |
|---|---|---|
| `GLDevice` | `engine.gl` | Root facade handle; hands out the seven services and the capability profile |
| `GLCapabilityProfile` | `engine.gl` | Immutable value object; the §4.1 probe set + extension set; serializable as a test fixture |
| `ProgramHandle`, `ShaderHandle`, `TextureHandle`, `FramebufferHandle`, `UniformLocation` | `engine.gl` | Opaque handles — the engine never holds a raw GL int |
| `RecordingGLDevice`, `GLCallLog`, `GLCall`, `ScriptedResponses`, `ReplayAssertions` | `engine.gl.record` | The headless test backend |
| `Log`, `LogSink`, `LogChannels` | `engine.log` | Zero-dependency logging SPI + the fixed channel name list |
| `EngineDiagnostic`, `DiagnosticSeverity`, `UserChannel` | `engine.diag` | Loader-neutral error records that `:mod` routes to chat / GUI / log |
| `Lwjgl3GLDevice` (+ its service impls) | `mod.glue` | The only place in the codebase that may call LWJGL |
| `CapabilityProbe` | `mod.glue` | Builds a `GLCapabilityProfile` from a live context; dumps fixtures |
| `CompatCheck`, `CompatVerdict`, `BailRegistry` | `mod.compat` | The bail mechanism (policy is Phase 10) |
| `SchmaloogiumMixinPlugin` | `mod.mixin` | Reserved `IMixinConfigPlugin` slot on the MOD-phase config |

---

## 3. Contract conformance map

Phase 1 owns almost no pack-facing contract surface — the pack contract (RESEARCH.md §3, Apps A–F)
belongs to Phases 3–13. The in-scope contract rows for this phase are the ones the facade and the
debug affordances must satisfy, plus the vocabulary rule.

| Contract item | Provenance | Design element satisfying it | Tag |
|---|---|---|---|
| Startup probes **GL version** | RESEARCH.md §4.1 step 1 | `GLCapabilityProfile.glVersionMajor` / `.glVersionMinor`, plus `atLeast(int,int)` | `[V:observed]` |
| Startup probes **`GL_MAX_DRAW_BUFFERS`** | RESEARCH.md §4.1 step 1 | `GLCapabilityProfile.maxDrawBuffers` | `[V:observed]` |
| Startup probes **`GL_MAX_COLOR_ATTACHMENTS`** | RESEARCH.md §4.1 step 1 | `GLCapabilityProfile.maxColorAttachments` | `[V:observed]` |
| Startup probes **`GL_MAX_TEXTURE_IMAGE_UNITS`** | RESEARCH.md §4.1 step 1 | `GLCapabilityProfile.maxTextureImageUnits` | `[V:observed]` |
| **Mipmap generation requires GL 3.0** | RESEARCH.md §4.1 step 1 | `GLCapabilityProfile.supportsMipmapGeneration()` — derived, `atLeast(3,0)`; consumed by Phase 5's composite-mipmap policy | `[V:observed]` |
| Extension set available to the engine | DESIGN.md Phase 1 scope (additive to §4.1 — see the flagged delta below) | `GLCapabilityProfile.extensions()` + `hasExtension(String)`; the consumer is Phase 3's on-demand `MC_<GL_extension>` macros (§3.5) | `[A]` |
| `shaders.debug.save` equivalent — dump processed sources | RESEARCH.md App F.8; DESIGN.md §G4.5 | `-Dschmaloogium.debug.saveSources` reserved in §4.7's flag namespace; the dump itself is Phase 3's | `[V:doc]` |
| **Compat-profile baseline; `GL_QUADS` stays available** | RESEARCH.md §6.1 `[D-9]` | The facade is profile-agnostic by construction: `DrawService` exposes a `fullscreenQuad` primitive whose backend chooses `GL_QUADS` or the triangle-strip fallback. No core-profile-only entry point appears in any interface | `[V:doc]` |
| **No UBOs**; per-program uniform upload with location caching | RESEARCH.md §6.1 `[V:doc]` | `UniformService` exposes only default-block uniform uploads keyed by `UniformLocation`. No uniform-block entry point exists — the facade cannot express a UBO | `[V:doc]` |
| **Never compile against `org.lwjglx`** | RESEARCH.md §6.1, DESIGN.md §G2.2 | `enable_lwjglx=false` (§4.6) plus the §4.3 bytecode assertion, which lists `org.lwjglx` among the forbidden prefixes | `[V:mcp]` |
| **All engine GL goes through the facade**; no direct LWJGL outside `mod.glue` | DESIGN.md §G4.6 | The §4.3 bytecode assertion enforces the `:engine` half mechanically. The `mod.glue`-only half is a convention plus a `:mod` scan restricted to `org.lwjgl` references outside `com.schmaloogium.mod.glue` | `[V:doc]` |
| Pack-facing vocabulary used **verbatim** in identifiers | DESIGN.md §G4.1 | Naming convention recorded in §4.9; the facade deliberately contains no pack vocabulary at all (it is below that layer), so no synonym risk is introduced here | `[V:doc]` |

### 3.1 Flagged delta (reported, not smoothed over — §G1.1)

**DESIGN.md's Phase 1 scope adds "extension set" to `GLCapabilityProfile`; RESEARCH.md §4.1 does
not list it among the probes.** RESEARCH.md §4.1 names four probes (GL version, `GL_MAX_DRAW_BUFFERS`,
`GL_MAX_COLOR_ATTACHMENTS`, `GL_MAX_TEXTURE_IMAGE_UNITS`) plus the GL-3.0 mipmap gate. DESIGN.md
line 614 says "GL version, max draw buffers, max color attachments, max texture units, extension
set — the §4.1 probe set", attributing the extension set to §4.1.

**Ruling:** include the extension set. Per §G0.1, RESEARCH.md wins on conflict — but this is an
*addition*, not a contradiction: §4.1 describes what the reference implementation probes at startup,
while §3.5 independently requires that `MC_<GL_extension>` macros be emitted on demand, which is
impossible without an extension set. The DESIGN.md attribution is loose; the requirement is real and
sourced elsewhere in RESEARCH.md. Recorded as `[A]` provenance rather than `[V:observed]`, so no
later reader mistakes it for observed reference behavior. Also recorded in §11.3.

---

## 4. Detailed design

### 4.1 Template ground truth this design is written against

Everything below is `[V:template]`, read from the checkout on 2026-07-24. It matters because the
spec asks for a plan "against the template's actual build scripts", and several of these facts
change what the plan has to do.

| Fact | Value | Consequence |
|---|---|---|
| Branch | **`main`**, not `mixin` | There is **no** mixin config JSON, **no** `MixinConfigs` manifest attribute, **no** `mixin { }` or refmap block, and no mixinbooter dependency anywhere. All Mixin wiring in §4.5 is authored from nothing. `compileOnly "com.cleanroommc:sponge-mixin:0.20.13+mixin.0.8.7"` is the only mixin-adjacent line. |
| Build shape | Single project. `settings.gradle` has **no `include` lines**; `rootProject.name = rootProject.projectDir.getName()` | The module split is a genuine restructuring, not a reconfiguration. |
| Unimined block | `unimined.minecraft { version "1.12.2"; mappings { mcp("stable","39-1.12") }; cleanroom { loader "0.5.17-alpha"; … } }` at the **root** project | Must move wholesale into `:mod`. The loader version is an **inline literal** — §4.2 promotes it to a property. |
| Access-transformer wiring | `cleanroom { accessTransformer "${rootProject.projectDir}/src/main/resources/$access_transformer_locations" }` | Hardcodes `rootProject.projectDir` — the single most module-split-hostile line in the build. Defused by `use_access_transformer=false` (§4.4); the one-line fix is recorded for the phase that first needs an AT. |
| Blossom | `net.kyori.blossom` 2.2.0 on `sourceSets.main` only; convention dirs `src/main/java-templates` and `src/main/resource-templates`; `{{ token }}` syntax; java property `package` = `"${root_package}.${mod_id}"` | Must be re-declared per source set in `:mod`. The `package` derivation needs the §2.3 override. |
| `jar` manifest | `doFirst` writes `ModType=CRL` always; `ContainedDeps`/`NonModDeps` if `contain` non-empty; `FMLCorePlugin`/`FMLCorePluginContainsFMLMod` if `is_coremod`; `FMLAT` if `use_access_transformer` | This is the block §4.5 extends with `MixinConfigs`. |
| `contain` configuration | Custom config; `implementation.extendsFrom(contain)`; `jar { into('/') { from configurations.contain } }` + `ContainedDeps`/`NonModDeps` attrs | CRL jar-in-jar. Considered and rejected for `:engine` (§4.2.5). |
| Shadow | `com.gradleup.shadow` 9.5.1, `enable_shadow=false` ⇒ `shadowJar.enabled=false`; remap task selected as `enable_shadow ? remapShadowJar : remapJar` | Considered and rejected for `:engine` (§4.2.5). Active remap task is `remapJar`. |
| Artifacts | `jar` → classifier `dev`, `finalizedBy(remapJar)`; `remapJar` → the production jar | `:engine` merging must happen in `jar`, i.e. **before** remap. |
| JUnit | `enable_junit_testing=true` ⇒ `junit-jupiter:6.0.3` + `junit-platform-launcher`; `test { useJUnitPlatform(); javaLauncher = 25 }` | Reusable verbatim in all three modules. **No `src/test/` exists yet.** |
| Java | `java.toolchain.languageVersion = 25`, foojay resolver 1.0.0, explicit `VERSION_25` on `compileJava`/`compileTestJava`, UTF-8 everywhere | Moves to a root `subprojects {}` block. |
| Gradle | wrapper `9.6.1`; all three CI workflows pin `gradle-version: 9.6.1` and Temurin 25 | Unchanged by the split. |
| lwjglx | exactly one site: `if (enable_lwjglx.toBoolean()) { compileOnly "com.cleanroommc:lwjglx:1.0.0" }`; `enable_lwjglx = true` | **compileOnly only** — no runtime injection, no run-config flag. See §4.6. |
| `.gitignore` | already contains `**/build/` | No change needed for subproject build dirs. |
| CI artifact paths | all three workflows reference `build/libs` (root-relative) | Breaks under the split — §4.9. |

### 4.2 The Gradle module split

#### 4.2.1 `settings.gradle`

```groovy
pluginManagement {
    repositories { /* unchanged — gradlePluginPortal, mavenCentral, forge,
                      fabric, wagyourtail releases, arcseekers releases,
                      wagyourtail snapshots */ }
}

plugins {
    id 'org.gradle.toolchains.foojay-resolver-convention' version '1.0.0'
}

rootProject.name = 'Schmaloogium'

include ':engine'
include ':mod'
include ':conformance'
```

`[D-P1-2]` **`rootProject.name` is pinned to the literal `'Schmaloogium'`** rather than derived from
the directory name. The template's derivation exists to work around an IntelliJ bug `[V:template
comment]`, but under a multi-project build the root name leaks into IDEA module keys and into
`publishing`; a literal removes the "clone into a differently-named directory and the build
changes" failure mode. The arcseekers repository entry stays — it is where the kappa fork lives
(§4.2.6).

#### 4.2.2 Root `build.gradle` — aggregator only

The root project holds **no code, no source sets, and no Unimined**. It declares plugin versions
once (so Gradle resolves the plugin classpath a single time) and applies the common Java
configuration to subprojects.

```groovy
plugins {
    id 'com.gradleup.shadow'                     version '9.5.1'   apply false
    id 'org.jetbrains.gradle.plugin.idea-ext'    version '1.4.1'
    id 'xyz.wagyourtail.unimined'                version '1.4.26-kappa' apply false
    id 'net.kyori.blossom'                       version '2.2.0'   apply false
}

allprojects {
    group   = root_package          // com.schmaloogium
    version = mod_version
}

subprojects {
    apply plugin: 'java-library'

    java {
        toolchain { languageVersion = JavaLanguageVersion.of(25) }
    }

    tasks.withType(JavaCompile).configureEach {
        options.encoding = 'UTF-8'
        sourceCompatibility = targetCompatibility = JavaVersion.VERSION_25
    }

    if (enable_junit_testing.toBoolean()) {
        dependencies {
            testImplementation 'org.junit.jupiter:junit-jupiter:6.0.3'
            testRuntimeOnly    'org.junit.platform:junit-platform-launcher'
        }
        tasks.named('test') {
            useJUnitPlatform()
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion = JavaLanguageVersion.of(25)
            })
            if (show_testing_output.toBoolean()) {
                testLogging { showStandardStreams = true }
            }
        }
    }
}

apply from: 'gradle/scripts/extra.gradle'
```

`idea-ext` stays applied at root because the `idea.project.settings` block is a root-only concept;
its `runConfigurations` entries are retargeted at `:mod`'s tasks (`:mod:runClient`,
`:mod:runServer`). The template's `moduleJavacAdditionalOptions` key `project.name + '.main'` becomes
per-module keys (`engine.main`, `mod.main`, `conformance.main`) — recorded because it is a silent
breakage otherwise.

#### 4.2.3 `:engine/build.gradle` — the seam, by construction

```groovy
// SPDX-License-Identifier: GPL-3.0-or-later
repositories {
    mavenCentral()
}

dependencies {
    // Production dependencies: NONE. This is the seam (D-6).
    // Test-only tooling is permitted; see the forbidden-coordinate list
    // in the architecture test.
    testImplementation 'org.ow2.asm:asm:<pinned>'   // bytecode scan, test scope only
}

// Hand the architecture test the exact classpath it must assert over.
tasks.named('test') {
    systemProperty 'schmaloogium.test.compileClasspath',
        sourceSets.main.compileClasspath.asPath
    systemProperty 'schmaloogium.test.runtimeClasspath',
        sourceSets.main.runtimeClasspath.asPath
    systemProperty 'schmaloogium.test.classesDir',
        sourceSets.main.output.classesDirs.asPath
}
```

That is the whole file. **No `unimined` plugin, no `blossom`, no `shadow`, no
`gradle/scripts/dependencies.gradle`.** This is the structural half of the enforcement: Unimined is
what injects the Minecraft configuration and the loader dependencies into a project, so a project
that never applies it structurally cannot have them. The architecture test in §4.3 exists to prove
that a future edit has not quietly undone it.

ASM appears only in `testImplementation`. It is not a forbidden coordinate (it is not Minecraft,
Forge, Cleanroom, Mixin, or LWJGL), and it never reaches production scope — the architecture test
asserts over `sourceSets.main`, not the test classpath. `[D-P1-3]`

#### 4.2.4 `:mod/build.gradle` — everything loader-facing

All the machinery the template put at root moves here verbatim, with four changes:

1. **The loader pin reads a property**: `cleanroom { loader cleanroom_loader_version }`.
2. **The AT path is project-relative** when it is eventually enabled:
   `"${project.projectDir}/src/main/resources/$access_transformer_locations"` — the fix for the
   `rootProject.projectDir` hardcode. Inert for v0.1 (§4.4).
3. **Blossom is re-declared** for `:mod`'s own `sourceSets.main`, with the §2.3 `package` override:

   ```groovy
   sourceSets.main {
       blossom {
           javaSources {
               property('mod_id',      mod_id)
               property('mod_name',    mod_name)
               property('mod_version', mod_version)
               property('package',     root_package)   // NOT "${root_package}.${mod_id}"
           }
           resources { /* the template's nine resource properties, unchanged */ }
       }
   }
   ```

4. **`:engine` is merged into the jar** (§4.2.5).

```groovy
dependencies {
    implementation project(':engine')
}

jar {
    archiveClassifier = 'dev'
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from project(':engine').sourceSets.main.output
    doFirst {
        manifest {
            def attrs = [:]
            attrs['ModType']      = 'CRL'
            attrs['MixinConfigs'] = mixin_configs        // §4.5
            if (use_access_transformer.toBoolean()) {
                attrs['FMLAT'] = access_transformer_locations
            }
            attributes(attrs)
        }
    }
    finalizedBy(tasks.named(remapTaskName).get())
}
```

The `contain`/`shadow`/coremod branches of the template's manifest block are retained but stay
inert (`enable_shadow=false`, `is_coremod=false`, empty `contain`), so re-enabling any of them later
is a property flip rather than a rewrite.

`gradle/scripts/dependencies.gradle` and `gradle/scripts/publishing.gradle` are applied **from
`:mod` only**. `dependencies.gradle` carries `compileOnly sponge-mixin` and the (now-disabled)
lwjglx line, both of which are meaningless outside `:mod`. `publishing.gradle` publishes the mod
artifact; `:engine` is not published separately (it is not a library anyone else consumes — it ships
inside the mod jar).

#### 4.2.5 Jar packaging: how `:engine` reaches the shipped jar

`[D-P1-4]` **`:engine`'s compiled classes are merged directly into `:mod`'s jar**, via
`jar { from project(':engine').sourceSets.main.output }`, executed **before** `remapJar` (the `jar`
task is `finalizedBy(remapJar)` in the template, so ordering is already correct).

The spec asks explicitly: "does `:engine` shade into the mod jar via the template's `contain`
configuration?" Answer: no, and here is the reasoning, because a later phase may want to revisit it.

| Option | Verdict |
|---|---|
| **Merge classes into the mod jar** (chosen) | One flat jar. No relocation question — `:engine` is our own code under the same GPL-3.0-or-later license and the same package root, so there is nothing to isolate. `remapJar` is a structural no-op over `:engine` classes because they reference no Minecraft type (which the §4.3 test proves), so passing them through the remapper is harmless. |
| `contain` (CRL jar-in-jar) | Rejected. The template's `contain` copies whole jars to the archive root and sets `ContainedDeps`/`NonModDeps`, which the loader extracts at mod-load time `[V:template]`. That machinery exists for third-party jars whose identity must be preserved; using it for a first-party module adds a load-time extraction step, an extra manifest contract, and a second classloading path, for no benefit. |
| Shadow plugin | Rejected. Flipping `enable_shadow=true` switches the active remap task to `remapShadowJar` and introduces a shadow↔remap interaction the template currently keeps disabled, with no relocation actually needed. Strictly more risk than the merge. |

`:conformance` is never packaged. It has no place in the mod jar and no publication.

#### 4.2.6 Pin table (OQ-2)

All values **re-verified 2026-07-24** for this document. The spec's prediction held: `0.6.6-alpha`
was still current at re-verification time, but two releases (`0.6.5-alpha`, `0.6.6-alpha`) shipped
that same day, which is the cadence evidence, not a counterexample.

| Component | Pinned value | Where it lives after the split | Repository | Re-verified |
|---|---|---|---|---|
| **Cleanroom loader** | **`0.6.6-alpha`** | `gradle.properties` → `cleanroom_loader_version`, read by `:mod`'s `cleanroom { loader … }` | `https://repo.cleanroommc.com/releases` (injected by Unimined; note `maven.cleanroommc.com` **301-redirects** here) | 2026-07-24 — GitHub releases API and `maven-metadata.xml` `<release>` agree |
| Unimined (kappa fork) | `1.4.26-kappa` | root `build.gradle` plugins block | `https://maven.arcseekers.com/releases` | 2026-07-24 — newest kappa build; **trap:** `maven.wagyourtail.xyz/releases` carries only upstream Unimined, topping out at `1.4.1`, with **zero** kappa versions. Do not "upgrade" to 1.4.1. |
| Gradle | `9.6.1` | `gradle/wrapper/gradle-wrapper.properties` + all three CI workflows | services.gradle.org | 2026-07-24 (template value, unchanged) |
| Java toolchain | `25` | root `subprojects {}` + CI `setup-java` | Temurin via foojay resolver `1.0.0` | 2026-07-24 (template value, unchanged) |
| Mappings | MCP `stable`, `39-1.12` | `:mod` Unimined `mappings { }` | Unimined-managed | 2026-07-24 (template value, unchanged) |
| Mixin (compile-time) | `com.cleanroommc:sponge-mixin:0.20.13+mixin.0.8.7` | `:mod` via `dependencies.gradle`, `compileOnly` | CleanroomMC maven | 2026-07-24 (template value, unchanged) |
| Blossom | `2.2.0` | root plugins block, applied in `:mod` | gradlePluginPortal | 2026-07-24 |
| Shadow | `9.5.1` | root plugins block, `apply false` (inert) | gradlePluginPortal | 2026-07-24 |
| idea-ext | `1.4.1` | root plugins block | gradlePluginPortal | 2026-07-24 |
| foojay resolver | `1.0.0` | `settings.gradle` | gradlePluginPortal | 2026-07-24 |
| JUnit Jupiter | `6.0.3` | root `subprojects {}` | mavenCentral | 2026-07-24 |
| ASM (test-only, `:engine`) | to be pinned at implementation time | `:engine` `testImplementation` | mavenCentral | — |
| lwjglx | **dropped** (`enable_lwjglx=false`) | — | — | see §4.6 |
| ModularUI | **not pinned by this phase** | — | — | Phase 12 owns the dependency decision (OQ-9) |

**Nothing floats.** No dynamic versions (`+`, `latest.release`), no version ranges, no
`mavenLocal()`-sourced snapshots in any module. `dependencies.gradle` retains the template's
`mavenLocal()` entry, which stays last as the template comment requires, and is used for local
debugging only.

**The re-pin procedure** (this *is* the OQ-2 deliverable; the spike spec in §10.1 restates it in
§G4.4 form):

1. **Trigger.** Before every milestone tag (v0.1 … v0.5), before any release workflow run, and
   whenever a platform-caused failure is suspected. Never on a schedule, and never automatically —
   §G2.2 says versions are "pinned by Phase 1 and re-verified deliberately, never floated."
2. **Query.** `GET https://repo.cleanroommc.com/releases/com/cleanroommc/cleanroom/maven-metadata.xml`
   and read `<release>`. Cross-check against
   `GET https://api.github.com/repos/CleanroomMC/Cleanroom/releases?per_page=10` — the maven
   metadata is authoritative for *resolvability*, the GitHub API for *release notes*. A tag that
   appears in one and not the other is itself a finding.
3. **Read the delta.** Diff release notes from the current pin forward. Flag any mention of:
   CleanMix, MixinBooter, Foundation, classloader, mod discovery, LWJGL, or the render path. These
   are the categories that have historically moved (the 0.6.0→0.6.6 window contained a MixinBooter-11
   parity change, two CleanMix updates, a mixin-loading bug fix, and a Foundation classloader change).
4. **Bump.** Edit `cleanroom_loader_version` in `gradle.properties`. One line, one reviewable diff.
   That is the entire point of promoting it out of `build.gradle`.
5. **Verify.** `./gradlew build` (all modules), `./gradlew :engine:test :conformance:test`, and a
   manual `:mod:runClient` smoke run to the main menu. Once Phase 2's harness exists, its
   runnable-before-renderer subset joins this step.
6. **Record.** Append a row to `PINS.md` (repo root): date, old pin, new pin, notable delta,
   verification result, and the person/session that did it. `PINS.md` is created by the Phase 1
   implementation session with the current row as its first entry.
7. **On failure at step 5:** revert to the last known-good pin, record the failure in `PINS.md` with
   the symptom, and open the question upstream (§7.7 engagement). A broken alpha never blocks a
   milestone — it blocks the *bump*.

`[D-P1-5]` The loader pin is a `gradle.properties` property rather than an inline literal precisely
so that steps 4 and 6 are trivial and auditable.

### 4.3 Seam enforcement — the testable constraint

The §G3.1 sentence has two halves. Each gets its own mechanism.

#### Half one: `:engine` has no MC/Forge/Cleanroom/Mixin/LWJGL classpath

**Layer 1 — by construction.** `:engine/build.gradle` (§4.2.3) applies no Unimined and declares no
forbidden coordinate. This is the primary guarantee; the tests below exist to catch regressions.

**Layer 2 — classpath assertion.** A JUnit test in `:engine`
(`com.schmaloogium.engine.SeamClasspathTest`) reads the `schmaloogium.test.compileClasspath` and
`schmaloogium.test.runtimeClasspath` system properties injected by the build (§4.2.3), splits them
on the path separator, and asserts that no entry's file name matches, case-insensitively, any of:

```
minecraft   forge      cleanroom    unimined
mixin       spongepowered           mixinextras
lwjgl       lwjglx     fmlcore      launchwrapper
```

Failure message names the offending entry and the configuration it came from, so the diagnosis is
immediate.

**Layer 3 — bytecode assertion (the one the Impl gate names).** A JUnit test
(`com.schmaloogium.engine.SeamBytecodeTest`) walks every `.class` file under
`schmaloogium.test.classesDir`, reads each class's constant pool with ASM, and collects every
referenced type name. It asserts that no referenced type starts with any of:

```
net.minecraft.        net.minecraftforge.   com.cleanroommc.
org.spongepowered.    org.lwjgl             org.lwjglx
zone.rong.mixinbooter cpw.mods.
```

This is strictly stronger than Layer 2: a compile-time-only leak, a reflective string constant that
happens to be a type name, or a dependency that arrives transitively through a future edit all show
up here. It is also the layer that survives a build-script refactor, because it asserts over the
*artifact*, not the configuration.

**Why both layers.** Layer 2 catches "someone added a dependency"; Layer 3 catches "someone wrote
code against a type that arrived some other way". Neither subsumes the other, and each produces a
different, actionable failure message.

#### Half two: `:mod` never reaches into `:engine` internals

`[D-P1-6]` **The mechanism is a package-naming convention, enforced by a mirror bytecode scan — not
the Java Platform Module System.**

The convention: within `:engine`, any package segment named `internal` is off-limits to `:mod`.
Public API lives at `com.schmaloogium.engine.<subsystem>.*`; implementation details that must be
package-visible across a subsystem live at `com.schmaloogium.engine.<subsystem>.internal.*`.

The test: `com.schmaloogium.mod.SeamInternalsTest` in `:mod` scans `:mod`'s compiled classes for any
referenced type matching `com\.schmaloogium\.engine\..*\.internal\..*` and fails with the referencing
class named.

**JPMS was considered and rejected.** A `module-info.java` in `:engine` exporting only API packages
would be a genuinely structural guarantee — the compiler would refuse the reference outright. But
`:mod` runs under Cleanroom's Foundation classloader (the LaunchWrapper replacement, RESEARCH.md
§5.1), on a flat classpath assembled by the loader, where the module graph does not exist. Putting
`:engine` on the module path while `:mod` is on the classpath makes `:engine` an automatic module at
runtime with all packages open, which means the guarantee evaporates exactly where it would matter
while adding real build complexity. The bytecode scan gives the same enforcement at the same moment
(build time) with none of the runtime risk.

A third, softer layer: `:mod` also gets a scan asserting that no `org.lwjgl*` reference appears
outside `com.schmaloogium.mod.glue`, which is the mechanical half of §G4.6's "no direct LWJGL calls
outside `mod.glue`'s facade implementation."

#### The constraint, restated for later phases to inherit

> **C-1** `:engine`'s `main` compile and runtime classpaths contain no artifact matching the
> forbidden-coordinate list, and `:engine`'s compiled classes reference no type under
> `net.minecraft.`, `net.minecraftforge.`, `com.cleanroommc.`, `org.spongepowered.`, `org.lwjgl`,
> `org.lwjglx`, `zone.rong.mixinbooter`, or `cpw.mods.`.
>
> **C-2** `:mod`'s compiled classes reference no type matching
> `com.schmaloogium.engine.*.internal.*`.
>
> **C-3** `:mod`'s compiled classes reference no type under `org.lwjgl` outside the package
> `com.schmaloogium.mod.glue`.
>
> **C-4** `:conformance` depends on `:engine` and never on `:mod`.

C-1 is the Impl gate's "architecture test proving `:engine` has no MC/loader/mixin/LWJGL classpath."
C-1 through C-4 are non-negotiable for every later phase; a phase that needs to violate one has
found a design error and must flag it, not work around it.

### 4.4 Template conversion

#### 4.4.1 `gradle.properties`

| Property | Template | Schmaloogium | Note |
|---|---|---|---|
| `mod_id` | `modid` | `schmaloogium` | |
| `mod_name` | `Mod Name` | `Schmaloogium` | |
| `root_package` | `com.example` | `com.schmaloogium` | §2.3 |
| `mod_version` | `1.0.0` | `0.1.0` | SemVer; v0.1 is the first milestone, and shipping `1.0.0` before the pack matrix is met would be dishonest |
| `mod_description` | *(empty)* | one line: OptiFine/Iris-format shader-pack support for Cleanroom on 1.12.2 | mirrors RESEARCH.md §1.1 |
| `mod_url` | *(empty)* | the GitHub repo URL | |
| `mod_authors` | *(empty)* | populated | |
| `mod_credits` | *(empty)* | includes the license statement — see §4.8 | |
| `use_access_transformer` | `true` | **`false`** | §4.4.3 |
| `enable_lwjglx` | `true` | **`false`** | §4.6 |
| `is_coremod` | `false` | `false` (unchanged) | §4.5 |
| `enable_shadow` | `false` | `false` (unchanged) | §4.2.5 |
| `enable_junit_testing` | `true` | `true` (unchanged) | |
| `cleanroom_loader_version` | *(absent — inline literal)* | **`0.6.6-alpha`** | new property, §4.2.6 |
| `mixin_configs` | *(absent)* | **`schmaloogium.preinit.mixin.json,schmaloogium.default.mixin.json,schmaloogium.mod.mixin.json`** | new property, §4.5 |

`publish_to_local_maven` is documented in the template's `gradle.properties` but **read by no
script** `[V:template]` — recorded in §11.3 as a template defect; either wire it or delete it.

#### 4.4.2 Source-tree conversion

- `src/main/java/com/example/modid/ExampleMod.java` → `mod/src/main/java/com/schmaloogium/mod/core/SchmaloogiumMod.java`.
  The template's version calls `Minecraft.getMinecraft().getLanguageManager()` in a `@Mod` class
  shared with the server `[V:template]`; the replacement does not, because Schmaloogium is
  client-only (§1.2 non-goals: "Server-side anything").
- `src/main/java/com/example/modid/proxy/{IProxy,CommonProxy,ClientProxy}.java` → retained in
  `com.schmaloogium.mod.core.proxy` with the same three-type shape. `@SidedProxy` is how a
  client-only 1.12.2 mod keeps its client code off the server's classloading path; deleting the
  proxy split would be a regression, not a simplification.
- `src/main/java-templates/com/example/modid/Reference.java` →
  `mod/src/main/java-templates/com/schmaloogium/Reference.java`, unchanged in content (the `{{ package }}`,
  `{{ mod_id }}`, `{{ mod_name }}`, `{{ mod_version }}` tokens all still apply; only the `package`
  property's *value* changes, per §2.3).
- `src/main/resource-templates/mcmod.info` → `mod/src/main/resource-templates/mcmod.info`, content
  unchanged (all nine tokens are still correct); the *values* come from `gradle.properties`.
- `src/main/resource-templates/pack.mcmeta` → `mod/src/main/resource-templates/pack.mcmeta`,
  unchanged.
- `src/main/resources/modid_at.cfg` → **deleted** (§4.4.3).
- New: `mod/src/main/resources/schmaloogium.{preinit,default,mod}.mixin.json` (§4.5).

#### 4.4.3 Access transformers: none for v0.1

`[D-P1-7]` **`use_access_transformer = false`; `modid_at.cfg` is deleted.**

The spec says "decide whether ATs are needed at all for v0.1 — prefer none until a hook requires
one." No component designed in this phase needs one, and Phase 1 cannot know what Phase 7's hook
catalog will need. Shipping the template's example AT (`public net.minecraft.client.Minecraft
fileResourcepacks # Example mcp name AT entry` `[V:template]`) would widen a vanilla field for no
reason and set the `FMLAT` manifest attribute pointlessly.

What is recorded so a later phase can turn ATs on in minutes rather than rediscovering the wiring:

- Three coupled pieces exist `[V:template]`: `ext.access_transformer_locations = "${mod_id}_at.cfg"`;
  the Unimined `cleanroom { accessTransformer … }` call; and
  `processResources { rename '(.+_at.cfg)', 'META-INF/$1' }`, with `FMLAT` naming the file in the
  manifest.
- **The path in the Unimined call must be changed** from `${rootProject.projectDir}/src/main/resources/…`
  to `${project.projectDir}/src/main/resources/…` when it moves into `:mod`. This is done as part of
  the split (§4.2.4) even though the branch is inert, so the trap is disarmed in advance rather than
  waiting to bite Phase 7.
- ATs are written in **MCP** names and remapped to SRG by Unimined at build `[V:template README, V:mcp]`.
- The standing rule from the MCP guide, adopted: **never remove `final` via a mixin — use the AT.**

#### 4.4.4 `mcmod.info` and `pack.mcmeta`

`mcmod.info` is 1.12.2 mod metadata (not `mods.toml`) `[V:mcp]`, read by Forge/Cleanroom for the
in-game mod list. The MCP `explain_concept("mcmod.info")` recipe describes the schema and **names no
`license` key**; the 1.12.2 schema's fields are `modid`, `name`, `version`, `mcversion`,
`description`, `authorList`, `credits`, `url`, `updateJSON`, `logoFile` — which is exactly the
template's token set `[V:template]`.

`[D-P1-8]` **License is stated in `mod_credits` and in `LICENSE`/`README.md`, not in a `mcmod.info`
`license` key**, because no such key is part of the schema and inventing one would be metadata that
nothing reads. Recorded as a limitation rather than papered over: a user browsing the in-game mod
list sees the license only if they read the credits line.

`pack.mcmeta` (`pack_format: 3`) is unchanged — it makes the mod jar a valid resource pack, which
Phase 13's texture work will need.

### 4.5 Mixin wiring

**No mixin classes are authored by this phase.** The hook catalog is Phase 7's (App E). What follows
is the wiring those hooks will land in.

#### 4.5.1 Declaration: the `MixinConfigs` manifest attribute

`[D-P1-9]` Configs are declared with the **`MixinConfigs` jar-manifest attribute**, comma-separated,
written by `:mod`'s `jar` `doFirst` manifest block from the `mixin_configs` property (§4.4.1).

This is current canon and the legacy path is deprecated. RESEARCH.md §5.1: "Configs are declared via
the **`MixinConfigs` jar-manifest attribute**; the legacy MixinBooter loader interfaces are
deprecated." The MCP `mixin-setup` guide is explicit that `IEarlyMixinLoader`, `ILateMixinLoader`,
`IMixinConfigHijacker` and `@MixinLoader` are all `@Deprecated` — "do not use them for new mods."
`[V:mcp]` `[V:web]`

`[D-P1-10]` **`is_coremod` stays `false`.** Cleanroom ships CleanMix built in; the manifest path
needs no coremod. The MCP guide notes the template's mixin branch ships `is_coremod=true` with an
*empty* `IFMLLoadingPlugin` and that whether this is required or vestigial is "unconfirmed upstream",
recommending the manifest path — and RESEARCH.md §5.1 records that coremods "still exist but are
discouraged." Adding a coremod would also brush against D-5 ("no class replacement") by opening a
class-transformation path we have no need for. If a future phase discovers it genuinely needs
transformation before mod construction, that is a flagged decision with its own justification, not a
default.

#### 4.5.2 Config-file layout: three, one per CleanMix phase

`[D-P1-11]` Three config JSONs, one per CleanMix phase. The spec asks "one per phase needed?" —
yes, because the phases are the only axis along which CleanMix actually dispatches configs, and
splitting later means editing the manifest, the file set, and every `@Mixin` package declaration at
once. The template README states it directly: "You will need one json per phase (`PRE_INIT`,
`DEFAULT`, `MOD`)" `[V:template]`.

| File | `target` | Mixin package | Purpose | Milestone |
|---|---|---|---|---|
| `schmaloogium.preinit.mixin.json` | `@env(PRE_INIT)` | `com.schmaloogium.mod.mixin.preinit` | Reserved. Anything needing to apply before mod construction — the vertex-format work (Phase 10) is the likely first tenant. **Empty for v0.1.** | `v0.3` |
| `schmaloogium.default.mixin.json` | `@env(DEFAULT)` | `com.schmaloogium.mod.mixin` | The bulk: render-loop hooks (Phase 7), shadow-pass hooks (Phase 8), texture hooks (Phase 13). | `v0.1` |
| `schmaloogium.mod.mixin.json` | `@env(MOD)` | `com.schmaloogium.mod.mixin.compat` | Mixins gated on other mods being present — `Loader.isModLoaded(...)` is only answerable in this phase. Carries the config plugin. | `v0.3` |

Common fields, following the template snapshot `[V:mcp get_project_template("mixins.json")]`:

```json
{
  "required": true,
  "package": "com.schmaloogium.mod.mixin",
  "compatibilityLevel": "JAVA_8",
  "target": "@env(DEFAULT)",
  "minVersion": "0.8.7",
  "setSourceFile": true,
  "client": [],
  "mixins": [],
  "server": []
}
```

Two notes a reviewer should not have to rediscover:

- **`"server": []` is permanent, not merely empty.** Schmaloogium is client-only (§1.2). Recording
  this as intent stops a later phase from "filling in the gap."
- **`compatibilityLevel: "JAVA_8"` is kept from the template snapshot even though our source level is
  Java 25.** The field constrains the bytecode level Mixin will accept in *mixin* classes, not the
  project's source level. Kept because it is the verified template value and because raising it is a
  change with no known benefit — but flagged in §11.3 as a value we inherited rather than derived,
  worth a spot check the first time a mixin uses a Java-9+ language feature that survives to bytecode.

An `IMixinConfigPlugin` slot, `com.schmaloogium.mod.mixin.SchmaloogiumMixinPlugin`, is reserved on
the **MOD-phase** config. Its designed role is to consult the bail registry (§4.8) in
`shouldApplyMixin` so that a detected incompatible chunk-renderer replacement can veto vertex-pipeline
mixins *before they apply*, rather than applying them and then disabling the engine — a materially
better failure mode. It is a reserved slot in v0.1; Phase 10 gives it content.

#### 4.5.3 SRG-name targeting policy

`[D-P1-12]` **Every `@Mixin` target class, every `@Shadow`/`@Inject`/`@Redirect` member reference,
and every descriptor is written in SRG names.** MCP-readable names appear in `//` comments beside
them, never in the annotation.

RESEARCH.md App E's header states every `@Mixin` target "must use SRG name + descriptor", and §5.1
records that "Mixins are written against **SRG names** and applied through Cleanroom's remapper chain
in dev and production" — `Srg2McpRemapper` in dev, an `FMLDeobfuscatingRemapper` wrapper in
production `[V:mcp]`. The resolution tool is the MCP recipe `resolve_symbol(...)`, which App E was
built with; App E's table is the first place to look before resolving anything fresh.

Convention, so the catalog stays readable:

```java
@Inject(method = "func_78471_a",              // renderWorld(FF)V
        at = @At("HEAD"))
private void schmaloogium$onRenderWorldHead(float partialTicks, long finishTimeNano, CallbackInfo ci) { … }
```

Injected method names are prefixed `schmaloogium$` to avoid collisions with other mods' mixins into
the same class — standard practice, and cheap insurance in an ecosystem where coremod-heavy stacks
are the norm (RESEARCH.md §2.3).

#### 4.5.4 Refmap handling under Unimined

`[D-P1-13]` **Refmap generation is left to Unimined. `disableRefmap()` is not called.**

The template README states it plainly: "Don't worry about refmap, Unimined will handle it
automatically. You can still `disableRefmap()` manually though" `[V:template]`. The MCP guide agrees:
"Refmaps are handled by Unimined at build" `[V:mcp]`. `sponge-mixin` stays `compileOnly` because the
loader provides the runtime — RESEARCH.md §5.1 records exactly this.

The one thing to watch, recorded for the Phase 7 implementation session: the template checkout is the
`main` branch and therefore has **never had a mixin config present**, so Unimined's refmap machinery
in this project is unexercised. The first config to land should be verified to produce a refmap in
the built jar before any hook work proceeds — that check belongs in §12's checklist.

#### 4.5.5 Dev ergonomics

Added to `:mod`'s `unimined.minecraft { cleanroom { runs.all { … } } }` block, gated behind a new
`enable_mixin_debug` property (default `true` for local dev, set `false` in CI so build logs stay
readable):

- `-Dmixin.debug.export=true` — writes post-transform classes to `.mixin.out/`. The template's
  mixin-branch run config sets this `[V:mcp]`.
- `-Dmixin.checks.interfaces=true` — fails fast on interface-implementation mismatches. Same source.
- `-Dcrl.dev.mixin=<config>` — **documented, not set.** This is Cleanroom's hook for injecting
  extra dev-only mixin configs at runtime `[V:mcp]` `[RESEARCH.md §5.1]`. Its designed use here is
  the Phase 7 hook-spike workflow: try an injection in a throwaway config without touching the
  shipped manifest. Recorded in the developer README rather than wired into the build.

Cleanroom annotates crash reports with which mixins touched each class `[V:mcp]` — worth knowing
before Phase 7 builds any bespoke diagnostics.

### 4.6 lwjglx posture (OQ-21)

`[D-P1-14]` **`enable_lwjglx = false`.** The `compileOnly "com.cleanroommc:lwjglx:1.0.0"` line in
`dependencies.gradle` becomes inert.

**What `enable_lwjglx=true` actually means in the template.** Exactly one thing `[V:template]`: it
adds `com.cleanroommc:lwjglx:1.0.0` to the **`compileOnly`** configuration. There is no runtime
injection, no run-configuration flag, no `-Dlwjglx` anywhere, and no effect on the produced jar. Its
sole function is to let source code `import org.lwjglx.*` and compile. The template's own comment
says so: "Set this to true if you want to use old LWJGL2 methods… If you are porting an old mod
lazily just set this to true."

**Why we drop it.** RESEARCH.md §6.1 lists as a hard constraint: "`org.lwjglx` is runtime-only —
Compile against LWJGL3 proper `[V:mcp]`". DESIGN.md §G2.2 restates it as binding: "**LWJGL3-native
code only**: never compile against `org.lwjglx` (runtime-only shim, itself in flux — OQ-21)." With
`enable_lwjglx=true`, an accidental `org.lwjglx` import compiles silently and the violation is
discovered at runtime, on someone else's machine, on a configuration where the shim is absent. With
it `false`, the same import is a compile error in the developer's IDE. The build should reject the
mistake, not tolerate it.

**Runtime posture.** lwjglx is a loader-side compatibility layer for *other* mods. Whether it is
present in a given installation is not our concern, because we never reference it. Two consequences
worth recording:

1. `CapabilityProbe` (§4.4 of the facade, below) queries LWJGL3 entry points directly. If a future
   installation routes some GL calls through a shim, the probe still reads the real context's values,
   because it asks the driver, not the shim.
2. RESEARCH.md §5.3 flags "what lwjglx intercepts at runtime" as unverified, feeding OQ-3 (Phase 7's
   GL-context spike). If that spike discovers the shim materially alters context creation, the
   finding lands in Phase 7's doc; it does not change our compile-time posture, which is
   unconditional.

**The flux, tracked.** RESEARCH.md §5.1 records that the Cleanroom README no longer mentions LWJGL2
compat, and that two successors exist: **LWJGLXX** ("using lwjglx without redirecting everything",
early) and **LWJGLY** ("LWJGL 2⇒3 Shim & Router", an *empty placeholder repo*). Neither is a
dependency of ours and neither can become one under this decision — which is the point: the flux is
somebody else's, and our posture is stable regardless of how it resolves. The OQ-21 spike (§10.4)
exists to confirm that the runtime story holds in practice, not to reconsider the compile-time rule.

### 4.7 The `engine.gl` facade

#### 4.7.1 Granularity: grouped services, opaque handles

`[D-P1-15]` The facade is **a small set of role-oriented service interfaces behind a `GLDevice`
root, addressing GL objects through opaque handle types** — not a thin 1:1 mirror of GL verbs.

The spec presents the choice as "thin GL-verb layer vs. grouped services". The deciding argument is
OQ-20. A thin GL-verb facade (`int genFramebuffer()`, `void bindFramebuffer(int, int)`,
`void uniform1i(int, int)`) is trivial to implement and trivial to record — but it *is* OpenGL, with
the package name changed. It encodes imperative call-at-a-time semantics, integer object names, and
global bind-point state into `:engine`'s source. Kirino-Engine's model is the opposite: deferred
render commands, abstracted GL objects, immutable RenderPass/Subpass composition (RESEARCH.md §5.2).
Porting `:engine` from the first to the second would not be "implement a new backend" — it would be
rewriting the engine, which is precisely the outcome D-6 exists to prevent (RESEARCH.md §7.2: "the
core must survive a backend swap").

Grouped services with opaque handles cost a little indirection in `mod.glue` and buy a facade that a
pass-based backend can implement without `:engine` noticing. Opaque handles matter more than they
look: an `int` in engine code is an invitation to arithmetic, to comparison against `0`, to
`glBindTexture(GL_TEXTURE_2D, id)`-shaped thinking. A `TextureHandle` is not.

The counter-cost is honest and recorded: a grouped facade is a *design* surface, so getting its
granularity wrong is more expensive than getting a verb list wrong. That is why §10.3 specifies a
backend-swap drill rather than declaring victory.

#### 4.7.2 `GLCapabilityProfile`

An immutable value object. It is the single most-consumed type this phase produces: Phase 2 replays
recorded profiles, Phases 4/5/6 gate on it, and Phase 3 derives `MC_<GL_extension>` macros from it.

```java
package com.schmaloogium.engine.gl;

public record GLCapabilityProfile(
        int glVersionMajor,
        int glVersionMinor,
        String glslVersion,          // as reported by GL_SHADING_LANGUAGE_VERSION
        String vendor,               // GL_VENDOR
        String renderer,             // GL_RENDERER
        int maxDrawBuffers,          // GL_MAX_DRAW_BUFFERS          — §4.1 probe
        int maxColorAttachments,     // GL_MAX_COLOR_ATTACHMENTS     — §4.1 probe
        int maxTextureImageUnits,    // GL_MAX_TEXTURE_IMAGE_UNITS   — §4.1 probe
        int maxVertexAttribs,        // GL_MAX_VERTEX_ATTRIBS
        int maxTextureSize,          // GL_MAX_TEXTURE_SIZE
        Set<String> extensions) {

    public boolean atLeast(int major, int minor) { … }
    public boolean hasExtension(String name)     { … }

    /** RESEARCH.md §4.1: "mipmap gen requires GL 3.0". */
    public boolean supportsMipmapGeneration()    { return atLeast(3, 0); }
}
```

`maxVertexAttribs` and `maxTextureSize` are additions beyond the §4.1 probe set, on the same
reasoning as the extension set: Phase 10's extended vertex format needs the first (it binds
attributes at locations 10/11/12 and will grow), Phase 5's buffer sizing needs the second. Both are
tagged `[A]` in the §3 map. `extensions` is defensively copied and exposed unmodifiable — a record's
component accessor otherwise hands out a mutable set.

**Serialization.** `[D-P1-16]` The profile has a stable, human-readable, diff-friendly text form —
a sorted `key = value` properties document with `extensions` as a sorted newline-delimited block.
This is what makes the whole headless testing strategy work:

```
gl.version           = 4.6
glsl.version         = 4.60 NVIDIA
vendor               = NVIDIA Corporation
renderer             = NVIDIA GeForce RTX 3070/PCIe/SSE2
max.drawBuffers      = 8
max.colorAttachments = 8
max.textureImageUnits= 32
max.vertexAttribs    = 16
max.textureSize      = 32768
extensions =
  GL_ARB_debug_output
  GL_ARB_sampler_objects
  …
```

`GLCapabilityProfile.parse(Reader)` / `.write(Writer)` round-trip it. Profiles captured from real
GPUs become checked-in fixtures under `conformance/src/test/resources/profiles/`. Sorted and
line-oriented so that a fixture's diff is readable when a driver update changes one extension.

This format **is** what §G6 means by "recorded `GLCapabilityProfile`s", what Phase 2's
"capability-profile replay" replays, and what Phase 4/5/6's "recorded-GL run" impl gates run against.
Phase 1 owns the format; Phase 2 owns the fixture set and the update workflow.

#### 4.7.3 Handles

```java
package com.schmaloogium.engine.gl;

/** Marker for every GL object the engine holds. Never an int. */
public sealed interface GLHandle permits
        ProgramHandle, ShaderHandle, TextureHandle, FramebufferHandle, RenderbufferHandle {}

public sealed interface ProgramHandle     extends GLHandle permits …  {}
public sealed interface ShaderHandle      extends GLHandle permits …  {}
public sealed interface TextureHandle     extends GLHandle permits …  {}
public sealed interface FramebufferHandle extends GLHandle permits …  {}

/** Deliberately NOT a GLHandle — a location is a lookup result, not an object. */
public sealed interface UniformLocation permits … {
    /** True when the uniform was optimized out; uploads through it are no-ops. */
    boolean isAbsent();
}
```

Each backend supplies the permitted implementations: `Lwjgl3GLDevice` wraps ints;
`RecordingGLDevice` wraps synthetic sequence numbers; a hypothetical Kirino backend wraps whatever
it uses. `:engine` sees only the interfaces.

`UniformLocation.isAbsent()` is load-bearing and belongs here rather than in Phase 6: GLSL compilers
routinely optimize out unused uniforms, `glGetUniformLocation` returns `-1`, and the reference
implementation's per-program location caching (RESEARCH.md §4.2) depends on distinguishing "not
looked up yet" from "looked up, not present". Exposing that as a boolean instead of a sentinel
integer is exactly the kind of leak the opaque-handle decision is meant to prevent.

#### 4.7.4 The device and its services

```java
public interface GLDevice {
    GLCapabilityProfile capabilities();

    ShaderService      shaders();
    UniformService     uniforms();
    TextureService     textures();
    FramebufferService framebuffers();
    StateService       state();
    DrawService        draw();
    DebugService       debug();
}
```

Seven services, each a role rather than a GL module. Load-bearing signatures:

```java
public interface ShaderService {
    ShaderHandle  createShader(ShaderStage stage, String source);
    CompileResult compile(ShaderHandle shader);          // never throws
    ProgramHandle createProgram();
    void          attach(ProgramHandle p, ShaderHandle s);
    void          bindAttributeLocation(ProgramHandle p, int location, String name); // pre-link
    LinkResult    link(ProgramHandle p);                 // never throws
    ValidateResult validate(ProgramHandle p);            // never throws
    void          use(ProgramHandle p);                  // the universal state barrier (§4.2)
    void          delete(ProgramHandle p);
    void          delete(ShaderHandle s);
}

public interface UniformService {
    UniformLocation locate(ProgramHandle p, String name);
    void upload(UniformLocation loc, int v);
    void upload(UniformLocation loc, float v);
    void upload(UniformLocation loc, float x, float y);
    void upload(UniformLocation loc, float x, float y, float z);
    void upload(UniformLocation loc, float x, float y, float z, float w);
    void uploadMatrix4(UniformLocation loc, float[] m16, boolean transpose);
    // NO uniform-block / UBO entry point — the pack contract forbids it (§6.1, D-9).
}

public interface FramebufferService {
    FramebufferHandle create(String debugLabel);
    void attachColor(FramebufferHandle f, int attachmentIndex, TextureHandle t);
    void attachDepth(FramebufferHandle f, TextureHandle t);
    void drawBuffers(FramebufferHandle f, int[] attachmentIndices);
    FramebufferStatus check(FramebufferHandle f);
    void bind(FramebufferTarget target, FramebufferHandle f);
    void bindDefault(FramebufferTarget target);
    void blit(FramebufferHandle src, FramebufferHandle dst, BlitSpec spec);
    void delete(FramebufferHandle f);
}

public interface TextureService {
    TextureHandle create(String debugLabel);
    void allocate(TextureHandle t, TextureSpec spec);     // spec is a value object; formats are Phase 5's
    void setParameters(TextureHandle t, TextureParameters p);
    void bindToUnit(int unit, TextureHandle t);
    void generateMipmap(TextureHandle t);                 // caller checks supportsMipmapGeneration()
    void delete(TextureHandle t);
}

public interface StateService {
    void viewport(int x, int y, int w, int h);
    void clearColor(float r, float g, float b, float a);
    void clear(EnumSet<ClearTarget> targets);
    void depthMask(boolean enabled);
    void blend(BlendState state);                         // null/absent = disabled
    void alphaTest(AlphaTestState state);
    /** Snapshot the state we are about to perturb, for the §G4.6 restore discipline. */
    StateSnapshot snapshot(EnumSet<StateAspect> aspects);
    void restore(StateSnapshot snapshot);
}

public interface DrawService {
    /** The composite/final full-screen pass primitive. The backend picks GL_QUADS or the
     *  triangle-strip fallback (§6.1); the engine never expresses that choice. */
    void fullscreenQuad();
    void fullscreenQuadInstanced(int instanceCount);
}

public interface DebugService {
    void pushGroup(String label);
    void popGroup();
    void label(GLHandle handle, String label);
    boolean isActive();          // false unless a debug context and the dev flag are both on
}
```

Design rules embedded above, each with a reason:

- **`compile` / `link` / `validate` return results; they never throw.** §G2.4's rung 3 requires that a
  program failing compile/link/validate deletes itself and reports a user-visible error. A checked
  exception crossing the facade would make that a control-flow problem instead of a data problem.
  `CompileResult`/`LinkResult` carry `success`, the driver log, and a `EngineDiagnostic` (§4.9).
- **`StateService` is deliberately narrow.** It exposes only what §G4.6 says we perturb — viewport,
  clears, depth mask, blend, alpha test — plus explicit `snapshot`/`restore`. It exposes no way to set
  state that `GlStateManager` caches without going through it, because §G4.6 forbids exactly that
  ("we never bypass it for state it caches"). The narrowness is the enforcement: you cannot misuse an
  entry point that does not exist. **Which** state is perturbed at which moment is Phase 5/6/7 policy.
- **No GL constants appear in any signature.** `ShaderStage`, `FramebufferTarget`, `ClearTarget`,
  `FramebufferStatus`, `BlendState` are engine enums/records; the LWJGL3 backend maps them to `GL_*`.
  A raw `int target` parameter would be the GL-verb layer wearing a costume.
- **`DebugService` exists in v0.1 as a no-op.** Its implementation is `v0.5` (Phase 14), but its
  presence now means Phase 4/5's object-creation sites can call `label(handle, "colortex0")` from day
  one, which is exactly the "architect now, implement later" rule of §G0.3.

**What the facade deliberately does NOT contain**, so no later phase mistakes an omission for a gap:
texture formats and the fixed unit map (Phase 5/6), ping-pong/flip logic (Phase 5), draw-buffer
routing decisions (Phase 5), clear colors and when to clear (Phase 5), uniform cadences and smoothing
(Phase 6), the program registry and backup chains (Phase 4). The facade offers verbs; every one of
those is policy about when to use them.

#### 4.7.5 Recording / replay for headless tests

Lives in `com.schmaloogium.engine.gl.record`, inside `:engine` per §G3.1 ("engine.gl … + a
recording/replay implementation for headless tests"). Phase 1 owns the mechanism; Phase 2 owns
golden content and the update workflow.

```java
public record GLCall(String op, List<Object> args) {}

public final class GLCallLog {
    public List<GLCall> calls();
    public List<GLCall> callsMatching(String opPrefix);
    public String render();            // one call per line, for golden files & failure messages
}

public final class RecordingGLDevice implements GLDevice {
    public RecordingGLDevice(GLCapabilityProfile profile, ScriptedResponses responses);
    public GLCallLog log();
}

/** Canned answers for query-shaped calls, so tests can drive failure paths. */
public final class ScriptedResponses {
    public ScriptedResponses linkFails(String programLabel, String driverLog);
    public ScriptedResponses compileFails(String shaderLabel, String driverLog);
    public ScriptedResponses uniformAbsent(String uniformName);
    public ScriptedResponses framebufferStatus(String fboLabel, FramebufferStatus status);
}
```

Behavior:

- Every mutating call appends a `GLCall` and returns a synthetic handle (a monotonic sequence
  number wrapped in the appropriate handle type). Handles are `equals`-comparable so assertions can
  say "the texture attached at index 2 is the one created third".
- Every query-shaped call answers from the `GLCapabilityProfile` or the `ScriptedResponses`. The
  default responses are all-success, so a test that only cares about call sequence writes none.
- The log's rendered form is stable and deterministic: no timestamps, no identity hash codes, no
  iteration-order dependence. This is what makes it usable as a golden file, and it is a constraint,
  not an implementation note.

Assertions:

```java
public final class ReplayAssertions {
    public static ReplayAssertions assertThat(GLCallLog log);

    public ReplayAssertions calledInOrder(String... opNames);
    public ReplayAssertions neverCalled(String opName);
    public ReplayAssertions bindsBalanced();       // every bind has a matching unbind/rebind
    public ReplayAssertions noLeakedObjects();     // every create has a matching delete
    public ReplayAssertions drawBuffersWere(int... attachmentIndices);
}
```

`bindsBalanced()` and `noLeakedObjects()` are named here because two later impl gates ask for exactly
them: Phase 5's "creates/destroys the full buffer estate for a classic pack without leaks", and the
general §G4.6 restore discipline.

**The fixture-production loop.** `mod.glue.CapabilityProbe` builds a `GLCapabilityProfile` from a
live context at display init and, under `-Dschmaloogium.debug.dumpCapabilities`, writes it in the
§4.7.2 text form. A developer with a given GPU runs the client once and contributes a profile
fixture. Without this, the recorded profiles Phase 2 replays would have to be hand-written, and
hand-written capability sets are exactly where wrong assumptions hide.

### 4.8 License, headers, and third-party notices (D-7)

#### 4.8.1 The LICENSE file

`[D-P1-17]` `LICENSE` at the repo root contains the **verbatim GPL-3.0 text**, and the project is
licensed **GPL-3.0-or-later** — the "or later" living in the per-file SPDX headers and in
`README.md`, which is where GPL-3.0's own recommended practice puts it (the license text itself is
version-specific; the "or later" grant is a statement about the work).

#### 4.8.2 Source-header convention

`[D-P1-18]` Every source file — `.java`, `.gradle`, and the mixin `.json` files where comments are
permitted — opens with:

```java
// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
```

Two lines, machine-readable, no copied license boilerplate in every file. SPDX identifiers are the
form license scanners and the wider ecosystem actually read. `README.md` carries the full statement
(what the project is, that it is GPL-3.0-or-later, and where the text lives).

#### 4.8.3 `THIRD-PARTY.md` — the D-8 compliance mechanism

D-8 permits incorporating LGPL-3.0 code from Iris and Angelica, with compliance: "preserve
copyright/license notices, mark modifications" (RESEARCH.md §10.1). Compliance is not something to
reconstruct at release time from memory, so the mechanism exists from day one:

`THIRD-PARTY.md` at the repo root, one entry per incorporation:

| Field | Content |
|---|---|
| Files | our paths carrying the incorporated code |
| Upstream | project, license, commit/version, URL |
| Notice | the upstream copyright notice, verbatim |
| Modifications | what we changed, marked |

Plus a standing prohibition at the top of the file, so nobody has to go find it in RESEARCH.md §10.1:

> **Never copy from glsl-transformer.** Iris bundles it; it is **AGPL-3.0**; its network-service
> terms would attach to the derived portion. Iris's own LGPL-3.0 code is fine. `[D-8]` `[V:web]`
>
> **The OptiFine decompile (`schlorbium-project/`) is behavioral-observation-only.** No identifier,
> structure, or code derived from it ships. `[D-8]` `[§G7.2]`

The v0.1 `THIRD-PARTY.md` is expected to be empty of entries and full of these rules. That is the
correct state — the mechanism exists before the first incorporation, not after.

#### 4.8.4 OQ-12 — the licensing note

*Scoped as assigned: GPL-3.0-or-later mod, LGPL-2.1 platform, LGPL-3.0 GUI dependency, jar-in-jar.*

The question (RESEARCH.md §11, verbatim): *"GPL-3.0-or-later mod on LGPL-2.1 platform + LGPL-3.0 GUI
dep; jar-in-jar implications."* Status: *"open — concern reduced by the `[D-7]` GPL-3.0-or-later
change (LGPL-3.0 combines cleanly)."*

**The platform (Cleanroom, LGPL-2.1).** Cleanroom is the runtime environment Schmaloogium is loaded
by, not a library we redistribute. RESEARCH.md §10.3 characterizes it as "Platform, not a linked
library in the derivative-work sense; standard mod practice." Nothing about a GPL-3.0-or-later mod
running on an LGPL-2.1 loader requires anything of either party: we do not ship Cleanroom, we do not
modify it, and the LGPL's obligations attach to distribution of the LGPL'd work. Separately, and
only if it ever mattered, LGPL-2.1 §3 permits converting a copy to GPL-2.0-or-later — but that
conversion is a right of a redistributor of Cleanroom, which we are not.

**The GUI dependency (ModularUI, LGPL-3.0).** LGPL-3.0 is, by construction, GPL-3.0 plus additional
permissions. A work combining LGPL-3.0 code with GPL-3.0-or-later code is a GPL-3.0-or-later work,
and the LGPL portions keep their notices. This direction of combination is the clean one — which is
what RESEARCH.md §11's "concern reduced by the `[D-7]` change" refers to: under the previously-planned
MIT license the combination would have forced questions about the resulting whole; under
GPL-3.0-or-later it does not.

**Jar-in-jar specifically.** Two possible arrangements, both compliant:

1. *Mod dependency* — ModularUI is installed separately and we declare a dependency. This is mere
   aggregation at the installation level; no combination question arises at distribution time.
2. *Bundled via the template's `contain` configuration* — ModularUI's jar ships inside ours and the
   loader extracts it. Here we are distributing the LGPL-3.0 work, so LGPL-3.0's terms attach to
   that copy: preserve its notices, ship its license text, and do not restrict the recipient's LGPL
   rights (notably the ability to replace the bundled version). None of that conflicts with
   GPL-3.0-or-later, and the bundled jar remains a separate, identifiable work rather than being
   merged into ours. RESEARCH.md §10.3 records the same conclusion: "Dynamic linking as a mod
   dependency is ecosystem-standard; jar-in-jar (`contain`) bundling eased under GPL-3.0-or-later
   (LGPL-3.0 combines cleanly)."

**Ecosystem precedent.** The arrangement is not novel on this platform or this Minecraft version.
Fugue — a standard Cleanroom companion mod — is **GPL-3.0** (RESEARCH.md §5.1), i.e. a copyleft mod
shipping against this exact LGPL-2.1 loader. On the shader-engine side specifically, Iris is
**LGPL-3.0** and Angelica is **LGPL-3.0 with MIT portions** (RESEARCH.md §10.3), both of which D-8
already contemplates reusing into our GPL-3.0-or-later work. The combination pattern
GPL-3.0-or-later mod + LGPL platform + LGPL library is well-trodden.

**Conclusion.** No obstacle, and no change to D-7 is warranted. Two obligations for later phases,
recorded here so they are not rediscovered: (a) whichever arrangement Phase 12 picks for ModularUI,
if it bundles, `THIRD-PARTY.md` gets an entry and the license text ships; (b) any LGPL-3.0 code
incorporated *into our sources* under D-8 follows §4.8.3 regardless of the GUI decision. OQ-12 can
be marked resolved-by-note once a reviewer accepts this section.

### 4.9 Logging, debug flags, and error channels

#### 4.9.1 The `Log`/`LogSink` SPI

`[D-P1-19]` `:engine` defines its own minimal logging SPI rather than depending on log4j.

The reasoning is not that log4j is forbidden — it is not on §G3.1's list. It is that log4j on
1.12.2 is supplied by the Minecraft runtime, so an `:engine` dependency on it would be a production
dependency that exists only because Minecraft happens to provide it: a soft version of exactly the
coupling D-6 removes, and one that makes `:engine`'s headless tests require a logging backend they
have no reason to need. The SPI is roughly twenty lines.

```java
package com.schmaloogium.engine.log;

public interface Log {
    void debug(String message, Object... args);      // {} placeholders, log4j-style
    void info (String message, Object... args);
    void warn (String message, Object... args);
    void error(String message, Object... args);
    void error(Throwable t, String message, Object... args);
    boolean isDebugEnabled();
}

public interface LogSink {
    void emit(String channel, LogLevel level, String message, Object[] args, Throwable t);
}

public final class Logs {
    public static void install(LogSink sink);        // mod.core at boot; tests install a capturing sink
    public static Log channel(String name);          // name from LogChannels
}
```

`mod.core` installs a log4j-backed sink during `preInit`. Tests install a capturing sink and assert
over it. Before installation, a no-op sink is active — so a static initializer that logs cannot
explode during class loading.

#### 4.9.2 The channel list

`[D-P1-20]` §G4.5 says "Log channels are per-subsystem (`schmaloogium.pack`, `.compile`, `.frame`,
`.gl`, …; Phase 1 fixes the list)." Fixed, as constants on `LogChannels`:

| Channel | Subsystem | Owner phase |
|---|---|---|
| `schmaloogium.boot` | mod lifecycle, engine bootstrap, capability probe | 1 / 7 |
| `schmaloogium.pack` | pack discovery, file model, dimension folders | 3 |
| `schmaloogium.preprocess` | `#include` resolution, macro header, preprocessor | 3 |
| `schmaloogium.config` | `shaders.properties`, options, profiles, screens, ID files | 3 |
| `schmaloogium.compile` | shader compile / link / validate | 4 |
| `schmaloogium.registry` | stage registry, program slots, backup chains | 4 |
| `schmaloogium.buffers` | framebuffers, colortex, ping-pong, clears, sizing | 5 |
| `schmaloogium.uniforms` | built-in uniforms, cadences, samplers, unit map | 6 |
| `schmaloogium.expr` | custom-uniform expression engine | 11 |
| `schmaloogium.frame` | per-frame orchestration, pass dispatch | 7 |
| `schmaloogium.shadow` | shadow pass | 8 |
| `schmaloogium.gl` | facade-level GL events, capability gates, GL errors | 1 / 14 |
| `schmaloogium.compat` | coexistence detection, bail verdicts | 1 / 10 |
| `schmaloogium.gui` | options and pack-selection screens | 12 |
| `schmaloogium.conformance` | harness-side output | 2 |

Rules: channels are fixed strings on `LogChannels`, never composed at runtime; every log line goes to
exactly one channel; a subsystem that wants finer granularity uses `isDebugEnabled()` and message
content, not a new channel. This keeps a user's `log4j2.xml` filter meaningful — the reason for
per-subsystem channels in the first place.

The list is fixed but not frozen: a later phase that genuinely needs a channel adds it here via a
requested change to this document (§G1.1's "propose changes in your doc §11" applies in reverse —
a later phase flags it in its own §11 and this doc is amended by a fix-up session).

#### 4.9.3 Debug flags

`[D-P1-21]` Namespace: `-Dschmaloogium.debug.*`. All boolean. Absent means off. Never read
before `mod.core` has bootstrapped, so a malformed value can never affect class loading. Reserved
from day one, per §G4.5's instruction:

| Flag | Effect | Owner phase | Milestone |
|---|---|---|---|
| `schmaloogium.debug.saveSources` | Dump fully-processed shader sources to disk. **The `shaders.debug.save` equivalent (App F.8)** — reserved by name because the spec requires it | 3 | `v0.1` |
| `schmaloogium.debug.dumpCapabilities` | `CapabilityProbe` writes the live `GLCapabilityProfile` in the §4.7.2 text form; the fixture-production loop | 1 | `v0.1` |
| `schmaloogium.debug.recordGL` | Wrap the live `GLDevice` in a recorder and dump the `GLCallLog` — the same log format the headless tests assert over, captured from a real session | 1 | `v0.1` |
| `schmaloogium.debug.glLabels` | Activate `DebugService` (KHR_debug object labels and groups) | 14 | `v0.5` |

`recordGL` deserves a note: it means a bug reproduced in a live session produces an artifact that can
be replayed and asserted over in a headless test. That is the seam paying rent in the other
direction, and it costs one decorator.

Not in this namespace, and deliberately so: `-Dmixin.debug.export`, `-Dmixin.checks.interfaces`, and
`-Dcrl.dev.mixin` are the platform's flags, not ours (§4.5.5).

#### 4.9.4 User-facing error channels

§G4.5 names three: chat errors (pack-level failures, capability gates), the shader GUI (per-program
compile errors), and the log. The design problem is that `:engine` produces the errors and cannot
name a Minecraft chat component.

`[D-P1-22]` `:engine` emits loader-neutral `EngineDiagnostic` values; `:mod` routes them.

```java
package com.schmaloogium.engine.diag;

public record EngineDiagnostic(
        DiagnosticSeverity severity,     // INFO, WARN, ERROR, FATAL
        UserChannel        channel,      // CHAT, SHADER_GUI, LOG_ONLY
        String             messageKey,   // a lang key, e.g. "schmaloogium.error.program.link"
        List<Object>       args,
        String             detail,       // driver log / stack detail; GUI and log only, never chat
        String             logChannel) { // one of LogChannels
}

public interface DiagnosticReporter {
    void report(EngineDiagnostic d);
}
```

`:engine` holds a `DiagnosticReporter`. In `:mod`, the implementation fans out: `CHAT` becomes a
translated chat message via the client player (dropped, with a log line, if no player exists yet),
`SHADER_GUI` accumulates into a per-pack error store that Phase 12's screen renders, `LOG_ONLY` goes
nowhere else. Every diagnostic reaches the log regardless of channel — the log is the transcript, the
other two are notifications.

Message keys, not message text, cross the seam: `:engine` has no business holding user-facing English,
and Phase 12 needs lang keys for the GUI anyway. The severity/channel split maps onto the §G2.4
degradation ladder — a disabled single uniform is `WARN`/`LOG_ONLY`, a failed program is
`ERROR`/`SHADER_GUI`, a failed capability gate is `ERROR`/`CHAT`.

### 4.10 The `mod.compat` bail registry

Phase 1 owns **the mechanism**. The mod-id list, the detection technique, and the message text are
**Phase 10 / OQ-5** (DESIGN.md §G10 assigns OQ-5 to P10; the Phase 10 spec says "via Phase 1's bail
registry"). This section builds the slot and nothing else.

```java
package com.schmaloogium.mod.compat;

public sealed interface CompatVerdict {
    record Ok()                                        implements CompatVerdict {}
    record Degrade(String reasonKey, List<Object> args) implements CompatVerdict {}
    record Bail   (String reasonKey, List<Object> args) implements CompatVerdict {}
}

public interface CompatCheck {
    String id();                                  // stable, for logs and for user-facing attribution
    CompatVerdict check(CompatContext ctx);
}

public interface CompatContext {
    boolean isModLoaded(String modId);
    boolean isClassPresent(String binaryName);    // for detecting a replacement that ships unnamed
    GLCapabilityProfile capabilities();           // capability gates are compat checks too
}

public final class BailRegistry {
    public static void register(CompatCheck check);         // during preInit
    public static CompatEvaluation evaluate(CompatContext ctx);
}

public record CompatEvaluation(
        List<CompatVerdict.Bail>    bails,
        List<CompatVerdict.Degrade> degradations) {
    public boolean shouldBail() { return !bails.isEmpty(); }
}
```

**Evaluation points** — three, each chosen because it is a moment where the answer can change:

1. **Before engine bootstrap** (post-`FMLLoadCompleteEvent`, before the first pack load). The
   ordinary case: another mod is installed, we detect it, shaders never start.
2. **Before any vertex-format change** (Phase 10). RESEARCH.md §4.1 step 3 notes that a pack load can
   trigger a vertex-format rebuild and world-renderer reload; that is the operation most likely to
   collide with a replaced chunk pipeline, so it re-checks.
3. **In `SchmaloogiumMixinPlugin.shouldApplyMixin`** for MOD-phase mixins (§4.5.2). This is the
   strongest form: a vetoed mixin is never applied, so there is no partially-instrumented state to
   unwind. It is available only for MOD-phase configs, which is precisely where the vertex-pipeline
   compat mixins will live.

**On `Bail`:** shaders are forced off and *stay* off for the session; an `EngineDiagnostic` with
`severity=ERROR`, `channel=CHAT`, and the check's `reasonKey` goes out; a line lands on
`schmaloogium.compat`; and the reason is retained so Phase 12's GUI can display it instead of an
empty pack list. This is §G2.4's rung 4 ("a capability gate failing at init turns the pack off
gracefully with a chat error") and rung 5 ("shaders-off must always be a reachable state"). Bailing
is not an error path — it is a supported terminal state.

**On `Degrade`:** a warning to log and GUI, and the engine continues. The `Degrade` case exists in
the type from day one because OQ-5 is explicitly undecided between "detect and bail" and "integrate"
(RESEARCH.md §5.3); a verdict type that can only say "stop" would force Phase 10 to widen the
mechanism it was told to reuse.

**What Phase 1 ships:** the types above, the registry, the three evaluation points wired, the
diagnostic routing, and **zero registered checks**. `[D-P1-23]` No mod ids are named by this phase —
naming Celeritas or Nothirium here would be Phase 10's policy decision made by the wrong session, and
RESEARCH.md §2.3 shows the landscape moves (five-plus Vintagium forks, Celeritas source-only). The
example in the doc is a shape, not a policy:

```java
// Illustrative only — Phase 10 supplies the real checks and the mod-id list.
BailRegistry.register(new CompatCheck() {
    public String id() { return "example.chunk-renderer-replacement"; }
    public CompatVerdict check(CompatContext ctx) { return new CompatVerdict.Ok(); }
});
```

### 4.11 CI workflow adjustments

The three template workflows all hardcode root-relative `build/libs` `[V:template]`, which the module
split breaks — after the split, the mod jar is at `mod/build/libs`.

**`build.yml`.** Java 25 / Gradle 9.6.1 / `actions/*` versions unchanged.

- `./gradlew build` unchanged (it aggregates across modules).
- **New, explicitly-named step: "Seam architecture test"** running `./gradlew :engine:test`. It is a
  separate, named step rather than being folded into `build` so that a seam violation appears in the
  CI UI as *"Seam architecture test — failed"* rather than as an anonymous test failure inside a
  build. Given that the seam is this project's highest-weight structural risk (§2.2), its regression
  deserves to be legible at a glance. `[D-P1-24]`
- `./gradlew :conformance:test` as a second named step (a no-op until Phase 2, present so Phase 2
  adds content rather than plumbing).
- Artifact upload path `build/libs` → `**/build/libs/*.jar`.
- New `if: failure()` step uploading `**/build/reports/tests/**` — a failed architecture test whose
  report is unreachable is a bad day.

**`release.yml`.** `artifacts: "build/libs/*"` → `"mod/build/libs/*"`. Targeted at `:mod` specifically
rather than globbed, because a release should never accidentally publish `:engine`'s or
`:conformance`'s jars.

**`release-to-cf-mr.yml`.** The `files:` block's two globs retargeted at `mod/build/libs/`. Two
pre-existing items flagged for release time, not changed now: `modrinth-id` and `curseforge-id` are
both the literal string `placeholder` `[V:template]`, and `loaders: forge` is correct in the sense
that Cleanroom is Forge-lineage but should be confirmed against whatever the publishing platforms
expect for a Cleanroom-exclusive mod (D-1) before the first real publish.

**Phase 2 extension point.** A `conformance` job stub, `workflow_dispatch`-gated so it never runs
accidentally, containing: an `actions/cache` step keyed on pack version IDs (the §G6 fixture policy
is download-at-test-time with a local cache, and no pack may ever be committed), and a placeholder
step. Left deliberately visible and empty rather than absent, so Phase 2 fills a slot instead of
designing CI from scratch. Phase 2 owns everything inside it, including the OQ-10 headless-GL
question.

Not adopted: a license-header lint. It would be useful, but it is unasked-for scope and Phase 1
already has enough CI surface. Noted in §11.4 as a candidate.

---

## 5. Cross-phase interfaces

Phase 1 **consumes** nothing — it has no dependencies (§G5.1).

Phase 1 **exposes** the following. Everything here is a contract that later phases build against;
per §G5.3 a dependent phase reads this section, not the rest of the document.

### 5.1 Structural contracts

| Exposed | Detail | Consumed by |
|---|---|---|
| **Module layout** | `:engine`, `:mod`, `:conformance` with the §2.1 package table | all phases |
| **The seam constraints C-1 … C-4** | §4.3, stated mechanically and enforced by tests | all phases |
| **Package placement rule** | a phase's code goes in the package §2.1 assigns it; `.internal` sub-packages are private to `:engine` | all phases |
| **Version pin table + re-pin procedure** | §4.2.6 | all phases; operationally, whoever tags a milestone |
| **Naming** | `mod_id = schmaloogium`, root package `com.schmaloogium`, `Reference` at `com.schmaloogium.Reference` | all phases |

### 5.2 `engine.gl` — the facade

| Exposed | Detail | Consumed by |
|---|---|---|
| `GLDevice` + the seven services | §4.7.4 signatures | 4, 5, 6, 7, 8, 13, 14 |
| `GLCapabilityProfile` | §4.7.2, including `supportsMipmapGeneration()`, `hasExtension()`, `atLeast()` | 2, 3 (extension macros), 4, 5, 6, 14 |
| **`GLCapabilityProfile` text serialization format** | §4.7.2; `parse(Reader)` / `write(Writer)` | **2** (this is "recorded `GLCapabilityProfile`s"), 4, 5, 6 |
| Opaque handle types | §4.7.3, incl. `UniformLocation.isAbsent()` | 4, 5, 6, 13 |
| `RecordingGLDevice`, `GLCallLog`, `GLCall`, `ScriptedResponses` | §4.7.5 | **2**, 4, 5, 6 |
| `ReplayAssertions` incl. `bindsBalanced()`, `noLeakedObjects()`, `drawBuffersWere()` | §4.7.5 | 2, 4, 5 |
| `CompileResult` / `LinkResult` / `ValidateResult` | never-throwing result types carrying driver logs | 4 |
| `StateService.snapshot()` / `restore()` | the §G4.6 perturb-and-restore mechanism | 5, 6, 7 |
| `DebugService` | present in v0.1, active at v0.5 | 4, 5 (call sites), **14** (implementation) |

**Explicit note to Phase 2:** your declared input is "`PHASE_1_DOC.md` (module layout, facade,
`GLCapabilityProfile`)". All three are in §2.1, §4.7.4, and §4.7.2 respectively; the serialization
format your replay depends on is §4.7.2's text form, and the recording backend is §4.7.5. What Phase 1
does *not* give you: the fixture set itself, the golden-file format for anything other than
`GLCallLog.render()`, and any answer to OQ-10.

**Explicit note to Phases 4/5/6:** your impl gates say "a recorded-GL run". The mechanism is
`RecordingGLDevice` + a `GLCapabilityProfile` fixture + `ReplayAssertions`. If you need an assertion
that is not in §4.7.5's list, add it in your own doc's §5 as a requested change to this one — do not
assume it exists.

### 5.3 Conventions

| Exposed | Detail | Consumed by |
|---|---|---|
| `Log` / `LogSink` / `Logs` | §4.9.1 | all phases |
| **The fixed channel list** | §4.9.2 | all phases |
| `EngineDiagnostic`, `DiagnosticSeverity`, `UserChannel`, `DiagnosticReporter` | §4.9.4 | 3, 4, 5, 6, 7, 11, **12** (GUI is a channel consumer) |
| **Debug-flag namespace and the four reserved flags** | §4.9.3 | 3 (`saveSources`), 14 (`glLabels`) |
| Mixin config slots (three, by phase) + package placement | §4.5.2 | **7**, 10, 13 |
| SRG-targeting policy and the `schmaloogium$` prefix | §4.5.3 | 7, 10, 13 |
| `SchmaloogiumMixinPlugin` slot on the MOD config | §4.5.2 | 10 |
| `CompatCheck` / `CompatVerdict` / `CompatContext` / `BailRegistry` | §4.10 | **10** (policy), 7 (the bail hook) |
| SPDX header convention + `THIRD-PARTY.md` mechanism | §4.8.2, §4.8.3 | all phases; especially any phase incorporating LGPL-3.0 code under D-8 |
| CI job/step layout + the `conformance` extension point | §4.11 | **2** |

### 5.4 Requested changes to dependencies

None — Phase 1 has no dependencies. Requested changes to RESEARCH.md and DESIGN.md are in §11.4.

---

## 6. Failure modes & degradation

The §G2.4 ladder applied to foundation concerns. Rung 5 — "nothing in the shader engine ever crashes
the client or corrupts the vanilla framebuffer path; shaders-off must always be a reachable state" —
is the invariant every row below serves.

| Failure | Rung | Behavior |
|---|---|---|
| **Capability probe fails or returns nonsense** (missing entry point, driver returns 0 for a max) | 4 | `CapabilityProbe` catches, logs on `schmaloogium.gl`, and produces a *conservative* profile (GL 2.1, the spec minimums). The engine then fails its capability gates naturally and turns the pack off with a chat error. It does not guess optimistically, and it does not propagate an exception into display init. |
| **A capability gate fails at init** (pack needs more draw buffers / attachments / units than the profile offers) | 4 | Pack turns off gracefully; `EngineDiagnostic(ERROR, CHAT)` naming the shortfall; `schmaloogium.gl` line with the profile values. Vanilla rendering is untouched because no GL object was created yet. |
| **A shader fails to compile / a program fails to link or validate** | 3 | The facade returns a failed `CompileResult`/`LinkResult`/`ValidateResult` — it never throws. Phase 4 deletes the program, emits `EngineDiagnostic(ERROR, SHADER_GUI)` carrying the driver log, and resolves through the backup chain. |
| **A facade call fails at the driver level** (`glGetError` non-zero after an operation) | 3→4 | The LWJGL3 backend surfaces it as a result or a diagnostic on `schmaloogium.gl`; it never throws through a mixin into vanilla's call stack. Persistent failures escalate to a pack-level bail. |
| **A `CompatCheck` returns `Bail`** | 4 | Shaders forced off for the session, chat error with the check's reason, `schmaloogium.compat` line, reason retained for the GUI. A supported terminal state, not a crash. |
| **A `CompatCheck` itself throws** | 4 | Caught by `BailRegistry.evaluate`, logged with the check's `id()`, and treated as `Bail` — a check that cannot decide is not evidence of compatibility. Fails safe. |
| **No `LogSink` installed yet** (something logs during class loading) | — | A no-op sink is active until `mod.core` installs the real one. Logging can never be the thing that breaks startup. |
| **A diagnostic targets `CHAT` before a player exists** | — | Downgraded to log-only with a note; never buffered indefinitely and never dropped silently. |
| **`:engine` throws an unexpected `RuntimeException`** | 5 | `mod.core` wraps every engine entry point at the glue boundary. The engine is disabled for the session, one `EngineDiagnostic(FATAL, CHAT)` is emitted, and the vanilla path resumes. This wrapper is the last line of the ladder and it is `:mod`'s job precisely because `:engine` must not know what "the vanilla path" is. |
| **A seam violation reaches a build** | build-time | The §4.3 tests fail; CI's named "Seam architecture test" step goes red. Not a runtime failure mode — by design. |

---

## 7. Threading & performance notes

**Thread ownership.**

| Component | Thread |
|---|---|
| Every `engine.gl` facade call, and therefore every `Lwjgl3GLDevice` method | **Render thread only** (§G2.3: "The render thread owns all GL") |
| `CapabilityProbe` | Render thread, once, at display init |
| `BailRegistry.evaluate` | Main/client thread at bootstrap; render thread at the vertex-format-change and mixin-plugin evaluation points |
| `Logs` / `LogSink` | Any thread. The installed sink must be thread-safe; the log4j-backed one is |
| `DiagnosticReporter` | Any thread for `LOG_ONLY`; `CHAT` and `SHADER_GUI` deliveries hop to the client thread |
| `:engine` types generally | **No thread affinity by construction.** `:engine` holds no thread-local state and starts no threads. Phases 3 and 11 are permitted off-thread work (§G2.3), and the seam is what makes that safe to reason about |
| `RecordingGLDevice` | Test-only; single-threaded by assumption, and documented as such |

**Allocation posture** (§G2.5). Clean code first; optimize with evidence. Specifically for this
phase's types: `GLCapabilityProfile` is allocated once per session. Handles are small records
allocated at object-creation time, not per-frame. `EngineDiagnostic` is allocated on error paths
only. The one type on a potential hot path is `UniformLocation`, which Phase 6's location caching
will hold per program per uniform — allocated at cache-fill time, then reused, which is the same
shape as the reference implementation's behavior (RESEARCH.md §4.2, "per-program location caching +
redundant-upload skipping"). No array caches, no mutable-pose machinery, no object pools: §G2.5 is
explicit that generational ZGC on Java 25 removes the constraint that produced those in the
reference.

**Known hot paths introduced here.** Exactly one: the facade sits between the engine and every GL
call, so a per-call allocation or a megamorphic dispatch in a service implementation would be paid
per draw. Mitigations designed in: services are interfaces with a single production implementation
each (so the JIT sees a monomorphic call site in practice), no varargs on the uniform-upload
overloads, and no boxing in any signature on a per-frame path. `RecordingGLDevice` allocates
freely — it is never in a shipped path, and saying so here prevents a future reader from
"optimizing" it.

**Explicitly not a hot path.** `GLCallLog`, `ReplayAssertions`, `CapabilityProbe`, `BailRegistry`,
and the diagnostic machinery are all init-time or test-time.

---

## 8. Testability plan

### 8.1 Headless unit tests owned by this phase

| Test | Module | Asserts |
|---|---|---|
| `SeamClasspathTest` | `:engine` | Constraint **C-1**, classpath half — no forbidden coordinate on `main`'s compile or runtime classpath (§4.3 layer 2) |
| `SeamBytecodeTest` | `:engine` | Constraint **C-1**, bytecode half — no forbidden type referenced by any compiled `:engine` class (§4.3 layer 3). **This is the test the Impl gate names** |
| `SeamInternalsTest` | `:mod` | Constraint **C-2** — no `:mod` class references `com.schmaloogium.engine.*.internal.*` |
| `SeamLwjglConfinementTest` | `:mod` | Constraint **C-3** — no `org.lwjgl*` reference outside `com.schmaloogium.mod.glue` (the mechanical half of §G4.6) |
| `GLCapabilityProfileSerializationTest` | `:engine` | Round-trip `write` → `parse` is identity; output is sorted and deterministic; a hand-written fixture parses to the expected values |
| `GLCapabilityProfileDerivationTest` | `:engine` | `atLeast`, `hasExtension`, and `supportsMipmapGeneration()` (true at 3.0, false at 2.1 — the RESEARCH.md §4.1 gate) |
| `RecordingGLDeviceTest` | `:engine` | Calls are logged in order with correct arguments; handles are distinct and `equals`-comparable; `ScriptedResponses` drives failure paths; `render()` is stable across runs |
| `ReplayAssertionsTest` | `:engine` | Each assertion passes on a conforming log and fails with a useful message on a violating one — including `bindsBalanced()` and `noLeakedObjects()`, since Phase 5's impl gate depends on them being right |
| `BailRegistryTest` | `:mod` | `Ok`/`Degrade`/`Bail` aggregation; a throwing check is treated as `Bail`; evaluation is idempotent |
| `LogChannelTest` | `:engine` | Every `LogChannels` constant is unique and starts with `schmaloogium.`; the no-op sink is active before installation |
| `DiagnosticRoutingTest` | `:mod` | `CHAT`/`SHADER_GUI`/`LOG_ONLY` route correctly; `CHAT` with no player degrades to log; every diagnostic reaches the log |

`:conformance` gets its JUnit wiring, its `:engine` dependency, and a single placeholder test proving
the module builds and runs. Its content is Phase 2's.

### 8.2 Constraint C-4

`:conformance` must depend on `:engine` and never on `:mod`. Enforced by inspection of
`conformance/build.gradle` (there is nothing to scan — the dependency simply is not declared) and, if
a later phase wants it mechanical, by a build-time check that `:conformance`'s configurations contain
no `project(':mod')` entry. Left as inspection for v0.1; a violation would be a one-line, obvious
diff.

### 8.3 Fixtures

Two kinds, both introduced here:

1. **`GLCapabilityProfile` fixtures** under `conformance/src/test/resources/profiles/`, captured from
   real hardware via `-Dschmaloogium.debug.dumpCapabilities` (§4.9.3). Phase 1 defines the format and
   the capture mechanism; Phase 2 owns the fixture *set* (which GPUs, which minima, how they are
   refreshed).
2. **`GLCallLog` golden files**, rendered by `GLCallLog.render()`. Phase 1 guarantees the format is
   stable and deterministic — that guarantee is a testable property (`RecordingGLDeviceTest`) and it
   is what makes golden files viable at all. Phase 2 owns the golden-file workflow.

**No shader pack is ever committed.** §G6's resolved fixture policy (OQ-11) applies from this phase
forward: CI downloads at test time with a local cache, and re-hosting is prohibited for all seven
matrix packs. Nothing in Phase 1 needs a pack, so there is nothing to get wrong yet — recorded so it
stays that way.

### 8.4 Conformance tiers

Phase 1 exercises **no** conformance tier. T0–T3 are defined by Phase 2 and first run when a renderer
exists (Phase 7). What Phase 1 contributes to that future is the third slice of §G6's testability
split — the machinery for "per-phase headless tests … against the `engine.gl` facade / recorded
`GLCapabilityProfile`s" — and the `:conformance` module the harness lives in.

---

## 9. Milestone staging

Per §G4.3, every designed component carries exactly one tag meaning "implemented at that milestone".

| Component | Tag | Note |
|---|---|---|
| Gradle module split (`:engine`/`:mod`/`:conformance`) | `v0.1` | |
| Seam tests C-1, C-2, C-3 | `v0.1` | C-1 is the Impl gate |
| C-4 (inspection) | `v0.1` | |
| Template conversion (package, mod id, Blossom, metadata) | `v0.1` | |
| ATs disabled; `modid_at.cfg` deleted | `v0.1` | The re-enable path is documented, not built |
| `enable_lwjglx = false` | `v0.1` | |
| Version pin table + `PINS.md` + re-pin procedure | `v0.1` | Re-run at every milestone thereafter |
| `LICENSE` (GPL-3.0), SPDX headers, `THIRD-PARTY.md` | `v0.1` | |
| `GLCapabilityProfile` + serialization | `v0.1` | |
| `GLDevice`, `ShaderService`, `UniformService`, `TextureService`, `FramebufferService`, `StateService`, `DrawService` | `v0.1` | Interfaces + the LWJGL3 implementation |
| Opaque handle types | `v0.1` | |
| `RecordingGLDevice`, `GLCallLog`, `ScriptedResponses`, `ReplayAssertions` | `v0.1` | D-10 requires the headless path from week one |
| `CapabilityProbe` + `dumpCapabilities` | `v0.1` | |
| `DebugService` **interface** | `v0.1` | Present so call sites exist |
| `DebugService` **implementation** (KHR_debug labels/groups) | `v0.5` | Phase 14 |
| `schmaloogium.debug.glLabels` | `v0.5` | Phase 14 |
| `schmaloogium.debug.recordGL` | `v0.1` | |
| `Log`/`LogSink`/`Logs` + fixed channel list | `v0.1` | |
| `EngineDiagnostic` + routing (`CHAT`, `LOG_ONLY`) | `v0.1` | |
| `EngineDiagnostic` routing to `SHADER_GUI` | `v0.4` | The sink is Phase 12's screen; the store exists at v0.1 |
| `MixinConfigs` manifest attribute + `schmaloogium.default.mixin.json` | `v0.1` | The config exists; its contents arrive with Phase 7 |
| `schmaloogium.preinit.mixin.json` (empty, reserved) | `v0.1` | First tenant expected `v0.3` (Phase 10) |
| `schmaloogium.mod.mixin.json` (empty, reserved) | `v0.1` | First tenant expected `v0.3` |
| `SchmaloogiumMixinPlugin` slot | `v0.3` | Reserved at v0.1, content at v0.3 |
| Mixin dev flags (`mixin.debug.export`, `mixin.checks.interfaces`) | `v0.1` | |
| `CompatCheck`/`CompatVerdict`/`CompatContext`/`BailRegistry` mechanism | `v0.1` | |
| Registered compat checks (the policy) | `v0.3` | Phase 10 / OQ-5 |
| `BailRegistry` evaluation point 1 (bootstrap) | `v0.1` | |
| `BailRegistry` evaluation points 2 (vertex-format change) and 3 (mixin plugin) | `v0.3` | Both need Phase 10 to exist |
| CI: seam test step, artifact path fixes, test-report upload | `v0.1` | |
| CI: `conformance` job stub | `v0.1` | Empty slot; Phase 2 fills it |

---

## 10. OQ & spike specifications

Per §G4.4: verbatim question, concrete procedure, success/failure criteria, and a fallback designed
now.

### 10.1 OQ-2 — Cleanroom loader pin

**Question, verbatim from RESEARCH.md §11:**

> Current Cleanroom loader vs template's 0.5.17-alpha pin | Alpha drift; daily cadence | build setup
> | GitHub releases | **RESOLVED 2026-07-24**: 0.6.6-alpha current; **standing item** — re-verify at
> design time and pin deliberately

**Status at this phase.** Re-verified 2026-07-24 for this document: `0.6.6-alpha` remains current,
confirmed independently by the GitHub releases API and by `<release>` in
`repo.cleanroommc.com`'s `maven-metadata.xml`. Two releases shipped on the re-verification date
itself, which corroborates the daily cadence rather than contradicting the pin. **Pinned:
`0.6.6-alpha`**, as `cleanroom_loader_version` in `gradle.properties`.

Because OQ-2 is "resolved, standing", the spike is not an investigation — it is the recurring
procedure. §4.2.6 states it operationally; here it is in §G4.4 form.

**Procedure.**
1. Trigger: before every milestone tag, before any release-workflow run, and on suspicion of a
   platform-caused failure. Never automatic, never scheduled.
2. Read `<release>` from `https://repo.cleanroommc.com/releases/com/cleanroommc/cleanroom/maven-metadata.xml`.
3. Cross-check against `https://api.github.com/repos/CleanroomMC/Cleanroom/releases?per_page=10`.
4. Diff release notes from the current pin forward; flag any mention of CleanMix, MixinBooter,
   Foundation, classloader, mod discovery, LWJGL, or the render path.
5. Bump `cleanroom_loader_version`; `./gradlew build`, `./gradlew :engine:test :conformance:test`, and
   a manual `:mod:runClient` smoke run to the main menu.
6. Append a row to `PINS.md`.

**Success criteria.** The build succeeds, all module tests pass, the client reaches the main menu,
and (once a renderer exists) the harness's runnable subset is unchanged. `PINS.md` has a new row.

**Failure criteria.** Any of: resolution failure, compile failure, test failure, client crash, or a
behavioral change in the harness subset attributable to the bump.

**Fallback, designed now.** Revert `cleanroom_loader_version` to the last known-good value — a
one-line revert, which is the entire reason the pin is a property. Record the failed attempt in
`PINS.md` with the symptom and the release notes entry suspected. Raise it upstream via the §7.7
engagement channel. **A broken alpha blocks the bump, never the milestone.** The project ships
against the last known-good loader; there is no scenario in which platform churn stalls a release,
because we never depend on an unpinned version.

### 10.2 OQ-12 — licensing

**Question, verbatim from RESEARCH.md §11:**

> GPL-3.0-or-later mod on LGPL-2.1 platform + LGPL-3.0 GUI dep; jar-in-jar implications | licensing
> hygiene | §10.3 | short considered note; ecosystem precedent survey | open — concern reduced by the
> `[D-7]` GPL-3.0-or-later change (LGPL-3.0 combines cleanly)

**Procedure.** The verification path RESEARCH.md names is "short considered note; ecosystem precedent
survey" — which is what §4.8.4 is. The remaining procedure is confirmation, not investigation:
1. A reviewer reads §4.8.4 against RESEARCH.md §10.3 and confirms the three characterizations
   (platform-not-library; LGPL-3.0-into-GPL-3.0-or-later is the clean direction; jar-in-jar is
   distribution of the LGPL work and carries the LGPL's notice obligations).
2. When Phase 12 decides ModularUI's arrangement (mod dependency vs `contain`), that decision is
   checked against §4.8.4's obligation (b) and, if it bundles, a `THIRD-PARTY.md` entry plus the
   shipped license text is verified present in the built jar.

**Success criteria.** §4.8.4 survives review with no correction, and the Phase 12 arrangement — when
made — carries its notice obligations. OQ-12's status moves to resolved-by-note.

**Failure criteria.** A reviewer identifies a combination the note mischaracterizes, or a
distribution channel imposes a term inconsistent with GPL-3.0-or-later.

**Fallback, designed now.** If the ModularUI arrangement turns out to be problematic under bundling,
fall back to arrangement (1): declare ModularUI as an ordinary mod dependency and do not bundle it.
That removes the distribution question entirely at the cost of one more install step for users, and
requires no license change and no code change — only a build-file line. If a broader problem is found
with GPL-3.0-or-later itself, that is **not** this phase's call to make: D-7 is a user decision
(RESEARCH.md §1.3, "user decision 2026-07-24"), and §G1.1 says decisions contradicting D-1..D-10 are
to be flagged, not made. It would be flagged in §11.4 as a requested upstream change.

### 10.3 OQ-20 — seam hardness

**Question, verbatim from RESEARCH.md §11:**

> **Kirino-Engine trajectory**: timeline, default-on?, does a compat-profile vanilla pipeline survive
> beneath it, license terms | Every render-loop hook + the vertex pipeline could be invalidated; also
> the best future backend | long-term architecture (§5.2, §7.2) | track repo + #405; engage upstream
> (§7.7) | open — **highest-weight strategic risk**

**This phase's share.** §G10 assigns OQ-20 to "G8/S5 + **P1** (seam hardness requirement)". Phase 1
does not forecast Kirino's trajectory — that is G8/S5's. Phase 1 owns the question *"is our seam
actually hard enough to survive the swap that trajectory might force?"* The facade granularity
decision (§4.7.1) is an answer to that question, and an untested answer is a guess.

**Procedure — the backend-swap drill.**
1. Read Kirino-Engine's *public API surface only* (its README and the public types of its
   RenderPass/Subpass/render-command model). **API surface only** — RESEARCH.md §10.3 records Kirino
   as a custom "Custom Mod Permissions License" with the instruction "Observe API surface only;
   licensing needs review before any integration." Nothing is copied; nothing is derived.
2. On paper, map each of the seven `engine.gl` services onto that model. For each service method,
   record: *directly expressible*, *expressible with buffering* (the call must be deferred into a
   command list before submission), or *not expressible*.
3. Count the `:engine` classes that would need to change if the facade were reimplemented over that
   model. The recording backend already proves the facade admits at least one non-LWJGL
   implementation; this drill asks whether it admits a *structurally different* one.
4. Independently, write a `NullGLDevice` (every call a no-op, every query answered from a supplied
   profile) and run `:engine`'s tests against it. Any test that fails reveals a place where engine
   logic depends on GL *behavior* rather than on the facade *contract* — i.e. a seam leak.
5. Timing: run the drill once at the end of the Phase 1 implementation session, and again whenever
   Kirino's public API materially changes.

**Success criteria.** Step 3 yields **zero** `:engine` classes needing change, with every service
method landing in *directly expressible* or *expressible with buffering*. Step 4: all `:engine` tests
pass against `NullGLDevice`, except those that assert on recorded calls (which by construction need
the recorder).

**Failure criteria.** Any service method is *not expressible*; or step 3 requires changes to
`:engine` classes outside `engine.gl` itself; or step 4 surfaces engine logic depending on GL
behavior the facade does not promise.

**Fallback, designed now.** If the facade proves too fine-grained, coarsen it one level: replace the
imperative service calls on the hot path with **submitted descriptions** — the engine builds a
`RenderPassDescription` value (targets, program, uniform set, draw) and hands it to
`GLDevice.submit(...)`, and the backend decides how to realize it. That is a mechanical
transformation of the call sites in `engine.buffers`/`engine.registry`, not a redesign, *provided*
handles are already opaque and no GL constant has leaked into engine code — which is exactly why
§4.7.1 and §4.7.3 make those choices now. The fallback's viability is itself a reason for the opaque-
handle decision, and that is the point of designing it before it is needed.

**Upstream action** (RESEARCH.md §7.7, which the G8/S5 sketch also names): post Schmaloogium's hook
requirements to CleanroomMC Discussion #405 as a concrete consumer use case — a shader engine needs
pass insertion points, program substitution at draw time, and framebuffer routing control. A
sanctioned API that already accommodates a shader engine is the best possible resolution of OQ-20,
and it is likelier if the requirement is stated while the design is open.

### 10.4 OQ-21 — lwjglx flux

**Question, verbatim from RESEARCH.md §11:**

> lwjglx replacement flux (LWJGLXX/LWJGLY) | template has `enable_lwjglx=true`; legacy-GL shim
> behavior may change under us | build config; §6.1 | track CleanroomMC repos | open

**This phase's disposition.** The compile-time half is decided unconditionally (§4.6:
`enable_lwjglx=false`; compile against LWJGL3 proper), because §6.1 and §G2.2 make it binding. The
spike covers only the runtime half, which is genuinely open.

**Procedure.**
1. Build the mod jar with `enable_lwjglx=false` and confirm the §4.3 bytecode assertion reports no
   `org.lwjglx` reference — the compile-time guarantee, mechanically.
2. Launch a Cleanroom client of the pinned loader version **with** lwjglx present (the default
   install) and confirm the mod loads, the capability probe produces a plausible profile, and the
   `GLCapabilityProfile` values match what the hardware should report.
3. Launch **without** lwjglx, if the loader permits omitting it, and repeat. If it cannot be omitted,
   record that as the finding — "lwjglx is not optional on this loader version" is itself an answer,
   and a more useful one than a guess.
4. Compare the two profiles. A difference means the shim is intercepting capability queries, which is
   material to Phase 7's OQ-3 spike and gets handed there.
5. Re-check the CleanroomMC org for LWJGLXX and LWJGLY status at each milestone re-pin (§10.1 step 3
   already reads the release notes; add the two repos to that pass). LWJGLY was an *empty placeholder
   repo* at 2026-07-24; the first commit to it is the signal to look properly.

**Success criteria.** The jar contains no `org.lwjglx` reference; the mod loads and probes correctly
in both configurations; the two `GLCapabilityProfile`s are identical.

**Failure criteria.** The mod fails to load in either configuration; or the profiles differ; or the
loader gains a hard dependency on a shim whose API we would have to compile against — the one
outcome that would force revisiting §4.6.

**Fallback, designed now.** If a future loader release genuinely requires compiling against a shim
API, re-enable the dependency as `compileOnly` with a **pinned** version, add the shim's package to
an explicit allowlist in the §4.3 bytecode assertion (so the exception is visible in the test rather
than by deletion of the rule), and confine every reference to `mod.glue` — where §G4.6 already
confines LWJGL. `:engine` remains untouched under every branch of this fallback, because it never
referenced graphics APIs in the first place. That is the seam doing its job on a question it was not
designed for, which is the best available evidence that it is drawn in the right place.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale |
|---|---|---|
| D-P1-1 | `mod_id = schmaloogium`, `root_package = com.schmaloogium`; Blossom's `package` property overridden to `root_package` alone | The spec proposes `schmaloogium`; the template's `"${root_package}.${mod_id}"` derivation would otherwise produce `com.schmaloogium.schmaloogium` |
| D-P1-2 | `rootProject.name` pinned to the literal `'Schmaloogium'` | Under a multi-project build the root name leaks into IDEA module keys and publishing; directory-name derivation makes the build depend on the clone path |
| D-P1-3 | ASM is permitted in `:engine` at `testImplementation` scope only | Needed by the bytecode architecture test; not a forbidden coordinate; never on the production classpath the test asserts over |
| D-P1-4 | `:engine` classes are **merged** into `:mod`'s jar (not `contain`, not shadow) | Same codebase, same license, same package root — nothing to isolate. `contain` adds a load-time extraction path for a first-party module; shadow flips the active remap task for no gain |
| D-P1-5 | The loader pin lives in `gradle.properties` as `cleanroom_loader_version`, not inline in `build.gradle` | Makes the re-pin procedure a one-line, reviewable, revertible diff — which is what turns OQ-2 from a risk into a routine |
| D-P1-6 | The `:engine`-internals rule is a package convention (`.internal`) enforced by a bytecode scan; **JPMS is rejected** | `:mod` runs on Foundation's flat classpath where the module graph does not exist; `:engine` as an automatic module opens all packages, so the guarantee would evaporate exactly where it matters |
| D-P1-7 | No access transformers for v0.1; `use_access_transformer=false`; `modid_at.cfg` deleted; the `rootProject.projectDir` path bug fixed pre-emptively | The spec prefers none until a hook requires one; no Phase 1 component needs one; disarming the path trap now costs nothing and saves Phase 7 a debugging session |
| D-P1-8 | License stated in `mod_credits` + `LICENSE` + `README.md`; **no** `mcmod.info` `license` key | The 1.12.2 `mcmod.info` schema has no such key `[V:mcp]`; inventing one produces metadata nothing reads |
| D-P1-9 | Mixin configs declared via the `MixinConfigs` jar-manifest attribute, sourced from a `mixin_configs` property | Current canon; legacy MixinBooter loader interfaces are `@Deprecated` `[V:mcp]` `[RESEARCH.md §5.1]` |
| D-P1-10 | `is_coremod` stays `false` | CleanMix is built into the loader; coremods are discouraged; adding one opens a class-transformation path D-5 has no use for |
| D-P1-11 | Three mixin configs, one per CleanMix phase (`PRE_INIT`/`DEFAULT`/`MOD`); `"server": []` is permanent | Phases are the axis CleanMix dispatches on; splitting later means editing manifest, files, and every `@Mixin` package at once. Schmaloogium is client-only (§1.2) |
| D-P1-12 | SRG names in every annotation; MCP names in comments; injected methods prefixed `schmaloogium$` | App E's stated requirement; the prefix is cheap collision insurance in a coremod-heavy ecosystem |
| D-P1-13 | Refmap generation left to Unimined; `disableRefmap()` not called | Template README and the MCP guide agree; but the `main` branch has never had a config, so first-config refmap generation is an explicit checklist item |
| D-P1-14 | `enable_lwjglx = false` | Its only effect is `compileOnly org.lwjglx`; with it on, an illegal import compiles silently. §6.1/§G2.2 make the rule binding, so the build should enforce it |
| D-P1-15 | Grouped role services + opaque handles, not a thin GL-verb layer | A GL-verb facade is OpenGL with a different package name; it encodes imperative semantics into `:engine` and would make the Kirino swap a rewrite (OQ-20) |
| D-P1-16 | `GLCapabilityProfile` has a stable, sorted, human-readable text serialization | It is what "recorded `GLCapabilityProfile`s" means for Phase 2 and what Phase 4/5/6's "recorded-GL run" gates run against; diff-readability matters when a driver update changes one extension |
| D-P1-17 | `LICENSE` carries verbatim GPL-3.0; "or-later" lives in SPDX headers and `README.md` | The license text is version-specific; the "or later" grant is a statement about the work, which is where GPL-3.0's own guidance puts it |
| D-P1-18 | Two-line SPDX header on every source file | Machine-readable, no boilerplate duplication, and the form license scanners actually read |
| D-P1-19 | `:engine` defines a zero-dependency `Log`/`LogSink` SPI instead of depending on log4j | log4j on 1.12.2 comes from the Minecraft runtime; depending on it would be coupling that exists only because Minecraft supplies it, and would make headless tests need a logging backend |
| D-P1-20 | The §4.9.2 channel list is fixed; channels are constants, never composed | §G4.5 assigns the list to this phase; composed channel names make a user's log4j filter meaningless |
| D-P1-21 | Debug flags namespaced `schmaloogium.debug.*`, boolean, absent = off, read after bootstrap | §G4.5 requires `saveSources` reserved; a uniform namespace makes the set discoverable and keeps malformed values away from class loading |
| D-P1-22 | `:engine` emits `EngineDiagnostic` values with lang **keys**; `:mod` routes to chat/GUI/log | The seam forbids Minecraft types in `:engine`, and Phase 12 needs lang keys for the GUI regardless |
| D-P1-23 | The bail registry ships with **zero** registered checks and names no mod ids | Naming Celeritas or Nothirium here would be Phase 10's policy decision made by the wrong session; the landscape moves (RESEARCH.md §2.3) |
| D-P1-24 | CI runs `:engine:test` as its own named "Seam architecture test" step | The seam is the project's highest-weight structural risk; its regression should be legible at a glance, not buried in an aggregate build |

### 11.2 D-1..D-10 disposition

The Doc gate requires every binding decision to be either satisfied by this phase or explicitly
deferred with its owner named.

| ID | Decision (short form) | Disposition |
|---|---|---|
| **D-1** | Cleanroom-exclusive | **Satisfied.** Unimined's `cleanroom { }` loader block (no `forge`/`fabric` block), `ModType: CRL` manifest, no compatibility shim for stock Forge, and no abstraction layer pretending otherwise. §1.2 of RESEARCH.md keeps a later port *possible* via the seam without making it a goal — which is exactly what §4.3's C-1 delivers as a side effect. |
| **D-2** | Shaders only; written non-goals list | **Satisfied structurally.** No package in §2.1 corresponds to any §1.2 non-goal: there is no perf package, no MCPatcher-feature package, no telemetry, no installer, no server package. The layout makes scope creep visible as a new top-level package rather than a quiet addition. |
| **D-3** | Target = the fixed pack-compatibility matrix (App G), not "Iris parity" | **Deferred → Phase 2.** The matrix is the definition of done and the tiers T0–T3 are Phase 2's to define (§G6). Phase 1 contributes the `:conformance` module the machinery lives in and the fixture policy note (§8.3) that no pack may ever be committed. |
| **D-4** | Stage registry architected for the full modern stage set from day one | **Deferred → Phase 4.** Phase 1 reserves `com.schmaloogium.engine.registry` and nothing more; designing the registry's shape here would be Phase 4's work done by the wrong session. Recorded so Phase 4 knows the package is its own and empty by intent. |
| **D-5** | Mixin-based hooks only; no class replacement; ~25–30 targeted injections | **Satisfied at the wiring level; catalog deferred → Phase 7** (with additions from Phase 10 and Phase 13). §4.5 provides the manifest declaration, three phase-scoped configs, the SRG policy, and refmap handling. `is_coremod=false` (D-P1-10) closes the class-transformation door, and no `@Mixin(remap=…)`-style class-replacement affordance appears anywhere. Zero mixin classes are authored here. |
| **D-6** | Engine-core / loader-glue seam; core headless-testable and GL-abstracted | **Satisfied — this phase's core deliverable.** §4.3 states it as constraints C-1..C-4 and enforces C-1..C-3 with tests; §4.7 provides the GL abstraction and the headless backend that makes "testable via JUnit alone" true rather than aspirational. §2.2 records *why* it is a requirement (§5.2/OQ-20), per the spec's instruction. |
| **D-7** | GPL-3.0-or-later license (template's MIT-style LICENSE must be replaced) | **Satisfied.** §4.8: `LICENSE` restored with verbatim GPL-3.0 text, two-line SPDX headers on every source file, license stated in `mod_credits`/`README.md`, and `THIRD-PARTY.md` as the compliance mechanism. Note the found repo-state defect in §11.3 — the swap was made and then undone, so the task is restore, not replace. |
| **D-8** | Published docs + OSS source OK; LGPL-3.0 reuse with compliance; two prohibitions | **Satisfied as convention.** §4.8.3 creates `THIRD-PARTY.md` with the per-incorporation entry format (files / upstream / notice / modifications) and carries both standing prohibitions at its head: never copy from glsl-transformer (AGPL-3.0), and the OptiFine decompile is behavioral-observation-only. The mechanism exists before the first incorporation, which is the only time it can be built cheaply. |
| **D-9** | Compatibility-profile GL baseline; no core-profile rewrite | **Deferred → Phases 5+/7 for policy; enabled here.** Phase 1 owns no GL policy (explicitly Scope-out). What it does is make the constraint expressible and testable: the facade contains no core-profile-only entry point, `UniformService` has no UBO method at all (so the pack contract's prohibition cannot be violated), `DrawService.fullscreenQuad()` leaves the `GL_QUADS`-vs-triangle-strip choice to the backend, and `GLCapabilityProfile` makes every capability gate assertable headlessly. |
| **D-10** | Conformance harness from week one | **Deferred → Phase 2; unblocked here.** §G6 defines D-10's "week one" as the runnable-before-renderer subset: fixture downloader, preprocessor golden runs, capability-profile replay. Phase 1 supplies two of the three prerequisites — the `:conformance` module with JUnit wiring, and the capability-profile record/replay machinery (§4.7.5) plus its serialization format (§4.7.2). The third (the downloader) and all harness content are Phase 2's. |

### 11.3 Input contradictions, defects, and inherited values found

Reported, not smoothed over (§G1.1).

**Contradictions between inputs.**

1. **Extension set attributed to RESEARCH.md §4.1 by DESIGN.md, but not present there.** Detailed
   with its ruling in §3.1. Included, tagged `[A]` rather than `[V:observed]`, because §3.5's
   `MC_<GL_extension>` macros independently require it.

**Defects found in the template `[V:template]`** — all pre-existing, none introduced by this design:

2. Maintainer override: Defect details deleted; behavior was intentional.
3. **No `modImplementation` configuration exists.** The template's README and `dependencies.gradle`
   comments both instruct you to use `modImplementation` — "You **MUST** add mods by using
   `modImplementation` or `modRuntimeOnly`" — but only `modCompileOnly` and `modRuntimeOnly` are
   declared, and only those two are passed to Unimined's `mods { remap(...) }` block. Adding a mod
   dependency at both compile and runtime today requires declaring it twice. Phase 12 (ModularUI) is
   the first phase likely to hit this; the fix is to declare the configuration and add it to the
   remap list.
4. **`extra_jvm_args` parsing is broken.** `extraArgs.split { "\\s+" }` invokes Groovy's
   `CharSequence.split(Closure)` — which *partitions* into matching/non-matching lists — not
   `String.split(String regex)`. Dead code today because the property is empty; it will produce
   garbage `jvmArgs` the moment anyone sets it. Fix: `extraArgs.trim().split(/\s+/).toList()`.
5. **`gradle/scripts/extra.gradle`'s comment is false.** It claims "Helper methods (assertProperty,
   assertSubProperties, setDefaultProperty) are defined directly in build.gradle's script scope and
   exported via ext." No such methods exist; `ext` contains only `access_transformer_locations`.
   Either implement them or delete the comment — a comment describing an API that does not exist is
   worse than none.
6. **`publish_to_local_maven` is documented but never read** by any script. Either wire it or remove
   it from `gradle.properties`.
7. **The Unimined access-transformer path hardcodes `rootProject.projectDir`.** Harmless in a
   single-project template, fatal under the split. Fixed pre-emptively in §4.4.3 even though the
   branch is inert.
8. **All three CI workflows hardcode root-relative `build/libs`.** Fixed in §4.11.

**Inherited values worth a later spot check.**

9. **`compatibilityLevel: "JAVA_8"`** in the mixin configs is taken verbatim from the template
   snapshot while the project's source level is Java 25. The field constrains mixin-class bytecode,
   not project source, so this is very likely correct — but it is a value we inherited rather than
   derived, and the first mixin using a language feature that survives to bytecode above Java 8 is
   the moment to confirm it. Flagged for Phase 7.
10. **Unimined refmap generation is unexercised in this checkout** — the `main` branch has never had a
    mixin config. The first config to land should be verified to produce a refmap in the built jar
    before hook work proceeds (§12 item 22).

### 11.4 Items handed onward

**To Phase 2** — the `:conformance` module, its JUnit wiring, and the CI `conformance` job stub are
empty slots by intent, not omissions. The `GLCapabilityProfile` text format (§4.7.2) and the
`GLCallLog.render()` stability guarantee (§4.7.5) are the two contracts your golden-file workflow
should build on. Phase 1 supplies no fixture set and no answer to OQ-10.

**To Phase 3** — `schmaloogium.debug.saveSources` is reserved for you and unimplemented.
`GLCapabilityProfile.extensions()` is the source for `MC_<GL_extension>` macros.

**To Phase 7** — the three mixin configs exist and are empty. Verify refmap generation before
building the hook catalog on top (§11.3 item 10). `compatibilityLevel` is worth your spot check
(item 9). The `BailRegistry` bail hook is wired at bootstrap and awaits your frame-driver
integration.

**To Phase 10** — the bail registry mechanism is complete and has no registered checks. The
`Degrade` verdict exists specifically so that if OQ-5 resolves toward "integrate" rather than "bail",
you are not forced to widen a mechanism you were told to reuse. The `SchmaloogiumMixinPlugin` slot on
the MOD-phase config is the strongest veto point available (§4.10) — a vetoed mixin never applies, so
there is no partial instrumentation to unwind.

**To Phase 12** — the `SHADER_GUI` diagnostic channel and its per-pack error store exist; your screen
is the sink. Your ModularUI arrangement decision carries the §4.8.4 obligation (b). The
`modImplementation` defect (§11.3 item 3) will likely bite you first.

**To Phase 14** — `DebugService` exists as an interface with call sites from v0.1; its implementation
and `schmaloogium.debug.glLabels` are yours. Facade extensions must be additive (your spec says so);
§4.7.4's structure is designed for that.

**To G8/S5** — §10.3's backend-swap drill is the instrument for judging whether the seam held. Its
fallback (submitted `RenderPassDescription`s) is the pre-designed coarsening if it did not.

**Candidate, not adopted:** a CI license-header lint. Useful, cheap, and unasked-for. Recorded here
rather than added.

### 11.5 Requested upstream changes

**To RESEARCH.md** — two, both minor and both for the maintainer of that document, not for a phase
session:

1. §11's OQ-2 row and §5.1's loader-pin row both say "current is 0.6.6-alpha". Re-verified true on
   2026-07-24. When the status column is next updated, the re-pin procedure now lives in
   PHASE_1_DOC §4.2.6 / §10.1 and `PINS.md` is the ledger — worth a pointer so the standing item has
   an owner-of-record.
2. §11's OQ-12 row can move to resolved-by-note once §4.8.4 clears review (§10.2's success criterion).

**To DESIGN.md** — one:

3. Phase 1's scope line attributes the "extension set" to RESEARCH.md §4.1's probe set; §4.1 lists
   four probes and does not include it (§3.1). The requirement is real but sourced from §3.5. A
   half-sentence correction would prevent a future reader from looking for it in the wrong place.

Per §G1.1 neither document is modified by this session.

---

## 12. Implementation checklist

Ordered so that each item is independently actionable and the Impl gate — *"project builds empty
modules + passes an architecture test proving `:engine` has no MC/loader/mixin/LWJGL classpath; CI
green"* — is reached at item 15, with the rest completing the phase's scope.

Tags: `[v0.1]` etc. per §G4.3. Test hooks name the check that proves the item.

### Structure and build

| # | Item | Tag | Test hook |
|---|---|---|---|
| 1 | Restore `LICENSE` with verbatim GPL-3.0 text; un-stage the deletion | `v0.1` | File present at repo root; `git status` clean for it |
| 2 | Update `gradle.properties` per §4.4.1 (ids, package, version, `use_access_transformer=false`, `enable_lwjglx=false`, `cleanroom_loader_version`, `mixin_configs`) | `v0.1` | `./gradlew properties` shows the expected values |
| 3 | Rewrite `settings.gradle`: literal `rootProject.name`, three `include`s | `v0.1` | `./gradlew projects` lists `:engine`, `:mod`, `:conformance` |
| 4 | Rewrite root `build.gradle` as the §4.2.2 aggregator (plugins `apply false`, `subprojects` toolchain/encoding/JUnit, `allprojects` group/version, idea-ext) | `v0.1` | `./gradlew help` succeeds; root produces no jar |
| 5 | Create `engine/build.gradle` per §4.2.3 (no Unimined, no Blossom, mavenCentral only, ASM at test scope, classpath system properties) | `v0.1` | `./gradlew :engine:compileJava` succeeds |
| 6 | Create `conformance/build.gradle` (`java-library`, JUnit, `implementation project(':engine')`, no `:mod`) | `v0.1` | `./gradlew :conformance:compileJava`; C-4 by inspection |
| 7 | Create `mod/build.gradle`: move the Unimined block (loader from property, AT path project-relative), Blossom with the `package` override, `dependencies.gradle` + `publishing.gradle` applied here, `implementation project(':engine')`, `jar { from :engine output }` | `v0.1` | `./gradlew :mod:jar` produces a jar containing `com/schmaloogium/engine/**` |
| 8 | Move sources: `mod/src/main/java/com/schmaloogium/mod/core/{SchmaloogiumMod,proxy/*}`, `mod/src/main/java-templates/com/schmaloogium/Reference.java`, `mod/src/main/resource-templates/{mcmod.info,pack.mcmeta}`. Drop the client-only call from the shared `@Mod` class | `v0.1` | `./gradlew :mod:build`; generated `Reference` is at `com.schmaloogium.Reference` |
| 9 | Delete `src/main/resources/modid_at.cfg` and the now-empty root `src/` tree | `v0.1` | No `FMLAT` attribute in the built manifest |
| 10 | Retarget the idea-ext run configurations at `:mod:runClient` / `:mod:runServer`; per-module `moduleJavacAdditionalOptions` keys | `v0.1` | IDEA sync produces working run configs |

### The seam

| # | Item | Tag | Test hook |
|---|---|---|---|
| 11 | `SeamClasspathTest` in `:engine` — constraint C-1, classpath half | `v0.1` | Passes; fails informatively when a forbidden coordinate is added deliberately |
| 12 | `SeamBytecodeTest` in `:engine` — constraint C-1, bytecode half. **The Impl gate's test** | `v0.1` | Passes; fails when a `net.minecraft.*` reference is added deliberately |
| 13 | `SeamInternalsTest` in `:mod` — constraint C-2 | `v0.1` | Passes; fails on a deliberate `.internal.` reference |
| 14 | `SeamLwjglConfinementTest` in `:mod` — constraint C-3 | `v0.1` | Passes; fails on an `org.lwjgl` reference outside `mod.glue` |
| 15 | **Impl gate reached**: `./gradlew build` succeeds across all three modules and `./gradlew :engine:test` passes | `v0.1` | Both commands green locally |

### The GL facade

| # | Item | Tag | Test hook |
|---|---|---|---|
| 16 | `GLCapabilityProfile` record + `atLeast`/`hasExtension`/`supportsMipmapGeneration`, with defensive copy of `extensions` | `v0.1` | `GLCapabilityProfileDerivationTest` |
| 17 | `GLCapabilityProfile.parse`/`write` in the §4.7.2 sorted text form | `v0.1` | `GLCapabilityProfileSerializationTest` — round-trip identity, deterministic output |
| 18 | Handle types (`GLHandle` + the five sealed sub-interfaces, `UniformLocation.isAbsent()`) | `v0.1` | Compiles; used by item 19's signatures |
| 19 | The seven service interfaces + `GLDevice` + result/value types (`CompileResult`, `LinkResult`, `ValidateResult`, `TextureSpec`, `BlendState`, `StateSnapshot`, …) | `v0.1` | Compiles with no GL constant and no `int` object name in any signature — verified by review against §4.7.4 |
| 20 | `RecordingGLDevice`, `GLCall`, `GLCallLog`, `ScriptedResponses` | `v0.1` | `RecordingGLDeviceTest` — ordering, distinct handles, scripted failures, stable `render()` |
| 21 | `ReplayAssertions` incl. `bindsBalanced()`, `noLeakedObjects()`, `drawBuffersWere()` | `v0.1` | `ReplayAssertionsTest` — each assertion passes on conforming and fails informatively on violating logs |
| 22 | `Lwjgl3GLDevice` + the seven service implementations in `mod.glue` | `v0.1` | `SeamLwjglConfinementTest` (item 14) confines it; a manual `runClient` reaching the main menu |
| 23 | `CapabilityProbe` in `mod.glue` + `-Dschmaloogium.debug.dumpCapabilities` | `v0.1` | Running the client with the flag writes a parseable profile that round-trips through item 17 |
| 24 | `-Dschmaloogium.debug.recordGL` decorator wrapping the live device | `v0.1` | Flag produces a `GLCallLog` dump in the same format the tests assert over |

### Conventions

| # | Item | Tag | Test hook |
|---|---|---|---|
| 25 | `Log`/`LogSink`/`Logs` + `LogChannels` constants (§4.9.2) | `v0.1` | `LogChannelTest` — uniqueness, prefix, no-op sink before install |
| 26 | log4j-backed sink installed in `mod.core` at `preInit` | `v0.1` | A `runClient` shows `schmaloogium.boot` lines |
| 27 | `EngineDiagnostic`/`DiagnosticSeverity`/`UserChannel`/`DiagnosticReporter` + `:mod` routing | `v0.1` | `DiagnosticRoutingTest` — including `CHAT`-with-no-player degradation |
| 28 | SPDX headers on every source file; `README.md` license statement; `THIRD-PARTY.md` with both standing prohibitions and no entries | `v0.1` | Manual review; a header lint is a candidate, not adopted (§11.4) |
| 29 | `PINS.md` created with the §4.2.6 table as its first row | `v0.1` | File present; §10.1 step 6 has somewhere to write |

### Mixin wiring

| # | Item | Tag | Test hook |
|---|---|---|---|
| 30 | Three mixin config JSONs per §4.5.2, empty `client`/`mixins`/`server` | `v0.1` | Files present at `mod/src/main/resources/` |
| 31 | `MixinConfigs` manifest attribute wired into `:mod`'s `jar` `doFirst` from `mixin_configs` | `v0.1` | `unzip -p` the built jar's `MANIFEST.MF` shows all three, comma-separated |
| 32 | Dev flags on the client run: `mixin.debug.export`, `mixin.checks.interfaces`, gated on `enable_mixin_debug` | `v0.1` | `runClient` writes `.mixin.out/`; CI sets the property false |
| 33 | **Verify Unimined refmap generation** with a single throwaway no-op mixin, then remove it (§11.3 item 10) | `v0.1` | A refmap appears in the built jar; **blocks Phase 7 if it does not** |
| 34 | Document `-Dcrl.dev.mixin` in the developer README | `v0.1` | Present |

### Compat and CI

| # | Item | Tag | Test hook |
|---|---|---|---|
| 35 | `CompatCheck`/`CompatVerdict`/`CompatContext`/`BailRegistry` + `CompatEvaluation` | `v0.1` | `BailRegistryTest` — aggregation, throwing-check-is-bail, idempotence |
| 36 | Bail evaluation point 1 (pre-bootstrap) wired, with diagnostic routing and the shaders-off terminal state | `v0.1` | A test check returning `Bail` produces the chat diagnostic and the compat log line |
| 37 | `SchmaloogiumMixinPlugin` skeleton on the MOD-phase config (returns true for everything) | `v0.3` | Config loads with the plugin declared |
| 38 | `build.yml`: named "Seam architecture test" step, `:conformance:test` step, artifact glob `**/build/libs/*.jar`, `if: failure()` test-report upload | `v0.1` | CI green on a clean commit; red and legible on a deliberate seam violation |
| 39 | `release.yml` artifacts → `mod/build/libs/*`; `release-to-cf-mr.yml` file globs retargeted | `v0.1` | Dry-run inspection of the resolved paths |
| 40 | `conformance` job stub, `workflow_dispatch`-gated, with the fixture `actions/cache` step | `v0.1` | Workflow parses; job does not run on push |
| 41 | Run the §10.3 backend-swap drill and record the result in this doc as an addendum (§G4.4: "an addendum note in the owning phase doc") | `v0.1` | Drill produces a class-change count; `NullGLDevice` runs `:engine`'s tests |
| 42 | Run the §10.4 OQ-21 runtime checks and record the result; hand any capability-query divergence to Phase 7's OQ-3 | `v0.1` | Both configurations load; profiles compared |

### Fix template defects found (§11.3)

| # | Item | Tag | Test hook |
|---|---|---|---|
| 43 | Declare a `modImplementation` configuration and add it to Unimined's `mods { remap(...) }` list | `v0.1` | A mod dependency declared once resolves at both compile and runtime |
| 44 | Fix `extra_jvm_args` parsing: `extraArgs.trim().split(/\s+/).toList()` | `v0.1` | Setting the property produces the expected `jvmArgs` |
| 45 | Delete or implement `extra.gradle`'s false helper-method comment; wire or remove `publish_to_local_maven` | `v0.1` | Comment matches reality |

---

*End of PHASE_1_DOC.md. Per §G1.1, this build session stops here: the next step in the cadence is a
fresh verify session (§G1.2) producing `PHASE_1_REVIEW.md`.*
