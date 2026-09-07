# Phase 3 verification review — round 51

## 0. Method and reading order

I independently re-derived every surviving candidate before consulting historical reviews. The
load-bearing sources were:

1. `docs/phase3/v1/PHASE_3_DOC.md` as a whole, with focused checks of the canonical §2.2
   declarations, compatibility and standard-macro design, the complete manifest-declared §5
   interface region, load validation/failure semantics, tests, decisions, hand-offs, and checklist;
2. the manifest-selected regions of `docs/design/v3/DESIGN.md`: Part I, the mandatory template,
   the Phase 3 target specification, and the document gate;
3. `docs/research/v1/RESEARCH.md` as contract ground truth, especially the standard macro header
   and Appendix F.2 minimum-edition rule;
4. the binding Phase 1 contract in `docs/phase1/v14/PHASE_1_DOC.md`, including the incorporated
   `EngineDiagnostic` and `DiagnosticReporter` declarations; and
5. the permitted Pintonium and Oculus reports where needed to test whether reference evidence
   supplied a missing macro or edition-ordering rule.

I searched the complete target and authorities for null-reporter handling, exhaustive load-failure
classification, `MC_VERSION` arithmetic and bounds, runtime-identity string grammars and
projections, and provenance for edition comparison. Only after settling interpretation, severity,
interface impact, and duplication did I read all discovered prior reviews
`PHASE_3_REVIEW_1.md` through `PHASE_3_REVIEW_50.md`, in round order and including their
resolutions, as historical settled material.

There were no source-list deviations, no network use, and no agent fan-out. I did not invoke
`$verify-loop`, run `scripts/verify`, start another session, or read a forbidden transcript,
`docs/**/chatlogs/**` path, or `*.txt` source. The Gate reported no drops. Candidate-002 was
eliminated at Refute before adjudication and was not revived. Candidate-004 is consolidated with
candidate-001 below because both state the same null-reporter contradiction and order the same
repair.

## 1. Findings

### candidate-001 — Null diagnostics makes the exact-once failure-reporting contract impossible

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §2.2 lines 519–528 and §5.1 lines
  2756–2788; `docs/phase1/v14/PHASE_1_DOC.md` lines 3783–3785.
- **Claim:** Every public non-`Off` request must have one implementable result and diagnostic-delivery
  outcome, including a request whose `diagnostics` component is null.
- **Evidence:** `PackLoadRequest` is a plain public record with a direct `DiagnosticReporter`
  component and no null-rejecting constructor or fallback receiver. The binding load contract
  expressly says null required data, including diagnostics, returns `Failed(INVALID_REQUEST)` rather
  than throwing. The same contract then requires the exact primary diagnostic to be delivered once
  to the supplied reporter before every `Failed` result, and the named failure test repeats exact
  single delivery. Phase 1 exposes delivery only as the instance call
  `DiagnosticReporter.report(EngineDiagnostic)`; it supplies no static, implicit, or null-object
  sink. No implementation can perform the required callback on the null component whose presence
  the earlier branch expressly admits.
- **Severity:** correction. Define the null-reporter branch explicitly. The least disruptive repair
  is to return `Failed(INVALID_REQUEST)` with its non-null primary diagnostic, perform no external
  callback when no receiver exists, scope exact-once correspondence to non-null supplied reporters,
  and add a no-throw/no-I/O/null-reporter test. A constructor-level rejection is viable only if the
  current load-time non-throwing promise is deliberately reconciled.
- **Touches interface/change-trigger region:** yes. The correction changes the binding §5.1 load
  semantics consumed by Phases 7 and 12.

### candidate-003 — The accepted runtime domain does not determine an exact `MC_VERSION`

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §2.2 lines 529–536, §4.4 lines
  1725–1727, §5.1 lines 2510–2515 and 2760–2764, and §8.1 lines 2997–3001.
- **Claim:** Every valid `RuntimeIdentityData` tuple must produce one exact standard-header
  `MC_VERSION` replacement or one exact pre-I/O rejection.
- **Evidence:** The public tuple carries three independent `int` components, and §5 accepts every
  non-negative value. The macro design gives only the ordinary examples `1.9.4 -> 10904` and
  `1.12.2 -> 11202` plus the label “five-digit ... formatting algorithm.” It does not define
  arithmetic versus concatenation, minor/patch width, zero-major handling, checked overflow,
  canonical decimal serialization, or rejection timing for accepted tuples such as `(1,100,0)` or
  `(Integer.MAX_VALUE,0,0)`. The adjacent GL/GLSL rules do specify formulas, bounds, overflow, and
  failure timing, while the test inventory names only the 1.12.2 MC example. RESEARCH requires the
  10904-format macro, and the governing design assigns its construction to Phase 3 while requiring
  reference values to be re-derived. The project's normal 1.12.2 caller convention cannot narrow a
  broader published input contract silently.
- **Severity:** correction. Either bind non-`Off` requests to exactly `(1,12,2)` and emit ASCII
  decimal `11202`, or specify checked arithmetic, exact component bounds, canonical serialization,
  overflow disposition, and pre-I/O invalid-request timing for the broader domain. Add accepted
  boundary, first-invalid, and overflow vectors alongside `macro_mcVersion11202Format`.
- **Touches interface/change-trigger region:** yes. The repair changes either §5.1
  `RuntimeIdentityData` validity or the consumer-visible macro replacement semantics.

### candidate-005 — `PackLoadFailureCode` lacks an exhaustive cause mapping and precedence

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §5.1 lines 2781–2788, §6 lines
  2905–2910, and §8.1 lines 3124–3128.
- **Claim:** Phase 3 producers and Phase 7/12 consumers must be able to assign and interpret every
  closed failure code without guessing between overlapping failure classes.
- **Evidence:** Section 5 enumerates eight codes and gives exact outcomes for selected request,
  selection, and internal-source failures, but supplies no exhaustive mapping for
  `INPUT_UNREADABLE`, `INPUT_UNSAFE`, `INPUT_LIMIT_EXCEEDED`, `STRUCTURALLY_UNUSABLE`, and
  `UNEXPECTED_INTERNAL`. Section 6 groups corrupt/unreadable archives, unsafe paths, limit breaches,
  and absence of usable shader roots under an unspecified failed result. Its definition of
  “structurally unusable” additionally includes inability to be bounded or read safely, directly
  overlapping three dedicated input codes. Neither pipeline order nor similarly named discovery
  statuses makes a load-boundary classification or multi-defect precedence binding. The named test
  exercises every enum value's diagnostic transport, not representative causes or arbitration.
- **Severity:** correction. Add a binding cause-to-code matrix and deterministic specificity or
  first-failure precedence, including corrupt archive versus provider I/O, unsafe input, every
  finite-limit breach, internal-provider invalidity, safely read bounded input with no usable source
  configuration, and unexpected implementation exceptions. Narrow `STRUCTURALLY_UNUSABLE` to avoid
  overlap and assert exact codes for representative and multi-cause tests.
- **Touches interface/change-trigger region:** yes. This completes consumer-visible semantics of
  the closed failure algebra in §5.1.

### candidate-006 — Runtime identity strings lack executable grammars, comparison, and macro projection

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §2.2 lines 529–536, §4.4 lines
  1724–1732 and 1775–1779, §4.8 lines 2089–2096, and §5.1 lines 2760–2764.
- **Claim:** Later `:mod` code must be able to construct valid runtime identity data, and Phase 3
  must derive compatibility and numeric engine-identity output without inventing normalization or
  ordering rules.
- **Evidence:** Request validity depends on the undefined predicate “non-empty sanitized strings”
  for `engineEdition` and `engineVersion`. Compatibility depends on an undefined normalized
  `minecraftVersion` key and “case-insensitive natural ordering of sanitized edition tokens,” with
  no grammar, tokenization, locale, digit-run, leading-zero, separator, or tie rules. The standard
  header promises numeric `SCHMALOOGIUM_VERSION`, says MC/engine identity comes from
  `RuntimeIdentityData`, but gives no projection from the free-form `engineVersion` string to exact
  macro bytes. RESEARCH Appendix F.2 fixes only the minimum-edition concept. Phase 7 is merely a
  supplier of this Phase-3-owned plain-data interface, and OQ-7 defers identity-set posture rather
  than deterministic validation and projection of a selected policy.
- **Severity:** correction. Bind exact accepted/canonical forms for both strings and
  `version.<mcver>` keys, a locale-independent total edition comparator, malformed-input behavior,
  and a byte-exact numeric `SCHMALOOGIUM_VERSION` projection—or replace the free-form input with a
  validated typed numeric value. Add acceptance, rejection, ordering, key-normalization, and exact
  emitted-value vectors; apply §5.3 only if an established configuration component meaning changes.
- **Touches interface/change-trigger region:** yes. The repair completes request validity and
  published compatibility/macro semantics in the monitored §5 contract.

### candidate-007 — Appendix F.2 does not support the silently chosen edition-ordering policy

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §3.2 line 1338, §4.8 lines
  2089–2096, and decision D-P3-11 at line 3307; authority at
  `docs/research/v1/RESEARCH.md` Appendix F.2 lines 1450–1452.
- **Claim:** The conformance map must distinguish Appendix F.2's minimum-edition requirement from
  Phase 3's locally selected normalization, comparison, aggregation, and malformed-rule policy.
- **Evidence:** Appendix F.2 states only `version.<mcver>=<edition>` as a minimum engine edition.
  The target's row attributes its compatibility model to that authority and a test, while detailed
  design adds sanitized tokens, case-insensitive natural ordering, any-unmet aggregation, and
  warn-and-ignore behavior. The governing design expressly identifies `version.<mcver>` as
  unanswered by reference evidence and requires contract-visible local interpretations to be
  flagged. D-P3-11 cannot supply that provenance because it expressly adopts Oculus natural order
  only for non-contract discovery; Oculus FE-09 likewise concerns pack-list ordering. A test can
  enforce a choice but cannot establish its authority.
- **Severity:** correction. Record the comparator and related rule as a provisional, contract-visible
  Phase 3 interoperability decision, split the conformance-row provenance between Appendix F.2 and
  that decision, record the authority gap or upstream clarification request, and add exact mixed
  alphabetic/numeric, separator, case-tie, leading-zero, and prerelease-style vectors. This finding
  does not require changing the chosen public behavior; candidate-006 separately requires making
  that behavior executable.
- **Touches interface/change-trigger region:** no. The ordered repair can be confined to the
  conformance map, detailed provenance, decision/gap record, and tests while retaining §5 behavior.

## 2. Checked and clean

- Candidate-004 is dropped as an exact substantive duplicate of candidate-001. Both cite the same
  null `PackLoadRequest.diagnostics` branch, unconditional reporter callback, Phase 1 instance-only
  reporting operation, and null-case test omission, and both order the same §5 repair. Its substance
  is represented once rather than double-counted.
- Candidate-002, concerning Round 50's description of ID-mapping change scope, remains excluded
  after its pre-adjudication Refute disposition and was not revived.
- Prior settled material does not clear the admitted findings. Round 50 introduced the present
  primary-diagnostic/exact-reporter contract but did not define its null-receiver branch or an
  exhaustive cause-to-code matrix. Its exact GL/GLSL and vendor/renderer repair did not define the
  separate MC tuple encoding. Round 39 introduced closed compatibility status using the labels
  “sanitized” and “natural ordering”; it did not supply the missing grammar, comparator algorithm,
  engine-version projection, or local-policy provenance.
- The finder-reported clean areas were rechecked. The finalized-option-only materializer/evaluator
  signatures, GL/GLSL grammar and bounds, OS/vendor/renderer tables, numeric-tag boundary, option
  macro formatting, Phase 1 diagnostic vocabulary, schema-13 repetition, mandatory thirteen-section
  structure, OQ-7 spike shape, checklist identifiers, and closing fresh-review ledger yielded no
  additional candidate-backed finding.
- The selected Phase 1 module/package seam, capability profile, fixed logging channels,
  `EngineDiagnostic`/`DiagnosticReporter`, debug flag, and notice mechanism exist in the binding
  dependency. The jcpp build declaration remains an explicit upstream request rather than a silent
  assumption. The remaining discovery, persistence, source/include, dimension, texture, resource,
  program-state, and ID-map conformance surfaces examined by the finders remain clean.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=5; notes=0
Interface changed: yes

Five distinct correction-sized findings survive after consolidating the duplicate null-reporter
candidate. None requires rebuilding the Phase 3 architecture, so `FAIL` is not warranted, but the
nonzero correction count makes literal `PASS` unavailable. Four findings require edits to the
manifest-declared `cross-phase-interfaces` region; candidate-007 is a provenance/decision repair
that can preserve that region.

The recent correction trend remains non-converged: prior rounds 48–50 moved `5 -> 6 -> 4`, and this
round rises to five. The next required action is a scoped fix-up resolving candidates 001, 003, 005,
006, and 007, with the null-reporter duplicate handled once. The fix-up must synchronize every
changed §5 row, add exact boundary and cause-classification tests, and apply §5.3 only where a
published configuration component meaning changes. Because at least one ordered correction changes
the monitored interface region, a fresh whole-document Phase 3 verification round is required before
the phase can close or be consumed as a verified dependency.

## Resolutions

### Corrections applied

1. **candidate-001 (null reporter): resolved.** Phase 1 exposes only the instance operation
   `DiagnosticReporter.report`, so the target no longer requires an impossible callback through
   null. Sections 5.1, 6, 8, 9, and 12 now make null diagnostics a pre-I/O
   `Failed(INVALID_REQUEST)` result carrying a non-null primary diagnostic, with no callback or
   throw; exact-once correspondence is scoped to a supplied non-null reporter. The dedicated test
   also asserts no external I/O.
2. **candidate-003 (`MC_VERSION`): resolved.** The governing mission is fixed to MC 1.12.2, while
   neither RESEARCH nor permitted reference evidence defines a total encoding for the former
   three-`int` domain. Rather than invent general arithmetic, §§2.2, 4.4, 5.1, 8, 9, 11, and 12
   now accept exactly `(1,12,2)`, emit exact ASCII decimal `11202`, and reject every other tuple
   before pack I/O. Boundary tests cover neighboring, negative, large, and maximum-`int` tuples.
3. **candidate-005 (failure algebra): resolved.** Sections 5.1 and 6 now give every
   `PackLoadFailureCode` an exhaustive cause class and ordered arbitration. Authenticated
   unavailable statuses map to their specific unsafe/limit/unreadable codes rather than selection
   failure; internal-provider defects remain `INTERNAL_SOURCE_INVALID`; only safely read bounded
   input without a usable root/base source is `STRUCTURALLY_UNUSABLE`; and uncategorized validated-
   stage runtime failures are `UNEXPECTED_INTERNAL`. The new matrix test covers each class,
   corrupt archive versus filesystem-provider I/O, every finite bound, same-class ordering, and
   multi-cause precedence.
4. **candidate-006 (runtime strings and projection): resolved.** Sections 2.2, 4.4, 4.8, 5.1,
   8, 9, 11, and 12 now bind a bounded ASCII edition grammar, canonicalization, locale-independent
   token comparator, exact `version.<mcver>` grammar/matching, and a bounded canonical decimal
   `engineVersion` whose bytes are the `SCHMALOOGIUM_VERSION` replacement. Overrides cannot alter
   standard A–G, that version macro, or option macros. Tests cover acceptance/rejection, exact
   output, case and leading-zero equivalence, separators, mixed alphabetic/numeric runs, and
   prerelease-style ordering.
5. **candidate-007 (provenance): resolved.** The §3.2 row now attributes only the minimum-edition
   meaning to RESEARCH Appendix F.2 and separately cites new D-P3-52 for the provisional grammar,
   normalization, comparator, aggregation, and malformed-rule policy. Section 11 records both the
   local interoperability decision and an upstream ratification/replacement request. Oculus FE-09
   remains scoped only to non-contract discovery ordering and is not compatibility authority.

### Interface impact

The edits intentionally change the manifest-declared `cross-phase-interfaces` region: runtime
identity validity and macro projection, failure delivery/classification, and compatibility
interpretation are now binding for Phases 7 and 12. No `PackConfiguration` record component,
meaning, or default changed, so §5.3 retains schema 13. The declared change trigger fires and a
fresh whole-document Phase 3 verification round is required before closure or dependency use.

### Notes deferred

None. The adjudication admitted zero notes, so there is no note to apply or defer.

### Refusals

None. All five admitted corrections were implementable without contradicting authority; the
edition-order authority gap is explicitly provisional and routed upstream rather than presented as
RESEARCH-derived behavior.
