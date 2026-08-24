# Phase 13 verification review — round 1

## 0. Method and reading order

Sole adjudicator for round 1 of target `phase-13` (manifest `verification/targets/phase-13.json`,
governing design revision `docs/design/v3/DESIGN.md`).

Reading order actually followed:

1. Target `docs/phase13/v1/PHASE_13_DOC.md` — the manifest-resolved whole document (1–1436), with
   line-resolved reads of §3.2–§3.4 (321–352), §4.1.3–§4.1.5 (477–538), and the declared
   interface/change-trigger region §5 (1007–1102).
2. Authority: `docs/design/v3/DESIGN.md` §G1.3 (354–359), §G9 mandatory template (817–855),
   §G12.4 C-TX01 (1088), Phase 13 spec and Doc gate (2436–2513).
3. Contract ground truth: `docs/research/v1/RESEARCH.md` at the manifest-selected selectors,
   notably §4.6 texture-system behavior (583–600) and App B.3 (1228–1271).
4. Dependency binding regions: Phase 3 (1420–1689), Phase 5 (1996–2096), Phase 7 (1368–2153).
5. Supporting evidence: `docs/phase3/reviews/PHASE_3_REVIEW_34.md`,
   `docs/phase3/reviews/PHASE_3_REVIEW_36.md`, `docs/phase5/reviews/PHASE_5_REVIEW_38.md`,
   `docs/phase7/reviews/PHASE_7_REVIEW_36.md`, and the Pintonium evidence map selectors.
6. Prior Phase 13 reviews: none exist. Nothing has been previously cleared for this target, so no
   candidate was disposed against settled material.

Deviations: none. No network use. No agent fan-out: this session dispatched no subagents, ran no
verification scripts, and started no nested loop; the candidate set arrived pre-refuted and
pre-gated. No forbidden source (`docs/**/chatlogs/**`, `*.txt`, or any prior agent/session
transcript) was read. Note that `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` is listed as
supporting evidence but matches the `*.txt` forbidden path pattern; I resolved the conflict
conservatively by not opening it, and no admitted finding depends on it.

Gate drops: the Gate list is empty. Three candidates (candidate-005, candidate-006, candidate-007)
were eliminated earlier at the Refute stage; they are discussed in §2 and are not findings.

## 1. Findings

### Finding 1 (candidate-001) — §5.2 declares Phase 3 verified from round 34, but the latest Phase 3 review is round 36 (PASS-WITH-CORRECTIONS, interface changed)

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:1028` (§5.2 Phase 3 sub-table header), reinforced
  at §3.6 item 3 (:403–:409), `D-P13-13` (:1298–:1300), and the §11 closing statement (:1432–:1433).
- **Claim:** Phase 3 may be consumed as a verified dependency on the strength of
  `PHASE_3_REVIEW_34.md`'s literal PASS.
- **Evidence:** the target reads `#### Phase 3 — verified (docs/phase3/reviews/PHASE_3_REVIEW_34.md:46–:47, literal PASS)`.
  The latest Phase 3 review is round 36, whose verdict block reads `# PASS-WITH-CORRECTIONS` /
  `Counts: blocking=0; corrections=6; notes=0` / `Interface changed: yes`
  (`docs/phase3/reviews/PHASE_3_REVIEW_36.md:270`–`:272`), and which states at `:290`–`:292` that
  because every admitted repair changes the §5 region or its declared dependency surface, a fresh
  whole-document round is required "before Phase 3 can close or be consumed by a dependent". One
  ordered correction is the texture-key suffix disposition (`:275`), which lands on exactly the
  Phase 3 surface §4.3.1/§3.6 item 1 consumes. DESIGN §G1.3 (`docs/design/v3/DESIGN.md:354`–`:359`)
  keys "verified" to the *latest* review verdict and forecloses the PASS-WITH-CORRECTIONS route when
  §5 changed — the same rule the target itself invokes at `D-P13-13`. No passage in the target
  discloses any Phase 3 provisionality; §5.4, §0.2 item 1 and `D-P13-2` scope provisionality to
  Phase 7 only.
- **Severity:** correction. The dependency-state claim is false under the target's own governing
  rule and is consumer-visible, but no §4 structure collapses; the repair is a re-anchoring plus an
  extension of the existing §5.4-style disclosure, not a rebuild.
- **Touches interface/change-trigger region: yes.** The ordered edit rewrites the §5.2 sub-table
  header and adds a §5.4 disclosure entry, both inside the declared region 1007–1102.
- **Fix:** re-derive Phase 3's dependency state from `PHASE_3_REVIEW_36.md`; mark the §5.2 Phase 3
  sub-table as consumed provisionally pending Phase 3's required fresh round, correct §3.6 item 3,
  `D-P13-13` and the §11 closing sentence, and add a narrow Phase 3 entry to §5.4 naming the
  texture-key-suffix correction against §4.3.1/§3.6 item 1 and the risk that cited Phase 3 line
  anchors moved in the fix-up.

### Finding 2 (candidate-002) — the Phase 7 dependency-state anchor and the "round 33" conditional are stale

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:1055` (§5.2 Phase 7 header), §5.4 (:1078–:1091),
  origin at §0.2 item 1 (:47–:63), and the §11 closing sentence (:1433–:1435).
- **Claim:** Phase 7's dependency state is fixed by round 32's PASS-WITH-CORRECTIONS, and a §G1.3
  fix-up is owed here "if round 33 contradicts a row".
- **Evidence:** the target anchors solely to `PHASE_7_REVIEW_32.md:299`–`:301`. Phase 7's latest
  review is round 36, whose verdict block reads `# PASS` / `Counts: blocking=0; corrections=0;
  notes=2` / `Interface changed: no` (`docs/phase7/reviews/PHASE_7_REVIEW_36.md:107`–`:109`), with
  the interface region `docs/phase7/v1/PHASE_7_DOC.md:1368`–`:2153` certified unchanged (`:113`–`:114`).
  Rounds 33–36 have all run, so the conditional framed on a future "round 33" cannot fire as
  written. No passage anywhere in the target contemplates a Phase 7 round after 32.
- **Severity:** correction. I decline both the finder's stronger reading and the pure-note
  downgrade. Wholly deleting the provisional posture would be wrong — Phase 7 v1 still self-declares
  unverified pending a version roll, and the orchestrator's own dependency entry still labels it
  provisional — but the round anchor and the dead "round 33" conditional are stated as live facts in
  four places and are simply false, which leaves a reader unable to tell whether a fix-up is owed.
  That is more than a citation nicety, and less than a structural defect.
- **Touches interface/change-trigger region: yes.** The ordered edit rewrites §5.2's Phase 7 header
  and §5.4's third bullet, inside the declared region 1007–1102.
- **Fix:** re-anchor the Phase 7 dependency-state citation to `PHASE_7_REVIEW_36.md:107`–`:109`
  (literal PASS, `Interface changed: no`) in §5.2 and §0.2 item 1, and replace the "if round 33
  contradicts a row" conditional in §5.4 and §11 with the actual state — rounds 33–36 ran, round 36
  passed with no interface change, so no §G1.3 fix-up is owed on that trigger and the residual
  trigger is a future Phase 7 version roll. Keep a reduced provisional marking. Do **not** order
  re-verification of the six consumed rows: round 36 certifies the region unchanged.

### Finding 3 (candidate-004) — §4.1.4's stated byte order does not decode to the flat normal the same table cell and row T-3 name

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:507` and `:510`–`:512` (§4.1.4), asserted as
  conformant by §3.3 row T-3 (`:338`).
- **Claim:** §4.1.4 satisfies RESEARCH §4.6's "missing sprites → flat-normal `0xFF7F7FFF`" row
  (`docs/research/v1/RESEARCH.md:594`–`:595`).
- **Evidence:** line 507 labels `0xFF7F7FFF` "flat normal", while lines 510–512 fix the uploader's
  written component order as `(0xFF, 0x7F, 0x7F, 0xFF)` — R=0xFF, G=0x7F, B=0x7F — which decodes to
  a normal along +X, not the flat (0,0,1) normal the adjacent cell and the contract row name. The
  document therefore contradicts itself in two adjacent lines, and an implementer following §4.1.4
  literally writes a fill that the same subsection calls something it is not.
- **Severity:** correction. I considered and rejected the note downgrade: §3.3 T-3, §3.6 item 4,
  `D-P13-5` and §11.3 item 3 record only that *byte order is unresolved* per C-TX01; none observes
  that the specific order chosen is inconsistent with the word "flat normal" the doc itself uses.
  The defect is falsifiable from the contract text today, not only by a future T2 pixel-parity run,
  so leaving it to the test is not adequate coverage. It remains correction-sized because the
  assumption is deliberately localized to one `[A]`-tagged statement with a dedicated test.
- **Touches interface/change-trigger region: no.** §3.3 and §4.1.4 lie outside 1007–1102 and the
  ordered edit changes nothing inside it.
- **Fix:** reconcile the two statements in §4.1.4 in one place. Either drop the word "flat normal"
  from line 507 and state plainly that the literal MSB-first RGBA reading does not decode to
  (0,0,1) — recording that tension as the exact thing `companion_missingNormalUsesContractDefault`
  and C-TX01 escalation exist to resolve — or state the assumed packing under which `0xFF7F7FFF`
  *is* a flat normal. Do not perform an unannounced re-encoding: §G12.4's C-TX01
  (`docs/design/v3/DESIGN.md:1088`) forbids silently swapping the representation, so whichever
  reading is chosen must be visible, `[A]`-tagged, and single-place as it is today.

### Note 1 (candidate-003) — `supersede` appears in the state machine and thread table but in no exposed interface

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md` §2.2 (:189–:198), §4.5.3 (:892–:895), §7.1
  (:1152), §5.1 (:1011–:1022).
- **Claim under test:** that §5.1 leaves Phase 7 unable to drive the publication lifecycle without
  guessing an unexposed call.
- **Re-derivation:** the stronger framing is refuted by the document. §5.1's entry-point row already
  characterizes the surface as "pure planning, render-thread build, atomic publication, idempotent
  close" (`:1013`); §4.5.3 says a build *failure* "leaves the previous `READY` publication installed"
  (`:899`–`:900`), which presupposes that a build success installs; §6 repeats this (`:1125`);
  §4.7's replan row places the keep-or-replace decision inside Phase 13 (`:948`–`:949`); and §7.1
  groups `supersede` with "lease counting", plainly internal bookkeeping. Phase 7's driving sequence
  is therefore plan → build → lease/publicationId → close, exactly the exposed surface. What
  survives is only that no sentence says so outright.
- **Severity:** note. Not ordered for fix-up.
- **Touches interface/change-trigger region: no.** No change to §5.1's table is warranted.
- **Suggested (unordered) improvement:** one clarifying sentence in §4.5.3, and optionally a
  parenthetical in §7.1's row, stating that `supersede` is an internal consequence of a successful
  `build` atomically installing the new publication, never a consumer call.

## 2. Checked and clean

**Finder clean areas accepted on re-derivation.**

- *Interfaces.* The declared change-trigger region 1007–1102 does cover all of §5.1–§5.5, so
  requests, ungranted fallbacks, and downstream hand-offs sit inside the trigger. The consumed
  Phase 5 contracts — overlay lease/publication-id pairing, rejection reasons, the closed
  `TextureOverlayKey`/`TextureOverlayAbsence` domain, the unit-15 no-substitution rule, the
  `ColorInternalFormat`/`PixelFormat`/`PixelType` raw-upload vocabulary, and the resize
  consumer/reasons — match Phase 5's binding region without widening, and §4.3.6 with §5.3 R2 flags
  the key-domain shortfall rather than inventing a wider interface. The consumed Phase 7 rows are
  used as-is. Promises to Phase 5/6/3 consumers (`OverlayTable` totality and absence reasons,
  `AtlasSizeResult` with its validity window, `CompanionMacroState`, `BufferResizeConsumer`
  per-reason behavior) are implementable without guessing. Findings 1 and 2 concern the accuracy of
  §5.2's dependency-state anchors, not the shape of any consumed or exposed contract.
- *Conformance.* Every in-scope item the Phase 13 spec names — three custom-texture source forms,
  `.mcmeta`, stage expansion, companion atlases with their lifecycle and animation, the noise
  generator and override, `atlasSize`, `MC_NORMAL_MAP`/`MC_SPECULAR_MAP` wiring, and App E rows
  10–11 — carries at least one mapped conformance row, and I found no unmapped in-scope contract
  row. Spot checks of B3-5, B3-6, T-1, T-2, T-4 through T-7, D-1 and M-2 resolved to their cited
  lines and supported their claims. Only T-3's design element is defective, at Finding 3.
- *Doc gate.* All thirteen §G9 template sections (0–12) are present, in order, and substantive;
  none is an empty named heading. Section 0 supplies phase name, date, inputs read with paths and
  portions, dependency docs consumed, and an explicit deviations subsection with reasons. Section 3
  is a mapped conformance table with provenance tags. Each literal Doc-gate criterion in the Phase
  13 spec resolves to a locatable, non-trivial passage. Phase 13 is assigned no open question, and
  §10 records that absence with its citation rather than being left blank, so the
  question/procedure/success-failure/fallback structure is correctly untriggered.

**Candidates cleared or narrowed on re-derivation.** candidate-003's asserted interface gap is
false; only a clarity residue survives, recorded above as an unordered note. candidate-002's
recommended fix was narrowed: I rejected dropping the provisional posture outright and rejected
re-verifying the six consumed Phase 7 rows, since round 36 certifies that region unchanged.
candidate-004's recommended fix was narrowed to forbid the unannounced ARGB re-encoding, which
C-TX01 prohibits.

**Eliminated before adjudication (settled; not findings).** candidate-005 (row F3-1's RESEARCH
citation allegedly not supporting the "default 256" half), candidate-006 (row F5-9's cited line and
the 1/2/3/2 dimension arity), and candidate-007 (§4.2.1's REPEAT/LINEAR noise parameters attributed
to a RESEARCH range that does not state them) were all dropped at the Refute stage by strict
refuting majority or for no live severity. They were excluded for those stated pre-adjudication
reasons and were not revived or used as findings here.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=1
Interface changed: yes

Zero blocking findings. Three admitted corrections and one unordered note. No structural miss was
found: the §G9 skeleton, the conformance map, and the §4 design elements are sound, and every
repair is a scoped edit rather than a rebuild, so FAIL is not warranted. PASS is unavailable because
three corrections stand.

Interface disposition: Findings 1 and 2 each order edits inside the declared change-trigger region
`docs/phase13/v1/PHASE_13_DOC.md:1007`–`:1102` (§5.2 dependency-state headers and §5.4's
disclosure), so `Interface changed: yes`. Finding 3 and Note 1 order no edit inside that region.

Trend/convergence: this is round one; the supplied prior-round trend is empty and no prior Phase 13
review exists on disk, so no convergence claim is available and nothing has been previously cleared.

Next required action: a scoped §G1.3 fix-up resolving all three corrections — re-derive Phase 3's
dependency state from `PHASE_3_REVIEW_36.md` and disclose it provisionally, re-anchor Phase 7's
state to `PHASE_7_REVIEW_36.md` and retire the dead "round 33" conditional, and reconcile §4.1.4's
byte-order statement with the "flat normal" label it carries. Because the first two repairs change
the declared interface/change-trigger region, a fresh whole-document verification round is required
before Phase 13 can close.


## Resolutions

All three admitted corrections were re-derived independently against the cited authorities and
applied. Target edits landed in `docs/phase13/v1/PHASE_13_DOC.md` only; this review is otherwise
unchanged.

### Finding 1 (candidate-001) — applied

Re-derived: `docs/phase3/reviews/PHASE_3_REVIEW_36.md:270`–`:272` is the latest Phase 3 verdict and
reads `PASS-WITH-CORRECTIONS` / `blocking=0; corrections=6; notes=0` / `Interface changed: yes`, and
`:290`–`:292` requires a fresh whole-document round "before Phase 3 can close or be consumed by a
dependent". Under DESIGN §G1.3 Phase 3 is therefore not verified. Applied: §5.2's Phase 3 sub-table
header now reads "not verified; consumed provisionally" anchored to round 36; §3.6 item 3 was
rewritten to keep Phase 5's literal-PASS derivation while re-deriving Phase 3's state from round 36;
`D-P13-13` was restated accordingly; §5.4 gained a Phase 3 exposure bullet naming the ordered
texture-key suffix disposition (`PHASE_3_REVIEW_36.md:275`) against §4.3.1/§3.6 item 1 and the risk
that the round-36 fix-up moved cited Phase 3 line anchors; the §11 closing sentence was corrected.
Sweep beyond cited sites: §5.3 R1/R4 target Phase 3 but assert no verified state, and §0.1's
dependency-consumed sentence names portions only, so neither needed editing.

### Finding 2 (candidate-002) — applied as narrowed

Re-derived: `docs/phase7/reviews/PHASE_7_REVIEW_36.md:107`–`:109` reads `PASS` / `corrections=0` /
`Interface changed: no`, with `:113`–`:114` certifying `docs/phase7/v1/PHASE_7_DOC.md:1368`–`:2153`
unchanged. Applied: §5.2's Phase 7 header re-anchored to round 36 while retaining a reduced
provisional marking justified solely by Phase 7 v1's own unverified self-declaration; §0.2 item 1
keeps round 32 as the historical build-time state and adds the rounds 33–36 outcome; §5.4's third
bullet and the §11 closing sentence replace the dead "if round 33 contradicts a row" conditional
with the actual state and name a future Phase 7 version roll as the residual trigger. Consistent
with the finding, no re-verification of the six consumed Phase 7 rows was ordered or performed.
§5.4's heading and lead sentence were widened to cover both phases, and its two surviving Phase 7
bullets were labelled so the section is unambiguous after the Phase 3 bullet was added.

### Finding 3 (candidate-004) — applied

Re-derived: `0xFF7F7FFF` written MSB-first is R=0xFF, G=0x7F, B=0x7F, which is a normal along +X,
not (0,0,1). Applied in one place, §4.1.4: the table cell no longer calls the value a "flat normal"
(it now says "the contract default for a missing normal sprite — the literal value at
`docs/research/v1/RESEARCH.md:595`"), and the paragraph states plainly that the literal reading does
not decode to (0,0,1), naming `companion_missingNormalUsesContractDefault` and C-TX01 escalation as
the resolution route. No re-encoding was performed and RESEARCH's literal value is untouched, per
§G12.4 C-TX01 (`docs/design/v3/DESIGN.md:1088`). §3.6 item 4, `D-P13-5`, §3.3 row T-3 and §11.3
item 3 were re-read: each speaks of the unresolved byte order or of using RESEARCH's literal value,
and none repeats the defective "flat normal" identification, so they were left unchanged.

### Notes deferred

- **Note 1 (candidate-003).** Not applied. The adjudication records it as unordered, and the only
  residue is clarity, not an underdetermined contract; adding prose about `supersede` would enlarge
  next round's unreviewed surface for no contract gain. Re-derivation confirms §4.5.3, §6, §4.7 and
  §7.1 already make `supersede` internal.

### Interface disposition of this fix-up

The declared change-trigger region `docs/phase13/v1/PHASE_13_DOC.md:1007`–`:1102` (§5) was
intentionally edited: the Phase 3 and Phase 7 sub-table headers in §5.2, and §5.4's heading, lead
sentence, bullet labels and bullet set. §5.1, §5.3 and §5.5 are unchanged, and no consumed or
exposed contract shape was altered — only dependency-state accuracy and disclosure. A fresh
whole-document round is therefore required before Phase 13 can close.

### Target addendum

A single new `§0.4 Fix-up addendum — review round 1` (9 content lines) was added in the document's
existing §0 subsection style. No supersession sentences, dated-claim ranges or session-count
bookkeeping were imported, and the v1 directory was not rolled.

No refusal was necessary.