# PHASE_1_DOC.md — Verify session, round six

**Session type:** verify (`DESIGN.md` §G1.2) · **Document under review:** `Schmaloogium/PHASE_1_DOC.md`
**Date:** 2026-07-25 (pin re-verification performed 2026-07-25 ~01:35–01:40 UTC; the document is
stamped 2026-07-24) · **Verdict:** **PASS-WITH-CORRECTIONS**

---

## 0. The headline, stated first because it changes what this session could be

**The round-five fix-up never ran.** This pass was commissioned to attack new, unreviewed material
written by a fix-up applying `PHASE_1_REVIEW_5.md`'s V5-1 … V5-8. That material does not exist.

Three independent pieces of evidence, each checked directly:

1. `PHASE_1_REVIEW_5.md` has **no `## Resolutions` section**. Rounds one through four each have one
   (`PHASE_1_REVIEW.md`:278, `_2`:479, `_3`:720, `_4`:847). Round five's file ends at line 491 with
   *"Per §G1.2 this session stops here and fixes nothing."*
2. `PHASE_1_DOC.md` carries **no §0.6 addendum** (§0.4 is round one, §0.5 is rounds two–four, and
   there is nothing after it) and **no `[fix-up: PHASE_1_REVIEW_5.md …]` provenance marker anywhere**
   — the convention every previously-applied finding carries in §11.1.
3. Every site round five named is **unchanged**. I checked all eight at their cited line numbers
   against the current file; the table in §3 below records each one.

The document's own statements about its position in the cadence are therefore now false:

- §0.5 (145–149): *"a **fifth verify session** is required before Phase 2, Phase 3 or any other
  dependent consumes this document. Until that verdict exists, this doc is **not** a valid dependency
  input."* The fifth verdict exists; it was PASS-WITH-CORRECTIONS.
- The closing paragraph (2737–2745): *"**Four** verify sessions have since run … Because this fix-up
  again altered **§5**, the next step in the cadence is a **fifth verify session**."* Six have now run.

So this pass could not do the job it was sent to do — audit the fix-up's design choices on the
`glGetError` cadence, the instancing verb, the CI ordering and the ASM exclusion — because no choices
were made. What it did instead: re-derive round five's eight findings from source rather than
accepting them, extend one of them to two sites round five did not record, and attack the whole
document independently for anything five passes have missed. That produced six new items.

---

## 1. What I read, and in what order

Assigned reading, in the order §G1.2 prescribes:

1. `DESIGN.md` Part I in full (§G0–§G10, lines 1–574) and the Phase 1 spec in Part II (585–658);
   other phases by title only from §G5.1, plus the disclosed reads below.
2. `RESEARCH.md` §0 and §1, then the spec's **Required inputs**: §5.1, §5.2, §5.3, §6.1, §7.2, §12.2.
3. Template ground truth, complete: `build.gradle`, `settings.gradle`, `gradle.properties`,
   `gradle/scripts/{dependencies,extra,publishing}.gradle`, `gradle/wrapper/gradle-wrapper.properties`,
   all eight files under `src/**`, all three `.github/workflows/*.yml`, `README.md`, `.gitignore`,
   and `LICENSE` + `git log` (read-only) for the D-7 claim.
4. `PHASE_1_DOC.md`, in full.
5. **Last, only after my own findings were formed:** `PHASE_1_REVIEW.md`, `_2`, `_3`, `_4`, `_5`,
   including their `## Resolutions` sections (and the absence of one in `_5`).

**Read beyond the assigned list, each because a finding turns on it** (§G1.1/§G1.2 recording rule):

- **RESEARCH.md §3.2** (source directives — `const int countInstances = N`), **§3.4**, **§3.5** (the
  standard macro header, to test §3's macro row), **§4.1–§4.5** (probe set, registry mechanics,
  framebuffer architecture, per-frame flow, shadow pass), **App A.3**, **App B.1–B.5**,
  **App D.1–D.4** end to end, **App F.1/F.5/F.6/F.7**, **App G** (matrix licensing, for §8.3's claim).
- **`DESIGN.md` Part II, the *Scope-in* bullets of the Phase 6 and Phase 11 specs** — Phase 6's
  line 990 (*"Per-uniform GL-error isolation (ladder step 2)"*) and Phase 11's line 1350
  (*"runtime errors disable that uniform only (ladder step 1)"*). V6-2 turns on the second of these
  and cannot be stated without it. §G1.1 line 78 bars a *build* session from other phases' specs;
  rounds four and five established — and I adopt — that a verify session auditing an ownership or
  consumer claim has no such bar. Disclosed here in any case.

**Hard rules observed.** No code, no builds, no tests, no fixes, no scope creep. `PHASE_1_DOC.md`,
`RESEARCH.md`, `DESIGN.md` and the five existing review files were **not modified**. **No adversarial
sub-agents were used** — §G1.2 permits them; I did not need them, and every quotation below was read
against its source file by me directly. Live network was used **for the pin table only**, which the
Phase 1 spec orders.

---

## 2. Verification performed independently of the document

### 2.1 Pins — executed literally per §4.2.6, and nothing has drifted

The brief warned that drift was likely. It is not: the pins were last verified 2026-07-25 ~01:15 UTC
and the clock here reads **2026-07-25 01:35 UTC** — twenty minutes, not a day. I ran the procedure
anyway, literally, because the spec orders it and because a procedure that is only ever described is
not a procedure.

| Step | Executed | Result |
|---|---|---|
| §4.2.6 step 2 — `GET repo.cleanroommc.com/…/cleanroom/maven-metadata.xml`, read `<release>` | yes | `<latest>` = `<release>` = **`0.6.6-alpha`**; `<lastUpdated>` = `20260724133703`; version list ends `0.6.4-alpha, 0.6.5-alpha, 0.6.6-alpha` |
| §4.2.6 step 2 — cross-check `GET api.github.com/repos/CleanroomMC/Cleanroom/releases` | yes | `0.6.6-alpha` published `2026-07-24T13:37:05Z`; `0.6.5-alpha` `2026-07-24T01:30:51Z`. Maven `<lastUpdated>` and the GitHub publish time agree to two seconds; no tag in one and not the other |
| §4.2.6 step 3 — diff notes forward, flag the six categories, **rule** | yes | 0.6.6's notes name a **Foundation classloader** change (adding `com.mojang.` to the classloader) and a **CleanMix/MixinBooter** version bump — both flagged categories. Ruling: **extra verification**, not block: neither names anything this document pins, asserts or tests. The check the ruling selects is §4.2.6's own — a `runClient` with `enable_mixin_debug=true` and a confirmed refmap (§12 item 33), plus a `runClient` to the main menu. Both are already Phase 1 checklist items |
| §4.2.6 step 7 — re-check the remaining rows against their Repository coordinates | yes | Unimined kappa: `arcseekers` `<release>` = **`1.4.26-kappa`** (…1.4.24, 1.4.25, 1.4.26). ASM: Google Central mirror `<release>` = **`9.10.1`**, `<lastUpdated>` `20260523184314`. Template rows unchanged in the checkout |
| The pin table's stated **trap** | verified | `maven.wagyourtail.xyz/releases/…/xyz.wagyourtail.unimined.gradle.plugin/maven-metadata.xml` lists 71 versions topping out at **`1.4.1`** with **zero** kappa builds. §4.2.6's warning is exactly right |

**The procedure is executable and unambiguous.** Step 3 is the one that could have been vague, and it
was not: the three rulings partition cleanly and I reached one without hesitating. Round five reported
"record only" for the same window; I reached "extra verification" because 0.6.6's notes do name two
flagged categories. That is not a disagreement about the pin — both rulings keep `0.6.6-alpha` — and
the difference is itself evidence the step's own escalation clause works, since step 3 says an
ambiguity between two operators is a finding and the conservative branch applies. **It is not
ambiguous here:** the flagged categories name nothing this document pins, which is the literal test
for "extra verification", so my ruling is the one the text produces and round five's was a shade
generous. I record it and raise no finding — the difference costs one `runClient` that Phase 1's
checklist already contains.

**No drift. The pin table stands as written.**

### 2.2 Template ground truth (§4.1) — re-checked row by row

All fifteen rows of §4.1 are accurate against the files. The ones easiest to get wrong are right: the
branch is `main` and carries no mixin config, no `MixinConfigs` attribute and no `mixin { }` block;
`settings.gradle` has no `include` lines and derives `rootProject.name` from the directory;
`cleanroom { accessTransformer "${rootProject.projectDir}/…" }` really does hardcode the root project
dir; `enable_lwjglx` has exactly one effect (`compileOnly "com.cleanroommc:lwjglx:1.0.0"`, guarded, in
`dependencies.gradle`); `jar` is `finalizedBy(remapJar)` so a merge into `jar` precedes remap; all
three workflows reference root-relative `build/libs`; `.gitignore` already carries `**/build/`.
§11.3's five template defects are all real — including `extraArgs.split { "\\s+" }` at
`build.gradle:65`, which is Groovy's `CharSequence.split(Closure)` partition rather than a regex split,
and `extra.gradle`'s comment describing three helper methods that do not exist.

One item worth recording because the document already handles it correctly: **the GPL-3.0 LICENSE swap
has already been performed in the repository** (`git log`: `aa917a6 Update LICENSE from MIT to
GPL-V3`; the file is the verbatim 674-line GPL-3.0 text). §11.2's D-7 row and §12 item 1 both say so
and treat the item as verification rather than restoration. Correct.

### 2.3 Doc gate — **PASS**, all three criteria, read literally

| Criterion (`DESIGN.md`:650–651) | Verdict |
|---|---|
| "module/package layout finalized with dependency rules as testable constraints" | **met** — §2.1's table assigns a filling phase to every package in all three modules; C-1…C-4 (§4.3) are mechanical, each with a named test in §8.1 and a checklist item in §12 |
| "every D-1..D-10 either satisfied by this phase or explicitly deferred with its owner phase named" | **met** — §11.2 disposes all ten; every deferral names an owner (D-3→P2, D-4→P4, D-5 catalog→P7, D-9 policy→P5+/P7, D-10→P2) |
| "pin table complete with re-verification procedure" | **met** — fourteen rows, each with pinned value, location, repository coordinate and re-verification date; §4.2.6's seven steps and §10.1's §G4.4 restatement agree with each other, and §2.1 above shows the procedure executes |

### 2.4 Template completeness, OQ spikes, scope, binding decisions

- **All thirteen §G9 sections (0–12)** are present and substantive. The `14b`/`36b`/`41b` lettering in
  §12 is deliberate, explained, and preserves the numbering the Impl gate depends on.
- **All four assigned OQs** carry a full §G4.4 spike (OQ-2 §10.1, OQ-12 §10.2, OQ-20 §10.3, OQ-21
  §10.4) with the verbatim question, a procedure, success **and** failure criteria, and a fallback
  designed now. I checked all four quotations against RESEARCH.md §11; all four are faithful.
- **Scope discipline holds in both directions.** Every *Scope — in* bullet has a section, including
  the ones easiest to drop (the OQ-12 note, the bail-registry mechanism, the CI extension points, the
  headless JUnit baseline). Nothing from *Scope — out* is designed: no harness content, no pack
  format, no GL policy beyond the facade shape, no GUI evaluation. §1.2's twelve-row table names an
  owner for every adjacent concern.
- **Binding decisions.** No D-1…D-10 is contradicted, and no contract-visible component is
  "improved" — with the exception V5-2 raises, which is an unspecified verb rather than a committed
  divergence. **One binding *characterization* is wrong**, and it is new: see V6-2.

### 2.5 Contract fidelity — re-derived from source, and correct

I re-derived the claims round five checked rather than inheriting its verdict, because the brief said
appendices read end to end are where the worst defects hide. Every one holds:

| Claim the doc makes | Source read | Verdict |
|---|---|---|
| `blendFunc` is `ivec4` (`srcRGB`, `dstRGB`, `srcA`, `dstA`) | App D.4:1376 | ✅ verbatim |
| `atlasSize` is `ivec2`; `eyeBrightness`/`eyeBrightnessSmooth` are `ivec2` and live in App **D.1** | App D.3:1366; App D.1:1331–1332 | ✅ — the D.1/D.3 split is cited correctly at all five sites (§0.1, §3, both §4.7.4 sites, `[D-P1-25]`) |
| No `ivec3` or `mat3` has a contract consumer | App D.1–D.4 end to end; App F.6:1492 (`float\|int\|bool\|vec2\|vec3\|vec4`) | ✅ — I swept the whole inventory, not the cited rows |
| Composite/final block is "identity ortho, fog/depth/blend disabled" | §4.4:563 | ✅ verbatim; `depthMask` (writes) really is a different bit of state from `depthTest` |
| `depthtex1`/`depthtex2` are copy-target **textures** | §4.3:514–516; App B.2:1221–1222 | ✅ — `copyDepthToTexture` is right and `blit` genuinely is not |
| The fixed unit map re-points up to 16 units per program switch | App B.3 (units 0–15); §4.2:503–505 | ✅ — `[D-P1-29]`'s "highest-frequency call" is justified |
| `final` renders to the vanilla framebuffer with anaglyph-aware colour masking | §4.3:526 | ✅ — the deferred colour-mask row is correctly reasoned |
| The macro header's GL-side inputs are `MC_GL_VERSION`/`MC_GLSL_VERSION`/`MC_GL_VENDOR_*`/`MC_GL_RENDERER_*`, and `MC_VERSION`/`MC_OS_*`/option macros are Phase 3's | §3.5:312–318 | ✅ — the split is exactly right |
| `backFace.*` are App F.1 engine flags | App F.1:1443 | ✅ |
| GLSL-120 packs have no `gl_InstanceID`; `instanceId` is an `int` **uniform** | §3.5:306–309; App D.4:1377; §3.2:254–255 | ✅ — this is what makes V5-2 correct |

### 2.6 The five rulings the brief listed as settled

Re-checked; **all five hold** and none is re-raised. §4.7.4's `GlStateManager` sentence is a bypass
rule and is correct as written. `backFace.*` ownership belongs to Phase 3's mandated engine-flag map.
The ARB translation *strategy* is Phase 4's per `DESIGN.md`:848, and §3's `[V:doc]` tag qualifies the
pack-side contract item. §4.2.5's "the Impl-gate item **that depends on this merge**" is restrictive
and item 7 is the one carrying the merge instruction. §12 item 30's `runClient` hook does cover the
nested-mixin-package question.

---

## 3. Round five's findings, re-verified against the current document

Every one is **outstanding**, verbatim, at the line round five cited. I re-derived each rather than
taking it on trust; the "independently confirmed" column records what I checked, not what I read.

| # | Severity | Site in the current file | Independently confirmed? | §5? |
|---|---|---|---|---|
| **V5-1** `glGetError` cadence defeats rung 2 | correction | §4.7.4:1456–1462 (`"cannot lose an error"`, `subjectLabel = "(batched)"`); §5.2:2007; §6:2077–2078; §9:2238 — **plus two sites round five did not record: see V6-3** | **Yes.** GL sets an error flag and records no further error of that flag until `glGetError` clears it, so a sweep in which five uniforms fail yields at most one record — "cannot lose an error" is false. And in the shipping configuration (no debug context, no `-Dschmaloogium.debug.*` flag) every record carries `subjectLabel = "(batched)"`, so Phase 6 cannot name a uniform, while §5.2, §6 and §9 promise it unconditionally | **yes** |
| **V5-2** `fullscreenQuadInstanced(int)` | correction | §4.7.4:1396 — still the verb's **only** occurrence in 2 745 lines | **Yes.** `countInstances` re-renders N times with `instanceId` **incrementing** (§3.2:254–255, App A.3:1159), and `instanceId` is an `int` *uniform* (App D.4:1377). One instanced draw cannot vary a uniform per copy, and GLSL 120 has no `gl_InstanceID` (§3.5:306) — which is why the contract carries a uniform at all. The faithful shape is a caller-side loop over `fullscreenQuad()`. The verb has no §3 row, no §5 mention, no §9 tag, and no stated semantics | **conditional** |
| **V5-3** named CI seam steps preempted by `./gradlew build` | correction | §4.11:1934–1949; §6:2085; §12 item 38:2720; `[D-P1-24]`:1942–1945 | **Yes.** `build` → `check` → `test` in every subproject, so `./gradlew build` from the root already runs all four seam tests; on failure GitHub Actions skips the later steps. The document refutes its own premise at §4.2.4a:635, which argues that `./gradlew build` fails at `:conformance:compileTestJava` — reachable only through `test` | no |
| **V5-4** `:mod` ASM remedy | correction | §4.2.4:552–559; §4.2.6:691; `[D-P1-3]`:2465 | **Yes.** Gradle arbitrates versions per `group:name`; `org.ow2.asm:asm` and `org.ow2.asm:asm-debug-all` are different modules with no declared shared capability, so `resolutionStrategy.force` is inert against the legacy jar and only the `exclude` does work. And the block is scoped to `testRuntimeClasspath`, while C-2/C-3 *compile* against `testCompileClasspath` | no |
| **V5-5** `[D-P1-24]` decision-log residue | note | §11.1:2486 — still reads "CI runs `:engine:test` as its own named …" | **Yes.** §4.11:1942 now says "**Both** module tasks are named deliberately"; the decision log was not updated | no |
| **V5-6** `RecordingGLDevice` cannot be given a bounded log | note | §4.7.5:1541–1544 — sole constructor is `(GLCapabilityProfile, ScriptedResponses)` | **Yes.** Four sites promise a default capacity of 100 000 for the live decorator (§4.9.3:1799, §7:2123–2129, §12 item 24:2690) and §5.2:2011 exposes the bound to Phase 2, but no published member accepts a `GLCallLog` or a capacity | marginal |
| **V5-7** Phase 8 absent from the depth-copy consumers | note | §5.2:2015 names 6, 5, 13, 9; §9:2237 names 6, 5, 13 | **Yes.** §4.5:578 records the shadow pass "copies depth→shadowtex1 (water-shadow split)" and App B.2:1224 lists it beside `depthtex1`/`depthtex2`. That is a third instance of the operation, owned by Phase 8 at v0.2 | **yes** |
| **V5-8** configuration-time classpath resolution | note | §4.2.3:489–496, §4.2.4:563–570, §4.2.4a:619–626 | **Yes.** `sourceSets.main.compileClasspath.asPath` resolves eagerly during task configuration; in `:mod` that classpath contains Unimined's task-produced remapped Minecraft artifact. §4.2.5:661–668 flags the identical hazard for the jar merge and this one is not flagged | no |

**Where I differ from round five:** nowhere on substance. Two small extensions — V5-1 has two more
sites (V6-3) and V5-2's "no §12 checklist item" is loose, since §12 item 19's "the seven service
interfaces" covers `DrawService` generically. Neither changes the finding.

---

## 4. New findings

### V6-1 — The document asserts a cadence position that is two steps stale, and the round-five fix-up's artifacts do not exist · **correction** · no §5

**Location:** §0.5:145–149; the closing paragraph, 2737–2745. Also `PHASE_1_REVIEW_5.md` (no
`## Resolutions` section).

**Claim under test:** that §0's addenda and the closing paragraph describe the document's current
state, which is what a dependent session reads them for.

**Evidence.** §0.5 ends: *"a **fifth verify session** is required before Phase 2, Phase 3 or any other
dependent consumes this document. Until that verdict exists, this doc is **not** a valid dependency
input (§G5.3)."* The closing paragraph says *"**Four** verify sessions have since run"* and *"the next
step in the cadence is a **fifth verify session**."* Five had run before this one; six have now. There
is no §0.6, and no `[fix-up: PHASE_1_REVIEW_5.md …]` marker anywhere in the file — the provenance
convention every applied finding carries in §11.1 (`[fix-up: PHASE_1_REVIEW_4.md F4-3]` and so on).
`PHASE_1_REVIEW_5.md` carries no `## Resolutions` heading, which §G1.3 makes the fix-up's deliverable.

**Why this is a document finding and not only a process observation.** §G1.3 defines "verified" partly
as *"all resolutions recorded"*, and §0.4/§0.5 are the document's own record of where it stands in that
loop. A Phase 2 or Phase 3 session that reads §0.5 and the closing paragraph literally — which is what
they are for — concludes that the outstanding gate is a *fifth* verify session and that round four's
material is the newest unreviewed content. Both are false, and the second is the more misleading: the
newest unreviewed content is nothing, because nothing was written.

**Fix shape.** A §0.6 addendum recording round five's verdict and the round-six fix-up, the closing
paragraph updated to the true count, and `## Resolutions` written into `PHASE_1_REVIEW_5.md` — none of
which can honestly be done until the corrections are actually applied. This finding closes when the
fix-up that applies V5-1 … V5-8 and V6-1 … V6-6 records itself.

### V6-2 — §6 assigns ladder **rung 1** to a failure mode `DESIGN.md` does not describe, and §2.4 and §5.2 repeat the error · **correction** · **§5** (the row V5-1 already opens)

**Location:** §6's rung-1 row (2078); §2.4's `GLError` row (300); §5.2's GL-error row (2007).

**Claim under test:** that §6 maps §G2.4's ladder onto this subsystem "case by case" as §G9 requires,
and that the GL-error drain is "the signal §G2.4's **rungs 1–2** act on".

**Evidence.** `DESIGN.md`:217 states rung 1 verbatim: *"A custom uniform that errors at runtime
disables **that uniform** only."* Rung 2 (218) is the built-in-uniform GL-upload case. The
distinguishing axis is **which kind of uniform**, and `DESIGN.md` itself settles rung 1's owner and
its trigger: Phase 11's *Scope — in* at line 1350 reads *"**Error isolation**: parse errors disable
that uniform at load (chat-visible warning); **runtime errors disable that uniform only (ladder step
1)**; division-by-zero/NaN propagation semantics defined."* Rung 1 is an **expression-evaluation**
failure inside `engine.expr`, at v0.4, and it never reaches a GL call.

§6:2078 instead reads: *"**A single feature's GL call fails** (a capability the pack asked for is
unsupported in practice) | **1** | … the drain names the failing operation, the owning phase turns
**that feature** off and continues. Rungs 1 and 2 differ in what gets disabled, not in how the failure
is observed."* Every clause of that is wrong about rung 1: the trigger is not a GL call, the thing
disabled is a uniform and not a feature, no owning phase is named where `DESIGN.md` names one, and the
two rungs differ precisely in *how the failure is observed* — one is an evaluator error, the other a
driver error. §2.4:300 and §5.2:2007 then both describe the drain as *"the signal §G2.4's **rungs 1–2**
act on"*, which inherits the same mistake at the two places a dependent is most likely to read.

**This is new fix-up material.** Round four's F4-1 asked that *"§6 must state where rungs 1 and 2
live"*. The fix-up satisfied the letter of that by inventing a rung-1 case rather than recording that
rung 1 lives in Phase 11, above the facade. Nobody has reviewed it, and round five did not reach it.

**What would refute this finding:** a reading on which a custom uniform's *GL upload* error is rung 1
(defensible — rung 2 is scoped to *built-in* uniforms, so the custom-uniform upload case has to land
somewhere). That reading would rescue §5.2's and §2.4's "rungs 1–2" phrasing. It does **not** rescue
§6:2078, which is about capability features rather than uniforms under any reading.

**Fix shape** (the fix-up's call): rewrite §6's rung-1 row to say that rung 1 is Phase 11's
expression-evaluation isolation and lives above the facade — optionally keeping the
capability-feature case as its own unnumbered row, which is a real failure mode that simply is not
rung 1 — and either narrow §2.4/§5.2 to "rung 2" or state the custom-uniform-upload reading
explicitly. §5.2:2007 is the same row V5-1 forces open, so this costs no additional §5 churn.

### V6-3 — V5-1's defect has two sites round five did not record · **correction** · no §5 beyond V5-1's

**Location:** §2.4's key-type table (300); §12 item 22 (2688).

**Evidence.** Round five's V5-1 Location line names §4.7.4, §5.2, §6 (two rows) and §9. Two further
sites assert the same thing:

- §2.4:300 — *"`GLError`, `GLErrorKind` … Driver errors as data, **attributable to one call** — the
  signal §G2.4's rungs 1–2 act on."* In the shipping configuration a batched record is attributable to
  a *sweep*, not to one call; that is exactly what `subjectLabel = "(batched)"` concedes.
- §12 item 22:2688 — the checklist item instructs the implementation session to implement *"the stated
  `glGetError` cadence behind `drainErrors()` (`[D-P1-30]`)"*. Whatever the fix-up decides the cadence
  should be, this item points at the old one by reference and will silently instruct the wrong build.

**Why raise it separately.** The brief's own warning — *"Round five found two places where a fix landed
in three sites and missed the fourth. Assume this fix-up did the same somewhere"* — applies to the fix
that has not yet been written. Round five's own site list is short by two, and a fix-up working from
that list will reproduce the pattern. Recording the complete list is cheap insurance: **§2.4:300,
§4.7.4:1449–1462, §5.2:2007, §6:2077 and §6:2078, §9:2238, §12 item 22:2688.**

### V6-4 — §3's conformance map omits the App F.7 rows the facade explicitly exists to serve · **note** · no §5

**Location:** §3's table (317–338); the omission is against §4.7.4:1421–1431.

**Claim under test:** §G9's requirement that §3 carry *"every in-scope contract item (RESEARCH.md
§3/App row) → the design element satisfying it → provenance tag. ZERO unmapped rows"*, under the
document's own narrowing at §3:313–315 to *"the ones the facade and the debug affordances must
satisfy."*

**Evidence.** §4.7.4 names App F.7 as the source of three verbs — *"alpha/blend also being per-program
state from App F.7"* and *"sub-viewport `scale.<prog>`"* under `viewport`. App F.7:1510–1512 declares
`alphaTest.<prog>`, `blend.<prog>` and `scale.<prog>` as contract keys. §3 has twenty rows and none of
them is any of these, while it does carry rows of exactly this shape for other facade verbs
(`centerDepthSmooth` → `readDepthPixel`, noise/custom textures → `TextureService.upload`, `atlasSize`
→ the `ivec2` overload, `blendFunc` → the `ivec4` overload). Whatever criterion admitted those admits
these. `countInstances` (App A.3:1159) is the fourth such row and is V5-2's subject.

Round three swept App F.7 and confirmed the *design elements* exist (`PHASE_1_REVIEW_3.md`:179), which
is why this is a note: nothing is missing from the design, only from the table that claims to enumerate
it. Three rows.

### V6-5 — `FramebufferService.blit` is the one facade verb with no consumer named anywhere · **note** · no §5

**Location:** §4.7.4:1335–1339; `BlitSpec` in §12 item 19 (2685).

**Evidence.** `blit` is fully specified — round four's F4-7 saw to that, adding the rectangles, the
attachment mask, the NEAREST enforcement for depth and the binding-restore contract. What no round has
asked is **who calls it**. The word "blit" appears **nowhere in `RESEARCH.md` or `DESIGN.md`**; §4.7.4's
own `copyDepthToTexture` comment says *"This — **not** blit — is the verb the contract's depth copies
need"*; the composite ping-pong is a draw, not a copy (§4.3:511–513); and `blit` is absent from §5.2's
pixel-transfer row and from every "consumed by" list. Meanwhile the facade's stated criterion for
*excluding* a verb is *"No consumer at any milestone in the current phase set"* — the reason given for
withholding general colour readback and texture readback. `blit` meets that criterion and is present
anyway, and `BlitSpec` is scheduled for implementation at v0.1 (§12 item 19).

**Why a note and not a correction, and why a fix-up may reasonably decline it.** Round four considered
`blit` deliberately and chose to specify rather than remove it; overturning that is not what this
finding asks. The ask is one sentence: either name the consumer (Phase 5, if the buffer estate wants a
general FBO copy) or move `blit` to the deferred table beside its siblings. Declining with a recorded
reason is a legitimate resolution.

### V6-6 — `enable_mixin_debug`'s "CI sets `false`" is unwired, and cannot affect anything CI runs · **note** · no §5

**Location:** §4.4.1's `enable_mixin_debug` row (855); §4.5.5:1060–1064; §12 item 32's hook (2708);
§4.11 (which never mentions it).

**Evidence.** §4.5.5 puts the two mixin dev flags inside `:mod`'s
`unimined.minecraft { cleanroom { runs.all { … } } }` block — i.e. they configure Unimined's **run
tasks** (`runClient`/`runServer`) and nothing else. CI runs `./gradlew build` and the module `test`
tasks; it never invokes a run task. So the stated rationale — *"set `false` in CI so build logs stay
readable"* — describes an effect the property cannot have, and §12 item 32's test hook *"CI sets the
property false"* is unverifiable because no workflow step, `-P` flag or environment variable in §4.11
sets it.

Two-line fix: drop the CI half of the rationale and the hook clause, or, if the intent is real, name
the mechanism (`-Penable_mixin_debug=false` on the CI gradle invocations) in §4.11.

---

## 5. What came back clean

Named explicitly, because a sixth pass owes the reader the negative space as much as the findings.

- **Everything three rounds already swept stayed swept.** The App D / App F sweep re-derived from
  source agreed with the document at every row (§2.5). The five rulings the brief listed as settled all
  held (§2.6).
- **The doc gate passes on all three criteria read literally**, as does §G9 template completeness —
  thirteen sections, four full spike specs with verbatim questions, D-1…D-10 all dispositioned.
- **The pins did not drift, and the re-pin procedure executes**, including the trap it warns about.
- **The seam is sound.** C-1…C-4, their three enforcement layers, the JPMS rejection, the fixtures'
  placement in `:engine`'s `testFixtures` and `:conformance`'s own `repositories` block all survive
  scrutiny against Gradle semantics. `[D-P1-27]`'s reasoning about per-`Project` repositories is
  correct, and so is its account of why the failure lands in `compileTestJava` rather than
  `compileJava`.
- **The facade's granularity decision, the opaque-handle rule, the handle-lifetime contract, the
  no-UBO and no-GL-constant properties, and the `mod.glue`-only LWJGL confinement** are all coherent,
  and I found no GL constant or raw object name in any signature.
- **Scope discipline holds in both directions**, and the licensing work (§4.8, OQ-12) is accurate
  about LGPL-2.1, LGPL-3.0 and the jar-in-jar arrangements.
- **Not one blocking finding, on a 2 745-line document at its sixth pass.**

**The honest ratio.** Thirteen items: **six corrections, seven notes, zero blocking**. Eight are round
five's, still outstanding only because no fix-up ran; six are new, and of those, three (V6-2, V6-3,
V6-4) are extensions or neighbours of defects an earlier round already identified rather than
independent discoveries. Two of the new ones (V6-5, V6-6) are one-sentence tidying that a fix-up may
decline with a recorded reason. **The document is converging; the cadence is not.** The reason this
round exists is not that round five missed things — it did not miss much — but that its output was
never applied.

---

## 6. Verdict

**PASS-WITH-CORRECTIONS**

FAIL is reserved for structural misses requiring a rebuild (§G1.2), and nothing here is structural.
The module split, the seam and its four constraints, the facade's granularity, the pin machinery, the
mixin wiring, the licensing work and the bail-registry mechanism are all sound and have now survived
six passes.

PASS was available in principle and I looked for it. It is not available in fact: four corrections
from round five are outstanding verbatim, and two of them (V5-1, V5-2) concern what a dependent would
build against. Saying PASS here would close the phase over an unapplied findings list, which is the one
failure mode worse than another round.

### Per-finding disposition

| Finding | Severity | Touches §5? | Consequence |
|---|---|---|---|
| V5-1 `glGetError` cadence vs. rung 2 | correction | **yes** — §5.2:2007's GL-error row states both the promise and the cadence | forces round seven |
| V5-2 `fullscreenQuadInstanced` | correction | **conditional** — no §5 *text* change if deleted; §5.2 gains a row if kept and specified | the fix-up's choice decides |
| V5-3 CI step preemption | correction | no | fix-up only |
| V5-4 `:mod` ASM exclusion | correction | no | fix-up only |
| V5-5 `[D-P1-24]` decision-log residue | note | no | fix-up only |
| V5-6 `RecordingGLDevice` cannot take a bounded log | note | marginal — a constructor overload does not change §5.2:2011's claim | fix-up only |
| V5-7 Phase 8 absent from the depth-copy consumers | note | **yes** — §5.2:2015's consumer list | rides with V5-1 |
| V5-8 configuration-time classpath resolution | note | no | fix-up only |
| **V6-1** stale cadence statements; no round-five resolutions | correction | no — §0 and the closing paragraph | fix-up only; closes when the fix-up records itself |
| **V6-2** §6 misassigns ladder rung 1; §2.4/§5.2 repeat it | correction | **yes, marginal** — §5.2:2007, the same row V5-1 opens | rides with V5-1 |
| **V6-3** V5-1's two unrecorded sites (§2.4:300, §12 item 22) | correction | no beyond V5-1's | rides with V5-1 |
| **V6-4** §3 omits the App F.7 rows | note | no | fix-up only |
| **V6-5** `blit` has no named consumer | note | no | fix-up only; declining with a reason is legitimate |
| **V6-6** `enable_mixin_debug`'s CI clause | note | no | fix-up only |

**Nine of the thirteen do not touch §5 at all.** The §5 surface is opened by exactly three things:
V5-1's GL-error row, V5-7's consumer list, and — only if the fix-up keeps the verb — V5-2. V6-2 and
V6-3 land inside the row V5-1 already opens and add no new §5 axis.

### §G1.3 line

**`PHASE_1_DOC.md` is NOT verified.**

§G1.3 makes a phase verified only when its latest verdict is PASS, or PASS-WITH-CORRECTIONS *"with all
resolutions recorded and no §5 change outstanding."* Neither condition is met. What specifically
remains outstanding:

1. **All eight of round five's findings**, unapplied and unrecorded. `PHASE_1_REVIEW_5.md` still has no
   `## Resolutions` section, which is itself an unmet §G1.3 obligation.
2. **The six findings above**, V6-1 … V6-6.
3. **A §5 change is unavoidable.** V5-1 forces an edit to §5.2's GL-error row and V5-7 forces one to
   §5.2's pixel-transfer consumer list, independent of every choice the fix-up gets to make. The
   *"re-verify only if §5 changed"* rule will therefore fire again, and a **seventh verify session**
   must run before Phase 2, Phase 3 or any other dependent consumes this document (§G5.3). Until then
   Phase 2 and Phase 3 cannot start.

I record that without softening it, and one thing alongside it, because the shape of the remaining
work matters more than the count. The seventh pass will not inherit an open document. It will inherit
§5 edits that are **fully specified in advance** — one row's promise narrowed to what the cheap
`glGetError` mode can actually deliver, one consumer list gaining Phase 8, and one verb either deleted
or given semantics — against a §5 whose contract fidelity two independent passes have now re-derived
from RESEARCH.md end to end and found correct at every row. The remaining nine items never touch §5
and close permanently with the fix-up that applies them.

Per §G1.2 this session stops here and fixes nothing.

---

## Resolutions

*Recorded by the fix-up session of 2026-07-25 (§G1.3). Nothing above this heading was modified. This
session applied V6-1 … V6-6 below **and** round five's V5-1 … V5-8, whose table this session wrote
into `PHASE_1_REVIEW_5.md` — the section this round correctly found missing (V6-1). Round six's §3
re-verification is what let both rounds be closed in one pass: every V5 finding was confirmed
outstanding at its cited line, so no re-derivation was owed.*

### V6-1 … V6-6

| Finding | Disposition | Where |
|---|---|---|
| **V6-1** stale cadence statements; round five's artifacts absent | **Applied, and this session is its closure.** §0.5's status paragraph is retitled "at the time" and closed with the pointer §0.4 already models ("no longer the document's current state — see §0.6"); a new **§0.6** records rounds five and six, the five design calls this fix-up made and why, the reads beyond the assigned list (`DESIGN.md` §G2.4 and — disclosed — Phase 11's *Scope — in*; RESEARCH.md §3.2, §3.5, §4.5, App A.3, App B.2, App D.4, App F.7), and the true §G1.3 status. The closing paragraph now reads **six** verify sessions and **three** fix-ups and names the seventh verify session as the next step. `PHASE_1_REVIEW_5.md` has its `## Resolutions` section. The date discrepancy (reviews stamped 2026-07-25, document header 2026-07-24) is recorded in §0.6 and left to round seven rather than silently restamped | §0.5, §0.6 (new), closing paragraph, and `PHASE_1_REVIEW_5.md` |
| **V6-2** §6 misassigns rung 1 | **Applied.** §6's rung-1 row is replaced by a row recording that rung 1 is **Phase 11's expression-evaluation isolation** at v0.4, above the facade and never reaching a GL call (`DESIGN.md` §G2.4 and Phase 11's *Scope — in*) — so §6 still maps the whole ladder; it maps rung 1 to its real owner. The capability-feature case is **kept as its own explicitly unnumbered row**, because it is a real failure mode the ladder does not number: rungs 1 and 2 are both about uniforms, and labelling it would be inventing a step. §2.4, §4.7.4's `drainErrors()` javadoc, `[D-P1-30]` and §5.2 are narrowed to "rung 2". The refutation this finding names is closed rather than left for round seven: a *custom* uniform whose **upload** fails is neither rung — it is served by the same drain and the same disable-one-uniform behaviour, and §6 now says so in one sentence | §2.4, §4.7.4, §5.2, §6 (two rows), §11.1 `[D-P1-30]` |
| **V6-3** V5-1's two unrecorded sites | **Applied, at the complete list.** All seven sites were edited in one pass — §2.4, §4.7.4's `[D-P1-30]` bullet and the `GLError` / `drainErrors()` javadocs, §5.2, §6's two rows, §9 and §12 item 22 — with §11.1's `[D-P1-30]` and the new `[D-P1-32]` making nine. §12 item 22 mattered most: it pointed at the old cadence *by reference* and would otherwise have instructed the wrong build silently | §2.4, §4.7.4, §5.2, §6, §9, §11.1, §12 item 22 |
| **V6-4** §3 omits the App F.7 rows | **Applied, four rows not three.** `alphaTest.<prog>` → `StateService.alphaTest`, `blend.<prog>` → `StateService.blend` + `snapshot()`/`restore()`, `scale.<prog>` → `StateService.viewport` (the sub-viewport §4.7.4's inclusion criterion already cites as its reason for existing), and — the fourth, which is V5-2's subject — `const int countInstances = N` → the caller-side loop over `fullscreenQuad()` with an `instanceId` upload, with the GLSL-120 reason stated in the row | §3 |
| **V6-5** `blit` has no named consumer | **Applied, narrowed.** The verb stays: round four specified it deliberately and this session is not overturning that on a note. What it lacked is now supplied — §4.7.4 names **Phase 5**, the one owner of framebuffer-to-framebuffer movement, states plainly that no *contract* item demands it today (the depth copies are `copyDepthToTexture` and the composite ping-pong is a draw), and records the condition for revisiting: if Phase 5's design closes without using it, `blit` moves to the absent-verbs table at the next fix-up rather than lingering as a permanent exception to the facade's own "no verb without a consumer" rule | §4.7.4 |
| **V6-6** `enable_mixin_debug`'s CI clause | **Applied by deletion.** The CI half of the rationale is removed from §4.5.5 and from §4.4.1's property row, and §12 item 32's hook drops "CI sets the property false" for one that can actually be run (`-Penable_mixin_debug=false` suppresses the `.mixin.out/` dump on a run task). §4.5.5 now states why: the flags configure Unimined's **run tasks**, which no CI step invokes. Wiring it instead was considered and rejected — passing the property in CI would still change nothing observable, which is the whole finding | §4.4.1, §4.5.5, §12 item 32 |

### §5 status, and the §G1.3 line

**The document is still NOT verified, and that is the expected outcome of this session, not a
failure of it.** §5 changed at four rows — §5.2's GL-error row (V5-1, V6-2, V6-3), its pixel-transfer
consumer list (V5-7), its recorder row (V5-6) and its non-verbs row (V5-2's deletion, mirrored for
consistency with §4.7.4's table). §G1.3's "re-verify only if §5 changed" rule fires, so a **seventh
verify session** must run before Phase 2, Phase 3 or any other dependent consumes `PHASE_1_DOC.md`
(§G5.3).

What the seventh pass inherits is what round six predicted it would: no outstanding findings, a
complete resolution record for all six rounds, **no facade signature added** — one verb removed, one
constructor overload and one static factory on a test-support class — and the two §5 edits this round
specified in advance, applied as specified.
