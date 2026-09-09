# Schmaloogium — Phase 11: Custom-uniform expression engine — Architecture

## 0. Header

- **Phase:** 11 — Custom-uniform expression engine
- **Milestone:** v0.4
- **Module/package:** `:engine`, `com.schmaloogium.engine.expr`
- **Declared dependencies:** Phase 3 and Phase 6; no Phase 7 dependency
- **Governing design:** `docs/design/v3/DESIGN.md`
- **Design status:** verified per §G1.3 (attempt-11 wave, R18 2026-09-08); historical Review 10 PASS certified only its dated baseline
- **Date:** 2026-08-03

The commissioning request explicitly selected v3. This document therefore derives its pins from
`docs/design/v3/DESIGN.md` itself and does not transplant coordinates from another revision. The
governing phase row says “Depends on: 3, 6 (no Phase 7 dependency)” and assigns the complete
expression language to pure `:engine` code (`docs/design/v3/DESIGN.md:2281`–`:2288`). The global
module map puts that code in `engine.expr` and requires `:engine` to have zero Minecraft, Forge,
Cleanroom, Mixin, or LWJGL dependencies (`docs/design/v3/DESIGN.md:468`–`:500`).

The historical dependency reviews below record authoring provenance only. Current Phase 3
exact-current schema (23 received by D-P11-25; newest §5.2 receipt governs) and Phase 6
retirement amendments are adopted in §5; all three documents closed verified per §G1.3 at
the attempt-11 wave (2026-09-08).

### 0.1 Inputs actually read

| Input | Portion read | Why |
|---|---|---|
| `AGENTS.md` | complete | repository and document-session rules |
| `docs/MOVES.md` | complete | resolve versioned paths and the six `DESIGN.md` collision |
| `docs/design/v3/DESIGN.md` | Part I, §G0–§G12; Phase 11 spec at lines 2279–2353 | governing global rules, template, and assignment; the `:630`–`:632` soft-7 precedent is the recorded basis for the handoff-only P7 lifecycle read disclosed in §0.2 |
| `docs/research/v1/RESEARCH.md` | §0, §1, §3.4 item 4, §6.3 expression row, Appendix D, Appendix F.6 | authority, inputs, cadence, built-in types/exclusions, expression contract |
| `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties` | custom-uniform section, lines 326–425 | shipped pack-author expression specification |
| `docs/phase3/v1/PHASE_3_DOC.md` | complete | declaration-capture dependency contract |
| `docs/phase3/reviews/PHASE_3_REVIEW_34.md` | complete | dependency verification state |
| `docs/phase6/v1/PHASE_6_DOC.md` | complete | built-in value model, cadence, upload sink, lifecycle |
| `docs/phase6/reviews/PHASE_6_REVIEW_22.md` | complete | dependency verification state |
| `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` | §14, lines 707–728 | required expression-architecture evidence and license warning |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/custom/CustomUniforms.java` | load, dependency ordering, update/push, optimization portions | verify PD's load-bearing architecture claims |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java` | custom update/push/optimization call sites | verify cadence and lifecycle claims |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/parsing/IrisFunctions.java` | function registry, casts, smooth, helpers, constructors | verify the claimed App F.6 checklist |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/parsing/SmoothFloat.java` | complete | understand the reference's timing shape without adopting it |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/parsing/IrisOptions.java` | complete | verify operator/precedence evidence without adopting extras |

### 0.2 Extra reads, deviations, and tool disposition

Three genuine gaps required narrow reads beyond the listed inputs; one further deviation from the assigned reading list is disclosed here: the current `docs/phase7/v1/PHASE_7_DOC.md` lifecycle publication was read solely for its published consumer handoff (the §1.2 basis), a legitimate non-dependency read per the Phase 12 soft-7 precedent (`docs/design/v3/DESIGN.md:630`–`:632`) — Phase 11 declares no Phase 7 dependency and asserts none.

1. `docs/research/v1/RESEARCH.md:1005`–`:1028` was read to quote the exact OQ-22 ledger text that
   the Phase 11 spec explicitly hands to Phase 14.
2. `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:594`–`:622` was read because the
   shipped `shaders.properties` says only “time-corrected” and does not publish the correction
   equation. The digest supplies behavior only: a per-id approach factor, distinct rise/fall
   times, and reset on pack/world/resize (`:597`–`:601`). No decompiled source was read.
3. Pintonium's root license material and an independent web lookup of the current Iris repository
   were inspected solely for the stareval license gate. The upstream stareval repository still
   did not resolve and no component-specific license or reliable chain of title was found. The
   current Iris repository's umbrella license does not independently establish the provenance of
   separately vendored stareval bytes. The outcome is therefore **unverifiable → clean-room**.

No source under a `docs/**/chatlogs/` directory, no repository-root `*.txt`, no
`glsl-transformer`, and no `glsl-transformation-lib` source was read. No MCP lookup was needed:
this phase has no vanilla-symbol question. No code, build, test, or verification command is part of
this build session.

Phase 11's v3 adoption is complete: `verification/targets/phase-11.json` derives its selectors from
v3, its dry-run preflight has succeeded, and `docs/MOVES.md:89`–`:92` records the adoption. The
per-round verification chronology is stated in exactly one place, the §0.4-onward round addenda,
whose newest entry always restates whether a fresh verification round is still required. It is:
**Verification status — 2026-09-08 (attempt-11 wave close-out):** the attempt-11 fresh whole-owner review (`docs/phase11/reviews/PHASE_11_REVIEW_18.md`, frozen SHA-256 `840850c8ac575f974d4d7c71f96dd0e679f1c9646eb25708265fe91177a714be`) returned PASS-WITH-CORRECTIONS — 0 blocking, 1 correction, 3 notes; this fix-up wave applied every correction and recorded resolutions in the review file, no §5 bytes changed and no §5 change is outstanding from this wave, and per §G1.3 the document is **verified**, while §0.13's standing fresh-verification requirement remains open exactly as that addendum states.

### 0.3 Legal and provenance posture

The authoritative contract is RESEARCH Appendix F.6 and the shipped pack-author documentation,
not an evaluator implementation. The shipped document says variables are reusable but not
uploaded and that updates occur on program change
(`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:326`–`:330`); it also enumerates the
operators and complete named function surface (`:372`–`:414`).

Pintonium is architecture evidence only. Its source verifies an AST/resolver, dependency graph,
topological evaluation, and dead-definition reachability shape
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/custom/CustomUniforms.java:46`–`:176`,
`:243`–`:275`). It is not a trustworthy function contract: for example, `round` is documented but
its registrations are commented out, and the reference itself calls out incomplete varargs
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/parsing/IrisFunctions.java:250`–`:283`).
That directly disproves PD §14's stronger wording that its function set is complete. This design
uses the generic graph/evaluator shape as corroboration, adopts none of those bytes, and derives
every pack-visible operation from Appendix F.6.

The stareval license gate is closed against reuse. v3 says the vendored component is historically
MIT-credited but must be clean-room implemented if independent verification fails
(`docs/design/v3/DESIGN.md:920`–`:922`). No stareval source, API, naming, parser structure, or test
vector may be copied into Schmaloogium. The clean-room implementation described here is based on
the published expression surface in `docs/research/v1/RESEARCH.md:1493`–`:1512` and
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:326`–`:425`.

### 0.4 Round-1 contract corrections

Round 1 aligned the Phase 3 and Phase 6 dependency descriptions with their verified §5 grants,
corrected bridge result accounting, and made §5 precisely incorporate and synchronize Phase 11's
detailed public declarations. The §5 changes require a fresh verification round before closure.

### 0.5 Round-2 contract corrections

Round 2 corrected stale authority/status prose and closed the consumer-visible compile, plan,
activation, context, random, and metrics contracts. Because §5 changed, another fresh verification
round is required before closure.

### 0.6 Round-3 contract corrections

Round 3 synchronized verification and dependency provenance, corrected `pi`'s documented binary32
value, and closed the public backend selector's interpreter-ID and unsupported-ID behavior. Its §5
change requires another fresh verification round before closure.

### 0.7 Round-4 contract corrections

Round 4 reconciled diagnostic identity, refresh ordering, reset-owned random state, and conformance
provenance. Its §5 lifecycle clarifications require another fresh verification round before closure.

### 0.8 Round-5 contract corrections

Round 5 synchronized current verification state and fixed the binding event-to-reset lifecycle map.
Its §5 change requires another fresh verification round before closure.

### 0.9 Round-6 contract corrections

Round 6 consolidated the verification chronology into the §0.4-onward addenda and repointed the
drifted line anchors for the consumed Phase 3 and Phase 6 grants (§5.2, §5.3, §4.8, §3.3) and the
RESEARCH Appendix F.6 surface, declaration-form, and function-list citations (§0.3, §3.1, §3.2).
Its §5 citation changes require another fresh verification round before closure.

### 0.10 Round-7 contract corrections

Round 7 repointed four external line anchors: the OQ-22 ledger row (§0.2, §10), the Phase 3
ownership rows (§1.2), the RESEARCH cadence sentence (§2.2), and the Phase 6 accepted-prefix commit
rule with its superseding invalid-counter branch (§4.8). No §5 text changed and no contract
substance changed.

### 0.11 Round-8 contract corrections

Round 8 made the verification-state pointers addendum-insertion-proof: §0.2 and §11.2 item 4 now
rely on the newest addendum restating the outstanding requirement rather than on it having made the
most recent §5 change. No §5 text changed and no contract substance changed. The outstanding
requirement stands: §0.9's §5 citation change still requires a fresh verification round before
closure.

### 0.12 Round-9 contract corrections

Round 9 declared the previously unpinned `DiagnosticSeverity` and `DiagnosticChannel` domains in
§4.9. No §5 text changed and no other contract substance changed. The outstanding requirement
stands: §0.9's §5 citation change still requires a fresh verification round before closure.

### 0.13 Integration contract fix-up — unverified

IR-03/10/15/17/20 reconcile current configuration intake, terminal retirement, direct source-free
GUI diagnostics, named evaluator conformance and OQ-22 measurement. Active §§4/5/11 change under
D-P11-14…17; prior addenda and Review 10 remain historical evidence. No implementation, test,
measurement, build or fresh PASS is claimed. Current producer and consumer reviews remain gates.

### 0.14 Frozen-attempt-7 correction — unverified

D-P11-27 resolves C14-1 by admitting both custom declaration kinds into one resolvable graph,
retaining uniform-only uploads. It explicitly supersedes the historical D-P11-4 inference using
the shipped author example at lines 421–422; Appendix F.6 never prohibited those references.
Active §§2/4/5/6/7/8/12 and the original conformance catalog change; fresh whole-owner and P2/P6
receiving review remains required. No validation command, implementation or runtime proof is claimed.
Historical Pintonium pin claims remain provenance, not verified current byte identity; Review 14
N14-1 records their unavailability. This correction relies on the directly read shipped author
document, not a replacement OSS checkout or unresolved stareval source.

### 0.15 Review-16 contract corrections — unverified (anchor coordinates historical as of 2026-09-08)

Review 16 (2026-09-08) corrections applied: the D-P11-20 receipt in §5.2 now marks P4's
`RegistryFingerprint/own-build-v1` as the receipt-time composition identity and names the current
`RegistryFingerprint/profile-selection-v3` domain (C-1, D-P11-31); the §§4.8/5.3/5.4 Phase 6
§4.13 citation ranges were recomputed against current Phase 6 bytes after its §0.28/§0.29
amendments (C-2). C-3 was rejected with evidence: current Phase 2 v2 bytes adopt
RUN-EXPRESSION-CONFORMANCE at D-P2-28, so §5.6's adoption claim stands. §5 text changed; a fresh
verification round remains required and §0.13's standing requirement is unchanged.

### 0.16 Review-17 correction fix-up — unverified (anchor coordinates historical as of 2026-09-08)

Review 17 (2026-09-08) applied three dependency-anchor coordinate repoints: the §1.2 Phase 3
declaration-capture citation now ends at `docs/phase3/v1/PHASE_3_DOC.md:1748` (C17-1) and the
§1.3 precipitation-ownership citation stands verified at `:1748` (C17-2); the §4.8 and §5.3
Phase 6 §4.13 sub-ranges were recomputed to the Review-17 targets `:1537`–`:1541` and
`:1544`–`:1562`, with the §4.8 install/retention range already at `:1590`–`:1597` (C17-3). No
§5 grant, interface, or semantic text changed; citation coordinates only. The standing
requirement is unchanged: §0.13's fresh verification round before closure remains open.

---

## 1. Scope & boundaries

### 1.1 Owned by Phase 11

Phase 11 owns these v0.4 components:

- the lexer, parser, source spans, typed AST, name resolver, and immutable executable plan;
- the exact scalar/boolean/vector expression type system and declaration-boundary coercions;
- every Appendix F.6 operator and function, including lazy `if` and stateful `smooth`;
- dependency analysis for the shared uniform/variable definition graph, cycle/error
  propagation, and once-per-refresh memoization;
- a pure interpreter backend plus the backend-neutral evaluator interface;
- binding Phase 6's fixed built-ins with Phase 11's biome, biome-constant, and view-entity inputs;
- every-program-switch evaluation after built-ins through Phase 6's one custom bridge;
- parse/type/runtime isolation, stable diagnostics, and chat-warning requests;
- smooth and random state, plan replacement/reset, performance counters, and headless tests.

This is the full Objective: “grammar, functions, input binding, evaluation cadence” in pure
`:engine` (`docs/design/v3/DESIGN.md:2286`–`:2288`).

### 1.2 Explicit adjacent ownership

- **Phase 3** reads `shaders.properties`, validates declaration keys/types/names, preserves raw
  expression text and source order, and never invokes this grammar
  (`docs/phase3/v1/PHASE_3_DOC.md:1776`–`:1779`). Phase 11 never reopens pack files or reparses
  Java Properties syntax.
- **Phase 6** owns fixed built-in acquisition, current typed values, the post-built-in custom
  callback, active-program location/type checks, GL uploads, upload replay, and GL-error isolation.
  Phase 11 never samples Phase 6 providers, resolves GL locations, or calls LWJGL.
- **`mod.glue`** implements the biome/view snapshot provider. Phase 11 defines only loader-neutral
  value objects and an SPI.
- **Phase 7** later wires lifecycle calls and the provider at the composition root. This document
  exposes that composition interface; this fix-up reads the current P7 lifecycle solely for
  its published consumer handoff, not a new declared dependency.
- **Phase 12** owns GUI presentation, including any profiles/options UI.
- **Phase 14** owns the OQ-22 measurement decision and any compiled evaluator implementation.
- **Phase 2** owns `RUN-EXPRESSION-CONFORMANCE` orchestration/adapter; Phase 11 owns original
  vectors and provider/effect expectations (§5.6). Local matrix capture remains source-free.

### 1.3 Hard boundaries

The following are prohibited within `engine.expr`:

- Minecraft, Forge, Cleanroom, Mixin, LWJGL, GL names/locations, registry objects, entities,
  worlds, resource handles, or pack file I/O;
- a second program-switch participant or any change to Phase 6's built-in-first/custom-third
  ordering;
- per-draw inputs, provider resampling, GLSL source transformation, or declaration discovery;
- profile evaluation, precipitation rendering, GUI state, or option persistence;
- stareval, glsl-transformer, decompiled OptiFine code, or behavioral “improvements” beyond the
  documented contract.

The Appendix F.6 precipitation sentence is explicitly a Phase 7 behavior handoff, not an
expression-engine feature; Phase 3 records the same ownership
(`docs/phase3/v1/PHASE_3_DOC.md:1779`).

---

## 2. Architecture overview

### 2.1 Placement and public shape

All implementation lives in `:engine`:

```text
engine.expr.api       published plans, values, provider and lifecycle interfaces
engine.expr.parse     clean-room lexer/parser and source spans
engine.expr.type      type checking, overload selection, constant folding
engine.expr.plan      dependency graph, reachability, plan diagnostics
engine.expr.eval      backend SPI, v0.4 interpreter, memo slots, runtime errors
engine.expr.state     smooth cells, refresh clock, deterministic random source
```

Package separation is descriptive; Phase 1's seam checker is authoritative. No implementation
class outside `engine.expr.api` is contract-visible.

```text
Phase 3 ordered declarations       Phase 6 input schema
             \                    /
              CustomExpressionCompiler
                        |
               PlanBuildResult
                  /           \
        invalid declarations   CustomExpressionPlan
                                      |
                     CustomExpressionController
                       /          |           \
            Phase 6 value view  biome/view   runtime state
                       \          |           /
                         evaluator backend
                                |
                    Phase 6 typed upload sink
```

### 2.2 Compile and refresh flow

At pack load, `CustomExpressionCompiler` performs one deterministic transaction:

1. accept the Phase 3 ordered declaration snapshot and its pack fingerprint;
2. bind the Phase 6 fixed-input schema plus the immutable `BIOME_*` catalog and view-boolean
   schema;
3. register the shared definition namespace (§4.1), then lex, parse, resolve, type-check and
   constant-fold declarations without changing symbol ownership;
4. build the uniform/variable definition graph, reject exact SCC cycles, and propagate invalidity;
5. retain definitions reachable from at least one valid upload-designated uniform;
6. assign stable definition, memo-slot, diagnostic, random-site, and smooth-site identities;
7. compile the typed graph through the selected evaluator backend;
8. return an immutable plan plus all load diagnostics. A plan may be useful even when some
   declarations are disabled.

At each successful program activation, Phase 6 calls the installed controller after built-ins.
The controller snapshots the non-Phase-6 context exactly once, opens one evaluation epoch, advances
the refresh clock at most once for the Minecraft frame, evaluates retained definitions once in
precomputed prerequisite-first order into converted memo slots, then submits successful uniform
values in original declaration order without reevaluation.
One expression failure never aborts unrelated definitions. This matches the required cadence:
customs refresh “on every program switch after built-ins”
(`docs/research/v1/RESEARCH.md:1380`–`:1383`).

### 2.3 Core data model

```java
enum ExpressionType { BOOL, INT, FLOAT, VEC2, VEC3, VEC4 }

sealed interface ExpressionValue {
    record Bool(boolean value) implements ExpressionValue {}
    record Int(int value) implements ExpressionValue {}
    record Float(float value) implements ExpressionValue {}
    record Vec2(float x, float y) implements ExpressionValue {}
    record Vec3(float x, float y, float z) implements ExpressionValue {}
    record Vec4(float x, float y, float z, float w) implements ExpressionValue {}
}

record SourceSpan(int declarationOrdinal, int startOffset, int endOffset) {}
record DefinitionId(long value) {}
record SmoothKey(long value) {}
record ExpressionPlanFingerprint(String value) {}
```

`INT` is a definition type for either kind, not a second arithmetic dialect. Numeric operators and
functions evaluate in finite `FLOAT`; fixed integer inputs and `INT` definitions promote to
float when read. An `INT` definition converts its final finite scalar toward zero after a range
check. This follows the shipped description of `biome`, `temperature`, `rainfall`, and fixed scalar
uniforms as float parameters (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:341`–`:351`),
while preserving the declared `int` storage/upload boundary.

Matrix values are input containers, not expression results. A matrix must be followed by two
literal indices; member access produces one `FLOAT`. Integer vectors likewise expose numeric
members as `FLOAT` because Appendix F.6 provides only `vec2/vec3/vec4`, not `ivec` constructors.

### 2.4 Invariants

1. A published plan is immutable, fingerprinted, and independent of render programs and GL state.
2. Every definition has one declared type, one typed root, and zero or more definition edges.
3. Uniforms and variables share memoization; only the independent `UNIFORM` designation submits.
4. Uniform submissions preserve Phase 3 declaration order, not dependency order.
5. The built-in snapshot, biome/view snapshot, refresh epoch, and random stream seen by one refresh
   cannot change mid-evaluation.
6. No non-finite scalar enters a plan, leaves an evaluator, or reaches Phase 6.
7. An expression-local failure changes only that definition and its reverse-reachable readers.
8. Smooth state is keyed by plan fingerprint plus `SmoothKey`, never by active program.
9. A repeated switch in one frame has zero elapsed time but is still a new evaluation epoch.
10. The v0.4 backend is replaceable without changing parsing, typed AST, state semantics,
    diagnostics, provider interfaces, or Phase 6 integration.

---

## 3. Contract conformance map

### 3.1 Declaration, token, and operator coverage

Appendix F.6 declares both forms and says variables are reusable but not uploaded
(`docs/research/v1/RESEARCH.md:1495`–`:1496`).
In the provenance cells below, Appendix F.6 establishes the named surface and documented shapes;
§§4.2 and 4.6 plus D-P11-13 establish Phase 11's additional exact grammar and operator semantics.

| Contract surface | Design disposition | Provenance | Primary tests |
|---|---|---|---|
| `uniform.<float\|int\|bool\|vec2\|vec3\|vec4>.<name>=<expr>` | consume Phase 3 declaration; evaluate and submit only successful uniforms | RESEARCH App F.6 `[V:doc]` | `allUniformDeclarationTypes` |
| `variable.<type>.<name>=<expr>` | typed memoized intermediate; never submitted | RESEARCH App F.6 `[V:doc]` | `variablesOncePerRefreshNeverUploaded` |
| numeric literals | decimal syntax in §4.2; finite float value | RESEARCH App F.6 `[V:doc]` | `numericLiteralGrammarAndRange` |
| `pi` | immutable float constant produced by converting documented decimal `3.1415926` to binary32, bits `0x40490fda` | `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:332`–`:336` `[V:doc]` | `piConstantBinary32` |
| `true`, `false` | boolean literals | RESEARCH App F.6 `[V:doc]` | `booleanLiterals` |
| grouping `(` `)`, argument comma | grammar only; no implicit tuple | shipped pack-author docs `[V:doc]` | `groupingAndCallArity` |
| member `.x .y .z .r .g .b` | checked vector/color alias access | RESEARCH App F.6 `[V:doc]` | `vectorMemberAliasesAndBounds` |
| matrix `name.<row>.<col>` | two literal indices, each 0–3; produces float | RESEARCH App F.6 `[V:doc]` | `matrixAllSixteenCellsAndBounds` |
| unary `+ -` | finite numeric scalar; `-` checked after evaluation | RESEARCH App F.6 surface `[V:doc]`; §§4.2, 4.6; D-P11-13 | `unaryNumeric` |
| unary `!` | bool only | RESEARCH App F.6 surface `[V:doc]`; §§4.2, 4.6; D-P11-13 | `logicalNotType` |
| `+ - * / %` | numeric scalars; `/` and `%` zero are runtime errors; §4.6 semantics | RESEARCH App F.6 surface `[V:doc]`; §4.6; D-P11-13 | `arithmeticTruthTable` |
| `> >= < <=` | numeric scalars, boolean result | RESEARCH App F.6 surface `[V:doc]`; §4.6; D-P11-13 | `orderedComparison` |
| `== !=` | same primitive type or numeric promotion; exact finite equality | RESEARCH App F.6 surface `[V:doc]`; §4.6; D-P11-13 | `equalityTypeMatrix` |
| `&& \|\|` | booleans, left-to-right short circuit | RESEARCH App F.6 surface `[V:doc]`; §4.6; D-P11-13 | `booleanShortCircuit` |

Precedence from tightest to loosest is member access/call, unary, multiplicative, additive,
relational, equality, `&&`, then `||`; binary operators associate left. This is conventional and
contains no Pintonium-only Unicode aliases.

### 3.2 Function coverage

The published function list is exact at `docs/research/v1/RESEARCH.md:1507`–`:1510`; no extra
function becomes pack-visible merely because a reference engine contains it.
Except for `smooth`'s separately cited behavioral evidence, Appendix F.6 establishes each function
name and documented signature shape; §4.6 and D-P11-13 establish the additional exact semantics in
the rows below.

| Function | Accepted signature and exact behavior | Surface provenance (exact semantics: §4.6; D-P11-13) | Primary test |
|---|---|---|---|
| `sin(x)` | radians; finite float → finite float | RESEARCH App F.6 `[V:doc]` | `trigGoldenAngles` |
| `cos(x)` | radians; finite float → finite float | RESEARCH App F.6 `[V:doc]` | `trigGoldenAngles` |
| `asin(x)` | domain `[-1,1]`; radians | RESEARCH App F.6 `[V:doc]` | `inverseTrigDomains` |
| `acos(x)` | domain `[-1,1]`; radians | RESEARCH App F.6 `[V:doc]` | `inverseTrigDomains` |
| `tan(x)` | radians; non-finite result is an error | RESEARCH App F.6 `[V:doc]` | `tanFiniteBoundary` |
| `atan(x)` | radians | RESEARCH App F.6 `[V:doc]` | `atanQuadrants` |
| `atan2(y,x)` | Java `atan2` quadrant and signed-zero behavior, finite result required | RESEARCH App F.6 `[V:doc]` | `atan2QuadrantsAndZero` |
| `torad(deg)` | `deg * pi / 180` in float evaluation | RESEARCH App F.6 `[V:doc]` | `angleConversions` |
| `todeg(rad)` | `rad * 180 / pi` in float evaluation | RESEARCH App F.6 `[V:doc]` | `angleConversions` |
| `min(x,y,...)` | two or more numeric args; visits every arg; numeric minimum | RESEARCH App F.6 `[V:doc]` | `minMaxVarargsAllPositions` |
| `max(x,y,...)` | two or more numeric args; visits every arg; numeric maximum | RESEARCH App F.6 `[V:doc]` | `minMaxVarargsAllPositions` |
| `clamp(x,min,max)` | numeric; error when `min > max`; otherwise `max(min,min(x,max))` | RESEARCH App F.6 `[V:doc]` | `clampBoundsAndInvalidRange` |
| `abs(x)` | numeric; finite result required | RESEARCH App F.6 `[V:doc]` | `absIncludingNegativeZero` |
| `floor(x)` | greatest integral float `<= x` | RESEARCH App F.6 `[V:doc]` | `roundingNegativeAndTies` |
| `ceil(x)` | least integral float `>= x` | RESEARCH App F.6 `[V:doc]` | `roundingNegativeAndTies` |
| `exp(x)` | natural exponential; overflow/non-finite is an error | RESEARCH App F.6 `[V:doc]` | `expFiniteAndOverflow` |
| `frac(x)` | `x - floor(x)`, therefore in `[0,1)` for finite x | RESEARCH App F.6 `[V:doc]` | `fracNegativeInputs` |
| `log(x)` | natural log; requires `x > 0` | RESEARCH App F.6 `[V:doc]` | `logDomain` |
| `pow(x,y)` | Java real-power semantics; non-real/non-finite result is an error | RESEARCH App F.6 `[V:doc]` | `powDomains` |
| `random()` | next float in `[0,1)` from the injected plan-local source | RESEARCH App F.6 `[V:doc]` | `randomSeedAndLazyConsumption` |
| `round(x)` | `floor(x + 0.5)` as a float, including negative ties | RESEARCH App F.6 `[V:doc]` | `roundingNegativeAndTies` |
| `signum(x)` | `-1`, `0`, or `1`; preserves neither NaN nor an invalid input | RESEARCH App F.6 `[V:doc]` | `signumAndSignedZero` |
| `sqrt(x)` | requires `x >= 0` | RESEARCH App F.6 `[V:doc]` | `sqrtDomain` |
| `fmod(x,y)` | floor modulus `x - floor(x/y)*y`; zero divisor is an error | RESEARCH App F.6 `[V:doc]` | `remainderVsFloorMod` |
| `if(c1,v1,...,else)` | odd arity at least 3; conditions bool; all values same/coercible result type; lazy first-true branch | RESEARCH App F.6 `[V:doc]` | `ifMultiBranchLazy` |
| `smooth([id,]val[,fadeIn[,fadeOut]])` | scalar float; 1–4-arg resolution and state machine in §4.7 | RESEARCH App F.6 `[V:doc]`; `[V:observed — reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:597–601]`; D-P11-7 | `smoothOverloadsAndTrace` |
| `between(x,min,max)` | inclusive numeric `x >= min && x <= max`; error if min > max | RESEARCH App F.6 `[V:doc]` | `betweenInclusive` |
| `equals(x,y,eps)` | numeric `abs(x-y) <= eps`; requires finite `eps >= 0` | RESEARCH App F.6 `[V:doc]` | `equalsEpsilon` |
| `in(x,v1,v2,...)` | at least one candidate; exact equality after numeric promotion, or same-type bool | RESEARCH App F.6 `[V:doc]` | `inVarargsAllPositions` |
| `vec2(x,y)` | two numeric scalars → float vector | RESEARCH App F.6 `[V:doc]` | `vectorConstructors` |
| `vec3(x,y,z)` | three numeric scalars → float vector | RESEARCH App F.6 `[V:doc]` | `vectorConstructors` |
| `vec4(x,y,z,w)` | four numeric scalars → float vector | RESEARCH App F.6 `[V:doc]` | `vectorConstructors` |

The Pintonium checklist is used only as a negative completeness audit. It documents `round` but
does not register it
(`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/parsing/IrisFunctions.java:39`–`:73`,
`:273`–`:277`), and its vararg implementation contains unresolved limitations (`:282`–`:300`).
Appendix F.6 wins, so all rows above require independent golden vectors.

### 3.3 Input coverage and exclusions

| Input family | Type exposed to expressions | Binding | Provenance |
|---|---|---|---|
| Phase 6 scalar float | `FLOAT` | exact-name typed lookup | RESEARCH App D/F.6 `[V:doc]`; Phase 6 §5 |
| Phase 6 scalar int | promotes to `FLOAT` when read | exact-name typed lookup | RESEARCH App D/F.6 `[V:doc]`; Phase 6 §5 |
| Phase 6 float/int vectors | container; member yields `FLOAT` | exact-name lookup plus member selector | RESEARCH App D/F.6 `[V:doc]`; Phase 6 §5 |
| Phase 6 `mat4` | container; two indices yield `FLOAT` | exact-name lookup plus matrix selector | RESEARCH App D/F.6 `[V:doc]`; Phase 6 §5 |
| `biome` | `FLOAT`, exact biome numeric id | context snapshot | RESEARCH App F.6 `[V:doc]` |
| `temperature`, `rainfall` | finite `FLOAT` | context snapshot | RESEARCH App F.6 `[V:doc]` |
| every `BIOME_*` | immutable `FLOAT` constant from integer id | context schema at plan build | RESEARCH App F.6 `[V:doc]` |
| fourteen `is_*` names | `BOOL` | context snapshot | RESEARCH App F.6 `[V:doc]` |
| per-draw exclusions | absent from the schema; never dynamically sampled or neutral-filled | Phase 6 verified union; RESEARCH App D/F.6 `[V:doc]`; D-P11-9 |
| program-change cadence | controller evaluates after Phase 6 built-ins on every successful activation | RESEARCH §3.4/App D/F.6 `[V:doc]`; Phase 6 §5; D-P11-5 |
| precipitation rule | no expression-engine behavior; explicit Phase 7 handoff | RESEARCH App F.6 `[V:doc]`; Phase 3 conformance handoff |

The fourteen booleans are exactly `is_alive`, `is_burning`, `is_child`, `is_glowing`, `is_hurt`,
`is_in_lava`, `is_in_water`, `is_invisible`, `is_on_ground`, `is_ridden`, `is_riding`,
`is_sneaking`, `is_sprinting`, and `is_wet`, matching
`docs/research/v1/RESEARCH.md:1498`–`:1501` and the shipped list
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:356`–`:370`.

No sampler is an expression input. No vector object is usable without a documented member, and no
matrix object is usable without both indices. Unknown, absent, or not-yet-valid Phase 6 values are
runtime errors for the one uniform that reaches them; Phase 11 never invents zero.

Appendix F.6 authoritatively excludes every D.4 per-draw dynamic—`entityColor`, `entityId`,
`blockEntityId`, `blendFunc`, and `instanceId`—plus `fogMode` and `fogColor`, and expressly says it
does not narrow D.4 (`docs/research/v1/RESEARCH.md:1501`–`:1505`). Phase 6's verified schema matches
that seven-name rule (`docs/phase6/v1/PHASE_6_DOC.md:1605`–`:1610`). The stale five-name restatement
in the Phase 11 design row (`docs/design/v3/DESIGN.md:2300`–`:2303`) is reported in §11.

### 3.4 Pintonium do-not-inherit disposition

| Evidence | Disposition | Provenance / decision |
|---|---|---|
| resolver-indirected typed AST | clean-room equivalent shape is suitable; no API or code reuse | PD §14; `[V:observed — reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/custom/CustomUniforms.java:46–76]`; D-P11-1/D-P11-2 |
| dependency ordering and cycle detection | generic graph algorithm independently implemented | PD §14; `[V:observed — reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/custom/CustomUniforms.java:78–176]`; D-P11-2 |
| dead-definition removal | retain reachability from every valid uniform; no active-program heuristic | PD §14; `[V:observed — reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/custom/CustomUniforms.java:224–275]`; D-P11-2/D-P11-5 |
| update/push split | Phase 6's verified bridge, not Pintonium's lifecycle, governs cadence | PD §14; `[V:observed — reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java:501–505]`; D-P11-5 |
| function registry | checklist only; Appendix F.6 table above is independently complete | PD §14; `[V:observed — reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/parsing/IrisFunctions.java:39–73,273–300]`; D-P11-1/D-P11-2 |
| stareval parser/runtime | prohibited from reuse because its license remains unverified | PD §14; DESIGN §G11.2 rule 3; D-P11-1 |
| extra operators/functions/vector forms | rejected unless Appendix F.6 names them | RESEARCH App F.6 `[V:doc]`; D-P11-1/D-P11-2 |
| program/pass location maps | not adopted; Phase 6 owns active-program knowledge and uploads | PD §14; Phase 6 §5; D-P11-2/D-P11-5 |

---

## 4. Detailed design

### 4.1 Declaration intake, identity, and plan build

The compiler accepts one immutable request:

```java
record CustomExpressionCompileRequest(
    String packConfigurationFingerprint,
    List<CustomExpressionSource> declarations,
    FixedExpressionInputSchema fixedInputs,
    ExpressionContextSchema context,
    String backendSemanticId) {}

record CustomExpressionSource(
    int sourceOrdinal,
    DeclarationKind kind,
    ExpressionType declaredType,
    String name,
    String rawExpression,
    SourceAttribution attribution) {}

public interface CustomExpressionCompiler {
    PlanBuildResult compile(CustomExpressionCompileRequest request);
    PlanBuildResult compilePhase3(String packConfigurationFingerprint,
        List<CustomExpressionDecl> declarations, FixedExpressionInputSchema fixedInputs,
        ExpressionContextSchema context, String backendSemanticId);
}

sealed interface PlanBuildResult {
    record Success(CustomExpressionPlan plan, List<ExpressionDiagnostic> diagnostics) implements PlanBuildResult {}
    record Partial(CustomExpressionPlan plan, List<ExpressionDiagnostic> diagnostics) implements PlanBuildResult {}
    record Failure(List<ExpressionDiagnostic> diagnostics) implements PlanBuildResult {}
}

public interface CustomExpressionPlan {
    String fingerprint();
    String fixedSchemaVersion();
    String contextSchemaVersion();
    String backendSemanticId();
    List<CompiledUniform> uniforms();
    List<ExpressionDiagnostic> diagnostics();
}

record CompiledUniform(int sourceOrdinal, ExpressionType type, String name) {}
```

`compilePhase3(...)` is the sole public adapter: it maps each ordered
`CustomExpressionDecl` field-for-field to `CustomExpressionSource`, converts the two closed enums
by equal-named variant, preserves duplicates and ordinals, and fails with a diagnostic if an
unknown future variant appears. Inputs and returned lists are non-null immutable copies; `Failure`
has a non-empty diagnostic list and no plan, while success may have none and partial has at least
one diagnostic. Plans expose metadata only, own their backend executable graph privately, and are
thread-safe immutable values.
Both entry points validate backend selection before declaration adaptation/parsing, even for an
empty list. Pre-plan failures use `ExpressionDiagnosticLocation.SourceLess`; neither entry point
requires request attribution or borrows an arbitrary declaration's coordinates (§4.9/§4.11).

`sourceOrdinal` is Phase 3 property order and is stable within the configuration fingerprint.
**D-P11-27:** uniform and variable declarations share one exact case-sensitive namespace.
Before resolving expressions, register the earliest source-ordinal occurrence of each name;
its declared type and `kind` are fixed independently of expression success. Every later occurrence,
including a different kind or type, is disabled with `DUPLICATE_NAME`; it cannot upload or become a
fallback if the owner fails parsing, typing, cycle checks or evaluation. This explicit first-owner
rule supersedes the former ambiguous first-valid duplicate policy and avoids circular winner selection.
Reject either kind colliding with fixed inputs, context names (`biome`, `temperature`, `rainfall`,
`BIOME_*`, fourteen booleans), excluded built-in/per-draw names, `pi`, `true`, `false`, or function
names; diagnose `DUPLICATE_NAME`, never shadow or expose an excluded input as a custom definition.
An invalid owner stays a resolvable failed definition so readers get `INVALID_DEPENDENCY`,
not `UNKNOWN_NAME`. Only names with no registered owner or permitted input produce `UNKNOWN_NAME`.
The private graph stores source identity, declared type, typed expression, distinct dependencies
and `CustomExpressionKind`; `UNIFORM` independently designates an upload root, `VARIABLE` never
does. No graph node or extra production API is exported.

Compilation is deterministic for request bytes, schemas, and backend ID. Diagnostics do not alter
the plan fingerprint. The plan fingerprint hashes the source fingerprint, exact schema versions,
ordered declaration metadata/text, biome constant entries, language version, evaluator semantic
version, and backend semantic ID. It never hashes object identity or host paths.

### 4.2 Grammar and precedence

The clean-room grammar is:

```ebnf
expression      = logicalOr ;
logicalOr       = logicalAnd, { "||", logicalAnd } ;
logicalAnd      = equality, { "&&", equality } ;
equality        = relation, { ("==" | "!="), relation } ;
relation        = additive, { (">" | ">=" | "<" | "<="), additive } ;
additive        = multiply, { ("+" | "-"), multiply } ;
multiply        = unary, { ("*" | "/" | "%"), unary } ;
unary           = ["+" | "-" | "!"], unary | postfix ;
postfix         = primary, { member } ;
member          = ".", ("x" | "y" | "z" | "r" | "g" | "b" | index) ;
primary         = number | identifier | call | "(", expression, ")" ;
call            = identifier, "(", [expression, {",", expression}], ")" ;
index           = "0" | "1" | "2" | "3" ;
number          = digits, [".", digits], [exponent]
                | ".", digits, [exponent] ;
exponent        = ("e" | "E"), ["+" | "-"], digits ;
identifier      = letterOrUnderscore, {letterOrUnderscore | digit} ;
```

Whitespace is ignored between tokens. The entire raw expression must be consumed. Numeric signs
are unary operators, not literal characters. Hex, binary, suffixes, NaN/Infinity spellings,
Unicode operators, indexing brackets, swizzles longer than one member, implicit multiplication,
and assignment are rejected. Parser limits are 16 KiB UTF-8-equivalent text, 4,096 tokens, 128
nesting levels, 256 call arguments, and 2,048 AST nodes per declaration; hitting a limit is that
declaration's load error, not a client crash.

Matrix access is recognized only when the base symbol's schema type is `MAT4` and exactly two
numeric member tokens follow. `.r/.g/.b` alias `.x/.y/.z`. `.w/.a` are intentionally absent because
Appendix F.6 names only the three documented vector/color members; vec4 values can be constructed
and uploaded but the contract does not authorize reading their fourth member.

### 4.3 Static typing and coercion

The checker has these rules:

- `BOOL` never converts to or from a numeric type.
- Expression arithmetic consumes `FLOAT`. A fixed `Int1`, vector integer member, or `INT`
  definition (uniform or variable) promotes to float on read.
- `INT` declaration boundary accepts a finite numeric scalar, rejects values outside
  `[-2147483648, 2147483647]`, and truncates toward zero.
- `FLOAT` accepts a finite numeric scalar.
- `VEC2`, `VEC3`, and `VEC4` require exactly the matching constructor/result width. There is no
  scalar splat, widening, truncation, or vector arithmetic.
- `BOOL` requires a boolean result.
- Every vector component must be finite.
- `if` value branches may mix `INT` definitions/fixed inputs and numeric expressions because both
  are read as float; other branch types must be identical.
- `==`, `!=`, and `in` compare bool with bool or numeric with numeric. Vectors are not comparable.

Declaration-boundary conversion happens exactly once for either definition kind, before its memo
slot becomes `VALUE`. Every reference reads that converted value; a uniform's later command uses
the same stored value without reevaluation or a second conversion. Thus `uniform.int.a=2.75`
stores/uploads integer 2 and `a+0.5` reads promoted 2, yielding 2.5, not 3.25.
Static mismatches disable the declaration at load; dynamic range/non-finite failures use §4.9.
Both propagate only through reverse-reader edges.

### 4.4 Name resolution and input schema

Resolution order is fixed and independent of declaration order:

1. `pi`, `true`, `false`;
2. exact `BIOME_*` constants;
3. exact `biome`, `temperature`, `rainfall` and fourteen view booleans;
4. exact permitted Phase 6 built-in names;
5. exact registered custom-definition names, of either kind.

Uniforms and variables may reference either kind, forward or backward, including their own name
(a self-cycle, not an unknown identifier). Resolution uses declared types before expression
validation; invalid registered owners retain their identity for dependency diagnostics. Collision
rejection in §4.1 makes this order unambiguous. The published `screenDark3` reads `screenDark`
as a custom definition, not a Phase 6 built-in or a GL value (shipped author example, lines 421–422).

`FixedExpressionInputSchema` is a closed exact-name/type view supplied alongside Phase 6's runtime
value view. It distinguishes scalar, vector width/component kind, and mat4. Compilation rejects an
unknown identifier early. At refresh, every referenced input is looked up once per epoch into a
slot. A schema/value disagreement is an invariant diagnostic and fails only uniforms reaching that
slot.

The permitted fixed catalog is Appendix D.1–D.3 minus `fogMode` and `fogColor`; samplers and every
D.4 value are absent. This includes `frameCounter` and `frameTime`, which drive the refresh clock,
and all documented matrix cells. The schema is versioned and tested against Phase 6's inventory so
the two cannot drift silently.

`ExpressionContextSchema` carries an immutable sorted map of biome constant names to integer IDs
and the fixed fourteen-name boolean set. Invalid biome constant names, duplicate names with
different IDs, or non-finite context ranges reject the context schema before plan publication.
Numeric biome IDs are not assumed contiguous.

### 4.5 Dependency graph, reachability, and memoization

The graph has one vertex per registered definition of either kind and one distinct directed
edge `A -> B` when A reads B. Upload designation is independent of graph membership.
**D-P11-26, extended by D-P11-27:** this is reader-to-dependency orientation; ordinary incoming-edge
Kahn ordering would incorrectly visit readers first. Resolve all syntactic references before
constant folding, including lazy branches, so folding cannot hide unknown names, cycles or errors.

At load time compute strongly connected components with bounded explicit work stacks. Exactly
a component with more than one vertex, or a singleton with a self-edge, is cyclic. Only those
members receive `CYCLE`. Propagate cycle and other declaration invalidity through reverse
dependency-to-reader edges; otherwise-valid reached definitions receive `INVALID_DEPENDENCY`.
Preserve a declaration's own primary error rather than replacing it with a dependent error.
An acyclic prerequisite read by a cycle is not a cycle member or an invalid dependent and remains
available to independent roots. A traversal residual is never cycle-member evidence. Visit
components, members and alternative diagnostic paths deterministically by source ordinal.

After diagnosing every source declaration, follow reader-to-dependency edges from every valid
`UNIFORM` root; discard unreachable variables from execution, not diagnostics. Every valid uniform
is itself a root whether or not referenced by another definition or present in the active program.
For each retained vertex count distinct retained dependencies. Put zero-count vertices in a
source-ordinal priority queue; emit the earliest, decrement each reverse-edge reader, and enqueue
newly ready readers. Prerequisites always precede readers; source order only breaks ready-set ties.
All retained vertices must emit; a residual after invalidity removal is a corrupt-plan invariant.

Pre-sized per-definition memo slots have `UNVISITED`, `EVALUATING`, `VALUE`, or `ERROR` and epoch
generations; no graph objects allocate during refresh. Visit retained enabled definitions once in
the precomputed order. A previously failed definition or reverse-dependent reader stays disabled
until reset/new activation; readers of an `ERROR` do not execute their AST. Every successful
definition converts once, commits its smooth overlay once and stores `VALUE`. Repeated references
and later uniform submission only read the slot, never repeat random/smooth effects. Independent
prerequisites and roots still execute. After evaluation, submit only successful `UNIFORM` slots in
original declaration order. Sink absence/rejection cannot invalidate a stored value or its readers.

Definition scheduling remains eager, as in the prior variable graph: a statically reachable
definition executes even when its reference occurs only in another definition's unselected branch.
Within each definition, §4.6 lazy AST branches still skip their own nodes/effects/errors; static
invalidity is never branch-dependent. Random order is prerequisite-first with ready-source ties,
then left-to-right within each definition; no rewind on failure. A successfully committed
prerequisite's smooth state is not undone by a later reader or sink failure.

### 4.6 Operator and stateless function semantics

All scalar computation uses strict binary32 rounding at each AST operation, even on Java 25. The
interpreter explicitly narrows each result to `float`; a future backend must match the same golden
bits. Inputs and results are checked with `Float.isFinite`.

- `+`, `-`, and `*` are ordinary binary32 operations.
- `/` rejects either `+0.0` or `-0.0` divisor before evaluation.
- `%` is truncating remainder `x - trunc(x/y)*y`; it rejects zero divisor.
- `fmod` is the documented floor modulus `x - floor(x/y)*y`; it rejects zero divisor. This makes
  the named function observably distinct from `%` for negative operands.
- Boolean `&&`, `||`, `if`, and `in` evaluate left-to-right. `&&`, `||`, and `if` are lazy;
  unselected expressions do not consume random values, mutate smooth cells, or raise errors.
- Exact `==` is bit-value equality after numeric promotion except that `+0.0 == -0.0` is true.
  Non-finite values never reach comparison.
- `min`/`max` visit every argument exactly once. This requirement specifically catches the
  incomplete reference implementation rather than inheriting it.
- Domain and finite checks in §3.2 are part of semantics, not optional validation.

`RandomSource` exposes `nextFloat()` only. Production glue creates one stream per active plan from
a runtime seed; headless tests inject a fixed sequence. The stream survives program switches and
is reset on plan replacement/world reset/shaders-off. The plan fingerprint is not used as the sole
seed, so a pack does not replay an identical “random” animation every launch. A backend must consume
exactly one sample for each actually evaluated `random()` node. Consumption is not rolled back if a
later node in the same definition errors; this left-to-right rule is deterministic and avoids an
unpublished rewind requirement on the injected source.

### 4.7 `smooth` overload resolution and state machine

The shipped contract requires a unique optional id, default one-second fade, and optional distinct
fade times (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:401`–`:405`). The accepted
forms are:

| Arity | Resolution |
|---|---|
| 1 | `smooth(value)` |
| 2 | `smooth(id,value)` when arg 1 is a constant integral id; otherwise `smooth(value,fadeIn)` |
| 3 | `smooth(id,value,fadeIn)` when arg 1 is a constant integral id; otherwise `smooth(value,fadeIn,fadeOut)` |
| 4 | `smooth(id,value,fadeIn,fadeOut)`; id required |

An explicit id must constant-fold to a signed 32-bit integer without reading an input, variable,
`random`, or `smooth`. It is unique across the entire plan. The first source-ordered occurrence
owns a duplicate id; each later owning declaration is load-invalid. An omitted id becomes a stable
`SmoothKey` derived from the plan semantic version, declaration ordinal, and AST preorder site—not
from a runtime object hash.

Each cell is:

```java
record SmoothCell(boolean initialized, float value, double lastCommittedEvaluationSeconds) {}
```

State is plan-wide and survives every program switch. It is not keyed by `ResolvedProgramDescriptor`.
The state transition for target `t`, rise time `fadeIn`, fall time `fadeOut`, and cell elapsed time
`dt = controllerSeconds - lastCommittedEvaluationSeconds` is exact:

1. Reject non-finite target or fade. Reject negative fade. A missing fade is `1.0`; a missing
   `fadeOut` equals `fadeIn`.
2. If uninitialized, stage `t` and the current controller time, and return `t` without a startup ramp.
3. Choose `fadeIn` when `t > value`; otherwise choose `fadeOut`.
4. If `dt <= 0`, return the stored value unchanged. If selected fade is zero or `dt >= fade`, snap
   to `t`.
5. Otherwise compute in binary32:
   `updates = fade / dt`;
   `correction = 4.61 - 1 / (0.13 + updates / 10)`;
   `k = clamp(dt / fade * correction, 0, 1)`;
   `value = value + (t - value) * k`.
6. Reject a non-finite intermediate/result; the owning uniform takes the runtime-error path and the
   cell does not commit a partial transition. On success stage both the resulting value and current
   controller time, including unchanged/snap cases; commit only with the owning definition.

This is a behavior-only restatement of the allowed digest, which records the same correction and
separate up/down times (`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:597`–`:601`).
It is not derived from decompiled class or method structure.

The controller maintains finite binary64 `controllerSeconds`, initially zero, and a last-observed
frame counter. After validating same-activation Phase 6 `frameCounter`/finite nonnegative
`frameTime`, the first refresh in an activation establishes the counter at time zero; each later
different counter adds that frameTime exactly once. Same-counter switches leave the clock unchanged.
Inequality handles counter wrap; counter reset requires lifecycle reset. Invalid time or clock
overflow is a provider protocol failure before clock mutation. This clock observation commits
before context sampling and is not undone by a later provider/definition failure, so retrying the
same frame cannot add time twice.
Each reached initialized cell subtracts its own last successful committed evaluation time. A
skipped lazy site does not write its timestamp and therefore retains all elapsed time until reached.
Elapsed comparison with fade occurs before conversion to binary32; when `0 < dt < fade`, convert
dt to binary32 for the published equation. Underflow to zero leaves value unchanged. A successful
second evaluation in the same frame sees zero; a first late evaluation sees its full accrued time.
No lazy branch is evaluated eagerly and switches still reevaluate targets/variables.

All smooth-cell value and timestamp writes for one definition are transactional: evaluate against
an overlay, then commit both only if that variable or uniform produces a valid final value.
If two definitions attempt the same explicit smooth id, uniqueness prevents order-dependent sharing.
A definition's successful update commits once when its topological evaluation fills its memo slot;
a later reader or upload error does not undo that already valid prerequisite.

Smooth state resets on pack replacement, shaders-off, world epoch, framebuffer resize, GL-context
loss, or close. It does not reset on program fallback, stage change, repeated activation, or an
ordinary Phase 6 redundant-upload skip.

### 4.8 Program-switch cadence and Phase 6 bridge

One `CustomExpressionController` is installed through Phase 6 before first use and retained for the
runtime lifetime, as Phase 6 requires (`docs/phase6/v1/PHASE_6_DOC.md:1592`–`:1599`). It holds an
atomic current-plan slot changed only by composition lifecycle calls.

On `refresh(program, values, uploads)`:

1. return `NoCustoms` when no plan is active or no uniform is valid;
2. verify plan/configuration/schema generations;
3. resolve `frameCounter`/`frameTime` from `values` and open the controller's next refresh/memo
   epoch;
4. issue one `ExpressionContextRequest` carrying that epoch and `frameCounter`; accept its
   synchronous result only while that controller-issued epoch remains current; the controller clock
   was advanced once in step 3, while each reached cell computes its own elapsed time;
5. evaluate retained enabled definitions of both kinds once in §4.5 order, converting into memo slots;
6. visit successful `UNIFORM` slots in original declaration order and submit their stored typed values;
7. on expression failure disable the failing definition and reverse-dependent readers until reset/new
   activation, warn once per affected uniform, omit their commands, and continue independent roots;
8. on sink `Accepted`, `SkippedAbsent`, or `Rejected`, increment the matching authoritative
   `accepted`, `skippedAbsent`, or `rejected` counter and continue;
9. return `Completed(accepted,skippedAbsent,rejected)` with all three counters equal to the sink
   ledger.

Expression-local errors never produce `Aborted`. `Aborted` is reserved for a corrupt plan,
generation mismatch, provider protocol failure, or backend invariant that makes the remainder
unsafe. Phase 6 commits any already accepted prefix exactly as its contract states, subject to the
invalid-counter branch that supersedes it (`docs/phase6/v1/PHASE_6_DOC.md:1663`–`:1672`).
Every `Aborted(diagnosticId,accepted,skippedAbsent,rejected)` reports those same three counters for
the submitted prefix before the structural failure; Phase 11 never estimates or resets the ledger.

Every valid custom uniform is offered on every activation. Phase 6 decides whether its exact name
and type exist in the active linked program; program absence must be a non-error no-op. No plan is
compiled per program, and no active layout leaks into `engine.expr`.

Upload mapping is:

| Declared type | Required Phase 6 command |
|---|---|
| `float` | `Float1` |
| `int` | `Int1` |
| `bool` | `Bool1` (Phase 6 performs GL integer encoding) |
| `vec2` | `Float2` |
| `vec3` | `Float3` |
| `vec4` | `Float4` |

Phase 11 never uses `Int1` as an undocumented bool convention.

### 4.9 Diagnostics and error isolation

```java
enum ExpressionDiagnosticKind {
    LEX, PARSE, LIMIT, UNKNOWN_NAME, DUPLICATE_NAME, DUPLICATE_SMOOTH_ID,
    TYPE, ARITY, CYCLE, INVALID_DEPENDENCY, INPUT_ABSENT, INPUT_SCHEMA_MISMATCH,
    DIVIDE_BY_ZERO, DOMAIN, NON_FINITE, INT_RANGE, PROVIDER, UNSUPPORTED_BACKEND, BACKEND_INVARIANT
}

enum DiagnosticSeverity { WARNING, ERROR }

enum DiagnosticChannel { CHAT_AND_LOG, LOG_ONLY }

sealed interface ExpressionDiagnosticLocation {
    record Declaration(String declarationName, SourceAttribution attribution, SourceSpan span)
        implements ExpressionDiagnosticLocation {}
    record SourceLess() implements ExpressionDiagnosticLocation {}
}
record ExpressionDiagnostic(
    String stableId,
    ExpressionDiagnosticKind kind,
    DiagnosticSeverity severity,
    DiagnosticChannel channel,
    ExpressionDiagnosticLocation location,
    String summary) {}
public interface ExpressionDiagnosticSink {
    void report(ExpressionDiagnostic diagnostic);
}
```

Both domains are Phase 11-owned and closed. `ERROR` marks a diagnostic that disabled a uniform or
variable; `WARNING` marks one that did not. `CHAT_AND_LOG` marks a diagnostic whose sanitized
summary is chat-visible and also logged; `LOG_ONLY` marks detail, such as a dependency path, that
never reaches chat.

Diagnostics created with plan/declaration context derive stable IDs from diagnostic kind, plan
fingerprint, declaration ordinal, and source span; message prose is not hashed. The pre-plan
`UNSUPPORTED_BACKEND` diagnostic instead uses the pack fingerprint and requested backend ID exactly
as §4.11 specifies. Raw expressions and paths are sanitized before chat. Load diagnostics are
aggregated so one bad line does not hide later errors.
All record fields are non-null immutable values. `Declaration` preserves the real declaration
name, original attribution and actual span; source-less pre-plan/controller failures use `SourceLess`,
never invented paths, zero coordinates, or an arbitrary first declaration. Declaration stable-ID
inputs remain unchanged. Structural active-plan failures without a declaration use kind, active
plan fingerprint and stable reason code; rejected activation uses candidate fingerprint and reason.
The provider's supplied diagnosticId remains authoritative for an Unavailable result and its emitted
record. IDs never hash prose or fabricated locations.

The factory requires a real `ExpressionDiagnosticSink` after its metrics sink. It receives each
new runtime/activation diagnostic synchronously on the serialized controller caller thread before
that operation returns, preserving the exact immutable typed record and stableId. Ordinary errors
still return `Completed` with the actual upload counters; structural failures emit their typed
record and return `Aborted` with that record's ID and actual prefix counters; rejected activation
emits before `Rejected` with the same ID. `NoCustoms` emits nothing. Coalescing is once per stableId
per activation, cleared on reset; no global reporter or implicit constructor injection exists.
Compile diagnostics are returned in PlanBuildResult only (also accessible from a produced plan),
not emitted by the controller; composition forwards that returned list once through its same sink.

Normal `report` return acknowledges synchronous receipt, not chat display or persistence. A sink
RuntimeException is caught without recursion, retry, new expression diagnostic, disabling evaluation,
changing upload counts/results, or rolling back already committed expression state. That delivery
is lost; subsequent distinct diagnostics still attempt delivery. The controller marks the attempted
ID coalesced even on failure, preventing repeated-switch spam. The owner of the sink is responsible
for recording/reporting its own delivery failures through its independent host logging route;
P2's collector marks a failed collection as a failed run, never a successful empty observation.
The controller borrows the sink for its lifetime, never closes it, retains it across nonterminal
resets, and releases it on terminal close; no callbacks occur after close. The sink may retain
immutable records but may not reenter controller lifecycle/evaluation.

- A declaration lex/parse/type/name/arity/cycle error disables that owner and reverse-dependent
  readers at load, regardless of kind. Chat reports each affected uniform once; primary error and
  exact dependency paths remain in detailed diagnostics, with dependency paths log-only.
- A runtime domain, zero-divisor, absent-input, non-finite, int-range, or evaluator error disables
  that definition and all reverse-dependent readers for the active plan. The owner keeps the
  primary error; affected readers receive `INVALID_DEPENDENCY`. Chat warns once per affected
  uniform; independent definitions continue, including prerequisites also read by the failed owner.
- A partial smooth change made while evaluating a failed definition is rolled back. Already
  consumed `random()` samples remain consumed under §4.6's deterministic order.
- A sink rejection is Phase 6's error and is not relabeled as an expression failure.
- Provider/backend invariant failures return `Aborted`, rate-limit one user-visible message per
  plan/reason, and leave shaders operational with custom uniforms absent.

This is degradation-ladder rung 1: “A custom uniform that errors at runtime disables that uniform
only” (`docs/design/v3/DESIGN.md:419`–`:425`). No expression exception crosses the bridge.

#### 4.9.1 Direct immutable GUI projection

```java
enum ExpressionDiagnosticAttemptOutcome { ACCEPTED, REJECTED }
record ExpressionDiagnosticGuiEntry(
    String stableId, ExpressionDiagnosticKind kind, DiagnosticSeverity severity,
    String declarationName, String summary) {}
record ExpressionDiagnosticGuiSnapshot(
    String packFingerprint, String configurationFingerprint, long attemptSerial,
    ExpressionDiagnosticAttemptOutcome outcome,
    List<ExpressionDiagnosticGuiEntry> entries) {}
interface ExpressionDiagnosticGuiProjector {
    ExpressionDiagnosticGuiSnapshot project(
        String packFingerprint, String configurationFingerprint, long attemptSerial,
        ExpressionDiagnosticAttemptOutcome outcome, List<ExpressionDiagnostic> diagnostics);
}
```

P11 owns this pure projection and vocabulary. Inputs are non-null, fingerprints non-empty opaque
identities, serial positive, and entries immutable defensive copies in declaration/diagnostic
order, deduplicated by stableId. Invalid caller arguments throw `IllegalArgumentException` before
publication; no partial view results. Output contains no raw expression, source path/span,
attribution, dependency chain, pack resource/handle or exception text. Declaration name is taken
from `location.Declaration` and validated as an identifier, or empty for `location.SourceLess`;
summary is generated from a fixed kind/severity template, never copied from diagnostic summary
or arbitrary pack text. Stable identities/fingerprints are opaque equality keys, not text to decode into source.

P7 owns publication at the final composition outcome, not compiler completion: ACCEPTED means
the final pipeline/configuration was admitted (possibly with expression errors/NoCustoms);
REJECTED means the attempt was not admitted, including later compensated-off failure. A
`PlanBuildResult.Success` or `PlanActivationResult.Activated` alone cannot choose ACCEPTED.
Both existing P11 channels are eligible with identical WARNING→WARNING / ERROR→ERROR mapping;
LOG_ONLY detail is replaced by the safe template, not leaked. This direct typed input does not
route through P1 SHADER_GUI or add a third/fourth P11 channel.

P7's positive attempt serial orders this selected-pack session, independent of Phase 4 generations.
P12 consumes the P7-mediated immutable optional snapshot and rejects an older serial or different
selected pack. P7 clears the slot before selection change, explicit off and shutdown; no expression
compile means no new expression snapshot. A same-pack failed attempt may remain displayed as
REJECTED until next attempt/clear, never as the active plan's diagnostics. Configuration fingerprint
is that of the attempted current P3 configuration. Replaced snapshots have no retained resources;
UI copies may survive for display only and confer no publication or controller authority.

### 4.10 Biome and view-entity provider seam

```java
public interface ExpressionContextProvider {
    ExpressionContextResult snapshot(ExpressionContextRequest request);
}

record ExpressionContextRequest(
    String planFingerprint,
    long refreshEpoch,
    int frameCounter) {}

sealed interface ExpressionContextResult {
    record Available(ExpressionContextSnapshot snapshot) implements ExpressionContextResult {}
    record Unavailable(String diagnosticId) implements ExpressionContextResult {}
}

record ExpressionContextSnapshot(
    int biomeId,
    float temperature,
    float rainfall,
    ViewEntityFlags viewEntityFlags) {}

record ViewEntityFlags(
    boolean isAlive, boolean isBurning, boolean isChild, boolean isGlowing,
    boolean isHurt, boolean isInLava, boolean isInWater, boolean isInvisible,
    boolean isOnGround, boolean isRidden, boolean isRiding, boolean isSneaking,
    boolean isSprinting, boolean isWet) {}

record ExpressionContextSchema(String version, Map<String, Integer> biomeConstants) {}

public interface RandomSource { float nextFloat(); }
```

Schema fields are non-null immutable copies; `version` is non-empty, biome names are unique
`BIOME_*` identifiers, and IDs are exactly representable as float. `nextFloat()` must return a
finite value in `[0,1)`; violation is a provider protocol failure and aborts only that refresh.

The provider is invoked once per refresh, on the render thread, and returns values by copy. It does
not expose an entity, world, biome object, registry, or nullable value. Temperature/rainfall must be
finite; documented normal range is 0–1, but values outside that range are preserved with a bounded
warning because modded biome behavior can exceed vanilla assumptions. `biomeId` converts to exact
float for the expression.

`BIOME_*` constants are supplied in `ExpressionContextSchema` at plan compilation, not recomputed
on every switch. `mod.glue` derives names deterministically from the authoritative registry snapshot
for the world/pack epoch. A world-epoch change compiles or activates a schema-compatible new plan
before evaluation; it never mutates an existing plan's constants.

Headless tests use scripted providers with exact snapshot sequences, including `Unavailable`,
non-finite weather values, all boolean combinations, and biome-catalog replacement.

### 4.11 Evaluator backend and v0.4 decision

```java
interface EvaluatorBackend {
    String semanticId();
    BackendBuildResult build(TypedExpressionGraph graph);
}

interface ExecutableExpressionGraph {
    EvaluationResult evaluate(DefinitionId root, EvaluationFrame frame, MemoTable memo);
}
```

`EvaluatorBackend`, `ExecutableExpressionGraph`, the typed graph, value/frame/memo/state/error
algebras, and their build results are implementation-private Phase 11 SPI; consumers select a
backend only by the request's stable backend semantic ID. Backends may not add functions, change
rounding, reorder lazy effects, cache across refreshes, or expose implementation exceptions.

**v0.4 selects a typed-AST interpreter.** It minimizes license and verifier risk, keeps source spans
for diagnostics, and follows the global “clean code first, optimize with evidence” direction
(`docs/design/v3/DESIGN.md:451`–`:456`). The research row names MethodHandle/bytecode compilation
only as unverified opportunity (`docs/research/v1/RESEARCH.md:781`–`:787`). No bytecode library or
JIT-specific plan is a Phase 11 dependency.

The canonical v0.4 interpreter semantic ID is `schmaloogium:typed-ast-interpreter-v1`. Both compiler
entry points accept exactly that ID in v0.4. Any other ID deterministically returns
`PlanBuildResult.Failure` with one `UNSUPPORTED_BACKEND` error diagnostic whose stable ID derives
from the diagnostic kind, pack fingerprint, and requested backend ID; its location is
`ExpressionDiagnosticLocation.SourceLess()`, severity ERROR and channel CHAT_AND_LOG.
No declarations are adapted/parsed and no plan is produced, including for an empty declaration list.

The interpreter has dense node arrays, pre-resolved symbol/function ordinals, primitive memo
storage, and no steady-state allocation after plan activation. Every refresh records aggregate
node evaluations, uniforms attempted, variables memoized, and elapsed nanoseconds through an
optional no-op-by-default metrics sink.

```java
public interface ExpressionMetricsSink { void record(ExpressionMetrics metrics); }
record ExpressionMetrics(String planFingerprint, long refreshCount, long nodeEvaluations,
    long variableMemoHits, long variableMemoMisses, long uniformSuccesses,
    long uniformErrors, long uniformSkips, long elapsedNanos, String profilerCorrelationId) {}
```

The controller accepts a non-null sink at construction; the no-op sink is the default. Calls are
render-thread synchronous, receive non-null immutable aggregates, and sink failure is caught,
reported once, and disables metrics without affecting evaluation.

The implementation performance budget is measured, not asserted:

- representative workload: every custom expression from each locally downloaded matrix pack,
  plus a synthetic 128-uniform/256-variable graph at 40 program switches per frame;
- environment: release JVM, warmed 30 seconds, fixed scripted input sequence, interpreter semantic
  checks enabled, allocation profiler sampled separately;
- budget: p95 total expression time at or below 0.25 ms per rendered frame and zero bytes allocated
  per steady refresh; p99 must stay below 0.50 ms;
- if all real packs meet budget, retain interpreter for v0.4;
- if any representative real pack misses after profile-guided interpreter cleanup, hand evidence to
  Phase 14; do not silently introduce a compiler in Phase 11.

### 4.12 Lifecycle and reset

```java
public interface CustomExpressionController extends CustomUniformBridge, AutoCloseable {
    PlanActivationResult activate(CustomExpressionPlan plan,
                                  ExpressionContextProvider contexts,
                                  RandomSource random);
    void reset(ExpressionResetReason reason);
}

public interface CustomExpressionControllerFactory {
    CustomExpressionController create(ExpressionMetricsSink metricsSink,
                                      ExpressionDiagnosticSink diagnosticSink);
}

sealed interface PlanActivationResult {
    record Activated(String planFingerprint) implements PlanActivationResult {}
    record Rejected(String diagnosticId) implements PlanActivationResult {}
}

enum ExpressionResetReason {
    PACK_REPLACEMENT, SHADERS_OFF, WORLD_EPOCH, FRAMEBUFFER_RESIZE,
    GL_CONTEXT_LOSS, CLOSE
}
```

The factory and controller arguments are non-null; `create` transfers sole controller lifecycle to
the caller and performs no evaluation. The composition thread installs exactly one controller
before first use. `activate` arguments are non-null;
`activate` validates schema/backend fingerprints and either atomically installs the complete tuple
and returns `Activated`, or returns `Rejected` without changing the prior tuple before the next
refresh.
The render thread owns refresh and mutable evaluation state. A plan may be compiled off-thread,
consistent with v3's explicit permission for expression compilation
(`docs/design/v3/DESIGN.md:410`–`:415`).

Every reset atomically deactivates the complete plan/provider/random tuple and invalidates memo
generations, runtime-disabled-definition flags, clock tracking, and smooth cells; dropping the sole
tuple reference discards the old random stream. Until a later successful `activate` supplies a new
`RandomSource`, `refresh` returns `NoCustoms`. `PACK_REPLACEMENT` and every nonterminal reason permit
later activation; `CLOSE` is terminal. Repeated same-reason reset is idempotent. Reset never calls
Phase 6 or GL and retains neither provider nor random source after deactivation.

Composition forwards lifecycle events at these binding boundaries:

- Retained/new P6 runtime generation adoption (`PACK_REPLACEMENT`, `SHADERS_OFF`,
  `GL_CONTEXT_LOSS`): final old use completes, reset the affected P11 controller with the matching
  reason, then perform P6 adoption. Only ADOPTED/ALREADY_CURRENT plus fresh P11 Activated permits
  new custom participation; rejected adoption is not accepted publication.
- P6 direct `WORLD_EPOCH`: final old-world use, P11 WORLD_EPOCH reset, then P6 reset and fresh
  activation before next-world customs. Framebuffer resize resets P11 FRAMEBUFFER_RESIZE after
  final pre-resize use; it has no P6 reset counterpart.
- P6 instance disposal: after final use of **all** participants/callbacks, close its owned P11
  controller with terminal `CLOSE`, then call P6 `retire(reason)`. Use UNPUBLISHED_ABORT only
  for never-accepted candidates; REPLACEMENT for replaced/accepted-then-compensated instances
  after the old P4 barrier is actually invalidated; SHUTDOWN before P4 atomic teardown.
  A P4 Rejected candidate remains caller-owned; P4 Accepted or RecoveredOff must be handled
  according to actual owner disposition, never relabeled an unpublished abort.
- Retirement `Retired|AlreadyRetired` authorizes releasing P6's borrowed providers/services.
  `Rejected(WRONG_THREAD|ACTIVE_CALLBACK)` leaves P6 ownership unchanged: keep admission closed
  and services alive, finish the outer callback and retire on the render thread. P11 is not
  reactivated merely to undo its terminal close. No P6 CLOSE/reset alias or runtime close exists.

Failed/absent fresh activation leaves customs at NoCustoms; it does not postpone required P6
adoption/reset or authorize the old pipeline to resume. Rejected P11 activation locally preserves
its prior tuple only until the composition's mandatory reset/off/retirement boundary; that local
atomicity does not override P7's failed-rebuild-to-off policy. A discarded old controller cannot
be installed into the replacement runtime.

---

## 5. Cross-phase interfaces

### 5.1 Interfaces exposed by Phase 11

| Exposed contract | Exact content | Consumer |
|---|---|---|
| `CustomExpressionCompiler` / `CustomExpressionCompileRequest` | exact compile entry points; D-P11-27 shared uniform/variable namespace with first-owner duplicate/collision law, forward resolution and exact SCC/reverse-reader isolation (§§4.1–4.5); deterministic partial-success build; no pack I/O or P3 schema change | Phase 7 composition/reload, Phase 2 harness |
| `PlanBuildResult` | closed `Success(plan,diagnostics)`, `Partial(plan,diagnostics)`, `Failure(diagnostics)`; diagnostics never null/empty on failure; build disposition is not pipeline acceptance | Phase 7, Phase 2; GUI receives §5.5 projection only |
| `CustomExpressionPlan` | immutable metadata and valid-uniform declaration order; private shared definition graph with independent upload designation, prerequisite-first once-per-refresh converted memo values/effects, declaration-order uniform-only submission (§§4.3–4.8); no program/GL state | controller, Phase 2 |
| `CustomExpressionControllerFactory` / `CustomExpressionController` | `create(ExpressionMetricsSink, ExpressionDiagnosticSink)` requires both non-null, typed diagnostic sink borrowed until close; synchronous delivery/failure containment in §4.9; sole lifecycle, single P6 bridge; §4.12 activation/reset/CLOSE and exact P6 retirement ownership remain binding | Phase 6 installation, Phase 7 lifecycle, Phase 2 collector |
| `ExpressionContextSchema` | immutable `BIOME_*` name→id map and fixed fourteen view-boolean names | `mod.glue`, compiler |
| `ExpressionContextProvider` / request/result/snapshot | one loader-neutral biome/weather/view snapshot per refresh; closed available/unavailable result | `mod.glue`, scripted tests |
| backend selection | both compile entry points reject noncanonical IDs before adaptation/parsing with Failure and one ERROR/CHAT_AND_LOG SourceLess UNSUPPORTED_BACKEND; stable ID uses kind, pack fingerprint, requested ID (§4.11), including empty declarations; graph/build/value/frame/memo types remain private | v0.4 interpreter; Phase 2; Phase 14 internal candidate |
| `RandomSource` | `nextFloat()` in `[0,1)`; injectable, activation-tuple-lifetime stream discarded on every reset and freshly supplied by later activation | `mod.glue`, tests |
| `ExpressionMetricsSink` / `ExpressionMetrics` | synchronous non-null immutable aggregates; no-op default; sink failure disables metrics only; no pack data or node callbacks | Phase 14 OQ-22 ledger |
| `ExpressionDiagnosticSink` / `ExpressionDiagnostic` / GUI projection | exact typed `report(ExpressionDiagnostic)`, Declaration or SourceLess location; §4.9 delivery/coalescing/failure/lifetime/result rules; compiler returns load records, controller emits runtime/activation records; §4.9.1 projects both locations without source to final-attempt GUI snapshot | Phase 7 collector/P1 adaptation, Phase 2 recording; Phase 12 direct safe display only |
| `ExpressionConformanceVectors` / `ExpressionConformanceCase` | exact §5.6 original vector/provider/expected-effect contract, using existing compiler/controller/P6 bridge; no harness dependency in engine | Phase 2 RUN-EXPRESSION-CONFORMANCE, Phase 14 conditional differential evaluation |

All types above are pure Java. Consumers may retain immutable plans, schemas and source-free
GUI/conformance value snapshots; they may not retain a live callback view or mutable controller internals.

The exact consumer-visible declarations and semantics in §§2.3, 4.1–4.5, and 4.8–4.12 are incorporated
into this §5 publication and are binding, including record fields, closed variants/enums, callable
shapes, lifecycle, ownership, and error semantics. Every consumer-visible change to an incorporated
API, schema, variant, lifecycle, ownership rule, or semantic must update the corresponding §5 row
in the same document revision; an unchanged cross-reference does not waive synchronization.

### 5.2 Phase 3 contract consumed

Phase 11 consumes Phase 3's published binding algebra:

```java
enum CustomExpressionKind { UNIFORM, VARIABLE }
enum CustomExpressionType { FLOAT, INT, BOOL, VEC2, VEC3, VEC4 }
record CustomExpressionDecl(CustomExpressionKind kind,
                            CustomExpressionType type,
                            String name,
                            String rawExpression,
                            int sourceOrdinal,
                            SourceAttribution attribution) {}
List<CustomExpressionDecl> PackConfiguration.customExpressions();
```

The list must be immutable, source ordered, lossless after Properties unescaping, retain duplicates
for Phase 11 diagnostics, and every record field and the ordered list participate in
`PackConfiguration` fingerprinting. Phase 3 owns key/type/name validation; Phase 11 consumes this
projection without reopening pack files or reinterpreting Properties syntax
(Phase 3 §§2.2/5.1 custom-expression declaration algebra and §5.3 schema discipline).
Before extracting declarations, composition accepts exactly current `CURRENT_SCHEMA_VERSION`
(23 adopted by D-P11-25), including matching current nested IdMappingInput; reject every other version/mismatch, never upgrade older configurations or manufacture missing declarations.
The compile adapter copies this same configuration's ordered list and fingerprint unchanged;
it neither materializes a different option catalog nor uses stale pack declarations with new macros.

**Schema23 receiver receipt — D-P11-25, 2026-09-08 (unverified).** Admission uses the
current constant, now23, for containing/nested/inspection schemas and same-configuration identity
before extraction, compilation or reuse; MaterializedSource-v23 remains opaque. D-P11-21 and
older numeric receipts are historical. P3's typed selector ranges alter upstream identity, not
expression parsing. Carry the current fingerprint unchanged; ordinary/CLASSIC and alternate rules,
same-load assets/native/option contracts, nine source-free trees and projectionVersion=1 remain
unchanged. No binary authority, extra source parsing or inferred registry translation is granted.

**Historical schema21 receiver receipt — D-P11-20, 2026-09-08 (unverified).** Adopted P3
§0.63/D-P3-70/§5.3. Containing configuration and nested `IdMappingInput` each satisfy
`schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION` and agree before declaration extraction,
compilation, retention or plan reuse; 21 is the dated adopted value. Any received inspection
snapshot must match the same current schema/configuration identity before reporting. Reject
old/future/mismatched values without relabeling, deleting a profile component or synthesizing
old defaults/empty fields. The schema20/19 receipts below are historical, not admission gates.
Carry the current configuration fingerprint into existing compile/plan identity unchanged.
`MaterializedSource-v21` is upstream cache invalidation, not an expression input or source parser.
P4 D-P4-31's `RegistryFingerprint/own-build-v1` was the opaque composition identity at receipt;
the current domain is P4 D-P4-43's `RegistryFingerprint/profile-selection-v3`. P11 neither
parses/repairs either opaque token nor requires new registry getters or resolution evidence.
D-P3-69 same-load assets/nine source-free trees, declarations/ordering/duplicates, expression
semantics, actual context/random provider and P6/controller lifetimes remain unchanged.
No binary acquisition or timing API is granted; fresh owner/receiver reviews remain required.

**Historical schema20 receiver receipt — D-P11-19, 2026-09-08 (unverified).** Adopted P3
§0.62/D-P3-69/§5; earlier current-version assertions and the schema19 receipt below are
historical. Require containing/nested schema20 before extracting declarations, compilation
or plan reuse. Required non-null `assets` after `sources` is the exact same-load P3 capability
paired with the exact containing `PackIdentity`; foreign-load pairing is invalid even if
structurally equal. Carry the owner's metadata-derived configuration fingerprint unchanged
into existing compile/plan identity, not a new expression input. No dummy field, fabricated
empty manifest, capability reconstruction, old-schema or resource-epoch upgrade is allowed.
Expression declarations/ordering/duplicates, evaluator and P6 lifecycle remain unchanged;
P11 acquires/decodes no binary assets. Source-free inspection preserves the actual P3 ninth
canonical assets metadata section, digest strings via TextHash, original eight meanings and
projectionVersion=1, not bytes/cursors/providers. P13 owns optional owned-sidecar-only recovery;
other P3 safety/bounds/index/container/source/configuration failures remain fatal.
Fresh owner/receiver whole-document review and IR-01 remain; no prior PASS covers this edit.

**Historical schema19 receiver receipt — 2026-09-07:** adopted P3 D-P3-68/§0.61 and §11 migration
for containing/nested schema and same-build fingerprint identity only. Expression list,
ordering, duplicates, evaluator and P6 lifecycle contracts are unchanged; no materialized
native text or translator success enters compilation. Prior schema18 decisions remain
historical; current producer/receiver reviews and IR-01 are not closed by this receipt.

### 5.3 Phase 6 contract consumed

Phase 11 adopts the current, unverified Phase 6 §5 contracts:

- one `CustomUniformBridge.refresh(ResolvedProgramDescriptor, BuiltInExpressionView,
  CustomUniformUploadSink)` (`docs/phase6/v1/PHASE_6_DOC.md:1539`–`:1543`);
- exact-name `Present(ExpressionValue)` / `Absent` lookup and the closed scalar/vector/mat4
  runtime values (`:1546`–`:1564`);
- typed immutable upload submission and closed refresh result (`:1565`–`:1589`);
- built-ins-first execution on every successful activation, with Phase 11 owning expression errors
  and Phase 6 owning GL uploads (P6 §4.13);
- definition-order submission, finite values, type/location checks, duplicate rejection, and
  accepted-prefix semantics (§4.13, `:1521`–`:1673`).
- permanent `retire(UNPUBLISHED_ABORT|REPLACEMENT|SHUTDOWN)` and exact
  `Retired|AlreadyRetired|Rejected(WRONG_THREAD|ACTIVE_CALLBACK)` lifetime/final-use rules
  in P6 §4.14; P11's own CLOSE remains terminal but is never forwarded as a P6 enum value.

Phase 11 does not retain `ResolvedProgramDescriptor`, inspect a linked layout, resolve a location,
install a fourth participant, or invoke a Phase 6 provider.

### 5.4 Phase 6 schema, boolean, and absence contracts consumed

Phase 11 consumes Phase 6's immutable versioned `FixedExpressionInputSchema`, whose exact-name
lookup returns `Present(FixedExpressionInputType)|Absent` and matches the runtime value view. It
also consumes `CustomUploadCommand.Bool1(name,boolean)`, with Phase 6 owning linked-GLSL-`bool`
validation and 0/1 GL encoding. The sink's closed outcomes are `Accepted`, normal no-warning/no-GL
`SkippedAbsent`, and `Rejected(stableDiagnosticId)`; only actual invalid names, type mismatches, and
duplicates reject. These grants and their three authoritative refresh counters are binding in
Phase 6 §4.13 (`docs/phase6/v1/PHASE_6_DOC.md:1525`–`:1676`). Phase 6 retains ownership of active-layout
validation, GL encoding, diagnostics, and upload isolation.

### 5.5 Composition handoff without a Phase 7 dependency

Later composition must:

1. create one controller with `create(metricsSink, diagnosticSink)` using a P7-owned real typed
   collector/reporting adapter, then install it into Phase 6 before first activation;
2. compile a plan from the exact accepted Phase 3 configuration and Phase 6 schema;
3. construct the `mod.glue` context provider and a fresh random source for each activation;
4. activate the complete tuple before the corresponding registry/uniform runtime is used;
5. forward every lifecycle event through §4.12's binding map and ordering; successful fresh
   activation must precede custom participation in new-state use, while failed/absent activation
   leaves `NoCustoms` and does not block Phase 6 lifecycle progress;
6. forward returned load diagnostics once and receive runtime/activation diagnostics through
   `ExpressionDiagnosticSink.report(ExpressionDiagnostic)` (§4.9), preserving typed records/IDs;
   adapt to P1 `DiagnosticReporter.report(EngineDiagnostic)` with P11 WARNING→P1 WARN,
   ERROR→ERROR, CHAT_AND_LOG→CHAT and LOG_ONLY→LOG_ONLY. Use registered fixed message keys
   per kind, sanitized safe arguments, bounded real attribution/span in log-only detail when
   present and explicit absent detail for SourceLess; logChannel uses the registered expression
   channel. P1 handles client-thread delivery/no-player logging. Stable-ID coalescing remains
   in the typed collector/controller, since EngineDiagnostic has no stableId field.
   Separately publish only §5.5.1's safe direct GUI projection at final attempt outcome;
   neither GUI nor P2 conformance recovers typed records by reverse-converting EngineDiagnostic.

These are Phase 11's published requirements, not assumptions about Phase 7 internals.

---

#### 5.5.1 Phase 12/7 direct diagnostic adoption

§4.9.1's exact records and `ExpressionDiagnosticGuiProjector.project(...)` are binding.
P7 owns `ExpressionDiagnosticGuiSource.current() -> Optional<ExpressionDiagnosticGuiSnapshot>`;
P12 reads it on presentation/refresh beside `ReloadOutcomeSource`, not through a frame channel.
Empty clears the slot. P12 displays only this source-free projection, using final attempt outcome,
selected pack identity, configuration fingerprint and serial, with no expression parsing or P1
store/channel conversion. R11-1 names this coordinated P7/P12 adoption; current documentation
adoption remains unverified and grants no runtime-delivery claim.

### 5.6 Phase 2 evaluator conformance receiving request

R11-2 requests and Phase 2 adopts the named **RUN-EXPRESSION-CONFORMANCE** with
`ORIGINAL_VECTORS` and `LOCAL_MATRIX` modes. P11 owns the vector catalog, P2 the orchestration
and adapter; no `:engine` dependency on `:conformance`, test framework or pack acquisition results.

```java
interface ExpressionConformanceVectors {
    List<ExpressionConformanceCase> cases();
}
record ExpressionConformanceCase(
    String caseId, CustomExpressionCompileRequest request,
    List<ExpressionConformanceStep> steps, ExpressionConformanceExpected expected) {}
sealed interface ExpressionConformanceStep {
    record Activate(List<ExpressionContextResult> contexts,
                    List<Float> randomValues) implements ExpressionConformanceStep {}
    record Refresh(BuiltInExpressionView builtIns,
                   List<CustomSubmitResult> sinkResults) implements ExpressionConformanceStep {}
    record Reset(ExpressionResetReason reason) implements ExpressionConformanceStep {}
    record Close() implements ExpressionConformanceStep {}
}
enum ExpressionConformanceBuild { SUCCESS, PARTIAL, FAILURE }
record ExpressionConformanceObservation(
    List<CustomUploadCommand> commands, List<ExpressionDiagnosticKind> diagnosticKinds,
    List<String> stableDiagnosticIds, List<ExpressionDiagnosticLocation> diagnosticLocations,
    CustomRefreshResult refreshResult,
    int contextSamples, int randomSamples) {}
record ExpressionConformanceExpected(
    ExpressionConformanceBuild build, List<ExpressionDiagnosticKind> loadDiagnosticKinds,
    List<String> loadStableDiagnosticIds, List<ExpressionDiagnosticLocation> loadDiagnosticLocations,
    List<ExpressionConformanceObservation> refreshes) {}
enum ExpressionConformanceVerdict { PASS, FAIL, UNSUPPORTED }
record ExpressionConformanceCaseResult(
    String caseId, String backendSemanticId, String fixedSchemaVersion,
    String contextSchemaVersion, ExpressionConformanceVerdict verdict,
    List<String> mismatchPaths) {}
```

These are conformance-facing original fixtures, not new production evaluator entry points.
Lists and scripted views are deeply immutable, non-null, stable ordered snapshots.
Diagnostic kind/ID/location lists have identical lengths and matching order. Original fixtures may
contain their own synthetic Declaration locations; observations compare them exactly or compare
SourceLess exactly. These fixture locations are never copied into committed run reports. P2's
adapter checks both compiler entry points for build equivalence from the same original declaration
data before proceeding, and reports a mismatch rather than selecting the favorable result.
P6 owns `CustomSubmitResult`, `CustomUploadCommand`, `CustomRefreshResult` names and closed algebras.
The adapter calls existing `compile`, controller `activate/reset/close`, and bridge `refresh`;
it uses a P6-conforming scripted view and recording sink and an authentic synthetic P4/P6
activation context/descriptor, not a forged production credential or real GL. On each Activate,
the scripted context provider consumes exactly one result per requested snapshot; the random
provider consumes one value per nextFloat. Exhaustion is provider failure, not cycling/filling.
Each Refresh supplies one sink result per actual submission; unused/exhausted entries fail the
vector. All traces include explicitly scripted frameCounter/time values; no wall-clock waits.

Expected command values compare raw binary32 bits and exact boolean/int values, ordered names,
diagnostic kinds/stable identities and refresh counts. `refreshes` contains one observation per
Refresh, including NoCustoms after a nonterminal reset. Close is terminal and last; no fixture
refreshes a closed controller as if it were live. Runtime diagnostics are captured from the exact
`ExpressionDiagnosticSink` supplied to `create(metricsSink, diagnosticSink)`, in callback order;
P2 records typed kind/ID/location before reporting, never infers errors from omitted commands.
Load diagnostics come from PlanBuildResult; compare their kinds, IDs and location variants directly.
Collector failure makes the vector FAIL with a source-free delivery-failure mismatch path.
Load failure cases have no Activate/Refresh; every case's expected data is authored independently
from §3/§4 semantics, never generated by the evaluator under test.

Mandatory stable case families are `EXPR-OPERATORS`, `EXPR-FUNCTIONS`, `EXPR-MATRIX`,
`EXPR-ERROR-ISOLATION`, `EXPR-SMOOTH`, `EXPR-PROVIDER`, and `EXPR-LIFECYCLE`, with caseId
suffixes identifying each §8.1–8.3 vector. Coverage includes every named function/operator,
matrix row/column and exclusion, partial load/variable failure, sink absence/rejection and
accepted-prefix counts, lazy random consumption, same-frame smooth/no-double-advance, independent
rise/fall/reset traces and provider schema/availability failure. This evaluates the language,
not merely Properties parsing or successful shader loading.

The original catalog includes these fixed receiving vectors (all declarations are project-owned
synthetic fixtures, with unique ordinals and current schemas; duplicate-name cases are explicit):

| caseId | Input / scripted sequence | Independent observable expectation |
|---|---|---|
| EXPR-FUNCTIONS/remainder-vs-floor | float uniforms `a=-5%3`, `b=fmod(-5,3)`, `c=frac(-1.25)`, `d=round(-1.5)`; all sinks Accepted | ordered Float1 values `-2` (`c0000000`), `1` (`3f800000`), `0.75` (`3f400000`), `-1` (`bf800000`); no diagnostics; Completed(4,0,0) |
| EXPR-OPERATORS/lazy-random | `a=if(false,random(),0.25)`, `b=random()`; random script `[0.75]`; Accepted sinks | a=`0.25` (`3e800000`), b=`0.75`; exactly one random sample, Completed(2,0,0), no diagnostic from the unchosen branch |
| EXPR-MATRIX/row-column | `a=gbufferModelView.2.1`; scripted MAT4 has logical element `[row 2][column 1]=9` and every other element 0 | a=`9` (`41100000`), not transposed zero; separately absent matrix disables only a with INPUT_ABSENT and no submission |
| EXPR-ERROR-ISOLATION/divide | ordered `a=1/temperature`, `b=2`; scripted temperature=0, Accepted sink for b | load Success; runtime DIVIDE_BY_ZERO disables a only; b alone submits `2` (`40000000`), Completed(1,0,0); subsequent refresh omits disabled a until reset |
| EXPR-SMOOTH/same-frame-reset | `a=smooth(7,temperature,1,2)`; contexts target 0 at frame 1, target 1 at frame 1 again, target 1 at frame 2 with frameTime=1; reset WORLD_EPOCH, refresh, fresh activation target 0.25 | a=`0`, `0`, `1` in the first three refreshes; reset refresh NoCustoms/zero provider calls; fresh activation initializes at `0.25`, not old 1; context sampled once per active refresh |
| EXPR-SMOOTH/lazy-late-use | `a=if(is_in_water,smooth(7,temperature,1),0)`; frame1 true/target0; frame2 false then true/target1; frame3 false then true/target0; all later frames frameTime=1 | clock 0,1,1,2,2; outputs 0,0,1,0,0; cell committed times 0,0,1,1,2; each reached late site gets dt=1 and snaps, never dt=0 from the earlier skipped branch; all Completed(1,0,0), no diagnostics |
| EXPR-SMOOTH/late-first-use | same expression; frame1 false; frame2 false then true/target0.25; frame3 false then true/target1; frameTime=1 | clock 0,1,1,2,2; outputs 0,0,0.25,0,1; cell uninitialized through first two refreshes, initializes at time1 with no startup ramp, then advances at time2; raw bits 00000000,00000000,3e800000,00000000,3f800000 |
| EXPR-ERROR-ISOLATION/unsupported-empty | both compile entry points, empty declarations, backend `schmaloogium:unsupported-test` | Failure, exactly one UNSUPPORTED_BACKEND ERROR/CHAT_AND_LOG with SourceLess and ID(kind, pack fingerprint, requested ID); no plan, parsing, Activate or Refresh; projector emits empty declarationName and fixed safe summary |
| EXPR-PROVIDER/unavailable | valid `a=temperature`; context script Unavailable with a stable project-owned diagnostic ID | Aborted with (0,0,0), no commands, one context sample; unrelated program remains usable |
| EXPR-OPERATORS/uniform-reference | source order: `uniform.vec3.tint=vec3(level,bridge,level)`, `variable.float.bridge=level+0.25`, `uniform.float.level=0.5`; one refresh; all sinks Accepted | Success, no UNKNOWN_NAME/other diagnostic; Float3 tint=(0.5,0.75,0.5) then Float1 level=0.5; bridge never submits; Completed(2,0,0); dependency evaluation does not reorder uploads |
| EXPR-OPERATORS/uniform-conversion-memo | source order: `uniform.float.reader=count+count+0.5`, `uniform.int.count=2+random()`; random script [0.75,0.25]; two refreshes in the same frame; sink script [Accepted,SkippedAbsent] each refresh | Success; exactly one sample per refresh; reader=4.5 (40900000), count=integer 2 offered in that order each time; Completed(1,1,0) twice; no diagnostics; absence does not erase count's converted memo value |
| EXPR-OPERATORS/definition-lazy-effects | source order: `uniform.float.a=if(false,hidden,if(false,random(),0.25))`, `variable.float.hidden=random()`, `uniform.float.b=random()`; random script [0.75,0.5] | Success; eager hidden consumes 0.75 once but never uploads; a's inner lazy branch consumes none; ordered a=0.25,b=0.5; exactly two samples; Completed(2,0,0), no diagnostics |
| EXPR-ERROR-ISOLATION/uniform-cycle | source order: `uniform.float.a=b+c`, `variable.float.b=a`, `uniform.float.reader=a`, `uniform.float.c=temperature`, `uniform.float.good=c+1`; temperature=0.5 | Partial; exactly a,b are CYCLE, reader INVALID_DEPENDENCY; c is neither; only c=0.5 then good=1.5 submit, Completed(2,0,0); no runtime diagnostics |
| EXPR-ERROR-ISOLATION/uniform-runtime | source order: `uniform.float.reader=bad+1`, `uniform.float.bad=base/temperature`, `variable.float.base=2`, `uniform.float.good=base+1`; refresh temperature=0 then 1; all actual sinks Accepted | Success at load; first refresh bad DIVIDE_BY_ZERO, reader INVALID_DEPENDENCY; good alone submits 3 (40400000), Completed(1,0,0); second refresh still only good=3 and no repeated diagnostics until reset/new activation; base remains valid |
| EXPR-ERROR-ISOLATION/shared-namespace | source order: `variable.float.shared=0.5`, `uniform.int.shared=2`, `uniform.float.reader=shared+0.25`, `uniform.float.failed=missingName`, `variable.float.failed=1`, `uniform.float.dependent=failed`, `uniform.float.good=2` | Partial; DUPLICATE_NAME on later shared/failed, UNKNOWN_NAME on first failed, INVALID_DEPENDENCY on dependent; no fallback to later failed; only reader=0.75 then good=2 submit, Completed(2,0,0); variables and disabled duplicate uniform never submit |

For these six D-P11-27 vectors, Refresh uses frameCounter=1/frameTime=0 (both refreshes for the
two-refresh cases), one Available context per refresh, empty random scripts unless listed, and
Accepted for every actual sink call unless a row overrides it. Other referenced schema inputs
are present with the stated values; use current closed schemas.
The existing original-fixture diagnostic identity/location and exact-command comparison rules
apply. No production GL lookup, active-layout query, `glGetUniform`, or alternate upload path is
permitted. P2 receives all six cases through the unchanged §5.6 adapter; no P3 schema bump follows.

These named examples do not replace exhaustive §3/§8 coverage. Every completed vector fixes a
single expected build/runtime disposition; a permissive either-result oracle is prohibited.

PASS requires every expected observable to match; FAIL reports stable source-free mismatch
paths; UNSUPPORTED reports the unsupported backend/schema capability and is never PASS.
P2 aggregates results without dropping unsupported/error cases. LOCAL_MATRIX acquires packs by
P2 policy, uses same-build P3 declarations/fingerprints and these providers, and records every
declaration disposition without committing expression text/assets. A matrix case lacking an
independently authored oracle is disposition-only, not a golden PASS. Reports retain hashes,
semantic/schema IDs and diagnostic kinds, not pack expressions or source spans/paths. No run or
real-pack result is claimed by this architecture amendment.

## 6. Failure modes & degradation

| Failure | Detection | Local disposition | Ladder |
|---|---|---|---|
| malformed token/grammar/limit | plan build | disable one declaration; propagate only through reverse definition dependencies; chat warning for affected uniforms | rung 1 analogue at load |
| unknown/colliding name or wrong type/arity | plan build | same as parse failure | rung 1 analogue at load |
| definition cycle (either kind) | plan build | exact SCC members CYCLE, reverse readers INVALID_DEPENDENCY; preserve independent prerequisites/roots | rung 1 analogue at load |
| duplicate smooth id | plan build | first occurrence owns; disable later owning declaration/dependents | rung 1 analogue at load |
| fixed input absent or schema/value mismatch | refresh | disable failing definition and reverse-dependent readers; no neutral value | rung 1 |
| divide/remainder by zero | refresh | discard failing definition's smooth overlay; disable it and reverse-dependent readers only | rung 1 |
| NaN/infinity/domain/int-range result | every node/boundary | discard failing definition's smooth overlay; disable it and reverse-dependent readers only | rung 1 |
| context provider unavailable/throws | refresh boundary | catch, return `Aborted`, omit remaining customs, keep program | feature-level 2a |
| interpreter invariant/corrupt plan | refresh boundary | return `Aborted`, disable custom-expression feature until reset | feature-level 2a |
| sink rejects name/type/duplicate | Phase 6 sink | count and continue; Phase 6 owns diagnostic/GL decision | rung 2 boundary |
| active program omits custom uniform | Phase 6 sink | normal no-GL skip, no warning | not a failure |
| custom GL upload error | Phase 6 | disable that custom uniform for effective program/generation | rung 2 |
| plan fingerprint/generation mismatch | activation/refresh | refuse tuple or abort refresh; old consistent plan remains or no customs | feature-level 2a |
| close/reset races | thread assertion/state machine | reject illegal caller before mutation; never throw through render callback | feature-level 2a |
| diagnostic destination throws | report boundary | contain RuntimeException; attempted record lost/coalesced, no recursive report/retry, evaluation/ledger/result unchanged; later distinct records attempted; P7 owns independent failure logging, P2 fails collection | reporting-only |

There is no whole-program failure path in Phase 11. Chat warnings are coalesced by stable diagnostic
ID, and repeated program switches cannot spam. Detailed logs retain actual declaration name/source
attribution/span when present, or explicit source absence; never manufacture coordinates or emit
an unbounded expression dump.

---

## 7. Threading & performance notes

### 7.1 Thread ownership

- Declaration adaptation, lexing, parsing, type checking, graph construction, constant folding,
  reachability, and backend build may run off-thread on immutable inputs.
- Plan publication and controller activation occur on the composition thread before the plan's
  first render use.
- `refresh`, context sampling, memo slots, random stream, smooth cells, runtime-disable flags, and
  metrics counters are render-thread-owned.
- `reset` and `activate` serialize with refresh through the composition/render lifecycle; no lock is
  acquired on the refresh hot path.
- Diagnostics produced off-thread are immutable. User-facing delivery happens through the owning
  composition diagnostic queue, not directly from the compiler.

No API accepts or returns a Minecraft object, so scripted provider tests exercise the same engine
path as production.

### 7.2 Allocation and hot-path posture

Plan build may allocate straightforward collections. Refresh uses dense arrays for nodes, values,
memo generations, definition status, and effect overlays. Vectors are stored in primitive lanes,
not freshly allocated records per node. Source spans and diagnostics are plan-time objects.

The hot path performs:

- one context snapshot call;
- at most one Phase 6 lookup for each distinct referenced built-in per refresh;
- at most one evaluation/conversion/effect commit for each retained enabled definition of either kind;
- at most one sink call per successful uniform, reading its memoized value in declaration order;
- no scan over unused variables and no per-program plan construction.

Redundant GL upload elimination remains Phase 6's concern. Phase 11 must still evaluate on every
switch because `random`, `smooth`, biome/view inputs, and variables are observable there.

### 7.3 Performance evidence

The metrics sink is disabled by default and adds one predictable branch. When enabled it reports
aggregates only: plan fingerprint, refresh count, node count, variable memo hits/misses, uniform
success/error/skip counts, elapsed nanos, and allocation-profiler correlation ID. It never records
expression text or per-node values.

The §4.11 budget is an implementation gate, not a promise that bypasses measurement. A compiled
backend remains a compatible Phase 14 experiment, not a v0.4 requirement.

---

## 8. Testability plan

### 8.1 Lexer, parser, type, and graph tests

- one golden parse tree and source-span test for every grammar production and precedence boundary;
- table tests for every legal/illegal declaration type conversion;
- shared namespace: same-kind/cross-kind/type duplicates, invalid first owner without fallback,
  reserved/input/excluded collisions, forward uniform→uniform, uniform→variable and variable→uniform;
- diamonds, long chains, mixed-kind/self/disjoint cycles, exact SCC membership, reverse-reader
  invalidity, independent shared prerequisites, and unused-variable elimination;
- reader-before-dependency declaration `a=b+1; b=temperature` emits `b,a` and uploads the expected
  value; source ordinal only orders simultaneously ready definitions;
- `a=b+c; b=a; c=temperature`, with separate uniforms reading `a` and `c`: only `a,b` are
  `CYCLE`, the first uniform is `INVALID_DEPENDENCY`, and the independent `c` uniform still uploads;
  duplicate reads do not duplicate graph dependencies or evaluation;
- parser limits and adversarial deep/wide inputs without stack overflow or client exception;
- fingerprint determinism under identical input and changes to text/order/schema/backend semantic ID.

### 8.2 Operator and function golden vectors

Every row in §3.1 and §3.2 has an exact binary32 output or exact diagnostic vector. Mandatory edge
vectors include:

- negative `%` versus negative `fmod`;
- all comparison/equality combinations and signed zero;
- inverse trig, log, sqrt, pow, exp, and tangent domain/non-finite failures;
- negative `frac`, floor/ceil/round ties, min/max candidates after the second position;
- lazy `&&`, `||`, multi-branch `if`, unchosen failing branches, and random consumption count;
- `between` endpoints, negative epsilon, and `in` matches at first/middle/last position;
- vec2/3/4 arity, member/color aliases, rejected `.w/.a`, and every matrix row/column;
- every smooth overload, explicit/automatic key stability, duplicate ids, rise/fall, zero/default
  fade, repeated switches in one frame, lazy skipped/late-first-use §5.6 tables, counter wrap, and reset reasons.

The smooth trace test computes the published equation independently from tabulated inputs; it does
not call the implementation helper to generate its expected values.

### 8.3 Runtime, provider, and isolation tests

- scripted Phase 6 input schemas/views cover each scalar/vector/matrix variant, every Appendix D
  permitted name, all exclusions, unknown, absent, and schema mismatch;
- scripted context providers cover every boolean, biome constant, temperatures/rainfall, provider
  unavailability, and world-schema replacement;
- shared variable or uniform is evaluated/converted once per refresh even with many readers;
- uniform submission order is declaration order after dependency-order evaluation; only uniforms
  submit, and an absent/rejected uniform remains usable as a memoized expression dependency;
- parse failures, shared-definition runtime failures, direct uniform failures and sink rejection
  leave independent commands intact; §5.6 fixes eager-definition versus lazy-AST effect behavior;
- failed definitions rollback both smooth value and last-committed timestamp while random consumption
  follows the specified left-to-right rule; successful definitions commit both exactly once;
- typed runtime sink observations precede Completed/Aborted/Rejected, preserve IDs/real or absent
  locations, and throwing-destination containment leaves evaluation and counters unchanged;
- unsupported backend with empty/nonempty declarations fails source-less before parsing through
  both compile entry points; GUI projection never invents declaration/source data;
- absent active-program uniform produces a normal skip and no chat warning;
- bool maps only through the verified Phase 6 bool command;
- controller installation/activation/reset/close state transitions and late/duplicate calls;
- no Minecraft/Forge/Cleanroom/Mixin/LWJGL class appears in engine compile/runtime dependencies.

### 8.4 Real-pack and performance gates

The Phase 2 extension downloads licensed matrix packs under the existing external-fixture policy;
pack bytes or golden images are never committed. It captures custom declarations through Phase 3,
compiles them through this plan, and evaluates against scripted provider traces. Each unsupported or
failed expression must be attributed, not silently skipped.

The implementation gate is:

1. all headless golden vectors pass;
2. every matrix-pack custom declaration has a parse/type/evaluation disposition;
3. scripted end-to-end commands match expected values/order/errors;
4. the interpreter meets the measured §4.11 budget or records an OQ-22 handoff with profiles;
5. the seam/dependency checks prove pure `:engine` and zero steady-refresh allocation.

---

## 9. Milestone staging

| Component | Milestone | Exit condition |
|---|---|---|
| clean-room grammar, spans, limits, typed AST | v0.4 | all Appendix F.6 tokens/operators parse and type-check |
| fixed/biome/view input schemas and provider SPI | v0.4 | complete catalog and exclusion tests pass |
| shared definition graph, cycles, reachability, memoization | v0.4 | mixed uniform/variable graph/isolation vectors pass |
| complete function registry and lazy semantics | v0.4 | every §3.2 golden vector passes |
| smooth/random state and controller/per-cell clock | v0.4 | independent late-use/lazy/rollback/reset/cadence vectors pass |
| interpreter backend and Phase 6 bridge | v0.4 | scripted program-switch commands pass |
| typed diagnostic sink/location/projection, lifecycle, metrics | v0.4 | P7/P2 delivery, source-less build, sink-failure and lifecycle vectors pass |
| real-pack Phase 2 extension | v0.4 | matrix declarations receive complete dispositions |
| compiled MethodHandle/bytecode backend candidate | v0.5 / Phase 14, conditional | OQ-22 evidence shows need and semantic differential suite passes |

No post-v0.5 expression-language extension is implied. New syntax or functions require new contract
evidence and a language semantic-version change.

---

## 10. OQ & spike specifications

Phase 11 is assigned no open question (`docs/design/v3/DESIGN.md:2281`). It must nevertheless emit
one OQ-22 ledger entry for Phase 14 because the research row explicitly includes
“expression-engine compilation” among implementation-time spot checks
(`docs/research/v1/RESEARCH.md:1028`). This is a handoff, not a Phase 11 resolution.

### 10.1 OQ-22 expression-backend ledger handoff

**Question.** Does real-pack program-switch expression cost justify a compiled
MethodHandle/bytecode backend over the v0.4 interpreter?

**Procedure.** Run the §4.11 representative workload on at least two supported Java 25 platforms,
record pack/custom definition counts, switches/frame, node evaluations, p50/p95/p99 total
expression nanos/frame, worst refresh nanos, steady allocations, plan-build time, and profile top
nodes. First profile the unmodified interpreter; then allow only local interpreter cleanup.
Only when the supported-real-pack miss and AST-dispatch attribution trigger below both hold,
prototype one compiled backend behind the existing SPI and run the exact semantic differential suite.

**Owner/adoption.** Phase 14 accepts this method and the exact §4.11
`ExpressionMetricsSink`/`ExpressionMetrics` aggregate as its OQ-22 L-11 measurement obligation
(DESIGN v3 Phase 11 compiled-path methodology at 2310–2314). Phase 11 owns language semantics,
vectors and the private backend SPI; Phase 14 owns measurement, evidence ledger and the conditional
backend decision. Only the real-pack miss plus attribution trigger below authorizes a candidate;
synthetic stress alone cannot demand a compiler. Recipient adoption is documented/unverified.

**Success for interpreter.** All real packs and the synthetic stress workload meet p95 ≤ 0.25 ms,
p99 ≤ 0.50 ms, and zero steady allocation without suppressing evaluation or diagnostics.

**Trigger for compiled candidate.** A supported real pack misses the budget after measured local
cleanup, and profiles attribute material time to AST dispatch rather than provider/sink work.

**Compiled candidate success.** It improves the failing p95 by at least 2×, meets the frame budget,
has bounded build time/memory, and is bit/diagnostic/effect-order identical on every golden and
fuzz vector.

**Fallback.** Keep the interpreter. If a compiler is slower, unstable, adds unsafe bytecode or a
license-heavy dependency, or differs semantically, reject it without blocking v0.4.

The implementation effort records results in the OQ-22 ledger and Phase 11 addendum; this build
session does not run the spike or update RESEARCH.md.

---

## 11. Decisions & open items

### 11.1 Phase-local decision log

| ID | Decision | Rationale and contract check |
|---|---|---|
| D-P11-1 | clean-room a typed AST interpreter for v0.4; no stareval reuse | required license gate failed; App F.6 remains sufficient; `docs/design/v3/DESIGN.md:2310`–`:2322` |
| D-P11-2 | use Pintonium only as corroboration for resolver/graph/backend seams | source verifies the shape, but its function registry is incomplete; contract is independently mapped in §3 |
| D-P11-3 | arithmetic is finite binary32; `INT` is a checked declaration/variable boundary | shipped docs call parameters float while declarations include int; makes conversion explicit and testable |
| D-P11-4 | uniform outputs are not expression symbols; only variables are reusable intermediates | shipped docs say variables “can be used in other variables or uniforms” (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:327`–`:330`) |
| D-P11-5 | preserve original uniform declaration order while evaluating variables through memoized dependencies | satisfies Phase 6's verified submission-order contract and Appendix F.6 variable semantics |
| D-P11-6 | isolate all expression-local load/runtime errors; reserve `Aborted` for structural refresh failure | exact rung-1 requirement and Phase 6 prefix contract |
| D-P11-7 | use the documented behavioral smooth correction, keyed plan-wide and advanced once per frame | Appendix F.6 requires persistent time correction; behavioral digest supplies exact formula; §4.7 |
| D-P11-8 | select interpreter unless measured real-pack cost misses the written budget | G2.5 and Phase 11's explicit interpreter-first direction |
| D-P11-9 | exclude all five D.4 inputs plus fogMode/fogColor | Appendix F.6 directly requires the seven-name union; Phase 6 matches it; DESIGN's five-name restatement is stale |
| D-P11-10 | consume Phase 6's explicit bool command and active-program absence result | preserves Phase 6-owned encoding and makes normal shader-layout variation a no-op rather than an error |
| D-P11-11 | make smooth writes transactional per definition; keep random consumption left-to-right and non-rewinding | prevents partial smooth state while giving the injected random source a precise, implementable order |
| D-P11-12 | automatic smooth keys derive from source/AST identity, never execution order | stable across program switches and backend implementations |
| D-P11-13 | Appendix F.6 fixes the named operator/function surface; §§4.2 and 4.6 fix otherwise undocumented exact typing, domains, coercion, evaluation order, and finite-result behavior | distinguishes authoritative surface provenance from Phase 11's testable semantic choices |
| D-P11-14 | Adopt current P3 schema17 same-configuration declarations and P6 final-use retirement; preserve controller CLOSE but never forward it to P6 | IR-03/10; rejected publication/retirement cannot be relabeled accepted disposal |
| D-P11-15 | Publish the direct source-free immutable GUI projection with final P7 attempt outcome and selected-pack lifetime | IR-17; existing diagnostic channels remain unchanged and raw source never enters GUI |
| D-P11-16 | P11 original vectors/providers are the exact RUN-EXPRESSION-CONFORMANCE receiving contract, with P2-owned adapter and source-free reports | IR-20; function/smooth evaluation and effects, not Properties parsing, determine conformance |
| D-P11-17 | P14 accepts OQ-22 method/metrics and measured decision while P11 retains semantics/SPI | IR-15; interpreter remains the baseline absent a demonstrated real-pack miss |
| D-P11-18 | Adopt P3 schema18 same-load declarations and fingerprint cutover, superseding D-P11-14's schema17 reference only | Locale/session-option/old-light changes invalidate configuration-derived plans; evaluator and P6 lifecycle semantics are unchanged |
| D-P11-19 | 2026-09-08: adopt P3 D-P3-69 schema20, matching nested IDs and exact same-load assets/configuration identity in §5.2 | Supersedes previous current-version assertions only; no expression semantic change, acquisition authority or historical review clearance |
| D-P11-20 | 2026-09-08: adopt P3 D-P3-70 exact-current schema21/nested-ID and current configuration/materialization identity in §5.2; opaque P4 D-P4-31 registry identity stays composition-owned | Earlier schema receipts remain history; evaluator/provider/lifetime semantics unchanged, no extra evidence getters or binary authority; fresh review required |
| D-P11-21 | 2026-09-08: receive P3 schema22/current-constant and unchanged same-load/inspection contracts in §5.2 | Supersedes older numeric receipts only; BLOCK dual-era semantics remain upstream; no implementation clearance |
| D-P11-22 | R11-C1: advance one controller clock per observed frame; each cell accrues from its last successful committed evaluation time; transactional value/timestamp pair | Lazy branches do not spend elapsed time, same-frame successes do not double-advance; independent late-use tables in §5.6 |
| D-P11-23 | R11-C2: require typed ExpressionDiagnosticSink after metrics at factory construction, with synchronous delivery and contained destination failure | P7 real collector/P1 adaptation and P2 recording consume the same exact records; result counters and evaluation are independent of delivery |
| D-P11-24 | R11-C3: closed Declaration/SourceLess location instead of fabricated compile-request attribution | Both compile entry points reject unsupported IDs before parsing even empty lists; preserve declaration provenance/IDs and source-free GUI projection |
| D-P11-25 | Receive schema23/current-constant typed-selector identity and MaterializedSource-v23 | No expression grammar, provider or diagnostic change; no new parser or schema upgrade |
| D-P11-26 | R13-C13-1: reader-to-dependency edges use exact SCC cycle membership and reverse-reader invalidity; unresolved dependency counts produce prerequisite-first order | Preserve independent prerequisites shared with cycles and deterministic ready-source ties; internal planning correction, no schema, SPI or §5 shape change |
| D-P11-27 | C14-1: supersede D-P11-4's uniform-reference prohibition and D-P11-5/26's variable-only graph scope; both kinds share first-owner resolution, declared-type converted memo values and exact SCC/reverse-reader isolation; only UNIFORM designates upload | Shipped author example explicitly reads screenDark from screenDark3 (lines 421–422), disproving the old inference; original §5.6 vectors pin cycles/errors/effects/order. Duplicate ownership is fixed before expression validation, with no fallback. P6 remains sole upload owner; no P3 schema bump or GL lookup; architecture unverified |
| D-P11-28 | C15-1: state the current Phase 3 admission constant as 23 received by D-P11-25 in the §0 header and the §5.2 body, deferring to the newest §5.2 receipt; D-P11-21 and older numeric statements are historical | Textual consistency with D-P11-25 and current P3 bytes; no schema bump, admission-gate or grammar change |
| D-P11-29 | C15-2: repoint §§1.2/1.3/3.3/4.8/5.3/5.4 dependency anchors to current P3 declaration-capture/algebra/precipitation bytes and current P6 §4.13 coordinates, section-level where the review verified no sub-range | Coordinates only; the bullets' incorporated semantics already match the verified endpoints; no §5 grant content or receiver change |
| D-P11-30 | C15-3: restate §1.1 dependency-analysis scope as the shared uniform/variable definition graph per D-P11-27 | Closes the §1 cutover residue; §§4.1/4.4/4.5 and the incorporated §5.1 compiler row already define one shared namespace |
| D-P11-31 | C16-1: the §5.2 D-P11-20 receipt marks P4 D-P4-31's own-build-v1 as the receipt-time opaque composition identity; the current domain is D-P4-43's RegistryFingerprint/profile-selection-v3 | P4 :1953–:1954 supersedes positional-route-v2 two generations past own-build-v1; supersedes D-P11-20's present-tense identity statement only; opacity treatment and scheme-23 admission logic unchanged |

### 11.2 Contradictions, gaps, and rulings

1. **DESIGN versus RESEARCH exclusion restatement.** RESEARCH Appendix F.6 directly requires the
   seven-name union, while the Phase 11 DESIGN row lists only five. D-P11-9 follows RESEARCH and
   the matching verified Phase 6 contract; §11.4 requests correction of DESIGN's stale summary.
2. **PD checklist claim versus Pintonium source.** PD §14 says `IrisFunctions` supplies the full
   checklist (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:719`–`:725`), but the load-bearing
   source leaves `round` unregistered and admits vararg TODOs. Appendix F.6 wins; §3 maps the full
   language independently.
3. **stareval historical credit versus verifiable license.** Neither the missing upstream nor the
   current repository evidence establishes a reusable component-specific grant. The binding rule
   says clean-room when unverifiable; D-P11-1 applies it.
4. **Verification state.** Historical adoption/preflight/reviews remain provenance only.
   §0.13 changes active §5; fresh P11 and current producer/receiver reviews are required.
5. **Measurement handoff.** P14's former out-of-scope rejection is reconciled by §10.1 and
   its L-11 adoption. No measurement or compiled-backend need has been established.

### 11.3 Open handoffs

- **To `mod.glue`/later composition:** implement the exact biome/view snapshot and biome catalog;
  create one runtime random source; forward all reset reasons.
- **To Phase 2:** R11-2 / §5.6 RUN-EXPRESSION-CONFORMANCE, original vectors and local-matrix
  dispositions through the exact adapter. Owner/receiver adoption is unverified; no run is claimed.
- **To Phase 12/P7:** R11-1 / §4.9.1 and §5.5.1 direct typed source-free GUI input,
  final-outcome publication and clear/rejection lifetime; adopted documentation requires review.
- **To Phase 14:** accepted §10.1 OQ-22 method/metrics and conditional decision; semantics
  remain P11-owned, interpreter remains baseline, measurement not yet run.

### 11.4 Requested upstream and maintainer changes

1. Correct the Phase 11 DESIGN row's five-name exclusion restatement to match RESEARCH Appendix
   F.6's authoritative seven-name rule. Phase 11 already implements the authoritative behavior.

No change to RESEARCH.md, any DESIGN.md, PD, or a dependency document is performed by this session.

---

## 12. Implementation checklist

- [ ] **v0.4 — dependency gate:** consume the verified Phase 3 and Phase 6 §5 contracts. **Hook:**
  their newest reviews remain literal PASS for the binding documents read by implementation.
- [ ] **v0.4 — document review:** review current owner and dependency/receiver contracts against
  the header-selected v3 authority. **Hook:** fresh whole-owner review; retired profile/preflight
  machinery remains historical provenance only, per MOVES.
- [ ] **v0.4 — language core:** implement the clean-room lexer/parser, spans, limits, typed AST,
  and semantic version. **Test:** §8.1 grammar/span/limit vectors.
- [ ] **v0.4 — operations:** implement exact precedence, all operators, and every §3.2 function—no
  extras. **Test:** one named golden suite for every §3.1/§3.2 row.
- [ ] **v0.4 — numeric safety:** implement declaration-boundary conversions and finite/domain/
  zero-divisor checks. **Test:** type matrix plus every §8.2 error vector.
- [ ] **v0.4 — inputs:** implement the exact fixed input schema, exclusions, biome constants, and
  fourteen booleans. **Test:** exhaustive catalog/presence/schema tests in §8.3.
- [ ] **v0.4 — shared definitions:** implement §§4.1–4.5 shared namespace/first-owner policy,
  both-kind graph, exact SCC/reverse-reader isolation, reachability and converted memo slots.
  **Test:** §5.6 uniform references/cycles/errors/effects/conversion plus §8.1 graph cases.
- [ ] **v0.4 — stateful functions:** implement deterministic left-to-right random consumption and
  the exact transactional §4.7 smooth state machine/clock/reset behavior. **Test:** independent
  lazy/late-first-use tables, random-consumption, and paired value/timestamp rollback tests.
- [ ] **v0.4 — plans:** implement immutable plan build and partial-success diagnostic results.
  **Test:** fingerprint determinism, mixed-validity build, real declaration and source-less pre-plan attribution through both entry points.
- [ ] **v0.4 — backend:** implement the typed-AST interpreter behind `EvaluatorBackend` with
  source-span errors. **Test:** backend semantic/golden suite and parser-fuzz corpus.
- [ ] **v0.4 — Phase 6 integration:** implement the single bridge with declaration-order commands
  and rung-1 isolation. **Test:** scripted sink order/skip/reject/error integration cases.
- [ ] **v0.4 — lifecycle:** implement context provider SPI, controller activation/reset/close, and
  lifecycle assertions. **Test:** every legal/illegal state transition and provider outcome.
- [ ] **v0.4 — diagnostics:** implement required typed factory sink, P7/P2 collection, P1 adaptation
  and source-free projection. **Test:** stable IDs, source-less/real locations, delivery failure
  containment, lifecycle release, coalescing, sanitation and dependency-path logging.
- [ ] **v0.4 — headless conformance:** add every §8 golden vector, including independent smooth
  traces and effect rollback. **Hook:** Phase 11 pure-JVM test task.
- [ ] **v0.4 — matrix conformance:** extend Phase 2 scripted/matrix-pack runs without committing
  pack assets. **Hook:** download-at-test-time conformance target reports every declaration.
- [ ] **v0.4 — seam/performance:** prove pure-`:engine` dependencies and zero steady-refresh
  allocation. **Hook:** Phase 1 seam check plus allocation profile in §4.11.
- [ ] **v0.5/Phase 14 — OQ-22:** measure the §4.11 budget and record the §10 handoff while retaining
  the interpreter fallback. **Hook:** ledger contains the prescribed pack counts and p50/p95/p99
  data; compiled differential tests run only if triggered.
