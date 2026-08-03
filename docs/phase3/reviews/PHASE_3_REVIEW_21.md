# Phase 3 Adversarial Review — Round 21

## 0. Method and reading order

I first re-derived both candidates from the complete target, the manifest-selected governing-design regions, RESEARCH.md, and the Phase 1 binding contract. I used the supplied Pintonium and Oculus reports only as supporting evidence; neither was needed to decide the surviving issue. I then read prior reviews 1–20 last, as required, to distinguish settled material from the new §0.23 surface. No forbidden source, transcript, implementation tree, or network source was read. There were no deviations, no network use, no agent fan-out, no eliminated candidates, and no Gate drops.

The two supplied candidates identify the same omission at the same public surface. Candidate-001 is admitted in narrowed form below. Candidate-002 is dropped as an exact duplicate, not cleared on the merits and not counted again.

## 1. Findings

### candidate-001 — The ID-mapping interface does not define the origin and predicate values Phase 9 must use

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:315-370`, with the binding publication at `docs/phase3/v1/PHASE_3_DOC.md:1242`.
- **Claim:** The schema-v2 ID-mapping surface is not yet a closed executable contract for Phase 9. Phase 9 must construct an origin for mod-contribution parse requests and interpret property predicates during registry resolution, but `MappingOrigin` and `PropertyPredicate` have no declared public shapes, variants, construction rules, or equivalent normative semantics anywhere in the target.
- **Evidence:** `IdMappingParseRequest` requires `MappingOrigin origin` (`docs/phase3/v1/PHASE_3_DOC.md:332-339`), and both public rule records expose `List<PropertyPredicate>` and `MappingOrigin` (`docs/phase3/v1/PHASE_3_DOC.md:350-370`). The detailed protocol expressly says that Phase 9 supplies an origin (`docs/phase3/v1/PHASE_3_DOC.md:1137-1145`) and owns registry lookup, precedence, and alias resolution while consuming ordered property predicates and provenance (`docs/phase3/v1/PHASE_3_DOC.md:1176-1184`). Section 5 publishes these mapping types to Phase 9 as part of the complete publication surface (`docs/phase3/v1/PHASE_3_DOC.md:1223-1242`). A whole-target search finds uses of `MappingOrigin` and `PropertyPredicate`, but no declaration or equivalent closed semantic definition. This prevents Phase 9 from implementing the promised construction and interpretation behavior without inventing Phase 3 semantics.
- **Scope of correction:** Define the closed loader-neutral `PropertyPredicate` algebra, including its ordered/canonical content and matching-relevant validation semantics, and define the supported `MappingOrigin` constructor/factory or closed variants plus the identity required for attribution and precedence. Bind those definitions in §5. `IdMappingMacroEnvironment` and `IdMappingFileFingerprint` need not expose their internal representations if the contract instead states that Phase 3 issues immutable opaque values and specifies the consumer-visible validity, equality, and permitted operations needed by Phase 9.
- **Severity:** correction.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- Re-derived and found internally aligned: schema version `2`; per-kind `ABSENT` / `PRESENT_EMPTY` / `PRESENT_RULES`; isolated forced-11300 entity results; selector order and kind; mapping-era provenance; state derivation; parser failure containment; milestone, tests, hand-off, and checklist coverage. The former flattened mapping aggregate is not retained elsewhere.
- The existing discovery/load, materialization, declared-uniform, macro-contribution, option, resource-requirement, internal-pack-source, schema-versioning, and consumed Phase 1 contracts showed no new interface defect under the supplied lenses.
- The conformance map covers the examined Appendix F keys, Appendix A.3 directives, engine-flag owners, named reference pitfalls, and remaining owned Phase 3 contract families; no separate conformance finding survives re-derivation.
- Candidate-002 is dropped solely because it duplicates candidate-001's location, claim, evidence, severity, and repair. Its substance is represented by the single admitted finding.
- Prior reviews do not settle this defect: round 20 passed the pre-§0.23 surface, while §0.23 introduced the ID-mapping contracts now at issue. Earlier corrections concerning other executable public aggregates and fingerprints are distinct.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The document needs one bounded interface correction; no structural rebuild is required. Convergence is not regressing: the sole issue is localized to the newly added §0.23 maintenance surface, and the two reported candidates collapse to one defect. Apply the correction, append its resolution to this review, and because the repair changes §5's cross-phase interface, run a fresh verification round before Phase 3 can close. Literal PASS is unavailable in this round.

## Resolutions

### candidate-001 — resolved

Re-derived the gap from the published signatures and Phase 9 hand-off. Added a closed
`PropertyPredicate(propertyName, acceptedValues)` value with source/canonical order, OR-within and
AND-between matching, missing-property behavior, and fail-closed validation. Added closed pack and
mod `MappingOrigin` records with validated construction, record equality, diagnostic attribution,
and a Phase-9-assigned per-mod contribution ordinal. Phase 3 exposes those identity inputs but
still does not own or evaluate pack/mod or inter-mod precedence. The declarations are in §2.2,
their executable semantics in §4.9, their binding publication in §5.1, and named coverage in §8.1.

This changes the `cross-phase-interfaces` region. Per the manifest trigger, Phase 3 remains
unverified and requires a fresh verification round before it can close.

### Notes deferred

None; the adjudicator admitted no notes.
