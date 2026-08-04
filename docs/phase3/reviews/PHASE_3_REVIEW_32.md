# Phase 3 Adversarial Review — Round 32

## 0. Method and reading order

I independently re-derived the sole surviving candidate from the complete Phase 3 target, the
manifest-selected `v2.0-RC3` governing-design regions, RESEARCH.md contract ground truth, the
Phase 1 binding contract, and the supplied candidate evidence. The permitted supporting evidence
was not needed to decide the candidate. Only after settling its interpretation, severity, and
interface classification did I read prior reviews 1–31, in round order and including their
resolutions.

There were no deviations from the required reading order, no network use, no agent fan-out, and no
use of forbidden sources. Under the dispatched-role rule in the supplied `verify-loop` skill, I
did not invoke the verification harness or start another session. The Gate dropped
`candidate-001` because its quoted finder evidence did not resolve; I did not reconstruct or admit
it. No other candidate was eliminated before adjudication.

## 1. Findings

### candidate-002 — `MaterializedSource` does not expose the shader text Phase 4 must compile

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` §2.2, §4.5, and §5.1

**Claim:** Phase 3 publishes a successful materialization to Phase 4 without binding any component
or accessor that yields the exact final transformed GLSL source. A downstream implementation must
therefore invent part of the cross-phase carrier contract.

**Evidence:** The governing Phase 3 objective expressly owns source preprocessing
(`docs/design/v2.0-RC3/DESIGN.md:1325-1329`). The public operation returns an otherwise undefined
`MaterializedSource` on success (`docs/phase3/v1/PHASE_3_DOC.md:514-526`). The detailed pipeline
says that transformed text is retained in that object (`docs/phase3/v1/PHASE_3_DOC.md:1001-1002`),
and the document binds named projections for its source map, declaration catalog, and fingerprint
(`docs/phase3/v1/PHASE_3_DOC.md:608-615`, `:879-880`). However, binding §5 publishes
`MaterializedSource` to Phase 4 while specifying only its geometry and declaration metadata and no
text projection (`docs/phase3/v1/PHASE_3_DOC.md:1394-1395`). The whole-document search found no
equivalent transformed-text component or accessor. Phase 4 cannot compile the promised output
without guessing how to retrieve it.

**Severity:** correction. Bind an immutable `MaterializedSource` component or accessor that
returns the exact final transformed GLSL text Phase 4 passes to compilation, preferably as a Java
`String`, and associate it unambiguously with the published `SourceMap`. Specify a character/byte
encoding only if the public representation exposes bytes. Restating unrelated metadata is not
required.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The finder-reported new-surface area is internally consistent on custom-expression domains,
  immutable source order, duplicate retention, ordinals, attribution, empty-list behavior,
  fingerprint field ordering, Phase 11 ownership, schema-version references, and named coverage.
- The remaining cross-phase surface is coherent against the selected Phase 1 binding contract.
  In particular, the Phase 1 contracts named as consumed are present, and Phase 3 requests the
  absent jcpp build declaration instead of assuming it.
- The conformance maps cover the in-scope Appendix F keys and Appendix A.3 directives, including
  the four mandatory Pintonium pitfalls, with corresponding detailed semantics and named tests.
- Prior reviews do not settle or displace `candidate-002`. Round 4 repaired the distinct absence
  outcome of `SourceMaterializer`, and Round 15 repaired fingerprint construction; neither defined
  access to the materialized compilation text. Earlier literal-PASS rounds therefore do not clear
  this independently re-derived omission.
- Gate-dropped `candidate-001` remains excluded as unverifiable evidence and was not converted into
  a finding.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The sole surviving candidate is admitted as one interface correction. The previously reported
zero-correction convergence is reopened by this independently verified consumer-visible omission;
there is no structural rebuild condition and therefore no basis for FAIL.

The next required action is a scoped fix-up for `candidate-002`, with a resolution appended to
this review. Because the correction changes the manifest-declared cross-phase interface region, a
fresh whole-document verification round is required before Phase 3 can close.

## Resolutions

### candidate-002 — corrected

Re-derived against the Phase 3 preprocessing objective and the existing successful-materialization
contract. `MaterializedSource.transformedText()` now binds an immutable Java `String` containing
the exact final transformed GLSL text that Phase 4 passes to compilation, while
`MaterializedSource.sourceMap()` is explicitly the map for that same string and resolves its
generated numeric `#line` file identifiers. Because the public representation is characters, no
byte encoding was introduced.

The correction is applied consistently in §2.2, binding §5.1, §8's named test coverage, and §12's
implementation checklist, with compact §0.34 reporting. The §5.1 edit intentionally changes the
manifest-declared cross-phase interface region, so a fresh whole-document verification round is
required before Phase 3 can close.

### Notes deferred

None; the adjudicator admitted no notes.
