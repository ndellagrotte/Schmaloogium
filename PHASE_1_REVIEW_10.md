# PHASE_1_DOC.md — Verify session, round ten

**Reviewed:** `Schmaloogium/PHASE_1_DOC.md`, 3709 lines, as of the sixth fix-up (2026-07-25)
**Session type:** verify (`DESIGN.md` §G1.2) · **Round:** ten · **Date:** 2026-07-25
**Commissioned by:** `PHASE_1_REVIEW_10_BRIEF.md`
**Verdict:** **PASS-WITH-CORRECTIONS** — two corrections, two notes, zero blocking.
**§5 changed by any correction in this round: NO.** See §3's `§G1.3 line`; it is the sentence that
decides whether the cadence ends here.

---

## 0. What I read, and in what order

The order below is the brief's, and it is a contract rather than a convenience: `PHASE_1_REVIEW_9.md`
and its `## Resolutions` section were read **last**, after my own findings were written down. §G1.2
exists to stop a reviewer inheriting the author's frame; reading the resolutions early would have made
me an auditor of the sixth fix-up's reasoning instead of an independent reader of the document it
produced.

1. **`PHASE_1_REVIEW_10_BRIEF.md`**, complete — the assignment.
2. **`DESIGN.md` Part I in full** (§G0–§G10, ll. 1–575) and the **Phase 1 spec** (ll. 585–658). Other
   phases by title and §G5.1 row only, with one disclosed exception (§0.1).
3. **`RESEARCH.md`** §0 (reading guide, confidence tags) and §1 (mission, non-goals, D-1..D-10); then
   the spec's Required inputs §5.1, §5.2, §5.3, §6.1, §7.2, §12.2; then §4.2, §6.2, §6.3, §7.1, §7.3,
   §7.4 and §11 (§0.1 records which of those are beyond the list and why).
4. **Template ground truth, complete and at source:** `build.gradle`, `settings.gradle`,
   `gradle.properties`, `gradle/scripts/{dependencies,extra,publishing}.gradle`,
   `gradle/wrapper/gradle-wrapper.properties`, all eight files under `src/**`, all three
   `.github/workflows/*`, `README.md`.
5. **`PHASE_1_DOC.md`, §0 through §12, all 3709 lines**, in file order.
6. **Last:** `PHASE_1_REVIEW_9.md` including its `## Resolutions` section, plus targeted greps into
   rounds one through eight.

### 0.1 Reads beyond the assigned list, each with the finding it turned on

| Read | Why, and what turned on it |
|---|---|
| **`DESIGN.md` Part II — Phase 3's spec ll. 774–782 and Phase 7's ll. 1050–1055** | **V10-3.** The finding is about who the absent-verbs column's face-culling entry names, which is an ownership claim, so the brief's five-round precedent applies and the read is disclosed. What it settles: Phase 3's *Scope — in* carries **"Required output — the engine-flag ownership map"** with `backFace.*` → **Phase 7** in its own worked example (l. 779–780), and Phase 7's *Scope — in* carries **"Engine-flag wiring … (`clouds`, `backFace.*`, …)"** (ll. 1053–1055). Phase 3 produces the map; Phase 7 does the work. The document's face-culling row states both facts correctly — the finding is about which of them the *column* is carrying. |
| **`RESEARCH.md` §4.2 (ll. 491–507)** | **V10-1.** The whole finding is an arithmetic reconciliation against *"refreshes ~90 built-in uniforms"* and *"43 program slots"*, which live only here. Also the source the document itself cites at every one of the five `~90` sites. |
| **`RESEARCH.md` §11 (the OQ register, ll. 999–1027)** | The pin-table observation in §0.3. OQ-2's row is what states the volatility assumption my network sample is evidence about, and §G4.4 requires the verbatim question, which is only here. |
| **`RESEARCH.md` §6.2, §6.3, §7.1, §7.3, §7.4** | Read in the same pass as the assigned §6.1 and §7.2 — they are the surrounding paragraphs of two assigned sections, and separating them would have been an artificial saving. No finding turns on any of them. Disclosed because they are beyond the list, not because they earned their place. |
| **`git log --oneline` and `git status --short`, read-only** | The signature-invariance reconstruction limit (§2, item 10). Confirms `HEAD` is `79543cf docs: add review doc for phase 1 doc draft` with `PHASE_1_DOC.md` and `PHASE_1_REVIEW.md` modified in the working tree and every review file after the first untracked — i.e. all six fix-ups uncommitted, exactly as §0.9 l. 677 states. No git operation that writes anything was run. |

### 0.2 Deviations, and omissions recorded as omissions

1. **`PHASE_1_FIXUP_6_BRIEF.md` was NOT read, and the omission is deliberate.** The brief for this
   round tells me the sixth fix-up "overruled the brief that commissioned it" on V9-1, and invites me
   to weigh that the fix-up therefore overruled two documents rather than one. I declined the read,
   because it cannot change the answer: **`DESIGN.md` §G1.3 is the source of the rule, and a
   commissioning brief is not.** A brief that assumes a branch the rule forecloses is a brief that was
   wrong, and the number of documents sharing a mistaken assumption is not evidence about the rule.
   The ruling is tested in §2 item 1 against §G1.3's own words and against §G1.3's own definition of
   the verified state — not against anyone's brief, including this one's.
2. **Rounds one through six were not read end to end**, only grepped for the formulations under test
   (`sound remedy`, `mutating`, `43`/`forty-three`, `~90`, `scale.<prog>`, `countInstances`). Round
   nine's clean list has survived three consecutive audits and the brief instructs me not to spend
   budget re-deriving it; I did not.
3. **§10's four spike specs, §4.8's licensing sections, §4.9's channel and flag tables, §4.10's bail
   registry and §4.11's CI plan were read in full but not re-derived against their sources.** They
   are on the "do not re-fight" list, no site in them was touched by the sixth fix-up, and re-deriving
   settled ground is the failure mode §0.7's lesson warns about from the other side. Read for drift
   against the edited sites; none found (§2).
4. **No `DESIGN.md` phase spec other than 1, 3 and 7 was opened**, and Phases 3 and 7 only at the
   line ranges quoted in §0.1. The three ownership questions the sixth fix-up decided (V9-4's silence,
   V9-7's Phase 5 readmission, V9-8's Phase 4 citation) were audited as *applications* — did the edit
   land, does it say what the Resolutions claims — rather than re-derived from Part II. That is a real
   limit on this round's coverage and it is stated as one: if the Phase 4 citation quoted at four
   sites is misquoted at source, this round would not have caught it. Round nine derived it directly
   and the brief places it on the closed list.
5. **No code, no build, no test, no scope creep.** Exactly one file was created: this one.
   `PHASE_1_DOC.md`, `RESEARCH.md`, `DESIGN.md` and all nine prior review files — **including
   `PHASE_1_REVIEW_9.md`'s `## Resolutions` section, which is this round's primary evidence** — are
   unmodified.

### 0.3 Network use — one sanctioned purpose exercised, one deliberately not

**The pin table (§4.2.6's procedure), run.** Two fetches, 2026-07-25 **~09:55 UTC**:

- `https://repo.cleanroommc.com/releases/com/cleanroommc/cleanroom/maven-metadata.xml` —
  `<release>` = **`0.6.6-alpha`**, `<latest>` = `0.6.6-alpha`, `<lastUpdated>` =
  **`20260724133703`** (2026-07-24 13:37:03 UTC), version tail `0.6.2 → 0.6.6-alpha`.
- `https://api.github.com/repos/CleanroomMC/Cleanroom/releases?per_page=10` — newest tag
  **`0.6.6-alpha`**, `published_at` **2026-07-24T13:37:05Z**. The two sources agree to two seconds,
  which is step 2's cross-check passing rather than a coincidence.

**Say which it is: this is NOT an independent daily sample.** Rounds seven, eight and nine read at
~03:05, ~03:52 and ~04:48 UTC on 2026-07-25; mine is ~09:55 UTC on the **same calendar day**,
5h 07m after round nine's. It extends one observation window rather than opening a new one. What it
adds is duration, not independence: `lastUpdated` has now been unmoved for **20h 18m**, against the
"over fifteen hours" round nine recorded.

**What that implies for the volatility RESEARCH.md's OQ-2 row assumes.** OQ-2 is filed under *"Alpha
drift; **daily cadence**"* and §0.2's date-stamp discipline says platform facts "rot fast here". The
release history now visible does not fit a daily model in either direction: **0.5.16/0.5.17-alpha on
2026-07-09, then nothing for ten days, then seven releases in six days (0.6.0 on 07-19 18:02 through
0.6.6 on 07-24 13:37), then twenty quiet hours.** The cadence is **bursty, not daily** — long silences
punctuated by clusters. That is a mild correction to the *premise* of OQ-2's phrasing and, more
usefully, it is evidence **for** the design already in the document: §4.2.6's re-pin procedure fires
on milestone tags and release runs and says explicitly *"Never on a schedule, and never
automatically"* (l. 1332), which is the right instrument for bursty churn and the wrong one for a
metronome. I raise no finding on it — §11.5 item 1 already asks RESEARCH.md's maintainer to point
OQ-2's status column at §4.2.6/§10.1 and `PINS.md`, which is where this observation belongs, and
§G1.1 forbids me amending RESEARCH.md. **The `0.6.6-alpha` pin row holds unchanged at a fourth
observation.**

**The cited GL page, deliberately not fetched, and I say so as the brief asks.** Three rounds have
confirmed the `glGetError` block quotation word for word and one has confirmed both URLs resolve.
**No finding in this round turns on the GL quotation, the loop rule, or either URL** — V10-1 is an
arithmetic reconciliation and V10-2 a delegation-destination gap, neither of which touches what
`glGetError` promises. A fourth confirmation would buy nothing and would be network use without a
finding behind it.

**No third purpose. No other fetch of any kind was made.**

### 0.4 Adversarial sub-agents — declined, and the choice disclosed

§G1.2 permits them; §G1.3 is silent; the call is this session's and is disclosed either way. **None
was used.** Round eight declined, round nine used them under a hard re-derivation gate and recorded
that one agent breached its instructions, and the sixth fix-up declined. The reasoning adopted here is
the same one on its merits rather than by precedent: the brief requires every load-bearing quotation
to be re-derived by me before it reaches a finding, so a delegated finding is a hop rather than a
saving — and this round's two corrections are both *arithmetic and cross-reference* work over sites I
had to hold in view simultaneously, which is the shape delegation serves worst. **The project owner
was consulted on this call and on the pin fetch, and confirmed both.** Every quotation, line number
and count below was derived by me at the line.

---

## 1. Findings

Two corrections, two notes, zero blocking. Every one of them is in the sixth fix-up's new prose or in
something that prose asserts about the rest of the document — §0.7's standing lesson holding for a
fourth consecutive round.

---

### V10-1 — the recurring-foreign cost figure contradicts its own premise: §4.7.4 states a per-frame ceiling that is a per-*program-set* cost, and it is the one site of five that aggregates · **correction** · **touches §5: no**

**Location.** `PHASE_1_DOC.md` §4.7.4, `[D-P1-32]`'s block, **ll. 2218–2221**. The four sibling sites
that state the same cost correctly and are *not* the finding: §0.9 l. 572, §7 l. 2966,
`[D-P1-32]` l. 3342, §11.4 l. 3486. `RESEARCH.md` §4.2 l. 504 is the cited source.

**Claim under test.** That the sentence

> "A **recurring** foreign error costs one replay per program set per frame, and a program switch
> refreshes ~90 built-in uniforms (RESEARCH.md §4.2), so the ceiling is on the order of ninety extra
> synchronous queries and ninety redundant uploads **per frame** for as long as it recurs."

states a correct ceiling. This is the cost figure V9-1 introduced specifically so that the ledger the
elision was kept against would stop being incomplete, and which §0.9 ll. 570–579 and the
`Resolutions`' refusal note both describe as the record on which a future re-weighing will rest.

**Evidence.** The sentence supplies both multiplicands and then drops one.

1. **Its own premise is per program set.** *"one replay per program set per frame"* — the replay is
   entered once for each program set whose trailing drain came back non-empty, and the recurring case
   is by construction the one where that happens repeatedly within a frame.
2. **The per-replay cost is ~90 + ~90.** The replay is *"re-upload the set draining between uploads"*
   (§4.7.4 l. 2211, §5.2 l. 2837, `[D-P1-32]` l. 3342), and a program switch refreshes ~90 built-in
   uniforms (`RESEARCH.md` §4.2 l. 504, quoted correctly at l. 2219). So **one** replay is ~90
   redundant uploads and ~90 extra synchronous queries — which is exactly the figure the sentence then
   labels *"per frame"*.
3. **The multiplier is dropped.** Ceiling-per-frame = (replays per frame) × (~90). The sentence
   asserts the product equals one factor, which holds only if there is exactly **one** program set per
   frame. The document states the opposite four lines earlier, in the same subsection: dropping the
   elision *"would pay a factor of two on a synchronous driver query **at every program switch in the
   frame**"* (l. 2177), and §7 ll. 2958–2960 makes the same quantity the reason the cost matters —
   *"a program switch is the universal state barrier … and there are 43 slots, so a factor of two …
   would be paid per program switch per frame."* A design in which program switches per frame is the
   large number cannot also be one in which program sets per frame is one.
4. **Four sibling sites decline to aggregate, and they are right to.** §7 l. 2966 — *"costs a replay
   of the whole ~90-uniform set **per program set**"*. `[D-P1-32]` l. 3342 — *"costs a re-upload of
   the ~90-uniform set **per program set**"*. §11.4 l. 3486 — *"a replay of **a program's** whole
   ~90-uniform set … for as long as the foreign error recurs"*. §0.9 l. 572 — *"re-uploading ~90
   uniforms **per program set**"*. §5.2 l. 2837 states the consequence with no figure at all. **§4.7.4
   is the only site that converts the per-set cost into a per-frame total, and it is the site that
   introduced the figure.**

**Why it matters more than an arithmetic slip normally would.** Three things compound.

- **The direction of the error favours the conclusion being defended.** Under-stating the cost the
  elision *creates* makes the incomplete ledger look less incomplete and the decision to keep the
  elision look better supported. That is the identical failure mode §0.9 ll. 638–647 diagnoses for the
  old *"43 program switches per frame"* figure and corrects on exactly that ground — *"The direction
  of the residual error favoured the conclusion being defended, which is the reason it was worth
  correcting rather than noting."* The same test applied to this sentence gives the same answer.
- **It is the record the refusal rests on.** The sixth fix-up declined to re-open the elision
  decision and justified the decline by stating the omitted cost at three sites, describing the ledger
  as incomplete, and leaving the re-weighing to a session that wants to argue it (§0.9 ll. 569–579;
  `Resolutions`, *"The one thing refused"*). That is sound discipline — §2 item 12 says so — **but it
  only works if the recorded cost is right.** A future session re-weighing the elision from §4.7.4
  alone would take a per-frame ceiling of ninety and compare it against a per-program-switch factor of
  two, and would be comparing a per-frame number against a per-switch number.
- **It is a mispricing inside a correction written to repair a mispricing**, which is the exact shape
  round seven caught at `[D-P1-30]` and round nine caught again at the "43".

**Fix shape** (the fix-up's call, not mine). Either restore the multiplier — *"so the ceiling is on
the order of ninety extra synchronous queries and ninety redundant uploads **per program set**, and
therefore that figure multiplied by the number of program sets the frame sweeps"* — or drop the
per-frame aggregation entirely and match the four sibling sites, which already say the true thing in
four different phrasings. The second is smaller and needs no new quantity; the first is more useful to
whoever eventually re-weighs the elision, because the comparison it has to make is per-frame on both
sides.

**Touches §5: no.** §5.2's GL-error row (l. 2837) states the consequence without a figure — *"costing
a re-upload of the whole set"* — and is true as written. The correction is confined to §4.7.4
ll. 2218–2221. §7, `[D-P1-32]`, §11.4 and §0.9 need no edit; §0.9 is in any case a superseded-record
section by the document's own convention, and it is correct there anyway.

---

### V10-2 — five sites delegate the foreign-GL containment to §6's 3→4 row, and that row still prices the unattributable case as `OUT_OF_MEMORY` alone; the `Resolutions` table records it as checked, on a line number that points at a different row · **correction** · **touches §5: no**

**Location.** `PHASE_1_DOC.md` §6's 3→4 row, **l. 2910**. The delegating sites: §4.7.4 ll. 2169–2174,
§4.7.4's precondition (ii) ll. 2247–2259, §5.2's GL-error row l. 2837, `[D-P1-30]` l. 3340,
`[D-P1-32]` l. 3342. The claim that it was checked: `PHASE_1_REVIEW_9.md`'s `## Resolutions`,
*"Checked and correctly left alone"*, the `§6's rung-2 row and 3→4 row (ll. 2907, 2906)` bullet.

**Claim under test.** That §6's two GL-error rows *"already carry `nothing mutating through the
facade`* and *already name a foreign error as a second cause of an unattributable sweep, from V8-1.
V9-5's widening does not reach them because they were never narrow. Verified rather than assumed."*

**Evidence.**

1. **It is true of the rung-2 row and false of the 3→4 row.** §6 l. 2907 (rung 2) carries both halves:
   *"nothing mutating **through the facade** since the last drain"*, and *"**If the replay reproduces
   nothing** — `OUT_OF_MEMORY` need not recur, **and the error may not have been ours at all**, since
   the elision bit tracks *facade* calls while the GL flag is per-context (§4.7.4) — the drain is real
   but unattributable and falls to the 3→4 row below."* §6 l. 2910 (3→4) carries neither: its whole
   statement of the case is *"**This row is also where rung 2 lands when its replay finds nothing**
   (an `OUT_OF_MEMORY` that does not recur, `[D-P1-32]`)"*. **One cause, named parenthetically, and it
   is the narrow one.**
2. **This is precisely the defect V9-5 was written to remove, surviving at the destination.** V9-5's
   finding was that precondition (ii) *"is still written as the `OUT_OF_MEMORY` corner"* at the sites
   where it lives, while other sites had already been widened. The fix-up's remedy was to enumerate
   two causes *"so a future session cannot narrow it back without visibly deleting a numbered cause"*
   (`Resolutions`, V9-5). It applied that remedy at §4.7.4 ll. 2247–2259 and `[D-P1-32]` l. 3342 —
   and left the single-cause gloss standing at the one place all of them point.
3. **Five sites point there, and four name the row explicitly.** §4.7.4 l. 2173: *"the sweep falls to
   §6's 3→4 'unattributable' row"*. Precondition (ii) l. 2255: *"The case falls to §6's `not
   attributable to one uniform or feature` row (3→4)"*. §5.2's (ii): *"falls to §6's 3→4 row"*.
   §11.4 l. 3484: *"§6's 3→4 row logs it and keeps the program running"*. `[D-P1-32]` l. 3342: *"falls
   to §6's 3→4 row"* **for either of two reasons**. Precondition (ii) itself names the delegating set
   — *"five sites delegate the foreign-GL containment to it"* (l. 2248) — so the document knows the
   delegation is load-bearing and knows how many arrows point at that row.
4. **The consequence is a reader-visible contradiction.** §6 is where §G2.4's ladder is mapped onto
   this subsystem, and §G2.4 requires every phase doc to carry that mapping. A Phase 6 session that
   reaches the 3→4 row by following any of the five pointers is told the landing is an
   `OUT_OF_MEMORY`-that-does-not-recur case, immediately after being told by the pointer that the case
   is *"load-bearing in general rather than an `OUT_OF_MEMORY` corner"*. The recurring-foreign case —
   the one V10-1 prices, the one §5.2 tells Phase 6 to *"plan for"*, the one §11.4 hands Phase 7 — has
   no home in the row that is supposed to receive it.
5. **The line number in the `Resolutions` entry is the tell.** `(ll. 2907, 2906)` — l. 2907 is the
   rung-2 row and is correct; **l. 2906 is the rung-3 compile/link/validate row**, not the 3→4 row,
   which is at **l. 2910**. The bullet's own citation points one row above rung 2 rather than three
   rows below it. Every other line number in the `Resolutions` table resolves exactly (§2 item 8);
   this is the single exception, and it sits on the single entry whose ruling does not hold.

**This is the "wrong left alone" the brief names.** The `Resolutions` list of things *"Checked and
correctly left alone"* exists, in its own words, *"so the next audit can tell a considered omission
from an oversight"*, and the brief's instruction is that *"a wrong 'left alone' is the same defect as
a missed site, wearing a justification."* This is that: the ruling is stated as verified, the
justification given is true of one of the two rows it covers, and the row it is false of is the
destination of five delegations.

**Fix shape** (the fix-up's call, not mine). Give §6's 3→4 row the same two enumerated causes
§4.7.4's precondition (ii) now carries — (a) `OUT_OF_MEMORY` need not recur, (b) the error may never
have been ours, since the elision bit tracks facade calls while the GL flag is per-context — so the
destination says what the five pointers say about it. One clause. The rung-2 row at l. 2907 needs
nothing; it was correctly left alone.

**Touches §5: no.** §5.2's property (ii) already carries the general form — *"`OUT_OF_MEMORY` need not
recur, and per the cadence note below the error may not have been ours at all"* — which is why V9-5
was recorded as a §5-clean correction in the first place. The fix is confined to §6 l. 2910.

---

### V10-3 — the widened absent-verbs header describes two kinds of entry; the column has carried three since before it was widened, and the face-culling row is the third · **note** · **touches §5: yes if fixed at §5.2 — but a §4.7.4-only fix is available**

**Location.** §4.7.4's absent-verbs table: the header ll. 2300–2306 and the **face-culling row
l. 2313**. The same header restated at §5.2's non-verbs row **l. 2846**, whose consumer column carries
two more entries of the same third kind.

**Claim under test.** That V9-7's widened header now covers the column's content. The header reads:

> *Verbs deliberately absent … each with the phase that would request it* … **Where a row's *Why
> absent* cell names an adjacent owner rather than a requester, that phase appears in the last column
> too, and the column is headed accordingly.** The instanced-draw row is the case…

and the column is retitled *"Who requests it — or owns the served work in its place"*. §5.2 l. 2846
restates it and adds *"the instanced-draw row is the only one today, because nobody requests an
instanced verb"*.

**Evidence.**

1. **The face-culling row's last column names a phase that is neither.** l. 2313's entry is
   **`3` (produces the map; `DESIGN.md`'s own worked example routes `backFace.*` to `7`)**. Is Phase 3
   *"the phase that would request it"*? No: the row's own *Why absent* cell says the flags *"are
   applied by `:mod` through `GlStateManager` rather than through the facade"*, §1.2 l. 737 assigns
   all GL policy to Phases 5 and 6, and Phase 3 is the pack front-end. Is Phase 3 *"an adjacent owner
   of the served work"*? No: the served work is applying `backFace.*`, and **`DESIGN.md` l. 1053–1055
   gives that to Phase 7** — the row says so itself. Phase 3 owns the **ownership map**, which
   `DESIGN.md` l. 777–782 makes a *required output* of Phase 3 and which decides who would ever
   request the verb. That is a third role: not the requester, not the owner of the served work, but
   the owner of the deliverable that assigns them.
2. **§5.2's own consumer column carries two more of the third kind.** l. 2846 names **`3` (the App
   F.1 flag-ownership map that settles face culling; and the `const`-scan that detects
   `countInstances` at all)**. Producing a map and running a `const`-scan are neither requests for an
   absent verb nor ownership of work that replaces one. The row that declares §5.2 *"sufficient on its
   own"* describes its column in two categories and populates it with three.
3. **This is V9-7's finding at a different row, not a new class of problem.** V9-7 was: a column
   headed *"the phase that would request it"* had come to carry a phase that requests nothing. The
   fix-up's answer was to widen the header rather than narrow the set, and it widened it by exactly
   one category — the one the instanced-draw row needed. The face-culling row already carried a phase
   that requests nothing, at the time the header was widened, and the widening did not reach it.
   `Resolutions` V9-7 records the header edit at ll. 2300–2306 and the instanced-draw row at l. 2316;
   no `Where` entry points at l. 2313. It is a neighbour the sweep did not reach — which is the
   mechanism §0.9's own lesson names (ll. 668–675), applied to itself.
4. **The document's facts are right; only the column assignment is off.** I re-derived both halves of
   the row's parenthetical at source: `DESIGN.md` l. 777–782 makes the engine-flag ownership map
   Phase 3's required output and routes `backFace.*` to Phase 7 in its worked example; l. 1053–1055
   confirms Phase 7 wires it. §G5.3 item 4 lists *"P3's engine-flag ownership map fully claimed by its
   owners"* among the integration review's checks. The row is accurate. This is a taxonomy defect, not
   a factual one, which is why it is a note.

**Why it is a note and not a correction.** Nothing a dependent builds changes, and no reader is
misled about who does what — the face-culling row's prose says plainly that Phase 3 produces the map
and Phase 7 is where `DESIGN.md` routes the flags. What is imprecise is a header that claims to
enumerate the column's kinds and enumerates two of three, in a row §5.2 declares sufficient on its
own.

**Fix shape** (the fix-up's call, not mine), **and the branch matters for the cadence.** Two are
available and they have different §5 consequences:

- **(a) §4.7.4 only.** Widen the header at ll. 2300–2306 once more, to cover a row whose last column
  names the phase whose deliverable decides the assignment. §5.2's *"the instanced-draw row is the
  only one today"* survives untouched — it is scoped to rows naming *an adjacent owner of the served
  work*, and the face-culling row is not one of those. **This leaves §5 unchanged.** It does not reach
  §5.2's own two third-kind consumer entries, which is the cost of the cheaper branch and should be
  recorded if it is taken.
- **(b) §4.7.4 and §5.2.** The same header edit, plus §5.2 l. 2846's restatement. Complete, and
  **alters §5**, which under §G1.3 owes an eleventh verify session.

**Touches §5: yes if fixed at §5.2; no under branch (a).** This is the only finding in this round with
a §5 disposition that depends on the fix shape, and it is therefore the only one that can extend the
cadence. §3's `§G1.3 line` states the consequence of each branch explicitly, so the choice is made
with its cost visible rather than discovered afterwards.

---

### V10-4 — §4.2.3 quotes §12 item 4b in a form item 4b no longer has, and calls it the unchanged one of the two texts, in the same fix-up that changed it · **note** · **touches §5: no**

**Location.** §4.2.3 **ll. 1082–1085**; the text it quotes and characterizes, §12 item 4b **l. 3609**.

**Claim under test.** That

> "§12 item 4b already says exactly this, flat (*"does not export the class to the applying build
> script, so `new SeamClasspathArguments(...)` will not resolve"*), and it is the more accurate of the
> two texts; this paragraph is corrected to it rather than the reverse."

is true of item 4b as the document now stands. §0.9 ll. 627–629 makes the same claim in the addendum —
*"The §4.2.3 text is corrected **to** §12 item 4b's flat form, not the reverse — item 4b was the more
accurate of the two all along, which is itself worth recording."*

**Evidence.**

1. **The quotation is not verbatim.** §12 item 4b l. 3609 reads: *"…does not export the class to the
   applying build script **at the time that script is compiled**, so `new SeamClasspathArguments(...)`
   will not resolve **and no `ext` indirection preserves that call site**…"* The quoted span drops the
   five bolded words **from the middle** of the sentence, with no ellipsis, and stops before the
   `ext` clause. Dropping words from inside a quotation without marking the elision is the defect
   round nine raised as V9-9 against §0.8's App D quotation, and which the same fix-up repaired there.
   Here it is load-bearing rather than incidental: the dropped words are **the compilation-order
   qualifier**, which is precisely the thing §4.2.3's surrounding paragraph is arguing was missing
   from its own text and present in item 4b's.
2. **"Flat" and "corrected to it rather than the reverse" describe a text that was changed in the same
   edit.** `PHASE_1_REVIEW_9.md`'s `Resolutions`, V9-3, `Where` column records **§12 item 4b l. 3609**
   as an edited site, gaining *"compilation-order reason, `no ext indirection preserves that call
   site`, default-package requirement, `[U]` pointer, and the hook named as the experiment"*. So both
   texts were rewritten toward the same new content. The claim that §4.2.3 was corrected **to** item
   4b, and that item 4b *"was the more accurate of the two all along"*, is true of item 4b's **prior**
   form and is asserted about a form the reader can no longer find at the cited location.
3. **It is checkable in one step and fails.** A reader following §4.2.3's pointer to item 4b to see
   the "flat" version finds a version carrying the compilation-order reason and the `ext` clause —
   i.e. finds the two texts saying the same thing, which is fine, and finds the sentence that sent
   them there describing a difference that no longer exists.

**Why it is a note.** No design claim depends on it. Both texts are now correct and consistent, the
`[U]` tag and §11.3 item 10 dispose of the provenance question properly, and §12 item 4b's hook is
the right experiment. What is wrong is a quotation and a characterization of the document's own
history — the same class as V9-9, and disposed of the same way.

**Fix shape** (the fix-up's call, not mine). Either quote item 4b as it now reads (restore *"at the
time that script is compiled"*, or mark the elision), or drop the quotation and keep the substantive
half — *"§12 item 4b states the same conclusion and now carries the same reason; the two texts agree"*
— which is true, needs no quotation, and does not turn on which text was corrected toward which.

**Touches §5: no.** §4.2.3 and §12 item 4b only. §5 says nothing about the Gradle mechanism.

---

## 2. What was checked and came back clean

Named, because a round reporting only findings misrepresents its own coverage — and because the brief
is explicit that **an item that holds on derivation is itself the finding.** Each of the brief's ten
"where the fix-up is weakest" items is dispositioned here or in §1; the numbering follows the brief's.

**1. The V9-1 branch ruling holds, and this session was owed.** This is the one that decides whether
round ten exists, so it is derived from §G1.3's words rather than from the fix-up's citations.

- §G1.3's trigger is textual and it is about the **section**: *"If corrections altered the doc's
  Cross-phase interfaces section, the doc goes through a fresh verify session."* Branch (b) as round
  nine framed it — *"deleting the clause from §5.2 and keeping the substance in §4.7.4 and §11.4"* —
  **edits §5.2 by construction**, so it alters the section on the rule's own words. Round nine's gloss
  that it might *"possibly leave §5 unchanged"* is not a second branch; it is a purposive reading of
  "altered" as "altered materially for a dependent", and §G1.3 does not say that.
- **§G1.3's third bullet is a second, independent anchor** and nobody has cited it: a phase is
  verified at *"PASS-WITH-CORRECTIONS with all resolutions recorded and **no §5 change outstanding**"*
  — again a change to §5, not a change to what §5 obliges.
- **The purposive reading, taken seriously, retro-invalidates three sessions.** §0.8 ll. 509–511
  records that of round eight's three §5-touching corrections *"only V8-2 changed what a dependent
  phase does"*, and §0.9 ll. 697–700 says **no** correction of round nine's changed what a dependent
  builds. If materiality were the trigger, sessions eight, nine and ten were all unowed and the phase
  should have closed after the fourth fix-up. It did not, twice, on the reading the sixth fix-up
  applied a third time. Consistency is on the fix-up's side.
- **And §G5.3 item 1 supplies the reason the textual rule is the right one:** verification urgency is
  proportional to fan-out, and P1 *"feeds everything"*. A materiality trigger would require the
  fix-up to certify that its own edit to the contract section does not matter to a dependent — the
  self-assessment the build→verify cadence exists to remove. The textual trigger takes that judgement
  away from the party least able to make it neutrally.
- **The circularity in the fix-up's own argument is noted and does not change the answer.** §0.9
  ll. 551–555 rests the ruling on §0.7 l. 318 and §0.8 l. 494 — *this document's* practice — which
  would be circular if the practice were itself the error. Derived from §G1.3 and §G5.3 alone, the
  ruling stands anyway. **Branch (b) was genuinely foreclosed; round ten was owed; the fix-up was
  right to overrule both round nine's §3 and its own commissioning brief on it.**

**2. The recurring-foreign arithmetic — one site of five is wrong: see V10-1.** The other four
reconcile with each other and with `RESEARCH.md` §4.2's *"refreshes ~90 built-in uniforms"* on all
three of the brief's sub-questions: a program's uniform *set* **is** the ~90 (`RESEARCH.md` l. 504
attaches the figure to the use-program barrier, which is what a program set is); the unit is
**per-sweep**, not per-frame; and a single foreign error lands in **one** sweep's window, which is why
the document is careful to say *"a **recurring** foreign error"* wherever it prices the repeated case.
The premise work is right; only §4.7.4's aggregation of it is wrong.

**3. The removed "43" — the ledger is still legible and the removal was correct.** The brief's
concern is that the comparison now has a number on neither side. It does not: **§11.4 l. 3493 states
both sides** — *"roughly one extra query per frame against a factor of two at every program switch
(§7)"* — and §7 l. 2959 supplies the magnitude the deferred side needs, *"there are 43 **slots**"*,
carefully phrased so it bounds the switch count without asserting it. An implementation session can
act on that: one query per frame against twice-per-switch, with switches per frame bounded above by a
43-slot registry of which a real pack binds a fraction. The two corrected live sites (§4.7.4 l. 2177,
§11.4 l. 3494) now read *"at every program switch in the frame"*, which is true and is the quantity
that matters. And §0.9's argument that the old error's direction favoured the defended conclusion
**checks out**: 43 as a per-frame event count overstated the cost of *dropping* the elision, i.e.
favoured keeping it. Trading a wrong number for a correct qualitative statement strengthened the
argument rather than weakening it. Superseded copies at §0.7 l. 252 and §0.8 ll. 367, 377 are left
standing under the pointed-at-not-rewritten convention, with §0.8's V8-1 bullet carrying the pointer
(ll. 385–387); §0.7 l. 252 carries no arithmetic-specific pointer, which §0.9 l. 643 discloses, and
§0.7's bullet already carries a round-eight pointer and its subsection a closing supersession
sentence. Considered and not raised.

**4. V9-7's header widening — the row it was meant to fix is sound; a different row is not (V10-3).**
Three sub-questions, three answers. *Is a seven-phase column still doing a consumer column's job?*
**Yes.** §5.2 l. 2846 marks each phase with what it owns and marks Phase 5 explicitly as *"**not** a
requester of an absent verb"*, so the breadth is annotated rather than ambiguous, and §5.2's stated
purpose is to be sufficient on its own — a Phase 6 session reading only §5 does need to learn that the
`instanceId` upload is its entry point. *Is the face-culling row the same shape?* Not quite, and the
difference is the finding — see V10-3. *Does readmitting Phase 5 contradict §0.8's record?* **No, and
the device is right.** §0.8 ll. 499–503 carries a bold pointer that distinguishes the two halves
exactly — *"So the removal recorded in this sentence is superseded; the addition is not."* The
round-eight sentence is left intact above it. That is the §0.4–§0.7 convention applied correctly.

**5. V9-4's narrowing is right, and §3 is not left unmapped.** §G9 requires *"ZERO unmapped rows"*
from the conformance map, and the criterion is *contract item → design element satisfying it*. The
`scale.<prog>` row (l. 895) **maps**: the design element is `StateService.viewport(x, y, w, h)`, and
§4.7.4's inclusion criterion names the sub-viewport as the verb's reason for existing. What the row
declines to name is who computes the rectangle — an *ownership* question the map's criterion does not
ask. Not an unmapped row. On §G1.1: its rule is to report **contradictions** and not smooth them over;
the row does more than that by reporting a silence with its three sourced inputs, which is the honest
maximum available. On §11.5: declining is correct and the precedent cited against it does not
transfer. §11.5 item 4 (the missing §G2.4 rung) is a gap in a ladder **§G2.4 requires every phase doc
to map**, so it obstructs a mandated deliverable and three named phases will each meet it; the
`scale.<prog>` silence is an inter-phase boundary, and **§G5.3 item 4 names the integration review as
the instrument for exactly that** — *"shared-ownership seams"*, with the P5/P6 unit-map split given as
the worked example. The document routes it to the named instrument.

**6. V9-3's `[U]` is in the right place and the right size, and its claims check out against the
template.** *Placement:* §10 is scoped by §G4.4 to *assigned* OQs, and OQ numbering belongs to
`RESEARCH.md` §11 which §G1.1 forbids this document to amend — so a doc-local unverified claim has no
home there. §11.3's heading covers three categories and this is a fourth, which is why item 10 was
given **its own bolded subheading** naming the category (l. 3414). Disclosed rather than smuggled;
that is the correct handling of a template that does not anticipate the case. *Size:* a `[U]`
supporting a v0.1 checklist item is exactly what §12 item 4b's hook settles — *"`./gradlew
:engine:test --dry-run` configures without an unresolved-class error"* — and its failure mode is a
loud configuration error, so a spike spec would be ceremony over a one-command experiment.
*Substance, checked at source:* `buildSrc` genuinely does not exist in the template (verified —
`build.gradle`, `settings.gradle`, `gradle/`, `src/**` are the whole build); the template's three
scripts genuinely are `apply from:` scripts at `build.gradle` ll. 100, 238, 239 (verified verbatim);
all three printed blocks genuinely print `new SeamClasspathArguments(...)` with **no `import`**
(ll. 1053, 1182, 1237 — verified byte-identical three-argument calls, which is what makes the
default-package requirement real); and the `ext`-route analysis is internally sound — exporting a
`Class` needs `newInstance`, exporting a factory needs no `new`, and Groovy has no `new (expr)(...)`
form, so no route preserves the literal call site. The one judgement I would not have made the same
way is calling compilation order *"decisive"* and class-scope isolation *"the weaker half"*: both are
facts about the same unavailability, and neither strictly dominates. It is `[U]`, it is scheduled, and
splitting that hair is not worth a finding. **Separately, the paragraph's account of its own
relationship to §12 item 4b does not hold — V10-4.**

**7. The eleven lines of new javadoc do not drift, and the record is still the contract.** Checked
against every site the brief names. `drainErrors()` ll. 1917–1929 vs §5.2's GL-error row: both say the
drain elides when no mutating **facade** call has occurred, both say an empty return does not mean the
per-context flag is clear, both say a non-empty return may carry a foreign error. `GLError`
ll. 2061–2078 vs §6's rung-2 row (l. 2907), `[D-P1-32]` (l. 3342) and §2.4's key-type table (l. 853):
all four state window-scoped attribution, the one-call/many-call split, and the per-context caveat, in
the same terms. No drift found. On the sharper question — *is a record whose javadoc says it may name
the wrong call still the contract §12 item 19 verifies and §G2.4 rung 2 acts on?* **Yes, and the
javadoc is what makes it so.** §12 item 19's hook verifies the *declaration* (no GL constant, no int
object name, no LWJGL buffer type); item 22's hook verifies the *behaviour* (*"the batched record is
emitted once per window and never claims per-call attribution"*), which is the corrected claim, not
the old one. And rung 2 does not act on a single record: `[D-P1-32]`'s replay is what attributes, and
the javadoc says exactly that — *"which is why `[D-P1-32]`'s replay — not this record — is what
attribution rests on"*. A record that admits its own limit and points at the mechanism that resolves
it is a better contract than one that overclaims, which is the whole content of V9-2.

**8. §0.9's counts reconcile exactly, and every one of the 29 sites is edited.** This is the audit the
brief asks for, and it passes arithmetically as well as site by site. The `Resolutions` `Where` column
contains **31 distinct sites**. Two of them are neighbours the review named no finding for and which
the table itself marks as such — `drainErrors()`'s javadoc ll. 1917–1922 (*"neighbour — no finding
pointed at it"*, counted under V9-2) and §11.3 item 10 ll. 3414–3433 (*new*, counted under V9-3).
31 − 2 = **29 sites named by round nine**. Splitting by severity: the six corrections (V9-1…V9-6)
account for 21 distinct sites, minus those same two neighbours = **19**; the five notes (V9-7…V9-11)
add **10** further distinct sites, the overlaps being l. 2316, l. 2846, l. 3345 and l. 2837, each
already counted under a correction. **19 + 10 = 29, exactly as §0.9 l. 532 claims.** I then confirmed
each of the 29 is really edited, at the line: V9-1's eight (2160–2190 · 2214–2223 · 2837 · 2961–2967 ·
3478–3508 · 3342 · 3340 · 379–387), V9-2's six, V9-3's four, V9-4's two, V9-5's two, V9-6's three
markers, V9-7's three, V9-8's five, V9-9's one, V9-10's one, V9-11's four. **No site is named and
unedited, and no count fails to reconcile.** I also cross-checked the two "applied wider than named"
claims against round nine's own `Location` lines: V9-1 named seven sites and was applied at eight
(`[D-P1-30]` added), V9-2 named five and was applied at six (`drainErrors()` added). Both true.

**9. The bold-pointer convention is doing one job, not two.** The brief asks whether §0.8's new
pointer at ll. 407–415 — which says a supporting argument *"does **not** stand"* — is history being
edited under a device meant to preserve it. **It is not.** Round eight's V8-2 bullet is left intact
above it, including the sentence now withdrawn (*"a distinction this document already made correctly
one row later"*, ll. 394–395); the pointer is appended, marked bold like every other, states what was
withdrawn and why, and — the part that makes it preservation rather than revision — **states what
survives**: *"The retarget itself stands, and on stronger ground than either argument here."* Compare
the §0.4–§0.7 pointers, which say a later round corrected a conclusion. Withdrawing support while
naming the conclusion's replacement support is the same act at a different depth, and the record of
what round eight actually argued is fully recoverable from the page. If the convention were being
abused, the withdrawn clause would be gone; it is not.

**10. "No signature changed" verifies on four independent lines, under a stated reconstruction
limit.** I cannot check it byte-for-byte and neither could rounds seven, eight or nine: `git HEAD` is
`79543cf`, the 2159-line build-session draft, and all six fix-ups are uncommitted working tree
(confirmed by read-only `git log`/`git status`). Per the brief this is a workflow matter, not a
document defect, it is correctly absent from §11.5, and I do not raise it. What I can do is
reconstruct, and four independent internal records agree: **(i)** §5.2's per-revision changelog row
(l. 2836) enumerates every signature change by revision and records *"nothing in any signature"* for
the §0.7, §0.8 and §0.9 revisions; **(ii)** §12 items 18, 19 and 20 enumerate the expected types —
four sealed handle sub-interfaces, seven service interfaces, `GLDevice`, the named result and value
types, `DrawService` declaring *"`fullscreenQuad()` only"* — and every one matches the block at
ll. 1905–2079; **(iii)** §2.4's key-type table (ll. 848–861) matches the same set, including *"**Four**
handle types"*; **(iv)** §9's staging table (ll. 3084–3088) lists the same interfaces at the same
tags. The two round-nine edits inside ll. 1905–2079 are, as claimed, **inside comment blocks** —
`drainErrors()`'s javadoc ll. 1917–1928 and `GLError`'s ll. 2061–2077 — and neither touches a
declaration line. **The limit of this reconstruction, stated:** it proves the block is internally
consistent with four cross-references maintained by the same sessions, not that no declaration changed
between two uncommitted states. A commit per fix-up would replace all of the above with one `git
diff`. The tenth session pays the same cost the ninth did.

**The `Resolutions` table's four independent claims, tested.**

**11. The line numbers resolve — every one but one.** All 31 `Where` entries were re-resolved against
the 3709-line file. They land, including the awkward ones: §0.9 at ll. 515–702, §4.2.3's `[U]`
paragraph at ll. 1097–1107, §11.3 item 10 at ll. 3414–3433, the three call blocks at ll. 1053, 1182,
1237, §0.8's four appended pointers at ll. 379–387, 407–415, 420–423, 452–457, and the closing
paragraph at ll. 3687–3709. The single exception is in the *"left alone"* list, not the `Where`
column, and it is part of V10-2: `(ll. 2907, 2906)` cites the rung-3 row where it means the 3→4 row at
l. 2910. **They were re-resolved rather than estimated**, as the table claims.

**12. The one refusal is discipline, not evasion — conditionally on V10-1 being fixed.** The fix-up
applied V9-1's cost limb as a **record** and declined to re-weigh the elision, on the ground that
making that judgement inside a fix-up would be *"a design call arriving through a correction"* — which
is round eight's rule (§0.8 ll. 344–345) applied to itself, and no adversarial session would have
reviewed the result. I agree, and I would go further: the honest form of an incomplete ledger is to
say it is incomplete and name the missing term, which is what §0.9 l. 576 and `[D-P1-32]` l. 3342 both
do. The decision is now **flagged as undefended** rather than quietly defended — *"A future session
re-opening it will find the cost written down and the decision undefended by this one"* — and that is
a stronger position than a re-weighing produced by the wrong session type. **The condition is that the
record has to be right**, and at one of its three sites it is not: V10-1. Fix that and the refusal is
clean. Leave it and the refusal preserves a comparison a future session cannot make.

**13. "Neighbours swept" — six sites, all real neighbours, none scope creep.** `drainErrors()`'s
javadoc is inside the same signature block as the `GLError` javadoc V9-2 required, and carries the
same class of defect. §11.3 item 10 is required by `RESEARCH.md` §0.2 the moment V9-3's fix introduces
a `[U]` — not optional. §0 header l. 10 and the dates note ll. 13–17 were stale by one round the
moment §0.9 was added (*"§0.4–§0.7"* → *"§0.4–§0.9"*). §5.2's opening changelog row l. 2836 is the
per-revision record of the two rows the same fix-up edited, and its relabelling of §0.8's entry from
*"this revision"* is a consequence of adding a new one. The closing paragraph counted eight sessions
and five fix-ups. §0.9 itself is §G1.3's own deliverable shape as this document has practised it since
§0.4. **Every one is downstream of an edit that was made, not adjacent work that looked worth doing.**

**14. "Checked and correctly left alone" — every item verified, one ruling wrong.** Verified at the
line and correct: §0.7 ll. 254 and 257 (superseded round-seven rationale, and the bullet already
carries V8-1's pointer at ll. 258–263); §0.8 l. 485 (a deliberate quotation of the old formulation, as
the lesson it draws); §4.7.4 l. 2116 (*"every mutating verb returns `void`"* — the return type, a
different sense); §4.7.5 l. 2376 (the recorder, where every call is a facade call by construction);
§9's staging note l. 3086 (already *"through the facade"*); §12 item 22 l. 3638 (carries the facade
qualifier twice and the *"a drain after a drain issues no query"* hook intact); `[D-P1-33]` l. 3343
and `DrawService`'s javadoc ll. 2041–2050 (both describe the composite loop and name no owner and no
milestone, so neither the retarget nor the `[v0.5]` tag reaches them — confirmed by reading both);
`[D-P1-34]` l. 3344 (the `locate` name obligation only); §1.2's table (no `countInstances` row —
confirmed); the `[A]` tag on §3's second row l. 892 (untouched, and correctly so: the gbuffers case is
open, so it takes no milestone). **The one that does not hold is §6's pair — V10-2.**

**15. §11.5's routings are both correct.** Unchanged at four items, verified (two to `RESEARCH.md`,
two to `DESIGN.md`, ll. 3560–3589). V9-4's silence: declined correctly — see item 5. The
commit-per-fix-up observation: declined correctly, and for the reason given — §11.5 is *"Requested
upstream changes"* to `RESEARCH.md` and `DESIGN.md`, and a request to commit the working tree is
addressed to neither. It is recorded in §0.9 ll. 675–682 at the project owner's direction, which is
the right home for a workflow note.

**Also checked, and clean.** The `mutating` sweep: eleven live sites now carry the facade qualifier or
a disclosed different sense, and the grep turns up no unqualified survivor in live prose. The
`sound remedy` sweep: the phrase survives only at §0.7/§0.8 (superseded, pointed at) and in §5.2's and
the closing paragraph's *descriptions* of the correction. The `43`/`forty-three` sweep: no live site
reuses the cardinality as an event count. The five-sites/four-other-sites count in precondition (ii)
l. 2248 and `[D-P1-32]` l. 3342 reconciles exactly (five delegating sites; four *other* than
`[D-P1-32]` itself), and all five really do delegate — verified individually. `[D-P1-30]`'s and
`[D-P1-32]`'s `[fix-up: …]` markers now carry rounds eight and nine and `[D-P1-35]` carries V9-8, as
V9-6 required. `[D-P1-30]` l. 3340's cross-reference for the frame-level remedy correctly says
**§11.4**, not §11.5. §5.2's property (i) rescoping (V9-10) says *"asserts no property of what a
second evaluation would do"* and the two relayed facts are stated as facts about the sources, which is
the exact scope V9-10 asked for and no wider. §0.7 and §0.8's `§G1.3 status` paragraphs are both
past-tensed and both restamped *"at the time"*, and §0.8 gained the closing supersession sentence the
other four subsections carry. The seventeen §4.1 template rows, the four seam constraints C-1…C-4, the
pin table's structure and its three-ruling re-pin procedure, the four OQ spike specs, §4.2.5's
packaging analysis, §4.11's CI ordering, the channel list's fourteen-phase coverage, and §11.2's
D-1..D-10 disposition were read and are unchanged and undamaged by this fix-up. **V8-7 stays
closed** — a fourth round declines to re-open it. The GL quotation and both `docs.gl` URLs were not
re-verified, deliberately, and no finding turns on them (§0.3).

---

## 3. Verdict

**PASS-WITH-CORRECTIONS.** Two corrections, two notes, **zero blocking**.

FAIL is not close and was never in view. The module split, the seam and its four constraints, the
facade's shape and every signature in it, the conformance map, the pin table, the mixin wiring, the
licensing work and the build plan are sound and were sound three rounds ago. Both corrections are a
clause each. Neither changes what any dependent phase builds: V10-1 corrects a **cost figure** in a
ledger the document explicitly declines to act on, and V10-2 adds a **second named cause** to a
degradation row that five sites already describe in the widened form. Phase 6's rung-2 protocol,
Phase 7's composite loop and `instanceId` split, and every facade verb are untouched by anything here.

**The honest shape of this round.** §0.7's lesson held for a fourth time: every finding is in the
sixth fix-up's new prose or in something that prose asserts about the rest of the document, and
nothing was found in material three rounds have already cleared. Round nine's own instruction to this
session — *"audit the **unnamed** neighbours"* — is where both corrections came from. V10-2 is the
sharper of the two and it is round nine's mechanism applied to round nine's fix-up: a true claim
(*"§6's rows were never narrow"*) suppressed the sweep of the one row it was false about, and the
wrong line number in its own justification is the fingerprint. V10-1 is the other recurring shape —
a correction written to repair a mispricing, carrying a mispricing.

**What is genuinely better than the last three rounds, and worth recording.** The sixth fix-up's
`Where` column is the first that is auditable **arithmetically** as well as site by site: 19 + 10 = 29
reconciles against the table, the two embedded neighbours are marked as neighbours, and every line
number resolves. The sweep-over-formulations discipline it adopted is visible in the result — the
`mutating`, `sound remedy` and `43` greps come back clean in live prose, which is what defeated the
class of defect that produced V9-2. Four of the brief's ten test items (1, 3, 5, 9) are arguments that
**hold on derivation**, and per the brief that is the finding: the V9-1 branch ruling is right and
this session was owed; the "43" removal strengthened its argument rather than weakening it; V9-4's
narrowing and its §11.5 decline are both correct; and the bold-pointer convention is preserving
history, not editing it.

### Per-finding §5 disposition

| Finding | Severity | Touches §5? |
|---|---|---|
| **V10-1** §4.7.4's recurring-foreign per-frame ceiling drops its own multiplier | correction | **no** — §5.2's GL-error row states the consequence without a figure and is true as written; the fix is §4.7.4 ll. 2218–2221 alone |
| **V10-2** §6's 3→4 row still prices the unattributable case as `OUT_OF_MEMORY` alone, while five sites delegate to it | correction | **no** — §5.2's property (ii) already carries the general form; the fix is §6 l. 2910 alone |
| **V10-3** the absent-verbs column carries a third kind of entry the widened header does not describe | note | **only under fix branch (b).** Branch (a) — §4.7.4's header alone — leaves §5 unchanged and is available; branch (b) also edits §5.2 l. 2846 and alters §5 |
| **V10-4** §4.2.3 misquotes and mischaracterizes §12 item 4b | note | **no** — §4.2.3 and §12 item 4b only |

### §G1.3 line

**§5 is NOT changed by any correction in this round.** Both corrections are confined to §4.7.4 and
§6, and I have stated for each why §5.2 is already correct at the corresponding row rather than
leaving that to be inferred. This is the first round in four at which no correction touches the
Cross-phase interfaces section.

**What that means for the cadence, stated plainly because it is the point of this line.** Under
§G1.3, a phase is verified when its latest verdict is PASS, **or PASS-WITH-CORRECTIONS with all
resolutions recorded and no §5 change outstanding.** So:

- **If the seventh fix-up applies V10-1 and V10-2, applies or declines V10-4, and either declines
  V10-3 or fixes it under branch (a) at §4.7.4 alone** — then §5 is unchanged, §G1.3's *"re-verify
  only if §5 changed"* trigger **does not fire**, the fix-up **closes the phase**, and
  `PHASE_1_DOC.md` becomes a valid dependency input (§G5.3). **Phase 2 and Phase 3 unblock, and the
  ten-round verify cadence ends.** That outcome is available on the evidence in this review, and I
  say so without hedging: nothing I found requires an eleventh session.
- **If it elects branch (b) for V10-3** — editing §5.2 l. 2846 as well — then §5 is altered, the
  trigger fires on the same textual reading §2 item 1 upholds, and an **eleventh verify session** is
  owed before any dependent consumes the document. That is a real cost for a note, and it is the
  fix-up's call. I have written V10-3's fix shape so the cheaper branch is a genuine option rather
  than a fudge, and so that if branch (a) is taken, what it leaves unfixed (§5.2's own two
  third-kind consumer entries) is recorded as left rather than overlooked.

**Until the seventh fix-up records its resolutions, `PHASE_1_DOC.md` is not verified** — the verdict
is PASS-WITH-CORRECTIONS with resolutions outstanding, so §G1.3's verified state is not yet reached
and Phase 2, Phase 3 and everything downstream stay blocked (§G5.3).

**What the seventh fix-up inherits, and it is the narrowest inheritance of any round so far.** No
finding is left unapplied and none is refused, so there is no re-derivation debt. No correction
changes what a dependent phase builds. Both corrections are single-clause edits at sites named to the
line, and both have a stated fix shape that needs no new source. The one thing it should carry
forward is the instruction round nine gave this session and this session would give again: **the
sweep is the risk, and the sites that get missed are the ones a true claim elsewhere makes nobody
look at.** V10-2's row was missed because the sentence covering it was true of its neighbour. V10-3's
row was missed because the header was widened to fit the row the finding named. Both are one row away
from a site that was edited correctly.

*Per §G1.2 this session stops here. It wrote no code, ran no build and no test, launched no review
agent, applied no fix, and created exactly one file: this one. Two network requests were made, both
for §4.2.6's pin procedure, both disclosed in §0.3. `PHASE_1_DOC.md`, `RESEARCH.md`, `DESIGN.md` and
all nine prior review files — including `PHASE_1_REVIEW_9.md`'s `## Resolutions` section, which is
this round's primary evidence — are unmodified.*
