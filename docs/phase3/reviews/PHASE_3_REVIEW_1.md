# Phase 3 Adversarial Review — Round 1

## 0. Method and reading order

I independently re-derived the surviving candidates from, in order:

1. `docs/phase3/v1/PHASE_3_DOC.md` in full;
2. the selected governing material in `docs/design/v2.0-RC3/DESIGN.md`, especially §G9 and the
   Phase 3 specification/doc gate;
3. the relevant contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding interface region of `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the supplied supporting-evidence and candidate records.

There were no prior Phase 3 reviews to read last. I made no deviation from the assigned reading
order, used no network access, and performed no agent fan-out. I did not read any forbidden source.

The Gate dropped `candidate-001` because two refutation citations quoted through line 151 while
declaring lines 139–150. I did not revive it or use it as a finding.

## 1. Findings

### candidate-002 — `PackConfiguration` omits its promised `schemaVersion`

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:725`

**Claim:** Consumers cannot implement the published schema-version retention and compatibility
rules from the declared `PackConfiguration` contract.

**Evidence:** The public shape at `docs/phase3/v1/PHASE_3_DOC.md:153-164` ends with
`ConfigurationFingerprint fingerprint` and exposes no schema version. The fingerprint does include
parser schema version among its hash inputs (`docs/phase3/v1/PHASE_3_DOC.md:669-671`), but the
consumer rule separately requires “fingerprint/version equality”
(`docs/phase3/v1/PHASE_3_DOC.md:705-707`). Most decisively, the binding interface discipline says
that `PackConfiguration` carries both `schemaVersion` and a fingerprint and assigns schema bumps
compatibility meaning (`docs/phase3/v1/PHASE_3_DOC.md:723-728`). Because the governing design makes
`PackConfiguration` the sole downstream output (`docs/design/v2.0-RC3/DESIGN.md:1446-1449`), an
opaque fingerprint incorporating the parser version does not satisfy the separately promised
consumer-visible value.

**Severity:** correction. This is a precise, locally repairable contract contradiction rather than
a structural failure. Synchronize §2 and §5 around one implementable rule: either add a typed
schema-version component and define comparison semantics, or consistently make fingerprint
equality the sole retention mechanism and remove the separate version/schema-bump promises.

**Touches interface/change-trigger region:** yes.

### candidate-003 — The conformance map is not exhaustive for owned §3 contract families

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:234`

**Claim:** The mandatory conformance map does not map every in-scope RESEARCH §3/App contract item
to a satisfying design element and provenance.

**Evidence:** The governing template requires “every in-scope contract item” and “ZERO unmapped
rows” (`docs/design/v2.0-RC3/DESIGN.md:804-808`). Phase 3 expressly owns discovery/root/archive
semantics, source attribution, the macro/preprocessor environment, and unresolved ID mappings
(`docs/phase3/v1/PHASE_3_DOC.md:67-84`). Its map thoroughly covers Appendix F, Appendix A.3, and
selected dimension/include and reference decisions, but that partial coverage does not exhaust the
owned interoperability surface. In particular, RESEARCH specifies the standard macro families and
preprocessor operations across shader, properties, and ID-mapping inputs
(`docs/research/v1/RESEARCH.md:312-321`) and multiple ID-mapping forms, mod contributions, render
layers, and macro restrictions (`docs/research/v1/RESEARCH.md:453-462`). These families lack
explicit §3 conformance rows. Detailed design and test descriptions elsewhere do not substitute
for the mandatory traceability table.

**Severity:** correction. Expand §3 with the missing owned contract rows and precise provenance,
preserving the existing comprehensive Appendix F and Appendix A.3 mappings. This is a document-gate
repair and does not itself require changing §5.

**Touches interface/change-trigger region:** no.

## 2. Checked and clean

- The document contains all thirteen mandatory sections with substantive content.
- Appendix F.1 ownership is complete; Appendix F.2–F.8 and Appendix A.3 are comprehensively mapped
  with named tests.
- The four mandated Pintonium pitfalls, jcpp adoption, option-3-shaped identity architecture,
  OQ-7 spike, pure-`:engine` placement, and the reserved `centerDepthSmooth` contribution point are
  explicitly addressed.
- Phase 3 does not silently assume the missing jcpp dependency allowance: it requests the Phase 1
  build-contract change. Its consumed Phase 1 runtime contracts are supported by the dependency's
  binding §5, and it claims no unsupported GL service or handle.
- `candidate-001` remains cleared only by the Gate's fail-closed citation rejection and is not an
  admitted finding.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted findings are fix-up-sized; neither requires rebuilding the architecture. There is no
prior-round trend on this first review, so convergence cannot yet be assessed. Because
`candidate-002` affects the binding cross-phase interface/change-trigger region, the required next
action is a scoped fix-up resolving both corrections, followed by a fresh verification round before
Phase 3 may close.

## Resolutions

### candidate-002 — resolved

Re-derived the contradiction across the public record, reload rule, fingerprint definition, and
§5 interface discipline. Added `int schemaVersion` to `PackConfiguration`, made retention require
equality of both the schema version and content fingerprint, and defined which compatibility
changes require a bump. This changes the declared cross-phase interface, so Phase 3 requires a
fresh verification round before closure.

### candidate-003 — resolved

Re-swept the Phase 3-owned RESEARCH §3 families against the existing conformance tables. Added
explicit rows for discovery/root/archive safety, source attribution, every standard macro family,
the preprocessing operation/input contract, restricted ID-map macros, all ID-rule forms, mod
contributions, and layer rules. Existing Appendix F, Appendix A.3, dimension, and include rows were
retained rather than duplicated.

### Notes deferred

None.
