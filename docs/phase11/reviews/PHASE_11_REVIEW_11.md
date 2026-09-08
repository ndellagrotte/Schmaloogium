# Phase 11 — Fresh whole-document architecture review 11

**Verdict: PASS-WITH-CORRECTIONS**

- **Blocking findings:** 0
- **Owner corrections:** 3
- **Notes:** 3
- **section5Impact: true**
- **Owner:** `docs/phase11/v1/PHASE_11_DOC.md`, all §§0–12.
- **Frozen identity:** SHA-256 `962e74f5ffc1aa497199787e5483d7562c4753b46e72add2716efeb5e53b1829`, as commissioned and subsequently confirmed unchanged by Main. This reviewer did not run a checksum or validation command.

The architecture is substantially complete and does not require a subsystem rebuild. However, the current smooth clock loses elapsed time for lazily reached cells, and two promised diagnostic paths lack sufficient published inputs or delivery operations. These are current whole-owner defects, not objections to the already-remediated historical integration findings.

## Authority, scope and evidence

Reviewed the complete owner document under its header-selected `docs/design/v3/DESIGN.md`, all Part I global sections G0–G12, the Phase 11 specification, and particularly §§G1.2–G1.3/G5.3. Read the governing `docs/research/v1/RESEARCH.md` §§0–1, §3.4, §6.3, Appendix D and Appendix F.6, the shipped `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties` custom-expression section, and the narrow smooth behavior digest at `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:594–622`.

Dependency review covered current Phase 3 §5 and its expression/schema/fingerprint publication, and current Phase 6 §5 with incorporated §4.13 bridge and §4.14 lifetime/failure rules. Receiving-side checks covered current Phase 2 **v2** §4.9, Phase 7 construction/lifecycle and diagnostic publication, Phase 12 direct diagnostic consumption, and Phase 14 §5.8/L-11/S-22-6. No sibling is certified by those seam checks.

## Owner corrections

### P11-R11-C1 — Account for elapsed time per successfully evaluated smooth cell

**Location:** Owner §4.7, especially lines 702–707; related §§4.6, 4.8 and 5.6.

**Evidence:** §4.6 makes `if` lazy and forbids effects in an unselected branch. §4.7 gives positive `dt` only to the first refresh with a changed frame counter, gives every subsequent switch in that frame zero, and returns a smooth cell's stored value unchanged whenever `dt <= 0`. Its `SmoothCell.lastClockEpoch` is declared but never used by the transition.

**Failing architectural trace:** Use the valid expression `if(is_in_water,smooth(7,temperature,1),0)`. Initialize its cell at target zero. In every subsequent frame, script the first context snapshot with `is_in_water=false`, and a later switch with `is_in_water=true` and target one. The first switch consumes the frame's positive controller delta without evaluating the cell; the later switch evaluates it with zero. The output remains zero indefinitely despite repeated evaluation and advancing frame time. These are legal per-refresh provider snapshots under §4.10; the contract does not freeze view flags for the entire frame.

**Required correction:** Advance a controller clock at most once per frame, but calculate a cell's elapsed time from its own last successful committed evaluation. Update that timestamp transactionally with its value; a skipped lazy branch must not spend that cell's elapsed time. Preserve same-frame no-double-advance, reset semantics and definition-level rollback. Add an independently tabulated late-first-use/lazy-site trace to the existing §5.6/§8 smooth expectations. Do not repair this by eagerly evaluating unselected branches.

### P11-R11-C2 — Publish a runtime diagnostic delivery seam

**Location:** Owner §4.12 lines 982–984, §4.9 runtime-error rules, §5.1 controller/diagnostic rows, §5.5 item 6 and §5.6 lines 1240–1241.

**Evidence:** `CustomExpressionControllerFactory.create` accepts only an `ExpressionMetricsSink`; controller activation accepts only plan/context/random, and the inherited refresh accepts only program/built-ins/uploads. No exposed controller operation returns runtime `ExpressionDiagnostic` records. `Completed` contains counts only; `Aborted` carries one structural diagnostic ID, not ordinary expression errors. Yet §4.9 requires a chat warning and detailed log when an individual uniform fails, and §5.6 requires exact runtime diagnostic kinds/stable IDs from an “established recording diagnostic destination.” Phase 1 §4.9.4/§5 publishes `DiagnosticReporter`, but it is not connected to this controller contract. Phase 6 §4.13 explicitly leaves expression-error ownership with Phase 11 and cannot infer these omitted errors from upload commands.

**Impact:** The documented division-by-zero vector returns only `Completed(1,0,0)` and the surviving uniform command. Composition and the conformance adapter cannot receive the failing uniform's diagnostic through any specified operation. A private assumed global reporter or an undocumented factory constructor would introduce another integration contract rather than implement the published one.

**Required correction:** Specify a public construction-time diagnostic destination or explicit diagnostic-emission capability, with ownership, synchronous delivery, stable identity preservation, ordinary-error versus structural-error behavior, and destination-failure containment. Define the existing Phase 1 chat/log adaptation without changing the separately adopted source-free GUI projection. Synchronize §5.1/§5.5/§5.6 and the P2/P7 receiving obligations so production and original-vector runs use the same observable route.

### P11-R11-C3 — Make pre-plan diagnostic attribution representable

**Location:** Owner §4.11 lines 938–942; request/adapter declarations in §4.1 lines 458–478; diagnostic record in §4.9.

**Evidence:** An unsupported backend must fail before parsing declarations and the error must use “compile-request attribution.” Neither `CustomExpressionCompileRequest` nor `compilePhase3(...)` has such an input. Only individual `CustomExpressionSource` entries carry `SourceAttribution`. The declaration list is allowed to be empty, including for a valid pack with no custom declarations under Phase 3 §5.1.

**Impact:** An unsupported-backend request with an empty declaration list has no source attribution from which to construct the mandated error. Choosing an arbitrary declaration when present would still not implement request-level attribution, and inventing a pack path/coordinate would misstate provenance.

**Required correction:** Either add and define explicit request-level attribution at both entry points, or publish a source-less diagnostic location variant/optional with exact pre-plan semantics. Preserve the specified stable-ID inputs and pre-parse failure behavior. Update the incorporated §5 contract and the unsupported-backend conformance expectation. Do not manufacture a source coordinate.

## Positive architecture checks

- **Doc gate and milestone completeness:** All thirteen template sections are present. §§3.1–3.3 map both declaration forms, operators, all 32 named functions, vector/matrix access, biome inputs, all fourteen view booleans and exclusions. §§4.11/9/10 select the v0.4 interpreter and retain a conditional Phase 14 experiment rather than silently requiring compilation. §§8/12 contain implementation acceptance obligations rather than claimed executions.
- **Authority/licensing:** The seven-name exclusion union follows Research Appendix F.6 over DESIGN's stale five-name restatement and is explicitly recorded. Clean-room evaluation is the selected outcome of the unverifiable stareval license gate. PD is treated as evidence, not a language specification; no transformation dependency is adopted.
- **P3 intake:** §5.2's current schema21 receipt matches current P3 §5.3: exact-current containing/nested schemas before extraction/reuse, unchanged configuration identity, ordered duplicate-preserving declarations, no Properties reparse, no binary-asset acquisition. Older numeric receipts remain historical, not competing admission branches.
- **P6 dispatch and lifetime:** All six custom command types map to the corresponding P6 variants, including `Bool1`. `Accepted`, `SkippedAbsent` and `Rejected` each have explicit counter handling. `NoCustoms`, `Completed` and `Aborted` agree with the P6 sink ledger and accepted-prefix/invalid-counter distinction. Current terminal P6 retirement is no longer confused with P11's own CLOSE; rejected retirement retains borrowed services and admission remains closed.
- **Current handoffs:** P2 v2 §4.9 explicitly receives the complete original-vector contract, all closed step/result variants and unsupported/failure aggregation. P7 §5.1 and P12 §§4.9/5.3 receive the final-attempt source-free GUI projection with clearing and serial/pack rejection rules. P14 §5.8 and S-22-6 receive the exact metrics/method and require a real-pack miss plus AST-dispatch attribution before a compiler candidate. Historical IR-10/15/17/20 are not re-raised.

## Notes and limitations

1. **Receiver note — P7 composition remains an adoption obligation.** P11 §5.5 already explicitly requires controller creation, pre-first-use installation, compilation, fresh provider/random activation and lifecycle forwarding. Current P7 §4.1's ten-step construction procedure and §5.5 phase-11 row name the custom participant, terminal retirement and GUI projection, but do not supply the controller/context/random activation and nonterminal-reset sequence. The integration review §4.11 already records this as a pending composition handoff. This is not counted as another P11 owner correction or sibling verdict.
2. **Verification boundary.** This prospective review does not certify current P3/P6 dependency eligibility, any sibling, implementation, real-pack compatibility, allocation budgets or runtime behavior. §G5.3/IR-01 remain independently required. No tests, builds, formatters, linters or runtime validation were run, and none is needed to demonstrate the static contract contradictions above.
3. **Historical reference availability.** The cited `reference-src/pintonium-9c2fcc1/...` Java paths were unavailable locally. The separately identified current `reference-src/Pintonium-main/.../CustomUniforms.java` and `.../IrisFunctions.java` were read narrowly; they corroborate the generic dependency/reachability architecture and commented-out `round`/vararg limitations, but do not authenticate the historical snapshot or establish stareval reuse permission. Historical evidence is preserved rather than relabeled. No forbidden source or transcript was read.

## Required disposition

Apply C1–C3 as bounded owner corrections, synchronize the affected §5 declarations/semantics and receiver obligations, record resolutions, then obtain a fresh whole-document review because §5 changes. **This frozen owner is not PASS, but its architecture is repairable without rebuilding the phase.**

## Resolutions

- **P11-R11-C1:** Owner §4.7 now advances one controller clock per observed frame, while reached
  cells measure from their own last successful committed evaluation. Value and timestamp commit
  or roll back together; lazy skipping spends neither. §§5.6/8 publish independent exact
  lazy-late-use and late-first-use tables; cadence, failure, milestone/checklist and D-P11-22 agree.
- **P11-R11-C2:** §§4.9/4.12/5 require `create(ExpressionMetricsSink, ExpressionDiagnosticSink)`
  and exact typed `report(ExpressionDiagnostic)` delivery. Synchronous ordering, borrowed lifetime,
  coalescing, result/counter independence and nonrecursive destination-failure containment are
  explicit. P7 receives real collection/P1 adaptation duties and P2 captures the same typed route;
  GUI still uses only the safe direct projection. D-P11-23 records the cutover.
- **P11-R11-C3:** `ExpressionDiagnosticLocation.Declaration` preserves actual attribution/span;
  `SourceLess` represents pre-plan failures. Both compile entry points reject unsupported backends
  before adaptation/parsing even with no declarations, using unchanged kind/pack/backend stable-ID
  inputs. GUI emits empty declarationName for SourceLess; §5.6 includes unsupported-empty
  expectations and §§8/9/12/D-P11-24 carry the contract.
- **Coordinated receipt:** D-P11-21 adopts schema22 via exact current-constant containing/nested
  admission; older receipts are historical. Other P3 asset/native/option and nine-tree
  projectionVersion=1 contracts are preserved.

These are prospective architecture corrections only. Original review body/verdict is preserved.
§5 changed; fresh whole-document and affected receiver review remain required. No validation,
tests, build, formatter or linter was run, and no implementation or PASS clearance is asserted.