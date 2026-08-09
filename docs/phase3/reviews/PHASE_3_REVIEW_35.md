## 0. Method and reading order

I independently re-derived the surviving candidates against the target document, the selected
`v2.0-RC3` Part I, Phase 3 target specification, doc gate, and mandatory template in
`docs/design/v2.0-RC3/DESIGN.md`, the assigned contract ground truth in
`docs/research/v1/RESEARCH.md`, and the binding Phase 1 contract in
`docs/phase1/v14/PHASE_1_DOC.md`. I used a whole-target search plus the cited target ranges to
check each public shape, algorithm, conformance row, test hook, and hand-off. The permitted
Pintonium and Oculus reports were not needed to decide these candidates.

Only after settling each candidate's interpretation, severity, and interface classification did I
read the discovered prior reviews `PHASE_3_REVIEW_1.md` through `PHASE_3_REVIEW_34.md`, in round
order and including their resolutions. The supplied prior-round trend was empty; the actual prior
history nevertheless contains repeated literal PASS rounds followed by newly surfaced
post-fix-up corrections, so it does not establish convergence of the current surface.

There were no deviations from the assigned sources, no network use, and no agent fan-out. I read
no forbidden transcript, chatlog, root-level text file, or implementation/decompile source. I did
not invoke a verification harness or another verification session. The Gate drops
`candidate-003`, `candidate-006`, and `candidate-009` for unverifiable evidence; I did not revive
or use those candidates as findings. No Gate-surviving candidate was dropped on independent
re-derivation.

## 1. Findings

### candidate-001 — The custom-expression addendum cites an unlisted Phase 11 input while the header denies outside reads

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:15-43, 225-238`.
- **Claim:** The §0.1 actual-input/deviation ledger is complete and truthful after the custom-expression addendum.
- **Evidence:** The header enumerates the design, research, Phase 1, Pintonium, reference, and
  Oculus inputs, then states that no input outside that assignment was read
  (`docs/phase3/v1/PHASE_3_DOC.md:15-43`). The addendum nevertheless cites and quotes
  `docs/phase11/v1/PHASE_11_DOC.md:853-870` as the source of the immutable, source-ordered,
  duplicate-retaining, fingerprinted custom-expression boundary
  (`docs/phase3/v1/PHASE_3_DOC.md:225-238`). The governing reading rule permits an unlisted file
  only for a genuine gap and requires the path and reason in the phase-document header
  (`docs/design/v2.0-RC3/DESIGN.md:214-219`), while the mandatory header requires actual inputs
  and deviations with reasons (`docs/design/v2.0-RC3/DESIGN.md:797-799`). The Phase 11 hand-off
  is downstream-consumer evidence, not a silent assigned dependency.
- **Severity:** correction. The ledger must name the Phase 11 read and justify it as a narrowly
  scoped downstream request, or the citation and unsupported derivation must be removed.
- **Touches interface/change-trigger region:** no. This is a provenance/header correction; the
  already-published custom-expression §5 surface need not change.

### candidate-002 — Absent ID-map input has no diagnostic even when the request envelope is invalid

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:441-450, 1286-1295, 1344-1351, 1756-1768`.
- **Claim:** `IdMappingParser` has an unambiguous validation and diagnostic precedence for absent
  sources and malformed parse requests.
- **Evidence:** The parser contract says `source=Optional.empty()` unconditionally returns
  `ABSENT`, empty rule lists, the canonical absent fingerprint, and no diagnostic
  (`docs/phase3/v1/PHASE_3_DOC.md:1286-1289`). The same request envelope contains kind, source,
  origin, environment, and reporter (`docs/phase3/v1/PHASE_3_DOC.md:441-450`), and the origin
  contract says null, malformed, or out-of-range components make the request invalid and yield an
  empty diagnosed result (`docs/phase3/v1/PHASE_3_DOC.md:1344-1349`). The test list covers absent
  file states and origin validation separately, but names no combined invalid-envelope/absent
  case or precedence rule (`docs/phase3/v1/PHASE_3_DOC.md:1756-1768`). Thus an absent source with
  an invalid origin has two specified outcomes. Because the parser is explicitly exposed to
  Phase 9 in §5.1, this is a binding consumer ambiguity.
- **Severity:** correction. State that request-envelope, kind, origin, environment, and reporter
  validation precedes source-presence branching; only a structurally valid request may receive
  the clean canonical `ABSENT` result, and add the combined test.
- **Touches interface/change-trigger region:** yes. The public Phase 9 parser contract and its
  diagnostic precedence must be amended and freshly reviewed.

### candidate-004 — The schema-versioned declared-uniform payload has no fixed current version or bump rule

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:537-543, 621-627, 1375-1384, 1593-1606, 1723-1731`.
- **Claim:** A consumer or independent test can construct the same declared-uniform payload and
  know when its schema changes.
- **Evidence:** `CanonicalDeclaredUniformPayload` exposes an `int schemaVersion` and ordered
  declarations, but no current constant or relationship (`docs/phase3/v1/PHASE_3_DOC.md:537-543`).
  Materialization is required to construct the payload from a supposed catalog schema version and
  hash its deterministic encoding (`docs/phase3/v1/PHASE_3_DOC.md:621-627`); the fingerprint prose
  likewise includes `CanonicalDeclaredUniformPayload(schemaVersion, declarations)` but assigns no
  value or bump rule (`docs/phase3/v1/PHASE_3_DOC.md:1375-1384`). The only explicit current value
  and compatibility rule, `PackFrontEnd.CURRENT_SCHEMA_VERSION == 3`, governs the separate
  `PackConfiguration` surface, while the target explicitly states the nested ID-map equality but
  not an equality for the uniform payload (`docs/phase3/v1/PHASE_3_DOC.md:1593-1606`). The named
  independent reconstruction test makes the missing value executable rather than stylistic
  (`docs/phase3/v1/PHASE_3_DOC.md:1723-1731`).
- **Severity:** correction. Declare a dedicated payload/catalog current version and its exact
  compatibility and bump triggers, or explicitly bind it to `PackFrontEnd.CURRENT_SCHEMA_VERSION`
  and require construction and reconstruction tests to use that value.
- **Touches interface/change-trigger region:** yes. The payload is exposed to the declared-uniform
  consumers and participates in the materialization fingerprint, so its version contract is in
  the monitored §5 surface.

### candidate-005 — The String-only transformed-source interface still promises byte identity without an encoding

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:617-620, 1005-1021, 1720-1722`.
- **Claim:** The final transformed-source identity promised by the String/source-map surface is
  precisely defined and reproducible.
- **Evidence:** The public projection is an immutable Java `String`, its map is for that exact
  string, and the target expressly says no byte encoding is part of the projection
  (`docs/phase3/v1/PHASE_3_DOC.md:617-620`). The materialization algorithm nevertheless says that
  equal inputs are “byte-identical” and that equal plans produce equal “transformed bytes”
  (`docs/phase3/v1/PHASE_3_DOC.md:1005-1021`). The explicitly specified UTF-8/length-prefixed
  encoding applies to fingerprint payloads, not to the emitted transformed source
  (`docs/phase3/v1/PHASE_3_DOC.md:1375-1384`), and the named test checks the exact String/map pair
  rather than a byte serialization (`docs/phase3/v1/PHASE_3_DOC.md:1720-1722`). The exact Java
  String contract is sufficient for Phase 4; the byte wording is undefined and contradictory.
- **Severity:** correction. Replace the byte claims with equality of the final transformed String
  (and test repeated String equality), or separately publish one explicit output encoding if a
  byte-level guarantee is genuinely required.
- **Touches interface/change-trigger region:** no. Removing the undefined implementation-level
  byte wording leaves the existing String/source-map §5 interface intact.

### candidate-007 — Phase 3 records a stale PASS for an unverified Phase 1 dependency

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:17-23, 1577-1589` and
  `docs/phase1/v14/PHASE_1_DOC.md:5396-5399`.
- **Claim:** The Phase 1 document is a valid verified dependency contract and needs no fresh review
  before Phase 3 consumes it.
- **Evidence:** Phase 3's header says that `PHASE_1_REVIEW_15.md` is literal PASS and that no
  Phase 1 review was needed (`docs/phase3/v1/PHASE_3_DOC.md:19-22`), while §5.2 consumes concrete
  Phase 1 contracts including the module seam, capability profile, diagnostics, logging, debug
  flag, notice mechanism, and conformance extension (`docs/phase3/v1/PHASE_3_DOC.md:1577-1589`).
  The selected Phase 1 document expressly says its earlier PASS is historical after a binding §5
  change, that the document is not verified, and that it is not a valid dependency input until a
  fresh literal PASS (`docs/phase1/v14/PHASE_1_DOC.md:5396-5399`). The governing invariant requires
  every dependency PHASE document to be verified before a dependent reads it, with only the
  Phase-12/Phase-7 soft exception (`docs/design/v2.0-RC3/DESIGN.md:600-605, 632-644`). Phase 3
  has no such exception. This invalidates the declared dependency gate, not merely a historical
  label.
- **Severity:** blocking. Phase 3 cannot be treated as a valid consumable design on this dependency
  basis. The stale assertion must be removed, Phase 1 must receive a fresh whole-document literal
  PASS, and the consumed §5 surface must then be re-derived before Phase 3 closure.
- **Touches interface/change-trigger region:** yes. Phase 3 consumes Phase 1's binding interface,
  and Phase 1's own fresh-review trigger is part of the cross-phase readiness contract.

### candidate-008 — Failed loads expose unresolvable diagnostic IDs

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:387-405, 1398-1405, 1558-1570` and
  `docs/phase1/v14/PHASE_1_DOC.md:3774-3792`.
- **Claim:** Phase 7 and Phase 12 can consume a `Failed` result and correlate its diagnostic IDs
  with the diagnostics emitted through the Phase 1 reporter without guessing.
- **Evidence:** The public result has `Failed(PackLoadFailure)` but the complete publication table
  does not publish a closed `PackLoadFailure` or `DiagnosticId` contract
  (`docs/phase3/v1/PHASE_3_DOC.md:387-405, 1398-1405`). The failure prose promises a closed code,
  summary, and immutable diagnostic IDs, while saying only that the detailed cause is reported
  through `DiagnosticReporter` (`docs/phase3/v1/PHASE_3_DOC.md:1558-1570`). Phase 1's
  `EngineDiagnostic` has no ID and `DiagnosticReporter.report` returns void
  (`docs/phase1/v14/PHASE_1_DOC.md:3774-3785`); its channel fan-out supplies no lookup or
  correlation mechanism (`docs/phase1/v14/PHASE_1_DOC.md:3788-3792`). A success-side
  `PackConfiguration.diagnostics` list cannot describe a failed result because failed loads publish
  no configuration. Consumers therefore cannot resolve the promised IDs.
- **Severity:** correction. Publish an immutable `PackLoadFailure` with either the exact diagnostic
  collection returned for that failure or a typed ID and explicit identity/order mapping to the
  reporter's emitted diagnostics; add the failed-load correspondence tests.
- **Touches interface/change-trigger region:** yes. This changes the public load-failure surface
  consumed by Phases 7 and 12.

### candidate-010 — Phase 4's materializer and active OptionState handoff are not closed

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:393-405, 519-535, 935-936, 1407-1410,
  1477-1489, 1572-1575`.
- **Claim:** Phase 4 can obtain the Phase 3 materializer and the exact immutable active
  `OptionState` from the published `PackConfiguration` without inventing accessors or value
  semantics.
- **Evidence:** `PackConfiguration` exposes only `SourceCatalog sources` and
  `OptionConfiguration options` (`docs/phase3/v1/PHASE_3_DOC.md:393-405`). `SourceCatalog` is
  said to own a `SourceMaterializer`, and the materializer requires raw `OptionState`, but no
  `SourceCatalog` accessor or equivalent configuration route is declared
  (`docs/phase3/v1/PHASE_3_DOC.md:519-529`). The §5 row merely names the types and later assigns
  plan-dependent materialization to Phase 4 (`docs/phase3/v1/PHASE_3_DOC.md:1407`); it gives no
  callable path. `OptionState` is mentioned as an immutable reload result and as the input to
  `ProgramStateModel.evaluate`, but no public active-state projection, identity/value algebra,
  defaults, ambiguity behavior, or deterministic ordering is defined
  (`docs/phase3/v1/PHASE_3_DOC.md:935-936, 1477-1489`). The target simultaneously forbids
  consumers from reopening or reinterpreting the pack or bypassing the materializer
  (`docs/phase3/v1/PHASE_3_DOC.md:1572-1575`).
- **Severity:** correction. Add a named materializer accessor/route and a closed immutable
  `OptionState` current-state projection, then state the exact Phase 4 call and handoff test.
- **Touches interface/change-trigger region:** yes. Both missing routes are required by the
  published Phase 4-facing §5 surface.

### candidate-011 — Persistence codecs are promised but have no callable consumer contract

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:924-936, 1407-1411, 1740-1741, 1977`.
- **Claim:** Phase 12 can implement apply/discard and reload paths against a defined Phase 3
  persistence API rather than reimplementing file parsing and writing.
- **Evidence:** The §5 table exposes `OptionPersistenceCodec` and `GlobalShaderOptionsCodec` only
  as names plus the ISO-8859-1 formats (`docs/phase3/v1/PHASE_3_DOC.md:1407-1411`). The detailed
  section specifies changed-only versus global files, read retention, stable ordering, atomic
  move, symlink behavior, and immutable reload state, but no read/write methods, location or
  provider inputs, result types, missing/malformed behavior, diagnostic association, or typed
  write failures (`docs/phase3/v1/PHASE_3_DOC.md:924-936`). Phase 12 is assigned invocation timing
  but is told not to change the codecs (`docs/phase3/v1/PHASE_3_DOC.md:1977`), so it cannot
  implement the required paths from the current publication surface without inventing or
  duplicating the codecs.
- **Severity:** correction. Close both codec interfaces or one explicitly typed common abstraction
  with exact read/write signatures, immutable results, paths/identity, diagnostics, and all
  missing/malformed/write-failure outcomes.
- **Touches interface/change-trigger region:** yes. The named codec contracts are directly
  exposed to Phase 12 in §5.

### candidate-012 — ResourceRequirements erases the contract's per-buffer default clear colors

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1093-1105, 1121-1123, 1491-1514` and
  `docs/research/v1/RESEARCH.md:1200-1208`.
- **Claim:** When no explicit `colortexNClearColor` override exists, the published clear
  requirements preserve the authoritative default for each color attachment.
- **Evidence:** RESEARCH assigns fog color to colortex0, solid white to colortex1, and transparent
  black to colortex2–7 (`docs/research/v1/RESEARCH.md:1200-1208`). Phase 3 publishes
  `ColorAttachmentRequirement(..., boolean clear, Vec4f clearColor)` and says it is the
  Phase-5-consumed clear requirement (`docs/phase3/v1/PHASE_3_DOC.md:1093-1105, 1121-1123`).
  Its sole binding baseline instead creates every attachment with `RGBA`, clear enabled, and
  `Vec4f(0,0,0,0)` (`docs/phase3/v1/PHASE_3_DOC.md:1502-1509`), with no symbolic fog/white
  variant or authorized absent-entry projection anywhere in the target. A finite transparent-
  black vector cannot express the two index-specific defaults. The target itself says that a
  changed published default or consumer interpretation is an interface change
  (`docs/phase3/v1/PHASE_3_DOC.md:1511-1514, 1593-1599`).
- **Severity:** correction. Use a closed index-aware default/explicit-color algebra, or make
  no-override color absent and explicitly authorize Phase 5 to project fog/white/transparent-black
  by attachment index; preserve explicit overrides and add per-index tests.
- **Touches interface/change-trigger region:** yes. The published `ResourceRequirements` value and
  its consumer interpretation must change.

### candidate-013 — Clear and mipmap directive rows omit their authoritative stage scopes

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:759-797, 1070-1088, 1093-1105, 1511-1514` and
  `docs/research/v1/RESEARCH.md:1155-1186, 210-225`.
- **Claim:** The conformance map and scanner preserve the program-family restrictions for
  `colortexNClear`, `colortexNClearColor`, and `colortexNMipmapEnabled`.
- **Evidence:** The authoritative Appendix A.3 rows restrict clear and clear-color to
  composite/deferred scope and mipmap generation to composite/deferred/final scope
  (`docs/research/v1/RESEARCH.md:1183-1186`); these are program-family stages distinct from the
  source extensions and stage order (`docs/research/v1/RESEARCH.md:210-225`). Phase 3's rows map
  only each value to a field and test (`docs/phase3/v1/PHASE_3_DOC.md:794-797`). Its generic
  scanner sentence supplies explicit restrictions only for `.vsh` attributes/countInstances and
  `.gsh` geometry, not these three directives (`docs/phase3/v1/PHASE_3_DOC.md:1079-1088`). A
  wrong-family clear can mutate a global attachment requirement and a wrong-family mipmap can
  mutate a per-program requirement. No target row or test defines the allowed program-family set
  or wrong-family warn/ignore behavior. Since Phase 3 owns Appendix A.3 recognition and
  aggregation, this cannot be deferred to Phase 5 or Phase 4.
- **Severity:** correction. Add directive-specific family predicates, source-attributed warning
  and ignore behavior that retains the prior/default value, the corresponding §5 producer-scope
  wording, and parameterized wrong-family fixtures.
- **Touches interface/change-trigger region:** yes. Correct scope filtering changes the published
  `ResourceRequirements` values and must be reflected in the binding contract.

## 2. Checked and clean

- The mandatory thirteen-section skeleton, OQ-7 spike, pure-`:engine` placement, Appendix F.1
  ownership map, Appendix F conformance rows, remaining Appendix A.3 rows, discovery-generation
  rules, normalized-path projection, and mapping-rule algebra were rechecked. No additional
  candidate outside the supplied set was created.
- The declared-uniform fingerprint construction is acyclic: the canonical payload excludes the
  embedded materialization result, and the catalog/materialized-source equality promise is
  otherwise coherent. The surviving issue is the payload's unspecified current schema value, not
  the previously corrected self-reference.
- The String/source-map materialization handoff is present and precise for Phase 4: the exact
  final Java `String` and its map are published. Candidate-005 is admitted only for the separate
  undefined “byte-identical” wording; no UTF-8 output interface is required.
- The ID-map presence states, forced-11300 isolation, selector/predicate algebra, origin variants,
  fingerprints, and schema equality are otherwise aligned. Candidate-002 concerns only the
  uncombined absent-source/invalid-request precedence.
- The public failure/result, materializer result, program-state, resource-requirement, texture,
  and custom-expression surfaces retain their stated ownership boundaries. The findings above are
  reachability, closure, provenance, or semantic-fidelity gaps, not a demand that downstream
  phases perform work assigned to Phase 3.
- The selected Phase 1 runtime interfaces named by Phase 3—module/seam placement,
  `GLCapabilityProfile`, logging, `EngineDiagnostic`/`DiagnosticReporter`, debug flag, notice
  mechanism, and conformance extension—exist in the dependency's §5. Candidate-007 is a
  verification-readiness failure despite those interface declarations being present.
- The complete supplied finder clean areas were checked: no additional repeated identifier,
  path/number/round inconsistency, ownership omission, Pintonium-pitfall regression, or unmapped
  in-scope row survived. Prior Round 14's clearance of the colortex scope concern does not settle
  the current candidate because the current target still gives no directive-specific
  composite/deferred/final family predicates or tests.
- The Gate-dropped candidates `candidate-003`, `candidate-006`, and `candidate-009` remain excluded
  for unverifiable citations. No surviving candidate was cleared on independent re-derivation.

## 3. Verdict

# FAIL
Counts: blocking=1; corrections=9; notes=0
Interface changed: yes

The nine admitted corrections are individually fix-up-sized, but `candidate-007` is a hard
verification gate: the selected Phase 1 document explicitly is not a valid dependency input, and
Phase 3's current header falsely authorizes its consumption. This is a structural process failure,
not a wording-only correction; Phase 3 cannot close or serve dependents until Phase 1 receives a
fresh whole-document literal PASS and the consumed Phase 1 §5 surface is re-derived. The current
surface also contains nine independent corrections, seven of which alter the monitored §5
interface region, so the prior Round 34 literal PASS does not establish convergence for this
post-review surface.

The next required action is to stop dependent consumption, correct the Phase 3 dependency ledger
and all nine admitted defects, obtain a fresh Phase 1 whole-document PASS, then re-derive the
selected Phase 1 §5 contract and run a fresh whole-document Phase 3 verification. Any resulting
Phase 1 or Phase 3 §5 change must retain the fresh-review trigger before closure.
