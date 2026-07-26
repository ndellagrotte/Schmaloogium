# PHASE_1_DOC.md — Verify session, round nine

**Session type:** verify (DESIGN.md §G1.2) · **Subject:** `docs/phase1/v10/PHASE_1_DOC.md` as of the
fifth fix-up (§0.8) · **Date:** 2026-07-25 · **Verdict:** §3.

**Why this round exists.** The fifth fix-up applied `PHASE_1_REVIEW_8.md`'s V8-1 … V8-6 and altered
**§5.2 at two rows** — the GL-error row (l. 2543) and the non-verbs row (l. 2552) — so §G1.3's
*"re-verify only if §5 changed"* rule fired. Until this verdict exists `PHASE_1_DOC.md` is not a
valid dependency input (§G5.3).

---

## 0. What I read, and in what order

The brief's order, followed literally. Reading the resolutions early turns an independent reader into
an auditor of someone else's reasoning, which is what §G1.2 exists to prevent, so step 5 stayed last.

1. **`DESIGN.md` Part I in full** (§G0–§G10, ll. 1–575) and the **Phase 1 spec** (Part II,
   ll. 585–658). Other phases by title and §G5.1 row only, except the disclosed ownership reads below.
2. **`RESEARCH.md` §0** (reading guide, confidence tags) and **§1**, then the spec's Required inputs:
   **§5.1–§5.3, §6.1, §7.2, §12.2**.
3. **Template ground truth, complete:** `build.gradle`, `settings.gradle`, `gradle.properties`,
   `gradle/scripts/{dependencies,extra,publishing}.gradle`,
   `gradle/wrapper/gradle-wrapper.properties`, all eight files under `src/**`,
   `.github/workflows/{build,release,release-to-cf-mr}.yml`, `README.md`.
4. **`PHASE_1_DOC.md` §0 → §12.** The new surface listed in the brief was read at full fidelity;
   §10's four spike specs were read by structure only (see the deviations below).
5. **Last, after my own findings were formed:** `PHASE_1_REVIEW_8.md` in full **including its
   `## Resolutions` section**, plus targeted greps across rounds one to seven for specific claims.

### 0.1 Read beyond the assigned list, each because a finding turns on it

- **`RESEARCH.md` §4.2** (program-registry mechanics, ll. 491–508) — the "~90 built-in uniforms" and
  "43 program slots" figures V9-1's cost limb turns on, and the `refreshes`/`evaluates` verb contrast
  bearing on V9-2.
- **`RESEARCH.md` §4.3 and §4.4** (ll. 509–566) — the per-frame flow V9-1 turns on: where foreign GL
  sits relative to Phase 6's sweeps, and §4.4's exact words about world-state sampling.
- **`RESEARCH.md` App A.1/A.2** (ll. 1101–1155) — the composition of the 43 slots, for V9-1's
  arithmetic note.
- **`RESEARCH.md` App D end to end (D.1–D.4) and its cadence note** (ll. 1318–1382) — the reading
  V9-2 and N1 turn on.
- **`DESIGN.md` Part II, the *Scope — in* / *Scope — out* bullets of Phases 3, 4, 5, 6 and 7** — the
  read V9-5 and the §2 clearance of V8-2 both turn on. Disclosed rather than assumed: §G1.1 bars a
  *build* session from other phases' specs, and the four-round precedent (rounds five through eight
  each relied on it and each disclosed it) is that a session auditing an **ownership claim** may read
  the spec that settles the ownership. Phase 4's spec is in that set because it turned out to carry
  the strongest citation on the point — see N2.
- **`git log` / `git status` / `git show HEAD:PHASE_1_DOC.md`**, read-only — the reconstruction the
  signature-invariance check rests on (§2).

### 0.2 Deviations, and omissions recorded as omissions

1. **The fix-up sessions' own transcripts (`Schmaloogium/*.txt`) were deliberately not read.** They
   are on disk and would have given an exact pre-fix-up snapshot of §4.7.4. §G1.2 bars a reviewer
   from the author's conversation context precisely because it transmits the author's blind spots, so
   the byte-for-byte claim was reconstructed instead and the limits of the reconstruction are stated
   in §2 rather than papered over. **One sub-agent read one such transcript contrary to instruction;
   its transcript-derived conclusion is discarded and appears nowhere in this review.** The same
   conclusion — that §0.7's round-seven arguments were appended to, not rewritten — was then
   re-derived from permitted sources (§2).
2. **§10's spike specs were read by structure, not re-derived.** Round eight's clean list covers "the
   four OQ spike specs", and §G4.4 conformance was confirmed twice already. Recorded as a scoped read.
3. **`cleanroom-src/` was not read.** The spec lists it "skim only" and nothing in this round's
   findings turned on loader-internal layout. Same disposition §0.3 item 1 records for the build
   session.
4. **Adversarial sub-agents were used, and the choice is disclosed.** §G1.2 permits them; round eight
   declined on the reasoning that a brief requiring each load-bearing quote to be re-derived by the
   reviewer makes a delegated finding a hop rather than a saving. That reasoning is available but not
   binding, and the project owner directed their use for this round. It was mitigated by a hard
   **re-derivation gate: no sub-agent claim entered §1 or §2 until I had opened the cited file myself
   and confirmed the quote at its line.** The gate earned its keep — of the candidate defects
   returned, several did not survive my own derivation and are recorded in §2 as examined-and-cleared
   rather than promoted, and one (V9-4's "dangling cross-reference") I judged a misreading and
   dropped.
5. **No code, no builds, no tests, no fixes.** Exactly one file created: this one. `PHASE_1_DOC.md`
   and all eight prior review files, including their `## Resolutions` sections, are unmodified.

### 0.3 Network use — the two sanctioned purposes, no third

1. **The pin table (§4.2.6's procedure, run).** `repo.cleanroommc.com/.../maven-metadata.xml` reports
   `<release>0.6.6-alpha`, `<latest>0.6.6-alpha`, `<lastUpdated>20260724133703`. The GitHub releases
   API reports `0.6.6-alpha` published **2026-07-24T13:37:05Z** as newest, then `0.6.5-alpha`
   (2026-07-24T01:30:51Z), `0.6.4-alpha` (07-23), `0.6.3-alpha` (07-22), `0.6.2-alpha` and
   `0.6.1-alpha` (07-20), `0.6.0-alpha` (07-19). Both endpoints agree; **no drift; the pin stands.**
   **Which sample this is, stated because the brief asked:** I read at **~04:48 UTC on 2026-07-25**.
   Round seven read at ~03:05 UTC and round eight at ~03:52 UTC the same day, so this is a
   **56-minute-later re-confirmation, not an independent daily sample** — the third observation inside
   one two-hour window. It does carry one fact neither predecessor could have: `lastUpdated` is
   unmoved at 2026-07-24T13:37Z, so the *daily* cadence RESEARCH.md §5.2 records has now gone **over
   fifteen hours** without a release. That is mild evidence about volatility, not a second sample.
2. **The cited GL pages.** Both URLs the fifth fix-up put inline resolve. `docs.gl/gl4/glGetError` and
   `docs.gl/gl2/glGetError` return the passage **word for word identical to each other and to
   §4.7.4's block quote (ll. 1873–1877)**, including the conditional tail *"if all error flags are to
   be reset."* The "identical wording" claim (l. 1878–1879) **holds**. This is the third consecutive
   round to verify the quotation at source and the first to verify both URLs the document now carries.

---

## 1. Findings

**Six corrections, five notes, zero blocking.** Every one lands in the fifth fix-up's new prose or in a
site that prose makes a claim about — which is §0.7's standing lesson holding for the third consecutive
round.

---

### V9-1 — the foreign-GL hand-off overstates what its remedy buys, calls it uniquely sound against the same paragraph's own concession, offers a remedy the facade cannot express, and misprices the cost of the case it hands off · **correction** · **touches §5: yes**

**Location.** §11.4 ll. 3154–3167 (the new Phase 7 hand-off); §4.7.4 ll. 1913–1919; §5.2's GL-error
row l. 2543; §0.8's V8-1 bullet l. 375. Cost statements affected: §4.7.4 l. 1940, §7 ll. 2666–2669,
`[D-P1-32]` l. 3044.

**Claim under test.** That "one unconditional `drainErrors()` at a frame-driver-defined point …
**absorbs foreign errors at a known boundary** for roughly one extra query per frame" (ll. 3163–3165)
is **"the only sound remedy"** (l. 3162; "The one sound remedy", l. 1917; "the owner of the only sound
remedy", l. 2543) for the case the same paragraph states: "a foreign GL error occurring between two of
Phase 6's sweeps survives into a window Phase 6 will read" (ll. 3158–3159).

**Evidence.** Four separate problems, each derived from the document and its cited sources.

*(a) It is not the only sound remedy, and the paragraph four lines above says so.* §4.7.4 ll. 1913–1916:
*"**Dropping the elision would bound the window against all GL**, and it is deliberately not done: it
would pay a factor of two on a synchronous driver query across 43 program switches per frame … to
relabel a case the replay already contains."* That is a **cost** rejection of a remedy the sentence
concedes is effective, stated in the same breath as "The one sound remedy is …". §0.8 l. 365–366
repeats the concession verbatim, and `[D-P1-30]` l. 3042 states the complement ("the elision cannot
bound a window against vanilla's own GL"). The document therefore describes at least two sound
remedies and names the second one "the only" one. I checked whether discarding the leading drain's
return value breaks its soundness: it does not. Phase 6's protocol is *"drain, upload the program's
uniform set, drain — and **only if that drain is non-empty**, re-upload"* (l. 2543), so the leading
drain's contents are discarded — but the *flag* is cleared, which is the property at stake, and the
trailing window then holds only our uploads. Losing a record loses noise; it does not create
unsoundness. The rejection of a facade-internal guard, by contrast, is correct and rests on mechanism
rather than cost: *"the facade **cannot observe non-facade GL**"* (ll. 1916–1917).

*(b) The remedy does not address the case it is attached to, and the reduction is argued nowhere.*
The residue is a foreign error "between two of Phase 6's sweeps". Foreign GL is interleaved with those
sweeps **throughout** the frame, not concentrated at a boundary: `DESIGN.md` §G3.2 ll. 281–288 puts
"GBUFFERS phases with per-phase program dispatch" mid-frame and adds "Uniforms refresh at every
program switch (Phase 6)"; RESEARCH.md §4.4 ll. 540–546 enumerates the gbuffers chain — "sky → terrain
solid/cutout-mipped/cutout → damaged-block → entities … → clouds → weather" — each element separated
by vanilla draw work; and §3's second row (l. 669) says that geometry is "drawn by Minecraft's own
draw calls through Phase 7's hooks, which never reach the facade". A drain placed **once** at a
frame-driver point is not between sweep *N* and sweep *N+1* for any interior *N*. It bounds one gap
per frame — the one that spans the frame boundary — and leaves every interior gap exactly as it was.
The honest statement available is "prevents leakage across the frame boundary into the first sweep of
the next frame". What the document writes instead ("absorbs foreign errors at a known boundary",
offered as *the* remedy for the between-sweeps case) reads as a general fix, and **no site argues
otherwise**: I searched §4.7.4, §5.2, §6, §7, §11.4, `[D-P1-30]` and §0.8, and "absorb" occurs
exactly once in the document, at l. 3163, as a bare assertion.

*(c) "Unconditional" is not expressible through the facade this document supplies.* `drainErrors()`
takes no argument (l. 1673), and the elision is **contract**, not a hint: *"A drain with nothing to
observe issues no query at all … when it is clear `drainErrors()` returns empty without touching the
driver"* (ll. 1890–1892), with §12 item 22's review hook requiring "that a drain after a drain issues
no query" (l. 3297) and `[D-P1-32]` promising rung 2 needs "no additional verb" (l. 1933). So in
exactly the configuration the remedy targets — foreign GL arriving after our last mutating facade
call, with no facade mutation since the previous drain — Phase 7's "unconditional" drain **elides,
returns empty, clears no GL flag, and the foreign error survives into the next frame anyway**. In
practice a preceding facade call will often arm the bit, so it will often work; but by accident of
surrounding traffic rather than by construction, and the precondition is stated nowhere. Note the two
claims are entangled: when the drain elides, the "roughly one extra query per frame" cost is also zero.

*(d) The cost of the case is mispriced, and the mispricing runs against the decision it defends.*
§11.4 l. 3161 says "What is *not* contained is the noise." It is not only noise. A non-empty trailing
drain triggers the full replay — "re-uploads the set **draining between uploads**" (l. 2613) — and a
program switch refreshes **~90 built-in uniforms** (RESEARCH.md l. 504). So one *recurring* foreign
mid-frame error costs on the order of ninety extra synchronous queries plus ninety redundant uploads
**per frame**, and disables nothing. Three sites price the replay as the opposite: "The replay is paid
on the frame that is about to disable something, **once**" (l. 1940); "the per-upload cadence is
entered only after a drain has already come back non-empty, **on a frame that is about to disable a
uniform once**" (ll. 2667–2669); "entered only on a frame that is already about to disable a uniform,
**once**" (l. 3044). All three are true of the `OUT_OF_MEMORY` case they were written for and false of
the foreign-error case V8-1 introduced. This matters because the elision was **kept** on a cost
argument (+43 queries/frame rejected) whose ledger omits a cost the elision **creates** that can
exceed it. Including it can invert the comparison, and the comparison is the whole basis of the
decision.

*Arithmetic note, recorded rather than raised as its own finding because it is round-seven-era prose
that rounds seven and eight both passed:* "43 program switches per frame" (l. 3164; "forty-three",
l. 376) converts a **registry cardinality** into a per-frame event count. RESEARCH.md l. 493 says "43
program **slots**", and App A.1 l. 1142 says "43 slots **incl. the 2 virtual programs and the
16-element deferred/composite arrays**" — the two `*_pre` slots are "*(virtual — flip control only)*"
(ll. 1134, 1138) and the array slots fall back to "*(none — pass skipped)*", so a real pack binds a
fraction of 43, while §4.4 l. 551's "push/pop program semantics around leash/glint rendering" lets a
slot bind more than once. §7 l. 2664–2666 is careful ("there are 43 **slots**"); §11.4 and §0.8 are
not. The direction of the residual error happens to favour the conclusion being defended.

**Fix shape** (the fix-up's call, not mine). Replace "the only/one sound remedy" with what is true —
"the only remedy available at this phase's cost target", or "the only remedy outside this facade's
reach" — at all four sites (ll. 1917, 2543, 3162, 375). Narrow the benefit claim to what a boundary
drain actually bounds, or state the interior gaps as unaddressed. Either give Phase 7 a way to force a
query, or say that the hand-off requires a mutating facade call to precede the drain. And carry V8-1's
consequence into the three cost statements, so the replay is no longer priced as a once-per-disable
event.

**Touches §5: yes.** §5.2's GL-error row states the uniqueness claim in its own words and names an
owner on the strength of it — "§11.4 names Phase 7 as the owner of **the only sound remedy** (an
unconditional drain at a frame-driver point)" — in a row the row itself declares "contract, not
implementation detail", consumed by "**6** (rung 2 is its v0.1 scope-in), 4, 5, 14". Correcting §4.7.4
and §11.4 while leaving §5.2 would leave the contract section asserting what the design sections had
retracted.

---

### V9-2 — §0.8 and the `Resolutions` table both claim "every site now says 'mutating **facade** call'"; five sites do not, two of them inside §4.7.4 and one inside the `GLError` signature block · **correction** · **touches §5: no**

**Location.** The claim: §0.8 ll. 371–372 and `PHASE_1_REVIEW_8.md`'s `Resolutions` row for V8-1
(l. 503). The surviving sites: §2.4's key-type table l. 630; the `GLError` javadoc ll. 1805–1806;
§4.7.4's `[D-P1-32]` prose ll. 1929 and 1935; `[D-P1-32]` itself l. 3044.

**Claim under test.** *"§4.7.4's \"and the window is correctly bounded\" is deleted, **every site now
says \"mutating facade call\"**, and the consequence is stated once at §4.7.4 and carried into §5.2 as
contract."*

**Evidence.** The first and third clauses hold. `correctly bounded` survives only as the quoted-and-
deleted string at l. 371, and I confirmed by grep that no site carries it live. The middle clause is
false at five sites:

| Line | Section | Surviving wording |
|---|---|---|
| 630 | §2.4 key types | "Attribution is per **drain window** — one call named when the window held one, the sweep named when it held many" |
| 1805–1806 | `GLError` javadoc, §4.7.4 | "attributable to the DRAIN WINDOW that produced it — **and therefore to one call when the window held exactly one** (`[D-P1-32]`)" |
| 1929 | §4.7.4 `[D-P1-32]` prose | "a window holding exactly one **mutating call** yields a record naming that call" |
| 1935 | §4.7.4 `[D-P1-32]` prose | "the leading drain elides its query because **nothing mutating** has happened since the previous drain" |
| 3044 | `[D-P1-32]`, §11.1 | "**`[D-P1-30]`'s backend elides a drain with nothing behind it**" (in bold) |

The `GLError` javadoc is the load-bearing one. It states as **unconditional** the exact entailment
V8-1 denies, and it sits inside the signature block §5.2 cites as normative ("§4.7.4 signatures",
l. 2542) and §12 item 19 verifies against. l. 1929 is the same sentence §5.2 (l. 2543) and
`[D-P1-32]` (l. 3044) both carry **with** "facade" in bold — so the document says both forms of one
sentence, thirty lines apart, one of them corrected.

**The mechanism of the miss is visible, and it is the fix-up's own lesson.** No site in any `Where`
column falls inside ll. 1654–1818, because §0.8 correctly claims no signature changed — so the javadoc
*inside* the signature block was never swept. The fix-up's stated lesson is *"A review names the sites
its finding turns on; a fix-up owes the sites its edit turns on, and that is always the larger set"*
(ll. 460–461); round eight named six sites, the fix-up edited nine, and five more remain. The lesson
is right and the sweep it prescribes is still short.

**Touches §5: no.** §5.2 and `[D-P1-32]`'s headline both carry the corrected form; the defect is that
§4 and §2.4 now contradict §5. The fix is in §4.7.4, §2.4 and §11.1.

---

### V9-3 — §4.2.3's Gradle class-scope mechanism carries no provenance and no source anywhere, and its `ext`-indirection clause implies a workaround that does not exist · **correction** · **touches §5: no**

**Location.** §4.2.3 ll. 837–856 (rewritten by V8-5); §12 item 4b l. 3268; §0.8's V8-5 bullet
ll. 416–429.

**Claim under test.** That "an applied script plugin is compiled into its *own* class scope, so a
class declared in it is not resolvable by simple name from the applying script, and the literal
`new SeamClasspathArguments(...)` form all three code blocks use **would not compile without an
`ext`-indirection this document does not specify**" (ll. 846–849) — and that `buildSrc` and an
included build with a precompiled script plugin are **"equivalently"** homes that put the class on
every project's buildscript classpath **"the same way"** (ll. 843–845).

**Evidence.** Four parts.

*(a) Provenance: none, and none anywhere.* The only tag in the paragraph is `[V:template]` at l. 852,
and it covers the **file facts** only — which check out: `build.gradle` ll. 100, 238 and 239 are the
three `apply from:` sites, exactly as claimed. The **mechanism** carries no tag. §0.1's input table
(ll. 21–38) lists no Gradle documentation of any kind; §0.3 item 2 scopes the build session's web use
to "the OQ-2 re-pin only" (ll. 66–67); §0.8 ll. 448–450 records that this fix-up made "**One network
fetch** … `https://docs.gl/gl4/glGetError` … No other network use." Grepping the document for
`docs.gradle.org` returns nothing; `gradle.org` returns only two version-pin URLs. There is no
`[V:web]`, no `[U]`, no `[A]`, no §11 open-question row. Under RESEARCH.md §0.2 l. 36 this is textbook
`[U]` — "*Unverified claim originating from AI reasoning … every `[U]` must have an open-question row
(§11) or be upgraded*" — and it has neither. **This is structurally identical to what round seven
caught at `[D-P1-30]`** ("the GL semantics claim carried no provenance tag and no source anywhere in
the document, in a decision written specifically to correct an earlier GL error", §0.7 ll. 265–267):
V8-5 was a correction *about* a Gradle mechanism whose fix rests on a Gradle mechanism claim with the
same gap.

*(b) The claim is true — and true for a stronger reason than the one given.* It holds for a class
declared inline in `build.gradle`, for a class declared in an `apply from:` script, and `buildSrc`
does put its `main` output on every project's buildscript classpath. But the decisive reason is
**compilation order**, not class-scope isolation: the applying script is compiled in full before it
executes, and `apply from:` is a runtime statement, so the name is unresolvable at *compile* of the
applying script, before the applied script exists in any form. The document gives the weaker half of
its own argument.

*(c) Which is why the `ext` clause is wrong, and wrong in the direction of understating the case.*
"Would not compile **without** an `ext`-indirection" asserts that with one it *would* — i.e. the
printed call site survives and only unstated wiring is missing. No indirection rescues the literal
`new X(...)`: exporting a `Class` requires `ext.X.newInstance(...)`, exporting a factory closure
requires a call with no `new` at all. **Every workaround changes the call site in all three blocks**
(ll. 830, 931, 986 — which I verified are byte-identical three-argument `new SeamClasspathArguments(…)`
calls, with no `import` in any of them). The true statement is stronger: the literal form cannot be
made to work through `apply from:` at all. As written, the sentence tells an implementation session
that a small unspecified fix would rescue the printed code. **Answering the brief's question directly:
`ext`-indirection is not correctly characterised.**

*(d) Two smaller defects in the same paragraph.* "Equivalently … **the same way**" (ll. 843–845)
overstates: `buildSrc` is unconditional and automatic; an included build's plugin classes reach a
project's script scope only if that build is wired in `settings.gradle` **and** the project actually
applies the plugin. And the paragraph faults the rejected option for "an `ext`-indirection this
document does not specify" while leaving its own chosen option's requirement unspecified — the literal
form needs `SeamClasspathArguments` in `buildSrc`'s **default package**, or an `import` in all three
blocks, and neither is stated anywhere. Separately, **§12 item 4b is the more accurate of the two
texts**: "does not export the class to the applying build script, so `new SeamClasspathArguments(...)`
**will not resolve**" — flat, with no `ext` escape hatch and no "by simple name" hedge. The fix runs
§4.2.3 → item 4b, not the reverse.

**Touches §5: no.** §4.2.3 and §12 item 4b only. §5.1's structural rows do not reach the buildscript
mechanism.

---

### V9-4 — §3's `scale.<prog>` row assigns rectangle computation to Phase 5 with no provenance, and §0.8 cites that row as the settled precedent for V8-2's retarget · **correction** · **touches §5: no**

**Location.** §3 l. 672; §0.8's V8-2 bullet ll. 384–385.

**Claim under test.** §3 l. 672: "`scale.<prog>` — per-program sub-viewport … **computing the
rectangle from the scale factor is Phase 5's**." And §0.8's use of it: the retarget is right because
it is "a distinction this document already made correctly one row later, where §3's `scale.<prog>` row
separates computing the rectangle (Phase 5's) from executing the pass".

**Evidence.** `DESIGN.md` assigns `scale.<prog>` to a chain that does not include Phase 5. Phase 7's
*Scope — in*, part (a), l. 1047: "**Composite/final execution**: … `scale.<prog>` sub-viewports
[v0.5]". Phase 4's *Scope — in*, l. 855: "Per-program state application semantics: DRAWBUFFERS routing
validation, **scale/flip storage**". Phase 3's, l. 769: "per-program render-state overrides
(alphaTest/blend/**scale**/flip/enabled — stored; applied by Phase 4)". Phase 5's *Scope — in*
(ll. 895–925) has eight bullets — main FBO, clear rules, formats, depth textures, shadow FBO, sizing,
growth posture, the App B.3 unit map — and **no per-program sub-viewport bullet at all**, which is the
identical absence argument V8-2 used against Phase 5 for the instancing loop. Phase 5 does own buffer
*sizing* ("display size × render-quality multiplier; `superSamplingLevel`", l. 915), so a rectangle
could be *derived* from something it owns; that makes the attribution arguable, not sourced. It carries
no citation, and §11.3/§11.5 raise no doc-vs-doc conflict about it, which §G1.1 requires of a
disagreement with `DESIGN.md` rather than a silent ruling.

**Why it belongs to this round rather than to the swept past.** The row is old and untouched. What is
new is that §0.8 promotes it to **evidence** — the fix-up's supporting argument for a §5-touching
retarget cites as "correctly made" a sibling attribution that is itself unsourced and in tension with
Phase 7's *Scope — in*. Round eight's meta-finding was that a review's supporting argument is not
evidence; this is the same failure one level down, in a fix-up's supporting argument. Either the
computing/executing seam is real — in which case the row needs its citation — or it is not, in which
case the argument built on it needs a different support.

**Touches §5: no.** Neither §5.1 nor any §5.2 row mentions `scale.<prog>`. The fix is §3 l. 672,
§0.8's supporting clause, and a §11.3 or §11.5 entry if the conflict is judged real.

---

### V9-5 — precondition (ii) is still written as the `OUT_OF_MEMORY` corner at §4.7.4 and in `[D-P1-32]`, while three sites now say it is "load-bearing in general rather than an `OUT_OF_MEMORY` corner" and delegate the foreign-GL containment to it · **correction** · **touches §5: no**

**Location.** §4.7.4's precondition (ii), ll. 1963–1968; `[D-P1-32]`'s statement of it, l. 3044. The
sites that delegate to it: §4.7.4 l. 1910, §5.2 l. 2543, `[D-P1-30]` l. 3042, §11.4 ll. 3159–3161,
§0.8 ll. 372–375.

**Claim under test.** That the containment five sites rely on is recorded in the decision that
supplies it. §4.7.4 l. 1910: "**this is why `[D-P1-32]`'s second precondition is load-bearing *in
general* rather than an `OUT_OF_MEMORY` corner**". `[D-P1-30]` l. 3042: "it is `[D-P1-32]`'s
unattributable branch — not the bit — that contains the case".

**Evidence.** §4.7.4's own precondition (ii), fifty-three lines below the sentence that widens it,
still reads: "**The replay assumes the error reproduces, and says what happens when it does not.**
`GLErrorKind.OUT_OF_MEMORY` is the kind that need not recur. If the replay comes back clean, Phase 6
has a detected failure it cannot attribute…" — one cause, named, and no mention of non-facade GL.
`[D-P1-32]`'s version is the same: "a replay that reproduces nothing (`OUT_OF_MEMORY` need not recur)
is **unattributable** and falls to §6's 3→4 row rather than no-op'ing". **§5.2's (ii) was widened and
these two were not:** l. 2543 reads "(ii) If the replay comes back clean — `OUT_OF_MEMORY` need not
recur, **and per the cadence note below the error may not have been ours at all** — the sweep is
**unattributable**". The `Resolutions` table claims the widening for §5.2 only ("(ii) widened",
l. 503), so no named site was missed; the defect is that the contract section is now ahead of the
design section and the decision log on the same precondition, and `[D-P1-30]` points at a branch that
does not know what it is being asked to contain. A future session narrowing (ii) to the OOM corner —
which its own text invites — would silently delete the containment four other sites assert.

**Touches §5: no.** §5.2 already carries the general form. The fix is §4.7.4 ll. 1963–1968 and
`[D-P1-32]` l. 3044.

---

### V9-6 — the round-eight fix-up's `[fix-up: …]` marker record is incomplete, and `[D-P1-30]` still attributes to round seven a sentence carrying round eight's edit · **correction** · **touches §5: no**

**Location.** `[D-P1-30]` l. 3042; `[D-P1-32]` l. 3044; `[D-P1-35]` l. 3047.

**Claim under test.** The document's own accountability standard: §0.6's V6-1 (ll. 162–165) treats
"this document carried no §0.6 and **no `[fix-up: PHASE_1_REVIEW_5.md …]` marker**" as evidence that a
fix-up never ran. Sixteen decisions carry markers.

**Evidence.** `PHASE_1_REVIEW_8.md` appears in exactly **one** marker in the document —
`[D-P1-35]`'s: `[fix-up: PHASE_1_REVIEW_7.md V7-1; PHASE_1_REVIEW_8.md V8-2]`. The other two
round-eight-amended decisions name round eight in their prose and not in their markers:

- **`[D-P1-30]`** carries two round-eight edits — V8-1's "**That bit tracks *facade* calls while the
  GL error flag is per-context**…" clause and V8-4's two inline `docs.gl` URLs — and its marker is
  `[fix-up: PHASE_1_REVIEW_4.md F4-1; PHASE_1_REVIEW_5.md V5-1; PHASE_1_REVIEW_6.md V6-2;
  PHASE_1_REVIEW_7.md V7-2, V7-3]`. Compounding it, the sentence now carrying V8-4's URLs is still
  labelled "**The cadence is now stated in GL's own terms** (round seven)".
- **`[D-P1-32]`** says in its own text "is **deleted** (round eight, V8-3)" and its marker is
  `[fix-up: PHASE_1_REVIEW_5.md V5-1; PHASE_1_REVIEW_6.md V6-2, V6-3; PHASE_1_REVIEW_7.md V7-2, V7-3,
  V7-4]` — no round eight.

By the document's own V6-1 standard, the marker record does not show that round eight's fix-up reached
anything except `[D-P1-35]`.

**Touches §5: no.** §11.1 only.

---

### V9-7 — §5.2's non-verbs row is headed "each with the phase that would request it", and Phase 6 — newly added to it — requests nothing absent · **note** · **touches §5: yes if fixed**

**Location.** §5.2 l. 2552; §4.7.4's absent-verbs table header l. 2012; §0.8 ll. 469–471.

**Claim under test.** That the fifth fix-up's change to the row's consumer list — §0.8 ll. 469–471,
"removing Phase 5 and adding Phase 6" — leaves a set the row's own stated semantics can carry.

**Evidence.** The row's subject is "**The facade's stated non-verbs**, each with the phase that would
request it", and §4.7.4's table column is "Who requests it". Phase 6 requests no absent verb: the
`instanceId` upload runs through `void upload(UniformLocation loc, int v)` (l. 1695), which is
**served**, and Phase 6 is already named as a consumer on the served rows at ll. 2543 and 2551. The
ownership statement is substantively right (see §2) and it is in the wrong row: the consumer column
now mixes "who would request this absent verb" with "who owns an adjacent served entry point". Whether
that breadth is worth having is the fix-up's call — but if it is, the same breadth readmits Phase 5,
which §0.8 removed and which still owns the buffer estate the composite loop's N draws run inside
(`DESIGN.md` ll. 897–900's read/write/flip law), a stake §4.7.4 l. 2022 and §3 l. 668 both keep in
prose while §5.2 — the section that declares itself "sufficient on its own" (ll. 2524–2526) — no
longer points at it.

**Touches §5: yes if fixed** — the row is in §5.2. As a note it compels nothing.

---

### V9-8 — the retargeted `countInstances` sites drop `DESIGN.md`'s `[v0.5]` tag, and none cites the strongest available support · **note** · **touches §5: yes if fixed**

**Location.** §3 l. 668, §4.7.4 l. 2022, §5.2 l. 2552, `[D-P1-35]` l. 3047, §11.4 ll. 3144–3152.

**Claim under test.** That the retarget hands Phase 7 what `DESIGN.md` assigns it, faithfully and with
its strongest support — §11.4 l. 3151: "the composite loop is work `DESIGN.md` has already given you".

**Evidence.** `DESIGN.md` tags the loop `[v0.5]` **twice** — Phase 7's *Scope — in* l. 1047
("`countInstances` instancing loop [v0.5]") and Phase 4's l. 856 ("`countInstances` exposure to the
pass executor (execution is Phase 7, **tag v0.5**)"). No retargeted site carries a milestone, and
§11.4 l. 3151 tells Phase 7 the loop is "work `DESIGN.md` has already given you" while Phase 7's own
milestone is "v0.1 exit" (`DESIGN.md` l. 1019). The document is otherwise meticulous about handing
milestones across phase boundaries (l. 2551's "at v0.2", l. 2787's "arrive at v0.5"). Second half:
`DESIGN.md` l. 855–856 says "**execution is Phase 7**" in so many words, in Phase 4's *Scope — in* —
the single strongest citation for V8-2 — and no site uses it; all five argue instead from the
Composite/final bullet plus an absence in Phase 5. The conclusion is right (§2); the evidence base
presented is the weaker of the two available.

**Touches §5: yes if fixed** at §5.2 l. 2552.

---

### V9-9 — §0.8 truncates App D's cadence quotation inside its parenthesis and supplies its own closing bracket · **note** · **touches §5: no**

**Location.** §0.8 ll. 401–402.

**Claim under test.** That §0.8's quotation of App D's cadence note is verbatim, as this document's
quotations elsewhere are.

**Evidence.** RESEARCH.md l. 1379–1380 reads: "everything refreshes on program switch (per-program
location cache + redundant-upload skip; **matrices always upload**)". §0.8 renders it as a closed
quotation: *"refreshes on program switch (per-program location cache + redundant-upload skip)"* —
truncating inside the parentheses, supplying a `)`, with no ellipsis. It is non-distorting; the
dropped clause **strengthens** the upload reading the passage is arguing for. It is recorded because
this document's own §0.5 (l. 128) records a round-three headline that "rested on a four-word
misquotation of a sentence that is correct as written", and because §4.7.4 l. 1955 does not quote the
parenthesis at all and so is unaffected — the looser handling is confined to the rationale.

**Touches §5: no.**

---

### V9-10 — §5.2 property (i) says "asserts no property of them in either direction" of a sentence that has just relayed one; §4.7.4's scoped version is precise and §5.2's compression is not · **note** · **touches §5: yes if fixed**

**Location.** §5.2 property (i), l. 2543; compare §4.7.4 ll. 1952–1961.

**Claim under test.** The fix-up's own stated standard for what replaced the deleted per-sample premise
— §0.8 ll. 408–409, "no basis for the assumption in either direction" — restated in property (i) itself
as "this document asserts no property of them in either direction".

**Evidence.** §5.2: "Re-running the sweep would instead re-enter **your** world-state providers,
**whose** sampling cadence (RESEARCH.md §4.4 places **it** at frame begin) and smoothing math … are
yours to design — **this document asserts no property of them in either direction**." The possessive
chain makes "it" the providers' cadence, so the parenthesis predicates a cadence of them. §4.7.4
avoids this precisely: "this document deliberately asserts **no** property of **what a second
evaluation would do**", followed by "RESEARCH.md §4.4 places **the** world-state sampling at frame
begin" — a statement about the reference, correctly attributed, with the disclaimer scoped to the
property that actually matters.

**Why this is a note and not a correction.** On the substance the fix holds and the brief's question
answers cleanly: **property (i) does not assert the opposite of what it used to.** It says nothing
about double evaluation in either direction, which was the deleted claim's subject; and the two facts
it relays are both already Phase 6's own inputs — `DESIGN.md` l. 975 puts "**World-state sampling &
smoothing** (§4.4 frame-begin, App D.1/D.3)" in Phase 6's *Scope — in* in the document's own words,
and l. 966 puts the cadence model there too. So nothing unsourced was substituted for the deleted
premise. What is defective is only the sentence's self-description, which is looser than §4.7.4's and
therefore invites the next reader to think §5.2 says less than it does. Related and worth stating
because it bounds the fix: RESEARCH.md §4.4 l. 533 says "frame **start**", not "frame begin", and
§0.8 l. 404 adds "once per frame" which §4.4 does not say — but `DESIGN.md` ll. 281 and 975 and
RESEARCH.md §4.5 l. 569 all gloss the same moment as "frame begin", so the paraphrase is safe and a
fix should not over-correct it into a misquotation finding. The "advances by ≈0" gloss — which *is* a
property assertion, and one `DESIGN.md` does not supply — occurs at exactly two lines, 290 and 406,
both inside §0.7/§0.8 rationale, and reaches neither §4.7.4, §5.2, §6, §7 nor `[D-P1-32]`.

**Touches §5: yes if fixed** at §5.2 l. 2543.

---

### V9-11 — §0.7's `§G1.3 status` paragraph was not restamped to past tense when §0.8 superseded it, and one supersession pointer is spliced mid-paragraph · **note** · **touches §5: no**

**Location.** §0.7 l. 317 and ll. 304–305; compare §0.4 l. 113, §0.5 l. 151, §0.6 l. 214.

**Claim under test.** That §0.7 was superseded by the convention §0.4–§0.6 establish, rather than by a
partial application of it.

**Evidence.** The three earlier addenda convert the heading when superseded — "**§G1.3 status at the
time:** **that** fix-up altered §5" at ll. 113, 151 and 214. §0.7 l. 317 still reads "**§G1.3
status:** **this** fix-up altered §5 at two rows", which is §0.8's form at l. 466 for the *current*
addendum. So a reader meets §0.7's present-tense "An **eighth verify session** is therefore required …
until that verdict exists this doc is **not** a valid dependency input (§G5.3)" and learns only at the
paragraph's end (ll. 324–326) that the session has run. Second, smaller: of the three pointers the
fix-up added to §0.7, two are appended sentences (ll. 258–263 in the V7-2 bullet, ll. 286–291 in the
V7-4 bullet) and the third is **spliced mid-paragraph** at ll. 304–305, between "…refused the
request." and the surviving round-seven sentence — leaving l. 305 at **169 characters** where every
other line in §0.4–§0.8 wraps at ≤106 (next longest is 106).

**Touches §5: no.**

---

## 2. What was checked and came back clean

Named, because a round reporting only its findings misrepresents its own coverage. Where a brief item
turned out to be fine on derivation, it is said here rather than converted into a finding.

**The `Resolutions` table, audited site by site — every site named was edited. No finding.** I opened
every entry in every `Where` column. V8-1's nine sites plus the §0.7 pointer: §4.7.4 l. 1896 ("mutating
**facade** call", `correctly bounded` gone) · the new paragraph ll. 1899–1919 · §5.2 l. 2543 · §6
l. 2613 ("nothing mutating **through the facade**", and the 3→4 clause now naming a foreign error) ·
§7 l. 2662 · §9 l. 2788 · §12 item 22 l. 3297 · `[D-P1-30]` l. 3042 · §11.4 ll. 3154–3167 · §0.7
ll. 258–263 — **all present and all carrying what the row claims.** V8-2's five: §3 l. 668 · §4.7.4
l. 2022 · §5.2 l. 2552 · `[D-P1-35]` l. 3047 · §11.4 ll. 3144–3152 — all present. V8-3's five:
§4.7.4 ll. 1950–1962 · §5.2 property (i) · §6 l. 2613 (the "double-advance" formulation is gone; I
grepped) · `[D-P1-32]` l. 3044 · §0.7 ll. 286–291 — all present. V8-4's three: §4.7.4 ll. 1878–1884
with both URLs, the §0.2 branch reasoning and the 403 · `[D-P1-30]` l. 3042 · §0.7 ll. 304–305 — all
present. V8-5's two: §4.2.3 ll. 838–856 · §12 item 4b l. 3268 — both present. V8-6's one: §3 l. 668
now reads "the only observed form is RESEARCH.md §4.4's" — present. **A site named but not edited
would have been a finding; there is none.** That is the third consecutive round at which a whole
applied list survives audit.

**The three categories the table claims independently, checked.** *Sites edited beyond round eight's
naming:* §12 item 22 l. 3297 carries the facade qualifier ✓; the closing paragraph ll. 3346–3362 now
counts "**Eight** verify sessions … **five** fix-up sessions" and names a ninth ✓ — both real
neighbours of V8-1's edit, not scope creep. On the brief's **third** item, "a stale revision reference
in §5.2's non-verbs row": neither §0.8 (ll. 456–459, which names exactly two beyond-naming sites) nor
the `Resolutions` table claims to have corrected one. The `Resolutions` list of neighbours names §5.2's
**opening** row l. 2542, not the non-verbs row. The non-verbs row's reference reads "**Scoped in the
§0.7 revision**", which is correct — V7-1's scoping was applied by the fourth fix-up, recorded in §0.7.
So there is no such claim to audit, and the row is right as it stands. *Sites deliberately left alone:*
verified, all three. `DrawService`'s javadoc (ll. 1784–1796) describes the composite loop and **names
no owner** — it says only "on a COMPOSITE/DEFERRED program it is a caller-side loop over this
primitive" and routes the gbuffers case to §3/`[D-P1-35]`/§11.4. `[D-P1-33]` (l. 3045) likewise names
no phase; "the non-composite half is `[D-P1-35]`'s" is a pointer to a decision, not an owner. §1.2's
adjacent-concerns table (ll. 509–521) has no `countInstances` row. **The claim that V8-2 does not reach
them is true.** *§11.5 unchanged at four items:* verified — two requests to RESEARCH.md, two to
`DESIGN.md`, none added.

**And the routing of V8-1's residue to §11.4 rather than §11.5 is the right call.** I tested it against
the precedent the brief names. §11.5 item 4 asks `DESIGN.md` to add the missing §G2.4 rung because the
*upstream artifact* — the ladder — is deficient, and because "Phases 5, 6 and 13 will each meet the
case and each independently re-derive" it. Nothing analogous holds here: no upstream artifact is
deficient, `DESIGN.md` already assigns frame placement to Phase 7 (Phase 7's *Scope — in* part (a) is
the frame driver), and §6's 3→4 row plus §11.5 item 4 already carry the unnumbered category upstream.
A placement inside a phase's own scope is a hand-off, not a request. The routing survives — which is
independent of V9-1, whose defect is in how the hand-off is *worded*, not where it was sent.

**V8-2's retarget is correct, and better supported than the document says.** I read the specs directly
rather than adopting either the review's or the fix-up's reasoning. `DESIGN.md` Phase 7 *Scope — in*
**part (a)** l. 1047: "**Composite/final execution**: fullscreen quad (triangle-strip fallback),
identity ortho, fog/depth/blend disabled, per-pass mipmap generation (composite-mipmap bitmask),
`scale.<prog>` sub-viewports [v0.5], **`countInstances` instancing loop** [v0.5], anaglyph-aware final
to the vanilla framebuffer (Phase 5 handoff)" — the loop is named, the heading is verbatim, and it is
in part (a), exactly as §0.8 l. 379 claims. Phase 5's *Scope — in* (ll. 895–925) has no pass-execution
bullet and its *Scope — out* l. 927 reads "when copies/clears *happen* in the frame (Phase 7)" —
quoted verbatim at §0.8 ll. 381–382. And the point is settled a **second** time, more explicitly than
any site cites: Phase 4's *Scope — in* l. 856, "`countInstances` exposure to the pass executor
(**execution is Phase 7, tag v0.5**)", with Phase 4's *Scope — out* l. 858 adding "pass *execution*
and frame orchestration (Phase 7)". **No `DESIGN.md` disagreement is owed; the document was simply
wrong and is now right.** The single residual imprecision is in V9-8's second half and is a note.

**Phase 6 is the right owner of the `instanceId` upload entry point, and the *Scope — out* bullet cuts
for the document, not against it.** Phase 6's *Scope — out* l. 992–993 removes only "the hooks that
*invoke* updates (Phase 7/8)" — which presupposes the update itself stays. `DESIGN.md` states that
split explicitly in the adjacent clause of the same cadence bullet, l. 968–969: "celestial vectors
update at the sky-rotation moment (**hook is Phase 7's; the update entry point is yours**)" — precisely
the shape §5.2 l. 2552 and §11.4 l. 3148–3149 assert for `instanceId`. Corroborated by Phase 4's
*Scope — out* l. 858: "**uniform values and upload (Phase 6)**". **On the brief's sharper sub-question
— is a composite instancing loop a "hook" in the sense that bullet means? — the honest answer is no,
and it does not matter.** "Hook" is Phase 7 *part (b)* vocabulary ("**Scope — in, part (b) — Mixin hook
catalog**", l. 1057) while the loop lives in part (a); the doc's phrase "invoked at Phase 7's hooks"
(ll. 2552, 3149) is therefore loose, and §11.4's own preceding sentence gets it right ("part (a)",
l. 3145). But the operative word in the cadence bullet is **invoke**, not hook — "(Phases 7/9/10
invoke)" — and Phase 7 runs the loop either way, so the ownership split falls out identically.
`DESIGN.md` itself uses "hook" this loosely (l. 992 lists Phase 8 among "the hooks that invoke
updates" though Phase 8's invoker is shadow-camera setup; ll. 984–985 calls frame-driver capture
moments "Phase 7 hooks"). Loose within the source's own idiom, and not load-bearing.

**And the post-retarget consumer set is right about ownership.** Phase 9 has no stake in `instanceId`
specifically — §G5.1's Phase 9 row is "entity/TE id uniforms", and the cadence bullet's "(Phases
7/9/10 invoke)" is collective over five uniforms. Naming 7 for the loop, 6 for the upload, 3 for the
`const`-scan and 4 for the per-slot count matches `DESIGN.md` row for row. The one reservation is
categorical, not substantive, and is V9-7.

**V8-3's replacement claims verified at source, and the rule holds.** App D (ll. 1318–1382) gives the
three smoothed uniforms as values only — "`wetness` | float | rainStrength smoothed by
wetness/drynessHalflife", "`eyeBrightnessSmooth` | ivec2 | smoothed by eyeBrightnessHalflife",
"`centerDepthSmooth` | float | center-pixel depth, smoothed by centerDepthHalflife" — and says nothing
about what advances the filter or when. **So the deleted per-sample premise was indeed unsourced, and
deleting it rather than tagging it `[A]` was the right call**: `[A]` marks a working assumption, and
there was no basis in either direction. App D's cadence note l. 1379 does read "everything refreshes on
program switch (per-program location cache + redundant-upload skip; matrices always upload)", and
RESEARCH.md §4.2 l. 503–505 reads "A 'use program' call is the universal state barrier: it … refreshes
~90 built-in uniforms (per-program location caching + redundant-upload skipping), **evaluates** custom
uniforms". The upload reading is sound — and stronger than the argument printed for it: §4.2 reserves
"evaluates" for the thing that is evaluated per switch, and App D's "matrices always upload" is
explicit upload-side vocabulary, while the doc's stated inference (the skip "presupposes the value is
already computed") is neutral between re-sampling and reuse. Support mis-chosen, conclusion correct.
`DESIGN.md` Phase 6's *Scope — in* l. 975–977 is quoted **verbatim**, "time-corrected" included, and is
unambiguously in *Scope — in*. **"Idempotence alone" is now consistent at all six current sites** —
§4.7.4 ll. 1949–1952, §5.2 (i), §6 l. 2613, §7, `[D-P1-32]` l. 3044 — with no per-sample residue
outside §0.7's superseded bullet.

**§0.7's supersession convention is followed, and history is not being rewritten.** Round seven's
arguments stand intact: l. 256's "is correct in both cases", l. 278's "advance a halflife filter **per
sample**" and l. 303's inputs sentence are all still there, each with a bold pointer appended or
inserted. I confirmed intactness without reading the fix-up transcript: round eight cited "§0.7's V7-2
bullet (ll. 248–258)" and the bullet now spans ll. 248–263 — four lines longer, nothing removed. The
blanket device is uniform across §0.4 l. 115, §0.5 ll. 155–156, §0.6 ll. 220–221 and §0.7
ll. 325–326, and §0.8 correctly carries none, being current; §0.6 ll. 211–212 shows the per-claim bold
pointer is not novel to §0.7. **The one asked-about gap is not a gap:** §0.7's V7-1 bullet carries no
pointer about the Phase 5 → Phase 7 retarget because it never named an owner for the composite loop —
it describes the mechanism only ("a composite-only mechanism (a caller-side loop over
`DrawService.fullscreenQuad()`)") and assigns owners for the *non-composite* half. The Phase 5
attribution round eight retargeted lived in §5.2's non-verbs row, quoted at §0.8 l. 388 as "the loop
and its cadence are Phase 5/6's" — not in any §0.7 sentence. Silence is not a superseded assertion,
and the blanket pointer routes such a reader onward. What *is* defective is only the status-header
tense and one pointer's placement, which are V9-11. Likewise V8-5 supersedes nothing in §0.7 (the
second-home claim lived in §4.2.3 prose; grep finds no `buildSrc`/`SeamClasspathArguments` anywhere in
ll. 223–327), so its absence is correct.

**The decision log reads coherently end to end, with the two exceptions above.** `[D-P1-30]`'s deleted
claim is genuinely gone — "cannot lose an error" occurs at exactly two lines, l. 1927 (where §4.7.4
states the replacement invariant and marks the old one retired) and l. 3044 (where `[D-P1-32]` records
the deletion) — and `[D-P1-30]` carries the forward pointer ("The cadence this decision states is
corrected and extended by `[D-P1-32]`"). I checked the pairs the brief and my own reading flagged and
found no live contradiction: **D-P1-33 vs D-P1-35** are about different objects and each says which —
`[D-P1-33]` scopes a *mapping* and names no owner phase, `[D-P1-35]` retargets an *owner* and preserves
the distinction in kind ("the composite loop is **assigned**, the gbuffers/shadow re-render is
**open**"); **D-P1-32 vs D-P1-34** match at §5.2 property (iii) and §12 item 22; **D-P1-3 vs D-P1-27**
are consistent, with all three repository paths stated (§4.2.3 l. 813, §4.2.4a l. 974, and `:mod`
inheriting from `dependencies.gradle` at ll. 997–998) — which is also exactly the ordering `[D-P1-27]`
exists to protect; **D-P1-24** agrees with §4.11, §6's build-time row, §12 item 38 and §0.6's V5-3
bullet, `-x test` rejection included. The exceptions are V9-5 (the delegated obligation `[D-P1-32]`
does not carry) and V9-6 (the markers). One further compression, recorded here rather than as its own
finding because nothing depends on it: `[D-P1-35]` l. 3047 says "§5.2's non-verbs row no longer names
two phases for one directive" where §0.8 l. 393–394 says "for **two halves of** one directive" —
dropping three words inverts the truth value, since §5.2's row names 7, 6, 3 and 4 against
`countInstances`.

**§0.8's "no service signature was added, removed or changed" — corroborated, and I say which, because
byte-for-byte verification is not available to any verify session here.** There is no round-eight
snapshot: `git log` in `Schmaloogium/` ends at `79543cf docs: add review doc for phase 1 doc draft`,
which is the **original build-session draft** (2159 lines against today's 3363), and all five fix-ups
are uncommitted working tree. So I reconstructed. (1) I extracted every declaration in §4.7.2–§4.7.5
from `git show HEAD:PHASE_1_DOC.md` and from the current file and diffed them. The entire delta from
the original draft is: **removed** `void fullscreenQuadInstanced(int instanceCount)`; **added**
`readDepthPixel`, `copyDepthToTexture`, `TextureService.upload(TextureHandle, TextureData)`,
`upload(loc,int,int)`, `upload(loc,int,int,int,int)`, `depthTest`, `fog`, `List<GLError> drainErrors()`,
the `GLError` record, `RecordingGLDevice`'s three-arg constructor, `GLCallLog.bounded/unbounded/
droppedCallCount`, `ScriptedResponses.depthPixel/glError/validateFails`, and
`ReplayAssertions.noUseAfterDelete` — every one of which the §0.4/§0.5/§0.6 changelogs and l. 2542's
per-revision entries account for, and none of which is attributable to the §0.7 or §0.8 revisions on
the document's own accounting. (2) Structurally: **no site in any of the fifteen `Where`-column entries
falls inside the signature block at ll. 1654–1818.** (3) Round eight independently attested the same
invariance across round seven. Counts confirmed directly: `GLDevice` hands out exactly seven services
(ll. 1658–1664) and `GLHandle permits` exactly four types (ll. 1602–1603). **Verdict: the claim is
corroborated on three independent lines and is almost certainly true; it is not verified byte-for-byte,
because nothing in the working tree makes that possible.** The absence of a commit per fix-up is what
costs each verify session this check — worth the fix-up's attention, though it is a workflow matter
rather than a document defect, and §11.5 is for requests against RESEARCH.md and `DESIGN.md`. Ironically,
V9-2's `GLError` javadoc defect is a direct consequence of the claim being *true*: because no signature
changed, the block was never swept.

**Round eight's clean list, not re-fought.** I did not re-derive `[D-P1-33]`'s central argument,
`[D-P1-34]`'s sufficiency, §3's second row, §3.1's flagged-delta ruling, the four-handle/
no-renderbuffer model, the `ivec3`/`mat3` absence, the App F.7 mappings, V6-5's blit narrowing, the
CI ordering, §4.2.4a's half-deletion, the seventeen §4.1 template rows or the four OQ spike specs.
Where reading them was unavoidable I found nothing new. **V8-7 stays closed** — I saw no new evidence
and did not re-open it. Two things I *did* re-confirm incidentally because a finding sat next to them:
the seventeen §4.1 template rows still match the files (three `apply from:` sites at ll. 100/238/239,
`enable_lwjglx` at one `compileOnly` site, `settings.gradle` with no `include`, the
`${rootProject.projectDir}` AT hardcode, JUnit 6.0.3 with no `src/test/`, Gradle 9.6.1 in the wrapper
and all three workflows), and §11.3's template-defect items 2 and 4 are both real — `build.gradle`
declares only `modCompileOnly`/`modRuntimeOnly` against a README that mandates `modImplementation`,
and `extra.gradle`'s helper-method comment describes an API that does not exist.

**Candidates examined and dropped rather than reported.** §4.2.3's "which is not what the next sentence
promises" (l. 842) reads correctly — the next sentence promises one shared home, and three copies is
not that. §4.2.4's printed `from project(':engine').sourceSets.main.output` (l. 940) against §12 item
7's requirement of a dependency-derived form is inside round eight's cleared Gradle work and is
explicitly framed by §4.2.5 ll. 1037–1044 as a caveat on the expression recorded against item 7. And
§4.7.4 l. 1960's "which §G1.1 makes a thing to flag rather than to decide here" is a paraphrase of a
posture §G1.1 genuinely has (ll. 105, 106–108, 111–113), not a quotation, so it is not a misattribution.

**Doc gate, literally.** Module/package layout finalized with dependency rules as testable constraints
— C-1 … C-4 in §4.3, each with a named test in §8.1. Every D-1..D-10 dispositioned in §11.2. Pin table
complete with an executable re-verification procedure (§4.2.6's seven steps and three-ruling table,
restated in §G4.4 form at §10.1), and I ran steps 2 and 3 of it against the live endpoints. **Met.**
All thirteen §G9 sections present and substantive; all four assigned OQs carry spike specs.

---

## 3. Verdict

**PASS-WITH-CORRECTIONS.**

**Six corrections, five notes, zero blocking.** Nothing here is a structural miss: the module split,
the seam and its four constraints, the facade's shape, the conformance map, the pin table and the
build plan are all sound, and every correction is a clause, a sentence or a sweep of stale duplicates.
FAIL is not close, and no correction changes what any dependent phase builds.

**The honest shape of this round.** Every finding is in the fifth fix-up's new prose or in something
that prose asserts about the rest of the document — §0.7's lesson holding for a third consecutive
round. Four of the six corrections cluster on one subject: the foreign-GL consequence V8-1 introduced
is correct, was applied at every site round eight named, and is still not *finished* — its remedy is
overstated (V9-1), its wording survives uncorrected at five sites the fix-up's own claim covers
(V9-2), the precondition three sites lean on has not been widened where it lives (V9-5), and the
decision log does not record which round changed it (V9-6). The pattern is not a review's reasoning
being inherited — the fifth fix-up genuinely re-derived everything, and that discipline paid: V8-2 and
V8-3 both survived my independent derivation at source, and V8-2 turns out to be **better** supported
than the document argues. **This round's pattern is one level down again: a fix-up that re-derives
every finding can still under-scope its own sweep, and the sites it misses are the ones its correctness
elsewhere hides.** V9-2's `GLError` javadoc is the cleanest example — it was missed *because* the
signature-invariance claim is true, so no `Where` entry ever pointed inside that block.

The two findings that are not about V8-1 include the sharpest structurally: V9-3 shows the V8-5 fix resting on
an untagged, unsourced Gradle mechanism, which is exactly the provenance gap round seven caught at
`[D-P1-30]` — in a correction written about a Gradle mechanism. And V9-4 shows the fix-up's supporting
argument citing an unsourced sibling row as settled precedent.

### Per-finding §5 disposition

| Finding | Severity | Touches §5? |
|---|---|---|
| **V9-1** the foreign-GL hand-off: uniqueness, effectiveness, expressibility, cost | correction | **yes** — §5.2's GL-error row states "the only sound remedy" in its own words and names an owner on it |
| **V9-2** five sites retain the unqualified "mutating call" | correction | no — §5.2 and `[D-P1-32]`'s headline already carry the corrected form; the fix is §4.7.4, §2.4, §11.1 |
| **V9-3** §4.2.3's untagged Gradle mechanism and the `ext` clause | correction | no — §4.2.3 and §12 item 4b |
| **V9-4** §3's `scale.<prog>` attribution, cited as precedent by §0.8 | correction | no — §3, §0.8, and a §11.3/§11.5 entry if the conflict is judged real |
| **V9-5** precondition (ii) still the `OUT_OF_MEMORY` corner where it lives | correction | no — §5.2's (ii) was widened; §4.7.4 and `[D-P1-32]` were not |
| **V9-6** round-eight `[fix-up: …]` markers missing | correction | no — §11.1 |
| **V9-7** §5.2's non-verbs row admits a phase that requests nothing absent | note | yes if fixed |
| **V9-8** the `[v0.5]` tag and the uncited Phase 4 citation | note | yes if fixed at §5.2 |
| **V9-9** App D cadence quotation truncated in §0.8 | note | no |
| **V9-10** property (i)'s self-description looser than §4.7.4's | note | yes if fixed |
| **V9-11** §0.7's status tense and one pointer's placement | note | no |

### §G1.3 line

**`PHASE_1_DOC.md` is NOT verified.** The verdict is PASS-WITH-CORRECTIONS with resolutions
outstanding, so §G1.3's verified state is not reached.

**§5 changed by this round's corrections: yes — at one row, by one correction.** V9-1 requires editing
§5.2's GL-error row (l. 2543): the words "the only sound remedy" are false as written, and the
parenthetical "(an unconditional drain at a frame-driver point)" describes a remedy the facade §5.2
exposes cannot be made to deliver. Three of the notes would also touch §5.2 if adopted (V9-7, V9-8,
V9-10); notes compel nothing, and the fix-up should record its disposition either way.

**The next step in the cadence is therefore a fix-up session (§G1.3) followed by a tenth verify
session**, because §G1.3's *"re-verify only if §5 changed"* rule fires on V9-1. Until that verdict
exists `PHASE_1_DOC.md` is **not** a valid dependency input, and Phase 2, Phase 3 and everything
downstream stay blocked (§G5.3).

**Three things that narrow the tenth session further than this one was narrowed, and one that does
not.** *(1)* **No signature is touched by any correction here** — the facade's verb list, every service
interface, every handle type and every value type stay as they are; V9-2's `GLError` fix is a javadoc
sentence inside the block, not a signature. *(2)* **No correction changes what a dependent phase
builds.** Phase 6's rung-2 protocol is unchanged in every particular; Phase 7's composite loop and
`instanceId` split are unchanged; V9-1 corrects what Phase 7 is *told* about a remedy it has not yet
placed. *(3)* **Nothing here is inherited reasoning:** every finding was re-derived from the source it
cites, and the sub-agent gate means no candidate reached §1 without my own confirmation at the line.
What does *not* narrow it: **four of the six corrections are edits to the same subject across
eight sections**, so the fix-up's sweep is the risk again, and V9-2 exists precisely because the last
one stopped at the sites a `Where` column could name. If the tenth session inherits one instruction
from this one, it is to audit the *unnamed* neighbours — the javadoc inside a signature block, the
key-type table in §2.4, the second half of a bullet whose first half was corrected.

**If the fix-up judges V9-1 fixable without touching §5.2** — for instance by deleting the clause
rather than rewording it — then no correction touches §5, and §G1.3 closes the phase without a tenth
session. That call is the fix-up's, on the evidence above; it is not mine to make, and this line exists
so the option is visible rather than foreclosed by my phrasing.

*Per §G1.2 this session stops here. It wrote no code, ran no build and no test, applied no fix, and
created exactly one file: this one. `PHASE_1_DOC.md`, `RESEARCH.md`, `DESIGN.md` and all eight prior
review files — including `PHASE_1_REVIEW_8.md`'s `## Resolutions` section — are unmodified.*

---

## Resolutions

*Written by the sixth fix-up session (§G1.3), 2026-07-25, after this review's verdict. **Nothing above
this heading is modified** — the findings, §2's clean list and the verdict are evidence. Line numbers
below are `PHASE_1_DOC.md`'s **after** this fix-up (3709 lines); §0.9 is the addendum recording the
session.*

**Method note, because this round's meta-finding demanded one.** §3's closing assessment is that a
fix-up which re-derives every finding can still under-scope its own **sweep**, and that the sites it
misses are the ones its correctness elsewhere hides. Two disciplines follow and both were applied.
*(1) Nothing was adopted.* Every load-bearing claim was re-derived at its source before it was written:
`DESIGN.md` Part II's Phase 3/4/5/6/7 *Scope* bullets for V9-1, V9-4 and V9-8; RESEARCH.md §4.2, §4.4,
App A.1 and App D's cadence note for V9-1 and V9-9; the template's three `apply from:` sites and the
three printed `new SeamClasspathArguments(...)` blocks for V9-3; `PHASE_1_REVIEW_8.md` read-only —
including its `## Resolutions` section, which V9-2 and V9-6 are findings *about* — for those two.
*(2) The sweep ran over formulations, not over sites.* Before this table was written, the document was
grepped for every wording changed (`mutating`, `sound remedy`/`only remedy`, `absorb`,
`43 program switch`/`forty-three`, `need not recur`, `` `ext`-indirection ``, `countInstances`,
`§G1.3 status`), and every occurrence is either edited below or listed in **Checked and correctly left
alone** with its reason. **No network use of any kind. No adversarial sub-agents** — §G1.3 is silent,
so the call was this session's, and it is disclosed either way: with a sweep this narrow and every
quote requiring re-derivation regardless, a delegated finding is a hop rather than a saving.

**Counts.** This review named **29 distinct editable sites** across its eleven findings — 19 for the
six corrections, ten more for the five notes. **All 29 are edited.** A thirtieth pointer,
`PHASE_1_REVIEW_8.md`'s `Resolutions` row for V8-1, is evidence and is untouched. **Six further sites**
were reached by the sweep and are listed under *Neighbours swept*. **Six corrections applied, five
notes applied, none refused**; one correction (V9-4) applied **narrower** than its fix shape suggested
and one (V9-3) **reshaped** under derivation, each argued below and in §0.9.

| Finding | Disposition | Where (every site **edited**, not every site named) |
|---|---|---|
| **V9-1** — the foreign-GL hand-off: uniqueness, effectiveness, expressibility, cost | **Applied**, at **8 sites** (this review named 7). **Branch (a): §5.2 is reworded, not emptied — and the branch that would have closed the phase does not exist.** §G1.3's rule is *"if corrections **altered the doc's Cross-phase interfaces section**"*; a deletion alters it exactly as a rewording does, and this document's own precedent settles it twice — §0.7 and §0.8 each declare *"this fix-up altered §5"* for corrections that were **prose-only at these same two rows**, and each triggered a verify session. Leaving §5.2 untouched was ruled out by this review's own objection (it would leave the contract section asserting what §4 retracted). So the only live question was what §5.2 should say. **All three limbs re-derived and all three hold.** *Uniqueness:* §4.7.4 concedes four lines above that dropping the elision *"would bound the window against all GL"* and rejects it on **cost**; the document described two sound remedies and named the second "the only" one. *Effectiveness:* `DESIGN.md` §G3.2 and RESEARCH.md §4.4 put foreign GL throughout the frame, so one drain bounds the frame-boundary gap and no interior gap. *Expressibility:* `drainErrors()` takes no argument and the elision is contract, so the remedy elides in exactly the configuration it targets. **The elision decision is deliberately NOT re-opened** — see the refusal note after this table | §4.7.4's elision paragraph **ll. 2160–2190** ("the only sound remedy" replaced by *"Two remedies exist, and neither is described here as the only one"*; the cost/mechanism asymmetry stated; both limits added) · §4.7.4 **ll. 2214–2223** (the replay's *"paid … once"* now carries the recurring-foreign exception) · §5.2's GL-error row **l. 2837** (the clause rewritten: the remedy is one of two, carries two limits, and is not unconditional at the driver; a recurring clean replay named as evidence of foreign GL) · §7 **ll. 2961–2967** (same exception on the hot-path ledger) · §11.4's hand-off **ll. 3478–3508** (rewritten: two remedies, both limits, and the additive route for a forcing verb) · `[D-P1-32]` **l. 3342** (the exception recorded, with the ledger named incomplete) · `[D-P1-30]` **l. 3340** *(neighbour — this review named it for V9-6 only)* (*"the one remedy"* → *"the frame-level remedy"*, with the two-remedies sentence) · §0.8's V8-1 bullet **ll. 379–387** (**bold** supersession pointer per the §0.4–§0.7 convention; the round-eight argument left intact as a record) |
| **V9-2** — five sites retain the unqualified "mutating call" | **Applied**, at **6 sites** (this review named 5). All five confirmed by grep before editing. The `GLError` javadoc is the load-bearing one and is corrected in the direction the finding identifies: the entailment is now conditioned on a **mutating facade** call *and* explicitly marked non-unconditional, because a window may hold an error no facade call caused — so `op`/`subjectLabel` can name the wrong call, which is why `[D-P1-32]`'s replay rather than the record is what attribution rests on. **The whole signature block was swept, not the javadoc alone**, which is how the sixth site was found | §2.4's key-type table **l. 853** · `GLError` javadoc **ll. 2061–2067** · §4.7.4 **l. 2200** (*"a window holding exactly one mutating **facade** call"*) · §4.7.4 **l. 2208** (*"nothing mutating **through the facade**"*) · `[D-P1-32]`'s rationale half **l. 3342** · **`GLDevice.drainErrors()`'s own javadoc ll. 1917–1922** *(neighbour — no finding pointed at it)*: it claimed *"empty when clean"* of a call that also returns empty when it **elides**, so an empty drain does not mean the per-context flag is clear. Same class of defect, same block, found by sweeping rather than by a `Where` entry |
| **V9-3** — §4.2.3's untagged Gradle mechanism and the `ext` clause | **Applied, reshaped** — the conclusion is kept and its support is replaced. *(a)* The decisive reason is **compilation order**, not class-scope isolation: the applying script is compiled in full before it executes and `apply from:` is a runtime statement, so the name is unresolvable at *compile* of the applying script. *(b)* The `ext` escape hatch is **deleted**, not qualified: exporting a `Class` needs `newInstance`, exporting a factory needs no `new`, and every route changes all three printed call sites (now ll. 1053, 1182, 1237 — re-verified byte-identical three-argument calls with no `import`) — so the true statement is that the printed form cannot be made to work through `apply from:` at all. §12 item 4b was the more accurate text and §4.2.3 is corrected **to** it. *(c)* *"Equivalently … the same way"* is replaced: `buildSrc` is unconditional and automatic; an included build needs a `settings.gradle` wiring **and** an apply. *(d)* The chosen option's own unstated requirement is now stated (default package, or an `import` in all three blocks). **Provenance: `[U]` with an open-question row**, per RESEARCH.md §0.2, rather than a web fetch — decided with the project owner. Item 4b's existing hook is named as the experiment that settles it | §4.2.3 **ll. 1060–1107** (rewritten mechanism, `ext` clause deleted, included-build claim corrected, default-package requirement added, `[U]` paragraph added at ll. 1097–1107) · §12 item 4b **l. 3609** (compilation-order reason, *"no `ext` indirection preserves that call site"*, default-package requirement, `[U]` pointer, and the hook named as the experiment) · **§11.3 item 10, ll. 3414–3433** *(new — the open-question row `[U]` requires, under a new heading)* · §0.8's V8-5 bullet **ll. 452–457** (**bold** supersession pointer) |
| **V9-4** — §3's `scale.<prog>` attribution, cited as precedent by §0.8 | **Applied NARROWER than the fix shape suggested — a citation, and deliberately no owner.** This review allowed it may not be a defect; derivation says the attribution is unsourced but the seam is real. `DESIGN.md` gives the scale **factor** to Phase 3 (*"stored; applied by Phase 4"*) and Phase 4 (*"scale/flip storage"*), the buffer **dimensions** to Phase 5 (*"display size × render-quality multiplier"*), and **applying** the sub-viewport to Phase 7 (*Scope — in* part (a), `[v0.5]`); Phase 5 has no sub-viewport bullet; and nothing anywhere assigns the **multiplication**. So the row now reports the silence and names no owner — retargeting Phase 5 → Phase 7 would repeat the original error with a different digit, and §G5.3's integration review is the instrument for a seam this shape. **No §11.5 request is raised**, and that is a ruling rather than an omission: §G1.1 requires flagging a *conflict* with `DESIGN.md`, and a silence is not one. **§0.8's use of the row as V8-2's precedent does NOT stand as written** and the support is withdrawn; V8-2's conclusion is untouched and now rests on the stronger citation V9-8 identified | §3's `scale.<prog>` row **l. 895** (attribution replaced by the three sourced inputs and an explicit statement that `DESIGN.md` assigns no owner to the multiply; provenance cell now distinguishes the `[V:doc]` verb from the reported silence) · §0.8's V8-2 bullet **ll. 407–415** (**bold** pointer withdrawing the precedent and supplying `DESIGN.md` Phase 4's *"execution is Phase 7, tag v0.5"* in its place) |
| **V9-5** — precondition (ii) still the `OUT_OF_MEMORY` corner where it lives | **Applied.** Both sites now carry the general form §5.2 already had, and carry it as **two named causes** rather than as a widened sentence — (a) `OUT_OF_MEMORY` need not recur, (b) the error may never have been ours — so a future session cannot narrow it back without visibly deleting a numbered cause. The five delegating sites are named inside the precondition itself, which is what makes the delegation legible from the branch rather than only from the sites | §4.7.4's precondition (ii) **ll. 2247–2259** (rewritten: the *"load-bearing in general"* claim moved into the precondition itself, the five delegating sites named there, two causes enumerated (a)/(b), and a repeatedly-clean replay identified as evidence for (b)) · `[D-P1-32]` **l. 3342** (the same two causes, with the second marked as what four other sites lean on) |
| **V9-6** — round-eight `[fix-up: …]` markers missing | **Applied**, and the convention treated as binding on this document even though §G1.3 does not require it — sixteen decisions carry markers and §0.6's V6-1 used a *missing* marker as evidence a fix-up never ran, so an incomplete record is a defect measured against a standard this document chose for itself. Declining would leave the device unreliable at exactly the decisions three rounds have edited most. Round-nine markers were added wherever this session amended a decision, so the record is complete forward as well as backward | `[D-P1-30]` **l. 3340** (marker gains `PHASE_1_REVIEW_8.md V8-1, V8-4` and `PHASE_1_REVIEW_9.md V9-1, V9-6`; the *"(round seven)"* label on the sentence carrying V8-4's URLs corrected to attribute the URLs to round eight) · `[D-P1-32]` **l. 3342** (marker gains `PHASE_1_REVIEW_8.md V8-3` and `PHASE_1_REVIEW_9.md V9-1, V9-2, V9-5`) · `[D-P1-35]` **l. 3345** (marker gains `PHASE_1_REVIEW_9.md V9-8`) |
| **V9-7** — §5.2's non-verbs row admits a phase that requests nothing | **Note — applied**, and the *breadth* kept rather than the row narrowed. Removing Phase 6 is what the header literally licenses and is **rejected**: §5.2 is written to be sufficient on its own, and a Phase 6 session reading only §5 would then not learn that the `instanceId` upload inside Phase 7's loop is its own entry point. The **header** is widened instead. **Widening a header readmits what it excluded, and this one readmits Phase 5** — this review's own point 5, honoured: the composite loop's N draws run inside the buffer estate Phase 5 owns (`DESIGN.md`'s read/write/flip law), a stake §4.7.4 and §3 keep in prose while §5.2 had stopped pointing at it. Naming 7, 6, 5, 3 and 4 is the whole seam | §4.7.4's absent-verbs table header and column heading **ll. 2300–2306** (header now states that a row naming an adjacent owner puts that phase in the last column; column renamed *"Who requests it — or owns the served work in its place"*) · §4.7.4's instanced-draw row **l. 2316** (last cell rewritten: nobody requests the verb, and 7/6/5 are named with what each owns) · §5.2's non-verbs row **l. 2846**, both halves — the row header widened, with a pointer to §4.7.4's table header for which rows it covers, and the consumer list with **Phase 5 readmitted** and marked explicitly as *not* a requester |
| **V9-8** — the `[v0.5]` tag and the uncited Phase 4 citation | **Note — applied**, both halves. `DESIGN.md` Phase 4's *"`countInstances` exposure to the pass executor (**execution is Phase 7, tag v0.5**)"* settles owner **and** milestone in one line and was cited nowhere; it is now the primary citation at every retargeted site, and the `[v0.5]` tag travels with it. The milestone is load-bearing rather than decorative because Phase 7's own milestone is **v0.1 exit**, so an untagged hand-off reads as v0.1 assembly; §11.4 now says the loop is §G0.3 architect-now / implement-at-v0.5 work | §3's first `countInstances` row **l. 891** (*"Phase 7's, at `[v0.5]`"*, with the Phase 4 citation quoted) · §4.7.4's instanced-draw row **l. 2316** (`[v0.5]` + the Phase 4 quotation) · §5.2's non-verbs row **l. 2846** (a *"Cited and tagged in this revision (§0.9)"* clause, and `[v0.5]` inside the Phase 7 consumer entry) · `[D-P1-35]` **l. 3345** (the Phase 4 citation and the tag; **and the three-word compression §2 recorded** — *"for one directive"* → *"for **two halves of** one directive"*, which §0.8 had right and the decision log had dropped) · §11.4's composite-loop paragraph **ll. 3463–3476** (owner, milestone, the §G0.3 architect-now/implement-at-v0.5 consequence, and Phase 5's estate named) |
| **V9-9** — App D cadence quotation truncated in §0.8 | **Note — applied.** Treated as a quotation repair rather than a rewrite of superseded reasoning: the dropped clause *strengthens* the reading §0.8 argues for, so restoring it changes accuracy and nothing else. The document's own §0.5 records a round-three headline lost to a four-word misquotation, which is why two words are worth restoring | §0.8's V8-3 bullet **ll. 420–423** (*"; matrices always upload"* restored inside the parenthesis, with a note that it was truncated and why restoring it is safe) |
| **V9-10** — property (i)'s self-description looser than §4.7.4's | **Note — applied, scoped exactly as the finding asks and no wider.** The disclaimer now attaches to **what a second evaluation would do**, matching §4.7.4's precise form, and the two relayed facts are re-stated as statements *about the sources* rather than predications of Phase 6's providers. **Deliberately not done:** the citations are **not** deleted — both facts are already Phase 6's own *Scope — in* inputs (`DESIGN.md` ll. 966 and 975) — and RESEARCH.md §4.4's *"frame start"* against the document's *"frame begin"* is **not** turned into a misquotation finding, since `DESIGN.md` ll. 281 and 975 and RESEARCH.md l. 569 all gloss that moment as "frame begin" | §5.2's property (i), inside the GL-error row **l. 2837** |
| **V9-11** — §0.7's status tense and one pointer's placement | **Note — applied in both halves, and pre-emptively in a third.** §0.7's header is restamped to the §0.4–§0.6 convention and its paragraph converted to past tense throughout, so a reader no longer meets a present-tense *"an eighth verify session is therefore required"* and learns three sentences later that it has run. **§0.8's header is restamped in the same edit**, because §0.9 supersedes it — which is the failure this finding is about, and repeating it while fixing it was the one outcome the brief named. §0.8 also gains the closing supersession sentence §0.4–§0.7 all carry. The 169-character line is rewrapped: it is housekeeping, and it is free | §0.7 **ll. 318–327** (header → *"§G1.3 status at the time:"*, *"that fix-up"*, and the paragraph converted to past tense throughout) · §0.7 **ll. 304–307** (the spliced pointer rewrapped from 169 characters to ≤106) · §0.8 **ll. 494–511** (header restamped, paragraph to past tense) · §0.8 **ll. 512–513** (**new** closing supersession sentence, matching §0.4 l. 115, §0.5 ll. 155–156, §0.6 ll. 220–221 and §0.7 ll. 326–327) |

### The one thing refused, and it is a non-decision rather than a disposition

**The elision is not re-opened, and V9-1's cost limb (d) is applied as a *record* rather than as a
re-weighing.** This review established that the ledger the elision was kept against omits a cost the
elision **creates** — a recurring foreign error re-enters `[D-P1-32]`'s replay every frame, re-uploading
a program's whole ~90-uniform set (RESEARCH.md §4.2), reproducing nothing and disabling nothing — and
that including it *can* invert the comparison. It also said, correctly, that whether it *does* invert
it is a judgement it did not make. **Making that judgement inside a fix-up would be a design call
arriving through a correction**, which is exactly the move round eight's rule forbids and which no
adversarial session would then review. So the omitted cost is now stated at all three sites that
priced the replay as once-per-disable (§4.7.4 ll. 2214–2223, §7 ll. 2961–2967, `[D-P1-32]` l. 3342), the
ledger is described as **incomplete rather than settled**, and the re-weighing is left open with its
evidence written down. A tenth session finding this insufficient is finding a design gap, not an
unapplied correction — and it will find the argument for the omission here rather than absent.

**The arithmetic note is fixed in live prose and deliberately left standing in the addenda.**
*"43 program switches per frame"* reuses a registry cardinality as a per-frame event count:
RESEARCH.md l. 493 says 43 **slots**, App A.1 l. 1142 counts the two virtual `*_pre` programs and the
sixteen-element deferred/composite arrays inside that 43, a real pack binds a fraction, and §4.4's
push/pop semantics let one slot bind more than once. §7 l. 2959 was already careful (*"there are 43
**slots**"*). Corrected at the two live sites — §4.7.4 l. 2177 and §11.4 l. 3494, both now *"at every
program switch in the frame"* — and **left as written at §0.7 l. 252 and §0.8 ll. 367, 377**, which are
superseded records under the convention that history is pointed at and not rewritten; §0.8's V8-1
bullet carries the pointer that says so. The direction of the residual error favoured the conclusion
being defended, which is why it was worth correcting rather than only noting.

### Neighbours swept, beyond every `Where` column

Recorded separately because this review audited round eight's `Where` column site by site and round ten
will audit this one.

- **`GLDevice.drainErrors()`'s javadoc, ll. 1917–1922** — *"empty when clean"* is false in one
  direction under the elision. Found by sweeping ll. 1905–2079 rather than by any finding, which is
  precisely this review's §3 point about what a true signature-invariance claim hides. Counted above
  under V9-2.
- **§11.3 item 10, ll. 3414–3433 (new)** — the open-question row RESEARCH.md §0.2 requires of the `[U]`
  V9-3's fix introduces, under a new *"Unverified claims (`[U]`) this document makes and cannot
  source"* heading. Counted above under V9-3.
- **§0 header l. 10 and its dated-claims note ll. 13–17** — *"Last revised … (§0.8)"* → **(§0.9)**, and
  *"the fix-up addenda in §0.4–§0.7"* → **§0.4–§0.9**. The second was already stale by one round.
- **§5.2's opening row l. 2836** — the per-revision changelog gains this revision's entry (**again no
  signature change**, with the two prose-corrected rows and their findings named), and §0.8's entry is
  relabelled from *"this revision"* to *"the §0.8 revision"* now that it is not.
- **The end-of-document closing paragraph ll. 3687–3709** — counted *"**Eight** verify sessions …
  **five** fix-up sessions"* and named a ninth as the next step. Now nine/six, with this round's §5 rows,
  V9-4's narrowing and V9-3's reshaping named, and a **tenth** session as the next step.
- **New §0.9** at ll. 515–702, following §0.4–§0.8's established shape.

### Checked and correctly left alone

So the next audit can tell a considered omission from an oversight.

- **`PHASE_1_REVIEW_1.md` … `PHASE_1_REVIEW_8.md`, including their `## Resolutions` sections** — read for
  V9-2 and V9-6, **not modified**. `PHASE_1_REVIEW_8.md`'s V8-1 `Resolutions` row carries half of V9-2's
  false claim and is evidence; the correction lives in `PHASE_1_DOC.md` §0.8, which is this document's
  own text, and in §0.9.
- **"mutating" at four surviving sites, each for a different reason.** §0.7 ll. 254 and 257 are
  superseded round-seven rationale already carrying V8-1's bold pointer; §0.8 l. 485 *quotes* the old
  formulation deliberately, as the lesson it is drawing; §4.7.4 l. 2116 (*"every mutating verb returns
  `void`"*) is a different sense — the verb's return type, not the elision bit; §4.7.5 l. 2376 (*"every
  mutating call appends a `GLCall`"*) is the recording backend, where every call **is** a facade call by
  construction.
- **§6's rung-2 row and 3→4 row (ll. 2907, 2906)** — already carry *"nothing mutating **through the
  facade**"* and already name a foreign error as a second cause of an unattributable sweep, from V8-1.
  V9-5's widening does not reach them because they were never narrow. Verified rather than assumed.
- **§9's staging note l. 3086** — already reads *"elided entirely when nothing mutating has happened
  **through the facade**"*. No edit owed.
- **§12 item 22's review hook l. 3638** — already carries the facade qualifier twice, from the fifth
  fix-up's own neighbour sweep. Its *"a drain after a drain issues no query"* hook is what makes V9-1's
  expressibility limb true, and is left exactly as it stands.
- **§11.5 (l. 3560), unchanged at four items, and two candidates declined.** V9-4 raises none, because
  `DESIGN.md`'s silence about who multiplies is not a *conflict* and §G1.1 requires flagging conflicts.
  The commit-per-fix-up observation raises none either: §11.5 is for requests against RESEARCH.md and
  `DESIGN.md`, this review recorded it as a workflow observation for that reason, and it was **raised
  with the project owner**, who directed that it be noted in §0.9 and nothing be changed. It is noted
  there, with the cost each verify session pays.
- **`[D-P1-33]` (l. 3343), `[D-P1-34]` (l. 3344), `DrawService`'s javadoc (ll. 2041–2050) and §1.2's
  adjacent-concerns table (l. 728 ff.)** — re-checked for V9-7/V9-8 reach. `DrawService`'s javadoc and
  `[D-P1-33]` describe the composite loop and name no owner and no milestone, so neither the retarget
  nor the `[v0.5]` tag reaches them; `[D-P1-34]` is about the `locate` name obligation only; §1.2 has
  no `countInstances` row. Same disposition round eight recorded, re-verified rather than inherited.
- **§10's four spike specs, and §2's whole clean list** — not re-derived. Three consecutive rounds have
  cleared them and re-fighting settled ground is how a session spends its budget on nothing. **V8-7
  stays closed** — this is the third round to decline to re-open it.
- **The `[A]` tag on §3's second `countInstances` row** — untouched. V9-8 tags the *composite* loop
  `[v0.5]` because `DESIGN.md` assigns it; the gbuffers/shadow case is **open**, so it gets no milestone,
  and the two cases stay distinct in kind exactly as `[D-P1-35]` says.

### §G1.3 status

This fix-up altered **§5**, at the same two rows as the last two rounds. §5.2's **GL-error row**
carries V9-1's corrected remedy claim (one of two remedies, bounding only the frame-boundary gap, and
subject to the same elision), the recurring-foreign replay cost, and V9-10's rescoped property (i);
§5.2's **non-verbs row** carries V9-7's widened header with **Phase 5 readmitted** and V9-8's `[v0.5]`
tag and Phase 4 citation. **No service signature was added, removed or changed:** every service
interface, handle type and value type is byte-for-byte what rounds seven, eight and nine all reviewed —
V9-2's correction inside ll. 1905–2079 is a **javadoc sentence** on `GLError` and on `drainErrors()`,
not a declaration, and the block was swept end to end for exactly the reason §3 gives.

§G1.3's *"re-verify only if §5 changed"* rule therefore fires, and a **tenth verify session** is
required before Phase 2, Phase 3 or any other dependent consumes `PHASE_1_DOC.md`; until that verdict
exists the document is **not** a valid dependency input and everything downstream stays blocked
(§G5.3). Three things narrow what the tenth session inherits: **no finding was left unapplied and none
was refused**, so there is no re-derivation debt, and where a fix was narrowed (V9-4) or reshaped (V9-3)
the argument is in the table above and in §0.9; **no correction changes what a dependent phase builds**
— Phase 6's rung-2 protocol and Phase 7's composite loop and `instanceId` split are unchanged in every
particular, and what moved is what each is *told*; and the sweep this time ran over formulations rather
than sites, so the `Where` column above is auditable by grep and not only by inspection. **One question
is left open on purpose** and is the honest place to start an attack: whether the replay cost the
elision creates inverts the decision to keep it. It is written down at three sites, it is defended by
nobody, and it is a design call rather than a correction.

*Per §G1.3 this session stops here. It wrote no code, ran no build and no test, launched no review
agent, made no network request, and modified exactly two files: `PHASE_1_DOC.md` and this
`## Resolutions` section. `DESIGN.md`, `RESEARCH.md`, and `PHASE_1_REVIEW_1.md` through
`PHASE_1_REVIEW_8.md` — including their own `## Resolutions` sections — are unmodified, as is
everything above this heading.*
