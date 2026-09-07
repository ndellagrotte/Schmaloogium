# Phase 3 verification review — round 42

## 0. Method and reading order

I first re-derived every surviving candidate from the current target and the resolved source
contract, without consulting historical reviews. The decisive reads were:

1. `docs/phase3/v1/PHASE_3_DOC.md` as a whole, with focused checks of §0, the canonical §2.2
   declarations, the load/materialization and persistence pipelines, the §3 conformance map,
   program-state construction, binding §5, §8.2, milestones, decisions, and the implementation
   checklist;
2. `docs/design/v3/DESIGN.md` Part I, including §G0.4 adoption, the verification and interface
   rules, the mandatory template, and the Phase 3 specification and doc gate selected by this
   round's explicit override;
3. `docs/research/v1/RESEARCH.md` as contract ground truth, especially the compile flow,
   option-persistence rule, and Appendix F.3/F.4/F.7 material;
4. `docs/phase1/v14/PHASE_1_DOC.md`'s binding §5 contract, including package/sealing constraints,
   the recorded-capability serialization, and Phase 2's conformance ownership; and
5. the permitted Pintonium and Oculus reports where they supplied the candidate's provenance or
   resolved the concrete per-pack filename behavior.

I searched the whole target for public factories/providers, raw program-state declarations,
equivalent P3-C20 schemas, opposite preprocessing-order statements, direct-name persistence
compatibility, and row-local profile provenance before settling the independent dispositions.
Only after those judgments were settled did I read all discovered prior reviews
`PHASE_3_REVIEW_1.md` through `PHASE_3_REVIEW_41.md`, in round order and including their
resolutions, to apply settled-material and changed-premise rules.

There were no source-list deviations, no network use, and no agent fan-out. I did not invoke
`$verify-loop`, run a verification harness, start another session, or read a forbidden transcript,
chatlog, or `*.txt` source. The pre-adjudication elimination of `candidate-009` at Refute was not
revived. The Gate-dropped `candidate-002` was also not revived: its refutation quote omitted
verbatim text at its declared anchor, so its evidence remains fail-closed and it is not in the
adjudicated disposition set.

## 1. Findings

### candidate-003 — The exported front-end services have no public acquisition path

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §2.2 and §5.1, especially lines 362–371,
  671–699, 768–770, 1984–2017, and 2184–2187.
- **Claim:** The named Phase 7, Phase 9, and Phase 12 consumers must be able to obtain the
  Phase-3-owned service receivers without implementing inaccessible capability issuance or
  reaching into package-private internals.
- **Evidence:** The canonical public surface declares receiver operations on `PackFrontEnd`,
  `OptionPersistenceCodec`, `GlobalShaderOptionsCodec`, and `IdMappingParser`, but declares no
  constructor, factory, provider, or service bundle. At the same time, the target makes the
  front-end implementation package-private and its sole issuer of package-private permitted token
  implementations (lines 362, 753–759, and 2005–2008). Discovery tokens authenticate the issuing
  front-end instance, and the contract expressly provides no public implementation or consumer
  issuer (lines 2184–2187). A downstream implementation therefore cannot implement the complete
  protocol, while the complete §5 publication surface gives named consumers no way to obtain the
  Phase-3-owned implementation. `PersistenceFileAccess` is an operation input and byte-acquisition
  boundary (lines 2012–2017); it does not construct either codec.
- **Severity:** correction. Add a public Phase-3-owned acquisition boundary in the canonical §2.2
  declarations and bind it in §5.1. A public factory or immutable service bundle is sufficient,
  but it must state required dependencies, instance/snapshot lifetime, and ownership of token and
  persistence-target authentication. It must also give an acquisition route for each independently
  exposed codec/parser receiver that consumers are expected to call, or replace those interfaces
  with publicly obtainable implementations.
- **Touches interface/change-trigger region:** yes. The correction adds the missing callable
  creation contract to the complete §5 publication surface.

### candidate-004 — The raw program-state API is named but not type-closed

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §2.2, §4.8, and the §5.1
  `ProgramStateModel` row, especially lines 632–639, 1803–1819, 1996–2011, and 2081–2103.
- **Claim:** Phases 4 and 5 must be able to compile against every public type exposed through
  `ShaderPropertiesModel.programStates()` and the evaluated projection without inventing nominal
  records, components, accessors, or sealed payload types.
- **Evidence:** `ShaderPropertiesModel` has a public `ProgramStateModel programStates` component,
  and §5 publishes `ProgramStateModel`, `ProgramKey`, `ProgramState`, and
  `EvaluatedProgramStates` while claiming exact signatures, components, and variants are binding.
  The canonical §2.2 block nevertheless contains no declaration for the raw model, key, state,
  alpha/blend sums and payloads, viewport, flip key/override, enabled expression, or evaluated
  records. Detailed §4.8 says the raw aggregate is `Map<ProgramKey,ProgramState>` and describes
  `ProgramState` components, but `ProgramKey(dimensionId,programName)` does not even fix the nominal
  dimension component type. Section 5 supplies scalar algebra and an evaluated pseudo-shape, but
  still omits the raw aggregate/key/state declarations and exact alpha/blend payload record types.
  This contradicts the target's own assertion at lines 197–198 that §5 owns the complete binding
  shape and the governing interface-honesty rule that dependent promises be specified rather than
  gestured at.
- **Severity:** correction. Declare and bind one exact immutable public program-state graph:
  `ProgramStateModel` and its map/evaluation accessors, `ProgramKey` with an explicit
  `DimensionKey` component, `ProgramState`, the alpha/blend sealed variants and enabled payload
  records, function/factor enums, `ViewportScale`, `FlipBufferKey`, `FlipOverride`, the opaque
  `ProgramEnabledExpression`, and evaluated result records. State visibility, constructor
  validation, defensive collection immutability, and accessor behavior. If raw `ProgramState` is
  intentionally private, remove it from the exposed row and publish a fully declared opaque-model
  plus evaluated-only alternative instead. Apply §5.3 based on whether the chosen closure merely
  documents schema 7 or changes an existing component's shape, meaning, or default.
- **Touches interface/change-trigger region:** yes. The repair changes the monitored §5.1
  declaration/consumer contract.

### candidate-007 — Option rewriting is ordered before include expansion

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §§2.3, 3.2, 4.2, and 4.5, especially lines
  1052–1054, 1128, 1320–1323, and 1500–1512.
- **Claim:** Phase 3's producer pipeline must preserve the authoritative order: attributed include
  expansion first, standard macro environment next, changed-option rewriting next, and conditional
  preprocessing afterward.
- **Evidence:** The governing Phase 3 specification requires Pintonium-style `#include` resolution
  before all other preprocessing (`docs/design/v3/DESIGN.md:1369-1374`). RESEARCH gives the exact
  compile flow: resolve includes with `#line`, inject the standard header, rewrite changed option
  defines, then evaluate conditionals (`docs/research/v1/RESEARCH.md:497-500`). The target instead
  says load-time analysis proceeds through “line rewriting, include expansion, and jcpp” (lines
  1052–1054), the conformance row explicitly says rewriting occurs before include expansion (line
  1128), and the detailed processor runs jcpp over an “option-rewritten ... include expansion”
  (line 1509). No contrary target statement restores the required executable order.
- **Severity:** correction. Reorder the producer descriptions and implementation plan so include
  expansion with numeric `#line` attribution occurs first, the macro environment is established,
  every captured option occurrence in the expanded attributed stream is rewritten, and jcpp
  conditional evaluation follows. Revisit the option-dependent expansion-cache key and add an
  ordering test with a changed option in an included file. Do not change §5 or fingerprint meaning
  merely for an output-preserving internal reorder; update §5 only if implementation analysis shows
  an actual change to published transformed text, source maps, diagnostics, or fingerprint
  interpretation.
- **Touches interface/change-trigger region:** no. The required correction can remain in producer
  pipeline, conformance, test, and checklist text while preserving the existing published result.

### candidate-008 — Digest-slot persistence replaces the required `shaderpacks/<pack>.txt` format

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §§3.2, 4.3, and 5.1, especially lines 1129,
  1368–1383, 1987–1995, and 2435–2438.
- **Claim:** The Phase-3-owned per-pack persistence protocol must read and write the authoritative
  direct-name file rather than declare only a non-interoperable digest-slot family.
- **Evidence:** The governing Phase 3 scope requires per-pack
  `shaderpacks/<pack>.txt`, changed options only, and calls the Pintonium ISO-8859-1 round trip a
  matching reference (`docs/design/v3/DESIGN.md:1444-1448`). RESEARCH independently fixes the same
  path (`docs/research/v1/RESEARCH.md:603-605`). The supporting reference resolves `<pack>`
  concretely as the current pack name plus `.txt`
  (`docs/reference/oculus/v1.0/OCULUS_DESIGN.md:521-527`), while Pintonium likewise records
  `shaderpacks/<pack>.txt` (`PINTONIUM_DESIGN.md:716-718`). The target instead declares that the
  only legal files are `shaderpacks/p<64-hex>-<slot>.txt`, searches only those 65,536 slots, and
  explicitly tests that there is no legacy/direct-name probe. Its §5 row publishes this replacement
  target derivation to Phases 7 and 12. The collision-safe bounded design is useful engineering,
  but §G4.2 does not permit a phase-local observable “improvement” to replace the required format.
- **Severity:** correction. Make safe changed-only ISO-8859-1 read/write at the authoritative
  `shaderpacks/<pack>.txt` location part of the canonical persistence contract, with direct-child
  containment and symlink protections. If digest slots remain useful for exceptional host limits,
  define them only as an additional versioned fallback/migration protocol that interoperates with
  the direct-name format. Otherwise record an unresolved deviation and obtain an authoritative
  contract revision. Align the conformance row, detailed protocol, §5 binding row, tests, and
  checklist.
- **Touches interface/change-trigger region:** yes. `packOptionsTarget` and digest-target semantics
  are explicitly published in §5.1 to Phases 7 and 12.

### candidate-010 — The profile precedence row omits required Pintonium provenance

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md` §3.2 line 1138, with the supporting adoption at
  lines 1355–1361 and D-P3-4 at line 2633.
- **Claim:** A conformance row adopting constraint-count profile precedence must carry the PD
  citation, observed-source provenance, and recorded contract-check decision required by the
  mandatory template.
- **Evidence:** The governing template requires a row adopting or rejecting a Pintonium mechanism
  to carry its PD citation and, for contract-visible behavior, its §G11.4 decision reference
  (`docs/design/v3/DESIGN.md:831-835`). The Phase 3 specification identifies constraint-count
  profile precedence as adopted Pintonium App F.3/F.4 machinery and supplies PD §7.3
  (`DESIGN.md:1413-1416`). RESEARCH Appendix F.4 supplies profile syntax, inference, and `Custom`,
  but not constraint-count precedence (`RESEARCH.md:1472-1477`). Pintonium §7.3 supplies that
  precedence (`PINTONIUM_DESIGN.md:419-428`). The target's detailed design records the observed
  `ProfileSet.java` source and D-P3-4, yet the mandatory §3.2 row cites only App F.4 and tests.
- **Severity:** correction. Amend only that row's provenance cell: retain App F.4 for syntax,
  inference, and `Custom`; add PD §7.3, the observed `ProfileSet.java` provenance tag, and D-P3-4
  for adopted descending constraint-count precedence. Identify source order as Phase 3's
  deterministic tie-break unless a cited source establishes it.
- **Touches interface/change-trigger region:** no. This is a row-local provenance correction and
  changes no published consumer shape or semantic.

## 2. Checked and clean

- `candidate-001` and `candidate-006` are dropped as duplicate formulations of a governance claim
  that fails on re-derivation and settled material. The resolved v3 authority expressly says that
  selecting v3 does not itself adopt it, identifies Phases 3–9 as RC3-governed, orders a dry run
  before migration, and keeps a phase's declared design until all four §G0.4 steps complete
  (`docs/design/v3/DESIGN.md:195-220`). This round's `selection_source: override` is therefore an
  assessment context, not proof that Phase 3 already completed adoption. The current RC3 header and
  actual-input ledger must not be mechanically relabelled. Rounds 37, 39, and 40 reached the same
  disposition on the unchanged premise. A maintainer-authorized future migration must perform and
  record all §G0.4 steps and separately trigger review if it changes §5.
- `candidate-005` is dropped against specifically settled material. Round 38 adjudicated the same
  alleged missing P3-C20 manifest/golden schema and found that Phase 2 owns the golden-file format,
  fixture workflow, adapter, CI job, and `:conformance` integration. Phase 3's §8.2 list is a test
  workflow and emitted-input description, not a second runtime parser result or a mandate for
  Phase 3 to dictate Phase 2's repository paths and serialization envelope. The Phase 3 public
  in-process outputs and Phase 1's recorded `GLCapabilityProfile` format remain available to the
  Phase 2-owned adapter. Rounds 39–41 did not change that ownership or §8.2 premise, so no basis
  exists to reopen the settled candidate. If maintainers later choose a separately serialized
  cross-phase artifact, its jointly owned envelope can be added deliberately, but this candidate
  does not establish a current correction.
- The prior program-state repairs do not settle `candidate-004`. Round 18 added semantic structure,
  and Round 24 closed the viewport, opaque expression, and evaluated projections, but the current
  canonical declaration block still has no nominal raw `ProgramStateModel`/`ProgramKey`/
  `ProgramState` graph while §5 expressly exposes and promises those exact components. Round 41's
  schema-7 edits changed that program-state surface without supplying the missing declarations.
- The Round 10 profile review settled that constraint-count precedence is semantically permitted;
  it did not supply the row-local PD/D-P3-4 provenance now required by §G9. Round 41's own
  provenance fix demonstrates the same row-local rule on neighboring conformance rows without
  repairing the profile row.
- The new package-private issuing-front-end posture and digest-slot protocol were created by the
  Round 41 fix-up, so the acquisition and direct-name compatibility findings are changed-premise
  issues rather than attempts to reopen older general clean statements. No prior review
  specifically settled the reversed include-expansion/option-rewrite sequence.
- The finder-reported clean areas were independently rechecked. No additional defect was admitted
  in the Round 41 persistence arithmetic and collision authentication considered on their own,
  persisted-option load timing, schema-7 references, same-package sealing legality, macro staging,
  scale/flip arity and family filtering, renderer-feature gates, resource/directive families,
  consumed Phase 1 runtime contracts, milestone register, OQ-7 spike, or final pending-review
  ledger. The Appendix F.1 owner inventory, Appendix A.3 mapping, B1/B2/B3/B12 gates, dimension,
  ID-map, texture, custom-expression, debug-dump, and pure-`:engine` areas remain clean apart from
  the admitted candidates above.
- Pre-adjudication `candidate-009` remains eliminated by Refute. Gate-dropped `candidate-002`
  remains excluded for its unfaithful citation and was not converted into a finding.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=5; notes=0
Interface changed: yes

Five correction-sized findings survive. None requires rebuilding the Phase 3 architecture, so
`FAIL` is not warranted; nonzero corrections make literal `PASS` unavailable. Candidates 003, 004,
and 008 require edits to the manifest-declared §5 cross-phase interface region. Candidates 007 and
010 can be repaired without changing that region.

The correction count falls from eight in Round 40 and eight in Round 41 to five in this round, but
repeated interface-completion findings mean the document has not converged to closure. The next
required action is a scoped fix-up resolving candidates 003, 004, 007, 008, and 010 and recording
resolutions in this review. Because at least one ordered correction changes §5, the
`cross-phase-interfaces` trigger fires: a fresh whole-document verification round is required
before Phase 3 can close or be consumed as a verified dependency.

## Resolutions

### candidate-003 — resolved

Added the canonical dependency-free `PackFrontEnds.create()` acquisition route and immutable
`PackFrontEndServices` bundle containing `PackFrontEnd`, both persistence codecs, and
`IdMappingParser`. Binding §5 now fixes the bundle/receiver lifetime, bounded snapshot retention,
thread safety, absence of retained input handles, and one service-owned authentication domain.
The front end remains the sole token and pack-target issuer; consumers no longer need to implement
an inaccessible issuer. This intentionally changes `cross-phase-interfaces`.

### candidate-004 — resolved

Declared the complete immutable raw/evaluated program-state graph in canonical §2.2, including the
`DimensionKey`-typed `ProgramKey`, exact alpha/blend sums and payloads, viewport, flip, opaque
enabled expression, map/evaluation accessors, and evaluated records. Binding §5 now names every
type and fixes construction validation, defensive copying, ordering, absence, and accessor
behavior. This closes the already-published schema-7 component without changing its meaning or
default, so the configuration schema remains 7. This intentionally changes
`cross-phase-interfaces`.

### candidate-007 — resolved

Re-derived the executable order from DESIGN Phase 3's include-first rule and RESEARCH §4.2:
numeric-`#line` include expansion now precedes macro-environment setup, expanded-stream option
rewriting, and jcpp conditional evaluation. The expansion cache no longer keys option state, and a
changed option in an included file has a named ordering test. Published materialized output,
source-map, diagnostic, and fingerprint semantics are unchanged, so §5 did not change for this
correction.

### candidate-008 — resolved

Restored changed-only ISO-8859-1 persistence at the authoritative
`shaderpacks/<pack>.txt` location. The target authenticates a current candidate and uses its exact
direct-child entry name,
while containment, symlink rejection, safe atomic replacement, and typed host-limit failures keep
the direct-name protocol safe. Digest/slot files and identity headers are no longer legal probes or
replacements. The target declaration, conformance row, detailed protocol, tests, decision log,
milestone, checklist, and binding §5 row were aligned. This intentionally changes
`cross-phase-interfaces`; schema 7 remains current because no `PackConfiguration` component,
meaning, or default changed.

### candidate-010 — resolved

Amended only the profile conformance row: App F.4 remains provenance for syntax, inference, and
`Custom`; adopted constraint-count precedence now cites PD §7.3, the observed Pintonium
`ProfileSet.java` source, and D-P3-4. The row identifies source order as Phase 3's deterministic
tie-break rather than attributing it to the reference.

### Notes deferred

None. The adjudicator admitted zero notes. Because candidates 003, 004, and 008 intentionally
changed the declared interface region, the manifest trigger requires a fresh whole-document
verification round before Phase 3 can close.
