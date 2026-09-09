# Schmaloogium — Phase 14: GL modernization & performance — Architecture

## 0. Header

- **Phase:** 14 — GL modernization & performance
- **Milestone:** v0.5 + quality-of-life
- **Module/package:** `:engine` (`com.schmaloogium.engine.gl` — pure value additions only) and `:mod`
  (`com.schmaloogium.mod.glue` — the LWJGL3 backend where every behavior in this phase lives)
- **Declared dependencies:** Phases 5, 6, 7, 13 (`docs/design/v3/DESIGN.md:626`)
- **Assigned open questions:** OQ-15, OQ-22 (`docs/design/v3/DESIGN.md:626`, `:878`, `:885`)
- **Governing design:** `docs/design/v3/DESIGN.md`
- **Design status:** verified per §G1.3 (attempt-11 wave close-out, review R8, 2026-09-08); no implementation claim
- **Date:** 2026-09-07 (initial build: 2026-08-08; last revised 2026-09-08 — §§0.9–0.11 fix-up)
 
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
| `docs/phase5/v1/PHASE_5_DOC.md` | §0 header, §1, §5, §6, §7, §9 in full; §2/§3/§4 selectively — colortex filter/wrap policy (`:1431`–`:1432`), shadow-mipmap filter mutation (`:2245`–`:2255`), resource projection records (`:526`–`:545`) | dependency contract; buffer estate, flip textures, per-frame filter state, resize lifecycle |
| `docs/phase6/v1/PHASE_6_DOC.md` | §0, §1, §3, §5, §6, §7, §9, §11 in full; §4 selectively — the SPI records (`:716`–`:752`), frame-begin ordering (`:577`–`:590`), the `centerDepthSmooth` decision (`:1143`–`:1185`), the fixed sampler maps (`:1187`–`:1272`) | dependency contract; `CenterDepthSource`, `D-P6-1`, sampler participant, program-switch upload path |
| `docs/phase7/v1/PHASE_7_DOC.md` | §0 head, §1, §5.1, §6, §7, §9 in full; §4 selectively — pipeline build steps (`:896`–`:993`), reload lifecycle (`:1022`–`:1055`); §10.1 (OQ-3) | dependency contract; frame ordering, pack-switch/reload path, debug flags, the context-creation fallback my §4.5 depends on |
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
   scope-out row assigning this phase its work (`:1708`), the `DebugService`/`GLDevice`/
   `TextureService`/`StateService` declarations (`:2880`–`:3070`), `GLCapabilityProfile`
   (`:2636`–`:2670`), the debug-flag namespace §4.9.3 (`:3731`–`:3762`), the §5 rows for
   `GLCapabilityProfile` (`:5492`) and the GL-error surface (`:5490`), and the §7 thread-ownership
   row that explicitly invites a Phase 14 request (`:5608`). Nothing else in that 7 023-line
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
   `:91`). **At gate time `docs/phase13/reviews/` contained only `.gitkeep` — zero review rounds then; PHASE_13_REVIEW_1–10 have since landed.** The file is
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
   `docs/phase7/v1/PHASE_7_DOC.md:8`), and `docs/MOVES.md:101` confirms *"Phases 3–8 §0 select
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

### 0.5 Settled foundation receiver grants (2026-09-08)

D-P14-23 adopts P1 §§4.7.7–4.7.8/5/D-P1-52/54: complete owned-texture values/conversion,
debug activity without a debug-context prerequisite, and exact pure-value/helper package homes.
R-P14→P1-1/2/3 are fulfilled/adopted, unverified. Historical C-3 remains below with this
explicit superseding disposition. Optional post-v0.5 R-P14→P1-4 and all separate owner,
measurement/equivalence and fresh-review gates remain; no GL runtime proof is claimed.

### 0.6 Whole-owner correction receipt (2026-09-08)

C14-1…7 of the complete Phase14 Review1 are addressed by D-P14-26…30 and active
§§4–12 contracts, not implementation evidence. The coordinated schema22 grant is received
with current-constant admission; older numeric receipts and reference observations remain
historical. Governing header/design/research and P4's current domain/P6 recurrence were read.
No validation, runtime experiment, build, test, formatter or linter was run. Fresh whole-owner
review and reciprocal integration remain required; no implementation clearance is claimed.

### 0.7 Attempt-5 R2 correction receipt (2026-09-08)

Review2 corrections 1–2 are addressed by D-P14-31/32 and §§4/5/6/7/8/10/11/12:
complete authenticated object baseline alongside sampler state, and two-direction upload
ordering with separately accounted readiness/completion syncs. D-P14-33 receives schema23;
§0.6 and older numeric receipts remain historical. This session read the complete owner and
Review2, AGENTS.md, selected governing v3 threading/GL/scope rules and Research confidence,
mission, Adapt, GL/JVM, milestone and OQ sections. Original reports and source-confidence
receipts are preserved. No validation command or runtime experiment was run. These changed
documentation contracts require reciprocal receipts and fresh review; no API adoption,
implementation clearance, runtime proof or PASS is claimed.

### 0.8 Attempt-6 R3 correction receipt (2026-09-08)

Review3 C14-1…4 are addressed by D-P14-35…38 in the live A3/A5 algorithms,
incorporated §5 contracts, failure cases, §8 evidence plan and §12 checklist. Four-byte
readback state/failure safety, encoded label limits, virtual-aware boundary drain and
completion-based warm-up are owner-local mechanism corrections, not new API grants.
Original review bodies and historical confidence remain intact. No validation command or
runtime experiment was run; fresh review and Main integration remain required.

### 0.9 Attempt-7 R4 correction receipt (2026-09-08)

Review4 C1…4 are addressed by D-P14-39…42 in the live consumption rows and §2/§5/§9
text: the opaque P4 `RegistryFingerprint/profile-selection-v3` identity receipt
(superseding positional-route-v2 in D-P14-29), the native-evidence separation of
targetless logical texture issuance from exact-target materialization/labelling and
lifetime-ending deletion with binding zeroing, and the honestly deferred pack-switch
stall criterion. These are owner-local consumption/evidence corrections, not new API
grants. The review body and its verdict remain historical evidence; no validation
command or runtime experiment was run; fresh whole-document review remained required.

### 0.10 Attempt-8 R5 correction receipt (2026-09-08)

Review5 C1…2 are addressed by D-P14-43/44: the three-argument context-bound P13 lease
with its closed rejection reasons and base-serial currentness (the two-argument form
no longer exists), and `GlModernizationPlan` publishing exactly five tiers with
rationale ("six tiers" corrected in §2.1/§5.1). Owner-local corrections only; no
optional extension was granted and no native or parity evidence is claimed. The review
body and verdict remain historical evidence; no validation command or runtime
experiment was run; fresh whole-document review remained required.

### 0.11 Attempt-9 R6 correction fix-up (2026-09-08)

Review6 C1…4 are applied: every dependency/RESEARCH/DESIGN line pin re-derived against
the 2026-09-08 settled bytes of P1/P5/P6/P7/P13/RESEARCH/v3 (quotes retained; §3.4's
verification sentence superseded by this dated re-verification), §5.4's
`FrameAbortReason` enumeration restated as the current five values at
`docs/phase7/v1/PHASE_7_DOC.md:2631`–`:2633`, §§0.9–0.10 above record the missing R4/R5
receipts, and §5.1's P2 run-manifest cell now states proposed-only consumption (P2's
current bytes define no such manifest field and record no granted request). No §5
contract shape, grant or receiver obligation changed; this fix-up is unverified and a
fresh whole-document review is required before implementation consumption.

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
  shadow estate, and the unit→texture binding table (`docs/phase5/v1/PHASE_5_DOC.md:426`–`:443`).
  Phase 5 hands this phase exactly *"sampler objects, DSA modernization, asynchronous transfers,
  persistent staging, and performance tuning"* (`docs/phase5/v1/PHASE_5_DOC.md:473`–`:475`).
- **Owned by Phase 6:** the synchronous center-depth design A3 replaces, the Appendix D catalog, the
  shared-resolver sampler-integer uploads, cadences, smoothing math, and the `CenterDepthSource` seam. Phase 6
  hands this phase *"the optional PBO/fence replacement for synchronous center-depth readback. This
  document deliberately leaves that ledger item live"* (`docs/phase6/v1/PHASE_6_DOC.md:404`–`:405`).
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
  (`docs/phase1/v14/PHASE_1_DOC.md:1726`) and stages the `DebugService` implementation at
  *"`v0.5` / Phase 14"* (`docs/phase1/v14/PHASE_1_DOC.md:1906`, `:5894`).
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
LWJGL3 implementation lives in `mod.glue` — §G4.6: *"All engine GL goes through the `engine.gl` facade —
no direct LWJGL calls outside `mod.glue`'s facade implementation"* (`docs/design/v3/DESIGN.md:488`, `:593`–`:594`). Phase 14 is unusual among phases in that **almost all of
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
           GlModernizationPlan   the five tiers actually selected, + why (diagnostic strings)

:mod     com.schmaloogium.mod.glue
           Lwjgl3GLDevice        existing Phase-1-owned device; delegates to helpers below
         com.schmaloogium.mod.glue.gl              (P1 D-P1-54 granted helper home)
           SamplerCache          TextureHandle -> interned sampler; per-unit bind cache
           DsaStrategy           CORE_45 / ARB / BIND_TO_EDIT object-creation strategy
           KhrDebugBackend       Phase 1's DebugService, balance-safe by construction
           CenterDepthReadback   Phase 6's SPI; age/behavior grants still required
           GlWorkerContext       single shared GLFW compat context, post-v0.5/spike-gated
           CompileExecutor       proposed Inline | Worker seam; requires P4/P7 adoption

:conformance                                       (Phase 2's; Phase 14 adds runs, not machinery)
           the A3 imperceptibility comparison, the A6 allocation profile, the A7 audit
```

Nothing enters `mod.mixin`. P1 D-P1-54 grants the exact eight §2.1 pure values in `engine.gl`
and helper home above; this does not approve optional operations. The L-11 expression candidate, if evidence triggers
it, belongs inside Phase 11's existing implementation-private SPI under that owner's semantics;
no placement grant or compiled backend is inferred merely from this measurement responsibility.

### 2.2 The two structural ideas

**Idea 1 — the modernization plan is a value, chosen once, at init.** A single
`GlModernizationPlan` is derived from the live `GLCapabilityProfile` and the user policy, before the
first frame, and never changes for the lifetime of the GL context. Every row reads its tier from
that plan. This makes the whole phase testable headlessly (the derivation is a pure function of a
serializable profile — Phase 1 already ships `GLCapabilityProfile` fixtures,
`docs/phase1/v14/PHASE_1_DOC.md:6892`) and makes "which path ran" a single diagnostic line rather
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
No standalone native sampler/DSA service is needed; coordinated unit normalization is additive:

| Candidate addition | Disposition |
|---|---|
| `SamplerService` (create/parameterize/bind/delete sampler objects) | **Not added.** A1 is served entirely inside the LWJGL3 backend (§4.1): a sampler is a pure function of the `TextureParameters` Phase 5 already supplies, so `TextureService.bindToUnit` can bind the interned sampler alongside the texture with no caller change. Recorded as a *reserved future additive extension* should a phase ever need to bind a sampler independently of a texture; none does today. |
| Unit-set normalization | Adopted coordinated `TextureService.prepareUnitBindings(int occupiedUnitMask)` after P5 preflight and before object binds; P1 useFixedFunction supplies the separate all-unit clearing dispatch (§5.10). Not a sampler-batching grant. |
| A DSA verb set | **Not added.** A2 is by definition an internal object-creation strategy (`docs/design/v3/DESIGN.md:2534`–`:2535`). |
| An async-readback verb | **Not added.** A3 implements Phase 6's existing `CenterDepthSource` SPI, which Phase 6 already declares as *"loader-neutral sampling SPI with no Minecraft or GL-name types"* implemented from `mod.glue` (`docs/phase6/v1/PHASE_6_DOC.md:1825`). `FramebufferService.readDepthPixel` (`docs/phase1/v14/PHASE_1_DOC.md:3422`) remains the fallback path, unchanged. |
| An async-compile verb | **Not added** to `engine.gl`. `CompileExecutor` is a proposed policy seam, not an existing Phase 4 API; R-P14→P4-1 and R-P14→P7-1 must land before any caller migration. Current `ProgramRegistryCompiler.compile(RegistryBuildRequest)` remains synchronous. |
| KHR_debug verbs | **Already present.** Phase 1 ships `DebugService` at v0.1 precisely so call sites can label immediately (`docs/phase1/v14/PHASE_1_DOC.md:3490`–`:3495`, `:1906`); Phase 14 supplies the implementation. |
| The pure value types in §2.1 | **Package grant adopted/unverified.** Exact eight P1 D-P1-54 pure values in engine.gl; existing device stays mod.glue, backend helpers use mod.glue.gl |

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

resize (Phase 5 BufferResizeConsumer, PHASE_5_DOC.md:2738 — "Phases 13 and 14")
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
| **Ping-pong buffer + flip semantics, buffer clear rules** — packs depend on exact flip behavior | A1 and A2 change texture *parameterization* and *object creation*; neither reads or writes flip state, side selection, or clear policy, all of which stay in `engine.buffers` | `[V:doc]` `docs/research/v1/RESEARCH.md:627`; Phase 5 ownership at `docs/phase5/v1/PHASE_5_DOC.md:430`–`:431`; **D-P14-1** |
| **Filter and wrap state of every colortex** — `CLAMP_TO_EDGE` S/T; NEAREST for integer formats, LINEAR otherwise | A1 derives `SamplerKey` **from Phase 5's `TextureParameters` value**, never independently; the equivalence test in §8.1 asserts the bound sampler's state equals the texture's configured state for every texture in the estate | `[V:doc]` Phase 5's policy at `docs/phase5/v1/PHASE_5_DOC.md:1439`–`:1440`; **D-P14-1** |
| **Shadow filter, mipmap, and hardware-PCF compare mode** | `SamplerKey` carries `compareMode`; derived from Phase 5's `ShadowTextureResource(hardwareFiltering, mipmap, nearest)` (`docs/phase5/v1/PHASE_5_DOC.md:631`–`:632`) | `[V:doc]` `docs/research/v1/RESEARCH.md:524`–`:526` via `docs/phase5/v1/PHASE_5_DOC.md:1205`; §4.1.3 |
| **`centerDepthSmooth` — declared-trigger readback, App D `float`, tick-domain smoothing** | A3 replaces only *how the depth pixel arrives*. The EMA, the half-life, the tick domain, the declaration trigger, and the uploaded value's type all stay in Phase 6. The added latency is **contracted**, not hidden (§4.3.6, R-P14→P6-1) | `[V:observed]` `docs/research/v1/RESEARCH.md:642`, `:773`; Phase 6's design at `docs/phase6/v1/PHASE_6_DOC.md:1145`–`:1153`; **D-P14-8** |
| **Frame-begin sampling completes before any buffer resize or clear** | A3 runs *inside* `CenterDepthSource.readCenter`, which Phase 6 calls at step 6 of `beginFrame` (`docs/phase6/v1/PHASE_6_DOC.md:1065`–`:1066`); A3 issues no resize and no clear and adds no frame moment | governing REV1 constraint at `docs/design/v3/DESIGN.md:526`–`:528`; exported ordering contract at `docs/phase6/v1/PHASE_6_DOC.md:1818` |
| **Everything refreshes on program switch; matrices always upload** | A1–A7 touch no uniform upload and no barrier participant. A5's debug groups are gated `isActive()` and issue no GL when inactive | `[V:observed]` `docs/research/v1/RESEARCH.md:1380`–`:1381` via `docs/phase6/v1/PHASE_6_DOC.md:604`–`:605`; **D-P14-15** excludes contract cadence from optimization |
| **Stage semantics, program set, backup chains** | A4 moves `glCompileShader` to another thread of the same share group. It does not choose, order, name, resolve, or fall back between programs — all Phase 4's | `[V:doc]` `docs/research/v1/RESEARCH.md:626`; **D-P14-11** keeps link, uniform location and the `Program.use()` barrier on the render thread |
| **`_n`/`_s` companion atlases; missing sprites → `0xFF7F7FFF` / zero-specular** | A4's async upload changes *when bytes reach the driver*, never the byte values or the defaults, which are Phase 13's | `[V:doc]` `docs/research/v1/RESEARCH.md:638`; spec at `docs/design/v3/DESIGN.md:2451`–`:2454`; **spec-derived — R-P14→P13-1** |
| **Custom-texture `.mcmeta` blur/clamp and owner-approved filter/wrap semantics** | A1 consumes P13 TextureParameterSpec/fingerprint through P1 D-P1-52's granted complete conversion and §5.5 lifetime. U1 is separately corrected by its approved decision; unknown foreign/incomplete state retains sampler0, never guessed parameters | P13 §§4.3.5/5.1/5.5; D-P14-19/23, fulfilled R-P14→P1-3; optional execution/equivalence gates remain |
| **`GL_QUADS` stays available; compat profile is mandatory `[D-9]`** | A5's debug-context dev mode adopts a context-flag change **only** if OQ-3 sanctions one; the default plan is *"Make **no** context-flag change"* (`docs/phase7/v1/PHASE_7_DOC.md:4470`). A2/A1 request no profile change and no core-profile entry point that is absent from compat | `[D-9]` `docs/research/v1/RESEARCH.md:103`, `:758`; **D-P14-14** |
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
| **Dynamic per-program texture-unit allocation** | **Rejected — pre-decided.** §G11.4 lists it among the pre-decided rejections: *"dynamic per-program texture-unit allocation (ours: fixed App B.3 map incl. depthtex1 at unit 11)"*; **D-P14-3** | `docs/design/v3/DESIGN.md:953`–`:954`; PD §18 row at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:808`; Phase 6 rejected the same at `docs/phase6/v1/PHASE_6_DOC.md:635` |
| **KHR_debug object labels + per-phase push/pop groups behind a flag** | **Pattern adopted, wiring rejected** (§4.5), as the specification instructs at `docs/design/v3/DESIGN.md:2548`–`:2550`; **D-P14-13**, **D-P14-14** | PD §15 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:745`–`:747`. `[V:observed — Pintonium .../gl/debug/GLDebug.java:291]` — `if (Boolean.getBoolean("celeritas.enableGLDebug") && (GL.getCapabilities().GL_KHR_debug \|\| GL.getCapabilities().OpenGL43))`; label/push/pop at `:334`–`:355`; the stack-depth and label-length probes at `:330`–`:331` |
| **GPU-side `centerDepthSmooth` (1×1 R32F ping-pong pass)** | **Not this phase's to adopt, and already rejected upstream.** Phase 6 recorded `D-P6-1` selecting the synchronous CPU readback; §3.4 records the resolution and its consequence for A3 | PD §6.3 at `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:312`–`:331`; rejection at `docs/phase6/v1/PHASE_6_DOC.md:638`, `:1184`–`:1187` |
| **PBO / async readback** | **No reference exists.** PD §15: *"**No PBO/async readback anywhere** — Schmaloogium Phase 14's async center-depth design has no reference here"* (`:748`–`:749`). Verified: the single `glReadPixels` in the shader tree is synchronous (`.../IrisRenderSystem.java:190`). A3 is designed from RESEARCH §6.2 alone | `[V:observed — Pintonium .../IrisRenderSystem.java:190]`; `docs/research/v1/RESEARCH.md:773` |
| **Fence-sync objects on the 1.12.2 compat context** | **Adopted, with a provenance refinement to PD §15.** PD's "no async readback" is correct about *readback*, but fence sync itself **is** deployed in the same tree, in the chunk device — which upgrades the availability half of A3's `[U]` claim from unverified to observed. §4.3.3 reuses exactly its non-blocking poll shape; **§11.4 requests the PD clarification** | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/device/GLRenderDevice.java:233]` — `return new GlFence(GL32C.glFenceSync(GL32C.GL_SYNC_GPU_COMMANDS_COMPLETE, 0));`; the non-blocking poll at `reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/sync/GlFence.java:23` — `result = GL32C.glGetSynci(this.id, GL32C.GL_SYNC_STATUS, count);` |
| **Shared-context async compile** | **No reference exists.** A grep of the tree for `glfwCreateWindow`, `glfwMakeContextCurrent` and share-context construction returns nothing; PD §16 records that Pintonium never touches context creation. A4 is designed from RESEARCH §6.2 alone and carries OQ-15 | `docs/research/v1/RESEARCH.md:774`; `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:756`–`:779` via `docs/phase7/v1/PHASE_7_DOC.md:4447`–`:4449` |
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
decision, and §0.11's 2026-09-08 re-derivation re-verified all three citations against Phase 6's current bytes:

- **The decision.** `docs/phase6/v1/PHASE_6_DOC.md:2230` — *"`D-P6-1` | select synchronous CPU
  `centerDepthSmooth`; return empty macro contribution | only candidate expressible by current App D,
  Phase 1, Phase 3, and fixed-unit contracts; §4.8"*.
- **The decision text.** `docs/phase6/v1/PHASE_6_DOC.md:1184`–`:1187` — *"`[D-P6-1]` selects candidate
  A. `centerDepthMacroContributor()` consequently returns `MacroContribution.Empty` for every
  configuration. **Phase 14's PBO/fence item is not obviated** and remains the sole async-readback
  modernization ledger entry."*
- **The conformance-map rejection row.** `docs/phase6/v1/PHASE_6_DOC.md:638` — *"| PD GPU
  `centerDepthSmooth` | not populated; CPU path selected | **Contract-visible rejection, D-P6-1**"*.

Phase 6's rejection is contract-visible and was verified through §G11.4's decision rule at its own
§4.8 (`docs/phase6/v1/PHASE_6_DOC.md:1155`–`:1187`), on four independent grounds: the macro would
rewrite the pack's own `uniform float centerDepthSmooth;` declaration into invalid GLSL; App B.3
reserves no unit for a center-depth sampler; the GPU EMA is not bit-identical to the CPU readback;
and the reference's declaration-aware transformer is the prohibited AGPL dependency. Phase 6 also
staged it: *"| GPU center-depth alternative | evaluated now | not scheduled | rejected D-P6-1 |"*
(`docs/phase6/v1/PHASE_6_DOC.md:2206`).

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
   (`:659`–`:661`). Phase 13's fan-out to Phase 14 is precisely the propagation path.
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
   filter/wrap to *both* sides (`docs/phase5/v1/PHASE_5_DOC.md:1185`, `:1439`–`:1440`). Any per-pass
   or per-flip change of the effective filter — for a pass that declares mipmapping on a buffer it
   reads, for instance — is a `glTexParameteri` on the texture object, which is global to that
   object and therefore has to be set and unset around the pass.
2. **Shadow mipmap generation.** Phase 5's `generateShadowMipmaps` changes a shadow texture's min
   filter to a mipmap filter, generates, then restores: *"One buffer's generation failure does not
   stop later buffers when Phase 5 successfully restores that texture's configured non-mipmap min
   filter … **If restoration fails**, Phase 5 stops before later buffers, atomically performs the
   same containment as `degradeToNeutral(generation, MIPMAP_FILTER_RESTORE_FAILURE)`"*
   (`docs/phase5/v1/PHASE_5_DOC.md:2257`–`:2259`). An entire failure mode, a rejection enum
   constant, a result variant (`Neutralized`), and a containment path exist because a filter value
   lives on the texture object and must be temporarily mutated.

Sampler objects select effective filter, wrap, LOD and compare state at the per-unit binding.
They do not remove the texture object's sampling state: sampler zero exposes it. D-P14-31
therefore maintains the complete latest authenticated object baseline as well as the sampler
cache. Only proven redundant writes may be omitted; an unchanged caller sequence that really
changes owner parameters still requires those object writes. Native churn reduction is a
measurement hypothesis, not permission to split sampling parameters away from the object.

#### 4.1.2 `SamplerKey` — the derived value, in `:engine`

The load-bearing rule is **D-P14-1: a sampler is *derived*, never authored.** Phase 5 (and, at v0.5,
Phase 13) remains the sole author of what filter and wrap a texture has; Phase 14 computes the
sampler-object state as a pure function of that same value. This is what makes A1 a behavioral
no-op rather than a second, competing source of truth.

`SamplerKey` remains a proposed immutable value in the requested `engine.gl` placement.
P1 §§4.7.7/5/D-P1-52 now publishes the complete source and exact split, adopted here:
minFilter, magFilter, wrapS/T/R, compareMode, compareFunction, borderColor, minLod, maxLod,
lodBias and maxAnisotropy form the entire sampler key, with the owner's exact closed types.
`SamplerKey.of(TextureParameters)` copies this immutable prefix losslessly; no reduced
filter vocabulary or guessed field. The package/execution/equivalence gates remain separate.

GL divides a texture's parameter set in two, and the split is the design's central correctness
concern (§8.1's `SamplerStateSplitTest` pins it):

- **Sampler state** — exactly P1's prefix through maxAnisotropy, including all six min filters,
  both mag filters, each axis, compare mode/function, border RGBA and all LOD fields.
  Owner validation currently fixes border zero and anisotropy1; retain them in the key.
  Bound samplers override this state, so equality must hold for every accepted field.
- **Object state** — baseLevel/maxLevel/swizzle remain on the texture object.
  Immutability belongs to allocation and is not a setParameters field or sampler key.
  P1's LEGACY_DEPTH_LUMINANCE mapping preserves R,R,R,1 across its compatibility routes.

**D-P14-31:** successful `TextureService.setParameters(t, p)` under every sampler tier
establishes the complete authenticated P1 value on the owned texture object, including
sampler-state fields and baseLevel/maxLevel/swizzle. Under a sampler tier it also establishes
the matching `SamplerCache.intern(SamplerKey.of(p))` association. The GL state classification
above determines the key, not which object writes may be omitted. Skip a write only when
same-live-identity, error-free native state knowledge proves it redundant; unknown/invalidated
knowledge requires the baseline write. No caller changes or new parameter domain.
P1 D-P1-52 supplies the exact owned-source mapping; R-P14→P1-3 is fulfilled/adopted,
unverified. Incomplete/failed values never enter interning. Borrowed unknown state uses
sampler0; A1 retains independent implementation/equivalence/measurement gates.

Retain the complete authenticated current `TextureParameters`, not only the key, with each
live owned texture identity. Commit its latest value/cache association only after validation,
complete object establishment and sampler preparation succeed at the owner's error boundary.
Partial native updates are not rolled back by retaining an old Java value: invalidate state
knowledge, block affected draws and enter §4.1.7 replay/containment. Never report successful
parameterization or admit sampler-zero use of a partially established object.

#### 4.1.3 `SamplerCache` — interning, in `mod.glue`

```text
SamplerCache
  Map<SamplerKey, SamplerHandle>  interned      — reference-accounted through live/retiring owners
  perTexture                                 — actual handle/source/parameter/publication identity
  SamplerHandle[16]               boundPerUnit  — the fixed App B.3 units, index == unit number
```

- **Cardinality is small and closed.** The distinct sampler states an estate can want are: NEAREST or
  LINEAR × mipmapped or not, all `CLAMP_TO_EDGE` for the colour estate
  (`docs/phase5/v1/PHASE_5_DOC.md:1439`–`:1440`); plus the shadow set, which adds the hardware-PCF
  compare mode and per-texture nearest/mipmap flags from Phase 5's
  `ShadowTextureResource(hardwareFiltering, mipmap, nearest)` (`docs/phase5/v1/PHASE_5_DOC.md:631`–`:632`);
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

A1 derives/cache-binds samplers inside this owner-controlled path only. The coordinated
P1/P5 normalization contract is `TextureService.prepareUnitBindings(int occupiedUnitMask)`:
bits 0–15 identify exactly the preflighted `BoundObject` rows; any other bit is invalid.
P5 calls it once after complete successful preflight and before its ascending `BoundObject`
`bindToUnit` loop. P14 clears native samplers on every unoccupied unit there; occupied unknown
or unconverted borrowed objects clear their sampler in `bindToUnit`. Rejected/Degraded preflight
performs neither normalization nor binds nor any other GL mutation. Backend errors after
normalization follow existing BackendFailed containment and transfer no lease.
This is normalization, not a granted sampler-array batching API. `MULTI_BIND` batching within
the physical bind operation remains R-P14→P5-2/P1-gated; use PER_UNIT or NONE otherwise.

**`D-P14-3`: dynamic per-program unit allocation is rejected, pre-decided.** §G11.4 lists it among
the standing pre-decided rejections — *"dynamic per-program texture-unit allocation (ours: fixed App
B.3 map incl. depthtex1 at unit 11)"* (`docs/design/v3/DESIGN.md:953`–`:954`) — and Phase 6 already
rejected it for the sampler-uniform side (`docs/phase6/v1/PHASE_6_DOC.md:635`). This design contains
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

The concrete clearing dispatch is P1 `ShaderService.useFixedFunction()`: under the sampler
strategy its backend clears all sixteen native sampler units before fixed-function drawing,
including shader → declaration-empty fixed-function → continued-frame transitions. Existing
P4 barrier release and P7 terminal release invoke that same operation on normal, early,
thrown, abort and shaders-off finalization paths; there is no invented backend frame callback.
The clear uses native range clear where supported, otherwise per-unit zero binds. Unknown
cache state must be cleared, not skipped as if known zero. Only successfully completed clears
update bound-unit knowledge. Context loss invalidates knowledge without issuing GL on a lost
context. A clear failure cannot admit the fixed-function draw or claim safe restoration;
it enters existing owner containment. Changing the tier enum alone cannot repair native state.
§5.10 records the exact coordinated receiver obligations; actual native evidence is §8.2.

Ordinary fixed-function FINAL is not demotion: P5 binds its compatible frozen owned colortex0
at unit zero, P4 immediately activates the fixed terminal through `useFixedFunction`, and P7
draws without rebinding rows. D-P14-31 ensures the latest complete owner sampling state is
already on that object before the clear; no lazy replay or parameter discovery is needed in
activation. Clearing preserves active texture unit, object bindings, framebuffer bindings and
their cooperating caches; only sampler bindings change. No texture is rebound to repair FINAL.
After this draw, the sampler tier remains enabled; the next successful P5 preflight normalizes
then binds textures and their matching samplers before P4 shader activation. Complete all-unit
clearing still occurs on every ordinary or terminal fixed-function route, including unknown
cache state. A baseline/clear failure prevents draw and enters existing containment. Borrowed
objects are never parameterized/deleted by this rule; authenticated borrowed sampling may use
an equivalent sampler, and clearing reveals the foreign owner's state, not a fabricated baseline.

#### 4.1.6 Behavioral-no-op obligation, stated as a test

A1 is a no-op **iff**, for every texture *t* in the estate and every unit *u* it is bound to, the
sampler state applied through *u* equals the sampler state `TextureParameters` for *t* specifies.
`SamplerEquivalenceTest` checks the pure derivation/model over the estate; native application
and transition equivalence require §8.2's backend procedure. Neither facade traces nor model
equality prove native sampler state. Cover successful sampler-backed use → ordinary fixed-function
FINAL → resumed shader use without demotion, latest-parameter updates, and separately
creation/parameterization failure → NONE/containment, including every admitted P1 field.

#### 4.1.7 Fallback

`SamplerTier.NONE`, selected when `!profile.atLeast(3,3)` or when policy forces it off. Under `NONE`,
`SamplerCache` is not constructed, `setParameters` applies the whole parameter set with
`glTexParameter*` exactly as Phase 5 designs it today, and `bindToUnit` binds only the texture.
This is byte-for-byte the reference-faithful path; it is the shipping default until A1's ledger row
(§7.5, L-2) closes. Tier selection is one line of a pure function over a serializable profile, so
both paths are exercised headlessly from `GLCapabilityProfile` fixtures.

**Runtime demotion (D-P14-26).** Stop admitting affected-estate draws at a render-thread
safe point. Authenticate every affected live owned handle and its latest complete owner
parameters against the still-live publication/source/parameter identity. Replay the entire
P1 parameter set onto each texture object through the baseline mapping, preserving binding
neutrality, and check the owner's error boundary. Only after every replay succeeds clear
samplers, commit NONE and admit draws. No intermediate texture or partially restored estate
is drawable. Retire cache references only under §4.1.3's live/retiring accounting.
If authentication, replay or clear fails, report failure through the existing owning estate/
texture failure path; contain and rebuild from authenticated owner data under NONE before
publication, or keep shaders off. Do not expose stale texture defaults or report SUCCESS.
Never parameterize or delete a borrowed foreign object to repair an owned estate; its owner
retains its state and lifetime. Sampler zero for an unknown borrowed texture is not permission
to reconstruct that texture. Startup NONE and runtime demotion are distinct evidence cases.

#### 4.1.8 Hand-off to Phase 5 (not a change made here)

`glGenerateMipmap` does not require a mipmap min filter. H-P14→P5-1 therefore offers P5
an optional separately reviewed removal of its temporary filter mutation/restoration sequence.
Sampler enablement alone does not make that sequence or its failure paths unreachable:
while P5 still calls setParameters, D-P14-31 applies every nonredundant complete object update.
Any adopted simplification must retain the latest authenticated object baseline for sampler-zero
use. P5 owns the algorithm; this document changes neither its baseline nor NONE fallback.

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

**D-P14-40 — logical creation precedes exact-target native materialization.**
`TextureService.create(debugLabel)` creates only the owned logical handle and retains its
label; it chooses no target, generates no native name and issues no native label call.
After all P1 preflight succeeds, the first admitted `allocate(handle, spec)` invokes
`DsaStrategy.createTexture` with exactly spec's 1D/2D/3D/RECTANGLE target, never a guessed 2D.
CORE/ARB create the typed object directly; bind-to-edit generates a name and first binds it
to that exact target through the cooperating save/restore path before native labeling.
Only an existing native object may receive the retained label. Storage definition then uses
the same target; only successful allocation authenticates storage/target metadata.
The existing `initializeDepthTextureFromFramebuffer` first-initialization path supplies its
known TEXTURE_2D target and exact source depth format/extent before this same materialization
sequence; steady `copyDepthToTexture` requires already established matching 2D storage.
Preflight rejection leaves an unmaterialized handle unchanged. A failed native creation or
first storage definition publishes no usable storage: retain/account any created name for
cleanup, invalidate usability, and rebuild with a new logical handle rather than retargeting
a possibly typed failed name. An allocated handle's target never changes on reallocation.
Deleting a never-materialized (including preflight-rejected) handle ends its logical lifetime
and releases its retained label without any native texture-delete call. A name created before
failure is deleted exactly once by the owning cleanup path; a failed operation is not evidence
that no name exists. No caller-owned native name or second naming API is introduced.

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
(`docs/phase5/v1/PHASE_5_DOC.md:3112`).

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

  > **`D-P14-7` — binding neutrality, narrowed by D-P14-41.** Logical/native creation,
  > `allocate`, `setParameters`, `upload`, `generateMipmap`, `attachDepth`,
  > `attachDepthStencil`, `initializeDepthTextureFromFramebuffer` and `copyDepthToTexture`
  > preserve observable texture-unit/framebuffer bindings under every tier on successful
  > completion. On failure restore those temporary bindings or contain drawing if restoration
  > fails. `bindToUnit`, framebuffer binding and StateService binding verbs intentionally
  > change bindings. **Deletion is lifetime-ending cleanup, not binding-neutral editing.**

  DSA edits need no temporary binding; bind-to-edit saves/restores through P1's cooperating
  backend. `BindingNeutralityTest` models only this preservation set; §8.2 native queries,
  not a facade recording, establish actual tier substitutability.

  **D-P14-41 — deletion.** At the existing owner retirement drain, after outstanding leases
  and live/retiring references permit deletion, deleting a native texture zeroes every affected
  binding of that object in the current context; deleting a bound framebuffer likewise zeroes
  its affected read/draw bindings. Preserve unrelated bindings and the active unit, cooperating
  with GlStateManager for cached state and invalidating/updating backend and P5 binding
  knowledge before another draw. Retire authenticated object/parameter/sampler associations,
  not independently live sampler objects. Never save-and-rebind a deleted name, resurrect its
  handle, or treat numeric name reuse as identity reuse. Borrowed objects are released, never
  deleted. P13 reverse-order deletion and P5 lease/restoration ownership remain in force;
  scopes must release/restore before their lease permits the drain, so no restoration target
  can be deleted underneath them. Native cleanup failure remains accounted and contains
  drawing when state cannot be authenticated; it is not successful neutral restoration.
- **One further consequence:** A2 adds no GlStateManager observation seam to the engine.
  The existing P1 backend alone cooperates with cached state during temporary binding,
  restoration and lifetime-ending deletion; the uniform-observation consumer remains P6.

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
(`docs/phase6/v1/PHASE_6_DOC.md:716`–`:736`, `:751`–`:753`):

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
and step 7 advances the smoothers (`docs/phase6/v1/PHASE_6_DOC.md:1065`–`:1067`). So the sync design
   **already** returns a value one frame old; in a controlled one-frame-ready schedule async
   adds one more frame of age. Late completion follows only the eventual P6 age grant.
2. **`Sample` carries no frame identity.** Phase 6 requires identity echo of
   `FrameUniformSample` but states of this result only that *"`CenterDepthResult.Sample.depth` is
  finite in `[0,1]`"* (`docs/phase6/v1/PHASE_6_DOC.md:784`–`:785`). The type is therefore
   *structurally* able to carry a value sampled at an earlier frame.
3. **`Unavailable` is a designed, safe outcome.** Phase 6 maps it to §G2.4 rung 2a: *"center-depth
   dimensions/FBO unavailable | 2a | retain previous smoothed depth; first unavailable frame leaves
   cell invalid"* (`docs/phase6/v1/PHASE_6_DOC.md:2036`). Warm-up and invalidation can therefore use
   it without inventing a failure mode.
4. **Phase 6 forbids hiding the latency.** *"Phase 14 owns measuring/replacing that stall. Phase 6
   records frame time but **does not hide the cost with an uncontracted one-frame queue**"*
   (`docs/phase6/v1/PHASE_6_DOC.md:2097`–`:2098`). This is the sentence that shapes the whole row:
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
   (`docs/phase6/v1/PHASE_6_DOC.md:782`–`:784`, `:1079`–`:1080`).
2. **Invalidate on identity change (`D-P14-10`).** If `registryGeneration`, `worldEpoch`,
   `framebufferWidth/Height`, `pixelX` or `pixelY` differ from the slot's recorded request, **discard
   the entire ring** — delete the fences, leave the buffer objects, mark every slot `EMPTY`, clear
   `last` — and continue to step 4. A depth value from a previous world, a previous pack generation
   or a differently-sized framebuffer must never enter the EMA; Phase 6's world-epoch rule exists for
   the same reason (`docs/phase6/v1/PHASE_6_DOC.md:682`).
3. **Poll the oldest `IN_FLIGHT` slot, without ever blocking (`D-P14-9`).** Query
   `glGetSynci(sync, GL_SYNC_STATUS)`; the value is ready **iff** it returns `GL_SIGNALED`. This is
   exactly the reference's non-blocking shape —
   `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/sync/GlFence.java:23]`,
   `result = GL32C.glGetSynci(this.id, GL32C.GL_SYNC_STATUS, count);`. If signalled, bind that
   slot's PBO in a save/restore scope and map offset zero for four bytes with `GL_MAP_READ_BIT`.
   Copy to a local candidate using native float byte order, then unmap. Publish `last` only
   after a non-null, error-free map, successful `glUnmapBuffer` (`GL_TRUE`), error-free cleanup
   and a finite candidate in `[0,1]`. Delete the fence and mark the slot `EMPTY` on successful
   consumption. A signalled fence alone does not authenticate the transfer or mapped bytes.
   A transfer/fence/poll/map/unmap failure never publishes that candidate: invalidate the ring,
   clear `last`, restore state and return `Unavailable`, then demote to synchronous for the
   session when safe cleanup is established; failed restoration uses existing owner containment.
   **The render thread never calls `glClientWaitSync` with a nonzero timeout or `glFinish`.**
   If the oldest slot is still unsignalled and its request is older than `N` frames, discard
   the ring and `last`, count one timeout occurrence and return `Unavailable` without issuing
   another transfer on that call. Three consecutive timeout occurrences demote for the session;
   ring reset alone does not reset that counter, only a valid completion does. The next call
   uses the synchronous fallback after demotion. This is §6's operational limit, not a
   guarantee that a valid sample must arrive within two frames.
4. **Issue the next transfer (`D-P14-35`).** If a slot is `EMPTY`, use a backend-local native
   scope that saves actual incoming `GL_READ_FRAMEBUFFER_BINDING`, `GL_PIXEL_PACK_BUFFER_BINDING`
   and `GL_PACK_ALIGNMENT`, `GL_PACK_ROW_LENGTH`, `GL_PACK_SKIP_ROWS`, `GL_PACK_SKIP_PIXELS`,
   `GL_PACK_SWAP_BYTES`. Set alignment to 4, row length and both skips to 0, and swap bytes to
   false; bind the slot PBO and the accepted main-depth read framebuffer corresponding to the
   request. Issue `glReadPixels(pixelX, pixelY, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, 0)`.
   This canonical layout writes exactly four unswapped bytes at offset zero. `GL_PACK_LSB_FIRST`
   affects bitmap data only; image-height/skip-images do not affect this 2D `glReadPixels`
   transfer, so those states remain untouched. Draw-framebuffer binding is never changed.
   Use the existing backend GL-error ownership boundary to establish successful setup/transfer
   before creating `glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0)`; require a valid error-free
   fence before recording the request and marking `IN_FLIGHT`. Never admit a failed transfer
   merely because a subsequent fence signals. Restore every saved value and both bindings
   exactly in `finally`, on success, GL error and exception; restoration failure prevents
   successful admission and enters containment. Do not unbind to guessed zero defaults.
5. **Return (`D-P14-38`).** `Sample(last.depth)` if a valid `last` is present; otherwise
   `Unavailable("centerdepth.warmup")`. Warm-up lasts until the first valid completion after
   start/world change/resize/reload, or yields to §6's timeout/demotion policy. With `N=3`,
   no signalled fence on the third call legitimately means `Unavailable`; a later valid
   completion ends warm-up, while a stuck fence eventually triggers reset/demotion.
   Neither a blocking wait nor invented depth may force an earlier result.

The mapping scope restores its actual incoming pack-buffer binding on every exit too.
All layout and binding work is internal to the native backend: no public pixel-store API
or change to Phase 6's SPI is introduced, and the async age grant remains ungranted.

#### 4.3.4 Interaction with resize and reload

The ring is discarded by step 2 on any identity change, which is already sufficient. It is
*additionally* discarded eagerly on a Phase 5 `BufferResizeNotice` — Phase 5 names Phase 14 as a
consumer of that contract (`docs/phase5/v1/PHASE_5_DOC.md:2738`), and eager discard means the
first frame after a resize costs a warm-up `Unavailable` rather than an invalidation check against a
destroyed FBO. All eight reasons in §5.2 invalidate the ring, not just depth/display changes.
Phase 7 already sequences center-depth sampling before any resize is applied
(`docs/phase7/v1/PHASE_7_DOC.md:1566`–`:1569`: *"On an active frame, Phase 6 samples the prior depth
first; Phase 7 then abandons that shader frame … This avoids resizing the source before center-depth
sampling"*), so no in-flight transfer can be reading a framebuffer that is being torn down beneath
it.

#### 4.3.5 Cost, and the honest accounting

Removed: one synchronous `glReadPixels` per rendered frame **when the pack declares
`centerDepthSmooth`**, which Phase 6 characterises as *"deliberately one pipeline stall per rendered
frame"* (`docs/phase6/v1/PHASE_6_DOC.md:2095`). A synchronous single-pixel depth read forces the
driver to flush and wait for all preceding commands.

Added: one 4-byte buffer object per slot (8 bytes total at `N=2`), one fence per frame, one
non-blocking status query, relevant pack-state/binding save-normalize-restore and one 4-byte map/unmap
per frame, plus backend error-boundary checks required before publication. Allocation: zero in steady
state — the buffer objects are created once per generation; `last` is a mutable primitive pair, and
the map returns a `ByteBuffer` the backend reads one float from without retaining. This is the
allocation posture §7.2 requires of every per-frame path.

The one thing that is *not* free is the extra frame of age, and §4.3.6 is how that is discharged
rather than asserted away.

#### 4.3.6 The imperceptibility verification (doc-gate item)

The specification names it: *"imperceptibility verification (compare `centerDepthSmooth` traces sync
vs async on the Phase 2 scenes)"* (`docs/design/v3/DESIGN.md:2539`–`:2541`), and the doc gate repeats
it (`:2578`–`:2579`). Specified here concretely enough to run; the run itself is a Phase 2 harness
run, and the procedure is repeated as spike step S-22-3 in §10.2.

**Captured evidence has two separate sources.** Facade upload records yield only
`(frameId, uploadedValue)`; they do not contain raw depth, source age or smoothing time.
For the algebraic oracle use a controlled replay through existing `CenterDepthSource` and
`FrameBeginInput` seams, not a new public runtime API. Its source-free input table supplies
frame/world identity, raw depth, source-frame identity, Sample/Unavailable status, repeated
sample marker, smoothingTimeTicks and effective half-life, plus reset events and P6's initial
state. Feed identical raw/timing sequences to sync and granted-age source schedules; retain
both schedules and upload outputs locally. A replay is mathematical/contract evidence, not
proof of native readback age. Actual driver runs separately correlate completed PBO requests
with their recorded request identities in a P14-local diagnostic capture; unavailable input
capture invalidates that evidence rather than guessing raw values from uniforms. No P1 recorder
extension is assumed. Timing runs exclude instrumentation and debug flags.

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
identical seeds, identical scripted frame timing, ≥600 frames each. The first 5 frames are a
fixed measurement-settling exclusion, not a readback readiness guarantee. Record the actual
first-valid-completion frame and all startup/reset/timeout outcomes; never discard additional
failed frames until a run appears successful. `RUN-SCENE-SELFCHECK` must pass first — Phase 7
requires *"repeated frames identical"* (`docs/phase7/v1/PHASE_7_DOC.md:4311`) — because a
non-deterministic scene invalidates the comparison.

**Criteria.** Let `s_i` and `a_i` be the sync and async uploaded values at frame *i*, and
`d_i = |a_i − s_i|`. All four must hold:

| # | Criterion | Rationale |
|---|---|---|
| **C1 — source schedule** | In controlled one-frame-ready runs, each async accepted raw sample has the expected preceding source-frame identity; initial unavailable/reset and repeated/late samples follow only the eventual P6 age grant. | A constant output alone cannot prove one-frame delay. Native request/completion capture must separately establish the same age policy. |
| **C2 — exact recurrence replay** | Independently replay P6 §4.5 twice, once for each recorded source schedule, and compare each uploaded float to its oracle within `1e-6`. First valid sample initializes directly; world reset reinitializes; Unavailable retains prior state; repeated valid samples update once per accepted frame using that frame's delta, not their source age. For initialized state, zero delta retains; regressing time retains/reports; legal h=0 snaps for positive delta; otherwise compute `x+(s_previous-x)*2^(-delta/h)` in double and cast the result to float. Nonfinite inputs never enter state. | Handles variable smoothing deltas and all age-granted schedules without a false universal one-step bound. Warm-up at zero then raw one with delta=h=1 yields sync 0.5 versus delayed 0, a legitimate difference of 0.5. |
| **C3 — perceptual** | On F4, the T1 screenshot regression stays within Phase 2's existing T1 tolerance against the async run's own approved baseline, with **zero new outlier pixels** versus the sync run. | The only criterion a user can actually perceive. It deliberately reuses Phase 2's tolerance rather than defining a Phase 14 tolerance. |
| **C4 — no new failure** | Across all four steady-scene families: a valid async completion must occur within the fixed run, with no timeout reset/demotion, zero `Unavailable` after the observed first valid completion, zero GL errors attributed to readback, and no T1 regression on any other matrix pack. Retain/report startup length even within the five-frame measurement exclusion. | Warm-up means awaiting first valid completion, not two frames or the measurement exclusion. No completion, reset or demotion fails the async operational gate rather than making it vacuously pass. |

**What constitutes failure, and what happens then.** Any criterion missed is a **failure of the
async row, not of the milestone**. On failure: `AsyncReadbackTier.PBO_FENCE` remains `FORCE_OFF` by
default, the synchronous path ships, the measured numbers and the failing criterion are written into
ledger row L-3 (§7.5) and back into RESEARCH.md §11's OQ-22 status column by the implementation
effort per §G4.4 (`docs/design/v3/DESIGN.md:578`–`:580`), and the item is closed rather than left
open. C1/C2 failures are correctness or invalid-evidence failures, never measured perceptibility
results. Diagnose the source schedule/oracle/instrumentation before retrying. Only C3 measures
perceptibility; retain the separate C4 operational gate. Controlled unavailable/repeated cases
remain required for C2 and §8.1's late/stuck-fence cases even though C4's healthy steady-scene
run admits none after its observed first valid completion and admits no timeout/demotion.

#### 4.3.7 Fallback

`AsyncReadbackTier.SYNCHRONOUS`: `mod.glue`'s `CenterDepthSource` calls Phase 1's
`FramebufferService.readDepthPixel(f, x, y)` (`docs/phase1/v14/PHASE_1_DOC.md:3422`), which is
exactly what Phase 6 designs today (`docs/phase6/v1/PHASE_6_DOC.md:1081`–`:1083`). It is:

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
                                       glFlush() on this producer context
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
   state whose ownership is render-thread by contract (`docs/phase6/v1/PHASE_6_DOC.md:2053`); and
   `Program.use()` is *"the universal state barrier"* (`docs/design/v3/DESIGN.md:533`–`:535`), which
   must not become concurrent. This split keeps the expensive part (GLSL compilation of ~40 programs)
   off-thread while leaving every ownership rule in the project untouched.
3. **Fence, producer flush, then publication.** After compile/status/log (or upload), insert
   the completion fence, call `glFlush` on that same worker context, then publish the result.
   A different context's flush and render-thread `glWaitSync` cannot submit producer commands.
   Before enqueueing a GPU wait/attach/use, nonblocking consumer polling must establish fence
   completion; an unsignaled fence remains pending and subject to the watchdog, never an
   indefinite render-GPU wait. Native observation verifies this ordering for isolated jobs.
   Failed/canceled publication transfers no ownership: retain result/fence in the worker's
   accounted completion inventory for owner disposal after safe acknowledgement (§5.9).
4. **The render thread never blocks on the worker.** `CompileExecutor` is poll-shaped, not
   await-shaped: the pipeline build asks "is this batch done?" once per frame and keeps rendering
   vanilla meanwhile. This is what requires Phase 7's transaction to be resumable — R-P14→P7-1.
5. **A watchdog bounds waiting, not native lifetime.** At `T_watchdog` (default 5 s,
   configurable), close admission, terminal-fail the token and reject late publication.
   Request cooperative cancellation. Only acknowledged job quiescence and worker context
   detachment permit owner disposal and main-thread GLFW window destruction. If acknowledgement
   never arrives, quarantine the worker, context, queue and every uncertain object/reference;
   retain their accounting, admit no new shared jobs and do not replay the build in that
   share group or claim successful shutdown. Keep the transaction failed/shaders-off; vanilla
   continuation is conditional on a healthy main context, never promised against a hung driver.
   No blocking join, thread kill, unsafe destruction or background ownership abandonment is
   allowed. A later acknowledgement may permit cleanup; otherwise process termination is the
   safe ultimate reclamation boundary, reported as unresolved retention, not successful cleanup.
6. **D-P14-32 — texture upload has two ordered cross-context edges.** P13 keeps texture
   creation/storage allocation and complete parameter establishment on the render thread.
   After its last storage-defining command, that main producer inserts an allocation-readiness
   `glFenceSync`, calls `glFlush` on the main context, then submits the opaque job carrying
   that sync dependency and immutable prepared payload. Java queue publication/name sharing
   alone is not readiness. On the worker, nonblocking readiness polling must observe
   `GL_SIGNALED` before any dependent `glBindTexture` or `glTexSubImage2D`; an unsignaled
   allocation stays pending under cancellation/watchdog control, with no dependent GL command.
   Failed polling/fence creation/producer flush fails the job rather than guessing completion.
   After readiness, worker bind/upload is followed by its distinct completion fence, worker
   `glFlush`, then publication under rule 3. Render admission polls that outgoing completion
   nonblockingly before visibility/rebind/use and frozen-identity revalidation. No render
   blocking wait, later main swap/flush, worker flush of main work, or outgoing fence can
   substitute for the allocation edge. Storage policy never crosses to the worker.
   §5.9 specifies exactly-once lifetime accounting for both syncs; pending uploads are never
   bindable/animatable publications and grant no current async API.

#### 4.4.3 How a failed or unsupported shared context degrades

Stated as an ordered ladder, because "degrade gracefully" is not a design:

| Stage | Detection | Result |
|---|---|---|
| Creation | `glfwCreateWindow(1, 1, "", NULL, mainWindow)` returns `NULL`, or `glfwMakeContextCurrent` on the worker fails, or `GL.createCapabilities()` on the worker reports a profile inconsistent with the main context | `GlWorkerContext.create()` returns empty; `AsyncCompileTier.INLINE`; one info-level diagnostic; **no user-visible error** — this is a supported configuration, not a failure |
| Driver policy | `profile.vendor()`/`renderer()` is not on the per-family allowlist (`D-P14-12`) | `INLINE`, without attempting creation |
| Probe | Compile/error/fence-progress failure | close admission; acknowledged quiescence/detachment permits cleanup then INLINE; missing acknowledgement quarantines per rule 5 |
| First real batch | GL error or shared-only link failure | fail transaction; cooperative cleanup before synchronous retry, only after main/share-group health is established |
| Watchdog | rule 5 above | terminal failure and quarantine until acknowledged safe; no automatic in-session replay or successful shutdown claim |
| Context loss | main context loss | invalidate publication/admission, retain uncertain worker ownership; destroy only after detached acknowledgement, or reclaim at process termination; no old handle reuse |

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
  exist. What is absent is submit/poll staging at step 8, with render allocation → readiness
  fence → main flush → worker readiness before bind/upload, then worker completion fence →
  worker flush → publication, no bindable partial upload and no animation before readiness.
  Both syncs follow §5.9 cancellation/quarantine. Until adoption, uploads remain synchronous,
  independently of whether the compiler proposal is adopted.
- **Phase 1 (R-P14→P14-P1-4).** `RecordingGLDevice` is *"not made thread-safe to cover the Phase 14
  exception. If off-thread uploads ever need recording, that is a Phase 14 request against this
  document"* (`docs/phase1/v14/PHASE_1_DOC.md:5626`). Taking that invitation up in §5.3.

#### 4.4.6 Fallback

`AsyncCompileTier.INLINE` is today's shipping default and the safe recovered fallback after
§4.4.3's acknowledged teardown. Unresolved native work instead stays quarantined/failed closed.
Ledger row L-4 (§7.5) must distinguish these outcomes.

### 4.5 A5 — KHR_debug labels and groups, plus debug-context dev mode

#### 4.5.1 What already exists

Phase 1 ships the *interface* at v0.1 and stages the *implementation* at v0.5 to this phase:
*"`DebugService` (§4.7.4) exists as an interface at v0.1 so call sites can label objects immediately;
`-Dschmaloogium.debug.glLabels` (§4.9.3) gates it; the implementation is `v0.5` / Phase 14 (§9)"*
(`docs/phase1/v14/PHASE_1_DOC.md:1906`), with the milestone row *"`schmaloogium.debug.glLabels` |
`v0.5` | Phase 14"* (`:5894`). The interface is:
```java
public interface DebugService {          // docs/phase1/v14/PHASE_1_DOC.md:3490–:3495
    void pushGroup(String label);
    void popGroup();
    void label(GLHandle handle, String label);
    boolean isActive();          // installed supported GL4.3/KHR backend && glLabels (P1 D-P1-54)
}
```

Phase 5 records the same posture: *"`DebugService` labels are present from v0.1 and activate **only at Phase 14**"* (`docs/phase5/v1/PHASE_5_DOC.md:3116`).

#### 4.5.2 Labels

`KhrDebugBackend.label(handle, name)` retains the label for an unmaterialized texture and
issues `glObjectLabel(type, id, name)` only for an existing native object. Two design points:

- **Labels originate from the facade's existing `debugLabel` argument.** Logical texture
  creation retains it; D-P14-40 emits it after first exact-target native materialization,
  before storage work, not at targetless `TextureService.create`. An explicit label update
  before materialization updates that retained value without GL. Other owned objects,
  including framebuffers, likewise receive native labels only after native instantiation,
  not merely name reservation. This preserves one naming path for every owned object
  without labeling a nonobject or guessing a texture target.
- **Length is clamped in encoded bytes (`D-P14-36`).** Probe `GL_MAX_LABEL_LENGTH` once;
  the legal payload maximum is `GL_MAX_LABEL_LENGTH - 1` GLchar bytes, excluding the terminator,
  not Java characters. Encode as UTF-8 and truncate only at a complete encoded code-point
  boundary within that budget; never split a multibyte sequence or surrogate pair. Malformed
  UTF-16 is replaced during encoding before budgeting. Submit the explicit encoded byte length.
  A label whose encoded length equals the reported limit is already too long. The historical
  reference limit probe remains `[V:observed — Pintonium .../gl/debug/GLDebug.java:331]`;
  this corrected strict limit is not a new reproduction of that pinned source observation.
  An invalid/nonpositive limit disables labels without a native label call, rather than risking
  `GL_INVALID_VALUE` in Phase 1's error attribution (`docs/phase1/v14/PHASE_1_DOC.md:5508`).

#### 4.5.3 Groups — `D-P14-13`, balance-safe by construction

This is where the do-not-inherit row lives. The specification is explicit: *"copy the **pattern**
(object labels + per-phase push/pop groups behind a debug flag, PD §15), **not the wiring** —
Pintonium's `setPhase` has a push/pop imbalance bug (PD B7)"* (`docs/design/v3/DESIGN.md:2548`–`:2551`).
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
4. **Frame-boundary assertion (`D-P14-37`).** At Phase 7's `finish`/`abort`, both counters
   must be zero. If either is nonzero, first reset `virtualDepth` to zero **without any GL pop**;
   then issue exactly the outstanding `depth` native pops, decrementing it to zero. Emit one
   rate-limited imbalance diagnostic for the boundary recovery, not one per leaked group.
   This removes virtual overflow entries before discharging only actual pushes, leaving the
   native default group untouched even when overflow is followed by THROWN/abort.

`DebugGroupBalanceTest` (§8.1) drives arbitrary bounded push/pop/throw sequences across Phase 7's
three exit kinds — `NORMAL`, `EARLY_RETURN`, `THROWN` (`docs/phase7/v1/PHASE_7_DOC.md:2652`–`:2655`) — and
asserts both counters return to zero and native pops never exceed native pushes. Explicitly
push to real capacity, add multiple virtual overflow pushes, then throw/abort without matching
pops: recovery emits zero pops for those virtual entries, exactly the outstanding real pops
and one diagnostic. A subsequent balanced frame must remain at the native default group.

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
(`docs/phase7/v1/PHASE_7_DOC.md:4229`).

This is also what makes §4.7's audit tractable: the frame and pass groups are the segmentation keys
the redundant-state audit uses to slice a `GLCallLog` into frames and passes. A5 and A7 reinforce
each other, which is why both flags are required together for an audit run.

#### 4.5.5 Debug-context dev mode, and finding C-3

The specification asks for *"KHR_debug labels/groups + debug-context dev mode"*
(`docs/design/v3/DESIGN.md:2548`). Two facts constrain it, and their combination is a genuine
cross-document contradiction that this document reports rather than works around.

**Fact 1 — we cannot count on getting a debug context.** Context creation is OQ-3, owned by Phase 7,
and its designed default is explicit: *"**Failure/fallback.** Make **no context-flag change**"*
(`docs/phase7/v1/PHASE_7_DOC.md:4470`–`:4473`), with a context-hint change adopted *"only if it is
sanctioned by Cleanroom, preserves legacy fixed-function behavior, and passes client startup/resize
on every spike platform"* (`:4466`–`:4468`). PD §16 records that the reference never touches context
creation at all.

**Fact 2 — KHR_debug does not need one.** Object labels and debug groups are ordinary KHR_debug
entry points available on any context that exposes the extension or GL 4.3. The reference proves it:
its gate is the extension or version plus the flag, with **no debug-context condition** —
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/debug/GLDebug.java:291]`,
`if (Boolean.getBoolean("celeritas.enableGLDebug") && (GL.getCapabilities().GL_KHR_debug || GL.getCapabilities().OpenGL43))`.
A debug *context* affects only how much the driver volunteers through the message callback.

**Historical finding C-3 (superseded by P1 D-P1-54; retained provenance).** Phase 1 defined `isActive()` as *"false unless a debug context
and the dev flag are both on"* (pre-D-P1-54 wording; P1 §4.7.8's current gate has no debug-context
prerequisite). Under Phase 7's OQ-3 default
plan there is no debug context, so `isActive()` would be **permanently false** and the entire
`DebugService` — an affordance §G4.5 reserves from day one (`docs/design/v3/DESIGN.md:587`–`:589`)
— would be dead code on every shipping configuration. **Ruling:** the two documents disagree, and
KHR_debug's own capability model settles it — labels and groups do not require a debug context. §5.3
raises R-P14→P1-1 to redefine the gate. The design proceeds on the corrected gate and records the
dependency:

**`D-P14-14` — the gate is `(hasExtension("GL_KHR_debug") || atLeast(4,3)) && -Dschmaloogium.debug.glLabels`.**
A debug context is an *enhancement*, never a precondition.
P1 §§4.7.8/5/D-P1-54 now grants this rule; this receiver adopts actual installed backend/
entry-point availability, false before install and unsupported-profile no-GL behavior too.
Neither a context flag nor an empty driver callback is compatibility/error-free evidence.

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
(`docs/phase1/v14/PHASE_1_DOC.md:4994`), so a developer who enables labels is choosing a frame-rate
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
*"No steady-frame texture/FBO allocation occurs"*
(`docs/phase5/v1/PHASE_5_DOC.md:3093`–`:3098`), Phase 6's *"Steady state must allocate nothing"*
(`docs/phase6/v1/PHASE_6_DOC.md:2066`), Phase 7's *"Dumb hook dispatch allocates no incidental
collections/strings"* (`docs/phase7/v1/PHASE_7_DOC.md:4214`). A6 verifies those three claims rather than
trusting them, and a violation is a **finding against the owning phase**, not a Phase 14 work item.

#### 4.6.2 The measurement procedure

Run on Phase 2's fixed scenes, which Phase 7 requires to be deterministic
(`RUN-SCENE-SELFCHECK`, *"with repeated frames identical"*, `docs/phase7/v1/PHASE_7_DOC.md:4311`).

1. **Preconditions.** `RUN-SCENE-SELFCHECK` green; no `-Dschmaloogium.debug.*` flag set (both
   `recordGL` and `glLabels` change the `glGetError` cadence and would corrupt timing —
   `docs/phase1/v14/PHASE_1_DOC.md:4993`–`:4995`); a pinned JVM and a pinned loader per Phase 1's
   version discipline.
2. **Runs.** For each scene × each of {shaders off, internal pack, one classic matrix pack at T1}:
   three runs of 900 frames, discarding the first 300 as warm-up; report the median run.
3. **Instruments.** JFR with `jdk.ObjectAllocationSample` (allocation by call site),
   `jdk.GCPhasePause` (pause distribution), `jdk.ExecutionSample` (CPU profile), and a frame-time
   histogram. JFR is chosen because it is in-JVM, low-overhead, and needs no dependency; Phase 7
   already names JFR for its own hook overhead work (`docs/phase7/v1/PHASE_7_DOC.md:577`).
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
  activation because App D's cadence contract says so (`docs/phase6/v1/PHASE_6_DOC.md:2239`,
  `docs/research/v1/RESEARCH.md:1381`). They will appear in every profile as repeated GL traffic.
  They fail test 4 and are permanently out of bounds.
- **Everything-refreshes-on-program-switch is not a target** for the same reason
  (`docs/research/v1/RESEARCH.md:1380`).

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
(`docs/phase1/v14/PHASE_1_DOC.md:4982`). Vanilla's own rendering never reaches the facade — Phase 1
records that GL traffic exists which the facade never sees, which is exactly why its error attribution
is window-scoped (`docs/phase1/v14/PHASE_1_DOC.md:5508`). Therefore **vanilla GL churn is invisible
to this method by construction.** The §1.2 non-goal is not a rule the auditor must remember to obey;
it is a property of the instrument. A second filter drops any record whose subject handle is not
Schmaloogium-owned or Schmaloogium-borrowed, catching the one boundary case: ordinary foreign
textures, which Phase 1 makes *"bind-and-label-only"* (`docs/phase1/v14/PHASE_1_DOC.md:3234`–`:3235`) and
which we bind but do not own.

#### 4.7.2 The procedure

1. **Capture.** One deterministic Phase 2 fixed scene, with **both** `-Dschmaloogium.debug.recordGL`
   and `-Dschmaloogium.debug.glLabels` set. ≥120 consecutive steady frames after warm-up. The log is
   a bounded ring, default 100 000 calls, and over-capacity discards are counted
   (`docs/phase1/v14/PHASE_1_DOC.md:4982`) — **a run whose discard count is non-zero is invalid** and
   must be re-taken with a larger ring or fewer frames. This is stated because a silently truncated
   log would produce confidently wrong counts.
2. **Segment.** Split the log into frames and passes using A5's debug groups as the segmentation
   keys. This is why both flags are required together (§4.5.4) and is the concrete reason A5
   precedes A7 in the implementation order (§12).
3. **Determinism gate.** Any two steady frames of the same scene must produce **identical** classified
   sequences. If they do not, the scene is not steady and the audit is invalid — stop and fix the
   scene. Phase 7's `RUN-SCENE-SELFCHECK` already asserts frame identity at the image level
   (`docs/phase7/v1/PHASE_7_DOC.md:4311`); this is the call-level analogue and is strictly stronger.
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
| Backend-only A1 leaves facade `setParameters` calls unchanged; native redundant-identical object writes may decrease | D-P14-31 requires complete object baseline even with samplers; measure actual traffic by §8.2 | no native reduction means no measured win; never omit nonredundant baseline writes |
| Shadow filter set/restore calls and required native object updates remain until P5 separately adopts H-P14→P5-1 | P5 owns the sequence; sampler use alone cannot remove required baseline changes | no facade/native elimination claim from an unadopted simplification |
| Sampler-integer uploads are already near zero | Phase 6 predicts *"Sampler integers usually skip after the first activation of a linked program"* (`docs/phase6/v1/PHASE_6_DOC.md:2082`–`:2083`) | a finding **against Phase 6**, routed by §11.5 |
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
| `GlModernizationPlan` / `SamplerTier` / `DsaTier` / `DebugTier` / `AsyncCompileTier` / `AsyncReadbackTier` | immutable `:engine` record of the five tiers actually selected plus an immutable ordered list of rationale strings; derived by a **pure** static function of `GLCapabilityProfile` + `GlModernizationPolicy`, once, at bring-up, and never mutated for the life of the GL context | diagnostics; Phase 12 for display; Phase 2's run manifest — **proposed only**: environment-fact consumption requested here but not adopted — P2's current bytes define no such manifest field and record no granted request |
| `GlModernizationPolicy` | per-row `AUTO \| FORCE_ON \| FORCE_OFF`, five rows (sampler, DSA, debug, compile, readback); sourced from the mod config owned by `mod.core`; every row defaults to `AUTO`, and `AUTO` resolves to the reference-faithful path until that row's ledger entry closes (`D-P14-17`) | `mod.core` config; Phase 12 GUI if it chooses to surface them |
| `SamplerKey` and `SamplerKey.of(TextureParameters)` | the pure derivation function; the whole of A1's behavioral-equivalence obligation is in this one function, which is why it is exposed rather than hidden — it is the thing §8.1's equivalence test asserts over | `:engine` tests; Phase 5 and Phase 13 as the authors of the inputs |
| `CompileExecutor` / `CompileBatchToken` / `CompileBatchStatus` | **Proposal only:** submit/poll after explicit P4 prepare/worker/link and P7 resumability adoption (§5.9/§5.4); no current caller may infer an async compiler grant | Phase 4 owns compiler products; Phase 7 owns the transaction; P14 owns optional executor mechanism |
| **The `CenterDepthSource` implementation** | Phase 6's existing SPI, synchronous until R-P14→P6-1 grants sample age and the experiment passes; proposed native A3 incorporates §4.3.3/D-P14-35/38 state restoration, successful-transfer/map/unmap-only publication and completion-based warm-up with §6 timeout/demotion | Phase 6 through Phase 7 transaction step 2 |
| **The `DebugService` implementation** | not a new type: `mod.glue` implements P1's existing interface; D-P14-40 retains labels at logical create/update and emits only after exact-target native materialization (known2D depth initialization included), never on a reserved nonobject. §4.5.2–3/D-P14-36/37 retain UTF-8 payload ≤GL_MAX_LABEL_LENGTH−1 and exact real/virtual group draining | every labeling phase; P7 group call sites |
| **Internal texture lifetime law (D-P14-40/41)** | Existing facade only: targetless logical create; first admitted exact-target allocate/native materialization; known2D first depth initialization; preflight unchanged; failed native storage unusable and accounted until owned cleanup; unmaterialized delete has no native delete. Edit/copy neutrality excludes deletion: affected bindings become zero with cache cooperation, unrelated state preserved, no deleted-name restoration/resurrection, existing lease/retirement drains unchanged | P1 backend; P5/P13 resource owners; P2 native evidence |
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
| `D-P6-1` and the not-obviated statement (`docs/phase6/v1/PHASE_6_DOC.md:2230`, `:1184`–`:1187`) | resolves A3's conditional status (§3.4) |
| `CenterDepthSource`, `CenterDepthRequest`, `CenterDepthResult{Sample, Unavailable}` with their exact validation rules (`docs/phase6/v1/PHASE_6_DOC.md:1825`, `:716`–`:786`) | A3 implements this SPI in `mod.glue` in both tiers; the async form consumes it **as it exists** — no type change is required for the mechanism (`D-P14-8`) |
| The §6 degradation row *"center-depth dimensions/FBO unavailable | 2a | retain previous smoothed depth"* (`docs/phase6/v1/PHASE_6_DOC.md:2036`) | the designed meaning of A3's warm-up and invalidation `Unavailable` returns |
| `FixedSamplerResolver` injected immediately after `UniformConfiguration`; `Ready(List<ResolvedSamplerBinding>,policy)` / `Invalid(SamplerLayoutValidation)` | P6 consumes P5's sole resolver, not an independent map. Ready rows are exact name/full shape/unit; P6 only locates/uploads integers using the same effective layout/stage/band/policy as preceding P5 binds, preserving its cache and activity-token rules |
| `UniformConfiguration`'s exact half-lives in ticks, incl. center depth (`docs/phase6/v1/PHASE_6_DOC.md:657`) | the `h` in §4.3.6's criterion C2, so the tolerance is derived from the pack rather than invented |
| `centerDepthMacroContributor()` always `MacroContribution.Empty` (`docs/phase6/v1/PHASE_6_DOC.md:1826`) | confirms no macro-level redirect exists; A3 is the only async path |

**R-P14→P6-1 — contract the sample's age.** *Requested, not assumed.* Phase 6 states *"Phase 6 …
does not hide the cost with an uncontracted one-frame queue"* (`docs/phase6/v1/PHASE_6_DOC.md:2098`).
`CenterDepthResult.Sample(float depth)` carries no frame identity and Phase 6 imposes no echo
requirement on it (`:782`–`:785`), so a `mod.glue` implementation *could* silently return a stale
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
is. It is the outstanding owner-permission blocker; the separate C1–C4 evidence gates also remain.

### 5.4 Consumed from Phase 7 — ten-step baseline and optional requests

Current §§4.1/5.3 are the owner contract. Coordinated changed bytes remain unverified; initial-build
round-32/33 predictions in §0.3 are not current authority.

| Phase 7 contract | Use here |
|---|---|
| `FrameDriver` / `FrameHookSink` and the `enter`/`exit` scope pair (`docs/phase7/v1/PHASE_7_DOC.md:624`–`:625`, `:2631`–`:2641`) | the boundaries A5's debug groups bracket, and A7's segmentation keys |
| `FrameExitKind { NORMAL, EARLY_RETURN, THROWN }` and `FrameAbortReason`'s five current values — `PROTOCOL_REJECTION`, `BACKEND_FAILURE`, `RESIZE_EPOCH`, `WORLD_CHANGE`, `HOOK_UNHEALTHY` (`docs/phase7/v1/PHASE_7_DOC.md:2652`–`:2655`) | the exhaustive exit set A1's sampler clear (`D-P14-4`) and A5's group drain must cover |
| The `finally`-based finalization guarantee (`docs/phase7/v1/PHASE_7_DOC.md:1710`, `:1718`) | what A1's sampler clear rides so it survives a throw |
| `ShaderReloadController` / `DriverReloadRequest` / `ReloadToken` / `ReloadStatus{Queued, Building, Active, Off, Failed, Unknown}` (`docs/phase7/v1/PHASE_7_DOC.md:2853`, `:2952`–`:2974`) | the pack-switch entry point A4 accelerates; `Building` is the state an async build would occupy |
| Current ten-step §§4.1/5.3 transaction | Step 1 quiesce/drain/freeze and preliminary preferences before load; 2 inactive texture owner/adapters then runtime/provider with P5 resolver; 3 frozen ID inputs, P4 compile and P5 plan/create; 4 compose/revalidate; 5 retire old owners/registrations and publish P4 Ready; 6 adopt actual accepted registry generation; 7 publish P5 with full synchronous resize result; 8 build/validate/register P13 against actual accepted identities; 9 publish ID/invalidate geometry then atomically Active/admit; 10 failure compensation through owner retirement and P5/P4 off |
| Exact ownership and generation rules | Only caller-owned candidates are caller-closed; accepted resources retire through publishers. Old barriers invalidate before P6 retirement and borrowed services outlive retirement. PipelineVersion increments once per final outcome; P4 generation changes independently, so Ready then compensating Off may increment it twice |
| Construction and resize placement | CenterDepthSource installs in step 2; P5 allocation in step 3; P13 texture allocation/upload only in step 8 after accepted generations, never a guessed generation; resize publication/delivery is step 7, new P13 registration step 8 |
| Phase 7's own posture: *"Phase 7 adds no readback"* (`docs/phase7/v1/PHASE_7_DOC.md:4226`) | confirms A3 is the only readback path in the frame |
| OQ-3's default plan: *"Make **no context-flag change**"* (`docs/phase7/v1/PHASE_7_DOC.md:4470`–`:4473`) | the constraint that produces finding C-3 and shapes A5's gate (`D-P14-14`) |

**R-P14→P7-1 — resumability, still ungranted.** After R-P14→P4-1 is explicitly adopted,
permit suspension at step 3 between compiler preparation and a publishable candidate. P7 retains
the frozen configuration/schema/world/resource/hook/policy identities and caller-owned candidates;
P4 retains opaque worker products and their cleanup. `PENDING` permits vanilla only. At `READY`,
revalidate every identity before finishing step 3 and steps 4–9. Failure/superseding intent/world or
resource/resize invalidation cancels the pending build, rejects late results and follows step 10;
no retired atlas or prior registry is restored. Step 8 async upload requires separate P13 staging
adoption. Without either owner grant, run its current synchronous operation. No implicit resumability.
Any separately adopted step-8 suspension must receive D-P14-32's allocation-readiness fence
and main-producer flush before worker bind/upload, then worker completion fence/flush/publication.
Both waits stay nonblocking on render admission; P13 retains both sync inventories and upload
ownership through cancellation/late completion or quarantine (§5.9). P7 must not infer
readiness from enqueueing, token completion alone, a later swap, or another context's flush.

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

**R-P14→P13-2 — value handoff and P1 D-P1-52 complete conversion adopted; execution/equivalence gates remain.**
Consume exactly `TextureParameterSpec(MinFilter minFilter,MagFilter magFilter,WrapMode wrap)`
and `TextureParameterFingerprint(String value)` from the validated candidate/source metadata.
The owner-published closed domains are:
`MinFilter { NEAREST, LINEAR, NEAREST_MIPMAP_NEAREST, LINEAR_MIPMAP_NEAREST,
NEAREST_MIPMAP_LINEAR, LINEAR_MIPMAP_LINEAR }`,
`MagFilter { NEAREST, LINEAR }`, and `WrapMode { REPEAT, CLAMP_TO_EDGE }`.
Map min/mag one-to-one with no mip-mode reduction. Wrap applies S for 1D, S/T for 2D and
RECTANGLE, S/T/R for 3D. RECTANGLE requires CLAMP_TO_EDGE and non-mipmap minification;
an incompatible spec is rejected by owner target compatibility, never silently normalized.
**P13 policy/identity receiver receipt — 2026-09-07:** adopt D-P13-27 and the complete
§4.3.5 parser/recovery/legality plus §4.5.1 fingerprint semantics incorporated by P13 §5.
PNG baseline is NEAREST/REPEAT, raw LINEAR/CLAMP_TO_EDGE, noise override LINEAR/REPEAT.
Omitted fields preserve that baseline; explicit blur/clamp booleans override independently.
Malformed/unreadable sidecars atomically reset both fields with one warning and outcome digest;
generated noise is unchanged. Consume owner-computed `phase13.parameters/v2`, never relabel
v1, recompute defaults or reparse sidecars/properties. Integer LINEAR and RECT repeat/mipmap
are rejected, including incompatible baselines, not silently normalized. P13 §6 supplies the
closed PARAMETERIZATION_UNSUPPORTED failure. P1 D-P1-52's mandatory synchronous mapping
is now adopted/unverified, separate from optional sampler-object execution.
Unknown borrowed modes remain outside interning and use sampler 0, not a fabricated spec.
The P13 triple is not automatically the complete P1 value. Adopt D-P1-52's owned-source
conversion and exact admitted mip extent: neutral NONE/LEQUAL, zero border, LOD -1000/+1000,
bias0/aniso1, base0/max admitted last mip, IDENTITY color swizzle; canonical unused wrap axes.
Derive from the complete authenticated accepted value, never three values plus guessed defaults.
This also covers borrowed objects: actual authenticated foreign parameters only, no mutation to
make requests compatible, no invented compare/default state. P5 logical-buffer mipmaps cannot
overwrite a custom/foreign replacement's parameters.

**Publication/lifetime consumed exactly.** `TextureSystemFactory.create` returns inactive
`Created(TextureSystem)|Failed(TextureFailure)`. `plan` is `Planned(TexturePlan)|Invalid`;
`build(TextureBuildRequest(planRequest,sources))` is synchronous `Ready(TexturePublication)|Failed`,
validating matching prepared payload/catalog source/content/target/parameter/epoch before GL.
P7 builds in step 8 with accepted registry and estate generations and registers against accepted
sizing. Immutable publication/candidate views are non-owning; P14 never acquires deletion authority.
`TextureLeaseSource.lease(expectedPublication,selection,currentBaseBinding)` returns
`Acquired(lease)|Rejected(reason)`, where the third argument is P13 §5.1's opaque current
`AtlasBindingEvidence` supplied by the P7 observer; the removed two-argument form is not a P14
fallback. **D-P14-43:** receive P13 D-P13-43's closed reason set unchanged, including
INVALID_BASE_BINDING and STALE_BASE_BINDING, and treat every rejection as no lease and no
modernized path. P14 never fabricates, caches or defaults evidence, and never derives an atlas
association from extent, resource string or native name. The P5 Bound-only closure rule applies.
Retire immediately prevents new leases and makes `isCurrent()` false; currentness additionally
requires the latest authenticated base serial, so a changed base invalidates an open lease and
is refreshed only by P5 through P7/P8 before the next draw, never by a P14 rebind or sampler
re-parameterization. Outstanding leases delay reverse-order owned deletion, not stale-use rejection.
Close never blocks the render thread; borrowed references are released, never deleted. Same-owner
reuse requires complete equal source/parameter identity and accounting across every live/retiring
publication; equal hashes never grant cross-owner reuse or revive an old publication.

**R-P14→P13-1 — stageable upload remains ungranted.** The missing extension is separation of
prepared bytes from bindable completion, not missing texture interfaces. Request render-thread
storage allocation and parameter establishment followed by allocation-readiness fence and main
producer flush; worker observes readiness before bind/upload of immutable prepared payloads,
then completion fence → worker flush → publication and opaque nonblocking render polling.
No binding/animation before full readiness and identity revalidation. P13 owns both syncs'
cancel/late-completion cleanup and quarantine under §5.9 and P7 frozen transaction identities.
Current synchronous build remains baseline. If this extension is
declined, an independently granted compile optimization may proceed but atlas upload stays synchronous.

**D-P14-34 — reciprocal mandatory allocation receipt (2026-09-08, unverified).**
Receive P1 §4.7.7a/D-P1-63, P5 §4.2/D-P5-42 and P13 §4.3.3/D-P13-36:
shared target-bearing ColorTextureSpec/DepthTextureSpec, TextureData(target,region,mipLevel,
layout,texels), TextureAllocationTarget, TextureExtent/Region and disjoint PixelLayout.
DSA/bind-to-edit strategies preserve exact 1D/2D/3D/RECTANGLE target dispatch, fixed owned
target identity, canonical unused axes, mip-count/level/region bounds, P5 closed format/
transfer legality and checked exact scalar-component versus packed-word byte lengths.
Storage definition is not initialized-content proof. Honor tight unpack state, synchronous
borrowed-buffer lifetime without cursor/content mutation, native byte interpretation without
Java-cursor endian conversion, and finally restoration of active unit/target binding/unpack
PBO/settings. Invalid/unsupported preflight mutates nothing; native/restore failure never
publishes usable contents. Existing 2D depth initialize/copy, actual formats, source permissions
and no borrowed-destination mutation remain unchanged. P13 owns exact every-source conversion
and immutable payload lifetime; P14 neither passes its planning sum to allocate nor invents
default formats/targets. This mandatory synchronous value receipt grants no worker API.
The lifecycle is D-P14-40/41 (§4.2/§5.1): this receipt does not schedule native creation
or labeling at targetless `create`, assume failed allocation produced no object, or promise
binding neutrality for deletion. Current synchronous cleanup and deferred retirement apply.
P1 D-P1-64/P5 D-P5-43/P13 D-P13-38 reciprocally receive D-P14-31's baseline law; P13 §5.5
receives D-P14-32 only as a condition on future upload adoption. P7/P2/Main receipts and
Phase 1's §5 already names **14** among the consumers of `GLCapabilityProfile`

**R-P14→P13-3 — inventory covered.** Noise, companion and all custom source forms use the same
parameter/source/lease discipline; no alternate interning route or separate texture owner is added.

### 5.6 Requests against Phase 1 (not a declared dependency; read narrowly per §0.2)

Phase 1's §5 already names **14** among the consumers of `GLCapabilityProfile`
(`docs/phase1/v14/PHASE_1_DOC.md:5510`) and of the GL-error surface (`:5508`), and its §1 scope-out
table already assigns this phase *"KHR_debug labels/groups, sampler objects, async compile, GC
posture"* (`:1726`). Four requests follow; none blocks a row outright.

| ID | Request | Basis | If declined |
|---|---|---|---|
| **R-P14→P1-1** | **Fulfilled/adopted, unverified:** P1 D-P1-54 supported installed GL4.3/KHR backend + glLabels, false before install; no debug-context prerequisite | KHR_debug rev17 permits labels/groups in nondebug contexts; message volume remains implementation-dependent | Preserve unsupported/disabled no-GL behavior; fresh review and real labelled capture still required |
| **R-P14→P1-2** | **Fulfilled/adopted, unverified:** P1 D-P1-54 exact eight pure engine.gl values and mod.glue.gl helpers; existing Lwjgl3GLDevice remains mod.glue | Keeps existing owner/type/module seam; no parallel device implementation | Placement grant does not imply optional operation, worker or compiled-expression permission |
| **R-P14→P1-3** | **Fulfilled/adopted, unverified (2026-09-08):** P1 §§4.7.7/5/D-P1-52 complete TextureParameters, exact P13 conversion and sampler/object split; §4.1.2/§5.5 adopt all fields and validation | Lossless state derivation remains mandatory; type grant is not A1 execution/equivalence proof | Unknown foreign/incomplete state uses sampler0; legal synchronous owned state is required independently |
| **R-P14→P1-4** | (a) Thread-safety, or a documented off-thread mode, for `RecordingGLDevice` — Phase 1 explicitly invites this: *"it is not made thread-safe to cover the Phase 14 exception. If off-thread uploads ever need recording, that is a Phase 14 request against this document"* (`docs/phase1/v14/PHASE_1_DOC.md:5626`). (b) One new flag in the §4.9.3 namespace: `schmaloogium.debug.glContext`, owner P14, milestone v0.5, gated on OQ-3. (c) Optional: an `AutoCloseable DebugService.group(String)` scoped form | (a) A4's worker issues GL through the facade; (b) §4.5.5; (c) §4.5.3 | (a) the worker bypasses the recorder and off-thread calls are simply not recorded — A4 loses recorded-log coverage of the worker, nothing else; (b) the debug-context tier is dropped

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

The proposed worker operation set includes producer-context flush after each completion fence.
Result publication transfers ownership only on successful queue acceptance; rejected/canceled
completion remains inventoried by its producer until P4/P13's acknowledged disposal protocol
accepts it. Logical FAILED is not physical disposal. §4.4.2 rule 5's quarantine retains batch,
fence, upload buffer and object ownership even across failed shutdown, without reuse/deletion.
Context creation/destruction occurs on GLFW's required main thread; destruction additionally
requires worker detachment acknowledgement. These are requirements for future P4/P7/P13 grants,
not callable APIs granted by this document.

**D-P14-32 upload inventory (pending R-P14→P13-1/P7-1, not a compiler API).**
P13's render-thread upload owner retains the texture, immutable payload and allocation-readiness
sync in a per-job inventory from creation through acknowledged cleanup. Successful job enqueue
grants worker use of these references, never deletion authority; rejected enqueue leaves all
ownership with P13 and cannot leak a sync. The worker exclusively inventories its newly created
outgoing completion sync until successful result queue acceptance transfers that sync to P13.
Failed/canceled publication leaves it in worker inventory for acknowledged return/disposal.
Record both syncs independently, including absence when failure occurs before creation.

On success, the worker acknowledges it has finished querying/using the readiness dependency;
P13 deletes that sync exactly once on the healthy render context only after that acknowledgement.
The texture/payload remain retained through outgoing completion and worker quiescence. P13
deletes the completion sync exactly once after signaled nonblocking admission and cessation of
all worker/consumer access; only then may ready publication use the texture. Cancellation
before enqueue needs no worker acknowledgement but still retains unsignaled producer work until
safe completion. Cancellation after enqueue closes admission and terminal-fails the token:
worker acknowledgement must state quiescence and return every created sync/object reference,
including a completion whose publication failed. Render polling then establishes completion
of every issued GL operation before retirement/deletion/reuse. CPU acknowledgement alone
does not prove GPU completion; a timeout alone proves neither. No upload commands may issue
after acknowledged cancellation. Queue rejection, cancellation before/after allocation readiness,
mid-upload, after worker flush and after result acceptance all preserve this same ownership.
Missing acknowledgement or unresolved sync progress quarantines both syncs (when created),
texture, payload, queue and context with explicit accounting, no reuse or deletion and no
automatic synchronous replay in that share group. Context loss follows the same uncertain-
lifetime rule. GLFW destruction additionally requires detached acknowledgement per rule 5.

**Recipient disposition:** P4 §5.7/§11.4 records R-P14→P4-1 as pending, not adopted; its exact
prepare/link/cancel/visibility callable schema must be an owner amendment, not inferred from
this executor sketch. P5 §5.5/§11.5 likewise records R-P14→P5-2 as ungranted.

**Current schema23 receiver receipt — D-P14-33, 2026-09-08 (unverified).** Receive P3's
coordinated CURRENT_SCHEMA_VERSION=23 and MaterializedSource-v23 for existing opaque
identity handoffs and any separately granted future path. D-P14-29's schema22, D-P14-25's
schema21, D-P14-24's schema20 and schema19 receipts are historical, not active admission.
Require configuration, nested IdMappingInput and any received PackDecisionSnapshot schema
to equal P3's exact current constant23 before derivation/reuse; reject22/all other versions
without relabeling, reconstruction or synthesized fields. Range-capable selectors remain
P3-authored values; P9 alone resolves registries, never a P14 second parser/resolver.
Carry P3's exact current configuration/materialization identity opaquely through P4, and
**D-P14-39:** P4 D-P4-43 `RegistryFingerprint/profile-selection-v3` unchanged, committing
accepted selection/evaluated state upstream, not a P14 reconstruction. D-P4-31's own-build-v1
and D-P4-33's positional-route-v2 domains are historical only; old candidates/fingerprints/selectors
are rejected without upgrades. Required ownBuild after sourcePresent remains P4 evidence,
not P14's classifier or permission to alter effective fallback or infer a successful own build.
P3's structural success accepts usable base OR explicit OVERRIDE without merging base roots;
payload-free ScreenProfileEntry() belongs to P12's profiles()/preview-inference route, and
TEXTURE_RECTANGLE maps upstream to existing typed RECTANGLE acquired/uploaded by P13.
P14 neither reparses these tokens nor invents selected-profile data, enums, defaults or
coercions for illegal texture state. D-P14-24's assets provenance/failures and all nine
source-free projectionVersion=1 tree meanings remain unchanged; inspection is metadata only.
This receives identity, not binary acquisition, runtime/timing, inspection or async authority.
R-P14→P4-1 and R-P14→P5-2 remain ungranted, as do other separately optional execution APIs;
fresh owner/receiver reviews, IR-01 and measured runtime gates remain mandatory.

**Schema20 receiver receipt — D-P14-24, 2026-09-08 (unverified).** Adopt P3
§0.62/D-P3-69/§5 for eventual acceptance/inspection and any separately granted prepared
compile path. Earlier current-version assertions and the schema19 receipt below are historical.
Require `configuration.schemaVersion == IdMappingInput.schemaVersion == CURRENT_SCHEMA_VERSION == 20`
before derivation/reuse; reject schema19 and every other version. Required non-null `assets`
after `sources` is the exact same-load P3 capability with the exact containing `PackIdentity`;
foreign-load pairing is invalid even if structurally equal. Carry the owner's metadata-derived
configuration identity unchanged through existing P4/P7/P13 handoffs, without dummy fields,
empty-manifest upgrades, reconstructed capabilities or resource-epoch relabeling.
P14 gains no binary acquisition/sidecar parsing authority or async/inspection API.
P13 remains byte/parameter/recovery owner; only proven optional owned-sidecar-only read
failures survive as UNREADABLE, while P3's other safety/bounds/index/container/source/
configuration failures stay fatal. Source-free inspection requires matching schema20 snapshot
and retains P3's actual ninth canonical manifest metadata section, digest strings via TextHash,
original eight meanings and projectionVersion=1; no bytes/cursors/providers enter it.
Native materialization uses P3's v20 identity through P4; native, vertex, texture, expression
and modernization semantics are otherwise unchanged. Optional grants, fresh owner/receiver
whole-document reviews, IR-01 and runtime evidence remain gates; no earlier PASS covers this edit.

**Schema19 receiver receipt — 2026-09-07:** eventual configuration acceptance/inspection and
any explicitly granted prepared-compile path adopt P3 D-P3-68/§0.61 and §11 migration:
CURRENT_SCHEMA_VERSION=19, including matching nested IdMappingInput, reject 18/all other
versions before derivation or reuse, no synthesized native metadata or old translator result.
P4 retains source materialization/final catalog and synchronous compilation ownership; this
receipt creates no async or inspection API. Source-free P2 projectionVersion=1 remains the
only adopted inspection codec. Existing historical schema receipts remain evidence. Fresh
owner/receiver reviews, IR-01, optional grants and runtime evidence remain required.

### 5.10 Coordinated correction receivers and evidence boundary

**D-P14-27 (C14-2), adopted integration contract, unverified:** P1 declares
`TextureService.prepareUnitBindings(int occupiedUnitMask)` with a validated sixteen-bit set.
P5 calls it only after full successful preflight and before its BoundObject-only bind loop;
P14's backend clears unoccupied native sampler units. P1's `ShaderService.useFixedFunction`
clears all native samplers before fixed-function drawing under the sampler strategy. P4/P7
retain existing terminal release dispatch to that operation. No new frame callback, unit map,
sampler batching grant or mutation on Rejected/Degraded is implied.

P1/P5/P13 receive D-P14-26's complete authenticated owned-state replay before runtime NONE
admission, or existing failure containment/rebuild with retirement accounting unchanged.
Missing safe replay is failure, never a successful resize/retirement acknowledgement.
D-P14-31 additionally requires complete latest authenticated object parameters on every
successful setParameters, alongside cache state, before ordinary sampler-zero FINAL use.
P1/P5/P13 receive that baseline/failure law; P4/P7 retain P5-bind → P4-activate → draw with
no row rebinding, all-unit clearing and unchanged tier on resumed shader rendering.
Clearing is active-unit/object/framebuffer-binding neutral; foreign objects remain unmodified.
P14 owns native driver capture and temporary backend-local fault injection (§8.2); P1's
facade recorder remains unchanged. P2 receives source-free capture/replay manifests and the
separate native versus facade verdicts; P6's optional sample-age grant remains required.
No new P2/P6 public observation API is granted: controlled replay uses existing inputs.
P4/P7/P13's future async proposals must receive producer flush and quarantine/disposal rules
in §5.9 before adoption; P13/P7 upload proposals additionally require D-P14-32's main allocation
readiness fence/flush and worker readiness before bind/upload, with both syncs independently
accounted. Until then synchronous compilation/upload remain authoritative.

---

## 6. Failure modes and degradation

§G2.4's ladder (`docs/design/v3/DESIGN.md:419`–`:449`), applied case by case. Note the shape of this
table: because Phase 14 owns no contract-visible component, **almost every row degrades to "run the
reference-faithful path"** when safe restoration is proven. Failed restoration or uncertain
worker lifetime requires owner containment instead; fallback cannot erase a corruption risk.

| Failure | Detection | Required disposition | Rung |
|---|---|---|---:|
| GL < 3.3, or `ARB_sampler_objects` absent | `GlModernizationPlan.derive` at bring-up | `SamplerTier.NONE`; Phase 5's per-texture parameterization runs unchanged; info diagnostic | — (designed path) |
| Sampler object creation/parameterization fails | owner's error boundary | block affected draws, authenticate/replay complete current owned parameters on all affected live objects, clear then commit NONE; failed replay/clear contains/rebuilds or stays off; §4.1.3 retirement and borrowed restrictions survive | 2a/5 |
| Complete owned object baseline fails under a sampler tier | setParameters owner error boundary | invalidate partial native-state knowledge; admit neither shader nor ordinary fixed-function affected draw; replay/contain under §4.1.7, never publish old cache value as successful restoration | 2a/5 |
| Actual foreign/incomplete sampler state cannot be represented losslessly | §5.5 lossless-conversion gate; owned P13 mapping is granted by D-P1-52 | that unit binds sampler0 and retains owner parameters; already supported estate inputs may use samplers. Never fabricate unknown fields or treat the fulfilled owned-value grant as missing | 2a |
| Sampler clear fails before fixed-function/vanilla use | prepareUnitBindings/useFixedFunction native error boundary | do not admit draw or claim safe restoration; existing owner containment, no enum-only demotion or implied frame finally | 5 |
| GL 4.5 / `ARB_direct_state_access` absent | `derive` at bring-up | `DsaTier.BIND_TO_EDIT`; today's path | — (designed path) |
| A DSA entry point errors at first use | `drainErrors` in the estate-build window | demote the strategy to `BIND_TO_EDIT` for the session, re-run the failed operation, warn once naming vendor and renderer. Demotion is safe because the tiers are behaviorally identical (`D-P14-7`) | 2a |
| Binding neutrality violated by a backend bug | `BindingNeutralityTest` in CI; in the field, vanilla mis-rendering | a **CI-blocking** defect, not a runtime degradation: an incorrect binding cache is the §G4.6 hazard and there is no safe runtime response | 5 (prevented) |
| GL < 3.2 (no fence sync), or PBO creation fails | `derive`, or `drainErrors` at ring construction | `AsyncReadbackTier.SYNCHRONOUS`; Phase 6's `readDepthPixel` path | — (designed path) |
| A center-depth fence never signals | oldest `IN_FLIGHT` slot remains unsignalled at age greater than `N` frames after polling | discard ring and `last`, return `Unavailable` without new issue; third consecutive timeout demotes for the session, next call runs synchronous. Reset retains timeout count; only valid completion clears it. Warm-up can exceed two frames, including `N=3`; no blocking wait or fabricated sample (`D-P14-38`) | 2a |
| Readback setup/transfer/fence/poll/map/unmap fails or decoded depth is invalid | backend-owned error boundary, valid fence/map checks, unmap boolean and finite `[0,1]` validation | never publish failed candidate; restore saved pack layout and bindings on every exit, invalidate ring/last and return `Unavailable`; safe cleanup permits session demotion, failed restoration requires containment (`D-P14-35`) | 2a/5 |
| Generation, world epoch, framebuffer extent or pixel changes | step 2 of §4.3.3 | discard the whole ring; return `Unavailable`; Phase 6 *"retain[s] previous smoothed depth"* per its own row (`docs/phase6/v1/PHASE_6_DOC.md:2036`) | 2a |
| Framebuffer dimensions ≤ 0 | step 1 of §4.3.3 | `Unavailable` with no GL call — Phase 6 requires exactly this (`docs/phase6/v1/PHASE_6_DOC.md:782`–`:784`) | normal |
| R-P14→P6-1 not landed | design-time | `PBO_FENCE` is not enabled; `SYNCHRONOUS` ships. Shipping a silently-stale value is refused, not risked | — (policy) |
| Shared context cannot be created, or the driver is not allowlisted | §4.4.3 stages 1–2 | `AsyncCompileTier.INLINE`; **no user-visible error** — this is a supported configuration | — (designed path) |
| Worker probe/batch/watchdog failure | §4.4.3 | terminal-fail, stop admission; acknowledged quiescence/detachment enables cleanup and healthy-context INLINE recovery; otherwise quarantine uncertain jobs/context/objects, no replay or successful shutdown | 2a/5 |
| Upload allocation-readiness fence/flush/poll fails or never signals | main producer/worker readiness boundary | no worker bind/upload before readiness; terminal failure and §5.9 dual-sync acknowledged cleanup or quarantine; outgoing completion cannot repair the missing incoming edge | 2a/5 |
| Worker unexpected exception | worker boundary | contain and attempt cooperative detachment acknowledgement; exception alone proves neither detachment nor object safety; quarantine absent proof | 5 |
| GL context loss with worker running | context-loss boundary | invalidate old identities, retain uncertain worker ownership until acknowledged teardown or process termination; never destroy a context current elsewhere | 5 |
| KHR_debug absent, or `glLabels` unset | `derive` | `DebugTier.NONE`; a no-op `DebugService`. This is every shipping configuration | — (designed path) |
| A debug group push/pop is unbalanced by a caller | backend real/virtual counters | underflow is a no-op; boundary recovery removes virtual entries without GL, then exactly actual outstanding pushes with native pops; one rate-limited diagnostic, including leaked overflow plus abort (`D-P14-37`) | normal |
| `glObjectLabel` errors (invalid name or backend/driver failure) | existing backend-owned debug error boundary under per-call cadence | legal UTF-8 ≤`GL_MAX_LABEL_LENGTH-1` byte clamping happens before every call, never as recovery from an intentionally over-limit call; at most one legal retry, then disable labels for the session. Keep the error attributed to debug, not a uniform | 2a |
| An audit run's `GLCallLog` overflows its ring, or the scene is not frame-identical | §4.7.2 gates 1 and 3 | the audit **reports nothing** and is re-taken. No partial or estimated counts are ever produced | — (procedure) |
| A measurement shows a sibling phase allocating in the frame path | §4.6.2 attribution | a finding routed to that phase through §11.5. Phase 14 does not edit another phase's code or call sequence | — (procedure) |
| A row's ledger entry or spike fails | §7.5, §10 | that row ships its fallback permanently; the result is written back to RESEARCH.md §11's status column by the implementation effort per §G4.4. **A failed row never blocks the milestone** | — (procedure) |
| Any unexpected exception crosses a Phase 14 entry point | the `mod.glue` boundary Phase 1 owns | contained at Phase 1's boundary, shaders disabled, vanilla path resumes. No Phase 14 code path throws into the client | 5 |

Two properties of this table are worth stating explicitly, because they are what make a performance
phase safe:

1. Automatic demotion admits baseline drawing only after proven state restoration. Unknown
   borrowed-state exclusion is not permission to leave partially restored owned texture state.
2. Uncertain native lifetime is contained rather than hidden: a failed pack switch or retained
   quarantined resources may be observable. No successful shutdown/no-leak claim follows from
   timeout; healthy vanilla continuation cannot be guaranteed against a wedged driver.

---

## 7. Threading and performance notes

### 7.1 Thread ownership per component

| Component | Thread | Note |
|---|---|---|
| `GlModernizationPlan.derive` | any; pure | no GL, no state; runs at bring-up from a value |
| `SamplerCache` interning, binding, clearing | **render thread only** | inherits `Lwjgl3GLDevice`'s confinement |
| `DsaStrategy` | **render thread only** | same |
| `CenterDepthReadback` (submit, poll, map, ring discard) | **render thread only** | Phase 6 already requires it: *"`UniformPlatformProvider` / `CenterDepthSource` production implementation | render thread only"* (`docs/phase6/v1/PHASE_6_DOC.md:2054`). Async here means *asynchronous GPU transfer*, not another thread |
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
  `glGetError` cadence (`docs/phase1/v14/PHASE_1_DOC.md:4993`–`:4995`).
- **A4:** allocates per pack switch — a bounded, non-frame event — and nothing per frame.

### 7.3 Known hot paths, and what this phase does to each

| Hot path | Today | After |
|---|---|---|
| Per-pass fixed-unit binding, 16 units | P5 physical object binds after preflight | conditional sampler binds; only proven redundant object writes omitted while complete baseline is maintained (D-P14-31); native savings must be measured |
| Per-frame center-depth read, when declared | one synchronous `glReadPixels` — a full pipeline stall (`docs/phase6/v1/PHASE_6_DOC.md:2095`) | one non-blocking status query + one 4-byte map + one async `glReadPixels`; no stall |
| Shadow mipmap generation, per shadow pass | filter set → generate → filter restore, with restore-failure containment | unchanged until P5 separately adopts H-P14→P5-1; complete object baseline remains required in every tier |
| Texture and framebuffer creation | bind, edit, restore | DSA where available; fewer binding round-trips and no restore |

**Timing measurement discipline.** Frame-time numbers must be taken with **no** `-Dschmaloogium.debug.*`
flag set. Both `recordGL` and `glLabels` put the device on a per-call `glGetError` cadence
(`docs/phase1/v14/PHASE_1_DOC.md:4993`–`:4995`), which is a synchronous driver query per facade call.
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
| **L-2** | GL 3.3 samplers remove native state churn `[U]` (RESEARCH §6.2) | Historical deployed reference observation in §3.2; no current runtime result | §8.2 native capture before/after A1, separate facade audit, successful use→ordinary FINAL→resumed shader without demotion and separate demotion/containment injection | Measured native reduction with complete authenticated object baseline, actual state/output equivalence and safe fallback required for AUTO-on. No measured win keeps AUTO-off; unadopted shadow simplification is not shipping justification |
| **L-3** | PBO/fence readback one-frame latency and imperceptibility `[U]` (RESEARCH §6.2) | Historical fence-mechanism observation in §3.2, no current native readback evidence | §4.3.6 raw/timing/age replay and separate native/perceptual runs across four scene families | C1/C2 correctness requires diagnosis, not perceptual closure; all C1–C4 plus P6 age grant required for AUTO-on, otherwise synchronous default; record only observed evidence |
| **L-4** | *"**GLFW shared-context async shader compile** … Driver quality for shared compat contexts varies; needs a synchronous fallback"* `[U→OQ-15]` (`docs/research/v1/RESEARCH.md:774`) | **No reference.** The tree never calls `glfwCreateWindow` or `glfwMakeContextCurrent`; PD §16 records that it never touches context creation | this is OQ-15, not a cheap spot-check. §10.1's full spike | Per-driver-family, per §10.1's criteria. Default deny until a family passes (`D-P14-12`) |
| **L-5** | *"KHR_debug labels/groups + debug context … Dev-only; pairs with RenderBook's Nsight integration"* `[V:web]` (`docs/research/v1/RESEARCH.md:775`) | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/debug/GLDebug.java:291]` — gated on extension-or-version plus a flag, **no debug context** | capture one frame in RenderDoc or Nsight with `glLabels` set; confirm named groups and labelled objects appear. One session, no code | Groups and labels visible ⇒ closes confirmed. The **debug-context** half is separately gated on OQ-3 (Phase 7) and is ledgered as dependent, not as ours to close |
| **L-6** | DSA availability and behavioral invisibility (REV1, PD §15) | Historical deployed-tier observation in §3.2; no current backend result | Separate facade trace equality, §8.2 native binding/state checks, and per-tier T1 run | All three evidence layers required for AUTO; any delta retains BIND_TO_EDIT and requires diagnosis; upstream DSA row request remains §11.4 |
| **L-7** | *"Guaranteed `glGenerateMipmap` (GL 3.0 baseline) … None"* `[U]` (`docs/research/v1/RESEARCH.md:770`) | Phase 1 already derives `supportsMipmapGeneration()` as `atLeast(3,0)` and obliges callers to check (`docs/phase1/v14/PHASE_1_DOC.md:2990`) | none: covered by Phase 1's `GLCapabilityProfileDerivationTest` and Phase 7/8's existing mipmap paths | Ledgered as **closed by Phase 1's design**; listed for completeness because A1's mipmap-filter reasoning depends on it |
| **L-8** | *"**Delete the allocation-discipline design constraint** … generational ZGC on Java 25 makes straightforward code acceptable. Write clean code first, optimize with evidence"* `[U]` (`docs/research/v1/RESEARCH.md:784`) | none — this is the highest-value unverified claim in the phase, because the whole §G2.5 posture rests on it | §4.6.2's allocation profile, on the Phase 2 scenes, at v0.5 | Zero steady-state allocation in `com.schmaloogium.*` **and** GC pauses indistinguishable from the shaders-off baseline ⇒ the posture holds and the row closes confirmed. Any violation ⇒ a finding against the owning phase (§11.5), **not** a reintroduction of OF's array-cache machinery, which §4.8 marks **Skip** (`docs/research/v1/RESEARCH.md:645`) |
| **L-9** | *"FFM API for native buffer work … Replaces reflection-into-direct-buffer hacks; useful for **pixel-transfer paths**"* `[U]` (`docs/research/v1/RESEARCH.md:785`) | none; directly relevant because A3's PBO map and A4's atlas staging are exactly pixel-transfer paths | microbenchmark: read one float from a mapped PBO via `ByteBuffer` vs `MemorySegment`/`memGetFloat`, 10⁶ iterations; and stage one atlas both ways | ≥1% of frame time or ≥1 MB/s saved, per §4.6.3 test 2 ⇒ adopt in `mod.glue` only (`:engine` is C-1-constrained). Otherwise ⇒ closes as "no measurable win"; the straightforward `ByteBuffer` path ships |
| **L-10** | *"Vector API for CPU-side math … measure first (incubator churn risk)"* `[U]` (`docs/research/v1/RESEARCH.md:786`) | none | **not relied on by Phase 14.** Its cited uses — per-quad tangent math, frustum plane tests — are Phase 10's | Ledgered as **out of scope, owner Phase 10**. Recorded so the OQ-22 sweep is complete and nobody assumes Phase 14 closed it |
| **L-11** | *"Modern language features … `MethodHandle`/bytecode-compiled expressions for per-frame custom-uniform evaluation"* `[U]` (`docs/research/v1/RESEARCH.md:787`) | **Unmeasured**; P11 interpreter and metrics/SPI are the baseline, not performance evidence | **P14 owns measurement:** execute P11 §§4.11/10.1 exactly, using §5.8 metrics, on at least two supported Java 25 platforms; procedure below in §10.2 S-22-6 | Interpreter meets p95 ≤0.25 ms, p99 ≤0.50 ms and zero steady allocation ⇒ retain it, no compiler. Real-pack miss after local cleanup with material AST dispatch attribution ⇒ conditional P14 backend prototype inside P11 SPI; require ≥2× failing-p95 improvement, budgets, bounded build/memory and exact semantic differential results or reject to interpreter |
| **L-12** | Compute / SSBO / image load-store / indirect dispatch are feasible on the 1.12.2 compat context | `[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/gl/IrisRenderSystem.java]` per PD §15 (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:741`–`:744`): *"present and pack-exercised, on the 1.12.2 compat context. **This is the strongest available evidence that G8/S2 is feasible on Cleanroom.**"* | none in Phase 14 | Ledgered as **feasibility evidence for G8/S2 only** (`docs/design/v3/DESIGN.md:788`–`:793`). Not a Phase 14 work item and not a Phase 14 close |
| **L-13** | *"§2.4 effort estimates"*, the other half of OQ-22's catch-all (`docs/research/v1/RESEARCH.md:1028`) | none | not a GL claim; effort estimates are validated by the implementation effort's own tracking, not by a Phase 14 experiment | Ledgered as **out of scope for this phase**, recorded so OQ-22's full text is accounted for and the verify session can see nothing was quietly dropped |

L-2/L-6 evidence qualification: historical reference receipts are not current backend proof.
L-6's native binding-neutrality/state queries and per-tier draw comparison in §8.2 are required
in addition to identical facade logs and T1 images. L-3 requires recorded replay inputs and
native source-age evidence separately; C1/C2 mismatch is not a perceptual result. L-4 requires
both main-allocation and worker-completion producer-flushed isolated-job progress, plus safe
dual-sync lifetime outcomes and explicit quarantine failure accounting. None of these
experiments has been executed in this correction session.


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
| `SamplerKeyDerivationTest` | `SamplerKey.of` maps Phase 5's colour policy exactly: `CLAMP_TO_EDGE` S/T for every colour texture, NEAREST min **and** mag for integer formats, LINEAR otherwise (`docs/phase5/v1/PHASE_5_DOC.md:1439`–`:1440`); shadow textures map `hardwareFiltering → COMPARE_REF_TO_TEXTURE`, `mipmap → *_MIPMAP_*` min filter, `nearest → NEAREST` (`docs/phase5/v1/PHASE_5_DOC.md:631`–`:632`); equal parameters yield equal keys (structural equality, so interning is deterministic) |
| `SamplerStateSplitTest` | Every P1 field reaches the complete object baseline; only its exact sampler prefix reaches the key. A changed authenticated parameter remains effective under sampler zero, not merely under a sampler object |
| `SamplerEquivalenceTest` | Pure owner-parameter/key model equality plus successful update→ordinary FINAL→resumed shader model and failed update containment; actual native state/output proof remains §8.2 |
| `SamplerCacheLifecycleTest` | Each of all eight resize reasons invalidates bound-unit knowledge; retired identity cannot bind even with outstanding leases/equal hashes; outstanding live/retiring references prevent premature deletion; borrowed texture is never deleted; callback failure preserves exact installed-off/deliveredCount handling rather than falsely reporting SUCCESS |
| `SamplerLeakTest` | Model normalization clears unoccupied rows; fixed-function dispatch precedes every modeled fixed-function draw and terminal release; native zero-state proof requires §8.2 |
| `FixedUnitDisciplineTest` | The bind cache is indexed by App B.3 unit; no code path allocates, reassigns or reorders a unit; `depthtex1` resolves to 11. A regression here would reintroduce the pre-decided divergence at `docs/design/v3/DESIGN.md:953`–`:954` |
| `BindingNeutralityTest` | Model creation/edit/copy preservation only, distinct from lifetime-ending deletion; deleted handles cannot restore/bind and affected modeled bindings become zero without altering unrelated state. Actual tier/cache/zero-state proof requires §8.2 native queries |
| `TextureMaterializationLifecycleTest` | Targetless create/label update performs no modeled native creation/label; first admitted allocation uses each exact 1D/2D/3D/RECT target and labels only after object exists; known2D depth first initialization follows the same law. Preflight rejection leaves no name; deletion before materialization has no native delete; failure after name creation retains cleanup ownership, rejects use and deletes exactly once. Native proof remains §8.2 |
| `CenterDepthRingTest` | Controlled one-frame-ready schedule returns expected age; `N=3` with no completion on call three remains `Unavailable`, later valid completion ends warm-up. Stuck oldest fence at age >N resets ring/last without issuing that call; three consecutive timeouts demote, resets retain count and valid completion clears it. Failed transfer/map/unmap never yields its candidate; unsignalled poll uses prior valid `last` or `Unavailable`. Nonpositive dimensions touch no GL; all async polls are nonblocking, with no finish or positive-timeout wait. Model evidence only; actual native state/error proof is §8.2 |
| `CenterDepthInvalidationTest` | Each of `registryGeneration`, `worldEpoch`, width, height, `pixelX`, `pixelY` changing independently discards the whole ring and returns `Unavailable`; **no value from a prior world, generation or extent is ever returned** (`D-P14-10`) |
| `CenterDepthTraceComparisonTest` | Given two recorded traces from a scripted depth sequence, the analytic criteria C1 and C2 of §4.3.6 are computed and asserted headlessly. The *judgement* is thereby testable without a GPU; only the *capture* needs one |
| `CompileExecutorContractTest` (only after P4/P7 grant) | Completed units preserve deterministic source order and P4 failure/fallback semantics; failed/canceled/stale batches cannot publish, late completion disposes exactly once after worker acknowledgement, and publication remains render-thread. Worker submit/poll is non-waiting; Inline may compile synchronously in submit |
| `CompileFallbackLadderTest` | With controlled logical time, timeout terminal-fails without deletion or successful shutdown; late publication rejected; cooperative acknowledgement permits exactly-once cleanup, absent detachment retains ownership; INLINE retry requires safe healthy-context teardown |
| `UploadOrderingContractTest` (only after P13/P7 grant) | Unsignaled allocation dependency prevents worker bind/upload; unsignaled outgoing completion prevents render admission; independently canceled/rejected/late results retain both sync inventories until quiescence and GPU completion, with exactly-once cleanup or accounted quarantine |
| `DriverPolicyTest` | An unknown vendor/renderer resolves to `INLINE` (**default deny**, `D-P14-12`); an allowlisted family resolves to `SHARED_CONTEXT`; `FORCE_OFF` overrides an allowlist entry; a denylist entry overrides `FORCE_ON` |
| `DebugGroupBalanceTest` | Across NORMAL/EARLY_RETURN/THROWN and abort: underflow is a no-op plus one diagnostic; matching virtual pops issue no GL. Explicit real-capacity pushes → multiple leaked virtual pushes → throw/abort drains virtual entries with zero native pops, then exactly outstanding real pushes, with one boundary diagnostic. Both counters reach zero; next balanced frame preserves default native group, and native pops never exceed actual pushes |
| `DebugLabelCoverageTest` | Estate labels originate at logical create and emit only after actual native instantiation per D-P14-40, with pre-materialization updates retained. UTF-8 max−1/max/over-max payloads submit ≤GL_MAX_LABEL_LENGTH−1 bytes without splitting code points; truncation need not preserve uniqueness. Native timing/no-error proof requires §8.2 |
| `DebugInactiveIsFreeTest` | Under `DebugTier.NONE`, the recorded log contains **zero** debug records for a full frame of pushes, pops and labels |
| `AuditClassifierTest` | Given a synthetic `GLCallLog`: redundant-identical, redundant-restore, necessary and contract-mandated are classified per §4.7.2 step 4; matrix uploads and the program-switch sweep always land in contract-mandated and never in a ranking; a log with a non-zero discard count is **rejected as invalid** (gate 1); two non-identical steady frames **fail** the determinism gate (gate 3) |
| `AuditScopeFilterTest` | A synthetic log containing foreign-handle records has them dropped by the ownership filter; the classifier's output cannot contain a non-Schmaloogium subject (`D-P14-16`) |
| `Schema23IdentityGateTest` | Exact P3 current constant23 and matching nested/inspection schema with MaterializedSource-v23; reject22/all older identities without upgrade; carry current P4 profile-selection-v3 opaquely and reject stale composition identities |
| `Schema23OwnershipBoundaryTest` | Nine metadata-only projectionVersion=1 trees, assets/native/options/parameter domains remain unchanged; range selectors and forced11300Rules stay P3/P9-owned, never P14 parsing or async authority |

### 8.2 Recorded-GL and profile-fixture tests

`RecordingGLDevice` plus serialized `GLCapabilityProfile` fixtures (`docs/phase1/v14/PHASE_1_DOC.md:5511`,
`:4982`) carry the integration-shaped assertions that still need no driver:

- Identical facade scripts yield identical facade traces across backend tiers. Recorder fixtures
  prove facade dispatch, synthetic state and error responses only, not backend native behavior.
- **Native procedure (D-P14-28):** run the actual LWJGL backend on a pinned compat context.
  Use driver API capture (for example apitrace) with P14-owned object labels/creation inventory
  and deterministic pass boundaries to count actual glTexParameter*, glBindSampler/glBindSamplers
  and DSA/bind-to-edit calls, excluding vanilla calls. Query native texture parameters and unit
  sampler bindings before/after operations and draw the same texture through each tier; record
  source-free state/count/hash summaries. Keep raw driver captures local, not repository artifacts.
  Incomplete capture or unattributable calls invalidate native counts; do not estimate from facade logs.
- In a temporary backend-local development harness, inject failure at native sampler allocation/
  parameterization and parameter replay/clear boundaries after successful sampler-backed use;
  exercise actual backend transition code, not RecordingGLDevice's scripted glError response.
  Assert authenticated NEAREST/CLAMP_TO_EDGE (and all admitted P1 fields) on restored texture
  objects before NONE draws, or no affected draw when restoration fails. Borrowed objects remain
  unmodified; retirement stays accounted. Also exercise DSA failure/binding neutrality natively.
- A2 native lifecycle cases, under every supported DSA tier: create each admitted
  1D/2D/3D/RECT target via targetless logical create followed by allocation; capture no
  premature name/label, exact target materialization and label-after-object ordering.
  Include first known2D depth initialization, invalid preflight, never-allocated deletion,
  and failures before/after native name creation and during storage definition. Require
  no use of failed storage, no delete for absent names and exactly-once owned cleanup
  for created names. Separately query bound-texture deletion across multiple units and
  bound read/draw-FBO deletion: affected bindings/cache become zero, unrelated state and
  active unit survive, subsequent cooperating binds work, and no deleted name is restored.
  Exercise retirement with outstanding leases, then drain after release; no premature
  delete or borrowed delete. These deletion side effects are required, not neutrality
  failures. Record only source-free state/count summaries; this is future evidence.
- Exercise successful sampler-backed shader use → ordinary fixed-function FINAL of P5's frozen
  owned colortex0 → resumed shader rendering, without failure injection, demotion or tier change.
  Repeat after a successful authenticated parameter update. Query complete object parameters
  and effective sampling state before the clear/draw; require owner-equivalent filter/wrap and
  every admitted field, zero samplers on all sixteen units, unchanged active-unit/object/FBO
  bindings and output equal to the baseline. Confirm next P5 bind restores the correct sampler
  before resumed shader activation; no extra row rebind may mask an incorrect clear.
  Also cover declaration-empty fixed-function and every terminal release, unknown cache state,
  borrowed-object nonmutation, partial parameter-update/clear failure with no affected draw,
  unoccupied-unit clearing and mutation-free Rejected/Degraded preflight.
  MULTI_BIND mechanics are measured only after separate grants; do not predict one clear/frame
  when additional in-frame fixed-function transitions require more. No public observation seam
  or extension of P1's facade recorder is assumed by this backend-local experiment.
- A3 native case: enter with nonzero pack-buffer/read-FBO bindings, alignment 8, nonzero
  row length/skip rows and `GL_PACK_SKIP_PIXELS=1`, plus `GL_PACK_SWAP_BYTES=true`.
  Transfer a known non-byte-symmetric finite depth into a four-byte slot, consume it, and
  require correct numeric depth, no readback GL error and exact incoming pack-layout and
  binding restoration. Repeat backend-local setup/transfer/map failure and `glUnmapBuffer`
  false/error injection: no failed candidate becomes Sample; every exit restores state or
  contains without claiming success. Recorder echoes do not prove these native properties.
- A5 native cases: submit UTF-8 labels at max−1/max/over-max encoded byte lengths and a
  multibyte boundary; query legal labels and require no label-generated GL error. Leak
  multiple virtual overflow pushes before THROWN/abort; capture exactly real outstanding
  pops, no default-group underflow, both counters zero and one recovery diagnostic.
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
| Where is real native churn? | §8.2 actual-backend capture for native counts; §4.7.2 separately classifies facade traffic; L-2 never conflates them |
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
| `SamplerCache`, per-unit bind cache, conditional `MULTI_BIND` (A1) | now | **v0.5** | batching remains owner-granted separately; authenticated replay/containment ships with any enabled sampler tier |
| Sampler normalization and fixed-function dispatch (A1, D-P14-27) | now | **v0.5** | prepareUnitBindings after preflight; useFixedFunction before every fixed-function draw/terminal return; no implied frame callback |
| `DsaStrategy` three tiers (A2) | now | **v0.5** | facade-internal; no dependent phase is affected |
| `CenterDepthReadback` PBO+fence ring (A3) | now | **v0.5** | gated on R-P14→P6-1 **and** §4.3.6's criteria; ships `FORCE_OFF` otherwise |
| Synchronous `CenterDepthSource` (A3 fallback) | — | **v0.1, by Phase 6** | already designed and staged by Phase 6 (`docs/phase6/v1/PHASE_6_DOC.md:2206`); Phase 14 adds nothing at v0.1 |
| `CompileExecutor` + `InlineCompileExecutor` (A4 proposal) | now | **after owner grants** | R-P14→P4-1 and R-P14→P7-1 first; current synchronous compile remains until then |
| Async `_n`/`_s` atlas upload (A4) | now | **post-v0.5** | additionally gated on R-P14→P13-1 |
| `KhrDebugBackend` — labels and groups (A5) | now | **v0.5** | P1 D-P1-54 activity/placement grants adopted, fresh review and actual capture remain |
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
   **D-P14-42 — no implicit gate waiver.** A4 is post-v0.5 and spike/receiver-gated.
   If OQ-15 fails, a required extension is declined, or no measured reduction is available,
   condition 3 remains **unmet/deferred**, even if conditions 1–2 pass. The synchronous
   fallback may safely ship, but a full Phase 14 implementation-gate PASS is prohibited
   until reduction is demonstrated or an explicit governing amendment is adopted through
   the governing revision's adoption procedure. §11.4's pending request is not authority.
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
also relies on for OQ-3 (`docs/phase7/v1/PHASE_7_DOC.md:4447`–`:4449`). This spike has no reference
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
   nonblockingly establish completion of the producer-flushed worker fence, then `glWaitSync`,
   attach, link and validate. Compare, per program: link
   status, the info log, the complete active-uniform list with locations and types, and the active
   attribute list, against a fully synchronous run of the same pack. **Any difference is a failure**,
   not a curiosity.
4. **Rendering correctness.** Render each pack on the Phase 2 fixed scenes **and** the camera-path
   motion scenes, and diff against the synchronous run's T1 baseline. Corruption may be
   intermittent, so repeat each pack-switch 20 times per family and require every run to pass.
5. **Texture upload.** Repeat 3–4 for a companion-atlas-sized upload (two full atlases with mip
   chains) with main allocation → readiness fence → main flush → worker observed readiness
   before bind/upload → worker completion fence → worker flush → publication → nonblocking
   render admission; check pixel equality against synchronous upload and both sync inventories.
6. **Stall measurement.** Instrument the pack switch exactly as §9.2 condition 3 defines: median time
   from accepted request to `Active`, and the count of frames exceeding twice the scene median during
   the window. Measure synchronous and async, three runs each, no debug flags set.
7. **Adversarial cases.** (a) Re-switch pack mid-batch. (b) Change dimension. (c) Resize.
   (d) Cooperatively cancel, then separately simulate an unresponsive native worker without
   killing its thread. (e) Exit mid-batch. Assert bounded logical failure, no late publication,
   acknowledged detachment before destruction, and retained/quarantined ownership absent ack.
   An unresolved quarantine fails family reliability/allowlisting; it is not a successful shutdown
   or an untracked leak. Run hang scenarios in an isolated process; do not force unsafe cleanup.
   Also submit one isolated compile job and one isolated upload, then leave the worker idle.
   For upload, stop all subsequent main rendering/swaps/flushes after the mandatory allocation
   producer flush until worker completion is observed: capture allocation → readiness fence →
   main flush → worker readiness before bind/upload, then completion fence → worker flush →
   publication and nonblocking render readiness without another job, swap or consumer flush.
   Deliberately hold each dependency unsignaled in separate controlled cases; require no
   dependent upload/draw. Inject cancellation/rejected publication before readiness, during
   upload and after completion publication; verify both syncs' §5.9 ownership, acknowledged
   cleanup only after GPU completion, and quarantine when either progress/acknowledgement is
   unavailable. A later swap/flush supplying progress invalidates the isolated-job evidence.
8. **Record** driver versions, OS, GPU, and every result in `docs/decisions/OQ-15_ASYNC_COMPILE.md`,
   in the shape Phase 7 uses for OQ-3 (`docs/phase7/v1/PHASE_7_DOC.md:4441`–`:4473`), including a
   per-family verdict table that becomes `D-P14-12`'s allowlist data.

**(3) Success and failure criteria.** Per family, and **all must hold** for that family to be
allowlisted:

| # | Criterion | Threshold |
|---|---|---|
| K1 | **No corruption.** Link status, info logs, active-uniform lists (name, type, location) and active-attribute lists are identical to the synchronous run for every program of every pack. | exact equality |
| K2 | **No visual difference.** Every scene, static and motion, diffs within Phase 2's T1 tolerance against the synchronous run, with zero new outliers, across all 20 repeats. | zero failures in 20 |
| K3 | **Stall below threshold.** The count of frames exceeding twice the scene median during the switch window is **≤ 20% of the synchronous count**, and the median request-to-`Active` time does not increase. | ≤20% long frames; no total-time regression |
| K4 | **Adversarial safety and progress.** Step 7 proves producer-flushed idle-job completion, safe acknowledged cleanup, and fail-closed accounted quarantine without unsafe destruction; unresolved quarantine disqualifies the family, not a false successful shutdown. | zero unsafe lifetime transitions; no unresolved quarantine for allowlisting |
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
column. Safe synchronous fallback delivery is not blocked by the failed spike; the complete
implementation milestone/gate remains unsatisfied while §9.2 condition 3 is unmet.

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
| **D-P14-3** | Reject dynamic per-program texture-unit allocation; the bind cache is a fixed `SamplerHandle[16]` indexed by App B.3 unit | §G11.4 pre-decided rejection (`docs/design/v3/DESIGN.md:953`–`:954`); a fixed dense map also suits `glBindSamplers` better than a dynamic one |
| **D-P14-4** | Clear all native samplers in concrete useFixedFunction dispatch before drawing and existing terminal releases; prepareUnitBindings clears unoccupied rows | D-P14-27 replaces the historical implied backend frame-finally callback |
| **D-P14-5** | DSA tiering is entirely internal to the `mod.glue` LWJGL3 backend; no `:engine` type names a tier | the spec asks for a facade-*internal* strategy (`docs/design/v3/DESIGN.md:2534`–`:2535`), and internality is what makes it behavior-invisible |
| **D-P14-6** | `bindToUnit` is **excluded** from DSA and stays on the `GlStateManager`-cooperating path, diverging from the reference | `glBindTextureUnit` bypasses state `GlStateManager` caches, which §G4.6 forbids because the stale cache breaks vanilla rendering |
| **D-P14-7** | Creation/edit/copy operations in §4.2.3 preserve bindings; D-P14-41 excludes lifetime-ending deletion | Native queries establish preservation; a deleted binding must become zero, never be resurrected |
| **D-P14-8** | Async center-depth is implemented below Phase 6's `CenterDepthSource` seam, with its added latency **contracted** through R-P14→P6-1, never hidden | Phase 6 explicitly forbids *"an uncontracted one-frame queue"* (`docs/phase6/v1/PHASE_6_DOC.md:2098`), and the type would otherwise permit exactly that |
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
| **D-P14-23** | Adopt P1 D-P1-52/54 exact parameter split/conversion, debug activity gate and package homes; fulfill R-P14→P1-1/2/3 | No optional async/sampler execution, context change or evidence claim follows from a value/placement grant |
| **D-P14-24** | 2026-09-08: adopt P3 D-P3-69 schema20, matching nested IDs and same-load assets/configuration identity in §5.9; preserve the actual ninth source-free metadata section | Supersedes older current versions only; no acquisition/async API, semantic expansion or historical PASS promotion |
| **D-P14-25** | Historical 2026-09-08 schema21/MaterializedSource-v21 and own-build-v1 receipt, superseded by D-P14-29 | Preserve provenance only; no old identity admission |
| **D-P14-26** | Replay complete authenticated current texture-object parameters before sampler demotion; otherwise contain/rebuild | Sampler zero exposes object state, not the retired sampler state |
| **D-P14-27** | Receive prepareUnitBindings(mask) and useFixedFunction dispatch in §5.10, without batching/frame callback self-grants | Covers Unused rows and in-frame fixed-function draws while preserving pure preflight |
| **D-P14-28** | Native capture/fault injection is separate from facade evidence; EMA oracle uses explicit controlled raw/timing/age replay | No native optimization claim or mathematical bound can be derived from absent inputs |
| **D-P14-29** | Historical schema22/current-constant receipt, superseded by D-P14-33; P4 positional-route-v2 remains opaque | No old numeric admission; nine trees/projectionVersion1 and other owner semantics unchanged |
| **D-P14-30** | Producer fence→flush→publish; watchdog fails logically but retains uncertain physical ownership until detachment/quiescence proof | No unsafe context destruction, build replay or successful shutdown inferred from timeout |
| **D-P14-31** | Every successful setParameters maintains complete latest authenticated texture-object baseline alongside sampler cache; ordinary FINAL clears without demotion then shader rendering resumes | Sampler zero exposes object sampling state; only proven redundant writes may be omitted, never sampling fields split away |
| **D-P14-32** | Pending upload protocol requires main allocation fence/producer flush and worker readiness before bind/upload, then worker completion fence/flush/publication with dual-sync ownership | Shared names/queues and a later worker fence do not order earlier main storage; both syncs need acknowledged cleanup or quarantine |
| **D-P14-33** | Receive exact containing/nested/inspection schema23 and opaque MaterializedSource-v23; earlier numeric receipts are historical | Range-capable selector grant does not add a P14 parser or change nine metadata-only projectionVersion1 trees, assets/native/options/parameter domains |
| **D-P14-34** | Receive P1 D-P1-63/P5 D-P5-42/P13 D-P13-36 mandatory target-bearing synchronous values and exact source conversion; record reciprocal baseline receipts | DSA/sampler mechanisms preserve target/mip/byte/ownership/restoration semantics; allocation value grant is not worker API adoption |
| **D-P14-35** | A3 saves/normalizes/restores relevant actual pack layout and read-FBO/PBO bindings; only successful transfer/map/unmap and valid depth may publish | Four-byte storage alone neither fixes skip/swap state nor authenticates a signalled transfer |
| **D-P14-36** | A5 label payload is at most GL_MAX_LABEL_LENGTH−1 UTF-8 GLchar bytes, truncated at a valid encoding boundary | Native strict byte limit is not Java character count or the inclusive reported maximum |
| **D-P14-37** | Boundary drain removes virtual groups without GL then pops exactly real outstanding groups, with one diagnostic | Overflow plus abort must not pop the driver's default group |
| **D-P14-38** | Warm-up ends on first valid completion or yields to real timeout/demotion; fixed measurement exclusion is separate | N=3 late/stuck fences invalidate an unconditional two-frame promise; no wait or fabricated depth repairs it |
| **D-P14-39** | Receive P4 D-P4-43 profile-selection-v3 identity opaquely, superseding positional-route-v2 in D-P14-29 | No P14 profile evaluation, local hash upgrade or optional grant; selection/evaluated state remains P4-owned |
| **D-P14-40** | Logical texture create retains label with no target/name; first admitted exact-target allocation or known2D depth initialization materializes then labels | No guessed2D/nonobject labeling; failed names remain owned for cleanup and unmaterialized delete issues no GL delete |
| **D-P14-41** | Deletion ends lifetime and zeroes affected native bindings with cache cooperation; no deleted-name restoration | Edit/copy neutrality cannot override native deletion or lease/retirement ownership |
| **D-P14-42** | Stall criterion remains unmet/deferred until measured or an explicitly adopted governing amendment changes it | Safe synchronous fallback and an outstanding clarification request cannot produce a full implementation-gate PASS |
| **D-P14-43** | Receive P13 D-P13-43/P5 D-P5-50's required three-argument context-bound lease, its two added closed rejection reasons and base-serial currentness | The two-argument form no longer exists; P14 supplies observer evidence unchanged, never forges it, and never refreshes or re-associates a base atlas itself |
| **D-P14-44** | R5 C1: `GlModernizationPlan` publishes exactly five tiers (sampler, DSA, debug, compile, readback) plus rationale; "six tiers" in §2.1/§5.1 corrected to "five tiers" | The §2.2 record declares five tier components and P1 §4.7.8 grants exactly eight pure `:engine` values, so no sixth tier exists or may be created |

### 11.2 Input contradictions found, with rulings and provenance

Reported, never silently resolved (`docs/design/v3/DESIGN.md:282`–`:284`, `:141`–`:143`).

**C-1 — Historical initial-build contradiction: the brief said Phase 13 was unbuilt.**
`docs/phase14/briefs/PHASE_14_BUILD_BRIEF.md:50`–`:51` says *"`docs/phase13/` does not exist. There
is no `PHASE_13_DOC.md` to read at all — this is an unbuilt phase, not merely an unverified one."*
During the initial build session `docs/phase13/v1/PHASE_13_DOC.md` was present: 1 435 lines, thirteen `##`
sections, mtime 2026-08-08 14:04 — untracked when first observed, then committed mid-session as
`9ff94a5` — with `docs/phase13/reviews/` containing only `.gitkeep` at the time, i.e. **zero review rounds then** (PHASE_13_REVIEW_1–10 have since landed).
`docs/MOVES.md`'s Phase 13 v3 adoption record (`docs/MOVES.md:82`, `:89`, `:91`) confirms a Phase 13
build session ran. **Historical ruling:** the initial-build instruction and §G5.3 verification gate
prevented consuming that concurrent draft; the initial author did not read it. This preserves the
original quotation and provenance, not an active absence claim. **Current IR-23 disposition:**
§5.5 now consumes the actual P13 contract, including exact parameters and retirement;
P1 D-P1-52 grants the complete value conversion. Stageable upload and optional execution/
equivalence gates remain distinct. Current coordinated bytes still need fresh owner verification.

**C-2 — Three dependencies declare RC3 while being adjudicated against v3.**
`docs/phase5/v1/PHASE_5_DOC.md:18`, `docs/phase6/v1/PHASE_6_DOC.md:10` and
`docs/phase7/v1/PHASE_7_DOC.md:8` each declare `docs/design/v2.0-RC3/DESIGN.md`, and
`docs/MOVES.md:101` confirms *"Phases 3–8 §0 select RC3"*. Their latest rounds were nonetheless
adjudicated *"against the supplied v3 design override"* (`docs/phase5/reviews/PHASE_5_REVIEW_37.md:8`;
same at `docs/phase5/reviews/PHASE_5_REVIEW_38.md:6`) through the now-deleted `verification/targets/`
mechanism, without §G0.4's four-step adoption ever completing. **Ruling:** the discrepancy does not
affect this document, because every citation of those three docs here is to their *content* by
repo-relative path and line, which is revision-independent. It **does** affect a reader: a `§G`
coordinate quoted *inside* those documents is an RC3 coordinate and must not be resolved against v3.
Recorded so nobody makes that substitution. Requested upstream in §11.4.

**Historical C-3 — original activity-gate conflict, resolved by P1 D-P1-54 and D-P14-23.**
Phase 1 originally defined it as *"false unless a debug context and the dev flag are both on"* —
the pre-D-P1-54 wording (P1 §4.7.8 now gates `isActive()` on the installed backend's actual
GL4.3/KHR capability plus `-Dschmaloogium.debug.glLabels`, with no debug-context prerequisite).
Phase 7's OQ-3 fallback — its **default plan** — is *"Make **no context-flag change**"*
(`docs/phase7/v1/PHASE_7_DOC.md:4470`–`:4473`). Under both, no debug context would exist on any
shipping configuration, leaving `DebugService` — an affordance §G4.5 reserves *"from day one"*
(`docs/design/v3/DESIGN.md:587`–`:589`) — dead code. **Ruling:** the two documents are in genuine conflict and
KHR_debug's capability model settles it: object labels and debug groups require the extension or GL
4.3, **not** a debug context, as the reference's own gate proves —
`[V:observed — Pintonium reference-src/pintonium-9c2fcc1/common/src/main/java/org/embeddedt/embeddium/impl/gl/debug/GLDebug.java:291]`.
This design proceeds on the corrected gate (`D-P14-14`) and raises R-P14→P1-1 (§5.6). If that request
is declined, **A5 is undeliverable as specified**, which is stated plainly rather than worked around.
**Current receipt:** P1 §§4.7.8/5 grants the corrected gate; §0.5/§5.6 adopt it.
The old quote/request above is historical, not an outstanding foundation prerequisite.

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
(`docs/phase6/v1/PHASE_6_DOC.md:2230`), an explicit contract-visible **rejection** of PD §6.3's
GPU-side smoothing (`docs/phase6/v1/PHASE_6_DOC.md:638`), with the decision text stating in terms
*"Phase 14's PBO/fence item is **not obviated** and remains the sole async-readback modernization
ledger entry"* (`docs/phase6/v1/PHASE_6_DOC.md:1185`–`:1186`). All three citations were verified at the
line by this session and re-verified against Phase 6's current bytes on 2026-09-08 (§0.11). **Consequence: A3 stands in full** — the one-frame latency on an
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
   left open. This request is unadopted and supplies no waiver: §9.2 keeps condition 3
   unmet/deferred and prohibits full implementation-gate PASS pending measurement or
   explicit governing amendment adoption.
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
    Pending step-8 upload adoption additionally requires D-P14-32's main-allocation fence/flush,
    worker readiness before bind/upload, nonblocking outgoing completion and dual-sync
    cancellation/quarantine (§5.9). R2 correction1 preserves the existing P5-bind/P4-activate/
    draw order without rebinding or demotion; receive complete object baseline and neutral clear.

**To `docs/phase1/v14/PHASE_1_DOC.md`:**

13. **R-P14→P1-1/2/3** fulfilled/adopted, unverified by D-P1-52/54 and D-P14-23.
    **R-P14→P1-4** remains the separately requested optional/post-v0.5 extension (§5.6).
    Coordinated §5.10 additionally requires P1 prepareUnitBindings(mask)/useFixedFunction sampler
    dispatch and P5's successful-preflight placement; P4/P7 retain terminal useFixedFunction.
    Receive D-P14-31 complete authenticated object baseline alongside sampler state on every
    successful setParameters, ordinary FINAL→resumed shader neutrality/failure law, and
    D-P14-26 whole-owned-estate replay/containment before runtime demotion in P1/P5/P13.
    Native capture is P14-local, not an extension to RecordingGLDevice.

**To `docs/phase4/v1/PHASE_4_DOC.md`:**

14. **R-P14→P4-1** (§5.9) is routed to the compiler owner: opaque prepared batch, worker-only
    shader operations, render-thread link/candidate minting, deterministic failures and cancellation/
    late-result disposal. P4/P7 must publish and verify their exact seam before async is enabled.
    This remains a request, not owner adoption or an OQ-15 result.
    Future adoption must include fence→producer flush→publication, failed-publication ownership,
    acknowledged disposal/detachment and watchdog quarantine; mirror these in P7/P13 requests.

**To `docs/phase11/v1/PHASE_11_DOC.md`:**

15. **IR-15 recipient acceptance:** §§5.8/7.5 L-11/10.2 S-22-6 now adopt P11 method/metrics.
    P11 retains semantics/SPI and records the reciprocal handoff; future measurements and any
    conditional candidate require source-free ledger/P11 addendum, not an unmeasured compiler.

**To `docs/phase13/v1/PHASE_13_DOC.md`:**

16. **IR-23 conversion/lifetime adoption:** §5.5 consumes the existing exact value and deferred
    deletion. R-P14→P13-1 remains the stageable-upload proposal; R-P14→P13-2's parameter
    value and P1 D-P1-52 full conversion are supplied/adopted, unverified.
    Pending R-P14→P13-1 must carry D-P14-32's allocation-readiness fence/main flush,
    worker readiness-before-bind/upload, outgoing fence/worker flush/publication, and both
    syncs' exactly-once acknowledged cleanup/quarantine; no API is adopted by this receipt.
    D-P14-31's latest complete owned baseline, failed-update containment and borrowed
    nonmutation law apply independently of whether async staging is ever adopted.

**Attempt-5 R2 receiving receipt status:** D-P14-31/32 are corrected owner requirements,
not claims that other owners have already received these bytes. Main must record P1/P5/P13
baseline and P4/P7 ordinary-FINAL receipts, P7/P13 pending dual-sync obligations, and P2's
separate source-free native ordinary-transition/isolated-upload evidence requirements.
No health IDs or catalogue rows are added or renamed; L-2 and L-4 keep their existing IDs
with these expanded future evidence cases. Fresh owner and integration review remain required.

**To `docs/MOVES.md`:** no change requested by this phase; a Phase 14 row is added by whoever records
this document's adoption, per its own rules.

### 11.5 Items handed to later phases, to G8, and to the implementation effort

| ID | Hand-off | To |
|---|---|---|
| **H-P14→P5-1** | Optional owner-reviewed removal of temporary filter set/restore around generateShadowMipmaps because generation needs no mipmap min filter. Sampler enablement alone removes neither calls nor failure paths; while calls remain, every nonredundant object update remains required. Any simplification preserves D-P14-31 complete authenticated object baseline; P5 baseline/NONE path unchanged here | Phase 5 |
| **H-P14→P13-1** | §5.5 now consumes current parameter/publication/lifetime contracts. Reciprocal P13 §5.5 records exact conversion and sampler-0 fallback; staged upload remains R-P14→P13-1 ungranted, not an absent-interface claim | Phase 13 |
| **H-P14→ALL-1** | Any allocation site found in a frame path by §4.6.2 is a **finding against the owning phase**, routed by package attribution (`engine.buffers` → 5, `engine.uniforms` → 6, `engine.frame` → 7, …). Phase 14 measures; it does not edit another phase's code | the owning phase |
| **H-P14→ALL-2** | Any redundant-state candidate found by §4.7.2 in a sibling's call sequence is likewise that phase's, after passing §4.6.3's four-part test | the owning phase |
| **H-P14→G8-1** | PD §15's evidence that compute, SSBOs, image load-store and indirect dispatch all run pack-exercised on the 1.12.2 compat context (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:741`–`:744`) is carried as ledger row **L-12** — *"the strongest available evidence that G8/S2 is feasible on Cleanroom"*. Not a Phase 14 work item | G8/S2 (`docs/design/v3/DESIGN.md:788`–`:793`) |
| **H-P14→G8-2** | The `GlModernizationPlan` shape is deliberately extensible: G8/S2's compute and SSBO capability gating is the same kind of init-time, profile-derived, per-row tier decision and should reuse it rather than inventing a parallel mechanism | G8/S2 |
| **H-P14→IMPL-1** | `docs/decisions/OQ-15_ASYNC_COMPILE.md` is owed by the spike, in the shape Phase 7 uses for OQ-3 (`docs/phase7/v1/PHASE_7_DOC.md:4441`–`:4473`), and its per-family verdict table becomes `D-P14-12`'s shipped allowlist data | implementation effort |
| **H-P14→IMPL-2** | §7.5's thirteen ledger outcomes are written back into RESEARCH.md §11's OQ-22 status column, with an addendum note added to this document, per §G4.4 (`docs/design/v3/DESIGN.md:578`–`:580`) | implementation effort |
| **H-P14→P2-1** | Scene `S-CD-1` — a scripted near↔far camera path, 300 frames, for §4.3.6 family F3 — is proposed to Phase 2 as a scene. Phase 14 authors no scene | Phase 2 |
| **H-P14→REVIEW-1** | Integration review checks current P5/P6/P7/P13 consumption plus P11 metric-method acceptance and P4 compiler request; separates adopted baseline from ungranted optional extensions and historical verdicts | integration review |

### 11.6 Known gaps in this document

Stated so a verify session does not have to discover them:

1. **Complete P1 TextureParameters/sampler conversion is granted and adopted**
   (D-P1-52, D-P14-23, R-P14→P1-3). Fresh owner verification and optional execution/equivalence
   proof remain; sampler0 is required for actual unknown foreign/incomplete state, not as a
   substitute for implementing the legal synchronous owned mapping.
2. **P13 stageable upload does not exist as a granted API.** Its synchronous build, exact effective
   parameters, source pairing and deferred retirement do exist and are consumed in §5.5.
   Compile-only optimization requires its independent P4/P7 grants; atlas upload stays synchronous.
3. **Current coordinated P4/P5/P6/P7/P13 contracts: verified per §G1.3 (attempt-11 wave,
   2026-09-08).** Their actual binding, lifetime and ten-step ordering details matter, not
   merely the existence of interface names; R-P14→P4-1/P7-1/P13-1 and P5-2 stay optional ungranted extensions.
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
| 1 | Verify adopted P1 D-P1-54 exact package grant before placing files | v0.5 | C-1…C-4 with existing device owner preserved |
| 2 | Add the pure value types to `engine.gl`: `SamplerKey`, `SamplerTier`, `DsaTier`, `DebugTier`, `AsyncCompileTier`, `AsyncReadbackTier`, `GlModernizationPolicy`, `GlModernizationPlan` | v0.5 | compile-time C-1 check: zero LWJGL/Minecraft imports |
| 3 | Implement `GlModernizationPlan.derive` as a pure function of `GLCapabilityProfile` + policy, with a rationale string per row | v0.5 | `ModernizationPlanDerivationTest` over ≥7 profile fixtures |
| 4 | Wire `GlModernizationPolicy` into `mod.core`'s config with five `AUTO`-defaulted rows; log the derived plan once at bring-up | v0.5 | manual: the log line names every tier and why |
| 5 | Implement BIND_TO_EDIT with targetless logical create, exact-target materialize/label, known2D first depth initialization and accounted failed/absent-name cleanup (D-P14-40) | v0.5 | TextureMaterializationLifecycleTest plus §8.2 native lifecycle capture |
| 6 | Add ARB/CORE_45 with edit/copy neutrality and separate zero-binding/cache-cooperating deletion at existing retirement drains (D-P14-41) | v0.5 | BindingNeutralityTest model; §8.2 all-target/native deletion, failure and lease cases |
| 7 | Use adopted complete P1 value; implement key classification plus complete object baseline/cache commit and failed-update containment (D-P14-31) | v0.5 | SamplerStateSplitTest and ordinary FINAL native state/output |
| 8 | Implement sampler interning with actual source/parameter/publication identity, all eight resize reasons and live/retiring reference accounting (§4.1.3) | v0.5 | `SamplerCacheLifecycleTest` |
| 9 | Extend only P5's physical bind path; per-unit samplers after complete conversion, `MULTI_BIND` only after R-P14→P5-2/P1 grant | v0.5 | zero-bind rejected/degraded paths, Bound-only transfer, no stale sampler |
| 10 | Implement neutral all-unit clearing in useFixedFunction and normalization through prepareUnitBindings, preserving complete baseline and P5-bind/P4-activate order | v0.5 | §8.2 ordinary FINAL→resumed shader without demotion, borrowed nonmutation and failed-clear containment |
| 11 | Implement authenticated whole-estate replay then NONE, or contain/rebuild on failure | v0.5 | §8.2 native failure injection after successful sampler use; no false safe restoration |
| 12 | Implement retained logical labels emitted only after native instantiation, P1 activity gate and UTF-8 max−1 budget | v0.5 | DebugLabelCoverageTest and §8.2 label timing/no-error capture |
| 13 | Implement real/virtual counters, no-op underflow, virtual-without-GL then exact-real-pop boundary drain with one diagnostic | v0.5 | DebugGroupBalanceTest and native leaked-overflow→THROWN/abort capture |
| 14 | Implement the no-op `DebugTier.NONE` backend and make it the default | v0.5 | `DebugInactiveIsFreeTest` |
| 15 | Land R-P14→P7-2 part 1 (group call sites in Phase 7) | v0.5 | recorded-log frame/pass group nesting |
| 16 | Add `GL_DEBUG_OUTPUT` + `GL_DEBUG_OUTPUT_SYNCHRONOUS` + `glDebugMessageCallback` routing to `schmaloogium.gl` | v0.5 | manual: one injected GL error appears with a usable stack |
| 17 | Run ledger row **L-5**: one RenderDoc/Nsight capture with `glLabels` set | v0.5 | groups and labels visible; L-5 closes |
| 18 | Keep current synchronous P4 compile; migrate to proposed `CompileExecutor`/Inline only after R-P14→P4-1 and R-P14→P7-1 adoption | optional after grants | compiler ownership/cancellation/publication cases |
| 19 | Implement synchronous CenterDepthSource fallback at P7 transaction step 2 | v0.5 | current P6 SPI/order remains unchanged |
| 20 | Land R-P14→P6-1 (contract the sample age) | v0.5 | Phase 6's fresh verify round |
| 21 | After P6 age grant, implement nonblocking ring with canonical four-byte pack scope and exact restoration, successful transfer/map/unmap-only publication, identity invalidation and completion-based warm-up/timeout demotion | v0.5, gated | CenterDepthRingTest N=3 late/stuck cases; §8.2 nondefault skip/swap and transfer/map/unmap failures; CenterDepthInvalidationTest |
| 22 | Implement P6 recurrence oracle over explicit raw/timing/age/status/reset input schedules, separate from perceptual comparison | v0.5 | C1/C2 initialization, variable/zero delta, h=0, unavailable/repeated samples and 0→1 step |
| 23 | Propose scene S-CD-1; run families F1–F4 with fixed five-frame measurement exclusion separate from recorded first-valid completion, no hidden timeout/demotion | v0.5, gated | C1–C4; no completion cannot vacuously pass C4; L-3 decides A3's default only with P6 grant |
| 24 | Implement §4.7.2's audit classifier and its three validity gates over a `GLCallLog` | v0.5 | `AuditClassifierTest`, `AuditScopeFilterTest` |
| 25 | Run separate facade audit and actual-backend native capture before/after A1; never infer raw churn from facade events | v0.5 | L-2 measured native counts/state, §8.2 transition evidence |
| 26 | Run §4.6.2's allocation profile on the Phase 2 scenes across the three configurations | v0.5 | **L-8 closes**; violations become H-P14→ALL-1 hand-offs |
| 27 | Run L-6 per-tier comparison and L-1/L-7/L-9 spot-checks; run L-11 exact P11 method before considering any compiled evaluator | v0.5 | source-free measured ledger decisions; no untriggered compiler |
| 28 | Execute S-22-1 and S-22-4: freeze the row set, then write every outcome back to RESEARCH.md §11 and add the addendum note | v0.5 | §9.2 condition 4 |
| 29 | Evaluate every §9.2 condition, including measured stall reduction; report unmet/deferred conditions, never full PASS from synchronous fallback or pending amendment | v0.5 gate, remains open as needed | T3/T1 runs plus condition3 measured long-frame/reload comparison or explicit adopted governing amendment |
| 30 | Land exact P4 prepare/worker/link R-P14→P4-1 and matching P7 step-3 resumability R-P14→P7-1 | post-v0.5, optional | fresh owner verification and cancellation/late-result cases |
| 31 | Implement gated worker fence→flush→publish and upload main-allocation fence→main flush→worker readiness, with dual-sync acknowledged teardown/quarantine | post-v0.5 | isolated progress without later swap/flush, cancellation and unresolved retention, §10.1 |
| 32 | Run **OQ-15**'s spike (§10.1) on ≥2 driver families; write `docs/decisions/OQ-15_ASYNC_COMPILE.md` | post-v0.5 | criteria K1–K5; **L-4 closes**; the verdict table becomes the allowlist |
| 33 | Land R-P14→P13-1; add the async `_n`/`_s` atlas upload to the worker | post-v0.5 | atlas pixel equality vs a synchronous upload |
| 34 | Land R-P14→P1-4(b); add `-Dschmaloogium.debug.glContext` and the `GLFW_OPENGL_DEBUG_CONTEXT` request, gated on OQ-3's outcome | post-v0.5 | manual: debug-context message volume vs the non-debug tier |
| 35 | Reconcile any subsequent owner corrections to current §5 contracts and verification gates; never promote prior PASS to certify changed bytes | as owners change | fresh affected-owner review |
| 36 | Receive exact P3 schema23/current-constant and MaterializedSource-v23 identities plus current P4 profile-selection-v3 opaquely; reject historical identities | before reuse | §8.1 identity/ownership gates; no reconstruction or optional API grant |
| 37 | Receive P13's required three-argument context-bound lease with unchanged closed rejection reasons and base-serial currentness (D-P14-43); supply P7 observer evidence verbatim | before reuse | rejection/stale-serial cases leave no modernized path; no forged evidence, local refresh or two-argument fallback |

Items 1–29 are v0.5 work, not a claim that the complete selected implementation gate can
pass without condition 3. Items 30–34 are quality-of-life work, individually gated and
droppable with safe synchronous fallback; dropping them does not waive that criterion.

---

*End of `PHASE_14_DOC.md`. v1 remains governed by `docs/design/v3/DESIGN.md`. **Verification status — 2026-09-08 (attempt-11 wave close-out):** the attempt-11 fresh whole-owner review
(`docs/phase14/reviews/PHASE_14_REVIEW_8.md`, frozen SHA-256 `b185b50dea626f77eeee38c962c8236ee3d18134b703d384d4186bb9d5f699c5`) returned PASS-WITH-CORRECTIONS —
0 blocking, 2 corrections, 1 note; this fix-up wave applied every correction and recorded resolutions
in the review file; no §5 bytes changed and no §5 change is outstanding, so per §G1.3 the document is
**verified**. The §0.4–§0.11 rounds and initial-build departures stay history (§0.3/§11.2); the
P5/P6/P7/P13/P11 handoffs stay adopted documentation contracts, extensions and authority changes gated;
no implementation, test/validation result or OQ closure is claimed.*
