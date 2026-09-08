# Schmaloogium — Phase 1: Foundation & project architecture — Architecture

---

## 0. Header

**Phase:** 1 — Foundation & project architecture
**Milestone:** v0.1 · **Depends on:** — (Wave 0; this doc feeds every other phase)
**Assigned OQs:** OQ-2, OQ-12, OQ-20 (seam hardness), OQ-21
**Authored:** 2026-07-24 · **Last revised:** 2026-09-07 (§0.28)
**Deliverable:** this document, per DESIGN.md §G9.
**Verifies against:** `docs/design/v2.0-RC2/DESIGN.md` from §0.11 onward; `docs/design/v1.1/DESIGN.md`
through §0.10. **There is no longer one governing revision for the project:** RC2 governs **this
phase** and v1.1 still governs Phase 2, so the revision is resolved **per phase**. By default the
verification harness extracts the explicit governing declaration above from §0 using the validated
`phase-1` manifest in `verification/targets/`; `--design-version` is a verification-only override.
Historical §0 addenda remain records of what each session was told rather than independent current
defaults. §0.12 states the adoption state as of 2026-07-26; §0.1 records what was read from each revision.

*Both dates are stated, and deliberately. The authoring date is what the rest of the document reads
against — §4.1's template facts are read from the checkout on 2026-07-24, §4.2.6's thirteen pin rows
are re-verified 2026-07-24, and `[V:repo]` below is defined as inspection on 2026-07-24 — so a single
later stamp would silently re-date claims to a day on which they were not performed. The revision
date is the most recent of the dates the addenda in §0.4–§0.25 carry, and each addendum states
its own: §0.4–§0.5 are 2026-07-24, §0.6–§0.10 are 2026-07-25, §0.11–§0.14 are all
2026-07-26, §0.15–§0.17 are 2026-07-28, §0.18–§0.19 are 2026-07-29, and §0.20–§0.23 are
2026-08-03; §0.24–§0.25 are 2026-09-07. The
few repository observations the round-eleven and round-twelve fix-ups made are tagged
`[V:repo 2026-07-26]` inline for the same reason the authoring date is kept — §4.1's and §4.2.6's 2026-07-24 reads are not re-dated by a
later session touching a different part of the tree.*

### 0.1 Inputs actually read

| Input | What was read |
|---|---|
| `docs/design/v1.1/DESIGN.md` | **The build session's governing revision, and this document's anchor through §0.10.** All of Part I (§G0–§G10, lines 1–575) and the Phase 1 spec in Part II (lines 585–658). Phase titles only from §G5.1 for other phases. Superseded as the anchor by the row below at the round-eleven fix-up; retained rather than replaced because §0.4–§0.10 were written against it and cite it by line, and rewriting a historical record to point at a document it never read would be a worse defect than a stale coordinate (§0.10's head note). |
| `docs/design/v2.0-RC2/DESIGN.md` | **This document's governing revision from §0.11 onward** — read by the round-eleven fix-up. All of Part I, **lines 76–945**: §G0–§G10 as before, plus **§G11** (ll. 833–943), which is new in REV1 and had been read by no prior session of this phase. The Phase 1 spec in Part II, **lines 957–1067**, in full, including its *Required inputs* (l. 1042) and its *Doc gate* (ll. 1056–1060, whose two **new** REV1 criteria are ll. 1058–1060). Beyond the assigned list and each disclosed in §0.11 with the item it turned on: Phase 6's *Scope — in* and cadence model (ll. 1545–1557, 1601–1607), Phase 9's *Scope — in/out* (ll. 1918–1932), §G5.1's Phase 6/9/11 rows (ll. 572–577) and Phase 11's spec head (ll. 2081–2090) for V11-1 and §6's rung-1 row; Phase 3's and Phase 7's engine-flag bullets (ll. 1260–1265, 1693–1695) for §0.10's coordinate note; the dropped-item audit (l. 2419). Read again by the round-twelve fix-up for §4.12's and §5.1's ownership corrections: **Phase 5's *Scope — in*** (ll. 1487–1489), **Phase 6's sampler-re-pointing bullet** (l. 1563), **Phase 13's companion-atlas and custom-texture bullets** (ll. 2253–2262, 2269–2271), §G5.3's shared-ownership seam list (l. 631) and §G1.1 l. 252 — each disclosed in §0.12 with the finding it turned on. Read a third time by the round-**thirteen** fix-up, which needed the ownership of two capabilities this document attributes to other phases and therefore swept **Phase 4**'s program-registry bullet (l. 1334), **Phase 5**'s spec entire (ll. 1409–1525, for every occurrence of one term; ll. 1486, 1488 quoted), **Phase 7**'s composite/final bullet and injection timeline (ll. 1683–1685, 1709–1713), **Phase 8**'s shadow bullet (l. 1828) and **Phase 13**'s *Scope — in/out* (ll. 2269–2271, 2287) — each disclosed in §0.13 with the finding it turned on, and each an audit of a claim *this* document makes about another phase rather than a read of that phase's assignment. **RC2 governs this phase and remains a candidate overall — §0.12 records the adoption state and where to resolve it.** |
| `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` (`PD`) | Read by the round-eleven fix-up, scoped by §G11.6's reading map, which assigns P1 exactly two sections: **§2** (architecture / seam comparison, ll. 70–102) and **§16** (environment & platform notes, ll. 756–779). Also **§17**'s bug catalogue and **§18**'s divergence table, which §G11.4 makes *standing* do-not-inherit lists for any doc consuming Pintonium material — four §17 rows reach this phase and no §18 row does (§11.3 item 11). **No other PD section was read**, and the bound is stated rather than implied because REV2's Context budget names `Pintonium/` in its do-not-explore clause (§0.3 item 1). |
| `reference-src/pintonium-9c2fcc1/` (Pintonium source) | Read **only** to verify the three PD claims this document builds on, per §G1.1's instruction to treat PD as a pointer and check load-bearing claims at source. Files opened: `common-shaders/src/main/java/org/embeddedt/embeddium/compat/mc/MinecraftVersionShimService.java` (complete — the inventory §4.12 checks against); `com/mitchej123/glsm/{GLStateManagerService,RenderSystemService}.java` — **existence and package only until the round-twelve fix-up, which read both complete** (92 and 65 lines) because REV2's checklist names them and §4.12 had bucketed neither (V12-5); `org/taumc/celeritas/CeleritasShaderVersionService.java` plus the `org/taumc/celeritas/` and `api/v0/` directory listings and `api/v0/CeleritasShadersApi.java` l. 1 (package and neighbours — where PD §2's coordinate proved wrong, and where the round-eleven statement of *how* it was wrong proved wrong in turn, V12-7); `forge122/src/shaders/java/org/embeddedt/embeddium/impl/MinecraftVintageVersionShimImpl.java` ll. 505–512 (the main-framebuffer pair, read by the round-twelve fix-up — V12-6); `forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/startup/{MixinGameSettings,MixinInitRenderer,MixinGuiMainMenu}.java` (the `@Mixin`/`@Inject` lines only — §4.13's three stages); `forge122/src/main/java/org/taumc/celeritas/mixin/CeleritasVintageMixinPlugin.java` (its `getMixins()` structure) and `forge122/src/main/resources/mixins.celeritas.json` (complete — §4.5.2a); `settings.gradle.kts` ll. 77–133 and the top-level directory listing (§4.12's module count); `common-shaders/src/main/java/net/irisshaders/iris/pipeline/FinalPassRenderer.java` ll. 280–300 (read by the round-**thirteen** fix-up — the explicit `bindMainFramebuffer()` at the end of the final pass, l. 289, which is **behavioural observation only** per §G11.2 and refutes an `[A]`-tagged claim in §4.7.4 rather than supplying a design). **Nothing was copied**; §G11.2's licensing and §G11.3's repository traps applied throughout — in particular no attempt was made to recover the `celeritas-shader-refactor.zip` that §G11.3 records as tracked-but-deleted. |
| `docs/research/v1/RESEARCH.md` | §0 (reading guide, confidence tags), §1 (mission, non-goals, decision log D-1..D-10), §3.2 (source directives — cited by §3's `centerDepthSmooth` row), §4.1 (lifecycle — the GL capability probe set), §4.2 (program-registry mechanics — read for the facade's "use program is the universal state barrier" implication), §5.1, §5.2, §5.3, §6.1, §7.1 (the D-5 sentence only), §7.2, §7.4 (the coexistence sentence only), §8.3, §9, §10.1–§10.3, §11 (full OQ register), §12.2, §12.4, App D.1/D.3 (the `ivec2` uniforms), App E (header only — the "SRG name + descriptor" requirement quoted in §4.5.3), App H (glossary rows for platform terms). **App D.2/D.4, App B, App F.1/F.5/F.6/F.7, §3.4 and §3.5 were *not* read by the build session** — see §0.5, which records the fix-up session's reading and why that omission mattered. |
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
| `README.md` | Complete. |
| `Schmaloogium/.gitignore` | Complete. |
| `Schmaloogium/src/**` | All eight files: `main/java/com/example/modid/{ExampleMod.java, proxy/{IProxy,CommonProxy,ClientProxy}.java}`, `main/java-templates/com/example/modid/Reference.java`, `main/resource-templates/{mcmod.info,pack.mcmeta}`, `main/resources/modid_at.cfg`. |
| MCP `cleanroom` | `get_porting_guide("mixin-setup")`, `get_project_template("mixins.json")`, `get_project_template("checklist")`, `explain_concept("mcmod.info", loader="cleanroom")`. |

**Provenance tags used in this document.** RESEARCH.md §0.2 defines `[V:doc]`, `[V:observed]`,
`[V:template]`, `[V:mcp]`, `[V:web]`, `[A]`, `[U]`, `[D-n]` and `[Q:OQ-n]`, and this document uses
them with exactly those meanings — in particular `[V:doc]` means *verified against the shipped
OptiFine pack-author docs*, never "verified against a project document". Two tags are this
document's own and are declared here because RESEARCH.md does not define them; a third arrives from
§G11.4 at the round-eleven fix-up and is declared beside them:

| Tag | Meaning |
|---|---|
| `[V:design]` | Verified against `DESIGN.md` — a **project decision** of ours, not observed or documented reference behavior |
| `[V:repo]` | Verified by read-only inspection of this repository's working tree (files, `git log`, `git status`) on 2026-07-24, **unless a later date is stated inline** — the round-eleven fix-up's few repository observations carry `[V:repo 2026-07-26]` |
| `[V:observed — Pintonium <path>]` | **§G11.4's form, not this document's invention.** A claim adopted from the Pintonium reference, carrying the path it was read at. Where a citation instead reads `[V:observed — PD §n]`, the claim is taken from the mining report as a *pointer* and was **not** re-opened at Pintonium source — §G1.1 requires that only of load-bearing claims, and §4.12, §4.13, §4.5.2a and §11.3 item 11 each say which of the two applies at their site and why |

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
   bootstrap. **Two REV2 updates to this item, neither of which changes the omission.** *(a)*
   `cleanroom-src/` is an **alias** now rather than a workspace-root sibling — it resolves to
   `reference-src/cleanroom-0.6.6-alpha/`, gitignored, inside this repo (§G0.2). *(b)* REV2's Context
   budget line is *"mandatory reading ≈ 38k tokens"* and its do-not-explore clause has grown a second
   tree: *"do not spend it exploring cleanroom-src **or `Pintonium/`** beyond the listed inputs"*
   (ll. 1065–1067). The round-eleven fix-up honoured both — it read the two PD sections §G11.6 assigns
   P1 and the PD rows §G11.4 makes standing, and no other PD section — which §0.1's PD row and §0.11
   state as a bounded claim rather than leave to inference.

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

A separate fix-up session (§G1.3) applied `docs/phase1/reviews/PHASE_1_REVIEW_1.md`'s findings F-1 … F-12 to
this document. Its resolutions are recorded in that file under `## Resolutions`; the findings and the
verdict are unchanged there.

Inputs that session read beyond the build session's list, all for finding F-1 (the facade's missing
pixel-transfer verbs) and each cited by the finding itself: `PHASE_1_REVIEW_1.md`; RESEARCH.md §3.1 and
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
`PHASE_1_REVIEW_1.md`, `PHASE_1_REVIEW_2.md`, `PHASE_1_REVIEW_3.md`, `PHASE_1_REVIEW_4.md`;
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
  form. §4.2.3 had also quoted item 4b with a **seven**-word qualifying clause — *"at the time that
  script is compiled"* — dropped from the middle and no ellipsis, which is V9-9's defect at a second
  site. §4.2.3 now states the substance without a quotation — *"§12 item
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

***A coordinate note governing every line number in this subsection, stated once (V11-5).*** §0.10's
internal `PHASE_1_DOC.md` line numbers are in the coordinates of the file **round ten reviewed** —
commit `1d55717`, 3709 lines — not of the file this subsection's fix-up produced, and not of the file
you are reading. That is the honest form l. 853 already used (*"as round ten read them"*), promoted
here from an exception to the rule for the whole subsection; the blanket claim that contradicted it is
narrowed below. Every citation also names its *section*, which is what a reader should follow. Later
fix-ups shift these numbers again and are not expected to re-resolve them: this subsection is a
superseded record, and this document's convention since §0.4 is that history is pointed at, not
rewritten. **The same applies to this subsection's `DESIGN.md` coordinates, which are v1.1's** — the
revision governing when round ten ran. From §0.11 this document is anchored to **v2.0-RC2** (§0.1),
under which the sections cited below sit elsewhere: §G1.3 at ll. 302–320, §G5.3 at ll. 611–638,
Phase 3's engine-flag-ownership-map bullet at ll. 1260–1265 and Phase 7's engine-flag-wiring bullet at
ll. 1693–1695 — each re-derived at the line for this note, and each left unsubstituted below for the
reason just given.

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
- **V10-4 — the quotation is dropped rather than repaired.** §4.2.3 quoted §12 item 4b with a
  qualifying clause of **seven** words — *"at the time that script is compiled"* — removed from the
  middle and no ellipsis, and characterised item 4b as the text that was *not* changed
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
rather than read. That discipline governed this session's own citations — but the sentence that stood
here claimed they had been *"re-resolved against the finished file … not against the file it started
from"*, and they had not: they are in the coordinates of the file round ten reviewed, which is what the
note at the head of this subsection now says once for all of them (V11-5). The **claim** is narrowed
rather than the numbers re-resolved, because re-resolving them would make them stale again at the very
next fix-up — this one included.

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

**§G1.3 status at the time:** this fix-up **did not alter §5**. Every one of §5's four subsections is byte-for-byte
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
is defended by nobody, and it is a design call rather than a correction. **This subsection records
round ten only and is no longer the document's current state — see §0.11**, which supersedes its
conclusion in the sharpest way available: round eleven found a §5-touching correction, the trigger
fires again, and the phase is **not** verified.

### 0.11 Fix-up session addendum (round eleven — 2026-07-26)

**This round has two causes and they are kept apart throughout, because conflating them would make
each unauditable.** *Cause one:* an eleventh verify session ran after §0.10's fix-up —
`PHASE_1_REVIEW_11.md` (V11-1 … V11-6), PASS-WITH-CORRECTIONS with **one correction, five notes, zero
blocking**, and the correction touching §5. *Cause two:* the project owner directed a **migration of
this document from design v1.1 to `docs/design/v2.0-RC2/DESIGN.md`** — §G0.4 step 3, *"route
claim-touching deltas through a §G1.3 fix-up session … not a rebuild"*. **All six findings are
applied; none is refused and none is narrowed.** Two of the five notes offered a choice of fix shape
and the branch taken is argued below rather than merely recorded.

**The migration is larger than §G0.4's word "fix-up-sized" suggests, and the reason is worth stating
first because it shapes everything below.** REV2 describes its own deltas as shaped to be fix-up-sized
— and for the REV1→REV2 half that is exactly right: six of the seven Phase-1-spec deltas needed **no
edit at all**, because REV2 was corrected *toward* facts this document had already recorded
independently (§4.1's Blossom source sets at the `java-templates`/`resource-templates` rows, the
loader pin as an inline literal, *"No `src/test/` exists yet"*, §11.2's D-7 disposition naming the
commit REV2 now cites by hash). But **this document never absorbed REV1 at all** — before this
session it contained zero occurrences of the word "Pintonium" — so the migration is v1.1 → REV1 →
REV2, and REV1 brings two *Doc gate* criteria that no amount of trueing-up satisfies: a glue-seam
completeness check against PD §2's inventory, and a bootstrap sequence adopted or deviated with
reasons. Those are §4.12 and §4.13, and they are new design rather than bookkeeping.

**The design calls this session made**, recorded as arguments rather than outcomes, because a fix-up
session gets no adversarial review of its own and the next session can only attack reasoning that is
written down:

- **V11-1 is applied, and RC2 turned a three-source case into a five-source one.** Round eleven argued
  the Phase 9 attribution against `DESIGN.md` in three passages plus one internal contradiction. Every
  one was re-derived here **in RC2's coordinates rather than shifted from v1.1's** — Phase 6's
  *Scope — in* at l. 1601, its cadence model at ll. 1549–1550, Phase 9's *Scope — in* at ll. 1918–1920
  and *Scope — out* at ll. 1931–1932, the dropped-item audit at l. 2419 — and REV2 supplies a fifth
  that did not exist in v1.1: Phase 6's spec now carries an explicit notifier-audit duty for
  `blendFunc` (ll. 1602–1607), and §G4.6 the matching rule (ll. 554–557). The decline branch the review
  left open is therefore *further* from viable than when it was written, not closer. §3's row now
  states the non-attribution positively rather than by omission, because a reader who finds no mention
  of Phase 9 cannot tell a considered exclusion from an oversight — which is the same failure V11-1
  documents four rounds of this document making in the other direction.
- **V11-5 takes the *narrow-the-claim* branch, not the *re-resolve-the-numbers* branch, and the reason
  is that this fix-up proves the point.** Round eleven offered both. Re-resolving §0.10's internal line
  numbers against the finished file would have made them correct for exactly as long as it took the
  next edit to land — and this session's edits shift most of the document. So the blanket claim is
  narrowed to what is true and a **coordinate note is stated once at the head of §0.10**, on the model
  of l. 853's already-honest *"as round ten read them"*. The note was extended past the review's ask
  to cover §0.10's `DESIGN.md` coordinates too, which are v1.1's and which the migration would
  otherwise have silently orphaned; the RC2 equivalents are given in the note, each re-derived at the
  line, and deliberately **not** substituted into the body.
- **V11-4 takes the body-clause branch over the widened label.** The label is the name five sites
  delegate to, and a category name that changes is a category name that has to be swept; the
  distinction it was missing is one sentence, and a reader who reaches the row reaches the sentence.
- **§4.12's completeness check found something, and that is the round's most consequential result.**
  The check is the sort of exercise that is easy to perform as a ritual, and this one produced a real
  gap: App B.3's fixed unit map puts the **vanilla block atlas at unit 0 and `lightmap` at unit 1** on
  every GBUFFERS and SHADOW program, `TextureService.bindToUnit` takes a `TextureHandle`, and nothing
  in §4.7.4 produces one for a texture the engine did not create. **The obvious fix is refused on the
  seam**: an `adopt(int glName)` verb would put a raw GL name in an `:engine` signature, which
  `[D-P1-15]` and §4.7.3 exist to prevent and which `SeamBytecodeTest` would pass while the property
  was gone. What is adopted instead costs no facade **verb** — `mod.glue` implements `TextureHandle` for
  vanilla-owned textures, the GL name stays on the `:mod` side of C-1, and §5.1 names the slot while
  **Phase 5/6** fill it, because the unit map is **Phase 5/6** policy and not this phase's
  (`[D-P1-36]`). *Two claims in this bullet were corrected at the twelfth round and are narrowed here
  rather than left to be read as current: it said "Phase 6" alone, where `DESIGN.md` l. 1488 gives the
  texture-object half to Phase 5 (V12-1); and "costs no signature" was true of the verb list but not of
  the handle **declarations**, which had to lose `sealed` before a `mod.glue` implementation could
  compile at all (V12-3). See §0.12.*
- **§4.13 adopts two of PD §16's three bootstrap stages and deviates from the first, and the deviation
  is the argued part.** Stage 2 — `OpenGlHelper.initializeTextures` at `RETURN` — is adopted outright
  and is the load-bearing one: §7 had said *"at display init"*, which is a description rather than a
  site, and this is the site. Stage 1 is **deviated from**: Cleanroom hands us `preInit` and
  `FMLLoadCompleteEvent` natively and §4.9.1/§4.10 already sit on them, so adopting the reference's
  `GameSettings.loadOptions` mixin would spend one of D-5's ~25–30 injections on a moment the loader
  gives away — and would put a mixin ahead of the very check (§4.10 point 1) that decides whether to
  run at all. Stage 3 is adopted as a **signal** and left unwired, because Phase 1 has no consumer and
  a hook with no caller is not a design (`[D-P1-37]`). **The §G11.4 contract check was performed and
  came back negative**, which is recorded rather than skipped: a bring-up order is not
  contract-visible under §G4.2 — packs observe RESEARCH.md §3 and Apps A–D, F, and this is §4.1
  lifecycle — and the adoption is consistent with §4.1's actual constraint, which is an *ordering*
  (probe → discovery → load, init staying lazy) and not an instant.
- **§4.5.2a rejects the class-scan mixin plugin, and pays for the rejection.** The mechanism is real
  and PD describes it accurately — verified at the reference's own plugin and config. It does not fit
  because `[D-P1-11]` runs **three** phase-scoped configs where the reference runs one, and a scan
  answers *which classes are mixins* without answering *which CleanMix phase each belongs to*; making
  it phase-aware re-encodes in Java, against a convention that is itself upkeep, the split three JSONs
  already state. But the drift risk a scan removes is genuine, so the rejection is not free: a `:mod`
  **test** asserting config-array ↔ package agreement is adopted in its place (§12 item 30a),
  `[D-P1-38]`. A rejection that leaves the underlying risk unanswered is half an argument.
- **Three PD claims were load-bearing and all three were re-opened at Pintonium source; the rest were
  not, and the line between them is drawn explicitly.** §G1.1 asks for verification of load-bearing
  claims, not of every pointer. The three — §2's service inventory, §16's bootstrap sites, §16's
  class-scan plugin — were read at the files named in §0.1, and the reading **corrected PD once**:
  §2 gives the third seam interface as `org.taumc.celeritas.api.v0.CeleritasShaderVersionService` while
  the interface sits directly in `org.taumc.celeritas`. *The correction as this bullet first stated it —
  that the checkout has no `api.v0` package — was itself wrong, and is narrowed at §4.12 and §0.12:
  `api/v0/` exists in the same module and holds `CeleritasShadersApi`, so PD conflates two adjacent API
  surfaces (V12-7).* The four PD §17 rows in §11.3 item 11 were **not**
  re-opened, because none of them carries a Phase 1 design element, and that limit is stated at the
  site rather than left for a reviewer to discover.

**Inputs this session read beyond the build session's list**, each because a finding or a delta turned
on it: `PHASE_1_REVIEW_11.md` in full (the assignment) and `PHASE_1_REVIEW_10.md`'s `## Resolutions`
(ll. 733–915) **read-only, for its format**; `docs/design/v2.0-RC2/DESIGN.md` and
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` per §0.1's new rows; six files and one directory
listing under `reference-src/pintonium-9c2fcc1/`, itemised in §0.1; RESEARCH.md **App B.3**
(ll. 1227–1248, the unit map §4.12's finding turns on), **§4.1** (ll. 473–489, the lifecycle §4.13's
contract check turns on), **§4.4** l. 561 and **App A.3** l. 1187 (V11-3's two coordinates, both
re-resolved at the line before the javadoc was rewritten); `build.gradle` l. 60, `gradle.properties`,
`LICENSE` and `git show aa917a6` as read-only repository observations `[V:repo 2026-07-26]`.
`DESIGN.md` Part II beyond the Phase 1 spec was read for Phases 3, 6, 7, 9 and 11 — disclosed on the
standing precedent §0.8 records, that a session auditing an **ownership claim** may read the spec that
settles it. **No network use of any kind.** **Sub-agents: three read-only exploration agents were used
during the planning stage, for delta enumeration only**, and the choice is disclosed because §G1.3 is
silent and the call is therefore this session's; **no finding, coordinate or quotation below was
admitted on an agent's report** — every one was re-derived at its source by this session, which is the
discipline round nine's own agent breach (its §0.2) exists to impose. One agent-reported line number
was checked and found off, which is the reason the discipline is stated as practice rather than as
intent.

**Forbidden sources.** No directory named `chatlogs/` below `docs/` was opened, and no `*.txt` at the
repository root was opened — including the one currently sitting there. REV2 states this rule by
pattern precisely because `/export` mints new ones under dated names (§G1.1, ll. 245–249).

**The lesson worth recording.** Rounds nine and ten found that a *true claim* about a document can
suppress the sweep of the region it is true about. Round eleven's migration adds a different one, and
it is about the review cadence rather than about a sweep: **eleven adversarial rounds could not have
found §4.12's gap, because every one of them audited what this document says against the sources it
cites — and the gap is a thing the document does not say.** No unmapped row, no wrong citation, no
missing interface; `bindToUnit` is correct, App B.3 is Phase 5/6's, and each half is defensible on its
own. What surfaced it was an *external inventory* of what a version-facing seam is field-proven to
need, applied as a checklist. That is a genuinely different instrument from adversarial review, it
came from REV1's reference work rather than from this project's own reasoning, and its first
application to this document returned one contract-grounded finding. The general form: **a review
tests a document against its sources; only a checklist drawn from outside can test it against the
sources it never had.**

**Three states this document was in *at the end of the round-eleven fix-up*, disclosed rather than left
to inference — and all three moved within hours of it, so each carries what has since landed.** The
three paragraphs are dated rather than rewritten, on §0.10's head-note principle; the current state is
§0.12's, and a reader who needs it should go to `docs/MOVES.md` and `PHASE_FACTS` rather than to any
§0 addendum (V12-8).

1. **As of the round-eleven fix-up, only §G0.4 step 3 had been performed** — the owner's direction was
   that this session leave everything `/verify-loop`-related alone — so `v1.1` was still the project's
   governing design while this document verified against RC2, and the trap §G0.4 names was live: a
   twelfth verify session briefed with v1.1 coordinates against an RC2-anchored document would not
   error, it would silently compare the wrong texts. **Since then, steps 1, 2 and 4 have all landed**
   (commit `afeceda`, the commit after this fix-up's own `c108630`): the harness pins RC2 per phase,
   `docs/MOVES.md` records RC2 as governing Phase 1 and states that there is no longer one governing
   revision, and step 4 is settled as a **ruling** — the `-RC` suffix is retained deliberately until
   *every* downstream doc has adopted the revision. `[V:repo 2026-07-26]` The **stop-and-report
   instruction stands on its own merits** and is kept: a session whose briefed coordinates disagree
   with this document's should stop and report rather than guess which is right, whatever the adoption
   state.
2. **The `v10` → `v11` directory roll was owed and deliberately not performed at the round-eleven
   fix-up**, because `docs/MOVES.md` defines it as two steps run together — the `git mv` **and** the
   `docVersion` bump in the harness — and the second was out of that session's scope, while doing only
   the first fails silently. **It has since been performed, both halves together with no loop
   running:** this document is at `docs/phase1/v11/PHASE_1_DOC.md` and `PHASE_FACTS` carries
   `docVersion: 'v11'`, so the rule that `v<K>` equals the highest `§0.K` addendum holds again — and
   §0.12 is what makes the *next* roll owed. The one dangling reference the roll cost is recorded in
   `docs/MOVES.md`, deliberately unrepointed. `[V:repo 2026-07-26]`
3. **A twelfth verify session was owed and was not run** by that fix-up. **It has since run** —
   `PHASE_1_REVIEW_12.md`, PASS-WITH-CORRECTIONS — and §0.12 is its fix-up. The only §G0.4 item still
   outstanding is the one `docs/MOVES.md` names: `PHASE_2_DOC.md` has not migrated to RC2.

**§G1.3 status at the time:** that fix-up **altered §5**, and did not pretend otherwise. Three changes,
each traceable: V11-1's deletion of the Phase 9 clause from §5.2's pixel-transfer consumer column, and
two **new §5.1 rows** — the engine bring-up sequence (`[D-P1-37]`) and the `mod.glue` vanilla-texture
provider slot (`[D-P1-36]`) — the second of which existed because REV2's mandated completeness check
found a contract the facade could not express. §5.2's per-revision changelog row gained an entry for
that revision, which §0.10 had correctly declined to add when §5 was unchanged. **No service signature
was added, removed or changed:** the seven services, every handle type and every value type were
byte-for-byte what rounds seven through eleven all reviewed, and the two new rows were structural
contracts rather than verbs — `[D-P1-36]` refused the verb that would have been the easy fix, on the
seam. *(Round twelve found that claim true of the verb list and **not** of the handle declarations,
which could not have compiled with a `mod.glue` implementer while they were `sealed` — V12-3, §0.12.)*
So §G1.3's *"re-verify only if §5 changed"* trigger **fired**, and by its own words that fix-up did
**not** close the phase: `PHASE_1_DOC.md` was **not verified**, not a valid dependency input (§G5.3),
and everything downstream stayed blocked until a twelfth session returned. What that session inherited
was narrow in one dimension and wide in another, and both were worth stating: **no finding was left
unapplied and none was refused**, so there was no re-derivation debt; but §4.12, §4.13 and §4.5.2a were
**entirely unreviewed material**, and round seven's rule applied to them without mercy — *unreviewed
material yields findings in proportion to its size, not to the document's maturity*. **That prediction
held exactly:** eight of round twelve's nine corrections landed in that material and four of them on
`[D-P1-36]`, the decision this paragraph named as where a twelfth session should start. **This
subsection records round eleven only and is no longer the document's current state — see §0.12**, which
supersedes it on both counts: the §5 trigger fires again, for four corrections rather than one, and the
three disclosures above are dated rather than current.

*Two notes on convention, so the next session does not read either as an error.* The three new
decisions carry a `[REV2 migration — §0.11; DESIGN.md …]` marker rather than the
`[fix-up: <review file> <finding>]` form §11.1 otherwise uses: they originate in a design-revision
delta, not in a review finding, and inventing a review citation for them would make the marker device
report something false — which is the failure V6-1 used it to detect. And **§0.10's line numbers are
not updated by this fix-up**, by the rule stated in its own head note; the same will be true of this
subsection at the next.

### 0.12 Fix-up session addendum (round twelve — 2026-07-26)

**What ran.** `PHASE_1_REVIEW_12.md` (V12-1 … V12-12), PASS-WITH-CORRECTIONS — **nine corrections, three
notes, zero blocking**, four corrections touching §5. **All nine are applied; the three notes are
deferred with their reasons**, both recorded in that file's `## Resolutions` — where §G1.3 puts the
argument, so the rulings below are one line each. Re-derived at source rather than adopted: `DESIGN.md`
ll. 631, 1056–1060, 1488, 1563, 2269–2271 and §G1.1 l. 252; `GLStateManagerService` and
`RenderSystemService` **complete**; the shim's main-framebuffer pair; `api/v0/`; `docs/MOVES.md`,
`PHASE_FACTS` and `git log` read-only `[V:repo 2026-07-26]`; MCP `search_mappings`. No `chatlogs/`, no
root `*.txt`, no network, no agents.

- **V12-1 — the vanilla-owned texture set is Phase 5's.** `DESIGN.md` l. 1488 gives it *which texture
  object backs each unit per stage*; Phase 6 gets the pointing (l. 1563). Six sites; §11.4 gains **To Phase 5**.
- **V12-2 — stage 2 is a requirement on Phase 7's catalog, stage 3 a recommendation.** §5.1 called both
  requirements; §9, §4.13 and `[D-P1-37]` never did. §11.4's miscount goes with it.
- **V12-3 — only `GLHandle` stays `sealed`.** JPMS rejected ⇒ permitted subtypes must share the sealed
  type's package, so `Lwjgl3GLDevice`'s, `RecordingGLDevice`'s and `[D-P1-36]`'s handles were all
  uncompilable as declared. The four leaves and `UniformLocation` are now `non-sealed` (`[D-P1-15]`).
- **V12-4 — `ForeignTextureProvider` is declared** in `engine.gl` beside the handles, installed from
  `mod.core` at §4.13 stage 2. §12 item 22b had shipped it at v0.1 with no site declaring it.
- **V12-5 — the Doc gate's checklist has two halves and both are now run.** The glsm services are
  bucketed in §4.12: **no new gap**, and their state-read members corroborate §12 item 22a's hook.
- **V12-6 — `bindDefault` binds framebuffer name 0** (now in its javadoc), so it serves the shim's
  *unbind* half only; binding a framebuffer the engine did not create is an absent verb, requesters 7 then 5.
- **V12-7 — `org.taumc.celeritas.api.v0` exists** and holds `CeleritasShadersApi`. PD conflates two
  adjacent API surfaces — a narrower and truer correction than the one §0.11 claimed.
- **V12-8 — §G0.4 steps 1, 2 and 4 and the `v11` roll all landed after the round-eleven fix-up.** §0's
  header, §0.11's three disclosures and the closing note are **dated, not rewritten**.
- **V12-9 — the drift test excludes sub-packages another config declares.** The three mixin packages
  are nested, so the subtree form went red on a *correct* config set at its first real exercise.

**§G1.3 status at the time:** that fix-up **altered §5** at five rows — §5.1's two (V12-1 … V12-4) and §5.2's
changelog, opaque-handle and non-verbs rows (the last carries V12-6's new absent verb). **No facade verb, result type or value type changed;** two
declarations did, and both were stated in the changelog row rather than glossed. So §G1.3's trigger
**fired**: that fix-up did **not** close the phase, `PHASE_1_DOC.md` was **not verified**, it was **not**
a valid dependency input, and Phase 2, Phase 3 and everything downstream stayed blocked until a
**thirteenth** verify session returned (§G5.3). What that session inherited: the three deferred notes;
§4.7.3's provider block, §4.12's glsm half and §4.7.4's new absent-verb row as **fresh unreviewed
material** at round seven's price; and one open question — `sealed` met the JPMS rejection here for the
first time in twelve rounds, and nothing established it was the only declaration that cannot compile.
**This subsection records round twelve only and is no longer the document's current state — see §0.13**,
which supersedes it in the way §0.12's own prediction invited: three of round thirteen's four
corrections landed in the ~140 lines round twelve added, the trigger fires again, and the phase is
still **not** verified.

### 0.13 Fix-up session addendum (round thirteen — 2026-07-26)

**What ran.** `PHASE_1_REVIEW_13.md` (V13-1 … V13-8), PASS-WITH-CORRECTIONS — **four corrections, four
notes, zero blocking**, three corrections touching §5. **All four are applied; the four notes are
deferred with their reasons**, both recorded in that file's `## Resolutions` — where §G1.3 puts the
argument, so the rulings below are one line each. Re-derived at source rather than adopted: `DESIGN.md`
ll. 1334, 1469, 1486, 1683–1685, 1709–1713, 1828, 2269–2271, 2287 and a full sweep of the Phase 5 spec
(1409–1525) for one term; RESEARCH.md ll. 488, 526, 545–546, 813, 1185, 1229–1246, 1396 (App E's column
heading), 1414, 1481–1484; `FinalPassRenderer.java` l. 289, behavioural observation only (§G11.2). No
`chatlogs/`, no root `*.txt`, no network, no agents, no build.

- **V13-1 — the framebuffer-bind row's `[A]` ground is deleted.** The composite chain runs immediately
  before `final` (RESEARCH.md ll. 545–546) and GL bindings are sticky, so a `final` that does not rebind
  writes into the shader estate; App E row 17 catalogues a *hook need*, not vanilla behaviour. The row
  stays absent on the true ground — no verb binds it, and l. 1713's TAIL site is its home outside the
  facade. §5.2's row now states the v0.1 obligation, so this correction **does** touch §5.
- **V13-2 — the provider's key space is two vocabularies, not one.** App B.3's sampler names are the
  *destination* side of App F.5's form; Phase 13 holds the *source* side, a resource location. (a) is
  Phase 5's, (b) is Phase 13's, Phase 1 enumerates neither. §11.4's *To Phase 13* block gains the
  hand-off it never had.
- **V13-3 — a foreign handle is bind-only and outside the lifetime rule.** Legal to `bindToUnit` and
  `DebugService.label`; rejected on `allocate`/`setParameters`/`upload`/`generateMipmap`/`delete`. Phase 5
  must not carry one into the uninit delete loop — that destroys Minecraft's block atlas (§6 rung 5).
  The `mod.glue` handle resolves its object per use, so a vanilla reload needs no caller action.
- **V13-4 — composite mipmap generation is Phase 7's, not Phase 5's.** `DESIGN.md` l. 1683 (Phase 7
  generates), l. 1334 (Phase 4 carries the bitmask), l. 1469 (Phase 5's only mipmap interest is the
  shadow *filter config*), l. 1828 (Phase 8 generates on the shadow side). §5.2's profile row gains
  **7** and **8**, which it named neither of.

**§G1.3 status at the time:** that fix-up **altered §5** at four rows — §5.1's provider row and §5.2's
`GLCapabilityProfile`, opaque-handle and non-verbs rows. **No facade verb, result type, value type or
declaration changed:** the seven services, the handle types and `ForeignTextureProvider`'s signature were
byte-for-byte what round thirteen reviewed; what moved was *contract stated in prose about types that
already existed*. So §G1.3's trigger **fired**: that fix-up did **not** close the phase,
`PHASE_1_DOC.md` was **not verified**, it was **not** a valid dependency input, and Phase 2, Phase 3 and
everything downstream stayed blocked until a **fourteenth** verify session returned (§G5.3). What that
session inherited: the four deferred notes, two of which (V13-7, V13-8) that fix-up's own edits enlarged
rather than left alone — recorded as such in the Resolutions; and the widened key space and bind-only
rule as **fresh unreviewed material** at round seven's price.
**This subsection records round thirteen only and is no longer the document's current state — see
§0.14**, which supersedes it exactly as its own last sentence priced: both of round fourteen's
corrections landed in or beside the material round thirteen touched — one in the bind-only rule it
added, one a survivor of the very class its V13-4 fixed one row over — the trigger fires again, and
the phase is still **not** verified.

### 0.14 Fix-up session addendum (round fourteen — 2026-07-26)

**What ran.** `PHASE_1_REVIEW_14.md` (V14-1 … V14-7), PASS-WITH-CORRECTIONS — **two corrections, five
notes, zero blocking**, both corrections touching §5. **Both are applied; all five notes are deferred
with their reasons**, recorded in that file's `## Resolutions` — where §G1.3 puts the argument, so the
rulings below are one line each. Re-derived at source rather than adopted: `DESIGN.md` ll. 1245,
1334–1337, 1365, 2410 and a sweep of Phase 5's spec (ll. 1409–1525) for "blend"/"alphaTest" (zero
hits); RESEARCH.md ll. 419–437 (§3.6.7's `PER_BUFFER_BLENDING`, l. 429) and 1508–1516 (App F.7 —
`blend.<prog>` carries no per-buffer axis, l. 1511); §4.7.4's declarations swept for `TextureHandle`
(ten accepting verbs — the review's three-verb gap is exact). No `chatlogs/`, no root `*.txt`, no
network, no build.

- **V14-1 — the bind-only rule is stated by closure, not by list.** A foreign handle is legal to
  `bindToUnit` and `DebugService.label` and to nothing else in §4.7.4 that accepts a `TextureHandle`;
  the illegal set gains `FramebufferService.attachColor`/`attachDepth`/`copyDepthToTexture`, the last
  the destructive case (`dst` is written into). Five sites: §4.7.3, §5.1's provider row, §5.2's
  opaque-handle row, §11.4's *To Phase 5*, §12 item 22b — whose reviewer check is now the closure.
- **V14-2 — per-program alphaTest/blend routing is 3 (parse), 4 (apply), 7 (execute), not "Phase 5/6
  policy".** `DESIGN.md` l. 2410's coverage row; l. 1245 (stored by 3, applied by 4); ll. 1334/1365
  (per-slot carry; the use-program alpha/blend lock). The "per-buffer routing is Phase 5's" clause is
  deleted, not re-homed — App F.7's form has no per-buffer axis. §5.2's `StateService` row gains
  consumer **4**; §4.7.4's composite-block sentence now names 3/4/7 for the override values.

**§G1.3 status:** this fix-up **altered §5** at three rows — §5.1's provider row and §5.2's
opaque-handle and `StateService` rows. **No facade verb, result type, value type or declaration
changed:** the seven services, the handle types and `ForeignTextureProvider`'s signature are
byte-for-byte what round fourteen reviewed; what moved is contract stated in prose about types that
already existed. So §G1.3's trigger **fires**: this fix-up does **not** close the phase,
`PHASE_1_DOC.md` is **not verified**, it is **not** a valid dependency input, and Phase 2, Phase 3 and
everything downstream stay blocked until a **fifteenth** verify session returns (§G5.3). What that
session inherits: the five deferred notes plus the standing ones (V12-12, V13-5–V13-8 — V13-7 still
first, per round thirteen's instruction, and jointly addressable with V14-4); V14-3's and V14-5's
conditional-§5 branches, deferred this round on the brief's no-notes rule rather than on their merits;
and the closure-stated bind-only rule and the re-derived App F.7 rows as **fresh unreviewed material**
at round seven's price.

**This subsection records round fourteen only.** Round fifteen subsequently returned a literal PASS
with zero corrections (`docs/phase1/reviews/PHASE_1_REVIEW_15.md:253`–`:280`), closing that loop; the
new downstream-request fix-up below supersedes that verified byte state.

### 0.15 Fix-up session addendum (Phase 4 fixed-function request — 2026-07-28)

**What triggered this fix-up.** The Phase 4 build document followed §G1.1 and this document's own
§4.7.4/§5.2 escape hatch: it did not invent a null program, magic handle, or raw integer, but recorded
the missing operation in its §5
(`docs/phase4/v1/PHASE_4_DOC.md:895`–`:943`). The request is
`ShaderService.useFixedFunction()` — name now fixed with those semantics — so Phase 4's program-state
barrier can select the no-program terminal required by RESEARCH.md Appendix A.1. The request was
re-derived rather than promoted merely because Phase 4 says it: Appendix A.1 specifies fixed-pipeline
terminals for the external `<none>` sentinel and absent `shadow`/`gbuffers_basic` roots, and a
passthrough terminal for absent `final`
(`docs/research/v1/RESEARCH.md:1106`–`:1145`); RC3 assigns Phase 4 the whole Appendix A registry and
the use-program barrier (`docs/design/v2.0-RC3/DESIGN.md:1471`–`:1534`). A zero-agent
`scripts/verify --target phase-1 --preset lean --review-only --max-rounds 1 --dry-run` first resolved
this artifact to `docs/phase1/v14/PHASE_1_DOC.md`, RC2, and round sixteen. No forbidden `chatlogs/`,
root `*.txt`, network source, reference implementation source, agent, build, or code file was used.

- **The facade now has two explicit program-selection modes.** `ShaderService.use(ProgramHandle)`
  selects a live linked shader program; `ShaderService.useFixedFunction()` selects program zero.
  The latter has no parameter, so neither a raw GL name nor a synthetic/null `ProgramHandle` crosses
  C-1. Both are low-level selection verbs used inside Phase 4's state barrier; neither claims the
  barrier's sampler/uniform/custom refresh or alpha/blend policy.
- **Recording/replay and backend obligations move with the verb.** The recorder emits
  `shaders.useFixedFunction` with no arguments. Existing `calledInOrder`/`neverCalled` assertions
  therefore distinguish a fixed terminal from a shader selection, while `noUseAfterDelete()` remains
  a handle-lifetime assertion and treats the handle-free operation as legal. `Lwjgl3GLDevice` maps the
  operation to program zero internally through the same program-selection path as `use`; zero never
  becomes a handle, is never created/deleted, and never appears in an `:engine` signature.
- **All contract-bearing surfaces are updated together.** §3 maps the Appendix A.1 terminals; §4.7.4
  states exact semantics; §4.7.5 specifies the log; §5.2 publishes the operation and recorder/replay
  coverage to Phase 4; §8, §9 and §12 carry the tests, milestone and implementation checks; §11 logs
  `[D-P1-39]` and hands the accepted request back to Phase 4.

**§G1.3 status:** this fix-up changes the binding §5 surface by adding a facade declaration and
recorder/replay contract. Round fifteen's PASS remains the historical verdict on the pre-§0.15
bytes; it does not review this addition. `PHASE_1_DOC.md` is therefore **not verified**, is **not** a
valid dependency input, and everything downstream remains blocked until a fresh round-sixteen verify
session returns a literal PASS (or a correction-bearing review is fixed up and re-verified). The
version directory stays `v14` and the manifest stays pointed at it while that loop is open; no
mid-loop roll is permitted.

**Historical status:** round sixteen subsequently returned PASS-WITH-CORRECTIONS; §0.16 supersedes
this paragraph.

### 0.16 Fix-up session addendum (round sixteen — 2026-07-28)

Round sixteen admitted two corrections, both applied; the full re-derivation and site list are in
`docs/phase1/reviews/PHASE_1_REVIEW_16.md` under `## Resolutions`.

- §3 now distinguishes the true fixed-pipeline terminals, which select program zero through
  `useFixedFunction()`, from absent `final`, whose passthrough-copy terminal is downstream-owned.
- Binding §5 now carries Phase 2's derived-artifact constraints already stated in §11.4: no pack
  source text in goldens, no rendered images in the repository, and explicit
  `-PupdateGoldens` regeneration that still fails its regenerating run.

**§G1.3 status:** the second correction changes §5. This document remains **not verified** and is
not a valid dependency input until a fresh round seventeen returns a literal PASS with zero
blocking findings and zero corrections. The version and manifest remain at `v14` during the loop.

**Historical status:** round seventeen subsequently returned PASS-WITH-CORRECTIONS; §0.17
supersedes this paragraph.

### 0.17 Fix-up session addendum (round seventeen — 2026-07-28)

Round seventeen admitted one correction, applied only to the closing session history: §0.4's
round-one fix-up is now the first session, and §0.5's combined rounds 2–4 fix-up is the second. The
full re-derivation is in `docs/phase1/reviews/PHASE_1_REVIEW_17.md` under `## Resolutions`.

**§G1.3 status:** this correction does not change §5 or any interface region. With round seventeen's
only correction applied, this document is **verified** and is a valid dependency input. The version
stays `v14` until the post-loop version roll.

**Historical status:** round eighteen subsequently returned a literal PASS with zero findings
(`docs/phase1/reviews/PHASE_1_REVIEW_18.md:48`–`:64`). The downstream-request amendment below
supersedes that verified byte state.

### 0.18 Downstream-request addendum (Phase 5 framebuffer/depth contract — 2026-07-29)

**What triggered this amendment.** Phase 5 followed the §G1.1 dependency rule and requested three
narrow additions instead of inventing raw-LWJGL or uncontracted handle behavior. Its converged
review records the requested Phase 1 framebuffer/depth operations and the separate Phase 4
candidate-view clarification at
`docs/phase5/reviews/PHASE_5_REVIEW_23.md:34`–`:38`. The requests were re-derived against
RESEARCH.md's real `depthtex0` attachment and two copy-target contents
(`docs/research/v1/RESEARCH.md:509`–`:526`,
`docs/research/v1/RESEARCH.md:1216`–`:1225`) and RC3's mandatory first-copy/capability-tiered
depth path (`docs/design/v2.0-RC3/DESIGN.md:1621`–`:1652`).

- **The foreign-handle contract gains one authenticated exception, not a widening.**
  `BorrowedDepthAttachmentHandle extends TextureHandle` is the opaque `engine.gl` marker for the
  platform-owned, sampleable main-depth texture. A marker value is usable for sampling
  (`bindToUnit`), labeling, `attachDepth`, and `attachDepthStencil` only when the receiving backend
  authenticates that it issued the value for the same device/context. Merely implementing the
  public marker does not confer permission: forged and wrong-origin values are rejected before GL.
  Ordinary `ForeignTextureProvider` handles remain bind-and-label-only.
- **`FramebufferService` gains two distinct operations.**
  `attachDepthStencil(f,t)` attaches one compatible texture to both attachment points while
  `attachDepth(f,t)` remains depth-only.
  `initializeDepthTextureFromFramebuffer(src,dst,region)` defines owned destination level zero
  from the source's exact depth or packed depth/stencil format and contents on the first copy after
  creation/reallocation. The existing `copyDepthToTexture` is the steady-copy operation: it never
  defines or changes destination storage.
- **Validation, restoration, recording, and tests move with the signatures.** Owned and borrowed
  matrices, live/same-device provenance, combined-format validation, first-versus-steady copy
  state, prior framebuffer/texture-binding restoration, exact recorder operation names, facade
  tables, conformance mapping, milestones, `[D-P1-40]`, Phase 5's handoff, and checklist hooks are
  updated together.

**§G1.3 status:** this amendment changes the binding §5 interface region. Round eighteen's PASS is
the historical verdict on the pre-§0.18 bytes; it does not review this addition. Phase 1 is
therefore **not verified** and is not a valid dependency input until a fresh round nineteen returns
a literal PASS, or any correction-bearing review is fixed up and the resulting §5 surface
re-verified. The version directory and target remain at `v14` while the loop is open.

### 0.19 Fix-up addendum (round nineteen — 2026-07-29)

Round nineteen's three corrections are applied; full dispositions are in
`docs/phase1/reviews/PHASE_1_REVIEW_19.md` under `## Resolutions`.

The §0.18 mechanics sourced only to RC3 are no longer binding Phase 1 requirements. The facade
retains the three requested operation shapes and gains `borrowDepthAttachment`; Phase 5 owns
format, attachment/copy cadence, storage, freshness, restoration, and Minecraft lifetime policy.
Issuance proves only that the receiving device recognizes the live platform texture and returns
its own opaque, non-owned handle; it does not import RC3 policy.

**Historical status:** §0.18's live status is superseded, and round twenty subsequently returned
literal PASS on this §0.19 surface. That closed status is itself superseded by §0.20's downstream
grant and fresh-review requirement; the version remained `v14` between the two amendments.

### 0.20 Downstream-request addendum (Phase 8 package grant — 2026-08-03)

Round twenty subsequently returned literal PASS with zero findings
(`docs/phase1/reviews/PHASE_1_REVIEW_20.md`). Phase 8 then requested the three package homes its
verified architecture requires instead of assuming placement under Phase 1's closed table
(`docs/phase8/v1/PHASE_8_DOC.md` §5.5 R8-3).

This amendment grants exactly `com.schmaloogium.engine.shadow`,
`com.schmaloogium.mod.glue.shadow`, and `com.schmaloogium.mod.mixin.shadow`. The first contains
only loader-neutral shadow policy, math, traversal, lifecycle, and result types; the second owns
all Minecraft/Forge/LWJGL shadow adapters; the third owns the dumb SRG-targeted shadow Mixins and
accessors. The existing C-1 through C-4 seam constraints and `.internal` privacy rule apply
unchanged. The grant assigns placement only and does not import Phase 8 policy into Phase 1.

**Historical status (superseded by §0.21):** round twenty's PASS applies to the pre-§0.20 bytes.
This amendment changes binding §5, so Phase 1 is **not verified** and is not a valid dependency
input until a fresh round twenty-one returns literal PASS (or any corrections are fixed and the
changed interface is re-verified). The version and manifest remain at `v14` while the loop is open.

### 0.21 Fix-up addendum (round twenty-one — 2026-08-03)

Round twenty-one returned PASS-WITH-CORRECTIONS with one correction and no blocking findings or
notes (`docs/phase1/reviews/PHASE_1_REVIEW_21.md`). Section 1.2 now assigns the shadow subsystem
concerns introduced by §0.20 to Phase 8 and limits Phase 1's role to package placement and seam
constraints. Binding §5 is unchanged.

**Current §G1.3 status:** the correction is applied, but Phase 1 remains **not verified** and is not
a valid dependency input until a subsequent fresh review returns literal PASS. The version and
manifest remain at `v14` while the loop is open.

**Historical status:** round twenty-two subsequently returned literal PASS with zero findings
(`docs/phase1/reviews/PHASE_1_REVIEW_22.md`). The downstream-request amendment below supersedes
that verified byte state.

### 0.22 Downstream-request addendum (Phase 7 frame packages and replay evidence — 2026-08-03)

Phase 7 followed the dependency rule and requested two Phase-1-owned additions in
`docs/phase7/v1/PHASE_7_DOC.md` §5.4: R7-8 supplies legal package homes for the frame driver,
Minecraft glue, dumb Mixins, and capture agent; R7-6 grants Phase 2 R4A's total replay-aware
GL-error result. This amendment accepts both without importing frame policy, capture serialization,
or Phase 6's uniform-disable policy into Phase 1.

- The closed package table gains `engine.frame`, `mod.glue.frame`, `mod.mixin.frame`, and
  `mod.conformance`. Existing C-1 through C-4, LWJGL confinement, Mixin config agreement, and
  `.internal` privacy rules apply unchanged.
- `ReplayAwareGLError(GLError error, boolean attributed)` is an additive immutable diagnostic value.
  It does not change `GLError` or `GLDevice.drainErrors()` and adds no facade verb. The caller that
  performs `[D-P1-32]`'s replay emits exactly one result for every error in the triggering drain.
  `attributed=true` only when replay of the already-computed calls, with a drain between individual
  calls, reproduces and isolates that error to one named facade operation. Replay-clean,
  still-batched, ambiguous, or foreign-window outcomes are `false`; no consumer may infer the bit
  from `op` or `subjectLabel`.

The detailed package tables, illustrative declaration, binding §5 rows, milestones, decisions
`[D-P1-41]`/`[D-P1-42]`, tests, and checklist are updated together.

**Historical status (superseded by §0.23):** this amendment changes binding §5. Round twenty-two's PASS is historical;
Phase 1 is **not verified** and is not a valid dependency input until a fresh whole-document review
returns literal PASS, or any correction-bearing review is fixed up and the changed §5 surface is
then re-verified. The version directory and target remain at `v14` while the loop is open.

### 0.23 Fix-up addendum (round twenty-four — 2026-08-03)

Round twenty-four returned PASS-WITH-CORRECTIONS with two corrections and no blocking findings or
notes (`docs/phase1/reviews/PHASE_1_REVIEW_24.md`). The closing history now records round
twenty-three's literal PASS and this round. Section 5's preamble no longer claims that incorporated
facade declarations are reproduced there; it instead makes every change to an incorporated public
contract incomplete unless the corresponding binding §5 row changes in the same revision. The
facade declarations and semantics themselves are unchanged.

**Current §G1.3 status:** both corrections are applied, but the §5 synchronization rule is a binding
interface change. Phase 1 is **not verified** and is not a valid dependency input until a fresh
whole-document review returns literal PASS. The version directory and target remain at `v14` while
the loop is open.

### 0.24 Downstream-request addendum (Phase 7/13 package placement — 2026-09-07)

This maintainer-authorized architecture-only amendment checks the existing closed §2.1 table
before granting anything. Phase 7's §5.4 R7-8 asks to "add package slots" for the frame core,
glue, mixins, and capture agent (`docs/phase7/v1/PHASE_7_DOC.md:2301`); all four already exist
under §0.22 / `[D-P1-41]` in §2.1 and §5.1. **R7-8 is already granted, not granted again.**
Phase 13's §5.3 R3 still requests "engine.textures/mod.glue.textures/" and
"mod.mixin.textures" (`docs/phase13/v1/PHASE_13_DOC.md:1354`–`:1356`). Those three exact homes
are the only missing entries and the only additions here (`[D-P1-43]`).

Inputs read for this bounded ownership amendment: `docs/MOVES.md`; this document's package,
seam, §5, milestone, decision and hand-off surfaces; Phase 7 §§0/2.1/5.4 and Phase 13
§§0/1–2.1/5.3–5.5 for the requests and their ungranted dependencies; and the latest Phase 1
review's status. The governing revision remains **RC2 for Phase 1**, **RC3 for Phase 7**,
and **v3 for Phase 13**, per their own headers. The relevant module-layout/assignment passages
were read in each declared revision, not substituted across revisions. RESEARCH's D-6 requires
the core to remain "free of Minecraft/loader types behind a thin glue"
(`docs/research/v1/RESEARCH.md:829`–`:830`); no package grant relaxes that seam.

The active §2.1 tables, incorporated §5.1 structural contracts, §9 staging, §11 decision and
hand-offs, and §12 implementation checklist are synchronized. All pre-existing grants remain,
including Phase 8's shadow trio and Phase 13's parent `mod.mixin` allocation. No facade verb,
runtime protocol, downstream dependency API, module edge, or Mixin configuration is added.
Section 11.4 explicitly separates these placement grants from the still-ungranted dependency
requests. No source/package directories, reviews, manifests, or other documents are changed;
no builds, tests, or verification sessions are run, and `v14` is retained.

**Current §G1.3 status (supersedes earlier status paragraphs):** Review 25 recorded "PASS",
"Counts: blocking=0; corrections=0; notes=0", and "Interface changed: no"
(`docs/phase1/reviews/PHASE_1_REVIEW_25.md:61`–`:63`), verifying the §0.23 surface.
That PASS is now historical: this amendment changes binding §5. Phase 1 is **not verified**
and needs a fresh **whole-document** review returning literal PASS before verified downstream
consumption, per `docs/design/v2.0-RC2/DESIGN.md:308`–`:313`. This is not a review resolution
or a claim that the unchanged Phase 7/13 documents have consumed the grant.

### 0.25 Downstream-request addendum (Phase 4 legacy geometry — 2026-09-07)

This maintainer-authorized architecture-only amendment selects the Phase 1 alternative in
`docs/phase4/v1/PHASE_4_DOC.md:1859-1865`: "an engine-enum pre-link legacy geometry configuration
operation". The preferred Phase 3 route was assessed first, not silently skipped.
Its active contract says rewrites "may touch only the two spans"
(`docs/phase3/v1/PHASE_3_DOC.md:1997-1998`), rebuilds the original active `#version`
(`:1982-1984`), and exposes only `None` and `Translate(plan)` (`:1281-1284`).
`MaterializedSource` has no whole-source compatibility result (`:1203-1209`).
That is configuration rewriting, not complete legacy-to-core translation.

Complete translation would additionally have to preserve extension-era varying declarations,
input arrays and built-ins, effective-version semantics and adjacent-stage linkage, with every
transformation attributed and fingerprinted. The published ARB specification defines `varying in`,
`gl_VerticesIn`, the built-in input arrays (including two-dimensional `gl_TexCoordIn`), and separate
GL 3.2 layout rules. Its "Dependencies on OpenGL 3.2" says core geometry "provides no program
paramter state" and that a source layout overrides a program parameter. See
[ARB_geometry_shader4](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_geometry_shader4.txt),
§2.16, GLSL §§4.3.6/7.6 and that dependency section
(`[V:web]`, read 2026-09-07). A version bump plus token substitutions is not a completeness proof.
A new compliant translator is possible design work, but is not supplied by Phase 3's current
stage-local two-span contract or its no-AST decision D-P3-16. This amendment does not certify a
subset as dual-form support or adopt any prohibited transformer.

**Decision D-P1-44:** grant the narrow native legacy operation in active §§4.7.4/5.2, keeping
core program/shader objects and the D-6 seam. Source preservation remains Phase 3's, strategy
and compilation remain Phase 4's. This replaces the active no-pre-link assumption; all historical
addenda, the §0.24 package grants, and unrelated contracts are preserved. The source-side grant
needed to use this operation is explicitly **ungranted** in §5.2/§11.4: today Phase 3 rejects
`None` for a legacy pair and cannot return a legacy-preserving materialization.

Inputs actually read for this bounded amendment: `docs/MOVES.md`; this document's header,
scope/facade/§5/error/recorder/staging/decision/hand-off contracts; RC2 Part I and Phase 1
specification; Phase 3's header and active source, materialization, geometry and dependency
contracts; Phase 4's header, §4.8 and §§5.3–5.4; RC3 Part I, Phase 3 specification and the
Phase 4 compile/geometry requirements; RESEARCH §§0–1, §§3.1–3.2/3.5/6.1–6.2 and App A.3;
the shipped `doc/shaders.txt` geometry table; and the Khronos specification above to resolve
the actual API/version/limit gap. Phases 3/4 are request evidence, not new Phase 1 dependencies.
RC2 still governs Phase 1; RC3 still governs Phases 3/4. No review is consumed or edited here.

**Current §G1.3 status:** §5 changes again and requires fresh whole-document verification
returning literal PASS before verified downstream consumption. This is a producer-side
architecture grant, not end-to-end legacy support: §11.4 names the Phase 3 grant and Phase 4
migration still owed. No code, builds, tests, verification runs, authority edits or directory
rolls; `v14` remains the path.

---
### 0.26 Integration reconciliation — 2026-09-07

This architecture-only IR-04/14/18/29 fix-up reads the integration findings/ledger, P1's
complete §5 and incorporated diagnostic/bootstrap contracts, the RC2 Phase 1 specification
and §G1.3, and the P2/P3 owner snapshot requests. D-P1-45 grants P2 diagnostic-type consumption
without widening diagnostic fields or artifact permissions. D-P1-46 reaffirms loader-owned
stage one and keeps non-fullscreen countInstances authority-open. Existing package and
native-parameter grants are not missing APIs; native-preserving source output and jcpp
permission remain separate gates. Changed §5 and receiving contracts are **unverified pending
fresh whole-document reviews**. No implementation, validation or new PASS claim is made.

### 0.27 IR-18 prepared-submission decision — 2026-09-07

RC2 remains this phase's governing design. After research §3.2/4.5 and the scoped published/
licensed evidence recorded in P4 §11.5, the maintainer chose N adjacent prepared submissions
at v0.5, P7 policy/P10 adapters, one P8 traversal. D-P1-47/§5/§11 incorporate that authority;
earlier open-case statements in §0/decision history are superseded, not erased. No new facade
verb, renderer API, source reuse, implementation or verification claim. Changed §5 remains unverified.


### 0.28 Fresh R26 fix-up and commissioned foundation grants — 2026-09-07

This is the separate fix-up of `PHASE_1_REVIEW_26.md`, not its reviewer. R26-1 is
addressed by D-P1-48: successful-link primitive metadata belongs to the backend and the
existing `fullscreenQuad()` uses its triangle-strip route when required. P7 adoption and
the separate P10 native-QUADS topology question are explicitly handed onward in §11.4.
D-P1-49 admits P3 §5.4's pure-JVM jcpp dependency with a verified implementation-time pin;
D-P1-50 grants the bounded P10 R10-1 package/input facade and early-check contracts;
D-P1-51 reconciles active build instructions to the executable checkout without a pin upgrade.

Inputs actually read: complete RC2 globals and Phase 1 specification, complete P1 and R26;
P3's dependency/schema/request contracts; P10's complete §5 request and incorporated
layout, draw/restoration and compatibility sections; P7 fullscreen/prepared-submission
semantics; P4's consumed contracts and complete §11.5 prepared-submission contract;
the current root build/settings/properties, dependency/extra scripts, wrapper and all three
CI workflows; and the U1 decision. The additional owner reads answer concrete requests,
not new dependency edges. The published Khronos ARB_geometry_shader4 specification was
read at its native primitive and GL-3.2 precedence/query clauses: revision 26,
last modified 2011-01-21, copyright 2008–2013 Khronos under its stated specification
copyright terms, URL in §0.25. No implementation source was read or copied in this fix-up.
`[V:repo 2026-09-07]` below means file inspection only, not artifact resolution or execution.
New mechanisms are local design decisions, not source-observed implementations.

The July template/pin evidence and all prior addenda remain dated history. Active instructions
are overridden only where D-P1-51 explicitly says so. U1's approved documented-mechanism
correction supersedes this document's old pending-suffix prerequisite, not texture defaults.
P3 native-source completion is separately commissioned; this fix-up does not author its schema.
All changed §5 contracts remain **unverified**. Fresh independent whole-document review
returning literal PASS and final integration remain required; no code, build, test, formatter,
linter or validation command was run, and no complete legacy-rendering support is claimed.

### 0.29 Settled producer receipts and remaining foundation closure — 2026-09-07

P3 D-P3-68/schema19 now grants native-preserving source; §11.4 adopts its exact source/API
precedence and P3 adopts D-P1-49 jcpp admission. P10 §0.6 and P7 §0.44 adopt D-P1-50,
with P4 D-P4-28 supplying checked effective linked input. These remain unverified.
D-P1-52 completes the already-named `TextureParameters` contract for required synchronous
P5/P13 operation; optional P14 sampler/async architecture is not thereby approved.
D-P1-53 moves the real MOD-plugin/class-only veto to v0.1 with the approved geometry-only
adapter subset. The former empty-until-v0.3 plugin schedule cannot protect those early hooks.
Extended CLASSIC56 production and vertex-format transitions remain v0.3.
Historical addenda/review findings remain intact; fresh whole-document verification is required.

### 0.30 R27 native-array isolation correction — 2026-09-08

D-P1-55 addresses R27-1 and the identical NS-1 finding in the native/lifecycle seam
review. §4.7.6 now isolates generic zero for conventional positions and admits every
capture array through one complete plan, with actual-predecessor restoration and rollback.
The original reviews and prior amendments remain historical; this is an authorized
architecture correction, not reviewer re-verification, runtime success or literal PASS.
§5 incorporates the changed contract; §8 specifies value-observing proof still to run.
§11.4 requests exact P10 adoption and P7 receiving containment, not a new renderer API.
The published ARB_vertex_shader revision 0.83 array and state-query clauses support the position
precedence, enabled-array capture and absence of a queryable current generic-zero value:
https://registry.khronos.org/OpenGL/extensions/ARB/ARB_vertex_shader.txt .
No implementation source was copied, and no build/test/lint/formatter or GL run was performed.

### 0.31 P3 R55 schema ownership receipt — 2026-09-08

D-P1-56 reads P3 D-P3-70's schema21 correction: payload-free profile selector,
usable base-or-override load classification and exact documented rectangle token.
These change no P1 facade type or operation. Current P3 version assertions below defer
to its binding §5.3/CURRENT_SCHEMA_VERSION instead of making foundation a second schema
authority. Native-source algebra and same-load asset ownership remain exactly granted.
R28's original literal PASS is preserved; this changed §5 receipt requires fresh review.

### 0.32 Attempt-5 synchronous texture receiving amendment — 2026-09-08

D-P1-63 grants P13 R5 C2's mandatory target-bearing synchronous value boundary with
P5 D-P5-42; no new verb or optional async API. D-P1-64 receives P14 D-P14-31's
always-current complete owned-object baseline, and D-P1-65 receives schema23, making
earlier numeric receipts historical. Governing RC2 seam/thread/failure constraints,
RESEARCH App B.4/F.5, complete affected allocation/parameter contracts and P13 R5
were the selected inputs. Native/options/assets/parameter-domain authority is unchanged.
Changed §5 is unverified; fresh owner/receiver review and later runtime proof remain
required. No validation commands, build/tests, implementation edits or PASS claim.

### 0.33 Attempt-6 corrections and complete-plan receipt — 2026-09-08

D-P1-66 resolves R33 C33-1 with captured target maxima; D-P1-67 grants P5 R43's
mandatory typed clear; D-P1-68 receives P10 D-P10-29's complete source-participation
plan. RC2 Phase1 seam/failure constraints, P5's RC3 clear/format contract, affected
owner declarations and the cited Khronos native specifications govern these changes.
Historical pinned-source confidence and R33 N33-1 remain separate from current
corroboration; no old path/commit claim is reauthenticated here. Live §4/§5/§8/§12
change together; reviews remain historical and fresh applicable review is still required.
No implementation, validation command, native execution or new PASS is claimed.

### 0.34 Attempt-7 clear/recorder/lifetime corrections — 2026-09-08

D-P1-69/70 resolve R34's two corrections in actual algorithms, exact fixture
declaration, §5, cases and checklist; D-P1-71 receives P14's target-materialization
and deletion law without changing create inputs. P10's real late BLOCK brightness
completion is distinguished from partial ITEM ingress. Selected RC2 Phase1 authority,
R34, affected load-bearing P1/P10 contracts and the cited Khronos discard specification
were read; original pin/license qualifications and report verdicts remain historical.
P5/P7/P10/P14/P2 receiving deltas require fresh review. No validation command,
implementation, runtime proof or PASS is claimed.

### 0.35 Attempt-8 confinement/label corrections — 2026-09-08

D-P1-72 resolves R35 C1 by restating C-3's confinement as the package tree
`com.schmaloogium.mod.glue` and its subpackages at the four normative sites — §4.3's softer
layer (`:2537-2539`), the C-3 restatement (`:2551-2552`), §8.1's `SeamLwjglConfinementTest` row
(`:5708`) and §5.1's seam row (`:5460`). D-P1-73 resolves R35 C2 by giving D-P1-71's label limb
its full recorded-only form at §4.7.3 (`:3143-3149`), §4.7.4's creation-site instruction
(`:3836-3840`), §4.7.8's restatement (`:4720-4727`) and §5.2's materialization row (`:5509`).
Inputs read: `PHASE_1_REVIEW_35.md` and the RC2 Phase 1 specification; P14 owns the label law
(D-P14-40/41), and P5/P13/P2 consume the no-queued-`GLError` drain guarantee while every phase
inheriting C-3 reads the tree form. This addendum and §3's `blend.<prog>` coordinate repoints
(RESEARCH.md `:1517` and `:430`; row now at `:1906`) were applied by the review-36 fix-up
session. R35's verdict and all earlier review verdicts remain historical; fresh whole-document
review and final integration are still required. No build, test, implementation or PASS
claim accompanies these documentation edits.

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
- The GPL-3.0-or-later license posture (D-7). The `LICENSE` swap itself is **already executed in the
  repository** (commit `aa917a6`), so what this phase owns is the residual REV2 names — the or-later
  statement, the source-header convention and the `mcmod.info` metadata — plus the third-party-notice
  mechanism (§4.8, §11.2's D-7 row).
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
| Stage registry contents, all classic catalog slots, backup-chain semantics, compile/link flow | **Phase 4** |
| Legacy geometry source preservation/translation and source-map/fingerprint production | **Phase 3**; complete core translation is not established by its two-span contract. The requested native-preserving materialization is ungranted (§5.2) |
| Legacy geometry topology selection, capability decision, pre-link invocation and program fallback | **Phase 4**; Phase 1 supplies only the engine-enum operation and its backend/recorder semantics (§4.7.4) |
| All GL *policy*: texture formats, the fixed texture-unit map, ping-pong/flip rules, clear colors, buffer sizing, resize | **Phase 5** (buffers) and **Phase 6** (uniforms/samplers). The unit map's own split, because four other sites delegate to this row: **Phase 5** owns *which texture object backs each unit per stage* (`DESIGN.md` l. 1488), **Phase 6** owns *pointing the sampler uniforms at the units* (l. 1563) |
| The Mixin **hook catalog** — which classes, which methods, which `@At` targets (App E) | **Phase 7** (with additions from **Phase 10** and **Phase 13**) |
| GL context creation mechanics, HiDPI, resize (OQ-3); CleanMix divergences on hot injections (OQ-4) | **Phase 7** |
| Shadow policy, camera/celestial math, traversal and pass lifecycle; Minecraft/Forge/LWJGL adapters; shadow Mixins and accessors | **Phase 8**. Phase 1 grants only package placement and seam constraints |
| Coexistence **policy**: which mod ids bail, detection mechanics, the user-visible message text (OQ-5) | **Phase 10** |
| Vertex layout/producers, mesh epochs, declaration plans, shader lighting and topology | **Phase 10**, with P7 activation/lifecycle policy. P1 grants packages, borrowed-source input state and recording (§4.7.6); native conversion has the narrow approved §11.4 authority and still requires owner adoption/runtime proof |
| GUI framework evaluation — whether ModularUI is fit for generated screens (OQ-9) | **Phase 12** |
| Texture systems: noise generation, `_n`/`_s` companion atlases, custom-texture loading, platform adapters and dumb hooks | **Phase 13**. Phase 1 supplies package placement, seam constraints and transfer verbs (§4.7.4), not texture policy; unit-map ownership stays with Phases 5/6 (§1.2 above) |
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
| `com.schmaloogium.engine.frame` | frame orchestration policy, lifecycle state, and closed frame results; no Minecraft, Forge, Mixin, or LWJGL types | Phase 7 |
| `com.schmaloogium.engine.uniforms` | built-in uniform model, cadences, smoothing math, value-provider interfaces | Phase 6 |
| `com.schmaloogium.engine.shadow` | shadow policy, camera/celestial math, traversal, pass lifecycle, and closed results | Phase 8 |
| `com.schmaloogium.engine.vertex` | pure layout/field/epoch values, quad math, mesh policy and input plans; `.internal` for private stack/range/layout implementation | Phase 10 (D-P1-50) |
| `com.schmaloogium.engine.expr` | custom-uniform expression language | Phase 11 |
| `com.schmaloogium.engine.textures` | companion-atlas planning, noise generation, custom-texture resolution, `.mcmeta` interpretation, overlay model, `atlasSize` values, and closed results/failures; no Minecraft, Forge, Cleanroom, Mixin, or LWJGL types | Phase 13 |
| `com.schmaloogium.engine.gl` | **the GL facade** — interfaces, capability/parameter values, recording/replay; P14's exact pure modernization values granted by D-P1-54/§4.7.8 | **Phase 1**, Phase 14 values only |
| `com.schmaloogium.engine.log` | the zero-dependency `Log`/`LogSink` SPI and channel constants | **Phase 1** |
| `com.schmaloogium.engine.diag` | `EngineDiagnostic` and the user-facing-channel vocabulary | **Phase 1** |

**`:mod`** — the Cleanroom mod. Depends on `:engine`.

| Package | Contents | Filled by |
|---|---|---|
| `com.schmaloogium.mod.core` | `@Mod` entry, lifecycle, config, engine bootstrapping | Phase 1 (skeleton) / Phase 7 |
| `com.schmaloogium.mod.glue` | adapters: world-state sampling, Forge registries, resources, **the LWJGL3 implementation of `engine.gl`** | Phases 1 (facade impl shape), 6, 7, 8, 9 |
| `com.schmaloogium.mod.glue.frame` | Minecraft/Forge frame adapters and the LWJGL/platform bridge for Phase 7; no engine policy | Phase 7 |
| `com.schmaloogium.mod.glue.shadow` | Minecraft traversal/draw/state, Forge render-pass, and facade-backed shadow adapters; no engine policy | Phase 8 |
| `com.schmaloogium.mod.glue.vertex` | builder/state/VBO/list sidecars, format/cache/ordinal/task adapters and native vertex-input implementation; no engine policy | Phase 10 (D-P1-50) |
| `com.schmaloogium.mod.glue.textures` | atlas/resource-manager adapters, Forge stitch-event listener, and facade-backed texture uploader; no engine policy or direct GL outside the facade implementation | Phase 13 |
| `com.schmaloogium.mod.glue.gl` | Phase14 sampler/DSA/debug/readback/worker helper mechanisms; existing Lwjgl3GLDevice stays Phase1-owned in mod.glue and delegates | Phase 14, D-P1-54 placement only; operation/worker gates remain |
| `com.schmaloogium.mod.mixin` | all Mixin classes, SRG-targeted, declared via the `MixinConfigs` manifest attribute | Phases 7, 8, 10, 13 |
| `com.schmaloogium.mod.mixin.frame` | dumb Phase 7 redirects, injections, and accessors; no policy or retained frame state | Phase 7 |
| `com.schmaloogium.mod.mixin.shadow` | dumb Phase 8 shadow redirects and accessors; no policy or retained frame state | Phase 8 |
| `com.schmaloogium.mod.mixin.compat.vertex` | dumb MOD-phase vertex hooks, collectively gated by the existing plugin; no renderer or format policy | Phase 10 (D-P1-50) |
| `com.schmaloogium.mod.mixin.textures` | dumb Phase 13 texture accessors and tick hooks; observe and delegate, no texture policy or retained lifecycle state | Phase 13 |
| `com.schmaloogium.mod.gui` | pack selection + options screens | Phase 12 |
| `com.schmaloogium.mod.compat` | coexistence detection, **bail registry** | Phase 1 (mechanism) / Phase 10 (policy) |
| `com.schmaloogium.mod.conformance` | runner-launched capture agent and dumb capture-plan/manifest transport; never linked from the `:conformance` module | Phase 2 (wire design) / Phase 7 (client host) |

**`:conformance`** — the Phase 2 harness. Depends on `:engine`. Never ships in the mod jar.
Phase 1 stands up the module, its JUnit wiring, and its dependency edge; Phase 2 fills it.
Phase 7's capture agent has the exact package home `com.schmaloogium.mod.conformance`; it is a
`:mod`-compiled, runner-launched entry point and must not create a `:conformance` → `:mod` dependency.

**Closed placement grants.** Phase 7 R7-8 is already satisfied by the four existing frame/capture
entries; Phase 13 R3 adds only the three `textures` entries above (`[D-P1-43]`). Every existing
grant, including the parent `mod.mixin` allocation, remains valid. The new texture homes confer no
additional API or dependency permission: C-1 through C-4, engine `.internal` privacy, GL through
the facade, and §4.5.2a's Mixin config/package agreement apply unchanged. No new conformance
package or module edge is needed; `mod.conformance` remains in `:mod`, not `:conformance`.
Placement grants are distinct from verified availability; §0.24 states the fresh-review gate.

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

**REV1 adds external evidence, and it cuts both ways — which is why both halves are recorded.**
`DESIGN.md` §G3.1 (ll. 456–464) and §G10's OQ-20 row (l. 827) now cite Pintonium as evidence that
seam-hardness is *achievable*: a single version-agnostic shader core driving radically different MC
backends behind ~3 service interfaces (PD §2, inventoried in §4.12). That is real support for the
premise this section argues from — a backend swap behind a seam is a thing that has been done, not
only a thing that has been planned. **The other half is the one that matters to us**, and §G3.1
states it: their core *"is **not** headless-testable"* — `common-shaders` compiles against LWJGL3 and
Embeddium GL directly and calls GL statically throughout. So the reference validates the
**portability** half of D-6 and says nothing whatever about the **testability** half, which is
D-10's, and which our module seam buys and their service seam does not. The honest summary: the
evidence supports the goal and not the mechanism; our seam is strictly stronger for D-10, and §10.3's
drill remains the only thing that will tell us whether it is strong *enough*. `[V:observed — PD §2]`

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
| `GLDevice` | `engine.gl` | Root facade handle; eight services including the bounded v0.1 vertex-input service (§4.7.6), capability profile and GL-error drain |
| `GLCapabilityProfile` | `engine.gl` | Immutable value object; RESEARCH.md §4.1's probe set + extension set + the macro-header fields (`glslVersion`, `vendor`, `renderer`); serializable as a test fixture |
| `ProgramHandle`, `ShaderHandle`, `TextureHandle`, `BorrowedDepthAttachmentHandle`, `FramebufferHandle`, `UniformLocation` | `engine.gl` | Opaque handles — the engine never holds a raw GL int. **Four** direct `GLHandle` categories; the borrowed-depth marker is a narrower `TextureHandle`, not a fifth category, and there is no renderbuffer (§4.7.3) |
| `LegacyGeometryInputPrimitive`, `LegacyGeometryOutputPrimitive` | `engine.gl` | Closed pre-link parameter domains, respectively `TRIANGLES` and `TRIANGLE_STRIP`; no GL enum values or Phase 3 types cross the facade |
| `LinkedGeometryInputPrimitive`, `FullscreenPrimitive` | `engine.gl` | Closed linked-result/script and selected-route vocabulary (§4.7.4a), not new pre-link parameters |
| `VertexInputService`, `VertexSource`, `VertexBinding`, `VertexBindMode`, `VertexBindResult`, `VertexBindRejection` | `engine.gl` | Bounded borrowed-source input state; complete shapes/lifetimes in §4.7.6, P10 policy/types remain `engine.vertex` |
| `ForeignTextureProvider`, `ForeignTextures` | `engine.gl` | The slot a `mod.glue` implementation fills with ordinary `TextureHandle`s for textures **Minecraft** owns (`[D-P1-36]`, §4.7.3, §4.12). *Which* textures is not this phase's: the unit-map keys are **Phase 5's**, the `minecraft:`-resource-location keys **Phase 13's**. Those handles are **bind-and-label-only**, distinct from authenticated borrowed depth, and outside the owned-handle lifetime rule (§4.7.3) |
| `GLError`, `GLErrorKind` | `engine.gl` | Driver errors as data. Attribution is per **drain window** — one call named when the window held one mutating **facade** call, the sweep named when it held many (`[D-P1-32]`); a window may also hold an error no facade call caused, since the GL flag is per-context (§4.7.4) — and this is the signal §G2.4's **rung 2** acts on; rung 1 is Phase 11's expression isolation and never reaches GL (§6) |
| `ReplayAwareGLError` | `engine.gl` | One drained `GLError` plus the caller-established replay-attribution verdict. `attributed` is true only when isolated replay reproduces that error in the named call's window; labels, operation names, and clean or ambiguous replays never imply attribution (`[D-P1-42]`) |
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
| **Mipmap generation requires GL 3.0** | RESEARCH.md §4.1 step 1 | `GLCapabilityProfile.supportsMipmapGeneration()` — derived, `atLeast(3,0)`; consumed by **Phase 7's** per-pass composite mipmap generation (`DESIGN.md` l. 1683, in Phase 7's *Scope — in* under **Composite/final execution**), off the per-slot **composite-mipmap bitmask Phase 4** carries (l. 1334) which **Phase 3** parses (`colortexNMipmapEnabled`, RESEARCH.md App A.3 l. 1185), and by **Phase 8** for the per-config mipmap generation on shadow textures (l. 1828). **Phase 5 has no composite-mipmap policy to consume it** — the only occurrence of "mipmap" in Phase 5's whole spec (ll. 1409–1525) is l. 1469's shadow-FBO *"per-texture nearest/mipmap filter"* **config**, which Phase 8 then generates against. The attribution to Phase 5 this cell carried through the §0.12 revision was this row's alone, and §3's own `scale.<prog>` and `countInstances` rows already route the two directives sitting in the *same* `DESIGN.md` bullet to Phase 7 on exactly this reasoning (V13-4) | `[V:observed]` |
| Extension set available to the engine | `DESIGN.md` Phase 1 scope **plus RESEARCH.md §3.5** — REV2 splits the citation at ll. 992–997 (§4.1 lists four probes and does not include the extension set), closing §3.1's flagged delta | `GLCapabilityProfile.extensions()` + `hasExtension(String)`; the consumer is Phase 3's on-demand `MC_<GL_extension>` macros (RESEARCH.md §3.5) | `[A]` |
| **Standard macro header** — `MC_GL_VERSION`, `MC_GLSL_VERSION`, `MC_GL_VENDOR_*`, `MC_GL_RENDERER_*` injected after `#version` in every pack shader | RESEARCH.md §3.5 | `GLCapabilityProfile.glVersionMajor`/`.glVersionMinor` (`MC_GL_VERSION`), `.glslVersion`, `.vendor`, `.renderer` — the profile carries every input the header needs; Phase 3 formats the macros and owns `MC_VERSION`, `MC_OS_*` and the option macros, none of which are GL state | `[V:doc]` |
| `GL_MAX_VERTEX_ATTRIBS` / `GL_MAX_TEXTURE_SIZE` available to the engine | Additive to RESEARCH.md §4.1's probe set, on the same reasoning as the extension set (§4.7.2) | `GLCapabilityProfile.maxVertexAttribs` (Phase 10's extended vertex format binds at 10/11/12 and will grow), `.maxTextureSize` (Phase 5's buffer sizing) | `[A]` |
| `shaders.debug.save` equivalent — dump processed sources | RESEARCH.md App F.8; DESIGN.md §G4.5 | `-Dschmaloogium.debug.saveSources` reserved in §4.9.3's flag namespace; the dump itself is Phase 3's | `[V:doc]` |
| **KHR_debug labels/groups in dev** — the other affordance §G4.5 reserves from day one | DESIGN.md §G4.5 | `DebugService` (§4.7.4) exists as an interface at v0.1 so call sites can label objects immediately; `-Dschmaloogium.debug.glLabels` (§4.9.3) gates it; the implementation is `v0.5` / Phase 14 (§9) | `[V:design]` |
| Declaring `centerDepthSmooth` enables a **center-depth readback** | RESEARCH.md §3.2, App A.3 for the directive; §4.4 for the synchronous per-frame stall `[V:observed]` | `FramebufferService.readDepthPixel(...)` (§4.7.4) — the verb only; which pixel, at which moment, and the halflife smoothing are Phase 6's policy. The PBO/fence async form is RESEARCH.md §6.2's modernization, deferred to Phase 14 | `[V:doc]` |
| `depthtex0` is the real, sampleable main-depth attachment | RESEARCH.md §4.3 and App B.2 | Facade shape only: `borrowDepthAttachment`, `attachDepth`, and `attachDepthStencil`; Phase 5 owns format, freshness, reattachment, and Minecraft lifetime policy (`[D-P1-40]`) | `[V:observed]`; operation shape is a downstream request |
| `depthtex1`/`depthtex2` receive the documented depth moments | RESEARCH.md App B.2 | Facade shape only: distinct initialization and steady-copy operations; Phase 5 owns allocation, format, copy tier, cadence, and restoration (`[D-P1-40]`) | `[V:doc]`; operation shape is a downstream request |
| Noise texture and pack custom textures require **texel upload** | RESEARCH.md §4.1 step 4 ("create noise texture"), App F.5 (`texture.noise`, custom-texture source forms) | `TextureService.upload(TextureHandle, TextureData)` (§4.7.4) — the verb only; generation, formats and unit assignment are Phase 13's (with Phase 5 owning formats) | `[V:observed]` |
| **App B.3's fixed unit map names textures Minecraft owns** — unit 0 `texture` (the block atlas) and unit 1 `lightmap` on GBUFFERS/SHADOW programs | RESEARCH.md App **B.3**, the unit map packs rely on numerically; the gap was surfaced by §4.12's PD §2 completeness check | `TextureService.bindToUnit(int, TextureHandle)` supplies the **binding**; the `TextureHandle` for a vanilla-owned texture comes from a **`mod.glue` implementation of `ForeignTextureProvider`** (§4.7.3), not from `TextureService.create` (§4.12, `[D-P1-36]`, §5.1). **No facade verb is added** — an `adopt(int glName)` form would put a raw GL name in an `:engine` signature, which §4.7.3 and `[D-P1-15]` exist to prevent. Which textures the map needs, and the map itself, stay Phase 5/6 policy (§1.2) | `[V:doc]` for the map; `[V:observed — PD §2]` for the inventory that found the gap; §G11.4 decision `[D-P1-36]` |
| `atlasSize` / `eyeBrightness` are **`ivec2`** uniforms | RESEARCH.md App **D.3** (`atlasSize`, `terrainTextureSize` — camera/matrices/screen) and App **D.1** (`eyeBrightness`, `eyeBrightnessSmooth` — held item/player) | `UniformService.upload(loc, int, int)` (§4.7.4); the values and their cadences are Phase 6's | `[V:doc]` |
| `blendFunc` is an **`ivec4`** uniform (current blend `srcRGB`, `dstRGB`, `srcA`, `dstA`) | RESEARCH.md App **D.4**, §3.4 | `UniformService.upload(loc, int, int, int, int)` (§4.7.4). Phase 6 owns the value provider and its cadence (`DESIGN.md` §G5.1 puts App D's inventory at **v0.1**). The value is *observed* from `GlStateManager` per §G4.6 — that observation is Phase 6's, not the facade's. **Phase 9 is named nowhere in this row, and the omission is deliberate** (V11-1): `DESIGN.md` routes `blendFunc` to Phase 6 in five independent passages and to Phase 9 in none — Phase 6's *Scope — in* (l. 1601), its cadence model (ll. 1549–1550, where the "at their hooks (Phases 7/9/10 invoke)" gloss names *invokers* of a whole list, not owners of one uniform), Phase 9's own *Scope — in* (ll. 1918–1920, which enumerates its per-draw dynamics exhaustively and excludes `blendFunc`), Phase 9's *Scope — out* (ll. 1931–1932, "uniform upload mechanics (Phase 6)"), and the dropped-item audit (l. 2419, "`blendFunc`/GlStateManager → P6 + G4.6"). **REV2 adds the sixth and makes the duty concrete:** the observation must be *wired*, not assumed — Phase 6 designs the notifier, Phase 7 owns the hook that feeds it, and Phase 6's doc carries a notifier→producer audit table (§G4.6 ll. 554–557; Phase 6's spec ll. 1602–1607). The failure that rule is drawn from is Pintonium's `blendFunc` notifier, never assigned on 1.12.2, so any pack declaring the uniform NPEs at program build — PD §17 B6, a standing do-not-inherit row (§G11.4). Phase 1 handles it by supplying the `ivec4` verb and **not** claiming the observation for the facade | `[V:doc]`; the notifier-wiring duty is `[V:observed — PD §17 B6]`, cited from PD as a pointer to Phase 6/7 rather than re-verified at Pintonium source, because no Phase 1 design element rests on it (§G1.1's load-bearing test) |
| **Appendix A.1 fixed-function terminals** — external `<none>` and an absent `shadow` or `gbuffers_basic` root | RESEARCH.md App **A.1**, `docs/research/v1/RESEARCH.md:1106`–`:1145` | `ShaderService.useFixedFunction()` (§4.7.4, `[D-P1-39]`) selects program zero without exposing zero, a null, or a sentinel `ProgramHandle` to `:engine`. Phase 4 alone decides when its resolved binding is a fixed terminal and invokes this verb inside `ProgramStateBarrier`; the facade owns only the selection operation | `[V:doc]` |
| **Absent `final` terminal** — passthrough copy, not fixed-function selection | RESEARCH.md App **A.1**, `docs/research/v1/RESEARCH.md:1139`–`:1140` | Downstream final-pass execution owns the passthrough copy. It does **not** invoke `ShaderService.useFixedFunction()` merely because `final` is absent | `[V:doc]` |
| Geometry programs may declare the **ARB form** (`#extension GL_ARB_geometry_shader4` + `const int maxVerticesOut = N`) | `docs/research/v1/RESEARCH.md:213-215` says layout qualifiers **or** the ARB form; `:1161` names the legacy configuration; `:771` lists internal translation as an opportunity | `ShaderService.configureLegacyGeometry` supplies the native pre-link parameters through engine enums (§4.7.4, D-P1-44). Core-form sources retain their layout path. Phase 3 legacy-preserving output and Phase 4 adoption remain ungranted; neither two-span rewriting nor this facade grant alone establishes dual-form conformance (§5.2) | `[V:doc]`; route is D-P1-44 |
| **Compat-profile baseline; `GL_QUADS` stays available** | RESEARCH.md §6.1 `[D-9]` | The facade is profile-agnostic by construction: `DrawService` exposes a `fullscreenQuad` primitive whose backend chooses `GL_QUADS` or the triangle-strip fallback. No core-profile-only entry point appears in any interface | `[V:doc]` |
| **No UBOs**; per-program uniform upload with location caching | RESEARCH.md §6.1 `[V:doc]` | `UniformService` exposes only default-block uniform uploads keyed by `UniformLocation`. No uniform-block entry point exists — the facade cannot express a UBO | `[V:doc]` |
| **Never compile against `org.lwjglx`** | RESEARCH.md §6.1, DESIGN.md §G2.2 | `enable_lwjglx=false` (§4.6) plus the §4.3 bytecode assertion, which lists `org.lwjglx` among the forbidden prefixes | `[V:mcp]` |
| **All engine GL goes through the facade**; no direct LWJGL outside `mod.glue` | DESIGN.md §G4.6 | The §4.3 bytecode assertion enforces the `:engine` half mechanically. The `mod.glue`-only half is a convention plus a `:mod` scan restricted to `org.lwjgl` references outside `com.schmaloogium.mod.glue`. §4.7.4 adds the other half of §G4.6 — the backend's obligation to issue `GlStateManager`-cached state through `GlStateManager` | `[V:design]` |
| `const int countInstances = N` — **instanced re-render** with an incrementing `instanceId`, on a **composite/deferred** program | RESEARCH.md §3.2 and App A.3 for the directive; App **D.4** declares `instanceId` an `int` **uniform** ("0 original, 1..N instanced copies"). **the only observed form is RESEARCH.md §4.4's**, which is the only place the instancing *loop* is observed — "optional sub-viewport (`scale.<prog>`), `countInstances` instancing loop", in the composite-pass line | A **caller-side loop** over `DrawService.fullscreenQuad()` with `UniformService.upload(instanceIdLoc, i)` between copies. GLSL 120 has no `gl_InstanceID` (RESEARCH.md §3.5), so no single instanced draw can vary the uniform per copy — which is why the facade carries **no** instanced verb (`[D-P1-33]`, and §4.7.4's absent-verbs table says so). **The loop is Phase 7's, at `[v0.5]`**: `DESIGN.md` Part II names the `countInstances` instancing loop in Phase 7's *Scope — in*, under **Composite/final execution**, tagged `[v0.5]` there, and says it a second time and more explicitly in Phase 4's *Scope — in* — *"`countInstances` exposure to the pass executor (**execution is Phase 7, tag v0.5**)"* — which is the strongest citation on the point and the source of the milestone. Phase 5 — which owns the buffer estate the composite passes read and write — has no pass-execution bullet at all and puts "when copies/clears *happen* in the frame" in its *Scope — out*. The `instanceId` **upload** the loop makes between copies is **Phase 6's** entry point: `DESIGN.md` Phase 6's cadence model carries `instanceId` among the per-draw dynamics "at their hooks (Phases 7/9/10 invoke)" | `[V:doc]` |
| `const int countInstances = N` on a **gbuffers/shadow** program | RESEARCH §3.2/App A.3 require repeated vertex-stage geometry without program restriction; explicit maintainer choice on 2026-09-07 settles the submission boundary | P3 detects, P4 carries the effective provider's count, P7 policy/P10 existing adapters repeat prepared native submissions N times at v0.5; P8 traverses once. No new facade verb; complete authentication/restoration contract §5.2/§11.4 | D-P1-47; ordering is maintainer-approved, not reference-observed |
| `alphaTest.<prog>` — per-program alpha-test state | RESEARCH.md App F.7 | `StateService.alphaTest(AlphaTestState)` (§4.7.4) — the verb only. **The "which program carries which value is Phase 5/6 policy" clause this row carried through the §0.13 revision was wrong (V14-2):** `DESIGN.md` routes the directive **3 (parse), 4 (apply), 7 (execute)** — its coverage row says exactly that (l. 2410), the value is parsed and stored by **Phase 3** (*"per-program render-state overrides (alphaTest/blend/scale/flip/enabled — stored; applied by Phase 4)"*, l. 1245), carried as per-slot registry state and locked at the use-program barrier by **Phase 4** (ll. 1334, 1365 — the *"per-program alpha/blend lock"* is one of the barrier's stated obligations), and executed by **Phase 7**. "alphaTest" occurs nowhere in `DESIGN.md` outside ll. 1245 and 2410, and "blend" occurs zero times in Phase 5's whole spec (ll. 1409–1525) — the 5/6 attribution was this row's alone, the same defect class V13-4 fixed in the mipmap row against the same coverage table | `[V:doc]` for the directive; the routing is `[V:design]` (`DESIGN.md` ll. 1245, 1334, 1365, 2410) |
| `blend.<prog>` — per-program blend state | RESEARCH.md App F.7 | `StateService.blend(BlendState)` plus `snapshot()`/`restore()` (§4.7.4) — the verbs only; the routing is the row above's: parsed and stored by **Phase 3** (l. 1245), carried per slot and locked at the use-program barrier by **Phase 4** (ll. 1334, 1365), executed by **Phase 7** (l. 2410). **The "per-buffer routing decision is Phase 5's" clause this row carried through the §0.13 revision is deleted (V14-2), and not re-homed:** App F.7's form is `blend.<prog>=off\|<src> <dst> [<srcA> <dstA>]` (RESEARCH.md l. 1517) — per-program, with **no per-buffer axis to route**. Per-buffer blending in this project's sources is RESEARCH.md §3.6.7's Iris-side `PER_BUFFER_BLENDING` feature flag (l. 430), modern-superset material assigned to no phase at v0.1; its one adjacency in `DESIGN.md` is l. 1337's per-buffer `BufferBlendOverride` inside Phase **4**'s registry bullet's REV1 Pintonium cross-check — an inventory to check the slot model against, not an assignment, and in Phase 4's bullet, not Phase 5's | `[V:doc]` for the directive; the routing is `[V:design]` (ll. 1245, 1334, 1365, 2410) |
| R32 active mechanism correction | D-P1-57 | The preceding immediate verbs do not implement duration locks: P4 now uses lockAlphaBlend/AlphaBlendOverride and concrete HEAD interception; historical ownership explanations remain | Unverified |
| `scale.<prog>` — per-program sub-viewport | RESEARCH.md App F.7 | `StateService.viewport(x, y, w, h)` (§4.7.4), which §4.7.4's inclusion criterion already names the sub-viewport as its reason for existing. **Who computes the rectangle, this document does not say, and the reason is that `DESIGN.md` does not either.** Its three inputs have named owners — the **scale factor** is parsed and stored by **Phase 3** (*"per-program render-state overrides (alphaTest/blend/**scale**/flip/enabled — stored; applied by Phase 4)"*) and carried per slot by **Phase 4** (*"scale/flip storage"*); the **buffer dimensions** it multiplies are **Phase 5's** (*"Sizing: display size × render-quality multiplier; `superSamplingLevel`"*); and applying the result is **Phase 7's**, whose *Scope — in* part (a) lists *"`scale.<prog>` sub-viewports [v0.5]"* under **Composite/final execution**. Phase 5's *Scope — in* has no per-program sub-viewport bullet. The multiplication itself is assigned nowhere, so naming an owner here would be this document ruling on another phase's boundary — §G5.3's integration review is where a seam this shape gets settled | `[V:doc]` for the verb and the directive; the ownership is **`DESIGN.md`'s silence**, reported rather than filled |
| Pack-facing vocabulary used **verbatim** in identifiers | DESIGN.md §G4.1 | The facade deliberately contains no pack vocabulary at all (it is below that layer), so no synonym risk is introduced here. The phases that do carry pack vocabulary (3, 5, 6) inherit §G4.1 directly | `[V:design]` |

### 3.1 Flagged delta — **resolved upstream in REV2** (reported, not smoothed over — §G1.1)

**The delta as this document found it.** `DESIGN.md`'s Phase 1 scope added "extension set" to
`GLCapabilityProfile` and attributed the whole list to RESEARCH.md §4.1's probe set, which does not
contain it. RESEARCH.md §4.1 names four probes (GL version, `GL_MAX_DRAW_BUFFERS`,
`GL_MAX_COLOR_ATTACHMENTS`, `GL_MAX_TEXTURE_IMAGE_UNITS`) plus the GL-3.0 mipmap gate; the design
revision this document was built against read *"GL version, max draw buffers, max color attachments,
max texture units, extension set — the §4.1 probe set"* (v1.1 l. 614 — a **v1.1** coordinate, kept as
the record of what was found and dead against every later revision).

**Ruling, made here and unchanged by the resolution:** include the extension set. Per §G0.1,
RESEARCH.md wins on conflict — but this is an *addition*, not a contradiction: §4.1 describes what the
reference implementation probes at startup, while RESEARCH.md §3.5 independently requires that
`MC_<GL_extension>` macros be emitted on demand, which is impossible without an extension set. The
attribution was loose; the requirement is real and sourced elsewhere in RESEARCH.md. Recorded as `[A]`
provenance rather than `[V:observed]`, so no later reader mistakes it for observed reference behavior.

**Upstream resolution (REV2).** §11.5 item 3 asked for *"a half-sentence correction"* and REV2 makes
it, naming the request. The Phase 1 scope bullet now reads *"max texture units — the §4.1 probe set —
plus the extension set, whose contract surface is §3.5's on-demand `MC_<GL_extension>` macros;
**REV2** citation split resolving PHASE_1_DOC §11.5 item 3: §4.1 lists four probes and does not
include the extension set"* (`DESIGN.md` ll. 992–997). The split adopted upstream is the one this
section ruled for, so the delta is **closed rather than carried**: no design element changes, the
`[A]` tag stays — the requirement is still sourced from §3.5 rather than observed — and §11.3 item 1
and §11.5 item 3 are dispositioned to match. The finding is kept rather than deleted because a closed
contradiction that leaves no trace is one the next reader re-derives from scratch.

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

**Active migration override (D-P1-51).** This table is July evidence, not the current
executable configuration. Apply §4.2.6a's September pin and script-placement ledger when
performing the split; do not restore the old loader/plugin/wrapper or explicit Mixin dependency.

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
    id 'xyz.wagyourtail.unimined'                version '1.4.36-kappa' apply false
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

```

`idea-ext` stays applied at root because the `idea.project.settings` block is a root-only concept;
its `runConfigurations` entries are retargeted at `:mod`'s tasks (`:mod:runClient`,
`:mod:runServer`). The template's `moduleJavacAdditionalOptions` key `project.name + '.main'` becomes
per-module keys (`engine.main`, `mod.main`, `conformance.main`) — recorded because it is a silent
breakage otherwise.

`extra.gradle` is **not** applied to the source-set-free root. Its current Buildship
callback requires `sourceSets.main` and `generateJavaTemplates`, so it moves to `:mod`
after Unimined/Blossom setup (§4.2.6a). The root-only IDEA configuration remains here;
its `genSources` trigger targets `:mod:genSources`, not a nonexistent aggregator task.

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
    // P3 production dependency is admitted by D-P1-49, after its verified pin is recorded.
    // jcpp_version is one exact implementation-selected version, never a dynamic selector.
    implementation "org.anarres:jcpp:${jcpp_version}"
    // C-1 remains enforced over the complete main compile/runtime dependency closure.
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

The template's manifest branches remain. Shadow/coremod stay inert
(`enable_shadow=false`, `is_coremod=false`); `contain` is now activated solely for
D-P1-49's verified third-party closure, not for the first-party engine module.

`gradle/scripts/dependencies.gradle` and `gradle/scripts/publishing.gradle` are applied **from
`:mod` only**. The current dependency script has the conditional lwjglx line and
`modLibrary 'mezz:jei:4.33.0:dev'`, but no explicit sponge-mixin row. Preserve the current
Unimined-provided dependency model; do not resurrect July's row (§4.2.6a). `publishing.gradle` publishes the mod
artifact; `:engine` is not published separately (it is not a library anyone else consumes — it ships
inside the mod jar).
Apply `rootProject.file('gradle/scripts/extra.gradle')` from this project after its
Unimined/Blossom setup under §4.2.6a, preserving the current Buildship callback exactly.

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

#### 4.2.4b P3 jcpp admission and pin-at-implementation rule (D-P1-49)

P3 §5.4 requests precisely `org.anarres:jcpp` in pure `:engine` production scope,
not a runtime facade. Granted at v0.1 as `implementation`, with one exact
`jcpp_version` property selected and verified by the implementation session before
P3 code lands. RC2 §G7 item 7 / §G11.2 rule 4 supplies the Apache-2.0 candidate
admission; this is not a claim that a particular release or transitive graph was inspected now.
Record the exact version, repository/POM/artifact identity, full runtime dependency closure,
licenses/notices and verification outcome in `PINS.md` and the existing `THIRD-PARTY.md`
mechanism. No range, latest selector, snapshot or inferred Pintonium-vendored version.
If verification cannot establish a compatible pure-JVM closure, the dependency remains
implementation-blocked; do not weaken C-1 or adopt a prohibited transformer to unblock it.

The seam forbids platform dependencies, not all production libraries. C-1 continues
to scan the actual main compile/runtime closure and bytecode unchanged: jcpp and its
verified pure-JVM transitives pass normally, never by an allowlist that bypasses
Minecraft/Forge/Cleanroom/Mixin/LWJGL checks. No jcpp type enters public engine APIs;
P3 owns adapters, preprocessing behavior and diagnostics. No new module edge.

Merging first-party `:engine` classes does not ship its external runtime libraries.
The `:mod` packaging step must include the verified jcpp runtime closure once through
the existing third-party `contain` mechanism (not the first-party class merge), with
matching versions at engine compile, headless runtime and client runtime. Exclude duplicate
artifacts already deliberately supplied at the identical pinned runtime identity only with
recorded verification; do not rely on Minecraft accidentally supplying a library.
Preserve upstream LICENSE/NOTICE material and list shipped artifacts. This admits no
extra library merely because jcpp's POM happens to mention it; its resolved closure
must satisfy the same licensing/seam verification before inclusion.

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
D-P1-49's third-party jcpp closure uses `contain` separately; this section's rejection
of first-party engine containment and its flat engine-class merge remain unchanged.

#### 4.2.6 Pin table (OQ-2)

**Historical pin evidence follows.** The July values and verification claims are preserved.
The active September configuration and after-split instructions are §4.2.6a; its explicit
overrides win over this table, §10.1's old pin and every inherited implementation example.

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

#### 4.2.6a Current executable migration ledger — 2026-09-07 (D-P1-51)

`[V:repo 2026-09-07]` file inspection, **not** a new Maven/release availability check:

| Component | Current executable observation | Binding split instruction |
|---|---|---|
| Gradle wrapper | `gradle/wrapper/gradle-wrapper.properties`: `gradle-9.7.0-bin.zip` | Preserve **9.7.0**; no downgrade or latest-release upgrade |
| Unimined | `build.gradle`: **1.4.36-kappa** | Preserve exact plugin version in root `apply false`, apply only to `:mod` |
| Cleanroom | `build.gradle`: inline **0.6.10-alpha** | Move unchanged to `cleanroom_loader_version=0.6.10-alpha`, read only by `:mod` |
| Java / mappings | 25; MCP `stable`, `39-1.12` | Unchanged |
| Blossom / Shadow / idea-ext / foojay / JUnit | 2.2.0 / 9.5.1 / 1.4.1 / 1.0.0 / 6.0.3 | Unchanged; ASM 9.10.1 remains the separately planned historical test-only pin, not a newly observed executable dependency |
| Mixin dependency | Current `dependencies.gradle` has **no** explicit `sponge-mixin` declaration | Preserve current Unimined/loader resolution; do not add the July compileOnly row. Confirm resolved compile/refmap tooling and loader runtime at implementation, without bundling a competing Mixin runtime |
| Current mod library | `modLibrary 'mezz:jei:4.33.0:dev'` in dependency script | Preserve its existing mod-only configuration during the split; not an engine dependency or a new shader prerequisite |
| All three CI workflows | `setup-gradle` still says **9.6.1**, while build commands invoke `./gradlew` | Wrapper controls executed build version. Align setup inputs to **9.7.0** during implementation; do not claim current files already agree |
| Buildship | `extra.gradle` registers `eclipseDependencies`, lazy main compile-classpath provider, `eclipse.classpath.plusConfigurations`, and `generateJavaTemplates` synchronization | Apply exactly once from `:mod`, after Unimined/Blossom establish its main source set/template task, using `rootProject.file('gradle/scripts/extra.gradle')`. Never apply the source-set-consuming script to root or headless modules |

The script's `plugins.withId('eclipse')` guard defers Buildship setup until Eclipse is
applied; it does not create missing main source sets or template tasks. Applying it to
an Eclipse-imported aggregator would therefore reference absent model objects
(`[INFERENCE]` from the inspected callback, not a run result). Preserve lazy classpath
resolution and generated-source synchronization in the actual Minecraft-owning project.
Root IDEA setup retains its own `idea.project.settings`; headless projects retain ordinary
Java IDE models, not Minecraft/Blossom dependencies.

`PINS.md` creation must begin with this current-file baseline, explicitly labeled
"inspected, runtime/resolution verification pending", then append actual implementation
verification and jcpp pin results. Keep July rows as historical evidence, never re-date
them or invent a successful bump from them. §4.2.6's deliberate re-pin procedure remains;
this reconciliation performs **no bump** and does not declare any pin last-known-good by test.

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
outside `com.schmaloogium.mod.glue` and its subpackages, which is the mechanical half of §G4.6's
"no direct LWJGL calls outside `mod.glue`'s facade implementation."

#### The constraint, restated for later phases to inherit

> **C-1** `:engine`'s `main` compile and runtime classpaths contain no artifact matching the
> forbidden-coordinate list, and `:engine`'s compiled classes reference no type under
> `net.minecraft.`, `net.minecraftforge.`, `com.cleanroommc.`, `org.spongepowered.`, `org.lwjgl`,
> `org.lwjglx`, `zone.rong.mixinbooter`, or `cpw.mods.`.
>
> **C-2** `:mod`'s compiled classes reference no type matching
> `com.schmaloogium.engine.*.internal.*`.
>
> **C-3** `:mod`'s compiled classes reference no type under `org.lwjgl` outside
> `com.schmaloogium.mod.glue` and its subpackages.
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
| `cleanroom_loader_version` | *(absent — inline literal)* | **`0.6.10-alpha`** | new property retaining the current executable pin, §4.2.6a |
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
| `schmaloogium.preinit.mixin.json` | `@env(PRE_INIT)` | `com.schmaloogium.mod.mixin.preinit` | Reserved and empty; D-P1-50 grants the vertex family MOD placement, not preinit. Earlier attachment requires target evidence and a separate owner amendment. | `v0.1` |
| `schmaloogium.default.mixin.json` | `@env(DEFAULT)` | `com.schmaloogium.mod.mixin` | The bulk: render-loop hooks (Phase 7), shadow-pass hooks (Phase 8), texture hooks (Phase 13). | `v0.1` |
| `schmaloogium.mod.mixin.json` | `@env(MOD)` | `com.schmaloogium.mod.mixin.compat` | Coordinated P10 geometry-only hooks use the real class-only veto from v0.1. Plugin class and `plugin` key land together; CLASSIC56 hooks remain v0.3. No absent plugin class or permissive skeleton. | `v0.1` |

**All three files are `v0.1`**, and the tag is on the *file*, not on its first tenant — §G4.3 allows
each component exactly one tag. The `mixin_configs` manifest attribute names all three from v0.1
(§4.4.1, §12 item 31), and a manifest naming a config file that does not exist fails config load at
runtime — the same failure mode this table warns about for the `plugin` key. §9 carries the same
split: v0.1 geometry-only MOD family, v0.3 extended hooks.

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
better failure mode. D-P1-53 requires that functioning veto at v0.1 before any approved
geometry-only hook applies; P10 supplies its class-only policy through D-P1-50.

The MOD config's `plugin` key and actual `SchmaloogiumMixinPlugin` class ship together
at v0.1. DEFAULT/PRE_INIT receive no plugin key. This is not a returns-true skeleton:
it evaluates the registered early subset and retains the whole-family terminal veto.
It does not manufacture a runtime GL context or move CLASSIC56 production before v0.3.

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

#### 4.5.2a The class-scan alternative, evaluated and **rejected** (REV1)

REV2's Phase 1 spec directs this document to *"evaluate Pintonium's class-scan mixin plugin
(class-scans a package — no per-mixin JSON upkeep, PD §16) against the manifest canon; adopt or
reject with reasons."* Rejected, and the reasons are structural rather than a preference.

**What the mechanism actually is, read at source rather than from PD**
`[V:observed — Pintonium forge122/src/main/{java/org/taumc/celeritas/mixin/CeleritasVintageMixinPlugin.java,resources/mixins.celeritas.json}]`.
The 1.12.2 config declares `"plugin": "org.taumc.celeritas.mixin.CeleritasVintageMixinPlugin"`, a
`"package"` root, and an **empty `"client": []` array**; the plugin's `getMixins()` (l. 58) walks the
classpath URLs under that package — `Path.of(url.toURI())`, with jar and directory branches — strips
the `.class` suffix and returns the discovered list. So the JSON names *where*, the code enumerates
*what*, and there is genuinely no per-mixin upkeep. PD §16's description is accurate.

**Why it does not fit here, and the reason is our own `[D-P1-11]`.** Pintonium runs **one** config.
We run **three**, one per CleanMix phase, because the phase is the only axis CleanMix dispatches
configs on. A package scan answers *"which classes are mixins"*; it does not answer *"which CleanMix
phase does this mixin belong to"* — so a phase-aware scan would have to re-encode in Java the split
the three JSONs already express declaratively, keyed on something (a sub-package, an annotation, a
naming convention) that is itself upkeep, and upkeep in a place where a mistake fails at config load
rather than at review. The saving is exchanged for a worse version of the thing it saves.

**Three smaller reasons, none decisive alone.** The MOD-phase config's `plugin` slot is already
reserved for a *different* `IMixinConfigPlugin` duty — `shouldApplyMixin` consulting the bail
registry (§4.5.2) — and a class that both discovers and vetoes mixins is two responsibilities in the
one place where a failure is hardest to diagnose. D-5 budgets *"~25–30 targeted injections"* total,
so the `mixins` arrays this replaces are a few dozen lines across the project's whole life, not a
maintenance surface. And a declared array is **auditable by reading it**: §G5.3's integration review
and Phase 7's own catalog both benefit from a list that says what it contains without running a
classloader.

**What is kept from the finding, because the underlying risk is real.** The failure mode a scan
eliminates — an array drifting out of step with its package — is genuine, and the cheap insurance is
a **test, not a plugin**: a `:mod` test asserting that, for each config, the `@Mixin`-annotated classes
in its declared package **excluding any sub-package another config declares** are exactly the classes
its arrays name. **The exclusion is load-bearing, not tidy-up to be simplified away later** (V12-9):
observation 1 above records that DEFAULT's `com.schmaloogium.mod.mixin` is the *parent* of both other
configs' packages, so a plain subtree assertion would go **red on a correct config set** the first time
PRE_INIT or MOD acquires a tenant — correctly listed in its own arrays and correctly absent from
DEFAULT's — which is the first moment the test has anything to check at all. Observation 2's plugin
class needs no exception: an `IMixinConfigPlugin` is not `@Mixin`-annotated. It is one test, it runs
headless, it names the drift precisely, and it costs none of the above. Recorded as a §12 item rather than
folded into `[D-P1-11]`, since it is new work rather than a restatement. `[D-P1-38]`

*One incidental corroboration, recorded because §11.3 item 8 is an open spot check:* Pintonium's
1.12.2 config also carries `"compatibilityLevel": "JAVA_8"`, the same value this document inherited
from the template snapshot and flagged as underived. That is a working 1.12.2 shader engine using it,
which raises confidence without settling the question — the flag's meaning is about *mixin* bytecode,
and Pintonium's source level is not ours (PD §16 notes jvmdowngrader in their runtime). §11.3 item 8
stands as written.

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
"Refmaps are handled by Unimined at build" `[V:mcp]`. The current checkout has no explicit
`sponge-mixin` row; preserve its dependency model under §4.2.6a rather than recreate July's
compileOnly declaration. Loader-provided runtime and first-hook/refmap verification remain required.

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

**The posture has a working existence proof (REV1).** PD §16 records that Pintonium runs 1.12.2 on
**LWJGL3 via lwjgl3ify or Cleanroom**, with a coremod relocating LWJGL2 references in *other mods'*
classes, and calls Pintonium-on-Cleanroom-1.12.2 *"the existence proof for Schmaloogium's entire
platform bet (D-1, §G2.2)"*. Two consequences, neither of which changes `[D-P1-14]`. First,
"LWJGL3-native for our own code, runtime shim for everyone else's" is not a posture this document
invented — it is the deployed arrangement on this exact platform, which moves §10.4's runtime half
from open-and-untested to open-and-precedented. Second, and more useful to the spike: the shimming
happens **in a coremod relocating other mods' references**, not in the loader intercepting GL calls
generically. That is a *narrower* mechanism than the one RESEARCH.md §5.3 flags as unverified
("what lwjglx intercepts at runtime"), and it makes §10.4 step 4 a falsifiable prediction rather than
an open comparison: a relocating coremod has no reason to change what the driver reports to classes
that never referenced LWJGL2, so the two `GLCapabilityProfile`s should be **identical**, and a
difference would mean the mechanism is not what PD describes. `[V:observed — PD §16]`, cited from PD
as platform context; no Pintonium source was read for it, because no design element here rests on how
their coremod is written.

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
        int max3DTextureSize,        // GL_MAX_3D_TEXTURE_SIZE; zero only unsupported
        int maxRectangleTextureSize, // GL_MAX_RECTANGLE_TEXTURE_SIZE; zero only unsupported
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

**Captured target limits — D-P1-66 (R33 C33-1).** The native probe admits 3D on
`atLeast(1,2)` and rectangle on `atLeast(3,1)` or any of
`GL_ARB_texture_rectangle`, `GL_EXT_texture_rectangle`, `GL_NV_texture_rectangle`.
These are target-support gates, not inferred maxima. Query `GL_MAX_3D_TEXTURE_SIZE`
and `GL_MAX_RECTANGLE_TEXTURE_SIZE` (the extension aliases have the same meaning)
only behind their respective gates. A false gate produces exactly zero and performs
no unsupported query or target call. A true gate requires a positive captured integer
and the target's required image/subimage entry points; missing functions, GL query
errors or nonpositive results fail capture and prevent device/profile publication.
They never masquerade as unsupported zero. 1D/2D retain positive `maxTextureSize`.
No maximum is guessed from another limit, a version or an extension string.
Allocation checks width for 1D and width/height for 2D against `maxTextureSize`;
all three 3D dimensions use `max3DTextureSize`; rectangle width/height use
`maxRectangleTextureSize`. Unused axes retain §4.7.7a's canonical values.
Native support rationale: [Khronos glGet](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glGet.xhtml)
and [ARB_texture_rectangle issue 12](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_texture_rectangle.txt).
This is a facade decision grounded in distinct native limits, not observed pack parity.

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
max.3DTextureSize    = 2048
max.rectangleTextureSize = 32768
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

D-P1-66 makes `max.3DTextureSize` and `max.rectangleTextureSize` mandatory exact,
case-sensitive keys. Values are base-10 nonnegative Java-int-range integers; zero is
legal iff the corresponding gate above is false. Existing positive `max.textureSize`
remains mandatory. Parse rejects missing/duplicate/malformed/negative/overflowing or
gate-inconsistent values, never substitutes old defaults. Canonical write sorts scalar
keys lexicographically, uses exactly `key = value` with LF endings, no padding, and
then the existing sorted `extensions =` block; the display above is an illustrative
profile, not an exception to canonical ordering. Round-trip preserves all captured
integers exactly. P2 captures/replays this complete profile and explicitly refreshes
old fixtures; recorder construction has no secondary target-limit scripts or GL query.
The native probe also verifies callable required clear entry points for the advertised
GL>=3.0 (`glClearBufferfv/iv/uiv`) or GL2 `GL_EXT_texture_integer`
(`glClearColorIiEXT/IuiEXT`) tier before publication (D-P1-67); advertised-but-broken
support fails capture, not a fabricated working recorder capability.

#### 4.7.3 Handles

```java
package com.schmaloogium.engine.gl;

/** Marker for every GL object the engine holds. Never an int. Sealed — and this is the one
 *  sealing that means something: the permits list IS the statement that there are exactly
 *  four handle categories and no renderbuffer (§12 item 18 checks the clause). */
public sealed interface GLHandle permits
        ProgramHandle, ShaderHandle, TextureHandle, FramebufferHandle {}

/** A direct subtype of a sealed type must be final, sealed or non-sealed; an interface
 *  cannot be final, and these four must be implementable by backends OUTSIDE this package
 *  — so non-sealed is the only correct modifier, not a relaxation. See below. */
public non-sealed interface ProgramHandle     extends GLHandle {}
public non-sealed interface ShaderHandle      extends GLHandle {}
public non-sealed interface TextureHandle     extends GLHandle {}
public non-sealed interface FramebufferHandle extends GLHandle {}

/** A platform-owned, sampleable main-depth texture. The public marker is necessary but
 *  never sufficient permission: each backend authenticates that it issued the concrete
 *  value for this GLDevice/context before accepting it at the narrow borrowed-depth
 *  call sites below ([D-P1-40]). Merely implementing this interface is a forgery. */
public interface BorrowedDepthAttachmentHandle extends TextureHandle {}

/** Deliberately NOT a GLHandle — a location is a lookup result, not an object. Not sealed
 *  either, and for the same reason: `[D-P1-34]` obliges every *backend* to implement it. */
public interface UniformLocation {
    /** True when the uniform was optimized out; uploads through it are no-ops. */
    boolean isAbsent();
}
```

**Each backend supplies its own implementations, and that is why only `GLHandle` is sealed.**
`Lwjgl3GLDevice` wraps ints; `RecordingGLDevice` wraps synthetic sequence numbers; a hypothetical
Kirino backend wraps whatever it uses (§10.3's drill assumes exactly that); and `mod.glue` wraps
the GL names of textures Minecraft owns (`[D-P1-36]`). **None of those can be a *permitted*
subtype.** For a sealed type in the **unnamed module** every permitted subtype must sit in the
sealed type's own **package**, and the unnamed module is what `:engine` compiles into, because
§4.3 *"considered and rejected"* JPMS (`[D-P1-6]`) — while `Lwjgl3GLDevice` is in `mod.glue` (§2.1,
§2.4) and `RecordingGLDevice` in `engine.gl.record` (§4.7.5), neither of them
`com.schmaloogium.engine.gl` and the first not even the same Gradle project. **So sealing the four
leaves would have made the arrangement this facade is built on uncompilable**, and it had read that
way since the build session's own listing: `PHASE_1_REVIEW_4.md`'s F3-6 quotes these four
declarations' literal `permits …` ellipses, and the ellipsis is what hid the defect rather than what
caused it — no filling-in of it could have made a `mod.glue` class a permitted subtype
(`[D-P1-15]`, V12-3). **The opaqueness property never lived
in `sealed`:** it lives in these interfaces exposing no accessor that returns a GL name, plus
`SeamBytecodeTest` (§4.3) — which is the same trade §4.3 already makes when it prefers a bytecode
scan to a language-level guarantee. `:engine` sees only the interfaces.

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

**Owned texture materialization — D-P1-71 (P14 lifecycle receipt).**
`TextureService.create(debugLabel)` creates only the authenticated logical handle and
retains its label; it chooses no target and creates/labels no native texture. First
admitted `allocate(t,spec)` materializes the exact `spec.target()` after preflight,
then applies the retained label to that object. The existing first depth initialization
likewise materializes its already-known 2D target. No target argument or raw-name
adoption is added to create. Failed preflight leaves the handle unmaterialized.
Deleting an unmaterialized handle retires it without a native texture-delete call.
Native deletion is lifetime-ending, not binding-neutral: affected bindings/cache entries
become zero, unrelated bindings remain, and cleanup never rebinds a deleted name.
Existing final-use/lease retirement applies before deletion; no async grant follows.
Failed first native storage retains its materialized name for owned cleanup but
publishes no usable storage; rebuilding uses a new logical handle, never retargets
a possibly typed failed name. Deletion preserves the active unit and unrelated bindings.
`DebugService.label` on a live-but-unmaterialized owned texture handle is legal and
recorded-only (D-P1-73): it updates the retained label, issues no GL, appends no
native-label event and queues no `GLError`, and the retained value — original or
updated — is applied exactly once by the first admitted materialization, after
exact-target materialization and before storage work; it is dropped if the handle is
deleted while still unmaterialized. Foreign and borrowed handles are already
materialized objects, so this limb changes nothing for them.

`UniformLocation.isAbsent()` is load-bearing and belongs here rather than in Phase 6: GLSL compilers
routinely optimize out unused uniforms, `glGetUniformLocation` returns `-1`, and the reference
implementation's per-program location caching (RESEARCH.md §4.2) depends on distinguishing "not
looked up yet" from "looked up, not present". Exposing that as a boolean instead of a sentinel
integer is exactly the kind of leak the opaque-handle decision is meant to prevent.

**Handles for textures the engine did not create — the type `[D-P1-36]`'s slot is made of.**
§4.12's completeness check found that App B.3's units 0 and 1 name **Minecraft's** textures, which
`TextureService.create` can never produce. The slot is declared here, beside the handle types,
because §5 promises a dependent everything it needs and §12 item 22b makes this a Phase 1 **v0.1**
deliverable — a named slot with no declaration would leave a Phase 5/6 session the one route
§G1.1 l. 252 forbids ("do not invent the missing interface as if it existed"):

```java
package com.schmaloogium.engine.gl;

/** Handles for GL textures Minecraft owns. Implemented in `mod.glue` — the only place a GL
 *  name may exist (C-1) — so no §4.7.4 verb changes and no `adopt(int)` appears anywhere
 *  (`[D-P1-36]`, §4.12). The handle is the SAME TYPE as an engine-created one and carries a
 *  NARROWER contract: bind-only, and outside the handle-lifetime rule above. See below. */
public interface ForeignTextureProvider {
    /** `key` is a pack-facing texture identifier used verbatim (§G4.1), in one of two
     *  vocabularies. They are disjoint by construction: no App B.3 name contains a colon
     *  and a resource location always carries one.
     *    (a) App B.3's sampler names, bare — "texture", "lightmap", … — the fixed unit map.
     *    (b) `minecraft:`-namespaced resource locations — App F.5's custom-texture forms
     *        that name a texture Minecraft owns and keeps LIVE, e.g.
     *        "minecraft:dynamic/lightmap_1" or an atlas path. A `minecraft:` form that is
     *        merely a static asset needs NO foreign handle: read it through the resource
     *        accessors and go through TextureService.create/upload like any pack PNG.
     *  Empty when the platform has no such texture yet, or none under that key — the caller
     *  degrades per §6 rather than assuming presence. Never exposes the GL name.
     *  WHICH keys each vocabulary contains is NOT this phase's. (a) is Phase 5's — it owns
     *  which texture object backs each unit per stage — with Phase 6 pointing the sampler
     *  uniforms at the units. (b) is Phase 13's, whose Scope — in carries App F.5's source
     *  forms and whose Scope — out excludes the unit map, so the two owners cannot collide.
     *  Phase 1 enumerates neither. */
    Optional<TextureHandle> handleFor(String key);
}

/** Where `:engine` holds it: the shape §4.9.1 already uses for `LogSink`, for the same
 *  reason — a `:mod`-implemented SPI that `:engine` reaches from wherever it binds. */
public final class ForeignTextures {
    public static void install(ForeignTextureProvider p);   // mod.core, at §4.13 stage 2
    public static ForeignTextureProvider active();          // before install: empty for every key
}
```

It must be an **`:engine`** type: a provider declared in `mod.glue` and consumed by `:engine` would
invert the `:mod → :engine` edge (§2.2, C-1), which is what makes the declaration Phase 1's rather
than a dependent's. `mod.core` installs it at **§4.13 stage 2**, where the GL context and the engine
bootstrap already are; resolution is **lazy per call**, so installation does not require the vanilla
textures to exist yet. Render thread, like every other `engine.gl` call (§7). Before install — and
in every headless test that does not script one — `active()` answers empty for every key, the same
degradation `LogSink` takes (§4.9.1, §6).

**Foreign handles are backend-authenticated and split into two closed permission classes
(`[D-P1-40]`).** The common `TextureHandle` supertype is what keeps the raw GL name on the `:mod`
side of C-1, but it cannot prove ownership or origin. Every accepting backend therefore classifies
the concrete value before GL: its own live engine-owned texture, its own ordinary
`ForeignTextureProvider` value, its own `BorrowedDepthAttachmentHandle`, a value issued by another
device/context, or an unknown/forged implementation. Wrong-device, wrong-context, forged, deleted,
and otherwise unknown values are rejected with `IllegalArgumentException` before any binding or
attachment mutation. The two legal foreign classes then have different, narrow permissions:

- **Ordinary foreign texture — bind-and-label-only, stated by closure over §4.7.4
  (V14-1, amended by §0.18):**
  `TextureService.bindToUnit(int, TextureHandle)` and `DebugService.label(…)` — and **nothing else in
  §4.7.4 that accepts a `TextureHandle`**. **Illegal:** `TextureService`'s
  `allocate`/`setParameters`/`upload`/`generateMipmap`/`delete`, and `FramebufferService`'s three
  attachment forms — `attachColor`/`attachDepth`/`attachDepthStencil`, which would make a
  vanilla-owned
  texture a render target of an engine FBO *and* capture the object in the FBO's attachment state,
  past the reach of the per-use resolution rule below, so the attachment dangles across a vanilla
  reload; and both `initializeDepthTextureFromFramebuffer` and `copyDepthToTexture`, the
  **destructive** cases whose `dst` is defined or written. A backend rejects every such use before
  GL rather than overwriting/deleting Minecraft state. §12 item 22b carries the closure, not the
  current list, as the review obligation.
- **Authenticated borrowed depth — sample, label, or depth-attach only.** A
  `BorrowedDepthAttachmentHandle` issued by this backend may be passed to `bindToUnit`,
  `DebugService.label`, `FramebufferService.attachDepth`, and
  `FramebufferService.attachDepthStencil`; it remains illegal everywhere else. The combined form
  additionally requires the authenticated metadata to say `DEPTH24_STENCIL8`; the depth-only form
  accepts either a depth-only or packed texture but attaches only its depth aspect. It is never a
  legal color attachment, allocation/parameter/upload/mipmap/delete target, or initialization/
  steady-copy destination. The provider version/identity and reattachment cadence stay Phase 5's;
  Phase 1 authenticates origin and permission only. §12 item 22c tests this matrix, including a
  user-defined marker implementation and a genuine marker from another recording device.
- **No `delete`, therefore no re-acquisition duty.** The engine did not create the object and never
  calls `delete` on either foreign class, so *"invalid the moment its `delete` returns"* has no
  trigger and **Phase 5's drop-and-re-create across an uninit/rebuild does not apply to foreign
  handles.** This is the sharp
  edge, and it is worth naming as a failure rather than as a rule: RESEARCH.md §4.1 step 5's uninit is
  *"delete all GL objects"* and a routine v0.1 event, and Phase 5 is simultaneously the phase that owns
  that loop and the phase that defines vocabulary (a) — so a delete walked over a unit table holding the
  unit-0 handle destroys **Minecraft's block atlas**, which is §6's rung-5 invariant broken outright,
  not a degradation.
- **Validity across a *vanilla* reload is the backend's problem, not the caller's.** Minecraft destroys
  and re-creates its own texture objects on a resource reload, on its own schedule and without telling
  the engine. So `mod.glue`'s handle **resolves the underlying object at each use** rather than
  capturing a GL name when it is handed out — the same lazy-per-call rule `handleFor` itself follows,
  extended from the lookup to the handle. A consumer may therefore cache a foreign handle for as long
  as it likes, and re-asking the provider is equally correct; what it must not do is infer a
  re-acquisition duty from the rule above.

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
    VertexInputService  vertexInputs();

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

Seven baseline services plus the bounded vertex-input service in §4.7.6, each a role
rather than a GL module. Load-bearing baseline signatures:

```java
public interface ShaderService {
    ShaderHandle  createShader(ShaderStage stage, String source);
    CompileResult compile(ShaderHandle shader);          // never throws
    ProgramHandle createProgram();
    void          attach(ProgramHandle p, ShaderHandle s);
    void          bindAttributeLocation(ProgramHandle p, int location, String name); // pre-link
    void          configureLegacyGeometry(ProgramHandle p,
                      LegacyGeometryInputPrimitive input,
                      LegacyGeometryOutputPrimitive output, int maxVerticesOut); // pre-link
    LinkResult    link(ProgramHandle p);                 // never throws
    Optional<LinkedGeometryInputPrimitive> linkedGeometryInput(ProgramHandle p);
    SamplerInitializationResult initializeSamplerUnits(
        ProgramHandle p, List<SamplerUnitAssignment> assignments);
    ValidateResult validate(ProgramHandle p);            // never throws
    void          use(ProgramHandle p);   // select a live linked shader program
    void          useFixedFunction();     // select program zero; no null/magic handle ([D-P1-39])
    void          delete(ProgramHandle p);
    void          delete(ShaderHandle s);
}

public enum LegacyGeometryInputPrimitive { TRIANGLES }
public enum LegacyGeometryOutputPrimitive { TRIANGLE_STRIP }

public record SamplerUnitAssignment(String exactName, int unit) {}
public sealed interface SamplerInitializationResult {
    record Completed() implements SamplerInitializationResult {}
    record Failed(String detail, boolean selectionRestored) implements SamplerInitializationResult {}
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

public sealed interface FramebufferDrawSlot {
    record Attachment(int index) implements FramebufferDrawSlot {}
    record None() implements FramebufferDrawSlot {}
}

public interface FramebufferService {
    FramebufferHandle create(String debugLabel);
    BorrowedDepthAttachmentHandle borrowDepthAttachment(TextureHandle platformTexture);
    void attachColor(FramebufferHandle f, int attachmentIndex, TextureHandle t);

    /** Replaces f's depth attachment and leaves f with no stencil attachment. `t` may be
     *  live and owned by this device or an authenticated same-device
     *  BorrowedDepthAttachmentHandle. Restores prior read/draw bindings before return. */
    void attachDepth(FramebufferHandle f, TextureHandle t);

    /** Replaces both depth and stencil attachments with the SAME DEPTH24_STENCIL8 texture.
     *  `t` may be live and owned by this device or an authenticated same-device borrowed
     *  depth handle whose backend metadata proves the combined format. Restores prior
     *  read/draw bindings before return. */
    void attachDepthStencil(FramebufferHandle f, TextureHandle t);

    /** Positional output locations; None consumes a location but names no attachment.
     * Empty means no writes, never preserve-current. No raw sentinel is accepted. */
    void drawBuffers(FramebufferHandle f, List<FramebufferDrawSlot> slots);

    FramebufferStatus check(FramebufferHandle f);
    void bind(FramebufferTarget target, FramebufferHandle f);

    /** Binds framebuffer **name 0**, the GL default framebuffer, on `target`. NOT "whatever
     *  the platform regards as its default target": Minecraft renders the world into a
     *  framebuffer object of its own, and no verb here binds that one — see §4.12's bucket
     *  row and the absent-verbs table below, both of which turn on this sentence. */
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

    /** First copy after destination creation/reallocation. Redefines owned dst level zero
     *  to region.width × region.height using src's exact depth or packed depth/stencil
     *  internal format, then copies the region's contents (glCopyTexImage2D semantics).
     *  It is never legal for any foreign destination. Restores prior read/draw framebuffer
     *  and texture bindings before return. */
    void initializeDepthTextureFromFramebuffer(
        FramebufferHandle src, TextureHandle dst, TextureRegion region);

    /** Copy a region of f's depth attachment into a standalone texture. This — not blit — is the
     *  verb the contract's depth copies need: depthtex1 and depthtex2 are copy-target *textures*,
     *  not attachments (RESEARCH.md §4.3), so there is no destination framebuffer to blit into and
     *  Phase 5 is not expected to invent one. STEADY COPY ONLY: dst must be an owned, live texture
     *  whose level zero is already defined with the same format and region extent; this operation
     *  changes contents only and never reallocates/redefines storage. Called mid-frame between two
     *  draws into the main FBO (RESEARCH.md §4.4), so it restores the caller's prior read/draw
     *  framebuffer and texture bindings. */
    void copyDepthToTexture(FramebufferHandle src, TextureHandle dst, TextureRegion region);

    /** Synchronous single-pixel depth readback from f's depth attachment. Returns immediately
     *  usable data and therefore stalls the pipeline — faithful to reference behavior
     *  (RESEARCH.md §4.4, §6.2). WHICH pixel, at which moment, and any halflife smoothing of the
     *  result are Phase 6 policy; this is only the verb. */
    float readDepthPixel(FramebufferHandle f, int x, int y);

    /** Exact owned color attachment, selected through its positional draw-route index.
     *  Clears its full extent and restores all private temporary state; §4.7.4b. */
    void clearColorAttachment(FramebufferHandle f, int drawBufferIndex, ColorClearValue value);

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

    void prepareUnitBindings(int occupiedUnitMask);
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
    AlphaBlendOverride lockAlphaBlend(
        Optional<AlphaTestState> alpha, Optional<BlendState> blend);
    BlendState effectiveBlend();

    void fog(FogState state);                 // null/absent = disabled

    /** Snapshot the state we are about to perturb, for the §G4.6 restore discipline.
     *  StateAspect is an engine enum with one constant per verb above. */
    StateSnapshot snapshot(EnumSet<StateAspect> aspects);
    void restore(StateSnapshot snapshot);
}

/** Backend-implemented opaque render-thread lease; obtain only from StateService. */
public interface AlphaBlendOverride extends AutoCloseable {
    @Override void close();
}

public interface DrawService {
    /** The composite/final full-screen pass primitive. The backend uses its active linked
     *  input requirement: TRIANGLES forces the existing triangle strip even where QUADS
     *  is available; non-geometry retains QUADS preference (§4.7.4a, D-P1-48).
     *  The engine never expresses that backend choice.
     *  It delegates the PRIMITIVE only: it establishes no draw state, and the caller is
     *  responsible for the composite state block through StateService.
     *  `const int countInstances = N` (RESEARCH.md §3.2) is NOT served by an instanced verb —
     *  see the absent-verbs table below and [D-P1-33]: on a COMPOSITE/DEFERRED program it is a
     *  caller-side loop over this primitive with an `instanceId` upload between copies, which is
     *  the only form RESEARCH.md §4.4 observes. The same directive on a gbuffers/shadow program
     *  re-renders VANILLA geometry and never reaches this verb at all — see §3's second row,
     *  [D-P1-47] and §11.4 for the approved prepared-submission owner/contract. */
    void fullscreenQuad();
}

public interface DebugService {
    void pushGroup(String label);
    void popGroup();
    void label(GLHandle handle, String label);
    boolean isActive();          // installed supported backend && glLabels; no debug-context prerequisite (§4.7.8)
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

/** Replay evidence for one triggering drained error. The caller, not the backend, performs the
 *  isolation replay and supplies the verdict. */
public record ReplayAwareGLError(GLError error, boolean attributed) {
    public ReplayAwareGLError {
        Objects.requireNonNull(error, "error");
    }
}
```

**Candidate sampler initialization — D-P1-59.** P4 alone calls
`initializeSamplerUnits` after successful link/linked-input checks and before validation.
P5 supplies the immutable complete fixed-unit assignments; P1 has no sampler-name policy.
Validate the live same-device linked program, render thread/context, non-null list/elements,
nonempty unique exact names and units 0–15 before GL. Invalid arguments follow existing
precondition rejection with no event/mutation. An empty assignment list completes without GL.
For nonempty input, retain the backend's authenticated previous program selection and linked
input metadata, drain/report preceding errors, and stop on a nonempty preceding drain.
Privately select the candidate, locate each name and upload its integer unless absent.
Drain each lookup/upload window; stop at the first error, preserving sanitized detail.
Always attempt previous-selection restoration and its immediate error check before return.
These private selection operations update the same backend tracker, but neither dispatch P4/P6
participants nor execute public fixed-function sampler normalization. Texture/sampler bindings,
active unit and alpha/blend state remain unchanged. No candidate location escapes.
Completed requires every active assignment initialized and exact previous selection restored.
Failed(detail,true) permits P4's candidate cleanup/fallback; failed or unprovable restoration
returns Failed(detail,false), invalidates remembered selection and poisons shader admission
until safe backend recovery/reconstruction. It cannot become a local successful fallback.
Backend exceptions are contained by this result; independent cleanup still runs. There is no
caller-side upload replay or fabricated attribution. The operation is render-thread-only even
if optional worker compilation is later enabled; it is not a runtime uniform initializer.

**Sampler normalization dispatch — D-P1-60.** `prepareUnitBindings` accepts exactly a
16-bit occupied-unit mask (bit n denotes fixed unit n), with thread/context/argument checks
before mutation. P5 calls it only after all-row successful binding preflight. Under P14's
admitted sampler strategy it clears native sampler bindings on unoccupied units 0–15;
under NONE there are no owned native samplers to clear. It does not bind textures, change
the active texture unit, allocate, or reparameterize borrowed objects. Driver failure uses
the existing drain/exception surface and prevents P5 Bound/ownership transfer.
Public `useFixedFunction` clears all strategy-owned sampler bindings on units 0–15 before
its successful return, so P4/P7's existing fixed-terminal and result-checked final release
also normalize before vanilla draws. Failure cannot authorize a fixed-function draw.
No implied frame callback or sampler-state leak through Unused rows remains.

Recorder additions mirror the contracts, not private native call counts:
`shaders.initializeSamplerUnits(program,assignments,previousSelection,result)` retains the
opaque previous shader-or-fixed selection and closed result; invalid requests append nothing.
`ScriptedResponses.samplerInitializationFails(programLabel,detail,selectionRestored)` drives
both failure dispositions; uniformAbsent covers optimized-out names. Default success requires
a live linked program and mirrors prior-selection restoration. `textures.prepareUnitBindings`
records the integer mask; existing glError scripting covers normalization failure.
The zero-argument public shaders.useFixedFunction event remains unchanged. Actual sampler/
uniform native dispatch counts require P14's separate backend evidence, not facade-event counts.

**Lease issuance — D-P1-58 (R30-1).** `AlphaBlendOverride` is an unsealed engine interface,
like the existing backend-implemented handle seams. The LWJGL backend in `mod.glue` and
recorder in `engine.gl.record` each return their own private implementation from
`StateService.lockAlphaBlend`; neither needs an engine-package constructor or factory.
The implementation retains its issuing device's private lease identity, saved state and
consumed flag. No operation accepts a caller-supplied lease to acquire, replace or release
another device's state. Implementing the interface cannot manufacture that private authority.
Closing a genuine lease follows the existing thread/lifetime, idempotence, rollback and
poisoning rules below. No native handle, public issuer or dependency on mod types enters engine.

**Duration override mechanism — D-P1-57.** `lockAlphaBlend` requires non-null optionals;
empty leaves that aspect ordinary, present disabled state locks OFF. One lease per device,
including an empty lease; nested acquisition rejects before mutation. Before writing, capture
the exact real alpha enable/function/reference and blend enable/separate RGB/alpha factors
for present aspects only. Retain disabled-state factors too. The backend owns private
render-thread guards and DEFAULT-config cancellable HEAD hooks in `mod.mixin.frame` for
GlStateManager enableAlpha, disableAlpha, alphaFunc, enableBlend, disableBlend, blendFunc
(all overloads) and tryBlendFuncSeparate (all overloads). P7 registers these concrete hooks;
their only decision is delegation to the P1 backend's held-aspect guard. Cancellation occurs
before cache/native mutation. Ordinary attempts, including immediate facade setters/restore,
are suppressed, not queued, for held aspects; unlocked aspects proceed normally.
Acquisition/close use an unforgeable private try/finally bypass depth, invoking GlStateManager,
not raw writes and not a public bypass API. This bypass skips interception, never cache updates.
Repair cache from the captured actual state before restoration if necessary so equality skipping
cannot leave native/cache divergence. Close restores only held aspects to the exact pre-lock
snapshot, never the last suppressed request; marks consumed and repeated close is harmless.
No new acquisition is allowed until close completes. A failed acquire rolls back all touched
aspects before throwing; close tries every held aspect even after one failure. The lease is
consumed on failed close and the device's shader-state admission is poisoned until safe
backend recovery, not silently unlocked into shader drawing. Rollback failure likewise poisons.
Drain native errors within acquire/restore before returning; failure throws IllegalStateException,
with errors retained for ordinary diagnostics. These transactional non-void/close operations
are exceptions to the ordinary void-verb batched-error rule.
`effectiveBlend()` returns the complete effective cached/native-coherent blend value, including
disabled factors. Existing P7 RETURN observations publish this value, never attempted arguments;
cancelled HEAD calls do not emit changes. Bypass-owned successful changes notify through the
same P7 effective-state path before participants/drawing; P4 also samples effectiveBlend before
its built-in participant. A notification failure prevents successful acquisition/activation and
uses the same rollback/poison containment. No P6 observer gains lock policy.

Draw-route validation copies the immutable list, rejects nulls, negative/out-of-capability
attachment indices, duplicate non-None indices and length above maxDrawBuffers before GL.
Repeated None is legal. The backend alone encodes Attachment as COLOR_ATTACHMENT0+index
and None as GL_NONE; empty uses the native no-write operation. Restore read/draw bindings.
Record `framebuffers.drawBuffers(f,slots)` with tagged positional values, and
`state.lockAlphaBlend(alpha,blend)` / `state.closeAlphaBlendOverride(leaseId)` with exact
predecessor/effective values and failure outcomes. Recorder models suppression, bypass,
rollback and poisoning; no-event precondition rejection is preserved.

The concrete mod-only bridge is `com.schmaloogium.mod.glue.AlphaBlendOverrideHooks`:
`public static boolean suppressAlphaMutation()` and `public static boolean suppressBlendMutation()`
return held-aspect && privateBypassDepth==0 on the installed device, false before installation;
`public static void publishEffectiveBlend()` forwards the installed device's effectiveBlend
to the existing P7→P6 effective-state event path after a successful native/cache change.
P7 HEAD hooks only cancel when the matching predicate is true; RETURN hooks call
publishEffectiveBlend only when not suppressed and when the effective value changed.
P1 acquire/close invokes that same publishing bridge after coherent writes, never by recursively
calling hook callbacks. The bridge owns no setter, lock values, queue or alternative lifetime.
It is mod-only and unavailable from engine/conformance; P1 recording models the events directly.

Design rules embedded above, each with a reason:

- **`compile` / `link` / `validate` return results; they never throw.** §G2.4's rung 3 requires that a
  program failing compile/link/validate deletes itself and reports a user-visible error. A checked
  exception crossing the facade would make that a control-flow problem instead of a data problem.
  `CompileResult`/`LinkResult` carry `success`, the driver log, and a `EngineDiagnostic` (§4.9).
- **Program selection has two explicit modes, and neither mode is the whole state barrier.**
  `use(p)` requires a live linked `ProgramHandle` and selects it; `useFixedFunction()` selects
  program zero. The backend owns that integer mapping: zero never appears in an `:engine` signature,
  is never wrapped in a `ProgramHandle`, and is never created or deleted. `use(null)` is invalid,
  and a synthetic sentinel handle is invalid, because either form makes fixed function participate
  in the handle-lifetime rules it does not have. These are the two selection operations *inside*
  Phase 4's universal `ProgramStateBarrier`; Phase 4 still owns shadow override, fallback resolution,
  sampler/uniform/custom refresh, and the per-program alpha/blend lock (RESEARCH.md §4.2;
  `docs/phase4/v1/PHASE_4_DOC.md:758`–`:821`). The facade does not silently absorb that policy
  merely because the old `use` comment called one low-level verb “the universal state barrier.”
- **Framebuffer depth operations validate ownership, format, extent, and origin before GL
  (`[D-P1-40]`).** Every framebuffer argument must be a live handle created by the receiving
  device. Every owned texture argument must be live and from that device. A borrowed-depth
  argument is legal only at `attachDepth`/`attachDepthStencil`, and only when the backend's private
  credential proves that it issued the concrete handle for the same device/context; the public
  marker alone proves nothing. A wrong-device owned handle, wrong-origin borrowed handle, or forged
  marker is rejected with `IllegalArgumentException` before a GL call, attachment change, or
  recorder mutation. `attachDepth` leaves the FBO depth-only by detaching any prior stencil
  attachment; `attachDepthStencil` requires one exact packed `DEPTH24_STENCIL8` object and installs
  that same object at both attachment points. Both restore the caller's prior read and draw
  framebuffer bindings in a `finally` path, including when the driver reports failure.

  Initialization and steady copy are deliberately different calls. The initialization operation
  accepts an owned destination only, validates a positive in-bounds source region, and redefines
  destination level zero to the region's extent and the source attachment's exact depth or packed
  depth/stencil format before copying contents. `copyDepthToTexture` accepts an owned destination
  only and requires an already-defined, equal-format, equal-extent level zero; it changes contents
  without redefining storage. Both restore prior read/draw framebuffer and texture bindings before
  return. A driver/capability failure remains observable through `drainErrors()` and leaves recovery
  to Phase 5's ledger/completeness rules; a precondition or provenance failure is rejected before
  GL and is mutation-free.
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
  is Phase 5/6/7 policy — and, for the per-program `alphaTest.<prog>`/`blend.<prog>` override
  *values*, **Phase 3/4/7's**: parsed by 3, locked at Phase 4's use-program barrier, executed by 7
  (§3's rows, V14-2).
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
  presence now means Phase 4/5's object-creation sites label from day one, which is exactly the
  "architect now, implement later" rule of §G0.3. Since D-P1-71 the owned-texture order is
  label-then-materialize: pass the label to `TextureService.create(debugLabel)` at logical
  creation, or call `label(handle, "colortex0")` before the first admitted materialization, where
  it is recorded-only (D-P1-73) and applied by that materialization — never a live
  `glObjectLabel` against a name that does not exist yet.
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
- **Native legacy geometry has one narrow pre-link operation (D-P1-44).**
  `configureLegacyGeometry(p,input,output,maxVerticesOut)` replaces the former assumption that
  every ARB source can be translated upstream. The exact engine domains are `TRIANGLES` and
  `TRIANGLE_STRIP`; `maxVerticesOut` is a positive Java `int`, copied exactly from the
  Phase-4-validated Phase 3 declaration. The operation does not parse GLSL, choose topology,
  inject layouts, increase the GLSL version, or expose raw parameter names. Phase 1 imports
  no Phase 3/4 type to implement it. Core-layout geometry does not call this operation.

  **Order and ownership.** Render thread/current owning context only. The target is the live,
  unpublished program created by this device, with its geometry shader already attached and
  before its first `link` attempt. Null/foreign/forged handles, null enum arguments or nonpositive
  counts reject with `IllegalArgumentException`; deleted programs, no attached geometry stage,
  wrong thread/context or a prior link attempt reject with `IllegalStateException`. These are
  precondition errors, before driver calls, parameter mutation or recorder append. The backend
  keeps private issuance/liveness, attached-stage and link-attempt metadata for these checks;
  it never validates by the raw GL name alone. This narrow operation's checks do not change
  §4.7.3's general stale-handle disclaimer. Phase 4 invokes it once per fresh legacy program,
  after successful shader compilation/attachment and fixed attribute binding, before link.
  Repeating it before link replaces the complete parameter triple; there is no accumulated delta,
  reset operation, post-link reconfiguration, or ownership transfer. Deleting the program drops
  the metadata. No currently selected program, framebuffer, texture or vanilla cached state changes.

  **Capability and native mapping.** Phase 4 must first require
  `capabilities().hasExtension("GL_ARB_geometry_shader4")`; `atLeast(3,2)` is not a substitute.
  The LWJGL3 backend also checks the current context's actual extension/entry-point availability
  before dispatch. Absent support emits a queued `GLError` with this operation name,
  `INVALID_OPERATION`, the program label and a capability explanation, with no native parameter
  call. No synthetic success, core `glProgramParameteri` substitution, or guessed extension
  support is allowed. On support, only `mod.glue` maps the input to `GL_TRIANGLES`, output to
  `GL_TRIANGLE_STRIP`, and count unchanged, issuing `glProgramParameteriARB` on the same program
  with `GL_GEOMETRY_INPUT_TYPE_ARB`, `GL_GEOMETRY_OUTPUT_TYPE_ARB`, and
  `GL_GEOMETRY_VERTICES_OUT_ARB`, in that order. Core object creation/attach/link/validate/use/delete
  remain unchanged. This is backend implementation of an engine operation, not an ARB API
  exported to `:engine`.

  **Limits and failure.** No clamping and no driver limit guessed from the GL version.
  `MAX_GEOMETRY_OUTPUT_VERTICES_ARB` overflow is a parameter error; the active-varying-components
  product limit is also subject to link failure. The driver enforces both; headless callers
  script those failures rather than infer a limit absent from `GLCapabilityProfile`.
  Native errors follow the existing `drainErrors()` cadence; backend dispatch failures are
  caught and queued as `GLError` (`INVALID_OPERATION` for unavailable dispatch, `UNKNOWN` for
  other backend failures), never propagated as pack-facing exceptions. A valid invocation counts
  as one mutating facade call even if capability rejection prevents native mutation; queued errors
  must survive until the next drain, including under per-call debug checking.
  Partial parameter writes are not rolled back: this is an unpublished candidate, not live state.
  **Mandatory consumer transaction:** drain and handle preceding build errors, configure, then
  drain immediately before any link or other facade mutation. Any rejection or nonempty drain
  aborts this program, deletes its owned candidate objects and follows Phase 4's rung-3 fallback;
  never link a partial/default triple, retry it as core source, or disable only the geometry stage.
  A nonempty drain need not prove which native setter failed (or exclude a foreign error);
  conservative candidate rejection needs no such attribution. A clean drain permits link, whose
  `LinkResult` still decides actual GLSL/version/interface/total-output compatibility.

  The Khronos specification cited in §0.25 supplies the parameter and limit semantics:
  §2.16 says the input type "must be set before linking" and the total output limit can make
  `LinkProgram` fail. Its GL 3.2 interaction says source layouts take precedence over program
  parameters. Thus this operation cannot repair stripped extensions, extension-era built-ins
  left in core source, or conflicting source layouts. Phase 3 must publish the appropriate
  native legacy source first, and Phase 4 must select it explicitly (§5.2/§11.4).

#### 4.7.4a Linked primitive requirements and fullscreen submission (D-P1-48)

R26-1 changes no `fullscreenQuad()` signature and no topology policy of vanilla draws.
ARB_geometry_shader4 revision 26 §2.16.1/Errors permits TRIANGLES-input geometry with
TRIANGLES, TRIANGLE_STRIP or TRIANGLE_FAN, **not QUADS**. Its GL-3.2 dependency clause
distinguishes pre-link ARB parameters from actual linked properties; source layout can
override each parameter independently. The configured triple alone is therefore not an
adequate linked-state cache on GL 3.2+.

**Private backend state, render-thread/current-context owned.**

- Issued program metadata retains attached stages, the complete last clean native triple,
  first-link-attempt state and an immutable successful-link input requirement, keyed by
  issuance identity rather than a reusable GL integer. Configure is candidate-only and
  changes no active requirement. Capability/setter failure never produces clean metadata;
  the mandatory immediate drain/abort transaction remains binding.
- On successful driver link, a program without a geometry stage records `NO_GEOMETRY`.
  A geometry program on GL 3.2+ obtains its **effective linked input** once using the core
  `GL_GEOMETRY_INPUT_TYPE` query inside `link`, not the similarly named ARB parameter query.
  This covers native extension sources with layout overrides and ordinary core sources.
  Pre-3.2 native geometry uses the clean configured input committed by that link; source
  layouts are unavailable there. Unknown/failed metadata acquisition makes `LinkResult`
  unsuccessful, never a default-QUADS success. Failed link publishes no new executable
  requirement; Phase 4 deletes that candidate, never restores/reuses a previous executable.
- `ShaderService.linkedGeometryInput(p)` exposes only that immutable cached successful-link
  value to the compiler: `Optional.empty()` means **no geometry stage**, never unknown;
  a present value is the exact `LinkedGeometryInputPrimitive`. The enum contains only
  POINTS, LINES, LINES_ADJACENCY, TRIANGLES, TRIANGLES_ADJACENCY. It performs no GL
  call, source scan, selection or allocation of a new metadata object. Null/forged/
  wrong-device handle gives IllegalArgumentException; wrong thread/context, deleted,
  never-linked or failed-link handle gives IllegalStateException, all before GL.
  Failed metadata acquisition already makes `link` unsuccessful. Deletion/context
  retirement invalidates later inspection, but a copied enum value has no handle authority.
  P4 maps empty to NONE and present values exactly to its finalized P3-derived input
  category, comparing **before validate/drawable publication**. Mismatch fails the entire
  candidate with attributed diagnostics and normal cleanup/fallback; neither side is
  silently rewritten to match. This is a compile-time cached inspection grant, not
  an active-program query for P7/P10; those still receive P4's authenticated projection.
- `use(p)` authenticates this device's live successful-link record before dispatch.
  The backend confirms the selected native program at this **selection boundary** using
  `GL_CURRENT_PROGRAM`, then atomically updates its private active identity/requirement.
  `useFixedFunction()` uses the same path, confirming zero and recording `NO_GEOMETRY`,
  without creating a zero handle. This is one binding query per actual selection, not per
  draw and not a source scan; it is an explicit correctness cost, distinct from and not a
  change to the batched `glGetError` cadence. No query is inserted between countInstances
  copies. Known failed selection retains the confirmed prior selection; uncertain
  dispatch/query/context failure marks active state `UNKNOWN` and queues the existing
  operation-named `GLError`. No speculative requirement becomes active.
- Deleting an inactive program removes its metadata immediately. Deleting the active
  program first selects fixed function through that same backend path, then invalidates
  the issued program and its cached metadata even if native deletion is deferred by GL.
  A failed deselection leaves active state `UNKNOWN`, never a deleted-handle cache hit.
  Context teardown drops all records. Driver-name reuse cannot recover old metadata.
- P4/P7 nested restoration reselects the live predecessor via `use` or `useFixedFunction`,
  so the requirement is restored with the program, not copied independently from a caller
  snapshot. `StateService.snapshot/restore` still covers only its declared aspects and
  does **not** save programs. Backend-internal platform restoration must update/invalidate
  the same active tracker; arbitrary foreign-program restoration is `UNKNOWN` until an
  explicit engine selection. Engine-controlled fullscreen execution permits no untracked
  program mutation between activation and draw. Failure/restoration containment belongs
  to P4/P7, not a per-draw GL query intended to discover outside mutations.

**Dispatch.** `fullscreenQuad()` reads only that private active record. `NO_GEOMETRY`
keeps QUADS when supported, otherwise the retained four-vertex triangle strip.
`TRIANGLES` always uses the retained four-vertex triangle-strip implementation, including
on compatibility profiles supporting QUADS. Preserve its established vertex/UV ordering,
coverage and caller-owned ortho/state block; do not invent a triangle-list conversion.
Point, line or adjacency input, `UNKNOWN`, or an invalidated selected executable cannot
be served by this fullscreen primitive: queue `GLError("draw.fullscreenQuad", label,
INVALID_OPERATION, detail)` and issue no native draw. Native dispatch failure follows the
same drain channel. P7 must observe draw failure before reporting Completed or committing
flips/post-draw work; no retry with QUADS and no geometry-stage-only disable.
Any temporary backend vertex/current-value state used by the existing quad/strip implementation
is restored in finally; caller matrix/viewport/fog/depth/alpha/blend and program selection
are not changed by the primitive. Restoration failure invalidates admission and follows
the existing frame-off containment, never a reported successful pass.

**Recorder parity.** Add `LinkedGeometryInputPrimitive` in `engine.gl` with exactly
`POINTS`, `LINES`, `LINES_ADJACENCY`, `TRIANGLES`, `TRIANGLES_ADJACENCY`, and
`FullscreenPrimitive` with `QUADS`, `TRIANGLE_STRIP`. They are metadata/log vocabulary,
not new configurable primitive parameters. Add
`ScriptedResponses.linkedGeometryInput(String programLabel, LinkedGeometryInputPrimitive input)`.
The recorder commits the scripted effective input only on a successful link; an unoverridden
clean native triple supplies its TRIANGLES default. Core geometry without a scripted
linked input fails metadata acquisition rather than rescanning GLSL or guessing. Tests of
native source overrides must script the actual effective input, not the configured value.
Ordinary no-geometry links retain the existing default success.

Existing `glError` scripts for `shaders.use`, `shaders.useFixedFunction`, and
`draw.fullscreenQuad` drive failed selection/draw; uncertain selection marks `UNKNOWN`.
The recorder mirrors issuance, selection, deletion and nested re-selection.
`linkedGeometryInput` reads the same committed recorder record and appends the query-shaped
`GLCall("shaders.linkedGeometryInput", List.of(p, result))` for a valid request, where
`result` is the Optional enum value; invalid requests append nothing. It cannot mutate
active selection or error-drain cadence. Live recording copies the actual cached result.
For fullscreen submission it appends
`GLCall("draw.fullscreenQuad", List.of(FullscreenPrimitive.QUADS))` or the corresponding
`TRIANGLE_STRIP` argument only when that native submission is selected; rejected submission
records `GLCall("draw.fullscreenQuadRejected", List.of(reason))`, with the closed reason
name `INCOMPATIBLE_INPUT` or `UNKNOWN_PROGRAM`. Scripted native draw failure retains the
attempt event and queued error, not a fictional no-draw rollback. No raw integer, source,
client address or newly allocated geometry reaches the log. P2 must deliberately regenerate
affected synthetic call-log goldens under its existing explicit-update policy.
The live recorder observes the actual backend's selected route/outcome; it must not substitute
an all-success headless response for live metadata or failure.

#### 4.7.4b Mandatory typed color clear — D-P1-67 (P5 R43)

P5 §4.6 owns conversion and timing. The grant is
`FramebufferService.clearColorAttachment(FramebufferHandle f, int drawBufferIndex, ColorClearValue value)`.
The shared `engine.gl` sealed value has exactly three immutable four-component records:
`ColorClearValue.Floating(float r,float g,float b,float a)`,
`ColorClearValue.Signed(int r,int g,int b,int a)`, and
`ColorClearValue.Unsigned(long r,long g,long b,long a)`.
Floating components must be finite; unsigned components are mathematical integers in
`0..4294967295`, never signed-int reinterpretations in public data/logs.

Authenticate render thread/context, live same-device owned framebuffer and attached
owned defined storage (contents may be uninitialized: this call initializes them), then nonnull value/range, route and realized numeric class,
then capability, before mutation. The index is the **position** in that framebuffer's
established `List<FramebufferDrawSlot>`: require `0 <= index < maxDrawBuffers`,
within the list, and a non-None slot resolving to one color attachment. It is neither
logical colortex index nor the attachment ordinal. A hole rejects, not successful no-op.
Floating serves normalized/floating formats only; Signed/Unsigned serve matching integer
formats only. A mismatched type is rejected even if native GL would silently accept it.
Invalid handle/value/route/class uses IllegalArgumentException, expired/wrong-thread/context
IllegalStateException, unsupported valid request UnsupportedOperationException, with no event.
The ordinary state.clear(COLOR) path must reject an integer destination; it cannot substitute.

**Native tiers.** On GL>=3.0 use `glClearBufferfv/iv/uiv(GL_COLOR,index,rgba)`
respectively. On GL2 with `GL_EXT_texture_integer`, signed/unsigned use
`glClearColorIiEXT/IuiEXT` followed by `glClear(GL_COLOR_BUFFER_BIT)` with a private
draw route selecting **only the resolved attachment**. Legacy noninteger clears use
`glClearColor` plus that same single-attachment route. Floating attachment support
still requires its existing texture/color-buffer-float capability; where clamp control
exists temporarily disable fragment-color clamping so finite floating values are not
accidentally clamped. Without GL3 or EXT_texture_integer, integer allocation/clear
admission fails as unsupported before mutation; no admitted integer format is silently
downgraded and no engine caller issues raw GL. EXT_framebuffer_object or
ARB_framebuffer_object alone does not grant core glClearBuffer. These are mandatory
synchronous backend routes, not optional P14 modernization.
Normative [EXT_texture_integer §4.2.3](https://registry.khronos.org/OpenGL/extensions/EXT/EXT_texture_integer.txt)
defines integer setters and undefined float-to-integer clears; the
[glClearBuffer reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glClearBuffer.xhtml)
defines numeric classes, positional indices, masking and core availability from GL3.0.

**Full-extent/restoration transaction.** Before mutation snapshot actual read/draw FBO
bindings (combined binding on EXT-only contexts), destination route, viewport, scissor
enable/box, all affected color write masks (indexed only where supported), dithering,
framebuffer-sRGB, fragment-clamp and rasterizer-discard state where supported. Bind f,
disable scissor, dither, supported sRGB conversion and supported rasterizer discard,
enable all color channels for the addressed draw slot, and clear its full attachment
extent, never depth/stencil. The legacy single-route path enables slot zero's mask.
Do not change depth/stencil masks, program, texture bindings or alpha/blend locks.
Unsupported indexed/sRGB/clamp/discard state is neither queried nor changed.
**D-P1-69:** discard is legal on GL>=3.0 or `GL_EXT_transform_feedback` or
`GL_NV_transform_feedback`; use that capability-legal enum's actual enable value,
disable before every native tier and restore its exact predecessor independently in
finally. A failed save prevents clear; disable/restore errors follow the existing
failure/poison law. This is private normalization, not a public transform-feedback API.
The [EXT_transform_feedback “Discarding Rasterization” specification](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/EXT/EXT_transform_feedback.txt)
explicitly makes Clear ignored under enabled discard; a clean error drain alone
cannot establish initialized contents without this normalization.
Snapshot legacy clear-color state **losslessly**: on the legacy
compatibility path use checked native `glPushAttrib(GL_COLOR_BUFFER_BIT)` before
changing it and `glPopAttrib` in finally, preserving its original float/signed/unsigned
representation rather than converting through GetFloatv. Preserve/restore the parallel
GlStateManager cache exactly; cached setters use its sanctioned path, private native
push/pop restoration synchronizes its saved cache without reissuing a float clear setter.
Stack-save failure prevents clear; never pop a failed push.
Restore every touched state and route in finally on success, GL error or Java failure;
attempt independent restorations even after one fails, restoring f's route before prior
FBO bindings. Native restoration failure poisons draw admission. A void return alone
is not proof: P5 brackets each call with existing error drains, and consumes full-clear
only after all calls **and restoration** succeed. Existing exceptions/error events are
contained by P5 into BACKEND_FAILED; no fictional texel rollback.

**Recorder.** Identical handle/route/format/capability admission uses the captured profile
and authenticated allocation/route metadata. Record
`GLCall("framebuffers.clearColorAttachment", List.of(f, drawBufferIndex, value))`
only after admission; no pixels, pointer, raw GL enum or signed wrapping of unsigned
values. Append `GLCall("framebuffers.clearColorAttachment.restore", List.of(f, restored))`
with boolean restored from actual backend state or scripted outcome; native attempted
failure retains the attempted call. Existing
`glError("framebuffers.clearColorAttachment",fboLabel,kind)` and
`glError("framebuffers.clearColorAttachment.restore",fboLabel,kind)` script work and
restoration errors separately; a restoration error records false and poisons admission.
No script may turn a class mismatch or unsupported request into success.


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
| **Bind a framebuffer the engine did not create** — Minecraft's own world FBO | `bindDefault` is framebuffer **name 0** (see its javadoc) and `bind` needs a `FramebufferHandle`, which §4.7.4 yields only for framebuffers the engine created: the same shape of hole §4.12 found for `TextureHandle`, in the framebuffer direction. **The ground for the absence is that no verb here binds it and the bind has a legitimate home outside the facade — not that nobody needs it.** The need is real and it is v0.1: RESEARCH.md §4.3 **l. 526** is *"**Final** renders to the vanilla framebuffer (anaglyph-aware color masking)"*, and `DESIGN.md` states it on both sides of the seam untagged — Phase 5's *Scope — in* **l. 1486** (*"`Final` renders to the vanilla framebuffer (handoff contract with Phase 7)"*) and Phase 7's composite/final bullet **l. 1685**. It stays out because `DESIGN.md`'s own injection timeline already houses it in `:mod`: *"composite-all then final to the MC framebuffer"* at Phase 7's `renderWorldPass` **TAIL** (l. 1713), with *"rebind default FB"* at hook sites 3 and 4 (ll. 1709–1710) — the same pattern the colour-mask row above uses, and the reason this is an absent verb rather than a `[D-P1-36]`-shaped slot. **The `[A]`-tagged reason printed here through the §0.12 revision was wrong and is deleted (V13-1):** App E row 17's last column is headed *"Serves hook needs (§7.1)"*, so its `6` is **hook need 6** — *"composite/final at frame end"* (RESEARCH.md l. 813) — which catalogues `bindFramebuffer` as a site the mod must **hook**, not as evidence that vanilla's bracketing makes a rebind unnecessary. It does not: RESEARCH.md's per-frame flow ends *"COMPOSITE passes (fullscreen ping-pong …)"* → *"FINAL to screen"* (**ll. 545–546**) with no vanilla code between, and a GL framebuffer binding is sticky, so the binding in force when `final` runs is the last composite's colortex. **A `final` pass that does not rebind writes into the shader estate, not into Minecraft's framebuffer** — and nothing catches it, since `bindsBalanced()` sees facade-level calls only (§5.2), so a headless replay records a draw into the last composite's target and passes. If a facade verb is ever wanted anyway, the cheap answer is `[D-P1-36]`'s shape rather than a new verb: `mod.glue` implements `FramebufferHandle` for that FBO and `bind` takes it unchanged | **7** — and this is a **v0.1** obligation on you, not a someday one: RESEARCH.md §4.3's `final` renders to the vanilla framebuffer, `DESIGN.md` makes that a **handoff contract** between Phase 5 and Phase 7 (ll. 1486, 1685), and since no verb here binds it you arrange the bind yourself through vanilla's own path at l. 1713's TAIL site; **5**, if the buffer estate ever needs vanilla's FBO as a `blit`/`copyDepthToTexture` **source** |
| General pre-link parameter setter | Only the closed legacy geometry triple is served by `configureLegacyGeometry`; no arbitrary integer parameter-name/value escape hatch | **4**, for any separately justified additional parameter |
| **Instanced draw** (a `fullscreenQuadInstanced(int)`-shaped verb) | **It cannot express the directive it looks like it serves.** `const int countInstances = N` re-renders the geometry N times with an **incrementing `instanceId` uniform** (RESEARCH.md §3.2, App A.3), and App D.4 declares `instanceId` as an `int` *uniform* — one instanced draw cannot vary a uniform between copies, and the backend could not do it on the caller's behalf either, since it is handed no `UniformLocation`. GLSL 120 has no `gl_InstanceID` (RESEARCH.md §3.5), which is precisely why the contract carries a uniform at all. The faithful shape — **for the composite/deferred programs RESEARCH.md §4.4 observes the loop on** — is a caller-side loop over `fullscreenQuad()` with `UniformService.upload(instanceIdLoc, i)` between copies, which this facade already supports. The directive on a **gbuffers/shadow** program is not this row's subject and would not be served by an instanced verb either: that geometry is vanilla's and is drawn outside the facade (§3's second row, `[D-P1-35]`). A verb of this shape was present in the §0.5 revision, unspecified, and is deleted `[D-P1-33]`. Nobody **requests** this verb; what follows is who owns the served work instead. | **7** — the composite/final **execution** owner, `[v0.5]`: `DESIGN.md` names the `countInstances` instancing loop in Phase 7's *Scope — in* under composite/final execution, and says it a second time and more explicitly in Phase 4's, *"`countInstances` exposure to the pass executor (**execution is Phase 7, tag v0.5**)"* — which is where the milestone comes from. **6** owns the `instanceId` upload the loop makes between copies (`DESIGN.md` carries it among Phase 6's per-draw dynamics). **5** owns the buffer estate those passes read and write — the read/write/flip law the N draws run inside — but not the draw loop that runs them. An instanced verb itself is requested by **7**, and only if a future non-GLSL-120 path ever needs one |

Additions to this facade are expected and cheap; **silent** additions are not. A phase that needs a
verb adds it as a requested change in its own §5 (§5.2), and this document is amended by a fix-up
session — the same route §4.9.2 uses for a new log channel.

**§0.15 exercises that route for the first time.** Phase 4 requested fixed-function selection in
`docs/phase4/v1/PHASE_4_DOC.md:931`–`:946`; `ShaderService.useFixedFunction()` now serves it, so it is
not an absent-verb row. The operation is narrow: select program zero through the backend, with no raw
integer/null/sentinel handle in `:engine`. All barrier policy remains Phase 4's.

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
    public ScriptedResponses samplerInitializationFails(
        String programLabel, String detail, boolean selectionRestored);
    public ScriptedResponses linkedGeometryInput(
            String programLabel, LinkedGeometryInputPrimitive input);
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

- Both program-selection modes are mutating calls and are distinct in the log:
  `ShaderService.use(p)` appends `GLCall("shaders.use", List.of(p))`;
  `ShaderService.useFixedFunction()` appends
  `GLCall("shaders.useFixedFunction", List.of())`. The recorder never manufactures a program-zero
  handle. Consequently `calledInOrder("shaders.useFixedFunction", …)` and
  `neverCalled("shaders.use")` can prove a fixed terminal without a special assertion API, while
  `noUseAfterDelete()` examines the handle-bearing `shaders.use` call and treats the handle-free
  fixed-function selection as legal even after a prior program was deleted (`[D-P1-39]`).
- Legacy geometry records exactly
  `GLCall("shaders.configureLegacyGeometry", List.of(p,input,output,maxVerticesOut))`,
  one event per valid facade invocation, not three raw-GL events. The recorder enforces the
  same origin/liveness/stage/pre-link checks; precondition rejection appends nothing.
  Enum names and the exact decimal count render deterministically. Extension absence in the
  supplied profile queues `INVALID_OPERATION` without successful configuration; default success
  is available only when the profile contains `GL_ARB_geometry_shader4`.
  Existing `ScriptedResponses.glError("shaders.configureLegacyGeometry", programLabel, kind)`
  covers setter/dispatch failures at the immediate following drain; `linkFails` covers total-output
  or shader-interface rejection. No geometry-limit field is inferred in a recorded profile.
  Existing `calledInOrder`, argument inspection, `neverCalled`, `noLeakedObjects` and
  `noUseAfterDelete` prove configure-before-link and failure cleanup, without a new assertion API.
- Framebuffer depth operations also have exact, distinct stable names:
  `attachDepth` appends `GLCall("framebuffers.attachDepth", List.of(f,t,restoredRead,restoredDraw))`;
  `attachDepthStencil` appends
  `GLCall("framebuffers.attachDepthStencil", List.of(f,t,restoredRead,restoredDraw))`;
  initialization appends
  `GLCall("framebuffers.initializeDepthTextureFromFramebuffer",
  List.of(src,dst,region,restoredRead,restoredDraw,restoredTexture))`; and steady copy remains
  `framebuffers.copyDepthToTexture`. The recorder mints authenticated borrowed-depth handles with
  its own private device token for tests; it rejects a user-implemented marker and a handle minted
  by another recorder before appending anything. An authentic borrowed handle is never counted as
  created, leaked, or deletable by `noLeakedObjects()`/`noUseAfterDelete()`.
- Every mutating call appends a `GLCall`. Object-creation calls return a synthetic handle (a
  monotonic sequence number wrapped in the appropriate handle type). Handles are
  `equals`-comparable so assertions can
  say "the texture attached at index 2 is the one created third". **Sequence numbers are never
  reused, including after a `delete`** — which is what makes `noUseAfterDelete()` possible here and
  impossible in the LWJGL backend (§4.7.3).
- Every query-shaped call answers from the `GLCapabilityProfile` or the `ScriptedResponses`.
  Defaults are all-success except geometry links needing explicit effective-input metadata
  (§4.7.4a); that absence must not become a guessed successful executable.
- The log's rendered form is stable and deterministic: no timestamps, no identity hash codes, no
  iteration-order dependence. This is what makes it usable as a golden file, and it is a constraint,
  not an implementation note.
- **Bulk data is logged by summary, never by content.** A `TextureService.upload` call records its
  destination region, mip level, layout, texel count and a stable content hash — not the bytes.
  Depth initialization and steady copy record handles, exact format/extent, region, and restored
  binding identities, never copied depth values. Raw texel dumps would make `render()` unreadable
  and its golden files unreviewable, which would defeat the point of the format.
  `readDepthPixel` records the coordinates and the answer it gave.

Assertions:

```java
public final class ReplayAssertions {
    public static ReplayAssertions assertThat(GLCallLog log);

    public ReplayAssertions calledInOrder(String... opNames);
    public ReplayAssertions neverCalled(String opName);
    public ReplayAssertions bindsBalanced();       // every bind has a matching unbind/rebind
    public ReplayAssertions noLeakedObjects();     // every create has a matching delete
    public ReplayAssertions noUseAfterDelete();    // no handle appears in a call after its delete
    public ReplayAssertions drawBuffersWere(List<FramebufferDrawSlot> slots);
}
```

`bindsBalanced()` and `noLeakedObjects()` are named here because two later impl gates ask for exactly
them: Phase 5's "creates/destroys the full buffer estate for a classic pack without leaks", and the
general §G4.6 restore discipline. `noUseAfterDelete()` is the third, and it exists for the reason
§4.7.3 gives: the uninit/rebuild that an option toggle triggers is a v0.1 event, and a stale handle
fails *silently* against a live driver.

Phase 4's fixed-terminal replay needs no fifth assertion method. The exact zero-argument
`shaders.useFixedFunction` call above is part of the stable log vocabulary, so the existing
`calledInOrder`/`neverCalled` pair can distinguish it from `shaders.use`; the ordering assertion can
also place it after Phase 4's lock restoration and before any following state call. This is an
explicit reuse decision, not an omission from `ReplayAssertions`.

**What `bindsBalanced()` can and cannot see.** It reads the `GLCallLog`, so it sees facade-level
calls only. A binding perturbed *inside* the backend — `blit`, `attachDepth`,
`attachDepthStencil`, `initializeDepthTextureFromFramebuffer`, and `copyDepthToTexture` bind
objects to do their work — is invisible to it. That is why §4.7.4 states restoration as a
**contract on those verbs** rather than leaving it to this assertion, and why the recording backend
logs the restored binding identities explicitly. `calledInOrder` plus argument inspection can
therefore catch a backend/recorder contract mismatch even though a live driver's internal binds are
not independently visible to replay.

**The fixture-production loop.** `mod.glue.CapabilityProbe` builds a `GLCapabilityProfile` from a
live context at display init and, under `-Dschmaloogium.debug.dumpCapabilities`, writes it in the
§4.7.2 text form. A developer with a given GPU runs the client once and contributes a profile
fixture. Without this, the recorded profiles Phase 2 replays would have to be hand-written, and
hand-written capability sets are exactly where wrong assumptions hide.

#### 4.7.6 Bounded vertex input grant — R10-1 (D-P1-50)

The eighth service is `GLDevice.vertexInputs()`. This is the exact requested input-state
boundary, not buffer ownership, a renderer extension or a new draw API. Public types
below live in `engine.gl`; `VertexLayout`, `VertexInputPlan` and `VertexGeometryInput`
are the P10-owned pure `engine.vertex` values, not Minecraft/GL types.

```java
public interface VertexInputService {
    VertexBindResult bind(VertexSource source, VertexLayout layout,
                          VertexInputPlan plan, VertexBindMode mode);
    void restore(VertexBinding binding);
}
public enum VertexBindMode { LIVE_DRAW, LIST_CAPTURE, LIST_REPLAY_GUARD }
public enum VertexBindRejection {
    INVALID_SOURCE, STALE_SOURCE, INVALID_LAYOUT, INVALID_PLAN,
    WRONG_THREAD, OUT_OF_RANGE, UNSUPPORTED_INPUT
}
public sealed interface VertexBindResult {
    record Bound(VertexBinding binding) implements VertexBindResult {}
    record Rejected(VertexBindRejection reason) implements VertexBindResult {}
    record Failed(String diagnosticId) implements VertexBindResult {}
}
public interface VertexBinding {} // opaque; public implementation is not authority
public sealed interface VertexSource {
    non-sealed interface ClientRange extends VertexSource {}
    non-sealed interface BorrowedVbo extends VertexSource {}
    non-sealed interface DisplayListReplay extends VertexSource {}
}
```

**D-P1-68 — complete conventional participation receipt (P10 D-P10-29/R10-9).**
Consume P10's exact
`VertexInputPlan(String layoutFingerprint,List<AttributePointer> pointers,Set<ConventionalInput> conventionalInputs,VertexGeometryInput expectedGeometryInput)`,
with `ConventionalInput { POSITION,COLOR,UV0,UV1,NORMAL }`. All collections are
defensively copied immutable/non-null with no null members. The mask derives only
from authenticated **completed producer** semantics: COLOR/UV1 participate iff the
producer supplies them, including P10 D-P10-30's authenticated ITEM BakedQuad append
into BLOCK followed by successful real four-vertex brightness completion. The partial
ITEM descriptor alone is not final participation; filler or arbitrary union grants none.
Incomplete/throwing completion cannot issue a sealed source or captured plan.
NORMAL remains supplied-or-existing-generated quad normal; POSITION/UV0 and all other established behavior are unchanged.
CLASSIC_56 remains physically56 bytes; padding is not source participation.

LIVE_DRAW receives the whole plan for client and VBO paths, temporarily disabling
absent COLOR/UV1 arrays without pointer setup or current-value writes, and restoring
actual enables/selectors/affected state. P10's Forge projection must skip absent setup
and postDraw reset. LIST_CAPTURE uses the same mask plus its prepared generic union
as the complete allowlist; absent color/lightmap arrays and baked constants are excluded.
LIST_REPLAY_GUARD authenticates the captured whole plan and current effective geometry,
inherits then-current COLOR/UV1 on each playback and never reconstructs expired pointers.
Complete identity includes layout, ordered pointers, enum-declaration-ordered mask and
geometry in authenticated saved state/client/VBO/original-and-derived capture products.
Live generic subsets derive from the same authenticated source but need not equal the
prepared capture union. Changed mask/plan cannot reuse an incompatible product.
Existing generic-zero isolation, all-array capture isolation, rollback and exact LIFO
restoration remain binding; no native allocator/renderer API or ownership is added.

**Source issuance and lifetime.** These marker interfaces expose no GL names, native
addresses or mutable range getters. The vanilla-owning `mod.glue.vertex` adapter issues
same-device/context authenticated implementations at an observed prepare/upload/capture
boundary; it retains immutable range metadata privately. ClientRange retains a live JDK
ByteBuffer plus byte offset/count/limit and task/epoch identity without copying or owning
memory. BorrowedVbo retains a vanilla-owned object incarnation, byte range, upload generation
and epoch, not a public `adopt(int)` capability. DisplayListReplay retains the corresponding
vanilla-owned list incarnation, captured layout/primitive category and replay-safe state
metadata. Deletion, reallocation, builder reset, task cancellation or epoch retirement
invalidates issuance; a reused object name or equal public marker cannot resurrect it.
No delete/upload/allocation/lifecycle privilege belongs to this input service.

The backend authenticates its private issuer token, source incarnation/range and binding
stack identity; accepting an arbitrary marker implementation is forbidden. Cross-package
implementation works because the permitted nested categories are non-sealed; this does
not add a fifth `GLHandle` category or grant marker implementations trust. P10 observes
vanilla ownership and issues invalidation notifications inside the mod backend, not through
a new engine raw-name adoption API. Source bytes and products remain alive and unchanged
through the entire binding scope and every adjacent instance copy.

**Pre-mutation admission.** `bind` requires the render thread/current context, an authentic
live source, complete supported layout, exact range/count/stride with overflow-safe bounds,
valid field offsets/storage/normalization/locations and complete plan/mode compatibility.
No worker GL is permitted. Wrong thread returns WRONG_THREAD; forged/wrong-device/null source
INVALID_SOURCE; expired source STALE_SOURCE; invalid layout/plan their named reasons;
arithmetic/range failures OUT_OF_RANGE; unavailable capability or incompatible mode/input
UNSUPPORTED_INPUT. Rejected is mutation-free and appends no native call.

P10's `VertexInputPlan.expectedGeometryInput()` is the nonnull
`VertexGeometryInput {NONE,POINTS,LINES,LINES_ADJACENCY,TRIANGLES,TRIANGLES_ADJACENCY}`.
P7 delivers the authenticated effective P4 category; the mod adapter maps it exactly into
this P10 enum. P1 gains no registry dependency. LIVE_DRAW and LIST_REPLAY_GUARD compare
that expected category to §4.7.4a's actual private linked requirement **before input mutation**.
Unknown or unequal actual category rejects UNSUPPORTED_INPUT. The prepared native primitive
must also be compatible with that category. The private native submission adapter repeats
this in-memory identity/category/lifetime check immediately before issuing the existing
vanilla draw; no per-draw GL query, source rescan or public geometry-metadata accessor.
No program transition may intervene between validation and submission.

LIST_CAPTURE accepts only an authenticated prepared-epoch client range and immutable
capture plan, never derives its geometry category from the currently active shader and
never uploads instanceId or expands instanceCount. Captured canonical/derived geometry
is separately keyed by the prepared layout/category under P10's approved topology policy.
LIVE_DRAW accepts client or VBO sources; LIST_REPLAY_GUARD accepts a matching replay-safe
list source and performs only its required external guard/current-value state, never
reconstructs expired client pointers. Other source/mode combinations reject.
Existing vanilla-owned upload/capture adapters may create P10's authorized derived
triangle product; this service neither creates it nor chooses its winding/index policy.

**State transaction (D-P1-55, R27-1/NS-1).** The backend owns isolation as part of this
single `bind`/`restore` operation, not a new P10 array-management API. For LIVE_DRAW
with conventional positions, temporarily disable generic attribute array zero before
submission, even if enabled by another renderer and pointing at different valid storage.
Installing the conventional pointer alone does not give it precedence. Do not overwrite
generic zero's pointer merely to disable its enable; restore its actual predecessor.

LIST_CAPTURE treats the immutable complete plan as an enabled-array allowlist, not
just a list of fields to write. Enumerate every array legal in the actual compatibility
context and disable every enabled array not explicitly admitted, irrespective of owner.
This includes conventional vertex, normal, primary color, color-index and edge-flag arrays;
secondary-color and fog-coordinate arrays where supported; texture-coordinate arrays on
every legal client texture unit, not just units 0/1 or server sampler units; and every
legal generic location, not just P10 locations 10–12. Capability-gated additional array
families supported by the context must also be isolated using their documented state.
Legal limits/entry points come from the backend's capability inventory: never query an
unsupported enum, nonexistent client unit or generic location. If the backend cannot
enumerate/isolate a supported array family safely, reject UNSUPPORTED_INPUT before
mutation rather than leave it enabled or broaden the P10 field vocabulary.
Each admitted enabled array must have an authenticated source range and plan descriptor;
unmentioned arrays never borrow admission from the shader's apparent usage. Capture with
conventional positions excludes generic zero. Conflicting position declarations reject
INVALID_PLAN; this does not authorize P10 to add generic-position layouts.

The internal snapshot contains actual enables for the isolation set and complete descriptors
for every array whose descriptor setup can change: component count where applicable,
storage type, stride, pointer/address-or-offset, per-pointer buffer binding, normalization,
and capability-legal integer/long interpretation, divisor or other descriptor fields if
touched. Enable-only isolation leaves descriptors intact; their exact predecessor must
remain intact too, including generic zero's foreign pointer and buffer association.
Snapshot the actual array-buffer binding and client-active-texture selector before any
enumeration that changes them. Restore each changed descriptor with its original buffer
association, then its enable, then the original global binding/selector; never infer a
pointer's buffer association from the global binding or substitute buffer zero.

Also snapshot all legally queryable current values that setup, submission or capture can
change or leave unspecified, not only constants explicitly written by P10: primary and
secondary color, normal, fog coordinate, color index, edge flag, each affected client-unit
texture coordinate and each affected nonzero generic attribute (using its legal value
representation). Include any capability-gated current state affected by an admitted family.
Restore these values even when the plan used an array rather than a constant. There is
no current conventional position or current generic-zero value to query/restore; specifically
never query CURRENT_VERTEX_ATTRIB at zero on the ARB route. Disabled unrelated arrays do
not authorize reading their vertex memory. Array/current state not perturbed stays untouched.
Map storage and capability dispatch only in the backend and use GlStateManager for cached
state it owns. Do not change vertex bytes, matrices, program, primitive order or ownership.

Capture setup/snapshot/isolation runs before vanilla opens its list; successful capture
closes the list before restoration, and failure closes/abandons the candidate before
restoration. Neither isolation constants nor predecessor-restoring current-value commands
may accidentally become commands in the retained list. P10 owns candidate validity and
discard; P1 owns this transaction. LIST_REPLAY_GUARD remains a separate external guard/
current-value scope based on authenticated replay metadata: no capture-array enumeration,
no reinstating expired client pointers and no reconstruction from retired source storage.

Bound transfers one opaque LIFO render-thread
restoration obligation. `restore` accepts only the same device's live top binding; null,
forged, wrong-device, out-of-order, already-closed or wrong-thread calls fail before GL
with IllegalArgumentException for forged/wrong-device/null and IllegalStateException for
ordering/lifetime/thread violations. Correct restoration restores the exact snapshot in
finally and consumes the binding once; it never synthesizes a neutral state as predecessor.

Every potentially affected field is snapshotted before its first mutation, including
selector changes during enumeration; snapshot acquisition failure unwinds any such changes.
Setup failure after any mutation restores the actual saved predecessor internally before
returning Failed(diagnosticId); no Bound escapes and no draw/capture is permitted.
Native/restore errors remain visible through
the existing drain channel, and failed rollback additionally invalidates the input stack/
admission so P7 takes shaders-off containment. A consumed binding is never retried.
Callers drain before setup, after setup and after restore, treat any nonempty result as
failed evidence and do not submit after setup failure. This does not change batched
error attribution or silently convert an error into mutation-free Rejected.

**Recorder.** It owns synthetic same-recorder issuance and the identical range/epoch/
mode/stack checks. Add public recorder-fixture factories
`VertexSource.ClientRange clientVertexSource(String label, ByteBuffer bytes, int byteOffset, int vertexCount)`,
`VertexSource.BorrowedVbo borrowedVertexVbo(String label, long byteLength)`, and
`VertexSource.DisplayListReplay borrowedVertexList(String label, VertexLayout layout, VertexInputPlan capturePlan)`,
plus `void retireVertexSource(VertexSource source)`. These mint only synthetic recorder
identities; they are not production adoption or upload verbs. Fixture client bytes are
borrowed, never logged. VBO byte length permits exact layout/range validation; its source
range is the whole supplied length. **D-P1-70:** list issuance requires a nonnull
complete immutable capturePlan, validates its layoutFingerprint against the supplied
validated layout and every pointer/mask/capability association under the same capture
admission law, and privately retains the layout and defensive immutable plan before
returning the synthetic identity. Geometry derives solely from capturePlan.expectedGeometryInput().
List fixtures represent explicitly supplied replay-safe capture metadata, not a learned
first replay request. Every replay, including the first, compares against retained
whole-plan authority; identical physical layouts can carry different captured masks/unions.
Invalid fixture arguments reject before issuance. The former geometry-only overload
is removed; no default plan, raw-name adoption or locally minted substitute is permitted.

Use existing `ScriptedResponses.glError` keyed by `vertexInputs.bind` and
`vertexInputs.restore` for setup/rollback failures, with immutable pre/post input-state
snapshots in the recorder. Successful setup records
`vertexInputs.bind(sourceIdentity,sourceKind,layout,plan,mode,bindingIdentity)`;
partial setup retains that attempt plus `vertexInputs.rollback(bindingIdentity,restoredState)`;
normal closure records `vertexInputs.restore(bindingIdentity,restoredState)`.
State summaries contain enable/location/storage/offset/normalization/buffer-identity/current-
value data, never native names, addresses, ByteBuffer contents or shader source.
`bindsBalanced` includes these binding identities; borrowed source issuance is not owned
GL allocation, leak or deletion. Replay detects retired-source use and consumed binding
reuse separately from the four GLHandle categories. Live recording captures actual native
outcomes, not fixture defaults. Recorder success cannot prove driver list-capture semantics.

Service/source/recorder and base-layout adapters are v0.1 prerequisites at the earliest
claimed affected geometry support; CLASSIC56 extended producers/fields remain P10 v0.3.
Fresh P1/P7/P10 owner/receiver review and live primitive/list/restoration proof remain gates.

#### 4.7.7 Complete owned-texture parameter value (D-P1-52)

`TextureService.setParameters` already exists. This closes its missing public value shape
for mandatory synchronous use; no new service, object adoption or sampler-object verb.
All following types live in `engine.gl`, with no dependency on P5/P13 types.

```java
public record TextureParameters(
    TextureMinFilter minFilter, TextureMagFilter magFilter,
    TextureWrap wrapS, TextureWrap wrapT, TextureWrap wrapR,
    TextureCompareMode compareMode, TextureCompareFunction compareFunction,
    TextureBorderColor borderColor, float minLod, float maxLod, float lodBias,
    float maxAnisotropy, int baseLevel, int maxLevel, TextureSwizzle swizzle) {}
public enum TextureMinFilter {
    NEAREST, LINEAR, NEAREST_MIPMAP_NEAREST, LINEAR_MIPMAP_NEAREST,
    NEAREST_MIPMAP_LINEAR, LINEAR_MIPMAP_LINEAR
}
public enum TextureMagFilter { NEAREST, LINEAR }
public enum TextureWrap { REPEAT, CLAMP_TO_EDGE }
public enum TextureCompareMode { NONE, REF_TO_TEXTURE }
public enum TextureCompareFunction {
    NEVER, LESS, EQUAL, LEQUAL, GREATER, NOTEQUAL, GEQUAL, ALWAYS
}
public record TextureBorderColor(float red, float green, float blue, float alpha) {}
public enum TextureSwizzle { IDENTITY, LEGACY_DEPTH_LUMINANCE }
```

**Closed validation and mapping.** Fields are nonnull; floats finite, negative zero canonicalized
to positive zero; minLod≤maxLod, 0≤baseLevel≤maxLevel. This scope permits maxAnisotropy=1
only; explicitly set1 when the anisotropy extension is supported, otherwise native default1 needs no unsupported call. No driver/user-setting inference. Border is exactly
(0,0,0,0): neither granted wrap mode samples a border, and no unknown integer-border state
enters a claimed complete mapping. P1 maps enum names to corresponding native constants;
REF_TO_TEXTURE maps to COMPARE_REF_TO_TEXTURE. Apply S, ST or STR only for 1D, 2D/RECT
or 3D respectively; unused wrap axes must be CLAMP_TO_EDGE in canonical values.
Unsupported targets, enum/format/capability combinations or noncanonical values reject before GL.
RECT requires base=max=0, nonmipmap min filter and CLAMP_TO_EDGE on S/T.
Signed/unsigned integer storage requires mag NEAREST and min NEAREST or
NEAREST_MIPMAP_NEAREST. Comparison and LEGACY_DEPTH_LUMINANCE require depth/depth-stencil;
no color/integer texture may acquire comparison as a workaround.

Sampler state is exactly the prefix through maxAnisotropy. Object-only state is
baseLevel/maxLevel/swizzle. Storage immutability belongs to allocation, not this setter;
this call never converts mutable/immutable storage or changes its dimensions/format.
Depth-stencil sampling remains depth, never stencil; issue no GL4.3-only depth-stencil
mode setter on older contexts. IDENTITY is the color-texture RGBA mapping; depth values
use LEGACY_DEPTH_LUMINANCE consistently, including depth copies. It means R,R,R,1:
use supported swizzle on GL3.3/ARB/EXT texture-swizzle profiles, otherwise compatibility
GL_DEPTH_TEXTURE_MODE=LUMINANCE. Never issue RED as a GL2 depth-mode value.
Reject IDENTITY for depth rather than silently giving it different cross-profile meanings.
Capability checks precede mutation.
On profiles lacking swizzle, fresh owned color textures already have identity; no unsupported
swizzle enum is issued. Texture objects are not raw-adopted or externally reparameterized.

**Owner conversion, not a three-field guess.** P13-owned uploads map every min/mag name
exactly and repeat their single wrap on applicable axes only. Remaining sampler fields are
NONE/LEQUAL, zero border, min/max LOD -1000/+1000, bias0, anisotropy1. Base is0;
max is the highest contiguous uploaded/generated mip level in the admitted plan, or0
for nonmipped sources; swizzle IDENTITY. Missing levels never become drawable merely
because a sampler object is absent. P13 retains its source-specific baseline, sidecar outcome,
target/format rejection and parameter fingerprint; foreign-live values are never passed here.
P5 owns its color/depth/shadow filter decisions, including temporary mipmap work. It supplies
those exact filters and CLAMP_TO_EDGE axes, the same neutral LOD/border/anisotropy baseline,
and NONE/LEQUAL except hardware shadow comparison uses REF_TO_TEXTURE/LEQUAL.
All owned depth/depth-stencil textures use LEGACY_DEPTH_LUMINANCE; color uses IDENTITY.
P5 sets the planned last level before generating a chain, generates before mip-filter sampling,
and restores its owner-authored parameter state at the already-contracted moment; the setter
does not generate levels, change filters on its own, touch custom overrides, or alter flip law.

`setParameters` requires an owned live same-device/context texture and render thread.
Foreign (including borrowed depth), forged/wrong-origin/null inputs reject with
IllegalArgumentException before GL; wrong thread/context/deleted handle with IllegalStateException;
unsupported valid requests with UnsupportedOperationException. The caller drains before/after,
does not publish/sample on error and uses existing owner cleanup/fallback. Internal active-unit
and texture bindings are restored in finally via GlStateManager where cached. Object parameters
intentionally change; only bindings are restored. The last complete accepted value is retained
privately; partial native failure invalidates that value until complete reapplication or deletion.
Recorder validates identically, records `textures.setParameters(handleIdentity,parameters)` with
full immutable value and binding-restoration evidence, and exposes native errors through the
existing ScriptedResponses/drain mechanism. No per-bind native parameter query.

P14 may derive the entire sampler-state prefix from an authenticated complete value only;
base/max/swizzle stay on the object, and foreign unknown state still uses sampler0.
This value grant does not approve A1 execution, async work, new package homes or off-thread use.
Receiver §5 adoption and fresh reviews are required. Native perturb/set/failure/restoration,
integer/RECT legality, depth swizzle/compare and full mip-chain checks remain implementation proof.
`[V:web 2026-09-07]` parameter domains/defaults, object-state distinction and RECT restrictions:
https://registry.khronos.org/OpenGL-Refpages/gl4/html/glTexParameter.xhtml .
The compatibility depth baseline/allowed mode values are independently specified by
https://registry.khronos.org/OpenGL-Refpages/gl2.1/xhtml/glTexParameter.xml .
The limited public shape and neutral owner conversion are D-P1-52, not measured G6 parity.

#### 4.7.7a Target-bearing synchronous texture values (D-P1-63)

This mandatory value grant closes P13 R5 C2 together with P5 §4.2/D-P5-42.
The existing `create`, `allocate`, `upload`, depth-initialize and depth-copy verbs remain;
there is no async/staging API, direct engine GL, texture query or new parser.
These are shared closed `engine.gl` values. P5 owns format legality and defaults; P1 owns
their facade representation/native mapping. P3's parsed enums remain its existing types:
P13 converts their enumerants one-for-one, never parses names or adds a new format policy.
`ColorInternalFormat` is P5's 37 App B.4 values plus private `RGBA_COMPAT`;
`PixelFormat`/`PixelType` are exactly P5 §4.2's complete closed lists, and the existing
`DepthAttachmentFormat` domain is unchanged. No P3 pack grammar admits `RGBA_COMPAT`.
The allocation target below is a facade value, not an alternative pack target parser.

```java
public enum TextureAllocationTarget { TEXTURE_1D, TEXTURE_2D, TEXTURE_3D, RECTANGLE }
public record TextureExtent(int width, int height, int depth) {}
public record TextureRegion(int x, int y, int z, int width, int height, int depth) {}
public enum DepthTransferLayout { DEPTH_COMPONENT_FLOAT, DEPTH_STENCIL_UNSIGNED_INT_24_8 }
public sealed interface PixelLayout {
    record Color(PixelFormat format, PixelType type) implements PixelLayout {}
    record Depth(DepthTransferLayout value) implements PixelLayout {}
}
public sealed interface TextureSpec {
    record ColorTextureSpec(TextureAllocationTarget target, ColorInternalFormat format,
        PixelLayout.Color allocationLayout, TextureExtent extent, int mipLevels)
        implements TextureSpec {}
    record DepthTextureSpec(TextureAllocationTarget target, DepthAttachmentFormat format,
        PixelLayout.Depth allocationLayout, TextureExtent extent, int mipLevels)
        implements TextureSpec {}
}
public record TextureData(TextureAllocationTarget target, TextureRegion region,
    int mipLevel, PixelLayout layout, java.nio.ByteBuffer texels) {}
```

All fields are nonnull. Extents/region sizes are positive, origins and mip index nonnegative,
and arithmetic is checked before GL. Canonical unused axes are height=depth=1/y=z=0 for
1D; depth=1/z=0 for 2D/RECT. 3D retains all three axes. DepthTextureSpec admits only 2D;
color admits all four targets. `mipLevels` is a **count**, initially defined contiguous
levels `0..mipLevels-1`; valid range is `1..1+floor(log2(max used dimension))`, RECT exactly1.
At level l each used dimension is `max(1, baseDimension >> l)`; unused dimensions remain1.
Upload region end coordinates must fit that exact allocated level, never infer a target
from dimensions. RECT dispatch is native TEXTURE_RECTANGLE, not TEXTURE_2D; the other
targets map by identical names. Allocation uses target-specific image1D/2D/3D storage
definition, null data at each admitted level; upload uses corresponding subimage dispatch.
Allocation also establishes no unpack-PBO binding before a null-pointer definition, so
null is never interpreted as offset0 in a borrowed PBO. PBO save/clear/restore is issued
only where that binding is supported; unsupported profiles issue no PBO enum/entry point.
This defines storage, not initialized/sampleable contents. `generateMipmap` retains its
capability gate and defines the owner-admitted generated chain from initialized level0;
the backend updates its authenticated level metadata only on success. RECT generation rejects.
The first successful allocation fixes an owned object's target; reallocation may change
extent/format/levels only within that target. Changing target requires a new owned object.

PixelLayout describes tightly packed pixels, not internal channel sizes. Every synchronous
upload uses unpack alignment1, row-length/image-height0, all unpack skips0 and byte-swap/
LSB-first false, and no unpack PBO binding. Rows run x fastest, then y, then z. Scalar
component widths are BYTE/UNSIGNED_BYTE=1, SHORT/UNSIGNED_SHORT/HALF_FLOAT=2,
INT/UNSIGNED_INT/FLOAT=4; multiply by the format's 1/2/3/4 components. Packed types
occupy one 1/2/4-byte word per pixel as named, never multiply packed-word size by components.
P5's legal packed-format combinations remain binding. Depth layouts are respectively
4-byte float depth or 4-byte packed 24/8 words. `texels.remaining()` equals exactly the
checked region-volume times bytes-per-pixel. Raw payload bytes are transferred unchanged;
native scalar/packed-word interpretation is not changed by the Java cursor's byte order.
Decoded scalar/packed producers explicitly serialize native-order words; RGBA/RGB byte
producers emit channel bytes directly. No cursor-default endian conversion or channel swap.
The facade borrows bytes for this synchronous call only, never retains or mutates content,
position, limit or byte order. Caller keeps them stable through return; recorder retains
metadata/byte count, not payload. Preparation ownership and source digests remain P13's.

Preflight order is handle provenance/lifetime/thread/context, value shape/target/storage,
format/transfer compatibility, capabilities/limits, then payload bounds, all before mutation.
The capability step consumes D-P1-66's actual target-specific captured maxima, rejecting
zero/over-limit targets before allocation and using the same inputs in the recorder.
Integer color formats additionally require D-P1-67's GL3 or EXT_integer clear tier;
this is capability rejection, not the incomplete-framebuffer RGBA fallback.
Foreign and borrowed-depth destinations are forbidden. Invalid values/origin reject with
IllegalArgumentException; wrong thread/context/deleted handle with IllegalStateException;
unsupported valid requests with UnsupportedOperationException. Native errors remain the
existing drain surface: owner drains around work and never publishes/samples failed storage.
Partial allocation/upload invalidates that object's usable-content receipt until owner
rebuild/complete reinitialization, without treating a void return as successful GL.
Backend restores active unit, target binding, unpack-PBO and every touched unpack setting
in finally, using GlStateManager where cached. Restore failure poisons drawing admission;
it cannot become successful fallback. Recorder applies identical admission and records target,
format/layout, extent/region/mips, byte count and restoration evidence under existing
`textures.allocate`/`textures.upload` events; native failures use existing scripts/drains.

Existing framebuffer depth-copy semantics are unchanged: both verbs take a 2D region
`(srcX,srcY,0,width,height,1)`, copy to destination origin(0,0) level0 and require 2D
owned destination storage (initialization may establish its first target). Initialization
defines exact source depth/depth-stencil format/extent; steady copy preserves matching
storage. Existing first-copy tier rules, framebuffer restoration and borrowed source
permission matrix remain; this grant does not turn color upload into a depth-copy shortcut.

**D-P1-64 — complete object baseline on every successful setter.** Receive P14's
always-maintained baseline law: even with sampler caching enabled, `setParameters` applies
the entire latest authenticated owner-equivalent TextureParameters to the owned texture
object, alongside sampler cache state. base/max/swizzle alone is insufficient. Success
requires complete baseline application and binding restoration; partial failure invalidates
the receipt and invokes existing containment. Thus ordinary `useFixedFunction()` all-unit
sampler clearing cannot expose stale object state; no extra replay callback is needed at
ordinary boundaries. D-P1-62's demotion replay/check remains a defensive requirement,
not the first time object parameters become current. Never mutate borrowed textures;
all-unit normalization and live/retiring ownership accounting are unchanged.

Planned boundary evidence (not executed): distinguish identical-sized 2D/RECT allocations;
1D unused-axis rejection, 3D mip/region bounds and packed-word byte counts; all 37 raw
formats without fallback coercion; depth initialize/steady-copy equivalence; borrowed
destination rejection; failed upload/restoration containment; successful setter A→B with
sampler caching followed by fixed-function clearing observes B on the object, not A/defaults.
Fresh owner/receiver review and runtime proof remain required.

#### 4.7.8 P14 debug activity and package grant (D-P1-54)

R-P14→P1-1 is granted: at v0.5 `DebugService.isActive()` is true iff the installed backend
has actual GL4.3 or GL_KHR_debug capability/entry points and `schmaloogium.debug.glLabels`
is enabled. Before backend installation or on unsupported profiles it is false.
A debug context is not a prerequisite for labels/groups; message production on nondebug
contexts is implementation-dependent, so an empty callback is not proof of no driver errors.
The existing glLabels-triggered per-call drain cadence, opaque-handle validation and disabled
no-GL behavior remain. The gate states nothing about object lifetime; D-P1-71's label limb
(recorded as D-P1-73) does: `DebugService.label` on a live-but-unmaterialized owned texture
handle is legal and recorded-only — it updates the retained label, issues no GL, appends no
native-label event and queues no `GLError`, and the retained value — original or updated —
is applied exactly once by the first admitted materialization, before storage work; it is
dropped if the handle is deleted while still unmaterialized. Foreign and borrowed handles
are already materialized objects, so this limb changes nothing for them. No context flags
change merely to activate this service.
`[V:web 2026-09-08]` KHR_debug revision17 overview and procedures:
https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/KHR/KHR_debug.txt .

R-P14→P1-2 grants exactly `SamplerKey`, `SamplerTier`, `DsaTier`, `DebugTier`,
`AsyncCompileTier`, `AsyncReadbackTier`, `GlModernizationPolicy`, `GlModernizationPlan`
as pure values in `com.schmaloogium.engine.gl`, and helper implementations in
`com.schmaloogium.mod.glue.gl`. Existing `Lwjgl3GLDevice` stays in its P1-owned
`mod.glue` home and delegates to those helpers; no second device or hidden ownership transfer.
No new module edge, platform type in engine, Mixin package or compiled-expression grant.
R-P14→P1-3's value grant is §4.7.7. These complete missing v0.5 foundation contracts,
not the optional worker, extra debug-context flag, scoped-group API or per-owner async grants.
R-P14→P1-4 remains a separately tracked post-v0.5/optional request; recorder confinement
is not silently weakened. P14 must adopt these §5 surfaces and obtain fresh verification.

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
> **The prohibition now has a second address (REV1/REV2).** Pintonium depends on
> `org.taumc:glsl-transformation-lib`, a fork of the same library: **treat it as AGPL — never copy
> from it and never adopt it as a dependency**, including transitively through any Pintonium
> incorporation. `[§G7 item 7]` `[§G11.2 rule 2]`
>
> **The OptiFine decompile (`schlorbium-project/`) is behavioral-observation-only.** No identifier,
> structure, or code derived from it ships. `[D-8]` `[§G7.2]` *(REV2: `schlorbium-project/` is an
> **alias** now, not a sibling directory — it resolves to `reference-src/schlorbium-HD_U_G6_pre1/`
> inside this repo, gitignored. §G0.2 keeps the shorthand because RESEARCH.md and `DESIGN.md` cite it
> throughout; the rule is unaffected by where the tree sits.)*
>
> **Pintonium (`reference-src/pintonium-9c2fcc1/`) is LGPL-3.0: readable *and* incorporable with
> compliance** — the same compliance §G7 item 4 already imposes for Iris and Angelica, so an
> incorporation takes an ordinary entry in the table above and nothing new is needed to receive it.
> Two carve-outs ride with the permission and belong here rather than in a reader's memory: the
> `glsl-transformation-lib` prohibition above, and the vendored `kroppeb/stareval` expression engine,
> whose **license is unverified** — historically MIT per Iris's credits, but the upstream repository
> no longer resolves — so **verify before any reuse, else clean-room from App F.6**, which this
> project owns regardless. `org.anarres:jcpp` is **Apache-2.0, verified via its Maven POM**: a clean
> dependency candidate rather than a carve-out, and Phase 3's to take. Pintonium's own fat jar mixes
> LGPL and AGPL; §G7 item 7 records that as their compliance problem and **not a precedent**.
> `[§G7 item 7]` `[§G11.2]`

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

**The Pintonium relationship, in one paragraph, because REV1 adds it to this note's assigned scope.**
The reference implementation this project now reads is **LGPL-3.0**, and §G7 item 7 makes it readable
*and* incorporable on the same terms as Iris and Angelica — so it changes nothing about the analysis
above and adds one more instance of the same well-trodden combination. What it adds that is worth a
sentence in a *licensing* note is the **carve-out discipline**, because Pintonium is the first
reference whose tree is not uniformly one license: `org.taumc:glsl-transformation-lib` is treated as
**AGPL and is never copied and never depended on** — the same prohibition D-8 already carries against
Iris's bundled glsl-transformer, now with a second address; the vendored `kroppeb/stareval` is
**license-unverified** (upstream repository no longer resolves) and must be verified before reuse or
clean-roomed from App F.6; `org.anarres:jcpp` is **Apache-2.0, verified** and clean. §4.8.3 carries
all three as standing prohibitions so no future session has to reconstruct them. One thing that is
explicitly *not* a precedent: Pintonium's own shipped fat jar mixes LGPL and AGPL content, which
§G7 item 7 records as their compliance problem. We do not inherit their arrangement by reading their
code. `[V:web]` via §G7 item 7 / §G11.2 — this session did not independently audit the dependency
licenses, and says so rather than implying a review it did not perform.

**Conclusion.** No obstacle, and no change to D-7 is warranted. Two obligations for later phases,
recorded here so they are not rediscovered: (a) whichever arrangement Phase 12 picks for ModularUI,
if it bundles, `THIRD-PARTY.md` gets an entry and the license text ships; (b) any LGPL-3.0 code
incorporated *into our sources* under D-8 follows §4.8.3 regardless of the GUI decision — which now
includes anything taken from Pintonium, under the three carve-outs above. OQ-12 can be marked
resolved-by-note once a reviewer accepts this section.

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

Phase 2 may consume these same four types for its source-free evidence adapter (D-P1-45).
It projects `messageKey`, severity and channel; structured coordinates must come from the
emitting owner's attributed projection, because `EngineDiagnostic` itself has no location.
It never extracts coordinates from `args`/`detail`, commits those strings, or changes channel
routing. The source-bearing diagnostic may be retained locally for ordinary reporting but
does not enter goldens; P3 §5.1.1 supplies the complete source-free inspection projection.

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
public interface EarlyCompatContext {
    boolean isClassPresent(String binaryName); // resource/metadata probe, no class initialization
}

public interface EarlyCompatCheck extends CompatCheck {
    CompatVerdict checkEarly(EarlyCompatContext ctx);
}


public final class BailRegistry {
    public static void register(CompatCheck check); // early-safe checks before MOD plugin evaluation; others in preInit
    public static CompatEvaluation evaluate(CompatContext ctx);
    public static CompatEvaluation evaluateEarly(EarlyCompatContext ctx);
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

**Early class-only grant (D-P1-50).** P10 may register its `EarlyCompatCheck` once before
the MOD plugin's first evaluation. `evaluateEarly` evaluates only that subset in stable
registration order; repeated registration of the same object/id is a no-op, but a
different check reusing an id fails registration. Early probes inspect resource/metadata
presence without `Class.forName` initialization, Minecraft singletons, Forge mod-list
assumptions or a GL capability profile. A throwing early check is Bail. Its terminal
veto is retained for the session and prevents **all** dependent vertex hooks from applying;
later evaluation cannot undo partial transformation. Diagnostics are retained until normal
logging/chat/GUI sinks exist, not emitted through an unbootstrapped Minecraft singleton.
Normal `evaluate(CompatContext)` still evaluates full checks at the two runtime points.

The three existing config files remain: no P1 preinit hook is populated by this grant.
P10 places its coordinated vertex hook family under `mod.mixin.compat.vertex` in the MOD
config, with no game/render mutation merely from class loading. Default-format/cache
attachments must stay inert until P7's approved activation. If concrete target load order
requires an earlier attachment than MOD allows, P10 must obtain a separately reviewed
preinit/config placement amendment with target evidence; the grant does not silently
move the family or bypass early compatibility veto.

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

**What Phase 1 ships:** the types, registry and all three evaluation mechanisms above.
Point1 bootstrap and point3 real MOD-plugin early veto are wired at v0.1 before P10's
geometry-only family can apply (D-P1-53). P10 supplies that bounded detection policy then;
the full extended policy and point2 vertex-format-transition site remain v0.3.
§9 and §12 carry this same split; a returns-true plugin is not a completed early veto.
`[D-P1-23]` No mod ids are named by this phase —
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

**`build.yml`.** Java 25 / `actions/*` versions unchanged. Preserve wrapper Gradle **9.7.0**
and align all three current `setup-gradle` 9.6.1 inputs to it during implementation (§4.2.6a).

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

### 4.12 Glue-seam completeness check against the Pintonium service inventory (REV1/REV2)

REV2's Phase 1 spec adds this as a *Scope — in* bullet and as a **Doc gate** criterion: PD §2's
service inventory is *"a field-tested checklist of what a version-facing glue seam must cover"*, and
this document must *"use it to completeness-check the facade/provider design; note
deliberately-excluded items"*. This section is that check. It is **not an adoption** — nothing here
copies a Pintonium interface, and §G11.4's decision rule is not engaged for the mapping itself. It is
engaged for the one thing the check found, which carries `[D-P1-36]`.

**What the reference is, opened at source.** PD §2 describes a **service-based** seam:
`ServiceLoader` interfaces in shared, MC-version-agnostic code with per-version implementations. All
three exist in the checkout and were read for this check `[V:observed — Pintonium
common-shaders/src/main/java/…]`:

| PD §2's seam | Path under `reference-src/pintonium-9c2fcc1/` |
|---|---|
| `GLStateManagerService`, `RenderSystemService` | `common-shaders/src/main/java/com/mitchej123/glsm/{GLStateManagerService,RenderSystemService}.java` — 92 lines / 59 declarations and 65 lines / 31, **both read complete** for the second half of the check (§0.1; added this revision, V12-5) |
| `MinecraftVersionShimService` | `common-shaders/src/main/java/org/embeddedt/embeddium/compat/mc/MinecraftVersionShimService.java` — 148 lines, ~75 members; the inventory this check runs against |
| `CeleritasShaderVersionService` | `common-shaders/src/main/java/org/taumc/celeritas/CeleritasShaderVersionService.java` |

**One PD coordinate is corrected rather than repeated.** PD §2 gives the third interface as
`org.taumc.celeritas.api.v0.CeleritasShaderVersionService`; in the checkout the interface sits
**directly in `org.taumc.celeritas`**, whose only two entries are `CeleritasShaderVersionService.java`
and `api/`. **The `api.v0` segment is not fictitious, and the round-eleven wording that said the
checkout has no such package was wrong** (V12-7): `api/v0/` exists in the
same module and holds `CeleritasShadersApi`, a `ServiceLoader`-based public API surface
`[V:observed — Pintonium common-shaders/src/main/java/org/taumc/celeritas/api/v0/CeleritasShadersApi.java l. 1]`.
So PD's coordinate **conflates two adjacent API surfaces** rather than naming a package that does not
exist. The inventory PD draws from the interface is unaffected, and the correction is recorded because
§G1.1 asks a session to verify PD's pointers rather than inherit them — and because a demonstration of
verification that is itself wrong demonstrates the opposite.

**Why an interface-to-interface mapping is not the right check.** PD §2 itself records that
`common-shaders` *"is **not** headless-testable the way `:engine` is designed to be: it compiles
against LWJGL3 and Embeddium GL classes directly and calls GL statically throughout."* Their seam
abstracts *the game*; ours abstracts *the game and GL* (§4.3's C-1 forbids both on `:engine`'s
classpath), which is the whole of D-10's difference and the reason §4.7.5's headless backend can
exist at all. So the useful question is not "which of our interfaces corresponds to theirs" but the
one REV2 actually poses: **is there anything a version-facing glue seam is field-proven to need that
our design has nowhere to put?** REV2 defines that checklist in **two** parts — *"`MinecraftVersionShimService`'s
method list … **plus the glsm state services**"* (`DESIGN.md` ll. 999–1000) — so the check is run over
both halves, and the second half is bucketed after the first rather than folded into it, because its
counterpart here is §4.7.4 and not `mod.glue` (V12-5; through the §0.11 revision only the shim half
had been run, and the criterion is scored against the spec's two-part definition). Every member of
`MinecraftVersionShimService` was placed in one of four buckets, and the buckets are the answer.

| Bucket | Members (representative, not exhaustive) | Where it lands here |
|---|---|---|
| **Already served** — a verb or type this phase ships | `getAtlasSize`/`getTextureSize`; `unbindMainFramebuffer` — **but not its `bindMainFramebuffer` partner, which is the second gap below** (V12-6); `createNativeImage`/`readNativeImage`/`createNativeImageArray`; `getShadowModelView`/`getShadowProjection`; `isModLoaded`; `translate` | §3's `atlasSize` `ivec2` row; `FramebufferService.bindDefault(target)`, which binds framebuffer **name 0** and is therefore the *unbind* half exactly (their `unbindMainFramebuffer()` returns to 0 `[V:observed — Pintonium forge122/…/impl/MinecraftVintageVersionShimImpl.java l. 511]`); `TextureData`'s JDK `ByteBuffer` + `PixelLayout` (decoding is Phase 13's, the **upload verb** exists); `UniformService.uploadMatrix4` (values are Phase 8's); `CompatContext.isModLoaded` (§4.10); `[D-P1-22]`'s lang **keys** — `:engine` holds no user-facing English, which is a stronger answer than a `translate` accessor |
| **A `mod.glue` provider a later phase owns**, with a home §2.1 already assigns | the world/camera/player/weather/dimension accessors — `getSkyAngle`, `getMoonPhase`, `getDayTime`, `getRainStrength`, `getThunderStrength`, `getEyeBrightness`, `getNightVision`, `isEyeInWater`, `getCurrentHealth`/`Hunger`/`Air`/`Armor`, `isHurt`/`isBurning`/`isSneaking`/`isSprinting`/`isInvisible`/`isSpectator`/`isOnGround`, `getEyePosition`, `getPlayerLookVector`/`getPlayerBodyVector`, `getSkyColor`, `getBlindness`, `getPlayerMood`, `getCloudHeight`, `hasCeiling`/`hasSkyLight`/`getAmbientLight`, `isCurrentDimensionNether`/`End`, `getRenderDistance*`, `getUnshiftedCameraPosition`, `getLightningBoltPosition`, `isFirstPersonCamera`, `hideGui`, `isRightHanded`, `getScreenBrightness`, the height-limit trio; plus `populateBlockIds`, `getResourceManager`/`makeResourceLocation`, `markRendererReloadRequired`, `getMipmapLevels` | **Phase 6**'s value providers in `engine.uniforms` behind `mod.glue` adapters for the bulk of them; **Phase 9** for `populateBlockIds`; **Phase 3** for the resource accessors; **Phase 7** for `markRendererReloadRequired` (RESEARCH.md §4.1 step 3's vertex-format rebuild); Phase 5/13 for mipmap levels. §2.1 already assigns `mod.glue` to *"Phases 1 (facade impl shape), 6, 7, 9"* — the inventory adds no phase that table does not name |
| **Deliberately excluded — multi-backend machinery we do not have** | `ServiceLoader` dispatch itself; `getMcVersion`/`getBackupVersionNumber`; `getOsString`/`isOnOSX` | Pintonium needs version dispatch because one shader core drives several MC backends. **We have one.** D-1 makes Cleanroom-exclusivity binding, and the seam we hold against a *future* backend is the **module** boundary (§2.2, OQ-20), not a version-dispatch service — a service seam inside one module would be ceremony that buys nothing and costs a dispatch on every accessor |
| **Not ours at any phase** | `isDHPresent`; `getSmartCull`/`setSmartCull`; `getCurrentTick` | Distant Horizons compat is a §1.2 non-goal. Smart-cull is chunk-renderer state: Pintonium's shader support *rides a replacement chunk renderer* (§G11.1) and ours explicitly does not (D-2, and §4.10 exists to **bail** when one is present). `getCurrentTick` is a helper for their cadence buckets; our cadences are Phase 6's design, not a glue accessor |

**The second half of the checklist — the glsm state services, bucketed the same way** (V12-5)
`[V:observed — Pintonium common-shaders/src/main/java/com/mitchej123/glsm/]`. This is the half of
their seam that abstracts **GL**, so its counterpart is the facade itself and the mapping runs
verb-to-verb rather than accessor-to-provider:

| Bucket | Members | Where it lands here |
|---|---|---|
| **Already served by a facade verb** | `glGetInteger`/`glGetString`; the program/shader set (`glCreateShader`, `glCompileShader`, `glCreateProgram`, `glAttachShader`, `glLinkProgram`, `glUseProgram` for both a linked program and program zero, `glDeleteProgram`/`glDeleteShader`, `glGetProgrami`/`glGetShaderi`, `glGetUniformLocation`, `glGetAttribLocation`/`glBindAttribLocation`); `glUniform1i`, `glUniformMatrix4`; the framebuffer set (`glGenFramebuffers`, `glBindFramebuffer`, `glCheckFramebufferStatus`, `glFramebufferTexture2D`, `glDeleteFramebuffers`); `glGenTextures`/`glDeleteTextures`/`glActiveTexture`/`bindTexture`/`setBoundTexture`; `glViewport`, `glClearColor`, `clear`, depth enable/disable + `glDepthFunc`/`glDepthMask`, blend enable/disable + `glBlendFuncSeparate`; `glCopyTexSubImage2D` | `GLCapabilityProfile`'s probe set (§4.7.2); `ShaderService` (its whole verb list, including `useFixedFunction()` and `bindAttributeLocation`); `UniformService.upload`/`uploadMatrix4`; `FramebufferService`; `TextureService.create`/`delete`/`bindToUnit`; `StateService.viewport`/`clearColor`/`clear`/`depthTest`/`depthMask`/`blend`. `glCopyTexSubImage2D` is how `copyDepthToTexture` is *implemented*, not a verb above it |
| **Served by `snapshot()`/`restore()`'s aspect set** — the state **reads** those two verbs are built out of | `getDepthStateMask`; `isBlendEnabled`, `getBlendMode`; `getViewportWidth`/`getViewportHeight` | `StateService.snapshot(EnumSet<StateAspect>)` / `restore(StateSnapshot)`, whose aspect enum has one constant per verb (§4.7.4). **This bucket is also the strongest corroboration the check produced:** it is exactly the member set §12 item **22a**'s review hook audits, added against PD §17 B11 — their `getColorMask()` returns a hardcoded all-true, satisfying every signature while breaking restore discipline |
| **Already recorded as absent, each with its requester** | `glColorMask` / `getColorMask`; `enableCullFace`/`disableCullFace`; `glPixelStorei`; `glUniformMatrix3`; `glGetTexLevelParameteri` | §4.7.4's absent-verbs table, unchanged by this half: colour mask (**7**), face culling (**3** produces the map, **7** wires it), free-standing pixel-store state (carried inside `TextureData`), `mat3` (no contract consumer). `glGetTexLevelParameteri` is a texture-*dimension* query, and the one dimension the contract needs — `atlasSize` — reaches us as a **value** from `mod.glue` (§3's `atlasSize` row, Phase 13), never as a GL query |
| **Not ours at any phase** | the binding **reads** `getActiveTexture`, `getActiveTextureAccessor`, `getBoundTexture(int)`, `getActiveBoundTexture`; `glGenBuffers`/`glBindBuffer`, `glGenVertexArrays`/`glBindVertexArray`; `RenderSystemService`'s modern-`RenderSystem` surface — `assertOnRenderThread`/`assertOnRenderThreadOrInit`, `setShaderTexture`/`getShaderTexture`, `setShaderColor`, `getShaderFogColor`/`Start`/`End`, `getFogShape`, `getShaderLineWidth`, `getProjectionMatrix`/`setProjectionMatrixOrth`/`Origin`, `setPositionShader`, `setUnknownBlendState`; and the two `ServiceLoader` singletons | The binding reads are answered by `[D-P1-29]` instead of by a verb: because the backend issues every `GlStateManager`-cached verb *through* `GlStateManager`, vanilla's cache never goes stale and nothing has to read a binding back to restore it. Buffer objects and VAOs are the **backend's** private business — the only geometry this facade draws is `DrawService.fullscreenQuad()`, and D-9 keeps `GL_QUADS` available (§3), so the vertex source is not contract. The `RenderSystem` surface is 1.17+ vanilla, absent on 1.12.2; its fog/line-width/matrix accessors are vanilla **state** reached through `GlStateManager` and the fixed-function matrix stack (Phase 6's FF-matrix capture at Phase 7's hooks), not through a glue accessor. The singletons are the version-dispatch machinery the fourth bucket above already rejects |

**Result: the second half adds no gap, and that is structural rather than luck.** Their glsm services
exist to abstract GL for a core that otherwise calls GL statically; ours *is* the GL abstraction, so
every member is a facade verb, a snapshot aspect, an absence already recorded with a requester, or a
concept 1.12.2 does not have. *Limit stated rather than glossed:* this is a **member-list** check, as
the criterion asks — not a behavioural comparison, and not a claim that our verbs and theirs have
matching semantics.

**REV2's count note, checked against the tree rather than the diagram.** The 9c2fcc1 checkout holds
**three** platform modules — `forge1710/`, `forge122/` and `modern/` — plus **two** shared cores,
`common/` and `common-shaders/` `[V:observed — Pintonium (directory listing)]`. PD §2's diagram
additionally names a `:babric` backend. It is **absent from the tree**, and this session can sharpen
REV2's wording by one degree: `settings.gradle.kts` l. 126 *does* declare it
(`createStonecutterProject("babric", …)`), so the diagram is not simply stale — the module is
configured and not checked out. `forge122` is likewise a stonecutter project rather than a
single-version module (l. 121, over `1.12.2` and `1.10.2`). *Limit stated rather than glossed:* this
is a directory-and-settings observation, not a build audit; why `babric` is declared without content
was not investigated, because nothing in this phase turns on the answer. §G3.1's instruction — *"count
them from the tree, not from PD"* — is what this paragraph obeys.

#### The gaps the check found — one in the texture-handle direction, one in the framebuffer direction

`getColorTextureId()`, `getLightTextureId()`, `getMissingTextureId()`, `getTextureManager()` and
`createDynamicTexture(...)` are the shim's way of naming **textures Minecraft owns**. They have no
bucket above, and the reason is a real hole rather than a difference of shape.

**The contract needs them.** App B.3's fixed unit map is `[V:doc]` contract that packs rely on
numerically, and on GBUFFERS/SHADOW programs its first two rows are **unit 0 = `texture`** (the
vanilla block atlas) and **unit 1 = `lightmap`** — both created and owned by Minecraft, neither
produced by `TextureService.create(String)`. `TextureService.bindToUnit(int unit, TextureHandle t)`
therefore **cannot express two of the sixteen unit rows**, because nothing in §4.7.4 yields a
`TextureHandle` for a texture the engine did not create. The facade has the verb and no way to reach
its argument.

**Disposition: declare the provider; do **not** add a facade verb.** `[D-P1-36]` The fix that
suggests itself — an `adopt(int glName)` on `TextureService` — is the wrong one, and §4.7.3 says why:
a raw GL name in an `:engine` signature is exactly the leak opaque handles exist to prevent
(`[D-P1-15]`), and `SeamBytecodeTest` would pass it happily while the design lost the property that
test was written to protect. The seam's own answer is better and costs no facade **verb**: **`mod.glue`
implements `TextureHandle` for vanilla-owned textures and supplies them through a provider**, so the
GL name never crosses into `:engine` and the consumer receives the same opaque type it receives for
its own textures — *the same type under a narrower contract*, which §4.7.3 now states in full rather
than leaving to be inferred: bind-only, and outside the handle-lifetime rule (V13-3). §5.1 carries the slot and §4.7.3 now carries the **type** — `ForeignTextureProvider`,
declared this revision, because a §5 row, a §9 tag and a §12 item were pointing at an interface that
existed nowhere (V12-4), which is the exact inverse of the omission `[D-P1-33]` deleted a verb for.
**Two costs the §0.11 revision claimed this had none of, stated rather than buried:** the four handle
sub-interfaces lose `sealed` — a `mod.glue` implementation of a *sealed* `engine.gl` type cannot
compile at all, and the same was already true of `RecordingGLDevice` (§4.7.3, V12-3) — and `:engine`
gains the provider type.

**Who owns the contents — corrected against `DESIGN.md` (V12-1).** The unit map's ownership is shared
and **the half at issue is Phase 5's**: l. 1488 puts *"you own which texture object backs each unit
per stage"* in **Phase 5's** *Scope — in*, while Phase 6's duty is *"sampler uniforms re-point to the
App B.3 fixed unit map"* (l. 1563) — Phase 6's spec carries no texture-binding bullet at all. So
**Phase 5 defines the vanilla-owned set**, **Phase 6** points the samplers at those units, and
**Phase 13** needs the same slot for the `minecraft:`-asset custom-texture forms its spec lists
(`dynamic/lightmap_1`, atlas paths — `DESIGN.md` ll. 2269–2271), *not* for the `_n`/`_s` companion
atlases, which it builds itself and which therefore come from `TextureService.create` like any other
engine texture. **That consumer is the reason the provider's key space carries two vocabularies rather
than one** (V13-2): what Phase 13 holds is a **resource location**, which is the *source* side of App
F.5's `texture.<stage>.<samplerName>=<source>` form, while an App B.3 sampler name is the *destination*
side — so a key space of App B.3 names alone could not express the lookup Phase 13 must make, and §5.1
would have been promising it a route it could not take. §4.7.3 states both vocabularies and their two
owners. The narrowing holds in a second direction too: a `minecraft:` form naming a **static** asset
needs no foreign handle at all, because Phase 13 can read it through the resource accessors and upload
it; only the **live** vanilla-owned textures — the lightmap and the atlases — are unreachable any other
way. §3's row and `[D-P1-36]` said *"Phase 5/6"* all along; the round-eleven revision said
*"Phase 6"* at five sites, and `DESIGN.md` l. 631 makes this seam — *"the P5/P6 texture-unit-map
split"* — one of the shared-ownership seams §G5.3's integration review must cross-check. Phase 1 names
the slot and stops either way: enumerating the set is the unit map's job, and the unit map is not this
phase's.

**Recorded as a finding rather than quietly closed, because it is the kind that stays invisible until
a dependent hits it.** A Phase 5 or Phase 6 session at **v0.1** — its own milestone — reading §5 and
finding `bindToUnit` had no reason to suspect that its unit-0 and unit-1 rows had no expressible
source. That is precisely the failure §5's *"sufficient on its own"* promise exists to prevent, and it
took a field-tested inventory to surface it: ten adversarial rounds over this facade did not, because
every one of them audited what the document **says** against its sources, and this is a thing the
document does not say.

**The second gap: the same hole in the framebuffer direction** (V12-6). `bindMainFramebuffer()` binds
**Minecraft's own framebuffer object** (`CLIENT.getFramebuffer().bindFramebuffer(true)`, l. 506) while
`unbindMainFramebuffer()` returns to framebuffer **0** (l. 511)
`[V:observed — Pintonium forge122/src/shaders/java/org/embeddedt/embeddium/impl/MinecraftVintageVersionShimImpl.java]`
— opposite operations, and one verb cannot be both. `FramebufferService.bindDefault(target)` is
name 0, which it now says in its javadoc, so it serves the *unbind* half; the *bind* half has no verb,
because §4.7.4 yields a `FramebufferHandle` only for framebuffers the engine created. It is recorded
as an **absent verb with named requesters** (§4.7.4's table: Phase 7 for RESEARCH.md §4.3's
`final`-to-vanilla handoff, which `DESIGN.md` makes a Phase 5 ↔ Phase 7 contract; Phase 5 if it ever
needs vanilla's FBO as a blit source) rather than as a slot — **but not because "nobody has asked",
which was this paragraph's ground through the §0.12 revision and is false (V13-1).** A contract item
*is* unserved here: RESEARCH.md **l. 526** names the `final`-to-vanilla render, and `DESIGN.md` puts it
at **v0.1** on both sides, ll. **1486** (Phase 5) and **1685** (Phase 7). What differs from the texture
gap is not whether anything is unserved but **where the answer legitimately lives**. `bindToUnit` is a
facade verb whose *argument* is unreachable — the engine binds those units itself, inside its own
program draws — so only a facade-side slot can serve it. The framebuffer bind is a **call the facade
never makes**, and `DESIGN.md`'s own injection timeline already houses it in `:mod`: *"composite-all
then final to the MC framebuffer"* at Phase 7's `renderWorldPass` TAIL (l. 1713). One gap needs a facade
argument; the other needs a call outside the facade — and that, not an absent requester, is why one is
a slot and the other a row. The reason previously printed rested on App E row 17, whose last column is
*"Serves hook needs (§7.1)"* and therefore catalogues a **hook site** rather than vanilla behaviour that
would make a rebind unnecessary; §4.7.4's row now carries the derivation and the `bindsBalanced()` blind
spot that lets the mistake survive a headless run. If a requester does want the verb after all, the
answer is still `[D-P1-36]`'s shape — a `mod.glue` `FramebufferHandle` — and not a new verb.

### 4.13 Engine bring-up sequence (REV1/REV2)

REV2's Phase 1 spec adds a *Scope — in* bullet and the second new **Doc gate** criterion:
*"bootstrap sequence adopted or deviation justified."* PD §16 supplies a proven 1.12.2 three-stage
bring-up; this section adopts it with two deviations, both argued.

**The reference, verified at the injection site rather than taken from PD.** PD §16 names three
stages. All three are single-method mixins in one package, and each was opened
`[V:observed — Pintonium forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/startup/]`:

| Stage | Pintonium's mixin | Target and injection point (read at source) |
|---|---|---|
| 1 — early init | `MixinGameSettings` | `@Mixin(GameSettings.class)`, `@Inject(method = "loadOptions", at = @At("HEAD"))` |
| 2 — GL caps ready | `MixinInitRenderer` | `@Mixin(OpenGlHelper.class)`, `@Inject(method = "initializeTextures", at = @At("RETURN"))` |
| 3 — loading complete | `MixinGuiMainMenu` | `@Mixin(GuiMainMenu.class)`, `@Inject(method = "initGui", at = @At("RETURN"))` |

PD §16 gives stage 1 as *"`GameSettings.loadOptions` (early init)"* without an injection point; it is
**`HEAD`**, which the table above records because a stage-1 hook that ran at `RETURN` would be a
different moment entirely.

**The §G11.4 contract check, performed and reported.** A bring-up ordering is **not
contract-visible** under §G4.2, whose definition is behavior *packs* can observe — RESEARCH.md §3 and
Apps A–D, F. A lifecycle order lives in §4.1, which is behavioral rather than contractual, so the
`D-P<N>-<k>`-with-contract-check rule is satisfied by recording that the check was run and came back
negative, not by asserting a contract row. What §4.1 *does* impose is an **ordering constraint**, and
the adoption is consistent with it: §4.1 step 1 puts the capability probe and the global-config load
at *"display init"*, step 2 pack discovery, step 3 pack load, step 4 a **lazy** init at first frame.
Pintonium's stages map onto steps 1→3 with step 4 staying lazy in both designs. One divergence is
worth the sentence: §4.1 step 1 bundles the probe and the global config at display init, while
Pintonium splits them — config at stage 1, probe at stage 2. That is harmless, because step 1's force
is *"before pack discovery"* rather than *"in the same instant"*, and it is recorded so a later
session does not read the split as a contradiction it has to resolve.

**Adopted, with two deviations.** `[D-P1-37]`

1. **Stage 2 is adopted as-is, and it is the load-bearing one.**
   `OpenGlHelper.initializeTextures` at `RETURN` is the earliest 1.12.2 moment at which a GL context
   exists *and* vanilla's own texture setup has completed — which is exactly what `CapabilityProbe`
   needs and exactly what §7's *"render thread, once, at display init"* was gesturing at without
   naming a site. **Phase 1 names the moment; Phase 7 owns the catalog entry** (App E is its
   deliverable, §1.2), so this is a *requirement on* Phase 7's hook set rather than a hook authored
   here — no mixin class is written by this phase (§4.5). **This is also the moment `mod.core` installs
   the foreign-texture provider** (`ForeignTextures.install(…)`, §4.7.3, `[D-P1-36]`): it is where the
   GL context and the engine bootstrap already are, and the provider resolves keys lazily, so nothing
   requires vanilla's own textures to exist yet. Two things ride this stage rather than one, and saying
   so is the point of naming a moment at all.
2. **Stage 1 is deviated from: we use the FML lifecycle, not a `GameSettings` mixin.** Cleanroom is
   Forge-lineage and hands us `preInit` and `FMLLoadCompleteEvent` natively; Pintonium hooks
   `loadOptions` because it needs a moment before *its own* options load and has no equivalent event
   in every backend it serves. This document's two stage-1-shaped obligations already sit on those
   events — §4.9.1 installs the log sink at `preInit`, §4.10 evaluates bail point 1
   post-`FMLLoadCompleteEvent` — so adopting the mixin would add an injection where an event already
   exists. D-5 budgets *"~25–30 targeted injections"*; spending one on a moment the loader hands us
   free is the wrong trade, and it would put class-transformation-adjacent machinery in front of the
   very check (§4.10 point 1) whose job is to decide whether to run at all.
3. **Stage 3 is adopted as a signal and deferred as a placement.** `GuiMainMenu.initGui` at `RETURN`
   is the right "loading complete" marker and is recorded as such, but **nothing in Phase 1 consumes
   it**: the probe is stage 2's, the bail point is stage 1's, and the first real consumer is Phase 7's
   frame driver. Wiring it at v0.1 would be a hook with no caller. It goes to §11.4 as a named
   recommendation with its source, not into §9 as a v0.1 component.

**What this changes here, stated so the delta is auditable.** No type of its own, no facade signature,
no test — the `ForeignTextures.install(…)` call point above is a *placement* of §4.7.3's type, not a
declaration made here. §7's `CapabilityProbe` row gains a *named* moment where it had a description;
§11.4 gains a Phase 7 hand-off carrying stages 2 and 3 — the first as a requirement, the second as a
recommendation (V12-2); §5.1 exposes the sequence as a structural contract, because a
dependent that places its own bring-up work relative to ours needs to know where ours sits. The
sequence is **architecture now, implemented at v0.1 for stage 2 and by Phase 7 thereafter** — §G0.3's
distinction, applied to a thing that is otherwise easy to mistake for assembly detail.

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

This section is self-contained as an **index of every cross-phase obligation**, but detailed public
declarations and exact semantics incorporated from §4.7.2–§4.7.5 remain part of the binding
contract and must be read there. An edit to any incorporated declaration or semantic contract is
incomplete unless the same revision also updates its corresponding §5 row (including an explicit
"unchanged" entry when the row's incorporation remains exact). Thus every valid change to that
surface changes the declared interface region and fires its fresh-review trigger.

### 5.1 Structural contracts

| Exposed | Detail | Consumed by |
|---|---|---|
| **Module layout** | `:engine`, `:mod`, `:conformance` with the incorporated §2.1 package tables; §0.24 adds only the three Phase 13 texture homes. Existing grants and dependency edges are unchanged | all phases |
| **The seam constraints C-1 … C-4** | §4.3, stated mechanically and enforced by tests; C-3's confinement is the package tree `com.schmaloogium.mod.glue` and its subpackages | all phases |
| **Package placement rule** | a phase's code goes in the closed §2.1 package allocation; `.internal` sub-packages are private to `:engine`. The texture grant narrows placement without adding API permissions or weakening C-1 through C-4, facade-only GL, or §4.5.2a config/package agreement | all phases |
| **Phase 8 package grant** | §2.1 assigns `engine.shadow`, `mod.glue.shadow`, and `mod.mixin.shadow` exactly; C-1 through C-4 and the `.internal` rule apply without exception | **8** |
| **Phase 7 frame-package grant — R7-8 already granted; unchanged** | §2.1 assigns `engine.frame`, `mod.glue.frame`, `mod.mixin.frame`, and `mod.conformance` exactly. `engine.frame` owns pure policy and closed results; `mod.glue.frame` owns Minecraft/Forge/LWJGL adaptation; `mod.mixin.frame` stays dumb; `mod.conformance` is the capture-agent entry point and does not relax C-4. The normal `.internal` and seam rules apply without exception (`[D-P1-41]`). §0.24 adds no Phase 7 package | **7**, 2 |
| **Phase 13 texture-package grant — R3 granted** | §2.1 assigns exactly `com.schmaloogium.engine.textures` in `:engine` for pure texture policy/model/results, and `com.schmaloogium.mod.glue.textures` / `com.schmaloogium.mod.mixin.textures` in `:mod` for platform/facade adapters and dumb hooks respectively (`[D-P1-43]`). The existing parent `mod.mixin` grant is preserved. C-1 through C-4, `.internal` privacy, facade-only GL and Mixin config/package agreement remain binding; no texture protocol, dependency API or conformance edge is granted. Verified consumption awaits §0.24's fresh whole-document PASS; unrelated requests remain open (§11.4) | **13**, 7 (composition), 2 (conformance) |
| **P10 vertex package grant — R10-1** | Exactly §2.1's `engine.vertex` (with private `.internal`), `mod.glue.vertex`, `mod.mixin.compat.vertex`; C-1…C-4, facade-only GL, dumb hooks and config agreement unchanged. Layout/epoch/topology/lifecycle policy stays with P10/P7 | **10**, 7 |
| **P3 pure-JVM dependency admission — D-P1-49** | §4.2.4b's exact `org.anarres:jcpp` implementation-time verified pin, complete closure/license checks, unchanged C-1 enforcement and once-only runtime packaging are incorporated. No public jcpp types, platform-coordinate exception or new runtime interface | **3**, 2 (headless runtime) |
| **Version pins and build migration** | §4.2.6 retains July evidence/procedure; §4.2.6a controls current exact wrapper/plugin/loader values, observed CI mismatch and mod-only Buildship/generated-source placement. No pin upgrade or unperformed verification | all phases; operationally, whoever tags a milestone |
| **Naming** | `mod_id = schmaloogium`, root package `com.schmaloogium`, `Reference` at `com.schmaloogium.Reference` | all phases |
| **The engine bring-up sequence** | §4.13, `[D-P1-37]`. Three stages, adopted from a proven 1.12.2 reference (PD §16) with one deviation: **(1)** loader-facing setup on the FML lifecycle — log sink at `preInit` (§4.9.1), bail point 1 post-`FMLLoadCompleteEvent` (§4.10) — **not** a `GameSettings` mixin; **(2)** `OpenGlHelper.initializeTextures` at `RETURN` is the **capability-probe moment**, the earliest point at which a GL context exists and vanilla's texture setup has completed; **(3)** `GuiMainMenu.initGui` at `RETURN` is the "loading complete" signal, **recommended and not wired** — Phase 1 has no consumer for it. Exposed here because a dependent placing its own bring-up work relative to ours has to know where ours sits, and because **stage 2 is a requirement on Phase 7's hook catalog while stage 3 is a recommendation Phase 1 does not wire** — neither is a mixin this phase authors (§4.5). The two strengths are not interchangeable: the requirement obliges an App E row at Phase 7's v0.1, the recommendation is Phase 7's to place when its frame driver has a use for it (§9, §11.4, V12-2) | **7** (owns the stage-2 catalog entry, and stage 3's if it chooses to place it), 5, 6, 13 (anything gated on "caps are ready"), 10 |
| **A `mod.glue` provider for vanilla-owned textures** — `ForeignTextureProvider` | §4.12, `[D-P1-36]`. App B.3's fixed unit map puts the vanilla block atlas at **unit 0** and `lightmap` at **unit 1** on GBUFFERS/SHADOW programs; `TextureService.bindToUnit` takes a `TextureHandle` and §4.7.4 produces one only for textures the engine created. The slot is named here **and its type is declared**: `ForeignTextureProvider` in `com.schmaloogium.engine.gl` (§4.7.3), a `mod.glue` implementation of it installed by `mod.core` at §4.13 stage 2. **Keyed on a pack-facing texture identifier in two disjoint vocabularies** (§4.7.3, V13-2): **(a)** App B.3's sampler names, bare, for the fixed unit map, and **(b)** `minecraft:`-namespaced resource locations, for App F.5's custom-texture forms that name a **live** Minecraft-owned texture (`dynamic/lightmap_1`, atlas paths). They cannot collide — no App B.3 name contains a colon. A `minecraft:` form that is a *static* asset needs no foreign handle and goes through `TextureService.create`/`upload`. **The handles are bind-only and outside §4.7.3's lifetime rule** (V13-3; stated by closure since V14-1): legal to `bindToUnit` and `DebugService.label` and to **nothing else in §4.7.4 that accepts a `TextureHandle`** — illegal to `allocate`/`setParameters`/`upload`/`generateMipmap`/**`delete`** and to `FramebufferService.attachColor`/`attachDepth`/`copyDepthToTexture`, whose `dst` is *written into* (the destructive case) — and they survive Minecraft's own resource reloads because the `mod.glue` handle resolves its object at each use. The shape: `mod.glue` implements `TextureHandle` for these textures so the raw GL name never crosses C-1 — **which is why the four handle sub-interfaces are not `sealed`** (§4.7.3: a *sealed* `engine.gl` type cannot be implemented from `mod.glue` at all, and could not be implemented by `RecordingGLDevice` either). **Contents are shared, and each vocabulary has one owner:** **Phase 5** defines vocabulary (a), the vanilla-owned unit-map set — `DESIGN.md` l. 1488, *"you own which texture object backs each unit per stage"* — **Phase 6** re-points the samplers at those units (l. 1563), and **Phase 13** owns vocabulary (b), which `DESIGN.md` ll. 2269–2271 give it and which l. 2287's *Scope — out* keeps clear of the unit map; §1.2's row is that joint ownership. Phase 1 enumerates neither vocabulary. **No facade verb and no `adopt(int)` is added** — see `[D-P1-36]`; the two declaration changes this row *does* cost are in §5.2's changelog row | **5** (defines vocabulary (a): which texture object backs each unit per stage), **6** (points the sampler uniforms at those units), **13** (defines vocabulary (b): its `minecraft:`-asset custom-texture forms that name a live vanilla texture — *not* the `_n`/`_s` companion atlases, which it builds itself and gets from `TextureService.create`, and *not* static `minecraft:` assets, which it can load and upload) |

**§0.18 clarification of the row above:** every value returned by `ForeignTextureProvider` is an
**ordinary** foreign texture and remains bind-and-label-only. Its closure now also forbids
`attachDepthStencil` and `initializeDepthTextureFromFramebuffer`. The separately declared
`BorrowedDepthAttachmentHandle` is issued by the main-depth bridge, not by this provider; its exact
authenticated permission matrix is the binding §5.2 row below.

### 5.2 `engine.gl` — the facade

| Exposed | Detail | Consumed by |
|---|---|---|
| `GLDevice` + the seven services | §4.7.4 signatures. **Changed in the §0.5 revision:** `UniformService` gained an `ivec4` overload, `StateService` gained `depthTest`/`fog`, `FramebufferService` gained `copyDepthToTexture`, and `GLDevice` gained `drainErrors()`. **Changed in the §0.6 revision:** `DrawService.fullscreenQuadInstanced(int)` was **removed** (`[D-P1-33]`), `RecordingGLDevice` gained a log-supplying constructor, and the GL-error row's attribution promise was narrowed to what the default cadence delivers. **Changed in the §0.7 revision: nothing in any signature.** Two rows were corrected in prose only — the non-verbs row (the `countInstances` mapping scoped to composite/deferred and the non-composite case handed to Phase 7) and the GL-error row (the drain's cadence, and three preconditions of the rung-2 protocol). **Changed in the §0.8 revision: again nothing in any signature** — the same two rows corrected in prose: the **GL-error row** (the elision bit tracks *facade* calls while the GL flag is per-context, so a non-empty drain does not imply one of Phase 6's uploads failed — V8-1; and property (i) no longer asserts a per-sample halflife premise it had no source for — V8-3), and the **non-verbs row** (the composite `countInstances` loop is **Phase 7's**, not Phase 5's, with Phase 6 named for the `instanceId` upload — V8-2). **Changed in the §0.9 revision: again nothing in any signature** — every service interface, handle type and value type is byte-for-byte what rounds seven, eight and nine all reviewed, and round nine's one edit inside the §4.7.4 block is a **javadoc sentence** on `GLError` and `drainErrors()`, not a declaration (V9-2). The same two rows again, in prose: the **GL-error row** (the frame-level remedy is one of two and carries two limits, not "the only sound remedy" — V9-1; the recurring-foreign replay cost is stated — V9-1; property (i)'s disclaimer is rescoped to what a second evaluation would do — V9-10), and the **non-verbs row** (its header now covers adjacent owners as well as requesters, readmitting **Phase 5** — V9-7; the composite loop carries `DESIGN.md`'s **`[v0.5]`** tag and its Phase 4 citation — V9-8). **Changed in this revision (§0.11): again nothing in any signature — and this time nothing in this row's subject at all.** The seven services, every handle type and every value type are byte-for-byte what rounds seven through **eleven** reviewed. §5 *is* altered this revision, at three places elsewhere: V11-1's deletion from the pixel-transfer row's consumer column, and **two new §5.1 rows** — the engine bring-up sequence (`[D-P1-37]`, §4.13) and the `mod.glue` vanilla-texture provider slot (`[D-P1-36]`, §4.12, which the REV2-mandated PD §2 completeness check produced). Neither new row adds a facade verb, which is exactly why this row is untouched while §5 is not. **Changed in the §0.12 revision — the first revision since §0.5 in which a *declaration* moves, so it is stated first and plainly.** No **verb** is added, removed or changed: the seven services' method lists, the result types and the value types are byte-for-byte what rounds seven through **twelve** reviewed (round twelve swept them member by member and found nothing — its §2 item 7). Two declarations do change, both forced by the round-eleven material rather than chosen. *(a)* The four handle sub-interfaces and `UniformLocation` **lose the `sealed` modifier** (V12-3): with JPMS rejected (§4.3, `[D-P1-6]`) `:engine` compiles into the **unnamed module**, where a sealed type's permitted subtypes must sit in its own **package** — so `Lwjgl3GLDevice`'s handles (`mod.glue`), `RecordingGLDevice`'s (`engine.gl.record`) and `[D-P1-36]`'s `mod.glue` handle were *all* uncompilable as declared. `GLHandle` stays sealed and its `permits` clause is still the enforced "four types, not five" (§12 item 18). *(b)* `engine.gl` gains **`ForeignTextureProvider`** and its installer `ForeignTextures` (V12-4) — the type §5.1's provider row named without declaring. The **non-verbs row** below also gains an entry — binding a framebuffer the engine did not create (V12-6) — and the consumer column names **7** then **5** for it. **No member of any pre-existing type changed and no dependent's call site changes. Changed in §0.15:** `ShaderService` gains `useFixedFunction()`, the first new facade verb since §0.5, to select program zero without exposing a raw integer, null, or sentinel handle. It is the exact Phase 4 §5.4 request and is additive to `use(ProgramHandle)` (`[D-P1-39]`) | 4, 5, 6, 7, 8, 13, 14 |
| **P14 debug activity and placement — D-P1-54** | §4.7.8 exact installed-backend GL4.3/KHR+glLabels gate without debug-context prerequisite, bootstrap/inactive no-GL behavior; exact eight pure values and mod.glue.gl helper home. Existing device remains mod.glue; optional worker/context-flag operations not granted | **14**, 7 (debug call sites), 2 (recording) |
| **§0.25 legacy geometry addition** | Incorporates the exact `ShaderService.configureLegacyGeometry(ProgramHandle,LegacyGeometryInputPrimitive,LegacyGeometryOutputPrimitive,int)` declaration and complete §4.7.4 preconditions, ordering, native mapping, errors and mandatory drain/abort transaction. Closed enums contain only `TRIANGLES` and `TRIANGLE_STRIP`. Count is positive and never clamped. No source/strategy ownership or general parameter setter is granted (D-P1-44) | **4** |
| **R26 linked primitive/fullscreen contract — D-P1-48** | Incorporates complete §4.7.4a: effective linked input acquisition including core/source overrides; issuance/selection/delete/restore/unknown-state lifecycle; no per-draw GL query or source scan; TRIANGLES uses triangle strip even on QUADS-capable profiles, other geometry inputs reject without draw; exact recorder vocabulary/scripts and failure containment. Existing public fullscreen signature remains | **4**, **7**, 10 (backend comparison), 2 |
| **Vertex input service — R10-1/D-P1-50/D-P1-55** | `GLDevice.vertexInputs()` and complete §4.7.6 signatures, closed sources/results/rejections/modes, authenticated lifetime and geometry comparison incorporated. D-P1-55 requires live conventional-position generic-zero isolation, complete capability-legal capture-array allowlist, actual descriptors/enables/bindings/selectors/current-side-effect restoration and partial-setup rollback; list replay remains a no-pointer-reconstruction guard. Recorder and v0.1 base/v0.3 extended scope unchanged; receiving adoption and live value/restoration proof remain unverified | **10**, 7 (composition), 2 |
| **Complete owned-texture parameters — D-P1-52** | Exact §4.7.7 TextureParameters and closed enums, sampler/object split, complete P5/P13 conversion, target/format/capability admission, owned-only binding-neutral set/failure/recording and neutral LOD/border/aniso policy incorporated | **5**, **13**, **14** value adoption only |
| **R32 routes and duration lock — D-P1-57** | Exact FramebufferDrawSlot/positional drawBuffers and ReplayAssertions list signature; StateService.lockAlphaBlend(Optional<AlphaTestState>,Optional<BlendState>) → opaque AutoCloseable AlphaBlendOverride, effectiveBlend() → BlendState; complete interception/bypass/snapshot/error/notification semantics in §4.7.4 | **4**, **5**, **7**, 2/6 observation |
| **§0.18/§0.19 framebuffer/depth additions** | Facade shape: `BorrowedDepthAttachmentHandle extends TextureHandle`; `FramebufferService.borrowDepthAttachment(platformTexture)`, `attachDepthStencil(f,t)`, and `initializeDepthTextureFromFramebuffer(src,dst,region)`. Issuance accepts a live ordinary foreign handle recognized by the receiving device and returns its opaque, non-owned borrowed handle; rejection is pre-GL. Phase 5 owns format, freshness, attachment/copy cadence, allocation/copy tier, restoration, and Minecraft lifetime (`[D-P1-40]`) | **5**, 7, 8 |
| **GL-error surface** — `GLDevice.drainErrors()` → `List<GLError>` (`op`, `subjectLabel`, `kind`, `detail`) | §4.7.4. This is the signal §G2.4's **rung 2** acts on — rung 1 is Phase 11's expression isolation and never reaches GL (§6). **Attribution is scoped to the drain window, and this is contract, not implementation detail:** a window holding exactly one mutating **facade** call yields a record naming that call; a window holding several yields **at most one record per driver error flag** — in practice one, carrying `subjectLabel = "(batched, N calls)"` — because GL holds only the first error in a flag until that flag is cleared. The rung-2 protocol is therefore: drain, upload the program's uniform set, drain — and **only if that drain is non-empty**, re-upload the set draining between uploads, so each record names one uniform, then disable those uniforms only (`[D-P1-32]`). **Three properties of that protocol are contract too, because you cannot implement it correctly without them.** (i) The re-upload reuses the values **already computed for this sweep** and never re-evaluates the providers — `glUniform*` is idempotent on the bound program, so re-uploading cached values changes nothing except *which drain window* each upload lands in, which is the replay's entire purpose. Re-running the sweep would instead re-enter your world-state providers, and **this document deliberately asserts no property of what a second evaluation would do** — not that it is safe, and not that it is harmful. Two facts about the surroundings are relayed, and they are statements about the sources rather than about your providers: RESEARCH.md §4.4 places the world-state sampling at frame begin, and `DESIGN.md` puts the time-corrected halflife formula in **your** *Scope — in*. Both are already inputs of your own spec; the design that follows from them is yours, and that is precisely why the protocol is "re-upload" rather than "re-run". (ii) If the replay comes back clean — `OUT_OF_MEMORY` need not recur, and per the cadence note below the error may not have been ours at all — the sweep is **unattributable**, and falls to §6's 3→4 row rather than silently disabling nothing or guessing. (iii) A `GLError` naming a uniform depends on the backend **retaining the name passed to `locate(p, name)`**; `UniformLocation` carries no name in its signature, so this is a stated backend obligation (`[D-P1-34]`), not something a test can catch. The **cadence** that delivers all of it: a drain is a `glGetError` **loop terminating on `GL_NO_ERROR`** (the GL-sanctioned form — a single call leaks a second flag into the next window and misattributes it), and a drain issues **no query at all** when no mutating **facade** call has occurred since the previous drain — which is what makes the two-drain protocol cost **one** query per clean sweep. **That bit tracks *facade* calls, while the GL error flag is per-context**, so a drain window can hold an error this facade did not cause — vanilla's own draws never reach it (§3's second row). **A non-empty trailing drain therefore does not by itself imply that one of your uploads failed**, which is why (ii) is load-bearing in general rather than an `OUT_OF_MEMORY` corner. **Two consequences you should plan for.** First, a *recurring* foreign error re-enters the replay every frame — reproducing nothing, disabling nothing, and costing a re-upload of the whole set — so a replay that repeatedly comes back clean is evidence of foreign GL rather than of a flaky uniform, and is the case to escalate rather than to retry forever. Second, the mitigation is not this facade's: §4.7.4 states the case once and §11.4 hands **Phase 7** a frame-level drain to place, whose two limits are stated there — it bounds only the gap spanning the frame boundary, and it is subject to the same elision, so it is not unconditional at the driver unless a mutating facade call precedes it. It is one remedy among two, not the only sound one: §4.7.4 records that dropping the elision would also bound the window and is rejected on **cost**, not on soundness. This works in the **shipping** configuration; the per-call cadence under a debug context or `-Dschmaloogium.debug.recordGL`/`glLabels` (those two flags only) is an optimisation of it, not a precondition for it. `ScriptedResponses.glError(...)` makes both window shapes testable headlessly | **6** (rung 2 is its v0.1 scope-in), 4, 5, 14 |
| **Replay-aware GL-error result** — `ReplayAwareGLError(GLError error, boolean attributed)` | There is exactly one result for each `GLError` from the triggering non-empty drain. `attributed=true` only when the caller's replay isolates the named facade call and the error recurs in that call's one-call drain window. A clean replay, a batched/ambiguous window, a foreign-context error, or a non-reproducing error yields `false`; neither `op` nor `subjectLabel` can manufacture `true`. Phase 6 performs the cached-value replay and emits this owner-defined value; Phase 2/7 may serialize or assert it verbatim (`[D-P1-42]`) | **6**, 2, 7 |
| `GLCapabilityProfile` | §4.7.2, including `supportsMipmapGeneration()`, `hasExtension()`, `atLeast()`. For **Phase 3** it is the whole GL-side input to RESEARCH.md §3.5's standard macro header, not just the extension macros: `glVersionMajor`/`glVersionMinor` → `MC_GL_VERSION`, `glslVersion` → `MC_GLSL_VERSION`, `vendor` → `MC_GL_VENDOR_*`, `renderer` → `MC_GL_RENDERER_*`, `extensions()` → the on-demand `MC_<GL_extension>` set. **`supportsMipmapGeneration()` is the gate `TextureService.generateMipmap` obliges its *caller* to check** (§4.7.4), so its consumers are the phases that generate: **7** for per-pass composite mipmaps (`DESIGN.md` l. 1683) and **8** on the shadow side (l. 1828) — both were missing from this column while §3 named Phase 5, which owns neither (V13-4) | 2, 3 (**the whole macro header**), 4, 5, 6, **7**, **8**, 14 |
| **`GLCapabilityProfile` text serialization format** | §4.7.2; `parse(Reader)` / `write(Writer)` | **2** (this is "recorded `GLCapabilityProfile`s"), 4, 5, 6 |
| Opaque handle types | §4.7.3 — four direct `GLHandle` categories, `UniformLocation.isAbsent()`, and the owned-handle lifetime rule. `BorrowedDepthAttachmentHandle` is a narrower `TextureHandle`, not a fifth category. Backends classify every texture argument before GL. Ordinary `ForeignTextureProvider` values are legal only at `bindToUnit` and `DebugService.label`. An authenticated same-device borrowed-depth value is additionally legal at `attachDepth` and `attachDepthStencil`, with the combined form requiring packed depth/stencil metadata. Both foreign classes are illegal for allocation, parameters, upload, mipmaps, deletion, color attachment, and initialization/steady-copy destination use. A public marker implementation, wrong-device value, or wrong-origin borrowed value is rejected before GL. Neither foreign class is owned/deleted by the engine; ordinary provider handles resolve their underlying vanilla object per use, while Phase 5 detects borrowed-depth identity/version changes and reattaches (`[D-P1-40]`) | 4, **5**, 6, 7, 8, 13 |
| `RecordingGLDevice`, `GLCallLog`, `GLCall`, `ScriptedResponses` | §4.7.5. `GLCallLog` is bounded (`bounded(capacity)` + `droppedCallCount()`) and is supplied to the device. Scripted responses cover compile/link/validate failures, absent uniforms, framebuffer status, depth pixels, and driver errors. Fixed-function selection records zero-argument `shaders.useFixedFunction`. §0.18 adds distinct `framebuffers.attachDepthStencil` and `framebuffers.initializeDepthTextureFromFramebuffer` events, keeps steady `framebuffers.copyDepthToTexture` distinct, records restored binding identities and format/extent summaries, and authenticates recorder-issued borrowed-depth values without treating them as owned/leak-counted objects | **2**, **4**, 5, 6, 8 |
| **§0.25 recorder and error incorporation** | The preceding recorder and GL-error rows incorporate §4.7.5's exact `shaders.configureLegacyGeometry(p,input,output,maxVerticesOut)` event, no-event precondition rejection, extension gate and existing `glError`/`linkFails` scripts. `drainErrors`, `GLError`, replay APIs, handle type declarations and profile record/serialization signatures are **unchanged**; §4.7.4's narrow pre-link handle checks and mandatory error transaction are added semantics | **4**, 2 |
| `ReplayAssertions` incl. `bindsBalanced()`, `noLeakedObjects()`, **`noUseAfterDelete()`**, `drawBuffersWere()` | §4.7.5. `bindsBalanced()` sees facade-level calls only; the backend restore obligations on blit, depth attachment, initialization, and steady copy are contracts checked through restored-binding identities in their recorder events. Generic `calledInOrder`/argument inspection distinguishes combined attachment and initialization from depth-only attachment and steady copy. Borrowed handles are never counted as created/leaked/deletable; forged/wrong-origin rejection appends no call. Phase 4's fixed-terminal assertions remain unchanged | 2, **4**, 5, 8 |
| `CompileResult` / `LinkResult` / `ValidateResult` | never-throwing result types carrying driver logs | 4 |
| Candidate sampler initialization — D-P1-59 | §4.7.4 exact SamplerUnitAssignment/result and initializeSamplerUnits transaction; P5 policy supplies integers, P4 calls before validate, exact private prior-selection restoration, false restoration poisons admission; recorder/script contracts incorporated | **4**, 5, 2 |
| Sampler normalization — D-P1-60 | §4.7.4 prepareUnitBindings(occupiedUnitMask) after P5 complete preflight, plus public useFixedFunction normalization before vanilla/terminal draws; NONE has no sampler objects, optional P14 strategy owns native dispatch | **5**, 7, **14**, 2 |
| Historical P3 schema22 receipt — D-P1-61, superseded by D-P1-65 | D-P3-72 supplied containing/nested/inspection equality and MaterializedSource-v22 at that adoption. BLOCK isolated forced11300 provenance remains P3/P9-owned; current admission is schema23 below, not22 | all receivers retain exact-current admission |
| Runtime sampler demotion — D-P1-62 | Receive P14 D-P14-26: before NONE drawing, backend replays complete authenticated latest owner parameters to affected owned texture objects through private baseline mapping, with binding/error/restoration checks. Failed/unproven replay contains/rebuilds; no borrowed mutation, enum-only fallback or lost live/retiring accounting | 5, 13, 14 |
| Target-bearing synchronous values — D-P1-63 | Complete §4.7.7a TextureSpec/TextureData/TextureRegion/TextureExtent/PixelLayout/TextureAllocationTarget and P5-owned closed format vocabulary; distinct 1D/2D/3D/RECT dispatch, exact mips/bytes/ownership/restoration; existing depth-copy verbs unchanged | **5**, **13**, 14 (value reception), 2 (recording), 7/8 (depth consumers) |
| Complete object baseline — D-P1-64 | Every successful owned setParameters maintains complete latest object state alongside sampler cache; ordinary all-unit fixed-function clearing is safe without deferred replay; §4.7.7a failure/borrowed restrictions incorporated | **5**, **13**, **14**, 7, 2 |
| Exact-current schema23 — D-P1-65 | CURRENT_SCHEMA_VERSION=23, matching containing/nested IdMappingInput/received inspection and MaterializedSource-v23 before derive/retain/reuse; prior numeric receipts including D-P1-61 are historical. Nine metadata-only trees/projectionVersion1, assets/native/options/parameter contracts unchanged; P3 alone parses range selectors | all receivers |
| Captured target limits — D-P1-66 | §4.7.2 exact added fields/keys, capability-gated positive-or-unsupported-zero probes, fail-closed capture/parse, target-axis admission and recorder equality; §4.7.7a incorporates them | **2**, **5**, **13** |
| Mandatory typed color clear — D-P1-67/69 | Complete §4.7.4b ColorClearValue and framebuffers.clearColorAttachment grant, numeric-class/positional-route checks, GL3/EXT/legacy dispatch, full-extent private state restoration including capability-legal actual rasterizer-discard save/disable/finally restore, exact recorder events/scripts and failure retention | **5**, 2; 14 implements behind facade only |
| Complete conventional participation — D-P1-68/70 | P10 D-P10-29/30 exact complete VertexInputPlan/ConventionalInput receipt in §4.7.6; completed BLOCK producer includes real late UV1 completion, partial ITEM/filler grants none; source-absent COLOR/UV1 inheritance/capture exclusion, whole-plan identity and exact restoration. Recorder borrowedVertexList(String label, VertexLayout layout, VertexInputPlan capturePlan) validates and retains complete immutable authority before first replay; old overload removed | **10**, 7, 2 |
| Owned texture materialization — D-P1-71 | §4.7.3 logical create retains label only; first admitted exact-target allocation/known2D initialization materializes and labels; `label` on a live-but-unmaterialized owned handle is recorded-only (D-P1-73) — retained/updated with no GL, no queued `GLError` and no native-label event, applied once by the first admitted materialization before storage work, dropped if deleted first, foreign/borrowed handles unchanged; unmaterialized delete issues no native delete; native deletion zeroes affected bindings/cache and never restores deleted names, preserving lease retirement | **5**, **13**, **14**, 2 |
| **Cached linked geometry inspection — D-P1-48** | `ShaderService.linkedGeometryInput(ProgramHandle)` returns cached `Optional<LinkedGeometryInputPrimitive>` under §4.7.4a's exact successful-link/lifetime checks; empty only no geometry. P4 compares it to finalized P3 effective input before validate/publication, whole-candidate failure on mismatch. No GL/source query or runtime active-program exposure; recorder query event included | **4**, 2 |
| `StateService` state verbs + `snapshot()` / `restore()` | ordinary §G4.6 perturb/restore for viewport, clears, depth mask/test, blend, alpha and fog through GlStateManager. P4 per-program duration locks instead require D-P1-57 lockAlphaBlend and opaque close; immediate verbs cannot substitute and are suppressed for held aspects. Fullscreen establishes no state | **4**, 5, 6, **7** |
| **Pixel-transfer verbs** — `FramebufferService.readDepthPixel(f,x,y)`, `initializeDepthTextureFromFramebuffer(src,dst,region)`, `copyDepthToTexture(src,dst,region)`, `TextureService.upload(t, TextureData)`, `UniformService.upload(loc,int,int)` (ivec2) and `upload(loc,int,int,int,int)` (**ivec4**) | §4.7.4; value types `TextureData` / `TextureRegion` / `PixelLayout` / `BlitSpec`. Depth initialization is first-copy exact-format storage definition on an owned destination; steady copy requires matching defined storage. Neither accepts a foreign destination, and both restore framebuffer/texture bindings | **6** (the v0.1 synchronous `centerDepthSmooth` readback; `atlasSize`/`eyeBrightness`; `blendFunc`), **5** (`depthtex1`/`depthtex2` first and steady copies plus formats), **8** (shadow depth→`shadowtex1`), **13** (noise, companion atlases, custom textures) |
| **The facade's stated non-verbs**, with requesters and adjacent owners | §4.7.4's closing table remains binding: async/PBO and general colour/texture readback, `ivec3`/`mat3`, colour mask, face culling, free-standing pixel-store state, a **general** pre-link parameter setter, instanced draw, and binding Minecraft's own framebuffer. §0.25 serves the legacy geometry triple only; it is no longer an absent operation. Minecraft's FBO is not `bindDefault`'s name 0: Phase 7 must arrange the v0.1 final-to-vanilla bind through vanilla's path, with Phase 5 the second requester if its estate needs that FBO as a copy/blit source (§4.12). `bindsBalanced` cannot prove an internal/vanilla bind. `ivec4` is already served. Fullscreen and approved prepared-submission repetition use P7 v0.5 policy, P6 instance uploads and unchanged P5 estate/flip law; D-P1-47 replaces the gbuffers/shadow open case without granting an instanced draw | **14** async; **13** texture readback; **7** colour mask/anaglyph, face culling, framebuffer bind and repetition policy; **10** existing geometry adapters; **8** single shadow traversal; **6** `instanceId` upload; **5** estate policy and second framebuffer requester; **3** flag ownership and detection; **4** general pre-link requests and instance metadata |
| **Legacy geometry source grant / consumer migration** | P3 binding §5 and its exact CURRENT_SCHEMA_VERSION retain D-P3-68's GeometrySourceRequest.None/PreserveNative and GeometrySourceForm.None/CoreLayout/NativeLegacy, same-build source/language/maps/catalogs, source-layout precedence and route-bound fingerprints; Translate remains removed. D-P3-69's immutable post-archive PackConfiguration.assets()/PackAssetSnapshot remains P3-owned, not a P1 facade I/O grant or binary artifact permission. D-P1-56 receives D-P3-70 without another version authority. §11.4 incorporates P4 migration; native configure/cached linked-input comparison unchanged. Owner/receiver verification and runtime/integration proof remain required | **3**, **4** |
| `DebugService` | present in v0.1, active at v0.5 | 4, 5 (call sites), **14** (implementation) |
| **Approved prepared-submission countInstances boundary; no new facade verb** | §11.4/D-P1-47 incorporates the maintainer's 2026-09-07 v0.5 decision: N adjacent native submissions of already-prepared geometry, IDs `0..N-1`, private synchronous P7 policy/P10 draw adapters beneath authenticated main or shadow scopes; P8 traversal unchanged. No instanced draw, renderer-extension API, compilation-time expansion or world/event replay | **7**, **10**, **8**, **6** |

**Explicit note to Phase 2:** your declared input is "`PHASE_1_DOC.md` (module layout, facade,
`GLCapabilityProfile`)". All three are in §2.1, §4.7.4, and §4.7.2 respectively; the serialization
format your replay depends on is §4.7.2's text form, and the recording backend is §4.7.5. **The
capture mechanism is yours to drive, and it already exists** — you own the fixture *set* and its
refresh workflow (§8.3), and the way a fixture is produced is `mod.glue.CapabilityProbe` under
`-Dschmaloogium.debug.dumpCapabilities`, with `-Dschmaloogium.debug.recordGL` producing a
`GLCallLog` from a live session in the same format your golden files use (§4.9.3, §4.7.5). Do not
design a capture path; drive these. What Phase 1 does *not* give you: the fixture set itself, the
golden-file format for anything other than `GLCallLog.render()`, and any answer to OQ-10.
Your derived-artifact workflow must keep pack source text out of goldens and rendered images out of
the repository; committed image oracles are manifests, hashes, and provenance. Golden regeneration
must be explicit through `-PupdateGoldens`, must never run automatically, and must still fail the
run in which it regenerates artifacts (§8.3, §11.4).

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
| `EngineDiagnostic`, `DiagnosticSeverity`, `UserChannel`, `DiagnosticReporter` | §4.9.4, including D-P1-45's explicit Phase 2 source-free projection permission; unchanged fields/channels and no committed args/detail | **2**, 3, 4, 5, 6, 7, 11, **12** (GUI is a channel consumer) |
| **Debug-flag namespace and the four reserved flags** | §4.9.3 | 3 (`saveSources`), **2** (`dumpCapabilities` and `recordGL` — the fixture and call-log capture path your harness drives), 14 (`glLabels`) |
| Mixin config slots (three, by phase) + package placement | §4.5.2. **Two duties come with them, both yours (Phase 7):** confirm Unimined actually generates a refmap for the first config that lands — the template's `main` branch has never carried one, so the machinery is unexercised in this checkout (§12 item 33, and it **blocks** hook work if it fails); and spot-check `compatibilityLevel: "JAVA_8"`, inherited verbatim from the template snapshot while our source level is Java 25, the first time a mixin uses a language feature that survives to bytecode | **7**, 10, 13 |
| SRG-targeting policy and the `schmaloogium$` prefix | §4.5.3 | 7, 10, 13 |
| `SchmaloogiumMixinPlugin` slot on the MOD config | §4.5.2 | 10 |
| `CompatCheck` / `CompatVerdict` / `CompatContext` / `BailRegistry` | §4.10 | **10** (policy), 7 (the bail hook) |
| **Early class-only compatibility — D-P1-50** | §4.10's `EarlyCompatContext`, `EarlyCompatCheck`, early registration/evaluation and sticky whole-family MOD veto are incorporated. No pre-GL profile, class initialization, speculative mod-list result or ungated preinit attachment | **10**, 7 |
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
| **Capability probe fails or returns nonsense** (missing entry point, query error, nonpositive supported maximum) | 4 | D-P1-66: CapabilityProbe catches and logs on schmaloogium.gl, reports shaders-off with a chat diagnostic, and publishes no usable native profile/device. Never manufacture a conservative GL2.1 profile or target maxima after failed capture. Explicitly labelled synthetic headless fixtures may contain deliberately chosen coherent limits, but are not native capture, runtime fallback or proof of support. No exception escapes display init. |
| **A capability gate fails at init** (pack needs more draw buffers / attachments / units than the profile offers) | 4 | Pack turns off gracefully; `EngineDiagnostic(ERROR, CHAT)` naming the shortfall; `schmaloogium.gl` line with the profile values. Vanilla rendering is untouched because no GL object was created yet. |
| **A shader fails to compile / a program fails to link or validate** | 3 | The facade returns a failed `CompileResult`/`LinkResult`/`ValidateResult` — it never throws. Phase 4 deletes the program, emits `EngineDiagnostic(ERROR, SHADER_GUI)` carrying the driver log, and resolves through the backup chain. |
| **Legacy geometry pre-link configuration is rejected or its immediate drain is nonempty** | 3 | Phase 4 aborts before link, cleans up the entire candidate program and its shaders, reports the geometry source/declaration plus capability/driver diagnostic, and resolves the backup chain. No stage-only disable, clamped count, partial configuration or claimed two-span translation success (§4.7.4) |
| **A built-in uniform's upload fails at the driver level** | **2** | The backend records a `GLError`. Phase 6 drains, uploads the set, and drains again; if that drain is empty the sweep cost **one** `glGetError` — the leading drain elides its query (nothing mutating **through the facade** since the last drain) and the trailing drain's loop stops at its first `GL_NO_ERROR` (`[D-P1-30]`). If it is **not** empty, the record names the *sweep* rather than a uniform — GL holds only the first error in a flag until that flag is cleared — so Phase 6 re-uploads the set **draining between uploads**, which puts one mutating call in each window and names each failing uniform exactly (`[D-P1-32]`). The re-upload uses the values **already computed for this sweep** — `glUniform*` is idempotent on the bound program, so the replay changes only which drain window each upload lands in, and re-running the sweep would re-enter world-state providers whose cadence and smoothing math are Phase 6's own scope to design. It then **disables those uniforms only**, leaving the program running. `EngineDiagnostic(WARN, LOG_ONLY)` on `schmaloogium.uniforms` per §4.9.4's severity map. The facade supplies the signal (`GLDevice.drainErrors()`, §4.7.4); the disable policy is Phase 6's. **If the replay reproduces nothing** — `OUT_OF_MEMORY` need not recur, and the error may not have been ours at all, since the elision bit tracks *facade* calls while the GL flag is per-context (§4.7.4) — the drain is real but unattributable and falls to the 3→4 row below. |
| **A custom uniform's expression errors at runtime** | **1** | **Not a foundation failure mode, and recorded here so §6 maps the whole ladder.** `DESIGN.md`:378 scopes rung 1 to *custom* uniforms, and its Phase 11 spec puts the behaviour in `engine.expr`'s evaluator at **v0.4** (§G5.1 l. 577; the spec at ll. 2081–2083 is *"Pure `:engine` code"*) — above the facade, with no GL call involved. Nothing in this phase observes it; Phase 1 supplies only `Log` and `EngineDiagnostic` for it to report through. |
| **A single feature's GL call fails** (a capability the pack asked for is unsupported in practice) | **2a** | The drain names the failing operation, the owning phase turns *that feature* off and continues. **This row carried an explicitly *unnumbered* rung through ten rounds, and REV2 numbers it.** §11.5 item 4 carried the gap upstream where it was found — rungs 1 and 2 are both about uniforms, rung 3 is a program, rung 4 is a capability gate at init, and none of them covered a pack feature whose GL call fails at runtime while the program keeps running. `DESIGN.md` §G2.4 now inserts **rung 2a** at exactly the position item 4 proposed, *"a rung between 2 and 3"*: *"A pack **feature** whose GL call fails at runtime — neither a single uniform (rungs 1–2) nor the whole program (rung 3) — disables **that feature** only, at the owning phase's discretion, while the program keeps running."* It is lettered `2a` rather than renumbering 3–5 **so that existing §6 maps stay valid**, and REV2 names *this row* as the one to relabel at its next §G1.3 fix-up. This is that fix-up, the relabel is the entire change, and no behaviour moves. A *custom* uniform whose **upload** fails — as opposed to its evaluation — still lands here, served by the same drain and the same disable-one behaviour rung 2 describes. |
| **A facade call fails at the driver level, and the failure is not attributable to one uniform or feature** | 3→4 | The LWJGL3 backend records it in the drain and logs a diagnostic on `schmaloogium.gl`; it never throws through a mixin into vanilla's call stack. Mutating verbs return `void` — the drain, not a return value, is where the caller learns of it. Persistent failures escalate to a pack-level bail. **This row is also where rung 2 lands when its replay finds nothing** (`[D-P1-32]`), and that happens **for either of two reasons — this row is the destination of five delegations and is written to receive both** (§4.7.4's precondition (ii) names the set): (a) `GLErrorKind.OUT_OF_MEMORY` is the kind that need not recur; (b) **the error may never have been ours**, since the elision bit tracks *facade* calls while the GL error flag is per-context (§4.7.4), so a window can hold an error this facade did not cause and a replay of our own uploads will of course reproduce nothing. **Under (b) this row's *Failure* label names the detection point, not the origin** — the drain window is a *facade* window, so a driver error surfaces on a facade call that did not produce it. The distinction is carried in the cell rather than by widening the label, because the label is the name five sites delegate to (§4.7.4's precondition (ii), §5.2's GL-error row, `[D-P1-30]`, `[D-P1-32]` and §11.4) and a reader who reaches this row reaches this sentence (V11-4). A replay that comes back clean **repeatedly** is evidence for (b) rather than (a), and is the case §11.4's frame-level hand-off exists to reduce. Either way a detected failure that no window can attribute is unattributable by definition, and the honest response is to log it and keep the program running rather than disable an arbitrary uniform or pretend nothing happened. |
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
| `CapabilityProbe` | Render thread, once, at display init — **specifically at `OpenGlHelper.initializeTextures` @`RETURN`**, stage 2 of the §4.13 bring-up sequence (`[D-P1-37]`); Phase 7 owns the hook's catalog entry |
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
state barrier (RESEARCH.md §4.2), so a factor of two on a synchronous driver query is paid on every
actual activation; that workload-dependent switch count is not the catalog cardinality. `[D-P1-32]`'s attributed replay does not
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

`configureLegacyGeometry` and its two-drain consumer transaction are build-time only, never
program-use or per-draw work. They retain only private candidate metadata, not source text;
the existing GL-error cadence and hot uniform-upload path are unchanged.

---

## 8. Testability plan

Attempt-6 planned evidence (D-P1-66/67): round-trip distinct ordinary/3D/rectangle
maxima; accept equality and reject maximum+1 independently on every used axis;
unsupported rectangle records0 and issues no enum, advertised-but-broken query fails
capture, missing/negative/gate-inconsistent serialized keys fail before recording.
Exercise GL3 and GL2+EXT integer clears of mixed RGBA8/RGBA32F/R32I/R32UI with
route holes and nonidentity attachment numbering; check exact typed payload and intended
attachment only, unsupported GL2 integer rejection, UINT_MAX, fractional/negative
conversion, NaN/infinity rejection. Begin with nondefault FBOs/routes, scissor, indexed
masks, dither/sRGB/clamp and integer-origin legacy clear state; all-success restores
exactly, work/restore errors retain failure and block successful full-clear consumption.
These are unexecuted acceptance cases, not new conformance results.
P10 receipt evidence (D-P1-68): same physical56 layout with differing original COLOR/UV1
masks must not share stale state/VBO/list products; compare live client/VBO and cached
playback under two distinct current colors/lightmaps, changing them between playbacks.
Absent inputs inherit each draw's current state and capture freezes neither; supplied
inputs and established generated normals retain their behavior. Verify exact restoration
and mutation-free forged/stale/mismatched-plan rejection. Planned, not executed.
Attempt-7 planned cases (D-P1-69/70/71): enter GL2+EXT integer and noninteger clear
with discard enabled and observe full contents plus exact enabled predecessor afterward;
repeat core and unsupported-discard contexts, checking no unsupported query/change.
Inject save/disable/work/restore failures and require no successful full-clear consumption.
Issue two lists with identical physical layout/geometry but different captured masks and
generic unions; valid first replays pass, cross-plan first replays reject before mutation,
caller collection mutation cannot change retained authority, and invalid layout association
rejects at issuance. Receive P10 ITEM→BLOCK→real brightness completion and missing/throwing
completion cases; no incomplete product can reach bind. Logical create/delete without
allocation makes no native texture, all four first targets label only their exact object,
and deleting a bound materialized texture never resurrects its name. Planned, not executed.

### 8.1 Headless unit tests owned by this phase

R32 planned proof: routes 0N2, N0, 0N, NN, empty, duplicate attachments and both capacity
limits preserve output locations or reject before GL. Hold OFF/explicit alpha/blend while
ordinary vanilla setters attempt all mutations; observe unchanged effective draws/cache,
ordinary unlocked-aspect behavior and exact pre-lock restoration including disabled factors.
Inject each acquisition/close/notification failure and assert no active shader admission,
rollback attempts and idempotent consumed closure. These are planned checks, not execution.

| Test | Module | Asserts |
|---|---|---|
| `SeamClasspathTest` | `:engine` | Constraint **C-1**, classpath half — no forbidden coordinate on `main`'s compile or runtime classpath (§4.3 layer 2) |
| `SeamBytecodeTest` | `:engine` | Constraint **C-1**, bytecode half — no forbidden type referenced by any compiled `:engine` class (§4.3 layer 3). **This is the test the Impl gate names** |
| `SeamInternalsTest` | `:mod` | Constraint **C-2** — no `:mod` class references `com.schmaloogium.engine.*.internal.*` |
| `SeamLwjglConfinementTest` | `:mod` | Constraint **C-3** — no `org.lwjgl*` reference outside `com.schmaloogium.mod.glue` and its subpackages (the mechanical half of §G4.6) |
| `SeamConformanceDependencyTest` | `:conformance` | Constraint **C-4** — no `:mod` artifact on `:conformance`'s classpaths and no `com.schmaloogium.mod.` reference in its classes (§8.2) |
| `GLCapabilityProfileSerializationTest` | `:engine` | Round-trip `write` → `parse` is identity; output is sorted and deterministic; a hand-written fixture parses to the expected values |
| `LinkedGeometryInspectionTest` | `:engine` | Empty iff no geometry; successful native/core input equals committed link metadata, failed metadata never yields successful link, invalid/deleted/cross-device inspection rejects, query does not select a program; P4 owns projected-versus-actual mismatch cleanup/fallback proof |
| `GLCapabilityProfileDerivationTest` | `:engine` | `atLeast`, `hasExtension`, and `supportsMipmapGeneration()` (true at 3.0, false at 2.1 — the RESEARCH.md §4.1 gate) |
| `RecordingGLDeviceTest` | `:engine` | Calls are logged in order with correct arguments, including distinct `shaders.use`/`shaders.useFixedFunction`, `framebuffers.attachDepth`/`attachDepthStencil`, and initialization/steady-copy events. Handles are distinct and never reused after delete; recorder-issued borrowed-depth handles carry a private device origin and are not leak-counted; copied depth is summarized, not logged; restored read/draw/texture binding identities are present. Scripted compile/link/validate/GL-error paths, bounded-log behavior, supplied-log behavior, and stable rendering remain covered |
| `FramebufferDepthContractTest` | `:engine` | Exhaustive owned/ordinary-foreign/authenticated-borrowed/forged-marker/wrong-device/deleted matrix for every texture-accepting framebuffer verb; all invalid cases reject pre-GL with an unchanged log. `attachDepth` removes stencil, `attachDepthStencil` accepts only exact packed metadata and attaches the same handle to both points, and both report restored read/draw bindings. Initialization accepts owned destination only and defines exact format/extent; steady copy rejects undefined or mismatched storage and never redefines it; both restore read/draw/texture bindings |
| `ReplayAssertionsTest` | `:engine` | Each assertion passes on a conforming log and fails informatively on a violation, including balanced binds, leaks, and use-after-delete. Fixed-terminal fixtures distinguish `shaders.useFixedFunction`; depth fixtures distinguish combined attachment and initialization from depth-only/steady operations, assert restored binding identities, and prove borrowed handles are neither created/leaked nor deletable |
| `LegacyGeometryPreLinkContractTest` | `:engine` | Extension-present configure/clean-drain/link versus GL-3.2-without-extension rejection; exact positive count and closed topology; null/forged/wrong-device/deleted/no-geometry/after-link/wrong-thread rejection without recorder mutation. Parameter-error scripts followed by the required abort emit no link/use and leak no candidate objects; link-failure scripts cover total-output limits. Reconfiguration before link replaces the whole triple; deletion removes its state. Tests of Phase 4's cleanup/backup selection belong to Phase 4, not this facade-only test |
| `FullscreenGeometryContractTest` | `:engine` | On QUADS-capable profile: native clean configure→link→use draws TRIANGLE_STRIP; ordinary/core TRIANGLES does too; plain/fixed resumes QUADS. Scripted effective POINTS overriding native TRIANGLES rejects fullscreen without submission; unknown core metadata fails link. Failed configure/link never publishes active state, failed/uncertain use does not authorize guessed draw, active deletion invalidates, fresh issuance cannot inherit metadata, nested use/fixed/reselection restores exact previous route; draw/restoration errors cannot appear as clean completion. Recorder failure scripts and route arguments defend these observable outcomes, not source-string assertions |
| `BailRegistryTest` | `:mod` | `Ok`/`Degrade`/`Bail` aggregation; a throwing check is treated as `Bail`; evaluation is idempotent |
| `EarlyBailContractTest` | `:mod` | Early-only evaluation requires no GL/mod-list/game initialization; early exception/positive result vetoes the complete family and remains latched; duplicate id rules and delayed sink routing preserve one outcome; full checks still run at runtime points |
| `VertexInputContractTest` | `:engine` | All source/mode pairs, authentic/forged/foreign/retired sources, bounds/overflow, wrong thread, layout/plan/capability rejection, actual-versus-expected input mismatch before mutation, LIFO nesting and exact predecessor restoration, failed partial setup rollback and failed rollback admission closure; list capture independent of current program, replay requires matching captured category and live source; no owned-handle leak from borrowed issuance |
| `MixinConfigAgreementTest` | `:mod` | For each config, the `@Mixin`-annotated classes in its declared `package` **minus any sub-package another config declares** are exactly the classes its arrays name (§4.5.2a, `[D-P1-38]`) — the drift insurance taken instead of a class-scan plugin. The sub-package exclusion is required, not optional: the three declared packages are nested (§4.5.2 observation 1) |
| `LogChannelTest` | `:engine` | Every `LogChannels` constant is unique and starts with `schmaloogium.`; the no-op sink is active before installation |
| `DiagnosticRoutingTest` | `:mod` | `CHAT`/`SHADER_GUI`/`LOG_ONLY` route correctly; `CHAT` with no player degrades to log; every diagnostic reaches the log |

`VertexInputContractTest` additionally models D-P1-55's distinct enabled generic-zero
predecessor, unrelated enabled conventional/client-unit/generic capture arrays, capability
absence and nested bindings. Assert admitted values and complete pre/post state equality,
including implicit current-value effects; inject failure after isolation, selector movement
and pointer setup, requiring zero submission and exact rollback. A state-write trace alone
does not establish which vertices a driver fetched; the live proof below remains required.


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

**Derived artifacts are governed too (REV2) — and both kinds above are derived artifacts.** §G6
ll. 672–679 extends the fixture policy past pack files to *"goldens, baseline screenshots, diff
reports and run manifests"*, on the reasoning that those are *"where re-hostable content leaks"*. It
is binding on every phase and adopts Phase 2's policy: golden files carry **no pack source text**
(`[D-P2-5]`); **no rendered images enter the repository** (`[D-P2-6]`) — committed oracles are
*manifests*, hashes plus provenance, with the images left in local/CI caches; and goldens are **never
auto-updated**, regeneration being an explicit `-PupdateGoldens` flag that still fails the run it
regenerates in. Phase 1 is compliant **by construction rather than by care**, which is worth stating
plainly so the clause is not mistaken for new work:

- A `GLCallLog` golden is a rendered *call log* — op names, arguments, summaries — and §4.7.5 already
  forbids the one thing that could smuggle pack text into it, by logging bulk data *"by summary, never
  by content"*. At v0.1 there is no pack to quote from in the first place (§1.1).
- A `GLCapabilityProfile` fixture is driver-reported strings and integers (§4.7.2) captured from
  hardware, not from a pack.
- Neither is an image, and this phase commits no image of any kind.
- **The `-PupdateGoldens` half is Phase 2's, and lands there rather than here.** §8.3 item 2 already
  assigns Phase 2 the golden-file *workflow*; the REV2 clause constrains one property of that
  workflow, so it is flagged to Phase 2 in §11.4 rather than designed by this session. What Phase 1
  owes the flag is the determinism its failure mode depends on — a regeneration that "still fails the
  run" is only meaningful if a re-render of unchanged behaviour reproduces byte-identical output — and
  that is already a stated guarantee with a test behind it (`RecordingGLDeviceTest`, §8.1).

### 8.4 Conformance tiers

Phase 1 exercises **no** conformance tier. T0–T3 are defined by Phase 2 and first run when a renderer
exists (Phase 7). What Phase 1 contributes to that future is the third slice of §G6's testability
split — the machinery for "per-phase headless tests … against the `engine.gl` facade / recorded
`GLCapabilityProfile`s" — and the `:conformance` module the harness lives in.

---

**D-P1-48 live proof, planned not run.** At the first shader-capable client milestone,
compile/link native minimal and ordinary core fixtures on a compatibility context with
QUADS available. Exercise fullscreen under TRIANGLES, plain/fixed, and incompatible linked
input; capture the actual primitive/error result and restored caller state. Exercise native
layout override on GL3.2+ and compare effective link metadata with dispatch, not the ARB
configured-value query. P2 owns conformance evidence and explicit golden updates.
P10's separately approved native quad-conversion policy needs its own winding/provoking-
vertex/primitive-ID and lifetime evidence; fullscreen success proves none of it.

**D-P1-55 native-input proof, planned not run.** On a legal compatibility context,
use distinct valid position sources A (authenticated conventional client/VBO) and B
(enabled generic zero). Observe submitted positions from A for both live source routes.
Capture A with unrelated enabled conventional color/edge-flag, non-plan client texture
unit and non-plan generic arrays containing distinctive values; replay while varying those
foreign arrays/current values and observe that no foreign values were captured. Use a
fixture shader/output or feedback/readback that observes values, not just enable-call logs.
Compare the complete actual predecessor after live draw, capture, replay and nested scopes:
descriptors/per-pointer buffer associations, enables, global binding/client selector and
legal current values, including color/normal/texture/generic capture side effects.
Inject setup failure after each mutation class, checking no draw/list publication and exact
restoration; rollback error must close admission and invoke existing P7 containment.
Exercise capability-absent profiles without unsupported queries, generic zero without a
current-value query, and list replay after the original client storage is retired.
Do not interpret recorder success or these planned cases as driver proof.

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
| `GLDevice`, `ShaderService`, `UniformService`, `TextureService`, `FramebufferService`, `StateService`, `DrawService` | `v0.1` | Interfaces + the LWJGL3 implementation, including both explicit program-selection modes and the authenticated depth-only/combined depth-stencil attachment operations (`[D-P1-39]`, `[D-P1-40]`) |
| `configureLegacyGeometry` and closed topology enums | `v0.1` | Native backend plus recorder/error transaction (D-P1-44). Implementation consumption waits for the §5.2 source grant, Phase 4 migration and fresh whole-document reviews; no complete core translator is claimed |
| Effective linked-input metadata / fullscreen selection / recorder semantics (D-P1-48) | `v0.1` | Both native and ordinary core geometry, selection-bound tracking, no per-draw query; P7 must adopt and pass the live proof before claiming affected fullscreen support |
| P3 jcpp pin/closure admission (D-P1-49) | `v0.1` | Verified exact dependency/notice/runtime packaging and seam proof before P3 production code |
| Vertex source/input/recording and early-check mechanism (D-P1-50) | `v0.1` | Base layouts and authenticated native primitive admission precede affected `.gsh` support; P10 extended CLASSIC56 producers remain `v0.3`, not a reason to claim unimplemented early native support |
| Pixel-transfer verbs (`readDepthPixel`, first-copy `initializeDepthTextureFromFramebuffer`, steady `copyDepthToTexture`, `TextureService.upload`, ivec2 and **ivec4** uploads) + `TextureData`/`TextureRegion`/`PixelLayout`/`BlitSpec` | `v0.1` | Interfaces and LWJGL3 implementation. Phase 5's depthtex1/depthtex2 first copy defines exact storage; later copies preserve it. Phase 6 owns center-depth readback/ivec uploads, Phase 8 consumes the same depth-copy split for shadowtex1, and Phase 13 consumes texture upload |
| GL-error surface (`GLDevice.drainErrors()`, `GLError`, `GLErrorKind`, `ReplayAwareGLError`) + the backend's `glGetError` policy | `v0.1` | §G2.4 rung 2 is Phase 6's v0.1 scope-in, so both the drained signal and its replay verdict cannot be later than v0.1. The **cadence** ships with it: batched by default with window-scoped attribution — each drain a `glGetError` loop, elided entirely when nothing mutating has happened **through the facade** since the last one — and per call under a debug context or the `recordGL`/`glLabels` flags (`[D-P1-30]`, `[D-P1-32]`, `[D-P1-42]`) |
| Phase 7 frame package homes | `v0.1` | The packages themselves are architectural slots established at foundation time; Phase 7 fills them without moving policy into mixins or weakening C-1 through C-4 (`[D-P1-41]`) |
| Opaque handle types + owned/foreign/borrowed lifetime and provenance rules | `v0.1` | `BorrowedDepthAttachmentHandle` is a `TextureHandle`, not a fifth category; `FramebufferDepthContractTest` proves backend origin authentication and the permission matrix; `noUseAfterDelete()` ships with `ReplayAssertions` |
| `RecordingGLDevice`, `GLCallLog`, `ScriptedResponses`, `ReplayAssertions` | `v0.1` | D-10 requires the headless path from week one |
| `CapabilityProbe` + `dumpCapabilities` | `v0.1` | |
| Engine bring-up sequence — **stage 2** (the capability-probe moment) wired | `v0.1` | §4.13, `[D-P1-37]`. Stage 1's obligations already ship on the FML lifecycle (§4.9.1's sink, §4.10's bail point 1); **stage 3 is recommended, not wired** — Phase 7 places it when its frame driver has a use for it |
| `mod.glue` **vanilla-texture provider slot** | `v0.1` | §4.12, `[D-P1-36]`. The slot, its type (`ForeignTextureProvider`, §4.7.3) and its shape are v0.1 because App B.3's units 0 and 1 are v0.1 contract; its **contents** arrive with their owners — the unit-map keys with **Phase 5**'s set at v0.1 (**Phase 6** points the samplers), the `minecraft:` resource-location keys with **Phase 13** at `v0.5` (§4.7.3's vocabulary (b)). No facade **verb** moves; the four handle sub-interfaces do lose `sealed`, without which a `mod.glue` handle cannot exist at all (§0.12) |
| `DebugService` **interface** | `v0.1` | Present so call sites exist |
| `DebugService` **implementation** (KHR_debug labels/groups) | `v0.5` | Phase 14 |
| `schmaloogium.debug.glLabels` | `v0.5` | Phase 14 |
| `schmaloogium.debug.recordGL` (live decorator + bounded log) | `v0.1` | Ships inside the merged jar; opt-in and off by default (§4.9.3, §7) |
| `schmaloogium.debug.saveSources` (**flag name reserved**; `LogChannels`-style constant, no behavior) | `v0.1` | The dump itself is Phase 3's and arrives with the preprocessor — the reservation is what §G4.5 asks of this phase |
| `Log`/`LogSink`/`Logs` + fixed channel list | `v0.1` | |
| `EngineDiagnostic` + routing (`CHAT`, `LOG_ONLY`) | `v0.1` | |
| `EngineDiagnostic` routing to `SHADER_GUI` | `v0.4` | The sink is Phase 12's screen; the store exists at v0.1 |
| `MixinConfigs` manifest attribute + `schmaloogium.default.mixin.json` | `v0.1` | The config exists; its contents arrive with Phase 7 |
| `schmaloogium.preinit.mixin.json` (empty, reserved) | `v0.1` | No preinit vertex placement is granted |
| `schmaloogium.mod.mixin.json` | `v0.1` | Geometry-only MOD hooks and functional early veto; CLASSIC56 hooks v0.3 |
| `SchmaloogiumMixinPlugin` and MOD `plugin` key | `v0.1` | Ship together with real D-P1-50 early evaluation; not a permissive skeleton (D-P1-53) |
| Mixin dev flags (`mixin.debug.export`, `mixin.checks.interfaces`) | `v0.1` | |
| Mixin config ↔ package agreement test | `v0.1` | §4.5.2a/D-P1-38; evaluates the actual owner-populated configs, not assumed-empty arrays |
| Phase 8 package homes (`engine.shadow`, `mod.glue.shadow`, `mod.mixin.shadow`) | `v0.2` | Placement grant only; Phase 8 fills them and the existing seam/config-agreement tests enforce them |
| Phase 13 package homes (`engine.textures`, `mod.glue.textures`, `mod.mixin.textures`) | `v0.5` | Placement granted now; Phase 13 fills them at its existing milestone. No placeholder classes, new conformance edge, or earlier texture implementation is implied (`[D-P1-43]`) |
| `CompatCheck`/`CompatVerdict`/`CompatContext`/`BailRegistry` mechanism | `v0.1` | |
| Registered geometry-only early compat checks and evaluation point3 (MOD plugin) | `v0.1` | Required before the approved native adapter family applies; P10 owns policy |
| `BailRegistry` evaluation point1 (bootstrap) | `v0.1` | |
| Full extended checks and evaluation point2 (vertex-format change) | `v0.3` | Requires extended Phase 10; early base subset is not extended readiness |
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

**Current-file override:** the following is July history, not today's executable pin.
§4.2.6a preserves Cleanroom 0.6.10-alpha and the current wrapper/plugin values without
claiming a new availability check; actual implementation verification remains pending.

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

**REV1/REV2 evidence bearing on this drill, and precisely what it does not settle.** §G10's OQ-20 row
now carries Pintonium as *"further evidence seam-hardness is achievable"* (PD §2), with REV2
correcting the backend count — three platform modules in the checkout, which §4.12 counts from the
tree and where it also records that PD §2's diagram's `:babric` is declared in `settings.gradle.kts`
yet absent from it. For *this* drill the evidence buys exactly one thing, and saying which is the
point of the paragraph: **step 2's premise is now demonstrated rather than assumed.** Mapping a
service set onto a structurally different backend model has been carried out at production scale on
this Minecraft version, so a step-2 verdict of *"not expressible"* would be a finding about our
facade rather than about the exercise being unrealistic. It settles nothing in steps 3 or 4 —
Pintonium's core is not headless-testable (PD §2), so it offers no evidence at all about
`NullGLDevice`, which is step 4's whole subject and D-10's actual question. The success criteria
below are unchanged. `[V:observed — PD §2]`, with the three interfaces and the module count re-opened
at source in §4.12 rather than taken from the digest.

**Procedure — the backend-swap drill.**
1. Read Kirino-Engine's *public API surface only* (its README and the public types of its
   RenderPass/Subpass/render-command model). **API surface only** — RESEARCH.md §10.3 records Kirino
   as a custom "Custom Mod Permissions License" with the instruction "Observe API surface only;
   licensing needs review before any integration." Nothing is copied; nothing is derived.
   **REV2 confirms this step was already pointed at the right place, and closes off the wrong one.**
   v1.1's §G8/S5 offered *"local sketch material: `cleanroom-src/projects/kirino`"*; REV2 records that
   the path is an **empty, uninitialized git submodule** — a URL pointer to
   `CleanroomMC/Kirino-Engine` with zero files — and directs sessions to *"track upstream directly"*
   (ll. 755–757). Confirmed in the checkout: `reference-src/cleanroom-0.6.6-alpha/projects/kirino/`
   exists and contains nothing `[V:repo 2026-07-26]`. The drill never cited the local path, so
   nothing here changes; it is recorded because a step that was right by accident and a step that was
   right by construction read the same until one of them is written down.
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
| D-P1-15 | Grouped role services + opaque handles, not a thin GL-verb layer. **Of the handle types only `GLHandle` is `sealed`**; the four sub-interfaces and `UniformLocation` are not | A GL-verb facade is OpenGL with a different package name; it encodes imperative semantics into `:engine` and would make the Kirino swap a rewrite (OQ-20). The sealing was **narrowed** rather than relaxed: with JPMS rejected (§4.3) `:engine` is in the unnamed module, where a sealed type's permitted subtypes must share its **package** — so sealing the leaves contradicted the arrangement §4.7.3 describes in the next sentence (`Lwjgl3GLDevice` in `mod.glue`, `RecordingGLDevice` in `engine.gl.record`, a Kirino backend nowhere in this tree, and `[D-P1-36]`'s `mod.glue` handle) and was uncompilable. `GLHandle`'s `permits` clause keeps the only sealing that states something — four handle types, no renderbuffer — and opaqueness never rested on `sealed` but on the absence of a GL-name accessor plus `SeamBytecodeTest` `[fix-up: PHASE_1_REVIEW_12.md V12-3]` |
| D-P1-16 | `GLCapabilityProfile` has a stable, sorted, human-readable text serialization | It is what "recorded `GLCapabilityProfile`s" means for Phase 2 and what Phase 4/5/6's "recorded-GL run" gates run against; diff-readability matters when a driver update changes one extension |
| D-P1-17 | `LICENSE` carries verbatim GPL-3.0; "or-later" lives in SPDX headers and `README.md` | The license text is version-specific; the "or later" grant is a statement about the work, which is where GPL-3.0's own guidance puts it |
| D-P1-18 | Two-line SPDX header on every source file | Machine-readable, no boilerplate duplication, and the form license scanners actually read |
| D-P1-19 | `:engine` defines a zero-dependency `Log`/`LogSink` SPI instead of depending on log4j | log4j on 1.12.2 comes from the Minecraft runtime; depending on it would be coupling that exists only because Minecraft supplies it, and would make headless tests need a logging backend |
| D-P1-20 | The §4.9.2 channel list is fixed; channels are constants, never composed | §G4.5 assigns the list to this phase; composed channel names make a user's log4j filter meaningless |
| D-P1-21 | Debug flags namespaced `schmaloogium.debug.*`, boolean, absent = off, read after bootstrap | §G4.5 requires `saveSources` reserved; a uniform namespace makes the set discoverable and keeps malformed values away from class loading |
| D-P1-22 | `:engine` emits `EngineDiagnostic` values with lang **keys**; `:mod` routes to chat/GUI/log | The seam forbids Minecraft types in `:engine`, and Phase 12 needs lang keys for the GUI regardless |
| D-P1-23 | The bail registry ships with **zero** registered checks and names no mod ids | Naming Celeritas or Nothirium here would be Phase 10's policy decision made by the wrong session; the landscape moves (RESEARCH.md §2.3) |
| D-P1-24 | CI runs `:engine:test` **and** `:mod:test` as one named "Seam architecture test" step and `:conformance:test` as a second named step, **both placed before `./gradlew build`** | The seam is the project's highest-weight structural risk; its regression should be legible at a glance, not buried in an aggregate build. **Both module tasks** are named because C-1 lives in `:engine` while C-2 and C-3 live in `:mod` (§8.1), so a step running only `:engine:test` would leave half the seam sentence anonymous. **The ordering is load-bearing, not cosmetic:** `./gradlew build` → `check` → `test` runs all four seam tests itself, so a named step placed after it would never execute on the very failure it exists to name — the job would already have aborted inside `build` (§4.11) `[fix-up: PHASE_1_REVIEW_4.md F4-10; PHASE_1_REVIEW_5.md V5-5, V5-3]` |
| D-P1-25 | The facade carries **pixel-transfer verbs** (`FramebufferService.readDepthPixel`, `FramebufferService.copyDepthToTexture`, `TextureService.upload`, ivec2 and ivec4 uploads); its former no-pre-link geometry decision is **superseded by D-P1-44** | Pixel-transfer rationale is unchanged: Phase 6 needs synchronous center-depth readback and typed uploads; Phase 13 fills textures; depth copies target standalone textures. Each verb carries no format/cadence/unit policy. Historical geometry reasoning assumed complete upstream translation, which Phase 3's two-span contract cannot supply; the active facade now admits the narrow native operation, not a general parameter setter. Earlier dispositions remain in §0 addenda `[fix-up: PHASE_1_REVIEW_1.md F-1; PHASE_1_REVIEW_4.md F3-2, F4-7]` |
| D-P1-26 | Shared `GLCapabilityProfile` fixtures live in **`:engine`'s `testFixtures`** source set, consumed by `:conformance`/`:mod` via `testFixtures(project(':engine'))` | C-4 makes `:conformance → :engine` the only legal direction, so fixtures in `:conformance` are unreadable by the `:engine` tests that need them; a fixtures source set keeps the dependency edge legal and Phase 2's ownership of the fixture *set* intact `[fix-up: PHASE_1_REVIEW_1.md F-2]` |
| D-P1-27 | `:conformance` declares its **own** `repositories { mavenCentral() }` (§4.2.4a); `mavenCentral()` is **not** hoisted into the root `subprojects {}` block | Repositories are per-`Project` in Gradle with no inheritance, so without it `:conformance` cannot resolve JUnit and the Impl gate's `./gradlew build` fails in the *test* configuration. Hoisting would fix it in fewer lines but would inject `mavenCentral()` into `:mod` ahead of `dependencies.gradle`'s mod repositories and blur §4.2.3's single-file statement of what `:engine` can see `[fix-up: PHASE_1_REVIEW_4.md F3-5]` |
| D-P1-28 | **A handle is invalid the moment its `delete` returns**; Phase 5 owns re-acquisition across the uninit/rebuild; `ReplayAssertions.noUseAfterDelete()` enforces under replay what the LWJGL backend cannot | Teardown-and-rebuild is a routine v0.1 event (RESEARCH.md §4.1 step 5 — an option change fires it), and a driver may reissue a GL name after a delete, so a stale handle silently addresses a *different live object*. The recording backend's monotonic, never-reused sequence numbers make the misuse detectable; stating the rule is what turns an accident into a contract `[fix-up: PHASE_1_REVIEW_4.md F4-8]` |
| D-P1-29 | **Backend obligation:** every `Lwjgl3GLDevice` verb whose GL state `GlStateManager` caches is issued *through* `GlStateManager`, never through raw LWJGL — `bindToUnit` (unit + bind), every `StateService` verb but `viewport`, and clears | §G4.6 forbids bypassing the cache for state it holds, and `DESIGN.md` makes the consequence correctness rather than style ("the cache would go stale and break vanilla rendering"). `bindToUnit` is the highest-frequency instance, not an edge case: the fixed unit map re-points up to 16 units per program switch (RESEARCH.md §4.2). No signature changes; the rule constrains the implementation `[fix-up: PHASE_1_REVIEW_4.md F4-3]` |
| D-P1-30 | Driver errors surface through a **batched drain** (`GLDevice.drainErrors()`), not a per-call return status, with the backend's `glGetError` cadence stated as part of the contract | §G2.4's **rung 2** needs a signal — rung 1 is Phase 11's expression isolation and never reaches a GL call (§6) — and `DESIGN.md` puts per-uniform GL-error isolation in Phase 6's **v0.1** scope-in, a rung the facade previously gave nothing to act on, since every mutating verb returns `void`. A drain matches the consumer's shape (upload a program's set, then sweep) and keeps both the allocation and the synchronous driver query off the per-call hot path §7 identifies. **The cadence is now stated in GL's own terms** (round seven; the two inline URLs are round **eight**'s, V8-4, which supplied the source round seven read but did not cite): a drain is a `glGetError` **loop** terminating on `GL_NO_ERROR`, because an implementation may hold several error flags and a single call returns and clears an arbitrary one `[V:web]` — the OpenGL `glGetError` reference page, `https://docs.gl/gl4/glGetError`, wording identical at `https://docs.gl/gl2/glGetError` for the GL 2.1-era refpage, read 2026-07-25 — so a single call per drain leaks a flag into the next window and misattributes it. A drain also issues **no query at all** when no mutating facade call has occurred since the previous one, which is what makes the two-drain rung-2 protocol cost one query rather than two. **That bit tracks *facade* calls while the GL error flag is per-context**, so the elision cannot bound a window against vanilla's own GL: a drain window may hold an error this facade did not cause, and it is `[D-P1-32]`'s unattributable branch — not the bit — that contains the case (§4.7.4, and §11.4 for the frame-level remedy, which is Phase 7's to place). **There are two remedies and neither is uniquely sound:** dropping the elision would bound the window against all GL and is rejected on **cost**; a facade-internal guard is unavailable on **mechanism**. What §11.4 hands Phase 7 is the second remedy with its two limits attached — it bounds only the frame-boundary gap, and it is subject to this same elision. The per-call debug cadence is triggered by a debug context or the two GL-facing flags (`recordGL`, `glLabels`) only, not by any `-Dschmaloogium.debug.*` flag: `saveSources` is Phase 3's and `dumpCapabilities` is one-shot, and neither should change a per-frame query count silently. The cadence this decision states is corrected and extended by `[D-P1-32]` `[fix-up: PHASE_1_REVIEW_4.md F4-1; PHASE_1_REVIEW_5.md V5-1; PHASE_1_REVIEW_6.md V6-2; PHASE_1_REVIEW_7.md V7-2, V7-3; PHASE_1_REVIEW_8.md V8-1, V8-4; PHASE_1_REVIEW_9.md V9-1, V9-6]` |
| D-P1-31 | `StateService` gains `depthTest(boolean)` and `fog(FogState)`; **colour mask and face culling stay out**, each with a deferred-table row naming who would request it; `DrawService.fullscreenQuad()` establishes no state | RESEARCH.md §4.4's composite/final block ("identity ortho, fog/depth/blend disabled") is **v0.1**, and `depthMask` is depth *writes*, a different bit of state — so two of its four elements were unexpressible. The other two are deliberate: the facade has no matrix-state verb at all, and colour mask belongs to the anaglyph-aware final `DESIGN.md` assigns to Phase 7 `[fix-up: PHASE_1_REVIEW_4.md F3-3]` |
| D-P1-32 | **Rung-2 attribution is a property of the caller's drain window**, not of a per-call return status and not of a developer flag. A window holding exactly one mutating **facade** call yields a record naming that call; a window holding several yields **at most one record per driver error flag**, carrying `subjectLabel = "(batched, N calls)"`. Phase 6's rung-2 protocol is therefore: drain, upload the program's uniform set, drain — and **only if that second drain is non-empty**, re-upload the set draining between uploads, so each window holds one call and each record names one uniform. **Three preconditions of that replay are contract** (round seven): the re-upload uses the values **already computed for this sweep** and never re-evaluates the providers; a replay that reproduces nothing is **unattributable** and falls to §6's 3→4 row rather than no-op'ing — **for either of two reasons, and the second is what four other sites lean on**: `OUT_OF_MEMORY` need not recur, *and* the error may never have been ours, since the elision bit tracks *facade* calls while the GL flag is per-context; and a record can only name a uniform because the backend retains the name from `locate` (`[D-P1-34]`) | GL sets an error flag to the *first* error and records no further error in it until `glGetError` clears it, so a batched sweep in which five uniforms fail yields one record — `[D-P1-30]`'s "the cheap mode still cannot lose an error" was factually wrong about GL and is deleted. Making rung 2 debug-mode-only was the alternative and was **rejected**: `DESIGN.md` puts per-uniform GL-error isolation in Phase 6's **v0.1** scope-in, so a shipping build whose only fallback is "disable the whole set" would degrade harder than rung 3. The attributed replay does not contradict §7 — the clean sweep still costs exactly one `glGetError`, **which is true of the two-drain protocol because `[D-P1-30]`'s backend elides a drain with no mutating *facade* call behind it**, not because the protocol drains once — and the per-upload cadence is entered only on a frame that is already about to disable a uniform, once. **The exception the elision creates is stated rather than left implicit:** a *foreign* error makes the trailing drain non-empty with nothing of ours having failed, so a recurring one re-enters the replay every frame, reproduces nothing, disables nothing, and costs a re-upload of the ~90-uniform set per program set (§4.7.4, §7). That cost is created by `[D-P1-30]`'s elision and does not appear in the ledger the elision was kept against; recording it is this document's job, re-weighing the decision on it is not. The re-upload-cached-values rule rests on **idempotence alone**: `glUniform*` is idempotent on the bound program, so the replay changes only which drain window each upload lands in. Round seven's supporting claim — that the halflife-smoothed providers advance *per sample*, so a second evaluation would double-advance them — is **deleted** (round eight, V8-3): no cited source states it, App D gives the values only, App D's cadence note and RESEARCH.md §4.2 make a "refresh" an *upload*, RESEARCH.md §4.4 places the sampling at frame begin, and `DESIGN.md` assigns the **time-corrected** smoothing formula to Phase 6's own *Scope — in* — so §5 asserting a property of those providers was a call this phase does not own (§G1.1: flag, do not decide) `[fix-up: PHASE_1_REVIEW_5.md V5-1; PHASE_1_REVIEW_6.md V6-2, V6-3; PHASE_1_REVIEW_7.md V7-2, V7-3, V7-4; PHASE_1_REVIEW_8.md V8-3; PHASE_1_REVIEW_9.md V9-1, V9-2, V9-5]` |
| D-P1-33 | `DrawService.fullscreenQuadInstanced(int)` is **deleted**; `const int countInstances = N` **on a composite/deferred program** is served by a caller-side loop over `fullscreenQuad()` with `UniformService.upload(instanceIdLoc, i)` between copies. The composite scope is stated rather than assumed, and is cited to RESEARCH.md **§4.4** | `instanceId` is an `int` **uniform** (RESEARCH.md App D.4), not `gl_InstanceID` — GLSL 120 has neither (RESEARCH.md §3.5) — so one instanced draw cannot vary it per copy and the verb could not express the directive it appeared to serve. It carried no semantics, no §3 row, no §5 mention, no §9 tag and no checklist item, in a facade whose own rule is that silent additions are not cheap. The absence is now stated in §4.7.4's absent-verbs table and the loop is mapped in §3, so the deletion is not silent either. **The scope qualifier is round seven's correction:** the mapping was written as if it covered the whole directive, but §3.2, App A.3 and App D.4 — the three sources it cited — place `countInstances` in *Vertex Shader Configuration* with no program restriction, and `instanceId` in the **common** uniform block. Only §4.4 restricts the loop to composites, and it was not cited. The row is now scoped and §4.4 is cited; the non-composite half is `[D-P1-35]`'s `[fix-up: PHASE_1_REVIEW_5.md V5-2; PHASE_1_REVIEW_6.md V6-4; PHASE_1_REVIEW_7.md V7-1]` |
| D-P1-34 | **Backend obligation:** a backend's `UniformLocation` implementation **retains the name passed to `UniformService.locate(program, name)`**, so `GLError.subjectLabel` can carry it. Stated as prose, like `[D-P1-29]`, because no test can catch it; **no signature changes** — `UniformLocation` still exposes only `isAbsent()` (§4.7.3) | `GLError`'s javadoc promises `subjectLabel` is "the debug label of the handle **or the uniform name** involved", and rung 2's whole value is that a record names one uniform. Handles get their label from `create(String debugLabel)`; a location has no such parameter, so the only place the name ever exists is the argument to `locate`. A backend *may* discard it and still satisfy every signature in §4.7.4 — and rung-2 attribution would then be worthless while every test still passed. The facade cannot express the requirement in a type without making `UniformLocation` carry a string it has no other use for, so the obligation is written down instead, and §12 item 22's review hook checks it `[fix-up: PHASE_1_REVIEW_7.md V7-4]` |
| D-P1-35 | **`countInstances` outside the composite passes has named owners and no Phase 1 design element.** Phase 3 detects the directive in its `const`-scan; Phase 4 carries it as the per-slot instance count; **Phase 7** owns the gbuffers/shadow re-render itself. The facade gains nothing either way | The directive is a *vertex-stage* opt-in with no program restriction in any source this document cites (§3.2, App A.3), `instanceId` is in the **common** uniform block, and RESEARCH.md §4.2 carries "instance count" on every classic catalog slot — so restricting it to composites, as `[D-P1-33]`'s mapping silently did, is unsupported by that provenance. §4.4 restricts only the *observed loop*. For a gbuffers program "the geometry" is vanilla terrain or entity geometry drawn by Minecraft's own calls through Phase 7's hooks, which never reach the facade, so no verb here can satisfy it and inventing one would be Phase 7's design made by the wrong session. Recorded as an **open case handed onward** (§11.4) rather than designed, because RESEARCH.md documents no non-composite instancing loop and this session has no behavior to be faithful to. **Round eight (V8-2) closed the other half against the same document:** the *composite* loop had been attributed to Phase 5, but `DESIGN.md` names it in **Phase 7**'s *Scope — in* under composite/final execution — and, more explicitly still, in **Phase 4**'s: *"`countInstances` exposure to the pass executor (**execution is Phase 7, tag v0.5**)"*, the citation round nine (V9-8) found unused and this revision adopts, together with the **`[v0.5]`** milestone it carries. So both halves of the directive now land on Phase 7, and §5.2's non-verbs row no longer names two phases for **two halves of** one directive. The two cases remain distinct in kind — the composite loop is **assigned** by `DESIGN.md`, the gbuffers/shadow re-render is **open** `[fix-up: PHASE_1_REVIEW_7.md V7-1; PHASE_1_REVIEW_8.md V8-2; PHASE_1_REVIEW_9.md V9-8]` |
| D-P1-36 | **A `mod.glue` implementation of `engine.gl`'s `ForeignTextureProvider` supplies `TextureHandle`s for textures Minecraft owns** (§4.7.3); the facade gains **no** `adopt(int glName)` verb and no §4.7.4 **verb** changes. The provider's *contents* are **Phase 5's** — which texture object backs each unit per stage — with **Phase 6** pointing the sampler uniforms at the units and **Phase 13** a second consumer. **Two properties of the type are part of the decision and were left unstated until round thirteen** (V13-2, V13-3): the key space is **two disjoint vocabularies**, App B.3's sampler names (Phase 5's) and `minecraft:` resource locations (Phase 13's), because the identifier Phase 13 holds is a *source* resource location and not a *destination* sampler name; and the handle is **bind-only and outside the lifetime rule**, since the engine neither created the object nor may delete it | §4.12's PD §2 completeness check found the facade unable to express two rows of a contract it must satisfy: App B.3 puts the vanilla block atlas at **unit 0** and `lightmap` at **unit 1** on GBUFFERS/SHADOW programs, `bindToUnit` takes a `TextureHandle`, and nothing in §4.7.4 produces one for a texture `TextureService.create` did not make. The obvious verb is refused on the seam rather than on taste: a raw GL name in an `:engine` signature is the leak `[D-P1-15]`/§4.7.3 exist to prevent, and `SeamBytecodeTest` would pass it while the property it guards was gone. Implementing `TextureHandle` in `mod.glue` keeps the GL name on the `:mod` side of C-1 and hands the consumer the same opaque type it already receives, so the fix costs a slot, one `:engine` interface and the handle sealing — but no facade **verb**. §G11.4 decision: the adopted item is PD §2's *inventory as a checklist*, not a Pintonium mechanism; the contract check is App B.3, cited above. **Round twelve corrected three things inside this decision rather than around it, and the pattern is worth the sentence: every one was a consequence of the same slot, not an independent defect.** The provider *interface* was named at four sites and declared nowhere — now §4.7.3's `ForeignTextureProvider` (V12-4). The handle sub-interfaces could not be implemented from `mod.glue` while they were `sealed`, so the sealing is narrowed to `GLHandle` (`[D-P1-15]`, V12-3). And the *contents* were attributed to Phase 6, where `DESIGN.md` l. 1488 gives the texture-object half to **Phase 5** and l. 1563 gives Phase 6 the sampler half — this cell's own *"Phase 5/6 policy"* had it right while five sites around it did not (V12-1) `[REV2 migration — §0.11; DESIGN.md Phase 1 spec ll. 998–1003]` `[fix-up: PHASE_1_REVIEW_12.md V12-1, V12-3, V12-4]` |
| D-P1-37 | **PD §16's three-stage 1.12.2 bring-up is adopted**, with stage 1 deviated to the FML lifecycle, stage 2 (`OpenGlHelper.initializeTextures` @`RETURN`) adopted as the capability-probe moment, and stage 3 (`GuiMainMenu.initGui` @`RETURN`) adopted as a signal but left to Phase 7 to place | The sequence is proven on this exact platform and this document previously named no site at all for the probe — §7 said *"at display init"*, which is a description rather than a moment. Stage 1 is deviated because Cleanroom hands us `preInit` and `FMLLoadCompleteEvent` natively and §4.9.1/§4.10 already use them: adopting the `GameSettings.loadOptions` mixin would spend one of D-5's ~25–30 injections on a moment the loader gives away, and would place class-transformation-adjacent machinery ahead of the very check (§4.10 point 1) that decides whether to run. Stage 3 is deferred because Phase 1 has no consumer for it and a hook with no caller is not a design. **§G11.4 contract check, performed and negative:** a bring-up ordering is not contract-visible under §G4.2 (packs observe RESEARCH.md §3 / Apps A–D, F; this is §4.1 lifecycle), and it is *consistent* with §4.1's ordering — probe before discovery before load, with step 4's init staying lazy-at-first-frame in both designs (§4.13) `[REV2 migration — §0.11; DESIGN.md Phase 1 spec ll. 1010–1014, and the Doc gate's REV1 criteria at ll. 1058–1060 within the gate at ll. 1056–1060]` |
| D-P1-38 | **Pintonium's class-scan mixin plugin is rejected**; the three declarative configs of `[D-P1-11]` stand. A `:mod` **test** asserting config-array ↔ package agreement is adopted in its place (§12 item 30a) | A package scan answers *which classes are mixins* and not *which CleanMix phase each belongs to*. Pintonium runs one config and can therefore let a plugin enumerate an empty array; we run three, keyed on the only axis CleanMix dispatches on, so a phase-aware scan would re-encode in Java — against a sub-package or naming convention that is itself upkeep — the split three JSONs already state declaratively, and would fail at config load rather than at review. Three smaller reasons: the MOD config's `plugin` slot is already reserved for the bail-veto duty (§4.5.2) and discovery-plus-veto in one class is two responsibilities where diagnosis is hardest; D-5's injection budget makes the replaced arrays a few dozen lines across the project's life; and a declared array is auditable by reading it, which §G5.3's integration review and Phase 7's catalog both want. The drift risk the scan removes is real and is answered by the test instead `[REV2 migration — §0.11; DESIGN.md Phase 1 spec ll. 1007–1009, the REV1 clause inside the Mixin-wiring bullet at ll. 1004–1009]` |
| D-P1-39 | **`ShaderService` exposes `useFixedFunction()` as the explicit no-program selection operation, additive to `use(ProgramHandle)`.** It binds program zero inside the backend; callers may not encode the state as `use(null)`, a magic/sentinel handle, or an engine-visible integer. Recording uses zero-argument `shaders.useFixedFunction`, and the existing generic replay assertions inspect that exact operation | RESEARCH.md Appendix A.1 has fixed-pipeline terminals for `<none>` and absent shader roots plus absent-`final` passthrough, while the verified Phase 1 surface exposed only handle-bearing `use`. Phase 4 obeyed the dependency rule by requesting the missing verb in `docs/phase4/v1/PHASE_4_DOC.md:931`–`:946` instead of inventing it. A distinct zero-argument operation preserves C-1 and the handle-lifetime model: program zero is backend state, not an engine object, so it is neither created/deleted nor eligible for `noUseAfterDelete()`. Phase 4 retains every policy decision in `ProgramStateBarrier`; this verb performs selection only (§0.15, §3, §4.7.4–§4.7.5) |
| D-P1-40 | **Admit one backend-authenticated borrowed-depth subtype and separate depth attachment, combined depth/stencil attachment, first-copy initialization, and steady-copy operations.** Ordinary foreign textures remain bind-and-label-only; a public marker alone never grants permission. All provenance/precondition rejection is pre-GL, and every internal binding is restored | RESEARCH.md §4.3/App B.2 require a real sampleable `depthtex0` attachment and defined `depthtex1`/`depthtex2` contents; RC3's Phase 5 assignment requires the proven 1.12.2 borrowed-depth reattachment path, exact packed-depth/stencil handling, first-frame `glCopyTexImage2D`, and later capability-tiered copies (`docs/design/v2.0-RC3/DESIGN.md:1621`–`:1652`). A single unrestricted foreign-texture permission would permit deletion/color attachment/copy overwrite of Minecraft objects; one backend-authenticated subtype plus distinct verbs expresses exactly the required operations without a raw GL name, ownership transfer, or backend-state heuristic (§0.18, §4.7.3–§4.7.5) |
| D-P1-41 | **Reserve `engine.frame`, `mod.glue.frame`, `mod.mixin.frame`, and `mod.conformance` as Phase 7's exact package homes.** | Phase 7 needs a pure orchestration core, platform adapters, dumb injection sites, and a capture-agent entry point. Naming all four here prevents generic `mod.glue`/`mod.mixin` dumping grounds and preserves C-4: the capture agent is compiled with `:mod`; it is not a reverse dependency from `:conformance` (§0.22, §2.1, §5.1). |
| D-P1-42 | **Expose replay attribution as `ReplayAwareGLError(GLError error, boolean attributed)`, one result per triggering drained error, with `true` earned only by isolated reproduction.** | The original `GLError` accurately describes a drain window but cannot distinguish an isolated replay success from a clean, batched, ambiguous, or foreign-error replay. A boolean paired with the original immutable value is the minimum transport that lets Phase 6 report the result and Phase 2/7 capture it without guessing from labels or operation names (§0.22, §4.7.4, §5.2). |
| D-P1-43 | **Grant Phase 13 R3 exactly `engine.textures`, `mod.glue.textures`, and `mod.mixin.textures`; retain every existing grant, including Phase 7 R7-8.** | The current closed table already supplies all four frame/capture homes but lacks the texture trio. Reuse the engine-policy / platform-adapter / dumb-hook split of the frame and shadow grants; add no API permission, facade verb or module edge (§0.24, §2.1, §5.1). |
| D-P1-44 | **Accept Phase 4 §5.4 item 1's native alternative:** one engine-enum pre-link legacy geometry operation; no asserted complete core translation | Phase 3's two-span contract lacks whole-source/version/interface translation, whereas the published ARB specification supplies native semantics. Keep core GL objects and opaque handles, map only inside `mod.glue`, and require explicit Phase 3 source and Phase 4 consumer migration before legacy support is usable (§0.25, §§4.7.4/5.2/11.4). This supersedes only D-P1-25's geometry assumption |
| D-P1-45 | Grant Phase 2 R4B consumption of the existing diagnostic types only | P3/P4 source-free evidence uses stable codes, severity, channel and owner-supplied coordinates; no new diagnostic field/channel or source/log redistribution permission (§4.9.4/§5.3). |
| D-P1-46 | Keep stage-one bootstrap on FML lifecycle; retain the non-fullscreen countInstances authority gate | RC2 expressly permits justified bootstrap deviation; P1 already selected it. Only fullscreen composite/deferred execution has an accepted owner/milestone; metadata publication does not settle gbuffers/shadow traversal semantics. |
| D-P1-47 | Adopt the maintainer's 2026-09-07 prepared-submission countInstances boundary at v0.5; supersede D-P1-35/46's non-fullscreen authority-open posture only | RESEARCH §3.2 requires N renders; P4 §11.5 records author/OSS evidence limits and explicit maintainer selection of adjacent submission order. P7 policy/P10 adapters use P6's existing event; P8 traverses once; foundation grants no new GL or extension API. |
| D-P1-48 | Backend-owned effective linked primitive metadata; fullscreen TRIANGLES always selects existing triangle strip, never QUADS; preserve signature and error/restore boundary | R26-1 plus ARB_geometry_shader4 rev26 primitive and GL3.2 query/precedence rules. Core and native geometry covered; no per-draw query/source scan or vanilla topology policy invented |
| D-P1-49 | Grant P3 §5.4's `org.anarres:jcpp` production dependency conditionally on exact implementation-time pin, pure-JVM closure, license and runtime packaging verification | Existing Apache-2.0 candidate permission is not a build grant; unchanged seam checks need no platform-library exemption |
| D-P1-54 | Grant P14 R-P14→P1-1/2 exact supported-backend debug gate and pure-value/backend-helper homes | KHR labels/groups need no debug context; full optional execution and post-v0.5 worker/context changes remain their separate owner gates |
| D-P1-50 | Grant R10-1 exact vertex package homes, bounded borrowed-source input/restore service and class-only early compatibility evaluation | P10 owns layouts, native adapters and P7 lifecycle integration; P1 supplies mechanism/recording, not raw handles, renderer replacement or independent topology authority |
| D-P1-51 | Preserve executable September pins; relocate current extra.gradle Buildship callback to actual mod/Blossom owner, retain historical July evidence | File-inspected current checkout differs from old examples. Align CI setup with wrapper during implementation; no upgrade, downgrade or fabricated availability/runtime evidence |
| D-P1-52 | Complete TextureParameters for synchronous owned P5/P13 textures; exact sampler/object split and mapping, no foreign mutation or optional P14 execution grant | Missing type fields block ordinary legal texture upload too; sampler0 cannot substitute for unspecified object state |
| D-P1-53 | Real MOD-plugin early veto ships with approved geometry-only adapter subset at v0.1; CLASSIC56 and format transitions stay v0.3 | Earliest affected .gsh milestone must include compatibility plumbing; old absent/permissive plugin staging cannot protect already-applied hooks |
| D-P1-55 | 2026-09-08: isolate generic zero for live conventional positions and all non-admitted enabled capture arrays inside §4.7.6; restore complete actual predecessor/current side effects and partial failure; replay guard remains pointer-free | R27-1 and NS-1 are one owner defect. ARB_vertex_shader precedence/capture semantics require isolation before consumption, not merely restoration afterward; P10 adopts the owner operation without taking foreign-array ownership |
| D-P1-56 | Receive P3 D-P3-70/schema21 correction while making active schema references defer to P3 §5.3/CURRENT_SCHEMA_VERSION | P1 has no new configuration consumer, binary I/O or facade operation; preserve exact native-source and asset contracts without independently freezing the frontend's schema number |
| D-P1-57 | R32-1/2: grant positional FramebufferDrawSlot and real backend duration override/interception with private cache-coherent bypass, exact snapshot restore, effective blend notification and poisoned failure containment | P4 owns lifetime/policy, P5 packs only attachments without dropping output holes, P7 registers dumb hooks; §5 awaits fresh review |
| D-P1-58 | R30-1: make AlphaBlendOverride an opaque backend-implemented interface, issued only by each StateService's private implementation | Both mod.glue and engine.gl.record can issue genuine leases without split packages, reflection, exposed construction state or an engine-to-mod dependency; §5 incorporates the corrected declaration |
| D-P1-59 | P4 R34 C1: candidate-only fixed sampler initialization with private exact selection restoration and typed safe/fatal failure | P5 remains sole map, P4 initializes before state-dependent GL validation, P6 runtime participants unchanged |
| D-P1-60 | P14 R1 C2/3: explicit successful-preflight unit-mask dispatch and fixed-function sampler normalization | No implied frame callback, preflight mutation or facade-count claim about private native strategy traffic |
| D-P1-61 | Receive P3 D-P3-72 schema22/current-constant contract without local parsing | Changed BLOCK provenance belongs to P3/P9; prior numeric receipts and old PASSes cannot certify current dependencies |
| D-P1-62 | Receive full owned-parameter replay before runtime sampler demotion | Private baseline path avoids recursive optimizing decorators; unknown or failed restoration cannot authorize NONE drawing |
| D-P1-63 | Grant mandatory target-bearing synchronous allocation/upload values with P5 D-P5-42 and P13 D-P13-36 | Preserve depth/copy semantics; no direct GL/async/parser expansion |
| D-P1-64 | Receive P14 D-P14-31 complete latest owned-object baseline on every successful setParameters, not only runtime demotion | Preserve all-unit clearing and borrowed restrictions |
| D-P1-65 | Receive P3 exact-current schema23/MaterializedSource-v23 | Prior numeric receipts historical; nine metadata-only trees/projectionVersion1 unchanged |
| D-P1-66 | R33 C33-1: captured target-specific maxima, gated native probes, exact mandatory serialization and identical recorder admission | No guessed limits; P2 replay/P13 pure preflight receipts |
| D-P1-67 | P5 R43: grant exact typed owned-attachment clear with core/EXT native tiers and full-state/error/recorder transaction | P5 owns finite conversion and all-success full-clear policy; no integer downgrade |
| D-P1-68 | Receive P10 D-P10-29 complete conventionalInputs plan and authenticated source/product identity | Preserve source-absent COLOR/UV1 live inheritance and capture exclusion, physical56 and existing native boundary |
| D-P1-69 | Resolve R34 C34-1 with capability-legal private discard normalization/restoration in every typed-clear tier | Enabled discard cannot silently defeat full-clear success; no public state API |
| D-P1-70 | Resolve R34 C34-2 with exact complete capturePlan recorder list issuance before first replay; receive P10 D-P10-30 completed-producer distinction | No physical-layout inference, first-request authority or old overload |
| D-P1-71 | Receive P14 target-materialization/deletion timing without expanding create inputs | Logical label retention, exact-target first materialization, deletion cannot restore dead names |
| D-P1-72 | Resolve R35 C1 by stating C-3's LWJGL confinement as the package tree `com.schmaloogium.mod.glue` and its subpackages, restated at §4.3's softer layer, in §8.1's `SeamLwjglConfinementTest` and in §5.1's seam row | Five granted `mod.glue.*` subpackages host platform code; exact-package equality goes red on legal tenants and an unstated prefix match would widen silently |
| D-P1-73 | Resolve R35 C2 by giving D-P1-71 P14's label limb: `DebugService.label` on a live-but-unmaterialized owned texture handle is legal and recorded-only, queued and applied once by the first admitted materialization, dropped if deleted first; §4.7.4's creation-site instruction becomes label-then-materialize or `create(debugLabel)` | No native call against a not-yet-existing name and no queued `GLError` in the drain P5/P13 bracket their work with; foreign/borrowed handles are already materialized |

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
| **D-7** | GPL-3.0-or-later license (template's MIT-style LICENSE must be replaced) | **Satisfied.** The replacement D-7 asks for has already happened in the repository: `LICENSE` at the root is tracked and carries the verbatim GPL-3.0 text (674 lines; commit `aa917a6`, *"Update LICENSE from MIT to GPL-V3"*) `[V:repo]`. What §4.8 adds on top is the rest of the obligation — two-line SPDX headers on every source file, the "or-later" grant stated in `README.md` and `mod_credits` (§4.8.1, `[D-P1-17]`), and `THIRD-PARTY.md` as the D-8 compliance mechanism. §12 item 1 is therefore a verification step, not a file-restoration step. **REV2 records the same state upstream and narrows the assignment to match** (§G7 item 1, ll. 688–691): the swap is *"already executed in the repo (full GPLv3 text; commit `aa917a6`)"* and Phase 1's remaining license work is *"the or-later statement, the source-header convention, and `mcmod.info` metadata"*. That is an independent confirmation of a fact this document derived from the working tree rather than from the design, and the hash is adopted here for it. **One apparent tension, reconciled rather than left to the reader:** REV2's *"`mcmod.info` metadata"* and `[D-P1-8]`'s *"no `mcmod.info` `license` key"* are compatible — the statement goes in the `mod_credits` field, which **is** `mcmod.info` metadata; the 1.12.2 schema simply has no `license` key to put it in `[V:mcp]`. |
| **D-8** | Published docs + OSS source OK; LGPL-3.0 reuse with compliance; two prohibitions | **Satisfied as convention.** §4.8.3 creates `THIRD-PARTY.md` with the per-incorporation entry format (files / upstream / notice / modifications) and carries both standing prohibitions at its head: never copy from glsl-transformer (AGPL-3.0), and the OptiFine decompile is behavioral-observation-only. The mechanism exists before the first incorporation, which is the only time it can be built cheaply. |
| **D-9** | Compatibility-profile GL baseline; no core-profile rewrite | **Deferred → Phases 5+/7 for policy; enabled here.** Phase 1 owns no GL policy (explicitly Scope-out). What it does is make the constraint expressible and testable: the facade contains no core-profile-only entry point, `UniformService` has no UBO method at all (so the pack contract's prohibition cannot be violated), `DrawService.fullscreenQuad()` leaves the `GL_QUADS`-vs-triangle-strip choice to the backend, and `GLCapabilityProfile` makes every capability gate assertable headlessly. |
| **D-10** | Conformance harness from week one | **Deferred → Phase 2; unblocked here.** §G6 defines D-10's "week one" as the runnable-before-renderer subset: fixture downloader, preprocessor golden runs, capability-profile replay. Phase 1 supplies two of the three prerequisites — the `:conformance` module with JUnit wiring, and the capability-profile record/replay machinery (§4.7.5) plus its serialization format (§4.7.2). The third (the downloader) and all harness content are Phase 2's. |

### 11.3 Input contradictions, defects, and inherited values found

Reported, not smoothed over (§G1.1).

**Contradictions between inputs.**

1. **Extension set attributed to RESEARCH.md §4.1 by DESIGN.md, but not present there. — RESOLVED
   UPSTREAM (REV2).** Detailed with its ruling in §3.1. Included, tagged `[A]` rather than
   `[V:observed]`, because RESEARCH.md §3.5's `MC_<GL_extension>` macros independently require it.
   **`DESIGN.md` ll. 992–997 now split the citation** exactly as §11.5 item 3 requested, so this is no
   longer a live contradiction between inputs; it is retained as the record of one that was found,
   carried upstream, and granted.

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

**Pintonium do-not-inherit rows, shown handled (REV1/REV2).** §G11.4 makes PD §17's bug catalogue and
PD §18's divergence table **standing** lists, and requires a phase doc consuming Pintonium material to
show the relevant rows handled. Four of PD §17's thirteen rows reach this phase's surface; **no PD §18
row does**, and that is stated rather than left as an empty section, because §18 is entirely about
pack-facing semantics (attribute locations, the unit map's *allocation strategy*, dimension folders,
noise RNG, identity macros) — every one of which §1.2 assigns to Phases 3–10 as policy this phase does
not own.

11. **The four §17 rows this phase answers, and where.**
    - **B11 — `GLStateManagerImpl.getColorMask()` hardcodes all-true.** This is the sharpest one for
      Phase 1, because it is a defect in exactly the mechanism §G4.6 makes ours: state save/restore.
      `StateService.snapshot(EnumSet<StateAspect>)` / `restore(StateSnapshot)` (§4.7.4) is the
      perturb-and-restore instrument every later phase relies on, and a snapshot that returns a
      constant instead of reading real state would satisfy every signature, pass every test that does
      not have a live driver, and silently corrupt vanilla rendering. Handled two ways and neither is
      a comment: `[D-P1-29]` already obliges the backend to issue `GlStateManager`-cached state
      *through* `GlStateManager`, which is where the real values live, and §12 item 22's review hook —
      the one that exists precisely for obligations no test can catch — is extended to cover it
      (item 22a). `[V:observed — PD §17 B11]`
    - **B7 — GLDebug group push/pop asymmetry** (`setPhase` pops unconditionally, pushes selectively).
      `DebugService` is an interface at v0.1 with its implementation at v0.5/Phase 14 (§9), so the
      defect is inheritable only by that implementation. It is handed to Phase 14 in §11.4 with the
      shape of the failure named, which is more use than a warning to be careful.
      `[V:observed — PD §17 B7]`
    - **B10 — a landmine overload** (`addDynamicSampler(...)` returning `false` unconditionally,
      masked today and breaking at the next refactor). Not a defect we can inherit, because §4.7.4
      declares no such overload; recorded because it is a *facade-design* lesson rather than a bug —
      an overload whose contract differs from its sibling's is a trap the seven services must keep
      out, and §4.7.4's rule that additions are cheap but **silent** additions are not is the
      standing defence. `[V:observed — PD §17 B10]`
    - **B6 — the unwired `blendFunc` notifier.** Handled at §3's `blendFunc` row and routed to
      Phases 6 and 7, which is where REV2's §G4.6 puts it. Phase 1 supplies the `ivec4` verb and
      claims none of the observation. `[V:observed — PD §17 B6]`

    *Provenance limit, stated once for all four:* these are cited **from PD**, whose Location column
    names the sites, and were not re-opened in `reference-src/pintonium-9c2fcc1/`. §G1.1 asks a
    session to verify PD's load-bearing claims at source; none of these four is load-bearing for a
    Phase 1 design element — B11 and B7 sharpen obligations this document already carries, B10 changes
    nothing, and B6 is a hand-off. The three PD claims that *are* load-bearing here — the §2 service
    inventory, the §16 bootstrap sequence and the §16 class-scan plugin — were each re-opened at
    source (§4.12, §4.13, §4.5.2a).

12. **R26 current-file pin mismatch** `[V:repo 2026-09-07]`. Executable wrapper/plugin/
    loader are 9.7.0/1.4.36-kappa/0.6.10-alpha; the three CI setup inputs still say
    9.6.1 but invoke the wrapper. D-P1-51 preserves exact executable pins and requires
    setup-input alignment, not an upgrade or a rewrite of July evidence (§4.2.6a).
13. **R26 extra.gradle Buildship ownership** `[V:repo 2026-09-07]`. The current script
    consumes main source-set/template-task model objects. D-P1-51 places it in `:mod`
    after Unimined/Blossom, not the aggregator; failure on a source-set-free Eclipse root
    is `[INFERENCE]`, not reproduced execution. Preserve current functionality at migration.

### 11.4 Items handed onward

**To Phase 2** — the `:conformance` module, its JUnit wiring, and the CI `conformance` job stub are
empty slots by intent, not omissions. The `GLCapabilityProfile` text format (§4.7.2) and the
`GLCallLog.render()` stability guarantee (§4.7.5) are the two contracts your golden-file workflow
should build on. Phase 1 supplies no fixture set and no answer to OQ-10.
R4B is now owner-designed and adopted by P2's integration amendment: consume the existing
diagnostic types under §5.3/§4.9.4's source-free restriction. P3's `inspect(PackLoadRequest)`
projection and P4's separate same-build resolution enrichment supply artifact data; Phase 1
does not supply a front-end inspector, source coordinates inferred from text, or an archive hash.
All amended §5 surfaces still require fresh review.

**One REV2 clause lands on you rather than here** (§8.3). §G6 now governs *derived* artifacts as well
as pack files: goldens carry **no pack source text** (`[D-P2-5]`), **no rendered images enter the
repository** (`[D-P2-6]`) — committed oracles are manifests, hashes plus provenance — and goldens are
never auto-updated, regeneration being an explicit `-PupdateGoldens` that **still fails the run it
regenerates in**. Phase 1's two artifact kinds are compliant by construction (§8.3 says why) and Phase
1 owns none of the workflow. What it owes your flag is the property its failure mode presupposes: a
`GLCallLog.render()` of unchanged behaviour reproduces byte-identical output, which §4.7.5 states as a
constraint and `RecordingGLDeviceTest` checks. Without that, "still fails the run" would be noise
rather than a signal.

**To Phase 3** — `schmaloogium.debug.saveSources` is reserved for you: Phase 1 fixes the flag's name
at v0.1 and implements no behavior behind it, so the dump arrives with your preprocessor.
`GLCapabilityProfile` is the whole GL-side input to RESEARCH.md §3.5's standard macro header, not
only `extensions()` for `MC_<GL_extension>`: `MC_GL_VERSION`, `MC_GLSL_VERSION`, `MC_GL_VENDOR_*` and
`MC_GL_RENDERER_*` all read off the profile (§3's macro-header row). The former required-core-rewrite
assumption is withdrawn by D-P1-44. **P3 owner-granted/unverified; current schema is its §5.3/CURRENT_SCHEMA_VERSION (native grant D-P3-68):**
`GeometrySourceRequest.PreserveNative(expected)` supplies the recognized-pair route;
`None` remains the ordinary/no-legacy request, not a raw-source bypass.
It must run the existing same-build option/include/jcpp/contribution pipeline, retain the effective
GLSL version, active ARB extension, `maxVerticesOut` constant and all legacy varying/built-in
semantics, and publish the final declaration catalog with its identical materialization fingerprint.
The preserved constant may still be read by pack code; do not delete it just because the engine
also consumes its value. Preserve source attribution through includes and macros, diagnose an
invalid or mismatched pair source-locally, and include the native route discriminator, exact
text/map, pair/site identity, count and all existing materialization inputs in Phase-3-owned
fingerprinting. Native and transformed results must never alias in caches.
The source result must distinguish native legacy from ordinary/core materialization without
downstream reopening or rescanning, and retain any source layout precedence rather than silently
overriding it with API defaults. P3 §§2.2/4.5/4.10/5.1 now specify that closed request/result,
mixed-form handling and exact-current schema migration; the incomplete two-span translator is removed.
`MaterializedSource.geometry()` publishes `None` only for non-geometry stages and otherwise
`CoreLayout` or `NativeLegacy`, each with `effective().input()` and attributed declarations.
P4 must adopt that exact owner result and compare it to P1's cached actual linked input.
Fresh owner/receiver reviews and real compile/link/draw evidence remain required.
P3 D-P3-69's `PackConfiguration.assets()` is its immutable same-load `PackAssetSnapshot`,
usable after archive closure. This is P3-owned binary acquisition, not a facade I/O operation,
new P1 dependency, permission to relabel resource epochs, or permission to serialize pack
bytes in inspection/artifacts. The original schema19 grant in §0.29 remains dated history.
Separately, `DESIGN.md` makes the **engine-flag ownership map** yours — §4.7.4's
face-culling row defers to it rather than pre-empting it. One small thing your `const`-directive scan
is named for by this document: `countInstances` is a *vertex-stage* directive that a **gbuffers**
program may carry. Detection feeds the approved prepared-submission contract at v0.5
(D-P1-47, §11.4's Phase 7 entry), not an optional pack-discovery experiment.

**P3 jcpp request: owner-granted/unverified** by D-P1-49, §§4.2.4b/5.1.
Adopt the exact implementation-time pin, complete seam/closure/license and runtime packaging
rules in your §5.4; attribution alone never fulfilled this request. D-P3-68 now fulfills
the separate native-source owner request above; both grants remain unverified.

**To Phase 4** — your §5.4 fixed-function request is accepted as `[D-P1-39]`.
`ShaderService.useFixedFunction()` selects program zero without a raw integer, null, or sentinel
handle; `RecordingGLDevice` records `shaders.useFixedFunction` with no arguments, and the existing
`calledInOrder`/`neverCalled` assertions distinguish it from `shaders.use`. Invoke it only from the
fixed terminal of your `ProgramStateBarrier`, after restoring the previous alpha/blend lock and
without dispatching program-binding participants — the ordering and participant rule remain yours,
exactly as `docs/phase4/v1/PHASE_4_DOC.md:758`–`:821` specifies. This addition does **not** resolve
your separate legacy-geometry request. Most importantly, it is unverified until the fresh Phase 1
round required by §0.15 returns; do not consume it merely because this fix-up now prints the
signature.

**To Phase 4 — item 1 consumer migration (D-P1-44).** The fixed-function grant above remains
independent. This amendment supplies the missing native parameter verb, **not** a completed
dual-form path. In your own authorized owner session, migrate active §§3.1/4.6/4.8/5.2–5.4,
§8.2, fingerprint inputs and failure/implementation ledgers to:

1. Keep `GeometrySourceForm.CoreLayout` on the GL 3.2 route. Recognized legacy pairs request
   `GeometrySourceRequest.PreserveNative(config)` and consume `NativeLegacy`; ordinary stages
   use `None`. Never reconstruct removed `Translate` output or patch source locally.
2. Adapt the validated native legacy configuration into Phase 1's closed
   `LegacyGeometryInputPrimitive.TRIANGLES` / `LegacyGeometryOutputPrimitive.TRIANGLE_STRIP`
   and exact positive count. These are engine enums, not aliases of Phase 3 enums or GL integers.
   Keep source-layout precedence/compatibility decisions in the Phase 3/4 contract, not this verb.
3. Gate on the actual ARB extension, compile the preserved sources, create/attach and bind
   attributes through the existing core-object facade, then perform §4.7.4's drain/configure/drain
   transaction before link/validate. Precondition rejection, unavailable dispatch, parameter error
   or link failure takes rung 3 with complete cleanup, source-attributed diagnostics and the
   existing whole-binding fallback. Do not reuse an old linked executable or omit just `.gsh`.
4. Include the native strategy discriminator, enum names, exact count, extension capability and
   Phase 3 materialization fingerprint in registry identity; a strategy/count/capability/source
   change invalidates reuse. Phase 1 never hashes caller source or generates registry fingerprints.
5. Update the consumed §5 interface row and item-1 disposition to recognize P3's exact-current
   owner grant separately from fresh verification. Replace future-only geometry checks with
   source-preservation/built-in/varying fixtures and configure-before-link, absent-extension,
   parameter/total-output-limit, diagnostics, cleanup/fallback and cache-invalidation checks.
   Real driver compilation/linkage of original minimal legacy and core fixtures is required later;
   recorder success is not a GLSL compatibility proof.

Phase 4's integration amendment migrates to Phase 3's no-option-argument materializer and
direct per-program projections, and conditionally adopts the native operation. These are
receiver-adopted/unverified contracts, not implementation proof. P3 D-P3-68 supplies the
native-preserving source grant retained by current P3 §5; P4's exact consumer migration must be reviewed.
P1's jcpp build/pin/seam permission is granted/unverified by D-P1-49.
All changed §5 surfaces need fresh whole-document verification. No new Phase 1 dependency
or broader transformer/library permission is implied.


**To Phase 7** — the three mixin configs exist and are empty. Verify refmap generation before
building the hook catalog on top (§11.3 item 9). `compatibilityLevel` is worth your spot check
(item 8). The `BailRegistry` bail hook is wired at bootstrap and awaits your frame-driver
integration.

**R26-1 receiving amendment required (D-P1-48).** Replace P7 §4.6 step 8's caller-side
"choose QUADS when supported" with `draw().fullscreenQuad()`; the backend alone selects
the linked-compatible route under §4.7.4a. Adopt that full selection/restoration/error
contract in P7 §5/§8: no raw per-draw query, private program handle or source rescan.
Keep existing countInstances/instanceId cadence and caller state restoration. Drain and
contain draw failure before pass completion/flips; an incompatible or unknown linked input
cannot be reported as a drawn pass. P4 must also adopt link-metadata acquisition failures
and activation/restoration tracking without exposing private program handles. Fresh P1/P4/P7
review remains due; this owner amendment is not receiver adoption or runtime proof.

**To Phase 8** — R8-3 is granted exactly. Put pure policy, camera math, traversal, and the shadow
transaction in `com.schmaloogium.engine.shadow`; put Minecraft/Forge/LWJGL adapters in
`com.schmaloogium.mod.glue.shadow`; and put the SRG-targeted redirects/accessors in
`com.schmaloogium.mod.mixin.shadow`. The subpackages narrow ownership; they do not weaken C-1,
C-2, C-3, the `.internal` rule, or the mixin-config agreement test. Do not consume this grant until
the fresh Phase 1 review required by §0.20 returns literal PASS.

**One of the three bring-up stages is a requirement on your hook catalog, one is a recommendation, and
one was deviated from** (§4.13, `[D-P1-37]`; through the §0.11 revision this sentence said *two*
requirements and then enumerated one — V12-2). Phase 1 authors no mixin, so what follows is an assignment
against App E rather than code you inherit. **Stage 2 is a requirement:**
`OpenGlHelper.initializeTextures` at `@At("RETURN")` is where `CapabilityProbe` runs, and the reason
is not convenience — it is the earliest 1.12.2 moment at which a GL context exists *and* vanilla's own
texture setup has completed, so a probe placed earlier reads a half-built context and one placed later
delays every capability gate behind it. §7's row previously said only *"at display init"*, which is a
description; this is the site. **Stage 3 is a recommendation:** `GuiMainMenu.initGui` at
`@At("RETURN")` is the proven "loading complete" marker, and Phase 1 deliberately does **not** wire it,
because Phase 1 has no consumer and a hook with no caller is not a design. If your frame driver wants
a loading-complete signal, that is the site, and it is proven rather than proposed. **Stage 1 was
deviated from and you should know why**, so that a reference reading does not make it look like an
omission: the reference hooks `GameSettings.loadOptions` at `HEAD`; we use `preInit` and
`FMLLoadCompleteEvent` instead, because Cleanroom gives us both natively and spending one of D-5's
~25–30 injections on a moment the loader hands over free is the wrong trade. All three sites were read
at the reference's own mixin classes rather than taken from the digest
(`[V:observed — Pintonium forge122/src/shaders/java/org/taumc/celeritas/mixin/shaders/startup/]`).
The IR-14 receiving amendment removes `GameSettings.loadOptions` from the required CORE
hook catalog: stage one is loader-event work, so absence of that redundant injection cannot
disable shaders or manufacture a failed hook-health row. Stage two remains the required
GL-ready RETURN injection; stage three remains recommended/deferred.

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
document attributed the loop to Phase 5; that was wrong and is corrected here. The non-fullscreen
case is now also explicitly approved by the maintainer, not inferred from this fullscreen loop.
**IR-18 disposition (2026-09-07):** N adjacent native submissions of already-prepared geometry,
IDs `0..N-1`, at v0.5; P7 owns policy through existing P10 draw adapters. P8 retains one shadow
traversal. The complete contract and evidence in `docs/phase4/v1/PHASE_4_DOC.md` §11.5 bind
this handoff: effective-provider count, authenticated main/shadow admission, no main scope inside
shadow, nested instance restoration, no capture-time expansion, and stop/restore/abort on failure.
No new facade instancing verb or renderer-extension API is granted.

**Your Phase 7 package and replay-result requests are accepted as `[D-P1-41]` and
`[D-P1-42]`.** Put pure frame policy and closed results in `engine.frame`, platform adaptation in
`mod.glue.frame`, dumb injections/accessors in `mod.mixin.frame`, and the capture-agent entry point
in `mod.conformance`; none of those homes weakens C-1 through C-4. Consume Phase 6's
`ReplayAwareGLError` values verbatim: `attributed=true` means an isolated one-call replay reproduced
the particular triggering `GLError`; a label, batched window, clean replay, ambiguous replay, or
foreign-context error is never positive evidence.

**R7-8 reconciliation:** this is the existing §0.22 grant, not an outstanding package addition.
Phase 7's integration amendment adopts the exact homes and reconciles its ledger; architecture
is owner-designed/receiver-adopted, still unverified. The latest Phase 1/7 §5 surfaces require
fresh whole-document reviews; placement grants neither implementation evidence nor unrelated
dependency permissions.

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

**The non-fullscreen case is now decided, not optional** (D-P1-47).
`countInstances` on gbuffers/shadow is served below existing render scopes by a private,
synchronous mod-side adapter, not by a loop around world/entity/layer traversal. Prepared draws
A and B with N=2 execute `A0,A1,B0,B1`. Build/upload/reset and Forge callbacks run once.
VBO/client-array native submission repeats after setup and before teardown; geometry display
lists compile once with no instance uploads/count expansion and repeat only during playback,
with nested lower wrappers suppressed so the count is applied once. P10's hook coverage must
establish replay-stable program/geometry/state, not import a replacement renderer.
P7's accepted main scope or Valid shadow execution plus exact root-shadow selection/context is
required; stale credentials cannot authorize any copy. P6's existing instance event changes only
the copy ID; finally restores the enclosing value (outermost zero) before program release.
Failed copies stop immediately, restore and follow the existing frame/shadow failure path;
no replay of clears, depth splits, flips or mipmaps and no partial-pass success. P4 §11.5 records
the independently consulted published/OSS sources and their limits; the ordering is an explicit
maintainer decision, not a claim that those sources proved target-engine behavior. Fresh reviews
remain due; the original audit/history and prior decisions remain historical, not current gates.

**To Phase 5** — **the vanilla-owned texture set is yours to define, and this hand-off is new at the
twelfth round because the eleventh gave it to Phase 6** (`[D-P1-36]`, §4.12, §5.1; V12-1). Your
*Scope — in* reproduces App B.3 as your binding table and says in the same breath *"you own which
texture object backs each unit per stage"* (`DESIGN.md` l. 1488) — so when a unit's texture object is
one **Minecraft** created, you are the phase that says which one it is. Two of the sixteen rows are
that case at v0.1: unit 0 `texture` (the vanilla block atlas) and unit 1 `lightmap` on every GBUFFERS
and SHADOW program. What Phase 1 supplies is the **slot and its type** — `ForeignTextureProvider` in
`engine.gl` (§4.7.3), implemented in `mod.glue` so the raw GL name never crosses C-1, keyed on App B.3's
sampler names used verbatim (§G4.1) — that is **vocabulary (a)** of the provider's key space, and it is
the half that is yours; **vocabulary (b)**, `minecraft:` resource locations for App F.5's live-asset
custom-texture forms, is Phase 13's, and the two cannot collide because no App B.3 name contains a
colon (V13-2). What Phase 1 deliberately does *not* supply is the key set: naming
it here would be this document deciding your binding table.

**Your three framebuffer/depth requests are accepted as `[D-P1-40]`.**
`BorrowedDepthAttachmentHandle` is now the Phase 1 facade marker your `MainDepthSnapshot` carries.
The backend-authenticated handle may be sampled, labeled, and passed to `attachDepth`; pass it to
`attachDepthStencil` only when its snapshot format is `DEPTH24_STENCIL8`. Reattach on every
identity/version change as your design requires: Phase 1 authenticates origin and operation, but
does not infer freshness. Use `initializeDepthTextureFromFramebuffer` for the first
`depthtex1`/`depthtex2` copy after creation/reallocation and `copyDepthToTexture` only after matching
level-zero storage exists. Both are owned-destination-only, exact-format operations with binding
restoration; do not use backend state heuristics to collapse them into one call.

**Two rules about ordinary `ForeignTextureProvider` handles remain, and one is a client-crashing
trap** (V13-3). They are bind-and-label-only and illegal at every other texture-accepting facade
operation, including both new §0.18 verbs. They are also outside the drop-and-re-create duty
§4.7.3 otherwise puts on you. You own the uninit loop RESEARCH.md §4.1 step 5 describes as
*"delete all GL objects"*, and you also own the unit table these handles sit in; a delete walked
over that table destroys **Minecraft's block atlas** and breaks §6's rung-5 invariant outright.
Treat an ordinary foreign handle as something you were lent: bind it, never free or attach it, and
do not re-create it across an uninit — `mod.glue` resolves the underlying object at each use, so it
survives Minecraft's own resource reloads without your help. Borrowed depth is equally non-owned,
but has only the additional authenticated depth-attachment permissions stated above.

Phase 6 is the counterpart, not the owner —
it re-points sampler uniforms at units (l. 1563) and its spec carries no texture-binding bullet. This
is one of the shared-ownership seams §G5.3's integration review cross-checks by name (`DESIGN.md`
l. 631, *"the P5/P6 texture-unit-map split"*), so a disagreement between your table and Phase 6's
pointing is a thing that review is designed to catch — do not resolve it by silently widening either.

**To Phase 6** — this document carried no §11.4 entry for you until now, because everything it exposed
to you sat in §5.2's rows and §5's explicit note. Two items are new, and both came out of the RC2
migration rather than out of a review.

**The `mod.glue` vanilla-texture provider is the counterpart of your sampler re-pointing**
(`[D-P1-36]`, §4.12, §5.1). Phase 1 names the slot, declares its type (`ForeignTextureProvider`,
§4.7.3) and fixes its shape — `mod.glue` implements `TextureHandle` for textures Minecraft owns, so the
raw GL name never crosses C-1 — and then stops. **Which** textures the set contains is **Phase 5's**,
not yours: `DESIGN.md` l. 1488 gives it the texture object behind each unit per stage, and l. 1563
gives you the pointing. (The §0.11 revision said this slot's contents were yours; that was wrong
against both lines and is corrected here — V12-1.) The gap is not hypothetical: App B.3 puts the
vanilla block atlas at **unit 0** and `lightmap` at **unit 1** on every GBUFFERS and SHADOW program,
and until this revision the facade could bind a handle to a unit while offering no way to obtain one
for either. It was found by the PD §2 completeness check REV2 added to this phase's doc gate — **not**
by eleven rounds of adversarial review, all of which audited what this document *says* against its
sources. That is worth knowing when you judge how much else of the same kind may be left.

**The `blendFunc` notifier must be *wired*, not assumed** (§3's `blendFunc` row). Phase 1 supplies the
`ivec4` upload verb and claims none of the observation: §G4.6 makes the notifier yours to design,
Phase 7 owns the hook that feeds it, and REV2 requires your doc to carry a **notifier→producer audit
table** cross-checked against Phase 7's catalog. The failure it guards against is real in the
reference — Pintonium never assigns its `blendFunc` notifier on 1.12.2, so a pack that merely
*declares* the uniform NPEs at program build (PD §17 B6). A verb existing is not the same as a value
arriving.

**To Phase 10** — the bail registry mechanism is complete and has no registered checks. The
`Degrade` verdict exists specifically so that if OQ-5 resolves toward "integrate" rather than "bail",
you are not forced to widen a mechanism you were told to reuse. The `SchmaloogiumMixinPlugin` slot on
the MOD-phase config is the strongest veto point available (§4.10) — a vetoed mixin never applies, so
there is no partial instrumentation to unwind.

**R32 owner receipt — 2026-09-08, unverified.** P4 receives D-P1-57 and closes its previous
AlphaBlendOverride before acquiring the next; P5 receives positional framebuffer slots.
P7 must integrate all named alpha/blend HEAD interceptions and effective-state observations;
existing RETURN-only notifications are insufficient. P2 receives tagged recorder routes.
These grants change §5 and require fresh whole-document review; IR-01/final G5.3 remain open.

**R10-1 owner grant and native-primitive boundary (D-P1-50).** Adopt §2.1/§5.1 exact
packages, §4.7.6's complete bounded input contract and §4.10 early-check mechanism in your
own §§2–5/8/9/11. Do not duplicate the facade in P10 or claim live driver/list restoration
from recorder success. P7 owns lifecycle/generation/active-selection delivery, P10 owns
layouts/derivative geometry/upload/capture adapters; no raw GL outside the mod backend.

**R-P1→P10-55 — owner-granted / P10 D-P10-20 receiver-adopted, 2026-09-08; unverified.** Adopt D-P1-55's
complete §4.7.6 operation for client/VBO live draw and list capture, including pre-list setup/
post-list restoration, conventional-position precedence, all legal non-admitted array
isolation and current-value effects. P10 references the foundation mechanism; it does not
implement a second foreign-array owner or limit isolation to locations 10–12. Keep replay
guard pointer-free and retain P7's existing setup/drain/rollback containment. The P10 owner
is commissioned concurrently; this request is not evidence of a receiving edit or PASS.
P7 must receive the clarified failure/ordering obligation without new lifecycle policy.
No owning API or topology authority remains missing for R27-1; receiver review and actual
driver value/restoration/rollback proof remain outstanding.

During this fix-up the maintainer explicitly approved **conditional native quad-to-triangle
submission** where the active geometry input requires TRIANGLES, at the earliest milestone
claiming the affected `.gsh` support. This supersedes P10's old conversion Scope-out only
within the separately recorded `docs/decisions/GEOMETRY_PRIMITIVE_COMPATIBILITY.md`
authorization; it is **not awaiting policy authority**. P7/P10 must adopt exact topology,
winding, provoking-vertex, primitive-ID, canonical-source retention, draw-order/restoration
and adjacent-instance semantics, with corresponding conformance proof. No renderer rewrite.
P1's fullscreen route does not solve native vanilla QUADS, and its package/input grant does
not certify those adapters. Until exact owner/receiver adoption, fresh review and implementation
evidence land, do not claim affected native geometry support complete.

**To Phase 13 — R3 package allocation is granted** as `[D-P1-43]` in §2.1 and binding §5.1.
Use `com.schmaloogium.engine.textures` for pure policy/model/results,
`com.schmaloogium.mod.glue.textures` for Minecraft/Forge/resource adapters and the facade-backed
uploader, and `com.schmaloogium.mod.mixin.textures` for dumb accessors/tick hooks. The existing
parent `mod.mixin` allocation is not revoked; no fallback name needs inventing. C-1 through C-4,
`.internal` privacy, facade-only GL and the existing config/package agreement remain mandatory.
Phase 13's integration amendment now adopts the exact package homes and reconciles R3.
This is receiver-adopted architecture, not implementation or verification. The latest
Phase 1/13 §5 contracts require fresh whole-document reviews.

**Placement-only boundary for Phases 7/13.** Placement itself grants no runtime protocol.
The owners now separately publish P3's preliminary macro pair and direct mipmap/vertex
projections, P6's fixed-resolver/retirement (R7-10/11), and P8's shared-shadow/planning split
(R7-12/13); coordinated receiving amendments adopt those grants, still unverified. Do not
call them missing APIs or infer their implementation from package placement.
Still separately gated: P3's commissioned native-preserving source grant/migration and
optional P13 post-analysis R4. D-P1-49 grants jcpp build/pin/seam permission, unverified.
U1's approved documented-mechanism correction removes the unspecified suffix-grammar/typed-
suffix demand; retain numeric discriminators and P13 `.mcmeta`, with no changed defaults.
P1 native configuration and fullscreen routing exist as owner designs, not complete source/render support.
Already-granted R7-9 and all changed owner/receiver §5 surfaces still require fresh reviews.
No package row certifies PBR/shadow/conformance execution.

**To Phase 13** — the transfer verbs you need exist and carry no policy:
`TextureService.create`/`allocate`/`setParameters`/`upload`/`bindToUnit`/`generateMipmap`, with
`TextureData` carrying a JDK `ByteBuffer` and a `PixelLayout` from the same vocabulary `TextureSpec`
uses. `schmaloogium.textures` is your log channel. Two things are **yours to contest, not
inherited**: §4.7.4 declines a `glGetTexImage`-shaped texture readback on the expectation that your
companion-atlas construction builds data rather than reading it back — that is a guess about your
design, and if it is wrong the verb is an additive request in your §5; the fixed unit map and
physical binding are exclusively Phase 5's. Phase 6 consumes the pure resolver for uploads.

**The transfer verbs do not cover all of App F.5, and the route for the rest is
`ForeignTextureProvider`** (§4.7.3, §4.12, `[D-P1-36]`; new this revision, V13-2 — §5.1 named you a
consumer of the provider a revision before anything told you what to ask it for). Your `minecraft:`
source form splits in two. A **static** asset is not our problem or yours-with-help: read it through the
resource accessors and `TextureService.create`/`allocate`/`upload` it like a pack PNG. A **live**
vanilla-owned texture — `dynamic/lightmap_1`, the atlases — cannot be produced by any facade verb, and
`handleFor` is where you get a handle for it. **Vocabulary (b) of the provider's key space is yours to
define**: `minecraft:`-namespaced resource locations, used verbatim (§G4.1), disjoint from Phase 5's App
B.3 sampler names because no App B.3 name contains a colon. Phase 1 declares the vocabulary and
enumerates nothing in it — which of App F.5's forms actually need a foreign handle is a call inside your
own *Scope — in* (`DESIGN.md` ll. 2269–2271), and it stays clear of the unit-map ownership your
*Scope — out* excludes (l. 2287). The handles are **bind-only** and outside §4.7.3's lifetime rule: bind
them, never `delete` or `upload` into them. Your `_n`/`_s` companion atlases are unaffected — you build
those, so they are ordinary engine textures.

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
way, and the mandatory synchronous fallback is part of what makes that safe. **One inheritable defect
is named for you rather than left to be met** (§11.3 item 11): the reference's GLDebug group handling
is asymmetric — `setPhase` pops unconditionally and pushes selectively (PD §17 B7). It is dev-only,
and its cost is the kind that erodes trust in debug tooling rather than breaking a frame, which is
exactly why it survives in a working engine. `DebugService` is an interface here and an implementation
there, so this is inheritable only by you; a balanced-groups assertion in your own design is cheaper
than the warning.

**P14 receiver action (2026-09-08):** §§4.7.7–4.7.8/5 now grant R-P14→P1-1/2/3.
Adopt the complete TextureParameters split/conversion, exact debug activity gate and package
homes in your active §5. No remaining mandatory v0.5 request against P1 is hidden as optional;
R-P14→P1-4's worker recording/context flag/scoped convenience remain separate requests.

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

**To DESIGN.md** — two, **both granted in REV2**. They are recorded as closed rather than deleted,
because a request that leaves no trace is one a later reader cannot tell from a request never made:

3. **GRANTED — REV2, `DESIGN.md` ll. 992–997.** Phase 1's scope line attributed the "extension set" to
   RESEARCH.md §4.1's probe set; §4.1 lists four probes and does not include it (§3.1). The
   requirement is real but sourced from RESEARCH.md §3.5, and the ask was for a half-sentence
   correction so a future reader would not look for it in the wrong place. REV2 makes exactly that
   correction and names the request in its own text: *"**REV2** citation split resolving PHASE_1_DOC
   §11.5 item 3"*. §3.1 and §11.3 item 1 carry the disposition.
4. **GRANTED — REV2, `DESIGN.md` §G2.4 ll. 381–388.** §G2.4's degradation ladder had no rung for
   *"a single **feature's** GL call fails"*: rungs 1 and 2 are both about uniforms, rung 3 is a
   program, rung 4 is a capability gate at init, and §6 therefore recorded the case as an **explicitly
   unnumbered row** rather than mislabel it as rung 1. The ask was *"a rung between 2 and 3, or an
   explicit note in §G2.4"*, with the warning that without it Phases 5, 6 and 13 would each re-derive
   the category privately — sibling drift of exactly the kind §G5.3 describes the integration review
   as structurally unable to catch. REV2 adds **rung 2a** in that position, *lettered* rather than
   numbered so that existing §6 maps stay valid, and directs the five-rung phase docs to relabel at
   their next §G1.3 fix-up. §6's row is relabelled `2a` in this revision
   `[fix-up: PHASE_1_REVIEW_7.md V7-7]`.

**This migration raises no new request against `DESIGN.md`,** which is worth stating because a
migration that raised none by *omission* would look the same. Every REV2 delta this document consumed
either matched a fact it had already recorded independently — §4.1's template ground truth (the
Blossom source sets, the inline loader literal, the absent `src/test/`) and §11.2's D-7 disposition —
or was a correction it had itself asked for, above. The two REV1 obligations it newly absorbs, the
PD §2 glue-seam completeness check and PD §16's bootstrap sequence, are **assignments rather than
conflicts** and are discharged in §4.12 and §4.13. Where REV2 states a reference fact this session
could not verify to the standard §G11.4 sets, the limit is recorded at the site (§4.12's `:babric`
note) rather than escalated as a request.

**§0.25 geometry disposition — not a new global rewrite.** The supersession in D-P1-44 is local
to the former Phase 1 assumption. Phase 4's RC3 requirement still says "internal translation
strategy specified here" and "Core GL objects … via the facade, not ARB entry points"
(`docs/design/v2.0-RC3/DESIGN.md:1511-1514,1522-1524`). The selected operation retains core
objects and exports no ARB entry point or constant; only its native backend uses the ARB
geometry parameter facility, as the maintainer's requested alternative permits. This does
**not** prove complete core translation or relax the pack-facing dual-form requirement.
If the design maintainer intends the ARB prohibition to include even this hidden geometry-only
backend facility, that broader interpretation requires an explicit authority clarification;
it is not silently resolved by this phase. No authority text is edited and no general ARB
shader-object fallback is granted.

Per §G1.1 none of RESEARCH.md, any of the three design revisions, or PINTONIUM_DESIGN.md is modified
by this session.

---

## 12. Implementation checklist

Attempt-6 additions: implement D-P1-66 capability capture/serialization and allocation/
recorder admission together; implement D-P1-67 typed facade, native tiers, full-state
restoration and recorder events together. §8's mixed-format/nondefault-state and
target-boundary cases are required implementation evidence; none has been executed here.
Implement D-P1-68 as whole-plan receipt in client/VBO/capture/replay paths and recorder
identity; §8's absent/supplied participation and exact restoration cases gate the cutover.
Implement D-P1-69/70/71 in the actual clear transaction, exact recorder factory and
texture lifetime path; §8's enabled-discard/first-replay-mask/completion/materialization
cases gate implementation. Synchronize P5/P7/P10/P14 and P2 evidence; no execution claimed.

Ordered so that each item is independently actionable and the Impl gate — *"project builds empty
modules + passes an architecture test proving `:engine` has no MC/loader/mixin/LWJGL classpath; CI
green"* — is reached at item 15, with the rest completing the phase's scope.


R32 implementation prerequisite: land D-P1-57's actual setter interception, guarded bypass,
transactional acquire/restore and recorder together with positional route encoding; migrate
all callers and drawBuffersWere assertions, with no int-array or setter-only lock alias.
Execute the §8 boundary/failure and real vanilla-draw cases before claiming support.
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
| 18 | Handle types (`GLHandle`, sealed and permitting exactly four direct categories; the four leaves non-sealed; `BorrowedDepthAttachmentHandle extends TextureHandle`; `UniformLocation`) plus owned/foreign lifetime and origin rules | `v0.1` | `GLHandle.permits` still names exactly four categories and no renderbuffer. Backend/recorder implementations compile outside the package; the borrowed marker adds no category and item 22c proves that a public implementation alone confers no permission |
| 19 | The seven service interfaces + `GLDevice` + result/value types, including both program-selection modes, `attachDepth`/`attachDepthStencil`, first-copy `initializeDepthTextureFromFramebuffer`, steady `copyDepthToTexture`, readback/upload/ivec verbs, state verbs, `drainErrors()`, and `ReplayAwareGLError` | `v0.1` | Compiles with no GL constant/raw object name and no LWJGL buffer type; combined attachment and first-copy initialization are explicit operations, and replay attribution cannot be inferred from labels (`[D-P1-39]`, `[D-P1-40]`, `[D-P1-42]`) |
| 19a | Replay-result cardinality and evidence matrix | `v0.1` | One `ReplayAwareGLError` per triggering drained error; isolated recurrence is true; clean, batched, ambiguous, non-reproducing, and injected foreign-error cases are false |
| 20 | `RecordingGLDevice` constructors, `GLCall`, `GLCallLog`, `ScriptedResponses`, authenticated recorder borrowed-depth handles, and summary-not-content logging | `v0.1` | `RecordingGLDeviceTest` — exact distinct event names/arguments for fixed-function, depth-only/combined attachment, first/steady copy; restored binding identities; borrowed handles not leak-counted; forged/wrong-origin rejection appends nothing; existing ordering/failure/bounded-log/stable-render checks |
| 21 | `ReplayAssertions` incl. `bindsBalanced()`, `noLeakedObjects()`, **`noUseAfterDelete()`**, `drawBuffersWere()` | `v0.1` | `ReplayAssertionsTest` — conforming/violating fixtures, fixed-terminal distinction, depth operation distinction and restored-binding arguments; borrowed handles are neither created/leaked nor deletable |
| 22 | `Lwjgl3GLDevice` + the seven service implementations in `mod.glue`, **issuing every `GlStateManager`-cached verb through `GlStateManager`** (`[D-P1-29]`) and implementing the stated `glGetError` cadence behind `drainErrors()` — **per facade call under a debug context or `-Dschmaloogium.debug.recordGL`/`glLabels` (those two flags only); otherwise once per drain, where a drain is a `glGetError` *loop* terminating on `GL_NO_ERROR` and is skipped entirely when no mutating **facade** call has occurred since the previous drain, so that a window holding one mutating **facade** call names that call and a window holding many carries `subjectLabel = "(batched, N calls)"`** (`[D-P1-30]`, `[D-P1-32]`). **The backend also retains the name passed to `locate(program, name)`** on its `UniformLocation` implementation, so a record can carry a uniform name at all (`[D-P1-34]`). It maps `useFixedFunction()` to program zero internally through the same selection path as `use`, without constructing/deleting a zero handle or exposing the integer across C-1 (`[D-P1-39]`) | `v0.1` | `SeamLwjglConfinementTest` (item 14) confines it; a manual `runClient` reaching the main menu; a review pass over the verb list in §4.7.4 confirming no raw-LWJGL state call, that the two program-selection modes map respectively to the supplied live program and program zero, that the batched record is emitted **once per window and never claims per-call attribution**, that the drain **loops** rather than querying once, that a drain after a drain issues no query, and that `subjectLabel` carries a real uniform name on a single-call window — no headless test can prove the live backend mapping, which is why it is called out here |
| 22a | **Review hook extension for `[D-P1-29]`, added against PD §17 B11** (§11.3 item 11): confirm by reading that `StateService.snapshot(...)` reads **real** state for every `StateAspect` and returns no constant. Pintonium's `GLStateManagerImpl.getColorMask()` hardcodes all-true, which satisfies every signature and passes every driverless test while silently breaking the §G4.6 restore discipline every later phase depends on. Numbered `22a` because it belongs to item 22's review pass, which exists precisely for obligations no test can catch | `v0.1` | Reviewer confirms each snapshot aspect resolves to a `GlStateManager` read or a driver query, and that `restore()` round-trips a deliberately-perturbed state in a `runClient` spot check |
| 22b | The `mod.glue` ordinary vanilla-texture provider slot (`ForeignTextureProvider` / `ForeignTextures`, `[D-P1-36]`) and per-use object resolution | `v0.1` | Provider compiles from `mod.glue`; seam tests pass; before-install lookup is empty. `FramebufferDepthContractTest` plus live-backend review confirms closure: ordinary values are accepted only by `bindToUnit`/label and rejected by every other texture-accepting verb, including both §0.18 additions; per-use resolution survives vanilla reload |
| 22c | `Lwjgl3GLDevice` borrowed-depth authentication and framebuffer-depth implementation (`[D-P1-40]`): private same-device/context credential, owned-versus-borrowed matrix, exact combined-format check, first-versus-steady storage state, and binding restoration | `v0.1` | `FramebufferDepthContractTest` exhausts owned/ordinary/borrowed/forged/wrong-origin/deleted cases in the recorder. Live-backend review confirms no public-marker trust, `attachDepth` detaches stencil, combined attachment uses the same packed object twice, initialization maps to exact-format `glCopyTexImage2D`, steady copy never redefines storage, and every exit restores read/draw/texture bindings |
| 22d | Implement the exact legacy geometry operation and closed enums in `engine.gl`, native mapping in `mod.glue`, and recorder transaction in `engine.gl.record` (D-P1-44); no generic integer parameter API | `v0.1` | `LegacyGeometryPreLinkContractTest` covers the headless boundaries; later backend exercise compiles/links original legacy varying/built-in and core-layout fixtures, checks exact native parameters, capability/limit rejection, cleanup and unchanged current bindings. Phase 3 native output and Phase 4 migration must be granted and freshly verified first; no test or runtime exercise is performed by this architecture amendment |
| 22e | D-P1-48 effective-link metadata and fullscreen backend/recorder cutover | `v0.1` | `FullscreenGeometryContractTest` and §8 live proof; no QUADS with TRIANGLES input, no source/GL query between adjacent fullscreen copies, correct deletion/restoration/failure behavior; P7 receiving amendment required |
| 22f | D-P1-49 exact jcpp pin, closure, notices and mod containment | `v0.1` | Resolved compile/runtime graphs remain platform-free in engine; shipped mod loads identical verified library closure; no dependency on incidental Minecraft libraries |
| 22g | D-P1-50 bounded vertex-input source/service/recorder and early compatibility mechanism | `v0.1` | VertexInputContractTest/EarlyBailContractTest plus live client/VBO/list pointer/current-value restoration and approved topology conformance; P7/P10 adoption required, extended producers remain v0.3 |
| 23 | `CapabilityProbe` in `mod.glue` + `-Dschmaloogium.debug.dumpCapabilities`, invoked at **`OpenGlHelper.initializeTextures` @`RETURN`** — stage 2 of §4.13's bring-up sequence (`[D-P1-37]`). The hook itself is Phase 7's catalog entry (App E); this item is the probe's placement requirement against it | `v0.1` | Running the client with the flag writes a parseable profile that round-trips through item 17, **and the probe runs after vanilla's texture setup rather than before** — a profile with a plausible `GL_MAX_TEXTURE_IMAGE_UNITS` is the cheap signal |
| 24 | `-Dschmaloogium.debug.recordGL` decorator wrapping the live device, **with a bounded log** (`GLCallLog.bounded(100_000)` by default, oldest discarded and counted) supplied to the device through `new RecordingGLDevice(profile, responses, log)` (§4.7.5) — the decorator constructs the ring, the device does not — and off unless the flag is set | `v0.1` | Flag produces a `GLCallLog` dump in the same format the tests assert over; a long session does not grow the log without bound, and the dump reports `droppedCallCount()` when it wrapped |

### Conventions

| # | Item | Tag | Test hook |
|---|---|---|---|
| 25 | `Log`/`LogSink`/`Logs` + `LogChannels` constants (§4.9.2) | `v0.1` | `LogChannelTest` — uniqueness, prefix, no-op sink before install |
| 26 | log4j-backed sink installed in `mod.core` at `preInit` | `v0.1` | A `runClient` shows `schmaloogium.boot` lines |
| 27 | `EngineDiagnostic`/`DiagnosticSeverity`/`UserChannel`/`DiagnosticReporter` + `:mod` routing | `v0.1` | `DiagnosticRoutingTest` — including `CHAT`-with-no-player degradation |
| 28 | SPDX headers on every source file; README license statement; THIRD-PARTY with standing prohibitions and the verified shipped jcpp closure when admitted | `v0.1` | Notice/license review uses exact resolved artifact versions; no empty-ledger claim after dependency inclusion |
| 29 | PINS ledger begins with §4.2.6a current executable baseline labeled inspected/unverified, retains July history, then records actual verification and D-P1-49 exact jcpp pin | `v0.1` | Wrapper/plugin/loader are not downgraded; CI setup inputs align to wrapper; no invented runtime/availability result |

### Mixin wiring

| # | Item | Tag | Test hook |
|---|---|---|---|
| 30 | Three mixin config JSONs per §4.5.2; permanent empty server arrays, owner-populated client arrays; MOD plugin class/key together under item37 | `v0.1` | Packaged configs/classes load on pinned Cleanroom; no absent plugin class |
| 30-frame | Create the four Phase 7 package roots named by `[D-P1-41]` when Phase 7 first supplies code; do not add placeholder production classes merely to create directories | `v0.1` | Seam/package architecture test assigns every Phase 7 frame class to exactly one of `engine.frame`, `mod.glue.frame`, `mod.mixin.frame`, or `mod.conformance`; C-1 through C-4 remain green |
| 30-textures | Populate only the three Phase 13 texture homes named by `[D-P1-43]` when Phase 13 supplies its implementation, after the required dependency reviews; no placeholder production classes or new conformance dependency | `v0.5` | Existing C-1 through C-4 and Mixin config/package agreement cover the new tenants; engine texture policy remains headless and adapters consume only published engine APIs |
| 30a | **Mixin config ↔ package agreement test** in `:mod` (`[D-P1-38]`, §4.5.2a): for each config, the `@Mixin`-annotated classes in its declared `package` **excluding any sub-package another config declares** are exactly the classes its arrays name. **The exclusion is part of the specification, not an implementation liberty** — the three packages are nested (§4.5.2 observation 1), so a subtree-scoped predicate fails on a *correct* config set as soon as PRE_INIT or MOD gains a tenant (V12-9). This is the drift insurance taken **instead of** Pintonium's class-scan plugin, and it is the whole of what the rejection owes | `v0.1` | Passes vacuously at v0.1 (arrays empty, packages empty) and fails informatively when item 33's throwaway mixin is added without a matching array entry — which makes item 33 its first real exercise. A second case is worth writing at the same time, because it is the one the predicate exists for: a mixin in `…mixin.preinit`, listed in `schmaloogium.preinit.mixin.json` only, must leave **all three** configs green |
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
| 37 | Real `SchmaloogiumMixinPlugin` early-check implementation and MOD `plugin` key together; register P10 geometry-only checks before evaluation, retain whole-family veto | `v0.1` | Config loads; positive/throwing early check vetoes every dependent hook without game/GL initialization; clean path admits only the required subset; runtime target audit proves placement |
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

*End of PHASE_1_DOC.md. Per §G1.1 the build session stopped here. **Twenty-four** verify sessions have
since run — `PHASE_1_REVIEW_1.md` through `PHASE_1_REVIEW_14.md` returned
PASS-WITH-CORRECTIONS, and `PHASE_1_REVIEW_15.md` returned the literal PASS that closed that loop —
then `PHASE_1_REVIEW_16.md` and `PHASE_1_REVIEW_17.md` returned PASS-WITH-CORRECTIONS — and
`PHASE_1_REVIEW_18.md` returned literal PASS, round nineteen returned PASS-WITH-CORRECTIONS, and
round twenty returned literal PASS, round twenty-one returned PASS-WITH-CORRECTIONS, round
twenty-two returned literal PASS, round twenty-three returned literal PASS, and round twenty-four
returned PASS-WITH-CORRECTIONS — and **twenty** fix-up/maintenance sessions: the first applied
round one's F-1 … F-12 (§0.4); the second applied
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
and recording what that branch leaves; and the **eighth** applied round **eleven**'s one correction
and **all five** of its notes (§0.11), none refused and none narrowed, and — as a second and
deliberately separate cause, under the project owner's direction — **migrated this document from
design v1.1 to `docs/design/v2.0-RC2/DESIGN.md`** per §G0.4 step 3, which required §4.12's glue-seam
completeness check and §4.13's engine bring-up sequence to satisfy REV2's two new Doc-gate criteria;
and the **ninth** applied round **twelve**'s **nine corrections** (§0.12), none refused and none
narrowed, deferring its **three notes** with their reasons recorded in `PHASE_1_REVIEW_12.md`'s
`## Resolutions` — eight of the nine landing in the material the eighth fix-up had added and four of
them on `[D-P1-36]` alone, which is exactly what §0.11 predicted of unreviewed material; and the
**tenth** applied round **thirteen**'s **four corrections** (§0.13), none refused and none narrowed,
deferring its **four notes** with their reasons recorded in `PHASE_1_REVIEW_13.md`'s `## Resolutions` —
three of the four landing, again, in the ~140 lines the previous fix-up had added, and the fourth
(V13-4's mipmap misattribution in §3) being the round's one genuine miss by twelve prior rounds; and
the **eleventh** applied round **fourteen**'s **two corrections** (§0.14), none refused and none
narrowed, deferring its **five notes** with their reasons recorded in `PHASE_1_REVIEW_14.md`'s
`## Resolutions` — one correction (V14-1) landing in the bind-only block the tenth fix-up added, the
other (V14-2) a round-six-created row whose ownership clauses no round had examined; and the
**twelfth** applied Phase 4's downstream fixed-function request directly through the escape hatch
§4.7.4/§5.2 defines (§0.15), after round fifteen's PASS and without manufacturing a review finding:
`ShaderService.useFixedFunction()`, its recorder/replay semantics, backend obligation, tests,
milestone, decision `[D-P1-39]`, hand-off and checklist all land together; and the **thirteenth**
applied round sixteen's two corrections (§0.16), splitting absent-`final` passthrough from actual
fixed-function terminals and publishing Phase 2's derived-artifact workflow constraints in §5; and
the **fourteenth** applied round seventeen's sole closing-history correction (§0.17); and the
**fifteenth** accepted Phase 5's three downstream framebuffer/depth requests as `[D-P1-40]`
(§0.18), after round eighteen's literal PASS and without manufacturing a review finding; the
**sixteenth** applied round nineteen's three corrections (§0.19); and the **seventeenth** accepted
Phase 8's three package-home request after round twenty's literal PASS (§0.20); the **eighteenth**
applied round twenty-one's adjacent-ownership correction (§0.21); the **nineteenth** accepted
Phase 7's frame-package and replay-evidence requests after round twenty-two's literal PASS (§0.22);
and the **twentieth** applied round twenty-four's history and interface-coverage corrections (§0.23);
and the **attempt-8** fix-up applied review thirty-five's two corrections as D-P1-72/73 (§0.35), C-3's
confinement becoming the `mod.glue` package tree and D-P1-71's label limb its recorded-only form.
Every finding's disposition is recorded in the review files under `## Resolutions`, including the four
of round three's proposed fixes and the items of rounds five and six that were deliberately narrowed
rather than applied as written, and why.

**Current §G1.3 status (§0.25).** Review 25's literal PASS verified the §0.23 surface and remains
historical. The §0.24 package grants are preserved; §0.25 additionally grants only the native
legacy geometry facade operation and its incorporated §5 semantics. Phase 3 native-preserving
materialization and Phase 4 consumer migration remain explicitly ungranted. `PHASE_1_DOC.md`
is **not verified** and is not a valid dependency input until a fresh **whole-document** review
returns literal PASS. No review, build, test or verification run was performed, no code or other
document was edited, and no implementation or complete dual-form support is claimed. `v14` stays.*

*§0.26 integration amendment: P1 diagnostic permission and bootstrap/count/grant-ledger
reconciliation change §5. Current surface remains unverified; historical reviews are unchanged.
No implementation, validation or new PASS result accompanies this documentation fix-up.*

**Current §G1.3 status — §0.28.** R26 remains PASS-WITH-CORRECTIONS, not PASS.
Its separate fix-up and the commissioned jcpp/vertex/current-build grants change binding
§5 and incorporated semantics. P3 source and P4/P7/P10 receiving integration are separately
owned; fresh independent whole-document reviews and final integration are still owed.
No implementation, execution, validation command or conformance result accompanies these edits.

**Current §G1.3 status — §0.35.** R35 returned PASS-WITH-CORRECTIONS; the attempt-8 fix-up
(§0.35) resolves both corrections as D-P1-72/73 — C-3's confinement restated as the
`com.schmaloogium.mod.glue` package tree, D-P1-71's label limb given its recorded-only form —
and the review-36 fix-up appended this record plus §3's two RESEARCH.md coordinate repoints.
Addenda §0.29–§0.34 — the settled producer receipts, the R27 native-array isolation, the P3
schema ownership receipt and the attempt-5/6/7 corrections — remain in force with their own
recorded inputs. `PHASE_1_DOC.md` is **not verified** and is not a valid dependency input
until a fresh **whole-document** review returns literal PASS. No implementation, validation
command or conformance result accompanies these edits. `v14` stays.
