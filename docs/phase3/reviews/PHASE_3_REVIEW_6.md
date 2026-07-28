# Phase 3 Adversarial Review — Round 6

## 0. Method and reading order

I independently re-derived every surviving candidate from, in order:

1. `docs/phase3/v1/PHASE_3_DOC.md`;
2. the selected Part I, Phase 3 specification, doc-gate, and mandatory-template material in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the relevant ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding interface region of `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the supplied candidate records and permitted supporting evidence.

Only after settling those judgments did I read
`docs/phase3/reviews/PHASE_3_REVIEW_1.md` through
`docs/phase3/reviews/PHASE_3_REVIEW_5.md`, including their resolutions. I made no deviation from
the assigned reading order, used no network access, performed no agent fan-out, and read no
forbidden source. The Gate reported no drops. `candidate-003` had been eliminated before
adjudication and was not revived.

## 1. Findings

### candidate-001 — `GeometryTranslationPlan` has no executable shared contract

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:228` and §5.1

**Claim:** Phase 4 cannot construct, and Phase 3 cannot deterministically validate, execute, or
fingerprint, the newly exposed geometry-translation plan without inventing a shared protocol.

**Evidence:** The public materializer requires a `GeometryTranslationPlan`, but the adjacent
declarations define only the materializer and its result
(`docs/phase3/v1/PHASE_3_DOC.md:229-241`). The pipeline then requires Phase 3 to determine whether
that plan is applicable, apply it at attributed rewrite sites, and retain its fingerprint while
making equal inputs byte-identical (`docs/phase3/v1/PHASE_3_DOC.md:631-635`). The ownership split
is intentional—Phase 4 chooses the translation strategy while Phase 3 performs the rewrite
(`docs/phase3/v1/PHASE_3_DOC.md:690-695`)—but §5 merely lists the plan among the exposed contracts
and repeats that split (`docs/phase3/v1/PHASE_3_DOC.md:800-805`). It supplies no variants, fields,
applicability relation, permitted rewrite operations, validation rules, or canonical fingerprint
semantics. That is a named promise rather than the specified dependent-facing contract required by
the interface-honesty rule (`docs/design/v2.0-RC3/DESIGN.md:288-293`).

**Severity:** correction. Define the minimal immutable plan protocol in the detailed design and
§5: constructible strategy data or operations, its relation to `LegacyGeometryRewriteSite`,
applicability and failure rules, deterministic transformations, and canonical fingerprint inputs.
Preserve Phase 4 ownership of strategy selection and Phase 3 ownership of validation and execution.

**Touches interface/change-trigger region:** yes.

### candidate-002 — The Phase 7 precipitation handoff is assigned a Phase 3 parser assertion

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:376`

**Claim:** The test manifest inconsistently requires Phase 3 to implement a parser assertion for a
row that defines only downstream Phase 7 render behavior.

**Evidence:** The precipitation row contains no parsed key or Phase 3 model field. It preserves the
predicate and temperature boundary as a behavior handoff, assigns ownership to Phase 7, and names
`precipitation_noneAndTemperatureBoundary` (`docs/phase3/v1/PHASE_3_DOC.md:373-377`). That
ownership agrees with the explicit boundary placing render behavior in Phase 7
(`docs/phase3/v1/PHASE_3_DOC.md:117-128`). Nevertheless, §8.1 requires every test named in
§§3.1–3.2 and requires a parser assertion for every catalog row
(`docs/phase3/v1/PHASE_3_DOC.md:933-939`); the implementation checklist independently makes the
Phase 3 dispatcher/model manifest fail on any missing §3.1/§3.2 row
(`docs/phase3/v1/PHASE_3_DOC.md:1160-1161`). No exception classifies this behavior-only row as a
downstream conformance obligation.

**Severity:** correction. Assign the named boundary test explicitly to Phase 7 and narrow the
Phase 3 parser/dispatcher manifest to rows that define Phase 3 parser or model behavior.

**Touches interface/change-trigger region:** no.

### candidate-005 — Texture format domains are neither specified nor tested

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:368` and `:423`

**Claim:** Phase 3 cannot validate the accepted internal-format, pixel-format, and pixel-type
domains required for raw custom textures and colortex directives from its current design and
conformance map.

**Evidence:** The ground truth defines a closed 37-value internal-format domain shared by
`<buf>Format` and raw custom textures, identifies the pixel-format and pixel-type domains, and
requires integer internal formats to use the integer transfer path
(`docs/research/v1/RESEARCH.md:1256-1269`). Phase 3 owns parsing all three custom-texture source
forms into its model while Phase 13 owns loading (`docs/design/v2.0-RC3/DESIGN.md:1404-1408`), and
malformed directive handling plus the validated downstream configuration remain Phase 3
responsibilities (`docs/design/v2.0-RC3/DESIGN.md:1446-1449`). The raw-texture row names fields and
tests only source forms and arity (`docs/phase3/v1/PHASE_3_DOC.md:366-368`); the colortex row names
only formats and aliases (`docs/phase3/v1/PHASE_3_DOC.md:423`). Detailed design explicitly
preserves raw numeric/type tokens (`docs/phase3/v1/PHASE_3_DOC.md:725-728`) but provides no accepted
domain, invalid-token disposition, transfer compatibility rule, or corresponding exhaustive
tests.

**Severity:** correction. Define Phase 3's typed validation and diagnostics for the authoritative
internal-format, pixel-format, and pixel-type domains, including incompatible integer-transfer
combinations, and add named conformance coverage for accepted and rejected values in both raw
textures and colortex directives. Texture decoding and GL realization remain downstream.

**Touches interface/change-trigger region:** no.

## 2. Checked and clean

- The Round 5 geometry ownership split is sound: Phase 4 selects translation policy and Phase 3
  executes the front-end rewrite. The defect is limited to the missing shared plan contract.
- The precipitation predicate and `0.15` boundary match the ground truth, and Phase 7 is the
  correct behavioral owner; only its Phase 3 test-manifest classification is inconsistent.
- Main and named screens consistently default to two columns and auto-widen only above 18 resolved
  entries, with matching 18/19 coverage and Phase 12 ownership.
- Phase 3's consumption of Phase 1's module/seam, capability-profile, logging, diagnostics,
  debug-flag, notice, and conformance contracts matches the selected binding region. The missing
  jcpp build allowance remains requested rather than assumed.
- The remaining Appendix F and Appendix A.3 mappings, engine-flag ownership, include/dimension
  behavior, standard macro families, ID-map grammar, and required reference pitfalls yielded no
  additional finding.
- `candidate-004` is cleared as a duplicate of admitted `candidate-001`. Both allege the same
  undefined `GeometryTranslationPlan`, cite the same public call and execution/interface prose,
  and require the same shared data and rewrite contract. Counting both would duplicate one repair.
- Prior-round resolutions do not settle the admitted candidates. Round 5 introduced the undefined
  plan and the precipitation handoff/test mismatch; earlier rounds did not define the texture
  token domains or their validation tests.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three findings are bounded fix-up work rather than structural rebuilds. Prior rounds have
resolved their admitted findings, but Round 6 still exposes one binding-interface defect and two
non-interface conformance/test defects, so the document has not converged to literal PASS.
The next required action is a scoped fix-up resolving all three corrections, followed by a fresh
verification round because the §5 interface/change-trigger region must change. Phase 3 may close
only after a later round returns literal `PASS` with zero blocking findings and zero corrections.

## Resolutions

### candidate-001 — resolved

Defined `GeometryTranslationPlan` as immutable, constructible strategy data: root, closed input
and output primitive enums, and positive `maxVertices`. Phase 3 now checks the plan against the
recognized `LegacyGeometryRewriteSite`, permits only its two attributed spans to change, generates
the fixed core layout declarations itself, and returns `Unavailable` for root/site/value/range or
operation mismatch. The canonical fingerprint inputs are specified independently of caller hash
or object identity, and named tests cover every primitive pair, canonical equality, and rejection
paths. §5 exposes the same protocol while retaining Phase 4 strategy selection and Phase 3
validation/execution ownership. This changes the declared interface region, so a fresh verify
round is required before Phase 3 can close.

### candidate-002 — resolved

Classified the precipitation row explicitly as a Phase 7 behavior handoff with no Phase 3
parser/model assertion. The §8 manifest and §12 implementation item now require Phase 3 assertions
only for Phase-3-owned parser/model rows while retaining downstream test obligations in the
catalog. The predicate, `0.15` boundary, and Phase 7 ownership are unchanged.

### candidate-005 — resolved

Made raw texture tokens typed and closed over the authoritative 37 internal formats, twelve pixel
formats, and complete documented pixel-type list. Unknown tokens and integer/non-integer transfer
path mismatches diagnose and ignore only that texture line; colortex directives share the same
internal-format enum. Named tests cover accepted/rejected raw domains, integer-transfer
compatibility, and accepted/rejected colortex formats. Texture decoding and GL realization remain
Phase 13 work.

### Notes deferred

None; the adjudication admitted no notes.
