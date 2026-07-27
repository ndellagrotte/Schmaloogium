You are a fresh Claude Code session commissioning **design revision v2.0-RC3**, the Oculus
revision. You will not write it yourself — you will run a parallel-agent `Workflow` that mines
`reference-src/Oculus-1.12/`, gates what it finds, and produces two documents. Working directory:
/home/nick/IdeaProjects/schmaloogium-project/Schmaloogium/.

## Why this session exists

A fourth reference tree appeared in the checkout on 2026-07-26 and **nothing in `docs/` knows it
exists.** Every governance artifact that enumerates the reference trees still says three:
`docs/design/v2.0-RC2/DESIGN.md` §G0.2's alias table (ll. 128–133), `CLAUDE.md` ll. 161–164
("`reference-src/` is gitignored and holds three checkouts"), `docs/MOVES.md` ll. 42–50. A tree
that no document governs is a tree every future session will read under no rules at all — which is
precisely the condition §G11 was written to end for Pintonium.

The substantive reason is a gap list. `DESIGN.md` §G11.5 ll. 921–924 records what Pintonium could
not help with **at all**: *"shadow pass on 1.12.2, sky/weather/clouds hooks, held-item uniforms,
`blockEntityId`/`entityColor` delivery, companion atlas stitching, `version.<mcver>` gate, internal
default pack, sliders UI, render-quality multipliers."* Pintonium is, in §G11.1's own words
(l. 840), *"heavily WIP and largely AI-assisted, with confirmed bugs (PD §17) and dead
subsystems."* Oculus is the mature, complete Iris implementation those subsystems were extracted
from. It answers roughly **half** that list — and the brief's central discipline, below, is making
every agent say **which half** its finding lands in.

Your contract has three steps, then you stop: read enough to write the workflow honestly, run it,
report what it produced and what it failed to produce. **You are not the author of RC3** — a stage
of your workflow is. Do not hand-edit its output to make it look better than it is.

## What the tree actually is — read this before you believe anything else about it

`gradle.properties` declares `minecraft_version=1.12.2`, `forge_version=14.23.5.2860`,
`mappings_channel=stable` / `mappings_version=39` — the same MCP mappings this project pins at
`build.gradle:53`. **That describes the build target, not the source, and taking it at face value
is the single most likely way this workflow produces confident, wrong output.**

Verified against the tree, 2026-07-26:

- `git -C reference-src/Oculus-1.12 describe --tags` returns **`1.16.x-v1.2.2-147-g22bdab676`**.
  The nearest ancestor tag is Oculus **1.16.5 / v1.2.2**; the 1.12.2 branch is 147 commits on top,
  of which roughly ten are porting work. `README.md` declares `!!!PROJECT CLOSED!!!`.
- **76** source files still carry Mojang 1.16.5 imports (`net.minecraft.world.level`,
  `com.mojang.blaze3d.vertex`, `net.minecraft.util.Mth`) against **12** carrying 1.12.2 MCP markers
  (`OpenGlHelper`, `net.minecraft.util.math.MathHelper`).
- Of **132** `@Mixin` annotations, **zero** reference the 1.12.2 classes App E catalogs —
  `RenderGlobal`, `EntityRenderer`, `Framebuffer`. They target `LevelRenderer`, `GameRenderer`,
  `RenderTarget`, `ParticleEngine`.
- `src/main/resources/META-INF/mods.toml` (the Forge 1.13+ format, `loaderVersion="[36,)"`) is
  present, while `build.gradle`'s `processResources` expands an **`mcmod.info` that does not
  exist**. FML on 1.12.2 would not discover the mod.
- Hybrid files exist: `src/main/java/net/coderbot/iris/gl/shader/ProgramCreator.java` imports both
  1.16.5 `GlStateManager` and 1.12.2 `OpenGlHelper`.

**Conclusion the workflow must carry, not re-litigate: Oculus is not a 1.12.2 hook-point
reference.** Its Minecraft-facing code is unusable as-is and Pintonium remains the only working
1.12.2 evidence. Any finding of the form "Oculus shows how to hook X on 1.12.2" is false by
construction and a miner that produces one has misread the tree.

What it *is*: the complete, mature, loader-independent Iris logic — pack front-end, options and
profiles, include graph, directive parsing, uniform suppliers, shadow math, the composite/final
pass model, and the GUI. That is where every finding should come from.

## Read, in this order

1. **`docs/design/v2.0-RC2/DESIGN.md` Part I in full** (§G0–§G11, ll. 83–943). §G0.1 (ll. 85–99),
   §G0.1a (ll. 101–115), §G0.4 (ll. 145–176) and **§G11 entire** (ll. 833–943) are the load-bearing
   ones — §G11 is the exact template your §G12 must follow, six subsections in the same order.
2. **`docs/MOVES.md`** in full. The `⚠️ Three files named DESIGN.md` section (ll. 63–95), the
   version-label rules (ll. 97–125), the `-RC`-after-partial-adoption ruling (ll. 126–145), and the
   acceptance sweep (ll. 165–187).
3. **`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`** (`PD`, 892 lines) — read its front
   matter and its section *shape* closely. It is the template for the report you commission, and
   it is also the dedup baseline: a finding already in `PD` is not a finding.
4. **`RESEARCH.md` §0 and §1**, plus l. 102 (`D-8`, the AGPL prohibition) and ll. 145–148 (where
   *other* Oculus-1.12.2 backports are surveyed and dismissed — this specific tree is not among
   them, which is itself worth recording).
5. **`.claude/workflows/verify-loop.js`** ll. 46–149 — the pin/facts tables and the ~40-line
   rationale comment above them. You are copying its stage discipline and its schema shapes.
6. **`docs/tooling/VERIFY_LOOP_BRIEFS.md`** and one file from `docs/phase1/briefs/` — the house
   prompt style your agent prompts should match.
7. **Only then**, the Oculus tree itself, and only enough to write accurate prompts. The mining is
   your agents' job, not yours; a session that mines it personally has spent its context on work it
   was supposed to delegate.

Record what you read beyond this list, and why. Record deliberate omissions as omissions.

## The licensing boundary — stage one, blocking, no exceptions

`RESEARCH.md` l. 102 (`D-8`) states the prohibition: *"Iris's bundled glsl-transformer is
**AGPL-3.0 — never copy**"*. `DESIGN.md` §G11.2 item 2 (ll. 859–863) extends it. Oculus is the
tree that prohibition was written about, and the contamination is **binary, not source** — so a
`grep` for "AGPL" returns nothing and a careless agent concludes the tree is clean. It is not.

Verified boundary:

- The tree is **LGPL-3.0-only** — one `LICENSE` (GNU LGPL v3), and
  `src/main/resources/META-INF/mods.toml:3` reads `license="LGPL-3.0-only"`. There are **no SPDX
  headers anywhere**, so per-file provenance must be read from comment notices, not assumed.
- `libs/glsl-transformer-1.0.0-pre21.2.jar` — the AGPL artifact, present as compiled classes under
  `io/github/douira/glsl_transformer`, **stripped of `META-INF`, manifest and pom**, so its licence
  is not discoverable from the artifact itself.
- `glsl-relocated/` contains exactly one file, `glsl-relocated/build.gradle`, whose l. 11 declares
  `implementation(shadow("io.github.douira:glsl-transformer:1.0.1"))`.
- **Seven source files import `io.github.douira`, all under
  `src/main/java/net/coderbot/iris/pipeline/transform/`**: `TransformPatcher.java`,
  `CompatibilityTransformer.java`, `AttributeTransformer.java`, `CompositeTransformer.java`,
  `CompositeDepthTransformer.java`, `SodiumTerrainTransformer.java`, `Parameters.java`. These are
  LGPL Iris code written *against* an AGPL AST API; mining them imports AGPL-shaped surface.

**Hard blocklist, to appear verbatim in every miner prompt:**
`src/main/java/net/coderbot/iris/pipeline/transform/`, `libs/`, `glsl-relocated/`.
A miner that cites a path under any of them has failed its contract; the Gate drops the finding and
you report the breach.

Sub-licences to record if anything is carried across: `net/coderbot/iris/vendored/joml/` is **MIT**
(69 files); `de/odysseus/ithaka/digraph/` is **Apache-2.0** (22 files); `kroppeb/stareval/`
(28 files) carries **no header** — the same unverified-licence problem §G11.2 item 3 already
records, and the workflow must reach the same "verify before reuse" conclusion rather than a new
one. Six files carry explicit Sodium/Canvas LGPL attribution, e.g.
`src/main/java/net/coderbot/iris/gl/shader/ProgramCreator.java:1` — *"This file is based on code
from Sodium by JellySquid, licensed under the LGPLv3 license."*

One clean corroboration worth capturing: `build.gradle:147` declares
`implementation(shadow("org.anarres:jcpp:1.4.14"))`. §G11.2 item 4 already blesses jcpp as
Apache-2.0 and Phase 3 already adopted it on Pintonium's evidence alone. Two independent Iris-family
implementations choosing the same preprocessor is a strengthening of that decision, and it is the
cheapest real finding in the tree.

## Where the value is concentrated

Excluding `net/coderbot/iris/vendored/joml/` — which is **128,413 of the tree's 171,532 LOC, about
75%** — the real codebase is roughly 545 files and 43k LOC. **Every survey must exclude that path**
or it drowns in JOML.

| Oculus package | Feeds | What it holds |
|---|---|---|
| `shaderpack/` + `option/` + `parsing/` + `include/` | P3, P12 | `ShaderProperties` (610 LOC), `OptionAnnotatedSource` (559), profiles, include graph, directive parsers |
| `shaderpack/preprocessor/` | P3 | JCPP integration — `JcppProcessor`, `PropertiesPreprocessor` |
| `postprocess/` | P5 | `BufferFlipper` ping-pong, `CompositeRenderer` (379), `FinalPassRenderer` (395) |
| `rendertarget/` | P5 | colortex management, depth textures |
| `shadows/` + `shadow/ShadowMatrices.java` | **P8** | `AdvancedShadowCullingFrustum` (373), clipping planes — the **math**, never the hooks |
| `uniforms/` + `kroppeb/stareval/` | P6, P11 | suppliers, cadence, smoothing, the custom-uniform evaluator |
| `gui/` | P12 | `ShaderPackScreen` (574), option list — **sliders UI**, a §G11.5 "no help available" item |
| `pipeline/` | P4, P7 | `DeferredWorldRenderingPipeline` (1,269) — the master orchestrator |
| `build.gradle`, `settings.gradle` | P1 | the only worked example of RetroFuturaGradle + MixinBooter + Vintagium + AT wiring for a 1.12.2 Iris fork |

**Every finding must be classified `loader-independent` or `1.12.2-hook`.** The second category is
where this tree lies to you, and a miner that returns only the first category has probably done its
job correctly.

## The workflow you will run

Model it on `.claude/workflows/verify-loop.js`. **Stage boundaries are barriers and each is
justified** — the licensing gate must complete before any miner starts, dedup needs every miner's
output at once, and `OD` must exist before RC3 can cite it. `.claude/commands/verify-loop.md`
forbids converting them to a pipeline; the same reasoning applies here. Budget ~12–16 agents.

1. **Provenance & licensing gate** — 1 agent, serial, blocking. Re-verifies the porting-state
   numbers and the AGPL boundary above **from the tree, not from this brief**, and returns a block
   that is prepended verbatim to every later prompt. If it contradicts this brief, it wins and you
   report the discrepancy.
2. **Mine** — ~8 read-only `Explore` agents in parallel, one per row of the table above. Each
   returns findings with repo-relative `file:line` evidence and a verbatim quote, the phase it
   feeds, a proposed trust tier in §G11.5's four-tier vocabulary, and the loader-independent /
   1.12.2-hook classification.
3. **Cross-check** — 2–3 agents. Each finding against `RESEARCH.md` (contract) and against `PD`:
   *confirms* / *adds depth PD lacks* / *contradicts PD* / *already covered*. Only the middle two
   survive. Conflicts with RESEARCH.md are **recorded, never resolved** (§G0.1).
4. **Gate** — 1 read-only agent, low effort, reusing `verify-loop.js`'s `GATE_SCHEMA` shape.
   Re-resolves every citation at the line and drops anything that does not match verbatim —
   `anchor_ok: false` for text that is absent, paraphrased, or has words dropped from the middle
   without an ellipsis. Log every drop.
5. **Synthesize `OD`** — 1 agent, writes `docs/reference/oculus/v1.0/OCULUS_DESIGN.md` on the `PD`
   template: front matter declaring its own **Role** as a mining report that does not change the
   contract; subsystem sections each naming the phase they feed and closing with a **"Relevance to
   Schmaloogium"** block; a numbered pitfalls catalogue; a contract-divergence table; a trust-tier
   section; and a phase→answer table covering all 14 phases.
6. **Author RC3** — 1 agent. Copies RC2 to `docs/design/v2.0-RC3/DESIGN.md` and edits *that copy*:
   every change marked `**REV3:**` in place, a new **§G12** on §G11's six-subsection shape, the
   §G0.2 alias row, a §G7 licensing item, a "Revision highlights (RC2 → RC3)" table at the top, and
   a closing additions-audit table. Header must state `v2.0-RC3` and its own path.
7. **Verify** — 1–2 read-only agents. RC2 and all evidence byte-identical? Every `**REV3:**` claim
   anchored at the line? Highlights table matching the actual diff? `PHASE_FACTS`/`DESIGN_PINS`
   untouched? Sweep clean at exactly one line?

## Hard rules

- **Do not modify** `RESEARCH.md`, `docs/design/v1.1/`, `docs/design/v2.0-RC1/`,
  `docs/design/v2.0-RC2/`, `PINTONIUM_DESIGN.md`, or any phase doc or review including their
  `## Resolutions` sections. They are evidence. RC3 is a **new directory**; RC2 must come out of
  this byte-identical. Record the baseline first and check it at the end:
  ```
  md5sum docs/design/*/DESIGN.md docs/research/v1/RESEARCH.md \
         docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md
  ```
- **Do not re-point the harness.** `PHASE_FACTS` (`.claude/workflows/verify-loop.js` ll. 110–127)
  anchors Phase 1 to `v2.0-RC2` and Phase 2 to `v1.1`. RC3 must not touch `docVersion`, `design`,
  `spec`, `docGate`, or any `DESIGN_PINS` entry. Migrating a phase onto a revision is §G0.4 step 3,
  a deliberate maintainer operation that is **not** part of authoring a candidate.
  `docs/MOVES.md` ll. 90–91 is the incident that bought this rule: *"Pointing a phase at the wrong
  revision would not raise an error — it would silently feed every agent the wrong text."*
- **Derive no line pins for RC3.** §G0.4 step 1 runs at *adoption*, from the finished file's own
  headings, and never by shifting another revision's numbers. A pin table written into RC3 now
  would be invalidated by RC3's next edit.
- **One permitted harness edit, and it lands in three files.** `DESIGN_ALL`
  (`.claude/workflows/verify-loop.js` ll. 104–108) lists all three revision paths so do-not-modify
  protection "does not silently narrow to one file the moment a phase moves revisions". A fourth
  revision must be added there — in **both** `.claude/workflows/verify-loop.js` **and**
  `.claude/workflows/phase-verify-loop.js`, which are byte-identical copies with nothing keeping
  them in sync — and noted in `docs/tooling/VERIFY_LOOP_BRIEFS.md` per its standing "change both,
  saying which" rule. No other harness edit is authorised.
- **`docs/MOVES.md` is part of the deliverable, not an afterthought.** The three-column collision
  table (ll. 65–71) becomes four columns; a version-label row is added; the new reference tree is
  recorded in the reference-sources table. Then run the acceptance sweep. **This brief has already
  moved the expected result**: citing the two documents it commissions made them dangle, so
  MOVES.md now records three expected lines — the stale `docs/phase1/v10/PHASE_1_DOC.md`, plus
  `docs/design/v2.0-RC3/DESIGN.md` and `docs/reference/oculus/v1.0/OCULUS_DESIGN.md` as **pending**
  forward references. **Creating those two documents resolves both**, so a successful run drops the
  sweep back to exactly one line — and you must then narrow MOVES.md's expectation back to one,
  deleting the two pending entries. Any line beyond the expected set is a regression you introduced.
- **The `-RC3` label is not yours to choose.** `docs/MOVES.md` l. 99: directory names come from the
  document's own header. Ll. 135–136 sharpen it: `-RC` drops only when **every** downstream phase
  doc has adopted the revision, *"not when the first one does"*, and *"the label cannot move ahead
  of a design-document revision."*
- **Forbidden sources, by pattern:** any directory named `chatlogs/` anywhere below `docs/` (any
  extension — some are `.md`), and any `*.txt` at the repository root. These are prior sessions'
  transcripts and they transmit the author's blind spots. The rule is stated by pattern because a
  rule naming files goes stale the next time someone exports; the earlier filename-based form
  matched 1 of 17 transcripts, and a sub-agent breached it in Phase 1 round nine.
- **Cite repo-relative paths and quote at the line.** A bare filename does not resolve and the Gate
  drops it. If a coordinate you were given disagrees with what is at that line, **stop and report
  it** rather than guessing which is right (§G0.4's closing sentence).
- **Oculus is evidence, never contract.** RESEARCH.md wins every conflict and the conflict is
  reported, never silently resolved (§G0.1, §G0.1a). Contract-visible adoptions need a recorded
  decision per §G11.4, not a silent swap.
- **No code.** The deliverables are documents. Illustrative signatures inside them are fine.
- Pack-facing vocabulary verbatim — colortex, flip, gbuffers, backup chain, `mc_Entity` (§G4.1).

## Two things RC3 should correct, and one it must not touch

RC2's own front matter has been overtaken by events, and a revision that copies it forward
propagates the error:

1. **Ll. 6–8 say v1.1 "remains the governing design until the §G0.4 adoption procedure is
   executed", and ll. 9–11 describe Phase 1 as "at v10 with eleven reviews."** But `docs/MOVES.md`
   ll. 68 and 78–81 record that **RC2 already governs Phase 1** from its §0.11 on, while Phase 2
   still cites v1.1 — and Phase 1 now has thirteen reviews. RC3 states its own status correctly.
2. **§G0.4 step 1 says "the ~16 section→line mappings."** `docs/MOVES.md` l. 88 flags that this
   *"matched no literal count"* — the real counts are 12 for v1.1 and 13 for RC2. RC3 corrects it.

**Out of scope — report, do not fix:** `PHASE_FACTS[1].docVersion` is `'v11'` while
`PHASE_1_DOC.md` now carries §0.12 and §0.13, so by `docs/MOVES.md` l. 119 it is two version rolls
overdue. Rolling a phase doc is two steps run together and **only after a verify loop exits**
(ll. 111–121); doing it inside this workflow would point a later round at a directory that no
longer exists. Name it in your report and leave it alone.

## Do not re-fight these

Settled, and re-deriving them is how a run spends its budget on nothing: whether Pintonium is a
legitimate reference at all (§G11 settles it); the four §G11.2 licensing rulings, except the one
open question below; §G11.4's pre-decided rejection list — attribute locations 11–14, dynamic
per-program texture-unit allocation, Iris dimension-folder semantics, `Random(0)` noise, 16-colortex
unconditional allocation; the GPL-3.0-or-later project licence and the executed LICENSE swap; and
the §G3.1 module layout. Oculus evidence does not reopen any of them, and a finding that assumes it
does has mistaken evidence for contract.

**The one genuinely open question**, and it is worth a dedicated agent: §G11.2 item 2 instructs
sessions to treat `org.taumc:glsl-transformation-lib` as AGPL-3.0. Pintonium consumes it in a
package literally named `foss_transform/`, which *suggests* taumc re-licensed douira's work. If that
is true it changes a standing prohibition, which makes it contract-visible. **It must be settled on
the actual licence text of the taumc project, never on the name of a package** — a directory called
`foss_transform` is a claim by its author, not evidence. If the evidence is not conclusive, the
correct output is "unresolved, prohibition stands", recorded as an open question in RC3. Do not let
a workflow relax a licensing rule on an inference.

## Network use

Two sanctioned purposes: confirming the `org.taumc:glsl-transformation-lib` licence above, and
resolving the upstream provenance of `kroppeb/stareval` (§G11.2 item 3 records that the upstream
repo no longer resolves — check whether that is still true, since it decides between reuse and
clean-room implementation from App F.6). No third purpose. If an agent fetches anything else, it
discloses it and names the finding that turned on it.

## Deliverable

Two documents created, four files edited, and a report from you. Nothing else.

1. **`docs/reference/oculus/v1.0/OCULUS_DESIGN.md`** — the mining report, on the `PD` template,
   with front matter that declares its own Role as evidence that does not change the contract.
2. **`docs/design/v2.0-RC3/DESIGN.md`** — RC3, header stating `v2.0-RC3` and its own path, every
   change `**REV3:**`-marked, §G12 added, §G0.2 alias row added, §G7 item added, RC2→RC3 highlights
   table at the top, additions audit at the bottom.
3. **Edits:** `docs/MOVES.md` (collision table, version label, reference-sources row, sweep run);
   `.claude/workflows/verify-loop.js` and `.claude/workflows/phase-verify-loop.js` (`DESIGN_ALL`
   only, identically); `docs/tooling/VERIFY_LOOP_BRIEFS.md` (noting the `DESIGN_ALL` change).
4. **Your report**, stating plainly: how many findings each stage produced and how many the Gate
   dropped and why; which §G11.5 "no help available" items are now answered and which are **not**;
   every RESEARCH.md conflict found; the licensing open question's outcome; the evidence md5sums
   before and after; and the sweep output. If the workflow produced less than this brief hoped for,
   **say so as a fact rather than softening it** — a run that reports only successes misrepresents
   its own coverage.

Then stop. Do not begin §G0.4 adoption; RC3 ships as the candidate it is.

---
One deliberate choice, in case you want to adjust it: this brief states the porting-state finding
as settled fact and instructs the licensing gate to re-verify it anyway. That is on purpose and it
is not redundancy — the numbers were taken by one reader on one day, and a workflow that inherits
them without checking would carry a single point of failure into every downstream prompt. If the
gate finds the tree is better ported than stated here, that is a real finding about this brief and
the right output is to say so and widen the mining scope, not to quietly proceed on the version you
were handed.
