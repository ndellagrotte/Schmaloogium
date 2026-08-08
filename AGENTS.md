# AGENTS.md

This file provides durable repository guidance to coding-agent sessions (Oh My Pi / omp by
default; historical naming says "Codex" in a few places). Read it before changing files or
reviewing any governed document.

## What this repository is

**Schmaloogium provides OptiFine/Iris-format shader-pack support for Cleanroom clients on
Minecraft 1.12.2. Just shaders. Nothing else.** (`docs/research/v1/RESEARCH.md` §1.1 — the
mission statement, with a binding non-goals list at §1.2.)

The checkout is a [CleanroomModTemplate](README.md) on its `main` branch, and `src/` is still the
template's example mod (`com.example.modid.ExampleMod`). The mod is **designed, not built**: the
real work is a 14-phase architecture living in `docs/`, authored one phase document at a time by
one agent session each. So most work in this repository is *document* work under a strict
protocol — read §"The document system" before touching anything in `docs/`.

## Build and run

Java 25 toolchain (foojay auto-provisioning), Gradle wrapper 9.6.1, a custom
[Unimined fork](https://github.com/kappa-maintainer/Unimined) (`1.4.26-kappa`), MC 1.12.2 with
MCP `stable 39-1.12` mappings, Cleanroom loader pinned inline in `build.gradle`.

```bash
./gradlew build              # compiles, then remaps: jar (classifier `dev`) finalizedBy remapJar
./gradlew runClient          # from the cleanroom { runs } block
./gradlew runServer
./gradlew genSources         # decompiled vanilla sources; README says re-run until *-sources.jar appears
./gradlew test               # JUnit 5 (junit-jupiter 6.0.3), gated on enable_junit_testing
./gradlew test --tests 'com.example.SomeTest.someMethod'   # a single test
./gradlew publishToMavenLocal              # artifact debugging, gated on publish_to_local_maven
```

- **`src/test/` does not exist yet.** A green `./gradlew test` currently means "no tests ran",
  not "tests pass" — the JUnit wiring is a build flag, and the headless test baseline is Phase 1
  design work that has not been executed.
- **In IntelliJ, use the `2. Run Client` Gradle task** (registered via `idea-ext` in
  `build.gradle`), never the blue-icon `Minecraft Client` run configuration — README §"Running
  Client or Server".
- **Mod dependencies go in `gradle/scripts/dependencies.gradle`** and must use
  `modImplementation` / `modRuntimeOnly` / `modCompileOnly`. There is no `fg.deobf`/`rfg.deobf`
  here; adding a mod through a plain configuration crashes the game at load. `contain` (CRL
  jar-in-jar) and `shadow` are the non-mod bundling configurations.
- **`gradle.properties` is the build's control panel.** `mod_id` / `root_package` drive Blossom
  templating, publishing coordinates, and the AT filename; `enable_shadow`,
  `use_access_transformer`, `is_coremod`, `enable_lwjglx` and `show_testing_output` each change
  build shape. The comment block partway down the file marks the properties that need a Gradle
  refresh after editing.
- **Generated sources are templated, not written.** `src/main/java-templates/` and
  `src/main/resource-templates/` are Blossom (`{{ token }}`) inputs — `Reference.java`,
  `mcmod.info`, `pack.mcmeta`. Edit the template, not `build/generated/`.
- **CI** (`.github/workflows/`): `build.yml` builds every push and uploads `build/libs`;
  `release.yml` releases on a pushed tag; `release-to-cf-mr.yml` publishes to CurseForge/Modrinth
  (manual trigger, needs project IDs and tokens).

## The document system

Everything in `docs/` is governed. Read this before editing, and before citing.

### The authority chain

| Document | Role |
|---|---|
| `docs/research/v1/RESEARCH.md` | **Source of truth.** Wins every conflict. Confidence tags defined at its §0.2 (`[V:doc]`, `[V:observed]`, `[D-n]`, `[Q:OQ-n]`, `[U]`, `[A]`); decision log `D-1`…`D-10` at §1.3. |
| `docs/design/<rev>/DESIGN.md` | Arranges RESEARCH.md into an executable design. Part I (§G0–§G11) is what every session reads; Part II holds the 14 per-phase specs. Loses to RESEARCH.md — conflicts get *reported*, never silently resolved (§G0.1). |
| `docs/phase<N>/v<K>/PHASE_<N>_DOC.md` | One subsystem's architecture, on the mandatory §G9 thirteen-section template. Its §5 (cross-phase interfaces) is the binding contract dependents build against. |
| `docs/phase<N>/reviews/PHASE_<N>_REVIEW_<R>.md` | One adversarial review round: findings + exactly one verdict (PASS / PASS-WITH-CORRECTIONS / FAIL), with a `## Resolutions` section appended later by the fix-up session. |
| `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` (`PD`) | A mining report on the Pintonium codebase: **evidence, never contract** (§G0.1a). Rules of engagement at §G11 — both §G0.1a and §G11 exist only in the v2.0 revisions; v1.1 predates the reference entirely. |

Supporting files: `docs/MOVES.md` (the path manifest), `docs/decisions/`, and
`docs/phase<N>/briefs/` (the per-session briefs that commissioned a round).

### Six different files are named `DESIGN.md`

`docs/design/v1.1/`, `docs/design/v2.0-RC1/`, `docs/design/v2.0-RC2/`,
`docs/design/v2.0-RC3/`, `docs/design/v2.0-RC4/`, and `docs/design/v3/` each hold a `DESIGN.md`.
They are **different documents**, they do not share line numbers, and phase docs are anchored to
different ones. Every phase doc and review cites its revision *by line number*.

**Reading the wrong revision does not error — it yields plausible-looking wrong text at
coordinates that appear to resolve.** So:

- Which revision a phase is anchored to is declared data, not a guess: the phase doc's own §0
  states it, and `docs/MOVES.md` records the per-phase adoptions. Adopting or verifying against a
  candidate revision is a deliberate maintainer operation (§G0.4), never a side effect.
- Adding or moving a revision means **deriving a whole new pin set from that file's own headings**
  (`grep -n '^#'`, then print each range and confirm its first and last line). Never shift another
  revision's numbers by an offset.
- Adopting a candidate revision is a four-step maintainer procedure — see `§G0.4` in
  `docs/design/v2.0-RC2/DESIGN.md`. Historical `-RC` labels remain stable under partial adoption;
  `v3` was an explicit identity promotion and remains unadopted by default.
- If a coordinate you were given disagrees with what is at that line: **stop and report**, do not
  guess which is right.

### Paths, basenames, and version directories

Prose throughout the tree cites documents by **bare basename** (`RESEARCH.md`, `PHASE_1_DOC.md`,
`DESIGN.md`) — the versioned-directory reorg deliberately preserved basenames so ~1,200 prose
citations would survive. **Resolve any path through `docs/MOVES.md`**, which carries the complete
old→new manifest, the `DESIGN.md` collision warning, and the rule for what each version label
means. It also carries the dangling-reference sweep used as the acceptance check after any move.

A phase doc's `v<K>` = the highest `§0.K` fix-up addendum in it. **Rolling it is two steps run
together** — `git mv docs/phase<N>/v<K> v<K+1>`, then update that phase's rows in `docs/MOVES.md`
— and only ever **after** the fix-up that adds the addendum has actually landed. Rolling mid-review
points a later session at a directory that no longer exists, silently.

### Session types

Each phase passes through fresh, separate sessions, always in this order (`DESIGN.md` §G1):

1. **Build** (§G1.1) — one fresh agent session, one phase spec, writes
   `docs/phase<N>/v1/PHASE_<N>_DOC.md`,
   then stops.
2. **Verify** (§G1.2) — a *different fresh agent session*, without the author's context, adversarially
   attacks the doc and writes one numbered review with one verdict, then stops.
3. **Fix-up** (§G1.3) — another fresh agent writing role applies corrections, records each under
   `## Resolutions` in the review,
   adds a `§0.<K>` addendum to the doc.

A phase is **verified** only per §G1.3's definition, and §G5.3's gating invariant is that a
dependent build session may not read an unverified doc. If a fix-up changed the doc's §5, a fresh
verify session is owed before the phase closes.

### Running §G1.2/§G1.3

Verify and fix-up are **hand-run fresh agent sessions** — the former mechanized loop is retired
(2026-08-08). One session per role, following `DESIGN.md` §G1.2/§G1.3:

1. **Verify session** — one fresh session, blind to the author's context, reads the build
   session's mandatory inputs then attacks the doc (doc gate, conformance map, interface honesty,
   scope discipline, template completeness, binding decisions — §G1.2's priority order). It writes
   exactly one review file `docs/phase<N>/reviews/PHASE_<N>_REVIEW_<R>.md` (R = highest in that
   directory + 1) with findings + exactly one verdict, then stops without fixing anything.
2. **Fix-up session** — on PASS-WITH-CORRECTIONS, a fresh writing session applies the corrections,
   records each under `## Resolutions` in the review, and adds a `§0.<K>` addendum to the doc.
   On FAIL, rerun the build session with the review added to its Required inputs.

The evidence rules bind these sessions exactly as they bound the loop: repo-relative citations
quoted **at the line**, the forbidden-sources pattern (any `chatlogs/` below `docs/`, any root
`*.txt`), one verdict, single round per review directory, and literal-PASS means verdict PASS plus
zero blocking and zero corrections. Do not recreate the loop's internal stage decomposition (Attack
→ Refute → Steelman → Gate → Adjudicate) — that was engine machinery, not design text.

The provider-era wording in the immutable governing design revisions is interpreted through
`docs/tooling/CODEX_MIGRATION_OVERLAY.md`. It preserves their cited bytes and coordinates while
superseding the retired execution surface — including the retired verification loop.

## Rules binding every session here

Pointers, not restatements — the normative text is in the design revision the phase is anchored
to, and these glosses must not be treated as the rule:

- **Do not modify** `RESEARCH.md`, any `DESIGN.md` revision, `PINTONIUM_DESIGN.md`, or another
  phase's doc. Propose changes in your own doc's §11 "requested upstream changes" — §G1.1.
- **Forbidden sources, stated by pattern:** any directory named `chatlogs/` anywhere below
  `docs/` (any extension), and any `*.txt` at the repository root. These are prior sessions'
  terminal transcripts; they transmit the author's blind spots, which §G1.2's independence rule
  exists to prevent. Stated by pattern because `/export` mints new root-level transcripts under
  dated names, so any rule naming files is stale the next time someone exports. (Both are
  gitignored / untracked; the ignore rule is about repo size, not the source rule.)
- **No code in a build session** — no source files, stubs, or build changes; the deliverable is
  the architecture document. Illustrative signatures *inside* the doc are encouraged. §G1.1.
- **Cite repo-relative paths, and quote at the line.** A bare filename does not resolve, and a
  review drops findings whose citations do not.
- **Licensing (§G7, plus §G11.2 for Pintonium — v2.0 revisions only).** This project is GPL-3.0-or-later (`LICENSE` is
  the full GPLv3 text). Iris/Angelica/Pintonium are LGPL-3.0 and may be incorporated with
  compliance; **never copy from glsl-transformer / `glsl-transformation-lib` (AGPL)**; the
  OptiFine decompile is **behavioral-observation-only** — restate behavior, never code structure,
  class names, or identifiers.
- **Reference trees are read-only aliases.** `reference-src/` is gitignored and holds three
  checkouts that documents cite by shorthand (§G0.2): `cleanroom-src/` →
  `reference-src/cleanroom-0.6.6-alpha/`, `schlorbium-project/` →
  `reference-src/schlorbium-HD_U_G6_pre1/`, `Pintonium/` → `reference-src/pintonium-9c2fcc1/`.
  Read `§G11.3` (v2.0 revisions) before searching Pintonium — it lists real traps.
- **Use the `cleanroom` MCP server for vanilla symbols** (configured in
  `.codex/config.toml`; omp translates `[mcp_servers.*]` natively). Vanilla render classes exist in
  `cleanroom-src/` only as
  `.java.patch` files, so grepping it for a method body will mislead you; full sources are
  present only for Forge and loader internals.

## Where things stand

Current state is deliberately **not** recorded in this file — it rots, and a stale status line
here would be worse than none. To learn it:

1. Read the **highest-numbered** file in `docs/phase<N>/reviews/`: its verdict, whether it has a
   `## Resolutions` section, and its closing `§G1.3 status` — that is where a phase says plainly
   whether it is verified and what the next session owes.
2. Run `git status --short`. A version roll or a fix-up may be mid-flight, in which
   case the tree and the last commit disagree on purpose.
3. Resolve any path you find through `docs/MOVES.md`. For the next review number, look in
   `docs/phase<N>/reviews/` for the highest round and add one; the governing revision comes from
   the phase doc's own §0.

## Planned mod architecture

Designed in `PHASE_1_DOC.md` (§2 modules, §4.2 the Gradle split, §4.3 seam enforcement, §4.5
Mixin wiring, §4.7 the GL facade) — **none of it built yet**. The shape, from `DESIGN.md` §G3.1:

```
:engine       Pure JVM, Java 25. ZERO Minecraft/Forge/Cleanroom/Mixin/LWJGL dependencies.
              Headless-testable with JUnit alone. Packages: pack, preprocess, config,
              registry, buffers, uniforms, expr, gl.
:mod          The Cleanroom mod; depends on :engine. Packages: core, glue, mixin, gui, compat.
              ALL Minecraft-touching code lives here; mod.glue is the only place LWJGL is called.
:conformance  The Phase 2 harness. Depends on :engine. Never ships in the mod jar.
```

The **D-6 seam** is the load-bearing constraint, stated as a testable one: `:engine` compiles with
no classpath entry from Minecraft/Forge/Cleanroom/Mixin/LWJGL, and `:mod` never reaches into
`:engine` internals beyond its published interfaces. It exists because the render backend under
this mod may be replaced wholesale (Kirino-Engine, OQ-20) — it is not code hygiene.

Also decided but not yet applied: `mod_id = schmaloogium`, `root_package = com.schmaloogium`, with
`Reference` overridden to land at `com.schmaloogium.Reference`. Compare `gradle.properties`
against that before assuming which state the checkout is in. Hooks are Mixin-only (`D-5`,
~25–30 targeted injections); mixins stay *dumb* — they observe and delegate, never hold policy.
Pack-facing vocabulary (colortex, flip, gbuffers, backup chain, `mc_Entity`, …) is used **verbatim**
in identifiers; do not invent synonyms (§G4.1).
