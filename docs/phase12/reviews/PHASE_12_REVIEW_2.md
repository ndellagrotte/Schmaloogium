# Phase 12 verification review — round 2

## 0. Method and reading order

Target: `docs/phase12/v1/PHASE_12_DOC.md` (whole document, ll. 1–1761).
Authorities: `docs/design/v3/DESIGN.md` (Part I, Phase 12 target spec ll. 2357–2435, doc gate
ll. 2423–2428, mandatory template ll. 817–855) and `docs/research/v1/RESEARCH.md` (manifest
selectors, incl. §4.7 ll. 601–618). Dependencies: `docs/phase1/v14/PHASE_1_DOC.md` §5
(ll. 4175–4283) and `docs/phase3/v1/PHASE_3_DOC.md` §5 (ll. 1420–1689). Supporting evidence as
listed in the manifest.

Reading order followed the instruction: every surviving candidate was re-derived independently
against the target, the authorities, the dependency binding regions and the evidence *before*
`docs/phase12/reviews/PHASE_12_REVIEW_1.md` was opened. Round 1's eight findings all appear
discharged in the current revision (§0.2 now records Phase 3 as **not verified** with round 35/36
state and ⟳-marked provisional rows; the schema gate is symbolic with `4`; Phase 3 locators are
individually re-resolved; `PackSelectionActions` is declared and published as a closed five-intent
set; §4.6.3 binds the wire spellings). Nothing previously cleared constrains this round adversely.

Independent re-resolutions performed this round: P1 ll. 4198–4204 (row-by-row), RESEARCH §4.7
ll. 601–618, DESIGN Phase 12 Scope-in ll. 2364–2404 and the doc gate ll. 2423–2428, P3 ll. 938–946,
1437, 1575–1604, and the target's §4.6.3, §4.7.2–§4.7.4, §5.1–§5.2b and §8.1.

Deviations: none. No network use. No agent fan-out (single adjudicator; no subagents spawned, no
verify script, no nested session). No forbidden source opened: no chatlog directory and no `*.txt`
file was read, and no prior agent or session transcript was consulted.

Gate drops: none reported. One candidate (candidate-003, the §0/§0.1 spec-extent range) was
eliminated at Refute before adjudication; it is discussed in §2 and carries no disposition.

## 1. Findings

### F-1 (candidate-001) — §4.7.3's two `FULL` rows contradict the [D-P12-11] rule and the §8.1 test — correction

Location: `docs/phase12/v1/PHASE_12_DOC.md` ll. 904–905 (matrix), ll. 936–937 (honesty note),
l. 1407 (§8.1).

Claim under test: the trigger × lifecycle matrix is internally consistent with the bake-set
predicate prose and the named tests.

Evidence, re-derived: ll. 904–905 give `worldRendererReload = "if bake-set changed"` for the F3+R
keybind and `/reloadShaders`, both classified `FULL`; ll. 936–937 state that "on a **`FULL`** reload
the flag is unconditional"; l. 907 — the third `FULL` row — already sets **yes**; l. 919 scopes the
predicate explicitly to `REPUBLISH` ("fires on a `REPUBLISH` only when"), so its appearance in the
two `FULL` rows has no defined meaning. §8.1 names both `reload_triggerMatrixIsTotal` (table-driven
over every §4.7.3 row) and `reload_fullAlwaysSetsWorldRendererReload`; an implementer following the
table as written fails the latter. Ledger row `D-P12-11` (l. 1577) cross-references only §4.7.3 and
adds no `FULL` carve-out, so nothing anywhere reconciles the two readings.

Severity: correction. The intended rule is unambiguously recoverable and the repair is two table
cells, but a machine-checkable self-contradiction between a matrix and a named test is more than a
note.

Touches interface/change-trigger region: **no** — §4.7.3 lies outside ll. 1124–1280 and the fix
edits no §5 row.

Fix: set the `worldRendererReload` cell to **yes** for the `F3+R keybind` and `/reloadShaders` rows,
matching l. 907 and the honesty note.

### F-2 (candidate-005) — the persisted pack selection is written but its key, tokens and durable identity are unpublished — correction

Location: `docs/phase12/v1/PHASE_12_DOC.md` ll. 816–817 (§4.6.3), l. 871 (§4.7.2), l. 1134 (§5.1),
against `docs/phase3/v1/PHASE_3_DOC.md` ll. 941, 1437, 1579–1585.

Claim under test: the engine-settings/global-file contract Phase 12 exposes is complete enough to
implement without guessing.

Evidence, re-derived: §4.6.3 l. 817 asserts "the pack selection itself is persisted in the same
global file" and §4.7.2 l. 871 makes it a Phase-12-owned write, yet the bound wire table at
ll. 824–832 covers exactly the seven engine-setting keys, and the §5.1 row at l. 1134 promises wire
text for those seven only. Phase 3 does not close the gap: its global format is declared to carry
"engine-global settings only" (P3 l. 941) with `EngineOptionData` an opaque decoded-string map, and
it exposes only the codec mechanics to Phase 12 (P3 l. 1437). Phase 3's binding §5 region further
makes `PackCandidateId` valid only against the latest discovery generation and explicitly assigns
**Phase 7** the job of "obtain[ing] a current result before loading a persisted filesystem
selection" (P3 ll. 1579–1585). So the reader Phase 3 names cannot implement restore: no key name, no
`Off`/`Internal` tokens, no durable identity for a filesystem pack, no absent-key meaning, no
re-resolution rule. The target's own ownership argument at ll. 819–822 — Phase 3's map is opaque,
therefore Phase 12 binds key names and token text — applies to this entry and is not carried out.

Severity: correction. A bounded missing binding in a published contract, not a contradiction
requiring redesign.

Touches interface/change-trigger region: **yes** — the ordered fix adds/changes a bound row in the
§5.1 exposed-interface table inside ll. 1124–1280.

Fix: bind the persisted selection in §4.6.3's table and the matching §5.1 row (key name, exact
`Off`/`Internal` tokens, durable non-`PackCandidateId` identity for a filesystem pack, absent-key
meaning, and the rule that a reader re-runs `discover` and matches that identity to obtain a current
`PackCandidateId`); or, if the durable identity must originate in Phase 3, file it as a §5.4 request
with a stated interim assumption instead of asserting an unpublished write.

### F-3 (candidate-006) — "write-through on change" is quoted to a RESEARCH line that does not contain it — correction

Location: `docs/phase12/v1/PHASE_12_DOC.md` ll. 728–730 (§4.5.3).

Claim under test: RESEARCH §4.7 l. 604 states "write-through on change".

Evidence, re-derived: RESEARCH §4.7 ll. 603–605 read "applied by *rewriting source lines at compile
time*; only changed options persist to per-pack `shaderpacks/<pack>.txt`; global engine settings in
`optionsshaders.txt`" — the quoted phrase occurs nowhere in the section. It is a DESIGN Phase 12
Scope-in requirement at `docs/design/v3/DESIGN.md` l. 2381. The document's central justification for
"there is **no** write-on-every-click" therefore rests on a quotation attributed to the wrong (and
weaker) authority; a Gate re-resolving evidence would drop the cite.

The candidate's second half — that the Scope-in requirement is an unmapped conformance row — does
not survive: the Phase 12 doc gate (DESIGN ll. 2423–2428) scopes map completeness to App F.3/F.4
constructs, and §4.7.2's write-trigger table plus l. 873's "Never on hover, navigation, or discard"
already discharge the timing requirement substantively. Only the misattribution is admitted.

Severity: correction.

Touches interface/change-trigger region: **no** — §4.5.3 is outside ll. 1124–1280.

Fix: re-attribute the phrase in §4.5.3 to `docs/design/v3/DESIGN.md` l. 2381 (Phase 12 Scope-in,
"Persistence round-trip"), optionally cross-referencing §4.7.2. No new conformance row is required.

### F-4 (candidate-002) — §2.1's C-1…C-4 locator is one line short — note

Location: `docs/phase12/v1/PHASE_12_DOC.md` l. 224.

Re-derived: P1 l. 4199 is the "seam constraints C-1 … C-4" row and l. 4200 is the package-placement/
`.internal` row. §2.1 l. 224 names *both* rules but cites only l. 4200. The candidate's framing of a
three-way inconsistency fails: §5.1 l. 1139 cites l. 4199 for C-1…C-4 alone (correct) and §5.2b
l. 1174 cites ll. 4199–4200 for the two rules jointly (correct), so the target already carries the
right joint locator and nothing substantive is misstated.

Severity: note — an adjacent-line precision slip, immediately recoverable from §5.2b.

Touches interface/change-trigger region: **no** — the only defective citation is at l. 224, outside
ll. 1124–1280; the two in-region citations are correct and are not edited.

Fix (not ordered): widen l. 224 to `P1 §5.1 ll. 4199–4200`.

### F-5 (candidate-004) — the `ReloadCoordinator` row does not name the pending-slot holder or the `merge`-on-`submit` duty — note

Location: `docs/phase12/v1/PHASE_12_DOC.md` l. 1133 (§5.1), with §4.7.4 ll. 947, 963–974 and
§11.4 ll. 1638–1639.

Re-derived: the surviving gap is that no sentence states *who* holds the single-slot pending request
or that `submit` folds an undrained request into it via `ReloadRequest.merge`. But the obligation is
recoverable rather than absent: RS-1, which the §5.1 row names and thereby incorporates, says in
terms that "a burst of GUI activity must not produce a burst of invalidations" (l. 965–966) — that
*is* the N-submits-to-one-reload duty, so an implementation draining once per submit violates RS-1
rather than satisfying it. §4.7.4 l. 971 splits ownership (Phase 12 owns the merge algebra, Phase 7
owns the drain point), `[D-P12-17]` (l. 1583) fixes the mechanism as a single-slot merge, and §11.4
ll. 1638–1639 routes Phase 7 to §4.7.4 rather than to the row alone — refuting the premise that the
row is the only text Phase 7 must read. Since Phase 12 never executes a reload and the drain point
is Phase 7's, the slot's location behind `submit` is determined, not guessed.

Severity: note — a clarity gap in an otherwise implementable seam.

Touches interface/change-trigger region: **yes** — the (unordered) clarification lands in the §5.1
row at l. 1133 inside ll. 1124–1280. Notes are not ordered for fix-up.

Fix (not ordered): append to the §5.1 row that `submit` merges an undrained request into a single
pending slot via `ReloadRequest.merge` and each drain consumes and clears that slot (`[D-P12-17]`,
§4.7.4).

## 2. Checked and clean

I accept the three finder lenses' clean areas after spot-verification:

- **New-surface consistency.** Entry-kind counts (§2.2's variants vs §5.1's "5 variants + `Blank`"
  vs §8.1's `allFiveKindsPlusEmpty`), the five `PackSelectionActions` intents against
  `selection_actionSurfaceIsExactlyTheFivePublishedIntents`, the column boundary numbers
  (18→2, 19→3, 27→3, 28→4) across §3.1 F4-13, §3.4, §4.3.4 and §8.1, the schema constant `4` across
  §4.1/§5.2/§8.1, and the decision-ID set `D-P12-1…19` (all cited IDs defined, all defined IDs
  referenced) are consistent. Round 1's F-2, F-4 and F-5 are visibly discharged here.
- **Interfaces.** I re-resolved the Phase 3 §5 locators cited by §5.2 (ll. 1429–1438, 1575–1584,
  1587–1591, 1601–1604, 1614–1624, 1506, 1535) and each matched the current binding region; the
  ⟳ marking of round-36-touched rows and the honest "not verified" framing in §0.2/§5.2 discharge
  round 1's F-1 and F-3. §5.2b's Phase 1 rows match P1 §5.1/§5.3 as cited (with the single l. 224
  slip noted above). §5.3(B)'s Phase 7 assumptions are flagged, not assumed, and §5.4's four
  requests are honestly scoped.
- **Conformance.** App F.3/F.4 row citations resolve to the claimed RESEARCH lines (incl. the
  sliders row l. 1470, the `*` row l. 1479, the columns/auto-widen row l. 1480); the column formula
  and its counting rule match P3 ll. 950–955; C-2…C-17's §4.7/§4.8/App E.2/§9 offsets resolve; the
  reload matrix is closed (incl. the out-of-scope dimension-switch row) and the Pintonium
  do-not-inherit dispositions are contract-checked rather than inherited.
- **Doc gate.** All 13 mandatory template sections are present and substantive; `*`, `<empty>`,
  red-`!` and auto-widen are all mapped; §4.7.3 is total; §10 gives OQ-9 a full spike with fallback.

Candidates reclassified on my re-derivation: **candidate-002** was reduced from correction to note
(two of its three cited locators are correct; only l. 224 is under-inclusive, outside the interface
region), and **candidate-004** was reduced from correction to note (RS-1's own wording carries the
burst-coalescing obligation, and §11.4 routes Phase 7 to §4.7.4, so the seam is implementable). For
**candidate-006** the misattribution half is admitted and the "unmapped conformance row" half is
rejected against the doc gate's actual scope.

Eliminated before adjudication (settled, no disposition returned): **candidate-003**, alleging two
different Phase 12 spec extents in §0 and §0.1, fell to a strict refuting majority with no live
severity.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=2
Interface changed: yes

Interface disposition: of the admitted findings, F-2 (correction) orders an edit inside the declared
change-trigger region `cross-phase-interfaces` (`docs/phase12/v1/PHASE_12_DOC.md` ll. 1124–1280) and
F-5 (note, not ordered) would land there too, so the flag derives as `yes`. F-1, F-3 and F-4 order
no edit to that region.

Trend/convergence: round 1 was `FAIL` with 1 blocking, 5 corrections and 2 notes; this round is
`PASS-WITH-CORRECTIONS` with 0 blocking and 3 corrections, and every round-1 finding is discharged
in the current text. The loop is converging; the remaining defects are all localized repairs, none
requiring rebuild.

Next required action: a scoped fix-up session applying F-1 (two matrix cells), F-2 (bind the
persisted selection in §4.6.3 and §5.1, or convert it to a §5.4 request) and F-3 (re-attribute the
"write-through on change" quotation to DESIGN l. 2381). Because F-2 changes the monitored §5 region,
a fresh whole-document verification round is required before Phase 12 can close, and the Phase 3
consumption cannot be finally certified until Phase 3 itself obtains a literal `PASS`.


## Resolutions

All three admitted corrections were re-derived independently against the target, DESIGN v3,
RESEARCH v1 and the Phase 3 binding region before being applied. Only
`docs/phase12/v1/PHASE_12_DOC.md` was edited; a compact `§0.6 Round-2 fix-up` records the three
edits in the document's own header style.

**F-1 — applied.** Re-derived: the matrix's third `FULL` row (pack-selection change) already set
`worldRendererReload` = yes, the honesty note below the matrix makes the flag unconditional on
`FULL`, and the `[D-P12-11]` predicate is scoped in terms to `REPUBLISH`, so "if bake-set changed"
in the two remaining `FULL` rows was undefined and contradicted
`reload_fullAlwaysSetsWorldRendererReload` (§8.1). The `F3+R keybind` and `/reloadShaders` cells now
read **yes**. Swept the rest of the matrix: the surviving "if bake-set changed" cells are all on
`REPUBLISH` rows, where the predicate is defined; `NONE` rows are `no`. No other site restates the
two cells.

**F-2 — applied, in the binding form rather than as a §5.4 request.** Re-derived: §4.7.2 makes the
global write carry "current pack selection", §4.6.3 asserted the same, yet the bound table covered
only the seven engine-setting keys, and P3 §5.1 ll. 1579–1585 makes `PackCandidateId` valid only
against the latest discovery generation. The binding form was chosen because §4.6.3's own published
ownership argument — Phase 3 stores opaque decoded strings, so Phase 12 owns key names and token
text — already covers this entry, so no Phase 3 interface change is needed and no new design
decision is imported: the sanitized display name is the only durable, path-free, per-candidate value
Phase 3 publishes, and the re-resolution duty is P3's own sentence assigning Phase 7 the job of
obtaining a current discovery result before loading a persisted filesystem selection. Added: a
`shaderPack` block in §4.6.3 (tokens `off` \| `(internal)` \| sanitized display name byte-for-byte;
absent ⇒ `off`; exact case-sensitive match; unmatched or non-`AVAILABLE` ⇒ `off` with a
`schmaloogium.config` warning; ties broken by Phase 3's deterministic order) and a matching §5.1
row consumed by Phase 7.

*Interface disclosure:* this is an intentional edit inside the declared change-trigger region
`cross-phase-interfaces` (`docs/phase12/v1/PHASE_12_DOC.md` §5). One row was **added** to the §5.1
exposed-interface table; no existing §5 row was altered, deleted or reordered. The declared trigger
therefore fires and a fresh whole-document verification round is required before Phase 12 can close.

**F-3 — applied.** Re-derived at the source: RESEARCH §4.7 ll. 603–605 contain no such phrase, and
`docs/design/v3/DESIGN.md` l. 2381 does. §4.5.3 now attributes "write-through on change" to the
DESIGN Phase 12 Scope-in "Persistence round-trip" requirement and cross-references §4.7.2. Swept the
document for other uses of the phrase: this was the only one. The RESEARCH §4.7 ll. 604–605 cite in
§4.7.2 for the *changed-only* set is a different claim, re-resolved and left intact. No conformance
row added, per the review's rejection of the candidate's second half.

### Notes deferred

- **F-4 (l. 224 locator one line short).** Not applied: notes are not ordered for fix-up, and the
  review's own re-derivation records the joint locator as already correct at §5.2b l. 1174 and the
  §5.1 citation as correct, so nothing substantive is misstated.
- **F-5 (`ReloadCoordinator` row does not name the pending slot).** Not applied: notes are not
  ordered, and the edit would land inside the interface region, enlarging an already-firing
  change-trigger surface for a gap the review found recoverable from RS-1, `[D-P12-17]` and §4.7.4.

No refusal. No other file was modified and the review above this section is unchanged.