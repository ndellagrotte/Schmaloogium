You are a fresh Claude Code session running the tenth verify session (DESIGN.md §G1.2) on
Schmaloogium/PHASE_1_DOC.md. Working directory:
/home/nick/IdeaProjects/schmaloogium-project/Schmaloogium/.

## Why this session exists

The cadence: build session → nine verify sessions (`PHASE_1_REVIEW.md` … `_9.md`, every one
PASS-WITH-CORRECTIONS) → six fix-ups. The sixth fix-up applied round nine's V9-1 … V9-11 — six
corrections and all five notes, none refused — and recorded its resolutions in
`PHASE_1_REVIEW_9.md` under `## Resolutions`. It altered **§5.2 at two rows** (the GL-error row and
the non-verbs row), so §G1.3's *"re-verify only if §5 changed"* rule fired again. Until you return a
verdict, `PHASE_1_DOC.md` is **not a valid dependency input** (§G5.3) and Phase 2, Phase 3 and
everything downstream stay blocked.

Your contract is §G1.2's: read what the build session read, attack the document, write
`PHASE_1_REVIEW_10.md` with a findings list (location, claim, evidence, severity **blocking /
correction / note**) and exactly one verdict. **Fix nothing.** Do not touch `PHASE_1_DOC.md`, and do
not touch any prior review file including their `## Resolutions` sections — they are evidence.

## The standing lesson, and the one aimed at you

Three rules are now on the record, each found by the round that followed the one it describes:

- §0.7: *unreviewed material yields findings in proportion to its size, not to the document's
  maturity.*
- §0.8 (round eight): *a review's supporting argument is not evidence, and a fix-up that promotes it
  into §5 has changed its status without changing its support.*
- §0.9 (round nine): *a fix-up that re-derives every finding can still under-scope its own sweep, and
  the sites it misses are the ones its correctness elsewhere hides.*

**The sixth fix-up's new prose is now the largest unreviewed surface in the document, and exactly one
session has read it — the one that wrote it.** That is where your findings are. Round nine's §2 clean
list survived a third consecutive audit; do not spend your budget re-deriving it.

There is a fourth thing worth knowing before you start, and it is unusual enough to state plainly:
**the sixth fix-up overruled the brief that commissioned it.** That brief offered two branches for
V9-1 — reword §5.2 (§5 changes, a tenth session is owed) or delete the clause (*"§5 may then be
unchanged"*, the phase closes) — and round nine's §3 left the same door open in its own words. The
fix-up ruled branch (b) **does not exist**, on the reading that §G1.3's *"altered the doc's
Cross-phase interfaces section"* means the section's text moved, not that the interface contract
moved. It is argued in §0.9 and in the `Resolutions` table. **You are the first independent reader of
that argument, and it is the argument that decides whether you are the last verify session or the
second-to-last.** Test it against §G1.3's actual words and the document's own precedent, not against
this paragraph.

## Read, in this order

1. **DESIGN.md Part I in full** (§G0–§G10) and the **Phase 1 spec** in Part II (ll. 585–658). Other
   phases by title and §G5.1 row only — unless a finding turns on an ownership claim, in which case
   the five-round precedent lets you read that phase's *Scope* bullets and disclose it.
2. **RESEARCH.md §0** (reading guide, confidence tags) and **§1**, then the spec's Required inputs:
   **§5.1–§5.3, §6.1, §7.2, §12.2**.
3. **Template ground truth**, complete: `build.gradle`, `settings.gradle`, `gradle.properties`,
   `gradle/scripts/*`, `gradle/wrapper/gradle-wrapper.properties`, `src/**`, `.github/workflows/*`,
   `README.md`.
4. **PHASE_1_DOC.md**, §0 through §12. It is 3709 lines.
5. **Last, and only after your own findings are formed:** `PHASE_1_REVIEW_9.md` **including its
   `## Resolutions` section**, plus targeted searches across rounds one to eight. Reading the
   resolutions early makes you an auditor of someone else's reasoning instead of an independent
   reader; §G1.2 exists to prevent exactly that.

Record what you read beyond this list, and why, in your §0.1. Record deliberate omissions as
omissions — rounds eight and nine both did, and both were right to.

## The new surface (line numbers as of the sixth fix-up, 3709 lines)

- **§0.9, ll. 515–702** — the fix-up addendum, and by a wide margin the largest single block of new
  prose in the document. Seven argued design calls, an inputs disclosure, a lesson, a §G1.3 status
  paragraph. Round nine's own §3 predicted the next round's findings would cluster here.
- **§4.7.4 ll. 2160–2190** — the elision paragraph, rewritten. *"The one sound remedy"* is gone;
  *"Two remedies exist, and neither is described here as the only one"* replaces it, with a
  cost-versus-mechanism distinction and two stated limits.
- **§4.7.4 ll. 2214–2223** — a **new** recurring-foreign exception to the replay's *"paid … once"*.
- **§4.7.4 ll. 2247–2259** — precondition (ii), rewritten into two enumerated causes.
- **§4.7.4 ll. 1917–1922 and ll. 2061–2067** — **javadoc inside the signature block**:
  `GLDevice.drainErrors()` and the `GLError` record. New sentences, no declaration touched. This
  block is where round nine found the defect no `Where` column could point at.
- **§4.7.4 ll. 2300–2306 and l. 2316** — the absent-verbs table header, its **renamed** last column,
  and the instanced-draw row's rewritten last cell.
- **§5.2 l. 2836** (per-revision changelog) · **l. 2837** (GL-error row) · **l. 2846** (non-verbs
  row). **These three are the reason you exist.**
- **§3 l. 891** (the `countInstances` row, now `[v0.5]`) · **l. 895** (the `scale.<prog>` row, which
  now names **no owner** for the rectangle computation).
- **§2.4 l. 853** · **§7 ll. 2961–2967** · **§9 l. 3086** · **§12 item 4b l. 3609**.
- **§4.2.3 ll. 1060–1107** — the `SeamClasspathArguments` home, rewritten on a compilation-order
  argument, with a new **`[U]` provenance paragraph** at ll. 1097–1107.
- **§11.3 item 10, ll. 3414–3433** — a **new** subsection heading (*"Unverified claims (`[U]`) this
  document makes and cannot source"*) and its single item.
- **§11.4 ll. 3463–3476** (composite loop, now milestone-tagged) and **ll. 3478–3508** (the
  foreign-GL hand-off, rewritten with two limits and an additive-request route).
- **`[D-P1-30]` l. 3340 · `[D-P1-32]` l. 3342 · `[D-P1-35]` l. 3345** — amended rationales and
  amended `[fix-up: …]` markers.
- **§0.7 ll. 304–307, 318–327** — a rewrapped pointer and a restamped, past-tensed status paragraph.
- **§0.8 ll. 379–387, 407–415, 420–423, 452–457** — four **bold supersession pointers** appended to
  round eight's bullets; **ll. 494–511** restamped and past-tensed; **ll. 512–513** a new closing
  supersession sentence. The round-eight arguments themselves are left intact as a record. Check
  that the convention is followed rather than history being rewritten.
- **Housekeeping:** §0 header l. 10 and its dated-claims note ll. 13–17 · closing paragraph
  ll. 3687–3709.

## Where the sixth fix-up is weakest, stated by the session that wrote it

Not a list of defects — a list of places its author could not fully discharge the burden. Test each
**against the source, not against this paragraph.** If one turns out to be fine on derivation, say
so in §2; a round that reports only findings misrepresents its own coverage, and refusing an item
here with cause is a first-class outcome.

1. **The V9-1 branch ruling is the load-bearing one and it is a reading, not a fact.** §G1.3 says
   *"Re-verify only if §5 changed. If corrections altered the doc's Cross-phase interfaces section,
   the doc goes through a fresh verify session."* The fix-up read "section" textually and cited
   §0.7 l. 318 and §0.8 l. 494 — both of which declare *"altered §5"* for **prose-only** corrections
   at these same two rows — as settling it. Two counter-readings exist and neither is addressed:
   that §G1.3's purpose is to protect *dependents*, and a deletion that removes a false statement
   changes nothing a dependent builds against; and that round nine's §3 last paragraph and the
   fix-up's own commissioning brief both treated (b) as available, so the fix-up is overruling two
   documents rather than one. **Whether (b) was genuinely foreclosed decides whether this session
   was owed at all.** Judge it; do not assume the fix-up's answer, and do not assume the brief's.
2. **The recurring-foreign cost may not survive its own arithmetic.** The new prose says a recurring
   foreign error costs *"on the order of ninety extra synchronous queries and ninety redundant
   uploads per frame"* (§4.7.4 ll. 2214–2223), *"a replay of the whole ~90-uniform set per program
   set"* (§7 ll. 2961–2967, `[D-P1-32]` l. 3342), and *"one replay per program set per frame"*. Check
   the three statements against each other and against RESEARCH.md §4.2's *"refreshes ~90 built-in
   uniforms"*: is a program's uniform *set* the same as ~90, is the unit per-frame or per-sweep, and
   does a single foreign error land in one sweep's window or in every subsequent one? A cost figure
   introduced to correct a mispricing is the worst place for a second mispricing.
3. **The "43" was removed and nothing replaced it.** §4.7.4 l. 2177 and §11.4 l. 3494 now read *"at
   every program switch in the frame"*. The comparison the elision decision rests on — *"roughly one
   extra query per frame against a factor of two at every program switch"* — now has a number on
   neither side. §7 l. 2959 still says *"there are 43 slots"*. Test whether the ledger is still
   legible, whether an implementation session can act on it, and whether trading a wrong number for
   no number weakens the argument it was supposed to repair. The fix-up's §0.9 argues the direction
   of the old error favoured the conclusion being defended; check that too.
4. **V9-7's header widening may have broken the row it was meant to fix.** §5.2's non-verbs row now
   names **14, 13, 7, 6, 5, 3, 4** — seven phases — under a header widened to cover *"the phase that
   would request it, and, where a row names an adjacent owner of the served work instead, that phase
   too."* Three things to test. Is a seven-phase column still doing a consumer column's job? Is the
   claim that *"the instanced-draw row is the only one today"* true — the **face-culling** row names
   Phase 3 for the ownership map while noting `DESIGN.md` routes `backFace.*` to Phase 7, which looks
   like the same shape? And does readmitting **Phase 5** contradict §0.8's record of removing it, or
   is the bold pointer at §0.8 l. 499 the right device for that?
5. **V9-4 may have traded a wrong owner for an unmapped row.** §3 is the **conformance map**, and
   §G9 requires *"ZERO unmapped rows"* while the Doc gate is checked against it. The `scale.<prog>`
   row (l. 895) now says `DESIGN.md` assigns no owner to the rectangle computation and that this
   document therefore names none, with the provenance cell reading *"the ownership is `DESIGN.md`'s
   silence, reported rather than filled"*. Test whether a row that declines to name an owner still
   satisfies the conformance-map criterion, whether §G1.1's *"report contradictions"* rule extends to
   silences, and whether **§11.5 should carry a request to `DESIGN.md`** after all — the fix-up ruled
   it should not, on the ground that a silence is not a conflict, and §11.5 item 4 (the missing §G2.4
   rung) is the precedent for the opposite call.
6. **V9-3's `[U]` may be in the wrong place, or the wrong size.** §4.2.3's Gradle mechanism is now
   tagged `[U]` with a new open-question row at §11.3 item 10, on RESEARCH.md §0.2's requirement.
   But a `[U]` that supports a **v0.1 checklist item** and the structure of three build files is
   arguably a spike (§G4.4, §10) rather than a §11.3 note — and §11.3's own heading is *"Input
   contradictions, defects, and inherited values found"*, so a fourth category was invented under it.
   Check the placement, and check the claims themselves: is compilation order really the decisive
   reason, is `buildSrc`'s classpath contribution unconditional, does an included build's precompiled
   plugin really require both a `settings.gradle` wiring and an `apply`, and is *"no indirection
   rescues the literal `new X(...)`"* true?
7. **Eleven lines of new javadoc went into a block whose invariance the document asserts.** The
   `drainErrors()` javadoc now says the call is *"NOT a query of GL's state on demand, in either
   direction"* and that an empty return *"does not mean the per-context error flag is clear"*; the
   `GLError` javadoc now says its own entailment *"is not unconditional"* and that `op`/`subjectLabel`
   *"then name the wrong call"*. Test both against §5.2's GL-error row, §6's rung-2 row and
   `[D-P1-32]` for drift, and test whether a record whose own javadoc says it may name the wrong call
   is still the contract §12 item 19 verifies and §G2.4 rung 2 acts on.
8. **§0.9's counts are an auditable claim and were written expecting to be audited.** It states that
   round nine named **29 distinct editable sites**, that **all 29 are edited**, and that **six
   further sites** were reached by the sweep. Round nine audited round eight's `Where` column site by
   site and found every claim real. Do the same. **A site named but not edited is a finding**, and so
   is a count that does not reconcile.
9. **The bold-pointer convention may be doing two different jobs now.** §0.4–§0.7's pointers say *a
   later round corrected this*. §0.8's new pointer at ll. 407–415 says something stronger — that a
   supporting argument *"does **not** stand"* and is withdrawn. Test whether withdrawing a superseded
   bullet's support is the same act the convention was built for, or whether it is history being
   edited under a device meant to preserve it.
10. **§0.9 claims no signature changed.** It will very likely be true again — no declaration was
    touched — but the same claim is why round nine found the `GLError` javadoc defect in the first
    place. Verify it rather than taking it, and note that you cannot verify it byte-for-byte: see the
    workflow note below.

## Audit the `Resolutions` table, site by site

Round nine audited round eight's `Where` column site by site and found every named site really
edited — the third consecutive round at which a whole applied list survived audit. The sixth
fix-up's `Where` column was written expecting the same treatment, and its line numbers were
re-resolved against the final file rather than estimated. Check them. The table also claims four
things worth testing independently:

- **One refusal that is framed as a non-decision.** The fix-up applied V9-1's cost limb as a
  **record** and explicitly declined to re-open the elision, on the ground that re-weighing it would
  be a design call arriving through a correction. Test whether that is discipline or evasion — the
  ledger is now described as incomplete, and a decision defended by an incomplete ledger is either
  honestly flagged or quietly broken. **This is the question the fix-up says it left open on
  purpose**, and it is the honest place to start an attack.
- **"Neighbours swept" — six sites.** The `drainErrors()` javadoc, §11.3 item 10, the §0 header and
  its dates note, §5.2's opening changelog row, the closing paragraph, and §0.9 itself. Confirm they
  were real neighbours of an edit rather than scope creep.
- **"Checked and correctly left alone."** Four surviving *"mutating"* sites each with a stated
  reason (§0.7 ll. 254/257 as superseded rationale, §0.8 l. 485 as a deliberate quotation, §4.7.4
  l. 2116 as a different sense, §4.7.5 l. 2376 as the recorder); §6's two rows and §9's staging note
  as already correct; `[D-P1-33]`, `[D-P1-34]`, `DrawService`'s javadoc and §1.2 as out of V9-7/V9-8's
  reach; the `[A]` tag on §3's second row. Verify each — a wrong "left alone" is the same defect as a
  missed site, wearing a justification.
- **§11.5 unchanged at four items, with two candidates declined** (V9-4's silence, and the commit
  cadence). Test both routings.

## Do not re-fight these

Round nine's clean list survived a third consecutive audit, and re-deriving it is how a round spends
its budget on nothing. **V8-2's retarget is correct** and is now cited to its strongest support
(`DESIGN.md` Phase 4's *"execution is Phase 7, tag v0.5"*). **Phase 6 is the right owner of the
`instanceId` upload.** V8-3's replacement claims verify at source. Round eight's and round nine's
`Resolutions` tables were each audited site by site. §11.5's routing of V8-1's residue to §11.4 is
right. Also clean: `[D-P1-33]`, `[D-P1-34]`, `[D-P1-35]`'s central arguments; §3's second row;
§3.1's flagged delta; the four-handle/no-renderbuffer model; the `ivec3`/`mat3` absence; the App F.7
mappings; the seventeen §4.1 template rows; the Gradle/ASM work; the CI ordering; §4.2.4a's
half-deletion; the four OQ spike specs; the pin table's structure and its re-verification procedure.
**V8-7 stays closed** — three rounds have now declined to re-open it. The GL quotation has been
verified word-for-word at source three times and both `docs.gl` URLs once.

## One thing that is not a document defect, and is not yours to fix

`git HEAD` is still the original build-session draft (2159 lines against today's 3709) and all six
fix-ups are uncommitted working tree, so **no verify session can check signature invariance
byte-for-byte.** Round nine reconstructed it on three independent lines and recorded the limit
rather than papering over it; the sixth fix-up noted it in §0.9 at the project owner's explicit
direction, and correctly added nothing to §11.5, which is for requests against RESEARCH.md and
`DESIGN.md`. You will pay the same cost. Reconstruct, state the limits of your reconstruction, and
do not re-raise it as a finding or add it to §11.5.

## Network use — two sanctioned purposes

1. **The pin table.** RESEARCH.md flags Cleanroom's *daily* release cadence and §4.2.6's procedure
   exists to be run. Rounds seven, eight and nine all read inside one two-hour window on 2026-07-25
   (~03:05, ~03:52, ~04:48 UTC), and round nine recorded that `lastUpdated` had then been unmoved for
   over fifteen hours. Enough time has probably now passed that yours is a genuinely **independent
   daily sample** rather than a same-window re-confirmation — **say which it is**, and say what it
   implies about the volatility RESEARCH.md's OQ-2 row assumes.
2. **The cited GL page**, `https://docs.gl/gl4/glGetError` and `https://docs.gl/gl2/glGetError`.
   Three rounds have confirmed the quotation and one has confirmed both URLs. A fourth confirmation
   buys little; skip it unless a finding turns on it, and say so either way.

No third purpose. If you fetch anything else, disclose it and name the finding that turned on it.

## Hard rules (§G1.2)

No code, no builds, no tests, no scope creep. Do not modify `RESEARCH.md`, `DESIGN.md`,
`PHASE_1_DOC.md`, or any prior review file — **including `PHASE_1_REVIEW_9.md`'s `## Resolutions`
section, which is this round's primary evidence.** Adversarial sub-agents are permitted by §G1.2:
round eight declined them, round nine used them under a hard re-derivation gate and recorded that one
agent breached its instructions, and the sixth fix-up declined them. **The call is yours; disclose it
either way**, and if you use any, re-derive every load-bearing quote yourself before it reaches your
findings. Context discipline per §G1.1. Create exactly one file: `PHASE_1_REVIEW_10.md`. Then stop —
do not fix anything you find.

## Deliverable

`Schmaloogium/PHASE_1_REVIEW_10.md`, following rounds seven through nine's established shape:

1. **§0** — what you read and in what order; reads beyond the assigned list with the finding each
   turned on; deviations recorded as deviations; network use; the sub-agent disclosure.
2. **§1 — Findings**, each with location, claim under test, evidence, severity, and an explicit
   **touches §5: yes/no** line. That line decides whether an eleventh verify session is required.
3. **§2 — What was checked and came back clean.** Named, because a round reporting only findings
   misrepresents its coverage. Put the `Resolutions` site-by-site audit here if it comes back clean.
4. **§3 — Verdict**: PASS, PASS-WITH-CORRECTIONS, or FAIL. Reserve FAIL for structural misses
   requiring a rebuild. Include the per-finding §5 disposition table and the **§G1.3 line** stating
   whether §5 changed and what the next step in the cadence is.

**If your verdict is PASS — no corrections, or none touching §5 — say so plainly, because that is
the verdict that closes Phase 1 and unblocks Phase 2 and Phase 3.** Nine rounds of
PASS-WITH-CORRECTIONS is not evidence that a tenth is owed; it is a fact about how much new prose
each round introduced, and this round's fix-up introduced less new *design* than any of the last
three — six corrections and five notes, no signature touched, no dependent's build changed. Judge
the document in front of you.

---

One deliberate choice, in case you want to adjust it: the "where the fix-up is weakest" section names
*claims to test*, never verdicts to reach, and it names ten items — more than any prior brief. That
is on purpose, and it is not a prediction that ten findings exist. The sixth fix-up made more
**judgement calls** than any of its predecessors — it overruled its own brief on V9-1, narrowed V9-4
against the fix shape, reshaped V9-3's support, widened a §5 header rather than narrowing a row, and
declined to re-open a decision it had just shown was defended by an incomplete ledger. Judgement
calls are exactly what a fix-up gets no adversarial review of, which is why §0.9 records them as
arguments rather than outcomes and why they are enumerated here. **If an argument holds on
derivation, say so in §2 and move on — that is the finding.** And if one is worse than this brief
suggests, say that too.
