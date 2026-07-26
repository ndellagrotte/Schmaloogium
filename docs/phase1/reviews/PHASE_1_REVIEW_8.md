# PHASE_1_DOC.md — Verify session, round eight

**Session type:** verify (`DESIGN.md` §G1.2) · **Document under review:** `docs/phase1/v10/PHASE_1_DOC.md` (3 127 lines)
**Date:** 2026-07-25 (session start 03:48 UTC; network checks 03:50–03:56 UTC) · **Verdict:** **PASS-WITH-CORRECTIONS**

**Sub-agent disclosure:** **none used.** Every quotation in this review was read against its source
file or fetched page by this session directly. The §G1.2 permission for adversarial sub-agents was
declined because the brief requires each load-bearing quote to be re-derived by the reviewer before
admission as evidence, which makes a delegated finding a hop rather than a saving.

---

## 0. What I read, and in what order

Assigned reading, in the order §G1.2 prescribes:

1. `DESIGN.md` Part I in full (§G0–§G10, lines 1–574) and the Phase 1 spec in Part II (585–658);
   other phases by title and §G5.1 row only, plus the disclosed reads in §0.1.
2. `RESEARCH.md` §0 (reading guide, confidence tags) and §1 (mission, non-goals, decision log), then
   the spec's **Required inputs**: §1, §5.1, §5.2, §5.3, §6.1, §7.2, §12.2.
3. Template ground truth, complete: `build.gradle`, `settings.gradle`, `gradle.properties`,
   `gradle/scripts/*` (listing), `gradle/wrapper/gradle-wrapper.properties`, the `src/**` tree,
   `.github/workflows/*` (listing), `README.md`.
4. `PHASE_1_DOC.md`, §0 through §12.
5. **Last, and only after my own findings were formed:** `PHASE_1_REVIEW_7.md`, including its
   `## Resolutions` section, plus targeted searches across `PHASE_1_REVIEW_1.md` … `_6.md`.

### 0.1 Read beyond the assigned list, each because a finding turns on it

Recorded per §G1.1/§G1.2.

- **RESEARCH.md §3.2, §3.5, App A.3, App D.1–D.4** — the `countInstances` / `instanceId` provenance
  chain that V8-2 and V8-6 turn on, and the three smoothed uniforms V8-3 turns on. App D was read
  end to end, per the standing precedent that App D and App F are where the worst defects hide.
- **RESEARCH.md §4.2, §4.3, §4.4, §6.1–§6.3** — the per-slot "instance count", the composite-pass
  line §3 now cites, and the frame-begin world-state sampling step V8-3 turns on.
- **DESIGN.md Part II, the *Scope — in* / *Scope — out* bullets of Phases 3, 4, 5, 6 and 7** —
  auditing the ownership claims in `[D-P1-35]` and in §5.2's non-verbs row. §G1.1 line 78 bars a
  *build* session from other phases' specs; the four-round precedent is that a verify session
  auditing an ownership claim may read them, and the read is disclosed here regardless. **This is
  the read that produced V8-2.**
- **The OpenGL `glGetError` reference page**, live — new this round and in bounds because the
  document now cites it. Fetched from `docs.gl/gl4/glGetError` and `docs.gl/gl2/glGetError`.
  `registry.khronos.org/OpenGL-Refpages/gl2.1/xhtml/glGetError.xml` returned **HTTP 403**,
  independently reproducing §0.7's account of why the fix-up used the docs.gl mirror.
- **The pin-table sources**, live: `repo.cleanroommc.com/.../cleanroom/maven-metadata.xml` and
  `api.github.com/repos/CleanroomMC/Cleanroom/releases`.

### 0.2 Deviations from the assigned reading list

**`PHASE_1_REVIEW_1.md` through `PHASE_1_REVIEW_6.md` were not read end to end.** I read
`PHASE_1_REVIEW_7.md` in full including its `## Resolutions` section, and searched rounds one to six
by term for each finding I was preparing to raise — specifically to establish whether the composite-
loop ownership, the halflife premise, or the drain elision had been litigated before (V8-1 is new;
V8-2 and V8-3 are traced to their origins in §1). Rounds one to six are superseded by the document's
own §0.4–§0.6 and were audited by round seven. Recorded as a deliberate omission rather than claimed
as done.

### 0.3 Network use

Confined to the two permitted purposes: the pin table and the cited GL reference page. No third
fetch was made.

---

## 1. Findings

Five corrections, two notes, **zero blocking**. Three corrections touch §5.

---

### V8-1 — the drain-elision bit tracks *facade* calls, but the GL error flag is per-context and vanilla sets it too · **correction** · **touches §5: yes**

**Location.** §4.7.4 `[D-P1-30]` point 2 (ll. 1710–1717); §5.2's GL-error row (l. 2336); §7
(ll. 2456–2458); §9's GL-error row (l. 2581); §6's rung-2 row (l. 2406); §0.7's V7-2 bullet
(ll. 248–258).

**Claim under test.** That the elision is *"correct in both cases"* and *"self-correcting"* — §4.7.4:
*"if a mutating call **does** intervene between two sets, the bit is set, the leading drain queries,
and the window is correctly bounded."* And §5.2's contract form: *"a drain issues **no query at all**
when no mutating call has occurred since the previous drain."*

**Evidence.** The bit is defined two lines earlier as *"set by every mutating **facade** call, cleared
by every drain"* (l. 1711). The GL error flag it is used to reason about is **per-context**, not
per-facade — and this architecture guarantees a large volume of GL traffic that never touches the
facade:

- §3's own second row (l. 503) states it: on a gbuffers program the geometry is *"vanilla terrain or
  entity geometry drawn by Minecraft's own draw calls through Phase 7's hooks, **which never reach
  the facade**."*
- `DESIGN.md` §G4.6 makes cooperation with `GlStateManager` one-directional — vanilla drives it
  independently of us — and §G2.2 records that vanilla renders through fixed-function matrices,
  client arrays and display lists.
- §4.7.4's own absent-verbs table routes face culling *"through `GlStateManager` rather than through
  the facade"* (l. 1812), and the colour-mask row contemplates Phase 7 driving the anaglyph final
  *"through vanilla's own path"* (l. 1811).

So the intervening-call case the bullet says is handled is only handled for *facade* mutations. A
vanilla — or third-party-mod — GL call that errors between two of Phase 6's program-set sweeps sets
the driver flag and leaves the facade's bit **clear**. The leading drain then elides its query, the
foreign error survives into our window, and the trailing drain returns it on a uniform set in which
nothing of ours failed.

Two consequences, neither stated anywhere in the document:

1. **A non-empty trailing drain does not imply one of our uploads failed.** §5.2 is written to be
   *"sufficient on its own"* for a Phase 6 session (l. 1744), and read literally it says the opposite.
2. **The elision is what admits the foreign error.** An unconditional leading drain would have
   absorbed and discarded it before the window opened. The §0.7 argument presents elision as strictly
   dominating the two rejected alternatives at no cost; it has this cost, and caller-side
   amortization — rejected partly for creating *"a silent misattribution"* — creates the same one.

**What survives.** The *outcome* is safe, and by the document's own precondition (ii): the replay
re-uploads with a drain between each upload, reproduces nothing, and the sweep falls to §6's 3→4
"unattributable" row rather than disabling an innocent uniform. The containment is real but
incidental — precondition (ii) was written for `OUT_OF_MEMORY`, not for foreign errors, and nothing
in §4.7.4, §5.2, §6 or §7 identifies non-facade GL as a source a drain can observe.

**Fix shape** (the fix-up's call): say "mutating **facade** call" at both §5.2 and §4.7.4 l. 1715,
and state the consequence once — that a drain window can contain an error our facade did not cause,
that this is why precondition (ii) is load-bearing rather than an `OUT_OF_MEMORY` corner, and whether
Phase 6 is owed anything further. Whether to keep the elision is a judgement, not a defect: the one
query per clean sweep is real, and the alternative costs the factor of two §7 exists to bound.

---

### V8-2 — the composite `countInstances` loop is assigned to Phase 5, but `DESIGN.md` assigns it to Phase 7 · **correction** · **touches §5: yes**

**Location.** §5.2's non-verbs row (l. 2345, *"**5** (the composite loop itself …)"*); §4.7.4's
absent-verbs instanced-draw row (l. 1815, *"**5** (the composite owner)"*); §3's first row (l. 502,
*"The loop and its cadence are Phase 5/6's"*).

**Claim under test.** That Phase 5 owns the composite/deferred `countInstances` re-render loop and is
therefore the phase that would request an instanced-draw verb.

**Evidence.** `DESIGN.md` Part II, **Phase 7**'s *Scope — in, part (a)*, names it explicitly:

> **Composite/final execution**: fullscreen quad (triangle-strip fallback), identity ortho,
> fog/depth/blend disabled, per-pass mipmap generation (composite-mipmap bitmask), `scale.<prog>`
> sub-viewports [v0.5], **`countInstances` instancing loop [v0.5]**, anaglyph-aware final to the
> vanilla framebuffer (Phase 5 handoff).

**Phase 5**'s *Scope — in* contains no pass-execution bullet at all, and its *Scope — out* removes
the adjacent case in the same terms: *"when copies/clears **happen** in the frame (Phase 7)."* Phase 5
owns the buffer estate the composite passes read and write — ping-pong, flips, clears, formats,
sizing — not the draw loop that runs them.

The document is otherwise scrupulous about this distinction: §3's `scale.<prog>` row (l. 506) says
*"computing the rectangle from the scale factor is Phase 5's"*, correctly separating the computation
from the execution `DESIGN.md` gives Phase 7. The `countInstances` rows do not make that separation —
*"the loop itself"* and *"the composite owner"* are execution.

**Why this survived seven rounds.** Round seven saw the phrase and named it: V7-1's *touches §5* line
reads *"§5.2's non-verbs row states the mechanism and **assigns the consumer to Phase 5** (\"the
composite owner\") alone"* (`PHASE_1_REVIEW_7.md` ll. 252–253), and its fix shape asked for the owner of
the **non-composite** case. The fix-up delivered that half — `[D-P1-35]` names Phase 7 — and left the
composite half standing at all three sites. Neither round seven nor the fix-up checked the composite
attribution against Phase 7's *Scope — in*. This is the neighbour-left-standing pattern at a site the
fix-up edited.

**Fix shape.** Retarget the three sites to **Phase 7**, or state why Phase 5 is meant despite
`DESIGN.md`, and flag the disagreement per §G1.1. Note the pleasing side effect: `[D-P1-35]`'s
non-composite owner and the composite owner then coincide, and §5.2's non-verbs row stops naming two
different phases for two halves of one directive.

---

### V8-3 — the "halflife filter advances per sample" premise is unsourced, and `DESIGN.md` Phase 6 requires a formula under which it is false · **correction** · **touches §5: yes**

**Location.** §5.2's GL-error row, property (i) (l. 2336); §4.7.4's first replay precondition
(ll. 1747–1755); §6's rung-2 row (l. 2406); `[D-P1-32]`'s rationale (l. 2837); §0.7's V7-4 bullet
(ll. 271–281).

**Claim under test.** *"`wetness`, `eyeBrightnessSmooth` and `centerDepthSmooth` advance a halflife
filter **per sample**"*, and therefore *"sampling them a second time inside one sweep double-advances
the smoothing and produces a visible artifact."* This is stated as **contract** in §5, untagged.

**Evidence.** Three sources, none supporting it and one contradicting it:

1. **RESEARCH.md App D** gives the values only: `wetness` *"rainStrength smoothed by
   wetness/drynessHalflife"*, `eyeBrightnessSmooth` *"smoothed by eyeBrightnessHalflife"*,
   `centerDepthSmooth` *"smoothed by centerDepthHalflife"*. It says nothing about what advances the
   filter or when.
2. **App D's cadence model**, which §4.7.4 cites in the same sentence, says *"everything **refreshes**
   on program switch (per-program location cache + redundant-upload skip)"*. A refresh there is an
   **upload**, not a re-sample — the redundant-upload skip only makes sense if the value is already
   computed. RESEARCH.md §4.4 puts the actual sampling at **frame start**: *"sample world state →
   uniforms (time, rain→wetness decay, eye brightness smooth …)"* — once per frame, not per switch.
3. **`DESIGN.md` Phase 6's *Scope — in* contradicts it outright.** It assigns *"World-state sampling &
   smoothing (§4.4 frame-begin, App D.1/D.3)"* and requires the session to *"specify the smoothing
   math exactly (halflife → per-tick exponential decay formula, **time-corrected**)."* A
   time-corrected decay is a function of elapsed time; evaluated twice within one frame the second
   call advances by ≈0. Under the formula `DESIGN.md` mandates, the stated hazard cannot occur.

The premise did not originate with the fix-up — it is round seven's own wording in V7-4
(`PHASE_1_REVIEW_7.md` ll. 353–357), adopted verbatim and promoted into §5 as contract. It is the
pattern §0.7 names as its own lesson (*"a sentence of inherited reasoning that was backwards"*),
recurring one round later.

**What survives.** The **rule is correct and should stay.** Re-uploading cached values is right for
the reason the same sentence already gives — `glUniform*` is idempotent on the bound program and the
replay's only purpose is to change which drain window each upload lands in. That argument is
sufficient on its own and needs no claim about Phase 6's providers.

**Why it is a correction and not a note.** Phase 6's smoothing math is explicitly Phase 6's
*Scope — in*. §5 currently binds Phase 6 to a stated property of its own not-yet-designed providers,
sourced to nothing and contradicted by that phase's spec. §G1.1's rule for a call that is not this
phase's to make is to *flag* it. A Phase 6 session that designs the time-corrected formula
`DESIGN.md` requires will find §5 asserting something false about its work.

**Fix shape.** Drop the per-sample clause and keep the idempotence argument, or retain the caution
with an `[A]` tag and a pointer to Phase 6 as its owner.

---

### V8-4 — the document's first GL citation carries no URL, which `RESEARCH.md` §0.2's definition of `[V:web]` requires · **correction** · **touches §5: no**

**Location.** §4.7.4 (ll. 1702–1704); `[D-P1-30]` (l. 2835); §0.7 (ll. 291–295).

**Claim under test.** That `[V:web]` is used in the sense RESEARCH.md §0.2 defines. §0.1 of this
document commits to exactly that: the tags are used *"with exactly those meanings."*

**Evidence.** §0.2 defines the tag as *"Verified against a live web source (**URL in §12.5 or
inline**), accessed 2026-07-24."* The citation gives a page name, a mirror name and a read date, and
no URL at either site. §12.5 is RESEARCH.md's own source index, dated 2026-07-24, which this document
may not amend (§G1.1) and which does not contain the page. So neither branch of the definition is
satisfied.

This is not pedantry in this document specifically: §4.2.6's thirteen pin rows each carry a full
resolvable URL, several with the exact metadata path, and one row records which endpoint returns 403.
The one new citation is the only sourced claim in the document a re-verifier must guess at.

**Verified separately, and it all holds** — see §2. The quotation is exact, the identical-wording
claim is true, and the docs.gl mirror is the reasonable choice. The defect is provenance form, not
substance.

**Fix shape.** Add the URL inline, and — since §0.2 stamps `[V:web]` at 2026-07-24 *"unless noted"* —
keep the 2026-07-25 read date that is already there.

---

### V8-5 — §4.2.3 names two homes for `SeamClasspathArguments` "equivalently"; only one of them works, by the section's own argument · **correction** · **touches §5: no**

**Location.** §4.2.3 (ll. 671–681); §12 item 4b, which repeats the phrasing.

**Claim under test.** *"It therefore lives in **`buildSrc`**, or equivalently in a shared script
plugin under `gradle/`; the choice between those two is the implementation session's."*

**Evidence.** The section establishes the governing mechanism two sentences earlier, correctly:
*"A class declared inline in a Gradle build script is compiled into **that script's** class scope: it
is invisible to a sibling subproject's script, and a class in the root `build.gradle` is invisible to
`:engine/build.gradle` too."* That is right, and it is the reason inline is ruled out.

The same mechanism rules out the second home. A "shared script plugin under `gradle/`" in this
project means an `apply from:` script — that is what the template's three scripts are, applied at
`build.gradle` ll. 100, 238, 239 (`apply from: 'gradle/scripts/dependencies.gradle'` and the other
two) `[V:template]`. An applied script plugin is compiled into its **own** class scope; a class
declared in it is not resolvable by simple name from the applying script. The literal form the
section's three code blocks use — `new SeamClasspathArguments(...)` in §4.2.3, §4.2.4 and §4.2.4a —
compiles under `buildSrc` and fails under an applied script without an `ext`-indirection the document
does not mention.

`buildSrc` (or an included build carrying a precompiled plugin) is the only home that supports the
written form. The section treats them as comparable work — *"Neither exists in the template today, so
standing one up is real work"* — which will steer an implementation session toward the cheaper-looking
option, which is the broken one.

**Mitigating.** §12 item 4b's own test hook would catch it: *"`./gradlew :engine:test --dry-run`
configures without an unresolved-class error."* The failure is loud and immediate, not silent.

**Fix shape.** Drop "or equivalently a shared script plugin under `gradle/`", or qualify it to a
precompiled script plugin in an included build and note that a plain `apply from:` script will not
serve. Same edit at §12 item 4b.

---

### V8-6 — §3's first row attributes a *restriction* to RESEARCH.md §4.4, which observes rather than restricts · **note** · **touches §5: no**

**Location.** §3's first `countInstances` row (l. 502).

**Claim under test.** *"**The composite restriction is RESEARCH.md §4.4's**, which is the only place
the instancing *loop* is observed."*

**Evidence.** §4.4's composite-pass line reads *"optional sub-viewport (`scale.<prog>`),
`countInstances` instancing loop"* — an observation of the reference implementation's composite pass,
not a statement that the directive is confined to composites. §3.2 (*"re-renders geometry N times with
`instanceId` incrementing"*), App A.3's `(vsh)` tag, App D.4's common-block `instanceId` and §4.2's
per-slot instance count on all 43 slots all put the directive on any program.

**Why a note.** The row's own second clause immediately supplies the honest version (*"the only place
the instancing loop is observed"*), and the second row carries the remainder under an explicit `[A]`
tag reading *"the **scope** is inferred."* Taken together the pair is accurate; only the six words
*"the composite restriction is RESEARCH.md §4.4's"* overstate a single source. Worth one word
(*"restriction"* → *"the only observed form"*) at the next fix-up, not worth a round.

---

### V8-7 — `[D-P1-35]` assigns the gbuffers/shadow re-render to Phase 7, whose *Scope — in* does not name that case · **note** · **touches §5: no**

**Location.** `[D-P1-35]` (l. 2840); §3's second row (l. 503); §11.4's Phase 7 entry; §5.2's
non-verbs row.

**Claim under test.** Whether Phase 1 is making a decision that is not its to make (§G1.1: *"flag them
instead"*).

**Evidence.** Two of the three assignments are squarely within their targets' *Scope — in*: Phase 3
owns *"Directive scanning (complete App A.3 table)"*, which contains the `countInstances` row; Phase 4
owns per-slot *"instance count"* verbatim. The third is an extrapolation — Phase 7's *Scope — in*
names the `countInstances` loop only under composite/final execution, not on the gbuffers path.

**Why this passes.** Phase 7 is nonetheless the only defensible home: it owns the gbuffers
phase-dispatch table and the App E hook catalog through which vanilla geometry is drawn. More
importantly, the document does not *design* the case — §3's row carries an `[A]` tag reading *"an open
case handed onward, not designed here"*, §11.4 says *"open rather than designed"*, and §11.4's Phase 7
entry uses the conditional (*"the re-render itself **would be** yours"*). That is a flag in §G1.1's
sense, not a decision. Recorded so §G5.3's integration review, which audits *"orphaned §11 hand-offs
that no later phase adopted"*, has the pointer.

---

## 2. What was checked and came back clean

Named, because a round that reports only its findings misrepresents its own coverage.

**The GL citation, verified at source — the strongest clean result of this round.** I fetched
`docs.gl/gl4/glGetError` and compared the document's block quote (ll. 1697–1703) word for word. It is
**exact**, including the sentence boundaries and the conditional tail *"if all error flags are to be
reset."* The claim that the wording is *"identical in the GL 2.1-era and GL 4 refpages"* was checked
against `docs.gl/gl2/glGetError` and is **true**. The inference the document draws — that a single
call per drain leaks a second flag into the next window, where the first replayed window would name
an innocent uniform — follows from the quoted text and does not overreach it; the source's conditional
is exactly the condition `[D-P1-32]`'s replay operates under. §0.7's account of the fetch is also
independently corroborated: `registry.khronos.org` returned **403** to me too. The one defect is the
missing URL (V8-4); the substance is sound.

**The pin table, live-re-verified — no drift.** `maven-metadata.xml` reports `<release>0.6.6-alpha`,
`<lastUpdated>20260724133703`; the GitHub releases API reports `0.6.6-alpha` published
2026-07-24T13:37:05Z as the newest, with `0.6.5-alpha` (2026-07-24), `0.6.4` (07-23), `0.6.3` (07-22),
`0.6.2` (07-20) behind it. Both agree, exactly as §4.2.6's own procedure requires. The document's pin
of `0.6.6-alpha` stands. Round seven checked at ~03:05 UTC; I checked at ~03:52 UTC the same day, so
this is a 47-minute-later confirmation, not an independent day's sample — stated so a later reader
does not over-read it. Every other pinned coordinate was checked against the template files rather
than the network and matches: Unimined `1.4.26-kappa`, Gradle `9.6.1` (wrapper `distributionUrl`),
Java 25 toolchain, JUnit `6.0.3`, `sponge-mixin:0.20.13+mixin.0.8.7`, Blossom `2.2.0`, shadow `9.5.1`,
idea-ext `1.4.1`, foojay `1.0.0`.

**The `Resolutions` audit of `PHASE_1_REVIEW_7.md` — all eight rows real and complete.** I checked
every site named in every `Where` column. V7-1: §3 both rows (502–503), §4.7.4 `DrawService` javadoc
(1614–1619) and absent-verbs row (1815), §5.2 (2345), `[D-P1-33]` amended and `[D-P1-35]` new
(2838, 2840), §11.4 Phases 3 and 7 — all present. V7-2: §4.7.4 (1710–1717), §5.2 (2336), §6 (2406),
§7 (2456), §9 (2581), `[D-P1-30]`/`[D-P1-32]` (2835, 2837), §12 item 22 (3065) — all present. V7-3:
`drainErrors()` javadoc no longer promises call order unconditionally (1492–1495), §4.9.3's **four**
flag rows plus the note (2108–2127), and the rest — all present. V7-4: `GLError` javadoc (1630–1641),
§5.2's three lettered properties, §6's rung-2 **and** 3→4 rows (2406, 2409), `[D-P1-34]` new (2839) —
all present. V7-5: §4.2.4a's italic deletion note and §4.11 item 3 — both present, and they agree
(see below). V7-6: §4.2.3 plus §12 items 4b/5/6/7, the latter three now routing through 4b's provider
— all present. V7-7: §6's unnumbered row and §11.5's new item 4, cross-linked in both directions —
present. V7-8: header carries both dates (l. 10) and §0.6's dangling footnote is closed (l. 212).
**Nothing claimed as applied is missing, partial, or applied at fewer sites than claimed.** That is
the second consecutive round at which a whole applied list survives audit.

**The per-call cadence narrowing — swept, no neighbour left standing.** This was the change most
likely to leave residue, and it did not. Every site that states the trigger now names a debug context
plus `recordGL`/`glLabels` and only those: §4.7.4 (1686–1692), §4.9.3's four flag rows and the
following paragraph (2108–2127), §5.2 (2336), §7 (2452), §9 (2581), §12 item 22 (3065). I searched
the whole document for a surviving *"any `-Dschmaloogium.debug.*`"* formulation and found none.

**§4.2.4a's half-deletion and §4.11 item 3 agree.** What remains of the tying sentence rests on the
`--exclude-task` mechanism alone (`compileTestJava` is required by `test` and not by `assemble`/`jar`,
so `build -x test` drops it), which is correct. §4.11 item 3 states the same mechanism and grounds the
non-`-x test` decision on §12 item 15's **local** run — the one path where `build` actually reaches
`:conformance:compileTestJava`, since the italic note correctly observes that in CI the named
`:conformance:test` step fails first. The two passages are consistent and neither claims what V7-5
deleted.

**Template ground truth — all seventeen §4.1 rows verified against the files.** Branch `main` with no
mixin JSON and no `MixinConfigs` attribute; `settings.gradle` with no `include` and
`rootProject.name = rootProject.projectDir.getName()`; the Unimined block at root with `loader
"0.5.17-alpha"` as an inline literal; the `${rootProject.projectDir}` AT hardcode; Blossom's `package`
= `"${root_package}.${mod_id}"` (which is exactly `[D-P1-1]`'s stated reason for the override); the
`jar` `doFirst` manifest block; the `contain` configuration; shadow disabled with `remapJar` as the
active remap task; `jar` classifier `dev` `finalizedBy(remapJar)`; JUnit 6.0.3 with no `src/test/`;
Gradle 9.6.1; the single `enable_lwjglx` `compileOnly` site; `.gitignore` carrying `**/build/`.
All correct.

**Doc gate, literally.** Module/package layout finalized with dependency rules as testable
constraints — C-1 … C-4 in §4.3, each with a named test and a failure message (§8.1). Every D-1..D-10
dispositioned in §11.2. Pin table complete with a re-verification procedure that is genuinely
executable (§4.2.6's seven steps and three-ruling table, restated in §G4.4 form at §10.1). **Met.**

**Template completeness.** All thirteen §G9 sections present and substantive. All four assigned OQs
carry full spike specs in the §G4.4 shape — OQ-2 (§10.1), OQ-12 (§10.2), OQ-20 (§10.3), OQ-21 (§10.4).

**Conformance-map audit.** Twenty-one rows, zero unmapped. I spot-checked ten against the cited
RESEARCH.md text rather than the document's paraphrase — the four §4.1 probes, the GL-3.0 mipmap gate,
the §3.5 macro-header fields, App D.3/D.1's `ivec2` pair, App D.4's `blendFunc` `ivec4`, App F.7's
three per-program state keys, and both `countInstances` rows. Only the two findings above emerged.
**§3's second row is a legitimate conformance-map entry, not an unmapped row in a costume:** §G9's
template says *"any deviation is a flagged decision, never silent"*, and this row is a flagged
decision — provenance cited, ruling stated, `[A]` tag on the inferred scope, owners named, §11.4
hand-off written. A row saying "not ours, and here is who owns it" with all of that attached is the
form §G9 prescribes; the alternative — deleting it — would be the silence §G1.2 exists to catch.

**`[D-P1-34]` is sufficient.** A backend author reading it knows precisely what to do: retain the
`String` passed to `locate(program, name)` on the `UniformLocation` implementation so
`GLError.subjectLabel` can carry it. It states the alternative it forecloses (a backend that discards
it satisfies every signature while making rung-2 attribution worthless), explains why the facade
cannot express it in a type without making `UniformLocation` carry a string it has no other use for,
confirms no signature changes, and gives it a review hook at §12 item 22. A contract no test can catch
is legitimate here on `[D-P1-29]`'s established precedent, and it is written down rather than assumed
— which is the whole point.

**Round seven's clean list, not re-raised.** I re-derived `[D-P1-33]`'s central argument
independently — RESEARCH.md App D.4 declares `instanceId` an `int` uniform (*"0 original, 1..N
instanced copies"*) and §3.5 confirms the GLSL-120-era baseline, so no single instanced draw can vary
it per copy — and it holds. V6-5's blit narrowing, the four-handle/no-renderbuffer model, the
`ivec3`/`mat3` absence, the App F.7 mappings, the Phase 8 shadow-copy consumer, §3.1's flagged-delta
ruling, §6's unnumbered row, the Gradle/ASM work and the CI ordering were each examined and none gave
me new evidence to overturn. §3.1's ruling in particular is right on §G0.1's terms: an addition
sourced elsewhere in RESEARCH.md is not a conflict, and tagging it `[A]` rather than `[V:observed]` is
the honest disposition.

---

## 3. Verdict

**PASS-WITH-CORRECTIONS.**

Five corrections, two notes, zero blocking. Nothing here is a structural miss; the document's
architecture, its conformance map, its interfaces and its build plan are sound, and four of the five
corrections are a sentence or a clause each. FAIL is not close.

The honest ratio: the fix-up's new prose is where four of the five corrections landed, and the fifth
(V8-5) is in the other section it wrote. Everything seven rounds have already swept came back clean,
including several things I re-derived from source specifically to try to overturn. **The brief's
prediction was right in direction but not in size** — the surface was narrow, and it still yielded
three §5-touching corrections, because the fix-up's own new §5 prose had been read by exactly one
session, the one that wrote it. §0.7's closing lesson — *"unreviewed material yields findings in
proportion to its size, not to the document's maturity"* — describes this round as well as the last.

Two of the three §5 findings trace to reasoning the fix-up **inherited from round seven's own finding
text** and promoted into contract without checking it (V8-3 verbatim, V8-2 at a site round seven
explicitly quoted). That is worth recording as this round's pattern: **a review's supporting argument
is not evidence, and a fix-up that promotes it into §5 has changed its status without changing its
support.**

### Per-finding §5 disposition

| Finding | Severity | Touches §5? |
|---|---|---|
| **V8-1** drain elision vs. non-facade GL | correction | **yes** — §5.2's GL-error row states the elision as contract |
| **V8-2** composite loop owner is Phase 7, not Phase 5 | correction | **yes** — §5.2's non-verbs row names Phase 5 |
| **V8-3** the per-sample halflife premise | correction | **yes** — §5.2's GL-error row, property (i) |
| **V8-4** `[V:web]` without a URL | correction | no — §4.7.4 and `[D-P1-30]` only |
| **V8-5** the second `SeamClasspathArguments` home | correction | no — §4.2.3 and §12 item 4b |
| **V8-6** "restriction" attributed to §4.4 | note | no — §3 only |
| **V8-7** `[D-P1-35]`'s Phase 7 assignment | note | no — passes as written |

### §G1.3 line

**`PHASE_1_DOC.md` is NOT verified.** The verdict is PASS-WITH-CORRECTIONS with resolutions
outstanding, so §G1.3's verified state is not reached. Three of the five corrections alter §5.2 — its
GL-error row (V8-1, V8-3) and its non-verbs row (V8-2) — so once a fix-up applies them, §G1.3's
*"re-verify only if §5 changed"* rule fires again and a **ninth verify session** is required before
Phase 2, Phase 3 or any other dependent consumes this document (§G5.3). Until that verdict exists,
this document is not a valid dependency input.

Two things the ninth session should be told, because they narrow it further than round eight was
narrowed. **No signature changes here either** — every service interface, handle type and value type
is byte-for-byte what round seven and round eight both reviewed, and none of the five corrections
requires touching one. And **V8-2 is the only correction that changes what a dependent phase does**;
V8-1 and V8-3 correct statements about protocols whose behavior is unchanged, so a Phase 6 session
reading the corrected §5 will build the same thing, only with two premises it can trust.

*Per §G1.2 this session stops here. It wrote no code, ran no build and no test, applied no fix, and
created exactly one file: this one. `PHASE_1_DOC.md`, `RESEARCH.md`, `DESIGN.md` and all seven prior
review files — including `PHASE_1_REVIEW_7.md`'s `## Resolutions` section — are unmodified.*

---

## Resolutions

*Written by the fifth fix-up session (§G1.3), 2026-07-25, after this review's verdict. **Nothing
above this heading is modified** — the findings, the clean list and the verdict are evidence. Line
numbers below are `PHASE_1_DOC.md`'s **after** this fix-up; §0.8 is the addendum recording the
session.*

**Method note, because this round's meta-finding demanded one.** §3's closing assessment is that a
review's supporting argument is not evidence, and that a fix-up promoting one into §5 changes its
status without changing its support. Accordingly **no finding below was applied on this review's
reasoning.** Each was re-derived from the source it cites before anything was written: `DESIGN.md`
Part II's Phase 5/6/7 *Scope — in* / *Scope — out* bullets read directly for V8-2 and V8-3;
RESEARCH.md §4.4, §4.2, §3.2, App A.3 and App D read directly for V8-3 and V8-6; RESEARCH.md §0.2's
tag table for V8-4; `build.gradle`'s three `apply from:` sites for V8-5; and for V8-1, the
per-context/per-facade asymmetry re-derived from `PHASE_1_DOC.md` §3's own second row plus
`DESIGN.md` §G4.6 and §G2.2. **All five corrections and both notes survived. None was refused.** Two
fixes changed shape under derivation and both are marked below. One live fetch was made — 
`https://docs.gl/gl4/glGetError`, to confirm the URL V8-4 requires resolves and that §4.7.4's block
quote is verbatim against it. It is.

| Finding | Disposition | Where (every site **edited**, not every site named) |
|---|---|---|
| **V8-1** — drain elision vs. non-facade GL | **Applied**, at **9 sites** (this review named 6). The **elision is kept** — a judgement call, settled with the project owner: dropping it would pay a factor of two on a synchronous driver query across 43 program switches per frame (the cost §7 exists to bound) to relabel a case `[D-P1-32]`'s replay already contains, and a guard inside the facade is unavailable because **the facade cannot observe non-facade GL**. What is corrected is the claim: `"the window is correctly bounded"` is **deleted**, every site now reads "mutating **facade** call", and the consequence is stated once and promoted to §5 contract — *a non-empty trailing drain does not by itself imply that one of Phase 6's uploads failed*, which is why precondition (ii) is load-bearing generally rather than an `OUT_OF_MEMORY` corner. The residue is given an owner rather than left unowned | §4.7.4 l. 1896 (*"mutating **facade** call"*, and *"correctly bounded"* removed) · §4.7.4 ll. 1899–1919 (**new paragraph** — the per-context/per-facade asymmetry, the three architectural sources, the consequence, why the elision is kept, why no facade-side guard exists) · §5.2 GL-error row l. 2543 (*"no mutating **facade** call"*; new contract sentence; (ii) widened; attribution clause now *"one mutating **facade** call"*) · §6 rung-2 row l. 2613 (*"nothing mutating **through the facade**"*; the 3→4 clause now names a foreign error as a second cause) · §7 l. 2662 · §9 l. 2788 · **§12 item 22 l. 3297** *(neighbour — not named by this review)* · `[D-P1-30]` l. 3042 (new consequence clause) · §11.4 ll. 3154–3167 (**new hand-off to Phase 7**: one unconditional `drainErrors()` at a frame-driver point, ≈1 extra query/frame instead of 43; placement is Phase 7's, Phase 1 supplies only the verb) · §0.7's V7-2 bullet ll. 259–263 (**bold** supersession pointer per §0.6's convention; the round-seven argument itself is left intact as a record) |
| **V8-2** — composite loop owner is Phase 7, not Phase 5 | **Applied, and wider than the fix shape asked** — the widening is the one substantive departure from this review and is argued in §0.8. Derivation confirmed the retarget outright (`DESIGN.md` Phase 7 *Scope — in*, part (a) names the `countInstances` instancing loop under **Composite/final execution**; Phase 5 has no pass-execution bullet and its *Scope — out* removes the adjacent case), so **no §G1.1 disagreement is flagged — the document was simply wrong**. The widening: the old wording was *"the loop and its cadence are Phase 5/6's"*, and the **6** is right for a reason the **5** obscured — `DESIGN.md` Phase 6's cadence model carries `instanceId` among per-draw dynamics *"at their hooks (Phases 7/9/10 invoke)"*. So **Phase 7 runs the loop and Phase 6 owns the `instanceId` upload between copies**, and both are named rather than one digit swapped | §3's first row l. 668 (owner clause rewritten, both phases named with their `DESIGN.md` citations) · §4.7.4 absent-verbs instanced-draw row l. 2022 (*"**5** (the composite owner)"* → **7**, with the Phase 5 distinction stated) · §5.2 non-verbs row l. 2552 (**consumer list changed**: Phase 5 removed, Phase 6 added, both `countInstances` halves folded into the Phase 7 entry; Detail cell gains a *"Retargeted in this revision (§0.8)"* clause) · `[D-P1-35]` l. 3047 (both halves now land on Phase 7; the two cases kept distinct in kind — composite **assigned**, gbuffers **open**) · §11.4 ll. 3144–3152 (**new paragraph** to Phase 7 distinguishing the assigned composite loop from the open gbuffers case) |
| **V8-3** — the per-sample halflife premise | **Applied**, resolved toward **deletion** rather than the `[A]`-tagged-caution alternative this review offered. All three evidence strands re-derived and confirmed: App D gives the values only; App D's cadence note and RESEARCH.md §4.2 make a "refresh" an **upload** (the redundant-upload skip presupposes a computed value); RESEARCH.md §4.4 puts the sampling at **frame start**; `DESIGN.md` Phase 6's *Scope — in* requires a **time-corrected** decay formula. The `[A]` route is **refused with cause**: `[A]` marks a *working assumption*, and this document has no basis for the assumption in either direction, so tagging it would preserve an unsupported claim in a costume. **The rule is unchanged and stays** — idempotence carries it alone, and the "re-upload, not re-run" motivation is preserved in the honest §G1.1 form (*this document asserts no property of Phase 6's providers, whose cadence and smoothing math are Phase 6's own scope*) | §4.7.4 first precondition ll. 1950–1962 (clause deleted; idempotence argument kept; the three sources now cited *for the flag*, not for the claim) · §5.2 property (i) l. 2543 (rewritten; (ii) also widened) · §6 rung-2 row l. 2613 (*"double-advance the halflife filters"* replaced) · `[D-P1-32]` rationale l. 3044 (the deleted premise named as deleted, with its four counter-sources, so the decision log records the reversal rather than hiding it) · §0.7's V7-4 bullet ll. 287–291 (**bold** supersession pointer; round seven's argument left intact as a record) |
| **V8-4** — `[V:web]` without a URL | **Applied.** RESEARCH.md §0.2's definition re-read: *"URL in §12.5 or inline"*. §12.5 is RESEARCH.md's own index, which §G1.1 forbids this document from amending, so *inline* is the only available branch and is taken at both live citation sites. The 2026-07-25 read date is **kept**, as §0.2's *"unless noted"* provides for; the `registry.khronos.org` 403 is recorded with it, on the precedent of §4.2.6's pin row that does the same | §4.7.4 ll. 1878–1884 (both URLs inline, plus the §0.2 branch reasoning and the 403) · `[D-P1-30]` l. 3042 (both URLs inline at the `[V:web]` tag) · §0.7 ll. 304–305 (bold note that the URL was missing and where it now is) |
| **V8-5** — the second `SeamClasspathArguments` home | **Applied** as *drop*, not *qualify*. Verified against the template: `build.gradle` ll. 100/238/239 are `apply from:` script plugins `[V:template]`, and an applied script plugin is compiled into its own class scope — the **same mechanism §4.2.3 already invokes two sentences earlier to rule out inline** — so the literal `new SeamClasspathArguments(...)` form all three code blocks use does not resolve from the applying script. The one genuine alternative (an included build carrying a **precompiled** script plugin) is named positively; the plain `apply from:` form is named as not serving, with the reason attached. §12 item 4b's test hook is **unchanged** — `./gradlew :engine:test --dry-run` is what makes this failure loud | §4.2.3 ll. 838–852 · §12 item 4b l. 3268 |
| **V8-6** — "restriction" attributed to §4.4 | **Applied** as the six-word change requested, and **no more**. §4.4's composite-pass line re-read (it observes, and §3.2 / App A.3's `(vsh)` tag / App D.4's common-block `instanceId` / §4.2's 43 slots impose no restriction). The row's second clause and the second row's `[A]` tag are untouched, per this review's own instruction that the pair is accurate together | §3's first row l. 668 (*"The composite restriction is RESEARCH.md §4.4's"* → *"the only observed form is RESEARCH.md §4.4's"*) |
| **V8-7** — `[D-P1-35]`'s Phase 7 assignment | **No change required — passes as written**, and deliberately so. Re-checked: §3's row carries the `[A]` tag reading *"an open case handed onward, not designed here"*, §11.4 says *"open rather than designed"*, and §11.4's Phase 7 entry is conditional (*"would be yours"*). That is a flag in §G1.1's sense, not a decision, and editing it would remove a correct hand-off. Recorded here so §G5.3's integration review has the pointer this review asked for. *(§11.4's Phase 7 section did gain new material this round — the composite-loop paragraph (V8-2) and the foreign-GL hand-off (V8-1) — but `[D-P1-35]`'s open-case paragraph itself is unmodified apart from V8-2's clause about the two halves coinciding.)* | — (no edit) |

### Neighbours swept, beyond the finding sites

Recorded separately because this review audited round seven's `Where` column site by site and round
nine will audit this one.

- **§0 header l. 10** — *"Last revised: 2026-07-25 (§0.7)"* → **(§0.8)**.
- **§0.7's closing paragraph l. 325** — carries the standing convention: *"This subsection records
  round seven only and is no longer the document's current state — see §0.8."*
- **§5.2's opening row l. 2542** — the per-revision changelog gains this revision's entry: **still
  no signature change**, with the two prose-corrected rows named and attributed to V8-1/V8-3 and
  V8-2 respectively.
- **The end-of-document closing paragraph ll. 3346–3362** *(neighbour — not named by this review)* —
  counted *"**Seven** verify sessions … **four** fix-up sessions"* and stated round seven's §5 rows
  as the trigger for an eighth. Now eight/five, with this round's §5 rows and a **ninth** session.
- **New §0.8** at l. 328, following §0.4–§0.7's established shape.

**Checked and correctly left alone**, so a later audit can tell a considered omission from an
oversight: `DrawService`'s javadoc (ll. 1784–1795) and `[D-P1-33]` (l. 3045) both describe the
composite loop but **name no owner**, so V8-2 does not reach them — and the javadoc already used
V8-6's corrected formulation (*"the only form RESEARCH.md §4.4 observes"*), which is why §3's row
was the sole "restriction" site. §1.2's adjacent-concerns table has no `countInstances` row.
§11.5's four requested-upstream-changes items are **unchanged and none was added**: V8-1's residue
is a §11.4 hand-off, within this document's own gift, not a request against `DESIGN.md`.

### §G1.3 status

This fix-up altered **§5** at two rows — §5.2's **GL-error row** (V8-1's facade scoping and its new
contract sentence; V8-3's rewritten property (i)) and its **non-verbs row** (V8-2's retarget, which
also changes that row's consumer list: Phase 5 out, Phase 6 in). **No service signature was added,
removed or changed:** every service interface, handle type and value type is byte-for-byte what
rounds seven and eight both reviewed. §G1.3's *"re-verify only if §5 changed"* rule therefore fires,
and a **ninth verify session** is required before Phase 2, Phase 3 or any other dependent consumes
`PHASE_1_DOC.md`; until that verdict exists the document is **not** a valid dependency input
(§G5.3).

*Per §G1.3 this session stops here. It wrote no code, ran no build and no test, launched no review
agent, and modified exactly two files: `PHASE_1_DOC.md` and this `## Resolutions` section.
`DESIGN.md`, `RESEARCH.md`, and `PHASE_1_REVIEW_1.md` through `PHASE_1_REVIEW_7.md` — including their
own `## Resolutions` sections — are unmodified, as is everything above this heading.*
