# Schmaloogium

Client-only **OptiFine/Iris-format shader-pack support for
[Cleanroom](https://github.com/CleanroomMC/Cleanroom) on Minecraft 1.12.2**. Schmaloogium
loads classic-format shader packs (the `gbuffers_*` / `composite` / `final` pipeline)
through a headless-testable engine core and a set of targeted Mixin hooks.

## License

Schmaloogium is licensed **GPL-3.0-or-later**. The full GPL-3.0 text lives in
[`LICENSE`](LICENSE); the "or later" grant applies through each file's SPDX header.
Third-party material and its provenance are recorded in
[`THIRD-PARTY.md`](THIRD-PARTY.md); dependency pins and their verification in
[`PINS.md`](PINS.md).

## Building

Requires JDK 25 (Gradle toolchains can provision it) and the checked-in Gradle wrapper
(9.7.0). From the repository root:

```sh
./gradlew build                              # CI build; artifacts in mod/build/libs/
./gradlew test                               # JUnit Platform across all modules
./gradlew test -Pshow_testing_output=true    # Include test standard streams
./gradlew :mod:runClient                     # Development client; needs a display
./gradlew :mod:genSources                    # Generate mapped Minecraft sources
```

Windows uses `gradlew.bat`. IntelliJ users should select the Gradle **2. Run Client**
configuration, not the blue-icon Minecraft Client configuration.

## Module layout

| Module | Contents |
|---|---|
| `engine/` | Headless engine core. Zero Minecraft/Forge/Cleanroom/Mixin/LWJGL dependencies (proven by `SeamClasspathTest`/`SeamBytecodeTest`); all engine GL flows through the `engine.gl` facade. |
| `mod/` | Everything loader-facing: the `@Mod` entry point, the LWJGL3 facade backend, Mixin hooks, and the options GUI. |
| `conformance/` | The shader-pack conformance harness. Depends on `:engine` only. |

The architecture, phase contracts, and decisions live under `docs/` — start at
[`docs/MOVES.md`](docs/MOVES.md).

## Mixin debugging

`-Dmixin.debug.export` and `-Dmixin.checks.interfaces` are enabled on client runs by
default (`enable_mixin_debug`); exported classes land in `.mixin.out/` under the run
directory. `-Dcrl.dev.mixin` is the loader's own flag (see the Cleanroom documentation).

Schmaloogium-specific debug flags use the `-Dschmaloogium.debug.*` namespace:
`dumpCapabilities`, `recordGL`, `glLabels`, `saveSources` (reserved).
