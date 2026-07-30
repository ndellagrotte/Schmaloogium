# Phase 3 Adversarial Review — Round 18

## 0. Method and reading order

I independently re-derived the surviving candidate from, in order:

1. `docs/phase3/v1/PHASE_3_DOC.md` in full, with particular attention to the program-state
   conformance rows, profile semantics, detailed models, and §5 publication contract;
2. the selected governing material in `docs/design/v2.0-RC3/DESIGN.md`, including Part I, §G9,
   and the Phase 3 specification and document gate;
3. the relevant contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding interface region of `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the permitted supporting evidence and supplied candidate record.

Only after settling that judgment did I read
`docs/phase3/reviews/PHASE_3_REVIEW_1.md` through
`docs/phase3/reviews/PHASE_3_REVIEW_17.md`, in round order and including their resolutions.
I made no deviation from the assigned reading order, used no network access, performed no agent
fan-out, and read no forbidden source. The Gate reported no drops, and no candidate was eliminated
before adjudication.

## 1. Findings

### candidate-001 — `ProgramStateModel` is promised without an implementable data contract

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:1074`

**Claim:** Phases 4 and 5 cannot implement consumption of the published program-state model
without inventing its aggregate representation and executable semantics or reconstructing
Phase 3 parser results.

**Evidence:** Section 5 declares itself the complete Phase 3 publication surface and requires
every consumer to receive the same `PackConfiguration`, not a parallel parser result
(`docs/phase3/v1/PHASE_3_DOC.md:1059-1060`). Within that surface, the entire
`ProgramStateModel` contract is only “alpha/blend/scale/flip/enabled and profile-disabled
programs,” with Phases 4 and 5 named as consumers
(`docs/phase3/v1/PHASE_3_DOC.md:1073-1075`). The conformance map supplies useful value-level
parsing rules for `AlphaTestSpec`, `BlendSpec`, `ViewportScale`, tri-state flips, and evaluated
program enablement (`docs/phase3/v1/PHASE_3_DOC.md:529-533`), while the profile prose specifies
selection and enabled-expression evaluation (`docs/phase3/v1/PHASE_3_DOC.md:988-998`).
However, none defines the published model's exact keys and fields, immutable value structure,
absence/default results, combination of property enablement with profile-disabled programs,
precedence/materialization rules, or observable ordering and validation behavior. Consumers may
not recover those omissions by reopening the pack, rescanning directives, or reinterpreting
properties (`docs/phase3/v1/PHASE_3_DOC.md:1137-1140`). This falls short of the governing
requirement for exact data models and named exposed data contracts
(`docs/design/v2.0-RC3/DESIGN.md:809-813`).

**Severity:** correction. Define the closed immutable `ProgramStateModel` contract in §5.1, with
supporting §4 detail as needed: the appropriate program, buffer, and dimension key types; typed
alpha-test, blend, scale, flip, enabled, and profile-disable values; defaults and absence results;
duplicate, precedence, and profile-disable combination rules; validation outcomes; deterministic
iteration wherever observable; and the precise Phase 4 and Phase 5 views. This is a bounded
contract completion, not an architectural rebuild.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The new-surface examination found the Round 17 noise correction internally consistent:
  `NoiseRequirement(enabled, resolution)` is exposed through `ResourceRequirements` to Phase 13,
  while `NoiseTextureSpec` remains the separate override-source contract.
- The Phase 3 conformance maps retain coverage for the in-scope Appendix F keys, Appendix A.3
  directives, engine-flag ownership, and mandated Pintonium pitfalls, with corresponding detailed
  design and named-test coverage.
- The Phase 1 consumption and requested jcpp dependency/notice change remain honest: Phase 3 does
  not silently assume an absent dependency contract or claim unsupported GL services.
- The source-materialization, declared-uniform, macro-contribution, discovery/load,
  internal-pack, and closed `ResourceRequirements` publication contracts were rechecked without
  identifying another surviving candidate.
- No candidate was cleared on re-derivation. `candidate-001` was not settled by a prior review;
  earlier interface corrections addressed distinct contracts.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted omission is fix-up-sized and does not require rebuilding Phase 3. Round 17's distinct
noise-interface correction was resolved, but Round 18 identifies one new interface correction, so
the loop has not converged to literal PASS. The required next action is a scoped fix-up defining
the `ProgramStateModel` data contract and appending this review's `## Resolutions`. Because the
repair changes §5's binding cross-phase-interface region, the interface trigger fires and a fresh
verification round is required before Phase 3 may close.

## Resolutions

### candidate-001 — resolved

Re-derived against the Phase 3 specification's requirement that one immutable
`PackConfiguration` be the downstream truth and against §G9's exact-data-contract gate. The
omission was real and fix-up-sized.

Added §4.8's closed `ProgramStateModel` structure: dimension/program and flip-buffer key domains,
typed optional render-state fields, explicit OFF-versus-absence semantics, immutable deterministic
collections, last-valid-wins duplicate handling, malformed-line retention, and baseline absence
results. It now defines evaluation against one `OptionState` and optional selected profile,
including dimension-qualified and unqualified profile disables, and combines property enablement
with profile disablement by logical AND.

Replaced the terse §5.1 publication row with the exact Phase 4 evaluated view and Phase 5
flip-only projection. Phase 4 receives both contributing booleans and the final result, with final
false feeding the backup chain; Phase 5 sees only explicit flip overrides and cannot infer
defaults. Consumers still may not rescan source or properties.

The target received compact §0.21. The §5 interface region changed, so the manifest trigger fires:
a fresh verification round is required before Phase 3 may close.

### Notes deferred

None.
