# PHASE_1_DOC.md — Verify session, round five

**Session type:** verify (`DESIGN.md` §G1.2) · **Document under review:** `Schmaloogium/PHASE_1_DOC.md`
**Date:** 2026-07-25 (pin re-verification performed 2026-07-25 ~01:15 UTC; the document is stamped
2026-07-24) · **Verdict:** **PASS-WITH-CORRECTIONS**

**Why this session exists.** The fix-up of 2026-07-24 applied rounds two, three and four together and
altered **§5** — the facade gained an `ivec4` upload, `depthTest`/`fog`, `copyDepthToTexture`,
`drainErrors()` and `noUseAfterDelete()`, and §5 gained rows for Phases 2, 3 and 7. §G1.3's
"re-verify only if §5 changed" rule therefore fires, and until this verdict exists the document is
not a valid dependency input (§G5.3).

**Where this pass aimed.** Roughly four hundred lines of the document were written by a *fix-up*
session, and fix-up sessions get no adversarial review of their own: the new facade verbs, decisions
`D-P1-27` … `D-P1-31`, §4.2.4a, and the three `## Resolutions` tables themselves. That material was
the primary target. The parts three rounds already swept were re-checked, not re-litigated.

**The honest ratio.** Eight findings — four corrections, four notes, **zero blocking** — against a
2 745-line document on its fifth pass. Six of the eight land on material introduced by the last
fix-up; two are under-applications of round four's own fixes. Everything round four ruled on held
when I re-checked it, and the contract fidelity of the new §5 surface is **correct** in every row I
tested against RESEARCH.md. This document is converging, and §4 below says so without dressing the
list up.

---

## 0. What I read, and in what order

Assigned reading, in the order §G1.2 prescribes:

1. `DESIGN.md` Part I in full (§G0–§G10, lines 1–574) and the Phase 1 spec in Part II (lines
   585–658). Other phases' entries in §G5.1 by title only, plus — see the disclosure below — the
   *Scope-in* bullets of the Phase 6 and Phase 8 specs.
2. `RESEARCH.md` §0 (reading guide, confidence tags) and §1 (mission, non-goals, D-1…D-10), then the
   spec's **Required inputs**: §5.1, §5.2, §5.3, §6.1, §7.2, §12.2.
3. Template ground truth, complete: `build.gradle`, `settings.gradle`, `gradle.properties`,
   `gradle/scripts/{dependencies,extra,publishing}.gradle`,
   `gradle/wrapper/gradle-wrapper.properties`, all eight files under `src/**`, all three
   `.github/workflows/*.yml`, `README.md`, `.gitignore`.
4. `PHASE_1_DOC.md`, in full.
5. **Last, only after my own findings were formed:** `PHASE_1_REVIEW.md`, `PHASE_1_REVIEW_2.md`,
   `PHASE_1_REVIEW_3.md`, `PHASE_1_REVIEW_4.md`, including their `## Resolutions` sections.

**Read beyond the assigned list, each because a finding turns on it** (§G1.1/§G1.2 recording
requirement):

- **RESEARCH.md §3.2** — the source-directive list, where `const int countInstances = N` is defined
  (V5-2).
- **RESEARCH.md §3.4, §3.5** — the uniform/sampler/attribute contract and the standard macro header,
  to test §3's `blendFunc` and macro-header rows.
- **RESEARCH.md §4.1–§4.5** — lifecycle (the probe set, the uninit that `[D-P1-28]` rests on),
  registry mechanics (the unit re-point behind `[D-P1-29]`), framebuffer architecture, per-frame flow
  (the composite/final state block behind `[D-P1-31]`), and the shadow pass (V5-7).
- **RESEARCH.md App A.2/A.3** — backup chains and the source-directive table, for `countInstances`
  and the ARB geometry form.
- **RESEARCH.md App B.1–B.5** — buffers, the depth/shadow copies, the 16-unit map.
- **RESEARCH.md App D.1–D.4** — the whole uniform inventory, to test the `ivec2`/`ivec4`/no-`ivec3`
  claims end to end rather than at the cited rows.
- **RESEARCH.md App F.1/F.5/F.6/F.7, App G** — engine flags, custom textures, the custom-uniform
  type list, per-program render state, the pack matrix.

**Deviation disclosed.** I read the *Scope-in* bullets of the **Phase 6** and **Phase 8** specs in
`DESIGN.md` Part II. §G1.1 line 78 bars a *build* session from other phases' specs; round four
established — and I adopt — that a verify session auditing an ownership or consumer claim has no such
bar. Phase 6's bullet is what makes `blendFunc` a v0.1 obligation; Phase 8's is what makes V5-7 a
real gap rather than a stylistic one.

**Hard rules observed.** No code, no builds, no tests, no fixes, no scope creep. `PHASE_1_DOC.md`,
`RESEARCH.md`, `DESIGN.md` and the four existing review files were not modified. **No adversarial
sub-agents were used** — §G1.2 permits them and I did not need them; every load-bearing quote in this
review was read against its source file by me directly. Live network was used **for the pin table
only**, which the Phase 1 spec orders ("daily cadence, so check again").

---

## 1. Verification performed, independent of the document's characterizations

### 1.1 Pins — live, and none has drifted

The brief flagged drift as a live possibility: the pins are stamped 2026-07-24 and I checked them on
2026-07-25, one full day into a platform with a daily release cadence. Drift was the expected result.
It did not happen.

| Row | Doc value | Live observation | Verdict |
|---|---|---|---|
| Cleanroom loader | `0.6.6-alpha` | `repo.cleanroommc.com/…/cleanroom/maven-metadata.xml`: `<release>` and `<latest>` both `0.6.6-alpha`, `<lastUpdated>20260724133703</lastUpdated>`. GitHub releases API agrees: `0.6.6-alpha` published 2026-07-24T13:37:05Z, `0.6.5-alpha` 2026-07-24T01:30:51Z | **holds** |
| Unimined (kappa fork) | `1.4.26-kappa` | `maven.arcseekers.com/…/xyz.wagyourtail.unimined.gradle.plugin/maven-metadata.xml`: `<release>` = `1.4.26-kappa`, preceded by `1.4.22`…`1.4.25-kappa` | **holds** |
| ASM (test-only) | `9.10.1` | Google's Central mirror: `<release>` = `9.10.1`, preceded by `9.10`, `9.9.1`, `9.9` | **holds** |
| Gradle · Java · mappings · sponge-mixin · Blossom · Shadow · idea-ext · foojay · JUnit | template values | unchanged in the checkout; §4.1's transcription re-checked line by line against the files | **holds** |

Two things the document says about the loader row are worth confirming rather than assuming, because
both are checkable and both are true: the two-releases-in-one-day claim (`0.6.5` at 01:30Z and
`0.6.6` at 13:37Z on 2026-07-24) is exactly what the API shows, and the maven `<lastUpdated>` stamp
agrees with the GitHub publish time to within two seconds — so §4.2.6 step 2's "a tag that appears in
one and not the other is itself a finding" cross-check currently passes.

The re-pin procedure was executed literally as written, and it now works: every row carries the
coordinate its value is verified against (round four's F4-13 fix), and step 3's three rulings gave me
an unambiguous answer. **Record only** — no release note in the window describes a behavioral change
to anything this document pins, asserts, or tests.

### 1.2 Template ground truth (§4.1) — re-checked, no misstatement

Every one of §4.1's seventeen rows was checked against the file it cites. All are accurate,
including the ones that are easy to get subtly wrong: the branch really is `main` and carries no
mixin config, no `MixinConfigs` attribute and no `mixin { }` block; `settings.gradle` really has no
`include` lines; the AT wiring really hardcodes `${rootProject.projectDir}`; `enable_lwjglx` really
has exactly one effect (a `compileOnly` coordinate in `dependencies.gradle`, guarded); `jar` really
is `finalizedBy(remapJar)` so a merge into `jar` precedes remap; and all three workflows really
hardcode root-relative `build/libs`. §11.3's four template defects are all real — I reproduced the
`extra_jvm_args` one by reading `build.gradle:65` (`extraArgs.split { "\\s+" }` is Groovy's
`CharSequence.split(Closure)`, a partition, not a regex split) and the `extra.gradle` one by
confirming that `ext` carries only `access_transformer_locations`.

### 1.3 Doc gate — **PASS**, all three criteria, read literally

| Criterion (`DESIGN.md`:650–651) | Verdict |
|---|---|
| "module/package layout finalized with dependency rules as testable constraints" | **met** — §2.1's package table is complete and assigns a filling phase to every package; C-1…C-4 in §4.3 are mechanical, each with a named test in §8.1 and a checklist item in §12 |
| "every D-1..D-10 either satisfied by this phase or explicitly deferred with its owner phase named" | **met** — §11.2 disposes all ten; every deferral names an owner (D-3→P2, D-4→P4, D-5 catalog→P7, D-9 policy→P5+/P7, D-10→P2) |
| "pin table complete with re-verification procedure" | **met** — fourteen rows, each with location, repository coordinate and re-verification date; §4.2.6's seven-step procedure and §10.1's §G4.4 restatement agree with each other |

### 1.4 Template completeness, OQ spikes, scope, binding decisions

- **All thirteen §G9 sections** (0–12) are present and substantive. §12's `14b`/`36b`/`41b` lettering
  is deliberate and explained; it does not break the numbering the Impl gate depends on.
- **All four assigned OQs** carry a full §G4.4 spike: OQ-2 (§10.1), OQ-12 (§10.2), OQ-20 (§10.3),
  OQ-21 (§10.4). Each has the verbatim question, a concrete procedure, success *and* failure criteria,
  and a fallback designed now. I checked the four verbatim quotations against RESEARCH.md §11 — all
  four are faithful.
- **Scope discipline** holds in both directions. Nothing from *Scope — in* is dropped: template
  conversion, license swap, pins, module split, facade, mixin wiring, lwjglx posture, JUnit baseline,
  logging/debug/error channels, bail-registry mechanism, CI, and the OQ-12 note all have sections.
  Nothing from *Scope — out* is designed here: no harness content, no pack format, no GL policy
  beyond the facade shape, no GUI framework evaluation. §1.2's twelve-row table names an owner for
  every adjacent concern.
- **Binding decisions.** No D-1…D-10 is contradicted. No contract-visible component is "improved" —
  with the single exception V5-2 raises, which is a *risk* of improvement rather than one committed.

### 1.5 Contract fidelity of the new §5 surface — re-checked against source, and correct

The fix-up's new material rests on a handful of RESEARCH.md claims. I read each at its source rather
than trusting the citation, because the depthtex1-unit-11 class of error is what §G1.2 exists to
catch. All of them hold:

| Claim the doc makes | Source | Verdict |
|---|---|---|
| `blendFunc` is `ivec4` (`srcRGB`, `dstRGB`, `srcA`, `dstA`) | App D.4:1376 | ✅ verbatim |
| `atlasSize` is `ivec2` | App D.3:1366 | ✅ |
| `eyeBrightness`/`eyeBrightnessSmooth` are `ivec2`, in App **D.1** | App D.1:1331–1332 | ✅ — round three's F3-8 citation fix landed at all five sites; I checked §0.1, §3, both §4.7.4 sites and `[D-P1-25]` |
| No `ivec3` or `mat3` has a contract consumer | App D.1–D.4 end to end; App F.6:1492 (`float\|int\|bool\|vec2\|vec3\|vec4`) | ✅ — the sweep is genuinely exhaustive, not a spot check |
| The composite/final block is "identity ortho, fog/depth/blend disabled" | §4.4:563 | ✅ verbatim — this is what `depthTest` and `fog` were added for, and `depthMask` really is a different bit of state |
| `depthtex1`/`depthtex2` are copy-target **textures**, not attachments | §4.3:514–516; App B.2:1221–1222 | ✅ — `copyDepthToTexture` is the right verb and `blit` genuinely is not |
| The fixed unit map re-points up to 16 units per program switch | App B.3 (units 0–15); §4.2:504 | ✅ — `[D-P1-29]`'s "highest-frequency call" framing is justified, not rhetorical |
| `final` renders to the vanilla framebuffer with anaglyph-aware colour masking | §4.3:526 | ✅ — the deferred colour-mask row is correctly reasoned |
| `backFace.*` are engine flags in App F.1 | App F.1:1443 | ✅ |

### 1.6 Round four's rulings the brief listed as not-to-be-re-raised

I re-checked all five and **all five hold**. The bypass-rule reading of §4.7.4's `GlStateManager`
sentence is correct as written; `backFace.*` ownership does belong to Phase 3's mandated engine-flag
map and naming an owner here would be scope creep; the ARB translation *strategy* is Phase 4's per
`DESIGN.md`:848 while §3's `[V:doc]` tag qualifies the pack-side contract item; §4.2.5's "the
Impl-gate item **that depends on this merge**" is restrictive and item 7 does carry the merge
instruction; and §12 item 30's `runClient` hook does already cover the nested-mixin-package check.
None is re-raised.

---

## 2. Audit of the three `## Resolutions` tables

A new artifact nobody has reviewed. I checked every row that claims "applied" against the current
document, and every row that claims "not applied" against its stated reason.

**Round one (`PHASE_1_REVIEW.md`).** All twelve applied, none declined. Round two already audited
these and I did not repeat its work wholesale; I re-confirmed the two with downstream consequences —
F-2's fixtures really are in `:engine`'s `testFixtures` with `java-test-fixtures` applied in §4.2.3,
and F-12's caveat really is on the *expression* rather than the decision.

**Round two (`PHASE_1_REVIEW_2.md`).** Twelve rows, each pointing at the disposition round four
actually applied rather than restating round two's argument — which is the right form, since round
three re-raised round two's list almost verbatim. V2-1 … V2-7 all verified applied. **V2-8(b) is
correctly declined**: the reason given (repointing §4.2.5 at item 15 would point at an item carrying
no merge instruction) is sound, and I confirmed item 15 carries none. V2-9 and V2-10 correctly
resolve to "no change required".

**Round three (`PHASE_1_REVIEW_3.md`).** Twelve rows. The four deliberate non-applications are each
sound, and I want to record that explicitly because "the fix-up talked itself out of a real defect"
was one of the things this pass was sent to look for:

- **F3-3's headline** was not applied because it rested on a four-word misquotation. Correct — I read
  the sentence and it is a bypass rule.
- **F3-3's sub-claim 3** (name `backFace.*`'s owner) was not applied because `DESIGN.md` makes the
  engine-flag map a Phase 3 deliverable. Correct, and the deferred row that replaced it is the right
  instrument.
- **F3-4's proposed §5 row** was not applied because it would have obligated Phase 3 to the
  translation *strategy*, which is Phase 4's. Correct — and the real gap was closed under F4-4
  instead, which I verified is present at §5.2 and phrased as contestable.
- **F3-10 leg (b)** was refuted for the same reason as V2-8(b). Correct.

**Round four (`PHASE_1_REVIEW_4.md`).** Twenty-one numbered rows plus two ride-alongs. I verified
each in the document. Nineteen are fully applied; the two ride-alongs (`validateFails`,
`drawBuffers`' zero-length "none") are applied even though unnumbered, which is good practice. **Two
are under-applied**, and they become findings V5-5 and V5-6 below:

- **F4-10** was applied at §4.11, §6 and §12 item 38 — but the finding's own Location line named
  `[D-P1-24]` in the decision log, and §11.1's row was not updated. The resolution row's "where"
  column lists three sites and does not claim the fourth, so this is a silent under-scope rather than
  a false claim.
- **F4-11** gave `GLCallLog` a `bounded(capacity)` factory and gave §4.9.3/§7/§12 item 24 a default
  capacity — but left `RecordingGLDevice` with no way to be handed one.

Everything else checks out, including the three fixes round four insisted be *widened*: F3-8 really
is applied at five sites, F3-5's `:mod` addition really does carry the ASM note (whose mechanism is
V5-4's subject), and F4-3 really does close F3-3's sentence and the `bindToUnit` row together.

---

## 3. Findings

### V5-1 — The `glGetError` contract defeats the degradation rung it was added to serve, and its stated justification is wrong about GL · **correction** · **§5**

**Location:** §4.7.4 (1449–1462, the `[D-P1-30]` bullet); §5.2's GL-error row (2007); §6's rung-2 row
(2077) and rung-1 row (2078); §9 (2238).

**Claim under test:** that `GLDevice.drainErrors()` gives Phase 6 a signal sufficient to implement
§G2.4 rung 2 — "a built-in uniform whose GL upload errors disables *that uniform* only".

**Evidence.** §4.7.4 states the backend's cadence as part of the contract: `Lwjgl3GLDevice` calls
`glGetError` after every facade call "when a debug context is active or any `-Dschmaloogium.debug.*`
flag is set, **and once per `drainErrors()` otherwise** — so the expensive mode is opt-in and the
cheap mode still cannot lose an error, only its precise attribution (records made in the cheap mode
carry `subjectLabel = "(batched)"`)".

Two things are wrong with that sentence, and the second is the serious one.

1. **"cannot lose an error" is false as a matter of GL semantics.** When an error occurs the flag is
   set to that error code and **no other error is recorded** until `glGetError` is called and the
   flag is reset. Draining once per sweep therefore does not merely blur attribution — the first
   failing upload in a program's uniform set suppresses every subsequent error until the next drain.
   A sweep in which five uniforms fail yields at most one record.
2. **In the default configuration rung 2 is not implementable.** The default configuration is no
   debug context and no debug flag — that is the shipping configuration. There, every record carries
   `subjectLabel = "(batched)"`, so Phase 6 cannot name a uniform and cannot disable one. Yet §5.2's
   row tells Phase 6, without qualification, to "upload a program's uniform set, drain once, disable
   *the uniforms named in the returned records*"; §6's rung-2 row states flatly that "the backend
   records a `GLError` **naming the uniform**"; and §9 tags the whole surface `v0.1` on the reasoning
   that "§G2.4 rung 2 is Phase 6's v0.1 scope-in, so the signal it acts on cannot be later than
   v0.1". The signal exists at v0.1; the attribution rung 2 needs does not, unless a developer flag
   is on.

This is new material — `[D-P1-30]`, written by the fix-up applying F4-1 — and the per-drain fallback
was introduced by that fix-up, not by round four's finding. Nobody has reviewed it.

**What would refute this finding:** a demonstration that the cheap mode can attribute an error to one
uniform, or a decision that rung 2 is a debug-mode-only behavior — in which case §5.2, §6 and §9 must
say so, since a dependent reading any of the three today would build the wrong thing.

**Why it is a correction, not blocking:** the facade shape is right and the consumer is right; what
is wrong is a factual claim and an unqualified promise. Both are prose fixes, plus a decision about
what Phase 6 does when attribution is absent (the natural candidate — drain per upload for the
uniform sweep specifically, or state the whole-set fallback — is a design call for the fix-up, not
for me).

### V5-2 — `DrawService.fullscreenQuadInstanced(int)` cannot express the contract behaviour it appears to serve, and nothing in the document states what it does · **correction** · **§5 in substance**

**Location:** §4.7.4 (1396). That is the verb's **only** occurrence in the document.

**Claim under test:** that the facade's instancing verb is a faithful mapping of the pack contract's
`countInstances`.

**Evidence.** RESEARCH.md §3.2:254–255 defines the directive: "`const int countInstances = N`
re-renders geometry N times with `instanceId` incrementing." App A.3:1159 says the same — "instanced
re-render with `instanceId` uniform". And App D.4:1377 declares `instanceId` as an `int` **uniform**,
value "0 original, 1..N instanced copies".

A single `fullscreenQuadInstanced(N)` call cannot change a uniform between copies. Nor can the
backend do it on the caller's behalf: it is not given the `UniformLocation`, and `UniformService` is
a separate service. The GL-instancing reading is unavailable for a second, independent reason —
1.12.2-era packs are GLSL-120 (RESEARCH.md §3.5), and GLSL 120 has no `gl_InstanceID`, which is why
the contract carries a uniform at all. The faithful shape is a caller-side loop of `fullscreenQuad()`
with an `upload(instanceIdLoc, i)` between iterations, which the facade already supports without this
verb.

The verb is also orphaned in the document's own terms. It has no §3 conformance row, no §5.2 mention,
no §9 milestone tag, no §12 checklist item, and no stated semantics — in a section that closes with
"Additions to this facade are expected and cheap; **silent** additions are not", and whose sibling
verb `fullscreenQuad()` carries three sentences of specification.

**This overturns a prior round.** `PHASE_1_REVIEW_3.md`:209 asserted the mapping "`countInstances` →
`fullscreenQuadInstanced(int)` for composites" inside a sweep it then marked clean. The mapping was
asserted, not tested. Round four's conformance-map audit returned **PARTIAL** and looked elsewhere.

**What would refute this finding:** a showing that `instanceId` can be driven per-copy through a
single instanced draw on a compat-profile GLSL-120 program. I do not believe one exists.

**Disposition options for the fix-up** (its call, not mine): delete the verb — the loop is a caller
concern and the facade loses nothing — or keep it and specify it, in which case §4.7.4 must say what
it means for `instanceId` and §5.2 must expose it with a consumer, and a §3 row is owed either way.
Deleting requires no §5 *text* change; keeping it does.

### V5-3 — The named CI seam steps are preempted by `./gradlew build`, so §12 item 38's hook cannot pass as specified · **correction** · no §5

**Location:** §4.11 (1934–1949); §6's build-time row (2085); §12 item 38 (2720); `[D-P1-24]`
(1942–1945).

**Claim under test:** that a violation of any of C-1…C-4 "turns a *named* step red, none of them
surfacing only inside `build`" (§12 item 38's hook, verbatim).

**Evidence.** §4.11 keeps `./gradlew build` ("unchanged — it aggregates across modules") and adds two
named steps: "Seam architecture test" running `./gradlew :engine:test :mod:test`, and
`./gradlew :conformance:test`. But `build` → `check` → `test` in every subproject, so `./gradlew
build` from the root **already runs all four seam tests**. If it runs first, a seam violation fails
that step, GitHub Actions aborts the job, and the named steps never execute — the exact outcome
`[D-P1-24]` exists to prevent. If the named steps run first the claim holds. The document never says
which, and the natural reading of a bullet list that opens with "`./gradlew build` unchanged" and
then says "**New**, explicitly-named step" is that the named steps come after.

The document contains its own refutation of the premise. §4.11's rationale — the step is named
"rather than being folded into `build`" — assumes `build` does not run these tests; §4.2.4a argues at
length that "**`./gradlew build` — §12 item 15, the Impl gate — fails** at
`:conformance:compileTestJava`", which is reachable only through `test`. Both cannot be true.

**Fix shape** (for the fix-up): state the ordering — named seam steps **before** `./gradlew build` —
or scope the build step (`-x test`), or make the named steps `if: always()`. Any of the three
discharges item 38's hook; none changes §5.

*Distinct from round four's F4-10, which was about which tasks the step names and was correctly
applied. This is about whether the step ever runs.*

### V5-4 — `:mod`'s ASM remedy misdescribes its own mechanism and guards only half the classpath · **correction** · no §5

**Location:** §4.2.4 (552–559) and its comment; §4.2.6's ASM row (691, "the pin must **win the
conflict**"); `[D-P1-3]` (2465).

**Claim under test:** that forcing `org.ow2.asm:asm:9.10.1` on `:mod` prevents Unimined's inherited
`asm-debug-all` 5.x from breaking C-2/C-3 on Java 25 class files.

**Evidence.** Two defects, one cosmetic and one with teeth.

1. **`resolutionStrategy.force` cannot do what the document says it does.** Gradle resolves version
   conflicts per *module identity* (`group:name`). `org.ow2.asm:asm` and `org.ow2.asm:asm-debug-all`
   are **different modules**, so there is no conflict between them for Gradle to arbitrate and the
   `force` line is inert against the legacy jar. The only line doing the work is
   `exclude group: 'org.ow2.asm', module: 'asm-debug-all'`. The comment ("Force ours"), §4.2.6's row
   ("the pin must **win the conflict**") and `[D-P1-3]`'s rationale all describe a mechanism that does
   not exist. `asm-debug-all` is a shaded fat jar carrying `org.objectweb.asm` at 5.x — a
   split-package collision, not a version conflict, and exclusion is the correct instrument.
2. **The block is applied to `testRuntimeClasspath` only.** C-2 and C-3 are *compiled* against
   `testCompileClasspath`, a separate configuration. If `asm-debug-all` reaches it by exactly the
   inheritance the document's own comment asserts (`testImplementation` extends `implementation`,
   which is where Unimined's dev dependencies land), then the ASM-5 `org.objectweb.asm` classes sit
   unguarded on the compile classpath and javac binds to whichever jar it encounters first. That is
   order-dependent, not deterministically broken — but it is unguarded, and ASM 5 lacks the API these
   tests need. Declaring the exclusion at `testImplementation` covers compile and runtime in one
   line.

`:engine` and `:conformance` are unaffected — neither applies Unimined — which matches the document's
own scoping of the problem to `:mod`.

**Why this matters beyond tidiness:** this is the mechanism protecting the Impl gate's test from
failing with, in the document's own words, "an unhelpful 'Unsupported class file major version'
rather than a seam message".

### V5-5 — `[D-P1-24]`'s decision-log row still describes the state round four called a defect · **note** · no §5

**Location:** §11.1, the `D-P1-24` row (2486).

**Evidence.** The row reads: "CI runs `:engine:test` as its own named 'Seam architecture test'
step". Round four's F4-10 established that a step running only `:engine:test` leaves C-2 and C-3
surfacing anonymously, and the fix-up corrected §4.11 (1942–1945, which now says "**Both** module
tasks are named deliberately"), §6 (2085) and §12 item 38 (2720). F4-10's Location line named
`[D-P1-24]` among its sites; the decision log was missed, and the resolution row's "where" column
lists only the three sites that were changed.

The decision log is the table a later reader consults for *why*, so it is the worst place for the
pre-fix statement to survive. One-cell fix.

*This is the regression-residue pattern the brief anticipated: a fix closed three instances and left
a neighbour standing.*

### V5-6 — `RecordingGLDevice` offers no way to supply or size the bounded `GLCallLog` its own decorator requires · **note** · §5 marginal

**Location:** §4.7.5 (1527–1544); §4.9.3's `recordGL` row (1799); §7 (2123–2129); §12 item 24 (2690);
§5.2's recorder row (2011).

**Evidence.** Round four's F4-11 correctly established that the recorder ships and needs a bound. The
fix added `GLCallLog.bounded(int capacity)` and `droppedCallCount()`, and four places now promise a
default capacity of 100 000 for the live decorator. But `RecordingGLDevice`'s only published
constructor is `RecordingGLDevice(GLCapabilityProfile profile, ScriptedResponses responses)`, and
`log()` is a getter. No consumer — not Phase 1's own `recordGL` decorator, not Phase 2, to whom §5.2
exposes the bound as part of the contract — can hand the device a log or choose its capacity.
§4.7.5's "tests use an effectively-unbounded capacity" is unreachable by the same omission.

The fix is one constructor overload. It is a note rather than a correction because the intent is
unambiguous and no dependent is misled about behaviour — only about how to obtain it.

### V5-7 — §5's pixel-transfer row omits Phase 8, whose shadow pass performs the third depth copy · **note** · **§5**

**Location:** §5.2's pixel-transfer row (2015); §9's staging row (2237); §4.7.4's `copyDepthToTexture`
comment (1341–1346).

**Evidence.** `copyDepthToTexture` was added for the depth copies. RESEARCH.md §4.5:577 records that
the shadow pass "copies depth→shadowtex1 (water-shadow split)", and App B.2:1224 lists `shadowtex1`
as a copy in the same table as `depthtex1`/`depthtex2`. That is a third instance of exactly the
operation, owned by **Phase 8** at v0.2. §5.2's row names consumers **6**, **5**, **13** and **9**;
§9's row names Phase 6, Phase 5 and Phase 13. Phase 8 appears nowhere in either, and is covered only
by §5.2's generic first row ("`GLDevice` + the seven services … 4, 5, 6, 7, 8, 13, 14").

The verb is *sufficient* for Phase 8 — this is a completeness gap in the consumer list, not a design
gap — but §5 is written to be "sufficient on its own", and a Phase 8 session reading the row that
enumerates depth-copy consumers would not find itself there.

### V5-8 — Three test blocks resolve a dependency configuration at configuration time, the hazard §4.2.5 already knows how to describe · **note** · no §5

**Location:** §4.2.3 (489–496), §4.2.4 (563–570), §4.2.4a (619–626).

**Evidence.** All three modules inject the seam tests' classpaths with
`systemProperty 'schmaloogium.test.compileClasspath', sourceSets.main.compileClasspath.asPath`. The
`.asPath` call resolves the configuration when the block runs — task realization, i.e. configuration
time. This is the same class of hazard §4.2.5 flags for the jar merge ("reaches across projects into
another project's model at configuration time — the pattern Gradle's configuration cache and
project-isolation work flag"), and it is sharper in `:mod`, whose `compileClasspath` contains
Unimined's task-produced remapped Minecraft artifact: resolving it during configuration means
resolving an artifact whose producing task has not run.

Recorded as a note, not a correction: I have not proven a failure, and the lazy fix is routine (a
`Provider`, or `jvmArgumentProviders`). It is here because the document flags the identical pattern
one section earlier and does not flag this one, and because the Impl gate runs through these blocks.

---

## 4. Verdict

**PASS-WITH-CORRECTIONS**

Reserve FAIL for structural misses requiring a rebuild (§G1.2). Nothing here is structural. The
module split, the seam, the facade's granularity decision, the licensing work, the pin machinery and
the mixin wiring are all sound, and the four rounds of prior findings are genuinely closed — I
audited every resolution row and found two under-applications out of forty-five, both cosmetic in
consequence.

The four corrections are: one wrong factual claim about GL that invalidates an unqualified promise to
Phase 6 (V5-1); one facade verb that cannot express the contract behaviour it names and says nothing
about what it does instead (V5-2); one CI arrangement whose stated property is unachievable in the
order it is written (V5-3); and one Gradle remedy that describes a mechanism it does not use and
guards one of the two classpaths that need guarding (V5-4). Each is a bounded edit. None requires
redesigning anything.

**What I want on the record about the ratio.** Six of the eight findings land on material the last
fix-up wrote, which is exactly what the fifth pass was for and is not a criticism of that session —
unreviewed material is where defects are, by construction. Two are under-applications of round four's
own fixes. **Nothing in the parts three rounds already swept came back**, and I looked: the contract
sweep in §1.5 re-derived the `ivec2`/`ivec4`/no-`ivec3` conclusions from App D and App F end to end
rather than from the citations, and it agreed with the document at every row. The five rulings the
brief listed as settled were re-checked and all five held. The pins, which had a full day and a daily
cadence to drift, did not.

A fifth pass that manufactured findings to look productive would be worse than useless. This one
found four things worth fixing and says plainly that the rest is clean.

### Per-finding disposition

| Finding | Severity | Touches §5? | Consequence |
|---|---|---|---|
| V5-1 `glGetError` cadence vs. rung 2 | correction | **yes** — §5.2's GL-error row states the promise and the cadence | forces a sixth verify pass |
| V5-2 `fullscreenQuadInstanced` | correction | **yes if kept and specified**; no §5 *text* change if deleted | the fix-up's choice decides |
| V5-3 CI step preemption | correction | no | fix-up only |
| V5-4 `:mod` ASM exclusion | correction | no | fix-up only |
| V5-5 `[D-P1-24]` decision-log residue | note | no | fix-up only |
| V5-6 `RecordingGLDevice` cannot take a bounded log | note | marginal — §5.2 exposes the bound; a constructor overload does not change the row's claim | fix-up only |
| V5-7 Phase 8 absent from the pixel-transfer consumers | note | **yes** — the row's consumer list | rides along with V5-1 |
| V5-8 configuration-time classpath resolution | note | no | fix-up only |

### §G1.3 line

**`PHASE_1_DOC.md` is NOT verified.** The verdict is PASS-WITH-CORRECTIONS, and §G1.3 makes a phase
verified only when "all resolutions [are] recorded and no §5 change [is] outstanding". Corrections
are outstanding, and **V5-1 forces an edit to §5.2's GL-error row** — with V5-7 and, depending on the
fix-up's choice, V5-2 touching §5 as well. The "re-verify only if §5 changed" rule will therefore
fire again: after the fix-up applies these findings and records its resolutions under a
`## Resolutions` heading in this file, a **sixth verify session** must run before Phase 2, Phase 3 or
any other dependent consumes this document (§G5.3).

I record that without softening it. It is also worth stating what the sixth pass will inherit, since
the cadence is converging rather than looping: a §5 whose contract fidelity has now been checked
against RESEARCH.md end to end, a resolutions record audited row by row, and a findings list of four
bounded prose-and-signature edits — three of which do not touch §5 at all.

Per §G1.2 this session stops here and fixes nothing.

---

## Resolutions

*Recorded by the fix-up session of 2026-07-25 (§G1.3). Nothing above this heading was modified.
**This round's fix-up did not run when it should have** — the omission is round six's V6-1 — so this
session applied V5-1 … V5-8 together with `PHASE_1_REVIEW_6.md`'s V6-1 … V6-6, whose table lives in
that file. Where round six extended one of this round's findings (V6-3 adds two sites to V5-1) or
shares a cell with it (V6-2 rewrites the same §5.2 row), the row below says so.*

### V5-1 … V5-8

| Finding | Disposition | Where |
|---|---|---|
| **V5-1** `glGetError` cadence defeats rung 2 | **Applied, narrowed.** Both halves of the finding are conceded. The false claim is deleted: GL sets the flag to the *first* error and records no further error until the flag is cleared, so a batched sweep yields **at most one** record — the honest invariant is "a drain cannot lose the fact that the window failed". Rung 2 was **not** made debug-mode-only; instead attribution is defined as a property of the caller's **drain window**, and the rung-2 protocol is stated as contract: drain → upload the set → drain, and only on a non-empty drain, re-upload draining between uploads so each window holds one call and each record names one uniform. `[D-P1-32]` records why the debug-only alternative was rejected (`DESIGN.md` puts per-uniform isolation in Phase 6's v0.1 scope-in, so the only shipping fallback would be a hammer larger than rung 3) and why the replay does not contradict §7 (the clean sweep still costs one query; the replay is entered only on a frame already about to disable a uniform). **No signature changed** | §2.4, §4.7.4 (`[D-P1-30]` bullet, the new `[D-P1-32]` passage, the `GLError` javadoc, `drainErrors()`'s javadoc), §5.2, §6 (rung-2 row), §7, §9, §11.1, §12 item 22 |
| **V5-2** `fullscreenQuadInstanced(int)` | **Applied by deletion.** `instanceId` is an `int` uniform (App D.4) and GLSL 120 has no `gl_InstanceID` (§3.5), so the verb could not express `countInstances`; the caller-side loop over `fullscreenQuad()` with an `instanceId` upload between copies is the faithful shape and the facade already supports it. The deletion is **not** silent: §4.7.4's absent-verbs table gains a row with the reason and the phase that would request an instanced verb, §3 gains the `countInstances` row mapping the loop, §5.2's non-verbs row is updated, and §12 item 19's hook names the surviving single verb. `[D-P1-33]`. *One correction to this round's framing:* deleting is §5-free only if left silent — §5.2's non-verbs row enumerates §4.7.4's table, so mirroring the new row into it **is** a §5 text change, and it was made deliberately rather than accepting §4.7.4/§5.2 drift | §3, §4.7.4, §5.2, §11.1, §12 item 19 |
| **V5-3** named CI steps preempted by `build` | **Applied — the ordering, not the alternatives.** §4.11's bullets become an explicit ordered sequence with the two named seam steps **before** `./gradlew build`, and the premise both sections talked around is now stated outright: `build` → `check` → `test` runs all four seam tests itself, which is exactly why the order is load-bearing. `-x test` was rejected because it would also drop `:conformance:compileTestJava` and disarm §4.2.4a's account of the Impl gate — §4.2.4a now carries a sentence saying so, which is what makes the two claims consistent. `if: always()` was rejected because it leaves `build` as the first red step | §4.2.4a, §4.11, §6 (build-time row), §11.1 `[D-P1-24]`, §12 item 38 |
| **V5-4** `:mod` ASM remedy | **Applied, both defects.** `resolutionStrategy.force` is **removed**: `asm` and `asm-debug-all` are different `group:name` modules, so there was no conflict to arbitrate and the line was inert — the block's comment, §4.2.6's row and `[D-P1-3]` all described a mechanism that does not exist, and now describe the real one (a shaded fat jar carrying `org.objectweb.asm`, i.e. a split package, removable only by exclusion). The exclusion moves from `configurations.testRuntimeClasspath` to `configurations.testImplementation`, which both resolvable test classpaths extend, so C-2/C-3's **compile** classpath is guarded too. §12 item 7 — a fourth site this round's list did not name — carries the corrected wording and a `dependencyInsight` hook over **both** configurations | §4.2.4, §4.2.6, §11.1 `[D-P1-3]`, §12 item 7 |
| **V5-5** `[D-P1-24]` decision-log residue | **Applied.** The row now reads `:engine:test` **and** `:mod:test` as one named step plus `:conformance:test` as a second and — since V5-3 lands in the same cell — that both run before `./gradlew build`, with the reason. Provenance tags for F4-10, V5-5 and V5-3 are all recorded on the row | §11.1 `[D-P1-24]` |
| **V5-6** `RecordingGLDevice` cannot be given a log | **Applied.** `RecordingGLDevice(GLCapabilityProfile, ScriptedResponses, GLCallLog)` added beside the existing two-arg constructor, and `GLCallLog.unbounded()` added as `bounded(int)`'s counterpart so §4.7.5's "tests use an effectively-unbounded capacity" becomes reachable. §4.9.3, §5.2, §7 and §12 item 24 now say the decorator constructs the ring and hands it to the device; §8.1's `RecordingGLDeviceTest` row asserts it | §4.7.5, §4.9.3, §5.2, §7, §8.1, §12 item 24 |
| **V5-7** Phase 8 absent from the depth-copy consumers | **Applied.** §5.2's pixel-transfer row and §9's staging row both name **8** for the shadow pass's depth→`shadowtex1` copy at v0.2 (RESEARCH.md §4.5, App B.2). No verb changed — the gap was in the consumer enumeration, and §5 is written to be sufficient on its own | §5.2, §9 |
| **V5-8** configuration-time classpath resolution | **Applied, narrowed.** The lazy form — a `CommandLineArgumentProvider` on `jvmArgumentProviders`, with annotated inputs so the test task's up-to-date checking stays honest — is written out once in §4.2.3 with the reasoning, and §4.2.4/§4.2.4a use the same form and point back to it. §4.2.4 adds that `:mod` is where the hazard bites hardest, its `compileClasspath` holding Unimined's task-produced remapped Minecraft artifact. **Narrowed** in that the checklist is left alone: this is a note, prose is where a note belongs, and where the provider class lives is the implementation session's call | §4.2.3, §4.2.4, §4.2.4a |

### §5 status, and the §G1.3 line

**§5 changed**, at four rows: §5.2's GL-error row (V5-1, with V6-2 and V6-3 riding in the same cell),
its pixel-transfer consumer list (V5-7), its recorder row (V5-6) and its non-verbs row (V5-2's
deletion, mirrored). §G1.3's "re-verify only if §5 changed" rule therefore fires: a **seventh verify
session** must run before Phase 2, Phase 3 or any other dependent consumes `PHASE_1_DOC.md` (§G5.3).

Worth stating for that session, because it is what makes the pass cheap: **no service signature was
added.** One verb was removed (`fullscreenQuadInstanced`), and one constructor overload plus one
static factory were added on a test-support class. Round six's table in `PHASE_1_REVIEW_6.md` records
the same conclusion from the other side.
