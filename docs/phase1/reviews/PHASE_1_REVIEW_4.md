# Schmaloogium — Phase 1 verify session, round four (DESIGN.md §G1.2)

**Document under review:** `docs/phase1/v10/PHASE_1_DOC.md` as it stands, 2026-07-24 — unchanged since
round three, and unchanged since round two.
**Why this session exists:** `PHASE_1_REVIEW_3.md` returned PASS-WITH-CORRECTIONS with twelve
findings, three of them §5-touching, and its own F3-1 established that round two's fix-up never ran.
The cadence has now produced three verify sessions and one fix-up, and rounds two and three returned
substantially the same list. Before a fix-up spends effort on twelve items, this session asks the
question the loop has never asked: **which of those findings actually survive an attack, and what
have all three rounds missed?**
**Reviewer:** fresh session, 2026-07-24. Did not author the doc, did not review it in rounds one,
two or three, applied no fix-up, and inherited no context from any of those sessions.
**Deliverable filename:** `PHASE_1_REVIEW_4.md`, continuing rounds two and three's convention.

> **Read this first — two corrections to the premises this round inherited.**
>
> 1. **§G1.2 does not forbid adversarial sub-agents.** Round three recorded "no review or adversarial
>    sub-agents" among its hard rules. That rule is §G1.1's, and §G1.2 line 147 inherits only three
>    of them — "no code, no scope creep, context discipline" — conspicuously not the *"No verification,
>    no review"* bullet where the sub-agent prohibition lives. Round three was stricter than the
>    cadence requires. **This session used them** (disclosure in §0.3), which is what made an
>    adversarial pass over the findings possible.
> 2. **Round three's own §5-touching set is wrong in both directions.** F3-4 does not touch §5 and
>    should never have driven a re-verify. F3-2 and F3-3 do — and so do **six further items no round
>    has raised**. The re-verify is required, but not for the reasons round three gave.

---

## 0. What I read, and how

### 0.1 Assigned reading

`DESIGN.md` Part I (§G0–§G10) and the Phase 1 spec in Part II (lines 585–658); `RESEARCH.md` §0–§1
and the spec's Required inputs; `PHASE_1_DOC.md` in full; the template ground truth (`build.gradle`,
`settings.gradle`, `gradle.properties`, `gradle/scripts/*`, `gradle/wrapper/gradle-wrapper.properties`,
`src/**`, `.github/workflows/*`, `README.md`); and, last, `PHASE_1_REVIEW_1.md`,
`PHASE_1_REVIEW_2.md` and `PHASE_1_REVIEW_3.md`.

### 0.2 Read beyond the assigned list

| Extra input | Why |
|---|---|
| `RESEARCH.md` App B.1–B.5, App D.1–D.4, App F.1/F.5/F.6/F.7, §3.1/§3.2/§3.4/§3.5, §4.1–§4.4, §4.7, §4.8, §6.1/§6.2 | The contract sweep and the conformance-map audit §G1.2 requires |
| `DESIGN.md` Part II specs for Phases 3, 4, 6, 7 (Scope-in bullets only) | Three findings turn on which phase owns a thing. Round three inferred ownership; this round read it. §G1.1 line 78 bars *build* sessions from other phases' specs — a verify session auditing an ownership claim has no such bar, and disclosing the read is the §G1.1 remedy in any case |
| Live network: `repo.cleanroommc.com`, `api.github.com`, `maven.arcseekers.com`, `maven.wagyourtail.xyz`, `maven-central.storage-download.googleapis.com`, `maven.cleanroommc.com` | The pin table's re-verification, ordered by the spec |
| MCP `cleanroom` (`search_docs`, `get_porting_guide`, `search_mod_examples`, `get_example`), and the 1.12.2 `GlStateManager` field list | To settle F3-11 on evidence rather than on absence, and to establish which state `GlStateManager` actually caches |
| Repository state: `LICENSE`, `.gitignore`, `git log`, `git status` | The doc's repo-state claims. Read-only |

**Deliberately not read:** `cleanroom-src/src/main/java/com/cleanroommc/` beyond a `MixinConfig`
grep; `phase_1_chatlog.md`.

### 0.3 Deviation disclosed (§G1.1's recording requirement)

This session ran its attack through **fifteen sub-agents**: seven refuters (one per finding unit,
each instructed to *kill* its finding and to default to REFUTED on thin evidence), three steelmen
(on F3-2, F3-3, F3-4 — each shown the refuter's conclusion and told to argue the document's side),
three §G1.2 mandate agents (doc gate / template / spikes; conformance map / binding decisions;
interface honesty / scope / pins), and two completeness sweeps. Every load-bearing quote below was
then re-read against the source file by the synthesising session before being admitted as evidence.

Permitted by §G1.2 line 147, as set out above. Recorded here so the deviation from round three's
stricter self-imposed practice is on the record rather than silent.

### 0.4 Hard rules observed

No code, no builds, no tests, no scope creep, no fixes. `PHASE_1_DOC.md`, `RESEARCH.md`,
`DESIGN.md`, `PHASE_1_REVIEW_1.md`, `PHASE_1_REVIEW_2.md` and `PHASE_1_REVIEW_3.md` are unmodified.

---

## 1. Adversarial disposition of F3-1 … F3-12

Every line citation in `PHASE_1_REVIEW_3.md` resolves correctly — I checked them all before
designing the attack, and none is fabricated or mis-numbered. The dispositions below therefore turn
on substance, never on whether a quote matched.

| Finding | Round 3 said | **This round** | Severity now | §5 now |
|---|---|---|---|---|
| F3-1 round-2 fix-up never ran | blocking | **OVERSTATED** | note | no |
| F3-2 `blendFunc` / `ivec4` | blocking, §5 | **CONFIRMED** | blocking | **yes** |
| F3-3 `StateService` | correction, §5 | **OVERSTATED** | correction | **yes** |
| F3-4 ARB provenance | correction, §5 | **OVERSTATED** | note | **no** |
| F3-5 build files | correction | **CONFIRMED** | correction | no |
| F3-6 `RenderbufferHandle` | note | **CONFIRMED** | note | no |
| F3-7 dead `mod-*.jar` | note | **CONFIRMED** | note | no |
| F3-8 `eyeBrightness` → D.1 | note | **CONFIRMED** (fix under-scoped) | note | no |
| F3-9 §4.10 vs §9 | note | **CONFIRMED** | note | no |
| F3-10 cross-reference slips | note | **OVERSTATED** (half refuted) | note | no |
| F3-11 nested mixin packages | note | **REFUTED** | none | no |
| F3-12 ASM caveat | note | **REFUTED** | none | no |

Six confirmed, four overstated, two refuted.

### F3-1 — OVERSTATED: the facts are right, the filing is wrong · demote to **note**, **not** §5

Every factual claim holds and I verified each independently. `PHASE_1_REVIEW_2.md` ends at line 475
with `## 4. Verdict`; it has no `## Resolutions` heading and no renamed equivalent (its §3 audits
*round one's* resolutions, which is the opposite). A grep of `PHASE_1_DOC.md` for
`round 2|round two|REVIEW_2|V2-` returns nothing, and there is no §0.5. **All ten of V2-1 … V2-10 are
still present, and no silent or partial fix exists anywhere** — the negative checks matter most here:
`blendFunc`, `colorMask`, `cullFace` and `depth test` return **zero** hits across all 2323 lines, and
`RenderbufferHandle` occurs exactly once, in the dead permit.

What fails is the severity and the location. §G1.2 directs a verify session at the *document*;
§G1.3 puts the recording duty on the fix-up session and the record in the *review* file, so the
absent Resolutions section is not a defect in `PHASE_1_DOC.md`. §G9's §0 template requires phase
name, date, inputs read, dependency docs and deviations — no review-cadence ledger — so "there is no
§0.5" breaches nothing. And the doc's operative claim to dependents is still **true**: §0.4 line 98
and the closing paragraph both say it is not a valid dependency input. No dependent is misled.

Decisively, the finding concedes its own severity: round three's verdict table gives F3-1's
consequence as *"forces the fourth pass **by way of** F3-2/F3-3/F3-4"*. Delete F3-1 and the required
next actions are identical. `touches §5` was borrowed from other findings.

**What survives, at note severity:** §0.4 (83–98) and the closing paragraph (2319–2323) still
describe the post-round-one state and assert that the next cadence step is a fresh verify session,
when two have since returned PASS-WITH-CORRECTIONS. One refreshed sentence in each. Touches §0 only.

### F3-2 — CONFIRMED at full severity, on stronger evidence than the finding gives · **blocking**, **§5**

Attacked from five directions and survived all five; the steelman then built the strongest available
defence and could not rescue the text. The finding is right, and understated in one respect.

The scope defence — that `blendFunc` is App D.4 per-draw dynamics and therefore Phase 9's at v0.3 —
**inverts into the finding's strongest support**. `DESIGN.md:986` puts *"**`blendFunc` observation**
via GlStateManager cooperation"* inside **Phase 6's** Scope-in as a dedicated bullet; line 970 lists
it in the cadence model; line 1581's dropped-item audit routes it *"`blendFunc`/GlStateManager → P6
+ G4.6"*; and line 363 puts Phase 6 at milestone **v0.1**, depending on Phase 1. §G0.3 therefore
binds at full strength and cuts against the document. Round three's hedge ("shared with Phase 9 at
v0.3") is unnecessary — the fix-up should cite 986/363/1581 rather than the §G4.6 parenthetical.

The narrow-but-true defence fails on the sentence's grammar: line 1226's subject is *"The 1.12.2
contract"*, with App D.3 and App F.6 offered after an em-dash as evidence *for* it, not as its scope.
A grep of `RESEARCH.md` for `ivec3|ivec4|mat3` returns exactly one uniform row — line 1376's
`blendFunc` — so the claim has precisely one counterexample and is false as written. `blendFunc`
appears **nowhere** in `PHASE_1_DOC.md`, and the widest integer overload is `upload(loc,int,int)`
(1102): no four-int form, no array form, no generic form.

**Why blocking survives, and why the F3-2/F3-3 severity split is principled rather than
inconsistent.** F3-3's gaps are *absent* from §4.7.4's closing table, so §5.2's escape hatch ("If you
need an assertion **or a facade verb** that is not in §4.7.4/§4.7.5, add it in your own doc's §5")
stays usable. F3-2's item is *present* in that table with a false reason, and line 1690 actively
routes dependents to it: *"§4.7.4's closing table names the verbs that are absent on purpose and who
is expected to ask for each."* That is the doc contradicting contract ground truth in the one place
built to distinguish gap from decision — §G1.2 line 132's depthtex1-unit-11 class exactly.

**Two corrections to the finding's own text**, which the fix-up should not transcribe: (i) F3-2 says
"§5.2's non-verbs row states the same falsehood" — it does not; line 1676 merely lists `ivec4` among
absent verbs, which is true. The §5 flag is right on the other half: the fix adds an overload to the
exposed `UniformService` and must strike `ivec4` from 1676. (ii) 1676's real §5 defect is a false
*routing* — its Consumed-by column names 14 and 4 and never 6.

**Root cause, worth recording.** `DESIGN.md:637` does not list App D among Phase 1's Required inputs,
and §G1.1 line 78 says "Nothing more." The doc's §0.1 record confirms only App D.3 was read. The
author was structurally unlikely to see App D.4 — which does not change the severity, but does frame
the lesson: **a doc that volunteers a claim beyond its reading list inherits the burden of checking
it.** Whoever applies the fix should read App D end to end, not patch the single row.

### F3-3 — OVERSTATED: the headline is a quotation error, and one sub-claim is refuted three times over · **correction**, **§5**

**Sub-claim 1 — REFUTED.** The finding says the doc's exclusion rule "is false of *every* verb the
service already has". The doc, verbatim at `PHASE_1_DOC.md:1181-1182`:

> It exposes no way to set state that `GlStateManager` caches **without going through it**, because
> §G4.6 forbids exactly that ("we never bypass it for state it caches").

The trailing clause is load-bearing and the causal clause disambiguates it beyond argument: this is
a **bypass** rule about how the backend implements the verbs, not an **inclusion** criterion for
which verbs exist. The inclusion criterion is the preceding clause at 1180–1181. The finding's own
"Claim under test" block bolds the full sentence at review line 321, then its evidence section at
line 329 re-quotes it four words short and calls that false. Round three escalated round two's
accurate provenance quibble — *"§G4.6 does not enumerate perturbed state; the list is the doc's own"*
(V2-2) — into "the rule is false" by dropping four words. **This headline must not reach the fix-up:
applied literally it would delete a correct sentence that carries the §G4.6 enforcement claim.**

**Sub-claim 3 — REFUTED.** "App F.1's `backFace.*` flags have … no named owner" is false, and
`DESIGN.md` contradicts it three separate times. Lines 777–779 make *"**Required output — the
engine-flag ownership map**: a table assigning every App F.1 flag to the phase that implements its
behavior"* a **Phase 3 deliverable**, with `backFace.*` in the worked example routed to **Phase 7**;
line 1054 has Phase 7 claim the wiring; line 421 has §G5.3's integration review audit the map. Phase
1 writing the owner down would be scope creep into another phase's mandated deliverable, forbidden
by its own §1.3. The finding concedes the substantive answer is right and then faults the doc for
not pre-empting an objection the finding itself resolves in the doc's favour.

**Sub-claim 2 — survives, reduced from four state elements to two-and-a-half.** `StateAspect`
appears exactly once in 2323 lines (1155) and is never defined, so the reviewer's suggested rescue is
dead — but `snapshot`/`restore` could not close it anyway, since they reinstate state and cannot set
it. Depth *test* and fog genuinely have no verb, and Phase 7 is **v0.1 exit** (`DESIGN.md:364`), so
the milestone defence is unavailable. Colour mask / anaglyph is note-level and explicitly Phase 7's
per `DESIGN.md:1048`. **"Identity ortho" should be struck**: the facade has no matrix-state verb of
any kind by design, so a projection is not expressible through any engine call — though the two
agents split here, and the steelman is right that `fullscreenQuad()`'s comment (1160) delegates only
the primitive choice and never state, so *whether* it establishes the composite draw state is a live
unrecorded choice.

The doc's own criterion condemns the residue: line 1192 — *"A facade that can create and bind objects
but not put data into them or read data out of them is not implementable by its dependents"* — is
what justified reaching into Phase 6's and Phase 13's v0.1 needs for the pixel-transfer verbs. And
line 1184 concedes the boundary: *"**Which** state is perturbed at which moment is Phase 5/6/7
policy"* — whether the verb exists at all is Phase 1's question.

**On §5: true, but on precedent rather than inference, and cheap.** `PHASE_1_DOC.md:96` records that
the round-one fix-up "altered **§5** (F-1 and F-4)", and F-1 was the missing pixel-transfer verbs —
so adding facade verbs was already ruled a §5 change by this document. But §5.2 states nothing false
about state verbs, and the minimal fix leaves §5.2's text byte-identical. F3-3 adds no marginal
re-verify cost over F3-2.

### F3-4 — OVERSTATED: the governance document already made this decision · demote to **note**, **not §5**

The dispositive evidence is in `DESIGN.md`'s **Phase 4** spec, which neither round two nor round
three cites. Line 848: *"the preprocessor/scanner accepts both; **internal translation strategy
specified here**."* Line 845: *"Core GL objects (`glCreateProgram` family) via the **facade, not ARB
entry points** (§6.2)."* Phase 4 is v0.1 (line 361). So DESIGN.md itself (a) adopts §6.2's internal
translation, (b) schedules the strategy at v0.1, and (c) forbids ARB entry points through the facade
— a Phase 1 facade exposing `glProgramParameteriARB` would *contradict* governance. Phase 1 did not
make a unilateral call; it conformed to one, and reproduced the correct seam without being permitted
to read the spec that assigns it (§G1.1 line 78). That is corroboration, not over-reach.

The provenance leg fails too. §3's table is four-column (line 263) and §G9 line 521 defines it as
contract item → design element → **provenance tag**, so the tag qualifies the *contract item* — which
in row 276 is the pack-side ARB declaration fact, and that is `[V:doc]` three times over
(`RESEARCH.md` §3.1:215, App A.3:1160, and §6.2:770's own risk note). Row 273 is the exact structural
precedent in the same table: a §6.2 modernization named in the design column with the contract item's
tag untouched — and round three's own sweep passed that row clean. OQ-22 is a *"Spot-check ledger"*
(§G10:572), a bookkeeping duty on P14, not a design gate.

The §5 leg fails: §5.2:1676 does carry the assumption and its escape route, and §11.4:2173 states
the hand-off explicitly and as contestable — which is where §G9:538 puts hand-offs. §5 is defined as
*interfaces and data contracts* (§G9:526); a source-level rewrite obligation is neither.
**Round three's proposed fix — a §5 row obligating Phase 3 to the rewrite — must not be applied**: it
misassigns the work (`DESIGN.md:848` gives the strategy to Phase 4) and reaches into pack-format
design that Phase 1's Scope-out assigns away (`DESIGN.md:633`).

**What survives, as a note:** row 276's design column says "§6.2's adopted modernization", attributing
adoption to a section whose preamble frames its rows as unpromoted opportunities. The doc gets this
exactly right for the sibling row 1100 lines later — *"RESEARCH.md §6.2 lists it as a modernization
*opportunity*"* (1223) — which proves it is a copy-edit slip, not a misunderstanding. A six-word
hedge closes it. **No §5 edit.**

*(The separate, real §5 defect round three was reaching for — that Phase 3's obligation is not
surfaced in §5 — is confirmed below as **F4-4**, on interface-honesty grounds rather than provenance
grounds.)*

### F3-5 — CONFIRMED, and the sharpest defect in round three's list · **correction**, no §5

Four attacks, none landed. The most promising — a settings-level `dependencyResolutionManagement`
block supplying repositories build-wide — does not exist: the template `settings.gradle` is 36 lines
and its only `repositories` block is nested inside `pluginManagement`, which governs the plugin
classpath, and the doc's rewritten §4.2.1 (330–346) reproduces that shape exactly. In Gradle 9.6.1
(the wrapper pin) `repositories` is per-`Project` with no inheritance, and transitive externals of a
project dependency resolve against the **consuming** project's repositories. A grep for `repositor`
across the doc returns exactly two hits: line 332 (pluginManagement) and line 420 (`:engine`).
`:mod` is covered because §4.2.4 applies `dependencies.gradle`, whose line 1 is `repositories {`.
`:conformance` is covered by nothing — while the root `subprojects {}` hands it
`testImplementation 'org.junit.jupiter:junit-jupiter:6.0.3'`, and §8.1:1798 confirms it really does
get a test.

**As specified, `./gradlew build` — §12 item 15, the Impl gate — cannot pass**, failing at
`:conformance:compileTestJava` with "Cannot resolve external dependency … because no repositories are
defined". Item 6's own hook (`:conformance:compileJava`) cannot surface it, because the failure is in
the *test* configuration.

Sub-claim 2 holds on the direct check the finding demanded: §8.1:1787 and :1788 do place
`SeamInternalsTest` (C-2) and `SeamLwjglConfinementTest` (C-3) in `:mod`, and `[D-P1-3]` (2075)
scopes ASM to `:engine` "only". Sub-claim 3 holds narrowly — §2.1 line 155 introduces the tree as
"concrete Gradle project paths, **source roots**, and Java packages", so it is an enumeration, not a
sketch.

Two refinements for the fix-up. **The `:mod` ASM line needs a resolution note**: `:mod`'s test
classpath inherits Unimined's 1.12.2 dev dependencies, which carry a legacy `org.objectweb.asm`
(asm-debug-all 5.x) that cannot read Java 25 class files, so ASM 9.10.1 must be forced or the legacy
one excluded — otherwise C-2 and C-3 fail at run time with an unhelpful error. And prefer adding
`repositories { mavenCentral() }` to `:conformance` over hoisting into the root `subprojects {}`:
hoisting would inject `mavenCentral()` into `:mod` **ahead of** `dependencies.gradle`'s
CurseMaven/Modrinth entries and would dilute §4.2.3's by-construction argument for C-1.

### F3-6 · F3-7 · F3-8 · F3-9 — all CONFIRMED at note severity, with three fixes needing adjustment

**F3-6** holds, and the "it would not compile" escalation fails: the §4.7.3 listing is deliberate
non-compilable pseudo-Java (lines 1045–1048 carry literal `permits …` ellipses), which §G1.1 line 93
sanctions, and item 18's own hook is "Compiles" — javac catches it in the first minute. No forward
slot exists either: `renderbuffer`/`RBO`/`GL_RENDERBUFFER` return **zero** hits in `RESEARCH.md` and
`DESIGN.md`, and §4.3 makes every attachment a sampleable texture. §2.4's key-type table (246) lists
exactly four handle types, breaking the tie in favour of "four". Drop the permit; correct item 18.

**F3-7** holds. `build.gradle:22` has `archivesName = mod_id`, §4.2.4 moves the root machinery
"verbatim" with four changes none of which touches `base {}`, and §4.4.1 sets `mod_id = schmaloogium`.
**Take the finding's first option (`schmaloogium-*.jar`), not deletion** — the surviving `mod/build`
check misses a real violation shape: a `:conformance` dependency on the published coordinate resolves
from a Maven cache path containing `schmaloogium-0.1.0.jar` and no `mod/build` segment. While there,
the check should say "path segment" rather than a literal `mod/build` string, which would not match a
Windows classpath entry.

**F3-8** holds, and **its fix is under-scoped**. The mis-citation appears at **four** sites, not two:
line 275 (§3), 1102 (§4.7.4 comment), **1195** (§4.7.4 rationale) and **2097 (§11.1, `D-P1-25`)`** —
the last outside the finding's stated scope of "§3 and §4.7.4". Line 92's header input record needs
it too. A fix-up grepping only the two cited line numbers would leave two sites wrong.

*The depthtex1-unit-11 spot-check this finding invites was run properly:* eleven §3 rows opened
against their cited `RESEARCH.md` text, **exactly one bad** — this one. Clean: the four capability
probes and the GL-3.0 mipmap gate (§4.1:474-476), `shaders.debug.save` (App F.8:1522),
`centerDepthSmooth` (§3.2, A.3, §4.4), the noise texture (§4.1 step 4, App F.5), the ARB geometry
form (§3.1, A.3, §6.2), GL_QUADS / no-UBOs / lwjglx (§6.1:757-760). A second agent working
independently opened all seventeen rows and reached the same result.

**F3-9** holds, but **drop its §G4.3 citation** — §9 does give every component exactly one tag, so no
§G4.3 rule is violated; the defect is prose-vs-table. And "an implementation session resolves by
guessing" is overstated, since §4.10's own numbered list names "(Phase 10)" for point 2 two
paragraphs above. What kills the §G0.3 defence is that **§12 contains no item for evaluation point 2
at all**, so "three evaluation points wired" is inaccurate under the phase-ownership reading too.

### F3-10 — OVERSTATED: one leg holds, one is a misreading · **note**

Leg (a) holds, narrowly. §4.9's four subsections contain lists of names but no naming *convention*; a
whole-document grep for `verbatim|vocabular|synonym|naming convention` finds only §2.3 (mod id/root
package), §4.3's *package*-naming rule (645), and the §3 row itself. The pointer resolves to nothing.
**Fix by deletion**, not by inventing a section: drop "Naming convention recorded in §4.9;" and keep
the substantive clause, which is what actually discharges the §G4.1 row.

Leg (b) is refuted. §4.2.5's full sentence is *"Recorded against §12 item 7, which is the Impl-gate
item **that depends on this merge**"* — a restrictive clause identifying an item the gate depends on,
inherited verbatim from round one's F-12 (*"the packaging step the Impl gate (item 7 …) depends
on"*). Item 7 does carry the caveat §4.2.5 says is recorded there, so the cross-reference resolves.
And "the Impl-gate item" is not this document's designator for item 15 — item 12 is labelled *"**The
Impl gate's test**"*. **Applying this leg as written would be actively harmful**: repointing §4.2.5 at
item 15 would point at an item carrying no merge instruction.

### F3-11 — REFUTED · **no defect**

The premise is accurate — §4.5.2's table does specify packages, and the DEFAULT config's is the
parent of the other two. But **§12 item 30 already carries the exact check the finding proposes
adding a clause to**: *"`runClient` loads all three configs without error"*, at v0.1, on the item that
creates the files. There is no clause to add; the detection exists. On the platform side, the MCP
`cleanroom` tools return nothing supporting the hazard and something against it: the Cleanroom wiki
Configuration page defines `package` per config as "The root package of where the mixins resides"
alongside a separate `parent` key for config *inheritance*, and states no exclusivity or non-nesting
constraint; `search_mod_examples` for coremods-mixins returns no examples; `get_porting_guide`
("mixin-setup") shows a two-config manifest and says nothing about packages. "Undocumented" is true
but symmetric.

What remains is a hazard the finding's own author declines to assert — *"I did not verify this and I
am not asserting it is broken"* — which does not meet §G1.2's per-finding evidence requirement. And
the proposed fix costs more than stated and cuts the wrong way: renaming to sibling packages would
also rewrite §2.1's package table (196, exposed by §5.1 to all phases), §2.4's row (253) and the
reserved plugin FQN (840) — a contract-visible convention changed on an unverified hunch.

*One adjacent observation, raised as an observation and not a finding: the reserved plugin class sits
at `com.schmaloogium.mod.mixin.SchmaloogiumMixinPlugin`, inside the DEFAULT config's declared mixin
package. If any package-placement question in §4.5.2 deserves a look, that is the one.*

### F3-12 — REFUTED · **no defect**

The finding states in its own text that "the caveat was honest when written" and "**The pin is
correct**", and round three's verdict concedes it "improves the record rather than fixing a defect".
A findings-list entry certifying the document accurate is not a finding under §G1.2 — it belongs in
the verdict prose, where it does not inflate the correction count. The doc also owns the update path
already: the ASM row ends *"Re-check with the rest of the table at each milestone"*, and §5.1 exposes
the re-pin procedure as a standing obligation.

Independently re-verified twice this session: the Google Central mirror returns
`<release>9.10.1</release>`, `<lastUpdated>20260523184314</lastUpdated>`. **The pin is right.**

**One over-claim to keep out of the doc.** F3-12 calls its two sources "mutually independent".
`search.maven.org/solrsearch` is Sonatype's index *of Maven Central*; the Google endpoint is a byte
mirror *of Maven Central*; `repo1.maven.org` is Maven Central. Two transports, one dataset. That is
the same provenance inflation F3-4 charges the document with — so if F3-4's standard is upheld, the
replacement wording must not import it.

---

## 2. §G1.2 mandate — the checks a verify session owes regardless

### 2.1 Doc gate — **PASS**, all three criteria, read literally

Criterion 1 is met by **named tests with stated assertions**, not intentions: C-1…C-4 (§4.3,
671–683) are backed by five JUnit tests with the forbidden-coordinate and forbidden-type-prefix
lists, the injected system properties, and negative hooks in §12 ("fails when a `net.minecraft.*`
reference is added deliberately"). The apparent hole — nothing forbids `:engine` depending on
`:mod` — is closed structurally by Gradle cycle detection. Criterion 2: §11.2 disposes of all ten
with a named owner per deferral, and D-7's factual claim was verified against the tree. Criterion 3:
every template-sourced pin value was cross-checked against the actual build files (Gradle 9.6.1,
Java 25, MCP `stable`/`39-1.12`, shadow 9.5.1, idea-ext 1.4.1, unimined 1.4.26-kappa, blossom 2.2.0,
foojay 1.0.0, junit 6.0.3, sponge-mixin 0.20.13+mixin.0.8.7, lwjglx 1.0.0) — all correct, and
"nothing floats" is true of those files.

*One factual slip in round three's own §1.4, not in the doc: the pin table has **fourteen** rows, not
thirteen. The document asserts no count, so nothing is defective — but the review's Doc-gate table
should not be transcribed as-is.*

### 2.2 Template completeness — **PASS**, thirteen sections present and substantive

Checked section by section with line ranges rather than asserted. §7, the shortest, is **not thin**:
it delivers all three §G9 elements with component-specific content — a seven-row thread-ownership
table (not a blanket "render thread"), `UniformLocation` named as the one hot-path allocation with
the no-pooling stance tied to §G2.5, and exactly one hot path identified with three designed
mitigations plus an explicit not-hot-path list. §6 is substantive but maps only rungs 3/4/5, which is
where **F4-1** lives.

### 2.3 OQ spikes — **PASS**, all four, all four parts — and the verbatim-ness no prior round checked

Each quoted question block was extracted, normalised and **diffed programmatically** against its
`RESEARCH.md` §11 row. All four are **byte-exact**, including table pipes, em dashes, bold markup and
status text; the doc quotes the whole row rather than only the Question column, which is stricter
than §G4.4 requires. OQ-20's procedure is the strongest of the four — correctly scoped to Phase 1's
share ("is our seam hard enough", not "forecast Kirino"), with a countable metric and a falsifiable
experiment. OQ-12's documentary procedure is the right form, because `RESEARCH.md` §11's own
verification-path column for OQ-12 asks for "short considered note; ecosystem precedent survey" — and
§4.8.4 delivers both.

*But see **F4-9**: OQ-20's success criterion is vacuous at the milestone the spike is scheduled for.*

### 2.4 Conformance map — **PARTIAL**

Seventeen rows opened against their cited text; thirteen fully supported. The three defects are
F3-8's mis-citation, **F4-6** (an unmapped in-scope row), and **F4-16** (three rows tagged `[V:doc]`
for DESIGN.md-sourced project rules). Independent contract sweep: the two dirty rows round three
found are the two dirty rows — `blendFunc` and the composite/final draw state — and I concur with its
clean verdicts for App B.1–B.5, D.1–D.3, F.1/F.6/F.7, §3.2 and §4.1–§4.4, reached before reading it.

### 2.5 Binding decisions and §G4.2 — **PASS**

No D-1…D-10 is contradicted, and every deferral names a real §G5.1 owner. §G4.2 is clean, and
**twice the doc resists a temptation it could have taken**: it *keeps* the GL-3.0 mipmap gate that
§6.2:769 offers to drop, and *keeps* the synchronous `centerDepthSmooth` read whose async form is
Phase 14's under OQ-22. Adopting either would have been the §G4.2 violation. No flip quirk, clear
colour, unit number or fallback chain is touched by this phase.

### 2.6 Scope discipline — **PASS** in both directions

All thirteen Scope-in bullets are answered, several by quoting the spec's own question back (the
`contain` decision in §4.2.5, the AT decision in §4.4.3, the `mcmod.info` licence question in §4.4.4,
answered negatively because the 1.12.2 schema has no `license` key). Nothing from Scope-out is
designed: §8.4 refuses to exercise a conformance tier, §4.7.4 enumerates the GL policy it withholds,
the only pack-format text defers rather than designs, and ModularUI appears only in the OQ-12 note.

### 2.7 Pins — **live re-verified, no drift**

| Pin | Live result, 2026-07-24 | Verdict |
|---|---|---|
| Cleanroom loader `0.6.6-alpha` | `<latest>`/`<release>` both `0.6.6-alpha`, `lastUpdated 20260724133703`; GitHub API newest `0.6.6-alpha` published 2026-07-24T13:37:05Z, of ten releases. Sources agree to the second | ✅ no drift |
| Unimined `1.4.26-kappa` | arcseekers coordinate resolves; `<latest>`/`<release>` both `1.4.26-kappa`, `lastUpdated 20260711130828` | ✅ correct |
| ASM `9.10.1` | Google Central mirror `<release>9.10.1</release>`, `lastUpdated 20260523184314` | ✅ correct |

Two provenance side-claims also check out exactly: `maven.cleanroommc.com` really does **301** to
`repo.cleanroommc.com` at the recorded path, and `maven.wagyourtail.xyz` really does top out at
`1.4.1` with **zero** kappa builds — the doc's "do not upgrade to 1.4.1" warning is accurate as
written. Repo-state claims all true: `LICENSE` is 674 lines of verbatim GPL-3.0, tracked, commit
`aa917a6 Update LICENSE from MIT to GPL-V3`; `.gitignore` carries `**/build/`; `git log`/`git status`
match §11.2's D-7 row.

*The procedure's executability is a separate matter — see **F4-13**.*

---

## 3. New findings

Twenty-one items no round has raised. Numbered `F4-*`. Where two or three sub-agents reached the same
finding independently, that is noted — it is the strongest signal in this report.

### F4-1 — The facade has no GL-error surface, so §G2.4 rung 2 cannot be implemented; §6 never maps rungs 1–2 and asserts a mechanism no signature carries · **correction** · **§5**

*Reached independently by three of the five non-refuter agents.*

**Location:** §4.7.4 `UniformService` (1099–1109) and every void-returning verb; §6's failure table
(1727–1738, esp. 1732); §4.9.4 (1526); §4.7.4's closing table (1218–1228).

**Evidence.** `DESIGN.md:218` states ladder rung 2: *"A built-in uniform whose GL upload errors
disables *that uniform* only."* `DESIGN.md:990` puts *"Per-uniform GL-error isolation (ladder step
2)"* in **Phase 6's Scope-in** — verified directly — and Phase 6 is v0.1 and depends on Phase 1.
`RESEARCH.md:612` records it as reference behavior and :615 classes the family as *contract-adjacent*,
which §G4.2 makes load-bearing.

Every `UniformService.upload` overload returns `void`. A doc-wide grep for
`glGetError|GLError|checkError` returns exactly one hit, in §6 prose. So there is no result, no
status, and no error-query verb anywhere in the facade — the rung has no signal to act on. §6 line
1732 nonetheless asserts the backend surfaces a driver error *"as a result or a diagnostic"*: the
"result" half is false for every mutating verb, and the surviving half is a log line `:engine` cannot
branch on. Worse, that row assigns rungs **3→4** and escalation to a pack-level bail — the *opposite*
of rungs 1–2's "disable that uniform only" — so a uniform-upload error is routed off the ladder's
bottom two rungs entirely. §4.9.4:1526 already assigns the rung-1/2 outcome a severity and channel
(`WARN`/`LOG_ONLY`), which makes §6's silence an omission rather than a scoping judgement.

That this is inconsistency rather than deliberate layering is settled by the doc itself: line 1176
reasons explicitly *"§G2.4's rung 3 requires that a …"* into a return-type decision. Rung 2 gets no
equivalent — and per-uniform error reporting appears in neither kind of §4.7.4's
"deliberately does NOT contain" account, whose stated purpose (1219) is *"so a dependent knows whether
it is looking at a gap or at a decision"*.

**Severity.** Correction, not blocking — consistent with the F3-2/F3-3 line drawn above: silence
leaves §5.2's escape hatch usable, whereas an affirmative false contract claim inside the table
dependents are routed to does not. §6's line 1732 is *inaccurate* about a mechanism, which is why
this sits at the top of the corrections rather than among the notes.

**Fix.** Either (a) give the uploads an observable outcome — a returned status, or a batched
error-drain a per-program upload sweep can consult — with the backend's `glGetError` policy stated
(per-call in dev, per-batch in production) so the cost is a decision rather than an accident; or (b)
declare it absent on purpose with a closing-table row naming Phase 6 and a §6 row saying rungs 1–2
live above the facade. (a) is the honest option given `DESIGN.md:990`. Either way §6 must state where
rungs 1 and 2 live, and line 1732 must stop promising a "result".

### F4-2 — §5's preamble attributes to §G5.3 a rule §G5.3 does not contain — and three round-3 findings reason from it · **correction** · **§5**

**Location:** §5 preamble, line 1651.

**Evidence.** Verified directly. The doc says *"per §G5.3 a dependent phase reads this section, not
the rest of the document."* §G5.3 (`DESIGN.md:400-425`) says no such thing; its only "reads §5"
sentence (line 416) is about the **final integration review** session. The actual rules are §G1.1
line 77 — a dependent build session's mandatory reading *includes* "the PHASE docs of your declared
dependencies", in full — and §G1.1 line 111, *"**Dependency docs are contracts.** What a dependency's
PHASE doc exposes in its §5 is what you build against"*. Read: the whole doc, build against §5.

**Why this matters more than a citation slip.** The sentence converts "not in §5" into "invisible to
the dependent", and **three of round three's findings lean on it** — F3-3 (line 330), F3-4's third
evidence point, and F3-2's framing — as do two of round two's. Every finding built on it is weaker
than stated. F3-4's third leg collapses entirely once it is corrected.

**Fix.** Re-cite to §G1.1's "Dependency docs are contracts" bullet and restate accurately. If the
stronger standard is retained *deliberately*, then §5 must be made to meet it — today every Detail
cell is a pointer into §4 ("§4.7.4 signatures"), which is fine under the real rule and not under the
claimed one.

### F4-3 — `TextureService.bindToUnit` bypasses `GlStateManager`'s texture cache — the same defect as F3-3, on a service F3-3's fix would not touch · **correction** · **§5**

**Location:** §4.7.4 line 1142; the "deliberately narrow" rule at 1180–1184.

**Evidence.** The doc's only statement about `GlStateManager` coherence is scoped to `StateService`.
Verified against the 1.12.2 mappings that `GlStateManager`'s cached blocks include **`textureState`
and `activeTextureUnit`** alongside `blendState`, `alphaState`, `depthState`, `clearState`,
`colorMaskState` and `fogState`. `bindToUnit` is a facade verb implemented by `Lwjgl3GLDevice` — which
lives in `mod.glue` (250) and therefore *can* delegate to `GlStateManager`, but is nowhere required
to. Since the fixed unit map re-points up to 16 units on every program switch (`RESEARCH.md` §4.2),
**this is the highest-frequency bypass in the facade**, not an edge case, and `DESIGN.md:346` makes
cache coherence a correctness requirement ("the cache would go stale and break vanilla rendering").

**Fix.** State a backend obligation in §4.7.4 — *every `Lwjgl3GLDevice` verb whose GL state
`GlStateManager` caches must be issued through `GlStateManager`, not raw LWJGL* — and list the
affected verbs. **This closes F3-3's sentence and this row together; fixing only F3-3's `StateService`
prose leaves `bindToUnit` uncovered.**

### F4-4 · F4-5 — Interface honesty: three dependents carry obligations that exist only outside §5 · **correction** · **§5**

*The sharp test — could a session reading only §5 build against Phase 1? — run per dependent.
Phases 4, 5, 6, 8, 10, 12, 13, 14 pass cleanly. Three do not.*

**F4-4 — Phase 3** (correction). The source-level ARB-geometry→core rewrite is placed on Phase 3's
front-end at §4.7.4:1205 and §11.4:2173, and §5's only trace (1676) names **Phase 4**. Add a §5 row
addressed to Phase 3, phrased as contestable. *This is the half of F3-4 that is real; it stands on
interface honesty, not on provenance, and it is the reason F3-4's demotion to note does not lose
anything.*

**F4-5 — Phase 2** (correction). §8.3:1819 makes fixture capture Phase 1's and the fixture
set/refresh workflow Phase 2's — but §5 never names the capture mechanism. §5.3's debug-flag row
(1706) lists consumers "3 (`saveSources`), 14 (`glLabels`)" and omits Phase 2 entirely, and the
explicit §5.2 note to Phase 2 (1682) enumerates what is *withheld* while still never naming what is
*given*. A Phase 2 session designing the refresh workflow would invent a capture path Phase 1
already built. Add **2** (`dumpCapabilities`, `recordGL`) to the consumed-by column and one clause to
the Phase 2 note.

**F4-14 — Phase 7** (note, §5). §11.3/§11.4:2179 hand Phase 7 two verification duties (refmap
generation unexercised in this checkout; `compatibilityLevel: "JAVA_8"` inherited and unconfirmed)
that appear in neither of §5.3's mixin rows.

*§5.3's ModularUI row is the model: it deliberately lifts an obligation out of §11.3 into §5 so
Phase 12 need not read §11. The doc knows how to do this, which makes the three omissions
inconsistency rather than house style.*

### F4-6 — §3 maps no row for §3.5's standard-macro-header inputs, and §4.7.2 claims two profile fields are mapped there when they are not · **correction**

**Location:** §3 table (263–281); §4.7.2 lines 982–984 and 1003.

**Evidence.** `RESEARCH.md` §3.5 (313–314) requires `MC_GL_VERSION`, `MC_GLSL_VERSION`,
`MC_GL_VENDOR_*` and `MC_GL_RENDERER_*` in the header injected into every pack shader — contract-
visible by construction. §3 maps exactly **one** of the four macro families (the extension set, row
270) and cites §3.5 while doing so, so this is not a scope question. The design element already
exists — `GLCapabilityProfile` carries `glslVersion`, `vendor`, `renderer` — only the map row is
missing, against §G9's "**ZERO** unmapped rows". Separately, line 1003 asserts `maxVertexAttribs` and
`maxTextureSize` are "tagged `[A]` in the §3 map"; neither appears anywhere in §3.

**Fix.** Add one §3 row; add the promised `[A]` rows or delete the sentence at 1003. §5.1:1668 also
understates the profile to Phase 3 as being for "extension macros" only.

### F4-7 — `blit`'s binding contract and `BlitSpec` are both unspecified, and the depth copies have no stated destination · **correction** · **§5**

**Location:** §4.7.4 line 1119; §4.7.5 `bindsBalanced()` (1289); §4.7.4 `snapshot` (1155).

**Evidence.** Round three's sweep disposed of this row in one sentence — *"expressible through
`blit(src, dst, BlitSpec)`. ✅ clean"* — by asking whether a verb of the right shape exists. It did
not ask what the destination has to be. `RESEARCH.md:514`: only `depthtex0` is an attachment;
`depthtex1`/`depthtex2` are copy-target **textures**, so a framebuffer-to-framebuffer blit cannot
reach them unless Phase 5 first invents destination framebuffers that exist for no other purpose — a
requirement recorded nowhere. And `RESEARCH.md:542` puts the `depthtex2` copy *between two draws into
the main FBO*, so the verb executes while a draw framebuffer is live and must be live again
immediately after; the doc never says whether `blit` restores the prior binding. `bindsBalanced()`
sees only facade-level calls, so a binding perturbed inside the backend is invisible to the one
assertion that would catch it, and §4.7.4's perturb-and-restore set does not include framebuffer
binding. `BlitSpec` appears exactly once in the document and is never defined, so the depth-vs-colour
mask and the nearest-filter requirement are unstated too.

### F4-8 — Handles have no invalidation story across §4.1's uninit, and the two backends' handle identity diverges exactly where it would hide the bug · **correction** · **§5**

**Location:** §4.7.3 (1036–1065, backend note 1057–1059); §4.7.5 (1268–1270, 1290).

**Evidence.** `RESEARCH.md:488` makes full teardown-and-rebuild a **routine v0.1 event** — an option
toggle fires it — not a shutdown path, so handles held by long-lived engine state outlive their
objects as a matter of course. `Lwjgl3GLDevice` "wraps ints" (1057), and a driver is free to reissue
a GL name after a delete, so a stale handle *silently addresses a different live object* rather than
failing. The recording backend returns "a monotonic sequence number" (1268) and never reuses one — so
**the aliasing failure mode cannot occur under replay**, and assertions are built on handle equality
(1269), making this the basis of what the tests can distinguish rather than an incidental difference.
The one lifetime assertion offered is `noLeakedObjects()` — every create has a matching delete — which
checks the opposite direction from the one reload produces.

**Fix.** Say something explicit either way. Minimum: a sentence in §4.7.3 that a handle is invalid
after its `delete`, plus a `ReplayAssertions.noUseAfterDelete()` so the recording backend enforces
what the LWJGL backend cannot. If invalidation is Phase 5's problem instead, record it as a deferred
row with Phase 5 named, so it reads as a decision.

### F4-9 — §10.3's backend-swap drill is scheduled at the one milestone at which its own success criterion is vacuous · **correction**

**Location:** §10.3 steps 3–5 (1991–1998) and criteria (2000–2003); §12 item 41 (2306).

**Evidence.** Step 4 is *"all `:engine` tests pass against `NullGLDevice`, except those that assert on
recorded calls (which by construction need the recorder)"*. Of the seven `:engine` tests in §8.1, the
profile tests, `LogChannelTest` and the two seam tests touch no `GLDevice` at all; the only two that
do are `RecordingGLDeviceTest` and `ReplayAssertionsTest` — **exactly the ones the criterion
excludes**. After the exclusion the tested set is empty. §1.3 (143) guarantees
`engine.pack`/`registry`/`buffers`/`uniforms` are empty at v0.1, so there is no engine logic for the
step to catch. Step 5's re-run trigger is "whenever Kirino's API changes" — never "once engine logic
exists" — so the seam-leak half of the project's **highest-weight strategic risk** has no scheduled
moment at which it can find anything.

**Fix.** Split it: keep steps 1–3 (paper mapping, class count) at v0.1 and re-tag step 4 to the first
milestone at which `:engine` carries logic behind the facade, adding that trigger to step 5 and to
item 41's tag and hook.

### F4-10 — The named CI "Seam architecture test" step runs only `:engine:test`, so half the seam has no legible check · **correction**

**Location:** §4.11 (1613, 1618); §8.1 (1787–1789); §6 (1738); `[D-P1-24]` (2096); §12 item 38.

**Evidence.** The named step runs `./gradlew :engine:test`, which can execute only the two C-1 tests.
C-2 and C-3 live in `:mod` and no named step runs `:mod:test`, so violations of the seam sentence's
second half surface as anonymous failures inside `./gradlew build` — precisely the outcome
`[D-P1-24]`'s own rationale exists to prevent (*"legible at a glance, not buried in an aggregate
build"*). §6:1738 states the CI behavior as fact for all seam violations. Separately, §4.11's second
bullet still calls `:conformance:test` "a no-op until Phase 2" — untrue since round one's F-10 made
C-4 mechanical and placed `SeamConformanceDependencyTest` there; it is now the only step that runs
C-4.

*Distinct from F3-5(2), which is about whether the `:mod` tests can be built and run at all. This
assumes that fixed and is about CI step composition and three false legibility claims.*

### F4-11 — §7 declares the recorder test-only and never shipped, while §4.9.3/§9/§12 ship a live-session recorder at v0.1 · **correction**

**Location:** §7 (1754, 1771, 1775); §4.9.3 (1483); §9 (1874); §12 item 24 (2274).

**Evidence.** §7 asserts `RecordingGLDevice` "allocates freely — it is never in a shipped path" and
that `GLCallLog` is "init-time or test-time". But `-Dschmaloogium.debug.recordGL` is a v0.1,
Phase-1-owned flag that *"wraps the live `GLDevice` in a recorder"* in a real session — per-GL-call
appends on the one hot path §7 itself identifies — and §12 item 24 implements it at v0.1. Neither
§4.9.3 nor §12 states any bound on the log, so an unbounded per-call log over a session is a
memory-growth path nobody has been told to think about, and the live decorator's thread contract is
stated nowhere.

**Fix.** Replace "never in a shipped path" with the truth (the classes ship inside the merged jar per
`[D-P1-4]` and are activated by the flag), give that path a thread row and an explicit posture
(opt-in, allocation-heavy, bounded log with a stated cap or ring buffer, never on by default), and
add the bound to §4.9.3 and item 24.

### F4-12 — §7's blanket "render thread only" facade rule is stronger than the §G2.3 it cites, and forbids the off-thread work DESIGN sanctions for Phase 14 · **correction**

**Location:** §7 lines 1748, 1753.

**Evidence.** The row states the rule without exception and sources it to §G2.3. `DESIGN.md:208`'s
permissible-off-thread list ends with *"shader compilation and texture upload"* under Phase 14's
shared-context design with its mandatory synchronous fallback — and both are facade calls
(`ShaderService.compile`, `TextureService.upload`), the latter exposed in §5.2's pixel-transfer row.
§7 line 1753 reproduces §G2.3's permission list but drops exactly the clause that conflicts with the
row above it: Phases 3 and 11 do no GL, so the omitted Phase 14 clause is the only one that bears on
the facade. §9:1867 schedules Phase 13's uploads at v0.5, the same milestone as Phase 14's work, so
the conflict is not hypothetical.

### F4-13 — The re-pin procedure re-verifies one of fourteen rows and gives no decision rule for a flagged delta · **correction**

**Location:** §4.2.6 (570–596) and §10.1 (1914–1923); the coverage claim at 561; exposed at §5.1:1660.

**Evidence.** I followed the procedure literally. Step 2 is fully executable and I executed it. Step 3
— *"Diff release notes from the current pin forward. **Flag any mention of:** …"* — has no acceptance
criterion: it never says whether a hit blocks the bump, forces extra testing, or is merely recorded,
so two operators can legitimately reach opposite conclusions. And coverage: the procedure supplies a
query URL for the loader row and for no other, while the ASM row instructs *"Re-check with the rest of
the table at each milestone"* and §5.1 exposes the duty to "whoever tags a milestone". I had to find
the Unimined and ASM metadata coordinates myself.

**Fix.** Give each non-loader row its verification coordinate in the Repository column, and make step
3 terminate in a decision.

### F4-15 — §11.2's D-6 row says only C-1…C-3 are test-enforced, contradicting §4.3, §8.1, §8.2 and §12 · **correction**

§11.2:2112 reads *"§4.3 states it as constraints C-1..C-4 and enforces C-1..C-3 with tests"*, while
§8.2:1803 says the opposite in as many words — *"`:conformance` must depend on `:engine` and never on
`:mod`. **Mechanical, like the other three**"* — and §4.3:683, §8.1:1789 and §12 item 14b all name a
test. Draft residue from before round one's F-10; left standing it understates the Doc gate's own
evidence in the table a later reader is most likely to consult. One-cell fix.

### F4-16 · F4-17 · F4-18 · F4-19 · F4-20 · F4-21 — notes

- **F4-16 — Three §3 rows tag DESIGN.md-sourced project rules as `[V:doc]`** (272, 280, 281).
  `RESEARCH.md:30` defines `[V:doc]` as *"Verified against the shipped OptiFine pack-author docs"*.
  The KHR_debug row's provenance is §G4.5, the facade rule's is §G4.6, the vocabulary row's is §G4.1 —
  all project decisions. A Phase 13/14 reader could conclude the pack-author docs require KHR_debug
  labelling. Retag, or declare a `[V:design]` tag in §0 alongside the `[V:repo]` tag the doc already
  coins at 2113 without declaring. *Row 273 is the mirror image — `[V:observed]` where its core claim
  is `[V:doc]` — and can be fixed in the same pass.*
- **F4-17 — §4.5.2's Milestone column tags two of three mixin configs `v0.3` while §9, §12 and
  §4.5.2's own prose all land them at v0.1.** *Two agents, independently.* Line 810 simultaneously
  reads *"**Empty for v0.1.**"* and `v0.3`; §9:1879 tags the same artifact `v0.1` with "first tenant
  expected v0.3" in the note column — the correct shape. §12 item 31 wires the `MixinConfigs` manifest
  naming **all three** at v0.1, so deferring two files to v0.3 would fail config load at runtime — the
  exact failure mode §4.5.2 warns about for the `plugin` key. A second tag for one component also
  breaches §G4.3.
- **F4-18 — Six bare `§x.y` references point at `RESEARCH.md` but resolve against this document.**
  Lines 1094 (`§4.2` — this doc's §4.2 is "The Gradle module split"), 1108 and 1161 (`§6.1` — this
  doc's §6 has no subsections), 832, 2083, 2086. A *different* sub-class from F-7 and F3-10: these are
  correct references with the prefix omitted, so they silently resolve to a real but wrong section —
  and two sit inside the §4.7.4 code block §5.2 sends Phases 4/5/6 to read. The doc gets it right
  elsewhere (line 720 cites "§1.2 non-goals" correctly), so this is a sweep, not a rethink.
- **F4-19 — The "fixed" log-channel list has no channel for Phase 9 or Phase 13**, the only two of
  fourteen phases without one, against the rule that *"every log line goes to exactly one channel"*
  (1464). `DESIGN.md:1225` obliges Phase 9 to warn on unknown ids — a log line with nowhere to go.
- **F4-20 — Phase 13 is the one phase the doc designs *for* without a §1.2 anti-sprawl row or a §11.4
  hand-off**, though §5.2:1675 names it in bold as a consumer and §9:1867 schedules its uploads. §12's
  deferred-verb row at 1225 declines texture readback by asserting how Phase 13's companion-atlas
  construction will work — a claim about another phase's internal design with no hand-off inviting
  Phase 13 to contest it. §G9 calls §1.2 "the anti-sprawl device"; every other named phase has a row.
- **F4-21 — Four small documentation defects:** §5.4:1717 points at §11.4 for the upstream-change
  list, which is in §11.5; §0.1's inputs record omits `RESEARCH.md` §3.2 although §3 row 273 cites it
  (and the sections truly never read — §3.4, App B, App D.1/D.2/D.4 — are precisely where F3-2 and
  F3-8 landed); `schmaloogium.debug.saveSources` is tagged `v0.1` in §4.9.3, called "unimplemented" in
  §11.4, and is the only one of the four reserved flags with no §9 row; and §12 item 1's verb is
  "Verify" while its README/`mod_credits` half is authored by item 28, so a session working the list
  in order hits a failing hook at item 1 (README.md is still the untouched template
  "# CleanroomModTemplate") with no instruction to fix it.

### Also raised and dismissed, for the record

`ScriptedResponses` has `compileFails` and `linkFails` but no `validateFails`, while §6:1731 commits
Phase 4 to a validate-failure behavior at rung 3 and `ShaderService.validate` is a first-class
separately-failing operation. Real, but a one-line addition to §4.7.5 that rides along with any §5
change — recorded here rather than numbered. Likewise `drawBuffers`' encoding of the contract's
"draw-buffers = none" state (`RESEARCH.md:561`) as a zero-length array, which the doc never states
means *none* rather than *unchanged* — one clause on line 1115.

---

## 4. Verdict

**PASS-WITH-CORRECTIONS**

I reach the same verdict as round three, and I want to be precise about what changed underneath it.

**The document is better than round three's list makes it look.** Two of the twelve findings are
refuted outright — one rests on a hypothesis its own author declines to assert while the check it
asks for already exists at §12 item 30, and the other certifies the document correct. Two more are
substantially overstated: F3-3's headline is a **quotation error** against a sentence that is correct
as written, and one of its sub-claims is contradicted three times in `DESIGN.md`; F3-4 faults the doc
for a decision `DESIGN.md`'s own Phase 4 spec had already made and scheduled, which the Phase 1
author was forbidden to read and reproduced correctly anyway. F3-1's facts are all true and its filing
is wrong. Against that, the mandate checks came back strong: the Doc gate passes literally on all
three criteria, all thirteen §G9 sections are substantive (§7 included — I checked rather than
asserted), all four OQ questions are **byte-exact** transcriptions, scope discipline holds in both
directions, no binding decision is contradicted, §G4.2 is clean twice over where the doc resisted a
modernization it could have taken, and the pins have not drifted.

**And it has more §5 debt than three rounds have found.** F3-2 survived a five-way attack and a
steelman and is stronger than written — `DESIGN.md:986` and `:363` put `blendFunc` inside a **v0.1**
dependent's declared scope, which the finding never cites. Beyond it, six new items touch §5: the
facade has no way to observe a per-uniform GL error, so a rung `DESIGN.md:990` assigns to Phase 6 as
v0.1 scope-in cannot be built (three agents, independently); `bindToUnit` bypasses the same cache
F3-3's rule is about, on a service F3-3's fix would not reach; `blit`'s binding contract and
`BlitSpec` are undefined and the depth copies have no stated destination; handles have no
invalidation story across a teardown that an option toggle triggers, and the two backends diverge
exactly where it would hide the bug; and three dependents — 2, 3 and 7 — carry obligations that live
only in §4 and §11. Underneath several of these sits **F4-2**: §5's preamble states a governance rule
§G5.3 does not contain, and five findings across rounds two and three reason from it.

Nothing here requires rebuilding the document, so FAIL would be wrong. §4 is 1,345 lines of genuine
design; the seam, the pin discipline, the OQ spikes and the decision log are the work of a careful
architect operating inside a reading discipline that structurally hid App D.4 and §3.5 from them.
That is the honest frame for this list: **a doc that volunteers claims beyond its assigned reading
inherits the burden of checking them**, and that is where nearly every confirmed defect lives.

### §G1.3 line

**The document is NOT verified.** §G1.3 grants that state on "PASS, or PASS-WITH-CORRECTIONS with
all resolutions recorded and no §5 change outstanding". No resolutions are recorded for rounds two,
three or four, and §5-touching corrections are outstanding. Under §G5.3's gating invariant
`PHASE_1_DOC.md` is **not a valid dependency input** for Phase 2, Phase 3 or any other dependent.

**A fix-up session must run**, applying V2-1 … V2-10, F3-1 … F3-12 **as dispositioned in §1 above**,
and F4-1 … F4-21. Because §5 changes, **a fifth verify pass is required** before any dependent
consumes the doc.

**Three instructions for that fix-up, which are the point of this round:**

1. **Do not apply four items as round three writes them.** F3-3's headline would delete a correct
   sentence carrying the §G4.6 enforcement claim; F3-3's sub-claim 3 would have Phase 1 usurp Phase
   3's mandated ownership map; F3-4's proposed §5 row would misassign the translation to Phase 3 when
   `DESIGN.md:848` gives it to Phase 4, and would reach into pack-format scope; F3-10's second leg
   would repoint §4.2.5 at an item carrying no merge instruction. F3-11 and F3-12 should be closed as
   *no change required*.
2. **Three fixes need widening.** F3-8's mis-citation is at **four** sites, not two (275, 1102, 1195,
   2097, plus 92). F3-5's `:mod` ASM addition needs a resolution note so 9.10.1 wins over Unimined's
   inherited asm-debug-all 5.x, which cannot read Java 25 class files. F3-3's `GlStateManager` fix
   must cover `TextureService.bindToUnit` (F4-3) or it leaves the highest-frequency bypass standing.
3. **Close every §5-touching item in one change**, as round three advised and for the reason it gave —
   but the set is larger than it knew: F3-2, F3-3 (narrowed), F4-1, F4-2, F4-3, F4-4, F4-5, F4-7,
   F4-8, plus the two dismissed one-liners (`validateFails`, `drawBuffers` none) that cost nothing to
   ride along. F3-4 and F3-6 should be closed **without** touching §5 — F3-6 by dropping the dead
   permit, F3-4 by the six-word hedge. Get the whole §5 right once, so the fifth pass has one coherent
   section to attack rather than a sixth partial edit.

### Per-finding §5 disposition

| Item | Severity | Touches §5? |
|---|---|---|
| F3-2 `blendFunc` / `ivec4` | blocking | **yes** — the exposed `UniformService` and 1676's routing |
| F3-3 `StateService` (narrowed to depth-test + fog) | correction | **yes** — on the doc's own round-1 precedent (line 96) |
| F3-5 build files | correction | no |
| F3-1 (demoted), F3-4 (demoted), F3-6 … F3-10 | note | no |
| F3-11, F3-12 | — | no change required |
| F4-1 per-uniform GL-error surface | correction | **yes** |
| F4-2 §5 preamble misattributes §G5.3 | correction | **yes** |
| F4-3 `bindToUnit` cache bypass | correction | **yes** |
| F4-4 Phase 3 ARB obligation absent from §5 | correction | **yes** |
| F4-5 Phase 2 fixture capture absent from §5 | correction | **yes** |
| F4-7 `blit` binding contract / `BlitSpec` | correction | **yes** |
| F4-8 handle invalidation | correction | **yes** |
| F4-14 Phase 7 verification duties | note | **yes** |
| F4-6, F4-9 … F4-13, F4-15 … F4-21 | correction / note | no |

---

*End of PHASE_1_REVIEW_4.md. Per §G1.2 this session stops here and fixes nothing.*

---

## Resolutions

*Recorded by the fix-up session of 2026-07-24 (§G1.3). Nothing above this heading was modified. This
session applied V2-1 … V2-10 and F3-1 … F3-12 as this round dispositioned them — those tables live in
`PHASE_1_REVIEW_2.md` and `PHASE_1_REVIEW_3.md` — and F4-1 … F4-21 below. All three of this round's
instructions to the fix-up were followed: nothing on its do-not-apply list was applied, the three
under-scoped fixes were widened, and every §5-touching item was closed in one change.*

### F4-1 … F4-21

| Finding | Disposition | Where |
|---|---|---|
| **F4-1** no GL-error surface | **Applied, option (a).** `GLDevice.drainErrors()` returns `List<GLError>` (`op`, `subjectLabel`, `kind`, `detail`) — enough to attribute a failure to **one uniform**, which is what rung 2 disables. The backend's `glGetError` cadence is stated as contract (per-call under a debug context or any `-Dschmaloogium.debug.*` flag; per-drain otherwise, with `subjectLabel = "(batched)"`). §6 line 1732's false "as a result" is gone and §6 gained **rung 1 and rung 2 rows** naming where each lives. `ScriptedResponses.glError(...)` makes it headlessly testable. `[D-P1-30]` records why a batched drain beat a per-call status | §4.7.4, §4.7.5, §5.2, §6, §9, §12 items 19–20, 22 |
| **F4-2** §5 preamble misattributes §G5.3 | **Applied.** The sentence is replaced with the real rules — §G1.1's mandatory reading (the whole dependency doc) and its "**Dependency docs are contracts**" bullet — followed by a commitment to keep §5 sufficient on its own anyway, which is the stronger standard the finding says must then be met | §5 preamble |
| **F4-3** `bindToUnit` cache bypass | **Applied as a stated backend obligation**, `[D-P1-29]`: every `Lwjgl3GLDevice` verb whose state `GlStateManager` caches is issued through `GlStateManager`, with the verb list — `bindToUnit` (unit **and** bind), every `StateService` verb but `viewport`, and clears. §12 item 22 carries it as a review check, since no test can catch it. This closes F3-3's sentence and this row together, as the finding required | §4.7.4, §5.2, §12 item 22 |
| **F4-4** Phase 3's ARB obligation absent from §5 | **Applied** as a §5.2 row addressed to Phase 3, phrased as contestable, and explicit that the translation *strategy* belongs to Phase 4 per `DESIGN.md` — the distinction that makes this the right form and F3-4's proposed row the wrong one | §5.2, §11.4 |
| **F4-5** Phase 2's capture mechanism absent from §5 | **Applied.** §5.3's debug-flag row now names **2** for `dumpCapabilities` and `recordGL`, and §5.2's Phase 2 note names `CapabilityProbe` and the recorder as the capture path — "do not design a capture path; drive these" | §5.2, §5.3 |
| **F4-6** unmapped §3.5 inputs; false `[A]` claim | **Applied.** §3 gained a standard-macro-header row (`MC_GL_VERSION`, `MC_GLSL_VERSION`, `MC_GL_VENDOR_*`, `MC_GL_RENDERER_*` → the profile's version pair, `glslVersion`, `vendor`, `renderer`) and an `[A]` row for `maxVertexAttribs`/`maxTextureSize`, so §4.7.2's claim is now true. §5.2 and §11.4 no longer understate the profile to Phase 3 as "extension macros" | §3, §4.7.2, §5.2, §11.4 |
| **F4-7** `blit` / `BlitSpec` / depth destinations | **Applied, with a new verb.** `FramebufferService.copyDepthToTexture(src, dst, region)` is the verb the `depthtex1`/`depthtex2` copies need — they target textures, not attachments, so Phase 5 is not asked to invent destination framebuffers. `BlitSpec` is now defined (rects, attachment mask, filter, NEAREST enforced for depth), and **both** verbs restore the caller's prior bindings. §4.7.5 records that `bindsBalanced()` sees facade-level calls only, which is why the restore is a contract and is logged | §4.7.4, §4.7.5, §5.2, §9, §12 item 19 |
| **F4-8** handle invalidation | **Applied.** §4.7.3 states the lifetime rule, why the LWJGL backend cannot detect misuse (drivers reissue names) and why the recorder can (monotonic, never reused), and names **Phase 5** as the owner of re-acquisition across the uninit/rebuild. `ReplayAssertions.noUseAfterDelete()` added, with the note that `noLeakedObjects()` checks the opposite direction. `[D-P1-28]` | §4.7.3, §4.7.5, §5.2, §8.1, §9, §12 items 18, 20–21 |
| **F4-9** §10.3 drill scheduled where it is vacuous | **Applied by splitting it.** Steps 1–3 stay v0.1 (§12 item 41); step 4 (`NullGLDevice`) moves to the first milestone where `:engine` carries logic behind the facade — v0.3 in practice — as new §12 item **41b**, with step 5's trigger widened and the success criterion tightened so an empty tested set cannot pass | §10.3, §12 items 41, 41b |
| **F4-10** CI seam step runs only `:engine:test` | **Applied.** The named step is now `./gradlew :engine:test :mod:test`; §4.11's "no-op until Phase 2" claim about `:conformance:test` is corrected (it runs C-4); §6's build-time row and §12 item 38's hook now require a deliberate violation of **each** of C-1 … C-4 to turn a *named* step red | §4.11, §6, §12 item 38 |
| **F4-11** recorder declared test-only but ships | **Applied.** §7 states the truth — the classes ship inside the merged jar and the flag activates them live — plus the posture (opt-in, off by default, allocation-heavy) and a thread row for the live decorator. `GLCallLog` gained `bounded(capacity)`/`droppedCallCount()`, §4.9.3 and §12 item 24 carry the default bound (100 000 calls, oldest discarded and counted) | §4.7.5, §4.9.3, §7, §12 item 24 |
| **F4-12** §7's thread rule stronger than §G2.3 | **Applied.** §7 now carries the clause it had dropped: shader compilation and texture upload may run off-thread under Phase 14's shared-context design with its mandatory synchronous fallback. §11.4's Phase 14 hand-off says so too | §7, §11.4 |
| **F4-13** re-pin procedure | **Applied, both halves.** Every row of §4.2.6 now carries the coordinate its value is verified against, and step 3 terminates in one of three rulings — record only / extra verification (with the check named per category) / block the bump — with the tie-breaker that ambiguity itself selects the conservative branch. §10.1 mirrors it and adds the sweep of the remaining rows | §4.2.6, §5.1, §10.1 |
| **F4-14** Phase 7's verification duties | **Applied** — §5.3's mixin row now carries both duties (refmap generation unexercised in this checkout, and the inherited `compatibilityLevel: "JAVA_8"`), modelled on the ModularUI row. §11.3 notes that it does | §5.3, §11.3 |
| **F4-15** §11.2's D-6 row | **Applied** — "enforces C-1..C-4 with tests", with the per-constraint test named | §11.2 |
| **F4-16** `[V:doc]` on project rules | **Applied.** §0.1 now declares `[V:design]` and `[V:repo]` (the latter was already in use, undeclared) and restates what `[V:doc]` means. The KHR_debug, facade-rule and vocabulary rows are retagged `[V:design]`; the `centerDepthSmooth` row is retagged `[V:doc]` with its stall behaviour separately marked `[V:observed]` | §0.1, §3 |
| **F4-17** mixin-config milestone contradiction | **Applied** — all three config files are `v0.1` with "first tenant expected v0.3" in the note, matching §9 and §12 item 31, and a paragraph states why the tag is on the file rather than its first tenant | §4.5.2 |
| **F4-18** six bare `§x.y` references | **Applied**, and swept wider than the six: every remaining reference of this class in §4.7.4's code block, §4.5.2, §11.1 and §11.3 now carries its `RESEARCH.md` prefix | throughout |
| **F4-19** no channel for Phase 9 or 13 | **Applied** — `schmaloogium.ids` (9) and `schmaloogium.textures` (13) added, with a sentence stating that the list covers all fourteen phases because "exactly one channel" is otherwise unsatisfiable | §4.9.2 |
| **F4-20** Phase 13 has no §1.2 row or hand-off | **Applied** — Phase 13 gains a §1.2 anti-sprawl row and a §11.4 hand-off, and §4.7.4's texture-readback row no longer asserts how Phase 13's atlas construction will work: it names the expectation as Phase 1's guess and invites the additive request | §1.2, §4.7.4, §11.4 |
| **F4-21** four documentation defects | **All four applied.** (a) §5.4 now points at §11.5. (b) §0.1's inputs record adds RESEARCH.md §3.2 and App D.1/D.3, and states plainly which sections the build session did **not** read — the ones where F3-2 and F3-8 landed. (c) `saveSources` now reads consistently in §4.9.3, §9 (new row: name reserved at v0.1) and §11.4 (the dump is Phase 3's). (d) §12 item 1 now separates verifying `LICENSE` from authoring the README/`mod_credits` half, and points at item 28 | §0.1, §4.9.3, §5.4, §9, §11.4, §12 item 1 |
| **Also raised and dismissed** — `validateFails`, `drawBuffers`' zero-length "none" | **Both applied** as the ride-alongs this round suggested: `ScriptedResponses.validateFails(...)` added, and `drawBuffers`' zero-length array is now documented as the contract's "draw-buffers = none" rather than "unchanged" | §4.7.4, §4.7.5 |

### The three instructions, discharged

1. **Nothing on the do-not-apply list was applied.** F3-3's headline, F3-3's sub-claim 3, F3-4's §5
   row and F3-10's second leg were all rejected for the reasons this round gave; F3-11 and F3-12 were
   closed as *no change required*. Each rejection is recorded in `PHASE_1_REVIEW_3.md`'s resolutions
   rather than left silent.
2. **The three under-scoped fixes were widened.** F3-8 was applied at five sites, not two; F3-5's
   `:mod` ASM addition carries the forced-resolution note against Unimined's `asm-debug-all` 5.x; and
   F3-3's `GlStateManager` fix covers `bindToUnit` via F4-3.
3. **Every §5-touching item was closed in one change** — F3-2, F3-3 (narrowed), F4-1, F4-2, F4-3,
   F4-4, F4-5, F4-7, F4-8, F4-14, plus both ride-alongs. F3-4 and F3-6 were closed without touching
   §5.

### §G1.3 line

**The document is still NOT verified, and that is the expected outcome of this session.** §5 changed
— the facade gained an `ivec4` upload, `depthTest`/`fog`, `copyDepthToTexture`, `drainErrors()` and
`noUseAfterDelete()`, and §5 gained rows for Phases 2, 3 and 7 — so §G1.3's "re-verify only if §5
changed" rule fires and a **fifth verify session** must run before Phase 2, Phase 3 or any other
dependent consumes `PHASE_1_DOC.md` (§G5.3). What has changed is that the fifth pass inherits a
document with **no outstanding findings and a complete resolution record** for rounds one through
four, and one coherent §5 to attack rather than a partial edit.
