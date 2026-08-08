## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected v3 Part I, Phase 7 specification, document gate, and mandatory template,
RESEARCH ground truth, and the binding §5 contracts of Phases 2–6. I first compared every
challenged §5.2 dependency-location assertion with the current manifest-selected dependency
region. Only after settling those judgments did I read
`docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_24.md`, in order and last.

The resolved v3 design selection is an explicit verification-only override; it does not change the
target's declared adoption state. The only reading-list deviation was
`reference-src/schlorbium-HD_U_G6_pre1/files.txt`: the resolved contract forbids every `*.txt`
source, so I did not read it. The remaining supporting implementation evidence was immaterial to
these dependency-citation candidates. There was no network use, forbidden-source use, or agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
supplied `verify-loop` instructions required completing only this role without invoking the loop or
delegating. Candidate-001 was eliminated by Refute before adjudication; Gate dropped none.

## 1. Findings

### candidate-003 — Four dependency inventories point outside the manifest-selected binding contracts

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1878`, `:1892`–`:1894`, `:1924`, and
  `:1936`–`:1937`
- **Claim:** Section 5.2 inaccurately identifies obsolete detailed-design ranges as the current,
  complete, or binding interface locations for contracts consumed from Phases 2, 3, 5, and 6.
- **Evidence:** Phase 7 calls Phase 2 lines 1471–1487 its binding surfaces
  (`docs/phase7/v1/PHASE_7_DOC.md:1875`–`:1878`), but the selected Phase 2 cross-phase interface
  starts at line 1591 and its Phase-7-consumed scene, determinism, run-manifest, and capture-plan
  rows are at `docs/phase2/v1/PHASE_2_DOC.md:1597`–`:1606`. Phase 7 calls Phase 3 lines
  1251–1337 its complete binding exposure (`docs/phase7/v1/PHASE_7_DOC.md:1892`–`:1894`), while
  Phase 3 declares its complete publication surface beginning at
  `docs/phase3/v1/PHASE_3_DOC.md:1394`, with the relevant rows at `:1403`–`:1418`. Phase 7 calls
  Phase 5 lines 1819–1833 the current binding rows
  (`docs/phase7/v1/PHASE_7_DOC.md:1913`–`:1924`), but those lines are detailed-design prose and
  Phase 5's current binding surface begins at `docs/phase5/v1/PHASE_5_DOC.md:1996`; the resize
  family is bound at `:2018`. Finally, Phase 7 calls Phase 6 lines 1181–1196 its binding ordering
  and public surface (`docs/phase7/v1/PHASE_7_DOC.md:1936`–`:1937`), whereas the selected Phase 6
  interface begins at `docs/phase6/v1/PHASE_6_DOC.md:1376`, with the factory, runtime,
  frame-begin result, and ordering rows at `:1382`–`:1385`. The inventory semantics broadly align
  with those actual interfaces, so the defect is incorrect contract provenance rather than a
  missing architecture.
- **Required correction:** Replace all four stale assertions with narrow citations to the exact
  consumed rows inside the current manifest-selected Phase 2, 3, 5, and 6 §5 regions. Audit the
  adjacent Phase 4 supporting citation in the same pass, without substituting whole-selector
  ranges where exact rows suffice.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — every false dependency-location assertion is
  inside the manifest-declared Phase 7 cross-phase interface region.

## 2. Checked and clean

The finder-reported conformance and interface clean areas survived independent re-derivation apart
from the admitted dependency-citation defect. The consumed contract names and broad semantics are
present in the current dependency §5 regions. Phase 7's frame orchestration, program-family map,
all eleven hook needs, seven-row injection-timeline disposition, engine flags, lifecycle triggers,
internal/off behavior, and Phase 8/9 seams remain substantively specified. No additional defect
was substantiated in the neighboring publication-order numbering, §0.27 interface-change status,
or repeated resize type names.

Candidate-002 is derivationally correct that the Phase 5 citation at
`docs/phase7/v1/PHASE_7_DOC.md:1924` does not identify Phase 5's current binding row. It is dropped
as a separate finding because candidate-003 includes that identical citation, evidence, interface
classification, and remedy; admitting both would double-count one defect. Candidate-001 was
eliminated before adjudication after its claimed missing registration lifecycle did not survive
refutation. Gate dropped no candidate.

Prior Round 23 corrected the then-current Phase 5 inventory and Round 24 passed that resulting
surface, but neither settles the present finding against the dependency regions selected for this
round: Round 24 itself treated the now-obsolete Phase 5 detailed range as authoritative and did not
correct the other three stale anchors. Thus prior PASS is not evidence that the current resolved
contract locations are accurate.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a bounded cross-phase citation correction, not a structural miss requiring
rebuild. Relative to Round 24's literal PASS, the current resolved surface has regressed to one
correction, so convergence is no longer established. The next required action is a scoped fix-up
of candidate-003, including this review's `## Resolutions` record and the Phase 7 addendum. Because
the correction changes the manifest-selected cross-phase interface region, a fresh whole-document
and interface verification round is required before Phase 7 can close.

## Resolutions

### candidate-003 — resolved

Re-derived the consumed contracts from the manifest-selected dependency §5 regions. Phase 7 §5.2
now cites Phase 2's exact tier/scene/determinism/manifest/capture rows at 1597–1606, Phase 3's
consumed publication rows at 1403–1418, Phase 5's general frame/buffer rows at 2002–2017 and its
resize row at 2018, and Phase 6's runtime/frame/event/participant rows at 1382–1390. The adjacent
Phase 4 citation was also stale: it now points to the consumed rows at 1560–1574 and the explicit
consumer prohibition at 1576–1580. No dependency semantics were changed.

This correction intentionally changes the manifest-selected cross-phase interface region. A fresh
whole-document and interface verification round is therefore required before Phase 7 can close.
The compact §0.28 addendum records only that status and the citation repair.

### Notes deferred

None; the adjudication admitted no notes.
