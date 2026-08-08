# Schmaloogium — Phase 14: GL modernization & performance — Architecture

## 0. Header

- **Phase:** 14 — GL modernization & performance
- **Milestone:** v0.5 + quality-of-life
- **Module/package:** `:engine` (`com.schmaloogium.engine.gl` — pure value additions only) and `:mod`
  (`com.schmaloogium.mod.glue` — the LWJGL3 backend where every behavior in this phase lives)
- **Declared dependencies:** Phases 5, 6, 7, 13 (`docs/design/v3/DESIGN.md:626`)
- **Assigned open questions:** OQ-15, OQ-22 (`docs/design/v3/DESIGN.md:626`, `:878`, `:885`)
- **Governing design:** `docs/design/v3/DESIGN.md`
- **Design status:** initial build document, not yet verified
- **Date:** 2026-08-08

This is an **initial build against v3**, not a §G0.4 re-pointing: Phase 14 has never been built, so
the four-step adoption procedure at `docs/design/v3/DESIGN.md:195`–`:220` does not apply
(commissioning record: `docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md:19`–`:29`). Every v3 coordinate
below was derived from v3's own headings (`grep -n '^#'`) and each range's first and last line was
confirmed by reading it. No coordinate is transplanted from another revision. Part I is
`docs/design/v3/DESIGN.md:125`–`:1135`; the Phase 14 specification is `:2514`–`:2588`; the §G9
mandatory template is `:817`–`:854`; the Phase 13 specification is `:2436`–`:2512`.

**Execution surface.** There is no `verification/targets/phase-14.json`; the whole directory was
deleted 2026-08-08 in commit `e173848`. `docs/MOVES.md:98`–`:99` states that "§0's per-doc
declaration is the single source of truth. The governing revision is declared in each phase
document's own §0", and `docs/tooling/CODEX_MIGRATION_OVERLAY.md:12`–`:22` is the sanctioned
interpreter for the immutable revisions' retired execution wording. This §0 declaration is therefore
the sole source of truth for this document's governing revision.

### 0.1 Inputs actually read

| Input | Portion read | Why |
|---|---|---|
| `AGENTS.md` | complete | repository governance before touching `docs/` |
| `docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md` | complete | commissioning record and authorized departures |
| `docs/tooling/CODEX_MIGRATION_OVERLAY.md` | complete | interpreter for retired execution-surface wording |
| `docs/design/v3/DESIGN.md` | Part I §G0–§G12 (`:125`–`:1135`); Phase 14 spec (`:2514`–`:2588`); §G9 template (`:817`–`:854`); Phase 13 spec (`:2436`–`:2512`); §G5.1 titles only for all other phases | governing global rules, my assignment, the template, and the absent dependency's spec |
| `docs/research/v1/RESEARCH.md` | §0 (`:11`–`:53`), §1 (`:55`–`:106`), §4.8 (`:620`–`:650`), §6.2 (`:765`–`:779`), §6.3 (`:780`–`:789`), §9 (`:940`–`:956`), §11 (`:1000`–`:1029`) | source of truth: tags, mission/non-goals, the Adapt rows, the GL/JVM opportunity tables, the v0.5 milestone row, OQ-15/OQ-22 |
| `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` | §15 whole (`:732`–`:755`), §6.3 (`:312`–`:332`), §17 bug catalogue (`:780`–`:800`), §18 divergence table (`:803`–`:819`) | the two assigned PD sections plus the two do-not-inherit tables §G11.4 makes standing |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/IrisRenderSystem.java` | DSA tier selection (`:24`–`:45`), sampler verbs (`:358`–`:394`), `DSAAccess` tiers (`:458`, `:490`, `:494`, `:581`), `glReadPixels` (`:190`) | verify PD §15's DSA-tiering and sampler-object claims at the source, per §G1.1's Pintonium reading discipline |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/sampler/GlSampler.java` | complete (36 lines) | verify the deployed `GlSampler` shape |
| `reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/debug/GLDebug.java` | gate (`:290`–`:296`), verbs (`:298`–`:307`), `KHRDebugState` (`:330`–`:356`) | verify PD §15's KHR_debug claim and its gate condition |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java` | `setPhase` (`:1268`–`:1284`) | verify PD B7's push/pop imbalance before designing against it |
| `reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/device/GLRenderDevice.java` | fence creation (`:233`) | verify PD §15's "no PBO/async readback" claim and find what *is* deployed |
| `reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/sync/GlFence.java` | complete head (`:1`–`:40`) | the non-blocking fence-poll shape my §4.3 design reuses |
| `docs/phase5/v1/PHASE_5_DOC.md` | §0 header, §1, §5, §6, §7, §9 in full; §2/§3/§4 selectively — colortex filter/wrap policy (`:1042`–`:1045`), shadow-mipmap filter mutation (`:1723`–`:1739`), resource projection records (`:526`–`:545`) | dependency contract; buffer estate, flip textures, per-frame filter state, resize lifecycle |
| `docs/phase6/v1/PHASE_6_DOC.md` | §0, §1, §3, §5, §6, §7, §9, §11 in full; §4 selectively — the SPI records (`:540`–`:588`), frame-begin ordering (`:860`–`:898`), the `centerDepthSmooth` decision (`:926`–`:968`), the fixed sampler maps (`:970`–`:1002`) | dependency contract; `CenterDepthSource`, `D-P6-1`, sampler participant, program-switch upload path |
| `docs/phase7/v1/PHASE_7_DOC.md` | §0 head, §1, §5.1, §6, §7, §9 in full; §4 selectively — pipeline build steps (`:690`–`:770`), reload lifecycle (`:1022`–`:1055`); §10.1 (OQ-3) | dependency contract; frame ordering, pack-switch/reload path, debug flags, the context-creation fallback my §4.5 depends on |
| `docs/phase5/reviews/PHASE_5_REVIEW_37.md`, `PHASE_5_REVIEW_38.md`, `docs/phase6/reviews/PHASE_6_REVIEW_24.md`, `docs/phase7/reviews/PHASE_7_REVIEW_32.md` | method sections and verdict blocks | the §G5.3 dependency gate check every build session owes |
| `docs/MOVES.md` | §"There is no longer one governing revision" (`:80`–`:112`) | resolve versioned paths; confirm the §0-declaration rule |
| `docs/phase11/v1/PHASE_11_DOC.md` | §0 only (`:1`–`:60`) | format exemplar (most recent v3-governed doc), per the commissioning brief |

### 0.2 Reads beyond the assigned list, with reasons

§G1.1 permits reading beyond the list on a genuine gap, and requires recording what was read and
why (`docs/design/v3/DESIGN.md:243`–`:245`). Four such reads occurred.

1. **The v3 Phase 13 specification (`docs/design/v3/DESIGN.md:2436`–`:2512`).** Read because
   `PHASE_13_DOC.md` is not a valid dependency input (§0.3 item 2). It is the only source for what
   Phase 13 will own, and §5.4's Phase-13 requests are written against it. Authorized by
   `docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md:64`–`:68`.
2. **`docs/phase1/v14/PHASE_1_DOC.md`, narrow sections.** Phase 1 is not a declared dependency of
   Phase 14, but this phase's assignment is "the facade's internal object-creation strategy"
   (`docs/design/v3/DESIGN.md:2533`–`:2535`) and "facade extensions … are additive"
   (`:2575`–`:2576`). Neither can be designed without the facade's actual shape. Read: §1's
   scope-out row assigning this phase its work (`:1490`), the `DebugService`/`GLDevice`/
   `TextureService`/`StateService` declarations (`:2880`–`:3070`), `GLCapabilityProfile`
   (`:2636`–`:2670`), the debug-flag namespace §4.9.3 (`:3731`–`:3762`), the §5 rows for
   `GLCapabilityProfile` (`:4222`) and the GL-error surface (`:4220`), and the §7 thread-ownership
   row that explicitly invites a Phase 14 request (`:4322`). Nothing else in that 5 399-line
   document was read.
3. **`docs/phase7/v1/PHASE_7_DOC.md` §10.1 (OQ-3).** Outside the sections the brief lists, read
   because scope row 5 (KHR_debug **plus debug-context dev mode**) is unbuildable without knowing
   whether a context-flag change is available. It is not, by default (§4.5, finding C-3).
4. **`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` §17 and §18.** Beyond the two assigned PD
   sections, read because §G11.4 makes both do-not-inherit lists standing and requires the relevant
   rows to be shown handled in this doc's §3 (`docs/design/v3/DESIGN.md:958`–`:960`).

No appendix of RESEARCH.md was read: no conformance-map row in §3 required one, because this phase
owns no contract-visible component (§1.3). No `docs/**/chatlogs/` path, no repository-root `*.txt`,
and no `omp-session-*.html` transcript was opened — including the four such files that appeared in
the working tree during this session. No `OCULUS_DESIGN.md` content was read: §G12.6 states its map
"does not amend any phase's current Required inputs" (`docs/design/v3/DESIGN.md:1113`–`:1116`) and
the Phase 14 spec does not list OD. No file from `glsl-transformation-lib` was opened.

### 0.3 Deviations from the assigned reading list and the §G5.3 gate

Phase 14 depends on 5, 6, 7 and 13 (`docs/design/v3/DESIGN.md:626`). §G5.3 invariant 1 requires each
to be verified per the §G1.3 definition before a dependent build session reads it
(`docs/design/v3/DESIGN.md:659`–`:663`, `:357`–`:359`). The gate check:

| Dep | Latest review | Verdict | Interface changed | Verified per §G1.3 | Consumed here? |
|---|---|---|---|---|---|
| 5 | `docs/phase5/reviews/PHASE_5_REVIEW_38.md:43`–`:45` | PASS | no | **yes** | yes, as a contract |
| 6 | `docs/phase6/reviews/PHASE_6_REVIEW_24.md:43`–`:45` | PASS | no | **yes** | yes, as a contract |
| 7 | `docs/phase7/reviews/PHASE_7_REVIEW_32.md:299`–`:301` | PASS-WITH-CORRECTIONS | **yes** | **no** | yes, **provisionally** (deviation 1) |
| 13 | none — `docs/phase13/reviews/` is empty | — | — | **no** | **no** (deviation 2) |

1. **§G5.3.1 gating-invariant departure, maintainer-authorized — Phase 7.** This session consumed
   `docs/phase7/v1/PHASE_7_DOC.md` while it is unverified. Review 32 recorded
   `Counts: blocking=0; corrections=7; notes=3` and `Interface changed: yes`
   (`docs/phase7/reviews/PHASE_7_REVIEW_32.md:300`–`:301`), which engages §G1.3's re-verify clause
   (`docs/design/v3/DESIGN.md:354`–`:356`); round 33 is owed. The document says so of itself: *"Round
   32 most recently changed binding §5, and v1 remains unverified pending a fresh whole-document
   review"* (`docs/phase7/v1/PHASE_7_DOC.md`, closing trailer). The authorization is
   `docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md:57`–`:63`. **Consequence, stated in §5.3:**
   everything this document consumes from Phase 7's §5 is provisional and re-checkable if round 33
   changes it. Working-tree note: at read time `docs/phase7/v1/PHASE_7_DOC.md` and
   `docs/phase7/reviews/PHASE_7_REVIEW_32.md` carried uncommitted modifications adding a §0.36
   round-32 notes fix-up, which states that it *"does not change the interface region, and the fresh
   verification owed by §0.35 remains outstanding."* All Phase 7 citations here resolve against those
   working-tree bytes.
2. **`PHASE_13_DOC.md` is not a valid dependency input.** The commissioning brief records Phase 13
   as unbuilt (`docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md:50`–`:51`). **That premise was falsified
   during this session** and the correction is recorded here rather than smoothed over:
   `docs/phase13/v1/PHASE_13_DOC.md` exists (1 435 lines, thirteen `##` sections, mtime
   2026-08-08 14:04), is **untracked in git**, and `docs/phase13/reviews/` is **empty — zero review
   rounds**. `docs/MOVES.md` carries uncommitted modifications recording a Phase 13 v3 adoption
   (`docs/MOVES.md:82`, `:89`, `:91`). The file is therefore the in-flight product of a concurrent
   Wave-5 build session (`docs/design/v3/DESIGN.md:647`), not a verified dependency.
   **Ruling:** §G5.3 invariant 1 bars it, and the brief's authorization was scoped precisely to
   proceeding *without* it while forbidding invention of its interfaces
   (`docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md:64`–`:68`) — it did not authorize consuming an
   unreviewed sibling-wave draft, which is exactly the high-fan-out propagation §G5.3 exists to
   prevent, and which a concurrent writer may still be changing. This session therefore **did not
   read it**. Every Phase-13-sourced item appears in §5.4 as an explicit **request against a
   spec-derived assumption** (`docs/design/v3/DESIGN.md:2450`–`:2483`), never as an existing
   interface. A future §G1.3 fix-up should reconcile §5.4 against Phase 13's actual §5 once it is
   verified. Recorded again in §11.2 finding C-1.
3. **Governing-revision discrepancy in three dependencies.** `PHASE_5_DOC.md`, `PHASE_6_DOC.md` and
   `PHASE_7_DOC.md` each declare `docs/design/v2.0-RC3/DESIGN.md` as their governing revision in
   their own §0 (`docs/phase5/v1/PHASE_5_DOC.md:18`; `docs/phase6/v1/PHASE_6_DOC.md:10`;
   `docs/phase7/v1/PHASE_7_DOC.md:7`), and `docs/MOVES.md:100` confirms *"Phases 3–8 §0 select
   RC3"*. Yet their latest review rounds were adjudicated **against v3** through the now-deleted
   `verification/targets/` override — Review 37 says it checked the document *"against the supplied
   v3 design override"* (`docs/phase5/reviews/PHASE_5_REVIEW_37.md:8`), and Review 38 repeats it
   (`docs/phase5/reviews/PHASE_5_REVIEW_38.md:6`) — while §G0.4's four-step adoption procedure
   (`docs/design/v3/DESIGN.md:203`–`:220`) was never completed for them. **Consequence:** this
   document cites those three docs' *content* by repo-relative path and line, which is
   revision-independent and safe. It never treats a `§G`-coordinate quoted **inside** those
   documents as a v3 coordinate. A reader must not either. Recorded again in §11.2 finding C-2.
4. **Two stale self-status trailers in verified dependencies.** Both are superseded by their own
   review files, which govern per §G1.3 (`docs/design/v3/DESIGN.md:357`–`:359`). They are noted so a
   reader does not conclude these dependencies are unusable, and the corrections are requested
   upstream in §11.4 — this session may not edit another phase's doc.
   - `docs/phase5/v1/PHASE_5_DOC.md` closing trailer still says Phase 5 is *"**not verified** pending
     a fresh whole-document review"*. Reviews 37 and 38 were exactly those rounds and both returned
     literal PASS with `Interface changed: no`.
   - `docs/phase6/v1/PHASE_6_DOC.md:138`–`:141` still says *"the current bytes remain **not
     verified** until a fresh review returns literal PASS"*. Review 24 returned literal PASS with
     `blocking=0; corrections=0; notes=0` and `Interface changed: no`
     (`docs/phase6/reviews/PHASE_6_REVIEW_24.md:43`–`:45`).
5. **Context-budget error in this phase's own specification.** The spec declares *"**Context
   budget:** ≈ 34k tokens mandatory reading"* (`docs/design/v3/DESIGN.md:2586`). The three existing
   dependency documents alone are ~130k tokens (2 511 + 1 829 + 2 486 lines) before Part I (~50k) or
   RESEARCH.md. Requested upstream in §11.4.
6. **Doc-vs-doc contradiction on the Wave 5 schedule.** Reported with a ruling in §3.4 and §11.2
   finding C-4; not smoothed over, per `docs/design/v3/DESIGN.md:282`–`:284`.

---

## 1. Scope & boundaries

### 1.1 What Phase 14 owns

Phase 14 turns RESEARCH.md §4.8's four **Adapt** rows that name GL work, plus the three additional
scope rows the v3 specification adds, into concrete designs. The objective is stated at
`docs/design/v3/DESIGN.md:2522`–`:2524`: *"replace the reference's legacy per-frame costs with
modern-GL equivalents inside our own pipeline — the only performance work the mission permits."*

Seven owned rows, referred to throughout as **A1–A7**:

| ID | Row | v3 spec | RESEARCH §4.8 / §6.2 origin |
|---|---|---|---|
| **A1** | GL 3.3 sampler objects per stage, replacing per-frame filter re-parameterization of flip textures | `:2529`–`:2532` | §4.8 *"Per-frame filter re-parameterization of flip textures → **Adapt** → GL 3.3 sampler objects"* (`docs/research/v1/RESEARCH.md:643`); §6.2 row at `:772` |
| **A2** | DSA tiering as the facade's **internal** object-creation strategy | `:2533`–`:2535` | REV1 addition; §4.8's *"ARB-era GL entry points (`*ARB`) → **Adapt** → Core equivalents within compat profile"* (`docs/research/v1/RESEARCH.md:641`) is the adjacent row |
| **A3** | PBO + fence-sync async center-depth readback | `:2536`–`:2542` | §4.8 *"Per-frame synchronous center-depth read → **Adapt** → PBO + fence async"* (`docs/research/v1/RESEARCH.md:642`); §6.2 row at `:773` |
| **A4** | Shared-context async shader compile + async `_n`/`_s` atlas upload (**OQ-15**) | `:2543`–`:2547` | §4.8 *"Synchronous on-thread compile of ~40 programs → **Adapt** → Shared-context async compile"* (`docs/research/v1/RESEARCH.md:644`); §6.2 row at `:774` |
| **A5** | KHR_debug labels/groups + debug-context dev mode | `:2548`–`:2551` | §6.2 row at `docs/research/v1/RESEARCH.md:775`; reserved from day one by §G4.5 (`docs/design/v3/DESIGN.md:587`–`:589`) |
| **A6** | Allocation/GC posture, measurement methodology, and the **OQ-22 spot-check ledger** | `:2552`–`:2559` | §4.8 *"Allocation-discipline machinery → **Skip** → Modern GC removes the constraint"* (`docs/research/v1/RESEARCH.md:645`); §6.3 rows at `:784`–`:788` |
| **A7** | Redundant-state audit methodology, our own pipeline only | `:2560`–`:2561` | §6.2 as a whole; bounded by §1.2's first non-goal (`docs/research/v1/RESEARCH.md:72`) |

Phase 14 additionally owns the two OQs assigned to it by §G10: **OQ-15** (`docs/design/v3/DESIGN.md:878`)
and **OQ-22** (`:885`). Both are specified as spikes in §10; neither is resolved here, per §G4.4.

### 1.2 Adjacent concerns, explicitly not owned here

Everything the specification's *Scope — out* list names (`docs/design/v3/DESIGN.md:2563`–`:2565`)
is owned elsewhere. Stated per §G9's anti-sprawl device:

- **Owned by RESEARCH.md §1.2 as a permanent non-goal — not by any phase:** any vanilla-pipeline
  optimization, and any chunk-pipeline work. *"Performance tweaks (chunk pipeline rewrites, fast
  math, smooth FPS, lagometer…) … Other Cleanroom-ecosystem mods own performance"*
  (`docs/research/v1/RESEARCH.md:72`). §4.7's audit method is scoped **by construction** so vanilla
  churn cannot enter it (D-P14-16), rather than by discipline.
- **Owned by Phase 5:** the synchronous filter/wrap design A1 replaces — every `TextureParameters`
  value, the flip/ping-pong state machine, the clear policy, all 37 formats, sizing, resize, the
  shadow estate, and the unit→texture binding table (`docs/phase5/v1/PHASE_5_DOC.md:339`–`:356`).
  Phase 5 hands this phase exactly *"sampler objects, DSA modernization, asynchronous transfers,
  persistent staging, and performance tuning"* (`docs/phase5/v1/PHASE_5_DOC.md:384`–`:386`).
- **Owned by Phase 6:** the synchronous center-depth design A3 replaces, the Appendix D catalog, the
  fixed App B.3 sampler maps, cadences, smoothing math, and the `CenterDepthSource` seam. Phase 6
  hands this phase *"the optional PBO/fence replacement for synchronous center-depth readback. This
  document deliberately leaves that ledger item live"* (`docs/phase6/v1/PHASE_6_DOC.md:285`–`:286`).
- **Owned by Phase 7:** frame orchestration, the reload/pack-switch transaction A4 accelerates, every
  Mixin hook, and every `DebugService` call site A5 needs. Phase 7 hands this phase *"timing and
  resize-consumer seams but defines no optimization policy"*
  (`docs/phase7/v1/PHASE_7_DOC.md:368`–`:369`). **Phase 14 adds no Mixin and no vanilla hook**
  (D-P14-18).
- **Owned by Phase 13:** the `_n`/`_s` companion atlases, noise, and custom textures whose *upload*
  A4 accelerates and whose *filter/wrap and `.mcmeta` blur/clamp* A1 must honor
  (`docs/design/v3/DESIGN.md:2450`–`:2483`). Phase 14 loads no texture and stitches no atlas.
- **Owned by Phase 1:** the `engine.gl` facade shape, `GLCapabilityProfile`, opaque handles,
  `DebugService`'s *interface*, the `-Dschmaloogium.debug.*` namespace, `RecordingGLDevice`, and the
  GL-error drain protocol. Phase 1's scope-out table already assigns this phase *"KHR_debug
  labels/groups, sampler objects, async compile, GC posture"*
  (`docs/phase1/v14/PHASE_1_DOC.md:1490`) and stages the `DebugService` implementation at
  *"`v0.5` / Phase 14"* (`docs/phase1/v14/PHASE_1_DOC.md:1652`, `:4508`).
- **Owned by Phase 2:** the scenes, the tiers, the diffing, the run manifests, and the fixture
  policy that every measurement in §7 and §8 runs inside. Phase 14 defines no scene and no tolerance
  format; it defines what to measure on Phase 2's scenes.
- **Owned by Phase 4:** program compilation, linking, backup chains, and the `Program.use()` state
  barrier. A4 changes *where a shader object is compiled*, never what is compiled or how it is
  resolved.
- **Owned by G8/S2:** compute, SSBOs, image load-store, and indirect dispatch. PD §15's evidence that
  all four run pack-exercised on the 1.12.2 compat context
  (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:741`–`:744`) is carried in §7.5's ledger as
  **feasibility evidence for G8/S2 only** — it is not a Phase 14 work item.

### 1.3 Hard boundary: this phase owns no contract-visible component

Every row A1–A7 is required to be *"a strict behavioral no-op from the pack's perspective"*
(`docs/design/v3/DESIGN.md:2573`–`:2574`), which is §G4.2's rule stated as an architecture
requirement (`:552`–`:558`). Three consequences bind the whole document:

1. **No pack-observable semantics are authored here.** Filter and wrap values are Phase 5's and
   Phase 13's; unit numbers are Phase 6's; `centerDepthSmooth`'s value semantics are Phase 6's; the
   program set is Phase 4's. Phase 14 changes only *how* the driver is asked, never *what* is asked.
2. **Every row ships with a fallback to the reference-faithful path**
   (`docs/design/v3/DESIGN.md:2574`–`:2575`), selected at init from `GLCapabilityProfile` or at
   runtime from a driver policy, and defaulting to the reference-faithful path until that row's
   ledger entry or spike closes (D-P14-17).
3. **Facade extensions are additive** (`docs/design/v3/DESIGN.md:2575`–`:2576`). §2.3 records that
   the additions this design needs are **pure value types only** — no new GL entry point on
   `engine.gl` is required by any of A1–A7.

Because there is no contract-visible component here, §3's conformance map is shaped accordingly: it
maps the contract items this phase must leave **invariant**, plus the §G11.4-gated Pintonium
adoption/rejection rows and the standing do-not-inherit rows (§3.2–§3.3).

---

## 2. Architecture overview

### 2.1 Responsibilities and the module split

§G3.1 is the binding placement rule: `:engine` carries policy and the facade *interfaces*, and the
LWJGL3 implementation lives in `mod.glue` — *"`mod.glue` is the only place LWJGL is called"*
(`docs/design/v3/DESIGN.md:488`, `:598`). Phase 14 is unusual among phases in that **almost all of
it lands in `mod.glue`**, because almost all of it is invisible above the facade.

```text
:engine  com.schmaloogium.engine.gl                (pure values; zero LWJGL — Phase 1's C-1)
           SamplerKey            derived value: the sampler-object state of a texture
           SamplerTier           MULTI_BIND | PER_UNIT | NONE
           DsaTier               CORE_45 | ARB | BIND_TO_EDIT
           DebugTier             KHR | NONE
           AsyncCompileTier      SHARED_CONTEXT | INLINE
           AsyncReadbackTier     PBO_FENCE | SYNCHRONOUS
           GlModernizationPolicy per-row AUTO | FORCE_ON | FORCE_OFF
           GlModernizationPlan   the six tiers actually selected, + why (diagnostic strings)

:mod     com.schmaloogium.mod.glue.gl              (the whole behavior of this phase)
           Lwjgl3GLDevice        (Phase 1's) — A1 and A2 live inside it
             SamplerCache          TextureHandle -> interned sampler; per-unit bind cache
             DsaStrategy           CORE_45 / ARB / BIND_TO_EDIT object-creation strategy
           KhrDebugBackend       Phase 1's DebugService, implemented; balance-safe by construction
           CenterDepthReadback   Phase 6's CenterDepthSource, implemented over a PBO+fence ring
           GlWorkerContext       the single shared GLFW compat context (A4, spike-gated)
           CompileExecutor       Inline (ships now) | Worker (spike-gated); Phase 4/7 see one type

:conformance                                       (Phase 2's; Phase 14 adds runs, not machinery)
           the A3 imperceptibility comparison, the A6 allocation profile, the A7 audit
```

Nothing in this phase enters `mod.mixin`. Nothing in this phase enters `:engine` outside
`engine.gl`, and everything it does enter there is an immutable value with no GL type in its
signature, satisfying Phase 1's C-1 seam constraint.

### 2.2 The two structural ideas

**Idea 1 — the modernization plan is a value, chosen once, at init.** A single
`GlModernizationPlan` is derived from the live `GLCapabilityProfile` and the user policy, before the
first frame, and never changes for the lifetime of the GL context. Every row reads its tier from
that plan. This makes the whole phase testable headlessly (the derivation is a pure function of a
serializable profile — Phase 1 already ships `GLCapabilityProfile` fixtures,
`docs/phase1/v14/PHASE_1_DOC.md:1516`) and makes "which path ran" a single diagnostic line rather
than a per-call branch history.

```java
public record GlModernizationPlan(
        SamplerTier sampler, DsaTier dsa, DebugTier debug,
        AsyncCompileTier compile, AsyncReadbackTier readback,
        List<String> rationale) {

    /** Pure: no GL, no LWJGL, no Minecraft. Phase 1's profile is the only capability input. */
    public static GlModernizationPlan derive(GLCapabilityProfile p, GlModernizationPolicy policy);
}
```

**Idea 2 — a modernization is a no-op or it is not shipped.** Each row is a *strategy substitution
underneath an unchanged contract*, and each carries an explicit equivalence obligation that a test
discharges (§8). Where an equivalence cannot be discharged — A3 and A4 both change *timing*, not
just mechanism — the phase does not hide the change: it contracts it (§5.3 request R-P14→P6-1,
§5.3 request R-P14→P7-1) or it ships the fallback.

### 2.3 What is added to `engine.gl`, and what deliberately is not

The specification requires facade extensions to be additive (`docs/design/v3/DESIGN.md:2575`–`:2576`).
The design finds that **no new GL entry point is needed**:

| Candidate addition | Disposition |
|---|---|
| `SamplerService` (create/parameterize/bind/delete sampler objects) | **Not added.** A1 is served entirely inside the LWJGL3 backend (§4.1): a sampler is a pure function of the `TextureParameters` Phase 5 already supplies, so `TextureService.bindToUnit` can bind the interned sampler alongside the texture with no caller change. Recorded as a *reserved future additive extension* should a phase ever need to bind a sampler independently of a texture; none does today. |
| A DSA verb set | **Not added.** A2 is by definition an internal object-creation strategy (`docs/design/v3/DESIGN.md:2534`–`:2535`). |
| An async-readback verb | **Not added.** A3 implements Phase 6's existing `CenterDepthSource` SPI, which Phase 6 already declares as *"loader-neutral sampling SPI with no Minecraft or GL-name types"* implemented from `mod.glue` (`docs/phase6/v1/PHASE_6_DOC.md:1392`). `FramebufferService.readDepthPixel` (`docs/phase1/v14/PHASE_1_DOC.md:3008`) remains the fallback path, unchanged. |
| An async-compile verb | **Not added** to `engine.gl`. `CompileExecutor` is a `:engine` *policy* interface consumed by the pipeline build, whose two implementations differ only in which thread runs `glCompileShader`; Phase 4 keeps `ShaderService` unchanged. |
| KHR_debug verbs | **Already present.** Phase 1 ships `DebugService` at v0.1 precisely so call sites can label immediately (`docs/phase1/v14/PHASE_1_DOC.md:3060`–`:3065`, `:1652`); Phase 14 supplies the implementation. |
| The pure value types in §2.1 | **Added.** Immutable records/enums with no GL type. Requires the package-placement grant requested in §5.3 (R-P14→P1-2). |

### 2.4 Where each row attaches to the existing pipeline

```text
init (Phase 1 bring-up stage 2)
  └─ GlModernizationPlan.derive(profile, policy)        ── A1 A2 A3 A4 A5 tier selection
     ├─ DsaStrategy installed in Lwjgl3GLDevice          ── A2
     ├─ SamplerCache installed in Lwjgl3GLDevice         ── A1
     ├─ KhrDebugBackend installed as DebugService        ── A5
     ├─ CenterDepthReadback installed as CenterDepthSource (Phase 7 composition step 5)  ── A3
     └─ CompileExecutor installed for the pipeline build ── A4

pack switch / reload  (Phase 7 §4.1 steps 1–18, PHASE_7_DOC.md:690–:729)
  └─ step 7 ProgramRegistryCompiler.compile via CompileExecutor          ── A4 (needs R-P14→P7-1)
     step 9 BufferArchitecture.create → textures allocated               ── A1 A2 interning + DSA
     Phase 13 companion-atlas upload                                     ── A4 (needs R-P14→P13-1)

frame  (Phase 7 FrameHookSink)
  open  → Phase 6 beginFrame → CenterDepthSource.readCenter              ── A3
  enter/exit scopes → DebugService.pushGroup/popGroup                    ── A5 (needs R-P14→P7-2)
  pass  → TextureService.bindToUnit ×16 from Phase 5's snapshot          ── A1 (sampler bound too)
  finish→ sampler units cleared before vanilla resumes                   ── A1 (D-P14-4)

resize (Phase 5 BufferResizeConsumer, PHASE_5_DOC.md:2018 — "Phases 13 and 14")
  └─ SamplerCache re-intern; CenterDepthReadback ring discarded          ── A1 A3

measurement (Phase 2 runs)
  └─ allocation profile, redundant-state audit, imperceptibility compare ── A6 A7 A3
```

---

## 3. Contract conformance map

### 3.1 Framing: an invariance map, not an implementation map

§G9 requires *"every in-scope contract item (RESEARCH.md §3/App row) → the design element
satisfying it → provenance tag. ZERO unmapped rows"* (`docs/design/v3/DESIGN.md:831`–`:835`).
Phase 14 implements **no** contract item: §1.3 establishes that it owns no contract-visible
component, and the specification requires every row to be a behavioral no-op
(`docs/design/v3/DESIGN.md:2573`–`:2574`). The in-scope contract set for this phase is therefore
exactly the set of contract items that this phase's changes **could** perturb, and the mapped design
element is the mechanism that guarantees each one is not perturbed. A row here is satisfied by an
*equivalence obligation with a test*, not by a feature.

| In-scope contract item | Design element that keeps it invariant | Provenance / disposition |
|---|---|---|
| **Fixed texture-unit map** — packs rely on these numbers, units 0–15 by stage | A1's per-unit sampler bind cache is indexed by the **fixed** App B.3 unit, never allocated; §4.1.4. Phase 6 remains sole author of the map | `[V:doc]` `docs/research/v1/RESEARCH.md:628` *"Fixed texture-unit map — **Keep** — Contract-visible (App B.3)"*; consumed from `docs/phase6/v1/PHASE_6_DOC.md:974`–`:991`; **D-P14-3** |
| `depthtex1` is unit 11 | same; A1 introduces no unit-assignment logic at all | `[V:doc]` + Phase 6's ruling at `docs/phase6/v1/PHASE_6_DOC.md:455`, `:987` |
| **Ping-pong buffer + flip semantics, buffer clear rules** — packs depend on exact flip behavior | A1 and A2 change texture *parameterization* and *object creation*; neither reads or writes flip state, side selection, or clear policy, all of which stay in `engine.buffers` | `[V:doc]` `docs/research/v1/RESEARCH.md:627`; Phase 5 ownership at `docs/phase5/v1/PHASE_5_DOC.md:343`–`:345`; **D-P14-1** |
| **Filter and wrap state of every colortex** — `CLAMP_TO_EDGE` S/T; NEAREST for integer formats, LINEAR otherwise | A1 derives `SamplerKey` **from Phase 5's `TextureParameters` value**, never independently; the equivalence test in §8.1 asserts the bound sampler's state equals the texture's configured state for every texture in the estate | `[V:doc]` Phase 5's policy at `docs/phase5/v1/PHASE_5_DOC.md:1042`–`:1045`; **D-P14-1** |
| **Shadow filter, mipmap, and hardware-PCF compare mode** | `SamplerKey` carries `compareMode`; derived from Phase 5's `ShadowTextureResource(hardwareFiltering, mipmap, nearest)` (`docs/phase5/v1/PHASE_5_DOC.md:534`) | `[V:doc]` `docs/research/v1/RESEARCH.md:523`–`:525` via `docs/phase5/v1/PHASE_5_DOC.md:868`; §4.1.3 |
| **`centerDepthSmooth` — declared-trigger readback, App D `float`, tick-domain smoothing** | A3 replaces only *how the depth pixel arrives*. The EMA, the half-life, the tick domain, the declaration trigger, and the uploaded value's type all stay in Phase 6. The added latency is **contracted**, not hidden (§4.3.6, R-P14→P6-1) | `[V:observed]` `docs/research/v1/RESEARCH.md:642`, `:773`; Phase 6's design at `docs/phase6/v1/PHASE_6_DOC.md:928`–`:934`; **D-P14-8** |
| **Frame-begin sampling completes before any buffer resize or clear** | A3 runs *inside* `CenterDepthSource.readCenter`, which Phase 6 calls at step 6 of `beginFrame` (`docs/phase6/v1/PHASE_6_DOC.md:864`–`:867`); A3 issues no resize and no clear and adds no frame moment | governing REV1 constraint at `docs/design/v3/DESIGN.md:526`–`:528`; exported ordering contract at `docs/phase6/v1/PHASE_6_DOC.md:1385` |
| **Everything refreshes on program switch; matrices always upload** | A1–A7 touch no uniform upload and no barrier participant. A5's debug groups are gated `isActive()` and issue no GL when inactive | `[V:observed]` `docs/research/v1/RESEARCH.md:1379`–`:1380` via `docs/phase6/v1/PHASE_6_DOC.md:448`–`:449`; **D-P14-15** excludes contract cadence from optimization |
| **Stage semantics, program set, backup chains** | A4 moves `glCompileShader` to another thread of the same share group. It does not choose, order, name, resolve, or fall back between programs — all Phase 4's | `[V:doc]` `docs/research/v1/RESEARCH.md:626`; **D-P14-11** keeps link, uniform location and the `Program.use()` barrier on the render thread |
| **`_n`/`_s` companion atlases; missing sprites → `0xFF7F7FFF` / zero-specular** | A4's async upload changes *when bytes reach the driver*, never the byte values or the defaults, which are Phase 13's | `[V:doc]` `docs/research/v1/RESEARCH.md:638`; spec at `docs/design/v3/DESIGN.md:2451`–`:2454`; **spec-derived — R-P14→P13-1** |
| **Custom-texture `.mcmeta` blur/clamp and `texture.<stage>.<sampler>` filter/wrap suffixes must be honored** | A1 must derive the `SamplerKey` for a Phase 13 texture from Phase 13's parsed filter/wrap value, or the suffixes silently stop working. The interface does not exist yet | `[V:doc]` spec at `docs/design/v3/DESIGN.md:2467`–`:2478`, including the do-not-inherit note *"filter/wrap suffixes are stripped and ignored there (PD §7.4); ours must honor them"* at `:2476`–`:2478`; **spec-derived — R-P14→P13-2** |
| **`GL_QUADS` stays available; compat profile is mandatory `[D-9]`** | A5's debug-context dev mode adopts a context-flag change **only** if OQ-3 sanctions one; the default plan is *"Make **no** context-flag change"* (`docs/phase7/v1/PHASE_7_DOC.md:2321`). A2/A1 request no profile change and no core-profile entry point that is absent from compat | `[D-9]` `docs/research/v1/RESEARCH.md:103`, `:758`; **D-P14-14** |
| **No UBOs for the pack contract** | nothing in A1–A7 introduces a uniform block; A2's DSA verbs cover texture/framebuffer/buffer object creation only | `[V:doc]` `docs/research/v1/RESEARCH.md:759` |
| **Never crash the client; shaders-off always reachable; vanilla framebuffer path never corrupted** | Every row degrades to its fallback in-place (§6). A1 additionally clears all sampler bindings before control returns to vanilla, because a leftover sampler silently overrides vanilla's texture filtering | §G2.4 rung 5 (`docs/design/v3/DESIGN.md:439`–`:440`); **D-P14-4** |
| **Optimization happens inside our own pipeline only, never in vanilla's** | A7's classification is scoped by construction to facade-visible calls on Schmaloogium-owned or -borrowed subjects; vanilla's draws never reach the facade | `[D-2]` `docs/research/v1/RESEARCH.md:72`; §G2.5 (`docs/design/v3/DESIGN.md:451`–`:456`); **D-P14-16** |
| **Clean code first, optimize with evidence** | A6's four-part justification test (§7.4) is the operative form of this rule; the OQ-22 ledger (§7.5) is its record | `[U]` `docs/research/v1/RESEARCH.md:784`; §G2.5; **D-P14-15** |

There is no unmapped in-scope item: the set above is exhaustive over the contract surfaces the seven
rows can reach, and §1.3 establishes that no other contract surface is in this phase's scope.

### 3.2 Pintonium adoption and rejection rows (§G11.4)

§G9 REV1: *"where the design adopts or rejects a Pintonium mechanism, the row carries the PD citation
and (for contract-visible items) the §G11.4 decision reference"* (`docs/design/v3/DESIGN.md:834`–`:835`).
**No row below is contract-visible** (§1.3), so none requires the §G11.4 contract check that
`docs/design/v3/DESIGN.md:947`–`:951` reserves for contract-visible adoptions; each nevertheless
carries a recorded `D-P14-k` decision and a source-verified provenance tag, because §G11.4's first
bullet applies to every adopted claim.

| Pintonium mechanism | Disposition | Provenance, verified at the cited source line |
|---|---|---|
| **DSA tiering** — `DSACore` (GL 4.5) → `DSAARB` → bind-to-edit, chosen at init | **Adopted as the facade-internal object-creation strategy** (§4.2); non-contract-visible; **D-P14-5** | PD §15 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:737`. `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/IrisRenderSystem.java:33`–`:43]` — `if (GL.getCapabilities().OpenGL45) { dsaState = new DSACore(); } else if (GL.getCapabilities().GL_ARB_direct_state_access) { dsaState = new DSAARB(); } else { dsaState = new DSAUnsupported(); }`; the tier classes are at `:490`, `:494`, `:581` over the interface at `:458` |
| **Routing `bindTextureToUnit` through the DSA tier** | **Rejected.** `glBindTextureUnit` bypasses the active-unit and bound-texture state `GlStateManager` caches, desyncing vanilla — §G4.6's cooperation rule forbids it. Binding stays on the cooperating path under all three tiers; **D-P14-6** | `[V:observed — Pintonium .../IrisRenderSystem.java:312]` — `dsaState.bindTextureToUnit(target, unit, texture);`. §G4.6 at `docs/design/v3/DESIGN.md:593`–`:596` |
| **GL 3.3 sampler objects (`GlSampler`) with per-unit bind caches** | **Adopted** (§4.1); **D-P14-1**, **D-P14-2** | PD §15 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:738`–`:740`. `[V:observed — Pintonium .../gl/sampler/GlSampler.java:10`–`:26]` — constructor takes `(boolean linear, boolean mipmapped, boolean shadow, boolean hardwareShadow)` and sets min/mag filter, `GL_TEXTURE_WRAP_S/T = GL_CLAMP_TO_EDGE`, and `GL_TEXTURE_COMPARE_MODE = GL_COMPARE_REF_TO_TEXTURE` for hardware shadow. Per-unit cache: `.../IrisRenderSystem.java:367`–`:375` — `if (samplers[unit] == sampler) { return; }` |
| **GL 4.5 `glBindSamplers` batching** | **Adopted as the `MULTI_BIND` tier**, and it fits our design *better* than the reference's: our unit map is a fixed, dense 0–15 array, so one `glBindSamplers(0, 16)` replaces the whole per-pass sampler set; **D-P14-2** | `[V:observed — Pintonium .../IrisRenderSystem.java:377`–`:389]` — `hasMultibind` is `OpenGL45 \|\| GL_ARB_multi_bind` (`:45`) and `GL45C.glBindSamplers(0, emptyArray);` clears the whole range in one call |
| **Dynamic per-program texture-unit allocation** | **Rejected — pre-decided.** §G11.4 lists it among the pre-decided rejections: *"dynamic per-program texture-unit allocation (ours: fixed App B.3 map incl. depthtex1 at unit 11)"*; **D-P14-3** | `docs/design/v3/DESIGN.md:954`–`:955`; PD §18 row at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808`; Phase 6 rejected the same at `docs/phase6/v1/PHASE_6_DOC.md:473` |
| **KHR_debug object labels + per-phase push/pop groups behind a flag** | **Pattern adopted, wiring rejected** (§4.5), as the specification instructs at `docs/design/v3/DESIGN.md:2549`–`:2551`; **D-P14-13**, **D-P14-14** | PD §15 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:745`–`:747`. `[V:observed — Pintonium .../gl/debug/GLDebug.java:291]` — `if (Boolean.getBoolean("celeritas.enableGLDebug") && (GL.getCapabilities().GL_KHR_debug \|\| GL.getCapabilities().OpenGL43))`; label/push/pop at `:334`–`:355`; the stack-depth and label-length probes at `:330`–`:331` |
| **GPU-side `centerDepthSmooth` (1×1 R32F ping-pong pass)** | **Not this phase's to adopt, and already rejected upstream.** Phase 6 recorded `D-P6-1` selecting the synchronous CPU readback; §3.4 records the resolution and its consequence for A3 | PD §6.3 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:312`–`:331`; rejection at `docs/phase6/v1/PHASE_6_DOC.md:468`, `:965`–`:968` |
| **PBO / async readback** | **No reference exists.** PD §15: *"**No PBO/async readback anywhere** — Schmaloogium Phase 14's async center-depth design has no reference here"* (`:748`–`:749`). Verified: the single `glReadPixels` in the shader tree is synchronous (`.../IrisRenderSystem.java:190`). A3 is designed from RESEARCH §6.2 alone | `[V:observed — Pintonium .../IrisRenderSystem.java:190]`; `docs/research/v1/RESEARCH.md:773` |
| **Fence-sync objects on the 1.12.2 compat context** | **Adopted, with a provenance refinement to PD §15.** PD's "no async readback" is correct about *readback*, but fence sync itself **is** deployed in the same tree, in the chunk device — which upgrades the availability half of A3's `[U]` claim from unverified to observed. §4.3.3 reuses exactly its non-blocking poll shape; **§11.4 requests the PD clarification** | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/device/GLRenderDevice.java:233]` — `return new GlFence(GL32C.glFenceSync(GL32C.GL_SYNC_GPU_COMMANDS_COMPLETE, 0));`; the non-blocking poll at `reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/sync/GlFence.java:23` — `result = GL32C.glGetSynci(this.id, GL32C.GL_SYNC_STATUS, count);` |
| **Shared-context async compile** | **No reference exists.** A grep of the tree for `glfwCreateWindow`, `glfwMakeContextCurrent` and share-context construction returns nothing; PD §16 records that Pintonium never touches context creation. A4 is designed from RESEARCH §6.2 alone and carries OQ-15 | `docs/research/v1/RESEARCH.md:774`; `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:756`–`:779` via `docs/phase7/v1/PHASE_7_DOC.md:2297`–`:2299` |
| **Compute / SSBO / image load-store / indirect dispatch on the 1.12.2 compat context** | **Not adopted — not this phase's scope.** Carried in §7.5's ledger as feasibility evidence for **G8/S2 only** | PD §15 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:741`–`:744`; G8/S2 at `docs/design/v3/DESIGN.md:788`–`:793` |

### 3.3 Standing do-not-inherit rows, demonstrably handled (§G11.4, §G11.5)

*"Do-not-inherit lists are standing … Phase docs consuming Pintonium material show the relevant rows
handled (doc §3 conformance map)"* (`docs/design/v3/DESIGN.md:958`–`:960`).

| Row | What it is, verified at the source | How this design makes it structurally impossible |
|---|---|---|
| **PD §17 B7 — `GLDebug` group push/pop asymmetry in `setPhase`** (*"pops unconditionally, pushes selectively"*, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:793`) | Verified: `GLDebug.popGroup();` is unconditional, and the following `if (phase != NONE && … )` guards the matching push — `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java:1272`–`:1281]` | §4.5.3 rejects the pop-then-maybe-push wiring entirely and makes imbalance **harmless by construction**: the backend owns a depth counter, an underflowing `popGroup` is a no-op plus one diagnostic, an overflowing `pushGroup` increments a virtual depth so its matching pop also no-ops, and the frame boundary asserts depth 0 and drains otherwise. §8.1's `DebugGroupBalanceTest` exercises Phase 7's `NORMAL`, `EARLY_RETURN` and `THROWN` exits — the exact shapes that produce this bug. **D-P14-13** |
| **PD §18 — dynamic texture-unit allocation** | `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808` | §4.1.4: the bind cache is a fixed `SamplerHandle[16]` indexed by App B.3 unit; there is no allocator and no per-program unit state anywhere in this phase. **D-P14-3** |
| **PD §17 B11 — `GLStateManagerImpl.getColorMask()` hardcodes all-true**; lesson: *"State save/restore must read real state (G4.6 cooperation rule)"* (`:797`) | `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:797` | §4.2.3: this phase adds no state save/restore of its own. Under `BIND_TO_EDIT` the backend restores the *actual* prior binding it read; under the DSA tiers there is nothing to save because nothing is perturbed. §8.1's `BindingNeutralityTest` asserts the observable binding is identical before and after every non-binding verb, under all three tiers. **D-P14-7** |
| **PD §17 B9 — `depthtex2` debug-named `"dephtex2"`; alt texture never labeled** (`:795`) | `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:795` | §4.5.2: labels are emitted from Phase 1's existing `create(String debugLabel)` argument, which every Phase 5 resource already supplies, so there is no second, hand-typed name to misspell and no object that can be created without one. §8.1's `DebugLabelCoverageTest` asserts every created handle in a recorded estate carries a distinct non-blank label. |
| **§G11.2 rule 2 — `glsl-transformation-lib` (AGPL): never copy, never adopt as a dependency** (`docs/design/v3/DESIGN.md:915`–`:919`) | — | Nothing in this phase touches GLSL text. A4 moves an opaque source string to another thread and calls `glCompileShader`; it never parses, rewrites, or inspects it. No file under that dependency was opened (§0.2). |
| **§G11.5 — "Do not copy: … the stubbed/dead features in PD §17"** (`docs/design/v3/DESIGN.md:975`–`:977`) | — | The four mechanisms adopted in §3.2 are all live and pack-exercised, each verified at the source line above; none is a PD §17 stub. |

### 3.4 The PBO item's conditional status — resolved, not re-litigated

The specification makes A3 conditional: *"if Phase 6 adopted GPU-side smoothing (PD §6.3), this item
is **obviated** … If Phase 6 kept the sync readback, the original design stands"*
(`docs/design/v3/DESIGN.md:2536`–`:2542`). The condition is settled, in Phase 6's own recorded
decision, and this document verified all three citations at the line:

- **The decision.** `docs/phase6/v1/PHASE_6_DOC.md:1678` — *"`D-P6-1` | select synchronous CPU
  `centerDepthSmooth`; return empty macro contribution | only candidate expressible by current App D,
  Phase 1, Phase 3, and fixed-unit contracts; §4.8"*.
- **The decision text.** `docs/phase6/v1/PHASE_6_DOC.md:965`–`:968` — *"`[D-P6-1]` selects candidate
  A. `centerDepthMacroContributor()` consequently returns `MacroContribution.Empty` for every
  configuration. **Phase 14's PBO/fence item is not obviated** and remains the sole async-readback
  modernization ledger entry."*
- **The conformance-map rejection row.** `docs/phase6/v1/PHASE_6_DOC.md:468` — *"| PD GPU
  `centerDepthSmooth` | not populated; CPU path selected | **Contract-visible rejection, D-P6-1**"*.

Phase 6's rejection is contract-visible and was verified through §G11.4's decision rule at its own
§4.8 (`docs/phase6/v1/PHASE_6_DOC.md:946`–`:963`), on four independent grounds: the macro would
rewrite the pack's own `uniform float centerDepthSmooth;` declaration into invalid GLSL; App B.3
reserves no unit for a center-depth sampler; the GPU EMA is not bit-identical to the CPU readback;
and the reference's declaration-aware transformer is the prohibited AGPL dependency. Phase 6 also
staged it: *"| GPU center-depth alternative | evaluated now | not scheduled | rejected D-P6-1 |"*
(`docs/phase6/v1/PHASE_6_DOC.md:1654`).

**Ruling.** The condition resolves to the second branch. **A3 is not obviated; the original design
stands in full** — one-frame latency on an already-smoothed value, the synchronous path retained as
fallback and configuration, and an imperceptibility verification specified (§4.3, §8.3). This is not
re-litigated here and no Phase 14 decision reopens it; it is recorded with provenance in §11.3 as
required by the commissioning brief (`docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md:87`–`:99`).

### 3.5 Input contradiction: the Wave 5 schedule versus the gating invariant

Reported, with a ruling, per `docs/design/v3/DESIGN.md:282`–`:284`. All four coordinates were derived
from v3 itself and read.

**The contradiction.** `docs/design/v3/DESIGN.md:647` schedules *"Wave 5: build P8 ∥ P9 ∥ P12 ∥ P13 ∥
P14 → verify all five"* — P13 and P14 **built in parallel**. But `:626` makes P13 a hard dependency
of P14, and `:628`–`:632` states that *"'Depends on' is literal: those phases' `PHASE_<i>_DOC.md`
files are declared inputs — and per the §G5.3 invariant they must be **verified** docs (§G1.3
definition), not merely written."* §G5.3 invariant 1 repeats it as *"the entire point of the
cadence"* (`:659`–`:663`). The two cannot both hold: a phase built in the same parallel wave as its
dependency cannot read that dependency's verified doc, because verification of that wave has not
started. The design sanctions exactly **one** soft dependency, and it is not this one:
`:668`–`:671` — *"**The one sanctioned exception** is Phase 12's soft dependency on Phase 7"* — with
the enabling clause written into the §G5.1 table for Phase 12 alone (`:624` *"1, 3 (soft: 7)"*, and
`:630`–`:632`). Phase 14's row reads a plain *"5, 6, 7, 13"* (`:626`).

**Ruling.** The **gating invariant governs; the wave diagram is the schedule that must yield.**
Three grounds:

1. §G5.3 item 2 is explicit that the diagram is subordinate: *"**Waves are a schedule, not a
   barrier.** A build session may start as soon as *its own* dependencies are verified"*
   (`:664`–`:667`). The waves are a planning convenience; the invariant is the rule. Read that way,
   `:647` is a drafting optimism about how much of Wave 5 can overlap, not a licence.
2. §G5.3 item 1 states the invariant's purpose in terms this case fits exactly: *"an unverified
   error in a high-fan-out doc propagates into every dependent and forces their rebuilds"*
   (`:660`–`:662`). Phase 13's fan-out to Phase 14 is precisely the propagation path.
3. The design knows how to write a sanctioned soft dependency — it does so for Phase 12 in three
   places — and it did not write one for Phase 14. The absence is meaningful, not an oversight to be
   read around.

**Consequence for this document.** Phase 14 was built anyway, on explicit maintainer instruction
(`docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md:57`–`:68`). That is a *disclosed, authorized
departure*, not an application of a rule that does not exist. Concretely: Phase-13-sourced items are
§5.4 requests against the v3 spec, never assumed interfaces (§0.3 item 2); Phase-7-sourced items are
provisional (§5.3). **Requested upstream (§11.4):** either add an explicit soft-dependency clause to
Phase 14's §G5.1 row bounding it to Phase 13's texture-estate lifecycle, in the shape §G5.3 item 3
already uses for Phase 12, or move P14 to a Wave 6 that follows P13's verification. The parallel-wave
line and the literal-dependency line should not both stand unqualified.

---

## 4. Detailed design

Every subsection has the same five parts, because the doc gate demands the same three things of each
row: **design**, **fallback**, and **ledger entry** (`docs/design/v3/DESIGN.md:2578`). The ledger
entries are consolidated in §7.5 and cross-referenced from here.

### 4.1 A1 — GL 3.3 sampler objects per stage

#### 4.1.1 The cost being removed

RESEARCH.md §4.8 records the reference behavior: *"Per-frame filter re-parameterization of flip
textures | **Adapt** | GL 3.3 sampler objects (§6.2)"* (`docs/research/v1/RESEARCH.md:643`), and
§6.2 states the replaced behavior as *"Re-setting filter parameters on flip textures every frame"*
with the risk note *"Low risk; removes per-frame state churn `[U]`"* (`:772`). Two concrete
instances exist in our own designed pipeline:

1. **Flip-side filter state.** Phase 5 allocates a main/alt pair per logical colortex and applies
   filter/wrap to *both* sides (`docs/phase5/v1/PHASE_5_DOC.md:848`, `:1042`–`:1045`). Any per-pass
   or per-flip change of the effective filter — for a pass that declares mipmapping on a buffer it
   reads, for instance — is a `glTexParameteri` on the texture object, which is global to that
   object and therefore has to be set and unset around the pass.
2. **Shadow mipmap generation.** Phase 5's `generateShadowMipmaps` changes a shadow texture's min
   filter to a mipmap filter, generates, then restores: *"One buffer's generation failure does not
   stop later buffers when Phase 5 successfully restores that texture's configured non-mipmap min
   filter … **If restoration fails**, Phase 5 stops before later buffers, atomically performs the
   same containment as `degradeToNeutral(generation, MIPMAP_FILTER_RESTORE_FAILURE)`"*
   (`docs/phase5/v1/PHASE_5_DOC.md:1728`–`:1734`). An entire failure mode, a rejection enum
   constant, a result variant (`Neutralized`), and a containment path exist because a filter value
   lives on the texture object and must be temporarily mutated.

Sampler objects remove the cause of both: **filter, wrap, LOD and compare state moves off the
texture object and onto a per-unit sampler binding**, so the state a program samples with is chosen
at bind time and no texture is ever mutated to change how it is read.

#### 4.1.2 `SamplerKey` — the derived value, in `:engine`

The load-bearing rule is **D-P14-1: a sampler is *derived*, never authored.** Phase 5 (and, at v0.5,
Phase 13) remains the sole author of what filter and wrap a texture has; Phase 14 computes the
sampler-object state as a pure function of that same value. This is what makes A1 a behavioral
no-op rather than a second, competing source of truth.

```java
/** engine.gl — immutable, no GL type, C-1 clean. The sampler-object half of a texture's state. */
public record SamplerKey(
        Filter minFilter,          // NEAREST | LINEAR | NEAREST_MIPMAP_NEAREST | LINEAR_MIPMAP_LINEAR
        Filter magFilter,          // NEAREST | LINEAR
        Wrap   wrapS, Wrap wrapT, Wrap wrapR,   // CLAMP_TO_EDGE | REPEAT | MIRRORED_REPEAT
        CompareMode compareMode,   // NONE | COMPARE_REF_TO_TEXTURE
        CompareFunc compareFunc) {

    /** The whole of A1's equivalence obligation lives in this one function. */
    public static SamplerKey of(TextureParameters p);
}
```

GL divides a texture's parameter set in two, and the split is the design's central correctness
concern (§8.1's `SamplerStateSplitTest` pins it):

- **Sampler state** — min/mag filter, wrap S/T/R, min/max LOD, LOD bias, border colour, texture
  compare mode and func. When a sampler object is bound to a unit, these **override** the texture
  object's values for sampling through that unit. These are exactly `SamplerKey`'s components.
- **Texture state** — base level, max level, swizzle, immutability. These are *not* sampler state and
  must continue to be set on the texture object by `TextureService.setParameters`. Phase 5 explicitly
  has one: the sfb *"old-pack swizzle"* (`docs/phase5/v1/PHASE_5_DOC.md:350`).

So `TextureService.setParameters(t, p)` under `SamplerTier != NONE` becomes: apply the
texture-state half with `glTexParameter*` as today, and route the sampler-state half to
`SamplerCache.intern(SamplerKey.of(p))`, recording the result against the texture handle. No caller
changes. **`TextureParameters`' exact field set is not published by Phase 1** — it appears once, in
the `setParameters` signature at `docs/phase1/v14/PHASE_1_DOC.md:3016`, and nowhere else in that
document — so §5.3 request R-P14→P1-3 asks for it, and the split above is stated as this design's
assumption until it is granted.

#### 4.1.3 `SamplerCache` — interning, in `mod.glue`

```text
SamplerCache
  Map<SamplerKey, SamplerHandle>  interned      — created lazily, destroyed with the estate
  Map<TextureHandle, SamplerKey>  perTexture    — what sampler each texture wants
  SamplerHandle[16]               boundPerUnit  — the fixed App B.3 units, index == unit number
```

- **Cardinality is small and closed.** The distinct sampler states an estate can want are: NEAREST or
  LINEAR × mipmapped or not, all `CLAMP_TO_EDGE` for the colour estate
  (`docs/phase5/v1/PHASE_5_DOC.md:1042`–`:1045`); plus the shadow set, which adds the hardware-PCF
  compare mode and per-texture nearest/mipmap flags from Phase 5's
  `ShadowTextureResource(hardwareFiltering, mipmap, nearest)` (`docs/phase5/v1/PHASE_5_DOC.md:534`);
  plus, at v0.5, Phase 13's custom-texture filter/wrap suffixes and `.mcmeta` blur/clamp, which is
  the one input this design cannot yet name (R-P14→P13-2). A dozen distinct samplers covers the
  whole estate; they are created once per estate generation and shared.
- **Lifecycle is Phase 5's generation, not Phase 14's.** The cache is keyed to the published
  `BufferEstateView.generation()` (`docs/phase5/v1/PHASE_5_DOC.md:2004`). Phase 14 registers as a
  `BufferResizeConsumer` — Phase 5 already names *"Phases 13 and **14**"* as that contract's
  consumers (`docs/phase5/v1/PHASE_5_DOC.md:2018`) — and on a notice re-interns from the new
  estate's parameters and deletes the previous generation's sampler objects. No sampler outlives its
  generation, matching Phase 5's opaque-handle lifetime discipline.
- **Interning is deterministic.** `SamplerKey` is a record, so equality is structural; two textures
  with identical parameters share one sampler object. That is also what makes the `MULTI_BIND` tier
  cheap: a per-pass sampler array is 16 ints assembled from `boundPerUnit`.

#### 4.1.4 Binding, and the rejection of dynamic unit allocation

Binding hangs off the existing verb. Phase 7 binds fixed units from Phase 5's immutable
`TextureBindingSnapshot`, which has *"exactly sixteen ascending fixed-unit rows and total lookup"*
(`docs/phase5/v1/PHASE_5_DOC.md:2017`). Under A1, `TextureService.bindToUnit(unit, t)` binds the
texture as today **and** the sampler `perTexture.get(t)` to the same unit, through a per-unit cache
that skips a redundant call — the shape verified in the reference at
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/IrisRenderSystem.java:367`–`:375]`
(`if (samplers[unit] == sampler) { return; }`).

**`MULTI_BIND` tier.** When `GLCapabilityProfile.atLeast(4,5) || hasExtension("GL_ARB_multi_bind")`,
the sixteen fixed units are bound with one `glBindSamplers(0, ids)` at the start of a pass instead of
up to sixteen `glBindSampler` calls. Our fixed, dense App B.3 map suits this strictly better than the
reference's dynamic map does, because the range is always the same contiguous 0–15. The reference
uses the same call only to *clear* the range (`.../IrisRenderSystem.java:387`).

**`D-P14-3`: dynamic per-program unit allocation is rejected, pre-decided.** §G11.4 lists it among
the standing pre-decided rejections — *"dynamic per-program texture-unit allocation (ours: fixed App
B.3 map incl. depthtex1 at unit 11)"* (`docs/design/v3/DESIGN.md:954`–`:955`) — and Phase 6 already
rejected it for the sampler-uniform side (`docs/phase6/v1/PHASE_6_DOC.md:473`). This design contains
no allocator: `boundPerUnit` is a fixed array whose index *is* the App B.3 unit number, and the only
way an entry changes is a `bindToUnit` call whose unit came from Phase 5's snapshot. There is no
per-program sampler state anywhere in Phase 14, so the divergence cannot be reintroduced by
accident.

#### 4.1.5 `D-P14-4` — clearing samplers before vanilla resumes

This is the row's one genuine new hazard, and it is a §G2.4 rung-5 hazard: *"Nothing in the shader
engine ever crashes the client or corrupts the vanilla framebuffer path"*
(`docs/design/v3/DESIGN.md:439`–`:440`). A sampler object left bound to unit *n* keeps overriding the
filter and wrap of **whatever texture vanilla later binds there**, silently changing vanilla's
rendering with no error and no visible cause in our code. The reference guards the same hazard with
`unbindAllSamplers()` (`.../IrisRenderSystem.java:377`–`:389`).

The design obligation, therefore: **every exit path from shader rendering clears all sixteen units
before control returns to vanilla.** The clear is one `glBindSamplers(0, zeros)` under `MULTI_BIND`,
or up to sixteen `glBindSampler(i, 0)` calls (skipped where `boundPerUnit[i] == 0`) under `PER_UNIT`.
"Every exit path" is defined by Phase 7's frame contract and covers all of them:
`FrameExitKind.NORMAL`, `EARLY_RETURN` and `THROWN` (`docs/phase7/v1/PHASE_7_DOC.md:1383`), plus
`abort(token, reason)` for all six `FrameAbortReason` values, plus a shaders-off publication, plus GL
context loss. Because the clear must survive a throw, it belongs in the backend's own `finally`
around the frame's outermost scope rather than in a caller — Phase 7's driver already guarantees
*"the outer wrapper … attempts coherent finalization in `finally`"*
(`docs/phase7/v1/PHASE_7_DOC.md:2166`–`:2168`), and A1's clear rides that guarantee.
`SamplerLeakTest` (§8.1) asserts, over an arbitrary bounded sequence of scope pushes, pops and
throws, that the recorded call log ends every frame with all sixteen units at sampler 0.

#### 4.1.6 Behavioral-no-op obligation, stated as a test

A1 is a no-op **iff**, for every texture *t* in the estate and every unit *u* it is bound to, the
sampler state applied through *u* equals the sampler state `TextureParameters` for *t* specifies.
`SamplerEquivalenceTest` (§8.1) discharges it directly over a recorded estate. This is stronger than
"we set the same values": it is a property over the whole published estate, re-checked per
generation, so a future Phase 5 or Phase 13 parameter that A1 does not know about **fails the test**
rather than silently degrading a pack's filtering. That failure mode is deliberate; it is the safety
net for R-P14→P13-2 not landing.

#### 4.1.7 Fallback

`SamplerTier.NONE`, selected when `!profile.atLeast(3,3)` or when policy forces it off. Under `NONE`,
`SamplerCache` is not constructed, `setParameters` applies the whole parameter set with
`glTexParameter*` exactly as Phase 5 designs it today, and `bindToUnit` binds only the texture.
This is byte-for-byte the reference-faithful path; it is the shipping default until A1's ledger row
(§7.5, L-2) closes. Tier selection is one line of a pure function over a serializable profile, so
both paths are exercised headlessly from `GLCapabilityProfile` fixtures.

#### 4.1.8 Hand-off to Phase 5 (not a change made here)

Once `SamplerTier != NONE`, Phase 5's `generateShadowMipmaps` no longer needs to mutate a min filter
at all: `glGenerateMipmap` does not require a mipmap min filter to be set, and the *sampling* filter
now comes from the sampler. `MIPMAP_FILTER_RESTORE_FAILURE`, its `Neutralized` result variant, and
its containment path would become unreachable in that mode. **Phase 5 owns that algorithm and this
document does not change it** — it is recorded as hand-off H-P14→P5-1 in §11.5 so Phase 5 can decide
whether to simplify. Under the `NONE` fallback, Phase 5's path stands exactly as written.

### 4.2 A2 — DSA tiering as a facade-internal strategy

#### 4.2.1 The strategy

The specification is precise about the shape: *"adopt PD §15's pattern — GL 4.5 `DSACore` → `DSAARB`
→ bind-to-edit fallback, chosen at init — as the facade's internal object-creation strategy wherever
it stays behavior-invisible (G4.2)"* (`docs/design/v3/DESIGN.md:2533`–`:2535`), and the doc gate
names it: *"DSA tiering integrated as a facade-internal strategy row"* (`:2580`–`:2581`).

```java
/** mod.glue.gl — package-private to the LWJGL3 backend. No :engine type mentions it. */
interface DsaStrategy {
    int  createTexture(int target);
    int  createFramebuffer();
    void texParameteri(int texture, int target, int pname, int param);
    void texImage/texStorage(...);
    void generateMipmaps(int texture, int target);
    void framebufferTexture2D(int fb, int attachment, int target, int texture, int level);
    void drawBuffers(int framebuffer, int[] buffers);
    void readBuffer(int framebuffer, int buffer);
    void blitFramebuffer(...);
}
```

Three implementations, selected once by `GlModernizationPlan.derive`:

| Tier | Gate | Mechanism |
|---|---|---|
| `CORE_45` | `profile.atLeast(4,5)` | `glCreateTextures`, `glTextureParameteri`, `glNamedFramebufferDrawBuffers`, `glGenerateTextureMipmap`, … |
| `ARB` | `profile.hasExtension("GL_ARB_direct_state_access")` | the `ARB_direct_state_access` entry points; the reference's `DSAARB extends DSAUnsupported` layering (`.../IrisRenderSystem.java:494`) is the same idea — override what the tier adds, inherit the rest |
| `BIND_TO_EDIT` | always available | today's path: bind, edit, restore the prior binding |

`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/IrisRenderSystem.java:33`–`:43]`
for the init-time selection, and `:458`, `:490`, `:494`, `:581` for the interface and the three tier
classes.

#### 4.2.2 `D-P14-6` — `bindToUnit` is excluded from the tiering

This is a deliberate, load-bearing divergence from the reference, which routes binding through the
strategy: `dsaState.bindTextureToUnit(target, unit, texture);`
(`[V:observed — Pintonium .../IrisRenderSystem.java:312]`).

We must not. `glBindTextureUnit` (GL 4.5) binds without touching `GL_ACTIVE_TEXTURE`, but 1.12.2's
`GlStateManager` caches **both** the active unit and the per-unit bound texture. Using the DSA form
would leave that cache asserting a binding that is no longer true, and the next vanilla draw that
trusts the cache would skip a `glBindTexture` it actually needed. §G4.6 states the rule this
violates: *"we never bypass it for state it caches (the cache would go stale and break vanilla
rendering)"* (`docs/design/v3/DESIGN.md:595`–`:596`). Phase 5 restates it as its own driver-interaction
rule: *"All GL state cached by `GlStateManager` is changed through Phase 1's backend discipline"*
(`docs/phase5/v1/PHASE_5_DOC.md:2163`).

So: **binding is a real state change and stays on the cooperating path under all three tiers;
DSA covers only object creation and object editing.** The same reasoning excludes any DSA form of
framebuffer *binding*; it does not exclude `glNamedFramebuffer*` *editing*, which perturbs no
binding at all.

#### 4.2.3 The `GlStateManager` cache-staleness analysis

The specification asks for this explicitly (*"DSA edits bypass binding, so state the cache-staleness
analysis"*). The result is the opposite of the intuitive worry, and stating it precisely is what
makes the row safe:

- **DSA does not create staleness; it eliminates the window in which staleness can occur.** A stale
  cache arises when the true GL state and `GlStateManager`'s belief diverge. A DSA edit changes
  neither — it mutates object contents without touching any binding point — so the cache remains
  exactly as correct as it was.
- **`BIND_TO_EDIT` is the tier that *can* create staleness**, and only transiently: it binds
  something to edit it. That is today's situation, and Phase 1's backend already owns the discipline
  of restoring what it perturbed. A2 does not relax it.
- **The real hazard is an inverted assumption, not a stale cache.** Under `BIND_TO_EDIT`, a caller
  could come to rely on the *side effect* that "after `allocate(t, spec)`, `t` is bound". Under
  `CORE_45` that is false. The invariant that makes the tiers substitutable is therefore stated as a
  facade property, not left implicit:

  > **`D-P14-7` — binding neutrality.** `allocate`, `setParameters`, `upload`, `generateMipmap`,
  > `attachDepth`, `attachDepthStencil`, `initializeDepthTextureFromFramebuffer`, `copyDepthToTexture`
  > and `delete` are **binding-neutral**: the observable texture-unit and framebuffer bindings after
  > the call are identical to those before it, under every tier. `bindToUnit`,
  > `bindDefault`/framebuffer binding, and `StateService`'s verbs are the only binding changes.

  Under DSA this holds because nothing is bound; under `BIND_TO_EDIT` the backend saves and restores
  to make it hold. `BindingNeutralityTest` (§8.1) asserts it against a recorded log for all three
  tiers over the same call script, which is also the strongest available proof that the tiers are
  substitutable.
- **One further consequence, recorded so nobody has to re-derive it:** `GlStateManager` is not
  observed *by* this phase at all. §G4.6's cooperation is one-directional and its only *observing*
  consumer is Phase 6's `blendFunc` uniform (`docs/design/v3/DESIGN.md:600`–`:603`). A2 neither
  reads nor writes any state `GlStateManager` caches.

#### 4.2.4 Behavior invisibility, and its one caveat

A2 is invisible above the facade by construction: no `:engine` type names a tier, no facade signature
changes, and `RecordingGLDevice` records *facade verbs*, so the recorded log — the thing every
headless test in Phases 5, 6 and 7 asserts over — is **identical under all three tiers**. That is
precisely §G4.2's *"Non-contract internals may be modernized freely"* (`docs/design/v3/DESIGN.md:557`–`:558`).

The caveat is honest and worth stating: driver bugs in DSA paths are real and are not caught by any
test we can write headlessly, because headless tests never reach a driver. The mitigation is the
tier itself — `GlModernizationPolicy` can force `BIND_TO_EDIT` at runtime without a rebuild — plus
the T1 regression requirement in §9's implementation gate, which runs the matrix on real drivers.

#### 4.2.5 Fallback

`DsaTier.BIND_TO_EDIT`. It is not a degraded mode bolted on afterwards: it is the tier the facade
already implements, so "fallback" here means "do not install the strategy", and the shipping default
until L-6 (§7.5) closes.

### 4.3 A3 — PBO + fence-sync async center-depth readback

**Conditional status: resolved in §3.4. The item is not obviated and the original design stands in
full.**

#### 4.3.1 What Phase 6 actually exposes, and what it forbids

The seam is small and this design consumes it exactly as it exists
(`docs/phase6/v1/PHASE_6_DOC.md:547`–`:554`, `:571`–`:573`):

```java
public interface CenterDepthSource { CenterDepthResult readCenter(CenterDepthRequest request); }

public sealed interface CenterDepthResult {
    record Sample(float depth) implements CenterDepthResult {}
    record Unavailable(String diagnosticId) implements CenterDepthResult {}
}

public record CenterDepthRequest(
    long registryGeneration, long worldEpoch, long frameId,
    int framebufferWidth, int framebufferHeight, int pixelX, int pixelY) {}
```

Four properties of that contract govern the design:

1. **It is called at a fixed frame moment.** Step 6 of Phase 6's `beginFrame` is *"read the previous
   frame's main depth attachment at `(floor(priorFramebufferWidth/2), floor(priorFramebufferHeight/2))`"*,
   and step 7 advances the smoothers (`docs/phase6/v1/PHASE_6_DOC.md:864`–`:867`). So the sync design
   **already** returns a value one frame old; async adds exactly one more frame of age in steady
   state, not two-from-nothing.
2. **`Sample` carries no frame identity.** Phase 6 requires identity echo of
   `FrameUniformSample` but states of this result only that *"`CenterDepthResult.Sample.depth` is
   finite in `[0,1]`"* (`docs/phase6/v1/PHASE_6_DOC.md:587`–`:588`). The type is therefore
   *structurally* able to carry a value sampled at an earlier frame.
3. **`Unavailable` is a designed, safe outcome.** Phase 6 maps it to §G2.4 rung 2a: *"center-depth
   dimensions/FBO unavailable | 2a | retain previous smoothed depth; first unavailable frame leaves
   cell invalid"* (`docs/phase6/v1/PHASE_6_DOC.md:1500`). Warm-up and invalidation can therefore use
   it without inventing a failure mode.
4. **Phase 6 forbids hiding the latency.** *"Phase 14 owns measuring/replacing that stall. Phase 6
   records frame time but **does not hide the cost with an uncontracted one-frame queue**"*
   (`docs/phase6/v1/PHASE_6_DOC.md:1553`–`:1558`). This is the sentence that shapes the whole row:
   the type would *permit* a silent stale value, and Phase 6 has ruled that silence out.

**`D-P14-8`.** The readback is implemented below the seam, in `mod.glue`, **and its latency is
contracted rather than hidden** — §5.3 request R-P14→P6-1 asks Phase 6 to state the sample's age in
its §5 (ideally by adding `long sampledFrameId, long sampledWorldEpoch` to `Sample`). Until that
request lands, `AsyncReadbackTier.PBO_FENCE` is not enabled: shipping a silently-stale value would
be exactly the thing Phase 6 forbade. This is why A3's fallback is not a contingency but the
shipping default (§4.3.7).

#### 4.3.2 The ring

`CenterDepthReadback` implements `CenterDepthSource` in `mod.glue`.

```text
Slot[N], N = 2 (configurable 2..3)
  pbo        one buffer object, 4 bytes  (one GL_DEPTH_COMPONENT / GL_FLOAT pixel)
  fence      GLsync, or absent
  request    the CenterDepthRequest whose transfer this slot holds
  state      EMPTY | IN_FLIGHT | READY
last         the most recent successfully read (depth, request) pair, or absent
```

`N = 2` is the minimum that lets one transfer be in flight while another is read, and it is the
right default because the value is consumed exactly once per frame; `N = 3` exists only as a knob for
drivers that signal late.

#### 4.3.3 The per-frame algorithm

On `readCenter(request)`, on the render thread, in this order:

1. **Validate.** If `request.framebufferWidth() <= 0 || request.framebufferHeight() <= 0`, return
   `Unavailable` without touching GL — Phase 6 requires exactly this
   (`docs/phase6/v1/PHASE_6_DOC.md:585`–`:587`, `:878`–`:880`).
2. **Invalidate on identity change (`D-P14-10`).** If `registryGeneration`, `worldEpoch`,
   `framebufferWidth/Height`, `pixelX` or `pixelY` differ from the slot's recorded request, **discard
   the entire ring** — delete the fences, leave the buffer objects, mark every slot `EMPTY`, clear
   `last` — and continue to step 4. A depth value from a previous world, a previous pack generation
   or a differently-sized framebuffer must never enter the EMA; Phase 6's world-epoch rule exists for
   the same reason (`docs/phase6/v1/PHASE_6_DOC.md:917`–`:918`).
3. **Poll the oldest `IN_FLIGHT` slot, without ever blocking (`D-P14-9`).** Query
   `glGetSynci(sync, GL_SYNC_STATUS)`; the value is ready **iff** it returns `GL_SIGNALED`. This is
   exactly the reference's non-blocking shape —
   `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/sync/GlFence.java:23]`,
   `result = GL32C.glGetSynci(this.id, GL32C.GL_SYNC_STATUS, count);`. If signalled: map the PBO
   (`glMapBufferRange`, `GL_MAP_READ_BIT`, 4 bytes), copy the float, unmap, delete the fence, mark
   the slot `EMPTY`, and set `last`. **The render thread never calls `glClientWaitSync` with a
   nonzero timeout and never calls `glFinish` on this path** — a blocking wait would reintroduce
   precisely the stall the row exists to remove, on a worse schedule than the synchronous read it
   replaces.
4. **Issue the next transfer.** If a slot is `EMPTY`: bind the PBO to `GL_PIXEL_PACK_BUFFER`, issue
   `glReadPixels(pixelX, pixelY, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, 0)` against the depth
   attachment of the framebuffer the request names, unbind the pack buffer, create
   `glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0)`, record the request, mark `IN_FLIGHT`. The
   `glReadPixels` here is asynchronous precisely because a pack buffer is bound — that is the whole
   mechanism.
5. **Return.** `Sample(last.depth)` if `last` is present; otherwise `Unavailable("centerdepth.warmup")`.
   Warm-up is at most the first two frames after start, world change, resize or reload.

Framebuffer binding and pack-buffer binding are both restored before return, per D-P14-7's binding
neutrality.

#### 4.3.4 Interaction with resize and reload

The ring is discarded by step 2 on any identity change, which is already sufficient. It is
*additionally* discarded eagerly on a Phase 5 `BufferResizeNotice` — Phase 5 names Phase 14 as a
consumer of that contract (`docs/phase5/v1/PHASE_5_DOC.md:2018`), and eager discard means the
first frame after a resize costs a warm-up `Unavailable` rather than an invalidation check against a
destroyed FBO. `BufferResizeReason.MAIN_DEPTH_EXTENT` and `DISPLAY_EXTENT` are the two that matter.
Phase 7 already sequences center-depth sampling before any resize is applied
(`docs/phase7/v1/PHASE_7_DOC.md:1046`–`:1051`: *"On an active frame, Phase 6 samples the prior depth
first; Phase 7 then abandons that shader frame … This avoids resizing the source before center-depth
sampling"*), so no in-flight transfer can be reading a framebuffer that is being torn down beneath
it.

#### 4.3.5 Cost, and the honest accounting

Removed: one synchronous `glReadPixels` per rendered frame **when the pack declares
`centerDepthSmooth`**, which Phase 6 characterises as *"deliberately one pipeline stall per rendered
frame"* (`docs/phase6/v1/PHASE_6_DOC.md:1555`). A synchronous single-pixel depth read forces the
driver to flush and wait for all preceding commands.

Added: one 4-byte buffer object per slot (8 bytes total at `N=2`), one fence per frame, one
non-blocking status query per frame, and one 4-byte map/unmap per frame. Allocation: zero in steady
state — the buffer objects are created once per generation, `last` is a mutable primitive pair, and
the map returns a `ByteBuffer` the backend reads one float from without retaining. This is the
allocation posture §7.2 requires of every per-frame path.

The one thing that is *not* free is the extra frame of age, and §4.3.6 is how that is discharged
rather than asserted away.

#### 4.3.6 The imperceptibility verification (doc-gate item)

The specification names it: *"imperceptibility verification (compare `centerDepthSmooth` traces sync
vs async on the Phase 2 scenes)"* (`docs/design/v3/DESIGN.md:2539`–`:2541`), and the doc gate repeats
it (`:2578`–`:2579`). Specified here concretely enough to run; the run itself is a Phase 2 harness
run, and the procedure is repeated as spike step S-22-3 in §10.2.

**What is captured.** The *uploaded* value of the `centerDepthSmooth` uniform, per frame — not an
internal engine value, so no new interface is needed. `-Dschmaloogium.debug.recordGL` wraps the live
device in `RecordingGLDevice` and dumps a `GLCallLog` in the same format the headless tests assert
over (`docs/phase1/v14/PHASE_1_DOC.md:3741`); the `uniforms.upload` records for the
`centerDepthSmooth` location carry the value. The trace for a run is the ordered sequence
`(frameId, value)`. Note the flag puts the device on the per-call `glGetError` cadence, so absolute
frame times from these runs are not valid performance data — this run measures *values*, and §7.3
measures time separately without the flag.

**Over what scenes.** Four families, all Phase 2 assets, run at a fixed frame count:
- **F1 — the Phase 2 fixed static scenes**, as the baseline that must show a near-exact match.
- **F2 — the camera-path motion scenes.** Mandatory, not optional: §G6 REV1 makes motion scenes a
  standing requirement because *"Temporal effects … are the observed conformance long tail"*
  (`docs/design/v3/DESIGN.md:696`–`:699`). A one-frame input delay is by definition a temporal
  effect, so a static-only comparison would be worthless here.
- **F3 — `S-CD-1`, a purpose-built worst case**: a scripted camera path that swings from a wall one
  block away to open sky and back, twice, over 300 frames. This maximises `|raw − smoothed|`, which
  is the quantity the one-frame delay multiplies.
- **F4 — one DOF-consuming classic matrix pack at T1** (SEUS Renewed is the canonical choice), on
  F2's motion scenes, because `centerDepthSmooth` is a depth-of-field focal input and the only
  question that finally matters is whether a viewer can see it.

**Runs.** For each scene: one run with `AsyncReadbackTier.SYNCHRONOUS`, one with `PBO_FENCE`,
identical seeds, identical scripted frame timing, ≥600 frames each, discarding the first 5 frames of
each run as warm-up. `RUN-SCENE-SELFCHECK` must pass first — Phase 7 requires *"repeated frames
identical"* (`docs/phase7/v1/PHASE_7_DOC.md:2277`) — because a non-deterministic scene invalidates
the comparison.

**Criteria.** Let `s_i` and `a_i` be the sync and async uploaded values at frame *i*, and
`d_i = |a_i − s_i|`. All four must hold:

| # | Criterion | Rationale |
|---|---|---|
| **C1 — pure delay** | On a constant-depth scene, the async trace equals the sync trace shifted by exactly one frame, within `1e-6` for every frame after warm-up. | Proves the mechanism is a *delay* and not a *different filter*. If C1 fails, something other than latency changed and the row is wrong, not merely imperceptible. |
| **C2 — bounded by one EMA step** | `max_i d_i ≤ B + 1e-4`, where `B = max_i ( |raw_i − s_i| · (1 − 2^(−Δt_i / h)) )` is computed from the same run's trace, `h` is the effective center-depth half-life in ticks from Phase 6's `UniformConfiguration` (`docs/phase6/v1/PHASE_6_DOC.md:487`), and `Δt_i` is that frame's tick delta. | This is the *analytic* bound on how much a single extra EMA step can move an already-smoothed value. It is derived from the pack's own declared half-life rather than from a number invented here, so it is correct for every pack instead of being tuned to one. Phase 6's smoothing is a time-corrected closed-form EMA (`D-P6-3`), which is what makes the bound exact. |
| **C3 — perceptual** | On F4, the T1 screenshot regression stays within Phase 2's existing T1 tolerance against the async run's own approved baseline, with **zero new outlier pixels** versus the sync run. | The only criterion a user can actually perceive. It deliberately reuses Phase 2's tolerance rather than defining a Phase 14 tolerance. |
| **C4 — no new failure** | Across all four families: zero `Unavailable` results after warm-up, zero GL errors attributed to the readback in `drainErrors`, and no T1 regression on any other matrix pack. | Guards the invalidation logic (D-P14-10) and the ring lifecycle. |

**What constitutes failure, and what happens then.** Any criterion missed is a **failure of the
async row, not of the milestone**. On failure: `AsyncReadbackTier.PBO_FENCE` remains `FORCE_OFF` by
default, the synchronous path ships, the measured numbers and the failing criterion are written into
ledger row L-3 (§7.5) and back into RESEARCH.md §11's OQ-22 status column by the implementation
effort per §G4.4 (`docs/design/v3/DESIGN.md:578`–`:580`), and the item is closed rather than left
open. C1 failing is qualitatively different from C2/C3 failing and is called out separately: C1 is a
**correctness** failure that must be diagnosed before the row can be re-attempted, while C2 or C3
failing is a legitimate "the latency is perceptible on this hardware/pack" result that closes the
row honestly.

#### 4.3.7 Fallback

`AsyncReadbackTier.SYNCHRONOUS`: `mod.glue`'s `CenterDepthSource` calls Phase 1's
`FramebufferService.readDepthPixel(f, x, y)` (`docs/phase1/v14/PHASE_1_DOC.md:3008`), which is
exactly what Phase 6 designs today (`docs/phase6/v1/PHASE_6_DOC.md:880`–`:882`). It is:

- the **shipping default** until both R-P14→P6-1 lands and §4.3.6's criteria pass;
- the automatic selection when `!profile.atLeast(3,2)` (fence sync) or the PBO path reports a GL
  error at first use;
- available as a runtime `FORCE_OFF` with no restart, because the tier is read per call from an
  immutable plan reference that a config change replaces wholesale.

---

### 4.4 A4 — Shared-context async shader compile and async `_n`/`_s` atlas upload

This row carries **OQ-15**. Per §G4.4 this document writes a *spike specification* and does not
resolve the question (`docs/design/v3/DESIGN.md:575`–`:580`); the spike spec is §10.1. What follows
is the architecture the spike tests and the **mandatory synchronous fallback, designed now
regardless of outcome** (`docs/design/v3/DESIGN.md:2546`–`:2547`).

#### 4.4.1 The cost being removed

RESEARCH.md §6.2 states the replaced behavior as *"Multi-second render-thread freeze on pack
switch"* with the risk note *"Driver quality for shared compat contexts varies; needs a synchronous
fallback `[U→OQ-15]`"* (`docs/research/v1/RESEARCH.md:774`), and §4.8 as *"Synchronous on-thread
compile of ~40 programs"* (`:644`). In our own designed pipeline the freeze is Phase 7's pipeline
build: step 7 *"call Phase 4 `ProgramRegistryCompiler.compile`"* and step 9's buffer creation, inside
the transaction Phase 7 runs *"at a no-draw, no-open-frame safe point"*
(`docs/phase7/v1/PHASE_7_DOC.md:696`–`:716`). The second half of the freeze is Phase 13's companion
atlases: two additional full atlases with matching mip chains is the accepted memory cost
(`docs/design/v3/DESIGN.md:2498`–`:2500`), and uploading them is a large synchronous transfer.

#### 4.4.2 Threading model, stated precisely

§G2.3 makes this the **only** sanctioned off-render-thread GL work in the project: *"Permissible
off-thread work: pack file discovery/parsing, preprocessing, expression compilation (Phase 11), and
— only via the Phase 14 shared-context design with its mandatory synchronous fallback — shader
compilation and texture upload. Everything else runs on the render thread."*
(`docs/design/v3/DESIGN.md:412`–`:415`). The model is therefore stated exhaustively.

```text
render thread                          gl-worker thread ("schmaloogium-gl-worker", exactly one)
─────────────                          ────────────────────────────────────────────────────────
main GL context                        GlWorkerContext: one hidden 1×1 GLFW window created with
                                       GLFW_VISIBLE=FALSE, sharing the main context; made current
                                       on this thread once, at creation, and never elsewhere

CompileExecutor.submit(unit) ───────►  glCreateShader / glShaderSource / glCompileShader
                                       glGetShaderiv(COMPILE_STATUS) + info log
                                       glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
                                  ◄─── CompiledShader(id, status, log, sync)
glWaitSync(sync)                       (the share-group visibility barrier)
glAttachShader / glLinkProgram
glGetProgramiv / uniform locations
Program.use() barrier
```

Six rules make it safe, and each exists because of a specific hazard:

1. **Exactly one worker thread and exactly one worker context.** Multiple contexts in a share group
   multiply the driver-bug surface for no benefit — compilation is not the bottleneck once it is off
   the render thread. The worker owns a bounded work queue.
2. **`D-P14-11` — the split is at *link*, not at *compile*.** Shader-object compilation runs on the
   worker; **program creation, attach, link, validate, uniform-location query and the
   `Program.use()` barrier all stay on the render thread.** Three reasons: linking is where the
   shared-context driver bugs concentrate; uniform locations are Phase 4's and Phase 6's per-program
   state whose ownership is render-thread by contract (`docs/phase6/v1/PHASE_6_DOC.md:1516`); and
   `Program.use()` is *"the universal state barrier"* (`docs/design/v3/DESIGN.md:533`–`:535`), which
   must not become concurrent. This split keeps the expensive part (GLSL compilation of ~40 programs)
   off-thread while leaving every ownership rule in the project untouched.
3. **Objects cross the share group through an explicit sync, never implicitly.** The worker signals a
   fence after compiling; the render thread issues `glWaitSync` on it before attaching. A bare
   `glFlush` is the weaker, more commonly-mis-specified form; a fence is exact and costs nothing on
   the render thread because `glWaitSync` does not block the CPU. The reference proves fences work on
   this compat context (`[V:observed — Pintonium .../gl/device/GLRenderDevice.java:233]`).
4. **The render thread never blocks on the worker.** `CompileExecutor` is poll-shaped, not
   await-shaped: the pipeline build asks "is this batch done?" once per frame and keeps rendering
   vanilla meanwhile. This is what requires Phase 7's transaction to be resumable — R-P14→P7-1.
5. **A watchdog bounds every failure.** If a submitted batch has not completed within `T_watchdog`
   (default 5 s, configurable), the worker is declared unhealthy: it is torn down, `CompileExecutor`
   permanently degrades to `Inline` for the remainder of the session, a diagnostic goes to the
   `schmaloogium.compile` channel, and the pipeline build restarts synchronously. A hung worker
   therefore costs one late pack switch, never a hung client (§G2.4 rung 5).
6. **Texture upload obeys the same rules.** The `_n`/`_s` atlas upload is submitted to the same
   worker as a distinct work item (`glBindTexture` + `glTexSubImage2D` on the worker's context, then
   a fence); the texture object is created on the render thread and its *contents* are filled
   off-thread. Storage allocation stays on the render thread so that no `TextureSpec` decision
   crosses a thread.

#### 4.4.3 How a failed or unsupported shared context degrades

Stated as an ordered ladder, because "degrade gracefully" is not a design:

| Stage | Detection | Result |
|---|---|---|
| Creation | `glfwCreateWindow(1, 1, "", NULL, mainWindow)` returns `NULL`, or `glfwMakeContextCurrent` on the worker fails, or `GL.createCapabilities()` on the worker reports a profile inconsistent with the main context | `GlWorkerContext.create()` returns empty; `AsyncCompileTier.INLINE`; one info-level diagnostic; **no user-visible error** — this is a supported configuration, not a failure |
| Driver policy | `profile.vendor()`/`renderer()` is not on the per-family allowlist (`D-P14-12`) | `INLINE`, without attempting creation |
| Probe | The worker compiles one trivial known-good shader at creation and the render thread links and deletes it. Any GL error, a failed compile, or a fence that does not signal within 2 s | tear down the worker; `INLINE` for the session |
| First real batch | any GL error drained after `glWaitSync`/`glAttachShader`, or a link failure that the `Inline` path does not reproduce | tear down; `INLINE`; re-run that pipeline build synchronously; diagnostic naming the driver |
| Watchdog | rule 5 above | tear down; `INLINE`; restart the build synchronously |
| Context loss | the render thread observes GL context loss (Phase 6 has a `GL_CONTEXT_LOSS` reset reason, `docs/phase6/v1/PHASE_6_DOC.md:1383`) | the worker and its context are destroyed with everything else; the plan is re-derived at the next bring-up |

**`D-P14-12` — the driver allowlist defaults to deny.** No driver family is eligible for
`SHARED_CONTEXT` until OQ-15's spike positively records a pass for that family. The policy is a small
data table keyed on `GLCapabilityProfile.vendor()` and `renderer()` substrings, shipped as data so a
family can be enabled or disabled without a code change, and overridable per-user by
`GlModernizationPolicy.FORCE_ON`/`FORCE_OFF`. This is what *"selected at runtime per driver"*
(`docs/design/v3/DESIGN.md:2547`) means concretely, and defaulting to deny is what makes a
never-run spike safe.

#### 4.4.4 The mandatory synchronous fallback, designed now

```java
/** :engine policy interface. Both implementations satisfy it identically from the caller's view. */
public interface CompileExecutor {
    /** Non-blocking. Returns a token the caller polls. */
    CompileBatchToken submit(List<ShaderCompileUnit> units);
    /** Never blocks. PENDING | READY(results) | FAILED(diagnosticId). */
    CompileBatchStatus poll(CompileBatchToken token);
}
```

- **`InlineCompileExecutor`** — `submit` compiles every unit on the calling (render) thread and
  returns a token that `poll` immediately reports `READY`. The pipeline build's control flow is
  therefore *identical* in both tiers: submit, poll, proceed when ready. Under `Inline` the first
  poll always succeeds, so the build completes in one call exactly as Phase 7 designs it today.
- **`WorkerCompileExecutor`** — the §4.4.2 model.

This is the design decision that makes a failed spike cost nothing: because the synchronous path is
expressed through the *same* interface with the same call sequence, switching tiers changes no
control flow in Phase 4 or Phase 7, and `Inline` is what ships on day one. §G4.4's requirement that
*"a failed spike never stalls a milestone"* (`docs/design/v3/DESIGN.md:578`–`:579`) is satisfied
structurally rather than by intention.

#### 4.4.5 What this needs from Phase 7, and what it needs from Phase 13

- **Phase 7 (R-P14→P7-1).** Phase 7's build transaction is written as a single render-thread sequence
  of eighteen steps at one safe point (`docs/phase7/v1/PHASE_7_DOC.md:690`–`:729`), and its exposed
  `ReloadStatus.Building()` carries no progress (`docs/phase7/v1/PHASE_7_DOC.md:1689`). Async compile
  needs the transaction to be **resumable across frames**: step 7 submits, later frames poll, and the
  publication steps 8–18 run in the frame where the batch reports `READY`, with vanilla rendering
  continuing meanwhile. Phase 7's §5 does not currently express that. Requested, not assumed.
  **If declined, A4 ships `Inline` and nothing else changes.**
- **Phase 13 (R-P14→P13-1).** The `_n`/`_s` upload half needs Phase 13's companion-atlas
  load/stitch/reload lifecycle to expose an upload seam that can accept a staged upload — that is,
  the point at which pixel bytes become a `TextureData` must be separable from the point at which
  the texture becomes bindable, with a completion signal. The v3 spec defines the lifecycle
  (`docs/design/v3/DESIGN.md:2451`–`:2458`, `:2482`–`:2483`) but no interface exists (§0.3 item 2).
  **If unavailable, A4 covers shader compile only and the atlas upload stays synchronous** — the two
  halves are independent work items sharing one worker.
- **Phase 1 (R-P14→P14-P1-4).** `RecordingGLDevice` is *"not made thread-safe to cover the Phase 14
  exception. If off-thread uploads ever need recording, that is a Phase 14 request against this
  document"* (`docs/phase1/v14/PHASE_1_DOC.md:4322`). Taking that invitation up in §5.3.

#### 4.4.6 Fallback

`AsyncCompileTier.INLINE`, which is today's behavior exactly, shipping by default, and the automatic
result of every rung of §4.4.3's ladder. Ledger row L-4 (§7.5).

### 4.5 A5 — KHR_debug labels and groups, plus debug-context dev mode

#### 4.5.1 What already exists

Phase 1 ships the *interface* at v0.1 and stages the *implementation* at v0.5 to this phase:
*"`DebugService` (§4.7.4) exists as an interface at v0.1 so call sites can label objects immediately;
`-Dschmaloogium.debug.glLabels` (§4.9.3) gates it; the implementation is `v0.5` / Phase 14 (§9)"*
(`docs/phase1/v14/PHASE_1_DOC.md:1652`), with the milestone row *"`schmaloogium.debug.glLabels` |
`v0.5` | Phase 14"* (`:4508`). The interface is:

```java
public interface DebugService {          // docs/phase1/v14/PHASE_1_DOC.md:3060–:3065
    void pushGroup(String label);
    void popGroup();
    void label(GLHandle handle, String label);
    boolean isActive();          // false unless a debug context and the dev flag are both on
}
```

Phase 5 already reserves the call sites: *"`DebugService` labels are present from v0.1 and **activate
only at Phase 14**"* (`docs/phase5/v1/PHASE_5_DOC.md:2167`).

#### 4.5.2 Labels

`KhrDebugBackend.label(handle, name)` issues `glObjectLabel(type, id, name)`. Two design points:

- **Labels are emitted from the `debugLabel` argument the facade already carries.** Phase 1's
  `TextureService.create(String debugLabel)` and `FramebufferService.create(String debugLabel)`
  (`docs/phase1/v14/PHASE_1_DOC.md:3014`, `:2944`) already require every created object to be named
  at creation. The backend labels at creation time rather than requiring a separate call. That is
  the structural handling of PD B9's *"depthtex2 debug-named `"dephtex2"`; alt texture never
  labeled"* (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:795`): there is no second,
  hand-maintained naming path to drift, and no object can be created unnamed.
- **Length is clamped.** `GL_MAX_LABEL_LENGTH` is probed once and labels are truncated to it; the
  reference probes the same limit (`[V:observed — Pintonium .../gl/debug/GLDebug.java:331]`).
  Exceeding it is a `GL_INVALID_VALUE`, which would pollute Phase 1's error drain and produce
  spurious rung-2 uniform disables — a real hazard given how `drainErrors` attributes windows
  (`docs/phase1/v14/PHASE_1_DOC.md:4220`).

#### 4.5.3 Groups — `D-P14-13`, balance-safe by construction

This is where the do-not-inherit row lives. The specification is explicit: *"copy the **pattern**
(object labels + per-phase push/pop groups behind a debug flag, PD §15), **not the wiring** —
Pintonium's `setPhase` has a push/pop imbalance bug (PD B7)"* (`docs/design/v3/DESIGN.md:2549`–`:2551`).
Verified at the source: the pop is unconditional and the matching push is inside an `if`
(`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java:1272`–`:1281]`).

The wiring is rejected outright — this design has no `setPhase`-shaped verb that pops and
conditionally pushes. But rejecting one call site is not enough, because the same bug can be
reintroduced by any caller. So imbalance is made **structurally harmless**, in the backend:

1. The backend owns an integer `depth`, and a `virtualDepth` for pushes it declined to issue.
2. `pushGroup(label)`: if `depth < GL_MAX_DEBUG_GROUP_STACK_DEPTH − 1`, issue `glPushDebugGroup` and
   `depth++`; otherwise `virtualDepth++` and issue nothing. The reference probes the same limit and
   guards the same overflow (`[V:observed — Pintonium .../GLDebug.java:330`, `:342`–`:347]`).
3. `popGroup()`: if `virtualDepth > 0`, `virtualDepth--` and issue nothing — so an unmatched *virtual*
   push cannot cause a real pop that unbalances the driver's stack. Else if `depth > 0`, issue
   `glPopDebugGroup` and `depth--`. Else — an **underflow** — issue nothing, and emit one
   rate-limited diagnostic per session on the `schmaloogium.gl` channel naming the call site.
   Underflow is exactly PD B7's shape, and here it is a logged no-op rather than a corrupted stack.
4. **Frame-boundary assertion.** At Phase 7's `finish`/`abort`, `depth + virtualDepth` must be zero.
   If it is not, drain to zero with real pops and emit one diagnostic. A leaked group would otherwise
   nest the *next* frame inside the previous one and make every subsequent capture unreadable — the
   symptom that makes B7 corrosive rather than cosmetic.

`DebugGroupBalanceTest` (§8.1) drives arbitrary bounded push/pop/throw sequences across Phase 7's
three exit kinds — `NORMAL`, `EARLY_RETURN`, `THROWN` (`docs/phase7/v1/PHASE_7_DOC.md:1383`) — and
asserts the depth returns to zero and the recorded pop count never exceeds the recorded push count.
Those three exits are precisely where an unbalanced-scope bug lives.

**Optional, non-blocking:** §5.3 asks Phase 1 for an additional scoped form
(`AutoCloseable group(String label)`) so call sites cannot be written unbalanced at all. The
construction above is safe without it; the scoped form would make the safety local rather than
central.

#### 4.5.4 Group placement — Phase 7's call sites, not Phase 14's hooks

Groups are only useful if they bracket meaningful work, and every meaningful boundary belongs to
Phase 7: `enter(token, RenderSection)` / `exit(token, scope)`, the shadow-pass invocation, the
deferred/composite/final pass executor, and the pipeline build. **Phase 14 adds no hook and no call
site** (`D-P14-18`); it asks Phase 7 to call `debug().pushGroup`/`popGroup` at boundaries it already
owns — R-P14→P7-2. The cost when inactive is one field read returning `false`, because
`KhrDebugBackend` is replaced wholesale by a no-op implementation when the tier is `NONE` (§4.5.6),
so there is not even a branch in the shipping configuration. Phase 7's own posture is compatible:
*"Debug hook timing uses preallocated counters and is off by default"*
(`docs/phase7/v1/PHASE_7_DOC.md:2196`).

This is also what makes §4.7's audit tractable: the frame and pass groups are the segmentation keys
the redundant-state audit uses to slice a `GLCallLog` into frames and passes. A5 and A7 reinforce
each other, which is why both flags are required together for an audit run.

#### 4.5.5 Debug-context dev mode, and finding C-3

The specification asks for *"KHR_debug labels/groups + debug-context dev mode"*
(`docs/design/v3/DESIGN.md:2548`). Two facts constrain it, and their combination is a genuine
cross-document contradiction that this document reports rather than works around.

**Fact 1 — we cannot count on getting a debug context.** Context creation is OQ-3, owned by Phase 7,
and its designed default is explicit: *"**Failure/fallback.** Make **no context-flag change**"*
(`docs/phase7/v1/PHASE_7_DOC.md:2320`–`:2323`), with a context-hint change adopted *"only if it is
sanctioned by Cleanroom, preserves legacy fixed-function behavior, and passes client startup/resize
on every spike platform"* (`:2317`–`:2319`). PD §16 records that the reference never touches context
creation at all.

**Fact 2 — KHR_debug does not need one.** Object labels and debug groups are ordinary KHR_debug
entry points available on any context that exposes the extension or GL 4.3. The reference proves it:
its gate is the extension or version plus the flag, with **no debug-context condition** —
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/debug/GLDebug.java:291]`,
`if (Boolean.getBoolean("celeritas.enableGLDebug") && (GL.getCapabilities().GL_KHR_debug || GL.getCapabilities().OpenGL43))`.
A debug *context* affects only how much the driver volunteers through the message callback.

**Finding C-3 (reported in §11.2).** Phase 1 defines `isActive()` as *"false unless a debug context
and the dev flag are both on"* (`docs/phase1/v14/PHASE_1_DOC.md:3064`). Under Phase 7's OQ-3 default
plan there is no debug context, so `isActive()` would be **permanently false** and the entire
`DebugService` — an affordance §G4.5 reserves from day one (`docs/design/v3/DESIGN.md:587`–`:589`)
— would be dead code on every shipping configuration. **Ruling:** the two documents disagree, and
KHR_debug's own capability model settles it — labels and groups do not require a debug context. §5.3
raises R-P14→P1-1 to redefine the gate. The design proceeds on the corrected gate and records the
dependency:

**`D-P14-14` — the gate is `(hasExtension("GL_KHR_debug") || atLeast(4,3)) && -Dschmaloogium.debug.glLabels`.**
A debug context is an *enhancement*, never a precondition.

The enhancement itself, in two tiers:

- **Always available:** `glEnable(GL_DEBUG_OUTPUT)`, `glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS)` and
  `glDebugMessageCallback` on the existing context, routing messages to Phase 1's `schmaloogium.gl`
  log channel with severity mapped onto Phase 1's log levels. KHR_debug permits message generation on
  a non-debug context; drivers may volunteer less, which is a quality difference, not a functional
  one. `GL_DEBUG_OUTPUT_SYNCHRONOUS` is what makes a message's stack trace point at the offending
  call, which is the whole value in a dev session.
- **If and only if OQ-3 sanctions a hint change:** a new flag `-Dschmaloogium.debug.glContext`
  (requested into Phase 1's namespace at §5.3, owner P14, milestone v0.5) additionally requests
  `GLFW_OPENGL_DEBUG_CONTEXT`. This is gated on someone else's spike outcome and is designed, not
  scheduled.

Neither tier is ever on by default. Both are the payoff RESEARCH.md §6.2 records: *"KHR_debug
labels/groups + debug context … Dev-only; pairs with RenderBook's Nsight integration `[V:web]`"*
(`docs/research/v1/RESEARCH.md:775`) — object labels and groups are what turn an Nsight or RenderDoc
capture of our frame from an undifferentiated call list into the named pass structure of
`docs/design/v3/DESIGN.md:514`–`:519`.

#### 4.5.6 Fallback

`DebugTier.NONE`: `DebugService` is a no-op implementation — every verb returns immediately,
`isActive()` returns `false`, no counter is maintained. Selected when the extension and version are
both absent or the flag is unset, which is **every shipping configuration**. This is also the
default: A5 costs a shipping user nothing. Note the one non-obvious coupling, which Phase 1 states on
both sides: setting `glLabels` also puts the device on the per-call `glGetError` cadence
(`docs/phase1/v14/PHASE_1_DOC.md:3742`), so a developer who enables labels is choosing a frame-rate
cost knowingly, and §7.3's timing runs must not set the flag.

### 4.6 A6 — Allocation and GC posture, and the measurement methodology

The spot-check ledger this row owns is §7.5; the OQ-22 spike specification is §10.2. This subsection
is the *method* the ledger and the spike both use.

#### 4.6.1 The posture, restated as an obligation on this phase

§G2.5: *"Clean code first, optimize with evidence (§6.3): modern GC removes OF's
allocation-discipline constraint; do not replicate array caches / mutable-pose machinery. Initial
performance is allowed to be worse than OF-with-shaders"* (`docs/design/v3/DESIGN.md:451`–`:456`),
resting on the `[U]`-tagged claim that *"generational ZGC on Java 25 makes straightforward code
acceptable"* (`docs/research/v1/RESEARCH.md:784`).

That claim is unverified, and this phase is where it gets checked. **Phase 14's job is not to
optimize; it is to produce the evidence that says whether optimization is needed, and to refuse the
optimizations the evidence does not justify.** Concretely, the phase asserts a falsifiable prediction:
the sibling designs already claim zero steady-state allocation in their frame paths — Phase 5's
*"The steady frame allocates no textures, FBOs, collections, or unit maps"*
(`docs/phase5/v1/PHASE_5_DOC.md:2146`), Phase 6's *"Steady state must allocate nothing"*
(`docs/phase6/v1/PHASE_6_DOC.md:1529`), Phase 7's *"Hot hook paths allocate no collections or
strings"* (`docs/phase7/v1/PHASE_7_DOC.md:2197`). A6 verifies those three claims rather than
trusting them, and a violation is a **finding against the owning phase**, not a Phase 14 work item.

#### 4.6.2 The measurement procedure

Run on Phase 2's fixed scenes, which Phase 7 requires to be deterministic
(`RUN-SCENE-SELFCHECK`, *"with repeated frames identical"*, `docs/phase7/v1/PHASE_7_DOC.md:2277`).

1. **Preconditions.** `RUN-SCENE-SELFCHECK` green; no `-Dschmaloogium.debug.*` flag set (both
   `recordGL` and `glLabels` change the `glGetError` cadence and would corrupt timing —
   `docs/phase1/v14/PHASE_1_DOC.md:3741`–`:3742`); a pinned JVM and a pinned loader per Phase 1's
   version discipline.
2. **Runs.** For each scene × each of {shaders off, internal pack, one classic matrix pack at T1}:
   three runs of 900 frames, discarding the first 300 as warm-up; report the median run.
3. **Instruments.** JFR with `jdk.ObjectAllocationSample` (allocation by call site),
   `jdk.GCPhasePause` (pause distribution), `jdk.ExecutionSample` (CPU profile), and a frame-time
   histogram. JFR is chosen because it is in-JVM, low-overhead, and needs no dependency; Phase 7
   already names JFR for its own hook overhead work (`docs/phase7/v1/PHASE_7_DOC.md:2342`).
4. **Baselining — the non-goal made structural.** Every number is reported as a **delta against the
   shaders-off run of the same scene on the same machine**. Vanilla's absolute allocation and frame
   time are *not* results and are never optimized: §1.2's first non-goal makes vanilla performance
   somebody else's product (`docs/research/v1/RESEARCH.md:72`). Reporting only deltas means a
   vanilla regression cannot even be expressed in this method's output.
5. **Attribution.** Allocation samples are filtered to stack frames in `com.schmaloogium.*`, then
   attributed to the owning phase by package (`engine.buffers` → 5, `engine.uniforms` → 6,
   `engine.frame` → 7, and so on). A site outside our packages is not our finding.
6. **Outputs.** A run manifest of counts and medians — **no images, no pack source text**, per §G6's
   derived-artifact policy (`docs/design/v3/DESIGN.md:718`–`:725`). The manifest lives in the
   local/CI cache like every other Phase 2 derived artifact, never in the repository.

#### 4.6.3 `D-P14-15` — when an optimization is justified

The four-part test. **All four must hold**, and anything that fails one is recorded in the ledger and
*not done*:

1. **Ownership.** The site is inside Schmaloogium's own pipeline. Vanilla and chunk-pipeline sites
   are out of scope permanently, not deferred (`docs/research/v1/RESEARCH.md:72`).
2. **Measured cost.** ≥1% of median frame time, **or** ≥1 MB/s of steady-state allocation, measured
   by §4.6.2 on at least one matrix pack at T1. A predicted cost is not a cost.
3. **No-op with a fallback.** The change is a strict behavioral no-op (§G4.2) and ships with a
   fallback to the path it replaces — the same bar every row in this phase meets.
4. **No contract complexity.** It does not add branching or state to a contract-visible component.
   A contract-visible component is *"everything in RESEARCH.md §3 and Apps A–D, F"*
   (`docs/design/v3/DESIGN.md:554`–`:555`) and *"may not 'improve' semantics"* (`:556`–`:557`).

Two exclusions are called out because they will otherwise be proposed by someone reading a profile:

- **Unconditional matrix uploads are not a target.** Phase 6's `D-P6-10` uploads matrices every
  activation because App D's cadence contract says so (`docs/phase6/v1/PHASE_6_DOC.md:1687`,
  `docs/research/v1/RESEARCH.md:1380`). They will appear in every profile as repeated GL traffic.
  They fail test 4 and are permanently out of bounds.
- **Everything-refreshes-on-program-switch is not a target** for the same reason
  (`docs/research/v1/RESEARCH.md:1379`).

#### 4.6.4 Fallback

A6's fallback is the null change: if the measurement says the clean-code posture holds, nothing is
optimized and every ledger row closes as "no action". That is a *success*, not a failure to deliver
— it is what §G2.5 predicts and what the `[U]` tag at `docs/research/v1/RESEARCH.md:784` asks to be
checked. If a specific measurement says otherwise, that finding routes to the owning phase through
§11.5's hand-off list.

### 4.7 A7 — Redundant-state audit methodology

*"Redundant-state audit methodology: identifying per-frame GL churn in our own pipeline (never
vanilla's — §1.2)"* (`docs/design/v3/DESIGN.md:2560`–`:2561`). A repeatable procedure, not an
aspiration.

#### 4.7.1 `D-P14-16` — the non-goal is honored structurally, not by discipline

The audit's input is a `GLCallLog` produced by `RecordingGLDevice`, which records **facade verbs**
(`docs/phase1/v14/PHASE_1_DOC.md:3741`). Vanilla's own rendering never reaches the facade — Phase 1
records that GL traffic exists which the facade never sees, which is exactly why its error attribution
is window-scoped (`docs/phase1/v14/PHASE_1_DOC.md:4220`). Therefore **vanilla GL churn is invisible
to this method by construction.** The §1.2 non-goal is not a rule the auditor must remember to obey;
it is a property of the instrument. A second filter drops any record whose subject handle is not
Schmaloogium-owned or Schmaloogium-borrowed, catching the one boundary case: ordinary foreign
textures, which Phase 1 makes *"bind-and-label-only"* (`docs/phase1/v14/PHASE_1_DOC.md:1622`) and
which we bind but do not own.

#### 4.7.2 The procedure

1. **Capture.** One deterministic Phase 2 fixed scene, with **both** `-Dschmaloogium.debug.recordGL`
   and `-Dschmaloogium.debug.glLabels` set. ≥120 consecutive steady frames after warm-up. The log is
   a bounded ring, default 100 000 calls, and over-capacity discards are counted
   (`docs/phase1/v14/PHASE_1_DOC.md:3741`) — **a run whose discard count is non-zero is invalid** and
   must be re-taken with a larger ring or fewer frames. This is stated because a silently truncated
   log would produce confidently wrong counts.
2. **Segment.** Split the log into frames and passes using A5's debug groups as the segmentation
   keys. This is why both flags are required together (§4.5.4) and is the concrete reason A5
   precedes A7 in the implementation order (§12).
3. **Determinism gate.** Any two steady frames of the same scene must produce **identical** classified
   sequences. If they do not, the scene is not steady and the audit is invalid — stop and fix the
   scene. Phase 7's `RUN-SCENE-SELFCHECK` already asserts frame identity at the image level
   (`docs/phase7/v1/PHASE_7_DOC.md:2277`); this is the call-level analogue and is strictly stronger.
4. **Classify.** For each mutating record, in order, maintain the last *effective* value per
   `(verb, subject)` and assign one of:
   - **Redundant-identical** — same verb, same subject, same arguments as the last effective value,
     with no intervening change to that state. Pure churn.
   - **Redundant-restore** — a set/restore pair around a region containing no call that observes the
     state. The shadow-mipmap filter pair of §4.1.1 is the archetype.
   - **Necessary** — everything else.
   - **Contract-mandated** — necessary *and* required by a contract cadence: matrix uploads, the
     program-switch refresh sweep (§4.6.3's exclusions). Counted separately so it never contaminates
     a ranking.
5. **Rank.** By `count × cost class`, where the cost classes are: *cheap* (`glUniform*`,
   `glBindTexture`, `glBindSampler`), *medium* (`glBindFramebuffer`, `glTexParameter*`,
   `glDrawBuffers`), *expensive* (`glReadPixels`, `glGenerateMipmap`, `glFinish`, buffer
   re-allocation). The classes are ordinal, not calibrated: they order candidates for investigation;
   test 2 of §4.6.3 supplies the actual cost before anything is changed.
6. **Route.** Each surviving candidate goes through §4.6.3's four-part test. A candidate in Phase 14's
   own code is a Phase 14 work item. A candidate in Phase 5's, 6's or 7's call sequence is a §11.5
   hand-off to that phase — **this phase does not edit another phase's call sequence.**
7. **Record.** A counts manifest in the local/CI cache, per §G6's derived-artifact rules; no image and
   no pack text.

#### 4.7.3 Falsifiable predictions, recorded now

Stated so the first audit either confirms or refutes them, rather than being read as confirmation of
whatever it finds:

| Prediction | Basis | If refuted |
|---|---|---|
| Per-texture `setParameters` on flip textures is the largest *redundant-identical* population before A1, and near zero after | the row's whole premise (`docs/research/v1/RESEARCH.md:643`, `:772`) | A1's ledger row L-2 records "no measurable churn removed"; the tier still ships for the shadow-mipmap simplification, or is dropped |
| Shadow-mipmap filter set/restore is the archetypal *redundant-restore* population, eliminated by A1 | `docs/phase5/v1/PHASE_5_DOC.md:1728`–`:1734` | as above; H-P14→P5-1 is withdrawn |
| Sampler-integer uploads are already near zero | Phase 6 predicts *"Sampler integers usually skip after the first activation of a linked program"* (`docs/phase6/v1/PHASE_6_DOC.md:1542`–`:1543`) | a finding **against Phase 6**, routed by §11.5 |
| Frame-path allocation is zero in `com.schmaloogium.*` | the three sibling claims in §4.6.1 | a finding against the owning phase; A6 is the instrument that produces it |

#### 4.7.4 Fallback

The audit is a procedure, so its "fallback" is its degraded mode: if `RecordingGLDevice` cannot
produce a valid log (ring overflow, non-deterministic scene) the audit **does not run and reports
nothing**. It never produces partial or estimated counts. An audit that cannot meet its determinism
gate is an invalid audit, and saying so is the whole point of gate 3.

---
