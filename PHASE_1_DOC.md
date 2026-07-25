# Schmaloogium — Phase 1: Foundation & project architecture — Architecture

---

## 0. Header

**Phase:** 1 — Foundation & project architecture
**Milestone:** v0.1 · **Depends on:** — (Wave 0; this doc feeds every other phase)
**Assigned OQs:** OQ-2, OQ-12, OQ-20 (seam hardness), OQ-21
**Authored:** 2026-07-24 · **Last revised:** 2026-07-25 (§0.10)
**Deliverable:** this document, per DESIGN.md §G9.

*Both dates are stated, and deliberately. The authoring date is what the rest of the document reads
against — §4.1's template facts are read from the checkout on 2026-07-24, §4.2.6's thirteen pin rows
are re-verified 2026-07-24, and `[V:repo]` below is defined as inspection on 2026-07-24 — so a single
later stamp would silently re-date claims to a day on which they were not performed. The revision
date is what the fix-up addenda in §0.4–§0.10 were written on.*

### 0.1 Inputs actually read

| Input | What was read |
|---|---|
| `Schmaloogium/DESIGN.md` | All of Part I (§G0–§G10, lines 1–575) and the Phase 1 spec in Part II (lines 585–658). Phase titles only from §G5.1 for other phases. |
| `Schmaloogium/RESEARCH.md` | §0 (reading guide, confidence tags), §1 (mission, non-goals, decision log D-1..D-10), §3.2 (source directives — cited by §3's `centerDepthSmooth` row), §4.1 (lifecycle — the GL capability probe set), §4.2 (program-registry mechanics — read for the facade's "use program is the universal state barrier" implication), §5.1, §5.2, §5.3, §6.1, §7.1 (the D-5 sentence only), §7.2, §7.4 (the coexistence sentence only), §8.3, §9, §10.1–§10.3, §11 (full OQ register), §12.2, §12.4, App D.1/D.3 (the `ivec2` uniforms), App E (header only — the "SRG name + descriptor" requirement quoted in §4.5.3), App H (glossary rows for platform terms). **App D.2/D.4, App B, App F.1/F.5/F.6/F.7, §3.4 and §3.5 were *not* read by the build session** — see §0.5, which records the fix-up session's reading and why that omission mattered. |
| `Schmaloogium/build.gradle` | Complete. |
| `Schmaloogium/settings.gradle` | Complete. |
| `Schmaloogium/gradle.properties` | Complete. |
| `Schmaloogium/gradle/scripts/dependencies.gradle` | Complete. |
| `Schmaloogium/gradle/scripts/extra.gradle` | Complete. |
| `Schmaloogium/gradle/scripts/publishing.gradle` | Complete. |
| `Schmaloogium/gradle/wrapper/gradle-wrapper.properties` | Complete. |
| `Schmaloogium/.github/workflows/build.yml` | Complete. |
| `Schmaloogium/.github/workflows/release.yml` | Complete. |
| `Schmaloogium/.github/workflows/release-to-cf-mr.yml` | Complete. |
| `Schmaloogium/README.md` | Complete. |
| `Schmaloogium/.gitignore` | Complete. |
| `Schmaloogium/src/**` | All eight files: `main/java/com/example/modid/{ExampleMod.java, proxy/{IProxy,CommonProxy,ClientProxy}.java}`, `main/java-templates/com/example/modid/Reference.java`, `main/resource-templates/{mcmod.info,pack.mcmeta}`, `main/resources/modid_at.cfg`. |
| MCP `cleanroom` | `get_porting_guide("mixin-setup")`, `get_project_template("mixins.json")`, `get_project_template("checklist")`, `explain_concept("mcmod.info", loader="cleanroom")`. |

**Provenance tags used in this document.** RESEARCH.md §0.2 defines `[V:doc]`, `[V:observed]`,
`[V:template]`, `[V:mcp]`, `[V:web]`, `[A]`, `[U]`, `[D-n]` and `[Q:OQ-n]`, and this document uses
them with exactly those meanings — in particular `[V:doc]` means *verified against the shipped
OptiFine pack-author docs*, never "verified against a project document". Two tags are this
document's own and are declared here because RESEARCH.md does not define them:

| Tag | Meaning |
|---|---|
| `[V:design]` | Verified against `DESIGN.md` — a **project decision** of ours, not observed or documented reference behavior |
| `[V:repo]` | Verified by read-only inspection of this repository's working tree (files, `git log`, `git status`) on 2026-07-24 |

### 0.2 Dependency PHASE docs consumed

None. Phase 1 has no dependencies (§G5.1).

### 0.3 Deviations from the assigned reading list, with reasons

1. **`cleanroom-src/src/main/java/com/cleanroommc/` was NOT skimmed.** The spec lists it as
   "skim only" and the Context budget line says "do not spend it exploring cleanroom-src beyond
   the listed skim." Nothing in this phase's scope turned out to need loader-internal boot/mixin
   bootstrap layout: the `MixinConfigs` manifest contract is fully specified by RESEARCH.md §5.1
   and the MCP `mixin-setup` guide, both of which are same-day-fresh and agree. Recorded here as a
   deliberate omission rather than claimed as done. If the Phase 7 spike on CleanMix divergences
   (OQ-4) finds the manifest contract underspecified, that is the session that should read the
   bootstrap.

2. **Web and Maven lookups were performed** beyond the listed inputs, for the OQ-2 re-pin only.
   §G1.1 permits web use when "a listed input is missing or contradictory"; more directly, the
   Phase 1 spec *orders* re-verification ("re-verify the current Cleanroom loader release …
   daily cadence, so check again"). Sources queried, all 2026-07-24:
   - `https://api.github.com/repos/CleanroomMC/Cleanroom/releases` — release list + notes.
   - `https://repo.cleanroommc.com/releases/com/cleanroommc/cleanroom/maven-metadata.xml` — the
     resolvable artifact list and `<release>` marker.
   - `https://maven.arcseekers.com/releases/xyz/wagyourtail/unimined/xyz.wagyourtail.unimined.gradle.plugin/maven-metadata.xml`
     — the kappa-fork version list.
   - `https://maven.wagyourtail.xyz/releases/…` — checked and found to carry only upstream
     (non-kappa) Unimined; recorded in §4.2's pin table because it is a real re-pin trap.

3. **`RESEARCH.md` sections read beyond the assigned §1/§5.1–§5.3/§6.1/§7.2/§12.2**, each with its
   reason — the complete list, matching §0.1 above:
   - **§4.1** — the spec's own facade bullet names "the §4.1 probe set", so `GLCapabilityProfile`
     cannot be designed without it.
   - **§4.2** — the facade's shape is constrained by the "use program is the universal state
     barrier" behavior that Phase 4/6 will drive through it.
   - **§7.1** (the D-5 sentence only) — to confirm the mixin-only hook posture the wiring serves.
   - **§7.4** (the coexistence sentence only) — the `mod.compat` bail registry is assigned to this
     phase and §7.4 is where its "detect and disable with a clear message" premise lives.
   - **§8.3** and **§9** — the testability split and milestone shape this phase's §8/§9 must fit.
   - **§10.1–§10.3** — §G7's licensing rules point here, and OQ-12 is assigned to this phase.
   - **§11** — §G4.4 requires each assigned OQ's question *verbatim*, which is only in §11.
   - **§12.4** — named in the spec's Required inputs (the MCP recipes).
   - **App E** (header only) — the SRG-name-plus-descriptor requirement §4.5.3 adopts and quotes.
   - **App H** — glossary rows for platform terms, per §G4.1's "the project dictionary".

   All are short, targeted reads; none is a whole-appendix study.

4. **No git operations, builds, tests, or review agents were run** (§G1.1). One read-only
   inspection of the working tree's git status was made; it confirmed §4.8.1's statement that
   `LICENSE` is present, tracked, and the verbatim GPL-3.0 text.

### 0.4 Fix-up session addendum (round one — 2026-07-24)

A separate fix-up session (§G1.3) applied `Schmaloogium/PHASE_1_REVIEW.md`'s findings F-1 … F-12 to
this document. Its resolutions are recorded in that file under `## Resolutions`; the findings and the
verdict are unchanged there.

Inputs that session read beyond the build session's list, all for finding F-1 (the facade's missing
pixel-transfer verbs) and each cited by the finding itself: `PHASE_1_REVIEW.md`; RESEARCH.md §3.1 and
App A.3 (`.gsh`, `GL_ARB_geometry_shader4` + `maxVerticesOut`), §4.1 step 4 (noise texture creation),
§4.4 and §6.2 (the synchronous `centerDepthSmooth` readback and its PBO modernization row), App D.3
(`ivec2` uniforms), App F.5 (custom textures / `texture.noise`). One Maven Central version query was
made to pin ASM (§4.2.6, finding F-8).

**§G1.3 status at the time:** that fix-up altered **§5** (F-1 and F-4), so §G1.3's "re-verify only if
§5 changed" rule required a fresh verify session. Three have since run. **This subsection records
round one only and is no longer the document's current state — see §0.5.**

### 0.5 Fix-up session addendum (rounds two, three and four — 2026-07-24)

Three further verify sessions ran after §0.4's fix-up — `PHASE_1_REVIEW_2.md` (V2-1 … V2-10),
`PHASE_1_REVIEW_3.md` (F3-1 … F3-12) and `PHASE_1_REVIEW_4.md` (F4-1 … F4-21) — each returning
PASS-WITH-CORRECTIONS. **No fix-up ran for rounds two or three**, which round three's F3-1 and round
four's §1 both establish; round four then attacked round three's list adversarially and
dispositioned every item. A single fix-up session (§G1.3) has now applied all three rounds together:

- **V2-1 … V2-10** and **F3-1 … F3-12**, *as dispositioned by round four* — which refuted F3-11 and
  F3-12 outright, demoted F3-1 and F3-4 to notes with no §5 consequence, and narrowed F3-3 to two
  missing verbs plus one recorded deferral, after establishing that its headline rested on a
  four-word misquotation of a sentence that is correct as written.
- **F4-1 … F4-21**, including six further §5-touching items no earlier round raised.

Four of round three's proposed fixes were deliberately **not** applied, each because applying it
would have made the document worse; the reasons are recorded in the resolutions table in
`PHASE_1_REVIEW_3.md` rather than repeated here.

**Inputs this session read beyond the build session's list**, each because a finding turned on it:
`PHASE_1_REVIEW.md`, `PHASE_1_REVIEW_2.md`, `PHASE_1_REVIEW_3.md`, `PHASE_1_REVIEW_4.md`;
RESEARCH.md **App D end to end (D.1–D.4)**, **App B.1–B.5**, **App F.1/F.5/F.6/F.7**, **§3.4**,
**§3.5**, §4.3 and §4.4 (the composite/final draw state and the depth copies), §4.2 (the fixed unit
map's per-program re-point); DESIGN.md §G1.1/§G1.3/§G5.3 and — for three ownership questions the
findings turned on — the *Scope-in* bullets of the Phase 3, 4, 6, 7 and 14 specs in Part II. §G1.1
line 78 bars a *build* session from other phases' specs; a fix-up session auditing an ownership
claim has no such bar, and the read is disclosed here in any case.

**The lesson worth recording, because most confirmed defects share it.** `DESIGN.md`'s Phase 1
*Required inputs* do not list App D, so the build session was structurally unlikely to see App D.4's
`blendFunc | ivec4` row — the counterexample to a claim §4.7.4 volunteered about the whole 1.12.2
contract. Likewise RESEARCH.md §3.5's macro header, the source of profile fields §3 never mapped.
**A document that volunteers a claim beyond its assigned reading inherits the burden of checking it**,
and this fix-up therefore read App D and App F end to end rather than patching the cited rows.

**§G1.3 status at the time:** that fix-up altered **§5** — the facade gained an `ivec4` upload, two
state verbs, a GL-error surface, a depth-copy verb and a use-after-delete assertion, and §5 gained
rows for Phases 2, 3 and 7 — so a **fifth verify session** was required before Phase 2, Phase 3 or
any other dependent consumed this document. Two verify sessions have since run — rounds five and six
— and round five's fix-up never ran. **This subsection records rounds two to four only and is no
longer the document's current state — see §0.6.**

### 0.6 Fix-up session addendum (rounds five and six — 2026-07-25)

Two further verify sessions ran after §0.5's fix-up — `PHASE_1_REVIEW_5.md` (V5-1 … V5-8) and
`PHASE_1_REVIEW_6.md` (V6-1 … V6-6) — each returning PASS-WITH-CORRECTIONS. **No fix-up ran for
round five**, which is round six's headline finding (V6-1): `PHASE_1_REVIEW_5.md` carried no
`## Resolutions` section, this document carried no §0.6 and no `[fix-up: PHASE_1_REVIEW_5.md …]`
marker, and all eight of round five's findings stood verbatim at their cited lines. Round six
re-derived every one of them from source rather than inheriting them, confirmed all eight, extended
V5-1 to two further sites, and added six findings of its own. A single fix-up session (§G1.3) has now
applied both rounds together.

**The five design calls this session made**, since the reviews left them open and a later reader is
owed the reasoning rather than only the outcome:

- **V5-1 / V6-2 / V6-3 — the `glGetError` contract.** The claim that the cheap cadence "cannot lose
  an error" was false and is deleted: GL holds the first error until the flag is cleared, so a
  batched sweep in which five uniforms fail yields one record. Rung 2 is **not** made debug-mode-only.
  Instead attribution is defined as a property of the caller's **drain window**, and Phase 6's
  protocol is stated: drain, upload the set, drain — and only on a non-empty drain, re-upload
  draining between uploads to attribute (`[D-P1-32]`). This keeps rung 2 implementable in the
  shipping configuration at v0.1, which `DESIGN.md` requires, without putting a `glGetError` on the
  clean per-frame path §7 protects.
- **V5-2 — `DrawService.fullscreenQuadInstanced(int)` is deleted**, not specified. `instanceId` is an
  `int` uniform and GLSL 120 has no `gl_InstanceID`, so the verb could not express `countInstances`;
  the faithful shape is a caller-side loop. The deletion is recorded in §4.7.4's absent-verbs table,
  §3, §5.2 and §12 item 19 rather than left silent (`[D-P1-33]`).
- **V5-3 — the CI ordering is stated**: the two named seam steps run **before** `./gradlew build`.
  `-x test` was rejected because it would also drop `:conformance:compileTestJava` and disarm
  §4.2.4a's account of the Impl gate; `if: always()` was rejected because it leaves `build` as the
  first red step, which is the outcome `[D-P1-24]` exists to prevent.
- **V5-4 — the `:mod` ASM remedy is corrected in mechanism and scope.** `resolutionStrategy.force` is
  removed (it cannot arbitrate between two different `group:name` modules), and the exclusion moves
  to `testImplementation`, covering the `testCompileClasspath` C-2/C-3 actually compile against.
- **V6-5 — `blit` is kept with its consumer named** (Phase 5, the buffer estate) rather than moved to
  the absent-verbs table, with the condition for revisiting recorded. **V6-6 — `enable_mixin_debug`'s
  CI clause is deleted**: the flags reach only Unimined's run tasks, which CI never invokes.

**Inputs this session read beyond the build session's list**, each because a finding turned on it:
`PHASE_1_REVIEW_5.md`, `PHASE_1_REVIEW_6.md`; `DESIGN.md` §G2.4 (rung 1 verbatim) and — disclosed
because §G1.1 bars a *build* session from other phases' specs, and a fix-up auditing an ownership
claim should disclose the read regardless — the **Phase 11** *Scope — in* bullet, which settles rung
1's owner and trigger; RESEARCH.md §3.2 and App A.3 (`countInstances`), §3.5 (GLSL 120 has no
`gl_InstanceID`), App D.4 (`instanceId` as an `int` uniform), §4.5 and App B.2 (the shadow pass's
third depth copy, for V5-7), and App F.7 (the three per-program state keys §3 had not mapped, for
V6-4).

**The lesson worth recording.** Round five's eight findings sat unapplied for a full round, and round
six spent its budget re-deriving them instead of attacking new material. The cost of a skipped fix-up
is not the fix-up — it is a whole verify session. §G1.3's deliverable is the `## Resolutions`
section; a fix-up that edits the doc and does not write one has not run.

*One housekeeping note left for round seven rather than decided here: the two reviews are stamped
2026-07-25 while this document's header still reads 2026-07-24. The header is deliberately not
restamped — that is not a correction any finding asked for.* **Round seven asked for it (V7-8), and
§0.7 settled it: the header now carries both dates rather than either alone.**

**§G1.3 status at the time:** that fix-up altered **§5** — §5.2's GL-error row (attribution narrowed
to what the default cadence delivers), its pixel-transfer consumer list (**Phase 8** added), its
recorder row (a log-supplying constructor) and its non-verbs row (instanced draw) — so a **seventh
verify session** was required before Phase 2, Phase 3 or any other dependent consumed this document.
**No service signature was added:** one verb was removed, and one constructor overload plus one
static factory were added on a test-support class. That seventh session has since run
(`PHASE_1_REVIEW_7.md`). **This subsection records rounds five and six only and is no longer the
document's current state — see §0.7.**

### 0.7 Fix-up session addendum (round seven — 2026-07-25)

A seventh verify session ran after §0.6's fix-up — `PHASE_1_REVIEW_7.md` (V7-1 … V7-8), returning
PASS-WITH-CORRECTIONS: five corrections, three notes, **zero blocking**. It is, in that review's own
count, the first round in six at which the previous fix-up's whole applied list — fourteen items
across rounds five and six — landed at every site it claimed, and the first at which no finding concerns a
missing verb, a wrong appendix citation, an unmapped contract row or an interface a dependent cannot
reach. **All eight findings are applied. None was narrowed and none was refused.**

**The design calls this session made**, recorded as arguments rather than outcomes because round
seven asked for exactly that, and because a fix-up session gets no adversarial review of its own:

- **V7-1 — `countInstances` is scoped, and the other half gets owners.** The mapping added in §0.6
  named a composite-only mechanism (a caller-side loop over `DrawService.fullscreenQuad()`) for a
  directive none of its three cited sources restricts to composites: `doc/shaders.txt` puts
  `countInstances` under *Vertex Shader Configuration* beside `mc_Entity` and `at_tangent`, App A.3
  tags it `(vsh)`, `uniform int instanceId` sits in the **common** uniform block, and RESEARCH.md
  §4.2 carries "instance count" on all 43 program slots. Only **§4.4** restricts the *observed loop*
  to the composite pass, and §4.4 was not cited. Two things follow and both are now written down: the
  §3 row is scoped to composite/deferred **and cites §4.4 for the restriction**, and the
  gbuffers/shadow case gets a second row, an owner split (**Phase 3** detects the directive,
  **Phase 4** carries the per-slot count, **Phase 7** owns the re-render) and a §11.4 hand-off
  (`[D-P1-35]`). It is recorded as an **open case, not a designed one** — RESEARCH.md observes no
  non-composite instancing loop, so there is no reference behavior here to be faithful to, and
  designing one would be Phase 7's work done by the wrong session.
- **V7-2 — the one-query claim is kept and made true, rather than weakened.** The protocol makes two
  `drainErrors()` calls per sweep while four sites costed it at one. Three answers were available:
  restate the cost as two, amortize caller-side (the trailing drain of set *N* is the leading drain of
  set *N+1*), or make the backend elide a drain that has nothing to observe. The **elision** is
  chosen. Restating as two doubles a synchronous driver query across 43 program switches per frame —
  the exact cost §7 exists to bound — for no reason but honesty about a fixable inefficiency. Caller
  amortization is only sound while nothing mutating happens between program sets, which makes it a
  precondition Phase 6 must honour and a silent misattribution if it does not. The elision — one bit,
  set by every mutating facade call and cleared by every drain — is correct in both cases: back-to-back
  sets cost one query, and an intervening mutating call re-arms the leading drain exactly when the
  window needs bounding. It asks nothing of Phase 6 (`[D-P1-30]`). **Round eight (V8-1) found the
  "correct in both cases" claim true only of *facade* mutations: the GL error flag is per-context and
  vanilla's draws never reach the facade, so a foreign error between two sweeps leaves the bit clear
  and survives into our window. §0.8 settled it — the elision is kept for the cost reason above, the
  claim is corrected, and the containment is credited to `[D-P1-32]`'s unattributable branch rather
  than to the bit.**
- **V7-3 — the drain is a loop, and the GL fact behind it is now cited.** Round seven's sharpest
  observation is not the imprecision itself but how it survived: the GL semantics claim carried **no
  provenance tag and no source anywhere in the document**, in a decision written specifically to
  correct an earlier GL error. The rule is that an implementation may maintain several error flags
  and `glGetError` *"returns and clears an arbitrary error flag value … `glGetError` should always be
  called in a loop, until it returns `GL_NO_ERROR`, if all error flags are to be reset."* The loop is
  adopted, and it is free on the clean path (it stops at the first `GL_NO_ERROR`). A single call per
  drain would leak a flag into the following window — and on the first window of `[D-P1-32]`'s replay
  that means a spurious record and an **innocent uniform disabled**, which is the attribution the
  whole decision exists to establish. The per-call debug trigger is narrowed at the same site from
  *any* `-Dschmaloogium.debug.*` flag to the two GL-facing ones, so Phase 3's source dump cannot
  silently change a per-frame query count.
- **V7-4 — the replay re-uploads cached values, and this is the call the reviews left open.** The
  alternative is re-evaluating the providers, and it is unsafe rather than merely wasteful: App D's
  `wetness`, `eyeBrightnessSmooth` and `centerDepthSmooth` advance a halflife filter **per sample**,
  so a second evaluation inside one sweep double-advances the smoothing and puts a visible artifact
  on precisely the frame the engine is already degrading. Re-uploading cached values has no such
  hazard — `glUniform*` is idempotent on the bound program, and the replay's only purpose is to
  change which *drain window* each upload lands in, not which value it carries. Two smaller calls at
  the same site: a replay that reproduces nothing (`OUT_OF_MEMORY` need not recur) is **unattributable**
  and falls to §6's 3→4 row rather than silently no-op'ing, and the backend is put under a stated
  obligation to retain the name from `locate(p, name)` so a record can name a uniform at all
  (`[D-P1-34]`, in `[D-P1-29]`'s prose form because no test can catch it). **Round eight (V8-3) found
  the per-sample premise unsourced: App D gives the values only, its cadence note and RESEARCH.md §4.2
  make a "refresh" an *upload* (the redundant-upload skip presupposes the value is already computed),
  RESEARCH.md §4.4 puts the sampling at frame begin, and `DESIGN.md` requires Phase 6 to specify a
  *time-corrected* decay — under which a second evaluation inside one frame advances by ≈0. §0.8 keeps
  the rule and deletes the premise; idempotence carries it alone.**
- **V7-8 — the header carries both dates.** Restamping to 2026-07-25 was rejected outright: §4.1's
  template reads, §4.2.6's thirteen pin rows and `[V:repo]`'s definition are all dated 2026-07-24, and
  one later stamp would re-date every one of them to a day on which the work was not done. *"Authored
  … last revised …"* keeps each dated claim true and removes the discrepancy a reader had to
  reconcile from a footnote.

**Inputs this session read beyond the build session's list**, each because a finding turned on it:
`PHASE_1_REVIEW_7.md`; RESEARCH.md **§4.2** (the per-slot "instance count") and **§4.4** (the
composite-pass instancing loop) — the two passages V7-1's scoping turns on, and §4.4 is now cited in
§3 where it was missing; and — the read this document has never made before — the **OpenGL
`glGetError` reference page** `[V:web]`, whose wording is identical in the GL 2.1-era and GL 4
refpages, read 2026-07-25 through the docs.gl mirror after `registry.khronos.org` refused the
request. **The URL was missing at both citation sites, which round eight (V8-4) found and §0.8
supplied: `https://docs.gl/gl4/glGetError` and `https://docs.gl/gl2/glGetError`.** Round seven
identified the absence of any GL source as the reason an imprecise cadence survived a fix-up written
to correct an imprecise cadence; the citation now sits at `[D-P1-30]`.

**The lesson worth recording.** Rounds five and six were about things that were *missing* — verbs,
citations, rows. Round seven found none of those and still returned five corrections, every one of
them about **precision in a protocol the document already had structurally right**: a cost figure
that was off by a factor of two, a GL rule stated for the single-flag case only, three preconditions
a Phase 6 session would have had to invent, a mapping narrower than its own citations, a sentence of
inherited reasoning that was backwards. The pattern is that `[D-P1-32]` — the largest piece of
design in this document that had never been through a verify session — carried three of the five.
**Unreviewed material yields findings in proportion to its size, not to the document's maturity.**

**§G1.3 status at the time:** that fix-up altered **§5** at two rows — §5.2's **non-verbs row** (V7-1's
scope qualifier and the Phase 7 owner) and its **GL-error row** (V7-2's cost, V7-3's cadence, V7-4's
three preconditions). **No service signature was added or removed by any of it:** the facade's verb
list, every service interface and every handle type were byte-for-byte what round seven reviewed. An
**eighth verify session** was therefore required before Phase 2, Phase 3 or any other dependent could
consume this document, and until that verdict existed the doc was **not** a valid dependency input
(§G5.3). What the eighth session inherited was narrow and stated: no unapplied findings, no
re-derivation debt, and two rows of prose in one section. That eighth session has since run
(`PHASE_1_REVIEW_8.md`). **This subsection records round seven only and is no longer the document's
current state — see §0.8.**

### 0.8 Fix-up session addendum (round eight — 2026-07-25)

An eighth verify session ran after §0.7's fix-up — `PHASE_1_REVIEW_8.md` (V8-1 … V8-7), returning
PASS-WITH-CORRECTIONS: five corrections, two notes, **zero blocking**, three of the five touching
§5. **All five corrections are applied and none was refused**; one (V8-2) is applied *wider* than
its fix shape asked and one (V8-3) resolved toward deletion rather than the tagged-caution
alternative it offered, both with the reason recorded below. V8-6's note is applied as the six-word
change it asked for and no more. **V8-7 is deliberately unchanged:** round eight examined
`[D-P1-35]` and ruled it a legitimate *flag* rather than a decision — §3's row carries an `[A]` tag,
§11.4 says "open rather than designed", and §11.4's Phase 7 entry is conditional — so editing it
would remove a correct hand-off.

**Round eight's meta-finding, and what this session did about it.** Round eight's closing assessment
is that two of its three §5 findings trace to round seven's *supporting argument* being promoted
into §5 contract without being re-checked — V8-3 verbatim, V8-2 at a site round seven had quoted.
Its statement of the pattern: *a review's supporting argument is not evidence, and a fix-up that
promotes it into §5 has changed its status without changing its support.* That warning is aimed at
this session as squarely as at the last, so **every finding was re-derived from the source it cites
before anything was written into this document** — `DESIGN.md` Part II's Phase 5/6/7 *Scope* bullets
read directly for V8-2, RESEARCH.md §4.4, §4.2 and App D read directly for V8-3, RESEARCH.md §0.2's
tag table for V8-4, and `build.gradle`'s three `apply from:` sites for V8-5. All five corrections and
both notes survived that derivation; none was refused with cause. What the derivation *did* change is
the **shape** of two of the fixes, below.

**The design calls this session made**, recorded as arguments rather than outcomes, because a fix-up
session gets no adversarial review of its own and the next session can only attack reasoning that is
written down:

- **V8-1 — the elision is kept, the claim it rested on is corrected, and the residue is given an
  owner.** The finding is real and it survives independent derivation: the bit is set by every
  mutating **facade** call, but the GL error flag is **per-context**, and this architecture
  guarantees GL traffic that never reaches the facade — §3's own second row says vanilla geometry is
  "drawn by Minecraft's own draw calls through Phase 7's hooks, which never reach the facade", §G4.6
  makes `GlStateManager` cooperation one-directional, and §4.7.4's own absent-verbs table routes face
  culling and contemplates the anaglyph final through vanilla's path. So a foreign error between two
  of Phase 6's sweeps leaves the bit clear, the leading drain elides, and the error lands in a window
  in which nothing of ours failed. Three resolutions were available and the choice is **keep the
  elision**. Dropping it would bound the window against all GL, and it would pay a factor of two on a
  synchronous driver query across 43 program switches per frame — the exact cost §7 exists to bound —
  to relabel a case the design already contains: `[D-P1-32]`'s replay reproduces nothing, the sweep is
  unattributable, and §6's 3→4 row logs it and keeps the program running, so no innocent uniform is
  disabled. A guard *inside* the facade was considered and rejected on a mechanism argument rather
  than a cost one: **the facade cannot observe non-facade GL**, so there is no bit it could set. What
  is not kept is the wording. §4.7.4's *"and the window is correctly bounded"* is deleted, every site
  now says "mutating **facade** call", and the consequence is stated once at §4.7.4 and carried into
  §5.2 as contract — **a non-empty trailing drain does not by itself imply that one of Phase 6's
  uploads failed**, which is why precondition (ii) is load-bearing in general and not an
  `OUT_OF_MEMORY` corner. The one sound remedy — a single unconditional drain at a frame-driver-defined
  point, roughly one extra query per frame instead of forty-three — is a **placement**, and placement
  in the frame is Phase 7's, so it goes to §11.4 as a hand-off rather than into this facade's design.
  **Round nine (V9-1) found "the one sound remedy" false on three limbs, and §0.9 corrected it at all
  four sites: it is not the only sound remedy — this very bullet concedes two sentences earlier that
  dropping the elision "would bound the window against all GL" and rejects it on *cost* — it bounds
  only the gap spanning the frame boundary rather than the between-sweeps case it is attached to, and
  it is not "unconditional" through the verb §5.2 exposes, because the elision applies to it too. The
  hand-off is kept and the elision is kept; the claim is corrected and its two limits are now stated
  with it. The "forty-three" in this bullet reuses a registry cardinality as a per-frame event count
  (RESEARCH.md l. 493 says 43 *slots*); it is left standing here as the round-eight record and is
  corrected in the live prose — see §0.9.**
- **V8-2 — the composite loop is Phase 7's, and the directive's two halves are split by their real
  owners.** `DESIGN.md` Part II settles the first half outright: Phase 7's *Scope — in*, part (a),
  names the `countInstances` instancing loop under **Composite/final execution**, while Phase 5's
  *Scope — in* contains no pass-execution bullet at all and its *Scope — out* removes the adjacent
  case in the same terms ("when copies/clears *happen* in the frame (Phase 7)"). Phase 5 owns the
  buffer estate the composite passes read and write, not the draw loop that runs them — a distinction
  this document already made correctly one row later, where §3's `scale.<prog>` row separates
  computing the rectangle (Phase 5's) from executing the pass. There is therefore **no disagreement
  with `DESIGN.md` to flag** under §G1.1; the document was simply wrong, and the three sites are
  retargeted. **This is applied wider than round eight's fix shape, and deliberately.** The old
  wording was *"the loop and its cadence are Phase 5/6's"*, and the **6** in it is right for a reason
  the **5** obscured: `DESIGN.md` Phase 6's cadence model carries `instanceId` among the per-draw
  dynamics *"at their hooks (Phases 7/9/10 invoke)"*. So the honest split is that **Phase 7 runs the
  loop and Phase 6 owns the `instanceId` upload it makes between copies**, and naming both is what
  stops a future reader re-deriving the seam. Round eight's predicted side effect follows:
  `[D-P1-35]`'s non-composite owner and the composite owner now coincide, and §5.2's non-verbs row no
  longer names two phases for two halves of one directive. The two cases stay distinct in *kind*, and
  §11.4 now says so — the composite loop is **assigned** by `DESIGN.md`, the gbuffers/shadow re-render
  is **open**.
  **Round nine (V9-4) withdrew the supporting clause and (V9-8) supplied a better one, both applied in
  §0.9. The `scale.<prog>` row cited above as "already made correctly" was itself unsourced** — the
  rectangle's three inputs have owners but the multiplication is assigned nowhere in `DESIGN.md`, and
  Phase 7's *Scope — in* names the sub-viewports — so §3's row now reports the silence and this bullet's
  appeal to it does **not** stand. **The retarget itself stands, and on stronger ground than either
  argument here:** `DESIGN.md` Phase 4's *Scope — in* says outright *"`countInstances` exposure to the
  pass executor (**execution is Phase 7, tag v0.5**)"*, which settles the owner *and* the milestone in
  one line, and no site cited it. It is now cited, and the `[v0.5]` tag it carries travels with the
  hand-off.
- **V8-3 — the per-sample premise is deleted rather than kept under a tag.** Three sources were read
  directly and none supports it. App D gives the values only (*"rainStrength smoothed by
  wetness/drynessHalflife"* and the two siblings) and says nothing about what advances the filter or
  when. App D's cadence model — which §4.7.4 cited *in the same sentence* — says everything
  *"refreshes on program switch (per-program location cache + redundant-upload skip; matrices always
  upload)"* — the trailing clause restored per round nine's V9-9, which found it truncated inside the
  parenthesis with a supplied `)` and no ellipsis; it strengthens rather than weakens the reading
  below — and a refresh
  there is an **upload**, not a re-sample: the redundant-upload skip is only meaningful if the value
  is already computed, and RESEARCH.md §4.2 says the same of the state barrier. RESEARCH.md §4.4 puts
  the actual sampling at **frame start**, once per frame. And `DESIGN.md` Phase 6's *Scope — in*
  requires that session to specify *"halflife → per-tick exponential decay formula, **time-corrected**"* —
  a function of elapsed time, under which a second evaluation inside one frame advances by ≈0. Round
  eight offered an alternative: retain the caution with an `[A]` tag pointing at Phase 6. That is
  refused, because an `[A]` tag marks a *working assumption* and this document has no basis for the
  assumption in either direction — tagging it would preserve an unsupported claim in a costume. What
  replaces it is the honest form, which is a flag in §G1.1's sense: the rule stands on **idempotence
  alone** (`glUniform*` is idempotent on the bound program, and the replay's only purpose is to change
  which drain window each upload lands in), and the reason the protocol says "re-upload" rather than
  "re-run" is stated as *this document asserts no property of Phase 6's providers*, whose sampling
  cadence and smoothing math are Phase 6's own *Scope — in*. The rule Phase 6 must implement is
  unchanged; only the support under it is.
- **V8-5 — the second home is dropped, not qualified.** §4.2.3 establishes the governing mechanism
  correctly two sentences before it goes wrong: a class declared in a Gradle build script is compiled
  into *that script's* class scope. The same mechanism disposes of the second candidate. A "shared
  script plugin under `gradle/`" in this project means an `apply from:` script — that is exactly what
  the template's three are, at `build.gradle` ll. 100, 238 and 239 `[V:template]` — and an applied
  script plugin is compiled into its **own** class scope, so `new SeamClasspathArguments(...)`, the
  literal form all three code blocks use, does not resolve from the applying script without an
  `ext`-indirection this document never mentions. Presenting the two as equivalent work ("Neither
  exists in the template today") would steer an implementation session toward the cheaper-looking
  option, which is the broken one. The section now names `buildSrc` (or an included build carrying a
  **precompiled** script plugin, which is the one genuine alternative) and says explicitly that a
  plain `apply from:` script will not serve, with the class-scope reason attached. §12 item 4b carries
  the same correction; its test hook is unchanged, because *"`./gradlew :engine:test --dry-run`
  configures without an unresolved-class error"* is precisely what makes this failure loud.
  **Round nine (V9-3) found this fix resting on the same gap it was written to close, and §0.9
  corrected it: the "`ext`-indirection this document never mentions" clause implies a workaround that
  does not exist — no indirection preserves the literal `new X(...)` call site — the decisive reason is
  compilation order rather than class-scope isolation, "equivalently … the same way" overstated the
  included-build option, and the mechanism carried no provenance tag and no source anywhere. It is now
  `[U]` with an open-question row at §11.3 item 10, settled by item 4b's own hook.**
- **V8-4 — the URL goes inline and the read date stays.** RESEARCH.md §0.2 defines `[V:web]` as
  verified against a live web source with the *"URL in §12.5 or inline"*, and §0.1 of this document
  commits to using the tags "with exactly those meanings". §12.5 is RESEARCH.md's own source index,
  which §G1.1 forbids this document from amending, so the only available branch is *inline* — and it
  is now taken at both live citation sites. §0.2 stamps `[V:web]` at 2026-07-24 *"unless noted"*, so
  the existing 2026-07-25 date is kept as the note rather than harmonized away; the 403 that sent the
  read to the docs.gl mirror is recorded with it, on the precedent of §4.2.6's pin row that does the
  same.

**Inputs this session read beyond the build session's list**, each because a finding turned on it:
`PHASE_1_REVIEW_8.md`; RESEARCH.md **§3.2**, **§4.2**, **§4.4**, **App A.3** and **App D** (the
`countInstances`/`instanceId` provenance V8-2 and V8-6 turn on, and the three smoothed uniforms and
cadence note V8-3 turns on); and **`DESIGN.md` Part II, the *Scope — in* / *Scope — out* bullets of
Phases 5, 6 and 7** — the read V8-2 and V8-3 both turn on. That last one is disclosed rather than
assumed: §G1.1 bars a *build* session from other phases' specs, and the standing precedent — round
eight relied on it too, and disclosed it in its own §0.1 — is that a session auditing an **ownership
claim** may read the spec that settles the ownership. `build.gradle`'s three `apply from:` sites are
template ground truth and were already in the assigned list; they are named here because V8-5 turns
on them specifically. **One network fetch** was made, `https://docs.gl/gl4/glGetError`, solely to
confirm that the URL V8-4 requires inline resolves and that §4.7.4's block quote is verbatim against
it. It is, word for word including the conditional tail. No other network use.

**The lesson worth recording.** Round eight's meta-finding held up: re-derived from source rather
than adopted, both of the §5 findings it traced to inherited reasoning were real, and the discipline
it prescribes — do not promote a review's supporting argument into §5 without re-checking it — is
now this document's second consecutive round of evidence for the same rule. But the sweep found
something one level down that round eight itself did not catch. Round eight named six sites for V8-1
and this fix-up edited **nine**: §12 item 22 carried the same *"no mutating call"* formulation in a
review hook, and the document's closing paragraph still counted seven verify sessions and four
fix-ups. Neither is a defect round eight missed — they are sites its *finding* did not turn on but
its *fix* does. **A review names the sites its finding turns on; a fix-up owes the sites its edit
turns on, and that is always the larger set.** Three sites were also checked and correctly left
alone — `DrawService`'s javadoc and `[D-P1-33]` describe the composite loop but name no owner, and
§1.2 has no `countInstances` row — which is recorded so round nine can tell a considered omission
from an oversight.

**§G1.3 status at the time:** that fix-up altered **§5** at the same two rows as the one before it.
§5.2's **GL-error row** carried V8-1's facade/per-context correction (including the new contract
statement that a non-empty trailing drain does not imply a Phase 6 upload failed) and V8-3's rewritten
property (i); §5.2's **non-verbs row** carried V8-2's retarget of the composite `countInstances` loop
from Phase 5 to Phase 7, with Phase 6 named for the `instanceId` upload — which also changed that row's
consumer list, removing Phase 5 and adding Phase 6. **Round nine (V9-7) found the resulting set larger
than the row's own header could carry — Phase 6 requests no absent verb — and §0.9 widened the header
to cover adjacent owners rather than narrowing the set, which readmits **Phase 5** for the buffer
estate the composite loop's draws run inside. So the removal recorded in this sentence is superseded;
the addition is not.** **No service signature was added, removed or
changed by any of it:** the facade's verb list, every service interface, every handle type and every
value type were byte-for-byte what rounds seven and eight both reviewed. A **ninth verify session** was
therefore required before Phase 2, Phase 3 or any other dependent could consume this document, and
until that verdict existed the doc was **not** a valid dependency input (§G5.3). Two things narrowed
what that session inherited: no finding was left unapplied and none was refused, so there was no
re-derivation debt; and of the three §5-touching corrections, **only V8-2 changed what a dependent
phase does** — V8-1 and V8-3 corrected statements *about* protocols whose behavior is unchanged, so a
Phase 6 session reading the corrected §5 builds the same thing, with two premises it can now trust.
That ninth session has since run (`PHASE_1_REVIEW_9.md`). **This subsection records round eight only
and is no longer the document's current state — see §0.9.**

### 0.9 Fix-up session addendum (round nine — 2026-07-25)

A ninth verify session ran after §0.8's fix-up — `PHASE_1_REVIEW_9.md` (V9-1 … V9-11), returning
PASS-WITH-CORRECTIONS: **six corrections, five notes, zero blocking**, one correction and three of the
notes touching §5. **All six corrections are applied and none was refused**; one (V9-4) is applied
**narrower** than its fix shape suggested, one (V9-3) was reshaped under derivation, and **all five
notes are applied** — the reason is in the first design call below, and it is not that they were free.

**Round nine's meta-finding, and what this session did about it.** Round eight's rule — *a review's
supporting argument is not evidence* — held for a third round. Round nine's is one level down: *a
fix-up that re-derives every finding can still under-scope its own **sweep**, and the sites it misses
are the ones its correctness elsewhere hides.* V9-2 is the proof: the `GLError` javadoc was missed
*because* §0.8's signature-invariance claim is true, so no `Where` entry ever pointed inside
ll. 1905–2079. Both rules were treated as aimed at this session. Every load-bearing claim was
re-derived at its source before it was written here — `DESIGN.md` Part II's Phase 3/4/5/6/7 *Scope*
bullets for V9-1, V9-4 and V9-8, RESEARCH.md §4.2/§4.4/App A.1/App D for V9-1 and V9-9, the template's
three `apply from:` sites and the three printed call blocks for V9-3 — and the sweep was run by grep
over every formulation changed, not over the sites round nine named. **Round nine named 29 distinct
editable sites across its eleven findings — 19 for the six corrections, ten more for the five notes —
and all 29 are edited.** A thirtieth pointer, `PHASE_1_REVIEW_8.md`'s `Resolutions` row for V8-1, is
evidence and is untouched. **Six further sites** were reached by the sweep rather than by a `Where`
entry, and are listed in `PHASE_1_REVIEW_9.md` under `Neighbours swept`; the first of them —
`GLDevice.drainErrors()`'s own javadoc, which claimed *"empty when clean"* of a call that also returns
empty when it elides — is the second defect found inside ll. 1905–2079 by sweeping a block no finding
pointed into, and is the clearest vindication of round nine's rule.

**The design calls this session made**, recorded as arguments rather than outcomes, because a fix-up
session gets no adversarial review of its own and the next session can only attack reasoning that is
written down:

- **V9-1 — the claim is corrected in place, the hand-off is kept, the elision is kept, and the
  branch that would have closed the phase does not work.** Round nine left the fix shape open between
  (a) rewording §5.2's clause, which alters §5 and owes a tenth verify session, and (b) deleting the
  clause from §5.2 and keeping the substance in §4.7.4 and §11.4, which it described as possibly
  leaving §5 unchanged. **(b) is not available, and the reason is textual rather than tactical.**
  §G1.3's rule is *"if corrections **altered the doc's Cross-phase interfaces section**"* — the
  section, not the interfaces — and a deletion alters it exactly as a rewording does. This document's
  own precedent settles it twice over: §0.7 and §0.8 each declare *"this fix-up altered §5"* for
  corrections that were **prose-only, at these same two rows**, and each triggered a verify session on
  that basis; round nine itself ruled §5 changed while stating that *"no correction changes what a
  dependent phase builds"*. Leaving §5.2 alone is not available either — it would leave the contract
  section asserting what §4 had retracted, which is round nine's own objection. So the only honest
  question left was *what* §5.2 should say, and the answer is what is true.
  **The claim was false on three limbs, each re-derived.** *Uniqueness:* §4.7.4 concedes four lines
  above that dropping the elision *"would bound the window against all GL"* and rejects it on **cost**,
  not soundness, and `[D-P1-30]` states the complement — so the document described two sound remedies
  and named the second "the only" one. *Effectiveness:* the case handed off is *"between two of Phase
  6's sweeps"*, and `DESIGN.md` §G3.2 and RESEARCH.md §4.4 put foreign GL throughout the frame,
  interleaved with the gbuffers chain — one drain at a frame-driver point bounds the frame-boundary
  gap and no interior gap. *Expressibility:* `drainErrors()` takes no argument and the elision is
  contract, so in exactly the configuration the remedy targets it elides, clears no flag, and the error
  survives. All four sites are corrected (§4.7.4, §5.2, §11.4, and §0.8 by appended pointer), the two
  limits now travel with the hand-off, and Phase 7 is told the route by which it can request a forcing
  verb rather than being handed one it did not ask for.
  **What was deliberately *not* done, and this is the more important half.** The elision decision is
  **not re-opened**. Round nine established that the cost ledger the elision was kept against omits a
  cost the elision *creates* — a recurring foreign error re-enters `[D-P1-32]`'s replay every frame,
  re-uploading ~90 uniforms per program set, disabling nothing — and that including it *can* invert the
  comparison. It also said, correctly, that whether it *does* invert it is a judgement it did not make.
  Making that judgement here would be a design call arriving through a correction, which is precisely
  the move round eight's rule forbids. So the omitted cost is now **stated** at the three sites that
  priced the replay as once-per-disable (§4.7.4, §7, `[D-P1-32]`), the ledger is described as
  incomplete rather than as settled, and the re-weighing is left to whoever wants to argue it with its
  own evidence. **A future session re-opening it will find the cost written down and the decision
  undefended by this one** — which is the honest state, not a hedge.
- **The four notes that touch §5 were decided *after* V9-1, and that ordering is the argument.** Once
  V9-1 forces a §5.2 edit, adopting V9-7, V9-8 or V9-10 costs nothing in cadence, so the temptation
  round nine warned about — deciding a note by what it does to the gate — is removed rather than
  resisted. Each was then taken on its merits, and each is a real improvement: V9-7 because a row
  headed *"the phase that would request it"* cannot carry a phase that requests nothing; V9-8 because
  an untagged hand-off to a phase whose own milestone is v0.1 exit reads as v0.1 work; V9-10 because a
  disclaimer whose antecedent is the wrong noun invites the next reader to think §5.2 says less than it
  does. Had V9-1 come out the other way, the same four would still have been applied and a tenth
  session owed anyway — which is worth saying, because it means the ordering was a discipline and not
  a licence.
- **V9-7 — the breadth is kept and Phase 5 comes back with it.** The alternative was removing Phase 6
  from the non-verbs row, which is what the header literally licenses. It is rejected because §5.2 is
  written to be *sufficient on its own*, and a Phase 6 session reading only §5 would then not learn
  that the `instanceId` upload inside Phase 7's loop is its own entry point. So the **header** is
  widened instead — the column carries the requester of an absent verb, and, where a row names an
  adjacent owner of the served work instead, that owner. **Widening a header readmits what it
  previously excluded, and this one readmits Phase 5**, whose buffer estate the composite loop's N
  draws run inside (`DESIGN.md`'s read/write/flip law) — a stake §4.7.4 and §3 both keep in prose while
  §5.2 had stopped pointing at it. Naming 7, 6, 5, 3 and 4 is the whole seam, which is what a row
  claiming sufficiency owes.
- **V9-4 — the fix is a citation and an owner is *not* named, and §0.8's precedent is withdrawn.**
  Round nine allowed that this may not be a defect. Derivation says the row's attribution is unsourced
  but the seam is real: `DESIGN.md` gives the scale **factor** to Phases 3 and 4 (*"stored; applied by
  Phase 4"*, *"scale/flip storage"*), the buffer **dimensions** to Phase 5 (*"display size ×
  render-quality multiplier"*), and **applying** the sub-viewport to Phase 7 (*Scope — in* part (a),
  `[v0.5]`), and says nothing at all about who multiplies. Phase 5 has no sub-viewport bullet. **So the
  correction is to report the silence, not to fill it** — naming Phase 7 instead of Phase 5 would
  repeat the original error with a different digit, and §G5.3's integration review is the instrument
  for a seam this shape. **No §11.5 request is raised**: §G1.1 requires flagging a *conflict* with
  `DESIGN.md`, and this is a silence, which is a different thing. The consequence for §0.8 is stated
  rather than quietly dropped: its V8-2 bullet cited this row as *"a distinction this document already
  made correctly"*, and that support is **withdrawn**. V8-2's conclusion is untouched and now rests on
  `DESIGN.md` Phase 4's *"execution is Phase 7, tag v0.5"* — which is V9-8's second half, and a better
  citation than either argument previously printed.
- **V9-3 — the mechanism is kept, restated on its stronger reason, and tagged `[U]`.** Three things
  were wrong and all three are re-derived. The decisive reason an `apply from:` script cannot export
  the class is **compilation order** — the applying script is compiled in full before it executes —
  not class-scope isolation, which is the weaker half. The *"without an `ext`-indirection"* clause
  implies a workaround that does not exist: exporting a `Class` needs `newInstance`, exporting a
  factory needs no `new`, and **every route changes all three printed call sites**, so the true
  statement is stronger than the one the document made against itself. And *"equivalently … the same
  way"* overstated the included-build option, which needs a `settings.gradle` wiring *and* an apply
  where `buildSrc` needs neither. **The provenance gap is closed the way RESEARCH.md §0.2 prescribes
  rather than by asserting harder**: the mechanism is `[U]`, §11.3 item 10 is its open-question row,
  and §12 item 4b's existing hook is named as the experiment that settles it. Fetching Gradle's docs
  to upgrade the tag was available and was **declined**, with the project owner: the brief records
  that no network use is needed, the claim is settled cheaply and loudly at implementation time, and a
  `[U]` that says so is more honest than a `[V:web]` bought at the cost of an undisclosed read. The
  §4.2.3 text is corrected **to** §12 item 4b's flat form, not the reverse — item 4b was the more
  accurate of the two all along, which is itself worth recording.
  **Round ten (V10-4) withdrew that last sentence, and §4.2.3's corresponding claim with it.** Item 4b
  was not left "flat": the same edit rewrote it too, adding the compilation-order qualifier and the
  `ext` clause — `PHASE_1_REVIEW_9.md`'s `Resolutions` records §12 item 4b among V9-3's edited sites —
  so both texts were moved toward the same new content and neither was corrected *to* the other's prior
  form. §4.2.3 had also quoted item 4b with five words dropped from the middle and no ellipsis, which
  is V9-9's defect at a second site. §4.2.3 now states the substance without a quotation — *"§12 item
  4b states the same conclusion and now carries the same reason; the two texts agree"* — which is true
  and cannot go stale again. **What survives is everything that mattered:** the conclusion, the
  compilation-order reason, the deleted `ext` escape hatch, the default-package requirement, the `[U]`
  tag and §11.3 item 10. Only the account of which text was corrected toward which is withdrawn.
- **V9-6 — the marker convention is treated as binding on this document even though §G1.3 is not.**
  §G1.3 requires no markers; round nine said so. But sixteen decisions carry them, and §0.6's V6-1 used
  a *missing* marker as evidence that a fix-up never ran — so an incomplete marker record is a defect
  measured against a standard this document chose for itself, and declining it would leave the
  document's own accountability device unreliable at exactly the decisions three rounds have edited
  most. `PHASE_1_REVIEW_8.md` is added to `[D-P1-30]` and `[D-P1-32]`, round nine is added wherever
  this session amended a decision, and `[D-P1-30]`'s *"(round seven)"* label — on a sentence now
  carrying round eight's URLs — is corrected to say which round supplied what.
- **The arithmetic note is fixed in live prose and deliberately left standing in the addenda.**
  *"43 program switches per frame"* reuses a registry cardinality as a per-frame event count:
  RESEARCH.md says 43 **slots**, App A.1 counts the two virtual `*_pre` programs and the sixteen-element
  deferred/composite arrays inside that 43, and a real pack binds a fraction of them while §4.4's
  push/pop semantics let one slot bind more than once. §7 was already careful (*"there are 43
  **slots**"*); §4.7.4 and §11.4 were not, and are now. **§0.7 and §0.8 keep the old figure**, because
  they are superseded records and the convention this document has followed since §0.4 is that history
  is pointed at, not rewritten — §0.8's V8-1 bullet carries the pointer. The direction of the residual
  error favoured the conclusion being defended, which is the reason it was worth correcting rather than
  noting.

**Inputs this session read beyond the build session's list**, each because a finding turned on it:
`PHASE_1_REVIEW_9.md`; `PHASE_1_REVIEW_8.md` **read-only, including its `## Resolutions` section** (V9-2
and V9-6 are findings *about* that section's claims, so auditing them required reading it — it is not
modified); **`DESIGN.md` Part II, the *Scope — in* / *Scope — out* bullets of Phases 3, 4, 5, 6 and 7**
— V9-1, V9-4 and V9-8 all turn on them, and the read is disclosed rather than assumed on the standing
precedent §0.8 records, that a session auditing an **ownership claim** may read the spec that settles
the ownership; RESEARCH.md **§4.2** (the ~90 built-in uniforms and the 43 slots), **§4.4** (the
per-frame flow V9-1's effectiveness limb turns on), **App A.1** (what the 43 actually counts) and
**App D**'s cadence note (V9-9's quotation). `build.gradle`'s three `apply from:` sites and the three
printed `new SeamClasspathArguments(...)` blocks are template ground truth already in the assigned
list, named here because V9-3 turns on them specifically. **No network use of any kind.** Round nine
re-verified the pin table at ~04:48 UTC on 2026-07-25 and both `docs.gl` URLs at source; that was the
third pin observation inside two hours and a fourth buys nothing. **No adversarial sub-agents were
used, and the choice is disclosed because §G1.3 is silent and the call is therefore this session's.**
Round nine used them under a hard re-derivation gate and recorded that one agent breached its
instructions; with a sweep this narrow and every quote requiring re-derivation anyway, a delegated
finding would have been a hop rather than a saving — which is round eight's reasoning, adopted here on
its merits rather than by precedent.

**The lesson worth recording.** Round nine's rule is that a fix-up's sweep is its risk, and the test
of it is not whether the named sites were edited — three consecutive rounds have passed that test —
but whether the **unnamed** ones were. The mechanism it identified is worth stating in general form,
because it will recur: **a true claim about a document can suppress the sweep of the region it is true
about.** *"No signature changed"* is true, so no `Where` column ever pointed inside the signature
block, so the javadoc inside it went five rounds carrying a sentence §5.2 had already corrected. The
defence is not more care; it is grepping the *formulation* rather than visiting the *sites*, which is
what this session did and what its `Where` column reflects. One further observation, recorded here
because the brief asked for it to be raised rather than decided: **every verify session so far has paid
for the absence of a commit per fix-up.** `git HEAD` is still the original build-session draft (2159
lines against today's 3709) and all six fix-ups are uncommitted working tree, so no verify session can
check signature invariance byte-for-byte — round nine had to reconstruct it on three independent lines
and said so. It is a workflow matter, not a document defect; §11.5 is for requests against RESEARCH.md
and `DESIGN.md` and correctly gains nothing; it was **raised with the project owner, who directed that
it be noted here and left unchanged**. The tenth session inherits the same cost and this note.

**§G1.3 status at the time:** that fix-up altered **§5**, at the same two rows as the last two rounds.
§5.2's **GL-error row** carried V9-1's correction of the remedy claim (it is one of two remedies, it
bounds only the gap spanning the frame boundary, and it is subject to the same elision), the
recurring-foreign cost consequence, and V9-10's rescoped property (i); §5.2's **non-verbs row** carried
V9-7's widened header with Phase 5 readmitted and V9-8's `[v0.5]` tag and Phase 4 citation. **No service
signature was added, removed or changed by any of it:** the facade's verb list, every service interface,
every handle type and every value type were byte-for-byte what rounds seven, eight and nine all
reviewed — V9-2's correction is a **javadoc sentence inside** the signature block, not a declaration,
and the block was swept end to end for exactly that reason. A **tenth verify session** was therefore
required before Phase 2, Phase 3 or any other dependent could consume this document, and until that
verdict existed the doc was **not** a valid dependency input (§G5.3); Phase 2, Phase 3 and everything
downstream stayed blocked. Three things narrowed what the tenth session inherited. **No finding was
left unapplied and none was refused**, so there was no re-derivation debt; where a fix was narrowed
(V9-4) or reshaped (V9-3) the argument is above and in `PHASE_1_REVIEW_9.md`'s `Resolutions`. **No
correction changed what a dependent phase builds** — Phase 6's rung-2 protocol was unchanged in every
particular, Phase 7's composite loop and `instanceId` split were unchanged, and what moved is what
Phase 7 is *told* about a remedy it has not yet placed and what Phase 6 is told about why a clean
replay happens. And **one question was left open on purpose**: whether the replay cost the elision
creates inverts the decision to keep it. It is written down, it is not defended here, and it is a
design call rather than a correction.

That tenth session has since run (`PHASE_1_REVIEW_10.md`), returning PASS-WITH-CORRECTIONS with **no
§5 change** — the first round in four at which no correction touches the Cross-phase interfaces
section. **This subsection records round nine only and is no longer the document's current state —
see §0.10.**

### 0.10 Fix-up session addendum (round ten — 2026-07-25)

A tenth verify session ran after §0.9's fix-up — `PHASE_1_REVIEW_10.md` (V10-1 … V10-4), returning
PASS-WITH-CORRECTIONS: **two corrections, two notes, zero blocking**, and — for the first time in four
rounds — **no finding whose fix touches §5 on the shape chosen**. Both corrections are applied, both
notes are applied, **none is refused and none is narrowed**. One note (V10-3) offered two fix branches
with different cadence consequences; the cheaper one was taken, on its merits, and what it leaves is
recorded below rather than left to be discovered.

**Round ten's own account of where its findings came from, and what this session did about it.** Both
corrections came from the instruction round nine gave round ten — *audit the **unnamed** neighbours* —
and both sit one row away from a site an earlier round edited correctly. V10-2 is the sharper: round
nine's `Resolutions` declared §6's two GL-error rows *"checked and correctly left alone"*, and the
claim was true of the rung-2 row and false of the 3→4 row three rows below it, with a wrong line number
in its own justification as the fingerprint. That is round nine's rule — *a true claim about a document
can suppress the sweep of the region it is true about* — turned on round nine's own fix-up. It was
treated as aimed at this session too: every load-bearing claim below was re-derived at its source
before it was written here, and the sweep was run by grep over the formulations changed rather than
over the four sites round ten named.

**The design calls this session made**, recorded as arguments rather than outcomes, because a fix-up
session gets no adversarial review of its own and the next session can only attack reasoning that is
written down:

- **V10-3 — branch (a) is taken, and the reason is not that it closes the phase.** The finding is a
  taxonomy defect: §4.7.4's absent-verbs header enumerated two kinds of last-column entry — the
  requester, and the adjacent owner of the served work — while the column has carried a **third** kind
  since before V9-7 widened it. The face-culling row names **Phase 3**, which is neither: `:mod`
  applies `backFace.*` through `GlStateManager` so Phase 3 requests nothing (§1.2), and `DESIGN.md`
  ll. 1053–1055 gives the *served work* to Phase 7. What Phase 3 owns is the **engine-flag ownership
  map**, a *required output* of its spec (`DESIGN.md` ll. 777–782) whose worked example routes
  `backFace.*` to Phase 7 — the deliverable that decides who would ever request the verb. Both halves
  re-derived at source. Two fixes were available: **(a)** widen §4.7.4's header a second time, or
  **(b)** widen it and §5.2 l. 2846's restatement as well. (b) alters §5 and owes an eleventh verify
  session; (a) does not. **The branch was decided on whether §5 is still true and still sufficient
  under (a), and it is.** §5.2's clause — *"where a row names an adjacent owner of the served work
  instead, that phase too … the instanced-draw row is the only one today"* — is scoped to the
  **second** kind, and the face-culling row is not one of those, so the sentence stays true by its own
  terms and by reference to a header that now says which rows are which. Nothing a dependent builds
  changes; no reader is misled about who does what, because the face-culling row's prose has said
  plainly all along that Phase 3 produces the map and Phase 7 is where `DESIGN.md` routes the flags.
  **What branch (a) does not reach, stated because it is the cost and not an oversight:** §5.2's own
  consumer column carries two entries of the third kind — *"**3** (the App F.1 flag-ownership map that
  settles face culling; and the `const`-scan that detects `countInstances` at all)"* — under a row
  header that still enumerates two kinds. That is a residue, it is knowingly left, and it is left
  because it is an imprecision in how a column describes itself rather than a defect in what the column
  says. §G5.3's integration review is the instrument that reads every doc's §5 against its siblings; if
  an eleventh session or that review wants the taxonomy complete at §5.2, this paragraph is where the
  decision to leave it is recorded, with its reason, rather than absent.
- **V10-1 — the multiplier is restored rather than the aggregation dropped, and the choice serves a
  reader this document has already promised something to.** The defect was arithmetic: §4.7.4's
  sentence supplied both multiplicands — *"one replay per program set per frame"* and ~90 uniforms per
  program switch — and then labelled the product of one of them *"per frame"*, which holds only if the
  frame sweeps one program set. The same paragraph asserts the opposite four lines earlier. Two fixes
  were available and the smaller one was **not** taken: dropping the per-frame framing would have
  matched the four sibling sites that already say *"per program set"* and would have been correct, but
  it leaves whoever re-weighs the elision comparing a per-**set** cost against a per-**switch** cost.
  §0.9 refused to re-weigh the elision and justified the refusal by writing the omitted cost down; that
  refusal is only sound if the record is usable, so the record was made per-frame on both sides. **No
  new quantity was introduced and the "43" was deliberately not re-imported** — §7 already bounds the
  switch count carefully as *"43 **slots**"*, and reusing a registry cardinality as a per-frame event
  count is the exact defect round nine removed from live prose. The sentence points at §7 instead.
- **V10-4 — the quotation is dropped rather than repaired.** §4.2.3 quoted §12 item 4b with five words
  removed from the middle and no ellipsis, and characterised item 4b as the text that was *not* changed
  — in the same fix-up that changed it. Repairing the quotation was available; removing it is better,
  because the substantive claim never needed a quotation. What §4.2.3 has to say is that the two texts
  agree, and they do. The characterisation of the document's own history is withdrawn at §4.2.3 and
  **pointed at, not rewritten, in §0.9**, which is this document's convention for a superseded record.
- **§5 is left byte-identical, and that includes its per-revision changelog row — a ruling, not an
  omission.** §5.2's opening row (the `GLDevice` + seven services row) currently reads *"Changed in
  this revision (§0.9)"*. Every prior fix-up that added a §5 entry also relabelled its predecessor's
  from *"this revision"*, and copying that habit here would have been a mistake: **relabelling it
  alters §5, and §G1.3's trigger is textual.** Round ten's §2 item 1 upholds that reading on §G1.3's
  own words and on its third bullet's *"no §5 change outstanding"*, and §0.9 argued the same thing
  from the other direction — *a deletion alters the section exactly as a rewording does*. A row whose
  job is to record per-revision changes to §5 is owed **no entry for a revision that changes nothing in
  §5**, and the parenthetical *"(§0.9)"* already names which revision the phrase means, so nothing is
  ambiguous and nothing is stale in substance. Leaving it untouched is the reading of §G1.3 this
  document has applied three times running; editing it to look tidy would have cost an eleventh verify
  session for a cosmetic gain, which is the inverse of gate-gaming and just as wrong.
- **No `[fix-up: …]` marker changed, and that is checked rather than skipped.** §0.6's V6-1 established
  the convention and V9-6 completed it: a marker records the review findings that **amended a decision**
  in §11.1's log. Round ten's corrections edit *applications* of `[D-P1-32]` — §4.7.4's exception
  paragraph and §6's 3→4 row — but no decision-log text is amended, at `[D-P1-30]`, `[D-P1-32]`,
  `[D-P1-33]`, `[D-P1-35]` or anywhere else. Adding a round-ten marker to a decision this session did
  not touch would make the device report something false, which is the failure V6-1 used it to detect.

**Inputs this session read beyond the build session's list**, each because a finding turned on it:
`PHASE_1_REVIEW_10.md` in full (the assignment); `DESIGN.md` §G1.3 ll. 151–162 and §G5.3 ll. 400–425
(the contract, and the gating invariant this round's outcome turns on); `DESIGN.md` Phase 3's
*Scope — in* ll. 777–782 and Phase 7's ll. 1050–1055 (V10-3's ownership derivation, re-derived at the
line rather than adopted from the review); `PHASE_1_REVIEW_9.md` **read-only, including its
`## Resolutions` section**, which V10-2 and V10-4 are findings *about*; and `PHASE_1_FIXUP_6_BRIEF.md`
for the deliverable shape §0.4–§0.9 follow. **No network use of any kind** — the pin row held at a
fourth observation in round ten's §0.3 and no finding here turns on a platform fact. **No adversarial
sub-agents were used, and the choice is disclosed because §G1.3 is silent and the call is therefore
this session's**: both corrections are arithmetic and cross-reference work over sites that have to be
held in view together, which is the shape delegation serves worst, and every quotation and line number
below was derived at the line.

**The lesson worth recording.** Round nine's rule was that a true claim about a document can suppress
the sweep of the region it is true about. Round ten found the same mechanism one level in, and it is
worth stating in the sharper form: **a fix-up's own "checked and correctly left alone" list is a sweep
suppressor.** §0.9's list said §6's two GL-error rows were verified rather than assumed — and the
sentence was true of one row and false of its neighbour, which is exactly the shape that stops the next
reader looking. The list is still worth keeping; round ten's §2 item 14 verified every other entry in it
and found them all correct, and a considered omission really is distinguishable from an oversight only
if it is written down. What the entry needed was the discipline the `Where` column already has:
**name the site to the line, and re-resolve the line.** §0.9's entry cited *"ll. 2907, 2906"* for two
rows that are at 2907 and 2910, and the wrong number is the tell that the second row was reasoned about
rather than read. This session's own citations were re-resolved against the finished file for that
reason, not against the file it started from.

**The workflow cost every verify session has paid is gone, and this is the round that records it.**
§0.9 raised — at the project owner's direction, and without deciding it — that no verify session since
round seven could check signature invariance byte-for-byte, because `git HEAD` was the 2159-line
build-session draft and every fix-up sat uncommitted; round ten's §2 item 10 restated the limit and
named its remedy exactly: *"A commit per fix-up would replace all of the above with one `git diff`."*
**The project owner committed the working tree during this session** (`1d55717`, capturing the
3709-line state round ten reviewed; this session ran no git command that writes). So this fix-up is the
first whose §5 invariance and signature invariance are **proved rather than reconstructed** — one
`git diff` against that baseline, ten hunks, none of them inside §5 and none inside the signature
block. The four internal cross-references §2 item 10 had to lean on still agree; they are no longer
what the claim rests on. An eleventh session, if one is ever commissioned, inherits a baseline instead
of a reconstruction.

**§G1.3 status:** this fix-up **did not alter §5**. Every one of §5's four subsections is byte-for-byte
what round ten reviewed, and the three §5.2 rows a reader might expect to have moved are each named
here with why they did not: the **GL-error row** needed nothing because it already carries property
(ii)'s general two-cause form and states the recurring-foreign consequence with **no figure** to
correct; the **non-verbs row** needed nothing under V10-3's branch (a), whose clause stays true by its
own scope and by reference to §4.7.4's rewritten header; and the **per-revision changelog row** is owed
no entry for a revision that changes nothing in §5, as argued above. **No service signature was added,
removed or changed:** no correction this round reaches the §4.7.4 signature block at all — ll. 1905–2079
as round ten read them, and the `git diff` above has no hunk inside that range — not a declaration and
not a javadoc, so the facade's verb list, every service interface, every handle type and every value type
are what rounds seven through ten all reviewed. Because §5 is unchanged, §G1.3's *"re-verify only if §5
changed"* trigger **does not fire**, and by its own words **this fix-up closes the phase**. Under
§G1.3's third bullet the phase is now **verified** — its latest verdict is PASS-WITH-CORRECTIONS, all
resolutions are recorded (`PHASE_1_REVIEW_10.md`, `## Resolutions`), and no §5 change is outstanding —
so `PHASE_1_DOC.md` is a **valid dependency input** (§G5.3 invariant) and **Phase 2, Phase 3 and
everything downstream unblock.** Two things are left standing on purpose and belong to whoever comes
next rather than to this session: **§5.2's two third-kind consumer entries**, the recorded cost of
V10-3's branch (a), which §G5.3's final integration review is the named instrument for; and **the
elision question** round nine opened and round ten sharpened — whether the replay cost the elision
creates inverts the decision to keep it. That cost is now written down correctly at all five sites, it
is defended by nobody, and it is a design call rather than a correction.

---

## 1. Scope & boundaries

### 1.1 What Phase 1 owns

Phase 1 owns the *frame* that every other phase is built inside, and nothing that happens within
it. Concretely:

- The Gradle module split (`:engine`, `:mod`, `:conformance`) and the package layout inside each.
- The **seam** (D-6) stated as a testable constraint, plus its enforcement mechanism.
- The `engine.gl` facade's **shape**: interface set, handle model, `GLCapabilityProfile`, and the
  recording/replay implementation used for headless tests.
- Template conversion: root package, mod id, Blossom templating, `mcmod.info`/`pack.mcmeta`,
  access-transformer posture.
- The GPL-3.0-or-later license swap (D-7) and the source-header / third-party-notice conventions.
- The version pin table and the re-pin procedure (OQ-2).
- Mixin **wiring** (manifest attribute, config-file layout, SRG policy, refmap handling, dev flags).
- The lwjglx posture (OQ-21).
- The headless JUnit baseline in `:engine` and `:conformance`.
- Logging channel names, debug-flag namespace, and the user-facing error-channel convention.
- The `mod.compat` bail-registry **mechanism**.
- CI workflow adjustments for the module split, with extension points left for Phase 2.

### 1.2 Adjacent concerns, and who owns them

Every concern this document touches but does not own — the §G9 anti-sprawl device:

| Concern this doc brushes against | Owned by |
|---|---|
| Conformance harness content: scenes, capture drivers, image diff, fixture downloader, golden-file format and update workflow, headless-GL-in-CI viability | **Phase 2** |
| Everything pack-format: discovery, `#include`/preprocessing, option discovery, `shaders.properties` model, identity macros | **Phase 3** |
| Stage registry contents, the 43 program slots, backup-chain semantics, compile/link flow | **Phase 4** |
| All GL *policy*: texture formats, the fixed texture-unit map, ping-pong/flip rules, clear colors, buffer sizing, resize | **Phase 5** (buffers) and **Phase 6** (uniforms/samplers) |
| The Mixin **hook catalog** — which classes, which methods, which `@At` targets (App E) | **Phase 7** (with additions from **Phase 10** and **Phase 13**) |
| GL context creation mechanics, HiDPI, resize (OQ-3); CleanMix divergences on hot injections (OQ-4) | **Phase 7** |
| Coexistence **policy**: which mod ids bail, detection mechanics, the user-visible message text (OQ-5) | **Phase 10** |
| GUI framework evaluation — whether ModularUI is fit for generated screens (OQ-9) | **Phase 12** |
| Texture systems: the noise texture's generation, `_n`/`_s` companion atlases, custom-texture loading and their unit assignment — this doc supplies only the transfer verbs (§4.7.4) | **Phase 13** |
| KHR_debug labels/groups, sampler objects, async compile, GC posture | **Phase 14** |
| Kirino backend port itself (as opposed to the seam that makes it possible) | **G8/S5** |

### 1.3 A note on what "foundation" does *not* mean here

Phase 1 does not pre-decide anything a later phase is assigned. Where this document names a type
that a later phase will fill (`StageRegistry`, `PackConfiguration`), it names only the *package it
lives in*, never its contents. The one place this rule is deliberately stretched is the
`engine.gl` facade, because the spec assigns its design here and every later phase's headless tests
depend on it existing.

---

## 2. Architecture overview

### 2.1 The three modules

The §G3.1 layout, refined with concrete Gradle project paths, source roots, and Java packages.
Names in §G3.1 are preserved verbatim; this section adds `engine.log`, `engine.diag`, and the
`.internal` convention, which are refinements the spec's "everything else in G3 is yours to
refine" clause permits.

```
Schmaloogium/                    (root Gradle project — aggregator only, no code)
├── engine/                      :engine
│   ├── src/{main,test}/java/com/schmaloogium/engine/…
│   └── src/testFixtures/resources/profiles/          (GLCapabilityProfile fixtures, §8.3)
├── mod/                         :mod
│   ├── src/main/java/com/schmaloogium/mod/…
│   ├── src/test/java/com/schmaloogium/mod/…                  (the four :mod tests, §8.1)
│   ├── src/main/java-templates/com/schmaloogium/Reference.java
│   ├── src/main/resource-templates/{mcmod.info,pack.mcmeta}
│   └── src/main/resources/{schmaloogium.*.mixin.json,assets/…}
└── conformance/                 :conformance
    └── src/{main,test}/java/com/schmaloogium/conformance/…
```

**`:engine`** — pure JVM. Java 25. Zero dependencies on Minecraft, Forge, Cleanroom, Mixin, or
LWJGL. Testable headless with JUnit alone.

| Package | Contents | Filled by |
|---|---|---|
| `com.schmaloogium.engine.pack` | pack discovery, file model, dimension folders, sources | Phase 3 |
| `com.schmaloogium.engine.preprocess` | `#include`, macro header, preprocessor, option discovery/rewrite | Phase 3 |
| `com.schmaloogium.engine.config` | `shaders.properties` model, options/profiles/screens, ID-file grammar, persistence | Phase 3 (+12) |
| `com.schmaloogium.engine.registry` | stage registry (modern-superset shape), program slots, backup chains, per-program state | Phase 4 |
| `com.schmaloogium.engine.buffers` | framebuffer/color-buffer *policy* — ping-pong, flips, clears, formats, sizing | Phase 5 |
| `com.schmaloogium.engine.uniforms` | built-in uniform model, cadences, smoothing math, value-provider interfaces | Phase 6 |
| `com.schmaloogium.engine.expr` | custom-uniform expression language | Phase 11 |
| `com.schmaloogium.engine.gl` | **the GL facade** — interfaces, `GLCapabilityProfile`, recording/replay impl | **Phase 1** |
| `com.schmaloogium.engine.log` | the zero-dependency `Log`/`LogSink` SPI and channel constants | **Phase 1** |
| `com.schmaloogium.engine.diag` | `EngineDiagnostic` and the user-facing-channel vocabulary | **Phase 1** |

**`:mod`** — the Cleanroom mod. Depends on `:engine`.

| Package | Contents | Filled by |
|---|---|---|
| `com.schmaloogium.mod.core` | `@Mod` entry, lifecycle, config, engine bootstrapping | Phase 1 (skeleton) / Phase 7 |
| `com.schmaloogium.mod.glue` | adapters: world-state sampling, Forge registries, resources, **the LWJGL3 implementation of `engine.gl`** | Phases 1 (facade impl shape), 6, 7, 9 |
| `com.schmaloogium.mod.mixin` | all Mixin classes, SRG-targeted, declared via the `MixinConfigs` manifest attribute | Phases 7, 10, 13 |
| `com.schmaloogium.mod.gui` | pack selection + options screens | Phase 12 |
| `com.schmaloogium.mod.compat` | coexistence detection, **bail registry** | Phase 1 (mechanism) / Phase 10 (policy) |

**`:conformance`** — the Phase 2 harness. Depends on `:engine`. Never ships in the mod jar.
Phase 1 stands up the module, its JUnit wiring, and its dependency edge; Phase 2 fills it.

### 2.2 The dependency graph, and the seam

```
:conformance ──→ :engine ←── :mod
                    ↑            ↑
              (no MC, ever)   (all MC lives here)
```

The §G3.1 constraint, quoted exactly and adopted unchanged as this phase's central deliverable:

> **`:engine` compiles with no classpath entry from Minecraft/Forge/Cleanroom/Mixin/LWJGL, and
> `:mod` never reaches into `:engine` internals beyond its published interfaces.**

§4.3 turns each half of that sentence into a mechanically checkable test.

**Why this is a requirement and not hygiene** `[V:web §5.2]` `[Q:OQ-20]`. RESEARCH.md §5.2
confirms Kirino-Engine as a real CleanroomMC artifact (393 commits, updated 2026-07-24) whose
README states it will "not be compatible with existing render mods" — it replaces the whole
pipeline. RESEARCH.md §7.2 states the consequence directly: "the render backend under Schmaloogium
may be replaced wholesale within the mod's lifetime — the core must survive a backend swap." The
seam is therefore load-bearing against the project's highest-weight strategic risk, not a code-style
preference. That is why §4.3 spends three enforcement layers on it and why §10.3 specifies a
backend-swap drill rather than treating OQ-20 as somebody else's problem.

### 2.3 Naming: root package and mod id

`[D-P1-1]` `mod_id = schmaloogium`, `root_package = com.schmaloogium`.

The template derives the generated `Reference` class's package as `"${root_package}.${mod_id}"`
`[V:template build.gradle blossom block]`, which for these values would produce
`com.schmaloogium.schmaloogium`. `:mod`'s Blossom block therefore **overrides the `package`
property to `root_package` alone**, so `Reference` lands at `com.schmaloogium.Reference` — a single
class at the namespace root, shared by all three modules' notion of "the mod's identity", and
consistent with the `com.schmaloogium.{engine,mod,conformance}` tree. Publishing follows: group
`com.schmaloogium`, artifact `schmaloogium` (the template's `publishing.gradle` already does
`setGroupId(root_package)` / `setArtifactId(mod_id)` `[V:template]`).

### 2.4 Key types introduced by this phase

| Type | Module | Role |
|---|---|---|
| `GLDevice` | `engine.gl` | Root facade handle; hands out the seven services, the capability profile and the GL-error drain |
| `GLCapabilityProfile` | `engine.gl` | Immutable value object; RESEARCH.md §4.1's probe set + extension set + the macro-header fields (`glslVersion`, `vendor`, `renderer`); serializable as a test fixture |
| `ProgramHandle`, `ShaderHandle`, `TextureHandle`, `FramebufferHandle`, `UniformLocation` | `engine.gl` | Opaque handles — the engine never holds a raw GL int. **Four** handle types; there is no renderbuffer (§4.7.3) |
| `GLError`, `GLErrorKind` | `engine.gl` | Driver errors as data. Attribution is per **drain window** — one call named when the window held one mutating **facade** call, the sweep named when it held many (`[D-P1-32]`); a window may also hold an error no facade call caused, since the GL flag is per-context (§4.7.4) — and this is the signal §G2.4's **rung 2** acts on; rung 1 is Phase 11's expression isolation and never reaches GL (§6) |
| `RecordingGLDevice`, `GLCallLog`, `GLCall`, `ScriptedResponses`, `ReplayAssertions` | `engine.gl.record` | The headless test backend |
| `Log`, `LogSink`, `LogChannels` | `engine.log` | Zero-dependency logging SPI + the fixed channel name list |
| `EngineDiagnostic`, `DiagnosticSeverity`, `UserChannel` | `engine.diag` | Loader-neutral error records that `:mod` routes to chat / GUI / log |
| `Lwjgl3GLDevice` (+ its service impls) | `mod.glue` | The only place in the codebase that may call LWJGL |
| `CapabilityProbe` | `mod.glue` | Builds a `GLCapabilityProfile` from a live context; dumps fixtures |
| `CompatCheck`, `CompatVerdict`, `BailRegistry` | `mod.compat` | The bail mechanism (policy is Phase 10) |
| `SchmaloogiumMixinPlugin` | `mod.mixin` | Reserved `IMixinConfigPlugin` slot on the MOD-phase config |

---

## 3. Contract conformance map

Phase 1 owns almost no pack-facing contract surface — the pack contract (RESEARCH.md §3, Apps A–F)
belongs to Phases 3–13. The in-scope contract rows for this phase are the ones the facade and the
debug affordances must satisfy, plus the vocabulary rule.

| Contract item | Provenance | Design element satisfying it | Tag |
|---|---|---|---|
| Startup probes **GL version** | RESEARCH.md §4.1 step 1 | `GLCapabilityProfile.glVersionMajor` / `.glVersionMinor`, plus `atLeast(int,int)` | `[V:observed]` |
| Startup probes **`GL_MAX_DRAW_BUFFERS`** | RESEARCH.md §4.1 step 1 | `GLCapabilityProfile.maxDrawBuffers` | `[V:observed]` |
| Startup probes **`GL_MAX_COLOR_ATTACHMENTS`** | RESEARCH.md §4.1 step 1 | `GLCapabilityProfile.maxColorAttachments` | `[V:observed]` |
| Startup probes **`GL_MAX_TEXTURE_IMAGE_UNITS`** | RESEARCH.md §4.1 step 1 | `GLCapabilityProfile.maxTextureImageUnits` | `[V:observed]` |
| **Mipmap generation requires GL 3.0** | RESEARCH.md §4.1 step 1 | `GLCapabilityProfile.supportsMipmapGeneration()` — derived, `atLeast(3,0)`; consumed by Phase 5's composite-mipmap policy | `[V:observed]` |
| Extension set available to the engine | DESIGN.md Phase 1 scope (additive to RESEARCH.md §4.1 — see the flagged delta below) | `GLCapabilityProfile.extensions()` + `hasExtension(String)`; the consumer is Phase 3's on-demand `MC_<GL_extension>` macros (RESEARCH.md §3.5) | `[A]` |
| **Standard macro header** — `MC_GL_VERSION`, `MC_GLSL_VERSION`, `MC_GL_VENDOR_*`, `MC_GL_RENDERER_*` injected after `#version` in every pack shader | RESEARCH.md §3.5 | `GLCapabilityProfile.glVersionMajor`/`.glVersionMinor` (`MC_GL_VERSION`), `.glslVersion`, `.vendor`, `.renderer` — the profile carries every input the header needs; Phase 3 formats the macros and owns `MC_VERSION`, `MC_OS_*` and the option macros, none of which are GL state | `[V:doc]` |
| `GL_MAX_VERTEX_ATTRIBS` / `GL_MAX_TEXTURE_SIZE` available to the engine | Additive to RESEARCH.md §4.1's probe set, on the same reasoning as the extension set (§4.7.2) | `GLCapabilityProfile.maxVertexAttribs` (Phase 10's extended vertex format binds at 10/11/12 and will grow), `.maxTextureSize` (Phase 5's buffer sizing) | `[A]` |
| `shaders.debug.save` equivalent — dump processed sources | RESEARCH.md App F.8; DESIGN.md §G4.5 | `-Dschmaloogium.debug.saveSources` reserved in §4.9.3's flag namespace; the dump itself is Phase 3's | `[V:doc]` |
| **KHR_debug labels/groups in dev** — the other affordance §G4.5 reserves from day one | DESIGN.md §G4.5 | `DebugService` (§4.7.4) exists as an interface at v0.1 so call sites can label objects immediately; `-Dschmaloogium.debug.glLabels` (§4.9.3) gates it; the implementation is `v0.5` / Phase 14 (§9) | `[V:design]` |
| Declaring `centerDepthSmooth` enables a **center-depth readback** | RESEARCH.md §3.2, App A.3 for the directive; §4.4 for the synchronous per-frame stall `[V:observed]` | `FramebufferService.readDepthPixel(...)` (§4.7.4) — the verb only; which pixel, at which moment, and the halflife smoothing are Phase 6's policy. The PBO/fence async form is RESEARCH.md §6.2's modernization, deferred to Phase 14 | `[V:doc]` |
| Noise texture and pack custom textures require **texel upload** | RESEARCH.md §4.1 step 4 ("create noise texture"), App F.5 (`texture.noise`, custom-texture source forms) | `TextureService.upload(TextureHandle, TextureData)` (§4.7.4) — the verb only; generation, formats and unit assignment are Phase 13's (with Phase 5 owning formats) | `[V:observed]` |
| `atlasSize` / `eyeBrightness` are **`ivec2`** uniforms | RESEARCH.md App **D.3** (`atlasSize`, `terrainTextureSize` — camera/matrices/screen) and App **D.1** (`eyeBrightness`, `eyeBrightnessSmooth` — held item/player) | `UniformService.upload(loc, int, int)` (§4.7.4); the values and their cadences are Phase 6's | `[V:doc]` |
| `blendFunc` is an **`ivec4`** uniform (current blend `srcRGB`, `dstRGB`, `srcA`, `dstA`) | RESEARCH.md App **D.4**, §3.4 | `UniformService.upload(loc, int, int, int, int)` (§4.7.4). Phase 6 owns the value provider and its cadence (`DESIGN.md` §G5.1 puts App D's inventory at **v0.1**); Phase 9 owns per-draw dynamics at v0.3. The value is *observed* from `GlStateManager` per §G4.6 — that observation is Phase 6's, not the facade's | `[V:doc]` |
| Geometry programs may declare the **ARB form** (`#extension GL_ARB_geometry_shader4` + `const int maxVerticesOut = N`) | RESEARCH.md §3.1, App A.3, §6.2 | Handled **upstream of the facade**: RESEARCH.md §6.2 lists core GL 3.2 geometry shaders with internal translation as a modernization *opportunity*, which this project adopts, so the ARB form is a source-level rewrite in the Phase 3 front-end / Phase 4 compile path. `ShaderService` therefore exposes no pre-link program-parameter verb, and says so (§4.7.4) | `[V:doc]` |
| **Compat-profile baseline; `GL_QUADS` stays available** | RESEARCH.md §6.1 `[D-9]` | The facade is profile-agnostic by construction: `DrawService` exposes a `fullscreenQuad` primitive whose backend chooses `GL_QUADS` or the triangle-strip fallback. No core-profile-only entry point appears in any interface | `[V:doc]` |
| **No UBOs**; per-program uniform upload with location caching | RESEARCH.md §6.1 `[V:doc]` | `UniformService` exposes only default-block uniform uploads keyed by `UniformLocation`. No uniform-block entry point exists — the facade cannot express a UBO | `[V:doc]` |
| **Never compile against `org.lwjglx`** | RESEARCH.md §6.1, DESIGN.md §G2.2 | `enable_lwjglx=false` (§4.6) plus the §4.3 bytecode assertion, which lists `org.lwjglx` among the forbidden prefixes | `[V:mcp]` |
| **All engine GL goes through the facade**; no direct LWJGL outside `mod.glue` | DESIGN.md §G4.6 | The §4.3 bytecode assertion enforces the `:engine` half mechanically. The `mod.glue`-only half is a convention plus a `:mod` scan restricted to `org.lwjgl` references outside `com.schmaloogium.mod.glue`. §4.7.4 adds the other half of §G4.6 — the backend's obligation to issue `GlStateManager`-cached state through `GlStateManager` | `[V:design]` |
| `const int countInstances = N` — **instanced re-render** with an incrementing `instanceId`, on a **composite/deferred** program | RESEARCH.md §3.2 and App A.3 for the directive; App **D.4** declares `instanceId` an `int` **uniform** ("0 original, 1..N instanced copies"). **the only observed form is RESEARCH.md §4.4's**, which is the only place the instancing *loop* is observed — "optional sub-viewport (`scale.<prog>`), `countInstances` instancing loop", in the composite-pass line | A **caller-side loop** over `DrawService.fullscreenQuad()` with `UniformService.upload(instanceIdLoc, i)` between copies. GLSL 120 has no `gl_InstanceID` (RESEARCH.md §3.5), so no single instanced draw can vary the uniform per copy — which is why the facade carries **no** instanced verb (`[D-P1-33]`, and §4.7.4's absent-verbs table says so). **The loop is Phase 7's, at `[v0.5]`**: `DESIGN.md` Part II names the `countInstances` instancing loop in Phase 7's *Scope — in*, under **Composite/final execution**, tagged `[v0.5]` there, and says it a second time and more explicitly in Phase 4's *Scope — in* — *"`countInstances` exposure to the pass executor (**execution is Phase 7, tag v0.5**)"* — which is the strongest citation on the point and the source of the milestone. Phase 5 — which owns the buffer estate the composite passes read and write — has no pass-execution bullet at all and puts "when copies/clears *happen* in the frame" in its *Scope — out*. The `instanceId` **upload** the loop makes between copies is **Phase 6's** entry point: `DESIGN.md` Phase 6's cadence model carries `instanceId` among the per-draw dynamics "at their hooks (Phases 7/9/10 invoke)" | `[V:doc]` |
| `const int countInstances = N` on a **gbuffers/shadow** program — the other half of the same directive | RESEARCH.md §3.2 and App A.3 state it as a **vertex-stage** opt-in beside `mc_Entity`, `mc_midTexCoord` and `at_tangent` and impose **no program restriction**; `uniform int instanceId` sits in the *common* uniform block, above the GBuffers heading; RESEARCH.md §4.2 lists "instance count" among the per-program state carried by **all 43 slots** | **No Phase 1 design element — stated rather than omitted.** "The geometry" there is vanilla terrain/entity geometry drawn by Minecraft's own draw calls through Phase 7's hooks, which never reach the facade, so the caller-side loop above cannot satisfy this half and no facade verb would. Ownership is split and named: **Phase 3** detects the directive in its `const`-scan, **Phase 4** carries it as the per-slot instance count, **Phase 7** owns the re-render itself if the case proves real (`[D-P1-35]`, §11.4) | `[A]` — the *scope* is inferred: RESEARCH.md observes no non-composite instancing loop, so this is an open case handed onward, not designed here |
| `alphaTest.<prog>` — per-program alpha-test state | RESEARCH.md App F.7 | `StateService.alphaTest(AlphaTestState)` (§4.7.4) — the verb only; which program carries which value is Phase 5/6 policy | `[V:doc]` |
| `blend.<prog>` — per-program blend state | RESEARCH.md App F.7 | `StateService.blend(BlendState)` plus `snapshot()`/`restore()` (§4.7.4); the per-buffer routing decision is Phase 5's | `[V:doc]` |
| `scale.<prog>` — per-program sub-viewport | RESEARCH.md App F.7 | `StateService.viewport(x, y, w, h)` (§4.7.4), which §4.7.4's inclusion criterion already names the sub-viewport as its reason for existing. **Who computes the rectangle, this document does not say, and the reason is that `DESIGN.md` does not either.** Its three inputs have named owners — the **scale factor** is parsed and stored by **Phase 3** (*"per-program render-state overrides (alphaTest/blend/**scale**/flip/enabled — stored; applied by Phase 4)"*) and carried per slot by **Phase 4** (*"scale/flip storage"*); the **buffer dimensions** it multiplies are **Phase 5's** (*"Sizing: display size × render-quality multiplier; `superSamplingLevel`"*); and applying the result is **Phase 7's**, whose *Scope — in* part (a) lists *"`scale.<prog>` sub-viewports [v0.5]"* under **Composite/final execution**. Phase 5's *Scope — in* has no per-program sub-viewport bullet. The multiplication itself is assigned nowhere, so naming an owner here would be this document ruling on another phase's boundary — §G5.3's integration review is where a seam this shape gets settled | `[V:doc]` for the verb and the directive; the ownership is **`DESIGN.md`'s silence**, reported rather than filled |
| Pack-facing vocabulary used **verbatim** in identifiers | DESIGN.md §G4.1 | The facade deliberately contains no pack vocabulary at all (it is below that layer), so no synonym risk is introduced here. The phases that do carry pack vocabulary (3, 5, 6) inherit §G4.1 directly | `[V:design]` |

### 3.1 Flagged delta (reported, not smoothed over — §G1.1)

**DESIGN.md's Phase 1 scope adds "extension set" to `GLCapabilityProfile`; RESEARCH.md §4.1 does
not list it among the probes.** RESEARCH.md §4.1 names four probes (GL version, `GL_MAX_DRAW_BUFFERS`,
`GL_MAX_COLOR_ATTACHMENTS`, `GL_MAX_TEXTURE_IMAGE_UNITS`) plus the GL-3.0 mipmap gate. DESIGN.md
line 614 says "GL version, max draw buffers, max color attachments, max texture units, extension
set — the §4.1 probe set", attributing the extension set to §4.1.

**Ruling:** include the extension set. Per §G0.1, RESEARCH.md wins on conflict — but this is an
*addition*, not a contradiction: §4.1 describes what the reference implementation probes at startup,
while RESEARCH.md §3.5 independently requires that `MC_<GL_extension>` macros be emitted on demand, which is
impossible without an extension set. The DESIGN.md attribution is loose; the requirement is real and
sourced elsewhere in RESEARCH.md. Recorded as `[A]` provenance rather than `[V:observed]`, so no
later reader mistakes it for observed reference behavior. Also recorded in §11.3.

---

## 4. Detailed design

### 4.1 Template ground truth this design is written against

Everything below is `[V:template]`, read from the checkout on 2026-07-24. It matters because the
spec asks for a plan "against the template's actual build scripts", and several of these facts
change what the plan has to do.

| Fact | Value | Consequence |
|---|---|---|
| Branch | **`main`**, not `mixin` | There is **no** mixin config JSON, **no** `MixinConfigs` manifest attribute, **no** `mixin { }` or refmap block, and no mixinbooter dependency anywhere. All Mixin wiring in §4.5 is authored from nothing. `compileOnly "com.cleanroommc:sponge-mixin:0.20.13+mixin.0.8.7"` is the only mixin-adjacent line. |
| Build shape | Single project. `settings.gradle` has **no `include` lines**; `rootProject.name = rootProject.projectDir.getName()` | The module split is a genuine restructuring, not a reconfiguration. |
| Unimined block | `unimined.minecraft { version "1.12.2"; mappings { mcp("stable","39-1.12") }; cleanroom { loader "0.5.17-alpha"; … } }` at the **root** project | Must move wholesale into `:mod`. The loader version is an **inline literal** — §4.2 promotes it to a property. |
| Access-transformer wiring | `cleanroom { accessTransformer "${rootProject.projectDir}/src/main/resources/$access_transformer_locations" }` | Hardcodes `rootProject.projectDir` — the single most module-split-hostile line in the build. Defused by `use_access_transformer=false` (§4.4); the one-line fix is recorded for the phase that first needs an AT. |
| Blossom | `net.kyori.blossom` 2.2.0 on `sourceSets.main` only; convention dirs `src/main/java-templates` and `src/main/resource-templates`; `{{ token }}` syntax; java property `package` = `"${root_package}.${mod_id}"` | Must be re-declared per source set in `:mod`. The `package` derivation needs the §2.3 override. |
| `jar` manifest | `doFirst` writes `ModType=CRL` always; `ContainedDeps`/`NonModDeps` if `contain` non-empty; `FMLCorePlugin`/`FMLCorePluginContainsFMLMod` if `is_coremod`; `FMLAT` if `use_access_transformer` | This is the block §4.5 extends with `MixinConfigs`. |
| `contain` configuration | Custom config; `implementation.extendsFrom(contain)`; `jar { into('/') { from configurations.contain } }` + `ContainedDeps`/`NonModDeps` attrs | CRL jar-in-jar. Considered and rejected for `:engine` (§4.2.5). |
| Shadow | `com.gradleup.shadow` 9.5.1, `enable_shadow=false` ⇒ `shadowJar.enabled=false`; remap task selected as `enable_shadow ? remapShadowJar : remapJar` | Considered and rejected for `:engine` (§4.2.5). Active remap task is `remapJar`. |
| Artifacts | `jar` → classifier `dev`, `finalizedBy(remapJar)`; `remapJar` → the production jar | `:engine` merging must happen in `jar`, i.e. **before** remap. |
| JUnit | `enable_junit_testing=true` ⇒ `junit-jupiter:6.0.3` + `junit-platform-launcher`; `test { useJUnitPlatform(); javaLauncher = 25 }` | Reusable verbatim in all three modules. **No `src/test/` exists yet.** |
| Java | `java.toolchain.languageVersion = 25`, foojay resolver 1.0.0, explicit `VERSION_25` on `compileJava`/`compileTestJava`, UTF-8 everywhere | Moves to a root `subprojects {}` block. |
| Gradle | wrapper `9.6.1`; all three CI workflows pin `gradle-version: 9.6.1` and Temurin 25 | Unchanged by the split. |
| lwjglx | exactly one site: `if (enable_lwjglx.toBoolean()) { compileOnly "com.cleanroommc:lwjglx:1.0.0" }`; `enable_lwjglx = true` | **compileOnly only** — no runtime injection, no run-config flag. See §4.6. |
| `.gitignore` | already contains `**/build/` | No change needed for subproject build dirs. |
| CI artifact paths | all three workflows reference `build/libs` (root-relative) | Breaks under the split — §4.11. |

### 4.2 The Gradle module split

#### 4.2.1 `settings.gradle`

```groovy
pluginManagement {
    repositories { /* unchanged — gradlePluginPortal, mavenCentral, forge,
                      fabric, wagyourtail releases, arcseekers releases,
                      wagyourtail snapshots */ }
}

plugins {
    id 'org.gradle.toolchains.foojay-resolver-convention' version '1.0.0'
}

rootProject.name = 'Schmaloogium'

include ':engine'
include ':mod'
include ':conformance'
```

`[D-P1-2]` **`rootProject.name` is pinned to the literal `'Schmaloogium'`** rather than derived from
the directory name. The template's derivation exists to work around an IntelliJ bug `[V:template
comment]`, but under a multi-project build the root name leaks into IDEA module keys and into
`publishing`; a literal removes the "clone into a differently-named directory and the build
changes" failure mode. The arcseekers repository entry stays — it is where the kappa fork lives
(§4.2.6).

#### 4.2.2 Root `build.gradle` — aggregator only

The root project holds **no code, no source sets, and no Unimined**. It declares plugin versions
once (so Gradle resolves the plugin classpath a single time) and applies the common Java
configuration to subprojects.

```groovy
plugins {
    id 'com.gradleup.shadow'                     version '9.5.1'   apply false
    id 'org.jetbrains.gradle.plugin.idea-ext'    version '1.4.1'
    id 'xyz.wagyourtail.unimined'                version '1.4.26-kappa' apply false
    id 'net.kyori.blossom'                       version '2.2.0'   apply false
}

allprojects {
    group   = root_package          // com.schmaloogium
    version = mod_version
}

subprojects {
    apply plugin: 'java-library'

    java {
        toolchain { languageVersion = JavaLanguageVersion.of(25) }
    }

    tasks.withType(JavaCompile).configureEach {
        options.encoding = 'UTF-8'
        sourceCompatibility = targetCompatibility = JavaVersion.VERSION_25
    }

    if (enable_junit_testing.toBoolean()) {
        dependencies {
            testImplementation 'org.junit.jupiter:junit-jupiter:6.0.3'
            testRuntimeOnly    'org.junit.platform:junit-platform-launcher'
        }
        tasks.named('test') {
            useJUnitPlatform()
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion = JavaLanguageVersion.of(25)
            })
            if (show_testing_output.toBoolean()) {
                testLogging { showStandardStreams = true }
            }
        }
    }
}

apply from: 'gradle/scripts/extra.gradle'
```

`idea-ext` stays applied at root because the `idea.project.settings` block is a root-only concept;
its `runConfigurations` entries are retargeted at `:mod`'s tasks (`:mod:runClient`,
`:mod:runServer`). The template's `moduleJavacAdditionalOptions` key `project.name + '.main'` becomes
per-module keys (`engine.main`, `mod.main`, `conformance.main`) — recorded because it is a silent
breakage otherwise.

#### 4.2.3 `:engine/build.gradle` — the seam, by construction

```groovy
// SPDX-License-Identifier: GPL-3.0-or-later
plugins {
    id 'java-test-fixtures'     // the shared GLCapabilityProfile fixtures live here (§8.3)
}

repositories {
    mavenCentral()
}

dependencies {
    // Production dependencies: NONE. This is the seam (D-6).
    // Test-only tooling is permitted; see the forbidden-coordinate list
    // in the architecture test.
    testImplementation 'org.ow2.asm:asm:9.10.1'     // bytecode scan, test scope only
}

// Hand the architecture test the exact classpath it must assert over — LAZILY.
// `.asPath` inside a task-configuration block resolves the configuration when the block
// runs, i.e. at configuration time: the same hazard §4.2.5 flags for the jar merge. A
// CommandLineArgumentProvider defers resolution to execution, and with its inputs annotated
// (@Classpath / @InputFiles) it keeps the test task's up-to-date checking honest — which
// matters here, because a seam violation added to :engine must re-run these tests.
tasks.named('test') {
    jvmArgumentProviders.add(new SeamClasspathArguments(
        sourceSets.main.compileClasspath,
        sourceSets.main.runtimeClasspath,
        sourceSets.main.output.classesDirs))
}
```

`SeamClasspathArguments` is a small `CommandLineArgumentProvider` emitting the three
`-Dschmaloogium.test.*` arguments the seam tests read. **It needs one shared home, and the two
candidates are not interchangeable.** A class declared inline in a Gradle build script is compiled
into *that script's* class scope: it is invisible to a sibling subproject's script, and a class in
the root `build.gradle` is invisible to `:engine/build.gradle` too. Three build files instantiate
this type (§4.2.3, §4.2.4, §4.2.4a), so "inline" means **three copies of the same class** — which is
not what the next sentence promises. It therefore lives in **`buildSrc`**, whose `main` output Gradle
puts on every project's buildscript classpath unconditionally and automatically. An included build
carrying a **precompiled** script plugin is the one genuine alternative, but it is not the same thing
and this document does not present it as one: it reaches a project's script scope only if the build is
wired in `settings.gradle` **and** the project applies the plugin, so it is two deliberate steps where
`buildSrc` is none.

**A plain `apply from:` script under `gradle/` does not serve**, and the decisive reason is not the
class-scope isolation this paragraph invoked to rule out inline — it is **compilation order**. The
applying script is compiled in full before it executes, and `apply from:` is a runtime statement, so
the name is unresolvable at *compile* of the applying script, before the applied script exists in any
form. That is stronger than "not resolvable by simple name", and it disposes of the workaround the
weaker form implies: **no indirection rescues the literal `new SeamClasspathArguments(...)`** that all
three code blocks print. Exporting a `Class` through `ext` requires `ext.X.newInstance(...)` at the
call site; exporting a factory closure requires a call with no `new` at all. Every route changes all
three call sites, so the honest statement is that the printed form cannot be made to work through
`apply from:` — not that it needs unstated wiring. **§12 item 4b states the same conclusion and now
carries the same reason; the two texts agree.**

The template's three scripts under `gradle/` are exactly that kind — `apply from:
'gradle/scripts/dependencies.gradle'` and its two siblings at `build.gradle` ll. 100, 238, 239
`[V:template]` — so naming them as an equivalent home would point an implementation session at the
cheaper-looking option that does not work. **The chosen option carries an unstated requirement of its
own, stated here rather than discovered at item 4b:** the three blocks print `new
SeamClasspathArguments(...)` with no `import`, so the class must sit in `buildSrc`'s **default
package**, or all three blocks gain an `import`. `buildSrc` does not exist in the template today, so
standing it up is real work and has its own checklist item (§12 item 4b) ahead of the three items
that wire it. §4.2.4 and §4.2.4a use the same form and point back here for the reason.

**Provenance, stated because this paragraph has none and the document's own rule requires it.** The
`[V:template]` tag above covers the **file facts** only — the three `apply from:` sites. The Gradle
mechanism itself (script-plugin compilation order, `buildSrc`'s automatic buildscript classpath entry,
the `ext` export shapes) is **`[U]`**: it originates in this session's reasoning about Gradle, no
Gradle documentation appears in §0.1's input table, and §0.3 scopes this document's web use to the
OQ-2 re-pin. RESEARCH.md §0.2 requires every `[U]` to carry an open-question row or be upgraded, so
§11.3 item 10 carries it. It is cheap to settle and the settling is already scheduled: §12 item 4b's
test hook — *"`./gradlew :engine:test --dry-run` configures without an unresolved-class error"* — is
precisely the experiment, and the failure it guards is loud rather than silent. Recorded as unverified
rather than asserted harder, because a correction resting on an unsourced mechanism is the defect
round seven caught at `[D-P1-30]` and this paragraph exists to correct a Gradle claim.

That is the whole file. **No `unimined` plugin, no `blossom`, no `shadow`, no
`gradle/scripts/dependencies.gradle`.** The only plugin beyond the root `subprojects` Java
configuration is core Gradle's `java-test-fixtures`, which exists solely to give the shared
capability-profile fixtures a source set that both `:conformance` and `:mod` can consume through a
dependency rather than a path (§8.3). This is the structural half of the enforcement: Unimined is
what injects the Minecraft configuration and the loader dependencies into a project, so a project
that never applies it structurally cannot have them. The architecture test in §4.3 exists to prove
that a future edit has not quietly undone it.

ASM appears only in `testImplementation`. It is not a forbidden coordinate (it is not Minecraft,
Forge, Cleanroom, Mixin, or LWJGL), and it never reaches production scope — the architecture test
asserts over `sourceSets.main`, not the test or `testFixtures` classpath. `[D-P1-3]` **The same
permission extends to `:mod`'s and `:conformance`'s test scope**, because C-2, C-3 and C-4 are
bytecode scans too and cannot be written without it (§4.2.4a, §4.2.4). It is a test-scope permission
in all three modules and a production dependency in none. The same is true of the fixtures source
set: it is test-scope by construction and invisible to `:engine`'s `main`.

#### 4.2.4 `:mod/build.gradle` — everything loader-facing

All the machinery the template put at root moves here verbatim, with four changes:

1. **The loader pin reads a property**: `cleanroom { loader cleanroom_loader_version }`.
2. **The AT path is project-relative** when it is eventually enabled:
   `"${project.projectDir}/src/main/resources/$access_transformer_locations"` — the fix for the
   `rootProject.projectDir` hardcode. Inert for v0.1 (§4.4).
3. **Blossom is re-declared** for `:mod`'s own `sourceSets.main`, with the §2.3 `package` override:

   ```groovy
   sourceSets.main {
       blossom {
           javaSources {
               property('mod_id',      mod_id)
               property('mod_name',    mod_name)
               property('mod_version', mod_version)
               property('package',     root_package)   // NOT "${root_package}.${mod_id}"
           }
           resources { /* the template's nine resource properties, unchanged */ }
       }
   }
   ```

4. **`:engine` is merged into the jar** (§4.2.5).

```groovy
dependencies {
    implementation project(':engine')

    // C-2 and C-3 are bytecode scans over :mod's own classes, so :mod needs the same
    // test-scope ASM :engine has (§4.2.3, [D-P1-3]).
    testImplementation 'org.ow2.asm:asm:9.10.1'
    testImplementation testFixtures(project(':engine'))
}

// :mod's test classpaths inherit Unimined's 1.12.2 dev dependencies, which drag in the
// legacy ASM shipped as asm-debug-all 5.x. That jar is SHADED: it carries the
// org.objectweb.asm packages itself, so against org.ow2.asm:asm it is a split package, not
// a version conflict. Gradle arbitrates versions per group:name and these are two different
// modules, so no resolutionStrategy can choose between them — exclusion is the instrument.
// ASM 5 cannot read Java 25 class files, so if its classes win classpath order, C-2/C-3
// fail with "Unsupported class file major version" rather than a seam message.
// The exclusion is declared on testImplementation, NOT on testRuntimeClasspath: C-2 and C-3
// are COMPILED against testCompileClasspath, and both resolvable test configurations extend
// testImplementation, so one line covers compile and runtime.
configurations.testImplementation {
    exclude group: 'org.ow2.asm', module: 'asm-debug-all'
}

// Hand C-2 and C-3 the exact classpath and class directory they must assert over —
// the same three properties, and the same lazy form, :engine (§4.2.3) and :conformance
// (§4.2.4a) inject. This module is where the hazard bites hardest: :mod's compileClasspath
// contains Unimined's TASK-PRODUCED remapped Minecraft artifact, so an eager .asPath would
// resolve an artifact whose producing task has not run.
tasks.named('test') {
    jvmArgumentProviders.add(new SeamClasspathArguments(
        sourceSets.main.compileClasspath,
        sourceSets.main.runtimeClasspath,
        sourceSets.main.output.classesDirs))
}

jar {
    archiveClassifier = 'dev'
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from project(':engine').sourceSets.main.output
    doFirst {
        manifest {
            def attrs = [:]
            attrs['ModType']      = 'CRL'
            attrs['MixinConfigs'] = mixin_configs        // §4.5
            if (use_access_transformer.toBoolean()) {
                attrs['FMLAT'] = access_transformer_locations
            }
            attributes(attrs)
        }
    }
    finalizedBy(tasks.named(remapTaskName).get())
}
```

The `contain`/`shadow`/coremod branches of the template's manifest block are retained but stay
inert (`enable_shadow=false`, `is_coremod=false`, empty `contain`), so re-enabling any of them later
is a property flip rather than a rewrite.

`gradle/scripts/dependencies.gradle` and `gradle/scripts/publishing.gradle` are applied **from
`:mod` only**. `dependencies.gradle` carries `compileOnly sponge-mixin` and the (now-disabled)
lwjglx line, both of which are meaningless outside `:mod`. `publishing.gradle` publishes the mod
artifact; `:engine` is not published separately (it is not a library anyone else consumes — it ships
inside the mod jar).

#### 4.2.4a `:conformance/build.gradle` — the empty slot, wired

Numbered `4.2.4a` rather than renumbering the sections after it, on the same reasoning as §12's
item 14b. Phase 1 stands the module up; Phase 2 fills it.

```groovy
// SPDX-License-Identifier: GPL-3.0-or-later
repositories {
    mavenCentral()
}

dependencies {
    implementation        project(':engine')
    testImplementation    testFixtures(project(':engine'))
    testImplementation    'org.ow2.asm:asm:9.10.1'      // C-4's bytecode half
    // Never :mod. C-4 asserts exactly that.
}

// The same lazy form §4.2.3 explains, for the same reason.
tasks.named('test') {
    jvmArgumentProviders.add(new SeamClasspathArguments(
        sourceSets.main.compileClasspath,
        sourceSets.main.runtimeClasspath,
        sourceSets.main.output.classesDirs))
}
```

**The `repositories` block is load-bearing and is easy to lose.** Repositories are per-`Project` in
Gradle with no inheritance, and the transitive externals of a project dependency resolve against the
**consuming** project's repositories. The root `subprojects {}` block (§4.2.2) adds
`testImplementation 'org.junit.jupiter:junit-jupiter:6.0.3'` to every subproject but declares no
repository; `:engine` declares its own (§4.2.3) and `:mod` inherits one by applying
`gradle/scripts/dependencies.gradle`. Without the block above, `:conformance` cannot resolve JUnit
and **`./gradlew build` — §12 item 15, the Impl gate — fails** at `:conformance:compileTestJava`
with "Cannot resolve external dependency … because no repositories are defined". Item 6's own hook
(`:conformance:compileJava`) cannot surface it, because the failure is in the *test* configuration.
That path — `build` → `check` → `test` → `compileTestJava` — is why §4.11 does not scope the build
step with `-x test`: Gradle's `--exclude-task` drops the named task together with the tasks reachable
only through it, and `compileTestJava` is required by `test` but not by `assemble`/`jar`, so
`build -x test` would drop the compile along with it and disarm this gate.

*This sentence used to tie the same path to §4.11's **ordering** as well. That half was wrong in both
directions and is deleted: the ordering's rationale is `[D-P1-24]`'s (a named step placed after
`build` never runs, because `build` runs the seam tests itself and goes red first), and under §4.11's
own ordering the named `:conformance:test` step — which depends on `compileTestJava` too — fails
first, so `build` never reaches `:conformance:compileTestJava` in CI at all. The gate is armed either
way, and armed under a legible name; only the inherited explanation was incorrect
`[fix-up: PHASE_1_REVIEW_7.md V7-5]`.*

`[D-P1-27]` **The repository is declared per-project, not hoisted into the root `subprojects {}`
block.** Hoisting is the shorter diff and it was rejected: it would inject `mavenCentral()` into
`:mod` **ahead of** `dependencies.gradle`'s CurseMaven/Modrinth entries, changing mod-dependency
resolution order for a reason unrelated to mod dependencies, and it would weaken §4.2.3's
by-construction argument for C-1 — that `:engine/build.gradle` declares, visibly and in one file,
exactly what `:engine` can see.

#### 4.2.5 Jar packaging: how `:engine` reaches the shipped jar

`[D-P1-4]` **`:engine`'s compiled classes are merged directly into `:mod`'s jar**, via
`jar { from project(':engine').sourceSets.main.output }`, executed **before** `remapJar` (the `jar`
task is `finalizedBy(remapJar)` in the template, so ordering is already correct).

The spec asks explicitly: "does `:engine` shade into the mod jar via the template's `contain`
configuration?" Answer: no, and here is the reasoning, because a later phase may want to revisit it.

| Option | Verdict |
|---|---|
| **Merge classes into the mod jar** (chosen) | One flat jar. No relocation question — `:engine` is our own code under the same GPL-3.0-or-later license and the same package root, so there is nothing to isolate. `remapJar` is a structural no-op over `:engine` classes because they reference no Minecraft type (which the §4.3 test proves), so passing them through the remapper is harmless. |
| `contain` (CRL jar-in-jar) | Rejected. The template's `contain` copies whole jars to the archive root and sets `ContainedDeps`/`NonModDeps`, which the loader extracts at mod-load time `[V:template]`. That machinery exists for third-party jars whose identity must be preserved; using it for a first-party module adds a load-time extraction step, an extra manifest contract, and a second classloading path, for no benefit. |
| Shadow plugin | Rejected. Flipping `enable_shadow=true` switches the active remap task to `remapShadowJar` and introduces a shadow↔remap interaction the template currently keeps disabled, with no relocation actually needed. Strictly more risk than the merge. |

**One caveat on the *expression*, not the decision.** `from project(':engine').sourceSets.main.output`
reaches across projects into another project's model at configuration time — the pattern Gradle's
configuration cache and project-isolation work flag. The merge itself is right; the implementation
session should prefer a dependency-derived form that produces the same jar contents — consuming
`:engine`'s artifact through a configuration, or at least
`from project(':engine').tasks.named('jar')` / `.map { … }` — so a Gradle upgrade does not turn the
packaging step into a debugging session. Recorded against §12 item 7, which is the Impl-gate item
that depends on this merge.

`:conformance` is never packaged. It has no place in the mod jar and no publication.

#### 4.2.6 Pin table (OQ-2)

All values **re-verified 2026-07-24** for this document. The spec's prediction held: `0.6.6-alpha`
was still current at re-verification time, but two releases (`0.6.5-alpha`, `0.6.6-alpha`) shipped
that same day, which is the cadence evidence, not a counterexample.

| Component | Pinned value | Where it lives after the split | Repository | Re-verified |
|---|---|---|---|---|
| **Cleanroom loader** | **`0.6.6-alpha`** | `gradle.properties` → `cleanroom_loader_version`, read by `:mod`'s `cleanroom { loader … }` | `https://repo.cleanroommc.com/releases` (injected by Unimined; note `maven.cleanroommc.com` **301-redirects** here) | 2026-07-24 — GitHub releases API and `maven-metadata.xml` `<release>` agree |
| Unimined (kappa fork) | `1.4.26-kappa` | root `build.gradle` plugins block | `https://maven.arcseekers.com/releases` — metadata at `…/releases/xyz/wagyourtail/unimined/xyz.wagyourtail.unimined.gradle.plugin/maven-metadata.xml` | 2026-07-24 — newest kappa build; **trap:** `maven.wagyourtail.xyz/releases` carries only upstream Unimined, topping out at `1.4.1`, with **zero** kappa versions. Do not "upgrade" to 1.4.1. |
| Gradle | `9.6.1` | `gradle/wrapper/gradle-wrapper.properties` + all three CI workflows | `https://services.gradle.org/versions/current` | 2026-07-24 (template value, unchanged) |
| Java toolchain | `25` | root `subprojects {}` + CI `setup-java` | Temurin via foojay resolver `1.0.0`; `https://api.adoptium.net/v3/info/available_releases` | 2026-07-24 (template value, unchanged) |
| Mappings | MCP `stable`, `39-1.12` | `:mod` Unimined `mappings { }` | Unimined-managed (no direct coordinate; a mappings change is a Unimined-release event, so it is checked with the Unimined row) | 2026-07-24 (template value, unchanged) |
| Mixin (compile-time) | `com.cleanroommc:sponge-mixin:0.20.13+mixin.0.8.7` | `:mod` via `dependencies.gradle`, `compileOnly` | `https://repo.cleanroommc.com/releases/com/cleanroommc/sponge-mixin/maven-metadata.xml` | 2026-07-24 (template value, unchanged) |
| Blossom | `2.2.0` | root plugins block, applied in `:mod` | gradlePluginPortal — `https://plugins.gradle.org/m2/net/kyori/blossom/net.kyori.blossom.gradle.plugin/maven-metadata.xml` | 2026-07-24 |
| Shadow | `9.5.1` | root plugins block, `apply false` (inert) | gradlePluginPortal — same metadata path under `com/gradleup/shadow/` | 2026-07-24 |
| idea-ext | `1.4.1` | root plugins block | gradlePluginPortal — same metadata path under `org/jetbrains/gradle/plugin/idea-ext/` | 2026-07-24 |
| foojay resolver | `1.0.0` | `settings.gradle` | gradlePluginPortal — same metadata path under `org/gradle/toolchains/foojay-resolver-convention/` | 2026-07-24 |
| JUnit Jupiter | `6.0.3` | root `subprojects {}` | mavenCentral — `https://maven-central.storage-download.googleapis.com/maven2/org/junit/jupiter/junit-jupiter/maven-metadata.xml` | 2026-07-24 |
| ASM (test-only: `:engine`, `:mod`, `:conformance`) | **`9.10.1`** | `testImplementation` in all three modules (§4.2.3, §4.2.4, §4.2.4a) | mavenCentral — metadata readable at `https://maven-central.storage-download.googleapis.com/maven2/org/ow2/asm/asm/maven-metadata.xml` (Google's Central mirror) or via `https://search.maven.org/solrsearch/select?q=g:org.ow2.asm+AND+a:asm&core=gav` | 2026-07-24 — **second-sourced**: the Central search API and the Google Central mirror both report `9.10.1` as `<release>`. Note both are transports over the same Maven Central dataset, so this is redundancy against one endpoint being unavailable, not two independent observations; `repo1.maven.org` metadata refuses direct requests (HTTP 403). Test-scope only, so a wrong guess fails the bytecode tests loudly rather than shipping. In `:mod`, Unimined's inherited `asm-debug-all` 5.x is **excluded** rather than out-voted: it is a shaded jar carrying `org.objectweb.asm` itself, so against `org.ow2.asm:asm` it is a **split package** and not a version conflict Gradle can arbitrate — no `force` line has any effect on it (§4.2.4, `[D-P1-3]`). |
| lwjglx | **dropped** (`enable_lwjglx=false`) | — | — | see §4.6 |
| ModularUI | **not pinned by this phase** | — | — | Phase 12 owns the dependency decision (OQ-9) |

**Nothing floats.** No dynamic versions (`+`, `latest.release`), no version ranges, no
`mavenLocal()`-sourced snapshots in any module. `dependencies.gradle` retains the template's
`mavenLocal()` entry, which stays last as the template comment requires, and is used for local
debugging only.

**The re-pin procedure** (this *is* the OQ-2 deliverable; the spike spec in §10.1 restates it in
§G4.4 form):

1. **Trigger, and scope.** Before every milestone tag (v0.1 … v0.5), before any release workflow
   run, and whenever a platform-caused failure is suspected. Never on a schedule, and never
   automatically — §G2.2 says versions are "pinned by Phase 1 and re-verified deliberately, never
   floated." Steps 2–3 are written for the loader row because it is the volatile one, but **every
   row of the table is re-checked at each milestone**, each against the coordinate in its Repository
   column. Any row whose coordinate no longer resolves is a finding in its own right.
2. **Query.** `GET https://repo.cleanroommc.com/releases/com/cleanroommc/cleanroom/maven-metadata.xml`
   and read `<release>`. Cross-check against
   `GET https://api.github.com/repos/CleanroomMC/Cleanroom/releases?per_page=10` — the maven
   metadata is authoritative for *resolvability*, the GitHub API for *release notes*. A tag that
   appears in one and not the other is itself a finding.
3. **Read the delta, and rule on it.** Diff release notes from the current pin forward. Flag any
   mention of: CleanMix, MixinBooter, Foundation, classloader, mod discovery, LWJGL, or the render
   path. These are the categories that have historically moved (the 0.6.0→0.6.6 window contained a
   MixinBooter-11 parity change, two CleanMix updates, a mixin-loading bug fix, and a Foundation
   classloader change). **A flag never blocks the bump by itself — it selects the verification the
   bump owes**, and the step terminates in one of exactly three rulings, recorded in `PINS.md`:

   | Ruling | When | What it costs |
   |---|---|---|
   | **Record only** | No flagged category appears | Step 5 as written |
   | **Extra verification** | A flagged category appears but names nothing this project uses | Step 5 plus one targeted check of the named area — for CleanMix/MixinBooter, `runClient` with `enable_mixin_debug=true` and a confirmed refmap (§12 item 33); for Foundation/classloader/mod discovery, a `runClient` to the main menu with the mod list confirmed; for LWJGL/render path, the §10.4 OQ-21 profile comparison |
   | **Block the bump** | The notes describe a behavioral change to something this document pins, asserts, or tests — the mixin manifest contract, the loader's classloading model, the GL context, or the run-configuration flags | Stay on the last known-good pin, open the question upstream (§7.7), and record the blocked attempt |

   Two operators applying this step to the same release notes must reach the same ruling; if they
   cannot, that ambiguity is itself the finding and the conservative branch (block) applies.
4. **Bump.** Edit `cleanroom_loader_version` in `gradle.properties`. One line, one reviewable diff.
   That is the entire point of promoting it out of `build.gradle`.
5. **Verify.** `./gradlew build` (all modules), `./gradlew :engine:test :mod:test :conformance:test`
   — all four seam constraints, which live in three different modules (§8.1) — and a manual
   `:mod:runClient` smoke run to the main menu, plus whatever check step 3's ruling added. Once
   Phase 2's harness exists, its runnable-before-renderer subset joins this step.
6. **Record.** Append a row to `PINS.md` (repo root): date, old pin, new pin, notable delta,
   verification result, and the person/session that did it. `PINS.md` is created by the Phase 1
   implementation session with the current row as its first entry.
7. **On failure at step 5:** revert to the last known-good pin, record the failure in `PINS.md` with
   the symptom, and open the question upstream (§7.7 engagement). A broken alpha never blocks a
   milestone — it blocks the *bump*.

`[D-P1-5]` The loader pin is a `gradle.properties` property rather than an inline literal precisely
so that steps 4 and 6 are trivial and auditable.

### 4.3 Seam enforcement — the testable constraint

The §G3.1 sentence has two halves. Each gets its own mechanism.

#### Half one: `:engine` has no MC/Forge/Cleanroom/Mixin/LWJGL classpath

**Layer 1 — by construction.** `:engine/build.gradle` (§4.2.3) applies no Unimined and declares no
forbidden coordinate. This is the primary guarantee; the tests below exist to catch regressions.

**Layer 2 — classpath assertion.** A JUnit test in `:engine`
(`com.schmaloogium.engine.SeamClasspathTest`) reads the `schmaloogium.test.compileClasspath` and
`schmaloogium.test.runtimeClasspath` system properties injected by the build (§4.2.3), splits them
on the path separator, and asserts that no entry's file name matches, case-insensitively, any of:

```
minecraft   forge      cleanroom    unimined
mixin       spongepowered           mixinextras
lwjgl       lwjglx     fmlcore      launchwrapper
```

Failure message names the offending entry and the configuration it came from, so the diagnosis is
immediate.

**Layer 3 — bytecode assertion (the one the Impl gate names).** A JUnit test
(`com.schmaloogium.engine.SeamBytecodeTest`) walks every `.class` file under
`schmaloogium.test.classesDir`, reads each class's constant pool with ASM, and collects every
referenced type name. It asserts that no referenced type starts with any of:

```
net.minecraft.        net.minecraftforge.   com.cleanroommc.
org.spongepowered.    org.lwjgl             org.lwjglx
zone.rong.mixinbooter cpw.mods.
```

This is strictly stronger than Layer 2: a compile-time-only leak, a reflective string constant that
happens to be a type name, or a dependency that arrives transitively through a future edit all show
up here. It is also the layer that survives a build-script refactor, because it asserts over the
*artifact*, not the configuration.

**Why both layers.** Layer 2 catches "someone added a dependency"; Layer 3 catches "someone wrote
code against a type that arrived some other way". Neither subsumes the other, and each produces a
different, actionable failure message.

#### Half two: `:mod` never reaches into `:engine` internals

`[D-P1-6]` **The mechanism is a package-naming convention, enforced by a mirror bytecode scan — not
the Java Platform Module System.**

The convention: within `:engine`, any package segment named `internal` is off-limits to `:mod`.
Public API lives at `com.schmaloogium.engine.<subsystem>.*`; implementation details that must be
package-visible across a subsystem live at `com.schmaloogium.engine.<subsystem>.internal.*`.

The test: `com.schmaloogium.mod.SeamInternalsTest` in `:mod` scans `:mod`'s compiled classes for any
referenced type matching `com\.schmaloogium\.engine\..*\.internal\..*` and fails with the referencing
class named.

**JPMS was considered and rejected.** A `module-info.java` in `:engine` exporting only API packages
would be a genuinely structural guarantee — the compiler would refuse the reference outright. But
`:mod` runs under Cleanroom's Foundation classloader (the LaunchWrapper replacement, RESEARCH.md
§5.1), on a flat classpath assembled by the loader, where the module graph does not exist. Putting
`:engine` on the module path while `:mod` is on the classpath makes `:engine` an automatic module at
runtime with all packages open, which means the guarantee evaporates exactly where it would matter
while adding real build complexity. The bytecode scan gives the same enforcement at the same moment
(build time) with none of the runtime risk.

A third, softer layer: `:mod` also gets a scan asserting that no `org.lwjgl*` reference appears
outside `com.schmaloogium.mod.glue`, which is the mechanical half of §G4.6's "no direct LWJGL calls
outside `mod.glue`'s facade implementation."

#### The constraint, restated for later phases to inherit

> **C-1** `:engine`'s `main` compile and runtime classpaths contain no artifact matching the
> forbidden-coordinate list, and `:engine`'s compiled classes reference no type under
> `net.minecraft.`, `net.minecraftforge.`, `com.cleanroommc.`, `org.spongepowered.`, `org.lwjgl`,
> `org.lwjglx`, `zone.rong.mixinbooter`, or `cpw.mods.`.
>
> **C-2** `:mod`'s compiled classes reference no type matching
> `com.schmaloogium.engine.*.internal.*`.
>
> **C-3** `:mod`'s compiled classes reference no type under `org.lwjgl` outside the package
> `com.schmaloogium.mod.glue`.
>
> **C-4** `:conformance` depends on `:engine` and never on `:mod` — asserted by
> `SeamConformanceDependencyTest` (§8.2), in the same classpath-plus-bytecode form as C-1.

C-1 is the Impl gate's "architecture test proving `:engine` has no MC/loader/mixin/LWJGL classpath."
C-1 through C-4 are non-negotiable for every later phase; a phase that needs to violate one has
found a design error and must flag it, not work around it.

### 4.4 Template conversion

#### 4.4.1 `gradle.properties`

| Property | Template | Schmaloogium | Note |
|---|---|---|---|
| `mod_id` | `modid` | `schmaloogium` | |
| `mod_name` | `Mod Name` | `Schmaloogium` | |
| `root_package` | `com.example` | `com.schmaloogium` | §2.3 |
| `mod_version` | `1.0.0` | `0.1.0` | SemVer; v0.1 is the first milestone, and shipping `1.0.0` before the pack matrix is met would be dishonest |
| `mod_description` | *(empty)* | one line: OptiFine/Iris-format shader-pack support for Cleanroom on 1.12.2 | mirrors RESEARCH.md §1.1 |
| `mod_url` | *(empty)* | the GitHub repo URL | |
| `mod_authors` | *(empty)* | populated | |
| `mod_credits` | *(empty)* | includes the license statement — see §4.8 | |
| `use_access_transformer` | `true` | **`false`** | §4.4.3 |
| `enable_lwjglx` | `true` | **`false`** | §4.6 |
| `is_coremod` | `false` | `false` (unchanged) | §4.5 |
| `enable_shadow` | `false` | `false` (unchanged) | §4.2.5 |
| `enable_junit_testing` | `true` | `true` (unchanged) | |
| `cleanroom_loader_version` | *(absent — inline literal)* | **`0.6.6-alpha`** | new property, §4.2.6 |
| `mixin_configs` | *(absent)* | **`schmaloogium.preinit.mixin.json,schmaloogium.default.mixin.json,schmaloogium.mod.mixin.json`** | new property, §4.5 |
| `enable_mixin_debug` | *(absent)* | **`true`** locally; **not read by CI** — the flags reach only Unimined's run tasks (§4.5.5) | new property, §4.5.5 — gates `-Dmixin.debug.export` / `-Dmixin.checks.interfaces` on the client run; §12 item 32 |

`publish_to_local_maven` is documented in the template's `gradle.properties` but **read by no
script** `[V:template]` — recorded in §11.3 as a template defect; either wire it or delete it.

#### 4.4.2 Source-tree conversion

- `src/main/java/com/example/modid/ExampleMod.java` → `mod/src/main/java/com/schmaloogium/mod/core/SchmaloogiumMod.java`.
  The template's version calls `Minecraft.getMinecraft().getLanguageManager()` in a `@Mod` class
  shared with the server `[V:template]`; the replacement does not, because Schmaloogium is
  client-only (§1.2 non-goals: "Server-side anything").
- `src/main/java/com/example/modid/proxy/{IProxy,CommonProxy,ClientProxy}.java` → retained in
  `com.schmaloogium.mod.core.proxy` with the same three-type shape. `@SidedProxy` is how a
  client-only 1.12.2 mod keeps its client code off the server's classloading path; deleting the
  proxy split would be a regression, not a simplification.
- `src/main/java-templates/com/example/modid/Reference.java` →
  `mod/src/main/java-templates/com/schmaloogium/Reference.java`, unchanged in content (the `{{ package }}`,
  `{{ mod_id }}`, `{{ mod_name }}`, `{{ mod_version }}` tokens all still apply; only the `package`
  property's *value* changes, per §2.3).
- `src/main/resource-templates/mcmod.info` → `mod/src/main/resource-templates/mcmod.info`, content
  unchanged (all nine tokens are still correct); the *values* come from `gradle.properties`.
- `src/main/resource-templates/pack.mcmeta` → `mod/src/main/resource-templates/pack.mcmeta`,
  unchanged.
- `src/main/resources/modid_at.cfg` → **deleted** (§4.4.3).
- New: `mod/src/main/resources/schmaloogium.{preinit,default,mod}.mixin.json` (§4.5).

#### 4.4.3 Access transformers: none for v0.1

`[D-P1-7]` **`use_access_transformer = false`; `modid_at.cfg` is deleted.**

The spec says "decide whether ATs are needed at all for v0.1 — prefer none until a hook requires
one." No component designed in this phase needs one, and Phase 1 cannot know what Phase 7's hook
catalog will need. Shipping the template's example AT (`public net.minecraft.client.Minecraft
fileResourcepacks # Example mcp name AT entry` `[V:template]`) would widen a vanilla field for no
reason and set the `FMLAT` manifest attribute pointlessly.

What is recorded so a later phase can turn ATs on in minutes rather than rediscovering the wiring:

- Three coupled pieces exist `[V:template]`: `ext.access_transformer_locations = "${mod_id}_at.cfg"`;
  the Unimined `cleanroom { accessTransformer … }` call; and
  `processResources { rename '(.+_at.cfg)', 'META-INF/$1' }`, with `FMLAT` naming the file in the
  manifest.
- **The path in the Unimined call must be changed** from `${rootProject.projectDir}/src/main/resources/…`
  to `${project.projectDir}/src/main/resources/…` when it moves into `:mod`. This is done as part of
  the split (§4.2.4) even though the branch is inert, so the trap is disarmed in advance rather than
  waiting to bite Phase 7.
- ATs are written in **MCP** names and remapped to SRG by Unimined at build `[V:template README, V:mcp]`.
- The standing rule from the MCP guide, adopted: **never remove `final` via a mixin — use the AT.**

#### 4.4.4 `mcmod.info` and `pack.mcmeta`

`mcmod.info` is 1.12.2 mod metadata (not `mods.toml`) `[V:mcp]`, read by Forge/Cleanroom for the
in-game mod list. The MCP `explain_concept("mcmod.info")` recipe describes the schema and **names no
`license` key**; the 1.12.2 schema's fields are `modid`, `name`, `version`, `mcversion`,
`description`, `authorList`, `credits`, `url`, `updateJSON`, `logoFile` — which is exactly the
template's token set `[V:template]`.

`[D-P1-8]` **License is stated in `mod_credits` and in `LICENSE`/`README.md`, not in a `mcmod.info`
`license` key**, because no such key is part of the schema and inventing one would be metadata that
nothing reads. Recorded as a limitation rather than papered over: a user browsing the in-game mod
list sees the license only if they read the credits line.

`pack.mcmeta` (`pack_format: 3`) is unchanged — it makes the mod jar a valid resource pack, which
Phase 13's texture work will need.

### 4.5 Mixin wiring

**No mixin classes are authored by this phase.** The hook catalog is Phase 7's (App E). What follows
is the wiring those hooks will land in.

#### 4.5.1 Declaration: the `MixinConfigs` manifest attribute

`[D-P1-9]` Configs are declared with the **`MixinConfigs` jar-manifest attribute**, comma-separated,
written by `:mod`'s `jar` `doFirst` manifest block from the `mixin_configs` property (§4.4.1).

This is current canon and the legacy path is deprecated. RESEARCH.md §5.1: "Configs are declared via
the **`MixinConfigs` jar-manifest attribute**; the legacy MixinBooter loader interfaces are
deprecated." The MCP `mixin-setup` guide is explicit that `IEarlyMixinLoader`, `ILateMixinLoader`,
`IMixinConfigHijacker` and `@MixinLoader` are all `@Deprecated` — "do not use them for new mods."
`[V:mcp]` `[V:web]`

`[D-P1-10]` **`is_coremod` stays `false`.** Cleanroom ships CleanMix built in; the manifest path
needs no coremod. The MCP guide notes the template's mixin branch ships `is_coremod=true` with an
*empty* `IFMLLoadingPlugin` and that whether this is required or vestigial is "unconfirmed upstream",
recommending the manifest path — and RESEARCH.md §5.1 records that coremods "still exist but are
discouraged." Adding a coremod would also brush against D-5 ("no class replacement") by opening a
class-transformation path we have no need for. If a future phase discovers it genuinely needs
transformation before mod construction, that is a flagged decision with its own justification, not a
default.

#### 4.5.2 Config-file layout: three, one per CleanMix phase

`[D-P1-11]` Three config JSONs, one per CleanMix phase. The spec asks "one per phase needed?" —
yes, because the phases are the only axis along which CleanMix actually dispatches configs, and
splitting later means editing the manifest, the file set, and every `@Mixin` package declaration at
once. The template README states it directly: "You will need one json per phase (`PRE_INIT`,
`DEFAULT`, `MOD`)" `[V:template]`.

| File | `target` | Mixin package | Purpose | Milestone |
|---|---|---|---|---|
| `schmaloogium.preinit.mixin.json` | `@env(PRE_INIT)` | `com.schmaloogium.mod.mixin.preinit` | Reserved. Anything needing to apply before mod construction — the vertex-format work (Phase 10) is the likely first tenant, expected `v0.3`. **The file ships empty at v0.1.** | `v0.1` |
| `schmaloogium.default.mixin.json` | `@env(DEFAULT)` | `com.schmaloogium.mod.mixin` | The bulk: render-loop hooks (Phase 7), shadow-pass hooks (Phase 8), texture hooks (Phase 13). | `v0.1` |
| `schmaloogium.mod.mixin.json` | `@env(MOD)` | `com.schmaloogium.mod.mixin.compat` | Mixins gated on other mods being present — `Loader.isModLoaded(...)` is only answerable in this phase; first tenant expected `v0.3`. Will carry the config plugin: the **`plugin` key is written at `v0.3`, in the same change as the class** — never at v0.1, because a `plugin` naming a class that does not exist fails config load at runtime. | `v0.1` |

**All three files are `v0.1`**, and the tag is on the *file*, not on its first tenant — §G4.3 allows
each component exactly one tag. The `mixin_configs` manifest attribute names all three from v0.1
(§4.4.1, §12 item 31), and a manifest naming a config file that does not exist fails config load at
runtime — the same failure mode this table warns about for the `plugin` key. §9 carries the same
three rows with "first tenant expected v0.3" in its note column.

Common fields, following the template snapshot `[V:mcp get_project_template("mixins.json")]`:

```json
{
  "required": true,
  "package": "com.schmaloogium.mod.mixin",
  "compatibilityLevel": "JAVA_8",
  "target": "@env(DEFAULT)",
  "minVersion": "0.8.7",
  "setSourceFile": true,
  "client": [],
  "mixins": [],
  "server": []
}
```

Two notes a reviewer should not have to rediscover:

- **`"server": []` is permanent, not merely empty.** Schmaloogium is client-only (RESEARCH.md §1.2's
  non-goals, "Server-side anything"). Recording this as intent stops a later phase from "filling in
  the gap."
- **`compatibilityLevel: "JAVA_8"` is kept from the template snapshot even though our source level is
  Java 25.** The field constrains the bytecode level Mixin will accept in *mixin* classes, not the
  project's source level. Kept because it is the verified template value and because raising it is a
  change with no known benefit — but flagged in §11.3 as a value we inherited rather than derived,
  worth a spot check the first time a mixin uses a Java-9+ language feature that survives to bytecode.

An `IMixinConfigPlugin` slot, `com.schmaloogium.mod.mixin.SchmaloogiumMixinPlugin`, is reserved on
the **MOD-phase** config. Its designed role is to consult the bail registry (§4.10) in
`shouldApplyMixin` so that a detected incompatible chunk-renderer replacement can veto vertex-pipeline
mixins *before they apply*, rather than applying them and then disabling the engine — a materially
better failure mode. It is a reserved slot in v0.1; Phase 10 gives it content.

**"Reserved" means reserved in this document, not declared in the JSON.** The v0.1 config files carry
the common fields above and no `plugin` key — the key is added at `v0.3` together with the class
(§9, §12 items 30 and 37). Declaring a plugin class that does not yet exist would fail config load,
which is the one way this slot could hurt before it helps.

**Two package-placement observations, neither of them a change** — recorded so §12 item 30's
"`runClient` loads all three configs without error" hook is run with them in mind, and so item 33's
throwaway-mixin check is the moment they are confirmed rather than assumed:

1. The DEFAULT config's mixin package `com.schmaloogium.mod.mixin` is the **parent** of the other
   two configs' packages. Mixin registers package-based scanning and classloader exclusions per
   config, and CleanMix's behaviour with a nested pair is undocumented in every source consulted —
   the Cleanroom wiki's Configuration page defines `package` as "the root package of where the
   mixins resides" alongside a separate `parent` key for config *inheritance*, and states no
   exclusivity or non-nesting constraint. Sibling packages would be the cheap insurance, and were
   rejected here only because the convention is contract-visible (§2.1's package table, §2.4, and
   §5.3 all carry it) and no evidence of breakage exists. If the item-30 hook fails, this is the
   first thing to try.
2. The reserved plugin class `com.schmaloogium.mod.mixin.SchmaloogiumMixinPlugin` sits *inside* the
   DEFAULT config's declared mixin package while being named by the **MOD** config. An
   `IMixinConfigPlugin` is not itself a mixin and is not scanned as one, so this is expected to be
   harmless — but it is the one placement in this table that spans two configs, and Phase 10 should
   confirm it when the class lands rather than inherit it as settled.

#### 4.5.3 SRG-name targeting policy

`[D-P1-12]` **Every `@Mixin` target class, every `@Shadow`/`@Inject`/`@Redirect` member reference,
and every descriptor is written in SRG names.** MCP-readable names appear in `//` comments beside
them, never in the annotation.

RESEARCH.md App E's header states every `@Mixin` target "must use SRG name + descriptor", and §5.1
records that "Mixins are written against **SRG names** and applied through Cleanroom's remapper chain
in dev and production" — `Srg2McpRemapper` in dev, an `FMLDeobfuscatingRemapper` wrapper in
production `[V:mcp]`. The resolution tool is the MCP recipe `resolve_symbol(...)`, which App E was
built with; App E's table is the first place to look before resolving anything fresh.

Convention, so the catalog stays readable:

```java
@Inject(method = "func_78471_a",              // renderWorld(FJ)V
        at = @At("HEAD"))
private void schmaloogium$onRenderWorldHead(float partialTicks, long finishTimeNano, CallbackInfo ci) { … }
```

Injected method names are prefixed `schmaloogium$` to avoid collisions with other mods' mixins into
the same class — standard practice, and cheap insurance in an ecosystem where coremod-heavy stacks
are the norm (RESEARCH.md §2.3).

#### 4.5.4 Refmap handling under Unimined

`[D-P1-13]` **Refmap generation is left to Unimined. `disableRefmap()` is not called.**

The template README states it plainly: "Don't worry about refmap, Unimined will handle it
automatically. You can still `disableRefmap()` manually though" `[V:template]`. The MCP guide agrees:
"Refmaps are handled by Unimined at build" `[V:mcp]`. `sponge-mixin` stays `compileOnly` because the
loader provides the runtime — RESEARCH.md §5.1 records exactly this.

The one thing to watch, recorded for the Phase 7 implementation session: the template checkout is the
`main` branch and therefore has **never had a mixin config present**, so Unimined's refmap machinery
in this project is unexercised. The first config to land should be verified to produce a refmap in
the built jar before any hook work proceeds — that check belongs in §12's checklist.

#### 4.5.5 Dev ergonomics

Added to `:mod`'s `unimined.minecraft { cleanroom { runs.all { … } } }` block, gated behind a new
`enable_mixin_debug` property (default `true` for local dev; set `false` locally when the
`.mixin.out/` dump is noise):

- `-Dmixin.debug.export=true` — writes post-transform classes to `.mixin.out/`. The template's
  mixin-branch run config sets this `[V:mcp]`.
- `-Dmixin.checks.interfaces=true` — fails fast on interface-implementation mismatches. Same source.
- `-Dcrl.dev.mixin=<config>` — **documented, not set.** This is Cleanroom's hook for injecting
  extra dev-only mixin configs at runtime `[V:mcp]` `[RESEARCH.md §5.1]`. Its designed use here is
  the Phase 7 hook-spike workflow: try an injection in a throwaway config without touching the
  shipped manifest. Recorded in the developer README rather than wired into the build.

**These flags configure Unimined's `runs` tasks and nothing else**, so the property has no effect on
`./gradlew build` or on the module `test` tasks — that is, on anything CI invokes (§4.11). An earlier
revision claimed CI sets it `false` to keep build logs readable; that claim was unwired, because CI
never reaches a run task. Any future need to quieten CI logs is a change to the CI invocation, not to
this property.

Cleanroom annotates crash reports with which mixins touched each class `[V:mcp]` — worth knowing
before Phase 7 builds any bespoke diagnostics.

### 4.6 lwjglx posture (OQ-21)

`[D-P1-14]` **`enable_lwjglx = false`.** The `compileOnly "com.cleanroommc:lwjglx:1.0.0"` line in
`dependencies.gradle` becomes inert.

**What `enable_lwjglx=true` actually means in the template.** Exactly one thing `[V:template]`: it
adds `com.cleanroommc:lwjglx:1.0.0` to the **`compileOnly`** configuration. There is no runtime
injection, no run-configuration flag, no `-Dlwjglx` anywhere, and no effect on the produced jar. Its
sole function is to let source code `import org.lwjglx.*` and compile. The template's own comment
says so: "Set this to true if you want to use old LWJGL2 methods… If you are porting an old mod
lazily just set this to true."

**Why we drop it.** RESEARCH.md §6.1 lists as a hard constraint: "`org.lwjglx` is runtime-only —
Compile against LWJGL3 proper `[V:mcp]`". DESIGN.md §G2.2 restates it as binding: "**LWJGL3-native
code only**: never compile against `org.lwjglx` (runtime-only shim, itself in flux — OQ-21)." With
`enable_lwjglx=true`, an accidental `org.lwjglx` import compiles silently and the violation is
discovered at runtime, on someone else's machine, on a configuration where the shim is absent. With
it `false`, the same import is a compile error in the developer's IDE. The build should reject the
mistake, not tolerate it.

**Runtime posture.** lwjglx is a loader-side compatibility layer for *other* mods. Whether it is
present in a given installation is not our concern, because we never reference it. Two consequences
worth recording:

1. `CapabilityProbe` (§4.7.5, below) queries LWJGL3 entry points directly. If a future
   installation routes some GL calls through a shim, the probe still reads the real context's values,
   because it asks the driver, not the shim.
2. RESEARCH.md §5.3 flags "what lwjglx intercepts at runtime" as unverified, feeding OQ-3 (Phase 7's
   GL-context spike). If that spike discovers the shim materially alters context creation, the
   finding lands in Phase 7's doc; it does not change our compile-time posture, which is
   unconditional.

**The flux, tracked.** RESEARCH.md §5.1 records that the Cleanroom README no longer mentions LWJGL2
compat, and that two successors exist: **LWJGLXX** ("using lwjglx without redirecting everything",
early) and **LWJGLY** ("LWJGL 2⇒3 Shim & Router", an *empty placeholder repo*). Neither is a
dependency of ours and neither can become one under this decision — which is the point: the flux is
somebody else's, and our posture is stable regardless of how it resolves. The OQ-21 spike (§10.4)
exists to confirm that the runtime story holds in practice, not to reconsider the compile-time rule.

### 4.7 The `engine.gl` facade

#### 4.7.1 Granularity: grouped services, opaque handles

`[D-P1-15]` The facade is **a small set of role-oriented service interfaces behind a `GLDevice`
root, addressing GL objects through opaque handle types** — not a thin 1:1 mirror of GL verbs.

The spec presents the choice as "thin GL-verb layer vs. grouped services". The deciding argument is
OQ-20. A thin GL-verb facade (`int genFramebuffer()`, `void bindFramebuffer(int, int)`,
`void uniform1i(int, int)`) is trivial to implement and trivial to record — but it *is* OpenGL, with
the package name changed. It encodes imperative call-at-a-time semantics, integer object names, and
global bind-point state into `:engine`'s source. Kirino-Engine's model is the opposite: deferred
render commands, abstracted GL objects, immutable RenderPass/Subpass composition (RESEARCH.md §5.2).
Porting `:engine` from the first to the second would not be "implement a new backend" — it would be
rewriting the engine, which is precisely the outcome D-6 exists to prevent (RESEARCH.md §7.2: "the
core must survive a backend swap").

Grouped services with opaque handles cost a little indirection in `mod.glue` and buy a facade that a
pass-based backend can implement without `:engine` noticing. Opaque handles matter more than they
look: an `int` in engine code is an invitation to arithmetic, to comparison against `0`, to
`glBindTexture(GL_TEXTURE_2D, id)`-shaped thinking. A `TextureHandle` is not.

The counter-cost is honest and recorded: a grouped facade is a *design* surface, so getting its
granularity wrong is more expensive than getting a verb list wrong. That is why §10.3 specifies a
backend-swap drill rather than declaring victory.

#### 4.7.2 `GLCapabilityProfile`

An immutable value object. It is the single most-consumed type this phase produces: Phase 2 replays
recorded profiles, Phases 4/5/6 gate on it, and Phase 3 derives `MC_<GL_extension>` macros from it.

```java
package com.schmaloogium.engine.gl;

public record GLCapabilityProfile(
        int glVersionMajor,
        int glVersionMinor,
        String glslVersion,          // as reported by GL_SHADING_LANGUAGE_VERSION
        String vendor,               // GL_VENDOR
        String renderer,             // GL_RENDERER
        // the three count probes below are RESEARCH.md §4.1 step 1
        int maxDrawBuffers,          // GL_MAX_DRAW_BUFFERS
        int maxColorAttachments,     // GL_MAX_COLOR_ATTACHMENTS
        int maxTextureImageUnits,    // GL_MAX_TEXTURE_IMAGE_UNITS
        int maxVertexAttribs,        // GL_MAX_VERTEX_ATTRIBS
        int maxTextureSize,          // GL_MAX_TEXTURE_SIZE
        Set<String> extensions) {

    public boolean atLeast(int major, int minor) { … }
    public boolean hasExtension(String name)     { … }

    /** RESEARCH.md §4.1: "mipmap gen requires GL 3.0". */
    public boolean supportsMipmapGeneration()    { return atLeast(3, 0); }
}
```

`maxVertexAttribs` and `maxTextureSize` are additions beyond the RESEARCH.md §4.1 probe set, on the
same reasoning as the extension set: Phase 10's extended vertex format needs the first (it binds
attributes at locations 10/11/12 and will grow), Phase 5's buffer sizing needs the second. Both carry
their own `[A]` row in the §3 map. `extensions` is defensively copied and exposed unmodifiable — a
record's component accessor otherwise hands out a mutable set.

`glslVersion`, `vendor` and `renderer` are not decoration either: RESEARCH.md §3.5 requires
`MC_GLSL_VERSION`, `MC_GL_VENDOR_*` and `MC_GL_RENDERER_*` in the standard macro header injected into
every pack shader, and `MC_GL_VERSION` comes from the version pair. The profile is therefore the
whole GL-side input to Phase 3's header, which is what the §3 macro-header row records; Phase 3 owns
the formatting and the non-GL macros.

**Serialization.** `[D-P1-16]` The profile has a stable, human-readable, diff-friendly text form —
a sorted `key = value` properties document with `extensions` as a sorted newline-delimited block.
This is what makes the whole headless testing strategy work:

```
gl.version           = 4.6
glsl.version         = 4.60 NVIDIA
vendor               = NVIDIA Corporation
renderer             = NVIDIA GeForce RTX 3070/PCIe/SSE2
max.drawBuffers      = 8
max.colorAttachments = 8
max.textureImageUnits= 32
max.vertexAttribs    = 16
max.textureSize      = 32768
extensions =
  GL_ARB_debug_output
  GL_ARB_sampler_objects
  …
```

`GLCapabilityProfile.parse(Reader)` / `.write(Writer)` round-trip it. Profiles captured from real
GPUs become checked-in fixtures under `engine/src/testFixtures/resources/profiles/` — inside
`:engine`, because every test that consumes a profile lives there or downstream of there (§8.3
explains the placement and how `:conformance` and `:mod` reach them). Sorted and line-oriented so that
a fixture's diff is readable when a driver update changes one extension.

This format **is** what §G6 means by "recorded `GLCapabilityProfile`s", what Phase 2's
"capability-profile replay" replays, and what Phase 4/5/6's "recorded-GL run" impl gates run against.
Phase 1 owns the format; Phase 2 owns the fixture set and the update workflow.

#### 4.7.3 Handles

```java
package com.schmaloogium.engine.gl;

/** Marker for every GL object the engine holds. Never an int. */
public sealed interface GLHandle permits
        ProgramHandle, ShaderHandle, TextureHandle, FramebufferHandle {}

public sealed interface ProgramHandle     extends GLHandle permits …  {}
public sealed interface ShaderHandle      extends GLHandle permits …  {}
public sealed interface TextureHandle     extends GLHandle permits …  {}
public sealed interface FramebufferHandle extends GLHandle permits …  {}

/** Deliberately NOT a GLHandle — a location is a lookup result, not an object. */
public sealed interface UniformLocation permits … {
    /** True when the uniform was optimized out; uploads through it are no-ops. */
    boolean isAbsent();
}
```

Each backend supplies the permitted implementations: `Lwjgl3GLDevice` wraps ints;
`RecordingGLDevice` wraps synthetic sequence numbers; a hypothetical Kirino backend wraps whatever
it uses. `:engine` sees only the interfaces.

**Four handle types, not five.** There is no `RenderbufferHandle` and no renderbuffer verb anywhere
in the facade, deliberately: RESEARCH.md §4.3's reference architecture makes **every** attachment a sampleable
texture (the pack contract requires `depthtex0/1/2`, `shadowtex0/1` and `colortex0-7` all to be
readable from shaders), so a renderbuffer would have no contract consumer, and RESEARCH.md §4.1's
uninit is fully served by the four `delete` verbs. §2.4's key-type table and §12 item 18 both say
four.

**Handle lifetime — a handle is invalid the moment its `delete` returns.** This has to be stated
because RESEARCH.md §4.1 step 5 makes full teardown-and-rebuild a **routine v0.1 event** (a pack
change, an option change or a dimension change fires it), not a shutdown path, so handles held by
long-lived engine state outlive their objects as a matter of course. `[D-P1-28]` The rules:

- Using a handle after its `delete` is a programming error in the caller. **Phase 5 owns
  re-acquisition** — it owns the buffer estate's lifecycle, so it is the phase that must drop and
  re-create its handles across an uninit/rebuild rather than carry them over.
- **The two backends cannot fail the same way, and that asymmetry is deliberate.** A driver may
  reissue a GL name after a delete, so a stale handle in `Lwjgl3GLDevice` silently addresses a
  *different live object* rather than failing — the worst kind of bug to hunt. `RecordingGLDevice`
  hands out a monotonic sequence number and never reuses one, so misuse is *detectable* under
  replay. The facade therefore leans on the recording backend to catch what the LWJGL backend
  structurally cannot: `ReplayAssertions.noUseAfterDelete()` (§4.7.5) is that check.
- `noLeakedObjects()` is not the same assertion and does not cover this: it checks that every create
  has a matching delete, which is the direction a *leak* runs. A reload produces the opposite —
  deletes whose handles are still held.

`UniformLocation.isAbsent()` is load-bearing and belongs here rather than in Phase 6: GLSL compilers
routinely optimize out unused uniforms, `glGetUniformLocation` returns `-1`, and the reference
implementation's per-program location caching (RESEARCH.md §4.2) depends on distinguishing "not
looked up yet" from "looked up, not present". Exposing that as a boolean instead of a sentinel
integer is exactly the kind of leak the opaque-handle decision is meant to prevent.

#### 4.7.4 The device and its services

```java
public interface GLDevice {
    GLCapabilityProfile capabilities();

    ShaderService      shaders();
    UniformService     uniforms();
    TextureService     textures();
    FramebufferService framebuffers();
    StateService       state();
    DrawService        draw();
    DebugService       debug();

    /** Errors observed since the last drain; empty when clean. Draining clears.
     *  NOT a query of GL's state on demand, in either direction: the drain elides entirely
     *  when no mutating FACADE call has occurred since the previous one ([D-P1-30]), so an
     *  empty return means "nothing of ours mutated, or nothing errored" — it does not mean
     *  the per-context error flag is clear. And a non-empty return may carry an error no
     *  facade call caused, for the same reason (§4.7.4, §11.4).
     *  ORDER: under the per-call debug cadence the list is in call order. Under the default
     *  cadence it is the order the driver's error flags come back in, which GL does not
     *  define — a drain that returns several elements is reporting several FLAGS, not a
     *  sequence ([D-P1-30] cites the rule).
     *  This is the signal §G2.4's rung 2 acts on, at the attribution granularity [D-P1-32]
     *  states — see the GL-error surface below. */
    List<GLError> drainErrors();
}
```

Seven services, each a role rather than a GL module. Load-bearing signatures:

```java
public interface ShaderService {
    ShaderHandle  createShader(ShaderStage stage, String source);
    CompileResult compile(ShaderHandle shader);          // never throws
    ProgramHandle createProgram();
    void          attach(ProgramHandle p, ShaderHandle s);
    void          bindAttributeLocation(ProgramHandle p, int location, String name); // pre-link
    LinkResult    link(ProgramHandle p);                 // never throws
    ValidateResult validate(ProgramHandle p);            // never throws
    void          use(ProgramHandle p);   // the universal state barrier (RESEARCH.md §4.2)
    void          delete(ProgramHandle p);
    void          delete(ShaderHandle s);
}

public interface UniformService {
    UniformLocation locate(ProgramHandle p, String name);
    void upload(UniformLocation loc, int v);
    void upload(UniformLocation loc, int x, int y);        // ivec2 — App D.3: atlasSize,
                                                           //         App D.1: eyeBrightness
    void upload(UniformLocation loc, int x, int y, int z, int w);   // ivec4 — App D.4: blendFunc
    void upload(UniformLocation loc, float v);
    void upload(UniformLocation loc, float x, float y);
    void upload(UniformLocation loc, float x, float y, float z);
    void upload(UniformLocation loc, float x, float y, float z, float w);
    void uploadMatrix4(UniformLocation loc, float[] m16, boolean transpose);
    // NO uniform-block / UBO entry point — the pack contract forbids it
    // (RESEARCH.md §6.1, D-9).
}

public interface FramebufferService {
    FramebufferHandle create(String debugLabel);
    void attachColor(FramebufferHandle f, int attachmentIndex, TextureHandle t);
    void attachDepth(FramebufferHandle f, TextureHandle t);

    /** Which attachments this framebuffer writes. A ZERO-LENGTH array means the contract's
     *  "draw-buffers = none" state (RESEARCH.md §4.3's first-person item overlay), NOT
     *  "leave the current selection alone" — there is no verb for the latter. */
    void drawBuffers(FramebufferHandle f, int[] attachmentIndices);

    FramebufferStatus check(FramebufferHandle f);
    void bind(FramebufferTarget target, FramebufferHandle f);
    void bindDefault(FramebufferTarget target);

    /** Framebuffer-to-framebuffer copy. BlitSpec carries the source and destination rectangles,
     *  the attachment mask (colour and/or depth) and the filter; a depth blit must specify
     *  NEAREST, which BlitSpec enforces at construction rather than leaving to the backend.
     *  Restores the caller's prior draw and read bindings before returning (see below).
     *  CONSUMER: Phase 5, the one owner of framebuffer-to-framebuffer movement. No CONTRACT
     *  item demands it today — the depth copies are copyDepthToTexture (see below) and the
     *  composite ping-pong is a draw, not a copy — so it is here because a buffer estate
     *  without a framebuffer-to-framebuffer verb is implausible, not because a directive
     *  names it. If Phase 5's design closes without using it, it moves to the absent-verbs
     *  table at the next fix-up rather than lingering as a permanent exception to this
     *  facade's own "no verb without a consumer" rule. */
    void blit(FramebufferHandle src, FramebufferHandle dst, BlitSpec spec);

    /** Copy a region of f's depth attachment into a standalone texture. This — not blit — is the
     *  verb the contract's depth copies need: depthtex1 and depthtex2 are copy-target *textures*,
     *  not attachments (RESEARCH.md §4.3), so there is no destination framebuffer to blit into and
     *  Phase 5 is not expected to invent one. Called mid-frame between two draws into the main FBO
     *  (RESEARCH.md §4.4), so it too restores the caller's prior bindings. */
    void copyDepthToTexture(FramebufferHandle src, TextureHandle dst, TextureRegion region);

    /** Synchronous single-pixel depth readback from f's depth attachment. Returns immediately
     *  usable data and therefore stalls the pipeline — faithful to reference behavior
     *  (RESEARCH.md §4.4, §6.2). WHICH pixel, at which moment, and any halflife smoothing of the
     *  result are Phase 6 policy; this is only the verb. */
    float readDepthPixel(FramebufferHandle f, int x, int y);

    void delete(FramebufferHandle f);
}

public interface TextureService {
    TextureHandle create(String debugLabel);
    void allocate(TextureHandle t, TextureSpec spec);     // spec is a value object; formats are Phase 5's
    void setParameters(TextureHandle t, TextureParameters p);

    /** Upload texels into an allocated texture: the whole image or a sub-region, one mip level.
     *  TextureData is a value object — TextureRegion, mip level, a PixelLayout drawn from the same
     *  engine-level format vocabulary TextureSpec uses, and a java.nio.ByteBuffer of texels
     *  (a JDK type; C-1 forbids an LWJGL buffer type in :engine). What gets uploaded, in what
     *  format, and to which unit is Phase 13/5 policy. */
    void upload(TextureHandle t, TextureData data);

    void bindToUnit(int unit, TextureHandle t);
    void generateMipmap(TextureHandle t);                 // caller checks supportsMipmapGeneration()
    void delete(TextureHandle t);
}

public interface StateService {
    void viewport(int x, int y, int w, int h);
    void clearColor(float r, float g, float b, float a);
    void clear(EnumSet<ClearTarget> targets);
    void depthMask(boolean enabled);          // depth WRITES
    void depthTest(boolean enabled);          // depth TEST — a different bit of state
    void blend(BlendState state);             // null/absent = disabled
    void alphaTest(AlphaTestState state);
    void fog(FogState state);                 // null/absent = disabled

    /** Snapshot the state we are about to perturb, for the §G4.6 restore discipline.
     *  StateAspect is an engine enum with one constant per verb above. */
    StateSnapshot snapshot(EnumSet<StateAspect> aspects);
    void restore(StateSnapshot snapshot);
}

public interface DrawService {
    /** The composite/final full-screen pass primitive. The backend picks GL_QUADS or the
     *  triangle-strip fallback (RESEARCH.md §6.1); the engine never expresses that choice.
     *  It delegates the PRIMITIVE only: it establishes no draw state, and the caller is
     *  responsible for the composite state block through StateService.
     *  `const int countInstances = N` (RESEARCH.md §3.2) is NOT served by an instanced verb —
     *  see the absent-verbs table below and [D-P1-33]: on a COMPOSITE/DEFERRED program it is a
     *  caller-side loop over this primitive with an `instanceId` upload between copies, which is
     *  the only form RESEARCH.md §4.4 observes. The same directive on a gbuffers/shadow program
     *  re-renders VANILLA geometry and never reaches this verb at all — see §3's second row,
     *  [D-P1-35] and §11.4 for who owns that case. */
    void fullscreenQuad();
}

public interface DebugService {
    void pushGroup(String label);
    void popGroup();
    void label(GLHandle handle, String label);
    boolean isActive();          // false unless a debug context and the dev flag are both on
}

/** One driver-level error, attributable to the DRAIN WINDOW that produced it — and therefore
 *  to one call when the window held exactly one MUTATING FACADE call ([D-P1-32]).
 *  That entailment is not unconditional: the GL error flag is per-context and this
 *  architecture guarantees GL traffic that never reaches the facade, so a window may hold
 *  an error no facade call caused. `op`/`subjectLabel` then name the wrong call, which is
 *  why [D-P1-32]'s replay — not this record — is what attribution rests on (see the
 *  GL-error surface below and §11.4).
 *  `op` is the facade verb ("uniforms.upload", "textures.allocate"); `subjectLabel` is the
 *  debug label of the handle or the uniform name involved when the window held one call, and
 *  "(batched, N calls)" when it held several. The uniform-name case is a BACKEND OBLIGATION,
 *  not a facade guarantee: UniformLocation carries no name in its signature, so a backend
 *  must retain the one passed to locate(program, name) or rung 2 has nothing to name
 *  ([D-P1-34]). §G2.4 rung 2's per-uniform disable is reached
 *  through the attributed replay [D-P1-32] describes, not by reading a batched record.
 *  `kind` is an engine enum
 *  (INVALID_ENUM, INVALID_VALUE, INVALID_OPERATION, OUT_OF_MEMORY,
 *  INVALID_FRAMEBUFFER_OPERATION, UNKNOWN) — never a GL constant. */
public record GLError(String op, String subjectLabel, GLErrorKind kind, String detail) {}
```

Design rules embedded above, each with a reason:

- **`compile` / `link` / `validate` return results; they never throw.** §G2.4's rung 3 requires that a
  program failing compile/link/validate deletes itself and reports a user-visible error. A checked
  exception crossing the facade would make that a control-flow problem instead of a data problem.
  `CompileResult`/`LinkResult` carry `success`, the driver log, and a `EngineDiagnostic` (§4.9).
- **`StateService` is deliberately narrow, and the inclusion criterion is stated rather than
  implied.** A verb exists here when the *reference pass structure* requires the engine itself to
  perturb that state to run a pack pass: viewport and clears (per-buffer clears, sub-viewport
  `scale.<prog>`), depth mask, **depth test**, blend, alpha test and **fog** — the last three plus
  depth test being exactly the composite/final block RESEARCH.md §4.4 describes ("under an identity
  ortho, fog/depth/blend disabled"), and alpha/blend also being per-program state from App F.7.
  The enumeration is **this document's**, derived from RESEARCH.md §4.3/§4.4 and App F.7; §G4.6
  supplies the *discipline*, not the list. Everything else is out: colour mask has its own row in
  the deferred table below, matrix state is absent by design (no pack-facing matrix verb exists —
  an "identity ortho" is not expressible through any engine call and is not meant to be, because
  the 1.12.2 path establishes it outside the facade), and vertex-format state is Phase 10's.
  It exposes no way to set state that `GlStateManager` caches **without going through it**, because
  §G4.6 forbids exactly that ("we never bypass it for state it caches") — a rule about how the
  backend implements these verbs, not about which verbs exist. The narrowness is the enforcement:
  you cannot misuse an entry point that does not exist. **Which** state is perturbed at which moment
  is Phase 5/6/7 policy.
- **The other half of §G4.6 is a backend obligation, and it is stated here because no test can
  catch it.** `[D-P1-29]` **Every `Lwjgl3GLDevice` verb whose GL state `GlStateManager` caches must
  be issued *through* `GlStateManager`, never through raw LWJGL.** Concretely that is
  `TextureService.bindToUnit` (both the unit selection and the bind — `GlStateManager` caches
  `textureState` and `activeTextureUnit`), every `StateService` verb except `viewport`, and any
  clear. `bindToUnit` is named first because it is the **highest-frequency** call in the set, not an
  edge case: the fixed unit map re-points up to 16 units on every program switch (RESEARCH.md §4.2),
  so a raw-LWJGL bind here would stale the vanilla cache thousands of times a frame — and
  `DESIGN.md` §G4.6 makes that a correctness failure ("the cache would go stale and break vanilla
  rendering"), not a style one. The rule is a constraint on the **implementation** of the verbs; the
  facade's own signatures are unchanged by it, which is why it lives here as prose and in `:mod`'s
  review checklist rather than in an interface.
- **The facade surfaces driver errors, because §G2.4's rung 2 needs a signal.** `[D-P1-30]`
  Every mutating verb returns `void`, so `GLDevice.drainErrors()` is where a driver-level failure
  becomes observable. It is a **batched drain** rather than a per-call status for two reasons: a
  returned status on `upload` would put an allocation (or a boxed status) on the one hot path §7
  identifies, and rung 2's consumer is naturally a sweep. (Rung 1 is *not* served here: `DESIGN.md`
  scopes it to a custom uniform whose **expression** errors, which is Phase 11's evaluator at v0.4
  and never reaches a GL call — §6.) **The backend's `glGetError` policy is part of the contract,
  not an implementation detail:** `Lwjgl3GLDevice` calls `glGetError` after *every* facade call when
  a debug context is active or when **`-Dschmaloogium.debug.recordGL` or
  `-Dschmaloogium.debug.glLabels`** is set — the two GL-facing flags, and only those two. The
  trigger is *not* "any `-Dschmaloogium.debug.*` flag": `saveSources` is Phase 3's source dump and
  `dumpCapabilities` is a one-shot init probe, and neither should silently change the facade's
  per-frame driver-query count for a developer who asked for something else (§4.9.3 records the
  coupling on the two flags that carry it). Otherwise the cadence is **once per `drainErrors()`**,
  and "once" means two specific things:

  1. **A drain is a loop, not a single query.** `drainErrors()` calls `glGetError` repeatedly until
     it returns `GL_NO_ERROR`. This is the GL-sanctioned drain, and the distinction is not
     pedantry — the specification does not promise one flag: *"To allow for distributed
     implementations, there may be several error flags. If any single error flag has recorded an
     error, the value of that flag is returned and that flag is reset to `GL_NO_ERROR` when
     `glGetError` is called. If more than one flag has recorded an error, `glGetError` returns and
     clears an arbitrary error flag value. Thus, `glGetError` **should always be called in a loop,
     until it returns `GL_NO_ERROR`**, if all error flags are to be reset."* `[V:web]` — the OpenGL
     `glGetError` reference page — **`https://docs.gl/gl4/glGetError`**, wording identical at
     **`https://docs.gl/gl2/glGetError`** for the GL 2.1-era refpage, read 2026-07-25 (the docs.gl
     mirror, because `registry.khronos.org/OpenGL-Refpages/gl2.1/xhtml/glGetError.xml` returns
     HTTP 403). RESEARCH.md §0.2 defines `[V:web]` as verified against a live web source with the
     **URL in §12.5 or inline**, and §12.5 is RESEARCH.md's own index which this document may not
     amend (§G1.1) — so the URL is inline, and the read date overrides §0.2's default 2026-07-24
     stamp under its own "unless noted". A single call per drain leaks any second flag into the *next* window, where it is
     attributed to the wrong one — which on the first window of `[D-P1-32]`'s replay means a
     spurious record and an innocent uniform disabled, the exact failure that decision exists to
     prevent. **The loop is free on the clean path:** it terminates on the first `GL_NO_ERROR`, so an
     empty drain is still exactly one query; it pays a second only when there was something to
     report.
  2. **A drain with nothing to observe issues no query at all.** The backend tracks one bit — set by
     every mutating facade call, cleared by every drain — and when it is clear `drainErrors()`
     returns empty without touching the driver. This is what makes the stated rung-2 protocol
     (*drain, upload the set, drain*) cost **one** query per sweep rather than two: program sets
     swept back-to-back leave the bit clear at the leading drain, so only the trailing drain queries.
     It is also self-correcting where caller-side amortization would not be — if a mutating **facade**
     call *does* intervene between two sets, the bit is set and the leading drain queries. Neither
     property asks any discipline of Phase 6.

     **What the bit does not bound, stated once and here because §5.2 is read as contract.** The bit
     tracks *facade* mutations; the GL error flag it is used to reason about is **per-context**, and
     this architecture guarantees a large volume of GL traffic that never reaches the facade. On a
     gbuffers program the geometry is vanilla terrain or entity geometry drawn by Minecraft's own draw
     calls through Phase 7's hooks (§3's second row); `DESIGN.md` §G4.6 makes cooperation with
     `GlStateManager` one-directional, so vanilla drives it independently of us; and this section's own
     absent-verbs table routes face culling through `GlStateManager` and contemplates Phase 7 driving
     the anaglyph final through vanilla's path. A vanilla — or third-party-mod — GL call that errors
     between two of Phase 6's program-set sweeps therefore leaves the bit **clear**: the leading drain
     elides its query and the trailing drain returns an error our facade did not cause. **A non-empty
     trailing drain consequently does not imply that one of Phase 6's uploads failed.** This is why
     `[D-P1-32]`'s second precondition is load-bearing *in general* rather than an `OUT_OF_MEMORY`
     corner — the replay re-uploads with a drain between each upload, reproduces nothing, and the sweep
     falls to §6's 3→4 "unattributable" row rather than disabling an innocent uniform. The containment
     is real, and it is the **replay** that supplies it, not the bit. **Two remedies exist, and neither
     is described here as the only one.** Dropping the elision *would* bound the window against all GL:
     it is effective, and it is rejected on **cost**, not on soundness — it would pay a factor of two on
     a synchronous driver query at every program switch in the frame, the cost §7 exists to bound, to
     relabel a case the replay already contains. A guard inside the facade is a different matter and is
     unavailable for a **mechanism** reason rather than a cost one: the facade **cannot observe
     non-facade GL**, so there is no bit it could set. The second remedy is the one that lies outside
     this facade's reach — an unconditional drain at a frame-driver-defined point — and that is **Phase
     7's** to place (§11.4): this document supplies the verb and does not design the placement. **Two
     limits on it belong with it**, because a remedy stated without them reads as a general fix. A drain
     placed once per frame bounds the gap that spans the **frame boundary** and leaves every gap between
     two *interior* sweeps exactly as it was — foreign GL is interleaved with those sweeps throughout
     the frame (`DESIGN.md` §G3.2's gbuffers dispatch, RESEARCH.md §4.4's gbuffers chain), not
     concentrated at a boundary. And "unconditional" is the caller's word, not the backend's: under the
     elision above, such a drain issues no query unless a mutating facade call has intervened, so the
     placement must either follow one or ask for a verb that forces the query — an additive request in
     Phase 7's own §5, by the route this section already names.

  `[D-P1-32]` **What the cheap mode can and cannot tell you, stated exactly, because the §0.5
  revision got it wrong.** GL sets an error flag to the *first* error that occurs and **records no
  further error in that flag until `glGetError` clears it**. A drain window is therefore not "the
  same information with blurrier labels": a sweep in which five uniforms fail yields **one record**
  — or, on an implementation maintaining several flags, at most one per flag — in every case fewer
  than five, and none of them naming a call. The honest invariant is *"a drain cannot lose the fact
  that the window failed"* — **not** "cannot lose an error", which is what this bullet used to claim
  and which is false as a matter of GL semantics. Attribution is consequently a property of **the
  caller's drain window**: a window holding exactly one mutating **facade** call yields a record naming
  that call; a window holding several carries `subjectLabel = "(batched, N calls)"`. Both statements
  are about what the facade can attribute, not about what the window can contain — the flag is
  per-context and a window may hold a foreign error as well (above).

  **This is what makes rung 2 implementable in the shipping configuration**, with no debug flag and
  no additional verb. Phase 6 drains, uploads the program's uniform set, and drains again. Empty —
  the ordinary case, every frame — and the sweep cost **one** `glGetError`: the leading drain elides
  its query because nothing mutating **through the facade** has happened since the previous drain
  (which is the elision's reach and not GL's — above), and the trailing drain's
  loop terminates on its first `GL_NO_ERROR` (both properties are `[D-P1-30]`'s, above). Non-empty,
  and Phase 6 **re-uploads the set draining between uploads**: each window then holds one call, each
  record names one uniform, and Phase 6 disables those uniforms only, which is precisely "a built-in
  uniform whose GL upload errors disables that uniform only" (`DESIGN.md` §G2.4 rung 2, assigned to
  Phase 6 at v0.1). The replay is paid on the frame that is about to disable something, once; the
  clean path §7 identifies as this phase's one hot path is untouched. **One exception, stated because
  the elision above creates it:** a *foreign* error — one this facade did not cause — makes a trailing
  drain non-empty without anything of ours having failed, so the replay runs, reproduces nothing, and
  disables nothing. A one-off costs one replay, exactly as the sentence above says. A **recurring**
  foreign error costs one replay per program set per frame, and a program switch refreshes ~90 built-in
  uniforms (RESEARCH.md §4.2), so the ceiling is on the order of ninety extra synchronous queries and
  ninety redundant uploads **per program set** — and therefore that figure multiplied by the number of
  program sets the frame sweeps, for as long as it recurs. **The multiplier is the point and is stated
  rather than folded away:** the same paragraph above prices the alternative *"at every program switch
  in the frame"*, and §7 states the identical quantity from the other side, so a future session
  re-weighing the elision has a per-frame comparison on both sides rather than a per-frame number
  against a per-switch one. That cost is created by the elision and is not in the ledger the paragraph
  above weighs the elision against; this document records it rather than re-opening that decision,
  which would be a design call needing its own argument.

  Making rung 2 debug-mode-only
  was the alternative and was rejected: `DESIGN.md` puts per-uniform GL-error isolation in Phase 6's
  **v0.1** scope-in, and a shipping build whose only fallback is "disable the whole set" would
  degrade harder than rung 3.

  **Two preconditions the replay carries, stated because §5 is written to be sufficient on its own
  and a Phase 6 session implementing the protocol literally must get both right.**

  - **The replay re-uploads the values already computed for this sweep; it never re-evaluates the
    providers.** The reason is idempotence, and it is sufficient on its own: `glUniform*` is
    idempotent on the bound program, so re-uploading **cached** values changes nothing except *which
    drain window* each upload lands in — which is the replay's entire purpose. Re-running the sweep
    would instead re-enter Phase 6's world-state providers, and this document deliberately asserts
    **no** property of what a second evaluation would do. RESEARCH.md §4.4 places the world-state
    sampling at **frame begin**, and App D's cadence model "refreshes" uniforms on program switch in
    the sense of an *upload* — its redundant-upload skip presupposes the value is already computed
    (RESEARCH.md §4.2) — while `DESIGN.md` puts the smoothing math itself (*"halflife → per-tick
    exponential decay formula, **time-corrected**"*) in **Phase 6's** own *Scope — in*. A claim about
    how `wetness`, `eyeBrightnessSmooth` or `centerDepthSmooth` behave under double evaluation would
    be a statement about another phase's not-yet-designed providers, which §G1.1 makes a thing to flag
    rather than to decide here. This is the whole reason the protocol is "re-upload", not "re-run the
    sweep".
  - **The replay assumes the error reproduces, and says what happens when it does not. This branch is
    load-bearing *in general*, not as an `OUT_OF_MEMORY` corner** — five sites delegate the foreign-GL
    containment to it (§4.7.4 above, §5.2's GL-error row, `[D-P1-30]`, `[D-P1-32]`, §11.4), so
    narrowing it back to one kind would silently delete what they rely on. **Two causes, not one.**
    (a) `GLErrorKind.OUT_OF_MEMORY` is the kind that need not recur. (b) **The error may never have
    been ours**: the elision bit tracks *facade* calls while the GL error flag is per-context, so a
    window can hold an error this facade did not cause, and a replay of our own uploads will of course
    reproduce nothing. Either way Phase 6 has a detected failure it cannot attribute: it does **not**
    silently no-op, and it does not disable an arbitrary uniform. The case falls to §6's *"not
    attributable to one uniform or feature"* row (3→4) — log it on `schmaloogium.gl`, keep the program
    running, and let a persistent recurrence escalate. Rung 2 degrades to rung 3's shape rather than
    to nothing. **A replay that comes back clean *repeatedly* is evidence for (b) rather than (a)**,
    and is the shape §11.4's frame-level hand-off exists to reduce.

  `RecordingGLDevice` answers the drain from `ScriptedResponses`, so both window shapes — and
  therefore rung-2 behaviour end to end — are testable with no GL at all.
- **No GL constants appear in any signature.** `ShaderStage`, `FramebufferTarget`, `ClearTarget`,
  `FramebufferStatus`, `BlendState` are engine enums/records; the LWJGL3 backend maps them to `GL_*`.
  A raw `int target` parameter would be the GL-verb layer wearing a costume.
- **`DebugService` exists in v0.1 as a no-op.** Its implementation is `v0.5` (Phase 14), but its
  presence now means Phase 4/5's object-creation sites can call `label(handle, "colortex0")` from day
  one, which is exactly the "architect now, implement later" rule of §G0.3.
- **Data moves in both directions, and both directions are verbs here.** A facade that can create and
  bind objects but not put data into them or read data out of them is not implementable by its
  dependents: Phase 6's `centerDepthSmooth` readback is a **v0.1** consumer of this section
  (RESEARCH.md §4.4/§6.2), Phase 13 must fill the noise texture and the `_n`/`_s` and custom textures
  (RESEARCH.md §4.1 step 4, App F.5), `atlasSize` (App D.3) and `eyeBrightness` (App D.1) are
  `ivec2`, `blendFunc` (App D.4) is `ivec4`, and the `depthtex1`/`depthtex2` copies move depth into
  standalone textures. Hence `readDepthPixel`, `TextureService.upload`, `copyDepthToTexture`, and the
  `int,int` and `int,int,int,int` uniform overloads above. Each is a
  transfer verb with no policy attached — no format choice, no cadence, no unit number — which is why
  they belong here and their callers' rules do not.
- **No pre-link program-parameter hook, and that is a statement rather than an omission.** Packs may
  configure geometry shaders the legacy way — `#extension GL_ARB_geometry_shader4 : enable` plus
  `const int maxVerticesOut = N;` (RESEARCH.md §3.1, App A.3) — which in raw GL requires
  `glProgramParameteriARB` calls between attach and link. RESEARCH.md §6.2 lists core GL 3.2
  geometry shaders **with internal translation** as a modernization opportunity, the preprocessor
  continuing to accept both source forms, and this project adopts it. The ARB form is therefore
  handled *upstream of the facade*, as a source-level rewrite in the Phase 3 front-end / Phase 4
  compile path, and `ShaderService` sees
  only core-form sources — so `bindAttributeLocation` remains its only pre-link verb. If Phase 4
  concludes the ARB program-parameter path must be driven through GL after all, that is an additive
  `ShaderService` request in Phase 4's own §5 (the mechanism §5.2 already names), not a redesign.
  §5.2 surfaces the resulting obligation to **Phase 3** as a flagged assumption it may contest.

**What the facade deliberately does NOT contain**, so no later phase mistakes an omission for a gap.
Two kinds, and the distinction matters:

*Policy, which belongs to the phase that owns the rule:* texture formats and the fixed unit map
(Phase 5/6), ping-pong/flip logic (Phase 5), draw-buffer routing decisions (Phase 5), clear colors and
when to clear (Phase 5), uniform cadences and smoothing (Phase 6), the program registry and backup
chains (Phase 4). The facade offers verbs; every one of those is policy about when to use them.

*Verbs deliberately absent — data-transfer and state alike* — so a dependent knows whether it is
looking at a gap or at a decision. **The last column carries three kinds of entry, and each row's
*Why absent* cell says which kind it is.** Most rows name the phase that would **request** the verb.
Where nobody requests it, the column names the phase that **owns the served work in its place** —
the instanced-draw row is that case: nobody requests an instanced verb, and the phases named there
own the work that replaces it. Where `DESIGN.md` routes the question through a **deliverable** rather
than through a requester, the column names the phase that **owns the deliverable which decides the
assignment** — the face-culling row is that case: Phase 3's engine-flag ownership map is what routes
`backFace.*`, and `DESIGN.md`'s own worked example routes it to Phase 7, who does the wiring. The
third kind is neither a requester nor an owner of the served work, and saying so is the whole reason
the column is headed as it is:

| Absent verb | Why absent | Who requests it — or owns the served work, or owns the deliverable that decides |
|---|---|---|
| PBO + fence-sync **asynchronous** readback | RESEARCH.md §6.2 lists it as a modernization *opportunity* over the reference's synchronous `glReadPixels`, with latency to verify. v0.1 ships the faithful synchronous `readDepthPixel` | **14** (RESEARCH.md §6.2 is Phase 14's ledger) |
| General **color-attachment** readback (regions, formats, screenshots) | No consumer at any milestone in the current phase set; the one contract readback is center-depth | 14, or any phase that acquires a real need |
| Texture **read**back (`glGetTexImage`-shaped) | No consumer at any milestone in the current phase set. Phase 1's expectation is that Phase 13's companion-atlas construction builds data rather than reading it back — but that is Phase 13's design to make, not this document's, and if it needs the verb the request is additive | **13** |
| `ivec3` / `mat3` uniform uploads | **No contract consumer.** The sweep of App D end to end plus App F.6's custom-uniform types (`float/int/bool/vec2/vec3/vec4`) turns up no declaration of either. `ivec4` is *not* in this row: App D.4 declares `blendFunc` as `ivec4`, and the overload exists (see `UniformService`) | whichever phase meets the first one; additive |
| **Colour mask** (`glColorMask`-shaped) | The one RESEARCH.md §4.3 state element with a GL consequence that this facade withholds. RESEARCH.md §4.3's `final` pass renders to the vanilla framebuffer with **anaglyph-aware colour masking**, and `DESIGN.md` assigns the anaglyph-aware final to Phase 7. If Phase 7 drives that through vanilla's own path it needs no verb; if it wants it through the facade, the request is additive and this row is where it starts | **7** |
| **Face-culling state** (`backFace.*`, App F.1) | Terrain draws through vanilla's path, so these flags are applied by `:mod` through `GlStateManager` rather than through the facade. The authoritative assignment is not this document's to make: `DESIGN.md` makes the **engine-flag ownership map** a Phase 3 deliverable (§G5.3's integration review audits it), and this row exists so a reader is not left wondering whether the absence is an oversight | **3** (produces the map; `DESIGN.md`'s own worked example routes `backFace.*` to **7**) |
| Free-standing pixel-store state (row length, alignment) | Carried *inside* `TextureData`'s layout instead, so an upload cannot leave global state perturbed behind it | — (by design) |
| Pre-link program parameters | See the ARB-geometry rule above: the path is translated upstream | **4**, if translation proves insufficient |
| **Instanced draw** (a `fullscreenQuadInstanced(int)`-shaped verb) | **It cannot express the directive it looks like it serves.** `const int countInstances = N` re-renders the geometry N times with an **incrementing `instanceId` uniform** (RESEARCH.md §3.2, App A.3), and App D.4 declares `instanceId` as an `int` *uniform* — one instanced draw cannot vary a uniform between copies, and the backend could not do it on the caller's behalf either, since it is handed no `UniformLocation`. GLSL 120 has no `gl_InstanceID` (RESEARCH.md §3.5), which is precisely why the contract carries a uniform at all. The faithful shape — **for the composite/deferred programs RESEARCH.md §4.4 observes the loop on** — is a caller-side loop over `fullscreenQuad()` with `UniformService.upload(instanceIdLoc, i)` between copies, which this facade already supports. The directive on a **gbuffers/shadow** program is not this row's subject and would not be served by an instanced verb either: that geometry is vanilla's and is drawn outside the facade (§3's second row, `[D-P1-35]`). A verb of this shape was present in the §0.5 revision, unspecified, and is deleted `[D-P1-33]` Nobody **requests** this verb; what follows is who owns the served work instead. **7** — the composite/final **execution** owner, `[v0.5]`: `DESIGN.md` names the `countInstances` instancing loop in Phase 7's *Scope — in* under composite/final execution, and says it a second time and more explicitly in Phase 4's, *"`countInstances` exposure to the pass executor (**execution is Phase 7, tag v0.5**)"* — which is where the milestone comes from. **6** owns the `instanceId` upload the loop makes between copies (`DESIGN.md` carries it among Phase 6's per-draw dynamics). **5** owns the buffer estate those passes read and write — the read/write/flip law the N draws run inside — but not the draw loop that runs them. An instanced verb itself is requested by **7**, and only if a future non-GLSL-120 path ever needs one |

Additions to this facade are expected and cheap; **silent** additions are not. A phase that needs a
verb adds it as a requested change in its own §5 (§5.2), and this document is amended by a fix-up
session — the same route §4.9.2 uses for a new log channel.

#### 4.7.5 Recording / replay for headless tests

Lives in `com.schmaloogium.engine.gl.record`, inside `:engine` per §G3.1 ("engine.gl … + a
recording/replay implementation for headless tests"). Phase 1 owns the mechanism; Phase 2 owns
golden content and the update workflow.

```java
public record GLCall(String op, List<Object> args) {}

public final class GLCallLog {
    public List<GLCall> calls();
    public List<GLCall> callsMatching(String opPrefix);
    public String render();            // one call per line, for golden files & failure messages

    /** Bounded. The log keeps at most `capacity` calls and discards the OLDEST beyond it,
     *  recording how many were dropped so render() can say so rather than lie by omission.
     *  Tests use an effectively-unbounded capacity; the live -Dschmaloogium.debug.recordGL
     *  decorator (§4.9.3) uses a bounded ring, because a GL call log over a real session is
     *  otherwise unbounded memory growth on the one hot path §7 identifies. */
    public static GLCallLog bounded(int capacity);
    public static GLCallLog unbounded();   // the counterpart tests use
    public int droppedCallCount();
}

public final class RecordingGLDevice implements GLDevice {
    /** Records into a fresh GLCallLog.unbounded(). */
    public RecordingGLDevice(GLCapabilityProfile profile, ScriptedResponses responses);
    /** Records into a caller-supplied log. This is how the live
     *  -Dschmaloogium.debug.recordGL decorator (§4.9.3) gives the device its bounded
     *  100 000-call ring, and how a test that wants a specific capacity chooses one.
     *  Without it neither consumer could size or supply the bound §5.2 exposes. */
    public RecordingGLDevice(GLCapabilityProfile profile, ScriptedResponses responses,
                             GLCallLog log);
    public GLCallLog log();
}

/** Canned answers for query-shaped calls, so tests can drive failure paths. */
public final class ScriptedResponses {
    public ScriptedResponses linkFails(String programLabel, String driverLog);
    public ScriptedResponses compileFails(String shaderLabel, String driverLog);
    public ScriptedResponses validateFails(String programLabel, String driverLog);
    public ScriptedResponses uniformAbsent(String uniformName);
    public ScriptedResponses framebufferStatus(String fboLabel, FramebufferStatus status);
    /** Canned driver error, returned by the next drainErrors(). What makes §G2.4 rung 2 —
     *  "disable that uniform only" — testable with no GL context. */
    public ScriptedResponses glError(String op, String subjectLabel, GLErrorKind kind);
    /** Canned answer for FramebufferService.readDepthPixel — what makes Phase 6's
     *  centerDepthSmooth readback and its halflife smoothing testable with no GL at all. */
    public ScriptedResponses depthPixel(String fboLabel, int x, int y, float depth);
}
```

Behavior:

- Every mutating call appends a `GLCall` and returns a synthetic handle (a monotonic sequence
  number wrapped in the appropriate handle type). Handles are `equals`-comparable so assertions can
  say "the texture attached at index 2 is the one created third". **Sequence numbers are never
  reused, including after a `delete`** — which is what makes `noUseAfterDelete()` possible here and
  impossible in the LWJGL backend (§4.7.3).
- Every query-shaped call answers from the `GLCapabilityProfile` or the `ScriptedResponses`. The
  default responses are all-success, so a test that only cares about call sequence writes none.
- The log's rendered form is stable and deterministic: no timestamps, no identity hash codes, no
  iteration-order dependence. This is what makes it usable as a golden file, and it is a constraint,
  not an implementation note.
- **Bulk data is logged by summary, never by content.** A `TextureService.upload` call records its
  destination region, mip level, layout, texel count and a stable content hash — not the bytes.
  Raw texel dumps would make `render()` unreadable and its golden files unreviewable, which would
  defeat the point of the format. `readDepthPixel` records the coordinates and the answer it gave.

Assertions:

```java
public final class ReplayAssertions {
    public static ReplayAssertions assertThat(GLCallLog log);

    public ReplayAssertions calledInOrder(String... opNames);
    public ReplayAssertions neverCalled(String opName);
    public ReplayAssertions bindsBalanced();       // every bind has a matching unbind/rebind
    public ReplayAssertions noLeakedObjects();     // every create has a matching delete
    public ReplayAssertions noUseAfterDelete();    // no handle appears in a call after its delete
    public ReplayAssertions drawBuffersWere(int... attachmentIndices);
}
```

`bindsBalanced()` and `noLeakedObjects()` are named here because two later impl gates ask for exactly
them: Phase 5's "creates/destroys the full buffer estate for a classic pack without leaks", and the
general §G4.6 restore discipline. `noUseAfterDelete()` is the third, and it exists for the reason
§4.7.3 gives: the uninit/rebuild that an option toggle triggers is a v0.1 event, and a stale handle
fails *silently* against a live driver.

**What `bindsBalanced()` can and cannot see.** It reads the `GLCallLog`, so it sees facade-level
calls only. A binding perturbed *inside* the backend — `blit` and `copyDepthToTexture` both change
the framebuffer binding to do their work — is invisible to it. That is why §4.7.4 states the
restore as a **contract on those two verbs** rather than leaving it to this assertion, and why the
recording backend logs the restore explicitly: `blit` and `copyDepthToTexture` each append one call
recording the binding they restored, so `calledInOrder` can catch a backend that forgets.

**The fixture-production loop.** `mod.glue.CapabilityProbe` builds a `GLCapabilityProfile` from a
live context at display init and, under `-Dschmaloogium.debug.dumpCapabilities`, writes it in the
§4.7.2 text form. A developer with a given GPU runs the client once and contributes a profile
fixture. Without this, the recorded profiles Phase 2 replays would have to be hand-written, and
hand-written capability sets are exactly where wrong assumptions hide.

### 4.8 License, headers, and third-party notices (D-7)

#### 4.8.1 The LICENSE file

`[D-P1-17]` `LICENSE` at the repo root contains the **verbatim GPL-3.0 text**, and the project is
licensed **GPL-3.0-or-later** — the "or later" living in the per-file SPDX headers and in
`README.md`, which is where GPL-3.0's own recommended practice puts it (the license text itself is
version-specific; the "or later" grant is a statement about the work).

#### 4.8.2 Source-header convention

`[D-P1-18]` Every source file — `.java`, `.gradle`, and the mixin `.json` files where comments are
permitted — opens with:

```java
// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
```

Two lines, machine-readable, no copied license boilerplate in every file. SPDX identifiers are the
form license scanners and the wider ecosystem actually read. `README.md` carries the full statement
(what the project is, that it is GPL-3.0-or-later, and where the text lives).

#### 4.8.3 `THIRD-PARTY.md` — the D-8 compliance mechanism

D-8 permits incorporating LGPL-3.0 code from Iris and Angelica, with compliance: "preserve
copyright/license notices, mark modifications" (RESEARCH.md §10.1). Compliance is not something to
reconstruct at release time from memory, so the mechanism exists from day one:

`THIRD-PARTY.md` at the repo root, one entry per incorporation:

| Field | Content |
|---|---|
| Files | our paths carrying the incorporated code |
| Upstream | project, license, commit/version, URL |
| Notice | the upstream copyright notice, verbatim |
| Modifications | what we changed, marked |

Plus a standing prohibition at the top of the file, so nobody has to go find it in RESEARCH.md §10.1:

> **Never copy from glsl-transformer.** Iris bundles it; it is **AGPL-3.0**; its network-service
> terms would attach to the derived portion. Iris's own LGPL-3.0 code is fine. `[D-8]` `[V:web]`
>
> **The OptiFine decompile (`schlorbium-project/`) is behavioral-observation-only.** No identifier,
> structure, or code derived from it ships. `[D-8]` `[§G7.2]`

The v0.1 `THIRD-PARTY.md` is expected to be empty of entries and full of these rules. That is the
correct state — the mechanism exists before the first incorporation, not after.

#### 4.8.4 OQ-12 — the licensing note

*Scoped as assigned: GPL-3.0-or-later mod, LGPL-2.1 platform, LGPL-3.0 GUI dependency, jar-in-jar.*

The question (RESEARCH.md §11, verbatim): *"GPL-3.0-or-later mod on LGPL-2.1 platform + LGPL-3.0 GUI
dep; jar-in-jar implications."* Status: *"open — concern reduced by the `[D-7]` GPL-3.0-or-later
change (LGPL-3.0 combines cleanly)."*

**The platform (Cleanroom, LGPL-2.1).** Cleanroom is the runtime environment Schmaloogium is loaded
by, not a library we redistribute. RESEARCH.md §10.3 characterizes it as "Platform, not a linked
library in the derivative-work sense; standard mod practice." Nothing about a GPL-3.0-or-later mod
running on an LGPL-2.1 loader requires anything of either party: we do not ship Cleanroom, we do not
modify it, and the LGPL's obligations attach to distribution of the LGPL'd work. Separately, and
only if it ever mattered, LGPL-2.1 §3 permits converting a copy to GPL-2.0-or-later — but that
conversion is a right of a redistributor of Cleanroom, which we are not.

**The GUI dependency (ModularUI, LGPL-3.0).** LGPL-3.0 is, by construction, GPL-3.0 plus additional
permissions. A work combining LGPL-3.0 code with GPL-3.0-or-later code is a GPL-3.0-or-later work,
and the LGPL portions keep their notices. This direction of combination is the clean one — which is
what RESEARCH.md §11's "concern reduced by the `[D-7]` change" refers to: under the previously-planned
MIT license the combination would have forced questions about the resulting whole; under
GPL-3.0-or-later it does not.

**Jar-in-jar specifically.** Two possible arrangements, both compliant:

1. *Mod dependency* — ModularUI is installed separately and we declare a dependency. This is mere
   aggregation at the installation level; no combination question arises at distribution time.
2. *Bundled via the template's `contain` configuration* — ModularUI's jar ships inside ours and the
   loader extracts it. Here we are distributing the LGPL-3.0 work, so LGPL-3.0's terms attach to
   that copy: preserve its notices, ship its license text, and do not restrict the recipient's LGPL
   rights (notably the ability to replace the bundled version). None of that conflicts with
   GPL-3.0-or-later, and the bundled jar remains a separate, identifiable work rather than being
   merged into ours. RESEARCH.md §10.3 records the same conclusion: "Dynamic linking as a mod
   dependency is ecosystem-standard; jar-in-jar (`contain`) bundling eased under GPL-3.0-or-later
   (LGPL-3.0 combines cleanly)."

**Ecosystem precedent.** The arrangement is not novel on this platform or this Minecraft version.
Fugue — a standard Cleanroom companion mod — is **GPL-3.0** (RESEARCH.md §5.1), i.e. a copyleft mod
shipping against this exact LGPL-2.1 loader. On the shader-engine side specifically, Iris is
**LGPL-3.0** and Angelica is **LGPL-3.0 with MIT portions** (RESEARCH.md §10.3), both of which D-8
already contemplates reusing into our GPL-3.0-or-later work. The combination pattern
GPL-3.0-or-later mod + LGPL platform + LGPL library is well-trodden.

**Conclusion.** No obstacle, and no change to D-7 is warranted. Two obligations for later phases,
recorded here so they are not rediscovered: (a) whichever arrangement Phase 12 picks for ModularUI,
if it bundles, `THIRD-PARTY.md` gets an entry and the license text ships; (b) any LGPL-3.0 code
incorporated *into our sources* under D-8 follows §4.8.3 regardless of the GUI decision. OQ-12 can
be marked resolved-by-note once a reviewer accepts this section.

### 4.9 Logging, debug flags, and error channels

#### 4.9.1 The `Log`/`LogSink` SPI

`[D-P1-19]` `:engine` defines its own minimal logging SPI rather than depending on log4j.

The reasoning is not that log4j is forbidden — it is not on §G3.1's list. It is that log4j on
1.12.2 is supplied by the Minecraft runtime, so an `:engine` dependency on it would be a production
dependency that exists only because Minecraft happens to provide it: a soft version of exactly the
coupling D-6 removes, and one that makes `:engine`'s headless tests require a logging backend they
have no reason to need. The SPI is roughly twenty lines.

```java
package com.schmaloogium.engine.log;

public interface Log {
    void debug(String message, Object... args);      // {} placeholders, log4j-style
    void info (String message, Object... args);
    void warn (String message, Object... args);
    void error(String message, Object... args);
    void error(Throwable t, String message, Object... args);
    boolean isDebugEnabled();
}

public interface LogSink {
    void emit(String channel, LogLevel level, String message, Object[] args, Throwable t);
}

public final class Logs {
    public static void install(LogSink sink);        // mod.core at boot; tests install a capturing sink
    public static Log channel(String name);          // name from LogChannels
}
```

`mod.core` installs a log4j-backed sink during `preInit`. Tests install a capturing sink and assert
over it. Before installation, a no-op sink is active — so a static initializer that logs cannot
explode during class loading.

#### 4.9.2 The channel list

`[D-P1-20]` §G4.5 says "Log channels are per-subsystem (`schmaloogium.pack`, `.compile`, `.frame`,
`.gl`, …; Phase 1 fixes the list)." Fixed, as constants on `LogChannels`:

| Channel | Subsystem | Owner phase |
|---|---|---|
| `schmaloogium.boot` | mod lifecycle, engine bootstrap, capability probe | 1 / 7 |
| `schmaloogium.pack` | pack discovery, file model, dimension folders | 3 |
| `schmaloogium.preprocess` | `#include` resolution, macro header, preprocessor | 3 |
| `schmaloogium.config` | `shaders.properties`, options, profiles, screens, ID files | 3 |
| `schmaloogium.compile` | shader compile / link / validate | 4 |
| `schmaloogium.registry` | stage registry, program slots, backup chains | 4 |
| `schmaloogium.buffers` | framebuffers, colortex, ping-pong, clears, sizing | 5 |
| `schmaloogium.uniforms` | built-in uniforms, cadences, samplers, unit map | 6 |
| `schmaloogium.ids` | entity/block-entity/item id aliasing, per-draw dynamics, unknown-id warnings | 9 |
| `schmaloogium.textures` | texture systems: noise texture, `_n`/`_s` companion atlases, custom textures | 13 |
| `schmaloogium.expr` | custom-uniform expression engine | 11 |
| `schmaloogium.frame` | per-frame orchestration, pass dispatch | 7 |
| `schmaloogium.shadow` | shadow pass | 8 |
| `schmaloogium.gl` | facade-level GL events, capability gates, GL errors | 1 / 14 |
| `schmaloogium.compat` | coexistence detection, bail verdicts | 1 / 10 |
| `schmaloogium.gui` | options and pack-selection screens | 12 |
| `schmaloogium.conformance` | harness-side output | 2 |

Rules: channels are fixed strings on `LogChannels`, never composed at runtime; every log line goes to
exactly one channel; a subsystem that wants finer granularity uses `isDebugEnabled()` and message
content, not a new channel. This keeps a user's `log4j2.xml` filter meaningful — the reason for
per-subsystem channels in the first place.

The list covers **all fourteen phases**, which is a property worth stating rather than leaving to be
counted: "every log line goes to exactly one channel" is only satisfiable if every phase that logs
has one. `schmaloogium.ids` and `schmaloogium.textures` exist for exactly that reason — Phase 9 is
obliged to warn on unknown ids (`DESIGN.md` routes unknown names through §G2.4's degradation) and
Phase 13's texture work is a subsystem in its own right, and neither had a home.

The list is fixed but not frozen: a later phase that genuinely needs a channel adds it here via a
requested change to this document (§G1.1's "propose changes in your doc §11" applies in reverse —
a later phase flags it in its own §11 and this doc is amended by a fix-up session).

#### 4.9.3 Debug flags

`[D-P1-21]` Namespace: `-Dschmaloogium.debug.*`. All boolean. Absent means off. Never read
before `mod.core` has bootstrapped, so a malformed value can never affect class loading. Reserved
from day one, per §G4.5's instruction:

| Flag | Effect | Owner phase | Milestone |
|---|---|---|---|
| `schmaloogium.debug.saveSources` | Dump fully-processed shader sources to disk. **The `shaders.debug.save` equivalent (App F.8)** — reserved by name because the spec requires it. Phase 1 reserves the flag *name* at v0.1; the dump behind it is Phase 3's and arrives with the preprocessor. **Does not change the facade's `glGetError` cadence** — see the note below the table | 3 | `v0.1` (name reserved) |
| `schmaloogium.debug.dumpCapabilities` | `CapabilityProbe` writes the live `GLCapabilityProfile` in the §4.7.2 text form; the fixture-production loop. One-shot at init; **does not change the `glGetError` cadence** | 1 | `v0.1` |
| `schmaloogium.debug.recordGL` | Wrap the live `GLDevice` in a recorder and dump the `GLCallLog` — the same log format the headless tests assert over, captured from a real session. **Opt-in, allocation-heavy, never on by default, and the log is a bounded ring** (`GLCallLog.bounded(…)`, §4.7.5; default capacity 100 000 calls, the oldest discarded and counted). The decorator **constructs the ring and passes it to `RecordingGLDevice`'s three-arg constructor** — the device does not choose its own capacity. **Also puts `Lwjgl3GLDevice` on the per-call `glGetError` cadence** (§4.7.4, `[D-P1-30]`), which is the point: a call-level log with window-level error attribution would be half a record | 1 | `v0.1` |
| `schmaloogium.debug.glLabels` | Activate `DebugService` (KHR_debug object labels and groups). **Also puts `Lwjgl3GLDevice` on the per-call `glGetError` cadence** (§4.7.4, `[D-P1-30]`) | 14 | `v0.5` |

`recordGL` deserves a note: it means a bug reproduced in a live session produces an artifact that can
be replayed and asserted over in a headless test. That is the seam paying rent in the other
direction, and it costs one decorator — **a decorator that ships**, since `[D-P1-4]` merges
`:engine` (including `engine.gl.record`) into the mod jar. Its posture is therefore stated rather
than assumed: it wraps the live `GLDevice` only when the flag is set, it allocates per GL call, its
log is bounded, and it inherits the render-thread confinement of the device it wraps (§7). Nobody
should read §4.7.5's "test backend" framing as a guarantee that these classes are absent at runtime.

**Two of the four flags change the facade's `glGetError` cadence, and two deliberately do not.**
`recordGL` and `glLabels` are the GL-facing pair, and each puts `Lwjgl3GLDevice` on the per-call
cadence `[D-P1-30]` states. `saveSources` and `dumpCapabilities` do not, and the narrowness is the
point: the cadence flip costs a synchronous driver query per facade call, and a Phase 3 developer
dumping shader sources — or anyone producing a capability fixture — has not asked for that and
should not discover it as a frame-rate change. The trigger is stated on both sides here because
§4.7.4 is where the cadence lives and §4.9.3 is where a developer reads about the flag.

Not in this namespace, and deliberately so: `-Dmixin.debug.export`, `-Dmixin.checks.interfaces`, and
`-Dcrl.dev.mixin` are the platform's flags, not ours (§4.5.5).

#### 4.9.4 User-facing error channels

§G4.5 names three: chat errors (pack-level failures, capability gates), the shader GUI (per-program
compile errors), and the log. The design problem is that `:engine` produces the errors and cannot
name a Minecraft chat component.

`[D-P1-22]` `:engine` emits loader-neutral `EngineDiagnostic` values; `:mod` routes them.

```java
package com.schmaloogium.engine.diag;

public record EngineDiagnostic(
        DiagnosticSeverity severity,     // INFO, WARN, ERROR, FATAL
        UserChannel        channel,      // CHAT, SHADER_GUI, LOG_ONLY
        String             messageKey,   // a lang key, e.g. "schmaloogium.error.program.link"
        List<Object>       args,
        String             detail,       // driver log / stack detail; GUI and log only, never chat
        String             logChannel) { // one of LogChannels
}

public interface DiagnosticReporter {
    void report(EngineDiagnostic d);
}
```

`:engine` holds a `DiagnosticReporter`. In `:mod`, the implementation fans out: `CHAT` becomes a
translated chat message via the client player (dropped, with a log line, if no player exists yet),
`SHADER_GUI` accumulates into a per-pack error store that Phase 12's screen renders, `LOG_ONLY` goes
nowhere else. Every diagnostic reaches the log regardless of channel — the log is the transcript, the
other two are notifications.

Message keys, not message text, cross the seam: `:engine` has no business holding user-facing English,
and Phase 12 needs lang keys for the GUI anyway. The severity/channel split maps onto the §G2.4
degradation ladder — a disabled single uniform is `WARN`/`LOG_ONLY`, a failed program is
`ERROR`/`SHADER_GUI`, a failed capability gate is `ERROR`/`CHAT`.

### 4.10 The `mod.compat` bail registry

Phase 1 owns **the mechanism**. The mod-id list, the detection technique, and the message text are
**Phase 10 / OQ-5** (DESIGN.md §G10 assigns OQ-5 to P10; the Phase 10 spec says "via Phase 1's bail
registry"). This section builds the slot and nothing else.

```java
package com.schmaloogium.mod.compat;

public sealed interface CompatVerdict {
    record Ok()                                        implements CompatVerdict {}
    record Degrade(String reasonKey, List<Object> args) implements CompatVerdict {}
    record Bail   (String reasonKey, List<Object> args) implements CompatVerdict {}
}

public interface CompatCheck {
    String id();                                  // stable, for logs and for user-facing attribution
    CompatVerdict check(CompatContext ctx);
}

public interface CompatContext {
    boolean isModLoaded(String modId);
    boolean isClassPresent(String binaryName);    // for detecting a replacement that ships unnamed
    GLCapabilityProfile capabilities();           // capability gates are compat checks too
}

public final class BailRegistry {
    public static void register(CompatCheck check);         // during preInit
    public static CompatEvaluation evaluate(CompatContext ctx);
}

public record CompatEvaluation(
        List<CompatVerdict.Bail>    bails,
        List<CompatVerdict.Degrade> degradations) {
    public boolean shouldBail() { return !bails.isEmpty(); }
}
```

**Evaluation points** — three, each chosen because it is a moment where the answer can change:

1. **Before engine bootstrap** (post-`FMLLoadCompleteEvent`, before the first pack load). The
   ordinary case: another mod is installed, we detect it, shaders never start.
2. **Before any vertex-format change** (Phase 10). RESEARCH.md §4.1 step 3 notes that a pack load can
   trigger a vertex-format rebuild and world-renderer reload; that is the operation most likely to
   collide with a replaced chunk pipeline, so it re-checks.
3. **In `SchmaloogiumMixinPlugin.shouldApplyMixin`** for MOD-phase mixins (§4.5.2). This is the
   strongest form: a vetoed mixin is never applied, so there is no partially-instrumented state to
   unwind. It is available only for MOD-phase configs, which is precisely where the vertex-pipeline
   compat mixins will live.

**On `Bail`:** shaders are forced off and *stay* off for the session; an `EngineDiagnostic` with
`severity=ERROR`, `channel=CHAT`, and the check's `reasonKey` goes out; a line lands on
`schmaloogium.compat`; and the reason is retained so Phase 12's GUI can display it instead of an
empty pack list. This is §G2.4's rung 4 ("a capability gate failing at init turns the pack off
gracefully with a chat error") and rung 5 ("shaders-off must always be a reachable state"). Bailing
is not an error path — it is a supported terminal state.

**On `Degrade`:** a warning to log and GUI, and the engine continues. The `Degrade` case exists in
the type from day one because OQ-5 is explicitly undecided between "detect and bail" and "integrate"
(RESEARCH.md §5.3); a verdict type that can only say "stop" would force Phase 10 to widen the
mechanism it was told to reuse.

**What Phase 1 ships:** the types above, the registry, the three evaluation points **designed**, the
diagnostic routing, and **zero registered checks**. Only **point 1 is wired at v0.1** (§12 item 36).
Points 2 and 3 are `v0.3`: point 2 needs Phase 10's vertex-format change to exist, and point 3 needs
`SchmaloogiumMixinPlugin`, which ships with the `plugin` key at v0.3 (§4.5.2, §12 items 36b and 37).
§9's staging table says the same thing, and it is the authority if this paragraph ever drifts from
it again. `[D-P1-23]` No mod ids are named by this phase —
naming Celeritas or Nothirium here would be Phase 10's policy decision made by the wrong session, and
RESEARCH.md §2.3 shows the landscape moves (five-plus Vintagium forks, Celeritas source-only). The
example in the doc is a shape, not a policy:

```java
// Illustrative only — Phase 10 supplies the real checks and the mod-id list.
BailRegistry.register(new CompatCheck() {
    public String id() { return "example.chunk-renderer-replacement"; }
    public CompatVerdict check(CompatContext ctx) { return new CompatVerdict.Ok(); }
});
```

### 4.11 CI workflow adjustments

The three template workflows all hardcode root-relative `build/libs` `[V:template]`, which the module
split breaks — after the split, the mod jar is at `mod/build/libs`.

**`build.yml`.** Java 25 / Gradle 9.6.1 / `actions/*` versions unchanged.

**The step order is load-bearing, and it is stated here rather than left implied.** `./gradlew build`
from the root runs `check` → `test` in **every** subproject, so it already executes all four seam
tests itself. A named seam step placed *after* it would therefore never run on the failure it exists
to name: `build` would go red first and GitHub Actions would abort the job, leaving exactly the
anonymous test failure inside a build that `[D-P1-24]` exists to prevent. The named steps come
first — that is the whole mechanism, and it costs nothing.

1. **"Seam architecture test"** — `./gradlew :engine:test :mod:test`. A separate, named step so that
   a seam violation appears in the CI UI as *"Seam architecture test — failed"* rather than as an
   anonymous test failure. Given that the seam is this project's highest-weight structural risk
   (§2.2), its regression deserves to be legible at a glance. `[D-P1-24]` **Both module tasks are
   named deliberately:** C-1 lives in `:engine` but C-2 and C-3 live in `:mod` (§8.1), so a step
   running only `:engine:test` would leave half the seam sentence — the `:engine`-internals rule and
   the LWJGL confinement — surfacing anonymously.
2. **`./gradlew :conformance:test`** as a second named step. It runs **C-4**
   (`SeamConformanceDependencyTest`, §8.2) — the only step that does — plus the placeholder test; its
   *harness* content is what remains empty until Phase 2, so Phase 2 adds content rather than
   plumbing.
3. **`./gradlew build`** — unchanged, and still the aggregate gate (§12 item 15). It re-runs the four
   seam tests; on a clean commit they are `UP-TO-DATE` and the repetition is free. It is deliberately
   **not** scoped with `-x test`: `build -x test` drops `:conformance:compileTestJava` along with
   `test` (it is reachable only through it), and that compile is the only thing that surfaces a
   missing `:conformance` repository — so the aggregate gate, here and at §12 item 15's local run,
   would go green with `[D-P1-27]` silently disarmed.
4. Artifact upload path `build/libs` → `**/build/libs/*.jar`.
5. New `if: failure()` step uploading `**/build/reports/tests/**` — a failed architecture test whose
   report is unreachable is a bad day.

**`release.yml`.** `artifacts: "build/libs/*"` → `"mod/build/libs/*"`. Targeted at `:mod` specifically
rather than globbed, because a release should never accidentally publish `:engine`'s or
`:conformance`'s jars.

**`release-to-cf-mr.yml`.** The `files:` block's two globs retargeted at `mod/build/libs/`. Two
pre-existing items flagged for release time, not changed now: `modrinth-id` and `curseforge-id` are
both the literal string `placeholder` `[V:template]`, and `loaders: forge` is correct in the sense
that Cleanroom is Forge-lineage but should be confirmed against whatever the publishing platforms
expect for a Cleanroom-exclusive mod (D-1) before the first real publish.

**Phase 2 extension point.** A `conformance` job stub, `workflow_dispatch`-gated so it never runs
accidentally, containing: an `actions/cache` step keyed on pack version IDs (the §G6 fixture policy
is download-at-test-time with a local cache, and no pack may ever be committed), and a placeholder
step. Left deliberately visible and empty rather than absent, so Phase 2 fills a slot instead of
designing CI from scratch. Phase 2 owns everything inside it, including the OQ-10 headless-GL
question.

Not adopted: a license-header lint. It would be useful, but it is unasked-for scope and Phase 1
already has enough CI surface. Noted in §11.4 as a candidate.

---

## 5. Cross-phase interfaces

Phase 1 **consumes** nothing — it has no dependencies (§G5.1).

Phase 1 **exposes** the following. Everything here is a contract that later phases build against:
§G1.1 puts *"the PHASE docs of your declared dependencies"* in a dependent build session's mandatory
reading — the whole document, not this section alone — and then makes this section the thing that
binds: *"**Dependency docs are contracts.** What a dependency's PHASE doc exposes in its §5 is what
you build against."* Read the document; build against §5. A dependent that needs something not here
flags the request in its own §5 rather than inventing it (§G1.1), and §5.2's escape hatch below says
how.

This section is nevertheless written to be **sufficient on its own** — every obligation this
document places on another phase appears here, not only in §4 or §11 — because a reader who skims
is a reader this section has to survive.

### 5.1 Structural contracts

| Exposed | Detail | Consumed by |
|---|---|---|
| **Module layout** | `:engine`, `:mod`, `:conformance` with the §2.1 package table | all phases |
| **The seam constraints C-1 … C-4** | §4.3, stated mechanically and enforced by tests | all phases |
| **Package placement rule** | a phase's code goes in the package §2.1 assigns it; `.internal` sub-packages are private to `:engine` | all phases |
| **Version pin table + re-pin procedure** | §4.2.6 — every row carries the coordinate its value is re-verified against, and step 3 terminates in one of three rulings (record only / extra verification / block the bump) | all phases; operationally, whoever tags a milestone |
| **Naming** | `mod_id = schmaloogium`, root package `com.schmaloogium`, `Reference` at `com.schmaloogium.Reference` | all phases |

### 5.2 `engine.gl` — the facade

| Exposed | Detail | Consumed by |
|---|---|---|
| `GLDevice` + the seven services | §4.7.4 signatures. **Changed in the §0.5 revision:** `UniformService` gained an `ivec4` overload, `StateService` gained `depthTest`/`fog`, `FramebufferService` gained `copyDepthToTexture`, and `GLDevice` gained `drainErrors()`. **Changed in the §0.6 revision:** `DrawService.fullscreenQuadInstanced(int)` was **removed** (`[D-P1-33]`), `RecordingGLDevice` gained a log-supplying constructor, and the GL-error row's attribution promise was narrowed to what the default cadence delivers. **Changed in the §0.7 revision: nothing in any signature.** Two rows were corrected in prose only — the non-verbs row (the `countInstances` mapping scoped to composite/deferred and the non-composite case handed to Phase 7) and the GL-error row (the drain's cadence, and three preconditions of the rung-2 protocol). **Changed in the §0.8 revision: again nothing in any signature** — the same two rows corrected in prose: the **GL-error row** (the elision bit tracks *facade* calls while the GL flag is per-context, so a non-empty drain does not imply one of Phase 6's uploads failed — V8-1; and property (i) no longer asserts a per-sample halflife premise it had no source for — V8-3), and the **non-verbs row** (the composite `countInstances` loop is **Phase 7's**, not Phase 5's, with Phase 6 named for the `instanceId` upload — V8-2). **Changed in this revision (§0.9): again nothing in any signature** — every service interface, handle type and value type is byte-for-byte what rounds seven, eight and nine all reviewed, and round nine's one edit inside the §4.7.4 block is a **javadoc sentence** on `GLError` and `drainErrors()`, not a declaration (V9-2). The same two rows again, in prose: the **GL-error row** (the frame-level remedy is one of two and carries two limits, not "the only sound remedy" — V9-1; the recurring-foreign replay cost is stated — V9-1; property (i)'s disclaimer is rescoped to what a second evaluation would do — V9-10), and the **non-verbs row** (its header now covers adjacent owners as well as requesters, readmitting **Phase 5** — V9-7; the composite loop carries `DESIGN.md`'s **`[v0.5]`** tag and its Phase 4 citation — V9-8) | 4, 5, 6, 7, 8, 13, 14 |
| **GL-error surface** — `GLDevice.drainErrors()` → `List<GLError>` (`op`, `subjectLabel`, `kind`, `detail`) | §4.7.4. This is the signal §G2.4's **rung 2** acts on — rung 1 is Phase 11's expression isolation and never reaches GL (§6). **Attribution is scoped to the drain window, and this is contract, not implementation detail:** a window holding exactly one mutating **facade** call yields a record naming that call; a window holding several yields **at most one record per driver error flag** — in practice one, carrying `subjectLabel = "(batched, N calls)"` — because GL holds only the first error in a flag until that flag is cleared. The rung-2 protocol is therefore: drain, upload the program's uniform set, drain — and **only if that drain is non-empty**, re-upload the set draining between uploads, so each record names one uniform, then disable those uniforms only (`[D-P1-32]`). **Three properties of that protocol are contract too, because you cannot implement it correctly without them.** (i) The re-upload reuses the values **already computed for this sweep** and never re-evaluates the providers — `glUniform*` is idempotent on the bound program, so re-uploading cached values changes nothing except *which drain window* each upload lands in, which is the replay's entire purpose. Re-running the sweep would instead re-enter your world-state providers, and **this document deliberately asserts no property of what a second evaluation would do** — not that it is safe, and not that it is harmful. Two facts about the surroundings are relayed, and they are statements about the sources rather than about your providers: RESEARCH.md §4.4 places the world-state sampling at frame begin, and `DESIGN.md` puts the time-corrected halflife formula in **your** *Scope — in*. Both are already inputs of your own spec; the design that follows from them is yours, and that is precisely why the protocol is "re-upload" rather than "re-run". (ii) If the replay comes back clean — `OUT_OF_MEMORY` need not recur, and per the cadence note below the error may not have been ours at all — the sweep is **unattributable**, and falls to §6's 3→4 row rather than silently disabling nothing or guessing. (iii) A `GLError` naming a uniform depends on the backend **retaining the name passed to `locate(p, name)`**; `UniformLocation` carries no name in its signature, so this is a stated backend obligation (`[D-P1-34]`), not something a test can catch. The **cadence** that delivers all of it: a drain is a `glGetError` **loop terminating on `GL_NO_ERROR`** (the GL-sanctioned form — a single call leaks a second flag into the next window and misattributes it), and a drain issues **no query at all** when no mutating **facade** call has occurred since the previous drain — which is what makes the two-drain protocol cost **one** query per clean sweep. **That bit tracks *facade* calls, while the GL error flag is per-context**, so a drain window can hold an error this facade did not cause — vanilla's own draws never reach it (§3's second row). **A non-empty trailing drain therefore does not by itself imply that one of your uploads failed**, which is why (ii) is load-bearing in general rather than an `OUT_OF_MEMORY` corner. **Two consequences you should plan for.** First, a *recurring* foreign error re-enters the replay every frame — reproducing nothing, disabling nothing, and costing a re-upload of the whole set — so a replay that repeatedly comes back clean is evidence of foreign GL rather than of a flaky uniform, and is the case to escalate rather than to retry forever. Second, the mitigation is not this facade's: §4.7.4 states the case once and §11.4 hands **Phase 7** a frame-level drain to place, whose two limits are stated there — it bounds only the gap spanning the frame boundary, and it is subject to the same elision, so it is not unconditional at the driver unless a mutating facade call precedes it. It is one remedy among two, not the only sound one: §4.7.4 records that dropping the elision would also bound the window and is rejected on **cost**, not on soundness. This works in the **shipping** configuration; the per-call cadence under a debug context or `-Dschmaloogium.debug.recordGL`/`glLabels` (those two flags only) is an optimisation of it, not a precondition for it. `ScriptedResponses.glError(...)` makes both window shapes testable headlessly | **6** (rung 2 is its v0.1 scope-in), 4, 5, 14 |
| `GLCapabilityProfile` | §4.7.2, including `supportsMipmapGeneration()`, `hasExtension()`, `atLeast()`. For **Phase 3** it is the whole GL-side input to RESEARCH.md §3.5's standard macro header, not just the extension macros: `glVersionMajor`/`glVersionMinor` → `MC_GL_VERSION`, `glslVersion` → `MC_GLSL_VERSION`, `vendor` → `MC_GL_VENDOR_*`, `renderer` → `MC_GL_RENDERER_*`, `extensions()` → the on-demand `MC_<GL_extension>` set | 2, 3 (**the whole macro header**), 4, 5, 6, 14 |
| **`GLCapabilityProfile` text serialization format** | §4.7.2; `parse(Reader)` / `write(Writer)` | **2** (this is "recorded `GLCapabilityProfile`s"), 4, 5, 6 |
| Opaque handle types | §4.7.3 — four types, `UniformLocation.isAbsent()`, and the **lifetime rule**: a handle is invalid the moment its `delete` returns, and the uninit/rebuild that a pack or option change triggers (RESEARCH.md §4.1 step 5) is a routine v0.1 event. **Phase 5 owns re-acquisition** across it | 4, **5**, 6, 13 |
| `RecordingGLDevice`, `GLCallLog`, `GLCall`, `ScriptedResponses` | §4.7.5. `GLCallLog` is bounded (`bounded(capacity)` + `droppedCallCount()`) and is **supplied to the device**: `RecordingGLDevice(profile, responses, log)`, with `GLCallLog.unbounded()` as the counterpart tests use; `ScriptedResponses` covers compile/link/**validate** failures, absent uniforms, framebuffer status, scripted depth pixels and scripted driver errors | **2**, 4, 5, 6 |
| `ReplayAssertions` incl. `bindsBalanced()`, `noLeakedObjects()`, **`noUseAfterDelete()`**, `drawBuffersWere()` | §4.7.5. `bindsBalanced()` sees facade-level calls only — the backend's restore obligation on `blit`/`copyDepthToTexture` is a contract in §4.7.4, checked through the restore calls the recorder logs | 2, 4, 5 |
| `CompileResult` / `LinkResult` / `ValidateResult` | never-throwing result types carrying driver logs | 4 |
| `StateService` state verbs + `snapshot()` / `restore()` | the §G4.6 perturb-and-restore mechanism, over viewport, clears, depth mask, **depth test**, blend, alpha test and **fog** — the composite/final block RESEARCH.md §4.4 requires. `DrawService.fullscreenQuad()` establishes **no** state: the caller sets the block. The backend issues every `GlStateManager`-cached verb through `GlStateManager` (§4.7.4, `[D-P1-29]`) | 5, 6, **7** |
| **Pixel-transfer verbs** — `FramebufferService.readDepthPixel(f,x,y)`, `FramebufferService.copyDepthToTexture(src,dst,region)`, `TextureService.upload(t, TextureData)`, `UniformService.upload(loc,int,int)` (ivec2) and `upload(loc,int,int,int,int)` (**ivec4**) | §4.7.4; value types `TextureData` / `TextureRegion` / `PixelLayout` / `BlitSpec` | **6** (the v0.1 synchronous `centerDepthSmooth` readback; `atlasSize`/`eyeBrightness`; **`blendFunc`**, which App D.4 declares `ivec4` and §G5.1 puts in Phase 6's v0.1 inventory), **5** (the `depthtex1`/`depthtex2` copies, and the formats), **8** (the shadow pass's depth→`shadowtex1` copy at v0.2 — RESEARCH.md §4.5, and App B.2 lists it in the same table as `depthtex1`/`depthtex2`), **13** (noise texture, `_n`/`_s` atlases, custom textures), **9** (per-draw `blendFunc` dynamics at v0.3) |
| **The facade's stated non-verbs**, each with the phase that would request it — **and, where a row names an adjacent owner of the served work instead, that phase too** (§4.7.4's table header says which rows those are; the instanced-draw row is the only one today, because nobody *requests* an instanced verb) | §4.7.4's closing table — async/PBO readback, general colour readback, texture readback, `ivec3`/`mat3`, **colour mask**, **face culling**, pre-link program parameters, and **instanced draw**. `ivec4` is **not** among them: it is served. **Scoped in the §0.7 revision:** the caller-side loop that replaces an instanced verb answers `countInstances` **on composite/deferred programs only** — the form RESEARCH.md §4.4 observes. **Retargeted in the §0.8 revision:** the owner of that loop is **Phase 7**, not Phase 5 — `DESIGN.md` names the `countInstances` instancing loop in Phase 7's *Scope — in* under composite/final execution, and Phase 5's *Scope — in* carries no pass-execution bullet. **Cited and tagged in this revision (§0.9):** the stronger citation is `DESIGN.md` Phase 4's *Scope — in*, *"`countInstances` exposure to the pass executor (**execution is Phase 7, tag v0.5**)"*, which settles the owner and the milestone in one line — so the composite loop is **`[v0.5]` work**, not v0.1 work, and Phase 7's own v0.1-exit milestone does not carry it. The same directive on a **gbuffers/shadow** program re-renders vanilla geometry through Phase 7's hooks, never reaches this facade, and is an **open case with named owners** rather than a Phase 1 design element (`[D-P1-35]`, §3's second row, §11.4) | **14** (async readback), **13** (texture readback), **7** (colour mask / anaglyph; **and both halves of `countInstances` — the composite loop itself `[v0.5]`, which `DESIGN.md` names in your *Scope — in* under composite/final execution and tags in Phase 4's, and the non-composite re-render if it proves real** — plus an instanced draw verb only if a future non-GLSL-120 path ever needs one, since `countInstances` on a composite is a caller-side loop, `[D-P1-33]`), **6** (the `instanceId` **upload** that loop makes between copies — `DESIGN.md` carries it among your per-draw dynamics, invoked at Phase 7's hooks), **5** (**not** a requester of an absent verb, and named because the composite loop's N draws run inside the buffer estate you own — the read/write/flip law is yours and the loop does not change it; §4.7.4's table header says why this column carries adjacent owners), **3** (the App F.1 flag-ownership map that settles face culling; **and the `const`-scan that detects `countInstances` at all**), **4** (pre-link program parameters, only if the ARB geometry form is not translated upstream; **and the per-slot instance count**) |
| **Assumption Phase 3 may contest** — the ARB-geometry source rewrite | §4.7.4 exposes **no** pre-link program-parameter verb because the legacy `#extension GL_ARB_geometry_shader4` + `maxVerticesOut` form is rewritten to the core form **in your front-end**, upstream of GL (§4.7.4, §11.4). This document does not design that rewrite and does not own the translation *strategy* — `DESIGN.md` assigns the strategy to **Phase 4**'s spec, which also forbids ARB entry points through the facade. What is yours is the front-end half; if you conclude the rewrite belongs at the GL level instead, flag it in your §5 and the verb is added additively | **3**, with **4** |
| `DebugService` | present in v0.1, active at v0.5 | 4, 5 (call sites), **14** (implementation) |

**Explicit note to Phase 2:** your declared input is "`PHASE_1_DOC.md` (module layout, facade,
`GLCapabilityProfile`)". All three are in §2.1, §4.7.4, and §4.7.2 respectively; the serialization
format your replay depends on is §4.7.2's text form, and the recording backend is §4.7.5. **The
capture mechanism is yours to drive, and it already exists** — you own the fixture *set* and its
refresh workflow (§8.3), and the way a fixture is produced is `mod.glue.CapabilityProbe` under
`-Dschmaloogium.debug.dumpCapabilities`, with `-Dschmaloogium.debug.recordGL` producing a
`GLCallLog` from a live session in the same format your golden files use (§4.9.3, §4.7.5). Do not
design a capture path; drive these. What Phase 1 does *not* give you: the fixture set itself, the
golden-file format for anything other than `GLCallLog.render()`, and any answer to OQ-10.

**Explicit note to Phases 4/5/6:** your impl gates say "a recorded-GL run". The mechanism is
`RecordingGLDevice` + a `GLCapabilityProfile` fixture + `ReplayAssertions`. The fixtures reach your
tests as a dependency, not a path: they live in `:engine`'s `testFixtures` source set and are consumed
with `testImplementation testFixtures(project(':engine'))` (§8.3). If you need an assertion **or a
facade verb** that is not in §4.7.4/§4.7.5, add it in your own doc's §5 as a requested change to this
one — do not assume it exists. §4.7.4's closing table names the verbs that are absent on purpose and
who is expected to ask for each.

**Explicit note to Phase 6:** your spec assigns the synchronous center-depth readback at **v0.1**.
The verb is `FramebufferService.readDepthPixel(f, x, y)` — synchronous and stalling by design, per
RESEARCH.md §4.4/§6.2 — and `ScriptedResponses.depthPixel(...)` (§4.7.5) makes your smoothing math
testable with no GL context. The cadence, the pixel, and `centerDepthHalflife` are yours; the async
PBO variant is Phase 14's.

### 5.3 Conventions

| Exposed | Detail | Consumed by |
|---|---|---|
| `Log` / `LogSink` / `Logs` | §4.9.1 | all phases |
| **The fixed channel list** | §4.9.2 | all phases |
| `EngineDiagnostic`, `DiagnosticSeverity`, `UserChannel`, `DiagnosticReporter` | §4.9.4 | 3, 4, 5, 6, 7, 11, **12** (GUI is a channel consumer) |
| **Debug-flag namespace and the four reserved flags** | §4.9.3 | 3 (`saveSources`), **2** (`dumpCapabilities` and `recordGL` — the fixture and call-log capture path your harness drives), 14 (`glLabels`) |
| Mixin config slots (three, by phase) + package placement | §4.5.2. **Two duties come with them, both yours (Phase 7):** confirm Unimined actually generates a refmap for the first config that lands — the template's `main` branch has never carried one, so the machinery is unexercised in this checkout (§12 item 33, and it **blocks** hook work if it fails); and spot-check `compatibilityLevel: "JAVA_8"`, inherited verbatim from the template snapshot while our source level is Java 25, the first time a mixin uses a language feature that survives to bytecode | **7**, 10, 13 |
| SRG-targeting policy and the `schmaloogium$` prefix | §4.5.3 | 7, 10, 13 |
| `SchmaloogiumMixinPlugin` slot on the MOD config | §4.5.2 | 10 |
| `CompatCheck` / `CompatVerdict` / `CompatContext` / `BailRegistry` | §4.10 | **10** (policy), 7 (the bail hook) |
| **Mod-dependency declaration mechanics** | §4.2.6 (ModularUI deliberately not pinned — Phase 12 owns the decision), §4.8.4 (both arrangements and the obligations each carries: `contain` jar-in-jar is distribution of the LGPL-3.0 work and brings its notice obligations), §11.3 item 2 (the template declares only `modCompileOnly`/`modRuntimeOnly` — **no `modImplementation` configuration exists**; §12 item 43 is the fix) | **12** |
| SPDX header convention + `THIRD-PARTY.md` mechanism | §4.8.2, §4.8.3 | all phases; especially any phase incorporating LGPL-3.0 code under D-8 |
| CI job/step layout + the `conformance` extension point | §4.11 | **2** |

### 5.4 Requested changes to dependencies

None — Phase 1 has no dependencies. Requested changes to RESEARCH.md and DESIGN.md are in §11.5.

---

## 6. Failure modes & degradation

The §G2.4 ladder applied to foundation concerns. Rung 5 — "nothing in the shader engine ever crashes
the client or corrupts the vanilla framebuffer path; shaders-off must always be a reachable state" —
is the invariant every row below serves.

| Failure | Rung | Behavior |
|---|---|---|
| **Capability probe fails or returns nonsense** (missing entry point, driver returns 0 for a max) | 4 | `CapabilityProbe` catches, logs on `schmaloogium.gl`, and produces a *conservative* profile (GL 2.1, the spec minimums). The engine then fails its capability gates naturally and turns the pack off with a chat error. It does not guess optimistically, and it does not propagate an exception into display init. |
| **A capability gate fails at init** (pack needs more draw buffers / attachments / units than the profile offers) | 4 | Pack turns off gracefully; `EngineDiagnostic(ERROR, CHAT)` naming the shortfall; `schmaloogium.gl` line with the profile values. Vanilla rendering is untouched because no GL object was created yet. |
| **A shader fails to compile / a program fails to link or validate** | 3 | The facade returns a failed `CompileResult`/`LinkResult`/`ValidateResult` — it never throws. Phase 4 deletes the program, emits `EngineDiagnostic(ERROR, SHADER_GUI)` carrying the driver log, and resolves through the backup chain. |
| **A built-in uniform's upload fails at the driver level** | **2** | The backend records a `GLError`. Phase 6 drains, uploads the set, and drains again; if that drain is empty the sweep cost **one** `glGetError` — the leading drain elides its query (nothing mutating **through the facade** since the last drain) and the trailing drain's loop stops at its first `GL_NO_ERROR` (`[D-P1-30]`). If it is **not** empty, the record names the *sweep* rather than a uniform — GL holds only the first error in a flag until that flag is cleared — so Phase 6 re-uploads the set **draining between uploads**, which puts one mutating call in each window and names each failing uniform exactly (`[D-P1-32]`). The re-upload uses the values **already computed for this sweep** — `glUniform*` is idempotent on the bound program, so the replay changes only which drain window each upload lands in, and re-running the sweep would re-enter world-state providers whose cadence and smoothing math are Phase 6's own scope to design. It then **disables those uniforms only**, leaving the program running. `EngineDiagnostic(WARN, LOG_ONLY)` on `schmaloogium.uniforms` per §4.9.4's severity map. The facade supplies the signal (`GLDevice.drainErrors()`, §4.7.4); the disable policy is Phase 6's. **If the replay reproduces nothing** — `OUT_OF_MEMORY` need not recur, and the error may not have been ours at all, since the elision bit tracks *facade* calls while the GL flag is per-context (§4.7.4) — the drain is real but unattributable and falls to the 3→4 row below. |
| **A custom uniform's expression errors at runtime** | **1** | **Not a foundation failure mode, and recorded here so §6 maps the whole ladder.** `DESIGN.md`:217 scopes rung 1 to *custom* uniforms, and its Phase 11 spec puts the behaviour in `engine.expr`'s evaluator at **v0.4** — above the facade, with no GL call involved. Nothing in this phase observes it; Phase 1 supplies only `Log` and `EngineDiagnostic` for it to report through. |
| **A single feature's GL call fails** (a capability the pack asked for is unsupported in practice) | **— (unnumbered)** | The drain names the failing operation, the owning phase turns *that feature* off and continues. `DESIGN.md`'s ladder has **no rung for this**: rungs 1 and 2 are both about uniforms, so labelling it rung 1 would be inventing a step. It is a real failure mode and is recorded unnumbered rather than mislabelled. A *custom* uniform whose **upload** fails — as opposed to its evaluation — lands here too, served by the same drain and the same disable-one-uniform behaviour rung 2 describes. **The gap is also carried upstream, where it was found:** §11.5 item 4 asks `DESIGN.md` to add the rung, so Phases 5, 6 and 13 do not each re-derive this row independently. |
| **A facade call fails at the driver level, and the failure is not attributable to one uniform or feature** | 3→4 | The LWJGL3 backend records it in the drain and logs a diagnostic on `schmaloogium.gl`; it never throws through a mixin into vanilla's call stack. Mutating verbs return `void` — the drain, not a return value, is where the caller learns of it. Persistent failures escalate to a pack-level bail. **This row is also where rung 2 lands when its replay finds nothing** (`[D-P1-32]`), and that happens **for either of two reasons — this row is the destination of five delegations and is written to receive both** (§4.7.4's precondition (ii) names the set): (a) `GLErrorKind.OUT_OF_MEMORY` is the kind that need not recur; (b) **the error may never have been ours**, since the elision bit tracks *facade* calls while the GL error flag is per-context (§4.7.4), so a window can hold an error this facade did not cause and a replay of our own uploads will of course reproduce nothing. A replay that comes back clean **repeatedly** is evidence for (b) rather than (a), and is the case §11.4's frame-level hand-off exists to reduce. Either way a detected failure that no window can attribute is unattributable by definition, and the honest response is to log it and keep the program running rather than disable an arbitrary uniform or pretend nothing happened. |
| **A `CompatCheck` returns `Bail`** | 4 | Shaders forced off for the session, chat error with the check's reason, `schmaloogium.compat` line, reason retained for the GUI. A supported terminal state, not a crash. |
| **A `CompatCheck` itself throws** | 4 | Caught by `BailRegistry.evaluate`, logged with the check's `id()`, and treated as `Bail` — a check that cannot decide is not evidence of compatibility. Fails safe. |
| **No `LogSink` installed yet** (something logs during class loading) | — | A no-op sink is active until `mod.core` installs the real one. Logging can never be the thing that breaks startup. |
| **A diagnostic targets `CHAT` before a player exists** | — | Downgraded to log-only with a note; never buffered indefinitely and never dropped silently. |
| **`:engine` throws an unexpected `RuntimeException`** | 5 | `mod.core` wraps every engine entry point at the glue boundary. The engine is disabled for the session, one `EngineDiagnostic(FATAL, CHAT)` is emitted, and the vanilla path resumes. This wrapper is the last line of the ladder and it is `:mod`'s job precisely because `:engine` must not know what "the vanilla path" is. |
| **A seam violation reaches a build** | build-time | The §4.3 tests fail. CI's named "Seam architecture test" step runs `:engine:test` **and** `:mod:test`, so C-1, C-2 and C-3 all go red under that name; C-4 goes red under the named `:conformance:test` step (§4.11). **Both named steps run before `./gradlew build`**, which would otherwise run the same four tests first and abort the job under an anonymous name (`[D-P1-24]`). Not a runtime failure mode — by design. |

---

## 7. Threading & performance notes

**Thread ownership.**

| Component | Thread |
|---|---|
| Every `engine.gl` facade call, and therefore every `Lwjgl3GLDevice` method | **Render thread only** (§G2.3: "The render thread owns all GL"), **with one sanctioned exception below** |
| `ShaderService.compile` / `TextureService.upload` under Phase 14's shared-context design | The one exception §G2.3 itself carves out: shader compilation and texture upload may run off-thread on a second GL context, *with the mandatory synchronous fallback*. That is Phase 14's design to build (v0.5), and Phase 13's v0.5 uploads are its first client. Until it exists, the row above holds unconditionally — and the facade's own signatures assume nothing either way |
| `CapabilityProbe` | Render thread, once, at display init |
| `BailRegistry.evaluate` | Main/client thread at bootstrap; render thread at the vertex-format-change and mixin-plugin evaluation points |
| `Logs` / `LogSink` | Any thread. The installed sink must be thread-safe; the log4j-backed one is |
| `DiagnosticReporter` | Any thread for `LOG_ONLY`; `CHAT` and `SHADER_GUI` deliveries hop to the client thread |
| `:engine` types generally | **No thread affinity by construction.** `:engine` holds no thread-local state and starts no threads. Phases 3 and 11 are permitted off-thread work, and Phase 14 the compile/upload exception above (§G2.3); the seam is what makes that safe to reason about |
| `RecordingGLDevice` in tests | Single-threaded by assumption, and documented as such |
| `RecordingGLDevice` as the live `-Dschmaloogium.debug.recordGL` decorator | **Render thread only** — it inherits the confinement of the `GLDevice` it wraps, and it is not made thread-safe to cover the Phase 14 exception. If off-thread uploads ever need recording, that is a Phase 14 request against this document, not an assumption to make now |

**Allocation posture** (§G2.5). Clean code first; optimize with evidence. Specifically for this
phase's types: `GLCapabilityProfile` is allocated once per session. Handles are small records
allocated at object-creation time, not per-frame. `EngineDiagnostic` is allocated on error paths
only. The one type on a potential hot path is `UniformLocation`, which Phase 6's location caching
will hold per program per uniform — allocated at cache-fill time, then reused, which is the same
shape as the reference implementation's behavior (RESEARCH.md §4.2, "per-program location caching +
redundant-upload skipping"). No array caches, no mutable-pose machinery, no object pools: §G2.5 is
explicit that generational ZGC on Java 25 removes the constraint that produced those in the
reference.

**Known hot paths introduced here.** Exactly one: the facade sits between the engine and every GL
call, so a per-call allocation or a megamorphic dispatch in a service implementation would be paid
per draw. Mitigations designed in: services are interfaces with a single production implementation
each (so the JIT sees a monomorphic call site in practice), no varargs on the uniform-upload
overloads, and no boxing in any signature on a per-frame path. The GL-error surface is a **batched
drain** for the same reason, and the cost being avoided is the *query* as much as the allocation: a
returned status on `upload` would put a value on that path per call, and `glGetError` is a
synchronous driver query — which is why the per-call cadence is the debug-mode one and why draining
once per program set, one query per sweep, is the default. **One query, and the figure is earned
rather than asserted:** the stated rung-2 protocol makes *two* drain calls per sweep, and it costs
one query because a drain with no mutating **facade** call behind it issues none, while the drain that does
query loops only until its first `GL_NO_ERROR` (`[D-P1-30]`). Both properties are backend
obligations, not caller discipline — which matters here, because a program switch is the universal
state barrier (RESEARCH.md §4.2) and there are 43 slots, so a factor of two on a synchronous driver
query would be paid per program switch per frame. `[D-P1-32]`'s attributed replay does not
reintroduce it on this path either: the clean sweep still costs exactly one query, and the per-upload
cadence is entered only after a drain has already come back non-empty — which, for the failure this
protocol was written for, is a frame that is about to disable a uniform once (§4.7.4). **The one case
in which that is not once is a *foreign* error that recurs**, since the elision lets vanilla's own GL
land in our window: the replay then runs every frame, reproduces nothing, disables nothing, and costs
a replay of the whole ~90-uniform set per program set. It is bounded by nothing this phase owns, and
§11.4's hand-off to Phase 7 is where a frame-level mitigation would go.

**`RecordingGLDevice` allocates freely, and it *does* ship.** `[D-P1-4]` merges `:engine` — including
`engine.gl.record` — into the mod jar, and `-Dschmaloogium.debug.recordGL` (§4.9.3) wraps the live
device in it during a real session. So the honest statement is not "never in a shipped path" but:
the classes ship, the decorator is **opt-in and off by default**, it allocates per GL call while
active, and its log is a **bounded ring** (`GLCallLog.bounded(…)`, default 100 000 calls, oldest
discarded and counted) — a log the **decorator constructs and hands to the device**
(`RecordingGLDevice(profile, responses, log)`, §4.7.5) — precisely so an hour-long session cannot
grow it without limit. Nobody should
"optimize" the recorder for the test path, and nobody should assume it is absent at runtime.

**Explicitly not a hot path.** `ReplayAssertions`, `CapabilityProbe`, `BailRegistry`, and the
diagnostic machinery are all init-time or test-time. `GLCallLog` is init-time or test-time **except**
under `recordGL`, where it is per-call by definition — which is what the bound is for.

---

## 8. Testability plan

### 8.1 Headless unit tests owned by this phase

| Test | Module | Asserts |
|---|---|---|
| `SeamClasspathTest` | `:engine` | Constraint **C-1**, classpath half — no forbidden coordinate on `main`'s compile or runtime classpath (§4.3 layer 2) |
| `SeamBytecodeTest` | `:engine` | Constraint **C-1**, bytecode half — no forbidden type referenced by any compiled `:engine` class (§4.3 layer 3). **This is the test the Impl gate names** |
| `SeamInternalsTest` | `:mod` | Constraint **C-2** — no `:mod` class references `com.schmaloogium.engine.*.internal.*` |
| `SeamLwjglConfinementTest` | `:mod` | Constraint **C-3** — no `org.lwjgl*` reference outside `com.schmaloogium.mod.glue` (the mechanical half of §G4.6) |
| `SeamConformanceDependencyTest` | `:conformance` | Constraint **C-4** — no `:mod` artifact on `:conformance`'s classpaths and no `com.schmaloogium.mod.` reference in its classes (§8.2) |
| `GLCapabilityProfileSerializationTest` | `:engine` | Round-trip `write` → `parse` is identity; output is sorted and deterministic; a hand-written fixture parses to the expected values |
| `GLCapabilityProfileDerivationTest` | `:engine` | `atLeast`, `hasExtension`, and `supportsMipmapGeneration()` (true at 3.0, false at 2.1 — the RESEARCH.md §4.1 gate) |
| `RecordingGLDeviceTest` | `:engine` | Calls are logged in order with correct arguments; handles are distinct, `equals`-comparable and never reused after a `delete`; `ScriptedResponses` drives failure paths including a scripted compile/link/**validate** failure and a scripted `GLError` surfacing through `drainErrors()`; a bounded log discards oldest-first and reports `droppedCallCount()`; a device constructed with a caller-supplied bounded log records into that log; `render()` is stable across runs |
| `ReplayAssertionsTest` | `:engine` | Each assertion passes on a conforming log and fails with a useful message on a violating one — including `bindsBalanced()`, `noLeakedObjects()` and `noUseAfterDelete()`, since Phase 5's impl gate depends on the first two and the handle-lifetime rule (§4.7.3) depends on the third |
| `BailRegistryTest` | `:mod` | `Ok`/`Degrade`/`Bail` aggregation; a throwing check is treated as `Bail`; evaluation is idempotent |
| `LogChannelTest` | `:engine` | Every `LogChannels` constant is unique and starts with `schmaloogium.`; the no-op sink is active before installation |
| `DiagnosticRoutingTest` | `:mod` | `CHAT`/`SHADER_GUI`/`LOG_ONLY` route correctly; `CHAT` with no player degrades to log; every diagnostic reaches the log |

`:conformance` gets its JUnit wiring, its `:engine` dependency, and a single placeholder test proving
the module builds and runs. Its content is Phase 2's.

### 8.2 Constraint C-4

`:conformance` must depend on `:engine` and never on `:mod`. **Mechanical, like the other three** —
the Doc gate asks for testable constraints, and leaving one to inspection makes the set uneven for no
saving.

`conformance/build.gradle` injects `schmaloogium.test.compileClasspath`, `…runtimeClasspath` and
`…classesDir` exactly as `:engine`'s does (§4.2.3, §4.2.4a), and `SeamConformanceDependencyTest` in
`:conformance` asserts two things: no classpath entry resolves to the `:mod` project, and no compiled
`:conformance` class references a type under `com.schmaloogium.mod.`. Same two-layer shape as C-1,
same immediate failure message, and it costs the handful of lines the previous draft of this section
already sketched.

**Both halves of the classpath pattern must be able to match**, which takes two corrections a reader
would otherwise inherit as working:

- The artifact pattern is `schmaloogium-*.jar`, **not** `mod-*.jar`. `:mod` keeps the template's
  `base { archivesName = mod_id }` (§4.2.4 moves the root machinery verbatim; §4.4.1 sets
  `mod_id = schmaloogium`), so its artifacts are `schmaloogium-0.1.0-dev.jar` /
  `schmaloogium-0.1.0.jar` and a `mod-*.jar` pattern can never fire. It is not redundant with the
  path check either: a `:conformance` dependency on the *published coordinate* resolves from a Maven
  cache path that contains `schmaloogium-0.1.0.jar` and no `mod/build` segment at all.
- The path check tests for a `mod` + `build` **path segment pair**, using the platform's file
  separator — not a literal `mod/build` substring, which would not match a Windows classpath entry.

### 8.3 Fixtures

Two kinds, both introduced here:

1. **`GLCapabilityProfile` fixtures** under `engine/src/testFixtures/resources/profiles/`, captured
   from real hardware via `-Dschmaloogium.debug.dumpCapabilities` (§4.9.3). Phase 1 defines the format
   and the capture mechanism; Phase 2 owns the fixture *set* (which GPUs, which minima, how they are
   refreshed) and the refresh workflow.

   **Why `:engine` and not `:conformance`.** Every test that consumes a profile is in `:engine` or
   downstream of it: this phase's `GLCapabilityProfileSerializationTest` /
   `GLCapabilityProfileDerivationTest` / `RecordingGLDeviceTest` (§8.1), and the "recorded-GL run"
   impl gates of Phases 4/5/6, whose subsystems and headless tests live in `:engine` too (§2.1). C-4
   fixes the dependency direction as `:conformance → :engine` and never the reverse, so a resource
   sitting in `:conformance`'s test resources is **not** on `:engine`'s test classpath — the tests that
   need it could not read it. `:engine` therefore applies core Gradle's `java-test-fixtures` plugin
   (§4.2.3) and the profiles live in its `testFixtures` source set: `:engine`'s own tests get them
   automatically, and `:conformance` and `:mod` declare
   `testImplementation testFixtures(project(':engine'))` — a dependency edge in the legal direction
   rather than a path reaching across modules.
2. **`GLCallLog` golden files**, rendered by `GLCallLog.render()`. Phase 1 guarantees the format is
   stable and deterministic — that guarantee is a testable property (`RecordingGLDeviceTest`) and it
   is what makes golden files viable at all. Phase 2 owns the golden-file workflow.

**No shader pack is ever committed.** §G6's resolved fixture policy (OQ-11) applies from this phase
forward: CI downloads at test time with a local cache, and re-hosting is prohibited for all seven
matrix packs. Nothing in Phase 1 needs a pack, so there is nothing to get wrong yet — recorded so it
stays that way.

### 8.4 Conformance tiers

Phase 1 exercises **no** conformance tier. T0–T3 are defined by Phase 2 and first run when a renderer
exists (Phase 7). What Phase 1 contributes to that future is the third slice of §G6's testability
split — the machinery for "per-phase headless tests … against the `engine.gl` facade / recorded
`GLCapabilityProfile`s" — and the `:conformance` module the harness lives in.

---

## 9. Milestone staging

Per §G4.3, every designed component carries exactly one tag meaning "implemented at that milestone".

| Component | Tag | Note |
|---|---|---|
| Gradle module split (`:engine`/`:mod`/`:conformance`) | `v0.1` | |
| Seam tests C-1, C-2, C-3, C-4 | `v0.1` | C-1 is the Impl gate; C-4 is `SeamConformanceDependencyTest` (§8.2) |
| Template conversion (package, mod id, Blossom, metadata) | `v0.1` | |
| ATs disabled; `modid_at.cfg` deleted | `v0.1` | The re-enable path is documented, not built |
| `enable_lwjglx = false` | `v0.1` | |
| Version pin table + `PINS.md` + re-pin procedure | `v0.1` | Re-run at every milestone thereafter |
| `LICENSE` (GPL-3.0), SPDX headers, `THIRD-PARTY.md` | `v0.1` | |
| `GLCapabilityProfile` + serialization | `v0.1` | |
| `GLDevice`, `ShaderService`, `UniformService`, `TextureService`, `FramebufferService`, `StateService`, `DrawService` | `v0.1` | Interfaces + the LWJGL3 implementation |
| Pixel-transfer verbs (`readDepthPixel`, `copyDepthToTexture`, `TextureService.upload`, ivec2 and **ivec4** uploads) + `TextureData`/`TextureRegion`/`PixelLayout`/`BlitSpec` | `v0.1` | Interfaces and LWJGL3 implementation at v0.1 because the first consumers are v0.1: Phase 6's center-depth readback and its `blendFunc` (`ivec4`) uniform, Phase 5's `depthtex1`/`depthtex2` copies, and **Phase 8**'s shadow-pass depth→`shadowtex1` copy at `v0.2` (RESEARCH.md §4.5, App B.2). Phase 13's texture uploads arrive at `v0.5` against the same verbs |
| GL-error surface (`GLDevice.drainErrors()`, `GLError`, `GLErrorKind`) + the backend's `glGetError` policy | `v0.1` | §G2.4 rung 2 is Phase 6's v0.1 scope-in, so the signal it acts on cannot be later than v0.1. The **cadence** ships with it: batched by default with window-scoped attribution — each drain a `glGetError` loop, elided entirely when nothing mutating has happened **through the facade** since the last one — and per call under a debug context or the `recordGL`/`glLabels` flags (`[D-P1-30]`, `[D-P1-32]`). Rung 2 is implementable at v0.1 in the *shipping* configuration, which is the property this tag asserts |
| Opaque handle types + the handle-lifetime rule | `v0.1` | `noUseAfterDelete()` ships with `ReplayAssertions` |
| `RecordingGLDevice`, `GLCallLog`, `ScriptedResponses`, `ReplayAssertions` | `v0.1` | D-10 requires the headless path from week one |
| `CapabilityProbe` + `dumpCapabilities` | `v0.1` | |
| `DebugService` **interface** | `v0.1` | Present so call sites exist |
| `DebugService` **implementation** (KHR_debug labels/groups) | `v0.5` | Phase 14 |
| `schmaloogium.debug.glLabels` | `v0.5` | Phase 14 |
| `schmaloogium.debug.recordGL` (live decorator + bounded log) | `v0.1` | Ships inside the merged jar; opt-in and off by default (§4.9.3, §7) |
| `schmaloogium.debug.saveSources` (**flag name reserved**; `LogChannels`-style constant, no behavior) | `v0.1` | The dump itself is Phase 3's and arrives with the preprocessor — the reservation is what §G4.5 asks of this phase |
| `Log`/`LogSink`/`Logs` + fixed channel list | `v0.1` | |
| `EngineDiagnostic` + routing (`CHAT`, `LOG_ONLY`) | `v0.1` | |
| `EngineDiagnostic` routing to `SHADER_GUI` | `v0.4` | The sink is Phase 12's screen; the store exists at v0.1 |
| `MixinConfigs` manifest attribute + `schmaloogium.default.mixin.json` | `v0.1` | The config exists; its contents arrive with Phase 7 |
| `schmaloogium.preinit.mixin.json` (empty, reserved) | `v0.1` | First tenant expected `v0.3` (Phase 10) |
| `schmaloogium.mod.mixin.json` (empty, reserved) | `v0.1` | First tenant expected `v0.3` |
| `SchmaloogiumMixinPlugin` slot | `v0.3` | Reserved in this document at v0.1; the class **and** the `plugin` key in `schmaloogium.mod.mixin.json` both land at v0.3, in the same change (§4.5.2) |
| Mixin dev flags (`mixin.debug.export`, `mixin.checks.interfaces`) | `v0.1` | |
| `CompatCheck`/`CompatVerdict`/`CompatContext`/`BailRegistry` mechanism | `v0.1` | |
| Registered compat checks (the policy) | `v0.3` | Phase 10 / OQ-5 |
| `BailRegistry` evaluation point 1 (bootstrap) | `v0.1` | |
| `BailRegistry` evaluation points 2 (vertex-format change) and 3 (mixin plugin) | `v0.3` | Both need Phase 10 to exist |
| CI: seam test step, artifact path fixes, test-report upload | `v0.1` | |
| CI: `conformance` job stub | `v0.1` | Empty slot; Phase 2 fills it |

---

## 10. OQ & spike specifications

Per §G4.4: verbatim question, concrete procedure, success/failure criteria, and a fallback designed
now.

### 10.1 OQ-2 — Cleanroom loader pin

**Question, verbatim from RESEARCH.md §11:**

> Current Cleanroom loader vs template's 0.5.17-alpha pin | Alpha drift; daily cadence | build setup
> | GitHub releases | **RESOLVED 2026-07-24**: 0.6.6-alpha current; **standing item** — re-verify at
> design time and pin deliberately

**Status at this phase.** Re-verified 2026-07-24 for this document: `0.6.6-alpha` remains current,
confirmed independently by the GitHub releases API and by `<release>` in
`repo.cleanroommc.com`'s `maven-metadata.xml`. Two releases shipped on the re-verification date
itself, which corroborates the daily cadence rather than contradicting the pin. **Pinned:
`0.6.6-alpha`**, as `cleanroom_loader_version` in `gradle.properties`.

Because OQ-2 is "resolved, standing", the spike is not an investigation — it is the recurring
procedure. §4.2.6 states it operationally; here it is in §G4.4 form.

**Procedure.**
1. Trigger: before every milestone tag, before any release-workflow run, and on suspicion of a
   platform-caused failure. Never automatic, never scheduled.
2. Read `<release>` from `https://repo.cleanroommc.com/releases/com/cleanroommc/cleanroom/maven-metadata.xml`.
3. Cross-check against `https://api.github.com/repos/CleanroomMC/Cleanroom/releases?per_page=10`.
4. Diff release notes from the current pin forward; flag any mention of CleanMix, MixinBooter,
   Foundation, classloader, mod discovery, LWJGL, or the render path — then **rule**: record only,
   extra verification (the targeted check §4.2.6 names for that category), or block the bump. The
   ruling, not the flag, is what the step produces.
5. Bump `cleanroom_loader_version`; `./gradlew build`,
   `./gradlew :engine:test :mod:test :conformance:test`, and a manual `:mod:runClient` smoke run to
   the main menu, plus whatever step 4's ruling added.
6. Append a row to `PINS.md`, including the ruling.
7. Re-check the remaining rows of §4.2.6 against the coordinates in their Repository column.

**Success criteria.** The build succeeds, all module tests pass, the client reaches the main menu,
and (once a renderer exists) the harness's runnable subset is unchanged. `PINS.md` has a new row.

**Failure criteria.** Any of: resolution failure, compile failure, test failure, client crash, or a
behavioral change in the harness subset attributable to the bump.

**Fallback, designed now.** Revert `cleanroom_loader_version` to the last known-good value — a
one-line revert, which is the entire reason the pin is a property. Record the failed attempt in
`PINS.md` with the symptom and the release notes entry suspected. Raise it upstream via the §7.7
engagement channel. **A broken alpha blocks the bump, never the milestone.** The project ships
against the last known-good loader; there is no scenario in which platform churn stalls a release,
because we never depend on an unpinned version.

### 10.2 OQ-12 — licensing

**Question, verbatim from RESEARCH.md §11:**

> GPL-3.0-or-later mod on LGPL-2.1 platform + LGPL-3.0 GUI dep; jar-in-jar implications | licensing
> hygiene | §10.3 | short considered note; ecosystem precedent survey | open — concern reduced by the
> `[D-7]` GPL-3.0-or-later change (LGPL-3.0 combines cleanly)

**Procedure.** The verification path RESEARCH.md names is "short considered note; ecosystem precedent
survey" — which is what §4.8.4 is. The remaining procedure is confirmation, not investigation:
1. A reviewer reads §4.8.4 against RESEARCH.md §10.3 and confirms the three characterizations
   (platform-not-library; LGPL-3.0-into-GPL-3.0-or-later is the clean direction; jar-in-jar is
   distribution of the LGPL work and carries the LGPL's notice obligations).
2. When Phase 12 decides ModularUI's arrangement (mod dependency vs `contain`), that decision is
   checked against §4.8.4's obligation (b) and, if it bundles, a `THIRD-PARTY.md` entry plus the
   shipped license text is verified present in the built jar.

**Success criteria.** §4.8.4 survives review with no correction, and the Phase 12 arrangement — when
made — carries its notice obligations. OQ-12's status moves to resolved-by-note.

**Failure criteria.** A reviewer identifies a combination the note mischaracterizes, or a
distribution channel imposes a term inconsistent with GPL-3.0-or-later.

**Fallback, designed now.** If the ModularUI arrangement turns out to be problematic under bundling,
fall back to arrangement (1): declare ModularUI as an ordinary mod dependency and do not bundle it.
That removes the distribution question entirely at the cost of one more install step for users, and
requires no license change and no code change — only a build-file line. If a broader problem is found
with GPL-3.0-or-later itself, that is **not** this phase's call to make: D-7 is a user decision
(RESEARCH.md §1.3, "user decision 2026-07-24"), and §G1.1 says decisions contradicting D-1..D-10 are
to be flagged, not made. It would be flagged in §11.4 as a requested upstream change.

### 10.3 OQ-20 — seam hardness

**Question, verbatim from RESEARCH.md §11:**

> **Kirino-Engine trajectory**: timeline, default-on?, does a compat-profile vanilla pipeline survive
> beneath it, license terms | Every render-loop hook + the vertex pipeline could be invalidated; also
> the best future backend | long-term architecture (§5.2, §7.2) | track repo + #405; engage upstream
> (§7.7) | open — **highest-weight strategic risk**

**This phase's share.** §G10 assigns OQ-20 to "G8/S5 + **P1** (seam hardness requirement)". Phase 1
does not forecast Kirino's trajectory — that is G8/S5's. Phase 1 owns the question *"is our seam
actually hard enough to survive the swap that trajectory might force?"* The facade granularity
decision (§4.7.1) is an answer to that question, and an untested answer is a guess.

**Procedure — the backend-swap drill.**
1. Read Kirino-Engine's *public API surface only* (its README and the public types of its
   RenderPass/Subpass/render-command model). **API surface only** — RESEARCH.md §10.3 records Kirino
   as a custom "Custom Mod Permissions License" with the instruction "Observe API surface only;
   licensing needs review before any integration." Nothing is copied; nothing is derived.
2. On paper, map each of the seven `engine.gl` services onto that model. For each service method,
   record: *directly expressible*, *expressible with buffering* (the call must be deferred into a
   command list before submission), or *not expressible*.
3. Count the `:engine` classes that would need to change if the facade were reimplemented over that
   model. The recording backend already proves the facade admits at least one non-LWJGL
   implementation; this drill asks whether it admits a *structurally different* one.
4. Independently, write a `NullGLDevice` (every call a no-op, every query answered from a supplied
   profile) and run `:engine`'s tests against it. Any test that fails reveals a place where engine
   logic depends on GL *behavior* rather than on the facade *contract* — i.e. a seam leak.

   **This step is scheduled at the first milestone at which `:engine` carries logic behind the
   facade — not at v0.1**, and the reason is worth stating because it is the difference between a
   check and a ritual. At v0.1 the only `:engine` tests that touch a `GLDevice` at all are
   `RecordingGLDeviceTest` and `ReplayAssertionsTest`, which the criterion below excludes by
   construction (they assert on recorded calls, so they need the recorder); §1.3 guarantees
   `engine.pack`/`registry`/`buffers`/`uniforms` are empty at v0.1. Run at v0.1, the step's tested
   set is **empty** and it cannot fail. Steps 1–3 stay at v0.1, where they do bite: they are a paper
   mapping of a facade that exists.
5. Timing: run steps 1–3 at the end of the Phase 1 implementation session; run step 4 at the first
   milestone where `:engine` holds pass logic behind the facade — in practice **v0.3**, when
   Phase 4's registry and Phase 5's buffer estate are live — and re-run the whole drill whenever
   Kirino's public API materially changes. §12 item 41 carries the v0.1 half; item 41b carries
   step 4.

**Success criteria.** Step 3 yields **zero** `:engine` classes needing change, with every service
method landing in *directly expressible* or *expressible with buffering*. Step 4, at the milestone
it is scheduled for: all `:engine` tests pass against `NullGLDevice`, except those that assert on
recorded calls (which by construction need the recorder) — and the excepted set must be a **minority
of the tests that exercise the facade**, otherwise the step is measuring nothing and the milestone
was chosen too early.

**Failure criteria.** Any service method is *not expressible*; or step 3 requires changes to
`:engine` classes outside `engine.gl` itself; or step 4 surfaces engine logic depending on GL
behavior the facade does not promise.

**Fallback, designed now.** If the facade proves too fine-grained, coarsen it one level: replace the
imperative service calls on the hot path with **submitted descriptions** — the engine builds a
`RenderPassDescription` value (targets, program, uniform set, draw) and hands it to
`GLDevice.submit(...)`, and the backend decides how to realize it. That is a mechanical
transformation of the call sites in `engine.buffers`/`engine.registry`, not a redesign, *provided*
handles are already opaque and no GL constant has leaked into engine code — which is exactly why
§4.7.1 and §4.7.3 make those choices now. The fallback's viability is itself a reason for the opaque-
handle decision, and that is the point of designing it before it is needed.

**Upstream action** (RESEARCH.md §7.7, which the G8/S5 sketch also names): post Schmaloogium's hook
requirements to CleanroomMC Discussion #405 as a concrete consumer use case — a shader engine needs
pass insertion points, program substitution at draw time, and framebuffer routing control. A
sanctioned API that already accommodates a shader engine is the best possible resolution of OQ-20,
and it is likelier if the requirement is stated while the design is open.

### 10.4 OQ-21 — lwjglx flux

**Question, verbatim from RESEARCH.md §11:**

> lwjglx replacement flux (LWJGLXX/LWJGLY) | template has `enable_lwjglx=true`; legacy-GL shim
> behavior may change under us | build config; §6.1 | track CleanroomMC repos | open

**This phase's disposition.** The compile-time half is decided unconditionally (§4.6:
`enable_lwjglx=false`; compile against LWJGL3 proper), because §6.1 and §G2.2 make it binding. The
spike covers only the runtime half, which is genuinely open.

**Procedure.**
1. Build the mod jar with `enable_lwjglx=false` and confirm the §4.3 bytecode assertion reports no
   `org.lwjglx` reference — the compile-time guarantee, mechanically.
2. Launch a Cleanroom client of the pinned loader version **with** lwjglx present (the default
   install) and confirm the mod loads, the capability probe produces a plausible profile, and the
   `GLCapabilityProfile` values match what the hardware should report.
3. Launch **without** lwjglx, if the loader permits omitting it, and repeat. If it cannot be omitted,
   record that as the finding — "lwjglx is not optional on this loader version" is itself an answer,
   and a more useful one than a guess.
4. Compare the two profiles. A difference means the shim is intercepting capability queries, which is
   material to Phase 7's OQ-3 spike and gets handed there.
5. Re-check the CleanroomMC org for LWJGLXX and LWJGLY status at each milestone re-pin (§10.1 step 3
   already reads the release notes; add the two repos to that pass). LWJGLY was an *empty placeholder
   repo* at 2026-07-24; the first commit to it is the signal to look properly.

**Success criteria.** The jar contains no `org.lwjglx` reference; the mod loads and probes correctly
in both configurations; the two `GLCapabilityProfile`s are identical.

**Failure criteria.** The mod fails to load in either configuration; or the profiles differ; or the
loader gains a hard dependency on a shim whose API we would have to compile against — the one
outcome that would force revisiting §4.6.

**Fallback, designed now.** If a future loader release genuinely requires compiling against a shim
API, re-enable the dependency as `compileOnly` with a **pinned** version, add the shim's package to
an explicit allowlist in the §4.3 bytecode assertion (so the exception is visible in the test rather
than by deletion of the rule), and confine every reference to `mod.glue` — where §G4.6 already
confines LWJGL. `:engine` remains untouched under every branch of this fallback, because it never
referenced graphics APIs in the first place. That is the seam doing its job on a question it was not
designed for, which is the best available evidence that it is drawn in the right place.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale |
|---|---|---|
| D-P1-1 | `mod_id = schmaloogium`, `root_package = com.schmaloogium`; Blossom's `package` property overridden to `root_package` alone | The spec proposes `schmaloogium`; the template's `"${root_package}.${mod_id}"` derivation would otherwise produce `com.schmaloogium.schmaloogium` |
| D-P1-2 | `rootProject.name` pinned to the literal `'Schmaloogium'` | Under a multi-project build the root name leaks into IDEA module keys and publishing; directory-name derivation makes the build depend on the clone path |
| D-P1-3 | ASM is permitted at `testImplementation` scope only, in **`:engine`, `:mod` and `:conformance`** — and in `:mod` Unimined's inherited `asm-debug-all` 5.x is **excluded** at `testImplementation` scope | Needed by the bytecode architecture tests, which is all four seam constraints and therefore three modules; not a forbidden coordinate; never on any production classpath the tests assert over. `asm-debug-all` is a **shaded fat jar carrying `org.objectweb.asm` itself**, so against `org.ow2.asm:asm` it is a *split package*, not a version conflict: Gradle arbitrates per `group:name`, the two are different modules, and no `resolutionStrategy.force` can choose between them — exclusion is the only instrument. It is declared at `testImplementation` rather than on `testRuntimeClasspath` because C-2/C-3 are **compiled** against `testCompileClasspath`; both resolvable configurations extend `testImplementation`, so one line covers compile and runtime. ASM 5 cannot read Java 25 class files, so an unguarded compile classpath fails C-2/C-3 with "Unsupported class file major version" rather than a seam message (§4.2.4) `[fix-up: PHASE_1_REVIEW_5.md V5-4]` |
| D-P1-4 | `:engine` classes are **merged** into `:mod`'s jar (not `contain`, not shadow) | Same codebase, same license, same package root — nothing to isolate. `contain` adds a load-time extraction path for a first-party module; shadow flips the active remap task for no gain |
| D-P1-5 | The loader pin lives in `gradle.properties` as `cleanroom_loader_version`, not inline in `build.gradle` | Makes the re-pin procedure a one-line, reviewable, revertible diff — which is what turns OQ-2 from a risk into a routine |
| D-P1-6 | The `:engine`-internals rule is a package convention (`.internal`) enforced by a bytecode scan; **JPMS is rejected** | `:mod` runs on Foundation's flat classpath where the module graph does not exist; `:engine` as an automatic module opens all packages, so the guarantee would evaporate exactly where it matters |
| D-P1-7 | No access transformers for v0.1; `use_access_transformer=false`; `modid_at.cfg` deleted; the `rootProject.projectDir` path bug fixed pre-emptively | The spec prefers none until a hook requires one; no Phase 1 component needs one; disarming the path trap now costs nothing and saves Phase 7 a debugging session |
| D-P1-8 | License stated in `mod_credits` + `LICENSE` + `README.md`; **no** `mcmod.info` `license` key | The 1.12.2 `mcmod.info` schema has no such key `[V:mcp]`; inventing one produces metadata nothing reads |
| D-P1-9 | Mixin configs declared via the `MixinConfigs` jar-manifest attribute, sourced from a `mixin_configs` property | Current canon; legacy MixinBooter loader interfaces are `@Deprecated` `[V:mcp]` `[RESEARCH.md §5.1]` |
| D-P1-10 | `is_coremod` stays `false` | CleanMix is built into the loader; coremods are discouraged; adding one opens a class-transformation path D-5 has no use for |
| D-P1-11 | Three mixin configs, one per CleanMix phase (`PRE_INIT`/`DEFAULT`/`MOD`); `"server": []` is permanent | Phases are the axis CleanMix dispatches on; splitting later means editing manifest, files, and every `@Mixin` package at once. Schmaloogium is client-only (RESEARCH.md §1.2) |
| D-P1-12 | SRG names in every annotation; MCP names in comments; injected methods prefixed `schmaloogium$` | App E's stated requirement; the prefix is cheap collision insurance in a coremod-heavy ecosystem |
| D-P1-13 | Refmap generation left to Unimined; `disableRefmap()` not called | Template README and the MCP guide agree; but the `main` branch has never had a config, so first-config refmap generation is an explicit checklist item |
| D-P1-14 | `enable_lwjglx = false` | Its only effect is `compileOnly org.lwjglx`; with it on, an illegal import compiles silently. RESEARCH.md §6.1 and §G2.2 make the rule binding, so the build should enforce it |
| D-P1-15 | Grouped role services + opaque handles, not a thin GL-verb layer | A GL-verb facade is OpenGL with a different package name; it encodes imperative semantics into `:engine` and would make the Kirino swap a rewrite (OQ-20) |
| D-P1-16 | `GLCapabilityProfile` has a stable, sorted, human-readable text serialization | It is what "recorded `GLCapabilityProfile`s" means for Phase 2 and what Phase 4/5/6's "recorded-GL run" gates run against; diff-readability matters when a driver update changes one extension |
| D-P1-17 | `LICENSE` carries verbatim GPL-3.0; "or-later" lives in SPDX headers and `README.md` | The license text is version-specific; the "or later" grant is a statement about the work, which is where GPL-3.0's own guidance puts it |
| D-P1-18 | Two-line SPDX header on every source file | Machine-readable, no boilerplate duplication, and the form license scanners actually read |
| D-P1-19 | `:engine` defines a zero-dependency `Log`/`LogSink` SPI instead of depending on log4j | log4j on 1.12.2 comes from the Minecraft runtime; depending on it would be coupling that exists only because Minecraft supplies it, and would make headless tests need a logging backend |
| D-P1-20 | The §4.9.2 channel list is fixed; channels are constants, never composed | §G4.5 assigns the list to this phase; composed channel names make a user's log4j filter meaningless |
| D-P1-21 | Debug flags namespaced `schmaloogium.debug.*`, boolean, absent = off, read after bootstrap | §G4.5 requires `saveSources` reserved; a uniform namespace makes the set discoverable and keeps malformed values away from class loading |
| D-P1-22 | `:engine` emits `EngineDiagnostic` values with lang **keys**; `:mod` routes to chat/GUI/log | The seam forbids Minecraft types in `:engine`, and Phase 12 needs lang keys for the GUI regardless |
| D-P1-23 | The bail registry ships with **zero** registered checks and names no mod ids | Naming Celeritas or Nothirium here would be Phase 10's policy decision made by the wrong session; the landscape moves (RESEARCH.md §2.3) |
| D-P1-24 | CI runs `:engine:test` **and** `:mod:test` as one named "Seam architecture test" step and `:conformance:test` as a second named step, **both placed before `./gradlew build`** | The seam is the project's highest-weight structural risk; its regression should be legible at a glance, not buried in an aggregate build. **Both module tasks** are named because C-1 lives in `:engine` while C-2 and C-3 live in `:mod` (§8.1), so a step running only `:engine:test` would leave half the seam sentence anonymous. **The ordering is load-bearing, not cosmetic:** `./gradlew build` → `check` → `test` runs all four seam tests itself, so a named step placed after it would never execute on the very failure it exists to name — the job would already have aborted inside `build` (§4.11) `[fix-up: PHASE_1_REVIEW_4.md F4-10; PHASE_1_REVIEW_5.md V5-5, V5-3]` |
| D-P1-25 | The facade carries **pixel-transfer verbs** (`FramebufferService.readDepthPixel`, `FramebufferService.copyDepthToTexture`, `TextureService.upload`, `UniformService.upload(loc,int,int)` and `upload(loc,int,int,int,int)`) and **no pre-link program-parameter hook**; the ARB geometry form is translated upstream of the facade | Dependents cannot implement their milestones without them: Phase 6's synchronous `centerDepthSmooth` readback is a v0.1 consumer (RESEARCH.md §4.4/§6.2), Phase 13 fills the noise/`_n`/`_s`/custom textures (RESEARCH.md §4.1 step 4, App F.5), `atlasSize` (App D.3) and `eyeBrightness` (App D.1) are `ivec2`, `blendFunc` (App D.4) is `ivec4`, and the `depthtex1`/`depthtex2` copies target textures rather than framebuffers (RESEARCH.md §4.3). Each verb carries no policy. The ARB geometry path is a *source-level* construct that RESEARCH.md §6.2's internal translation handles in the front-end, so a GL-level program-parameter verb would be the wrong seam — and its absence is stated, not implied `[fix-up: PHASE_1_REVIEW.md F-1; PHASE_1_REVIEW_4.md F3-2, F4-7]` |
| D-P1-26 | Shared `GLCapabilityProfile` fixtures live in **`:engine`'s `testFixtures`** source set, consumed by `:conformance`/`:mod` via `testFixtures(project(':engine'))` | C-4 makes `:conformance → :engine` the only legal direction, so fixtures in `:conformance` are unreadable by the `:engine` tests that need them; a fixtures source set keeps the dependency edge legal and Phase 2's ownership of the fixture *set* intact `[fix-up: PHASE_1_REVIEW.md F-2]` |
| D-P1-27 | `:conformance` declares its **own** `repositories { mavenCentral() }` (§4.2.4a); `mavenCentral()` is **not** hoisted into the root `subprojects {}` block | Repositories are per-`Project` in Gradle with no inheritance, so without it `:conformance` cannot resolve JUnit and the Impl gate's `./gradlew build` fails in the *test* configuration. Hoisting would fix it in fewer lines but would inject `mavenCentral()` into `:mod` ahead of `dependencies.gradle`'s mod repositories and blur §4.2.3's single-file statement of what `:engine` can see `[fix-up: PHASE_1_REVIEW_4.md F3-5]` |
| D-P1-28 | **A handle is invalid the moment its `delete` returns**; Phase 5 owns re-acquisition across the uninit/rebuild; `ReplayAssertions.noUseAfterDelete()` enforces under replay what the LWJGL backend cannot | Teardown-and-rebuild is a routine v0.1 event (RESEARCH.md §4.1 step 5 — an option change fires it), and a driver may reissue a GL name after a delete, so a stale handle silently addresses a *different live object*. The recording backend's monotonic, never-reused sequence numbers make the misuse detectable; stating the rule is what turns an accident into a contract `[fix-up: PHASE_1_REVIEW_4.md F4-8]` |
| D-P1-29 | **Backend obligation:** every `Lwjgl3GLDevice` verb whose GL state `GlStateManager` caches is issued *through* `GlStateManager`, never through raw LWJGL — `bindToUnit` (unit + bind), every `StateService` verb but `viewport`, and clears | §G4.6 forbids bypassing the cache for state it holds, and `DESIGN.md` makes the consequence correctness rather than style ("the cache would go stale and break vanilla rendering"). `bindToUnit` is the highest-frequency instance, not an edge case: the fixed unit map re-points up to 16 units per program switch (RESEARCH.md §4.2). No signature changes; the rule constrains the implementation `[fix-up: PHASE_1_REVIEW_4.md F4-3]` |
| D-P1-30 | Driver errors surface through a **batched drain** (`GLDevice.drainErrors()`), not a per-call return status, with the backend's `glGetError` cadence stated as part of the contract | §G2.4's **rung 2** needs a signal — rung 1 is Phase 11's expression isolation and never reaches a GL call (§6) — and `DESIGN.md` puts per-uniform GL-error isolation in Phase 6's **v0.1** scope-in, a rung the facade previously gave nothing to act on, since every mutating verb returns `void`. A drain matches the consumer's shape (upload a program's set, then sweep) and keeps both the allocation and the synchronous driver query off the per-call hot path §7 identifies. **The cadence is now stated in GL's own terms** (round seven; the two inline URLs are round **eight**'s, V8-4, which supplied the source round seven read but did not cite): a drain is a `glGetError` **loop** terminating on `GL_NO_ERROR`, because an implementation may hold several error flags and a single call returns and clears an arbitrary one `[V:web]` — the OpenGL `glGetError` reference page, `https://docs.gl/gl4/glGetError`, wording identical at `https://docs.gl/gl2/glGetError` for the GL 2.1-era refpage, read 2026-07-25 — so a single call per drain leaks a flag into the next window and misattributes it. A drain also issues **no query at all** when no mutating facade call has occurred since the previous one, which is what makes the two-drain rung-2 protocol cost one query rather than two. **That bit tracks *facade* calls while the GL error flag is per-context**, so the elision cannot bound a window against vanilla's own GL: a drain window may hold an error this facade did not cause, and it is `[D-P1-32]`'s unattributable branch — not the bit — that contains the case (§4.7.4, and §11.4 for the frame-level remedy, which is Phase 7's to place). **There are two remedies and neither is uniquely sound:** dropping the elision would bound the window against all GL and is rejected on **cost**; a facade-internal guard is unavailable on **mechanism**. What §11.4 hands Phase 7 is the second remedy with its two limits attached — it bounds only the frame-boundary gap, and it is subject to this same elision. The per-call debug cadence is triggered by a debug context or the two GL-facing flags (`recordGL`, `glLabels`) only, not by any `-Dschmaloogium.debug.*` flag: `saveSources` is Phase 3's and `dumpCapabilities` is one-shot, and neither should change a per-frame query count silently. The cadence this decision states is corrected and extended by `[D-P1-32]` `[fix-up: PHASE_1_REVIEW_4.md F4-1; PHASE_1_REVIEW_5.md V5-1; PHASE_1_REVIEW_6.md V6-2; PHASE_1_REVIEW_7.md V7-2, V7-3; PHASE_1_REVIEW_8.md V8-1, V8-4; PHASE_1_REVIEW_9.md V9-1, V9-6]` |
| D-P1-31 | `StateService` gains `depthTest(boolean)` and `fog(FogState)`; **colour mask and face culling stay out**, each with a deferred-table row naming who would request it; `DrawService.fullscreenQuad()` establishes no state | RESEARCH.md §4.4's composite/final block ("identity ortho, fog/depth/blend disabled") is **v0.1**, and `depthMask` is depth *writes*, a different bit of state — so two of its four elements were unexpressible. The other two are deliberate: the facade has no matrix-state verb at all, and colour mask belongs to the anaglyph-aware final `DESIGN.md` assigns to Phase 7 `[fix-up: PHASE_1_REVIEW_4.md F3-3]` |
| D-P1-32 | **Rung-2 attribution is a property of the caller's drain window**, not of a per-call return status and not of a developer flag. A window holding exactly one mutating **facade** call yields a record naming that call; a window holding several yields **at most one record per driver error flag**, carrying `subjectLabel = "(batched, N calls)"`. Phase 6's rung-2 protocol is therefore: drain, upload the program's uniform set, drain — and **only if that second drain is non-empty**, re-upload the set draining between uploads, so each window holds one call and each record names one uniform. **Three preconditions of that replay are contract** (round seven): the re-upload uses the values **already computed for this sweep** and never re-evaluates the providers; a replay that reproduces nothing is **unattributable** and falls to §6's 3→4 row rather than no-op'ing — **for either of two reasons, and the second is what four other sites lean on**: `OUT_OF_MEMORY` need not recur, *and* the error may never have been ours, since the elision bit tracks *facade* calls while the GL flag is per-context; and a record can only name a uniform because the backend retains the name from `locate` (`[D-P1-34]`) | GL sets an error flag to the *first* error and records no further error in it until `glGetError` clears it, so a batched sweep in which five uniforms fail yields one record — `[D-P1-30]`'s "the cheap mode still cannot lose an error" was factually wrong about GL and is deleted. Making rung 2 debug-mode-only was the alternative and was **rejected**: `DESIGN.md` puts per-uniform GL-error isolation in Phase 6's **v0.1** scope-in, so a shipping build whose only fallback is "disable the whole set" would degrade harder than rung 3. The attributed replay does not contradict §7 — the clean sweep still costs exactly one `glGetError`, **which is true of the two-drain protocol because `[D-P1-30]`'s backend elides a drain with no mutating *facade* call behind it**, not because the protocol drains once — and the per-upload cadence is entered only on a frame that is already about to disable a uniform, once. **The exception the elision creates is stated rather than left implicit:** a *foreign* error makes the trailing drain non-empty with nothing of ours having failed, so a recurring one re-enters the replay every frame, reproduces nothing, disables nothing, and costs a re-upload of the ~90-uniform set per program set (§4.7.4, §7). That cost is created by `[D-P1-30]`'s elision and does not appear in the ledger the elision was kept against; recording it is this document's job, re-weighing the decision on it is not. The re-upload-cached-values rule rests on **idempotence alone**: `glUniform*` is idempotent on the bound program, so the replay changes only which drain window each upload lands in. Round seven's supporting claim — that the halflife-smoothed providers advance *per sample*, so a second evaluation would double-advance them — is **deleted** (round eight, V8-3): no cited source states it, App D gives the values only, App D's cadence note and RESEARCH.md §4.2 make a "refresh" an *upload*, RESEARCH.md §4.4 places the sampling at frame begin, and `DESIGN.md` assigns the **time-corrected** smoothing formula to Phase 6's own *Scope — in* — so §5 asserting a property of those providers was a call this phase does not own (§G1.1: flag, do not decide) `[fix-up: PHASE_1_REVIEW_5.md V5-1; PHASE_1_REVIEW_6.md V6-2, V6-3; PHASE_1_REVIEW_7.md V7-2, V7-3, V7-4; PHASE_1_REVIEW_8.md V8-3; PHASE_1_REVIEW_9.md V9-1, V9-2, V9-5]` |
| D-P1-33 | `DrawService.fullscreenQuadInstanced(int)` is **deleted**; `const int countInstances = N` **on a composite/deferred program** is served by a caller-side loop over `fullscreenQuad()` with `UniformService.upload(instanceIdLoc, i)` between copies. The composite scope is stated rather than assumed, and is cited to RESEARCH.md **§4.4** | `instanceId` is an `int` **uniform** (RESEARCH.md App D.4), not `gl_InstanceID` — GLSL 120 has neither (RESEARCH.md §3.5) — so one instanced draw cannot vary it per copy and the verb could not express the directive it appeared to serve. It carried no semantics, no §3 row, no §5 mention, no §9 tag and no checklist item, in a facade whose own rule is that silent additions are not cheap. The absence is now stated in §4.7.4's absent-verbs table and the loop is mapped in §3, so the deletion is not silent either. **The scope qualifier is round seven's correction:** the mapping was written as if it covered the whole directive, but §3.2, App A.3 and App D.4 — the three sources it cited — place `countInstances` in *Vertex Shader Configuration* with no program restriction, and `instanceId` in the **common** uniform block. Only §4.4 restricts the loop to composites, and it was not cited. The row is now scoped and §4.4 is cited; the non-composite half is `[D-P1-35]`'s `[fix-up: PHASE_1_REVIEW_5.md V5-2; PHASE_1_REVIEW_6.md V6-4; PHASE_1_REVIEW_7.md V7-1]` |
| D-P1-34 | **Backend obligation:** a backend's `UniformLocation` implementation **retains the name passed to `UniformService.locate(program, name)`**, so `GLError.subjectLabel` can carry it. Stated as prose, like `[D-P1-29]`, because no test can catch it; **no signature changes** — `UniformLocation` still exposes only `isAbsent()` (§4.7.3) | `GLError`'s javadoc promises `subjectLabel` is "the debug label of the handle **or the uniform name** involved", and rung 2's whole value is that a record names one uniform. Handles get their label from `create(String debugLabel)`; a location has no such parameter, so the only place the name ever exists is the argument to `locate`. A backend *may* discard it and still satisfy every signature in §4.7.4 — and rung-2 attribution would then be worthless while every test still passed. The facade cannot express the requirement in a type without making `UniformLocation` carry a string it has no other use for, so the obligation is written down instead, and §12 item 22's review hook checks it `[fix-up: PHASE_1_REVIEW_7.md V7-4]` |
| D-P1-35 | **`countInstances` outside the composite passes has named owners and no Phase 1 design element.** Phase 3 detects the directive in its `const`-scan; Phase 4 carries it as the per-slot instance count; **Phase 7** owns the gbuffers/shadow re-render itself. The facade gains nothing either way | The directive is a *vertex-stage* opt-in with no program restriction in any source this document cites (§3.2, App A.3), `instanceId` is in the **common** uniform block, and RESEARCH.md §4.2 carries "instance count" on all 43 slots — so restricting it to composites, as `[D-P1-33]`'s mapping silently did, is unsupported by that provenance. §4.4 restricts only the *observed loop*. For a gbuffers program "the geometry" is vanilla terrain or entity geometry drawn by Minecraft's own calls through Phase 7's hooks, which never reach the facade, so no verb here can satisfy it and inventing one would be Phase 7's design made by the wrong session. Recorded as an **open case handed onward** (§11.4) rather than designed, because RESEARCH.md documents no non-composite instancing loop and this session has no behavior to be faithful to. **Round eight (V8-2) closed the other half against the same document:** the *composite* loop had been attributed to Phase 5, but `DESIGN.md` names it in **Phase 7**'s *Scope — in* under composite/final execution — and, more explicitly still, in **Phase 4**'s: *"`countInstances` exposure to the pass executor (**execution is Phase 7, tag v0.5**)"*, the citation round nine (V9-8) found unused and this revision adopts, together with the **`[v0.5]`** milestone it carries. So both halves of the directive now land on Phase 7, and §5.2's non-verbs row no longer names two phases for **two halves of** one directive. The two cases remain distinct in kind — the composite loop is **assigned** by `DESIGN.md`, the gbuffers/shadow re-render is **open** `[fix-up: PHASE_1_REVIEW_7.md V7-1; PHASE_1_REVIEW_8.md V8-2; PHASE_1_REVIEW_9.md V9-8]` |

### 11.2 D-1..D-10 disposition

The Doc gate requires every binding decision to be either satisfied by this phase or explicitly
deferred with its owner named.

| ID | Decision (short form) | Disposition |
|---|---|---|
| **D-1** | Cleanroom-exclusive | **Satisfied.** Unimined's `cleanroom { }` loader block (no `forge`/`fabric` block), `ModType: CRL` manifest, no compatibility shim for stock Forge, and no abstraction layer pretending otherwise. §1.2 of RESEARCH.md keeps a later port *possible* via the seam without making it a goal — which is exactly what §4.3's C-1 delivers as a side effect. |
| **D-2** | Shaders only; written non-goals list | **Satisfied structurally.** No package in §2.1 corresponds to any §1.2 non-goal: there is no perf package, no MCPatcher-feature package, no telemetry, no installer, no server package. The layout makes scope creep visible as a new top-level package rather than a quiet addition. |
| **D-3** | Target = the fixed pack-compatibility matrix (App G), not "Iris parity" | **Deferred → Phase 2.** The matrix is the definition of done and the tiers T0–T3 are Phase 2's to define (§G6). Phase 1 contributes the `:conformance` module the machinery lives in and the fixture policy note (§8.3) that no pack may ever be committed. |
| **D-4** | Stage registry architected for the full modern stage set from day one | **Deferred → Phase 4.** Phase 1 reserves `com.schmaloogium.engine.registry` and nothing more; designing the registry's shape here would be Phase 4's work done by the wrong session. Recorded so Phase 4 knows the package is its own and empty by intent. |
| **D-5** | Mixin-based hooks only; no class replacement; ~25–30 targeted injections | **Satisfied at the wiring level; catalog deferred → Phase 7** (with additions from Phase 10 and Phase 13). §4.5 provides the manifest declaration, three phase-scoped configs, the SRG policy, and refmap handling. `is_coremod=false` (D-P1-10) closes the class-transformation door, and no `@Mixin(remap=…)`-style class-replacement affordance appears anywhere. Zero mixin classes are authored here. |
| **D-6** | Engine-core / loader-glue seam; core headless-testable and GL-abstracted | **Satisfied — this phase's core deliverable.** §4.3 states it as constraints C-1..C-4 and enforces **all four** with tests (§8.1 names one per constraint; C-4's is `SeamConformanceDependencyTest`, §8.2); §4.7 provides the GL abstraction and the headless backend that makes "testable via JUnit alone" true rather than aspirational. §2.2 records *why* it is a requirement (§5.2/OQ-20), per the spec's instruction. |
| **D-7** | GPL-3.0-or-later license (template's MIT-style LICENSE must be replaced) | **Satisfied.** The replacement D-7 asks for has already happened in the repository: `LICENSE` at the root is tracked and carries the verbatim GPL-3.0 text (674 lines; commit `Update LICENSE from MIT to GPL-V3`) `[V:repo]`. What §4.8 adds on top is the rest of the obligation — two-line SPDX headers on every source file, the "or-later" grant stated in `README.md` and `mod_credits` (§4.8.1, `[D-P1-17]`), and `THIRD-PARTY.md` as the D-8 compliance mechanism. §12 item 1 is therefore a verification step, not a file-restoration step. |
| **D-8** | Published docs + OSS source OK; LGPL-3.0 reuse with compliance; two prohibitions | **Satisfied as convention.** §4.8.3 creates `THIRD-PARTY.md` with the per-incorporation entry format (files / upstream / notice / modifications) and carries both standing prohibitions at its head: never copy from glsl-transformer (AGPL-3.0), and the OptiFine decompile is behavioral-observation-only. The mechanism exists before the first incorporation, which is the only time it can be built cheaply. |
| **D-9** | Compatibility-profile GL baseline; no core-profile rewrite | **Deferred → Phases 5+/7 for policy; enabled here.** Phase 1 owns no GL policy (explicitly Scope-out). What it does is make the constraint expressible and testable: the facade contains no core-profile-only entry point, `UniformService` has no UBO method at all (so the pack contract's prohibition cannot be violated), `DrawService.fullscreenQuad()` leaves the `GL_QUADS`-vs-triangle-strip choice to the backend, and `GLCapabilityProfile` makes every capability gate assertable headlessly. |
| **D-10** | Conformance harness from week one | **Deferred → Phase 2; unblocked here.** §G6 defines D-10's "week one" as the runnable-before-renderer subset: fixture downloader, preprocessor golden runs, capability-profile replay. Phase 1 supplies two of the three prerequisites — the `:conformance` module with JUnit wiring, and the capability-profile record/replay machinery (§4.7.5) plus its serialization format (§4.7.2). The third (the downloader) and all harness content are Phase 2's. |

### 11.3 Input contradictions, defects, and inherited values found

Reported, not smoothed over (§G1.1).

**Contradictions between inputs.**

1. **Extension set attributed to RESEARCH.md §4.1 by DESIGN.md, but not present there.** Detailed
   with its ruling in §3.1. Included, tagged `[A]` rather than `[V:observed]`, because RESEARCH.md §3.5's
   `MC_<GL_extension>` macros independently require it.

**Defects found in the template `[V:template]`** — all pre-existing, none introduced by this design:

2. **No `modImplementation` configuration exists.** The template's README and `dependencies.gradle`
   comments both instruct you to use `modImplementation` — "You **MUST** add mods by using
   `modImplementation` or `modRuntimeOnly`" — but only `modCompileOnly` and `modRuntimeOnly` are
   declared, and only those two are passed to Unimined's `mods { remap(...) }` block. Adding a mod
   dependency at both compile and runtime today requires declaring it twice. Phase 12 (ModularUI) is
   the first phase likely to hit this; the fix is to declare the configuration and add it to the
   remap list.
3. **`extra_jvm_args` parsing is broken.** `extraArgs.split { "\\s+" }` invokes Groovy's
   `CharSequence.split(Closure)` — which *partitions* into matching/non-matching lists — not
   `String.split(String regex)`. Dead code today because the property is empty; it will produce
   garbage `jvmArgs` the moment anyone sets it. Fix: `extraArgs.trim().split(/\s+/).toList()`.
4. **`gradle/scripts/extra.gradle`'s comment is false.** It claims "Helper methods (assertProperty,
   assertSubProperties, setDefaultProperty) are defined directly in build.gradle's script scope and
   exported via ext." No such methods exist; `ext` contains only `access_transformer_locations`.
   Either implement them or delete the comment — a comment describing an API that does not exist is
   worse than none.
5. **`publish_to_local_maven` is documented but never read** by any script. Either wire it or remove
   it from `gradle.properties`.
6. **The Unimined access-transformer path hardcodes `rootProject.projectDir`.** Harmless in a
   single-project template, fatal under the split. Fixed pre-emptively in §4.4.3 even though the
   branch is inert.
7. **All three CI workflows hardcode root-relative `build/libs`.** Fixed in §4.11.

**Inherited values worth a later spot check.**

8. **`compatibilityLevel: "JAVA_8"`** in the mixin configs is taken verbatim from the template
   snapshot while the project's source level is Java 25. The field constrains mixin-class bytecode,
   not project source, so this is very likely correct — but it is a value we inherited rather than
   derived, and the first mixin using a language feature that survives to bytecode above Java 8 is
   the moment to confirm it. Flagged for Phase 7.
9. **Unimined refmap generation is unexercised in this checkout** — the `main` branch has never had a
   mixin config. The first config to land should be verified to produce a refmap in the built jar
   before hook work proceeds (§12 item 33).

*Items 8 and 9 are duties, not observations, so §5.3's mixin row carries both to Phase 7 — a Phase 7
session working from §5 alone does not have to reach §11 to find them.*

**Unverified claims (`[U]`) this document makes and cannot source.** RESEARCH.md §0.2 requires every
`[U]` to carry an open-question row or be upgraded; this is that row.

10. **§4.2.3's Gradle script-plugin mechanism is `[U]`.** The claims are that an `apply from:` script
    plugin's classes are unresolvable from the applying script because that script is compiled in full
    before it executes; that `buildSrc`'s `main` output is on every project's buildscript classpath
    unconditionally; that an included build's precompiled plugin requires both a `settings.gradle`
    wiring and an `apply`; and that no `ext` indirection preserves a literal `new X(...)` call site.
    They originate in this session's reasoning about Gradle. §0.1's input table lists no Gradle
    documentation, §0.3 scopes this document's web use to the OQ-2 re-pin, and no `[V:web]` tag or
    §12.5-equivalent source exists for any of it — so `[V:template]` at §4.2.3 covers the three
    `apply from:` **file** facts and nothing else. **Settling it is scheduled, not deferred:** §12 item
    4b's hook (`./gradlew :engine:test --dry-run` configures without an unresolved-class error) runs
    the experiment, and its failure mode is a loud configuration error rather than a silent wrong
    result. If it fails, the fallback is already named in §4.2.3 (the included-build precompiled
    plugin) and the third form is already ruled out. **Recorded rather than asserted harder because
    the alternative is the exact defect round seven caught at `[D-P1-30]`** — a correction about a
    mechanism resting on an unsourced claim about that mechanism, which is what V9-3 found here.
    Whoever runs item 4b upgrades this row's tag or replaces the design; a later fix-up carries the
    outcome back to §4.2.3.

### 11.4 Items handed onward

**To Phase 2** — the `:conformance` module, its JUnit wiring, and the CI `conformance` job stub are
empty slots by intent, not omissions. The `GLCapabilityProfile` text format (§4.7.2) and the
`GLCallLog.render()` stability guarantee (§4.7.5) are the two contracts your golden-file workflow
should build on. Phase 1 supplies no fixture set and no answer to OQ-10.

**To Phase 3** — `schmaloogium.debug.saveSources` is reserved for you: Phase 1 fixes the flag's name
at v0.1 and implements no behavior behind it, so the dump arrives with your preprocessor.
`GLCapabilityProfile` is the whole GL-side input to RESEARCH.md §3.5's standard macro header, not
only `extensions()` for `MC_<GL_extension>`: `MC_GL_VERSION`, `MC_GLSL_VERSION`, `MC_GL_VENDOR_*` and
`MC_GL_RENDERER_*` all read off the profile (§3's macro-header row). One assumption of the facade is
yours to honor or contest, and §5.2 carries it as a row so you need not find it here: §4.7.4 exposes
**no** pre-link program-parameter verb because RESEARCH.md §6.2's internal translation makes the
legacy `GL_ARB_geometry_shader4` + `maxVerticesOut` form a source-level rewrite in your front-end
(the *strategy* for that translation is specified by Phase 4's spec, not by this document). If the
rewrite turns out to belong at the GL level instead, flag it in your §5 and the verb is added
additively. Separately, `DESIGN.md` makes the **engine-flag ownership map** yours — §4.7.4's
face-culling row defers to it rather than pre-empting it. One small thing your `const`-directive scan
is named for by this document: `countInstances` is a *vertex-stage* directive that a **gbuffers**
program may carry, and detecting it is the first step in a case Phase 1 deliberately leaves open
(`[D-P1-35]`, §11.4's Phase 7 entry).

**To Phase 7** — the three mixin configs exist and are empty. Verify refmap generation before
building the hook catalog on top (§11.3 item 9). `compatibilityLevel` is worth your spot check
(item 8). The `BailRegistry` bail hook is wired at bootstrap and awaits your frame-driver
integration.

**The composite `countInstances` loop is yours, it is assigned rather than open, and it is `[v0.5]`.**
`DESIGN.md` names the `countInstances` instancing loop in your *Scope — in*, part (a), under
**Composite/final execution**, tagged `[v0.5]` there — and settles both owner and milestone a second
time, more explicitly, in **Phase 4's** *Scope — in*: *"`countInstances` exposure to the pass executor
(**execution is Phase 7, tag v0.5**)"*. **The milestone matters here because your own is v0.1 exit**,
so this is architected-now / implemented-at-v0.5 work in §G0.3's sense, not v0.1 assembly. §3's first
row maps it to a caller-side loop over `DrawService.fullscreenQuad()` with an `instanceId` upload
between copies, because `instanceId` is an `int` **uniform** and GLSL 120 has no `gl_InstanceID`, so no
instanced verb could express it (`[D-P1-33]`). The upload itself is Phase 6's entry point — `DESIGN.md`
carries `instanceId` among its per-draw dynamics, invoked at your hooks. Phase 5 owns the buffer estate
the N draws run inside; the loop does not change its read/write/flip law. Until round eight this
document attributed the loop to Phase 5; that was wrong and is corrected here. Distinguish it from the
*open* case two paragraphs below: the composite loop is work `DESIGN.md` has already given you, at a
milestone it has already set, and the gbuffers/shadow re-render is neither.

**One residue of the GL-error design is yours to place, and it is a placement rather than a
design.** The backend elides a drain that has seen no mutating **facade** call since the previous
one (`[D-P1-30]`), which is what makes Phase 6's two-drain rung-2 protocol cost one query per clean
sweep. That bit tracks *facade* calls; the GL error flag is **per-context**, and vanilla's own draws
— the ones your hooks drive — never reach the facade. So a foreign GL error occurring between two of
Phase 6's sweeps survives into a window Phase 6 will read (§4.7.4). The outcome is contained:
`[D-P1-32]`'s replay reproduces nothing, the sweep is unattributable, and §6's 3→4 row logs it and
keeps the program running — no innocent uniform is disabled. What is *not* contained is the noise —
nor its cost, which is a replay of a program's whole ~90-uniform set (RESEARCH.md §4.2) for as long as
the foreign error recurs (§4.7.4, §7).

**Two remedies exist, and this is the one that is yours.** §4.7.4 names both and prefers neither on
soundness. Dropping the elision would bound the window against all GL and is rejected there on
**cost**; a guard inside the facade is unavailable on **mechanism**, since the facade cannot observe
non-facade GL. What is left, and what lies outside this facade's reach rather than inside it, is
**one `drainErrors()` at a frame-driver-defined point** — roughly one extra query per frame against a
factor of two at every program switch (§7). **Take its two limits with it, because they change where
it is worth placing.** *(1)* It bounds the gap that spans the **frame boundary** and no other. Foreign
GL is interleaved with Phase 6's sweeps throughout the frame — `DESIGN.md` §G3.2 puts the gbuffers
dispatch mid-frame and refreshes uniforms at every program switch, and RESEARCH.md §4.4 separates each
element of the gbuffers chain with vanilla draw work — so a drain placed once leaves every *interior*
gap exactly as it was. It is a reduction in leakage, not an elimination of it, and this document does
not argue the size of the reduction because it has no evidence for one. *(2)* "Unconditional" is the
caller's word, not the backend's: `drainErrors()` takes no argument and the elision is contract
(`[D-P1-30]`, §12 item 22), so in precisely the configuration this remedy targets — foreign GL arriving
with no mutating facade call since the previous drain — the drain **elides, clears no flag, and the
error survives anyway**. In practice surrounding facade traffic will often have armed the bit, but by
accident of that traffic rather than by construction. If you want the guarantee rather than the
tendency, the request is a verb that forces the query, additive in your own §5 by the route §4.7.4
already names — Phase 1 declines to add it unasked. Whether any of this is worth placing, and where in
the frame it goes, is yours; Phase 1 supplies the verb and deliberately does not design the placement.

**One open case is yours, and it is open rather than designed** (`[D-P1-35]`, §3's second row).
`const int countInstances = N` is a **vertex-stage** directive with no program restriction in
RESEARCH.md §3.2 or App A.3, and `instanceId` sits in shaders.txt's **common** uniform block, so a
**gbuffers or shadow** program may legitimately carry it. On such a program "the geometry" is vanilla
terrain or entity geometry drawn by Minecraft's own draw calls — through your hooks, never through
`engine.gl` — so §3's composite mapping (a caller-side loop over `DrawService.fullscreenQuad()`)
cannot serve it and no facade verb would. What Phase 1 supplies is the honest boundary rather than a
design: **Phase 3** detects the directive in its `const`-scan, **Phase 4** carries it as the per-slot
instance count RESEARCH.md §4.2 lists for all 43 slots, and the re-render itself would be yours.
Whether it needs building at all is genuinely open — RESEARCH.md §4.4 observes the instancing loop
**only** in the composite-pass line, so there is no reference behavior here to be faithful to. If
your hook catalog finds real packs relying on it, the work is yours and the request against this
document (if any) is additive.

**To Phase 10** — the bail registry mechanism is complete and has no registered checks. The
`Degrade` verdict exists specifically so that if OQ-5 resolves toward "integrate" rather than "bail",
you are not forced to widen a mechanism you were told to reuse. The `SchmaloogiumMixinPlugin` slot on
the MOD-phase config is the strongest veto point available (§4.10) — a vetoed mixin never applies, so
there is no partial instrumentation to unwind.

**To Phase 13** — the transfer verbs you need exist and carry no policy:
`TextureService.create`/`allocate`/`setParameters`/`upload`/`bindToUnit`/`generateMipmap`, with
`TextureData` carrying a JDK `ByteBuffer` and a `PixelLayout` from the same vocabulary `TextureSpec`
uses. `schmaloogium.textures` is your log channel. Two things are **yours to contest, not
inherited**: §4.7.4 declines a `glGetTexImage`-shaped texture readback on the expectation that your
companion-atlas construction builds data rather than reading it back — that is a guess about your
design, and if it is wrong the verb is an additive request in your §5; and the fixed unit map is
Phase 5/6 policy, so `bindToUnit` takes the number you are given rather than choosing one.

**To Phase 12** — the `SHADER_GUI` diagnostic channel and its per-pack error store exist; your screen
is the sink. Your ModularUI arrangement decision carries the §4.8.4 obligation (b). The
`modImplementation` defect (§11.3 item 2) will likely bite you first. §5.3 collects all three pieces
(pin posture, arrangements and their obligations, the missing configuration) as one exposed row, since
your spec names "ModularUI dependency mechanics" as a required input.

**To Phase 14** — `DebugService` exists as an interface with call sites from v0.1; its implementation
and `schmaloogium.debug.glLabels` are yours. Facade extensions must be additive (your spec says so);
§4.7.4's structure is designed for that, and its closing table names the two RESEARCH.md §6.2
modernizations that are yours to request: PBO + fence-sync asynchronous readback (v0.1 ships the
faithful synchronous `readDepthPixel`) and any general colour readback that path needs. Your
off-thread compile/upload design is the **one sanctioned exception** to §7's render-thread rule, and
§7 now says so rather than forbidding it by omission — the facade's signatures assume nothing either
way, and the mandatory synchronous fallback is part of what makes that safe.

**To G8/S5** — §10.3's backend-swap drill is the instrument for judging whether the seam held. Its
fallback (submitted `RenderPassDescription`s) is the pre-designed coarsening if it did not.

**Candidate, not adopted:** a CI license-header lint. Useful, cheap, and unasked-for. Recorded here
rather than added.

### 11.5 Requested upstream changes

**To RESEARCH.md** — two, both minor and both for the maintainer of that document, not for a phase
session:

1. §11's OQ-2 row and §5.1's loader-pin row both say "current is 0.6.6-alpha". Re-verified true on
   2026-07-24. When the status column is next updated, the re-pin procedure now lives in
   PHASE_1_DOC §4.2.6 / §10.1 and `PINS.md` is the ledger — worth a pointer so the standing item has
   an owner-of-record.
2. §11's OQ-12 row can move to resolved-by-note once §4.8.4 clears review (§10.2's success criterion).

**To DESIGN.md** — two:

3. Phase 1's scope line attributes the "extension set" to RESEARCH.md §4.1's probe set; §4.1 lists
   four probes and does not include it (§3.1). The requirement is real but sourced from RESEARCH.md §3.5. A
   half-sentence correction would prevent a future reader from looking for it in the wrong place.
4. **§G2.4's degradation ladder has no rung for "a single *feature's* GL call fails."** Rungs 1 and 2
   are both about uniforms, rung 3 is a program, rung 4 is a capability gate at init — none of them
   covers a pack feature whose GL call fails at runtime and which the owning phase should switch off
   while the program keeps running. §6 records it as an **explicitly unnumbered row** rather than
   mislabel it as rung 1, which is the honest local answer but leaves the gap upstream where it was
   found. Proposed: a rung between 2 and 3, or an explicit note in §G2.4 that the case exists and is
   handled at the feature owner's discretion. Without it, Phases 5, 6 and 13 will each meet the case
   and each independently re-derive the unnumbered category or mislabel it — sibling drift of exactly
   the kind §G5.3 describes the integration review as structurally unable to catch. §G2.4 also
   requires every phase doc's §6 to "map this ladder"; a phase doc mapping a ladder that does not
   cover its subsystem should not have to invent the missing step privately
   `[fix-up: PHASE_1_REVIEW_7.md V7-7]`.

Per §G1.1 neither document is modified by this session.

---

## 12. Implementation checklist

Ordered so that each item is independently actionable and the Impl gate — *"project builds empty
modules + passes an architecture test proving `:engine` has no MC/loader/mixin/LWJGL classpath; CI
green"* — is reached at item 15, with the rest completing the phase's scope.

Tags: `[v0.1]` etc. per §G4.3. Test hooks name the check that proves the item.

### Structure and build

| # | Item | Tag | Test hook |
|---|---|---|---|
| 1 | **Verify** `LICENSE` is the verbatim GPL-3.0 text (it already is, and is tracked). The `README.md` / `mod_credits` half is **authoring, not verification** — `README.md` is still the untouched template (`# CleanroomModTemplate`), and item 28 is where it is written; do that item's README work here or accept that this hook stays red until item 28 | `v0.1` | `LICENSE` present at repo root and unmodified; the GPL-3.0-**or-later** statement present in `README.md` and `mod_credits` **once item 28 has run** |
| 2 | Update `gradle.properties` per §4.4.1 (ids, package, version, `use_access_transformer=false`, `enable_lwjglx=false`, `cleanroom_loader_version`, `mixin_configs`) | `v0.1` | `./gradlew properties` shows the expected values |
| 3 | Rewrite `settings.gradle`: literal `rootProject.name`, three `include`s | `v0.1` | `./gradlew projects` lists `:engine`, `:mod`, `:conformance` |
| 4 | Rewrite root `build.gradle` as the §4.2.2 aggregator (plugins `apply false`, `subprojects` toolchain/encoding/JUnit, `allprojects` group/version, idea-ext) | `v0.1` | `./gradlew help` succeeds; root produces no jar |
| 4b | Stand up **`buildSrc`** (or an included build carrying a **precompiled** script plugin) and put `SeamClasspathArguments` in it — the `CommandLineArgumentProvider` items 5, 6 and 7 all instantiate (§4.2.3). Numbered `4b` on the same reasoning as `14b`: it comes **before** the three items that depend on it without renumbering them. Put it in the **default package**, or add an `import` to all three blocks — they print `new SeamClasspathArguments(...)` bare. Two forms are **not** options, for two *different* reasons: declaring it inline means three copies, because a script-scoped class is invisible outside its own script; and a plain `apply from:` script under `gradle/` (what the template's three scripts are) does not export the class to the applying build script **at the time that script is compiled**, so `new SeamClasspathArguments(...)` will not resolve and no `ext` indirection preserves that call site (§4.2.3, whose mechanism claim is `[U]` — §11.3 item 10; **this hook is the experiment that settles it**) | `v0.1` | All three `build.gradle` files resolve the same type; `./gradlew :engine:test --dry-run` configures without an unresolved-class error |
| 5 | Create `engine/build.gradle` per §4.2.3 (no Unimined, no Blossom, mavenCentral only, `java-test-fixtures`, ASM `9.10.1` at test scope, classpath system properties **through item 4b's provider**) + the empty `src/testFixtures/resources/profiles/` directory | `v0.1` | `./gradlew :engine:compileJava` and `:engine:compileTestFixturesJava` succeed |
| 6 | Create `conformance/build.gradle` per §4.2.4a: **`repositories { mavenCentral() }`** (without it the module cannot resolve JUnit and item 15 fails), `java-library`, JUnit, `implementation project(':engine')`, `testImplementation testFixtures(project(':engine'))`, test-scope ASM, no `:mod`, classpath system properties for item 14b (through item 4b's provider) | `v0.1` | `./gradlew :conformance:compileTestJava` — **the test configuration, not `compileJava`**, since a missing repository fails only there; C-4 proven by item 14b |
| 7 | Create `mod/build.gradle`: move the Unimined block (loader from property, AT path project-relative), Blossom with the `package` override, `dependencies.gradle` + `publishing.gradle` applied here, `implementation project(':engine')`, `testImplementation testFixtures(project(':engine'))`, **test-scope ASM `9.10.1`, with Unimined's inherited `asm-debug-all` 5.x *excluded* at `testImplementation` scope so both `testCompileClasspath` and `testRuntimeClasspath` are covered** (§4.2.4, `[D-P1-3]`), **the three `schmaloogium.test.*` system properties C-2/C-3 read** (through item 4b's provider), and the `:engine` class merge into `jar`. **Express the merge through a dependency-derived form** (a consumable configuration, or `project(':engine').tasks.named('jar')`) rather than `project(':engine').sourceSets.main.output` — the latter is cross-project model access at configuration time (§4.2.5) | `v0.1` | `./gradlew :mod:jar` produces a jar containing `com/schmaloogium/engine/**`, with no cross-project configuration-time warning; `./gradlew :mod:dependencyInsight --configuration testCompileClasspath --dependency org.ow2.asm` and the same for `testRuntimeClasspath` each show `asm:9.10.1` and **no `asm-debug-all`** |
| 8 | Move sources: `mod/src/main/java/com/schmaloogium/mod/core/{SchmaloogiumMod,proxy/*}`, `mod/src/main/java-templates/com/schmaloogium/Reference.java`, `mod/src/main/resource-templates/{mcmod.info,pack.mcmeta}`. Drop the client-only call from the shared `@Mod` class | `v0.1` | `./gradlew :mod:build`; generated `Reference` is at `com.schmaloogium.Reference` |
| 9 | Delete `src/main/resources/modid_at.cfg` and the now-empty root `src/` tree | `v0.1` | No `FMLAT` attribute in the built manifest |
| 10 | Retarget the idea-ext run configurations at `:mod:runClient` / `:mod:runServer`; per-module `moduleJavacAdditionalOptions` keys | `v0.1` | IDEA sync produces working run configs |

### The seam

| # | Item | Tag | Test hook |
|---|---|---|---|
| 11 | `SeamClasspathTest` in `:engine` — constraint C-1, classpath half | `v0.1` | Passes; fails informatively when a forbidden coordinate is added deliberately |
| 12 | `SeamBytecodeTest` in `:engine` — constraint C-1, bytecode half. **The Impl gate's test** | `v0.1` | Passes; fails when a `net.minecraft.*` reference is added deliberately |
| 13 | `SeamInternalsTest` in `:mod` — constraint C-2 | `v0.1` | Passes; fails on a deliberate `.internal.` reference |
| 14 | `SeamLwjglConfinementTest` in `:mod` — constraint C-3 | `v0.1` | Passes; fails on an `org.lwjgl` reference outside `mod.glue` |
| 14b | `SeamConformanceDependencyTest` in `:conformance` — constraint C-4 (§8.2). Numbered `14b` deliberately: items 15+ are referenced by number elsewhere, including the Impl gate | `v0.1` | Passes; fails when `project(':mod')` is added to `:conformance` deliberately |
| 15 | **Impl gate reached**: `./gradlew build` succeeds across all three modules and `./gradlew :engine:test` passes | `v0.1` | Both commands green locally |

### The GL facade

| # | Item | Tag | Test hook |
|---|---|---|---|
| 16 | `GLCapabilityProfile` record + `atLeast`/`hasExtension`/`supportsMipmapGeneration`, with defensive copy of `extensions` | `v0.1` | `GLCapabilityProfileDerivationTest` |
| 17 | `GLCapabilityProfile.parse`/`write` in the §4.7.2 sorted text form; first fixture committed under `engine/src/testFixtures/resources/profiles/` | `v0.1` | `GLCapabilityProfileSerializationTest` — round-trip identity, deterministic output, and the fixture loads from the test-fixtures classpath |
| 18 | Handle types (`GLHandle` + the **four** sealed sub-interfaces — no `RenderbufferHandle`, §4.7.3 — plus `UniformLocation.isAbsent()`) and the handle-lifetime rule in their javadoc | `v0.1` | Compiles; used by item 19's signatures; the `permits` clause names exactly the four declared types |
| 19 | The seven service interfaces + `GLDevice` + result/value types (`CompileResult`, `LinkResult`, `ValidateResult`, `TextureSpec`, `TextureData`, `TextureRegion`, `PixelLayout`, `BlendState`, `FogState`, `StateAspect`, `StateSnapshot`, `BlitSpec`, `GLError`, `GLErrorKind`, …), **including every pixel-transfer verb** (`readDepthPixel`, `copyDepthToTexture`, `TextureService.upload`, the ivec2 **and ivec4** uploads), the `depthTest`/`fog` state verbs, and `GLDevice.drainErrors()` | `v0.1` | Compiles with no GL constant and no `int` object name in any signature, and no LWJGL buffer type on `TextureData` — verified by review against §4.7.4; `DrawService` declares **`fullscreenQuad()` only**, `fullscreenQuadInstanced` having been deleted per `[D-P1-33]` |
| 20 | `RecordingGLDevice` (**both constructors** — the two-arg form and the log-supplying three-arg form, §4.7.5), `GLCall`, `GLCallLog` (incl. `bounded(capacity)` / `unbounded()` / `droppedCallCount()`), `ScriptedResponses` (incl. `depthPixel`, `validateFails`, `glError`, and summary-not-content logging of uploads) | `v0.1` | `RecordingGLDeviceTest` — ordering, distinct never-reused handles, scripted failures incl. a scripted depth value and a scripted `GLError`, oldest-first discard on a bounded log, stable `render()` with an upload in the log |
| 21 | `ReplayAssertions` incl. `bindsBalanced()`, `noLeakedObjects()`, **`noUseAfterDelete()`**, `drawBuffersWere()` | `v0.1` | `ReplayAssertionsTest` — each assertion passes on conforming and fails informatively on violating logs |
| 22 | `Lwjgl3GLDevice` + the seven service implementations in `mod.glue`, **issuing every `GlStateManager`-cached verb through `GlStateManager`** (`[D-P1-29]`) and implementing the stated `glGetError` cadence behind `drainErrors()` — **per facade call under a debug context or `-Dschmaloogium.debug.recordGL`/`glLabels` (those two flags only); otherwise once per drain, where a drain is a `glGetError` *loop* terminating on `GL_NO_ERROR` and is skipped entirely when no mutating **facade** call has occurred since the previous drain, so that a window holding one mutating **facade** call names that call and a window holding many carries `subjectLabel = "(batched, N calls)"`** (`[D-P1-30]`, `[D-P1-32]`). **The backend also retains the name passed to `locate(program, name)`** on its `UniformLocation` implementation, so a record can carry a uniform name at all (`[D-P1-34]`) | `v0.1` | `SeamLwjglConfinementTest` (item 14) confines it; a manual `runClient` reaching the main menu; a review pass over the verb list in §4.7.4 confirming no raw-LWJGL state call, that the batched record is emitted **once per window and never claims per-call attribution**, that the drain **loops** rather than querying once, that a drain after a drain issues no query, and that `subjectLabel` carries a real uniform name on a single-call window — no test can catch any of these, which is why they are called out here |
| 23 | `CapabilityProbe` in `mod.glue` + `-Dschmaloogium.debug.dumpCapabilities` | `v0.1` | Running the client with the flag writes a parseable profile that round-trips through item 17 |
| 24 | `-Dschmaloogium.debug.recordGL` decorator wrapping the live device, **with a bounded log** (`GLCallLog.bounded(100_000)` by default, oldest discarded and counted) supplied to the device through `new RecordingGLDevice(profile, responses, log)` (§4.7.5) — the decorator constructs the ring, the device does not — and off unless the flag is set | `v0.1` | Flag produces a `GLCallLog` dump in the same format the tests assert over; a long session does not grow the log without bound, and the dump reports `droppedCallCount()` when it wrapped |

### Conventions

| # | Item | Tag | Test hook |
|---|---|---|---|
| 25 | `Log`/`LogSink`/`Logs` + `LogChannels` constants (§4.9.2) | `v0.1` | `LogChannelTest` — uniqueness, prefix, no-op sink before install |
| 26 | log4j-backed sink installed in `mod.core` at `preInit` | `v0.1` | A `runClient` shows `schmaloogium.boot` lines |
| 27 | `EngineDiagnostic`/`DiagnosticSeverity`/`UserChannel`/`DiagnosticReporter` + `:mod` routing | `v0.1` | `DiagnosticRoutingTest` — including `CHAT`-with-no-player degradation |
| 28 | SPDX headers on every source file; `README.md` license statement; `THIRD-PARTY.md` with both standing prohibitions and no entries | `v0.1` | Manual review; a header lint is a candidate, not adopted (§11.4) |
| 29 | `PINS.md` created with the §4.2.6 table as its first row | `v0.1` | File present; §10.1 step 6 has somewhere to write |

### Mixin wiring

| # | Item | Tag | Test hook |
|---|---|---|---|
| 30 | Three mixin config JSONs per §4.5.2, empty `client`/`mixins`/`server`, and **no `plugin` key** (it arrives with item 37) | `v0.1` | Files present at `mod/src/main/resources/`; `runClient` loads all three configs without error |
| 31 | `MixinConfigs` manifest attribute wired into `:mod`'s `jar` `doFirst` from `mixin_configs` | `v0.1` | `unzip -p` the built jar's `MANIFEST.MF` shows all three, comma-separated |
| 32 | Dev flags on the client run: `mixin.debug.export`, `mixin.checks.interfaces`, gated on `enable_mixin_debug` | `v0.1` | `runClient` writes `.mixin.out/`; `-Penable_mixin_debug=false` suppresses it. **No CI clause:** the flags reach only Unimined's run tasks, which CI never invokes (§4.5.5) |
| 33 | **Verify Unimined refmap generation** with a single throwaway no-op mixin, then remove it (§11.3 item 9) | `v0.1` | A refmap appears in the built jar; **blocks Phase 7 if it does not** |
| 34 | Document `-Dcrl.dev.mixin` in the developer README | `v0.1` | Present |

### Compat and CI

| # | Item | Tag | Test hook |
|---|---|---|---|
| 35 | `CompatCheck`/`CompatVerdict`/`CompatContext`/`BailRegistry` + `CompatEvaluation` | `v0.1` | `BailRegistryTest` — aggregation, throwing-check-is-bail, idempotence |
| 36 | Bail evaluation point 1 (pre-bootstrap) wired, with diagnostic routing and the shaders-off terminal state | `v0.1` | A test check returning `Bail` produces the chat diagnostic and the compat log line |
| 36b | Bail evaluation point **2** (before a vertex-format change) wired at the site Phase 10 creates — listed here so all three §4.10 evaluation points have a checklist home, and tagged `v0.3` because the site does not exist until Phase 10 does. Point **3** is item 37's plugin | `v0.3` | Re-evaluation runs on a pack-triggered vertex-format rebuild; a check flipping to `Bail` between points 1 and 2 is honoured |
| 37 | `SchmaloogiumMixinPlugin` skeleton (returns true for everything) **and** the `plugin` key added to `schmaloogium.mod.mixin.json` in the same change — never one without the other | `v0.3` | Config loads with the plugin declared; reverting only the class would fail config load, which is why they ship together |
| 38 | `build.yml`: named "Seam architecture test" step running **`:engine:test :mod:test`** (C-1 lives in `:engine`; C-2 and C-3 live in `:mod`), named `:conformance:test` step (which runs C-4), artifact glob `**/build/libs/*.jar`, `if: failure()` test-report upload — and **both named steps placed *before* the `./gradlew build` step**, because `build` runs all four seam tests itself through `check`→`test` and would otherwise fail first and abort the job (§4.11, `[D-P1-24]`) | `v0.1` | CI green on a clean commit; a deliberate violation of **each** of C-1, C-2, C-3 and C-4 turns a *named* step red, none of them surfacing only inside `build` |
| 39 | `release.yml` artifacts → `mod/build/libs/*`; `release-to-cf-mr.yml` file globs retargeted | `v0.1` | Dry-run inspection of the resolved paths |
| 40 | `conformance` job stub, `workflow_dispatch`-gated, with the fixture `actions/cache` step | `v0.1` | Workflow parses; job does not run on push |
| 41 | Run §10.3's backend-swap drill **steps 1–3** (public-API read, paper mapping of the seven services, `:engine` class-change count) and record the result in this doc as an addendum (§G4.4: "an addendum note in the owning phase doc") | `v0.1` | Drill produces a per-method expressibility verdict and a class-change count |
| 41b | Run §10.3 **step 4** — write `NullGLDevice` and run `:engine`'s tests against it — at the first milestone where `:engine` holds pass logic behind the facade. Deliberately **not** v0.1: at v0.1 the criterion excludes the only two tests that touch a `GLDevice`, so the step would pass over an empty set (§10.3) | `v0.3` | `NullGLDevice` exists and `:engine`'s tests run against it, with the excepted (recorder-dependent) set a minority of the facade-exercising tests |
| 42 | Run the §10.4 OQ-21 runtime checks and record the result; hand any capability-query divergence to Phase 7's OQ-3 | `v0.1` | Both configurations load; profiles compared |

### Fix template defects found (§11.3)

| # | Item | Tag | Test hook |
|---|---|---|---|
| 43 | Declare a `modImplementation` configuration and add it to Unimined's `mods { remap(...) }` list | `v0.1` | A mod dependency declared once resolves at both compile and runtime |
| 44 | Fix `extra_jvm_args` parsing: `extraArgs.trim().split(/\s+/).toList()` | `v0.1` | Setting the property produces the expected `jvmArgs` |
| 45 | Delete or implement `extra.gradle`'s false helper-method comment; wire or remove `publish_to_local_maven` | `v0.1` | Comment matches reality |

---

*End of PHASE_1_DOC.md. Per §G1.1 the build session stopped here. **Ten** verify sessions have
since run — `PHASE_1_REVIEW.md` through `PHASE_1_REVIEW_10.md`, each returning PASS-WITH-CORRECTIONS —
and **seven** fix-up sessions: the first applied round one's F-1 … F-12 (§0.4); the second applied
rounds two, three and four together, as round four dispositioned them (§0.5); the third applied
rounds **five and six** together (§0.6), round five's fix-up having never run — which is round six's
own headline finding (V6-1) and the reason two rounds are closed in one session; the fourth applied
round **seven**'s V7-1 … V7-8 (§0.7), all eight, none narrowed and none refused; the fifth
applied round **eight**'s five corrections and one of its two notes (§0.8), none refused, with V8-2
applied wider than its fix shape asked and V8-7 left unchanged because round eight ruled it a
correct hand-off rather than a defect; and the sixth applied round **nine**'s six corrections and
**all five** of its notes (§0.9), none refused, with V9-4 applied narrower than its fix shape
suggested — the `scale.<prog>` seam is reported as `DESIGN.md`'s silence rather than given an owner —
and V9-3 reshaped so its Gradle mechanism carries a `[U]` tag and an open-question row (§11.3 item 10)
instead of an unsourced assertion; and the seventh applied round **ten**'s two corrections and **both**
of its notes (§0.10), none refused and none narrowed, taking V10-3's cheaper fix branch on its merits
and recording what that branch leaves. Every finding's disposition is recorded in the review files
under `## Resolutions`, including the four of round three's proposed fixes and the items of rounds
five and six that were deliberately narrowed rather than applied as written, and why. **This fix-up
left §5 unchanged** — the first of the seven to do so. Round ten's two corrections are confined to
§4.7.4 (a per-frame cost ceiling that had dropped its own multiplier) and §6 (the 3→4 unattributable
row, which five sites delegate to and which named only one of the two causes they rely on); its two
notes are confined to §4.7.4's absent-verbs table header and §4.2.3. Each of §5.2's three candidate
rows is named in §0.10 with the reason it needed no edit, and **no service signature was added,
removed or changed** — no correction reaches the signature block at all. So §G1.3's *"re-verify only
if §5 changed"* trigger does not fire, **this fix-up closes the phase**, and under §G1.3's definition
`PHASE_1_DOC.md` is now **verified** — PASS-WITH-CORRECTIONS, all resolutions recorded, no §5 change
outstanding. It is a valid dependency input, and Phase 2, Phase 3 and everything downstream are
unblocked (§G1.3, §G5.3).*
