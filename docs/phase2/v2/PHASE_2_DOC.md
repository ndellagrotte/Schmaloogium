# Schmaloogium — Phase 2: Conformance harness — Architecture

> **Phase:** 2 — Conformance harness · **Milestone:** v0.1 (design; implementation starts week one)
> **Depends on:** Phase 1 · **OQs assigned:** OQ-10
> **Date:** 2026-07-25 · **v2 rebuild/adoption:** 2026-08-03 (§0.36)
> **Session type:** Maintainer-authorized §G1.1 rebuild after Review 36's FAIL, adopting
> `docs/design/v3/DESIGN.md` for Phase 2. No source code was written and no paid verification round
> was launched; Round 37 is the next independent §G1.2 review.

---

## 0. Header

### 0.1 Inputs actually read

| Input | Extent | Why |
|---|---|---|
| `docs/design/v3/DESIGN.md` Part I (§G0–§G12) | whole | governing global rules selected by this §0 declaration |
| `docs/design/v3/DESIGN.md` Part II, **Phase 2 spec** (§"Phase 2 — Conformance harness") | whole | the adopted assignment and doc gate |
| `docs/research/v1/RESEARCH.md` §0, §1, §8, §9, App G, §5.1 RenderBook bullet, §12.5 RenderBook row | assigned extents | source of truth and v3 Phase 2 required inputs |
| `docs/research/v1/RESEARCH.md` §3.1/§3.2/§3.5, §4.1/§4.3–§4.5, App A.1–A.3, App B.1, App H, §10.3, §11 OQ-8/OQ-10/OQ-11 | inherited claim extents re-audited where affected | scene, tier, golden, licensing, vocabulary, and spike provenance retained from v1 |
| `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` (`PD`) §§4, 7, 19 | whole sections | required reference gaps, seven-pack parse calibration, and temporal blurring evidence; evidence only under §G11 |
| `Pintonium/README.md`; `Pintonium/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/{MixinEntityRenderer_Shaders,MixinRenderGlobal_Shaders}.java` | targeted verification of revision, bug statement, and hook inventory only | §G11.4 provenance check for the load-bearing PD §4/§19 claims; no mechanism or source structure adopted |
| `docs/phase1/v14/PHASE_1_DOC.md` | §5 whole, with cited §2/§4/§8 detail followed as needed | current verified dependency contract: modules, facade, profiles, recording, diagnostics, CI, and capture package grant |
| `docs/phase2/v1/PHASE_2_DOC.md` | whole inherited architecture | immutable historical source for this v2 rebuild |
| `docs/phase2/reviews/PHASE_2_REVIEW_36.md` | whole | FAIL authorization and all four rebuild dispositions; remains unresolved historical review evidence |
| `.github/workflows/{build,release,release-to-cf-mr}.yml` | whole | current CI ground truth |
| Historical web/MCP observations recorded in v1 §0.1 | retained as dated provenance, not refreshed claims | this rebuild does not change the RenderBook API, Modrinth response shape, or vanilla screenshot symbol contracts |

### 0.2 Dependency PHASE docs consumed

`docs/phase1/v14/PHASE_1_DOC.md` — the only declared dependency (§G5.1), verified and current. Every
item consumed from it is cited to a section and listed in §5.2.

### 0.3 Deviations from the assigned reading list, with reasons

1. **PD §7 was added to the Phase 2 spec's literal PD §4/§19 reading list.** V3 makes Pintonium's
   seven-pack front-end outcomes a calibration target, and PD §7 is the section that records those
   outcomes and their gaps. It is evidence, never a substitute for D-3 or RESEARCH.md.
2. **The v1 document's additional RESEARCH.md contract/behavior sections remain in the provenance
   set.** They justify the six scene families and golden fields. The migration re-audited the rows
   whose semantics change, rather than discarding mature traceability merely because v3's minimum
   reading list is shorter.
3. **No Oculus mining sections were added.** V3 §G12.6 is a routing map, not an amendment to Phase
   2's Required inputs; this rebuild needs no Oculus claim to satisfy motion or reference calibration.
4. **No web or MCP refresh was needed.** The affected surface is governance, motion scheduling, wire
   schemas, and source-text-free calibration records. V1's dated RenderBook, Modrinth, and vanilla
   symbol observations remain explicit historical provenance and are not represented as newly checked.

### 0.4 Round-2 fix-up

Corrected the live Phase 1 dependency status and replaced the reverse `:engine` →
`:conformance` golden SPI with an engine-owned snapshot API consumed by a conformance-side adapter.
The §5 interface changed and requires a fresh verify round.

### 0.5 Round-3 fix-up

Specified the two process-boundary wire schemas, gated complete real goldens on both Phase 3 and
Phase 4, and expanded §3 traceability for the Phase 2 subrequirements. The §5 interface changed and
requires a fresh verify round.

### 0.6 Round-4 fix-up

Made every T0 predicate and both baseline identities reconstructible from the canonical run
manifest, including runner-synthesized failure manifests. The §5 interface changed and requires a
fresh verify round.

### 0.7 Round-5 fix-up

Closed the world-tree identity domain, completed the run-manifest pack scalars, and exposed the
dual-spec scheduling conflict. The §5 interface changed and requires a fresh verify round.

### 0.8 Round-6 fix-up

Made the unattributable GL-error count derivable from the wire records and separated the dual-spec
release cadence from its single implementation milestone. The §5 interface changed.

### 0.9 Round-7 fix-up

Made Complementary's primary-target status family-wide and placed the v0.1 dual-spec runs with the
other v0.1 render-dependent work.

### 0.10 Round-9 fix-up

Completed the run-manifest resource wire grammar and made tier-ledger evidence scene-set-complete.
The §5 interface changed and requires a fresh verify round.

### 0.11 Round-10 fix-up

Corrected resource-record ordering, made T3 A/B and manual evidence reachable from the tier ledger,
and assigned live resource snapshots to Phase 5. The §5 interface changed.

### 0.12 Round-11 fix-up

Made manual attestations content-addressable and made every recorded GL error fail T0. The §5
interface changed and requires a fresh verify round.

### 0.13 Round-12 fix-up

Anchored ledger artifacts beneath the trusted run-output root and made the real-pack GL smoke part
of OQ-10's result.

### 0.14 Round-13 fix-up

Fixed canonical, contained locations for every ledger evidence file and specified one shared
cache/run-root establishment procedure. The §5 interface changed.

### 0.15 Round-14 fix-up

Moved the Git-worktree refusal ahead of cache creation and made GL-error attribution come only
from a requested replay-aware Phase 1 result. The §5 interface changed.

### 0.16 Downstream-request addendum (Phase 8 hook evidence — 2026-08-03)

Review round 15 ended in literal **PASS** with zero blocking findings and zero corrections
(`docs/phase2/reviews/PHASE_2_REVIEW_15.md`). This maintenance amendment accepts Phase 8 R8-5's
Phase-2 half: `schmaloogium.run-manifest/1` now carries the complete immutable Phase 7 application
report, including nested Phase 8 health rows, by direct field-for-field projection. The schema
forbids deriving health or capability from rendered behavior. Because §§4.5.4 and 5 change, Phase 2
v1 is unverified pending fresh review round 16; the version directory is unchanged while that loop
is open.

**Historical status:** review round 16 subsequently returned literal **PASS** with zero findings
(`docs/phase2/reviews/PHASE_2_REVIEW_16.md`). The amendment below supersedes that verified surface.

### 0.17 Downstream-request addendum (Phase 7 runner-owned pack provenance — 2026-08-03)

Phase 7 R7-7 identifies a process-boundary hole: the agent could previously fill the manifest's
pack provenance without a specified authoritative transport. This amendment makes the runner's
resolved `PackFixture` facts immutable capture-plan inputs: acquisition mode, verified archive
SHA-512, and licence reach the agent through the plan and return verbatim in its temporary manifest.
The runner validates all three before publication and never trusts a pack, scene, or agent-side
rediscovery to self-report them. `[D-P2-23]` records the decision.

This amendment changes §§4.5.1–4.5.4 and binding §5. Phase 2 v1 is therefore **not verified** after
round 16's historical PASS; the directory remains `v1` pending a fresh whole-document review.

### 0.18 Round-17 fix-up

Removed obsolete owner-review gates from the accepted Phase 1 v14 package and replay-aware
GL-error grants. Binding §5 changed and requires a fresh whole-document review.

### 0.19 Round-18 fix-up

Corrected the closing verification history and replaced an ungranted Phase 1 diagnostic-type
consumption with an explicit upstream request. Binding §5 changed.

### 0.20 Round-19 fix-up

Removed the remaining ungranted diagnostic-domain-type claim from the run-manifest definition.

### 0.21 Round-20 fix-up

Completed the Phase-2-owned diagnostic wire grammar and corrected the closing review history.

### 0.22 Round-21 fix-up

Corrected the closing verification history after the round-20 fix-up.

### 0.23 Round-22 fix-up

Corrected the closing verification history after the round-21 fix-up.

### 0.24 Round-23 fix-up

Corrected the closing verification history after the round-22 fix-up.

### 0.25 Round-24 fix-up

Corrected the closing verification history after the round-23 fix-up.

### 0.26 Round-25 fix-up

Corrected the v0.3 terrain-scene gate and advanced the closing verification history.

### 0.27 Round-26 fix-up

Corrected the closing verification history after the round-25 fix-up.

### 0.28 Round-27 fix-up

Corrected the closing verification history after the round-26 fix-up.

### 0.29 Round-28 fix-up

Corrected the closing verification history after the round-27 fix-up.

### 0.30 Round-29 fix-up

Advanced the closing verification history for the round-29 review surface.

### 0.31 Round-30 fix-up

Advanced the closing verification history after the round-29 fix-up.

### 0.32 Round-31 fix-up

Corrected the closing verification history as required by round 31.

### 0.33 Round-32 fix-up

Corrected the §0.32 provenance and advanced the closing verification history.

### 0.34 Round-33 fix-up

Advanced the closing verification history after the round-32 fix-up.

### 0.35 Round-34 fix-up

Restricted client capture to registry-backed shader-pack selections so its provenance schemas remain total.

### 0.36 v3 adoption and Review-36 rebuild

The maintainer authorized a fresh v2 artifact after Review 36's **FAIL**, rather than appending
resolutions to that immutable review or mutating the historical v1 document. This is a deliberate
exception to the normal post-PASS version-roll convention: `docs/phase2/v1/PHASE_2_DOC.md` remains
the exact artifact Review 36 assessed, while this file adopts `docs/design/v3/DESIGN.md` for Phase 2.

The rebuild applies all four Review-36 dispositions as one coherent contract change: real
frame-indexed camera motion replaces the static-path narrowing; capture-plan and run-manifest schema
majors advance to `/2` with no `/1` compatibility reader; the golden update workflow and all
reporting become explicit verification interface regions; and v3's Pintonium reference-gap and
seven-pack parse-calibration requirements become mapped runs and evidence. It also audits v1.1-era
governance, resolved upstream requests, §G2.4 rung 2a, §G6 derived-artifact rules, and §G11
provenance. Because §5 changes, this v2 artifact is unverified pending fresh whole-document Review
37. Review 36 remains unchanged and intentionally has no `## Resolutions` section.

### 0.37 Round-37 fix-up

Corrected the tier contract so per-call drain cadence narrows diagnostic windows but only replay-confirmed isolated recurrence establishes GL-error attribution.

---

## 1. Scope & boundaries

### 1.1 What Phase 2 owns

The machinery that decides whether every other phase is done (`D-3`, `D-10`), and nothing that
renders. Concretely:

- **Tier machinery** — T0–T3 made decidable, per-pack tier state, evidence rules, reporting (§4.2).
- **Fixed scenes and deterministic camera paths** — the scene-definition format, its parser and
  validator, dense frame-indexed motion samples, the determinism ledger, and the initial scene set
  (§4.3, §4.4).
- **Capture automation** — the design of the two-sided capture path and the run manifest it produces
  (§4.5); designable now, runnable once v0.1 renders.
- **Image diff** — tolerance model, baselines, approval workflow, diff artifacts (§4.6, §4.7).
- **The T2 protocol** — the manual OptiFine G6 oracle-capture procedure (§4.8).
- **Named harness runs** — the run catalogue every milestone exit criterion and every phase impl gate
  cites (§4.9, mapped in §3.5).
- **The fixture system** — pack registry, acquisition, integrity, cache, and the structural encoding
  of §G6's never-rehost rule (§4.10).
- **The headless-core golden harness** — golden document format, comparer, update workflow (§4.11).
- **Pintonium parse calibration** — source-text-free observed outcomes for the exact seven matrix
  archives, used to calibrate diagnostics but never to weaken D-3 (§4.11.7).
- **The `GLCapabilityProfile` fixture set** and its refresh workflow (§4.12) — the *set*, not the
  format, which is Phase 1's.
- **Reporting** (§4.13) and **CI wiring** (§4.14), filling the slots Phase 1 left.
- **OQ-10's spike specification** and the RenderBook evaluation plan (§10).
- **The "runnable before any renderer exists" subset** (§9.2) — what week one builds.

### 1.2 Adjacent concerns, and who owns them

| Concern | Owner |
|---|---|
| The pack front-end this harness feeds sources through — discovery, preprocessing, options, `shaders.properties`, `PackConfiguration` | **Phase 3** |
| Per-slot compile / backup-chain resolution status (T3's "no silent fallback" rule reads it) | **Phase 4** |
| Everything about buffers the golden harness records *decisions* about | **Phase 5** |
| Uniform inventory and cadences | **Phase 6** |
| The renderer, the frame loop, and the frame-end hook the capture agent needs | **Phase 7** |
| The shadow pass the night/shadow scene exercises | **Phase 8** |
| Pack-option state the scene format pins | **Phase 12** |
| Performance benchmarking of any kind — frame times are *recorded* by the manifest, never *gated* on | **Phase 14** (`DESIGN.md` Phase 2 *Scope — out*) |
| Scene **content** tuning per pack (which coordinates make a given pack look interesting) | implementation-time, not this document (*Scope — out*) |
| `GLCapabilityProfile`'s type and text format; `RecordingGLDevice` / `GLCallLog` / `ScriptedResponses` / `ReplayAssertions`; the `:conformance` module's existence, JUnit wiring and dependency edge; the CI `conformance` job stub; the `schmaloogium.conformance` log channel; the `-Dschmaloogium.debug.*` namespace | **Phase 1** (`PHASE_1_DOC.md` §4.7.2, §4.7.5, §4.2.4a, §4.11, §4.9.2, §4.9.3) |
| Where capability-profile fixtures live on disk (`:engine`'s `testFixtures`) | **Phase 1** §8.3; the *set* is ours |
| Resolving OQ-10 | nobody yet — this document specifies the spike (§10), per §G4.4 |

### 1.3 What "conformance harness" does not mean here

Three narrowings, stated because each is a plausible misreading:

1. **The harness never renders and never decides how to render.** It launches a client, consumes what
   the client wrote, and compares. Every rendering decision belongs to Phases 3–14.
2. **The harness is not a test framework.** JUnit 6 is already wired (`PHASE_1_DOC.md` §4.2.4a); this
   phase contributes domain machinery that runs *inside* JUnit, plus one Gradle task split (§4.14).
3. **A tier is a claim about a pack, not about a build.** Tier state is per-`(pack, version, tier,
   scene set)` and lives in a ledger with evidence (§4.2.4), because "BSL is at T1" is a sentence the
   project will say in release notes and must be able to defend.

---

## 2. Architecture overview

### 2.1 The shape of the problem: three execution contexts

Every conformance question this project has falls into exactly one of three contexts, and the split
is the reason the harness is not one program:

| Context | Has | Runs | Home |
|---|---|---|---|
| **A — headless JVM** | JUnit, `:engine`, no GL, no Minecraft | pack sources through the front-end; golden comparison; fixture download; image diff; report rendering | `:conformance` |
| **B — live client** | GL, Minecraft, Cleanroom, the whole mod | world load, scene application, frame capture, run-manifest emission | `:mod` |
| **C — human** | judgement | T1 baseline approval; the T2 OptiFine oracle capture; manual fixture placement | documented procedures (§4.7.3, §4.8, §4.10.4) |

Context A is everything that can run **before any renderer exists**, which is what `D-10`'s "week
one" means (§G6's second testability slice). Context B is designable now and runnable at v0.1.
Context C is deliberately never automated: `OF` is not redistributable (§8.2, §10.3) and a first
baseline that nobody looked at is not an oracle.

### 2.2 Module placement, and the C-4 wall

`PHASE_1_DOC.md` §4.3 states constraint **C-4**: *"`:conformance` depends on `:engine` and never on
`:mod`"*, asserted mechanically by `SeamConformanceDependencyTest` in classpath-plus-bytecode form,
and `conformance/build.gradle` (§4.2.4a) declares exactly `mavenCentral()`, `project(':engine')`,
`testFixtures(project(':engine'))` and test-scope ASM. **There is no Minecraft, Forge, LWJGL or
Unimined anywhere on `:conformance`'s classpath, in any configuration.**

This is the single most consequential constraint on this phase, and it produces a finding the spec's
bullet list does not anticipate:

> `[D-P2-1]` **The capture driver cannot live in `:conformance`.** World load, camera placement and
> screenshot capture are Minecraft-client calls; a class making them cannot compile in a module with
> no Minecraft on its compile classpath. The harness therefore crosses into the client through a
> **process boundary, not a classpath edge**: `:conformance` writes a *capture plan*, launches the
> client as a separate JVM, and reads back PNGs and a *run manifest*. The client-side half is a small
> agent in `:mod`.

The alternative — putting Minecraft on `:conformance` — was considered and rejected: it would require
applying Unimined there, which contradicts §4.2.4a as written, and it would make C-4 unassertable in
the form Phase 1 specified. A process boundary costs one file format (§4.5.2) and buys an
architecture test that stays green.

Two consequences follow, and both are handled as *requests*, never as assumptions (§5.4):

- `:mod`'s package table (`PHASE_1_DOC.md` §2.1) contains `mod.core`, `mod.glue`, `mod.mixin`,
  `mod.gui`, `mod.compat` and nothing else, and §5.1 makes package placement a rule. The capture
  agent therefore needs a **requested** package, `com.schmaloogium.mod.conformance`.
- Constraint **C-3** forbids `org.lwjgl` references outside `com.schmaloogium.mod.glue`. §4.5.3
  designs the frame grab so the agent needs none.

### 2.3 Package layout

Inside `:conformance`, at `com.schmaloogium.conformance.*` (the root `PHASE_1_DOC.md` §2.1 assigns):

| Package | Contents |
|---|---|
| `conformance.tier` | `Tier`, `TierOutcome`, `TierEvidence`, `PackTierState`, `TierLedger`, the tier evaluators |
| `conformance.scene` | `SceneSpec` + its value types, `SceneParser`, `SceneValidator`, `CapturePlan`, `CapturePlanWriter` |
| `conformance.capture` | `CaptureRunner` (external-process driver), `RunManifest`, `RunManifestReader`, `CaptureArtifacts` |
| `conformance.diff` | `ImageDiffer`, `TolerancePolicy`, `DiffResult`, `ClusterAnalysis`, `IgnoreMask`, `DiffReportWriter` |
| `conformance.fixture` | `PackFixtureRegistry`, `PackFixture`, `AcquisitionMode`, `FixtureResolver`, `FixtureCache`, `HttpTransport`, `IntegrityCheck` |
| `conformance.golden` | `GoldenDocument`, `GoldenSection`, `GoldenWriter`, `GoldenComparer`, `GoldenUpdatePolicy`, `GoldenProjectionAdapter` |
| `conformance.report` | `ConformanceReport`, `ReportRenderer` (markdown / JSON / JUnit-XML) |
| `conformance.run` | the named-run catalogue (§4.9) as code: `HarnessRun`, `RunId`, `RunRegistry` |

Harness code lives in `:conformance`'s **`main`** source set (it is a library the tests drive, and
`§4.2.4a` gives `main` the `:engine` dependency); the tests that *are* runs live in `test`.

In `:mod`, at the requested `com.schmaloogium.mod.conformance`:

| Type | Role |
|---|---|
| `CaptureAgent` | lifecycle: reads the plan, drives the run, writes artifacts, disarms on any failure |
| `CapturePlanReader` | a deliberately dumb reader for the flat plan format (§4.5.2) |
| `SceneApplier` | applies world/client state: time, weather, gamerules, position, held items, video settings |
| `FrameGrabber` | one `BufferedImage` per captured sample (§4.5.3) |
| `RunManifestWriter` | emits the manifest (§4.5.4) |

### 2.4 Key types introduced by this phase

| Type | Package | Role |
|---|---|---|
| `SceneSpec` | `conformance.scene` | the authored, human-edited scene: world + client + static shots + dense camera paths |
| `CapturePlan` | `conformance.scene` | the derived, default-free flat plan with every capture sample resolved before the process boundary |
| `RunManifest` | `conformance.capture` | everything the client observed during a run — the evidence artifact T0 and T3 are decided from |
| `TolerancePolicy` | `conformance.diff` | a named tolerance profile; the reason a diff verdict is reproducible |
| `DiffResult` | `conformance.diff` | per-pixel + aggregate + cluster findings, with the artifacts written |
| `PackFixture` | `conformance.fixture` | one App G row, resolved: acquisition mode, pin, hash, licence line |
| `GoldenDocument` | `conformance.golden` | a sorted, deterministic, source-text-free record of front-end decisions |
| `TierLedger` | `conformance.tier` | per-`(pack, version)` tier state with evidence pointers |
| `HarnessRun` | `conformance.run` | a named run: inputs, procedure, pass condition, artifacts, where it runs |

### 2.5 Artifact flow

```
 registry (committed)                scene/*.scene (committed)
        │                                     │
        ▼                                     ▼
 FixtureResolver ──► cache/packs/…      SceneParser ──► CapturePlan (temp)
        │                                     │
        ├──────────── context A ──────────────┤
        │                                     │
        ▼                                     ▼
 GoldenComparer ◄── GoldenProjectionAdapter ◄── PackDecisionSnapshot (:engine)
        │                                     CaptureRunner ═══► [ separate JVM: client ]
        │                                                        CaptureAgent
        ▼                                                        │
 golden/*.golden (committed, no pack source)      cache/runs/<id>/<capture>/<sample>.png + run.manifest
                                                                 │
                                     context A ◄─────────────────┘
                                                 │
                        ImageDiffer ◄── baselines (cache) + baseline manifest (committed)
                                                 │
                                                 ▼
                                      TierLedger ──► ConformanceReport
```

The `═══►` is the only edge that leaves the JVM, and **every arrow crossing between contexts is a
file, never a call** — the capture plan going out, the images and manifest coming back. That is what
makes C-4 hold, and what makes a run reproducible from its artifacts alone months later.

---

## 3. Contract conformance map

Phase 2 owns no pack-facing contract surface — RESEARCH.md §3 and Apps A–F belong to Phases 3–13
(§G4.2). Its in-scope rows are of four other kinds, and this section maps all four with **zero
unmapped rows**. The scene set (§3.4) is mapped *to* contract rows because the spec puts "one scene
per behavioral family the matrix packs exercise" in scope, and a scene set that cites nothing is a
list of guesses.

### 3.1 App G rows → fixture registry entries

Every row of RESEARCH.md App G, its licence constraint, and the acquisition mode §4.10 gives it.

| App G pack | Version (App G) | Tier role | Licence constraint (App G / §10.3) | Acquisition mode (§4.10.2) |
|---|---|---|---|---|
| BSL Shaders | v10.1.3 | dual-spec | ARR → no bundling; App G names Modrinth-API download at test time | `MODRINTH` |
| Complementary Reimagined | r5.8.1 | dual-spec, **primary-target family** (§8.1, Angelica precedent) | Complementary License 1.6: no redistribution outside Modrinth/CF | `MODRINTH` |
| Complementary Unbound | r5.8.1 | dual-spec, **primary-target family** (§8.1, Angelica precedent) | as Reimagined | `MODRINTH` |
| Sildur's Vibrant | v2.01 Extreme | dual-spec, strongest legacy candidate | ARR → no bundling | `MODRINTH` **pending verification** — see §11.3 item 1 |
| Chocapic13 | V9 (final) | classic (T2 oracle tier) | ARR + edit-licence; no unmodified redistribution | `MANUAL` (CurseForge canonical) |
| SEUS Renewed | 1.0.1 | classic | "© Sonic Ether. All rights reserved." | `MANUAL` (sonicether.com canonical) |
| projectLUMA | v1.32 | classic | ARR, attribution required | `MANUAL` (CurseForge canonical) |

The App G column *"License / CI stance"* is not decoration in this design: it is a **field on
`PackFixture`**, it is copied into every `RunManifest`, and §4.10.5 makes "no matrix pack is ever
committed or re-hosted" a structural property rather than a rule someone remembers.

### 3.2 RESEARCH.md §8.2 tier rows → tier machinery

| §8.2 row | Applies to | §8.2 gate, verbatim | Design element |
|---|---|---|---|
| **T0 — loads** | all matrix packs | "pack parses, programs compile, no GL errors, stable frame loop" | §4.2.1 — four machine-decidable predicates over the `RunManifest` |
| **T1 — renders plausibly** | all matrix packs | "hand-approved baseline screenshots (fixed seed/scene/time/camera set), re-diffed against *themselves* thereafter (regression oracle)" | §4.2.2 + §4.7 — the approval workflow and the self-diff |
| **T2 — pixel-parity vs OF** | **classic packs only** | "screenshot diff vs OF G6 on the fixed scene set within tolerance" | §4.2.3 + §4.8 — the manual oracle protocol and the `CROSS_ENGINE` tolerance profile |
| **T3 — feature-complete** | per pack | "every feature the pack exercises (per its own option screens) behaves; no fallback program silently masking a failure" | §4.2.4 — the per-pack feature checklist **and** the manifest's per-slot fallback record, which turns the second clause into an assertion |

### 3.3 RESEARCH.md §8.3 / `DESIGN.md` §G6 harness requirements → design elements

| Requirement | Source | Design element |
|---|---|---|
| Fixed test scenes: seed + coordinates + time + weather + camera paths | §8.3 | §4.3 (`SceneSpec`, `[shot]` and frame-indexed `[path]` blocks) |
| Automated screenshot capture | §8.3 | §4.5 |
| Image diff with tolerance | §8.3 | §4.6 |
| Headless smoke tests for the portable core: pack sources through the preprocessor against a recorded GL-capability profile | §8.3 | §4.11 + §4.12 |
| Validate resource-sizing decisions without a live context | §8.3 | §4.11.3 (`SIZING` golden section) |
| RenderBook JUnit OpenGL Extension confirmed to exist; CI viability unproven | §8.3, §5.1 | §10 (evaluation plan + spike) |
| JUnit wiring already in the template | §8.3, §G6 | consumed from `PHASE_1_DOC.md` §4.2.4a |
| No matrix pack committed or re-hosted; download at test time with a local cache | §8.3, §G6, resolved OQ-11 | §4.10.3–§4.10.5 |
| Modrinth API version IDs where available; SEUS/Chocapic/projectLUMA canonical-download-only | §8.3, §G6 | §4.10.2 |
| T1 baselines hand-approved once, then the regression oracle | §G6 | §4.7.3 |
| T2's oracle is OptiFine G6 captured manually outside CI | §G6 | §4.8 |
| Tier gates as implementation exit criteria for behavioral phases | §G6 slice 3 | §4.9 + §3.5 |
| Per-phase headless tests against the facade / recorded profiles | §G6 slice 1 | not ours — each phase's §8; this phase supplies the profile *set* (§4.12) |
| Per-pack tier state and reporting | Phase 2 spec | §4.2.5 (`TierLedger`) + §4.13; verified by `TierLedgerTest` + `ReportRendererTest` |
| Deterministic setup and suppression of randomness | Phase 2 spec | §4.3 + §4.4; verified by `SceneParserRejectionTest` + `RUN-SCENE-SELFCHECK` |
| Per-pixel and aggregate thresholds | Phase 2 spec | §4.6.2–§4.6.3; verified by `ImageDifferTest` + `ClusterAnalysisTest` |
| GPU/driver variance handling | Phase 2 spec | §4.6.3–§4.6.5; verified by `TolerancePolicyTest` and the calibration procedure |
| Baseline storage and versioning | Phase 2 spec | §4.7.1–§4.7.4; verified by baseline-manifest validation in `RUN-T1-REGRESS` |
| Diff artifacts | Phase 2 spec | §4.6.4 + §4.13; verified by `ImageDifferTest` + `ReportRendererTest` |
| Fixture cache layout and CI cache keys | Phase 2 spec | §4.10.3 + §4.14; verified by `FixtureCacheRootTest` and the workflow-dispatch run |
| Golden format and explicit update workflow | Phase 2 spec | §4.11.1–§4.11.5; verified by `GoldenWriterDeterminismTest`, `GoldenCorpusTest`, and the fail-after-update rule |
| Camera-path motion in every motion-sensitive family | `docs/design/v3/DESIGN.md` Phase 2 spec and doc gate; PD §19.1 | §§3.4, 4.3.4, 4.4, 4.5 and `RUN-MOTION-PATHS`; all six rendered families are conservatively motion-sensitive |
| Shadow, sky, and weather coverage despite no working Pintonium reference | `docs/design/v3/DESIGN.md` §G6 and Phase 2 spec; PD §4 | §3.4's `terrain-day`, `night-shadows`, and `weather-rain` rows are intentionally reference-free coverage; Pintonium does not define their pass conditions |
| Seven-pack front-end outcomes calibrated against observed Pintonium results | `docs/design/v3/DESIGN.md` Phase 2 spec; PD §7 | §4.11.7 and `RUN-PINTONIUM-PARSE-CALIBRATION`; missing observations fail calibration, while Schmaloogium must still parse all seven under D-3 |
| Derived artifacts contain no pack source and no committed rendered images | `docs/design/v3/DESIGN.md` §G6 | `[D-P2-5]`, `[D-P2-6]`, §§4.7, 4.10.4, 4.11.1, and 4.11.7 |

### 3.4 The initial scene set → behavioral families → the contract rows they exercise

One scene per behavioral family, each justified. Scene ids are stable and appear in every artifact
path and every run name.

| Scene id | Family | Required static content and moving path | Contract / behavior rows it exercises |
|---|---|---|---|
| `terrain-day` | terrain + sky, the baseline frame | open landscape, mixed solid / cutout / cutout-mipped blocks, clear sky, midday; `path terrain-pan` translates and yaws across the skyline | App A.1 `gbuffers_terrain` family and backup chain; `gbuffers_skybasic` / `gbuffers_skytextured` / `gbuffers_clouds`; §4.4's gbuffers order; §3.2's `mc_Entity` stamping. Sky coverage is intentionally reference-free because Pintonium leaves it fixed-function (PD §4) |
| `water-translucent` | translucency and the depth split | shoreline with water and glass plus static above/under-water shots; `path shoreline-track` translates parallel to the waterline without crossing the eye-in-water boundary | App A.1 `gbuffers_water`; §4.3's `depthtex1` copy before translucent terrain; `isEyeInWater` (App D); deferred ordering |
| `night-shadows` | the shadow pass | long cast shadows, moon, block light; `path shadow-parallax` translates laterally across foreground and shadow edges | §4.5 in whole; App A.1 `shadow`; App B.2. This is intentionally reference-free coverage because Pintonium's working packs do not validate a 1.12.2 shadow pass (PD §4) |
| `weather-rain` | weather and the second depth copy | rain, wet surfaces, settled wetness; `path rain-track` translates and yaws across rain geometry and reflections | App A.1 `gbuffers_weather`; §4.3's `depthtex2` copy before weather; `wetness`/`wetnessHalflife` (§3.2, App A.3). This is intentionally reference-free because Pintonium has no weather hook (PD §4) |
| `hand-item` | first-person geometry | main-hand and translucent-item static shots, HUD off; `path hand-turn` rotates the camera while the held item remains fixed | §4.4's split hand rendering; App A.1 `gbuffers_hand` / `gbuffers_hand_water`; `heldItemId` (App D.1) |
| `entities-blocks` | per-draw identity | mobs, armour glint, beacon beam, chest and other block entities; `path entity-orbit` uses explicit translation+yaw samples around the group | App A.1 entity/block program family; `entityId`/`blockEntityId` (App D.4) |

The shadow/sky/weather reference-gap classification is evidence, not contract: PD §4 plus the
verified 1.12.2 hook surfaces
`[V:observed — Pintonium/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinEntityRenderer_Shaders.java;
Pintonium/forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/MixinRenderGlobal_Shaders.java]`.
Their absence from that reference never narrows App E or D-3 coverage.

All six rendered families are classified **motion-sensitive**. That is intentionally conservative:
temporal post-processing can consume the completed image, previous matrices, depth history, or
motion vectors regardless of which gbuffers family supplied the pixels (PD §19.1;
`[V:observed — Pintonium/README.md]`). Each committed
scene therefore contains at least one `/2` `[path]` satisfying §4.3.4; a static shot never
substitutes for its motion gate.

`entities-blocks` exceeds the five families the spec names. It is included because Phase 9's and
Phase 10's impl gates need a scene where per-draw identity is visible, and because a scene set that
cannot see an entity cannot detect the most common class of `mc_Entity` regression. It is authored
in week one and reported separately as Phase 9/10 implementation and regression coverage; it is not
part of the mandatory v0.3 terrain-scene exit criterion.

**Deliberately not in the initial set**, with reasons: a modern-pass scene (`shadowcomp`/`prepare`
have no v0.1–v0.5 implementation, §G8), a compute scene (same), and a per-pack "showcase" scene
(*Scope — out*: scene content tuning is implementation-time).

### 3.5 RESEARCH.md §9 exit criteria → harness runs (the doc-gate table)

Every milestone exit criterion in RESEARCH.md §9, traced to the named runs of §4.9 that gate it.
Run definitions — inputs, procedure, pass condition, artifacts, where they execute — are §4.9's.

| Milestone | Exit criterion, verbatim (§9) | Gating run(s) |
|---|---|---|
| **v0.1** | "≥1 classic pack at T1 on the fixed scenes" | `RUN-T1-APPROVE` (once, human) then `RUN-T1-REGRESS[pack, all scenes]` |
| **v0.1** | "T0 across the classic matrix" | `RUN-T0[classic × all scenes]` |
| **v0.2** | "Classic packs with shadows at T1" | `RUN-T1-REGRESS[classic, incl. `night-shadows`]` |
| **v0.2** | "first T2 runs" | `RUN-T2-PILOT[one classic pack, `terrain-day`]` |
| **v0.3** | "Classic packs at T2 within tolerance on terrain scenes" | `RUN-T2[classic, `terrain-day`]` |
| **v0.4** | "Classic matrix at T2/T3" | `RUN-T2[classic × all scenes]`, `RUN-T3[classic]` |
| **v0.4** | "options round-trip persistence" | `RUN-OPTIONS-ROUNDTRIP` (headless, no renderer needed) |
| **v0.5** | "Full classic matrix at T3" | `RUN-T3[classic × all scenes]` |
| **post-v0.5** | "Modern-matrix progression" | `RUN-T0[dual-spec]`, then `RUN-T1-REGRESS[dual-spec]` |

V3's motion doc gate is independent of those milestone rows: `RUN-MOTION-PATHS[all six families]`
must pass before Phase 2 closes and joins every local/pre-release release gate once capture exists.
The v3 parse-calibration requirement is `RUN-PINTONIUM-PARSE-CALIBRATION[all seven packs]`; it is
headless and belongs to the week-one/fixture-dependent surface, not to a rendering milestone.

Appendix G separately requires dual-spec T0/T1 **through v0.5**, conflicting with §9's
post-v0.5 progression. Pending upstream clarification (§11.3 item 10), the provisional schedule
runs `RUN-T0[dual-spec]` and `RUN-T1-REGRESS[dual-spec]` at each v0.1–v0.5 release gate;
modern-stage feature coverage remains post-v0.5.

Two criteria in that table are *not* image runs and are worth naming as such: v0.1's T0 row is
decided entirely from `RunManifest` predicates (§4.2.1), and v0.4's options round-trip is a headless
golden run that never launches a client (§4.9, `RUN-OPTIONS-ROUNDTRIP`). Both are therefore reachable
earlier and more cheaply than the image tiers, which is deliberate.

**Phase impl gates** (§G6's third testability slice) map to the same catalogue; the mapping is in
§5.3 so that a phase reading only this document's cross-phase section still finds its gate.

---

## 4. Detailed design

### 4.1 Vocabulary, identifiers, paths, and one determinism rule

§G4.1 makes pack-facing terms verbatim; this section fixes the harness's own.

| Kind | Form | Examples |
|---|---|---|
| Pack id | lowercase kebab, stable forever, never the display name | `bsl`, `complementary-reimagined`, `complementary-unbound`, `sildurs-vibrant`, `chocapic13`, `seus-renewed`, `projectluma` |
| Pack version key | the App G version string, normalised | `10.1.3`, `r5.8.1`, `v9` |
| Scene id | as §3.4; equals the file's base name | `night-shadows` |
| Shot id | `<scene>/<shot>` | `water-translucent/underwater` |
| Path id | `<scene>/<path>` | `terrain-day/terrain-pan` |
| Capture-sample id | `<scene>/<SHOT|PATH>/<capture>/<sampleOrdinal>` | `terrain-day/PATH/terrain-pan/3` |
| Profile id | `<vendor-short>-gl<major><minor>[-note]` | `nvidia-gl46`, `mesa-llvmpipe-gl45`, `minimum-gl21` |
| Run id | `RUN-<KIND>[-<QUALIFIER>]` | `RUN-T0`, `RUN-T2-PILOT`, `RUN-GOLDEN-CORE` |
| Artifact path | `<runId>/<packId>@<version>/<sceneId>/<captureKind>/<captureId>/<sampleOrdinal>.png` | — |

Committed, in-repo: `conformance/scenes/*.scene`, `conformance/fixtures/packs.registry`,
`conformance/fixtures/tolerances.profile`, `conformance/baselines/**/*.baseline` (**manifests, not
images**), `conformance/features/<packId>.features`, `conformance/src/test/resources/golden/**`,
`conformance/TIERS.md` (generated, committed).

Never in-repo: the cache root (§4.10.3) — pack archives, generated worlds, run outputs, baseline and
oracle **images**, diff reports.

`[D-P2-4]` **One determinism rule governs every text artifact this phase writes** — scene files,
capture plans, run manifests, golden documents, baseline manifests, tolerance files, reports:
UTF-8, `\n` line endings only, `Locale.ROOT` formatting, keys sorted, floating-point values written
with a fixed decimal form (never locale- or platform-dependent `toString`), no absolute paths, and no
timestamps except in fields explicitly labelled provenance. This is the same property
`PHASE_1_DOC.md` §4.7.5 makes a constraint for `GLCallLog.render()` — *"no timestamps, no identity
hash codes, no iteration-order dependence"* — generalised, because every one of these artifacts is
either compared byte-wise or reviewed in a diff.

### 4.2 Tier machinery

#### 4.2.1 T0 — loads

§8.2's gate is *"pack parses, programs compile, no GL errors, stable frame loop"*. Four predicates,
all evaluated **off the `RunManifest`** (§4.5.4) rather than from live state, so a T0 verdict is
re-derivable from artifacts months later:

| Predicate | Decided by | Fails when |
|---|---|---|
| **parses** | manifest's front-end block: a `PackConfiguration` was produced with zero `FATAL`/`ERROR` diagnostics | any pack-level parse or validation error |
| **programs compile** | manifest's per-slot table: every slot resolved to a linked **and validated** program or is legitimately absent | any `FAILED` slot |
| **no GL errors** | manifest's GL-error block | any recorded `GLError` |
| **stable frame loop** | manifest capture/frame blocks: exact plan coverage, dense sample ordinals, planned/actual pose equality, no uncaught exception, no `CompatVerdict.Bail`, no shaders-off transition, no frame exceeding the hang ceiling | any of those |

`PHASE_1_DOC.md` §4.7.4 states that a drain window may hold an error no facade call caused because
the GL error flag is per-context. That uncertainty affects diagnosis, not the authoritative T0
predicate: **every recorded error fails T0**, whether `attributed` is true or false. Two design
consequences:

- `[D-P2-2]` **Conformance runs set `-Dschmaloogium.debug.recordGL`.** Per `PHASE_1_DOC.md` §4.9.3
  that flag puts `Lwjgl3GLDevice` on the **per-call `glGetError` cadence**. This narrows each
  facade-controlled drain window and lets a record diagnostically name its sole facade call, but
  does not establish that the call caused the error: only Phase 6 replay that isolates the named
  call and reproduces the error yields `ReplayAwareGLError.attributed=true` (§5.2). The cost — a
  synchronous driver query per facade call — is irrelevant here: a conformance run is not a
  performance measurement (*Scope — out*), and the same flag also hands us a `GLCallLog` from a
  real session in the format §4.11's goldens use.
- The report separately counts records with `attributed=false` so diagnosis does not falsely blame
  the facade; this count does not exempt them from the T0 failure.

#### 4.2.2 T1 — renders plausibly

Requires an **approved baseline** for the exact `(packId, packVersion, sceneId, captureKind,
captureId, sampleOrdinal, toleranceProfile, machineClass)` tuple (§4.7). The evaluation is one
`ImageDiffer` call per captured sample against its same-ordinal baseline.

Outcomes are four-valued, and the third exists specifically so a missing oracle can never read as
success: `PASS` · `FAIL` · **`NO_BASELINE`** · `SKIPPED(reason)`. `NO_BASELINE` is not a pass and is
not a failure; it is an instruction to run the approval workflow (§4.7.3).

#### 4.2.3 T2 — pixel-parity vs OptiFine G6

`[D-P2-12]` **T2 applies to the classic tier only, and a T2 request for a dual-spec pack is refused
by `RunRegistry` as a configuration error rather than skipped.** §8.2 is explicit that modern-pack
behaviour on OF-1.12.2 is not a defined baseline; a silent skip would let a report imply the run was
attempted. The refusal names §8.2 in its message.

T2's inputs are the oracle images produced by §4.8's manual protocol plus a same-machine requirement
recorded in both manifests. Tolerance profile: `CROSS_ENGINE` (§4.6.3).

#### 4.2.4 T3 — feature-complete

§8.2's gate has two clauses and the second is the one that makes T3 checkable rather than a vibe.

**Clause 1 — "every feature the pack exercises (per its own option screens) behaves."** Per pack, a
committed `conformance/features/<packId>.features` file lists each feature the pack's own option
screens expose, and binds it to the scene, capture kind/id/sample and observable that demonstrates it:

```
# one row per feature; all four fields required
feature = shadows.soft
  source  = pack option screen "Shadows" → SHADOW_FILTER
  capture = night-shadows/SHOT/main/0
  observe = pack-option A/B: capturing with the option off and on produces images that
            differ beyond SAME_MACHINE tolerance, and both pass T1 against their own baselines
```

The `observe` form above is the general one: a feature "behaves" when toggling it changes the frame
*and* both states are individually stable. That is weaker than "looks correct" — which no automated
oracle can decide — and stronger than "the option parsed", which is what a naive T3 would check.
Features whose observable cannot be expressed this way are recorded with `observe = manual` and a
human sign-off field; the report distinguishes automated from attested rows and never merges them.

**Clause 2 — "no fallback program silently masking a failure"** becomes an assertion:
`[D-P2-13]` for every program slot where the pack **ships a source file**, the run manifest must
record `SOURCED`. A slot recorded `CHAIN` while its source exists means the source failed to compile
and the backup chain hid it — a T3 failure, reported with the slot name and the driver log. A slot
recorded `CHAIN` with **no** source is correct behaviour (App A.2's inheritance) and passes. This is
the reason §4.5.4's manifest carries a per-slot resolution record at all, and the reason §5.4 asks
Phase 4 to expose it.

#### 4.2.5 The ledger, and the evidence rule

`TierLedger` holds one row per `(packId, packVersion, tier, sceneSetId, sceneSetSha256)` with:
outcome, artifact directory, date, machine class, and `evidenceIndexSha256`. The referenced
`evidence.index` is canonical sorted text with dense records
`{sceneId,captureKind,captureId,sampleOrdinal,featureId,variant,runId,manifestSha256,
attestationSha256}`, sorted uniquely by
`(sceneId,captureKind,captureId,sampleOrdinal,featureId,variant,runId)`, where `captureKind` is
`SHOT|PATH` (or empty only for a capture-free manual attestation) and `variant` is
`PRIMARY|FEATURE_OFF|FEATURE_ON|MANUAL`. One `PRIMARY` record (empty `featureId` and
`attestationSha256`) is required for every captured sample declared by every constituent scene, and
no record may name a capture/sample outside the scene schema; this separately enforces exact
scene-set and motion-window coverage while permitting additional evidence.
Automated T3 rows require one `FEATURE_OFF` and one `FEATURE_ON` record for their declared feature
and capture sample. Each points to the run manifest whose pinned options and named capture sample
establish that state and whose T1 result passed. Manual T3 rows require one `MANUAL` record whose non-empty
`attestationSha256` identifies the sign-off artifact; its manifest fields are empty only when no
capture applies. Its bytes are the regular file
`<artifact-directory>/attestations/<attestationSha256>.attestation`. The trusted run-output root is
the established `<cache>/runs` directory from §4.10.3; `artifact-directory` is a normalized
relative path beneath it and may contain no empty, `.`, or `..` component. The index is exactly
`<artifact-directory>/evidence.index`; each non-manual record's manifest is exactly
`<runId>/manifest.manifest`, where `runId` is one non-empty path component other than `.` or `..`.
Starting from the established root, the ledger resolves every component of those paths and the
attestation path without following links, requires directory components and a regular-file leaf,
and rejects missing, escaping, linked, or replaced components. It verifies the index against
`evidenceIndexSha256`, each manifest against its record's `manifestSha256`, and each attestation
against the lowercase hash in its filename before deciding the row. All non-manual records require
a run id and manifest hash. The index SHA-256 covers its exact UTF-8 bytes. It is rendered to
`conformance/TIERS.md` (human) and
`conformance/tiers.state` (machine, sorted, §4.1's rule); both show the scene-set id and hash,
evidence-index hash, and every evidence pointer.

`[D-P2-19]` **The evidence rule: a tier is recorded only with an evidence pointer.** A tier with no
complete, hash-valid evidence index is `NOT_ATTEMPTED`, never a remembered pass. Changing the
scene-set id, its file hash, or its membership invalidates the row; a missing/mismatched constituent
manifest does too. The stored row remains auditable but its effective outcome is `NOT_ATTEMPTED`,
and both renderers identify the stale field or scene. The report also flags *inconsistent* ledgers
within the same pack, version, and scene-set identity — a T2 pass sitting above a T0 failure is
either a harness bug or a stale row, and saying so is cheaper than trusting it.

### 4.3 The scene specification

#### 4.3.1 Format

`[D-P2-14]` A line-oriented, sectioned `key = value` text format parsed by a hand-written parser in
`conformance.scene`. **No new dependency coordinate**: `PHASE_1_DOC.md` §4.2.6 owns the pin table, a
TOML/JSON/YAML library would be a request against it, and the grammar here is small enough that the
parser is smaller than the request. The aesthetic deliberately matches `GLCapabilityProfile`'s text
form (`PHASE_1_DOC.md` §4.7.2): sorted on write, human-readable, diff-friendly.

The canonical scene major is **`schmaloogium.scene/2`**. Grammar: `#` to end of line is a comment;
`[section]` opens a singleton section; `[shot <name>]` opens a static capture; `[path <name>]` opens
a deterministic moving capture; `key = value`; keys are dotted; blank lines are insignificant.
An unknown key is an error, not a warning. Scene defaults are authoring conveniences resolved by
`CaptureRunner` after validation; neither capture-plan writer nor either wire reader defaults a
value. The v1 scene form is historical and no `/1` compatibility reader is required.

#### 4.3.2 Field catalogue

| Section | Key | Type / values | Purpose |
|---|---|---|---|
| `[scene]` | `schema` | exact string `schmaloogium.scene/2` | forward compatibility; any other major fails loudly |
| | `id` | string, must equal the file base name | artifact paths |
| | `description`, `family` | string, one of §3.4's families | report legibility |
| | `minMilestone` | `v0.1`…`post-v0.5` | which milestone first gates on this scene (§9) |
| `[world]` | `seed` | signed long, decimal | worldgen determinism |
| | `worldType`, `generateStructures`, `dimension` | vanilla values; dimension `0`/`-1`/`1` | per-dimension pack folders (§3.1) become testable later |
| | `time` | absolute ticks | pins time-of-day **and** moon phase (moon phase is a function of the world day), and therefore the celestial matrices §4.5 derives |
| | `weather` | `clear` \| `rain` \| `thunder` | `gbuffers_weather`, `wetness` |
| | `weatherTicks` | int | long enough that weather cannot end mid-run |
| | `difficulty`, `gamemode` | `peaceful`, `spectator`/`creative` | suppresses hostile spawns and damage |
| | `gamerule.<name>` | string | §4.4's suppressions, written explicitly rather than assumed |
| | `entity.<n>` | summon spec: type, exact position, NBT incl. `NoAI:1` | the `entities-blocks` scene; entities that move are entities that break diffs |
| | `prepTicks` | int | ticks to run after load before the first capture (chunk load, lighting settle) |
| `[client]` | `width`, `height` | int | frame size; also the FBO size the engine derives (§4.3 of RESEARCH.md) |
| | `fov`, `gamma`, `renderDistance`, `guiScale`, `mipmapLevels`, `particles`, `fancyGraphics`, `clouds`, `ao` | vanilla settings | every one of these changes pixels |
| | `hideGui` | bool, default `true` | `GameSettings.hideGUI` (SRG `field_74319_N`) |
| | `viewBobbing`, `entityShadows`, `smoothCamera`, `anaglyph`, `vsync`, `fullscreen`, `pauseOnLostFocus` | bool, defaults all `false` | motion, vanilla blob shadows (suppressed by the shadow pass anyway — §4.5 of RESEARCH.md — but not before v0.2), and window-state noise |
| `[pack]` | `shaderpack` | a pack id + version, or `internal`, or `OFF` | which fixture the run installs |
| | `option.<name>` | string | pack options pinned for the run (§5.4 asks Phase 12 for the setter) |
| | `engine.<name>` | string | engine-side option values; the key set is **not fixed here** — it mirrors whatever Phase 12's persistence model exposes, and §4.3.3 validates it through a hook that phase supplies |
| `[shot <name>]` | `pos` | `x y z` doubles | camera position, exact |
| | `look` | `yaw pitch` doubles | camera orientation, exact |
| | `heldMain`, `heldOff` | item spec | `gbuffers_hand`, `heldItemId` |
| | `warmupFrames` | int | §4.4's convergence rule |
| | `captureFrames` | int, default `1` | `>1` captures consecutive frames and asserts they are identical — the cheapest possible detector for a nondeterminism leak |
| | `note` | string | why this pose |
| `[path <name>]` | `heldMain`, `heldOff` | item spec, same domains as `[shot]` | held state is fixed for this path; use another path to change it |
| | `warmupFrames` | int | rendered at the first sample pose before sample ordinal 0 |
| | `samples.count` | int, `2..240` | dense explicit sample domain; the hard ceiling bounds run time and artifact volume |
| | `sample.<n>.pos` | `x y z` finite doubles | exact current camera position for rendered sample `n` |
| | `sample.<n>.look` | `yaw pitch` finite doubles | exact current camera orientation for rendered sample `n` |
| | `captureStartSample` | int | first sample ordinal whose completed frame is grabbed |
| | `captureSampleCount` | int, at least `2` | bounded contiguous capture window; `start + count <= samples.count` |
| | `note` | string | why this path and capture window exist |

#### 4.3.3 Validation rules

`SceneValidator` refuses, with a message naming the rule: an unknown key; a missing required key;
`id` ≠ file name; a `warmupFrames` below the family floor (§4.4); a `[shot]` with no `pos`/`look`; a
`[path]` outside the `2..240` sample bound; any missing or non-dense sample index; a capture window
outside the sample range or shorter than two; a captured window with no non-zero position or
orientation delta between consecutive samples; a non-finite coordinate; a `gamerule` that §4.4's
ledger marks mandatory and the file leaves unset; a duplicate capture name across shots and paths;
or a `[pack] engine.*` key that Phase 12's validation hook rejects (until that hook exists, unknown
`engine.*` keys are collected and reported as **unvalidated**, not accepted silently — §5.4).
`SceneCorpusTest` additionally requires every one of §3.4's six scene ids to contain at least one
valid path. `internal` and `OFF` remain valid for headless scene consumers, but `CaptureRunner`'s
client-capture preflight rejects either selection before cache or client-process work; client
capture accepts only an id + version resolving to exactly one registry `PackFixture` and verified
archive.

#### 4.3.4 On "camera paths"

`[D-P2-24]` **supersedes `[D-P2-3]`: a camera path is real frame-to-frame motion represented by
dense explicit samples, never by interpolation or wall-clock time.** Static shots remain valuable
and retain `[shot]`, but do not satisfy the v3 motion gate (PD §19.1;
`[V:observed — Pintonium/README.md]`).

Execution is exact. The agent sets the first pose and renders `warmupFrames` there. It then iterates
sample ordinals `0..samples.count-1`: immediately before each rendered frame it installs that
sample's exact `pos` and `look`, renders exactly one frame, and grabs the result iff the ordinal is
inside `[captureStartSample, captureStartSample + captureSampleCount)`. Sample 0 therefore has the
first pose as both planned current and planned previous state; sample `n>0` has sample `n-1` as its
planned previous state. There is no interpolation, easing, tick-rate conversion, sleep, elapsed-time
condition, or "advance until close" loop. The capture window is contiguous, explicitly bounded,
contains at least two samples, and contains at least one non-zero pose delta.

This makes temporal state deterministic rather than avoiding it: a path's inputs are a finite
frame-indexed sequence, and §4.5.4 records both planned and actual current/previous poses for every
sample. A diff remains keyed by sample ordinal, so two executions compare the same temporal input.

### 4.4 The determinism ledger

The list of everything that can make two runs of the same scene differ, and what this design does
about each. It is the reason the scene format has the fields it has, and it is the first thing to
consult when a T1 diff is flaky.

| Source | How it reaches the frame | Suppression | Residual risk |
|---|---|---|---|
| Day/night advance | world time → sky colour, celestial matrices, shadow direction (§4.5) | `gamerule.doDaylightCycle=false` **and** absolute `[world] time` | none |
| Weather transitions | rain/thunder strength → `gbuffers_weather`, `wetness` | `gamerule.doWeatherCycle=false`, explicit `weather` + `weatherTicks` ≫ run length | none |
| Mob spawning and AI movement | entities in frame | `doMobSpawning=false`, `difficulty=peaceful`, scene entities summoned with `NoAI:1` at exact coordinates | despawn rules; the manifest records the entity count per sample so a mismatch is visible |
| Random ticks (growth, fire, leaf decay) | block states change between runs | `randomTickSpeed=0`, `doFireTick=false`, `mobGriefing=false` | none |
| Item/entity drops | new entities | `doTileDrops=false`, `doEntityDrops=false` | none |
| Particles | additive geometry through `gbuffers_textured` | `[client] particles` pinned; particle-bearing families pin the tick too | particle systems seeded from `Random` — detected by repeated SHOT samples and same-ordinal two-run PATH comparison |
| Animated textures (water, lava, fire, portal) | atlas contents change per client tick | capture at a fixed **frame ordinal** after scene application, so the tick index is fixed | a stall during load shifting the tick — the manifest records world tick and partial tick, so a shift is diagnosable rather than mysterious |
| Partial ticks | interpolation between ticks | same fixed-ordinal rule; the manifest records `partialTicks` | if a family proves flaky, the additive request to Phase 7 is a fixed-partial-tick override (§5.4) |
| Smoothed uniforms — `wetness`/`dryness`, `eyeBrightnessSmooth`, `centerDepthSmooth` | exponential decay toward a target over the pack's declared halflives (§3.2, App A.3) | **the warm-up rule:** `warmupFrames ≥ max(60, 8 × the largest halflife in ticks the pack declares)`, read from the front-end's parsed constants at plan time | a pack declaring an extreme halflife makes a scene slow — visible as run duration, not as a wrong answer |
| Previous-frame camera/matrix uniforms | TAA, bloom, motion vectors, depth history (PD §19.1) | `/2` paths provide an exact pose per rendered frame; first-pose warm-up establishes sample 0 history; manifest records planned and actual current/previous poses | a producer sampling at the wrong lifecycle point; detected as a pose mismatch before image diff |
| Camera-path scheduling | a variable render cadence could select a different pose | one rendered frame per dense sample ordinal; no interpolation, wall-clock waits, or tick-rate conversion; bounded capture window | a skipped/duplicated sample; manifest `actual` count and dense ordinals fail the run |
| `frameCounter`, `frameTimeCounter` | monotonic per-frame values packs may sample | deterministic only because the frame ordinal is; both recorded in the manifest | a pack using wall-clock-derived values — none exist in the G6 contract; the Iris-only `currentDate`/`currentTime` (§3.6.6) are post-v0.5 and are recorded here so they are not reintroduced silently |
| Noise texture | `noiseTextureResolution²` xorshift-generated RGB (§4.6 of RESEARCH.md) | deterministic by construction; resolution recorded in the manifest | a pack-supplied `texture.noise` — recorded by hash |
| World generation | terrain, structures, ores | fixed `seed` **and** a fixed mod set: the manifest records every mod id and jar hash, and a mod-set change invalidates baselines (§4.7.4) | a mod with nondeterministic worldgen; detectable because the world save is cached and re-copied (§4.5.5) rather than regenerated per run |
| Resource packs / language | atlas contents, text | pinned by the mod-set and client blocks | none |
| Window vs framebuffer size (HiDPI) | buffer sizing, `viewWidth`/`viewHeight` | `[client] width/height`, `fullscreen=false`; the manifest records both the window and framebuffer size | **OQ-3 is open and owned by Phase 7**; a platform where they differ would need the scene to pin the framebuffer size instead. Recorded, not solved here |
| GPU, driver, and driver version | everything | not suppressible | handled by tolerance profiles (§4.6.3) and by the `machineClass` field in every baseline manifest |

### 4.5 Capture automation

Designable now, runnable once v0.1 renders — the spec says so and this section is written to that
line: it specifies formats, ordering and obligations, and it does not specify the Mixin injection
points, which are Phase 7's.

#### 4.5.1 The run lifecycle

1. `:conformance` resolves the pack fixture (§4.10) and the scene, applies §4.3.3's client-capture
   restriction, and freezes the registry-owned acquisition mode and licence plus the SHA-512 of the
   verified archive. These
   three values are runner facts; neither scene nor pack content can supply them.
2. It resolves the world save for `(seed, mcVersion, modSetHash)` from the cache, generating it once
   if absent (§4.5.5), then **copies** it into the run directory. A run never mutates its input.
3. `CaptureRunner` resolves **every** scene default, expands each static shot into explicit repeated
   samples, preserves each path's explicit samples, and writes the complete default-free
   `CapturePlan` (§4.5.2), including the three immutable runner facts, into a run directory.
4. It launches the client as a **separate process** — the dev-run configuration the module already
   has — with `-Dschmaloogium.conformance.plan=<plan>`, `-Dschmaloogium.conformance.out=<runDir>`,
   `-Dschmaloogium.debug.recordGL` (`[D-P2-2]`), and a hard wall-clock timeout.
5. The client boots; `CaptureAgent` arms itself only because the plan property is present, and does
   nothing otherwise.
6. The agent loads the copied world, applies `[world]` and `[client]` state, installs the shader pack
   and its pinned options, and waits `prepTicks`.
7. Per resolved capture, in file order: apply held state and the first pose, render the exact warm-up
   count there, then process every dense sample as §4.3.4 specifies — one pose application and one
   rendered frame per ordinal, grabbing only the bounded capture window. Static `SHOT` samples are
   repeated identical poses; `PATH` samples are authored motion.
8. At the frame-end capture point the agent obtains Phase 7's immutable pose report, records
   `SHOT|PATH`, capture id, sample ordinal, and planned/actual current and previous poses for every
   post-warm-up frame, and copies the three plan provenance facts verbatim into its temporary
   `RunManifest` (§4.5.4). It never reads provenance from the installed pack or accepts a
   pack-provided claim.
9. `:conformance` reads the temporary manifest and first compares all three values byte-for-byte
   with the authoritative plan. Missing or unequal values reject the agent artifact; only an exact
   match may be atomically published. It then reads the images, evaluates the requested tiers, and
   writes the report (§4.13).
10. On any failure before the runner atomically publishes a complete manifest, `CaptureRunner`
    writes the canonical failure manifest defined in §4.5.4 using the plan's authoritative pack
    facts. Thus every attempted run is `FAILED` or `SKIPPED` with a serialized reason — never absent,
    never silently green, and never populated by an untrusted self-report (§6).

#### 4.5.2 The capture plan — a deliberately dumb format

The plan is the derived, flat form of the scene: every value already resolved (no defaults to apply,
no expressions, no cross-references), one `key = value` per line, captures and their samples as
ordered dense indexed blocks.
`[D-P2-1]`'s process boundary means the client side must parse *something*; making that something
trivial keeps the rich parser, validator, default resolution, and error messages on the
`:conformance` side where they can be unit-tested without a client. `CapturePlanWriter` serializes
only a complete resolved value; it performs no defaulting. `CapturePlanReader` in `:mod` splits on
`=`, performs no defaulting, and aborts on an unknown or absent key.

**Canonical schema `schmaloogium.capture-plan/2`.** The first line is
`schema = schmaloogium.capture-plan/2`; subsequent lines are `<key> = <value>`, sorted
lexicographically by key. Keys are ASCII dotted identifiers. Strings use JSON string escaping;
booleans are `true|false`, integers are base-10, and finite doubles use §4.1's fixed decimal form.
Required scalar keys are `run.id`, `scene.id`, `scene.hash`, `pack.id`, `pack.version`,
`pack.acquisitionMode`, `pack.archiveSha512`, `pack.licence`,
`world.path`, every non-repeated resolved `[world]`, `[client]`, and `[pack]` field from §4.3.2,
and `captures.count`. Repeated entries use a required count and dense zero-based indices:
`world.gamerules.<n>.{name,value}`, `world.entities.<n>.{type,pos,nbt}`,
`pack.options.<n>.{name,value}`, `pack.engineOptions.<n>.{name,value}`, and each
`captures.<n>.{kind,id,heldMain,heldOff,warmupFrames,samples.count,captureStartSample,
captureSampleCount,note}`. `kind` is exactly `SHOT|PATH`. Every capture has dense
`captures.<n>.samples.<m>.{pos.x,pos.y,pos.z,look.yaw,look.pitch}` records. For `SHOT`, the runner
expands the authored pose into `captureFrames` identical samples, with capture start zero and count
equal to the sample count. For `PATH`, the records are the authored samples unchanged, and its
window obeys §4.3.3. `heldMain`, `heldOff`, and `note` are required JSON strings (empty means
absent); every other listed field is required. Capture order is authored block order; sample order
is ordinal order.
`pack.acquisitionMode` is exactly `MODRINTH|MANUAL`; `pack.archiveSha512` is exactly 128 lowercase
hexadecimal digits; and `pack.licence` is a non-empty JSON string. `CaptureRunner` derives these
values from the resolved `PackFixture` and verified archive, and the plan hash covers their exact
serialized bytes. `CapturePlanWriter` accepts them only from the runner-owned fixture resolution;
the scene model, pack options, archive contents, and agent expose no alternate write path.
Consequently schema `/2` has no `internal`/`OFF` sentinel form: preflight rejects those scene
selections before a plan is written.
Indices must cover `0..count-1` without gaps, and capture index is execution order. Duplicate,
missing, malformed, unknown, or out-of-version keys abort before world load; schema major versions
other than `2` are unsupported. `/1` is historical evidence and no compatibility reader or migration
path is required. A canonical fixture in
`conformance/src/test/resources/wire/capture-plan-v2.plan` must parse and re-render byte-identically.

#### 4.5.3 The frame grab

`[D-P2-15 companion]` The agent needs a `BufferedImage` of the finished frame **without referencing
LWJGL**, because constraint C-3 confines `org.lwjgl` to `com.schmaloogium.mod.glue` and the agent
lives elsewhere. Minecraft already provides exactly this call:

> `net.minecraft.util.ScreenShotHelper.createScreenshot(int width, int height, Framebuffer framebufferIn)`
> → `java.awt.image.BufferedImage` — SRG `func_186719_a`, descriptor
> `(IILnet/minecraft/client/shader/Framebuffer;)Ljava/awt/image/BufferedImage;` `[V:mcp 2026-07-25]`

against `Minecraft`'s own framebuffer. So the agent reads the frame through a vanilla API, writes the
PNG itself (rather than through `saveScreenshot`, which time-stamps the filename and returns a chat
component), and stays C-3-clean.

Two properties of the write:

- `[D-P2-16]` **Image identity is the hash of the pixels, not of the file bytes.** The baseline
  manifest stores a SHA-256 over the raw ARGB raster in a fixed scan order, so an ImageIO or encoder
  change can never look like a rendering change.
- PNG output is written with metadata suppressed (no `tIME`, no text chunks), per §4.1's rule.

**The moment of capture is a requirement on Phase 7, not a design here.** The frame must be grabbed
after the engine's `final` pass has written the vanilla framebuffer (§4.3 of RESEARCH.md, "Final
renders to the vanilla framebuffer") and before that framebuffer is presented. `hideGui = true`
removes the HUD, but the ordering obligation remains, and §5.4 states it as a request.

#### 4.5.4 The run manifest

The single most important artifact this phase defines: it is what T0 and T3 are decided from, what
makes a tier claim defensible months later (§4.2.5), and what makes a flaky diff diagnosable.

| Block | Contents |
|---|---|
| `run` | run id, scene id + scene-file hash, plan hash, start/end wall clock (provenance fields), exit status |
| `environment` | OS, JVM, MC version, Cleanroom loader version, mod list with ids and jar hashes, resource-pack list |
| `gl` | `GLCapabilityProfile` in §4.7.2-of-Phase-1's text form — vendor, renderer, GL and GLSL version, the max-* probes, extensions |
| `pack` | pack id, version key, acquisition mode, archive SHA-512, licence line copied from the registry (§3.1), the option values pinned by the scene |
| `programs` | per program slot: `SOURCED` \| `CHAIN(from=<slot>)` \| `ABSENT` \| `FAILED(log)`, plus whether the pack shipped a source file for it — the pair that makes §4.2.4's clause 2 decidable |
| `resources` | the sizing decisions the engine made: colour-buffer count and formats, depth textures, shadow buffers and resolution, `centerDepthSmooth` on/off, noise resolution — the live counterpart of §4.11's `SIZING` golden |
| `hooks` | the complete frozen Phase 7 hook-application report: every primary row plus every nested owner subreport, including Phase 8's immutable health rows when installed; never a capability reconstruction |
| `captures` | per capture: `SHOT|PATH`, id, planned/actual warm-up and sample counts, and bounded capture window |
| `frames` | one record per post-warm-up sample: capture kind/id, sample ordinal, captured bit, planned/actual current and previous camera poses, world tick, `partialTicks`, `frameCounter`, entity count, duration |
| `gl_errors` | all `GLError` records (op, subject, kind, detail, attributed); the reported unattributable count is the number whose `attributed=false` (§4.2.1) |
| `images` | per captured sample: capture kind/id, sample ordinal, path, dimensions, pixel-raster SHA-256 |
| `diagnostics` | Phase-2-owned wire records with `code`, `severity`, `channel`, `file`, and `line` |

**Canonical schema `schmaloogium.run-manifest/2`.** It uses the same line, scalar, escaping, sorting,
duplicate-key, and major-version rules as the capture plan. The first line is
`schema = schmaloogium.run-manifest/2`; blocks above become dotted key prefixes. Every scalar
named in the table has the table's lower-camel dotted name and is required except
`run.startedAt`/`run.endedAt`, which are optional provenance strings. The required run/environment
scalars are
`run.{id,sceneId,sceneHash,planHash,exitStatus,failureReason,uncaughtException,compatVerdict,
shadersActiveThroughout,hangCeilingMillis,timedOut}`,
`frontEnd.{completed,packConfigurationProduced}`, and
`environment.{os,jvm,minecraftVersion,cleanroomVersion,worldSha256,modSetSha256}`, plus
`gl.available`, `resources.available`, and `hooks.available`.
The required pack scalars are JSON strings `pack.{id,version,acquisitionMode,archiveSha512,licence}`.
`pack.id`, `pack.version`, and `pack.licence` are non-empty; `pack.acquisitionMode` is exactly
`MODRINTH|MANUAL`; and `pack.archiveSha512` is exactly 128 lowercase hexadecimal digits.
The last three are a direct projection of the capture plan's runner-owned facts. Before atomic
publication, `CaptureRunner` requires exact equality for all three; absent or mismatched values
produce a runner-synthesized failure manifest carrying the plan values. Agent or pack self-report
never overrides them (`[D-P2-23]`).
The schema describes client-capture attempts only; because preflight admits only registry-backed
packs, no manifest encodes `internal` or `OFF` and no sentinel provenance values exist.
`run.exitStatus` is exactly `COMPLETE|FAILED|SKIPPED`; `failureReason` and `uncaughtException` are
required strings (empty means none), `compatVerdict` is exactly `Continue|Bail|NOT_REACHED`, and
the completion/availability fields are booleans while `hangCeilingMillis` is a non-negative
integer. Repeated records use a required
`<block>.count` plus dense zero-based
`<block>.<n>.<field>` keys: `environment.mods{id,sha256}`, `environment.resourcePacks{id,sha256}`,
`pack.options{name,value}`, `programs{slot,status,from,sourcePresent,driverLog}`,
`captures{kind,id,plannedWarmupFrames,actualWarmupFrames,plannedSamples,actualSamples,
captureStartSample,captureSampleCount}`,
`frames{captureKind,captureId,sampleOrdinal,captured,plannedCurrent.posX,plannedCurrent.posY,
plannedCurrent.posZ,plannedCurrent.yaw,plannedCurrent.pitch,plannedPrevious.posX,
plannedPrevious.posY,plannedPrevious.posZ,plannedPrevious.yaw,plannedPrevious.pitch,
actualCurrent.posX,actualCurrent.posY,actualCurrent.posZ,actualCurrent.yaw,actualCurrent.pitch,
actualPrevious.posX,actualPrevious.posY,actualPrevious.posZ,actualPrevious.yaw,
actualPrevious.pitch,worldTick,partialTicks,frameCounter,entityCount,durationMillis}`,
`gl_errors{op,subject,kind,detail,attributed}`,
`images{captureKind,captureId,sampleOrdinal,path,width,height,pixelSha256}`, and
`diagnostics{code,severity,channel,file,line}`. `from` is non-empty only for `CHAIN`; `driverLog`
only for `FAILED`; otherwise both are required empty strings. When `gl.available=true`, the `gl`
prefix embeds exactly the canonical Phase 1 `GLCapabilityProfile` fields; when false, those fields
must be absent.

Capture records occur in plan order and use exactly the plan's `kind` and `id`; frame records are
sorted by `(capture index, sampleOrdinal)` and cover every planned post-warm-up sample exactly once
in a complete manifest. `sampleOrdinal` is dense from zero for each capture. `captured=true` exactly
inside that capture's planned window and false outside; image records have a one-to-one key match
with those true frame records. Every pose component is a finite fixed-decimal number. Planned
current/previous poses are copied from the plan using §4.3.4's previous-sample rule; actual poses
are copied from Phase 7's immutable frame pose report at the capture hook, never inferred from the
image or echoed by `SceneApplier`. A complete run requires exact normalized equality of planned and
actual poses and exact planned/actual warm-up and sample counts; any mismatch fails the run before
image diff. These records make static and moving execution reconstructible from the plan hash plus
manifest without wall-clock reasoning.

Every diagnostic field is required. `code`, `severity`, `channel`, and `file` are JSON strings:
`code` is non-empty; `severity` is exactly `INFO|WARN|ERROR|FATAL`; and `channel` is exactly
`CHAT|SHADER_GUI|LOG_ONLY`. `file` is empty when unavailable and otherwise non-empty. `line` is a
non-negative decimal integer, with `0` meaning unavailable; a positive line requires a non-empty
`file`. These are Phase-2 wire domains, not consumption of Phase 1's diagnostic domain types.

When `resources.available=true`, these keys are required and are the complete resource wire block.
Counts and resolutions are non-negative decimal integers; booleans are `true|false`; formats and
program/name fields are JSON strings. `resources.colorBuffers.count` governs dense
`resources.colorBuffers.<n>.{format,clear,clearColorR,clearColorG,clearColorB,clearColorA}` records;
clear colours are finite JSON numbers even when `clear=false`.
`resources.depthTextures.count`, `resources.shadow.{depthTextures,colorTextures,resolution}`,
`resources.centerDepthSmooth.enabled`, and `resources.noise.resolution` are scalars.
The three shadow property sets are dense boolean records governed by the corresponding texture
count: `resources.shadow.depth.<n>.{hardwareFiltering,mipmap,nearest}` and
`resources.shadow.color.<n>.{hardwareFiltering,mipmap,nearest}`.
`resources.vertexAttributes.count` governs dense `{program,name}` records, one per opted-in
`mc_Entity|mc_midTexCoord|at_tangent` pair; `resources.instances.count` governs dense
`{program,count}` records. Vertex-attribute records are sorted uniquely by `(program,name)`;
instance records are sorted uniquely by `program`, and instance counts are positive integers.
`resources.capabilityGate` is exactly `OK|SHORTFALL`;
`resources.capabilityShortfalls.count` is zero for `OK`, otherwise governs dense
`{limit,required,available}` records sorted by `limit`, where `limit` is exactly
`maxDrawBuffers|maxColorAttachments|maxTextureImageUnits` and the values are non-negative integers.
When `resources.available=false`, every other `resources.*` key is absent. A complete agent manifest
requires all three availability flags true; a runner-synthesized failure manifest may use false.

When `hooks.available=true`, `hooks.rows.count` governs the complete dense primary report in its
source list order. Every `hooks.rows.<n>` has required
`{catalogId,target,expectedCount,actualCount,classes.count,fallback}` fields;
`hooks.rows.<n>.classes.<m>` is dense, unique, and in closed enum order
`CORE,FEATURE,OBSERVER,DEFERRED`, with values drawn only from that set. `catalogId` and `target` are
non-empty JSON strings, counts are non-negative integers, `classes.count` is positive, and
`fallback` is exactly `NONE|EVENT|VANILLA|SHADERS_OFF`.
`hooks.rows.<n>.deferredOwnerPhase` is present and a positive integer exactly when the class list
contains `DEFERRED`; otherwise that key is absent. Primary catalog IDs are unique. The writer
preserves the frozen Phase 7 list's IDs, target strings, order, counts, classes, deferred owner, and
fallback exactly; it neither sorts the report into a new identity nor drops dormant rows.

`hooks.subreports.count` governs dense records sorted by unique positive
`hooks.subreports.<n>.ownerPhase`. Each record requires
`{ownerPhase,canonicalFingerprint,featureEnabled,rows.count}`;
`canonicalFingerprint` is exactly 64 lowercase hexadecimal digits, `featureEnabled` is boolean,
and `rows.count` is positive. Its dense rows require
`{catalogId,expectedCount,actualCount,disposition}`, are sorted by unique non-empty `catalogId`, and
use only `HEALTHY|FEATURE_DISABLED`. Owner phase 8, when present, is a byte-for-value projection of
Phase 7's nested `ShadowHookHealth` report: all eight rows, their existing IDs, expected/actual
counts, dispositions, canonical fingerprint, and aggregate enabled bit. Missing owner phase 8 is
explicit absence, never healthy shadow capability. `hooks.available=false` requires every other
`hooks.*` key to be absent. No manifest producer or consumer may infer a row, count, disposition,
fingerprint, or enabled bit from a successful frame, image, selected program, bound resource,
diagnostic, or any other runtime behavior; the sole source is Phase 7's frozen
`HookApplicationReport`.
`gl_errors.count` counts all dense records, every record's `attributed` field is a required boolean,
and the unattributable-error count is derived only as
`count(gl_errors.<n>.attributed == false)`; no separate scalar encodes it.
Before either copy or hash, the cache tree is walked without following links. Only directories and
regular files are admitted; every symbolic link, socket, device, FIFO, or other entry is a hard
containment failure, including a link whose target would remain inside the cache root. The copy
preserves the admitted relative tree and bytes, and the digest is computed from that copied tree.
`environment.worldSha256` sorts `/`-separated regular-file relative paths by UTF-8 byte order and
hashes, for each file, its UTF-8 path, one zero byte, its base-10 byte length, one zero byte, and its
bytes. `environment.modSetSha256` applies the same
framing to the `environment.mods` records sorted by `(id,sha256)`, using `id` as path and the
lowercase hexadecimal jar digest as content. Both are lowercase SHA-256 and are the sole identities
used by §4.7 baseline approval and invalidation.

T0 is derived without live state: `parses` requires `frontEnd.completed`,
`frontEnd.packConfigurationProduced`, and no `FATAL`/`ERROR` diagnostic; stable-frame-loop requires
`exitStatus=COMPLETE`, empty `uncaughtException`, `compatVerdict=Continue`,
`shadersActiveThroughout=true`, `timedOut=false`, exact plan/manifest capture coverage, exact
planned/actual warm-up and sample counts, exact normalized planned/actual current and previous poses,
and every `durationMillis <= hangCeilingMillis`. Program and GL predicates use their existing blocks, and T0
also requires all three availability flags true. Hook availability proves evidence completeness;
individual hook dispositions affect T0 only through existing engine outcomes such as
`shadersActiveThroughout`, never through a Phase-2 capability guess.
`CaptureRunner` validates the agent's temp file and atomically publishes it; if launch, timeout,
crash, malformed/truncated output, or any earlier step prevents publication, it instead emits a
schema-valid manifest from the plan and runner observations with `exitStatus=FAILED`,
`failureReason` set, unknown client evidence represented by the false/`NOT_REACHED` values above,
and zero counts for unavailable repeated blocks. Such a manifest deterministically fails T0.
Unknown core keys fail; extension keys under `x.<producer>.*` are preserved and reported, and never
affect a verdict. A canonical full-block fixture at
`conformance/src/test/resources/wire/run-manifest-v2.manifest` must round-trip byte-identically;
truncation or a missing required count/field names the incomplete block. Schema `/1` is historical;
the `/2` reader rejects it and no compatibility reader or automatic migration is required.

#### 4.5.5 Worlds

Generated once per `(seed, mcVersion, modSetHash)` into the cache, then copied per run. Generation is
the slowest and least deterministic step in the pipeline; doing it once and hashing the result turns
"did worldgen change?" into a cheap comparison instead of a mystery. The canonical world and
mod-set hashes are recorded in the manifest and are baseline-invalidation triggers (§4.7.4).

#### 4.5.6 The agent's own failure posture

The capture agent is a debug affordance living inside a shipped mod. §G2.4's rung 5 — *"nothing in
the shader engine ever crashes the client"* — applies to it directly: it arms only on an explicit
system property, every step is guarded, and any failure disarms the agent, logs on
`schmaloogium.conformance` (the channel `PHASE_1_DOC.md` §4.9.2 already assigns to Phase 2) and
leaves the client running normally so the failure can be observed. It never rethrows into vanilla's
call stack. §6 carries the row.

### 4.6 Image diff

#### 4.6.1 Preconditions

Both images must have identical dimensions and colour model. A mismatch is a **hard error**, not a
large diff: comparing a 1920×1080 candidate against a 1280×720 baseline produces a number, and the
number means nothing. Scene-pinned `[client] width/height` is what makes this a real check.

#### 4.6.2 Three levels

`[D-P2-7]`

- **L1, per pixel.** Per-channel absolute difference in 8-bit sRGB. A pixel is *differing* when its
  maximum channel delta exceeds `channelTolerance`.
- **L2, aggregate.** `differingFraction ≤ maxDifferingFraction`, `maxChannelDelta ≤ maxDelta`,
  `rmse ≤ maxRmse`.
- **L3, cluster.** Connected components (4-neighbour) over the differing-pixel mask:
  `largestClusterArea ≤ maxClusterArea` and `clusterCount ≤ maxClusters`.

**L3 is the level that earns its keep and the reason an aggregate-only model was rejected.** A 40×40
block of entirely wrong pixels is 0.08 % of a 1920×1080 frame — under any sane aggregate threshold —
and is exactly what a broken hand, a missing beacon beam, or a shadow that stopped being cast looks
like. Conversely, driver-level dithering differences are diffuse and never cluster. L3 separates
"structurally different" from "numerically noisy" using the only property that reliably distinguishes
them.

Considered and rejected: SSIM or any perceptual metric as the *gate*. Both blur small structural
failures — the exact class L3 exists to catch — and both would add a dependency coordinate against
`PHASE_1_DOC.md` §4.2.6's pin table. If calibration data later shows the three levels misclassifying,
a perceptual metric can be added as an additional **reported** number before it is ever a gate.

#### 4.6.3 Tolerance profiles

Named, committed in `conformance/fixtures/tolerances.profile`, and cited by every verdict — a diff
result without a named profile is not reproducible.

| Profile | Used by | `channelTolerance` | `maxDifferingFraction` | `maxDelta` | `maxClusterArea` |
|---|---|---|---|---|---|
| `IDENTICAL` | repeated SHOT samples and same-ordinal repeated PATH runs; golden-adjacent uses | 0 | 0 | 0 | 0 |
| `SAME_MACHINE` | T1 regression on one machine | 1 | 0.0005 | 8 | 64 |
| `CROSS_DRIVER` | T1 after a driver or GPU change | 3 | 0.005 | 24 | 512 |
| `CROSS_ENGINE` | T2 vs OptiFine G6, same machine | 6 | 0.02 | 48 | 4096 |
| `ADVISORY` | cross-vendor comparisons | — | — | — | — |

**These numbers are starting points, and this document says so rather than implying they are
measured.** No frame has ever been rendered by this engine; §4.6.5 is the procedure that replaces
them with evidence, and the profile file carries a `calibratedOn` provenance field that is empty
until it runs. `ADVISORY` deliberately has no thresholds: it produces a full report and **never a
verdict**, because a cross-vendor pixel comparison is information, not a gate.

#### 4.6.4 Ignore masks

Per captured sample, an optional sidecar keyed by `(captureKind,captureId,sampleOrdinal)` lists
rectangles to exclude, in the scene directory and committed (a handful of integers, not an image).
Every mask must carry a `reason` field, and the report prints the masked fraction — an ignore mask is
a small admission of defeat and should be visible as one.

#### 4.6.5 Calibration procedure

1. Capture one scene twice on the same machine with no change at all → the floor for `SAME_MACHINE`
   is the observed maximum, not zero, if the observed maximum is not zero (and if it is not zero,
   §4.4 has a leak worth finding first).
2. Capture again after a driver update, or on a second machine with the same GPU family → the
   `CROSS_DRIVER` floor.
3. For `CROSS_ENGINE`, diff OptiFine G6 against itself across two captures first (isolating capture
   noise from engine difference), then against our render.
4. Write the observed maxima × a stated safety factor into the profile file, with
   `calibratedOn = <date, GPU, driver>`.

### 4.7 Baselines: storage, versioning, approval

#### 4.7.1 The constraint that shapes the storage design

App G settles that no matrix **pack** may be committed or re-hosted (§8.3, resolved OQ-11). A
rendered frame is a *different artifact*: it is not the pack's files, but it is produced by the pack's
shader code operating on Minecraft's assets. **RESEARCH.md does not settle that case**, and this
document is not the place to decide a licensing question by assertion.

`[D-P2-6]` **Baseline and oracle images are therefore never committed to the repository.** They live
in the same never-in-repo cache as the fixtures (§4.10.3). What *is* committed is a **baseline
manifest** — hashes and provenance — which is our own metadata, is small, diffs readably, and makes a
stale or substituted baseline detectable. The cost is that a fresh clone cannot run T1 until it has
captured or been given baselines, which is acceptable because T1 needs a GPU and a client anyway and
therefore never runs in CI regardless (§10's fallback). V3 §G6 now makes this conservative
derived-artifact posture binding across phases rather than leaving it as a Phase 2-only choice.

#### 4.7.2 Layout

Cache (never committed):
`<cache>/baselines/<packId>@<version>/<sceneId>/<captureKind>/<captureId>/<sampleOrdinal>.png`.
Repo (committed): `conformance/baselines/<packId>@<version>/<sceneId>.baseline`, one file per scene,
sorted, containing one record per captured sample:

```
captureKind     = PATH
captureId       = terrain-pan
sampleOrdinal   = 3
pixelSha256     = 3f0a…
width           = 1920
height          = 1080
toleranceProfile= SAME_MACHINE
machineClass    = nvidia-gl46 / 580.xx / linux-x86_64
approvedBy      = <name>
approvedOn      = 2026-08-03
runId           = RUN-T1-APPROVE
runManifestSha  = 9c21…
sceneSha256     = 71bd…
worldSha256     = a0e4…
modSetSha256    = 5512…
```

#### 4.7.3 The approval workflow (human, once per pack version)

1. Run `RUN-T1-APPROVE`, which captures every selected SHOT and every bounded PATH sample and writes
   a **contact sheet** — an HTML index of every image at review size, grouped by capture and ordinal,
   with the run manifest inline.
2. A human looks at every image and decides whether the pack is *rendering plausibly* — §8.2's own
   words, and a judgement no automation makes.
3. On acceptance the harness promotes the images into the baseline cache and writes the baseline
   manifest with the run manifest's `environment.worldSha256` and
   `environment.modSetSha256` unchanged, plus the approver and date.
4. The manifest is committed. From that point the images are a regression oracle and §4.2.2 applies.

**Approval is never automatic, and specifically never automatic in CI.** There is no
`--auto-approve`; the closest affordance is re-running the workflow. A harness that can approve its
own baselines has no oracle at all.

#### 4.7.4 Invalidation

| Trigger | Detected by | Effect |
|---|---|---|
| Pack version changed | registry pin / archive SHA-512 | baselines for the old version stay; the new version starts at `NO_BASELINE` |
| Scene file changed | `sceneSha256` | `NO_BASELINE` for that scene; re-approval required |
| World or mod set changed | compare the current run manifest's `environment.worldSha256` / `environment.modSetSha256` to the baseline fields | `NO_BASELINE`, with the changed field named |
| Machine class changed | manifest comparison | not invalid — the profile escalates to `CROSS_DRIVER` and the report says so |
| Engine behaviour changed | *not* auto-detected | deliberate: an engine change that alters the image is exactly what T1 exists to catch. A change believed to be an improvement is re-approved by a human, and the manifest's `approvedOn` records that it was |

### 4.8 The T2 protocol — capturing OptiFine G6 oracle renders

Manual, outside CI, and documented step by step, because OptiFine is not redistributable (§10.3) and
the oracle images are local artifacts (§G6). This is a procedure, not a program; the only code
involved is the differ that consumes its output.

#### 4.8.1 Preconditions

| Requirement | Why |
|---|---|
| A **separate** Forge 1.12.2 installation with **OptiFine HD_U G6_pre1** — the exact reference build (§0.3 of RESEARCH.md) | Any other OF build is a different oracle |
| **The same machine, GPU and driver version** as the candidate capture, recorded in both manifests | §4.6.3's `CROSS_ENGINE` profile assumes it; cross-vendor T2 is `ADVISORY` only |
| The same world save, copied from the harness cache (§4.5.5) | Same terrain, same everything |
| The same pack archive, verified by SHA-512 against the registry | A different pack version is a different oracle |
| Vanilla video settings matching the scene's `[client]` block exactly | Every one of those settings changes pixels |
| **OptiFine's own features neutralised**: antialiasing off, anisotropic filtering off, fast render off, render quality 1.0, shadow quality 1.0, and the normal/specular-map and hand-depth/old-lighting/old-hand-light toggles set to the values the scene pins | §1.2 records that OF's interlock matrix exists *because* it bundles AA/AF/fast-render and that Schmaloogium has none of them. Leaving them on compares our engine against a different renderer |

#### 4.8.2 Procedure, per scene's selected static shots

1. Copy the cached world save into the OF instance; launch; open the save in singleplayer.
2. Apply the scene's world state by command, using the scene file's own values verbatim:
   `/gamerule doDaylightCycle false`, `/gamerule doWeatherCycle false`, `/time set <ticks>`,
   `/weather <clear|rain|thunder> <weatherTicks>`, `/difficulty peaceful`, `/gamemode <mode>`, plus
   each `gamerule.<name>` row.
3. Select the pack in OF's shader GUI and set the pack options the scene pins.
4. For each selected `[shot]`: `/tp @p <x> <y> <z> <yaw> <pitch>` with the scene's exact decimals, set the held
   items, hide the HUD (F1), ensure the debug overlay is off, wait for the pack's smoothed values to
   settle — the same wall-clock the warm-up rule implies (§4.4) — then take one screenshot.
5. Move the screenshots into
   `<cache>/oracle/<packId>@<version>/<sceneId>/SHOT/<shotId>/0.png`.
6. Run the oracle-manifest tool: it hashes each image's **pixel raster** (§4.5.3), records the OF
   build, GPU, driver, date and operator, and writes
   `conformance/oracle/<packId>@<version>/<sceneId>.oracle` — **committed, manifest only, no images**
   (§4.7.1's policy applies identically here).

#### 4.8.3 Properties of the protocol worth stating

- **The oracle can go stale silently, and the manifest is the defence.** An OF settings change or a
  driver update between oracle capture and candidate capture would otherwise show up as an engine
  defect. Every T2 verdict prints the oracle manifest's provenance line next to the candidate's.
- **Positional exactness is achievable but not free.** `/tp` with explicit decimals sets position and
  rotation exactly; eye height and any residual view offset are the same on both engines because both
  are vanilla's. Any residual sub-pixel offset shows up as a diffuse, uniform difference — which is
  exactly the shape `CROSS_ENGINE`'s tolerance is calibrated against in §4.6.5 step 3.
- **T2 is only ever run for the classic tier** (`[D-P2-12]`), and only for scenes whose features the
  milestone actually implements: a T2 run against `night-shadows` before v0.2 compares our missing
  shadow pass against OF's, which is a known-fail, not information. §4.9 encodes that as the run's
  declared scene set per milestone rather than leaving it to judgement.
- **T2's manual OF protocol uses static shots; it does not discharge the v3 motion gate.** Exact
  frame-indexed PATH replay has no trustworthy manual OF control surface. Moving-camera coverage is
  therefore the required T1 self-baseline `RUN-MOTION-PATHS` across all six families, while T2 keeps
  its narrow purpose: same-machine classic-pack pixel parity at reproducible static poses.

### 4.9 The named harness runs

The catalogue §3.5 and §5.3 both point at. Each run is a `HarnessRun` value in `conformance.run`
with these fields, so that "which runs gate v0.3?" is a query and not a document search.

| Run id | Context | Inputs | Pass condition | Where it runs |
|---|---|---|---|---|
| `RUN-GOLDEN-CORE` | A (headless) | internal default pack + the §4.11.6 micro-packs × the whole profile fixture set | every produced golden document is byte-identical to its committed counterpart | **CI, every push** (`:conformance:test`) |
| `RUN-CAPS-GATE` | A | micro-packs × `minimum-gl21` and the hostile synthetic profile | the front-end's capability-dependent decisions match the golden; a pack that cannot fit the profile produces the expected refusal diagnostic, not an exception | **CI, every push** |
| `RUN-GOLDEN-MATRIX` | A + fixtures | the 7 matrix packs × a 3-profile subset | as `RUN-GOLDEN-CORE` | CI `conformance` job (network) |
| `RUN-PINTONIUM-PARSE-CALIBRATION` | A + fixtures + prior local observation records | the exact 7 matrix archives + one source-text-free Pintonium observation each | all observations present/hash-matching and Schmaloogium parses all seven; Pintonium outcome equality is reported, never required | CI `conformance` job after explicit local calibration capture |
| `RUN-OPTIONS-ROUNDTRIP` | A + fixtures | matrix packs' option sets | parse → mutate → persist → re-parse yields an identical model, and the persisted file contains only changed options (§4.7 of RESEARCH.md) | CI `conformance` job |
| `RUN-SCENE-SELFCHECK` | B (client) | one pack, all scenes, three-sample static shots plus every path | repeated static samples compare `IDENTICAL`; two fresh executions of each path compare same-ordinal images `IDENTICAL`; both manifests pass pose/count reconstruction | local / pre-release |
| `RUN-MOTION-PATHS` | B (client) | one registry-backed pack × the required moving path in all six families | every path's full sample sequence executes twice; manifests match `/2` plan/current/previous pose semantics; every captured ordinal passes T1 against its approved same-ordinal baseline | local / pre-release and Phase 2 close gate |
| `RUN-T0` | B | pack × scene set | §4.2.1's four predicates | local / pre-release |
| `RUN-T1-APPROVE` | B + C (human) | pack × scene set | contact sheet produced; a human approves; manifests written | local, once per pack version |
| `RUN-T1-REGRESS` | B | pack × scene set + approved baselines | every selected SHOT and bounded PATH sample passes at its profile; `NO_BASELINE` is not a pass | local / pre-release |
| `RUN-T2-PILOT` | B + C | one classic pack, `terrain-day`, oracle present | `CROSS_ENGINE` pass | local, v0.2 |
| `RUN-T2` | B + C | classic packs × the milestone's declared static SHOT set, oracles present | `CROSS_ENGINE` pass on every selected static shot; moving coverage is the separate T1 `RUN-MOTION-PATHS` gate | local, v0.3+ |
| `RUN-T3` | B | pack × scene set + `<packId>.features` + run manifest | every automated feature row observed; every attested row signed; **zero `CHAIN`-with-source slots** (§4.2.4) | local, v0.4+ |
| `RUN-GL-SMOKE` | B′ (`:mod` test JVM) | none | a compatibility-profile GL context ≥ 2.1 is created and a GLSL 120 shader compiles | the OQ-10 spike's subject (§10) |

`RUN-GOLDEN-CORE` and `RUN-CAPS-GATE` are the two runs that are green in CI **before a single line of
renderer exists**, which is what makes §9.2's week-one subset more than a promise.

### 4.10 The fixture system

#### 4.10.1 The registry

`conformance/fixtures/packs.registry`, committed, sorted, one block per App G row:

```
[pack bsl]
displayName   = BSL Shaders
tier          = dual-spec
mode          = MODRINTH
modrinthProject = <slug>
modrinthVersion = <version id>          # pinned; never "latest"
version       = 10.1.3
sha512        = <hex>
archiveName   = <filename as published>
licence       = ARR — no bundling, no re-hosting (RESEARCH.md App G)
sourceUrl     = https://modrinth.com/shader/bsl-shaders
```

`[D-P2-20]` **The `modrinthVersion` and `sha512` fields are left unfilled by this document and are
populated by the implementation effort.** RESEARCH.md App G gives version *names* and dates, not
version IDs or hashes; inventing them here would be fabricating a pin that CI would then trust. §12
carries the population step, and `FixtureResolver` refuses a registry entry with an empty pin rather
than falling back to "latest" — a floating pin is a silently changing oracle.

#### 4.10.2 Acquisition modes

- **`MODRINTH`** — resolve `GET /version/{id}` on the Modrinth API. The response carries `files[]`,
  each with `url`, `filename`, `primary`, `size` and `hashes` containing `sha512` and `sha1`
  `[V:web docs.modrinth.com getversion, 2026-07-25]`. The resolver takes the `primary` file, checks
  its `sha512` against the registry pin **before** download (fail fast on a changed artifact) and
  again over the downloaded bytes. Pinning by version **ID** rather than project + version *name* is
  what makes the fixture immutable.
- **`MANUAL`** — SEUS Renewed, Chocapic13 V9 and projectLUMA are canonical-download-only (§8.3). The
  resolver never fetches these. If the expected file is absent from the cache it prints the canonical
  URL, the exact expected filename, the expected SHA-512 and the exact target path, and the run
  reports `SKIPPED(fixture-absent)`.

**Client behaviour that does not depend on figures this document could not verify:** the operation
page consulted documents the endpoint and the JSON shape but carries no rate-limit or User-Agent
text, so the downloader is written conservatively rather than to a number: one descriptive
`User-Agent` identifying the project and a contact, **no parallel fixture fetches**, honour `429`
and `Retry-After` with bounded backoff, and re-read Modrinth's rate-limit documentation as a §12
checklist item before the first CI run.

#### 4.10.3 The cache, and where it may not be

Root resolution order: `-Dschmaloogium.conformance.cacheDir` → `$SCHMALOOGIUM_CONFORMANCE_CACHE` →
`$XDG_CACHE_HOME/schmaloogium/conformance` → `~/.cache/schmaloogium/conformance`.

`FixtureResolver` and `TierLedger` share one root establishment routine. It resolves a relative
configured value against the process working directory, converts it to an absolute normalized
path, and finds its nearest existing ancestor without following links. Before creating anything,
it walks that ancestor and its ancestors with no-follow component inspection; a `.git` entry
refuses the configured root. Only after that check does it create missing cache and `runs`
directories one component at a time, rejecting any existing symbolic-link or non-directory
component. It then opens the cache and `runs` directories as retained `SecureDirectoryStream`s
without following links and records their filesystem identities (`fileKey`); unavailable secure
streams or stable identities are hard unsupported-filesystem failures. Every later descendant
traversal is descriptor-relative from the retained `runs` stream, uses no-follow component
operations, and verifies both path identities against the retained identities before and after the
operation. Replacement, disappearance, or identity change aborts the operation; it never retries
through the replacement.

```
<cache>/packs/<packId>/<versionKey>/<archive>          + .sha512 + SOURCE.txt
<cache>/worlds/<mcVersion>/<seed>-<modSetSha>/
<cache>/runs/<runId>/manifest.manifest                  (canonical run manifest)
<cache>/runs/<runId>/…                                  (plan, images, diff reports)
<cache>/baselines/<packId>@<version>/<sceneId>/<captureKind>/<captureId>/<sampleOrdinal>.png
<cache>/oracle/<packId>@<version>/<sceneId>/SHOT/<shotId>/<sampleOrdinal>.png
```

`SOURCE.txt` is written next to every downloaded archive and records the pack's licence line, the
source URL and the never-redistribute rule — so a developer who finds the file in six months knows
what it is without finding this document.

`[D-P2-9]` **`FixtureResolver` refuses a cache root that lies inside a git work tree.** It walks the
nearest existing ancestor and its ancestors looking for a `.git` entry and fails with an
explanatory message before creating the requested root if it finds one. This is the structural half
of §G6's *"never re-host"* and of the doc gate's "fixture
licensing policy encoded structurally": the rule stops depending on anybody remembering it, and the
default root is outside the repo anyway. A `.gitignore` entry is *not* the mechanism — it is a belt
for the case where someone overrides the root deliberately.

#### 4.10.4 Never-rehost, structurally

Four independent mechanisms, because this is the project's one hard licensing constraint (§G7 item 6):

1. Packs are never in the repo, because the cache root cannot be in the repo (§4.10.3).
2. CI artifact uploads glob the **workspace** (`**/build/reports/…`, `**/build/libs/…`); the cache is
   outside the workspace and therefore cannot be uploaded by construction.
3. Goldens and Pintonium calibration records contain no pack source text, messages, or excerpts
   (`[D-P2-5]`, §§4.11.1, 4.11.7), so committed derived evidence carries only decisions, hashes,
   sanitized codes, and provenance.
4. Baseline and oracle images are not committed either (`[D-P2-6]`).

#### 4.10.5 Integrity and failure

| Case | Behaviour |
|---|---|
| Hash mismatch after download | hard failure naming both hashes; the file is deleted; **never** a silent retry or a "maybe the pin is stale" fallback |
| Registry pin empty | hard failure (§4.10.1) |
| Network unavailable, cache warm | proceed from cache; the manifest records `cached` |
| Network unavailable, cache cold | `SKIPPED(fixture-unavailable)` with the reason; never a pass |
| `-Dschmaloogium.conformance.offline=true` | no network attempt at all; cold cache is a skip |
| Archive present but not a readable pack (bad zip, no `shaders/` root) | hard failure; the fixture layer validates shape before any front-end sees it |

#### 4.10.6 CI cache key

`actions/cache` keyed on a hash of `packs.registry` (which contains every pin and every hash), with a
restore-key prefix so a registry edit that changes one pack does not discard the rest. Phase 1's stub
already anticipates "keyed on pack version IDs" (`PHASE_1_DOC.md` §4.11); hashing the registry file is
the same idea with one moving part instead of seven.

### 4.11 The headless-core golden harness

The heart of the before-renderer subset, and the place §8.3's *"this is where the `[D-6]` seam pays
rent"* is cashed.

#### 4.11.1 What a golden is, and what it must never contain

A **golden document** is a sorted, deterministic text record of what the front-end *decided* about a
pack under a given `GLCapabilityProfile`.

`[D-P2-5]` **A golden never contains pack source text.** Not a preprocessed shader, not a snippet,
not an error line quoted from a source file. Every one of the seven matrix packs forbids
redistribution (§3.1); a golden that embedded their GLSL would re-host it in our repository, and
goldens are committed. The format therefore records **shapes, decisions, names and hashes**: buffer
counts and formats, which directives were seen and with what values, which options exist and their
declared ranges, which macros the header will carry, per-file line counts and SHA-256 hashes, and
diagnostic *codes* with source *locations* rather than source *lines*.

This constraint is not a nuisance; it is what makes goldens committable, and committable goldens are
what make CI meaningful before a renderer exists.

#### 4.11.2 Document structure

```
# schmaloogium golden · kind=frontend · schema=1
input.pack        = chocapic13@v9
input.packSha512  = …
input.profile     = nvidia-gl46
engine.version    = 0.1.0-dev

[sources]
count             = 37
shaders/gbuffers_terrain.vsh = lines=412 sha256=…
…

[programs]
gbuffers_terrain  = SOURCED drawBuffers=0,1,2,3 alphaTest=default blend=default
gbuffers_water    = SOURCED drawBuffers=0,2
gbuffers_hand     = CHAIN(from=gbuffers_textured_lit)
…

[sizing]                        ← §4.11.3
[options]                       ← discovered switches, variables, const options, ranges
[properties]                    ← shaders.properties model: flags, profiles, screens, per-program state
[macros]                        ← the standard macro header this profile produces (names + values)
[diagnostics]                   ← code, severity, file, line — never the line's text
```

#### 4.11.3 The `[sizing]` section — §8.3's "resource-sizing decisions … with no GL context"

The section the spec names explicitly. It records the decisions §3.2's *implicit resource
declaration* rule produces from source scanning:

| Key | Contract row it reflects |
|---|---|
| `colorBuffers.count`, `colorBuffers.<n>.format` | §3.2 `colortexN` declaration raising the count; `colortexNFormat` (App A.3, App B.4) |
| `colorBuffers.1.format = RGBA32F` when `gdepth` is declared | §3.2's "declaring `gdepth` upgrades buffer 1's format" |
| `colorBuffers.<n>.clear`, `.clearColor` | App A.3's `colortexNClear` / `colortexNClearColor`; App B.1's default clear rules |
| `depthTextures.count` | §3.2's `depthtex0/1/2` allocation |
| `shadow.depthTextures`, `shadow.colorTextures`, `shadow.resolution`, `shadow.hardwareFiltering`, `shadow.mipmap`, `shadow.nearest` | App A.3's shadow directive block |
| `centerDepthSmooth.enabled` | §3.2's "declaring `centerDepthSmooth` enables the center-depth readback" |
| `noise.resolution` | `noiseTextureResolution` (default 256, App A.3) |
| `vertexAttributes` | which of `mc_Entity` / `mc_midTexCoord` / `at_tangent` each program opts into (§3.2) |
| `instances.<program>` | `countInstances` per slot (App A.3) |
| `capabilityGate` | `OK` or the shortfall, when the pack's needs exceed the profile's `maxDrawBuffers` / `maxColorAttachments` / `maxTextureImageUnits` |

The last row is why `RUN-CAPS-GATE` exists: with two synthetic profiles and no GL at all, the harness
proves that a pack needing 8 draw buffers on a 4-draw-buffer profile produces §G2.4 rung 4's graceful
refusal rather than an exception.

#### 4.11.4 Engine snapshot boundary and conformance adapter

Phase 3 owns an API and immutable data model in `:engine` that expose front-end decisions without
depending on the harness. Illustrative names are `FrontEndInspector` and `PackDecisionSnapshot`;
their final engine package and member layout belong to Phase 3. The directional contract is:

```java
// :engine; owns every type in this signature
public interface FrontEndInspector {
    PackDecisionSnapshot inspect(PackSource pack, GLCapabilityProfile profile);
}
```

`GoldenProjectionAdapter` lives in `com.schmaloogium.conformance.golden`, consumes that engine
snapshot, and maps it to `GoldenDocument`. The snapshot is deterministic for equal inputs, exposes
the Phase-3-owned decisions enumerated by §§4.11.2–4.11.3, and contains diagnostics as structured
coordinates, never pack source text. Phase 4 enriches that engine-owned model with the per-slot
resolution fields requested by R10; no `:engine` type implements or references a `:conformance`
type. A **complete** real golden is producible only after both Phase 3 and Phase 4 land. Before Phase
4, tests may project an explicitly synthetic/partial document, but no matrix golden may be approved
and no `RUN-GOLDEN-MATRIX` verdict may be issued.

Until Phase 3 lands, `GoldenWriter`, `GoldenComparer`, the format and every one of their tests are
exercised against a hand-built `GoldenDocument` — which is why the golden machinery is in the
week-one subset while the golden *content* is not.

#### 4.11.5 Update workflow

`./gradlew :conformance:test -PupdateGoldens` rewrites the committed goldens from the current
behaviour and **fails the build afterwards** if anything changed, so an update can never be an
accident of a normal run. The diff is then reviewed by a human like any other diff — which is the
entire point of a sorted, readable, source-text-free format. A golden update that a reviewer cannot
explain is a defect report, not a rebase.

#### 4.11.6 The hermetic corpus — packs we own

`[D-P2-11]` Matrix packs can never be committed, so the always-run CI tier needs a corpus that can
be. Two sources, both ours and both GPL-3.0-or-later:

- **The internal default pack** — RESEARCH.md §9 puts it in v0.1's scope, so it exists anyway.
- **Micro-packs**, in `conformance/src/test/resources/packs/`, each a handful of tiny files aimed at
  one contract mechanism:

| Micro-pack | Exercises |
|---|---|
| `mp-minimal` | one `gbuffers_terrain` pair only — the backup chain covering the whole terrain family (App A.2) |
| `mp-buffers` | `colortex0-7`, `gdepth`, formats, clear and clear-colour overrides (§3.2, App B.4) |
| `mp-shadow` | shadow depth/colour declarations and the whole App A.3 shadow directive block |
| `mp-includes` | nested `#include` at depth 10, one at depth 11 (must fail), `#line` bookkeeping (§3.2) |
| `mp-options` | `#define` switches, `#define` variables with ranges, `const` options, `sliders`, `screen.*`, profiles, `lang/*.lang` (§3.3, App F.3/F.4) |
| `mp-properties` | the `shaders.properties` surface: tri-state flags, per-program `alphaTest`/`blend`/`scale`/`flip`, custom textures, custom uniforms (App F) |
| `mp-dimension` | `world-1/` and `world1/` folders, including the empty-folder-disables case (§3.1) |
| `mp-broken` | a slot whose source fails to compile — the §G2.4 rung 3 path and §4.2.4's `CHAIN`-with-source detection |

These are test fixtures, not shader packs: they need not render anything beautiful, and several need
not render at all.

#### 4.11.7 Pintonium parse calibration — evidence, never contract

V3 uses Pintonium's front-end outcomes as a calibration target (PD §7), while §G0.1a and D-3 keep
the boundary absolute: **Pintonium never defines Schmaloogium conformance.** Schmaloogium must parse
all seven matrix packs end-to-end even when Pintonium fails one; a Pintonium success is useful
competitor evidence, and a Pintonium failure is a diagnostic comparison, not permission to fail.
No Pintonium mechanism is adopted here, so §G11.4's contract-visible adoption gate is not invoked.

The committed, source-text-free record for each exact matrix archive is
`conformance/calibration/pintonium/<packId>@<version>.calibration`, with canonical first line
`schema = schmaloogium.pintonium-parse-calibration/1`. It obeys §4.1 and contains exactly:

- `pack.{id,version,archiveName,archiveSha512}` copied from the populated registry and verified
  archive; the SHA-512 is the archive actually passed to Pintonium.
- `pintonium.{revision,outcome}` where revision is the full 40-lowercase-hex commit identity
  (initial calibration: `9c2fcc1a4814cafc0242370757e9e05ea83c5be3`) and outcome is exactly
  `SUCCESS|FAILURE`. Calibration capture requires a clean checkout at that revision. A crash/timeout
  is `FAILURE` plus its sanitized code, never a missing observation.
- `environment.{os,arch,jvm,minecraftVersion,loaderVersion,launchMode,modSetSha256,
  pintoniumConfigSha256}`. These make a result comparable without recording an absolute path or host
  identity.
- `diagnostics.count` plus dense `diagnostics.<n>.{code,severity,sourcePathSha256,line}` records.
  `code` is a stable
  calibration-owned category, `severity` is `INFO|WARN|ERROR|FATAL`, the path field hashes the
  normalized pack-relative path, and `line` is zero when unavailable. Messages, source excerpts,
  preprocessed text, absolute paths, usernames, and archive contents are forbidden.

The validator requires `archiveSha512` to be 128 lowercase hexadecimal digits; every SHA-256 field
to be 64; diagnostic ordinals to cover `0..diagnostics.count-1` without gaps; and every listed key
to be present. Unknown keys or any forbidden text fail the record rather than being ignored.

`RUN-PINTONIUM-PARSE-CALIBRATION` resolves all seven registry rows, requires one hash-matching
Pintonium observation for each, runs the same exact archives through Schmaloogium's front end for
all configured profile inputs, and emits a comparison table. Its pass condition is: all seven
observations are present and schema-valid; every record names the configured Pintonium revision and
exact archive; and Schmaloogium parses all seven with no `ERROR`/`FATAL`. Equality with Pintonium's
outcome is **reported but is not a pass condition**. Thus missing observations fail calibration,
Pintonium `FAILURE` + Schmaloogium success passes with a visible competitor delta, and any
Schmaloogium parse failure fails D-3 regardless of what Pintonium did.

Calibration capture is an explicit local maintenance operation against the read-only Pintonium
checkout; CI consumes the sanitized records and matrix archives but does not build, bundle, or
publish Pintonium. `[D-P2-5]` applies to these records exactly as it does to goldens.

### 4.12 The `GLCapabilityProfile` fixture set

Phase 1 owns the type, the text format and the capture mechanism
(`PHASE_1_DOC.md` §4.7.2, §8.3, `-Dschmaloogium.debug.dumpCapabilities`); **Phase 2 owns the set and
its refresh workflow**, and §5.2 of that document says so explicitly. The files live in `:engine`'s
`testFixtures` (`engine/src/testFixtures/resources/profiles/`) — Phase 1's placement, for the reason
its §8.3 gives: `:conformance` and `:mod` reach them through
`testImplementation testFixtures(project(':engine'))`, a dependency edge in the legal direction.

| Profile id | Origin | Why it is in the set |
|---|---|---|
| `minimum-gl21` | synthetic | the conservative profile `PHASE_1_DOC.md` §6 says `CapabilityProbe` produces when probing fails; `supportsMipmapGeneration()` is false here (the §4.1-of-RESEARCH.md GL 3.0 gate) |
| `baseline-gl30` | synthetic | the first profile where mipmap generation is guaranteed |
| `typical-gl33` | synthetic | GL 3.3 — sampler objects available (§6.2), the era most 1.12.2 players actually have |
| `nvidia-gl46` | captured | a modern discrete GPU |
| `amd-gl46` | captured | vendor-specific extension set and GLSL version string differences (`MC_GL_VENDOR_*`, §3.5) |
| `intel-igpu` | captured | the low-`maxTextureImageUnits`/low-`maxTextureSize` reality |
| `mesa-llvmpipe` | captured | the software renderer OQ-10's CI path would use (§10) — in the set whether or not the spike succeeds, because it is what a CI-shaped profile looks like |
| `hostile-4db` | synthetic | 4 draw buffers, 8 attachments, 16 texture units — the shortfall profile `RUN-CAPS-GATE` uses |

`profiles.index` — committed alongside — carries one row per profile: id, origin (`synthetic` or
`captured`), contributor, capture date, driver version, and whether any field was hand-edited.
A **hand-edited captured profile is a lie about hardware**, so the index makes it visible; synthetic
profiles are labelled as such and are allowed to be exactly what they need to be.

**Refresh workflow.** A contributor runs the client once with
`-Dschmaloogium.debug.dumpCapabilities`, drops the emitted file into the profiles directory, adds its
index row, and opens a change. Nothing is auto-refreshed: a profile changing under CI would silently
change what every golden asserts.

### 4.13 Reporting

One `ConformanceReport` per invocation, rendered three ways by `ReportRenderer`:

- **Markdown** — the human artifact and the source of `conformance/TIERS.md`. Pack × tier matrix at
  the top, per-scene/capture/sample detail below, a motion-path coverage matrix for all six
  families, and the seven-pack Pintonium/Schmaloogium parse comparison; every non-pass carries its reason.
- **JSON** — machine-readable, sorted, §4.1's rule; what a future dashboard or release script reads.
- **JUnit XML** — so a CI run surfaces per-pack, per-scene rows in the normal test UI rather than as
  a wall of log output.

Three reporting rules, each of which exists because its absence is a way for a harness to lie:

1. **Every outcome is one of `PASS` / `FAIL` / `SKIPPED(reason)` / `NO_BASELINE` / `NOT_ATTEMPTED`.**
   There is no "n/a" and nothing is omitted for being uninteresting.
2. **Every skip prints its reason and its remedy** — "fixture absent: place `<file>` at `<path>`,
   SHA-512 `<hash>`, from `<url>`".
3. **The summary line counts skips separately from passes.** "7 packs, 5 pass, 2 skipped" is the
   truth; "5/5 passing" is not.
4. **Temporal rows never collapse to a scene-level green.** Reports print `SHOT|PATH`, capture id,
   sample ordinal, the plan/manifest pose verdict, and the baseline verdict; one missing or failed
   bounded PATH sample keeps its family red.
5. **Reference calibration is labelled evidence-only.** The report distinguishes missing
   observation, Pintonium `SUCCESS|FAILURE`, and Schmaloogium parse outcome; it never presents a
   Pintonium match as conformance or a Pintonium failure as an exemption from D-3.

### 4.14 CI wiring

Phase 1 left two slots (`PHASE_1_DOC.md` §4.11): the named `./gradlew :conformance:test` step in
`build.yml`, and a `workflow_dispatch`-gated `conformance` job stub with an `actions/cache` step.
This phase fills both, and the fill is constrained by something Phase 1's step order makes true:

> `[D-P2-8]` **`:conformance:test` runs on every push, so it must be hermetic.** It is step 2 of
> `build.yml` (§4.11) and it also runs inside `./gradlew build` (step 3). A fixture download in that
> task would put the network — and seven third-party pack downloads — on every commit. The module
> therefore has **two** test tasks.

| Gradle task | Tag filter | Contains | Runs in |
|---|---|---|---|
| `test` (default) | excludes `fixtures` and `gl` | `SeamConformanceDependencyTest` (C-4, Phase 1's), the harness's own meta-tests (§8), `RUN-GOLDEN-CORE`, `RUN-CAPS-GATE` | `build.yml` steps 2 and 3, every push |
| `conformanceTest` | includes `fixtures` | `RUN-GOLDEN-MATRIX`, `RUN-PINTONIUM-PARSE-CALIBRATION`, `RUN-OPTIONS-ROUNDTRIP`, and any future fixture-dependent headless run | the `conformance` job only |

JUnit tags: `@Tag("fixtures")` for anything that resolves a matrix pack, `@Tag("gl")` for anything
needing a GL context. The default `test` task's exclusion is declarative, so a new fixture-dependent
test that forgets its tag fails in CI by trying to reach the network — loudly, on the first push,
which is the right time.

**The `gl` tag has a second, mandatory home, and missing it would break Phase 1's CI step.**
GL-context tests live in `:mod`'s test source set (`[D-P2-15]`, §10.2), and `PHASE_1_DOC.md` §4.11
step 1 runs **`./gradlew :engine:test :mod:test`** as CI's named "Seam architecture test" step — on a
headless `ubuntu-latest` runner. A GL test picked up by `:mod`'s default `test` task would therefore
fail that step for a reason that has nothing to do with the seam, and would do it on every push.
`:mod`'s `test` task must exclude `@Tag("gl")`, with the GL tests reachable only through a separate
`:mod:glTest` task (request R4, §5.4). This is stated here rather than only in §10.2 because it is a
property of the CI wiring, and CI wiring is where it will bite.

**The `conformance` job, filled:**

```yaml
conformance:
  if: github.event_name == 'workflow_dispatch'
  runs-on: ubuntu-latest
  steps:
    - checkout / setup-java 25 / setup-gradle 9.6.1        # unchanged from build.yml
    - name: Restore pack fixture cache
      uses: actions/cache@…
      with:
        path: ~/.cache/schmaloogium/conformance/packs
        key: packs-${{ hashFiles('conformance/fixtures/packs.registry') }}
        restore-keys: packs-
    - name: Conformance (headless, fixture-dependent)
      run: ./gradlew :conformance:conformanceTest
    - name: Upload conformance report
      if: always()
      uses: actions/upload-artifact@…
      with:
        path: |
          conformance/build/reports/conformance/**
          **/build/reports/tests/**
```

Three properties of that job, stated because each is a decision:

- The cache `path` is the **packs** subtree only. Baselines, oracles, worlds and run outputs are not
  cached and are not uploaded — they are either machine-specific or covered by §4.7.1's policy.
- The upload globs the **workspace**, and the cache lives outside it (§4.10.3), so no third-party
  pack can reach an artifact even by accident (§4.10.4 mechanism 2).
- `if: always()` on the upload: a conformance report is most valuable exactly when the job failed.

`release.yml` and `release-to-cf-mr.yml` need no Phase 2 change beyond the retargeting Phase 1
already specified (§4.11); nothing in this phase ships in the mod jar except the capture agent, which
is inert without its system property.

---

## 5. Cross-phase interfaces

### 5.1 What Phase 2 exposes

| Exposed | Detail | Consumed by |
|---|---|---|
| **The tier definitions** — T0–T3 with their decision procedures | §4.2. A phase claiming a tier claims *these* predicates; every recorded GL error fails T0 regardless of attribution. Ledger evidence uses §4.2.5's canonical index, manifest, and attestation paths beneath §4.10.3's established run root, with no-follow containment and digest validation | all behavioural phases |
| **The named-run catalogue** — `RunId`, `HarnessRun`, `RunRegistry` | §4.9. **This is the vocabulary every impl gate should cite**; §5.3 says how | all behavioural phases; whoever tags a milestone |
| **The `/2` scene format and initial scene set** | §4.3, §3.4. Stable scene/capture ids, static SHOTs, dense frame-indexed PATH samples, and at least one moving path in every one of the six motion-sensitive families | 7, 8, 9, 10, 13 |
| **The determinism ledger** | §4.4. A phase adding a new time- or randomness-dependent input must add a row, or it has silently broken every baseline | all |
| **The golden document format + adapter input requirements** | §4.11.1–§4.11.4. Phase 3 owns the `:engine` snapshot API, Phase 4 supplies its per-slot resolution enrichment, and complete real goldens wait for both; Phase 2 maps it in `:conformance` | **3**, **4**, 5 |
| **The `[sizing]` golden section** — the concrete list of resource-sizing decisions the headless harness validates | §4.11.3 | **3**, **4**, **5** |
| **The `GLCapabilityProfile` fixture set + `profiles.index`** | §4.12. Phase 1 owns the type and format; this is the *set* your "recorded-GL run" impl gates run against | **4**, **5**, **6**, 14 |
| **The run manifest wire schema** | §4.5.4, schema `schmaloogium.run-manifest/2`, canonically stored at `<cache>/runs/<runId>/manifest.manifest`, including complete `captures.*`, frame pose/history, `resources.*`, and `hooks.*` grammars. Every frame identifies `SHOT|PATH`, capture id and sample ordinal and carries planned/actual current/previous poses. Client capture admits only registry-backed packs; runner-owned provenance, world identity, T0/T3 evidence, GL attribution, and frozen hook reports retain their v1 guarantees. `/1` is historical and has no compatibility reader | **3** (front-end and pack configuration), **4** (per-slot program resolution), **5** (immutable live resource snapshot), **7** (capture, pose report, frozen hook report, and serialization using R4A) |
| **The capture-agent contract + capture-plan wire schema** — what `:mod` must implement and what Phase 7 must hook | §4.5, schema `schmaloogium.capture-plan/2`, §5.4 R11–R14 and R17–R19. The runner resolves all scene defaults before serialization; the plan writer, agent reader, and manifest reader perform no defaulting. Dense samples, bounded windows, and runner-owned pack provenance are immutable inputs. `/1` is historical and no compatibility reader is required | **7** |
| **Runner-owned pack-provenance bridge** | Acquisition mode and licence originate in the fixture registry; archive SHA-512 originates in post-resolution verification. They cross the client process only through the immutable capture plan and return only as verbatim manifest values. Pack content, scene text, agent rediscovery, and rendered behavior are not evidence for any of the three | **7**, CI/reporting |
| **The fixture registry, cache API and never-rehost rules** | §4.10 | anyone adding a pack; CI |
| **Tolerance profiles** | §4.6.3, calibrated by §4.6.5 | anyone reading a diff verdict |
| **The CI task split** — hermetic `test` vs fixture-dependent `conformanceTest`, and the tag policy | §4.14 | anyone adding a test to `:conformance` |
| **The micro-pack corpus** | §4.11.6 — eight tiny GPL packs, each aimed at one contract mechanism | **3**, 4, 5 (they are front-end test inputs as much as harness inputs) |
| **The Pintonium parse-calibration record and run** | §4.11.7. Seven exact archive identities, Pintonium revision/outcome, sanitized diagnostics, and environment; source-text-free and evidence-only. Missing observations fail calibration, while D-3 still requires Schmaloogium to parse every pack | **3**, CI/reporting |

### 5.2 What Phase 2 consumes from Phase 1

Every row cites the section of `PHASE_1_DOC.md` it comes from. Nothing here is assumed; §5.2 of that
document names Phase 2 explicitly for the first four.

| Consumed | From | Used by |
|---|---|---|
| Module layout, package placement rule, and the `:conformance` module with its `:engine` edge and JUnit wiring | §2.1, §4.2.4a | §2.3 |
| **Constraint C-4** (`:conformance` depends on `:engine`, never on `:mod`) | §4.3, §8.2 | §2.2 — the constraint that produces `[D-P2-1]` |
| `GLCapabilityProfile` — the record and its derivations | §4.7.2 | §4.11, §4.12 |
| **`GLCapabilityProfile`'s text serialization** (`parse`/`write`) | §4.7.2; §5.2 names this row "**2** (this is 'recorded `GLCapabilityProfile`s')" | §4.12, and the manifest's `gl` block (§4.5.4) |
| Fixture *placement*: `engine/src/testFixtures/resources/profiles/`, reached by `testImplementation testFixtures(project(':engine'))` | §8.3 | §4.12 |
| `RecordingGLDevice`, `GLCallLog` (incl. `bounded`/`droppedCallCount`), `GLCall`, `ScriptedResponses`, `ReplayAssertions` | §4.7.5 | the harness does not use these directly; it *depends on their format stability* for `recordGL` capture (§4.2.1) and hands them to Phases 4/5/6 as the mechanism their gates name |
| **`GLCallLog.render()`'s stability guarantee** — no timestamps, no identity hashes, no iteration-order dependence | §4.7.5 | §4.1's determinism rule generalises it; a `recordGL` log captured during a conformance run is an artifact of the run |
| `-Dschmaloogium.debug.recordGL` (per-call `glGetError` cadence, bounded ring) and `-Dschmaloogium.debug.dumpCapabilities` | §4.9.3; §5.3 names both for "**2** … the fixture and call-log capture path your harness drives" | `[D-P2-2]`, §4.12's refresh workflow |
| `CapabilityProbe` as the fixture-production mechanism | §4.7.5, §5.2's note to Phase 2 (*"Do not design a capture path; drive these"*) | §4.12 — and this document does not design one |
| The `schmaloogium.conformance` log channel | §4.9.2 (owner column: 2) | §4.5.6 |
| `ReplayAwareGLError(GLError, attributed)` | §2.4/§4.7.4/§5.2, `[D-P1-42]` | the manifest's total `gl_errors.*.attributed` boolean; Phase 2 copies the producer result and never infers from `op` or `subjectLabel` |
| CI job/step layout and the `conformance` extension point | §4.11; §5.3's last row names Phase 2 as its consumer | §4.14 |
| The version pin table and its re-pin procedure | §4.2.6 | §4.10's refusal to add a dependency coordinate without one |

Phase 1's four-field `GLError` alone does **not** supply the manifest's `attributed` boolean. Phase 1
has accepted and exposed the R4A grant as `[D-P1-42]`; its `ReplayAwareGLError` is consumable and
is the sole admissible source. Phase 6 performs the replay and Phase 7 copies the result. `op` and
`subjectLabel` remain non-evidence.

**What Phase 1 explicitly does not give this phase**, and this document therefore supplies: the
fixture set, any golden-file format other than `GLCallLog.render()`, and any answer to OQ-10
(`PHASE_1_DOC.md` §5.2's closing note, §11.4's "To Phase 2" paragraph). All three are §4.12, §4.11
and §10 respectively.

### 5.3 How a phase cites its impl gate

`DESIGN.md` §G6's third testability slice makes behavioural phases' impl gates "§9-derived impl gates
expressed as Phase-2-defined harness runs". The mechanism:

- A gate that names a **tier and a pack class** resolves through §3.5's milestone table to a run id.
  §G5.1 gives each phase its milestone, so the chain phase → milestone → exit criterion → run is
  complete without any phase reading another phase's spec.
- A gate that says **"a recorded-GL run"** — which `PHASE_1_DOC.md` §5.2 notes is the wording of
  Phases 4, 5 and 6's gates — is Phase 1's mechanism (`RecordingGLDevice` + `ReplayAssertions`), run
  against **this phase's profile fixture set** (§4.12). Phase 2 supplies the profiles and nothing
  else; the assertions are the phase's own.
- A gate needing a run that §4.9 does not contain is a **request against this document**, made in the
  requesting phase's §5 and answered by a §G1.3 fix-up here — not invented locally, and not silently
  substituted with a different run.

**This phase's own impl gate**, from the spec: *"fixture downloader + headless golden-run skeleton +
scene-spec parser implemented and green in CI before Phase 7's implementation lands (D-10)"*. §9.2's
week-one subset is scoped to satisfy it with room to spare, and §12's ordering front-loads exactly
those three.

### 5.4 Requests — flagged, never assumed

Per §G1.1, what this phase needs and does not own is stated as a request. Accepted requests are
marked fulfilled and consumed only from the owning document's binding §5; the rest are not treated
as existing.

**To Phase 1** (a dependency; these are requested changes to `PHASE_1_DOC.md`):

| # | Request | Why |
|---|---|---|
| R1 — **accepted and consumable from Phase 1 v14 `[D-P1-41]`** | A `com.schmaloogium.mod.conformance` package in §2.1's `:mod` table, owner "Phase 2 (design) / Phase 7 (hooks)" | §5.1 makes package placement a rule; the new exact slot gives the capture agent a legal home without weakening C-4 (`[D-P2-1]`) |
| R2 | Two client-read system properties admitted to the `-Dschmaloogium.*` namespace: `schmaloogium.conformance.plan` and `schmaloogium.conformance.out` | §4.9.3 fixes the debug-flag namespace and its table has an owner column; §4.9.2 already establishes the "a later phase adds via requested change" mechanism for channels. These two are read by `:mod`, so they are Phase 1's namespace; `cacheDir` and `offline` are read only by `:conformance` and are ours |
| R3 | Acknowledgement that `conformance/build.gradle` gains a second test task and JUnit tag configuration (§4.14) | §4.2.4a says "Phase 1 stands the module up; Phase 2 fills it", which most likely already covers this; flagged because the file is Phase 1's artifact and the change is additive rather than internal |
| R4 | In `mod/build.gradle`: the default `test` task **excludes `@Tag("gl")`**, and a separate `glTest` task (opt-in, `-PglTests`) includes it | Not cosmetic. `PHASE_1_DOC.md` §4.11 step 1 runs `:mod:test` as CI's named "Seam architecture test" step on a headless runner; a GL test inside that task fails the seam step for a non-seam reason on every push (§4.14, §10.2). `:mod/build.gradle` is Phase 1's file, so the change is requested rather than assumed |
| R4A — **accepted and consumable from Phase 1 v14 `[D-P1-42]`** | An additive replay-aware GL-error result carrying `GLError error` and `boolean attributed`: `true` only when replay reproduces and isolates the error to the named facade operation; `false` when replay is clean or the window remains batched/foreign. The producer returns one result for every drained error, covering single-call, batched, replay-clean, and foreign-error windows without changing `GLError` | The run manifest requires a total boolean for every record, while the four-field `GLError` cannot supply it (§4.5.4). This classification comes from the owner of the drain/replay protocol, never from Phase 7 guessing from `op` or `subjectLabel` |
| R4B | Grant Phase 2 consumption of `EngineDiagnostic`, `DiagnosticSeverity`, and `UserChannel` | Phase 1 v14 grants these types to selected phases but not Phase 2. Until this request is accepted in Phase 1's binding §5, Phase 2 does not consume those domain types or treat them as the implementation source of the Phase-2-owned `diagnostics{code,severity,channel,file,line}` wire records |

No facade verb is requested. R4A is an additive diagnostic result from the existing drain/replay
protocol; §4.7.4's absent-verbs table is not touched.

**To Phase 3** (a Wave-1 sibling — these are requests against its future §5, not consumptions):

| # | Request |
|---|---|
| R5 | An `:engine`-owned API and immutable snapshot model (§4.11.4) exposing Phase 3's decisions in §§4.11.2–4.11.3 deterministically, with **no pack source text** and no reference to `:conformance`; Phase 2's `GoldenProjectionAdapter` performs the `GoldenDocument` mapping, and complete real goldens await R10's Phase 4 enrichment |
| R6 | A `PackSource` that reads a folder **or a zip** with no Minecraft resource manager involved — the harness feeds it a downloaded archive from a cache directory |
| R7 | A stable pack-identity hash over the pack's own files, so a golden's `input.packSha512` and a manifest's pack block agree |
| R8 | The parsed engine constants — `wetnessHalflife`, `drynessHalflife`, `eyeBrightnessHalflife`, `centerDepthHalflife` (§3.2, App A.3) — readable from the snapshot, because §4.4's warm-up rule computes from them |
| R9 | Diagnostics exposed as **code + severity + file + line**, not as quoted source lines, so `[D-P2-5]` holds for the `[diagnostics]` golden section |

**To Phase 4:** R10 — per program slot, the resolution status `SOURCED` / `CHAIN(from=<slot>)` /
`ABSENT` / `FAILED(driverLog)` **paired with whether the pack shipped a source file for that slot**,
queryable at runtime (for §4.5.4's manifest) and present in the engine snapshot (for goldens). §4.2.4's
clause-2 assertion is not implementable without the pair.

**To Phase 5:** R10A — expose to Phase 7 an immutable runtime snapshot containing every canonical
`resources.*` value in §4.5.4, including the capability gate and shortfalls, with the same
availability and absence rules. Phase 5 owns these live sizing decisions; Phase 7 only captures and
serializes the snapshot.

**To Phase 7:**

| # | Request |
|---|---|
| R11 | A frame-end hook after the engine's `final` pass has written the vanilla framebuffer and before it is presented, at which the capture agent may grab one frame (§4.5.3) |
| R12 | A readiness signal — "engine active, pack loaded, N frames rendered without error" — so the agent can distinguish "warming up" from "wedged" |
| R13 | *Conditional:* a fixed-`partialTicks` override, **only if** §4.4's residual risk on animated textures materialises in practice. Not requested now; recorded so it is a known additive route rather than a surprise |
| R14 | A clean programmatic shutdown after the last capture sample, so a capture run terminates without a timeout kill |
| R17 | Capture and serialize every accepted R4A replay-aware result, copying its boolean verbatim to `gl_errors.<n>.attributed`; preserve single-call, batched, replay-clean, and foreign-error records, and never derive attribution from `GLError.op` or `subjectLabel` |
| R18 | Capture and serialize the complete frozen `HookApplicationReport` defined by Phase 7: preserve every primary catalog ID/target/order/count/class/deferred-owner/fallback and every nested owner-phase/fingerprint/enabled/row field exactly. Include Phase 8's eight-row nested report when present; represent absence as absence; never infer hook health or capability from runtime behavior |
| R19 — **required `/1` → `/2` migration** | Replace Phase 7's historical `schmaloogium.capture-plan/1` / `schmaloogium.run-manifest/1` consumer-producer contract with the `/2` contracts in §§4.5.2–4.5.4. Reject `/1`; execute one rendered frame per dense sample after first-pose warm-up; expose an immutable frame-end pose report containing actual current and previous camera poses; and serialize `SHOT|PATH`, capture id, ordinal, counts, windows, poses, and image keys exactly. Phase 7 must be migrated and freshly verified before its capture implementation consumes Phase 2 v2 |

**To Phase 12:** R15 — programmatic get/set of pack options and engine options, for the scene format's
`[pack] option.*` and `engine.*` blocks, plus a validation hook so §4.3.3 can reject an unknown
`engine.*` key instead of collecting it as unvalidated. R16 — the persistence semantics
`RUN-OPTIONS-ROUNDTRIP` asserts: a round-trip preserves the model, and **only changed options
persist** (§4.7 of RESEARCH.md).

---

## 6. Failure modes & degradation

§G2.4's **six semantic rungs** are numbered `1`, `2`, `2a`, `3`, `4`, `5`; `2a` is the v3
feature-level rung inserted without renumbering the historical ladder. The engine owns each remedy.
Phase 2 owns faithful evidence and never converts a degraded run into green. Its one direct engine-
adjacent behavior is the shipped-but-inert capture agent at rung 5.

### 6.1 The ladder, where this phase touches it

| Failure | Rung | Behaviour |
|---|---|---|
| A custom uniform errors at runtime | **1** | Engine disables that custom uniform only; harness preserves diagnostic and GL evidence and fails any applicable T0 run rather than inferring recovery from the image |
| A built-in uniform upload errors | **2** | Engine disables that built-in uniform only through the replay-aware protocol; manifest copies every R4A result verbatim |
| A pack feature's GL call fails, but neither one uniform nor the whole program failed | **2a** | Owning phase disables only that feature. Manifest/report preserve the feature diagnostic; Phase 2 never relabels it as rung 2 or 3 and never treats continued rendering as a pass |
| A pack program fails compile/link/validate | **3** | Engine deletes it and resolves through the backup chain. Harness records the per-slot result and driver log; T0/T3 decide from it. `mp-broken` keeps the path permanent |
| A capability gate fails at init | **4** | Pack turns off gracefully with a chat error. `RUN-CAPS-GATE` asserts this headlessly against `minimum-gl21` and `hostile-4db` |
| The capture agent or any shader-engine boundary would otherwise crash/corrupt vanilla | **5** | Capture failures are caught, agent disarms, state is restored, and the client remains usable; runner emits a failed `/2` manifest. Shaders-off remains reachable |

### 6.2 The harness's own failures — not rungs, and never silently green

Two invariants govern the whole table: **the harness never turns a red into a green**, and **no tier
is claimed without evidence** (§4.2.5).

| Failure | Behaviour |
|---|---|
| Matrix fixture absent (`MANUAL` mode, nothing in cache) | `SKIPPED(fixture-absent)` **with the remedy printed** — URL, filename, SHA-512, target path (§4.13 rule 2). Counted as a skip, never folded into a pass count |
| Network unavailable, cache cold | `SKIPPED(fixture-unavailable)`; warm cache proceeds and the manifest records `cached` |
| Downloaded archive's SHA-512 ≠ the pin | **Hard failure.** The file is deleted. No retry, no "the pin must be stale" fallback — a changed artifact under a fixed pin is exactly the event integrity checking exists for |
| Registry pin empty | Hard failure (`[D-P2-20]`); never "resolve latest" |
| Baseline absent for a T1 capture sample | `NO_BASELINE` — a distinct outcome, never a pass, never auto-approved, and never auto-approved in CI under any flag (§4.7.3) |
| Oracle absent for a T2 static shot | `SKIPPED(oracle-absent)` with §4.8's procedure referenced |
| Candidate and baseline dimensions differ | Hard failure, not a large diff (§4.6.1) |
| Client process times out or exits non-zero | Run `FAILED`; the partial manifest, the client log and any images already written are preserved in the run directory. A timeout is never a skip |
| Hook report missing, mutable, malformed, filtered, or unavailable | The agent cannot publish a complete manifest; runner fallback writes `hooks.available=false`, omits all other `hooks.*` keys, and T0 fails. No observed rendering is used to reconstruct the report |
| Repeated SHOT samples or same-ordinal repeated PATH runs are not `IDENTICAL` | Run `FAILED` with a determinism-leak message pointing at §4.4's ledger. Consecutive samples within a moving path are expected to differ and are never compared to one another as a self-check |
| `/2` capture count, ordinal, window, or planned/actual current/previous pose mismatch | Run `FAILED` before image diff; a rendered image cannot repair non-reconstructible temporal execution |
| Any of the seven Pintonium observation records is missing, stale, wrong-revision, wrong-archive, malformed, or contains forbidden source/message text | `RUN-PINTONIUM-PARSE-CALIBRATION` fails with the pack and field named. A Pintonium `FAILURE` observation itself is valid evidence; a Schmaloogium parse failure still fails D-3 |
| Golden mismatch | Test failure showing the unified diff of two sorted text documents. Never auto-updated: `-PupdateGoldens` is explicit and fails the build after writing (§4.11.5) |
| Scene file invalid | Refused at parse with the rule that was violated; the run does not start (§4.3.3) |
| OQ-10 fails — no usable GL context on CI runners | The pre-designed fallback in §10.3(4), which costs no tier and no coverage in CI because CI never ran an image tier |
| Differ heap pressure on large images | Bounded concurrency (§7), and the differ streams the raster rather than holding decoded copies of every capture sample at once |
| Cache root inside a git work tree | Refused before any download (`[D-P2-9]`) |

---

## 7. Threading & performance notes

**Thread ownership.**

| Component | Thread |
|---|---|
| Everything in `:conformance` | No thread affinity by construction, and no thread is started except by the test runner and `CaptureRunner`'s process management. Nothing here touches GL, by C-4 |
| `CaptureRunner` | The test thread; it blocks on an external process with a timeout |
| `CaptureAgent` lifecycle (plan read, scene application, manifest write) | Client thread, at defined lifecycle points |
| `FrameGrabber` | **Render thread only** — `ScreenShotHelper.createScreenshot` performs a GL readback, and §G2.3 gives the render thread all GL. The grab happens inside Phase 7's frame-end hook (R11), which is on that thread by construction |
| `FixtureResolver` downloads | Any thread, but **serialised** (§4.10.2): no parallel fetches against a third-party API |

**Concurrency policy.** Headless golden runs and image diffs are embarrassingly parallel per
`(pack, scene)` and JUnit parallel execution is enabled for them. Two exclusions: anything that
writes the fixture cache (serialised by a lock file plus download-to-`.part`-then-atomic-rename, so
two Gradle workers cannot half-write the same archive), and anything that launches a client —
`CaptureRunner` allows **one client per machine at a time**, because two clients contending for one
GPU produce timing that §4.4 cannot suppress.

**Allocation posture** (§G2.5: clean code first, optimise with evidence). One place deserves care:
the differ. A 1920×1080 frame is ~8 MB as an `int[]` raster and a diff holds three (expected, actual,
differing-mask), so per-comparison concurrency is bounded by **heap, not by core count** — the differ
works on `int[]` rasters obtained once per image, never on `BufferedImage.getRGB` per pixel, and
never boxes. Everything else in this phase is I/O-bound or runs once per invocation.

**Explicitly not a hot path, and explicitly not a performance gate.** Nothing here runs per frame in
a shipped game except the capture agent, which is off unless its system property is set. Frame
durations *are* recorded in the manifest — as a hang detector and as diagnostic context — and are
**never** a pass condition: performance benchmarking is Phase 14's, and the spec puts it in this
phase's *Scope — out*.

---

## 8. Testability plan

### 8.1 The harness's own tests

All in `:conformance` unless noted; all in the hermetic `test` task (§4.14) unless tagged.

| Test | Asserts |
|---|---|
| `SceneParserTest` | every `/2` field in §4.3.2 parses to the right type; SHOT and PATH blocks retain authored order; dense samples, comments, and blank lines behave exactly as specified |
| `SceneParserRejectionTest` | each §4.3.3 rule fails with a message naming it — wrong major, unknown/missing key, id mismatch, duplicate capture, missing pose, non-dense/out-of-bound samples, short/escaping window, no non-zero captured delta, warm-up below floor, missing gamerule |
| `SceneRoundTripTest` | parse → write → parse is identity, and the written form is sorted and byte-stable (§4.1) |
| `SceneCorpusTest` | every committed scene parses/validates and all six §3.4 ids contain at least one valid moving PATH; a SHOT-only replacement fails the corpus |
| `CapturePlanTest` | `CaptureRunner` resolves all scene defaults and expands SHOT samples before constructing `/2`; `CapturePlanWriter` and `CapturePlanReader` reject an incomplete/default-dependent value and perform no defaulting; dense capture/sample fields and runner-owned provenance round-trip byte-identically; `/1`, gaps, malformed/unknown fields, and bad provenance abort; any byte changes the plan hash |
| `MotionPathSchedulerTest` | first pose receives the exact warm-up; every dense ordinal causes exactly one pose application and one rendered frame; only the bounded window captures; no interpolation or clock read occurs; planned previous pose is first pose for ordinal 0 and prior sample thereafter |
| `ImageDifferTest` | identical images → zero differing pixels; a single altered pixel is found at the right coordinate; a diffuse ±1 noise field passes `SAME_MACHINE` and fails `IDENTICAL`; a 40×40 solid block **fails** `SAME_MACHINE` on L3 while passing L2 — the case `[D-P2-7]` exists for; dimension mismatch throws rather than scoring |
| `ClusterAnalysisTest` | 4-connectivity, largest-area and count on hand-built masks, including a diagonal chain that must *not* merge |
| `TolerancePolicyTest` | profiles load from the committed file; an unknown profile name fails; `ADVISORY` produces a report and refuses to yield a verdict |
| `IgnoreMaskTest` | rectangles exclude the right pixels; masked fraction is reported; a mask without a `reason` is rejected |
| `FixtureResolverTest` | with a stubbed `HttpTransport`: cache hit, cache miss then download, hash mismatch → delete + hard fail, empty pin → fail, offline + cold → skip, offline + warm → proceed, `MANUAL` mode never touches the transport |
| `FixtureCacheRootTest` | the no-follow `.git`-ancestor walk refuses an in-repo root (`[D-P2-9]`), including when the requested root is missing and several levels below the work tree, **leaves that requested path absent**, and accepts a root outside it; relative roots resolve against the working directory; linked cache/`runs` components, unavailable secure streams or identities, and replacement of either retained root before or during traversal abort |
| `RegistryTest` | every App G row is present with a licence line and a source URL; `MODRINTH` entries carry a project and a version id; `MANUAL` entries carry no fetch path |
| `GoldenWriterDeterminismTest` | byte-identical output across repeated runs, across `Locale.ROOT` vs a comma-decimal locale, across two timezones, and across two different input iteration orders |
| `GoldenComparerTest` | equal documents pass; a single changed value produces a diff naming section, key, expected and actual |
| `GoldenCorpusTest` | **every committed golden parses into the document model and re-renders byte-identically.** This is the structural proof of `[D-P2-5]`: a golden that contained free source text could not round-trip through a model that has no field to hold it |
| `RunManifestReaderTest` | canonical `run-manifest-v2.manifest` round-trips with SHOT/PATH captures, dense ordinals, captured/image bijection, planned/actual current+previous poses, T0 state, hashes, GL errors, hooks, and Phase 8 subreport; `/1`, gaps, pose omissions, bad windows/keys/orders/types, and unsupported versions abort; `x.<producer>.*` is preserved-and-reported |
| `TierEvaluatorTest` | reconstructs each T0 failure independently, including capture/sample count, ordinal, image-bijection, and current/previous pose mismatches; derives unattributable counts solely from `attributed=false`; covers per-sample T1 `NO_BASELINE`, T2 dual-spec refusal, and T3 sourced/unsourced `CHAIN` without capability inference |
| `RunManifestFailureTest` | launch failure, timeout, crash, truncated output, missing hook/pose report, missing provenance, every plan-provenance mismatch, and every `/2` temporal mismatch produce a canonical runner-synthesized failure manifest with authoritative plan facts and deterministically fail T0; pack-authored provenance is ignored |
| `HookManifestEvidenceTest` | serialization is a field-for-field copy of a frozen Phase 7 report; Phase 7 primary IDs/order never change; an owner-phase-8 subreport preserves exactly eight IDs/counts/dispositions and its fingerprint/enabled bit; absence stays absent; successful frames and images cannot synthesize health |
| `BaselineIdentityTest` | world and mod-set hashes are invariant to traversal/record order, change with any input byte or identity, flow unchanged into approval manifests, and a mismatch yields `NO_BASELINE`; world copy/hash rejects an internal link, an escaping link, FIFO, socket, and representative device entry without following it |
| `TierLedgerTest` | scene-set identity and exact captured-sample coverage, including every bounded PATH ordinal; missing/hash-mismatched/stale evidence → `NOT_ATTEMPTED`; contained path/digest/attestation rules remain enforced; inconsistent ledgers are flagged and renderings remain stable |
| `PintoniumParseCalibrationTest` | exactly seven source-text-free records; exact registry archive identity and configured Pintonium revision/environment; missing/stale/malformed records fail; forbidden messages/source/absolute paths fail; Pintonium success/failure deltas report, while any Schmaloogium parse failure fails D-3 |
| `ReportRendererTest` | snapshot of all three renderings; skips separate from passes; PATH rows never collapse by scene; all seven Pintonium/Schmaloogium comparisons are labelled evidence-only; every non-pass carries a reason |
| `HarnessRunRegistryTest` | every §4.9 id resolves; every §3.5 exit criterion maps to a run; the v3 motion and parse-calibration gates map specifically to `RUN-MOTION-PATHS` and `RUN-PINTONIUM-PARSE-CALIBRATION` |

Already owned by Phase 1 and running in this task: `SeamConformanceDependencyTest` (C-4,
`PHASE_1_DOC.md` §8.1) and `:conformance`'s placeholder test, which §12 replaces with real content.

### 8.2 Fixtures this phase's own tests use

Synthetic and tiny, all committed: hand-built `GoldenDocument`s; hand-built `RunManifest`s including
a malformed one; small PNGs generated in-test (never checked in as binaries); a stub `HttpTransport`
returning canned Modrinth JSON and byte streams; the eight micro-packs (§4.11.6), which are test
inputs for the front-end phases too. No matrix pack is needed by any test in the hermetic task —
which is what makes `[D-P2-8]`'s split possible.

### 8.3 Which conformance-tier runs exercise this subsystem

**None, and the answer is structural rather than an omission.** This phase *is* the tier machinery;
a tier run exercising it would be the harness grading itself. The equivalent assurance is §8.1's
meta-tests plus two properties that make the harness's own correctness observable in use:
repeated SHOT plus same-ordinal repeated PATH self-checking (§4.9's `RUN-SCENE-SELFCHECK`) and the ledger's inconsistency
flagging (§4.2.5).

---

## 9. Milestone staging

Per §G4.3, every designed component carries exactly one tag meaning "implemented at that milestone".
The distribution is unusual and deliberate: `D-10` puts the harness in week one, so almost everything
here is `v0.1` even though the behaviour it will eventually measure is not.

### 9.1 Component → milestone

| Component | Tag | Note |
|---|---|---|
| `/2` scene format, parser, validator, dense PATH scheduler (§4.3) | `v0.1` | impl-gate item; all six families carry motion |
| Determinism ledger as enforced defaults + validation (§4.4) | `v0.1` | |
| Scene `terrain-day`, `water-translucent`, `hand-item` plus their required moving paths | `v0.1` | authored and gated at v0.1 |
| Scene `night-shadows` + `shadow-parallax` | authored `v0.1`, shadow result first gated `v0.2` | motion/schema gate exists at v0.1; shadow rendering arrives at v0.2 |
| Scene `entities-blocks` + `entity-orbit` | authored `v0.1`, separately reported with the `v0.3` Phase 9/10 implementation gates | per-entity/TE id uniforms are v0.3; not part of the terrain-scene exit criterion |
| Scene `weather-rain` + `rain-track` | authored `v0.1`, weather result first gated `v0.5` | motion/schema gate exists at v0.1; depthtex2 behavior arrives at v0.5 |
| Capture plan `/2` + default-free writer/reader (§4.5.2), including dense samples and runner-owned pack provenance | `v0.1` | Runner alone resolves scene defaults before serialization |
| `CaptureAgent`, `SceneApplier`, `FrameGrabber`, `RunManifestWriter` (§4.5) | `v0.1` | designed now, runnable the moment v0.1 renders — the spec's own phrasing |
| Run manifest `/2` + reader (§4.5.4), including frame pose history and complete hook evidence | `v0.1` | T0/T3 and motion reconstruction decide from it; `/1` is unsupported history |
| World generation cache (§4.5.5) | `v0.1` | |
| Image differ, tolerance profiles, cluster analysis, ignore masks (§4.6) | `v0.1` | testable against synthetic images with no renderer |
| Tolerance calibration (§4.6.5) | `v0.1` | first real captures replace the starting numbers |
| Baseline storage, manifests, approval workflow (§4.7) | `v0.1` | |
| T0 evaluator (§4.2.1) | `v0.1` | |
| T1 evaluator (§4.2.2) | `v0.1` | |
| T2 protocol, oracle manifest tool, T2 evaluator (§4.8, §4.2.3) | `v0.2` | §9's v0.2 exit criterion is "first T2 runs" |
| T3 evaluator + per-pack `.features` files (§4.2.4) | `v0.4` | §9's v0.4 row is the first T3 |
| Tier ledger + `TIERS.md` (§4.2.5) | `v0.1` | |
| Fixture registry, resolver, cache, integrity, in-repo refusal (§4.10) | `v0.1` | impl-gate item |
| Modrinth transport (§4.10.2) | `v0.1` | |
| Manual-mode instructions and skip reporting (§4.10.2, §4.13) | `v0.1` | |
| Golden document model, writer, comparer, update workflow (§4.11) | `v0.1` | impl-gate item |
| Engine snapshot **consumption through `GoldenProjectionAdapter`** | `v0.1`, when Phase 3 lands | the adapter cannot compile until the engine API and model exist |
| Micro-pack corpus (§4.11.6) | `v0.1` | |
| `GLCapabilityProfile` fixture set — synthetic profiles + `profiles.index` (§4.12) | `v0.1` | |
| Captured hardware profiles | `v0.1`, ongoing | contributed as machines become available |
| `RUN-GOLDEN-CORE`, `RUN-CAPS-GATE` | `v0.1` | the two CI-green-before-a-renderer runs |
| `RUN-GOLDEN-MATRIX` | `v0.1` | |
| Pintonium observation format + `RUN-PINTONIUM-PARSE-CALIBRATION` | `v0.1` | source-text-free seven-pack calibration; never a conformance oracle |
| `RUN-SCENE-SELFCHECK`, `RUN-MOTION-PATHS`, `RUN-T0`, `RUN-T1-APPROVE`, `RUN-T1-REGRESS` | `v0.1` | |
| `RUN-OPTIONS-ROUNDTRIP` | `v0.4` | §9's v0.4 exit criterion |
| Report renderer, three formats (§4.13) | `v0.1` | |
| CI task split + filled `conformance` job (§4.14) | `v0.1` | |
| `RUN-GL-SMOKE` + the OQ-10 spike (§10) | `v0.1` | the spike is week-one work: its answer changes what CI can do |
| Dual-spec `RUN-T0` / `RUN-T1-REGRESS` | `v0.1` | Run definitions implemented with the other T0/T1 runs; execute at every v0.1–v0.5 release gate provisionally under Appendix G, pending §11.3 item 10 |
| Dual-spec modern-pass families | `post-v0.5` | §G8's S1–S4; §9's post-v0.5 row |

### 9.2 The "runnable before any renderer exists" subset — the doc-gate list

This is what the implementation effort builds in **literal week one** (§G6's second slice), and every
item is buildable and testable with no GL context, no Minecraft, and no renderer:

1. **Fixture downloader** — registry, resolver, Modrinth transport, cache layout, integrity checks,
   the in-repo cache refusal, manual-mode instructions. Tests: `FixtureResolverTest`,
   `FixtureCacheRootTest`, `RegistryTest`.
2. **Scene-spec parser, validator, and PATH scheduler** — plus all six authored scene files, each
   with a moving path, and `SceneCorpusTest`/`MotionPathSchedulerTest`.
3. **Golden-run skeleton** — document model, writer, comparer, and update workflow over hand-built
   `GoldenDocument`s. Tests: `GoldenWriterDeterminismTest`,
   `GoldenComparerTest`, `GoldenCorpusTest`.
4. **Image differ** — all three levels, tolerance profiles, masks, report writer. Testable entirely
   against synthetic image pairs generated in-test.
5. **Tier model, ledger and report renderer** — testable against hand-built manifests.
6. **The `GLCapabilityProfile` fixture set** (synthetic profiles at minimum) and `profiles.index`.
7. **The micro-pack corpus** — eight tiny packs, which are also Phase 3's first test inputs.
8. **CI wiring** — the hermetic/gated task split and the filled `conformance` job.
9. **The OQ-10 spike** (§10) — itself a CI experiment needing nothing from the engine.
10. **Pintonium calibration-record validation and comparison renderer** — capture of the seven
    observations is an explicit local maintenance step, but schema validation and D-3 comparison
    are headless and need no Schmaloogium renderer.

Items 1–3 are exactly this phase's impl gate. **Not** in the subset, and why: the capture agent
(needs a client), every image tier (needs frames), baseline approval (needs images), and the
tolerance calibration (needs two real captures).

---

## 10. OQ & spike specifications

### 10.1 What the RenderBook JUnit OpenGL Extension actually is

RESEARCH.md §5.1 and §8.3 record that the extension *exists*; this section records what it is,
because the spike's design depends on it. `[V:web github.com/tttsaurus/Mc122RenderBook,
articles/unit_test/junit_gl_extension.md, 2026-07-25]`

| Property | Finding |
|---|---|
| Form | An article plus source in an educational repository — **not published to any Maven repository**. It is a pattern to port, not a coordinate to depend on |
| Class | `GLTestExtension`, used as `@ExtendWith(GLTestExtension.class)` |
| JUnit | Jupiter — `BeforeAllCallback` / `AfterAllCallback`. Compatible in shape with the pinned JUnit 6 (`PHASE_1_DOC.md` §4.2.4a), whose extension API is the same `org.junit.jupiter.api.extension` surface |
| Context creation | **GLFW3 via LWJGL3**: a hidden 1×1 window (`glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)`), `glfwMakeContextCurrent`, `GL.createCapabilities()` |
| Threading | A dedicated daemon "GL Test Thread" through an `ExecutorService`, a global `ReentrantLock` serialising context lifecycle across test classes, work submitted as `CompletableFuture` and joined |
| Test shape | `GLTestExtension.submit(() -> { … }).join()`, with `assumeGL46()`-style assumptions for version gating |
| Headless / EGL | **No explicit handling.** It relies on GLFW's default platform behaviour — which is precisely why OQ-10 is open |
| Licence | MIT (§10.3) |

Two consequences for the port:

- **The pattern is right and the profile hint is ours to add.** The extension does not hint an OpenGL
  profile, so it inherits GLFW's default. `D-9` requires a **compatibility** context, so our port
  must either request `GLFW_OPENGL_ANY_PROFILE` explicitly or verify what the default gives, and the
  spike must *measure* the profile rather than assume it (§10.3's criterion S1).
- **Attribution, not dependency.** §G7 item 5 makes RenderBook's test-harness patterns freely usable
  (MIT). If code is copied rather than merely learned from, `PHASE_1_DOC.md` §4.8.3's
  `THIRD-PARTY.md` mechanism gets an entry with the notice preserved. No pin is added to §4.2.6's
  table because there is no artifact to pin.

### 10.2 Where GL-context tests can live

`[D-P2-15]` **In `:mod`'s test source set, at `mod/src/test/java/com/schmaloogium/mod/glcontext/`,
tagged `@Tag("gl")`.** The reasoning is forced, and it is worth writing down because "put the GL test
in the conformance module" is the obvious wrong answer:

- Not `:conformance` — C-4 gives it no LWJGL, no Minecraft and no Unimined (§2.2). A GLFW call cannot
  compile there.
- Not `:engine` — C-1 forbids any `org.lwjgl` reference outright.
- `:mod` works, and **without weakening C-3**: `PHASE_1_DOC.md` §4.3 states C-3 over *"`:mod`'s
  compiled classes"*, and §4.2.3 records that the architecture tests assert over `sourceSets.main`,
  not the test classpath — the same permission that lets ASM be a test-scope dependency in all three
  modules. A GL test in `:mod`'s **test** source set is therefore outside C-3's scope by
  construction, not by exception.

The task is opt-in — `./gradlew :mod:glTest -PglTests` — and **`:mod`'s default `test` task must
exclude `@Tag("gl")`**, because `PHASE_1_DOC.md` §4.11 runs `:mod:test` in CI's named seam step on a
headless runner (§4.14, request R4). The same opt-in task is what §10.3(4)'s fallback turns into a
pre-release checklist item.

### 10.3 OQ-10 — spike specification

Per §G4.4, four parts.

#### (1) The question, verbatim from RESEARCH.md §11

> **OQ-10** — "Headless GL testing in CI (RenderBook JUnit GL Extension exists — but does it run
> headless/EGL on CI runners?)"

#### (2) Procedure

**Stage A — local baseline.** Port the §10.1 pattern into `:mod`'s test source set as
`RUN-GL-SMOKE`: create a context; record `GL_VERSION`, `GL_VENDOR`, `GL_RENDERER`,
`GL_SHADING_LANGUAGE_VERSION`, the profile mask, `GL_MAX_DRAW_BUFFERS`, `GL_MAX_COLOR_ATTACHMENTS`,
`GL_MAX_TEXTURE_IMAGE_UNITS`; compile and link a trivial GLSL 120 vertex+fragment pair; create an FBO
with as many colour attachments as the profile claims and check completeness; tear down. On a
developer machine with a display this must pass, or the spike is testing the port rather than CI.

**Stage B — the CI matrix.** Run the same test on `ubuntu-latest` in five configurations, each a
separate job so a failure is attributable:

| # | Configuration | Purpose |
|---|---|---|
| B1 | bare, no display | establish the expected `glfwInit` failure — the control |
| B2 | `xvfb-run -a` with the runner's stock Mesa | the most likely winner |
| B3 | `xvfb-run -a` + `LIBGL_ALWAYS_SOFTWARE=1`, `GALLIUM_DRIVER=llvmpipe` | pin the software path so the result does not depend on the runner image's GPU story |
| B4 | EGL surfaceless (`EGL_PLATFORM=surfaceless`, GLFW platform hint where the bundled GLFW supports it) | the genuinely headless path; **expected to be the one that returns core-only or nothing**, which is why it is measured rather than assumed |
| B5 | OSMesa or SwiftShader | last resort, recorded even if slow |

**Stage C — measure, for every configuration that produced a context.** Profile (compat vs core),
GL and GLSL version, the three max-* probes, whether GLSL 120 compiled, FBO completeness at 8
attachments, wall-clock from job start to context, and **flakiness across 20 consecutive runs**.
Capture each successful configuration's profile as a `GLCapabilityProfile` fixture via the same text
format (§4.12) — the spike pays for itself in fixtures even if it fails its criteria.

**Stage D — only if C succeeds.** Extend to a real smoke: compile the internal default pack's
programs through `Lwjgl3GLDevice` and assert zero recorded `GLError`s. This is the test that would
make a CI GL tier worth having rather than merely possible.

#### (3) Success and failure criteria

Success requires **all** of:

| | Criterion | Why this one |
|---|---|---|
| S1 | A **compatibility-profile** context, GL ≥ 2.1 | `D-9` is not negotiable: packs are GLSL-120-era fixed-function-coupled code (§3.5). **A core-only context is a failure regardless of its version number** — this is the criterion most likely to be missed by a spike that only asks "did a context appear?" |
| S2 | A GLSL 120 vertex+fragment pair compiles and links | the actual thing packs need |
| S3 | ≥ 8 draw buffers and ≥ 8 colour attachments | App B.1's "8 where hardware allows"; below this the estate a pack builds cannot be exercised |
| S4 | ≤ 90 s from job start to a usable context | above this, per-push GL testing is not affordable and the tier is pre-release-only anyway |
| S5 | 20 / 20 consecutive CI runs green | a flaky GL job is worse than no GL job: it trains everyone to ignore red |
| S6 | In the selected successful CI configuration, Stage D compiles and links the internal default pack with zero recorded `GLError`s | proves the available context can run the real smoke that justifies a CI GL tier |

Failure is any of: no context in any configuration; **core-only in every configuration**; GLSL 120
rejected; fewer than 20/20; or Stage D compilation, linking, or its zero-`GLError` assertion fails
in the selected otherwise-successful configuration.

**Partial success is a real outcome and has a defined landing.** If S1, S2, S4, S5 and S6 hold but
S3 does not (a software renderer offering fewer than 8 draw buffers), the CI GL tier is limited to
"programs compile and link" — worth having — while every buffer-estate assertion stays headless
against the `hostile-4db` profile (§4.12), where it was going to be more precise anyway.

#### (4) Fallback design — decided now, not later

`[D-P2-21]` If the spike fails:

1. **CI keeps exactly what it already had:** `RUN-GOLDEN-CORE` and `RUN-CAPS-GATE` on every push,
   `RUN-GOLDEN-MATRIX` and `RUN-OPTIONS-ROUNDTRIP` in the gated `conformance` job. No CI coverage is
   lost, because none of it was ever GL-dependent.
2. **GL-context tests become local and pre-release**: `:mod:glTest -PglTests`, tagged `gl`, listed as
   a release-checklist item alongside `RUN-T0` and `RUN-T1-REGRESS` — which already had to run
   locally, since they need a client and a GPU.
3. **T0's GL evidence continues to come from run manifests** (§4.5.4), produced by local runs. This
   was always the design; the spike would have added a cheaper early signal, not the only one.
4. **The RenderBook pattern is still used** — locally, where it works — so Stage A is not wasted work
   even in the failure branch.

**The property that makes this a genuine fallback rather than a consolation:** no RESEARCH.md §9 exit
criterion in §3.5's table is assigned to a GL-in-CI run. The spike's failure therefore moves no
milestone gate. It costs an early-warning signal and nothing else, and §3.5 can be re-checked against
that claim by a reviewer in about a minute.

**Recording the result.** Per §G4.4, the implementation effort writes the outcome into RESEARCH.md
§11's OQ-10 status column and adds an addendum note here.

### 10.4 OQs this phase deliberately does not touch

`OQ-8` ("conformance oracle for modern packs") is marked *mitigated* by the §8.2 tiers and is
assigned to nobody in §G10; this document implements the mitigation (dual-spec packs are T0/T1
targets and are structurally refused at T2, `[D-P2-12]`) and does not reopen it. `OQ-3` (context
creation, HiDPI) is Phase 7's and surfaces here only as §4.4's recorded residual risk on window vs
framebuffer size.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale |
|---|---|---|
| `D-P2-1` | The capture driver lives in `:mod`; the harness crosses into the client through a **process boundary**, not a classpath edge | C-4 gives `:conformance` no Minecraft in any configuration (§2.2) |
| `D-P2-2` | Conformance runs set `-Dschmaloogium.debug.recordGL` | it puts the facade on the per-call `glGetError` cadence, narrowing diagnostic drain windows without establishing causation; every recorded error still fails T0 |
| `D-P2-3` | **Superseded by D-P2-24.** Historical v1 narrowed a camera path to static shots | v3 and PD §19.1 make actual motion a doc-gate requirement; v2 does not retain the narrowing |
| `D-P2-4` | One determinism rule for every text artifact this phase writes | generalises `PHASE_1_DOC.md` §4.7.5's `render()` guarantee; each artifact is either byte-compared or diff-reviewed (§4.1) |
| `D-P2-5` | **Goldens never contain pack source text** | goldens are committed and every matrix pack forbids redistribution (§4.11.1) |
| `D-P2-6` | **Baseline and oracle images are never committed**; manifests are | App G governs pack *files*; a rendered frame is an unsettled case, and the conservative policy costs almost nothing (§4.7.1) |
| `D-P2-7` | Three-level diff: per-pixel → aggregate → **connected cluster**; no perceptual metric as a gate | a 40×40 wrong block is 0.08 % of a frame and is what real breakage looks like (§4.6.2) |
| `D-P2-8` | Two test tasks: hermetic `test`, fixture-dependent `conformanceTest` | `:conformance:test` runs on every push in Phase 1's CI design (§4.14) |
| `D-P2-9` | `FixtureResolver` refuses a cache root inside a git work tree | the structural half of "never re-host" (§4.10.3) |
| `D-P2-10` | An absent manual fixture is `SKIPPED` **with its remedy printed**, never a pass, and skips are counted separately in every summary | §4.13's three reporting rules |
| `D-P2-11` | The hermetic corpus is packs **we own**: the internal default pack + eight micro-packs | matrix packs can never be committed, and CI needs something to chew on from week one (§4.11.6) |
| `D-P2-12` | A T2 request for a dual-spec pack is **refused** as a configuration error, not skipped | §8.2 says T2 is classic-only; a skip would let a report imply it was attempted (§4.2.3) |
| `D-P2-13` | T3's "no fallback silently masking a failure" is asserted from the manifest's per-slot `SOURCED`/`CHAIN` record paired with source presence | it is the only clause of §8.2's T3 that can be made mechanical (§4.2.4) |
| `D-P2-14` | The scene format is hand-parsed; **no new dependency coordinate** | §4.2.6's pin table is Phase 1's, and the grammar is smaller than the request (§4.3.1) |
| `D-P2-15` | GL-context tests live in `:mod`'s **test** source set | C-1 and C-3 scan `main`; `:conformance` has no LWJGL at all (§10.2) |
| `D-P2-16` | Image identity is a hash of the **pixel raster**, not of the file bytes | an encoder change must never look like a rendering change (§4.5.3) |
| `D-P2-17` | Warm-up floor: `max(60, 8 × the largest declared halflife in ticks)` | the smoothed uniforms (§3.2's `wetnessHalflife` family) are the slowest-converging inputs in the frame (§4.4) |
| `D-P2-18` | A sixth scene, `entities-blocks`, beyond the five families the spec names | Phases 9 and 10 need a scene where per-draw identity is visible; separately reported with their implementation gates, not the v0.3 terrain-scene exit criterion (§3.4) |
| `D-P2-19` | The evidence rule: a tier is recorded only with a run id and a manifest hash | a remembered pass is not a pass (§4.2.5) |
| `D-P2-20` | Registry version IDs and hashes are **left unfilled** here and populated by the implementation effort; an empty pin is a hard failure, never "latest" | App G gives version names, not pins; a fabricated pin is one CI would trust (§4.10.1) |
| `D-P2-21` | OQ-10's fallback is designed now and costs no milestone gate | §10.3(4) |
| `D-P2-22` | Hook evidence is serialized only as a complete direct projection of Phase 7's frozen primary/nested application report | application facts have stable owners and IDs; rendered behavior cannot authenticate whether an injection applied |
| `D-P2-23` | Pack acquisition mode, verified archive SHA-512, and licence are immutable runner facts transported through the capture plan and validated before manifest publication | The runner owns fixture resolution and integrity. Letting the pack or agent rediscover or self-report provenance would make the manifest attest to untrusted input rather than to the artifact actually executed (§0.17, §4.5). |
| `D-P2-24` | Camera paths are dense explicit frame-indexed samples with first-pose warm-up, one rendered frame per sample, and a bounded ≥2-sample capture window containing motion | Deterministic temporal input exposes the `previous*`/depth/motion-vector/TAA/bloom defect class without interpolation or wall-clock races (§4.3.4; PD §19.1) |
| `D-P2-25` | Scene, capture-plan, and run-manifest current majors are `/2`; capture plan and manifest `/1` are unsupported history | Motion changes the process-boundary meaning and requires reconstructible sample and pose fields; silently reading `/1` would fabricate absent temporal evidence |
| `D-P2-26` | Pintonium parse outcomes are complete source-text-free calibration evidence, never a conformance oracle | V3 requires the comparison, while D-3 requires Schmaloogium to parse all seven regardless of competitor outcome (§4.11.7) |

### 11.2 Disposition of `D-1` … `D-10`

| Decision | Disposition |
|---|---|
| D-1 Cleanroom-exclusive | Honoured — the only client the capture agent targets is the Cleanroom client. The T2 oracle runs on a *separate* OptiFine-on-Forge install, which is an oracle, not a support target |
| D-2 Shaders only | Honoured — nothing here measures anything outside shader behaviour; frame times are recorded and never gated (§7) |
| D-3 The matrix is the definition of done | **This phase implements it** — §3.1's registry and §4.9's runs are D-3 made operational; Pintonium calibration never relaxes all-seven Schmaloogium parsing |
| D-4 Modern-superset stage registry | Not this phase's, and not obstructed: the golden `[programs]` section is a key-per-slot list with no fixed cardinality, so growing past 43 slots or past `composite15` changes a golden's content and not its schema |
| D-5 Mixins only, no class replacement | Honoured — the capture agent asks Phase 7 for a hook (R11); it replaces nothing |
| D-6 The seam | Honoured and load-bearing: C-4 is the constraint that produced `[D-P2-1]`, and the headless golden harness is the seam paying rent (§8.3 of RESEARCH.md) |
| D-7 GPL-3.0-or-later | Honoured — the micro-packs and every harness artifact are ours; RenderBook (MIT) gets a `THIRD-PARTY.md` entry if its code is copied (§10.1) |
| D-8 Clean-room methodology | Honoured — §4.8 uses OptiFine as a **black-box oracle**: images produced by running the shipped product, which is behavioural observation. No decompiled source is read, quoted, or ported by this phase |
| D-9 Compat-profile baseline | Honoured, and made a **spike criterion**: OQ-10's S1 fails a core-only context regardless of version (§10.3) |
| D-10 Harness from week one | **This phase is D-10**; §9.2 is the week-one list |

### 11.3 Input contradictions, gaps, and open items

Reported, not smoothed over (§G1.1).

1. **Sildur's Vibrant: Modrinth or author site?** RESEARCH.md §8.3 states that *"BSL, Complementary,
   Sildur's have stable Modrinth version IDs via the Modrinth API"*; App G's row for Sildur's gives
   its Source as `sildurs-shaders.github.io` and, unlike the BSL row, does not name Modrinth-API
   download. **Ruling:** register `sildurs-vibrant` as `MODRINTH` — §8.3 is the more specific
   statement about *acquisition*, and App G's Source column is a provenance field rather than an
   acquisition instruction — **with a verification step at registry-population time** (§12). If
   verification fails, the entry becomes `MANUAL`, which costs nothing structurally, and §11.5 item 1
   asks RESEARCH.md for the correction.
2. **Phase-doc paths are resolved upstream.** V3 §G1 and `docs/MOVES.md` now define
   `docs/phase<N>/v<K>/`, `reviews/`, and `briefs/`; the former v1.1-era repo-root conflict and its
   upstream request are closed. This maintainer-authorized rebuild lives at
   `docs/phase2/v2/PHASE_2_DOC.md` under the §0.36 exception, while v1 remains history.
3. **Phase 1's old gating exception is historical only.** V1 recorded that it began before an
   earlier Phase 1 correction chain closed. The current manifest-selected
   `docs/phase1/v14/PHASE_1_DOC.md` is verified and valid for dependency consumption; no Phase 1 gate
   remains open here.
4. **The historical camera-path narrowing is closed.** `[D-P2-3]` is superseded by
   `[D-P2-24]`; `/2` supplies actual dense motion in every initial family and no longer treats a
   static shot as a path.
5. **The rendered-frame licensing research gap remains, but governance is settled.** RESEARCH.md
   does not classify screenshots produced by packs; `[D-P2-6]` and v3 §G6 conservatively prohibit
   committing them and permit only manifests/hashes/provenance. No implementation is blocked and no
   upstream DESIGN request remains.
6. **App G's version column is names, not pins** — `[D-P2-20]`. Not a contradiction; a gap this
   document declines to fill by invention.
7. **Derived-artifact governance is now upstream.** V3 §G6 adopts `[D-P2-5]`/`[D-P2-6]` globally:
   no pack source in goldens or calibration records, no rendered images in the repository, explicit
   fail-after-write golden updates, and committed image oracles limited to manifests/hashes/provenance.
   The prior request to DESIGN.md is closed.
8. **Modrinth's rate limit and User-Agent expectations were not verified.** The operation page
   consulted documents the endpoint and the JSON shape and carries neither figure. §4.10.2's client
   is written so that no figure is load-bearing (serialised fetches, descriptive User-Agent, `429` +
   `Retry-After` with bounded backoff), and §12 carries the read as a checklist item.
9. **`[client]`'s setting list is asserted from vanilla knowledge, not from a cited row.** The scene
   fields in §4.3.2 that name vanilla video settings are `[A]` — a working assumption that these are
   the settings which change pixels. The corrective is cheap and scheduled: the first
   `RUN-SCENE-SELFCHECK` and the first `CROSS_DRIVER` calibration will expose any setting that
   matters and was not pinned. Recorded rather than presented as verified.
10. **Dual-spec timing conflicts.** Appendix G makes the four dual-spec packs T0/T1 targets through
    v0.5, while §9 places modern-matrix progression after v0.5. This document provisionally runs
    their T0/T1 gates at every v0.1–v0.5 release while leaving modern-stage coverage post-v0.5
    (§3.5); §11.5 requests an authoritative reconciliation.

### 11.4 Items handed onward

**To Phase 3** — R5–R9 (§5.4) are the whole of it, and R5 is the load-bearing one: without a
deterministic, source-text-free engine snapshot there is no golden harness, and the golden harness is what
`D-10` buys. The eight micro-packs (§4.11.6) are yours to use as front-end test inputs; they were
designed against §3.2/§3.3/App F precisely so they would be.

**To Phase 4** — R10. §4.2.4's clause-2 assertion is the only mechanical part of T3, and it needs
`(resolution status, source present)` as a **pair**. A status alone cannot distinguish App A.2's
legitimate inheritance from a compile failure the backup chain absorbed.

**To Phase 5** — R10A. Supply the immutable live resource snapshot; its fields and absence rules are
the canonical `resources.*` wire block in §4.5.4.

**To Phase 7** — R11–R14 and R17–R19. R11 is the one that gates every image tier: without a defined moment after
`final` and before present, the capture agent has no correct place to grab a frame. Capture and
serialize Phase 3's front-end/pack evidence, Phase 4's program-resolution evidence, and Phase 5's
R10A snapshot; R17 consumes Phase 1's accepted R4A result. R18 requires a complete direct
copy of the frozen primary/nested application report, including Phase 8 when present, with no
renamed IDs or inferred capability. R19 is a mandatory downstream migration: Phase 7's historical
`/1` capture consumer cannot implement this document and must adopt `/2`, publish the actual
current/previous pose report, and undergo its own fresh verification before implementation. R13 is
conditional and is recorded so it is a known route rather than a surprise.

**To Phase 12** — R15/R16. Until R15 exists, `[pack] engine.*` keys are collected and reported as
**unvalidated** rather than accepted (§4.3.3), so a typo there is visible but not fatal.

**To Phase 8/9/10/13** — the scenes your milestones gate on already exist and are authored at v0.1
(§9.1): `night-shadows` (P8), `entities-blocks` (P9, P10), `weather-rain` and `water-translucent`
(P13's textures and P5's depth copies). If a subsystem you design has a behaviour none of the six
scenes can see, that is a request against §4.3's scene set, made in your §5.

**To the implementation effort** — §12 is ordered so that the impl gate is met first. Two things in
it are not code: populating the registry pins (§4.10.1) and running the OQ-10 spike (§10.3).

**To whoever runs the OQ-10 spike** — capture a `GLCapabilityProfile` fixture from every
configuration that produces a context, whatever the verdict (§10.3 Stage C). The fixture set is worth
more than the answer.

### 11.5 Requested upstream changes

**To RESEARCH.md** (for that document's maintainer, not for a phase session):

1. **§8.3's Sildur's-on-Modrinth claim** should carry a verification note, or App G should gain an
   acquisition column, so §11.3 item 1 does not have to be re-derived by whoever populates the
   registry.
2. **Reconcile dual-spec timing.** Appendix G requires T0/T1 through v0.5 while §9 schedules
   modern-matrix progression post-v0.5; state whether the former is an additional legacy-path gate
   or change one of the two schedules.

**To DESIGN.md:** none. V3 §G1 resolves the phase/review path convention and v3 §G6 makes the
derived-artifact rules global. Those two historical v1 requests are closed by adoption, not carried
forward as if still open. V3 itself remains immutable.

Per §G1.1 neither document is modified by this session.

---

## 12. Implementation checklist

Ordered so that the impl gate — *"fixture downloader + headless golden-run skeleton + scene-spec
parser implemented and green in CI before Phase 7's implementation lands"* — is satisfied by item 12.
Every item names its milestone tag and its test hook.

**Week one — the before-renderer subset (§9.2)**

| # | Item | Tag | Test hook |
|---|---|---|---|
| 1 | Replace `:conformance`'s placeholder test; add the package skeleton of §2.3 | `v0.1` | `./gradlew :conformance:test` green |
| 2 | Add the JUnit tag configuration and the `conformanceTest` task to `conformance/build.gradle`; default `test` excludes `fixtures` and `gl` (`[D-P2-8]`, request R3) | `v0.1` | a `@Tag("fixtures")` test is absent from `test` and present in `conformanceTest` |
| 3 | `/2` `SceneSpec` + parser/validator + dense `PathCapture` scheduler (§4.3) | `v0.1` | `SceneParserTest`, `SceneParserRejectionTest`, `SceneRoundTripTest`, `MotionPathSchedulerTest` |
| 4 | Author the six scene files of §3.4, each with its required moving `[path]` and bounded ≥2-sample window | `v0.1` | `SceneCorpusTest` |
| 5 | `/2` `CapturePlan`: runner resolves all scene defaults and dense SHOT/PATH samples before construction; writer and reader perform no defaulting; runner-owned acquisition mode/archive SHA-512/licence are frozen (§4.5.2) | `v0.1` | `CapturePlanTest`, `MotionPathSchedulerTest` |
| 6 | `packs.registry` format + `PackFixtureRegistry`; **populate every App G row except the pins** | `v0.1` | `RegistryTest` |
| 7 | **Verify Sildur's Vibrant is on Modrinth** and fix its mode if not (§11.3 item 1) | `v0.1` | `RegistryTest` asserts mode/URL consistency |
| 8 | **Read Modrinth's rate-limit and User-Agent documentation** and record the figures in the transport's javadoc (§11.3 item 8) | `v0.1` | — (a documentation step, deliberately listed) |
| 9 | `FixtureCache` + root resolution + **in-repo refusal** (`[D-P2-9]`) | `v0.1` | `FixtureCacheRootTest` |
| 10 | `HttpTransport` + Modrinth resolver + SHA-512 integrity + `MANUAL`-mode instructions (§4.10) | `v0.1` | `FixtureResolverTest` with the stub transport |
| 11 | **Populate the registry pins** — version IDs and SHA-512 for the four `MODRINTH` rows; canonical filenames and hashes for the three `MANUAL` rows (`[D-P2-20]`) | `v0.1` | a live `conformanceTest` resolution of each |
| 12 | `GoldenDocument` model, `GoldenWriter`, `GoldenComparer`, `-PupdateGoldens` workflow (§4.11) | `v0.1` | `GoldenWriterDeterminismTest`, `GoldenComparerTest`, `GoldenCorpusTest` — **impl gate met here** |
| 13 | Author the eight micro-packs (§4.11.6) | `v0.1` | they are inputs to items 12 and 16 |
| 14 | Synthetic `GLCapabilityProfile` fixtures (`minimum-gl21`, `baseline-gl30`, `typical-gl33`, `hostile-4db`) + `profiles.index` (§4.12) | `v0.1` | consumed by items 12 and 16; index validated by `RegistryTest`'s sibling |
| 15 | `ImageDiffer`, `TolerancePolicy`, `ClusterAnalysis`, `IgnoreMask`, `DiffReportWriter` (§4.6) | `v0.1` | `ImageDifferTest`, `ClusterAnalysisTest`, `TolerancePolicyTest`, `IgnoreMaskTest` |
| 16 | `RUN-GOLDEN-CORE` and `RUN-CAPS-GATE` wired as real tests over items 12–14 | `v0.1` | both green in `:conformance:test` **with no renderer in existence** |
| 17 | `Tier`, `TierOutcome`, evaluators, `TierLedger`, `TIERS.md` renderer (§4.2) | `v0.1` | `TierEvaluatorTest`, `TierLedgerTest` |
| 18 | `ConformanceReport` + the three renderers, incl. the skip-counting rule (§4.13) | `v0.1` | `ReportRendererTest` |
| 19 | `HarnessRun` / `RunRegistry` with §4.9's catalogue | `v0.1` | `HarnessRunRegistryTest` — including the §3.5 traceability assertion |
| 20 | Fill the CI `conformance` job (§4.14); add the fixture cache step | `v0.1` | a `workflow_dispatch` run resolves fixtures and runs `conformanceTest` |
| 21 | **Run the OQ-10 spike** (§10.3) and record the outcome in RESEARCH.md §11 + an addendum here | `v0.1` | the spike's own criteria S1–S6 |

**As soon as Phase 3 and Phase 4 land**

| # | Item | Tag | Test hook |
|---|---|---|---|
| 22 | Implement `GoldenProjectionAdapter` against Phase 3's engine-owned snapshot API plus Phase 4's R10 enrichment; approve the first complete golden corpus by review | `v0.1` | `RUN-GOLDEN-CORE` produces complete real content |
| 23 | `RUN-GOLDEN-MATRIX` over the seven fixtures × three profiles; unavailable before both inputs land | `v0.1` | `conformanceTest` |
| 23a | Capture and review the seven exact Pintonium observations at revision `9c2fcc1a4814cafc0242370757e9e05ea83c5be3`; implement the source-text-free record validator/comparer and `RUN-PINTONIUM-PARSE-CALIBRATION` | `v0.1` | `PintoniumParseCalibrationTest`; all seven Schmaloogium parses required |

**As soon as v0.1 renders**

| # | Item | Tag | Test hook |
|---|---|---|---|
| 24 | Migrate Phase 7's `/1` consumer to `/2` (R19), then implement `CaptureAgent`, no-default `CapturePlanReader`, `SceneApplier`, `FrameGrabber`, and `/2` `RunManifestWriter`, including frame-end actual current/previous pose reports and R18 hook evidence | `v0.1` | `MotionPathSchedulerTest`, `RUN-SCENE-SELFCHECK`, `HookManifestEvidenceTest`; Phase 7 fresh verification required before consumption |
| 25 | `/2` `CaptureRunner` + world cache + timeout handling + byte-exact provenance, capture-window, ordinal, count, and pose validation before atomic publication (§4.5.1, §4.5.5, `[D-P2-23]`) | `v0.1` | complete reconstructible run directory; every missing/mismatch case yields runner-authored failure manifest |
| 26 | `RUN-SCENE-SELFCHECK` and `RUN-MOTION-PATHS` across all six scenes — **the determinism and v3 motion acceptance tests** | `v0.1` | repeated SHOT and same-ordinal PATH runs `IDENTICAL`; all `/2` pose/count/window checks pass |
| 27 | Calibrate `SAME_MACHINE` and `CROSS_DRIVER` (§4.6.5) and write `calibratedOn` | `v0.1` | the profile file stops carrying placeholder numbers |
| 28 | `RUN-T0` across the classic matrix — **v0.1's second exit criterion** | `v0.1` | §4.2.1's predicates |
| 29 | `RUN-T1-APPROVE` for one classic pack; commit the baseline manifests — **v0.1's first exit criterion** | `v0.1` | contact sheet reviewed and signed |
| 30 | `RUN-T1-REGRESS` in the pre-release checklist | `v0.1` | |
| 31 | Implement dual-spec `RUN-T0` / `RUN-T1-REGRESS`; execute them at every v0.1–v0.5 release gate, provisionally pending §11.3 item 10 | `v0.1` | §4.2.1 and §4.2.2 predicates |

**Later milestones**

| # | Item | Tag |
|---|---|---|
| 32 | T2: the §4.8 procedure written up as a runbook, the oracle-manifest tool, the T2 evaluator, `RUN-T2-PILOT` | `v0.2` |
| 33 | Calibrate `CROSS_ENGINE` per §4.6.5 step 3 | `v0.2` |
| 34 | `RUN-T2` over the milestone's declared scene set | `v0.3` |
| 35 | `RUN-OPTIONS-ROUNDTRIP` (R16) | `v0.4` |
| 36 | T3 evaluator + author `conformance/features/<packId>.features` for the classic tier | `v0.4` |
| 37 | Scenes for the dual-spec modern-pass families | `post-v0.5` |

---

*Review round 36 returned **FAIL** against the v3 surface and remains immutable without a
`## Resolutions` section. Review round 37 returned **PASS-WITH-CORRECTIONS** against the
maintainer-authorized v2 rebuild; §0.37 applies its sole correction. This artifact is **not
verified** pending the required fresh whole-document Review 38.*
