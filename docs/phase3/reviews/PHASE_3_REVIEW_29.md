# Phase 3 Adversarial Review — Round 29

## 0. Method and reading order

I independently re-derived both candidates from the complete Phase 3 target, then the
manifest-selected v3 governing-design regions, RESEARCH.md, the Phase 1 binding contract, and the
candidate evidence. The permitted Pintonium and Oculus reports were not needed to decide either
candidate. Only after settling each interpretation, severity, and interface classification did I
read prior reviews 1–28, in round order and including their resolutions.

There were no deviations from the required reading order, no network use, no agent fan-out, and no
use of forbidden sources. Under the dispatched-role rule in the supplied `verify-loop` skill, I
did not invoke the verification harness or start another session. No candidate was eliminated
before adjudication, and the Gate reported no drops.

## 1. Findings

### candidate-001 — The conformance map contradicts the published texture-key algebra

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:675-680,1330-1354`.
- **Claim:** Appendix F.5's conformance row and the binding Phase 3 texture model give incompatible
  accounts of where sampler type participates in texture disambiguation.
- **Evidence:** The conformance map says that multiple texture types on one unit are modeled with
  keys `(stage,sampler,type)` and validated later
  (`docs/phase3/v1/PHASE_3_DOC.md:675-680`). The binding publication instead closes
  `TextureBindingKey` as `(stage,sampler,duplicateDiscriminator)`; only the `Raw` source variant
  carries a `TextureTarget`, while `PackPath` and `MinecraftResource` do not
  (`docs/phase3/v1/PHASE_3_DOC.md:1330-1345`). RESEARCH supplies no texture-type property-key
  segment: it says only that multiple types sharing a unit are distinguished by sampler type and
  constrained to one type per unit per program (`docs/research/v1/RESEARCH.md:1483-1489`). The
  governing Phase 13 scope likewise assigns per-unit sampler-type disambiguation to the consumer
  (`docs/design/v3/DESIGN.md:2467-2473`). Thus the defect is the map's assertion that sampler type
  is part of the Phase 3 model key, not evidence that the published key must gain a fourth
  component. Revise the row to state that Phase 3 preserves
  `(stage,sampler,duplicateDiscriminator)` and that Phases 4/13 derive sampler type from program
  declarations for later shared-unit validation; align the named coexistence test.
- **Severity:** correction.
- **Touches interface/change-trigger region:** no.

### candidate-002 — Round 28 changed binding texture semantics without the required schema bump

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:213-215,1348-1354,1478-1483`.
- **Claim:** Keeping `PackFrontEnd.CURRENT_SCHEMA_VERSION` at 2 violates the document's own
  compatibility rule after Round 28 made new texture semantics binding.
- **Evidence:** The §0.30 addendum expressly records that Round 28 closed the §5 custom/noise
  texture publication algebra and deterministic ordering
  (`docs/phase3/v1/PHASE_3_DOC.md:213-215`). That contract now fixes generated-noise absence,
  immutable list ordering, complete-key duplicate precedence, and the prohibition on inferred
  source defaults (`docs/phase3/v1/PHASE_3_DOC.md:1348-1354`). Section 5.3 nevertheless keeps the
  schema at 2 while requiring a version increment whenever a published component's meaning or
  executable default changes, absent a defined shape-stable extension mechanism
  (`docs/phase3/v1/PHASE_3_DOC.md:1478-1483`). Earlier parsing/conformance prose named source forms
  and generated noise but did not already bind the newly specified ordering, duplicate, and
  consumer-default semantics (`docs/phase3/v1/PHASE_3_DOC.md:675-681`). Increment the schema and
  update compatibility tests, or define an exact applicable shape-stable extension mechanism;
  the current text supplies neither.
- **Severity:** correction.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The finder-reported new-surface areas were rechecked. Apart from the two findings above, the new
  algebra consistently retains sidecar references, stage expansion, all source variants,
  Minecraft resource identity, noise generation/override state, and the Phase 13 ownership of
  image opening, decoding, sidecar interpretation, and GL realization.
- The remaining §5 publication surface is interface-honest against the selected Phase 1 binding
  contract: consumed dependency capabilities are exposed or explicitly requested, and the public
  aggregates retain closed shapes, defaults, ordering, failure rules, and consumer ownership.
- The examined Appendix F and Appendix A.3 families retain substantive design and named-test
  coverage. No additional conformance-map omission survived equivalent-coverage checks.
- Neither candidate was cleared by prior settled material. Round 28's resolution created the
  present closed texture algebra and explicitly retained schema version 2 on record-shape grounds;
  it neither reconciled the conflicting `(stage,sampler,type)` map row nor addressed §5.3's
  separate rule for changed meaning and executable defaults. Candidate-001 survives only as a
  non-interface conformance-text correction; the authoritative syntax does not require changing
  the published Phase 3 key.
- There were no candidates eliminated before adjudication, no Gate drops, and no findings dropped
  on independent derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded fix-up work and do not require rebuilding the architecture. Round 27
reached literal PASS, Round 28 introduced one correction and its interface fix-up, and this round
finds two defects in that new surface; convergence has therefore regressed rather than reached a
new literal PASS.

The next required action is a scoped fix-up resolving both candidates and appending this review's
`## Resolutions`. Because candidate-002 requires a consumer-compatibility change in the declared
cross-phase interface/change-trigger region, a fresh whole-document verification round is required
before Phase 3 may close.

## Resolutions

### candidate-001 — applied

Re-derived Appendix F.5 against the binding texture publication. The conformance row now preserves
`TextureBindingKey(stage,sampler,duplicateDiscriminator)` and assigns sampler-type derivation from
program declarations plus shared-unit validation to Phases 4/13. The coexistence test was aligned
as `texture_sharedUnitSamplerTypeDerivedLater`. Section 5 also states the same ownership explicitly;
the key and source-variant algebra itself was not changed.

### candidate-002 — applied

The Round 28 ordering, duplicate-precedence, generated-noise, and no-inferred-default semantics are
consumer-visible meanings/defaults under §5.3, so the exact compatibility rule requires a bump.
`PackFrontEnd.CURRENT_SCHEMA_VERSION` is now 3; live ID-mapping publication and implementation labels
were advanced to schema v3 because its nested version must equal the containing configuration.
Versions 1 and 2 are rejected without inferred upgrade, and the compatibility-test inventory retains
the current-value, incompatible-change, unsupported-version, and retained-state checks. Historical
addenda remain historical. This intentionally changes the manifest-declared §5 interface region, so
a fresh whole-document verification round is required before Phase 3 can close.

### Notes deferred

None.
