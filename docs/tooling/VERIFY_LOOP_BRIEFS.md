# Verify-loop brief templates

*The prompts `/verify-loop` assembles per round, kept here as readable documents rather than only as
string literals in `Schmaloogium/.claude/workflows/verify-loop.js`. The script is the executable
copy; **this file is the reviewable one.** If you change what an agent is told, change it in both,
and say which.*

**The harness is parameterized by phase.** `args.phase` selects the document; paths, the spec and
doc-gate line ranges, the OQ set and the dependency docs all derive from the script's `PHASE_FACTS`
table. Adding a phase is a table row, not a prompt edit. Everything below is written generically;
where a passage was learned from a specific Phase 1 round, the round is named as provenance rather
than as a claim about the document currently under review.

**Every path in every prompt is repo-relative to `Schmaloogium/`.** The documents moved out of the
repo root into `docs/` in commit `9df5f05`; the harness originally referenced bare filenames and
none of them resolved afterwards. This file lives at `docs/tooling/` for the same reason — it
documents phase-agnostic tooling, not Phase 1.

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
- **Forbidden sources, by pattern and by provenance:** do not read any prior session's terminal
  transcript, at any path — anything under a directory named `chatlogs/` **anywhere below `docs/`**
  (**any** extension; some are `.md`) and **any `*.txt` at the repo root**. **A sub-agent read one contrary to instruction in
  Phase 1 round nine**; its transcript-derived conclusion was discarded and re-derived from permitted
  sources (`docs/phase1/reviews/PHASE_1_REVIEW_9.md` §0.2). §G1.2 bars a reviewer from the author's
  conversation context precisely because it transmits the author's blind spots.

  The rule used to read `Schmaloogium/*.txt` — a single-level, `.txt`-only glob on the repo root.
  After the reorg that matched **1 of the 17** transcripts on disk: the fifteen Phase 1 chatlogs had
  moved to `docs/phase1/chatlogs/`, and one was `.md` and had never been covered at all.

  **Both halves are load-bearing, which the repo demonstrated within the hour.** Restating the rule
  as "`docs/phase*/chatlogs/**` plus `phase2chat.txt`" fixed the moved files but named the root-level
  transcript individually — and `/export` immediately dropped a second one there
  (`2026-07-25-175142-….txt`, the session that wrote this harness). Naming files does not survive a
  command that creates them. **And the `chatlogs/` half was outrun in turn:** `docs/phase*/chatlogs/`
  is one directory level deep, so it missed `docs/reference/pintonium/chatlogs/` — a 3,970-line
  session transcript sitting outside the `phase*` tree entirely. That is three separate times a rule
  has been narrower than the repository. It now reads *any directory named `chatlogs/` anywhere below
  `docs/`*. Hence: a **pattern** at the root, a **depth-independent pattern** for `chatlogs/`, and an
  explicit provenance catch-all telling the agent that an unfamiliar transcript is still barred and
  that uncertainty means do not open it. Enforcement remains prompt-level — `Explore` has `Bash`, and
  nothing in `settings.local.json` denies these reads — so this is structural, not airtight.
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

**This clause has two forms, and picking the wrong one is a real failure.** The paragraph above
continues, on a mature document, with *"N verify rounds have already run; the failure mode at this
maturity is no longer 'the reviewer missed something' but 'the reviewer kept the loop alive'."* That
was written for Phase 1 at round eleven and it is *false* on a document that has never been
reviewed — and harmful, because it biases finders toward silence and the adjudicator toward a false
PASS on a document nobody has read.

So at `startRound=1` the harness inverts it: **the whole document is unreviewed surface, a
substantial findings list on a first reading is expected rather than suspicious, and the
anti-inflation discipline applies to *severity* — do not call a note a correction — not to volume.**
`clean_areas` stays required either way. The script keys this off `FIRST_EVER_REVIEW`.

---

## 1. The verify brief — five attack lenses

One agent per lens, run in parallel, each blind to the others. Ordered by historical yield; `lean`
takes the first three — **so the order decides what a cheap round actually checks.**

**The order is not fixed.** On a mature document the last fix-up's prose leads, because that is
where the findings were. On a document that has never been reviewed lens 1 is degenerate — there is
no prior fix-up — so the structured checks lead instead and the rewritten whole-document lens sweeps
up what they do not cover:

| | `lean` takes |
|---|---|
| mature doc | new surface · interfaces · conformance |
| first-ever review | interfaces · conformance · doc gate |

### Lens 1 — the new surface *(highest yield on a mature doc; source of nearly every finding for four rounds)*

The previous fix-up's new prose is the largest unreviewed surface in the document, and exactly one
session has read it — the one that wrote it. Attack the sites it edited, its `§0.N` addendum, any
claim that addendum makes *about the rest of the document*, and — the part that matters —
**neighbours**: the row, bullet or sentence adjacent to a site that was edited correctly. V9-2, V10-2
and V10-3 were all neighbours that a true claim elsewhere stopped anyone looking at.

*On a first-ever review* this lens is rewritten: the entire document is the new surface, so it takes
what the structured lenses skip — §4 at the level of internal consistency, §6, §7, §8, §9, §12 — and
attacks claims the document makes *about itself* ("X is specified in §Y" — go read §Y), repeated
identifiers and paths that must agree, and places where §4 decides something §2 describes
differently.

### Lens 2 — interface honesty and §5

§G1.2's *Interface honesty* check, and it owns the **`touches §5`** call — the most consequential
judgement in the round, because §G1.3 makes it decide whether another verify session is owed. Ask
whether §5 is still *sufficient on its own*, which is what it claims to be, and whether any §4
statement retracts something §5 still asserts.

**The inward half of this check only exists when the phase has a dependency**, and for Phase 1
(`depends on: —` in §G5.1) it was vacuous. Where there are dependency docs the lens gains the
highest-yield work available: take every item the document says it consumes back to the
dependency's own **§5** — the binding contract under §G1.1 — and open the cited section rather than
trusting the citation. A consumption row citing a section that does not say what the row claims
cannot be found by reading the document under review alone. Phase 2's §5.2 is an entire consumption
table of exactly this shape.

### Lens 3 — conformance map

Zero unmapped in-scope rows in §3, and mapped rows spot-checked **against the cited RESEARCH.md/App
text**. The criterion is *contract item → design element satisfying it*; a row that declines to name
an owner for something the criterion does not ask about is **not** an unmapped row. The class of
error this lens exists to catch is a row citing a source that does not say what the row claims.

### Lens 4 — doc gate and template completeness

The phase spec's *Doc gate* met **literally**; all thirteen §G9 sections present *and substantive*;
every assigned OQ carrying a full §G4.4 spike spec. A section that exists but says nothing is a
finding; a section that is short but complete is not.

The spec line range, the doc-gate line range and the OQ set are interpolated per phase from
`PHASE_FACTS` (Phase 1: ll. ~585-658 / ~649-652 / OQ-2, OQ-12, OQ-20, OQ-21 · Phase 2: ll. ~662-723
/ ~713-715 / OQ-10). These are the one kind of fact in the table that rots silently, since `DESIGN.md`
is edited by sessions other than this loop — re-check them if a doc-gate finding ever looks like it
is reading the wrong text.

### Lens 5 — scope discipline and binding decisions

Nothing designed that *Scope — out* assigns elsewhere; nothing from *Scope — in* dropped; no
D-1..D-10 contradicted; no contract-visible component "improved" against §G4.2. §G1.1 also requires
input contradictions to be *reported* with a ruling and its provenance — a place where the doc
smoothed one over is a finding.

### Reading order, and it is a discipline rather than a suggestion

Finders read `docs/design/v1.1/DESIGN.md` Part I and the phase spec, then the phase doc under review,
then its dependency phase docs where it has any, then only what their lens needs.

**They read no review file for the phase under review** — `PHASE_<N>_REVIEW_*.md` for their own `N`.
The adjudicator reads those, last. The ban is scoped to the phase deliberately: a **dependency**
phase's reviews are not an independence problem, they are evidence about the contract being checked,
and the document under review cites them (`PHASE_2_DOC.md` §0.1 cites `PHASE_1_REVIEW_11.md` for the
§G5.3 gating question). Those may be read.

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
`docs/phase<N>/reviews/PHASE_<N>_REVIEW_<R>.md` in §0 / §1 / §2 / §3 shape with an explicit
**touches §5: yes/no** on every finding and **exactly one verdict**, emitted as a heading on its own
line (`# PASS-WITH-CORRECTIONS`).

Two clauses carry the weight.

**Read the prior rounds last.** Only now, with the candidates in hand, read the previous review
including its `## Resolutions`, and disposition each candidate against the settled list. A finding a
prior round already cleared is not a finding — and *an argument that holds on derivation is itself
worth recording in §2.* On a first-ever review this clause is replaced by its negation: there are no
prior rounds, nothing has been cleared, and every candidate stands or falls on the adjudicator's own
derivation.

**A verdict-token hazard, where the document under review is about pass/fail.** `PHASE_2_DOC.md`
designs a conformance harness and uses the bare words `PASS`, `FAILED` and `SKIPPED` about eight
times as *run-outcome values* in its own schemas and tables. Those are its subject matter, not
verdicts, and they collide with the adjudicator's verdict enum. The prompt says so explicitly: do
not let a grep for "PASS" over the document inform the review of it.

**The verdict standard**, and it is the most important paragraph in the harness. From the round-6
prompt's own closing note:

> after five rounds the failure mode has shifted from "reviewer misses things" to "reviewer keeps the
> loop alive."

At round eleven that is doubly true, so the adjudicator is told plainly: **PASS is available and it
is the outcome that ends the cadence.** Return PASS when there are no blocking findings and no
corrections; notes do not block PASS. A round that invents a correction to look productive is worse
than useless. **And the converse, stated just as hard: do not soften a real correction to reach
PASS.** Judge the document in front of you.

**On a first-ever review the standard is re-pointed, not relaxed.** The loop-fatigue argument does
not exist at round one, so the adjudicator is told instead: nothing here has been argued over or
cleared before, a first reading returning a substantial list is expected, and **PASS is available on
the evidence and nothing else** — a strong claim on a document nobody has reviewed, and one to make
only if it is true. Both halves of the anti-inflation clause survive: inventing a correction is
worse than useless, and softening a real one to reach PASS is worse still.

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
cd Schmaloogium
awk '/^## 5\. Cross-phase interfaces/,/^## 6\. Failure modes/' \
    docs/phase<N>/<version>/PHASE_<N>_DOC.md | sha256sum
```

Both hashes are returned with `section5_unchanged`. **If §5 did change, say so truthfully** — it
means §G1.3's re-verify trigger fires, which is a legitimate outcome and not a failure. Never report
it unchanged without having run the comparison.

The content anchors are portable across phases — both phase docs carry `## 5. Cross-phase
interfaces` and `## 6. Failure modes` verbatim, per §G9's mandatory template — but **the path is
not**, and the bare `PHASE_1_DOC.md` this command used to carry stopped resolving after the reorg.

`git status --short` must show **exactly two paths** touched: the phase doc and the round's review
file. One caveat the fix-up is told to report rather than paper over: if the phase's `reviews/`
directory holds no tracked file, git collapses it to a single `??` entry and cannot show which files
changed. Neither phase defined today is in that state — `docs/phase1/reviews/` holds eleven reviews
and `docs/phase2/reviews/` a tracked `.gitkeep` — so the check is exact for both. A newly added
phase's `reviews/` would collapse until its first review lands or a `.gitkeep` is committed. The
phase doc itself is tracked, so a modification to it always shows. When the collapse does occur the
agent returns the check as *degraded* in `files_modified` instead of claiming a clean result.

Note the operator-side §5 check in the command's pre-flight is **not** the same command and must not
use `git show HEAD:…` as its baseline: in the window between a reorg's `git mv` and its commit,
`HEAD` still holds the doc at its old path, so the `git show` fails — and a failed `git show`
still produces a non-empty `diff`, which reads as a false "§5 changed" on every round. Snapshot with
the content-anchored `awk` above before the run instead.

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
- **Editing evidence.** `PHASE_1_REVIEW_1.md` … `_10.md`, including their `## Resolutions` sections,
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
