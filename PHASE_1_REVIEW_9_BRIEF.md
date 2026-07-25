You are a fresh Claude Code session running the ninth verify session (DESIGN.md §G1.2) on
Schmaloogium/PHASE_1_DOC.md. Working directory:
/home/nick/IdeaProjects/schmaloogium-project/Schmaloogium/.

## Why this session exists

The cadence: build session → eight verify sessions (PHASE_1_REVIEW.md … _8.md, every one
PASS-WITH-CORRECTIONS) → five fix-ups. The fifth fix-up applied round eight's V8-1 … V8-6 and
recorded its resolutions in PHASE_1_REVIEW_8.md under `## Resolutions`. It altered **§5.2 at two
rows** — the GL-error row and the non-verbs row — so §G1.3's "re-verify only if §5 changed" rule
fired again. Until you return a verdict, PHASE_1_DOC.md is **not a valid dependency input**
(§G5.3) and Phase 2, Phase 3 and everything downstream stay blocked.

Your contract is §G1.2's: read what the build session read, attack the document, write
`PHASE_1_REVIEW_9.md` with a findings list (location, claim, evidence, severity **blocking /
correction / note**) and exactly one verdict. **Fix nothing.** Do not touch PHASE_1_DOC.md, and do
not touch any prior review file including their `## Resolutions` sections — they are evidence.

## The standing lesson, and why it aims at you

§0.7's recorded lesson is *"unreviewed material yields findings in proportion to its size, not to
the document's maturity."* Round eight confirmed it: four of its five corrections landed in the
fourth fix-up's new prose. Round eight then added its own: *"a review's supporting argument is not
evidence, and a fix-up that promotes it into §5 has changed its status without changing its
support."*

**The fifth fix-up's new prose is now the largest unreviewed surface in the document, and exactly
one session has read it — the one that wrote it.** That is where your findings are. Everything
seven rounds swept clean has stayed clean across two consecutive audits; do not spend your budget
re-deriving it.

## Read, in this order

1. **DESIGN.md Part I in full** (§G0–§G10) and the **Phase 1 spec** in Part II (ll. 585–658).
   Other phases by title and §G5.1 row only — unless a finding turns on an ownership claim, in
   which case the four-round precedent lets you read that phase's *Scope* bullets and disclose it.
2. **RESEARCH.md §0** (reading guide, confidence tags) and **§1**, then the spec's Required
   inputs: **§1, §5.1–§5.3, §6.1, §7.2, §12.2**.
3. **Template ground truth**, complete: `build.gradle`, `settings.gradle`, `gradle.properties`,
   `gradle/scripts/*`, `gradle/wrapper/gradle-wrapper.properties`, `src/**`, `.github/workflows/*`,
   `README.md`.
4. **PHASE_1_DOC.md**, §0 through §12.
5. **Last, and only after your own findings are formed:** `PHASE_1_REVIEW_8.md` **including its
   `## Resolutions` section**, plus targeted searches across rounds one to seven. Reading the
   resolutions early makes you an auditor of someone else's reasoning instead of an independent
   reader; §G1.2 exists to prevent exactly that.

Record what you read beyond this list, and why, in your §0.1. Record deliberate omissions as
omissions — round eight did, and it was right to.

## The new surface (line numbers as of the fifth fix-up)

- **§0.8, ll. 328–475** — the fix-up addendum. The largest single block of new prose in the
  document. Five argued design calls, an inputs disclosure, a lesson, a §G1.3 status paragraph.
- **§4.7.4 ll. 1899–1919** — a **new paragraph** on what the drain-elision bit does not bound.
- **§4.7.4 ll. 1950–1962** — the first replay precondition, rewritten.
- **§4.7.4 ll. 1878–1884** — the GL citation, now carrying URLs inline.
- **§5.2 l. 2543** (GL-error row) and **l. 2552** (non-verbs row) — the two §5 rows that changed.
  **l. 2542** carries the per-revision changelog. These three are the reason you exist.
- **§3's first row l. 668** · **§4.7.4 l. 2022** · **§6 l. 2613** · **§7 l. 2662** · **§9 l. 2788**
  · **§12 items 4b (l. 3268) and 22 (l. 3297)**.
- **§4.2.3 ll. 838–852** — the `SeamClasspathArguments` home, rewritten.
- **§11.4 ll. 3144–3152 and 3154–3167** — two new hand-off paragraphs to Phase 7.
- **`[D-P1-30]` l. 3042 · `[D-P1-32]` l. 3044 · `[D-P1-35]` l. 3047** — amended rationales.
- **§0.7 ll. 259–263, 287–291, 304–305** — bold supersession pointers appended to round seven's
  bullets. The round-seven arguments themselves are left intact as a record; check that the
  convention is followed rather than history being rewritten.
- **Housekeeping:** §0 header l. 10 · §0.7 closing l. 325 · closing paragraph ll. 3346–3362.

## Where the fifth fix-up is weakest, stated by the session that wrote it

Not a list of defects — a list of places its author could not fully discharge the burden. Test
each **against the source, not against this paragraph.** If one of these turns out to be fine,
say so; a round that reports only findings misrepresents its own coverage.

1. **§4.2.3's Gradle class-scope claim is untagged and unsourced.** The section asserts that an
   applied `apply from:` script plugin is compiled into its own class scope, so a class declared
   in it is not resolvable by simple name from the applying script. The *file* facts carry
   `[V:template]`; the **mechanism** carries no tag and no source anywhere in the document. This
   is structurally identical to what round seven caught at `[D-P1-30]` — *"the GL semantics claim
   carried no provenance tag and no source anywhere in the document, in a decision written
   specifically to correct an earlier GL error."* V8-5 was a correction about a Gradle mechanism,
   and its fix rests on a Gradle mechanism claim with the same provenance gap. Check whether
   `[V:doc]`/`[V:web]` provenance exists for it, whether the claim is even true of precompiled vs.
   applied script plugins, and whether `ext`-indirection is correctly characterised.
2. **§11.4's foreign-GL hand-off may overstate what it buys.** It tells Phase 7 that one
   unconditional `drainErrors()` at a frame-driver-defined point "absorbs foreign errors at a
   known boundary" for roughly one extra query per frame. But foreign GL is interleaved with
   Phase 6's sweeps *throughout* the frame — a single frame-boundary drain absorbs only what
   precedes it. Whether a per-frame drain meaningfully reduces the misattribution window, or
   merely relocates it, is not argued anywhere. Attack the arithmetic and the claim.
3. **"Stated once" may be stated five times.** V8-1's consequence — that a drain window can hold
   an error the facade did not cause — now appears at §4.7.4, §5.2, §6, `[D-P1-30]` and §11.4.
   §4.7.4 claims to state it "once and here". Check for drift between the five statements, and
   whether the duplication is the kind §4.9.2/§4.7.4 elsewhere treat as a maintenance hazard.
4. **V8-2 was applied wider than round eight's fix shape asked.** Round eight said "retarget to
   Phase 7". The fix-up also named **Phase 6** for the `instanceId` upload, citing `DESIGN.md`
   Phase 6's cadence model (`instanceId` among per-draw dynamics *"at their hooks (Phases 7/9/10
   invoke)"*) and its *Scope — out* (*"the hooks that invoke updates (Phase 7/8)"*). Read both
   yourself. Is Phase 6 the right owner of that upload entry point, is a composite instancing loop
   a "hook" in the sense that bullet means, and does §5.2's non-verbs row now name the right
   consumer set after Phase 5 was removed and Phase 6 added?
5. **V8-3 may have swapped one unsourced claim for another.** The per-sample halflife premise was
   deleted. What replaced it asserts that App D's "refresh" means an *upload* (because the
   redundant-upload skip presupposes a computed value) and that RESEARCH.md §4.4 puts sampling at
   frame start. Verify both readings at source. Also check that §5.2 property (i) now genuinely
   asserts *nothing* about Phase 6's providers rather than asserting the opposite of what it used
   to — the fix-up's own stated standard was "no property in either direction".
6. **`[D-P1-32]`'s rationale now records a reversal of `[D-P1-30]`-era reasoning.** Check the
   decision log reads coherently end to end and that no two decisions now contradict each other.
7. **§0.8's §G1.3 status claims "no service signature was added, removed or changed."** Verify it
   byte-for-byte against what round eight reviewed rather than taking the claim.

## Audit the `Resolutions` table, site by site

Round eight audited round seven's `Where` column site by site and found every claim real; the
fifth fix-up's `Where` column was written expecting the same treatment. Do it. **A site named but
not edited is a finding.** The table also claims three categories worth checking independently:

- **Sites edited beyond what round eight named** — §12 item 22, the closing paragraph, and a stale
  revision reference in §5.2's non-verbs row. Confirm they were real neighbours, not scope creep.
- **Sites deliberately left alone** — `DrawService`'s javadoc (ll. 1784–1795), `[D-P1-33]`
  (l. 3045), §1.2's adjacent-concerns table. The claim is that none names an owner so V8-2 does
  not reach them. Verify.
- **§11.5 unchanged at four items.** The fix-up claims V8-1's residue is a §11.4 hand-off rather
  than an upstream request. Test whether that routing is right — §G2.4's missing rung (§11.5 item
  4) is the precedent for the opposite call.

## Do not re-fight these

Round eight's clean list survived two consecutive audits and re-deriving it is how a round spends
its budget on nothing: `[D-P1-33]`'s central argument, `[D-P1-34]`'s sufficiency, the GL quotation
itself (verified word-for-word at source twice), §3's second row as a legitimate conformance-map
entry, §3.1's flagged-delta ruling, the four-handle/no-renderbuffer model, the `ivec3`/`mat3`
absence, the App F.7 mappings, V6-5's blit narrowing, the Gradle/ASM work, the CI ordering,
§4.2.4a's half-deletion and its agreement with §4.11 item 3, the seventeen §4.1 template rows, the
four OQ spike specs. **V8-7 was examined and ruled a correct hand-off, not a defect** — round eight
declined to fix it deliberately and the fix-up honoured that; do not re-open it without new
evidence.

## Network use — two sanctioned purposes, same as round eight

1. **The pin table.** RESEARCH.md flags Cleanroom's *daily* release cadence, and §4.2.6's
   procedure exists to be run. Round eight checked `repo.cleanroommc.com/.../maven-metadata.xml`
   and `api.github.com/repos/CleanroomMC/Cleanroom/releases` at ~03:52 UTC and confirmed
   `0.6.6-alpha` with no drift. Enough time may have passed that this is now an independent
   sample rather than a same-hour re-confirmation — say which it is.
2. **The cited GL page**, `https://docs.gl/gl4/glGetError` and `https://docs.gl/gl2/glGetError`,
   now that the document carries the URLs inline. Confirm the URLs resolve, the quotation is
   exact, and the "identical wording" claim holds.

No third purpose.

## Hard rules (§G1.2)

No code, no builds, no tests, no scope creep. Adversarial sub-agents are permitted by §G1.2 —
round eight **declined** them, on the reasoning that a brief requiring each load-bearing quote to
be re-derived by the reviewer makes a delegated finding a hop rather than a saving. That reasoning
is available to you, not binding on you; **disclose the choice either way**, as round eight did.
Context discipline per §G1.1. Create exactly one file: `PHASE_1_REVIEW_9.md`. Then stop — do not
fix anything you find.

## Deliverable

`Schmaloogium/PHASE_1_REVIEW_9.md`, following rounds seven and eight's established shape:

1. **§0** — what you read and in what order; reads beyond the assigned list with the finding each
   turned on; deviations recorded as deviations; network use.
2. **§1 — Findings**, each with location, claim under test, evidence, severity, and an explicit
   **touches §5: yes/no** line. That line decides whether a tenth verify session is required.
3. **§2 — What was checked and came back clean.** Named, because a round reporting only findings
   misrepresents its coverage.
4. **§3 — Verdict**: PASS, PASS-WITH-CORRECTIONS, or FAIL. Reserve FAIL for structural misses
   requiring a rebuild. Include the per-finding §5 disposition table and the **§G1.3 line**
   stating whether §5 changed and what the next step in the cadence is.

If your verdict is **PASS** — no corrections, or none touching §5 — say so plainly, because that
is the verdict that finally closes Phase 1 and unblocks Phase 2 and Phase 3. Nine rounds of
PASS-WITH-CORRECTIONS is not evidence that a tenth is owed; it is evidence about how much new
prose each round introduced. Judge the document in front of you.

---
One deliberate choice, in case you want to adjust it: the "where the fix-up is weakest" section
names *claims to test*, never verdicts to reach. That is on purpose — round eight's meta-finding
is that adopted reasoning is how defects propagate, and a brief that pre-answers its own questions
would reproduce exactly the failure it is warning against. If any item there turns out to be fine
on derivation, the right output is to say so in §2, not to manufacture a finding to match the
prompt.
