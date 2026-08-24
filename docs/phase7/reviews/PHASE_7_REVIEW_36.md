# Phase 7 — Review 36

## 0. Method and reading order

1. Independent re-derivation first: target `docs/phase7/v1/PHASE_7_DOC.md` at the cited sites (§3.1
   conformance rows, §5.2 Phase 5 inventory and its binding citation, §5.1 hook signature).
2. Authority `docs/design/v3/DESIGN.md` (Part I, Phase 7 target spec, doc gate, §G9 mandatory
   template) and contract ground truth `docs/research/v1/RESEARCH.md` (§4.1 lines 474–491, §4.4
   lines 528–566), read directly with line numbers rather than trusting candidate coordinates.
3. Dependency `docs/phase5/v1/PHASE_5_DOC.md:1996`–`:2020` (binding §5.1 rows).
4. Only afterwards, prior reviews `PHASE_7_REVIEW_1..35` in round order, adjudicator-last.

No network use. No subagent fan-out; the dispatched candidate set was adjudicated as delivered.
Forbidden sources (`docs/**/chatlogs/**`, `*.txt`, any agent/session transcript) were not read.
Gate drops: none. One candidate (candidate-001, §0.2 Phase 3 dependency-gate literal) was
eliminated before adjudication by strict refuting majority; it is discussed in §2 and is not
re-opened here.

## 1. Findings

No blocking findings and no corrections. Two notes, neither ordered for fix-up.

### Note 1 (candidate-002) — Phase 5 binding citation stops one row short of the `MainDepthSource` row

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:2023`, `:2032`–`:2034` (§5.2 Phase 5 inventory).
- **Claim under test:** every consumed Phase 5 contract in the §5.2 inventory is anchored inside
  the cited binding range.
- **Evidence (verified directly):** the only Phase 5 anchors offered are
  `docs/phase5/v1/PHASE_5_DOC.md:2002`–`:2017` plus `:2018` (resize family). The row declaring
  `MainDepthSource`, `MainDepthPreparation`, `MainDepthSnapshot` is at Phase 5 `:2019`. Phase 7
  lists `refreshMainDepth` / `MainDepthSource` as consumed at `:2023` and carries
  `MainDepthPreparation` in the exposed `afterFirstClear` signature at `:1382`.
- **Why note, not correction:** the citing sentence is expressly scoped to "the general Phase 5
  frame/buffer rows"; the `refreshMainDepth` half of the consumed row *is* covered in range at
  Phase 5 `:2006`; the uncited row sits two lines below the cited block in the same Phase 5 §5.1
  table and its substance ("Phase 7 installs/prepares") matches Phase 7's text exactly. No contract
  is left underdetermined and no consumer can be misdirected. Rounds 34 and 35 adjudicated this same
  site at note severity on unchanged bytes; my independent re-derivation reaches the same
  calibration and I decline to escalate a settled note.
- **touches interface/change-trigger region: no** (unordered note; it orders no edit to the
  declared §5 interface region even though the cited line sits inside it).
- **Suggested wording (not ordered):** extend the anchor to `:2002`–`:2019`, or add `:2019`
  explicitly for the main-depth SPI.

### Note 2 (candidate-003) — §3.1 RESEARCH provenance ranges are imprecise at their endpoints

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:535`, `:537`, `:538`, `:539`, `:541`, `:544` (§3.1).
- **Claim under test:** each §3.1 row's cited RESEARCH range contains the text it maps.
- **Evidence (re-derived from RESEARCH.md directly, rejecting the candidate's coordinates where
  they disagree):** the candidate's strong framing does not survive. Most cited ranges do overlap
  their supporting text: `:540`–`:542` covers 541–542 of the gbuffers chain; `:543`–`:544` covers
  544 (depthtex1/deferred); `:545`–`:546` covers 546 (composite ping-pong); `:551`–`:552` covers
  552 (push/pop dispatch); `:557`–`:559` covers 558–559 (depth copies/center depth); `:562`–`:564`
  covers 563–564 (identity ortho, mipmap regeneration). The candidate's §4.1 instance is
  affirmatively wrong: RESEARCH's Uninit item occupies `:489`–`:490` with 491 blank, so the row's
  cited `:483`–`:490` fully contains the dimension-switch and resolution-multiplier triggers.
  Adjacent rows `:566` and `:478`–`:481` resolve exactly, so there is no systematic offset. What
  remains is genuine but narrow: the water/hand row's primary coordinate `:544` points at the
  neighbouring depthtex1/deferred line (the water→hand-solid→hand-translucent statement is `:545`,
  the hand bullet `:561`–`:562`), and several ranges truncate one line early so that trailing
  clauses (armor glint/eyes/particles/clouds/weather; "leash/glint rendering"; `scale.<prog>` /
  `countInstances`; "→ FINAL to screen") sit just outside the window.
- **Why note, not correction:** every affected row's design element is independently and
  unambiguously identified in-document (§3.2 dispatch table, §4.5, §4.6, §4.10 H-HAND), the cited
  ranges land within one line of their text in the same fenced block or bullet list of the same
  cited section, and no contract content is misstated. Rounds 34 and 35 each adjudicated members of
  this same §3.1 coordinate family at note severity on unchanged bytes and expressly rejected the
  systematic-bias framing; my re-derivation confirms that rejection, and the candidate adds no
  consumer-hittable defect that would justify escalating a twice-settled note.
- **touches interface/change-trigger region: no** (§3.1 lies outside the declared interface region
  1368–2153, and the note orders no edit).
- **Suggested wording (not ordered):** re-point the water/hand row to `:545` plus `:561`–`:562`,
  and extend the gbuffers, composite, push/pop and fullscreen-executor ranges by one line each to
  `:541`–`:543`, `:546`–`:547`, `:552`–`:553`, `:563`–`:565`. Leave `:483`–`:490`, `:566`, and
  `:478`–`:481` unchanged; they already resolve correctly.

## 2. Checked and clean

- **new-surface lens (accepted).** The Round-35 edits are internally coherent: §5.4's closing
  paragraph and §11.5's final bullet both express the revision-agnostic "most recently changed
  binding §5" condition matching §11.3 and the closing status; the §0.39 entry, the header
  "Last revised" pointer, and the closing status agree; identifiers introduced in Rounds 31–33
  (`ResizeObservationPort` family, `AnaglyphEye`, `BlendStateValue`) are each declared and used
  consistently; R7-8/R7-9 gate wording is uniform across §1.1, §5.4, §11.3, §11.5, §12.
- **interfaces lens (accepted, spot-verified).** Phase 2 (`:1597`–`:1606`), Phase 4
  (`:1560`–`:1574`), Phase 5 general rows (`:2002`–`:2017`, `:2018`) and Phase 6 (`:1382`–`:1390`)
  citations resolve onto rows carrying the named contracts. Exposed §5.1 contracts are closed
  (sealed results, closed enums, explicit rejection vocabularies), each names a consumer, and §5.3
  steps 1–15 plus §4.3/§4.10 hook rows supply sequencing sufficient to implement without guessing.
  Phase 8/9 slots are correctly dormant integration slots, not declared dependencies.
- **conformance lens (accepted).** §3.3 maps all eleven §7.1 hook needs within `:796`–`:819`; §3.2
  enumerates every Appendix A.1 slot family including deferred/composite/final and the virtual
  `*_pre` rows; §3.4 dispositions all seven Pintonium timeline rows with adopt/split/reject
  rationale and flags sky/weather/clouds as reference-free; §3.5/§3.6/§3.7 record contradictions
  rather than silently resolving them; §4.10.8's App E table dispositions all 18 rows. The thirteen
  mandatory §G9 sections are present and populated, and OQ-3/OQ-4 remain addressed.
- **Eliminated before adjudication.** candidate-001 (§0.2 recording Phase 3's dependency gate as a
  literal PASS against §5.2/§11.3's "unverified" statement) was dropped by strict refuting majority
  as a historical-record entry rather than a live claim; that disposition is settled and I found no
  independent reason to re-open it.
- **Refuted on re-derivation.** candidate-003's §4.1 "uninit trigger truncation" instance and its
  systematic one-line-bias framing are affirmatively false, and its recommended `:483`–`:491` fix
  would be wrong; the surviving residue is folded into Note 2 at note severity.

## 3. Verdict

# PASS
Counts: blocking=0; corrections=0; notes=2
Interface changed: no

Zero blocking findings and zero corrections. Both admitted items are unordered notes at citation
endpoints already adjudicated at note severity in prior rounds on unchanged bytes; neither leaves a
contract underdetermined, and neither orders an edit to the declared interface/change-trigger region
(`docs/phase7/v1/PHASE_7_DOC.md:1368`–`:2153`).

Trend/convergence: Round 34 closed at 2 corrections / 2 notes, Round 35 at 1 correction / 3 notes,
and this round at 0 corrections / 2 notes. The remaining candidate surface has converged onto
repeated citation-precision residue with no new substantive defect, which is the expected terminal
state for this loop.

Next required action: none for Phase 7. The document may close; the two notes may be folded
opportunistically into any future §0 fix-up entry but are not ordered and do not require a further
verify round.
