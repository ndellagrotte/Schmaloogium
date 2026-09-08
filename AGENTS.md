# Repository Guidelines

## Project Overview

Schmaloogium targets **client-only OptiFine/Iris-format shader-pack support for Cleanroom on Minecraft 1.12.2**. Scope is shaders, not performance/chunk rewrites, MCPatcher features, cosmetics, telemetry, stock Forge support, or server functionality. Mission and decisions live in `docs/research/v1/RESEARCH.md`; `README.md` is still inherited CleanroomModTemplate guidance.

**Current implementation:** a single-project Java mod template with initialization logging and empty proxies. The shader engine, rendering hooks, multi-module layout, and conformance harness described in phase documents are not implemented.

## Architecture & Data Flow

- **Build:** `gradle.properties` → Blossom templates → generated `Reference` constants and mod metadata → compilation → Unimined remapping → artifacts in `build/libs/`. Edit template inputs, not generated output.
- **Runtime:** Forge discovers `ExampleMod` through `@Mod`, injects `ClientProxy` or `CommonProxy` through `@SidedProxy`, then calls `preInit`. That handler only logs; both proxies directly implement the empty `IProxy` interface.
- **Planned boundary:** headless-testable, GL-abstracted engine core separated from Cleanroom/Minecraft loader glue; targeted Mixin hooks, not class replacement. These are design contracts, not existing packages or configured subprojects. Do not assume `:engine`, `:mod`, or `:conformance` exists.
- The template entry point calls `Minecraft.getMinecraft()` unconditionally. It is not a server-safe example; keep client-only work behind the side boundary rather than propagating that pattern.

## Key Directories

| Path | Purpose / editing guidance |
| --- | --- |
| `src/main/java/com/example/modid/` | Actual mod source; `proxy/` contains the empty side abstractions. |
| `src/main/java-templates/` | Tokenized `Reference.java`; preserve `{{ ... }}` placeholders. |
| `src/main/resource-templates/` | Blossom inputs for `mcmod.info` and `pack.mcmeta`. |
| `src/main/resources/` | Static resources, including enabled `modid_at.cfg`. |
| `gradle/scripts/` | Dependency configuration, optional publishing, and build extension point. |
| `docs/` | Versioned research, designs, phase contracts, decisions, and review evidence. Navigate through `docs/MOVES.md`. |
| `reference-src/` | Gitignored third-party references, not project implementation; source restrictions below apply. |

Do not edit or commit generated `.gradle/`, `build/`, `run/`, or `out/` contents. Do not use `docs/**/chatlogs/` or repository-root `*.txt` transcripts as task inputs; the documentation requires independent evidence.

## Development Commands

Run from the repository root with JDK 25 available through `JAVA_HOME` or `PATH`:

```sh
./gradlew build                              # CI build; artifacts in build/libs/
./gradlew test                               # JUnit Platform; currently no test sources
./gradlew test -Pshow_testing_output=true    # Include test standard streams
./gradlew runClient                          # Development client; needs graphics environment
./gradlew genSources                         # Generate mapped Minecraft sources
```

Windows uses `gradlew.bat`. IntelliJ users should select the Gradle **2. Run Client** configuration, not the blue-icon **Minecraft Client** configuration. `runServer` is configured by the template but is not a supported product target.

No repository formatter or linter is configured; do not invent `lint` or `spotlessApply` tasks. Commands above are configuration-backed, not evidence of a successful local run. `scripts/verify`, `$verify-loop`, and verification profiles are retired and absent; older `docs/tooling/` runbooks are stale on this point.

## Code Conventions & Common Patterns

- Java uses four-space indentation, same-line braces, `UpperCamelCase` types, `lowerCamelCase` members, and `UPPER_SNAKE_CASE` constants; compilation uses UTF-8.
- Reuse Log4j `LOGGER` with parameterized messages, e.g. `LOGGER.info("Proxy is {}", proxy)`, and Forge lifecycle annotations.
- Dependency injection currently means Forge's `@SidedProxy`; state is limited to its injected static field, the logger, and metadata constants. No application DI container, async/executor pattern, state store, or custom error-handling policy exists. Follow the relevant phase contract when introducing these concerns.
- Identity changes must align `gradle.properties`, Java packages/imports, string-qualified proxy targets, template locations, and `${mod_id}_at.cfg`. Do not opportunistically rename the template identity.
- Add dependencies in `gradle/scripts/dependencies.gradle`: `modImplementation`/`modRuntimeOnly` for remapped mods, `modLibrary` for deliberately unremapped dependencies, `contain` for embedded jars. Do not use `fg.deobf` or `rfg.deobf`; preserve Maven Local last. Shadow packaging is disabled by default.
- Access transformers use **MCP names**; Unimined remaps them to SRG and packages matching files under `META-INF`.

### Documentation and source boundaries

- Research takes precedence over design and mining reports. Select the governing design from the target phase's **§0/header**, not the newest directory: `docs/design/v3/DESIGN.md` is not a global default. Use full versioned paths and resolve old citations via `docs/MOVES.md`.
- For phase work, dependency **§5 interfaces** are contracts. Preserve confidence labels and historical reviews/quotes; record phase-local decisions as `D-P<N>-<k>` and proposed upstream changes in §11. Do not silently rewrite upstream research/design or other phases.
- OptiFine decompile is last-resort behavioral observation only: no copied code, structure, or decompile-derived identifiers. Licensed OSS reuse requires notices and marked modifications. Never copy `glsl-transformer` or copy/depend on unresolved `org.taumc:glsl-transformation-lib`.
- Within the Oculus reference checkout, never read, cite, summarize, copy, or depend on `src/main/java/net/coderbot/iris/pipeline/transform/`, `libs/`, or `glsl-relocated/`. Consult the governing design's licensing section before mining references.

## Important Files

- `src/main/java/com/example/modid/ExampleMod.java`: sole mod entry point.
- `src/main/java-templates/com/example/modid/Reference.java`: generated identity inputs.
- `build.gradle`: platform pins, toolchains, templates, launches, and remapped packaging.
- `settings.gradle`: plugin repositories and Foojay toolchain resolver; no included subprojects.
- `gradle.properties`: identity, testing, access-transformer, compatibility, and publishing switches.
- `gradle/wrapper/gradle-wrapper.properties`: authoritative Gradle distribution pin.
- `.github/workflows/build.yml`: wrapper build and artifact upload; release workflows remain template-oriented.
- `docs/MOVES.md` and `docs/research/v1/RESEARCH.md`: documentation navigation and mission/contracts respectively.

## Runtime/Tooling Preferences

Use **Java 25 and the checked-in Gradle wrapper**, not Java 8 despite the Minecraft version. Current pins: Gradle **9.7.0**, Unimined **1.4.36-kappa** (custom fork), Cleanroom **0.6.10-alpha**, Minecraft **1.12.2**, MCP stable **39-1.12**. CI installs Gradle 9.6.1 separately but invokes the wrapper; the wrapper controls the build version.

Uncached setup requires network access for Gradle, dependencies, and potentially Foojay-provisioned toolchains. Gradle owns dependencies; Node/Bun is not a mod build requirement. LWJGLX compatibility is enabled, but property comments discourage new LWJGL2 API usage. Mixin is the planned hook approach, not an already configured source subsystem.

## Testing & QA

- JUnit Jupiter **6.0.3** is enabled through `enable_junit_testing=true`; tests use JUnit Platform and Java 25. There is currently **no `src/test/` or repository-owned test suite**. A successful empty `test` task is not regression coverage.
- No coverage tool or numeric coverage threshold is configured. For implementation work, consult the relevant phase's §8 for behavioral cases; distinguish headless checks from real client/GL verification. Build/toolchain changes should include a client-main-menu smoke check when a graphics runtime is available.
- Multi-module tests, fixture/conformance tasks, opt-in GL tests, and golden-update flags in phase documents are planned interfaces, not current commands. Future rendering QA requires controlled scenes and moving-camera checks, not arbitrary screenshots alone.
- Do not commit/re-host matrix shader packs, pack source in goldens, or rendered images. Keep fixture/image data in local or permitted CI caches; repository evidence should be source-free summaries and provenance/hash manifests.
