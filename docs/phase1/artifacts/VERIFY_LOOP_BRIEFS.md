# Verify-loop brief templates

*The prompts `/verify-loop` assembles per round, kept here as readable documents rather than only as
string literals in `.claude/workflows/verify-loop.js`. The script is the executable copy; **this file
is the reviewable one.** If you change what an agent is told, change it in both, and say which.*

Every template below is written in the eight-part spine that `PHASE_1_REVIEW_9_BRIEF.md`,
`PHASE_1_REVIEW_10_BRIEF.md` and `PHASE_1_FIXUP_6_BRIEF.md` converged on across ten rounds:

> *Why this session exists* · *Read, in this order* · *Where the last round is weakest* ·
> *Do not re-fight these* · *Network use* · *Hard rules* · *Deliverable* · a closing
> meta-paragraph naming the brief's own authoring choices.

**What the loop changes about that spine, and it is the only structural change:** three of those
sections used to be written by hand each round — *the new surface*, *where the last round is
weakest*, and *do not re-fight these*. They are now **data**, returned by the previous round's
fix-up agent in its `weakest_points`, `sites_edited` and `do_not_refight` fields and interpolated
into the next round's prompts. A fix-up gets no adversarial review of its own; making it state its
own weakest points as a machine-readable field is how the next round gets a target.

---

## 0. The block every agent carries

Prepended to all of them. Three of its clauses were each bought with a specific failure.

### Hard rules

- **READ-ONLY** for every agent except the adjudicator and the fix-up. No creating, editing or
  deleting; no builds, tests or gradle; no mutating git. No writes via shell redirection, `tee` or
  `sed -i` either — naming the shell routes matters, because `Explore` has `Bash`.
- **One forbidden source, named:** do not read `Schmaloogium/*.txt`. Those are prior sessions'
  terminal transcripts. **A sub-agent read one contrary to instruction in round nine**; its
  transcript-derived conclusion was discarded and re-derived from permitted sources
  (`PHASE_1_REVIEW_9.md` §0.2). §G1.2 bars a reviewer from the author's conversation context
  precisely because it transmits the author's blind spots.
- **No scope creep.** Answer only what is asked.
- **The return value is data**, not a message to a human.

### What does not count as work

> You get no credit for confirming that a quote matches a line. Verifying the anchor is the starting
> point, not the finding. Your job is the *interpretive* question underneath it.

Lifted verbatim from the round-4 workflow. In the loop it has teeth it did not have then: a separate
Gate agent re-resolves every citation, so a finding that is only an anchor check is dropped and has
cost the round nothing but tokens.

### Empty is honest

> An empty findings array is a perfectly good outcome. **Do not manufacture findings.**

Plus a required `clean_areas` field on every candidate-producing schema, so the clean-to-dirty ratio
is on the record. Round 4 measured the noise floor without a refutation stage: **33 raw agent
findings, 21 kept.**

---

## 1. The verify brief — five attack lenses

One agent per lens, run in parallel, each blind to the others. Ordered by historical yield; `lean`
takes the first three.

### Lens 1 — the new surface *(highest yield; source of nearly every finding for four rounds)*

The previous fix-up's new prose is the largest unreviewed surface in the document, and exactly one
session has read it — the one that wrote it. Attack the sites it edited, its `§0.N` addendum, any
claim that addendum makes *about the rest of the document*, and — the part that matters —
**neighbours**: the row, bullet or sentence adjacent to a site that was edited correctly. V9-2, V10-2
and V10-3 were all neighbours that a true claim elsewhere stopped anyone looking at.

### Lens 2 — interface honesty and §5

§G1.2's *Interface honesty* check, and it owns the **`touches §5`** call — the most consequential
judgement in the round, because §G1.3 makes it decide whether another verify session is owed. Ask
whether §5 is still *sufficient on its own*, which is what it claims to be, and whether any §4
statement retracts something §5 still asserts.

### Lens 3 — conformance map

Zero unmapped in-scope rows in §3, and mapped rows spot-checked **against the cited RESEARCH.md/App
text**. The criterion is *contract item → design element satisfying it*; a row that declines to name
an owner for something the criterion does not ask about is **not** an unmapped row. The class of
error this lens exists to catch is a row citing a source that does not say what the row claims.

### Lens 4 — doc gate and template completeness

The Phase 1 spec's *Doc gate* met **literally**; all thirteen §G9 sections present *and
substantive*; every assigned OQ carrying a full §G4.4 spike spec. A section that exists but says
nothing is a finding; a section that is short but complete is not.

### Lens 5 — scope discipline and binding decisions

Nothing designed that *Scope — out* assigns elsewhere; nothing from *Scope — in* dropped; no
D-1..D-10 contradicted; no contract-visible component "improved" against §G4.2. §G1.1 also requires
input contradictions to be *reported* with a ruling and its provenance — a place where the doc
smoothed one over is a finding.

### Reading order, and it is a discipline rather than a suggestion

Finders read `DESIGN.md` Part I and the Phase 1 spec, then `PHASE_1_DOC.md`, then only what their
lens needs. **They read no `PHASE_1_REVIEW_*.md` file at all.** The adjudicator reads them, last.

---

## 2. The refutation brief

`N` skeptics per candidate, independent, each told to **kill** it. Six named ways a finding can be
bad, lifted from the round-4 `POSTURE` block: out of scope for this phase · already covered elsewhere
in the doc (**grep the whole document** before conceding an absence) · severity inflation · the §5
claim is wrong · the proposed fix is wrong even where the defect is real · it is a taxonomy or
house-style quibble rather than something a dependent could trip over.

Verdict vocabulary, used precisely: **`CONFIRMED`** (real, and the severity and §5 flag are right) ·
**`OVERSTATED`** (something real, but the severity, scope, §5 claim or fix is wrong — say which) ·
**`REFUTED`** (not a defect; the doc is right or the finder misread it).

> Concede only on evidence you read yourself in the source file. Never on the finder's say-so.

Each refuter is told it is one of `N` working independently: *two agents converging on a wrong answer
is a real failure mode — but do not manufacture disagreement either.* Survival is by majority;
severity is the median across non-refuting judges, which is what stops one loud agent inflating a
note into a correction.

## 3. The steelman brief *(`thorough` only)*

For every surviving correction or §5-touching finding, one agent takes the opposite posture: assume
the author was a careful architect who had a reason, and build the **strongest possible defence of
the document as written** — then say honestly whether it holds.

> A defence you cannot support with a quote is not a defence.

It sees the refuters' conclusions explicitly framed *for you to disagree with rather than defer to*,
and its `final_severity` is the one that stands, because it is the only agent that has read both
sides.

## 4. The gate brief — the re-derivation gate, mechanised

Six of ten rounds **declined** sub-agents, with a real objection to this whole harness:

> the brief requires each load-bearing quote to be re-derived by the reviewer before admission as
> evidence, which makes a delegated finding a hop rather than a saving.

The answer is not to argue with that. It is to mechanise the gate round nine ran by hand — *"no
candidate reached §1 without my own confirmation at the line"* — so the harness earns its delegation
instead of assuming it. One agent opens every cited `file:line` and confirms the quote **verbatim**.

**This is the one stage where anchor-checking IS the work.** `anchor_ok: false` when the text is not
there, is paraphrased, or has words dropped from the middle without an ellipsis — that last is a real
defect this document has produced twice (V9-9, V10-4). A stale line number over correct substance is
not a failure: it returns `corrected_location` instead. Failed findings are **dropped and logged**.

## 5. The adjudication brief

One agent, the only one permitted to create a file that round, and it creates exactly one:
`PHASE_1_REVIEW_<R>.md` in §0 / §1 / §2 / §3 shape with an explicit **touches §5: yes/no** on every
finding and **exactly one verdict**.

Two clauses carry the weight.

**Read the prior rounds last.** Only now, with the candidates in hand, read the previous review
including its `## Resolutions`, and disposition each candidate against the settled list. A finding a
prior round already cleared is not a finding — and *an argument that holds on derivation is itself
worth recording in §2.*

**The verdict standard**, and it is the most important paragraph in the harness. From the round-6
prompt's own closing note:

> after five rounds the failure mode has shifted from "reviewer misses things" to "reviewer keeps the
> loop alive."

At round eleven that is doubly true, so the adjudicator is told plainly: **PASS is available and it
is the outcome that ends the cadence.** Return PASS when there are no blocking findings and no
corrections; notes do not block PASS. A round that invents a correction to look productive is worse
than useless. **And the converse, stated just as hard: do not soften a real correction to reach
PASS.** Judge the document in front of you.

It is also told the thing that keeps delegation honest: *sub-agents generate candidates and
citations; they do not generate findings. Findings, severities and the verdict are yours.*

## 6. The fix-up brief

One agent, §G1.3's contract: apply the corrections, record each resolution under `## Resolutions`,
then stop. **Notes are not applied** — they go under a `### Notes deferred` heading with a reason, so
the next round can tell a considered deferral from an oversight. Refusal-with-cause is a first-class
outcome.

**Re-derive; do not adopt.** A review's supporting argument is not evidence. And **sweep over
formulations, not over sites**: grep for every wording changed and confirm no variant survives. *A
review names the sites its finding turns on; a fix-up owes the sites its edit turns on, and that is
always the larger set.*

### Convergence discipline — a hard constraint, not a style note

This is the part with no precedent in the hand-written briefs, and it exists because of the
document's own standing lesson:

> §0.7: *unreviewed material yields findings in proportion to its size, not to the document's
> maturity.*

Every addendum is next round's finding surface, which makes **the fix-up structurally the finding
generator** — and a naive loop therefore non-convergent. So the `§0.N` addendum is capped at **40
lines** (what ran, the design calls as one-line rulings, the `§G1.3 status` paragraph), with the full
argument moved to the review file's `## Resolutions`, which is what §G1.3 actually requires. The
addenda are this document's self-imposed convention and have themselves generated findings (V9-11,
half of V10-4).

### The §5 gate — run it, do not assert it

Snapshot §5 **by content anchor, never by line number** — the fix-up's own insertions shift every
line below them, which is the trap that made this session re-resolve its citations three times:

```bash
awk '/^## 5\. Cross-phase interfaces/,/^## 6\. Failure modes/' PHASE_1_DOC.md | sha256sum
```

Both hashes are returned with `section5_unchanged`. **If §5 did change, say so truthfully** — it
means §G1.3's re-verify trigger fires, which is a legitimate outcome and not a failure. Never report
it unchanged without having run the comparison. `git status --short` must show **exactly two files**
modified.

### The report is the next round's brief

`weakest_points` is the field that matters: **the judgement calls this session made that an
adversarial reader should test**, stated as claims to test and never as verdicts to reach. This is
the mechanised form of *"Where the Nth fix-up is weakest, stated by the session that wrote it"*, and
it is what a fix-up owes a cadence in which it gets no adversarial review of its own.

---

## 7. What the loop deliberately does not automate

- **FAIL.** §G1.3 routes it to a rerun of the *build* session with the review added to its Required
  inputs — a far larger operation and a human call. The loop stops and reports.
- **Design calls.** Where a fix shape is genuinely a design decision rather than a correction, the
  fix-up agent refuses with cause and records it, exactly as the fifth fix-up refused V8-3 and the
  sixth declined to re-open the elision. **A design call arriving through a correction is the move
  round eight's rule forbids**, and no adversarial session would review it.
- **Editing evidence.** `PHASE_1_REVIEW.md` … `_10.md`, including their `## Resolutions` sections,
  are immutable. Where an earlier review contains an error — round nine's `(ll. 2907, 2906)` cited a
  row three rows off — the correction is recorded in the current round's Resolutions, never applied
  to the evidence.
- **Network.** Rationed to at most one agent for one purpose, §4.2.6's pin table, disclosed either
  way. No finding in the last four rounds turned on a platform fact.

---

*One deliberate choice, in case you want to adjust it: the loop is told to exit on PASS meaning
**zero blocking and zero corrections**, with notes recorded and deferred rather than applied. The
stricter reading — zero findings of any severity — was available and was rejected, because an
adversarial reviewer under instruction to attack can nearly always produce a note, and a loop whose
exit condition an agent can always deny is not a loop with an exit condition. The looser reading —
§G1.3's "verified" state — was also rejected, because `PHASE_1_DOC.md` reached it at the seventh
fix-up and the loop would exit on round one having done nothing. If ten more rounds run without a
PASS, the thing to question is not the cap but whether the fix-up is still writing more prose than
the corrections require.*
