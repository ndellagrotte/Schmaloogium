You are a fresh Claude Code session running the sixth fix-up session (DESIGN.md §G1.3) on
Schmaloogium/PHASE_1_DOC.md, applying `PHASE_1_REVIEW_9.md`. Working directory:
/home/nick/IdeaProjects/schmaloogium-project/Schmaloogium/.

## Why this session exists

The cadence: build session → nine verify sessions (`PHASE_1_REVIEW.md` … `_9.md`, every one
PASS-WITH-CORRECTIONS) → five fix-ups. The ninth returned **six corrections, five notes, zero
blocking** (V9-1 … V9-11). One correction — V9-1 — touches §5.2, so on the fix shape round nine
expected, §G1.3's *"re-verify only if §5 changed"* rule fires again and a tenth verify session is
owed. Until a verdict exists that clears §5, `PHASE_1_DOC.md` is **not a valid dependency input**
(§G5.3) and Phase 2, Phase 3 and everything downstream stay blocked.

Your contract is §G1.3's: read the phase spec, the phase doc and the review file; apply the
corrections; record each resolution in `PHASE_1_REVIEW_9.md` under a `## Resolutions` heading. Then
stop. **Fix nothing that round nine ruled clean, and verify nothing beyond ordinary care** — that is
the tenth session's job.

## The decision that matters most, and it is genuinely yours

**V9-1 decides whether Phase 1 closes.** The finding is that four sites call one unconditional
frame-boundary `drainErrors()` "the only/one sound remedy" when §4.7.4 ll. 1913–1914 concedes in the same
paragraph that dropping the elision *"would bound the window against all GL"* and rejects it on
**cost**, not soundness — and that the remedy so crowned does not address the between-sweeps case it
is attached to, cannot be made unconditional through the verb §5.2 exposes, and is priced against a
ledger that omits the replay cost the elision creates.

Two branches, and they lead to different cadences:

- **(a) Reword §5.2's clause** (l. 2543: *"§11.4 names Phase 7 as the owner of the only sound remedy
  (an unconditional drain at a frame-driver point)"*). §5 changes → tenth verify session.
- **(b) Delete the clause from §5.2**, keeping the corrected substance in §4.7.4 and §11.4. §5 may
  then be unchanged → §G1.3 closes the phase and Phase 2/3 unblock.

Round nine left this open on purpose — its §3 says so in the last paragraph of the §G1.3 line. **Do
not pick (b) because it closes the phase.** §5.2 declares itself *"sufficient on its own — every
obligation this document places on another phase appears here"* (ll. 2524–2526), and its GL-error row
is consumed by Phases 6, 4, 5 and 14. If deletion genuinely leaves §5 sufficient for a Phase 6 or
Phase 7 session reading nothing else, (b) is the honest answer and you should take it. If deletion
makes §5 silent about something a dependent needs, it is gate-gaming, the tenth session will say so,
and you will have spent a round buying nothing. Argue whichever you pick, in §0.9, as an argument
rather than an outcome.

**The same applies to the three notes that touch §5 if adopted** — V9-7 (the non-verbs row's consumer
semantics), V9-8 (the missing `[v0.5]` tag), V9-10 (property (i)'s self-description). Adopting any one
flips the §G1.3 answer on its own. Decide each on its merits and *then* state the cadence
consequence. Never the other way round.

## Read, in this order

1. **DESIGN.md §G1.3 first** (ll. 151–162) — your contract, including what "verified" means and when
   re-verification fires. Then **Part I in full** (§G0–§G10) and the **Phase 1 spec** (ll. 585–658).
2. **PHASE_1_DOC.md, whole.** You are editing eight sections of it; read all thirteen.
3. **PHASE_1_REVIEW_9.md, whole** — the findings, **§2's clean list** (which is what stops you
   re-fighting settled ground), and §3's verdict and disposition table.
4. **Then, per finding, the source it cites — and only that.** The reads each finding turns on are
   named in it.

Reading rounds one to eight is permitted where a finding turns on their history — V9-6 turns on the
`[fix-up:]` marker convention that §0.6's V6-1 (PHASE_1_DOC.md ll. 162–165) established — but do not
re-import their findings. Record what you read beyond this list, and why, in §0.9.

## Re-derive; do not adopt

Round eight's meta-finding, now confirmed twice: *a review's supporting argument is not evidence, and
a fix-up that promotes it into §5 has changed its status without changing its support.* Round nine's
own finding is one level down: *a fix-up that re-derives every finding can still under-scope its own
sweep, and the sites it misses are the ones its correctness elsewhere hides.* Both are aimed at you.

Re-derive every load-bearing claim at source before you write it into the document. Specifically:

- **V9-1** — DESIGN.md §G3.2 ll. 278–291 and RESEARCH.md §4.4 ll. 528–566, for where foreign GL sits
  relative to Phase 6's sweeps; RESEARCH.md l. 504 for "~90 built-in uniforms"; RESEARCH.md l. 493 and
  App A.1 l. 1142 for what "43" actually counts.
- **V9-3** — your own reading of Gradle's script-compilation model (there is no source for it in the
  document, which is half the finding), plus `build.gradle` ll. 100, 238, 239 `[V:template]`.
- **V9-4** — DESIGN.md Phase 3 l. 769, Phase 4 ll. 855–858, Phase 5 ll. 895–929, Phase 7 l. 1047.
- **V9-2 and V9-5** — the listed sites, read in place rather than trusted from the table.

## The sweep is the risk, and V9-2 is what happens when it is short

Round eight named six sites for V8-1. The fifth fix-up edited nine, claimed *"every site now says
'mutating **facade** call'"*, and five sites still do not. Its own lesson was right — *"A review names
the sites its finding turns on; a fix-up owes the sites its edit turns on, and that is always the
larger set"* — and its sweep was still short. The sites that get missed are the ones no finding could
point at:

- **javadoc and comments inside the signature blocks (ll. 1654–1818)** — missed last time *because*
  "no signature changed" is true, so no `Where` entry ever pointed inside them. The `GLError` javadoc
  (ll. 1805–1806) is V9-2's worst site for exactly this reason.
- **§2.4's key-type table** (l. 630), which restates facade semantics one line per type.
- **the second half of a bullet whose first half was corrected** (V9-5 is this: §5.2's precondition
  (ii) was widened, §4.7.4's and `[D-P1-32]`'s were not).
- **§12's checklist items and their review hooks**, and **§9's staging notes**.

Before you write your `Where` column, grep the document for every formulation you changed and confirm
no variant survives. Then state how many sites you edited against how many round nine named — round
nine's table will be audited site by site the way it audited round eight's, and **a site named but not
edited is a finding.**

**And do not repeat V9-11 while fixing it.** When you add §0.9, restamp §0.8's `**§G1.3 status:**` to
`**§G1.3 status at the time:**` / *"that fix-up"*, as §0.4 (l. 113), §0.5 (l. 151) and §0.6 (l. 214)
do and §0.7 (l. 317) failed to.

## Where round nine is weakest, stated by round nine

Not a list of things to refuse — a list of places its author could not fully discharge the burden.
Test each **against the source, not against this paragraph.** A fix-up that applies everything
uncritically has learned nothing from three rounds of this, and refusal-with-cause is a first-class
outcome (the fifth fix-up refused V8-3's `[A]` alternative and was right to).

1. **V9-4 may not be a defect.** Phase 5 owns buffer *sizing* (DESIGN.md l. 915), and a `scale.<prog>`
   rectangle is derived from a buffer dimension. DESIGN.md assigns the *sub-viewport* to Phase 7
   (l. 1047) and *storage* to Phases 3/4 (ll. 769, 855) and says nothing about who multiplies. If you
   conclude Phase 5 is right, the fix is a **citation**, not a retarget — and §0.8's use of that row
   as V8-2's precedent then stands as written.
2. **V9-1's cost limb (d) is conditional and round nine says so.** The ~90-drains-per-frame figure
   assumes the foreign error *recurs*. A one-off foreign error costs one replay, exactly as §4.7.4
   l. 1940, §7 ll. 2667–2669 and `[D-P1-32]` l. 3044 say. What is unconditional is that all three are
   written as if the recurring case cannot arise. **Whether that inverts the decision to keep the
   elision is a judgement round nine did not make; do not treat it as made.** If you re-open the
   elision, that is a design call needing its own argument, not a correction being applied.
3. **V9-1's arithmetic note is round-seven-era prose that three rounds have passed.** "43 program
   switches per frame" reuses a registry cardinality as a per-frame event count. Round nine recorded
   it rather than raising it. Fix it everywhere or leave it deliberately — but say which.
4. **V9-6 assumes the `[fix-up:]` marker convention is binding.** §G1.3 does not require markers; the
   convention is the document's own, and §0.6's V6-1 used a *missing* marker as evidence a fix-up
   never ran rather than as a defect in itself. Reasonable to apply, reasonable to decline with cause.
5. **V9-7 may be pedantry.** §5's "sufficient on its own" rule is an argument *for* breadth, and the
   row's Phase 6 entry is substantively right (round nine cleared the ownership in §2). If you keep
   the breadth, consider whether Phase 5 should come back for the buffer-estate stake §4.7.4 l. 2022
   and §3 l. 668 both keep in prose.
6. **V9-10 is a self-description defect, not a substance defect, and round nine cleared the
   substance.** Property (i) does *not* assert the opposite of the deleted per-sample claim, and both
   facts it relays are already Phase 6's own inputs (DESIGN.md ll. 966 and 975). The fix is scoping one
   disclaimer the way §4.7.4 ll. 1953–1954 already does. **Do not over-correct into deleting the
   citations**, and do not turn RESEARCH.md §4.4's "frame start" versus the document's "frame begin"
   into a misquotation finding — DESIGN.md ll. 281 and 975 and RESEARCH.md l. 569 all gloss that moment
   as "frame begin".
7. **V9-11's second half is housekeeping.** A 169-character line is a wrap violation, not a defect of
   substance. Fix it or don't; it is not worth an argument either way.

## Do not re-fight these

Round nine's clean list survived a third consecutive audit; re-deriving it is how a session spends its
budget on nothing. **V8-2's retarget is correct** — and better supported than the document argues,
which is V9-8's second half: DESIGN.md l. 856 says *"execution is Phase 7, tag v0.5"* outright in
Phase 4's *Scope — in*, and no site cites it. **Phase 6 is the right owner of the `instanceId` upload**
(DESIGN.md ll. 968–969's "the update entry point is yours" pattern, plus Phase 4's *Scope — out*
l. 858). V8-3's replacement claims verify at source. Round eight's `Resolutions` table was audited site
by site and every named site was really edited. §11.5's routing of V8-1's residue to §11.4 rather than
upstream is right. Also clean: `[D-P1-33]`, `[D-P1-34]`, `[D-P1-35]`'s central arguments; §3's second
row; §3.1's flagged delta; the four-handle/no-renderbuffer model; the `ivec3`/`mat3` absence; the App
F.7 mappings; the seventeen §4.1 template rows; the Gradle/ASM work; the CI ordering; §4.2.4a's
half-deletion; the four OQ spike specs; the pin table. The decision log reads coherently end to end
apart from V9-5 and V9-6. **V8-7 stays closed** — two rounds have now declined to re-open it.

## The signature question, and one thing that is not yours to fix

§0.8's *"no service signature was added, removed or changed"* is **true**, and round nine confirmed it
on three independent lines — but it could not confirm it **byte-for-byte**, because `git HEAD` is the
original build-session draft (2159 lines against today's 3363) and all five fix-ups are uncommitted
working tree. Every verify session pays that cost. It is a workflow matter, not a document defect, and
§11.5 is for requests against RESEARCH.md and DESIGN.md — so round nine recorded it as an observation.
**If you think the cadence should commit each fix-up, raise it with the project owner; do not decide it
unilaterally and do not add it to §11.5 as if it were a document request.**

Note that **none of round nine's corrections touches a signature.** V9-2's fix is a javadoc sentence
*inside* the block, not a declaration. If your §0.9 claims signature invariance again it will be true
again — and V9-2 is precisely the reason to sweep ll. 1654–1818 anyway.

## Network use

**None is needed.** Round nine re-verified the pin table at ~04:48 UTC on 2026-07-25 (`0.6.6-alpha`,
both endpoints agreeing, `lastUpdated` unmoved at 2026-07-24T13:37Z, no drift) and confirmed both
`docs.gl` URLs and the quotation at source. That is the third pin observation inside two hours; a
fourth buys nothing. If you fetch anything, disclose it and name the finding that turned on it.

## Hard rules (§G1.3, plus §G1.1's carry-overs)

No code, no builds, no tests, no scope creep. Do not modify `RESEARCH.md` or `DESIGN.md` — proposals go
in §11.5. **Do not modify `PHASE_1_REVIEW.md` … `PHASE_1_REVIEW_8.md` at all, including their
`## Resolutions` sections — they are evidence.** In `PHASE_1_REVIEW_9.md`, append `## Resolutions` at
the end and change **nothing** above that heading. Context discipline per §G1.1. Adversarial
sub-agents: §G1.1 forbids them to a build session and §G1.3 is silent, so the call is yours —
**disclose it either way**, and if you use any, re-derive every load-bearing quote yourself before it
reaches the document. Round nine used them under exactly that gate and recorded that one agent
breached its instructions, which is why the gate matters.

## Deliverable

Exactly two files touched, and no others created:

1. **`Schmaloogium/PHASE_1_DOC.md`** — the corrections applied, plus a new **§0.9** addendum following
   §0.4–§0.8's established shape:
   - what ran (round nine's counts, what was applied / applied-wider / narrowed / refused);
   - **the design calls you made, recorded as arguments rather than outcomes** — a fix-up session gets
     no adversarial review of its own, and the next session can only attack reasoning that is written
     down. V9-1's branch choice belongs here, in full;
   - inputs read beyond the build session's list, each with the finding it turned on;
   - the lesson worth recording;
   - a **§G1.3 status** paragraph: whether §5 changed, whether any signature changed, and what the
     next step in the cadence is.
   Restamp §0.8's status header as you supersede it.
2. **`Schmaloogium/PHASE_1_REVIEW_9.md`** — a `## Resolutions` section: per finding, the disposition
   (applied / applied wider / narrowed / **refused with cause** / no change required), and a `Where`
   column naming **every site edited, not every site named**. Then a **"Neighbours swept"** list
   (sites your edit reached that round nine did not name), a **"Checked and correctly left alone"**
   list so the next audit can tell a considered omission from an oversight, and a `### §G1.3 status`
   block.

Then stop.

---

One deliberate choice, in case you want to adjust it: the "where round nine is weakest" section names
*claims to test*, never verdicts to reach — and it names seven of eleven findings, which is a higher
proportion than any prior brief. That is on purpose. Round nine's own conclusion is that the failure
mode at this maturity is no longer missing material but adopted material, and a brief that pre-answered
its own questions would reproduce exactly what it is warning against. If a finding is fine on
derivation, refuse it with cause and record the derivation. If one is worse than round nine said, say
that too. Nine rounds of PASS-WITH-CORRECTIONS is a fact about how much new prose each round
introduced, not a quota — and if your sweep is complete and §5 comes out unchanged, the honest outcome
is that Phase 1 closes.
