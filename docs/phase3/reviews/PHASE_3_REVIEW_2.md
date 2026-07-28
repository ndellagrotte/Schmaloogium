# Phase 3 Adversarial Review — Round 2

## 0. Method and reading order

I independently re-derived every candidate from, in order:

1. `docs/phase3/v1/PHASE_3_DOC.md` in full;
2. the selected governing material in `docs/design/v2.0-RC3/DESIGN.md`, including Part I, §G9,
   and the Phase 3 specification/doc gate;
3. the relevant contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding interface region of `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the supplied candidate records and permitted supporting evidence.

Only after forming those judgments did I read
`docs/phase3/reviews/PHASE_3_REVIEW_1.md`, including its resolutions. I made no deviation from the
assigned reading order, used no network access, performed no agent fan-out, and read no forbidden
source. The Gate reported no drops, and no candidate had been eliminated before adjudication.

## 1. Findings

### candidate-001 — Schema-version contract lacks a current value and executable compatibility rule

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:743`

**Claim:** Producers and consumers cannot implement the public schema-version contract
consistently because it defines evolution categories without defining the current version or the
compatibility predicate.

**Evidence:** `PackConfiguration` publicly exposes `int schemaVersion`
(`docs/phase3/v1/PHASE_3_DOC.md:158-170`), and consumers may retain derived state only when both
that value and the fingerprint match (`docs/phase3/v1/PHASE_3_DOC.md:725-727`). The interface
discipline calls it a non-negative parser-schema constant, distinguishes additive changes from
changes requiring a bump, and requires rejection of incompatible versions
(`docs/phase3/v1/PHASE_3_DOC.md:743-751`), but it supplies neither an initial/current value nor a
precise supported-version relation. The publication tests cover atomicity and fingerprint changes,
not schema publication or unsupported-version rejection
(`docs/phase3/v1/PHASE_3_DOC.md:843-849`). Phase 3 owns the sole validated
`PackConfiguration` output (`docs/design/v2.0-RC3/DESIGN.md:1325-1329`), so this cannot be left for
consumers to choose independently.

**Severity:** correction. Define a named current schema constant and initial value, an exact
accept/reject predicate, and named tests for current-value publication, additive no-bump behavior,
an incompatible bump, unsupported-version rejection, and schema-mismatch retention invalidation.
An exhaustive test for every hypothetical future field change is unnecessary.

**Touches interface/change-trigger region:** yes.

### candidate-002 — `InternalPackSource` has no implementable cross-phase contract

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:173`

**Claim:** Phase 7 cannot independently implement the `(internal)` pack provider required by Phase
3 because the Phase 3-owned interface has no callable shape or behavioral semantics.

**Evidence:** `PackLoadRequest` requires an `InternalPackSource`, but the public-shape discussion
only calls it an engine interface returning named immutable byte sources
(`docs/phase3/v1/PHASE_3_DOC.md:144-175`). Pack discovery selects that source for `(internal)` and
says Phase 7 supplies bytes and stable identity (`docs/phase3/v1/PHASE_3_DOC.md:400-402`). Yet §5,
which claims to be the complete publication surface, provides only “in-memory source-provider
seam, no MC types” (`docs/phase3/v1/PHASE_3_DOC.md:701-723`). It defines no method or entry type,
normalization and lookup/enumeration behavior, immutable-byte ownership, missing-entry result,
deterministic ordering, or failure reporting. The governing template requires load-bearing
signatures, exact semantics, and named cross-phase data contracts
(`docs/design/v2.0-RC3/DESIGN.md:802-813`). Phase 7 owns the content, not the protocol that Phase 3
declares as its interface.

**Severity:** correction. Define the smallest engine-only interface that the loader needs,
including its exact method and value types, stable identity representation where needed,
normalized entry semantics, byte ownership/lifetime, observable ordering, absence behavior, and
failure/diagnostic behavior; align §2 and §5.

**Touches interface/change-trigger region:** yes.

### candidate-003 — Appendix A.3 mappings drop shader-stage restrictions

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:327`

**Claim:** The directive scanner can populate invalid requirements because its conformance mapping
does not preserve the authoritative shader-stage qualifiers.

**Evidence:** The authoritative table restricts extended attributes and `countInstances` to vertex
shaders and the legacy geometry pair to geometry shaders
(`docs/research/v1/RESEARCH.md:1158-1160`). Phase 3 maps those directives to fields and positive
tests but omits all three qualifiers (`docs/phase3/v1/PHASE_3_DOC.md:327-329`). Its general
table-driven validation rules supply no equivalent stage check
(`docs/phase3/v1/PHASE_3_DOC.md:603-608`), despite the source model distinguishing `.vsh`, `.fsh`,
and `.gsh` (`docs/phase3/v1/PHASE_3_DOC.md:422-428`). Downstream consumers cannot repair the
result because they are forbidden to rescan directives
(`docs/phase3/v1/PHASE_3_DOC.md:719-726`).

**Severity:** correction. Permit attribute opt-ins and `countInstances` to populate requirements
only from `.vsh`, and legacy geometry configuration only from `.gsh`; define wrong-stage
ignore/diagnostic behavior and add named negative cases for each family.

**Touches interface/change-trigger region:** no.

## 2. Checked and clean

- The Round 1 additions for discovery/root/archive safety, source attribution, standard macro
  families, preprocessing inputs, ID-map forms and restrictions, mod contributions, and layer
  rules have substantive design coverage and named tests.
- Appendix F is comprehensively mapped; all four mandated Pintonium pitfalls remain explicit; the
  identity macro model remains option-3-shaped with OQ-7 open; and the
  `centerDepthSmooth` contributor slot remains reserved.
- Phase 3's consumed Phase 1 runtime contracts are present in the dependency's binding §5. The
  missing jcpp build-contract allowance is requested rather than silently assumed.
- The other principal consumer surfaces have consistent downstream ownership; no additional
  cross-phase interface defect survived equivalent-coverage checks.
- Round 1's resolved omission of the `schemaVersion` field does not clear candidate-001: the field
  now exists, but its value, compatibility predicate, and executable tests remain unspecified.
  Round 1's conformance-map resolution also does not clear candidate-003 because it did not settle
  the directive-stage qualifiers.
- No candidate was refuted or cleared on re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three findings are localized fix-up work; none requires rebuilding the architecture. Round 1
closed two earlier corrections, but Round 2 exposes three distinct remaining defects, so the
document has not converged to literal PASS. Because candidates 001 and 002 change the binding
cross-phase interface/change-trigger region, the next required action is a scoped fix-up resolving
all three corrections, followed by a fresh verification round before Phase 3 may close.

## Resolutions

### candidate-001 — resolved

Re-derived from the public `schemaVersion` field, the retention rule, and Phase 3's ownership of
the sole validated output. §2.2 and §5.3 now define
`PackFrontEnd.CURRENT_SCHEMA_VERSION = 1`, require this revision to publish `1`, and make exact
equality the supported-version predicate. Additive optional fields do not bump the version;
removal, semantic change, or executable-default change does. §8.1 names tests for current
publication, additive no-bump behavior, an incompatible bump, unsupported-version rejection, and
schema-mismatch retention invalidation.

### candidate-002 — resolved

Re-derived against the transactional snapshot loader and the §5 publication requirement. §2.2 now
defines the minimal engine-only provider: stable `PackIdentity`, lookup by validated
`NormalizedPackPath`, `Optional` absence, a typed provider-read failure, and
`InternalPackEntry`. Returned bytes are defensive copies owned by Phase 3; no provider storage
survives publication. Phase 3 owns deterministic normalized indexing, so the provider exposes no
unneeded enumeration protocol. §5.1 now publishes these exact contracts and semantics for Phase 7.

### candidate-003 — resolved

Re-derived directly from RESEARCH Appendix A.3. §3.3 now qualifies extended attributes and
`countInstances` as `.vsh`-only and the legacy geometry pair as `.gsh`-only, with a named negative
test for each family. §4.7 makes stage part of the scanner table key: a recognized wrong-stage
occurrence is ignored, emits one source-attributed warning, and cannot replace a valid value.

### Notes deferred

None. The adjudicator admitted no notes, and all three corrections were locally resolvable without
a new design decision or an authority conflict.
