# PHASE_1_DOC.md — Verify session, round eleven

**Phase:** 1 — Foundation & project architecture
**Document under review:** `PHASE_1_DOC.md`, 3887 lines, identical to `git HEAD` (`2913008`, "docs: 7th
fix-up session"). **Every line number in this review is in that file's coordinates**, and each was
re-resolved against it after the review was written — a discipline this round has a finding about
(V11-5), so it is stated rather than assumed.

**Verdict: PASS-WITH-CORRECTIONS** — one correction, five notes, zero blocking. The single correction
**alters §5**, so §G1.3's re-verify trigger fires. §3 carries the disposition table and the §G1.3 line.

---

## 0. What I read, and in what order

Per §G1.2 step 1, the build session's assigned reading first, then the document, then — and only then —
the prior rounds.

1. `DESIGN.md` Part I: §G0.3 (ll. 48–56), §G1.1 (68–116), §G1.2 (118–149), §G1.3 (151–162), §G4.2
   (310–316), §G4.3 (318–322), §G4.4 (324–331), §G4.6 (341–348), §G5.1's phase table (356–377),
   §G9's template (508–542).
2. `DESIGN.md` Part II, the **Phase 1 spec** (585–658) including its *Doc gate* (649–651) and its
   *Required inputs* (636–642).
3. `RESEARCH.md`: §3.4 (285–300), §4.1 step 1 (473–477), §4.3 (509–526), §4.4 (528–565), App A.3's
   directive table (1177–1190), App D.1 (1331–1332), App D.3 (1366–1367), App D.4 (1369–1382),
   App E row 16 (1413), App F.8 (1521).
4. `PHASE_1_DOC.md` — the sections the candidate set reaches, read in full at each site: §0.5's lesson
   paragraph (144–156), §0.9's V9-3 bullet (627–639), **§0.10 entire** (735–866), §3 entire
   (1028–1078), §4.2.3's Gradle paragraph (1079–1090 region), §4.7.4's service declarations
   (2098–2200), its drain-window prose (2320–2345), its precondition (ii) (2414–2426), its
   absent-verbs table with preamble (2463–2492), §4.10's bail-registry prose (2843–2850), **§5 entire**
   (2979–3066), §6 entire (3069–3090), §9's milestone table (3243–3284), §11.1's `[D-P1-25]` (3508),
   §12 items 1–8 (3776–3786). Section inventory checked by heading against §G9.
5. **Then** `PHASE_1_REVIEW_10.md` in full, including its `## Resolutions` (733–915).
6. **Then** the earlier rounds, by targeted grep and by reading the passages the greps returned:
   `PHASE_1_REVIEW_3.md` ll. 175–230 and its `## Resolutions` F3-2 row; `PHASE_1_REVIEW_4.md`
   ll. 100–150, 750, and its `## Resolutions` (847–880); `PHASE_1_REVIEW_8.md`'s finding headers and
   §5 disposition table (72–520). Greps for `blendFunc`, `DRAWBUFFERS`, `drawBuffers`,
   `bindAttributeLocation`, `clearColor`, `five words`, `separator`, `pipe`, `2489` were run across
   all eleven prior review files.

### 0.1 Reads beyond the assigned list, each with the finding it turned on

- **`DESIGN.md` Phase 6's spec (960–1000) and Phase 9's spec (1190–1222), and the dropped-item audit
  (1578–1584).** V11-1 turns on all three; they are the ground truth for who owns `blendFunc`. §G1.1
  l. 78 bars a *build* session from other phases' specs; a verify session auditing an ownership claim
  the document makes about another phase has no such bar, and the read is disclosed here regardless.
- **`git`, read-only.** `git log`, `git status --short`, `git show <rev>:PHASE_1_DOC.md`, and
  `git diff -U0 1d55717..2913008` — to re-derive §0.10's §G1.3 gate claim from the baseline rather
  than from the fix-up's account of it (§2 item 1), and to establish that §0.10's internal line
  numbers are the *baseline's* (V11-5). No git command that writes was run.

### 0.2 Deviations, and omissions recorded as omissions

- **The `*.txt` transcripts in the repo root were not opened.** §G1.2 bars a reviewer from the author's
  conversation context; `PHASE_1_REVIEW_9.md` §0.2 records a sub-agent breaking this rule in round nine
  and its conclusion being discarded. Not repeated.
- **Not re-derived, and named rather than glossed:** §10's four spike specs, §11.5's upstream requests,
  §8's testability plan, §12's checklist beyond items 1–8, §4.2–§4.6 and §4.8–§4.11 except where a
  candidate landed in them. Ten rounds have swept these; re-fighting settled ground spends the round's
  budget on nothing. §2 says what *was* derived.
- **Template ground truth** (`build.gradle`, `settings.gradle`, `gradle.properties`,
  `gradle/scripts/*`, `src/**`, `.github/workflows/*`, `README.md`) was not opened this round. No
  candidate turned on a template fact; round ten verified `apply from:` at `build.gradle` ll. 100, 238,
  239 and nothing has moved since. Recorded as an omission, not as coverage.
- **No build, test or gradle invocation.** §G1.2 inherits the build session's no-code rule.

### 0.3 Network use

**None of any kind.** No finding this round turns on a platform fact, and the pin table's fourth
observation held in round ten's §0.3.

### 0.4 Sub-agent disclosure

This round ran as an **automated fan-out of roughly 22 read-only agents** under a mechanised
re-derivation gate: candidate finders swept assigned regions, each candidate was put to **two
independent refuters**, and a gate re-resolved every citation against the file before the candidate
reached this session. Seven candidates survived to me. **The gate confirmed anchors only** — it does
not generate findings, and an anchor check is not a finding. Every claim admitted to §1 below was
re-opened at its source by this session and is admitted on that derivation, not on the fan-out's.

**What the refutation stage changed, disclosed because it is the round's real work:** five of the
seven candidates arrived proposed as *corrections* and were argued down to *notes* by their refuters;
I re-derived each and agree with four of those five demotions and disagree with none. **One candidate
was cleared outright** on my own derivation and appears in §2 rather than §1 (the §3 unmapped-rows
candidate — §2 item 3). **One candidate the refuters demoted to a note I have kept a note** but with a
different diagnosis than the finder gave (V11-2). **One candidate the refuters left at *correction*
survives at correction** (V11-1). I do not have the pre-gate candidate list, so I cannot enumerate
what the gate dropped before it reached me; that limit is stated rather than papered over.

---

## 1. Findings

### V11-1 — §3's `blendFunc` row and §5.2's restatement give per-draw `blendFunc` to Phase 9 at v0.3; no cited source says so and `DESIGN.md` routes it to Phase 6 three times · **correction** · **touches §5: yes**

**Location.** §3's conformance map, the `blendFunc` row, **l. 1049**; restated in §5.2's
pixel-transfer row consumer column, **l. 3018**.

**Claim under test.** That the design element satisfying App D.4's `blendFunc | ivec4` row is
attributed to the phase the contract ground truth assigns it to.

**What the document says.**

- l. 1049: *"Phase 6 owns the value provider and its cadence (`DESIGN.md` §G5.1 puts App D's inventory
  at **v0.1**); **Phase 9 owns per-draw dynamics at v0.3**."*
- l. 3018, in the consumer column of §5 — the section §5's own preamble calls *"a contract that later
  phases build against"*: *"**9** (per-draw `blendFunc` dynamics at v0.3)"*.

**What the sources say, each opened at the line.**

1. `DESIGN.md`:986 — *"**`blendFunc` observation** via GlStateManager cooperation (§G4.6, App E row
   16)"* — is a dedicated bullet in **Phase 6's** *Scope — in*. Phase 6's milestone is **v0.1**
   (`DESIGN.md`:363).
2. `DESIGN.md`:1204–1206 — Phase 9's *Scope — in* enumerates its per-draw dynamics **exhaustively**:
   *"`entityId` (per entity, via the RenderManager hook), `blockEntityId` (per TE, via
   TileEntityRendererDispatcher), `entityColor` (hurt/flash tint) — value computation + the Phase 6
   per-draw upload path"*. `blendFunc` is not among them, and the clause that closes the bullet routes
   the *upload path* to Phase 6.
3. `DESIGN.md`:1212–1213 — Phase 9's *Scope — out*: *"uniform upload mechanics (Phase 6)"*.
4. `DESIGN.md`:1581 — the dropped-item audit, whose whole function is to record where an item with no
   obvious home was placed: *"`blendFunc`/GlStateManager → **P6 + G4.6**"*. Never Phase 9.
5. `DESIGN.md`:366 — §G5.1's Phase 9 row scopes the phase as *"Alias resolution, per-mod merge,
   held-item, entity/TE id uniforms"*. The phase **title** contains the words "per-draw dynamics";
   its scope does not contain `blendFunc`.
6. `RESEARCH.md`:1413 — App E row 16 puts the `blendFunc` observation on `GlStateManager` as
   *"4-adjacent state cooperation"*.

**The one text that cuts the other way, and why it does not carry the claim.** `DESIGN.md`:970–971
lists `blendFunc` among Phase 6's per-draw dynamics *"at their hooks (Phases 7/9/10 invoke)"*. That
gloss attaches to the **whole list** and says who invokes hooks, not who owns which uniform; Phase 9's
own spec then resolves which of that list is its, and `blendFunc` is not in it. Reading the gloss as an
assignment requires overriding items 1–4 above. The document does not report this as an input
contradiction (§G1.1 l. 106) — it states the Phase 9 attribution as settled fact, twice.

**The document also contradicts itself.** §9's milestone table, **l. 3258**, prices the same verbs:
*"the first consumers are v0.1: **Phase 6's** center-depth readback and its **`blendFunc` (`ivec4`)
uniform**"* — Phase 6, v0.1, no Phase 9. §5.2's own row at l. 3018 names **6** for `blendFunc` in the
same cell that names **9** for it. And `Phase 9` occurs in exactly three places in the whole document
(ll. 1049, 2766, 3018): the two under review, plus a log-channel mention. §11.4 carries hand-off blocks
for every other phase §5.2 names as a consumer; it carries none for Phase 9.

**Why this is not settled ground.** Round three raised the `blendFunc` gap (F3-2) and hedged the owner
as *"per-draw dynamics shared with Phase 9 at v0.3"* (`PHASE_1_REVIEW_3.md`:297). **Round four
attacked that hedge and rejected it**: *"The scope defence — that `blendFunc` is App D.4 per-draw
dynamics and therefore Phase 9's at v0.3 — **inverts into the finding's strongest support**"*, citing
986 / 363 / 1581, and ruling *"Round three's hedge … is **unnecessary**"*
(`PHASE_1_REVIEW_4.md`:124–130). The §0.5 fix-up applied the verb, adopted round four's Phase 6
citation — and kept the Phase 9 clause anyway, recording *"§5.2's pixel-transfer row names **6** and
**9**"* in `PHASE_1_REVIEW_3.md`'s `## Resolutions`. So the attribution is not a cleared finding; it is
a rejected defence that was written into the document alongside the correction it was offered against,
and no round since has re-opened it.

**Failure scenario.** A Phase 6 build session — a real declared dependent, at **v0.1**, whose spec
gives it the `blendFunc` observation — reads §3 l. 1049 and §5.2 l. 3018 and finds ownership split:
value provider mine at v0.1, per-draw dynamics Phase 9's at v0.3. The natural response is to design the
provider now and defer the per-draw upload path to v0.3, which slips a v0.1 obligation `DESIGN.md`:986
gives Phase 6 outright, and hands it to a phase whose *Scope — out* refuses it. Symmetrically, a §G5.3
integration review reading §5.2 finds Phase 1 asserting an ownership `DESIGN.md` never granted.

**Severity: correction, not note.** This is the same shape as round eight's **V8-2** — *"the composite
`countInstances` loop is assigned to Phase 5, but `DESIGN.md` assigns it to Phase 7"* — which round
eight rated **correction · touches §5: yes** and which the sixth fix-up applied. An ownership-plus-
milestone misattribution stated in the conformance map and repeated in the contract section is
precisely the class §G1.2 l. 130–132 puts second in priority order. Not blocking: no interface is
missing, no dependent is unable to build; the `ivec4` overload exists and Phase 6 is named correctly in
three other places.

**Touches §5: yes.** l. 3018 is inside §5.2. The §3 half **cannot** be fixed alone: §5.2's entry is
the more explicit of the two (*"per-draw `blendFunc` dynamics at v0.3"* is unambiguous where §3's
sentence is at least ambiguous), so a §3-only edit leaves the stronger statement standing in the
binding section.

**Fix shape.** Delete *"; Phase 9 owns per-draw dynamics at v0.3"* from l. 1049 — the row's next clause
already carries the correct owner (*"that observation is Phase 6's"*) — and delete
*"**9** (per-draw `blendFunc` dynamics at v0.3)"* from l. 3018's consumer column. If Phase 9 is to be
named at all, name it only as an **invoker** per `DESIGN.md`:970–971's *"at their hooks (Phases 7/9/10
invoke)"*, without attaching `blendFunc` or a v0.3 milestone to it — and if the fix-up judges
`DESIGN.md`:970–971 genuinely ambiguous against 986/1204/1581, §G1.1 l. 106 makes that a
*contradiction to report* in §3/§11 with the ruling and its provenance, not a fact to state.

---

### V11-2 — §4.7.4's absent-verbs table: the instanced-draw row carries three cell separators where every other row carries four, so the assignment column §5 routes dependents to renders empty · **note** · **touches §5: no**

**Location.** §4.7.4, *"Verbs deliberately absent"* table: header **l. 2479**, defective row **l. 2489**.

**Claim under test.** That the table delivers a per-row answer in its third column — the column §5.2
sends dependents to.

**Evidence, derived mechanically.** A pipe count over ll. 2479–2489 returns `4` for the header and for
every row 2480–2488, and **`3` for l. 2489**. In rendered markdown that row therefore has two cells,
not three, and its *"Who requests it — or owns the served work, or owns the deliverable that decides"*
cell is **empty**. The assignment content — `7` the composite/final execution owner at `[v0.5]`, `6`
the `instanceId` upload, `5` the buffer estate — sits at the tail of the *Why absent* cell instead. The
tell that a separator was deleted rather than never present is the missing sentence terminator at the
join: ``…and is deleted `[D-P1-33]` Nobody **requests** this verb…``.

**Why the row matters and not another.** Round ten's own rewritten preamble points at this row's third
column by name: *"the instanced-draw row is that case: nobody requests an instanced verb, and **the
phases named there** own the work that replaces it"* (l. 2471). §5.2 makes the table the authority
for absent-verb ownership: *"§4.7.4's closing table names the verbs that are absent on purpose and who
is expected to ask for each"* (ll. 3038–3039). This is the one row whose assignment moved across
rounds eight, nine and ten.

**Disposition against round ten.** `PHASE_1_REVIEW_10.md`:875–879 **saw this line and disclosed it**,
declining to change it: *"§4.7.4's instanced-draw row (l. 2489) has a missing **sentence boundary** …
it is punctuation in a row no finding touches, and changing it was declined"*. That is a disclosure,
not a clearance — the decline was on licence grounds, not merit — and the **diagnosis is wrong**: the
defect is a missing **cell separator**, not a missing period, and it empties a column rather than
blurring a sentence. Recorded here with the correct diagnosis so a twelfth session does not re-inherit
the wrong one.

**Severity: note.** No information is lost — the assignments are in the immediately preceding cell, and
§5.2's own consumer column carries the same 7/6/5 assignments independently — so no dependent builds
the wrong thing. That is the same calibration round ten applied to V10-3, which was a defect in *how a
column describes itself* rather than in what it says, and was rated a note.

**Fix shape (recorded, not applied).** Insert the `|` **after** the discriminator sentence, not before
it: ``…is deleted `[D-P1-33]`. Nobody **requests** this verb; what follows is who owns the served work
instead. | **7** — the composite/final **execution** owner …`` — so that the preamble's claim at
ll. 2468–2469 (*"each row's *Why absent* cell says which kind it is"*) stays true of this row once the
assignment content moves into column three. Confined to §4.7.4.

---

### V11-3 — §4.7.4's `drawBuffers` javadoc attributes the "draw-buffers = none" fact to `RESEARCH.md` §4.3, which does not contain it · **note** · **touches §5: no**

**Location.** §4.7.4, `FramebufferService.drawBuffers` javadoc, **l. 2132**.

**Claim under test.** That the single site in the document where the contract's "draw-buffers = none"
state is mapped cites a source that carries it.

**Evidence.** l. 2132 reads *"…the contract's "draw-buffers = none" state (**RESEARCH.md §4.3's
first-person item overlay**), NOT "leave the current selection alone""*. `RESEARCH.md` §4.3
("Framebuffer architecture") runs **ll. 509–526** and ends at *"**Final** renders to the vanilla
framebuffer (anaglyph-aware color masking)"*; nothing in it mentions the first-person item overlay or
a draw-buffers-none state. §4.4 ("Per-frame flow") begins at **l. 528**, and the sentence is at
**l. 561**: *"the first-person item overlay routes through **draw-buffers=none**"*. The fact is real
and the ruling built on it is sound; the section number is one off.

**Provenance for the mis-numbering.** Round four's finding that produced this javadoc cited the source
correctly — `PHASE_1_REVIEW_4.md`:750 gives it as `RESEARCH.md:561` — and the §0.5 fix-up applied it
under the wrong section heading. So this is an unlitigated transcription error, not a cleared one.

**Why it is worth recording.** This javadoc is the document's one **contract-visible semantic ruling**
about draw-buffer routing (a zero-length array *is* the "none" state, and there is no "leave it alone"
verb). §G4.2 (`DESIGN.md`:313–314) requires contract-visible designs to *"cite their provenance row"*.
The provenance is cited, so §G4.2 is satisfied in substance — but a Phase 5 reader checking the ruling
against §4.3 finds nothing supporting it. That is the failure mode §G1.2 l. 130–132 names in its
weaker form.

**Severity: note.** One section number in a javadoc comment, one section away from the truth; nothing
in the design changes. Round eight rated the same shape (V8-6, §3's first row attributing a restriction
to §4.4) a note.

**Fix shape.** *"(RESEARCH.md §4.4's first-person item overlay; App A.3's `N` = none)"* — App A.3's
directive table at `RESEARCH.md`:1187 carries *"per-program draw-buffer routing (digits 0–7, `N`
none)"* and is the stronger citation for the "none" state as such. A javadoc comment inside the
§4.7.4 block, not a declaration: no signature moves and §5 is untouched.

---

### V11-4 — §6's 3→4 row is labelled *"a facade call fails"*, and cause (b) that round ten added inside the same cell says no facade call failed · **note** · **touches §5: no**

**Location.** §6, the 3→4 row, **l. 3083** — the *Failure* cell against its own *Behavior* cell.

**Claim under test.** That the row's label admits both of the causes the row was widened to receive.

**Evidence.** The *Failure* cell reads *"**A facade call fails at the driver level**, and the failure
is not attributable to one uniform or feature"*. Round ten's V10-2 widened the *Behavior* cell of the
same row to enumerate two causes, the second of which is *"(b) **the error may never have been ours**,
since the elision bit tracks *facade* calls while the GL error flag is per-context (§4.7.4), so a
window can hold an error this facade did not cause"*. Under cause (b) **no facade call failed** — the
row's own label asserts the proposition its body exists to deny. §4.7.4 establishes the case at length
(ll. 2330–2331: *"the leading drain elides its query and the trailing drain returns an error our
facade did not cause"*), so this is not loose wording about a hypothetical.

**Corroborating tell.** §4.7.4's delegation to this row quotes **only the second half** of the label:
*"The case falls to §6's *"not attributable to one uniform or feature"* row (3→4)"* (ll. 2422–2423).
The clause it drops is the clause that does not fit the case being delegated.

**Severity: note.** The *Behavior* cell states both causes explicitly, in numbered form, and states the
correct response for each; a reader who reaches the row is not misled about what to do. The label is a
category name that has not been swept with the cell round ten rewrote.

**Fix shape.** Either widen the cell — *"**A driver error surfaces in a facade drain window and is not
attributable to one uniform or feature** (including an error no facade call produced)"* — or add one
clause to the body noting that under (b) *"a facade call fails"* names the detection point, not the
origin. Confined to §6.

---

### V11-5 — §0.10 states that its citations were re-resolved against the finished file; all four of its internal citations are in the pre-fix-up file's coordinates, and the unflagged one misresolves · **note** · **touches §5: no**

**Location.** The claim at **ll. 830–831**; the citations at **l. 753**, **l. 829** and **l. 853**.

**Claim under test.** That §0.10's closing lesson — *"name the site to the line, and re-resolve the
line"* — is applied to §0.10's own line numbers.

**Evidence.** ll. 830–831: *"This session's own citations were **re-resolved against the finished
file** for that reason, **not against the file it started from**."* The file it started from is
`1d55717`, 3709 lines; the finished file is 3887. Against those two:

- **l. 753**, unflagged: *"widen it and **§5.2 l. 2846**'s restatement as well"*. §5.2's non-verbs row
  is at **l. 3019** in the finished file and at l. 2846 in the baseline (`git show
  1d55717:PHASE_1_DOC.md`, l. 2846 is that row verbatim). Finished-file l. 2846 is §4.10's
  bail-registry prose — *"registry"). This section builds the slot and nothing else."* — so a reader
  following the citation lands nowhere near §5.2. This is the exact failure §0.10 diagnoses two
  paragraphs earlier.
- **l. 829**, unflagged: *"for two rows that **are** at 2907 and 2910"*, present tense. The two §6
  GL-error rows are at **3080** and **3083** in the finished file; 2907 and 2910 are their baseline
  positions.
- **l. 853**, honestly flagged: *"ll. 1905–2079 **as round ten read them**"* — correct against the
  baseline (the `java` fence opens at baseline l. 1905; the current one opens at l. 2011) and
  disclosed as such. This is the right practice, and it is also an exception the blanket sentence at
  l. 830 does not admit.

**Severity: note.** §0.10 is a fix-up addendum, not a contract section; the citations that carry
obligations to other phases (§5, §11.4) are unaffected, and every §0.10 citation also names its
*section*, so a reader recovers. The defect is that a paragraph teaching a citation discipline does not
follow it — worth recording precisely because the next session will inherit the sentence as if it were
a guarantee.

**Fix shape.** Either re-resolve l. 753 to *"§5.2 l. 3019"* and l. 829 to *"3080 and 3083"*, or narrow
ll. 830–831 to what is true — that §0.10's internal line numbers are given in the coordinates of the
file round ten reviewed — and say so once, at the top of the list, on the model of l. 853.

---

### V11-6 — *"five words removed from the middle"* undercounts the elision it diagnoses; the dropped span is seven words, and the miscount is at both sites · **note** · **touches §5: no**

**Location.** §0.10 **l. 782**; the round-ten insertion into §0.9 at **l. 634**. The elided span lives
in §12 item 4b at **l. 3782**.

**Evidence.** Both sites say the withdrawn §4.2.3 quotation dropped *"five words"* from the middle of
§12 item 4b. The withdrawn quotation ran *"does not export the class to the applying build script, so
`new SeamClasspathArguments(...)` will not resolve"* (verifiable at baseline l. 1083). §12 item 4b
reads *"does not export the class to the applying build script **at the time that script is
compiled**, so `new SeamClasspathArguments(...)` will not resolve"* (l. 3782, and identical at baseline
l. 3609). The dropped span is **"at the time that script is compiled" — seven words.**

**Why this is a miscount rather than a counting convention.** Round ten's own `Resolutions` names the
span verbatim: *"Repairing it was available — restore **"at the time that script is compiled"** and the
`ext` clause"* (`PHASE_1_REVIEW_10.md`:791). The fix-up had the exact string in view when it wrote
"five".

**Severity: note.** A historical count inside a fix-up addendum; the substantive account of V10-4 —
what was withdrawn, why, and what survives — is accurate, and the underlying edit (§4.2.3 now claims
agreement rather than quoting) is correct, which I checked against l. 3782 independently.

**Fix shape.** "seven words" at ll. 782 and 634 — or drop the count for *"with a qualifying clause
removed from the middle and no ellipsis"*, which cannot go stale.

---

## 2. What was checked and came back clean

Named because a round reporting only findings misrepresents its coverage, and because on this document
an item that holds on derivation *is* the round's product. Items 1–4 are my own derivations; item 8
reports the fan-out's coverage as the fan-out's.

**1. §0.10's §G1.3 gate claim is sound, and I re-derived it from the baseline rather than from the
fix-up's account.** This decides whether the phase was validly closed by the seventh fix-up, so it is
the one claim I refused to take on citation. `git diff -U0 1d55717..2913008 -- PHASE_1_DOC.md` produces
eleven hunks, and the largest gap between them runs from baseline l. 2307 to baseline l. 2910. §5's
baseline extent is ll. **2806–2895** (the current §5 at ll. 2979–3068 less the 173-line offset the
diff establishes, confirmed by re-resolving §5.2's non-verbs row at baseline 2846 → current 3019 and
§6's rows at baseline 2907/2910 → current 3080/3083). That range lies wholly inside the untouched gap:
**§5 is byte-identical across the seventh fix-up.** The §4.7.4 signature block's baseline extent
(ll. 1905–2079) likewise falls between the hunks at `-1082,4` and `-2221,3` and is untouched. The
3709-line baseline figure, commit `1d55717`'s existence, and the working tree's identity with `HEAD`
all check out. **§0.10's reading of §G1.3 was therefore correct when it was written**: the trigger did
not fire on round ten's corrections, and the phase closed validly. V11-1 is what re-opens it — a new
§5 change, not a defect in the old ruling.

**2. Round ten's four corrections are correctly applied and correctly reasoned, each re-derived.**
V10-2's substantive fix is right: §6's rung-2 row (l. 3080) names both causes and delegates to the 3→4
row, and the 3→4 row now carries the same (a)/(b) enumeration §4.7.4's precondition (ii) carries at
ll. 2417–2421. V10-3's ownership derivation holds at source — `DESIGN.md` ll. 777–782 make the
engine-flag map a Phase 3 *required output*, its worked example routes `backFace.*` to Phase 7, and
ll. 1053–1055 give Phase 7 the wiring — and §5.2's non-verbs-row clause survives branch (a) true by its
own scope, exactly as §0.10 argues. V10-4's replacement claim is true: §12 item 4b (l. 3782) states the
same conclusion and now carries the same compilation-order reason including the `ext` clause. V10-1's
restored multiplier is coherent with §7's *"43 **slots**"* and the deliberate refusal to re-import 43
as a per-frame event count holds. The three §5.2 rows §0.10 names as deliberately unmoved are each
unmoved for the reason given. **My findings V11-2, V11-4 and V11-6 all sit adjacent to round ten's
edits without contradicting any of them.**

**3. Dropped on my own derivation: the candidate that §3 leaves App A.3's `DRAWBUFFERS` routing, the
per-buffer clear/`colortexNClearColor` overrides, and pre-link attribute binding at 10/11/12
unmapped.** The candidate reasoned by parity with §3's App F.7 rows (`alphaTest`/`blend`/`scale`,
mapped because Phase 1 supplies the verb though the policy is elsewhere). It does not survive two
things. First, **a prior round examined exactly these three items and cleared each by name**:
`PHASE_1_REVIEW_3.md` ll. 200–216 runs an item-by-item contract sweep and records
*"`colortexNClear`/`ClearColor` → `clearColor`/`clear`; … `DRAWBUFFERS` / `RENDERTARGETS` →
`drawBuffers`; … §4.2's pre-link attribute binding at 10/11/12 → `bindAttributeLocation` … ✅ all
clean"*, concluding *"Two dirty rows out of the whole contract"* — `blendFunc` and the composite/final
draw state — where "dirty" meant *unserved by a verb*, which is the standard §G9's "ZERO unmapped
rows" is about. Second, on my own reading none of the three carries a contract-visible **design
decision** taken in this phase that would need a provenance row: `clear`/`clearColor` and
`bindAttributeLocation` are bare verbs whose every contract-bearing parameter is supplied by the
caller. The one exception — `drawBuffers`' ruling that a zero-length array *is* the "none" state — does
carry such a decision, and it **does** cite provenance inline; that the cited section number is wrong
is V11-3, which is where the surviving half of this candidate went.

**4. Structural and spot checks I ran myself.** All thirteen §G9 sections are present as `##` headings
in order (0 Header … 12 Implementation checklist), and §5.4 correctly reads "none" — Phase 1 has no
dependencies, so the *consumes* half of the interface-honesty check is vacuously clean. §3's four
§4.1-step-1 probe rows and the GL-3.0 mipmap gate match `RESEARCH.md` ll. 475–476 field for field. The
`ivec2` row's two citations resolve: `atlasSize` at App D.3 l. 1366 and `eyeBrightness` /
`eyeBrightnessSmooth` at App D.1 ll. 1331–1332, all genuinely `ivec2` — the depthtex1-unit-11 class of
substitution checked for and absent. `shaders.debug.save` is at App F.8 l. 1521 as §3 l. 1044 says.
App D.4 l. 1376 declares `blendFunc` as `ivec4`, so the verb §3 l. 1049 maps to it is the right verb —
the row's **only** defect is the ownership clause (V11-1). `/* DRAWBUFFERS:XXXX */` with its `N`=none
state is at App A.3 l. 1187. `blendFunc` occurs at eight sites in the document (ll. 146, 1049, 2116,
2441, 2484, 3018, 3258, 3508) and `Phase 9` at exactly three (ll. 1049, 2766, 3018 — the middle one a
log-channel mention), which is what bounds V11-1's fix to two clauses and establishes that no third
site restates the attribution.

**5. §6's ladder, beyond V11-4's label.** The 3→4 row's self-description as *"the destination of five
delegations"* is literally checkable and literally true; §4.7.4's precondition (ii) (ll. 2414–2426),
§5.2's GL-error row, `[D-P1-30]`, `[D-P1-32]` and §11.4 all land on it, and all agree on the
destination. The unnumbered row for a single feature's GL failure is honestly labelled unnumbered
rather than mislabelled a rung, and carries the upstream request (§11.5 item 4) that would close the
gap at source. `DESIGN.md`:217's citation for rung 1 resolves.

**6. Round ten's disclosure practice is sound and was worth having.** Two of this round's six findings
(V11-2, V11-6) were reachable *because* round ten wrote down what it saw and did not take, and what
string it declined to restore. That is the mechanism §0.10's own lesson recommends, working. V11-2
corrects the disclosure's diagnosis; it does not fault the disclosure.

**7. Deliberately not re-raised.** §5.2's two third-kind consumer entries and the per-revision
changelog row's *"Changed in this revision (§0.9)"* label are each named, argued and knowingly left in
§0.10 ll. 761–768 and 788–799. I re-derived both arguments and agree with them; re-raising a decision
that is recorded with its reason and its cost is loop maintenance, not review. The elision question
(whether the replay cost inverts the decision to keep the elision) is a **design call**, correctly
priced at all five sites, and is not a finding.

**8. Reported by the fan-out and not independently re-derived by me, marked as such.** §5.2's
signature honesty — every type, method, overload and value object §5.2 names resolving to a declaration
in §4.7.2–§4.7.5 with matching arity, and §5.3's fourteen convention types — was swept by two agents
with no defect found; I confirmed only the entries V11-1 touches. Twenty-two of §3's twenty-five rows
were re-resolved at their sources by the fan-out with no defect found; I re-opened five of those myself
(item 4) and found them sound. The end-matter counts (ten review files, seven fix-up addenda) and the
`[fix-up: …]` marker claim were checked by the fan-out against the diff. These are reported as the
fan-out's coverage, not as mine.

---

## 3. Verdict

# PASS-WITH-CORRECTIONS

**One correction, five notes, zero blocking.** The clean-to-dirty ratio is the honest headline: an
eleventh adversarial pass over a document that has absorbed seven fix-ups found **one** substantive
defect, and it is an ownership clause a review four rounds ago already argued against and the fix-up
kept anyway. Everything the last three rounds corrected is correctly corrected; the §G1.3 gate claim
that closed the phase was true when it was made; nothing structural is missing.

**PASS was live and is refused for one reason.** V11-1 is a misattribution of ownership and milestone
inside §3's conformance map and inside §5 — the two places §G1.2 ranks first after the doc gate — and
`DESIGN.md` contradicts it in three independent passages while the document contradicts it in a fourth
of its own. Softening that to a note to reach PASS would be the inverse of the failure this cadence is
guarding against. The five notes are **recorded and left unapplied**; none of them blocks anything, and
a fix-up that applies only V11-1 has discharged this review.

### Per-finding §5 disposition

| Finding | Severity | Touches §5? |
|---|---|---|
| **V11-1** §3 l. 1049 and §5.2 l. 3018 give per-draw `blendFunc` to Phase 9 at v0.3; `DESIGN.md` routes it to Phase 6 | **correction** | **yes** — l. 3018 is inside §5.2, and the §3 half cannot be fixed alone because §5.2's phrasing is the more explicit of the two |
| **V11-2** §4.7.4 l. 2489's missing cell separator empties the assignment column | note | **no** — §4.7.4 only; §5.2's consumer column already carries the same 7/6/5 assignments |
| **V11-3** §4.7.4 l. 2132 cites `RESEARCH.md` §4.3 for a fact in §4.4 | note | **no** — a javadoc comment, not a declaration |
| **V11-4** §6 l. 3083's *Failure* label contradicts cause (b) in its own cell | note | **no** — §6 only |
| **V11-5** §0.10's internal citations are in baseline coordinates against a claim that they are not | note | **no** — §0.10 only |
| **V11-6** "five words" undercounts a seven-word elision, at two sites | note | **no** — §0.10 and §0.9 only |

### §G1.3 line

**§G1.3's *"re-verify only if §5 changed"* trigger fires.** V11-1's fix edits §5.2 l. 3018, which
alters the *Cross-phase interfaces* section on the rule's own textual reading — the reading rounds
eight, nine and ten all applied, and which round ten's §2 item 1 anchored a second time on §G1.3's
third bullet (*"no §5 change outstanding"*). Accordingly:

- A fix-up session (§G1.3) applies V11-1 to `PHASE_1_DOC.md` and records its resolution under a
  `## Resolutions` heading **in this file**. The five notes may be applied or declined; a decline
  should be recorded with its reason, per the convention §0.6 established.
- Because that fix-up alters §5, **`PHASE_1_DOC.md` goes through a fresh verify session before any
  dependent consumes it.** Until that round returns, the phase is **not** verified under §G1.3's third
  bullet: there is a §5 change outstanding. §0.10's closing paragraph — *"this fix-up closes the phase …
  Phase 2, Phase 3 and everything downstream unblock"* — was correct on round ten's corrections and is
  superseded by this round's, and the twelfth session should expect §0.11 to say so rather than to
  restate it.
- If the fix-up instead judges V11-1 wrong and declines it, that decline **must** be argued against
  `DESIGN.md` ll. 986, 1204–1206, 1212–1213 and 1581 at the line, and against
  `PHASE_1_REVIEW_4.md`:124–130's prior rejection of the same attribution — not against this review's
  framing. A decline on those grounds leaves §5 unchanged and closes the phase; §G1.3 permits it and
  this review does not pre-empt it.
- If only the notes are applied and V11-1 is declined, §5 is untouched and no further verify session is
  owed.

*Per §G1.2 this session stops here. It wrote no code, ran no build and no test, made no network
request, and created exactly one file: this one. `PHASE_1_DOC.md`, `DESIGN.md`, `RESEARCH.md`, and
`PHASE_1_REVIEW_1.md` through `PHASE_1_REVIEW_10.md` — including their `## Resolutions` sections — are
unmodified.*

---

## Resolutions

*Written by the **eighth** fix-up session (§G1.3), 2026-07-26, after this review's verdict. **Nothing
above this heading is modified** — the findings, §2's clean list and the verdict are evidence. Line
numbers below are `PHASE_1_DOC.md`'s **after** this fix-up (**4573** lines, against the 3887 this
review read); §0.11 is the addendum recording the session.*

*This fix-up had **two causes**, and they are kept apart here as they are in §0.11. The first is this
review. The second is a **migration of the document from design v1.1 to
`docs/design/v2.0-RC2/DESIGN.md`**, directed by the project owner as §G0.4 step 3. The second is much
the larger, and a `Resolutions` section that folded the two together would misreport what this
review's six findings actually cost — so **everything below is scoped to the findings**, and the
migration is accounted for in §0.11. The one place they meet is the §G1.3 line, because both altered
§5.*

**Method note.** Three disciplines, each made necessary by this review or an earlier one.
*(1) Nothing was adopted on a citation.* Every finding was re-opened at its site before it was
touched, and every source coordinate re-resolved at the source file. V11-3's two were confirmed:
RESEARCH.md §4.4's first-person-item sentence is at **l. 561**, App A.3's `N`-is-none row at
**l. 1187**, and §4.3 does run **ll. 509–526** without containing the sentence — the review's
derivation holds in all three limbs. V11-1's ground truth was **re-derived in RC2's coordinates
rather than shifted from v1.1's**, which is the discipline §G0.4 warns is the whole trap of a
revision migration. *(2) The sweep ran over formulations, not over sites.* `grep -n "Phase 9"` and
`grep -n "blendFunc"` were run **after** the V11-1 edits rather than before: `Phase 9` now occurs at
exactly two places — §3's `blendFunc` row, where it appears in order to be *excluded*, and §4.9.2's
log-channel row — and `blendFunc` at the eight sites this review counted, with §9's row independently
re-opened and confirmed to name Phase 6 at v0.1. *(3) V11-2's mechanical check was re-run, not
trusted.* A pipe count over the absent-verbs table returns **4 for the header and 4 for every one of
its nine rows**; before the fix, that row returned 3.

**Evidence provenance.** `PHASE_1_DOC.md` was byte-identical to `git HEAD` (`9e11b80`) when this
session started, so every claim below is provable by one `git diff` rather than reconstructed:
**53 hunks, 769 insertions, 82 deletions**. **Three hunks fall inside §5** (baseline ll. 3003, 3009,
3018), enumerated in the §G1.3 status below. The §4.7.4 signature block was extracted from the
baseline and from the result and diffed on its own: **exactly one hunk, and it is a javadoc
comment** — V11-3's two lines — with no declaration, no overload, no handle type and no value type
touched. **No network use of any kind.** **Sub-agents: three read-only exploration agents ran during
the planning stage, for design-revision delta enumeration only**; the choice is disclosed because
§G1.3 is silent and the call is therefore this session's. **No finding, coordinate or quotation was
admitted on an agent's report** — every one was re-derived here, and one agent-reported coordinate was
checked and found wrong, which is why that rule is stated as practice rather than as intent. No
directory named `chatlogs/` below `docs/` and no `*.txt` at the repository root was opened, including
the one currently present (§G1.1's forbidden-sources pattern, stated by pattern for that reason).

**Counts.** Six findings: **six applied, none refused, none narrowed.** This review names **fourteen**
distinct sites across them; **eight were edited** and **six were checked and correctly left alone**
(listed below with the reason for each). Two edits reach sites this review did **not** name — §0.10's
new head note, which is where V11-5's branch (b) had to land, and §5.2's per-revision changelog row,
which is owed the moment §5 changes. That asymmetry is the standing one: a review names the sites its
finding turns on; a fix-up owes the sites its *edit* turns on. Two findings offered a choice of fix
shape (V11-4, V11-5) and in both the branch is argued rather than merely taken.

| Finding | Disposition | Where (every site **edited**, not every site named) |
|---|---|---|
| **V11-1** | **Applied, and RC2 makes the case stronger than this review could state.** The review argued the Phase 9 attribution against three `DESIGN.md` passages plus one internal contradiction; all were re-derived, and the migration added a **fifth** independent passage that did not exist in v1.1 — REV2's Phase 6 spec now carries an explicit `blendFunc` notifier-audit duty (ll. 1602–1607) with the matching rule at §G4.6 (ll. 554–557). In RC2's coordinates the five are: Phase 6's *Scope — in* **l. 1601**, its cadence model **ll. 1549–1550** (where *"at their hooks (Phases 7/9/10 invoke)"* names invokers of a whole list, not owners of one uniform), Phase 9's *Scope — in* **ll. 1918–1920** (exhaustive, and `blendFunc` is not in it), Phase 9's *Scope — out* **ll. 1931–1932**, and the dropped-item audit **l. 2419**. The decline branch this review left open is therefore further from viable than when it was written. **Applied wider than the fix shape in one respect, deliberately:** §3's row now states the non-attribution *positively* rather than by deletion alone, because a reader who finds no mention of Phase 9 cannot distinguish a considered exclusion from an oversight — which is exactly the failure this finding documents four rounds of this document making in the other direction | §3's `blendFunc` row, **l. 1286** (the Phase 9 clause deleted; the five-passage derivation and the REV2 notifier duty added in its place). §5.2's pixel-transfer row, **l. 3520** (consumer column: `**9** (per-draw \`blendFunc\` dynamics at v0.3)` deleted; the row's `**6**` entry, which already carried `blendFunc` correctly, is untouched) |
| **V11-2** | **Applied, at the join the review specified.** The separator goes **after** the discriminator sentence, not before it, so the preamble's claim that *"each row's *Why absent* cell says which kind it is"* stays true of this row once the assignment content moves into column three. The review's diagnosis is adopted over round ten's: the defect was a missing **cell separator**, not a missing sentence boundary — and the missing period was real too, so both are supplied | §4.7.4's absent-verbs table, **l. 2799**. Re-verified mechanically after the edit: the header, the separator and all **nine** rows return a pipe count of 4 |
| **V11-3** | **Applied, with the stronger citation the review recommended rather than only the corrected section number.** The javadoc now reads *"(RESEARCH.md §4.4's first-person item overlay; App A.3's `N` = none)"* — App A.3's directive table is the better authority for the "none" state *as such*, and §4.4 is where the overlay sentence actually lives. Both coordinates re-opened at RESEARCH.md before the edit | §4.7.4, `FramebufferService.drawBuffers`'s javadoc, **l. 2442**. This is the **only** hunk inside the signature block, and it is a comment |
| **V11-4** | **Applied — note, and the *body-clause* branch is taken over the widened label.** The review offered both as equal. The label is the name **five sites** delegate to (§4.7.4's precondition (ii), §5.2's GL-error row, `[D-P1-30]`, `[D-P1-32]`, §11.4), so widening it is a change that has to be swept through all five to stay quotable; the distinction it was missing is one sentence, and a reader who reaches the row reaches the sentence. The cell now says plainly that under cause (b) the *Failure* label names the **detection point, not the origin** — the drain window is a *facade* window, so a driver error surfaces on a facade call that did not produce it | §6's 3→4 row, **l. 3585** (*Behavior* cell only; the *Failure* label is deliberately unchanged and the reason is in the cell) |
| **V11-5** | **Applied — note, and the *narrow-the-claim* branch is taken, because this fix-up is itself the argument for it.** The review offered re-resolving §0.10's internal numbers or narrowing the sentence that claims they were already re-resolved. Re-resolving would have made them correct for exactly as long as it took the next edit to land, and this session's edits shift most of the document — so the numbers stand and the **claim** is narrowed to what is true, with a coordinate note stated **once at the head of §0.10** on the model of l. 853's already-honest *"as round ten read them"*, which the review itself named as the pattern. **Extended past the ask, and disclosed as such:** the note also covers §0.10's `DESIGN.md` coordinates, which are v1.1's and which the migration would otherwise have silently orphaned. Their RC2 equivalents are given *in the note* — §G1.3 at ll. 302–320, §G5.3 at ll. 611–638, Phase 3's engine-flag bullet at ll. 1260–1265, Phase 7's at ll. 1693–1695, each re-derived at the line — and deliberately **not** substituted into the body | §0.10's head note, **ll. 761–774** (new). §0.10's blanket claim, **ll. 867–872** (narrowed, and it now names the sentence it replaces so the correction is legible) |
| **V11-6** | **Applied, and the count is replaced by the count *plus the span*, which cannot be wrong.** The review offered "seven words" or dropping the count for a form that cannot go stale. Both sites now carry **seven** *and* quote the elided clause — *"at the time that script is compiled"* — which is strictly more informative than either branch and independently checkable against §12 item 4b. The span was re-counted at l. 4491 before either edit | §0.10's V10-4 bullet, **ll. 818–820**; §0.9's round-ten insertion, **ll. 654–656** |

### Neighbours swept, beyond every `Where` column

- **§9's milestone table, l. 3784's pixel-transfer row.** V11-1's own §1 cites it as the document's
  internal contradiction of the Phase 9 attribution. Re-opened: it names *"Phase 6's center-depth
  readback and its `blendFunc` (`ivec4`) uniform"* at v0.1 and no Phase 9 — correct as it stands, and
  now consistent with §3 and §5.2 rather than contradicting them.
- **§11.4's hand-off blocks.** The review observes that §11.4 carries a block for every phase §5.2
  names as a consumer *except* Phase 9. After V11-1 that is no longer an asymmetry to explain, because
  §5.2 no longer names Phase 9. Checked and left alone on those grounds — and a **Phase 6** block was
  added for an unrelated reason (§0.11), which is the first §11.4 entry that phase has had.
- **§5.2's per-revision changelog row, l. 3511.** Not named by any finding. §0.10 argued — correctly —
  that a row recording per-revision §5 changes is owed **no entry for a revision that changes nothing
  in §5**. §5 changes this revision, so the entry is owed, and the row now carries it. The same
  paragraph relabels *"Changed in this revision (§0.9)"* to *"Changed in the §0.9 revision"*, which
  every prior §5-touching fix-up did to its predecessor and which §0.10 correctly declined to do.
- **`grep -n "unnumbered"` across the whole document.** Run because the migration relabels §6's
  unnumbered rung to `2a`; it confirms the word survives only where it describes the *history* of that
  row and in §11.5 item 4's record of the request that closed it.

### Checked and correctly left alone

*So the next audit can tell a considered omission from an oversight.*

- **l. 2479, the absent-verbs table header** (V11-2's other named site). It enumerates three kinds of
  last-column entry and is true of every row **once l. 2489's cell separator exists**; the defect was
  the row, not the header. Round ten widened this header for V10-3 and it needs no second widening.
- **ll. 753 and 829, §0.10's two unflagged stale citations** (V11-5's named sites). Deliberately **not**
  re-resolved — that is what branch (b) means, and the head note now governs them explicitly. Left
  standing rather than corrected is a decision, not an omission, and the reason is that correcting
  them would guarantee they are wrong again after the next fix-up.
- **l. 853, §0.10's honestly-flagged citation** (V11-5's third named site). It is the model the head
  note generalises. Unchanged, deliberately.
- **l. 4449, §12 item 4b's elided span** (V11-6's source site). Re-counted — *"at the time that script
  is compiled"* is seven words — and correct as written; the defect was the two sites *describing* it.
- **l. 3784, §9's milestone row** — see above.
- **Disclosed because it was seen and not taken:** V11-1's fix shape offers naming Phase 9 as an
  **invoker** per `DESIGN.md` ll. 1549–1550 rather than dropping it entirely. That was available and
  is **declined**, on the review's own reasoning: the gloss names who invokes a *whole list* of
  per-draw dynamics, and reproducing it in a row about **one** uniform would re-import exactly the
  ambiguity the finding exists to remove. §3's row instead states why Phase 9 is absent, which serves
  the same reader without the risk.

### §G1.3 status

**This fix-up altered §5, and the trigger fires.** The baseline hash of
`awk '/^## 5\. Cross-phase interfaces/,/^## 6\. Failure modes/'` was
`b4dc7149d52ae462ed5240b5df7d87291af0351a7e3142a6b6ab7abc2a115e6c`; it is now
`82491afc8a821b70d75eae5e4b26ab3ad1ba147813acef2337243b9da998559b`. Three hunks, and only one of them
is this review's:

1. **This review's** — V11-1's deletion from §5.2's pixel-transfer consumer column (baseline l. 3018).
2. **The migration's** — two new **§5.1** rows (inserted after baseline l. 3003): the engine bring-up
   sequence (`[D-P1-37]`, §4.13) and the `mod.glue` vanilla-texture provider slot (`[D-P1-36]`,
   §4.12). The second exists because REV2's mandated PD §2 completeness check found that App B.3's
   **unit 0 and unit 1** — the vanilla block atlas and `lightmap` — had no expressible source through
   this facade.
3. **Both** — §5.2's changelog row (baseline l. 3009), which records the above.

**No service signature was added, removed or changed.** The seven services, every handle type and
every value type are byte-for-byte what rounds seven through eleven all reviewed; the signature
block's only hunk is V11-3's javadoc, and `[D-P1-36]` explicitly **refuses on the seam** the one verb
(`adopt(int glName)`) that would have changed that, because a raw GL name in an `:engine` signature is
the leak `[D-P1-15]` and §4.7.3 exist to prevent.

So under §G1.3's third bullet the phase is **not verified** — there is a §5 change outstanding —
`PHASE_1_DOC.md` is **not** a valid dependency input, and a **twelfth** verify session is owed before
any dependent consumes it (§G5.3). This review's own §G1.3 line anticipated exactly this and
instructed the fix-up to expect §0.11 to say so rather than restate §0.10's closing paragraph; §0.11
does.

**Three things are left standing on purpose**, and they belong to whoever comes next rather than to
this session:

- **RC2 is still unadopted.** Only step 3 of §G0.4's four-step procedure was performed, by explicit
  direction: the `/verify-loop` harness's line pins and the operator docs are untouched, so the
  project's governing design remains **v1.1** while this document verifies against **RC2**. A twelfth
  session briefed with v1.1 coordinates against an RC2-anchored document would not error — it would
  silently read the wrong text. §G0.4's closing sentence is the instruction: **stop and report**, do
  not guess.
- **The `v10` → `v11` directory roll is owed.** `docs/MOVES.md` ll. 100–110 makes it two steps run
  together, and the second is a harness edit outside this session's scope. Doing only the first fails
  silently. `v<K>` = the highest `§0.K` addendum is therefore unsatisfied at `v10`, knowingly.
- **§4.12, §4.13 and §4.5.2a are entirely unreviewed material** — three new subsections and three new
  decisions (`[D-P1-36]`, `[D-P1-37]`, `[D-P1-38]`), none of which has been through a verify session.
  Round seven's rule prices that honestly: *unreviewed material yields findings in proportion to its
  size, not to the document's maturity.* `[D-P1-36]` is the one that changes what a dependent builds,
  and is where a twelfth session should start.

*Per §G1.3 this session stops here. It wrote no code, ran no build and no test, launched no review or
adversarial agent, made no network request, and modified exactly two files: `PHASE_1_DOC.md` and this
`## Resolutions` section. `DESIGN.md` in all three revisions, `RESEARCH.md`, `PINTONIUM_DESIGN.md`,
`PHASE_2_DOC.md`, `docs/MOVES.md`, the `/verify-loop` harness and its operator documentation, and
`PHASE_1_REVIEW_1.md` through `PHASE_1_REVIEW_10.md` — including their `## Resolutions` sections — are
unmodified.*
