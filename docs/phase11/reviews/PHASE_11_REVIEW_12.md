# Phase 11 — Fresh whole-document architecture review R12

**Owner:** `docs/phase11/v1/PHASE_11_DOC.md`, complete §§0–12.

**Frozen owner SHA256:** `468d15aa32eeeb77bf3a7724f9220a0bd0ea1d2412fc2624966dd25188c3196c`, as commissioned and recorded for review round 12 in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_5.json:58–62`. This reviewer performed no checksum/validation command and made no repository mutation; the hash is the supplied frozen identity, not a newly measured result.

**Required owner corrections:** 0. **Notes:** 2. **Section 5 correction impact:** false.

## Scope and authority

This was a fresh review of the entire current owner, not merely an assessment of its R11 resolution text. Read all thirteen owner sections, the owner diff, repository rules and `docs/MOVES.md`; independently examined the governing requirements before reaching a verdict. The selected authority is the owner's header-selected `docs/design/v3/DESIGN.md`, not a global newest revision. The review covered its complete Part I G0–G12 and Phase 11 specification at lines 2281–2353, together with `docs/research/v1/RESEARCH.md` §§0–1, §3.4 item 4, §6.3, Appendix D, Appendix F.6 and the OQ-22 ledger row.

Primary language evidence was the shipped `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:326–425`. The narrow permitted behavior digest at `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:594–622` supplied smooth-equation corroboration only. Dependency checks used the current P3/P6 §5 grants and their incorporated expression, schema, dispatch and lifetime contracts. Reciprocal receiver checks covered P7 composition, P2 v2 evaluator conformance, P12 GUI presentation, P1 diagnostic adaptation and P14 measurement intake. These checks establish evidence for this owner's interfaces; they do not certify those owners.

R11 and its resolutions were consulted only as history after reading the current contract and relevant receivers. Neither its previous verdict nor the attempt inventory's recorded trace results was treated as proof.

## Independent findings and checks

### 1. Language and evaluator architecture

The conformance map at `docs/phase11/v1/PHASE_11_DOC.md:326–448` covers both declaration forms, constants, operators, all 32 named functions, vector/color and matrix access, biome inputs and all fourteen view booleans. The seven excluded names follow the authoritative union in `docs/research/v1/RESEARCH.md:1498–1505`; the owner's decision and upstream-discrepancy entry preserve that priority rather than adopting the shorter design restatement.

Owner §§4.1–4.6 specify declaration adaptation, deterministic identity, grammar/precedence, scalar and vector typing, declaration-boundary conversion, variable resolution and reachability, memoization, finite/domain failures, and effect ordering. The finite binary32 and additional exact-semantics decisions are explicitly distinguished from the published function-name/signature evidence in D-P11-3/D-P11-13. The interpreter remains v0.4's selected backend, with implementation-private executable machinery and a public semantic-ID selector, not an ungranted compiled implementation. Error isolation and the §§8–12 implementation gates remain substantive and prospective.

No additional required owner defect was established by this whole-language review.

### 2. R11-C1: per-cell elapsed time and transactional state

The corrected cell carries `lastCommittedEvaluationSeconds` at owner line 680; its delta is defined against the controller clock at lines 683–698. Lines 706–725 explicitly separate once-per-observed-frame clock advancement from each reached cell's last successful evaluation. Lazy skipping writes neither cell value nor timestamp. Context/definition failure cannot cause a retry in the same frame to add time again, while a successful definition commits its value/timestamp pair together. A valid variable's committed state is not undone by a later dependent-uniform error.

Independently followed the two named tables at owner lines 1339–1340. For lazy late use, the clock sequence is `0,1,1,2,2`; the skipped branches leave the cell timestamp unchanged, so the later reached sites receive one second and produce `0,0,1,0,0`. For late first use, the cell stays uninitialized through the first two observations, initializes to 0.25 at controller time 1 and reaches 1 at time 2, giving `0,0,0.25,0,1`. These are deductions from the written state transitions, not executed tests. The rollback obligations at lines 1469–1470 cover both state components. R11-C1 is resolved without eager evaluation or a program-keyed smoothing cell.

### 3. R11-C2: an actual typed diagnostic route

The owner now publishes `ExpressionDiagnosticSink.report(ExpressionDiagnostic)` and requires it after the metrics sink in the factory at lines 1029–1032. Lines 828–848 specify synchronous receipt before the operation returns, exact immutable records and IDs, ordinary versus structural result behavior, coalescing, borrowed lifetime, terminal release, and nonrecursive containment of destination RuntimeException. A lost delivery cannot change expression state or upload counts, and the receiver must independently record its delivery failure.

The receiving dispatch is present, not merely implied: `docs/phase7/v1/PHASE_7_DOC.md:3325–3359` constructs and installs the actual controller with a real P7 collector, forwards returned compile diagnostics once, receives runtime/activation records through `report`, and applies the P1 adaptation. `docs/phase2/v2/PHASE_2_DOC.md:1756–1774` supplies the same required typed sink and compares the actual kind/ID/location sequences; collector failure makes the run fail rather than producing an apparently empty successful observation. P1's receiving `DiagnosticReporter` and channel fan-out exist at `docs/phase1/v14/PHASE_1_DOC.md:4643–4672`; its registered expression log channel is at line 4577. No implicit global reporter or reverse conversion from host messages is needed. R11-C2 is resolved.

### 4. R11-C3: source-less pre-plan failures reach their consumers

Owner lines 792–797 close the diagnostic-location domain as Declaration or SourceLess. Both compiler entry points reject unsupported backend IDs before adaptation/parsing, including empty declaration lists (lines 506–508 and 985–990). The failure's kind, severity, channel and stable-ID inputs are fixed without fabricated attribution.

The source-less variant is explicitly consumed by P2 at `docs/phase2/v2/PHASE_2_DOC.md:1761–1774`. The owner projector handles it with an empty declaration name and fixed safe summary at owner lines 883–890. P7 preserves the typed distinction, and `docs/phase12/v1/PHASE_12_DOC.md:1148–1173,1391–1405` forwards the exact optional snapshot through `showErrors`; an absent declaration label does not discard the entry. The unsupported-empty vector at owner line 1341 exercises this specified path. R11-C3 is resolved.

### 5. Current dependency and lifecycle symmetry

P3's published declaration algebra and lossless duplicate-preserving order at `docs/phase3/v1/PHASE_3_DOC.md:3433–3455` match owner §5.2. The exact-current containing/nested schema22 receipt at owner lines 1139–1152 agrees with the producer's current-schema discipline at `docs/phase3/v1/PHASE_3_DOC.md:4084–4099`. Older numeric receipts are explicitly historical; no old-schema upgrade, alternative parser, asset acquisition or inferred registry getter is introduced.

P6's incorporated §4.13 supplies all six typed upload commands, the closed submission outcomes and refresh results, the versioned fixed schema and same-activation value view. Its actual consumer dispatch at `docs/phase6/v1/PHASE_6_DOC.md:1621–1668` implements name/type/absence/duplicate routing, authoritative counters, accepted-prefix semantics and the superseding invalid-counter branch. Owner §4.8 follows those cases rather than equating absence with rejection or using integer uploads as an undocumented boolean convention.

P7's current construction/install/compile/reset/activation sequence at `docs/phase7/v1/PHASE_7_DOC.md:3325–3347` now receives the owner's §4.12/§5.5 requirements. It installs before first P6 use, compiles the exact configuration/schema, activates a complete provider/random tuple after adoption, and resets at the documented lifecycle boundaries. P11 CLOSE remains distinct from P6 retirement; replacement, unpublished abort and shutdown ordering do not invent a P6 CLOSE alias or release services after rejected retirement.

### 6. GUI, conformance and measurement handoffs

The GUI route is final-attempt publication, not compiler-success publication: owner §§4.9.1/5.5.1, P7 lines 3308–3323 and P12 lines 1391–1405 agree on final ACCEPTED/REJECTED outcome, selected-pack identity, positive serial, empty clearing, rejected-attempt display and source-free presentation.

Owner §5.6 and `docs/phase2/v2/PHASE_2_DOC.md:1741–1785` agree on original-vector cases, all closed steps, exact observations, script exhaustion, unsupported/failure aggregation and disposition-only matrix cases without an independent oracle. This is an evaluator run, not a substituted Properties or shader-load check.

`docs/phase14/v1/PHASE_14_DOC.md:1722–1732,2300–2318` receives the owner's exact metrics and OQ-22 method. Real-pack budget miss plus AST-dispatch attribution remains the conditional compiler trigger. No optional public timer, backend implementation or runtime result is inferred from that adoption.

## Numbered notes and limitations

### N1 — Historical reference availability and confidence

The owner cites the historical `reference-src/pintonium-9c2fcc1/...` Java snapshot, but the exact CustomUniforms, IrisFunctions and CommonIrisRenderingPipeline paths were unavailable in this checkout. `docs/MOVES.md` was consulted; no historical-source identity was silently substituted.

Permitted narrow reads of the separately identified `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/uniforms/custom/CustomUniforms.java:46–176,224–275` corroborate the generic resolver/dependency/reachability shape. `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/parsing/IrisFunctions.java:39–73,250–300` corroborates the commented-out round registrations and vararg limitations. These current bytes do not authenticate the missing historical pin, prove its precise cadence, or establish a stareval reuse license. The clean-room outcome and independent published language authority remain intact. **Severity: note; no required owner correction.**

### N2 — Architecture acceptance is not implementation or integration clearance

This review establishes no executed language conformance, pack tier, GL behavior, timing/allocation result or sibling PASS. Owner §§8/10/12 and the governing final integration rule retain those separate gates. The historical verification-profile/checklist prose is read with `docs/MOVES.md`'s explicit tooling-retirement record, not as an instruction to recreate or run the retired harness. No formatters, linters, builds, tests, runtime tools, source edits or report-file writes were performed. No forbidden transcripts, transformer implementation or OptiFine decompile source was read. **Severity: note; cross-owner/final-integration disposition remains Main's responsibility.**

## Final verdict and disposition

**PASS**

There are zero required current owner corrections and no structural rebuild requirement. R11-C1–C3 are resolved in the frozen owner, with their changed receiving shapes explicitly present in the relevant consumers. Preserve this report as Phase 11 R12 evidence; no further owner §5 amendment is requested by this review. Final integration and all implementation/runtime clearance remain separate.
