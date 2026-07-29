# Schmaloogium — Phase 2: Conformance harness — Review Round 8

## 0. Method and reading order

I independently re-derived all three gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, especially the conformance tiers and milestone requirements.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially §5 and
   its GL-error contract.
4. The live manifest, `verification/targets/phase-2.json`, and the current target,
   `docs/phase2/v1/PHASE_2_DOC.md`.
5. Only after settling every candidate, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
   `docs/phase2/reviews/PHASE_2_REVIEW_7.md`, in round order.

The required deviation is itself a finding: the dispatched resolved contract says the target ends
at line 1940, but the current target continues through line 2016. I inspected the omitted tail to
establish the selector defect and to avoid representing the round as complete; that inspection
cannot retroactively repair the engine's immutable selection or provide valid finder coverage.

No network access was used. This already-dispatched atomic adjudication role started no subagents,
agent fan-out, or nested verification run. The canonical engine supplied the finder, refuter, and
Gate material. The Gate reported no drops, and no candidates were eliminated before adjudication.
Forbidden sources were not read.

The prior reviews do not settle the admitted findings. Their broad clean conclusions predate the
Round-7 fix-up and do not cure this round's stale resolved target. Round 6 clarified how
unattributable GL errors are represented, but did not adjudicate whether such errors may pass the
authoritative T0 predicate.

## 1. Findings

### candidate-001 — The dispatched whole-document selector excludes the target tail

- **Location:** resolved target contract for `docs/phase2/v1/PHASE_2_DOC.md`.
- **Claim:** The resolved `whole_document` range of lines 1–1940 covers the complete target
  reviewed in Round 8.
- **Evidence:** The resolved endpoint falls in the middle of §11.5 item 5
  (`docs/phase2/v1/PHASE_2_DOC.md:1937–1940`). Mandatory §12 begins only at
  `docs/phase2/v1/PHASE_2_DOC.md:1949–1953`, and the document closes at
  `docs/phase2/v1/PHASE_2_DOC.md:2012–2016`. Thus 76 lines, including the Round-7-edited ordered
  checklist, were outside the engine-declared whole-document selection. The live manifest uses a
  start-only content selector (`verification/targets/phase-2.json:10–18`), so the repository
  configuration does not justify the stale resolved endpoint; the dispatch/resolution state is
  internally inconsistent.
- **Severity:** blocking.
- **Disposition:** Stop this verification chain. Correct or regenerate the resolved target state,
  rerun startup validation against the complete current target, and dispatch a fresh review. A
  finder clean-area report cannot establish whole-document coverage for material its validated
  selector excluded.
- **touches interface/change-trigger region: no**

### candidate-003 — T0 permits recorded GL errors that the authority forbids

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md` §4.2.1.
- **Claim:** The target's T0 decision procedure is semantically faithful to the authoritative
  “no GL errors” predicate.
- **Evidence:** The authority defines T0 as “pack parses, programs compile, no GL errors, stable
  frame loop,” without an attribution exception
  (`docs/research/v1/RESEARCH.md:913–918`). The target instead fails the GL predicate only for an
  error attributed to a facade call (`docs/phase2/v1/PHASE_2_DOC.md:441–450`) and explicitly makes
  any surviving unattributed error a warning that never fails T0
  (`docs/phase2/v1/PHASE_2_DOC.md:465–468`). Phase 1's binding GL-error contract explains why a
  drain can contain a foreign error and why attribution may fail; it does not redefine the
  Research T0 acceptance gate. A run containing a recorded GL error can therefore pass the
  target's evaluator while failing the authoritative predicate.
- **Severity:** correction.
- **Disposition:** Make every GL error admitted to the defined T0 run fail T0. If ambient errors
  are intended to be outside the predicate, define a controlled shaders-off baseline/subtraction
  procedure that substantiates that exclusion, or request an explicit upstream clarification;
  attribution failure alone cannot turn a recorded error into a passing outcome.
- **touches interface/change-trigger region: no**

## 2. Checked and clean

Within the readable target, the Complementary family label is consistently applied to both
variants, the dual-spec run milestone and provisional recurrence agree across §§3.5, 9.1, and 12,
and checklist numbering remains consecutive. The conformance sweep otherwise found the Appendix G
pack rows, T1–T3 mappings, §9 exit-criterion runs, and the dual-spec source conflict substantively
covered. The dependency/interface sweep found the examined Phase 1 consumptions grounded in the
binding contract or explicitly presented as requests, and Phase 2's §5 promises point to
substantive detailed designs.

Candidate-002 is dropped. The live manifest already uses content-derived anchors from
`## 5. Cross-phase interfaces` through `## 6. Failure modes & degradation`
(`verification/targets/phase-2.json:139–147`), and that end anchor follows all of §§5.1–5.4 at
`docs/phase2/v1/PHASE_2_DOC.md:1454–1456`. Its proposed manifest correction is therefore already
present. The stale resolved coordinates are another symptom of the dispatch-state failure admitted
as candidate-001, not evidence that the repository's interface selector is fixed to those lines.
It is not counted separately and does not set the interface-change flag. The Gate dropped none.

## 3. Verdict

# FAIL
Counts: blocking=1; corrections=1; notes=0
Interface changed: no

Candidate-001 is a structural verification miss: this round cannot establish whole-document
coverage and cannot be repaired by a Phase 2 document fix-up. Candidate-003 is a bounded
non-interface semantic correction, but applying it cannot validate the incomplete Round-8 review.

The correction trend has not converged: rounds 5–8 are 3 → 2 → 2 → 1 corrections, but Round 8 also
introduces one blocking dispatch defect. No admitted finding changes the cross-phase interface
region. The next required action is to repair or refresh the stale selector-resolution state,
validate the complete current target, and dispatch a fresh review; once the verification input is
sound, resolve candidate-003 through the normal correction workflow. Phase 2 cannot close until a
fresh round returns literal PASS with zero blocking findings and zero corrections.
