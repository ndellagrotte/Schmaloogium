# Schmaloogium — Phase 11: Custom-uniform expression engine — Architecture

## 0. Header

- **Phase:** 11 — Custom-uniform expression engine
- **Milestone:** v0.4
- **Module/package:** `:engine`, `com.schmaloogium.engine.expr`
- **Declared dependencies:** Phase 3 and Phase 6; no Phase 7 dependency
- **Governing design:** `docs/design/v3/DESIGN.md`
- **Design status:** initial build document, not yet verified
- **Date:** 2026-08-03

The commissioning request explicitly selected v3. This document therefore derives its pins from
`docs/design/v3/DESIGN.md` itself and does not transplant coordinates from another revision. The
governing phase row says “Depends on: 3, 6 (no Phase 7 dependency)” and assigns the complete
expression language to pure `:engine` code (`docs/design/v3/DESIGN.md:2281`–`:2288`). The global
module map puts that code in `engine.expr` and requires `:engine` to have zero Minecraft, Forge,
Cleanroom, Mixin, or LWJGL dependencies (`docs/design/v3/DESIGN.md:468`–`:500`).

The dependency gate is open. Phase 3 Review 31 is literal `PASS` with zero blocking findings and
zero corrections (`docs/phase3/reviews/PHASE_3_REVIEW_31.md:48`–`:57`). Phase 6 Review 18 is also
literal `PASS` with zero blocking findings and zero corrections
(`docs/phase6/reviews/PHASE_6_REVIEW_18.md:47`–`:61`). Those reviews verify the current dependency
documents read below.

### 0.1 Inputs actually read

| Input | Portion read | Why |
|---|---|---|
| `AGENTS.md` | complete | repository and document-session rules |
| `docs/MOVES.md` | complete | resolve versioned paths and the six `DESIGN.md` collision |
| `docs/design/v3/DESIGN.md` | Part I, §G0–§G12; Phase 11 spec at lines 2279–2353 | governing global rules, template, and assignment |
| `docs/research/v1/RESEARCH.md` | §0, §1, §3.4 item 4, §6.3 expression row, Appendix D, Appendix F.6 | authority, inputs, cadence, built-in types/exclusions, expression contract |
| `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties` | custom-uniform section, lines 326–425 | shipped pack-author expression specification |
| `docs/phase3/v1/PHASE_3_DOC.md` | complete | declaration-capture dependency contract |
| `docs/phase3/reviews/PHASE_3_REVIEW_31.md` | complete | dependency verification state |
| `docs/phase6/v1/PHASE_6_DOC.md` | complete | built-in value model, cadence, upload sink, lifecycle |
| `docs/phase6/reviews/PHASE_6_REVIEW_18.md` | complete | dependency verification state |
| `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` | §14, lines 707–728 | required expression-architecture evidence and license warning |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/uniforms/custom/CustomUniforms.java` | load, dependency ordering, update/push, optimization portions | verify PD's load-bearing architecture claims |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/CommonIrisRenderingPipeline.java` | custom update/push/optimization call sites | verify cadence and lifecycle claims |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/parsing/IrisFunctions.java` | function registry, casts, smooth, helpers, constructors | verify the claimed App F.6 checklist |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/parsing/SmoothFloat.java` | complete | understand the reference's timing shape without adopting it |
| `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/parsing/IrisOptions.java` | complete | verify operator/precedence evidence without adopting extras |

### 0.2 Extra reads, deviations, and tool disposition

Three genuine gaps required narrow reads beyond the listed inputs:

1. `docs/research/v1/RESEARCH.md:999`–`:1027` was read to quote the exact OQ-22 ledger text that
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

The v3 adoption records are not yet prepared for Phase 11: `docs/MOVES.md:67`–`:90` records v3 as
adopted only by Phase 2, and there is no `verification/targets/phase-11.json`. The v3 adoption
procedure requires a newly derived target profile, dry run, and manifest record
(`docs/design/v3/DESIGN.md:195`–`:220`). The build-session hard rule permits only this v1 phase
document (`docs/design/v3/DESIGN.md:256`–`:270`), so §11 requests those maintainer actions before
the first verification run; this document does not silently perform them.

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
the published expression surface in `docs/research/v1/RESEARCH.md:1492`–`:1508` and
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:326`–`:425`.

---

## 1. Scope & boundaries

### 1.1 Owned by Phase 11

Phase 11 owns these v0.4 components:

- the lexer, parser, source spans, typed AST, name resolver, and immutable executable plan;
- the exact scalar/boolean/vector expression type system and declaration-boundary coercions;
- every Appendix F.6 operator and function, including lazy `if` and stateful `smooth`;
- dependency analysis for `variable.*`, cycle/error propagation, and once-per-refresh memoization;
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
  (`docs/phase3/v1/PHASE_3_DOC.md:1145`–`:1147`). Phase 11 never reopens pack files or reparses
  Java Properties syntax.
- **Phase 6** owns fixed built-in acquisition, current typed values, the post-built-in custom
  callback, active-program location/type checks, GL uploads, upload replay, and GL-error isolation.
  Phase 11 never samples Phase 6 providers, resolves GL locations, or calls LWJGL.
- **`mod.glue`** implements the biome/view snapshot provider. Phase 11 defines only loader-neutral
  value objects and an SPI.
- **Phase 7** later wires lifecycle calls and the provider at the composition root. This document
  exposes that interface without reading or depending on Phase 7.
- **Phase 12** owns GUI presentation, including any profiles/options UI.
- **Phase 14** owns the OQ-22 measurement decision and any compiled evaluator implementation.
- **Phase 2** extends conformance runs with real-pack scripted inputs; Phase 11 owns the pure golden
  vectors those runs call.

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
(`docs/phase3/v1/PHASE_3_DOC.md:690`–`:693`).

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
3. lex, parse, type-check, and constant-fold each declaration independently;
4. build a graph of variable dependencies, reject cycles, and propagate invalid dependencies;
5. retain only variables reachable from at least one valid uniform;
6. assign stable definition, memo-slot, diagnostic, random-site, and smooth-site identities;
7. compile the typed graph through the selected evaluator backend;
8. return an immutable plan plus all load diagnostics. A plan may be useful even when some
   declarations are disabled.

At each successful program activation, Phase 6 calls the installed controller after built-ins.
The controller snapshots the non-Phase-6 context exactly once, opens one evaluation epoch, advances
the refresh clock at most once for the Minecraft frame, evaluates reachable variables once in
precomputed topological order into memo slots, evaluates valid uniforms, and submits successful
values in original declaration order.
One expression failure never aborts unrelated definitions. This matches the required cadence:
customs refresh “on every program switch after built-ins”
(`docs/research/v1/RESEARCH.md:1379`–`:1382`).

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

`INT` is a declaration and variable type, not a second arithmetic dialect. Numeric operators and
functions evaluate in finite `FLOAT`; fixed integer inputs and `INT` variables promote exactly to
float when read. An `INT` definition converts its final finite scalar toward zero after a range
check. This follows the shipped description of `biome`, `temperature`, `rainfall`, and fixed scalar
uniforms as float parameters (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:341`–`:351`),
while preserving the declared `int` storage/upload boundary.

Matrix values are input containers, not expression results. A matrix must be followed by two
literal indices; member access produces one `FLOAT`. Integer vectors likewise expose numeric
members as `FLOAT` because Appendix F.6 provides only `vec2/vec3/vec4`, not `ivec` constructors.

### 2.4 Invariants

1. A published plan is immutable, fingerprinted, and independent of render programs and GL state.
2. Every enabled uniform has one declared type, one typed root, and zero or more variable edges.
3. Variables are not submitted; a reachable variable is evaluated no more than once per refresh.
4. Uniform submissions preserve Phase 3 declaration order, not dependency order.
5. The built-in snapshot, biome/view snapshot, refresh epoch, and random stream seen by one refresh
   cannot change mid-evaluation.
6. No non-finite scalar enters a plan, leaves an evaluator, or reaches Phase 6.
7. An expression-local failure changes only that definition and uniforms that depend on it.
8. Smooth state is keyed by plan fingerprint plus `SmoothKey`, never by active program.
9. A repeated switch in one frame has zero elapsed time but is still a new evaluation epoch.
10. The v0.4 backend is replaceable without changing parsing, typed AST, state semantics,
    diagnostics, provider interfaces, or Phase 6 integration.

---

## 3. Contract conformance map

### 3.1 Declaration, token, and operator coverage

Appendix F.6 declares both forms and says variables are reusable but not uploaded
(`docs/research/v1/RESEARCH.md:1492`–`:1495`).

| Contract surface | Design disposition | Provenance | Primary tests |
|---|---|---|---|
| `uniform.<float\|int\|bool\|vec2\|vec3\|vec4>.<name>=<expr>` | consume Phase 3 declaration; evaluate and submit only successful uniforms | RESEARCH App F.6 `[V:doc]` | `allUniformDeclarationTypes` |
| `variable.<type>.<name>=<expr>` | typed memoized intermediate; never submitted | RESEARCH App F.6 `[V:doc]` | `variablesOncePerRefreshNeverUploaded` |
| numeric literals | decimal syntax in §4.2; finite float value | RESEARCH App F.6 `[V:doc]` | `numericLiteralGrammarAndRange` |
| `pi` | immutable float constant equal to `Math.PI` rounded once to binary32 | RESEARCH App F.6 `[V:doc]` | `piConstantBinary32` |
| `true`, `false` | boolean literals | RESEARCH App F.6 `[V:doc]` | `booleanLiterals` |
| grouping `(` `)`, argument comma | grammar only; no implicit tuple | shipped pack-author docs `[V:doc]` | `groupingAndCallArity` |
| member `.x .y .z .r .g .b` | checked vector/color alias access | RESEARCH App F.6 `[V:doc]` | `vectorMemberAliasesAndBounds` |
| matrix `name.<row>.<col>` | two literal indices, each 0–3; produces float | RESEARCH App F.6 `[V:doc]` | `matrixAllSixteenCellsAndBounds` |
| unary `+ -` | finite numeric scalar; `-` checked after evaluation | RESEARCH App F.6 `[V:doc]` | `unaryNumeric` |
| unary `!` | bool only | RESEARCH App F.6 `[V:doc]` | `logicalNotType` |
| `+ - * / %` | numeric scalars; `/` and `%` zero are runtime errors; §4.6 semantics | RESEARCH App F.6 `[V:doc]` | `arithmeticTruthTable` |
| `> >= < <=` | numeric scalars, boolean result | RESEARCH App F.6 `[V:doc]` | `orderedComparison` |
| `== !=` | same primitive type or numeric promotion; exact finite equality | RESEARCH App F.6 `[V:doc]` | `equalityTypeMatrix` |
| `&& \|\|` | booleans, left-to-right short circuit | RESEARCH App F.6 `[V:doc]` | `booleanShortCircuit` |

Precedence from tightest to loosest is member access/call, unary, multiplicative, additive,
relational, equality, `&&`, then `||`; binary operators associate left. This is conventional and
contains no Pintonium-only Unicode aliases.

### 3.2 Function coverage

The published function list is exact at `docs/research/v1/RESEARCH.md:1503`–`:1506`; no extra
function becomes pack-visible merely because a reference engine contains it.

| Function | Accepted signature and exact behavior | Provenance | Primary test |
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

There is an authority-chain inconsistency. Appendix F.6 names five exclusions—`entityColor`,
`entityId`, `blockEntityId`, `fogMode`, `fogColor`
(`docs/research/v1/RESEARCH.md:1496`–`:1501`)—while Appendix D.4 labels its whole table “excluded”
and additionally contains `blendFunc` and `instanceId`
(`docs/research/v1/RESEARCH.md:1369`–`:1377`). Phase 6's verified interface follows the conservative
union and exposes none of D.4 plus neither fog value (`docs/phase6/v1/PHASE_6_DOC.md:1197`–`:1200`).
Phase 11 therefore cannot and does not expose `blendFunc` or `instanceId`; §11 requests an
authoritative clarification. This is not a silent change to Appendix F.6.

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
    EvaluatorBackend backend) {}

record CustomExpressionSource(
    int sourceOrdinal,
    DeclarationKind kind,
    ExpressionType declaredType,
    String name,
    String rawExpression,
    SourceAttribution attribution) {}
```

`sourceOrdinal` is Phase 3 property order and is stable within the configuration fingerprint.
Declaration names use the already validated Phase 3 grammar. Phase 11 additionally rejects a
variable name that collides with a fixed input, `BIOME_*`, one of the fourteen booleans, `pi`,
`true`, `false`, or a function name. Duplicate variables use first-valid-wins for resolution and
disable each later duplicate. Uniform output names need not enter the expression namespace and
cannot be referenced by other expressions. A duplicate uniform name similarly leaves the first
valid declaration active and disables later definitions, agreeing with Phase 6's first-command
rule.

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
  variable promotes to float on read.
- `INT` declaration boundary accepts a finite numeric scalar, rejects values outside
  `[-2147483648, 2147483647]`, and truncates toward zero.
- `FLOAT` accepts a finite numeric scalar.
- `VEC2`, `VEC3`, and `VEC4` require exactly the matching constructor/result width. There is no
  scalar splat, widening, truncation, or vector arithmetic.
- `BOOL` requires a boolean result.
- Every vector component must be finite.
- `if` value branches may mix `INT` variables/fixed inputs and numeric expressions because both
  are read as float; other branch types must be identical.
- `==`, `!=`, and `in` compare bool with bool or numeric with numeric. Vectors are not comparable.

Declaration-boundary conversions happen before a variable is memoized and before a uniform upload
command is built. Thus a `variable.int` is evaluated and converted once, and every reference sees
the same stored integer. A mismatch disables that declaration at load and propagates a named
dependency diagnostic to uniforms that require it.

### 4.4 Name resolution and input schema

Resolution order is fixed and independent of declaration order:

1. `pi`, `true`, `false`;
2. exact `BIOME_*` constants;
3. exact `biome`, `temperature`, `rainfall` and fourteen view booleans;
4. exact permitted Phase 6 built-in names;
5. exact valid `variable.*` names.

Uniform names never resolve as expression inputs. Variables may refer forward to other variables,
which is why the graph phase follows parsing. A collision is diagnosed rather than resolved by
shadowing.

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

The graph has one vertex per first-valid variable and one directed edge `A -> B` when A reads B.
Uniforms are roots but not graph symbols. A deterministic Kahn traversal uses source ordinal as its
tie-break. Vertices left after traversal form one or more cycles; each cycle member is disabled,
then invalidity propagates to dependent variables and uniforms with a diagnostic path.

After diagnostics, reverse reachability begins from every valid uniform and removes variables that
cannot affect an upload. This is load-time dead-definition elimination only. It never assumes which
programs declare a custom uniform and never suppresses diagnostics for malformed source.

Each refresh allocates no graph objects. Pre-sized memo slots have states `UNVISITED`,
`EVALUATING`, `VALUE`, or `ERROR`. The epoch reset changes slot generation counters rather than
clearing objects. The controller visits all reachable variables once in the precomputed topological
order; `EVALUATING` remains an invariant guard even though cycles were rejected. A variable is
converted to its declared type before its slot becomes `VALUE`. If it becomes `ERROR`, each
dependent uniform fails independently while unrelated roots continue.

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
record SmoothCell(boolean initialized, float value, long lastClockEpoch) {}
```

State is plan-wide and survives every program switch. It is not keyed by `ResolvedProgramDescriptor`.
The state transition for target `t`, rise time `fadeIn`, fall time `fadeOut`, and frame delta `dt`
is exact:

1. Reject non-finite target or fade. Reject negative fade. A missing fade is `1.0`; a missing
   `fadeOut` equals `fadeIn`.
2. If uninitialized, store and return `t` without a startup ramp.
3. Choose `fadeIn` when `t > value`; otherwise choose `fadeOut`.
4. If `dt <= 0`, return the stored value unchanged. If selected fade is zero or `dt >= fade`, snap
   to `t`.
5. Otherwise compute in binary32:
   `updates = fade / dt`;
   `correction = 4.61 - 1 / (0.13 + updates / 10)`;
   `k = clamp(dt / fade * correction, 0, 1)`;
   `value = value + (t - value) * k`.
6. Reject a non-finite intermediate/result; the owning uniform takes the runtime-error path and the
   cell does not commit a partial transition.

This is a behavior-only restatement of the allowed digest, which records the same correction and
separate up/down times (`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:597`–`:601`).
It is not derived from decompiled class or method structure.

The controller derives `dt` from Phase 6's same-activation `frameCounter` and `frameTime`. The first
refresh whose frame counter differs from the last committed counter advances by validated
`frameTime`; later program switches with the same counter use zero. Inequality, not ordering,
handles the documented counter wrap. A frame-counter reset is paired with a lifecycle reset.
Consequently program switching cannot make fades run faster, while every switch still reevaluates
targets and variables as required.

All smooth-cell writes for one definition are transactional: evaluate against an overlay, then
commit only if that variable or uniform produces a valid final value. If two definitions attempt
the same explicit smooth id, the uniqueness rule prevents order-dependent sharing. A variable's
successful update commits once when its topological evaluation fills the memo slot; a later
uniform error does not undo an already valid intermediate.

Smooth state resets on pack replacement, shaders-off, world epoch, framebuffer resize, GL-context
loss, or close. It does not reset on program fallback, stage change, repeated activation, or an
ordinary Phase 6 redundant-upload skip.

### 4.8 Program-switch cadence and Phase 6 bridge

One `CustomExpressionController` is installed through Phase 6 before first use and retained for the
runtime lifetime, as Phase 6 requires (`docs/phase6/v1/PHASE_6_DOC.md:1190`–`:1196`). It holds an
atomic current-plan slot changed only by composition lifecycle calls.

On `refresh(program, values, uploads)`:

1. return `NoCustoms` when no plan is active or no uniform is valid;
2. verify plan/configuration/schema generations;
3. fetch one `ExpressionContextSnapshot` and validate its epoch;
4. resolve `frameCounter`/`frameTime`, establish `dt`, and open a memo epoch;
5. evaluate every reachable variable once in precomputed topological order;
6. visit valid uniform roots in original declaration order, convert each result, and submit one
   typed command;
7. on expression failure, permanently disable only that uniform until the next plan/reset, warn
   once, omit its command, and continue;
8. on sink `Rejected`, count the rejection and continue because Phase 6 owns that diagnostic;
9. on normal active-program absence, count a skip rather than an error;
10. return `Completed(submitted,rejected)` using Phase 6's existing count meaning.

Expression-local errors never produce `Aborted`. `Aborted` is reserved for a corrupt plan,
generation mismatch, provider protocol failure, or backend invariant that makes the remainder
unsafe. Phase 6 commits any already accepted prefix exactly as its contract states
(`docs/phase6/v1/PHASE_6_DOC.md:1217`–`:1223`).

Every valid custom uniform is offered on every activation. Phase 6 decides whether its exact name
and type exist in the active linked program; program absence must be a non-error no-op. No plan is
compiled per program, and no active layout leaks into `engine.expr`.

Upload mapping is:

| Declared type | Required Phase 6 command |
|---|---|
| `float` | `Float1` |
| `int` | `Int1` |
| `bool` | `Bool1` (requested dependency addition; Phase 6 performs GL integer encoding) |
| `vec2` | `Float2` |
| `vec3` | `Float3` |
| `vec4` | `Float4` |

Phase 11 never uses `Int1` as an undocumented bool convention.

### 4.9 Diagnostics and error isolation

```java
enum ExpressionDiagnosticKind {
    LEX, PARSE, LIMIT, UNKNOWN_NAME, DUPLICATE_NAME, DUPLICATE_SMOOTH_ID,
    TYPE, ARITY, CYCLE, INVALID_DEPENDENCY, INPUT_ABSENT, INPUT_SCHEMA_MISMATCH,
    DIVIDE_BY_ZERO, DOMAIN, NON_FINITE, INT_RANGE, PROVIDER, BACKEND_INVARIANT
}

record ExpressionDiagnostic(
    String stableId,
    ExpressionDiagnosticKind kind,
    DiagnosticSeverity severity,
    DiagnosticChannel channel,
    String declarationName,
    SourceAttribution attribution,
    SourceSpan span,
    String summary) {}
```

Stable IDs derive from diagnostic kind, plan fingerprint, declaration ordinal, and source span;
message prose is not hashed. Raw expressions and paths are sanitized before chat. Load diagnostics
are aggregated so one bad line does not hide later errors.

- A uniform lex/parse/type/name/arity error disables that uniform at load and emits one chat-visible
  warning plus a detailed log record.
- A variable load error disables the variable and every dependent uniform. Chat reports each
  affected uniform once; the detailed dependency path is log-only.
- A runtime domain, zero-divisor, absent-input, non-finite, int-range, or evaluator error disables
  the affected uniform for the active plan, emits one chat-visible warning, and continues. If a
  failing variable is shared, each dependent uniform is disabled independently; unrelated uniforms
  continue.
- A partial smooth change made while evaluating a failed definition is rolled back. Already
  consumed `random()` samples remain consumed under §4.6's deterministic order.
- A sink rejection is Phase 6's error and is not relabeled as an expression failure.
- Provider/backend invariant failures return `Aborted`, rate-limit one user-visible message per
  plan/reason, and leave shaders operational with custom uniforms absent.

This is degradation-ladder rung 1: “A custom uniform that errors at runtime disables that uniform
only” (`docs/design/v3/DESIGN.md:419`–`:425`). No expression exception crosses the bridge.

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
```

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
public interface EvaluatorBackend {
    String semanticId();
    BackendBuildResult build(TypedExpressionGraph graph);
}

public interface ExecutableExpressionGraph {
    EvaluationResult evaluate(DefinitionId root, EvaluationFrame frame, MemoTable memo);
}
```

The graph, value algebra, frame, memo API, state transaction, error algebra, and semantic ID are
backend-neutral. Backends may not add functions, change rounding, reorder lazy effects, cache
across refreshes, or expose implementation exceptions.

**v0.4 selects a typed-AST interpreter.** It minimizes license and verifier risk, keeps source spans
for diagnostics, and follows the global “clean code first, optimize with evidence” direction
(`docs/design/v3/DESIGN.md:451`–`:456`). The research row names MethodHandle/bytecode compilation
only as unverified opportunity (`docs/research/v1/RESEARCH.md:781`–`:787`). No bytecode library or
JIT-specific plan is a Phase 11 dependency.

The interpreter has dense node arrays, pre-resolved symbol/function ordinals, primitive memo
storage, and no steady-state allocation after plan activation. Every refresh records aggregate
node evaluations, uniforms attempted, variables memoized, and elapsed nanoseconds through an
optional no-op-by-default metrics sink.

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

enum ExpressionResetReason {
    PACK_REPLACEMENT, SHADERS_OFF, WORLD_EPOCH, FRAMEBUFFER_RESIZE,
    GL_CONTEXT_LOSS, CLOSE
}
```

The composition thread installs exactly one controller before first use. `activate` validates all
fingerprints and swaps the complete plan/provider/random tuple atomically before the next refresh.
The render thread owns refresh and mutable evaluation state. A plan may be compiled off-thread,
consistent with v3's explicit permission for expression compilation
(`docs/design/v3/DESIGN.md:410`–`:415`).

Every reset invalidates memo generations, random state, runtime-disabled-uniform flags, clock
tracking, and smooth cells. `PACK_REPLACEMENT` permits later activation; `CLOSE` is terminal.
Repeated same-reason reset is idempotent. Reset never calls Phase 6 or GL and never retains a
provider beyond close.

---

## 5. Cross-phase interfaces

### 5.1 Interfaces exposed by Phase 11

| Exposed contract | Exact content | Consumer |
|---|---|---|
| `CustomExpressionCompiler` / `CustomExpressionCompileRequest` | deterministic partial-success plan build; ordered declarations, exact schemas, backend; no pack I/O | Phase 7 composition/reload, Phase 2 harness |
| `PlanBuildResult` | closed `Success(plan,diagnostics)`, `Partial(plan,diagnostics)`, `Failure(diagnostics)`; diagnostics never null/empty on failure | Phase 7, Phase 12 display, Phase 2 |
| `CustomExpressionPlan` | immutable fingerprinted graph, valid-uniform declaration order, schema/backend semantic IDs, load diagnostics; no runtime state | controller, Phase 2 |
| `CustomExpressionController` | the single Phase 6 `CustomUniformBridge`; atomic activation, reset reasons, close; rung-1 isolation | Phase 6 installation, Phase 7 lifecycle |
| `ExpressionContextSchema` | immutable `BIOME_*` name→id map and fixed fourteen view-boolean names | `mod.glue`, compiler |
| `ExpressionContextProvider` / request/result/snapshot | one loader-neutral biome/weather/view snapshot per refresh; closed available/unavailable result | `mod.glue`, scripted tests |
| `EvaluatorBackend` / `ExecutableExpressionGraph` | backend-neutral typed graph boundary with fixed semantics and error algebra | v0.4 interpreter; Phase 14 compiled candidate |
| `RandomSource` | `nextFloat()` in `[0,1)`; injectable, plan-lifetime stream | `mod.glue`, tests |
| `ExpressionMetricsSink` | optional aggregate counters/timing, no pack data or node callbacks | Phase 14 OQ-22 ledger |
| `ExpressionDiagnostic` | stable ID, kind, severity/channel, declaration attribution and source span | Phase 7 diagnostics, Phase 12 display |

All types above are pure Java. A consumer may retain plans and schemas because they are immutable;
it may not retain snapshots or mutable controller internals.

### 5.2 Phase 3 contract consumed and requested closure

Phase 3 presently exposes only `CustomExpressionDecl | typed name + raw expression`
(`docs/phase3/v1/PHASE_3_DOC.md:1314`–`:1335`). Its conformance map proves that it preserves kind,
type, name, raw text, and order (`docs/phase3/v1/PHASE_3_DOC.md:690`–`:692`), but §5 does not close:

- declaration kind/type enum variants;
- the ordered collection accessor on `PackConfiguration`;
- source ordinal and source attribution;
- duplicate retention semantics at the published boundary; or
- inclusion of the collection in the configuration fingerprint.

Phase 11 does **not** assume those missing members. Before implementation, request a Phase 3 fix-up
to publish:

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
for Phase 11 diagnostics, and participate in `PackConfiguration` fingerprinting. This is a requested
dependency-interface change and requires Phase 3 fix-up plus fresh verification before Phase 11
code consumes it.

### 5.3 Phase 6 contract consumed

Phase 11 consumes exactly the verified Phase 6 contracts:

- one `CustomUniformBridge.refresh(ResolvedProgramDescriptor, BuiltInExpressionView,
  CustomUniformUploadSink)` (`docs/phase6/v1/PHASE_6_DOC.md:1135`–`:1147`);
- exact-name `Present(ExpressionValue)` / `Absent` lookup and the closed scalar/vector/mat4
  runtime values (`:1148`–`:1162`);
- typed immutable upload submission and closed refresh result (`:1163`–`:1187`);
- built-ins-first execution on every successful activation, with Phase 11 owning expression errors
  and Phase 6 owning GL uploads (`:1201`–`:1207`);
- definition-order submission, finite values, type/location checks, duplicate rejection, and
  accepted-prefix semantics (`:1209`–`:1223`).

Phase 11 does not retain `ResolvedProgramDescriptor`, inspect a linked layout, resolve a location,
install a fourth participant, or invoke a Phase 6 provider.

### 5.4 Requested Phase 6 interface closures — flagged, never assumed

Three required semantics are absent from Phase 6 §5:

1. **Compile-time input schema.** `BuiltInExpressionView` reveals a type only when a value is
   present during refresh. A typed load-time compiler needs an immutable exact-name schema even
   when the first active program has not run. Request `FixedExpressionInputSchema.lookup(name) ->
   Present(FixedExpressionInputType)|Absent`, versioned with the Phase 6 inventory and guaranteed
   to match every runtime `ExpressionValue` variant.
2. **Boolean upload.** Appendix F.6 requires `uniform.bool`, but Phase 6's command algebra has
   `Int1` and no `Bool1` (`docs/phase6/v1/PHASE_6_DOC.md:1166`–`:1178`). Request
   `CustomUploadCommand.Bool1(name,boolean)` and an explicit Phase 6 rule that it validates against
   linked GLSL `bool` and performs the GL integer encoding. Phase 11 will not infer that `Int1`
   means bool.
3. **Normal active-program absence.** A pack-level custom uniform may be declared only in some
   linked programs. The behavioral contract says evaluation/upload occurs only when the uniform is
   defined in the active program
   (`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:618`–`:622`). Phase 6 currently has
   only `Accepted` and `Rejected` (`docs/phase6/v1/PHASE_6_DOC.md:1179`–`:1182`). Request
   `SkippedAbsent`, or an equally explicit contract that active-layout absence is an accepted
   no-GL no-op and not a diagnostic. Type mismatch and duplicate submission remain `Rejected`.

These changes belong to Phase 6 because its sink alone owns active linked declarations and GL type
mapping. They require a Phase 6 fix-up and fresh verification before implementation. The signatures
in §4 describe the needed boundary; they do not claim it already exists.

### 5.5 Composition handoff without a Phase 7 dependency

Later composition must:

1. create one controller and install it into Phase 6 before first activation;
2. compile a plan from the exact accepted Phase 3 configuration and Phase 6 schema;
3. construct the `mod.glue` context provider and random source;
4. activate the complete tuple before the corresponding registry/uniform runtime is used;
5. forward lifecycle resets in the order specified by §4.12;
6. route Phase 11 chat/log diagnostics through Phase 1's diagnostic channels.

These are Phase 11's published requirements, not assumptions about Phase 7 internals.

---

## 6. Failure modes & degradation

| Failure | Detection | Local disposition | Ladder |
|---|---|---|---|
| malformed token/grammar/limit | plan build | disable one declaration; propagate only through variable dependencies; chat warning for affected uniforms | rung 1 analogue at load |
| unknown/colliding name or wrong type/arity | plan build | same as parse failure | rung 1 analogue at load |
| variable cycle | plan build | disable cycle and transitive dependent uniforms; keep unrelated roots | rung 1 analogue at load |
| duplicate smooth id | plan build | first occurrence owns; disable later owning declaration/dependents | rung 1 analogue at load |
| fixed input absent or schema/value mismatch | refresh | disable only each reaching uniform; no neutral value | rung 1 |
| divide/remainder by zero | refresh | discard that definition's smooth overlay, disable only affected uniform | rung 1 |
| NaN/infinity/domain/int-range result | every node/boundary | discard that definition's smooth overlay, disable only affected uniform | rung 1 |
| context provider unavailable/throws | refresh boundary | catch, return `Aborted`, omit remaining customs, keep program | feature-level 2a |
| interpreter invariant/corrupt plan | refresh boundary | return `Aborted`, disable custom-expression feature until reset | feature-level 2a |
| sink rejects name/type/duplicate | Phase 6 sink | count and continue; Phase 6 owns diagnostic/GL decision | rung 2 boundary |
| active program omits custom uniform | Phase 6 sink | normal no-GL skip, no warning | not a failure |
| custom GL upload error | Phase 6 | disable that custom uniform for effective program/generation | rung 2 |
| plan fingerprint/generation mismatch | activation/refresh | refuse tuple or abort refresh; old consistent plan remains or no customs | feature-level 2a |
| close/reset races | thread assertion/state machine | reject illegal caller before mutation; never throw through render callback | feature-level 2a |

There is no whole-program failure path in Phase 11. Chat warnings are coalesced by stable diagnostic
ID, and repeated program switches cannot spam. Detailed logs retain the declaration name, source
attribution, source span, error kind, and dependency path, but not an unbounded expression dump.

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
- at most one evaluation for each reachable variable;
- one evaluation and at most one sink call per enabled uniform;
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
- unknown/reserved/colliding/duplicate identifiers;
- forward variables, diamonds, long chains, self-cycle, multi-node cycle, multiple disjoint cycles,
  invalid-dependency propagation, and unused-variable elimination;
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
  fade, repeated switches in one frame, counter wrap, and reset reasons.

The smooth trace test computes the published equation independently from tabulated inputs; it does
not call the implementation helper to generate its expected values.

### 8.3 Runtime, provider, and isolation tests

- scripted Phase 6 input schemas/views cover each scalar/vector/matrix variant, every Appendix D
  permitted name, all exclusions, unknown, absent, and schema mismatch;
- scripted context providers cover every boolean, biome constant, temperatures/rainfall, provider
  unavailability, and world-schema replacement;
- shared variable is counted once per refresh even when many uniforms reference it;
- uniform submission order is declaration order after dependency-order evaluation;
- one parse failure, one shared-variable runtime failure, one direct uniform failure, and one sink
  rejection leave unrelated uniform commands intact;
- failed definitions rollback smooth effects while random consumption follows the specified
  left-to-right rule; successful smooth definitions commit exactly once;
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
| variable graph, cycles, reachability, memoization | v0.4 | graph/isolation vectors pass |
| complete function registry and lazy semantics | v0.4 | every §3.2 golden vector passes |
| smooth/random state and refresh clock | v0.4 | state-machine trace/reset/cadence vectors pass |
| interpreter backend and Phase 6 bridge | v0.4 | scripted program-switch commands pass |
| diagnostics, controller lifecycle, metrics sink | v0.4 | failure ladder and lifecycle tests pass |
| real-pack Phase 2 extension | v0.4 | matrix declarations receive complete dispositions |
| compiled MethodHandle/bytecode backend candidate | v0.5 / Phase 14, conditional | OQ-22 evidence shows need and semantic differential suite passes |

No post-v0.5 expression-language extension is implied. New syntax or functions require new contract
evidence and a language semantic-version change.

---

## 10. OQ & spike specifications

Phase 11 is assigned no open question (`docs/design/v3/DESIGN.md:2281`). It must nevertheless emit
one OQ-22 ledger entry for Phase 14 because the research row explicitly includes
“expression-engine compilation” among implementation-time spot checks
(`docs/research/v1/RESEARCH.md:1027`). This is a handoff, not a Phase 11 resolution.

### 10.1 OQ-22 expression-backend ledger handoff

**Question.** Does real-pack program-switch expression cost justify a compiled
MethodHandle/bytecode backend over the v0.4 interpreter?

**Procedure.** Run the §4.11 representative workload on at least two supported Java 25 platforms,
record pack/custom definition counts, switches/frame, node evaluations, p50/p95/p99 total
expression nanos/frame, worst refresh nanos, steady allocations, plan-build time, and profile top
nodes. First profile the unmodified interpreter; then allow only local interpreter cleanup. If it
still misses, prototype one compiled backend behind the existing SPI and run the exact semantic
differential/golden suite.

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
| D-P11-9 | treat all D.4 inputs plus fogMode/fogColor as unavailable | verified Phase 6 contract cannot expose the narrower F.6 list; contradiction remains requested upstream |
| D-P11-10 | require an explicit Phase 6 bool command and active-program absence result | prevents undocumented encoding and turns normal shader-layout variation into a no-op rather than an error |
| D-P11-11 | make smooth writes transactional per definition; keep random consumption left-to-right and non-rewinding | prevents partial smooth state while giving the injected random source a precise, implementable order |
| D-P11-12 | automatic smooth keys derive from source/AST identity, never execution order | stable across program switches and backend implementations |

### 11.2 Contradictions, gaps, and rulings

1. **Appendix F.6 versus Appendix D.4 exclusions.** F.6 names five; D.4 adds `blendFunc` and
   `instanceId` under an all-excluded heading. The verified Phase 6 view exposes the conservative
   union. D-P11-9 follows the dependency contract and requests an upstream clarification rather
   than inventing inaccessible inputs.
2. **PD checklist claim versus Pintonium source.** PD §14 says `IrisFunctions` supplies the full
   checklist (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:719`–`:725`), but the load-bearing
   source leaves `round` unregistered and admits vararg TODOs. Appendix F.6 wins; §3 maps the full
   language independently.
3. **stareval historical credit versus verifiable license.** Neither the missing upstream nor the
   current repository evidence establishes a reusable component-specific grant. The binding rule
   says clean-room when unverifiable; D-P11-1 applies it.
4. **Phase 3 conformance prose versus §5 publication.** Phase 3 demonstrates preserved kind/type/
   order but publishes only “typed name + raw expression.” §5.2 requests the closed record/list/
   fingerprint contract and assumes none of it before verification.
5. **Phase 6 command algebra versus `uniform.bool`.** The dependency has no bool command. §5.4
   requests one rather than relying on an implementation convention.
6. **Phase 6 rejection algebra versus program-local declarations.** Normal absence needs a no-op
   outcome. §5.4 requests the distinction from an actual invalid name/type.
7. **v3 commission versus repository adoption records.** The user explicitly commissioned Phase 11
   from v3, but the target/MOVES adoption steps do not yet exist. This document uses v3 coordinates;
   the maintainer work below is required before verification.

### 11.3 Open handoffs

- **To Phase 3:** publish and verify the closed ordered custom-expression declaration contract in
  §5.2.
- **To Phase 6:** publish and verify the input schema, bool command, and absent-program no-op in
  §5.4.
- **To `mod.glue`/later composition:** implement the exact biome/view snapshot and biome catalog;
  create one runtime random source; forward all reset reasons.
- **To Phase 2:** add the scripted-provider, matrix-pack, error, and function/smooth golden runs.
- **To Phase 12:** display immutable Phase 11 load diagnostics; do not parse expressions in GUI.
- **To Phase 14:** run the §10 OQ-22 ledger method before considering a compiled backend.

### 11.4 Requested upstream and maintainer changes

1. Clarify RESEARCH Appendix F.6/D.4 so the exclusion list unambiguously says whether all per-draw
   dynamics (`blendFunc`, `instanceId` included) are excluded. Until then the verified Phase 6 union
   is the only implementable interface.
2. Apply the Phase 3 §5 contract closure requested in §5.2, append resolutions through the normal
   fix-up protocol, and freshly verify the interface.
3. Apply the Phase 6 §5 closures requested in §5.4, append resolutions through the normal fix-up
   protocol, and freshly verify the interface.
4. Create `verification/targets/phase-11.json` by deriving all selectors from
   `docs/design/v3/DESIGN.md`, dry-run it, and update `docs/MOVES.md` to record Phase 11's v3
   adoption before the first paid review. Do not reuse another revision's line offsets.

No change to RESEARCH.md, any DESIGN.md, PD, or a dependency document is performed by this session.

---

## 12. Implementation checklist

- [ ] **v0.4 — dependency gate:** complete and freshly verify the requested Phase 3 and Phase 6
  contracts. **Hook:** their newest reviews are literal PASS with `Interface changed: no` after the
  grants.
- [ ] **v0.4 — verification setup:** add the Phase 11 v3 target profile and adoption record.
  **Hook:** `scripts/verify --target phase-11 --dry-run` validates v3-derived selectors.
- [ ] **v0.4 — language core:** implement the clean-room lexer/parser, spans, limits, typed AST,
  and semantic version. **Test:** §8.1 grammar/span/limit vectors.
- [ ] **v0.4 — operations:** implement exact precedence, all operators, and every §3.2 function—no
  extras. **Test:** one named golden suite for every §3.1/§3.2 row.
- [ ] **v0.4 — numeric safety:** implement declaration-boundary conversions and finite/domain/
  zero-divisor checks. **Test:** type matrix plus every §8.2 error vector.
- [ ] **v0.4 — inputs:** implement the exact fixed input schema, exclusions, biome constants, and
  fourteen booleans. **Test:** exhaustive catalog/presence/schema tests in §8.3.
- [ ] **v0.4 — variables:** implement the dependency graph, deterministic cycle diagnostics,
  reachability, and memo slots. **Test:** §8.1 graph cases and once-per-refresh counters.
- [ ] **v0.4 — stateful functions:** implement deterministic left-to-right random consumption and
  the exact transactional §4.7 smooth state machine/clock/reset behavior. **Test:** independent
  smooth traces, random-consumption, and smooth-rollback tests.
- [ ] **v0.4 — plans:** implement immutable plan build and partial-success diagnostic results.
  **Test:** fingerprint determinism, mixed-validity build, and attribution tests.
- [ ] **v0.4 — backend:** implement the typed-AST interpreter behind `EvaluatorBackend` with
  source-span errors. **Test:** backend semantic/golden suite and parser-fuzz corpus.
- [ ] **v0.4 — Phase 6 integration:** implement the single bridge with declaration-order commands
  and rung-1 isolation. **Test:** scripted sink order/skip/reject/error integration cases.
- [ ] **v0.4 — lifecycle:** implement context provider SPI, controller activation/reset/close, and
  lifecycle assertions. **Test:** every legal/illegal state transition and provider outcome.
- [ ] **v0.4 — diagnostics:** implement stable chat/log routing and rate limiting. **Test:** stable
  IDs, coalescing across switches, sanitation, and dependency-path logging.
- [ ] **v0.4 — headless conformance:** add every §8 golden vector, including independent smooth
  traces and effect rollback. **Hook:** Phase 11 pure-JVM test task.
- [ ] **v0.4 — matrix conformance:** extend Phase 2 scripted/matrix-pack runs without committing
  pack assets. **Hook:** download-at-test-time conformance target reports every declaration.
- [ ] **v0.4 — seam/performance:** prove pure-`:engine` dependencies and zero steady-refresh
  allocation. **Hook:** Phase 1 seam check plus allocation profile in §4.11.
- [ ] **v0.5/Phase 14 — OQ-22:** measure the §4.11 budget and record the §10 handoff while retaining
  the interpreter fallback. **Hook:** ledger contains the prescribed pack counts and p50/p95/p99
  data; compiled differential tests run only if triggered.
