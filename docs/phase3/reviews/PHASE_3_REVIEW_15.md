# Phase 3 verification review — round 15

## 0. Method and reading order

I independently reviewed the complete Phase 3 target, the selected Part I, Phase 3 specification,
document gate, and mandatory-template material in `docs/design/v2.0-RC3/DESIGN.md`, the relevant
contract ground truth in `docs/research/v1/RESEARCH.md`, and the selected binding Phase 1 §5
contract. I re-derived each surviving candidate against the target and authorities, searched the
whole target for equivalent fingerprint construction, directive-scope, and custom-expression
handoff semantics, and consolidated duplicate candidates before consulting settled material.

Only after completing that independent judgment did I read prior reviews 1 through 14, in order,
with Round 14 read last. There were no deviations from the required reading order, no network use,
and no agent fan-out. The dispatched-role exception in the supplied `verify-loop` skill was
followed: I did not invoke the verification harness or start another session. I read no forbidden
source.

The Gate reported no drops. `candidate-004` had already been eliminated at Refute and was not
revived.

## 1. Findings

### candidate-001 — Declared-uniform catalog fingerprinting is self-referential

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:309-311`, `:385-388`, `:959-965`, and `:989`

**Claim:** The new declared-uniform contract does not define an acyclic, deterministic input from
which an implementation can construct the materialization fingerprint embedded in the catalog.

**Evidence:** `DeclaredUniformCatalog` contains a `MaterializationFingerprint materialization`
field (`docs/phase3/v1/PHASE_3_DOC.md:309-311`). The document makes equality between that field and
`MaterializedSource.fingerprint()` part of the binding data contract
(`docs/phase3/v1/PHASE_3_DOC.md:385-388`). It then requires the materialized-source fingerprint to
include the catalog schema version and “canonical catalog bytes,” while embedding the resulting
fingerprint in that same catalog (`docs/phase3/v1/PHASE_3_DOC.md:959-965`). Section 5 publishes the
same exact-equality promise to Phase 4 and Phase 6
(`docs/phase3/v1/PHASE_3_DOC.md:989`).

No canonical encoding excludes the embedded `materialization` field, and no staged,
fingerprint-free catalog payload is defined. Consequently, the hash input contains the result it
is meant to produce, leaving implementers to invent a cycle-breaking rule.

**Severity:** correction. Define a schema-versioned canonical payload containing the ordered
declaration data but excluding `DeclaredUniformCatalog.materialization`; hash that payload with
the other specified materialization inputs, then embed the completed fingerprint in both the
materialized source and catalog. State the same construction in §5 and add an independently
reconstructable deterministic fingerprint test.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The maintenance addendum's declared-uniform catalog is otherwise coherent across ownership,
  default-block scope, immutable declaration order, closed structural GLSL types, source
  attribution, unavailable-materialization behavior, downstream consumers, tests, checklist, and
  decision log.
- Phase 3 otherwise preserves the materialization, geometry-translation, discovery/load,
  preprocessing, configuration, cache-validity, and downstream-ownership contracts. Its Phase 1
  consumption remains limited to the selected binding interface.
- The finder-reported clean areas for the new catalog surface, cross-phase interfaces, and
  conformance map were checked. No additional defect survived.
- `candidate-002` is dropped as an exact duplicate of `candidate-001`: it identifies the same
  record field, recursive hash domain, equality promise, severity, interface impact, and repair.
  Admitting it separately would double-count one defect.
- `candidate-003` is dropped against settled material. Round 14 expressly re-derived and cleared
  this same colortex program-scope issue before literal PASS. The target bytes relevant to
  directive scanning and `ResourceRequirements` were not changed by §0.17, which is confined to
  declared-uniform publication. The target makes stage part of directive dispatch, ignores
  wrong-stage occurrences with a warning, preserves clear/clear-color and per-pass mipmap
  requirements, and names the corresponding directive tests. Round 15 supplies no changed
  premise that permits reopening that settled disposition.
- `candidate-005` is likewise dropped against settled material. Its Appendix F.6 rows and §5
  `CustomExpressionDecl` handoff predate §0.17; Round 14's complete-target review expressly
  cleared the Appendix F conformance map and downstream ownership before literal PASS. The
  maintenance addendum changed only declared-uniform catalog publication, not custom-expression
  declarations. The candidate therefore identifies no regression or new surface in the bytes
  owed fresh review.
- `candidate-004`, concerning `countInstances`, remained eliminated before adjudication and was
  not converted into a finding.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The one admitted defect is a bounded specification correction, not a structural miss requiring a
rebuild. Round 14 reached literal PASS; Round 15 reviews the later §0.17 interface addition and
finds one new-surface correction, so the apparent increase from zero to one is attributable to
the post-PASS maintenance change rather than loop divergence. The next required action is a
scoped fix-up resolving `candidate-001` and appending this review's `## Resolutions`. Because the
repair must clarify the binding §5 fingerprint contract, the interface change trigger fires and a
fresh verification round is required before Phase 3 may close.

## Resolutions

### candidate-001 — resolved

Re-derived from the published records and fingerprint rules, the admitted cycle was real:
`DeclaredUniformCatalog.materialization` was both an asserted output and, through unspecified
“canonical catalog bytes,” potentially part of its own hash input.

Added §0.18 and defined `CanonicalDeclaredUniformPayload(schemaVersion, declarations)` as the
fingerprint-free intermediate. Sections 2.2 and 4.10 now require a deterministic length-prefixed
encoding with fixed field/declaration order, exact symbolic tags, canonical base-10 scalars, and
UTF-8 strings; the encoding expressly excludes `DeclaredUniformCatalog.materialization`. Phase 3
hashes that payload with the other materialization inputs before embedding the single completed
fingerprint in both the materialized source and catalog. Binding §5 publishes the same staged
construction.

Added
`declaredUniformCatalog_fingerprintReconstructsFromPayloadWithoutEmbeddedResult`, which requires
an independent reconstruction from the canonical payload and other stated inputs. The existing
equality test remains, so the suite checks both acyclic construction and the two published copies.

This changes the `cross-phase-interfaces` region. A fresh verify round is required before Phase 3
can close.

### Notes deferred

None.
