# Phase 3 Adversarial Review — Round 25

## 0. Method and reading order

I independently re-derived both candidates from the complete Phase 3 target, then the
manifest-selected governing-design regions, RESEARCH.md, the Phase 1 binding contract, and the
candidate evidence. The permitted Pintonium and Oculus reports were not needed to decide either
candidate. Only after settling each interpretation, severity, and interface classification did I
read prior reviews 1–24, in round order and including their resolutions.

There were no deviations from the required reading order, no network use, no agent fan-out, and no
use of forbidden sources. Under the dispatched-role rule in the supplied `verify-loop` skill, I did
not invoke the verification harness or start another session. No candidate was eliminated before
adjudication, and the Gate reported no drops.

## 1. Findings

### candidate-001 — Detailed construction gives an obsolete exhaustive account of optional absence

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1073-1076`.
- **Claim:** The §0.27 change to optional shadow FOV is not propagated consistently into §4.7's
  construction semantics.
- **Evidence:** Section 4.7 says that absence is represented only by an empty collection,
  “`Optional.empty()` for no legacy geometry pair,” or an explicit typed baseline
  (`docs/phase3/v1/PHASE_3_DOC.md:1073-1076`). The sole binding contract also assigns
  `fov=Optional.empty()` to the orthographic shadow baseline, while separately assigning
  `Optional.empty()` to absent program geometry (`docs/phase3/v1/PHASE_3_DOC.md:1346-1352`). The
  §4.7 sentence is therefore false as an exhaustive explanation of producer construction even
  though §5 remains the correct consumer contract.
- **Severity:** correction. Amend §4.7's absence sentence so that `Optional.empty()` covers both
  orthographic shadow FOV and absence of a legacy geometry pair. Leave §5's already-correct
  baseline unchanged.
- **Touches interface/change-trigger region:** no.

### candidate-002 — Present shadow FOV has no binding unit or projection-valid domain

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1336-1344`.
- **Claim:** A Phase 8 consumer cannot implement the perspective-shadow projection from a present
  `ShadowRequirements.fov` without guessing its numeric interpretation.
- **Evidence:** The sole binding contract publishes `ShadowRequirements(...,Optional<Float> fov,... )`
  but constrains the FOV only through the generic rule that all otherwise unlisted floats are
  finite; it then claims those are the complete consumer-visible scalar bounds and units
  (`docs/phase3/v1/PHASE_3_DOC.md:1336-1344`). It defines only the absent case as orthographic
  (`docs/phase3/v1/PHASE_3_DOC.md:1346-1349`). RESEARCH requires perspective projection when
  `shadowMapFov` is set (`docs/research/v1/RESEARCH.md:569-572`). Thus Phase 8 must guess at least
  the angular unit and admissible perspective domain for a present value. Section 5 expressly
  reserves consumer interpretation to itself and classifies changes to that interpretation as
  interface changes (`docs/phase3/v1/PHASE_3_DOC.md:1354-1357`).
- **Severity:** correction. In §5, define empty as orthographic and present as perspective, with
  the normalized angular unit, exact accepted range, and endpoint semantics. Align §4.7's producer
  validation and malformed/out-of-range disposition with that published domain, and add unit and
  boundary conformance cases.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The finder-reported new-surface, interface, and conformance areas were rechecked. The evaluated
  `ProgramStateModel` shape and ordering remain consistent between §§4.8 and 5.1; attachment
  creation consistently uses transparent black; schema version remains 2; and the closing status
  correctly records that §0.27 requires this fresh review.
- Apart from candidate-002, the `ResourceRequirements` record graph, baselines, scalar rules,
  collection ordering, and consumer projections are coherent. Phase 3 otherwise consumes only
  contracts present in the selected Phase 1 binding region, and the jcpp build requirement remains
  an explicit dependency request rather than an assumption.
- The examined Appendix F and Appendix A.3 mappings have equivalent detailed coverage; no separate
  conformance omission survives re-derivation.
- Neither candidate was refuted or cleared. Candidate-001 is a stale explanatory statement outside
  §5, not a competing interface change. Candidate-002 is a distinct present-value interpretation
  gap; Round 24's correction settled only the absent orthographic representation and did not define
  a present FOV's unit or domain. No earlier review settled either current defect.
- There were no candidates eliminated before adjudication and no findings dropped on derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded fix-up work and do not require rebuilding the architecture. Round 24's
three corrections repaired the §0.27 surface, but this round finds one stale construction statement
and one incomplete consumer interpretation introduced or exposed by that repair. The surface has
therefore not converged to literal PASS.

The next required action is a scoped fix-up resolving both candidates and appending this review's
`## Resolutions`. Because candidate-002 requires a change inside the declared cross-phase
interface/change-trigger region, a fresh whole-document verification round is required before
Phase 3 may close.

## Resolutions

### candidate-001 — resolved

Re-derived §4.7's absence inventory against the binding baselines and amended it so
`Optional.empty()` covers both orthographic shadow FOV and absence of a legacy geometry pair. The
already-correct §5 baseline remains empty and orthographic.

### candidate-002 — resolved

The documented `SHADOWFOV:90.0`/`shadowMapFov = 90.0` forms are normalized as degrees, and the
required perspective projection needs an angle strictly inside its nondegenerate 0–180-degree domain.
Section 5 now binds empty to orthographic projection and present to a finite degree value strictly
between 0 and 180, with both endpoints invalid. Sections 3.3 and 4.7 apply the existing malformed-
occurrence rule: invalid values warn, are ignored, and retain the prior/baseline value. Section 8
adds named acceptance and boundary cases for every syntax form, including non-finite values.

This intentionally changes the declared cross-phase interface region by closing the consumer
meaning of the existing `fov` component; the record shape and schema version remain unchanged. A
fresh whole-document verification round is required before Phase 3 can close.

### Notes deferred

None; the adjudicator admitted no notes.
