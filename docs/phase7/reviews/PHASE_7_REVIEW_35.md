# Phase 7 Review — Round 35

## 0. Method and reading order

Sole adjudicator for round 35 of `phase-7`. Reading order followed as instructed:

1. Target `docs/phase7/v1/PHASE_7_DOC.md` at the cited sites (§0 header and §0.25–§0.38, §3.1,
   §5.2 Phase 5/6 inventories and their binding citations, §5.4, §5.5, §11.3–§11.5, §12, closing
   status).
2. Authority `docs/design/v2.0-RC3/DESIGN.md` (Part I, Phase 7 target spec, doc gate, mandatory
   template) and contract ground truth `docs/research/v1/RESEARCH.md` §4.4 (lines 529–567).
3. Dependencies: `docs/phase5/v1/PHASE_5_DOC.md` §5.1 binding rows (1998–2020) and
   `docs/phase6/v1/PHASE_6_DOC.md` §5.1 binding rows (1379–1399).
4. Only after independent re-derivation, the 34 prior reviews in `docs/phase7/reviews`
   (adjudicator-last), consulted for settled dispositions.

No network use. No subagent fan-out from this session; the candidate set was produced upstream by
the canonical engine. No Gate drops and no pre-adjudication eliminations were reported. Forbidden
sources (`docs/**/chatlogs/**`, `*.txt`) were not read; the finders' non-resolution of
`files.txt`-backed §3.7 rows is accepted as correct scope discipline.

Deviation of note: the manifest resolves the RC3 revision, which the target header also names; no
divergence arose this round.

## 1. Findings

### Finding 1 (candidate-001) — §5.4 and §11.5 still gate the accepted Phase 8 grants on the review "recorded in/required by §0.25"

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:2130`–`:2131` (§5.4) and `:2445`–`:2446` (§11.5).
- **Claim under test:** after Rounds 33/34 resynchronized the header pointer and closing status to
  the latest §5 change, the two statements gating Phase 8 grant consumability still name the
  correct owed review.
- **Evidence:** §5.4 reads "the grants remain unavailable to dependents until this document
  receives the fresh review recorded in §0.25"; §11.5 reads "The grant becomes consumable only
  after the fresh Phase 7 review required by §0.25." §0.25's own status clause (lines 234–238)
  names a specific condition: "a fresh round twenty-two returns literal PASS". Rounds 22 through 34
  have since run (§0.26–§0.38; closing status 2496–2498), and eight later fix-ups changed binding
  §5, the most recent being Round 34 (§0.38, lines 317–321). So the §0.25 condition is satisfiable
  on its face while the document's actual posture is that v1 is unverified pending the review owed
  by the Round-34 §5 change. §11.3 (lines 2413–2415) and the closing paragraph already state the
  correct, revision-agnostic condition, so the §0.25 anchors are internally inconsistent with the
  same document.
- **Severity:** correction. This is the operative gate on when downstream Phase 8 may consume the
  accepted R8-1/R8-4/R8-5 grants; as written a consumer can read the named condition as already
  met. Not blocking: the correct condition is stated twice elsewhere in the document and no
  contract semantics are misstated.
- **Touches interface/change-trigger region:** yes (the §5.4 edit at 2130–2131 lies inside the
  declared cross-phase-interfaces region 1362–2146).
- **Fix:** replace both `§0.25` anchors with the revision-agnostic condition already used in §11.3
  and the closing status — the grants become consumable only after the most recently changed binding
  §5 (per the latest §0 fix-up entry) receives a literal-PASS whole-document review, together with
  Phase 8's own §5 verification.

### Finding 2 (candidate-002) — Phase 5 binding citation stops one row short of the `MainDepthSource` row

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:2017`, `:2026`–`:2028`.
- **Claim under test:** every consumed Phase 5 contract is anchored inside the cited binding range.
- **Evidence:** the only Phase 5 anchor is `docs/phase5/v1/PHASE_5_DOC.md:2002`–`:2017` plus
  `:2018`; the row declaring `MainDepthSource`, `MainDepthPreparation`, `MainDepthSnapshot` is at
  Phase 5 `:2019`. Phase 7 lists `MainDepthSource` as consumed (line 2017) and exposes
  `MainDepthPreparation` in the public `FrameHookSink.afterFirstClear` signature (line 437). The
  `refreshMainDepth` half of the consumed row *is* covered, at Phase 5 `:2006`.
- **Severity:** note. The citing sentence is expressly scoped to "the general Phase 5 frame/buffer
  rows"; the uncited row is two lines below the cited block inside the same Phase 5 §5.1 table, and
  Phase 7's substance (installs/prepares) matches Phase 5 `:2019` exactly. No contract is left
  underdetermined. Round 34 adjudicated this same site at note severity on unchanged bytes; my
  re-derivation reaches the same calibration and I do not escalate.
- **Touches interface/change-trigger region:** no (unordered note; it orders no edit).
- **Suggested wording (not ordered):** extend the anchor to `:2002`–`:2019`.

### Finding 3 (candidate-003) — Phase 6 citation omits the consumed `UniformPlatformProvider`/`CenterDepthSource` row

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:2038`, `:2040`–`:2041`.
- **Claim under test:** the Phase 6 binding citation covers the rows Phase 7 consumes.
- **Evidence:** the anchor is `docs/phase6/v1/PHASE_6_DOC.md:1382`–`:1390`, whose final line is the
  Phase-11-only `FixedExpressionInputSchema` row; the consumed platform-provider/center-depth row is
  at Phase 6 `:1392`, listing "`mod.glue`, Phase 7" as consumers.
- **Severity:** note. The citation sentence is explicitly scoped to "runtime, frame-begin, event,
  and participant rows", so it asserts no false coverage; Phase 6 `:1392` independently binds the
  row with Phase 7 named as consumer, and Phase 7 §5.3 already specifies the provider's
  construction ordering. This is citation completeness, not consumer-hittable ambiguity. The
  candidate's suggested widening to `:1394` is too broad — that line adds the fixed sampler maps row.
- **Touches interface/change-trigger region:** no (unordered note; it orders no edit).
- **Suggested wording (not ordered):** append `docs/phase6/v1/PHASE_6_DOC.md:1392` for the
  platform-provider row; leave the `:1390` endpoint alone.

### Finding 4 (candidate-004) — §3.1 water/hand row's primary RESEARCH coordinate points at the neighbouring row's line

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:531` (§3.1).
- **Claim under test:** each §3.1 row's cited RESEARCH §4.4 range supports the item it maps.
- **Evidence:** the water/hand row cites `docs/research/v1/RESEARCH.md:544`, which is
  "copy depth→depthtex1, then DEFERRED passes" — the content already claimed by the neighbouring
  row at `:543`–`:544`. The water→hand-solid→hand-translucent statement is at `:545`. The row's
  second coordinate `:560`–`:561` does reach the hand-rendering bullet head at `:561`.
- **Severity:** note. The candidate's broader claim of a systematic one-line downward bias across
  the §3.1 block does not hold on re-derivation: `:538` (fixed unit map) and `:566` (frame-end
  guarantee) are exact, and every other multi-line row's range contains its supporting line
  (`:543`–`:544` covers 544; `:545`–`:546` covers 546; `:551`–`:552` covers 552; `:557`–`:559`
  covers 558–559; `:562`–`:564` covers 563–564; `:533`–`:537` covers 535–537). Only the water/hand
  primary coordinate and two range-edge truncations remain, all recoverable within one line, in a
  non-interface conformance table. Rounds 33 and 34 adjudicated this same site as an unordered note
  on unchanged bytes; I do not escalate a settled note.
- **Touches interface/change-trigger region:** no.
- **Suggested wording (not ordered):** re-point the primary coordinate to `:545` (keeping `:561`),
  and optionally extend the gbuffers row to `:543` and the composite/final row to `:547`.

## 2. Checked and clean

Re-derived and confirmed clean, or cleared on re-derivation:

- **New-surface lens (accepted):** the §0.26–§0.38 fix-up log is internally coherent and each entry
  correctly declares whether §5 changed; the Round-34 repairs landed (the false Phase 3 freshness
  clause is gone, the header pointer reads §0.38 and the closing status attributes the most recent
  §5 change to Round 34); §5.4's R7-9 gate, §11.3's dependency-blocker list, and §12 rows 1 and 5
  state the Phase 3 reverification posture consistently. `FrameHookSink`'s §2.2 operation list
  matches its §5.1 declaration.
- **Interface lens (accepted):** the Phase 3 (`:1429`–`:1444`), Phase 4 (`:1560`–`:1574`,
  `:1576`–`:1580`) and the enumerated Phase 6 (`:1382`–`:1390`) anchors resolve inside their
  dependencies' manifest-selected binding regions; the exposed §5.1 result algebras are closed with
  stated disposition rules; Phase 8/9 slots are honestly framed as dormant downstream slots rather
  than retroactive dependencies.
- **Conformance lens (accepted):** §3.2 maps every Appendix A.1 slot family including the virtual
  `*_pre` slots, the deferred/composite arrays and `final`, inside a range that genuinely contains
  the table; §3.3 traces all eleven §7.1 hook needs with no unmapped row; §3.4's seven Pintonium
  rows carry adopt/deviate rulings and correctly flag sky/weather/clouds as reference-free.
  `files.txt`-backed §3.7 rows were not re-resolved (forbidden source).
- **Candidates partially cleared inside admitted findings:** the `refreshMainDepth` half of the
  §5.2 Phase 5 row is correctly anchored (Phase 5 `:2006`); the Phase 6 citation sentence's
  enumerated scope is accurate for the four families it names; the §3.1 rows other than water/hand
  are supported by their cited ranges, so candidate-004's systematic-bias framing is rejected.
- **Prior-review interaction:** Rounds 33 and 34 deliberately deferred the Phase 5 anchor and the
  §3.1 coordinate notes; those dispositions are settled on unchanged bytes and I keep them as
  notes. No prior round has adjudicated the §0.25 grant anchors, so Finding 1 is new.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=3
Interface changed: yes

One correction and three notes; no blocking finding and no structural miss. The thirteen mandatory
sections, conformance maps, and interface algebras are intact, so nothing requires rebuilding.
Finding 1's ordered edit touches §5.4, inside the declared cross-phase-interfaces region (lines
1362–2146), so the change trigger fires: a fresh verify round is required before Phase 7 can close.

Trend/convergence: no prior-round trend was supplied to this session, but the surviving defect count
has narrowed again against Round 34 (two corrections, two notes → one correction, three notes), and
the single correction is a bookkeeping consequence of the document's own accumulated fix-up log
rather than a substantive design defect. This is consistent with a converging document.

Next required action: apply Finding 1 (Findings 2–4 are notes and are not ordered), then re-run
verification for `phase-7` because the interface region changes.


## Resolutions

### Applied

**Finding 1 (correction) — applied.** Re-derived independently: §0.25's status clause (target
lines 234–238) conditions freshness on "a fresh round twenty-two returns literal PASS", a condition
that rounds 22–34 have already passed through, while eight later fix-ups (§0.27–§0.38) changed
binding §5. So both grant gates named a satisfiable-on-its-face condition. §11.3 (2413–2417) and the
closing status already state the revision-agnostic condition, confirming the internal inconsistency
rather than a design ambiguity.

Edits:
- §5.4 closing paragraph (was 2130–2131): the grants are unavailable "until the most recently
  changed binding §5 — per the latest §0 fix-up entry — receives a whole-document review returning
  literal PASS."
- §11.5 final bullet (was 2445–2446): consumable "only after the most recently changed binding §5
  (per the latest §0 fix-up entry) receives a literal-PASS whole-document review and Phase 8's own
  §5 is verified" — matching §11.3, which already names both conditions.
- New §0.39 Round-35 fix-up entry in the existing §0 style, plus the two neighbour-consistency
  updates that entry forces: the header `Last revised` pointer (§0.38 → §0.39) and the closing
  status's "through Round 34 / Round 34 most recently changed binding §5" → Round 35.

Neighbour sweep: §5.4's R7-8/R7-9 rows and the "every still-pending request needs its owner's
§G1.3 fix-up and any review owed by a changed §5" sentence already use the revision-agnostic
formulation and were left alone. No other site in the document gates a grant on §0.25; the two
remaining §0.25 references (the §0.25 heading/status itself and the closing status's historical
"rounds preceded §0.25") are historical records and are correct as written.

**Interface region:** the §5.4 edit lies inside the declared cross-phase-interfaces region, so the
change trigger fires intentionally. The edit narrows no contract and grants nothing new; it only
restates the gate condition on the already-accepted R8-1/R8-4/R8-5 grants. A fresh verify round is
required before Phase 7 can close.

### Notes deferred

- **Finding 2 (Phase 5 anchor stops at `:2018`).** Not applied: unordered note, adjudicated at note
  severity in Rounds 34 and 35 on unchanged bytes. The citing sentence is expressly scoped, and
  editing §5.2 would enlarge the interface diff for a citation-completeness improvement.
- **Finding 3 (Phase 6 platform-provider row uncited).** Not applied: unordered note; Phase 6
  `:1392` independently names Phase 7 as consumer and §5.3 already fixes construction ordering.
- **Finding 4 (§3.1 water/hand primary coordinate).** Not applied: unordered note; the adjudicator
  explicitly rejected the systematic-bias framing and declined to escalate a settled note.