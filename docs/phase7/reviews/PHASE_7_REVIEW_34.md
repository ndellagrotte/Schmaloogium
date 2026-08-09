# Phase 7 Review — Round 34

## 0. Method and reading order

Sole adjudicator for round 34 of `phase-7`. Reading order followed as instructed:

1. Target `docs/phase7/v1/PHASE_7_DOC.md` at the cited sites (§0 header, §0.35–§0.37, §3.1, §5.2
   Phase 3/4/5/6 inventories and their binding citations, §12, closing status).
2. Authority `docs/design/v3/DESIGN.md` (Part I, Phase 7 target spec, doc gate, mandatory
   template) and contract ground truth `docs/research/v1/RESEARCH.md` (§4.4 frame flow, lines
   529–567).
3. Dependencies: `docs/phase3/v1/PHASE_3_DOC.md` (§5.1 rows 1429–1444, trailing status 2119–2121)
   and `docs/phase5/v1/PHASE_5_DOC.md` (§5.1 binding rows 2002–2020), plus Phase 2/4/6 regions as
   needed.
4. Only after independent re-derivation, the 33 prior reviews in `docs/phase7/reviews`
   (adjudicator-last), consulted for settled dispositions.

No network use. No subagent fan-out from this session; the candidate set was produced upstream by
the canonical engine. No Gate drops. One pre-adjudication elimination was reported (candidate-004,
Phase 5 clear-consumption row, dropped by strict refuting majority); I did not resurrect it.
Forbidden sources (`docs/**/chatlogs/**`, `*.txt`) were not read; the finder's non-resolution of
`files.txt`-backed §3.7 rows is accepted as correct scope discipline.

Deviation of note: the header still names `docs/design/v2.0-RC3/DESIGN.md` as governing while the
manifest resolves v3. Prior rounds settled that the cited RC3 text is verbatim identical to the v3
target-spec text, so no semantic infidelity arises; I did not reopen it.

## 1. Findings

### Finding 1 (candidate-001) — §5.2 still asserts the consumed Phase 3 exposure is "freshly verified", contradicting Phase 3's own status and this document's own gates

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1983`–`:1985`.
- **Claim under test:** the Round-33 re-pointing of the Phase 3 §5.1 binding citation left the
  surrounding freshness assertion correct.
- **Evidence:** the sentence reads "…binding at `docs/phase3/v1/PHASE_3_DOC.md:1429`–`:1444` and is
  freshly verified by `docs/phase3/reviews/PHASE_3_REVIEW_22.md`." Phase 3's own trailing status
  (`docs/phase3/v1/PHASE_3_DOC.md:2119`–`:2121`) records that its loop has advanced through round 36
  and that "Phase 3 v1 is **not verified** pending a fresh whole-document review". The target itself
  treats Phase 3 as awaiting reverification at `:1938` and `:2434`–`:2436`. The re-pointed range
  `:1429`–`:1444` is itself correct; only the freshness clause is false.
- **Severity:** correction. This is the §G5.3 gate statement for the largest consumed dependency
  surface; as written it tells a consumer the whole Phase 3 binding block is settled while the same
  document gates R7-9-dependent production on Phase 3 reverification. It is not blocking: every
  operative gate elsewhere already encodes the correct posture, and no contract semantics are
  misstated.
- **Touches interface/change-trigger region:** yes (line 1984 is inside 1356–2140).
- **Fix:** drop the freshness clause and state the document's actual posture, e.g. "…is binding at
  `docs/phase3/v1/PHASE_3_DOC.md:1429`–`:1444`; Phase 3 v1 is currently unverified pending a fresh
  whole-document review, so R7-9-dependent production remains gated (§5.4)." The §0.2 gate row at
  line 63 states a historically true literal-PASS coordinate and need not be rewritten; the fix is
  scoped to 1983–1985 (and, if desired, an annotation that line 63's PASS is historical).

### Finding 2 (candidate-002) — header "Last revised" pointer and closing status were not synchronized with the Round-33 fix-up

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:7` and `:2489`–`:2492`.
- **Claim under test:** the Round-33 fix-up (§0.37) is reflected in the document's own change
  accounting.
- **Evidence:** line 7 reads "**Last revised:** 2026-08-08 (§0.36)" while §0.37 (lines 311–315) is a
  later Round-33 fix-up that explicitly states "The §5 interface region changed and requires fresh
  verification". The closing paragraph at 2490–2491 still reads "Corrections and recorded notes
  through Round 32 are applied; Round 32 most recently changed binding §5". The document's own
  convention is to synchronize both (§0.32, line 275: "Round 29 synchronizes the header and closing
  status with the Round-28 correction").
- **Severity:** correction. The self-contradiction understates which round's §5 change owes fresh
  verification — a fact consumers of the change record rely on — and the document's own convention
  makes the synchronization normative, not stylistic. Not blocking: the substantive Round-33 repairs
  are present in the body.
- **Touches interface/change-trigger region:** no (both sites are outside 1356–2140).
- **Fix:** set line 7 to `(§0.37)` with the Round-33 date, and revise 2490–2491 to state that
  corrections and recorded notes through Round 33 are applied and that Round 33 most recently
  changed binding §5.

### Finding 3 (candidate-003) — Phase 5 binding citation stops one row short of the `MainDepthSource`/`MainDepthPreparation` row

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:2011`, `:2020`–`:2022`.
- **Claim under test:** every consumed Phase 5 contract is anchored inside the cited binding range.
- **Evidence:** the only Phase 5 anchor spans `docs/phase5/v1/PHASE_5_DOC.md:2002`–`:2018`, but the
  row defining `MainDepthSource`, `MainDepthPreparation`, `MainDepthSnapshot` is at `:2019`
  (`:2018` is the resize family, correctly cited separately). Phase 7 lists `MainDepthSource` as
  consumed at line 2011 and exposes `MainDepthPreparation` in its own §5.1 hook signature at
  line 1370.
- **Severity:** note. The citing sentence is expressly scoped to "the general Phase 5 frame/buffer
  rows"; the uncited row is adjacent, inside the same Phase 5 §5.1 table the anchor already resolves
  into, and both type names appear verbatim in the target (lines 709, 1370, 2011). No contract is
  left underdetermined — this is citation completeness, not consumer-hittable ambiguity.
- **Touches interface/change-trigger region:** yes (lines 2011–2022 inside 1356–2140).
- **Suggested wording (not ordered):** extend the anchor to `:2002`–`:2019` and name
  `MainDepthPreparation` alongside `MainDepthSource` in the §5.2 row.

### Finding 4 (candidate-005) — §3.1 fullscreen-executor row's provenance range omits the `scale.<prog>` and `countInstances` clauses

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:529` (§3.1).
- **Claim under test:** the row's cited range contains all four contract clauses it names.
- **Evidence:** the row cites `docs/research/v1/RESEARCH.md:562`–`:564`. Direct read shows `:562` is
  the unrelated first-person-overlay clause; the composite-pass bullet spans `:563`–`:565`, with
  `optional sub-viewport (`scale.<prog>`), `countInstances` instancing loop` physically on `:565`.
  No other row in the target cites `:565`.
- **Severity:** note. Round 33 adjudicated this exact site (its Finding 7) at note severity and did
  not order it; my re-derivation reaches the same calibration. The substantive mapping to
  `FullscreenPassExecutor` §4.6 is correct, the correct target sits inside the very bullet the
  citation already points into, and the neighbouring row's `:566` citation resolves exactly, so the
  defect is an isolated endpoint truncation. Re-raising a settled note as a correction is not
  warranted on unchanged bytes.
- **Touches interface/change-trigger region:** no (line 529 is outside 1356–2140).
- **Suggested wording (not ordered):** re-point the range to `:563`–`:565`.

## 2. Checked and clean

Re-derived and confirmed clean, or cleared on re-derivation:

- **New-surface lens (accepted):** the §3.5 Phase 3 flag coordinates (697, 702–705, 706–709, 710,
  711, 713) and the precipitation coordinate (757) resolve exactly to the cited Phase 3 rows; the
  schema-v4 `IdMappingInput` alignment is consistent at §4.1 step 1, §5.2, and §5.3 and matches
  Phase 3's published schema version; the H-TERRAIN-02 v0.5 depth-copy gate is now stated
  consistently across §4.5, the §4.10 catalog row, §5.2, §9, and §12. All four Round-33 repairs
  landed. The Phase 3 §5.1 anchor `:1429`–`:1444` spans exactly the consumed rows — only the
  neighbouring freshness clause (Finding 1) is defective.
- **Interface lens (accepted):** the Phase 3, 4, and 6 consumption anchors resolve inside their
  dependencies' manifest-selected binding regions; the exposed §5.1 result algebras are closed with
  stated disposition rules; the Phase 8/9 downstream slots remain honestly marked ungranted until
  verified rather than assumed.
- **Conformance lens (accepted):** §3.1's world-state, fixed-unit, shadow-ordering, gbuffers,
  depthtex1/deferred, water/hand, composite/final, push/pop, depth-copy and composite-guarantee rows
  resolve to supporting lines (`:566` quoted exactly); §3.2 maps every Appendix A.1 slot family with
  no unmapped row; §3.3 disposes all eleven §7.1 hook needs; §3.4 covers all seven Pintonium rows
  with adopt/deviate rulings and correctly flags sky/weather/clouds as reference-free; §3.5 covers
  all eleven Phase-3-owned flags; §4.10.8 accounts for all 18 Appendix E rows with owner phase and
  milestone. `files.txt`-backed §3.7 rows were not re-resolved (forbidden source).
- **Eliminated upstream:** candidate-004 (Phase 5 clear consumption omitting `clearPlan`) was
  dropped before adjudication by refuting majority; independent spot-check of §5.2 line 2010 shows
  the frame/pass transaction row names the executed sequence and the planning step is described in
  §4 prose, so no live severity survives.
- **Prior-review interaction:** Round 33's Findings 6 and 7 were recorded as unordered notes and
  deliberately not edited; Finding 4 above is the same site and stays a note. Round 33's clearance
  that "header and closing status agree" is superseded by its own §0.37 addendum, which created the
  desync in Finding 2.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=2
Interface changed: yes

Two corrections and two notes; no blocking finding and no structural miss. The thirteen mandatory
sections, conformance maps, and interface algebras are intact, so nothing requires rebuilding.
Finding 1 (correction) and Finding 3 (note) fall inside the declared cross-phase-interfaces region
(lines 1356–2140), so the change trigger fires: a fresh verify round is required before Phase 7 can
close.

Trend/convergence: no prior-round trend was supplied to this session, but the surviving defects have
narrowed sharply against Round 33 (four corrections, three notes → two corrections, two notes), and
both remaining corrections are bookkeeping consequences of the Round-33 edit itself rather than new
substantive design defects. This is consistent with a converging document.

Next required action: apply Findings 1 and 2 (Findings 3 and 4 are notes and are not ordered), then
re-run verification for `phase-7` because the interface region changes.
