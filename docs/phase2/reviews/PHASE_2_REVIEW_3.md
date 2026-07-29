# Schmaloogium — Phase 2: Conformance harness — Review Round 3

## 0. Method and reading order

I independently re-derived all three gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, including its conformance and milestone requirements.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially §5.
4. The whole target, `docs/phase2/v1/PHASE_2_DOC.md`.
5. Only after settling every candidate, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` and
   `docs/phase2/reviews/PHASE_2_REVIEW_2.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents or other agent fan-out and did not invoke the verification
harness. The canonical engine supplied the finder, refuter, steelman, and Gate material. The Gate
dropped no candidates, and no candidates were eliminated before adjudication. Forbidden sources
were not read.

Round 2 does not settle the present findings. Its correction replaced the reverse module dependency
with an engine-owned snapshot and a conformance-side adapter; candidate-001 tests the completeness
and staging of that new boundary. Round 2's clean-area conclusion about the conformance map did not
itemize the explicit compound scope requirements tested by candidate-003. Neither prior review
defines the missing process-boundary wire schemas in candidate-002.

## 1. Findings

### candidate-001 — Phase-3-only staging cannot produce the complete golden snapshot

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md` §§1.2, 4.11.2, 4.11.4, 5.4, and 12.
- **Claim:** The corrected engine-snapshot boundary is consistent with the ownership and
  implementation staging of every field required by the golden document.
- **Evidence:** The ownership table assigns per-slot compile and backup-chain resolution status to
  Phase 4 (`docs/phase2/v1/PHASE_2_DOC.md:110–114`). The mandatory golden example nevertheless
  includes program states such as `SOURCED` and `CHAIN`
  (`docs/phase2/v1/PHASE_2_DOC.md:1013–1017`), and the snapshot is required to expose the decisions
  enumerated by the complete golden structure (`docs/phase2/v1/PHASE_2_DOC.md:1061–1066`). Section
  5 then requests the snapshot API from Phase 3 but requests the per-slot resolution content from
  Phase 4 (`docs/phase2/v1/PHASE_2_DOC.md:1303–1316`). Despite that split, checklist items 22–23
  approve real golden content and run the full matrix “As soon as Phase 3 lands”
  (`docs/phase2/v1/PHASE_2_DOC.md:1850–1855`). No partial schema, unavailable state, enrichment
  stage, or Phase 4 completion gate reconciles these obligations.
- **Disposition:** Admitted. Either define a valid Phase-3-only partial projection and defer the
  Phase-4-dependent assertions, or require both Phase 3 and Phase 4 before complete real-golden
  production. Align R5, R10, the snapshot description, and §12 staging.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-002 — Process-boundary artifacts lack implementable wire schemas

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md` §§4.5.2, 4.5.4, 5.1, 8.1, and 9.1.
- **Claim:** The separately implemented writers and readers of `CapturePlan` and `RunManifest` can
  interoperate without inventing serialization syntax.
- **Evidence:** The architecture deliberately crosses a separate-JVM boundary by writing a capture
  plan and reading a run manifest (`docs/phase2/v1/PHASE_2_DOC.md:168–178`). The capture-plan
  description specifies only `key = value`, resolved values, and an ordered indexed shot block
  (`docs/phase2/v1/PHASE_2_DOC.md:608–615`); it does not define exact keys, index grammar, types,
  cardinality, escaping, required fields, or a schema version. The run-manifest table similarly
  inventories semantic blocks without defining their concrete field or repeated-record grammar,
  escaping, required/optional rules, or version behavior
  (`docs/phase2/v1/PHASE_2_DOC.md:643–659`). The shared determinism rule fixes encoding and
  canonicalization properties, not either structure
  (`docs/phase2/v1/PHASE_2_DOC.md:388–395`). Yet the acceptance tests require byte-identical plans,
  key recognition, complete block round-trips, truncation failure, and unknown-block preservation
  (`docs/phase2/v1/PHASE_2_DOC.md:1421–1432`), and both formats are Phase 2 v0.1 deliverables
  (`docs/phase2/v1/PHASE_2_DOC.md:1475–1477`). Section 5 exposes the run-manifest format and
  capture-agent contract to downstream phases (`docs/phase2/v1/PHASE_2_DOC.md:1232–1234`).
- **Disposition:** Admitted. Add normative, versioned canonical schemas or codecs with exact keys,
  field types and cardinalities, block/index and repeated-record representation, required/optional
  rules, deterministic ordering, escaping, and unknown-field/version behavior. Add canonical
  round-trip fixtures and point §5 consumers directly to the schemas.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-003 — The conformance map omits explicit Phase 2 scope requirements

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md` §§3.2–3.3.
- **Claim:** The contract conformance map covers every in-scope Phase 2 requirement with zero
  unmapped rows.
- **Evidence:** The mandatory template requires “every in-scope contract item” in the map and
  “ZERO unmapped rows” (`docs/design/v1.1/DESIGN.md:521–523`). The Phase 2 specification separately
  names per-pack tier state and reporting; deterministic setup and randomness suppression;
  per-pixel and aggregate thresholds plus GPU/driver variance; baseline storage/versioning and diff
  artifacts; cache layout and CI keys; and golden format/update workflow
  (`docs/design/v1.1/DESIGN.md:671–700`). The target itself claims zero unmapped rows
  (`docs/phase2/v1/PHASE_2_DOC.md:261–267`), but its broad rows for tier gates, automated capture,
  image diff, fixture caching, and goldens do not trace those explicit subrequirements to their
  detailed designs (`docs/phase2/v1/PHASE_2_DOC.md:287–312`). This is a traceability defect, not a
  claim that all corresponding architecture is absent; for example, reporting is substantively
  designed in §4.13 (`docs/phase2/v1/PHASE_2_DOC.md:1131–1148`).
- **Disposition:** Admitted. Expand §3 with explicit rows for each governing Phase 2
  subrequirement, pointing to its existing design and verification site. Keep the expansion limited
  to requirements stated by the governing sources.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finders found no additional inconsistency in the revised dependency direction, package
placement, capability-profile fixture ownership, illustrative-name qualification, or the separation
between week-one golden machinery and engine-produced content. Phase 2's consumption of the Phase 1
binding contract remains directionally honest: the one-way `:conformance` to `:engine` edge,
capability-profile serialization, recording/replay primitives, debug flags, fixtures, and CI
extension points are represented without recreating the reverse dependency corrected in round 2.

The App G pack rows, T0–T3 semantic definitions, initial scene-family coverage, and §9
exit-criterion-to-run mapping were spot-checked and remain substantively aligned with their
authoritative sources. All three current candidates survived independent re-derivation; none was
refuted or cleared. The Gate dropped none, and no candidate-free finding is added.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three defects are bounded fix-up work, not structural misses requiring a rebuild.
Candidates 001 and 002 require changes in the cross-phase interface/change-trigger region;
candidate-003 is confined to conformance traceability.

The loop has not converged: round 2 had two corrections and changed §5; this fresh review of that
surface finds three corrections, two interface-affecting. The next required action is a scoped
fix-up resolving all three findings and recording their resolutions here. Because §5 must change,
a fresh verify round is required before Phase 2 can close or its interface can be consumed as
verified.

## Resolutions

### candidate-001 — resolved

Re-derived the ownership split from §§1.2, 4.11, 5.4, and 12. Phase 3 can expose only the
front-end-owned portion of the engine snapshot; Phase 4 owns the per-slot resolution state present
in the mandatory complete golden. Section 4.11.4 now makes Phase 4's R10 data an enrichment of the
engine-owned snapshot, forbids approval or a matrix verdict for a partial document, and requires
both phases before complete real-golden production. R5, R10, §5.1, the staging table, tests, and
checklist items 22–23 now state the same gate. The week-one hand-built golden machinery remains
available and unchanged in purpose.

### candidate-002 — resolved

Added canonical, versioned `schmaloogium.capture-plan/1` and
`schmaloogium.run-manifest/1` schemas at their defining sections. They now fix the line grammar,
scalar lexical forms, required fields, dense repeated-record encoding, ordering, escaping,
duplicate/missing/malformed behavior, version handling, and the manifest's reserved extension
namespace. Canonical byte-round-trip fixtures and rejection cases were added to §8.1, and §5.1 now
points consumers to the named schemas rather than only the semantic block descriptions.

### candidate-003 — resolved

Expanded §3.3 with separate traceability rows for per-pack tier state/reporting, deterministic
setup/randomness suppression, threshold classes, GPU/driver variance, baseline storage/versioning,
diff artifacts, fixture cache/CI keys, and the golden format/update workflow. Each row points to its
existing design and verification site; no new substantive requirement was invented.

### Notes deferred

None. The adjudication admitted no notes.
