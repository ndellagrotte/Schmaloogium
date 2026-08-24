# Phase 11 — Verification Review, Round 7

Target: `docs/phase11/v1/PHASE_11_DOC.md` (whole document)
Manifest: `verification/targets/phase-11.json`
Governing design revision: `docs/design/v3/DESIGN.md` (v3, override selection)

## 0. Method and reading order

Sources read before judgment: the target document regions cited by every candidate (§0.2, §1.2,
§2.2, §4.8, §5.3, §10) plus surrounding context; the binding dependency regions of
`docs/phase6/v1/PHASE_6_DOC.md` (§4.13 declarations at 1209–1233 and the definition-order /
accepted-prefix / invalid-counter prose at 1319–1338) and `docs/phase3/v1/PHASE_3_DOC.md`
(1142–1153, 687–697, 753–761); and `docs/research/v1/RESEARCH.md` (the Appendix D cadence sentence
at 1376–1387 and the open-question ledger rows at 1024–1029). Every candidate's citation was
re-resolved independently, raw and line-exact, against the primary text before any prior review was
opened. Prior reviews (Rounds 1–6) were read last, adjudicator-last, only to check settled material
and recurrence.

Deviations: none. No forbidden source was read — no `docs/**/chatlogs/**`, no `*.txt`, no prior
agent or session transcript. No network use. No subagent fan-out from this session; the candidate
set was supplied by the engine. Gate drops: none. Candidates eliminated before adjudication: none.

## 1. Findings

### Finding 1 (candidate-001, subsuming candidate-003) — §4.8's accepted-prefix citation points at Phase 6's `ExpressionValue` record block

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:718`–`:719`.
- **Claim under test:** "Phase 6 commits any already accepted prefix exactly as its contract
  states (`docs/phase6/v1/PHASE_6_DOC.md:1217`–`:1223`)."
- **Evidence:** Phase 6 lines 1215–1225 are the `ExpressionValue` sealed interface and its record
  members (`Int1` at 1217 through `Int4` at 1223); nothing in that range states commit behavior.
  The accepted-prefix commit rule is at Phase 6 `:1325`–`:1328` ("Phase 6 commits only the accepted
  prefix, in order, during that activation; skipped/rejected prefix entries perform no GL …"), and
  the immediately following invalid-counter branch at `:1329`–`:1334` expressly "supersedes the
  otherwise-applicable accepted-prefix commit rule". §5.3 anchors the same grant correctly at
  `:1296`–`:1329` (target line 1016), so §4.8 is also internally inconsistent with §5.3.
- **Severity:** correction. I weighed the steelman that §5.3's correct anchor supplies equivalent
  coverage and reduces this to a note. It fails: §4.8 is the passage that states Phase 11's own
  reliance on the Abort-time commit rule, and its binding-contract pointer lands on unrelated type
  declarations, not a near-miss line offset. It is not blocking because the substantive rule as
  stated is faithful to Phase 6. Candidate-003 reports the identical defect and is subsumed here.
- **Fix ordered:** repoint the §4.8 citation to the range that actually carries the rule —
  `:1325`–`:1334`, so the superseding invalid-counter branch is included — or, if consistency with
  §5.3 is preferred, `:1296`–`:1334`. Re-derive boundaries from the current Phase 6 text.
- **Touches interface/change-trigger region:** no. Line 719 lies outside 955–1048 and the ordered
  edit changes no §5 text.

### Finding 2 (candidate-002) — §1.2's two Phase 3 anchors both resolve to unrelated Phase 3 material

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:158` and `:187` (the candidate's stated 206–221 is
  off; the substance is unaffected).
- **Claim under test:** Phase 3 "preserves raw expression text and source order, and never invokes
  this grammar (`docs/phase3/v1/PHASE_3_DOC.md:1145`–`:1147`)"; and "Phase 3 records the same
  [precipitation] ownership (`docs/phase3/v1/PHASE_3_DOC.md:690`–`:693`)".
- **Evidence:** Phase 3 `:1143`–`:1148` is the `ProgramRequirements` record and the `DrawRouting`
  sealed interface; Phase 3 `:690`–`:693` is §3.1 Appendix F.1 engine-flag prose followed by the
  flag table at 695+. The material both sentences rely on is the Appendix F.6 rows at Phase 3
  `:756` (expression text lossless after unescape, occurrence order preserved, not evaluated by
  Phase 3 → Phase 11) and `:757` (F.6 precipitation rule, "this row has no Phase 3 parser/model
  assertion" → Phase 7). §0.9 affirmatively claims the drifted Phase 3 anchors were repointed in
  Round 6; these two were not.
- **Severity:** correction. Both ownership claims are true and supported by Phase 3, so this is
  mis-anchored citation rather than a wrong contract claim; but a consumer following either pointer
  lands hundreds of lines away in unrelated declarations/prose, which is more than style.
- **Fix ordered:** repoint target line 158 to `docs/phase3/v1/PHASE_3_DOC.md:754`–`:756` (the
  `uniform.*`/`variable.*`/lossless-text rows) and target line 187 to `:757`.
- **Touches interface/change-trigger region:** no.

### Finding 3 (candidate-004) — §2.2's cadence citation range excludes the sentence it quotes

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:248`–`:249`.
- **Claim under test:** the quoted required cadence "on every program switch after built-ins" is at
  `docs/research/v1/RESEARCH.md:1379`–`:1382`.
- **Evidence:** RESEARCH line 1379 is blank; the cadence sentence runs 1380–1383 and the quoted
  custom-uniform clause is physically on line 1383, one line past the cited range's end. No other
  line-anchored citation of this cadence exists in the target (§3.3's cadence row names sections
  only).
- **Severity:** correction. The steelman that the anchor still lands inside the same paragraph is
  partly right, but the target presents a direct quotation and the cited range does not contain the
  quoted words, which is a failed quote resolution rather than a formatting preference. It is not
  blocking: the substantive cadence claim is fully supported by the authority.
- **Fix ordered:** repoint to `docs/research/v1/RESEARCH.md:1380`–`:1383`.
- **Touches interface/change-trigger region:** no.

### Finding 4 (candidate-005) — both OQ-22 anchors (§0.2 and §10) resolve to the OQ-21 row

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:50`–`:51` and `:1208`–`:1209`.
- **Claim under test:** `RESEARCH.md:999`–`:1027` is "the exact OQ-22 ledger text", and
  `RESEARCH.md:1027` contains "expression-engine compilation".
- **Evidence:** RESEARCH line 1027 is the OQ-21 lwjglx row; the entire OQ-22 catch-all row,
  including the phrase "expression-engine compilation", is line 1028 — outside §0.2's attested read
  range and one line past §10's anchor. These are the target's only line-anchored OQ-22 citations;
  §9's milestone row and §11.3's handoff bullet carry none.
- **Severity:** correction. §10's load-bearing Phase 14 handoff attributes a quoted phrase to a line
  that does not contain it, and §0.2's read attestation excludes the row it claims to have quoted.
  Not blocking: the OQ-22 substance is stated correctly and the handoff itself is sound.
- **Fix ordered:** change §10's anchor to `docs/research/v1/RESEARCH.md:1028` and extend §0.2's
  attested range to `:999`–`:1028`.
- **Touches interface/change-trigger region:** no.

## 2. Checked and clean

**Candidate re-derivation.** No candidate was refuted outright. Candidate-003 duplicates
candidate-001 (identical location, identical defect) and is therefore dropped as subsumed, not as
refuted; its substance is fully carried by Finding 1, and its refuters' disagreement over severity
(`correction` vs `note`) is resolved above in favour of `correction`. No candidate was eliminated
before adjudication and there were no Gate drops, so there is no pre-settled material to discuss.

**New-surface lens.** I accept the reported clean areas after spot-verification: the RESEARCH
Appendix F.6 anchors repointed in §0.3, §3.1, §3.2 and §3.3 resolve to the quoted material; §5.2's
Phase 3 `:1442` and `:1459`–`:1480` anchors and §5.3's Phase 6 `:1200`–`:1205`, `:1207`–`:1225`,
`:1226`–`:1250`, `:1283`–`:1286`, `:1296`–`:1329` anchors resolve exactly (I re-read Phase 6
1209–1233 directly and confirm the declaration-block boundaries); §3.3's `:1266`–`:1267` seven-name
anchor is exact; §11.2 item 4's deferral to the §0.4-onward addenda is consistent with §0.2 and
§0.9. Repeated identifiers (fourteen `is_*` booleans, seven excluded names,
`schmaloogium:typed-ast-interpreter-v1`, the 0.25/0.50 ms budgets, D-P11-1..13) are internally
consistent. Round 6's Finding 1 (chronology drift) does not recur.

**Interfaces lens.** The declared `cross-phase-interfaces` region (955–1048) is untouched by every
admitted finding. The consumed Phase 6 grant set, the closed `UniformResetReason` mapping with
`FRAMEBUFFER_RESIZE` correctly declared as having no Phase 6 counterpart, §5.1's incorporation and
synchronization clause over §§2.3, 4.1, 4.9–4.12, and §5.5's composition handoff all hold on
re-derivation; no substantive interface mismatch survived.

**Conformance lens.** Doc-gate coverage is complete: every Appendix F.6 token, operator,
declaration form, named function and constructor, member/matrix access, biome/view input, and the
seven-name exclusion union appears as a mapped row with a named test, alongside the smooth state
machine, the evaluator interface and selection criteria, the error ladder, and the stareval license
outcome. The `shaders.properties` and SHADER_ENGINE_IMPL citations resolve and support their
claims. The mandatory thirteen-section structure is present and the phase carries no assigned open
question.

## 3. Verdict

# PASS-WITH-CORRECTIONS

Counts: blocking=0; corrections=4; notes=0
Interface changed: no

Interface disposition: no admitted finding orders any edit inside the manifest-declared
`cross-phase-interfaces` region (`docs/phase11/v1/PHASE_11_DOC.md:955`–`:1048`). All four ordered
edits are line-anchor repointings in §0.2, §1.2, §2.2, §4.8 and §10, outside that region. The
region's change trigger does not fire on this round's ordered corrections; it will fire only if the
fix-up nonetheless edits §5.

Trend and convergence: Round 6 closed PASS-WITH-CORRECTIONS with 0 blocking, 5 corrections and an
interface change. Round 7 is strictly narrower: 0 blocking, 4 corrections, no interface change. All
four are the same residual class Round 6 surfaced and only partially discharged — line-anchor drift
against dependency and authority documents — and Round 6's own clean-area statement that "RESEARCH
citations at target lines 241 and 1027 resolve" was itself the source of the surviving §10 defect.
The contract's substance has been stable across both rounds; convergence is real and the remaining
work is mechanical. No convergence warning is warranted, but the fix-up should sweep *all* remaining
external line anchors rather than only the four cited here, since two consecutive rounds have found
residuals after targeted repointing.

Next required action: apply the four ordered corrections in a Round-7 fix-up, re-deriving every
replacement range from the current dependency and authority text rather than adopting the proposed
numbers verbatim, and performing a full sweep of every `PHASE_3_DOC.md:`, `PHASE_6_DOC.md:`,
`RESEARCH.md:` and `DESIGN.md:` line anchor in the document. If the fix-up leaves §5 untouched, no
interface change trigger fires from this round.


## Resolutions

All four corrections applied to `docs/phase11/v1/PHASE_11_DOC.md`. Every replacement range was
re-derived from the current primary text; proposed numbers were not adopted verbatim.

1. **Finding 1 (§4.8 accepted-prefix anchor).** Re-read Phase 6 1320–1336 raw: the commit rule
   begins mid-line at `:1325` and runs to `:1328`; the invalid-result branch is `:1329`–`:1334` and
   expressly supersedes it. Cited `:1325`–`:1334` and added a six-word clause naming the
   superseding branch so the widened range is not unexplained. §5.3's `:1296`–`:1329` anchor was
   left untouched, keeping §5 unedited.
2. **Finding 2 (§1.2 Phase 3 anchors).** Re-read Phase 3 750–760 raw: `uniform.*` row 754,
   `variable.*` 755, the lossless-text/occurrence-order row 756, the F.6 precipitation row 757.
   Target line 158 now cites `:754`–`:756`, line 187 `:757`.
3. **Finding 3 (§2.2 cadence).** Re-read RESEARCH 1374–1388 raw: 1379 blank, cadence sentence
   1380–1383 with the custom-uniform clause on 1383. Cited `:1380`–`:1383`.
4. **Finding 4 (OQ-22).** Re-read RESEARCH 1020–1032 raw: 1027 is OQ-21, 1028 is the OQ-22
   catch-all containing "expression-engine compilation". §10 now cites `:1028`; §0.2's attested
   read range widened to `:999`–`:1028`.
5. **Full anchor sweep (ordered by the verdict).** Re-derived every remaining `DESIGN.md:`,
   `RESEARCH.md:`, `PHASE_3_DOC.md:` and `PHASE_6_DOC.md:` anchor: DESIGN `:2281`, `:2286`–`:2288`,
   `:468`–`:500`, `:920`–`:922`, `:419`–`:425`, `:451`–`:456`, `:410`–`:415`, `:2300`–`:2303`,
   `:2310`–`:2322`; RESEARCH `:1493`–`:1512`, `:1495`–`:1496`, `:1498`–`:1501`, `:1501`–`:1505`,
   `:1507`–`:1510`, `:781`–`:787`; Phase 3 `:1442`, `:1459`–`:1480`; Phase 6 `:1200`–`:1205`,
   `:1207`–`:1225`, `:1226`–`:1250`, `:1253`–`:1259`, `:1266`–`:1267`, `:1283`–`:1286`,
   `:1296`–`:1329`, `:1389`–`:1391`. All resolve to the material they are cited for; no further
   repointing was needed.

New §0.10 records the Round-7 fix-up in the document's existing addendum style.

Interface disposition: no text inside the declared `cross-phase-interfaces` region
(`docs/phase11/v1/PHASE_11_DOC.md:955`–`:1048` pre-edit) was changed. The only edits above that
region are in §0.2 (one character) and the §0.10 insertion, which shift the region's line numbers
but not its content. The change trigger should not fire on content.

### Notes deferred

None; the round recorded no notes.

No refusal.