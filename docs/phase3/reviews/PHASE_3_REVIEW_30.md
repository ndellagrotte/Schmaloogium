# Phase 3 Adversarial Review — Round 30

## 0. Method and reading order

I independently re-derived the sole surviving candidate from the complete Phase 3 target, then
the manifest-selected v3 governing-design regions, the relevant RESEARCH.md contract ground
truth, the Phase 1 binding contract, the manifest, and the permitted supporting evidence. Only
after settling the candidate's interpretation, severity, and interface classification did I read
prior reviews 1–29, in round order and including their resolutions.

There were no deviations from the required reading order, no network use, no agent fan-out, and
no use of forbidden sources. Under the dispatched-role exception in the supplied `verify-loop`
skill, I did not invoke the verification harness or start another session.

The Gate dropped `candidate-004` because its RESEARCH.md quote did not resolve uniquely, and
dropped `candidate-001` and `candidate-002` because their supplied quotes were not verbatim at
their declared anchors. I did not revive those candidates or derive findings from them.

## 1. Findings

### candidate-003 — The requested dependency change incorrectly reopens Phase 1's existing notice contract

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1503-1511`.
- **Claim:** Phase 3 does not honestly distinguish an existing Phase 1 binding contract from the
  genuinely missing jcpp dependency declaration and seam accommodation.
- **Evidence:** Phase 1's binding §5 expressly exposes the “SPDX header convention +
  `THIRD-PARTY.md` mechanism” to “all phases”
  (`docs/phase1/v14/PHASE_1_DOC.md:4274-4276`). Phase 3 also lists that mechanism among its
  consumed Phase 1 contracts, but §5.4 categorically says that “Phase 1 §5 does not expose a
  dependency-declaration/notice contract” and requests both a jcpp pin and Apache-2.0 attribution
  (`docs/phase3/v1/PHASE_3_DOC.md:1503-1511`). The pure-`:engine` jcpp declaration/version pin and
  any necessary seam-test accommodation may be absent, but recording jcpp attribution is use of
  the already-published generic notice mechanism, not a missing dependency contract.
- **Severity:** correction. Revise §5.4 to acknowledge that jcpp attribution consumes Phase 1's
  existing SPDX/`THIRD-PARTY.md` mechanism. Retain only the genuinely missing request for the
  jcpp dependency declaration/version pin and any explicit pure-JVM seam-test accommodation.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The finder-reported new surface is internally consistent apart from the admitted finding: live
  production and interface labels use schema v3, historical schema-v2 chronology remains clearly
  historical, and `TextureBindingKey` consistently excludes sampler type in favor of downstream
  derivation.
- The schema-v3 discipline, nested `IdMappingInput` version equality, texture binding semantics,
  discovery/load generation protocol, `ResourceRequirements`, `ProgramStateModel`, and the
  remaining consumed Phase 1 runtime contracts were rechecked without another surviving defect.
- The conformance map retains complete examined Appendix F and Appendix A.3 coverage, including
  the four required Pintonium pitfalls and named tests for the owned discovery, preprocessing,
  macro, option, directive, and ID-mapping families.
- `candidate-003` is not cleared by prior settled material. Prior reviews repeatedly described a
  missing jcpp dependency or dependency/notice allowance, but none reconciled that shorthand with
  Phase 1 §5's already-binding generic SPDX/`THIRD-PARTY.md` mechanism. The current §5.4 wording
  therefore remains contradictory even though its intended attribution action is understandable.
- Gate-eliminated candidates `candidate-001`, `candidate-002`, and `candidate-004` remain dropped;
  no finding was dropped during independent derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The sole admitted finding is a bounded interface-honesty correction and does not require rebuilding
the architecture. Round 29 had two corrections; this round reduces the count to one, so the trend
improves but has not converged to literal PASS.

The next required action is a scoped fix-up resolving `candidate-003` and appending this review's
`## Resolutions`. Because the repair changes the manifest-declared §5 cross-phase-interface region,
the interface change trigger fires: a fresh whole-document verification round is required before
Phase 3 may close.

## Resolutions

### candidate-003 — applied

Re-derived against the binding Phase 1 §5.3 convention table and swept the Phase 3 formulations.
Phase 3 §5.4 now acknowledges that jcpp attribution consumes the already-published
SPDX/`THIRD-PARTY.md` mechanism. Its requested dependency change is limited to the missing
`:engine` declaration/version pin and explicit pure-JVM seam-test accommodation. The corresponding
§11.5 hand-off and §12 implementation step now make the same distinction.

This intentionally changes the manifest-declared §5 cross-phase-interface region. The fresh
whole-document verification trigger therefore fires before Phase 3 can close.

### Notes deferred

None. The adjudicator admitted no notes.
