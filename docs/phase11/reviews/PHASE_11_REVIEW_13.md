# Phase 11 — Whole-owner Architecture Review R13

## Frozen artifact and disposition

- **Owner:** `docs/phase11/v1/PHASE_11_DOC.md`
- **Review:** Phase 11, round 13, architecture-review attempt 6
- **Frozen SHA256:** `f5264ac2d5303a705e8183dbdaf1b30eeb8a94c2b195f4e940b7128532f4c2bf`
- **Inventory:** `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_6.json`, Phase 11 entry. The assigned digest agrees with that inventory; no checksum/validation command was run.
- **Verdict:** **PASS-WITH-CORRECTIONS**
- **Required corrections:** 1
- **Notes:** 1
- **§5 impact:** **No required interface change.** C13-1 repairs the internal graph algorithm to satisfy the already-published dependency ordering and error-isolation behavior. Its minimal fix changes §4.5 and the associated §8.1 test specification, not a callable shape, schema, receiver grant, or intended §5 semantic. This does not waive any existing current-owner or integration review gate.

The owner was read completely, including all sections 0–12 and the current D-P11-25 receipt. No repository files were edited or reports written. This is an independent architecture review, not a confirmation of earlier fixes or a certification of any sibling.

## Scope and governing authority

The selected authority is **`docs/design/v3/DESIGN.md`**, as explicitly declared by the owner header, not the globally newest design by assumption. I read Part I, §§G0–G12, and the Phase 11 specification at `docs/design/v3/DESIGN.md:2279–2353`. The specification requires the entire Appendix F.6 language, persistent time-corrected smoothing, interpreter/backend decision, error isolation, provider seams, and a recorded clean-room/license outcome.

The controlling research was independently checked at `docs/research/v1/RESEARCH.md:11–111` (§§0–1), `:288–306` (§3.4), `:781–788` (§6.3), `:999–1028` (OQ ledger), `:1322–1383` (Appendix D), and `:1493–1512` (Appendix F.6). `AGENTS.md` and `docs/MOVES.md` were read for source restrictions, per-document governance, moved references, and retirement of the old verification machinery.

The dependency checks used current Phase 3 and Phase 6 publications and their incorporated declarations. Reciprocal receiving checks were limited to the load-bearing Phase 7 construction/lifecycle/collector route, Phase 12 presentation route, Phase 2 evaluator adapter, Phase 14 measurement receiver, and Phase 1 diagnostic routing. Those checks establish interface agreement or identify an owner defect; they do not give those documents review verdicts.

## Independent checks

### 1. Complete language and authority mapping

The owner’s declaration, token, operator, function, member-access, type, and input tables at `docs/phase11/v1/PHASE_11_DOC.md:326–448` cover the Appendix F.6 named surface. The shipped pack-author specification remains available at `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:326–425`; it independently supports declaration forms, program-change refresh, the named functions, documented vector/color members, and matrix row/column access.

The seven-name exclusion union follows the current controlling research, not the older five-name design summary: `docs/research/v1/RESEARCH.md:1501–1505` and the current Phase 6 schema at `docs/phase6/v1/PHASE_6_DOC.md:1595–1613`. The owner already records that upstream discrepancy in §11. The clean-room semantic choices are distinguished from the published function-name surface through D-P11-13; reference-only operators, vector extensions, and reference smoothing units are not silently admitted.

One defect remains in the graph algorithm, detailed below. No additional required correction was established in the grammar/type/function coverage or scalar/vector upload mapping.

### 2. Current Phase 3 schema23 intake

D-P11-25 at `docs/phase11/v1/PHASE_11_DOC.md:1144–1150` explicitly requires the current constant, now 23, for containing/nested/inspection identity before extraction, compilation, or reuse. It expressly makes D-P11-21 and older numeric receipts historical. Therefore the preceding parenthetical schema22 receipt is not treated as an active schema22 admission route.

This agrees with `docs/phase3/v1/PHASE_3_DOC.md:4152–4158`. Declaration intake also agrees with the current producer’s exact closed algebra and lossless occurrence semantics at `docs/phase3/v1/PHASE_3_DOC.md:3538–3559`: immutable ordered declarations, preserved duplicates and decoded expression text, unchanged source attribution, and the original configuration fingerprint. The typed selector amendment remains upstream identity data, not a new P11 selector parser or materialization API.

The receiving composition path at `docs/phase7/v1/PHASE_7_DOC.md:3389–3400` actually calls the existing `compilePhase3` entry point using the same configuration, the runtime’s fixed-input schema, the live context schema, and the canonical interpreter ID. It handles Success/Partial versus Failure rather than inventing a successful empty replacement plan.

### 3. Interpreter, bridge, and effect ordering

The interpreter remains the v0.4 baseline under `docs/phase11/v1/PHASE_11_DOC.md:961–1017`; both public compiler entry points reject unsupported backend IDs before declaration parsing, including empty input. A backend candidate remains conditional measurement work rather than a newly granted compiled implementation.

I followed actual receiving branches in `docs/phase6/v1/PHASE_6_DOC.md:1522–1669`: fixed schema/value variants, all six upload commands, Accepted/SkippedAbsent/Rejected processing, the authoritative three-counter ledger, NoCustoms, and accepted-prefix versus invalid-counter disposal. P11’s mapping and result handling at `docs/phase11/v1/PHASE_11_DOC.md:731–778` match those branches. Bool1 does not become an undocumented Int1 convention; absent active-program uniforms remain normal skips; replay remains P6-owned and does not reevaluate expressions.

### 4. Per-cell clock and reset semantics

The current state machine at `docs/phase11/v1/PHASE_11_DOC.md:658–729` separates the once-per-observed-frame controller clock from each cell’s last successful committed evaluation time. Clock observation commits before context sampling; skipped lazy sites retain elapsed time; successful same-frame revisits see zero elapsed time; value and timestamp changes commit together with the owning definition. Random consumption retains the separately specified non-rewinding order.

I manually checked the written same-frame, lazy-late-use, and late-first-use traces at `docs/phase11/v1/PHASE_11_DOC.md:1338–1340` against that state machine. Their initialization, timestamps, and snap outputs agree. This was a static trace review, not an executed vector suite. The correction equation and separate rise/fall behavior are independently supported by the permitted behavior digest at `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:597–601`; no decompiled implementation was consulted.

### 5. Typed/source-less diagnostics and complete receiving routes

The required factory sink, immutable Declaration/SourceLess distinction, synchronous delivery, attempted-ID coalescing, contained sink failure, and terminal sink release are explicit at `docs/phase11/v1/PHASE_11_DOC.md:780–864`. The unsupported-backend path is source-less before parsing at `:984–990`.

Those records have real receivers:

- Phase 7 constructs the required sink, retains exact typed records, forwards compile diagnostics once, and separately handles reporting failure at `docs/phase7/v1/PHASE_7_DOC.md:3389–3422`.
- Phase 1’s actual CHAT/SHADER_GUI/LOG_ONLY dispatch is at `docs/phase1/v14/PHASE_1_DOC.md:4762–4780`; P11’s adapter does not assume a nonexistent stableId field or reconstruct typed records from host message text.
- Phase 12 receives the unchanged optional source-free snapshot through its actual `showErrors` route, preserving source-less entries and final attempt outcome, at `docs/phase12/v1/PHASE_12_DOC.md:1176–1197` and `:1427–1441`.
- Phase 2 consumes the actual typed sink and both compiler entry points, preserves kind/ID/location sequences, fails collector loss, and dispatches Activate/Refresh/Reset/Close at `docs/phase2/v2/PHASE_2_DOC.md:1752–1795`.

No silent drop or source-bearing GUI conversion was found along these owner-produced routes.

### 6. Lifecycle, staging, and measurement ownership

The reset/deactivation and terminal CLOSE contract at `docs/phase11/v1/PHASE_11_DOC.md:1020–1087` agrees with current P6 retirement/final-use rules at `docs/phase6/v1/PHASE_6_DOC.md:1675–1795` and the actual P7 receiver at `docs/phase7/v1/PHASE_7_DOC.md:3402–3422`. P11 CLOSE is not forwarded as a P6 enum; rejected retirement does not authorize service release or revival of a closed controller; replacement and shutdown retain their distinct barrier-ordering requirements.

Sections 6–12 provide substantive failure, threading, performance, testability, milestone, decision, and implementation-gate coverage. The P14 reciprocal method/metrics contract is present at `docs/phase14/v1/PHASE_14_DOC.md:1795–1806` and `:2430–2448`. It requires measured real-pack cost and dispatch attribution before a candidate backend; synthetic stress alone is not authorization. No measurement, runtime, pack-tier result, or optional API grant is inferred.

## Required correction

### C13-1 — Make dependency traversal and cycle isolation consistent with the declared edge direction

**Severity:** correction; substantive evaluator/error-isolation defect, locally repairable.

**Owner evidence:** `docs/phase11/v1/PHASE_11_DOC.md:615–618` defines an edge `A -> B` when A reads B, specifies a Kahn traversal, and describes vertices remaining afterward as cycle members. `:624–629` then consumes the resulting topological order directly for memoized evaluation; `:746` repeats that execution rule.

**Concrete breakage:** For valid variables `a=b+1` and `b=temperature`, an ordinary zero-indegree Kahn traversal of the published graph emits a before b, the opposite of the memoization dependency order. More importantly, consider `a=b+c`, `b=a`, `c=temperature`, with one uniform reading a and an otherwise independent uniform reading c. The published graph contains a→b, b→a, and a→c. All three vertices have nonzero indegree, so ordinary Kahn traversal leaves c behind along with the actual a/b cycle. The stated residual-as-cycle rule can consequently disable c and the independent uniform, although c does not depend on the cycle. That violates the owner’s promised dependency-local failure propagation and preservation of unrelated roots. Even after correcting edge orientation, Kahn residuals can contain noncyclic dependents, so residual membership alone is not an exact cycle-membership test.

**Minimal owner fix:** In §4.5, specify dependency-first traversal unambiguously: either use dependency→reader edges with incoming dependency counts, or retain reader→dependency edges and explicitly count unresolved outgoing dependencies while updating reverse reader lists. Define root reachability consistently with that orientation. Identify actual cycle members using strongly connected components or an equivalent precise cycle-membership step; distinguish those members from transitive invalid dependents and never invalidate prerequisites merely because a cycle reads them. Add the simple chain and cycle-with-independent-prerequisite cases to the existing §8.1 graph-test specification. This does not require a new backend, public API, schema, or receiver behavior.

**Evidence character:** The examples are direct static consequences of the documented graph and standard Kahn traversal, not claims about an executed implementation. The available permitted reference independently illustrates dependency counts and reverse reader dispatch at `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/uniforms/custom/CustomUniforms.java:78–176`; it is corroboration only, not code or algorithm authority to copy.

## Note

### N13-1 — Keep historical pinned-source receipts distinct from available corroboration

**Severity:** note; evidence limitation, not a required architecture correction.

The historical source paths cited by the owner at `docs/phase11/v1/PHASE_11_DOC.md:37–41`, `:78–87`, and `:437–448` name `reference-src/pintonium-9c2fcc1`. The cited CustomUniforms path is unavailable in this checkout; consulting `docs/MOVES.md` does not establish that the available `reference-src/Pintonium-main` checkout is byte-identical to that historical pin.

Permitted current corroboration was nevertheless available and read: `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/uniforms/custom/CustomUniforms.java:46–275` supports the generic resolver/dependency/update/push/reachability shape; `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/parsing/IrisFunctions.java:250–315` still shows commented-out round registration and problematic vararg machinery. Its `SmoothFloat.java:60–111` has a different time model and is not substituted for the allowed OptiFine behavioral equation. Current pipeline update/push sites at `reference-src/Pintonium-main/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java:489–526` establish an update/push split, not P11’s required every-switch cadence; research and the P6 contract supply that authority.

The available root `reference-src/Pintonium-main/LICENSE:1–18` is GPLv3 text, not a component-specific stareval grant. It neither proves the historical pin’s license nor relaxes the clean-room restriction. Preserve historical receipts rather than rewriting them as current observations. Any future source-reuse proposal must independently establish its exact source/license identity; none is authorized here.

## Limitations and final disposition

No builds, tests, formatters, linters, runtime launches, validation commands, or repository mutations were performed. No forbidden chatlogs, root transcripts, Oculus transformation/library directories, glsl-transformer implementation, or OptiFine decompiled source were read. The available reference checkout was not promoted to the missing historical pin. Historical preflight and review statements remain historical, and the retired verification mechanism is not an executable present-day requirement supplied by this review.

The full-owner architecture is substantially complete, including the schema23 receipt, interpreter boundary, corrected per-cell clock, typed/source-less diagnostic receivers, and lifecycle coordination. C13-1 is a concrete local graph-design correction rather than a structural reason to rebuild the document. Resolve it in the owner before architecture closure. Current dependency/receiver reviews and final integration remain separate gates.

**Final verdict: PASS-WITH-CORRECTIONS.**

## Resolutions

2026-09-08 — C13-1 is addressed in the owner by D-P11-26. Section 4.5 now
defines exact SCC cycle membership, reverse-reader invalidity propagation, forward
dependency reachability, and prerequisite-first Kahn emission using unresolved
dependency counts. Independent prerequisites of cycles remain usable. Section 8.1
adds the forward chain and cycle/shared-prerequisite observable cases. This is an
internal planning correction, not a schema/SPI/§5 shape change. No expression engine
or runtime test was implemented here; fresh whole-owner review remains required.
