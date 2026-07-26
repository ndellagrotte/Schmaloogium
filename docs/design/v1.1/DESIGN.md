# Schmaloogium — Design Document

> **Status:** v1.1, 2026-07-24. Authored from [RESEARCH.md](../../research/v1/RESEARCH.md) (first complete
> draft, same date). v1.1 encodes the build/verify wave workflow (§G1, §G5.3).
> **Role:** The phase-segmented design for building Schmaloogium — OptiFine/Iris-format
> shader-pack support for Cleanroom clients on Minecraft 1.12.2. Just shaders. Nothing else.
> **Audience:** AI agent sessions building or verifying one phase each (protocols in §G1),
> and the project owner supervising them.

---

## Part I — Global sections

Everything a phase session must know that is not phase-specific. Phase sessions read all of
Part I; it is deliberately compact. Part II holds the per-phase specifications.

---

## G0. Front matter

### G0.1 Relationship to RESEARCH.md

**RESEARCH.md remains the source of truth.** This document arranges its findings into an
executable design; it does not supersede it. Rules:

- Where this document cites a RESEARCH.md section (`§n`) or appendix (`App X`), the cited
  text is normative and is *not* restated here in full. Phase sessions read the cited text.
- On any conflict between this document and RESEARCH.md, **RESEARCH.md wins**, and the
  conflict must be reported in the phase doc's *Decisions & open items* section — never
  silently resolved.
- RESEARCH.md's confidence tags (`[V:doc]`, `[V:observed]`, `[D-n]`, `[Q:OQ-n]`, `[U]`,
  `[A]` — RESEARCH.md §0.2) carry over unchanged. Phase docs must preserve provenance:
  contract claims cite `§3`/Apps; behavioral claims cite `§4`; platform claims cite `§5`.
- RESEARCH.md facts were verified 2026-07-24. The only fact expected to rot quickly is the
  Cleanroom loader version (daily alpha cadence, OQ-2) — Phase 1 re-verifies and pins it.

### G0.2 Path conventions

Paths are written relative to the **workspace root** — the directory containing this repo
and its siblings:

| Shorthand | Meaning |
|---|---|
| `Schmaloogium/…` | this repo (the mod + this document + RESEARCH.md) |
| `schlorbium-project/…` | decompiled OptiFine G6_pre1 reference (study-only — §G7 rules) |
| `cleanroom-src/…` | Cleanroom platform sources. **Vanilla render classes exist there only as `.java.patch` files** (`cleanroom-src/patches/minecraft/…`); full sources are present only for Forge (`src/main/java/net/minecraftforge/`) and loader internals (`src/main/java/com/cleanroommc/`). For vanilla method bodies and signatures, use the MCP `cleanroom` server recipes (RESEARCH.md §12.4), not cleanroom-src. |

### G0.3 The load-bearing structural principle

**Phases are subsystems; milestones are implementation staging.** A phase architects its
*entire* subsystem — including parts whose implementation RESEARCH.md §9 schedules for a
later milestone — and tags every designed component with the milestone at which it gets
implemented (`v0.1`…`v0.5`, `post-v0.5`). Phase order ≠ milestone order. Example: Phase 7
designs the depth-copy architecture now, tagged "copies implemented at v0.5", because
retrofitting architecture is the failure mode that kills projects (decision D-4's rationale,
generalized).

---

## G1. Agent protocol — session types and how to execute them

Every phase passes through two session types, always in this order and always as separate
fresh sessions: a **build session** (§G1.1) authors the phase doc; a **verify session**
(§G1.2) adversarially reviews it. Corrections, if any, are applied by a **fix-up session**
(§G1.3). Scheduling — who may start when — is governed by the wave cadence and gating
invariant in §G5.2–§G5.3.

### G1.1 Build sessions

You are one fresh Claude Code session assigned exactly one phase from Part II. Your contract
has three steps, then you stop:

1. **Read and understand your phase.** Mandatory reading, in order:
   - All of Part I (G0–G10) of this document, plus your own phase spec in Part II.
   - RESEARCH.md §0 (reading guide, tags) and §1 (mission, non-goals, decision log) — always.
   - The **Required inputs** listed in your phase spec: specific RESEARCH.md sections and
     appendices, specific external files, and the PHASE docs of your declared dependencies.
   - Nothing more. Do not read other phases' specs beyond their titles in G5, other
     RESEARCH.md sections, or unlisted files unless you hit a genuine gap — and if you do,
     record what you read and why in your phase doc's header.
2. **Architect the implementation.** Produce the complete technical design for your
   subsystem: data models, algorithms, state machines, lifecycles, exact semantics, module
   placement per §G3, interfaces exposed to dependent phases. Where your spec assigns an
   open question (OQ-n), write a *spike specification* per §G4.4 — do not attempt to resolve
   the OQ yourself.
3. **Write your phase doc** to `Schmaloogium/PHASE_<N>_DOC.md` (N = your phase number, e.g.
   `PHASE_3_DOC.md`), following the mandatory template in §G9. Then **stop working**.

**Hard rules:**

- **No code.** No source files, no skeletons, no stubs, no build changes. The deliverable is
  the architecture document only. (Illustrative type/method *signatures inside the doc* are
  encouraged where load-bearing; compilable files are not.)
- **No verification, no review.** Do not run builds or tests, do not launch review/adversarial
  agents, do not screenshot-diff anything, and do not review your own doc beyond ordinary
  care. Verification is the separate verify session (§G1.2) — never yours.
- **No scope creep.** Anything your spec's *Scope — out* list names is owned by another
  phase; state the ownership in your doc §1 and move on.
- **Context discipline.** Finish below ~40% of a 1M-token context (~400k tokens). The
  mandatory reading for every phase is budgeted well under half of that; if you approach the
  ceiling mid-design, finish the doc at reduced detail and flag the gaps in doc §11 rather
  than overrun. If your spec names a fallback split (only Phase 7 has one), invoke it.
- **Decision recording.** Phase-local decisions get IDs `D-P<N>-<k>` (e.g. `D-P3-1`) with a
  one-line rationale each, logged in doc §11. Decisions that would contradict RESEARCH.md's
  D-1..D-10 are not yours to make — flag them instead.
- **Input contradictions** (doc-vs-doc, doc-vs-behavior — e.g. the App B.3 depthtex1 unit
  inconsistency) are *reported* in doc §3/§11 with your ruling and its provenance, never
  silently smoothed over.
- **Do not modify** RESEARCH.md, DESIGN.md, or another phase's doc. Propose changes to any
  of them in your doc §11 ("requested upstream changes").
- **Dependency docs are contracts.** What a dependency's PHASE doc exposes in its §5
  (cross-phase interfaces) is what you build against. If it is missing something you need,
  flag the request in your doc §5; do not invent the missing interface as if it existed.
- **Tools:** use the MCP `cleanroom` recipes (RESEARCH.md §12.4) for vanilla symbol
  resolution and platform docs. Web search only if a listed input is missing or
  contradictory — RESEARCH.md's web facts are same-day fresh.

### G1.2 Verify sessions

You are one fresh Claude Code session assigned to adversarially review one completed
`PHASE_<N>_DOC.md`. You must not be the session that wrote it, and you must not be given
the author's conversation context — a reviewer sharing the author's context inherits the
author's blind spots.

1. **Read** exactly what the build session was assigned: Part I, the phase spec,
   RESEARCH.md §0–§1, the spec's Required inputs, and the dependency PHASE docs — then the
   phase doc under review. Same reading list, independent eyes.
2. **Attack the doc.** Your posture is refutation, not summary. Checks, in priority order:
   - **Doc gate** — every criterion in the spec's *Doc gate* met, literally.
   - **Conformance-map audit** — zero unmapped in-scope contract rows; spot-check mapped
     rows *against the cited RESEARCH.md/App text* for semantic fidelity (the
     depthtex1-unit-11 class of error is exactly what you exist to catch).
   - **Interface honesty** — everything the doc consumes from dependencies actually exists
     in their §5; everything promised to dependents is specified, not gestured at.
   - **Scope discipline** — nothing designed that the spec's *Scope — out* assigns
     elsewhere; nothing from *Scope — in* silently dropped.
   - **Template completeness** — all thirteen G9 sections present and substantive; every
     assigned OQ carries a full spike spec (question/procedure/criteria/fallback, §G4.4).
   - **Binding decisions** — no D-1..D-10 contradicted; no contract-visible component
     "improved" (§G4.2).
3. **Write** `PHASE_<N>_REVIEW.md` (repo root, next to the phase doc): a findings list
   (each finding: location, claim, evidence, severity **blocking / correction / note**)
   and exactly one verdict — **PASS**, **PASS-WITH-CORRECTIONS**, or **FAIL**. Reserve
   FAIL for structural misses that require rebuilding the doc; fixable defects are
   PASS-WITH-CORRECTIONS. Then **stop** — do not fix anything yourself.

Verify sessions obey the build-session hard rules (no code, no scope creep, context
discipline). Context budget: the build session's mandatory reading plus the phase doc
itself — comfortably inside the same ceiling.

### G1.3 Fix-up sessions and the "verified" state

- On **PASS-WITH-CORRECTIONS**: a fresh session reads the phase spec, the phase doc, and
  the review file; applies the corrections to `PHASE_<N>_DOC.md`; and records each
  resolution in the review file under a `## Resolutions` heading. On **FAIL**: rerun the
  build session with the review file added to its Required inputs.
- **Re-verify only if §5 changed.** If corrections altered the doc's *Cross-phase
  interfaces* section, the doc goes through a fresh verify session before any dependent
  consumes it; otherwise the fix-up closes the phase.
- A phase is **verified** when its latest review verdict is PASS, or PASS-WITH-CORRECTIONS
  with all resolutions recorded and no §5 change outstanding. Only verified docs are valid
  dependency inputs (§G5.3 invariant).

---

## G2. Mission and constraints digest

**Mission (D-2, RESEARCH.md §1.1):** OptiFine/Iris-format shader-pack support for Cleanroom
clients on MC 1.12.2. Just shaders. Nothing else. The non-goals list (§1.2) is binding;
scope creep must argue against that written record.

### G2.1 The decision log (binding on every phase)

Condensed from RESEARCH.md §1.3 — phase docs honor these or flag, never silently overturn:

| ID | Decision (short form) |
|---|---|
| D-1 | Cleanroom-exclusive |
| D-2 | Shaders only; written non-goals list (§1.2) |
| D-3 | Target = the fixed pack-compatibility matrix (App G), not "Iris parity" |
| D-4 | Stage registry architected for the full modern stage set from day one; wires a subset |
| D-5 | Mixin-based hooks only; no class replacement; ~25–30 targeted injections (App E) |
| D-6 | Engine-core / loader-glue seam; core headless-testable and GL-abstracted |
| D-7 | GPL-3.0-or-later license (template's MIT-style LICENSE must be replaced) |
| D-8 | Published docs + OSS source OK; LGPL-3.0 reuse with compliance; two prohibitions: glsl-transformer (AGPL) never-copy, OF decompile behavioral-observation-only |
| D-9 | Compatibility-profile GL baseline; no core-profile rewrite |
| D-10 | Conformance harness from week one |

### G2.2 Hard constraints

- **Compat profile is mandatory** (D-9, §6.1): packs are GLSL-120-era fixed-function-coupled
  code. LWJGL3's value is modern entry points *within* compat. `GL_QUADS` stays available;
  keep the triangle-strip composite fallback anyway.
- **No UBOs for the pack contract** (§1.2, §6.1): packs declare default-block uniforms;
  per-program upload with location caching is structurally required.
- **Cooperate with fixed-function state**: vanilla renders through FF matrices, client
  arrays, and display lists; matrix uniforms are captured from the FF stack at defined
  moments (§4.4).
- **LWJGL3-native code only**: never compile against `org.lwjglx` (runtime-only shim, itself
  in flux — OQ-21).
- **Alpha-platform drift**: loader/toolchain versions are pinned by Phase 1 and re-verified
  deliberately, never floated.

### G2.3 Threading model

The render thread owns all GL. Permissible off-thread work: pack file discovery/parsing,
preprocessing, expression compilation (Phase 11), and — only via the Phase 14 shared-context
design with its mandatory synchronous fallback — shader compilation and texture upload.
Everything else runs on the render thread. Phase docs state thread ownership for each
component (doc §7).

### G2.4 Failure philosophy: never crash the client

Graceful degradation is contract-adjacent behavior packs implicitly rely on (§4.7). The
degradation ladder, top to bottom:

1. A custom uniform that errors at runtime disables *that uniform* only.
2. A built-in uniform whose GL upload errors disables *that uniform* only.
3. A program failing compile/link/validate deletes itself, reports a user-visible error, and
   resolves through the backup chain (App A.2).
4. A capability gate failing at init turns the pack off gracefully with a chat error.
5. Nothing in the shader engine ever crashes the client or corrupts the vanilla framebuffer
   path. Shaders-off must always be a reachable state.

Every phase doc has a §6 mapping this ladder onto its subsystem.

### G2.5 Performance posture

Clean code first, optimize with evidence (§6.3): modern GC removes OF's allocation-discipline
constraint; do not replicate array caches / mutable-pose machinery. Initial performance is
allowed to be worse than OF-with-shaders (§2.4); optimization happens inside our own pipeline
only (Phase 14), never by drifting into the §1.2 non-goals.

---

## G3. Architecture overview

### G3.1 Module layout

Three Gradle projects, enforcing the D-6 seam structurally (Phase 1 refines names and build
mechanics; the *seam itself* is not refinable — §5.2 makes it strategically load-bearing,
because Kirino-Engine may replace the entire render backend under us):

```
:engine       Pure-JVM engine core. Java 25. ZERO dependencies on Minecraft, Forge,
              Cleanroom, Mixin, or LWJGL. Testable headless via JUnit alone.
  engine.pack        pack discovery, file model, dimension folders, sources
  engine.preprocess  #include, macro header, preprocessor, option discovery/rewrite
  engine.config      shaders.properties model, options/profiles/screens, ID-file grammar,
                     persistence formats
  engine.registry    stage registry (modern superset shape), program slots, backup chains,
                     per-program state
  engine.buffers     framebuffer/color-buffer policy: ping-pong, flips, clears, formats,
                     sizing (policy only — GL objects live behind the facade)
  engine.uniforms    built-in uniform model, cadences, smoothing math, value-provider
                     interfaces (implementations live in :mod glue)
  engine.expr        custom-uniform expression language
  engine.gl          the GL facade: interfaces + a GLCapabilityProfile value object +
                     a recording/replay implementation for headless tests

:mod          The Cleanroom mod. Depends on :engine.
  mod.core           @Mod entry, lifecycle, config, engine bootstrapping
  mod.glue           adapters: world-state sampling, Forge registries, resources,
                     the LWJGL3 implementation of engine.gl
  mod.mixin          all Mixin classes (SRG-targeted, MixinConfigs manifest attribute)
  mod.gui            pack selection + options screens (ModularUI)
  mod.compat         coexistence detection (chunk-renderer replacers etc.), bail registry

:conformance  The Phase 2 harness: scene specs, capture drivers, image diff, fixture
              downloader, headless golden-run tests of :engine against recorded
              GLCapabilityProfiles.
```

Dependency rule, stated as a testable constraint: **`:engine` compiles with no classpath
entry from Minecraft/Forge/Cleanroom/Mixin/LWJGL, and `:mod` never reaches into `:engine`
internals beyond its published interfaces.** Phase 1 specifies the enforcement mechanism.

### G3.2 One-frame data flow (orientation only; normative detail in §4.4)

```
frame begin ─ sample world state ─ snapshot prev-frame camera/matrices
  → SHADOW PASS (Phase 8) → bind main FBO, clear per rules (Phase 5)
  → GBUFFERS phases with per-phase program dispatch (Phases 4/7)
  → depth copy → DEFERRED ping-pong passes → translucent terrain → hand
  → COMPOSITE ping-pong passes → FINAL to screen (Phase 7)
```

Uniforms refresh at every program switch (Phase 6); vertices carry the extended attributes
(Phase 10) stamped with alias IDs (Phase 9); every source line the GL sees passed through
the Phase 3 front-end; every buffer it writes is Phase 5's.

### G3.3 Where the mixins live

All vanilla-touching code is `mod.mixin` + `mod.glue`, targeting the App E class/method
catalog. Mixins stay *dumb*: they observe state, delegate to engine entry points, and never
contain policy (the reference's "use program is the universal state barrier; hook sites stay
dumb" rule, §4.2, adopted as our own).

---

## G4. Cross-phase contracts

### G4.1 Vocabulary

Pack-facing terms are used **verbatim** in code, docs, and identifiers: colortex, flip,
backup chain, gbuffers, deferred, composite, shadowcomp, `mc_Entity`, `at_tangent`, dfb/sfb,
etc. — the App H glossary is the project dictionary. Do not invent synonyms; a reader
holding `doc/shaders.txt` must recognize every name.

### G4.2 "Contract-visible" components

A component is **contract-visible** when packs can observe its behavior (everything in
RESEARCH.md §3 and Apps A–D, F). Contract-visible designs must match the cited contract
exactly, cite their provenance row, and may not "improve" semantics (flip quirks, clear
colors, unit numbers, fallback chains are all load-bearing). Non-contract internals may be
modernized freely (§4.8 Keep/Adapt/Skip is the map).

### G4.3 Milestone tags

Every designed component carries exactly one tag: `v0.1` … `v0.5`, or `post-v0.5`. The tag
means "implemented at that milestone", per the §G0.3 principle. Phase docs aggregate tags in
their §9 so the implementation effort can be sliced by milestone across phases.

### G4.4 OQ / spike protocol

Phases do not resolve open questions; they specify **spikes** for the implementation effort.
A spike spec contains: (1) the question, verbatim from RESEARCH.md §11; (2) a concrete
procedure (what to build/run/measure, in dev env or CI); (3) success and failure criteria;
(4) the fallback design if the spike fails — designed *now*, so a failed spike never stalls
a milestone. Spike results are recorded back into RESEARCH.md §11 (status column) by the
implementation effort, plus an addendum note in the owning phase doc.

### G4.5 Error taxonomy and logging

Three user-facing channels: chat errors (pack-level failures, capability gates), the shader
GUI (per-program compile errors, per RESEARCH.md §4.7), and the log. Log channels are
per-subsystem (`schmaloogium.pack`, `.compile`, `.frame`, `.gl`, …; Phase 1 fixes the list).
Debug affordances reserved from day one: `-Dschmaloogium.debug.saveSources` (the
`shaders.debug.save` equivalent, App F.8) and KHR_debug labels/groups in dev (Phase 14).

### G4.6 GL discipline

All engine GL goes through the `engine.gl` facade — no direct LWJGL calls outside
`mod.glue`'s facade implementation. Cooperation with `GlStateManager` is one-directional:
we *observe* its state (e.g. `blendFunc` uniform) and restore what we perturb; we never
bypass it for state it caches (the cache would go stale and break vanilla rendering).
Per-program alpha/blend overrides lock state for the program's duration and release it
after, per §4.2.

---

## G5. Phase index and dependency graph

### G5.1 The 14 phases

| # | Phase | One-line scope | Milestone | Depends on | OQs |
|---|---|---|---|---|---|
| 1 | Foundation & project architecture | Template → real project: modules, seams, Mixin wiring, license, pins, test scaffolding | v0.1 | — | OQ-2, OQ-12, OQ-20 (seam), OQ-21 |
| 2 | Conformance harness | T0–T3 machinery, scenes, diffing, fixtures, CI viability | v0.1 (design) | 1 | OQ-10 |
| 3 | Pack front-end | Discovery → preprocessing → options → shaders.properties → validated PackConfiguration | v0.1 | 1 | OQ-7 (arch) |
| 4 | Stage/program registry & compilation | Modern-superset stage registry, 43 slots, backup chains, compile/link | v0.1 | 1, 3 | — |
| 5 | Framebuffer & buffer architecture | Main/shadow FBOs, ping-pong/flips, clears, formats, sizing, resize | v0.1 | 1, 3, 4 | — |
| 6 | Uniform & sampler system | App D inventory, cadences, smoothing, unit map, value providers | v0.1 | 1, 3, 4 | — |
| 7 | Render-loop integration | Frame driver + the full Mixin hook catalog; v0.1 assembly | v0.1 exit | 2, 3, 4, 5, 6 | OQ-3, OQ-4 |
| 8 | Shadow pass | Celestial camera, texel snap, traversal, depth split, PCF | v0.2 | 4, 5, 6, 7 | — |
| 9 | ID aliasing & per-draw dynamics | Alias resolution, per-mod merge, held-item, entity/TE id uniforms | v0.3 | 3, 6, 7 | — |
| 10 | Extended vertex pipeline | 56-byte format, per-quad attributes, entity stack, both draw paths | v0.3 | 4, 7, 9 | OQ-5, OQ-14 |
| 11 | Custom-uniform expression engine | Full F.6 language, evaluator, error isolation | v0.4 | 3, 6 | — |
| 12 | Options GUI, persistence & reload | ModularUI screens, sliders, profiles, persistence, reload paths | v0.4 | 1, 3 (soft: 7) | OQ-9 |
| 13 | Texture systems | `_n`/`_s` atlases, noise, custom textures, atlasSize | v0.5 | 3, 5, 7 | — |
| 14 | GL modernization & performance | Sampler objects, async readback/compile, KHR_debug, GC posture | v0.5 + QoL | 5, 6, 7, 13 | OQ-15, OQ-22 |

"Depends on" is literal: those phases' `PHASE_<i>_DOC.md` files are declared inputs — and
per the §G5.3 invariant they must be **verified** docs (§G1.3 definition), not merely
written. Phase 12's dependency on 7 is **soft** — it needs only Phase 7's
*reload-lifecycle* section; if PHASE_7_DOC.md is not yet verified, Phase 12 may run
against Phase 7's spec in this document and flag the assumption.

### G5.2 Execution waves with the build → verify cadence

Each wave builds its phases (in parallel), then verifies them (in parallel), before
dependent waves start:

```
Wave 0:  build P1                          → verify P1
Wave 1:  build P2 ∥ P3                     → verify P2 ∥ P3
Wave 2:  build P4                          → verify P4
Wave 3:  build P5 ∥ P6                     → verify P5 ∥ P6
Wave 4:  build P7 ∥ P11                    → verify P7 ∥ P11   (P11 needs only P3+P6)
Wave 5:  build P8 ∥ P9 ∥ P12 ∥ P13 ∥ P14   → verify all five   (only P9's verdict gates Wave 6)
Wave 6:  build P10                         → verify P10
Final:   integration review (§G5.3 item 4)
```

The strictly ordered spine is P1 → P3 → P4 → {P5, P6} → P7. Note the deliberate inversions
against milestone order: the harness (P2) is designed *first* (D-10), and aliasing (P9)
precedes the vertex pipeline (P10) because alias resolution is upstream of `mc_Entity`
stamping.

### G5.3 Workflow invariants

1. **The gating invariant: a phase doc must be verified (§G1.3 definition) before any
   dependent build session reads it.** This is the entire point of the cadence: an
   unverified error in a high-fan-out doc propagates into every dependent and forces their
   rebuilds (P3 feeds eight phases; P1 feeds everything; the leaves feed nothing).
   Verification urgency is proportional to fan-out — highest early, lowest at the leaves.
2. **Waves are a schedule, not a barrier.** A build session may start as soon as *its own*
   dependencies are verified — full completion of the prior wave is sufficient but not
   necessary (e.g. Wave 6 waits only on P9's verification, not on all of Wave 5's; P11 may
   start the moment P3 and P6 are verified, regardless of P7).
3. **The one sanctioned exception** is Phase 12's soft dependency on Phase 7 (§G5.1):
   Phase 12 may build against Phase 7's spec in this document, flagging the assumption. If
   Phase 7's verified doc later contradicts that assumption, Phase 12's doc gets a fix-up
   session (§G1.3), not a rebuild.
4. **Final integration review.** After all 14 phases are verified, one additional fresh
   session reads every phase doc's §1 (scope & boundaries) and §5 (cross-phase interfaces)
   plus each doc's §11 hand-off items, and audits them *against each other* — the sibling
   drift that per-phase verification structurally cannot catch. Checklist: consumed-vs-
   exposed interface symmetry across every dependency edge in §G5.1; shared-ownership
   seams (the P5/P6 texture-unit-map split; P7's App E hook rows deferred to P10/P13 and
   actually picked up there; P3's engine-flag ownership map fully claimed by its owners);
   orphaned §11 hand-offs that no later phase adopted. Deliverable:
   `PHASE_INTEGRATION_REVIEW.md`, same findings format and verdict rules as §G1.2, with
   fix-ups per §G1.3. **Implementation starts only after this review's findings are
   resolved.**

---

## G6. Conformance and testing summary

Full strategy: RESEARCH.md §8. What every phase must know:

- **Definition of done is the pack matrix** (D-3, App G): 3 classic packs (SEUS Renewed,
  Chocapic13 V9, projectLUMA) + 4 dual-spec packs (BSL, Complementary ×2, Sildur's).
  Tiers: **T0** loads · **T1** renders plausibly (self-baseline regression) · **T2**
  pixel-parity vs OptiFine G6 (classic packs only) · **T3** feature-complete per pack.
- **Testability is sliced three ways:**
  1. *Per-phase headless tests* — every phase doc's §8 specifies JUnit tests of its
     subsystem against the `engine.gl` facade / recorded `GLCapabilityProfile`s. This is
     where the D-6 seam pays rent (§8.3): pack sources run through the front-end and
     resource-sizing decisions validate with no GL context.
  2. *The Phase 2 harness* — scenes, capture, diffing, fixtures, CI. Designed in Wave 1,
     before any renderer exists; its "runnable-before-renderer subset" (fixture downloader,
     preprocessor golden runs, capability-profile replay) is what D-10's "week one" means
     for the implementation schedule.
  3. *Tier gates as implementation exit criteria* — behavioral phases carry §9-derived impl
     gates expressed as Phase-2-defined harness runs.
- **Fixture licensing is settled** (resolved OQ-11): no matrix pack may ever be committed or
  re-hosted. CI downloads at test time (Modrinth API version IDs where available) with a
  local cache; SEUS/Chocapic/projectLUMA are canonical-download-only.
- T1 baselines are hand-approved once v0.1 first renders, then become the regression oracle.
  T2's oracle is OptiFine G6 screenshots captured manually outside CI (Phase 2 defines the
  protocol).

## G7. Licensing and legal rules for sessions

Binding on every session (from D-7/D-8, RESEARCH.md §10):

1. **Schmaloogium is GPL-3.0-or-later.** Phase 1 executes the license swap (the template
   ships an MIT-style LICENSE).
2. **The schlorbium repo is behavioral-observation-only.** Sessions reading
   `schlorbium-project/SHADER_ENGINE_IMPL.md` / `DESIGN.md` may restate *behavior* in their
   phase docs, never code structure, class/method names, or identifiers from the decompile.
   The decompiled sources themselves (`net/schlorbium/…`) are last-resort reading when the
   digests are silent — and never a code source.
3. **The contract sources are legally clean**: `schlorbium-project/doc/shaders.txt` and
   `doc/shaders.properties` are the shipped pack-author docs; upstream `sp614x/optifine`
   OptiFineDoc and Iris/shaderLABS docs are published specs. Cite freely.
4. **Iris and Angelica may be read, and their LGPL-3.0 code incorporated** with compliance
   (notices preserved, modifications marked; LGPL-3.0 combines into GPL-3.0-or-later).
   **Exception: Iris's bundled glsl-transformer is AGPL-3.0 — never copy from it.**
5. Dependency digest: Cleanroom platform LGPL-2.1; ModularUI LGPL-3.0 (mod-dependency or
   jar-in-jar both fine under GPL-3.0-or-later; OQ-12 note lives with Phase 1);
   Mc122RenderBook MIT (test-harness patterns freely usable); Kirino-Engine custom license —
   observe API surface only.
6. Matrix shader packs: per-pack licenses, all prohibit bundling (App G) — §G6 fixture
   policy applies.

## G8. Post-v0.5 provisional roadmap (sketch — NOT phases, no sessions)

*This section is deliberately non-binding. It exists so pre-v0.5 phases can shape
architecture for growth (D-4, §7.3–§7.4) without designing the future in detail. A DESIGN.md
revision after v0.5 conformance data exists will turn this into real phases. Source:
RESEARCH.md §3.6, §9 "post-v0.5" row, §11.*

Provisional slices, in likely order:

- **S1 — Modern pass arrays & buffer growth:** wire shadowcomp/prepare/begin/setup into the
  (already superset-shaped) stage registry; `/* RENDERTARGETS */`; colortex to 16 (then 32);
  shadowcolor2–7. Mostly "turning on" what P4/P5 sized for.
- **S2 — Compute & storage:** `.csh` + `_a…_z` dispatch slots, work-group directives,
  colorimg/shadowcolorimg, custom images, SSBOs, indirect dispatch. GL 4.2/4.3-floor-gated
  per pack; excludes macOS/old GPUs (§3.6.8).
- **S3 — Identity & capability negotiation:** finalize OQ-7 (leaning option 3: OF-era
  identity + honest Iris-style feature flags + own `SCHMALOOGIUM` macro + per-pack
  overrides — the P3 identity-set architecture makes this a configuration decision, not a
  rewrite); era-bridge experiments for `#version 150+` packs on the compat context (OQ-18).
- **S4 — Modern geometry data:** `at_midBlock` first (the P10 growth design's canonical
  test), then `renderStage` instrumentation, translucent-split programs /
  `separateEntityDraws` feasibility review against §3.6.8's risk table.
- **S5 — Kirino backend:** if/when Kirino-Engine ships (OQ-20 — the highest-weight
  strategic risk), port `mod.glue`/`mod.mixin` to Kirino passes behind the D-6 seam. Track
  via §7.7 upstream engagement (participate in CleanroomMC Discussion #405 with our hook
  requirements as the consumer use-case). Local sketch material: `cleanroom-src/projects/kirino`.
- **Dual-spec matrix packs** (current BSL/Complementary/Sildur's releases) progress to T1+
  across S1–S4.

## G9. PHASE_X_DOC.md — mandatory template

Every phase doc uses exactly this skeleton (sections may be short, never absent):

```
# Schmaloogium — Phase <N>: <title> — Architecture

0. Header — phase name; date; inputs ACTUALLY read (paths + RESEARCH.md sections);
   dependency PHASE docs consumed; deviations from the assigned reading list, with reasons.
1. Scope & boundaries — what this phase owns; an explicit "owned by Phase Y" line for every
   adjacent concern touched (the anti-sprawl device).
2. Architecture overview — responsibilities, placement in the G3 module layout, key types
   and their relationships (prose + signatures where load-bearing).
3. Contract conformance map — a table: every in-scope contract item (RESEARCH.md §3/App
   row) → the design element satisfying it → provenance tag. ZERO unmapped rows; any
   deviation is a flagged decision, never silent.
4. Detailed design — per component: data model, algorithms, state machines, lifecycle,
   exact semantics. The bulk of the document.
5. Cross-phase interfaces — what this phase EXPOSES to dependents (named interfaces / data
   contracts) and CONSUMES from dependencies; requested changes to a dependency's doc are
   flagged here, never silently assumed.
6. Failure modes & degradation — the G2.4 ladder applied to this subsystem, case by case.
7. Threading & performance notes — thread ownership per component; allocation posture;
   known hot paths.
8. Testability plan — headless unit tests (facade / GLCapabilityProfile based), fixtures,
   and which conformance-tier runs exercise this subsystem.
9. Milestone staging — component → milestone tag table; what is architected-now /
   implemented-later.
10. OQ & spike specifications — per assigned OQ: question, spike procedure, success/failure
    criteria, fallback design (the G4.4 protocol).
11. Decisions & open items — phase-local decision log (D-P<N>-k + rationale); input
    contradictions found; items handed to later phases or to G8; requested upstream changes.
12. Implementation checklist — ordered, independently actionable work items for future
    coding sessions, each with milestone tag and test hook.
```

## G10. Open-question assignments

Every open OQ (RESEARCH.md §11) has exactly one owner. Resolved rows listed for
completeness — do not re-litigate them.

| OQ | Status | Owner | Handling |
|---|---|---|---|
| OQ-1 | resolved | — | Sanctioned render API is in-progress (Kirino); see OQ-20 |
| OQ-2 | resolved, standing | **P1** | Re-verify current loader release; pin deliberately |
| OQ-3 | open | **P7** | Spike spec: GL context creation / HiDPI / resize under Cleanroom |
| OQ-4 | open | **P7** | Spike spec: CleanMix divergences on hot render-path injections |
| OQ-5 | open | **P10** | Coexistence policy design (detect-and-bail first) |
| OQ-6 | resolved | — | Modern pass semantics documented (§3.6) |
| OQ-7 | open | **P3** (architecture) + **G8/S3** (final decision) | Identity set configurable from day one |
| OQ-8 | mitigated | — | Conformance tiers (§8.2) |
| OQ-9 | open | **P12** | Spike spec: ModularUI fitness for generated screens |
| OQ-10 | open | **P2** | Spike spec: headless GL in CI |
| OQ-11 | resolved | — | Download-at-test-time fixture policy (§G6) |
| OQ-12 | open | **P1** | Short considered licensing note in PHASE_1_DOC |
| OQ-13 | resolved | — | Modern attributes documented (§3.6.5 / App C.3) |
| OQ-14 | open | **P10** | Spike spec: baked-quad/LightUtil caches on format switch |
| OQ-15 | open | **P14** | Spike spec: shared-context async compile; sync fallback mandatory |
| OQ-16 | resolved | — | Backport graveyard mapped (§2.2) |
| OQ-17 | open | none (messaging only) | Not load-bearing for design; revisit at release marketing |
| OQ-18 | open | **G8/S3** | Era-bridge experiments need a working v0.1 |
| OQ-19 | resolved | — | Iris is LGPL-3.0 (+AGPL caveat) |
| OQ-20 | open | **G8/S5** + **P1** (seam hardness requirement) | Highest-weight strategic risk |
| OQ-21 | open | **P1** | lwjglx flux: compile LWJGL3-native; track LWJGLXX/LWJGLY |
| OQ-22 | open | **P14** | Spot-check ledger for the §6.2/§6.3 modernization claims |

---

## Part II — Phase specifications

*Each spec is the complete assignment for one session. Format: Objective · Deliverable ·
Scope (in / out) · Required inputs · Architecture requirements · Doc gate (what makes the
phase doc complete) · Impl gate (the §9-derived criterion recorded for later coding
sessions) · Context budget.*

---

### Phase 1 — Foundation & project architecture

**Milestone:** v0.1 · **Depends on:** — · **OQs:** OQ-2, OQ-12, OQ-20 (seam hardness), OQ-21

**Objective.** Turn the pristine CleanroomModTemplate into Schmaloogium's real project
architecture: the module split that enforces the D-6 seam, the GL facade, Mixin wiring,
license, version pins, and the conventions every later phase builds on.

**Deliverable.** `PHASE_1_DOC.md` per §G9.

**Scope — in:**
- Template conversion plan: rename `com.example.modid` → final root package/mod id
  (propose `schmaloogium`); `gradle.properties` fields; `Reference.java` templating;
  `mcmod.info`/`pack.mcmeta`; AT file rename (decide whether ATs are needed at all for
  v0.1 — prefer none until a hook requires one).
- **License swap to GPL-3.0-or-later** (D-7): LICENSE file, source-header convention,
  `mcmod.info` metadata.
- **Version pins** (OQ-2): re-verify the current Cleanroom loader release (template pins
  0.5.17-alpha; 0.6.6-alpha was current 2026-07-24 — daily cadence, so check again), pin it
  and the Unimined kappa/Gradle/Java-25 toolchain deliberately; document the re-pin
  procedure.
- **Gradle module split** per §G3.1 (`:engine`, `:mod`, `:conformance`): concrete
  `settings.gradle`/`build.gradle` restructuring plan against the template's actual build
  scripts (`gradle/scripts/{dependencies,extra,publishing}.gradle`), including how the
  Unimined/Blossom machinery stays confined to `:mod`, jar packaging (does `:engine` shade
  into the mod jar via the template's `contain` configuration?), and the seam-enforcement
  mechanism (e.g. no-MC-on-compile-classpath by construction, plus an architecture test).
- **`engine.gl` facade design**: interface granularity (thin GL-verb layer vs. grouped
  services), the `GLCapabilityProfile` value object (GL version, max draw buffers, max
  color attachments, max texture units, extension set — the §4.1 probe set), and the
  recording/replay implementation for headless tests.
- **Mixin wiring**: `MixinConfigs` jar-manifest declaration (the current canon — legacy
  MixinBooter interfaces are deprecated, §5.1), mixin-config JSON layout (one per phase
  needed?), SRG-name targeting policy, refmap handling under Unimined, dev ergonomics
  (`crl.dev.mixin`, `-Dmixin.debug.export=true`).
- **lwjglx posture** (OQ-21): our code compiles against LWJGL3 proper only; document what
  `enable_lwjglx=true` in the template means for runtime and whether we keep it.
- Headless JUnit baseline in `:engine`/`:conformance` (JUnit is template-wired).
- Logging channels (§G4.5 list), debug-flag names (reserve
  `-Dschmaloogium.debug.saveSources`), user-facing error channel conventions.
- `mod.compat` **bail registry stub**: the mechanism for "detected incompatible
  chunk-renderer replacement → disable shaders with a clear message" (policy content comes
  from Phase 10 / OQ-5; the mechanism slot exists from day one).
- CI workflow adjustments (the template's three GitHub workflows) for the module split and
  `:conformance` (test stages land with Phase 2's design; leave extension points).
- OQ-12 note: a short considered paragraph on GPL-3.0-or-later mod + LGPL-2.1 platform +
  LGPL-3.0 ModularUI (jar-in-jar), citing ecosystem precedent.

**Scope — out:** harness content (Phase 2); anything pack-format (Phase 3); all GL policy
beyond the facade shape (Phases 5+); GUI framework evaluation (Phase 12).

**Required inputs:**
- RESEARCH.md §1, §5.1–§5.3, §6.1, §7.2, §12.2.
- Template ground truth: `Schmaloogium/{build.gradle,gradle.properties,settings.gradle}`,
  `Schmaloogium/gradle/scripts/*.gradle`, `Schmaloogium/src/**`, `Schmaloogium/.github/workflows/*`,
  `README.md`.
- Skim only: `cleanroom-src/src/main/java/com/cleanroommc/` (boot/mixin bootstrap layout).
- MCP recipes: `get_porting_guide("mixin-setup")`, `get_project_template(...)` (§12.4).

**Architecture requirements:** the seam is non-negotiable (§G3.1) and must be stated as a
testable constraint; everything else in G3 is yours to refine (and later phases inherit
your refinement). Honor D-5/D-6/D-7 explicitly. Kirino (§5.2) is the reason seam-hardness
is a requirement, not hygiene — record it.

**Doc gate:** module/package layout finalized with dependency rules as testable
constraints; every D-1..D-10 either satisfied by this phase or explicitly deferred with its
owner phase named; pin table complete with re-verification procedure.

**Impl gate:** project builds empty modules + passes an architecture test proving `:engine`
has no MC/loader/mixin/LWJGL classpath; CI green.

**Context budget:** mandatory reading ≈ 30k tokens (Part I + spec + template files +
RESEARCH.md sections). Ample headroom; do not spend it exploring cleanroom-src beyond the
listed skim.

---

### Phase 2 — Conformance harness

**Milestone:** v0.1 (design; implementation starts week one) · **Depends on:** 1 · **OQs:** OQ-10

**Objective.** Design the conformance machinery that defines "done" for every other phase
(D-3, D-10): tiers, scenes, capture, diffing, fixtures, and the headless-core test harness.

**Deliverable.** `PHASE_2_DOC.md` per §G9.

**Scope — in:**
- **Tier machinery** (T0–T3, §8.2): what each tier measures, per-pack tier state tracking,
  reporting format.
- **Fixed-scene specification**: a scene = seed + coordinates + time + weather + camera
  path (§8.3); the file format for scene definitions; the initial scene set (terrain,
  water/translucency, night/shadows, weather, hand/held-item — one scene per behavioral
  family the matrix packs exercise).
- **Capture automation**: world load, deterministic setup (fixed seed/time/weather,
  disable randomness sources), camera placement, screenshot capture — designed against
  vanilla/Forge client APIs (designable now; runnable once v0.1 renders).
- **Image diff**: tolerance model (per-pixel + aggregate thresholds; GPU/driver variance
  allowance), baseline storage/versioning (T1 self-baselines are hand-approved once, then
  regression oracles), diff-report artifacts.
- **T2 protocol**: capturing OptiFine G6 reference renders on the same scenes — manual,
  outside CI, documented step-by-step (OF is not redistributable; the oracle images are
  local artifacts).
- **Fixture system** (resolved OQ-11 policy, §G6): download-at-test-time via Modrinth API
  (stable version IDs for BSL/Complementary/Sildur's), canonical-URL manual placement for
  SEUS/Chocapic/projectLUMA, local cache layout, never-rehost rule enforced structurally
  (cache outside the repo, CI cache keys).
- **Headless-core harness**: golden-run tests feeding matrix-pack sources through the
  Phase 3 front-end against recorded `GLCapabilityProfile`s; validating resource-sizing
  decisions with no GL context (§8.3). Define the golden-file format and update workflow.
- **RenderBook JUnit OpenGL Extension evaluation plan** + **OQ-10 spike spec**: does
  headless/EGL/virtual-display GL work on CI runners? Success/failure criteria; fallback =
  GL-context tests run locally/pre-release only, CI stays headless-core + parse tiers.
- **§9 exit-criteria mapping**: every milestone exit criterion in RESEARCH.md §9 → the
  concrete harness run(s) that gate it.
- Identify the **"runnable before any renderer exists" subset** — this is what the
  implementation effort builds in literal week one.

**Scope — out:** the front-end being tested (Phase 3); scene *content* tuning per pack
(implementation-time); performance benchmarking (Phase 14).

**Required inputs:**
- RESEARCH.md §8 (whole), §9 (exit-criteria column), App G, §5.1 (RenderBook bullet),
  §12.5 (Mc122RenderBook URL).
- `PHASE_1_DOC.md` (module layout, facade, `GLCapabilityProfile`).
- `Schmaloogium/.github/workflows/*` (CI ground truth).
- Web (only if needed): Modrinth API docs for version-pinned downloads;
  `github.com/tttsaurus/Mc122RenderBook` for the JUnit extension's actual API.

**Doc gate:** every §9 exit criterion traceable to a specified harness run; fixture
licensing policy encoded structurally; the before-renderer subset explicitly listed;
OQ-10 spike spec complete with fallback.

**Impl gate:** fixture downloader + headless golden-run skeleton + scene-spec parser
implemented and green in CI before Phase 7's implementation lands (D-10).

**Context budget:** ≈ 35k tokens mandatory reading. Web fetches are bounded (two sites).

---

### Phase 3 — Pack front-end: ingestion, preprocessing & configuration model

**Milestone:** v0.1 · **Depends on:** 1 · **OQs:** OQ-7 (architecture only)

**Objective.** Everything from disk to a validated in-memory `PackConfiguration`: pack
discovery, source preprocessing, option discovery, directive scanning, and the complete
`shaders.properties` model. One coherent subsystem because the pieces are circular: the
properties file is itself macro-preprocessed, and option discovery happens in shader
sources. Pure `:engine` code — no MC types anywhere in this phase.

**Deliverable.** `PHASE_3_DOC.md` per §G9.

**Scope — in:**
- **Pack discovery** (§3.1, §4.1): `shaderpacks/` enumeration; folder and zip packs
  (nested-root tolerance, path sanitization); "OFF"/"(internal)" sentinels; per-dimension
  `world<id>` folders (world −128..128 scan; when present, shaders load *only* from there;
  empty folder disables shaders for that dimension; only `.vsh`/`.fsh` read from dimension
  folders).
- **Source model & `#include`** (§3.2): ≤10 deep, relative and `/`-absolute-from-shaders
  forms, `#line` bookkeeping for correct error attribution.
- **Standard macro header** (§3.5): the full identity set (`MC_VERSION` 10904-format,
  `MC_GL_VERSION`, `MC_GLSL_VERSION`, OS/vendor/renderer macros, on-demand
  `MC_<GL_extension>`, option macros) injected after `#version`. **Identity-set
  architecture for OQ-7**: the set is *configurable data*, shaped for §7.5 option 3
  (OF-era identity + capability macros + own `SCHMALOOGIUM` macro + per-pack overrides),
  with the final decision deferred to G8/S3 — design so the decision is a config change,
  not a rewrite.
- **Preprocessor** (§3.5): `#define/#undef/#ifdef/#ifndef/#if/#elif/#else/#endif`,
  `defined X` / `defined(X)`, macro substitution; applied to shader sources,
  `shaders.properties`, and ID-mapping files (standard macros only for the latter, §3.7).
- **Option discovery** (§3.3, App F.3): switch options (`#define NAME` / commented form;
  recognized only when the same file `#ifdef`s them), variable options
  (`#define NAME value // tooltip [values]`), the const-option whitelist, the
  ambiguity-disables rule, lang-file decoration model (`shaders/lang/*.lang`).
- **Option application** via source-line rewrite at compile time (§4.7).
- **Directive scanning** (complete App A.3 table): implicit resource declarations
  (uniform declarations sizing buffers — `shadowtex1` → second shadow depth buffer,
  `colortex7` → buffer count, `gdepth` → RGBA32F upgrade, `centerDepthSmooth` → readback
  enable), const directives, `DRAWBUFFERS`, vertex-attribute opt-ins, `countInstances`,
  geometry-shader legacy config — in all three syntactic forms (`const` declarations,
  `/* KEY:value */`, legacy `// KEY:value`).
- **Full `shaders.properties` parse** (App F.1–F.8): engine flags, `version.<mcver>` gate,
  custom-texture *specs* (all three F.5 source forms parsed into a model; loading is
  Phase 13), screens/profiles/sliders model, custom-uniform/variable declarations
  (captured as raw expression strings; the language is Phase 11), per-program render-state
  overrides (alphaTest/blend/scale/flip/enabled — stored; applied by Phase 4).
- **ID-mapping file grammar** (§3.7): `block/item/entity.properties` parse (long/short/
  property-matched/legacy id:meta forms, `layer.*` keys) into an unresolved model —
  *resolution against registries is Phase 9*.
- **Persistence formats**: per-pack `shaderpacks/<pack>.txt` (changed options only),
  global `optionsshaders.txt` equivalent — formats and read/write model; GUI is Phase 12.
  Tag: v0.1 is GUI-less — parsing and applying persisted options only.
- Processed-source debug dump (`-Dschmaloogium.debug.saveSources`, App F.8 equivalent).
- **Required output — the engine-flag ownership map**: a table assigning every App F.1
  flag to the phase that implements its behavior (e.g. `clouds`/`backFace.*`/`sun`/`moon`/
  `vignette`/`underwaterOverlay`/`rain.depth`/`beacon.beam.depth` → Phase 7;
  `shadowTranslucent` → Phase 8; `oldHandLight`/`dynamicHandLight` → Phase 9;
  `oldLighting`/`separateAo`/`frustum.culling` → owner named by you). Later phases wire
  their slice; the map is the master list.

**Scope — out:** program compilation (Phase 4); buffer allocation (Phase 5 — you produce
sizing *requirements*); uniform values (Phase 6); expression evaluation (Phase 11); GUI
(Phase 12); texture loading (Phase 13); alias resolution (Phase 9).

**Required inputs:**
- RESEARCH.md §3.1–§3.3, §3.5, §3.7, §4.1 (steps 2–3), §4.7 (options), §7.5, App A.3,
  App F (whole), App H.
- `schlorbium-project/doc/shaders.properties` (whole file, 489 ln — the annotated key
  reference).
- `schlorbium-project/doc/shaders.txt` — the sections on directives, macros, options, and
  ID mapping (skip program/uniform tables; those are Phases 4/6 inputs).
- `PHASE_1_DOC.md` (module layout, logging, debug flags).

**Architecture requirements:** pure `:engine`; every parse error follows the G2.4 ladder
(malformed directive → warn + ignore line, never abort the pack unless structurally
unusable); the `PackConfiguration` output object is the single source downstream phases
consume (registry configs, buffer requirements, uniform declarations, option state).

**Doc gate:** conformance map covers every App F key and App A.3 directive with zero
unassigned rows; flag-ownership map complete; identity-set architecture demonstrably
option-3-shaped with the decision still open; pure-core placement confirmed.

**Impl gate:** headless golden runs (Phase 2 harness) parse all seven matrix packs' sources
end-to-end without error; resource-sizing decisions match hand-verified expectations for
at least one classic pack.

**Context budget:** ≈ 45k tokens mandatory reading (largest front-end surface). Stay out
of `SHADER_ENGINE_IMPL.md` — §3/App F + shipped docs are the contract; you do not need
behavioral internals. Fallback if sprawling: none — this phase merged two concerns
deliberately; if the doc grows large, compress prose, not coverage.

---

### Phase 4 — Stage/program registry & compilation

**Milestone:** v0.1 · **Depends on:** 1, 3 · **OQs:** —

**Objective.** The engine's spine: a stage registry modeling the full modern pass sequence
(D-4) with the G6 five stages as instances, the 43-slot program registry with backup
chains, and the compile/link pipeline.

**Deliverable.** `PHASE_4_DOC.md` per §G9.

**Scope — in:**
- **Stage registry** (§7.3, §3.6.1 shape only): stage identity for the full superset
  (`setup → begin → shadow → shadowcomp → prepare → gbuffers → deferred → composite →
  final`); sparse pass arrays up to index 99; per-pass buffer read/write sets; flip
  bookkeeping slots; compute-dispatch placeholders (`.csh` + `_a…_z` — slots exist, wiring
  is G8/S2). **No hardcoded 16s or 8s anywhere.** The G6 era is a *configuration* of this
  mechanism: 1.12.2 wires shadow/gbuffers/deferred/composite/final with arrays populated
  to 15.
- **Program registry** (App A.1): all 43 slots incl. virtual `deferred_pre`/`composite_pre`
  (flip-control only); per-slot stage, backup parent, and per-program state (draw-buffer
  routing, composite-mipmap bitmask, instance count, alpha/blend overrides, render scale,
  flip config) — populated from Phase 3's `PackConfiguration`.
- **Backup-chain resolution** (App A.2): empty program inherits the *entire configuration*
  of the nearest non-empty ancestor; `shadow` never inherits; profile-disabled /
  `program.<name>.enabled=false` programs are treated as absent.
- **Compile/link flow** (§4.2): per-file — Phase 3 front-end output → compile → attach →
  pre-link attribute binding at fixed locations (`mc_Entity`=10, `mc_midTexCoord`=11,
  `at_tangent`=12) → link → validate. Core GL objects (`glCreateProgram` family) via the
  facade, not ARB entry points (§6.2).
- **Geometry shaders, dual-form** (§3.2, §6.2): GL 3.2 layout qualifiers *and* the legacy
  `GL_ARB_geometry_shader4` + `maxVerticesOut` declaration form — the preprocessor/scanner
  accepts both; internal translation strategy specified here.
- **Failure handling** (§4.7): validation failure → delete program, user-visible error,
  resolve through backup chain (ladder step 3).
- **"Use program" state-barrier contract** (§4.2): define the barrier's obligations
  (sampler re-point, uniform refresh, custom-uniform evaluation, per-program alpha/blend
  lock, shadow-pass force-selection override) as an *interface* — Phase 6 supplies the
  uniform machinery, Phase 7/8 invoke it. Hook sites stay dumb.
- Per-program state application semantics: DRAWBUFFERS routing validation, scale/flip
  storage, `countInstances` exposure to the pass executor (execution is Phase 7, tag v0.5).

**Scope — out:** FBO/texture objects (Phase 5); uniform values and upload (Phase 6); pass
*execution* and frame orchestration (Phase 7); shadow-pass camera (Phase 8).

**Required inputs:**
- RESEARCH.md §3.1, §3.6.1 (shape only), §4.1 (steps 4–5), §4.2, §7.3, App A (whole).
- `schlorbium-project/doc/shaders.txt` — the "Shader Programs" table section (program
  names, renders, fallback column).
- `PHASE_1_DOC.md` (facade, module layout), `PHASE_3_DOC.md` (`PackConfiguration`,
  scanned directives, per-program overrides).

**Architecture requirements:** registry lives in `engine.registry` (pure); GL touchpoints
only through the facade so registry logic is headless-testable against a
`GLCapabilityProfile`. Contract-visible: program names, fallback chains, attribute
locations, barrier semantics.

**Doc gate:** the registry demonstrably instantiates both the G6 table and the §3.6.1
superset without structural change (show both configurations); every App A.1 row mapped;
barrier contract fully specified as an interface.

**Impl gate:** headless tests — backup-chain resolution over synthetic packs matches App
A.2 semantics; a recorded-GL compile flow runs a classic pack's program set to "linked"
with zero unmapped directives.

**Context budget:** ≈ 35k tokens mandatory reading.

---

### Phase 5 — Framebuffer & buffer architecture

**Milestone:** v0.1 (shadow FBO structure now, wired at v0.2) · **Depends on:** 1, 3, 4 · **OQs:** —

**Objective.** The buffer estate: main FBO with ping-pong/flip semantics, clear rules,
formats, depth textures, the shadow FBO structure, sizing, and resize lifecycle.
Contract-visible almost end to end — packs depend on exact flip behavior.

**Deliverable.** `PHASE_5_DOC.md` per §G9.

**Scope — in:**
- **Main FBO ("dfb")** (§4.3, App B.1): up to 8 logical color buffers at G6 wiring, each
  backed by two textures (main/alt) for ping-pong; gbuffers read/write "main";
  deferred/composite read "main", write "alt", then **flip the buffers they wrote**;
  per-program flip overrides incl. the virtual `*_pre` programs (config from Phases 3/4);
  the last-writer-leaves-flip-enabled convention (App F.7).
- **Clear rules** (§4.3, App B.1): buffer 0 → fog color; buffer 1 → solid white (+
  `gdepth`-declared → RGBA32F upgrade); buffers 2–7 → transparent black; per-buffer
  `<buf>Clear`/`ClearColor` overrides; **clearing honors flip state (clears both sides
  when flipped)**.
- **Formats** (App B.4): the 37 internal formats, pixel formats/types, the integer
  pixel-transfer path for integer formats, incomplete-framebuffer fallback → recreate
  everything as plain RGBA (ladder-conformant, user-visible warning).
- **Depth textures** (App B.2): depthtex0 (real attachment), depthtex1/2 as copy targets —
  the copy *moments* are Phase 7's; you own the textures, copy mechanics, and their
  lifecycle.
- **Shadow FBO ("sfb")** (§4.3): created only when shadow buffers are used (Phase 3 sizing
  output); ≤2 depth (+ optional hardware-PCF compare mode), ≤2 color, at
  `shadowMapResolution` × shadow-quality multiplier; per-texture nearest/mipmap filter
  config. Structure and lifecycle here; pass wiring is Phase 8.
- **Sizing** (§4.3): display size × render-quality multiplier; `superSamplingLevel`;
  resize/recreate lifecycle (display resize, multiplier change, pack change → uninit/init,
  §4.1 step 5's uninit triggers as they affect buffers).
- **Growth posture** (§3.6.3, D-4): data model addresses buffers by index without
  hardcoded 8/16 caps (16/32 colortex, shadowcolor2–7 are G8 wiring, not new architecture);
  `Final` renders to the vanilla framebuffer (handoff contract with Phase 7).
- Reproduce the **App B.3 fixed texture-unit map** as this phase's binding table
  (ownership shared with Phase 6: you own which texture object backs each unit per stage;
  Phase 6 owns pointing sampler uniforms at units) — including the documented
  **depthtex1-at-unit-11 ruling** (App B.3 note: the shipped doc's ID table says 12;
  behavior and uniform table say 11; **11 is authoritative**).

**Scope — out:** when copies/clears *happen* in the frame (Phase 7); sampler uniform
re-pointing (Phase 6); shadow camera/pass (Phase 8); custom texture binding (Phase 13);
sampler objects & async transfers (Phase 14).

**Required inputs:**
- RESEARCH.md §4.3, App B (whole), §3.6.3 (growth shape only), §4.1 (init/uninit steps).
- `schlorbium-project/doc/shaders.txt` — buffer/format/pixel-type sections.
- `PHASE_1_DOC.md` (facade), `PHASE_3_DOC.md` (sizing requirements, format directives,
  clear overrides), `PHASE_4_DOC.md` (flip config storage, DRAWBUFFERS routing).

**Architecture requirements:** policy in `engine.buffers` (pure, testable as a state
machine over facade calls); GL objects behind the facade. Flip/clear semantics are
contract-visible — specify them as explicit state machines with transition tables, because
they are exactly the "long tail" behavior conformance testing exists to catch (§2.4).

**Doc gate:** App B.3 reproduced exactly incl. the unit-11 ruling; clear/flip specified as
testable state machines; every App B.1/B.2/B.4 row in the conformance map.

**Impl gate:** headless state-machine tests cover flip/clear/override permutations; a
recorded-GL run creates/destroys the full buffer estate for a classic pack without leaks.

**Context budget:** ≈ 35k tokens mandatory reading.

---

### Phase 6 — Uniform & sampler system

**Milestone:** v0.1 (shadow-set *values* wired at v0.2) · **Depends on:** 1, 3, 4 · **OQs:** —

**Objective.** The ~50 built-in uniforms with their exact semantics and update cadences,
the sampler-unit re-pointing that fulfills Phase 4's state barrier, world-state smoothing
math, and the value-provider seam that keeps all of it headless-testable.

**Deliverable.** `PHASE_6_DOC.md` per §G9.

**Scope — in:**
- **Full App D inventory** (D.1–D.4): every uniform with type, semantics, and provider;
  the shadow matrix/celestial set designed now, its *values* wired when Phase 8 lands
  (tag v0.2).
- **Cadence model** (App D cadence note, §4.2): everything refreshes on program switch;
  per-program location cache; redundant-upload skipping (matrices always upload);
  celestial vectors update at the sky-rotation moment (hook is Phase 7's; the update
  entry point is yours); shadow matrices during shadow-camera setup (Phase 8 invokes);
  per-draw dynamics (`entityColor`, `entityId`, `blockEntityId`, `blendFunc`,
  `instanceId`) at their hooks (Phases 7/9/10 invoke); custom uniforms evaluated after
  built-ins on every switch (Phase 11 plugs in here — define the extension point).
- **Sampler re-pointing**: on every use-program, sampler uniforms re-point to the App B.3
  fixed unit map (per-stage variants — gbuffers/shadow vs deferred/composite/final).
- **World-state sampling & smoothing** (§4.4 frame-begin, App D.1/D.3):
  `wetness`/`drynessHalflife` decay, `eyeBrightnessSmooth`, `centerDepthSmooth` — specify
  the smoothing math exactly (halflife → per-tick exponential decay formula, time-corrected);
  `isEyeInWater`, night vision/blindness, sky/fog color sampling. **Synchronous
  center-depth readback design here** (a per-frame `glReadPixels` stall, faithful to
  reference behavior); the async PBO replacement is Phase 14's.
- **Previous-frame snapshots**: camera position + modelview/projection captured per frame
  for the `previous*` uniforms (TAA-style contract, §4.4).
- **FF-matrix capture points**: `gbufferModelView`/`gbufferProjection` (+inverses)
  captured from the fixed-function stack at defined moments (§4.4; the moments are Phase
  7 hooks; the capture/inverse machinery is yours).
- **`blendFunc` observation** via GlStateManager cooperation (§G4.6, App E row 16).
- **Value-provider interfaces** in `engine.uniforms`: every uniform's value comes through
  a provider interface implemented by `mod.glue` (world sampling) — so the whole system
  unit-tests headless with scripted providers.
- Per-uniform GL-error isolation (ladder step 2).

**Scope — out:** custom-uniform expressions (Phase 11); the hooks that *invoke* updates
(Phase 7/8); alias-derived id values (Phase 9); `atlasSize` value source (Phase 13).

**Required inputs:**
- RESEARCH.md §3.4, §4.2 (barrier), §4.4 (frame-begin/uniform lines), App B.3, App D
  (whole).
- `schlorbium-project/doc/shaders.txt` — the "Uniforms" section.
- `PHASE_1_DOC.md`, `PHASE_3_DOC.md` (declared-uniform scan → which uniforms are active),
  `PHASE_4_DOC.md` (state-barrier interface you fulfill).

**Architecture requirements:** contract-visible: names, types, semantics, cadences (App
D); implementation may batch/cache differently than the reference (§4.8) but observable
behavior must match. Pure-core with provider seam is mandatory.

**Doc gate:** every App D row mapped to provider + cadence + milestone tag; smoothing
formulas written out; barrier fulfillment traced to Phase 4's interface point by point.

**Impl gate:** headless tests — cadence engine with scripted providers reproduces
documented update patterns; smoothing math matches closed-form expectations; location
cache/redundant-skip behavior verified against a recording facade.

**Context budget:** ≈ 35k tokens mandatory reading.

---

### Phase 7 — Render-loop integration & frame orchestration

**Milestone:** v0.1 exit (assembles the first end-to-end render) · **Depends on:** 2, 3, 4, 5, 6 · **OQs:** OQ-3, OQ-4

**Objective.** The frame driver that orchestrates every prior phase into a rendered frame,
and the complete Mixin hook catalog that connects it to the vanilla 1.12.2 render loop.
This is the phase where Schmaloogium becomes a shader engine.

**Deliverable.** `PHASE_7_DOC.md` per §G9, with **two mandated parts** (a: frame driver;
b: hook catalog). **Fallback:** if the session nears its context ceiling, part (b) splits
off as Phase 7b — finish part (a)'s doc completely, and write a Phase 7b assignment
paragraph into doc §11 (part (a)'s doc becomes 7b's input).

**Scope — in, part (a) — engine-side frame driver** (§4.4):
- Frame begin/end lifecycle: world-state sampling trigger (Phase 6), previous-frame
  snapshot, gbuffers texture-set bind, main-FBO bind + clears (Phase 5), **the
  composite-guarantee** (composites/final run even on early frame exits).
- **The phase-dispatch table** (App A.1): program selection around each render phase —
  sky (incl. the celestial-rotation moment → Phase 6 celestial update), terrain
  solid/cutout-mipped/cutout, damaged-block, entities, glowing outline, block entities,
  beacon beam, armor glint, spider eyes, particles (lit/unlit), clouds, weather; **push/pop
  program semantics** around leash/glint rendering; shadow-pass force-selection deference
  (Phase 4 barrier rule).
- **Deferred trigger** between solid and translucent terrain: depth copy → depthtex1,
  deferred ping-pong execution [copy at v0.5; architecture + pass execution now];
  translucent terrain (water program) → hand solid → hand translucent with the
  depth-scale matrix trick; weather depth copy → depthtex2 [v0.5]; first-person overlay
  → draw-buffers-none routing.
- **Composite/final execution**: fullscreen quad (triangle-strip fallback), identity
  ortho, fog/depth/blend disabled, per-pass mipmap generation (composite-mipmap bitmask),
  `scale.<prog>` sub-viewports [v0.5], `countInstances` instancing loop [v0.5],
  anaglyph-aware final to the vanilla framebuffer (Phase 5 handoff).
- **Internal default pack** (§4.1 sentinels are Phase 3's; the built-in passthrough pack
  content is yours) and the shaders-off path (G2.4 rule 5).
- **Dimension-switch lifecycle**: uninit/reinit when per-dimension packs exist (§4.1 step
  5); resolution-multiplier and pack-change uninit triggers coordinated with Phase 5.
- **Engine-flag wiring** for this phase's slice of the Phase 3 flag-ownership map
  (`clouds`, `backFace.*`, `sun`, `moon`, `vignette`, `underwaterOverlay`, `rain.depth`,
  `beacon.beam.depth`, `frustum.culling` as applicable).

**Scope — in, part (b) — Mixin hook catalog** (§7.1, App E):
- Per-site injection specs for **all App E classes**: for each hook — target class/method
  (SRG name + descriptor from App E), injection type (`@Inject`/`@Redirect`/`@ModifyArg`/
  `@At` strategy inside long methods), what engine entry point it calls, and its failure
  posture (a hook that fails to apply must degrade per G2.4, not crash).
- The celestial-rotation sub-method site inside `renderSky` (`func_174976_a`) —
  identified concretely (App E coverage note); use MCP `resolve_symbol` +
  `get_method_signature` to validate every App E row you consume.
- Display-resize/framebuffer-size interception and GL-context posture: **OQ-3 spike spec**
  (context creation mechanics, compat-profile request, HiDPI framebuffer-size vs window
  size, what lwjglx intercepts — procedure: inspect Cleanroom's window/context layer in a
  dev env; fallback: hook `Framebuffer`/resize sites only, no context-flag changes).
- **OQ-4 spike spec** (CleanMix divergences on hot-path injections — procedure: apply a
  representative injection per category — `@Inject` head/return, `@Redirect`, constant
  `@At` — into `EntityRenderer`/`RenderGlobal`/`BufferBuilder` in dev, verify
  application + measure overhead; fallback: alternative injection forms per site,
  documented per hook).
- Coexistence bail hook (Phase 1's registry; policy from Phase 10).
- The v0.1 assembly narrative: which hooks + driver pieces constitute the first
  end-to-end render, in dependency order.

**Scope — out:** shadow-pass content (Phase 8 — you leave its invocation slot before the
world render); vertex-write/draw-path hooks (Phase 10 — App E rows 5–8 are catalogued as
Phase 10 consumers, not specified here); per-entity/TE id values (Phase 9); atlas hooks
(Phase 13 — App E rows 10–11 likewise deferred).

**Required inputs:**
- RESEARCH.md §4.4 (whole), §7.1, §5.3, App A.1, App E (whole).
- `cleanroom-src/patches/minecraft/net/minecraft/client/renderer/EntityRenderer.java.patch`
  and `RenderGlobal.java.patch` (Cleanroom's own deltas to your two main target classes —
  what Cleanroom already changed, you must not fight).
- `schlorbium-project/SHADER_ENGINE_IMPL.md` — frame-flow and lifecycle sections only,
  under §G7 rules (restate behavior, never structure).
- `schlorbium-project/files.txt` (cross-check: every hook class appears in OF's
  replacement list).
- MCP recipes: `resolve_symbol`, `get_method_signature` (validate App E), 
  `search_cleanroom_api(kind=event)` (prefer a Forge/Cleanroom event over a mixin where
  one exists — e.g. render-world events).
- `PHASE_2_DOC.md` (exit-criteria runs), `PHASE_3_DOC.md`–`PHASE_6_DOC.md`.

**Architecture requirements:** hooks stay dumb (§G3.3); the frame driver lives engine-side
with a narrow glue interface so a future Kirino backend (G8/S5) replaces part (b) without
touching part (a) — state this seam explicitly in doc §5. Where a Forge event covers a
hook need at acceptable fidelity, prefer it over a mixin (fewer injections = smaller OQ-4
surface); document each such choice.

**Doc gate:** every §7.1 hook need 1–11 traced to a hook-site spec, a Forge event, or an
explicit deferral (with owner phase and milestone tag); v0.1 assembly narrative complete;
both spike specs complete with fallbacks.

**Impl gate:** RESEARCH.md §9 v0.1 exit — ≥1 classic pack at T1 on the fixed scenes; T0
across the classic matrix (Phase 2 harness runs).

**Context budget:** the heaviest phase: ≈ 60k tokens mandatory reading + MCP resolution
traffic. Watch the ceiling; the 7b fallback exists for a reason. Do not read decompiled
engine sources; `SHADER_ENGINE_IMPL.md`'s frame-flow section + §4.4 suffice.

---

### Phase 8 — Shadow pass

**Milestone:** v0.2 · **Depends on:** 4, 5, 6, 7 · **OQs:** —

**Objective.** The complete shadow pass: its camera, culling, traversal, render order,
depth split, and uniform wiring — running inside frame begin, before the world render,
in the slot Phase 7 reserved.

**Deliverable.** `PHASE_8_DOC.md` per §G9.

**Scope — in** (all §4.5 unless noted):
- **Shadow camera**: forced third-person; ortho projection (±`shadowDistance` half-plane,
  near 0.05 / far 256) or perspective when `shadowMapFov` is set; modelview = celestial
  rotation (sun by day, moon by night) + `sunPathRotation`; **texel snapping** by
  `shadowIntervalSize` (default 2.0) so shadow texels don't crawl.
- **Culling & traversal**: shadow-frustum planes derived from the shadow MVP **plus
  synthesized side planes along the light direction**; sun-aligned-box chunk traversal
  between camera and light when shadow render distance < view distance;
  `shadowDistanceRenderMul` optimization semantics.
- **Render order**: terrain solid → cutout-mipped → cutout; entities via Forge
  render-pass interop (free on a Forge-lineage loader — verify the Cleanroom equivalent);
  depth copy → shadowtex1 (**water-shadow split**, before shadow translucents); optional
  translucent terrain (`shadowTranslucent` flag — this phase's slice of the Phase 3
  flag-ownership map, plus clouds-in-shadow per config); per-config mipmap generation on
  shadow textures (Phase 5 texture config).
- **Hardware PCF** (compare-mode setup on shadow depth textures, `shadowHardwareFiltering`).
- **Vanilla blob-shadow suppression** while a shadow pass exists.
- **Uniform wiring**: shadow matrices (`shadowProjection`/`shadowModelView` + inverses)
  captured during shadow-camera setup into Phase 6's slots; `shadowAngle`;
  shadowtex/shadowcolor sampler activation per App B.3.
- The force-shadow-program rule during the pass (Phase 4's barrier override — you define
  when the pass begins/ends; the barrier enforces it).
- Additional hook sites needed beyond Phase 7's catalog (e.g. blob-shadow suppression,
  `setupTerrain` reuse for shadow traversal) — specified in the same App E format,
  flagged as Phase 8 additions.

**Scope — out:** shadow FBO structure (Phase 5); barrier mechanics (Phase 4);
shadowcomp passes (G8/S1).

**Required inputs:**
- RESEARCH.md §4.5 (whole), App A.3 (shadow directives), App B.2/B.3, App D.3 (shadow
  rows), App E rows 1–2 (renderWorldPass/setupTerrain context).
- `schlorbium-project/doc/shaders.txt` — shadow section.
- `schlorbium-project/SHADER_ENGINE_IMPL.md` — shadow-pass section only (§G7 rules).
- MCP: `search_cleanroom_api("render pass entity", kind=event)` for the Forge
  render-pass interop equivalent.
- `PHASE_4_DOC.md`–`PHASE_7_DOC.md`.

**Architecture requirements:** contract-visible via shadow uniforms/buffers (§4.8): the
celestial camera math, texel snap, and depth-split moments must match observed behavior —
packs compute shadow-space positions from these matrices, so any deviation shows as
misplaced shadows in T1/T2.

**Doc gate:** camera math written out (matrices, snapping formula); traversal algorithm
specified; every shadow-related App A.3/B.2/D.3 row in the conformance map; added hook
sites in App E format.

**Impl gate:** RESEARCH.md §9 v0.2 — classic packs with shadows at T1; first T2 runs.

**Context budget:** ≈ 40k tokens mandatory reading.

---

### Phase 9 — ID aliasing & per-draw dynamics

**Milestone:** v0.3 · **Depends on:** 3, 6, 7 · **OQs:** —

**Objective.** The identity layer: resolving pack-facing stable IDs against live Forge
registries, merging mod-provided extensions, and driving the per-draw id/held-item
uniforms. Runs *before* Phase 10 because alias resolution is upstream of `mc_Entity`
vertex stamping.

**Deliverable.** `PHASE_9_DOC.md` per §G9.

**Scope — in:**
- **Alias resolution** (§3.7): Phase 3's parsed grammar → resolved lookup tables against
  Forge block/item/entity registries: short names (`red_flower`), namespaced
  (`minecraft:red_flower`), property-matched forms, legacy id:meta. Reload on
  registry/resource changes.
- **Per-mod merge**: `assets/<modid>/shaders/{block,item,entity}.properties` from every
  loaded mod jar, merged with pack-provided files (define precedence: pack wins over mod,
  document it) — the mod-facing extension point RESEARCH.md flags as must-preserve.
- **Custom render layers** (`layer.solid/cutout/cutout_mipped/translucent=<blocks>`;
  solid-opaque cubes excluded) — resolution here; the terrain dispatch that honors them is
  Phase 7's (flag the cross-reference in doc §5).
- **Held-item tracking**: `heldItemId`/`heldItemId2` (main/off hand),
  `heldBlockLightValue`/`2` including the `oldHandLight` brighter-hand-wins mode and
  `dynamicHandLight` (this phase's slice of the flag-ownership map).
- **Per-draw dynamics**: `entityId` (per entity, via the RenderManager hook),
  `blockEntityId` (per TE, via TileEntityRendererDispatcher), `entityColor` (hurt/flash
  tint) — value computation + the Phase 6 per-draw upload path; hook-site specs in App E
  format (rows 13–14) if Phase 7 catalogued but did not specify them.
- The **alias-lookup service** interface Phase 10 consumes for `mc_Entity` stamping
  (`renderType<<16 | aliasedBlockId`, metadata) — the entity-data *stack* itself is
  Phase 10's; the id computation is yours.

**Scope — out:** vertex stamping and the chunk-build stack (Phase 10); uniform upload
mechanics (Phase 6); grammar parsing (Phase 3).

**Required inputs:**
- RESEARCH.md §3.7, §4.7 (aliases), App D.1/D.4, App E rows 13–14.
- `schlorbium-project/doc/shaders.txt` — ID-mapping section.
- Skim: `cleanroom-src/src/main/java/net/minecraftforge/` registry API
  (`registries/`, `fml/common/registry/`) — resolution targets.
- MCP: `resolve_symbol` for registry classes as needed.
- `PHASE_3_DOC.md` (grammar model), `PHASE_6_DOC.md` (per-draw upload), `PHASE_7_DOC.md`
  (hook catalog state).

**Architecture requirements:** resolution tables are rebuilt, never mutated in place
(reload safety); unknown names degrade per G2.4 (warn once, id absent) — packs tolerate
missing ids, they do not tolerate crashes.

**Doc gate:** every §3.7 form + the per-mod merge + precedence rules in the conformance
map; alias-service interface fully specified for Phase 10.

**Impl gate:** headless tests resolve fixture properties files against a scripted
registry; in-game, `heldItemId`/`entityId` uniforms verified on the Phase 2 scene set
(v0.3 harness runs).

**Context budget:** ≈ 30k tokens mandatory reading.

---

### Phase 10 — Extended vertex pipeline

**Milestone:** v0.3 · **Depends on:** 4, 7, 9 · **OQs:** OQ-5, OQ-14

**Objective.** The 56-byte extended vertex format and everything that feeds it: the
vertex-builder side channel, per-quad attribute math, the chunk-build entity stack, and
attribute delivery in both draw paths. The most invasive subsystem — and the one that
owns the chunk-renderer coexistence policy.

**Deliverable.** `PHASE_10_DOC.md` per §G9.

**Scope — in:**
- **The format** (App C.1): vanilla 28-byte block vertex → 56 bytes / 14 ints;
  `mc_midTexCoord` (2×float @ 32), `at_tangent` (4×short @ 40), `mc_Entity` (shorts @ 48);
  attribute locations 10/11/12 (Phase 4 pre-binds them).
- **Population rules** (App C.2): `mc_Entity` = current top of the chunk-build entity
  stack (values from Phase 9's alias service), stamped into every vertex at write time;
  per quad (every 4 vertices): face normal = normalize((v2−v0)×(v3−v1)), UV-delta
  tangent/bitangent with handedness `w = sign(dot(bitangent, normal×tangent))`,
  `mc_midTexCoord` = average of the quad's UVs.
- **The vertex-builder side channel**: attached to every buffer builder; stamping +
  per-quad computation; interaction with `begin`/`endVertex`/`addVertexData` (App E row 5).
- **Chunk-build entity push/pop** around per-block model rendering (App E rows 3–4, 9):
  block state pushes `(renderType<<16 | aliasedBlockId, metadata)`; async chunk-build
  coordination (ChunkRenderDispatcher worker threads — thread-safety of the stack is
  yours to design, doc §7).
- **Both draw paths** (App E rows 6–8): VBO (`VertexBuffer.bufferData`/`drawArrays`) and
  client-array (`WorldVertexBufferUploader.draw`, `Tessellator.draw`) — attribute
  pointers stride 56 at offsets 32/40/48, arrays 10–12 enabled only around shader-active
  draws.
- **Format swap lifecycle**: extended format swapped in/out on pack toggle; world-renderer
  reload; **OQ-14 spike spec** (Forge baked-quad/LightUtil cache interplay under
  Cleanroom — procedure: toggle formats in a dev world with Forge lighting pipeline
  on/off, observe cache corruption; fallback: force-invalidate caches on swap / pin the
  Forge lighting path).
- **Per-program attribute enablement** from Phase 3's scan (a program declaring
  `mc_Entity` opts in; others get pointers disabled).
- **Growth design** (§7.4, App C.3): attribute slots addressable by name; layout growable
  without touching every consumer; `at_midBlock` as the canonical first addition
  (post-v0.5 tag — design the slot, don't wire it).
- **OQ-5 coexistence policy**: detect replaced chunk pipelines (Celeritas/Vintagium
  forks/Nothirium today, Kirino tomorrow) → **detect-and-bail with a clear user message**
  as the v0.3 policy (RESEARCH.md §7.4 `[A]`), via Phase 1's bail registry; spike spec
  for detection mechanics (classloading probes vs mod-id checks); integration is
  explicitly future work (G8).
- Hook-site specs (App E format) for rows 3–9 — Phase 7 catalogued them as your
  consumers; you specify them.

**Scope — out:** alias id computation (Phase 9); `_n`/`_s` atlases (Phase 13 — the
tangent frame you compute is what makes them useful, note the dependency); compile-time
attribute binding (Phase 4).

**Required inputs:**
- RESEARCH.md §4.6 (vertex part), §7.4, App C (whole), App E rows 3–9.
- `schlorbium-project/SHADER_ENGINE_IMPL.md` — vertex-pipeline section only (§G7 rules).
- `cleanroom-src/patches/minecraft/net/minecraft/client/renderer/BufferBuilder.java.patch`
  (+ `chunk/` patches if present) — Cleanroom's own deltas to your target classes.
- MCP: `resolve_symbol`/`get_method_signature` for App E rows 3–9 validation.
- `PHASE_4_DOC.md` (attribute binding), `PHASE_7_DOC.md` (hook catalog conventions),
  `PHASE_9_DOC.md` (alias service).

**Architecture requirements:** per-quad math is contract-visible (packs read the tangent
frame); specify it numerically (formulas + worked example). The side channel must be
zero-cost when shaders are off (G2.4 rule 5). Thread-safety across chunk-build workers is
a first-class design concern, not a footnote.

**Doc gate:** App C fully mapped; both draw paths specified; both spike specs complete
with fallbacks; growth design shows `at_midBlock` addable without consumer edits;
coexistence policy + message text drafted.

**Impl gate:** RESEARCH.md §9 v0.3 — classic packs at T2 within tolerance on terrain
scenes.

**Context budget:** ≈ 45k tokens mandatory reading + MCP traffic.

---

### Phase 11 — Custom-uniform expression engine

**Milestone:** v0.4 · **Depends on:** 3, 6 (no Phase 7 dependency — parallel-friendly) · **OQs:** —

**Objective.** The complete `uniform.<type>.<name>=<expr>` / `variable.<type>.<name>`
expression language: grammar, functions, input binding, evaluation cadence, and an
evaluator architecture chosen for per-frame cost. Pure `:engine` code.

**Deliverable.** `PHASE_11_DOC.md` per §G9.

**Scope — in** (App F.6 is the contract):
- **Grammar**: numeric literals, `pi true false`, operators
  (`+ - * / %`, `! && ||`, comparisons), member access (vector `.x/.y/.z`, color
  `.r/.g/.b`, matrix `name.<row>.<col>`), the full function set (`sin cos asin acos tan
  atan atan2 torad todeg min max clamp abs floor ceil exp frac log pow random round
  signum sqrt fmod`), conditional `if(cond,val,…,val_else)`, **`smooth([id,]val[,fadeIn
  [,fadeOut]])`** with per-id persistent state (time-corrected exponential, default 1s),
  boolean helpers `between/equals/in`, constructors `vec2/vec3/vec4`.
- **Input binding**: every fixed scalar built-in uniform (Phase 6's value model), biome
  params (`biome`, `temperature`, `rainfall`, `BIOME_*` constants), view-entity booleans
  (`is_alive` … `is_wet`); the **per-draw exclusion rule** (`entityColor entityId
  blockEntityId fogMode fogColor` are not expression inputs — App F.6).
- **`variable.*` intermediates**: evaluated once per update, referenced by uniforms, not
  uploaded.
- **Typing**: declared type (`float/int/bool/vec2/3/4`) vs expression result — coercion
  and mismatch rules.
- **Cadence**: evaluated on every program switch, after built-ins (Phase 6's extension
  point); `smooth()` state keyed by id, surviving across switches, time-corrected.
- **Evaluator architecture**: parsed-AST interpreter vs compiled (MethodHandle/bytecode,
  §6.3) — design the interface so both fit, specify the decision criteria (per-switch
  cost budget across a real pack's uniform count), recommend the v0.4 choice (interpreter
  first unless evidence says otherwise — G2.5), note the compiled path as an OQ-22 ledger
  item for Phase 14's methodology.
- **Error isolation**: parse errors disable that uniform at load (chat-visible warning);
  runtime errors disable that uniform only (ladder step 1); division-by-zero/NaN
  propagation semantics defined.
- Biome/view-entity value-provider interfaces (implemented by `mod.glue`, headless-tested
  with scripts).

**Scope — out:** where values come from (Phase 6 providers); the properties-file capture
of declarations (Phase 3); GUI display of profiles referencing options (Phase 12).

**Required inputs:**
- RESEARCH.md §3.4 (item 4), App F.6, §6.3 (expression row).
- `schlorbium-project/doc/shaders.properties` — the custom-uniform/variable section
  (the annotated expression-language reference).
- `PHASE_3_DOC.md` (declaration capture), `PHASE_6_DOC.md` (value model, extension point).

**Architecture requirements:** pure `engine.expr`; contract-visible function semantics
(packs ship expressions tuned against OF's evaluator — match documented behavior,
including `smooth()`'s time correction); headless test vectors are the primary
verification (golden expression → value tables).

**Doc gate:** every App F.6 token/function/operator in the conformance map; `smooth()`
state machine specified; evaluator interface + decision criteria written; error semantics
per the ladder.

**Impl gate:** headless golden-vector suite passes; matrix packs' custom uniforms parse
and evaluate against scripted providers (Phase 2 golden runs extended).

**Context budget:** ≈ 25k tokens mandatory reading — the lightest phase; keep it tight.

---

### Phase 12 — Options GUI, persistence & reload

**Milestone:** v0.4 · **Depends on:** 1, 3 (soft: 7 — reload-lifecycle section only) · **OQs:** OQ-9

**Objective.** The user-facing surface: pack selection, generated option screens,
profiles, sliders, persistence round-trip, and the reload paths — on ModularUI if the
spike sustains it.

**Deliverable.** `PHASE_12_DOC.md` per §G9.

**Scope — in:**
- **Pack-selection screen**: pack list (folder/zip, sentinels), current selection,
  engine-settings entries (Schmaloogium's equivalents of OF's 8: normal/specular map
  toggles, render/shadow quality multipliers, hand depth, old hand light, old lighting —
  AA/AF explicitly do not exist per §1.2).
- **Generated option screens** from Phase 3's model (App F.4): `screen=`/`screen.NAME=`
  with `[SUBSCREEN]`, `<profile>`, `<empty>`, `*` (unplaced options), `columns=N`
  (default 2, auto-widen past 18); sliders (`sliders=` list); profile inference from
  current option values ("Custom" otherwise) and click-to-cycle; tooltips from lang files
  (split on ". ", trailing "!" renders red); value prefix/suffix decoration.
- **Persistence round-trip**: only changed options to `shaderpacks/<pack>.txt`; global
  engine settings file; write-through on change; Phase 3 owns formats — you own when/how
  they're read/written from the GUI.
- **Reload paths**: F3+R keybind, `/reloadShaders` command, resource-reload integration —
  each mapped to the correct lifecycle (full pack reload vs option re-apply vs
  world-renderer reload), coordinated with the Phase 7 lifecycle (soft dependency: if
  `PHASE_7_DOC.md` is absent, design against Phase 7's spec here and flag the assumption
  in doc §5).
- **OQ-9 spike spec**: ModularUI fitness — procedure: prototype a generated screen with
  a slider, a subscreen, and profile cycling from a real pack's `screen.*` config;
  success: all three bind cleanly; failure fallback: vanilla-GuiScreen-based minimal UI
  (uglier, zero-dependency) — design the screen *model* UI-framework-agnostically so the
  fallback swaps the view layer only.
- GUI-side error surfacing (per-program compile errors, capability-gate messages — G4.5
  channels).

**Scope — out:** option semantics/parsing (Phase 3); what reload does internally
(Phases 4–7); ModularUI licensing note (Phase 1 / G7).

**Required inputs:**
- RESEARCH.md §4.7 (options/GUI), §7.6, App F.3/F.4.
- MCP: `search_mod_examples(query="ModularUI", category=gui)` — real usage patterns.
- `PHASE_1_DOC.md` (module layout, ModularUI dependency mechanics), `PHASE_3_DOC.md`
  (options/screens/profiles model, persistence formats).

**Architecture requirements:** `mod.gui` only; the screen *model* (tree of options/
subscreens/profiles with bindings) lives engine-side and is headless-testable; the
ModularUI (or fallback) view is a thin adapter — this is the OQ-9 hedge.

**Doc gate:** every App F.3/F.4 construct in the conformance map (incl. `*`, `<empty>`,
red-"!", auto-widen); reload-path × lifecycle matrix complete; OQ-9 spike + fallback
designed.

**Impl gate:** RESEARCH.md §9 v0.4 — options round-trip persistence; classic matrix at
T2/T3 (jointly with Phase 11).

**Context budget:** ≈ 30k tokens mandatory reading + MCP examples.

---

### Phase 13 — Texture systems

**Milestone:** v0.5 · **Depends on:** 3, 5, 7 · **OQs:** —

**Objective.** The texture estate beyond the FBOs: normal/specular companion atlases,
the noise texture, pack custom textures in all three source forms, and the `atlasSize`
uniform.

**Deliverable.** `PHASE_13_DOC.md` per §G9.

**Scope — in:**
- **`_n`/`_s` companion atlases** (§4.6): every atlas sprite may have `_n`/`_s`
  companions; full companion atlases with matching mip chains; missing sprites →
  flat-normal `0xFF7F7FFF` / zero-specular defaults; bound on units 2/3 during world
  rendering (App B.3); stitch/load hooks on `TextureMap`/`TextureAtlasSprite` (App E rows
  10–11 — hook-site specs in the Phase 7 format); `MC_NORMAL_MAP`/`MC_SPECULAR_MAP`
  macro wiring back into Phase 3's identity set.
- **Noise texture**: `noiseTextureResolution²` RGB, xorshift-generated (specify the
  generator so it's reproducible), unit 15, `texture.noise=<path>` override.
- **Custom textures** (App F.5, model from Phase 3): all three source forms —
  pack-relative PNG; `minecraft:` asset locations (incl. `dynamic/lightmap_1`, atlas
  paths, `_n`/`_s` variant selection); raw binary
  (`<path> <target> <internalFormat> <dims…> <pixelFormat> <pixelType>`, 1D/2D/3D/RECT);
  `.mcmeta` blur/clamp sidecars; per-stage binding (gbuffers → gbuffers+shadow programs;
  deferred; composite → composite+final); multiple texture types per unit disambiguated
  by sampler type (one type per unit per program); lifecycle across pack/resource
  reloads.
- **`atlasSize`** uniform (set while the atlas is bound — value source here, upload via
  Phase 6).
- Sprite-animation interaction (companions must animate with their base sprite — design
  the tick hookup).

**Scope — out:** unit-map ownership (Phases 5/6); tangent math that consumes the normals
(Phase 10); labPBR semantics (pack-side convention — engine-neutral, G8 advertises it).

**Required inputs:**
- RESEARCH.md §4.6 (texture part), App B.3 (units 2/3/15), App E rows 10–11, App F.5.
- `schlorbium-project/SHADER_ENGINE_IMPL.md` — texture-system section only (§G7 rules).
- MCP: `resolve_symbol` for TextureMap/TextureAtlasSprite/stitch-event symbols;
  `search_cleanroom_api("texture stitch", kind=event)` (prefer the Forge stitch events
  over mixins where fidelity allows).
- `PHASE_3_DOC.md` (custom-texture model, macro set), `PHASE_5_DOC.md` (unit binding
  tables), `PHASE_7_DOC.md` (hook conventions).

**Architecture requirements:** companion loading must not regress vanilla atlas stitching
when shaders are off (G2.4 rule 5); memory posture documented (two extra full atlases is
the accepted cost, §4.8 Keep).

**Doc gate:** all three custom-texture forms + `.mcmeta` + stage mapping in the
conformance map; companion-atlas lifecycle (load/stitch/reload/animate) specified;
hook sites in App E format.

**Impl gate:** RESEARCH.md §9 v0.5 (jointly with Phase 14) — full classic matrix at T3;
packs using `MC_NORMAL_MAP` render correctly on the fixed scenes.

**Context budget:** ≈ 35k tokens mandatory reading.

---

### Phase 14 — GL modernization & performance

**Milestone:** v0.5 + quality-of-life · **Depends on:** 5, 6, 7, 13 · **OQs:** OQ-15, OQ-22

**Objective.** The §6.2/§4.8 "Adapt" set as concrete designs: replace the reference's
legacy per-frame costs with modern-GL equivalents inside our own pipeline — the only
performance work the mission permits (§1.2, G2.5).

**Deliverable.** `PHASE_14_DOC.md` per §G9.

**Scope — in:**
- **GL 3.3 sampler objects** per stage, replacing per-frame re-parameterization of flip
  textures (filter state decoupled from texture objects; mapping onto Phase 5's estate).
- **PBO + fence-sync async center-depth readback**, replacing Phase 6's synchronous
  design: one-frame latency on an already-smoothed value; the design must verify
  imperceptibility (test: compare `centerDepthSmooth` traces sync vs async on the Phase 2
  scenes) and keep the sync path as a fallback/config.
- **Shared-context async shader compile** + async `_n`/`_s` atlas upload (**OQ-15 spike
  spec**: GLFW shared compat contexts across drivers — procedure: prototype on ≥2 driver
  families, measure pack-switch stall; success: no corruption + stall < threshold;
  **mandatory synchronous fallback designed regardless of outcome**, selected at runtime
  per driver).
- **KHR_debug** labels/groups + debug-context dev mode (pairs with RenderBook's Nsight
  workflow, §6.2); integration with Phase 1's debug flags.
- **Allocation/GC posture** + measurement methodology: how the implementation effort
  validates that clean-code-first holds (allocation profiling on the Phase 2 scenes;
  criteria for when an optimization is justified) — the **OQ-22 spot-check ledger**:
  each §6.2/§6.3 `[U]` claim this phase relies on gets a row (claim → cheap experiment →
  decision point).
- **Redundant-state audit** methodology: identifying per-frame GL churn in our own
  pipeline (never vanilla's — §1.2).

**Scope — out:** any vanilla-pipeline optimization (non-goal, §1.2); chunk pipelines
(non-goal); the sync designs being replaced (Phases 5/6 own them; you supersede with
their docs as input).

**Required inputs:**
- RESEARCH.md §6.2, §6.3, §4.8 (Adapt rows), §11 (OQ-15/OQ-22 rows).
- `PHASE_5_DOC.md`, `PHASE_6_DOC.md`, `PHASE_7_DOC.md`, `PHASE_13_DOC.md`.

**Architecture requirements:** every modernization is a strict behavioral no-op from the
pack's perspective (contract-visible behavior unchanged — G4.2); each ships with a
fallback to the reference-faithful path; facade extensions (new GL entry points) are
additive.

**Doc gate:** each Adapt row → design + fallback + ledger entry; OQ-15 spike complete;
imperceptibility test for async readback specified.

**Impl gate:** RESEARCH.md §9 v0.5 (jointly with Phase 13) — full classic matrix at T3;
pack-switch stall measurably reduced vs the synchronous baseline without T1 regressions.

**Context budget:** ≈ 30k tokens mandatory reading.

---

## Appendix: coverage cross-checks

*For the document maintainer, not phase sessions.*

**RESEARCH.md §9 milestone scope → phase mapping:**

| §9 scope item | Phase |
|---|---|
| gbuffers + composite + final; program registry w/ backup chains | 4, 7 |
| preprocessor + macros + includes; source-scan resource sizing | 3 |
| main FBO ping-pong + flips + clears | 5 |
| built-in uniforms (no shadow set); fixed unit map | 6 |
| options parsing (no GUI); internal default pack | 3; 7 |
| Shadow pass (all components) | 8 |
| Extended vertex format + per-quad attributes + entity-data stack | 10 |
| block/item/entity aliases + per-mod merge; per-entity/TE id uniforms | 9 |
| Custom uniforms/variables expression engine | 11 |
| profiles/screens/sliders model; options GUI; persistence | 3 (model); 12 (GUI) |
| per-program alphaTest/blend/scale/flip overrides | 3 (parse), 4 (apply), 7 (execute) |
| `_n`/`_s` atlases + noise + custom textures | 13 |
| depth copies incl. async center-depth; render scale; instancing | 7 (arch, v0.5 tags); 6→14 (center-depth) |
| conformance harness | 2 |
| post-v0.5 row | G8 |

**Dropped-item audit** (items with no obvious home, verified placed): wetness/eyeBrightness/
centerDepth smoothing → P6; dimension-switch lifecycle → P3 (discovery) + P7 (lifecycle);
graceful degradation → G2.4 + every doc §6; geometry-shader dual-form → P4; version gate →
P3; blob-shadow suppression + clouds-in-shadow → P8; `blendFunc`/GlStateManager → P6 + G4.6;
composite guarantee/anaglyph/overlay-drawbuffers-none/push-pop → P7; `layer.*` → P9 (resolve)
+ P7 (dispatch); debug source dump → P3; engine-flag ownership → P3 map, wired by owners;
Kirino/upstream engagement → G8 + P1 seam.

*End of design document.*
