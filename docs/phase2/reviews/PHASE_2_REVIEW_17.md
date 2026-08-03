# Schmaloogium — Phase 2: Conformance harness — Review Round 17

## 0. Method and reading order

I independently re-derived both gated candidates before reading any prior review. I read the
manifest-selected target, `docs/phase2/v1/PHASE_2_DOC.md`; the governing Part I, Phase 2 target
specification, document gate, and mandatory template in `docs/design/v1.1/DESIGN.md`; the contract
ground truth in `docs/research/v1/RESEARCH.md`; the selected binding dependency in
`docs/phase1/v14/PHASE_1_DOC.md`; and the supporting CI evidence under `.github/workflows/`. Only
after settling both candidates did I read `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
`PHASE_2_REVIEW_16.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. Forbidden
sources were not read. The Gate reported no drops, and no candidate was eliminated before
adjudication.

The prior reviews were considered only after independent judgment. Rounds 14–16 settle the
ownership and availability of the replay-aware result: Phase 1 owns the accepted contract, Phase 6
produces its values through replay, and Phase 7 serializes them. Round 16's PASS predates the §0.17
amendment, but that amendment does not reopen Phase 1's completed owner review. No prior review
settles candidate-001's editorial-range interpretation, which is independently cleared below.

## 1. Findings

### candidate-002 — Accepted Phase 1 interfaces are still marked as awaiting owner review

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:1545–1549,1587–1591`
- **Claim:** Binding §5 must state the current availability of the selected Phase 1 contracts
  consistently, so implementers can consume accepted grants without a fictitious review gate.
- **Evidence:** Phase 2 says that Phase 1 applied `[D-P1-42]` but that the amended surface “remains
  implementation-gated until its required fresh review returns literal PASS”
  (`docs/phase2/v1/PHASE_2_DOC.md:1545–1549`). Its request table likewise labels both R1 and R4A
  “fresh owner review pending” (`docs/phase2/v1/PHASE_2_DOC.md:1587–1591`). The selected Phase 1
  binding contract unconditionally exposes the Phase 7 frame-package grant
  (`docs/phase1/v14/PHASE_1_DOC.md:4182–4185`) and `ReplayAwareGLError`, expressly permitting
  Phase 2/7 to serialize or assert it (`docs/phase1/v14/PHASE_1_DOC.md:4202–4204`). Phase 2 itself
  states that the Phase 1 verification chain is complete, Phase 1 is valid for dependency
  consumption, and “No Phase 1 gate remains open here”
  (`docs/phase2/v1/PHASE_2_DOC.md:2045–2048`). The pending-review qualifiers therefore contradict
  both the binding dependency and the target's current dependency-status ruling.
- **Disposition:** Admitted. Remove the obsolete fresh-owner-review/PASS gate from §5.2 and the
  R1/R4A labels, and describe the selected Phase 1 v14 grants as accepted and consumable. Preserve
  the distinct runtime responsibilities: Phase 6 performs replay and Phase 7 serializes its result.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The new runner-owned pack-provenance contract is consistent across §0.17, the capture lifecycle and
wire schemas, binding §5, failure behavior, tests, staging, `[D-P2-23]`, downstream handoffs, and
the checklist. Acquisition mode, verified archive SHA-512, and licence have consistent origins,
transport, validation, and publication rules. The conformance map covers the Phase 2 scope,
T0–T3, all seven Appendix G packs, harness requirements, scene families, named runs, milestone exit
criteria, and the disclosed Appendix G/§9 scheduling conflict. Other examined Phase 1 consumptions
match the selected binding contract, and downstream promises are backed by detailed designs or
explicitly unresolved requests.

### candidate-001 — cleared on re-derivation

- **Location tested:** `docs/phase2/v1/PHASE_2_DOC.md:160–161`
- **Claim tested:** “This amendment changes §§4.5.1–4.5.4” falsely claims a physical text edit in
  every subsection in that interval.
- **Disposition:** Dropped. The notation naturally identifies the contiguous detailed-design
  region affected by the amendment; it does not expressly inventory every physical edit site.
  Section 4.5.3 can remain unchanged while the amendment affects the surrounding lifecycle,
  capture-plan, and run-manifest contract. The statement correctly requires a fresh whole-document
  review and creates no false consumer obligation or contractual ambiguity. Enumerating §§4.5.1,
  4.5.2, and 4.5.4 would be optional editorial precision, not a defect.
- **Final severity:** none
- **touches interface/change-trigger region:** no

No other candidate was available to admit. The Gate dropped none, and no finding is created from
the finder-reported clean areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a bounded consistency fix, not a structural miss requiring rebuild. It is
inside the declared cross-phase interface region and can incorrectly prevent implementers from
consuming contracts that the selected dependency already exposes.

Rounds 15 and 16 had converged to literal PASS. The §0.17 amendment legitimately reopened review,
and this round finds one stale interface gate rather than a defect in the new provenance surface;
the sequence therefore regresses from PASS and is not currently converged. The next required action
is a scoped fix-up resolving candidate-002 and recording its resolution in this review. Because §5
must change, a fresh whole-document verify round is required before Phase 2 can close again.

## Resolutions

### candidate-002 — resolved

Re-derived against the selected Phase 1 v14 binding §5: `[D-P1-41]` exposes the Phase 7 frame
package grant, including `mod.conformance`, and `[D-P1-42]` exposes `ReplayAwareGLError` for Phase
2/7 serialization and assertion. Phase 2's own dependency-status ruling also records that Phase 1's
verification chain is complete. The contrary pending-review qualifiers had no remaining gate to
refer to.

Updated `docs/phase2/v1/PHASE_2_DOC.md` §5 throughout the affected formulation: R1 and R4A are now
marked accepted and consumable from Phase 1 v14; the prose-level fresh-PASS gate was removed; and
the run-manifest, R17 request, and downstream handoff now consume the accepted R4A result without a
pending-acceptance qualifier. The runtime split is unchanged: Phase 6 performs replay and Phase 7
serializes the resulting boolean verbatim. Added compact §0.18 and advanced the header revision.

This changes the declared cross-phase interface region, so a fresh whole-document verify round is
required before Phase 2 can close.

### Notes deferred

None; the adjudication admitted no notes.
