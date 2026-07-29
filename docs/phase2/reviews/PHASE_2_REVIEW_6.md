# Schmaloogium — Phase 2: Conformance harness — Review Round 6

## 0. Method and reading order

I independently re-derived both gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, including the conformance and milestone requirements.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially its
   GL-error and §5 contracts.
4. The whole selected target, `docs/phase2/v1/PHASE_2_DOC.md`.
5. Only after settling both candidates, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
   `docs/phase2/reviews/PHASE_2_REVIEW_5.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents or other agent fan-out and did not invoke the verification
harness. The canonical engine supplied the finder, refuter, steelman, and Gate material. The Gate
dropped no candidates, and no candidates were eliminated before adjudication. Forbidden sources
were not read.

The prior reviews do not settle the present defects. Round 3 established the run-manifest schema,
and rounds 4–5 extended its semantic fields, but none defines the separately promised
unattributable-error count or a derivation for it. Round 5's dual-spec correction established the
v0.1–v0.5 release-gate cadence; it did not authorize placing that cadence range in a column which
the governing design restricts to one implementation milestone.

## 1. Findings

### candidate-001 — The run-manifest schema leaves the unattributable-error count ambiguous

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:705–731`, with reporting semantics at
  `docs/phase2/v1/PHASE_2_DOC.md:455–458` and interface exposure at
  `docs/phase2/v1/PHASE_2_DOC.md:1340`.
- **Claim:** `schmaloogium.run-manifest/1` gives producers and consumers one unambiguous
  representation from which the reported unattributable GL-error count is obtained.
- **Evidence:** The manifest block catalogue requires attributed `GLError` records and
  “separately, the count of unattributable ones”
  (`docs/phase2/v1/PHASE_2_DOC.md:705`). The canonical repeated-record schema instead defines only
  the common `gl_errors.count` and dense records containing
  `{op,subject,kind,detail,attributed}` (`docs/phase2/v1/PHASE_2_DOC.md:726–731`). It neither names
  a separate unattributable-count scalar nor says that consumers derive the count by selecting
  records whose `attributed=false`. This is observable: §4.2.1 requires the report to say how many
  unattributable errors occurred (`docs/phase2/v1/PHASE_2_DOC.md:455–458`), while §5 exposes the
  wire schema to Phases 4 and 7 (`docs/phase2/v1/PHASE_2_DOC.md:1340`).
- **Disposition:** Admitted. Choose one canonical representation. Preferably define the reported
  count as the number of dense `gl_errors` records whose `attributed=false`; alternatively add a
  uniquely named scalar with its type and validation rule. Align the block catalogue, schema
  validation, canonical fixture, and consumer tests.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-002 — Dual-spec release cadence is used as an implementation milestone tag

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:1571–1613`, repeated at
  `docs/phase2/v1/PHASE_2_DOC.md:1996`.
- **Claim:** Every component in the milestone and implementation-checklist tables carries exactly
  one implementation milestone, as required by the governing design.
- **Evidence:** The governing rule says every designed component carries exactly one tag and that
  the tag means “implemented at that milestone”
  (`docs/design/v1.1/DESIGN.md:320–322`). The target repeats that rule
  (`docs/phase2/v1/PHASE_2_DOC.md:1571–1573`) and already assigns `RUN-T0` and
  `RUN-T1-REGRESS` the single implementation tag `v0.1`
  (`docs/phase2/v1/PHASE_2_DOC.md:1606–1608`). Nevertheless, the separate dual-spec row puts
  provisional `v0.1`–`v0.5` in the Tag column
  (`docs/phase2/v1/PHASE_2_DOC.md:1611–1613`), and checklist item 36 repeats that range in its Tag
  column (`docs/phase2/v1/PHASE_2_DOC.md:1996`). The target itself correctly describes the range
  elsewhere as execution at each release gate (`docs/phase2/v1/PHASE_2_DOC.md:379–382`), confirming
  that it is recurring cadence rather than implementation timing.
- **Disposition:** Admitted. Give the dual-spec run definitions one implementation tag, consistent
  with the existing `v0.1` run definitions, and move the provisional v0.1–v0.5 release-gate cadence
  into the Note column. Make the same implementation-versus-cadence distinction in checklist item
  36 without removing its §11.3 qualification.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The conformance mapping otherwise preserves the dual-spec source conflict consistently across
§3.5, §9.1, §11.3, §11.5, and §12. The world-tree admission and copied-tree hashing rules remain
internally coherent. The required pack scalar names, encodings, and acquisition values remain
consistent. The run-manifest's T0 evidence, baseline identities, availability rules, dense-record
grammar, and Phase 3/Phase 4 golden-enrichment boundary showed no additional defect in the examined
surface.

The dependency and interface sweep found no additional mismatch with Phase 1's binding contract.
The full conformance sweep found substantive coverage for the pack matrix, T0–T3 semantics,
fixture and harness requirements, §9 exit criteria, the before-renderer subset, and OQ-10. The
generic device-entry test does not contradict another target requirement.

Both candidates survived independent re-derivation; neither was refuted or cleared. The Gate
dropped none, and no candidate-free finding is added.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both defects are bounded fix-up work rather than structural misses requiring a rebuild.
Candidate-001 affects the run-manifest wire schema exposed in §5; candidate-002 is a non-interface
milestone-taxonomy correction.

The loop has not converged: rounds 4 and 5 both returned corrections and changed the interface, and
this fresh review finds two more corrections, one interface-affecting. The next required action is
a scoped fix-up resolving both findings and recording their resolutions in this review. Because
the exposed run-manifest contract must be clarified, a fresh verify round is required before
Phase 2 can close.

## Resolutions

### candidate-001 — resolved

Re-derived from §4.2.1's reporting promise and the existing dense-record grammar, the canonical
representation is now one source of truth: `gl_errors.count` counts every record, `attributed` is a
required boolean on every record, and consumers derive the unattributable count by counting records
whose value is `false`. No redundant scalar can disagree with the records. The block catalogue,
schema validation, named canonical fixture, evaluator tests, and §5 exposure now state that rule.
Because §5 changed, a fresh verify round is required before Phase 2 can close.

### candidate-002 — resolved

Re-derived from `docs/design/v1.1/DESIGN.md` §G4.3, the dual-spec run definitions now carry exactly
one implementation tag, `v0.1`, consistent with the other T0/T1 run definitions. Their provisional
execution at each v0.1–v0.5 release gate remains in the Note/item prose and retains the §11.3 item
10 qualification. The same distinction is made in implementation-checklist item 36.

### Notes deferred

None; the adjudication admitted no notes.
