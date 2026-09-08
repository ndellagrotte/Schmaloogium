# Schmaloogium — Phase 14: GL modernization & performance — Architecture

## 0. Header

- **Phase:** 14 — GL modernization & performance
- **Milestone:** v0.5 + quality-of-life
- **Module/package:** `:engine` (`com.schmaloogium.engine.gl` — pure value additions only) and `:mod`
  (`com.schmaloogium.mod.glue` — the LWJGL3 backend where every behavior in this phase lives)
- **Declared dependencies:** Phases 5, 6, 7, 13 (`docs/design/v3/DESIGN.md:626`)
- **Assigned open questions:** OQ-15, OQ-22 (`docs/design/v3/DESIGN.md:626`, `:878`, `:885`)
- **Governing design:** `docs/design/v3/DESIGN.md`
- **Design status:** documentation integration fix-up; changed contracts unverified, no implementation or new PASS claim
- **Date:** 2026-09-07 (initial build: 2026-08-08)
 
**Integration change notice — IR-15/23/26/27.** The current owner contracts supersede the
initial-build consumption assumptions below: Phase 5 owns fixed policy and physical binding,
Phase 6 consumes its resolver, Phase 7 owns the ten-step synchronous transaction, and Phase 13
publishes effective parameters and deferred retirement. §§4/5/7/10/11 adopt those contracts and
Phase 11's OQ-22 measurement handoff. D-P14-19…22 record the changes. Current owner verification
and optional-extension gates remain open; historical review verdicts do not certify these bytes.
No test, runtime experiment, formatter or validation command was run for this documentation fix-up.

The **initial build was against v3**, not a §G0.4 re-pointing: Phase 14 had never been built, so
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

### 0.1 Initial-build inputs actually read (historical record)

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

### 0.2 Initial-build additional reads (historical record)

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

### 0.3 Initial-build departures and gate evidence (historical record)

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
   2026-08-08 14:04). It was untracked when first observed and was **committed during this session**
   (`9ff94a5`), and `docs/MOVES.md` records a Phase 13 v3 adoption (`docs/MOVES.md:82`, `:89`,
   `:91`). **`docs/phase13/reviews/` contains only `.gitkeep` — zero review rounds.** The file is
   therefore the freshly landed product of a concurrent Wave-5 build session
   (`docs/design/v3/DESIGN.md:647`) and is **unverified per §G1.3**, not a valid dependency input.
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

### 0.4 Current integration inputs and gates

This fix-up reads `docs/PHASE_INTEGRATION_REVIEW.md` IR-15/23/26/27; current
`docs/phase4/v1/PHASE_4_DOC.md` §5.1/§11.4; `docs/phase5/v1/PHASE_5_DOC.md`
§§2.4/4.12/5; `docs/phase6/v1/PHASE_6_DOC.md` §§4.9/5;
`docs/phase7/v1/PHASE_7_DOC.md` §§4.1/5.3;
`docs/phase13/v1/PHASE_13_DOC.md` §§4.3.5/4.5/4.7/5; and
`docs/phase11/v1/PHASE_11_DOC.md` §§4.11/5/10.1. Authority is
`docs/design/v3/DESIGN.md` Phase 11 evaluator requirement and Phase 14 scope/gates, under
`AGENTS.md`'s phase-owner rules. These are current-byte documentation contracts, not fresh verified
grants. §§0.1–0.3 and C-1 retain initial-build history only; their absent-interface and old PASS
premises are not current consumption authority. No restricted source, transcript or prior review
was opened for this fix-up. Each owner retains its own governing revision.

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
  shared-resolver sampler-integer uploads, cadences, smoothing math, and the `CenterDepthSource` seam. Phase 6
  hands this phase *"the optional PBO/fence replacement for synchronous center-depth readback. This
  document deliberately leaves that ledger item live"* (`docs/phase6/v1/PHASE_6_DOC.md:285`–`:286`).
- **Owned by Phase 7:** frame orchestration, the current ten-step reload/pack-switch transaction,
  every Mixin hook and every `DebugService` call site A5 requests. Phase 7's timing aggregates are
  internal, not a public elapsed-per-pass API; Phase 14 accepts JFR attribution (§5.4). Resize notices
  are Phase 5's contract. **Phase 14 adds no Mixin and no vanilla hook** (D-P14-18).
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
- **Owned by Phase 11:** expression language/diagnostics/effects and the private evaluator SPI.
  Phase 14 accepts its measurement handoff and conditional backend experiment (§5.8/L-11),
  without requiring compiled evaluation or taking ownership of expression semantics.
- **Owned by G8/S2:** compute, SSBOs, image load-store, and indirect dispatch. PD §15's evidence that
  all four run pack-exercised on the 1.12.2 compat context
  (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:741`–`:744`) is carried in §7.5's ledger as
  **feasibility evidence for G8/S2 only** — it is not a Phase 14 work item.

### 1.3 Hard boundary: this phase owns no contract-visible component

Every row A1–A7 is required to be *"a strict behavioral no-op from the pack's perspective"*
(`docs/design/v3/DESIGN.md:2573`–`:2574`), which is §G4.2's rule stated as an architecture
requirement (`:552`–`:558`). Three consequences bind the whole document:

1. **No pack-observable semantics are authored here.** Filter and wrap values are Phase 5's and
   Phase 13's; unit names/numbers/shapes are Phase 5's; `centerDepthSmooth`'s value semantics are Phase 6's; the
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

:mod     com.schmaloogium.mod.glue.gl              (A1–A5 GL mechanisms)
           Lwjgl3GLDevice        (Phase 1's) — A1 and A2 live inside it
             SamplerCache          TextureHandle -> interned sampler; per-unit bind cache
             DsaStrategy           CORE_45 / ARB / BIND_TO_EDIT object-creation strategy
           KhrDebugBackend       Phase 1's DebugService, implemented; balance-safe by construction
           CenterDepthReadback   Phase 6's CenterDepthSource, implemented over a PBO+fence ring
           GlWorkerContext       the single shared GLFW compat context (A4, spike-gated)
           CompileExecutor       proposed Inline | Worker seam; requires Phase 4/7 adoption

:conformance                                       (Phase 2's; Phase 14 adds runs, not machinery)
           the A3 imperceptibility comparison, the A6 allocation profile, the A7 audit
```

Nothing in this phase enters `mod.mixin`. A1–A5's new engine values use the requested `engine.gl`
placement and carry no GL type. The L-11 conditional expression candidate, if evidence triggers
it, belongs inside Phase 11's existing implementation-private SPI under that owner's semantics;
no placement grant or compiled backend is inferred merely from this measurement responsibility.

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
| An async-compile verb | **Not added** to `engine.gl`. `CompileExecutor` is a proposed policy seam, not an existing Phase 4 API; R-P14→P4-1 and R-P14→P7-1 must land before any caller migration. Current `ProgramRegistryCompiler.compile(RegistryBuildRequest)` remains synchronous. |
| KHR_debug verbs | **Already present.** Phase 1 ships `DebugService` at v0.1 precisely so call sites can label immediately (`docs/phase1/v14/PHASE_1_DOC.md:3060`–`:3065`, `:1652`); Phase 14 supplies the implementation. |
| The pure value types in §2.1 | **Added.** Immutable records/enums with no GL type. Requires the package-placement grant requested in §5.3 (R-P14→P1-2). |

### 2.4 Where each row attaches to the existing pipeline

```text
init (Phase 1 bring-up stage 2)
  └─ GlModernizationPlan.derive(profile, policy)        ── A1 A2 A3 A4 A5 tier selection
     ├─ DsaStrategy installed in Lwjgl3GLDevice          ── A2
     ├─ SamplerCache installed in Lwjgl3GLDevice         ── A1
     ├─ KhrDebugBackend installed as DebugService        ── A5
     ├─ CenterDepthReadback installed as CenterDepthSource (P7 transaction step 2) ── A3
     └─ current synchronous P4 compiler; proposed CompileExecutor only after grants ── A4

pack switch / reload  (Phase 7 §§4.1/5.3, current ten-step transaction)
  └─ step 3 P4 compile + P5 plan/create                              ── A1 A2; A4 gated
     steps 5–7 accept registry, adopt actual generation, accept estate
     step 8 P13 synchronous texture build/identity checks/registration ── A1 A2; upload A4 gated

frame  (Phase 7 FrameHookSink)
  open  → Phase 6 beginFrame → CenterDepthSource.readCenter              ── A3
  enter/exit scopes → DebugService.pushGroup/popGroup                    ── A5 (needs R-P14→P7-2)
  pass  → P5 preflights and physically binds before returning Bound      ── A1 (sampler bound too)
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
| **Fixed texture-unit map** — packs rely on these numbers, units 0–15 by stage | A1's per-unit sampler bind cache is indexed by the **fixed** App B.3 unit, never allocated; §4.1.4. Phase 5 is sole policy author and physical binder; Phase 6 consumes its resolver for integer uploads | `[V:doc]` `docs/research/v1/RESEARCH.md:628` *"Fixed texture-unit map — **Keep** — Contract-visible (App B.3)"*; current `docs/phase5/v1/PHASE_5_DOC.md` §§4.12.1/5.1 and `docs/phase6/v1/PHASE_6_DOC.md` §§4.9/5.2; **D-P14-3**, **D-P14-19** |
| `depthtex1` is unit 11 | same; A1 introduces no unit-assignment logic at all | `[V:doc]` App B.3; current Phase 5 §§4.12.1/5.1 policy, consumed unchanged by Phase 6 |
| **Ping-pong buffer + flip semantics, buffer clear rules** — packs depend on exact flip behavior | A1 and A2 change texture *parameterization* and *object creation*; neither reads or writes flip state, side selection, or clear policy, all of which stay in `engine.buffers` | `[V:doc]` `docs/research/v1/RESEARCH.md:627`; Phase 5 ownership at `docs/phase5/v1/PHASE_5_DOC.md:343`–`:345`; **D-P14-1** |
| **Filter and wrap state of every colortex** — `CLAMP_TO_EDGE` S/T; NEAREST for integer formats, LINEAR otherwise | A1 derives `SamplerKey` **from Phase 5's `TextureParameters` value**, never independently; the equivalence test in §8.1 asserts the bound sampler's state equals the texture's configured state for every texture in the estate | `[V:doc]` Phase 5's policy at `docs/phase5/v1/PHASE_5_DOC.md:1042`–`:1045`; **D-P14-1** |
| **Shadow filter, mipmap, and hardware-PCF compare mode** | `SamplerKey` carries `compareMode`; derived from Phase 5's `ShadowTextureResource(hardwareFiltering, mipmap, nearest)` (`docs/phase5/v1/PHASE_5_DOC.md:534`) | `[V:doc]` `docs/research/v1/RESEARCH.md:523`–`:525` via `docs/phase5/v1/PHASE_5_DOC.md:868`; §4.1.3 |
| **`centerDepthSmooth` — declared-trigger readback, App D `float`, tick-domain smoothing** | A3 replaces only *how the depth pixel arrives*. The EMA, the half-life, the tick domain, the declaration trigger, and the uploaded value's type all stay in Phase 6. The added latency is **contracted**, not hidden (§4.3.6, R-P14→P6-1) | `[V:observed]` `docs/research/v1/RESEARCH.md:642`, `:773`; Phase 6's design at `docs/phase6/v1/PHASE_6_DOC.md:928`–`:934`; **D-P14-8** |
| **Frame-begin sampling completes before any buffer resize or clear** | A3 runs *inside* `CenterDepthSource.readCenter`, which Phase 6 calls at step 6 of `beginFrame` (`docs/phase6/v1/PHASE_6_DOC.md:864`–`:867`); A3 issues no resize and no clear and adds no frame moment | governing REV1 constraint at `docs/design/v3/DESIGN.md:526`–`:528`; exported ordering contract at `docs/phase6/v1/PHASE_6_DOC.md:1385` |
| **Everything refreshes on program switch; matrices always upload** | A1–A7 touch no uniform upload and no barrier participant. A5's debug groups are gated `isActive()` and issue no GL when inactive | `[V:observed]` `docs/research/v1/RESEARCH.md:1379`–`:1380` via `docs/phase6/v1/PHASE_6_DOC.md:448`–`:449`; **D-P14-15** excludes contract cadence from optimization |
| **Stage semantics, program set, backup chains** | A4 moves `glCompileShader` to another thread of the same share group. It does not choose, order, name, resolve, or fall back between programs — all Phase 4's | `[V:doc]` `docs/research/v1/RESEARCH.md:626`; **D-P14-11** keeps link, uniform location and the `Program.use()` barrier on the render thread |
| **`_n`/`_s` companion atlases; missing sprites → `0xFF7F7FFF` / zero-specular** | A4's async upload changes *when bytes reach the driver*, never the byte values or the defaults, which are Phase 13's | `[V:doc]` `docs/research/v1/RESEARCH.md:638`; spec at `docs/design/v3/DESIGN.md:2451`–`:2454`; **spec-derived — R-P14→P13-1** |
| **Custom-texture `.mcmeta` blur/clamp and owner-approved filter/wrap semantics** | A1 consumes Phase 13 `TextureParameterSpec(MinFilter,MagFilter,WrapMode)` and its parameter fingerprint through §5.5's exact conversion/lifetime rule. The separate upstream property-suffix authority conflict is not silently resolved here; sampler 0 preserves owner-applied texture state while any full conversion is unavailable | `docs/phase13/v1/PHASE_13_DOC.md` §§4.3.5/5.1/5.5; **D-P14-19**, R-P14→P1-3 |
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
| **GL 4.5 `glBindSamplers` batching** | **Proposed `MULTI_BIND` mechanism**, disabled pending R-P14→P5-2/P1 owner-controlled preflight/batch boundary; no parallel Phase 7 binder. Fixed dense units suit batching only after complete sampler-state conversion; **D-P14-2/19** | Historical `[V:observed — Pintonium .../IrisRenderSystem.java:377`–`:389]`: `hasMultibind` uses OpenGL45 or GL_ARB_multi_bind and `GL45C.glBindSamplers(0, emptyArray)` clears the range; mechanism evidence is not an owner-interface grant |
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

**Consequence for this document.** The initial build proceeded on explicit maintainer instruction
(`docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md:57`–`:68`); §0.3 preserves that authorized departure.
This integration fix-up now consumes the actual P5/P6/P7/P13 owner contracts in §5 under the current
documentation-remediation assignment, with no new verification claim. **Requested upstream (§11.4):**
either add an explicit soft-dependency clause to
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

`SamplerKey` is a proposed immutable derived value in the requested `engine.gl` placement,
with a proposed `SamplerKey.of(TextureParameters)` conversion. Its exact complete fields are
gated on R-P14→P1-3, not defined by a reduced example that omits LOD or border state.
The conversion must retain every supported minification mode (including all six P13 modes),
magnification, applicable-axis wrapping, compare state and all remaining sampler fields supplied
by the owner. P14 introduces no lossy four-filter vocabulary.

GL divides a texture's parameter set in two, and the split is the design's central correctness
concern (§8.1's `SamplerStateSplitTest` pins it):

- **Sampler state** — min/mag filter, wrap S/T/R, min/max LOD, LOD bias, border colour, texture
  compare mode and func. When a sampler object is bound to a unit, these **override** the texture
  object's values for sampling through that unit. R-P14→P1-3 must publish the exact complete
  source/derived field set before A1 is enabled. No omitted field gets an invented default.
- **Texture state** — base level, max level, swizzle, immutability. These are *not* sampler state and
  must continue to be set on the texture object by `TextureService.setParameters`. Phase 5 explicitly
  has one: the sfb *"old-pack swizzle"* (`docs/phase5/v1/PHASE_5_DOC.md:350`).

So `TextureService.setParameters(t, p)` under `SamplerTier != NONE` becomes: apply the
texture-state half with `glTexParameter*` as today, and route the sampler-state half to
`SamplerCache.intern(SamplerKey.of(p))`, recording the result against the texture handle. No caller
changes. **`TextureParameters`' exact field set is not published by Phase 1** — it appears once, in
the `setParameters` signature at `docs/phase1/v14/PHASE_1_DOC.md:3016`, and nowhere else in that
document — so §5.6 request R-P14→P1-3 asks for it. The split is a correctness obligation,
not a granted conversion or a substitute for sampler 0 while fields remain unspecified.

#### 4.1.3 `SamplerCache` — interning, in `mod.glue`

```text
SamplerCache
  Map<SamplerKey, SamplerHandle>  interned      — reference-accounted through live/retiring owners
  perTexture                                 — actual handle/source/parameter/publication identity
  SamplerHandle[16]               boundPerUnit  — the fixed App B.3 units, index == unit number
```

- **Cardinality is small and closed.** The distinct sampler states an estate can want are: NEAREST or
  LINEAR × mipmapped or not, all `CLAMP_TO_EDGE` for the colour estate
  (`docs/phase5/v1/PHASE_5_DOC.md:1042`–`:1045`); plus the shadow set, which adds the hardware-PCF
  compare mode and per-texture nearest/mipmap flags from Phase 5's
  `ShadowTextureResource(hardwareFiltering, mipmap, nearest)` (`docs/phase5/v1/PHASE_5_DOC.md:534`);
  plus Phase 13's exact `TextureParameterSpec` (§5.5). The distinct count depends on validated
  effective owner parameters; there is no assumed dozen-state cap.
- **Lifecycle follows actual ownership, not a bare generation number.** Estate metadata uses
  Phase 5's accepted generation. Phase 13 source/configuration/parameter/publication/resource and
  foreign-object identities also participate. An equal handle number, digest or generation alone
  cannot reuse a stale entry. On all eight resize reasons (§5.2), invalidate bound-unit knowledge
  and retire affected entries; obtain replacement parameters only from the transaction's accepted
  pairing. Do not infer a new registry from a notice or construct a competing publication.
  Retired publications cannot bind; outstanding leases merely defer deletion. Shared sampler
  allocations stay accounted for across every live/retiring owner reference and are deleted only
  after no reference can need them, with units cleared first. Never delete a foreign texture.
- **Interning is deterministic.** `SamplerKey` is a record, so equality is structural; two textures
  with identical parameters share one sampler object. That is also what makes the `MULTI_BIND` tier
  cheap: a per-pass sampler array is 16 ints assembled from `boundPerUnit`.

#### 4.1.4 Binding, and the rejection of dynamic unit allocation

Binding hangs off Phase 5's existing physical operation, not a Phase 7/14 replay of a snapshot.
`textureBindings(passSnapshot,overlayLease,expectedOverlay)` (and the corresponding five-argument
`shadowBindings`) authenticates the same retained Phase 4 selection/context, preflights all sixteen
rows and then performs ascending object binds. Only `Bound(TextureBindingSnapshot)` transfers the
overlay lease into its closeable snapshot. `Degraded`/`Rejected` bind nothing; `BackendFailed` may
follow partial GL but transfers nothing. Bound's owner closes the binding in `finally`; on every
other exit the acquiring caller closes its lease. Activation and Phase 6 uploads follow Bound.

A1 may derive/cache a sampler only inside this owner-controlled binding path, before Bound and
immediate activation, never in an independent frame binder. Unknown/unconverted and Unused units
use sampler 0, never a stale previous sampler. P5's no-bind preflight and failure containment remain.
`MULTI_BIND` may batch only inside that same operation after full preflight, with complete
sampler-state knowledge and completion before Bound. There is no current facade batch boundary;
until P5/P1 explicitly grant one, use the existing per-unit path or `NONE`, not a new P7 binder.

**`D-P14-3`: dynamic per-program unit allocation is rejected, pre-decided.** §G11.4 lists it among
the standing pre-decided rejections — *"dynamic per-program texture-unit allocation (ours: fixed App
B.3 map incl. depthtex1 at unit 11)"* (`docs/design/v3/DESIGN.md:954`–`:955`) — and Phase 6 already
rejected it for the sampler-uniform side (`docs/phase6/v1/PHASE_6_DOC.md:473`). This design contains
no allocator: `boundPerUnit` is a fixed array whose index *is* the App B.3 unit number, and the only
way an entry changes is Phase 5's authenticated physical bind (or required clear), never snapshot replay.
There is no per-program sampler policy in Phase 14.

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
net for any missing or incomplete parameter conversion, including R-P14→P1-3.

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
destroyed FBO. All eight reasons in §5.2 invalidate the ring, not just depth/display changes.
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
build: current ten-step transaction step 3 calls Phase 4 `ProgramRegistryCompiler.compile` and
Phase 5 plan/create; step 8 builds Phase 13 textures after accepted registry/estate generations.
All currently run synchronously at a no-draw, no-open-frame safe point (Phase 7 §§4.1/5.3).
For companion atlases, two additional full atlases with matching mip chains is the accepted memory cost
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

**Proposed interface, not a current owner grant.** R-P14→P4-1 (§5.9) and R-P14→P7-1
must land before either implementation replaces the existing synchronous compiler.

```java
/** :engine policy interface. Both implementations satisfy it identically from the caller's view. */
public interface CompileExecutor {
    /** Worker never waits; Inline compiles synchronously. Batch is opaque and P4-issued. */
    CompileBatchToken submit(PreparedCompileBatch batch);
    /** Never blocks. PENDING | READY(results) | FAILED(diagnosticId). */
    CompileBatchStatus poll(CompileBatchToken token);
}
```

- **`InlineCompileExecutor`** — `submit` compiles every unit on the calling (render) thread and
  returns a token that `poll` immediately reports `READY`. The pipeline build's control flow is
  therefore *identical* in both tiers: submit, poll, proceed when ready. Under `Inline` the first
  poll always succeeds, so the build completes in one call exactly as Phase 7 designs it today.
- **`WorkerCompileExecutor`** — the §4.4.2 model.

After owner adoption, both tiers share this sequence. Before adoption, the fallback is the current
Phase 4 synchronous `compile(RegistryBuildRequest)`, not an invented submit/poll adapter.
A declined split leaves core control flow unchanged and does not block the milestone.

#### 4.4.5 Required compiler, transaction and texture-owner grants

- **Phase 4 (R-P14→P4-1).** §5.9 requests the exact prepare/worker/link ownership split. P4
  freezes validated materialization/policy inputs, owns private shader results, produces the candidate
  only after render-thread link/layout work, and preserves existing failure/fallback semantics.
- **Phase 7 (R-P14→P7-1).** Its current ten-step transaction is synchronous. The proposal suspends
  step 3 after preparation and before a publishable candidate, then resumes remaining step 3 and
  steps 4–9 only after readiness and identity revalidation. No shader draw while pending; invalidation
  cancels and drains late results under compiler ownership before the existing off compensation.
- **Phase 13 (R-P14→P13-1).** Its synchronous build and exact prepared payload/catalog contract
  exist. What is absent is submit/poll staging at step 8, with allocation on render thread, no
  bindable partial upload and no animation before readiness. Until owner adoption, atlas uploads
  remain synchronous, independently of whether the compiler proposal is adopted.
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

## 5. Cross-phase interfaces

### 5.1 What Phase 14 exposes

Phase 14 is a leaf: no phase in the §G5.1 graph depends on it (`docs/design/v3/DESIGN.md:611`–`:626`).
Its exposed surface is therefore small, and is aimed at the implementation effort and at G8, not at
another phase's design.

| Exposed contract | Exact content | Consumer(s) |
|---|---|---|
| `GlModernizationPlan` / `SamplerTier` / `DsaTier` / `DebugTier` / `AsyncCompileTier` / `AsyncReadbackTier` | immutable `:engine` record of the six tiers actually selected plus an immutable ordered list of rationale strings; derived by a **pure** static function of `GLCapabilityProfile` + `GlModernizationPolicy`, once, at bring-up, and never mutated for the life of the GL context | diagnostics; Phase 2's run manifest (as an environment fact, not a result); Phase 12 for display |
| `GlModernizationPolicy` | per-row `AUTO \| FORCE_ON \| FORCE_OFF`, five rows (sampler, DSA, debug, compile, readback); sourced from the mod config owned by `mod.core`; every row defaults to `AUTO`, and `AUTO` resolves to the reference-faithful path until that row's ledger entry closes (`D-P14-17`) | `mod.core` config; Phase 12 GUI if it chooses to surface them |
| `SamplerKey` and `SamplerKey.of(TextureParameters)` | the pure derivation function; the whole of A1's behavioral-equivalence obligation is in this one function, which is why it is exposed rather than hidden — it is the thing §8.1's equivalence test asserts over | `:engine` tests; Phase 5 and Phase 13 as the authors of the inputs |
| `CompileExecutor` / `CompileBatchToken` / `CompileBatchStatus` | **Proposal only:** submit/poll after explicit P4 prepare/worker/link and P7 resumability adoption (§5.9/§5.4); no current caller may infer an async compiler grant | Phase 4 owns compiler products; Phase 7 owns the transaction; P14 owns optional executor mechanism |
| **The `CenterDepthSource` implementation** | Phase 6's existing SPI, synchronous until R-P14→P6-1 grants sample age and the experiment passes | Phase 6 through Phase 7 transaction step 2 |
| **The `DebugService` implementation** | not a new type: `mod.glue`'s implementation of **Phase 1's** existing interface, balance-safe per `D-P14-13` | every phase that labels an object; Phase 7's group call sites |
| **Measurement procedures** — §4.3.6 imperceptibility comparison, §4.6.2 allocation profile, §4.7.2 redundant-state audit, §7.5 OQ-22 ledger | procedures and criteria, executed as Phase 2 harness runs; outputs are counts manifests in the local/CI cache, never repository artifacts | the implementation effort; G8/S2 for the feasibility rows |

No exposed contract carries a GL name, an LWJGL type, a Minecraft type, a `ProgramHandle`, or a
mutable value. Phase 14 exposes **no new GL entry point on `engine.gl`** (§2.3).

### 5.2 Consumed from Phase 5 — current owner contract, unverified changes

Phase 5 §5 incorporates §§2.4/4.12. Its old PASS is historical and does not certify the current seam.

| Contract | Phase 14 consumption |
|---|---|
| `FixedSamplerPolicies.appB3()` / `.resolver()` | Sole fixed spelling/unit/shape policy and fingerprint, pure before registry/runtime/estate creation; no second map or allocator |
| `textureBindings` / `shadowBindings` / `TextureBindingResult` | P5 alone performs physical binds after complete preflight; exact Bound-only lease transfer and all four outcomes are consumed as §4.1.4 states |
| `TextureBindingSnapshot` | Closeable sixteen-row evidence with `BoundObject`/`Unused`; usable only while pass/frame/estate/depth/registry/selection/overlay identities and owner currentness remain valid. Never replay expired rows or retain handles as authority |
| `BufferEstateView`, sizing/inventory/formats, shadow resource parameters | Non-owning accepted generation metadata and owner-authored parameter inputs; not texture-ownership transfer |
| Resize publication/registration/notice/results | All eight reasons in declaration/priority order: `DISPLAY_EXTENT`, `RENDER_QUALITY`, `MAIN_DEPTH_EXTENT`, `SHADOW_RESOLUTION`, `SHADOW_QUALITY`, `PACK_CONFIGURATION`, `REGISTRY_PLAN`, `COLOR_INVENTORY_OR_FORMAT`. Notice is old/new sizing, new generation and one prioritized reason, not registry identity |

Register through the existing closed `Registered`/rejected protocol at the accepted pairing.
Each callback is render-thread synchronous and returns `SUCCESS` or `FAILED`; preserve the owner's
full delivery result, including `ConsumerFailed`'s installed-off ownership and `deliveredCount`.
An optional cache failure should demote/retire locally when safe, never claim SUCCESS after
incomplete safety cleanup or hide a required failure from P7. §4.1.3 defines invalidation and
lease-safe retirement. P7 owns compensation; no old publication is revived.

H-P14→P5-1 remains an optional mipmap simplification, not permission to change P5's baseline.
**R-P14→P5-2 (ungranted):** a full-preflight internal sampler batch boundary, preserving zero-bind
rejection, Bound-only transfer and backend-failure containment. `MULTI_BIND` remains disabled
without that boundary; per-unit/`NONE` needs no parallel binder.

### 5.3 Consumed from Phase 6 — current owner contract and sample-age request

Current §5 adopts Phase 5's resolver; old PASS evidence does not certify its changed interfaces.

| Phase 6 §5 contract | Use here |
|---|---|
| `CenterDepthSource`, `CenterDepthRequest`, `CenterDepthResult{Sample, Unavailable}` with their exact validation rules (`docs/phase6/v1/PHASE_6_DOC.md:1392`, `:547`–`:588`) | A3 implements this SPI in `mod.glue` in both tiers; the async form consumes it **as it exists** — no type change is required for the mechanism (`D-P14-8`) |
| `D-P6-1` and the not-obviated statement (`docs/phase6/v1/PHASE_6_DOC.md:1678`, `:965`–`:968`) | resolves A3's conditional status (§3.4) |
| The frame-begin ordering contract — center-depth read completes before any Phase 5 resize or clear (`docs/phase6/v1/PHASE_6_DOC.md:1385`, `:864`–`:876`) | A3 adds no frame moment and preserves the ordering; it is the reason no in-flight transfer can outlive its framebuffer |
| The §6 degradation row *"center-depth dimensions/FBO unavailable | 2a | retain previous smoothed depth"* (`docs/phase6/v1/PHASE_6_DOC.md:1500`) | the designed meaning of A3's warm-up and invalidation `Unavailable` returns |
| `FixedSamplerResolver` injected immediately after `UniformConfiguration`; `Ready(List<ResolvedSamplerBinding>,policy)` / `Invalid(SamplerLayoutValidation)` | P6 consumes P5's sole resolver, not an independent map. Ready rows are exact name/full shape/unit; P6 only locates/uploads integers using the same effective layout/stage/band/policy as preceding P5 binds, preserving its cache and activity-token rules |
| `UniformConfiguration`'s exact half-lives in ticks, incl. center depth (`docs/phase6/v1/PHASE_6_DOC.md:487`) | the `h` in §4.3.6's criterion C2, so the tolerance is derived from the pack rather than invented |
| `centerDepthMacroContributor()` always `MacroContribution.Empty` (`docs/phase6/v1/PHASE_6_DOC.md:1393`) | confirms no macro-level redirect exists; A3 is the only async path |

**R-P14→P6-1 — contract the sample's age.** *Requested, not assumed.* Phase 6 states *"Phase 6 …
does not hide the cost with an uncontracted one-frame queue"* (`docs/phase6/v1/PHASE_6_DOC.md:1558`).
`CenterDepthResult.Sample(float depth)` carries no frame identity and Phase 6 imposes no echo
requirement on it (`:587`–`:588`), so a `mod.glue` implementation *could* silently return a stale
value — which is exactly what Phase 6 has ruled out. Requested, in preference order:

1. **Preferred:** widen the record to `Sample(float depth, long sampledFrameId, long sampledWorldEpoch)`,
   and state in §5 that an implementation may return a sample from an earlier accepted frame provided
   the identities are truthful. This is the honest form and additionally lets Phase 6's
   time-corrected EMA account for the true sample age if it ever wants to.
2. **Minimum acceptable:** leave the type alone and add one sentence to Phase 6's §5
   `CenterDepthSource` row stating that a `Sample` may originate from an earlier accepted frame of
   the same `registryGeneration` and `worldEpoch`, so an implementation with bounded latency is
   contract-conforming.

**Consequence if declined or not yet landed:** `AsyncReadbackTier.PBO_FENCE` stays `FORCE_OFF` and
the synchronous path ships (§4.3.7). A3's mechanism is unaffected; only its permission to be enabled
is. This request is the *sole* blocker on A3.

### 5.4 Consumed from Phase 7 — ten-step baseline and optional requests

Current §§4.1/5.3 are the owner contract. Coordinated changed bytes remain unverified; initial-build
round-32/33 predictions in §0.3 are not current authority.

| Phase 7 contract | Use here |
|---|---|
| `FrameDriver` / `FrameHookSink` and the `enter`/`exit` scope pair (`docs/phase7/v1/PHASE_7_DOC.md:1363`–`:1372`) | the boundaries A5's debug groups bracket, and A7's segmentation keys |
| `FrameExitKind { NORMAL, EARLY_RETURN, THROWN }` and `FrameAbortReason`'s six values (`docs/phase7/v1/PHASE_7_DOC.md:1383`–`:1386`) | the exhaustive exit set A1's sampler clear (`D-P14-4`) and A5's group drain must cover |
| The `finally`-based finalization guarantee (`docs/phase7/v1/PHASE_7_DOC.md:2166`–`:2168`) | what A1's sampler clear rides so it survives a throw |
| `ShaderReloadController` / `ReloadRequest` / `ReloadToken` / `ReloadStatus{Queued, Building, Active, Off, Failed, Unknown}` (`docs/phase7/v1/PHASE_7_DOC.md:1671`–`:1693`) | the pack-switch entry point A4 accelerates; `Building` is the state an async build would occupy |
| Current ten-step §§4.1/5.3 transaction | Step 1 quiesce/drain/freeze and preliminary preferences before load; 2 inactive texture owner/adapters then runtime/provider with P5 resolver; 3 frozen ID inputs, P4 compile and P5 plan/create; 4 compose/revalidate; 5 retire old owners/registrations and publish P4 Ready; 6 adopt actual accepted registry generation; 7 publish P5 with full synchronous resize result; 8 build/validate/register P13 against actual accepted identities; 9 publish ID/invalidate geometry then atomically Active/admit; 10 failure compensation through owner retirement and P5/P4 off |
| Exact ownership and generation rules | Only caller-owned candidates are caller-closed; accepted resources retire through publishers. Old barriers invalidate before P6 retirement and borrowed services outlive retirement. PipelineVersion increments once per final outcome; P4 generation changes independently, so Ready then compensating Off may increment it twice |
| Construction and resize placement | CenterDepthSource installs in step 2; P5 allocation in step 3; P13 texture allocation/upload only in step 8 after accepted generations, never a guessed generation; resize publication/delivery is step 7, new P13 registration step 8 |
| Phase 7's own posture: *"Phase 7 adds no readback"* (`docs/phase7/v1/PHASE_7_DOC.md:2194`–`:2195`) | confirms A3 is the only readback path in the frame |
| OQ-3's default plan: *"Make **no** context-flag change"* (`docs/phase7/v1/PHASE_7_DOC.md:2320`–`:2323`) | the constraint that produces finding C-3 and shapes A5's gate (`D-P14-14`) |

**R-P14→P7-1 — resumability, still ungranted.** After R-P14→P4-1 is explicitly adopted,
permit suspension at step 3 between compiler preparation and a publishable candidate. P7 retains
the frozen configuration/schema/world/resource/hook/policy identities and caller-owned candidates;
P4 retains opaque worker products and their cleanup. `PENDING` permits vanilla only. At `READY`,
revalidate every identity before finishing step 3 and steps 4–9. Failure/superseding intent/world or
resource/resize invalidation cancels the pending build, rejects late results and follows step 10;
no retired atlas or prior registry is restored. Step 8 async upload requires separate P13 staging
adoption. Without either owner grant, run its current synchronous operation. No implicit resumability.

**R-P14→P7-2 — debug-group call sites, and a frame-phase timing seam.** Two parts:

1. **Groups.** That Phase 7 call `debug().pushGroup(label)` / `debug().popGroup()` at boundaries it
   already owns — the frame, each `RenderSection` scope, the shadow invocation, and each
   deferred/composite/final pass. Phase 14 adds no hook (`D-P14-18`), and the cost when inactive is
   nil (§4.5.4).
2. **Timing — adopted fallback, not an exposed timer.** Phase 7 narrows its §1.2 promise to
   internal off-by-default debug aggregates and records this same disposition in §§5.5/11.
   No public elapsed-per-RenderSection/pass record exists; frame counts are not elapsed timing.
   Phase 14 accepts JFR stack attribution for A6/A7 ranking; debug groups may segment a separate
   counts/capture run only if their optional call sites are adopted. Timing runs never enable
   label/recordGL flags. A read-only frame-id/per-pass-nanos projection remains an optional
   unadopted future request; missing instrumentation does not block synchronous rendering.

### 5.5 Consumed from Phase 13 — parameters/lifetime and optional upload staging

Phase 13 §§2.3/4.3.5/4.5/4.7/5 publish real current contracts. The initial-build claim that no
interface exists is historical, not an active premise. Changed owner contracts remain unverified.

**R-P14→P13-2 — value handoff adopted, complete sampler conversion still gated.**
Consume exactly `TextureParameterSpec(MinFilter minFilter,MagFilter magFilter,WrapMode wrap)`
and `TextureParameterFingerprint(String value)` from the validated candidate/source metadata.
The owner-published closed domains are:
`MinFilter { NEAREST, LINEAR, NEAREST_MIPMAP_NEAREST, LINEAR_MIPMAP_NEAREST,
NEAREST_MIPMAP_LINEAR, LINEAR_MIPMAP_LINEAR }`,
`MagFilter { NEAREST, LINEAR }`, and `WrapMode { REPEAT, CLAMP_TO_EDGE }`.
Map min/mag one-to-one with no mip-mode reduction. Wrap applies S for 1D, S/T for 2D and
RECTANGLE, S/T/R for 3D. RECTANGLE requires CLAMP_TO_EDGE and non-mipmap minification;
an incompatible spec is rejected by owner target compatibility, never silently normalized.
`.mcmeta` blur false/absent gives NEAREST, true LINEAR; clamp false/absent REPEAT, true
CLAMP_TO_EDGE. Consume the effective owner result, including sidecar/default outcome;
never reparse properties or invent stripped suffix semantics. Unknown borrowed modes remain
outside interning and use sampler 0, not a fabricated representable spec.
The P13 value is **not** automatically P1's `TextureParameters`: missing compare/LOD/border
state and object-only base/max-level/swizzle/immutability must come from the exact granted P1
conversion/owner policy. Do not infer those fields from three values. Until a lossless complete
conversion exists, that texture's unit binds sampler 0 and retains owner-applied texture parameters.
This also covers borrowed objects: actual authenticated foreign parameters only, no mutation to
make requests compatible, no invented compare/default state. P5 logical-buffer mipmaps cannot
overwrite a custom/foreign replacement's parameters.

**Publication/lifetime consumed exactly.** `TextureSystemFactory.create` returns inactive
`Created(TextureSystem)|Failed(TextureFailure)`. `plan` is `Planned(TexturePlan)|Invalid`;
`build(TextureBuildRequest(planRequest,sources))` is synchronous `Ready(TexturePublication)|Failed`,
validating matching prepared payload/catalog source/content/target/parameter/epoch before GL.
P7 builds in step 8 with accepted registry and estate generations and registers against accepted
sizing. Immutable publication/candidate views are non-owning; P14 never acquires deletion authority.
`TextureLeaseSource.lease(expectedPublication,selection)` returns `Acquired(lease)|Rejected(reason)`;
the P5 Bound-only closure rule applies. Retire immediately prevents new leases and makes
`isCurrent()` false. Outstanding leases delay reverse-order owned deletion, not stale-use rejection.
Close never blocks the render thread; borrowed references are released, never deleted. Same-owner
reuse requires complete equal source/parameter identity and accounting across every live/retiring
publication; equal hashes never grant cross-owner reuse or revive an old publication.

**R-P14→P13-1 — stageable upload remains ungranted.** The missing extension is separation of
prepared bytes from bindable completion, not missing texture interfaces. Request render-thread
storage allocation, worker upload of immutable prepared payloads, opaque completion polling,
no binding/animation before ready, and owner-controlled cancel/late-completion cleanup under P7's
frozen transaction identities. Current synchronous build remains baseline. If this extension is
declined, an independently granted compile optimization may proceed but atlas upload stays synchronous.

**R-P14→P13-3 — inventory covered.** Noise, companion and all custom source forms use the same
parameter/source/lease discipline; no alternate interning route or separate texture owner is added.

### 5.6 Requests against Phase 1 (not a declared dependency; read narrowly per §0.2)

Phase 1's §5 already names **14** among the consumers of `GLCapabilityProfile`
(`docs/phase1/v14/PHASE_1_DOC.md:4222`) and of the GL-error surface (`:4220`), and its §1 scope-out
table already assigns this phase *"KHR_debug labels/groups, sampler objects, async compile, GC
posture"* (`:1490`). Four requests follow; none blocks a row outright.

| ID | Request | Basis | If declined |
|---|---|---|---|
| **R-P14→P1-1** | Redefine `DebugService.isActive()`. Its current comment — *"false unless a debug context and the dev flag are both on"* (`docs/phase1/v14/PHASE_1_DOC.md:3064`) — makes the whole service dead under Phase 7's OQ-3 default of *"Make **no** context-flag change"* (`docs/phase7/v1/PHASE_7_DOC.md:2320`). Requested: `(KHR_debug or GL 4.3) && -Dschmaloogium.debug.glLabels`, with a debug context an enhancement. Finding C-3 | KHR_debug requires no debug context; the reference gates on extension-or-version plus a flag (`[V:observed — Pintonium .../gl/debug/GLDebug.java:291]`) | A5 is unshippable as specified. This is the one request whose refusal would make a scope row undeliverable, so it is flagged as such rather than softened |
| **R-P14→P1-2** | A package-placement grant for Phase 14, in the shape Phase 8 already has (`docs/phase1/v14/PHASE_1_DOC.md` §5.1 *"Phase 8 package grant"*): permission to add the §2.1 pure value types to `com.schmaloogium.engine.gl`, and to place the backend in `com.schmaloogium.mod.glue.gl`. §2.1 assigns no package to Phase 14 today | Phase 1's rule is *"a phase's code goes in the package §2.1 assigns it"* | the values live in an existing `engine.gl` sub-namespace by Phase 1's direction; no design change, only placement |
| **R-P14→P1-3** | Publish `TextureParameters`' complete field set and exact P13 three-field conversion with remaining sampler-state and object-state policy; §4.1.2's illustrative subset is not a complete type | A1 must preserve every owner-authored parameter without guessed defaults | Keep sampler 0/per-texture parameters for every texture without a complete lossless conversion; no partial A1 enablement based only on the illustration |
| **R-P14→P1-4** | (a) Thread-safety, or a documented off-thread mode, for `RecordingGLDevice` — Phase 1 explicitly invites this: *"it is not made thread-safe to cover the Phase 14 exception. If off-thread uploads ever need recording, that is a Phase 14 request against this document"* (`docs/phase1/v14/PHASE_1_DOC.md:4322`). (b) One new flag in the §4.9.3 namespace: `schmaloogium.debug.glContext`, owner P14, milestone v0.5, gated on OQ-3. (c) Optional: an `AutoCloseable DebugService.group(String)` scoped form | (a) A4's worker issues GL through the facade; (b) §4.5.5; (c) §4.5.3 | (a) the worker bypasses the recorder and off-thread calls are simply not recorded — A4 loses recorded-log coverage of the worker, nothing else; (b) the debug-context tier is dropped and the always-available `GL_DEBUG_OUTPUT` tier stands; (c) `D-P14-13`'s backend construction already makes imbalance harmless |

### 5.7 Consumed from Phase 2 (not a declared dependency; procedures only)

A3's imperceptibility comparison, A6's profile and A7's audit are all Phase 2 harness runs. Phase 14
defines **what to measure and what constitutes pass**; Phase 2 owns the scenes, the tiers, the
diffing, the tolerances, the fixture acquisition and the run-manifest schema, and none of them is
redefined here. The one new scene requested is `S-CD-1` (§4.3.6 family F3) — a scripted near↔far
camera path — proposed to Phase 2 as a scene, not authored here. §G6's derived-artifact rules bind
every output: no pack source text, no rendered images in the repository
(`docs/design/v3/DESIGN.md:718`–`:725`).

### 5.8 Consumed from Phase 11 — OQ-22 measurement ownership

P14 accepts the §10.1 methodology and §5.1 `ExpressionMetricsSink.record(ExpressionMetrics)`
consumer handoff. The immutable record is `(String planFingerprint,long refreshCount,
long nodeEvaluations,long variableMemoHits,long variableMemoMisses,long uniformSuccesses,
long uniformErrors,long uniformSkips,long elapsedNanos,String profilerCorrelationId)`.
Delivery is synchronous render-thread, non-null, no-op by default; sink failure disables metrics
only. No node callbacks, pack text or mutable evaluator state crosses the boundary.
P11 retains language, effect/diagnostic ordering, backend semantic IDs and implementation-private
SPI. P14 owns measurement and the conditional candidate experiment, not a new expression language.
§7.5 L-11 and §10.2 specify the decision and source-free metrics; no compiled evaluator is required
without measured real-pack evidence.

### 5.9 R-P14→P4-1 — explicit compiler owner-change request (ungranted)

Current P4 §5.1 remains `compile(RegistryBuildRequest) -> Ready(caller-owned candidate) |
ShadersOff(failure)`, synchronous render-thread. Request P4-owned preparation that freezes the
same configuration/dimension/materialization/macro/policy/capability inputs and validates all
callback outputs before GL into an opaque compiler-issued immutable prepared batch. No worker
calls P3/P5/runtime callbacks or retains the mutable request/services.

Only shader-object create/source/compile/status/log/fence work may execute on the shared worker.
The prepared batch and completion tokens expose no shader/program handle or candidate credential.
P4 consumes completion on the render thread, establishes visibility, then owns program creation,
attach/link/validate, declaration/location queries, deterministic fallback/failure projection and
minting the existing caller-owned candidate. P7 remains sole publication caller/transaction orchestrator;
P14 supplies executor mechanics, not registry products or fallback-chain policy.

Proposed submit/poll outcomes are opaque token then `PENDING | READY(results) | FAILED(diagnosticId)`;
cancellation marks that token terminal/failed and rejects any late result. P4 owns exactly-once
shader/fence disposal after worker acknowledgement, including failures, canceled or stale builds;
never delete an object while the worker still uses it or let late completion publish. P7 freezes and
revalidates the same build identities at step 3 suspension/resumption (§5.4); no source text enters
diagnostics/manifests. Exact owner API declarations and safe worker-drain behavior must be published
and freshly verified by P4, then reciprocally adopted by P7, before migration. OQ-15 evidence is an
additional gate, not a substitute for either grant. Declined/unlanded: existing synchronous compile
remains; after adoption Inline remains the driver fallback. No core rebuild is required to decline.

**Recipient disposition:** P4 §5.7/§11.4 records R-P14→P4-1 as pending, not adopted; its exact
prepare/link/cancel/visibility callable schema must be an owner amendment, not inferred from
this executor sketch. P5 §5.5/§11.5 likewise records R-P14→P5-2 as ungranted.

---

## 6. Failure modes and degradation

§G2.4's ladder (`docs/design/v3/DESIGN.md:419`–`:449`), applied case by case. Note the shape of this
table: because Phase 14 owns no contract-visible component, **almost every row degrades to "run the
reference-faithful path"** rather than to a reduced-function state. That is the direct consequence of
the fallback requirement at `docs/design/v3/DESIGN.md:2574`–`:2575`, and it is the reason this phase
cannot cause a pack-visible failure that the pre-Phase-14 pipeline would not also cause.

| Failure | Detection | Required disposition | Rung |
|---|---|---|---:|
| GL < 3.3, or `ARB_sampler_objects` absent | `GlModernizationPlan.derive` at bring-up | `SamplerTier.NONE`; Phase 5's per-texture parameterization runs unchanged; info diagnostic | — (designed path) |
| Sampler object creation/parameterization fails | owner's build-window error drain | clear units and demote the affected estate to `NONE`; retire sampler entries with §4.1.3 reference accounting, delete only unused objects, preserve required P5 failure/compensation if safe cleanup fails | 2a |
| Complete P13-to-P1 sampler conversion unavailable | §5.5 lossless-conversion gate | that unit binds sampler 0 and retains owner parameters; already supported estate inputs may use samplers. This explicit exclusion is not a partially initialized sampler | 2a |
| A sampler is left bound when control returns to vanilla | the backend's own `finally` at frame exit; `SamplerLeakTest` in CI | cannot occur by construction (`D-P14-4`); if the clear itself errors, force `SamplerTier.NONE`, clear again, and warn — **a leaked sampler is a vanilla-corruption hazard, so it escalates rather than degrades locally** | 5 |
| GL 4.5 / `ARB_direct_state_access` absent | `derive` at bring-up | `DsaTier.BIND_TO_EDIT`; today's path | — (designed path) |
| A DSA entry point errors at first use | `drainErrors` in the estate-build window | demote the strategy to `BIND_TO_EDIT` for the session, re-run the failed operation, warn once naming vendor and renderer. Demotion is safe because the tiers are behaviorally identical (`D-P14-7`) | 2a |
| Binding neutrality violated by a backend bug | `BindingNeutralityTest` in CI; in the field, vanilla mis-rendering | a **CI-blocking** defect, not a runtime degradation: an incorrect binding cache is the §G4.6 hazard and there is no safe runtime response | 5 (prevented) |
| GL < 3.2 (no fence sync), or PBO creation fails | `derive`, or `drainErrors` at ring construction | `AsyncReadbackTier.SYNCHRONOUS`; Phase 6's `readDepthPixel` path | — (designed path) |
| A center-depth fence never signals | the slot stays `IN_FLIGHT` past `N` frames | discard the ring, return `Unavailable`, and after three consecutive occurrences demote to `SYNCHRONOUS` for the session. **The render thread never waits** (`D-P14-9`), so a stuck fence costs stale-then-sync, never a hang | 2a |
| Generation, world epoch, framebuffer extent or pixel changes | step 2 of §4.3.3 | discard the whole ring; return `Unavailable`; Phase 6 *"retain[s] previous smoothed depth"* per its own row (`docs/phase6/v1/PHASE_6_DOC.md:1500`) | 2a |
| Framebuffer dimensions ≤ 0 | step 1 of §4.3.3 | `Unavailable` with no GL call — Phase 6 requires exactly this (`docs/phase6/v1/PHASE_6_DOC.md:585`–`:587`) | normal |
| R-P14→P6-1 not landed | design-time | `PBO_FENCE` is not enabled; `SYNCHRONOUS` ships. Shipping a silently-stale value is refused, not risked | — (policy) |
| Shared context cannot be created, or the driver is not allowlisted | §4.4.3 stages 1–2 | `AsyncCompileTier.INLINE`; **no user-visible error** — this is a supported configuration | — (designed path) |
| Worker probe fails, a first real batch errors, or the watchdog fires | §4.4.3 stages 3–5 | tear down the worker, `INLINE` for the session, re-run that pipeline build synchronously, diagnose naming the driver. Worst case is one late pack switch | 2a |
| Worker thread throws an unexpected exception | the worker's own boundary | contain, tear down, `INLINE`, diagnose. **The exception never crosses to the render thread** and never reaches the client | 5 |
| GL context loss with a worker running | Phase 6's `GL_CONTEXT_LOSS` reset reason (`docs/phase6/v1/PHASE_6_DOC.md:1383`) | worker and its context are destroyed with everything else; the plan is re-derived at the next bring-up; no handle from the old context survives | 5 |
| KHR_debug absent, or `glLabels` unset | `derive` | `DebugTier.NONE`; a no-op `DebugService`. This is every shipping configuration | — (designed path) |
| A debug group push/pop is unbalanced by a caller | the backend's depth counter | no-op plus one rate-limited diagnostic; the frame boundary drains to zero (`D-P14-13`). **Cannot corrupt the driver's group stack** | normal |
| `glObjectLabel` errors (over-long or invalid name) | `drainErrors` under the per-call cadence the flag enables | clamp and retry once, then disable labelling for the session. Labels must never pollute Phase 1's error attribution, because a spurious error there disables a *uniform* (`docs/phase1/v14/PHASE_1_DOC.md:4220`) | 2a |
| An audit run's `GLCallLog` overflows its ring, or the scene is not frame-identical | §4.7.2 gates 1 and 3 | the audit **reports nothing** and is re-taken. No partial or estimated counts are ever produced | — (procedure) |
| A measurement shows a sibling phase allocating in the frame path | §4.6.2 attribution | a finding routed to that phase through §11.5. Phase 14 does not edit another phase's code or call sequence | — (procedure) |
| A row's ledger entry or spike fails | §7.5, §10 | that row ships its fallback permanently; the result is written back to RESEARCH.md §11's status column by the implementation effort per §G4.4. **A failed row never blocks the milestone** | — (procedure) |
| Any unexpected exception crosses a Phase 14 entry point | the `mod.glue` boundary Phase 1 owns | contained at Phase 1's boundary, shaders disabled, vanilla path resumes. No Phase 14 code path throws into the client | 5 |

Two properties of this table are worth stating explicitly, because they are what make a performance
phase safe:

1. **Every automatic demotion is to the synchronous/per-texture baseline.** Failed sampler
   initialization demotes coherently with owner-safe retirement. Deliberate sampler-0 exclusion for
   an unconvertible texture is valid mixed mode, never permission to leave partial sampler state.
2. **No Phase 14 failure is pack-visible.** A pack cannot observe which tier ran, so no failure here
   reaches §G2.4 rungs 1, 3 or 4 — those rungs concern uniforms, programs and capability gates, none
   of which this phase owns. The rows that reach rung 5 do so because they threaten *vanilla*, not
   because they threaten a pack.

---

## 7. Threading and performance notes

### 7.1 Thread ownership per component

| Component | Thread | Note |
|---|---|---|
| `GlModernizationPlan.derive` | any; pure | no GL, no state; runs at bring-up from a value |
| `SamplerCache` interning, binding, clearing | **render thread only** | inherits `Lwjgl3GLDevice`'s confinement |
| `DsaStrategy` | **render thread only** | same |
| `CenterDepthReadback` (submit, poll, map, ring discard) | **render thread only** | Phase 6 already requires it: *"`UniformPlatformProvider` / `CenterDepthSource` production implementation | render thread only"* (`docs/phase6/v1/PHASE_6_DOC.md:1517`). Async here means *asynchronous GPU transfer*, not another thread |
| `KhrDebugBackend`, incl. its depth counters | **render thread only** | groups are frame-scoped; the debug-message callback may be invoked by the driver on the render thread only, because `GL_DEBUG_OUTPUT_SYNCHRONOUS` is set |
| `CompileExecutor.submit` / `poll` | **render thread only** | the *interface* is render-thread; only the work it schedules is not |
| `GlWorkerContext` and its queue | **the single `schmaloogium-gl-worker` thread** | the only off-render-thread GL in the project, sanctioned by §G2.3 (`docs/design/v3/DESIGN.md:412`–`:415`); its context is made current on that thread once and never elsewhere |
| `InlineCompileExecutor` | **render thread only** | proposed after owner adoption; before it, current P4 synchronous compile remains baseline |
| Measurement procedures (§4.3.6, §4.6.2, §4.7.2) | Phase 2 harness | offline; no production thread |

**No shared mutable state crosses the worker boundary.** `ShaderCompileUnit` is an immutable value
(source text, stage, identity); `CompiledShader` is an immutable result (object id, status, log,
fence). The queue is the only shared structure and it is bounded. There is no lock held across a GL
call in either direction, and the render thread never blocks on the worker (§4.4.2 rule 4).

### 7.2 Allocation posture

Phase 14 must meet the same bar it measures (§4.6.1), so the per-frame paths allocate nothing in
steady state:

- **A1:** `boundPerUnit` is a preallocated `SamplerHandle[16]`; the `MULTI_BIND` path reuses one
  preallocated `int[16]` for `glBindSamplers`; interning happens at estate build and on resize, never
  per frame; the redundant-bind check is an array compare.
- **A2:** the strategy is one field holding one long-lived object; every verb takes primitives.
- **A3:** buffer objects are created once per generation; `last` is a mutable primitive pair; the
  mapped `ByteBuffer` is read for one float and not retained; the fence handle is a `long`. The one
  per-frame object is the mapped buffer, which LWJGL returns from a reusable internal path — if
  profiling shows otherwise, `MemoryUtil.memGetFloat` on the mapped address avoids it entirely, which
  is also ledger row L-9's FFM experiment.
- **A5:** inactive is a no-op object, so there is no counter and no string. Active allocates label
  strings, which is acceptable because the flag is dev-only and already carries the per-call
  `glGetError` cadence (`docs/phase1/v14/PHASE_1_DOC.md:3742`).
- **A4:** allocates per pack switch — a bounded, non-frame event — and nothing per frame.

### 7.3 Known hot paths, and what this phase does to each

| Hot path | Today | After |
|---|---|---|
| Per-pass fixed-unit binding, 16 units | 16 `glBindTexture` (Phase 5's snapshot) | + 1 `glBindSamplers` (`MULTI_BIND`) or ≤16 cached `glBindSampler` (`PER_UNIT`), **−** the per-pass `glTexParameter*` churn A1 removes |
| Per-frame center-depth read, when declared | one synchronous `glReadPixels` — a full pipeline stall (`docs/phase6/v1/PHASE_6_DOC.md:1555`) | one non-blocking status query + one 4-byte map + one async `glReadPixels`; no stall |
| Shadow mipmap generation, per shadow pass | filter set → generate → filter restore, with a whole containment path for restore failure | generate only; the sampling filter is in the sampler (hand-off H-P14→P5-1) |
| Pack switch | synchronous compile of ~40 programs plus two atlas uploads, on the render thread | compile off-thread with a poll loop; the render thread keeps drawing vanilla (spike-gated) |
| Texture and framebuffer creation | bind, edit, restore | DSA where available; fewer binding round-trips and no restore |

**Timing measurement discipline.** Frame-time numbers must be taken with **no** `-Dschmaloogium.debug.*`
flag set. Both `recordGL` and `glLabels` put the device on a per-call `glGetError` cadence
(`docs/phase1/v14/PHASE_1_DOC.md:3741`–`:3742`), which is a synchronous driver query per facade call.
A timing run taken under either flag is invalid, and the A7 audit — which requires both — is
explicitly a *counts* method, never a timing method.

### 7.4 The performance claim this phase is allowed to make

§2.4 of RESEARCH.md permits initial performance worse than OptiFine-with-shaders, and §G2.5 forbids
drifting into the non-goals. So the only claim Phase 14 makes is the one its impl gate states
(§9.2): **pack-switch stall measurably reduced versus the synchronous baseline, without T1
regressions** (`docs/design/v3/DESIGN.md:2583`–`:2584`). Everything else — frame time, allocation
rate, state-change counts — is *measured and reported*, and acted on only through §4.6.3's four-part
test. No row in this phase promises a frame-rate improvement.

### 7.5 The OQ-22 spot-check ledger

The doc gate requires *"each Adapt row → design + fallback + **ledger entry**"*
(`docs/design/v3/DESIGN.md:2578`), and the scope row defines the ledger: *"each §6.2/§6.3 `[U]` claim
this phase relies on gets a row (claim → cheap experiment → decision point)"* (`:2554`–`:2556`).
OQ-22 itself is *"Catch-all for low-risk `[U]` items … the §6.2/§6.3 modernization claims without
their own row"* (`docs/research/v1/RESEARCH.md:1028`). §10.2 is its spike specification; this is the
ledger.

Every row is **claim → cheap experiment → decision point**, with its current evidence. "Cheap" is
the operative word: each experiment is hours, not a milestone.

| ID | Claim, and its RESEARCH tag | Current evidence | Cheap experiment | Decision point |
|---|---|---|---|---|
| **L-1** | *"Core GL program/shader objects … Trivial; behavior-identical"* `[U]` (`docs/research/v1/RESEARCH.md:769`) | Phase 4 already targets core objects; the reference uses core `GL20C`/`GL32C` classes throughout | none needed beyond Phase 4's existing compile tests: assert programs link and packs render identically. **Owner: Phase 4**; ledgered here because A4 builds on it | If any matrix pack behaves differently on core vs ARB objects, escalate to its own OQ row. Expected: closes as confirmed at v0.1 |
| **L-2** | *"**GL 3.3 sampler objects** … Low risk; removes per-frame state churn"* `[U]` (`docs/research/v1/RESEARCH.md:772`) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/sampler/GlSampler.java:10`–`:26]` — deployed and pack-exercised on the 1.12.2 compat context; per-unit cache at `.../IrisRenderSystem.java:367`–`:375` | §4.7.2's audit run **before and after** A1 on one classic pack. Count `glTexParameter*` calls per frame in each | Churn measurably removed **and** `SamplerEquivalenceTest` green ⇒ `SamplerTier` `AUTO` resolves on. Churn not measurable ⇒ the tier still ships for the shadow-mipmap simplification, or `AUTO` resolves off and the row closes as "no measurable win" |
| **L-3** | *"**PBO + fence-sync async readback** … One-frame latency on an already-smoothed value; verify imperceptibility"* `[U]` (`docs/research/v1/RESEARCH.md:773`) | **No readback reference exists** — PD §15: *"No PBO/async readback anywhere"* (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:748`), verified: the tree's only `glReadPixels` is synchronous (`.../IrisRenderSystem.java:190`). **But fence sync itself is deployed** on this compat context: `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/device/GLRenderDevice.java:233]` and the non-blocking poll at `.../gl/sync/GlFence.java:23` | §4.3.6's imperceptibility comparison, in full: four scene families, criteria C1–C4 | All four criteria pass **and** R-P14→P6-1 has landed ⇒ `AUTO` resolves on. Any criterion fails, or the request is declined ⇒ `FORCE_OFF` default and the row closes with its numbers recorded. **C1 failing is a correctness defect requiring diagnosis, not a close** |
| **L-4** | *"**GLFW shared-context async shader compile** … Driver quality for shared compat contexts varies; needs a synchronous fallback"* `[U→OQ-15]` (`docs/research/v1/RESEARCH.md:774`) | **No reference.** The tree never calls `glfwCreateWindow` or `glfwMakeContextCurrent`; PD §16 records that it never touches context creation | this is OQ-15, not a cheap spot-check. §10.1's full spike | Per-driver-family, per §10.1's criteria. Default deny until a family passes (`D-P14-12`) |
| **L-5** | *"KHR_debug labels/groups + debug context … Dev-only; pairs with RenderBook's Nsight integration"* `[V:web]` (`docs/research/v1/RESEARCH.md:775`) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/debug/GLDebug.java:291]` — gated on extension-or-version plus a flag, **no debug context** | capture one frame in RenderDoc or Nsight with `glLabels` set; confirm named groups and labelled objects appear. One session, no code | Groups and labels visible ⇒ closes confirmed. The **debug-context** half is separately gated on OQ-3 (Phase 7) and is ledgered as dependent, not as ours to close |
| **L-6** | DSA tiering is available and behavior-invisible on this platform. *No RESEARCH §6.2 row exists* — a REV1 addition from PD §15, so it has no `[U]` tag and would otherwise have no ledger row at all | `[V:observed — Pintonium .../IrisRenderSystem.java:33`–`:43]` — three tiers selected at init, deployed on the 1.12.2 compat context | `BindingNeutralityTest` across all three tiers on the same call script, plus one T1 run per tier on one classic pack | Identical recorded logs **and** no T1 delta ⇒ `AUTO` resolves to the highest available tier. Any delta ⇒ `BIND_TO_EDIT` default, and the delta is a defect to diagnose. **Requested upstream (§11.4): a §6.2 row for DSA, since the design now relies on it** |
| **L-7** | *"Guaranteed `glGenerateMipmap` (GL 3.0 baseline) … None"* `[U]` (`docs/research/v1/RESEARCH.md:770`) | Phase 1 already derives `supportsMipmapGeneration()` as `atLeast(3,0)` and obliges callers to check (`docs/phase1/v14/PHASE_1_DOC.md:1647`) | none: covered by Phase 1's `GLCapabilityProfileDerivationTest` and Phase 7/8's existing mipmap paths | Ledgered as **closed by Phase 1's design**; listed for completeness because A1's mipmap-filter reasoning depends on it |
| **L-8** | *"**Delete the allocation-discipline design constraint** … generational ZGC on Java 25 makes straightforward code acceptable. Write clean code first, optimize with evidence"* `[U]` (`docs/research/v1/RESEARCH.md:784`) | none — this is the highest-value unverified claim in the phase, because the whole §G2.5 posture rests on it | §4.6.2's allocation profile, on the Phase 2 scenes, at v0.5 | Zero steady-state allocation in `com.schmaloogium.*` **and** GC pauses indistinguishable from the shaders-off baseline ⇒ the posture holds and the row closes confirmed. Any violation ⇒ a finding against the owning phase (§11.5), **not** a reintroduction of OF's array-cache machinery, which §4.8 marks **Skip** (`docs/research/v1/RESEARCH.md:645`) |
| **L-9** | *"FFM API for native buffer work … Replaces reflection-into-direct-buffer hacks; useful for **pixel-transfer paths**"* `[U]` (`docs/research/v1/RESEARCH.md:785`) | none; directly relevant because A3's PBO map and A4's atlas staging are exactly pixel-transfer paths | microbenchmark: read one float from a mapped PBO via `ByteBuffer` vs `MemorySegment`/`memGetFloat`, 10⁶ iterations; and stage one atlas both ways | ≥1% of frame time or ≥1 MB/s saved, per §4.6.3 test 2 ⇒ adopt in `mod.glue` only (`:engine` is C-1-constrained). Otherwise ⇒ closes as "no measurable win"; the straightforward `ByteBuffer` path ships |
| **L-10** | *"Vector API for CPU-side math … measure first (incubator churn risk)"* `[U]` (`docs/research/v1/RESEARCH.md:786`) | none | **not relied on by Phase 14.** Its cited uses — per-quad tangent math, frustum plane tests — are Phase 10's | Ledgered as **out of scope, owner Phase 10**. Recorded so the OQ-22 sweep is complete and nobody assumes Phase 14 closed it |
| **L-11** | *"Modern language features … `MethodHandle`/bytecode-compiled expressions for per-frame custom-uniform evaluation"* `[U]` (`docs/research/v1/RESEARCH.md:787`) | **Unmeasured**; P11 interpreter and metrics/SPI are the baseline, not performance evidence | **P14 owns measurement:** execute P11 §§4.11/10.1 exactly, using §5.8 metrics, on at least two supported Java 25 platforms; procedure below in §10.2 S-22-6 | Interpreter meets p95 ≤0.25 ms, p99 ≤0.50 ms and zero steady allocation ⇒ retain it, no compiler. Real-pack miss after local cleanup with material AST dispatch attribution ⇒ conditional P14 backend prototype inside P11 SPI; require ≥2× failing-p95 improvement, budgets, bounded build/memory and exact semantic differential results or reject to interpreter |
| **L-12** | Compute / SSBO / image load-store / indirect dispatch are feasible on the 1.12.2 compat context | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/IrisRenderSystem.java]` per PD §15 (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:741`–`:744`): *"present and pack-exercised, on the 1.12.2 compat context. **This is the strongest available evidence that G8/S2 is feasible on Cleanroom.**"* | none in Phase 14 | Ledgered as **feasibility evidence for G8/S2 only** (`docs/design/v3/DESIGN.md:788`–`:793`). Not a Phase 14 work item and not a Phase 14 close |
| **L-13** | *"§2.4 effort estimates"*, the other half of OQ-22's catch-all (`docs/research/v1/RESEARCH.md:1028`) | none | not a GL claim; effort estimates are validated by the implementation effort's own tracking, not by a Phase 14 experiment | Ledgered as **out of scope for this phase**, recorded so OQ-22's full text is accounted for and the verify session can see nothing was quietly dropped |

**Coverage check.** RESEARCH.md §6.2 has ten rows (`:769`–`:778`); §6.3 has five (`:784`–`:788`).
Ledgered above: §6.2 rows at `:769` (L-1), `:770` (L-7), `:772` (L-2), `:773` (L-3), `:774` (L-4),
`:775` (L-5), `:778` (L-12). Not ledgered, with reason: `:771` core geometry shaders — Phase 3/4's
preprocessor concern, no Phase 14 reliance; `:776` explicit GLFW context hints and `:777` HiDPI —
both `[Q:OQ-3]`, owned by Phase 7, and A5's debug-context half is ledgered as *dependent* on that
outcome in L-5. §6.3 rows: `:784` (L-8), `:785` (L-9), `:786` (L-10), `:787` (L-11); `:788` JUnit 6 +
headless GL testing is `[Q:OQ-10]`, owned by Phase 2. Plus L-6 for DSA, which has no RESEARCH row and
is flagged upstream in §11.4.

---

## 8. Testability plan

§G6 requires *"per-phase headless tests — every phase doc's §8 specifies JUnit tests of its subsystem
against the `engine.gl` facade / recorded `GLCapabilityProfile`s"* (`docs/design/v3/DESIGN.md:705`–`:708`).
Phase 14 has an unusual testability profile and it is stated plainly rather than glossed: **its
policy is fully headless-testable, and its mechanism is not.** Tier *selection*, `SamplerKey`
*derivation*, group *balance* and audit *classification* are pure functions of values and are tested
with JUnit alone. Whether a given driver's DSA path or shared context actually works is only
answerable on a driver, and §8.3's harness runs are where that is settled.

### 8.1 Headless unit tests (`:engine`, JUnit only, no GL context)

| Test | Assertions |
|---|---|
| `ModernizationPlanDerivationTest` | Over a matrix of `GLCapabilityProfile` fixtures — GL 2.1, 3.0, 3.2, 3.3, 4.3, 4.5, and 3.3-with-`ARB_direct_state_access` — each of the five tiers resolves to the documented value; every `FORCE_OFF` yields the reference-faithful tier regardless of capability; every `FORCE_ON` on an incapable profile still yields the fallback (**policy may not overrule capability**); the rationale list is non-empty and names the deciding capability for each row |
| `SamplerKeyDerivationTest` | `SamplerKey.of` maps Phase 5's colour policy exactly: `CLAMP_TO_EDGE` S/T for every colour texture, NEAREST min **and** mag for integer formats, LINEAR otherwise (`docs/phase5/v1/PHASE_5_DOC.md:1042`–`:1045`); shadow textures map `hardwareFiltering → COMPARE_REF_TO_TEXTURE`, `mipmap → *_MIPMAP_*` min filter, `nearest → NEAREST` (`docs/phase5/v1/PHASE_5_DOC.md:534`); equal parameters yield equal keys (structural equality, so interning is deterministic) |
| `SamplerStateSplitTest` | Every field of `TextureParameters` is classified exactly once as sampler state or texture state, with **no field unclassified** — this is the test that fails loudly if R-P14→P1-3 lands a field this design did not anticipate, rather than letting it silently fall through |
| `SamplerEquivalenceTest` | Over a recorded estate: for every texture and every unit it binds to, the sampler state applied through that unit equals the sampler state its `TextureParameters` specify. A texture whose parameters cannot be derived (the R-P14→P13-2 gap) is asserted **excluded and reported**, never silently defaulted. This is A1's whole behavioral-no-op obligation, discharged as a property over the estate |
| `SamplerCacheLifecycleTest` | Each of all eight resize reasons invalidates bound-unit knowledge; retired identity cannot bind even with outstanding leases/equal hashes; outstanding live/retiring references prevent premature deletion; borrowed texture is never deleted; callback failure preserves exact installed-off/deliveredCount handling rather than falsely reporting SUCCESS |
| `SamplerLeakTest` | Over arbitrary bounded sequences of scope push/pop/throw and all three `FrameExitKind`s plus all six `FrameAbortReason`s, the recorded log ends every frame with all sixteen units at sampler 0 (`D-P14-4`) |
| `FixedUnitDisciplineTest` | The bind cache is indexed by App B.3 unit; no code path allocates, reassigns or reorders a unit; `depthtex1` resolves to 11. A regression here would reintroduce the pre-decided divergence at `docs/design/v3/DESIGN.md:954`–`:955` |
| `BindingNeutralityTest` | Against `RecordingGLDevice`, the same call script under all three `DsaTier`s produces (a) an identical facade-verb log, and (b) an identical observable binding state after every non-binding verb (`D-P14-7`). This is the proof that the tiers are substitutable |
| `CenterDepthRingTest` | Warm-up returns `Unavailable`; a signalled fence yields `Sample`; an unsignalled fence yields the previous value; ≤0 dimensions return `Unavailable` **with no GL call** (`docs/phase6/v1/PHASE_6_DOC.md:585`–`:587`); the render thread issues no blocking wait on any path — asserted by the absence of a `clientWaitSync`/`finish` record in the log (`D-P14-9`) |
| `CenterDepthInvalidationTest` | Each of `registryGeneration`, `worldEpoch`, width, height, `pixelX`, `pixelY` changing independently discards the whole ring and returns `Unavailable`; **no value from a prior world, generation or extent is ever returned** (`D-P14-10`) |
| `CenterDepthTraceComparisonTest` | Given two recorded traces from a scripted depth sequence, the analytic criteria C1 and C2 of §4.3.6 are computed and asserted headlessly. The *judgement* is thereby testable without a GPU; only the *capture* needs one |
| `CompileExecutorContractTest` (only after P4/P7 grant) | Completed units preserve deterministic source order and P4 failure/fallback semantics; failed/canceled/stale batches cannot publish, late completion disposes exactly once after worker acknowledgement, and publication remains render-thread. Worker submit/poll is non-waiting; Inline may compile synchronously in submit |
| `CompileFallbackLadderTest` | Each of §4.4.3's six stages, injected in turn, degrades to `INLINE`, tears down the worker, emits exactly one diagnostic, and completes the pipeline build synchronously. The watchdog fires at `T_watchdog` and never earlier |
| `DriverPolicyTest` | An unknown vendor/renderer resolves to `INLINE` (**default deny**, `D-P14-12`); an allowlisted family resolves to `SHARED_CONTEXT`; `FORCE_OFF` overrides an allowlist entry; a denylist entry overrides `FORCE_ON` |
| `DebugGroupBalanceTest` | Over arbitrary bounded push/pop/throw sequences across `NORMAL`, `EARLY_RETURN` and `THROWN` exits: recorded pops never exceed recorded pushes; an underflow is a no-op plus exactly one diagnostic; an overflow past `GL_MAX_DEBUG_GROUP_STACK_DEPTH` issues nothing and its matching pop issues nothing; depth is zero at every frame boundary (`D-P14-13`). **This is PD B7's bug shape, asserted impossible** |
| `DebugLabelCoverageTest` | Every handle created in a recorded estate carries a distinct, non-blank label taken from its `create(String debugLabel)` argument; labels are clamped to `GL_MAX_LABEL_LENGTH`; over-long labels never reach GL (PD B9's handling) |
| `DebugInactiveIsFreeTest` | Under `DebugTier.NONE`, the recorded log contains **zero** debug records for a full frame of pushes, pops and labels |
| `AuditClassifierTest` | Given a synthetic `GLCallLog`: redundant-identical, redundant-restore, necessary and contract-mandated are classified per §4.7.2 step 4; matrix uploads and the program-switch sweep always land in contract-mandated and never in a ranking; a log with a non-zero discard count is **rejected as invalid** (gate 1); two non-identical steady frames **fail** the determinism gate (gate 3) |
| `AuditScopeFilterTest` | A synthetic log containing foreign-handle records has them dropped by the ownership filter; the classifier's output cannot contain a non-Schmaloogium subject (`D-P14-16`) |

### 8.2 Recorded-GL and profile-fixture tests

`RecordingGLDevice` plus serialized `GLCapabilityProfile` fixtures (`docs/phase1/v14/PHASE_1_DOC.md:1516`,
`:3741`) carry the integration-shaped assertions that still need no driver:

- The **same recorded log** is produced under every `DsaTier`, and under `SamplerTier.NONE` the log is
  byte-identical to the pre-Phase-14 estate build. This is the single strongest available evidence
  that A1's fallback and A2 as a whole are true no-ops.
- A full frame under `SamplerTier.MULTI_BIND` shows exactly one sampler-range bind per pass and one
  clear per frame; under `PER_UNIT`, at most sixteen binds per pass with the redundant-bind cache
  suppressing repeats.
- Scripted GL errors (`ScriptedResponses.glError`) drive every §6 demotion row: a sampler-creation
  error falls the whole estate back to `NONE`; a DSA error demotes to `BIND_TO_EDIT` and re-runs the
  operation; a `glObjectLabel` error clamps once then disables labelling.
- At least two profiles are used throughout, per Phase 6's own gate shape: a minimum GL 2.1 profile
  (every tier falls back) and a GL 4.5 profile (every tier engages). A 3.3 profile without
  `ARB_direct_state_access` exercises the mixed case that is easiest to get wrong.

### 8.3 What only a driver can settle, and where it is settled

| Question | Where |
|---|---|
| Does this driver's DSA path behave identically? | §9.2's implementation gate: one T1 run per tier on one classic pack; L-6's decision point |
| Does a shared compat context work on this driver family? | **OQ-15's spike, §10.1** — the only way this is answerable |
| Is the async center-depth latency imperceptible? | **§4.3.6's comparison**, criteria C1–C4; L-3's decision point |
| Does the clean-code allocation posture actually hold? | **§4.6.2's profile**; L-8's decision point |
| Where is the real redundant-state churn? | **§4.7.2's audit**; L-2's decision point |
| Do labels and groups show up in Nsight/RenderDoc? | L-5's one-session experiment |

### 8.4 Conformance-tier coverage

- **T0** — every tier combination must load every classic matrix pack. A tier that changes *whether*
  a pack loads is a defect, not a trade-off.
- **T1** — the binding gate. Every enabled row must show **no regression** against the approved T1
  baseline. This is the criterion the impl gate names (`docs/design/v3/DESIGN.md:2583`–`:2584`) and
  it is the reason every row has a runtime `FORCE_OFF`.
- **T1 camera-path motion** — mandatory for A3, per §G6 REV1 (`docs/design/v3/DESIGN.md:696`–`:699`).
  A one-frame input delay is a temporal effect; a static-only comparison would be worthless.
- **T2** — A3 is the only row that could perturb a pixel-parity comparison, through DOF focal
  distance. Criterion C3 runs against T1 tolerance; if T2 is available for the classic packs at v0.5,
  the same comparison is repeated at T2 tolerance and the stricter result governs.
- **T3** — the joint v0.5 gate with Phase 13 (§9.2).

No pack source text and no rendered image enters the repository; every artifact here is a counts or
hash manifest in the local/CI cache, per §G6 (`docs/design/v3/DESIGN.md:718`–`:725`).

---

## 9. Milestone staging

### 9.1 Component → milestone

Per §G4.3 every designed component carries exactly one tag (`docs/design/v3/DESIGN.md:567`–`:571`).
Phase 14's spec milestone is *"v0.5 + quality-of-life"* (`docs/design/v3/DESIGN.md:2516`), and §G0.3's
principle applies: the whole subsystem is architected now, tagged by when it is implemented
(`:185`–`:193`).

| Component | Architected | Implemented | Note |
|---|---:|---:|---|
| `GlModernizationPlan` / tiers / `GlModernizationPolicy` | now | **v0.5** | pure values; the first thing built, because every other row reads its tier |
| `SamplerKey` + derivation (A1) | now | **v0.5** | derived from Phase 5's parameters; Phase 13's inputs arrive at the same milestone |
| `SamplerCache`, per-unit bind cache, `MULTI_BIND` batching (A1) | now | **v0.5** | |
| Sampler clear on every vanilla-return path (A1, `D-P14-4`) | now | **v0.5** | ships with the tier, never after it — it is a rung-5 guard, not a polish item |
| `DsaStrategy` three tiers (A2) | now | **v0.5** | facade-internal; no dependent phase is affected |
| `CenterDepthReadback` PBO+fence ring (A3) | now | **v0.5** | gated on R-P14→P6-1 **and** §4.3.6's criteria; ships `FORCE_OFF` otherwise |
| Synchronous `CenterDepthSource` (A3 fallback) | — | **v0.1, by Phase 6** | already designed and staged by Phase 6 (`docs/phase6/v1/PHASE_6_DOC.md:1643`); Phase 14 adds nothing at v0.1 |
| `CompileExecutor` + `InlineCompileExecutor` (A4 proposal) | now | **after owner grants** | R-P14→P4-1 and R-P14→P7-1 first; current synchronous compile remains until then |
| `GlWorkerContext` + `WorkerCompileExecutor` (A4) | now | **post-v0.5** | additionally gated on OQ-15 evidence; no owner split/resumability grant is inferred from a successful spike |
| Async `_n`/`_s` atlas upload (A4) | now | **post-v0.5** | additionally gated on R-P14→P13-1 |
| `KhrDebugBackend` — labels and groups (A5) | now | **v0.5** | Phase 1 already stages `schmaloogium.debug.glLabels` at *"`v0.5` | Phase 14"* (`docs/phase1/v14/PHASE_1_DOC.md:4508`); gated on R-P14→P1-1 |
| `GL_DEBUG_OUTPUT` message callback (A5) | now | **v0.5** | no context change needed |
| `GLFW_OPENGL_DEBUG_CONTEXT` request (A5) | now | **post-v0.5** | gated on OQ-3's outcome, which is Phase 7's, not ours |
| Allocation/GC measurement procedure (A6) | now | **v0.5** | runs at v0.5 because that is when the pipeline is feature-complete enough for the numbers to mean anything |
| §4.6.3's four-part justification test (A6) | now | **v0.5, standing** | a standing rule for the implementation effort, not a one-off |
| OQ-22 ledger execution (A6, §7.5) | now | **v0.5**, rows L-4/L-9 **post-v0.5** | each row closes at the milestone that touches it, exactly as OQ-22's verification path says (`docs/research/v1/RESEARCH.md:1028`) |
| Redundant-state audit procedure (A7) | now | **v0.5** | requires A5's groups for segmentation, so it follows A5 in §12 |
| §4.3.6 imperceptibility comparison | now | **v0.5** | blocking for A3's enablement |

Nothing in this phase is tagged v0.1–v0.4. That is correct and deliberate: every row replaces
something that must exist first, and RESEARCH.md §9 places *"depth copies incl. async center-depth"*
in v0.5 (`docs/research/v1/RESEARCH.md:951`).

### 9.2 The implementation gate

*"**Impl gate:** RESEARCH.md §9 v0.5 (jointly with Phase 13) — full classic matrix at T3;
pack-switch stall measurably reduced vs the synchronous baseline without T1 regressions"*
(`docs/design/v3/DESIGN.md:2583`–`:2584`). RESEARCH.md §9's v0.5 row is *"`_n`/`_s` companion atlases
+ `MC_NORMAL_MAP`/`MC_SPECULAR_MAP`; noise texture; custom textures (all 3 source forms); depth
copies **incl. async center-depth**; render scale; instancing"* with exit criterion *"Full classic
matrix at T3"* (`docs/research/v1/RESEARCH.md:951`).

Phase 14's half of the joint gate, stated as runnable conditions:

1. **Full classic matrix at T3** — jointly with Phase 13; Phase 14's contribution is that no enabled
   tier prevents any classic pack from reaching T3.
2. **No T1 regression on any matrix pack, under every tier combination that ships enabled by
   default.** This is the binding constraint. A row that cannot meet it ships `FORCE_OFF`.
3. **Pack-switch stall measurably reduced versus the synchronous baseline.** Measured as: median
   wall-clock time from an accepted `ShaderReloadController.request` to `ReloadStatus.Active`, and —
   the number that actually matters to a user — the **count of frames whose frame time exceeds twice
   the scene median** during that window. Three runs, median, one classic pack, on the pinned dev
   environment, with no `-Dschmaloogium.debug.*` flag set (§7.3). *"Measurably reduced"* is read as:
   the long-frame count strictly decreases and the total reload time does not increase.
   **Honest note:** condition 3 is achievable only through A4, which is `post-v0.5` and spike-gated.
   If OQ-15 fails or R-P14→P7-1 is declined, **this condition cannot be met at v0.5** and the gate
   must be read as satisfied by conditions 1–2 with condition 3 deferred, its spike result recorded.
   This is stated rather than papered over; it is also flagged in §11.4 as a requested upstream
   clarification, because a gate that depends on an unresolved OQ should say so.
4. **Every ledger row in §7.5 has closed** — confirmed, refuted, or explicitly out of scope with its
   owner named — and the results are written back into RESEARCH.md §11's OQ-22 status column by the
   implementation effort per §G4.4 (`docs/design/v3/DESIGN.md:578`–`:580`).
5. **Every §8.1 test green against at least two recorded capability profiles**, matching the shape of
   the sibling phases' gates.

---

## 10. OQ and spike specifications

Two OQs are assigned to Phase 14 by §G10: OQ-15 (`docs/design/v3/DESIGN.md:878`) and OQ-22 (`:885`).
Per §G4.4 each spec has four parts — the question verbatim from RESEARCH.md §11, a concrete
procedure, success and failure criteria, and the fallback designed *now*
(`docs/design/v3/DESIGN.md:575`–`:580`). **Neither OQ is resolved here.**

### 10.1 OQ-15 — shared-context async compile

**(1) The question, verbatim from RESEARCH.md §11** (`docs/research/v1/RESEARCH.md:1021`):

> "Shared-context async compile reliability across drivers (compat contexts)"

Its row records *"Why it matters: §6.2 headline feature"*, *"Blocks: quality-of-life"*, and
*"Verification path: prototype + synchronous fallback design"*, status **open**. §G10 assigns it to
**P14** with the handling *"Spike spec: shared-context async compile; sync fallback mandatory"*
(`docs/design/v3/DESIGN.md:878`), and notes REV1 leaves it *"unaffected itself"*.

**Current evidence: none.** Pintonium never touches context creation — a search of the tree for
`glfwCreateWindow`, `glfwMakeContextCurrent` and share-context construction returns nothing, and PD
§16 records the same (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:756`–`:779`), which Phase 7
also relies on for OQ-3 (`docs/phase7/v1/PHASE_7_DOC.md:2297`–`:2299`). This spike has no reference
implementation to lean on, which is why its fallback is mandatory rather than prudent.

**(2) Procedure.** In a pinned Cleanroom dev environment, on **≥2 driver families** — NVIDIA
proprietary and AMD (Mesa `radeonsi`) are the minimum pair, with Intel (Mesa `iris`) and Windows AMD
strongly preferred, giving four:

1. **Context creation.** From the render thread after Minecraft's window exists, create a hidden
   shared context: `glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)` then
   `glfwCreateWindow(1, 1, "", NULL, mainWindow)`. Record whether creation succeeds, what version and
   profile the worker context reports, and whether it matches the main context. Requesting no
   version hints (inheriting the main context's) and explicitly requesting compat are both tried;
   record which the loader tolerates. **Note the interaction with OQ-3**: if Cleanroom's window layer
   owns hint state, setting a hint here may perturb later window creation — verify and record, and
   restore every hint after the call.
2. **Currency.** Make the worker context current on the dedicated worker thread. Verify it is never
   current on the render thread, and that the render thread's context is unaffected (draw one vanilla
   frame after making the worker current and confirm it is unchanged).
3. **Compile correctness.** Off-thread, compile the complete shader set of **each classic matrix
   pack** — SEUS Renewed, Chocapic13 V9, projectLUMA — plus one dual-spec pack. On the render thread,
   `glWaitSync` the worker's fence, then attach, link and validate. Compare, per program: link
   status, the info log, the complete active-uniform list with locations and types, and the active
   attribute list, against a fully synchronous run of the same pack. **Any difference is a failure**,
   not a curiosity.
4. **Rendering correctness.** Render each pack on the Phase 2 fixed scenes **and** the camera-path
   motion scenes, and diff against the synchronous run's T1 baseline. Corruption may be
   intermittent, so repeat each pack-switch 20 times per family and require every run to pass.
5. **Texture upload.** Repeat 3–4 for a companion-atlas-sized upload (two full atlases with mip
   chains) issued from the worker, checking pixel equality of the uploaded atlas against a
   synchronously uploaded one.
6. **Stall measurement.** Instrument the pack switch exactly as §9.2 condition 3 defines: median time
   from accepted request to `Active`, and the count of frames exceeding twice the scene median during
   the window. Measure synchronous and async, three runs each, no debug flags set.
7. **Adversarial cases.** (a) Switch packs again while a compile batch is in flight. (b) Change
   dimension mid-compile. (c) Resize the window mid-compile. (d) Kill the worker thread mid-batch.
   (e) Exit the client mid-batch. Each must degrade per §4.4.3 and must not hang, crash, or leak a
   GL object.
8. **Record** driver versions, OS, GPU, and every result in `docs/decisions/OQ-15_ASYNC_COMPILE.md`,
   in the shape Phase 7 uses for OQ-3 (`docs/phase7/v1/PHASE_7_DOC.md:2312`–`:2313`), including a
   per-family verdict table that becomes `D-P14-12`'s allowlist data.

**(3) Success and failure criteria.** Per family, and **all must hold** for that family to be
allowlisted:

| # | Criterion | Threshold |
|---|---|---|
| K1 | **No corruption.** Link status, info logs, active-uniform lists (name, type, location) and active-attribute lists are identical to the synchronous run for every program of every pack. | exact equality |
| K2 | **No visual difference.** Every scene, static and motion, diffs within Phase 2's T1 tolerance against the synchronous run, with zero new outliers, across all 20 repeats. | zero failures in 20 |
| K3 | **Stall below threshold.** The count of frames exceeding twice the scene median during the switch window is **≤ 20% of the synchronous count**, and the median request-to-`Active` time does not increase. | ≤20% long frames; no total-time regression |
| K4 | **Adversarial safety.** All five cases in step 7 degrade per §4.4.3, with no hang, no crash, no leaked GL object and no state corruption. | zero failures |
| K5 | **Stability.** No driver crash or reset across the full run on that family. | zero |

K3 is the threshold the specification asks to be stated (*"success: no corruption + stall <
threshold"*, `docs/design/v3/DESIGN.md:2545`–`:2546`). It is deliberately expressed as **long-frame
count** rather than total time: a pack switch that takes the same wall-clock time but stops freezing
the client is the actual user-visible win, and a total-time-only threshold could be met by a change
that helps nobody. The 20% figure is a target, not a physical constant; a family that achieves K1,
K2, K4 and K5 but only reaches, say, 50% is recorded as a **partial pass** — allowlisted only if the
implementation effort judges the reduction worth the added surface, with the number recorded either
way.

**Failure:** any family failing any criterion is **not allowlisted**; it runs `INLINE` permanently.
Failure on *every* family closes OQ-15 as "shared compat contexts are not reliable on our matrix",
`WorkerCompileExecutor` is not shipped, and the result is written back to RESEARCH.md §11's status
column. **No milestone is blocked** — see §9.2's honest note on gate condition 3.

**(4) Fallback, designed now.** Current Phase 4 synchronous `compile(RegistryBuildRequest)` ships
unless its owner explicitly adopts R-P14→P4-1 and P7 adopts R-P14→P7-1. After adoption,
`InlineCompileExecutor` is the same-thread default and automatic driver fallback. A failed spike
never requires migration of an ungranted interface and never changes render-thread publication.

### 10.2 OQ-22 — the modernization-claim spot-check ledger

**(1) The question, verbatim from RESEARCH.md §11** (`docs/research/v1/RESEARCH.md:1028`):

> "Catch-all for low-risk `[U]` items: §2.4 effort estimates and the §6.2/§6.3 modernization claims
> without their own row (core-GL swap, sampler objects, PBO readback latency, FFM/Vector API payoffs,
> expression-engine compilation)"

Its row records *"Why it matters: Individually small; collectively they shape effort planning"*,
*"Blocks: implementation-time choices"*, and *"Verification path: spot-check each at the milestone
that touches it; promote to its own OQ row if it turns out contentious"*, status **open**. §G10
assigns it to **P14** with the handling *"Spot-check ledger for the §6.2/§6.3 modernization claims"*
and the REV1 note that *"PD §15 supplies ledger evidence (DSA tiers, sampler objects, compute/SSBO on
1.12.2 compat)"* (`docs/design/v3/DESIGN.md:885`).

OQ-22 is not one question but thirteen, so its spike is a **sweep**, and §7.5's ledger is its
instrument. Its verification path — *"spot-check each at the milestone that touches it"* — is why the
rows close individually rather than together.

**(2) Procedure.**

- **S-22-1 — freeze the row set.** At the start of the v0.5 implementation effort, re-derive the
  ledger from RESEARCH.md §6.2 and §6.3 as they then stand and confirm §7.5's coverage check still
  accounts for every row, either with a ledger entry or with a named owner elsewhere. A row added to
  §6.2/§6.3 since 2026-07-24 that this phase relies on gets a new ledger row.
- **S-22-2 — run each row's cheap experiment**, exactly as §7.5's "cheap experiment" column
  specifies, at the milestone its "decision point" names. Each is hours, not days; that is the design
  constraint on the column.
- **S-22-3 — scheduled representative runs.** L-3's imperceptibility comparison, L-8's allocation
  profile and L-11's representative expression workload run with the appropriate conformance
  environment. L-11 follows P11's fixed scripted inputs and separate allocation-profile run,
  not a substituted microbenchmark of a single AST node.
- **S-22-4 — record and write back.** Each row closes as **confirmed**, **refuted**, or **out of
  scope (owner named)**, with its measured numbers. The implementation effort writes the results into
  RESEARCH.md §11's status column and adds an addendum note to this phase doc, per §G4.4
  (`docs/design/v3/DESIGN.md:578`–`:580`). **This document does not modify RESEARCH.md** (§G1.1).
- **S-22-5 — promote what turns out contentious.** OQ-22's own verification path directs it:
  *"promote to its own OQ row if it turns out contentious"*. A row whose experiment produces a
  disputed or platform-dependent result is proposed as a new OQ in this doc's §11.4 rather than
  argued to a conclusion inside the ledger.
- **S-22-6 — accepted P11 expression method (D-P14-20).** Run all custom expressions from each
  locally acquired matrix pack plus the synthetic 128-uniform/256-variable graph at 40 switches
  per frame, on at least two supported Java 25 platforms. Use a release JVM warmed for 30 seconds,
  deterministic scripted inputs and interpreter semantic checks; sample allocations separately.
  Consume §5.8 immutable metrics and correlate refreshes with the harness frame, not a new P7 timer.
  Record source-free pack identity/custom definition counts, switches/frame, node evaluations,
  p50/p95/p99 total expression nanos/frame, worst refresh nanos, steady allocations, plan-build
  time/memory and profile top nodes; retain no pack expressions or provider snapshots.
  First measure the unmodified interpreter, then only profile-guided local interpreter cleanup.
  All real packs and stress workload meeting p95 ≤0.25 ms, p99 ≤0.50 ms and zero steady allocation
  closes the decision as retain-interpreter. A supported real pack still missing after cleanup,
  with material cost attributed to AST dispatch rather than provider/sink work, is the only
  compiled-backend trigger. A stress-only miss is recorded/investigated, not compiler authorization.
  The conditional P14 prototype stays inside P11's private SPI and requires at least 2× improvement
  of the failing p95, both frame budgets, bounded build time/memory, and bit/diagnostic/effect-order
  identity over every P11 golden/fuzz vector. Unsafe bytecode, license-heavy dependency, instability,
  slowdown or semantic mismatch rejects it. Keep interpreter and existing cadence/diagnostics.
  Record the measured decision in L-11 and a P11 addendum; propose RESEARCH OQ-22 write-back through
  §11.4 only after evidence. This document records no measurement outcome or compiled backend grant.

**(3) Success and failure criteria.**

- **Success for the sweep** is not "every claim confirmed" — it is **every row closed with evidence**.
  A refuted row is a successful spot-check: OQ-22's purpose is to stop unverified `[U]` claims from
  silently shaping effort planning, not to validate them.
- **Per-row criteria** are the "decision point" column of §7.5, which is where they belong so that a
  row's claim, experiment and criterion are read together.
- **Failure of the sweep** is a row left open at the v0.5 gate with no evidence and no named owner.
  §9.2 condition 4 makes that a gate failure, which is the only enforcement OQ-22 needs.
- **Escalation:** L-8 is the row to watch. It is the only one whose refutation would have
  architectural consequences beyond this phase, because §G2.5's entire clean-code-first posture rests
  on it. If L-8 is refuted, the response is **not** to reintroduce OF's allocation-discipline
  machinery — §4.8 marks that **Skip** (`docs/research/v1/RESEARCH.md:645`) — but to raise a new OQ
  and route specific findings to their owning phases through §11.5.

**(4) Fallback, designed now.** Every ledger row's fallback is its row's fallback, already designed
in §4: A1 → `SamplerTier.NONE`; A2 → `BIND_TO_EDIT`; A3 → `SYNCHRONOUS`; A4 → `INLINE`; A5 → `NONE`;
A6 → the null change (do nothing, which is what §G2.5 predicts); A7 → report nothing rather than
report estimates. Because `AUTO` resolves to the reference-faithful path until a row's ledger entry
closes (`D-P14-17`), **an unrun ledger is not a risk** — it is simply a shipped product that behaves
exactly as it would have without this phase. That property is what makes OQ-22 safe to leave open
across a milestone, and it is the reason `D-P14-17` exists.

---

## 11. Decisions and open items

### 11.1 Phase-local decision log

Per §G1.1, phase-local decisions get IDs `D-P14-<k>` with a one-line rationale each
(`docs/design/v3/DESIGN.md:277`–`:281`). None of these contradicts RESEARCH.md's `D-1`…`D-10`; §11.3
records why. **No decision here adopts a Pintonium mechanism for a contract-visible component**, so
none requires the §G11.4 contract check reserved for that case (`docs/design/v3/DESIGN.md:947`–`:951`)
— §1.3 and §3.2 establish that this phase owns no contract-visible component. Each Pintonium-derived
decision nevertheless carries its source-verified provenance in §3.2.

| ID | Decision | One-line rationale |
|---|---|---|
| **D-P14-1** | Derive sampler state from P5 owner parameters and P13 `TextureParameterSpec` through a complete granted P1 conversion, never a second authored policy | unknown fields keep sampler 0 rather than guessed defaults |
| **D-P14-2** | Sampler tiering `MULTI_BIND → PER_UNIT → NONE`, chosen once at init from `GLCapabilityProfile` | `NONE` is byte-for-byte today's path, so the fallback needs no separate design |
| **D-P14-3** | Reject dynamic per-program texture-unit allocation; the bind cache is a fixed `SamplerHandle[16]` indexed by App B.3 unit | §G11.4 pre-decided rejection (`docs/design/v3/DESIGN.md:954`–`:955`); a fixed dense map also suits `glBindSamplers` better than a dynamic one |
| **D-P14-4** | All sixteen sampler units are cleared on **every** path that returns control to vanilla, from the backend's own `finally` | a leftover sampler silently overrides vanilla's filtering — a §G2.4 rung-5 vanilla-corruption hazard, not a leak |
| **D-P14-5** | DSA tiering is entirely internal to the `mod.glue` LWJGL3 backend; no `:engine` type names a tier | the spec asks for a facade-*internal* strategy (`docs/design/v3/DESIGN.md:2534`–`:2535`), and internality is what makes it behavior-invisible |
| **D-P14-6** | `bindToUnit` is **excluded** from DSA and stays on the `GlStateManager`-cooperating path, diverging from the reference | `glBindTextureUnit` bypasses state `GlStateManager` caches, which §G4.6 forbids because the stale cache breaks vanilla rendering |
| **D-P14-7** | Every non-binding facade verb is **binding-neutral** under all three DSA tiers, asserted by a recorded-GL test | tier substitutability requires that no caller can depend on a bind side effect; stating it as a property makes it testable |
| **D-P14-8** | Async center-depth is implemented below Phase 6's `CenterDepthSource` seam, with its added latency **contracted** through R-P14→P6-1, never hidden | Phase 6 explicitly forbids *"an uncontracted one-frame queue"* (`docs/phase6/v1/PHASE_6_DOC.md:1558`), and the type would otherwise permit exactly that |
| **D-P14-9** | The render thread polls the readback fence with `glGetSynci(GL_SYNC_STATUS)` and **never** blocks on it | a blocking wait would reinstate the stall the row exists to remove, on a worse schedule than the synchronous read |
| **D-P14-10** | Any change of registry generation, world epoch, framebuffer extent or pixel coordinate discards the **entire** PBO ring and returns `Unavailable` | a depth value from another world, pack generation or framebuffer size must never enter the EMA; partial invalidation cannot express that safely |
| **D-P14-11** | Async compile splits at **link**: shader-object compile off-thread; program creation, link, validate, uniform locations and the `Program.use()` barrier stay on the render thread | linking is where shared-context driver bugs concentrate, and every ownership rule in the project keeps its render-thread confinement untouched |
| **D-P14-12** | Driver eligibility for the shared context **defaults to deny**; a family is enabled only by a recorded OQ-15 pass, as shipped data | an unrun or partially-run spike then costs nothing, because the safe path is the default rather than the exception |
| **D-P14-13** | KHR_debug group balance is guaranteed **in the backend** — depth counter, no-op underflow and overflow, frame-boundary drain — not by call-site discipline | PD B7 is a call-site-discipline failure (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:793`); making imbalance harmless is the only handling a future caller cannot undo |
| **D-P14-14** | The KHR_debug gate is `(KHR_debug or GL 4.3) && -Dschmaloogium.debug.glLabels`; a debug context is an enhancement, never a precondition | Phase 7's OQ-3 default is *"make no context-flag change"*, under which a debug-context precondition would make `DebugService` permanently dead (finding C-3) |
| **D-P14-15** | An optimization is justified only if all four of §4.6.3's tests hold — ours, ≥1% frame time or ≥1 MB/s measured, no-op with fallback, no added contract complexity | §G2.5's *"optimize with evidence"* needs a threshold to be a rule rather than a slogan, and test 4 protects contract-visible components from being "improved" |
| **D-P14-16** | The redundant-state audit is scoped **by construction**: its instrument only sees facade calls on Schmaloogium-owned or -borrowed subjects | vanilla's rendering never reaches the facade, so the §1.2 non-goal becomes a property of the method rather than a rule the auditor must remember |
| **D-P14-17** | Every row is individually switchable at runtime, and `AUTO` resolves to the **reference-faithful path** until that row's ledger entry or spike closes | an unrun ledger then ships a product identical to one built without this phase, which is what makes leaving OQ-22 open across a milestone safe |
| **D-P14-18** | Phase 14 adds **no Mixin and no vanilla hook**; every call site it needs is one an existing phase already owns | `D-5` bounds the injection budget to ~25–30 sites (`docs/research/v1/RESEARCH.md:99`), and a performance phase has no business spending from it |
| **D-P14-19** | Adopt current P5 sole-policy/physical-binding/Bound-only lease protocol, all eight resize reasons, P6 resolver, P13 conversion/retirement and P7 ten-step construction points | fixes IR-23 without a second binder, map, guessed generation or revived lease |
| **D-P14-20** | P14 accepts P11's OQ-22 method/metrics and owns L-11 decision; compiler only after a measured real-pack miss and exact differential success | IR-15 recipient accepts the handoff while P11 retains semantics/SPI |
| **D-P14-21** | Accept P7's narrowed internal-timing promise and JFR attribution, leaving public elapsed projection optional/ungranted | IR-26 tooling cannot block synchronous baseline |
| **D-P14-22** | Require explicit P4 prepare/worker/link ownership adoption plus P7 resumability before any async caller migration; current synchronous/Inline fallback remains | IR-27 prevents a policy proposal from minting compiler ownership |

### 11.2 Input contradictions found, with rulings and provenance

Reported, never silently resolved (`docs/design/v3/DESIGN.md:282`–`:284`, `:141`–`:143`).

**C-1 — Historical initial-build contradiction: the brief said Phase 13 was unbuilt.**
`docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md:50`–`:51` says *"`docs/phase13/` does not exist. There
is no `PHASE_13_DOC.md` to read at all — this is an unbuilt phase, not merely an unverified one."*
During the initial build session `docs/phase13/v1/PHASE_13_DOC.md` was present: 1 435 lines, thirteen `##`
sections, mtime 2026-08-08 14:04 — untracked when first observed, then committed mid-session as
`9ff94a5` — with `docs/phase13/reviews/` containing only `.gitkeep`, i.e. **zero review rounds**.
`docs/MOVES.md`'s Phase 13 v3 adoption record (`docs/MOVES.md:82`, `:89`, `:91`) confirms a Phase 13
build session ran. **Historical ruling:** the initial-build instruction and §G5.3 verification gate
prevented consuming that concurrent draft; the initial author did not read it. This preserves the
original quotation and provenance, not an active absence claim. **Current IR-23 disposition:**
§5.5 now consumes the actual P13 contract, including exact parameters and retirement; only the
stageable-upload/full-sampler-conversion extensions remain gated. Current coordinated bytes still
need fresh owner verification.

**C-2 — Three dependencies declare RC3 while being adjudicated against v3.**
`docs/phase5/v1/PHASE_5_DOC.md:18`, `docs/phase6/v1/PHASE_6_DOC.md:10` and
`docs/phase7/v1/PHASE_7_DOC.md:7` each declare `docs/design/v2.0-RC3/DESIGN.md`, and
`docs/MOVES.md:100` confirms *"Phases 3–8 §0 select RC3"*. Their latest rounds were nonetheless
adjudicated *"against the supplied v3 design override"* (`docs/phase5/reviews/PHASE_5_REVIEW_37.md:8`;
same at `docs/phase5/reviews/PHASE_5_REVIEW_38.md:6`) through the now-deleted `verification/targets/`
mechanism, without §G0.4's four-step adoption ever completing. **Ruling:** the discrepancy does not
affect this document, because every citation of those three docs here is to their *content* by
repo-relative path and line, which is revision-independent. It **does** affect a reader: a `§G`
coordinate quoted *inside* those documents is an RC3 coordinate and must not be resolved against v3.
Recorded so nobody makes that substitution. Requested upstream in §11.4.

**C-3 — `DebugService.isActive()` would be permanently false under Phase 7's OQ-3 default.**
Phase 1 defines it as *"false unless a debug context and the dev flag are both on"*
(`docs/phase1/v14/PHASE_1_DOC.md:3064`). Phase 7's OQ-3 fallback — its **default plan** — is *"Make
**no** context-flag change"* (`docs/phase7/v1/PHASE_7_DOC.md:2320`–`:2323`). Under both, no debug
context is ever created, so `isActive()` never returns true and the whole `DebugService` is dead in
every shipping configuration — an affordance §G4.5 reserves *"from day one"*
(`docs/design/v3/DESIGN.md:587`–`:589`). **Ruling:** the two documents are in genuine conflict and
KHR_debug's capability model settles it: object labels and debug groups require the extension or GL
4.3, **not** a debug context, as the reference's own gate proves —
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/debug/GLDebug.java:291]`.
This design proceeds on the corrected gate (`D-P14-14`) and raises R-P14→P1-1 (§5.6). If that request
is declined, **A5 is undeliverable as specified**, which is stated plainly rather than worked around.

**C-4 — §G5.2's Wave 5 versus §G5.1/§G5.3's literal dependency.** Full statement, ruling and
provenance in §3.5. Summary: `docs/design/v3/DESIGN.md:647` builds P13 and P14 in parallel while
`:626` makes P13 a hard dependency and `:628`–`:632` plus `:659`–`:663` require verified dependency
docs; the design's only sanctioned soft dependency is Phase 12's on Phase 7 (`:668`–`:671`), and
Phase 14 has no such clause. **Ruling: the gating invariant governs and the wave diagram yields**,
because §G5.3 item 2 subordinates the diagram itself (*"Waves are a schedule, not a barrier"*,
`:664`–`:667`). Requested upstream in §11.4.

**C-5 — Historical timing promise narrowed; JFR fallback reciprocally adopted (IR-26).**
The original P7 wording was *"Phase 7 exposes timing and resize-consumer seams but defines no
optimization policy."* Its §5 did not expose elapsed timing, and the resize protocol belonged
to P5. Current P7 §§1.2/5.5/11 narrow that promise to internal off-by-default debug aggregates.
P14 §5.4 accepts JFR stack attribution; optional adopted groups can segment separate counts runs.
A public read-only per-pass elapsed record remains a future owner-change request, not a baseline
dependency or a fabricated supplied API. No timing measurement was performed by this fix-up.

**C-6 — RESEARCH.md §6.2 has no DSA row, though the design now depends on DSA tiering.** The DSA
scope row is a REV1 addition sourced from PD §15 (`docs/design/v3/DESIGN.md:2533`–`:2535`); RESEARCH
§6.2's ten rows (`docs/research/v1/RESEARCH.md:769`–`:778`) contain nothing about direct state
access. **Ruling:** not a conflict — RESEARCH.md is silent, not contradictory, and §G0.1's precedence
rule is not engaged. But an evidence-bearing claim the design relies on should have a row in the
source of truth with a confidence tag, so §7.5 gives it ledger row **L-6** with its own experiment
and decision point, and §11.4 requests the RESEARCH addition. Recorded rather than left implicit.

### 11.3 Binding decisions honored, and the resolved condition

**RESEARCH.md `D-1`…`D-10` (`docs/research/v1/RESEARCH.md:95`–`:104`).** No decision in §11.1
contradicts any of them, and three are load-bearing here:

- **`D-9` — compatibility-profile GL baseline, no core-profile rewrite.** Every tier in this phase is
  an entry point available *within* compat, which is precisely §6.1's framing: *"LWJGL3's value =
  modern entry points/extensions/tooling within compat"* (`docs/research/v1/RESEARCH.md:758`). A5's
  debug-context tier is the only row that could touch context flags and it is gated on OQ-3's
  sanction, with *"preserves legacy fixed-function behavior"* already among Phase 7's own criteria.
- **`D-6` — the engine-core/loader-glue seam.** The whole mechanism of this phase lives in
  `mod.glue`; `:engine` receives only immutable values with no GL type, so Phase 1's C-1 holds. This
  is also why a Kirino backend swap (OQ-20) would take Phase 14 with it rather than being blocked by
  it.
- **`D-2` — shaders only; the written non-goals list.** `D-P14-16` makes the first non-goal a
  property of A7's instrument rather than a rule, and §4.6.2 step 4 makes vanilla's absolute numbers
  inexpressible in A6's output.

**The already-resolved condition, recorded with provenance and not re-litigated.** The commissioning
brief directs that A3's conditional status is settled and must not be reopened
(`docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md:87`–`:99`). Phase 6 recorded **`D-P6-1`: select
synchronous CPU `centerDepthSmooth`; return empty macro contribution**
(`docs/phase6/v1/PHASE_6_DOC.md:1678`), an explicit contract-visible **rejection** of PD §6.3's
GPU-side smoothing (`docs/phase6/v1/PHASE_6_DOC.md:468`), with the decision text stating in terms
*"Phase 14's PBO/fence item is **not obviated** and remains the sole async-readback modernization
ledger entry"* (`docs/phase6/v1/PHASE_6_DOC.md:966`–`:967`). All three citations were verified at the
line by this session. **Consequence: A3 stands in full** — the one-frame latency on an
already-smoothed value, the synchronous path retained as fallback and configuration, and the
imperceptibility verification specified in §4.3.6. Full statement in §3.4. No `D-P14-k` reopens it;
`D-P14-8` builds on it.

### 11.4 Requested upstream changes

This document may not modify RESEARCH.md, any `DESIGN.md` revision, `PINTONIUM_DESIGN.md`,
`OCULUS_DESIGN.md`, or another phase's doc (`docs/design/v3/DESIGN.md:285`–`:290`). All of the
following are proposals.

**To `docs/design/v3/DESIGN.md` (or its successor candidate):**

1. **Resolve the Wave 5 / hard-dependency contradiction (C-4).** Either add an explicit
   soft-dependency clause to Phase 14's §G5.1 row (`:626`) bounding it to Phase 13's texture-estate
   lifecycle, in the shape §G5.3 item 3 already uses for Phase 12 (`:668`–`:671`), or move P14 out of
   Wave 5 (`:647`) into a wave that follows P13's verification. The parallel-wave line and the
   literal-dependency line should not both stand unqualified.
2. **Correct Phase 14's context budget.** *"≈ 34k tokens mandatory reading"* (`:2586`) is
   substantially wrong: the three *existing* dependency documents alone are ~130k tokens (2 511 +
   1 829 + 2 486 lines), before Part I (~50k), RESEARCH.md's seven required sections, PD §15/§6.3, or
   the absent Phase 13 doc. The realistic figure is ≥200k. A budget this far off invites a session to
   under-read its dependencies.
3. **Qualify the impl gate's third condition** (`:2583`–`:2584`). *"Pack-switch stall measurably
   reduced vs the synchronous baseline"* is achievable only through the async compile row, which is
   OQ-15-gated and therefore may legitimately not exist at v0.5. Requested: state the condition as
   contingent on OQ-15's outcome, so a passing v0.5 is not blocked by an open OQ the design itself
   left open. §9.2 records how this document reads the gate in the meantime.
4. **Note the `DebugService` gate in §G4.5.** §G4.5 reserves *"KHR_debug labels/groups in dev"*
   (`:587`–`:589`) without stating whether a debug context is required. Adding "no debug context
   required" would prevent the C-3 class of divergence recurring.

**To `docs/research/v1/RESEARCH.md`:**

5. **Add a §6.2 row for DSA (C-6).** Direct state access is a REV1-added design dependency with real
   deployed evidence and no row in the source of truth. Proposed row: *"GL 4.5 / `ARB_direct_state_access`
   object creation and editing | bind-to-edit round-trips | behavior-invisible; tiered with a
   bind-to-edit fallback `[V:observed — Pintonium]`"*. §7.5's L-6 is its ledger entry meanwhile.
6. **OQ-22's status column** is where §7.5's thirteen row outcomes are written back by the
   implementation effort per §G4.4 — noted here so the write-back is not lost.

**To `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`:**

7. **Refine §15's PBO bullet.** *"No PBO/async readback anywhere"* (`:748`) is correct about
   readback but reads as though the tree has no asynchronous GPU-transfer machinery at all. It does:
   `glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0)` at
   `reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/device/GLRenderDevice.java:233`,
   with a non-blocking `glGetSynci(GL_SYNC_STATUS)` poll at
   `reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/sync/GlFence.java:23`,
   in the chunk device on the 1.12.2 compat context. That is direct evidence that **fence sync
   works** on this platform — the availability half of Phase 14's async-readback claim — and it is
   currently invisible to a reader of §15.

**To `docs/phase5/v1/PHASE_5_DOC.md` (its own fix-up session, not this document):**

8. **Historical status request superseded.** The initial-build request to clear P5's stale
   trailer concerned pre-cutover bytes (§0.3). Do not apply it to today's changed/unverified
   binding/lifecycle contract or claim its older PASS certifies it.
9. **Optional simplification** — hand-off H-P14→P5-1, §11.5.
   **R-P14→P5-2** (§5.2) requests the optional internal batch boundary; disabled until granted.

**To `docs/phase6/v1/PHASE_6_DOC.md`:**

10. **Historical status request superseded.** The initial-build P6 trailer correction (§0.3)
    does not certify the changed resolver/retirement contract. Fresh current-owner verification
    remains required; there is no request to relabel current bytes PASS.
11. **R-P14→P6-1** (§5.3) — contract the center-depth sample's age. The one request blocking A3.

**To `docs/phase7/v1/PHASE_7_DOC.md`:**

12. **R-P14→P7-1** remains optional/ungranted and paired with R-P14→P4-1. **R-P14→P7-2** groups remain optional; timing disposition is the reciprocally adopted narrowed promise/JFR fallback (§5.4/C-5), not a pending baseline blocker.

**To `docs/phase1/v14/PHASE_1_DOC.md`:**

13. **R-P14→P1-1** through **R-P14→P1-4** (§5.6). R-P14→P1-1 is the one whose refusal makes a scope
    row undeliverable.

**To `docs/phase4/v1/PHASE_4_DOC.md`:**

14. **R-P14→P4-1** (§5.9) is routed to the compiler owner: opaque prepared batch, worker-only
    shader operations, render-thread link/candidate minting, deterministic failures and cancellation/
    late-result disposal. P4/P7 must publish and verify their exact seam before async is enabled.
    This remains a request, not owner adoption or an OQ-15 result.

**To `docs/phase11/v1/PHASE_11_DOC.md`:**

15. **IR-15 recipient acceptance:** §§5.8/7.5 L-11/10.2 S-22-6 now adopt P11 method/metrics.
    P11 retains semantics/SPI and records the reciprocal handoff; future measurements and any
    conditional candidate require source-free ledger/P11 addendum, not an unmeasured compiler.

**To `docs/phase13/v1/PHASE_13_DOC.md`:**

16. **IR-23 conversion/lifetime adoption:** §5.5 consumes the existing exact value and deferred
    deletion. R-P14→P13-1 remains stageable-upload proposal; R-P14→P13-2's parameter value is
    supplied, but P1's full sampler-state conversion remains distinct and ungranted.

**To `docs/MOVES.md`:** no change requested by this phase; a Phase 14 row is added by whoever records
this document's adoption, per its own rules.

### 11.5 Items handed to later phases, to G8, and to the implementation effort

| ID | Hand-off | To |
|---|---|---|
| **H-P14→P5-1** | Once `SamplerTier != NONE`, `generateShadowMipmaps` need not mutate a texture's min filter: `glGenerateMipmap` does not require a mipmap min filter, and the sampling filter now lives in the sampler. `MIPMAP_FILTER_RESTORE_FAILURE`, its `Neutralized` result variant and its containment path (`docs/phase5/v1/PHASE_5_DOC.md:1728`–`:1739`) would become unreachable in that mode. **Phase 5 owns that algorithm; this is offered, not applied**, and the `NONE` fallback leaves Phase 5's path exactly as written | Phase 5 |
| **H-P14→P13-1** | §5.5 now consumes current parameter/publication/lifetime contracts. Reciprocal P13 §5.5 records exact conversion and sampler-0 fallback; staged upload remains R-P14→P13-1 ungranted, not an absent-interface claim | Phase 13 |
| **H-P14→ALL-1** | Any allocation site found in a frame path by §4.6.2 is a **finding against the owning phase**, routed by package attribution (`engine.buffers` → 5, `engine.uniforms` → 6, `engine.frame` → 7, …). Phase 14 measures; it does not edit another phase's code | the owning phase |
| **H-P14→ALL-2** | Any redundant-state candidate found by §4.7.2 in a sibling's call sequence is likewise that phase's, after passing §4.6.3's four-part test | the owning phase |
| **H-P14→G8-1** | PD §15's evidence that compute, SSBOs, image load-store and indirect dispatch all run pack-exercised on the 1.12.2 compat context (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:741`–`:744`) is carried as ledger row **L-12** — *"the strongest available evidence that G8/S2 is feasible on Cleanroom"*. Not a Phase 14 work item | G8/S2 (`docs/design/v3/DESIGN.md:788`–`:793`) |
| **H-P14→G8-2** | The `GlModernizationPlan` shape is deliberately extensible: G8/S2's compute and SSBO capability gating is the same kind of init-time, profile-derived, per-row tier decision and should reuse it rather than inventing a parallel mechanism | G8/S2 |
| **H-P14→IMPL-1** | `docs/decisions/OQ-15_ASYNC_COMPILE.md` is owed by the spike, in the shape Phase 7 uses for OQ-3 (`docs/phase7/v1/PHASE_7_DOC.md:2312`–`:2313`), and its per-family verdict table becomes `D-P14-12`'s shipped allowlist data | implementation effort |
| **H-P14→IMPL-2** | §7.5's thirteen ledger outcomes are written back into RESEARCH.md §11's OQ-22 status column, with an addendum note added to this document, per §G4.4 (`docs/design/v3/DESIGN.md:578`–`:580`) | implementation effort |
| **H-P14→P2-1** | Scene `S-CD-1` — a scripted near↔far camera path, 300 frames, for §4.3.6 family F3 — is proposed to Phase 2 as a scene. Phase 14 authors no scene | Phase 2 |
| **H-P14→REVIEW-1** | Integration review checks current P5/P6/P7/P13 consumption plus P11 metric-method acceptance and P4 compiler request; separates adopted baseline from ungranted optional extensions and historical verdicts | integration review |

### 11.6 Known gaps in this document

Stated so a verify session does not have to discover them:

1. **Complete P1 `TextureParameters`/sampler conversion remains ungranted** (R-P14→P1-3).
   P13's exact min/mag/wrap value does not fill unspecified compare/LOD/border fields. Sampler 0
   and existing texture parameters are mandatory until complete conversion is available.
2. **P13 stageable upload does not exist as a granted API.** Its synchronous build, exact effective
   parameters, source pairing and deferred retirement do exist and are consumed in §5.5.
   Compile-only optimization requires its independent P4/P7 grants; atlas upload stays synchronous.
3. **Current coordinated P4/P5/P6/P7/P13 contracts remain unverified.** Their actual binding,
   lifetime and ten-step ordering details matter, not merely the existence of interface names.
   R-P14→P4-1/P7-1/P13-1 and P5-2 are still optional ungranted extensions.
4. **A2's driver-behavior claim is untestable headlessly.** No test we can write proves a given
   driver's DSA path is correct; §9.2's per-tier T1 run and the runtime `FORCE_OFF` are the whole
   mitigation, and that is stated rather than implied.
5. **§10.1's K3 threshold (20% of the synchronous long-frame count) is a target, not a derived
   constant.** Unlike §4.3.6's criterion C2, which is analytic, K3 is a judgement. The partial-pass
   provision exists so the number does not silently become a rule.
6. **No RESEARCH.md appendix was read** (§0.2). §3's map is built from §4.8, §6.2, §6.3 and the
   dependency docs' own conformance rows. If a verify session finds an appendix row this phase can
   perturb that §3 does not carry, that is a real omission and not a scoping decision.

---

## 12. Implementation checklist

Ordered and independently actionable, each with a milestone tag and a test hook. The order is a
dependency order, not a preference: the plan value precedes every consumer; A5's groups precede A7's
audit because they are its segmentation keys; the synchronous fallbacks precede the paths they back.

| # | Work item | Milestone | Test hook |
|---:|---|---|---|
| 1 | Resolve R-P14→P1-2 (package grant) before any file is placed | v0.5 | Phase 1's seam-enforcement tests (C-1…C-4) |
| 2 | Add the pure value types to `engine.gl`: `SamplerKey`, `SamplerTier`, `DsaTier`, `DebugTier`, `AsyncCompileTier`, `AsyncReadbackTier`, `GlModernizationPolicy`, `GlModernizationPlan` | v0.5 | compile-time C-1 check: zero LWJGL/Minecraft imports |
| 3 | Implement `GlModernizationPlan.derive` as a pure function of `GLCapabilityProfile` + policy, with a rationale string per row | v0.5 | `ModernizationPlanDerivationTest` over ≥7 profile fixtures |
| 4 | Wire `GlModernizationPolicy` into `mod.core`'s config with five `AUTO`-defaulted rows; log the derived plan once at bring-up | v0.5 | manual: the log line names every tier and why |
| 5 | Implement `DsaStrategy` `BIND_TO_EDIT` first — the existing behavior, extracted behind the interface with no functional change | v0.5 | `BindingNeutralityTest`; the recorded log must be unchanged from before extraction |
| 6 | Add `DsaStrategy` `ARB` and `CORE_45`; install by tier at bring-up | v0.5 | `BindingNeutralityTest` across all three tiers on one call script |
| 7 | Resolve R-P14→P1-3 (`TextureParameters` fields); implement `SamplerKey.of` and the sampler/texture state split | v0.5 | `SamplerKeyDerivationTest`, `SamplerStateSplitTest` |
| 8 | Implement sampler interning with actual source/parameter/publication identity, all eight resize reasons and live/retiring reference accounting (§4.1.3) | v0.5 | `SamplerCacheLifecycleTest` |
| 9 | Extend only P5's physical bind path; per-unit samplers after complete conversion, `MULTI_BIND` only after R-P14→P5-2/P1 grant | v0.5 | zero-bind rejected/degraded paths, Bound-only transfer, no stale sampler |
| 10 | **Implement the sampler clear on every vanilla-return path, in the backend's own `finally`** — ships **with** item 9, never after it | v0.5 | `SamplerLeakTest` over all exit kinds and abort reasons |
| 11 | Implement the whole-estate fallback to `SamplerTier.NONE` on any sampler GL error | v0.5 | scripted `ScriptedResponses.glError` demotion tests (§8.2) |
| 12 | Resolve R-P14→P1-1 (`isActive()` gate); implement `KhrDebugBackend` labels with `GL_MAX_LABEL_LENGTH` clamping | v0.5 | `DebugLabelCoverageTest` |
| 13 | Implement balance-safe groups: depth counter, no-op underflow with one diagnostic, overflow virtual depth, frame-boundary drain | v0.5 | `DebugGroupBalanceTest` across `NORMAL`/`EARLY_RETURN`/`THROWN` |
| 14 | Implement the no-op `DebugTier.NONE` backend and make it the default | v0.5 | `DebugInactiveIsFreeTest` |
| 15 | Land R-P14→P7-2 part 1 (group call sites in Phase 7) | v0.5 | recorded-log frame/pass group nesting |
| 16 | Add `GL_DEBUG_OUTPUT` + `GL_DEBUG_OUTPUT_SYNCHRONOUS` + `glDebugMessageCallback` routing to `schmaloogium.gl` | v0.5 | manual: one injected GL error appears with a usable stack |
| 17 | Run ledger row **L-5**: one RenderDoc/Nsight capture with `glLabels` set | v0.5 | groups and labels visible; L-5 closes |
| 18 | Keep current synchronous P4 compile; migrate to proposed `CompileExecutor`/Inline only after R-P14→P4-1 and R-P14→P7-1 adoption | optional after grants | compiler ownership/cancellation/publication cases |
| 19 | Implement synchronous CenterDepthSource fallback at P7 transaction step 2 | v0.5 | current P6 SPI/order remains unchanged |
| 20 | Land R-P14→P6-1 (contract the sample age) | v0.5 | Phase 6's fresh verify round |
| 21 | Implement the PBO+fence ring: non-blocking poll, 4-byte map, ring discard on identity change, resize-consumer discard | v0.5 | `CenterDepthRingTest`, `CenterDepthInvalidationTest` |
| 22 | Implement the trace-comparison judgement (criteria C1–C4) as a headless analysis over two recorded traces | v0.5 | `CenterDepthTraceComparisonTest` |
| 23 | Propose scene `S-CD-1` to Phase 2; run §4.3.6's comparison across families F1–F4 | v0.5 | criteria C1–C4; **L-3 closes and decides A3's default** |
| 24 | Implement §4.7.2's audit classifier and its three validity gates over a `GLCallLog` | v0.5 | `AuditClassifierTest`, `AuditScopeFilterTest` |
| 25 | Run the audit **before and after** item 9 on one classic pack | v0.5 | **L-2 closes**; §4.7.3's predictions confirmed or refuted |
| 26 | Run §4.6.2's allocation profile on the Phase 2 scenes across the three configurations | v0.5 | **L-8 closes**; violations become H-P14→ALL-1 hand-offs |
| 27 | Run L-6 per-tier comparison and L-1/L-7/L-9 spot-checks; run L-11 exact P11 method before considering any compiled evaluator | v0.5 | source-free measured ledger decisions; no untriggered compiler |
| 28 | Execute S-22-1 and S-22-4: freeze the row set, then write every outcome back to RESEARCH.md §11 and add the addendum note | v0.5 | §9.2 condition 4 |
| 29 | Verify §9.2's gate: full classic matrix at T3 jointly with Phase 13; no T1 regression under any default-enabled tier | v0.5 | Phase 2 `RUN-T1-REGRESS`, `RUN-T0`, T3 runs |
| 30 | Land exact P4 prepare/worker/link R-P14→P4-1 and matching P7 step-3 resumability R-P14→P7-1 | post-v0.5, optional | fresh owner verification and cancellation/late-result cases |
| 31 | Implement `GlWorkerContext` and `WorkerCompileExecutor` per §4.4.2, with the six safety rules and the watchdog | post-v0.5 | `CompileFallbackLadderTest`, `DriverPolicyTest` |
| 32 | Run **OQ-15**'s spike (§10.1) on ≥2 driver families; write `docs/decisions/OQ-15_ASYNC_COMPILE.md` | post-v0.5 | criteria K1–K5; **L-4 closes**; the verdict table becomes the allowlist |
| 33 | Land R-P14→P13-1; add the async `_n`/`_s` atlas upload to the worker | post-v0.5 | atlas pixel equality vs a synchronous upload |
| 34 | Land R-P14→P1-4(b); add `-Dschmaloogium.debug.glContext` and the `GLFW_OPENGL_DEBUG_CONTEXT` request, gated on OQ-3's outcome | post-v0.5 | manual: debug-context message volume vs the non-debug tier |
| 35 | Reconcile any subsequent owner corrections to current §5 contracts and verification gates; never promote prior PASS to certify changed bytes | as owners change | fresh affected-owner review |

Items 1–29 constitute v0.5. Items 30–34 are the quality-of-life half of *"v0.5 + quality-of-life"*
(`docs/design/v3/DESIGN.md:2516`) and are individually droppable: each is gated on a request or a
spike, and each fails closed to a path that already ships.

---

*End of `PHASE_14_DOC.md`. v1 remains governed by `docs/design/v3/DESIGN.md`; the IR-15/23/26/27
documentation fix-up changes cross-phase consumption and remains **unverified**. Initial-build
departures and quotations are preserved as history in §0.3/§11.2, not present-day absence claims.
Current P5 binding/P6 resolver/P7 transaction/P13 lifetime and P11 measurement handoffs are adopted
as documentation contracts; optional extensions, authority changes and implementation experiments
remain gated. No implementation, test/validation result, OQ closure or fresh PASS is claimed.*
