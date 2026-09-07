## 0. Method and reading order

I independently re-derived all five surviving candidates before consulting historical reviews. The
load-bearing source order was:

1. the complete `docs/phase3/v1/PHASE_3_DOC.md`, including its header/input ledger, canonical
   declarations, conformance maps, global-persistence and resource-fingerprint additions, complete
   manifest-declared §5 interface region, tests, decisions, hand-offs, checklist, and closing ledger;
2. the resolved authority `docs/design/v3/DESIGN.md`, specifically Part I, §G0.4's adoption rule,
   the decision/provenance rules, the mandatory template, the Phase 3 target specification, and the
   document gate;
3. the relevant contract ground truth in `docs/research/v1/RESEARCH.md`, including the tag-support,
   texture, option-persistence, and ownership rows;
4. the binding §5 contract in `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the permitted Pintonium and Oculus supporting evidence needed to test suffix and tag provenance.

I searched the target for equivalent `EngineOptionData` validity rules, decision-log entries,
tag-selector provenance, and row-local texture-suffix citations before settling every candidate's
interpretation, severity, and interface classification. Only after those judgments were settled did
I read all discovered prior reviews `PHASE_3_REVIEW_1.md` through
`PHASE_3_REVIEW_47.md`, including their resolutions, as the final historical step. Prior reviews
were used to identify settled material and changed premises, not as authority over the current
bytes.

There were no source-list deviations, no network use, and no agent fan-out. I did not invoke
`$verify-loop`, run a verification harness, start another session, or read any forbidden transcript,
chatlog, or `*.txt` source. No candidate was eliminated before adjudication, and the Gate reported
no drops. No finding was dropped on independent derivation.

## 1. Findings

### candidate-001 — The Round-47 v3 attribution contradicts the RC3-only actual-input ledger

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:5-16,39-43,3158-3161`.
- **Claim:** The mandatory header must truthfully distinguish the phase's governing pin from every
  design revision actually read or relied upon by the current fix-up.
- **Evidence:** The header declares `docs/design/v2.0-RC3/DESIGN.md` as the governing design, lists
  RC3 as the only design input, and states that no input outside the assignment was read
  (`PHASE_3_DOC.md:5-16,39-43`). The Round-47 suffix ruling nevertheless expressly attributes its
  gap premise to “Design v3” (`:3158-3161`). The mandatory template requires paths for inputs
  actually read and reasons for reading-list deviations (`docs/design/v3/DESIGN.md:817-826`). At
  the same time, v3's adoption rule says that reading or selecting v3 does not by itself repin Phase
  3: the declared design remains in force until all four adoption steps complete
  (`DESIGN.md:195-220`). Thus the present text is contradictory whether the v3 label records a real
  additional read or is merely a revision miscitation.
- **Severity:** correction. Reconcile the provenance without mechanically assuming adoption. If the
  ruling used only the declared RC3 material—which contains the same suffix-gap premise—correct the
  §11.3 revision label to RC3. If v3 was actually consumed, add its exact consumed selectors and the
  reason for the additional/override read to §0.1 and revise the absolute no-outside-input claim.
  Change the governing-design pin only if the complete §G0.4 adoption procedure has actually been
  completed and recorded.
- **Touches interface/change-trigger region:** no. The correction is confined to the header or the
  §11 provenance label and need not alter §5 or the suffix behavior.

### candidate-002 — Round 47 adds two phase-local policies without D-P3 records

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1617-1631,1936-1944,2221-2239,3083-3130`.
- **Claim:** The new invalid-global-read fallback and complete resource canonicalization policy must
  have stable phase-local decision IDs and one-line rationales in §11.
- **Evidence:** The governing decision rule requires each phase-local choice to be logged under a
  `D-P<N>-<k>` identifier with a one-line rationale (`docs/design/v3/DESIGN.md:277-281`). Round 47
  chooses canonical empty `EngineOptionData` when an invalid read request has no valid baseline
  (`PHASE_3_DOC.md:1617-1621`), rather than another total result design. It also chooses
  constructor-level signed-zero normalization and a complete recursive, producer-only
  `ResourceRequirements` payload codec (`:1936-1944,2221-2239`), aligning structural equality with
  fingerprint input without making the encoding a downstream wire format. The §11.1 log ends at
  D-P3-43 (`:3125-3130`). Existing fingerprint, persistence, and authentication decisions concern
  different payloads or lifecycle choices and do not record either new rationale.
- **Severity:** correction. Add sequential decisions, naturally D-P3-44 and D-P3-45, with one-line
  rationales: one for returning canonical empty under the existing total non-variant global-read
  result when no valid baseline exists, and one for constructor-level signed-zero normalization plus
  the exact producer-only resource codec. Preserve the already-published behavior and do not turn
  the configuration fingerprint into a portable digest or wire-format promise.
- **Touches interface/change-trigger region:** no. The ordered repair is decision-log documentation;
  it does not require changing the established §4/§5 semantics.

### candidate-003 — `EngineOptionData` has no executable validity or safety domain

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:718-726,1617-1631,2351-2357,2587-2601`.
- **Claim:** Phase 7 and Phase 12 must be able to construct valid global engine-option values and
  predict `INVALID_REQUEST`, ignored malformed input, retained unknown-safe input, and applied
  overlays without inventing key/value predicates.
- **Evidence:** The canonical public type is only
  `EngineOptionData(Map<String,String> values)` (`PHASE_3_DOC.md:718-726`). The global codec then
  validates an “immutable `EngineOptionData`,” overlays each “valid occurrence,” retains “unknown
  safe keys,” ignores “malformed occurrences,” and serializes every validated entry
  (`:1617-1631`). Binding §5 makes valid-baseline status and those entry categories observable in
  exact result tuples (`:2351-2357`) and makes structural validity of load request data observable as
  `INVALID_REQUEST` (`:2587-2601`). No global-specific rule defines the known raw keys, their
  accepted boolean/integer/finite-decimal spellings and bounds, safe unknown-key/value syntax,
  null/empty/control-character treatment, typed-invalid-known-value behavior, collection
  immutability/order validation, or the exact condition that invalidates a baseline or write value.
  The nearby replacement-token grammar is expressly for catalog-bound per-pack `OptionState`, and
  §4.4's eight typed macro projections define post-decode effects/defaults rather than the raw global
  codec domain. Java Properties syntax alone does not define the target's additional “safe,”
  “valid,” and “malformed” policy categories. The governing Phase 3 specification assigns the
  global persistence format and read/write model to this phase (`docs/design/v3/DESIGN.md:1444-1448`).
- **Severity:** correction. Add one authoritative `EngineOptionData`/global-codec invariant and
  incorporate it from §§2.2, 4.3, and 5. Separate request-object validity from logical Properties
  occurrence parsing; reuse the existing eight semantic setting names but define each accepted raw
  value grammar and range; define retained unknown-key/value safety; and state whether a safe but
  typed-invalid known occurrence is retained for round trip or omitted while typed projection warns
  and defaults. Define deterministic validation priority and add boundary tests for invalid
  baselines/writes, malformed known values, unknown-safe round trip, duplicate precedence, and exact
  output. Increment the schema only if the correction changes an existing published value meaning,
  default, or fingerprint interpretation under §5.3.
- **Touches interface/change-trigger region:** yes. The missing predicates are incorporated into the
  binding §5 persistence and load contracts; completing them requires a monitored §5 update and a
  fresh verification round.

### candidate-004 — RESEARCH §3.6.8 does not define the claimed `%` tag-selector grammar

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1399-1401,2130-2133,2167-2175`.
- **Claim:** The exact pack-visible `%namespace:path` spelling, short-name canonicalization, and
  rejection set must have exact authority or be identified as a phase-local interpretation rather
  than attributed wholesale to RESEARCH §3.6.8.
- **Evidence:** The conformance row calls `%namespace:path` a tag-selector extension and cites
  RESEARCH §3.6.8 (`PHASE_3_DOC.md:1399-1401`); §4.9 repeats that attribution and then requires one
  leading `%`, lowercase short or namespaced names, implicit `minecraft` for short names, and
  rejection of empty, doubled-percent, property-bearing, metadata-bearing, or numeric tag tokens
  (`:2130-2133,2167-2175`). The cited authority states only that tag-based ID files do not exist on
  1.12.2 and need an ore-dictionary-style shim plus entries-before-tags priority
  (`docs/research/v1/RESEARCH.md:439-448`). It supplies no `%` token spelling or parser grammar, and
  no equivalent exact grammar appears in the permitted Pintonium or Oculus reports. These are
  pack-observable parsing decisions owned by Phase 3, while Phase 9 owns only expansion/resolution
  (`docs/design/v3/DESIGN.md:1440-1443`). Governing §§G0.1/G4.2 require exact provenance for such
  claims, and §G9 requires deviations or local interpretations to be flagged.
- **Severity:** correction. Retain the generic unresolved-tag, 1.12 shim, and entries-before-tags
  statements under RESEARCH §3.6.8. For the concrete `%` grammar, either supply an exact governing
  citation defining every accepted/canonicalized/rejected form, or label and record the grammar as a
  D-P3 interpretation with its contract check and a DESIGN/RESEARCH clarification request rather
  than presenting it as literal §3.6.8 conformance. Named tests can enforce a chosen interpretation
  but cannot supply provenance.
- **Touches interface/change-trigger region:** no. Provenance, decision, and open-item corrections
  can be made in §§3, 4, and 11 while retaining the published ID-map type surface and behavior.

### candidate-005 — The texture-suffix conformance row omits its mandatory PD and decision links

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1301,3116`.
- **Claim:** A conformance row rejecting a Pintonium behavior for a contract-visible Appendix F
  input must itself carry the Pintonium evidence citation and recorded decision reference.
- **Evidence:** The mandatory template says that a row adopting or rejecting a Pintonium mechanism
  carries the PD citation and, for contract-visible items, the §G11.4 decision reference
  (`docs/design/v3/DESIGN.md:831-835`). The terminal filter/wrap-suffix row disposes Pintonium's
  strip-and-ignore behavior but its provenance cell says only “REV1 gap” plus the named test
  (`PHASE_3_DOC.md:1301`). Pintonium's actual evidence is PD §7.4
  (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:437-449`), and the target already records the
  rejection/authority-gap rationale as D-P3-29 (`PHASE_3_DOC.md:3116`). Citations elsewhere do not
  satisfy §G9's explicit row-local linkage requirement.
- **Severity:** correction. Amend that row's provenance cell to include `PD §7.4`, the applicable
  observed Pintonium source provenance tag, and `D-P3-29`, while retaining the existing authority-gap
  disposition and `texture_undocumentedSamplingSuffixNotNormalized` test.
- **Touches interface/change-trigger region:** no. This is a row-local provenance repair and orders
  no change to §5, the schema, or suffix behavior.

## 2. Checked and clean

- The finder-reported schema-12 surface was rechecked. Positional `DRAWBUFFERS` retains every `N`;
  absent routing remains `AllUsed`; the default/plain versus explicit attachment-format algebra and
  mandatory `gdepth`→`Explicit(RGBA32F)` rule agree across §§2–5; schema-11/schema-12 rejection is
  bidirectional; and the complete resource codec consistently remains producer canonicalization
  rather than a portable digest.
- The discovery/load capabilities, sealed catalog/state authentication, per-pack changed-only
  persistence, global all-entry persistence matrix, source/materialization identities, executable
  program projection, resource baselines, custom-texture sidecar projection, schema discipline, and
  fresh-review trigger are otherwise internally reflected in §5 with implementable consumer
  semantics.
- The selected Phase 1 module/package seam, complete `GLCapabilityProfile` macro input, fixed log
  channels, loader-neutral diagnostics, debug flag, and SPDX/third-party mechanism exist in the
  binding dependency contract. The missing jcpp build declaration remains requested rather than
  assumed.
- The Appendix F.1 ownership map, mapped Appendix F.2–F.8 families other than the provenance defects
  admitted above, all Appendix A.3 directive families, the four required Pintonium pitfalls,
  discovery, dimensions, includes, macros, preprocessing, debug dumping, and OQ-7 spike retain
  substantive design and named-test coverage.
- No surviving candidate was refuted, consolidated, or cleared. Earlier governance candidates were
  correctly dropped while the target merely faced a v3 review override; candidate-001 survives now
  because Round 47 added an express “Design v3” attribution under an unchanged RC3-only/no-extra-
  input ledger. Round 47's global matrix and resource codec repairs do not settle candidate-002's
  missing decisions or candidate-003's undefined classification predicates. Round 37's tag
  provenance correction established only the generic modern-risk/shim classification and did not
  supply the exact `%` grammar. Round 47's new suffix row retained D-P3-29 elsewhere but omitted the
  row-local links required by §G9.
- There were no candidates eliminated before adjudication and no Gate drops to carry into this
  section.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=5; notes=0
Interface changed: yes

All five admitted findings are bounded documentation, provenance, decision-recording, or
interface-completion corrections; none requires rebuilding the Phase 3 architecture, so `FAIL` is
not warranted. Literal `PASS` is unavailable because five corrections remain. Candidate-003 alone
requires a monitored cross-phase-interface edit; the other four can be repaired without changing
that region.

The recent correction sequence is not converged: Rounds 45–48 contain 5, 2, 4, and now 5
corrections. The increase reflects defects in or exposed by the Round-47 global-persistence,
resource-codec, suffix, and governance prose rather than evidence supporting closure.

The next required action is a scoped fix-up resolving candidates 001 through 005 and recording each
resolution in this review. The fix-up must reconcile the design-input provenance, add the missing
D-P3 records, define the complete global `EngineOptionData` validity/safety domain in §5, correct the
tag-grammar provenance, and add the suffix row's PD/decision links. Because candidate-003 changes
the manifest-declared `cross-phase-interfaces` region, a fresh whole-document Phase 3 verification
round is required before Phase 3 can close or be consumed as a verified dependency.

## Resolutions

I re-derived the admitted corrections against the resolved v3 override, RESEARCH, the binding
Phase 1 §5 contract, and the permitted Pintonium evidence before editing the target. The RC3 phase
pin remains unchanged because the v3 §G0.4 adoption procedure was not performed.

### candidate-001 — applied

The header now records the exact v3 selectors consumed as Round 48 override authority, explains
that this additional read does not adopt v3, and limits the former no-extra-input statement to the
original build. The texture-suffix ruling now distinguishes RC3 from the Round-48 v3 override.
This reconciles actual-input provenance without mechanically changing the governing RC3 pin.

### candidate-002 — applied

The sequential decision log now includes D-P3-44 for canonical-empty fallback when an invalid
global read has no valid baseline and D-P3-45 for constructor-level resource signed-zero
canonicalization plus the complete recursive producer-only resource codec. D-P3-45 expressly
preserves the existing non-portable, non-wire-format boundary. D-P3-46 and D-P3-47 also record the
newly explicit global-value and local tag-grammar choices so those phase-local policies are not
left implicit.

### candidate-003 — applied

Sections 2.2 and 4.3 now incorporate one binding §5.1 invariant. Section 5.1 names exactly the
eight known settings; fixes exact lower-case Boolean, finite-positive float, and non-negative
integer spellings/ranges; defines unknown-safe key/value syntax, empty-value behavior, Unicode and
control-character exclusions; and requires defensive immutable canonical ordering. It separately
orders request-object validation and logical Properties occurrence classification. A typed-invalid
known occurrence is warned and omitted rather than retained, an unknown-safe occurrence is warned
and retained, an omitted duplicate cannot erase a prior valid value, and the last valid occurrence
wins.

The same invariant fixes pre-I/O invalid-baseline/write/load behavior, canonical-empty fallback,
deterministic diagnostic priority, exact all-entry ordering, escaping, ISO-8859-1 bytes, and final
line termination. The test plan now names boundary, occurrence-classification, and byte-exact
output suites, including invalid baselines/writes/load data. D-P3-46 records the rationale.

No schema increment was made: this closes predicates previously named only as
“valid,” “safe,” and “malformed”; it changes no record component, established setting
meaning/default, or configuration-fingerprint interpretation. Schema 12 therefore remains the
published value.

### candidate-004 — applied

The conformance map, detailed parser rules, binding interface row, decision log, and upstream
request now distinguish RESEARCH §3.6.8's actual generic tag-risk/shim/entries-before-tags claim
from the concrete `%` syntax. D-P3-47 labels the one-percent, lower-case short/namespaced grammar,
short-name `minecraft` canonicalization, and rejection behavior as a Phase 3 interpretation pending
DESIGN/RESEARCH clarification. No parser behavior was changed.

### candidate-005 — applied

The terminal filter/wrap-suffix conformance row now carries PD §7.4, the row-local observed
`ShaderProperties.java` provenance tag, D-P3-29, the authority-gap disposition, and the existing
named test. The correction changes provenance only and does not certify suffix grammar or restore
Pintonium's destructive strip-and-ignore behavior.

### Interface and verification consequence

The §5.1 `EngineOptionData` invariant, its incorporated interface rows, and the now-explicit §5.1
heading intentionally change the manifest-declared `cross-phase-interfaces` region. This is the
admitted candidate-003 interface completion, not a new schema meaning. The manifest change trigger
therefore fires: a fresh whole-document Phase 3 verification round is required before closure or
dependency consumption.

### Notes deferred

None. The adjudicator admitted zero notes, and no correction required a new design decision beyond
the phase-local interpretations recorded as D-P3-44 through D-P3-47.
