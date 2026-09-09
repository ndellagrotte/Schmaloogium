# PINS — version pin ledger

Every pin this build rests on, with its verification state. Nothing floats: no dynamic
versions, ranges, or `mavenLocal()` snapshots in any module. The re-pin procedure is
PHASE_1_DOC §4.2.6 (trigger: every milestone, every release run, any suspected
platform-caused failure; never on a schedule).

## Current baseline — 2026-09-09 (implementation session, D-P1-51)

Inspected from the current files; runtime/resolution verification pending until the
Phase 1 build gate (`./gradlew build` + all four seam tests + `:mod:runClient` smoke)
completes. No bump is performed and no pin is declared last-known-good by test here.

| Component | Pinned value | Where it lives | Status |
|---|---|---|---|
| Gradle wrapper | 9.7.0 | `gradle/wrapper/gradle-wrapper.properties` | inspected; preserved per §4.2.6a |
| Unimined (kappa fork) | 1.4.36-kappa | root `build.gradle` (apply false), applied in `:mod` | inspected; preserved |
| Cleanroom loader | 0.6.10-alpha | `gradle.properties` → `cleanroom_loader_version`, read by `:mod` only | inspected; preserved (moved from inline root literal) |
| Java toolchain | 25 | root `subprojects {}` (foojay resolver 1.0.0) | inspected; provisioned JDK observed locally |
| Mappings | MCP `stable`, `39-1.12` | `:mod` Unimined `mappings { }` | inspected; preserved |
| Blossom / Shadow / idea-ext | 2.2.0 / 9.5.1 / 1.4.1 | root plugins block | inspected; preserved |
| JUnit Jupiter | 6.0.3 | root `subprojects {}` | inspected; preserved |
| ASM (test-only, 3 modules) | 9.10.1 | `testImplementation` in `:engine`, `:mod`, `:conformance` | inspected; preserved |
| Mixin compile-time | (no explicit row — Unimined/loader resolution) | `:mod` via `dependencies.gradle` | inspected; per §4.2.6a, no July `compileOnly` row resurrected |
| lwjglx | dropped (`enable_lwjglx=false`) | — | per PHASE_1_DOC §4.6 |
| ModularUI | not pinned | — | Phase 12 owns (OQ-9) |

## Dependency pins — verified this session (2026-09-09)

### jcpp 1.4.14 (D-P1-49)

- **Coordinate**: `org.anarres:jcpp:1.4.14`, Maven Central. POM inspected at
  `https://repo1.maven.org/maven2/org/anarres/jcpp/1.4.14/jcpp-1.4.14.pom`;
  `<release>` of `maven-metadata.xml` is 1.4.14 (last metadata update 2019-08-19).
- **License**: Apache-2.0, per the POM's `<licenses>` block. Recorded in
  `THIRD-PARTY.md`.
- **Runtime closure, verified by bytecode inspection of the jar**: the library path
  (`org.anarres.cpp` minus `Main` and `CppTask`) references only
  `org.slf4j.Logger`/`LoggerFactory` among externals. The POM's compile-scope
  `org.apache.tools.ant` references live exclusively in `CppTask` (an Ant task adapter,
  never loaded by this project); `net.sf.jopt-simple` and `com.github.zafarkhaja`
  (java-semver) are CLI-only; `com.google.code.findbugs` is annotation-only (absent
  annotations are ignored by the JVM).
- **Declarations**: `:engine` `implementation` with the four unreferenced groups
  excluded; `:mod` `contain`s `jcpp` (same exclusions) plus
  `org.slf4j:slf4j-api:1.7.12` (MIT, zero transitive deps) so headless runtime and
  client runtime see the verified closure.
- **Known duplication risk, recorded**: Minecraft 1.12.2 ships slf4j-api 1.7.25 via
  launchwrapper; the contained 1.7.12 is binary-compatible for jcpp's
  `LoggerFactory.getLogger`/`debug`/`info` usage. Client-runtime classpath order to be
  confirmed at the Phase 7 `runClient` smoke.

## July history — 2026-07-24 re-verification (preserved from PHASE_1_DOC §4.2.6)

Historical evidence only; never re-date or treat as a successful bump. Notable rows:
Cleanroom loader 0.6.6-alpha (superseded by the 0.6.10-alpha file inspection above);
Unimined 1.4.26-kappa (superseded likewise; trap: `maven.wagyourtail.xyz` carries only
upstream 1.4.1, no kappa builds — do not "upgrade"); Gradle 9.6.1 (superseded by the
wrapper's 9.7.0); ASM 9.10.1 second-sourced via Central search API + Google mirror.

## Change log

| Date | Component | Old | New | Delta | Verification | Session |
|---|---|---|---|---|---|---|
| 2026-09-09 | jcpp | — | 1.4.14 | initial admission (D-P1-49) | POM + jar closure inspected; engine/mod build gate pending | Phase 1 implementation |
| 2026-09-09 | slf4j-api | — | 1.7.12 | jcpp runtime closure | POM transitively inspected; MIT | Phase 1 implementation |
